package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntToDoubleFunction;

/**
 * Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md
 * sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be
 * sold callable, its coupon carries the right on a Ho-Lee tree fitted to the
 * curve at the volatility of the city's own rate, and from its first call date
 * the city may call the whole line at 100.
 *
 * What this has to prove:
 *
 * 1. The tree is the curve: on a calm flat curve and on a hiking, inverted
 *    one, every month's zero-coupon price from the tree is the curve's
 *    factor to 1e-9, and a non-call 10- and 30-year bullet prices at the
 *    curve's own present value to 1e-6 per 100, at the floor of the volatility
 *    and above it; with a spread on the curve as well.
 * 2. The option: a callable bond is never worth more than the same bond that
 *    cannot be called, and the same when its first call is past its maturity;
 *    its spread is positive, grows with the volatility, with an earlier first
 *    call and with the term, and a bond at the par coupon plus the spread is
 *    worth 100. The four figures beside the study's are printed, not asserted.
 * 3. The volatility: a history with known monthly changes gives their
 *    standard deviation times the square root of twelve, over the last
 *    VOL_MONTHS only; a quiet one gives RATE_VOL_FLOOR; so does a young one;
 *    a month not counted breaks a change; a played city measures its own.
 * 4. The sale: a played city sells a callable 10-year whose coupon is over a
 *    non-call 10-year's by the spread the tree gives (within a coupon step),
 *    callable from FIRST_CALL_10Y years on; a non-call sale does not reopen it
 *    and a callable one does; a 5-year callable is refused; and a city played
 *    for two years that never asked for the option has no callable piece.
 * 5. The call: before the first call month it refuses and changes nothing; at
 *    it, with the central bank holding part of the line, every holder is paid
 *    its face, the treasury's cash falls by the principal, the gain booked is
 *    the carrying value less the principal, and the money audit closes that
 *    month and the next.
 * 6. Worth calling, caused from both sides: a line at today's par yield is not
 *    worth calling and the same line with rates pushed down is, and the
 *    threshold is WORTH_CALLING of the face on either side of it.
 * 7. The save: a callable line saved and loaded keeps its flag, its first call
 *    month, its coupon and its market value.
 *
 * Each fixture causes its condition rather than finding a city in it. Run with
 * section numbers (CallCheck 1 2 3) to run just those.
 *
 * @author Jerus
 */
public class CallCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void near(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-100s FAIL  %,.9f != %,.9f (allowed %.1e)%n", label, actual, expected, tol);
        } else {
            out.printf("%-100s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /** A month, held to the audit: the audit is returned. */
    static MoneyAudit.Result play(Game g) {
        quietly(g::toggleNextMonth);
        return g.getLastMoneyAudit();
    }

    static boolean closes(MoneyAudit.Result r) {
        return Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved());
    }

    static boolean wanted(String[] args, int section) {
        if (args.length == 0) return true;
        for (String a : args) if (a.equals(String.valueOf(section))) return true;
        return false;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        if (wanted(args, 1)) treeIsTheCurve();
        if (wanted(args, 2)) theOption();
        if (wanted(args, 3)) theVolatility();
        Game city = null;
        if (wanted(args, 4) || wanted(args, 6) || wanted(args, 7)) city = playedCity("callcheck");
        if (wanted(args, 4)) theSale(city);
        if (wanted(args, 5)) theCall();
        if (wanted(args, 6)) worthCalling(city);
        if (wanted(args, 7)) theSave(city);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /* =====================================================================
       THE FIXTURE CURVES: the study's calm city, and a hiking one
       ===================================================================== */

    /** The study's calm city (spec section 3.3): the 1-year zero rate and the zero rate at 2, 5, 10, 20, 30 and 50 years. */
    static final int[] ROW_MONTHS = {12, 24, 60, 120, 240, 360, 600};
    static final double[] ROW_ZERO = {.0312, .0318, .0335, .0362, .0402, .0427, .0462};

    /** The zero rate the study's calm row gives at m months: straight lines between its points, flat at the short rate up to a year. */
    static double calmZero(int m) {
        if (m <= ROW_MONTHS[0]) return ROW_ZERO[0];
        for (int i = 1; i < ROW_MONTHS.length; i++) {
            if (m <= ROW_MONTHS[i]) {
                double k = (m - ROW_MONTHS[i - 1]) / (double) (ROW_MONTHS[i] - ROW_MONTHS[i - 1]);
                return ROW_ZERO[i - 1] + k * (ROW_ZERO[i] - ROW_ZERO[i - 1]);
            }
        }
        return ROW_ZERO[ROW_ZERO.length - 1];
    }

    /** The calm city's curve: a flat dial at the 1-year rate and the shape that takes the zero rate along the study's row. */
    static YieldCurve calm() {
        IntToDoubleFunction shape = m -> m <= 12 ? 0 : calmZero(m) - ROW_ZERO[0];
        return YieldCurve.of(YieldCurve.flatPath(ROW_ZERO[0]), 0, 1.0, shape);
    }

    /** A hiking city's curve: the dial at 7% closing on 3% with a two-year time constant, so the short end is over the long end, and a modest term premium on top. */
    static YieldCurve hiking() {
        double[] path = new double[YieldCurve.HORIZON];
        for (int t = 0; t < path.length; t++) path[t] = .03 + .04 * Math.exp(-t / 24.0);
        IntToDoubleFunction shape = m -> m <= 12 ? 0 : .012 * (1 - Math.exp(-(m - 12) / 120.0));
        return YieldCurve.of(path, 0, 1.0, shape);
    }

    /** A bullet's payments per 100 at a coupon, month 1 first. */
    static double[] bullet(double coupon, int months) {
        return Auction.bulletPer100(coupon, months);
    }

    /* =====================================================================
       1. THE TREE IS THE CURVE
       ===================================================================== */

    static void treeIsTheCurve() {
        out.println("--- 1. the tree is the curve: its zero-coupon prices are the curve's factors, its non-call bonds the curve's present values ---");

        String[] names = {"calm", "hiking"};
        YieldCurve[] curves = {calm(), hiking()};
        double[] sigmas = {CallTree.RATE_VOL_FLOOR, 0.02};
        for (int c = 0; c < curves.length; c++) {
            YieldCurve curve = curves[c];
            for (double sigma : sigmas) {
                String where = names[c] + " curve, volatility " + String.format("%.2f", sigma * 100) + " points";
                CallTree tree = CallTree.fit(curve, sigma, 360);
                double worst = 0;
                for (int m = 1; m <= 360; m++) worst = Math.max(worst, Math.abs(tree.zeroCouponPrice(m) - curve.factor(m)));
                near("the " + where + ": the tree's price of a unit paid in every month 1..360 is the curve's factor (the worst)", worst, 0, 1e-9);
                near("...the price of a unit in month 120", tree.zeroCouponPrice(120), curve.factor(120), 1e-9);
                near("...and in month 360", tree.zeroCouponPrice(360), curve.factor(360), 1e-9);

                for (int years : new int[] {10, 30}) {
                    int n = years * 12;
                    for (double coupon : new double[] {curve.parYield(n), .06}) {
                        double[] flows = bullet(coupon, n);
                        double tree100 = tree.price(flows, -1, 100);
                        near("...a non-call " + years + "-year at " + String.format("%.3f%%", coupon * 100) + " prices at the curve's present value",
                                tree100, curve.pv(flows), 1e-6);
                    }
                    near("...a non-call " + years + "-year at the par yield prices at 100", tree.price(bullet(curve.parYield(n), n), -1, 100), 100, 1e-6);
                }
            }
            // The static form fits a tree for the flows it is handed.
            double[] flows = bullet(curve.parYield(120), 120);
            near("the " + names[c] + " curve: the static price() of a non-call 10-year is the curve's present value",
                    CallTree.price(curve, CallTree.RATE_VOL_FLOOR, flows, -1, 100), curve.pv(flows), 1e-6);
            // ...and with a tail over fair on the curve, as a sale is priced.
            double tail = .004;
            near("the " + names[c] + " curve: with a spread on every zero rate the tree is the spread curve (YieldCurve.pvAtSpread())",
                    CallTree.price(curve, tail, CallTree.RATE_VOL_FLOOR, flows, -1, 100), curve.pvAtSpread(flows, tail), 1e-6);
            near("...and at no spread the factor is the factor to the bit", curve.factorAtSpread(60, 0), curve.factor(60), 0);
        }
    }

    /* =====================================================================
       2. THE OPTION
       ===================================================================== */

    static void theOption() {
        out.println("\n--- 2. the option: never worth more than the bond, worth nothing when it cannot be used, dearer as rates move more, as it comes earlier, as the term grows ---");

        YieldCurve[] curves = {calm(), hiking()};
        String[] names = {"calm", "hiking"};
        double[] sigmas = {CallTree.RATE_VOL_FLOOR, 0.01, 0.02};
        for (int c = 0; c < curves.length; c++) {
            YieldCurve curve = curves[c];
            String name = names[c];
            for (int[] bond : new int[][] {{10, CallTree.FIRST_CALL_10Y}, {30, CallTree.FIRST_CALL_LONG}}) {
                int years = bond[0], n = years * 12, first = bond[1] * 12;
                double par = curve.parYield(n);
                double[] flows = bullet(par, n);
                double plain = curve.pv(flows);
                boolean never = true, grows = true;
                double last = 0, lastSpread = 0;
                for (double sigma : sigmas) {
                    CallTree tree = CallTree.fit(curve, sigma, n);
                    double callable = tree.price(flows, first, 100);
                    never &= callable <= plain + 1e-9;
                    grows &= callable <= last || last == 0;
                    last = callable;
                    double spread = tree.optionSpread(curve, n, first);
                    grows &= spread > lastSpread;
                    lastSpread = spread;
                    near("the " + name + " " + years + "-year, call from year " + bond[1] + ", volatility " + String.format("%.2f", sigma * 100)
                            + ": a bond at par plus the spread is worth 100 callable", tree.price(bullet(par + spread, n), first, 100), 100, 1e-6);
                    check("...and the spread is positive (" + String.format("%.1f", spread * 10_000) + " bp)", spread > 0);
                }
                check("the " + name + " " + years + "-year: callable is never worth more than the same bond that cannot be called", never);
                check("...and the callable price falls and the spread grows as the volatility does", grows);
                CallTree tree = CallTree.fit(curve, CallTree.RATE_VOL_FLOOR, n);
                near("...first call at the maturity: the same price as no call at all", tree.price(flows, n, 100), tree.price(flows, -1, 100), 1e-12);
                near("...first call past the maturity: the same", tree.price(flows, n + 50, 100), tree.price(flows, -1, 100), 1e-12);
                check("...and the option spread of a call that cannot be used is nothing",
                        tree.optionSpread(curve, n, n + 5) == 0 && tree.optionSpread(curve, n, -1) == 0);
                double early = tree.optionSpread(curve, n, 24), mid = tree.optionSpread(curve, n, first), late = tree.optionSpread(curve, n, n - 24);
                check("...the sooner it may be called the dearer the option (" + String.format("%.0f > %.0f > %.0f bp", early * 1e4, mid * 1e4, late * 1e4) + ")",
                        early > mid && mid > late && late > 0);
                double now = tree.price(flows, 0, 100);
                check("...callable now, it is worth no more than the call price (" + String.format("%.4f", now) + ")", now <= 100 + 1e-9);
            }
            double ten = CallTree.optionSpread(curve, CallTree.RATE_VOL_FLOOR, 120, CallTree.FIRST_CALL_10Y * 12);
            double thirty = CallTree.optionSpread(curve, CallTree.RATE_VOL_FLOOR, 360, CallTree.FIRST_CALL_LONG * 12);
            check("the " + name + " city: the 30-year callable from year 10 pays more for its option than the 10-year from year 5 ("
                    + String.format("%.1f > %.1f bp", thirty * 1e4, ten * 1e4) + ")", thirty > ten);
        }

        // The four figures against the study's (spec section 2.6), information: the harness's calm curve is the study's row joined by straight lines.
        YieldCurve calm = calm();
        double[] figures = new double[4];
        int k = 0;
        out.println("   against the study's calm city (spec 2.6), non-call coupon and the option, in bp:");
        for (double sigma : new double[] {CallTree.RATE_VOL_FLOOR, 0.01}) {
            for (int[] bond : new int[][] {{10, CallTree.FIRST_CALL_10Y}, {30, CallTree.FIRST_CALL_LONG}}) {
                int n = bond[0] * 12;
                figures[k++] = CallTree.optionSpread(calm, sigma, n, bond[1] * 12);
                out.printf("   %2d-year from year %2d, volatility %.2f: non-call %.3f%%, option +%.1f bp, callable %.3f%%%n",
                        bond[0], bond[1], sigma * 100, calm.parYield(n) * 100, figures[k - 1] * 1e4, (calm.parYield(n) + figures[k - 1]) * 100);
            }
        }
        out.printf("   the study: 10y +34, 30y +58 at 0.75; 10y +49, 30y +82 at 1.00; here +%.0f, +%.0f, +%.0f, +%.0f%n",
                figures[0] * 1e4, figures[1] * 1e4, figures[2] * 1e4, figures[3] * 1e4);
    }

    /* =====================================================================
       3. THE VOLATILITY
       ===================================================================== */

    /** A rate history whose monthly changes are +step, -step, +step, ... for this many months, from a start. */
    static double[] zigzag(double start, double step, int changes) {
        double[] r = new double[changes + 1];
        r[0] = start;
        for (int i = 1; i <= changes; i++) r[i] = r[i - 1] + (i % 2 == 1 ? step : -step);
        return r;
    }

    static void theVolatility() {
        out.println("\n--- 3. the volatility of the city's own rate: the standard deviation of its monthly changes, times root 12, never under the floor ---");

        // Sixty changes of +-0.40 points: mean zero, the sample standard deviation is 0.004 x sqrt(60/59).
        double[] history = zigzag(.03, .004, CallTree.VOL_MONTHS);
        double expected = .004 * Math.sqrt(60.0 / 59.0) * Math.sqrt(12);
        check("fixture: the expected figure is over the floor (" + String.format("%.3f", expected * 100) + " points)", expected > CallTree.RATE_VOL_FLOOR);
        near("sixty changes of +-0.40 points give their standard deviation times root 12", CallTree.volatility(history), expected, 1e-12);

        // The same sixty after a wild history before them: only the last VOL_MONTHS count.
        double[] wild = new double[100 + history.length];
        for (int i = 0; i < 100; i++) wild[i] = .03 + (i % 2 == 0 ? .05 : -.05);
        System.arraycopy(history, 0, wild, 100, history.length);
        wild[99] = wild[100] = history[0];
        near("a wilder history before them does not count: only the last " + CallTree.VOL_MONTHS + " changes are measured",
                CallTree.volatility(wild), expected, 1e-12);

        // A level that never moves, and one that moves less than the floor.
        double[] flat = new double[61];
        Arrays.fill(flat, .03);
        near("a rate that never moved gives the floor", CallTree.volatility(flat), CallTree.RATE_VOL_FLOOR, 0);
        double[] calmer = zigzag(.03, .0005, 60);
        check("fixture: a rate that moves a twentieth of a point is measured under the floor",
                .0005 * Math.sqrt(60.0 / 59.0) * Math.sqrt(12) < CallTree.RATE_VOL_FLOOR);
        near("...and is priced at the floor", CallTree.volatility(calmer), CallTree.RATE_VOL_FLOOR, 0);

        // Fewer than a year of it: the floor, however wild.
        double[] young = zigzag(.03, .02, CallTree.VOL_MIN_CHANGES - 1);
        near("under thirteen months of the rate, the floor stands however wild it was", CallTree.volatility(young), CallTree.RATE_VOL_FLOOR, 0);
        double[] thirteen = zigzag(.03, .02, CallTree.VOL_MIN_CHANGES);
        check("...and thirteen months are enough", CallTree.volatility(thirteen) > CallTree.RATE_VOL_FLOOR);
        near("no history at all is the floor", CallTree.volatility(new double[0]), CallTree.RATE_VOL_FLOOR, 0);

        // Months that were not counted (NaN, as History pads the front) break a change and are not one.
        double[] padded = new double[40 + history.length];
        Arrays.fill(padded, 0, 40, Double.NaN);
        System.arraycopy(history, 0, padded, 40, history.length);
        near("months not counted (NaN) in front of the history are not changes", CallTree.volatility(padded), expected, 1e-12);

        // The cities: a bare manager prices at the floor; a young city too; a played city measures its own.
        near("a manager nobody has handed a volatility prices at the floor", new DebtManager().rateVolatility(), CallTree.RATE_VOL_FLOOR, 0);
        Game young2 = new Game(GameFiles.scratch("callcheck-young"));
        near("a city with no history yet is priced at the floor", young2.rateVolatility(), CallTree.RATE_VOL_FLOOR, 0);
        near("...and its manager is handed the same", young2.getDebtManager().rateVolatility(), young2.rateVolatility(), 0);
    }

    /* =====================================================================
       A PLAYED CITY (BuybackCheck's: the households, the bank and the world have the money to bid)
       ===================================================================== */

    /**
     * FOUR DEPOTS STANDING SINCE PT1-M4, beside the four it orders. The works department that built 400 points a month for a
     * city nobody staffed is retired, and the founding yard's 160 points raised this city's houses so slowly that it had 256
     * people at month 62 and its price index no year of prices until month 86: section 4 sold the callable at month 85, on a
     * curve whose rule read the target for want of an inflation, and the first inflation it ever read (15%) landed in the
     * two months before the line was valued - the curve rose 150 basis points under it and the line was worth 89.0 per 100.
     * Builders enough to raise the city (addStack(t, n, true), as foundingDepot() once stood its one) give it its year of
     * prices by month 50 and a calm one at the sale (inflation -0.7% and -0.6% either side of it): the line is worth about
     * its face. Two depots were not enough: the price index swung from +20% to -9% a year across the sale (105.8 per 100).
     */
    static Game playedCity(String label) {
        GameFiles files = GameFiles.scratch(label);
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "Construction Depot"), 4, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
            for (int i = 0; i < 36 && city.getMigration().getLastDecliningShare() > 0; i++) city.simulateMonths(1);
        });
        return city;
    }

    /** The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. */
    static void worldWantsPaper(Game city) {
        CapitalFlows hot = city.getCapitalFlows();
        double[] cf = hot.toSaveArray();
        cf[0] = hot.getStock() / 10;
        hot.restore(cf);
        city.getBank().setForeignDeposits(hot.getStock());
    }

    /** Every callable bond on the city's books. */
    static List<LongTermBond> callables(Game g) {
        List<LongTermBond> found = new ArrayList<>();
        for (Debt d : g.getDebtManager().getDebt()) if (d instanceof LongTermBond b && b.isCallable()) found.add(b);
        return found;
    }

    /** Every piece the city's own bond lines hold (callable or not). */
    static int bonds(Game g) {
        int n = 0;
        for (Debt d : g.getDebtManager().getDebt()) if (!d.isForeign() && d instanceof LongTermBond) n++;
        return n;
    }

    /* =====================================================================
       4. THE SALE
       ===================================================================== */

    /**
     * A sale of this many times what the households could take, and of no more than that many times a third of a year's tax revenue
     * (EM1, 2026-10-29). Sized by the households alone it grew with their savings: at month 85 of the played city they could take 152,000
     * and now, holding the money the companies' tills did, 417,000, so the sales of this section put 4.1 million of paper on the books
     * where they put 1.9, took the credit spread to 5.0% where it was 2.5%, and left the auction's bidders (the bank's room above all)
     * nothing for a callable 20-year: a sale under the minimum issue, quoted empty. What the section asserts is that the option can be
     * sold, not how much paper the households' wealth would let a fixture pile up.
     */
    static double saleOf(Game city, double spares) {
        return Math.max(100, Math.min(city.getHouseholdBalance().spareForPaper() * spares,
                spares * FloatLinkCheck.SALE_OF_REVENUE * 12 * city.getDebtManager().getMonthlyTaxRevenue()));
    }

    static void theSale(Game city) {
        out.println("\n--- 4. the sale: a callable 10-year costs its option in the coupon, callable from year 5; nothing sells it unasked ---");

        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();

        // Nothing asks for the option: two more years of the city, the rollover and the auto-builder at their work, and a sale or two by the old signatures.
        check("fixture: the city holds no callable piece before it starts", callables(city).isEmpty());
        double plainAsk = saleOf(city, 1);
        quietly(() -> {
            city.handleLongBondLogic(plainAsk, 10, 100);
            city.handleLongBondLogic(plainAsk, 20, 100, Auction.Cap.FILL);
            city.handleLongBondForCash(plainAsk / 2, 30, 100);
        });
        check("fixture: ...nor after sales by the signatures that do not ask for the option (a bond, a bond at a cap, a bond for the cash)",
                callables(city).isEmpty() && bonds(city) >= 1);
        for (int i = 0; i < 24; i++) play(city);
        check("a city played two years that never asked for one has no callable piece (" + bonds(city) + " bond lines on its books)",
                callables(city).isEmpty());

        worldWantsPaper(city);
        city.setCashForTest(Founding.WEALTHY_CASH);
        check("fixture: the city has a year of prices before it sells, so the curve it sells on already reads an inflation",
                city.getPriceIndex().hasRate());
        double ask = saleOf(city, 3);
        double face = Math.ceil(ask / 100) * 100;

        DebtQuote plain = city.quoteLongBond(ask, 10, 100, Auction.Cap.DEFAULT, false);
        DebtQuote call = city.quoteLongBond(ask, 10, 100, Auction.Cap.DEFAULT, true);
        check("fixture: both sales are quoted, for the same face", !plain.isEmpty() && !call.isEmpty()
                && plain.faceValue() == call.faceValue() && !plain.isShort() && !call.isShort());
        check("the quote says it is callable, and the plain one not", call.callable() && !plain.callable() && plain.firstCallMonth() == -1);
        check("...and when: " + CallTree.FIRST_CALL_10Y + " years on", call.firstCallMonth() == city.getMonth() + CallTree.FIRST_CALL_10Y * 12);
        double vol = city.rateVolatility();
        double spread = CallTree.optionSpread(ledger.quoteCurve(face), vol, 120, CallTree.FIRST_CALL_10Y * 12);
        out.printf("   the 10-year: fair %.3f%% non-call, %.3f%% callable (the option %.1f bp at volatility %.2f points); coupon %.3f%% against %.3f%%%n",
                plain.auction().ask().fair() * 100, call.auction().ask().fair() * 100, spread * 1e4, vol * 100,
                call.coupon() * 100, plain.coupon() * 100);
        near("the callable ask's fair yield is the non-call fair plus the tree's option spread",
                call.auction().ask().fair() - plain.auction().ask().fair(), spread, 1e-9);
        check("the spread is positive", spread > 0);
        double gap = call.coupon() - plain.coupon();
        check("the callable coupon is over the non-call one by the spread, to within a coupon step (" + String.format("%.1f bp against %.1f", gap * 1e4, spread * 1e4) + ")",
                gap >= spread - Auction.COUPON_STEP - 1e-9 && gap <= spread + Auction.COUPON_STEP + 1e-9);

        // Sold.
        int bondsBefore = bonds(city);
        quietly(() -> city.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true));
        List<LongTermBond> made = callables(city);
        check("a callable sale puts one callable piece on the books, and only one", made.size() == 1 && bonds(city) == bondsBefore + 1);
        LongTermBond line = made.isEmpty() ? null : made.get(0);
        if (line == null) return;
        check("...it is a callable line, with the quote's first call month and coupon",
                line.isCallable() && line.getFirstCallMonth() == call.firstCallMonth() && Math.abs(line.couponRate() - call.coupon()) < 1e-12);
        check("...the first call is " + CallTree.FIRST_CALL_10Y + " years after the sale, and the term ten", line.callTermMonths() == CallTree.FIRST_CALL_10Y * 12 && line.getDuration() == 120);
        check("...and it is not callable yet", !line.isCallableNow(city.getMonth()) && !city.canCall(line) && city.callLine(line) == 0);
        check("...the piece is on the books, unsettled", ledger.getDebt().contains(line) && line.unsettledFace() > 0);
        check("...the schedule's first call matches the calendar's: " + line.monthsToFirstCall() + " months to go",
                line.monthsToFirstCall() == line.getFirstCallMonth() - city.getMonth());
        check("the audit closes the month of the sale and the next", closes(play(city)) && closes(play(city)));
        check("...and its first call is now a month nearer, by the schedule and by the calendar",
                line.monthsToFirstCall() == line.getFirstCallMonth() - city.getMonth());

        // Valued on the tree.
        YieldCurve curve = ledger.curve();
        double[] flows = line.remainingCashFlows();
        double principal = line.getOustandingPrincipal();
        double value = ledger.marketValue(line);
        near("the callable piece is valued on the tree at the city's volatility, from its first call month",
                value, CallTree.price(curve, city.rateVolatility(), flows, line.monthsToFirstCall(), principal), 1e-6 * principal);
        check("...never above the same payments that cannot be called (" + String.format("%.3f against %.3f per 100", value / principal * 100, curve.pv(flows) / principal * 100) + ")",
                value <= curve.pv(flows) + 1e-9 * principal);
        check("...and a callable line sold in the market is worth about its face (" + String.format("%.2f per 100", value / principal * 100) + ")",
                value / principal > .9 && value / principal < 1.02);

        // A non-call sale at the same term does not reopen it; a callable one does.
        city.setCashForTest(Founding.WEALTHY_CASH);
        DebtQuote nonCall = city.quoteLongBond(ask, 10, 100, Auction.Cap.DEFAULT, false);
        check("a non-call sale at the same term the next month opens a line of its own: it does not reopen the callable one",
                !nonCall.isEmpty() && nonCall.auction().ask().isNewLine() && ledger.benchmarkLine(10) != line && !(ledger.benchmarkLine(10) instanceof LongTermBond b && b.isCallable()));
        double faceBefore = line.getFaceValue();
        int before = bonds(city);
        quietly(() -> city.handleLongBondLogic(ask, 10, 100));
        check("...and books a new piece, the callable line untouched", bonds(city) == before + 1 && callables(city).size() == 1 && line.getFaceValue() == faceBefore);

        DebtQuote again = city.quoteLongBond(ask, 10, 100, Auction.Cap.DEFAULT, true);
        check("a callable sale at the same term reopens the callable line, at its coupon and its first call date",
                !again.isEmpty() && !again.auction().ask().isNewLine() && ledger.benchmarkLine(10, true) == line
                        && Math.abs(again.auction().ask().lineCoupon() - line.couponRate()) < 1e-12 && again.firstCallMonth() == line.getFirstCallMonth());
        int beforeAgain = bonds(city);
        quietly(() -> city.handleLongBondLogic(ask, 10, 100, Auction.Cap.DEFAULT, true));
        check("...the piece grows by the face sold, and no piece is added",
                bonds(city) == beforeAgain && callables(city).size() == 1 && Math.abs(line.getFaceValue() - faceBefore - again.faceValue()) < 1e-6);
        check("...still callable, from the same month", line.isCallable() && line.getFirstCallMonth() == call.firstCallMonth());
        check("a fixture line with another first call date is not the same line",
                differentCallDateIsAnotherLine(city));

        // Refused.
        int bondsNow = bonds(city);
        DebtQuote five = city.quoteLongBond(ask, 5, 100, Auction.Cap.DEFAULT, true);
        String told = city.handleLongBondLogic(ask, 5, 100, Auction.Cap.DEFAULT, true);
        check("a callable 5-year is refused: quoted nothing, the treasury told why, nothing booked",
                five.isEmpty() && told.equals(CallTree.REFUSAL) && bonds(city) == bondsNow);
        check("...and so is a callable 2-year and a callable 7-year",
                city.quoteLongBond(ask, 2, 100, Auction.Cap.DEFAULT, true).isEmpty() && city.quoteLongBond(ask, 7, 100, Auction.Cap.DEFAULT, true).isEmpty());
        for (int years : new int[] {20, 30, 50}) {
            DebtQuote q = city.quoteLongBond(ask, years, 100, Auction.Cap.DEFAULT, true);
            check("a callable " + years + "-year may be sold, first callable " + CallTree.FIRST_CALL_LONG + " years on",
                    !q.isEmpty() && q.callable() && q.firstCallMonth() == city.getMonth() + CallTree.FIRST_CALL_LONG * 12);
        }
        check("the audit still closes", closes(play(city)));
    }

    /** A callable line built with another first call date is not the line a standard callable sale reopens. */
    static boolean differentCallDateIsAnotherLine(Game city) {
        DebtManager ledger = city.getDebtManager();
        Debt odd = ledger.addCallableBond(100, 240, city.getMonth(), .05, city.getMonth() + 12);
        try {
            Debt found = ledger.benchmarkLine(20, true);
            return found != odd && ledger.benchmarkLine(20) != odd;
        } finally {
            ledger.retire(odd);
        }
    }

    /* =====================================================================
       5. THE CALL
       ===================================================================== */

    static void theCall() {
        out.println("\n--- 5. the call: refused before the first call month; at it, every holder paid its face, the central bank's too ---");

        Game city = playedCity("callcheck-cb");
        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();
        CentralBank cb = city.getCentralBank();
        Bank bank = city.getBank();
        CapitalFlows hot = city.getCapitalFlows();

        worldWantsPaper(city);
        city.setCashForTest(Founding.WEALTHY_CASH);
        quietly(() -> city.handleLongBondLogic(Math.max(100, hb.spareForPaper() * 3), 10, 100, Auction.Cap.DEFAULT, true));
        List<LongTermBond> made = callables(city);
        check("fixture: the treasury sold a callable ten-year line", made.size() == 1);
        if (made.isEmpty()) return;
        LongTermBond bond = made.get(0);
        check("fixture: ...unsettled until the month turns, and a call of it is refused", bond.unsettledFace() > 0 && !city.canCall(bond));
        play(city);
        cb.setTargetShare(.3);
        int guard = 0;
        while (bond.centralBankPrincipal() <= 0 && guard++ < 24) play(city);
        check("fixture: the central bank, on its dial, holds part of the line", bond.centralBankPrincipal() > 0 && guard < 24);
        check("fixture: ...and the bank, the households, the businesses or the world hold the rest",
                bond.bankPrincipal() + bond.householdPrincipal() + bond.firmPrincipal() + bond.worldPrincipal() > 0);
        city.setCashForTest(Founding.WEALTHY_CASH);

        // Before the first call month: refused, nothing changes.
        check("before its first call month it cannot be called (" + (bond.getFirstCallMonth() - city.getMonth()) + " months to go)", !city.canCall(bond));
        double[] before = snapshot(city, bond);
        double returned = city.callLine(bond);
        check("...callLine() refuses: it returns nothing and changes nothing", returned == 0 && Arrays.equals(before, snapshot(city, bond)) && ledger.getDebt().contains(bond));
        bond.moveFirstCallForTest(city.getMonth() + 1);
        check("a month before the date it still refuses", !city.canCall(bond) && city.callLine(bond) == 0 && Arrays.equals(before, snapshot(city, bond)));

        // At it.
        bond.moveFirstCallForTest(city.getMonth());
        check("at its first call month it can be called, and it costs its principal, 100 per 100",
                city.canCall(bond) && bond.monthsToFirstCall() == 0 && city.callCost(bond) == bond.getOustandingPrincipal());
        double principal = bond.getOustandingPrincipal();
        check("fixture: ...and the treasury could pay for it", principal < city.getCash());
        double value = ledger.marketValue(bond);
        check("a line that can be called now is worth no more than its principal (" + String.format("%.3f per 100", value / principal * 100) + ")", value <= principal + 1e-6);

        // Short of cash: refused.
        double cash = city.getCash();
        city.setCashForTest(principal / 2);
        double[] short1 = snapshot(city, bond);
        check("a treasury short of the principal is refused, and nothing changes", city.callLine(bond) == 0 && Arrays.equals(short1, snapshot(city, bond)));
        city.setCashForTest(cash);

        double bankFace = bond.bankPrincipal(), hhFace = bond.householdPrincipal(), firmFace = bond.firmPrincipal();
        double worldFace = bond.worldPrincipal(), cbFace = bond.centralBankPrincipal();
        double carrying = bond.getCarryingValue();
        double cash0 = city.getCash(), bankCash0 = bank.getCash(), bankBook0 = bank.getCityBook();
        double savings0 = hb.totalSavings(), hotPaper0 = hot.getPaper(), carried0 = city.getBuybackUnsettled();
        double cbHeld0 = cb.getPaperHeld(), redemption0 = cb.getRedemptionDue();
        double tills0 = tills(city), firmPaper0 = city.getOutwardInvestment().totalPaper();
        out.printf("   the line: D$%,.0fk - the bank %,.0fk, the households %,.0fk, the businesses %,.0fk, the world %,.0fk, the central bank %,.0fk; carried at %,.2fk%n",
                principal, bankFace, hhFace, firmFace, worldFace, cbFace, carrying);

        double paid = city.callLine(bond);
        near("the call pays the principal", paid, principal, 1e-9);
        check("the line leaves the books", !ledger.getDebt().contains(bond));
        near("the treasury's cash fell by the principal", cash0 - city.getCash(), principal, 1e-6);
        near("the bank was paid its face", bank.getCash() - bankCash0, bankFace, 1e-6);
        near("...its book of the city's paper fell by that face", bankBook0 - bank.getCityBook(), bankFace, 1e-6);
        near("the households' savings rose by their face", hb.totalSavings() - savings0, hhFace, 1e-6);
        near("the world's book of the paper fell by its face", hotPaper0 - hot.getPaper(), worldFace, 1e-6);
        near("the businesses' tills rose by their face, at once", tills(city) - tills0, firmFace, 1e-6);
        near("...and their paper fell by it", firmPaper0 - city.getOutwardInvestment().totalPaper(), firmFace, 1e-6);
        near("the central bank's book of paper fell by its face", cbHeld0 - cb.getPaperHeld(), cbFace, 1e-6);
        near("...and it is paid its face: the redemption due to it rose by it", cb.getRedemptionDue() - redemption0, cbFace, 1e-6);
        near("what the households, the world and the central bank were paid is carried to the next month's window",
                city.getBuybackUnsettled() - carried0, hhFace + worldFace + cbFace, 1e-6);
        check("the two books of the households' paper agree, and of the world's, and of the central bank's",
                Math.abs(hb.totalPaper() - ledger.householdPrincipal()) <= 1e-6 && Math.abs(hot.getPaper() - ledger.worldPrincipal()) <= 1e-6
                        && Math.abs(cb.getPaperHeld() - ledger.centralBankPrincipal()) <= 1e-6);
        check("the faces the holders were paid are the principal", Math.abs(bankFace + hhFace + firmFace + worldFace + cbFace - principal) <= 1e-6);

        MoneyAudit.Result m1 = play(city);
        near("the gain on debt retired the month booked is the carrying value less the principal",
                city.getEconomyManager().getNationalAccounts().getRetiredGain(), carrying - principal, 1e-6);
        near("the month's journal names the principal paid, not the gain", TreasuryCheck.amount(city.getTreasuryJournal(), "Called a bond"), -principal, 1e-9);
        check("the audit closes the month the payments are declared in", closes(m1));
        check("...and the month after", closes(play(city)));
        near("the central bank holds what the paper says it does", cb.getPaperHeld(), ledger.centralBankPrincipal(), 1e-6);
        check("a line called is gone: it cannot be called again", !city.canCall(bond) && city.callLine(bond) == 0);
    }

    /** Everything a refused call must leave as it was. */
    static double[] snapshot(Game g, Debt d) {
        return new double[] {g.getCash(), d.getOustandingPrincipal(), d.getFaceValue(), d.getCarryingValue(),
                d.bankPrincipal(), d.householdPrincipal(), d.firmPrincipal(), d.worldPrincipal(), d.centralBankPrincipal(),
                g.getBank().getCash(), g.getBank().getCityBook(), g.getHouseholdBalance().totalSavings(),
                g.getCapitalFlows().getPaper(), g.getCentralBank().getPaperHeld(), g.getCentralBank().getRedemptionDue(),
                g.getBuybackUnsettled(), tills(g), g.getDebtManager().getDebt().size()};
    }

    /** Every sector's till, added up. */
    static double tills(Game g) {
        double sum = 0;
        for (String key : Sectors.KEYS) sum += g.getEconomyManager().getSectorCash(key);
        return sum;
    }

    /* =====================================================================
       6. WORTH CALLING
       ===================================================================== */

    static void worthCalling(Game city) {
        out.println("\n--- 6. worth calling: the present value saved passes " + String.format("%.0f%%", CallTree.WORTH_CALLING * 100) + " of the face ---");

        // The arithmetic, on the calm curve: a coupon chosen so that the saving is a hair under the threshold, and a hair over it.
        YieldCurve curve = calm();
        int months = 120;
        double principal = 1_000, fee = 5;
        double p0 = curve.pv(bullet(0, months)), p1 = curve.pv(bullet(1, months));
        for (double share : new double[] {CallTree.WORTH_CALLING - .001, CallTree.WORTH_CALLING + .001}) {
            // pv(c) per 100 is linear in the coupon: solve for the coupon that saves `share` of the face net of the fee.
            double target = 100 + fee / principal * 100 + share * 100;
            double coupon = (target - p0) / (p1 - p0);
            LongTermBond line = LongTermBond.callable(principal, months, 0, coupon, 0);
            double saving = DebtManager.callSavingOn(curve, line, fee);
            near("a line whose coupon saves " + String.format("%.1f%%", share * 100) + " of its face net of the fee: the saving is that",
                    saving / principal, share, 1e-9);
            boolean worth = DebtManager.worthCalling(saving, principal);
            check("...is " + (share > CallTree.WORTH_CALLING ? "" : "not ") + "worth calling (the threshold is " + String.format("%.0f%%", CallTree.WORTH_CALLING * 100) + " of the face)",
                    worth == (share > CallTree.WORTH_CALLING) && worth == (saving > CallTree.WORTH_CALLING * principal));
        }
        LongTermBond par = LongTermBond.callable(principal, months, 0, curve.parYield(months), 0);
        near("a line at the par yield of the day saves nothing but the fee it would cost to sell again",
                DebtManager.callSavingOn(curve, par, fee), -fee, 1e-9);

        // In a played city: a line at today's par yield for the months it has left, and then the rates pushed down.
        DebtManager ledger = city.getDebtManager();
        reprice(city);
        int left = 60;
        double face = 1_000;
        double coupon = ledger.curve().parYield(left);
        Debt line = ledger.addCallableBond(face, left, city.getMonth(), coupon, city.getMonth());
        double policy = ledger.getPolicyRate();
        boolean wasStruck = ledger.isStruck();
        double struckInflation = ledger.getStruckInflation(), struckExpected = ledger.getStruckExpected();
        try {
            check("fixture: a callable line with five years left, at today's par yield, callable now", city.canCall(line));
            double saving0 = city.callSaving(line);
            // What a new sale costs: the auction's fixed fee in today's money (Game.issuanceFee(), worked out here from its parts).
            double unit = city.getDenomination().getUnit();
            double fee0 = Game.FIXED_ISSUE_COST * city.getExpectations().getExpectedLevel() / (unit > 0 ? unit : 1);
            double worth0 = ledger.curve().pv(line.remainingCashFlows());
            out.printf("   rates as they are: the line is worth D$%,.3fk against a principal of D$%,.0fk, a new sale costs D$%,.3fk, so it saves D$%,.3fk - %.2f%% of its face%n",
                    worth0, face, fee0, saving0, saving0 / face * 100);
            check("fixture: ...the line is worth its face to within a hundredth (the booking moved the curve a little) and a new sale costs a positive fee",
                    Math.abs(worth0 - face) < .01 * face && fee0 > 0 && fee0 < CallTree.WORTH_CALLING * face);
            near("...the saving is the line's payments at the curve less the principal less the fee", saving0, worth0 - face - fee0, 1e-9);
            check("rates unchanged: it saves less than the threshold and is not worth calling",
                    saving0 < CallTree.WORTH_CALLING * face && !city.worthCalling(line));

            // Pushed down: the dial to nothing and the market's expectations of inflation to nothing, so the whole curve falls.
            ledger.setPolicyRate(DebtManager.MIN_POLICY_RATE);
            ledger.strikeExpectations(0, 0);
            double saving1 = city.callSaving(line);
            out.printf("   rates pushed down: the 5-year yields %.3f%% against the line's %.3f%%; it saves D$%,.3fk - %.2f%% of its face%n",
                    ledger.curve().parYield(left) * 100, coupon * 100, saving1, saving1 / face * 100);
            check("fixture: the curve fell under the line's coupon", ledger.curve().parYield(left) < coupon - .005);
            check("rates pushed down: the saving passes " + String.format("%.0f%%", CallTree.WORTH_CALLING * 100) + " of the face and it is worth calling",
                    saving1 > CallTree.WORTH_CALLING * face && city.worthCalling(line));
            near("...the saving is the line's payments at the new curve less the principal less the same fee",
                    saving1, ledger.curve().pv(line.remainingCashFlows()) - face - fee0, 1e-9);
            check("...worth calling is the saving over the threshold, either side", city.worthCalling(line) == (saving1 > CallTree.WORTH_CALLING * face)
                    && city.worthCalling(line) != (saving0 > CallTree.WORTH_CALLING * face));
            Debt plain = ledger.addLongTermBond(face, left, city.getMonth(), coupon);
            check("a bond that cannot be called is never worth calling, whatever it saves (it would save "
                    + String.format("%.1f%%", city.callSaving(plain) / face * 100) + ")", !city.worthCalling(plain));
            ledger.retire(plain);
        } finally {
            ledger.setPolicyRate(policy);
            if (wasStruck) ledger.strikeExpectations(struckInflation, struckExpected);
            ledger.retire(line);
            reprice(city);
        }
    }

    /** The market priced as the city stands (Game keeps that private): a quote of a bill books nothing and prices it first. */
    static void reprice(Game g) {
        g.quoteLongBond(100, 10, 100);
    }

    /* =====================================================================
       7. THE SAVE
       ===================================================================== */

    static void theSave(Game city) throws Exception {
        out.println("\n--- 7. the save: a callable line keeps its flag, its first call month, its coupon and its market value ---");

        DebtManager ledger = city.getDebtManager();
        city.setCashForTest(Founding.WEALTHY_CASH);
        if (callables(city).isEmpty()) {
            quietly(() -> city.handleLongBondLogic(Math.max(100, city.getHouseholdBalance().spareForPaper() * 3), 10, 100, Auction.Cap.DEFAULT, true));
            play(city);
        }
        List<LongTermBond> lines = callables(city);
        check("fixture: the city holds a callable line", !lines.isEmpty());
        if (lines.isEmpty()) return;
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = city.saveGame(10, "the call").ok);
        check("fixture: the city saved to slot 10 of its scratch root", ok[0]);
        Game back = new Game(city.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        DebtManager loaded = back.getDebtManager();
        check("fixture: the loaded city has the same pieces", loaded.getDebt().size() == ledger.getDebt().size());
        near("the loaded city measures the same volatility", back.rateVolatility(), city.rateVolatility(), 0);
        int callable = 0;
        for (int i = 0; i < ledger.getDebt().size(); i++) {
            Debt a = ledger.getDebt().get(i), b = loaded.getDebt().get(i);
            if (!(a instanceof LongTermBond x) || !x.isCallable()) {
                check("piece " + i + " (" + a.getType() + ") is not callable, and is not after the load", !(b instanceof LongTermBond y && y.isCallable()));
                continue;
            }
            callable++;
            check("piece " + i + ": a callable line is a callable line after the load", b instanceof LongTermBond y && y.isCallable());
            LongTermBond y = (LongTermBond) b;
            check("...with the same first call month (" + x.getFirstCallMonth() + ")", y.getFirstCallMonth() == x.getFirstCallMonth());
            near("...the same coupon", y.couponRate(), x.couponRate(), 0);
            check("...the same months to its first call", y.monthsToFirstCall() == x.monthsToFirstCall());
            near("...and the same market value, to the bit", loaded.marketValue(y), ledger.marketValue(x), 0);
            check("...and callable under the same rule", back.canCall(y) == city.canCall(x));
        }
        check("fixture: the callable lines were among the pieces", callable == lines.size());
    }
}

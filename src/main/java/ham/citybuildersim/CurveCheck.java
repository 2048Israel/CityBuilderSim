package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;

/**
 * Proves the curve from the dial the market expects (CD1, spec-city-debt.md
 * section 3): the expected dial's path, the zero rates it compounds to, the
 * price of every payment at its own month's factor, and every issue and
 * buyback priced off the same curve. Not part of the game.
 *
 * WHY THIS EXISTS. Until CD1 the city's curve sat on the dial in parallel
 * (a premium that depended on the months and on nothing else) and a piece was
 * priced at ONE flat rate, so the yield The book printed was the rate it had
 * been handed and a hand on the dial bought cheap fifty-year money (spec
 * section 1.2 and 1.4). The curve now drifts the dial to the rule over
 * YieldCurve.DRIFT_MONTHS at the inflation the city expects, and each payment
 * is discounted on its own month. Every claim of that design is a recurrence
 * or a sum with a constant in it, so each is asserted against its constant on
 * a fixture that causes the condition: the study's four cities (section 3.3)
 * as bare DebtManagers, and Game cities for the month, the quotes, the save.
 *
 * What it has to prove, a section each:
 *
 *   1. Calm prices as today: on target with the dial at neutral the expected
 *      dial is the dial at every month, the zero rate is the short rate plus
 *      the term premium less the compression, section 3.3's calm row to the
 *      hundredth, and a manager nobody has struck prices the same flat curve.
 *   2. Hiking inverts: the gap between the dial and the rule closes by e in
 *      DRIFT_MONTHS, the inflation expected closes by e in ADAPT_MONTHS and
 *      settles on the rule at where it settles, the 10-year sits under the
 *      1-year, the short end is unmoved, and the hiking row.
 *   3. Behind is steep: the 1-year over the 1-month by more than two points,
 *      the 10-year three points over the old parallel curve's, the row.
 *   4. QE bends the long end: a quarter of the term paper held takes its
 *      share of the term premium off the long rates and nothing off the short.
 *   5. The autopilot's gap is nothing: in a Game city on the rule, the struck
 *      pair is what the month's top read, the rule at the struck inflation IS
 *      the dial, and the only gap expected to close is the floor over the
 *      dial - at Standard and at Strict.
 *   6. Every payment on its month: a term loan, a serial, a note and a dollar
 *      term loan are worth the hand sum of their flows at their factors; the
 *      solved yield reprices them flat to the cent; two 20-year loans with
 *      different coupons yield differently on a steep curve.
 *   7. The issue at the curve: on a steep city's curve the serial's booked
 *      coupon prices its schedule at par, the term loan is worth its quoted
 *      gross the moment it is booked, the note is worth at least what it raised.
 *   8. No money pump: issued and bought straight back, each kind, home and
 *      abroad (the dollar legs where the window is open, and always on
 *      ForeignDebtCheck's trading city), in the steep city and in a calm one,
 *      the city is no richer and the piece is gone.
 *   9. BondMarket reads it: the market's benchmark is the city's curveRate().
 *  10. The strike holds through the month and a save: the pair, the curve and
 *      every piece's price survive a save and a load to the bit, and the pair
 *      struck at a month's top is not moved by the month's end.
 *  11. The parts add up: the dial expected, the credit spread, the term
 *      premium less the compression are the zero rate, in all four cities.
 *  12. How often it fires in a real run: the months the 10-year sits under the
 *      1-year and the months the dial is off the rule by more than a quarter
 *      point, over 120 months of the playtest's founding.
 *
 * @author Jerus
 */
public class CurveCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-110s FAIL  %.12f != %.12f (off by %.3e, allowed %.3e)%n", label, actual, expected,
                    Math.abs(actual - expected), tol);
        } else {
            out.printf("%-110s OK%n", label);
        }
    }

    /** ...for a figure in money, where the tolerance is a share of it. */
    static void closeRel(String label, double actual, double expected, double relTol) {
        boolean ok = Math.abs(actual - expected) <= relTol * Math.abs(expected);
        if (!ok) {
            fails++;
            out.printf("%-110s FAIL  %.6f != %.6f (off by %.3e of it, allowed %.1e)%n", label, actual, expected,
                    Math.abs(actual - expected) / Math.abs(expected), relTol);
        } else {
            out.printf("%-110s OK%n", label);
        }
    }

    /** Two figures that must be the same number to the bit. */
    static void same(String label, double actual, double expected) {
        boolean ok = Double.compare(actual, expected) == 0;
        if (!ok) {
            fails++;
            out.printf("%-110s FAIL  %.17g != %.17g%n", label, actual, expected);
        } else {
            out.printf("%-110s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("curve");
        long started = System.currentTimeMillis();

        calm();
        hiking();
        behind();
        qe();
        autopilot(root);
        pieces();
        Game steep = steepCity(root, "steep");
        issueAtTheCurve(steep);
        noMoneyPump(root, steep);
        bondMarket(steep);
        theStrike(root, steep);
        theParts();
        howOften(root);

        cleanUp(root);
        out.printf("%nCurveCheck ran %.1f s.%n", (System.currentTimeMillis() - started) / 1000.0);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------------------ fixtures ------------------------------ */

    /** The target the study's cities aim at: the game's default, the figure the rule's neutral rate is struck on. */
    static final double TARGET = .02;

    /** The study's economy and debt (spec section 3.3, debt-model.py): a year's output and a year's tax, in local money, and the term paper outstanding. */
    static final double GDP_YEAR = 5.4e9, TAX_YEAR = 1.1e9, DEBT = 3.4e9;

    /** The maturities section 1 asserts at, in months. */
    static final int[] MONTHS = {3, 12, 24, 60, 120, 240, 360, 600};

    /** Section 3.3's columns: 1 month, 1, 2, 5, 10, 20, 30 and 50 years. */
    static final int[] ROW = {1, 12, 24, 60, 120, 240, 360, 600};

    /** The study's four cities: the dial, the inflation now, the credibility, the share of the term paper the central bank holds, and section 3.3's row in per cent. */
    record Study(String name, double dial, double inflationNow, double credibility, double centralBankShare, double[] row) { }

    static final Study CALM = new Study("calm", .03, .02, .80, 0,
            new double[] {3.12, 3.12, 3.18, 3.35, 3.62, 4.02, 4.27, 4.62});
    static final Study HIKING = new Study("hiking", .07, .06, .65, 0,
            new double[] {7.12, 6.85, 6.50, 6.04, 6.02, 6.27, 6.47, 6.78});
    static final Study BEHIND = new Study("behind", .01, .05, .60, 0,
            new double[] {1.12, 3.65, 4.46, 5.00, 5.35, 5.79, 6.05, 6.41});
    static final Study QE = new Study("QE", .03, .02, .80, .25,
            new double[] {3.12, 3.12, 3.15, 3.24, 3.37, 3.57, 3.70, 3.87});
    static final Study[] CITIES = {CALM, HIKING, BEHIND, QE};

    /**
     * Where a city expects inflation to settle: credibility x target + (1 -
     * credibility) x pi_now. This is Expectations.takeMonth()'s `expected`
     * (credibility x target + (1 - credibility) x smoothed), with the year's
     * inflation standing for the smoothed figure as the study's does; the
     * EXPECTED_FLOOR it is held over does not bind at any of these cities
     * (each fixture says so).
     */
    static double settle(Study s) {
        return s.credibility() * TARGET + (1 - s.credibility()) * s.inflationNow();
    }

    /**
     * A bare city of the study's: the target, the bank's money free so the
     * floor is the dial, the economy and the debt the study priced, one
     * domestic term loan carrying it (a quarter of it moved to the central
     * bank in the QE city), the dial, and - if struck - the inflation now and
     * where it settles.
     */
    static DebtManager bare(Study s, boolean struck) {
        DebtManager dm = new DebtManager();
        dm.setInflationTarget(TARGET);
        dm.setCostOfFunds(0);
        dm.setGDP(GDP_YEAR / 12);
        dm.setTaxRevenue(TAX_YEAR / 12);
        Debt bond = dm.addLongTermBond(DEBT, 360, 0, .04);
        if (s.centralBankShare() > 0) bond.moveToCentralBank(s.centralBankShare() * DEBT);
        dm.setPolicyRate(s.dial());
        dm.updateInterest();
        if (struck) dm.strikeExpectations(s.inflationNow(), settle(s));
        return dm;
    }

    /** The inflation expected t months ahead: pi_inf + (pi_now - pi_inf) e^(-t / ADAPT_MONTHS), the spec's section 3.1. */
    static double inflationIn(double now, double settle, double t) {
        return settle + (now - settle) * Math.exp(-t / Expectations.ADAPT_MONTHS);
    }

    /** The inflation at which the city's own rule gives this rate: bisection on advisedPolicyRate(), which rises with inflation between the target and a rate of 100%. */
    static double inflationFor(DebtManager dm, double rate) {
        double lo = dm.getInflationTarget(), hi = .5;
        for (int i = 0; i < 200; i++) {
            double mid = (lo + hi) / 2;
            if (dm.advisedPolicyRate(mid) < rate) lo = mid; else hi = mid;
        }
        return (lo + hi) / 2;
    }

    static String months(int m) {
        return m % 12 == 0 ? (m / 12) + "-year" : m + "-month";
    }

    /** Prints the model's row beside the study's and asserts each point to the hundredth of a point. */
    static void row(String who, DebtManager dm, double[] study) {
        StringBuilder line = new StringBuilder("   " + who + " row, per cent:");
        for (int m : ROW) line.append(String.format(" %.4f", dm.curveRate(m) * 100));
        out.println(line);
        for (int i = 0; i < ROW.length; i++) {
            close("the " + who + " city's " + months(ROW[i]) + " rate is the study's " + study[i] + " to the hundredth",
                    dm.curveRate(ROW[i]) * 100, study[i], .005);
        }
    }

    /** One press of a city, as the playtest steps it: a broke city steps rather than skips. */
    static void press(Game g) {
        int before = g.getMonth();
        g.simulateMonths(1);
        if (g.getMonth() == before) g.toggleNextMonth();
    }

    static void pressQuietly(Game g) {
        quietly(() -> press(g));
    }

    /** The playtest's founding with the rule's hand on the dial, played on to this month. */
    static Game city(Path root, String label, int toMonth) {
        Game[] g = new Game[1];
        quietly(() -> {
            g[0] = MonetaryCheck.founding(root, label);
            g[0].getDebtManager().setAutopilot(true);
            while (g[0].getMonth() < toMonth) press(g[0]);
        });
        return g[0];
    }

    /** The inflation the month's top reads: the year's, or the target before there is one (Game.strikeTheCurve()). */
    static double inflationRead(Game g) {
        return g.getPriceIndex().hasRate() ? g.getPriceIndex().inflation() : g.getDebtManager().getInflationTarget();
    }

    static Debt lastPiece(Game g) {
        List<Debt> d = g.getDebtManager().getDebt();
        return d.get(d.size() - 1);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }

    /* ================= 1. calm prices as today ================= */
    static void calm() {
        out.println("--- 1. calm prices as today ---");
        DebtManager dm = bare(CALM, true);
        check("fixture: the calm city is struck, its dial is neutral and the debt priced is the study's",
                dm.isStruck() && dm.getPolicyRate() == DebtManager.NEUTRAL_RATE
                        && Math.abs(dm.getPricedDebt() - DEBT) < 1);
        check("fixture: it expects its target, so the rule has no reason to move",
                Math.abs(settle(CALM) - TARGET) < 1e-15 && settle(CALM) > Expectations.EXPECTED_FLOOR);
        check("fixture: the bank's money costs nothing, so the floor is the dial", dm.floorRate() == dm.getPolicyRate());

        double worst = 0;
        for (int t = 0; t <= YieldCurve.HORIZON; t++) worst = Math.max(worst, Math.abs(dm.expectedDialIn(t) - dm.getPolicyRate()));
        close("the dial the market expects is the dial at every month to the horizon", worst, 0, 1e-12);
        close("the short rate is the dial plus the two credit spreads", dm.getRate(),
                dm.getPolicyRate() + dm.gdpSpread() + dm.revenueSpread(), 1e-12);
        check("fixture: the credit spreads are something, so the short rate is more than the dial",
                dm.gdpSpread() + dm.revenueSpread() > .0005);
        for (int m : MONTHS) {
            close("the calm " + months(m) + " rate is the short rate plus the term premium less the compression",
                    dm.curveRate(m), dm.getRate() + DebtManager.termPremium(m) - dm.compression(m), 1e-12);
        }
        row("calm", dm, CALM.row());

        DebtManager unstruck = bare(CALM, false);
        check("fixture: a manager nobody has struck says so, and prices a flat path", !unstruck.isStruck()
                && unstruck.curve().isFlat());
        double apart = 0;
        for (int m : MONTHS) apart = Math.max(apart, Math.abs(unstruck.curveRate(m) - dm.curveRate(m)));
        close("an unstruck manager prices the calm city's curve at every maturity", apart, 0, 1e-12);
    }

    /* ================= 2. hiking inverts ================= */
    static void hiking() {
        out.println("\n--- 2. hiking inverts ---");
        DebtManager dm = bare(HIKING, true);
        double now = HIKING.inflationNow(), settle = settle(HIKING);
        DoubleUnaryOperator rule = dm::advisedPolicyRate;
        check("fixture: the hiking city is struck with the study's dial, and its credibility puts the settle at 3.4%",
                dm.isStruck() && dm.getPolicyRate() == .07 && Math.abs(settle - .034) < 1e-12
                        && settle > Expectations.EXPECTED_FLOOR);
        double p0 = dm.expectedDialIn(0), r0 = rule.applyAsDouble(inflationIn(now, settle, 0));
        check("fixture: the rule says more than the dial (9% against 7%), so there is a gap to close",
                Math.abs(r0 - .09) < 1e-9 && p0 == dm.floorRate() && p0 == .07);
        double d = YieldCurve.DRIFT_MONTHS;
        double gapDrift = dm.expectedDialIn(YieldCurve.DRIFT_MONTHS)
                - rule.applyAsDouble(inflationIn(now, settle, d));
        close("the gap between the expected dial and the rule closes by e^-1 in DRIFT_MONTHS",
                gapDrift / (p0 - r0), Math.exp(-1), 1e-9);

        // pi(ADAPT_MONTHS), read back out of the path: the rule's value there is the path less the gap still
        // open, and the inflation that gives that rule is the one the city expects a year ahead.
        double a = Expectations.ADAPT_MONTHS;
        double ruleAtAdapt = dm.expectedDialIn(Expectations.ADAPT_MONTHS) - (p0 - r0) * Math.exp(-a / d);
        double piAtAdapt = inflationFor(dm, ruleAtAdapt);
        close("the inflation expected a year ahead is e^-1 of the way from where it is to where it settles",
                (piAtAdapt - settle) / (now - settle), Math.exp(-1), 1e-6);
        close("...which is pi_inf + (pi_now - pi_inf) e^-1", piAtAdapt, inflationIn(now, settle, a), 1e-9);
        close("the dial expected at the horizon is the rule at where inflation settles",
                dm.expectedDialIn(YieldCurve.HORIZON - 1), rule.applyAsDouble(settle), 1e-6);

        check("the 10-year sits under the 1-year: the curve inverts", dm.curveRate(120) < dm.curveRate(12));
        close("the 1-month is the dial plus the two spreads: the short end is unmoved", dm.curveRate(1),
                dm.getPolicyRate() + dm.gdpSpread() + dm.revenueSpread(), 1e-12);
        close("...and it is getRate()", dm.curveRate(1), dm.getRate(), 1e-12);
        row("hiking", dm, HIKING.row());
    }

    /* ================= 3. behind is steep ================= */
    static void behind() {
        out.println("\n--- 3. behind is steep ---");
        DebtManager dm = bare(BEHIND, true);
        check("fixture: the behind city holds its dial at 1% by hand against a rule far above it",
                dm.isStruck() && dm.getPolicyRate() == .01
                        && dm.advisedPolicyRate(BEHIND.inflationNow()) - dm.getPolicyRate() > .05
                        && settle(BEHIND) > Expectations.EXPECTED_FLOOR);
        check("fixture: the bank's money costs nothing, so the floor is the dial", dm.floorRate() == dm.getPolicyRate());
        check("the 1-year is more than two points over the 1-month", dm.curveRate(12) - dm.curveRate(1) > .02);
        double parallel = dm.getRate() + DebtManager.TERM_PREMIUM_10Y;
        out.printf("   behind: the 10-year %.4f, the old parallel curve's %.4f%n", dm.curveRate(120), parallel);
        check("the 10-year is at least three points over the old parallel curve's: cheap long money is gone",
                dm.curveRate(120) - parallel >= .03);
        row("behind", dm, BEHIND.row());
    }

    /* ================= 4. QE bends the long end ================= */
    static void qe() {
        out.println("\n--- 4. QE bends the long end ---");
        DebtManager calm = bare(CALM, true), qe = bare(QE, true);
        check("fixture: the central bank holds a quarter of the QE city's term paper and none of the calm city's",
                Math.abs(qe.centralBankShareOfTerm() - .25) < 1e-12 && calm.centralBankShareOfTerm() == 0);
        check("fixture: the two cities have the same floor and the same short rate",
                qe.floorRate() == calm.floorRate() && Math.abs(qe.getRate() - calm.getRate()) < 1e-12);
        double share = Math.min(1, .25 / CentralBank.FULL_COMPRESSION_SHARE);
        check("fixture: a quarter held is a part of the full compression, not all of it", share > 0 && share < 1);
        for (int m : new int[] {120, 360, 600}) {
            close("the QE city's " + months(m) + " rate is the calm city's less the share of the premium taken off",
                    qe.curveRate(m),
                    calm.curveRate(m) - share * DebtManager.termPremium(m) * CentralBank.QE_COMPRESSION, 1e-12);
        }
        close("nothing is taken off at a year: the short end has no premium", qe.curveRate(12), calm.curveRate(12), 1e-12);
        close("...nor at a month", qe.curveRate(1), calm.curveRate(1), 1e-12);
        row("QE", qe, QE.row());
    }

    /* ================= 5. the autopilot's gap is nothing ================= */
    static void autopilot(Path root) {
        out.println("\n--- 5. the autopilot's gap is nothing ---");
        Game c = city(root, "x5", 54);
        DebtManager dm = c.getDebtManager();
        check("fixture: the city is past its base month with a year of prices, and the rule has the dial",
                c.getPriceIndex().isBased() && c.getPriceIndex().hasRate() && dm.isAutopilot());
        autopilotMonth(c, "Standard");

        dm.setStrictness(DebtManager.Strictness.STRICT);
        double inflation = c.getPriceIndex().inflation();
        check("fixture: at Strict the rule says something other than Standard's at this inflation, so the curve's rule matters",
                Math.abs(dm.advisedPolicyRate(inflation, DebtManager.Strictness.STRICT)
                        - dm.advisedPolicyRate(inflation, DebtManager.Strictness.STANDARD)) > .001);
        autopilotMonth(c, "Strict");
    }

    /** One press of the autopilot city, read against what the month's top read just before it. */
    static void autopilotMonth(Game c, String at) {
        DebtManager dm = c.getDebtManager();
        double inflation = c.getPriceIndex().inflation();
        double expected = c.getExpectations().getExpectedInflation();
        pressQuietly(c);
        check("at " + at + ": the curve's expectations are struck", dm.isStruck());
        same("at " + at + ": the struck inflation is the price index's, as read just before the press",
                dm.getStruckInflation(), inflation);
        same("at " + at + ": the struck expectation is the city's, as read just before the press",
                dm.getStruckExpected(), expected);
        close("at " + at + ": the rule at the struck inflation IS the dial", dm.advisedPolicyRate(dm.getStruckInflation()),
                dm.getPolicyRate(), 1e-12);
        double drift = YieldCurve.DRIFT_MONTHS;
        double ruleThen = dm.advisedPolicyRate(
                inflationIn(dm.getStruckInflation(), dm.getStruckExpected(), drift));
        close("at " + at + ": the gap the market expects to close is the floor over the dial, a DRIFT_MONTHS e-fold of it",
                dm.expectedDialIn(YieldCurve.DRIFT_MONTHS) - ruleThen,
                (dm.floorRate() - dm.getPolicyRate()) * Math.exp(-1), 1e-12);
        check("at " + at + ": the inflation is above where the city expects it to settle, so the rule is expected to fall",
                Math.abs(dm.expectedDialIn(0) - dm.expectedDialIn(YieldCurve.HORIZON - 1)) > .005);
    }

    /* ================= 6. every payment on its month ================= */
    static double handSum(Debt piece, IntToDoubleFunction factor) {
        double[] flows = piece.remainingCashFlows();
        double sum = 0;
        for (int i = 0; i < flows.length; i++) sum += flows[i] * factor.applyAsDouble(i + 1);
        return sum;
    }

    static void pieces() {
        out.println("\n--- 6. every payment on its month ---");
        DebtManager dm = bare(HIKING, true);
        dm.setExchangeRate(1);
        Debt term = dm.addLongTermBond(1e8, 240, 0, .06);
        Debt serial = dm.addMediumTermBond(1e8, 60, 0, .06);
        Debt note = dm.addShortTermTBill(1e8, 9, 0);
        Debt dollar = dm.addLongTermBond(1e8, 120, 0, .05, true);
        dm.updateInterest();
        check("fixture: each piece has a payment for every month it has left",
                term.remainingCashFlows().length == 240 && serial.remainingCashFlows().length == 60
                        && note.remainingCashFlows().length == 9 && dollar.remainingCashFlows().length == 120);
        check("fixture: the dollar piece is dollars, at parity", dollar.isForeign() && dm.getExchangeRate() == 1);

        Object[][] book = {{"term loan", term}, {"serial", serial}, {"note", note}};
        for (Object[] row : book) {
            Debt p = (Debt) row[1];
            closeRel("the " + row[0] + " is worth the sum of its payments at their months' factors on the city's curve",
                    dm.marketValue(p), handSum(p, dm.curve()::factor), 1e-9);
        }
        closeRel("the dollar term loan is worth the sum of its payments at the world's factors",
                dm.marketValue(dollar), handSum(dollar, dm::foreignFactor), 1e-9);
        double oneRate = term.getMarketValue(dm.curveRate(term.getRemainingMonths()));
        out.printf("   the term loan: payment by payment %,.0f, at the old single rate %,.0f%n", dm.marketValue(term), oneRate);
        check("fixture: pricing the term loan at one rate would have been a different price (the curve is not flat)",
                Math.abs(oneRate - dm.marketValue(term)) > 1e-3 * dm.marketValue(term));

        Object[][] all = {{"term loan", term}, {"serial", serial}, {"note", note}, {"dollar term loan", dollar}};
        for (Object[] row : all) {
            Debt p = (Debt) row[1];
            close("the " + row[0] + " at its own solved yield, priced flat, is its market value to the cent",
                    p.getMarketValue(dm.yieldOf(p)), dm.marketValue(p), .01);
        }

        DebtManager steep = bare(BEHIND, true);
        Debt low = steep.addLongTermBond(1e8, 240, 0, .04);
        Debt high = steep.addLongTermBond(1e8, 240, 0, .05);
        steep.updateInterest();
        double yLow = steep.yieldOf(low), yHigh = steep.yieldOf(high);
        out.printf("   behind city, 20-year loans a point apart: yields %.4f and %.4f, the curve's 20-year %.4f%n",
                yLow, yHigh, steep.curveRate(240));
        check("two 20-year term loans with coupons a point apart yield differently on a steep curve",
                Math.abs(yLow - yHigh) > 1e-5);
        check("...and neither yield is the curve's rate at 240 months: the yield is a result, not the rate it was priced at",
                Math.abs(yLow - steep.curveRate(240)) > 1e-4 && Math.abs(yHigh - steep.curveRate(240)) > 1e-4);
    }

    /* ================= 7. the issue at the curve ================= */

    /** What the issue sections borrow: small against what the founding city earns (its output is about 3,300 a month). */
    static final double ASK = 20_000;

    /**
     * A founded city with the rule's hand on the dial to month 38, then the
     * dial held two points under the rule by hand and one press on: the
     * market expects the dial to rise to the rule, so the curve is steep. The
     * city has no year of prices yet at this month, so the rule reads the
     * target, which is the month's own strike.
     */
    static Game steepCity(Path root, String label) {
        Game g = city(root, label, 38);
        DebtManager dm = g.getDebtManager();
        double rule = dm.advisedPolicyRate(inflationRead(g));
        dm.takeTheDial(Math.max(0, rule - .02));
        pressQuietly(g);
        return g;
    }

    static void issueAtTheCurve(Game g) {
        out.println("\n--- 7. the issue at the curve ---");
        DebtManager dm = g.getDebtManager();
        YieldCurve curve = dm.curve();
        out.printf("   the steep city at month %d: 1-month %.4f, 1-year %.4f, 10-year %.4f%n", g.getMonth(),
                dm.curveRate(1) * 100, dm.curveRate(12) * 100, dm.curveRate(120) * 100);
        check("fixture: the dial is held by hand, two points under the rule at the inflation the month struck",
                !dm.isAutopilot() && dm.isStruck()
                        && Math.abs(dm.advisedPolicyRate(dm.getStruckInflation()) - dm.getPolicyRate() - .02) < 1e-12);
        check("fixture: its curve is steep - not flat, the 1-year over the 1-month and the 10-year over the 1-month by points",
                !curve.isFlat() && dm.curveRate(12) - dm.curveRate(1) > .005 && dm.curveRate(120) - dm.curveRate(1) > .02);
        check("fixture: the city can pay for buying back any one piece it borrows (a buyback costs about what the piece raised)",
                g.getCash() > 3 * ASK);

        // The serial: the coupon is the one that prices its schedule at par on the curve.
        int before = dm.getDebt().size();
        quietly(() -> g.handleMediumBondLogic(ASK, 5, 1000));
        check("fixture: the serial was booked", dm.getDebt().size() == before + 1);
        Debt serial = lastPiece(g);
        double face = serial.getFaceValue();
        out.printf("   the serial: face %,.2f, worth %,.4f%n", face, dm.marketValue(serial));
        close("the serial's booked coupon prices its schedule at par on the curve: its market value is its face, to a cent per million",
                dm.marketValue(serial), face, .01 * face / 1e6);

        // The term loan: worth its quoted gross the moment it is booked.
        DebtQuote quote = g.quoteLongBond(ASK, 20, 1000);
        quietly(() -> g.handleLongBondLogic(ASK, 20, 1000));
        check("fixture: the term loan was booked on the quote it was shown",
                dm.getDebt().size() == before + 2 && lastPiece(g).getFaceValue() == quote.faceValue());
        Debt term = lastPiece(g);
        double gross = quote.cashReceived() + g.costOfIssuance(quote.faceValue());
        out.printf("   the term loan: face %,.2f, quoted gross %,.4f, worth %,.4f, yield %.4f against coupon %.4f%n",
                quote.faceValue(), gross, dm.marketValue(term), dm.yieldOf(term) * 100, quote.couponRate() * 100);
        // The quote's proceeds are rounded to the cent, which is half a cent either way of the gross.
        close("the term loan is worth its quoted gross (what it raised and the fees) the moment it is booked, to a cent per million",
                dm.marketValue(term), gross, .005 + .01 * quote.faceValue() / 1e6);

        // The note: at or above what it raised.
        DebtQuote note = g.quoteTBill(ASK, 12, 1000);
        quietly(() -> g.handleTBillLogic(ASK, 12, 1000));
        check("fixture: the note was booked on the quote it was shown",
                dm.getDebt().size() == before + 3 && lastPiece(g).getFaceValue() == note.faceValue());
        Debt bill = lastPiece(g);
        out.printf("   the note: face %,.2f, raised %,.4f, worth %,.4f%n", note.faceValue(), note.cashReceived(),
                dm.marketValue(bill));
        check("the note's market value is at or above what it raised", dm.marketValue(bill) >= note.cashReceived());
    }

    /* ================= 8. no money pump ================= */

    /** What the dollar legs borrow, in dollars. */
    static final double USD_ASK = 1_000;

    static void noMoneyPump(Path root, Game steep) throws Exception {
        out.println("\n--- 8. no money pump ---");
        roundTrips("steep", steep);
        roundTrips("calm", city(root, "calm", 54));
        Game trading = tradingCity(root);
        roundTrips("trading", trading);
    }

    static Game tradingCity(Path root) throws Exception {
        Game[] g = new Game[1];
        Exception[] thrown = new Exception[1];
        quietly(() -> {
            try { g[0] = ForeignDebtCheck.tradingCity(root.resolve("trading")); } catch (Exception e) { thrown[0] = e; }
        });
        if (thrown[0] != null) throw thrown[0];
        return g[0];
    }

    /** Issues each kind and buys it straight back: the city ends no richer, the piece is off the books. */
    static void roundTrips(String city, Game g) {
        boolean tradingCity = "trading".equals(city);
        if (!tradingCity) {
            roundTrip(city, g, "Note", () -> g.handleTBillLogic(ASK, 12, 1000));
            roundTrip(city, g, "Serial", () -> g.handleMediumBondLogic(ASK, 5, 1000));
            // A bond sold while a line of its term is the benchmark goes into that line (CD2), and the round trip
            // buys back the piece it sold - the whole line, the 20-year one section 7 left standing too. So the
            // round trip sells the 30-year, a line of its own.
            roundTrip(city, g, "Term", () -> g.handleLongBondLogic(ASK, 30, 1000));
        }
        // The dollar legs: on the home cities when the window is open, and always on ForeignDebtCheck's trading
        // city, the fixture RestructureCheck proves the world's pricing on.
        if (!g.foreignWindowOpen()) {
            out.println("   the " + city + " city's dollar window is shut (" + g.foreignWindowReason()
                    + "): no dollar round trip there");
            return;
        }
        double ask = tradingCity ? RestructureCheck.DOLLAR_ASK : USD_ASK;
        roundTrip(city + " dollar", g, "Note", () -> g.handleForeignLogic("Note", ask, 12, 100, false));
        roundTrip(city + " dollar", g, "Term", () -> g.handleForeignLogic("Term", ask, 20, 100, false));
    }

    static void roundTrip(String city, Game g, String kind, Runnable issue) {
        DebtManager dm = g.getDebtManager();
        double worst = Double.NEGATIVE_INFINITY;
        for (int round = 0; round < 2; round++) {
            double before = g.getCash();
            int pieces = dm.getDebt().size();
            quietly(issue);
            boolean booked = dm.getDebt().size() == pieces + 1;
            check("fixture: the " + city + " " + kind + " was booked" + (round == 0 ? "" : " again"), booked);
            if (!booked) return;
            Debt piece = lastPiece(g);
            double raised = g.getCash() - before;
            quietly(() -> g.repurchaseDebt(piece));
            check("the " + city + " " + kind + " is off the books after the buyback", !dm.getDebt().contains(piece));
            double net = g.getCash() - before;
            worst = Math.max(worst, net);
            check("the " + city + " " + kind + " bought straight back leaves the city no richer (raised "
                    + String.format("%,.2f", raised) + ", net " + String.format("%,.2f", net) + ")", net <= 0);
        }
        out.printf("   %s %s: the best of two round trips nets the city %,.2f%n", city, kind, worst);
    }

    /* ================= 9. BondMarket reads it ================= */
    static void bondMarket(Game g) throws Exception {
        out.println("\n--- 9. BondMarket reads the city's curve ---");
        BondMarket.Readings readings = g.bondReadings();
        check("fixture: the bond market is attached to the city's readings", g.getBondMarket().readings() == readings);
        DebtManager dm = g.getDebtManager();
        check("fixture: the curve being read is the steep one, not a flat one", !dm.curve().isFlat());
        for (int m : new int[] {12, 120, 360}) {
            same("the bond market's benchmark at " + months(m) + " is the city's curveRate()", readings.curve(m), dm.curveRate(m));
        }
        check("...and the curve it reads is not the parallel one: its 10-year is not the short rate plus the premium",
                Math.abs(readings.curve(120) - (dm.getRate() + DebtManager.termPremium(120))) > 1e-4);
    }

    /* ================= 10. the strike holds through the month and a save ================= */
    static void theStrike(Path root, Game g) throws Exception {
        out.println("\n--- 10. the strike holds through the month and a save ---");
        DebtManager dm = g.getDebtManager();
        GameFiles files = g.getGameFiles();
        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = g.saveGame(10, "the curve").ok);
        check("fixture: the city, with its pieces on the books, saved to slot 10 of its scratch root",
                ok[0] && dm.getDebt().size() >= 3 && dm.isStruck());
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        DebtManager bm = back.getDebtManager();
        check("fixture: the loaded city has the same pieces", bm.getDebt().size() == dm.getDebt().size());
        check("the loaded city is struck", bm.isStruck());
        same("the struck inflation comes back to the bit", bm.getStruckInflation(), dm.getStruckInflation());
        same("the struck expectation comes back to the bit", bm.getStruckExpected(), dm.getStruckExpected());
        for (int m : new int[] {1, 3, 12, 24, 60, 120, 240, 360, 600}) {
            same("the " + months(m) + " rate comes back to the bit", bm.curveRate(m), dm.curveRate(m));
        }
        for (int i = 0; i < dm.getDebt().size(); i++) {
            Debt a = dm.getDebt().get(i), b = bm.getDebt().get(i);
            same("piece " + i + " (" + a.getType() + ") is worth the same after the load", bm.marketValue(b), dm.marketValue(a));
        }

        // The pair struck at the month's top is held to the month's end. Press on to a month where the end
        // of the month moves the expectation (the founding has no year of prices at first, and an
        // expectation with nothing to read does not move).
        Expectations x = g.getExpectations();
        double expectedBefore = x.getExpectedInflation();
        double inflationBefore = inflationRead(g);
        int guard = 0;
        while (guard++ < 24) {
            expectedBefore = x.getExpectedInflation();
            inflationBefore = inflationRead(g);
            pressQuietly(g);
            if (x.getExpectedInflation() != expectedBefore) break;
        }
        out.printf("   the month's end moved the expectation from %.6f to %.6f (month %d)%n", expectedBefore,
                x.getExpectedInflation(), g.getMonth());
        check("fixture: the end of the month moved the expectation", x.getExpectedInflation() != expectedBefore);
        same("after the month the struck expectation is what Expectations read before the press", dm.getStruckExpected(),
                expectedBefore);
        check("...and it is not what the month's end has since made of it", dm.getStruckExpected() != x.getExpectedInflation());
        same("after the month the struck inflation is what the month's top read", dm.getStruckInflation(), inflationBefore);
        double newlyExpected = x.getExpectedInflation(), newlyRead = inflationRead(g);
        pressQuietly(g);
        same("the next press strikes the new expectation", dm.getStruckExpected(), newlyExpected);
        same("...and the new inflation", dm.getStruckInflation(), newlyRead);
    }

    /* ================= 11. the parts add up ================= */
    static void theParts() {
        out.println("\n--- 11. the parts add up ---");
        for (Study s : CITIES) {
            DebtManager dm = bare(s, true);
            for (int m : new int[] {120, 360}) {
                close("the " + s.name() + " city's " + months(m) + " rate is the dial expected, the credit spread and the premium less QE",
                        dm.expectedDialOver(m) + dm.creditSpread() + DebtManager.termPremium(m) - dm.compression(m),
                        dm.curveRate(m), 1e-12);
            }
        }
        DebtManager qe = bare(QE, true);
        check("fixture: the QE city's compression is something, so the sum has a part to lose",
                qe.compression(360) > .001 && qe.creditSpread() > 0);
    }

    /* ================= 12. how often it fires in a real run ================= */
    static void howOften(Path root) {
        out.println("\n--- 12. how often it fires in a real run ---");
        for (boolean ruleHasTheDial : new boolean[] {false, true}) {
            Game[] g = new Game[1];
            quietly(() -> {
                g[0] = MonetaryCheck.founding(root, ruleHasTheDial ? "run-rule" : "run-held");
                if (ruleHasTheDial) g[0].getDebtManager().setAutopilot(true);
            });
            DebtManager dm = g[0].getDebtManager();
            int inverted = 0, steep = 0, gapped = 0, moved = 0, counted = 0;
            double widest = 0, pairInflation = Double.NaN, pairExpected = Double.NaN;
            int from = g[0].getMonth();
            for (int i = 0; i < 120; i++) {
                pressQuietly(g[0]);
                if (!dm.isStruck()) continue;
                counted++;
                if (dm.curveRate(120) < dm.curveRate(12)) inverted++;
                if (dm.curveRate(120) - dm.curveRate(12) > .02) steep++;
                double gap = Math.abs(dm.floorRate() - dm.advisedPolicyRate(dm.getStruckInflation()));
                widest = Math.max(widest, gap);
                if (gap > .0025) gapped++;
                if (Double.compare(pairInflation, dm.getStruckInflation()) != 0
                        || Double.compare(pairExpected, dm.getStruckExpected()) != 0) {
                    if (counted > 1) moved++;
                }
                pairInflation = dm.getStruckInflation();
                pairExpected = dm.getStruckExpected();
            }
            out.printf("   months %d-%d of the playtest's founding (%s): of %d months, the 10-year sat under the 1-year in %d, over it by"
                            + " more than two points in %d; the dial was more than a quarter point off the rule in %d (widest %.2f points);"
                            + " the struck pair moved in %d%n",
                    from + 1, g[0].getMonth(), ruleHasTheDial ? "the rule holds the dial" : "the dial as the founding leaves it",
                    counted, inverted, steep, gapped, widest * 100, moved);
            check("fixture: every month of the run was struck", counted == 120);
            check("the struck pair moved in at least one month of the run (" + (ruleHasTheDial ? "the rule's dial" : "the held dial") + ")",
                    moved >= 1);
        }
    }
}

package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

/**
 * The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md
 * sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's policy rate
 * plus a margin fixed at the sale, never under nothing; a linker pays a real
 * coupon on a principal that follows the city's price index and, at maturity,
 * the indexed face - never under the original face. Both are sold at the auction
 * at the margin or real coupon that prices them at 100 on the curve, and carried
 * on the books by effective interest: the floater's rate resets with its coupon,
 * the linker's carrying value catches up with each indexation.
 *
 * What this has to prove:
 *
 * 1. Fair prices it at 100: on a calm, a hiking and a flat curve, with a flat and
 *    a moving dial, the floater's payments at DebtManager.fairMargin() are worth
 *    100 (the harness sums the present value itself) and a basis point either way
 *    moves the price the right way; the linker's at fairRealCoupon() likewise with
 *    the index at 2%, at 0% (the par yield) and falling, where the floor binds and
 *    the last payment is the face. The study's figures are printed beside the game's.
 * 2. A sale, in a played city: the margin is the auction's fair margin plus its
 *    tail and the real coupon the fair plus the tail rounded down to Auction
 *    .COUPON_STEP, both caused by capping a sale far past the bids; the floater's
 *    index is the dial, the linker's base the price index; a second sale opens a
 *    second line; a 3-year floater, a 20-year linker and a dollar one are refused
 *    and change nothing; a default city with its rollover on never sells one unasked.
 * 3. The floater's coupon follows the dial: each month's coupon is the face over
 *    twelve at the dial plus the margin, read from the piece and from what its
 *    holders were paid; the effective rate resets with it and stays where it was
 *    when the dial did not move; the month's books hold; a margin under nothing
 *    pays nothing on a dial at nothing; its value stays near par through a move
 *    that takes a fixed bond of the same term down.
 * 4. The linker's uplift: under an index the harness drives (rising, flat,
 *    falling), the ratio follows the index over the base, each month's uplift is
 *    the carrying value's catch-up to the present value of the known schedule at
 *    the unchanged real rate, the coupon is the face times the ratio times the real
 *    coupon over twelve, the statements' uplift is the month's and is in the
 *    expenses, the treasury bridge foots with its not-cash row and leaves only the
 *    bank's coupon timing, and the walk from EARNED to the budget leaves nothing in
 *    a city whose paper the bank holds (and, where others hold it, only what it
 *    leaves for any paper: the coupons EARNED never counted).
 * 5. To maturity: a linker under a rising index repays its face through the
 *    principal and the uplift through the coupons, each holder by its share; under a
 *    falling one the original face (the floor) and no uplift; both end carried at
 *    nothing; over each life interest expense plus uplift booked is the cash coupons
 *    plus the face less what was raised, to the cent. A floater played to maturity
 *    with the dial moved twice closes the same way.
 * 6. The money closes: the audit closes every month pressed in 3-5 (and 7-9), the
 *    households, the bank, the world and the central bank holding parts; every
 *    holder was paid its share of each coupon and the cash coupons are what the
 *    treasury paid on its interest line, so nothing is counted twice.
 * 7. The buyback: half of a floater and half of a linker bought back at or over
 *    fair, fair being the market value on the dial and index it expects; the piece's
 *    face, carrying value and holders fall by what each sold and its rate, margin,
 *    real coupon and ratio stay; the audit closes; a round trip never nets the city
 *    money.
 * 8. The rollover: under SAME_STRUCTURE a floater falling due rolls into a floater
 *    and a linker into a linker, each of the nearest of its own terms, and the
 *    central bank's par with it; under TWELVE_MONTH_BILL into a bill.
 * 9. The save: a floater and a linker mid-life, saved and loaded, keep their type,
 *    margin, index, real coupon, base, ratio, carrying value, effective rate and
 *    market value, and the government block its uplift; the month after the load
 *    books what the same month played without the save does; a change of unit
 *    scales their money and not their rates.
 *
 * Each fixture causes its condition rather than finding a city in it. Where a
 * linker has to live a year and not ten, a real sale is shortened after it has
 * settled (so the households, the bank and the central bank hold it) and its books
 * reopened at the price it sold at; a bank-held fixture built through
 * DebtManager.addLinkedBond() stands beside it where the walk from EARNED has to be
 * nothing. The cash the harness sets in a city is no flow, and the treasury bridge
 * forgets it in one month, which the fixture lets pass first. Run with section
 * numbers (FloatLinkCheck 1 3) to run just those; 6 reads what 3-5 pressed.
 *
 * @author Jerus
 */
public class FloatLinkCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-104s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void near(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-104s FAIL  %,.9f != %,.9f (allowed %.1e)%n", label, actual, expected, tol);
        } else {
            out.printf("%-104s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    static <T> T quiet(Supplier<T> s) {
        System.setOut(quiet);
        try { return s.get(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static boolean closes(MoneyAudit.Result r) {
        return Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved());
    }

    static boolean wanted(String[] args, int section) {
        if (args.length == 0) return true;
        for (String a : args) if (a.equals(String.valueOf(section))) return true;
        return false;
    }

    /**
     * The worst gap between what the model did and what the harness worked out, over
     * every case a section met: reported once, and failed if no case ran at all.
     */
    static final class Worst {
        int n;
        double worst;
        String at = "";

        void add(double actual, double expected, String where) {
            n++;
            double e = Math.abs(actual - expected);
            if (Double.isNaN(e)) e = Double.POSITIVE_INFINITY;   // a figure that is no number is the worst gap there is
            if (e > worst) {
                worst = e;
                at = where;
            }
        }

        void report(String label, double tol) {
            boolean ok = n > 0 && worst <= tol;
            if (!ok) fails++;
            out.printf("%-104s %s%n", label + " (" + n + " cases)",
                    ok ? "OK" : n == 0 ? "FAIL  no case ran" : "FAIL  worst " + worst + " at " + at);
        }
    }

    /** The audits of every month the harness pressed, by what happened in it. */
    static final class Tally {
        final Map<String, int[]> byEvent = new LinkedHashMap<>();
        int months, open;
        double worst;
        String worstAt = "";

        void add(String event, MoneyAudit.Result r) {
            int[] c = byEvent.computeIfAbsent(event, k -> new int[2]);
            c[0]++;
            months++;
            if (!closes(r)) {
                c[1]++;
                open++;
                double gap = Math.abs(r.residual) / Math.max(1e-12, MoneyAudit.tolerance(r.moved()));
                if (gap > worst) {
                    worst = gap;
                    worstAt = event;
                }
            }
        }
    }

    static final Tally audits = new Tally();

    /** Which of sections 3-5 have run, for section 6 to know what it can ask of the months they pressed. */
    static final boolean[] ran = new boolean[10];

    /** A month, held to the audit and told to the tally. */
    static MoneyAudit.Result press(Game g, String event) {
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        audits.add(event, r);
        return r;
    }

    /* =====================================================================
       THE CROSS-CUTTING IDENTITIES, held at every press of 3-5 (section 6 reports them)
       ===================================================================== */

    /** Each holder was paid its share of every coupon: households, businesses, the world, the central bank. */
    static final Worst X_HOLDERS = new Worst();
    /** The cash coupons the pieces paid are what the treasury paid on its interest line, the uplift paid beside it. */
    static final Worst X_TREASURY = new Worst();
    /** The debt manager's month and the statements' lines are the sums of the pieces' months. */
    static final Worst X_BOOKS = new Worst();
    /** The bridge's not-cash row is the expense the cash never paid. */
    static final Worst X_NOTCASH = new Worst();
    /** The bridge foots through its journal and its residual. */
    static final Worst X_FOOTS = new Worst();
    /** The bridge's residual is the bank's coupon timing and nothing else: the uplift is not in it. */
    static final Worst X_BRIDGE = new Worst();
    /** The walk from EARNED to the budget leaves what EARNED never counted - the coupons paid beyond the bank - and nothing of the uplift. */
    static final Worst X_EARNED = new Worst();

    /** A price index the harness drives: the level at step k is the reference times the growth of the first k steps, and flat beyond the last. */
    static final class Index implements DoubleSupplier {
        final double ref;
        final double[] growth;
        int k;

        Index(double ref, double... growth) {
            this.ref = ref;
            this.growth = growth;
        }

        double levelAt(int steps) {
            double level = ref;
            for (int i = 0; i < steps && i < growth.length; i++) level *= growth[i];
            return level;
        }

        @Override
        public double getAsDouble() {
            return levelAt(k);
        }
    }

    /** One piece as a press finds it: the figures the month's identities start from. */
    static final class Snap {
        final Debt piece;
        final int remaining;
        final double carrying, eff, face, ratio, index, worldShare;
        final boolean settled;

        Snap(Debt d) {
            piece = d;
            remaining = d.getRemainingMonths();
            carrying = d.getCarryingValue();
            eff = d.getEffectiveRate();
            face = d.getFaceValue();
            ratio = d instanceof InflationLinkedBond l ? l.getRatio() : Double.NaN;
            index = d instanceof FloatingRateNote f ? f.getIndex() : Double.NaN;
            settled = d.unsettledFace() <= 0 && d.getSettleDue() <= 0;
            // The world leaves after the coupons are paid, when the hot money does (Game.worldPaperLeavesWithTheMoney()),
            // so a settled piece's world share at payment is the one the press found.
            worldShare = d.getOustandingPrincipal() > 0 ? d.worldPrincipal() / d.getOustandingPrincipal() : 0;
        }
    }

    /** What a piece cost and paid over its life: the books' identity is read off it at the end. */
    static final class Life {
        final Debt piece;
        final double raised;
        double expense, uplift, coupon;

        Life(Debt piece, double raised) {
            this.piece = piece;
            this.raised = raised;
        }
    }

    /** The piece-level identities of one stretch of presses, and the lives of the pieces followed. */
    static final class Books {
        final Worst coupon = new Worst(), index = new Worst(), unmoved = new Worst(), reset = new Worst(),
                ratio = new Worst(), catchUp = new Worst(), realRate = new Worst(), flat = new Worst(),
                upliftPaid = new Worst(), expense = new Worst(), carry = new Worst(), ended = new Worst();
        final Map<Debt, Life> lives = new IdentityHashMap<>();
        double prevBank = 0;
        int moves;

        void follow(Debt piece, double raised) {
            lives.put(piece, new Life(piece, raised));
        }
    }

    /**
     * A month pressed and held to everything the pieces owe the books: the coupon, the
     * reset or the catch-up, the effective interest, the carrying value; then the
     * statements' lines, the holders, the bridge and the walk from EARNED.
     */
    static MoneyAudit.Result press(Game city, Books b, String event) {
        DebtManager dm = city.getDebtManager();
        NationalAccounts na = city.getEconomyManager().getNationalAccounts();
        CentralBank cbank = city.getCentralBank();
        double dial = dm.getPolicyRate();
        double level = dm.priceIndexLevel();
        List<Snap> before = new ArrayList<>();
        for (Debt d : dm.getDebt()) if (d.getRemainingMonths() > 0 && !d.isForeign()) before.add(new Snap(d));

        // WN3: the businesses' share of a coupon is read where the coupon is struck - right after the settle, before the coupons are
        // paid (Game.nextMonth(): householdsTakeTheirShare() then processAllDebts()) - not from the month's end. On the world's band the
        // businesses' paper in the sale month stands 1.2% under its end-of-month holding at the moment they were paid (measured:
        // couponsToFirms 1.394 against 1.377 reckoned from the end; the month after, the two agree to the bit), i.e. they sell part of
        // what they bought at the settle to the desk later in that month (the cause is inferred, not traced to the call).
        Map<Debt, Double> firmsShare = new IdentityHashMap<>();
        city.settleProbeForTest = after -> {
            if (!after) return;
            for (Snap s : before) {
                double out = s.piece.getOustandingPrincipal();
                firmsShare.put(s.piece, out > 0 ? s.piece.firmPrincipal() / out : 0.0);
            }
        };
        MoneyAudit.Result r;
        try {
            r = press(city, event);
        } finally {
            city.settleProbeForTest = null;
        }

        double sumExp = 0, sumCpn = 0, sumUp = 0, sumUpPaid = 0;
        int maturing = 0;
        for (Snap s : before) if (s.remaining == 1) maturing++;
        for (Snap s : before) {
            Debt p = s.piece;
            String at = p.getType() + " " + p.getDuration() + "m, " + event;
            boolean last = s.remaining == 1;
            double cpn = p.getLastCoupon(), exp = p.getLastInterestExpense();
            double up = 0, upPaid = 0;
            if (p instanceof FloatingRateNote f) {
                double want = s.face * Math.max(0, dial + f.getMargin()) / 12;
                b.coupon.add(cpn, want, at);
                b.index.add(f.getIndex(), dial, at);
                if (dial == s.index) {
                    b.unmoved.add(f.getEffectiveRate(), s.eff, at);
                } else {
                    b.moves++;
                    if (!last) {
                        double[] known = new double[s.remaining];
                        Arrays.fill(known, want);
                        known[s.remaining - 1] += s.face;
                        b.reset.add(f.getEffectiveRate(), Debt.effectiveRateOf(known, s.carrying), at);
                    }
                }
            } else if (p instanceof InflationLinkedBond l) {
                double ratio = level / l.getBaseIndex();
                up = l.getLastUplift();
                upPaid = l.getLastUpliftPaid();
                b.ratio.add(l.getRatio(), ratio, at);
                double owedUplift = last ? s.face * (Math.max(1, ratio) - 1) : 0;
                b.coupon.add(cpn, s.face * ratio * l.getRealCoupon() / 12 + owedUplift, at);
                double[] known = new double[s.remaining];
                Arrays.fill(known, s.face * ratio * l.getRealCoupon() / 12);
                known[s.remaining - 1] += s.face * Math.max(1, ratio);
                b.catchUp.add(up, Debt.presentValue(known, s.eff) - s.carrying, at);
                b.realRate.add(l.getEffectiveRate(), s.eff, at);
                b.upliftPaid.add(upPaid, owedUplift, at);
                if (ratio == s.ratio) b.flat.add(up, 0, at);
            }
            double adjusted = s.carrying + up;
            if (!last) {
                b.expense.add(exp, adjusted * p.getEffectiveRate() / 12, at);
                b.carry.add(p.getCarryingValue(), adjusted + exp - cpn, at);
            } else {
                b.ended.add(p.getCarryingValue(), 0, at);
            }
            Life life = b.lives.get(p);
            if (life != null) {
                life.expense += exp;
                life.uplift += up;
                life.coupon += cpn;
            }
            sumExp += exp;
            sumCpn += cpn;
            sumUp += up;
            sumUpPaid += upPaid;
        }

        // The statements' lines and the debt manager's month are the pieces' sums.
        X_BOOKS.add(dm.getInterestBooked(), sumExp, "interest booked, " + event);
        X_BOOKS.add(na.getInterestExpense(), sumExp, "interest line, " + event);
        X_BOOKS.add(dm.getUpliftBooked(), sumUp, "uplift booked, " + event);
        X_BOOKS.add(na.getInflationUplift(), sumUp, "uplift line, " + event);
        X_BOOKS.add(dm.getUpliftPaid(), sumUpPaid, "uplift paid, " + event);
        X_BOOKS.add(na.getUpliftPaidInCash(), sumUpPaid, "uplift paid in cash, " + event);
        X_BOOKS.add(na.getUpliftNotCash(), sumUp - sumUpPaid, "uplift not cash, " + event);
        X_BOOKS.add(dm.getCouponBooked() + dm.getUpliftPaid(), sumCpn, "coupons booked, " + event);
        // The month's interest is the coupons and the discount wearing off - the uplift paid, which the coupons carry, is not the discount.
        X_BOOKS.add(dm.getAmortisedBooked(), sumExp - sumCpn + sumUpPaid, "amortised booked, " + event);
        X_BOOKS.add(na.getInterestExpense(), na.getInterestPaidInCash() + na.getInterestNotCash(), "interest = cash + not cash, " + event);
        X_TREASURY.add(na.getInterestPaidInCash() + na.getUpliftPaidInCash(), sumCpn, event);
        X_NOTCASH.add(city.getTreasuryNotCash(), sumExp - sumCpn + sumUp - na.getRetiredGain(), event);
        double journal = 0;
        for (TreasuryJournal.Entry e : city.getTreasuryJournal()) journal += e.amount();
        X_FOOTS.add(city.getTreasurySurplus() + city.getTreasuryNotCash() + city.getTreasuryRaised()
                - city.getTreasuryRepaid() + journal + city.getTreasuryResidual(), city.getTreasuryChange(), event);

        // The holders: each was paid its share of every coupon. A piece falling due is split by what each holder was
        // repaid of its principal (the coupon, the uplift in it, goes by the same shares); several falling due together
        // are not split, unless the bank alone held them and nobody else was repaid anything.
        double outsideRepaid = city.getPrincipalToHouseholds() + city.getPrincipalToFirms() + city.getPrincipalToWorld() + cbank.getPaperRedeemed();
        boolean known = maturing <= 1 || outsideRepaid <= 0;
        double hh = 0, fm = 0, wd = 0, cb = 0;
        if (known) {
            for (Snap s : before) {
                Debt p = s.piece;
                double cpn = p.getLastCoupon();
                double[] share;
                if (s.remaining == 1) {
                    share = outsideRepaid <= 0 ? new double[4]
                            : new double[] { city.getPrincipalToHouseholds() / s.face, city.getPrincipalToFirms() / s.face,
                                    city.getPrincipalToWorld() / s.face, cbank.getPaperRedeemed() / s.face };
                } else {
                    double owed = p.getOustandingPrincipal();
                    share = owed > 0
                            ? new double[] { p.householdPrincipal() / owed, firmsShare.getOrDefault(p, p.firmPrincipal() / owed),
                                    s.settled ? s.worldShare : p.worldPrincipal() / owed, p.centralBankPrincipal() / owed }
                            : new double[4];
                }
                hh += cpn * share[0];
                fm += cpn * share[1];
                wd += cpn * share[2];
                cb += cpn * share[3];
            }
            X_HOLDERS.add(city.getCouponsToHouseholds(), hh, "households, " + event);
            X_HOLDERS.add(city.getCouponsToFirms(), fm, "businesses, " + event);
            X_HOLDERS.add(city.getCouponsToWorld(), wd, "world, " + event);
            X_HOLDERS.add(cbank.getPaperCoupons(), cb, "central bank, " + event);
            double bank = sumCpn - hh - fm - wd - cb;
            if (!Double.isNaN(b.prevBank)) X_BRIDGE.add(city.getTreasuryResidual(), bank - b.prevBank, event);
            X_EARNED.add(city.getEarnedResidual(), -(hh + fm + wd + cb), event);
            b.prevBank = bank;
        } else {
            b.prevBank = Double.NaN;
        }
        return r;
    }

    /* =====================================================================
       THE PURE FIXTURES: the study's calm city, a hiking one, and a flat one
       ===================================================================== */

    /** A curve with the dial at 3% for ever and no shape: every zero rate 3%. */
    static YieldCurve flatCurve() {
        return YieldCurve.of(YieldCurve.flatPath(.03), 0, 1.0, m -> 0);
    }

    /** The study's calm city (spec section 3.3): the dial at 3.00%, the 1-year zero rate 3.12% - twelve points of credit spread - and the long end on CallCheck's row. */
    static YieldCurve studyCalm() {
        IntToDoubleFunction shape = m -> m <= 12 ? 0 : CallCheck.calmZero(m) - CallCheck.ROW_ZERO[0];
        return YieldCurve.of(YieldCurve.flatPath(.03), CallCheck.ROW_ZERO[0] - .03, 1.0, shape);
    }

    /** The dial a curve expects, month by month. */
    static double[] dialPath(YieldCurve c) {
        double[] path = new double[YieldCurve.HORIZON];
        for (int t = 0; t < path.length; t++) path[t] = c.path(t);
        return path;
    }

    /** What a floater pays per 100 on a dial path, as the spec says it: each month's coupon reads the dial one tick on, never under nothing, and the face comes with the last. */
    static double[] floaterFlows(double[] path, double margin, int months) {
        double[] f = new double[months];
        for (int i = 0; i < months; i++) f[i] = 100 * Math.max(0, path[Math.min(i + 1, path.length - 1)] + margin) / 12;
        f[months - 1] += 100;
        return f;
    }

    /** The payments' present value on a curve, summed here from the curve's factors. */
    static double pv(YieldCurve c, double[] flows) {
        double sum = 0;
        for (int i = 0; i < flows.length; i++) sum += flows[i] * c.factor(i + 1);
        return sum;
    }

    /** An index growing this much a year, month by month. */
    static double[] growthAt(double annual) {
        double[] g = new double[YieldCurve.HORIZON];
        Arrays.fill(g, Math.pow(1 + annual, 1.0 / 12));
        return g;
    }

    /** What a linker pays per 100 when its first payment is on a ratio of 1 and the index then grows as given; the last month adds the face, or the indexed face when that is more (the floor) or whatever it is (no floor). */
    static double[] linkerFlows(double real, double[] growth, int months, boolean floor) {
        double[] f = new double[months];
        double r = 1;
        for (int i = 0; i < months; i++) {
            if (i > 0) r *= growth[Math.min(i, growth.length - 1)];
            f[i] = 100 * r * real / 12;
            if (i == months - 1) f[i] += 100 * (floor ? Math.max(1, r) : r);
        }
        return f;
    }

    /** The real coupon that prices that linker at 100, found from two evaluations of the present value (it is linear in the coupon). */
    static double solveReal(YieldCurve c, double[] growth, int months, boolean floor) {
        double pv0 = pv(c, linkerFlows(0, growth, months, floor));
        double pv1 = pv(c, linkerFlows(1, growth, months, floor));
        return (100 - pv0) / (pv1 - pv0);
    }

    /* =====================================================================
       MAIN
       ===================================================================== */

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        long t0 = System.currentTimeMillis();

        if (wanted(args, 1)) fairAtPar();
        if (wanted(args, 2) || wanted(args, 3) || wanted(args, 4)) {
            theSale();
            if (wanted(args, 2)) neverUnasked();
            if (wanted(args, 3)) {
                followsTheDial();
                ran[3] = true;
            }
            if (wanted(args, 4)) {
                theUplift();
                ran[4] = true;
            }
        }
        if (wanted(args, 5)) {
            toMaturity();
            ran[5] = true;
        }
        if (wanted(args, 6) && (wanted(args, 3) || wanted(args, 4) || wanted(args, 5))) theMoneyCloses();
        if (wanted(args, 7)) theBuyback();
        if (wanted(args, 8)) theRollover();
        if (wanted(args, 9)) theSave();

        out.printf("%n(%d s)%n", (System.currentTimeMillis() - t0 + 500) / 1000);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /* =====================================================================
       THE PLAYED CITY
       ===================================================================== */

    /** The city BuybackCheck was written against, with the rollover off, the dial held by hand at 3% and the world given an appetite. */
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
            // ...and two depots standing (PT1-M4), as CallCheck's played city stands its own: the works department that
            // built for nobody is retired, and the founding yard alone raised this town to 256 people by month 62, whose
            // asks were too small for the fixed fee the near-par assertions read against.
            city.buildStack(template(city, "Construction Depot"), 2, true);
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

    /** A played city ready to sell: the world wants paper, cash to spare, nothing rolled over by itself, the dial where the harness holds it. */
    static Game readyCity(String label, boolean manualRollover, double centralBankShare) {
        Game city = playedCity(label);
        worldWantsPaper(city);
        city.setCashForTest(Founding.WEALTHY_CASH);
        if (manualRollover) city.setRolloverMode(Rollover.Mode.MANUAL);
        quietly(() -> city.getDebtManager().takeTheDial(.03));
        if (centralBankShare > 0) quietly(() -> city.getCentralBank().setTargetShare(centralBankShare));
        // The cash just set is no flow: the treasury bridge opens a month with last month's closing, so it takes a month
        // to forget the difference, and the months that follow are the ones the harness holds to the bridge.
        press(city, "a month for the bridge to forget the cash set");
        return city;
    }

    /** The piece the last call put on the books, or null. */
    static Debt newest(DebtManager dm, Set<Debt> before) {
        Debt found = null;
        for (Debt d : dm.getDebt()) if (!before.contains(d)) found = d;
        return found;
    }

    static Set<Debt> piecesOf(DebtManager dm) {
        return new HashSet<>(dm.getDebt());
    }

    /** What the households could take, to size a sale that clears at fair. */
    static double askOf(Game city, double share) {
        return Math.max(100, city.getHouseholdBalance().spareForPaper() * share);
    }

    /** A bank-held fixture floater, books opened at a sale-like price. */
    static FloatingRateNote fixtureFloater(Game city, double face, int months, double margin) {
        DebtManager dm = city.getDebtManager();
        FloatingRateNote f = (FloatingRateNote) dm.addFloatingRateNote(face, months, city.getMonth(), margin, dm.getPolicyRate());
        f.markIssued(face * .985, dm.getPolicyRate());
        f.settled();
        return f;
    }

    /** A bank-held fixture linker, books opened at a sale-like price. */
    static InflationLinkedBond fixtureLinker(Game city, double face, int months, double real, double base) {
        DebtManager dm = city.getDebtManager();
        InflationLinkedBond l = (InflationLinkedBond) dm.addLinkedBond(face, months, city.getMonth(), real, base);
        l.markIssued(face * .985, dm.getPolicyRate());
        l.settled();
        return l;
    }

    /* =====================================================================
       1. FAIR PRICES IT AT 100
       ===================================================================== */

    static void fairAtPar() {
        out.println("--- 1. fair prices it at 100: the margin and the real coupon that make the payments worth the face on the curve ---");

        YieldCurve[] curves = { CallCheck.calm(), CallCheck.hiking(), flatCurve() };
        String[] names = { "calm", "hiking", "flat" };
        double bp = .0001;
        for (int c = 0; c < curves.length; c++) {
            YieldCurve curve = curves[c];
            double[] path = dialPath(curve);
            for (int years : FloatingRateNote.MATURITIES) {
                int n = years * 12;
                String what = "the " + names[c] + " curve, a " + years + "-year floater";
                double margin = DebtManager.fairMargin(curve, path, n);
                double[] flows = floaterFlows(path, margin, n);
                near(what + ": at the fair margin (" + String.format("%+.1f bp", margin * 1e4) + ") its payments are worth 100", pv(curve, flows), 100, 1e-6);
                double less = pv(curve, floaterFlows(path, margin - bp, n)), more = pv(curve, floaterFlows(path, margin + bp, n));
                check("...a basis point less is worth under 100 (" + String.format("%.4f", less) + ") and a basis point more over (" + String.format("%.4f", more) + ")",
                        less < 100 - 1e-4 && more > 100 + 1e-4);
                FloatingRateNote fixture = new FloatingRateNote(1000, n, 0, margin, path[0]);
                double[] projected = fixture.projectedFlows(path);
                double worst = 0;
                for (int i = 0; i < n; i++) worst = Math.max(worst, Math.abs(projected[i] - flows[i] * 10));
                check("...and the piece's own projectedFlows() on that path are those payments, tenfold for a face of 1,000", worst <= 1e-9 && projected.length == n);
                check("...a path nobody gives is the known schedule: today's coupon flat and the face at the end",
                        Arrays.equals(fixture.projectedFlows(null), fixture.scheduleOwed()));
                if (names[c].equals("flat")) {
                    near("...on a flat dial and a curve with no shape the fair margin is nothing", margin, 0, 1e-9);
                }
            }
            for (double growth : new double[] { .02, 0, -.03 }) {
                double[] g = growthAt(growth);
                for (int years : InflationLinkedBond.MATURITIES) {
                    int n = years * 12;
                    String what = "the " + names[c] + " curve, a " + years + "-year linker with the index at " + String.format("%+.0f%%", growth * 100);
                    double real = DebtManager.fairRealCoupon(curve, g, n);
                    double[] flows = linkerFlows(real, g, n, true);
                    near(what + ": at the fair real coupon (" + String.format("%.3f%%", real * 100) + ") its payments are worth 100", pv(curve, flows), 100, 1e-6);
                    double less = pv(curve, linkerFlows(real - bp, g, n, true)), more = pv(curve, linkerFlows(real + bp, g, n, true));
                    check("...a basis point less is worth under 100 and a basis point more over", less < 100 - 1e-4 && more > 100 + 1e-4);
                    check("...and the piece's own flowsFor() are those payments", maxDiff(InflationLinkedBond.flowsFor(100, real, 1.0, g, n), flows) <= 1e-9);
                    InflationLinkedBond fixture = new InflationLinkedBond(1000, n, 0, real, 1.0);
                    check("...and so are its projectedFlows() from a ratio of 1, tenfold for a face of 1,000",
                            maxDiff(fixture.projectedFlows(1.0, g), Arrays.stream(flows).map(x -> x * 10).toArray()) <= 1e-9);
                    double noFloor = solveReal(curve, g, n, false);
                    if (growth == 0) {
                        near("...a ratio that never moves is a bullet: the real coupon is the curve's par yield", real, curve.parYield(n), 1e-9);
                    } else if (growth > 0) {
                        near("...the index rises, so the floor never bites: the coupon is the one without a floor", real, noFloor, 1e-9);
                    } else {
                        double lastRatio = Math.pow(1 + growth, (n - 1) / 12.0);
                        check("...the index falls, so the ratio at the end is under 1 (" + String.format("%.3f", lastRatio) + ")", lastRatio < 1);
                        double lastCoupon = 100 * lastRatio * real / 12;
                        near("...the floor binds: the last payment is the original face and the coupon on the indexed one",
                                flows[n - 1], 100 + lastCoupon, 1e-9);
                        check("...the floor is worth something to the lender, so the real coupon is under what it would be without it ("
                                + String.format("%.3f%% < %.3f%%", real * 100, noFloor * 100) + ")", real < noFloor);
                    }
                    if (growth == .02) {
                        double zero = DebtManager.fairRealCoupon(curve, growthAt(0), n);
                        check("...the index at 2% asks a lower real coupon than the index at 0% (" + String.format("%.3f%% < %.3f%%", real * 100, zero * 100) + ")", real < zero);
                    }
                }
            }
            if (names[c].equals("calm")) {
                // The study's calm city has the dial at 3.00% and a 1-year zero rate of 3.12%: the 12 points between are the credit
                // spread its notes carry, which the margin pays on top of the term premium. CallCheck's calm curve puts the dial at 3.12%.
                YieldCurve study = studyCalm();
                double[] studyPath = dialPath(study);
                out.println("   against the study's calm city (spec 2.4 and 2.5), the dial at 3.00% and its 12-point credit spread, the zero rates joined by straight lines:");
                for (int years : FloatingRateNote.MATURITIES) {
                    out.printf("   %d-year floater: policy %+.1f bp here (%+.1f bp with no spread), the study's %s%n", years,
                            DebtManager.fairMargin(study, studyPath, years * 12) * 1e4, DebtManager.fairMargin(curve, path, years * 12) * 1e4,
                            years == 2 ? "+18" : "+34");
                }
                for (int years : InflationLinkedBond.MATURITIES) {
                    out.printf("   %d-year linker: real %.2f%% here, the study's %s%n", years,
                            DebtManager.fairRealCoupon(study, growthAt(.02), years * 12) * 100, years == 10 ? "1.61%" : "2.17%");
                }
            }
        }
        // the terms
        check("a floater is issued at 2 and 5 years and at no other", FloatingRateNote.isIssuable(2) && FloatingRateNote.isIssuable(5)
                && !FloatingRateNote.isIssuable(3) && !FloatingRateNote.isIssuable(10) && !FloatingRateNote.isIssuable(1));
        check("a linker is issued at 10 and 30 years and at no other", InflationLinkedBond.isIssuable(10) && InflationLinkedBond.isIssuable(30)
                && !InflationLinkedBond.isIssuable(20) && !InflationLinkedBond.isIssuable(5) && !InflationLinkedBond.isIssuable(2));
    }

    static double maxDiff(double[] a, double[] b) {
        if (a.length != b.length) return Double.POSITIVE_INFINITY;
        double worst = 0;
        for (int i = 0; i < a.length; i++) worst = Math.max(worst, Math.abs(a[i] - b[i]));
        return worst;
    }

    /* =====================================================================
       2. A SALE
       ===================================================================== */

    /** The city sections 2-4 share, and the pieces sold in it. */
    static Game city;
    static final List<FloatingRateNote> floaters = new ArrayList<>();
    static final List<InflationLinkedBond> linkers = new ArrayList<>();
    static LongTermBond fixedTwoYear;

    /** The bank's share of the last month's coupons in the shared city, carried from section 3 to section 4 for the bridge's timing. */
    static double sPrevBank = 0;

    /** A tail the harness caps the sale at, to cause one: a sale far past the bids stops at the cap. */
    static final double CAP_TAIL = .0025;

    static final Worst SALE_MARGIN = new Worst(), SALE_INDEX = new Worst(), SALE_CASH = new Worst(), SALE_BOOKS = new Worst(),
            SALE_EFF = new Worst(), SALE_PRICE = new Worst(), SALE_RATIO = new Worst(), SALE_ROUND = new Worst();

    /** Sells a floater by its handler and holds the piece to its quote. */
    static FloatingRateNote sellFloater(int years, double ask) {
        DebtManager dm = city.getDebtManager();
        DebtQuote q = city.quoteFloatingNote(ask, years, 100);
        Set<Debt> before = piecesOf(dm);
        double cash0 = city.getCash();
        String said = quiet(() -> city.handleFloatingNoteLogic(ask, years, 100));
        Debt piece = newest(dm, before);
        check("a " + years + "-year floater sold by its handler is a new piece of type FLOAT, " + years * 12 + " months, its own line",
                piece instanceof FloatingRateNote && "FLOAT".equals(piece.getType()) && piece.getRemainingMonths() == years * 12
                        && dm.getDebt().size() == before.size() + 1 && said.startsWith("Floating-rate note issued"));
        if (!(piece instanceof FloatingRateNote f)) throw new IllegalStateException("no floater sold: " + said);
        String at = years + "-year floater";
        SALE_MARGIN.add(f.getMargin(), q.coupon(), at);
        SALE_MARGIN.add(f.getMargin(), q.auction().ask().fairCoupon() + q.auction().tail(), at);
        SALE_INDEX.add(f.getIndex(), dm.getPolicyRate(), at);
        SALE_INDEX.add(f.couponRate(), Math.max(0, dm.getPolicyRate() + f.getMargin()), at);
        SALE_CASH.add(city.getCash() - cash0, q.cashReceived(), at);
        SALE_BOOKS.add(f.getCarryingValue(), q.cashReceived(), at);
        SALE_BOOKS.add(f.getFaceValue(), q.faceValue(), at);
        SALE_EFF.add(f.getEffectiveRate(), f.couponRate(), at);
        SALE_PRICE.add(dm.priceOfPar(f), q.auction().pricePer100(), at);
        return f;
    }

    /** Sells a linker by its handler and holds the piece to its quote. */
    static InflationLinkedBond sellLinker(int years, double ask) {
        DebtManager dm = city.getDebtManager();
        DebtQuote q = city.quoteLinkedBond(ask, years, 100);
        Set<Debt> before = piecesOf(dm);
        double cash0 = city.getCash();
        double indexNow = dm.priceIndexLevel();
        String said = quiet(() -> city.handleLinkedBondLogic(ask, years, 100));
        Debt piece = newest(dm, before);
        check("a " + years + "-year linker sold by its handler is a new piece of type LINKED, " + years * 12 + " months, its own line",
                piece instanceof InflationLinkedBond && "LINKED".equals(piece.getType()) && piece.getRemainingMonths() == years * 12
                        && dm.getDebt().size() == before.size() + 1 && said.startsWith("Inflation-linked bond issued"));
        if (!(piece instanceof InflationLinkedBond l)) throw new IllegalStateException("no linker sold: " + said);
        String at = years + "-year linker";
        SALE_MARGIN.add(l.getRealCoupon(), q.coupon(), at);
        // ...the quote's fair real coupon plus its tail, rounded down to a step: a multiple of it, at most that, within a step of it.
        double through = q.auction().ask().fairCoupon() + q.auction().tail(), step = Auction.COUPON_STEP;
        SALE_ROUND.add(Math.max(0, l.getRealCoupon() - through - 1e-12) + Math.max(0, through - l.getRealCoupon() - step)
                + Math.abs(l.getRealCoupon() / step - Math.rint(l.getRealCoupon() / step)), 0, at);
        SALE_INDEX.add(l.getBaseIndex(), indexNow, at);
        SALE_RATIO.add(l.getRatio(), 1, at);
        SALE_CASH.add(city.getCash() - cash0, q.cashReceived(), at);
        SALE_BOOKS.add(l.getCarryingValue(), q.cashReceived(), at);
        SALE_BOOKS.add(l.getFaceValue(), q.faceValue(), at);
        SALE_PRICE.add(dm.priceOfPar(l), q.auction().pricePer100(), at);
        return l;
    }

    static void theSale() {
        out.println("\n--- 2. a sale: the margin is fair plus the tail, the real coupon fair plus the tail rounded down; the index is the dial, the base the price index ---");

        city = readyCity("floatlink", true, .1);
        DebtManager dm = city.getDebtManager();
        // WN3: the sales are asked at the households' whole spare, not a quarter of it. The fixed fee of an issue is a share of its face
        // that falls as the face rises, and the near-par assertions read against it (see playedCity()); a quarter of this town's spare is
        // 7,400 of face, and a 2-year floater's effective rate then stands 8.7 basis points over its coupon where 5 are allowed, and the bank's room alone
        // (1.98 million) covers any ask this size at fair.
        double ask = askOf(city, 1);
        // WN3: "far past the bids" is reckoned from the bidders, not from the households' savings alone: 60 times the households' spare
        // (1.77 million of face) was one bid in the world band's city, whose bank alone has room for 1.98 million, and the sale sold in full
        // at no tail. Sixty times everything the bidders could take, at the maturities the harness sells (AuctionCheck 8 reckons its huge
        // sale the same way).
        double bids = 0;
        for (int months : new int[] { 24, 60, 120, 360 }) {
            Auction.Bidders bd = city.auctionBidders(months);
            bids = Math.max(bids, bd.householdSpare() + bd.bankRoomFace() + bd.firmsIdle() + bd.worldStock() + bd.monthlyGdp() * 12);
        }
        double huge = 60 * bids;
        double step = Auction.COUPON_STEP;

        // The tail, caused: a sale far past the bids stops at the cap, short, and the tail is the cap.
        for (int years : FloatingRateNote.MATURITIES) {
            DebtQuote q = city.quoteFloatingNote(huge, years, 100, Auction.Cap.tail(CAP_TAIL));
            Auction.Result r = q.auction();
            String what = years + "-year floater, a sale far past the bids capped at " + String.format("%.2f", CAP_TAIL * 100) + " points";
            check(what + ": short, and stopped at the cap", r.shortSale() && Math.abs(r.tail() - CAP_TAIL) < 1e-9);
            near("...its margin is the fair margin plus the tail", q.coupon(), r.ask().fairCoupon() + r.tail(), 1e-12);
            near("...the fair margin is DebtManager.fairMargin() on the curve the issue stands on", r.ask().fairCoupon(), dm.fairMargin(years * 12, r.ask().face()), 1e-9);
            YieldCurve curve = dm.quoteCurve(r.ask().face());
            near("...and prices a floater at 100 on that curve and the dial the market expects",
                    pv(curve, floaterFlows(dm.expectedPolicyPath(), r.ask().fairCoupon(), years * 12)), 100, 1e-6);
        }
        for (int years : InflationLinkedBond.MATURITIES) {
            DebtQuote q = city.quoteLinkedBond(huge, years, 100, Auction.Cap.tail(CAP_TAIL));
            Auction.Result r = q.auction();
            String what = years + "-year linker, a sale far past the bids capped at " + String.format("%.2f", CAP_TAIL * 100) + " points";
            double fair = r.ask().fairCoupon(), through = fair + r.tail();
            check(what + ": short, and stopped at the cap", r.shortSale() && Math.abs(r.tail() - CAP_TAIL) < 1e-9);
            check("...its real coupon is the fair one plus the tail rounded down to Auction.COUPON_STEP: a multiple of it, at most that, within one step of it ("
                            + String.format("%.4f%% from %.4f%%", q.coupon() * 100, through * 100) + ")",
                    Math.abs(q.coupon() / step - Math.rint(q.coupon() / step)) < 1e-9 && q.coupon() <= through + 1e-12 && through - q.coupon() < step);
            near("...the fair real coupon is DebtManager.fairRealCoupon() on the curve the issue stands on", fair, dm.fairRealCoupon(years * 12, r.ask().face()), 1e-9);
            YieldCurve curve = dm.quoteCurve(r.ask().face());
            near("...and prices a new linker at 100 on that curve and the index the market expects",
                    pv(curve, linkerFlows(fair, dm.expectedInflationGrowth(), years * 12, true)), 100, 1e-6);
        }
        // ...and a sale the bids cover clears at fair: no tail, the real coupon within a step under fair.
        DebtQuote covered = city.quoteLinkedBond(ask, 10, 100);
        check("a sale the bids cover clears at fair, with no tail, and its real coupon is under fair by less than a step ("
                        + String.format("%.4f%% under %.4f%%", covered.coupon() * 100, covered.auction().ask().fairCoupon() * 100) + ")",
                !covered.auction().shortSale() && covered.auction().tail() == 0 && covered.coupon() <= covered.auction().ask().fairCoupon() + 1e-12
                        && covered.auction().ask().fairCoupon() - covered.coupon() < step);

        // The quotes in words, and for the cash a sale must bring (the rollover sizes its issues by it).
        check("a quote says what the paper is: the floater's coupon as the policy rate plus a margin, the linker's as a real coupon",
                city.quoteFloatingNote(ask, 2, 100).summary().contains("policy rate") && covered.summary().contains("Real coupon"));
        double cashWanted = 50_000;
        DebtQuote forFloater = city.quoteFloatingNoteForCash(cashWanted, 2, 100), forLinker = city.quoteLinkedBondForCash(cashWanted, 10, 100);
        check("a floater quoted for the cash it must bring covers it, by less than a lot of 100 (" + String.format("%,.2f", forFloater.cashReceived()) + " for 50,000)",
                forFloater.cashReceived() >= cashWanted - 1e-6 && forFloater.cashReceived() - cashWanted < 100 + 1e-6 && "Float".equals(forFloater.instrument()));
        check("...and so does a linker (" + String.format("%,.2f", forLinker.cashReceived()) + ")",
                forLinker.cashReceived() >= cashWanted - 1e-6 && forLinker.cashReceived() - cashWanted < 100 + 1e-6 && "Linked".equals(forLinker.instrument()));

        // What cannot be sold: refused with the instrument's own words, and nothing moves.
        double[] was = { city.getCash(), dm.getDebt().size(), dm.getAllPrincipal() };
        String f3 = quiet(() -> city.handleFloatingNoteLogic(ask, 3, 100));
        String f10 = quiet(() -> city.handleFloatingNoteLogic(ask, 10, 100));
        String l20 = quiet(() -> city.handleLinkedBondLogic(ask, 20, 100));
        String l5 = quiet(() -> city.handleLinkedBondLogic(ask, 5, 100));
        String d1 = quiet(() -> city.handleForeignLogic("Float", 100, 2, 100, false));
        String d2 = quiet(() -> city.handleForeignLogic("Linked", 100, 10, 100, false));
        check("a 3-year floater and a 10-year one are refused in the instrument's words",
                FloatingRateNote.REFUSAL.equals(f3) && FloatingRateNote.REFUSAL.equals(f10));
        check("a 20-year linker and a 5-year one are refused in the instrument's words",
                InflationLinkedBond.REFUSAL.equals(l20) && InflationLinkedBond.REFUSAL.equals(l5));
        check("a dollar floater and a dollar linker are refused: they are written at home only",
                Game.NO_DOLLAR_FLOAT_OR_LINKED.equals(d1) && Game.NO_DOLLAR_FLOAT_OR_LINKED.equals(d2)
                        && city.quoteForeign("Float", 100, 2, 100).isEmpty() && city.quoteForeign("Linked", 100, 10, 100).isEmpty()
                        && city.quoteForeignForCash("Float", 1000, 2, 100).isEmpty() && city.quoteForeignForCash("Linked", 1000, 10, 100).isEmpty());
        check("...and the quotes for those terms are empty", city.quoteFloatingNote(ask, 3, 100).isEmpty() && city.quoteLinkedBond(ask, 20, 100).isEmpty());
        check("...and none of the refusals moved the cash, the pieces or the principal",
                city.getCash() == was[0] && dm.getDebt().size() == (int) was[1] && dm.getAllPrincipal() == was[2]);

        // The sales: one of each term, and a second of the first term, which opens a second line.
        for (int years : FloatingRateNote.MATURITIES) floaters.add(sellFloater(years, ask));
        for (int years : InflationLinkedBond.MATURITIES) linkers.add(sellLinker(years, ask));
        int floaterLines = floaters.size(), linkerLines = linkers.size();
        double firstFloaterFace = floaters.get(0).getFaceValue(), firstLinkerFace = linkers.get(0).getFaceValue();
        FloatingRateNote again = sellFloater(FloatingRateNote.MATURITIES[0], ask);
        floaters.add(again);
        check("a second sale of the same term opens a second line: two floaters of " + FloatingRateNote.MATURITIES[0] + " years, and the first one not reopened",
                again != floaters.get(0) && countOf(dm, FloatingRateNote.class) == floaterLines + 1
                        && floaters.get(0).getFaceValue() == firstFloaterFace && again.getFaceValue() > 0);
        InflationLinkedBond linkAgain = sellLinker(InflationLinkedBond.MATURITIES[0], ask);
        linkers.add(linkAgain);
        check("...and the same for a linker: two of " + InflationLinkedBond.MATURITIES[0] + " years, the first at its face",
                linkAgain != linkers.get(0) && countOf(dm, InflationLinkedBond.class) == linkerLines + 1
                        && linkers.get(0).getFaceValue() == firstLinkerFace && linkAgain.getFaceValue() > 0);
        SALE_MARGIN.report("every sale: the margin or real coupon is the quote's, the quote's fair plus its tail", 1e-12);
        SALE_ROUND.report("...a linker's real coupon is that fair plus the tail rounded down to Auction.COUPON_STEP: a multiple of it, within a step under", 1e-9);
        SALE_INDEX.report("...a floater's index is the dial it was sold at and the linker's base the price index", 1e-12);
        SALE_RATIO.report("...a linker is sold at a ratio of 1", 0);
        SALE_CASH.report("...the treasury received what the quote said", 1e-6);
        SALE_BOOKS.report("...the books opened with what it raised, on the face it sold", 1e-6);
        SALE_EFF.report("...a floater's effective rate is its coupon's, plus the fee's share (under 5 basis points)", 5e-4);
        SALE_PRICE.report("...a sale with no tail is worth what the buyback values it at (per 100)", 5e-3);
        for (FloatingRateNote f : floaters) {
            out.printf("   %d-year floater: margin %+.3f%%, coupon %.3f%% at the dial %.2f%%, sold at %.4f%% of par%n",
                    f.getDuration() / 12, f.getMargin() * 100, f.couponRate() * 100, f.getIndex() * 100, f.getCarryingValue() / f.getFaceValue() * 100);
        }
        for (InflationLinkedBond l : linkers) {
            out.printf("   %d-year linker: real %.3f%%, base %.5f, sold at %.4f%% of par%n",
                    l.getDuration() / 12, l.getRealCoupon() * 100, l.getBaseIndex(), l.getCarryingValue() / l.getFaceValue() * 100);
        }
    }

    static int countOf(DebtManager dm, Class<? extends Debt> type) {
        int n = 0;
        for (Debt d : dm.getDebt()) if (type.isInstance(d)) n++;
        return n;
    }

    /** A default city, a bill and a bond sold and played for years: not one floater or linker, though the rollover has run. */
    static void neverUnasked() {
        Game plain = playedCity("floatlink-default");
        worldWantsPaper(plain);
        plain.setCashForTest(Founding.WEALTHY_CASH);
        plain.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);   // a new game's setting: each piece rolls into its own kind
        DebtManager dm = plain.getDebtManager();
        double ask = askOf(plain, .25);
        quietly(() -> {
            plain.handleTBillLogic(ask, 12, 100);
            plain.handleLongBondLogic(ask, 5, 100);
        });
        int pieces = 0, floatsOrLinks = 0, rolledBills = 0;
        Set<Debt> seen = new HashSet<>(dm.getDebt());
        for (int m = 0; m < 30; m++) {
            // With no cash in the till at the month the bill falls due, the rollover has to sell something to pay it.
            if (m == 11 || m == 12) plain.setCashForTest(0);
            press(plain, "a default city");
            for (Debt d : dm.getDebt()) {
                pieces++;
                if (d instanceof FloatingRateNote || d instanceof InflationLinkedBond) floatsOrLinks++;
                if (seen.add(d) && d instanceof ShortTermTBill) rolledBills++;
            }
        }
        check("fixture: the default city (the rollover on, each piece into its own kind) held paper all the while: " + pieces + " piece-months, "
                + rolledBills + " bills rolled in after the first", pieces >= 30 && rolledBills >= 1);
        check("...and not one piece in it was a floating-rate note or an inflation-linked bond: " + floatsOrLinks + " in 30 months", floatsOrLinks == 0);
    }

    /* =====================================================================
       3. THE FLOATER'S COUPON FOLLOWS THE DIAL
       ===================================================================== */

    static void followsTheDial() {
        out.println("\n--- 3. the floater's coupon follows the dial: face x (dial + margin) / 12, the rate reset with it, the books holding ---");

        DebtManager dm = city.getDebtManager();
        double ask = askOf(city, .25);
        List<Double> facesSold = new ArrayList<>();
        for (Debt d : dm.getDebt()) facesSold.add(d.getFaceValue());
        int piecesSold = dm.getDebt().size();
        quietly(() -> city.handleLongBondLogic(ask, 2, 100));
        for (Debt d : dm.getDebt()) if (d instanceof LongTermBond b && b.getDuration() == 24 && !b.isCallable()) fixedTwoYear = b;
        check("fixture: a fixed 2-year bond is on the books beside the floaters, for the comparison", fixedTwoYear != null);
        boolean nobodyReopened = dm.getDebt().size() == piecesSold + 1;
        for (int i = 0; i < piecesSold; i++) nobodyReopened &= dm.getDebt().get(i).getFaceValue() == facesSold.get(i);
        check("...it opened a line of its own: no floater and no linker took any of its face (a bond's benchmark line is a bond's)", nobodyReopened);

        Books b = new Books();
        b.prevBank = 0;
        press(city, b, "sale month: the settle");
        int pieces = dm.getDebt().size();
        press(city, b, "dial unmoved");
        double[] dials = { .045, 0, .005, .02, .03 };
        String[] events = { "dial moved up", "dial at nothing", "dial moved to half a point", "dial moved to two points", "dial back to three" };
        for (int i = 0; i < dials.length; i++) {
            double was = dm.priceOfPar(floaters.get(0)), wasFixed = dm.priceOfPar(fixedTwoYear);
            double to = dials[i];
            quietly(() -> dm.takeTheDial(to));
            if (i == 0) {
                double nowFloat = dm.priceOfPar(floaters.get(0)), nowFixed = dm.priceOfPar(fixedTwoYear);
                out.printf("   the dial from %.2f%% to %.2f%%: the 2-year floater's value %.4f -> %.4f per 100, the fixed 2-year bond's %.4f -> %.4f%n",
                        3.0, to * 100, was, nowFloat, wasFixed, nowFixed);
                // The floater is not quite deaf to the dial: its coupon in a month reads the dial of that tick while the curve discounts
                // the month on the dial before it, so a path that falls back costs it about the dial's expected change over its life, a twelfth.
                check("a move of the dial takes a fixed bond of the same term down by more than half a point (" + String.format("%.2f", wasFixed - nowFixed) + ")", wasFixed - nowFixed > .5);
                check("...and leaves the floater near par: within a quarter of a point of 100 (" + String.format("%.3f", nowFloat) + ")", Math.abs(nowFloat - 100) < .25);
                check("...so the floater moves under a quarter as much as the fixed bond (" + String.format("%.3f against %.3f", Math.abs(nowFloat - was), wasFixed - nowFixed) + ")",
                        Math.abs(nowFloat - was) * 4 < wasFixed - nowFixed);
            }
            press(city, b, events[i]);
        }
        check("fixture: the dial script moved the dial five times and the floaters reset on it", b.moves >= 5 * floaters.size());
        b.coupon.report("every month, every floater's coupon is its face x (the dial + its margin, never under nothing) / 12", 1e-6);
        b.index.report("...and the index it reads is the dial of the month", 1e-12);
        b.unmoved.report("a month the dial did not move leaves the effective rate where it was, to the bit", 0);
        b.reset.report("a month it moved re-solves the effective rate on the known schedule (today's coupon flat, the face) at the carrying value", 1e-9);
        b.expense.report("every piece's interest expense is its carrying value x its effective rate / 12", 1e-6);
        b.carry.report("...and the carrying value is the last one plus the expense less the coupon", 1e-6);
        check("fixture: the floaters were among the pieces pressed, and the debt book did not change under them", pieces == dm.getDebt().size());

        // The effective rate follows the coupon: after the last move it sits within a few basis points of it.
        for (FloatingRateNote f : floaters) {
            check(f.getDuration() / 12 + "-year floater: the effective rate " + String.format("%.4f%%", f.getEffectiveRate() * 100)
                            + " is within 5 basis points of the coupon " + String.format("%.4f%%", f.couponRate() * 100) + " and its fee's share",
                    Math.abs(f.getEffectiveRate() - f.couponRate()) < 5e-4);
        }
        out.printf("   the books held with the dial's moves (%d piece-months), the holders as the central bank's dial left them:%n", b.expense.n);
        for (FloatingRateNote f : floaters) {
            out.printf("   %d-year floater of D$%,.0f: households %,.0f, bank %,.0f, businesses %,.0f, world %,.0f, central bank %,.0f%n",
                    f.getDuration() / 12, f.getFaceValue(), f.householdPrincipal(), f.bankPrincipal(), f.firmPrincipal(), f.worldPrincipal(), f.centralBankPrincipal());
        }
        sPrevBank = b.prevBank;
        aMarginUnderNothing();
    }

    /** The coupon is never under nothing: a floater with a margin below it, on a dial at nothing. */
    static void aMarginUnderNothing() {
        FloatingRateNote u = new FloatingRateNote(1000, 24, 0, .005, .03);
        near("a floater of 1,000 at a margin of +0.5 point on a dial at 3% has a coupon rate of 3.5% and owes 1000 x 3.5% / 12 a month", u.couponRate() * 1000 / 12, 1000 * .035 / 12, 1e-12);
        check("...its known schedule is that coupon every month and the face in the last",
                u.scheduleOwed().length == 24 && Math.abs(u.scheduleOwed()[0] - 1000 * .035 / 12) < 1e-12 && Math.abs(u.scheduleOwed()[23] - (1000 * .035 / 12 + 1000)) < 1e-12);
        u.reset(Double.NaN);
        check("...a dial that is no number leaves it where it was", u.getIndex() == .03);
        u.reset(0);
        near("...a dial at nothing takes the margin alone", u.couponRate(), .005, 1e-15);
        FloatingRateNote under = new FloatingRateNote(1000, 24, 0, -.01, .03);
        near("a margin of -1 point on a dial at 3% leaves 2%", under.couponRate(), .02, 1e-15);
        under.reset(.005);
        near("...on a dial at half a point it leaves nothing, never under", under.couponRate(), 0, 0);
        near("...and owes no coupon (the face alone in the last month)", under.scheduleOwed()[0], 0, 0);

        runFixtures();
        near("a floater built through addFloatingRateNote() with a margin of -1 point pays no coupon in the month the dial is at nothing", fxNegAtNothing, 0, 0);
        near("...nor in the month it is at half a point (the coupon is never under nothing)", fxNegAtHalf, 0, 0);
        near("...and with the dial at two points it pays the point that is left: 10,000 x 1% / 12", fxNegAtTwo, 10_000 * (.02 - .01) / 12, 1e-9);
        check("...it is a floater whose margin is under nothing, and the books carried it all the while", fxNeg.getMargin() < 0 && fxNeg.getRemainingMonths() == 12);
    }

    /* =====================================================================
       4. THE LINKER'S UPLIFT
       ===================================================================== */

    static void theUplift() {
        out.println("\n--- 4. the linker's uplift: the ratio follows the index, the carrying value catches up, the statements and the bridge carry it ---");

        anIndexThatIsNoNumber();
        DebtManager dm = city.getDebtManager();
        NationalAccounts na = city.getEconomyManager().getNationalAccounts();
        Books b = new Books();
        b.prevBank = sPrevBank;
        double[] growth = { 1.01, 1.01, 1.01, 1.01, 1.01, 1.00, 0.98, 0.98, 0.98, 1.015, 1.015 };
        Index idx = new Index(dm.priceIndexLevel(), growth);
        dm.setPriceIndexSource(idx);
        quietly(() -> dm.takeTheDial(.03));
        double upliftTotal = 0;
        int deflationMonths = 0, deflationNegative = 0;
        double[] ratios = new double[linkers.size()];
        press(city, b, "dial back to three (the index handed over)");
        for (int k = 1; k <= growth.length; k++) {
            idx.k = k;
            String event = growth[k - 1] > 1 ? "indexation: the index rises" : growth[k - 1] == 1 ? "indexation: the index did not move" : "deflation month: the index falls";
            press(city, b, event);
            double booked = 0;
            for (int i = 0; i < linkers.size(); i++) {
                InflationLinkedBond l = linkers.get(i);
                booked += l.getLastUplift();
                ratios[i] = l.getRatio();
                if (growth[k - 1] < 1) {
                    deflationMonths++;
                    if (l.getLastUplift() < 0) deflationNegative++;
                }
            }
            upliftTotal += booked;
            if (growth[k - 1] == 1) {
                near("a month the index did not move books no uplift at all", dm.getUpliftBooked(), 0, 0);
                near("...and the statements' line says nothing", na.getInflationUplift(), 0, 0);
            }
        }
        b.ratio.report("every month, every linker's ratio is the price index over the index it was sold at", 1e-12);
        b.catchUp.report("...each month's uplift is the carrying value's catch-up: the present value of the known schedule at the unchanged real rate, less what it was", 1e-6);
        b.realRate.report("...the real effective rate stays what the sale made it", 0);
        b.coupon.report("...the coupon is the face x the ratio x the real coupon / 12", 1e-6);
        b.flat.report("...a month the ratio did not move books nothing", 1e-9);
        b.upliftPaid.report("...nothing of the uplift is paid in cash before the last month", 1e-12);
        b.expense.report("every piece's interest expense is its (caught-up) carrying value x its effective rate / 12", 1e-6);
        b.carry.report("...and the carrying value is the caught-up one plus the expense less the coupon", 1e-6);
        check("the index falling books a negative uplift on every linker, in each of its " + deflationMonths / linkers.size() + " months",
                deflationMonths > 0 && deflationNegative == deflationMonths);
        check("...and rising books a positive one: the total booked over the " + growth.length + " months is " + String.format("%,.2f", upliftTotal) + " and the linkers' ratios ended at "
                + Arrays.toString(Arrays.stream(ratios).mapToObj(r -> String.format("%.4f", r)).toArray()), upliftTotal > 0);
        X_BOOKS.report("the debt manager's month and the statements' lines (interest, uplift booked and paid, coupons) are the pieces' sums, every pressed month of 3-4", 1e-6);

        // The statements: the uplift is an expense, in the totals.
        NationalAccounts fresh = new NationalAccounts();
        double expenses0 = fresh.getTotalExpenses(), balance0 = fresh.getBalance();
        fresh.setInflationUplift(500, 200);
        near("the statements' total expenses carry the uplift (500 set on a fresh account)", fresh.getTotalExpenses() - expenses0, 500, 1e-9);
        near("...so the month's result falls by it", balance0 - fresh.getBalance(), 500, 1e-9);
        near("...of which 200 was paid in cash and 300 is not", fresh.getUpliftNotCash(), 300, 1e-9);

        // The bridge: the not-cash row carries the uplift not yet paid, and what the residual leaves is the bank's coupon timing.
        X_NOTCASH.report("the treasury bridge's not-cash row is the expense the cash never paid: the discount amortised and the uplift not yet paid, less the gain on paper retired", 1e-6);
        X_FOOTS.report("...the bridge foots: change = surplus + not-cash + raised - repaid + journal + residual", 1e-6);
        X_BRIDGE.report("...and its residual is the bank's coupon timing, to the cent: the uplift is not in it", 1e-2);
        X_EARNED.report("the walk from EARNED to the budget leaves what EARNED never counted - the coupons paid beyond the bank - and none of the uplift", 1e-3);
        boolean walked = false;
        double row = Double.NaN;
        for (TreasuryJournal.Entry e : city.getEarnedToBudget()) {
            if (e.label().equals("Inflation uplift on linked bonds, not yet paid")) {
                walked = true;
                row = e.amount();
            }
        }
        check("the walk from EARNED to the budget has a row for the inflation uplift on linked bonds, not yet paid", walked);
        near("...and it takes the uplift not yet paid off the walk: minus the month's uplift not in cash (" + String.format("%,.2f", na.getUpliftNotCash()) + ")", row, -na.getUpliftNotCash(), 1e-9);

        // What the market prices the city on is all it owes: the face, and the indexation the linkers have accrued past it.
        double owed = 0;
        for (InflationLinkedBond l : linkers) owed += l.getFaceValue() * Math.max(0, l.getRatio() - 1);
        near("the indexation the linkers owe past their face is each face x how far its ratio is over 1 (" + String.format("%,.2f", owed) + ")", dm.getIndexationOwed(), owed, 1e-6);
        check("...and it is something, and part of the debt the curve is priced on", owed > 0 && dm.getPricedDebt() >= dm.getAllPrincipal() + dm.getIndexationOwed() - 1e-6);
    }

    /** A linker on its own, no city: an index that is no number books nothing, the floor is the face, the ratio is the level over the base. */
    static void anIndexThatIsNoNumber() {
        InflationLinkedBond u = new InflationLinkedBond(1000, 12, 0, .02, 1.0);
        boolean quietBad = true;
        for (double bad : new double[] { Double.NaN, 0, -1, Double.POSITIVE_INFINITY }) quietBad &= u.index(bad) == 0 && u.getRatio() == 1;
        check("a price index that is no number, or not above nothing, books nothing and leaves the ratio at 1", quietBad);
        near("a linker handed a base that is no price level is taken at a base of 1", new InflationLinkedBond(1000, 12, 0, .02, 0).getBaseIndex(), 1, 0);
        near("the next ratio at a level that is no number is the ratio as it stands", u.ratioAt(Double.NaN), 1, 0);
        double up = u.index(1.1);
        check("the index at 1.1 over a base of 1 books an uplift (" + String.format("%.2f", up) + ") and a ratio of 1.1", up > 0 && Math.abs(u.getRatio() - 1.1) < 1e-15);
        near("...and it owes at maturity the indexed face: 1,100", u.owedAtMaturity(), 1100, 1e-9);
        near("...its indexed principal is the same", u.getIndexedPrincipal(), 1100, 1e-9);
        double down = u.index(.9);
        check("the index back at 0.9 books a negative uplift (" + String.format("%.2f", down) + ")", down < 0);
        near("...and it owes at maturity the face, never under: the floor", u.owedAtMaturity(), 1000, 1e-9);
        near("...though its indexed principal is 900", u.getIndexedPrincipal(), 900, 1e-9);
        near("...and its known schedule ends on the face, with the coupon on the 900: the last payment is 1,000 + 1,000 x 0.9 x 2% / 12", u.scheduleOwed()[11], 1000 + 1000 * .9 * .02 / 12, 1e-9);
    }

    /* =====================================================================
       5. TO MATURITY
       ===================================================================== */

    /**
     * A linker sold for real and shortened to a year after it has settled, so the households, the bank
     * and (once its dial has bought in) the central bank hold it: its books are reopened at the price it
     * sold at on the shorter schedule, and what it raised is returned.
     */
    static double shorten(Game g, InflationLinkedBond l, int months) {
        l.remainingMonths = months;
        l.duration = months;
        l.openBooks(l.getCarryingValue());
        return l.getCarryingValue();
    }

    static void report(String name, Books b) {
        b.ended.report(name + ": every piece repaid is carried at nothing in the month it is", 1e-9);
        for (Life life : b.lives.values()) {
            Debt p = life.piece;
            if (p.getRemainingMonths() > 0) continue;   // not yet repaid: its life is not over
            near(name + ": " + p.getType() + " " + p.getDuration() + "m: interest expense + uplift booked = cash coupons + face - raised ("
                            + String.format("%,.2f + %,.2f = %,.2f + %,.2f - %,.2f", life.expense, life.uplift, life.coupon, p.getFaceValue(), life.raised) + ")",
                    life.expense + life.uplift, life.coupon + p.getFaceValue() - life.raised, 1e-6);
            check("...and it ends carried at nothing", Math.abs(p.getCarryingValue()) <= 1e-9);
        }
    }

    static final Worst M_SHARES = new Worst();
    static final Worst FX_ZERO = new Worst();

    /* ----- the bank-held fixtures: one city, run once, read by 3 (the margin under nothing) and 5 (the floor) ----- */

    static Game fxCity;
    static FloatingRateNote fxNeg, fxPos;
    static InflationLinkedBond fxUp, fxDown;
    static Books fxBooks;
    static double fxNegAtNothing, fxNegAtHalf, fxNegAtTwo, fxRepaid, fxUpPaid, fxDownPaid, fxUpRatio, fxDownRatio, fxUpFace = 20_000;
    static double fxOwed, fxOwedWant, fxPricedGap, fxDownMid;

    /**
     * Four fixtures built through DebtManager.addFloatingRateNote() and addLinkedBond() and opened at a price a sale
     * would have made, the bank holding all of them, in a played city with a price index the harness drives: a floater
     * with a margin of -1 point (24 months), another of +0.5 (12), a linker whose base is the index (12) and its twin
     * whose base is half as much again, so that its ratio stays under 1. Twelve presses with the dial moved about
     * (through nothing and half a point and two) and the index rising a point a month.
     */
    static void runFixtures() {
        if (fxCity != null) return;
        fxCity = readyCity("floatlink-fixtures", true, 0);
        Game fx = fxCity;
        DebtManager dm = fx.getDebtManager();
        double l0 = dm.priceIndexLevel();
        double[] rising = new double[14];
        Arrays.fill(rising, 1.01);
        Index idx = new Index(l0, rising);
        dm.setPriceIndexSource(idx);
        fxNeg = fixtureFloater(fx, 10_000, 24, -.01);
        fxPos = fixtureFloater(fx, 10_000, 12, .005);
        fxUp = fixtureLinker(fx, fxUpFace, 12, .02, l0);
        fxDown = fixtureLinker(fx, 20_000, 12, .02, l0 * 1.5);
        fxBooks = new Books();
        for (Debt d : new Debt[] { fxNeg, fxPos, fxUp, fxDown }) fxBooks.follow(d, d.getCarryingValue());
        double[] dials = { .03, .03, .03, 0, .005, .02, .02, .045, .045, .03, .03, .03, .03 };
        for (int k = 1; k <= 12; k++) {
            idx.k = k;
            double to = dials[k - 1];
            if (to != dm.getPolicyRate()) quietly(() -> dm.takeTheDial(to));
            String event = k == 12 ? "maturity: a linker repays the indexed face, another the original (the floor), a floater its face"
                    : k == 4 ? "dial at nothing: the negative margin pays nothing" : "bank-held fixtures, month " + k;
            press(fx, fxBooks, event);
            FX_ZERO.add(fx.getEarnedResidual(), 0, event);
            if (k == 4) fxNegAtNothing = fxNeg.getLastCoupon();
            if (k == 5) fxNegAtHalf = fxNeg.getLastCoupon();
            if (k == 6) fxNegAtTwo = fxNeg.getLastCoupon();
            if (k == 8) {
                // What the market prices the city on is all it owes: the linker's indexation too - and not the twin's, whose face is owed whole.
                fxOwed = dm.getIndexationOwed();
                fxOwedWant = fxUpFace * (fxUp.getRatio() - 1);
                fxPricedGap = dm.getPricedDebt() - dm.getAllPrincipal() - Math.max(0, fx.getCash() < 0 ? -fx.getCash() : 0)
                        - fx.getCentralBank().getAdvancesToTreasury() - fxOwed;
                fxDownMid = fxDown.getRatio();
            }
            if (k == 12) {
                fxRepaid = fx.getTreasuryRepaid();
                fxUpPaid = fxUp.getLastUpliftPaid();
                fxDownPaid = fxDown.getLastUpliftPaid();
                fxUpRatio = fxUp.getRatio();
                fxDownRatio = fxDown.getRatio();
            }
        }
    }

    static void toMaturity() {
        out.println("\n--- 5. to maturity: the indexed face under inflation, the original face under deflation, the books closing to the cent ---");

        // --- bank-held fixtures: the literal walk, the floor ---
        runFixtures();
        DebtManager dm = fxCity.getDebtManager();
        NationalAccounts na = fxCity.getEconomyManager().getNationalAccounts();
        Books fb = fxBooks;
        fb.coupon.report("every month, the fixture floaters' and linkers' coupons are what their terms say", 1e-6);
        fb.unmoved.report("...a dial that did not move left the floaters' effective rates where they were, to the bit", 0);
        fb.reset.report("...one that moved re-solved them on the known schedule", 1e-9);
        fb.catchUp.report("...the linkers' uplifts are their carrying values' catch-ups", 1e-6);
        fb.carry.report("...the carrying values roll by the expense and the coupon", 1e-6);
        check("fixture: the up-twin's ratio at the end is above 1 (" + String.format("%.4f", fxUpRatio) + ") and the down-twin's under 1 (" + String.format("%.4f", fxDownRatio) + ")",
                fxUpRatio > 1 && fxDownRatio < 1);
        near("the linker under a rising index paid, in the coupon of its last month, the indexed face less the face: 20,000 x (ratio - 1)", fxUpPaid, fxUpFace * (fxUpRatio - 1), 1e-6);
        near("the twin under a ratio of less than 1 paid no uplift: the floor", fxDownPaid, 0, 0);
        near("...and the bridge's repaid row of that month is the three faces, the indexed one's included at its original face",
                fxRepaid, 10_000 + 20_000 + 20_000, 1e-6);
        near("the statements' uplift paid in cash that month is the up-twin's alone", na.getUpliftPaidInCash(), fxUpPaid, 1e-9);
        check("the up-twin ended carried at nothing and its face was repaid whole: all three pieces are off the books",
                Math.abs(fxUp.getCarryingValue()) <= 1e-9 && !dm.getDebt().contains(fxUp) && !dm.getDebt().contains(fxDown) && !dm.getDebt().contains(fxPos));
        near("mid-life, with the twin's ratio at " + String.format("%.3f", fxDownMid) + ", what the linkers owe past their face is the up-twin's indexation alone: the twin's floor owes nothing more", fxOwed, fxOwedWant, 1e-6);
        check("...the twin's ratio was under 1 then and the up-twin's indexation was something (" + String.format("%,.0f", fxOwed) + ")", fxDownMid < 1 && fxOwed > 0);
        near("...and the debt the market prices the city on is its principal and that indexation", fxPricedGap, 0, 1e-6);
        FX_ZERO.report("the walk from EARNED to the budget leaves nothing in any month - the uplift months, the margin under nothing, the maturity - when the bank holds the paper", 1e-3);
        report("fixtures", fb);

        // --- a real linker under a rising index, a real floater to maturity with the dial moved twice ---
        Game rg = readyCity("floatlink-rising", true, .1);
        DebtManager rdm = rg.getDebtManager();
        NationalAccounts rna = rg.getEconomyManager().getNationalAccounts();
        double[] rise = new double[13];
        Arrays.fill(rise, 1.01);
        Index rIdx = new Index(rdm.priceIndexLevel(), rise);
        rdm.setPriceIndexSource(rIdx);
        double rask = askOf(rg, .25);
        Books rb = new Books();
        Set<Debt> none = piecesOf(rdm);
        quietly(() -> rg.handleFloatingNoteLogic(rask, 2, 100));
        FloatingRateNote floater = (FloatingRateNote) newest(rdm, none);
        rb.follow(floater, floater.getCarryingValue());
        Set<Debt> one = piecesOf(rdm);
        quietly(() -> rg.handleLinkedBondLogic(rask, 10, 100));
        InflationLinkedBond linker = (InflationLinkedBond) newest(rdm, one);
        int presses = 0, linkerMonth = 0;
        double raised = Double.NaN;
        while (presses < 4 && (presses < 1 || linker.centralBankPrincipal() <= 0)) {
            press(rg, rb, presses == 0 ? "sale month: the settle" : "the central bank's dial buys in");
            presses++;
        }
        check("fixture: households, the bank and the central bank hold the linker before it is shortened",
                linker.householdPrincipal() > 0 && linker.centralBankPrincipal() > 0 && linker.getOustandingPrincipal() > 0);
        out.printf("   the linker of D$%,.0f: households %,.0f, bank %,.0f, businesses %,.0f, world %,.0f, central bank %,.0f%n", linker.getFaceValue(),
                linker.householdPrincipal(), linker.bankPrincipal(), linker.firmPrincipal(), linker.worldPrincipal(), linker.centralBankPrincipal());
        raised = shorten(rg, linker, 12);
        rb.follow(linker, raised);
        int floaterPress = presses;
        double linkerFace = linker.getFaceValue();
        Worst due = new Worst();
        while (rdm.getDebt().contains(floater) && presses < 40) {
            presses++;
            if (presses - floaterPress == 6) quietly(() -> rdm.takeTheDial(.05));
            if (presses - floaterPress == 14) quietly(() -> rdm.takeTheDial(.02));
            boolean linkerLast = rdm.getDebt().contains(linker) && linker.getRemainingMonths() == 1;
            if (rdm.getDebt().contains(linker)) {
                linkerMonth++;
                rIdx.k = linkerMonth;
            }
            boolean floaterLast = floater.getRemainingMonths() == 1;
            if (rdm.getDebt().contains(linker)) due.add(linker.principalDueNextMonth(), linkerLast ? linkerFace : 0, "linker, " + linker.getRemainingMonths() + " months left");
            due.add(floater.principalDueNextMonth(), floaterLast ? floater.getFaceValue() : 0, "floater, " + floater.getRemainingMonths() + " months left");
            press(rg, rb, linkerLast ? "maturity: a linker repays the indexed face by each holder's share"
                    : floaterLast ? "maturity: a floater repays its face, the dial having moved twice"
                    : presses - floaterPress == 6 || presses - floaterPress == 14 ? "dial moved" : "indexation: the index rises, held by all");
            if (linkerLast) {
                double ratio = linker.getRatio();
                near("the real linker repaid, in its last month, the face through the principal row: the bridge's repaid row", rg.getTreasuryRepaid(), linkerFace, 1e-6);
                near("...and the uplift through the coupons: the indexed face less the face", linker.getLastUpliftPaid(), linkerFace * (ratio - 1), 1e-6);
                near("...the coupon it paid is the real coupon on the indexed face and the uplift", linker.getLastCoupon(),
                        linkerFace * ratio * linker.getRealCoupon() / 12 + linkerFace * (ratio - 1), 1e-6);
                double cpn = linker.getLastCoupon(), fcpn = floater.getLastCoupon(), fout = floater.getOustandingPrincipal();
                // What the floater's coupon of the month paid each holder comes off the month's totals; the rest is the linker's.
                M_SHARES.add(rg.getCouponsToHouseholds() - fcpn * floater.householdPrincipal() / fout, cpn * rg.getPrincipalToHouseholds() / linkerFace, "households");
                M_SHARES.add(rg.getCentralBank().getPaperCoupons() - fcpn * floater.centralBankPrincipal() / fout,
                        cpn * rg.getCentralBank().getPaperRedeemed() / linkerFace, "central bank");
                M_SHARES.add(rg.getCouponsToFirms() - fcpn * floater.firmPrincipal() / fout, cpn * rg.getPrincipalToFirms() / linkerFace, "businesses");
                check("the households and the central bank were both repaid a part of the principal, and so both got a part of the uplift",
                        rg.getPrincipalToHouseholds() > 0 && rg.getCentralBank().getPaperRedeemed() > 0
                                && rg.getCouponsToHouseholds() > fcpn && rg.getCentralBank().getPaperCoupons() > 0);
                near("the statements' uplift paid in cash is the same", rna.getUpliftPaidInCash(), linker.getLastUpliftPaid(), 1e-9);
                near("...and the interest line excludes it: the cash interest plus the uplift paid is the coupons of the month (the linker's and the floater's)",
                        rna.getInterestPaidInCash() + rna.getUpliftPaidInCash(), cpn + fcpn, 1e-6);
                check("...and the debt manager's uplift paid is the same, and the coupon booked is the coupons less it",
                        Math.abs(rdm.getUpliftPaid() - linker.getLastUpliftPaid()) <= 1e-9
                                && Math.abs(rdm.getCouponBooked() - (cpn + fcpn - linker.getLastUpliftPaid())) <= 1e-6);
            }
        }
        due.report("the principal either piece's next payment repays is nothing until the last month, and then its face - the linker's not indexed: the rollover refinances the face, the uplift is paid in the coupon", 1e-6);
        rb.coupon.report("every month the real floater's coupon is its face x (the dial + margin) / 12 and the linker's the real coupon on the indexed face", 1e-6);
        rb.reset.report("...the floater's rate reset on both moves of the dial", 1e-9);
        rb.catchUp.report("...the linker's carrying value caught up with the index every month", 1e-6);
        rb.carry.report("...the carrying values roll by the expense and the coupon", 1e-6);
        M_SHARES.report("each holder was paid, of the linker's last coupon (the uplift in it), the share it was repaid of the face - the households, the central bank, the businesses", 1e-6);
        report("real city", rb);
        check("fixture: the floater played to maturity moved the dial twice and rolled nothing (the rollover is off)",
                !rdm.getDebt().contains(floater) && !rdm.getDebt().contains(linker) && rb.moves >= 2);

        // --- a real linker under a falling index ---
        Game dg = readyCity("floatlink-falling", true, .1);
        DebtManager ddm = dg.getDebtManager();
        NationalAccounts dna = dg.getEconomyManager().getNationalAccounts();
        double[] fall = new double[13];
        Arrays.fill(fall, 0.99);
        Index dIdx = new Index(ddm.priceIndexLevel(), fall);
        ddm.setPriceIndexSource(dIdx);
        Set<Debt> dnone = piecesOf(ddm);
        double dask = askOf(dg, .25);
        quietly(() -> dg.handleLinkedBondLogic(dask, 10, 100));
        InflationLinkedBond deflating = (InflationLinkedBond) newest(ddm, dnone);
        Books db = new Books();
        int dpress = 0;
        while (dpress < 4 && (dpress < 1 || deflating.centralBankPrincipal() <= 0)) {
            press(dg, db, dpress == 0 ? "sale month: the settle" : "the central bank's dial buys in");
            dpress++;
        }
        check("fixture: households and the central bank hold the linker before it is shortened", deflating.householdPrincipal() > 0 && deflating.centralBankPrincipal() > 0);
        db.follow(deflating, shorten(dg, deflating, 12));
        double dface = deflating.getFaceValue();
        double uplifts = 0;
        for (int k = 1; k <= 12; k++) {
            dIdx.k = k;
            press(dg, db, k == 12 ? "maturity: a linker repays the original face (the floor), the index having fallen" : "deflation month: the index falls, held by all");
            uplifts += deflating.getLastUplift();
        }
        check("the index fell every month, so the ratio ended at " + String.format("%.4f", deflating.getRatio()) + ", under 1", deflating.getRatio() < 1);
        check("...each month booked a negative uplift: " + String.format("%,.2f", uplifts) + " over the life", uplifts < 0);
        near("at the last payment the face was repaid whole through the principal row: the bridge's repaid row", dg.getTreasuryRepaid(), dface, 1e-6);
        near("...and no uplift was paid: the floor is the original face, not the indexed one", deflating.getLastUpliftPaid(), 0, 0);
        near("...the last coupon is the real coupon on the indexed face and nothing added", deflating.getLastCoupon(), dface * deflating.getRatio() * deflating.getRealCoupon() / 12, 1e-6);
        near("...the statements' uplift paid in cash is nothing", dna.getUpliftPaidInCash(), 0, 0);
        check("...and it is off the books, carried at nothing", !ddm.getDebt().contains(deflating) && Math.abs(deflating.getCarryingValue()) <= 1e-9);
        db.coupon.report("every month the linker's coupon is the real coupon on the (falling) indexed face", 1e-6);
        db.catchUp.report("...its carrying value caught up with the index every month", 1e-6);
        db.carry.report("...the carrying value rolls by the expense and the coupon", 1e-6);
        report("falling city", db);
    }

    /* =====================================================================
       6. THE MONEY CLOSES
       ===================================================================== */

    static void theMoneyCloses() {
        out.println("\n--- 6. the money closes: every month of 3-5 audited, the holders paid their shares, nothing counted twice ---");

        check("the audit closed every month pressed (" + audits.months + " months; " + audits.open + " open"
                + (audits.open > 0 ? ", the worst " + String.format("%.1f", audits.worst) + "x its tolerance in: " + audits.worstAt : "") + ")",
                audits.months > 0 && audits.open == 0);
        for (Map.Entry<String, int[]> e : audits.byEvent.entrySet()) {
            if (e.getValue()[1] > 0) out.printf("   OPEN in %s: %d of %d months%n", e.getKey(), e.getValue()[1], e.getValue()[0]);
        }
        // The months each kind of event is pressed in, by the section that presses them.
        String[][] needed = { { "3", "sale month: the settle" }, { "3", "dial moved up" }, { "3", "dial at nothing" },
                { "4", "indexation: the index rises" }, { "4", "deflation month: the index falls" },
                { "5", "maturity: a linker repays the indexed face by each holder's share" },
                { "5", "maturity: a floater repays its face, the dial having moved twice" },
                { "5", "maturity: a linker repays the original face (the floor), the index having fallen" } };
        for (String[] need : needed) {
            if (!ran[Integer.parseInt(need[0])]) continue;
            check("...among them the month of: " + need[1], audits.byEvent.containsKey(need[1]) && audits.byEvent.get(need[1])[1] == 0);
        }
        X_HOLDERS.report("every holder - households, businesses, the world, the central bank - was paid its share of every coupon, the uplift's in the last", 1e-6);
        X_TREASURY.report("the cash coupons the pieces paid are what the treasury paid on its interest line, with the uplift paid beside it - nothing counted twice", 1e-6);
        X_BOOKS.report("the debt manager's month and the statements' lines are the pieces' sums, across every pressed month", 1e-6);
        X_NOTCASH.report("the bridge's not-cash row is the expense the cash never paid, across every pressed month", 1e-6);
        X_FOOTS.report("the bridge foots, across every pressed month", 1e-6);
        X_BRIDGE.report("the bridge's residual is the bank's coupon timing and nothing else - holders of every kind, the maturity months among them", 1e-2);
        X_EARNED.report("the walk from EARNED to the budget leaves only the coupons EARNED never counted, across every pressed month", 1e-3);
    }

    /* =====================================================================
       7. THE BUYBACK
       ===================================================================== */

    /** The piece's payments as the market prices it, worked out here from the dial and index the manager expects. */
    static double[] handPricing(DebtManager dm, Debt piece) {
        int n = piece.getRemainingMonths();
        double face = piece.getFaceValue();
        double[] flows = new double[n];
        if (piece instanceof FloatingRateNote f) {
            double[] path = dm.expectedPolicyPath();
            for (int i = 0; i < n; i++) flows[i] = face * Math.max(0, path[Math.min(i + 1, path.length - 1)] + f.getMargin()) / 12;
            flows[n - 1] += face;
        } else if (piece instanceof InflationLinkedBond l) {
            double[] growth = dm.expectedInflationGrowth();
            double r = dm.priceIndexLevel() / l.getBaseIndex();
            for (int i = 0; i < n; i++) {
                if (i > 0) r *= growth[Math.min(i, growth.length - 1)];
                flows[i] = face * r * l.getRealCoupon() / 12;
                if (i == n - 1) flows[i] += face * Math.max(1, r);
            }
        }
        return flows;
    }

    static double tills(Game g) {
        double sum = 0;
        for (String key : Sectors.KEYS) sum += g.getEconomyManager().getSectorCash(key);
        return sum;
    }

    /** A part of a settled piece bought back, held to everything a part has to hold. */
    static Buyback.Result part(Game g, Debt piece, double face, String what) {
        DebtManager dm = g.getDebtManager();
        double p0 = piece.getOustandingPrincipal(), c0 = piece.getCarryingValue(), f0 = piece.getFaceValue(), eff0 = piece.getEffectiveRate();
        double bank0 = piece.bankPrincipal(), hh0 = piece.householdPrincipal(), firm0 = piece.firmPrincipal(), world0 = piece.worldPrincipal(), cb0 = piece.centralBankPrincipal();
        double cash0 = g.getCash();
        Bank bank = g.getBank();
        HouseholdBalance hb = g.getHouseholdBalance();
        CapitalFlows hot = g.getCapitalFlows();
        double bankCash0 = bank.getCash(), bankBook0 = bank.getCityBook(), savings0 = hb.totalSavings(), hotPaper0 = hot.getPaper(),
                tills0 = tills(g), firmPaper0 = g.getOutwardInvestment().totalPaper();
        double margin0 = piece instanceof FloatingRateNote f ? f.getMargin() : Double.NaN, index0 = piece instanceof FloatingRateNote f ? f.getIndex() : Double.NaN;
        double real0 = piece instanceof InflationLinkedBond l ? l.getRealCoupon() : Double.NaN, ratio0 = piece instanceof InflationLinkedBond l ? l.getRatio() : Double.NaN,
                base0 = piece instanceof InflationLinkedBond l ? l.getBaseIndex() : Double.NaN,
                uplift0 = piece instanceof InflationLinkedBond l ? l.getLastUplift() : Double.NaN;

        Buyback.Result quote = g.quoteBuyback(piece, face, Double.NaN);
        near(what + ": the fair price the holders offer against is the market value on the dial and index it expects (worked out here), per 100",
                quote.fairPer100(), dm.curve().pv(handPricing(dm, piece)) / p0 * 100, 1e-6);
        boolean over = true;
        for (Buyback.Offer o : quote.ladder()) over &= o.per100() >= quote.fairPer100() - 1e-9;
        check("...every offer is at or over fair (" + quote.ladder().size() + " offers), and the stop and the average are too",
                over && quote.averagePer100() >= quote.fairPer100() - 1e-9 && quote.stopPer100() >= quote.averagePer100() - 1e-9);
        check("...quoting moves nothing", piece.getOustandingPrincipal() == p0 && g.getCash() == cash0);

        Buyback.Result deal = g.buyBack(piece, face, Double.NaN);
        double k = 1 - deal.takenFace() / p0;
        check(what + ": the buyback took the face asked (" + String.format("%,.0f", deal.takenFace()) + " of " + String.format("%,.0f", p0) + ")", Math.abs(deal.takenFace() - face) <= 1e-6 && deal.takenFace() > 0);
        near("...the principal fell by it", piece.getOustandingPrincipal(), p0 * k, .01);
        near("...the face with it", piece.getFaceValue(), f0 * k, .01);
        near("...and the carrying value", piece.getCarryingValue(), c0 * k, .01);
        near("...the effective rate did not move", piece.getEffectiveRate(), eff0, 0);
        if (piece instanceof FloatingRateNote f) {
            check("...nor did the margin or the index", f.getMargin() == margin0 && f.getIndex() == index0);
        } else if (piece instanceof InflationLinkedBond l) {
            check("...nor did the real coupon, the base or the ratio", l.getRealCoupon() == real0 && l.getBaseIndex() == base0 && l.getRatio() == ratio0);
            near("...and the indexed principal fell with the face", l.getIndexedPrincipal(), f0 * k * ratio0, .01);
            near("...and so did the month's uplift it last booked: the same bond, smaller", l.getLastUplift(), uplift0 * k, 1e-9);
        }
        near("the bank's principal fell by exactly what it sold", bank0 - piece.bankPrincipal(), deal.bankFace(), 1e-6);
        near("...the households'", hh0 - piece.householdPrincipal(), deal.householdFace(), 1e-6);
        near("...the businesses'", firm0 - piece.firmPrincipal(), deal.firmFace(), 1e-6);
        near("...the world's", world0 - piece.worldPrincipal(), deal.worldFace(), 1e-6);
        near("...and the central bank's did not move: it is never asked", piece.centralBankPrincipal(), cb0, 0);
        near("the treasury's cash fell by what was paid", cash0 - g.getCash(), deal.paid(), 1e-6);
        near("the bank was paid its own slices' prices", bank.getCash() - bankCash0, deal.bankPaid(), 1e-6);
        near("...its book of the city's paper fell by the face it sold", bankBook0 - bank.getCityBook(), deal.bankFace(), 1e-6);
        near("the households' savings rose by their own slices' prices", hb.totalSavings() - savings0, deal.householdPaid(), 1e-6);
        near("the world's book of the paper fell by the face it sold", hotPaper0 - hot.getPaper(), deal.worldFace(), 1e-6);
        near("the businesses' tills rose by their own slices' prices, at once", tills(g) - tills0, deal.firmPaid(), 1e-6);
        near("...and their paper fell by the face they sold", firmPaper0 - g.getOutwardInvestment().totalPaper(), deal.firmFace(), 1e-6);
        MoneyAudit.Result m1 = press(g, "a buyback of half");
        near("the gain on debt retired the month booked is the carrying value of the face taken less the price",
                g.getEconomyManager().getNationalAccounts().getRetiredGain(), deal.gainOnCarrying(), 1e-6);
        check("the audit closes the month the payments are declared in, and the month after", closes(m1) && closes(press(g, "a buyback of half")));
        return deal;
    }

    static void theBuyback() {
        out.println("\n--- 7. the buyback: every offer at or over fair on the dial and index the market expects, a part of a floater and of a linker bought back ---");

        Game g = readyCity("floatlink-buyback", true, 0);
        DebtManager dm = g.getDebtManager();
        double ask = askOf(g, .25);
        Set<Debt> before = piecesOf(dm);
        quietly(() -> g.handleFloatingNoteLogic(ask, 5, 100));
        FloatingRateNote f = (FloatingRateNote) newest(dm, before);
        Set<Debt> mid = piecesOf(dm);
        quietly(() -> g.handleLinkedBondLogic(ask, 10, 100));
        InflationLinkedBond l = (InflationLinkedBond) newest(dm, mid);
        check("fixture: a floater and a linker are on the books, unsettled, and a part of either quotes nothing",
                f != null && l != null && f.unsettledFace() > 0 && l.unsettledFace() > 0
                        && g.quoteBuyback(f, f.getOustandingPrincipal() / 2, Double.NaN).takenFace() == 0
                        && g.quoteBuyback(l, l.getOustandingPrincipal() / 2, Double.NaN).takenFace() == 0);
        press(g, "sale month: the settle");
        press(g, "the month after a sale");
        check("fixture: both settled, the households hold most of each (the bank D$" + String.format("%,.0f and D$%,.0f", f.bankPrincipal(), l.bankPrincipal())
                        + "), the central bank none",
                f.unsettledFace() == 0 && l.unsettledFace() == 0 && f.householdPrincipal() > 0 && l.householdPrincipal() > 0
                        && f.centralBankPrincipal() == 0 && l.centralBankPrincipal() == 0);
        // a dial move first, so the floater's expected path and the linker's index are not at their sale
        quietly(() -> dm.takeTheDial(.035));
        for (Debt piece : new Debt[] { f, l }) {
            double p0 = piece.getOustandingPrincipal();
            Buyback.Result half = part(g, piece, p0 / 2, "half of the " + piece.getType() + " " + piece.getDuration() / 12 + "-year");
            check("...the piece is still on the books, at half its principal", dm.getDebt().contains(piece) && Math.abs(piece.getOustandingPrincipal() - p0 / 2) <= .01);
            out.printf("   %s: asked D$%,.0f, offered %.1fx, stop %.3f, average %.3f, fair %.3f, paid D$%,.0f%n", piece.getType(), half.takenFace(), half.cover(), half.stopPer100(),
                    half.averagePer100(), half.fairPer100(), half.paid());
        }

        // No money pump: sold and bought straight back, in the month and the month between.
        g.setCashForTest(Founding.WEALTHY_CASH);
        for (String kind : new String[] { "floater", "linker" }) {
            double worst = Double.NEGATIVE_INFINITY;
            for (int round = 0; round < 2; round++) {
                double sale = askOf(g, .5);
                Set<Debt> had = piecesOf(dm);
                double cash0 = g.getCash();
                quietly(() -> {
                    if (kind.equals("floater")) g.handleFloatingNoteLogic(sale, 2, 100); else g.handleLinkedBondLogic(sale, 10, 100);
                });
                double raised = g.getCash() - cash0;
                Debt fresh = newest(dm, had);
                check("fixture: a " + kind + " was sold (round " + (round + 1) + ")", fresh != null && raised > 0);
                if (fresh == null) continue;
                double soldFace = fresh.unsettledFace();
                if (round == 0) {
                    // the same month: the whole piece or nothing
                    Buyback.Result back = g.buyBack(fresh, 1e12, Double.NaN);
                    check("the " + kind + " bought straight back whole in the month it was sold is off the books",
                            back.takenFace() > 0 && Math.abs(back.takenFace() - soldFace) <= 1e-6 * Math.max(1, soldFace) && !dm.getDebt().contains(fresh));
                    worst = Math.max(worst, raised - back.paid());
                    out.printf("   %s, the same month: D$%,.0f of face raised D$%,.2f, bought back for D$%,.2f (%.2f per 100, fair %.2f)%n",
                            kind, soldFace, raised, back.paid(), back.averagePer100(), back.fairPer100());
                } else {
                    press(g, "a round trip");
                    Buyback.Result back = g.buyBack(fresh, soldFace, Double.NaN);
                    check("the " + kind + " settled and the face sold was bought back", back.paid() > 0 && Math.abs(back.takenFace() - soldFace) <= 1e-6 * Math.max(1, soldFace));
                    worst = Math.max(worst, raised - back.paid());
                    out.printf("   %s, the month between: D$%,.0f of face raised D$%,.2f, bought back for D$%,.2f (%.2f per 100, fair %.2f)%n",
                            kind, soldFace, raised, back.paid(), back.averagePer100(), back.fairPer100());
                }
            }
            check("a " + kind + " round trip never nets the city money (the best it did: " + String.format("%.2f", worst) + " ahead)", worst <= .01);
        }
        check("the audit still closes after the round trips", closes(press(g, "the round trips")));

        // The rest of the first pair: the pieces leave the books.
        for (Debt piece : new Debt[] { f, l }) {
            double rest = piece.getOustandingPrincipal();
            Buyback.Result last = g.buyBack(piece, rest, Double.NaN);
            near("the rest of the " + piece.getType() + ": asked for all that is left, the city takes it", last.takenFace(), rest, 1e-6);
            check("...and the piece leaves the books", !dm.getDebt().contains(piece));
        }
        check("...and the audit closes that month and the next", closes(press(g, "the last of a line bought back")) && closes(press(g, "the last of a line bought back")));
    }

    /* =====================================================================
       8. THE ROLLOVER
       ===================================================================== */

    static void theRollover() {
        out.println("\n--- 8. the rollover: a floater into a floater and a linker into a linker, each of the nearest of its own terms; a bill under TWELVE_MONTH_BILL ---");

        check("a floater of 1 or 3 years rolls into the 2-year, of 4 into the 5-year, of 7 or more into the 5-year; the shorter on a tie",
                FloatingRateNote.nearestYears(1) == 2 && FloatingRateNote.nearestYears(3) == 2 && FloatingRateNote.nearestYears(4) == 5
                        && FloatingRateNote.nearestYears(7) == 5 && FloatingRateNote.nearestYears(40) == 5);
        check("a linker of 5 years rolls into the 10-year, of 20 (a tie) into the 10-year, of 25 into the 30-year",
                InflationLinkedBond.nearestYears(5) == 10 && InflationLinkedBond.nearestYears(20) == 10 && InflationLinkedBond.nearestYears(25) == 30
                        && InflationLinkedBond.nearestYears(50) == 30);

        Game g = readyCity("floatlink-rollover", false, 0);
        DebtManager dm = g.getDebtManager();
        g.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        check("fixture: the city rolls each piece into its own kind", g.getRolloverMode() == Rollover.Mode.SAME_STRUCTURE);
        int[] floatMonths = { 24, 60 }, linkMonths = { 120, 360 };
        List<Debt> old = new ArrayList<>();
        for (int months : floatMonths) old.add(dueNextMonth(fixtureFloater(g, 20_000, months, .002)));
        for (int months : linkMonths) old.add(dueNextMonth(fixtureLinker(g, 20_000, months, .02, dm.priceIndexLevel())));
        g.setCashForTest(0);
        Rollover.Plan plan = g.rolloverPlan();
        List<String> issues = new ArrayList<>();
        for (Rollover.Issue i : plan.issues()) issues.add(i.type() + " " + i.term());
        check("the plan before the press rolls each into its own kind, by the nearest term of its own: " + issues,
                issues.contains("Float 2") && issues.contains("Float 5") && issues.contains("Linked 10") && issues.contains("Linked 30") && issues.size() == 4);
        Set<Debt> had = piecesOf(dm);
        MoneyAudit.Result r = press(g, "the rollover: each piece into its own kind");
        List<Debt> fresh = new ArrayList<>();
        for (Debt d : dm.getDebt()) if (!had.contains(d)) fresh.add(d);
        for (Debt d : fresh) {
            out.printf("   new %s %d months, %d left, face %,.0f%s%n", d.getType(), d.getDuration(), d.getRemainingMonths(), d.getFaceValue(),
                    d instanceof FloatingRateNote f ? String.format(", margin %+.3f%%, index %.2f%% (the dial %.2f%%)", f.getMargin() * 100, f.getIndex() * 100, dm.getPolicyRate() * 100)
                            : d instanceof InflationLinkedBond l ? String.format(", real %.3f%%, base %.5f, ratio %.5f (the index %.5f)", l.getRealCoupon() * 100, l.getBaseIndex(), l.getRatio(), dm.priceIndexLevel()) : "");
        }
        check("the pieces falling due are gone from the books", old.stream().noneMatch(dm.getDebt()::contains));
        for (int months : floatMonths) {
            int years = months / 12;
            // A piece booked at the press has paid its first coupon by the end of it: one month gone.
            check("a " + years + "-year floater falling due rolled into a new " + years + "-year floater, on the dial it read, with a margin of its own",
                    fresh.stream().anyMatch(d -> d instanceof FloatingRateNote f && f.getDuration() == months && f.getRemainingMonths() == months - 1
                            && "FLOAT".equals(f.getType()) && Double.isFinite(f.getMargin()) && f.getIndex() == dm.getPolicyRate()));
        }
        for (int months : linkMonths) {
            int years = months / 12;
            check("a " + years + "-year linker falling due rolled into a new " + years + "-year linker, a ratio of 1 on the index it was booked at",
                    fresh.stream().anyMatch(d -> d instanceof InflationLinkedBond l && l.getDuration() == months && l.getRemainingMonths() == months - 1
                            && "LINKED".equals(l.getType()) && l.getRatio() == 1 && l.getBaseIndex() > 0 && l.getRealCoupon() > 0));
        }
        check("...four new pieces and no other kind: " + fresh.stream().map(Debt::getType).toList(),
                fresh.size() == 4 && fresh.stream().allMatch(d -> d instanceof FloatingRateNote || d instanceof InflationLinkedBond));
        check("the audit closes the month it rolled", closes(r));
        check("...and the next, with the new pieces settled", closes(press(g, "the month after a rollover")));

        // Under TWELVE_MONTH_BILL each falls due into a bill.
        g.setRolloverMode(Rollover.Mode.TWELVE_MONTH_BILL);
        List<Debt> due = new ArrayList<>();
        for (int months : floatMonths) due.add(dueNextMonth(fixtureFloater(g, 20_000, months, .002)));
        for (int months : linkMonths) due.add(dueNextMonth(fixtureLinker(g, 20_000, months, .02, dm.priceIndexLevel())));
        g.setCashForTest(0);
        Rollover.Plan bills = g.rolloverPlan();
        check("the plan under TWELVE_MONTH_BILL is bills alone: " + bills.issues(),
                !bills.issues().isEmpty() && bills.issues().stream().allMatch(i -> i.type().equals("Note") && i.term() == Rollover.BILL_MONTHS));
        Set<Debt> had2 = piecesOf(dm);
        MoneyAudit.Result r2 = press(g, "the rollover: everything into a bill");
        List<Debt> fresh2 = new ArrayList<>();
        for (Debt d : dm.getDebt()) if (!had2.contains(d)) fresh2.add(d);
        check("...and the press rolled them into 12-month bills, with no floater or linker among the new pieces: " + fresh2.stream().map(Debt::getType).toList(),
                !fresh2.isEmpty() && fresh2.stream().allMatch(d -> d instanceof ShortTermTBill && d.getDuration() == Rollover.BILL_MONTHS));
        check("...the pieces falling due are gone, and the audit closes", due.stream().noneMatch(dm.getDebt()::contains) && closes(r2));

        // The central bank's own par rolls too (Game, THE CENTRAL BANK ROLLS ITS OWN): a floater and a linker it holds, falling due, are
        // replaced by the same kinds, and it holds them from issue.
        Game c = readyCity("floatlink-central-bank", false, .1);
        DebtManager cdm = c.getDebtManager();
        c.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        double cask = askOf(c, .25);
        Set<Debt> none = piecesOf(cdm);
        quietly(() -> c.handleFloatingNoteLogic(cask, 2, 100));
        FloatingRateNote heldFloater = (FloatingRateNote) newest(cdm, none);
        Set<Debt> one = piecesOf(cdm);
        quietly(() -> c.handleLinkedBondLogic(cask, 10, 100));
        InflationLinkedBond heldLinker = (InflationLinkedBond) newest(cdm, one);
        int guard = 0;
        press(c, "sale month: the settle");
        while ((heldFloater.centralBankPrincipal() <= 0 || heldLinker.centralBankPrincipal() <= 0) && guard++ < 6) press(c, "the central bank's dial buys in");
        check("fixture: the central bank, on its dial, holds part of the floater and of the linker", heldFloater.centralBankPrincipal() > 0 && heldLinker.centralBankPrincipal() > 0);
        double floaterPar = heldFloater.centralBankPrincipal(), linkerPar = heldLinker.centralBankPrincipal();
        for (Debt d : new Debt[] { heldFloater, heldLinker }) {
            d.remainingMonths = 2;                       // two months left, the term it rolls by unchanged
            d.openBooks(d.getCarryingValue());
        }
        press(c, "the month before");
        c.setCashForTest(0);
        Set<Debt> hadC = piecesOf(cdm);
        MoneyAudit.Result rc = press(c, "the central bank's par rolled with the rollover");
        List<Debt> freshC = new ArrayList<>();
        for (Debt d : cdm.getDebt()) if (!hadC.contains(d)) freshC.add(d);
        FloatingRateNote newFloater = null;
        InflationLinkedBond newLinker = null;
        for (Debt d : freshC) {
            if (d instanceof FloatingRateNote f) newFloater = f;
            if (d instanceof InflationLinkedBond l) newLinker = l;
        }
        check("the floater and the linker the central bank held fell due and were rolled into a floater and a linker: " + freshC.stream().map(Debt::getType).toList(),
                newFloater != null && newLinker != null && !cdm.getDebt().contains(heldFloater) && !cdm.getDebt().contains(heldLinker)
                        && newFloater.getDuration() == 24 && newLinker.getDuration() == 120);
        if (newFloater != null && newLinker != null) {
            out.printf("   the central bank held D$%,.0f of the floater and D$%,.0f of the linker; it holds D$%,.0f of the new floater and D$%,.0f of the new linker%n",
                    floaterPar, linkerPar, newFloater.centralBankPrincipal(), newLinker.centralBankPrincipal());
            check("...and the central bank holds part of each of the new pieces: its par was rolled, not repaid", newFloater.centralBankPrincipal() > 0 && newLinker.centralBankPrincipal() > 0);
            near("...the central bank's book of the city's paper is what the pieces say it holds", c.getCentralBank().getPaperHeld(), cdm.centralBankPrincipal(), 1e-6);
        }
        check("the audit closes the month it rolled, and the next", closes(rc) && closes(press(c, "the month after the central bank's par rolled")));
    }

    /** A fixture that falls due at the next press: its term stays what it was, its remaining months are one. */
    static Debt dueNextMonth(Debt piece) {
        piece.remainingMonths = 1;
        return piece;
    }

    /* =====================================================================
       9. THE SAVE
       ===================================================================== */

    static void theSave() throws Exception {
        out.println("\n--- 9. the save: a floater and a linker mid-life keep their terms, books and market value, and the month after a load books what the same month does without one ---");

        Game g = readyCity("floatlink-save", true, 0);
        DebtManager dm = g.getDebtManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double[] path = new double[12];
        Arrays.fill(path, 1.01);
        Index idx = new Index(dm.priceIndexLevel(), path);
        dm.setPriceIndexSource(idx);
        double ask = askOf(g, .25);
        Set<Debt> had = piecesOf(dm);
        quietly(() -> g.handleFloatingNoteLogic(ask, 5, 100));
        FloatingRateNote f = (FloatingRateNote) newest(dm, had);
        Set<Debt> mid = piecesOf(dm);
        quietly(() -> g.handleLinkedBondLogic(ask, 10, 100));
        InflationLinkedBond l = (InflationLinkedBond) newest(dm, mid);
        for (int k = 1; k <= 3; k++) {
            idx.k = k;
            if (k == 2) quietly(() -> dm.takeTheDial(.04));
            press(g, k == 1 ? "sale month: the settle" : k == 2 ? "dial moved" : "indexation: the index rises");
        }
        check("fixture: the floater's dial moved (index " + String.format("%.2f%%", f.getIndex() * 100) + " from a sale at 3.00%) and the linker was indexed (ratio "
                + String.format("%.4f", l.getRatio()) + ")", f.getIndex() == .04 && l.getRatio() > 1.02 && l.getLastUplift() > 0);

        boolean[] ok = new boolean[1];
        quietly(() -> ok[0] = g.saveGame(10, "floatlink").ok);
        check("fixture: the city saved to slot 10 of its scratch root", ok[0]);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        DebtManager loaded = back.getDebtManager();
        check("the loaded manager is handed the city's price index, not nothing", !Double.isNaN(loaded.priceIndexLevel()));
        loaded.setPriceIndexSource(idx);   // the harness drives the index in both cities: a source is not saved
        check("fixture: the loaded city has the same pieces", loaded.getDebt().size() == dm.getDebt().size());
        FloatingRateNote f2 = null;
        InflationLinkedBond l2 = null;
        for (Debt d : loaded.getDebt()) {
            if (d instanceof FloatingRateNote x) f2 = x;
            if (d instanceof InflationLinkedBond x) l2 = x;
        }
        check("...a floater of type FLOAT and a linker of type LINKED among them, read back as their own classes",
                f2 != null && l2 != null && "FLOAT".equals(f2.getType()) && "LINKED".equals(l2.getType()));
        near("the floater keeps its margin", f2.getMargin(), f.getMargin(), 0);
        near("...its index (the dial it last read)", f2.getIndex(), f.getIndex(), 0);
        near("...its carrying value", f2.getCarryingValue(), f.getCarryingValue(), 0);
        near("...its effective rate", f2.getEffectiveRate(), f.getEffectiveRate(), 0);
        near("...its remaining months and its holders", f2.getRemainingMonths() + f2.householdPrincipal() + f2.bankPrincipal(),
                f.getRemainingMonths() + f.householdPrincipal() + f.bankPrincipal(), 0);
        near("...and its market value, to the bit", loaded.marketValue(f2), dm.marketValue(f), 0);
        near("the linker keeps its real coupon", l2.getRealCoupon(), l.getRealCoupon(), 0);
        near("...its base index", l2.getBaseIndex(), l.getBaseIndex(), 0);
        near("...its ratio", l2.getRatio(), l.getRatio(), 0);
        near("...its last uplift", l2.getLastUplift(), l.getLastUplift(), 0);
        near("...its carrying value", l2.getCarryingValue(), l.getCarryingValue(), 0);
        near("...its effective rate", l2.getEffectiveRate(), l.getEffectiveRate(), 0);
        near("...and its market value, to the bit", loaded.marketValue(l2), dm.marketValue(l), 0);
        NationalAccounts na2 = back.getEconomyManager().getNationalAccounts();
        near("the government block keeps the month's uplift booked", na2.getInflationUplift(), na.getInflationUplift(), 0);
        near("...the uplift paid in cash", na2.getUpliftPaidInCash(), na.getUpliftPaidInCash(), 0);
        near("...and the not-cash part, which the bridge adds back", na2.getUpliftNotCash(), na.getUpliftNotCash(), 0);
        near("...and the bridge's not-cash row itself", back.getTreasuryNotCash(), g.getTreasuryNotCash(), 0);
        check("fixture: the uplift saved is something (" + String.format("%,.2f", na.getInflationUplift()) + ")", na.getInflationUplift() > 0);

        // The month after: the same dial, the same index, the same month, with and without the save.
        idx.k = 4;
        press(g, "the twin played without a save");
        press(back, "the twin played after a load");
        near("the month after the load books the same uplift on the linker", l2.getLastUplift(), l.getLastUplift(), 1e-9);
        near("...the same coupon on it", l2.getLastCoupon(), l.getLastCoupon(), 1e-9);
        near("...the same interest expense", l2.getLastInterestExpense(), l.getLastInterestExpense(), 1e-9);
        near("...the same coupon on the floater", f2.getLastCoupon(), f.getLastCoupon(), 1e-9);
        near("...the same interest expense on it", f2.getLastInterestExpense(), f.getLastInterestExpense(), 1e-9);
        near("...the statements' uplift is the same", na2.getInflationUplift(), na.getInflationUplift(), 1e-9);
        near("...the debt manager's uplift booked is the same", loaded.getUpliftBooked(), dm.getUpliftBooked(), 1e-9);
        near("...and the carrying values it leaves", l2.getCarryingValue() + f2.getCarryingValue(), l.getCarryingValue() + f.getCarryingValue(), 1e-6);
        check("the loaded city's month closed its audit, as the original's did", closes(back.getLastMoneyAudit()) && closes(g.getLastMoneyAudit()));

        // A change of unit (a currency reform lops zeros): the money scales, the rates, the margin, the base and the ratio do not.
        double scale = .01;
        double lFace = l2.getFaceValue(), lCarry = l2.getCarryingValue(), lUplift = l2.getLastUplift(), lIndexed = l2.getIndexedPrincipal(), lOwed = l2.owedAtMaturity();
        double fFace = f2.getFaceValue(), fCarry = f2.getCarryingValue(), fCoupon = f2.getLastCoupon();
        double[] lFlows = l2.remainingCashFlows(), fFlows = f2.remainingCashFlows();
        double lReal = l2.getRealCoupon(), lBase = l2.getBaseIndex(), lRatio = l2.getRatio(), lEff = l2.getEffectiveRate();
        double fMargin = f2.getMargin(), fIndex = f2.getIndex(), fEff = f2.getEffectiveRate();
        l2.redenominate(scale);
        f2.redenominate(scale);
        near("a change of unit by a hundred takes the linker's face to a hundredth", l2.getFaceValue(), lFace * scale, 1e-12);
        near("...its carrying value", l2.getCarryingValue(), lCarry * scale, 1e-12);
        near("...the uplift it last booked", l2.getLastUplift(), lUplift * scale, 1e-12);
        near("...its indexed principal and what it owes at maturity", l2.getIndexedPrincipal() + l2.owedAtMaturity(), (lIndexed + lOwed) * scale, 1e-12);
        near("...every payment it still owes", maxDiff(l2.remainingCashFlows(), Arrays.stream(lFlows).map(x -> x * scale).toArray()), 0, 1e-12);
        check("...but not its real coupon, its base index, its ratio or its effective rate",
                l2.getRealCoupon() == lReal && l2.getBaseIndex() == lBase && l2.getRatio() == lRatio && l2.getEffectiveRate() == lEff);
        near("...and the floater's face, carrying value and last coupon", f2.getFaceValue() + f2.getCarryingValue() + f2.getLastCoupon(),
                (fFace + fCarry + fCoupon) * scale, 1e-12);
        near("...every payment it still owes", maxDiff(f2.remainingCashFlows(), Arrays.stream(fFlows).map(x -> x * scale).toArray()), 0, 1e-12);
        check("...but not its margin, its index or its effective rate", f2.getMargin() == fMargin && f2.getIndex() == fIndex && f2.getEffectiveRate() == fEff);
    }
}

package ham.citybuildersim;

/**
 * The city's paper sold at auction: the bidders, each by its own rule in the
 * game, and the one yield that clears them.
 *
 * WHY: until CD2 the households took a share at the quoted rate and the bank
 * took the rest by construction, whatever its room; nobody could say what an
 * issue cost to sell. Here the city names what it offers and the highest yield
 * it will take, every bidder bids by the rule it already follows elsewhere in
 * the game, and the sale clears at ONE yield (D5) - the lowest at which the
 * bids cover the issue. The difference between that yield and fair is the cost
 * of selling, and it is a figure the screen can show before the button.
 *
 * This class is the game's version of the study's auction function in
 * docs/studies/city-debt/debt-model.py (spec-city-debt.md, section 4): pure,
 * no Game, handed a snapshot of the bidders and the ask. The caller works out
 * what the snapshot holds (the bank's room over Bank.RISK_CITY, the curve's
 * fair yield with the issue on the books); nothing else in the game computes a
 * clearing yield, and the screen's chart of the bids reads bidsAt() on a grid
 * of yields so it draws the arithmetic that clears, not a copy of it.
 */
public final class Auction {

    private Auction() { }

    /* =====================================================================
       THE DIALS (spec-city-debt.md, section 9)
       ===================================================================== */

    /** The default highest yield the city takes over fair at its own auction: one point (D27, Jerus's to move). */
    public static final double MAX_TAIL = .01;

    /** A new bond line's coupon is the clearing yield rounded DOWN to this, an eighth of a point, so the paper sells near par (D2). */
    public static final double COUPON_STEP = .00125;

    /** A bond line takes reopenings while it has this share of its term left, nine tenths (D14). */
    public static final double BENCHMARK_LIFE = .9;

    /** The terms a bill is sold at, in months (D3). Bond terms live on LongTermBond.MATURITIES. */
    public static final int[] BILL_MONTHS = {1, 3, 6, 12};

    /** The terms an amortizing bond is sold at, in years (D3). */
    public static final int[] AMORTIZING_YEARS = {5, 10, 15, 20, 25, 30};

    /** How close the bisection finds the stop, in yield: a hundredth of a basis point is 1e-6, this is a tenth of that. */
    public static final double STOP_TOLERANCE = 1e-7;

    /** The bisection halves a gap of under a unit of yield to STOP_TOLERANCE in a few dozen steps; this is the belt over the braces. */
    private static final int MAX_STEPS = 200;

    /* =====================================================================
       WHAT IS SOLD, WHO BIDS, WHAT COMES OUT
       ===================================================================== */

    /** The kind of paper: a discount bill, a bullet bond with a monthly coupon, or an amortizing bond. */
    public enum Kind { BILL, BOND, AMORTIZING }

    /** How a cap on the yield is read: as a tail over fair, as a yield, or not at all. */
    public enum Mode { TAIL, YIELD, FILL }

    /**
     * The highest yield the city will take (D20, D27): a tail over fair
     * (fair is known only when the sale runs, so the tail is stored and
     * resolved then), a named yield, or no highest yield at all (Fill it,
     * Jerus, 2026-10-12: "increased yield if you allow for it to get fully
     * filled"). {@code value} is the tail for TAIL, the yield for YIELD and
     * NaN for FILL; the screen reads {@code mode} to say which it is.
     */
    public record Cap(Mode mode, double value) {

        /** The default: fair plus MAX_TAIL. */
        public static final Cap DEFAULT = tail(MAX_TAIL);

        /** No highest yield: the stop rises until the bids cover the issue, or no rule bids more. */
        public static final Cap FILL = new Cap(Mode.FILL, Double.NaN);

        /** Fair plus this much, whatever fair is when the sale runs. */
        public static Cap tail(double overFair) {
            return new Cap(Mode.TAIL, Math.max(0, overFair));
        }

        /** This yield, and never under fair. */
        public static Cap at(double yield) {
            return new Cap(Mode.YIELD, yield);
        }

        /** True for Fill it. */
        public boolean fill() {
            return mode == Mode.FILL;
        }
    }

    /**
     * The bidders as they stand when the sale runs. Every figure is the
     * caller's, read from the model; this class holds none of the city.
     *
     * @param depositRate    what the bank pays savers, a year
     * @param worldRate      the world's own price of money
     * @param countryPremium what the world charges this city for its risk
     * @param householdSpare households' savings past their cushion, money
     * @param firmsIdle      the businesses' money with nothing owed against it: tills plus what is abroad (OutwardInvestment's wealth)
     * @param firmsHeld      the city's paper the businesses already hold, face
     * @param worldStock     hot money in the city now, money
     * @param worldPanicking true while the world is in a run: it bids nothing
     * @param monthlyGdp     a month of the city's output
     * @param bankRoomFace   the face the bank may still hold: its room over Bank.RISK_CITY, the caller's
     * @param bankExtra      the yield over fair the bank needs for its funding past the floor the curve carries; nothing usually
     */
    public record Bidders(double depositRate, double worldRate, double countryPremium,
                          double householdSpare, double firmsIdle, double firmsHeld,
                          double worldStock, boolean worldPanicking, double monthlyGdp,
                          double bankRoomFace, double bankExtra) {

        /** An empty city: nobody has anything to bid with. */
        public static Bidders none() {
            return new Bidders(0, 0, 0, 0, 0, 0, 0, false, 0, 0, 0);
        }
    }

    /**
     * What a schedule is worth at the sale, per 100 of face, given the tail the
     * sale cleared at - the stop over fair, in yield. The game hands one that
     * prices on the city's curve (YieldCurve.pvAtSpread()), so a sale with no
     * tail is worth exactly what the buyback will value it at and the tail is
     * a spread over the curve; without one the sale prices at the one flat
     * yield of the stop.
     *
     * WHY (CD2, slice 3's finding): priced at one flat yield, the schedule of a
     * bond whose coupon was rounded down to an eighth was worth more at the
     * sale than DebtManager.marketValue() values it on an upward curve, and a
     * round trip netted the city money on a big enough issue - the fee is
     * fixed and the gain is not. Priced on the curve, the round trip loses
     * the fee and the tail, as it should.
     */
    public interface Pricer {
        double per100(double[] flowsPer100, double tail);
    }

    /**
     * What is for sale.
     *
     * @param kind       bill, bond or amortizing
     * @param months     the term in months (an amortizing bond's is its years times twelve)
     * @param face       the face on offer
     * @param fair       the curve's fair yield for this paper with the issue on the books, the caller's
     * @param lineCoupon NaN for a new line; the line's own coupon for a reopening
     * @param fee        the fixed cost of a sale, money
     * @param pricer     how the schedule is priced at the sale; null prices it at the stop, one flat yield (a pure fixture's)
     */
    public record Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee, Pricer pricer) {

        /** An ask priced at one flat yield, the stop: the fixture's. */
        public Ask(Kind kind, int months, double face, double fair, double lineCoupon, double fee) {
            this(kind, months, face, fair, lineCoupon, fee, null);
        }

        /** True when this opens a line rather than reopens one. */
        public boolean isNewLine() {
            return Double.isNaN(lineCoupon);
        }

        /** The term in whole years, an amortizing bond's slices. */
        public int years() {
            return months / 12;
        }
    }

    /** What each bidder takes: face in every field. */
    public record Fill(double households, double firms, double world, double bank) {

        /** The four together. */
        public double total() {
            return households + firms + world + bank;
        }
    }

    /**
     * The sale: the one stop, what it cost, what was sold and who took it.
     *
     * @param ask         what was offered
     * @param cap         the highest yield the city allowed
     * @param ceiling     that cap as a yield: never under fair
     * @param stop        the clearing yield, at which everyone is filled (D5)
     * @param tail        the stop over fair
     * @param cover       what was bid up to the ceiling, each bidder at most the issue, over the issue
     * @param sold        the face sold: the whole issue, or what was bid at the ceiling when that fell short
     * @param shortSale   true when the bids did not reach the issue at the ceiling
     * @param coupon      the coupon the paper carries: 0 for a bill
     * @param pricePer100 the paper's value per 100 of face at the stop: one flat yield, or on the curve with the tail as a spread when the ask carries a pricer
     * @param proceeds    price over 100 times the face sold, less the fee; nothing when nothing sold
     * @param fills       who took the face sold
     */
    public record Result(Ask ask, Cap cap, double ceiling, double stop, double tail, double cover,
                         double sold, boolean shortSale, double coupon, double pricePer100,
                         double proceeds, Fill fills) {

        /** True when nothing was offered. */
        public boolean isEmpty() {
            return ask.face() <= 0;
        }

        /** The face the city asked for and did not get. */
        public double shortBy() {
            return ask.face() - sold;
        }

        /** A bill's discount rate, (100 - price) / 100 over the year: NaN for paper that is not a bill. */
        public double discountRate() {
            if (ask.kind() != Kind.BILL || ask.months() <= 0) return Double.NaN;
            return (100 - pricePer100) / 100 * 12 / ask.months();
        }
    }

    /* =====================================================================
       THE BIDDERS' RULES, each the rule the game already follows

       They are public so the screen's chart and the harness read the same
       arithmetic. Each is a bid at a yield y; every one is non-decreasing in
       y, which is what lets run() find the stop by bisection.
       ===================================================================== */

    /**
     * The households (D32, Jerus, 2026-10-20): HouseholdBalance's rule at the
     * auction's own yield - HOUSEHOLD_PAPER_APPETITE of the issue for each
     * unit of yield over the deposit rate - out of savings past the cushion
     * and never more than the issue. No half-of-the-issue cap: D32 removed it.
     */
    public static double householdsAt(double y, Bidders b, double face) {
        double want = HouseholdBalance.HOUSEHOLD_PAPER_APPETITE
                * Math.max(0, y - Math.max(0, b.depositRate())) * face;
        return Math.max(0, Math.min(Math.min(want, b.householdSpare()), face));
    }

    /**
     * The businesses with idle cash (D33): OutwardInvestment's rule with the
     * city's paper beside the deposit and the world - a share of their idle
     * wealth, APPETITE per unit of what the paper pays over the better of the
     * two, never past MAX_SHARE, less what they already hold, moved at
     * OUT_SPEED as their money abroad is.
     */
    public static double firmsAt(double y, Bidders b) {
        double spread = Math.max(0, y - Math.max(b.depositRate(), b.worldRate()));
        double target = Math.max(0, b.firmsIdle())
                * Math.min(OutwardInvestment.MAX_SHARE, OutwardInvestment.APPETITE * spread);
        return Math.max(0, target - b.firmsHeld()) * OutwardInvestment.OUT_SPEED;
    }

    /**
     * The world as hot money (D7): CapitalFlows' rule with the auction's yield
     * as the city's rate - the stock it would hold at this excess return,
     * less what is here, at ARRIVAL_SPEED. Nothing while it is panicking.
     * The target is CapitalFlows.stockAt()'s arithmetic, mirrored rather than
     * called because stockAt() is an instance method of a model that holds a
     * stock; AuctionCheck compares the two on the same inputs.
     */
    public static double worldAt(double y, Bidders b) {
        if (b.worldPanicking()) return 0;
        double wouldSpread = Math.max(0, Math.min(CapitalFlows.MAX_SPREAD,
                Math.max(b.depositRate(), y) - b.worldRate() - b.countryPremium()));
        double target = wouldSpread * CapitalFlows.APPETITE * Math.max(0, b.monthlyGdp()) * 12;
        return Math.max(0, target - b.worldStock()) * CapitalFlows.ARRIVAL_SPEED;
    }

    /** The bank (spec 4.2), competitive: its whole room at fair plus what its funding needs past the curve, and nothing under that - a step, not a slope. */
    public static double bankAt(double y, Bidders b, double fair) {
        return y >= fair + b.bankExtra() ? Math.max(0, b.bankRoomFace()) : 0;
    }

    /** The four bids at one yield, for the paper on offer. */
    public static Fill bidsAt(double y, Bidders b, Ask a) {
        return new Fill(householdsAt(y, b, a.face()), firmsAt(y, b), worldAt(y, b), bankAt(y, b, a.fair()));
    }

    /* =====================================================================
       THE AUCTION
       ===================================================================== */

    /**
     * The highest yield the cap allows, as a yield. A tail is read against
     * fair; a named yield under fair is fair (nobody bids under it); Fill it
     * stops where no rule adds anything - the world's, at its rate plus its
     * premium plus MAX_SPREAD - but is never under fair either.
     */
    public static double ceilingOf(Cap cap, Ask a, Bidders b) {
        double fair = a.fair();
        double ceiling;
        switch (cap.mode()) {
            case TAIL: ceiling = fair + cap.value(); break;
            case YIELD: ceiling = cap.value(); break;
            default: ceiling = b.worldRate() + b.countryPremium() + CapitalFlows.MAX_SPREAD; break;
        }
        return Math.max(fair, ceiling);
    }

    /**
     * Sell the ask to the bidders. The stop is the lowest yield at which the
     * four bids cover the issue, never under fair and never over the ceiling;
     * if nothing covers it at the ceiling the sale is short, at the ceiling,
     * and sells what was bid. Everyone is filled at the stop (D5): the
     * households first and in full (D13), the other three pro rata to their
     * bids at it.
     */
    public static Result run(Ask a, Bidders b, Cap cap) {
        double face = a.face();
        double fair = a.fair();
        if (face <= 0) {
            return new Result(a, cap, fair, fair, 0, 0, 0, false, 0, 0, 0, new Fill(0, 0, 0, 0));
        }
        double ceiling = ceilingOf(cap, a, b);

        double stop;
        double sold;
        boolean shortSale = false;
        if (covered(fair, b, a)) {
            stop = fair;
            sold = face;
        } else if (!covered(ceiling, b, a)) {
            stop = ceiling;
            shortSale = true;
            sold = Math.min(face, bidsAt(ceiling, b, a).total());
        } else {
            double lo = fair;
            double hi = ceiling;
            for (int i = 0; i < MAX_STEPS && hi - lo > STOP_TOLERANCE; i++) {
                double mid = (lo + hi) / 2;
                if (covered(mid, b, a)) hi = mid; else lo = mid;
            }
            stop = hi;
            sold = face;
        }

        Fill bids = bidsAt(stop, b, a);
        double households = Math.min(bids.households(), sold);
        double others = bids.firms() + bids.world() + bids.bank();
        double share = others > 0 ? (sold - households) / others : 0;
        Fill fills = new Fill(households, bids.firms() * share, bids.world() * share, bids.bank() * share);

        Fill top = bidsAt(ceiling, b, a);
        double cover = (Math.min(top.households(), face) + Math.min(top.firms(), face)
                + Math.min(top.world(), face) + Math.min(top.bank(), face)) / face;

        double coupon = couponFor(a, stop);
        double[] flows = a.kind() == Kind.AMORTIZING
                ? amortizingPer100(coupon, a.years()) : bulletPer100(coupon, a.months());
        double price = a.pricer() != null ? a.pricer().per100(flows, stop - fair) : Debt.presentValue(flows, stop);
        double proceeds = sold > 0 ? price / 100 * sold - a.fee() : 0;

        return new Result(a, cap, ceiling, stop, stop - fair, cover, sold, shortSale,
                coupon, price, proceeds, fills);
    }

    /** Do the bids at this yield cover the issue? */
    private static boolean covered(double y, Bidders b, Ask a) {
        return bidsAt(y, b, a).total() >= a.face();
    }

    /**
     * The coupon the paper carries. A new bond line: the stop rounded down to
     * COUPON_STEP (D2), with a hair added before the floor so a stop that IS a
     * multiple of the step does not lose a step to floating error. A
     * reopening keeps its line's coupon. An amortizing bond: the stop itself,
     * the one coupon that prices its whole schedule at 100. A bill: none.
     */
    private static double couponFor(Ask a, double stop) {
        switch (a.kind()) {
            case BILL: return 0;
            case AMORTIZING: return stop;
            default:
                return a.isNewLine()
                        ? Math.floor((stop + 1e-12) / COUPON_STEP) * COUPON_STEP
                        : a.lineCoupon();
        }
    }

    /* =====================================================================
       THE SCHEDULES, per 100 of face

       Public so the harness prices a sale by summing them itself. Month i of
       the array is paid i months after the sale; the sale prices them at one
       flat yield, monthly compounding (Debt.presentValue's convention).
       ===================================================================== */

    /** A bill or a bullet bond: the coupon a month (a bill's is nothing) and 100 at the end. */
    public static double[] bulletPer100(double coupon, int months) {
        double[] flows = new double[Math.max(0, months)];
        for (int m = 0; m < flows.length; m++) flows[m] = 100 * coupon / 12;
        if (flows.length > 0) flows[flows.length - 1] += 100;
        return flows;
    }

    /** An amortizing bond (MediumTermBond's shape): the principal in equal yearly slices, the coupon monthly on what is left. */
    public static double[] amortizingPer100(double coupon, int years) {
        double[] flows = new double[Math.max(0, years) * 12];
        double left = 100;
        double slice = years > 0 ? 100.0 / years : 0;
        for (int m = 0; m < flows.length; m++) {
            flows[m] = left * coupon / 12;
            if ((m + 1) % 12 == 0) {
                flows[m] += slice;
                left -= slice;
            }
        }
        return flows;
    }
}

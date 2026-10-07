package ham.citybuildersim;

/**
 * What the city expects prices to do, and how far it believes the central bank.
 *
 * WHY THIS EXISTS (0.7.42, the anchor). Until 0.7.41 the only things that held
 * the price level were constants: the shelf's opening price, the price of
 * ground, the cash in every building's cost, the fees - all in founding money,
 * all fixed for good. A push ended as a one-off level step, pulled back by
 * those constants, and an inflation target meant nothing because nothing in
 * the city ever read it: under the autopilot a city ran at about 0% a year
 * whatever the dial said (the project's spec-inflation.md, section 1, and
 * inflation-research.md). A real economy's anchor is not a constant; it is
 * what people expect, and what they expect depends on whether they believe
 * the bank.
 *
 * SO THE MONEY CONSTANTS FOLLOW WHAT PEOPLE EXPECT PRICES TO BE, not what they
 * were. This class keeps four numbers, the level the month is struck at and
 * two records (the month's lean and, since 0.7.45, its move in credibility):
 * the bank's CREDIBILITY
 * (kappa, between KMIN and KMAX), recent inflation SMOOTHED over ADAPT_MONTHS,
 * EXPECTED INFLATION - the target weighted by credibility plus the smoothed
 * rate weighted by the rest - and the EXPECTED PRICE LEVEL, which compounds at
 * expected inflation from 1.0 the month the basket is based. Game re-strikes
 * the money constants at that level every month (Game.restrikeMoneyConstants()),
 * the wages take half their indexing from it (LabourMarket.updateCostOfLiving()),
 * the real rates are struck against it (Game.realRateDifferential(),
 * realDepositRate()), and the currency drifts at its credible part
 * (ForeignAccounts.setExpectedDrift()).
 *
 * CREDIBILITY IS HARD TO WIN AND SLOW TO LOSE WHILE THE BANK FIGHTS. On target
 * (a smoothed miss within TOLERANCE) it climbs a GAIN_MONTHS-th of the way to
 * KMAX a month, five years to most of it. Off target it falls toward KMIN at a
 * LOSS_MONTHS-th of the way a month times the share of a full miss - but only
 * as far as the bank is NOT leaning against it: a rate set at the rule's
 * advice, or past it, keeps every bit of credibility a supply shock would
 * otherwise cost (the lean). The advice is what holding the target takes -
 * the Standard rule's, however strict the player has made the bank (0.7.52,
 * DebtManager's HOW STRICT) - so a looser rule leans less, and loses trust
 * as far as it falls short. Measured in the prototype: a credibility floor
 * of .1, lost to supply shocks regardless of the rate, locked the founding
 * at 10% a year; at .25 and lean-aware it settles.
 *
 * THE MONTH STRIKES ITS CONSTANTS AT ITS TOP. The level is struck into the
 * money constants as the first thing a press does (Game.nextMonth(),
 * strikeLevel() then Game.restrikeMoneyConstants()), at the level the last
 * month ended on - so the month lives at one level from its first statement
 * to the next press, and a constant read between the presses (the pension in
 * the header's EARNED, a build card's cost) is the month's, as every other
 * price is. The struck level is saved beside the anchor's own, so a load
 * strikes the constants where the month that was saved had them. What Game
 * strikes for itself from the level (the FIXED grant, the rebates, the issue
 * fee) reads getExpectedLevel(), which inside a month is the struck level and
 * between the presses is the one the next press strikes at - so a bill read
 * before a month runs is the bill it pays.
 *
 * The level is drift-only. A save from before 0.7.42 seeds it at 1.0, because
 * its constants are still at founding money and nothing should jump on the
 * load; a level catch-up to the index was measured to be a unit root with the
 * floor. A reform does not touch it - it is a ratio, and a constant is struck
 * as founding / unit x level.
 *
 * @author Jerus
 */
public class Expectations {

    /* =====================================================================
       THE DIALS (spec-inflation.md, section 2.1 and star 14)
       ===================================================================== */

    /** Months recent inflation is smoothed over, as an exponential average: a twelfth of the gap a month. */
    public static final int ADAPT_MONTHS = 12;

    /** How far smoothed inflation may sit from the target, a fraction a year, and still count as on target: one point. */
    public static final double TOLERANCE = .01;

    /** The miss past TOLERANCE that costs credibility at the full LOSS_MONTHS speed: four points more, five off target in all. */
    public static final double MISS_SCALE = .04;

    /** Months over which credibility falls toward KMIN on a full miss nobody leans against: a twenty-fourth of the distance a month, half of it gone in about seventeen. */
    public static final int LOSS_MONTHS = 24;

    /** Months over which credibility climbs toward KMAX on target: a sixtieth of the distance a month, about five years to rebuild. */
    public static final int GAIN_MONTHS = 60;

    /** The least anyone believes the bank: a quarter of expected inflation stays the target however long it misses (.1 was measured to lock the founding at 10%). */
    public static final double KMIN = .25;

    /** The most anyone believes it: a twentieth of expected inflation is always recent experience. */
    public static final double KMAX = .95;

    /** Where a new city, and a save from before the anchor, starts: a bank believed more than not, with something to earn. */
    public static final double KSEED = .80;

    /** The least inflation anybody expects, a fraction a year: the floor under the deflation attractor (minus one per cent). */
    public static final double EXPECTED_FLOOR = -.01;

    /* =====================================================================
       THE STATE - eight slots saved: four numbers and whether they are seeded, the level the month is struck at, and the month's lean and its move in credibility as records
       ===================================================================== */

    private double credibility = KSEED;
    private double smoothed = DebtManager.DEFAULT_INFLATION_TARGET;
    private double expected = DebtManager.DEFAULT_INFLATION_TARGET;
    private double level = 1.0;
    /** True once seeded - by a new game's first month, or an old save's load. */
    private boolean seeded;
    /** This month's lean against the miss, 0 to 1 - a record for the screens; nothing reads it back. */
    private double lean;
    /** The level the money constants are struck at this month: the expected level as the month began (strikeLevel()). */
    private double struck = 1.0;
    /** This month's move in credibility, the month's own less the last's (0.7.45): a record for NEEDS YOU and the Policy tab - nothing reads it back. 0 before the basket is based and the month the anchor is seeded. */
    private double step;

    /** How far the city believes the bank, KMIN to KMAX. */
    public double getCredibility() { return credibility; }

    /** Year-on-year inflation smoothed over ADAPT_MONTHS, a fraction a year. */
    public double getSmoothedInflation() { return smoothed; }

    /** What the city expects inflation to be, a fraction a year: the target before the basket is based. */
    public double getExpectedInflation() { return expected; }

    /** What it expects prices to be against founding: 1.0 until the basket is based, then compounding at expected inflation. Every Pe-struck money constant is founding / unit times this. */
    public double getExpectedLevel() { return level; }

    /** How hard the bank leaned against the miss this month, 0 to 1 (0 on target, or the wrong way). */
    public double getLean() { return lean; }

    /** The level this month's money constants are struck at: the expected level the last month ended on (THE MONTH STRIKES ITS CONSTANTS AT ITS TOP). What a Pe-struck price is founding / unit times. */
    public double getStruckLevel() { return struck; }

    /** How far credibility moved this month, a fraction (0.7.45): negative is trust lost. 0 before the basket is based and the month the anchor is seeded - a record, saved as the eighth slot. */
    public double getCredibilityStep() { return step; }

    /** How far smoothed inflation - what credibility is judged on - sits from a target, a fraction a year: positive is over it (0.7.45). Pure. */
    public double missFrom(double target) { return smoothed - target; }

    /** The top of a month: the constants are to be struck at the level the last month ended on. Returns it. */
    public double strikeLevel() {
        struck = level;
        return struck;
    }

    /** Expected inflation as a month's growth: (1 + expected) to the twelfth, less one. */
    public double monthlyExpected() { return Math.pow(1 + expected, 1.0 / 12) - 1; }

    /* =====================================================================
       THE MONTH
       ===================================================================== */

    /**
     * One month, read straight after the price index has taken its month.
     *
     * @param index   the price index, just struck
     * @param target  the inflation target, a fraction a year
     * @param policy  the policy rate this month
     * @param neutral the neutral nominal rate (DebtManager.neutralRate()): the Standard rule's rate on target
     * @param rule    what holding the target takes at this year's inflation, inside the dial: the Standard rule's
     *                advice (DebtManager.holdingRate()), whatever the bank's strictness (0.7.52) - the rule's own at Standard
     */
    public void takeMonth(PriceIndex index, double target, double policy, double neutral, double rule) {
        boolean seedingNow = !seeded;
        if (!seeded) seed(index, target);
        step = 0;
        // BEFORE THE BASKET IS BASED there is no inflation to read: everyone
        // expects the target, the level is founding's, and credibility waits.
        if (!index.isBased()) {
            level = 1.0;
            expected = target;
            smoothed = target;
            lean = 0;
            return;
        }
        double before = credibility;
        double inflation = index.hasRate() ? index.inflation() : target;
        smoothed += (inflation - smoothed) / ADAPT_MONTHS;
        double miss = Math.abs(smoothed - target);

        // THE LEAN: how far from neutral toward the advice the rate stands -
        // `rule`, what holding the target takes, the Standard rule's at any
        // strictness since 0.7.52 - when the advice says to fight the miss:
        // above neutral against inflation over target, below it against
        // inflation under.
        double leaning = 0;
        if (smoothed > target && rule > neutral) leaning = (policy - neutral) / (rule - neutral);
        else if (smoothed < target && rule < neutral) leaning = (neutral - policy) / (neutral - rule);
        lean = Math.max(0, Math.min(1, leaning));

        if (miss <= TOLERANCE) {
            credibility += (KMAX - credibility) / GAIN_MONTHS;
        } else {
            credibility -= (credibility - KMIN) / LOSS_MONTHS
                    * Math.min(1, (miss - TOLERANCE) / MISS_SCALE) * (1 - lean);
        }
        credibility = Math.max(KMIN, Math.min(KMAX, credibility));
        // ...and the month's move, for the screens (0.7.45): none the month it was seeded.
        step = seedingNow ? 0 : credibility - before;
        expected = Math.max(EXPECTED_FLOOR, credibility * target + (1 - credibility) * smoothed);
        level *= Math.pow(1 + expected, 1.0 / 12);
    }

    /**
     * Seeds a city with no anchor yet: a new game's first month, or a save
     * from before 0.7.42 on its load. Credibility at KSEED, smoothed inflation
     * at the year's rate if the index has one (the target if not), and the
     * level at 1.0 - the constants are still at founding money.
     */
    public void seed(PriceIndex index, double target) {
        credibility = KSEED;
        smoothed = index != null && index.hasRate() ? index.inflation() : target;
        expected = Math.max(EXPECTED_FLOOR, credibility * target + (1 - credibility) * smoothed);
        level = 1.0;
        struck = 1.0;
        lean = 0;
        step = 0;
        seeded = true;
    }

    /** A new game: unseeded, at the defaults; its first month seeds it at the city's target. */
    public void reset() {
        credibility = KSEED;
        smoothed = expected = DebtManager.DEFAULT_INFLATION_TARGET;
        level = 1.0;
        struck = 1.0;
        lean = 0;
        step = 0;
        seeded = false;
    }

    /* -------------------------------- carrying -------------------------------- */

    /** The slots a save carries: credibility, smoothed, expected, level, seeded - then the lean, the level the month's constants are struck at, and (0.7.45) the month's move in credibility. */
    public static final int SAVE_SLOTS = 8;

    /** The state as the save carries it: DataSave.expectations. ORDER IS THE FORMAT; new slots go on the end. */
    public double[] toSaveArray() {
        return new double[] { credibility, smoothed, expected, level, seeded ? 1 : 0, lean, struck, step };
    }

    /**
     * @return false for an array too short to be this class's (null on a save
     *         from before 0.7.42), with nothing changed - the load path then
     *         seeds it (seed()).
     */
    public boolean restore(double[] saved) {
        if (saved == null || saved.length < 5) return false;
        credibility = Math.max(KMIN, Math.min(KMAX, saved[0]));
        smoothed = saved[1];
        expected = saved[2];
        level = saved[3] > 0 ? saved[3] : 1.0;
        seeded = saved[4] > .5;
        lean = saved.length > 5 ? saved[5] : 0;
        struck = saved.length > 6 && saved[6] > 0 ? saved[6] : level;
        // A save from before 0.7.45 has no step: nothing moved the month it loads.
        step = saved.length > 7 && Double.isFinite(saved[7]) ? saved[7] : 0;
        return true;
    }
}

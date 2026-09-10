package ham.citybuildersim;

import java.util.Random;

/**
 * The rest of the world, which has its own inflation and did not use to.
 *
 * WHY THIS EXISTS. Every foreign price in the game was a constant - food at
 * $0.20, building materials at $2.00, ore at $0.20 - converted at the exchange
 * rate and otherwise frozen since the founding of the city. That is a world in
 * which nothing outside ever changes, and it had two consequences that were
 * both quietly load-bearing:
 *
 *   - the only way an import could get dearer was for the CITY's currency to
 *     fall, so every price shock the player felt was their own fault. There was
 *     no such thing as bad luck from outside.
 *   - purchasing power parity had to be pinned at 1.00, because the world's
 *     basket never moved. An anchor at a constant is an anchor to the founding
 *     year for ever, which is not what PPP means and gets worse the longer a
 *     city runs.
 *
 * Jerus: "have the USD itself have a random weighted avg moving inflation of
 * random 1-8%".
 *
 * A WEIGHTED MOVING AVERAGE OF RANDOM DRAWS, which is what that describes and
 * also what inflation actually looks like. A fresh uniform draw every month
 * would be white noise - 1% in March, 8% in April, meaningless in a twelve-month
 * rate. Real inflation is persistent: this year's is mostly last year's. So each
 * month draws, and the rate walks a small fraction of the way toward the draw,
 * which produces long slow swings between the bounds rather than a jitter.
 *
 * DETERMINISTIC, seeded off the month like Health's outbreak roll, because
 * ForeignCheck asserts that two runs of the same city come out identical and a
 * world with real randomness in it would end that.
 *
 * @author Jerus
 */
public class WorldEconomy {

    /** The band the world's inflation wanders in. */
    public static final double MIN_INFLATION = .01;
    public static final double MAX_INFLATION = .08;

    /** Where it starts, before it has wandered anywhere. */
    public static final double OPENING_INFLATION = .03;

    /**
     * How much of last month's rate survives into this one.
     *
     * At .90 the rate moves a tenth of the way to each new draw, which is slow
     * enough to be a swing rather than noise and fast enough that the band is
     * partly realised: 1.9% to 5.0% over a full run. It was .97, which sounds
     * like a smaller change than it is - see the block below.
     */
    public static final double PERSISTENCE = .90;

    /* =====================================================================
       WHY THE DRAW IS BIASED LOW, AND WHY THE BAND WAS A FICTION

       Jerus, on watching a long run: "inflation cause numbers to be huge in the
       span of 300 years... simple lower the model to 1-5% instead of 1-8% or
       make 1-4% more likely than 5-8%".

       Both were reasonable and measuring first turned up something neither was
       about: THE BAND WAS NEVER REALISED. A flat 1-8% draw run through
       PERSISTENCE at .97 came out, over 4,001 months, as a rate that lived
       between 3.08% and 5.39%. The comment that used to sit above PERSISTENCE
       claimed it took "a decade to wander [the band] properly"; it never
       crossed it once. A moving average of a uniform draw has that draw's
       variance divided by about sixty-five, so 1-8% arrived as 4.5% give or
       take a quarter of a point, for ever.

       Which means the band was doing NONE of the work the player was supposed
       to feel and ALL of the work Jerus was complaining about: it set an
       average of 4.5%, which is what compounds, and it never delivered the 8%
       year or the 1% year, which is what would have made it worth having. A
       constant that has not been checked for what it actually produces is not
       evidence either.

       So both halves are fixed, and they are different fixes:

         THE DRAW is biased toward the bottom of the band by squaring a uniform.
         Two thirds of draws now land under 4%, the average falls from 4.5% to
         3.4%, and 8% is still reachable - just rare, which is what Jerus asked
         for and also what a tail should be.

         THE PERSISTENCE is loosened so some of the band actually arrives. The
         realised rate now spans 1.94% to 5.01% over a full run instead of
         3.08% to 5.39%: a decade of dear imports and then a quiet one, which a
         player can notice and plan around.

       The long-run price LEVEL is unmoved by the persistence change - 1.85
       either way - because TREND_PULL decides that, not the wander. Lowering
       the average is what brings the level down, from 2.58 to 1.85 over 333
       years.
       ===================================================================== */

    /**
     * How far the uniform draw is bent toward the bottom of the band.
     *
     * At 2 the draw is u squared, whose mean is a third rather than a half. Two
     * thirds of months draw under 4%; 8% needs a draw within a whisker of 1.0,
     * and so turns up about as often as a bad decade should.
     */
    public static final double LOW_BIAS = 2.0;

    private static final long SEED = 0x5F3A91C7L;

    private double annualInflation = OPENING_INFLATION;
    private double priceLevel = 1.0;

    /** Scrambles a month into something that does not correlate with its neighbours. */
    private static long scramble(long n) {
        n ^= (n << 21);
        n ^= (n >>> 35);
        n ^= (n << 4);
        return n;
    }

    /**
     * The world's long-run trend, against which the wandering rate is a cycle.
     *
     * THREE HUNDRED YEARS IS THE PROBLEM, NOT THE BAND. A rate wandering in
     * 1-8% averages about 4.5%, and 4.5% compounded over the 4,001 months this
     * game's playtest runs is a price level of **2,400,000x**. That is
     * arithmetically correct and completely unusable: measured, it put GDP at
     * $784.5B a month, ore at $6,583 a tonne, and the exchange rate against its
     * floor with wages "lifted" by 446,732%.
     *
     * It is also not history. UK prices from 1690 to today are about 200x,
     * which is 1.6% a year. A modern 1-8% band is a sensible description of a
     * DECADE and a nonsensical description of three centuries, and this game
     * simulates three centuries.
     *
     * So the band is kept - it is what the player feels month to month - and
     * the LEVEL is pulled back toward a trend. The short run stays a real,
     * unpredictable, sometimes painful thing that makes imports dearer for a
     * decade at a time; the long run stops compounding.
     *
     * THE TREND IS ZERO, which is not the same as saying the world has no
     * inflation. The level still wanders - it ends a full run around 1.85x
     * founding, having been higher and lower on the way - it just does not
     * RATCHET. A positive trend here is affordable the moment a default run is
     * shorter than three centuries, and the arithmetic is already written.
     *
     * Since 2026-09-07 this is no longer the only defence against large
     * numbers: the player can lop zeros off the currency. See Denomination.
     * That handles the city's OWN inflation, which this cannot reach, and it
     * handles it without pretending prices did not rise.
     */
    public static final double TREND_INFLATION = .0;

    /** How hard the level is pulled back to trend. Half-life about ten years. */
    public static final double TREND_PULL = .006;

    private int monthsRun;

    /**
     * Holds the world's prices still, for a fixture measuring something else.
     *
     * The twin of ForeignAccounts.pinRate(), and needed for the same reason and
     * by the same fixture. MiningCheck prices a foundry against the scrap
     * ceiling; it already pins the exchange rate because scrap moving from $400
     * to $420 had turned it red once. World inflation is a second way for the
     * same number to move while the harness is looking the other way, and a
     * fixture that has not been checked for what else it is measuring is not
     * evidence.
     */
    public void pin() { pinned = true; }

    public boolean isPinned() { return pinned; }

    private boolean pinned;

    public void advanceMonth(int month) {
        if (pinned) return;
        Random roll = new Random(scramble(SEED + month));
        // Biased toward the bottom of the band - see LOW_BIAS. A flat draw put
        // the average at 4.5%, which is what was compounding into the numbers
        // Jerus objected to.
        double draw = MIN_INFLATION + Math.pow(roll.nextDouble(), LOW_BIAS)
                                    * (MAX_INFLATION - MIN_INFLATION);
        annualInflation = annualInflation * PERSISTENCE + draw * (1 - PERSISTENCE);

        priceLevel *= Math.pow(1 + annualInflation, 1.0 / 12);

        /*
         * ...and back toward trend, which is what keeps three centuries of a
         * 4.5% average from becoming a 2.4-million-fold price level. The pull
         * is gentle enough that a decade of high inflation is felt in full and
         * only a century of it is argued with.
         */
        monthsRun++;
        double onTrend = Math.pow(1 + TREND_INFLATION, monthsRun / 12.0);
        priceLevel += (onTrend - priceLevel) * TREND_PULL;
        if (priceLevel < 1e-6) priceLevel = 1e-6;
        recordLevel();
    }

    /** Where the trend says the world's prices should be by now. */
    public double trendLevel() {
        return Math.pow(1 + TREND_INFLATION, monthsRun / 12.0);
    }

    /** What the world's basket costs now against what it cost at founding. */
    public double getPriceLevel() { return priceLevel; }

    /**
     * The HEADLINE rate: what the band is doing this month.
     *
     * Not what the level is doing. The pull toward trend below is applied to
     * the level and not to this rate, so over a long run the two say different
     * things: across 4,000 months this read 3.3% a year on average while the
     * level rose from 1.0 to 1.84, which is 0.18% a year. The game's own
     * summary line printed both on the same line for weeks - "prices 1.841
     * since founding, inflating at 3.5%/yr" - and nobody put them together.
     * Fine for the screen, where the player is told what the world's prices
     * are doing right now. Wrong for anything that compounds it: see
     * realisedInflation().
     */
    public double getInflation() { return annualInflation; }

    /**
     * What the world's prices ACTUALLY did over the last twelve months, from
     * the level itself.
     *
     * THE CURRENCY WAS DRIFTING ON THE HEADLINE. ForeignAccounts moves the
     * rate by (local inflation - world inflation) every month, and Game was
     * handing it getInflation() for the world's half - a rate the level does
     * not follow. With local inflation near zero and "world inflation" reading
     * 3.3%, the currency appreciated about 3.2% a year, for ever, against a
     * world whose prices were not actually rising. Only the pull toward parity
     * resisted it, and the equilibrium sat 55% below purchasing-power parity
     * for the whole of a mature run.
     *
     * That fell entirely on the two sectors that earn in world money and pay
     * in local money. Their selling price was flat in dollars - steel $853 a
     * tonne at month 50 and $847 at month 4,000 - and their wage bill per tonne
     * in dollars nearly doubled, purely through the conversion. Heavy Industry
     * went from 28% of months at a loss to 60%, Mining from 55% to 96%, and it
     * was taken for a wage-price loop. It was a currency measured with two
     * instruments that disagreed.
     *
     * Measured with this passed instead: the rate ends 33% off parity instead
     * of 55%, and Heavy Industry does not borrow a cent in 4,000 months.
     *
     * A thirteen-month ring on the level, carried in the save. On a save from
     * before the ring existed it is back-cast from the headline, which is the
     * old answer for one year and the right one after that.
     */
    public double realisedInflation() {
        double yearAgo = levels[(head + 1) % LEVEL_RING];
        if (yearAgo <= 0) return annualInflation;
        return priceLevel / yearAgo - 1;
    }

    private static final int LEVEL_RING = 13;
    /** The level at the end of each of the last thirteen months; head is the newest. */
    private final double[] levels = new double[LEVEL_RING];
    private int head;

    // A fresh world has a year of history at its opening rate, so the first
    // twelve months read the headline rather than zero. Runs after the fields
    // above it, which is why it sits below them.
    { backcastLevels(); }

    private void recordLevel() {
        head = (head + 1) % LEVEL_RING;
        levels[head] = priceLevel;
    }

    /** Fills the ring as if the headline rate had held for a year. */
    private void backcastLevels() {
        double monthly = Math.pow(1 + annualInflation, 1.0 / 12);
        for (int back = 0; back < LEVEL_RING; back++) {
            levels[(head - back + LEVEL_RING) % LEVEL_RING] = priceLevel / Math.pow(monthly, back);
        }
    }

    /* -------------------------------- carrying -------------------------------- */

    public double[] toSaveArray() {
        double[] out = new double[3 + LEVEL_RING];
        out[0] = priceLevel;
        out[1] = annualInflation;
        out[2] = monthsRun;
        // Oldest first, so restore() can replay them in order.
        for (int back = LEVEL_RING - 1; back >= 0; back--) {
            out[3 + (LEVEL_RING - 1 - back)] = levels[(head - back + LEVEL_RING) % LEVEL_RING];
        }
        return out;
    }

    public void restore(double[] saved) {
        if (saved == null || saved.length < 2) return;
        if (saved[0] > 0) priceLevel = saved[0];
        annualInflation = saved[1];
        // The trend is measured from months run, which no other state implies.
        if (saved.length > 2) monthsRun = (int) Math.round(saved[2]);
        if (saved.length >= 3 + LEVEL_RING) {
            head = LEVEL_RING - 1;
            System.arraycopy(saved, 3, levels, 0, LEVEL_RING);
        } else {
            backcastLevels();
        }
    }

    public void reset() {
        priceLevel = 1.0;
        annualInflation = OPENING_INFLATION;
        monthsRun = 0;
        pinned = false;
        head = 0;
        java.util.Arrays.fill(levels, 0);
        backcastLevels();
    }
}

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

    /* =====================================================================
       THE WORLD'S PRICE LEVEL IS STATIONARY, AND THE MEAN SAYS WHERE

       Nobody had written this down and it is the whole mechanism. advanceMonth()
       compounds the level and then pulls it back toward a trend line, and
       TREND_INFLATION is zero - so the trend line is FLAT and the level settles
       wherever the two forces balance:

           mean/12 x L  =  TREND_PULL x (L - 1)

       At the old mean of 3.33% - the band was [1%, 8%] with a low bias, and
       E[U^2] is a third - that solves to L = 1.835. Measured over a full run:
       1.841. So the world was not inflating at all. Its price level reached
       1.84 early in every game and sat there for three centuries, which is why
       the headline read 3.1% while realised read 0.0%.

       That mattered because parity is the city's prices over the WORLD's, and
       the city cannot inflate: a world stuck at 1.84 is a permanent 1.84x
       handicap that no domestic instrument can answer. Measured over sixteen
       paired seeds, moving the mean to 1% takes the level to 1.151 - the
       arithmetic predicts 1.16 - and the currency from 0.372 to 0.607, better
       in SIXTEEN SEEDS OUT OF SIXTEEN. It is the first change in this whole
       line of work that helped the exchange rate at all.

       AND THE BAND IS CENTRED NOW, which is what retires LOW_BIAS. The bias
       existed to drag a [1%, 8%] band's mean down toward its floor; a band
       centred on the mean does not need dragging. It is also two-sided, so the
       world sometimes DEFLATES and the city imports a price rise for once,
       instead of every shock pointing the same way.
       ===================================================================== */

    /** What the world's inflation averages. Jerus's number, and the default. */
    public static final double DEFAULT_MEAN_INFLATION = .01;

    /** How far either side of the mean a draw can land. */
    public static final double INFLATION_SPREAD = .03;

    /** The most and least a city may be founded with. Testing room, not policy. */
    public static final double MIN_MEAN_INFLATION = .0;
    public static final double MAX_MEAN_INFLATION = .08;

    /**
     * The worlds the founding screen offers, as its five chips: none, the
     * default, twice it, the mean every city grew up in before 2026-09-13
     * (LEGACY_MEAN_INFLATION), and five. On Settings until 0.7.10, which moved
     * them to the screen a city is founded on - the only moment they act.
     */
    public static final double[] FOUNDING_CHOICES = { 0, DEFAULT_MEAN_INFLATION, .02, .0333, .05 };

    /**
     * Where the world's price level settles at a given mean.
     *
     * Closed form, because the level IS a closed form: advanceMonth()
     * compounds at the mean and pulls back toward a flat trend, so it rests
     * where `mean/12 x L = TREND_PULL x (L - 1)` - the block above, the same
     * arithmetic that predicted 1.84, 1.37 and 1.15 before any of them were
     * measured. The founding screen's note under the chips; in the
     * interface's window until 0.7.10, which is a screen computing a model
     * figure, so it lives here now.
     */
    public static double settledLevelAt(double mean) {
        double monthly = Math.pow(1 + Math.max(0, mean), 1.0 / 12) - 1;
        double denom = TREND_PULL - monthly;
        return denom <= 1e-9 ? 99 : TREND_PULL / denom;
    }

    private double meanInflation = DEFAULT_MEAN_INFLATION;

    /**
     * Sets the world a city is founded into.
     *
     * AT FOUNDING ONLY - Jerus: "in settings have it be adjustable but only in
     * game start". The world a city grew up in is a fact about that city, not a
     * preference somebody can change in year two: every price, every wage and
     * the whole exchange rate history were struck against it. So the founding
     * screen carries the choice (on Settings, in GamePrefs, until 0.7.10),
     * the founding applies it (Game.buildWorld(), from Founding), the save
     * carries it, and nothing moves it afterwards.
     */
    public void setMeanInflation(double mean) {
        meanInflation = Math.max(MIN_MEAN_INFLATION, Math.min(MAX_MEAN_INFLATION, mean));
        annualInflation = meanInflation;
    }

    public double getMeanInflation() { return meanInflation; }

    /** The band this world's inflation wanders in: the mean, either way. */
    public double minInflation() { return meanInflation - INFLATION_SPREAD; }
    public double maxInflation() { return meanInflation + INFLATION_SPREAD; }

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
     *
      * RETIRED 2026-09-13, kept because the reasoning is still true of the band
      * it was written for. It dragged a [1%, 8%] draw's mean down toward the
      * floor; the band is centred on the mean now and does not need dragging.
      * See the block at the top of this class.
      */
    public static final double LOW_BIAS = 2.0;

    private static final long SEED = 0x5F3A91C7L;

    private double annualInflation = DEFAULT_MEAN_INFLATION;
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
        // Flat, on a band centred at the mean - see LOW_BIAS, which this
        // retired, and the block at the top of the class.
        double draw = minInflation() + roll.nextDouble()
                                    * (maxInflation() - minInflation());
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
     * THE CURRENCY WAS DRIFTING ON THE HEADLINE. ForeignAccounts moved the
     * rate by (local inflation - world inflation) every month (until 0.7.2,
     * when that drift was deleted and this became the world's half of the
     * real rate differential instead), and Game was handing it getInflation()
     * for the world's half - a rate the level does not follow. With local
     * inflation near zero and "world inflation" reading 3.3%, the currency
     * appreciated about 3.2% a year, for ever, against a world whose prices
     * were not actually rising. Only the pull toward parity resisted it, and
     * the equilibrium sat 55% below purchasing-power parity for the whole of
     * a mature run.
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
        double[] out = new double[4 + LEVEL_RING];
        out[0] = priceLevel;
        out[1] = annualInflation;
        out[2] = monthsRun;
        // Last, so a save written before this existed still reads back: the
        // ring is found by length and an older array simply has no slot here.
        out[3 + LEVEL_RING] = meanInflation;
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
        /*
         * A CITY FOUNDED BEFORE THIS EXISTED KEEPS THE WORLD IT GREW UP IN.
         * Its whole price history was struck against a 3.33% mean, and handing
         * it a 1% world on load would move parity under it by a factor of 1.6
         * between one month and the next.
         */
        meanInflation = saved.length > 3 + LEVEL_RING
                ? Math.max(MIN_MEAN_INFLATION,
                           Math.min(MAX_MEAN_INFLATION, saved[3 + LEVEL_RING]))
                : LEGACY_MEAN_INFLATION;
    }

    /** The mean every city founded before 2026-09-13 grew up in. See restore(). */
    public static final double LEGACY_MEAN_INFLATION = .0333;

    public void reset() {
        priceLevel = 1.0;
        annualInflation = meanInflation;
        monthsRun = 0;
        pinned = false;
        head = 0;
        java.util.Arrays.fill(levels, 0);
        backcastLevels();
    }
}

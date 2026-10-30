package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Proves the D$ is a float that follows its parity and swings like a real
 * currency (XR1, 2026-10-21): parity is the basket with rent held to a third,
 * the trend is followed with no lag, the market is a seeded swing of a real
 * float's size, a real rate gap holds a level, the balance and the vault's
 * cover add theirs, interventions move the rate at the real size, one seed is
 * one path across a save, and in a real city the mechanics fire. Not part of
 * the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-10-21: "the current one is wierd". The study
 * (docs/studies/spec-exchange-rates.md, section 1) found why, in four lines:
 * the D$ moved 1.2% a year where a float moves 8-12%, in runs of up to 78
 * months one way; a pull toward a parity that keeps moving settles at
 * k / (k + g) of it - 0.55 at 4% a year - for ever; parity was 41% to 80% rent,
 * which no border arbitrages, so the D$ sat 20-34% dearer than the traded
 * basket; and the treasury's purchases of dollars made it dearer, not cheaper.
 * What replaced the rule is ForeignAccounts, THE FLOAT: every piece a level,
 * nothing a speed, each a line of its own. CurrencyCheck restates the
 * old defence and real-rate premises on it; this is the float itself, from the
 * design's own figures (the study's section 3, and its prototype beside it, in docs/studies/exchange-rates/).
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition rather than waiting for it:
 *
 *   0. PARITY IS THE BASKET WITH RENT HELD TO A THIRD. A bare PriceIndex whose
 *      spending puts rent at 60% of the basket: once based, rent weighs
 *      RENT_CAP in the level parity is struck from and the others their
 *      weights scaled to fill the rest; rent's price doubled moves the parity
 *      level by the capped weights and the index by the whole ones; at the
 *      next rebasing the parity level does not jump; a basket with rent under
 *      the cap has parity level and index the same.
 *   1. PARITY'S TREND IS FOLLOWED WITH NO LAG. A quiet market, trade in
 *      balance, the vault full and no gap: parity rising 4% a year for fifty
 *      years leaves rate / parity within 2% of one from year ten (a pull of
 *      .004 would have left .55); a burst of 50% in four years then 2.5% a
 *      year keeps it within 5% from month twelve.
 *   2. THE MARKET IS A REAL FLOAT'S. One seeded currency, the fundamentals
 *      still, twelve thousand months: the monthly move's standard deviation is
 *      MARKET_SIGMA x sqrt(1 + (1 - rho) / (1 + rho)) and a year's about 8%;
 *      the market's own half-life, measured as an economist would, is
 *      MARKET_HALF_LIFE; the monthly moves are a random walk at a month. At
 *      half of OPEN_ENOUGH the rate carries half of the market, and below
 *      MIN_TRADE of trade none of it.
 *   3. THE REAL RATE HOLDS A LEVEL, 60% AT ONCE AND THE REST WITHIN A YEAR OR
 *      TWO. A gap held 240 months puts the rate where gapLevelFor() says; a
 *      step in it shows GAP_IMPACT and a share of the rest in its month and
 *      halves what is still to come every GAP_BUILD_HALF_LIFE months; the
 *      level is capped; slot 3's widest gap holds the D$ a quarter dearer; a
 *      gap that closes gives the gain back.
 *   4. THE BALANCE AND THE VAULT'S COVER. A deficit of a tenth of GDP arrives
 *      at BALANCE_WEIGHT x 0.1, half in its first month; a deficit of GDP's
 *      whole at BALANCE_CAP; a surplus the other way; capital sent abroad that
 *      recycles a surplus leaves nothing. An empty vault costs RISK_PREMIUM, a
 *      vault of THIN_COVER months nothing, of half of it half.
 *   5. INTERVENTIONS MOVE THE RATE AT THE REAL SIZE. A purchase of 1% of a
 *      year's GDP moves it INTERVENTION_IMPACT x 1%, half of it still felt six
 *      months on; a sale the other way; the twins of the study's 1.4 - one
 *      buys a year's imports of dollars - part the other way from the 7.7%
 *      dearer the old rule left; and the defence's sale lifts the rate as the
 *      treasury's sale of the same amount does.
 *   6. ONE SEED, ONE PATH. The same seed and months are the same rates to the
 *      bit, a different seed parts; two cities on the default founding play
 *      the same rates, one on another world seed does not.
 *   7. A SAVE MID-SWING CONTINUES EXACTLY. A seeded currency saved with its
 *      market moving, an intervention fading and a purchase pending plays on
 *      to the bit as the one it came from; a city saved and loaded keeps its
 *      seed, its draws, its trend, its market and its rate, and plays on
 *      beside the city it was saved from.
 *   8. IN A REAL CITY THE D$ MOVES LIKE A FLOAT, AND THE MECHANICS FIRE. The
 *      default playtest's city, 360 months: the six moves add to the month's
 *      move every month; the defence sells, the treasury deals, the market
 *      moves the rate, and the counts are printed against the old rule's;
 *      the volatility a year is a float's (3% to 20%, where the old rule's
 *      was 1.2%) and no run one way is as long as the old rule's 78 months.
 *      The playtest's own counts of the same agree with the harness's.
 *   9. THE GAME SURVIVES THE SPIRAL (Jerus, 2026-10-21: "ship it as is, im
 *      going to test it to see if player led behaviour can prevent
 *      hyperinflation"). The default city hyperinflates under XR1, so it must
 *      live through it: a city whose rate is pinned at its guard keeps every
 *      figure finite, the audit closing, a save and a load; and a currency
 *      driven past its guard holds it exactly with every move finite.
 *
 * Numbering is the running order. Money is in thousands, as everywhere.
 *
 * @author Jerus
 */
public class ExchangeRateCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %,.12f != %,.12f%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    /** Runs a piece of work with the game's narration sent nowhere. */
    static void quietly(Runnable work) {
        PrintStream was = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(was); }
    }

    /** The trade a quiet currency settles on, each way, and the output it is weighed against: openness one. */
    static final double TRADE = 3_000, GDP = 6_000;

    /** The market's seed in the sections that draw one; the others are quiet. */
    static final long SEED = 20_261_021L;

    /** The old pull toward parity, REVERSION, retired by XR1 (WHAT XR1 RETIRED): a month's share of the distance it closed. */
    static final double OLD_PULL = .004;

    /** A currency past settling on trade in balance, its vault at twice THIN_COVER (the purchase forgotten, a set-up and not a deal), its market seeded unless the seed is 0 (quiet). */
    static ForeignAccounts still(double exports, double imports, double gdp, long seed) {
        ForeignAccounts fx = CurrencyCheck.settled(exports, imports, gdp);
        fx.buyReserves(fx.monthlyImports() * 2 * ForeignAccounts.THIN_COVER);
        fx.forgetDealing();
        if (seed != 0) fx.setMarketSeed(seed);
        return fx;
    }

    /** ...at the standard trade, and quiet. */
    static ForeignAccounts calm() { return still(TRADE, TRADE, GDP, 0); }

    /** One month of the standard trade, repriced. */
    static void month(ForeignAccounts fx) {
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
        fx.repriceCurrency();
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        parityIsTheCappedBasket();
        theTrendIsFollowed();
        theMarketIsAFloats();
        theRealRateHoldsALevel();
        theBalanceAndTheCover();
        interventionsAtTheRealSize();
        oneSeedOnePath();
        aSaveMidSwing();
        inARealCity();
        theGameSurvivesTheSpiral();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= 0. parity is the basket with rent held to a third ================= */

    /** The four fees' prices a fixture's basket is fed: all at 1. */
    static final double[] FEES = { 1, 1, 1, 1 };
    /** Spending that puts rent at 60% of the basket (groceries 15, meals 10, luxury 5, services 10 - the fee lines add to it), and spending that puts it at 30%. */
    static final double[] RENT_HEAVY = { .15, .60, .10, .05, 0 }, RENT_HEAVY_FEES = { .04, .03, .02, .01 };
    static final double[] RENT_LIGHT = { .30, .30, .20, .05, 0 }, RENT_LIGHT_FEES = { .06, .04, .03, .02 };

    static void feed(PriceIndex px, double[] prices, double[] spends, double[] feeSpends, int month) {
        px.takeMonth(prices, spends, FEES, feeSpends, month);
    }

    static void parityIsTheCappedBasket() {
        out.println("--- 0. parity is the basket with rent held to a third ---");

        PriceIndex px = new PriceIndex();
        final double[] flat = { 1, 1, 1, 1, 0 };
        int month = 0;
        for (; month < PriceIndex.SETTLING_MONTHS; month++) feed(px, flat, RENT_HEAVY, RENT_HEAVY_FEES, month);
        double rentWeight = px.getWeight(PriceIndex.RENT);
        assertTrue("fixture: the basket is based", px.isBased());
        close("fixture: the households spent 60% of the basket on rent", rentWeight, .60, 1e-12);
        assertTrue("fixture: rent weighs more than the cap, so the level parity is struck from holds it down",
                px.isRentCapped() && rentWeight > PriceIndex.RENT_CAP);
        close("fixture: at the base month parity's level and the index both read one",
                px.getParityLevel() + px.getIndex(), 2, 1e-12);

        // Rent at the cap and every other weight scaled to fill what is left, in their own proportions.
        double scale = (1 - PriceIndex.RENT_CAP) / (1 - rentWeight);
        boolean weightsHold = true;
        double sum = 0;
        StringBuilder shown = new StringBuilder();
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) {
            double capped = px.getParityWeight(k);
            double expected = k == PriceIndex.RENT ? PriceIndex.RENT_CAP : px.getWeight(k) * scale;
            weightsHold &= Math.abs(capped - expected) <= 1e-12;
            sum += capped;
            shown.append(String.format(" %s %.4f (the index's %.4f)", PriceIndex.COMPONENT_NAMES[k], capped, px.getWeight(k)));
        }
        out.println("   parity's weights:" + shown);
        assertTrue("once based, rent weighs RENT_CAP in parity and the others their weights scaled to fill the rest", weightsHold);
        close("...and parity's weights add to what the basket's do", sum, 1, 1e-12);

        // Rent's price doubled.
        feed(px, new double[] { 1, 2, 1, 1, 0 }, RENT_HEAVY, RENT_HEAVY_FEES, month++);
        close("rent doubled moves parity's level by rent's capped weight", px.getParityLevel(),
                1 + px.getParityWeight(PriceIndex.RENT) * (2 - 1), 1e-12);
        close("...and the index by rent's whole weight", px.getIndex(), 1 + px.getWeight(PriceIndex.RENT) * (2 - 1), 1e-12);
        assertTrue("...so parity's level is the lower: the cap held rent down", px.getParityLevel() < px.getIndex());
        out.printf("   rent doubled: parity %.6f, the index %.6f%n", px.getParityLevel(), px.getIndex());
        // ...and the others weigh more than they do in the index.
        feed(px, new double[] { 2, 1, 1, 1, 0 }, RENT_HEAVY, RENT_HEAVY_FEES, month++);
        close("groceries doubled moves parity's level by groceries' scaled-up weight", px.getParityLevel(),
                1 + px.getParityWeight(PriceIndex.GROCERIES), 1e-12);
        close("...the index by groceries' own", px.getIndex(), 1 + px.getWeight(PriceIndex.GROCERIES), 1e-12);
        assertTrue("...so the traded prices weigh more in parity than in the index",
                px.getParityLevel() > px.getIndex());

        // The next rebasing: rent doubled held until the month before it, and the last year's spending is rent at 30%.
        final double[] rent2 = { 1, 2, 1, 1, 0 };
        int rebase = px.getLinkedMonth() + PriceIndex.REBASE_MONTHS;
        for (; month < rebase; month++) {
            boolean light = month >= rebase - PriceIndex.WEIGHT_MONTHS + 1;
            feed(px, rent2, light ? RENT_LIGHT : RENT_HEAVY, light ? RENT_LIGHT_FEES : RENT_HEAVY_FEES, month);
        }
        double parityBefore = px.getParityLevel(), indexBefore = px.getIndex();
        double[] cappedOld = new double[PriceIndex.COMPONENTS], wholeOld = new double[PriceIndex.COMPONENTS];
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) { cappedOld[k] = px.getParityWeight(k); wholeOld[k] = px.getWeight(k); }
        assertTrue("fixture: the basket in force is still the old one the month before the rebasing",
                px.getLinkedMonth() < rebase && px.isRentCapped());
        // The rebasing month, with every price moved, so the level it reads is a real test of continuity.
        final double[] moved = { 1.2, 2.5, 0.9, 1.5, 0 };
        feed(px, moved, RENT_LIGHT, RENT_LIGHT_FEES, month++);
        assertTrue("fixture: the basket was struck again this month, on the year's spending", px.getLinkedMonth() == rebase);
        close("...and rent is 30% of the new basket", px.getWeight(PriceIndex.RENT), .30, 1e-12);
        double expectedCapped = 0, expectedWhole = 0;
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) {
            double relative = k == PriceIndex.SERVICES ? 1 : moved[k];
            expectedCapped += cappedOld[k] * relative;
            expectedWhole += wholeOld[k] * relative;
        }
        out.printf("   the month before the rebasing parity read %.6f (the index %.6f); the rebasing month's prices on the old basket"
                + " read %.6f (%.6f); the new basket opens at %.6f (%.6f)%n", parityBefore, indexBefore, expectedCapped,
                expectedWhole, px.getParityLevel(), px.getIndex());
        close("at the rebasing the parity level is the old basket's, rent capped, that month: it does not jump",
                px.getParityLevel(), expectedCapped, 1e-12);
        close("...and so is the index", px.getIndex(), expectedWhole, 1e-12);
        double parityAt = px.getParityLevel();
        feed(px, moved, RENT_LIGHT, RENT_LIGHT_FEES, month++);
        close("the month after, the prices unchanged, the new basket reads the level it opened at", px.getParityLevel(), parityAt, 1e-12);
        assertTrue("...and rent, under the cap in the new basket, weighs what the households spent on it in parity too",
                !px.isRentCapped() && Math.abs(px.getParityWeight(PriceIndex.RENT) - px.getWeight(PriceIndex.RENT)) <= 1e-12);

        // A basket with rent under the cap: parity's level is the index.
        PriceIndex light = new PriceIndex();
        double worst = 0;
        for (int m = 0; m < 60; m++) {
            double[] prices = { 1 + .004 * m, 1 + .01 * Math.sin(m / 5.0) + .003 * m, 1 + .002 * m, 1 + .006 * m, 0 };
            feed(light, prices, RENT_LIGHT, RENT_LIGHT_FEES, m);
            if (light.isBased()) worst = Math.max(worst, Math.abs(light.getParityLevel() - light.getIndex()));
        }
        assertTrue("fixture: a basket with rent at 30%, based, and prices that moved", light.isBased() && !light.isRentCapped()
                && light.getIndex() > 1.05);
        close("a basket with rent under the cap: parity's level is the index, every month", worst, 0, 1e-12);
    }

    /* ================= 1. parity's trend is followed with no lag ================= */

    static void theTrendIsFollowed() {
        out.println("\n--- 1. parity's trend is followed, with no lag ---");

        ForeignAccounts steady = calm();
        assertTrue("fixture: the market is quiet, the vault holds THIN_COVER months or more, trade is in balance and the gate is open",
                !steady.isMarketSeeded() && steady.importCover() >= ForeignAccounts.THIN_COVER && steady.balanceTarget() == 0
                        && Math.abs(steady.gate() - 1) < 1e-12);
        double level = 1, monthly = Math.pow(1.04, 1 / 12.0), worst = 0, atTen = 0, atFifty = 0;
        boolean still = true;
        for (int m = 1; m <= 600; m++) {
            level *= monthly;
            steady.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            steady.setParity(level, 1.0, .04, 0);
            steady.setRealRateDifferential(0);
            still &= steady.coverTarget() == 0 && steady.balanceTarget() == 0 && steady.monthDeficitUsd() == 0;
            steady.repriceCurrency();
            double ratio = steady.getRate() / steady.getParity();
            if (m >= 120) worst = Math.max(worst, Math.abs(ratio - 1));
            if (m == 120) atTen = ratio;
            if (m == 600) atFifty = ratio;
            still &= steady.getDefenceUsd() == 0;
        }
        assertTrue("fixture: for fifty years nothing but parity moved the currency: no deficit, no premium, no sale", still);
        double g = Math.pow(1.04, 1 / 12.0) - 1;
        out.printf("   parity +4%% a year for 50 years: rate / parity %.4f at year 10, %.4f at year 50, the worst from year 10 %.4f;"
                + " a pull of %.3f a month would have left k / (k + g) = %.2f%n", atTen, atFifty, 1 + worst, OLD_PULL,
                OLD_PULL / (OLD_PULL + g));
        assertTrue("from year ten the rate stays within 2% of parity", worst < .02);
        assertTrue("...where the old pull would have left it at k / (k + g): " + String.format("%.2f", OLD_PULL / (OLD_PULL + g)),
                Math.abs(atFifty - 1) < .02 && OLD_PULL / (OLD_PULL + g) < .6);

        // A burst: parity +50% in 48 months, then 2.5% a year.
        ForeignAccounts burst = calm();
        level = 1;
        double lo = Double.MAX_VALUE, hi = 0;
        for (int m = 1; m <= 240; m++) {
            level *= m <= 48 ? Math.pow(1.5, 1 / 48.0) : Math.pow(1.025, 1 / 12.0);
            burst.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            burst.setParity(level, 1.0, .03, 0);
            burst.repriceCurrency();
            if (m >= 12) {
                double ratio = burst.getRate() / burst.getParity();
                lo = Math.min(lo, ratio);
                hi = Math.max(hi, ratio);
            }
        }
        out.printf("   a burst, parity +50%% in 48 months then +2.5%% a year: rate / parity from month 12 between %.3f and %.3f"
                + " (the prototype's 0.97-1.02)%n", lo, hi);
        assertTrue("through a burst the rate stays within 5% of parity from month twelve", lo >= .95 && hi <= 1.05);
    }

    /* ================= 2. the market is a real float's ================= */

    /** A path's monthly log moves of the rate: the first from the founding rate of one. */
    static double[] moves(double[] lnRate) {
        double[] d = new double[lnRate.length];
        for (int t = 0; t < d.length; t++) d[t] = lnRate[t] - (t == 0 ? 0 : lnRate[t - 1]);
        return d;
    }

    static double mean(double[] x, int from) {
        double s = 0;
        for (int t = from; t < x.length; t++) s += x[t];
        return s / (x.length - from);
    }

    /** The population standard deviation of x from `from` on. */
    static double sd(double[] x, int from) {
        double m = mean(x, from), s = 0;
        for (int t = from; t < x.length; t++) s += (x[t] - m) * (x[t] - m);
        return Math.sqrt(s / (x.length - from));
    }

    /** The first autocorrelation of x from `from` on. */
    static double autocorrelation(double[] x, int from) {
        double m = mean(x, from), num = 0, den = 0;
        for (int t = from; t < x.length; t++) {
            den += (x[t] - m) * (x[t] - m);
            if (t > from) num += (x[t] - m) * (x[t - 1] - m);
        }
        return num / den;
    }

    /** One path's AR(1) sums, [cross-product, lag's squares], both about the path's own means: pooled across paths they give a slope with a smaller error than one path's. */
    static double[] arSums(double[] x, int from) {
        double m1 = 0, m2 = 0;
        int n = x.length - from - 1;
        for (int t = from + 1; t < x.length; t++) { m1 += x[t - 1]; m2 += x[t]; }
        m1 /= n;
        m2 /= n;
        double cross = 0, lag = 0;
        for (int t = from + 1; t < x.length; t++) {
            cross += (x[t - 1] - m1) * (x[t] - m2);
            lag += (x[t - 1] - m1) * (x[t - 1] - m1);
        }
        return new double[] { cross, lag };
    }

    /** A still currency's path under a market: [ln rate, the market, ln of parity's trend], a month each. */
    static double[][] swing(ForeignAccounts fx, double exports, double imports, double gdp, int months) {
        double[] ln = new double[months], market = new double[months], trend = new double[months];
        for (int t = 0; t < months; t++) {
            fx.takeMonth(CurrencyCheck.month(exports, imports, 0, 0), gdp);
            fx.repriceCurrency();
            ln[t] = Math.log(fx.getRate());
            market[t] = fx.getMarket();
            trend[t] = Math.log(fx.getParityTrend());
        }
        return new double[][] { ln, market, trend };
    }

    static void theMarketIsAFloats() {
        out.println("\n--- 2. the market is a real float's ---");

        final int months = 12_000, burnIn = 120;
        final double rho = Math.pow(.5, 1 / ForeignAccounts.MARKET_HALF_LIFE);
        // THE MONTHLY MOVE OF A STILL CURRENCY. The fundamentals are still (parity flat, gap and balance nothing, the vault
        // full, the gate open), so ln rate = m, the market: m(t) = rho m(t-1) + s e(t), e a standard normal. Its variance
        // is V = s^2 / (1 - rho^2), and the monthly move d(t) = m(t) - m(t-1) has Var(d) = 2 V (1 - rho) = 2 s^2 / (1 + rho)
        // = s^2 (1 + (1 - rho) / (1 + rho)): a little over s, because the swing fades as well as arrives. Cov(d(t), d(t+1))
        // = -V (1 - rho)^2, a first autocorrelation of -(1 - rho) / 2: a random walk at a month (R9).
        final double expectedSd = ForeignAccounts.MARKET_SIGMA * Math.sqrt(1 + (1 - rho) / (1 + rho));

        ForeignAccounts fx = still(TRADE, TRADE, GDP, SEED);
        assertTrue("fixture: the market is seeded and the fundamentals still: gate open, no balance, no premium, no gap",
                fx.isMarketSeeded() && Math.abs(fx.gate() - 1) < 1e-12 && fx.balanceTarget() == 0 && fx.coverTarget() == 0
                        && fx.getRealRateDifferential() == 0);
        double[][] path = swing(fx, TRADE, TRADE, GDP, months);
        double worstGap = 0;
        for (int t = 0; t < months; t++) worstGap = Math.max(worstGap, Math.abs(path[0][t] - path[1][t]));
        close("fixture: nothing but the market moved the rate (ln rate is the market, every month)", worstGap, 0, 1e-12);
        assertTrue("fixture: the market took a draw a month", fx.getMarketDraws() == months);
        double[] d = moves(path[0]);
        double sdMove = sd(d, burnIn), vol = sdMove * Math.sqrt(12), ac = autocorrelation(d, burnIn);
        out.printf("   %,d months: the monthly move's s.d. %.5f against %.5f derived (MARKET_SIGMA %.3f); a year's volatility %.1f%%"
                + " (about 8%%); the first autocorrelation of the moves %+.4f (derived %+.4f)%n", months, sdMove, expectedSd,
                ForeignAccounts.MARKET_SIGMA, vol * 100, ac, -(1 - rho) / 2);
        assertTrue("the monthly move's standard deviation is MARKET_SIGMA x sqrt(1 + (1 - rho) / (1 + rho)), within 5%",
                Math.abs(sdMove / expectedSd - 1) < .05);
        assertTrue("...and a year's volatility is about 8% (the dial's own sentence)", Math.abs(vol - .08) < .01);
        assertTrue("the monthly moves are a random walk at a month: the first autocorrelation is within 0.1 of nothing",
                Math.abs(ac) < .1);

        // The half-life, as an economist would measure it: the slope of the market on its last month, pooled over five
        // paths (one path's slope has an error of a tenth of the half-life at this length; five bring it to a twentieth).
        double cross = 0, lag = 0;
        StringBuilder each = new StringBuilder();
        for (int s = 0; s < 5; s++) {
            double[] market = s == 0 ? path[1] : swing(still(TRADE, TRADE, GDP, SEED + s), TRADE, TRADE, GDP, months)[1];
            double[] sums = arSums(market, burnIn);
            cross += sums[0];
            lag += sums[1];
            each.append(String.format(" %.1f", Math.log(.5) / Math.log(sums[0] / sums[1])));
        }
        double slope = cross / lag, halfLife = Math.log(.5) / Math.log(slope);
        out.printf("   the AR(1) slope of the market on its last month %.5f (rho %.5f): a half-life of %.1f months"
                + " (MARKET_HALF_LIFE %.0f); the five paths' own:%s%n", slope, rho, halfLife, ForeignAccounts.MARKET_HALF_LIFE, each);
        assertTrue("the half-life a regression of the market on its last month gives is MARKET_HALF_LIFE, within 15%",
                Math.abs(halfLife / ForeignAccounts.MARKET_HALF_LIFE - 1) < .15);

        // The gate: half of OPEN_ENOUGH, exports and imports a twentieth of GDP between them.
        final int gated = 600;
        ForeignAccounts half = still(GDP / 40, GDP / 40, GDP, SEED);
        close("fixture: exports and imports make a twentieth of GDP, half of OPEN_ENOUGH", half.getOpenness(), ForeignAccounts.OPEN_ENOUGH / 2, 1e-12);
        assertTrue("fixture: ...so the gate is a half, with the trade over MIN_TRADE",
                Math.abs(half.gate() - .5) < 1e-12 && half.monthlyExports() + half.monthlyImports() > ForeignAccounts.MIN_TRADE);
        double[][] halfPath = swing(half, GDP / 40, GDP / 40, GDP, gated);
        double worstHalf = 0, worstSame = 0;
        for (int t = 0; t < gated; t++) {
            worstHalf = Math.max(worstHalf, Math.abs(halfPath[0][t] - halfPath[2][t] - half.gate() * halfPath[1][t]));
            worstSame = Math.max(worstSame, Math.abs(halfPath[1][t] - path[1][t]));
        }
        close("at half of OPEN_ENOUGH the rate sits off its trend by gate() of the market, exactly half of it", worstHalf, 0, 1e-12);
        close("...the market itself the same path, the gate only weighing it", worstSame, 0, 1e-12);

        // Below MIN_TRADE of trade the rate is the trend, whatever the openness.
        ForeignAccounts thin = still(20, 20, 100, SEED);
        assertTrue("fixture: the economy is open (trade 40% of output) but trades under MIN_TRADE a month",
                thin.getOpenness() >= ForeignAccounts.OPEN_ENOUGH
                        && thin.monthlyExports() + thin.monthlyImports() <= ForeignAccounts.MIN_TRADE);
        double[][] thinPath = swing(thin, 20, 20, 100, gated);
        double worstThin = 0, widest = 0;
        for (int t = 0; t < gated; t++) {
            worstThin = Math.max(worstThin, Math.abs(thinPath[0][t] - thinPath[2][t]));
            widest = Math.max(widest, Math.abs(thinPath[1][t]));
        }
        assertTrue("fixture: ...while the market swings (its widest " + String.format("%.3f", widest) + ")", widest > .01 && thin.gate() == 0);
        close("below MIN_TRADE of trade the rate is the trend, every month", worstThin, 0, 1e-12);
    }

    /* ================= 3. the real rate holds a level ================= */

    static void theRealRateHoldsALevel() {
        out.println("\n--- 3. the real rate holds a level: 60% at once and the rest within a year or two ---");

        // A gap held for twenty years.
        for (double gap : new double[] { .02, -.02 }) {
            ForeignAccounts fx = calm();
            for (int m = 0; m < 240; m++) {
                fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
                fx.setRealRateDifferential(gap);
                fx.repriceCurrency();
            }
            double level = ForeignAccounts.gapLevelFor(gap);
            assertTrue("fixture: a gap of " + String.format("%+.0f", gap * 100) + " points, held, the rest still",
                    Math.abs(level) > .02 && Math.abs(fx.gate() - 1) < 1e-12 && fx.getBalanceReached() == 0 && fx.getCoverReached() == 0);
            close("held " + String.format("%+.0f", gap * 100) + " points for 240 months: the gap's level reached is gapLevelFor(gap)",
                    fx.getGapReached(), level, 1e-9);
            close("...and the rate is parity's trend x exp(gate x that level)", fx.getRate(),
                    fx.getParityTrend() * Math.exp(fx.gate() * level), 1e-9);
            assertTrue("...a gap over the world's real rate holds the D$ dearer, one under it cheaper", (gap > 0) == (fx.getRate() < fx.getParityTrend()));
        }

        // A step in the gap.
        final double step = .02, k = 1 - Math.pow(.5, 1 / ForeignAccounts.GAP_BUILD_HALF_LIFE), L = ForeignAccounts.gapLevelFor(step);
        ForeignAccounts fx = calm();
        for (int m = 0; m < 12; m++) {
            fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            fx.setRealRateDifferential(0);
            fx.repriceCurrency();
        }
        assertTrue("fixture: twelve months with no gap: nothing reached, the rate at parity",
                fx.getGapReached() == 0 && Math.abs(fx.getRate() - 1) < 1e-12);
        fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
        fx.setRealRateDifferential(step);
        fx.repriceCurrency();
        close("a step in the gap: that month's move from the gap is (GAP_IMPACT + k (1 - GAP_IMPACT)) x the step's level",
                fx.getMoveGap(), (ForeignAccounts.GAP_IMPACT + k * (1 - ForeignAccounts.GAP_IMPACT)) * L, 1e-12);
        assertTrue("...which is at least GAP_IMPACT of the level, at once", Math.abs(fx.getMoveGap()) >= ForeignAccounts.GAP_IMPACT * Math.abs(L));
        double toCome = fx.getGapTarget() - fx.getGapReached();
        for (int m = 0; m < 9; m++) {
            fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            fx.setRealRateDifferential(step);
            fx.repriceCurrency();
        }
        double toComeLater = fx.getGapTarget() - fx.getGapReached();
        close("nine months on (GAP_BUILD_HALF_LIFE), what is still to come is half what was still to come after the step's month",
                toComeLater, toCome / 2, 1e-9);
        double nineOn = fx.getGapReached() / L;
        for (int m = 0; m < 14; m++) {
            fx.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            fx.setRealRateDifferential(step);
            fx.repriceCurrency();
        }
        out.printf("   a step of %.0f points (level %+.4f): %.0f%% of it in its month, %.0f%% ten months in, %.0f%% two years in%n", step * 100, L,
                100 * (1 - toCome / L), 100 * nineOn, 100 * fx.getGapReached() / L);
        assertTrue("...and within two years of the step 90% of the level has arrived", fx.getGapReached() / L >= .9);

        // The cap.
        boolean capped = true, odd = true, falling = true;
        double previous = 0;
        for (int p = 1; p <= 100; p++) {
            double gap = p / 100.0, level = ForeignAccounts.gapLevelFor(gap);
            capped &= Math.abs(level) <= ForeignAccounts.GAP_CAP && Math.abs(ForeignAccounts.gapLevelFor(-gap) + level) <= 1e-15;
            falling &= level <= previous;
            previous = level;
        }
        close("the level never passes GAP_CAP: a gap of 100 points is held at the cap", ForeignAccounts.gapLevelFor(1), -ForeignAccounts.GAP_CAP, 1e-5);
        assertTrue("...for every gap from 1 to 100 points, either way, and a wider gap never holds the D$ less dear", capped && falling);
        double widest = 1 - Math.exp(ForeignAccounts.gapLevelFor(.169));
        out.printf("   slot 3's widest gap, 16.9 points: the D$ %.1f%% dearer than parity's trend (the study's 25%%)%n", widest * 100);
        assertTrue("slot 3's widest gap, 16.9 points, holds the D$ 24-27% dearer", widest >= .24 && widest <= .27);

        // A rise appreciates at once and gives the gain back as the gap closes.
        ForeignAccounts rise = calm();
        double lowest = Double.MAX_VALUE, afterClose = 0, firstMonth = 0;
        int lowestAt = -1;
        double worstLate = 0;
        final double three = .03, closeAt = 24;
        for (int m = 1; m <= 24 + 120; m++) {
            rise.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            rise.setRealRateDifferential(m <= closeAt ? three : 0);
            rise.repriceCurrency();
            if (m == 1) firstMonth = rise.getRate();
            if (rise.getRate() < lowest) { lowest = rise.getRate(); lowestAt = m; }
            if (m == closeAt + 1) afterClose = rise.getRate();
            if (m >= closeAt + 60) worstLate = Math.max(worstLate, Math.abs(rise.getRate() / rise.getParityTrend() - 1));
        }
        out.printf("   a gap of 3 points for 24 months then none: the rate %.4f the first month, lowest %.4f at month %d, %.4f the month after the close,"
                + " the worst gap from the trend from 60 months after the close %.4f%n", firstMonth, lowest, lowestAt, afterClose, worstLate);
        assertTrue("a rise appreciates at once: the first month shows at least GAP_IMPACT of the level",
                firstMonth <= Math.exp(ForeignAccounts.GAP_IMPACT * ForeignAccounts.gapLevelFor(three)) + 1e-12);
        assertTrue("...the lowest point is in the 24 months the gap was held", lowestAt >= 1 && lowestAt <= closeAt);
        assertTrue("...the gain is given back as the gap closes", afterClose > lowest);
        assertTrue("...and the rate is back within 1% of the trend by 60 months after the close", worstLate < .01);
    }

    /* ================= 4. the balance and the vault's cover ================= */

    /** A currency with the trade and the capital of its months, settled and then repriced for `months` months, its vault empty. */
    static ForeignAccounts held(double exports, double imports, double capitalOut, int months, double[] firstMonth) {
        ForeignAccounts fx = new ForeignAccounts();
        for (int m = 0; m < CurrencyCheck.TRADING_MONTHS; m++) fx.takeMonth(CurrencyCheck.month(exports, imports, 0, capitalOut), GDP);
        for (int m = 0; m < months; m++) {
            fx.takeMonth(CurrencyCheck.month(exports, imports, 0, capitalOut), GDP);
            fx.repriceCurrency();
            if (m == 0 && firstMonth != null) firstMonth[0] = fx.getBalanceReached();
        }
        return fx;
    }

    static void theBalanceAndTheCover() {
        out.println("\n--- 4. the balance moves the rate, and so does the vault's cover ---");

        double[] first = new double[1];
        ForeignAccounts tenth = held(.45 * GDP, .55 * GDP, 0, 60, first);
        close("fixture: the trailing overall balance is a deficit of a tenth of GDP",
                (tenth.monthlyCurrentAccount() + tenth.monthlyFinancialAccount()) / tenth.getGdpTrailing(), -.1, 1e-9);
        close("a deficit of a tenth of GDP, held, arrives at BALANCE_WEIGHT x 0.1", tenth.getBalanceReached(),
                ForeignAccounts.BALANCE_WEIGHT * .1, 1e-9);
        close("...and half of a step in its first month (MARKET_SPEED)", first[0], ForeignAccounts.MARKET_SPEED * ForeignAccounts.BALANCE_WEIGHT * .1, 1e-12);

        ForeignAccounts whole = held(0, GDP, 0, 60, null);
        assertTrue("fixture: a deficit of GDP's whole: the raw level is past the cap",
                ForeignAccounts.BALANCE_WEIGHT * 1.0 > ForeignAccounts.BALANCE_CAP);
        close("a deficit of GDP's whole is held at BALANCE_CAP", whole.getBalanceReached(), ForeignAccounts.BALANCE_CAP, 1e-9);

        ForeignAccounts surplus = held(.55 * GDP, .45 * GDP, 0, 60, null);
        close("a surplus of a tenth of GDP the other way: minus BALANCE_WEIGHT x 0.1", surplus.getBalanceReached(),
                -ForeignAccounts.BALANCE_WEIGHT * .1, 1e-9);

        ForeignAccounts recycled = held(.55 * GDP, .45 * GDP, .1 * GDP, 60, null);
        close("fixture: a surplus of a tenth of GDP, all of it sent abroad as capital: the current account is positive",
                recycled.monthlyCurrentAccount() / recycled.getGdpTrailing(), .1, 1e-9);
        close("...and the financial account the other way", recycled.monthlyFinancialAccount() / recycled.getGdpTrailing(), -.1, 1e-9);
        close("capital sent abroad that recycles a surplus leaves the target at nothing", recycled.balanceTarget(), 0, 1e-9);
        close("...and so the level reached", recycled.getBalanceReached(), 0, 1e-9);

        // The vault's cover.
        ForeignAccounts empty = CurrencyCheck.settled(TRADE, TRADE, GDP);
        close("an empty vault's cover premium is RISK_PREMIUM", empty.coverTarget(), ForeignAccounts.RISK_PREMIUM, 1e-12);
        ForeignAccounts full = CurrencyCheck.settled(TRADE, TRADE, GDP);
        full.buyReserves(ForeignAccounts.THIN_COVER * full.monthlyImports());
        close("fixture: a vault of THIN_COVER months of imports", full.importCover(), ForeignAccounts.THIN_COVER, 1e-12);
        close("a vault of THIN_COVER months costs nothing", full.coverTarget(), 0, 1e-12);
        ForeignAccounts halfVault = CurrencyCheck.settled(TRADE, TRADE, GDP);
        halfVault.buyReserves(ForeignAccounts.THIN_COVER / 2 * halfVault.monthlyImports());
        close("fixture: a vault of half of THIN_COVER", halfVault.importCover(), ForeignAccounts.THIN_COVER / 2, 1e-12);
        close("...costs half of RISK_PREMIUM", halfVault.coverTarget(), ForeignAccounts.RISK_PREMIUM / 2, 1e-12);
    }

    /* ================= 5. interventions move the rate at the real size ================= */

    static void interventionsAtTheRealSize() {
        out.println("\n--- 5. interventions move the rate at the real size ---");

        // A purchase of 1% of a year's GDP, pending until the next reprice.
        ForeignAccounts buy = calm();
        double annual = 12 * buy.getGdpTrailing(), onePercent = .01 * annual;
        buy.buyReserves(onePercent);
        close("fixture: the treasury has bought dollars worth 1% of a year's GDP, pending", buy.getDealtSinceReprice(), onePercent, 1e-9);
        month(buy);
        close("that month's move from interventions is INTERVENTION_IMPACT x 1%, the D$ cheaper", buy.getMoveIntervention(),
                ForeignAccounts.INTERVENTION_IMPACT * .01, 1e-12);
        assertTrue("...a purchase of dollars cheapens the D$: the rate rose", buy.getMoveIntervention() > 0 && buy.getLastMove() > 0);
        close("...and nothing is pending now", buy.getDealtSinceReprice(), 0, 0);
        double first = buy.getInterventionFelt();
        for (int m = 0; m < 6; m++) month(buy);
        close("six months on (INTERVENTION_HALF_LIFE) half of it is still felt", buy.getInterventionFelt(),
                first * Math.pow(.5, 6 / ForeignAccounts.INTERVENTION_HALF_LIFE), 1e-12);
        close("...which is half", buy.getInterventionFelt(), first / 2, 1e-12);

        ForeignAccounts sell = calm();
        double sold = sell.sellReserves(onePercent);
        close("fixture: the treasury has sold dollars worth 1% of a year's GDP", sold, onePercent, 1e-9);
        month(sell);
        close("a sale the other way: INTERVENTION_IMPACT x 1%, the D$ dearer", sell.getMoveIntervention(),
                -ForeignAccounts.INTERVENTION_IMPACT * .01, 1e-12);

        // The twins of the study's 1.4: the control keeps the founders' vault, the other buys a year's imports of dollars.
        ForeignAccounts control = still(TRADE, TRADE, GDP, SEED), buyer = still(TRADE, TRADE, GDP, SEED);
        double bought = 12 * buyer.monthlyImports();
        buyer.buyReserves(bought);
        assertTrue("fixture: the twins have one seed and one vault; one has bought a year's imports of dollars, pending",
                control.getMarketSeed() == buyer.getMarketSeed() && control.getReservesUsd() < buyer.getReservesUsd()
                        && Math.abs(buyer.getDealtSinceReprice() - bought) < 1e-9 && control.getDealtSinceReprice() == 0);
        for (int m = 0; m < 24; m++) { month(control); month(buyer); }
        double ratio = buyer.getRate() / control.getRate() - 1;
        out.printf("   two years on, a year's imports bought (%.0f%% of a year's GDP): the buyer's rate %.4f against the control's %.4f,"
                + " %+.2f%% (the old rule: the D$ 7.7%% dearer)%n", 100 * bought / annual, buyer.getRate(), control.getRate(), ratio * 100);
        assertTrue("two years on the buyer's D$ is CHEAPER than the control's: the finding reversed", buyer.getRate() > control.getRate());
        close("...by exactly the intervention still felt: INTERVENTION_IMPACT x the purchase over a year's GDP, faded 23 months",
                Math.log(buyer.getRate() / control.getRate()),
                ForeignAccounts.INTERVENTION_IMPACT * bought / annual * Math.pow(.5, 23 / ForeignAccounts.INTERVENTION_HALF_LIFE), 1e-9);

        // The defence's sale goes through the same channel.
        final double vault = 10_000, imports = vault / ForeignAccounts.COMFORTABLE_COVER, exports = imports / 2, gdp = 20_000, deficit = 2_000;
        final double capitalOut = deficit - (imports - exports);
        ForeignAccounts defended = CurrencyCheck.defended(exports, imports, gdp, vault, capitalOut, capitalOut);
        double capacity = defended.absorption(), r0 = defended.getRate(), fall0 = CurrencyCheck.firstFall(defended, 0);
        close("fixture: a deficit month of US$2M", defended.monthDeficitUsd(), deficit, 1e-9);
        close("fixture: the vault's capacity is the most there is", capacity, ForeignAccounts.MAX_ABSORPTION, 1e-12);
        assertTrue("fixture: the D$ would fall by more than the deficit's cap takes back, and the vault holds that cap",
                defended.getDealtSinceReprice() == 0 && fall0 > 0 && CurrencyCheck.need(capacity, fall0, defended.getGdpTrailing(), r0) > capacity * deficit
                        && capacity * deficit < vault);
        defended.repriceCurrency();
        close("a deficit month with a would-be fall sells absorption() x the deficit", defended.getDefenceUsd(), capacity * deficit, 1e-9);
        ForeignAccounts twin = CurrencyCheck.defended(imports, imports, gdp, vault, 0);
        assertTrue("fixture: the twin has the same output and no deficit",
                Math.abs(twin.getGdpTrailing() - defended.getGdpTrailing()) < 1e-9 && twin.monthDeficitUsd() == 0);
        double localSold = defended.getDefenceLocal();
        close("fixture: the twin's treasury sells the same local amount", twin.sellReserves(localSold), localSold, 1e-9);
        twin.repriceCurrency();
        close("the defence's lift equals a treasury sale of the same local amount", defended.getMoveIntervention(), twin.getMoveIntervention(), 1e-12);
        close("...which is INTERVENTION_IMPACT x what it fetched over a year's output, the D$ dearer", defended.getMoveIntervention(),
                -ForeignAccounts.INTERVENTION_IMPACT * localSold / (12 * defended.getGdpTrailing()), 1e-12);
        close("...and the twin's central bank sold nothing", twin.getDefenceUsd(), 0, 0);
    }

    /* ================= 6. one seed, one path ================= */

    /** A currency fed a month of a city's weather, deterministic in the month's number: trade that swings, capital that comes and goes, a parity that drifts, a real gap that wanders, and the treasury's deals. Rows: the rate, the market, the six moves, the intervention felt, what the defence sold. */
    static void drive(ForeignAccounts fx, int from, int to, java.util.List<double[]> trace) {
        for (int m = from; m < to; m++) {
            double exports = TRADE * (1 + .3 * Math.sin(m / 7.0)), imports = TRADE * (1 + .3 * Math.cos(m / 11.0));
            double capitalOut = m % 17 == 0 ? 400 : 0, capitalIn = m % 23 == 0 ? 250 : 0;
            double level = Math.exp(.003 * m + .08 * Math.sin(m / 30.0));
            if (m == 30) fx.buyReserves(2_500);
            if (m == 70) fx.sellReserves(2_000);
            fx.startMonth();
            fx.takeMonth(CurrencyCheck.month(exports, imports, capitalIn, capitalOut), GDP);
            fx.setParity(level, 1.0, .036, .02);
            fx.setRealRateDifferential(.03 * Math.sin(m / 40.0));
            fx.repriceCurrency();
            if (trace != null) {
                trace.add(new double[] { fx.getRate(), fx.getMarket(), fx.getMoveTrend(), fx.getMoveGap(), fx.getMoveBalance(),
                        fx.getMoveCover(), fx.getMoveIntervention(), fx.getMoveMarket(), fx.getInterventionFelt(), fx.getDefenceUsd(),
                        fx.getLastWouldBeMove() });
            }
        }
    }

    static int differences(java.util.List<double[]> a, java.util.List<double[]> b) {
        int different = 0;
        for (int t = 0; t < a.size(); t++) {
            for (int k = 0; k < a.get(t).length; k++) {
                if (Double.compare(a.get(t)[k], b.get(t)[k]) != 0) { different++; break; }
            }
        }
        return different;
    }

    /** A trading town on the default founding, as CurrencyCheck builds it; the rate it plays is what the founding's seed makes. */
    static Game town(String label, Founding founding) {
        Game city = new Game(GameFiles.scratch(label), founding);
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "House"), 500, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 8, false);
            city.buildStack(ForeignCheck.template(city, "Small Grocery Store"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(ForeignCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Industrial Bakery"), 2, false);
        });
        return city;
    }

    /** The town's rate, month by month, for this many months. */
    static double[] play(Game city, int months) {
        double[] rates = new double[months];
        for (int m = 0; m < months; m++) {
            quietly(() -> city.simulateMonths(1));
            rates[m] = city.getForeignAccounts().getRate();
        }
        return rates;
    }

    static void oneSeedOnePath() {
        out.println("\n--- 6. one seed, one path ---");

        java.util.List<double[]> one = new java.util.ArrayList<>(), again = new java.util.ArrayList<>(), other = new java.util.ArrayList<>();
        ForeignAccounts a = still(TRADE, TRADE, GDP, SEED), b = still(TRADE, TRADE, GDP, SEED), c = still(TRADE, TRADE, GDP, SEED + 1);
        drive(a, 0, 240, one);
        drive(b, 0, 240, again);
        drive(c, 0, 240, other);
        double marketReach = 0;
        int defended = 0;
        for (double[] row : one) { marketReach = Math.max(marketReach, Math.abs(row[1])); if (row[9] > 0) defended++; }
        assertTrue("fixture: the path moves - the market swings (widest " + String.format("%.3f", marketReach) + ") and the defence sold in "
                + defended + " months", marketReach > .02 && defended > 0);
        assertTrue("two currencies with one seed and the same 240 months: every figure of every month equal to the bit", differences(one, again) == 0);
        int parted = differences(one, other);
        out.printf("   a different seed parts in %d of 240 months%n", parted);
        assertTrue("...a different seed parts from the first: the rates differ in nearly every month", parted > 200);
        assertTrue("...and the seeds are the seeds", a.getMarketSeed() == b.getMarketSeed() && a.getMarketSeed() != c.getMarketSeed());

        // Two cities on the default founding, and one on another world.
        Founding standard = Founding.defaults();
        Game first = town("exchange-rate-one", standard);
        Game second = town("exchange-rate-two", standard);
        assertTrue("fixture: two cities on one founding have the market seed the world seed makes",
                first.getForeignAccounts().getMarketSeed() == second.getForeignAccounts().getMarketSeed()
                        && first.getForeignAccounts().getMarketSeed()
                                == (Game.marketSeedFor(standard.getWorldSeed()) & ((1L << 52) - 1)));
        double[] ratesOne = play(first, 60), ratesTwo = play(second, 60);
        int unequal = 0;
        for (int m = 0; m < 60; m++) if (Double.compare(ratesOne[m], ratesTwo[m]) != 0) unequal++;
        out.printf("   two cities on the default founding, 60 months: the rate %.6f -> %.6f in both; %d months apart%n", 1.0, ratesOne[59], unequal);
        assertTrue("two scratch cities founded alike play the same rates for 60 months, to the bit", unequal == 0);
        assertTrue("...and the market moved in them (" + first.getForeignAccounts().getMarketDraws() + " draws)",
                first.getForeignAccounts().getMarketDraws() >= 60 && Math.abs(first.getForeignAccounts().getMarket()) > 0);
        Game elsewhere = town("exchange-rate-world", standard.withWorldSeed(standard.getWorldSeed() + 1));
        assertTrue("fixture: a founding on another world seed has another market seed",
                elsewhere.getForeignAccounts().getMarketSeed() != first.getForeignAccounts().getMarketSeed());
        double[] ratesElsewhere = play(elsewhere, 60);
        int parts = 0;
        for (int m = 0; m < 60; m++) if (Double.compare(ratesOne[m], ratesElsewhere[m]) != 0) parts++;
        out.printf("   a founding on another world seed: %d of 60 months differ, the rate %.6f against %.6f at month 60%n", parts, ratesElsewhere[59], ratesOne[59]);
        assertTrue("a founding on another world seed plays a different path", parts > 30);
    }

    /* ================= 7. a save mid-swing continues exactly ================= */

    static void aSaveMidSwing() {
        out.println("\n--- 7. a save mid-swing continues exactly ---");

        ForeignAccounts live = still(TRADE, TRADE, GDP, SEED);
        drive(live, 0, 100, null);
        live.buyReserves(1_200);
        assertTrue("fixture: at the save the market is moving, an intervention is fading, a purchase is pending and the defence has sold",
                Math.abs(live.getMarket()) > .005 && Math.abs(live.getInterventionFelt()) > 1e-4 && live.getDealtSinceReprice() == 1_200
                        && live.getDefenceUsdLifetime() > 0 && live.getMarketDraws() == 100);
        ForeignAccounts loaded = new ForeignAccounts();
        loaded.restore(live.toSaveArray());
        assertTrue("a restored currency has the seed, the draws, the trend, the market, the felt intervention and the pending purchase",
                loaded.getMarketSeed() == live.getMarketSeed() && loaded.getMarketDraws() == live.getMarketDraws()
                        && loaded.isMarketSeeded()
                        && Double.compare(loaded.getParityTrend(), live.getParityTrend()) == 0
                        && Double.compare(loaded.getMarket(), live.getMarket()) == 0
                        && Double.compare(loaded.getInterventionFelt(), live.getInterventionFelt()) == 0
                        && Double.compare(loaded.getDealtSinceReprice(), live.getDealtSinceReprice()) == 0
                        && Double.compare(loaded.getRate(), live.getRate()) == 0);
        java.util.List<double[]> kept = new java.util.ArrayList<>(), continued = new java.util.ArrayList<>();
        drive(live, 100, 160, kept);
        drive(loaded, 100, 160, continued);
        out.printf("   saved at month 100 (market %+.4f, felt %+.4f, a purchase pending), 60 months on: %d months differ in the rate or any of the six moves"
                + "; the rates end %.9f and %.9f%n", loaded.getMarket(), loaded.getInterventionFelt(), differences(kept, continued),
                live.getRate(), loaded.getRate());
        assertTrue("both play 60 more months on the same inputs: every rate, every move, the market and the defence equal to the bit",
                differences(kept, continued) == 0);

        // A city.
        Game saved = town("exchange-rate-save", Founding.defaults());
        quietly(() -> saved.simulateMonths(40));
        ForeignAccounts fx = saved.getForeignAccounts();
        assertTrue("fixture: 40 months played: the market has taken draws and moved", fx.getMarketDraws() > 0 && fx.getMarket() != 0);
        assertTrue("saved to its scratch folder", saved.saveGame(2, "the swing").ok);
        Game back = new Game(saved.getGameFiles());
        quietly(() -> back.loadGameSave(2));
        ForeignAccounts bfx = back.getForeignAccounts();
        out.printf("   a city saved at month %d: market seed %d, %d draws, trend %.6f, market %+.5f, rate %.6f; loaded: %d, %d, %.6f, %+.5f, %.6f%n",
                saved.getMonth(), fx.getMarketSeed(), fx.getMarketDraws(), fx.getParityTrend(), fx.getMarket(), fx.getRate(),
                bfx.getMarketSeed(), bfx.getMarketDraws(), bfx.getParityTrend(), bfx.getMarket(), bfx.getRate());
        assertTrue("the loaded city's market seed and draws are the saved city's", bfx.getMarketSeed() == fx.getMarketSeed()
                && bfx.getMarketDraws() == fx.getMarketDraws() && bfx.isMarketSeeded());
        close("...its trend", bfx.getParityTrend(), fx.getParityTrend(), 1e-9);
        close("...its market", bfx.getMarket(), fx.getMarket(), 1e-9);
        close("...and its rate", bfx.getRate(), fx.getRate(), 1e-9);
        // SaveFileCheck's replay asserts its figures to 1e-9 (same()); so does this.
        double worst = 0;
        for (int m = 0; m < 12; m++) {
            quietly(() -> { saved.simulateMonths(1); back.simulateMonths(1); });
            worst = Math.max(worst, Math.abs(fx.getRate() - bfx.getRate()));
        }
        out.printf("   twelve months on, the largest gap between the two rates: %.3e%n", worst);
        assertTrue("over the next 12 months the two cities' rates stay within SaveFileCheck's replay tolerance, 1e-9", worst < 1e-9);
        assertTrue("...and they are still the same path: the draws agree", bfx.getMarketDraws() == fx.getMarketDraws());
    }

    /* ================= 8. in a real city the D$ moves like a float ================= */

    /** What a real city's D$ did, counted a month at a time the way the playtest counts it (LongPlaytest.countTheFloat()), and checked against it. */
    static final class Tally {
        int months, fromMonth60, longest, run, sign, far, defended, dealt, marketOver2, gateShut;
        double sum, sum2, worstAdd, biggest, gateSum, defendedUsd;

        /** Plays one month as the playtest plays it and counts it. */
        void step(Game g) {
            ForeignAccounts fx = g.getForeignAccounts();
            double before = fx.getRate(), dealtBefore = fx.getDealtSinceReprice();
            int was = g.getMonth();
            LongPlaytest.run(g, 1);
            if (g.getMonth() == was || !(before > 0)) return;
            double move = Math.log(fx.getRate() / before);
            double six = fx.getMoveTrend() + fx.getMoveGap() + fx.getMoveBalance() + fx.getMoveCover() + fx.getMoveIntervention()
                    + fx.getMoveMarket();
            worstAdd = Math.max(worstAdd, Math.abs(six - move));
            months++;
            if (g.getMonth() >= 60) { fromMonth60++; sum += move; sum2 += move * move; }
            int s = move > 1e-9 ? 1 : move < -1e-9 ? -1 : 0;
            run = s != 0 && s == sign ? run + 1 : s != 0 ? 1 : 0;
            sign = s;
            longest = Math.max(longest, run);
            if (Math.abs(Math.log(fx.getRate() / fx.getParity())) > Math.log(1.25)) far++;
            if (fx.getDefenceUsd() > 0) { defended++; defendedUsd += fx.getDefenceUsd(); }
            if (dealtBefore != 0 || fx.getBoughtThisMonth() + fx.getSoldThisMonth() > 0) dealt++;
            if (Math.abs(fx.getMoveMarket()) > .02) marketOver2++;
            if (Math.abs(move) > Math.abs(biggest)) biggest = move;
            gateSum += fx.gate();
            if (fx.gate() < 1) gateShut++;
        }

        double volatility() {
            double mean = sum / fromMonth60;
            return Math.sqrt(Math.max(0, sum2 / fromMonth60 - mean * mean)) * Math.sqrt(12);
        }
    }

    static void play(Game g, int months, Tally tally) {
        for (int i = 0; i < months; i++) {
            if (tally == null) LongPlaytest.run(g, 1);
            else tally.step(g);
        }
    }

    /**
     * The default playtest's city, founded and played as LongPlaytest.main plays it (its founding by hand, then its
     * rhythm of skips and looks - the fixture AuctionCheck's real run borrows), up to this month. A harness city, on a
     * scratch folder.
     */
    static Game playtestCity(String label, int target, Tally tally) {
        PrintStream was = LongPlaytest.out, system = System.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try {
            Game g = new Game(GameFiles.scratch(label), LongPlaytest.founding());
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            play(g, 3, tally);
            LongPlaytest.villageBuild(g, "House", 20);
            play(g, 4, tally);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            play(g, 5, tally);
            LongPlaytest.advise(g);
            play(g, 6, tally);
            LongPlaytest.ensureSchools(g);
            LongPlaytest.childcareWhenNeeded(g);
            int stop = 0;
            while (g.getMonth() < target) {
                stop++;
                int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                play(g, Math.min(skip, target - g.getMonth()), tally);
                LongPlaytest.ensureSchools(g);
                LongPlaytest.childcareWhenNeeded(g);
                for (int move = 0; move < LongPlaytest.movesPerLook() && g.getMonth() < target; move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    play(g, 1, tally);
                }
                play(g, Math.max(0, Math.min(2, target - g.getMonth())), tally);
            }
            return g;
        } finally {
            System.setOut(system);
            LongPlaytest.out = was;
        }
    }

    static void inARealCity() {
        out.println("\n--- 8. in a real city the D$ moves like a float, and the mechanics fire ---");

        final int target = 360;
        Tally t = new Tally();
        LongPlaytest.resetTheFloat();
        long started = System.currentTimeMillis();
        Game g = playtestCity("exchange-rate-playtest", target, t);
        double seconds = (System.currentTimeMillis() - started) / 1000.0;
        ForeignAccounts fx = g.getForeignAccounts();
        double vol = t.volatility();
        out.printf("   the default playtest's city, %d months in %.0f s (counted: %d, %d from month 60):%n", g.getMonth(), seconds, t.months, t.fromMonth60);
        out.printf("   volatility %.1f%% a year from month 60 (the old rule: 1.2%%); the longest run one way %d months (the old rule's: 78);"
                + " %d months more than 25%% from parity%n", vol * 100, t.longest, t.far);
        out.printf("   the defence sold in %d months (US$%,.0fk); the treasury dealt in %d months; the market moved the rate over 2%% in %d months;"
                + " the biggest month %+.1f%%%n", t.defended, t.defendedUsd, t.dealt, t.marketOver2, t.biggest * 100);
        out.printf("   the rate ends at %.4f against parity %.4f and its fair level %.4f; the gate averaged %.2f (open in full in %d of %d months,"
                + " the city's openness %.2f now)%n", fx.getRate(), fx.getParity(), fx.getFairLevel(), t.gateSum / Math.max(1, t.months),
                t.months - t.gateShut, t.months, fx.getOpenness());
        assertTrue("fixture: the city played its " + target + " months", g.getMonth() >= target && t.months >= target - 10);
        assertTrue("fixture: the market took a draw every month it was not pinned", fx.getMarketDraws() >= g.getMonth() - 3 && fx.isMarketSeeded());
        close("the six moves add to the month's move, every month", t.worstAdd, 0, 1e-12);
        assertTrue("the volatility a year is a float's: between 3% and 20% (today's rule: 1.2%)", vol >= .03 && vol <= .20);
        assertTrue("the longest run one way is under 36 months (today's: 78)", t.longest < 36);
        // The mechanics fire, and the playtest counts what the harness counts.
        assertTrue("the market moves the rate in a real city: some month, by more than 2%", t.marketOver2 > 0);
        assertTrue("the playtest's own counts of the same agree: the longest run, the months far from parity, the market's big months, the treasury's deals",
                LongPlaytest.floatRunLongest == t.longest && LongPlaytest.floatFarFromParity == t.far
                        && LongPlaytest.floatMarketOver2 == t.marketOver2 && LongPlaytest.floatDealt == t.dealt);
        close("...and its volatility a year, from the same month", LongPlaytest.floatVolatility(), vol, 1e-9);
        close("...and its biggest month", LongPlaytest.floatBiggest, t.biggest, 1e-12);

        // The treasury dealt in no month of this run (the count above): a counter that reads nothing must be shown to read, so the
        // harness makes a deal itself, between months as a player does, and the next month must count it, here and in the playtest.
        int dealtRun = t.dealt, playtestDealt = LongPlaytest.floatDealt;
        // WN3: on the world's band the playtest's treasury ends month 360 in overdraft (cash -18,397 on 201,258 of debt), and
        // Game.buyForeignCurrency() spends min(amount, cash): no dollars are bought, none is pending, and the counter is shown
        // nothing. A player buys with money in hand, so the treasury is given some (a thousandth of a month's GDP) as
        // the deal is made; the months that follow are counted as ever.
        quietly(() -> {
            if (g.getCash() < 1) g.setCashForTest(g.getEconomyManager().getMonthGdp() * .001);
            g.buyForeignCurrency(Math.max(1, g.getCash() * .001));
        });
        assertTrue("fixture: a purchase of dollars is pending", g.getForeignAccounts().getDealtSinceReprice() > 0);
        stepQuietly(t, g);
        assertTrue("a month that opens with a purchase of dollars pending is counted as one the treasury dealt in, here and in the playtest",
                t.dealt == dealtRun + 1 && LongPlaytest.floatDealt == playtestDealt + 1);
    }

    /** One counted month with the narration sent nowhere (LongPlaytest prints through a stream of its own). */
    static void stepQuietly(Tally t, Game g) {
        PrintStream was = LongPlaytest.out, system = System.out;
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        try { t.step(g); } finally { System.setOut(system); LongPlaytest.out = was; }
    }

    /* ================= 9. the game survives the spiral ================= */

    /** The first figure of the city that is not a number, or null: the rate, the price index, the treasury's cash, GDP, M0 and the vault. */
    static String notANumber(Game g) {
        ForeignAccounts fx = g.getForeignAccounts();
        if (!Double.isFinite(fx.getRate())) return "the rate";
        if (!Double.isFinite(g.getPriceIndex().getIndex())) return "the price index";
        if (!Double.isFinite(g.getCash())) return "the treasury's cash";
        if (!Double.isFinite(g.getEconomyManager().getMonthGdp())) return "GDP";
        if (!Double.isFinite(g.getCentralBank().m0())) return "M0";
        if (!Double.isFinite(fx.getReservesUsd())) return "the vault";
        return null;
    }

    /** The audit closes as it does in the playtest (LongPlaytest.audit(), CurrencyCheck 4): no residual over a cent, or none over a ten-millionth of the money moved. */
    static boolean auditCloses(Game g) {
        MoneyAudit.Result r = g.getLastMoneyAudit();
        return Double.isFinite(r.residual) && (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7);
    }

    static void theGameSurvivesTheSpiral() {
        out.println("\n--- 9. the game survives the spiral ---");

        Game g = playtestCity("exchange-rate-spiral", 36, null);
        ForeignAccounts fx = g.getForeignAccounts();
        out.printf("   the default city at month %d: rate %.4f, prices %.3f, cash $%,.0fk, M0 $%,.0fk; pinning the rate at its guard %,.0f%n", g.getMonth(),
                fx.getRate(), g.getPriceIndex().getIndex(), g.getCash(), g.getCentralBank().m0(), fx.getMaxRate());
        assertTrue("fixture: the city is alive and its figures are numbers", g.getMonth() >= 36 && notANumber(g) == null);
        fx.pinRate(fx.getMaxRate());
        assertTrue("fixture: the rate is pinned at its guard", fx.isPinned() && fx.getRate() == fx.getMaxRate());
        int start = g.getMonth(), lost = 0, unclosed = 0;
        String firstBad = null;
        int firstBadMonth = 0, firstUnclosed = 0;
        for (int m = 0; m < 24; m++) {
            quietly(() -> g.simulateMonths(1));
            String bad = notANumber(g);
            if (bad != null) { lost++; if (firstBad == null) { firstBad = bad; firstBadMonth = g.getMonth(); } }
            if (!auditCloses(g)) { unclosed++; if (firstUnclosed == 0) firstUnclosed = g.getMonth(); }
        }
        out.printf("   24 months at the guard: months %d to %d; rate %,.0f, prices %,.3f, cash $%,.0fk, GDP $%,.0fk, M0 $%,.0fk, vault US$%,.0fk%n",
                start + 1, g.getMonth(), fx.getRate(), g.getPriceIndex().getIndex(), g.getCash(), g.getEconomyManager().getMonthGdp(),
                g.getCentralBank().m0(), fx.getReservesUsd());
        assertTrue("the city played all 24 months at the guard", g.getMonth() == start + 24);
        assertTrue("the rate, the price index, the treasury's cash, GDP, M0 and the vault are numbers every month"
                + (firstBad == null ? "" : " (first not: " + firstBad + " at month " + firstBadMonth + ")"), lost == 0);
        assertTrue("the month's audit closes every month, as it does in the playtest" + (unclosed == 0 ? "" : " (first not: month " + firstUnclosed + ")"),
                unclosed == 0);

        assertTrue("it saves to its scratch folder", g.saveGame(2, "the spiral").ok);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(2));
        assertTrue("...and loads into a second city, whose figures are numbers", back.getMonth() == g.getMonth() && notANumber(back) == null);
        int backLost = 0, backUnclosed = 0;
        for (int m = 0; m < 12; m++) {
            quietly(() -> back.simulateMonths(1));
            if (notANumber(back) != null) backLost++;
            if (!auditCloses(back)) backUnclosed++;
        }
        out.printf("   the loaded city 12 months on: rate %,.4f, prices %,.3f, cash $%,.0fk%n", back.getForeignAccounts().getRate(),
                back.getPriceIndex().getIndex(), back.getCash());
        assertTrue("the loaded city plays 12 more months with every figure a number, the audit closing",
                back.getMonth() == g.getMonth() + 12 && backLost == 0 && backUnclosed == 0);

        // A bare currency driven past its guard.
        ForeignAccounts bare = still(TRADE, TRADE, GDP, SEED);
        boolean finite = true, held = true;
        int atGuard = 0, firstAtGuard = 0;
        for (int m = 1; m <= 12 + 120; m++) {
            bare.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            bare.setParity(m <= 12 ? 1 : 1e12, 1.0, 0, 0);
            bare.repriceCurrency();
            double[] moves = { bare.getMoveTrend(), bare.getMoveGap(), bare.getMoveBalance(), bare.getMoveCover(), bare.getMoveIntervention(),
                    bare.getMoveMarket(), bare.getLastMove(), bare.getLastWouldBeMove() };
            for (double v : moves) finite &= Double.isFinite(v);
            finite &= Double.isFinite(bare.getRate()) && Double.isFinite(bare.getFairLevel()) && Double.isFinite(bare.deviationFromFair());
            if (bare.getRate() == bare.getMaxRate()) {
                atGuard++;
                if (firstAtGuard == 0) firstAtGuard = m;
            } else if (firstAtGuard != 0) {
                held = false;
            }
        }
        out.printf("   parity at 1e12 for 120 months: the rate reached its guard %,.0f in month %d and stayed %d months; the trend ends at %.4g%n",
                bare.getMaxRate(), firstAtGuard, atGuard, bare.getParityTrend());
        assertTrue("fixture: the trend arrived at the guard, not at once (parity jumped to 1e12 after a year)", firstAtGuard > 13 && firstAtGuard < 132);
        assertTrue("a currency driven past its guard holds the rate at getMaxRate() exactly, every month after it arrived",
                held && atGuard == 132 - firstAtGuard + 1 && bare.getRate() == bare.getMaxRate());
        assertTrue("...and every move is a number all the way", finite);
        double[] saved = bare.toSaveArray();
        boolean numbers = true;
        for (double v : saved) numbers &= Double.isFinite(v);
        ForeignAccounts restored = new ForeignAccounts();
        restored.restore(saved);
        assertTrue("it survives a save round trip: the array is all numbers and the rate, trend and market come back to the bit",
                numbers && restored.getRate() == bare.getRate() && Double.compare(restored.getParityTrend(), bare.getParityTrend()) == 0
                        && Double.compare(restored.getMarket(), bare.getMarket()) == 0
                        && restored.getMarketDraws() == bare.getMarketDraws());
        for (ForeignAccounts fa : new ForeignAccounts[] { bare, restored }) {
            fa.takeMonth(CurrencyCheck.month(TRADE, TRADE, 0, 0), GDP);
            fa.setParity(1e12, 1.0, 0, 0);
            fa.repriceCurrency();
        }
        assertTrue("...and the next month on each leaves both at the guard with the same moves",
                bare.getRate() == bare.getMaxRate() && restored.getRate() == restored.getMaxRate()
                        && Double.compare(bare.getLastMove(), restored.getLastMove()) == 0 && Double.isFinite(bare.getLastMove()));
    }
}

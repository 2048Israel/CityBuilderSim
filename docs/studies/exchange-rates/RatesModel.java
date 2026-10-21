import java.nio.file.*;
import java.util.*;

/**
 * The exchange-rate study's prototype (docs/studies/spec-exchange-rates.md): today's rule
 * and the proposed one, measured. Not game code, and nothing in the game reads it.
 *
 * WHY: Jerus, 2026-10-21, "the current one is wierd". The study says what is weird in
 * numbers and proposes a rule; this file is where both numbers come from.
 *
 * Run from this folder (Java 21, no build):  java RatesModel.java slot3-series.csv
 * slot3-series.csv is his slot 3's monthly history (476 months, 0.8.x), read from a copy
 * of the save, never the save itself. The output is rates-model-output.txt.
 */
public class RatesModel {

    /* ----- today's rule's dials (ForeignAccounts at e6c8e74) ----- */
    static final double REVERSION = .004, TARGET = .02, WORLD_BASE_RATE = .02;

    /* ----- the proposal's dials (spec §3) ----- */
    /** The market's own swings fade with this half-life, in months: PPP's 3-5 years [S1]. */
    static final double MARKET_HALF_LIFE = 42;
    /** ...and are drawn this large a month: about 8% a year, a G10 float's [S2]. */
    static final double MARKET_SIGMA = .022;
    /** How many years the market expects a real rate gap to last: the level a gap holds. */
    static final double PERSISTENCE = 2.5;
    /** The most a real rate gap can move the level, either way (a soft cap, tanh). */
    static final double GAP_CAP = .35;
    /** An empty vault's discount on the currency; none at SAFE_COVER months of imports. */
    static final double RISK_PREMIUM = .10, SAFE_COVER = 3;
    /** How much of a change in the fundamentals reaches the rate in its month. */
    static final double MARKET_SPEED = .5;
    /** Parity's trend: a level and a slope (Holt), so the rate follows a moving parity with no lag. */
    static final double TREND_LEVEL = 1 / 6.0, TREND_SLOPE = 1 / 24.0;

    static final StringBuilder out = new StringBuilder();
    static void p(String f, Object... a) { out.append(String.format(Locale.ROOT, f, a)).append('\n'); }

    public static void main(String[] args) throws Exception {
        double[][] s = load(Path.of(args.length > 0 ? args[0] : "slot3-series.csv"));
        p("RatesModel - the exchange-rate study's prototype, %s", java.time.LocalDate.now());
        today(s);
        lag();
        replay(s);
        world();
        pegs();
        timing();
        boardData(s);
        String text = out.toString();
        System.out.print(text);
        Files.writeString(Path.of("rates-model-output.txt"), text);
    }

    /* columns of slot3-series.csv */
    static final int RATE = 1, PARITY = 2, INDEX = 3, POLICY = 4, EXPECTED = 5, CRED = 6,
            CA = 7, EXPORTS = 8, IMPORTS = 9, VAULT = 10, GDP = 11, GROCERIES = 12, RENT = 13, RENT_WEIGHT = 14;

    /**
     * Parity on traded goods: today's parity until the basket has weights (the index is based), then
     * chained on the groceries' index against the world's level from there.
     */
    static double[] tradedParity(double[][] s) {
        double[] x = new double[s.length];
        int based = 0;
        while (based < s.length - 1 && s[based][RENT_WEIGHT] <= 0) based++;
        double worldAt = s[based][INDEX] / Math.max(1e-9, s[based][PARITY]);
        for (int t = 0; t < s.length; t++) {
            if (t <= based) { x[t] = s[t][PARITY]; continue; }
            double world = s[t][INDEX] / Math.max(1e-9, s[t][PARITY]);
            x[t] = s[based][PARITY] * (s[t][GROCERIES] / s[based][GROCERIES]) / (world / worldAt);
        }
        if (!basedSaid) { basedSaid = true; p("(the basket is based at m%d: parity on traded goods is chained from there)", based); }
        return x;
    }
    static boolean basedSaid;

    static double[][] load(Path path) throws Exception {
        List<String> lines = Files.readAllLines(path);
        double[][] rows = new double[lines.size() - 1][];
        for (int i = 1; i < lines.size(); i++)
            rows[i - 1] = Arrays.stream(lines.get(i).split(",")).mapToDouble(Double::parseDouble).toArray();
        return rows;
    }

    /* ======================= 1. TODAY, MEASURED ======================= */

    static void today(double[][] s) {
        p("");
        p("=== 1. Today's D$ in his slot 3 (476 months), measured ===");
        int n = s.length;
        double[] r = col(s, RATE), par = col(s, PARITY);
        stats("today", r, par, 0);
        // The month's move taken apart: the anchored drift, the pull toward parity, and the push (the rest).
        double drift = 0, pull = 0, push = 0;
        double[] world = new double[n];
        for (int t = 0; t < n; t++) world[t] = s[t][INDEX] / Math.max(1e-9, par[t]);
        for (int t = 1; t < n; t++) {
            double worldInfl = t >= 12 ? world[t] / world[t - 12] - 1 : 0;
            double d = Math.log(1 + s[t][CRED] * TARGET) - Math.log(1 + worldInfl);
            double dm = t >= 24 ? d / 12 : 0;     // the drift needs a based index; zero before
            double pl = Math.log(1 + (par[t] - r[t - 1]) * REVERSION / r[t - 1]);
            double total = Math.log(r[t] / r[t - 1]);
            drift += dm; pull += pl; push += total - dm - pl;
        }
        p("the D$'s log move over the run %+.3f (1.000 to %.3f D$ a US$), made of:", Math.log(r[n - 1] / r[0]), r[n - 1]);
        p("   the anchored drift %+.3f | the pull toward parity %+.3f | the push (balance and real rate) %+.3f", drift, pull, push);
        p("parity moved %+.3f: the city's prices against the world's", Math.log(par[n - 1] / par[0]));
        // The lag in steady state: k / (k + g), with g parity's growth a month.
        int a = 264;
        double g = Math.log(par[n - 1] / par[a]) / (n - 1 - a);
        p("from m%d parity grows %.2f%% a year: a pull of %.3f a month settles at rate/parity = %.2f; measured mean %.2f",
                a, 100 * (Math.exp(12 * g) - 1), REVERSION, REVERSION / (REVERSION + g), meanRatio(r, par, a, n));
        // What parity is made of: the basket's rent.
        double[] traded = tradedParity(s);
        double wMin = 1, wMax = 0;
        for (int t = 60; t < n; t++) { wMin = Math.min(wMin, s[t][RENT_WEIGHT]); wMax = Math.max(wMax, s[t][RENT_WEIGHT]); }
        p("rent is %.0f%% to %.0f%% of the basket parity is struck from (from m60); from m192 to m240 parity rose %.0f%%: rent %+.0f%%, groceries %+.0f%%",
                100 * wMin, 100 * wMax, 100 * (par[240] / par[192] - 1), 100 * (s[240][RENT] / s[192][RENT] - 1),
                100 * (s[240][GROCERIES] / s[192][GROCERIES] - 1));
        p("parity on groceries alone (the traded part) at the end: %.3f, against the whole basket's %.3f and the rate's %.3f",
                traded[n - 1], par[n - 1], r[n - 1]);
        stats("vs traded", r, traded, 0);
    }

    static double meanRatio(double[] r, double[] par, int from, int to) {
        double m = 0; for (int t = from; t < to; t++) m += r[t] / par[t]; return m / (to - from);
    }

    /** Volatility, runs and the distance from parity of one path. */
    static double[] stats(String label, double[] r, double[] par, int from) {
        int n = r.length;
        double sum = 0, sum2 = 0; int k = 0, run = 0, longest = 0, last = 0, far = 0;
        double absDev = 0, worst = 0;
        List<Integer> runs = new ArrayList<>();
        for (int t = from + 1; t < n; t++) {
            double c = Math.log(r[t] / r[t - 1]);
            sum += c; sum2 += c * c; k++;
            int sign = c > 1e-9 ? 1 : c < -1e-9 ? -1 : 0;
            if (sign != 0 && sign == last) run++; else { if (run > 0) runs.add(run); run = sign != 0 ? 1 : 0; }
            last = sign; longest = Math.max(longest, run);
            double dev = Math.log(r[t] / par[t]);
            absDev += Math.abs(dev); worst = Math.max(worst, Math.abs(dev));
            if (Math.abs(dev) > Math.log(1.25)) far++;
        }
        if (run > 0) runs.add(run);
        Collections.sort(runs);
        double mean = sum / k, sd = Math.sqrt(Math.max(0, sum2 / k - mean * mean));
        double[] res = { sd * Math.sqrt(12), longest, runs.isEmpty() ? 0 : runs.get(runs.size() / 2), absDev / k, worst, far };
        if (label != null)
            p("%-10s volatility %.1f%% a year | longest run one way %d months (median %d) | mean distance from parity %.1f%%, worst %.1f%% | months over 25%% from parity %d of %d",
                    label, 100 * res[0], (int) res[1], (int) res[2], 100 * (Math.exp(res[3]) - 1), 100 * (Math.exp(res[4]) - 1), far, k);
        return res;
    }

    static double[] col(double[][] s, int c) { double[] x = new double[s.length]; for (int i = 0; i < s.length; i++) x[i] = s[i][c]; return x; }

    /* ======================= 2. THE LAG ======================= */

    /** Parity's trend: Holt's level and slope on the log of parity. */
    static final class Trend {
        double level, slope; boolean started;
        double step(double logParity) {
            if (!started) { level = logParity; slope = 0; started = true; return level; }
            double prev = level;
            level = TREND_LEVEL * logParity + (1 - TREND_LEVEL) * (level + slope);
            slope = TREND_SLOPE * (level - prev) + (1 - TREND_SLOPE) * slope;
            return level;
        }
    }

    static void lag() {
        p("");
        p("=== 2. The lag: a parity that keeps moving ===");
        for (double g : new double[] { .01, .021, .04, .10 }) {
            double today = 1, par = 1; Trend tr = new Trend(); double prop = 1;
            StringBuilder b = new StringBuilder();
            for (int m = 1; m <= 333 * 12; m++) {
                par *= Math.pow(1 + g, 1 / 12.0);
                today += (par - today) * REVERSION;
                prop = Math.exp(tr.step(Math.log(par)));
                if (m == 120 || m == 600 || m == 1200 || m == 3996)
                    b.append(String.format(Locale.ROOT, "  y%-3d today %.2f proposed %.3f |", m / 12, today / par, prop / par));
            }
            p("parity +%4.1f%% a year, rate/parity:%s steady state today k/(k+g) = %.2f",
                    100 * g, b, REVERSION / (REVERSION + Math.log(1 + g) / 12));
        }
        // An inflation burst as slot 3's: parity +50% in 48 months, then +2.5% a year.
        double today = 1, par = 1; Trend tr = new Trend(); StringBuilder b = new StringBuilder();
        for (int m = 1; m <= 240; m++) {
            par *= m <= 48 ? Math.pow(1.5, 1 / 48.0) : Math.pow(1.025, 1 / 12.0);
            today += (par - today) * REVERSION;
            double prop = Math.exp(tr.step(Math.log(par)));
            if (m == 12 || m == 48 || m == 60 || m == 96 || m == 240)
                b.append(String.format(Locale.ROOT, "  m%-3d today %.2f proposed %.2f |", m, today / par, prop / par));
        }
        p("a burst, parity +50%% in 4 years then +2.5%% a year, rate/parity:%s", b);
    }

    /* ======================= 3. THE PROPOSED FLOAT ON HIS CITY ======================= */

    /** One city's D$ under the proposal: parity's trend times the fundamentals' level times the market. */
    static final class Float {
        final Trend trend = new Trend();
        final Random rnd; final double rho = Math.pow(.5, 1 / MARKET_HALF_LIFE);
        double market, level, push;   // the market's swing, the fundamentals reached so far, a held intervention
        Float(long seed) { rnd = new Random(seed); }
        /** @return log of the rate. gap: the city's real rate less the world's, a year; cover: months of imports in the vault. */
        double step(double parity, double gap, double cover, boolean noise) {
            double fundamentals = -GAP_CAP * Math.tanh(PERSISTENCE * gap / GAP_CAP)
                    + RISK_PREMIUM * Math.max(0, 1 - cover / SAFE_COVER);
            level += MARKET_SPEED * (fundamentals - level);
            market = rho * market + (noise ? MARKET_SIGMA * rnd.nextGaussian() : 0);
            push *= Math.pow(.5, 1 / 6.0);
            return trend.step(Math.log(parity)) + level + market + push;
        }
    }

    static void replay(double[][] s) {
        p("");
        p("=== 3. The proposed float on his slot 3: same real gap, same vault ===");
        p("(offline: the rate does not feed back into the city's prices or trade here; the balance term is left out,");
        p(" because the saves keep the current account and not the financial account that recycles it)");
        p("");
        p("--- 3a. on today's parity, the whole basket ---");
        replayOn(s, col(s, PARITY));
        p("");
        p("--- 3b. on parity from traded goods (groceries), the proposal ---");
        replayOn(s, tradedParity(s));
    }

    static void replayOn(double[][] s, double[] par) {
        int n = s.length;
        double[] r = col(s, RATE), basket = col(s, PARITY);
        double[] world = new double[n], gap = new double[n], cover = new double[n];
        double imports = 0;
        for (int t = 0; t < n; t++) {
            world[t] = s[t][INDEX] / Math.max(1e-9, basket[t]);
            double worldInfl = t >= 12 ? world[t] / world[t - 12] - 1 : .02;
            gap[t] = (s[t][POLICY] - s[t][EXPECTED]) - (WORLD_BASE_RATE - worldInfl);
            imports = t == 0 ? s[t][IMPORTS] : (imports * 11 + s[t][IMPORTS]) / 12;
            cover[t] = imports > 1e-9 ? s[t][VAULT] * r[t] / imports : 99;
        }
        // The fundamentals alone, no market noise.
        Float calm = new Float(0); double[] calmPath = new double[n];
        for (int t = 0; t < n; t++) calmPath[t] = Math.exp(calm.step(par[t], gap[t], cover[t], false));
        int seeds = 200; double[][] paths = new double[seeds][n];
        double vol = 0, longest = 0, dist = 0, far = 0;
        for (int k = 0; k < seeds; k++) {
            Float f = new Float(1000 + k);
            for (int t = 0; t < n; t++) paths[k][t] = Math.exp(f.step(par[t], gap[t], cover[t], true));
            double[] st = stats(null, paths[k], par, 0);
            vol += st[0]; longest += st[1]; dist += st[3]; far += st[5];
        }
        p("   month | parity | today  | proposed: calm | seeds 10%%-90%% | real gap");
        for (int t = 0; t < n; t += 24) {
            double[] at = new double[seeds]; for (int k = 0; k < seeds; k++) at[k] = paths[k][t]; Arrays.sort(at);
            p("   m%-4d | %.3f  | %.3f  | %.3f          | %.3f-%.3f      | %+5.1f pts", t, par[t], r[t], calmPath[t],
                    at[seeds / 10], at[seeds * 9 / 10], 100 * gap[t]);
        }
        stats("today", r, par, 0);
        stats("calm", calmPath, par, 0);
        p("proposed, mean of %d seeds: volatility %.1f%% a year | longest run one way %.0f months | mean distance from parity %.1f%% | months over 25%% from parity %.0f of %d",
                seeds, 100 * vol / seeds, longest / seeds, 100 * (Math.exp(dist / seeds) - 1), far / seeds, n - 1);
        int peak = 0; for (int t = 0; t < n; t++) if (gap[t] > gap[peak]) peak = t;
        p("the widest real gap, m%d: %+.1f points (dial %.1f%%, expected inflation %.1f%%): the calm level holds the D$ %.0f%% dearer than parity's trend",
                peak, 100 * gap[peak], 100 * s[peak][POLICY], 100 * s[peak][EXPECTED],
                100 * (1 - Math.exp(-GAP_CAP * Math.tanh(PERSISTENCE * gap[peak] / GAP_CAP))));
    }

    /* ======================= 4. THE WORLD'S MONEYS ======================= */

    /** A money's character: its loading on the dollar, its move in a risk-off, its own swing, its inflation over the US's. */
    record Money(String name, double dollar, double riskOff, double sigma, double inflationGap, double carry) {}

    static final Money[] MONEYS = {
        new Money("euro",            1.0,  .03, .020, .000, -.010),
        new Money("yen",             1.0, -.08, .022, -.015, -.025),
        new Money("pound",           1.0,  .06, .018, .005, .005),
        new Money("Swiss franc",     1.0, -.05, .020, -.010, -.015),
        new Money("Canadian dollar", 0.6,  .07, .020, .000, .000),
        new Money("Australian $",    1.0,  .14, .028, .005, .015),
        new Money("EM float",        1.0,  .20, .040, .040, .040),
    };
    /** The world's dollar cycle: its half-life in months and its monthly draw (swings of 35-50% over 6-11 years, BIS broad dollar). */
    static final double DOLLAR_HALF_LIFE = 60, DOLLAR_SIGMA = .016;
    /** A risk-off begins about once in eight years and fades with this half-life, in months. */
    static final double RISK_OFF_A_YEAR = 1 / 8.0, RISK_OFF_HALF_LIFE = 3;

    static void world() {
        p("");
        p("=== 4. The world's moneys: 1,000 seeded 26-year paths (2000-2025's length) per money ===");
        p("(rate = PPP path x the dollar cycle x the risk-off x its own swing; local money per US$)");
        int seeds = 1000, months = 312, mm = MONEYS.length;
        double rhoD = Math.pow(.5, 1 / DOLLAR_HALF_LIFE), rhoM = Math.pow(.5, 1 / MARKET_HALF_LIFE), rhoR = Math.pow(.5, 1 / RISK_OFF_HALF_LIFE);
        double[] vol = new double[mm], hl = new double[mm], worst1 = new double[mm], worst3 = new double[mm];
        double[][] perSeedWorst1 = new double[mm][seeds];
        double episodes = 0; double[] dollarSwing = new double[seeds];
        double carrySum = 0, carry2 = 0, carryWorst = 0; int carryYears = 0; List<Double> carryYearly = new ArrayList<>();
        for (int k = 0; k < seeds; k++) {
            Random rnd = new Random(77_000 + k);
            double dollar = 0, risk = 0; double[] own = new double[mm];
            double[][] log = new double[mm][months], dev = new double[mm][months];
            double dMin = 0, dMax = 0;
            for (int t = 0; t < months; t++) {
                dollar = rhoD * dollar + DOLLAR_SIGMA * rnd.nextGaussian();
                dMin = Math.min(dMin, dollar); dMax = Math.max(dMax, dollar);
                risk *= rhoR;
                if (rnd.nextDouble() < RISK_OFF_A_YEAR / 12) { risk += 1; episodes++; }
                for (int i = 0; i < mm; i++) {
                    Money m = MONEYS[i];
                    own[i] = rhoM * own[i] + m.sigma() * rnd.nextGaussian();
                    double ppp = m.inflationGap() * t / 12.0;
                    dev[i][t] = m.dollar() * dollar + m.riskOff() * risk + own[i];
                    log[i][t] = ppp + dev[i][t];
                }
            }
            dollarSwing[k] = dMax - dMin;
            for (int i = 0; i < mm; i++) {
                double sum = 0, sum2 = 0, w1 = 0, w3 = 0, xy = 0, xx = 0;
                for (int t = 1; t < months; t++) {
                    double c = log[i][t] - log[i][t - 1]; sum += c; sum2 += c * c;
                    w1 = Math.max(w1, Math.abs(c));
                    if (t >= 3) w3 = Math.max(w3, Math.abs(log[i][t] - log[i][t - 3]));
                    xy += dev[i][t] * dev[i][t - 1]; xx += dev[i][t - 1] * dev[i][t - 1];
                }
                double mean = sum / (months - 1);
                vol[i] += Math.sqrt(sum2 / (months - 1) - mean * mean) * Math.sqrt(12);
                double beta = xy / xx; hl[i] += beta > 0 && beta < 1 ? Math.log(.5) / Math.log(beta) / 12 : 99;
                perSeedWorst1[i][k] = w1; worst3[i] = Math.max(worst3[i], w3);
            }
            // The carry: long the Australian dollar, short the yen, earning their rate gap each year.
            int au = 5, jp = 1; double gapMonth = (MONEYS[au].carry() - MONEYS[jp].carry()) / 12;
            double infl = (MONEYS[au].inflationGap() - MONEYS[jp].inflationGap()) / 12;
            for (int t = 1; t < months; t++) {
                double cross = (log[au][t] - log[jp][t]) - (log[au][t - 1] - log[jp][t - 1]);
                double ret = gapMonth - cross + infl;     // the real gap earned less the real move of the cross, a month
                carrySum += ret; carry2 += ret * ret; carryYears++; carryWorst = Math.min(carryWorst, ret); carryYearly.add(ret);
            }
        }
        p("   money           | volatility a year | half-life measured as an economist would (true 3.5y) | worst month (median seed / worst) | worst 3 months");
        for (int i = 0; i < mm; i++) {
            Arrays.sort(perSeedWorst1[i]);
            p("   %-15s | %5.1f%%           | %4.1f years                                         | %4.1f%% / %4.1f%%                  | %4.1f%%",
                    MONEYS[i].name(), 100 * vol[i] / seeds, hl[i] / seeds, 100 * (Math.exp(perSeedWorst1[i][seeds / 2]) - 1),
                    100 * (Math.exp(perSeedWorst1[i][seeds - 1]) - 1), 100 * (Math.exp(worst3[i]) - 1));
        }
        Arrays.sort(dollarSwing);
        p("the dollar cycle: its widest swing in 26 years, median seed %.0f%%, 90th percentile %.0f%%",
                100 * (Math.exp(dollarSwing[seeds / 2]) - 1), 100 * (Math.exp(dollarSwing[seeds * 9 / 10]) - 1));
        p("risk-offs: %.2f a path of 26 years; at one, the Australian dollar falls %.0f%% against the yen in its month",
                episodes / seeds, 100 * (1 - Math.exp(-(MONEYS[5].riskOff() - MONEYS[1].riskOff()))));
        double cm = carrySum / carryYears, cs = Math.sqrt(carry2 / carryYears - cm * cm);
        double skew = 0; for (double v : carryYearly) skew += Math.pow((v - cm) / cs, 3); skew /= carryYearly.size();
        p("the carry, long A$ short yen on a %.1f-point real gap: %.1f%% a year on average, s.d. %.1f%% a year, Sharpe %.2f, monthly skew %+.2f, worst month %.0f%%",
                100 * (MONEYS[5].carry() - MONEYS[1].carry()), 1200 * cm, 100 * cs * Math.sqrt(12), cm / cs * Math.sqrt(12), skew, 100 * carryWorst);
    }

    /* ======================= 5. PEGS ======================= */

    /** A soft peg's break: the hazard a month at an overvaluation x (log), and the vault's floor. */
    static double hazard(double x) { return .001 + .03 * Math.max(0, x - .10) / .10; }
    static final double OVERSHOOT = .25, VAULT_FLOOR = 1;

    static void pegs() {
        p("");
        p("=== 5. A soft peg to the US$ that inflates faster than the US ===");
        p("(the shadow rate is where the float would be; the vault starts at 6 months of imports; the trade deficit");
        p(" grows 0.3 of a month's imports per unit of overvaluation; mobile money of 3 months' imports leaves at the hazard)");
        Random rnd = new Random(5);
        for (double infl : new double[] { .02, .05, .10 }) {
            int seeds = 2000; double[] life = new double[seeds], jump = new double[seeds]; int byVault = 0;
            double rhoM = Math.pow(.5, 1 / MARKET_HALF_LIFE);
            for (int k = 0; k < seeds; k++) {
                double shadow = 0, market = 0, vault = 6; int t = 0; boolean broke = false;
                while (t < 1200 && !broke) {
                    t++;
                    market = rhoM * market + MARKET_SIGMA * rnd.nextGaussian();
                    shadow = infl * t / 12.0 + market;      // log of shadow / peg: positive means the peg holds the money too dear
                    double over = Math.max(0, shadow);
                    double h = hazard(over);
                    vault -= .3 * over + 3 * h;              // the deficit at the peg, and the mobile money leaving
                    vault += .05;                            // the vault's own income and the steady surplus at fair value
                    if (vault < VAULT_FLOOR) { broke = true; byVault++; }
                    else if (rnd.nextDouble() < h) broke = true;
                }
                life[k] = t / 12.0; jump[k] = Math.max(0, shadow) * (1 + OVERSHOOT);
            }
            Arrays.sort(life); Arrays.sort(jump);
            p("inflation %4.1f points over the US: the peg lasts %.1f years (median; 10%%-90%%: %.1f-%.1f); %d%% end with the vault spent; the break %.0f%% (median, 90th %.0f%%)",
                    100 * infl, life[seeds / 2], life[seeds / 10], life[seeds * 9 / 10], 100 * byVault / seeds,
                    100 * (Math.exp(jump[seeds / 2]) - 1), 100 * (Math.exp(jump[seeds * 9 / 10]) - 1));
        }
    }

    /* ======================= THE BOARDS' DATA ======================= */

    /** board-1-path.csv: his slot 3 under today's rule and the proposal (seed 1004); board-3-world.csv: one world, 2000-2040. */
    static void boardData(double[][] s) throws Exception {
        int n = s.length;
        double[] basket = col(s, PARITY), traded = tradedParity(s), r = col(s, RATE);
        double[] gap = new double[n], cover = new double[n]; double imports = 0;
        for (int t = 0; t < n; t++) {
            double world = s[t][INDEX] / Math.max(1e-9, basket[t]);
            double worldPrev = t >= 12 ? s[t - 12][INDEX] / Math.max(1e-9, basket[t - 12]) : world / 1.02;
            gap[t] = (s[t][POLICY] - s[t][EXPECTED]) - (WORLD_BASE_RATE - (world / worldPrev - 1));
            imports = t == 0 ? s[t][IMPORTS] : (imports * 11 + s[t][IMPORTS]) / 12;
            cover[t] = imports > 1e-9 ? s[t][VAULT] * r[t] / imports : 99;
        }
        Float calmF = new Float(0); double[] calm = new double[n];
        for (int t = 0; t < n; t++) calm[t] = Math.exp(calmF.step(traded[t], gap[t], cover[t], false));
        int seeds = 200; double[][] paths = new double[seeds][n]; double[] dist = new double[seeds];
        for (int k = 0; k < seeds; k++) {
            Float f = new Float(1000 + k);
            for (int t = 0; t < n; t++) { paths[k][t] = Math.exp(f.step(traded[t], gap[t], cover[t], true)); dist[k] += Math.abs(Math.log(paths[k][t] / calm[t])); }
        }
        double[] sorted = dist.clone(); Arrays.sort(sorted);
        int typical = 0; for (int k = 0; k < seeds; k++) if (dist[k] == sorted[seeds / 2]) typical = k;
        StringBuilder b = new StringBuilder("month,basketParity,tradedParity,today,fair,p10,p90,typicalSeed\n");
        for (int t = 0; t < n; t++) {
            double[] at = new double[seeds]; for (int k = 0; k < seeds; k++) at[k] = paths[k][t]; Arrays.sort(at);
            b.append(String.format(Locale.ROOT, "%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f\n", t, basket[t], traded[t], r[t],
                    calm[t], at[seeds / 10], at[seeds * 9 / 10], paths[typical][t]));
        }
        Files.writeString(Path.of("board-1-path.csv"), b);
        p("(board 1's single path is seed %d, the median of 200 by its distance from the fair level)", 1000 + typical);

        // One world: the dollar cycle and the eight's rates (index, 2000 = 100), Random from 2000, 41 years.
        Random rnd = new Random(2026);
        double rhoD = Math.pow(.5, 1 / DOLLAR_HALF_LIFE), rhoM = Math.pow(.5, 1 / MARKET_HALF_LIFE), rhoR = Math.pow(.5, 1 / RISK_OFF_HALF_LIFE);
        double dollar = 0, risk = 0; double[] own = new double[MONEYS.length];
        StringBuilder w = new StringBuilder("month,dollar,riskOff");
        for (Money m : MONEYS) w.append(',').append(m.name().replace(' ', '_'));
        w.append('\n');
        for (int t = 0; t < 492; t++) {
            dollar = rhoD * dollar + DOLLAR_SIGMA * rnd.nextGaussian();
            risk *= rhoR;
            if (rnd.nextDouble() < RISK_OFF_A_YEAR / 12) risk += 1;
            w.append(t).append(String.format(Locale.ROOT, ",%.2f,%.3f", 100 * Math.exp(dollar), risk));
            for (int i = 0; i < MONEYS.length; i++) {
                Money m = MONEYS[i];
                own[i] = rhoM * own[i] + m.sigma() * rnd.nextGaussian();
                w.append(String.format(Locale.ROOT, ",%.2f", 100 * Math.exp(m.inflationGap() * t / 12.0 + m.dollar() * dollar + m.riskOff() * risk + own[i])));
            }
            w.append('\n');
        }
        Files.writeString(Path.of("board-3-world.csv"), w);
        p("");
        p("(the boards' data written: board-1-path.csv, board-3-world.csv)");
    }

    /* ======================= 6. TIMING ======================= */

    static void timing() {
        p("");
        p("=== 6. What a month costs: 196 moneys and the city's D$ ===");
        int nations = 196; Random rnd = new Random(1);
        double[] own = new double[nations], load = new double[nations], gap = new double[nations];
        for (int i = 0; i < nations; i++) { load[i] = rnd.nextDouble(); gap[i] = rnd.nextGaussian() * .01; }
        double rhoM = Math.pow(.5, 1 / MARKET_HALF_LIFE), rhoD = Math.pow(.5, 1 / DOLLAR_HALF_LIFE), dollar = 0, risk = 0, sink = 0;
        Float city = new Float(9);
        long best = Long.MAX_VALUE;
        for (int rep = 0; rep < 20; rep++) {
            long t0 = System.nanoTime();
            for (int m = 0; m < 12_000; m++) {
                dollar = rhoD * dollar + DOLLAR_SIGMA * rnd.nextGaussian();
                risk *= .79;
                for (int i = 0; i < nations; i++) {
                    own[i] = rhoM * own[i] + .02 * rnd.nextGaussian();
                    sink += gap[i] * m / 12.0 + load[i] * dollar + .05 * risk + own[i];
                }
                sink += city.step(1 + m * 1e-4, .01, 4, true);
            }
            best = Math.min(best, System.nanoTime() - t0);
        }
        p("one month, all 196 and the city: %.1f microseconds (best of 20 runs of 1,000 years; checksum %.1f)", best / 12_000.0 / 1000, sink % 1000);
    }
}

package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * The world's moneys against the US dollar, a month at a time (WN4; spec-exchange-rates 3.7, spec-nations 5.1): each money's rate is its
 * purchasing-power path, plus its loading on the dollar cycle, on the risk-off and on its nation's terms of trade, plus its own swing, with
 * the pegs held and the soft ones broken by the rule the D$'s peg breaks by (ForeignAccounts.hazard). A pure model: it reads the world file and
 * the nations' path, and nothing in the economy reads it yet.
 *
 * WHY THIS EXISTS. The moneys' rates were WN1's yearly draw: relative parity as a drift and a 10% shock a year, with no dollar cycle, no
 * risk-off, no terms of trade and no month. So nothing could make the yen rise in a panic or the Australian dollar fall with iron ore, the
 * rate moved once a year in a step, and the D$ had no world to move with. This is the study's rule, every money of the world each month.
 *
 * THE SHAPE. ln rate_k, units of money k a US dollar, is lnPPP_k(t) + b_k C(t) + r_k R(t) + T_k(t) + S_k(t) where t is the month since
 * January 2000 (the file's month 0), C the dollar cycle, R the risk-off, T the terms of trade reached and S the money's own swing. Historical
 * replays the file's rates to December 2025 and runs the rule from January 2026; Random runs it from February 2000. A city begins in January
 * 2000 on both, so a WorldMoneys is made at month 0 (the file's rates; Random's state opened from their gaps from parity) and stepped a month at
 * a time; on Historical the first 311 steps replay the file and the 311th opens the state from December 2025's gaps. Every draw is
 * new Random(Nations.scramble(seed, who, t, what)), a pure function of the seed and the month, so one seed is one path and a reload continues it.
 *
 * It copies what it needs from the file and keeps no reference to it; it keeps the Nations (for the price levels, the regimes and the money of
 * a nation's year).
 */
public final class WorldMoneys {

    /* =====================================================================
       THE DIALS - each with its source: [R..] is spec-exchange-rates' list, (est.) the game's estimate
       ===================================================================== */

    /** One standard deviation of the dollar cycle's monthly innovation: 1.6% (the broad dollar's swings of 35-50% over 6-11 years [R5]). */
    public static final double CYCLE_SIGMA = .016;

    /** The dollar cycle's half-life in months: five years, one AR(1) for the world [R5]. */
    public static final double CYCLE_HALF_LIFE = 60;

    /** The chance a month a risk-off starts: once in eight years [R6]. */
    public static final double RISK_OFF_RATE = 1.0 / 96;

    /** The risk-off's half-life in months: it fades by half in three [R6]. */
    public static final double RISK_OFF_HALF_LIFE = 3;

    /** The least a risk-off's size is, the multiple of the loadings it moves the moneys by (est.: 2024's was smaller than one). */
    public static final double RISK_OFF_SIZE_MIN = .5;

    /** The most a risk-off's size is (est.: 2008's was larger than one). */
    public static final double RISK_OFF_SIZE_MAX = 1.5;

    /** A money's own swing's half-life in months: three and a half years, an AR(1) around its parity [R1]. */
    public static final double OWN_HALF_LIFE = 42;

    /** The share of a nation's commodity share of exports its terms of trade pass into its money [R17][R18]: a better price of what it sells makes the money dearer. */
    public static final double TOT_PASS = .5;

    /** The months a January's change in the terms of trade takes to reach the rate, a third in each of the year's first (est.). */
    public static final int TOT_MONTHS = 3;

    /** The first month the rule rules on Historical: January 2026, after the file's 312 months (the file's real rates are replayed before it). */
    public static final int HISTORICAL_RULE_START = (Nations.HISTORY_LAST_YEAR + 1 - Nations.FIRST_YEAR) * 12;

    /** The first month the rule rules on Random: February 2000, the month after the file's January 2000 it opens from. */
    public static final int RANDOM_RULE_START = 1;

    /** The boards the file's pegs include, which never break: the game keeps no nation's vault, and a board breaks only if its vault falls under its money (est., the IMF's list [R21]). */
    public static final Set<String> CURRENCY_BOARDS = Set.of("HKD", "BND", "DJF", "XCD");

    /**
     * The eight moneys the IMF's COFER names, the vault's (spec-nations 5.2), in this order. THE ORDER IS LOAD-BEARING: the vault's saved balances
     * are keyed by it (WN4 slice C), so a ninth money goes at the end and none is moved.
     */
    public static final List<String> EIGHT = List.of("USD", "EUR", "JPY", "GBP", "CNY", "CAD", "AUD", "CHF");

    /** The six floating majors the dollar cycle is fitted to at the opening: the euro, the yen, the pound, the franc, and the Canadian and Australian dollars. */
    static final List<String> MAJORS = List.of("EUR", "JPY", "GBP", "CHF", "CAD", "AUD");

    /* ----- the kinds: what each money loads on ----- */

    /** A kind of money: its own swing's monthly innovation sd, its loading on the dollar cycle (b), and on the risk-off (r, negative for a money that rises in a panic). */
    public record Kind(String name, double sigma, double cycle, double riskOff) { }

    /** The euro: own swing 2.0% a month, the cycle in full, +3% in a risk-off (the study's table 3.7, its own swing solved given the cycle [R6]). */
    public static final Kind EURO = new Kind("euro", .020, 1, .03);

    /** The yen: 2.3%, the cycle in full, -8% in a risk-off (it rises) [R6]. */
    public static final Kind YEN = new Kind("yen", .023, 1, -.08);

    /** The pound: 1.9%, the cycle in full, +6% [R6]. */
    public static final Kind POUND = new Kind("pound", .019, 1, .06);

    /** The Swiss franc: 2.1%, the cycle in full, -5% (it rises) [R6]. */
    public static final Kind FRANC = new Kind("franc", .021, 1, -.05);

    /** The Canadian dollar: 2.1%, 0.6 of the cycle (tied to the US), +7% [R6]. */
    public static final Kind CANADIAN = new Kind("Canadian dollar", .021, .6, .07);

    /** The Australian dollar: 2.8%, the cycle in full, +14% [R6]. */
    public static final Kind AUSTRALIAN = new Kind("Australian dollar", .028, 1, .14);

    /** The New Zealand dollar: 2.7%, the cycle in full, +14% (est.: the Australian dollar's loading). */
    public static final Kind KIWI = new Kind("New Zealand dollar", .027, 1, .14);

    /** The yuan: 1.0%, 0.3 of the cycle, no risk-off (est.: a managed money). */
    public static final Kind YUAN = new Kind("yuan", .010, .3, 0);

    /** Another advanced float, one whose anchor nation is high income when the rule begins: 2.2%, the cycle in full, +6% (est.: the euro's and the pound's, between them). */
    public static final Kind ADVANCED_FLOAT = new Kind("advanced float", .022, 1, .06);

    /** An emerging float, any other money: 4.0%, the cycle in full, +20% [R6]. */
    public static final Kind EMERGING_FLOAT = new Kind("emerging float", .040, 1, .20);

    /** The US dollar: it moves against nothing, its rate is 1 always. */
    public static final Kind DOLLAR_KIND = new Kind("US dollar", 0, 0, 0);

    private static final Map<String, Kind> NAMED = Map.of("USD", DOLLAR_KIND, "EUR", EURO, "JPY", YEN, "GBP", POUND, "CHF", FRANC,
            "CAD", CANADIAN, "AUD", AUSTRALIAN, "NZD", KIWI, "CNY", YUAN);

    /** The monthly persistence of each AR(1): 0.5 to the 1 over its half-life. */
    private static final double PHI_CYCLE = StrictMath.pow(.5, 1 / CYCLE_HALF_LIFE),
            PHI_RISK_OFF = StrictMath.pow(.5, 1 / RISK_OFF_HALF_LIFE), PHI_OWN = StrictMath.pow(.5, 1 / OWN_HALF_LIFE);

    /** The current format of toSaveArray. */
    static final int SAVE_FORMAT = 1;

    /* The draws' who and what: a world's draw sits at WORLD, a money's at MONEY_BASE plus its number in the file, and no what is a Nations draw's (1-8). */
    private static final long WORLD = 9_999, MONEY_BASE = 10_000;
    private static final int CYCLE_DRAW = 100, RISK_START_DRAW = 101, RISK_SIZE_DRAW = 102, OWN_DRAW = 103, BREAK_DRAW = 104;

    /* =====================================================================
       THE MONEYS - made once from the file and the nations
       ===================================================================== */

    private final Nations nations;
    private final long seed;
    private final boolean historical;
    private final int ruleStart, ruleYear, nationCount, m, dollar, usa;
    /** The moneys, sorted by code; each one's anchor nation (1-based, whose price level is its price level), who it draws as, its kind, the money it is pegged to (-1: a float). */
    private final String[] code, kindName;
    private final Map<String, Integer> index = new HashMap<>();
    private final int[] anchor, target;
    private final long[] who;
    private final double[] sigma, cycleLoad, riskLoad, constant;
    private final boolean[] board, live;
    /** The file's monthly rates, per money, for the file's months; NaN where it has none. */
    private final double[][] fileRate;
    /** The nations' commodity share of exports, index nation - 1. */
    private final double[] share;
    private final double[] startRate;

    /* ----- the state: saved ----- */
    private int month;
    private boolean opened;
    private double cycle, riskOff;
    private boolean riskStarted;
    private final double[] rate, lnRate, lnShadow, swing, tot, pending, pegRatio, gap;
    private final boolean[] held, present, broke;
    private final double[] lastTot;
    private final boolean[] hasLast;

    /* ----- worked, not saved ----- */
    private final int[] mark;
    private int cacheYear = -1;
    private double[] lnA0, lnA1;
    private double lnUs0, lnUs1;
    private boolean quiet;
    private double forced;

    /**
     * The moneys of a world on a path and seed, standing in January 2000 (month() is 0): the file's rates of that month, and on Random the state
     * opened from their gaps from parity. commodityShare is each nation's share of exports in commodities, index nation - 1 (WorldMarket.Base's
     * commodityShare), null for none; the file is the nations' own. Keeps no reference to the file.
     */
    public WorldMoneys(WorldData file, Nations nations, double[] commodityShare) {
        Objects.requireNonNull(file, "file");
        this.nations = Objects.requireNonNull(nations, "nations");
        nationCount = nations.count();
        if (file.nationCount() != nationCount) throw new IllegalArgumentException("world moneys: the file has " + file.nationCount() + " nations and the path " + nationCount);
        if (file.firstMonth() != 0) throw new IllegalArgumentException("world moneys: the file's rates begin in month " + file.firstMonth() + ", not January 2000");
        if (commodityShare != null && commodityShare.length != nationCount)
            throw new IllegalArgumentException("world moneys: " + commodityShare.length + " commodity shares for " + nationCount + " nations");
        share = commodityShare == null ? new double[nationCount] : commodityShare.clone();
        seed = nations.seed();
        historical = nations.path() == Nations.Path.HISTORICAL;
        ruleStart = historical ? HISTORICAL_RULE_START : RANDOM_RULE_START;
        ruleYear = Nations.FIRST_YEAR + ruleStart / 12;
        usa = nations.index(Nations.FRONTIER);
        if (usa < 1) throw new IllegalArgumentException("world moneys: no " + Nations.FRONTIER + " among the nations");
        code = nations.pathMoneys();
        m = code.length;
        for (int k = 0; k < m; k++) index.put(code[k], k);
        Integer d = index.get(Nations.DOLLAR);
        if (d == null) throw new IllegalArgumentException("world moneys: no nation is on the US dollar");
        dollar = d;
        anchor = new int[m];
        target = new int[m];
        who = new long[m];
        sigma = new double[m];
        cycleLoad = new double[m];
        riskLoad = new double[m];
        constant = new double[m];
        kindName = new String[m];
        board = new boolean[m];
        live = new boolean[m];
        startRate = new double[m];
        int fileMonths = Math.min(file.months(), HISTORICAL_RULE_START);
        fileRate = new double[m][fileMonths];
        for (int k = 0; k < m; k++) {
            anchor[k] = nations.anchorNation(code[k]);
            int number = file.moneyNumber(code[k]);
            who[k] = MONEY_BASE + (number > 0 ? number : 10_000 + k);
            for (int t = 0; t < fileMonths; t++) fileRate[k][t] = number > 0 ? file.rate(number, t) : Double.NaN;
            Kind kind = kindOf(k);
            kindName[k] = kind.name();
            sigma[k] = kind.sigma();
            cycleLoad[k] = kind.cycle();
            riskLoad[k] = kind.riskOff();
            live[k] = !historical || nations.usedAfterHistory(code[k]);
            target[k] = -1;
            int a = anchor[k];
            if (k != dollar && a > 0) {
                String regime = nations.regime(a);
                int colon = regime.indexOf(':');
                if (colon > 0 && (regime.startsWith("peg:") || regime.startsWith("uses:"))) {
                    Integer to = index.get(regime.substring(colon + 1));
                    if (to != null && to != k) target[k] = to;
                }
            }
            board[k] = target[k] >= 0 && CURRENCY_BOARDS.contains(code[k]);
        }
        rate = new double[m];
        lnRate = new double[m];
        lnShadow = new double[m];
        swing = new double[m];
        tot = new double[m];
        pending = new double[m];
        pegRatio = new double[m];
        gap = new double[m];
        held = new boolean[m];
        present = new boolean[m];
        broke = new boolean[m];
        mark = new int[m];
        lastTot = new double[nationCount];
        hasLast = new boolean[nationCount];
        Arrays.fill(rate, Double.NaN);
        replay(0);
        for (int k = 0; k < m; k++) startRate[k] = present[k] ? rate[k] : Double.NaN;
        for (int k = 0; k < m; k++) constant[k] = parityConstant(k, file);
        month = 0;
        if (ruleStart == 1) open(0);
    }

    /** The kind of money k: by code for the named ones, else an advanced float if its anchor is a high-income nation when the rule begins, else an emerging float. */
    private Kind kindOf(int k) {
        Kind named = NAMED.get(code[k]);
        if (named != null) return named;
        int a = anchor[k];
        boolean advanced = a > 0 && Nations.incomeGroup(nations.year(a, ruleYear).outputPerHead()) == 0;
        return advanced ? ADVANCED_FLOAT : EMERGING_FLOAT;
    }

    /** The month index of a game month: months since January 2000, the file's month 0 (game month 1 is January 2000). */
    public static int monthIndex(int gameMonth) {
        return (CityCalendar.yearOf(gameMonth) - Nations.FIRST_YEAR) * 12 + CityCalendar.monthOfYear(gameMonth) - 1;
    }

    /* =====================================================================
       THE FILE'S FIGURES AND THE PARITY PATH
       ===================================================================== */

    /**
     * A money's rate in a month the file has a figure for, else the anchor's yearly rate if the anchor was on that money that year, else 1 for the
     * dollar; NaN where there is none (the caller holds the last one).
     */
    private double figure(int k, int t) {
        double v = t < fileRate[k].length ? fileRate[k][t] : Double.NaN;
        if (v > 0 && Double.isFinite(v)) return v;
        if (k == dollar) return 1;
        int a = anchor[k];
        int year = Nations.FIRST_YEAR + t / 12;
        if (a > 0 && year <= Nations.HISTORY_LAST_YEAR) {
            Nations.Year y = nations.year(a, year);
            if (code[k].equals(y.money()) && y.rateToUsd() > 0 && Double.isFinite(y.rateToUsd())) return y.rateToUsd();
        }
        return Double.NaN;
    }

    /** A month of the file, replayed: each money takes its figure, or holds the last one it had; one that never had any is not present. */
    private void replay(int t) {
        for (int k = 0; k < m; k++) {
            if (k == dollar) {
                rate[k] = 1;
                lnRate[k] = lnShadow[k] = 0;
                present[k] = true;
                continue;
            }
            double v = figure(k, t);
            if (v > 0) {
                rate[k] = v;
                lnRate[k] = StrictMath.log(v);
                present[k] = true;
            }
            lnShadow[k] = lnRate[k];
        }
    }

    /**
     * The constant that anchors a money's parity on its real rate over the file's years: the mean of ln(file rate) - ln(cpi of the anchor over the
     * US's) over the years 2000-2025 the anchor was on the money and the file has all three. So the strong dollar of 2000-2001 is a gap the swings
     * close, not parity. A money with no such year takes its first figure's month (January 2000's if it has one), its gap there 0.
     */
    private double parityConstant(int k, WorldData file) {
        int a = anchor[k];
        if (k == dollar || a < 1) return 0;
        double sum = 0;
        int count = 0;
        for (int y = Nations.FIRST_YEAR; y <= Nations.HISTORY_LAST_YEAR; y++) {
            if (!code[k].equals(file.moneyAt(a, (y - Nations.FIRST_YEAR) * 12 + 11))) continue;
            double r = file.pathValue("rate", a, y), pa = file.pathValue("cpi", a, y), pu = file.pathValue("cpi", usa, y);
            if (!(r > 0 && pa > 0 && pu > 0) || !Double.isFinite(r) || !Double.isFinite(pa) || !Double.isFinite(pu)) continue;
            sum += StrictMath.log(r) - StrictMath.log(pa / pu);
            count++;
        }
        if (count > 0) return sum / count;
        if (present[k]) return lnRate[k] - relativePrice(k, 0);
        for (int t = 1; t < fileRate[k].length; t++) {
            double v = fileRate[k][t];
            if (v > 0 && Double.isFinite(v)) return StrictMath.log(v) - relativePrice(k, t);
        }
        return 0;
    }

    /** The anchor's price level and the US's, January of this year and of the next, in logs: the parity path's two ends. */
    private void prepare(int year) {
        if (year == cacheYear) return;
        if (lnA0 == null) {
            lnA0 = new double[m];
            lnA1 = new double[m];
        }
        for (int k = 0; k < m; k++) {
            int a = anchor[k];
            lnA0[k] = a > 0 ? logPrice(a, year) : 0;
            lnA1[k] = a > 0 ? logPrice(a, year + 1) : 0;
        }
        lnUs0 = logPrice(usa, year);
        lnUs1 = logPrice(usa, year + 1);
        cacheYear = year;
    }

    private double logPrice(int nation, int year) {
        return StrictMath.log(nations.year(nation, year).priceLevel());
    }

    /** ln(P_a / P_US) of money k's anchor in month t, log-linear between Januaries: ln P(t) = ln P(y) + (t mod 12)/12 (ln P(y+1) - ln P(y)). */
    private double relativePrice(int k, int t) {
        prepare(Nations.FIRST_YEAR + t / 12);
        double f = (t % 12) / 12.0;
        return lnA0[k] + f * (lnA1[k] - lnA0[k]) - (lnUs0 + f * (lnUs1 - lnUs0));
    }

    /** ln of money k's parity rate in month t: its constant and the anchor's prices against the US's. */
    private double parity(int k, int t) {
        return constant[k] + relativePrice(k, t);
    }

    /* =====================================================================
       THE OPENING - the state from the gaps from parity
       ===================================================================== */

    /**
     * Opens the state at month t (the month before the rule's first): C the least-squares fit of gap_k = b_k C over the floating majors, each money's
     * own swing what is left of its gap, the risk-off and the terms of trade 0, and each peg's ratio to the money it is pegged to, held.
     */
    private void open(int t) {
        double num = 0, den = 0;
        for (String major : MAJORS) {
            Integer k = index.get(major);
            if (k == null || !present[k]) continue;
            double g = lnRate[k] - parity(k, t);
            num += cycleLoad[k] * g;
            den += cycleLoad[k] * cycleLoad[k];
        }
        cycle = den > 0 ? num / den : 0;
        riskOff = 0;
        for (int k = 0; k < m; k++) {
            tot[k] = pending[k] = 0;
            swing[k] = k != dollar && present[k] ? lnRate[k] - parity(k, t) - cycleLoad[k] * cycle : 0;
            held[k] = target[k] >= 0 && present[k] && present[target[k]];
            pegRatio[k] = held[k] ? lnRate[k] - lnRate[target[k]] : 0;
        }
        opened = true;
    }

    /* =====================================================================
       THE MONTH
       ===================================================================== */

    /** The month now, months since January 2000: 0 after the constructor. */
    public int month() { return month; }

    /** The first month the rule rules: 1 on Random, 312 on Historical. */
    public int ruleStart() { return ruleStart; }

    /** Whether the rule rules this month (Historical replays the file before it). */
    public boolean ruling() { return month >= ruleStart; }

    /**
     * Records the terms of trade of a January the constructor could not be given (the founding's: the city begins in January 2000), as last year's
     * for the next January's change; steps nothing.
     */
    public void recordTermsOfTrade(double[] lnTermsOfTrade) {
        checkTermsOfTrade(lnTermsOfTrade);
        deltas(lnTermsOfTrade);
    }

    /**
     * Steps to month t, which has to be the next. lnTermsOfTrade is each nation's ln terms of trade (index nation - 1, WorldMarket.lnTermsOfTrade)
     * in a January the market solved, null in every other month. Before the rule's first month (Historical's replay) the money takes the file's
     * figure, and the terms of trade of a January are only remembered.
     */
    public void step(int t, double[] lnTermsOfTrade) {
        if (t != month + 1) throw new IllegalArgumentException("world moneys: the month after " + month + " is " + (month + 1) + ", not " + t);
        checkTermsOfTrade(lnTermsOfTrade);
        month = t;
        riskStarted = false;
        Arrays.fill(broke, false);
        Arrays.fill(gap, 0);
        if (t < ruleStart) {
            replay(t);
            if (t % 12 == 0 && lnTermsOfTrade != null) deltas(lnTermsOfTrade);
            if (t == ruleStart - 1) open(t);
            return;
        }
        rule(t, lnTermsOfTrade);
    }

    private void checkTermsOfTrade(double[] ln) {
        if (ln != null && ln.length != nationCount)
            throw new IllegalArgumentException("world moneys: " + ln.length + " terms of trade for " + nationCount + " nations");
    }

    /** Each nation's change in ln terms of trade on the last January's (0 for one with no last), the last now this one's; a figure that is no number is no news. */
    private double[] deltas(double[] ln) {
        double[] delta = new double[nationCount];
        for (int i = 0; i < nationCount; i++) {
            if (!Double.isFinite(ln[i])) continue;
            if (hasLast[i]) delta[i] = ln[i] - lastTot[i];
            lastTot[i] = ln[i];
            hasLast[i] = true;
        }
        return delta;
    }

    /** A month of the rule (the study's 3.7), in its order: the cycle, the risk-off, the terms of trade, the swings, then the rates and the pegs' breaks. */
    private void rule(int t, double[] lnTermsOfTrade) {
        // 1. The dollar cycle.
        cycle = PHI_CYCLE * cycle + (quiet ? 0 : CYCLE_SIGMA * gauss(WORLD, t, CYCLE_DRAW));
        // 2. The risk-off: what is left of last month's, and a new one with its chance.
        riskOff *= PHI_RISK_OFF;
        double size = 0;
        if (forced > 0) {
            size = forced;
            forced = 0;
        } else if (!quiet && unit(WORLD, t, RISK_START_DRAW) < RISK_OFF_RATE) {
            size = RISK_OFF_SIZE_MIN + (RISK_OFF_SIZE_MAX - RISK_OFF_SIZE_MIN) * unit(WORLD, t, RISK_SIZE_DRAW);
        }
        if (size > 0) {
            riskOff += size;
            riskStarted = true;
        }
        // 3. The terms of trade: a January's change, pending for the year, reaches the rate a third in each of its first months.
        int moy = t % 12;
        if (moy == 0) {
            Arrays.fill(pending, 0);
            if (lnTermsOfTrade != null) {
                double[] delta = deltas(lnTermsOfTrade);
                for (int k = 0; k < m; k++) {
                    int a = anchor[k];
                    if (a > 0 && live[k] && present[k] && k != dollar) pending[k] = -TOT_PASS * share[a - 1] * delta[a - 1];
                }
            }
        }
        if (moy < TOT_MONTHS) for (int k = 0; k < m; k++) tot[k] += pending[k] / TOT_MONTHS;
        // 4. Each money's own swing, and 5. the shadow: where the rule has the money, which a float is.
        for (int k = 0; k < m; k++) {
            mark[k] = 0;
            if (k == dollar || !live[k] || !present[k]) continue;
            swing[k] = PHI_OWN * swing[k] + (quiet ? 0 : sigma[k] * gauss(who[k], t, OWN_DRAW));
            lnShadow[k] = parity(k, t) + cycleLoad[k] * cycle + riskLoad[k] * riskOff + tot[k] + swing[k];
        }
        for (int k = 0; k < m; k++) resolve(k, t);
    }

    /**
     * Money k's rate this month: a float's is its shadow; a held peg's is its ratio over the rate of the money it is pegged to (worked first: pegs
     * chain, and a loop holds last month's), and its gap x from the shadow (positive: the shadow is weaker) is the chance it breaks by, a soft peg
     * only: the rate lands at the shadow plus OVERSHOOT of the gap again, the overshoot is added to the swing so it fades with it, and the money
     * floats from then on. A money the rule does not rule (a dead one on Historical, one never present) keeps its rate. Returns the ln rate.
     */
    private double resolve(int k, int t) {
        if (mark[k] != 0) return lnRate[k];   // done this month, or in progress: a loop, and last month's rate
        mark[k] = 1;
        if (k == dollar) {
            lnRate[k] = 0;
            rate[k] = 1;
        } else if (live[k] && present[k]) {
            if (!held[k]) {
                lnRate[k] = lnShadow[k];
            } else {
                int to = target[k];
                double ln = mark[to] == 1 ? lnRate[k] : pegRatio[k] + resolve(to, t);
                double x = lnShadow[k] - ln;
                gap[k] = x;
                if (!board[k] && !quiet && unit(who[k], t, BREAK_DRAW) < ForeignAccounts.hazard(Math.max(0, x))) {
                    double overshoot = ForeignAccounts.OVERSHOOT * Math.max(0, x);
                    swing[k] += overshoot;
                    lnShadow[k] += overshoot;
                    ln = lnShadow[k];
                    held[k] = false;
                    broke[k] = true;
                }
                lnRate[k] = ln;
            }
            rate[k] = StrictMath.exp(lnRate[k]);
        }
        mark[k] = 2;
        return lnRate[k];
    }

    /* =====================================================================
       THE DRAWS - pure functions of (seed, who, month, what)
       ===================================================================== */

    private double gauss(long who, int t, int what) { return new Random(Nations.scramble(seed, who, t, what)).nextGaussian(); }

    private double unit(long who, int t, int what) { return new Random(Nations.scramble(seed, who, t, what)).nextDouble(); }

    /* =====================================================================
       WHAT A CALLER READS - a code the model does not carry reads NaN, null or false
       ===================================================================== */

    /** The moneys it carries, sorted by code (a few may have no rate yet, or at all: NaN). */
    public List<String> codes() { return Collections.unmodifiableList(Arrays.asList(code)); }

    private int at(String c) {
        Integer k = index.get(c);
        return k == null ? -1 : k;
    }

    /** A money's units to the US dollar this month: 1 for the dollar, the file's figure on Historical's replay, NaN for a money it does not carry or that has no rate. */
    public double rate(String c) {
        int k = at(c);
        return k < 0 || !present[k] ? Double.NaN : rate[k];
    }

    /** Units of b for one a, through the dollar: rate(b) / rate(a). */
    public double cross(String a, String b) { return rate(b) / rate(a); }

    /** The rate of a nation's money this year (the year of the month now; the money Nations says it is on). */
    public double rateOfNation(int nation) {
        return rate(nations.year(nation, Nations.FIRST_YEAR + month / 12).money());
    }

    /** A money's rate in January 2000, which both paths begin from. */
    public double rateAtStart(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : startRate[k];
    }

    /** The dollar cycle C, a log: positive, the dollar is strong. */
    public double cycle() { return cycle; }

    /** The risk-off R this month: 0 in calm, the size of the last one fading by half in RISK_OFF_HALF_LIFE months. */
    public double riskOff() { return riskOff; }

    /** Whether a risk-off started this month. */
    public boolean riskOffStarted() { return riskStarted; }

    /** The moneys whose pegs broke this month. */
    public List<String> breaksThisMonth() {
        List<String> list = new ArrayList<>();
        for (int k = 0; k < m; k++) if (broke[k]) list.add(code[k]);
        return list;
    }

    /** Whether a money is ruled: carried, with a rate, and one the nations still use once the rule runs (Historical's old moneys, the lira and the drachma, keep their last rate). */
    public boolean ruled(String c) {
        int k = at(c);
        return k >= 0 && live[k] && present[k];
    }

    /** Whether a money's peg holds now (false for a float, for a peg that has broken, and on Historical before the rule runs, when the file's rates are the rates). */
    public boolean pegHeld(String c) {
        int k = at(c);
        return k >= 0 && held[k];
    }

    /** The money its regime pegs or ties it to (it stays when the peg breaks); null for a float. */
    public String pegAnchor(String c) {
        int k = at(c);
        return k < 0 || target[k] < 0 ? null : code[target[k]];
    }

    /** Whether it is a currency board, which never breaks. */
    public boolean isBoard(String c) {
        int k = at(c);
        return k >= 0 && board[k];
    }

    /** The gap x the peg faced this month, a log (positive: the shadow weaker than the peg); 0 for a money that faced no check. */
    public double pegGap(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : gap[k];
    }

    /** Where the rule has the money, units a dollar: its rate if it floats; beside a held peg, the pressure on it. On Historical's replay, the rate. */
    public double shadow(String c) {
        int k = at(c);
        return k < 0 || !present[k] ? Double.NaN : k == dollar ? 1 : StrictMath.exp(lnShadow[k]);
    }

    /** The money's parity rate this month, units a dollar: its constant and its anchor's prices against the US's. */
    public double ppp(String c) {
        int k = at(c);
        return k < 0 || !present[k] ? Double.NaN : StrictMath.exp(parity(k, month));
    }

    /** The money's own swing S, a log. */
    public double swing(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : swing[k];
    }

    /** The terms of trade reached T, a log: negative, the money dearer. */
    public double totReached(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : tot[k];
    }

    /** The money's loading on the dollar cycle, b. */
    public double cycleLoading(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : cycleLoad[k];
    }

    /** The money's loading on the risk-off, r (negative: it rises in a panic). */
    public double riskOffLoading(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : riskLoad[k];
    }

    /** The monthly sd of the money's own swing's innovation (0 while the test hook has it quiet). */
    public double sigma(String c) {
        int k = at(c);
        return k < 0 ? Double.NaN : quiet ? 0 : sigma[k];
    }

    /** The money's kind, by name ("euro", "advanced float", "emerging float"...). */
    public String kind(String c) {
        int k = at(c);
        return k < 0 ? null : kindName[k];
    }

    /** The nation whose price level is the money's, 1-based; 0 for a money it does not carry. */
    public int anchorOf(String c) {
        int k = at(c);
        return k < 0 ? 0 : anchor[k];
    }

    /** A nation's commodity share of exports, as it was given. */
    public double commodityShare(int nation) {
        if (nation < 1 || nation > nationCount) throw new IllegalArgumentException("world moneys: no nation numbered " + nation);
        return share[nation - 1];
    }

    /* =====================================================================
       THE SAVE - the state, not what the file and the path give
       ===================================================================== */

    private static final int HEADER = 4, GLOBALS = 5, PER_MONEY = 11;

    /**
     * The state as numbers: a header (the format, the money count, the nation count and a fingerprint of the money codes, so a restore against another
     * file is refused), the month, the opening, C, R and whether a risk-off started, each money's eleven figures, and each nation's last terms of trade.
     * Nothing is NaN, so it goes through JSON. A WorldMoneys of the same file, nations and shares that restores it steps on as this one does.
     */
    public double[] toSaveArray() {
        double[] out = new double[HEADER + GLOBALS + PER_MONEY * m + 2 * nationCount];
        int p = 0;
        out[p++] = SAVE_FORMAT;
        out[p++] = m;
        out[p++] = nationCount;
        out[p++] = Arrays.hashCode(code);
        out[p++] = month;
        out[p++] = opened ? 1 : 0;
        out[p++] = cycle;
        out[p++] = riskOff;
        out[p++] = riskStarted ? 1 : 0;
        double[][] columns = {rate, lnRate, lnShadow, swing, tot, pending, pegRatio, gap};
        for (double[] column : columns) {
            for (int k = 0; k < m; k++) out[p++] = present[k] || column != rate ? column[k] : 0;
        }
        for (int k = 0; k < m; k++) out[p++] = held[k] ? 1 : 0;
        for (int k = 0; k < m; k++) out[p++] = present[k] ? 1 : 0;
        for (int k = 0; k < m; k++) out[p++] = broke[k] ? 1 : 0;
        for (int i = 0; i < nationCount; i++) out[p++] = hasLast[i] ? lastTot[i] : 0;
        for (int i = 0; i < nationCount; i++) out[p++] = hasLast[i] ? 1 : 0;
        return out;
    }

    /** Puts the state back from toSaveArray; throws IllegalArgumentException, and changes nothing, for one made on another file (another count of moneys or nations, other codes) or another format. */
    public void restore(double[] saved) {
        Objects.requireNonNull(saved, "saved");
        int expected = HEADER + GLOBALS + PER_MONEY * m + 2 * nationCount;
        if (saved.length < HEADER) throw new IllegalArgumentException("world moneys: the save is " + saved.length + " numbers long and has no header");
        if (saved[0] != SAVE_FORMAT) throw new IllegalArgumentException("world moneys: the save is format " + saved[0] + ", this reads " + SAVE_FORMAT);
        if (saved[1] != m || saved[2] != nationCount || saved[3] != Arrays.hashCode(code))
            throw new IllegalArgumentException("world moneys: the save holds " + (long) saved[1] + " moneys and " + (long) saved[2] + " nations, and this file has "
                    + m + " moneys and " + nationCount + " nations (or other money codes)");
        if (saved.length != expected) throw new IllegalArgumentException("world moneys: the save is " + saved.length + " numbers long, this file's is " + expected);
        int p = HEADER;
        month = (int) saved[p++];
        opened = saved[p++] != 0;
        cycle = saved[p++];
        riskOff = saved[p++];
        riskStarted = saved[p++] != 0;
        double[][] columns = {rate, lnRate, lnShadow, swing, tot, pending, pegRatio, gap};
        for (double[] column : columns) for (int k = 0; k < m; k++) column[k] = saved[p++];
        for (int k = 0; k < m; k++) held[k] = saved[p++] != 0;
        for (int k = 0; k < m; k++) present[k] = saved[p++] != 0;
        for (int k = 0; k < m; k++) broke[k] = saved[p++] != 0;
        for (int i = 0; i < nationCount; i++) lastTot[i] = saved[p++];
        for (int i = 0; i < nationCount; i++) hasLast[i] = saved[p++] != 0;
        for (int k = 0; k < m; k++) if (!present[k]) rate[k] = Double.NaN;
        cacheYear = -1;
    }

    /* =====================================================================
       FOR THE CHECK - what a harness does that the game does not
       ===================================================================== */

    /**
     * Isolates one term for a section: from now every sigma, the risk-off's chance and the pegs' hazard read 0, and the cycle, the risk-off, every
     * swing and the terms of trade reached are cleared, so the rate of a float is its parity path and what the section then adds.
     */
    void quiet() {
        quiet = true;
        cycle = riskOff = 0;
        Arrays.fill(swing, 0);
        Arrays.fill(tot, 0);
        Arrays.fill(pending, 0);
    }

    /** A risk-off of this size starts at the next step of the rule, whatever the draw says. */
    void forceRiskOff(double size) { forced = size; }
}

package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The city's figure for each of the Rankings' 70 measures (spec-leaderboards 6,
 * "Where the game's side comes from"), worked out of its HistorySave and the
 * few figures a history does not keep.
 *
 * WHY. The board ranks the active city beside the real world on measures the
 * real world publishes in its own units (a rate per 1,000 people, a share of
 * GDP, a year's wage in whole dollars), and the game keeps its figures as the
 * model counts them: money in thousands, a month at a time, a rate as a
 * fraction, a share of GDP not at all. Left to the screen, each of those
 * conversions would be written where it is drawn and drift from the next. Here
 * it is one pure function of a history, so the board reads the live game
 * (Rankings.City.of(Game)) and LB3 reads a saved city's history file with the
 * same code, and nothing in here reads Game.
 *
 * UNITS (the model counts money in THOUSANDS, ui/Money). Every
 * money figure this makes is in WHOLE units of the city's money, and a flow is a
 * year of it. The series that are thousands, each multiplied by THOUSANDS where it
 * becomes a figure: gdp, consumption, totalWage and minimumWage (and so the average
 * wage and the minimum wage), and the ones that are only ever a share of gdp
 * (investment, government, netExports, exportsAbroad, importsAbroad,
 * currentAccount, m2, debt, surplus, the five taxes, fundValue, pensionBill,
 * healthBill, bankLent), where numerator and denominator are both thousands of
 * the city's money and need no factor; rentPrice, thousands against the average
 * wage's thousands, likewise. reservesUsd is thousands of US dollars: it meets
 * importsAbroad (thousands of the city's money, which a reform scales and the
 * rate is the city's money per US dollar) after dividing each month's imports by
 * that month's fxRate. None of the "abroad" series is a US dollar series but
 * reservesUsd and foreignDebtUsd (HistorySave, THE EDGE).
 *
 * THE RULES (one year, F(s) and friends). F(s) is series s summed over the last 12
 * months, scaled by 12 over n when the city has lived n < 12 months (n at least 1);
 * L(s) its last month; back(s, k) its value k months before. A series is read through
 * HistorySave.alignedTail, a month at a time; the months a history no longer holds
 * monthly (HistorySave.foldOldYears: the years past MONTHLY_KEPT) are never read as
 * months, so a measure that needs more months than the history holds as months is
 * absent, and so is one whose series has a month "not counting" (NaN) inside its
 * window. A measure the city has no figure for is absent from the map, never 0.
 *
 * ABSENT IN LB2, each for its reason: participation (the workforce is every adult
 * of working age, the students and the prisoners included, so there is no inactivity
 * to rate); tertiary (students counts the people studying, and the bands carry no
 * age group to put it over, which is what a gross enrolment is); yield_10y (the city
 * has one borrowing rate and no ten-year yield); debt_foreign (lane A is rebuilding
 * the city's foreign-money debt: stay off it); interest_revenue (the interest paid is
 * not a series of its own; the debt's rate times its principal would only estimate
 * it); credit_rating (no agency); bad_loans (the bank keeps write-offs and
 * provisions, flows, and no share of its loans past due); stock_market (a share is
 * priced per founding share, and no market capitalisation is kept); hh_debt (only the
 * student loans are kept, with no household income to put them over); aged_65 (the
 * bands break at 70, not 65); sharing (no series says how many households share a
 * home); price_income (no price of a home is kept, only the month's rent);
 * electricity_pp, water_pp (energyRatio and waterRatio are delivered over drawn, a
 * ratio, not an amount); renewable_share, cars_per_1000, transit, oil (not kept).
 * ADDED, a history series after all: police, the coverage (staffed officers over
 * Crime.FULL_OFFICERS_PER_100K) times that full figure, officers per 100,000; the
 * coverage is capped at 1, so a city above full coverage reads the full figure.
 *
 * WHERE A RULE OF THE BRIEF DID NOT FIT WHAT THE SERIES IS, and the book's own
 * definition was taken: unemployment is YearBook.unemploymentTail, the people out
 * of work over the LABOUR force (workforce less students and prisoners), not over
 * the workforce, which still counts them; the average wage is YearBook.averageWage,
 * the wage bill over the posts FILLED, not over the jobs offered. The minimum wage is
 * the floor in today's money (Extras.cashMinimumWage, which holds the player's nudge
 * and the cost of living the wages have reached) where the caller has it, else the
 * history's minimumWage (the REAL floor, at founding prices) times the price index;
 * either is a monthly pay for the game's full-time month, so a year of it is twelve
 * (PayTier: "a Job Bank median at 173 hours a month"), no hours constant to apply.
 *
 * DEATHS (deaths) are the six bands' deaths added, not the deaths series, which is the same dead rounded to whole people each month and so reads 0
 * for a village's 0.4 a month (births, arrivals and departures are rounded counts too, and are read as the history keeps them).
 *
 * THE LIFE TABLE (life_exp) reads the six bands' deaths; deathsOrphans and
 * deathsUnhoused are INSIDE them (Unemployment.attributeDeaths splits the bands'
 * dead, it adds none), so the six sum to the month's deaths up to the rounding of
 * the deaths series, and neither is added.
 */
public final class CityMeasures {

    private CityMeasures() { }

    /** The model counts money in thousands (ui/Money): this turns a money series' figure into whole units of the city's money. */
    public static final double THOUSANDS = 1_000;

    /** The months in a year: the window F(s) sums, and the distance inflation and population growth look back. */
    public static final int YEAR = 12;

    /** Months the real growth of a year over the year before needs: two years. */
    public static final int GROWTH_MONTHS = 2 * YEAR;

    /** Years the long growth compounds over, and the months it needs monthly: that decade, and the year after it (growth_10y's 12 months ending 120 back, and the last 12). */
    public static final int DECADE = 10, DECADE_MONTHS = DECADE * YEAR + YEAR;

    /** The ceiling the bank's leverage ratio is recorded at (HistorySave: clamped at ten, which is also what a city with no book reads); a city at it has no ratio to rank. */
    public static final double LEVERAGE_CEILING = 10;

    /** What the history does not keep: read live by Rankings.City.of(Game), from a save's header in LB3. */
    public record Extras(double areaKm2, double[] bandPeople /* by AgeBand.ordinal(); NaN where unknown */, double cashMinimumWage /* the wage floor a month in today's money, in thousands as the model counts it; NaN where unknown */) {
        /** Extras without the wage floor in today's money: the minimum wage is then the history's floor lifted by the price index. */
        public Extras(double areaKm2, double[] bandPeople) { this(areaKm2, bandPeople, Double.NaN); }

        /** The people of a band, NaN where the extras do not know. */
        double people(AgeBand band) {
            return bandPeople == null || band.ordinal() >= bandPeople.length ? Double.NaN : bandPeople[band.ordinal()];
        }
    }

    /**
     * A figure in the measure's own unit, its value at founding's prices where the measure has a constant series (gdp, gdp_pp, hh_spend_pp; NaN otherwise), and a few
     * words for the drawer on how it is made. Money (gdp, gdp_pp, hh_spend_pp, avg_wage, min_wage) is in WHOLE units of the city's money, a flow over a year: the
     * history's gdp, consumption and totalWage series, the wage floor (minimumWage, Extras.cashMinimumWage) and every other money series are thousands, and these
     * five are made from them times THOUSANDS; the rest of the money series (investment, government, netExports, exportsAbroad, importsAbroad, currentAccount, m2,
     * debt, surplus, the taxes, fundValue, pensionBill, healthBill, bankLent, rentPrice, reservesUsd) are thousands too and are used only against another thousands
     * series, so a share needs no factor. A percent measure is a percent (2.5, not .025), a ratio a ratio.
     */
    public record Figure(double value, double real, String how) { }

    /** Every measure the city has a figure for at the history's last month, in the file's order; a measure it has none for is absent, never 0. */
    public static Map<String, Figure> of(HistorySave h, Extras x) {
        Map<String, Figure> out = new LinkedHashMap<>();
        if (h == null) return out;
        Reading r = new Reading(h);
        if (r.monthly < 1) return out;
        if (x == null) x = new Extras(Double.NaN, null);
        String per = r.window < YEAR ? " (" + r.window + " months, scaled to a year)" : "";

        // ---- economy ----
        double gdp = r.flow("gdp"), gdpReal = r.flowDeflated("gdp"), pop = r.last("population");
        put(out, "gdp", gdp * THOUSANDS, gdpReal * THOUSANDS, "a year of output, C+I+G+NX, in the city's money" + per);
        put(out, "gdp_pp", gdp * THOUSANDS / pop, gdpReal * THOUSANDS / pop, "a year of output over the people now" + per);
        double a = r.sumReal("gdp", GROWTH_MONTHS, 0, YEAR), b = r.sumReal("gdp", GROWTH_MONTHS, YEAR, GROWTH_MONTHS);
        put(out, "growth_real", positive(a, b) ? 100 * (b / a - 1) : Double.NaN, Double.NaN, "the last 12 months of output at founding's prices over the 12 before");
        a = r.sumReal("gdp", DECADE_MONTHS, 0, YEAR);
        b = r.sumReal("gdp", DECADE_MONTHS, DECADE_MONTHS - YEAR, DECADE_MONTHS);
        put(out, "growth_10y", positive(a, b) ? 100 * (Math.pow(b / a, 1.0 / DECADE) - 1) : Double.NaN, Double.NaN,
                "a year of output at founding's prices over the year ten years back, a rate a year");
        put(out, "hh_spend_pp", r.flow("consumption") * THOUSANDS / pop, r.flowDeflated("consumption") * THOUSANDS / pop,
                "a year of households' purchases over the people now" + per);
        put(out, "investment", share(r.flow("investment"), gdp), Double.NaN, "a year of investment over a year of output" + per);
        put(out, "gov_spend", share(r.flow("government"), gdp), Double.NaN, "a year of what the city's own services cost to provide, over output" + per);
        put(out, "exports", share(r.flow("exportsAbroad"), gdp), Double.NaN, "a year sold abroad over output, the city's money" + per);
        put(out, "imports", share(r.flow("importsAbroad"), gdp), Double.NaN, "a year bought abroad over output, the city's money" + per);
        put(out, "trade_balance", share(r.flow("netExports"), gdp), Double.NaN, "exports less imports over output" + per);
        put(out, "current_account", share(r.flow("currentAccount"), gdp), Double.NaN, "exports less imports less interest paid abroad, over output" + per);

        // ---- work and pay ----
        double[] unemployed = YearBook.unemploymentTail(h, 1);
        put(out, "unemployment", 100 * unemployed[0], Double.NaN,
                "people out of work over the labour force: the workforce less the students and the prisoners");
        double[] wages = YearBook.averageWage(h);
        double avgWage = wages.length > 0 ? wages[wages.length - 1] * YEAR * THOUSANDS : Double.NaN;
        put(out, "avg_wage", avgWage, Double.NaN, "the month's wage bill over the posts filled, times 12");
        double minWage;
        String minHow;
        if (x.cashMinimumWage() > 0) {
            minWage = x.cashMinimumWage() * YEAR * THOUSANDS;
            minHow = "the wage floor a month in today's money, times 12 (a full-time month)";
        } else {
            minWage = r.last("minimumWage") * r.last("priceIndex") * YEAR * THOUSANDS;
            minHow = "the floor at founding prices lifted by the price index, times 12 (the player's nudge and the wages' lag are not in the history)";
        }
        put(out, "min_wage", minWage, Double.NaN, minHow);
        put(out, "kaitz", minWage / avgWage, Double.NaN, "the minimum wage over the average wage");

        // ---- prices and money ----
        double inflation = r.monthly > YEAR ? 100 * (r.last("priceIndex") / r.ago("priceIndex", YEAR) - 1) : Double.NaN;
        put(out, "inflation", inflation, Double.NaN, "the price index over its reading twelve months ago");
        put(out, "policy_rate", 100 * r.last("policyRate"), Double.NaN, "the central bank's dial now, a rate a year");
        put(out, "real_rate", 100 * r.last("bankPrime") - inflation, Double.NaN, "what a sound business pays the bank, less inflation");
        double fx = r.last("fxRate"), parity = r.last("fxParity");
        put(out, "fx_vs_parity", fx / parity, Double.NaN, "the market rate over the rate where a basket costs the same here and abroad");
        put(out, "reserves_months", reservesMonths(r), Double.NaN, "the vault in dollars over a month's imports at each month's rate" + per);
        put(out, "m2", share(r.last("m2"), gdp), Double.NaN, "what the public holds, over a year of output");
        put(out, "price_level", parity / fx, Double.NaN, "parity over the rate: a basket here against the same abroad");

        // ---- public money ----
        put(out, "debt", share(r.last("debt"), gdp), Double.NaN, "the city's own borrowing over a year of output");
        put(out, "budget_balance", share(r.flow("surplus"), gdp), Double.NaN, "a year of revenue less spending over output" + per);
        double taxes = r.flow("taxWage") + r.flow("taxProperty") + r.flow("taxSales") + r.flow("taxBusiness") + r.flow("taxIndustrial");
        put(out, "tax_revenue", share(taxes, gdp), Double.NaN, "the wage, property, sales, profit and industrial taxes over output" + per);
        put(out, "fund", share(r.last("fundValue"), gdp), Double.NaN, "everything the city's fund holds, over a year of output");
        put(out, "pensions", share(r.flow("pensionBill"), gdp), Double.NaN, "a year of pensions paid over output" + per);

        // ---- banks ----
        double leverage = r.last("bankLeverageRatio");
        put(out, "bank_capital", leverage < LEVERAGE_CEILING ? 100 * leverage : Double.NaN, Double.NaN, "the bank's equity over everything on its sheet");
        put(out, "credit_private", share(r.last("bankLent"), gdp), Double.NaN, "the bank's loan book over a year of output");

        // ---- people ----
        put(out, "people", pop, Double.NaN, "everyone alive in the city");
        put(out, "pop_growth", r.monthly > YEAR ? 100 * (pop / r.ago("population", YEAR) - 1) : Double.NaN, Double.NaN, "the people over the people twelve months ago");
        put(out, "births", 1000 * r.flow("births") / pop, Double.NaN, "a year of births per 1,000 people now" + per);
        put(out, "deaths", 1000 * bandDeaths(r) / pop, Double.NaN, "a year of deaths per 1,000 people now, the six bands' dead added" + per);
        double births = r.flow("births");
        put(out, "child_deaths", births > 0 ? 1000 * r.flow("deathsBabies") / births : Double.NaN, Double.NaN, "under 6, the city's youngest band");
        put(out, "life_exp", lifeExpectancy(r, x), Double.NaN, "computed: a life table from the city's six age bands");
        double adults = x.people(AgeBand.ADULT);
        put(out, "old_age_dep", 100 * (x.people(AgeBand.SENIOR) + x.people(AgeBand.ELDER)) / adults, Double.NaN, "70 and over against 18-69, the city's bands");
        put(out, "net_migration", 1000 * (r.flow("arrivals") - r.flow("departures")) / pop, Double.NaN, "a year of arrivals less departures per 1,000 people now" + per);
        put(out, "household_size", pop / r.last("households"), Double.NaN, "the people over the households, those outside the families included");
        double children = x.people(AgeBand.BABY) + x.people(AgeBand.CHILD) + x.people(AgeBand.TEEN);
        put(out, "orphans", 10_000 * r.last("orphans") / children, Double.NaN, "children with no adult per 10,000 under 18");

        // ---- housing ----
        put(out, "rent_income", 100 * r.last("rentPrice") / (avgWage / YEAR / THOUSANDS), Double.NaN, "a family home's month of rent over the average wage's month");
        put(out, "homes_per_1000", 1000 * r.last("homes") / pop, Double.NaN, "homes standing per 1,000 people");
        put(out, "homeless", 10_000 * r.last("unhoused") / pop, Double.NaN, "people with no home at all per 10,000");

        // ---- safety ----
        put(out, "homicides", 100_000 * r.flow("deathsKilled") / pop, Double.NaN, "adults killed by violence in a year per 100,000 people now" + per);
        put(out, "prisoners", 100_000 * r.last("prisoners") / pop, Double.NaN, "people in a cell per 100,000");
        put(out, "police", r.last("policeCoverage") * Crime.FULL_OFFICERS_PER_100K, Double.NaN, "officers staffed per 100,000 people: the coverage times full coverage, which it is capped at");
        put(out, "recorded_crime", r.mean("crimeRate"), Double.NaN, "the year's crimes per 100,000 people, the months' rates averaged" + per);

        // ---- health and care ----
        put(out, "health_spend", share(r.flow("healthBill"), gdp), Double.NaN, "healthcare's net cost to the treasury over output; private spending is not kept" + per);
        put(out, "sick", 100 * r.last("sickRate"), Double.NaN, "the share of the workforce off sick");
        put(out, "hungry", 100 * r.last("hunger"), Double.NaN, "the share of the city short of a basket a month");
        put(out, "care", 100 * r.last("careCoverage"), Double.NaN, "general-care coverage");

        // ---- land ----
        double area = x.areaKm2();
        put(out, "area", area > 0 ? area : Double.NaN, Double.NaN, "the land the city owns");
        put(out, "density", pop / area, Double.NaN, "the people over the land the city owns");
        return out;
    }

    /** Adds a figure when it is a number a city can have: finite, and a real value finite or NaN. */
    private static void put(Map<String, Figure> out, String key, double value, double real, String how) {
        if (!Double.isFinite(value)) return;
        out.put(key, new Figure(value, Double.isFinite(real) ? real : Double.NaN, how));
    }

    /** Whether every number is a finite one over nothing: a growth's two sums need both over 0. */
    private static boolean positive(double... v) {
        for (double d : v) if (!(d > 0) || !Double.isFinite(d)) return false;
        return true;
    }

    /** 100 x part over the year of output; NaN where output is not over 0, so the measure is absent. */
    private static double share(double part, double gdp) {
        return gdp > 0 ? 100 * part / gdp : Double.NaN;
    }

    /** The vault in months of imports: the last month's reservesUsd over a month's imports in US dollars, the window's months each at that month's rate. NaN where there are no imports to cover. */
    private static double reservesMonths(Reading r) {
        double[] imports = r.tail("importsAbroad", r.window), rate = r.tail("fxRate", r.window);
        if (imports == null || rate == null) return Double.NaN;
        double usd = 0;
        for (int i = 0; i < imports.length; i++) {
            if (!(rate[i] > 0)) return Double.NaN;
            usd += imports[i] / rate[i];
        }
        double perMonth = usd / imports.length;
        return perMonth > 0 ? r.last("reservesUsd") / perMonth : Double.NaN;
    }

    /** The six bands' deaths, in AgeBand order, by the series that carry them. */
    private static final String[] BAND_DEATHS = { "deathsBabies", "deathsChildren", "deathsTeens", "deathsAdults", "deathsSeniors", "deathsElders" };

    /**
     * F of the six bands' deaths added: a year of the city's dead. The deaths series is the same deaths rounded to whole people each month
     * (HistorySave.recordMonth), which in a village of 650 (0.4 dead a month) reads 0 and understates its death rate five-fold; the bands keep
     * them to the hundredth.
     */
    private static double bandDeaths(Reading r) {
        double dead = 0;
        for (String band : BAND_DEATHS) dead += r.flow(band);
        return dead;
    }

    /** Life expectancy at birth from the six bands' last year of deaths over the people now in them; NaN under a year lived, or with a band empty. */
    private static double lifeExpectancy(Reading r, Extras x) {
        if (r.monthly < YEAR) return Double.NaN;
        double[] hazard = new double[AgeBand.values().length];
        for (AgeBand band : AgeBand.values()) {
            double people = x.people(band);
            double[] dead = r.tail(BAND_DEATHS[band.ordinal()], YEAR);
            if (dead == null || !(people > 0)) return Double.NaN;
            double sum = 0;
            for (double d : dead) sum += d;
            hazard[band.ordinal()] = sum / people;
        }
        return lifeTable(hazard);
    }

    /**
     * A period life table over the six bands (spec-leaderboards 6): hazard[b] is a year's chance of dying, as a rate, in band b. With l the share
     * alive at a band's start (1 at birth) and w its width in years, the years lived in a closed band are l (1 - e^(-m w)) / m (l w where m is 0) and
     * l becomes l e^(-m w); the last band is open, l / m. The sum is the life expectancy; NaN where the open band's hazard is not over 0 (no one
     * dies there, so no age is reached) or any is not a number. A flat hazard m gives 1 / m exactly, which LB2's check holds it to.
     */
    static double lifeTable(double[] hazard) {
        AgeBand[] bands = AgeBand.values();
        double alive = 1, lived = 0;
        for (int b = 0; b < bands.length; b++) {
            double m = hazard[b];
            if (!Double.isFinite(m) || m < 0) return Double.NaN;
            if (b == bands.length - 1) return m > 0 ? lived + alive / m : Double.NaN;
            double w = bands[b].getToAge() - bands[b].getFromAge();
            lived += m == 0 ? alive * w : alive * (1 - Math.exp(-m * w)) / m;
            alive *= Math.exp(-m * w);
        }
        return Double.NaN;
    }

    /**
     * A history read the way the rules above say: the series a month at a time, in the entries that are months. monthly is how many of the axis's
     * entries are months (HistorySave.yearlyPoints() are folded years); window is the months a year's sum reaches back (12, or fewer in a city
     * that has lived fewer); scale lifts that sum to a year.
     */
    private static final class Reading {
        final HistorySave h;
        final int monthly, window;
        final double scale;

        Reading(HistorySave h) {
            this.h = h;
            monthly = h.months() - h.yearlyPoints();
            window = Math.min(YEAR, monthly);
            scale = window > 0 ? (double) YEAR / window : Double.NaN;
        }

        /** The last n months of a series, or null when the history holds fewer than n as months. */
        double[] tail(String series, int n) {
            if (n < 1 || n > monthly) return null;
            return h.alignedTail(series, n);
        }

        /** F(s): the series summed over the window and scaled to a year; NaN where a month in it was not counted. */
        double flow(String series) {
            double[] t = tail(series, window);
            if (t == null) return Double.NaN;
            double sum = 0;
            for (double v : t) sum += v;
            return sum * scale;
        }

        /** The window's series each month over that month's price index, summed and scaled to a year: output at founding's prices. */
        double flowDeflated(String series) {
            double[] t = tail(series, window), index = tail("priceIndex", window);
            if (t == null || index == null) return Double.NaN;
            double sum = 0;
            for (int i = 0; i < t.length; i++) {
                if (!(index[i] > 0)) return Double.NaN;
                sum += t[i] / index[i];
            }
            return sum * scale;
        }

        /** The last `months` months of a series each over that month's price index, summed over entries from (inclusive) to to (exclusive), counted from the oldest of them. */
        double sumReal(String series, int months, int from, int to) {
            double[] t = tail(series, months), index = tail("priceIndex", months);
            if (t == null || index == null) return Double.NaN;
            double sum = 0;
            for (int i = from; i < to; i++) {
                if (!(index[i] > 0)) return Double.NaN;
                sum += t[i] / index[i];
            }
            return sum;
        }

        /** L(s): the series' last month. */
        double last(String series) {
            double[] t = tail(series, 1);
            return t == null ? Double.NaN : t[0];
        }

        /** back(s, k): the series k months before the last. */
        double ago(String series, int k) {
            double[] t = tail(series, k + 1);
            return t == null ? Double.NaN : t[0];
        }

        /** The mean of the window, for a series that is already a rate a year. */
        double mean(String series) {
            double[] t = tail(series, window);
            if (t == null) return Double.NaN;
            double sum = 0;
            for (double v : t) sum += v;
            return sum / t.length;
        }
    }
}

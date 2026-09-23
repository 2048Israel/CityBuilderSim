package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The run, one line a year - for READING rather than for drawing.
 *
 * WHY THIS EXISTS
 *
 * Jerus, 2026-09-14: "i want another txt or something which is different, it
 * basically summarizes not by month, but by year, all the stuff, so that you or
 * another ai, can be sent that, and doesnt die due to tokens... so that they can
 * analyze the economic situation of the run."
 *
 * The history is every month the city has ever lived across a hundred-odd series.
 * A 333-year run is forty thousand numbers a series and nobody - person or
 * model - can hold it. Twelve months folded into one row divides it by twelve
 * and loses almost nothing an economy is judged on, because an economy is
 * judged on years.
 *
 * WHY EVERY COLUMN CARRIES ITS OWN RULE
 *
 * A year is not one operation. Summing a population gives twelve times the city;
 * averaging GDP gives a month and calls it a year; summing an interest rate is
 * meaningless in any unit. So each series declares what it IS - a flow, a level
 * or a rate - and the fold follows from that. The rules are a table in this
 * file rather than a guess from the name, and YearBookCheck asserts the table
 * covers every series HistorySave keeps. A series added without a rule still
 * appears, marked, rather than silently vanishing from the analysis.
 *
 * WHY THE EXTREMES ARE HERE TOO
 *
 * A year's average hides the month the bank failed and the epidemic that ran in
 * March. That is the same lesson the skip report was built on: an episode
 * cannot be reconstructed from endpoints. So every rate column also reports its
 * worst and best single month inside the row, and the file ends with a plain
 * list of what actually happened and when.
 *
 * NOTHING HERE READS THE CITY. It is a pure function of a HistorySave, which is
 * what lets a harness build a history by hand and assert the arithmetic, and
 * what lets the export run on a loaded slot as happily as on the live game.
 */
public final class YearBook {

    /** How a column's months become one number. */
    public enum Kind {
        /** A monthly flow - the row's months ADDED. GDP, births, tax, write-offs. */
        FLOW,
        /** A stock - the value in the row's LAST month. Population, cash, deposits. */
        LEVEL,
        /** A rate, a price or an index - the row's months AVERAGED. */
        RATE
    }

    public static final int MONTHS_A_YEAR   = 12;
    public static final int MONTHS_A_DECADE = 120;

    private YearBook() { }

    /* ==================================================================
       THE RULES

       One entry per series HistorySave keeps. The dynamic families - crime
       by cause, households by shape, the share registers, and each sector's
       net income and workers (0.7.4) - are matched by prefix, because their
       keys are made from enum names and company names at runtime and cannot
       be listed here.
       ================================================================== */

    /**
     * What a series IS, and one line saying what it means and in what unit.
     *
     * The note is not decoration. This file exists to be handed to something
     * that has never seen the game, and a column called "taxIndustrial" with no
     * gloss is a number nobody can use. The units especially: money here is in
     * thousands and rates are fractions, and a reader who assumes otherwise is
     * wrong by a factor of a thousand and confident.
     */
    private record Rule(Kind kind, String note) { }

    private static Map<String, Rule> rules() {
        Map<String, Rule> m = new LinkedHashMap<>();

        /* ---- flows: a figure FOR the month, so a row is the sum ---- */
        flow(m, "gdp", "GDP by expenditure, C+I+G+NX, in thousands");
        flow(m, "consumption", "C, the households' final purchases - the shops' sales and the rent - in thousands");
        flow(m, "investment", "I, construction work done plus the change in stock, in thousands");
        flow(m, "government", "G, government consumption - what the city's own services cost to provide, in thousands");
        flow(m, "netExports", "NX, exports less imports - negative when the city imports more, in thousands");
        flow(m, "revenue", "every government receipt, in thousands");
        flow(m, "surplus", "revenue less spending - negative is a deficit, in thousands");
        flow(m, "births", "people born");
        flow(m, "deaths", "people who died, of all three causes together");
        flow(m, "arrivals", "people who moved in");
        flow(m, "departures", "people who left");
        flow(m, "totalWage", "the city's whole wage bill, in thousands");
        flow(m, "schoolBill", "what the schools cost the treasury net of tuition, in thousands");
        flow(m, "currentAccount", "exports less imports less interest paid abroad, in thousands");
        flow(m, "exportsAbroad", "sold abroad, in thousands");
        flow(m, "importsAbroad", "bought abroad, in thousands");
        flow(m, "bankWriteOffs", "loans the bank wrote off, in thousands");
        flow(m, "bankProfit", "the bank's net income - negative is a loss, in thousands");
        flow(m, "taxWage", "wage tax collected, in thousands");
        flow(m, "taxProperty", "property tax collected, in thousands");
        flow(m, "taxSales", "sales tax collected net of input credits, in thousands");
        flow(m, "taxBusiness", "profit tax from the bank and every sector except the food industry, in thousands");
        flow(m, "taxIndustrial", "the food industry's profit tax, kept on its own line, in thousands");
        flow(m, "contributions", "pension contributions collected, in thousands");
        flow(m, "pensionBill", "pensions paid, in thousands");
        flow(m, "healthBill", "healthcare's net cost to the treasury, in thousands");
        flow(m, "graduates", "movement between education bands - a flow, and it can be negative");
        flow(m, "evicted", "out-of-work households that lost their home");
        flow(m, "eiPaid", "unemployment benefit paid, in thousands");
        flow(m, "eiPremiums", "unemployment premiums collected, in thousands");
        flow(m, "healthPremiums", "health premiums collected off wages, in thousands");
        flow(m, "studentGrants", "grants paid to students, in thousands");
        flow(m, "studentLoanInterest", "interest the graduates paid on their student loans, in thousands");
        flow(m, "remittance", "the central bank's profit paid to the treasury, in thousands");
        flow(m, "diedOfIllness", "people the long sickness killed");
        flow(m, "deathsBabies", "deaths aged 0-5");
        flow(m, "deathsChildren", "deaths aged 6-12");
        flow(m, "deathsTeens", "deaths aged 13-17");
        flow(m, "deathsAdults", "deaths aged 18-69");
        flow(m, "deathsSeniors", "deaths aged 70 to 84");
        flow(m, "deathsElders", "deaths aged 85 and over");
        flow(m, "deathsOrphans", "deaths among children with no adult");
        flow(m, "deathsUnhoused", "deaths among people with no home");
        flow(m, "deathsKilled", "adults killed by violence");
        flow(m, "caughtNotHeld", "people arrested and released for want of a cell");
        flow(m, "stolen", "taken by theft, in thousands");
        flow(m, "safetyBill", "gross cost of police and prisons, in thousands");

        /* ---- levels: a stock, so a row is where it STOOD at the end ---- */
        level(m, "cash", "the treasury's cash in hand - negative is an overdraft, in thousands");
        level(m, "debt", "the city's own borrowing outstanding, in thousands");
        level(m, "businessDebt", "what the private sector owes the bank, in thousands");
        level(m, "jobs", "posts the city offers");
        level(m, "workforce", "adults of working age - the students and the prisoners are still in it");
        level(m, "outOfWork", "people out of work - the labour force less the posts filled, the People screen's figure");
        level(m, "population", "people alive");
        level(m, "reservesUsd", "the treasury's foreign reserves, in thousands of US dollars");
        level(m, "foreignDebtUsd", "borrowing in someone else's money, in thousands of US dollars");
        level(m, "m0", "M0: what the central bank owes - every dollar it has made and not taken back, in thousands");
        level(m, "m2", "M2: what the public holds - the bank's deposits, households', sectors' and foreign, plus currency, in thousands");
        level(m, "advancesToTreasury", "what the treasury owes the central bank in advances - the printed overdraft, in thousands");
        level(m, "reserves", "the central bank's reserves liability - M0 less currency, in thousands");
        level(m, "bankDeposits", "deposits the bank has gathered, in thousands");
        level(m, "bankLent", "the bank's loan book, in thousands");
        level(m, "bankEquity", "the bank's capital - negative means it has failed, in thousands");
        level(m, "bankCapacity", "the most the bank may lend, in thousands");
        level(m, "bankBranches", "branches standing - zero is a city with no bank");
        level(m, "householdSavings", "households' savings all told, in thousands");
        level(m, "homes", "homes standing");
        level(m, "households", "households, the people outside the families included");
        level(m, "students", "people studying, and out of the labour supply while they are");
        level(m, "licences", "licence holders across every gated job");
        level(m, "unburied", "bodies with nowhere to go - they make the living ill");
        level(m, "outOfWorkOnEi", "people drawing unemployment benefit");
        level(m, "outOfWorkOffEi", "people whose claim has run out");
        level(m, "unhoused", "people with no home at all");
        level(m, "orphans", "children with no adult");
        level(m, "studentLoansOwed", "student debt outstanding, in thousands");
        level(m, "sickPastTwoMonths", "people sick longer than two months - the ring that kills");
        level(m, "prisoners", "people in a cell");
        level(m, "constructionCapacity", "construction points the city can put out in a month");

        /* ---- rates, prices and indices: a row is the AVERAGE ---- */
        rate(m, "interestRate", "what the city pays to borrow, a fraction a year");
        rate(m, "minimumWage", "the dial, in thousands a month - the base of every wage in the city");
        rate(m, "unskilledPremium", "what an unskilled post pays over its band's base, a multiple");
        rate(m, "skilledShare", "share of adults holding at least a diploma");
        rate(m, "schoolCoverage", "basic schooling coverage, at the narrowest of the three stages");
        rate(m, "energyRatio", "power delivered over power drawn, capped at 1 - under 1 is a shortage");
        rate(m, "waterRatio", "water delivered over water drawn, capped at 1");
        rate(m, "roadRatio", "road throughput, 1 is free flow and 0.35 is the floor");
        rate(m, "sickRate", "share of the workforce off sick");
        rate(m, "careCoverage", "general-care coverage");
        rate(m, "sickRecovery", "share of the long sick who got better");
        rate(m, "landPrice", "what the world asks the city for ground, per square foot in thousands of US dollars since 0.7.6 (local money before)");
        rate(m, "foodPrice", "food, per unit in thousands");
        rate(m, "materialsPrice", "construction material, per unit in thousands");
        rate(m, "orePrice", "iron ore at the export floor, per tonne in thousands");
        rate(m, "rentPrice", "rent, per month in thousands");
        rate(m, "fxRate", "Danzik dollars per US dollar - 1.000 at founding, HIGHER is a fallen currency");
        rate(m, "priceIndex", "the fixed basket against its base month, 1.000 at the base");
        rate(m, "bankPremium", "points the strained bank adds to every rate in the city");
        rate(m, "bankStrain", "the book over capacity, clamped at 10 - past 1 it funds abroad");
        rate(m, "crimeRate", "crimes per 100,000 people a year - Canada is 5,585");
        rate(m, "policeCoverage", "staffed officers against full coverage, which is 360 per 100,000");
        rate(m, "landUse", "land allocated over land owned");
        return m;
    }

    private static void flow(Map<String, Rule> m, String k, String note)  { m.put(k, new Rule(Kind.FLOW, note)); }
    private static void level(Map<String, Rule> m, String k, String note) { m.put(k, new Rule(Kind.LEVEL, note)); }
    private static void rate(Map<String, Rule> m, String k, String note)  { m.put(k, new Rule(Kind.RATE, note)); }
    /**
     * The runtime families, matched on the part before the colon.
     *
     * The two share prefixes are taken from HistorySave's own key builders
     * rather than typed again here - they were typed again in the first cut and
     * were wrong ("price:" against the real "sharePrice:"), which put every
     * company in the city under "no rule declared". A prefix that is READ from
     * the thing it has to match cannot drift from it.
     */
    private static Map<String, Rule> prefixRules() {
        Map<String, Rule> m = new LinkedHashMap<>();
        m.put("crime:", new Rule(Kind.FLOW, "crimes of that cause"));
        m.put("households:", new Rule(Kind.LEVEL, "households of that shape standing"));
        m.put(HistorySave.priceKey(""), new Rule(Kind.RATE, "the dealer's quote per founding share, in thousands"));
        m.put(HistorySave.valueKey(""), new Rule(Kind.RATE, "fair value per founding share, in thousands"));
        m.put(HistorySave.netIncomeKey(""), new Rule(Kind.FLOW, "that sector's net income after tax - negative is a loss, in thousands"));
        m.put(HistorySave.workersKey(""), new Rule(Kind.LEVEL, "that sector's posts filled - the people it employs"));
        return m;
    }

    private static final Map<String, Rule> RULES = rules();
    private static final Map<String, Rule> PREFIXES = prefixRules();

    /**
     * The rule for a series, or null if nobody has declared one.
     *
     * Null rather than a guess or an exception: a player mid-run must still get
     * a file, and a wrong fold quoted as fact is worse than a named unknown.
     * The writer shows an unruled series as a December value AND says so;
     * YearBookCheck fails, which is where it should be caught.
     */
    public static Kind kindOf(String series) {
        Rule rule = ruleFor(series);
        return rule == null ? null : rule.kind();
    }

    /** What a series means, in one line, or null if nobody has said. */
    public static String noteOf(String series) {
        Rule rule = ruleFor(series);
        return rule == null ? null : rule.note();
    }

    private static Rule ruleFor(String series) {
        Rule exact = RULES.get(series);
        if (exact != null) return exact;
        for (Map.Entry<String, Rule> e : PREFIXES.entrySet()) {
            if (series.startsWith(e.getKey())) return e.getValue();
        }
        return null;
    }

    /** Every series in this history that nobody has declared a rule for. */
    public static List<String> unruled(HistorySave history) {
        List<String> out = new ArrayList<>();
        for (String name : history.seriesByName().keySet()) {
            if (kindOf(name) == null) out.add(name);
        }
        return out;
    }

    /* ==================================================================
       THE DERIVED COLUMNS

       Same argument as HistorySave's: nothing derived is stored, because a
       stored copy is a second number that can disagree with the two it came
       from. These are computed here, from the aligned series, so a month
       missing from an input is missing from the result.
       ================================================================== */

    private static final class Column {
        final String name;
        final Kind kind;
        final double[] monthly;
        final String note;
        Column(String name, Kind kind, double[] monthly, String note) {
            this.name = name; this.kind = kind; this.monthly = monthly; this.note = note;
        }
    }

    /* ==================================================================
       THE DERIVED SERIES THAT TWO SCREENS BOTH WANT

       These are public and they live here because they were struck TWICE:
       once for the book and once in HistoryScreen.historyValues() for the
       Reports chart, from the same wrong denominator. Both said 5.5%
       unemployment on a city with more posts than people to fill them, and
       fixing one left the other quietly wrong - the chart is the copy a
       player actually looks at.

       It is the same argument as PopulationManager.getUnemployed(), whose own
       comment says it lives in one place because "the wage bill was once
       computed in two places ... and the copies disagreed". One definition,
       and the graph and the file cannot disagree about the same city again.
       ================================================================== */

    /**
     * Who is actually available to work: the workforce less the people who
     * are not looking.
     *
     * `workforce` is NOT the labour force and never was - it still counts
     * every full-time student and every prisoner. PopulationManager takes
     * both off in getLabourForce(), and its comment records that counting
     * students as job-seekers was already found and fixed once, on the People
     * screen, in September. This is that same subtraction, done off a saved
     * history instead of a live city.
     *
     * A history from before students or prisoners were recorded contributes
     * nothing rather than a zero, which leaves the labour force reading as
     * the old `workforce` for those months - wrong in the same direction as
     * before, but only where the file has nothing better to offer.
     */
    public static double[] labourForce(HistorySave h) {
        double[] workforce = h.aligned("workforce");
        double[] students  = h.aligned("students");
        double[] prisoners = h.aligned("prisoners");
        double[] out = new double[workforce.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = workforce[i]
                    - (Double.isNaN(students[i])  ? 0 : students[i])
                    - (Double.isNaN(prisoners[i]) ? 0 : prisoners[i]);
        }
        return out;
    }

    /**
     * Posts with somebody in them - the labour force less the pool.
     *
     * `jobs` is posts OFFERED. A city can hold more posts than workers, and
     * slot 3 held about eleven thousand it could not fill for seventy years,
     * so the number of people being paid is neither the posts nor the labour
     * force. Where the pool was not recorded the smaller of the two is the
     * closest a history can get, and it is right whenever the city is not
     * simultaneously short of workers AND carrying unemployment.
     */
    public static double[] filledPosts(HistorySave h) {
        double[] force = labourForce(h);
        double[] jobs  = h.aligned("jobs");
        double[] pool  = h.aligned("outOfWork");
        double[] out = new double[force.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = Double.isNaN(pool[i])
                    ? Math.min(force[i], jobs[i])
                    : Math.max(0, force[i] - pool[i]);
        }
        return out;
    }

    /** THE definition of the city's unemployment rate off a history. */
    public static double[] unemployment(HistorySave h) {
        double[] force = labourForce(h);
        double[] jobs  = h.aligned("jobs");
        double[] pool  = h.aligned("outOfWork");
        double[] out = new double[force.length];
        for (int i = 0; i < out.length; i++) {
            double whoIsOut = Double.isNaN(pool[i]) ? Math.max(0, force[i] - jobs[i]) : pool[i];
            out[i] = force[i] > 0 ? Math.max(0, whoIsOut / force[i]) : Double.NaN;
        }
        return out;
    }

    /**
     * THE definition of the average wage off a history: the wage bill over
     * the posts that are FILLED.
     *
     * Not over the workforce. On slot 3 that denominator carried 96,000
     * students and 1,360 prisoners who are paid nothing, and the line a
     * player reads against rent came out 5% low in a quiet decade and further
     * out once the universities filled.
     */
    public static double[] averageWage(HistorySave h) {
        double[] bill   = h.aligned("totalWage");
        double[] filled = filledPosts(h);
        double[] out = new double[bill.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = filled[i] > 0 ? bill[i] / filled[i] : Double.NaN;
        }
        return out;
    }

    /**
     * Output in FOUNDING money, a month at a time: nominal GDP over the price
     * index.
     *
     * Jerus, 2026-09-14: "for the graphs, i want real gdp, aka a graph that
     * shows inflation adjusted gdp." Nominal GDP rises when the city makes
     * more AND when the same things cost more, and those are opposite news.
     * Divided by the INDEX rather than deflated month by month, because the
     * index is already a ratio to the founding basket - so this comes out in
     * the same money every other founding-money figure is quoted in, and is
     * comparable across a currency reform for the same reason the index is.
     *
     * Struck here once (0.7.5) for the book's column, the Reports chart's
     * line and the recession test, which had been two copies of one division.
     */
    public static double[] realGdp(HistorySave h) {
        return real(h, "gdp");
    }

    /**
     * ...and a rolling YEAR of it, which is what the Reports page draws and
     * what a recession is read off.
     *
     * Jerus, 2026-09-14: "make real gdp a rolling figure, not the monthly
     * snapshot." A single month of this city's output is mostly noise - one
     * mine opening, one mill shedding a shift - and twelve months summed is
     * what an economy's output actually means. Summed rather than averaged,
     * so the number IS a year of output.
     *
     * NaN until there are twelve months behind it, deliberately: a "year"
     * drawn from seven months is not a year, and a line that starts low and
     * climbs for its first year would look like growth that never happened.
     * A NaN inside the window poisons that window and nothing else.
     */
    public static double[] realGdpYear(HistorySave h) {
        return rollingYear(realGdp(h));
    }

    /** Twelve months summed, ending at each month; NaN before the twelfth, and wherever the window holds a NaN. */
    private static double[] rollingYear(double[] monthly) {
        double[] out = new double[monthly.length];
        for (int i = 0; i < monthly.length; i++) {
            if (i + 1 < MONTHS_A_YEAR) { out[i] = Double.NaN; continue; }
            double sum = 0;
            boolean clean = true;
            for (int k = i - MONTHS_A_YEAR + 1; k <= i; k++) {
                if (Double.isNaN(monthly[k])) { clean = false; break; }
                sum += monthly[k];
            }
            out[i] = clean ? sum : Double.NaN;
        }
        return out;
    }

    /**
     * Any money series in FOUNDING money, a month at a time: divided by the
     * price index the way realGdp() is, so GDP's four parts and GDP itself
     * are the same money and the layers under the line add up to it. NaN
     * where either is not recorded, or the index is not positive.
     *
     * GDP IN LAYERS (0.7.6). Jerus: "breaking it down" - the Reports page's
     * real GDP drawn as what it is made of. The four parts are history
     * series of their own now (HistorySave, GDP in layers), and this and
     * realYear() put them in the same money as the line: deflated as
     * realGdp() is, summed as realGdpYear() is.
     */
    public static double[] real(HistorySave h, String key) {
        double[] nominal = h.aligned(key), index = h.aligned("priceIndex");
        double[] out = new double[nominal.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = index[i] > 0 ? nominal[i] / index[i] : Double.NaN;
        }
        return out;
    }

    /**
     * GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in
     * the order they stack. Their sum is "gdp" for the month, to the cent.
     */
    public static final String[] GDP_PARTS = { "consumption", "investment", "government", "netExports" };

    /**
     * ...and a rolling YEAR of one of them in founding money (0.7.6), summed
     * as realGdpYear() is - so a year of consumption, investment, government
     * and net exports adds up to the year of real GDP it is part of. NaN
     * until twelve months of it are behind the month, which on a save from
     * before the parts were kept is twelve months after it first played one.
     */
    public static double[] realYear(HistorySave h, String key) {
        return rollingYear(real(h, key));
    }

    /**
     * Inflation YEAR ON YEAR - the price index against its own reading twelve
     * months earlier, the window PriceIndex uses. A month of this game holds a
     * harvest and a shipping bill, and the month-on-month rate is mostly
     * those. The first twelve months have no reading rather than a made-up
     * zero. The book's column and the chart's line, struck once (0.7.5).
     */
    public static double[] inflation(HistorySave h) {
        double[] index = h.aligned("priceIndex");
        double[] out = new double[index.length];
        for (int i = 0; i < out.length; i++) {
            double then = i >= MONTHS_A_YEAR ? index[i - MONTHS_A_YEAR] : Double.NaN;
            out[i] = then > 0 ? index[i] / then - 1 : Double.NaN;
        }
        return out;
    }

    private static List<Column> columns(HistorySave h) {
        List<Column> out = new ArrayList<>();

        double[] arrivals  = h.aligned("arrivals");
        double[] leavers   = h.aligned("departures");
        double[] exports   = h.aligned("exportsAbroad");
        double[] imports   = h.aligned("importsAbroad");

        int n = h.months();

        out.add(new Column("unemployment", Kind.RATE, unemployment(h),
                "people out of work over the labour force (workforce less students and prisoners);"
                + " before the pool was recorded, the labour force less the posts offered"));

        out.add(new Column("averageWage", Kind.RATE, averageWage(h),
                "the wage bill over the posts that are FILLED, in thousands a month"));

        out.add(new Column("labourForce", Kind.LEVEL, labourForce(h),
                "people available to work - the workforce less the students and the prisoners"));

        out.add(new Column("realGdp", Kind.FLOW, realGdp(h),
                "output in FOUNDING money - nominal GDP divided by the price index"));

        out.add(new Column("inflation", Kind.RATE, inflation(h),
                "the price index against its own reading twelve months earlier"));

        double[] netMigration = new double[n];
        double[] tradeBalance = new double[n];
        for (int i = 0; i < n; i++) {
            netMigration[i] = arrivals[i] - leavers[i];
            tradeBalance[i] = exports[i] - imports[i];
        }
        out.add(new Column("netMigration", Kind.FLOW, netMigration, "arrivals less departures"));
        out.add(new Column("tradeBalance", Kind.FLOW, tradeBalance, "exports less imports"));

        for (String name : h.seriesByName().keySet()) {
            Kind kind = kindOf(name);
            out.add(new Column(name, kind == null ? Kind.LEVEL : kind, h.aligned(name),
                    kind == null ? "NO RULE DECLARED - folded as an end-of-row value, which is WRONG if it is a flow"
                                 : noteOf(name)));
        }
        return out;
    }

    /* ==================================================================
       THE FOLD
       ================================================================== */

    /** A row's value for a column, by that column's own rule. */
    private static double fold(Column c, int from, int to) {
        switch (c.kind) {
            case FLOW: {
                double sum = 0;
                for (int i = from; i <= to; i++) {
                    if (Double.isNaN(c.monthly[i])) return Double.NaN;
                    sum += c.monthly[i];
                }
                return sum;
            }
            case LEVEL:
                return c.monthly[to];
            default: {
                double sum = 0; int seen = 0;
                for (int i = from; i <= to; i++) {
                    if (Double.isNaN(c.monthly[i])) continue;
                    sum += c.monthly[i]; seen++;
                }
                return seen == 0 ? Double.NaN : sum / seen;
            }
        }
    }

    private static double worst(Column c, int from, int to, boolean high) {
        double best = Double.NaN;
        for (int i = from; i <= to; i++) {
            double v = c.monthly[i];
            if (Double.isNaN(v)) continue;
            if (Double.isNaN(best) || (high ? v > best : v < best)) best = v;
        }
        return best;
    }

    /* ==================================================================
       THE FILE
       ================================================================== */

    public static String years(HistorySave h)   { return write(h, MONTHS_A_YEAR); }
    public static String decades(HistorySave h) { return write(h, MONTHS_A_DECADE); }

    private static String write(HistorySave history, int span) {
        StringBuilder out = new StringBuilder(1 << 16);
        int months = history.months();
        String unit = span == MONTHS_A_YEAR ? "year" : "decade";
        String tag  = span == MONTHS_A_YEAR ? "yr" : "dec";

        if (months < 1) {
            return GameVersion.title() + " - " + unit + " book\nThe city has lived no months yet.\n";
        }

        List<Column> cols = columns(history);
        List<Integer> axis = history.getMonth();
        int first = axis.get(0), last = axis.get(months - 1);

        /* --------- the rows, as index ranges into the month axis --------- */
        List<int[]> rows = new ArrayList<>();   // {fromIndex, toIndex}
        int startIndex = 0;
        for (int i = 1; i <= months; i++) {
            boolean end = (i == months)
                    || bucket(axis.get(i), span) != bucket(axis.get(startIndex), span);
            if (end) { rows.add(new int[]{startIndex, i - 1}); startIndex = i; }
        }

        /* --------- fold everything, then drop what never moved --------- */
        Map<String, double[]> folded = new LinkedHashMap<>();
        List<Column> shown = new ArrayList<>();
        List<String> silent = new ArrayList<>();
        for (Column c : cols) {
            double[] values = new double[rows.size()];
            boolean anything = false;
            for (int r = 0; r < rows.size(); r++) {
                values[r] = fold(c, rows.get(r)[0], rows.get(r)[1]);
                if (!Double.isNaN(values[r]) && values[r] != 0) anything = true;
            }
            if (anything) { shown.add(c); folded.put(c.name, values); }
            else silent.add(c.name);
        }

        /* ------------------------ the preamble ------------------------ */
        out.append(GameVersion.title()).append(" - the ").append(unit).append(" book\n");
        out.append("Months ").append(first).append('-').append(last)
           .append(" (").append(CityCalendar.formatShort(first)).append(" to ")
           .append(CityCalendar.formatShort(last)).append("), ")
           .append(rows.size()).append(' ').append(unit).append(rows.size() == 1 ? "" : "s")
           .append(", ").append(shown.size()).append(" columns.\n");
        out.append("Written from the city's own history. Nothing here is recomputed from the city,")
           .append(" so this file and the graphs cannot disagree.\n\n");

        out.append("MONEY IS IN THOUSANDS throughout, as everywhere in this game: a gdp of 693 is $693,000.\n");
        out.append("RATES ARE FRACTIONS: 0.124 is 12.4%. The exceptions are crimeRate, which is crimes per 100,000 people a year,\n");
        out.append("and fxRate, which is Danzik dollars per US dollar - 1.000 at founding, HIGHER means the currency has fallen.\n\n");

        out.append("HOW A ROW WAS MADE\n");
        out.append("One row is one ").append(unit).append(". How its months became one number depends on what the number IS,\n");
        out.append("and every column below is marked with which:\n");
        out.append("  [+] a FLOW  - a figure for that month, so the row is the months ADDED (gdp, births, tax, write-offs)\n");
        out.append("  [=] a LEVEL - a stock, so the row is the value in its LAST month (population, cash, deposits, debt)\n");
        out.append("  [~] a RATE  - a rate, a price or an index, so the row is the months AVERAGED, and the WITHIN block\n");
        out.append("                further down carries that row's worst and best single month\n");
        out.append("A BLANK cell means the city was alive but was not recording that number yet - it is not a zero.\n");
        out.append("A [+] cell is blank if ANY month of the row is missing, because a sum of eight months is not a ")
           .append(unit).append(".\n");
        out.append("A [~] cell is the mean of the months that WERE recorded, because a mean of eight months is still a mean.\n");
        out.append("The n column is how many months the row actually covers; the last row of a run can be short.\n\n");

        /* ------------------------- the columns ------------------------- */
        out.append("COLUMNS\n");
        out.append("  ").append(pad(tag, 22)).append("    the ").append(unit)
           .append(", 1 is the founding ").append(unit).append('\n');
        out.append("  ").append(pad("mo", 22)).append("    the last month of the row\n");
        out.append("  ").append(pad("n", 22)).append("    months in the row\n");
        for (Column c : shown) {
            out.append("  ").append(pad(c.name, 22)).append(mark(c.kind)).append(' ')
               .append(c.note == null ? "" : c.note).append('\n');
        }
        if (!silent.isEmpty()) {
            out.append("\nNOT SHOWN - every row came out blank or zero, so the column is left out rather than padding the file:\n  ");
            out.append(String.join(", ", silent)).append('\n');
        }
        List<String> unruled = unruled(history);
        if (!unruled.isEmpty()) {
            out.append("\nNO AGGREGATION RULE - folded as a December value, which is WRONG if any of these is a monthly flow:\n  ");
            out.append(String.join(", ", unruled)).append('\n');
        }

        /* --------------------------- the table --------------------------- */
        out.append('\n').append(unit.toUpperCase(Locale.ROOT)).append("S (tab separated)\n");
        out.append(tag).append("\tmo\tn");
        for (Column c : shown) out.append('\t').append(c.name);
        out.append('\n');
        for (int r = 0; r < rows.size(); r++) {
            int from = rows.get(r)[0], to = rows.get(r)[1];
            out.append(bucket(axis.get(from), span) + 1).append('\t')
               .append(axis.get(to)).append('\t').append(to - from + 1);
            for (Column c : shown) out.append('\t').append(compact(folded.get(c.name)[r]));
            out.append('\n');
        }

        /* ------------------------- the extremes ------------------------- */
        List<Column> rates = new ArrayList<>();
        for (Column c : shown) if (c.kind == Kind.RATE) rates.add(c);
        if (!rates.isEmpty()) {
            out.append("\nWITHIN THE ").append(unit.toUpperCase(Locale.ROOT))
               .append(" - the worst and best SINGLE MONTH inside each row, for the [~] columns only (tab separated)\n");
            out.append(tag);
            for (Column c : rates) out.append('\t').append(c.name).append(".lo\t").append(c.name).append(".hi");
            out.append('\n');
            for (int r = 0; r < rows.size(); r++) {
                int from = rows.get(r)[0], to = rows.get(r)[1];
                out.append(bucket(axis.get(from), span) + 1);
                for (Column c : rates) {
                    out.append('\t').append(compact(worst(c, from, to, false)))
                       .append('\t').append(compact(worst(c, from, to, true)));
                }
                out.append('\n');
            }
        }

        out.append('\n').append(whatHappened(history, cols));
        return out.toString();
    }

    private static int bucket(int month, int span) { return (month - 1) / span; }

    private static String mark(Kind k) {
        return k == Kind.FLOW ? "[+]" : k == Kind.LEVEL ? "[=]" : "[~]";
    }

    /* ==================================================================
       WHAT HAPPENED

       Everything here is read off the same monthly series the table folded,
       so nothing in this section can contradict the rows above it. A year's
       average is exactly where an episode goes to hide, and this is the part
       an analyst reads first.
       ================================================================== */

    private static String whatHappened(HistorySave h, List<Column> cols) {
        StringBuilder out = new StringBuilder();
        Map<String, double[]> by = new LinkedHashMap<>();
        for (Column c : cols) by.put(c.name, c.monthly);
        List<Integer> axis = h.getMonth();

        out.append("WHAT HAPPENED - episodes a yearly average hides, read off the same monthly series\n");

        double[] pop = by.get("population");
        int peakAt = argBest(pop, true), troughAfter = -1;
        if (peakAt >= 0) {
            double deepest = 0;
            int fellFrom = -1, fellTo = -1;
            double running = Double.NaN; int runningAt = -1;
            for (int i = 0; i < pop.length; i++) {
                if (Double.isNaN(pop[i])) continue;
                if (Double.isNaN(running) || pop[i] > running) { running = pop[i]; runningAt = i; }
                double fall = running > 0 ? 1 - pop[i] / running : 0;
                if (fall > deepest) { deepest = fall; fellFrom = runningAt; fellTo = i; }
            }
            troughAfter = fellTo;
            out.append("  People       founded at ").append(compact(firstReal(pop)))
               .append(", peak ").append(compact(pop[peakAt])).append(" in ").append(at(axis, peakAt))
               .append(", ended at ").append(compact(lastReal(pop))).append('\n');
            if (deepest > 0.02 && fellFrom >= 0) {
                out.append("               deepest fall ").append(pct(deepest)).append(" - from ")
                   .append(compact(pop[fellFrom])).append(" in ").append(at(axis, fellFrom))
                   .append(" down to ").append(compact(pop[fellTo])).append(" by ").append(at(axis, fellTo)).append('\n');
            }
        }

        double[] px = by.get("priceIndex");
        if (hasAny(px)) {
            int hi = argBest(px, true), lo = argBest(px, false);
            out.append("  Prices       index high ").append(compact(px[hi])).append(" in ").append(at(axis, hi))
               .append(", low ").append(compact(px[lo])).append(" in ").append(at(axis, lo))
               .append(", ended ").append(compact(lastReal(px))).append('\n');
            double[] infl = by.get("inflation");
            double mean = meanOf(infl);
            if (!Double.isNaN(mean)) {
                out.append("               average inflation over the run ").append(pct(mean)).append(" a year\n");
            }
        }

        double[] fx = by.get("fxRate");
        if (hasAny(fx)) {
            int far = argFurthestFrom(fx, 1.0);
            out.append("  The currency furthest from parity ").append(compact(fx[far])).append(" in ")
               .append(at(axis, far)).append(", ended ").append(compact(lastReal(fx))).append('\n');
        }

        spell(out, "  The bank     ", by.get("bankEquity"), axis, v -> v < 0,
                "equity under water", true, false);
        spell(out, "               ", by.get("bankBranches"), axis, v -> v < 1,
                "no branch standing", false, false);
        double[] offs = by.get("bankWriteOffs");
        if (hasAny(offs)) {
            int worstOff = argBest(offs, true);
            out.append("               written off ").append(compact(sumOf(offs)))
               .append(" all told, worst month ").append(compact(offs[worstOff]))
               .append(" in ").append(at(axis, worstOff)).append('\n');
        }

        spell(out, "  Shortages    ", by.get("energyRatio"), axis, v -> v < 0.999, "power short", false, true);
        spell(out, "               ", by.get("waterRatio"),  axis, v -> v < 0.999, "water short", false, true);
        spell(out, "               ", by.get("roadRatio"),   axis, v -> v < 0.999, "roads congested", false, true);

        spell(out, "  Land         ", by.get("landUse"), axis, v -> v >= 0.99, "at or past 99% used", true, false);
        spell(out, "  Housing      ", by.get("unhoused"), axis, v -> v > 0, "somebody with no home", true, false);
        spell(out, "               ", by.get("unburied"), axis, v -> v > 0, "somebody unburied", true, false);
        spell(out, "  Work         ", by.get("unemployment"), axis, v -> v > 0.20, "unemployment past 20%", true, false);
        spell(out, "  Health       ", by.get("sickRate"), axis, v -> v > 0.10, "sick rate past 10%", true, false);
        spell(out, "  The treasury ", by.get("cash"), axis, v -> v < 0, "overdrawn", false, true);
        spell(out, "  Crime        ", by.get("caughtNotHeld"), axis, v -> v > 0, "caught and let go for want of a cell", true, false);

        // The named episodes - the same list the Reports page marks under its
        // chart, so the file and the chart agree about what to call a year.
        String label = "  Named        ";
        for (Episode e : episodes(h)) {
            out.append(label).append(e.name()).append(" - months ")
               .append(e.fromMonth()).append('-').append(e.toMonth()).append('\n');
            label = "               ";
        }

        return out.toString();
    }

    /** A condition, how many months met it, where they were, and its worst reading. */
    private interface Test { boolean holds(double v); }

    /** Index ranges {first, last} of every unbroken run of months where the test held. */
    private static List<int[]> runsOf(double[] series, Test test) {
        List<int[]> runs = new ArrayList<>();
        int startIndex = -1;
        for (int i = 0; i < series.length; i++) {
            boolean in = !Double.isNaN(series[i]) && test.holds(series[i]);
            if (in) {
                if (startIndex < 0) startIndex = i;
            } else if (startIndex >= 0) {
                runs.add(new int[]{startIndex, i - 1}); startIndex = -1;
            }
        }
        if (startIndex >= 0) runs.add(new int[]{startIndex, series.length - 1});
        return runs;
    }

    private static void spell(StringBuilder out, String label, double[] series, List<Integer> axis,
                              Test test, String what, boolean worstIsHigh, boolean worstIsLow) {
        if (series == null || !hasAny(series)) return;
        List<int[]> runs = runsOf(series, test);
        int count = 0;
        double extreme = Double.NaN; int extremeAt = -1;
        for (int[] r : runs) {
            for (int i = r[0]; i <= r[1]; i++) {
                count++;
                boolean better = Double.isNaN(extreme)
                        || (worstIsLow ? series[i] < extreme : series[i] > extreme);
                if (better) { extreme = series[i]; extremeAt = i; }
            }
        }
        if (count == 0) return;

        out.append(label).append(what).append(" in ").append(count)
           .append(count == 1 ? " month" : " months");
        if (worstIsHigh || worstIsLow) {
            out.append(", worst ").append(compact(extreme)).append(" in ").append(at(axis, extremeAt));
        }
        out.append(": ").append(spans(runs, axis)).append('\n');
    }

    /** Consecutive months collapsed into ranges, and a long list cut off honestly. */
    private static String spans(List<int[]> runs, List<Integer> axis) {
        StringBuilder s = new StringBuilder();
        int shown = 0;
        for (int[] r : runs) {
            if (shown == 8) { s.append(", and ").append(runs.size() - 8).append(" more"); break; }
            if (shown > 0) s.append(", ");
            int a = axis.get(r[0]), b = axis.get(r[1]);
            s.append(a == b ? String.valueOf(a) : (a + "-" + b));
            shown++;
        }
        return s.toString();
    }

    /* ==================================================================
       THE NAMED EPISODES (0.7.5)

       Jerus, 2026-09-23, on the Reports page: "event marks, if you think
       that's doable and accurate go for it, perhaps a little name generator
       if that isn't too hard, for example, if banks failed then 'financial
       crisis of 2045', or whatever you think is appropriate".

       ACCURATE decides where this lives. An episode is a run of months in
       which a condition held, read off the saved series - nothing else the
       game keeps can say when a crisis began and ended, and the notices of
       2045 were never kept. So it is a pure function of the history, here
       beside the spells the book already lists, and the Reports page's marks
       and the book's "Named" lines are one list rather than two that could
       disagree.

       THE RUNS ARE DROPPED BEFORE THEY ARE JOINED. A run under
       EPISODE_MIN_MONTHS is noise and goes first; what survives is joined
       across any relief shorter than EPISODE_JOIN_MONTHS. The other order
       would let two one-month blips five months apart become an episode that
       starts at a month the chart does not shade, and the point of the marks
       is that each one sits at the start of something visible.
       ================================================================== */

    /** A run shorter than this many months is noise, and is not named - or shaded on the chart. */
    public static final int EPISODE_MIN_MONTHS = 3;

    /** Two runs with fewer months of relief than this between them are one episode. */
    public static final int EPISODE_JOIN_MONTHS = 6;

    /** A recession this many months long, or longer, is called a depression. */
    public static final int DEPRESSION_MONTHS = 24;

    /**
     * One named stretch of the city's life.
     *
     * @param kind      which condition held: financial, recession, depression,
     *                  currency, inflation, deflation, epidemic, treasury, slump
     * @param name      what a reader calls it - "Financial crisis of 2045"
     * @param fromMonth the first month it held, on the history's own axis
     * @param toMonth   the last month of the episode, relief inside it included
     * @param worst     the tested series at its worst inside the episode, in
     *                  that series' own terms (bank equity in thousands, a
     *                  year-on-year fall as a fraction, an exchange rate's
     *                  multiple of a year before, a rate as a fraction)
     */
    public record Episode(String kind, String name, int fromMonth, int toMonth, double worst) { }

    /**
     * Every named episode in a history, oldest first.
     *
     * THE TABLE IS THE GENERATOR. Jerus asked for the names; each row is a
     * condition, the series it is read off, which way is worse, and what a
     * reader would call a run of it, with the year the run began. Two of a
     * name are told apart by ", again" rather than by a number nobody reads.
     */
    public static List<Episode> episodes(HistorySave h) {
        List<Episode> found = new ArrayList<>();
        if (h.months() < 1) return found;
        List<Integer> axis = h.getMonth();
        double[] inflation = inflation(h);

        //    kind          the series                       the condition      worst is  the name
        named(found, axis, "financial", h.aligned("bankEquity"), v -> v < 0,     false, "Financial crisis of %d");
        named(found, axis, "recession", realGrowth(h),           v -> v < 0,     false, "Recession of %d");
        named(found, axis, "currency",  currencyMove(h),         v -> v > 2,     true,  "Currency crisis of %d");
        named(found, axis, "inflation", inflation,               v -> v > .25,   true,  "The %d inflation");
        named(found, axis, "deflation", inflation,               v -> v < -.10,  false, "The %d deflation");
        named(found, axis, "epidemic",  h.aligned("sickRate"),   v -> v > .10,   true,  "Epidemic of %d");
        named(found, axis, "treasury",  h.aligned("cash"),       v -> v < 0,     false, "Treasury crisis of %d");
        named(found, axis, "slump",     unemployment(h),         v -> v > .20,   true,  "The %d slump");

        // A recession that ran two years is a depression, and is called one.
        for (int i = 0; i < found.size(); i++) {
            Episode e = found.get(i);
            if (e.kind().equals("recession") && e.toMonth() - e.fromMonth() + 1 >= DEPRESSION_MONTHS) {
                found.set(i, new Episode("depression",
                        String.format(Locale.ROOT, "Depression of %d", CityCalendar.yearOf(e.fromMonth())),
                        e.fromMonth(), e.toMonth(), e.worst()));
            }
        }

        // Oldest first - a stable sort, so two that began the same month keep the table's order.
        found.sort((a, b) -> Integer.compare(a.fromMonth(), b.fromMonth()));

        // The second of a name in one year is "..., again".
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int i = 0; i < found.size(); i++) {
            Episode e = found.get(i);
            if (!seen.add(e.name())) {
                found.set(i, new Episode(e.kind(), e.name() + ", again",
                        e.fromMonth(), e.toMonth(), e.worst()));
            }
        }
        return found;
    }

    /**
     * The months to shade on a chart as recession, as {firstMonth, lastMonth}
     * on the history's axis: every run where the rolling year of real output
     * was below the year before it, of at least EPISODE_MIN_MONTHS - NOT
     * joined, because a band is the months themselves and the relief between
     * two is worth seeing. Each recession episode starts where one of these
     * does.
     */
    public static List<int[]> recessions(HistorySave h) {
        List<int[]> out = new ArrayList<>();
        List<Integer> axis = h.getMonth();
        for (int[] r : runsOf(realGrowth(h), v -> v < 0)) {
            if (r[1] - r[0] + 1 >= EPISODE_MIN_MONTHS) out.add(new int[]{axis.get(r[0]), axis.get(r[1])});
        }
        return out;
    }

    /** The rolling year of real output against the year before it, as a fraction. */
    private static double[] realGrowth(HistorySave h) {
        double[] year = realGdpYear(h);
        double[] out = new double[year.length];
        for (int i = 0; i < out.length; i++) {
            double then = i >= MONTHS_A_YEAR ? year[i - MONTHS_A_YEAR] : Double.NaN;
            out[i] = then > 0 ? year[i] / then - 1 : Double.NaN;
        }
        return out;
    }

    /** The exchange rate as a multiple of itself a year before - above 2 is a currency that halved. */
    private static double[] currencyMove(HistorySave h) {
        double[] fx = h.aligned("fxRate");
        double[] out = new double[fx.length];
        for (int i = 0; i < out.length; i++) {
            double then = i >= MONTHS_A_YEAR ? fx[i - MONTHS_A_YEAR] : Double.NaN;
            out[i] = then > 0 ? fx[i] / then : Double.NaN;
        }
        return out;
    }

    /** One row of the table: the runs of a condition, dropped, joined and named. */
    private static void named(List<Episode> found, List<Integer> axis, String kind, double[] series,
                              Test test, boolean worstIsHigh, String name) {
        List<int[]> joined = new ArrayList<>();
        for (int[] r : runsOf(series, test)) {
            if (r[1] - r[0] + 1 < EPISODE_MIN_MONTHS) continue;
            int[] last = joined.isEmpty() ? null : joined.get(joined.size() - 1);
            if (last != null && r[0] - last[1] - 1 < EPISODE_JOIN_MONTHS) last[1] = r[1];
            else joined.add(new int[]{r[0], r[1]});
        }
        for (int[] r : joined) {
            double worst = Double.NaN;
            for (int i = r[0]; i <= r[1]; i++) {
                double v = series[i];
                if (Double.isNaN(v) || !test.holds(v)) continue;
                if (Double.isNaN(worst) || (worstIsHigh ? v > worst : v < worst)) worst = v;
            }
            int from = axis.get(r[0]);
            found.add(new Episode(kind, String.format(Locale.ROOT, name, CityCalendar.yearOf(from)),
                    from, axis.get(r[1]), worst));
        }
    }

    /* ---------------------------- small helpers ---------------------------- */

    private static String at(List<Integer> axis, int i) {
        return "month " + axis.get(i) + " (" + CityCalendar.formatShort(axis.get(i)) + ")";
    }

    private static boolean hasAny(double[] v) {
        if (v == null) return false;
        for (double d : v) if (!Double.isNaN(d)) return true;
        return false;
    }

    private static double firstReal(double[] v) {
        for (double d : v) if (!Double.isNaN(d)) return d;
        return Double.NaN;
    }

    private static double lastReal(double[] v) {
        for (int i = v.length - 1; i >= 0; i--) if (!Double.isNaN(v[i])) return v[i];
        return Double.NaN;
    }

    private static double sumOf(double[] v) {
        double sum = 0; boolean any = false;
        for (double d : v) if (!Double.isNaN(d)) { sum += d; any = true; }
        return any ? sum : Double.NaN;
    }

    private static double meanOf(double[] v) {
        double sum = 0; int n = 0;
        for (double d : v) if (!Double.isNaN(d)) { sum += d; n++; }
        return n == 0 ? Double.NaN : sum / n;
    }

    private static int argBest(double[] v, boolean high) {
        int best = -1;
        for (int i = 0; i < v.length; i++) {
            if (Double.isNaN(v[i])) continue;
            if (best < 0 || (high ? v[i] > v[best] : v[i] < v[best])) best = i;
        }
        return best;
    }

    private static int argFurthestFrom(double[] v, double anchor) {
        int best = -1;
        for (int i = 0; i < v.length; i++) {
            if (Double.isNaN(v[i])) continue;
            if (best < 0 || Math.abs(v[i] - anchor) > Math.abs(v[best] - anchor)) best = i;
        }
        return best;
    }

    private static String pad(String s, int width) {
        return s.length() >= width ? s : s + " ".repeat(width - s.length());
    }

    private static String pct(double fraction) {
        return String.format(Locale.ROOT, "%.2f%%", fraction * 100);
    }

    /**
     * Three significant figures, and never a thousands separator.
     *
     * Wide numbers are the whole cost of this file - ninety columns times three
     * hundred rows - and a run holds figures from 0.0001 to 1e14 in the same
     * table. Locale.ROOT throughout, or a machine set to French writes decimal
     * commas into a tab-separated file and every column after the first is a
     * lie.
     */
    static String compact(double v) {
        if (Double.isNaN(v)) return "";
        if (v == 0) return "0";
        if (Double.isInfinite(v)) return v > 0 ? "inf" : "-inf";
        double a = Math.abs(v);
        if (a >= 1e7 || a < 1e-4) {
            int exp = (int) Math.floor(Math.log10(a));
            double mantissa = v / Math.pow(10, exp);
            if (Math.abs(mantissa) >= 9.995) { mantissa /= 10; exp++; }
            return trim(String.format(Locale.ROOT, "%.2f", mantissa)) + "e" + exp;
        }
        if (a >= 1000) return String.format(Locale.ROOT, "%.0f", v);
        if (a >= 100)  return trim(String.format(Locale.ROOT, "%.1f", v));
        if (a >= 10)   return trim(String.format(Locale.ROOT, "%.2f", v));
        if (a >= 1)    return trim(String.format(Locale.ROOT, "%.3f", v));
        return trim(String.format(Locale.ROOT, "%.4f", v));
    }

    private static String trim(String s) {
        if (s.indexOf('.') < 0) return s;
        int end = s.length();
        while (end > 0 && s.charAt(end - 1) == '0') end--;
        if (end > 0 && s.charAt(end - 1) == '.') end--;
        return s.substring(0, end);
    }
}

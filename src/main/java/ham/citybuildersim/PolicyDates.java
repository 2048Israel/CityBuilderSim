package ham.citybuildersim;

/**
 * When Canada first had each of the Policy tab's levers, as history: the game month Canada first had each one in a city founded in January
 * 1900, the study's entry for it, and what Canada first did and on what terms (R0, research-1900.md 7); and the dollar's gold years, the one
 * dated regime the game still plays (R0f).
 *
 * WHY. R0 (0.9.10x, 2026-11-04) opened each lever on its date, the inbox saying when and on what terms, history as the tutorial; R3 let a
 * law discovery bring one forward by up to four years. Jerus, 2026-11-19 (OP1, which replaced PP1's political points): "everything policy
 * related is available right away, and later ill think of an actual tutorial". So since OP1 nothing in the game opens on these dates: every
 * lever is open from a city's founding, in every city, each as a lever opened under R0 (opensAtFoundingRate(), call 16: the three income
 * taxes at the city's founding rate, every promise at zero; TaxPolicy.openAtFounding()), and only the player moves them. The table stays as
 * the study's record: research.json's law rows carry its dates (ResearchCheck holds the two equal), and a tutorial may read it. The gold
 * years stay history's (OP1 call 2): a city founded the 1900 way is founded on gold at par, which August 1914 suspends, July 1926 restores
 * and September 1931 ends (goldPeriod(), ForeignAccounts.hearMoneyMonth()), unless the player has taken the dollar off it first.
 *
 * Pure: no state, no instances, no randomness. A lever the study dates by the year alone is Canada's in January of that year, because a
 * month would be a claim the study does not make; one it dates to a month is Canada's in that month.
 */
public final class PolicyDates {

    private PolicyDates() { }

    /* =====================================================================
       THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1)

       The table is the study's, row for row, less the rows the game has no lever for: the 1915 workers' compensation levy (PL1), care
       subsidies (PL11: care has one price lever, the clinic fee scale, and no subsidy of its own) and deposit insurance (L1, 1967: a bank's
       resolution is modelled, a deposit-insurance dial is not). The game's three income taxes are the study's profit tax (PL2, 1916),
       income tax (PL3, 1917: the game's wage tax, banded, is the personal income tax) and sales tax (PL5, 1920). The levers the table does
       not date were a city's own before 1900, each with the reason in its terms, so a later source moves one line. Declared in the order
       Canada had them, the dated ones first. Since OP1 every one of them is open from founding.
       ===================================================================== */

    /**
     * The Policy levers, the ones history dates in the order Canada had them, then the ones a city set before 1900. A dated lever has the
     * study's entry key ("PL3", "L5": the law discovery of the same date in research.json) and the act or body that first had it; an undated
     * one has no key and its terms are the reason a city had it in 1900. Every one is open from founding (OP1).
     */
    public enum Lever {

        /** Money, August 1914: the Finance Act let the government lend to the banks. A date, not a discovery (7.2), so the table gives it no key. */
        POLICY_RATE("policy rate", 1914, 8, "", "the Finance Act's Advance Rate",
                "the rate at which the government lent to the banks"),
        /** Taxes, 1916: the first of the game's three income taxes. */
        PROFIT_TAX("profit tax", 1916, "PL2", "the Business Profits War Tax", "25% of profits above 7% of capital (companies)"),
        /** Taxes, 1917: the game's wage tax, banded, is the personal income tax, so the Income War Tax Act is its date. */
        WAGE_TAX("income tax", 1917, "PL3", "the Income War Tax Act", "4% above C$1,500 (single) or C$3,000; a supertax of 2% to 25%"),
        /** Wages, 1918: Manitoba and British Columbia, for women (1917 by one source); men mostly in the 1930s (unverified). */
        MINIMUM_WAGE("minimum wage", 1918, "PL4", "Manitoba and British Columbia, for women", "weekly rates by trade and city"),
        /** Taxes, 1920. */
        SALES_TAX("sales tax", 1920, "PL5", "", "1% on manufacturers' and wholesalers' sales; 6% by 1923"),
        /** Zoning, 1924 (unverified; New York had it in 1916): until then everything is free, the decided default. */
        ZONING("zoning", 1924, "PL6", "Kitchener, unverified", "use districts"),
        /** Promises, 1927: the pension's replacement, the promise; its contribution is PENSION_CONTRIBUTION's. */
        PENSION_REPLACEMENT("old-age pension", 1927, "PL7", "the Old Age Pensions Act",
                "up to C$20 a month at 70; income under C$365; Ottawa paid half"),
        /** Money, 11 March 1935: the Bank of Canada opens the rest of Money (the policy rate having opened in 1914). */
        CENTRAL_BANK("central bank", 1935, 3, "", "the Bank of Canada", "Bank Rate 2.5%"),
        /** Taxes and Promises, 1939: the grant and the loan rate together. */
        STUDENT_AID("student aid", 1939, "PL9", "Dominion-Provincial Student Aid", "terms not found"),
        /** Taxes and Promises, 7 August 1940: the premium and the benefit together; contributions began on 1 July 1941. */
        EI("unemployment insurance (EI)", 1940, 8, "PL10", "the Unemployment Insurance Act",
                "9 to 36 cents a week by wage class, plus 20% from the government; benefit 34 to 40 times the average contribution;"
                        + " about 42% of workers covered; contributions from 1 July 1941"),
        /** Taxes, 1 January 1947: Saskatchewan's public hospital insurance (national cost-sharing followed in 1958). */
        HEALTH_PREMIUM("health premium", 1947, 1, "PL12", "Saskatchewan's public hospital insurance", "a premium of C$5 a person (unverified)"),
        /** Taxes, 1 January 1966: the contribution, the half of the pension the worker and the employer pay. */
        PENSION_CONTRIBUTION("pension contribution", 1966, 1, "L5", "the Canada Pension Plan", "1.8% from the worker, 1.8% from the employer"),

        /** Open from founding: a council's mill rate, municipal before 1900. */
        PROPERTY_TAX("property tax",
                "a council mill rate on land and buildings, a city's own before 1900; 79% of municipal revenue in 1933 (est.)"),
        /** Open from founding: relief on the farmland the property tax assesses. */
        FARMLAND_RELIEF("farmland relief", "part of the property tax's roll, so it opens with the property tax"),
        /** Open from founding: poor relief predates 1900. */
        FOOD_ASSISTANCE("food assistance", "poor relief predates 1900"),
        /** Open from founding: a fare is a service's own price, and a city set its services. */
        TRANSIT_FARE("transit fare", "a fare is a service's own price, and in 1900 a city set its services"),
        /** Open from founding: schools were the school boards', on a school rate. */
        TUITION_SCALES("tuition scales", "schools were run by school boards on a school rate: compulsory and free in Ontario from 1871"),
        /** Open from founding: the fee scale on the three care types. */
        HEALTH_FEE_SCALE("clinic fees", "doctors' fees were the doctors' own price, and in 1900 a city set its services"),
        /** Open from founding: no source dates a fuel duty. */
        FUEL_DUTY("fuel duty", "no source dates it, and a duty with no cars taxes nothing"),
        /** Open from founding: harbour dues. */
        PORT_DUES("port dues", "harbour dues were a port's own price"),
        /** Open from founding: municipal "bonuses" to attract factories. */
        SECTOR_SUBSIDIES("sector subsidies", "municipal bonuses to attract factories (extent unverified)");

        private final String label;
        private final int year;
        private final int monthOfYear;
        private final boolean monthKnown;
        private final boolean open;
        private final String entry;
        private final String act;
        private final String terms;

        /** A lever the study dates to a month. */
        Lever(String label, int year, int monthOfYear, String entry, String act, String terms) {
            this(label, year, monthOfYear, true, false, entry, act, terms);
        }

        /** A lever the study dates by the year alone: Canada's in January. */
        Lever(String label, int year, String entry, String act, String terms) {
            this(label, year, 1, false, false, entry, act, terms);
        }

        /** A lever a city had before 1900, with the reason. */
        Lever(String label, String reason) {
            this(label, CityCalendar.EPOCH_YEAR, 1, false, true, "", "", reason);
        }

        private Lever(String label, int year, int monthOfYear, boolean monthKnown, boolean open, String entry, String act, String terms) {
            this.label = label;
            this.year = year;
            this.monthOfYear = monthOfYear;
            this.monthKnown = monthKnown;
            this.open = open;
            this.entry = entry;
            this.act = act;
            this.terms = terms;
        }

        /** The lever as a player reads it: "income tax". */
        public String label()       { return label; }
        /** The calendar year Canada first had it in. */
        public int year()           { return year; }
        /** 1-12; 1 where the study dates it by the year alone. */
        public int monthOfYear()    { return monthOfYear; }
        /** Whether the study dates it to a month (else January of its year). */
        public boolean monthKnown() { return monthKnown; }
        /** The study's entry key ("PL3", "L5"), or "" for an undated lever and for the policy rate. */
        public String entry()       { return entry; }
        /** The law or body that first had it ("the Income War Tax Act"), or "". */
        public String act()         { return act; }
        /** Its first terms in a phrase with no full stop; for an undated lever, the reason a city had it in 1900. */
        public String terms()       { return terms; }
        /** The sources the terms rest on: SOURCES for a dated lever, OPEN_SOURCES for an undated one. */
        public String sources()     { return open ? OPEN_SOURCES : SOURCES; }
    }

    /** The sources the table's rows rest on, as the study lists them under it: P4 section 3.1 and the keys in its brackets. */
    public static final String SOURCES = "[P4] 3.1: [BURNS], [CYB25], [H1], [H113], [LOP], [SC-UI], [ILO63], [BOC-W], [BOC-BR], [AWCBC],"
            + " [TCE-MW], [OREO], [CPP-W], [CDIC-W], [HIDS-W], [ZON-W]";

    /** The source for the levers no table row dates: the study's rule for 1900, that a city set its property tax, schools, services and bylaws. */
    public static final String OPEN_SOURCES = "research-1900.md 7.1 and [P4] 3.2: in 1900 a Canadian city set only its property tax, its schools,"
            + " its services and its building bylaws";

    /* ----- the dates as game months ----- */

    /** The game month of a real calendar month: the epoch's January is month 1, and a date before the epoch is month 1. */
    static int monthOf(int year, int monthOfYear) {
        return Math.max(1, 1 + 12 * (year - CityCalendar.EPOCH_YEAR) + (monthOfYear - 1));
    }

    /** The game month Canada first had a lever in; 1 for a lever a city had before 1900. History only: nothing opens on it (OP1). */
    public static int canadaMonth(Lever lever) {
        return monthOf(lever.year(), lever.monthOfYear());
    }

    /** Canada's suspension of gold, August 1914, when the Finance Act let the government lend to the banks (research-1900.md 7.2). */
    public static final int GOLD_SUSPENDED = monthOf(1914, 8);

    /** Canada's return to gold, July 1926 (research-1900.md 7.2). */
    public static final int GOLD_RETURN = monthOf(1926, 7);

    /** Canada's final departure, September 1931, following Britain; the study's 1929-31 interval ends here. */
    public static final int GOLD_END = monthOf(1931, 9);

    /** The dated gold episode (1 from the epoch to GOLD_SUSPENDED, 2 from GOLD_RETURN to GOLD_END), or 0 between and after. */
    public static int goldPeriod(int month) {
        if (month < GOLD_SUSPENDED) return 1;
        return month >= GOLD_RETURN && month < GOLD_END ? 2 : 0;
    }

    /**
     * Whether a lever opens at the city's founding rate (TaxPolicy.DEFAULT_INCOME_TAX) and not at zero: the three income taxes, which fund the
     * city, where a promise (a pension and its contribution, EI, the health premium, student aid) costs it and opens at zero. WHY (R0 call 16):
     * opening at zero left the playtest's untended city with no profit, wage or sales tax until about 1950, its treasury below zero from 1921
     * to 1967. Since OP1 the founding opens every lever this way (TaxPolicy.openAtFounding()).
     */
    public static boolean opensAtFoundingRate(Lever lever) {
        return lever == Lever.PROFIT_TAX || lever == Lever.WAGE_TAX || lever == Lever.SALES_TAX;
    }
}

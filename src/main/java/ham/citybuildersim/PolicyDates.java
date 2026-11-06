package ham.citybuildersim;

/**
 * When Canada first had each of the Policy tab's levers, as pure data: the game month each one opens in a city founded in January 1900, the
 * study's entry for it, what Canada first did and on what terms, and how far a law discovery may bring it forward (R0, research-1900.md 7).
 *
 * WHY. Until R0 every lever was open from month 1, so a city founded in January 1900 could tax incomes 17 years before Canada did, set a
 * minimum wage 18 years early and promise old-age pensions 27 years before the country had any: the Policy screen was open in full on the
 * first day, and there was nothing in it to learn. The game's institutions are Canada's (a law, unlike a technology, does not cross borders
 * under licence), so each lever now opens on the date Canada first had it, the inbox says when and on what terms, and the player decides:
 * history is the tutorial. The three income taxes open at the city's founding rate and the promises (the pensions, EI, the health premium,
 * student aid) at zero (opensAtFoundingRate(), call 16). This class is only the dates. Nothing reads it yet (R0's first slice): the gate that holds a lever shut
 * is the second slice's (TaxPolicy's effective rates and setters, the minimum wage, zoning, the save and the inbox) and the Policy screen's
 * drawing of a shut one the third's. Money's two dates are here as data; the gate on them, and the gold years between, are batch R0f's.
 *
 * Pure: no state, no instances, no randomness. A lever the study dates by the year alone opens in January of that year, because a month would
 * be a claim the study does not make; one it dates to a month opens in that month.
 */
public final class PolicyDates {

    private PolicyDates() { }

    /* =====================================================================
       THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1)

       The table is the study's, row for row, less the rows the game has no lever for: the 1915 workers' compensation levy (PL1), care
       subsidies (PL11: care has one price lever, the clinic fee scale, and no subsidy of its own) and deposit insurance (L1, 1967: a bank's
       resolution is modelled, a deposit-insurance dial is not). The game's three income taxes are the study's profit tax (PL2, 1916),
       income tax (PL3, 1917: the game's wage tax, banded, is the personal income tax) and sales tax (PL5, 1920). The levers the table does
       not date are open from founding, each with the reason in its terms, so a later source moves one line. Declared in the order they
       open, the dated ones first.
       ===================================================================== */

    /**
     * The Policy levers that history dates, in the order they open, then the ones open from founding. A dated lever has the study's entry key
     * ("PL3", "L5": the law discovery that can bring it forward) and the act or body that first had it; an open one has no key and its terms
     * are the reason it is open.
     */
    public enum Lever {

        /** Money, August 1914: the Finance Act let the government lend to the banks. A date, not a discovery (7.2), so the table gives it no key. */
        POLICY_RATE("policy rate", 1914, 8, "", "the Finance Act's Advance Rate",
                "the rate at which the government lent to the banks"),
        /** Taxes, 1916: the first of the game's three income taxes. */
        PROFIT_TAX("profit tax", 1916, "PL2", "the Business Profits War Tax", "25% of profits above 7% of capital (companies)"),
        /** Taxes, 1917: the game's wage tax, banded, is the personal income tax, so the Income War Tax Act is its date. */
        WAGE_TAX("income tax", 1917, "PL3", "the Income War Tax Act", "4% above $1,500 (single) or $3,000; a supertax of 2% to 25%"),
        /** Wages, 1918: Manitoba and British Columbia, for women (1917 by one source); men mostly in the 1930s (unverified). */
        MINIMUM_WAGE("minimum wage", 1918, "PL4", "Manitoba and British Columbia, for women", "weekly rates by trade and city"),
        /** Taxes, 1920. */
        SALES_TAX("sales tax", 1920, "PL5", "", "1% on manufacturers' and wholesalers' sales; 6% by 1923"),
        /** Zoning, 1924 (unverified; New York had it in 1916): until then everything is free, the decided default. */
        ZONING("zoning", 1924, "PL6", "Kitchener, unverified", "use districts"),
        /** Promises, 1927: the pension's replacement, the promise; its contribution is PENSION_CONTRIBUTION's. */
        PENSION_REPLACEMENT("old-age pension", 1927, "PL7", "the Old Age Pensions Act",
                "up to $20 a month at 70; income under $365; Ottawa paid half"),
        /** Money, 11 March 1935: the Bank of Canada opens the rest of Money (the policy rate having opened in 1914). */
        CENTRAL_BANK("central bank", 1935, 3, "PL8", "the Bank of Canada", "Bank Rate 2.5%"),
        /** Taxes and Promises, 1939: the grant and the loan rate together. */
        STUDENT_AID("student aid", 1939, "PL9", "Dominion-Provincial Student Aid", "terms not found"),
        /** Taxes and Promises, 7 August 1940: the premium and the benefit together; contributions began on 1 July 1941. */
        EI("unemployment insurance (EI)", 1940, 8, "PL10", "the Unemployment Insurance Act",
                "9 to 36 cents a week by wage class, plus 20% from the government; benefit 34 to 40 times the average contribution;"
                        + " about 42% of workers covered; contributions from 1 July 1941"),
        /** Taxes, 1 January 1947: Saskatchewan's public hospital insurance (national cost-sharing followed in 1958). */
        HEALTH_PREMIUM("health premium", 1947, 1, "PL12", "Saskatchewan's public hospital insurance", "a premium of $5 a person (unverified)"),
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

        /** A lever the study dates by the year alone: it opens in January. */
        Lever(String label, int year, String entry, String act, String terms) {
            this(label, year, 1, false, false, entry, act, terms);
        }

        /** A lever open from founding, with the reason. */
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
        /** The calendar year it opens in. */
        public int year()           { return year; }
        /** 1-12; 1 where the study dates it by the year alone. */
        public int monthOfYear()    { return monthOfYear; }
        /** Whether the study dates it to a month (else it opens in January of its year). */
        public boolean monthKnown() { return monthKnown; }
        /** The study's entry key ("PL3", "L5"), or "" for an open lever and for the policy rate. */
        public String entry()       { return entry; }
        /** The law or body that first had it ("the Income War Tax Act"), or "". */
        public String act()         { return act; }
        /** Its first terms in a phrase with no full stop; for an open lever, the reason it is open. */
        public String terms()       { return terms; }
        /** The sources the terms rest on: SOURCES for a dated lever, OPEN_SOURCES for an open one. */
        public String sources()     { return open ? OPEN_SOURCES : SOURCES; }
    }

    /**
     * The most months a law discovery may bring a lever forward: four years, the law field's reach (research-1900.md 7.2). The saved opening
     * month is what a find moves; the finds themselves are batch R3's, and nothing opens early without one.
     */
    public static final int MAX_EARLY_MONTHS = 48;

    /** The sources the table's rows rest on, as the study lists them under it: P4 section 3.1 and the keys in its brackets. */
    public static final String SOURCES = "[P4] 3.1: [BURNS], [CYB25], [H1], [H113], [LOP], [SC-UI], [ILO63], [BOC-W], [BOC-BR], [AWCBC],"
            + " [TCE-MW], [OREO], [CPP-W], [CDIC-W], [HIDS-W], [ZON-W]";

    /** The source for the levers no table row dates: the study's rule for 1900, that a city set its property tax, schools, services and bylaws. */
    public static final String OPEN_SOURCES = "research-1900.md 7.1 and [P4] 3.2: in 1900 a Canadian city set only its property tax, its schools,"
            + " its services and its building bylaws";

    /* ----- the dates as game months ----- */

    /** The game month of a real calendar month: the epoch's January is month 1, and a date before the epoch is month 1 (open at founding). */
    static int monthOf(int year, int monthOfYear) {
        return Math.max(1, 1 + 12 * (year - CityCalendar.EPOCH_YEAR) + (monthOfYear - 1));
    }

    /** The game month a lever opens in on Canada's date; 1 for a lever open from founding. */
    public static int openingMonth(Lever lever) {
        return monthOf(lever.year(), lever.monthOfYear());
    }

    /** Whether a lever is open in a game month: from its opening month on. */
    public static boolean isOpen(Lever lever, int gameMonth) {
        return gameMonth >= openingMonth(lever);
    }

    /** Whether a lever is open in the founding month, the first month of the game. */
    public static boolean openAtFounding(Lever lever) {
        return openingMonth(lever) <= 1;
    }

    /**
     * Whether a lever opens at the city's founding rate (TaxPolicy.DEFAULT_INCOME_TAX) and not at zero: the three income taxes, which fund the
     * city, where a promise (a pension, EI, the health premium, student aid) costs it and opens at zero. WHY (R0 call 16): opening at zero left
     * the playtest's untended city with no profit, wage or sales tax until about 1950, its treasury below zero from 1921 to 1967.
     */
    public static boolean opensAtFoundingRate(Lever lever) {
        return lever == Lever.PROFIT_TAX || lever == Lever.WAGE_TAX || lever == Lever.SALES_TAX;
    }

    /** The earliest month a law discovery may bring a lever to: MAX_EARLY_MONTHS before its opening month, never before month 1. */
    public static int earliestOpening(Lever lever) {
        return earliestFor(openingMonth(lever));
    }

    /** MAX_EARLY_MONTHS before an opening month, floored at month 1 (separate from the lever so the floor can be held on fixtures). */
    static int earliestFor(int openingMonth) {
        return Math.max(1, openingMonth - MAX_EARLY_MONTHS);
    }

    /**
     * The lever a discovery's code brings forward ("PL3" -> the income tax, "L5" -> the pension contribution), or null for a code that is no
     * lever's key (a blank, a code the table does not date, and every lever that has no key: the policy rate and the open ones). Game asks it
     * of a find (R3), so that the lever's own entry decides what a find moves and nothing else does.
     */
    public static Lever leverOf(String code) {
        if (code == null || code.isBlank()) return null;
        for (Lever lever : Lever.values()) if (!lever.entry().isEmpty() && lever.entry().equals(code.trim())) return lever;
        return null;
    }

    /* ----- what the player is told ----- */

    /** The date a lever opens as the Policy screen prints it: "1917", "August 1940", or "founding" for a lever open from the start. */
    public static String openingDate(Lever lever) {
        return dateOf(lever, openingMonth(lever));
    }

    /** A lever's opening month as a date: "founding" for month 1 or earlier, the month and year where the study dates it to a month, else the year alone. */
    private static String dateOf(Lever lever, int openingMonth) {
        if (openingMonth <= 1) return "founding";
        return lever.monthKnown() ? CityCalendar.format(openingMonth) : Integer.toString(CityCalendar.yearOf(openingMonth));
    }

    /**
     * The lever as a player reads it, with its article, lower case: "an income tax", "a profit tax", "student aid", "zoning" (the three that
     * take none: EI, student aid and zoning). One rule for the inbox's title and the Policy screen's shut card, so they never disagree.
     */
    public static String named(Lever lever) {
        String label = lever.label();
        boolean uncountable = lever == Lever.EI || lever == Lever.STUDENT_AID || lever == Lever.ZONING;
        if (uncountable) return label;
        return ("AEIOUaeiou".indexOf(label.charAt(0)) >= 0 ? "an " : "a ") + label;
    }

    /**
     * What a shut lever's card says first, on Canada's date: "Opens 1917 - an income tax: 4% above $1,500 (single) or $3,000; a supertax of
     * 2% to 25%": when it opens, what it is, and Canada's first terms for it.
     */
    public static String shutLine(Lever lever) {
        return shutLine(lever, openingMonth(lever));
    }

    /** The same on the city's own opening month (R3's finds move it earlier): the month and year where the study dates it to a month, else the year. */
    public static String shutLine(Lever lever, int cityOpeningMonth) {
        return "Opens " + dateOf(lever, cityOpeningMonth) + " - " + named(lever) + ": " + lever.terms();
    }

    /** What a shut lever's card says second, on Canada's date: "Canada had none until then; the tax opens at the city's founding rate, 15%." */
    public static String shutWhy(Lever lever) {
        return shutWhy(lever, openingMonth(lever));
    }

    /** The same for a city whose opening month a find has moved earlier: then "then" would name the wrong date, so it says history's and the find. */
    public static String shutWhy(Lever lever, int cityOpeningMonth) {
        String until = cityOpeningMonth == openingMonth(lever) ? "then"
                : openingDate(lever) + ", and a find has brought it forward";
        String opens = opensAt(lever);
        return "Canada had none until " + until + "; " + Character.toLowerCase(opens.charAt(0)) + opens.substring(1) + ".";
    }

    /**
     * The inbox sentence for the month a lever opens: when Canada first had it, under what, on what terms, where it opens (an income tax at
     * the founding rate, the minimum wage's floor at the going unskilled wage, zoning with no ground zoned until a zone is drawn, every other
     * dated lever at zero) and that the setting is the
     * player's. A lever open from founding has no first day to report, so its notice is empty.
     */
    public static String notice(Lever lever) {
        if (openAtFounding(lever)) return "";
        String under = lever.act().isEmpty() ? "" : " (" + lever.act() + ")";
        return "Canada's first " + lever.label() + " came in " + openingDate(lever) + under + ": " + lever.terms() + ". " + opensAt(lever)
                + "; the setting is yours.";
    }

    /**
     * The one line under a dial that sets several levers at once ("every tax") naming the ones still shut, which it leaves alone: "Still shut, so
     * this dial leaves them alone: profit tax (opens 1916) and sales tax (opens 1920)." Each lever's date is the city's own opening month, as
     * the caller reads it; an empty list gives an empty line.
     */
    public static String stillShut(java.util.List<Lever> shut, java.util.function.ToIntFunction<Lever> cityOpeningMonth) {
        if (shut.isEmpty()) return "";
        java.util.List<String> parts = new java.util.ArrayList<>();
        for (Lever l : shut) parts.add(l.label() + " (opens " + dateOf(l, cityOpeningMonth.applyAsInt(l)) + ")");
        String list = parts.size() == 1 ? parts.get(0)
                : String.join(", ", parts.subList(0, parts.size() - 1)) + " and " + parts.get(parts.size() - 1);
        return "Still shut, so this dial leaves " + (shut.size() == 1 ? "it" : "them") + " alone: " + list + ".";
    }

    /** Where a dated lever opens, as a sentence the notice finishes: the founding rate, the going unskilled wage, or zero. */
    static String opensAt(Lever lever) {
        if (opensAtFoundingRate(lever)) {
            return "The tax opens at the city's founding rate, " + DecisionLog.pct(TaxPolicy.DEFAULT_INCOME_TAX);
        }
        if (lever == Lever.MINIMUM_WAGE) return "The floor opens at the going unskilled wage";
        if (lever == Lever.ZONING) return "No ground is zoned until you draw a zone";
        return "The lever opens at zero";
    }
}

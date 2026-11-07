package ham.citybuildersim;

import java.util.List;

/**
 * The wage ladder a city is founded on: today's (PayTier's) or 1901's (the census's).
 *
 * WHY A SECOND LADDER, AND NOT PayTier EDITED (R0d, 2026-11-06; docs/studies/research-1900.md 5.2 and 12.1, "Wage ladder: make the
 * normalization explicit"; R0-notes call 2). A city founded in 1900 pays 1901's wages: about a quarter of today's real wage, with the 1901
 * census's ratios between the skills (an engineer 3.2 times a factory man, against 2.8 between today's high-tech engineer and a labourer).
 * PayTier is every default city's ladder - the harnesses, the playtest, every save a player holds measure it - so editing its figures would
 * move every city by one bit. The ladder is therefore a choice made once, at the founding, and read through here: TODAY answers with PayTier's
 * own figures (to the bit), Y1901 with the census's. It is derived from the saved founding (Founding.isUnfitted(), R1c call 1 and R2 call 3),
 * so nothing about it is saved.
 *
 * ONE NORMALIZATION, STATED ONCE. The census gives dollars a year; the game pays thousands a month, in founding money, and founding money is
 * 1900's price level (PriceLevel.FACTOR). The US CPI is the same in 1900 and 1901 (8.333), and the Canadian dollar stood at par with the US
 * dollar on gold, so a 1901 census dollar IS a founding dollar: monthlyFromAnnual() converts it and nothing else touches it - no PriceLevel.FACTOR
 * on it, and no cost-of-living multiplier on top, because LabourMarket's own costOfLiving starts at 1 in the founding month (research-1900 12.1,
 * "Do not apply both a historical multiplier and LabourMarket's cost-of-living multiplier to the same wage").
 *
 * WHAT THE CENSUS DOES NOT SAY IS FLAGGED. One rung, the elite (doctors and lawyers, self-employed in 1901 and so outside a census of
 * wage-earners), is not in the 1901 tables: the table keeps an estimate flag and says how it was made, and the harness reads it
 * (WageLadderCheck 1), so a figure that is an estimate never reads as an observation.
 *
 * Sources (Statistics Canada, under the Statistics Canada Open Licence, commercial use allowed; B1 is also out of Crown copyright, Copyright
 * Act s.12, fifty years after publication):
 *   [B1]     Census and Statistics Office, Bulletin I, "Wage-earners by occupations" (the 1901 census, 1907), Table II,
 *            https://publications.gc.ca/collections/collection_2017/statcan/CS98-1901P-1.pdf
 *   [CYB07a] Canada Year Book 1907, "Earnings at regular work, by class of occupation, 1901",
 *            https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015901-eng.htm
 *   [CYB07b] Canada Year Book 1907, "Employees and salaries, by principal occupation in the professional class, 1901",
 *            https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015903-eng.htm
 */
public final class WageLadder {

    /** One rung of the 1901 table: the PayTier it prices, the census occupation it stands on, the year's earnings, where it was read, whether it is an estimate, and a note. */
    public record Rung(PayTier tier, String comparator, double annual1901, String source, boolean estimate, String note) { }

    /* ===== THE CONVERSION, ONCE ===== */

    /**
     * What one 1901 census dollar is in founding money: one. Founding money is 1900's price level (PriceLevel.FACTOR); the US CPI is the same in
     * 1900 and 1901 (8.333) and the Canadian dollar stood at par with the US dollar on gold, so the census's dollar is the founding dollar,
     * converted here and nowhere else.
     */
    public static final double FOUNDING_PER_CENSUS_DOLLAR = 1.0;

    /** The months in the census's year of earnings. */
    public static final double MONTHS_A_YEAR = 12;

    /** The game's wages are in thousands: .0227 reads $22.67. */
    public static final double THOUSAND = 1000;

    /** A year's earnings in census dollars as a month's wage in thousands of founding money: the one conversion. */
    public static double monthlyFromAnnual(double annualCensusDollars) {
        return annualCensusDollars * FOUNDING_PER_CENSUS_DOLLAR / MONTHS_A_YEAR / THOUSAND;
    }

    /* ===== THE 1901 TABLE: one rung per PayTier, in PayTier's order; each figure is one line to change ===== */

    /** Civil engineers' earnings in 1901 (B1, Table II: $852,968 over 648 men), the professional rung and the base the elite rung's estimate is struck on. */
    public static final double CIVIL_ENGINEERS_1901 = 1316.31;

    /**
     * The 1901 ladder, a rung for each PayTier in PayTier's order, each the comparator nearest the rung's own today (PayTier's comments), men's
     * earnings at regular work for a year (R0d's research, 2026-11-06: the averages B1 prints, or its total over its number where the scan is
     * illegible). Five rungs are the census's; the elite rung has no representative 1901 figure and is an estimate.
     */
    public static final List<Rung> RUNGS_1901 = List.of(
            new Rung(PayTier.UNSKILLED, "Labourers, men", 256.29, "B1", false,
                    "$27,601,806 over 107,698 (the domestic and personal class's labourers; the farm hands averaged $207.55, often with board, CYB07a)"),
            new Rung(PayTier.SKILLED, "Electricians, men", 518.12, "CYB07b", false,
                    "$1,242,970 over 2,399, today's comparator a service technician (office clerks $523.97, B1; the trade and transport class $503.22, CYB07a)"),
            new Rung(PayTier.COLLEGE, "Draughtsmen and designers, men", 723.72, "B1", false,
                    "$240,999 over 333, today's comparator a civil engineering technician (bookkeepers $584.19, B1, the scan's count repaired; nurses and midwives, women, $233.69, B1)"),
            new Rung(PayTier.PROFESSIONAL, "Civil engineers", CIVIL_ENGINEERS_1901, "B1", false,
                    "government officials $1,335.36 (B1); CYB07b's engineers, $1,305.59, divide B1's salaries by 1,143 of its 1,243 engineers, a misprint"),
            new Rung(PayTier.SENIOR_PROFESSIONAL, "Bank managers", 1784.37, "B1", false,
                    "the printed average; the bulletin's total and count disagree by 5% (accountants $835.11, bank inspectors $2,177.50, B1)"),
            new Rung(PayTier.ELITE, "no 1901 figure (doctors and lawyers were self-employed): civil engineers x today's elite/professional ratio",
                    CIVIL_ENGINEERS_1901 * PayTier.ELITE.getTodaysWage() / PayTier.PROFESSIONAL.getTodaysWage(), "est.", true,
                    "the census counted wage-earners: 31 salaried physicians averaged $1,097.74 and 8 lawyers about $750 (B1); the 1941 census's wage-earning physicians and lawyers took 1.17 times a civil engineer"));

    static {
        PayTier[] tiers = PayTier.values();
        if (RUNGS_1901.size() != tiers.length) throw new IllegalStateException("RUNGS_1901 has " + RUNGS_1901.size() + " rungs for " + tiers.length + " tiers");
        for (int i = 0; i < tiers.length; i++) {
            if (RUNGS_1901.get(i).tier() != tiers[i]) throw new IllegalStateException("RUNGS_1901 rung " + i + " is " + RUNGS_1901.get(i).tier() + ", not " + tiers[i]);
        }
    }

    /** The 1901 rung for a tier (the table is in PayTier's order). */
    public static Rung rung1901(PayTier tier) { return RUNGS_1901.get(tier.ordinal()); }

    /* ===== THE TWO LADDERS ===== */

    /** Today's ladder: PayTier's own figures, the default of every city. */
    public static final WageLadder TODAY = new WageLadder("today's", false);

    /** 1901's ladder: the census's earnings in founding money, for a city founded the 1900 way. */
    public static final WageLadder Y1901 = new WageLadder("1901's", true);

    /** The ladder a city is founded on: 1901's where it is founded the 1900 way (Founding.isUnfitted()), today's otherwise. */
    public static WageLadder of(Founding founding) {
        return founding != null && founding.isUnfitted() ? Y1901 : TODAY;
    }

    private final String name;
    private final boolean historical;

    private WageLadder(String name, boolean historical) {
        this.name = name;
        this.historical = historical;
    }

    /** What the ladder is called: today's or 1901's. */
    public String name() { return name; }

    /** Whether this is the census's ladder rather than PayTier's. */
    public boolean isHistorical() { return historical; }

    /** What a month of a rung pays, in thousands of founding money: PayTier's own on today's ladder, the 1901 rung's year over twelve on 1901's. */
    public double monthlyWage(PayTier tier) {
        return historical ? monthlyFromAnnual(rung1901(tier).annual1901()) : tier.getMonthlyWage();
    }

    /** The bottom rung's wage: what the minimum wage starts at, the dial's bounds are multiples of, and the rent and the pension are struck on. */
    public double anchor() { return monthlyWage(PayTier.UNSKILLED); }

    /**
     * What this job pays relative to the unskilled floor on this ladder. Today's is LabourMarket.ratioOf()'s expression, to the bit; 1901's is
     * the job's rung over the 1901 anchor.
     */
    public double ratio(JobType job) {
        if (!historical) {
            double floor = PayTier.UNSKILLED.getMonthlyWage();
            return floor > 0 ? PayTier.wageOf(job) / floor : 1;
        }
        double floor = anchor();
        return floor > 0 ? monthlyWage(PayTier.of(job)) / floor : 1;
    }

    @Override public String toString() { return name; }
}

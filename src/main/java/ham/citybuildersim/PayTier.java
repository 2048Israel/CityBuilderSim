package ham.citybuildersim;

/**
 * The six pay levels a household can be in.
 *
 * WHY SIX, AND WHY THIS AXIS AT ALL
 *
 * Families are grouped by what they earn, so there has to be a finite set of
 * "what they earn". There were TEN distinct wage figures across the eleven job
 * types - not eleven, because UNIV_FINANCE and UNIV_HIGHTECH_ENG were both 6.5 -
 * and ten tiers times a dozen family shapes is a matrix nobody can read.
 *
 * Grouped by role rather than by arithmetic convenience: the three college jobs
 * pay the same as each other, the two professional doctorates pay the same, and
 * the applied-science group sits between them. Measured against a real city of
 * 8,792 the collapse moves the whole wage bill by **-1.08%**, which is the
 * price of the tidier axis and cheap at that.
 *
 * WHAT THE SAME MEASUREMENT SHOWED, WHICH MATTERS MORE
 *
 * That city employed 3,371 NO_DIPLOMA, 1,274 DIPLOMA, 114 COLLEGE_ENGINEERING,
 * 2 COLLEGE_BUSINESS, 2 UNIV_SCIENCE - and ZERO doctors, lawyers, finance staff,
 * high-tech engineers or policy staff. Six of the eleven job types are not used
 * by any building in the game.
 *
 * So in play this axis currently has about three live tiers, and the family
 * matrix will be mostly empty until something employs the top half of the
 * ladder. That is a fact about the BUILDINGS, not about this enum, and it is
 * recorded here because it is the first thing that will look broken on the
 * demographics screen and the first thing somebody will try to "fix" in the
 * wrong place.
 */
public enum PayTier {

    /** Labouring. */
    UNSKILLED("Unskilled", .800),

    /** Trades and clerical. */
    SKILLED("Skilled", 1.500),

    /** The three college paths - health, business, engineering. */
    COLLEGE("College", 3.500),

    /** Applied science and public administration. */
    PROFESSIONAL("Professional", 6.000),

    /** Finance and high technology. */
    SENIOR_PROFESSIONAL("Senior professional", 6.500),

    /** Medicine and law. */
    ELITE("Elite", 8.000);

    private final String label;
    private final double monthlyWage;

    PayTier(String label, double monthlyWage) {
        this.label = label;
        this.monthlyWage = monthlyWage;
    }

    public String getLabel()      { return label; }
    public double getMonthlyWage(){ return monthlyWage; }

    /**
     * Which tier a job belongs to.
     *
     * THE ONE DEFINITION. PopulationManager.setWagesPerType() reads its figures
     * from here rather than keeping its own list, because two wage tables in two
     * files is the shape of bug this codebase has paid for four times already -
     * the copy is right the day it is written and wrong the first time the
     * original moves.
     */
    public static PayTier of(JobType job) {
        return switch (job) {
            case NO_DIPLOMA -> UNSKILLED;
            case DIPLOMA    -> SKILLED;

            case COLLEGE_HEALTH, COLLEGE_BUSINESS, COLLEGE_ENGINEERING -> COLLEGE;

            case UNIV_SCIENCE, UNIV_POLICY -> PROFESSIONAL;

            case UNIV_FINANCE, UNIV_HIGHTECH_ENG -> SENIOR_PROFESSIONAL;

            case UNIV_DOCTOR, UNIV_LAW -> ELITE;
        };
    }

    /** The wage every job of this type is paid. */
    public static double wageOf(JobType job) {
        return of(job).getMonthlyWage();
    }
}

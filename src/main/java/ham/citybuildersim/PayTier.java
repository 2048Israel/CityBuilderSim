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
/* =====================================================================
   THE LADDER, RE-ANCHORED ON PUBLISHED MEDIANS - 2026-09-09

   Every rung below is a Nova Scotia Job Bank median at 173 hours a month.
   Figures are in THOUSANDS, so .800 read $800 and 3.460 reads $3,460 - see
   BuildingsTemplate's header and claude/reading-the-numbers.md.

   WHAT WAS WRONG, MEASURED:

       tier                   was      real comparator                 real
       Unskilled             $800      material-handling labourer    $3,460   4.3x low
       Skilled             $1,500      electronic service tech       $4,524   3.0x low
       College             $3,500      LPN / civil eng technician    $5,500   1.6x low
       Professional        $6,000      civil engineer                $8,190   1.4x low
       Senior professional $6,500      software eng / systems mgr    $9,600   1.5x low
       Elite               $8,000      general practitioner         $15,595   1.9x low

   TWO FAULTS, AND THE SECOND IS THE INTERESTING ONE.

   The bottom was far too low, and it STAYED too low - deflated by the price
   index, the unskilled wage sat between $586 and $1,815 for the whole of a
   600-month run, never within 2x of the real figure.

   And the ladder was far too SPREAD: 10x top to bottom, against Nova Scotia's
   real 4.5x, widening to 11-18x by mid-game as shortage premiums opened it
   further. Real wage ladders are much flatter than intuition says. The old
   spread made a degree worth roughly three times what it is worth in life,
   which quietly inflated the whole demographics model - every graduate was a
   lottery win rather than a step up.

   Jerus: "check wages, check if they match reality." They did not.
   ===================================================================== */
public enum PayTier {

    /** Labouring. Material-handling labourer, NS median $20.00/hr. */
    UNSKILLED("Unskilled", 3.460),

    /** Trades and clerical. Electronic service technician, $26.15/hr. */
    SKILLED("Skilled", 4.500),

    /** The three college paths - health, business, engineering.
     *  Licensed practical nurse $30.42/hr, civil engineering technician $33.12. */
    COLLEGE("College", 5.500),

    /** Applied science and public administration. Civil engineer, $47.34/hr. */
    PROFESSIONAL("Professional", 8.200),

    /** Finance and high technology. Software engineer $52.50, systems manager $58.97. */
    SENIOR_PROFESSIONAL("Senior professional", 9.600),

    /** Medicine and law. General practitioner, $187,135 a year. */
    ELITE("Elite", 15.600);

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

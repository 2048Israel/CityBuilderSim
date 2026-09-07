package ham.citybuildersim;

/**
 * What a school actually teaches.
 *
 * The same shape as CareType and for the same reason: nine education buildings
 * would otherwise know their capacity and not what that capacity was FOR. A
 * High School's 900 places and a Medical School's 90 are both "capacity", and
 * without this field the only thing telling them apart is the name on the
 * button - which is to say, nothing a line of code can read.
 *
 * TWO KINDS OF SCHOOL, AND THE DIFFERENCE IS THE WHOLE DESIGN
 *
 * The first six move a person UP THE LADDER: elementary, middle and high school
 * turn a child into somebody with a diploma; a college and a university turn a
 * diploma into a qualification. What comes out is a WageBand, and within a band
 * workers are interchangeable.
 *
 * The last four do something else entirely - they do not raise anybody's level,
 * they make a PROFESSION possible. A city without a medical school can have all
 * the university graduates it likes and not one of them can be a doctor; the
 * posts sit empty and the only doctors it will ever have are the ones who moved
 * there. Build the school and its graduates can hold that job.
 *
 * Jerus's design: "they gate the specialisations". It breaks the
 * within-a-band fungibility exactly where that simplification is least
 * believable, and it turns each graduate school into a distinct thing a city
 * decides to become rather than another building that makes the same number
 * bigger.
 *
 * WHY NOTHING REFUSES TO BE BUILT
 *
 * A graduate school in a town of forty thousand is not forbidden, it is
 * ruinous. Jerus: "basically there would be one student in the whole grad
 * school, so cost ineffective basically." The cost is fixed and enormous; the
 * throughput is however many people are eligible and willing, which in a small
 * city is nearly nobody. So the eight-hundred-thousand-population gate everyone
 * talks about is not a rule anywhere in this file - it is where the arithmetic
 * stops being stupid, and the player can work it out by reading the build menu.
 */
public enum EducationType {

    /** Every building in the game that is not a school. */
    NONE("None", null, null),

    /* ------------------------- the basic ladder -------------------------
       Three stages across two age bands, and the pipeline is only as wide as
       its narrowest stage. A city with elementary schools for every child and
       one high school does not produce a lot of half-educated adults; it
       produces as many diplomas as the high school can seat. See
       Education.basicCoverage().
       ------------------------------------------------------------------ */

    /** Ages 6-10, the first half of CHILD. */
    ELEMENTARY("Elementary", null, null),

    /** Ages 10-13, the second half. Nobody reaches high school without it. */
    MIDDLE("Middle school", null, null),

    /** The TEEN band, and what actually hands out the diploma. */
    HIGH("High school", WageBand.DIPLOMA, null),

    /* --------------------------- adult study --------------------------- */

    COLLEGE("College", WageBand.COLLEGE, null),
    UNIVERSITY("University", WageBand.UNIVERSITY, null),

    /* ------------------------- the professions -------------------------
       Each of these licenses ONE job type and raises nobody's band. Their
       students are already university graduates; what they leave with is
       permission to hold a particular post.
       ------------------------------------------------------------------ */

    MEDICAL("Medical school", null, JobType.UNIV_DOCTOR),
    LAW("Law school", null, JobType.UNIV_LAW),
    BUSINESS("Business school", null, JobType.UNIV_FINANCE),
    ENGINEERING("Institute of technology", null, JobType.UNIV_HIGHTECH_ENG);

    private final String label;
    private final WageBand produces;
    private final JobType licenses;

    EducationType(String label, WageBand produces, JobType licenses) {
        this.label = label;
        this.produces = produces;
        this.licenses = licenses;
    }

    public String getLabel() { return label; }

    /** The band somebody leaves with, or null for a stage that only feeds one. */
    public WageBand produces() { return produces; }

    /** A course adults enrol in and leave the labour supply for; the basic stages are not. */
    public boolean isAdult() { return this == COLLEGE || this == UNIVERSITY || isProfessional(); }

    /** The job this school makes possible, or null if it teaches a level. */
    public JobType licenses() { return licenses; }

    /** True for the three stages that a child has to pass through in order. */
    public boolean isBasic() {
        return this == ELEMENTARY || this == MIDDLE || this == HIGH;
    }

    /** True for the four that gate a profession rather than raising a level. */
    public boolean isProfessional() { return licenses != null; }

    /**
     * Which age band this school's places are measured against.
     *
     * The denominator, exactly as in CareType: a city with twice the children
     * needs twice the elementary places without anybody deciding so. Adult
     * study has no age denominator - it is measured against the people
     * qualified to enrol, which is a band and not an age - so it returns null.
     */
    public AgeBand servesAges() {
        switch (this) {
            case ELEMENTARY:
            case MIDDLE: return AgeBand.CHILD;
            case HIGH:   return AgeBand.TEEN;
            default:     return null;
        }
    }

    /**
     * What somebody must already have to enrol.
     *
     * Null for the basic stages, which take whoever is the right age. The four
     * professional schools all take university graduates, which is why a city
     * cannot shortcut to a medical school - not because anything forbids it,
     * but because it would have no students.
     */
    public WageBand requires() {
        switch (this) {
            case COLLEGE:     return WageBand.DIPLOMA;
            case UNIVERSITY:  return WageBand.DIPLOMA;
            case MEDICAL:
            case LAW:
            case BUSINESS:
            case ENGINEERING: return WageBand.UNIVERSITY;
            default:          return null;
        }
    }

    /**
     * What share of the outside world's graduates already hold this licence.
     *
     * WITHOUT THIS A CITY COULD NEVER HAVE A DOCTOR. Migration brings people by
     * BAND, so an arriving university graduate had no licence and a city with
     * no medical school could not staff a hospital at any price, ever - which
     * destroys the whole "import while you are small, train once you are big"
     * arc the world-supply mix was built for. Some of the graduates who move to
     * a city are already doctors, and how many is what these numbers say.
     *
     * They are shares OF THE UNIVERSITY BAND, not of the whole world, and they
     * deliberately do not sum to one: most graduates anywhere hold no
     * professional licence at all.
     */
    public double worldLicenceShare() {
        switch (this) {
            case MEDICAL:     return .14;
            case LAW:         return .10;
            case BUSINESS:    return .11;
            case ENGINEERING: return .09;
            default:          return 0;
        }
    }

    /**
     * How long the course runs, in months.
     *
     * Throughput is places divided by this: a university with 2,000 seats and a
     * four-year degree graduates about forty a month, not two thousand. It is
     * where the lag lives, and the lag is the entire character of the mechanic -
     * a medical school built today is doctors in the 2040s.
     */
    public int months() {
        switch (this) {
            case ELEMENTARY:  return 48;
            case MIDDLE:      return 36;
            case HIGH:        return 60;
            case COLLEGE:     return 24;
            case UNIVERSITY:  return 48;
            case MEDICAL:     return 84;
            case LAW:         return 36;
            case BUSINESS:    return 24;
            case ENGINEERING: return 48;
            default:          return 12;
        }
    }
}

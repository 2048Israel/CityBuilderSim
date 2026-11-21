package ham.citybuildersim;

/**
 * The two stages of an adult life inside AgeBand.ADULT: the young, 18-34, and
 * the established, 35-69.
 *
 * WHY (HS2, 2026-11-19; docs/studies/spec-households.md 3.4). The pyramid held
 * the adults as one band of fifty-two years drained at 1/624 a month, so an
 * eighteen-year-old was as likely to retire this month as a sixty-nine-year-old,
 * and every working household was the same age: a couple with a teen and a
 * couple of twenty-five drew on one pool of adults and one pool of money.
 * Jerus: a "large family would be older adults". Now the adults are two
 * compartments in a row: the young move up at 1/204 a month and the
 * established retire at 1/420, both at the adult mortality, so the adult
 * years are an Erlang spread rather than an exponential one, and a city of
 * young migrants retires fewer people in its first decades. The split at 35
 * is where Canadian home-ownership jumps (44% to 63%) and median net worth
 * more than doubles (the study's [S1]); retirement stays at 70 (its call 5).
 *
 * NOT A BAND. AgeBand.ADULT stays the band every reader asks for, and the
 * pyramid answers it with the two stages' sum (PopulationCohorts.get), so the
 * workforce, the arrivals' mix and every screen read what they read before.
 * Only the households' books and the families' builder ask for a stage.
 */
public enum AdultStage {

    YOUNG       ("Young adults",       18, 35),
    ESTABLISHED ("Established adults", 35, 70);

    private final String label;
    private final int fromAge;
    private final int toAge;

    AdultStage(String label, int fromAge, int toAge) {
        this.label = label;
        this.fromAge = fromAge;
        this.toAge = toAge;
    }

    public String getLabel() { return label; }
    public int getFromAge()  { return fromAge; }
    public int getToAge()    { return toAge; }

    /** How many months an adult spends in the stage, on average: 204 young, 420 established. */
    public int spanMonths() {
        return (toAge - fromAge) * 12;
    }

    /**
     * The fraction that moves on each month: the young to the established,
     * the established to the seniors. Exponential within the stage, as every
     * band's is (AgeBand.monthlyOutflowRate()'s note); two stages in a row is
     * what makes the adult years as a whole less so.
     */
    public double monthlyOutflowRate() {
        return 1.0 / spanMonths();
    }

    /** The stage after this one, or null for the established, who retire into AgeBand.SENIOR. */
    public AdultStage next() {
        int i = ordinal() + 1;
        return i < values().length ? values()[i] : null;
    }

    /**
     * Whether households of this stage take the shape. The young form singles,
     * couples and young families - a baby, a child, a baby and a child, a
     * single parent - and the flatshare; the teens and the large families are
     * the established's (the study's 3.4). So 7 shapes young and all 11
     * established: 108 working cells, against the full cross's 132.
     */
    public boolean forms(FamilyStructure shape) {
        if (shape == null || shape.isRetired()) return false;
        if (this == ESTABLISHED) return true;
        return switch (shape) {
            case SINGLE_ADULT, COUPLE, SINGLE_PARENT, COUPLE_BABY, COUPLE_CHILD,
                 COUPLE_BABY_CHILD, SHARED_ADULTS -> true;
            default -> false;
        };
    }

    /** The stage a household in this stage's place in the save is written as: "" for the established, whose keys are the ones from before the stages. */
    public String keySuffix() {
        return this == ESTABLISHED ? "" : ":" + name();
    }
}

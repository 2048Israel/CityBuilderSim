package ham.citybuildersim;

/**
 * The shapes a household comes in.
 *
 * Each one declares how many of each age band it holds, and that declaration is
 * the whole definition - the model assembles households by drawing from the
 * cohorts until it runs out, so a shape is just a shopping list of people.
 *
 * SENIORS KEEP TO THEMSELVES, which is a modelling choice rather than a claim
 * about how people live. A senior household has no earner in it, so mixing
 * seniors into working households would put a pay tier on someone who has no
 * job - and the tier axis is the thing that makes the matrix mean anything.
 * Multi-generational households are a real omission and are noted as such.
 *
 * COUPLES SHARE ONE PAY TIER, per Jerus: the model has one tier per household,
 * so a household of two earners in different tiers cannot be represented. Real
 * enough at the coarse end - people do partner within their own income band far
 * more than at random - and the alternative is a tier-pair matrix six times
 * larger for a distinction nothing currently reads.
 */
public enum FamilyStructure {

    /* ----- households with nobody of working age ----- */

    SENIOR_ALONE   ("Senior living alone",      0, 0, 0, 0, 1),
    SENIOR_COUPLE  ("Senior couple",            0, 0, 0, 0, 2),

    /* ----- working-age households ----- */

    SINGLE_ADULT   ("Single adult",             0, 0, 0, 1, 0),
    COUPLE         ("Couple, no children",      0, 0, 0, 2, 0),

    SINGLE_PARENT  ("Single parent, one child", 0, 1, 0, 1, 0),

    COUPLE_BABY    ("Couple with a baby",       1, 0, 0, 2, 0),
    COUPLE_CHILD   ("Couple with a child",      0, 1, 0, 2, 0),
    COUPLE_TEEN    ("Couple with a teen",       0, 0, 1, 2, 0),

    COUPLE_BABY_CHILD ("Couple, a baby and a child", 1, 1, 0, 2, 0),
    COUPLE_CHILD_TEEN ("Couple, a child and a teen", 0, 1, 1, 2, 0),
    COUPLE_TWO_CHILDREN("Couple with two children",  0, 2, 0, 2, 0),

    LARGE_FAMILY   ("Large family",             1, 2, 1, 2, 0),

    /**
     * Five single adults in one home, formed ONLY when homes run short.
     *
     * Jerus's answer to homelessness, and a good one: rather than counting
     * people with nowhere to go, the model crowds them. It is what actually
     * happens when housing is tight - people who would rather live alone take a
     * flatshare - and it keeps the pressure VISIBLE in the same table as every
     * other household instead of hiding it in a homeless counter nobody can act
     * on.
     *
     * It is also the signal the housing model reads: single adults prefer to
     * live alone, so a city with any of these is a city that is short of homes,
     * and how many there are is how short.
     */
    SHARED_ADULTS  ("Five adults sharing",      0, 0, 0, 5, 0);

    private final String label;
    private final int[] members = new int[AgeBand.values().length];

    FamilyStructure(String label, int babies, int children, int teens,
                    int adults, int seniors) {
        this.label = label;
        members[AgeBand.BABY.ordinal()]   = babies;
        members[AgeBand.CHILD.ordinal()]  = children;
        members[AgeBand.TEEN.ordinal()]   = teens;
        members[AgeBand.ADULT.ordinal()]  = adults;
        members[AgeBand.SENIOR.ordinal()] = seniors;
    }

    public String getLabel() { return label; }

    public int membersOf(AgeBand band) {
        return members[band.ordinal()];
    }

    /** Everyone in the household, of any age. */
    public int size() {
        int n = 0;
        for (int m : members) n += m;
        return n;
    }

    /** How many earners it can field. Zero for a senior household. */
    public int earners() {
        return members[AgeBand.ADULT.ordinal()];
    }

    /** Dependants per household - the reason the tier matters. */
    public int dependants() {
        return size() - earners();
    }

    /** True for households with no working-age member, which carry no pay tier. */
    public boolean isRetired() {
        return earners() == 0;
    }
}

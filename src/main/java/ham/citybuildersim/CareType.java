package ham.citybuildersim;

/**
 * What a healthcare building actually does.
 *
 * WHY THIS EXISTS AT ALL
 *
 * The fourteen healthcare buildings shipped knowing their capacity but not what
 * that capacity was FOR. A Nursing Home's 220 and a Walk-in Clinic's 2,500 are
 * both "capacity", and the only thing distinguishing them was the name on the
 * button - which is to say, nothing a line of code could read. The comment in
 * BuildingManager said "capacity is the age band it serves"; that sentence was
 * true and it lived in a comment, which meant every future caller would have had
 * to re-derive it by matching strings. A rule stated only in prose is a rule
 * nobody can check.
 *
 * So the care type is data now, on the template and in buildings.json, and
 * BuildingDataCheck compares it like every other field.
 *
 * THE DENOMINATOR IS THE POINT
 *
 * Each type names the slice of the population it has to cover, and that slice is
 * what coverage divides by. Childcare beds serve babies and children, so a city
 * with a baby boom needs more of them without anybody deciding so; senior care
 * serves seniors, which is the ageing problem stated as arithmetic; general care
 * serves everyone, and it is general care that decides how much of the workforce
 * is off sick this month.
 *
 * BURIAL AND CREMATION SERVE THE DEAD, which is not a band of the living, so
 * servedBy() is false for every band and populationServed() returns zero. Their
 * capacity is measured against DEATHS - plots consumed permanently in the burial
 * case, throughput per month in the cremation case - and that is a different
 * denominator with a different unit, so it deliberately does not pretend to
 * share this one. NONE is the same shape for the opposite reason: every
 * non-healthcare building in the game has it.
 */
public enum CareType {

    /** Everything that is not a healthcare building. */
    NONE("None"),

    /** Daycare and nurseries: babies and children. */
    CHILDCARE("Childcare"),

    /** Clinics and hospitals: the whole city, and the sick rate comes from here. */
    GENERAL("General care"),

    /** Home care through long-term care: seniors. */
    SENIOR("Senior care"),

    /** Cemeteries. Plots are consumed permanently and the land never comes back. */
    BURIAL("Burial"),

    /** Crematoria. Throughput per month, almost no land, a great deal of power. */
    CREMATION("Cremation");

    private final String label;

    CareType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** True for the types whose coverage is measured against living residents. */
    public boolean servesTheLiving() {
        return this == CHILDCARE || this == GENERAL || this == SENIOR;
    }

    /** True if a resident in this band is somebody this type has to have room for. */
    public boolean servedBy(AgeBand band) {
        switch (this) {
            case CHILDCARE: return band == AgeBand.BABY || band == AgeBand.CHILD;
            case GENERAL:   return true;
            case SENIOR:    return band == AgeBand.SENIOR;
            default:        return false;
        }
    }

    /**
     * How many people this type is on the hook for, given the pyramid.
     *
     * Zero for the two that serve the dead and for NONE, so a caller dividing by
     * this must guard - see Health.coverage(), which treats a zero denominator as
     * fully covered rather than as a crisis. A city with no children does not
     * have a childcare shortage.
     */
    public double populationServed(PopulationCohorts cohorts) {
        if (cohorts == null || !servesTheLiving()) return 0;

        double served = 0;
        for (AgeBand band : AgeBand.values()) {
            if (servedBy(band)) served += cohorts.get(band);
        }
        return served;
    }
}

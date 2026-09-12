package ham.citybuildersim;

/**
 * What a safety building does: police, or prison cells.
 *
 * Jerus, 2026-09-11: "we need police, and also prison". The same shape as
 * CareType and EducationType, and for the same reason those exist - a rule
 * that reads "Police Station" off the button breaks the first time somebody
 * renames it, so what the capacity is FOR is data on the template and in
 * buildings.json, and BuildingDataCheck compares it.
 *
 * WHAT CAPACITY COUNTS
 *
 *   POLICE  sworn officers. Coverage is officers against FULL_PER_100K per
 *           hundred thousand people - twice Canada's 180, Jerus's call - and
 *           coverage is what deters and what catches. See Crime.
 *   PRISON  cells. A cell holds one prisoner for the six months of a
 *           sentence; a city whose police catch more than its cells can hold
 *           has people caught and not held.
 *
 * Both are cut by staffing like everything else the city runs: a police
 * station with no officers patrols nothing, and a jail with no guards holds
 * nobody.
 */
public enum SafetyType {

    /** Everything that is not a safety building. */
    NONE("None", ""),

    /** Police stations and headquarters: officers. */
    POLICE("Police", "officers"),

    /** Jails and penitentiaries: cells. */
    PRISON("Prisons", "cells");

    private final String label;
    private final String unit;

    SafetyType(String label, String unit) {
        this.label = label;
        this.unit = unit;
    }

    public String getLabel() { return label; }

    /** What one unit of capacity is: "officers", "cells". */
    public String getUnit()  { return unit; }

    /**
     * What the city has before it builds anything.
     *
     * A CONSTABULARY FOR THE FOUNDING TWELVE HUNDRED, at full coverage, as the
     * founding doctor is - so a new city is not a lawless one on its first
     * morning, and the first police station is something a growing city needs
     * rather than something every city has to buy before its first shop. No
     * founding cells: a town of twelve hundred sends its few to the county,
     * and a city that catches people has to build somewhere to hold them.
     *
     * Not discounted by staffing, like Healthcare.foundingCapacity(): nobody
     * is on the payroll for it, because it is not a building.
     */
    public double foundingCapacity() {
        if (this == POLICE) {
            return Healthcare.FOUNDING_CITY * Crime.FULL_OFFICERS_PER_100K / 100_000.0;
        }
        return 0;
    }
}

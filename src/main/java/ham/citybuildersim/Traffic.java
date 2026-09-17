package ham.citybuildersim;

/**
 * The three things that move, which used to be one number.
 *
 * WHY (2026-09-16). `BuildingsTemplate.roadLoad` was a single figure and it
 * mixed two completely different demands. An Iron Mine was 420 on sixty jobs
 * and thirteen hundred tonnes of ore; a Contact Centre was 240 on three
 * hundred jobs and nothing shipped at all; Low-Rise Apartments were 100 on no
 * jobs whatever. One network carried ore trucks and nurses' cars in the same
 * figure, and the only lever a player had was to build more road.
 *
 * Jerus: "a mine and an apartment building shouldn't be creating the same kind
 * of transportation demand." A freight problem and a commuter problem have
 * different answers - a rail line against a transit line - and until the
 * demands are separate the city cannot say which one it has.
 *
 * WHY BULK IS ITS OWN STREAM AND NOT JUST MORE GOODS. Jerus again: "two types
 * of freighting, goods and large goods, large goods would be stuff like ore".
 * The split is not about weight for its own sake, it is about WHAT CAN CARRY
 * IT. A tonne of ore or aggregate goes by rail happily; a shop's weekly
 * delivery of bread and milk is a van at a back door, and no railway ever
 * built solves it. So the rail-eligibility rule falls out of the classes
 * rather than needing a table of exceptions:
 *
 *                 inside the city          across the boundary
 *   COMMUTERS     transit, capped               -
 *   GOODS         road only                 rail-eligible
 *   BULK          rail-eligible             rail-eligible
 *
 * COMMUTERS NEVER CROSS THE BOUNDARY, which is why there is no fourth
 * constant for it: somebody who moves to the city is migration, not a
 * commute, and this game has no suburbs to commute in from.
 *
 * ORDINAL IS NOT A SAVED KEY today and nothing here is indexed by it in a
 * file - the loads are derived from the building stock every month (see
 * ServicesManager.updateInfrastructure), which is why this could be added at
 * all without moving the save format. If that ever stops being true, new
 * constants go on the END, for the reason BuildingType's header gives.
 */
public enum Traffic {

    /** People, to and from work. Homes send them, workplaces pull them. */
    COMMUTERS("Commuters"),

    /** Palletised and packaged - the shelf, mostly. A van at a back door. */
    GOODS("Goods"),

    /** Ore, steel, crops, aggregate. The things a railway was invented for. */
    BULK("Bulk");

    private final String label;

    Traffic(String label) { this.label = label; }

    public String label() { return label; }

    /** True for the two that are things rather than people. */
    public boolean isFreight() { return this != COMMUTERS; }
}

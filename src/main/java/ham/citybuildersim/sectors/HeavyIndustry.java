package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Good;

/**
 * The mills - Iron & Steel since 0.7.116, under the key it was saved by,
 * "Heavy Industry": the arc furnaces, which melt scrap into steel and sell it
 * to the city's fabricators or abroad, built by what pays.
 *
 * WHY (0.7.116, batch I3; runs/spec-materials.md 2.1 and 2.3, and Jerus's
 * Q2, Q4 and Q6 of 2026-10-08). Until then a mill bought IRON - "iron ore,
 * and the scrap that stands in for it" - local ore first and imported scrap
 * for the rest, sold steel at the US band (1.284 / .847), and was built only
 * into spare local ore: "no spare local ore to smelt" was its plan's gate,
 * and an order the sector could not make a case for was the borrowing
 * spiral the investment engine was written to end. Three of Jerus's answers
 * took that apart. Scrap splits from ore (Q4): the furnaces take SCRAP, and
 * IRON is ore alone, which nothing here uses until the integrated route
 * (batch I4). Steel goes to the world's price (Q6: .650 / .470), at which a
 * mill on imported scrap loses money exporting and earns a little replacing
 * imports. And the sector is renamed Iron & Steel (Q2), its saved key kept.
 *
 * SO IT IS A ProcessingSector (I2's base), and its plan is SpreadPlanner's
 * like every works of the materials chains (the study's 1.2): each furnace a
 * candidate earning its steel at what the city would otherwise pay or get
 * for it (outValue(): the import price for as much as the city imports, the
 * export price past it) less its scrap at what the city would otherwise get
 * or pay for that (inValue()), less its wages, power, water, repairs, tax
 * and consumables, asked the ground, the staff and the money on its whole
 * cost (decision A); the best on its cost ordered, one in flight; a kind
 * whose last furnace earns below nothing six months running the one the
 * spare-capacity rule may sell (idle, then shed). The study's four cases
 * for the two furnaces (its 2.5, at parity): the Electric Arc Furnace -0.5,
 * -0.1, 0.7 and 0.2% of its cost a month, the Steel Mini-Mill -0.5, 0.1, 1.0
 * and 0.4% - thin, and left as they are (its 10.2): built where the city's
 * fabricators import steel and its own scrap is cheap, shed where neither
 * holds. MaterialsCheck 9 holds the rows to the study.
 *
 * Its other rows - the Scrap Yard, the pellet plant, coke ovens, blast
 * furnaces, the oxygen shop and the large mini-mill - come with batch I4.
 */
public final class HeavyIndustry extends ProcessingSector {

    /** The Electric Arc Furnace's permanent id in buildings.json, the Steel Foundry's until 0.7.116 (batch I3; spec-materials 4): what the harnesses and the test player find it by. */
    public static final int ARC_FURNACE = 11;

    /** The Steel Mini-Mill's permanent id in buildings.json (0.7.116). */
    public static final int MINI_MILL = 12;

    public HeavyIndustry() {
        super("Heavy Industry", "Iron & Steel", BuildingType.HEAVY_INDUSTRY);
        makes(Good.STEEL);
        uses(Good.SCRAP);
        blurb("Melts scrap into steel in arc furnaces and sells it to the city's "
                + "fabricators, or abroad at the world's price. Builds a furnace only "
                + "where one pays on its whole cost: where the city imports steel, and "
                + "where its own scrap is cheap.");
    }
}

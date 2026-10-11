package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;

/**
 * Iron mines - Iron Mining since 0.7.120, under the key it was saved by,
 * "Mining" (Jerus's Q2, 2026-10-08: rename, keeping the saved keys). Its own
 * sector because the ore has a price.
 *
 * THE SMALLEST SECTOR CLASS THERE IS, and the shape a thousand of them
 * should have: it says what it makes, and which resource's ground it lifts -
 * everything else is a template's. The books, the credit, the market, the
 * payroll and the export of what the mills do not want are Sector's; the
 * ground as the limit, the plan that sinks a mine on a free site, and the
 * retirement once the ore is out are ExtractionSector's, since 0.7.112
 * (batch I2; runs/spec-materials.md 2.1) - Mining's own code until then,
 * moved there whole so the materials chains' mines and quarries share it.
 *
 * A mine ships what it lifts: the ore is not stockable (see Good.IRON), so
 * what the works will not take at the local price leaves at the export
 * price the same month, which is why a mine is worth building before the
 * works exist. From 0.7.120 (batch I3) nothing in the city used ore - the
 * arc furnaces melt SCRAP - until the integrated route (0.7.123, batch I4):
 * Iron & Steel's Pelletising Plant and blast furnaces buy it now. LandManager
 * decides how much of what was asked for is actually there, and nothing once
 * the deposit is out.
 *
 * FOUR MINES, BY TYPE AND SIZE, SINCE 0.7.124 (batch I5; Jerus's Q3 and Q19,
 * 2026-10-08; runs/spec-materials.md 2.4): on hematite, shipped as dug, the
 * Iron Mine (one site) and the Large Open-Pit Mine (twenty of one field); on
 * magnetite, ground and concentrated three tonnes of rock to one, the
 * Magnetite Mine & Concentrator (one) and the Large Magnetite Complex
 * (twenty). Which the sector sinks is ExtractionSector's rule: the best
 * earnings on the whole cost among those that fit the fields it owns.
 */
public final class Mining extends ExtractionSector {

    /** The Iron Mine's permanent id in buildings.json (0.7.120, batch I3; spec-materials 4): what the harnesses, the test player and the build advice find it by - a name is a label, and the chains rename buildings. */
    public static final int IRON_MINE = 14;

    /** The Large Open-Pit Mine's permanent id (0.7.124, batch I5): 500,000 t of hematite ore a month on twenty sites of one field. */
    public static final int LARGE_OPEN_PIT = 108;

    /** The Magnetite Mine & Concentrator's (0.7.124): 20,000 t of concentrate a month from 60,000 t of rock, on one magnetite site. */
    public static final int MAGNETITE_MINE = 109;

    /** The Large Magnetite Complex's (0.7.124): 400,000 t of concentrate a month from 1.2 Mt of rock, on twenty sites of one magnetite field. */
    public static final int LARGE_MAGNETITE = 110;

    public Mining() {
        super("Mining", "Iron Mining", BuildingType.MINING, Resource.IRON);
        makes(Good.IRON);
        // ...on diesel, to dig and haul (0.7.120, batch I3): 3 litres a tonne.
        uses(Good.DIESEL);
        blurb("Lifts iron ore off the ground and ships it abroad, or to the works "
                + "that will use it. The deposit is the limit, and the city has to "
                + "buy the ground it is under.");
    }

    /** The iron the ground gives, with the month's tonnes kept for the land office (LandManager.extractIron()). */
    @Override
    protected double dig(LandManager land, double asked) {
        return land.extractIron(asked);
    }

    /** ...of one type (0.7.124): its ground's, the month's tonnes of ground kept for the land office, added up across the types. */
    @Override
    protected double dig(LandManager land, Deposit.FieldType type, double fieldTonnes, boolean more) {
        return land.extractIron(type, fieldTonnes, more);
    }
}

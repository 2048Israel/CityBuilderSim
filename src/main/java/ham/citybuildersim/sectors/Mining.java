package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;

/**
 * Iron mines - Iron Mining since 0.7.116, under the key it was saved by,
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
 * works exist. Since 0.7.116 (batch I3) nothing in the city uses ore - the
 * arc furnaces melt SCRAP - until the integrated route's pellet plants and
 * blast furnaces (batch I4). LandManager decides how much of what was asked
 * for is actually there, and nothing once the deposit is out.
 */
public final class Mining extends ExtractionSector {

    /** The Iron Mine's permanent id in buildings.json (0.7.116, batch I3; spec-materials 4): what the harnesses, the test player and the build advice find it by - a name is a label, and the chains rename buildings. */
    public static final int IRON_MINE = 14;

    public Mining() {
        super("Mining", "Iron Mining", BuildingType.MINING, Resource.IRON);
        makes(Good.IRON);
        // ...on diesel, to dig and haul (0.7.116, batch I3): 3 litres a tonne.
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
}

package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;

/**
 * Iron mines. Its own sector because the ore has a price.
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
 * what the mills will not take at the local price leaves at the export
 * price the same month, which is why a mine is worth building before the
 * mills exist. LandManager decides how much of what was asked for is
 * actually there, and nothing once the deposit is out.
 */
public final class Mining extends ExtractionSector {

    public Mining() {
        super("Mining", "Mining", BuildingType.MINING, Resource.IRON);
        makes(Good.IRON);
        blurb("Lifts ore off the ground and sells it to the mills, or ships it abroad "
                + "when the mills do not want it. The deposit is the limit, and the "
                + "city has to buy the ground it is under.");
    }

    /** The iron the ground gives, with the month's tonnes kept for the land office (LandManager.extractIron()). */
    @Override
    protected double dig(LandManager land, double asked) {
        return land.extractIron(asked);
    }
}

package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

/**
 * The mills and the plants that feed the shops.
 *
 * KEYED "Industry", which is what every save and every screen has called it,
 * and it makes FOOD from both its buildings - the Textile Mill included.
 * Jerus, asked whether cloth should stop being bread: "keep it all as food
 * for now." When the households' basket grows a good, the mill gets one.
 *
 * Nothing overridden. The generic template IS IndustrialHandler's rules,
 * lifted: it plans output against the shops' demand rather than flooding
 * its own shed, withholds below marginal cost, dumps above the shed's line,
 * exports spare nameplate when the world's price clears the power it costs,
 * and reports what it wrote off when a warehouse was demolished. See
 * Sector.produceStock() and offer().
 */
public final class FoodIndustry extends Sector {

    public FoodIndustry() {
        super("Industry", "Industry", BuildingType.INDUSTRIAL);
        makes(Good.FOOD);
        blurb("Makes the food the shops sell. Idles rather than flood its own "
                + "warehouse, ships spare capacity abroad, and will not sell at home "
                + "below what a unit costs to move.");
    }

    /** The spare-capacity rule measures the mills against what the shops can serve, as it always has. */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null) return super.retirementDemandAndCapacity(game);
        double demand = Math.min(buildings.getTotalStoreCoverage(),
                game.getPopulationManager().getPopulation());
        return new double[] { demand, getCapacity(Good.FOOD) };
    }
}

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
 * IT BUYS ITS RAW MATERIAL NOW (2026-09-13), which it never did before, and
 * that is the whole of what changed here. For the life of this project the two
 * plants made food OUT OF NOTHING: no input line, no supplier, nothing on the
 * cost side but wages and the lights. It is why they ran a 72-77% operating
 * margin against every other plant's 28-31%, and why the Food Processing Plant
 * returned 6.2% of its build cost a month - the most profitable building in the
 * game, on a raw material that was free because there was none.
 *
 * Now there is one. Crops come from the tenth sector's fields if the city has
 * any and from the world if it has not, and the gap between those two prices is
 * what a farm next door is worth to a mill - the same sentence as a mine next
 * door to a mill, and a mill next door to a fabricator, one chain over. See
 * sectors.Agriculture and claude/farms.md.
 *
 * Everything else is the generic template, which IS IndustrialHandler's rules
 * lifted: it plans output against the shops' demand rather than flooding its
 * own shed, withholds below marginal cost, dumps above the shed's line, exports
 * spare nameplate when the world's price clears the power it costs, and reports
 * what it wrote off when a warehouse was demolished. See Sector.produceStock()
 * and offer().
 */
public final class FoodIndustry extends Sector {

    public FoodIndustry() {
        super("Industry", "Industry", BuildingType.INDUSTRIAL);
        makes(Good.FOOD);
        uses(Good.CROPS);
        blurb("Turns crops into the food the shops sell - buying them from the "
                + "city's own fields when there are any and from the world when "
                + "there are not. Idles rather than flood its own warehouse, ships "
                + "spare capacity abroad, and will not sell at home below what a "
                + "unit costs to move.");
    }

    /** Tonnes of crops the mills want this month, at the rate they are running. */
    public double getCropDemand() {
        return getInputAtCapacity(Good.CROPS) * getOperatingRate();
    }

    /** What a tonne is costing them - the number that decides whether milling pays here. */
    public double getCropPrice() {
        return markets == null ? 0 : markets.get(Good.CROPS).getLocalPrice();
    }

    /**
     * Where the crop price sits between a farm's export floor and the world's
     * delivered ceiling: 0 is fields with nobody else to sell to, 1 is a city
     * with no fields at all.
     */
    public double getCropPosition() {
        return markets == null ? 0 : markets.get(Good.CROPS).getPriceIndex();
    }

    /** The crop bill as a share of what the mills sold. About four tenths is right. */
    public double cropShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().inputs / revenue : 0;
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

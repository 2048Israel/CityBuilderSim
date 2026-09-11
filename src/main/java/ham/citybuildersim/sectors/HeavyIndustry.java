package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

/**
 * The mills. Buy iron - local ore first, imported scrap for the rest - and
 * sell steel abroad, because nothing in the city buys steel.
 *
 * Nothing here about the books, the ore market, the scrap fallback or the
 * export: IRON is importable so the shortfall comes from the world at the
 * scrap price, STEEL is exportable and has no local buyer so every tonne
 * leaves at the export price. The one thing a mill knows that a factory
 * does not is when to be built: only into ore that exists.
 *
 * Exists mainly so the city has somewhere to put people - see the old
 * HeavyIndustryHandler's note, which still holds: the return on a steel
 * mill is the city that grows around it.
 */
public final class HeavyIndustry extends Sector {

    public HeavyIndustry() {
        super("Heavy Industry", "Heavy Industry", BuildingType.HEAVY_INDUSTRY);
        makes(Good.STEEL);
        uses(Good.IRON);
        blurb("Smelts iron into steel and ships every tonne abroad. Buys the city's "
                + "ore when there is any and imports scrap when there is not; the "
                + "gap between the two is what a mine next door is worth.");
    }

    /** Tonnes of iron the mills want this month, at the rate they are running. */
    public double getOreDemand() {
        return getInputAtCapacity(Good.IRON) * getOperatingRate();
    }

    /** What a tonne of imported scrap costs - the mills' fallback, and the ore market's ceiling. */
    public double getScrapPricePerTonne() {
        return markets == null ? 0 : markets.get(Good.IRON).importPrice();
    }

    /** What a tonne of steel fetches over what the iron in it cost. */
    public double getConversionMargin() {
        if (markets == null) return 0;
        double steel = markets.get(Good.STEEL).exportPrice();
        // Tonnes of iron a tonne of steel takes: 1.1 on every mill in the catalogue.
        double perTonne = getCapacity(Good.STEEL) > 0
                ? getInputAtCapacity(Good.IRON) / getCapacity(Good.STEEL) : 1.1;
        return steel - perTonne * markets.get(Good.IRON).getLocalPrice();
    }

    /**
     * Whether to build another mill. The signal is the ore: a mill can
     * always fall back on imported scrap, so nothing stops it being built,
     * but on scrap alone it earns almost nothing, and a sector that expands
     * on a business case it does not have is the borrowing spiral the
     * investment engine was written to end. Requiring spare local ore also
     * gives the two sectors an order - mines first, mills after - which is
     * the cluster the ore market was designed to reward.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        Mining mines = game.getSectors().mining();
        double spareOre = mines.getPotentialOutput() - getOreDemand();
        if (spareOre <= 0) return BusinessInvestment.Decision.no(sector, "no spare local ore to smelt");

        BuildingsTemplate best = null;
        double bestScore = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.makes(Good.STEEL) <= 0 || t.uses(Good.IRON) <= 0) continue;
            // No point building a mill twice the size of the ore available.
            if (t.uses(Good.IRON) > spareOre) continue;
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = estimatedMonthlyProfit(t, plans) / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            return BusinessInvestment.Decision.no(sector,
                    String.format("%,.0f t of spare ore, nothing worth smelting it", spareOre));
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%,.0f tonnes of local ore going begging", spareOre), true);
    }

    /** A price-taking exporter always sells what it makes: it shrinks on distress only. */
    @Override
    public double[] retirementDemandAndCapacity(Game game) { return null; }
}

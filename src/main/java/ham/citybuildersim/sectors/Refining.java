package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Sector;

/**
 * The refinery. THE SEVENTEENTH SECTOR (0.7.62, batch K; the project's
 * spec-land.md 2.7).
 *
 * HEAVY INDUSTRY'S SHAPE, ON OIL. A refinery buys crude - the city's wells'
 * first, the world's for the rest (CRUDE is importable, so the shortfall is
 * the template's) - and makes FUEL, a thousand litres from a tonne, into its
 * tanks. The city's drivers (Motoring, at 6d) and its railway (Rail.haul())
 * draw on those tanks before they import, so the refiners' shelf replaces
 * the fuel the city used to buy abroad, and its price is the market's.
 *
 * WHEN TO BUILD ONE is the one thing it knows (plan()): only for a whole
 * plant's worth of the city's own fuel not yet covered, or of crude its
 * wells lift that no refinery takes - HeavyIndustry's rule - and then while
 * the fuel it would make clears above what it costs to make at nameplate:
 * the investors' own estimate (BusinessInvestment.estimatedMakerProfit()),
 * which values what the city will take at the local price and the rest at
 * the export price, with its crude at the local price for the wells' spare
 * and at what an import costs landed and hauled for the rest
 * (estimatedMonthlyProfit()). The interest test
 * (BusinessInvestment.servicesItsOwnDebt()) holds it as it holds any plant.
 */
public final class Refining extends Sector {

    /** Litres of fuel a tonne of crude makes: 86% of a barrel's 1,165 litres as transport fuels, at 7.33 barrels a tonne, rounded to the refinery's own figures (8,300 t into 8,300,000 L). */
    public static final double LITRES_PER_TONNE = 1000;

    public Refining() {
        super("Refining", "Refining", BuildingType.HEAVY_INDUSTRY);
        makes(Good.FUEL);
        uses(Good.CRUDE);
        blurb("Refines crude into the fuel the city's drivers and railway burn. Buys the "
                + "city's crude when its wells lift any and imports the rest; sells the "
                + "fuel at home first and ships what the city does not want.");
    }

    /** Tonnes of crude the refineries want this month, at the rate they are running. */
    public double getCrudeDemand() {
        return getInputAtCapacity(Good.CRUDE) * getOperatingRate();
    }

    /**
     * Whether to build another refinery: the best template by what the
     * investors' estimate says it would clear a month over its cost, while
     * that clears at all (spec-land 2.7) - and only for one of the city's own
     * two reasons, HeavyIndustry's shape, each a whole plant's worth: the
     * city's own fuel not yet covered by as much as the plant makes (the
     * drivers and the railway draw that much more than the refineries
     * standing and on site make), or its wells lifting as much crude as the
     * plant takes that no refinery is taking - as a mill is built only into
     * ore the mines have spare, all of its input.
     *
     * THE GATE IS THE MEASUREMENT. Without it the playtest's city stood 120
     * refineries at month 4,002 (scratch-k pt1): a tonne of crude bought at
     * .60 and sold abroad as a thousand litres at .0007 leaves $100 a tonne,
     * and a refinery's crew and power took less than that, so every refinery
     * the interest test passed was one more exporter on imported crude - the
     * unbounded export market at a fixed floor Good's header names (the van
     * plants, the locomotive works), here at a hundred posts a plant. With
     * the gate at any fuel not covered (pt2) the first refinery stood at
     * month 438 in a town of 800 people, exporting all but a few thousand of
     * its 8.3M litres a month, and a 300-house fixture town built one.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        // The city's own fuel, not yet covered, and its own crude, not yet taken.
        double room = plans.forecast(this, markets.get(Good.FUEL)) - getCapacity(Good.FUEL) - getPipeline(Good.FUEL);
        double spare = spareCrude(null);
        if (!(room > 0) && !(spare > 0)) {
            return BusinessInvestment.Decision.no(sector,
                    "the city's fuel is covered already, and its wells have no crude to spare");
        }
        boolean anyWhole = false;

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.makes(Good.FUEL) <= 0) continue;
            // A whole plant's worth of the city's own fuel, or of the wells' spare crude (HeavyIndustry's rule).
            if (t.makes(Good.FUEL) > room && t.uses(Good.CRUDE) > spare) continue;
            anyWhole = true;
            // ...and one the city could staff (0.7.18; see Sector.staffing()).
            Staffing staffing = staffing(t);
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = estimatedMonthlyProfit(t, plans) / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (staffingHold != null) {
                return BusinessInvestment.Decision.no(sector, staffingHold.why(staffingHoldName));
            }
            if (!anyWhole) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "%,.0f L of fuel and %,.0f t of crude to spare: none for another refinery",
                        Math.max(0, room), spare));
            }
            return BusinessInvestment.Decision.no(sector, "fuel at today's price would not clear a refinery's cost");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        GoodsMarket fuel = markets.get(Good.FUEL);
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("fuel at %s a litre clears a refinery's cost", Formats.INSTANCE.amount(fuel.getLocalPrice())), true);
    }

    /**
     * What one more refinery would clear a month: the investors' estimate
     * (BusinessInvestment.estimatedMakerProfit()), with its crude at what it
     * would actually cost - the wells' spare crude at the local price and the
     * rest at what an import costs landed and hauled (netImportPrice()).
     *
     * HEAVY INDUSTRY'S SPARE-ORE RULE AS A PRICE RATHER THAN A GATE. A mill is
     * not built past the ore the mines have spare; a refinery can import its
     * crude, so it is not held back - but the estimate read crude at the local
     * price, which with nothing traded is the middle of the band (0.55, where
     * the city would pay 0.60), and with the wells' crude all spoken for is the
     * price that crude clears at, not what this plant's 8,300 t more would
     * fetch. Measured in a 1,500-person town (scratch-k KProbe): the
     * template's estimate ordered a refinery for 2,455 litres a month of the
     * city's own fuel, an exporter on crude it would have had to import.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        double estimate = plans.estimatedMakerProfit(this, t);
        if (t == null || markets == null) return estimate;
        double need = t.uses(Good.CRUDE);
        if (!(need > 0)) return estimate;
        double imported = Math.max(0, need - spareCrude(t));
        GoodsMarket crude = markets.get(Good.CRUDE);
        double dearer = crude.netImportPrice() - crude.getLocalPrice();
        if (!(imported > 0) || !(dearer > 0) || !Double.isFinite(dearer)) return estimate;
        return estimate - imported * dearer * BusinessInvestment.operatingRateOf(getOperatingRate());
    }

    /**
     * The crude the city's wells could lift this month that no refinery
     * standing or on site will take, in tonnes (never below nothing): those on
     * site at a template's crude a litre - `t`'s, or the catalogue's first
     * refinery's when null.
     */
    public double spareCrude(BuildingsTemplate t) {
        Oil wells = game == null ? null : game.getSectors().oil();
        if (wells == null) return 0;
        BuildingsTemplate per = t;
        if (per == null && buildings != null) {
            for (BuildingsTemplate c : buildings.getTemplatesBySector(key())) { if (c.makes(Good.FUEL) > 0) { per = c; break; } }
        }
        double onSite = per != null && per.makes(Good.FUEL) > 0
                ? getPipeline(Good.FUEL) * per.uses(Good.CRUDE) / per.makes(Good.FUEL) : 0;
        return Math.max(0, wells.getPotentialOutput() - getCrudeDemand() - onSite);
    }

    /** A price-taking exporter always sells what it makes: it shrinks on distress only (HeavyIndustry's rule). */
    @Override
    public double[] retirementDemandAndCapacity(Game game) { return null; }
}

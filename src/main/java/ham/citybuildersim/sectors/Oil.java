package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Sector;

import java.util.List;

/**
 * Oil wells. THE SIXTEENTH SECTOR (0.7.62, batch K; the project's
 * spec-land.md 2.7).
 *
 * MINING'S SHAPE, ON THE OTHER RESOURCE. The world laid oil in fields from
 * the start (Resource.OIL, batch J1a) and the land office has sold the
 * ground over it since J1b; this is what lifts it. A well stands on an
 * unworked oil site the city owns (Game.hasDepositFor(), by its good), lifts
 * 415 t of crude a month - a hundred barrels a day - and ships what it
 * lifts: CRUDE is a flow good, so what the refiners do not take at the
 * local price leaves at the export price the same month, which is why a
 * well is worth drilling before there is a refinery. The ground limits it
 * through the template's hook (groundLimit()), and the wells retire when
 * the oil runs out, as the mines do when the ore does.
 *
 * Everything else - the books, the credit, the market, the payroll, the
 * export - is the template's.
 */
public final class Oil extends Sector {

    public Oil() {
        super("Oil", "Oil", BuildingType.MINING);
        makes(Good.CRUDE);
        blurb("Lifts crude from the city's oil fields and sells it to the refinery, or ships "
                + "it abroad when the refinery does not want it. The oil the city owns is the "
                + "limit, and the city has to buy the ground it is under.");
    }

    /** The ground, not the well, decides what comes up. */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (game == null || g != Good.CRUDE) return asked;
        LandManager land = game.getLandManager();
        return land == null ? asked : land.extractOil(asked);
    }

    /** What the wells could lift this month if the ground allowed it. */
    public double getPotentialOutput() {
        return getCapacity(Good.CRUDE) * getOperatingRate();
    }

    /**
     * Whether to drill another well: one a month while an owned oil site is
     * unworked and oil is left in the ground - Mining's rule, on oil
     * (spec-land 2.7). The deposit word says "deposit", which the Sectors
     * screen reads as ORE (BuildCard.wordKind()).
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        LandManager land = game.getLandManager();
        int unworked = land.getOilSites() - game.wellsCommitted();
        double reserve = land.getOilReserveTonnes();

        if (unworked <= 0) return BusinessInvestment.Decision.no(sector, "no oil deposit to drill");
        if (reserve <= 0)  return BusinessInvestment.Decision.no(sector, "every oil deposit is worked out");

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.makes(Good.CRUDE) <= 0) continue;
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
            return BusinessInvestment.Decision.no(sector, staffingHold != null
                    ? staffingHold.why(staffingHoldName) : "nothing worth sinking");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%d oil deposit site(s) undrilled, %,.0fk tonnes in the ground",
                        unworked, reserve / 1000), true);
    }

    /**
     * A price-taking exporter sells what it lifts and shrinks on distress -
     * while there is oil. A well over a worked-out field lifts nothing, and is
     * spare by any measure: Mining's rule, for Mining's reason.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null) return null;
        double capacity = getCapacity(Good.CRUDE);
        if (capacity <= 0) return null;
        return game.getLandManager().getOilReserveTonnes() > 0
                ? null
                : new double[] { 0, capacity };
    }

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;
        LandManager land = game.getLandManager();
        lines.add(Line.head("What is under the ground"));
        lines.add(Line.of("Oil sites owned", f.count(land.getOilSites())));
        lines.add(Line.of("Being worked", f.count(game.wellsCommitted()),
                game.wellsCommitted() < land.getOilSites() ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Crude in the ground", f.count(land.getOilReserveTonnes()) + " t"));
        if (game.wellsCommitted() < land.getOilSites() && land.getOilReserveTonnes() > 0) {
            lines.add(Line.note("There is oil under this city that no well is lifting."));
        }
        return lines;
    }
}

package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;
import ham.citybuildersim.Sector;

import java.util.List;

/**
 * A sector that digs or cuts one resource out of the ground the city owns:
 * the template every extraction industry of the materials chains extends
 * (0.7.112, batch I2; runs/spec-materials.md 2.1 and 2.6b).
 *
 * WHY. The materials chains bring four more industries that lift their
 * good off the city's fields - Coal Mining, Quarrying, Copper Mining and
 * Forestry - and each is Mining's shape: the ground, not the plant, is the
 * limit; a mine stands on a site of a field the city bought; one order at a
 * time, the best return on its cost; and a mine over a worked-out field is
 * spare by any measure. Until 0.7.112 that shape was Mining's own code, for
 * iron. Written here once, a chain's extraction sector says only which
 * resource it lifts and what it makes, and stays a declaration.
 *
 * WHAT IT HOLDS, from Mining as it was to 0.7.111, to the bit:
 *   - THE GROUND (groundLimit()): what the month lifts of its resource's
 *     good is what the city's ground still holds of it (dig(): LandManager
 *     .extract()), nothing once it is out;
 *   - THE PLAN (plan()): one order in flight; a free site of its resource
 *     (the sites owned less those its buildings stand on, Game.committedOn())
 *     and the resource still in the ground; of the buildings that make its
 *     good and the city could staff, the best estimatedMonthlyProfit() over
 *     its cost; then ground for it. To 0.7.115 the money gate was
 *     Game.consider()'s alone (on the order's whole cost since 0.7.113,
 *     batch I2b); since 0.7.116 (batch I3; spec-materials 2.6b) the plan
 *     asks it first, through SpreadPlanner's MONEY gate (planner(): on the
 *     whole cost, decision A), so a mine that would not carry its interest
 *     is not the one ordered and the sector says why - consider() asks the
 *     same of the order after it;
 *   - THE ESTIMATE (estimatedMonthlyProfit()): the maker's (Sector's), less
 *     what a mine buys abroad that is not a good, its consumables at the
 *     exchange factor (SpreadPlanner.ownCosts(), since 0.7.116: the Iron
 *     Mine's explosives and wear parts) - what the plan, consider() and the
 *     build card read;
 *   - THE RETIREMENT (retirementDemandAndCapacity()): a price-taking
 *     exporter shrinks on distress only, while there is ore; past it, all
 *     its plant is spare;
 *   - THE PAGE (ownLines()): the deposits found and the ones being worked.
 *
 * WHAT IS LEFT FOR THE CHAINS (spec-materials 2.4, 2.6b): the dig by field
 * type and a building's site (BuildingsTemplate.Site: type, sites, dig) -
 * LandManager.fits() gating the plan and the ground limit split by each
 * type's capacity (I5) - and Forestry's crews on the cuttable forest (I9).
 * Nothing here reads a site's type or dig yet: Mining's one building stands
 * on ANY, one site, a tonne of field a tonne lifted.
 */
public abstract class ExtractionSector extends Sector {

    /** The resource whose ground it lifts. */
    private final Resource resource;

    /** Its spread planner, for its money gate (0.7.116, batch I3; spec-materials 2.6b): ON_ITS_WHOLE_COST unless a check or a probe sets another. */
    private final SpreadPlanner planner = new SpreadPlanner();

    protected ExtractionSector(String key, String label, BuildingType group, Resource resource) {
        super(key, label, group);
        this.resource = resource;
    }

    /** The resource whose ground it lifts. */
    public final Resource resource() { return resource; }

    /** The good its ground limits: its resource's (Resource.good()). */
    public final Good groundGood() { return resource.good(); }

    /* ----- the ground ----- */

    /** The ground, not the mine, decides what comes up. */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (game == null || g != groundGood()) return asked;
        LandManager land = game.getLandManager();
        return land == null ? asked : dig(land, asked);
    }

    /** What the ground gives of `asked` this month: its resource's remainder taken out (LandManager.extract()). Mining keeps the iron's own tally. */
    protected double dig(LandManager land, double asked) {
        return land.extract(resource, asked);
    }

    /** Its spread planner (0.7.116): its money gate - a check or a probe sets the counterfactual on it. */
    public SpreadPlanner planner() { return planner; }

    /* ----- the estimate ----- */

    /** What its mines' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. */
    public double exchangeFactor() {
        return markets == null ? 1 : markets.getExchangeRate();
    }

    /**
     * What one more of its mines would clear a month (0.7.116, batch I3): the
     * maker's estimate (Sector's - its good at home and abroad, its inputs,
     * its wages, power, water, repairs and tax, at the rate its plant runs)
     * less its consumables at that rate and the exchange factor, and plus
     * any power it makes, at the tariff (SpreadPlanner.ownCosts(): the
     * Iron Mine's explosives and wear parts, D$199.8k a month at nameplate,
     * and no power). The figure the plan, Game.consider() and the build card
     * read.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        double maker = super.estimatedMonthlyProfit(t, plans);
        if (t == null) return maker;
        return maker - SpreadPlanner.ownCosts(t, BusinessInvestment.operatingRateOf(getOperatingRate()),
                exchangeFactor(), pricePerWatt);
    }

    /* ----- the dig ----- */

    /** What its buildings could lift this month if the ground allowed it. */
    public double getPotentialOutput() {
        return getCapacity(groundGood()) * getOperatingRate();
    }

    /** The sites of its resource the city owns: how many of its buildings can stand. */
    public int sitesOwned() {
        return game == null ? 0 : game.getLandManager().getSites(resource);
    }

    /** ...and those its buildings stand on, standing, on site or ordered (Game.committedOn()). */
    public int sitesWorked() {
        return game == null ? 0 : game.committedOn(resource);
    }

    /* ----- the plan ----- */

    /**
     * Whether to open another mine.
     *
     * Unlike every other sector, this one can be refused by geology. A mine
     * needs ground with ore under it, and that is the city's to buy - so the
     * private sector expanding into mining depends on the player having gone
     * and bought a deposit. That is the intended coupling: land is the one
     * input only the city controls, and now it gates an industry.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        int ordersInFlight = buildings.getUnderConstructionBySector(sector);
        if (ordersInFlight >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        LandManager land = game.getLandManager();
        int unminedDeposits = land.getSites(resource) - game.committedOn(resource);
        double reserveTonnes = land.getRemaining(resource);

        if (unminedDeposits <= 0) return BusinessInvestment.Decision.no(sector, "no deposit to dig");
        if (reserveTonnes <= 0)   return BusinessInvestment.Decision.no(sector, "the ore is worked out");

        Good good = groundGood();
        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        BuildingsTemplate moneyHold = null;
        double moneyEarns = 0, moneyCost = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.makes(good) <= 0) continue;
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
            double earns = estimatedMonthlyProfit(t, plans);
            // ...and one that would carry the interest on its whole cost (0.7.116, batch I3; spec-materials 2.6b).
            if (!planner.passesMoney(this, plans, game, earns, cost)) {
                if (moneyHold == null || earns / cost > moneyEarns / moneyCost) {
                    moneyHold = t;
                    moneyEarns = earns;
                    moneyCost = cost;
                }
                continue;
            }
            double score = earns / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (moneyHold != null) {
                return BusinessInvestment.Decision.no(sector, ProcessingSector.a(moneyHold.getName()) + " "
                        + planner.city(this, plans, game).money(moneyEarns, moneyCost));
            }
            return BusinessInvestment.Decision.no(sector, staffingHold != null
                    ? staffingHold.why(staffingHoldName) : "nothing worth sinking");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%d deposit(s) unworked, %,.0fk tonnes in the ground",
                        unminedDeposits, reserveTonnes / 1000), true);
    }

    /* ----- the retirement ----- */

    /**
     * A price-taking exporter always sells what it lifts: it shrinks on
     * distress only - WHILE THERE IS ORE. A mine over a worked-out deposit
     * lifts nothing, and plant that lifts nothing is spare by any measure.
     *
     * Measured before this (2026-09-11), on the playtest's first city: the
     * ore ran out, seven hundred miners stayed on the payroll of fifty-two
     * dead mines, the sector borrowed fourteen million a month for three
     * years to pay them - the shortfall desk lends against the mines' book
     * value, which was seven hundred million of holes - and was written
     * down by four hundred million when the book was finally called. The
     * distress rule never fired because the loans kept the till positive.
     * Fifty-two mines and eleven write-downs in the run before the sector
     * template, too; the bank was just fat enough then to eat it.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        if (game == null) return null;
        double capacity = getCapacity(groundGood());
        if (capacity <= 0) return null;
        return game.getLandManager().getRemaining(resource) > 0
                ? null
                : new double[] { 0, capacity };
    }

    /* ----- the page ----- */

    @Override
    public List<Line> ownLines(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;
        int owned = sitesOwned(), worked = sitesWorked();
        lines.add(Line.head("What is under the ground"));
        lines.add(Line.of("Deposits found", f.count(owned)));
        lines.add(Line.of("Being worked", f.count(worked),
                worked < owned ? Line.Tone.WARN : Line.Tone.GOOD));
        if (worked < owned) {
            lines.add(Line.note("There is ore under this city that nothing is digging."));
        }
        return lines;
    }
}

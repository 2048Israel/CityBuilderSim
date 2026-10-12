package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Deposit;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.Resource;
import ham.citybuildersim.Sector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A sector that digs or cuts one resource out of the ground the city owns:
 * the template every extraction industry of the materials chains extends
 * (0.7.112, batch I2; docs/studies/spec-materials.md 2.1 and 2.6b).
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
 *     its cost; then ground for it. To 0.7.119 the money gate was
 *     Game.consider()'s alone (on the order's whole cost since 0.7.113,
 *     batch I2b); since 0.7.120 (batch I3; spec-materials 2.6b) the plan
 *     asks it first, through SpreadPlanner's MONEY gate (planner(): on the
 *     whole cost, decision A), so a mine that would not carry its interest
 *     is not the one ordered and the sector says why - consider() asks the
 *     same of the order after it;
 *   - THE ESTIMATE (estimatedMonthlyProfit()): the maker's (Sector's), less
 *     what a mine buys abroad that is not a good, its consumables at the
 *     exchange factor (SpreadPlanner.ownCosts(), since 0.7.120: the Iron
 *     Mine's explosives and wear parts) - what the plan, consider() and the
 *     build card read;
 *   - THE RETIREMENT (retirementDemandAndCapacity()): a price-taking
 *     exporter shrinks on distress only, while there is ore; past it, all
 *     its plant is spare;
 *   - THE PAGE (ownLines()): the deposits found and the ones being worked.
 *
 * BY TYPE AND SIZE SINCE 0.7.124 (batch I5; spec-materials 2.4, 2.6b; the
 * study's 1.3: "the sector chooses the mine type that fits"). A building's
 * site (BuildingsTemplate.Site) says the type of field it stands on, the
 * sites it takes and the tonnes of field a unit it makes digs, and each of
 * the four rules above reads it:
 *   - THE GROUND by type: the month's ask split by each type's share of the
 *     sector's nameplate, each part dug from its own type's ground at its
 *     dig (a magnetite works three tonnes of rock a tonne of concentrate),
 *     LandManager.extract(r, type, t) - so a hematite mine never lifts a
 *     magnetite tonne, and W holds by type;
 *   - THE PLAN only among the mines that fit: its type's ore left, and room
 *     for its sites on ONE field of its type beside the works standing
 *     (Game.claimsOn(), LandManager.fits()) - a Large Open-Pit Mine twenty
 *     free sites of one hematite field - the best earnings on the whole cost
 *     winning; the size is the free sites' (Jerus's Q19);
 *   - THE ESTIMATE and the plan's cost with the mine's lorries (★, as I4
 *     weighs every works: ProcessingSector.fleetRunning(), fleetCapital());
 *   - THE RETIREMENT by type: a mine whose type is worked out is spare while
 *     the other type still has ore, and only it may be sold.
 *
 * A STOCKABLE GOOD'S LIFT (0.7.126, batch I6): groundLimit() reaches only
 * the flow goods. A good the sector stocks - Coal Mining's two coals, Copper
 * Mining's cathode (0.7.133) - is made at its ground's limit through
 * Sector.madeAtRate(), the hook getPlannedOutput(), getExportBoundOutput()
 * and produceStock() start from: the sector's own lift() holds each kind's
 * nameplate at the rate to its type's ground, and its endOfMonth() takes the
 * same dig out. WHICH BUILDINGS LIFT (0.7.131, batch I7): lifts(), the
 * plan's and the retirement's test of a building - one that makes its
 * ground's good, and Quarrying's blocks too. Coal Mining (0.7.126),
 * Quarrying (0.7.131) and Copper Mining (0.7.133) extend it; Forestry's
 * crews on the cuttable forest are I9's.
 */
public abstract class ExtractionSector extends Sector {

    /** The resource whose ground it lifts. */
    private final Resource resource;

    /** Its spread planner, for its money gate (0.7.120, batch I3; spec-materials 2.6b): ON_ITS_WHOLE_COST unless a check or a probe sets another. */
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

    /**
     * The ground, not the mine, decides what comes up - since 0.7.124 by
     * type (BY TYPE AND SIZE, above): the month's ask split by each kind of
     * dig's share of the nameplate (digs()), each part's field tonnes - the
     * ask times its dig - taken out of its own type's ground, and what came
     * up the field tonnes over the dig. One kind of dig takes the whole ask,
     * to the bit; a sector with none standing digs its resource as it did to
     * 0.7.123 (dig(land, asked)), so a month with no mine moves nothing.
     */
    @Override
    protected double groundLimit(Good g, double asked) {
        if (game == null || g != groundGood()) return asked;
        LandManager land = game.getLandManager();
        if (land == null) return asked;
        List<Dig> digs = digs(g);
        if (digs.isEmpty()) return dig(land, asked);
        double nameplate = 0;
        for (Dig d : digs) nameplate += d.capacity;
        double came = 0;
        for (int i = 0; i < digs.size(); i++) {
            Dig d = digs.get(i);
            double part = digs.size() == 1 ? asked : asked * (d.capacity / nameplate);
            if (!(d.dig > 0)) {
                came += part;
                continue;
            }
            came += dig(land, d.type, part * d.dig, i > 0) / d.dig;
        }
        return came;
    }

    /** What the ground gives of `asked` this month: its resource's remainder taken out (LandManager.extract()). Mining keeps the iron's own tally. */
    protected double dig(LandManager land, double asked) {
        return land.extract(resource, asked);
    }

    /**
     * ...of one type (0.7.124): `fieldTonnes` of its type's ground (null for
     * ANY, the walk) taken out (LandManager.extract(r, type, t)), what was
     * there; `more` when another type was dug before it this month. Mining
     * keeps the iron's own tally.
     */
    protected double dig(LandManager land, Deposit.FieldType type, double fieldTonnes, boolean more) {
        return land.extract(resource, type, fieldTonnes);
    }

    /** One kind of dig its buildings make: the type of field they stand on (null for ANY), the tonnes of field a unit takes, and their nameplate of the good. */
    record Dig(Deposit.FieldType type, double dig, double capacity) { }

    /**
     * Its buildings standing that make `g`, by the type of field they stand
     * on and their dig (a building with no site of its resource on ANY at a
     * tonne a tonne), each kind's nameplate, in the catalogue's order.
     */
    List<Dig> digs(Good g) {
        Map<String, Dig> kinds = new LinkedHashMap<>();
        if (buildings == null) return new ArrayList<>();
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            double units = t.makes(g) * buildings.getQuantity(t.getId());
            if (!(units > 0)) continue;
            BuildingsTemplate.Site site = t.site();
            boolean on = site != null && site.resource() == resource;
            Deposit.FieldType type = on ? site.type() : null;
            double dig = on ? site.dig() : 1;
            kinds.merge(type + "/" + dig, new Dig(type, dig, units), (a, b) -> new Dig(a.type, a.dig, a.capacity + b.capacity));
        }
        return new ArrayList<>(kinds.values());
    }

    /** Its spread planner (0.7.120): its money gate - a check or a probe sets the counterfactual on it. */
    public SpreadPlanner planner() { return planner; }

    /* ----- the estimate ----- */

    /** What its mines' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. */
    public double exchangeFactor() {
        return markets == null ? 1 : markets.getExchangeRate();
    }

    /**
     * What one more of its mines would clear a month (0.7.120, batch I3): the
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
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        return maker - SpreadPlanner.ownCosts(t, rate, exchangeFactor(), pricePerWatt) - fleetRunning(t, rate);
    }

    /*
     * ...AND ITS LORRIES (0.7.124, batch I5; ★ as I4 weighs every works,
     * ProcessingSector's AND THE LORRIES A WORKS NEEDS). A mine ships every
     * tonne it lifts by road, and its sector keeps the vans for them (Sector,
     * THE FLEET) and burns their diesel, which the maker's estimate leaves
     * out: an Iron Mine's 20,000 t keep 167 vans, about D$11M at parity, a
     * quarter of the mine; a Large Open-Pit Mine's 500,000 t about 4,200,
     * D$285M. So one more mine is weighed as a works is: the vans' wear and
     * diesel a month at the rate in what it earns (fleetRunning(), here and
     * so in Game.consider() and the build card), and the vans in what the
     * plan weighs it on (fleetCapital()). Not in SpreadPlanner.caseEarns():
     * the study's cases are the study's.
     */

    /** What one of `t`'s lorries cost a month at `rate`: ProcessingSector.fleetRunning() at what a van and a litre cost here; none with no markets. */
    public double fleetRunning(BuildingsTemplate t, double rate) {
        return markets == null || t == null ? 0 : ProcessingSector.fleetRunning(t, SpreadPlanner.values(markets), rate);
    }

    /** ...and buying them: ProcessingSector.fleetCapital(); none with no markets. */
    public double fleetCapital(BuildingsTemplate t) {
        return markets == null || t == null ? 0 : ProcessingSector.fleetCapital(t, SpreadPlanner.values(markets));
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
     *
     * AND SINCE 0.7.124 BY THE FIELD IT FITS (BY TYPE AND SIZE, above): a
     * mine is a candidate only where its type has ore left and its sites fit
     * on one field of its type beside the works standing (fits()), and the
     * best earnings on its whole cost - its lorries in both - wins among
     * those; with none that fits, it says which field it lacks.
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

        List<LandManager.Claim> standing = game.claimsOn(resource);
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        BuildingsTemplate moneyHold = null;
        double moneyEarns = 0, moneyCost = 0;
        BuildingsTemplate fieldHold = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (!lifts(t)) continue;
            // ...on a field it fits (0.7.124): its type's ore left, and its sites on one field of its type beside the standing.
            if (!fits(land, t, standing)) {
                if (fieldHold == null) fieldHold = t;
                continue;
            }
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
            // ...its lorries with it (0.7.124, ★): their wear and diesel in what it earns, the vans in what it costs.
            cost += fleetCapital(t);
            double earns = estimatedMonthlyProfit(t, plans);
            // ...and one that would carry the interest on its whole cost (0.7.120, batch I3; spec-materials 2.6b).
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
            if (staffingHold != null) return BusinessInvestment.Decision.no(sector, staffingHold.why(staffingHoldName));
            return BusinessInvestment.Decision.no(sector, fieldHold != null ? noField(land, fieldHold) : "nothing worth sinking");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        BuildingsTemplate.Site site = best.site();
        Deposit.FieldType type = site == null ? null : site.type();
        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%d deposit(s) unworked, %,.0fk tonnes %sin the ground",
                        unminedDeposits, land.getRemaining(resource, type) / 1000,
                        type == null ? "" : "of " + typeWord(type) + " "), true);
    }

    /**
     * Whether a building of its lifts its ground - a mine the plan weighs and
     * the retirement measures: one that makes its ground's good (Resource
     * .good()). Quarrying's Dimension Stone Quarry, whose blocks are not
     * stone's pricing good, says so too (0.7.131, batch I7).
     */
    protected boolean lifts(BuildingsTemplate t) {
        return t != null && t.makes(groundGood()) > 0;
    }

    /** Whether one more of `t` fits the city's ground now (0.7.124): fits() below against the works standing, on site or ordered (Game.claimsOn()) - what the plan asks of each candidate, and the build card may. */
    public boolean fits(BuildingsTemplate t) {
        return game != null && t != null && fits(game.getLandManager(), t, game.claimsOn(resource));
    }

    /**
     * Whether one more of `t` fits the city's ground beside the claims
     * standing (0.7.124): a building that stands on its resource's sites
     * needs ore of its type left (getRemaining(r, type)) and room for its
     * sites on one field of its type (LandManager.fits()); one that stands on
     * none, as a mine did before sites were declared, needs only the ore.
     */
    boolean fits(LandManager land, BuildingsTemplate t, List<LandManager.Claim> standing) {
        BuildingsTemplate.Site site = t.site();
        if (site == null || site.resource() != resource) return land.getRemaining(resource) > 0;
        return land.getRemaining(resource, site.type()) > 0 && land.fits(resource, site.type(), standing, site.sites());
    }

    /** Why the first mine that does not fit cannot stand: its type worked out, or no field of its type with room for its sites. */
    private String noField(LandManager land, BuildingsTemplate t) {
        BuildingsTemplate.Site site = t.site();
        String type = site == null || site.type() == null ? "" : typeWord(site.type()) + " ";
        if (site != null && !(land.getRemaining(resource, site.type()) > 0)) {
            return String.format("no %sore left for %s", type, ProcessingSector.a(t.getName()));
        }
        int sites = site == null ? 1 : site.sites();
        return String.format("no %sfield with %d free site%s for %s", type, sites, sites == 1 ? "" : "s",
                ProcessingSector.a(t.getName()));
    }

    /** A field type in words: "hematite", "magnetite". */
    static String typeWord(Deposit.FieldType type) {
        return type == null ? "" : type.name().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
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
        LandManager land = game.getLandManager();
        if (!(land.getRemaining(resource) > 0)) return new double[] { 0, capacity };
        // ...and BY TYPE since 0.7.124: a mine over its type worked out is as spare, while the other type still has ore.
        double spare = workedOutCapacity(land);
        return spare > 0 ? new double[] { 0, spare } : null;
    }

    /** Its nameplate standing on a type whose ore is worked out (0.7.124): the mines that lift nothing. */
    double workedOutCapacity(LandManager land) {
        double spare = 0;
        if (buildings == null || land == null) return 0;
        Good g = groundGood();
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            int held = buildings.getQuantity(t.getId());
            if (held > 0 && workedOut(land, t)) spare += held * t.makes(g);
        }
        return spare;
    }

    /** Whether a building of its stands on ground with nothing left: its type's ore (ANY or no site: its resource's) worked out. */
    boolean workedOut(LandManager land, BuildingsTemplate t) {
        if (!lifts(t)) return false;
        BuildingsTemplate.Site site = t.site();
        Deposit.FieldType type = site == null || site.resource() != resource ? null : site.type();
        return !(land.getRemaining(resource, type) > 0);
    }

    /**
     * ...and which of its mines may be sold (0.7.124): while any stands over a
     * worked-out type, only those - by the spare-capacity rule above and by
     * the distress rule alike, so a sector in trouble sells the mines that
     * lift nothing before one that still digs; otherwise any, as before.
     */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        LandManager land = game == null ? null : game.getLandManager();
        if (land == null || !(workedOutCapacity(land) > 0)) return true;
        return workedOut(land, t);
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
        // ...by type (0.7.124): each type's sites the city owns, and those its mines take.
        if (resource.hasTypes()) {
            LandManager land = game.getLandManager();
            for (Deposit.FieldType type : resource.typesOf()) {
                int of = land.getSites(resource, type);
                if (of <= 0) continue;
                String word = typeWord(type);
                lines.add(Line.of(Character.toUpperCase(word.charAt(0)) + word.substring(1) + " sites worked",
                        f.count(game.committedOn(resource, type)) + " of " + f.count(of)));
            }
        }
        if (worked < owned) {
            lines.add(Line.note("There is ore under this city that nothing is digging."));
        }
        return lines;
    }
}

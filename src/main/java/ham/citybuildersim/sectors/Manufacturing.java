package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

import java.util.List;

/**
 * What the city makes out of its own steel, and ships.
 *
 * THE NINTH SECTOR (2026-09-13, Jerus's call). The eighth answered the
 * plateau with pure labour - a seat, a wage, a client who is not here - and
 * it works, and it is bounded by exactly one thing: the unskilled wage. A
 * city that succeeds at contact centres bids that wage up until they close.
 * That is the boom-and-bleed the sector was calibrated on and it is supposed
 * to happen. What the city had no answer for was the NEXT rung - work that
 * pays more than a seat and can still be sold to somebody who is not here.
 *
 * The old note in claude/jobs-beyond-the-stalemate.md asked for exactly this
 * and described it exactly right: "an export sector designed to absorb
 * labour, unlike steel which is designed around a deposit. Needs power, land,
 * materials, port." Business Services was built first because it needed
 * nothing but people. This is the one that needed the goods economy to exist.
 *
 * WHAT IT DOES. It buys steel - from the city's mills if there are any, from
 * the world if there are not - and sells two things abroad:
 *
 *   Fabrication Shop      fabricated steel   135 posts, mostly labour and trades
 *   Fabrication Works     fabricated steel   276 posts, the same trade at scale
 *   Machine Works         machinery          173 posts, trades and technicians
 *
 * (and since 0.7.133, batch I8, a third of copper: the Wire & Cable Works
 * draws Copper Refining's rod into wire and cable, 400 posts - THE WIRE &
 * CABLE WORKS below).
 *
 * TWO GOODS, BOUNDED BY TWO DIFFERENT THINGS, and that is the whole design
 * rather than a detail. Fabricated steel is 61% steel and 20% wages, so what
 * decides whether a shop pays is THE STEEL PRICE: thin on imported steel at
 * the ceiling, an ordinary business on local steel in the middle of the band,
 * a good one on steel at its export floor. Machinery is 21% steel and about
 * half wages, so what decides it is THE WAGE BILL AND THE CURRENCY, the same
 * two that decide a contact centre - but at a diploma machinist's wage rather
 * than an agent's, which is why it is the rung above rather than a competitor.
 *
 * So a city reads its own position off this sector's screen. If the sector is
 * dying and the steel line is the red one, it needs a mill. If it is dying and
 * the wage line is, it has priced itself out - and, unlike a contact centre,
 * that is not fatal, because the machine works survives a wage that closes the
 * centre and the fabrication shop barely notices the wage at all.
 *
 * AND IT IS THE FIRST DOMESTIC CUSTOMER STEEL HAS EVER HAD. Until this
 * sector, STEEL's entry in Good said "not importable: there is no local buyer
 * to import for", and every tonne the mills made left at the export floor for
 * want of anybody here to sell it to. A fabricator bids, the strike lifts the
 * price off the floor toward the hot-rolled ceiling, and the mills get paid
 * more for the same tonne without changing anything they do. Ore, to steel, to
 * a beam that leaves: three links, and the middle one is finally worth
 * something at home. That is the cluster the ore market was built to reward,
 * one link further up than it could reach.
 *
 * NOT GATED ON THE GROUND, which is the point of it. Heavy Industry will not
 * build a mill without spare local ore, and should not - a mill on scrap alone
 * earns almost nothing. A fabricator on imported steel earns thin but real, so
 * the sector can open in a city with no deposit at all. Geology decides how
 * WELL it does here, not whether it can be done here, and that is the
 * difference between a sector that absorbs labour and one designed around a
 * deposit.
 *
 * See claude/manufacturing.md.
 */
public final class Manufacturing extends Sector {

    /*
     * THE OIL ON THE LINE (0.7.83, batch O6; docs/studies/spec-oil.md 2.5,
     * est., to confirm). A machine shop's cutting fluids, hydraulic and gear
     * oils, and the grease on a fabricator's cranes: LUBRICANTS, in litres, on
     * each template's `uses` - bought on the market as the steel is, the
     * refiners' lube plants first and the world for the rest. The first home
     * buyer the refinery's lubricants have had.
     */

    /** Lubricants a tonne of machinery made takes: ten litres (spec-oil 2.5, est.). */
    public static final double LUBRICANT_LITRES_A_TONNE_OF_MACHINERY = 10;

    /** ...and a tonne of fabricated steel: two litres (spec-oil 2.5, est.). */
    public static final double LUBRICANT_LITRES_A_TONNE_FABRICATED = 2;

    /*
     * THE WIRE & CABLE WORKS (0.7.133, batch I8; docs/studies/spec-materials.md
     * 2.1, the study's 4.3 and 4.4): copper's third industry, the one building
     * of the chain that is not Copper Mining's or Copper Refining's - 2,000 t
     * of wire and cable a month from 2,000 t of Copper Refining's rod, its
     * insulation in its upkeep (consumables). Built by this sector's own
     * plan, unchanged: what it earns on its cost against the fabrication
     * shops' and the machine works'. The study's four cases: -0.3% of its
     * cost a month on imported rod for export, 0.6% on the city's rod, 7.9%
     * replacing imported wire and cable, 7.0% on imported rod - "built to
     * replace imports, not to export", and nobody here buys wire and cable
     * until the construction upgrade (spec 2.12), so it ships until then.
     */

    /** The Wire & Cable Works' permanent id in buildings.json (0.7.133): 2,000 t of wire and cable a month from 2,000 t of rod. */
    public static final int WIRE_CABLE_WORKS = 128;

    public Manufacturing() {
        super("Manufacturing", "Manufacturing", BuildingType.HEAVY_INDUSTRY);
        makes(Good.FABRICATED_STEEL);
        makes(Good.MACHINERY);
        uses(Good.STEEL);
        uses(Good.LUBRICANTS);   // 0.7.83, batch O6: the oil on the line
        // ...and since 0.7.133 (batch I8) the Wire & Cable Works': Copper Refining's rod drawn into wire and cable.
        makes(Good.WIRE_CABLE);
        uses(Good.COPPER_ROD);
        blurb("Buys steel - from the city's mills if it has any, from the world if "
                + "not - cuts, welds and machines it, and ships it out. The first "
                + "thing in the city that ever wanted a tonne of steel, so a mill "
                + "and a fabricator here are worth more to each other than either "
                + "is alone. It needs no deposit of its own, only people.");
    }

    /** What it makes of a flow good, and since 0.7.123 (batch I4) the offcuts of its fabricated steel to the city's old steel, its own to sell (OldMetal). */
    @Override
    public void produceFlow(Good g) {
        super.produceFlow(g);
        if (g == Good.FABRICATED_STEEL && game != null) game.getOldMetal().fabricated(key(), output(g).produced);
    }

    /* ---------------------------------------------------------------- reading */

    /** Every post the sector holds, staffed or not - the sector in one number. */
    public double getPosts() {
        long[] posts = postsPerTier();
        double total = 0;
        for (long n : posts) total += n;
        return total;
    }

    /** Tonnes of steel the plants want this month, at the rate they are running. */
    public double getSteelDemand() {
        return getInputAtCapacity(Good.STEEL) * getOperatingRate();
    }

    /** What a tonne of steel is costing the plants here - the number the shops live or die on. */
    public double getSteelPrice() {
        return markets == null ? 0 : markets.get(Good.STEEL).getLocalPrice();
    }

    /**
     * Where the steel price sits between the mills' export floor and the
     * world's delivered ceiling: 0 is a mill with nobody else to sell to,
     * 1 is a city with no mill at all.
     */
    public double getSteelPosition() {
        return markets == null ? 0 : markets.get(Good.STEEL).getPriceIndex();
    }

    /**
     * The steel bill as a share of what the sector sold.
     *
     * The first of the two numbers that say whether this sector has a future
     * here. Against what it ACTUALLY billed rather than against nameplate, for
     * the reason payrollShare() carries: a ratio that counts unstaffed
     * capacity as earning is worse than no ratio (sectors.BusinessServices).
     */
    public double steelShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().inputs / revenue : 0;
    }

    /** ...and the wage bill, the same way. The other one. */
    public double payrollShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().payroll / revenue : 0;
    }

    /** Both together, which is what has to stay under one. */
    public double costShare() { return steelShare() + payrollShare(); }

    /* ------------------------------------------------------------------ plan */

    /**
     * Overridden for the reason Business Services and Heavy Industry override
     * it: the generic planner forecasts from LOCAL demand, and
     * BusinessInvestment says so in its own header - "the world is not
     * demand". Neither of these goods has a local buyer, so the generic path
     * would decide output is permanently ahead of demand and never build
     * anything.
     *
     * WHAT GATES IT INSTEAD is the profit test the brake already applies, plus
     * the staffing floor - and the floor matters more here than anywhere else
     * in the game, because the Fabrication Works is three hundred and thirty
     * posts, the largest single job step there is. See
     * Sector.MIN_STAFFABLE_TO_ORDER for what eighty percent is doing there and
     * what happened without it.
     *
     * NO ORE TEST, DELIBERATELY, and this is the one place this sector departed
     * from the mills it stands next to. To 0.7.119 the mills' plan refused to
     * build without spare local ore and was right to: a mill on imported scrap
     * earned almost nothing, and a sector that expands on a business case it
     * does not have is the borrowing spiral the investment engine was written
     * to end. (Since 0.7.120, batch I3, Iron & Steel builds its furnaces by
     * SpreadPlanner on their whole cost, and they melt scrap, not ore.) A
     * fabricator on imported steel is a different case - thin, but real, about
     * eighteen percent against an ordinary plant's thirty - so the profit test
     * is allowed to be the whole of the answer. If the steel price makes it a
     * bad business, estimatedMonthlyProfit() already knows: it prices the
     * inputs at today's local price, which in a city with no mills IS the
     * import ceiling the moment anybody bids.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {

            // Every post fillable at MIN_STAFFABLE_TO_ORDER, and none in a band
            // nobody could fill (0.7.18; see Sector.staffing()).
            Staffing staffing = staffing(t);
            double staffable = staffing.share;
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }

            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            // ...the Wire & Cable Works' lorries with it (0.7.133, ★ as every works of the materials chains is weighed).
            if (t.getId() == WIRE_CABLE_WORKS) cost += fleetCapital(t);
            // Still discounted as well as floored: the floor says whether any
            // of them is worth opening, the weight says which.
            double score = estimatedMonthlyProfit(t, plans) * staffable / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (staffingHold != null) {
                return BusinessInvestment.Decision.no(sector, staffingHold.why(staffingHoldName));
            }
            return BusinessInvestment.Decision.no(sector, String.format(
                    "steel at %s a tonne and the wage bill leave nothing in it",
                    Formats.INSTANCE.cash(getSteelPrice())));
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                "steel worth more shaped than shipped", true);
    }

    /**
     * What one more of its buildings would clear a month (0.7.133, batch I8):
     * the maker's estimate (Sector's) - and for the Wire & Cable Works, the
     * materials chains' row, less what it buys abroad that is not a good, its
     * insulation at the exchange factor (SpreadPlanner.ownCosts()), and its
     * freight - its vans' wear and diesel and its bulk's city legs at
     * Haulage's quote (★, as every works and mine of the materials chains is
     * weighed: ProcessingSector.freightRunning()). The figure the
     * plan and Game.consider() read. The sector's older rows buy no
     * consumables and were never weighed with their freight, so theirs is
     * the maker's estimate to the bit.
     *
     * WHY. Without them the works' D$3.2M of insulation a month at parity was
     * invisible to the plan and to consider(), and on a weak currency and the
     * city's narrowed freight the playtest ordered Wire & Cable Works on
     * imported rod for export that lost money from their first month (I8's
     * probe, fixI8-notes 4).
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        // The wire works subtracts its own freight below; every other works of its is weighed with its legs (Sector's).
        if (t == null || t.getId() != WIRE_CABLE_WORKS || markets == null) return super.estimatedMonthlyProfit(t, plans);
        double maker = plans.estimatedMakerProfit(this, t);
        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        return maker - SpreadPlanner.ownCosts(t, rate, markets.getExchangeRate(), pricePerWatt)
                - ProcessingSector.freightRunning(t, SpreadPlanner.values(markets), rate, legPrice());
    }

    /** The vans one of `t` must buy, at what a van costs here (ProcessingSector.fleetCapital()); none with no markets. */
    double fleetCapital(BuildingsTemplate t) {
        return markets == null || t == null ? 0 : ProcessingSector.fleetCapital(t, SpreadPlanner.values(markets));
    }

    /**
     * A price taker with no stock: it shrinks on distress, not on a demand
     * reading. Same as Heavy Industry and Business Services, and for the same
     * reason - neither good has a local demand series to compare capacity
     * against. The default would say this too, off a planning good that is not
     * stockable; it is written out because it is a design decision and not a
     * consequence of one.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) { return null; }

    /* ------------------------------------------------------------ the screen */

    @Override
    public List<Sector.Line> ownLines(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;

        if (getPosts() <= 0) {
            lines.add(Line.note("Nothing standing. This sector buys steel and ships what it "
                    + "makes of it, so it needs no ore of its own - but a city with a mill in "
                    + "it buys that steel cheaper than the world sells it, and that gap is most "
                    + "of what a fabrication shop earns."));
            return lines;
        }

        lines.add(Line.head("Where the steel comes from"));
        double position = getSteelPosition();
        lines.add(Line.of("Steel, a tonne", f.amount(getSteelPrice()),
                position < .34 ? Line.Tone.GOOD : position < .67 ? Line.Tone.NONE : Line.Tone.WARN));
        lines.add(Line.of("Wanted this month", f.units(getSteelDemand(), Good.STEEL)));
        lines.add(Line.note(position >= .67
                ? "Near what it costs to bring a tonne in, which is what a city with no mills "
                + "pays. A foundry here would sell into this."
                : position < .34
                ? "Near what a mill gets shipping it out - the mills have nowhere else to go and "
                + "the plants are getting the benefit."
                : "In the middle of the band, which is a mill and a fabricator both doing "
                + "better than they would trading with the world."));

        lines.add(Line.head("Whether it has a future here"));
        double steel = steelShare(), pay = payrollShare(), both = costShare();
        Line.Tone tone = both <= 0 ? Line.Tone.MUTED
                : both < .85 ? Line.Tone.GOOD
                : both < 1.0 ? Line.Tone.WARN
                : Line.Tone.BAD;
        lines.add(Line.of("Steel, as a share of what it sold", f.pct(steel)));
        lines.add(Line.of("Wages, as a share of what it sold", f.pct(pay)));
        lines.add(Line.of("The two together", f.pct(both), tone));
        if (both >= 1.0) {
            lines.add(Line.note(steel > pay
                    ? "Steel costs more than the shaped steel fetches. Either the mills here "
                    + "have other buyers or there are no mills here."
                    : "The city costs more than the work is worth. Either wages here have risen "
                    + "or the currency has."));
        } else if (both >= .85) {
            lines.add(Line.note("Thin. A fabrication shop runs about eighty percent on the two "
                    + "together and a machine works about seventy; past one hundred the plants "
                    + "start closing."));
        }
        return lines;
    }
}

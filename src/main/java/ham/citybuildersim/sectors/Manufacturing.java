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
 *   Fabrication Works     fabricated steel   330 posts, the same trade at scale
 *   Machine Works         machinery          173 posts, trades and technicians
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

    public Manufacturing() {
        super("Manufacturing", "Manufacturing", BuildingType.HEAVY_INDUSTRY);
        makes(Good.FABRICATED_STEEL);
        makes(Good.MACHINERY);
        uses(Good.STEEL);
        blurb("Buys steel - from the city's mills if it has any, from the world if "
                + "not - cuts, welds and machines it, and ships it out. The first "
                + "thing in the city that ever wanted a tonne of steel, so a mill "
                + "and a fabricator here are worth more to each other than either "
                + "is alone. It needs no deposit of its own, only people.");
    }

    /* ---------------------------------------------------------------- reading */

    /** Every post the sector holds, staffed or not - the sector in one number. */
    public double getPosts() {
        int[] posts = postsPerTier();
        double total = 0;
        for (int n : posts) total += n;
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
     * NO ORE TEST, DELIBERATELY, and this is the one place this sector departs
     * from the mills it stands next to. HeavyIndustry.plan() refuses to build
     * without spare local ore and is right to: a mill on imported scrap earns
     * almost nothing, and a sector that expands on a business case it does not
     * have is the borrowing spiral the investment engine was written to end. A
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
        boolean sawStaffingWall = false;
        double bestStaffable = 0;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {

            double staffable = staffableShare(t);
            if (staffable < MIN_STAFFABLE_TO_ORDER) {
                if (staffable > bestStaffable) bestStaffable = staffable;
                sawStaffingWall = true;
                continue;
            }

            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            // Still discounted as well as floored: the floor says whether any
            // of them is worth opening, the weight says which.
            double score = estimatedMonthlyProfit(t, plans) * staffable / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (sawStaffingWall) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "the city could staff %.0f%% of a plant; it wants %.0f%%",
                        bestStaffable * 100, MIN_STAFFABLE_TO_ORDER * 100));
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
    public List<Sector.Line> operations(Game game) {
        List<Line> lines = super.operations(game);
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
        lines.add(Line.of("Steel, a tonne", f.cash(getSteelPrice()),
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

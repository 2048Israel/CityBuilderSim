package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Markets;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Trade;

import java.util.List;

/**
 * The luxury shops. THE FOURTEENTH SECTOR (2026-09-17, Jerus's call).
 *
 * Jerus: "lets add restuarants as well as luxury stores, these two will absorb
 * some spending as well." And, on how they should price: "supply v demand,
 * thats the most important thing."
 *
 * =========================================================================
 * WHY THIS EXISTS, WHICH IS A MEASUREMENT AND NOT A THEME
 * =========================================================================
 *
 * Until today a household in this game could buy exactly four things: food, a
 * home, a car, and the city's fees. Food is capped by APPETITE - Consumption
 * scales every basket by the lesser of what a household can afford and what it
 * can eat - and a home is one home. So a city whose ordinary unskilled couple
 * earns two hundred and sixty times what subsistence costs had nowhere to put
 * the other ninety-four per cent of its money.
 *
 * It saved it. Household net worth reached FIVE THOUSAND MONTHS of the city's
 * entire output and was still climbing at the end of every run. Three separate
 * attempts to fix that inside the consumption function - paying the foreign
 * coupon home, letting investment income into the plan, a wealth term in the
 * plan itself - moved it and did not stop it, because a drain cannot empty a
 * sealed pipe. The pipe is sealed here.
 *
 * =========================================================================
 * THE SCARCE THING IS THE SHOP, NOT THE WATCH
 * =========================================================================
 *
 * The world has no shortage of watches: LUXURIES is importable at a world
 * price and the city can have as many as it will pay for. What a city can be
 * short of is somewhere to buy one - a counter, a Tuesday, somebody to serve
 * you - and that is a thing a PLAYER BUILDS.
 *
 * So the margin is what gets struck, on the same rule GoodsMarket.strike()
 * uses and the second-hand car market before it: a floor, a ceiling, and a
 * position between them off demand against supply. Which closes the loop
 * Jerus asked for:
 *
 *     crowded shops -> the margin widens -> luxury retail is worth building
 *       -> the investor builds boutiques -> coverage rises -> the margin falls
 *
 * A city that will not build shops cannot spend its money, and will see
 * exactly why on the sector's own screen. That is the mechanic; the absorption
 * is what it is for.
 *
 * =========================================================================
 * AND IT IS AN IMPORT, DELIBERATELY
 * =========================================================================
 *
 * The money LEAVES rather than circulating. A rich city finally runs a
 * current-account deficit instead of the three-century surplus that four
 * addenda of `why-there-is-no-inflation.md` could not shift - and the hoard
 * falls rather than going round again. If a domestic maker ever appears, that
 * is import substitution, and it is something a player should have to earn.
 */
public class LuxuryRetail extends Sector {

    /* =====================================================================
       THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT

       Both ends are SHARES OF WHAT THE SHOP PAID, which is the money-constant
       rule this project has found twenty-five instances of: a bound written in
       absolute money is not a bound, it is a bug waiting for a reform. A
       multiple of the landed cost moves with the landed cost and needs no
       seeding, and it is also the truer statement - a shop marks up, it does
       not add a fixed number of dollars to a watch.
       ===================================================================== */

    /** What a shop with nobody in it charges over what the piece cost it. */
    public static final double MARGIN_FLOOR = 1.25;

    /**
     * ...and what a shop with a queue charges.
     *
     * FOUR TIMES THE LANDED COST is roughly where real luxury retail sits, and
     * the gap between this and the floor is the whole of the player's signal:
     * a city short of shops watches its margin climb toward here and its
     * luxury sector turn profitable, which is what makes the investor build.
     */
    public static final double MARGIN_CEILING = 4.0;

    /** What one shop's counter is worth a month, before anybody has told it anything. */
    private double sellPrice;

    /* =====================================================================
       WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG
       PRICE MADE

       Round 1 of the bonds batch saw this sector's shelf go $375M, $150M,
       $375M, $150M, month after month; round 6 traced it (the project's
       the-firms-sell-bonds.md, section 4). It was not the order-up-to rule
       overshooting and it was not the design. It was this read.

       The shop used to take its cost as the wholesale market's LOCAL PRICE,
       read at the top of the month - which is the price the market struck at
       LAST month's clearing. LUXURIES has no maker in this city and no export
       floor (the world will not buy them back), so its band runs from zero to
       the import price. A clearing where the shop bid for something has a
       buyer and no maker, and strikes the ceiling: the import price, which is
       what the shop then pays. A clearing where the shop bid for NOTHING has
       nothing on either side, and GoodsMarket.strike() puts a market like that
       in "the middle" of its band - half the import price, a price at which
       not one piece changed hands and at which nobody could buy one.

       The shop read that half as its cost. Its ticket halved, the queue at
       the door doubled, the shelf emptied, it bid, the next clearing struck
       the import price, the ticket doubled, the queue halved, the shelf sat
       over three months of the smaller sales, it bid nothing - and the market
       was back in the middle. Measured on one city (seed 2, months
       1630-1648): cost 3.13 / 6.26 alternating, queue 24,680 / 12,357
       pieces, sold 6,505 / 3,940, cash $25M / nothing, and a cash-flow
       default every other month. No other sector's shelf alternated.

       SO THE COST IS WHAT A PIECE COSTS TO BRING IN THIS MONTH: the local
       price when somebody in the city is offering luxuries - that is the price
       a maker would sell at - and the import price when nobody is, because
       then abroad is the only place a piece comes from. It is the same figure
       the old read gave in every month the shop had bid, and it moves with the
       currency, since importPrice() is the world price at today's rate.
       ===================================================================== */

    /**
     * What one piece costs the shop to bring in: the local price when the city
     * has luxuries on offer, the import price when it has none.
     */
    public static double landedCost(GoodsMarket wholesale) {
        double landed = wholesale.landedPrice();
        if (!(landed > 0)) landed = Good.LUXURIES.worldImportPrice();
        return landed;
    }

    /** The month's reading, for the screen and the harness. */
    private double rMargin = MARGIN_FLOOR, rWanted, rServed, rCoverage, rLanded;

    public LuxuryRetail() {
        super("Luxury Retail", "Luxury Retail", BuildingType.LUXURY);
        makes(Good.LUXURY_TRADE);
        uses(Good.LUXURIES);

        /*
         * THREE MONTHS ON THE SHELF, like the vehicles and for the same
         * reason: a watch sits in a case until somebody buys it. A shop that
         * had to import within the month would be a shop that ran out every
         * time the city got richer.
         */
        pantry(Good.LUXURIES, 3);

        blurb("Buys luxuries from the world and sells them over a counter. Makes "
                + "nothing: what it has is somewhere to shop, which is the only "
                + "thing a city short of watches is actually short of.");
    }

    /* ===================================================================
       THE SALE
       =================================================================== */

    /** People the shops can serve a month, off their buildings. */
    public int coverage() {
        return buildings == null ? 0
                : (int) Math.round(buildings.totalBySector(key(), b -> b.getCoverage()));
    }

    public double getMargin()    { return rMargin; }
    public double getWanted()    { return rWanted; }
    public double getServed()    { return rServed; }
    public double getCoverage()  { return rCoverage; }
    public double getLanded()    { return rLanded; }
    public double getSellPrice() { return sellPrice; }

    /**
     * Strikes the margin against the queue and returns what a piece will cost
     * this month. Nothing is sold yet - the households have to be asked at
     * this price before they can answer.
     *
     * THE TWO-PASS SHAPE IS THE HOUSE'S, for the reason written at
     * HouseholdBalance.clearUsedCars(): demand depends on the price and the
     * price depends on demand, and every market in this game breaks that
     * circle the same way - strike off the demand that arrived, then sell at
     * what was struck. LuxuryCounter.shop() runs the two halves.
     *
     * @param wanted pieces the households came for, before any cap
     */
    public double strikeMargin(Markets markets, double wanted) {
        int cover = coverage();
        rCoverage = cover;
        rWanted = Math.max(0, wanted);
        wanted = rWanted;

        double landed = landedCost(markets.get(Good.LUXURIES));
        rLanded = landed;

        /*
         * THE POSITION, ON GoodsMarket.strike()'s OWN TERMS. Nobody at the
         * door is a shop at its floor; a queue round the block is a shop at
         * its ceiling; and with no shops at all there is nothing to strike, so
         * the margin sits where it opened and no one is served.
         */
        double position = wanted + cover <= 0 ? 0 : wanted / (wanted + cover);
        rMargin = MARGIN_FLOOR + (MARGIN_CEILING - MARGIN_FLOOR) * position;
        sellPrice = landed * rMargin;

        rServed = 0;
        return sellPrice;
    }

    /**
     * ...and sells what the counter and the shelf can actually get through.
     *
     * THE THREE CAPS, and each is a different thing a city can be short of:
     * customers the shops can serve at all, the share of them the shops can
     * get to with the power and the streets and the staff they have, and
     * pieces actually in stock.
     *
     * @return pieces sold
     */
    public double serve(Markets markets, double pieces) {
        int cover = coverage();
        double servable = Math.min(Math.max(0, pieces), cover) * getOperatingRate();
        double onShelf = getPantry(Good.LUXURIES);
        // Whole pieces: a quantity crossing from the money world into the
        // physical one crosses at a grain coarser than the dust. See the note
        // above HouseholdBalance.wantOf().
        double sold = Math.floor(Math.min(servable, onShelf));
        rServed = sold;
        noteShelfShort(Math.floor(servable) - sold, sellPrice);
        if (sold <= 0) return 0;

        Trade t = markets.get(Good.LUXURY_TRADE)
                .record(key(), Trade.HOUSEHOLDS, sold, sellPrice);
        bookSale(t);
        usePantry(Good.LUXURIES, sold);
        return sold;
    }

    /** What the shops hold, in pieces. */
    public double onShelf() { return getPantry(Good.LUXURIES); }

    /**
     * What to restock against, the month-one fallback included.
     *
     * THE SECOND BOOTSTRAP DEADLOCK, and it cost a whole run to find. The
     * pantry restocks against what was USED, and a shop that has never sold
     * anything has used nothing - so it ordered nothing, so it had nothing to
     * sell, so it used nothing. The boutiques went up, sat empty, earned zero
     * against the debt that built them, and the sector defaulted twenty-five
     * times into a hundred-and-twenty-nine-month borrowing ban while four and
     * a half million customers stood outside.
     *
     * So a shop with no sales to go on stocks for every customer it could
     * serve, which is exactly what Retail.recentUse() does and for exactly
     * this reason. The first month is a guess; every month after is a fact.
     */
    @Override
    protected double recentUse(Good g) {
        if (g != Good.LUXURIES) return super.recentUse(g);
        return rServed > 0 ? rServed : coverage();
    }

    /* ===================================================================
       PLANNING - the queue at a door that is not there
       =================================================================== */

    /**
     * Builds against the customers who CAME, not against a sales record.
     *
     * WHY THE DEFAULT COULD NOT WORK, and it was measured before this was
     * written. `planMaker()` scores a sector on what it has been selling, and
     * a sector with no shops has been selling nothing: over four thousand
     * months the city's households came for four and a half MILLION pieces,
     * the margin sat pinned at its 4.00x ceiling every single month for three
     * centuries, and not one boutique was ever built. The price signal was
     * screaming and the thing that was supposed to hear it was deaf, because
     * it was listening for a sales trend that could not exist until somebody
     * went first.
     *
     * THE SIGNAL IS THE QUEUE. `rWanted` is measured whether or not there is
     * anywhere to spend it - LuxuryCounter.shop() asks the households at the
     * margin's floor before it asks the shops anything - so it is exactly the
     * demand a shop that does not exist yet would serve. Retail's plan() does
     * the same thing against population; this does it against money that
     * turned up and found the door locked.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        int cover = coverage();
        double queue = rWanted;
        if (queue <= cover * (1 + BusinessInvestment.TARGET_HEADROOM)) {
            return BusinessInvestment.Decision.no(sector, "counters ahead of customers");
        }
        if (!(sellPrice > rLanded)) {
            return BusinessInvestment.Decision.no(sector, "no margin in it");
        }

        double output = game.getConstructionOutput();
        BuildingsTemplate best = null;
        double bestScore = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getCoverage() <= 0) continue;
            /*
             * WHAT A COUNTER EARNS IS THE MARGIN, not the ticket - a shop that
             * sells a nine-dollar watch for twelve has made three dollars and
             * owes the world nine. Scoring on the ticket would value a
             * boutique at four times what it is worth and build four times too
             * many of them.
             */
            double monthlyIncome = t.getCoverage() * (sellPrice - rLanded);
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }
        if (best == null) return BusinessInvestment.Decision.no(sector, "nothing worth building");

        int quantity = plans.orderSize(queue - cover, best.getCoverage(), best, output);
        if (quantity <= 0) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));

        return new BusinessInvestment.Decision(sector, best, quantity,
                String.format("%,.0f customers came against %,d the shops can serve", queue, cover),
                true);
    }

    /**
     * What one more counter would earn a month.
     *
     * THE SECTOR HAS TO VALUE ITS OWN, because the generic estimate reads a
     * maker's sales and this sector has never sold anything until the first
     * shop opens - which is the bootstrap every new sector in this game has
     * hit. It is not enough for plan() to WANT a boutique: consider() checks
     * that the build services its own debt, and it asks this method how much
     * the building earns. Answer nothing and a sector with four and a half
     * million customers at the door never borrows a penny.
     *
     * THE MARGIN, NOT THE TICKET, and net of what it costs to run. A shop that
     * sells a nine-dollar watch for twelve has made three dollars, owes the
     * world nine, and still has to pay its staff and its lights.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || t.getCoverage() <= 0) return 0;
        double landed = rLanded > 0 ? rLanded : Good.LUXURIES.worldImportPrice();
        double price = sellPrice > 0 ? sellPrice : landed * MARGIN_FLOOR;
        // Only the customers the counter would actually see: a boutique in a
        // city with a hundred spare counters sells nothing.
        double served = Math.min(t.getCoverage(), Math.max(0, rWanted - coverage()));
        if (served <= 0) served = t.getCoverage() * .5;
        return served * (price - landed) - plans.runningCostOf(t);
    }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    @Override
    public List<Line> operations(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        Formats f = Formats.INSTANCE;

        lines.add(Line.head("THE COUNTER"));
        lines.add(Line.of("People the shops can serve", f.count(rCoverage)));
        lines.add(Line.of("...who came", f.count(rWanted),
                rWanted > rCoverage ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("...and who was served", f.count(rServed)));
        if (rWanted > rCoverage + .5) {
            lines.add(Line.note(f.count(rWanted - rCoverage)
                    + " customers a month want a shop that is not there. Every one of"
                    + " them is money the city cannot spend."));
        }

        lines.add(Line.head("THE MARGIN, STRUCK AGAINST THE QUEUE"));
        lines.add(Line.of("What a piece cost the shop", f.cash(rLanded)));
        lines.add(Line.of("...and what it sold for", f.cash(sellPrice)));
        lines.add(Line.of("The mark-up", String.format("%.2fx", rMargin),
                rMargin > (MARGIN_FLOOR + MARGIN_CEILING) / 2 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.note(String.format(
                "A quiet shop charges %.2fx what it paid and a crowded one %.2fx."
                + " Build shops and the mark-up falls; leave them unbuilt and it climbs"
                + " until somebody else builds them.", MARGIN_FLOOR, MARGIN_CEILING)));

        lines.add(Line.head("THE SHELF"));
        lines.add(Line.of("Pieces in stock", f.count(onShelf())));
        return lines;
    }
}

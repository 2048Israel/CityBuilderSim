package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Sector;
import ham.citybuildersim.TaxPolicy;

/**
 * The plants between the farm and the shelf.
 *
 * THE ELEVENTH SECTOR (2026-09-16, Jerus's call): "a new sector which makes the
 * ready meals, processed meals, snacks, drinks, and cooking fats".
 *
 * Those five are a THIRD OF THE SHELF BY WEIGHT and a third of the food bill by
 * value - 16.3kg of the reference basket's 50, and $51.40 of its $161.75 - and
 * until today every gram of them was imported. The city could grow the meat and
 * bake the bread and still buy its sausages, its crisps, its cooking oil and
 * its drinks from abroad, because nothing here turned one food into another.
 *
 * WHAT IT CHANGES, and it is bigger than five goods: THE LIVESTOCK FARM FINALLY
 * HAS A CUSTOMER AT HOME. Agriculture's meat went to the shelf or abroad and
 * nowhere else, which made the dearest building in the catalogue a pure export
 * play. The Meat Works buys it. That is the same sentence as a mine next door
 * to a mill and a mill next door to a fabricator, one chain over, and it is the
 * fourth reason in this game to put two things near each other.
 *
 * EACH GOOD BUYS ITS OWN INPUT, which was Jerus's call over the simpler "crops
 * only", and it is what gives the three plants three different cost stories:
 *
 *   Meat Works        meat, vegetables and grains -> processed meats, ready meals
 *   Snack & Oils      crops                       -> snacks, cooking fats
 *   Bottling Plant    crops                       -> drinks
 *
 * TWO BRAKES, NOT ONE, and that is the design - the same shape Manufacturing
 * took. The Meat Works is 40% meat by revenue at mid-band and 46% at the floor,
 * so THE MEAT PRICE decides it: measured at month 98 of a real city, the same
 * building is worth $16,559 a month on meat at a farm's export floor and $1,678
 * on meat off a ship. TEN TIMES, on one price. The Bottling Plant is 4% crops
 * and 21% wages, so THE WAGE BILL AND THE WATER decide it - a drink is mostly
 * water, and the plant is the first building in the game whose utility bill is
 * a real line rather than a rounding error. The Snack & Oils Plant sits between
 * them, 19% crops, and it is the widest mark-up in the catalogue: crisps really
 * are $10 a kilo pressed out of $0.44 potatoes. A city priced out of one rung
 * can still build another, which is the whole reason there are three.
 *
 * IT DOES NOT USE WHAT IT MAKES, deliberately. Crisps are fried in oil and this
 * sector presses oil, so FATS is the obvious input for SNACKS - and it is left
 * out for the reason Agriculture leaves the cows' feed out: a sector that makes
 * a good and uses it hands its other outputs a share of that line's bill
 * through the joint-cost split, and the market would clear it without ever
 * saying so. See Sector.costShareOf().
 *
 * Everything else is the generic template: it plans output against the shops'
 * demand rather than flooding its own shed, withholds below marginal cost,
 * dumps above the shed's line, and ships spare nameplate abroad when the
 * world's price clears the power it costs.
 */
public final class FoodProcessing extends Sector {

    public FoodProcessing() {
        super("Food Processing", "Food Processing", BuildingType.INDUSTRIAL);
        /*
         * THE SECTOR'S OWN OUTPUT LIST, AND IT IS NOT THE BUILDINGS'.
         *
         * Markets iterates goodsMade() to decide what comes to market, so this
         * line - not the templates - is what makes a good exist. FoodIndustry's
         * header records what happens when the two disagree: 858,000kg of
         * nameplate capacity producing nothing, no exception and no log line.
         */
        makes(Good.PROCESSED_MEAT);
        makes(Good.READY_MEALS);
        makes(Good.SNACKS);
        makes(Good.FATS);
        makes(Good.DRINKS);

        uses(Good.MEAT);
        uses(Good.VEGETABLES);
        uses(Good.GRAINS);
        uses(Good.CROPS);

        blurb("Turns the city's meat and crops into the third of the shelf that "
                + "arrives already made - sausages, ready meals, crisps, cooking "
                + "oil and drinks. Buys from the city's own farms when there are "
                + "any and from the world when there are not, idles rather than "
                + "flood its own warehouse, and ships spare capacity abroad.");
    }

    /**
     * How full a new plant has to be before anybody finances one.
     *
     * A ratio, not a figure in money or in kilos, so it survives a reform and
     * a change of scale alike.
     */
    public static final double FILLED = .85;

    /* =====================================================================
       THREE PLANTS, FIVE GOODS, AND A PLANNER THAT COULD ONLY SEE ONE

       BusinessInvestment.planMaker() sizes a sector by ONE good -
       Sector.planningGood(), the first stockable thing it makes - and then
       skips every template that does not make it:

           for (BuildingsTemplate t : templatesBySector(key))
               if (t.makes(good) <= 0) continue;

       This sector's planning good is processed meat, and only the Meat Works
       makes any. So the Snack & Oils Plant and the Bottling Plant were
       unbuildable: not refused, not scored badly, INVISIBLE. Measured over
       four hundred months on 2026-09-16 - one Meat Works at month 57, and
       across the whole run zero snack plants and zero bottling plants, while
       the city imported every crisp and every bottle it drank.

       That is the third sector in this game to need its own planner for the
       same structural reason (see Manufacturing, Business Services), and the
       shape here is Manufacturing's: score every template the sector owns,
       take the best one that clears the floors, say plainly why when none
       does. The floors are two -

         FULLNESS, the rule below: nobody builds a food plant on exports.

         PROFIT, at the price the plant itself will leave behind - see
         estimatedMonthlyProfit(), which this sector also overrides, for why
         the generic estimate is not good enough here.

       AND NOT THE STAFFING FLOOR, which is the one borrowed rule this sector
       does NOT take, and it was measured before it was dropped. Sector's
       MIN_STAFFABLE_TO_ORDER asks the city for eighty percent of a building's
       posts in people who are not already working, and its own header says
       what it is for: "any building whose posts are a large step against the
       city that would fill them" - the contact centre's three hundred seats,
       the fabrication works' three hundred and thirty. These three plants are
       five, six and four posts, the SMALLEST investor-built job steps in the
       catalogue. Against a city at full employment the test reads "the city
       could staff 25% of a plant" - one spare tradesman against four posts -
       and it read exactly that, unchanged, for two hundred and fifty
       consecutive months while the city grew from four hundred people to
       nineteen thousand and imported every bottle it drank.

       A rule sized for three hundred posts is not a rule about four. What
       keeps these plants honest is that the city has to be eating what one
       makes and the plant has to clear its own payroll at the price it will
       actually get, and both of those are tested above.
       ===================================================================== */

    /* ---------------------------------------------------------------------
       NOBODY BUILDS A FOOD PLANT ON EXPORTS

       THE WORLD'S BAND IS NARROWER THAN THE PROCESSING STEP. Raw meat lands
       at $7.00 and processed meat ships at $5.60, and a kilo of sausage takes
       five sixths of a kilo of meat - so a plant that buys its input at the
       world's ceiling and sells its output at the world's floor clears about
       twenty-seven cents a tonne, before a single wage. Every other maker in
       this game has somewhere to put a surplus: a mine ships ore, a mill
       ships steel, a contact centre's whole customer is foreign. THIS ONE HAS
       NOWHERE. It is the mirror of Business Services.

       WHAT THAT DOES IF NOBODY SAYS IT. Measured over 4,000 months: the
       investor financed a Meat Works at month 42 into a city of eight hundred
       people, against a plant that fed two and a half thousand - half today's,
       which is the size it was before the calibration in BuildingManager. Two
       thirds of everything it made went abroad at the floor, it lost money
       every month from the month it opened, and the sector was written down
       seven times and finished the run holding nothing. The plant was not
       wrong and the price was not wrong; the SIZE of the city was.

       So: a plant is financed when the city already eats what it would make.
       Not a forecast - the demand on the market, today, less what this sector
       can already supply. Valued at each good's export floor so that five
       goods in one unit can be added up, which is the same yardstick
       Agriculture underwrites its fields on.

       IT IS NOT A BAN ON EXPORTING. A plant that is full still ships its
       spare nameplate abroad when the floor clears the power, the way every
       maker does - see Sector.getExportBoundOutput(). What it cannot do is be
       BUILT on that revenue.
       --------------------------------------------------------------------- */

    /** How much of one of these the city would eat today, as a share of what it makes. */
    public double fillFor(BuildingsTemplate t) {
        double onePlant = 0, uncovered = 0;
        for (Good g : t.goodsMade().keySet()) {
            double made = t.makes(g);
            if (made <= 0 || !g.traded() || markets == null) continue;
            double floor = Math.max(0, markets.get(g).exportPrice());
            onePlant += made * floor;
            uncovered += Math.max(0, markets.get(g).getDemand()
                    - getCapacity(g) - getPipeline(g)) * floor;
        }
        return onePlant > 0 ? uncovered / onePlant : 0;
    }

    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings == null || markets == null) return BusinessInvestment.Decision.no(sector, "nothing to plan with");
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        BuildingsTemplate best = null;
        double bestScore = 0;

        /* Why not, in the order the floors are applied - so the refusal names
         * the wall the sector actually hit rather than the last one it tried. */
        double bestFill = 0;
        String fullest = null;
        String unprofitable = null;
        boolean unprofitableEatsMeat = false;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {

            double fill = fillFor(t);
            if (fill < FILLED) {
                if (fill >= bestFill) { bestFill = fill; fullest = t.getName(); }
                continue;
            }

            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double income = estimatedMonthlyProfit(t, plans);
            if (income <= 0) {
                if (unprofitable == null) {
                    unprofitable = t.getName();
                    unprofitableEatsMeat = t.goodsUsed().containsKey(Good.MEAT);
                }
                continue;
            }

            // Income over what it costs to put up, which is the score every
            // other maker in the game is chosen by.
            double score = income / cost;
            if (score > bestScore) { bestScore = score; best = t; }
        }

        if (best == null) {
            if (unprofitable != null) {
                return BusinessInvestment.Decision.no(sector, unprofitableEatsMeat
                        ? String.format("the city eats what a %s makes, but at %s a kilo "
                                + "for meat there is nothing left in it after the wages",
                                unprofitable, Formats.INSTANCE.cash(getMeatPrice()))
                        : String.format("a %s would not clear its own costs at the price "
                                + "it would get for what it makes", unprofitable));
            }
            if (fullest != null) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "a %s would run at %.0f%% - the rest would go abroad at the floor, "
                        + "where this trade does not pay", fullest, bestFill * 100));
            }
            return BusinessInvestment.Decision.no(sector, "nothing in the catalogue to build");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                "the city already eats what one would make", true);
    }

    /* =====================================================================
       WHAT A PLANT WOULD EARN, AT THE PRICE IT WILL LEAVE BEHIND

       BusinessInvestment.estimatedMakerProfit() is honest arithmetic on two
       figures that are wrong for this sector, and both were measured rather
       than argued.

       ONE: IT PRICES THE OUTPUT AT TODAY'S PRICE, AND THIS PLANT IS WHAT
       MOVES IT. A good nobody here makes stands at the top of its band -
       GoodsMarket.strike() puts the position at 1 when supply is zero, which
       is the import ceiling, correctly, because every kilo on the shelf came
       off a ship. The first plant is then valued at the ceiling it is about
       to destroy. Measured twice. In a run: processed meat stood at $8.90 and
       ready meals at $7.90 the month the first Meat Works was financed, and
       $7.70 and $7.10 the month it opened - a third of the margin, gone to the
       plant's own arrival, against a plan struck on the higher pair. And in
       the harness, on a city of 2,935 people the month it wants its first
       Bottling Plant: the generic estimate says $58,621 a month, this one says
       $37,060. The difference is not an opinion about the future, it is the
       price the plant itself will leave behind.

       The band is closed-form, so the settled price is too: position is
       demand / (demand + supply), and the supply this plant would add is its
       nameplate. Pricing it there is not a forecast, it is the same equation
       the market will run next month with one more term in it.

       TWO: IT IGNORES THE SALES TAX, AND THIS SECTOR PAYS IT ON REVENUE. The
       ledger is a VAT: tax on what you sell, credit for the tax your
       SUPPLIERS remitted (SalesTaxLedger). An import carries no such credit -
       chargeImport() puts the same figure on both sides of the row, which
       nets to nothing - because no foreign seller remitted anything, and that
       is the design: "a sector cannot undercut a local supplier by buying
       from outside." Every other maker here buys at home and the credit takes
       most of the sting out. THIS ONE BUYS MEAT, and a city with no herds
       imports every gram of it.

       Measured: a Meat Works on imported meat, month 72, revenue $35k,
       operating income $1k, SALES TAX $5k. The tax was five times the profit
       and the generic estimate could not see it at all. That is the whole of
       why the sector went bankrupt with a plant standing and the planner
       still wanting another.

       WHAT IT MEANS FOR THE PLAYER, and it is the point of the sector: the
       Meat Works is a bet on the meat price. Measured at month 98 of a real
       city, the same building is worth $1,678 a month on meat off a ship and
       $16,559 on meat at a farm's export floor, with the farms' sales-tax
       credit behind it. Ten times over, on one price. The building did not
       change. The FARMS did.
       ===================================================================== */

    /** Whoever else in the city makes this, or null when only the world does. */
    private Sector makerOf(Good g) {
        if (game == null || game.getSectors() == null) return null;
        for (Sector s : game.getSectors().all()) {
            if (s != this && s.goodsMade().contains(g)) return s;
        }
        return null;
    }

    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (markets == null || t == null) return 0;

        double revenue = 0, localSales = 0;
        for (java.util.Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
            Good g = e.getKey();
            if (!g.traded()) continue;
            GoodsMarket m = markets.get(g);
            double made = e.getValue();
            double running = getCapacity(g) + getPipeline(g);
            double demand = Math.max(0, m.getDemandTrend());
            double supply = running + made;

            double lo = Math.max(0, m.floor()), hi = Math.max(lo, m.ceiling());
            double position = (demand + supply) > 0 ? demand / (demand + supply) : .5;
            double settled = lo + (hi - lo) * position;

            double atHome = Math.min(made, Math.max(0, demand - running));
            double abroad = g.exportable() ? made - atHome : 0;
            revenue += atHome * settled + abroad * lo;
            localSales += atHome * settled;
        }

        double inputs = 0, credited = 0;
        TaxPolicy tax = game == null ? null : game.getEconomyManager().getTaxPolicy();
        for (java.util.Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            Good g = e.getKey();
            if (!g.traded()) continue;
            GoodsMarket m = markets.get(g);
            double bill = e.getValue() * m.getLocalPrice();
            inputs += bill;
            /*
             * AND HOW MUCH OF IT THE CITY COULD ACTUALLY SUPPLY. Only the
             * local part carries a credit, and the split is not a guess: the
             * market records what it filled at home and what it shipped in
             * last month. A city with no farms credits nothing, which is the
             * case this whole override exists for.
             */
            Sector supplier = makerOf(g);
            double served = m.getLocalFilled() + m.getImported();
            double localShare = served > 0 ? m.getLocalFilled() / served : 0;
            if (tax != null && supplier != null) {
                credited += bill * localShare * tax.effectiveSalesRate(supplier);
            }
        }

        double rate = BusinessInvestment.operatingRateOf(getOperatingRate());
        double vat = tax == null ? 0 : Math.max(0, localSales * tax.effectiveSalesRate(this) - credited);

        return (revenue - inputs) * rate
                - plans.runningCostOf(t) - plans.standingCostOf(this, t) - vat;
    }

    /* ------------------------------------------------------------ the screen */

    /**
     * What the generic page cannot say: WHICH PRICE IS DECIDING THIS SECTOR.
     *
     * The five goods' own blocks are above, drawn by Sector.operations(), and
     * they are the sector's output. None of them explains the sector. The Meat
     * Works is a bet on the meat price - ten times the monthly income between
     * a herd and a ship, measured - and a player looking at a Meat Works that
     * loses money has no way to see that from a page about sausages.
     *
     * Same shape as Manufacturing's, which has the same problem one chain over
     * and solved it the same way: where the input comes from, and whether the
     * two costs that can eat a plant still leave anything.
     */
    @Override
    public java.util.List<Sector.Line> operations(Game game) {
        java.util.List<Line> lines = super.operations(game);
        if (game == null || markets == null) return lines;
        Formats f = Formats.INSTANCE;

        double standing = 0;
        for (Good g : goodsMade()) standing += getCapacity(g);
        if (standing <= 0) {
            lines.add(Line.note("Nothing standing, and a third of the shelf by weight is coming "
                    + "in by ship - the sausages, the ready meals, the crisps, the cooking oil "
                    + "and the drinks. Two of the three plants run on crops this city can grow. "
                    + "The third runs on meat, and it is only worth building where there are "
                    + "herds: the world sells meat dearer than it buys sausage."));
            return lines;
        }

        lines.add(Line.head("Where the meat comes from"));
        double position = getMeatPosition();
        lines.add(Line.of("Meat, a kilo", f.cash(getMeatPrice()),
                position < .34 ? Line.Tone.GOOD : position < .67 ? Line.Tone.NONE : Line.Tone.WARN));
        lines.add(Line.of("Wanted this month", f.units(getMeatDemand(), Good.MEAT)));
        lines.add(Line.note(position >= .67
                ? "Near what it costs to land a kilo, which is what a city with no herds pays. "
                + "A Meat Works buying at this price is working for the shipper."
                : position < .34
                ? "Near what a farm gets shipping it out - the herds have nowhere else to go and "
                + "the works is getting the benefit. This is when a Meat Works pays."
                : "In the middle of the band, which is a farm and a meat works both doing better "
                + "than they would trading with the world."));

        lines.add(Line.head("Whether the plants are clearing their costs"));
        double inputs = inputShare(), pay = payrollShare(), both = inputs + pay;
        Line.Tone tone = both <= 0 ? Line.Tone.MUTED
                : both < .80 ? Line.Tone.GOOD
                : both < 1.0 ? Line.Tone.WARN
                : Line.Tone.BAD;
        lines.add(Line.of("What it bought, as a share of what it sold", f.pct(inputs)));
        lines.add(Line.of("Wages, as a share of what it sold", f.pct(pay)));
        lines.add(Line.of("The two together", f.pct(both), tone));
        /*
         * AND THE THIRD COST, WHICH IS THE ONE NOBODY EXPECTS. The sales tax is
         * a VAT and an import carries no credit, so a plant fed from abroad
         * pays it on its whole ticket rather than on what it added. On one
         * measured month that was $5k against $1k of operating income. It is
         * shown whenever it is real, because a page that lists two costs when
         * three are eating the plant explains nothing.
         */
        double revenue = statement().revenue;
        if (revenue > 0 && statement().salesTax != 0) {
            double taxShare = statement().salesTax / revenue;
            /*
             * AND THE THRESHOLD IS THE PLAYER'S OWN RATE, NOT A FIGURE.
             *
             * The first draft warned above twelve percent, which is a constant
             * in a quantity the player sets: the sales rate is the income tax
             * rate plus this sector's offset and it has run from 12% to 30% in
             * a single game. At 12% income tax nothing would ever warn, and at
             * 30% a sector buying entirely at home and paying on a third of
             * its ticket would warn every month.
             *
             * What the line is actually about is whether the CREDITS are
             * working, so the yardstick is the rate itself: paying three
             * quarters of the full rate on revenue means three quarters of
             * what this sector buys came off a ship. Same lesson as every
             * money constant in this project - a bound that is not a ratio of
             * the thing it bounds is a bug waiting for a policy change.
             */
            double rate = game.getEconomyManager().getTaxPolicy().effectiveSalesRate(this);
            boolean uncredited = taxShare > rate * .75;
            lines.add(Line.of("Sales tax, as a share of what it sold", f.pct(taxShare),
                    uncredited ? Line.Tone.WARN : Line.Tone.NONE));
            lines.add(Line.note(uncredited
                    ? String.format("Nearly the whole %s rate, on everything sold rather than "
                    + "on what these plants added - because the credit is for tax a supplier "
                    + "HERE already remitted and nothing abroad remits anything. Farms in this "
                    + "city would cut this line, not just the meat price.", f.pct(rate))
                    : String.format("Well under the %s rate, which means the credits are "
                    + "working: these plants are paying on what they added and the farms' "
                    + "share was already paid by the farms.", f.pct(rate))));
        }
        if (both >= 1.0) {
            lines.add(Line.note(inputs > pay
                    ? "What the plants buy costs more than what they sell fetches. Either the "
                    + "farms here have other buyers or there are no farms here."
                    : "The city costs more than the work is worth. Either wages here have risen "
                    + "or the currency has."));
        } else if (both >= .80) {
            lines.add(Line.note("Thin. These plants are built to run about sixty-five percent on "
                    + "the two together - a Meat Works is 40% meat and 22% wages at mid-band - "
                    + "and the tax and the interest come out of what is left."));
        }
        return lines;
    }

    /** Kilograms of meat the plants want this month, at the rate they are running. */
    public double getMeatDemand() {
        return getInputAtCapacity(Good.MEAT) * getOperatingRate();
    }

    /** What a kilogram is costing them - the number that decides whether a Meat Works pays here. */
    public double getMeatPrice() {
        return markets == null ? 0 : markets.get(Good.MEAT).getLocalPrice();
    }

    /**
     * Where the meat price sits between a farm's export floor and the world's
     * delivered ceiling: 0 is a city with herds and nobody else to sell to,
     * 1 is a city with no farms at all.
     */
    public double getMeatPosition() {
        return markets == null ? 0 : markets.get(Good.MEAT).getPriceIndex();
    }

    /** The input bill as a share of what the plants sold. */
    public double inputShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().inputs / revenue : 0;
    }

    /** ...and the wage bill, the same way. The other one that can eat a plant. */
    public double payrollShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().payroll / revenue : 0;
    }

    /**
     * Spare capacity, measured in money rather than in kilograms.
     *
     * All five are counted by the kilo, so adding them WOULD type-check - and
     * it would weigh twelve kilos of drinks the same as one kilo of snacks,
     * which is eight times the money. Each good's demand and capacity is valued
     * at its own export floor first, the same yardstick Agriculture uses and
     * for the same reason.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        double demand = 0, capacity = 0;
        for (Good g : goodsMade()) {
            double floor = markets == null ? 0 : Math.max(0, markets.get(g).exportPrice());
            if (floor <= 0) continue;
            double trend = markets.get(g).getDemandTrend();
            demand   += Math.max(plannedDemand(g), trend) * floor;
            capacity += getCapacity(g) * floor;
        }
        return new double[] { demand, capacity };
    }
}

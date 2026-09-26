package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Markets;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Trade;

import java.util.List;
import java.util.Map;

/**
 * The kitchens. THE FIFTEENTH SECTOR (2026-09-18, Jerus's call).
 *
 * Jerus: "lets add restuarants as well as luxury stores, these two will absorb
 * some spending as well" - and then the rule that makes this one different
 * from a second boutique: *a meal out REPLACES groceries*.
 *
 * =========================================================================
 * SO IT IS GROCERIES' SIBLING, NOT LUXURIES'
 * =========================================================================
 *
 * A watch is imported. The world has as many as the city will pay for, so
 * LuxuryRetail is short of nothing but counters and the money it absorbs
 * leaves the country. A dinner cannot be imported. The food in it is the
 * thirteen things already on the shop shelf, bought in the same market at the
 * same prices, so a restaurant brings in no kilogram that was not there.
 *
 * THAT IS NOT A LIMITATION, IT IS THE MECHANIC. A city whose shops already
 * cannot reach everybody now has a second buyer in the same food market, and
 * the price says so. Jerus, on the supply wall: *"let the basket get dearer,
 * not bigger"*, and *"supply v demand, thats the most important thing"*. This
 * is where that bites.
 *
 * =========================================================================
 * WHAT A RESTAURANT ACTUALLY ADDS
 * =========================================================================
 *
 * Two things, and neither is food.
 *
 * ONE: A SECOND DOOR. Retail's binding constraint is not stock, it is
 * COVERAGE times the operating rate - people the shops can physically serve.
 * A city at the wall can feed some of its people through a kitchen instead,
 * and the hunger measure sees it, because a meal is a meal wherever it was
 * cooked. See HouseholdBalance.advanceMonth(), where meals eaten are added to
 * what a household ate before it is compared against subsistence.
 *
 * TWO: SOMEWHERE FOR THE MONEY TO GO. A meal out costs a multiple of what the
 * same food costs at home, and the multiple is the sector's whole revenue.
 * That is the absorption Jerus asked for, and unlike the luxury shops' it
 * circulates: the margin is wages and rent HERE.
 *
 * =========================================================================
 * A MEAL IS ONE NINETIETH OF A PERSON-MONTH
 * =========================================================================
 *
 * Three a day, thirty days. The number is not tuned to anything - it is what
 * a month of eating is - and it is the conversion every arithmetic in this
 * sector and in the household ledger runs through. Coverage is in MEALS,
 * groceries are in person-months, and reading one as the other is how a Diner
 * would look ninety times the business it is.
 */
public class Restaurants extends Sector {

    /* =====================================================================
       WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING

       Three meals a day, thirty days. A household that ate out every night
       for a month would buy thirty of these and cover a third of one person's
       food - which is about what eating out every night actually is.

       AND THE CATALOGUE IS SIZED THROUGH IT. A Diner's 13,500 meals is a
       hundred and fifty person-months of food, which at the founding city's
       prices is a $29.6k food bill against a $30.8k payroll - the ratio the
       trade actually runs on. See the note in BuildingManager for the first
       pass, which had the same eight staff cooking a fifth of that.
       ===================================================================== */

    /** Meals one person eats in a month: three a day, thirty days. */
    public static final double MEALS_A_PERSON_MONTH = 90;

    /** ...and the same fact the other way up, which is what the arithmetic wants. */
    public static final double PERSON_MONTHS_PER_MEAL = 1 / MEALS_A_PERSON_MONTH;

    /* =====================================================================
       THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S

       LuxuryRetail's floor is 1.25x because a quiet shop still sells a watch
       at something over what it paid, and its costs are a counter and a till.
       A restaurant's costs are PEOPLE: the catalogue's two kitchens run
       payroll at about a third of revenue, against food at another third. A
       kitchen charging 1.25x the food would not make the wages, so a quiet
       restaurant does not charge it - it charges what it must and waits.

       Both ends are multiples of what the food cost, for the money-constant
       reason this project has found twenty-six instances of: a bound written
       in absolute money is a bug waiting for a reform.

       AND THE BAND IS WIDER THAN THE TRADE'S OWN 2.9x-3.6x, because the trade's
       own figure is for a place where food and labour each take about a third
       of the ticket, and this city is not one. Measured at month 4,002 of the
       default playtest: a kitchen's food bill was $8.9k a month against $34.1k
       of wages - labour at FOUR TIMES food, not at parity - because three
       centuries of farms and food plants made food cheap while the wage
       schedule did not move. Where labour is dear relative to food, the food
       cost percentage has to fall, which is the same statement as the markup
       having to rise. A first band of 2.5x-5.0x left the sector losing $18.8k
       a month with its margin PINNED AT THE CEILING and 4.8 million diners
       queuing, which is a calibration saying no to a business the city was
       screaming for.
       ===================================================================== */

    /** What a kitchen with empty tables charges over what the food cost it. */
    public static final double MARGIN_FLOOR = 3.0;

    /** ...and what a kitchen with a queue at the door charges. */
    public static final double MARGIN_CEILING = 8.0;

    /** What one meal sells for this month. Struck, not quoted from a table. */
    private double sellPrice;

    /** The month's reading, for the screen and the harness. */
    private double rMargin = MARGIN_FLOOR, rWanted, rServed, rSeats, rFoodCost;

    /**
     * Kilograms of each good in one person-month, handed in by Game.
     *
     * The SAME basket Retail is given, from the same call, and that is the
     * point: a meal out replaces groceries, so it has to be made of the food
     * groceries are made of. See Game.cityBasketPerHead().
     */
    private final Map<Good, Double> basket = new java.util.EnumMap<>(Good.class);

    public Restaurants() {
        super("Restaurants", "Restaurants", BuildingType.HOSPITALITY);
        makes(Good.MEALS);
        /*
         * ONE MONTH IN THE WALK-IN, where a shop keeps three. Food goes off,
         * and a kitchen that held a quarter's dinners would be a kitchen
         * throwing most of them out. It also means a restaurant feels a food
         * shortage in weeks rather than in seasons, which is the honest
         * behaviour for a business with no warehouse.
         */
        for (Good g : Retail.SHELF) pantry(g, KITCHEN_COVER_MONTHS);

        blurb("Buys the same thirteen foods the shops buy and serves them cooked. "
                + "Adds no food to the city: what it adds is a second door to the "
                + "food there is, and somewhere for a rich city to spend.");
    }

    /** Months of food a kitchen keeps. A shop keeps three; see the constructor. */
    public static final double KITCHEN_COVER_MONTHS = 1;

    /** What one person-month costs in kilograms, by good. Game hands this in. */
    public void setBasket(Map<Good, Double> kgPerHead) {
        basket.clear();
        if (kgPerHead == null) return;
        for (Map.Entry<Good, Double> e : kgPerHead.entrySet()) {
            if (e.getKey() != null && e.getValue() != null && e.getValue() > 0) {
                basket.put(e.getKey(), e.getValue());
            }
        }
    }

    /** Kilograms of one good in one MEAL. Zero for anything not on the shelf. */
    public double kgPerMeal(Good g) {
        return basket.getOrDefault(g, 0.0) * PERSON_MONTHS_PER_MEAL;
    }

    /* ===================================================================
       THE SALE
       =================================================================== */

    /** Meals the kitchens can serve a month, off their buildings. */
    public int seats() {
        return buildings == null ? 0
                : (int) Math.round(buildings.totalBySector(key(), b -> b.getCoverage()));
    }

    /**
     * Meals the larder can actually put out: the ingredient that runs out
     * first, exactly as Retail.basketsOnShelf() asks the same question of a
     * shop's shelf. A kitchen with a tonne of grain and no meat cannot serve
     * the menu.
     */
    public double mealsInTheLarder() {
        if (basket.isEmpty()) return 0;
        double least = Double.MAX_VALUE;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            double perMeal = e.getValue() * PERSON_MONTHS_PER_MEAL;
            if (perMeal <= 0) continue;
            least = Math.min(least, getPantry(e.getKey()) / perMeal);
        }
        return least == Double.MAX_VALUE ? 0 : least;
    }

    /** What the food in one meal cost the kitchen, at the market's prices today. */
    public double foodCostOfAMeal(Markets markets) {
        if (markets == null || basket.isEmpty()) return 0;
        double sum = 0;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            double price = Math.max(0, markets.get(e.getKey()).getLocalPrice());
            sum += e.getValue() * PERSON_MONTHS_PER_MEAL * price;
        }
        return sum;
    }

    public double getMargin()    { return rMargin; }
    public double getWanted()    { return rWanted; }
    public double getServed()    { return rServed; }
    public double getSeats()     { return rSeats; }
    public double getFoodCost()  { return rFoodCost; }
    public double getSellPrice() { return sellPrice; }

    /**
     * Strikes the margin against the queue and returns what a meal costs this
     * month. Nothing is served yet - the households have to be asked at this
     * price before they can answer.
     *
     * THE TWO-PASS SHAPE IS THE HOUSE'S, for the reason written at
     * HouseholdBalance.clearUsedCars() and used again for the boutiques:
     * demand depends on the price and the price depends on demand, and every
     * market in this game breaks that circle the same way. LuxuryCounter.dine()
     * runs the two halves.
     *
     * @param wanted meals the households came for, before any cap
     */
    public double strikeMargin(Markets markets, double wanted) {
        int seats = seats();
        rSeats = seats;
        rWanted = Math.max(0, wanted);
        wanted = rWanted;

        double food = foodCostOfAMeal(markets);
        rFoodCost = food;

        /*
         * THE POSITION, ON GoodsMarket.strike()'s OWN TERMS: empty tables are
         * a kitchen at its floor, a queue at the door is a kitchen at its
         * ceiling, and with no kitchens at all there is nothing to strike.
         */
        double position = wanted + seats <= 0 ? 0 : wanted / (wanted + seats);
        rMargin = MARGIN_FLOOR + (MARGIN_CEILING - MARGIN_FLOOR) * position;
        sellPrice = food * rMargin;

        rServed = 0;
        return sellPrice;
    }

    /**
     * ...and serves what the tables and the larder can actually get through.
     *
     * THE THREE CAPS, each a different thing a city can be short of: meals the
     * kitchens can serve at all, the share of them they can reach with the
     * power and the streets and the staff they have, and food in the walk-in.
     *
     * @return meals served
     */
    public double serve(Markets markets, double meals) {
        int seats = seats();
        double servable = Math.min(Math.max(0, meals), seats) * getOperatingRate();
        // Whole meals: a quantity crossing from the money world into the
        // physical one crosses at a grain coarser than the dust. Nobody is
        // served four fifths of a dinner.
        double sold = Math.floor(Math.min(servable, mealsInTheLarder()));
        rServed = sold;
        noteShelfShort(Math.floor(servable) - sold, sellPrice);
        if (sold <= 0) return 0;

        Trade t = markets.get(Good.MEALS).record(key(), Trade.HOUSEHOLDS, sold, sellPrice);
        bookSale(t);
        /*
         * ...AND THE LARDER COMES DOWN BY WHAT WAS COOKED, over the whole
         * basket rather than over what happened to run low - the same loop
         * Retail runs over its shelf, and for the reason written there: a good
         * left out keeps last month's recentUse() for ever and the kitchen
         * restocks a line it stopped serving.
         */
        for (Good g : Retail.SHELF) usePantry(g, sold * kgPerMeal(g));
        return sold;
    }

    /**
     * What to restock against, the month-one fallback included.
     *
     * THE BOOTSTRAP EVERY NEW SECTOR IN THIS GAME HAS HIT, and it cost
     * LuxuryRetail a whole run to find: the pantry restocks against what was
     * USED, and a kitchen that has never served anything has used nothing - so
     * it orders nothing, so it has nothing to cook, so it uses nothing. A
     * kitchen with no service to go on buys for every seat it could fill.
     */
    @Override
    protected double recentUse(Good g) {
        double perMeal = kgPerMeal(g);
        if (perMeal <= 0) return super.recentUse(g);
        /*
         * A KITCHEN STOCKS FOR THE TABLES IT EXPECTS TO FILL, which is the
         * smaller of its own and the queue at the door - not for what it
         * managed last month.
         *
         * THE DIFFERENCE IS A TRAP, and it is the bootstrap deadlock one step
         * further on. "What I used last month" is self-limiting: a kitchen
         * throttled to half its tables by a bad road buys half a month of
         * food, which lets it serve half a month next time, for ever, with no
         * way back however long the queue gets. The first month is a guess and
         * every month after is a fact only while the fact is not the thing
         * doing the limiting.
         */
        double meals = Math.min(seats(), Math.max(rWanted, rServed));
        if (meals <= 0) meals = seats();
        return meals * perMeal;
    }

    /* ===================================================================
       PLANNING - the queue at a door that is not there
       =================================================================== */

    /**
     * Builds against the diners who CAME, not against a sales record.
     *
     * Same reason as LuxuryRetail's, measured there before it was written
     * here: planMaker() scores a sector on what it has been selling, and a
     * sector with no kitchens has been selling nothing. The signal is the
     * queue, and LuxuryCounter.dine() measures it at the margin's FLOOR whether or
     * not there is anywhere to eat.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        int seats = seats();
        double queue = rWanted;
        if (queue <= seats * (1 + BusinessInvestment.TARGET_HEADROOM)) {
            return BusinessInvestment.Decision.no(sector, "tables ahead of diners");
        }
        if (!(sellPrice > rFoodCost)) {
            return BusinessInvestment.Decision.no(sector, "no margin in it");
        }

        double output = game.getConstructionOutput();
        BuildingsTemplate best = null;
        double bestScore = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getCoverage() <= 0) continue;
            /*
             * WHAT A TABLE EARNS IS THE MARGIN, not the ticket. A kitchen that
             * sells a thirty-dollar dinner made of ten dollars of food has
             * twenty dollars to pay its staff with, and scoring on the thirty
             * would value it at three times what it is worth.
             */
            double monthlyIncome = t.getCoverage() * (sellPrice - rFoodCost);
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }
        if (best == null) return BusinessInvestment.Decision.no(sector, "nothing worth building");

        int quantity = plans.orderSize(queue - seats, best.getCoverage(), best, output);
        if (quantity <= 0) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));

        return new BusinessInvestment.Decision(sector, best, quantity,
                String.format("%,.0f meals a month came against %,d the kitchens can serve", queue, seats),
                true);
    }

    /**
     * What one more kitchen would earn a month.
     *
     * THE SECTOR HAS TO VALUE ITS OWN, because the generic estimate reads a
     * maker's sales and this sector has none until the first kitchen opens.
     * It is not enough for plan() to want a Diner: consider() checks that the
     * build services its own debt and asks this method what it earns.
     *
     * THE MARGIN, NOT THE TICKET, and net of what it costs to run - which for
     * a kitchen is mostly the staff, and is most of the margin.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || t.getCoverage() <= 0) return 0;
        double food = rFoodCost;
        if (!(food > 0)) return 0;
        double price = sellPrice > 0 ? sellPrice : food * MARGIN_FLOOR;
        // Only the diners the tables would actually see: a Diner in a city
        // with a thousand empty seats serves nobody.
        double served = Math.min(t.getCoverage(), Math.max(0, rWanted - seats()));
        if (served <= 0) served = t.getCoverage() * .5;
        return served * (price - food) - plans.runningCostOf(t);
    }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    @Override
    public List<Line> operations(Game game) {
        List<Line> lines = new java.util.ArrayList<>();
        Formats f = Formats.INSTANCE;

        lines.add(Line.head("THE TABLES"));
        lines.add(Line.of("Meals the kitchens can serve", f.count(rSeats)));
        lines.add(Line.of("...that were asked for", f.count(rWanted),
                rWanted > rSeats ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("...and that were served", f.count(rServed)));
        if (rServed > 0) {
            lines.add(Line.of("People fed for a month by that",
                    f.count(rServed * PERSON_MONTHS_PER_MEAL)));
        }
        if (rWanted > rSeats + .5) {
            lines.add(Line.note(f.count(rWanted - rSeats)
                    + " meals a month want a kitchen that is not there. A city short of"
                    + " tables is a city eating at home whether it wants to or not."));
        }

        lines.add(Line.head("THE MARGIN, STRUCK AGAINST THE QUEUE"));
        lines.add(Line.of("What the food in a meal cost", f.cash(rFoodCost)));
        lines.add(Line.of("...and what the meal sold for", f.cash(sellPrice)));
        lines.add(Line.of("The mark-up", String.format("%.2fx", rMargin),
                rMargin > (MARGIN_FLOOR + MARGIN_CEILING) / 2 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.note(String.format(
                "An empty kitchen charges %.2fx what the food cost and a full one %.2fx."
                + " Most of that is the staff: this is the most labour-heavy business in"
                + " the catalogue, which is what a restaurant is.",
                MARGIN_FLOOR, MARGIN_CEILING)));

        lines.add(Line.head("THE WALK-IN"));
        lines.add(Line.of("Meals the larder could cover", f.count(mealsInTheLarder())));
        lines.add(Line.note(String.format(
                "A meal is one %.0fth of a person-month - three a day, thirty days - and"
                + " the kitchens buy the same thirteen foods the shops buy, in the same"
                + " market, at the same prices. They add no food to this city. What they"
                + " add is a second door to the food it has.", MEALS_A_PERSON_MONTH)));
        return lines;
    }
}

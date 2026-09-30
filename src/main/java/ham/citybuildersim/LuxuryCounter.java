package ham.citybuildersim;

/**
 * The households' discretionary spending: the boutiques and the restaurants,
 * each striking its price against the queue.
 *
 * ==================== THE LUXURY COUNTER (2026-09-17) ====================
 *
 * Jerus: "lets add restuarants as well as luxury stores, these two will
 * absorb some spending as well", and on the pricing, "supply v demand,
 * thats the most important thing."
 *
 * THE SHAPE IS Motoring.month()'s, and deliberately: ask the seller a price, ask
 * the households what they will take at it, hand over what the seller
 * actually had, take the money. What is different is that the seller here
 * strikes its price against the QUEUE rather than reading it off a market
 * band - the world has no shortage of watches, so the scarce thing is the
 * shop, and the shop is what a player builds. See LuxuryRetail. Since 0.7.19
 * the boutiques' queue is the households who would buy at the price they
 * charge, struck as one fixed point (LuxuryRetail.strikeMargin()); the
 * kitchens still ask at the floor and then at the price (dine()).
 *
 * ==================== WHERE IT CAME FROM ====================
 *
 * This was Game's "THE LUXURY COUNTER" section until 2026-09-18, when it
 * moved out with its text intact: the eight month flows and their getters,
 * luxuryShopping() as shop() and diningOut() as dine(). Game keeps the eight
 * getters as delegations so that no caller changed, and calls the two in the
 * same order and the same place in the month it always did.
 *
 * WHY IT LEFT. Game.java was 8,200 lines, and a session reading the luxury
 * counter had to carry the month, the save and the treasury with it. A
 * mechanic that has the shape of a class - its own month, its own figures,
 * one place it is called from - is its own file since 2026-09-18, and Game
 * keeps what every reader goes through: the getters, and the order of the
 * month. The interface went the same way the same day. See the project's
 * splitting-game.md.
 */
public final class LuxuryCounter {

    private double luxuriesSold, luxurySpend, luxuryPrice, luxuryWanted;
    private double mealsServed, mealSpend, mealPrice, mealsWanted;

    /** Meals the kitchens served the households this month. */
    public double getMealsServed()  { return mealsServed; }

    /** ...and what the households paid for them. */
    public double getMealSpend()    { return mealSpend; }

    /** ...at this price a meal, struck against the queue at the door. */
    public double getMealPrice()    { return mealPrice; }

    /** ...against this many meals they came for. */
    public double getMealsWanted()  { return mealsWanted; }

    /**
     * The month's dining out. Was Game.diningOut(); the body is that one, through Game's getters.
     *
     * THE SHAPE WAS shop()'s until 0.7.19, and still is here: ask the
     * households at the margin's FLOOR to measure the queue, strike the
     * margin on it, ask again at what was struck, serve what the tables and
     * the larder allow, take the money. The boutiques now strike on the
     * buyers at the price they charge (LuxuryRetail.strikeMargin()); Jerus's
     * choice was for Luxury Retail's markup, and the kitchens keep theirs.
     *
     * WHAT IS DIFFERENT IS THE FOOD. A boutique's stock comes off a ship and
     * the city can have as much as it will pay for; a kitchen's comes out of
     * the same thirteen markets the shops buy in, so serving a dinner takes
     * food off a shelf somebody else was going to eat from. That is the
     * pressure, and it is the point.
     */
    void dine(Game game) {
        HouseholdBalance householdBalance = game.getHouseholdBalance();

        mealsServed = 0;
        mealSpend = 0;
        mealPrice = 0;
        mealsWanted = 0;

        ham.citybuildersim.sectors.Restaurants kitchens = game.getSectors().restaurants();
        if (kitchens == null) return;

        double food = kitchens.foodCostOfAMeal(game.getMarkets());
        if (!(food > 0)) return;
        double atTheFloor = food * ham.citybuildersim.sectors.Restaurants.MARGIN_FLOOR;
        mealsWanted = householdBalance.mealsWanted(atTheFloor);

        double price = kitchens.strikeMargin(game.getMarkets(), mealsWanted);
        if (!(price > 0)) return;
        mealPrice = price;

        // ...and now at what was actually struck, which is fewer than came at
        // the floor: a dear kitchen is a kitchen some people walk past.
        double affordable = householdBalance.mealsWanted(price);
        if (!(affordable > 0)) return;

        double served = kitchens.serve(game.getMarkets(), affordable);
        if (!(served > 0)) return;

        mealsServed = served;
        mealSpend = householdBalance.takeMeals(served, price);
    }


    /** Pieces the households bought over a counter this month. */
    public double getLuxuriesSold()  { return luxuriesSold; }

    /** ...what they paid for them... */
    public double getLuxurySpend()   { return luxurySpend; }

    /** ...what one went for... */
    public double getLuxuryPrice()   { return luxuryPrice; }

    /** ...and how many would buy at the price the shops struck (0.7.19; at the margin's floor until then), which in a city short of shops is more than were served. */
    public double getLuxuryWanted()  { return luxuryWanted; }

    /** The month's shopping. Was Game.luxuryShopping(), and the body was that one, through Game's getters, until 0.7.19 struck the margin on the buyers at its price. */
    void shop(Game game) {
        HouseholdBalance householdBalance = game.getHouseholdBalance();

        luxuriesSold = 0;
        luxurySpend = 0;
        luxuryPrice = 0;
        luxuryWanted = 0;

        ham.citybuildersim.sectors.LuxuryRetail shops = game.getSectors().luxuryRetail();
        if (shops == null) return;

        /*
         * THE SHOPPERS WHO BUY AT THE PRICE ACTUALLY CHARGED (0.7.19). The
         * queue was asked at the margin's FLOOR until then - the largest
         * reading of who came, at a price nobody is charged - and the margin
         * struck on it. Now the margin is struck on the households who would
         * buy at the margin it strikes, which is one question with one answer
         * (LuxuryRetail.strikeMargin()): the demand curve is handed over and
         * the shops find the margin its buyers put them at. What a piece costs
         * the shop is read inside, the way it always was (landedCost()).
         */
        double price = shops.strikeMargin(game.getMarkets(), householdBalance::luxuriesWanted);
        luxuryWanted = shops.getWanted();
        if (!(price > 0)) return;
        luxuryPrice = price;

        // ...and they are the ones sold to, as far as the counters, the staff
        // and the shelf reach (serve()).
        double affordable = luxuryWanted;
        if (!(affordable > 0)) return;

        double sold = shops.serve(game.getMarkets(), affordable);
        if (!(sold > 0)) return;

        luxuriesSold = sold;
        luxurySpend = householdBalance.takeLuxuries(sold, price);
    }
}

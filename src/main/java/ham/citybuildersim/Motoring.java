package ham.citybuildersim;

/**
 * The households' car market: the second-hand pass, then the showroom, with
 * the road told what is parked on it.
 *
 * ==================== THE HOUSEHOLDS BUY CARS (2026-09-16) ====================
 *
 * The fifth link, and the first time in this game that a household has
 * bought anything except food, a roof and a share.
 *
 * WHY IT IS HERE AND NOT IN THE MARKET PASS. Cars clear in the band like
 * everything else, but the households are not a sector and cannot bid in
 * a strike. They take what they want off the makers' shelf at the price
 * the market struck, and import the rest - which is exactly what the CITY
 * does for building material, through the same Markets.draw(). The units
 * count as demand for next month's strike (see Markets.noteDrawn), so the
 * price answers with a month's lag, as materials' does.
 *
 * WHY THE TOP OF THE MONTH. Savings are fresh - Game.updateHouseholdAccounts()
 * settled them, the call before this one in startOfMonthUpdate() - and the
 * makers' shelf holds what last month's production put there. A household
 * buying before the export market opens is domestic demand getting first
 * refusal on a domestic car, which is the right way round and is what the
 * city's own materials draw already does.
 *
 * THE PRICE IS QUOTED BEFORE IT IS CHARGED, and that is not a nicety. The
 * shelf is finite: past it every car is an import at the ceiling, so a
 * month that buys more than the city made costs more per car than the
 * market price says. Sizing the demand at the local price and settling it
 * at the blended one would have taken money the households had not agreed
 * to spend - and the clamp that stops savings going negative would have
 * swallowed the difference silently, which is money from nowhere wearing a
 * safety guard. So: quote at what they want, re-ask at what that would
 * cost, buy that, and settle at the blend of what was actually taken -
 * which is never dearer than the figure they were re-asked at, because
 * fewer cars means a smaller imported share.
 *
 * ==================== WHERE IT CAME FROM ====================
 *
 * This was Game's "THE HOUSEHOLDS BUY CARS" section until 2026-09-18, when
 * it moved out with its text intact: the nine month flows and their getters,
 * and motoring() as month(). The ownership rate carried across a load
 * (Game.carriedCarOwnership) stayed behind, because the load path reads it.
 * Game keeps the nine getters as delegations so that no caller changed.
 *
 * WHY IT LEFT. Game.java was 8,200 lines, and a session reading the car
 * market had to carry the month, the save and the treasury with it. A
 * mechanic that has the shape of a class - its own month, its own figures,
 * one place it is called from - is its own file since 2026-09-18, and Game
 * keeps what every reader goes through: the getters, and the order of the
 * month. The interface went the same way the same day. See the project's
 * splitting-game.md.
 */
public final class Motoring {

    private double householdCarsBought;
    private double householdCarSpend;
    private double householdCarImports;
    private double householdCarCredit;

    /** The second-hand market's month: offered, traded, what it cost and what a lender found. */
    private double usedCarsTraded, usedCarsOffered, usedCarSpend, usedCarCredit, usedCarPrice;

    /** Cars the households bought this month. */
    public double getHouseholdCarsBought() { return householdCarsBought; }

    /** ...what they paid for them, and what of that left the country. */
    public double getHouseholdCarSpend()   { return householdCarSpend; }
    public double getHouseholdCarImports() { return householdCarImports; }

    /** ...and what of it the bank advanced rather than the household finding. */
    public double getHouseholdCarCredit()  { return householdCarCredit; }

    /** Cars that changed hands second-hand this month. */
    public double getUsedCarsTraded()      { return usedCarsTraded; }

    /** ...against what was put up for sale, which in a crash is far more. */
    public double getUsedCarsOffered()     { return usedCarsOffered; }

    /** What one went for. Zero in a month when nobody offered one. */
    public double getUsedCarPrice()        { return usedCarPrice; }

    /** What the buyers paid for them, all in. */
    public double getUsedCarSpend()        { return usedCarSpend; }

    /** ...and what of that a lender advanced. */
    public double getUsedCarCredit()       { return usedCarCredit; }

    /** The car market's month. Was Game.motoring(); the body is that one, through Game's getters. */
    void month(Game game) {

        InfrastructureManager roads = game.getInfrastructureManager();
        HouseholdBalance householdBalance = game.getHouseholdBalance();
        Bank bank = game.getBank();

        /*
         * WHAT THE COMMUTE WAS LIKE, remembered before anything about this
         * month changes it. Who rides decides the load and the load decides
         * the ratio, so the ratio a driver answers has to be one from before
         * the month he is deciding about - see
         * InfrastructureManager.noteCongestion().
         */
        roads.noteCongestion(roads.getThroughputRatio());

        householdCarsBought = 0;
        householdCarSpend = 0;
        householdCarImports = 0;
        householdCarCredit = 0;
        usedCarsTraded = 0;
        usedCarsOffered = 0;
        usedCarSpend = 0;
        usedCarCredit = 0;
        usedCarPrice = 0;

        // Fifteen years on, cars are scrapped - whether or not anything can be
        // bought to replace them. See HouseholdBalance.wearOutCars().
        householdBalance.wearOutCars();

        GoodsMarket m = game.getMarkets().get(Good.CARS);
        double asking = m.getLocalPrice();
        /*
         * ...AND HOW MANY BOTHER AT ALL. The other half of Jerus's rule, and a
         * CEILING on ownership rather than a brake on it: a city whose lines
         * could carry everybody tops out at half a car per household for ever,
         * and one that builds those lines after it has motorised watches the
         * fleet decay to that ceiling as cars wear out. See
         * HouseholdBalance.TRANSIT_DETERRENT. Transit's effect on the decision
         * to DRIVE, with a car already bought, is a different mechanism and
         * lives on the network - see InfrastructureManager.willingToRide().
         */
        double ceiling = 1 - HouseholdBalance.TRANSIT_DETERRENT * roads.getTransitCover();

        /*
         * THE SECOND-HAND MARKET FIRST, and the order is the whole point.
         *
         * A household that cannot feed itself next month puts the car up here,
         * and a household that could never afford a showroom price buys it.
         * Clearing this BEFORE the new-car pass means the cheap cars go first,
         * which is what happens: nobody pays forty for a car they can have for
         * twelve. It also means the families who had to sell have the money in
         * the bank before the month whose bills they sold it for.
         *
         * AND THE BANK IS TOLD, for the same reason it is told about a new one:
         * a used car bought on credit is the bank's cash leaving for a seller's
         * account against a debt that did not exist a moment ago. The seller
         * here is another household rather than a showroom, so the transfer
         * itself nets to nothing across the household pool and only the
         * borrowed part crosses a boundary. See HouseholdBalance.clearUsedCars().
         */
        usedCarsTraded = householdBalance.clearUsedCars(asking, ceiling);
        usedCarPrice = householdBalance.getUsedCarPrice();
        usedCarsOffered = householdBalance.getUsedCarsOffered();
        usedCarSpend = householdBalance.getUsedCarSpend();
        usedCarCredit = householdBalance.getUsedCarsFinanced();
        if (usedCarCredit > 0) bank.lendToHouseholds(usedCarCredit);

        if (asking > 0) {
            double wanted = householdBalance.carsWanted(asking, ceiling);
            if (wanted > 0) {
                Markets.Draw quote = game.getMarkets().quote(Good.CARS, wanted, game.getSectors());
                double perCar = quote.cost() / wanted;
                double affordable = perCar > 0
                        ? householdBalance.carsWanted(perCar, ceiling) : 0;
                if (affordable > 0) {
                    Markets.Draw took = game.getMarkets().draw(
                            Good.CARS, null, Trade.HOUSEHOLDS, affordable, game.getSectors());
                    double paid = took.units() > 0 ? took.cost() / took.units() : 0;
                    householdCarsBought = took.units();
                    householdCarSpend = householdBalance.takeCars(took.units(), paid, ceiling);
                    householdCarImports = took.importCost();
                    /*
                     * AND THE BANK FINDS THE REST OF IT (2026-09-17).
                     *
                     * A financed car is money leaving the bank's own cash for
                     * a seller's till, against a debt that did not exist a
                     * moment ago - which is a pool falling, and the audit will
                     * say so unless the bank is told. It is told HERE rather
                     * than through totalBorrowed() in the household month,
                     * because that ran in Game.updateHouseholdAccounts(), the
                     * call before this one, and its working fields are cleared
                     * before the next one. See HouseholdBalance.CAR_DEPOSIT.
                     */
                    householdCarCredit = householdBalance.getCarsFinanced();
                    bank.lendToHouseholds(householdCarCredit);
                }
            }
        }

        // ...and the road is told what is now parked on it.
        roads.setCarOwnership(householdBalance.carsPerHousehold());
    }
}

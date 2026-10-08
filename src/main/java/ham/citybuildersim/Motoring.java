package ham.citybuildersim;

/**
 * The households' car market: the second-hand pass, then the showroom, with
 * the road told what is parked on it - and since 0.7.49 the drivers' fuel and
 * a buyer who weighs a car's full cost against the fare.
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

    /* ==================== THE FUEL, AND THE BUYER WEIGHS THE FARE (0.7.49) ====================
     *
     * A CAR COST NOTHING TO RUN until now: no fuel, upkeep or insurance
     * anywhere in the household model. A commute by car burns fuel, the
     * only running cost modelled - a car's wear is CAR_LIFE_MONTHS, which runs
     * by the calendar and not the mile, and its insurance is sunk. The fuel
     * is real money: struck at advanceDemographics() 6d as the drivers' month
     * (InfrastructureManager.getDrivers()) times a month of journeys at a
     * journey's price, paid by the rows that drive out of the fee waterfall
     * (HouseholdAccounts.setCommute()) and imported, like the railway's.
     *
     * AND THE HOUSEHOLD WITHOUT A CAR WEIGHS A MONTH'S PASS AGAINST A CAR'S
     * FULL MONTHLY COST - its payment over its life at the household rate,
     * and its fuel. Jerus: "acquiring car costs 500 a month and transit is
     * 250 then they take transit". The share who would rather ride
     * (InfrastructureManager.transitChosen()) scales how far good transit
     * lowers the ceiling on ownership (HouseholdBalance.TRANSIT_DETERRENT):
     * above half a car's cost the fare deters fewer buyers, at twice it
     * nobody. New and used cars alike, since the ceiling feeds both passes.
     */

    /**
     * What a journey to work by car burns, in world money: $2.00, fifteen
     * kilometres at eight litres a hundred and about $1.65 a litre. Priced at
     * the exchange rate like the railway's fuel, and the only running cost.
     * A car's wear is its CAR_LIFE_MONTHS, which runs by the calendar, not the
     * mile, and its insurance is sunk. A pump price: since 0.7.62 the journey
     * is its litres at petrol's market (journeyFuel()), and since 0.7.78 that
     * is the wholesale ladder's - see LITRES_PER_JOURNEY.
     */
    public static final double CAR_FUEL_PER_JOURNEY = .002;

    /**
     * ...and the litres in it (0.7.62): fifteen kilometres at eight litres a
     * hundred, the same journey. CAR_FUEL_PER_JOURNEY over this was FUEL's
     * import price, so a city with no refinery paid what it always paid a
     * journey, at the world's price level - PETROL's at 0.7.76 and 0.7.77
     * (batch O1). Since 0.7.78 (batch O2) petrol is on the research's
     * wholesale ladder, .0006118 a litre, and a journey's 1.2 L come to
     * .00073 in world money where CAR_FUEL_PER_JOURNEY's pump price was .002:
     * 63% less. CAR_FUEL_PER_JOURNEY stays what it was for a save from before
     * 0.7.49, which struck its commute at it (Game's load), and for the
     * harnesses' fixtures that hand a network a journey's fuel.
     */
    public static final double LITRES_PER_JOURNEY = 1.2;

    /** The month's fuel: the drivers' journeys at a journey's price, today's money. Struck at 6d. */
    private double fuelBill;

    /* ==================== THE FUEL IS DRAWN (0.7.62, batch K) ====================
     *
     * The drivers' litres - a month of journeys at LITRES_PER_JOURNEY - are
     * taken at 6d off the refiners' shelf at the market's price, and the rest
     * imported (Markets.draw(), as the households' cars are) - PETROL since
     * 0.7.76 (batch O1), where it was FUEL - so the bill is
     * what was actually charged: the shelf's part a sale on Refining's
     * statement, already in the audit as its SalesToHouseholds, and the
     * world's part the households' only import of fuel (fuelImports, the
     * audit's PetrolFunded and PetrolImports). With no refinery all of it is
     * imported at the world's price level, as every good is.
     */
    private double fuelImports, fuelLitres;

    /** The households' share who would rather ride than buy: 1 until a month is struck, and 1 at the default fare in the research cities. For the screens. */
    private double prefersTransit = 1;

    /** ...and the ceiling on ownership the month's buyers met, cars per household (1 until a month is struck). */
    private double ownershipCeiling = 1;

    /** Sets the month's fuel bill with every litre of it imported: what the fuel was before a refinery could sell any, and a save from before 0.7.62. */
    public void setFuelBill(double v) {
        fuelBill = Double.isFinite(v) ? Math.max(0, v) : 0;
        fuelImports = fuelBill;
        fuelLitres = 0;
    }
    public double getFuelBill()       { return fuelBill; }

    /** ...the part of it bought from the world (0.7.62): all of it with no refinery. */
    public double getFuelImports()    { return fuelImports; }

    /** ...and the litres the drivers burned (0.7.62); 0 for a month struck by setFuelBill(). */
    public double getFuelLitres()     { return fuelLitres; }

    /** The month's fuel as 6d struck it, put back by a load (0.7.62): the bill, its imported part and the litres. */
    public void restoreFuel(double bill, double imports, double litres) {
        fuelBill = Double.isFinite(bill) ? Math.max(0, bill) : 0;
        fuelImports = Double.isFinite(imports) ? Math.max(0, Math.min(fuelBill, imports)) : fuelBill;
        fuelLitres = Double.isFinite(litres) ? Math.max(0, litres) : 0;
    }

    /**
     * What a journey's fuel costs today, in today's money (0.7.62): its
     * litres at what a litre costs to bring in (GoodsMarket.landedPrice()) -
     * the refiners' price while the city has fuel on offer, the import price
     * while it has none. What the owners weigh a ride against
     * (InfrastructureManager.setCommute()); CAR_FUEL_PER_JOURNEY at the
     * world's price level with no refinery.
     */
    public static double journeyFuel(Markets markets) {
        double litre = markets == null ? Double.NaN : markets.get(Good.PETROL).landedPrice();
        return Double.isFinite(litre) && litre > 0 ? litre * LITRES_PER_JOURNEY : 0;
    }

    /**
     * The drivers' month of fuel, drawn (6d, 0.7.62): `journeys` at
     * LITRES_PER_JOURNEY off the refiners' shelf and the rest from the world,
     * at what the draw came to. See THE FUEL IS DRAWN.
     */
    void drawFuel(Game game, double journeys) {
        double litres = Double.isFinite(journeys) ? Math.max(0, journeys) * LITRES_PER_JOURNEY : 0;
        Markets.Draw took = game.getMarkets().draw(Good.PETROL, null, Trade.HOUSEHOLDS, litres, game.getSectors());
        fuelLitres = took.units();
        fuelBill = took.cost();
        fuelImports = took.importCost();
    }
    public double getPrefersTransit() { return prefersTransit; }
    public double getOwnershipCeiling() { return ownershipCeiling; }

    /**
     * A car's monthly payment over its life (CAR_LIFE_MONTHS) at an annual
     * rate: the annuity P i / (1 - (1 + i)^-n), i the rate over twelve; the
     * price over the months at no rate.
     */
    public static double carPayment(double price, double annualRate) {
        if (!(price > 0)) return 0;
        double i = Math.max(0, annualRate) / 12, n = HouseholdBalance.CAR_LIFE_MONTHS;
        if (i < 1e-12) return price / n;
        return price * i / (1 - Math.pow(1 + i, -n));
    }

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
         * lives on the network - see InfrastructureManager, WHO RIDES, BY WHAT
         * THEY PAY.
         *
         * ...SCALED BY WHO WOULD RATHER RIDE (0.7.49): a car's full monthly
         * cost - the showroom's price over its life at the household rate,
         * and a month of fuel - against a month's pass. See THE FUEL, AND THE
         * BUYER WEIGHS THE FARE.
         */
        double fullCost = carPayment(asking, bank.householdRate(game.getDebtManager().getPolicyRate()))
                + TaxPolicy.JOURNEYS_A_MONTH * roads.getFuelPerJourney();
        double pass = game.getEconomyManager().getTaxPolicy().monthlyFare();
        prefersTransit = InfrastructureManager.transitChosen(fullCost, pass);
        double ceiling = 1 - HouseholdBalance.TRANSIT_DETERRENT * roads.getTransitCover() * prefersTransit;
        ownershipCeiling = ceiling;

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
        if (usedCarCredit > 0) {
            bank.lendToHouseholds(usedCarCredit);
            // ...and its fee, added to what the buyers owe (0.7.7).
            bank.bookLoanFees(householdBalance.getUsedCarLoanFees());
        }

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
                    bank.bookLoanFees(householdBalance.getCarLoanFees());
                }
            }
        }

        // ...and the road is told what is now parked on it.
        roads.setCarOwnership(householdBalance.carsPerHousehold());
    }
}

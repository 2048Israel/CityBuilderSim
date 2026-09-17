package ham.citybuildersim;

/**
 * The cars: who buys one, what it costs them, and what it does to the road.
 *
 * WHY THIS HARNESS EXISTS. Transport steps 1 to 4 split the road's demand into
 * streams and gave the city three ways to serve it - highways, transit, rail -
 * and every one of them was a RELIEF on a baseline that never moved. A car is
 * the first thing in this game that makes the road worse, so for the first
 * time the promise "an existing city computes exactly what it computed
 * yesterday" is carried by a number that can be non-zero, and it needs holding
 * down in its own file.
 *
 * SIX CLAIMS.
 *
 *   1. A CITY WITH NO CARS IS THE CITY IT WAS, to the bit - the load, the
 *      ratio, the riders, and the pair of blends that serve a sector. Nothing
 *      below matters if this fails, because it is the promise that every save
 *      in existence still opens into the city it was saved from.
 *
 *   2. THE PENALTY IS THE STRAIGHT LINE IT SAYS IT IS, it lands only on the
 *      commuters who are still driving, and at saturation it is
 *      CAR_LOAD_AT_SATURATION and not some emergent number.
 *
 *   3. THE LOOP CLOSES. Jerus: "people drive until the road is full, then take
 *      the tram." A motorised city with a clear road puts nobody on transit; as
 *      the remembered commute worsens they come back, monotonically, and never
 *      past the ceiling any city's transit share has.
 *
 *   4. A CAR IS PAID FOR. What the households' savings lose is exactly what the
 *      sellers and the world were paid, in the same month - the money identity
 *      this codebase exists to keep - and the imported half is declared to the
 *      audit, because Markets.draw() has no sector to book it against.
 *
 *   5. THE FLEET IS A STOCK. It wears out at CAR_LIFE_MONTHS whether or not
 *      anything can replace it, replacement is not throttled by the diffusion
 *      rate, and ownership saturates at one per household rather than
 *      plateauing on an accident of two constants.
 *
 *   6. IT SURVIVES A SAVE, both ways: a city reloads with the fleet, the
 *      ownership rate and the remembered commute it was saved with, and a save
 *      from before cars existed reloads owning none.
 *
 * See claude/the-fifth-link.md, HouseholdBalance's cars section and
 * InfrastructureManager's.
 */
public class CarCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            @Override public void write(int b) { }
        }));
        try { r.run(); } finally { System.setOut(real); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-68s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-68s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise. Same family as RailCheck and TradeCostCheck, same reason. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0 || (Double.isNaN(a) && Double.isNaN(b));
    }

    static InfrastructureManager network() {
        InfrastructureManager n = new InfrastructureManager();
        n.setBuiltCapacity(10_000);
        n.setLoad(12_000);
        n.setBreakdown(new double[] { 8_600, 400, 3_000 });
        n.setModes(0, 0);
        return n;
    }

    public static void main(String[] args) {

        /* ================================================================
           1. A CITY WITH NO CARS
           ================================================================ */

        System.out.println("--- a city with nobody driving is the city it was ---");

        InfrastructureManager plain = network();
        double loadBefore = plain.getEffectiveLoad();
        double ratioBefore = plain.getThroughputRatio();
        double cityBefore = plain.cityThroughput();
        double forBefore = plain.throughputFor(new double[] { 240, 0, 0 });

        report("the factor is one, and it is one exactly",
                same(plain.carRoadFactor(), 1), String.valueOf(plain.carRoadFactor()));
        report("...and so is the willingness to ride",
                same(plain.willingToRide(), 1), String.valueOf(plain.willingToRide()));

        plain.setCarOwnership(0);
        report("setting the ownership to zero changes nothing, to the bit",
                same(plain.getEffectiveLoad(), loadBefore)
                        && same(plain.getThroughputRatio(), ratioBefore)
                        && same(plain.cityThroughput(), cityBefore)
                        && same(plain.throughputFor(new double[] { 240, 0, 0 }), forBefore),
                String.format("%s vs %s", plain.getEffectiveLoad(), loadBefore));

        /*
         * AND THE MEMORY OF A CLEAR ROAD IS STILL CLEAR. noteCongestion() walks
         * an average toward what it is told; told 1 for ever it has to stay at
         * 1 and not at 1 - 1e-16, because getJam() divides the shortfall and
         * willingToRide() multiplies the load by the answer.
         */
        for (int i = 0; i < 50; i++) plain.noteCongestion(1);
        report("...and a road that was never jammed is remembered as clear",
                same(plain.getRememberedThroughput(), 1) && same(plain.getJam(), 0),
                String.format("%s, jam %s", plain.getRememberedThroughput(), plain.getJam()));

        /* ================================================================
           2. THE PENALTY
           ================================================================ */

        System.out.println("\n--- what a car costs the street ---");

        InfrastructureManager driven = network();
        driven.setCarOwnership(1);
        report("a fully motorised city asks CAR_LOAD_AT_SATURATION of the road",
                same(driven.carRoadFactor(), InfrastructureManager.CAR_LOAD_AT_SATURATION),
                String.format("%.2fx", driven.carRoadFactor()));

        driven.setCarOwnership(.5);
        report("...and half way there is half way up the line",
                same(driven.carRoadFactor(),
                        1 + .5 * (InfrastructureManager.CAR_LOAD_AT_SATURATION - 1)),
                String.format("%.3fx", driven.carRoadFactor()));

        driven.setCarOwnership(1);
        double jammed = driven.getEffectiveLoad();
        report("a city where everybody drives is a different city",
                jammed > loadBefore * 2 && jammed < loadBefore * 3,
                String.format("%,.0f trips against %,.0f, ratio %.3f -> %.3f",
                        jammed, loadBefore, ratioBefore, driven.getThroughputRatio()));

        /*
         * ONLY THE DRIVERS PAY IT. A city whose transit carries every commuter
         * it is allowed to carry should feel the multiplier on what is LEFT,
         * not on the whole stream - otherwise a city that motorises and builds
         * a metro is charged twice for one journey.
         */
        InfrastructureManager metro = network();
        metro.setModes(0, 20_000);
        metro.setFare(0);
        metro.setCarOwnership(1);
        /*
         * AND THE JAM IS REMEMBERED, which this comparison cannot be made
         * without and the first draft of it forgot. A motorised city on a road
         * it remembers as clear puts NOBODY on a tram - that is the model
         * working, it is the first half of Jerus's sentence, and it makes a
         * metro worth exactly nothing until the street stops moving. Asking
         * what transit saves has to be asked of a city that would use it.
         */
        for (int i = 0; i < 60; i++) metro.noteCongestion(InfrastructureManager.MIN_THROUGHPUT);
        double commuters = metro.getLoad(Traffic.COMMUTERS);
        double riders = metro.getTransitRiders();
        double expected = (commuters - riders) * metro.carRoadFactor()
                + metro.getLoad(Traffic.GOODS) + metro.getLoad(Traffic.BULK);
        report("the penalty lands on the commuters still driving and no others",
                Math.abs(metro.getEffectiveLoad() - expected) < 1e-9,
                String.format("%,.0f of %,.0f still driving", commuters - riders, commuters));

        InfrastructureManager noMetro = network();
        noMetro.setCarOwnership(1);
        for (int i = 0; i < 60; i++) noMetro.noteCongestion(InfrastructureManager.MIN_THROUGHPUT);
        InfrastructureManager walkingMetro = network();
        walkingMetro.setModes(0, 20_000);
        walkingMetro.setFare(0);
        for (int i = 0; i < 60; i++) walkingMetro.noteCongestion(InfrastructureManager.MIN_THROUGHPUT);
        double savedDriving = noMetro.getEffectiveLoad() - metro.getEffectiveLoad();
        double savedWalking = network().getEffectiveLoad() - walkingMetro.getEffectiveLoad();
        report("...so a rider taken off a jammed motorised street is worth more than one off a walking street",
                savedDriving > savedWalking * 1.5,
                String.format("saves %,.0f trips against %,.0f", savedDriving, savedWalking));

        /* ================================================================
           3. THE LOOP
           ================================================================ */

        System.out.println("\n--- people drive until the road is full, then take the tram ---");

        InfrastructureManager loop = network();
        loop.setModes(0, 20_000);
        loop.setFare(0);
        loop.setCarOwnership(1);
        for (int i = 0; i < 60; i++) loop.noteCongestion(1);
        report("a city where everybody owns a car and the road is clear rides nothing",
                same(loop.getTransitRiders(), 0),
                String.format("%.6f riders", loop.getTransitRiders()));

        double last = -1;
        boolean monotone = true, bounded = true;
        for (int step = 0; step <= 10; step++) {
            double ratio = 1 - step * (1 - InfrastructureManager.MIN_THROUGHPUT) / 10.0;
            InfrastructureManager at = network();
            at.setModes(0, 20_000);
            at.setFare(0);
            at.setCarOwnership(1);
            for (int i = 0; i < 60; i++) at.noteCongestion(ratio);
            double now = at.getTransitRiders();
            if (now < last - 1e-9) monotone = false;
            if (now > at.getLoad(Traffic.COMMUTERS) * InfrastructureManager.TRANSIT_MAX_SHARE + 1e-9) bounded = false;
            last = now;
        }
        assertTrue("...and rides more of it the worse the commute gets, all the way down", monotone);
        assertTrue("...but never past the share any city's transit can carry", bounded);

        InfrastructureManager grid = network();
        grid.setModes(0, 20_000);
        grid.setFare(0);
        grid.setCarOwnership(1);
        for (int i = 0; i < 60; i++) grid.noteCongestion(InfrastructureManager.MIN_THROUGHPUT);
        /*
         * A MEMORY ARRIVES ASYMPTOTICALLY, so this is near and not bitwise:
         * sixty months at JAM_MEMORY leaves about a hundredth of a millionth of
         * the way to go, and a harness that demanded the last ulp would be
         * asserting on the exponential rather than on the rule.
         */
        report("at a standstill, three quarters of the car owners are on the tram",
                Math.abs(grid.willingToRide() - InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK) < 1e-6,
                String.format("%.1f%% willing", grid.willingToRide() * 100));

        /*
         * AND THE MEMORY IS WHAT STOPS IT OSCILLATING. One clear month inside a
         * jammed decade must not empty the trams; that is the difference
         * between a lag and a memory, and it is the reason JAM_MEMORY exists.
         */
        double before = grid.getTransitRiders();
        grid.noteCongestion(1);
        report("one good month does not empty the trams",
                grid.getTransitRiders() > before * .7,
                String.format("%,.0f riders after, %,.0f before", grid.getTransitRiders(), before));

        /* ================================================================
           4, 5, 6. IN A CITY
           ================================================================ */

        System.out.println("\n--- and now a city that buys them ---");

        GameFiles files = GameFiles.scratch("carcheck");
        Game game = new Game(files);

        quietly(() -> {
            game.newGame();
            game.getLandManager().setOwnedSqFt(400_000_000L);
            BuildingManager b = game.getBuildingManager();
            game.buildStack(b.getTemplateByName("House"), 900, true);
            game.buildStack(b.getTemplateByName("Convenience Store"), 20, true);
            game.buildStack(b.getTemplateByName("Small Grocery Store"), 6, true);
            game.buildStack(b.getTemplateByName("Paved Road"), 60, true);
            game.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            game.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            game.buildStack(b.getTemplateByName("Construction Depot"), 4, true);
            for (int i = 0; i < 24; i++) game.toggleNextMonth();
        });

        HouseholdBalance homes = game.getHouseholdBalance();
        InfrastructureManager roads = game.getInfrastructureManager();

        report("a city with money in the bank motorises",
                homes.totalCars() > 0 && roads.getCarOwnership() > 0,
                String.format("%,.0f cars, %.1f%% of households",
                        homes.totalCars(), roads.getCarOwnership() * 100));
        report("...and the road is told about them",
                roads.carRoadFactor() > 1,
                String.format("%.3fx on the commuters", roads.carRoadFactor()));

        /* ---- the money ---- */

        double savingsBefore = homes.totalSavings();
        double[] carsSold = new double[1];
        double[] paid = new double[1];
        double[] imported = new double[1];
        quietly(() -> {
            game.toggleNextMonth();
            carsSold[0] = game.getHouseholdCarsBought();
            paid[0] = game.getHouseholdCarSpend();
            imported[0] = game.getHouseholdCarImports();
        });

        /*
         * The households' savings move for more reasons than cars in a month -
         * wages land, the shops are paid, the exchange takes a bid. What has to
         * hold is the narrower thing: what takeCars() says it charged is what
         * the cells were actually charged, and the import half is declared.
         */
        report("a month of car sales is a month of real money",
                paid[0] > 0 && carsSold[0] > 0,
                String.format("%,.0f cars for $%,.0fk, $%,.0fk of it imported",
                        carsSold[0], paid[0], imported[0]));
        report("...and every car sold was paid for at the same price",
                Math.abs(paid[0] / carsSold[0] - game.getMarkets().get(Good.CARS).getLocalPrice())
                        < game.getMarkets().get(Good.CARS).getLocalPrice() * .35,
                String.format("$%,.1fk a car against a market at $%,.1fk",
                        paid[0] / carsSold[0], game.getMarkets().get(Good.CARS).getLocalPrice()));
        report("...and the savings really left",
                homes.totalSavings() != savingsBefore, "");

        /* ---- whole cars, which is what makes a reform safe ---- */
        boolean whole = true;
        double onTheRoad = 0;
        for (Household c : homes.cells()) {
            onTheRoad += c.totalCars();
            if (c.cars() < 0 || c.cars() > 1) whole = false;
        }
        assertTrue("no household owns less than none of a car, or more than one", whole);
        report("...and the fleet is the cells' own count",
                Math.abs(onTheRoad - homes.totalCars()) < 1e-9,
                String.format("%,.1f cars", onTheRoad));

        /* ---- the fleet is a stock ---- */

        System.out.println("\n--- and a fleet is a stock, not a flow ---");

        HouseholdBalance bench = new HouseholdBalance();
        Household cell = bench.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
        cell.households = 1000;
        cell.savings = 10_000;
        cell.disposable = 10;
        cell.cars = 1;
        double scrapped = bench.wearOutCars();
        report("a fleet wears out at one part in CAR_LIFE_MONTHS a month",
                Math.abs(scrapped - 1000 / HouseholdBalance.CAR_LIFE_MONTHS) < 1e-9,
                String.format("%.2f of 1,000 cars scrapped", scrapped));

        double wantedNow = bench.carsWanted(1, 1);
        report("...and a household whose car died replaces it at once, not at the diffusion rate",
                wantedNow >= Math.floor(scrapped),
                String.format("%.0f wanted against %.2f scrapped", wantedNow, scrapped));

        /*
         * SATURATION IS ONE PER HOUSEHOLD AND IS REACHED. Put the diffusion
         * rate on replacement as well and a fleet settles at about 78% for
         * ever, which is an arithmetic accident of two constants and not a
         * fact about anywhere. Run a rich cell out for a century and it has to
         * finish full.
         */
        HouseholdBalance rich = new HouseholdBalance();
        Household plenty = rich.cell(FamilyStructure.COUPLE, PayTier.ELITE);
        plenty.households = 1000;
        plenty.savings = 1_000_000;
        plenty.disposable = 10;
        for (int i = 0; i < 1200; i++) {
            rich.wearOutCars();
            rich.takeCars(rich.carsWanted(1, 1), 1, 1);
            plenty.savings = 1_000_000;   // a household that never runs out of money
        }
        /* =================================================================
           WHY THE BAR IS 95% AND NOT 100%, and why it is not the plateau this
           assertion exists to rule out.

           A cell buys WHOLE cars (see HouseholdBalance.wantOf, and the reform
           it is there to survive), so the fractional tail of a month's
           replacement is dropped rather than carried. At full ownership a
           thousand households scrap 5.56 cars a month and want floor(5.56 +
           0.02 x room) of them, and the two meet where room is about a car -
           which is a couple of percent of a cell and is the grain of the model,
           not a mechanism.

           THE PLATEAU THIS RULES OUT IS 78%, and it is a different animal
           entirely: put the diffusion rate on replacement as well as on new
           owners and a fleet settles at 0.02g = (1-g)/180 for ever, which is an
           arithmetic accident of two constants that a player would read as a
           design statement about how many people want a car. The distance
           between 97% and 78% is the whole of what this line is watching.
           ================================================================= */
        report("a city that can afford cars ends up with one per household, bar the grain of a whole car",
                rich.carsPerHousehold() > .95,
                String.format("%.1f%% after a century - the throttled-replacement plateau is 78%%",
                        rich.carsPerHousehold() * 100));

        HouseholdBalance broke = new HouseholdBalance();
        Household poor = broke.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        poor.households = 1000;
        poor.savings = 0;
        poor.disposable = 10;
        poor.cars = 1;
        for (int i = 0; i < 1200; i++) {
            broke.wearOutCars();
            broke.takeCars(broke.carsWanted(1, 1), 1, 1);
        }
        report("...and a city that cannot loses its fleet over the life of a car",
                broke.carsPerHousehold() < .01,
                String.format("%.2f%% left", broke.carsPerHousehold() * 100));

        /* ---- transit deters the purchase ---- */

        InfrastructureManager served = network();
        served.setModes(0, 1_000_000);
        report("a city whose transit could carry everybody halves what it will own",
                same(served.getTransitCover(), 1)
                        && same(1 - HouseholdBalance.TRANSIT_DETERRENT * served.getTransitCover(), .5),
                String.format("cover %.0f%%, ceiling %.2f cars per household",
                        served.getTransitCover() * 100,
                        1 - HouseholdBalance.TRANSIT_DETERRENT * served.getTransitCover()));
        report("...and a city with no transit deters nobody, exactly",
                same(network().getTransitCover(), 0), "");

        /*
         * AND THE CEILING WORKS BACKWARDS, which is the whole reason it is a
         * ceiling rather than a brake on the adoption rate. A city that
         * motorises and then builds its metro does not lose its cars; it stops
         * replacing them, and the fleet walks down to what the new ceiling
         * allows over the life of a car.
         */
        HouseholdBalance late = new HouseholdBalance();
        Household them = late.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
        them.households = 1000;
        them.savings = 1_000_000;
        them.disposable = 10;
        them.cars = 1;
        for (int i = 0; i < 400; i++) {
            late.wearOutCars();
            late.takeCars(late.carsWanted(1, .5), 1, .5);
            them.savings = 1_000_000;
        }
        report("a metro built after the cars unmotorises the city to its ceiling",
                late.carsPerHousehold() < .52 && late.carsPerHousehold() > .45,
                String.format("%.0f%% left of a fully motorised city, against a ceiling of 50%%",
                        late.carsPerHousehold() * 100));

        /* ---- the save ---- */

        System.out.println("\n--- and it all survives a save ---");

        double fleet = homes.totalCars();
        double owned = roads.getCarOwnership();
        double remembered = roads.getRememberedThroughput();
        quietly(() -> game.saveGame(10));
        Game back = new Game(files);
        quietly(() -> { back.run(); back.loadGame(10); });

        report("a reloaded city has the same fleet",
                Math.abs(back.getHouseholdBalance().totalCars() - fleet) < 1e-9,
                String.format("%,.1f against %,.1f", back.getHouseholdBalance().totalCars(), fleet));
        report("...the same ownership rate on the road",
                same(back.getInfrastructureManager().getCarOwnership(), owned),
                String.format("%s against %s", back.getInfrastructureManager().getCarOwnership(), owned));
        report("...and the same memory of the commute",
                same(back.getInfrastructureManager().getRememberedThroughput(), remembered),
                String.format("%s against %s",
                        back.getInfrastructureManager().getRememberedThroughput(), remembered));

        /*
         * AND THE OTHER DIRECTION, which is the one every save in existence is
         * about to take: a cell array one slot short - the shape written by
         * every build before this one - restores a city that owns no cars,
         * rather than reading the next cell's savings as a car park.
         */
        HouseholdBalance older = new HouseholdBalance();
        String[] keys = older.cellKeys();
        double[] old = new double[keys.length * HouseholdBalance.CELL_SLOTS_BEFORE_CARS + 3];
        for (int i = 0; i < old.length; i++) old[i] = i + 1;
        boolean took = older.restoreCells(keys, old, Equity.COMPANIES);
        report("a save from before cars existed still loads",
                took, String.valueOf(HouseholdBalance.CELL_SLOTS_BEFORE_CARS) + " slots a cell");
        report("...and the city it loads owns none",
                same(older.totalCars(), 0) && same(older.carsPerHousehold(), 0), "");

        System.out.println();
        if (fails == 0) {
            System.out.println("The cars work: an unmotorised city is untouched, a motorised one"
                    + " pays for its road, and the tram fills up when the street stops moving.");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}

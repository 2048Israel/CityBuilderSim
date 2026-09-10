package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The road network, from the curve up to a city that actually jams.
 *
 * Three things have to hold and none of them is obvious from the code:
 *
 *   1. The response curve is right - flat until it isn't, and floored.
 *   2. A NEW city is never congested. The base network exists precisely so
 *      that the opening hour is not a wall, which is the mistake the land pass
 *      made when the starting allocation could not fit a power plant.
 *   3. Building a road actually fixes it, and the fix survives a save.
 *
 * The last one is the point of the whole feature. A mechanic the player cannot
 * see themselves solve is just a tax.
 */
public class InfrastructureCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-9;
        if (!ok) {
            fails++;
            System.out.printf("%-58s FAIL  %.6f != %.6f%n", label, actual, expected);
        } else {
            System.out.printf("%-58s OK%n", label);
        }
    }

    /**
     * True once the month's report describes the same city the month ended in.
     *
     * Population and store stock are read into the statement at the start of a
     * month and moved by the month itself; while they are still moving, a save
     * cannot be expected to reproduce the report exactly, for reasons that have
     * nothing to do with roads.
     */
    static boolean steady(CommercialHandler shops) {
        return shops.getReportPopulation() == shops.getPopulation()
                && shops.getReportStoreInventory() == shops.getStoreInventory();
    }

    /** How many convenience stores give this much coverage - the fixture's shape, not a count. */
    static int shopsFor(Game g, double coverage) {
        BuildingsTemplate shop = template(g, "Convenience Store");
        return (int) Math.max(1, Math.ceil(coverage / shop.getCoverage()));
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    public static void main(String[] args) throws Exception {

        /* ================= 1. the curve, on its own ================= */
        System.out.println("--- the response curve ---");

        InfrastructureManager road = new InfrastructureManager();

        assertTrue("an empty network is not congested", !road.isCongested());
        close("...and carries everything", road.getThroughputRatio(), 1);
        close("a new city starts with a network at all",
                road.getCapacity(), InfrastructureManager.BASE_CAPACITY);

        road.setLoad(InfrastructureManager.BASE_CAPACITY * .5);
        close("half full, still free-flowing", road.getThroughputRatio(), 1);
        assertTrue("...and says so", "Clear".equals(road.getStatus()));

        // Exactly at free flow is the last moment nothing is lost. One unit
        // more and it starts to cost, which is the whole shape of the mechanic.
        road.setLoad(InfrastructureManager.BASE_CAPACITY * InfrastructureManager.FREE_FLOW);
        close("exactly at free flow, still nothing lost", road.getThroughputRatio(), 1);
        assertTrue("...but there is no headroom left", road.getHeadroom() < 1e-9);

        road.setLoad(InfrastructureManager.BASE_CAPACITY * .95);
        assertTrue("just past it, throughput starts falling",
                road.getThroughputRatio() < 1);
        assertTrue("...but only just", road.getThroughputRatio() > .9);
        assertTrue("...and it is congested, not merely busy",
                road.isCongested() && !road.isStrained());

        road.setLoad(InfrastructureManager.BASE_CAPACITY * 100);
        close("hopelessly overloaded, throughput hits the floor",
                road.getThroughputRatio(), InfrastructureManager.MIN_THROUGHPUT);
        assertTrue("...a gridlocked city still moves",
                road.getThroughputRatio() > 0);

        // The floor is what stops congestion being a death spiral: construction
        // is throttled by this same ratio, so a network that could reach zero
        // would leave a city unable to build its way out. See SimulationEngine.
        assertTrue("the floor is well clear of zero",
                InfrastructureManager.MIN_THROUGHPUT > .2);

        /* ================= 2. monotonic, with no cliff ================= */
        System.out.println("\n--- and it degrades smoothly ---");

        boolean monotonic = true;
        boolean smooth = true;
        double previous = 1;
        for (int i = 1; i <= 400; i++) {
            road.setLoad(InfrastructureManager.BASE_CAPACITY * i / 100.0);
            double now = road.getThroughputRatio();
            if (now > previous + 1e-12) monotonic = false;
            if (previous - now > .05) smooth = false;   // no single step off a cliff
            previous = now;
        }
        assertTrue("more traffic never means more throughput", monotonic);
        assertTrue("no cliff edge - one building never costs 5%", smooth);

        /* ================= 3. capacity is what you paid for ================= */
        System.out.println("\n--- what a road buys ---");

        road.setBuiltCapacity(600);
        close("built capacity adds to the base",
                road.getCapacity(), InfrastructureManager.BASE_CAPACITY + 600);
        close("...and the player's share is reported separately",
                road.getBuiltCapacity(), 600);

        road.setLoad(InfrastructureManager.BASE_CAPACITY + 600);
        assertTrue("a network at exactly capacity is congested", road.isCongested());

        road.reset();
        close("reset puts the base network back",
                road.getCapacity(), InfrastructureManager.BASE_CAPACITY);
        close("...with nothing on it", road.getLoad(), 0);

        /* ================= 4. a real city ================= */
        System.out.println("\n--- and now a city that has to live with it ---");

        Path root = Files.createTempDirectory("roads");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game city = new Game(files);
        city.run();

        assertTrue("a brand new city is not congested",
                !city.getInfrastructureManager().isCongested());
        close("...its roads carry everything", city.getRoadRatio(), 1);

        /*
         * Small enough to stay inside the base network. If this ever starts
         * failing, the opening of the game has become a traffic puzzle, which
         * is not what a first turn should be.
         *
         * SIX MONTHS, NOT TWELVE, since 2026-09-09. What is under test is
         * whether the buildings ON THIS LIST fit inside the base roads, and
         * they do comfortably - 170 of 400 by month five. What broke the
         * twelve-month version is that the works department was quadrupled at
         * the same time, so a year of private investment now adds ninety-five
         * homes of its own on top and the network fills at month eleven. That
         * is the city outgrowing its roads, which is the mechanic working, and
         * it belongs to section 5 below rather than here.
         */
        city.buildStack(template(city, "House"), 40, false);
        /*
         * SIZED FROM THE TEMPLATE. This fixture's whole second half is a city
         * whose shops are the bottleneck the roads have to feed, and it opened
         * with "3 stores" when a store covered 120 people. The retail
         * rebalance of 2026-09-10 made a store cover 480, so three of them fed
         * the grown city with or without roads and the delivered-share
         * assertion below compared 93% against 84% instead of 89% against 35%.
         * The number that matters is 360 of coverage for a thousand people;
         * the count is derived from it.
         */
        city.buildStack(template(city, "Convenience Store"), shopsFor(city, 360), false);
        city.simulateMonths(6);

        System.out.printf("   a starter city: %.0f of %.0f used%n",
                city.getInfrastructureManager().getLoad(),
                city.getInfrastructureManager().getCapacity());
        assertTrue("a starter city still has road to spare",
                city.getInfrastructureManager().getHeadroom() > 0);
        close("...and loses nothing to traffic", city.getRoadRatio(), 1);

        /* ================= 5. growth jams it ================= */
        System.out.println("\n--- growth is what breaks it ---");

        /*
         * GROWN UNTIL IT JAMS. "260 houses" jammed the network two and a half
         * times over when a food plant loaded the roads by 200 and the advisor
         * built several; at a fifth of the size (2026-09-10) the same 260
         * houses loaded it 491 against 400, throughput 73%, and every
         * assertion below about a congested city was being made of one that
         * was merely busy. The fixture's premise is the load, so the lorries
         * the plants used to put on the road come from six builders' yards at
         * 90 apiece instead - a building whose load did not move.
         */
        city.buildStack(template(city, "House"), 260, false);
        city.buildStack(template(city, "Construction Depot"), 6, false);
        city.simulateMonths(30);

        InfrastructureManager net = city.getInfrastructureManager();
        System.out.printf("   grown: %.0f of %.0f used, throughput %.1f%%%n",
                net.getLoad(), net.getCapacity(), city.getRoadRatio() * 100);

        assertTrue("a grown city outruns the network it was given",
                net.isCongested());
        assertTrue("...and the load really is above free flow",
                net.getUtilisation() > InfrastructureManager.FREE_FLOW);
        assertTrue("...it says Congested", "Congested".equals(net.getStatus()));

        // The reason a player notices at all. If congestion did not reach the
        // income statement it would be a number on a screen.
        // The regression guard for a bug the road pass very nearly shipped: the
        // throttle went into simulateMonth() alone, so the build rate the screen
        // quoted - and, worse, the figure construction was PAID for - stayed at
        // the uncongested number while the sites crawled. One definition now,
        // in Game.getConstructionOutput(); this is what proves it is still the
        // one being used.
        /*
         * SICKNESS IS IN HERE TOO, and it was not when this was written.
         *
         * This assertion restates Game.getConstructionOutput()'s formula, which
         * is the duplication trap in miniature: the day a fourth multiplier
         * arrived - workers off ill - the copy was wrong and the original was
         * fine, and this line failed at 285 against 352. That is the harness
         * working as intended, and the fix is to ask the city what its work
         * ratio is rather than to assume it is 1.
         */
        double undiscounted = city.getBuildingManager().getTotalConstructionCapacity()
                * city.getServicesManager().getConstructionHandler().getAverageFill()
                * city.getHealth().getWorkRatio();
        assertTrue("the builders are slowed by it too",
                city.getConstructionOutput() < undiscounted);
        close("...by exactly the throughput ratio",
                city.getConstructionOutput(),
                Math.round(undiscounted * city.getRoadRatio()));

        assertTrue("the shops feel it",
                city.getEconomyManager().getCommercialHandler().getRoadRatio() < 1);

        /* ================= 6. building a road fixes it ================= */
        System.out.println("\n--- and building a road fixes it ---");

        BuildingsTemplate roadNetwork = template(city, "Paved Road");

        assertTrue("roads are a building the city can order",
                roadNetwork.getCategory() == BuildingType.INFRASTRUCTURE);
        assertTrue("...that costs materials, so construction earns from it",
                roadNetwork.getConstructionMaterials() > 0);
        assertTrue("...and takes months, so it cannot be a panic button",
                roadNetwork.getConstructionPoints() > 0);
        assertTrue("...and adds capacity when it is done",
                roadNetwork.getCapacity() > 0);
        assertTrue("a road generates no traffic of its own",
                roadNetwork.getRoadLoad() == 0);
        assertTrue("...and employs nobody to run it",
                roadNetwork.getTotalJobs() == 0);

        /*
         * Run as a controlled pair, not as a before-and-after.
         *
         * Sixty months change a great deal besides the roads - the congested
         * city sheds shops, the investors reprice, the population moves - so
         * "income went up after I built a road" proves nothing on its own. Two
         * copies of the SAME saved city, one that builds and one that does not,
         * isolates the only variable anyone cares about.
         */
        assertTrue("the congested city saved", city.saveGame(2, "gridlock").ok);

        Game withRoads = new Game(files);
        withRoads.loadGameSave(2);

        Game without = new Game(files);
        without.loadGameSave(2);

        double capacityBefore = withRoads.getInfrastructureManager().getCapacity();

        /*
         * Funded, and given somewhere to put it. A road is a big lot, and this
         * check is about traffic rather than about whether a jammed city can
         * still raise the money - which it can, by borrowing, elsewhere.
         *
         * BOTH CITIES GET THE MONEY AND THE LAND since 2026-09-07, and only one
         * of them spends it on roads. Before, the subsidy and the acreage went
         * to withRoads alone, so the two cities differed in three ways at once
         * and the fixture called the sum of them "roads". The consumption
         * assertion below was the one that noticed: it passed by 2% on a
         * four-person population gap, which is not a margin, it is a coin
         * landing the right way up. Rent becoming a market tipped the coin -
         * roads eat construction materials, dearer materials mean a dearer
         * rent floor, and the road city's households had less left for the
         * shops - and a 2% pass became a 3% fail without anything about roads
         * changing at all.
         *
         * Controlled, the question is the one the section actually asks: same
         * money, same ground, one city builds roads. The GDP claim never needed
         * this - it wins by 50% - but the consumption claim did.
         */
        withRoads.getGovernmentInvestor().spend(-500000);
        without.getGovernmentInvestor().spend(-500000);
        withRoads.getLandManager().setOwnedSqFt(
                withRoads.getLandManager().getOwnedSqFt() + roadNetwork.getLandSqFt() * 3);
        without.getLandManager().setOwnedSqFt(
                without.getLandManager().getOwnedSqFt() + roadNetwork.getLandSqFt() * 3);

        Game.BuildResult ordered = withRoads.buildStack(roadNetwork, 2, false);
        assertTrue("the order goes through", ordered == Game.BuildResult.SUCCESS);

        // Ordering is not finishing. This is the half of the mechanic that
        // makes waiting until you are already jammed a bad idea.
        close("ordering one changes nothing yet",
                withRoads.getInfrastructureManager().getCapacity(), capacityBefore);

        withRoads.simulateMonths(60);
        without.simulateMonths(60);

        InfrastructureManager built = withRoads.getInfrastructureManager();
        System.out.printf("   with roads:    %.0f of %.0f used, throughput %.1f%%, income %.1f%n",
                built.getLoad(), built.getCapacity(),
                withRoads.getRoadRatio() * 100, withRoads.getIncome());
        System.out.printf("   without:       %.0f of %.0f used, throughput %.1f%%, income %.1f%n",
                without.getInfrastructureManager().getLoad(),
                without.getInfrastructureManager().getCapacity(),
                without.getRoadRatio() * 100, without.getIncome());

        assertTrue("the finished roads added capacity",
                built.getCapacity() > capacityBefore);
        assertTrue("...and cleared the jam", withRoads.getRoadRatio() > without.getRoadRatio());
        assertTrue("...while the city that built nothing is still stuck",
                without.getInfrastructureManager().isCongested());
        /*
         * BETTER OFF IN OUTPUT, not in the treasury's monthly line.
         *
         * This compared getIncome(), which is what the CITY collects - and the
         * city that built roads is also paying to keep them. At this scale the
         * upkeep is the same order as the extra tax, so the comparison came
         * down to a couple of dollars either way and flipped on 2026-09-07 when
         * the House grew to six and both cities got bigger. The benefit of a
         * road was never the treasury's; it is that the economy on it runs at
         * all, which is what GDP measures and what the throughput ratio above
         * is a direct input to.
         */
        /*
         * AVERAGED, NOT SNAPSHOT - and that correction matters more than it
         * sounds. One month's GDP contains one month's CAPITAL SPENDING, which
         * is lumpy in a way nothing else here is: measured on a single month,
         * the jammed city read $9,012 of investment against the healthy city's
         * $273, because somebody happened to order a power plant that month.
         * GDP came out sixteen times higher in the city that could not move its
         * goods, and the fixture duly reported that roads make you poorer.
         *
         * A year, on both, and the lump is one twelfth of what it was. This is
         * the same mistake as reading whichever month a loop stopped on, which
         * HouseholdCheck was carrying on the same day.
         */
        double gdpWith = 0, gdpWithout = 0;
        for (int m = 0; m < 12; m++) {
            withRoads.simulateMonths(1);
            without.simulateMonths(1);
            gdpWith += withRoads.getEconomyManager().getMonthGdp();
            gdpWithout += without.getEconomyManager().getMonthGdp();
        }
        gdpWith /= 12;
        gdpWithout /= 12;
        System.out.printf("   GDP over a year: with roads %.1f/mo, without %.1f/mo%n",
                gdpWith, gdpWithout);
        System.out.printf("   and the shops: %.1f/mo against %.1f/mo, for %d people against %d%n",
                withRoads.getEconomyManager().getNationalAccounts().getConsumption(),
                without.getEconomyManager().getNationalAccounts().getConsumption(),
                withRoads.getPopulationManager().getPopulation(),
                without.getPopulationManager().getPopulation());

        /* -------------------------------------------------------------------
           AND THE CLEANER SIGNAL UNDERNEATH IT, WHICH IS NOT CONSUMPTION.

           This used to assert that the road city's shops SELL MORE, on the
           reasoning that consumption is people earning and spending and has no
           capital lump in it. That was true when it was written and stopped
           being true when the shelf price learned to ration.

           Look at what the two cities actually do:

               delivered share   0.889 with roads   0.347 without
               units sold          143                164
               consumption       390.6/mo           403.4/mo

           The CONGESTED city spends more money at the shops. Its lorries cannot
           get through, its shelves are bare, the scarcity mark-up on what does
           arrive is enormous, and its households hand over more cash for fewer
           goods. Consumption measured in money now RISES with congestion, so
           asserting it falls is asserting the opposite of the mechanic.

           (The old assertion passed anyway, by 2% on a four-person population
           gap - a coin landing the right way up rather than a margin. Rent
           becoming a market tipped it: roads eat construction materials, dearer
           materials mean a dearer rent floor, and 2% the right way became 3%
           the wrong way without anything about roads changing.)

           What roads actually do is MOVE GOODS, and the delivered share says so
           by a factor of two and a half. That is the clean signal, it is the
           one this section is about, and unlike consumption it cannot be
           satisfied by charging more for less.
           ------------------------------------------------------------------- */
        double deliveredWith = withRoads.getEconomyManager()
                .getCommercialHandler().getDeliveredShare();
        double deliveredWithout = without.getEconomyManager()
                .getCommercialHandler().getDeliveredShare();
        System.out.printf("   and the shelves: %.0f%% of what customers came for"
                + " against %.0f%%%n", deliveredWith * 100, deliveredWithout * 100);
        assertTrue("...and its shops can actually be supplied",
                deliveredWith > deliveredWithout * 1.5);
        /* -------------------------------------------------------------------
           AND THERE IS NO SECOND OUTCOME ASSERTION HERE, ON PURPOSE.

           This slot has now held two of them and both were wrong, in the same
           way, three days apart.

           It first asserted that the road city PRODUCES more, in money. The
           paragraph above had already worked out why that cannot be trusted -
           a congested city has bare shelves, an enormous scarcity mark-up, and
           hands over more cash for fewer goods - and the assertion had simply
           been surviving on luck. Rebalance stage two took the luck away.

           So it was rewritten to assert POPULATION, on the reasoning that a
           real quantity cannot be faked by a mark-up. That lasted until the
           bank started paying savers a share of its interest income, whereupon
           the road city held FEWER people at every setting of that dial. Which
           is not absurd: deposit interest changes household savings, savings
           change what a household can afford, and affordability is a term in
           the migration target. The two cities differ in one input and then
           differ in everything.

           THE TELL IS IN THE PRINTOUT. Across three settings of a bank
           constant, the roadless city's annual GDP came out at 2,322.9, 1,232.2
           and 1,235.8 while the road city sat at 1,590.1, 1,590.5 and 1,590.8.
           The congested city's figures are noise; the well-supplied one's are
           stable. That is what a money quantity looks like when scarcity is
           setting the price.

           What roads do is MOVE GOODS, and this section already asserts it
           where it can be measured cleanly: deliveredShare is
           deliveredUnits/plannedUnits, a ratio of two REAL quantities that no
           price can touch, and it comes out at 74% against 22% - a factor of
           three and a half, stable across every one of those runs.

           GDP and population are printed above as context. They are the
           weather. The delivered share is the claim.
           ------------------------------------------------------------------- */

        /* ================= 7. across a save ================= */
        System.out.println("\n--- across a save ---");

        // Deliberately saved while congested. The ratio a month was traded at
        // is carried in the save now, and a city whose ratios are all 1 cannot
        // prove that carrying works - the same reason a city with no industry
        // cannot catch an industrial bug.
        Game jammed = new Game(files);
        jammed.run();
        jammed.buildStack(template(jammed, "House"), 300, false);
        jammed.buildStack(template(jammed, "Convenience Store"), shopsFor(jammed, 720), false);
        jammed.simulateMonths(40);

        /*
         * Deliberately saved MID-GROWTH, and that is the point of the fixture.
         *
         * This section used to run the city on until it stopped growing before
         * saving, because a month's statement is written at the start of the
         * month against what the previous month left behind - so in a growing
         * city the report describes a smaller place than the one the save is
         * taken from, and the load rebuilt it from the larger one. Waiting for
         * a steady city took that lag out of the measurement.
         *
         * The statements are carried whole now, so there is nothing left to
         * take out. A city still moving is the harder case and therefore the
         * one worth testing: if any figure below is rebuilt rather than
         * restored, a growing city is where it shows.
         */
        CommercialHandler shops = jammed.getEconomyManager().getCommercialHandler();

        assertTrue("the test city is still growing, which is the hard case",
                !steady(shops));
        assertTrue("the test city is genuinely congested",
                jammed.getInfrastructureManager().isCongested());

        double incomeBefore = jammed.getIncome();
        double ratioBefore = jammed.getRoadRatio();
        double basisBefore = jammed.getEconomyManager().getRoadRatioBasis();
        double grossBefore = jammed.getEconomyManager()
                .getCommercialHandler().getGrossRevenue();

        assertTrue("saved", jammed.saveGame(1, "gridlock").ok);

        Game reloaded = new Game(files);
        reloaded.loadGameSave(1);

        assertTrue("it loaded", reloaded.getLoadFailure() == null);
        close("the network came back", reloaded.getInfrastructureManager().getLoad(),
                jammed.getInfrastructureManager().getLoad());
        close("...at the same capacity",
                reloaded.getInfrastructureManager().getCapacity(),
                jammed.getInfrastructureManager().getCapacity());
        close("...and the same throughput", reloaded.getRoadRatio(), ratioBefore);

        // The one that caught a real bug: the ratio the month was TRADED at is
        // not the ratio the city ends the month showing, and recomputing the
        // report from the latter reports revenue nobody earned.
        close("the ratio the month was traded at came back",
                reloaded.getEconomyManager().getRoadRatioBasis(), basisBefore);
        close("...so retail revenue is unchanged",
                reloaded.getEconomyManager().getCommercialHandler().getGrossRevenue(),
                grossBefore);
        /*
         * Exact, on a growing city, with the roads jammed.
         *
         * This was a bounded assertion for about an hour, with a note saying
         * the residual was one month of lag in the food price the industrial
         * statement was written against - true, and the fourth input in a row
         * to be found underneath the last one. The month's statements are
         * carried whole now rather than rebuilt from their ingredients, so
         * there is no fifth.
         */
        System.out.printf("   income %.6f -> %.6f%n", incomeBefore, reloaded.getIncome());
        close("...and so is next month's income", reloaded.getIncome(), incomeBefore);

        close("...and the industrial statement, which was the last to drift",
                reloaded.getEconomyManager().getIndustrialHandler().getGrossRevenue(),
                jammed.getEconomyManager().getIndustrialHandler().getGrossRevenue());

        assertTrue("the basis is NOT just the current ratio, or this proved nothing",
                Math.abs(basisBefore - 1) > 1e-9);

        /* ================= 7b. THREE ROADS, AND ALL THREE USEFUL =================

           A building that loses to something else at EVERY price is not a
           choice, it is a mistake the player is allowed to make. Studio
           Apartments are that today - houses beat them below $38.81 a square
           foot and low-rises from $32.55, so there is no land price at which a
           studio is the right answer, and nothing in the game says so.

           The three roads were costed to avoid exactly that, and costing is not
           a promise. This is the promise: each of them is the cheapest road per
           trip carried somewhere in the range of land prices the game actually
           reaches, and every one of those bands is non-empty.

           The comparison has to be PER TRIP. A gravel road is cheaper than an
           elevated highway in every column and carries 40% of the traffic, so
           comparing sticker prices would "prove" the gravel road dominates when
           it does nothing of the kind.
           ================================================================= */
        System.out.println("\n--- three roads, and each of them wins somewhere ---");

        BuildingsTemplate gravel   = template(reloaded, "Gravel Road");
        BuildingsTemplate paved    = template(reloaded, "Paved Road");
        BuildingsTemplate elevated = template(reloaded, "Elevated Highway");
        BuildingsTemplate[] roads  = { gravel, paved, elevated };

        for (BuildingsTemplate r : roads) {
            assertTrue(r.getName() + " carries traffic",     r.getCapacity() > 0);
            assertTrue(r.getName() + " generates none",      r.getRoadLoad() == 0);
            assertTrue(r.getName() + " employs nobody",      r.getTotalJobs() == 0);
            assertTrue(r.getName() + " takes months",        r.getConstructionPoints() > 0);
            assertTrue(r.getName() + " occupies ground",     r.getLandSqFt() > 0);
        }

        /*
         * THE TRADE ITSELF, asserted rather than described.
         *
         * Jerus asked for one that is cheap in construction and hungry for land
         * and one that is the reverse. If a rebalance ever quietly undid that,
         * the three would still all be "useful" by the band test below while no
         * longer being the three things they were asked to be.
         */
        double gravelPoints   = gravel.getConstructionPoints()   / (double) gravel.getCapacity();
        double pavedPoints    = paved.getConstructionPoints()    / (double) paved.getCapacity();
        double elevatedPoints = elevated.getConstructionPoints() / (double) elevated.getCapacity();

        double gravelLand   = gravel.getLandSqFt()   / (double) gravel.getCapacity();
        double pavedLand    = paved.getLandSqFt()    / (double) paved.getCapacity();
        double elevatedLand = elevated.getLandSqFt() / (double) elevated.getCapacity();

        System.out.printf("  per 1,000 trips:  %-18s %8s %12s %10s%n",
                "", "points", "land sqft", "power");
        for (BuildingsTemplate r : roads) {
            System.out.printf("                    %-18s %8.0f %12.0f %10.1f%n", r.getName(),
                    r.getConstructionPoints() * 1000.0 / r.getCapacity(),
                    r.getLandSqFt() * 1000.0 / r.getCapacity(),
                    r.getElectricityConsumption() * 1000.0 / r.getCapacity());
        }

        assertTrue("the gravel road is the cheapest to build per trip",
                gravelPoints < pavedPoints && pavedPoints < elevatedPoints);
        assertTrue("...and the hungriest for ground per trip",
                gravelLand > pavedLand && pavedLand > elevatedLand);
        assertTrue("the elevated highway draws the most power per trip",
                elevated.getElectricityConsumption() / (double) elevated.getCapacity()
                > paved.getElectricityConsumption() / (double) paved.getCapacity());
        assertTrue("...and the gravel road the least",
                gravel.getElectricityConsumption() / (double) gravel.getCapacity()
                < paved.getElectricityConsumption() / (double) paved.getCapacity());

        /*
         * AND NOW THE BANDS.
         *
         * Money for one trip of capacity = (cash + materials x price + land x
         * ground price) / capacity. Only the last term moves with the land
         * price, so each road is a straight line in it and the cheapest of three
         * lines is a step function with at most three steps. Walking the price
         * upward and recording who is cheapest finds them without solving
         * anything.
         *
         * The range walked is the range the game reaches: ground starts at
         * $0.70 a square foot and is multiplied by blocks owned and population,
         * and the housing analysis found live crossovers in the thirties.
         */
        double matPrice = reloaded.getBuildingManager().getConstructionMaterialPrice();
        java.util.Set<String> everCheapest = new java.util.LinkedHashSet<>();
        String cheapestSoFar = null;

        System.out.println("\n  cheapest road per trip, walking the ground price:");
        for (int tenthsOfCent = 0; tenthsOfCent <= 600; tenthsOfCent++) {

            double landPrice = tenthsOfCent / 10.0 * .001;   // $/sq ft, in thousands

            BuildingsTemplate best = null;
            double bestCost = Double.MAX_VALUE;
            for (BuildingsTemplate r : roads) {
                double cost = (r.getCashCost()
                        + r.getConstructionMaterials() * matPrice
                        + r.getLandSqFt() * landPrice) / r.getCapacity();
                if (cost < bestCost) { bestCost = cost; best = r; }
            }

            everCheapest.add(best.getName());
            if (!best.getName().equals(cheapestSoFar)) {
                System.out.printf("    from $%5.2f / sq ft:  %s%n",
                        landPrice * 1000, best.getName());
                cheapestSoFar = best.getName();
            }
        }

        for (BuildingsTemplate r : roads) {
            assertTrue(r.getName() + " is the right answer at SOME land price",
                    everCheapest.contains(r.getName()));
        }
        assertTrue("all three roads win a band, not two of them",
                everCheapest.size() == 3);

        /* ================= 8. a new game forgets the traffic ================= */
        System.out.println("\n--- and a new game starts clear ---");

        reloaded.newGame();
        close("capacity is back to the base",
                reloaded.getInfrastructureManager().getCapacity(),
                InfrastructureManager.BASE_CAPACITY);
        close("nothing is on the roads", reloaded.getInfrastructureManager().getLoad(), 0);
        close("...and throughput is whole again", reloaded.getRoadRatio(), 1);

        cleanUp(root);

        System.out.println(fails == 0
                ? "\nAll checks passed."
                : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}

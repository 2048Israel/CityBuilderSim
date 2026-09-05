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

        // Small enough to stay inside the base network. If this ever starts
        // failing, the opening of the game has become a traffic puzzle, which
        // is not what a first turn should be.
        city.buildStack(template(city, "House"), 40, false);
        city.buildStack(template(city, "Convience Store"), 3, false);
        city.simulateMonths(12);

        System.out.printf("   a starter city: %.0f of %.0f used%n",
                city.getInfrastructureManager().getLoad(),
                city.getInfrastructureManager().getCapacity());
        assertTrue("a starter city still has road to spare",
                city.getInfrastructureManager().getHeadroom() > 0);
        close("...and loses nothing to traffic", city.getRoadRatio(), 1);

        /* ================= 5. growth jams it ================= */
        System.out.println("\n--- growth is what breaks it ---");

        city.buildStack(template(city, "House"), 260, false);
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

        BuildingsTemplate roadNetwork = template(city, "Road Network");

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

        // Funded, and given somewhere to put it. A road is a big lot, and this
        // check is about traffic rather than about whether a jammed city can
        // still raise the money - which it can, by borrowing, elsewhere.
        withRoads.getGovernmentInvestor().spend(-500000);
        withRoads.getLandManager().setOwnedSqFt(
                withRoads.getLandManager().getOwnedSqFt() + roadNetwork.getLandSqFt() * 3);

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
        assertTrue("...and the city that built them is better off for it",
                withRoads.getIncome() > without.getIncome());

        /* ================= 7. across a save ================= */
        System.out.println("\n--- across a save ---");

        // Deliberately saved while congested. The ratio a month was traded at
        // is carried in the save now, and a city whose ratios are all 1 cannot
        // prove that carrying works - the same reason a city with no industry
        // cannot catch an industrial bug.
        Game jammed = new Game(files);
        jammed.run();
        jammed.buildStack(template(jammed, "House"), 300, false);
        jammed.buildStack(template(jammed, "Convience Store"), 6, false);
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

package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ore, from the band it clears in to whether it makes steel worth building.
 *
 * The point of this feature is one number: a Steel Foundry earns about $9,650 a
 * month on a $3.6M asset, which is a quarter of a percent and the reason nobody
 * builds one. If local ore does not move that number substantially, everything
 * else here is decoration.
 *
 * So the last section measures it directly - the same foundry, the same city,
 * with and without a mine - rather than asserting that the parts are wired
 * together and hoping.
 */
public class MiningCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) {
            fails++;
            out.printf("%-58s FAIL  %.6f != %.6f%n", label, actual, expected);
        } else {
            out.printf("%-58s OK%n", label);
        }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /** Buys land until the city can fit what is coming, deposits included. */
    static void makeRoom(Game game, double sqFt, boolean wantDeposit) {
        for (int i = 0; i < 200; i++) {
            if (game.getLandManager().getAvailableSqFt() >= sqFt
                    && (!wantDeposit || game.getLandManager().getIronDeposits() > 0)) {
                return;
            }
            LandParcel target = wantDeposit && game.getLandManager().getIronDeposits() == 0
                    ? game.getLandManager().getMarket().richestDeposit()
                    : game.getLandManager().getMarket().bestValue();
            if (target == null || !game.buyLandParcel(target.getId())) {
                if (!game.buyLandBlock()) return;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. the band ================= */
        out.println("--- the price band ---");

        IronMarket market = new IronMarket();

        assertTrue("the mills' scrap price is the ceiling", market.getScrapPrice() > 0);
        assertTrue("the mines' export price is the floor",
                market.getExportPrice() > 0 && market.getExportPrice() < market.getScrapPrice());
        out.printf("   the band runs $%.0f to $%.0f%n",
                market.getExportPrice() * 1000, market.getScrapPrice() * 1000);

        // Nobody mining: the mills are where they always were, buying scrap.
        market.updatePrice(0, 5000);
        close("no mines - price sits at the scrap ceiling",
                market.getLocalPrice(), market.getScrapPrice());

        // Mining hard, nobody buying: the mines export, and will not go below it.
        market.updatePrice(5000, 0);
        close("no mills - price sits at the export floor",
                market.getLocalPrice(), market.getExportPrice());

        // The one that caught a real design error: demand/supply clamped to 1
        // put a matched pair at the ceiling, so the mine took everything and
        // the mill gained nothing at all.
        market.updatePrice(5000, 5000);
        close("supply meets demand - the middle of the band",
                market.getLocalPrice(),
                (market.getScrapPrice() + market.getExportPrice()) / 2);

        market.updatePrice(15000, 5000);
        assertTrue("three times the ore that is wanted - down near the floor",
                market.getPriceIndex() < .3);

        market.updatePrice(5000, 15000);
        assertTrue("three times the demand - up near the ceiling",
                market.getPriceIndex() > .7);

        // Monotonic: more ore never makes ore dearer.
        double previous = 1;
        boolean falling = true;
        for (int supply = 500; supply <= 40000; supply += 500) {
            market.updatePrice(supply, 5000);
            if (market.getPriceIndex() > previous + 1e-12) falling = false;
            previous = market.getPriceIndex();
        }
        assertTrue("more ore never makes ore dearer", falling);

        // The band is the whole mechanic: outside it, one side would rather
        // trade with the rest of the world.
        boolean insideBand = true;
        for (int supply = 0; supply <= 20000; supply += 250) {
            market.updatePrice(supply, 5000);
            if (market.getLocalPrice() < market.getExportPrice() - 1e-9
                    || market.getLocalPrice() > market.getScrapPrice() + 1e-9) {
                insideBand = false;
            }
        }
        assertTrue("the price never leaves the band", insideBand);

        /* ================= 2. a mine needs ground with ore in it ================= */
        out.println("\n--- a mine needs a deposit ---");

        Path root = Files.createTempDirectory("mining");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game city = new Game(files);
        System.setOut(quiet);
        city.run();
        System.setOut(out);

        BuildingsTemplate mine = template(city, "Iron Mine");

        /*
         * The band's two ends are BUILDING DATA, and this is where that gets
         * checked, because it is not obvious and it wastes an afternoon when
         * forgotten. Setting exportPrice or scrapPrice on IronMarket looks like
         * it works and is silently overwritten the next month:
         * EconomyManager.updateMining() re-reads the floor off the mine's
         * productionModifier1 and priceIronMarket() re-reads the ceiling off
         * the mills' own scrap cost. A whole sweep of the band once came back
         * with thirteen identical rows because of it.
         */
        IronMarket live = city.getEconomyManager().getIronMarket();
        close("the floor IS the mine's export price",
                live.getExportPrice(), mine.getProductionModifier1());
        // Against the TEMPLATE, not the running handler: with no foundry
        // standing the handler's scrap price is 0, and priceIronMarket()
        // deliberately declines to move the ceiling to zero on that basis.
        BuildingsTemplate foundry = template(city, "Steel Foundry");
        close("the ceiling IS what the mills pay for scrap",
                live.getScrapPrice(), foundry.getProductionModifier2());

        assertTrue("the Iron Mine is its own category",
                mine.getCategory() == BuildingType.MINING);
        /* ===================================================================
           THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT
           SHOULD NEVER HAVE BEEN (2026-09-09, rebalance stage two).

           It employed 376 people to lift 2,500 tonnes a month - 30,000 tonnes a
           YEAR, which is a quarry, not a mine. That is about 80 tonnes per
           worker per year against a real open-pit figure in the thousands, and
           the whole of the old iron price band was built on top of it:
           IronMarket's note recorded that the mine's cost was $159 a tonne and
           worked backwards from there to a $200 export floor, which is double
           the real seaborne price.

           Raising wages to real ones made that unpayable - 376 people at real
           wages is $554 a tonne of payroll alone - and the honest fix was the
           crew rather than the price. Sixty people lifting 30,000 t/yr costs
           about $103 a tonne and lives comfortably on $140 ore, which is a real
           domestic pellet price. Three hundred and sixteen jobs left the city
           with it; they were jobs the ore price was paying for and the ore
           price could not pay for.

           So the assertion is now that a mine is a SERIOUS employer, not the
           largest one. What it is measured against is deliberately the smallest
           thing that makes the claim mean anything - a Small Grocery Store -
           rather than a Food Processing Plant, which is a genuinely large
           industrial employer and should out-employ a small mine.
           =================================================================== */
        assertTrue("...and is still a serious employer",
                mine.getTotalJobs() > template(city, "Small Grocery Store").getTotalJobs());
        out.printf("   a mine employs %d people; a grocery %d; a food plant %d%n",
                mine.getTotalJobs(),
                template(city, "Small Grocery Store").getTotalJobs(),
                template(city, "Food Processing Plant").getTotalJobs());

        System.setOut(quiet);
        Game.BuildResult refused = city.buildStack(mine, 1, false);
        System.setOut(out);

        assertTrue("a city with no deposit cannot build one",
                refused == Game.BuildResult.NO_DEPOSIT);
        assertTrue("...and it is refused for the RIGHT reason, not for money",
                refused != Game.BuildResult.NEEDS_FUNDING);

        System.setOut(quiet);
        makeRoom(city, mine.getLandSqFt() * 2, true);
        System.setOut(out);

        assertTrue("buying a parcel with iron gives the city a deposit",
                city.getLandManager().getIronDeposits() > 0);

        System.setOut(quiet);
        Game.BuildResult allowed = city.buildStack(mine, 1, false);
        System.setOut(out);
        assertTrue("...and now the mine can be ordered",
                allowed == Game.BuildResult.SUCCESS);

        System.setOut(quiet);
        Game.BuildResult second = city.buildStack(mine, 1, false);
        System.setOut(out);
        assertTrue("but one deposit only supports one mine",
                second == Game.BuildResult.NO_DEPOSIT);

        /* ================= 3. does it actually pay? ================= */
        out.println("\n--- and now the only question that matters ---");

        /*
         * MEASURED AS A MARGIN, NOT AS A DOLLAR FIGURE
         *
         * This section used to assert "withMine > 35" and "withMine < 200",
         * which are not statements about the design, they are yesterday's
         * numbers written down. The design says: steel is barely worth building
         * on imported scrap, and properly worth building on local ore. That is a
         * margin, and a margin survives rebalancing.
         */
        double[] without = foundryIncome(files, root, false);
        double[] with = foundryIncome(files, root, true);

        double marginWithout = without[1] > 0 ? without[0] / without[1] * 100 : 0;
        double marginWith = with[1] > 0 ? with[0] / with[1] * 100 : 0;

        out.printf("   importing scrap at $%3.0f/t:  $%,9.2fk on $%,.0fk  =  %5.1f%%%n",
                without[2] * 1000, without[0], without[1], marginWithout);
        out.printf("   buying local ore at $%3.0f/t: $%,9.2fk on $%,.0fk  =  %5.1f%%%n",
                with[2] * 1000, with[0], with[1], marginWith);

        /* ===================================================================
           "BARELY BREAKS EVEN" WAS A CONSEQUENCE OF A FAKE STEEL PRICE.

           This asserted a margin between -2% and 4% on imported scrap, and the
           design sentence behind it was that steel is only just worth making
           without a mine next door. That sentence was true, but it was true
           because the game sold steel at $500 a tonne and bought scrap at $400
           - a $100 conversion margin against a real one of $874 (US hot-rolled
           band $1,284, shredded scrap $410, SteelBenchmarker 26 Aug 2026).

           Stage two priced both ends for real. Steel is now scrap plus HALF the
           real conversion margin, which is the discount IronMarket's own note
           always asked for - "a small distant producer is a price taker at both
           ends" - and at that price a mill with a real crew makes about 28% on
           imported scrap. That is what an electric-arc mill does. A mill that
           only just breaks even on its main input was never realistic; it was
           the price being wrong in the mill's favour twice over.

           WHAT THE SECTION IS REALLY FOR SURVIVES INTACT, and it is the next
           assertion rather than this one: local ore has to be a DIFFERENT
           BUSINESS, not a better month. That gap is now 28% against 55%, which
           is wider than it has ever been, so the mine is worth sinking for
           exactly the reason it always was.
           =================================================================== */
        assertTrue("a foundry makes an electric-arc mill's margin on scrap",
                marginWithout > 20 && marginWithout < 36);
        assertTrue("...and it is paying the scrap ceiling to do it",
                Math.abs(without[2] - template(city, "Steel Foundry").getProductionModifier2()) < 1e-6);

        /*
         * REBANDED 2026-09-09, from 25-36%, and the reason is a model change
         * rather than drift: steel is an export and exports are zero-rated, so
         * the mills stopped remitting 15% VAT on every tonne they ship. That
         * was $8.2M a month on Jerus's slot 7 and it was the single biggest
         * thing making heavy industry look like a bad business. The margin the
         * ore buys is what it always was; what changed is that the mill now
         * keeps it. See EconomyManager.settleSalesTax().
         */
        assertTrue("local ore takes steel past half its revenue",
                marginWith > 48 && marginWith < 62);
        assertTrue("...which is a different business, not a better month",
                marginWith - marginWithout > 20);

        /* ============ 4. and is the mine worth sinking? ============ */
        out.println("\n--- a mine has to pay even with nobody to sell to ---");

        /*
         * ASKED OF THE HANDLER, NOT OF A CITY
         *
         * A mine with no mill cannot be staged through Game any more. Steel is
         * profitable enough now that the investment engine builds a foundry
         * within six months, so a city started with a mine and no mill finishes
         * the run with both - which is the mechanic working exactly as intended
         * and completely useless as a control.
         *
         * So the worst case is put to the books directly: every tonne exported,
         * at the floor, with no local buyer at any price.
         */
        MiningHandler worstCase = new MiningHandler();
        double[] rates = new double[11];
        rates[0] = .800; rates[1] = 1.500; rates[4] = 4.000;
        int[] crew = new int[11];
        crew[0] = mine.getJobs(JobType.NO_DIPLOMA);
        crew[1] = mine.getJobs(JobType.DIPLOMA);
        crew[4] = mine.getJobs(JobType.COLLEGE_ENGINEERING);

        double[] fullFill = new double[11];
        java.util.Arrays.fill(fullFill, 1.0);

        double floor = mine.getProductionModifier1();
        worstCase.setCapacityTonnes(mine.getProduction1());
        worstCase.setLocalPrice(floor);
        worstCase.setExportPrice(floor);
        worstCase.setElectricityConsumption(mine.getElectricityConsumption());
        worstCase.setWaterConsumption(mine.getWaterConsumption());
        worstCase.setPricePerWatt(.01);
        worstCase.setPricePerWaterUnit(.05);
        worstCase.updateJobFillRate(fullFill);
        worstCase.updateWages(rates, crew);
        worstCase.settle(mine.getProduction1(), 0);      // nobody local wants any
        worstCase.computeMonthlyReport();

        double exportOnly = worstCase.getReportNetIncome();
        double perTonne = worstCase.getReportOperatingCost() / mine.getProduction1();

        out.printf("   %,.0f t/month at the $%.0f floor%n",
                mine.getProduction1(), floor * 1000);
        out.printf("   costs $%.0f a tonne to lift, sells for $%.0f%n",
                perTonne * 1000, floor * 1000);
        out.printf("   net $%,.2fk a month on a $%,.0fk building%n",
                exportOnly, mine.getCashCost());

        assertTrue("a mine lifts ore for less than the export floor",
                perTonne < floor);
        assertTrue("...so exporting alone is profitable, with no mill anywhere",
                exportOnly > 0);
        assertTrue("...comfortably, not marginally",
                exportOnly / mine.getCashCost() > .02);

        // And the co-location reward: the same mine, with a buyer next door.
        worstCase.setLocalPrice(with[2]);
        worstCase.settle(mine.getProduction1(), 1320);
        worstCase.computeMonthlyReport();
        double withBuyer = worstCase.getReportNetIncome();

        out.printf("   with one mill next door: $%,.2fk a month%n", withBuyer);
        assertTrue("a mill next door is worth more to a mine than exporting",
                withBuyer > exportOnly);

        cleanUp(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * The measurement: the same foundry, in the same city, with and without a
     * mine feeding it.
     *
     * BUILT INSTANTLY, ON PURPOSE
     *
     * Everything here goes up through addStack(..., true) rather than through
     * the construction queue. That is not a shortcut - it is the only way to
     * measure the thing being measured. Ordered normally, a city of 400 houses
     * at construction capacity 100 is still building them a decade later, so
     * both foundries would run at a fraction of capacity for want of a
     * workforce and the figure would be about the build queue rather than about
     * the price of ore.
     *
     * The two cities are otherwise identical and run the same months, so the
     * only difference between the two numbers is where the raw material came
     * from.
     */
    /** @return {net income, revenue, the ore price it traded at} */
    static double[] foundryIncome(GameFiles files, Path root, boolean withMine) throws Exception {

        Path dir = root.resolve(withMine ? "with-mine" : "without-mine");
        GameFiles own = new GameFiles(dir, root.resolve("no-legacy"));

        Game game = new Game(own);
        System.setOut(quiet);
        try {
            game.run();

            /*
             * THE CURRENCY IS HELD STILL. This section measures what a foundry
             * earns on imported scrap against local ore, and every world price
             * in the game now moves with the exchange rate - so a drifting rate
             * would have it measuring the currency instead. Caught the day the
             * rate started moving: scrap had gone from $400 a tonne to $420 and
             * the ceiling assertion below failed on a fixture that was right.
             */
            game.getForeignAccounts().pinRate(1.0);
            /*
             * ...AND THE WORLD'S OWN PRICES TOO, since 2026-09-07. The rate was
             * pinned when scrap moving $400 -> $420 turned this fixture red;
             * world inflation is a second way for the same tonne of scrap to
             * cost something else while the harness is measuring a foundry's
             * margin. Both pins, or neither means anything.
             */
            game.getWorldEconomy().pin();

            BuildingManager buildings = game.getBuildingManager();
            LandManager land = game.getLandManager();

            // Land and ore by fiat - this section is about prices, not planning.
            /*
             * Enough for what goes up and not much more.
             *
             * The first version handed the city 500 million square feet, which
             * quietly broke the measurement: land price rises with how much the
             * city owns, so a city 5,000 blocks wide was being quoted a hundred
             * times the going rate, and the property tax on a mine's 400,000
             * sq ft site swamped its whole margin. The fixture was measuring
             * its own land grab.
             */
            land.setOwnedSqFt(12_000_000);
            /*
             * Exactly one deposit in the mining city and none in the control,
             * so the private sector cannot quietly change the experiment.
             *
             * The investment engine builds mines now. Handing both cities four
             * deposits meant the "without a mine" city grew one on its own
             * within a few years, and the control stopped being a control. One
             * deposit, used by the fixture's own mine, leaves nothing for
             * anybody else to sink.
             */
            land.restoreIron(withMine ? 1 : 0, 20_000_000);

            /*
             * ...and enough of them to staff a bank as well.
             *
             * A Commercial Bank is 278 jobs, and it goes up in this city for the
             * credit-pricing reason below. At 500 houses those jobs came out of
             * the foundry's shift: a mill short of workers pays its whole
             * payroll and sells a fraction of its output, which moved the margin
             * being measured by more than a point - the same class of distortion
             * as the roads and the power below, and fixed the same way.
             */
            buildings.addStack(template(game, "House"), 700, true);
            buildings.addStack(template(game, "Convenience Store"), 8, true);
            buildings.addStack(template(game, "Small Grocery Store"), 2, true);
            buildings.addStack(template(game, "Construction Depot"), 4, true);

            /*
             * Powered, watered and un-jammed, and that is not padding.
             *
             * The first version of this fixture had none of the three, and both
             * foundries came back losing $55k a month - because the city was at
             * 35% road throughput and the measurement was really about traffic.
             * A mill throttled to a third of its output still pays its full
             * payroll, so congestion swamps anything the ore price does.
             *
             * Two Paved Roads against a load of about 1,960 trips, one coal
             * plant and one water plant. All three ratios sit at 1, and what is
             * left in the number is the price of ore.
             */
            buildings.addStack(template(game, "Coal Power Plant"), 1, true);
            buildings.addStack(template(game, "Water Treatment Plant"), 1, true);
            buildings.addStack(template(game, "Paved Road"), 3, true);

            /*
             * ...and healthy, and with somewhere to bury its dead, for exactly
             * the reason above.
             *
             * Sickness is the fourth utilisation ratio and it behaves like the
             * other three: a mill at 82% health pays its whole payroll and sells
             * four fifths of its output, which was worth four and a half points
             * of margin - more than the entire question this fixture is asking.
             * The cemetery is not decoration either: thirty-six months with
             * nowhere to put anybody leaves enough unburied to add fifteen
             * points to the sick rate on its own.
             *
             * A residual 3% remains and cannot be removed - WELL_SERVED_RATE is
             * a floor, because people fall ill in the best-served city on earth.
             */
            buildings.addStack(template(game, "Walk-in Clinic"), 2, true);
            buildings.addStack(template(game, "Memorial Cemetery"), 1, true);

            /*
             * A bank, for the same reason as the roads and the clinic.
             *
             * Business credit is priced off the bank's strain now, and a city
             * with no branches has no lending capacity and pays the full
             * eighteen-point premium on every dollar it borrows - which is the
             * intended answer to "no bank" and has nothing whatever to do with
             * the price of ore. Left out, this fixture measured the cost of
             * having no banking system and reported it as the foundry's margin.
             */
            buildings.addStack(template(game, "Commercial Bank"), 1, true);

            buildings.addStack(template(game, "Steel Foundry"), 1, true);

            if (withMine) {
                buildings.addStack(template(game, "Iron Mine"), 1, true);
            }

            game.simulateMonths(36);

            HeavyIndustryHandler mills = game.getEconomyManager().getHeavyIndustryHandler();

            /*
             * Per foundry, counted off what is STANDING.
             *
             * The investment engine builds foundries of its own now that steel
             * pays, so a fixture that puts up one mill can finish with four -
             * and dividing by the one it asked for reported a single foundry
             * earning more than its entire revenue.
             */
            int standing = Math.max(1, game.getBuildingManager()
                    .getQuantity(template(game, "Steel Foundry").getId()));

            return new double[]{
                mills.getReportNetIncome() / standing,
                mills.getReportRevenue() / standing,
                game.getEconomyManager().getIronMarket().getLocalPrice()
            };

        } finally {
            System.setOut(out);
        }
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}

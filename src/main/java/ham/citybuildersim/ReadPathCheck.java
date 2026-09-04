package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reading the city must not change the city.
 *
 * WHY THIS EXISTS
 *
 * Three of the worst bugs this project has had were the same bug:
 *
 *   printCommercialInfo()      banked a month of net income every time it ran,
 *                              so opening the sector screen twice paid the
 *                              shops twice
 *   getIndustrialTaxIncome()   recomputed industry's month from live fields and
 *                              rewrote two report figures doing it, so a
 *                              reloaded city collected $0 where the live one
 *                              collected $89,347
 *   getStoreIncome()           assigned productsSold - uncapped by stock - on
 *                              the tax path, and updateCommercialHandler() then
 *                              took that quantity off the shelf
 *
 * Each was found by hand, months apart, after it had already corrupted
 * something. The backlog ends with a note recommending "a periodic sweep: any
 * get*() on the tax or income path that assigns a field is a bug waiting for a
 * save to expose it". This is that sweep, automated.
 *
 * HOW IT WORKS
 *
 * It does not inspect the code. It plays a real city into a state where every
 * sector has money moving, fingerprints ~90 fields, then calls every read path
 * the UI and the treasury use - repeatedly, in a jumbled order, the way a player
 * clicking between screens would - and fingerprints again. Any field that moved
 * is a getter that is not a getter, including ones that do not exist yet.
 *
 * The repetition is the point. A read path that mutates ONCE and then settles
 * would pass a before/after comparison; one that accumulates shows up as a field
 * that drifts further the more the screens are opened.
 */
public class ReadPathCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /**
     * Everything a screen can ask the game, called the way a player browsing
     * would call it.
     *
     * Deliberately includes the printers. printCommercialInfo() is where this
     * class of bug started, and the JavaFX sector screens are pure readers of
     * the same report fields these print.
     */
    static void readEverything(Game g) {

        EconomyManager e = g.getEconomyManager();
        CommercialHandler c = e.getCommercialHandler();
        IndustrialHandler ih = e.getIndustrialHandler();
        HeavyIndustryHandler hh = e.getHeavyIndustryHandler();
        MiningHandler mh = e.getMiningHandler();
        ServicesManager s = g.getServicesManager();

        // the treasury's own read - the one that was mutating
        e.getTaxIncome();
        e.getTotalIncome();
        e.getExpenses();
        e.getMonthGdp();

        /*
         * calculateSalesTax() used to be read here. It is settleSalesTax() now -
         * a monthly step that strikes the VAT and assigns it, exactly like
         * chargePropertyTax() - so it has no business in a sweep whose entire
         * premise is that nothing in it changes anything. It is called once a
         * month from the month loop, and getTaxIncome() above reads the result,
         * which is the read path this sweep is actually for.
         */

        // each sector's tax line, read on its own the way the panels do
        c.getBusinessTaxIncome(e.getTaxRate());
        c.getRentIncome();
        ih.getIndustrialTaxIncome(e.getTaxRate());
        hh.getTaxIncome(e.getTaxRate());
        mh.getTaxIncome(e.getTaxRate());

        // the statements themselves
        c.printCommercialInfo();
        ih.printIndustrialInfo();

        // the balance sheets and the aggregates behind the info panels
        e.getCommercialCash();
        e.getRealEstateCash();
        e.getIndustrialCash();
        e.getStoreInventory();
        e.getIndustryFoodInventory();
        c.getLastMonthSales();
        c.neededInventory();
        c.getExpectedPurchase();
        ih.getGrossRevenue();
        ih.getNetIncome();
        hh.getNetIncome();
        mh.getNetIncome();
        mh.getPotentialOutput();

        // services, roads and construction
        s.getEnergyRatio();
        s.getWaterRatio();
        s.getRoadRatio();
        s.getServiceNetIncome();
        s.getConstructionHandler().getAverageFill();
        g.getConstructionOutput();

        // land and ore
        g.getLandManager().getAvailableSqFt();
        g.getLandManager().getPricePerSqFt();
        g.getLandListing();
        e.getIronMarket().getLocalPrice();
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        Path root = Files.createTempDirectory("readpath");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ a city with money moving in every sector ============ */
        out.println("--- a city worth reading ---");

        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.run();

            BuildingManager b = g.getBuildingManager();
            LandManager land = g.getLandManager();

            // Instant builds and land by fiat, for the same reason MiningCheck
            // does it: this is about read paths, not about the build queue.
            land.setOwnedSqFt(14_000_000);
            land.restoreIron(2, 20_000_000);

            b.addStack(template(g, "House"), 500, true);
            b.addStack(template(g, "Convience Store"), 8, true);
            b.addStack(template(g, "Small Grocery Store"), 2, true);
            b.addStack(template(g, "Construction Depot"), 4, true);
            b.addStack(template(g, "Food Processing Plant"), 1, true);
            b.addStack(template(g, "Coal Power Plant"), 1, true);
            b.addStack(template(g, "Water Treatment Plant"), 1, true);
            b.addStack(template(g, "Road Network"), 3, true);
            b.addStack(template(g, "Steel Foundry"), 1, true);
            b.addStack(template(g, "Iron Mine"), 1, true);

            g.simulateMonths(24);
        } finally {
            System.setOut(out);
        }

        out.printf("   month %d, %d people, $%,.0fk cash, %d stacks%n",
                g.getMonth(), g.getPopulationManager().getPopulation(),
                g.getCash(), g.getBuildingManager().getStackCount());

        EconomyManager econ = g.getEconomyManager();
        assertTrue("every sector is actually trading",
                econ.getCommercialHandler().getGrossRevenue() > 0
                        && econ.getIndustrialHandler().getGrossRevenue() > 0
                        && econ.getHeavyIndustryHandler().getReportNetIncome() != 0
                        && econ.getMiningHandler().getReportNetIncome() != 0);

        /* ============ the FIRST read, which is the hard one ============ */
        out.println("\n--- the first read after the month ends ---");

        /*
         * Taken before anything has been read, and that ordering is the whole
         * point of this section.
         *
         * The fifty-pass sweep below cannot catch a read path that mutates
         * IDEMPOTENTLY - one that writes the same wrong value every time. Its
         * own first snapshot has already triggered the write, so pass fifty
         * looks exactly like pass one and nothing appears to move. Item 7 was
         * precisely that shape: getStoreIncome() assigned the same uncapped
         * demand figure on every call.
         *
         * So the figures a read must never touch are captured here, straight
         * out of the month, with the city not yet asked a single question.
         */
        Map<String, Double> untouched = new LinkedHashMap<>();
        CommercialHandler ch = econ.getCommercialHandler();
        untouched.put("retail.productsSold", (double) ch.getProductsSold());
        untouched.put("retail.inventory", (double) ch.getStoreInventory());
        untouched.put("retail.reportSold", (double) ch.getReportProductsSold());
        untouched.put("retail.grossRevenue", ch.getGrossRevenue());
        untouched.put("retail.cash", econ.getCommercialCash());
        untouched.put("industry.inventory", (double) econ.getIndustryFoodInventory());
        untouched.put("industry.cash", econ.getIndustrialCash());
        untouched.put("cash", g.getCash());

        System.setOut(quiet);
        readEverything(g);
        System.setOut(out);

        int firstReadMoved = 0;
        for (Map.Entry<String, Double> entry : untouched.entrySet()) {
            double now = switch (entry.getKey()) {
                case "retail.productsSold" -> ch.getProductsSold();
                case "retail.inventory"    -> ch.getStoreInventory();
                case "retail.reportSold"   -> ch.getReportProductsSold();
                case "retail.grossRevenue" -> ch.getGrossRevenue();
                case "retail.cash"         -> econ.getCommercialCash();
                case "industry.inventory"  -> econ.getIndustryFoodInventory();
                case "industry.cash"       -> econ.getIndustrialCash();
                default                    -> g.getCash();
            };
            if (Math.abs(now - entry.getValue()) > 1e-9) {
                firstReadMoved++;
                out.printf("   MOVED  %-28s %,.4f -> %,.4f%n",
                        entry.getKey(), entry.getValue(), now);
            }
        }
        assertTrue("one pass over the screens moved nothing", firstReadMoved == 0);

        /*
         * And the invariant underneath all of it.
         *
         * Snapshot comparison has a blind spot even here: if a read path
         * mutated a field DURING the month as well, the value captured "before
         * any read" is already the corrupted one, and the read reproduces it
         * faithfully. Nothing appears to move because the damage was done
         * earlier by the same broken call.
         *
         * So state the property directly instead of inferring it from movement.
         * The live productsSold and the statement's rProductsSold are written
         * by one line in computeMonthlyReport(); if they ever disagree,
         * something else has written to one of them, and the shops are billing
         * for a different quantity than they are shipping.
         */
        assertTrue("the live sale figure IS the one on the statement",
                ch.getProductsSold() == ch.getReportProductsSold());

        /* ================ read it, and read it again ================ */
        out.println("\n--- fifty passes over every screen in the game ---");

        System.setOut(quiet);
        Map<String, Double> before = NewGameCheck.snapshot(g);
        for (int i = 0; i < 50; i++) {
            readEverything(g);
        }
        Map<String, Double> after = NewGameCheck.snapshot(g);
        System.setOut(out);

        out.printf("%d fields fingerprinted%n", before.size());

        Map<String, String> moved = new LinkedHashMap<>();
        for (String key : before.keySet()) {
            double a = before.get(key);
            double z = after.get(key);
            if (Math.abs(a - z) > 1e-9) {
                moved.put(key, String.format("%,.4f -> %,.4f", a, z));
            }
        }

        for (Map.Entry<String, String> entry : moved.entrySet()) {
            out.printf("   MOVED  %-34s %s%n", entry.getKey(), entry.getValue());
        }
        assertTrue("reading the city fifty times changed nothing", moved.isEmpty());

        /* ============ and the specific one item 7 was about ============ */
        out.println("\n--- the shops sell what the statement says they sold ---");

        CommercialHandler c = econ.getCommercialHandler();

        int shelfBefore = c.getStoreInventory();
        int onTheStatement = c.getReportProductsSold();

        /*
         * The tax path first, then the REAL sale.
         *
         * The first version of this passed getReportProductsSold() straight into
         * sellInventory() and was therefore vacuous - it proved the report
         * equals itself. The bug lives in the gap between the statement's figure
         * and the live productsSold field that updateCommercialHandler() hands
         * to sellInventory(), so the sale has to go through the same call the
         * month does.
         */
        System.setOut(quiet);
        for (int i = 0; i < 20; i++) {
            econ.getTaxIncome();                 // the path that used to assign
        }
        c.updateCommercialHandler();             // sells, then restocks
        System.setOut(out);

        int restocked = c.getReportLocalImports() + c.getReportGlobalImports();
        assertTrue("the shelf fell by exactly the units on the income statement",
                c.getStoreInventory() == shelfBefore - onTheStatement + restocked);
        out.printf("   %,d on the shelf, %,d sold, %,d restocked, %,d left%n",
                shelfBefore, onTheStatement, restocked, c.getStoreInventory());

        assertTrue("...and the statement never sold more than was in stock",
                onTheStatement <= shelfBefore);
        assertTrue("...and the shelf never goes negative",
                c.getStoreInventory() >= 0);

        /* ============ the tax the city takes is the tax it shows ============ */
        out.println("\n--- and the treasury agrees with the screen ---");

        System.setOut(quiet);
        double collected = c.getBusinessTaxIncome(econ.getTaxRate());
        System.setOut(out);

        double shown = c.getReportTotalTax();
        assertTrue("business tax collected == business tax printed",
                Math.abs(collected - shown) < 1e-9);
        out.printf("   collected $%,.2fk, printed $%,.2fk%n", collected, shown);

        assertTrue("...and it is the two companies taxed separately, not netted",
                Math.abs(shown - (c.getReportRetailTax() + c.getReportRealEstateTax())) < 1e-9);

        /* ============ a rate change reaches the treasury at once ============ */
        out.println("\n--- changing the rate is not a month late ---");

        double lowRate = econ.getTaxRate();
        double lowTake = c.getReportTotalTax();

        System.setOut(quiet);
        econ.getTaxPolicy().setIncomeTaxRate(lowRate * 2);
        g.simulateMonths(1);
        System.setOut(out);

        double highTake = econ.getCommercialHandler().getReportTotalTax();
        out.printf("   at %.0f%%: $%,.2fk    at %.0f%%: $%,.2fk%n",
                lowRate * 100, lowTake, lowRate * 200, highTake);
        assertTrue("doubling the rate moves the very next month's commercial tax",
                highTake > lowTake * 1.5);

        cleanUp(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
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

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
        ham.citybuildersim.sectors.Retail c = g.getSectors().retail();
        ham.citybuildersim.sectors.RealEstate re = g.getSectors().realEstate();
        Sector ih = g.getSectors().industry();
        Sector hh = g.getSectors().heavyIndustry();
        ham.citybuildersim.sectors.Mining mh = g.getSectors().mining();
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
        for (Sector sec : g.getSectors().all()) {
            sec.getProfitTax();
            sec.getNetIncome();
            sec.statement();
            sec.getBalanceSheet();
            sec.getInventoryValue();
            sec.getPayroll();
            sec.getOperatingRate();
            for (Good good : Good.values()) {
                sec.getStock(good);
                sec.getPantry(good);
                sec.getCapacity(good);
                sec.getPlannedOutput(good);
                sec.getCostPerUnit(good);
                sec.getMarginalCostPerUnit(good);
                sec.output(good);
                sec.input(good);
            }
            // the operations page, which is what the sector screen draws
            sec.operations(g);
            e.getAssessedValue(sec);
            e.getMaintenanceCharge(sec.key());
            g.isAutoSubsidised(sec);
            g.getSubsidyPaid(sec);
        }
        re.getRentIncome();
        re.rentBreakEven();
        re.blendedRentTarget();
        re.housingPressure();

        // the statements themselves, as text
        g.getSectorBooks();

        // the aggregates behind the info panels
        c.getLastMonthSales();
        c.getStoreInventory();
        c.getStoreSellPrice();
        c.getFoodPrice();
        c.getSupplyRatio();
        ih.statement();
        ih.getNetIncome();
        hh.getNetIncome();
        mh.getNetIncome();
        mh.getPotentialOutput();
        g.getSectors().totalCash();

        // services, roads and construction
        s.getEnergyRatio();
        s.getWaterRatio();
        s.getRoadRatio();
        s.getServiceNetIncome();
        g.getSectors().construction().getAverageFill();
        g.getConstructionOutput();
        g.quoteBuild(template(g, "House"), 1);

        // land, ore and every market
        g.getLandManager().getAvailableSqFt();
        g.getLandManager().getPricePerSqFt();
        g.getLandListing();
        for (GoodsMarket m : g.getMarkets().all()) {
            m.getLocalPrice();
            m.getDemand();
            m.getSupply();
            m.getPriceIndex();
            m.isShortage();
            m.floor();
            m.ceiling();
        }
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
            b.addStack(template(g, "Convenience Store"), 8, true);
            b.addStack(template(g, "Small Grocery Store"), 2, true);
            b.addStack(template(g, "Construction Depot"), 4, true);
            b.addStack(template(g, "Food Processing Plant"), 1, true);
            b.addStack(template(g, "Coal Power Plant"), 1, true);
            b.addStack(template(g, "Water Treatment Plant"), 1, true);
            b.addStack(template(g, "Paved Road"), 3, true);
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
                g.getSectors().retail().statement().revenue > 0
                        && g.getSectors().industry().statement().revenue > 0
                        && g.getSectors().heavyIndustry().getNetIncome() != 0
                        && g.getSectors().mining().getNetIncome() != 0);

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
        ham.citybuildersim.sectors.Retail ch = g.getSectors().retail();
        untouched.put("retail.productsSold", (double) ch.getProductsSold());
        untouched.put("retail.inventory", (double) ch.getStoreInventory());
        untouched.put("retail.reportSold", (double) ch.getLastMonthSales());
        untouched.put("retail.grossRevenue", ch.statement().revenue);
        untouched.put("retail.pending", ch.pending().revenue());
        untouched.put("retail.cash", ch.getCash());
        untouched.put("industry.inventory", g.getSectors().industry().getStock(Good.FOOD));
        untouched.put("industry.cash", g.getSectors().industry().getCash());
        untouched.put("cash", g.getCash());

        System.setOut(quiet);
        readEverything(g);
        System.setOut(out);

        int firstReadMoved = 0;
        for (Map.Entry<String, Double> entry : untouched.entrySet()) {
            double now = switch (entry.getKey()) {
                case "retail.productsSold" -> ch.getProductsSold();
                case "retail.inventory"    -> ch.getStoreInventory();
                case "retail.reportSold"   -> ch.getLastMonthSales();
                case "retail.grossRevenue" -> ch.statement().revenue;
                case "retail.pending"      -> ch.pending().revenue();
                case "retail.cash"         -> ch.getCash();
                case "industry.inventory"  -> g.getSectors().industry().getStock(Good.FOOD);
                case "industry.cash"       -> g.getSectors().industry().getCash();
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
         * The units sold and the units in the month's ledger are written by
         * one Trade in Retail.sellOwnPriced(); if they ever disagree,
         * something else has written to one of them, and the shops are billing
         * for a different quantity than they are shipping.
         */
        assertTrue("the live sale figure IS the one in the ledger",
                Math.abs(ch.getProductsSold()
                        - ch.pending().unitsSold.getOrDefault(Good.GROCERIES, 0.0)) < 1e-9);

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

        ham.citybuildersim.sectors.Retail c = g.getSectors().retail();

        /*
         * The shelf, across one real month.
         *
         * The old version drove the shops' own sale-and-restock call twenty
         * times over between tax reads, because the bug it caught lived in a
         * report field the tax path was assigning. There is no such field
         * now: the sale is a Trade, the restock is the market's fill, and
         * both land on the same shelf in Markets.clearMonth(). So the law is
         * asserted across a month: what was on the shelf, less what the
         * ledger says was sold, plus what the market delivered, is what is
         * on the shelf now.
         */
        int shelfBefore = c.getStoreInventory();
        System.setOut(quiet);
        for (int i = 0; i < 20; i++) {
            econ.getTaxIncome();                 // the path that used to assign
        }
        g.simulateMonths(1);
        System.setOut(out);

        double sold = c.pending().unitsSold.getOrDefault(Good.GROCERIES, 0.0);
        Sector.Input food = c.input(Good.FOOD);
        double restocked = food.boughtLocal + food.imported;
        assertTrue("the shelf fell by exactly the units in the ledger, plus the restock",
                Math.abs(c.getPantry(Good.FOOD) - (shelfBefore - sold + restocked)) < 1e-6);
        out.printf("   %,d on the shelf, %,.0f sold, %,.0f restocked, %,d left%n",
                shelfBefore, sold, restocked, c.getStoreInventory());

        assertTrue("...and the statement never sold more than was in stock",
                sold <= shelfBefore + 1e-9);
        assertTrue("...and the shelf never goes negative",
                c.getStoreInventory() >= 0);

        /* ============ the tax the city takes is the tax it shows ============ */
        out.println("\n--- and the treasury agrees with the screen ---");

        // Every sector's own deducted figure, plus the bank's, against the
        // two lines the treasury screen prints them on.
        double collected = econ.getBankTax();
        for (Sector sec : g.getSectors().all()) collected += sec.getProfitTax();
        double shown = econ.getBusinessTax() + econ.getIndustrialTax();
        assertTrue("business tax collected == business tax printed",
                Math.abs(collected - shown) < 1e-9);
        out.printf("   collected $%,.2fk, printed $%,.2fk%n", collected, shown);

        assertTrue("...and it is the companies taxed separately, not netted",
                Math.abs(econ.getHeavyIndustryTax()
                        - (g.getSectors().heavyIndustry().getProfitTax()
                        + g.getSectors().mining().getProfitTax())) < 1e-9);

        /* ============ a rate change reaches the treasury at once ============ */
        out.println("\n--- changing the rate is not a month late ---");

        // The two commercial companies together, as the old commercial
        // statement printed them. Retail alone would not do: the city rate
        // is also the VAT rate, so doubling it halves the shops' profit
        // before the profit tax is struck, and the shops' profit tax alone
        // barely moves. The landlords' rent is VAT-exempt, so theirs doubles.
        Sector landlords = g.getSectors().realEstate();
        double lowRate = econ.getTaxRate();
        double lowTake = c.getProfitTax() + landlords.getProfitTax();

        System.setOut(quiet);
        econ.getTaxPolicy().setIncomeTaxRate(lowRate * 2);
        g.simulateMonths(1);
        System.setOut(out);

        double highTake = c.getProfitTax() + landlords.getProfitTax();
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

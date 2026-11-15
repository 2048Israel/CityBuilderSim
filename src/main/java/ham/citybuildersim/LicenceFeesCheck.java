package ham.citybuildersim;

import com.google.gson.Gson;
import ham.citybuildersim.sectors.SpreadPlanner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Licence fees leave a running firm's money and enter this month's current account.
 *
 * WHY (R2c). A patent flag alone proves no payment: these fixtures stand a
 * patented furnace in a 1900-way city, bank actual months on either side of
 * expiry, and require positive payments before testing exemptions. Equal
 * gross alternatives cause the planner and the recipe selector to rank net
 * gains. Nonzero flows cross JSON and the sector restore used by Game's loader.
 */
public final class LicenceFeesCheck {
    private static int fails, passes;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= MoneyAudit.tolerance(Math.max(Math.abs(a), Math.abs(b)));
    }

    static void quietly(Runnable action) {
        PrintStream out = System.out;
        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);
            action.run();
        } finally { System.setOut(out); }
    }

    /** Actual patented catalogue building, actual private firm, real filled posts. */
    static Game town(String label, boolean historical) throws Exception {
        Game g = ResearchCheck.datedCity(label, historical);
        quietly(() -> {
            g.playOnLiterals();
            BuildingManager b = g.getBuildingManager();
            String[] names = { "Gravel Road", "House", "Small Grocery Store", "Mixed Farm", "Grain Farm",
                    "Industrial Bakery", "Bakery", "Coal Power Plant", "Water Treatment Plant" };
            int[] counts = { 6, 400, 4, 2, 1, 2, 2, 1, 1 };
            for (int n = 0; n < names.length; n++) g.buildStack(b.getTemplateByName(names[n]), counts[n], true);
        });
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE1"));
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            g.buildStack(g.getBuildingManager().getTemplate(11), 1, true);
            MaterialsCheck.handWorkers(g);
            // Cause a positive gain. At ordinary 1900 prices this furnace
            // loses money, which cannot exercise a charge on what it adds.
            g.getMarkets().get(Good.STEEL).setWorldPrices(
                    100 * Good.STEEL.worldImportPrice(), 100 * Good.STEEL.worldExportPrice());
        });
        return g;
    }

    static void payment() throws Exception {
        System.out.println("--- 1. a running patented entry pays an imported service in the audit's month ---");
        Game g = town("licence-payment", true);
        check("fixture: a PE1 Electric Arc Furnace really stands in a city founded unfitted",
                g.foundedUnfitted() && g.getBuildingManager().getStack(g.getBuildingManager().getTemplate(11)).getQuantity() == 1);
        quietly(() -> g.simulateMonths(1));
        double fee = LicenceFees.paid(g);
        MoneyAudit.Result a = g.getLastMoneyAudit();
        check("the firm actually paid positive IP charges", fee > 0);
        check("the named imported service is in the same month's audit", fee > 0
                && near(fee, a.figure("- " + Sectors.HEAVY_INDUSTRY + " IP charges")) && a.month == g.getMonth());
        check("its money audit closes with that payment", fee > 0 && Math.abs(a.residual) <= MoneyAudit.tolerance(a.moved()));
        check("the foreign account takes exactly that audit's current account, including the service import",
                fee > 0 && near(g.getForeignAccounts().currentAccount(), a.currentAccount()) && a.tradeOut >= fee);
        check("history records the paid flow", fee > 0 && near(g.getHistorySave().recentTotal("ipCharges", 1), Math.round(fee * 100.0) / 100.0));
        System.out.printf("   paid %.9f, audit residual %.12f%n", fee, a.residual);
    }

    static void exemptions() throws Exception {
        System.out.println("--- 2. expiry, unpatented and local discoveries, and the default founding ---");
        Game g = town("licence-expiry", true);
        int end = ResearchCatalog.knownFrom("PE1") + LicenceFees.TERM_MONTHS;
        ResearchCheck.atMonth(g, end - 2);
        quietly(() -> g.simulateMonths(1));
        double last = LicenceFees.paid(g);
        quietly(() -> g.simulateMonths(1));
        check("the last patent month pays and its twentieth anniversary pays nothing", last > 0 && LicenceFees.paid(g) == 0);

        Game home = town("licence-home", true);
        quietly(() -> home.simulateMonths(1));
        double before = LicenceFees.paid(home);
        home.getResearch().restore(null, null, null, Map.of("PE1", ResearchCatalog.knownFrom("PE1") - 1));
        quietly(() -> home.simulateMonths(1));
        check("the same running entry costs nothing when the city discovered it itself", before > 0 && LicenceFees.paid(home) == 0);

        Game free = town("licence-free", true);
        quietly(() -> free.simulateMonths(1));
        double licensed = LicenceFees.paid(free);
        // Hold the physical plant fixed and change only the catalogue entry's
        // patent status: B5 (barcode scanners) is explicitly unpatented.
        free.getBuildingManager().getTemplate(11).setSince("B5");
        ResearchCheck.atMonth(free, ResearchCatalog.knownFrom("B5"));
        quietly(() -> free.simulateMonths(1));
        check("the identical plant under an unpatented entry pays nothing", licensed > 0
                && !ResearchCatalog.get().entry("B5").fee() && LicenceFees.paid(free) == 0);

        Game plain = town("licence-default", false);
        quietly(() -> plain.simulateMonths(1));
        check("a default-founded city running the patented furnace pays nothing", LicenceFees.paid(plain) == 0);
    }

    static void planners() throws Exception {
        System.out.println("--- 3. equal gross alternatives are ranked after the patent rent ---");
        Game g = town("licence-planner", true);
        BuildingsTemplate t = g.getBuildingManager().getTemplate(11);
        BusinessInvestment p = g.getBusinessInvestment();
        double licensed = p.estimatedMonthlyProfit(t.getSector(), t);
        t.setSince(1900);
        double gross = p.estimatedMonthlyProfit(t.getSector(), t);
        check("fixture: the furnace has positive gross monthly earnings", gross > 0);
        check("the planner's patented earnings are gross less SHARE of that gain",
                gross > 0 && near(licensed, gross * (1 - LicenceFees.SHARE)));
        var paid = new SpreadPlanner.Candidate(t, licensed, 1, null, null, Map.of(), List.of());
        var free = new SpreadPlanner.Candidate(t, gross, 1, null, null, Map.of(), List.of());
        check("offered the patented option first at equal gross gain and cost, the spread planner prefers the unpatented option",
                SpreadPlanner.best(List.of(paid, free)) == free);

        int[] noJobs = new int[JobType.values().length];
        Recipe old = new Recipe("old", "old", "1900", null, false, Map.of(Good.STEEL, 1.0), Map.of(), noJobs, 0, 0, 0, 0);
        Recipe patent = new Recipe("patent", "patent", "PE1", null, true, Map.of(Good.STEEL, 2.0), Map.of(), noJobs, 0, 0, 0, 0);
        Recipe open = new Recipe("open", "open", "B5", null, false, Map.of(Good.STEEL, 2.0), Map.of(), noJobs, 0, 0, 0, 0);
        t.setRecipes(List.of(patent, old, open));
        Recipes.Prices prices = new Recipes.Prices(x -> 1, new double[noJobs.length], 0, 0, 1);
        // B5's history is later than PE1's term: find B5 locally while PE1 is
        // still patented, so both equal alternatives really are available.
        Research found = new Research();
        found.restore(null, null, null, Map.of("B5", g.getMonth()));
        check("fixture: both equal-gross recipes can actually run", Recipes.canRun(t, patent, true, g.getMonth(), found)
                && Recipes.canRun(t, open, true, g.getMonth(), found) && Recipes.value(patent, prices) == Recipes.value(open, prices));
        check("the recipe selector also prefers the unpatented equal-gross option",
                Recipes.best(t, true, g.getMonth(), prices, found) == open);

        // A paid piece actually enables the gain. Both its recipe and the
        // piece name E9, so fitting must lose one fee, never two.
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("E9"));
        Equipment piece = new Equipment("caster", "Caster", "E9", null, Map.of(Good.MACHINERY, .01), 0, 240);
        Recipe fitted = new Recipe("caster", "Caster", "E9", "caster", true,
                Map.of(Good.STEEL, 2.0), Map.of(), noJobs, 0, 0, 0, 0);
        t.setEquipment(List.of(piece));
        t.setRecipes(List.of(fitted, old));
        var cityPrices = Recipes.pricesOf(g);
        double added = Recipes.value(fitted, cityPrices) - Recipes.value(old, cityPrices);
        var verdict = EquipmentFit.verdictForNew(g, t, piece);
        check("fixture: the caster's enabling recipe adds positive output", added > 0);
        check("fitting counts its gain less exactly one licence fee",
                added > 0 && near(verdict.gain(), added * (1 - LicenceFees.SHARE)));
    }

    /** Nonzero statement and history through the actual save objects, with no slot written. */
    static boolean saveChecks() throws Exception {
        Game g = town("licence-save", true);
        quietly(() -> g.simulateMonths(1));
        double paid = LicenceFees.paid(g);
        SaveWriter.Job job = g.prepareSaveForChecks(10, "IP service flow");
        Gson gson = new Gson();
        DataSave saved = gson.fromJson(gson.toJson(job.data()), DataSave.class);
        SectorState state = saved.getSectors().stream().filter(s -> Sectors.HEAVY_INDUSTRY.equals(s.key)).findFirst().orElseThrow();
        Sector firm = g.getSectors().heavyIndustry();
        quietly(() -> firm.restore(state));
        HistorySave back = gson.fromJson(gson.toJson(job.history()), HistorySave.class);
        HistorySave loaded = new HistorySave();
        loaded.restoreFrom(back);
        return paid > 0 && near(paid, firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0))
                && near(loaded.recentTotal("ipCharges", 1), Math.round(paid * 100.0) / 100.0);
    }

    /** Repeated pricing and saved-flow reads leave every fingerprinted money pool unchanged. */
    static boolean readChecks() throws Exception {
        Game g = town("licence-read", true);
        quietly(() -> g.simulateMonths(1));
        double paid = LicenceFees.paid(g);
        Map<String, Double> before = NewGameCheck.snapshot(g);
        for (int n = 0; n < 30; n++) {
            LicenceFees.paid(g);
            g.getHistorySave().recentTotal("ipCharges", 1);
            g.getBusinessInvestment().estimatedMonthlyProfit(Sectors.HEAVY_INDUSTRY, g.getBuildingManager().getTemplate(11));
        }
        return paid > 0 && before.equals(NewGameCheck.snapshot(g)) && LicenceFees.paid(g) == paid;
    }

    public static void main(String[] args) throws Exception {
        payment();
        exemptions();
        planners();
        System.out.println("--- 4. the nonzero flow survives saves and reads ---");
        check("DataSave and HistorySave JSON and the load restorers keep the paid flow", saveChecks());
        check("repeated screen and planner reads keep the paid flow and every money pool still", readChecks());
        System.out.printf("%d passed, %d FAILED%n", passes, fails);
        System.exit(fails == 0 ? 0 : 1);
    }
}

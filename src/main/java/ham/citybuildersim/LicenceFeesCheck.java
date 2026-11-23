package ham.citybuildersim;

import com.google.gson.Gson;
import ham.citybuildersim.sectors.SpreadPlanner;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.Mining;
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
            SubstationCheck.generationWires(g); // PW5: Mining's transmission-only comparison needs a real wires bill.
            // The new village starts at zero residents and zero staffed
            // power. Let workers arrive and the grid run before standing a
            // patented plant: a nameplate alone is not production.
            g.simulateMonths(3);
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
        quietly(() -> g.simulateMonths(2));
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
        // Isolate PE1 while retaining the total across all sectors: R2d also
        // supplies independently patented recipes to this standing village.
        Map<String, Integer> local = new java.util.HashMap<>();
        for (ResearchCatalog.Entry e : ResearchCatalog.get().entries())
            if (!e.code().equals("PE1")) local.put(e.code(), 1);
        g.getResearch().restore(null, null, null, local);
        int end = ResearchCatalog.knownFrom("PE1") + LicenceFees.TERM_MONTHS;
        ResearchCheck.atMonth(g, end - 2);
        quietly(() -> g.simulateMonths(1));
        double last = g.getSectors().heavyIndustry().pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("production in the last patent month accrues a positive bill", last > 0 && g.getMonth() == end - 1);
        quietly(() -> g.simulateMonths(1));
        check("the strike after expiry pays that last bill exactly, while expired production accrues nothing", last > 0
                && near(LicenceFees.paid(g), last) && g.getSectors().heavyIndustry().pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) == 0);
        quietly(() -> g.simulateMonths(1));
        check("the following strike has no post-expiry royalty to pay", last > 0 && LicenceFees.paid(g) == 0);

        Game imported = town("licence-exemptions-control", true);
        ResearchCheck.atMonth(imported, ResearchCatalog.knownFrom("PB1"));
        quietly(() -> imported.simulateMonths(2));
        double before = LicenceFees.paid(imported);
        Game home = town("licence-home", true);
        ResearchCheck.atMonth(home, ResearchCatalog.knownFrom("PB1"));
        home.getResearch().restore(null, null, null, Map.of("PE1", ResearchCatalog.knownFrom("PE1") - 1));
        quietly(() -> home.simulateMonths(2));
        check("the same running entry costs nothing when the city discovered it itself", before > 0 && LicenceFees.paid(home) == 0);

        Game free = town("licence-free", true);
        // Hold the physical plant fixed and change only the catalogue entry's
        // patent status before any production: PB1 is explicitly unpatented,
        // and arrives while the control plant's PE1 is still in its term.
        free.getBuildingManager().getTemplate(11).setSince("PB1");
        ResearchCheck.atMonth(free, ResearchCatalog.knownFrom("PB1"));
        quietly(() -> free.simulateMonths(2));
        check("the identical plant under an unpatented entry pays nothing", before > 0
                && !ResearchCatalog.get().entry("PB1").fee() && LicenceFees.paid(free) == 0);

        Game plain = town("licence-default", false);
        quietly(() -> plain.simulateMonths(2));
        check("a default-founded city running the patented furnace pays nothing", LicenceFees.paid(plain) == 0);
    }

    static void dispatchedGain() throws Exception {
        System.out.println("--- 5. a run cut after production cannot erase its saved royalty (REV-R2c-2) ---");
        Game g = town("licence-run-cut", true);
        BuildingsTemplate cracker = g.getBuildingManager().getTemplateByName("Small Cracking Unit");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom(cracker.getSinceText()));
        quietly(() -> {
            g.buildStack(g.getBuildingManager().getTemplateByName("Oil Refinery"), 1, true);
            g.buildStack(cracker, 2, true);
            MaterialsCheck.handWorkers(g);
            g.getSectors().refining().setRunShareForTest(Refining.RUN_PACE);
            GoodsMarket petrol = g.getMarkets().get(Good.PETROL), crude = g.getMarkets().get(Good.CRUDE);
            petrol.setWorldPrices(100 * Good.PETROL.worldImportPrice(), 100 * Good.PETROL.worldExportPrice());
            petrol.setLocalPrice(100 * Good.PETROL.worldExportPrice() * g.getMarkets().getExchangeRate());
            // Cracking adds value, but the whole barrel loses against its
            // dear crude. The unheld operator cuts next month's run to zero.
            crude.setWorldPrices(10_000 * Good.CRUDE.worldImportPrice(), 10_000 * Good.CRUDE.worldExportPrice());
        });
        Refining ref = g.getSectors().refining();
        quietly(() -> g.simulateMonths(1));
        double bill = ref.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("fixture: an unheld refinery really produced on its crackers, then cut its next run to zero",
                !g.getBusinessInvestment().isHeld(Sectors.REFINING) && ref.monthsRate() > 0
                && ref.monthsFlow().run(RefineryFlow.Kind.CRACKER) > 0 && ref.runShare() == 0);
        check("the completed production leaves a positive pending royalty despite the new zero run", bill > 0);
        Gson gson = new Gson();
        DataSave saved = gson.fromJson(gson.toJson(g.prepareSaveForChecks(10, "dispatched licence").data()), DataSave.class);
        SectorState state = saved.getSectors().stream().filter(s -> Sectors.REFINING.equals(s.key)).findFirst().orElseThrow();
        quietly(() -> {
            ref.pending().otherInputs.clear();
            ref.pending().imports = 0;
            ref.setRunShareForTest(1);
            ref.restore(state);
            ref.restoreBills(state);
        });
        check("a save between production and settlement retains that exact pending bill and the zero run",
                bill > 0 && near(bill, ref.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0)) && ref.runShare() == 0);
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        double paid = ref.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("the next strike pays the saved dispatch, with a named service import and closed money/foreign accounts", bill > 0
                && near(paid, bill) && near(paid, audit.figure("- " + Sectors.REFINING + " IP charges"))
                && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                && near(g.getForeignAccounts().currentAccount(), audit.currentAccount()));
        System.out.printf("   dispatched %.9f, paid %.9f, next run %.3f, residual %.12f%n", bill, paid, ref.runShare(), audit.residual);
    }

    static void standingValue() throws Exception {
        System.out.println("--- 6. an installed furnace earns at home while another would lose (REV-R2c-3) ---");
        Game g = town("licence-standing-furnace", true);
        quietly(() -> {
            g.buildStack(g.getBuildingManager().getTemplateByName("Machine Works"), 1, true);
            MaterialsCheck.handWorkers(g);
            // Hold quantities, so the demand is really served by this one
            // installed furnace. Its customer still produces and buys steel.
            g.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
            g.getBusinessInvestment().holdSector(Sectors.MANUFACTURING);
            GoodsMarket steel = g.getMarkets().get(Good.STEEL), machinery = g.getMarkets().get(Good.MACHINERY);
            steel.setWorldPrices(1000 * Good.STEEL.worldImportPrice(), .000001 * Good.STEEL.worldExportPrice());
            steel.setLocalPrice(500 * Good.STEEL.worldImportPrice() * g.getMarkets().getExchangeRate());
            machinery.setWorldPrices(1e6 * Good.MACHINERY.worldImportPrice(), 1e6 * Good.MACHINERY.worldExportPrice());
            machinery.setLocalPrice(1e6 * Good.MACHINERY.worldExportPrice() * g.getMarkets().getExchangeRate());
        });
        var firm = g.getSectors().heavyIndustry();
        BuildingsTemplate furnace = g.getBuildingManager().getTemplate(11);
        boolean reached = false;
        for (int n = 0; n < 24 && !reached; n++) {
            quietly(() -> g.simulateMonths(1));
            reached = firm.output(Good.STEEL).soldLocal > 0 && firm.lastEarns(furnace) > 0
                    && firm.estimatedMonthlyProfit(furnace, g.getBusinessInvestment()) < 0;
        }
        double standing = firm.lastEarns(furnace), addition = firm.estimatedMonthlyProfit(furnace, g.getBusinessInvestment());
        double bill = firm.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("fixture: the installed patented furnace really supplies local steel at a positive standing gain, while one more loses",
                reached && LicenceFees.payable("PE1", g.getMonth(), g.getResearch()));
        check("the installed furnace accrues positive royalties on that standing gain despite the losing additional-unit appraisal",
                reached && bill > 0);
        // A standing planner also loses the standing royalty. Clearing only
        // its captured memo must raise that read even though the prospective
        // appraisal already has a loss and therefore carries no royalty.
        Map<Integer, Double> captured = g.licenceFeesForSave();
        g.restoreLicenceFees(Map.of());
        double freeStanding = firm.lastEarns(furnace);
        g.restoreLicenceFees(captured);
        double netStanding = firm.lastEarns(furnace);
        check("the standing planner nets a positive standing royalty while the prospective extra unit still loses",
                reached && freeStanding > netStanding && near(freeStanding - netStanding, captured.getOrDefault(11, 0.0))
                && firm.estimatedMonthlyProfit(furnace, g.getBusinessInvestment()) < 0);
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        double paid = firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("its saved standing-value bill is paid exactly as a service import and both accounts close", reached && bill > 0
                && near(paid, bill) && near(paid, audit.figure("- " + Sectors.HEAVY_INDUSTRY + " IP charges"))
                && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                && near(g.getForeignAccounts().currentAccount(), audit.currentAccount()));
        System.out.printf("   standing %.9f, addition %.9f, accrued %.9f, paid %.9f%n", standing, addition, bill, paid);
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
        Recipe open = new Recipe("open", "open", "PB2", null, false, Map.of(Good.STEEL, 2.0), Map.of(), noJobs, 0, 0, 0, 0);
        t.setRecipes(List.of(patent, old, open));
        Recipes.Prices prices = new Recipes.Prices(x -> 1, new double[noJobs.length], 0, 0, 1);
        // PB2 arrives from history while PE1 is still patented. Both options
        // are actually available without inventing an impossibly early find.
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PB2"));
        Research found = g.getResearch();
        check("fixture: both equal-gross recipes can actually run", Recipes.canRun(t, patent, true, g.getMonth(), found)
                && Recipes.canRun(t, open, true, g.getMonth(), found) && LicenceFees.payable("PE1", g.getMonth(), found)
                && !ResearchCatalog.get().entry("PB2").fee() && Recipes.value(patent, prices) == Recipes.value(open, prices));
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

    static void exhaustedDispatch() throws Exception {
        System.out.println("--- 8. a patented mine's profitable final dispatch still owes its royalty (REV-R2c-4) ---");
        Game g = town("licence-last-ore", true);
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("E4"));
        LandManager land = g.getLandManager();
        Deposit.FieldType mag = Deposit.FieldType.MAGNETITE;
        quietly(() -> {
            // THE FIXTURE CAUSES IT (PW1, REV-PW1c): a month struck before the mine opens, so the book is the merit order's, not the founding
            // tariff, and Mining pays the energy and transmission while commerce pays both wires: the mine's owner is billed below commerce.
            g.simulateMonths(1);
            for (int n = 0; n < 300 && land.getSites(Resource.IRON, mag) == 0; n++) {
                LandParcel offer = land.getMarket().richest(Resource.IRON);
                if (offer == null) {
                    Deposit field = LandCheck.nearestUnowned(g.getCityLand(), Resource.IRON);
                    if (field == null) break;
                    offer = MiningCheck.nearestOffer(land.getMarket(), field.x(), field.y());
                }
                if (offer == null) break;
                land.buyParcel(offer.getId(), 1e12, g.getMonth());
            }
            g.buildStack(g.getBuildingManager().getTemplate(Mining.MAGNETITE_MINE), 1, true);
            MaterialsCheck.handWorkers(g);
            // Keep the owned fields fixed; another sector buying a parcel
            // containing fresh magnetite would undo the exhausted premise.
            for (Sector sector : g.getSectors().all()) g.getBusinessInvestment().holdSector(sector.key());
            // Leave one tonne of concentrate in the real typed field. Dear
            // ore makes even that last dispatch profitable after full costs.
            land.extract(Resource.IRON, mag, Math.max(0, land.getRemaining(Resource.IRON, mag) - 3));
            g.getMarkets().get(Good.IRON).setWorldPrices(
                    1e9 * Good.IRON.worldImportPrice(), 1e9 * Good.IRON.worldExportPrice());
        });
        var firm = g.getSectors().mining();
        BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.MAGNETITE_MINE);
        quietly(() -> g.simulateMonths(1));
        Sector.Output row = firm.output(Good.IRON);
        double made = row.produced + row.captive + row.exportBound;
        double bill = firm.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        Map<Integer, Double> captured = g.licenceFeesForSave();
        g.restoreLicenceFees(Map.of());
        double rate = made / mine.makes(Good.IRON);
        var plans = g.getBusinessInvestment();
        double gain = ham.citybuildersim.sectors.ProcessingSector.grossStanding(mine, SpreadPlanner.values(g.getMarkets()), rate)
                - plans.standingRunningCostOf(mine) - plans.standingCostOf(firm, mine)
                - SpreadPlanner.ownCosts(mine, rate, g.getMarkets().getExchangeRate(), firm.powerPriceFor(mine))
                - firm.freightRunning(mine, rate);
        double commerce = g.getEconomyManager().getPricePerWatt();
        g.restoreLicenceFees(captured);
        boolean reached = made > 0 && firm.isWorkedOut(mine)
                && gain > 0 && LicenceFees.payable("E4", g.getMonth(), g.getResearch());
        check("fixture: the patented magnetite mine actually dispatches its last ore profitably and exhausts its typed field", reached);
        check("that final positive dispatch accrues a royalty despite no ore remaining at capture", reached && bill > 0);
        // THE FIXTURE CAUSES IT (PW1, REV-PW1c): the mine given a generating set the size of its draw, so its own generation is in its
        // licence gain; after the month struck above, Mining is billed the energy and transmission, below commerce's both wires.
        double gen0 = mine.getGen(), set = mine.getElectricityConsumption();
        mine.setGen(gen0 + set);
        double withSet = LicenceFees.installedBuildingGain(g, mine, firm, rate);
        mine.setGen(gen0);
        double without = LicenceFees.installedBuildingGain(g, mine, firm, rate);
        check("fixture: after a struck month Mining is billed below commerce, and the mine runs (its rate over 0)",
                firm.powerPriceFor(mine) < commerce && rate > 0 && set > 0);
        check("a mine's licence gain prices its own generation at what its owner is billed (PW1: Mining the energy and transmission, not both wires)",
                near(withSet - without, set * firm.powerPriceFor(mine)) && !near(withSet - without, set * commerce));
        quietly(() -> {
            // A different, unpatented type keeps the sector producing next
            // month. Its aggregate iron row must not pay for the empty mine.
            for (int n = 0; n < 300 && land.getSites(Resource.IRON, Deposit.FieldType.HEMATITE) == 0; n++) {
                LandParcel offer = land.getMarket().richest(Resource.IRON);
                if (offer == null) {
                    Deposit field = LandCheck.nearestUnowned(g.getCityLand(), Resource.IRON);
                    if (field == null) break;
                    offer = MiningCheck.nearestOffer(land.getMarket(), field.x(), field.y());
                }
                if (offer == null) break;
                land.buyParcel(offer.getId(), 1e12, g.getMonth());
            }
            g.buildStack(g.getBuildingManager().getTemplate(Mining.IRON_MINE), 1, true);
            MaterialsCheck.handWorkers(g);
            // Parcels bought for hematite can contain fresh magnetite too.
            // Exhaust that new ground before the second month's condition.
            land.extract(Resource.IRON, mag, land.getRemaining(Resource.IRON, mag));
        });
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        double paid = firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("the final dispatch's saved bill pays next month and both accounts close", reached && bill > 0
                && near(paid, bill) && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                && near(g.getForeignAccounts().currentAccount(), audit.currentAccount()));
        check("unpatented hematite really produces next month without a new royalty on the exhausted magnetite works",
                firm.output(Good.IRON).produced > 1 && firm.isWorkedOut(mine)
                && firm.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) == 0);
        System.out.printf("   hematite works %d, sites %d, output %.9f, pending %.9f%n",
                g.getBuildingManager().getQuantity(Mining.IRON_MINE), land.getSites(Resource.IRON, Deposit.FieldType.HEMATITE),
                firm.output(Good.IRON).produced, firm.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0));
        System.out.printf("   final ore %.9f, positive gain %.9f, accrued %.9f, paid %.9f%n", made, gain, bill, paid);
    }

    static void reviewRegressions() throws Exception {
        System.out.println("--- 4. standing refinery gains and default-city fitting reads (REV-R2c-1) ---");
        Game g = town("licence-installed-refinery", true);
        BuildingsTemplate cracker = g.getBuildingManager().getTemplateByName("Small Cracking Unit");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom(cracker.getSinceText()));
        quietly(() -> {
            g.buildStack(g.getBuildingManager().getTemplateByName("Oil Refinery"), 1, true);
            g.buildStack(cracker, 2, true);
            MaterialsCheck.handWorkers(g);
            g.getBusinessInvestment().holdSector(Sectors.REFINING);
            g.getSectors().refining().setRunShareForTest(1);
            GoodsMarket petrol = g.getMarkets().get(Good.PETROL);
            petrol.setWorldPrices(100 * Good.PETROL.worldImportPrice(), 100 * Good.PETROL.worldExportPrice());
            petrol.setLocalPrice(100 * Good.PETROL.worldExportPrice() * g.getMarkets().getExchangeRate());
        });
        Refining ref = g.getSectors().refining();
        RefineryFlow.Flow flow = ref.flow();
        check("fixture: the installed patented crackers run and consume all their spare feed",
                flow.run(RefineryFlow.Kind.CRACKER) > 0 && Refining.spareFeed(flow, RefineryFlow.Kind.CRACKER) < 1e-6
                && LicenceFees.payable(cracker.getSinceText(), g.getMonth(), g.getResearch()));
        check("fixture: another cracker would lose money because the standing units already take its feed",
                ref.estimatedMonthlyProfit(cracker, g.getBusinessInvestment()) < 0);
        quietly(() -> g.simulateMonths(2));
        double fee = ref.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("the profitable standing crackers pay positive IP charges even with no spare feed for another unit", fee > 0);
        check("that installed-unit payment is the same month's service import and its audit closes", fee > 0
                && near(fee, audit.figure("- " + Sectors.REFINING + " IP charges"))
                && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                && near(g.getForeignAccounts().currentAccount(), audit.currentAccount()));
        System.out.printf("   installed crackers paid %.9f, audit residual %.12f%n", fee, audit.residual);

        Game plain = town("licence-default-fitting", false);
        ResearchCheck.atMonth(plain, ResearchCatalog.knownFrom("E1"));
        BuildingsTemplate steel = plain.getBuildingManager().getTemplateByName("Oxygen Steel Shop");
        Equipment hearth = steel.getEquipment("open-hearth");
        // Cause the oxygen recipe to save money: its earlier open hearth
        // burns coal. At ordinary prices the present catalogue need not make
        // this switch pay, and a zero fee could conceal the default-city bug.
        GoodsMarket coal = plain.getMarkets().get(Good.THERMAL_COAL);
        coal.setWorldPrices(10_000 * Good.THERMAL_COAL.worldImportPrice(), 10_000 * Good.THERMAL_COAL.worldExportPrice());
        Recipes.Prices prices = Recipes.pricesOf(plain, steel);   // the steel works' recipes at what Heavy Industry is billed (PW1, REV-PW1c)
        Recipe oxygen = steel.getRecipe("basic-oxygen"), old = steel.getRecipe("open-hearth");
        double gross = Recipes.value(old, prices) - Recipes.value(oxygen, prices);
        check("fixture: E1 is under patent and its oxygen recipe has a positive gain to license",
                LicenceFees.payable("E1", plain.getMonth(), plain.getResearch())
                && LicenceFees.recipeFee(steel, oxygen, plain.getMonth(), prices, plain.getResearch()) > 0);
        check("E1 supersedes the hearth in the real catalogue: either fitted status runs oxygen, so the piece's gain is zero",
                EquipmentFit.verdictForNew(plain, steel, hearth).gain() == 0 && Recipes.gainOf(plain, steel) == 0
                && Recipes.canRun(steel, oxygen, true, plain.getMonth(), plain.getResearch())
                && Recipes.canRun(steel, oxygen, false, plain.getMonth(), plain.getResearch()));

        // Synthetic required E1 process: no real finite-gain patented recipe
        // piece remains. The tractor's FARM_HORSES gain is still R1d's NaN.
        // Retain a positive default gross-versus-net control on this equipped
        // template without changing the actual catalogue's E1 behaviour.
        int[] posts = new int[JobType.values().length];
        for (JobType job : JobType.values()) posts[job.ordinal()] = oxygen.getJobs(job);
        Recipe required = new Recipe(oxygen.getId(), "Synthetic required E1 process", oxygen.getSince(),
                hearth.getId(), true, oxygen.makes(), oxygen.uses(), posts, oxygen.getElectricity(),
                oxygen.getGen(), oxygen.getWater(), oxygen.getConsumables());
        Recipe bessemer = steel.getRecipe("bessemer");
        steel.setRecipes(List.of(required, old, bessemer));
        gross = Recipes.value(required, prices) - Recipes.value(bessemer, prices);
        check("synthetic fixture: the required patented process has positive gross gain and a positive imported fee",
                gross > 0 && LicenceFees.recipeFee(steel, required, plain.getMonth(), prices, plain.getResearch()) > 0);
        double during = EquipmentFit.verdictForNew(plain, steel, hearth).gain();
        check("a default city's fitting estimate during the patent term keeps the whole gross gain", near(during, gross));
        check("its game-layer recipe gain also carries no royalty", near(Recipes.gainOf(plain, steel), gross));
        ResearchCheck.atMonth(plain, ResearchCatalog.knownFrom("E1") + LicenceFees.TERM_MONTHS);
        check("its fitting estimate is identical before and after expiry at identical prices",
                near(during, EquipmentFit.verdictForNew(plain, steel, hearth).gain()));
    }

    /** A full normal save/load between capture and a standing read; only scratch slot 10. */
    private static boolean standingRoundTrip(Game g) {
        double standingBefore = g.getSectors().heavyIndustry().lastEarns(g.getBuildingManager().getTemplate(11));
        Game loadedGame = new Game(g.getGameFiles());
        boolean written = g.saveGame(10, "IP service flow").ok;
        quietly(() -> {
            loadedGame.loadGameSave(10);
        });
        double standingAfter = loadedGame.getSectors().heavyIndustry().lastEarns(loadedGame.getBuildingManager().getTemplate(11));
        boolean fullLoadKept = written && standingBefore == standingAfter && g.getLicenceFee(11) > 0
                && g.getLicenceFee(11) == loadedGame.getLicenceFee(11);
        if (!fullLoadKept) System.out.printf("   load standing %.12f -> %.12f; memo %.12f -> %.12f; written %s%n",
                standingBefore, standingAfter, g.getLicenceFee(11), loadedGame.getLicenceFee(11), written);
        return fullLoadKept;
    }

    /** Nonzero statement and history through JSON and scratch slot 10's normal load path. */
    static boolean saveChecks() throws Exception {
        Game g = town("licence-save", true);
        quietly(() -> g.simulateMonths(2));
        double paid = LicenceFees.paid(g);
        SaveWriter.Job job = g.prepareSaveForChecks(10, "IP service flow");
        Gson gson = new Gson();
        boolean fullLoadKept = standingRoundTrip(g);
        DataSave saved = gson.fromJson(gson.toJson(job.data()), DataSave.class);
        SectorState state = saved.getSectors().stream().filter(s -> Sectors.HEAVY_INDUSTRY.equals(s.key)).findFirst().orElseThrow();
        Sector firm = g.getSectors().heavyIndustry();
        quietly(() -> firm.restore(state));
        double memo = g.getLicenceFee(11);
        g.restoreLicenceFees(Map.of());
        // Production rows are not restored by a load. The standing cost must
        // come back from the saved memo even with those rows empty.
        Sector.Output row = firm.output(Good.STEEL);
        row.produced = row.exportBound = row.captive = 0;
        g.restoreLicenceFees(saved.getLicenceFees());
        boolean standingKept = memo > 0 && near(g.getLicenceFee(11), memo)
                && near(LicenceFees.forStanding(g, g.getBuildingManager().getTemplate(11)), memo);
        HistorySave back = gson.fromJson(gson.toJson(job.history()), HistorySave.class);
        HistorySave loaded = new HistorySave();
        loaded.restoreFrom(back);
        boolean settled = paid > 0 && near(paid, firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0))
                && near(loaded.recentTotal("ipCharges", 1), Math.round(paid * 100.0) / 100.0);
        double pending = state.ledger.otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        boolean accruedKept = near(loaded.recentTotal("ipAccrued", 1), Math.round(pending * 100.0) / 100.0);
        // A later fill of the live DTO cannot change the snapshot handed to
        // the writer, including the memo's map.
        g.liveSaveForChecks().setLicenceFees(Map.of(11, memo * 2));
        boolean frozen = near(job.data().getLicenceFees().getOrDefault(11, 0.0), memo);
        quietly(firm::strike);
        return settled && standingKept && fullLoadKept && accruedKept && frozen && pending > 0
                && near(pending, firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0));
    }

    /** Repeated pricing and saved-flow reads leave every fingerprinted money pool unchanged. */
    static boolean readChecks() throws Exception {
        Game g = town("licence-read", true);
        quietly(() -> g.simulateMonths(2));
        double paid = LicenceFees.paid(g);
        double pending = g.getSectors().heavyIndustry().pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        double memo = g.getLicenceFee(11);
        Map<String, Double> before = NewGameCheck.snapshot(g);
        boolean loadKept = standingRoundTrip(g);
        for (int n = 0; n < 30; n++) {
            LicenceFees.paid(g);
            g.getHistorySave().recentTotal("ipCharges", 1);
            g.getBusinessInvestment().estimatedMonthlyProfit(Sectors.HEAVY_INDUSTRY, g.getBuildingManager().getTemplate(11));
            g.getSectors().heavyIndustry().lastEarns(g.getBuildingManager().getTemplate(11));
        }
        return loadKept && paid > 0 && pending > 0 && before.equals(NewGameCheck.snapshot(g)) && LicenceFees.paid(g) == paid
                && g.getSectors().heavyIndustry().pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) == pending
                && g.getLicenceFee(11) == memo;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("exhausted")) {
            exhaustedDispatch();
            System.out.printf("%d passed, %d FAILED%n", passes, fails);
            System.exit(fails == 0 ? 0 : 1);
        }
        if (args.length == 1 && args[0].equals("standing")) {
            standingValue();
            System.out.printf("%d passed, %d FAILED%n", passes, fails);
            System.exit(fails == 0 ? 0 : 1);
        }
        payment();
        exemptions();
        planners();
        reviewRegressions();
        dispatchedGain();
        standingValue();
        exhaustedDispatch();
        System.out.println("--- 7. the nonzero flow survives saves and reads ---");
        check("DataSave and HistorySave JSON and the load restorers keep the paid flow", saveChecks());
        check("repeated screen and planner reads keep the paid flow and every money pool still", readChecks());
        System.out.printf("%d passed, %d FAILED%n", passes, fails);
        System.exit(fails == 0 ? 0 : 1);
    }
}

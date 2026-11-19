package ham.citybuildersim;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The city's own patented works pay R2c's patent rent too: a utility whose upgrade came from abroad is billed the patent share of the
 * service it delivers with that upgrade over what it would deliver without it, in a city founded the 1900 way, while the patent runs.
 *
 * WHY (R2c-P). R2c charged the firms only: the city's patented utilities (the water works' rapid sand, the coal plant's steam turbine and
 * pulverised coal, the desalination and wind building entries) used an imported discovery for nothing, and the public planners counted the
 * whole gain. Each fixture here stands a real shortage that the patented piece relieves and asserts the expected fee, worked out
 * independently from the handler's own water rule, before and after it crosses the strike, the audit, the national accounts, the history, a
 * save and a load. A guard (the catalogue's premise, a city founded today, a local find, a berth, a gain of nothing) is asserted beside
 * the thing it guards, so a fee that is wrong in either direction fails by name.
 */
public final class PublicLicenceCheck {
    private static int checks, fails;

    static void check(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        System.out.println((ok ? "  PASS " : "  FAIL ") + label + (ok ? "" : ": " + detail));
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(Math.abs(a), Math.abs(b)) + 1e-12;
    }

    /** The helper harnesses this one borrows narrate through their own streams; wired as HistoricalLicenceCheck.closedSteamInvoice() does, for a run from SaveFileCheck or ReadPathCheck too. */
    static void wire() {
        RecipeCheck.out = System.out; RecipeCheck.quiet = HistoricalRecipeCheck.quiet;
        FittingCheck.out = System.out; FittingCheck.quiet = HistoricalRecipeCheck.quiet;
        EquipmentCheck.out = System.out; EquipmentCheck.quiet = HistoricalRecipeCheck.quiet;
        Forms1900Check.out = System.out; Forms1900Check.quiet = HistoricalRecipeCheck.quiet;
    }

    static void months(Game g, int n) { HistoricalRecipeCheck.silent(() -> g.simulateMonths(n)); }

    /** Research with everything found at home but these codes (HistoricalRecipeCheck.allExcept() takes one). */
    static void allExcept(Game g, String... except) {
        List<String> abroad = List.of(except);
        Map<String, Integer> found = new java.util.HashMap<>();
        for (ResearchCatalog.Entry e : ResearchCatalog.get().entries())
            if (!abroad.contains(e.code())) found.put(e.code(), 1);
        g.getResearch().restore(Map.of(), Map.of(), Map.of(), found);
    }

    /** The statement's imported patent service, of the sector that would carry a firm's bill. */
    static double firmBill(Game g) {
        return g.getSectors().freight().statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
    }

    /* ===================== the fixtures ===================== */

    /** A 1900 town whose one water works carries rapid sand (PE5) from outside, with water-hungry works standing beside it. */
    static final class Works {
        Game g;
        BuildingsTemplate plant;
        BuildingsStacks stack;
        RecipeChange change;
        long mills;
        UtilitiesHandler u() { return g.getServicesManager().getUtilitiesHandler(); }
        /** The nameplate rapid sand adds to the stack that is fitted, by the catalogue's own undo. */
        double delta() { return -change.production() * (stack.getQuantity() - stack.getUnfitted(change.piece())); }
    }

    /**
     * Built at PE5's own month; one month played so the city's draw and staffing are read as they stand; then as many Pulp Mills (11600 water a
     * building) as the works could not serve at full staffing: without the rapid sand (a shortage the piece relieves), or even with it (overload).
     * The caller steps the month that captures the bill.
     */
    static Works waterWorks(String label, String abroad, boolean overload) throws Exception {
        Works w = plantOnly(label, abroad);
        UtilitiesHandler u = w.u();
        BuildingsTemplate mill = w.g.getBuildingManager().getTemplateByName("Pulp Mill");
        double ceiling = UtilitiesHandler.waterOutput(u.getFreshNameplate() - (overload ? 0 : w.delta()), u.getDesalNameplate(), 1,
                u.getFreshCap(), w.g.getBuildingManager().getFoundingEndowments());
        w.mills = (long) Math.floor(Math.max(0, ceiling - u.getWaterConsumption()) / mill.getWaterConsumption()) + 1;
        HistoricalLicenceCheck.stand(w.g, "Pulp Mill", (int) w.mills);
        return w;
    }

    /** A 1900 town at PE5's own month with one water works carrying rapid sand, one month played and nothing yet drawing on it (waterWorks() and smallShortage() stand the mills). */
    static Works plantOnly(String label, String abroad) throws Exception {
        wire();
        Works w = new Works();
        w.g = HistoricalLicenceCheck.town(label, true);
        allExcept(w.g, abroad.isEmpty() ? new String[0] : new String[] { abroad });
        ResearchCheck.atMonth(w.g, ResearchCatalog.knownFrom("PE5"));
        w.plant = HistoricalLicenceCheck.stand(w.g, "Water Treatment Plant", 1);
        w.stack = w.g.getBuildingManager().getStack(w.plant);
        w.change = w.plant.getRecipeChanges().stream().filter(c -> c.code().equals("PE5") && c.piece() != null).findFirst().orElseThrow();
        months(w.g, 1);
        return w;
    }

    /**
     * The least shortage the works can be made short by (REV-R2c-P-1): Pulp Mills one at a time, a month played after each, until the city asks
     * more water than the works make. Three new plants then more than fill what is missing.
     */
    static Works smallShortage(String label) throws Exception {
        Works w = plantOnly(label, "PE5");
        UtilitiesHandler u = w.u();
        long endowments = w.g.getBuildingManager().getFoundingEndowments();
        while (w.mills < 12 && u.getWaterConsumption() <= UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate(),
                u.getAverageUtilityFill(), u.getFreshCap(), endowments)) {
            HistoricalLicenceCheck.stand(w.g, "Pulp Mill", (int) ++w.mills);   // stand() asks for at least this many
            months(w.g, 1);
        }
        return w;
    }

    /**
     * The patent share of what `units` new plants of this works' template add to the water delivered, over `units` lacking the rapid sand, at the
     * planners' tariff (REV-R2c-P-1): worked from the handler's own water rule at rest, the plants beside the city as it stands. Before any weight for
     * the term left.
     */
    static double newFee(Works w, long units) {
        UtilitiesHandler u = w.u();
        long endowments = w.g.getBuildingManager().getFoundingEndowments();
        double with = w.plant.getProduction1(), less = with + w.change.production();
        double demand = u.getWaterConsumption();
        double added = Math.min(demand, UtilitiesHandler.waterOutput(u.getFreshNameplate() + units * with, u.getDesalNameplate(), u.getAverageUtilityFill(),
                u.getFreshCap(), endowments))
                - Math.min(demand, UtilitiesHandler.waterOutput(u.getFreshNameplate() + units * less, u.getDesalNameplate(), u.getAverageUtilityFill(),
                u.getFreshCap(), endowments));
        return LicenceFees.SHARE * Recipes.pricesOf(w.g).pricePerWaterUnit() * added;
    }

    /**
     * The share of a building's life a patent with `termLeft` months to run covers (REV-R2c-P-1), worked here from the city's life rate: the level
     * annuity over the lesser of LIFE_MONTHS and the term left, over the annuity over LIFE_MONTHS; the months' ratio at no rate; nothing at the end.
     */
    static double share(Game g, int termLeft) {
        if (termLeft <= 0) return 0;
        double i = BuildAdvice.lifeRate(g) / 12;
        int n = Math.min(BuildAdvice.LIFE_MONTHS, termLeft);
        return i > 0 ? (1 - Math.pow(1 + i, -n)) / (1 - Math.pow(1 + i, -BuildAdvice.LIFE_MONTHS)) : (double) n / BuildAdvice.LIFE_MONTHS;
    }

    /** What the works' fitting adds to the running cost of an order of plants `monthsAhead` ahead: the order's change with the discovery abroad less the same with it found at home (REV-R2c-P-1). */
    static double licenceOfOrder(Game g, BuildingsTemplate plant, long units, double monthsAhead) {
        BuildAdvice.Ahead at = new BuildAdvice.Ahead(monthsAhead, 1, 0);
        double abroad = BuildAdvice.runningChange(g, Map.of(), Map.of(plant, units), at);
        allExcept(g, "");
        double home = BuildAdvice.runningChange(g, Map.of(), Map.of(plant, units), at);
        allExcept(g, "PE5");
        return abroad - home;
    }

    /** What the works deliver with and without the piece, and the patent share of the difference at the planners' tariff: read at rest, independently. */
    record Water(double demand, double output, double with, double without, double tariff, double gain, double fee) { }

    static Water water(Works w) {
        UtilitiesHandler u = w.u();
        long endowments = w.g.getBuildingManager().getFoundingEndowments();
        double demand = u.getWaterConsumption();
        double output = UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(), endowments);
        double with = Math.min(demand, output);
        double without = Math.min(demand, UtilitiesHandler.waterOutput(u.getFreshNameplate() - w.delta(), u.getDesalNameplate(),
                u.getAverageUtilityFill(), u.getFreshCap(), endowments));
        double tariff = Recipes.pricesOf(w.g).pricePerWaterUnit();
        return new Water(demand, output, with, without, tariff, tariff * (with - without), LicenceFees.SHARE * tariff * (with - without));
    }

    /* ===================== 1. the catalogue's premise ===================== */

    static boolean payableEntry(String code) {
        if (code == null || code.isBlank()) return false;
        ResearchCatalog.Entry e = ResearchCatalog.get().entry(code);
        return e != null && e.fee();
    }

    static void catalogue(Game g) {
        System.out.println("--- 1. the catalogue's premise: only the city's electricity and water works carry a payable entry ---");
        List<String> onCityWorks = new ArrayList<>(), misplaced = new ArrayList<>(), berthEntries = new ArrayList<>(), recipeEntries = new ArrayList<>();
        int berths = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (!g.isCitysToDemolish(t)) continue;
            List<String> codes = new ArrayList<>();
            codes.add(t.getSinceText());
            for (Equipment piece : t.getEquipment()) codes.add(piece.getSince());
            boolean utility = t.getCategory() == BuildingType.ELECTRICITY || t.getCategory() == BuildingType.WATER;
            for (String code : codes) {
                if (!payableEntry(code)) continue;
                onCityWorks.add(t.getName() + " " + code);
                if (!utility) misplaced.add(t.getName() + " " + code);
                if (Ports.isPort(t)) berthEntries.add(t.getName() + " " + code);
            }
            for (Recipe r : t.getRecipes()) if (payableEntry(r.getSince())) recipeEntries.add(t.getName() + " recipe " + r.getId());
            for (RecipeChange c : t.getRecipeChanges()) if (c.piece() == null && payableEntry(c.since())) recipeEntries.add(t.getName() + " change " + c.code());
            if (Ports.isPort(t)) berths++;
        }
        check("fixture: the city's own works carry payable entries in the catalogue, and the five sea terminals and the berth are among its works",
                !onCityWorks.isEmpty() && berths >= 5, onCityWorks + " berths " + berths);
        check("every payable entry on a city work's template or piece sits on an electricity or water work, the only works whose gain the capture values",
                misplaced.isEmpty(), "misplaced " + misplaced);
        check("no berth (the Freight-crewed port templates) carries a payable entry, so no port saving is ever valued as a patent gain",
                berthEntries.isEmpty(), "berths " + berthEntries);
        check("no city work's recipe or free-standing recipe change carries a payable entry (the capture reads a template's and its pieces' only)",
                recipeEntries.isEmpty(), "recipes " + recipeEntries);
        BuildingsTemplate container = g.getBuildingManager().getTemplateByName("Container Terminal");
        ResearchCatalog.Entry e5 = ResearchCatalog.get().entry(container.getSinceText());
        check("the Container Terminal's own entry is in the catalogue and is not patented", e5 != null && !e5.fee() && Ports.isPort(container),
                "entry " + container.getSinceText() + " " + e5);
    }

    /* ===================== 2. a patented piece pays the patent share of what it delivers ===================== */

    static Works payment() throws Exception {
        System.out.println("--- 2. a patented piece on the city's water works pays the patent share of what it delivers ---");
        Works w = waterWorks("pl-water", "PE5", false);
        Game g = w.g;
        UtilitiesHandler u = w.u();
        months(g, 1);
        Water at = water(w);
        double noMove = UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(),
                g.getBuildingManager().getFoundingEndowments());
        check("fixture: the harness's reading of the handler's water rule, moved by nothing, is the output the works hold",
                near(noMove, u.getWaterProduction()), noMove + " vs " + u.getWaterProduction());
        check("fixture: a city founded the 1900 way, its water works one of the city's own, with rapid sand fitted and PE5 abroad inside its patent term",
                g.foundedUnfitted() && g.isCitysToDemolish(w.plant) && w.delta() > 0 && w.mills > 0
                && LicenceFees.payable("PE5", g.getMonth(), g.getResearch()), "delta " + w.delta() + " mills " + w.mills);
        check("fixture: the water-hungry works cause a shortage without the rapid sand, and the piece serves more of the demand",
                at.demand > UtilitiesHandler.waterOutput(u.getFreshNameplate() - w.delta(), u.getDesalNameplate(), u.getAverageUtilityFill(),
                        u.getFreshCap(), g.getBuildingManager().getFoundingEndowments())
                && at.with > at.without, String.format("demand %.3f with %.3f without %.3f", at.demand, at.with, at.without));
        double pending = g.getPublicIpPending();
        System.out.printf("   demand %.6f, output %.6f, with %.6f, without %.6f, expected %.9f, pending %.9f (%d mills)%n",
                at.demand, at.output, at.with, at.without, at.fee, pending, w.mills);
        check("the month's captured bill is the patent share of the water the rapid sand adds, at the planners' tariff",
                at.fee > 0 && near(pending, at.fee), pending + " vs " + at.fee);
        check("the bill is the city's own and not a firm's: no private firm's accrued or paid licence beside it", pending > 0 && LicenceFees.paid(g) == 0 && LicenceFees.accrued(g) == 0,
                "paid " + LicenceFees.paid(g) + " accrued " + LicenceFees.accrued(g));
        check("fixture: nothing has been paid yet, the bill waits for the next strike", g.getPublicIpPaid() == 0, "paid " + g.getPublicIpPaid());
        months(g, 1);
        double paid = g.getPublicIpPaid();
        check("the next strike pays the bill accrued before it, to the bit", pending > 0 && paid == pending, paid + " vs " + pending);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("the payment is in the same month's named audit, which closes, and the foreign accounts take it that month",
                near(paid, audit.figure("- city IP charges")) && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                && LicenceFeesCheck.near(g.getForeignAccounts().currentAccount(), audit.currentAccount()),
                "audit " + audit.figure("- city IP charges") + " residual " + audit.residual);
        // HL1: this caused treasury import must stay beside the household cash identity, never enter it as a household bill.
        MoneyAudit.Households households = g.getLastHouseholdsAudit();
        check("the households' identity closes in the month the treasury pays the public patent licence",
                paid > MoneyAudit.CENT && LongPlaytest.MoneyFixtures.householdsClose(households),
                "licence " + paid + " households residual " + households.residual);
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        check("the national accounts carry it as a budget line and the economy as the month's licence", paid > 0 && na.getPublicIpCharges() == paid
                && g.getEconomyManager().getPublicIpCharges() == paid, na.getPublicIpCharges() + " vs " + paid);
        double sectorImports = 0;
        for (Sector s : g.getSectors().all()) sectorImports += s.statement().imports;
        check("fixture: no reserve crude crosses the edge, so the accounts' imports are the sectors' and the licence",
                g.getReserve().getSettledImports() == 0, "reserve " + g.getReserve().getSettledImports());
        check("the licence is one of the month's imports in the national accounts",
                paid > 0 && near(na.getTotalImports(), sectorImports + paid), na.getTotalImports() + " vs " + (sectorImports + paid));
        check("the history carries the paid flow to cents, and the bill accrued beside it",
                LicenceFeesCheck.near(g.getHistorySave().recentTotal("publicIpCharges", 1), Math.round(paid * 100.0) / 100.0)
                && LicenceFeesCheck.near(g.getHistorySave().recentTotal("publicIpAccrued", 1), Math.round(g.getPublicIpPending() * 100.0) / 100.0)
                && paid > 0 && g.getPublicIpPending() > 0, "history " + g.getHistorySave().recentTotal("publicIpCharges", 1));
        check("the freight statement carries no IP charge of its own beside the paid licence: nothing here is a firm's bill", paid > 0 && firmBill(g) == 0, "freight " + firmBill(g));
        NationalAccounts alone = new NationalAccounts();
        alone.setPublicIpCharges(paid);
        check("a budget with the licence alone spends exactly the licence (the line is in the total expenses)",
                near(alone.getTotalExpenses(), paid) && paid > 0, alone.getTotalExpenses() + " vs " + paid);
        return w;
    }

    /* ===================== 3. the patent ends ===================== */

    static void expiry() throws Exception {
        System.out.println("--- 3. the patent ends TERM_MONTHS after history's month ---");
        Works w = waterWorks("pl-expiry", "PE5", false);
        Game g = w.g;
        int end = ResearchCatalog.get().entry("PE5").worldMonth() + LicenceFees.TERM_MONTHS;
        ResearchCheck.atMonth(g, end - 3);
        int steps = 4;
        double[] pending = new double[steps + 1], paid = new double[steps + 1], expected = new double[steps + 1];
        boolean[] runs = new boolean[steps + 1];
        pending[0] = g.getPublicIpPending();
        for (int k = 1; k <= steps; k++) {
            months(g, 1);
            pending[k] = g.getPublicIpPending();
            paid[k] = g.getPublicIpPaid();
            expected[k] = water(w).fee;
            runs[k] = LicenceFees.payable("PE5", g.getMonth(), g.getResearch());
        }
        int inTerm = 0, outOfTerm = 0;
        boolean everyMonthHasGain = true, accrues = true, none = true, settles = true, accruedInTerm = true;
        for (int k = 1; k <= steps; k++) {
            if (runs[k]) inTerm++; else outOfTerm++;
            everyMonthHasGain &= expected[k] > 0;
            if (runs[k]) { accrues &= near(pending[k], expected[k]); accruedInTerm &= pending[k] > 0; }
            else none &= pending[k] == 0;
            settles &= paid[k] == pending[k - 1];
        }
        boolean lastPaid = false, thenNothing = false;
        for (int k = 2; k < steps; k++) {
            if (runs[k - 1] && !runs[k]) {
                lastPaid = paid[k] == pending[k - 1] && paid[k] > 0;
                thenNothing = paid[k + 1] == 0 && pending[k + 1] == 0;
            }
        }
        System.out.printf("   term ends at month %d; accrued by month %s; paid %s%n", end, java.util.Arrays.toString(pending), java.util.Arrays.toString(paid));
        check("fixture: the months played run through the term's last month and beyond it, and the works hold a gain in every one",
                inTerm >= 2 && outOfTerm >= 2 && everyMonthHasGain, "in " + inTerm + " out " + outOfTerm + " expected " + java.util.Arrays.toString(expected));
        check("the bill accrues, at the independently worked fee, in every month the patent runs (the capture month decides)", accrues, "pending " + java.util.Arrays.toString(pending));
        check("nothing accrues in a month after the patent's last: its gain is the same and its fee is none, where the term's months accrued", none && accruedInTerm, "pending " + java.util.Arrays.toString(pending));
        check("each month's bill is paid at the following strike, the term's last month's included, and nothing is paid after it",
                settles && lastPaid && thenNothing, "paid " + java.util.Arrays.toString(paid) + " pending " + java.util.Arrays.toString(pending));
    }

    /* ===================== 4. a local discovery pays nothing ===================== */

    static void local(Works imported) throws Exception {
        System.out.println("--- 4. a local discovery pays nothing ---");
        Works w = waterWorks("pl-local", "", false);
        Game g = w.g;
        months(g, 1);
        Water at = water(w);
        double[] pending = new double[2], paid = new double[2];
        pending[0] = g.getPublicIpPending();
        paid[0] = g.getPublicIpPaid();
        months(g, 1);
        pending[1] = g.getPublicIpPending();
        paid[1] = g.getPublicIpPaid();
        Water twin = water(imported);
        check("fixture: the same shortage the piece relieves (a gain that would be billed) and the patent's term open, but PE5 found at home",
                at.gain > 0 && at.fee > 0 && at.with > at.without && !LicenceFees.payable("PE5", g.getMonth(), g.getResearch())
                && ResearchCatalog.get().entry("PE5").fee() && g.getMonth() - ResearchCatalog.get().entry("PE5").worldMonth() < LicenceFees.TERM_MONTHS,
                "gain " + at.gain + " fee " + at.fee);
        check("a local find pays nothing in two months where the same works on an imported find paid the patent share",
                pending[0] == 0 && pending[1] == 0 && paid[0] == 0 && paid[1] == 0 && imported.g.getPublicIpPaid() > 0 && twin.fee > 0,
                "local " + pending[0] + "," + pending[1] + "," + paid[0] + "," + paid[1] + " imported paid " + imported.g.getPublicIpPaid());
    }

    /* ===================== 5. an unpatented berth ===================== */

    /** Pulp Mills enough to take the city's demand past what its water makes without this much desalination at full staffing; how many. */
    static long crowd(Game g, double desalPlate) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double ceiling = UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate() - desalPlate, 1, u.getFreshCap(),
                g.getBuildingManager().getFoundingEndowments());
        BuildingsTemplate mill = g.getBuildingManager().getTemplateByName("Pulp Mill");
        long n = (long) Math.floor(Math.max(0, ceiling - u.getWaterConsumption()) / mill.getWaterConsumption()) + 1;
        HistoricalLicenceCheck.stand(g, "Pulp Mill", (int) n);
        return n;
    }

    /** The desalination stack's month, read at rest and worked independently: [demand, delivered with, delivered without, gain over running cost]. */
    static double[] desalReading(Game d, BuildingsTemplate desal, BuildingsStacks sea) {
        UtilitiesHandler w = d.getServicesManager().getUtilitiesHandler();
        long endowments = d.getBuildingManager().getFoundingEndowments();
        double plate = desal.getProduction1() * sea.getQuantity();
        double demand = w.getWaterConsumption();
        double with = Math.min(demand, UtilitiesHandler.waterOutput(w.getFreshNameplate(), w.getDesalNameplate(), w.getAverageUtilityFill(), w.getFreshCap(), endowments));
        double less = Math.min(demand, UtilitiesHandler.waterOutput(w.getFreshNameplate(), w.getDesalNameplate() - plate, w.getAverageUtilityFill(), w.getFreshCap(), endowments));
        double gain = Recipes.pricesOf(d).pricePerWaterUnit() * (with - less) - sea.getQuantity() * BuildAdvice.rawRunningCostAt(d, desal, 0);
        return new double[] { demand, with, less, gain };
    }

    static void berth() throws Exception {
        System.out.println("--- 5. an unpatented berth pays nothing and Freight none ---");
        wire();
        Game g = HistoricalLicenceCheck.town("pl-berth", true);
        // E5 (the container) and E6 (desalination) both abroad, at the later date: the one patented work in the city is the desalination plant.
        allExcept(g, "E5", "E6");
        ResearchCheck.atMonth(g, Math.max(ResearchCatalog.knownFrom("E5"), ResearchCatalog.knownFrom("E6")));
        BuildingsTemplate terminal = HistoricalLicenceCheck.stand(g, "Container Terminal", 1);
        BuildingsTemplate desal = HistoricalLicenceCheck.stand(g, "Desalination Plant", 1);
        BuildingsStacks berth = g.getBuildingManager().getStack(terminal), sea = g.getBuildingManager().getStack(desal);
        months(g, 1);
        long mills = crowd(g, desal.getProduction1() * sea.getQuantity());
        months(g, 2);
        double[] at = desalReading(g, desal, sea);
        double expected = LicenceFees.SHARE * at[3];
        check("fixture: a Container Terminal stands, known in its year and abroad, the city's own berth in a city founded the 1900 way",
                berth != null && berth.getQuantity() >= 1 && g.foundedUnfitted() && g.isCitysToDemolish(terminal) && Ports.isPort(terminal)
                && terminal.knownAt(g.getMonth(), g.getResearch()) && ResearchCatalog.get().entry(terminal.getSinceText()) != null
                && !LicenceFees.payable(terminal.getSinceText(), g.getMonth(), g.getResearch()), "berth " + berth);
        check("fixture: beside it a patented desalination plant the city needs: its gain over its running cost is positive",
                sea.getQuantity() >= 1 && LicenceFees.payable(desal.getSinceText(), g.getMonth(), g.getResearch()) && at[0] > at[2] && at[1] > at[2] && at[3] > 0,
                String.format("demand %.3f with %.3f without %.3f gain %.6f (%d mills)", at[0], at[1], at[2], at[3], mills));
        check("the berth adds nothing to the bill, which is the desalination plant's patent share alone, and Freight's statement carries no IP charge",
                expected > 0 && near(g.getPublicIpPending(), expected) && g.getPublicIpPaid() > 0 && firmBill(g) == 0
                && LicenceFees.publicGain(g, berth, null) == 0, "pending " + g.getPublicIpPending() + " paid " + g.getPublicIpPaid() + " expected " + expected);
    }

    /* ===================== 6. no gain, no fee ===================== */

    static void noGain() throws Exception {
        System.out.println("--- 6. no gain, no fee ---");
        wire();
        Game g = HistoricalLicenceCheck.town("pl-nogain", true);
        allExcept(g, "PE6");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE6"));
        BuildingsTemplate plant = HistoricalLicenceCheck.stand(g, "Coal Power Plant", 1);
        BuildingsStacks stack = g.getBuildingManager().getStack(plant);
        months(g, 2);
        RecipeChange c6 = plant.getRecipeChanges().stream().filter(c -> c.code().equals("PE6") && c.piece() != null).findFirst().orElseThrow();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double delta = -c6.production() * (stack.getQuantity() - stack.getUnfitted(c6.piece()));
        double without = u.getProduction() - delta * u.getAverageUtilityFill();
        check("fixture (a): the grid asks no more than it makes without the steam turbine, the piece fitted inside PE6's term",
                delta > 0 && u.getConsumption() <= without && LicenceFees.payable("PE6", g.getMonth(), g.getResearch()),
                "demand " + u.getConsumption() + " without " + without + " delta " + delta);
        double pendingBefore = g.getPublicIpPending(), paidBefore = g.getPublicIpPaid();
        double gainBefore = LicenceFees.publicGain(g, stack, c6.piece());
        // P2-1 (REV-R2c-P-1): the same surplus, read by the public fitting verdict: with no fee struck on the stack, none is subtracted for a new building.
        Equipment turbine = plant.getEquipment(c6.piece());
        double grossTurbine = RecipeChanges.gainOf(g, plant, turbine);
        double verdictTurbine = EquipmentFit.verdictForNew(g, plant, turbine).gain();
        double billTurbine = LicenceFees.publicBillOf(g, stack), newTurbine = LicenceFees.forNewWork(g, plant, BuildAdvice.rawRunningCostAt(g, plant, 0));
        System.out.printf("   (a) P2-1 surplus: gross %.9f, verdict %.9f, the stack's bill %.9f, one new plant's licence %.9f%n", grossTurbine, verdictTurbine, billTurbine, newTurbine);
        check("P2-1 surplus: with the grid asking no more than it makes without the steam turbine, the public fitting verdict of the piece is its whole gross gain while the patent runs,"
                        + " and the capture's bill and a new plant's licence are nothing",
                grossTurbine > 0 && near(verdictTurbine, grossTurbine) && billTurbine == 0 && newTurbine == 0 && LicenceFees.payable("PE6", g.getMonth(), g.getResearch()),
                "gross " + grossTurbine + " verdict " + verdictTurbine + " bill " + billTurbine + " new " + newTurbine);
        // The control: power-hungry works past what the grid makes at full staffing, so the same turbine now serves demand it did not.
        BuildingsTemplate hungry = null;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates())
            if (!g.isCitysToDemolish(t) && t.knownAt(g.getMonth(), g.getResearch()) && t.getElectricityConsumption() > 0
                    && (hungry == null || t.getElectricityConsumption() > hungry.getElectricityConsumption())) hungry = t;
        double fill = Math.max(u.getAverageUtilityFill(), 1e-6);
        long need = (long) Math.floor(Math.max(0, u.getProduction() / fill - u.getConsumption()) / hungry.getElectricityConsumption()) + 1;
        HistoricalLicenceCheck.stand(g, hungry.getName(), (int) need);
        months(g, 2);
        double short1 = LicenceFees.SHARE * Recipes.pricesOf(g).pricePerWatt()
                * (Math.min(u.getConsumption(), u.getProduction()) - Math.min(u.getConsumption(), u.getProduction() - delta * u.getAverageUtilityFill()));
        System.out.printf("   (a) demand %.1f over %.1f made, %.1f without; %d x %s; fee %.9f pending %.9f%n", u.getConsumption(), u.getProduction(),
                u.getProduction() - delta * u.getAverageUtilityFill(), need, hungry.getName(), short1, g.getPublicIpPending());
        check("(a) the turbine adds nothing the city was short of, so its gain and its bill are nothing though the patent runs; once the grid is short it pays its share",
                gainBefore == 0 && pendingBefore == 0 && paidBefore == 0 && short1 > 0 && near(g.getPublicIpPending(), short1),
                "gain " + gainBefore + " pending " + pendingBefore + " then " + g.getPublicIpPending() + " expected " + short1);

        Game d = HistoricalLicenceCheck.town("pl-desal", true);
        allExcept(d, "E6");
        ResearchCheck.atMonth(d, ResearchCatalog.knownFrom("E6"));
        BuildingsTemplate desal = HistoricalLicenceCheck.stand(d, "Desalination Plant", 1);
        BuildingsStacks sea = d.getBuildingManager().getStack(desal);
        months(d, 2);
        UtilitiesHandler w = d.getServicesManager().getUtilitiesHandler();
        double plate = desal.getProduction1() * sea.getQuantity();
        double supplyWithout = UtilitiesHandler.waterOutput(w.getFreshNameplate(), w.getDesalNameplate() - plate, w.getAverageUtilityFill(),
                w.getFreshCap(), d.getBuildingManager().getFoundingEndowments());
        check("fixture (b): the Desalination Plant's entry is abroad inside its term and the city's supply covers its demand without the plant",
                desal.isSeaWater() && LicenceFees.payable(desal.getSinceText(), d.getMonth(), d.getResearch()) && sea.getQuantity() >= 1
                && w.getWaterConsumption() <= supplyWithout, "demand " + w.getWaterConsumption() + " supply " + supplyWithout);
        double idle = LicenceFees.publicGain(d, sea, null);
        double idlePending = d.getPublicIpPending(), idlePaid = d.getPublicIpPaid();
        System.out.println("   (c) the player's own tariff: none. The water and power prices are constants of UtilitiesHandler (.05 and .01 times PriceLevel.FACTOR);"
                + " no policy, setting or screen moves them (Game only copies them from ServicesManager), so no lever makes a building entry's gain positive; the city's demand does.");

        // (c') the lever that does exist: demand. Water-hungry works past what the city's supply makes without the plant.
        long n = crowd(d, plate);
        months(d, 1);
        double[] at = desalReading(d, desal, sea);
        double pending = d.getPublicIpPending();
        System.out.printf("   (c') demand %.3f, with %.3f, without the plant %.3f, gain %.6f, pending %.6f (%d mills)%n", at[0], at[1], at[2], at[3], pending, n);
        check("fixture (c'): the works fall short without the plant and it serves more of the demand, over its running cost",
                at[0] > at[2] && at[1] > at[2] && at[3] > 0, "demand " + at[0] + " with " + at[1] + " without " + at[2] + " gain " + at[3]);
        check("(b) a building entry that serves no demand has a negative gain (its running cost) and pays nothing, and (c') once the city needs its water"
                        + " it pays the patent share of its gain over its running cost",
                idle < 0 && idlePending == 0 && idlePaid == 0 && near(LicenceFees.publicGain(d, sea, null), at[3]) && near(pending, LicenceFees.SHARE * at[3]),
                "idle gain " + idle + " pending " + idlePending + " then gain " + at[3] + " pending " + pending);
    }

    /* ===================== 7. the planners net the fee ===================== */

    static void planners(Works w) throws Exception {
        System.out.println("--- 7. the planners net the fee the capture would strike (REV-R2c-P-1) ---");
        Game g = w.g;
        UtilitiesHandler served = w.u();
        long servedEndowments = g.getBuildingManager().getFoundingEndowments();
        double servedOutput = UtilitiesHandler.waterOutput(served.getFreshNameplate(), served.getDesalNameplate(), served.getAverageUtilityFill(),
                served.getFreshCap(), servedEndowments);
        Equipment piece = w.plant.getEquipment("rapid-sand");
        double gross = RecipeChanges.gainOf(g, w.plant, piece);
        double netImported = EquipmentFit.verdictForNew(g, w.plant, piece).gain();
        double billOfStack = LicenceFees.publicBillOf(g, w.stack);
        double oneMore = LicenceFees.forNewWork(g, w.plant, BuildAdvice.rawRunningCostAt(g, w.plant, 0));
        allExcept(g, "");   // the same city and prices, the discovery found at home
        double grossLocal = RecipeChanges.gainOf(g, w.plant, piece);
        double netLocal = EquipmentFit.verdictForNew(g, w.plant, piece).gain();
        allExcept(g, "PE5");
        check("fixture: the served city: the works already meet the demand (one more fitted plant delivers nothing more) while its standing stack pays PE5's share",
                served.getWaterConsumption() <= servedOutput && billOfStack > 0 && LicenceFees.payable("PE5", g.getMonth(), g.getResearch()),
                "demand " + served.getWaterConsumption() + " output " + servedOutput + " stack bill " + billOfStack);
        check("P2-1 surplus: in the served city the public fitting verdict of PE5's piece is its whole gross gain, no fee, the patent running; the same as with the discovery local",
                gross > 0 && grossLocal > 0 && oneMore == 0 && near(netImported, gross) && near(netLocal, grossLocal) && near(gross, grossLocal),
                "imported " + netImported + " of " + gross + ", local " + netLocal + " of " + grossLocal + ", one more's licence " + oneMore);

        Works busy = waterWorks("pl-plan-new", "PE5", true);
        months(busy.g, 1);
        Game b = busy.g;
        UtilitiesHandler u = busy.u();
        long endowments = b.getBuildingManager().getFoundingEndowments();
        double demand = u.getWaterConsumption();
        double with = busy.plant.getProduction1();
        double less = with + busy.change.production();
        double fresh = u.getFreshNameplate();
        double added = Math.min(demand, UtilitiesHandler.waterOutput(fresh + with, u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(), endowments))
                - Math.min(demand, UtilitiesHandler.waterOutput(fresh + less, u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(), endowments));
        double expected = LicenceFees.SHARE * Recipes.pricesOf(b).pricePerWaterUnit() * added;
        double raw = BuildAdvice.rawRunningCostAt(b, busy.plant, 0);
        int termLeft = ResearchCatalog.get().entry("PE5").worldMonth() + LicenceFees.TERM_MONTHS - b.getMonth();
        double life = share(b, termLeft);
        double fee = LicenceFees.forNewWork(b, busy.plant, raw);
        double running = BuildAdvice.running(b, busy.plant, Map.of());
        Equipment sand = busy.plant.getEquipment("rapid-sand");
        double grossBusy = RecipeChanges.gainOf(b, busy.plant, sand);
        double verdictBusy = EquipmentFit.verdictForNew(b, busy.plant, sand).gain();
        allExcept(b, "");
        double verdictHome = EquipmentFit.verdictForNew(b, busy.plant, sand).gain();
        allExcept(b, "PE5");
        System.out.printf("   one more plant: demand %.3f, output %.3f, adds %.3f over the unfitted one, fee %.9f, over the %d months left (annuity share %.9f) %.9f, forNewWork %.9f, running %.9f over raw %.9f%n",
                demand, UtilitiesHandler.waterOutput(fresh, u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(), endowments), added, expected, termLeft,
                life, expected * life, fee, running, raw);
        check("fixture: the city asks more water than its works make even with the rapid sand, so one more fitted plant adds more than an unfitted one",
                demand > UtilitiesHandler.waterOutput(fresh, u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(), endowments) && added > 0 && with > less,
                "demand " + demand + " added " + added);
        check("P2-1 shortage: the public fitting verdict of PE5's piece is its gross gain less the fee worked independently for the new building"
                        + " (the patent share of the water it adds), which is above nothing and under the share of the gross; the discovery local keeps the gross",
                expected > 0 && expected < LicenceFees.SHARE * grossBusy && near(verdictBusy, grossBusy - expected) && near(verdictHome, grossBusy),
                "verdict " + verdictBusy + " gross " + grossBusy + " fee " + expected + " local " + verdictHome);
        check("a new patented water plant's licence, worked independently as the patent share of the water the rapid sand adds, weighted by the annuity share"
                        + " the term left covers (the patent has " + termLeft + " months to run, under a life), is what forNewWork charges and what the running cost the planners read adds to the building's own",
                expected > 0 && life > 0 && life < 1 && near(fee, expected * life) && near(running - raw, expected * life),
                "fee " + fee + " running-raw " + (running - raw) + " expected " + expected + " x " + life);
    }

    /* ===================== 7b. the term a new plant's licence is weighed by ===================== */

    static void term() throws Exception {
        System.out.println("--- 7b. a new plant's licence is weighed by the term its patent has left, and is nothing past it (REV-R2c-P-1) ---");
        Works w = waterWorks("pl-term", "PE5", true);
        months(w.g, 1);
        Game g = w.g;
        int end = ResearchCatalog.get().entry("PE5").worldMonth() + LicenceFees.TERM_MONTHS;
        double raw = BuildAdvice.rawRunningCostAt(g, w.plant, 0);
        double fee = newFee(w, 1);
        int k = 6;
        check("fixture: a patent's term is not less than a building's life, so a whole life fits inside it only in the term's first month, and k months left is a short way from its end",
                LicenceFees.TERM_MONTHS >= BuildAdvice.LIFE_MONTHS && k > 0 && k < BuildAdvice.LIFE_MONTHS && fee > 0, "fee " + fee);
        ResearchCheck.atMonth(g, end - k);
        double carried = BuildAdvice.running(g, w.plant, Map.of()) - raw;
        double share = share(g, k);
        System.out.printf("   %d months before expiry: fee %.9f, annuity share %.9f, carried by running() %.9f (forNewWork %.9f)%n", k, fee, share, carried,
                LicenceFees.forNewWork(g, w.plant, raw));
        check("P2-2 term: " + k + " months before the patent ends, running() carries the independent fee times the annuity share of " + k + " months in the life,"
                        + " and that is under the fee", share > 0 && share < 1 && near(carried, fee * share) && carried < fee && carried > 0,
                "carried " + carried + " fee " + fee + " share " + share);
        ResearchCheck.atMonth(g, end - 1);
        double last = BuildAdvice.running(g, w.plant, Map.of()) - raw;
        check("P2-2 term: in the patent's last month it carries the fee times one month's share of the life", near(last, fee * share(g, 1)) && last > 0 && last < carried,
                "last " + last + " expected " + fee * share(g, 1));
        ResearchCheck.atMonth(g, end - LicenceFees.TERM_MONTHS);
        double first = BuildAdvice.running(g, w.plant, Map.of()) - raw;
        check("P2-2 term: in the patent's first month, a whole life inside its term, it carries the whole fee", near(first, fee) && near(share(g, LicenceFees.TERM_MONTHS), 1),
                "first " + first + " fee " + fee);
        ResearchCheck.atMonth(g, end);
        double past = BuildAdvice.running(g, w.plant, Map.of()) - raw;
        check("P2-2 term: past the patent's end it carries nothing", Math.abs(past) <= 1e-12 && LicenceFees.forNewWork(g, w.plant, raw) == 0,
                "past " + past + " forNewWork " + LicenceFees.forNewWork(g, w.plant, raw));

        ResearchCheck.atMonth(g, end - k);
        double[] ahead = { 0, 3, 5, 6, 9, 30 };
        boolean inTerm = true, afterTerm = true;
        StringBuilder seen = new StringBuilder();
        for (double months : ahead) {
            double charged = licenceOfOrder(g, w.plant, 1, months);
            seen.append(String.format(" %.0f:%.9f", months, charged));
            if (months < k) inTerm &= near(charged, fee);
            else afterTerm &= Math.abs(charged) <= 1e-12;
        }
        System.out.println("   runningChange's licence of one plant by months ahead (" + k + " months of term left):" + seen);
        check("P2-2 term: an order of one plant opening before the patent ends is charged the fee, not weighted by a life (each month of the term counts alone)", inTerm, seen.toString());
        check("P2-2 term: an order of one plant opening in or after the patent's last month is charged nothing, the same plant today being charged", afterTerm && fee > 0, seen.toString());
    }

    /* ===================== 7c. an order of plants is billed as one ===================== */

    static void units() throws Exception {
        System.out.println("--- 7c. an order of three plants is charged the bill of three, not three times one's (REV-R2c-P-1) ---");
        Works w = smallShortage("pl-units");
        Game g = w.g;
        UtilitiesHandler u = w.u();
        long endowments = g.getBuildingManager().getFoundingEndowments();
        double demand = u.getWaterConsumption();
        double output = UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate(), u.getAverageUtilityFill(), u.getFreshCap(), endowments);
        double three = UtilitiesHandler.waterOutput(u.getFreshNameplate() + 3 * w.plant.getProduction1(), u.getDesalNameplate(), u.getAverageUtilityFill(),
                u.getFreshCap(), endowments);
        double one = newFee(w, 1), threeFee = newFee(w, 3);
        System.out.printf("   %d mills: demand %.3f, output %.3f, with three more plants %.3f; fee of one %.9f, of three %.9f (three times one %.9f)%n",
                w.mills, demand, output, three, one, threeFee, 3 * one);
        check("fixture: the works fall short, and three new fitted plants more than make up what is missing (the demand binds the delivered water)",
                demand > output && three >= demand && one > 0, "demand " + demand + " output " + output + " with three " + three);
        check("fixture: three plants' fee, worked independently, is above nothing and under three times one plant's", threeFee > 0 && threeFee < 3 * one,
                "three " + threeFee + " one " + one);
        int termLeft = ResearchCatalog.get().entry("PE5").worldMonth() + LicenceFees.TERM_MONTHS - g.getMonth();
        check("fixture: the patent has run its term's first months only, so the order's months are inside it", termLeft > 6 && LicenceFees.payable("PE5", g.getMonth() + 3, g.getResearch()),
                "term left " + termLeft);
        double charged1 = licenceOfOrder(g, w.plant, 1, 0), charged3 = licenceOfOrder(g, w.plant, 3, 0);
        System.out.printf("   runningChange's licence: one plant %.9f, three plants %.9f%n", charged1, charged3);
        check("P2-2 units: runningChange charges one plant the fee of one", near(charged1, one), charged1 + " vs " + one);
        check("P2-2 units: runningChange charges three plants the bill of three, worked independently, which is under three times one plant's fee",
                near(charged3, threeFee) && charged3 < 3 * charged1 && charged3 > 0, "three " + charged3 + " independent " + threeFee + " one " + charged1);
    }

    /* ===================== 7d. what the auto-builder reads of a utility's cost ===================== */

    static void costs(Works w) {
        System.out.println("--- 7d. the auto-builder's costs take out the licence the city paid, not one more's (REV-R2c-P-1) ---");
        Game g = w.g;
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Measure water = BuildAdvice.Measure.of(BuildAdvice.Kind.WATER);
        BuildingManager bm = g.getBuildingManager();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double people = g.getPopulationManager().getPopulation();
        double spend = na.getTotalExpenses() - na.getCapitalSpending() - na.getLandPurchases() - na.getArrearsPaid() - na.getReserveCrudeBought();
        long[] site = bm.getUnderConstructionById();
        double own = 0, ownRunning = 0, sites = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (!AutoBuilder.inRemit(t)) continue;
            int id = t.getId();
            if (water.serves(t)) {
                own += bm.getQuantity(id) * (auto.running(g, t) - BuildAdvice.licenceOf(g, t));
                ownRunning += bm.getQuantity(id) * auto.running(g, t);
            } else if (id < site.length && site[id] > 0 && AutoBuilder.staffedBuilding(t)) sites += site[id] * auto.running(g, t);
        }
        double bill = LicenceFees.publicBillOf(g, w.stack);
        double expected = (spend - own - bill + sites) / people;
        double before = (spend - ownRunning + sites) / people;
        double got = auto.costs(g, water);
        System.out.printf("   water works: people %.0f, spend %.6f, the stack's bill %.9f (pending %.9f, paid %.9f), one more's licence %.9f; costs %.9f, expected %.9f, with q x running %.9f%n",
                people, spend, bill, g.getPublicIpPending(), g.getPublicIpPaid(), BuildAdvice.licenceOf(g, w.plant), got, expected, before);
        check("fixture: the served city: a patent bill is struck on the stack, the next plant would pay none, and the city is founded the 1900 way, its people many",
                g.foundedUnfitted() && bill > 0 && BuildAdvice.licenceOf(g, w.plant) == 0 && people > 0 && w.stack.getQuantity() > 0 && na.getPublicIpCharges() > 0,
                "bill " + bill + " licence " + BuildAdvice.licenceOf(g, w.plant) + " people " + people + " paid " + na.getPublicIpCharges());
        check("the public bill of the stack is the month's bill the capture struck on it", near(bill, g.getPublicIpPending()) && near(bill, LicenceFees.capturePublic(g)),
                bill + " vs pending " + g.getPublicIpPending() + " capture " + LicenceFees.capturePublic(g));
        check("P2-3: costs() for the water measure, over the people, is the spend less q x (running less licenceOf) less the stack's bill, plus the site's terms",
                near(got, expected), got + " vs " + expected);
        check("P2-3: it is the spend's licence it leaves out, not q x running's: under what q x running would leave by the bill over the people",
                near(before - got, bill / people) && got < before, "q x running " + before + " costs " + got + " bill/people " + bill / people);
    }

    /* ===================== 8. the month both ways ===================== */

    static void bothWays() throws Exception {
        System.out.println("--- 8. the month both ways: the same city live and loaded ---");
        Works w = waterWorks("pl-both", "PE5", false);
        Game a = w.g;
        months(a, 1);
        double pending = a.getPublicIpPending();
        GameFiles.Result saved = RecipeCheck.save10(a, "public licence");
        Game b = RecipeCheck.load10(a);
        check("fixture: a bill is accrued and waiting, and the save was written", pending > 0 && saved.ok, "pending " + pending + " saved " + saved.ok);
        check("(a) the loaded city holds the saved bill to the bit", b.getPublicIpPending() == pending, b.getPublicIpPending() + " vs " + pending);
        months(a, 1);
        months(b, 1);
        NationalAccounts na = a.getEconomyManager().getNationalAccounts(), nb = b.getEconomyManager().getNationalAccounts();
        check("(b) the strike pays the same bill in both", a.getPublicIpPaid() == pending && b.getPublicIpPaid() == a.getPublicIpPaid(),
                a.getPublicIpPaid() + " vs " + b.getPublicIpPaid());
        check("the treasury's cash is the same to the bit in both after the month", a.getCash() == b.getCash(), a.getCash() + " vs " + b.getCash());
        check("the budget's licence line and its total expenses are the same to the bit",
                na.getPublicIpCharges() == nb.getPublicIpCharges() && na.getPublicIpCharges() == pending && na.getTotalExpenses() == nb.getTotalExpenses(),
                na.getPublicIpCharges() + "/" + nb.getPublicIpCharges() + " expenses " + na.getTotalExpenses() + " vs " + nb.getTotalExpenses());
        check("the month's next bill is the same to the bit in both", a.getPublicIpPending() == b.getPublicIpPending() && a.getPublicIpPending() > 0,
                a.getPublicIpPending() + " vs " + b.getPublicIpPending());
    }

    /* ===================== 9. a city founded today ===================== */

    static void today() throws Exception {
        System.out.println("--- 9. a city founded today pays nothing ---");
        wire();
        // RecipeCheck.town()'s city, with the three months that bring its workers to the utilities: unstaffed, the works make only the wells'.
        Game g = HistoricalLicenceCheck.town("pl-default", false);
        BuildingsTemplate plant = HistoricalLicenceCheck.stand(g, "Water Treatment Plant", 1);
        BuildingsStacks stack = g.getBuildingManager().getStack(plant);
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE5"));
        BuildingsTemplate mill = g.getBuildingManager().getTemplateByName("Pulp Mill");
        HistoricalLicenceCheck.stand(g, "Pulp Mill", 8);
        months(g, 1);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double gain = LicenceFees.publicGain(g, stack, "rapid-sand");
        check("fixture: the works are short of water, PE5's term is open and its piece would earn a gain in a city founded the 1900 way",
                !g.foundedUnfitted() && LicenceFees.payable("PE5", g.getMonth(), g.getResearch()) && gain > 0 && u.getWaterConsumption() > 0
                && mill != null, String.format("gain %.6f, demand %.1f, output %.1f, fresh %.1f, fill %.4f, month %d, payable %s, changeKnown %s",
                        gain, u.getWaterConsumption(), u.getWaterProduction(), u.getFreshNameplate(), u.getAverageUtilityFill(), g.getMonth(),
                        LicenceFees.payable("PE5", g.getMonth(), g.getResearch()), plant.getRecipeChanges().get(0).knownAt(g.getMonth(), g.getResearch())));
        check("no bill is struck, and no new plant is charged, in a city founded today",
                LicenceFees.capturePublic(g) == 0 && LicenceFees.forNewWork(g, plant, BuildAdvice.rawRunningCostAt(g, plant, 0)) == 0, "");
        months(g, 2);
        check("two months leave nothing pending and nothing paid", g.getPublicIpPending() == 0 && g.getPublicIpPaid() == 0,
                g.getPublicIpPending() + " / " + g.getPublicIpPaid());
        String json = new Gson().toJson(g.prepareSaveForChecks(10, "founded today").data());
        check("its save carries neither key", !json.contains("\"publicIpPending\"") && !json.contains("\"publicIpPaid\""), "keys in file");
    }

    /* ===================== SaveFileCheck and ReadPathCheck ===================== */

    /** The bill across DataSave, HistorySave and the load: nonzero pending and paid, scratch slot 10 only. */
    static boolean saveChecks() throws Exception {
        Works w = waterWorks("pl-save", "PE5", false);
        Game g = w.g;
        months(g, 2);
        double pending = g.getPublicIpPending(), paid = g.getPublicIpPaid();
        SaveWriter.Job job = g.prepareSaveForChecks(10, "public licence");
        Gson gson = new Gson();
        DataSave saved = gson.fromJson(gson.toJson(job.data()), DataSave.class);
        boolean dataKept = pending > 0 && paid > 0 && saved.getPublicIpPending() == pending && saved.getPublicIpPaid() == paid;
        HistorySave back = gson.fromJson(gson.toJson(job.history()), HistorySave.class);
        HistorySave loaded = new HistorySave();
        loaded.restoreFrom(back);
        boolean historyKept = LicenceFeesCheck.near(loaded.recentTotal("publicIpAccrued", 1), Math.round(pending * 100.0) / 100.0)
                && LicenceFeesCheck.near(loaded.recentTotal("publicIpCharges", 1), Math.round(paid * 100.0) / 100.0);
        GameFiles.Result written = RecipeCheck.save10(g, "public licence");
        Game b = RecipeCheck.load10(g);
        // The refresh a harness or a screen makes before the next month must not strike the loaded city's licence as nothing.
        b.getEconomyManager().refreshGovernmentAccounts(0, 0, 0, 0);
        NationalAccounts nb = b.getEconomyManager().getNationalAccounts();
        boolean loadKept = written.ok && b.getPublicIpPending() == pending && b.getPublicIpPaid() == paid
                && nb.getPublicIpCharges() == paid && b.getEconomyManager().getPublicIpCharges() == paid;
        g.liveSaveForChecks().setPublicIpPending(pending * 2);
        boolean frozen = job.data().getPublicIpPending() == pending;
        if (!(dataKept && historyKept && loadKept && frozen))
            System.out.printf("   public licence save: data %s history %s load %s frozen %s (pending %.9f paid %.9f)%n", dataKept, historyKept, loadKept, frozen, pending, paid);
        return dataKept && historyKept && loadKept && frozen;
    }

    /** Reading the bill and its planners again and again changes no money pool and no figure. */
    static boolean readChecks() throws Exception {
        Works w = waterWorks("pl-read", "PE5", true);
        Game g = w.g;
        months(g, 2);
        Map<String, Double> before = NewGameCheck.snapshot(g);
        double raw = BuildAdvice.rawRunningCostAt(g, w.plant, 0);
        double[] first = figures(g, w, raw);
        for (int n = 0; n < 30; n++) figures(g, w, raw);
        double[] last = figures(g, w, raw);
        boolean kept = before.equals(NewGameCheck.snapshot(g)) && java.util.Arrays.equals(first, last) && first[0] > 0 && first[1] > 0 && first[2] > 0 && first[3] > 0;
        if (!kept) System.out.println("   public licence reads: " + java.util.Arrays.toString(first) + " then " + java.util.Arrays.toString(last));
        return kept;
    }

    private static double[] figures(Game g, Works w, double raw) {
        return new double[] { g.getPublicIpPending(), g.getPublicIpPaid(), LicenceFees.publicGain(g, w.stack, "rapid-sand"),
                LicenceFees.forNewWork(g, w.plant, raw), BuildAdvice.running(g, w.plant, Map.of()),
                g.getHistorySave().recentTotal("publicIpCharges", 1), g.getHistorySave().recentTotal("publicIpAccrued", 1) };
    }

    public static void main(String[] args) throws Exception {
        wire();
        catalogue(RecipeCheck.town("pl-catalogue", true));
        Works imported = payment();
        expiry();
        local(imported);
        berth();
        noGain();
        planners(imported);
        term();
        units();
        costs(imported);
        bothWays();
        today();
        System.out.println("--- 10. the bill crosses a save, and a read leaves it still ---");
        check("DataSave and HistorySave JSON, scratch slot 10 and the load keep the bill, the licence line and the series", saveChecks(), "");
        check("repeated reads of the bill, the gain and the planners' figures keep every money pool and every figure still", readChecks(), "");
        System.out.printf("%d passed, %d FAILED%n", checks - fails, fails);
        System.exit(fails != 0 ? 1 : 0);
    }
}

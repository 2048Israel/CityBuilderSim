package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5).
 * WHY. A flat wires charge and unlimited wires passed PW1's price checks. These fixtures change real city stock,
 * the dispatch's demand input, and automation's demand input; each rule also fails against the pre-PW5 model.
 */
public final class SubstationCheck {
    static int fails;
    static final double CARRY = Substations.SUBSTATION_MW * 1000;
    static final Gson JSON = new Gson();
    static final PrintStream OUT = System.out, QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) { OUT.println((ok ? "  OK   " : "  FAIL ") + label); if (!ok) fails++; }
    static boolean near(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }
    static void quietly(Runnable work) { System.setOut(QUIET); try { work.run(); } finally { System.setOut(OUT); } }

    static Game city(String label, int substations) {
        Game g = new Game(GameFiles.scratch("substation-" + label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Substation");
            if (t != null && substations > 0 && g.buildStack(t, substations, true) != Game.BuildResult.SUCCESS)
                throw new AssertionError("fixture substation did not stand");
            g.getServicesManager().updateServices();
        });
        return g;
    }

    /** A fully staffed coal row removes generation scarcity; the city stock still supplies the physical wires. */
    static UtilitiesHandler dispatch(Game g, double peak) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        u.setPlants(Map.of(coal, 10L));
        double[] filled = new double[JobType.values().length]; Arrays.fill(filled, 1);
        u.updateJobFillRate(filled);
        u.updateUtilitiyWages(new double[filled.length], new long[filled.length], new long[filled.length]);
        u.setCalendarMonth(() -> 0);
        u.setWorksPower(0);
        u.setWattsConsumption(peak / PowerDispatch.PLAN_PEAK);
        u.updateEnergyRatio();
        u.strikePower();
        return u;
    }

    static double generated(UtilitiesHandler u, int half) {
        double sum = 0; for (PowerDispatch.Row r : u.getLatestRows()) sum += r.gen[half]; return sum;
    }

    static void carry() {
        OUT.println("--- the carry's two edges and the shared shortfall ---");
        Game g = city("carry", 1);
        UtilitiesHandler u = dispatch(g, CARRY * .99);
        boolean under = u.getEnergyRatio() == 1 && u.getHalfBlocksShort() == 0;
        dispatch(g, CARRY * 1.01);
        boolean over = u.getEnergyRatio() < 1 && u.getHalfBlocksShort() == 2
                && near(generated(u, 4), CARRY) && near(generated(u, 5), CARRY);
        check("a peak below one substation is served; above it only the two peak halves brown out at its carry", under && over);
        boolean conserved = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            PowerDispatch.Month m = u.getLatestMonth();
            conserved &= near(generated(u, s) + m.unserved[s], m.load[s]) && generated(u, s) <= CARRY + 1e-7;
            if (m.unserved[s] > 0) conserved &= near(m.price[s], PowerDispatch.VOLL_A_KWH);
        }
        check("a short grid conserves each half's energy, stops generation before burning fuel, and reports the cap price", conserved && over);
        Game empty = city("starter", 0);
        UtilitiesHandler without = dispatch(empty, 2 * CARRY);
        double starter = without.getLatestRows().stream().filter(r -> "grid".equals(r.name)).mapToDouble(r -> r.kw).sum();
        check("a city without substations carries only its existing free 10 MW starter grid", without.getEnergyRatio() < 1
                && near(generated(without, 4), starter));
    }

    static JsonObject costs(UtilitiesHandler.Book book) {
        JsonObject json = JSON.toJsonTree(book).getAsJsonObject();
        return json.has("gridCosts") ? json.getAsJsonObject("gridCosts") : new JsonObject();
    }
    static double number(JsonObject o, String key) { return o.has(key) ? o.get(key).getAsDouble() : 0; }

    static void tariff() {
        OUT.println("--- the substations' own costs, struck once ---");
        Game g = city("costs", 1);
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Substation");
        UtilitiesHandler u = dispatch(g, CARRY * .9);
        UtilitiesHandler.Book book = u.getStruckBook();
        JsonObject costs = costs(book);
        double value = t == null ? 0 : g.getEconomyManager().withBuildersTax(g.getBuildingManager().structureCost(t));
        double annuity = Substations.REAL_RATE / (1 - Math.pow(1 + Substations.REAL_RATE, -Substations.LIFE_YEARS));
        double payroll = t == null ? 0 : BuildCard.runningCost(g, t) - t.getUpkeep();
        double capital = value * annuity / 12, repairs = t == null ? 0 : value * t.repairsPerYear() / 12;
        double wires = (capital + repairs + payroll) / (book.servedKwh * (1 - PowerDispatch.GRID_LOSSES));
        check("the bill's wires recover this substation's capital, repairs and filled posts over twelve months' delivered kWh",
                value > 0 && near(number(costs, "capital"), capital) && near(number(costs, "repairs"), repairs)
                && near(number(costs, "payroll"), payroll)
                && near(book.commercePrice, (book.energyPrice + wires) * PowerDispatch.MONTH_HOURS)
                && near(book.industryPrice, (book.energyPrice + wires * Substations.TRANSMISSION_SHARE) * PowerDispatch.MONTH_HOURS));
        double before = book.commercePrice;
        if (t != null) t.setCashCost(2 * t.getCashCost());
        dispatch(g, CARRY * .9);
        check("changing the substation's cost changes the next bill; the previously struck book keeps its price",
                u.getStruckBook().commercePrice > before && book.commercePrice == before);
    }

    static void order() {
        OUT.println("--- the same order machinery as the roads ---");
        Game g = city("order", 1);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildAdvice.Measure grid;
        try { grid = BuildAdvice.Measure.of(BuildAdvice.Kind.valueOf("GRID")); }
        catch (IllegalArgumentException absent) { check("peak growth orders the missing substation, counts its site once, and does not order below the edge", false); return; }
        AutoBuilder a = g.getAutoBuilder(); a.setSlack(0, null); a.setOn(true, null);
        double season = PowerDispatch.season(CityCalendar.monthOfYear(g.getMonth()));
        u.setWattsConsumption(.99 * CARRY / (PowerDispatch.PLAN_PEAK * season));
        quietly(() -> a.pass(g));
        long before = g.getBuildingManager().getUnderConstructionById()[Substations.ID];
        u.setWattsConsumption(1.01 * CARRY / (PowerDispatch.PLAN_PEAK * season));
        quietly(() -> a.pass(g));
        long after = g.getBuildingManager().getUnderConstructionById()[Substations.ID];
        boolean placed = a.steps().stream().anyMatch(s -> s.measure().equals(grid) && s.ordered() > 0 && "Substation".equals(s.building()));
        quietly(() -> a.pass(g));
        long repeated = g.getBuildingManager().getUnderConstructionById()[Substations.ID];
        OUT.printf("   sites below/above/repeated: %d/%d/%d%n", before, after, repeated);
        check("peak growth orders the missing substation, counts its site once, and does not order below the edge",
                before == 0 && after == 1 && repeated == after && placed);
    }

    static void saved() throws Exception {
        OUT.println("--- closing costs survive the load rebuild ---");
        Game g = city("save", 1);
        quietly(() -> g.simulateMonths(2));
        String pending = JSON.toJson(g.getServicesManager().getUtilitiesHandler().toPowerState());
        boolean written = g.saveGame(10, "PW5 closing grid").ok;
        Game back = new Game(g.getGameFiles()); quietly(() -> back.loadGameSave(10));
        String restored = JSON.toJson(back.getServicesManager().getUtilitiesHandler().toPowerState());
        check("DataSave retains both the struck cost book and pending costs through the derived-services rebuild", written && pending.equals(restored));
        quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
        UtilitiesHandler x = g.getServicesManager().getUtilitiesHandler(), y = back.getServicesManager().getUtilitiesHandler();
        check("the first post-load strike bills exactly the same grid costs as the uninterrupted city", JSON.toJson(x.getStruckBook()).equals(JSON.toJson(y.getStruckBook())));
        check("pre-grid-cost saves are refused under the old-saves rule", GameVersion.isNoLongerReadable(GameVersion.SAVE_FORMAT - 1));
    }

    public static void main(String[] args) throws Exception {
        carry(); tariff(); order();
        if (args.length == 0) saved();
        OUT.println("SubstationCheck: " + fails + " failures");
        if (fails > 0) System.exit(1);
    }
}

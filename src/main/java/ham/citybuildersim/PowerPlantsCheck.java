package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The power plants and their fuels (PW2, 2026-11-23; spec-electricity 2.3 and 2.5): the Oil-Fired Plant, the Diesel Station, the
 * Oil-Fired Turbine, the Biomass Plant and the Solar Farm, and the Coal Power Plant and the Wind Farm refigured, each burning its fuel
 * from the market by the MWh, each offered from its year, the steam plants standing only on owned water, and each standing no sooner
 * than its months. PowerDispatchCheck proves the merit order on bare rows and PowerPriceCheck the coal's money; this proves the rows.
 *
 * WHAT THIS HAS TO PROVE:
 *   1. THE ROWS ARE THE STUDY'S: each row's price is its all-in cost a kW times its nameplate by HY1's rule (cash, points, materials),
 *      its output its nameplate at its availability over the game's month, its fuel a MWh the study's from its heat rate and its fuel's
 *      heat content, its ground, posts, cooling, months and year the table's; every ELECTRICITY row is dated.
 *   2. THE SUN RUNS AT ITS HOURS: a solar row makes its average x solarAt() in each half-block, nothing in the dark halves of the base
 *      and the shoulder, after the wind and before the rest; a solar farm standing in a town is dispatched so and booked as the sun.
 *   3. EACH FUEL IS BOUGHT FROM A REAL SELLER AND PAID ONCE: the fuelled rows run by their marginal cost, both sides (on the same diesel
 *      the turbine, the lower heat rate, runs first, and the station only past it); a town on each new fuelled row burns that fuel by the dispatch's kWh x its
 *      fuel a kWh; the market draws it for the city, what a seller at home holds from it (Refining's fuel oil and diesel, Wood Products'
 *      pellets) and the rest from the world; the seller's statement books the sale, the audit's FuelImports line and the Trade tab's
 *      good the import, once; the audit closes every month.
 *   4. THE COOLING GATE, both sides: a city that owns no sea, lake or river is refused a steam plant (NO_WATER: the order, the card, a
 *      run, the advice and the automatic builder) and not a closed-loop or dry row; a city that owns water is not refused one.
 *   5. THE BUILD-TIME FLOOR, both sides: a site of a row with months is worked no faster than its points over its months, so one
 *      stands in exactly its months with crews to spare and a quote waits them; a building with none is worked as fast as the crews go.
 *      A site's preparation is not held to it: a station whose ground owes ten times its points still stands in its months.
 *      An order's quote counts a site the floor holds at its cap, not its weight's share; one the floor does not hold, to the bit.
 *   6. EACH ROW FROM ITS YEAR (Q2), both sides: a row is known from its since and not the month before, in the build menu, the
 *      advice's list and the order; a 1900 city knows the coal and oil plants and not the diesel station, and it knows the diesel
 *      station from 1910.
 *   7. THE SUN IN A SAVE (the old-saves rule): a book's solar energy round-trips, and a book saved before PW2 reads none.
 *
 * Every fixture CAUSES its condition: the homes' draw is raised past the founding grid, a plant of a year not yet come stands by
 * fiat, a seller is stocked with half the month's burn, a dry city is founded where its rings hold no water, and a site's crews are
 * handed more than it can use. Every city is a scratch game (GameFiles.scratch); none is saved.
 */
public class PowerPlantsCheck {

    static int fails = 0, checks = 0, sectionStart = 0;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    /** HY1's cash per thousand dollars of all-in cost: the Wind Farm's old cash, 23,240, for its $31.8M (docs/queue/HY1-notes.md). */
    static final double CASH_PER_THOUSAND = 23240.0 / 31800;

    /** HY1's construction points to cash: the coal plant's old 120,000 points over its old 1,071,816 of cash. */
    static final double POINTS_PER_CASH = 120000.0 / 1071816;

    /** HY1's construction materials: both old plants' 1.85% of cash. */
    static final double MATERIALS_SHARE = 0.0185;

    /** A row's production1 is its average kW in the game's 720-hour month: a real month's 730 hours over 720 (HY1's arithmetic). */
    static final double HOURS = 730.0 / 720;

    /** Square feet in an acre. */
    static final double SQ_FT_PER_ACRE = 43_560;

    /** How many homes a town stands on, and each home's draw, kW: PowerPriceCheck's lever, past the founding grid's 10,000 kW. */
    static final int HOMES = 400;
    static final double HOME_KW = 50;

    /** Construction Depots a city short of power is given, so the automatic builder's plant is not held for builders. */
    static final int DEPOTS = 16;

    /** The months a town is played before a section reads it: the plant is staffed and has burned. */
    static final int WARM = 8;

    /** Site output handed to a site that can use less: past any row's points over its months. */
    static final int AMPLE = 100_000_000;

    /** One row of spec-electricity 2.3, as the study prints it. */
    record Study(String name, double usdPerKw, double nameplateKw, double runs, int months, boolean cooling, Good fuel,
                 double heatRate, double fuelPerMwh, double acres, int posts, int since, String variable) { }

    /** The modern study's table (spec-electricity 2.3) plus PW9's sourced historical station: $/kW, nameplate, availability or capacity factor, build months, cooling, fuel, Btu a kWh, fuel a MWh, acres, posts, year. */
    static final List<Study> STUDY = List.of(
            new Study("Coal Power Plant", 3271, 325_000, 0.85, 60, true, Good.THERMAL_COAL, 8490, 0.357, 200, 68, 1900, ""),
            new Study("Oil-Fired Plant", 3054, 100_000, 0.85, 48, true, Good.FUEL_OIL, 11227, 283, 30, 20, 1900, ""),
            new Study("Diesel Station", 1810, 21_000, 0.90, 30, false, Good.DIESEL, 10847, 298, 10, 3, 1910, ""),
            new Study("Oil-Fired Turbine", 2275, 100_000, 0.90, 24, false, Good.DIESEL, 9447, 260, 10, 6, 1949, ""),
            new Study("Biomass Plant", 4843, 50_000, 0.85, 60, true, Good.WOOD_PELLETS, 13300, 0.750, 100, 25, 1970, ""),
            new Study("Wind Farm", 1712, 20_000, 0.338, 36, false, null, 0, 0, 40, 2, 1980, "wind"),
            new Study("Solar Farm", 1484, 50_000, 0.24, 24, false, null, 0, 0, 333, 4, 1982, "solar"),
            // PW9: Engineering News's 1900 $100/kW, at the catalogue's 2025 dollar level; EPA's historical heat rate.
            new Study("Small Steam Station", 100 / PriceLevel.HISTORICAL_FACTOR, 850, .85, 15, true, Good.THERMAL_COAL,
                    75000, 3.15, 1, 9, 1900, ""));

    /** The new fuelled rows, the good each burns and the sector at home that makes it. */
    static final String[][] FUELLED = {
            {"Oil-Fired Plant", "FUEL_OIL", Sectors.REFINING},
            {"Diesel Station", "DIESEL", Sectors.REFINING},
            {"Oil-Fired Turbine", "DIESEL", Sectors.REFINING},
            {"Biomass Plant", "WOOD_PELLETS", Sectors.WOOD_PRODUCTS},
            {"Small Steam Station", "THERMAL_COAL", Sectors.COAL_MINING}};

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        checks++;
        if (!ok) fails++;
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    static boolean near(double a, double b, double tol) {
        return a == b || Math.abs(a - b) <= tol * Math.max(Math.abs(a), Math.abs(b));
    }

    static String f(double v) { return String.format("%.9g", v); }

    static void section(String title) {
        System.out.println("\n--- " + title + " ---");
        sectionStart = checks;
    }

    static void sectionEnd() { System.out.println("  (" + (checks - sectionStart) + " checks)"); }

    static void quietly(Runnable work) {
        PrintStream out = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(out); }
    }

    static <T> T hushed(java.util.function.Supplier<T> work) {
        PrintStream out = System.out;
        System.setOut(quiet);
        try { return work.get(); } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        System.out.println("=== PowerPlantsCheck: the power plants and their fuels (PW2) ===");
        rows();
        sun();
        fuels();
        cooling();
        floor();
        years();
        save();
        System.out.println(fails == 0 ? "PowerPlantsCheck: all ok (" + checks + " checks)" : "PowerPlantsCheck: " + fails + " FAILED of " + checks);
        System.exit(fails);
    }

    /* =====================================================================
       THE FIXTURES
       ===================================================================== */

    /** Ground a fixture city is given past its founding's, sq ft: room for any plant and the town round it (PowerPriceCheck's 400,000,000). */
    static final long MORE_GROUND = 400_000_000L;

    /** A founded city, the Wealthy preset's cash and MORE_GROUND more ground; dry, the whole of it founded where it holds no sea, lake or river. Null when no dry site is found. */
    static Game city(String label, boolean dry) {
        Game g = new Game(GameFiles.scratch("powerplants-" + label));
        quietly(g::newGame);
        LandManager land = g.getLandManager();
        if (dry) {
            if (!dryLand(g, land.getOwnedSqFt() + MORE_GROUND)) return null;
        } else {
            land.setOwnedSqFt(land.getOwnedSqFt() + MORE_GROUND);
        }
        g.setCashForTest(Founding.WEALTHY_CASH);
        return g;
    }

    /** Founds a new city's ground at sqFt, round the nearest cell of its world where all of it is dry: CityLand.found() with no fresh patch (CoalMiningCheck.coalTown()'s install). A restatement draws more blocks round the site, so the whole is founded at once. */
    static boolean dryLand(Game g, double sqFt) {
        World w = World.of(g.getCityLand().seed());
        LandManager land = g.getLandManager();
        double km2 = LandManager.km2(sqFt);
        int cx = (int) (w.foundingX() / World.CELL), cy = (int) (w.foundingY() / World.CELL);
        for (int ring = 0; ring < World.CELLS; ring++) {
            for (int y = cy - ring; y <= cy + ring; y++) {
                for (int x = cx - ring; x <= cx + ring; x++) {
                    if (Math.max(Math.abs(x - cx), Math.abs(y - cy)) != ring) continue;
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    long px = (long) x * World.CELL + World.CELL / 2, py = (long) y * World.CELL + World.CELL / 2;
                    boolean[] ok = {false};
                    quietly(() -> {
                        land.install(CityLand.found(w, px, py, km2), new double[CityLand.KINDS], w.totals(), w.seaTheta());
                        ok[0] = land.getSeaKm2() == 0 && land.getFreshKm2() == 0 && land.getLandDrySqFt() > 0;
                        if (ok[0]) land.restoreOwnedSqFt(land.getLandDrySqFt());
                    });
                    if (ok[0]) return true;
                }
            }
        }
        return false;
    }

    /** A town on one plant: HOMES homes drawing HOME_KW each, shops, water and roads standing at once (PowerPriceCheck.town()'s list), the plant stood by fiat whatever its year, and the wires its peak needs. */
    static Game town(String label, String plant, int count) {
        Game g = city(label, false);
        StringBuilder failed = new StringBuilder();
        quietly(() -> {
            BuildingManager b = g.getBuildingManager();
            b.getTemplateByName("House").setElectricityConsumption((int) HOME_KW);
            String[][] orders = { { "House", "" + HOMES }, { "Small Grocery Store", "4" }, { "Water Treatment Plant", "1" }, { "Paved Road", "10" } };
            for (String[] o : orders) {
                Game.BuildResult r = g.buildStack(b.getTemplateByName(o[0]), Integer.parseInt(o[1]), true);
                if (r != Game.BuildResult.SUCCESS) failed.append(o[0]).append(' ').append(r).append("; ");
            }
            b.addStack(b.getTemplateByName(plant), count, true);
            int wires = (int) Math.ceil(Substations.peak(HOMES * HOME_KW, java.util.Arrays.stream(PowerDispatch.SEASON).max().orElseThrow())
                    / (Substations.SUBSTATION_MW * 1000));
            g.setCashForTest(g.getCash() + g.quoteBuild(b.getTemplateByName("Substation"), Math.max(1, wires)).total);
            Game.BuildResult grid = g.buildStack(b.getTemplateByName("Substation"), Math.max(1, wires), true);
            if (grid != Game.BuildResult.SUCCESS) failed.append("Substation ").append(grid);
        });
        check("fixture: the " + label + " town's orders all stand, and its " + count + " x " + plant + " by fiat", failed.length() == 0
                && g.getBuildingManager().getStack(g.getBuildingManager().getTemplateByName(plant)).getQuantity() == count, "refused: " + failed);
        return g;
    }

    static PowerDispatch.Row rowOf(UtilitiesHandler u, String name) {
        for (PowerDispatch.Row r : u.getLatestRows()) if (r.name.equals(name)) return r;
        return null;
    }

    static final List<MoneyAudit.Result> audits = new ArrayList<>();
    static final List<String> auditAt = new ArrayList<>();

    static void month(Game g, String label) {
        quietly(() -> g.simulateMonths(1));
        audits.add(g.getLastMoneyAudit());
        auditAt.add(label + " m" + g.getMonth());
    }

    /* =====================================================================
       1. THE ROWS ARE THE STUDY'S
       ===================================================================== */

    /** The catalogue section 1 read, for the sections that need only templates. */
    static BuildingManager catalogue;

    static void rows() {
        section("1. the rows are their source tables' (spec-electricity 2.3 and PW9, by HY1's rule)");
        Game g = city("rows", false);
        BuildingManager b = g.getBuildingManager();
        catalogue = b;
        for (Study s : STUDY) {
            BuildingsTemplate t = b.getTemplateByName(s.name());
            if (t == null) { check(s.name() + " is in the catalogue", false); continue; }
            double cash = s.nameplateKw() * s.usdPerKw() / 1000 * CASH_PER_THOUSAND;
            // The file holds today's level; a template's dollars are struck at the founding's, once (pricedAtFoundingLevel()), so undo the factor.
            double fileCash = t.getCashCost() / PriceLevel.FACTOR;
            check(s.name() + ": its cash is its nameplate x $" + (int) s.usdPerKw() + "/kW at the Wind Farm's old cash per dollar, rounded",
                    Math.abs(fileCash - cash) <= 0.5 + 1e-9 * cash, f(fileCash) + " against " + f(cash));
            check(s.name() + ": its points the old coal plant's points to cash, its materials 1.85% of cash, each rounded",
                    Math.abs(t.getConstructionPoints() - cash * POINTS_PER_CASH) <= 0.5 && Math.abs(t.getConstructionMaterials() - cash * MATERIALS_SHARE) <= 0.5,
                    t.getConstructionPoints() + " and " + t.getConstructionMaterials());
            double average = s.nameplateKw() * s.runs() * HOURS;
            check(s.name() + ": " + (int) (s.nameplateKw() / 1000) + " MW at " + s.runs() * 100 + "%: production1 is its average kW in the game's month, rounded",
                    t.getNameplate() == s.nameplateKw() && Math.abs(t.getProduction1() - average) <= 0.5, t.getProduction1() + " against " + f(average));
            check(s.name() + ": " + s.months() + " months to build, " + (s.cooling() ? "" : "no ") + "cooling water, since " + s.since()
                            + ", " + (int) s.acres() + " acres, " + s.posts() + " posts",
                    t.buildMonths() == s.months() && t.cooling() == s.cooling() && t.getSince() == s.since()
                            && Math.abs(t.getLandSqFt() - s.acres() * SQ_FT_PER_ACRE) < 1 && t.getTotalJobs() == s.posts(),
                    t.buildMonths() + " months, cooling " + t.cooling() + ", since " + t.getSince() + ", " + t.getLandSqFt() + " sq ft, " + t.getTotalJobs() + " posts");
            check(s.name() + ": " + (s.variable().isEmpty() ? "dispatched by its marginal cost" : "dispatched at its weather, " + s.variable()),
                    t.isWind() == s.variable().equals("wind") && t.isSolar() == s.variable().equals("solar"));
            if (s.fuel() == null) {
                check(s.name() + ": it burns nothing", t.getFuel() == null && t.fuelPerKwh() == 0);
                continue;
            }
            double perKwh = s.heatRate() * PowerDispatch.MJ_PER_BTU / PowerDispatch.heatContentMJ(s.fuel());
            check(s.name() + ": it burns " + s.fuel() + " at " + (int) s.heatRate() + " Btu a kWh over the fuel's heat content (PowerDispatch.heatContentMJ())",
                    t.getFuel() == s.fuel() && t.getHeatRate() == s.heatRate() && near(t.fuelPerKwh(), perKwh, 1e-12), f(t.fuelPerKwh()) + " against " + f(perKwh));
            check(s.name() + ": ...which is the study's " + s.fuelPerMwh() + " units of " + s.fuel() + " a MWh, to its rounding (half a percent)",
                    near(t.fuelPerKwh() * 1000, s.fuelPerMwh(), 0.005), f(t.fuelPerKwh() * 1000));
        }
        List<String> undated = new ArrayList<>();
        for (BuildingsTemplate t : b.getTemplates()) if (t.getCategory() == BuildingType.ELECTRICITY && t.getSinceText().isEmpty()) undated.add(t.getName());
        check("every ELECTRICITY row is dated (Q2: each is offered from its year)", undated.isEmpty(), "undated: " + undated);
        check("the fuels' heat contents are the study's: 41.81 MJ a litre of fuel oil, 38.46 of diesel, 18.70 GJ a tonne of pellets",
                near(PowerDispatch.FUEL_OIL_MJ_PER_LITRE, 41.81, 5e-4) && near(PowerDispatch.DIESEL_MJ_PER_LITRE, 38.46, 5e-4)
                        && near(PowerDispatch.PELLETS_MJ_PER_TONNE, 18_700, 5e-4)
                        && Double.isNaN(PowerDispatch.heatContentMJ(Good.CRUDE)) && Double.isNaN(PowerDispatch.heatContentMJ(null)),
                f(PowerDispatch.FUEL_OIL_MJ_PER_LITRE) + ", " + f(PowerDispatch.DIESEL_MJ_PER_LITRE) + ", " + f(PowerDispatch.PELLETS_MJ_PER_TONNE));
        sectionEnd();
    }

    /* =====================================================================
       2. THE SUN RUNS AT ITS HOURS
       ===================================================================== */

    static void sun() {
        section("2. the sun runs at its hours");
        double windKw = 5_000, solarKw = 20_000, draw = 15_000;
        PowerDispatch.Row wind = PowerDispatch.Row.wind("wind", windKw), solar = PowerDispatch.Row.solar("sun", solarKw);
        PowerDispatch.Row fuelled = PowerDispatch.Row.fuelled("plant", 1e6, 1, null, 0, 0);
        PowerDispatch.Month m = PowerDispatch.dispatch(List.of(fuelled, solar, wind), draw, 1, 10);
        boolean shaped = true, dark = true;
        double kwh = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            double windTook = Math.min(windKw * PowerDispatch.windAt(s), m.load[s]);
            double want = Math.min(solarKw * PowerDispatch.solarAt(s), Math.max(0, m.load[s] - windTook));
            shaped &= Math.abs(wind.gen[s] - windTook) <= 1e-9 * draw && Math.abs(solar.gen[s] - want) <= 1e-9 * draw;
            kwh += solar.gen[s] * PowerDispatch.halfHours(s);
        }
        for (int s : new int[] {1, 3}) dark &= PowerDispatch.solarAt(s) == 0 && solar.gen[s] == 0;
        check("a solar row makes its average x solarAt(s) in each half-block, after the wind takes its share and before the fuelled rows: what the load leaves",
                shaped, java.util.Arrays.toString(solar.gen));
        check("...nothing in the dark halves of the base and the shoulder (SOLAR_SWING 1)", dark);
        check("...and the month's solar kWh is its half-blocks' run x their hours", near(solar.kwh, kwh, 1e-12), f(solar.kwh) + " against " + f(kwh));
        check("the merit order runs the must-run rows, the wind, the sun and only then the rest: the wind's and the sun's indices come before the plant's",
                m.getMeritOrder().indexOf(2) < m.getMeritOrder().indexOf(1) && m.getMeritOrder().indexOf(1) < m.getMeritOrder().indexOf(0), m.getMeritOrder() + "");
        double hours = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) hours += PowerDispatch.solarAt(s) * PowerDispatch.halfHours(s);
        check("uncurtailed, a solar row delivers its average over the month: SOLAR_BLOCK weighted by BLOCK_HOURS is 1, to the study's rounding (1e-3)",
                near(hours, PowerDispatch.MONTH_HOURS, 1e-3), f(hours) + " hours against " + PowerDispatch.MONTH_HOURS);
        check("the advice counts no firm power from the sun (SOLAR_FIRM 0, its darkest half-block) and the wind's least half-block, as PW1 did",
                PowerDispatch.SOLAR_FIRM == 0 && PowerDispatch.SOLAR_FIRM < PowerDispatch.WIND_FIRM && PowerDispatch.WIND_FIRM > 0, f(PowerDispatch.WIND_FIRM));

        Game g = town("solar", "Solar Farm", 1);
        for (int i = 0; i < WARM; i++) month(g, "solar");
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        PowerDispatch.Row row = rowOf(u, "Solar Farm");
        UtilitiesHandler.Book book = u.getStruckBook();
        double recorded = 0;
        for (UtilitiesHandler.RowRecord r : book.dispatch.rows()) if (r.kind() == PowerDispatch.Kind.SOLAR) recorded += r.kwh();
        check("fixture: the town's solar farm is a dispatched row of its kind and ran", row != null && row.kind == PowerDispatch.Kind.SOLAR && row.kwh > 0,
                row == null ? "no row" : f(row.kwh) + " kWh");
        boolean darkInTown = row != null && row.gen[1] == 0 && row.gen[3] == 0 && row.gen[0] > 0 && row.gen[4] > 0;
        check("...dark in the base's and the shoulder's dark halves, running in their sunny halves", darkInTown, row == null ? "" : java.util.Arrays.toString(row.gen));
        check("...and the book struck from its dispatch books the sun as the sun: solarKwh is its solar row's kWh",
                recorded > 0 && near(book.solarKwh, recorded, 1e-12), f(book.solarKwh) + " against " + f(recorded));
        // ...and the month pending in a save (REV-PW2, 2026-11-23): the power books as DataSave writes them (its Gson, the
        // whole save one object), read back, carry the solar row of the completed dispatch the next strike prices, its kind
        // by name and its energy to the bit.
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().create();
        UtilitiesHandler.PowerState saved = u.toPowerState();
        String written = gson.toJson(saved);
        UtilitiesHandler.PowerState back = gson.fromJson(written, UtilitiesHandler.PowerState.class);
        double pendingSun = 0, backSun = 0;
        if (saved.pendingDispatch != null) for (UtilitiesHandler.RowRecord r : saved.pendingDispatch.rows()) if (r.kind() == PowerDispatch.Kind.SOLAR) pendingSun += r.kwh();
        if (back.pendingDispatch != null) for (UtilitiesHandler.RowRecord r : back.pendingDispatch.rows()) if (r.kind() == PowerDispatch.Kind.SOLAR) backSun += r.kwh();
        check("a save carries the pending month's solar row: the power books written and read back hold it, its kind by name and its kWh to the bit",
                pendingSun > 0 && backSun == pendingSun && written.contains("\"SOLAR\"") && gson.toJson(back).equals(written),
                f(backSun) + " against " + f(pendingSun));
        sectionEnd();
    }

    /* =====================================================================
       3. EACH FUEL IS BOUGHT FROM A REAL SELLER AND PAID ONCE
       ===================================================================== */

    static void fuels() {
        section("3. each fuel is bought from a real seller and paid once");
        // The merit order, both sides: on the same diesel the turbine's lower heat rate makes it the cheaper row, so the station runs only past it.
        BuildingsTemplate turbine = catalogue.getTemplateByName("Oil-Fired Turbine"), station = catalogue.getTemplateByName("Diesel Station");
        double litre = Good.DIESEL.worldImportPrice();
        double mt = litre * turbine.fuelPerKwh() + turbine.getVariableUpkeep() / 1e6, ms = litre * station.fuelPerKwh() + station.getVariableUpkeep() / 1e6;
        check("on the same diesel the Oil-Fired Turbine's marginal cost is below the Diesel Station's: its heat rate is the lower", litre > 0 && mt < ms,
                f(mt) + " against " + f(ms));
        for (double share : new double[] {0.5, 1.5}) {
            PowerDispatch.Row tr = PowerDispatch.Row.fuelled("turbine", turbine.getNameplate(), mt, Good.DIESEL, turbine.fuelPerKwh(), turbine.getVariableUpkeep() / 1e6);
            PowerDispatch.Row st = PowerDispatch.Row.fuelled("station", station.getNameplate(), ms, Good.DIESEL, station.fuelPerKwh(), station.getVariableUpkeep() / 1e6);
            PowerDispatch.dispatch(List.of(st, tr), share * turbine.getNameplate(), 1, PowerDispatch.VOLL_A_KWH);
            boolean behind = tr.kwh > 0;
            for (int h = 0; h < PowerDispatch.HALVES; h++) behind &= st.gen[h] == 0 || tr.gen[h] >= turbine.getNameplate() * (1 - 1e-12);
            if (share < 1) check("...a draw of half the turbine's nameplate is the turbine's alone: the station, listed first, runs nothing", behind && st.kwh == 0,
                    f(tr.kwh) + " and " + f(st.kwh) + " kWh");
            else check("...a draw past it runs the station, and only in half-blocks where the turbine is at its nameplate", behind && st.kwh > 0,
                    f(tr.kwh) + " and " + f(st.kwh) + " kWh");
        }
        for (String[] row : FUELLED) {
            String plant = row[0], seller = row[2];
            Good good = Good.valueOf(row[1]);
            Game g = town(plant, plant, 1);
            for (int i = 0; i < WARM; i++) month(g, plant);
            UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(plant);
            PowerDispatch.Row r = rowOf(u, plant);
            UtilitiesHandler.Book book = u.getStruckBook();
            double burn = book.fuel.getOrDefault(good.name(), 0.0);
            UtilitiesHandler.RowRecord rec = null;
            for (UtilitiesHandler.RowRecord x : book.dispatch.rows()) if (x.fuel() == good) rec = x;
            check(plant + ": fixture: it ran and the struck book burned " + good, rec != null && rec.kwh() > 0 && burn > 0, f(burn) + " units");
            check(plant + ": the book's " + good + " is its dispatch's kWh x the row's fuel a kWh, the template's", rec != null
                    && near(rec.fuelPerKwh(), t.fuelPerKwh(), 1e-12) && near(burn, rec.fuelUnits(), 1e-12) && near(rec.fuelUnits(), rec.kwh() * t.fuelPerKwh(), 1e-12),
                    rec == null ? "" : f(rec.fuelUnits()) + " against " + f(rec.kwh() * t.fuelPerKwh()));
            check(plant + ": its marginal cost is above nothing: its fuel at the market's price and its upkeep", r != null && r.marginal > 0, r == null ? "" : f(r.marginal));

            // Both ways in one month: the seller at home holds half the burn the next strike buys.
            Sector maker = g.getSectors().byKey(seller);
            maker.setStock(good, burn / 2);
            double held = maker.getStock(good);
            month(g, plant);
            double[] d = PowerPriceCheck.cityDraw(g, good);
            double local = u.getFuelLocal(), imported = u.getFuelImported();
            check(plant + ": the market drew the burn for the city, to 1e-9: from " + seller + " at home and the world for the rest",
                    near(d[0] + d[1], burn, 1e-9) && d[0] > 0 && d[0] <= held * (1 + 1e-12) && d[1] > 0, f(d[0]) + " at home, " + f(d[1]) + " abroad, of " + f(burn));
            double atHome = maker.statement().sold.containsKey(good) ? maker.statement().sold.get(good).atHome : 0;
            check(plant + ": what " + seller + " was paid is fuelLocal, and its statement booked it among its sales at home",
                    near(local, d[2], 1e-9) && atHome >= local * (1 - 1e-9), f(local) + " against " + f(d[2]) + ", booked " + f(atHome));
            double byGood = u.getFuelImportedByGood().getOrDefault(good.name(), 0.0);
            check(plant + ": what the world was paid is fuelImported, all of it under " + good, near(imported, d[3], 1e-9) && near(byGood, imported, 1e-12),
                    f(imported) + " against " + f(d[3]));
            MoneyAudit.Result a = g.getLastMoneyAudit();
            check(plant + ": the audit's FuelImports line is that import, once", near(a.figure("- utilities FuelImports"), imported, 1e-9),
                    f(a.figure("- utilities FuelImports")));
            Sectors.TradeByGood trade = g.getTradeByGood();
            Sectors.GoodTrade gt = trade.goods().get(good);
            double utility = gt == null ? 0 : gt.buyers().getOrDefault(Sectors.UTILITY, 0.0);
            double services = trade.services().getOrDefault(Sectors.UTILITY, 0.0);
            check(plant + ": the national accounts book it once: the Trade tab's " + good + " carries the utility's import, and its services only the upkeep",
                    near(utility, imported, 1e-9) && near(services, u.getUpkeepPaid(), 1e-9), f(utility) + " under the good, " + f(services) + " in services");
        }
        int open = 0;
        double worst = 0;
        for (int i = 0; i < audits.size(); i++) {
            MoneyAudit.Result r = audits.get(i);
            if (Math.abs(r.residual) > MoneyAudit.tolerance(r.moved())) { open++; System.out.println("  open: " + auditAt.get(i) + " " + r); }
            worst = Math.max(worst, Math.abs(r.residual));
        }
        check("the money audit closes in every month of every town (" + audits.size() + " months)", open == 0 && !audits.isEmpty(),
                open + " open; worst residual " + f(worst));
        sectionEnd();
    }

    /* =====================================================================
       4. THE COOLING GATE
       ===================================================================== */

    /**
     * A city short of power with its wires standing, ready for the automatic builder's pass (EssentialWorksCheck.scarceCash()'s levers, with
     * cash to spare): its homes draw past the founding grid, the substations its forecast needs stand (SubstationCheck.wiresFor()), and
     * builders enough for a plant (EssentialWorksCheck.serveGrid()'s depots, to DEPOTS). No ground is bought, so a dry city stays dry
     * (EssentialWorksCheck.serveStreets() buys a thousand km2, its water with it).
     */
    static void shortOfPower(Game g) {
        quietly(() -> {
            BuildingManager b = g.getBuildingManager();
            b.getTemplateByName("House").setElectricityConsumption((int) HOME_KW);
            g.buildStack(b.getTemplateByName("House"), HOMES, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
            UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
            u.setWattsConsumption(Math.max(u.getProduction(), HOMES * HOME_KW) * 4);
            BuildAdvice.Measure grid = BuildAdvice.Measure.of(BuildAdvice.Kind.GRID);
            BuildAdvice.Ahead at = BuildAdvice.opening(g, BuildAdvice.LIFE_MONTHS, g.getAutoBuilder().getSlack());
            SubstationCheck.wiresFor(g, BuildAdvice.supplyDemand(g, grid, Map.of(), at)[1]);
            b.addStack(b.getTemplateByName("Construction Depot"), DEPOTS, true);
            g.setCashForTest(1e14);
        });
    }

    static AutoBuilder.Step powerStep(Game g) {
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        quietly(() -> {
            g.getAutoBuilder().setOn(true, null);
            g.getAutoBuilder().pass(g);
        });
        return g.getAutoBuilder().steps().stream().filter(s -> s.measure().equals(power)).findFirst().orElse(null);
    }

    static void cooling() {
        section("4. the cooling gate, both sides");
        Game dry = city("dry", true), wet = city("wet", false);
        check("fixture: a city founded where its rings hold no sea, lake or river, and one that owns fresh water", dry != null
                && dry.getLandManager().getSeaKm2() == 0 && dry.getLandManager().getFreshKm2() == 0
                && wet.getLandManager().getSeaKm2() + wet.getLandManager().getFreshKm2() > 0);
        if (dry == null) { sectionEnd(); return; }
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        for (String name : new String[] {"Coal Power Plant", "Oil-Fired Plant"}) {
            BuildingsTemplate t = dry.getBuildingManager().getTemplateByName(name), w = wet.getBuildingManager().getTemplateByName(name);
            java.util.LinkedHashMap<BuildingsTemplate, Long> run = new java.util.LinkedHashMap<>();
            run.put(t, 1L);
            check("the dry city: an order for a " + name + " is refused NO_WATER, and the card and a run say so before the click",
                    hushed(() -> dry.buildStack(t, 1, false)) == Game.BuildResult.NO_WATER && BuildCard.verdict(dry, t, 1).kind() == BuildCard.VerdictKind.NO_WATER
                            && dry.buildRunAhead(run) == 0 && dry.buildRunStop(run) == Game.BuildResult.NO_WATER && dry.getBuildingManager().getStack(t) == null
                            || dry.getBuildingManager().getStack(t).getUnderConstruction() == 0);
            check("...the advice values it at nothing there", BuildAdvice.unit(dry, power, t) == 0, f(BuildAdvice.unit(dry, power, t)));
            check("the wet city: the same order is not refused for water, the card has no NO_WATER, and the advice values it",
                    hushed(() -> wet.buildStack(w, 1, false)) == Game.BuildResult.SUCCESS && BuildCard.verdict(wet, w, 1).kind() != BuildCard.VerdictKind.NO_WATER
                            && BuildAdvice.unit(wet, power, w) > 0, f(BuildAdvice.unit(wet, power, w)));
        }
        List<String> cooled = new ArrayList<>(), open = new ArrayList<>();
        for (BuildingsTemplate t : dry.getBuildingManager().getTemplates()) {
            if (t.getCategory() != BuildingType.ELECTRICITY) continue;
            (dry.hasCoolingFor(t) ? open : cooled).add(t.getName());
        }
        check("in the dry city exactly the steam plants want water: coal, oil, biomass and small steam; the rest stand without it",
                cooled.equals(List.of("Coal Power Plant", "Oil-Fired Plant", "Biomass Plant", "Small Steam Station")) && open.contains("Diesel Station")
                        && open.contains("Oil-Fired Turbine") && open.contains("Solar Farm") && open.contains("Wind Farm"), cooled + "");
        check("...and the card's verdict for a diesel station there is not NO_WATER", BuildCard.verdict(dry, dry.getBuildingManager().getTemplateByName("Diesel Station"), 1).kind()
                != BuildCard.VerdictKind.NO_WATER);

        // The automatic builder, both sides: a dry 1900 city short of power knows only steam plants and a river's station, so it orders none.
        Game dryShort = city("dry-short", true), wetShort = city("wet-short", false);
        shortOfPower(dryShort);
        shortOfPower(wetShort);
        AutoBuilder.Step d = powerStep(dryShort), w = powerStep(wetShort);
        BuildingsTemplate dt = d == null || d.building() == null ? null : dryShort.getBuildingManager().getTemplateByName(d.building());
        BuildingsTemplate wt = w == null || w.building() == null ? null : wetShort.getBuildingManager().getTemplateByName(w.building());
        check("fixture: both cities are short of power and the automatic builder weighs it, and the dry one owns no water before or after",
                d != null && w != null && !dryShort.hasCoolingFor(dryShort.getBuildingManager().getTemplateByName("Oil-Fired Plant"))
                        && wetShort.hasCoolingFor(wetShort.getBuildingManager().getTemplateByName("Oil-Fired Plant")));
        check("the automatic builder in the dry city orders no plant that wants water", d != null && (d.ordered() == 0 || dt != null && !dt.cooling()), d + "");
        check("...in the wet city it orders a steam plant, one the city knows", w != null && w.ordered() > 0 && wt != null && wt.cooling()
                && wetShort.knows(wt), w + "");
        sectionEnd();
    }

    /* =====================================================================
       5. THE BUILD-TIME FLOOR
       ===================================================================== */

    static void floor() {
        section("5. the build-time floor, both sides");
        Game g = city("floor", false);
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate diesel = b.getTemplateByName("Diesel Station"), water = b.getTemplateByName("Water Treatment Plant");
        check("fixture: the diesel station has months to build (" + diesel.buildMonths() + ") and the water plant none", diesel.buildMonths() > 0 && water.buildMonths() == 0);
        int months = diesel.buildMonths();
        check("an order's wait with crews to spare is its months exactly (the floor), the water plant's under a month (the crews' own)",
                near(b.waitFor(diesel, 1, AMPLE), months, 1e-12) && b.waitFor(water, 1, AMPLE) < 1,
                f(b.waitFor(diesel, 1, AMPLE)) + " and " + f(b.waitFor(water, 1, AMPLE)));
        check("...and the build quote waits at least its months", g.quoteBuild(diesel, 1).months >= months - 1e-9, f(g.quoteBuild(diesel, 1).months));
        quietly(() -> {
            b.setConstructionMaterials(1_000_000_000_000L);
            b.addStack(diesel, 1, false);
            b.addStack(water, 1, false);
        });
        BuildingsStacks site = b.getStack(diesel), plant = b.getStack(water);
        double[] share = b.siteShares(AMPLE);
        int k = b.getStacksView().indexOf(site), j = b.getStacksView().indexOf(plant);
        check("with crews to spare a site's share is its points over its months, the cap; the water plant's is all it owes",
                near(share[k], diesel.getConstructionPoints() / (double) months, 1e-12) && near(share[j], water.getConstructionPoints(), 1e-12),
                f(share[k]) + " and " + f(share[j]));
        double few = diesel.getConstructionPoints() / (double) months / 4;
        double[] thin = b.siteShares(few);
        check("...with crews short of the cap the floor does not bind: the share is the crews' rule's, under the cap", thin[k] < diesel.getConstructionPoints() / (double) months
                && thin[k] > 0, f(thin[k]));
        quietly(() -> b.advanceConstruction(AMPLE, 0));
        check("one month with crews to spare stands the water plant and not the diesel station", plant.getQuantity() == 1 && site.getQuantity() == 0 && site.getUnderConstruction() == 1);
        for (int m = 1; m < months - 1; m++) quietly(() -> b.advanceConstruction(AMPLE, 0));
        double left = b.waitOnSite(diesel, AMPLE);
        check("after " + (months - 1) + " months the station is still on site, and its wait is the one month left", site.getQuantity() == 0 && site.getUnderConstruction() == 1
                && near(left, 1, 1e-9), f(left));
        quietly(() -> b.advanceConstruction(AMPLE, 0));
        check("...and in its " + months + "th month it stands: no sooner than its months, and no later with crews to spare", site.getQuantity() == 1 && site.getUnderConstruction() == 0);
        // The preparation (a foothill pad, the works: owed as progress below nothing) is the crews', not the floor's.
        double prep = 10.0 * diesel.getConstructionPoints();
        check("an order whose ground owes ten times its points still waits its months exactly with crews to spare",
                near(b.waitFor(diesel, 1, prep, AMPLE), months, 1e-12), f(b.waitFor(diesel, 1, prep, AMPLE)));
        quietly(() -> {
            b.addStack(diesel, 1, false);
            b.addPreparation(diesel, prep);
        });
        double[] owing = b.siteShares(AMPLE);
        check("fixture: the second station's site owes its preparation, as progress below nothing", site.getUnderConstruction() == 1
                && near(site.getConstructionProgress(), -prep, 1e-9), f(site.getConstructionProgress()));
        check("...its first month's share is all the preparation and its points over its months: the preparation is not capped",
                near(owing[k], prep + diesel.getConstructionPoints() / (double) months, 1e-9), f(owing[k]));
        for (int m = 0; m < months - 1; m++) quietly(() -> b.advanceConstruction(AMPLE, 0));
        check("...after " + (months - 1) + " months it is still on site", site.getQuantity() == 1 && site.getUnderConstruction() == 1);
        quietly(() -> b.advanceConstruction(AMPLE, 0));
        check("...and in its " + months + "th it stands, not " + months + " x 11 months on", site.getQuantity() == 2 && site.getUnderConstruction() == 0);
        // The quotes (BuildingManager.ruleWait()) share the output by weight, held steady; a site the floor holds is held for its
        // whole build, so what its cap leaves goes to the order. A third station on site alone, and a water plant quoted beside it.
        quietly(() -> b.addStack(diesel, 1, false));
        boolean alone = true;
        for (BuildingsStacks on : b.getStacksUnderConstruction()) alone &= on == site;
        double owes = site.pointsOnSite() - site.getConstructionProgress();
        double held = Math.min(owes, diesel.getConstructionPoints() / (double) months);
        double wd = BuildingManager.weightOf(diesel, 1), ww = BuildingManager.weightOf(water, 1), wp = water.getConstructionPoints();
        double edge = held * (wd + ww) / wd;
        check("fixture: a third station is on site alone, owing more than its cap", alone && site.getUnderConstruction() == 1 && owes > held && held > 0,
                f(owes) + " against " + f(held));
        double past = 2 * edge, short_ = edge / 2;
        check("past the output at which its weight's share is its cap (" + f(edge) + "), the water plant waits its points over the output the cap leaves",
                near(b.waitFor(water, 1, past), wp / (past - held), 1e-9) && !near(b.waitFor(water, 1, past), wp * (wd + ww) / (past * ww), 1e-6),
                f(b.waitFor(water, 1, past)) + " against " + f(wp / (past - held)));
        check("...and short of it the weight's own share, to the bit", b.waitFor(water, 1, short_) == wp * (wd + ww) / (short_ * ww),
                f(b.waitFor(water, 1, short_)) + " against " + f(wp * (wd + ww) / (short_ * ww)));
        sectionEnd();
    }

    /* =====================================================================
       6. EACH ROW FROM ITS YEAR
       ===================================================================== */

    static void years() {
        section("6. each row from its year (Q2), both sides");
        Game g = city("years", false);
        BuildingManager b = g.getBuildingManager();
        boolean edges = true;
        StringBuilder at = new StringBuilder();
        for (BuildingsTemplate t : b.getTemplates()) {
            if (t.getCategory() != BuildingType.ELECTRICITY) continue;
            int from = ResearchCatalog.knownFrom(t.getSinceText());
            edges &= !t.knownAt(from - 1) && t.knownAt(from);
            at.append(at.length() == 0 ? "" : ", ").append(t.getName()).append(' ').append(from);
        }
        check("every ELECTRICITY row is known from the month its since opens and not the month before", edges, at.toString());
        List<String> known = new ArrayList<>(), menu = new ArrayList<>();
        for (BuildingsTemplate t : b.getKnownTemplates()) if (t.getCategory() == BuildingType.ELECTRICITY) known.add(t.getName());
        for (BuildingsTemplate t : b.getTemplatesByCategory(EnumSet.of(BuildingType.ELECTRICITY))) menu.add(t.getName());
        check("a 1900 city (month " + g.getMonth() + ") knows coal, oil, hydro, the substation, PW9's small steam station and ASH1's distribution substation, and no later row",
                known.equals(List.of("Coal Power Plant", "Hydro Station", "Substation", "Oil-Fired Plant", "Small Steam Station", "Distribution Substation")), known + "");
        check("...its build menu's power is the same list, and an order for a diesel station is refused NOT_KNOWN", menu.equals(known)
                && hushed(() -> g.buildStack(b.getTemplateByName("Diesel Station"), 1, true)) == Game.BuildResult.NOT_KNOWN, menu + "");
        int edge = ResearchCatalog.knownFrom("1910");
        Game later = city("years-1910", false);
        quietly(() -> later.simulateMonths(edge - 1 - later.getMonth()));
        BuildingsTemplate diesel = later.getBuildingManager().getTemplateByName("Diesel Station");
        boolean before = !later.knows(diesel) && !later.getBuildingManager().getKnownTemplates().contains(diesel)
                && hushed(() -> later.buildStack(diesel, 1, true)) == Game.BuildResult.NOT_KNOWN;
        check("fixture: the city played to the month before 1910 (month " + later.getMonth() + "): the diesel station is not yet known nor ordered", before
                && later.getMonth() == edge - 1);
        quietly(() -> later.simulateMonths(1));
        later.setCashForTest(Founding.WEALTHY_CASH);
        boolean after = later.knows(diesel) && later.getBuildingManager().getKnownTemplates().contains(diesel)
                && later.getBuildingManager().getTemplatesByCategory(EnumSet.of(BuildingType.ELECTRICITY)).contains(diesel);
        check("...in January 1910 it is known and in the build menu", after && later.getMonth() == edge);
        check("...and its order passes the year: it is not refused NOT_KNOWN", hushed(() -> later.buildStack(diesel, 1, true)) == Game.BuildResult.SUCCESS);
        BuildingsTemplate steam = later.getBuildingManager().getTemplateByName("Small Steam Station");
        int closes = PolicyDates.monthOf(1915, 1); // ASME's transition date, independent of a missing/wrong catalogue boundary.
        check("PW9's last new-order year is inclusive: December 1914 offered, January 1915 closed, including research-aware reads",
                steam.getUntil() == 1914 && steam.knownAt(closes - 1) && !steam.knownAt(closes)
                        && steam.knownAt(closes - 1, later.getResearch()) && !steam.knownAt(closes, later.getResearch())
                        && diesel.getUntil() == 0 && diesel.knownAt(closes));
        quietly(() -> later.simulateMonths(closes - 1 - later.getMonth()));
        later.setCashForTest(Founding.WEALTHY_CASH);
        check("fixture: December 1914 offers small steam now, and its one-month-ahead menu already closes",
                later.getMonth() == closes - 1 && later.getBuildingManager().isTemplateKnown(steam)
                        && !later.getBuildingManager().isTemplateKnownIn(steam, 1));
        Game.BuildResult standing = hushed(() -> later.buildStack(steam, 1, true));
        Game.BuildResult pending = hushed(() -> later.buildStack(steam, 1, false));
        check("the final offered month accepts both a standing fixture order and an ordinary pending order",
                standing == Game.BuildResult.SUCCESS && pending == Game.BuildResult.SUCCESS);
        quietly(() -> later.simulateMonths(1));
        check("January 1915 removes the row from new-order menus and refuses a funded direct order",
                later.getMonth() == closes && !later.getBuildingManager().getKnownTemplates().contains(steam)
                        && !later.getBuildingManager().getTemplatesByCategory(EnumSet.of(BuildingType.ELECTRICITY)).contains(steam)
                        && hushed(() -> later.buildStack(steam, 1, false)) == Game.BuildResult.NOT_KNOWN);
        check("ending new orders preserves the catalogue id, the standing station, its dispatch row and the previously ordered site",
                later.getBuildingManager().getTemplates().contains(steam)
                        && later.getBuildingManager().getQuantity(steam.getId()) == 1
                        && later.getBuildingManager().getStack(steam).getUnderConstruction() == 1
                        && later.getAutoBuilder().pendingCapacity(later, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)).getOrDefault(steam, 0L) == 1
                        && rowOf(later.getServicesManager().getUtilitiesHandler(), steam.getName()) != null);
        sectionEnd();
    }

    /* =====================================================================
       7. THE SUN IN A SAVE
       ===================================================================== */

    static void save() {
        section("7. the sun in a save (the old-saves rule)");
        UtilitiesHandler.Book book = new UtilitiesHandler.Book();
        book.solarKwh = 1_234_567.0;
        book.windKwh = 7.0;
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.JsonObject json = gson.toJsonTree(book).getAsJsonObject();
        UtilitiesHandler.Book back = gson.fromJson(json, UtilitiesHandler.Book.class);
        check("a book's solar energy is written and read back to the bit", json.has("solarKwh") && back.solarKwh == book.solarKwh && back.windKwh == book.windKwh);
        json.remove("solarKwh");
        UtilitiesHandler.Book old = gson.fromJson(json, UtilitiesHandler.Book.class);
        check("a book saved before PW2 has no solarKwh and reads none: an old city had no sun, so no format bump is owed", old.solarKwh == 0 && old.windKwh == 7.0);
        check("a book's copy keeps its sun (the month's book and the struck one)", book.copy().solarKwh == book.solarKwh);
        check("the row kinds keep their order, SOLAR appended: a saved kind is read by its name",
                PowerDispatch.Kind.valueOf("SOLAR").ordinal() == PowerDispatch.Kind.values().length - 1 && PowerDispatch.Kind.FUELLED.ordinal() == 3);
        sectionEnd();
    }
}

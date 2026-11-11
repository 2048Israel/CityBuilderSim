package ham.citybuildersim;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The month's merit order (PW1, 2026-11-11; PowerDispatch, UtilitiesHandler's struck book): the load cut in six half-blocks, the
 * works first, the wind at its weather, the hydro levelling the load, the fuelled rows by marginal cost, the price each half-block
 * clears at, and the tariff struck from it. Pure: no Game, no I/O but the shipped rows of buildings.json.
 *
 * WHY. The utility used to set the month's power by one sum, the coal plant burned nothing, the wind never stopped and the tariff
 * was a constant. Every expected figure here is computed from the model's own constants (BLOCK_HOURS, BLOCK_LOAD, WIND_BLOCK,
 * WIND_SWING, GRID_LOSSES, halfHours()), never typed in, and every fixture builds the rows that CAUSE its condition.
 *
 * What this has to prove:
 *   1. THE BLOCKS SUM TO THE MONTH: the shares of hours sum to 1, the load over the hours to 1, the half-blocks' hours to the
 *      month's, the twelve seasons average about 1.
 *   2. GENERATED + UNSERVED = LOAD in every half-block, on a short fuelled row, wind with a fuelled row, hydro with a fuelled row;
 *      and the energy served and unserved is the energy asked.
 *   3. A SHORT HALF-BLOCK CLEARS AT VOLL, the ratio is the energy served over the energy asked, to the bit.
 *   4. WIND PAST A BLOCK'S LOAD is curtailed and counted; no half-block is served twice.
 *   5. THE CALM HALF: wind alone at the month's mean draw leaves the calm halves short and the windy ones not.
 *   6. HYDRO LEVELS THE LOAD: its water used is its water, its peak output is at least its base's, it is held to its cap, and
 *      it never runs more than the residual.
 *   7. THE MERIT ORDER BY MARGINAL COST: the cheaper runs first; when the fuel price moves the order and the prices swap.
 *   8. FUEL = kWh x FUEL A kWh, to the bit, from the Coal Power Plant's own row of buildings.json (and the shipped rows reach
 *      the template through the catalog).
 *   9. THE WORKS' SURPLUS IS TAKEN FIRST, and a half-block it alone covers clears at 0.
 *  10. THE STRUCK BOOK (UtilitiesHandler, no Game): the prices, the works' price, the founding tariff before a strike, the save's
 *      round trip, and the income falling by the fuel bill.
 */
public class PowerDispatchCheck {

    static int fails = 0;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    static boolean near(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static final double DRAW = 100_000;

    /** The load asked of half-block s at this draw and season, from the model's constants. */
    static double loadAt(double draw, double season, int s) {
        return draw * season / (1 - PowerDispatch.GRID_LOSSES) * PowerDispatch.BLOCK_LOAD[s / 2];
    }

    static double generated(List<PowerDispatch.Row> rows, int s) {
        double g = 0;
        for (PowerDispatch.Row r : rows) g += r.gen[s];
        return g;
    }

    static double marginal() { return PowerDispatch.VOLL_A_KWH / 100; }

    static PowerDispatch.Row fuelled(String name, double kw, double marginal) {
        return PowerDispatch.Row.fuelled(name, kw, marginal, null, 0, 0);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== PowerDispatchCheck: the month's merit order and the struck book (PW1) ===");
        blocks();
        conservation();
        shortAtVoll();
        windCurtailed();
        calmHalf();
        hydroLevels();
        meritOrder();
        fuelPerKwh();
        worksFirst();
        struckBook();
        System.out.println(fails == 0 ? "PowerDispatchCheck: all ok" : "PowerDispatchCheck: " + fails + " FAILED");
        System.exit(fails);
    }

    /* ---------- 1 ---------- */

    static void blocks() {
        System.out.println("\n--- 1. the blocks sum to the month ---");
        double share = 0, load = 0, hours = 0, seasons = 0, windMean = 0;
        for (int b = 0; b < PowerDispatch.BLOCKS; b++) {
            share += PowerDispatch.BLOCK_HOURS[b];
            load += PowerDispatch.BLOCK_HOURS[b] * PowerDispatch.BLOCK_LOAD[b];
        }
        for (int s = 0; s < PowerDispatch.HALVES; s++) hours += PowerDispatch.halfHours(s);
        for (double v : PowerDispatch.SEASON) seasons += v;
        check("the blocks' shares of the month's hours sum to 1", near(share, 1, 1e-9), "" + share);
        check("the blocks' load over their hours sums to 1: the month's energy is its mean's", near(load, 1, 1e-9), "" + load);
        check("the six half-blocks' hours sum to the month's " + PowerDispatch.MONTH_HOURS, near(hours, PowerDispatch.MONTH_HOURS, 1e-9), "" + hours);
        check("the twelve seasons average within 0.01 of 1", PowerDispatch.SEASON.length == 12 && Math.abs(seasons / 12 - 1) < 0.01, "" + seasons / 12);
        check("season() reads January first and is 1 outside 1..12",
                PowerDispatch.season(1) == PowerDispatch.SEASON[0] && PowerDispatch.season(12) == PowerDispatch.SEASON[11]
                        && PowerDispatch.season(0) == 1 && PowerDispatch.season(13) == 1);
        boolean symmetric = true;
        double calmest = Double.POSITIVE_INFINITY;
        for (int b = 0; b < PowerDispatch.BLOCKS; b++) {
            symmetric &= near(PowerDispatch.windAt(2 * b) + PowerDispatch.windAt(2 * b + 1), 2 * PowerDispatch.WIND_BLOCK[b], 1e-12);
        }
        for (int s = 0; s < PowerDispatch.HALVES; s++) calmest = Math.min(calmest, PowerDispatch.windAt(s));
        for (int b = 0; b < PowerDispatch.BLOCKS; b++) windMean += PowerDispatch.BLOCK_HOURS[b] * PowerDispatch.WIND_BLOCK[b];
        check("a block's two wind halves average the block's share of the wind", symmetric);
        check("WIND_FIRM is the calmest half-block's wind", near(PowerDispatch.WIND_FIRM, calmest, 1e-12), PowerDispatch.WIND_FIRM + " against " + calmest);
        check("PLAN_PEAK is the peak block's load with the losses in",
                near(PowerDispatch.PLAN_PEAK, PowerDispatch.BLOCK_LOAD[PowerDispatch.PEAK] / (1 - PowerDispatch.GRID_LOSSES), 1e-12));
        check("the wind's blocks, over their hours, average about 1 (a farm's average is its mean)", Math.abs(windMean - 1) < 0.02, "" + windMean);
    }

    /* ---------- 2 ---------- */

    static void conserved(String what, List<PowerDispatch.Row> rows, PowerDispatch.Month m) {
        boolean ok = true;
        double unservedKwh = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            ok &= Math.abs(generated(rows, s) + m.unserved[s] - m.load[s]) < 1e-6;
            unservedKwh += m.unserved[s] * PowerDispatch.halfHours(s);
        }
        check(what + ": generated + unserved = load in every half-block", ok);
        check(what + ": served + unserved energy = asked", near(m.servedKwh + unservedKwh, m.askedKwh, 1e-9), m.servedKwh + " + " + unservedKwh + " against " + m.askedKwh);
    }

    static void conservation() {
        System.out.println("\n--- 2. generated + unserved = load ---");
        List<PowerDispatch.Row> a = List.of(fuelled("coal", DRAW, marginal()));
        PowerDispatch.Month ma = PowerDispatch.dispatch(a, DRAW, 1, PowerDispatch.VOLL_A_KWH);
        conserved("a fuelled row alone, short", a, ma);
        check("...and it is short somewhere (the fixture causes it)", ma.servedKwh < ma.askedKwh);

        List<PowerDispatch.Row> b = List.of(PowerDispatch.Row.wind("wind", DRAW / 2), fuelled("coal", DRAW * 0.8, marginal()));
        conserved("wind and a fuelled row", b, PowerDispatch.dispatch(b, DRAW, 1, PowerDispatch.VOLL_A_KWH));

        List<PowerDispatch.Row> c = List.of(PowerDispatch.Row.hydro("hydro", DRAW * 0.3, DRAW * 0.1 * PowerDispatch.MONTH_HOURS, 0), fuelled("coal", DRAW * 0.9, marginal()));
        conserved("hydro and a fuelled row", c, PowerDispatch.dispatch(c, DRAW, 1, PowerDispatch.VOLL_A_KWH));
    }

    /* ---------- 3 ---------- */

    static void shortAtVoll() {
        System.out.println("\n--- 3. a short half-block clears at VOLL ---");
        double offer = loadAt(DRAW, 1, 0) * 1.01, m1 = marginal(), voll = PowerDispatch.VOLL_A_KWH;
        List<PowerDispatch.Row> rows = List.of(fuelled("coal", offer, m1));
        PowerDispatch.Month m = PowerDispatch.dispatch(rows, DRAW, 1, voll);
        check("the base block's two halves are covered and priced at the row's marginal", m.unserved[0] == 0 && m.unserved[1] == 0 && m.price[0] == m1 && m.price[1] == m1);
        boolean shortAll = true;
        for (int s = 2; s < PowerDispatch.HALVES; s++) {
            shortAll &= m.price[s] == voll && near(m.unserved[s], loadAt(DRAW, 1, s) - offer, 1e-9);
        }
        check("the shoulder's and the peak's halves, past the offer, clear at VOLL with the shortfall unserved", shortAll);
        check("the ratio is the energy served over the energy asked, to the bit", m.ratio == m.servedKwh / m.askedKwh, m.ratio + " against " + m.servedKwh / m.askedKwh);
        check("...and below 1", m.ratio < 1 && m.ratio > 0);
        PowerDispatch.Month none = PowerDispatch.dispatch(List.of(), 0, 1, voll);
        check("nothing asked is a ratio of 1 and a price of 0", none.ratio == 1 && none.energyPrice == 0);
        PowerDispatch.Month bad = PowerDispatch.dispatch(rows, Double.NaN, Double.NaN, -1);
        check("a non-finite draw is nothing asked", bad.ratio == 1 && bad.askedKwh == 0);
    }

    /* ---------- 4 ---------- */

    static void windCurtailed() {
        System.out.println("\n--- 4. wind past a block's load ---");
        double average = loadAt(DRAW, 1, 0) / PowerDispatch.windAt(0) * 1.5;
        List<PowerDispatch.Row> rows = List.of(PowerDispatch.Row.wind("wind", average), fuelled("coal", DRAW * 2, marginal()));
        PowerDispatch.Month m = PowerDispatch.dispatch(rows, DRAW, 1, PowerDispatch.VOLL_A_KWH);
        double expected = 0;
        boolean never = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            expected += Math.max(0, average * PowerDispatch.windAt(s) - loadAt(DRAW, 1, s)) * PowerDispatch.halfHours(s);
            never &= generated(rows, s) <= m.load[s] + 1e-9;
        }
        check("fixture: the windy base half's wind passes its load", average * PowerDispatch.windAt(0) > loadAt(DRAW, 1, 0));
        check("the curtailed energy is the excess over the load, times the hours", expected > 0 && near(m.curtailedKwh, expected, 1e-9), m.curtailedKwh + " against " + expected);
        check("no half-block's generation passes its load: nothing is served twice", never);
    }

    /* ---------- 5 ---------- */

    static void calmHalf() {
        System.out.println("\n--- 5. the calm half ---");
        List<PowerDispatch.Row> rows = List.of(PowerDispatch.Row.wind("wind", DRAW / (1 - PowerDispatch.GRID_LOSSES)));
        PowerDispatch.Month m = PowerDispatch.dispatch(rows, DRAW, 1, PowerDispatch.VOLL_A_KWH);
        boolean windyOk = true, calmShort = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            if (s % 2 == 0) windyOk &= m.unserved[s] == 0 && m.price[s] == 0;
            else calmShort &= m.unserved[s] > 0 && m.price[s] == PowerDispatch.VOLL_A_KWH;
        }
        check("wind alone at the month's mean draw covers the windy halves, at a price of 0", windyOk);
        check("...and leaves the calm halves short, at VOLL", calmShort);
    }

    /* ---------- 6 ---------- */

    static void hydroLevels() {
        System.out.println("\n--- 6. hydro levels the load ---");
        double offer = DRAW, cap = DRAW * 0.6, voll = PowerDispatch.VOLL_A_KWH;
        double need = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) need += Math.max(0, loadAt(DRAW, 1, s) - offer) * PowerDispatch.halfHours(s);
        check("fixture: the fuelled row alone leaves energy unserved (" + (long) need + " kWh)", need > 0);
        for (double share : new double[] {0.5, 1.5}) {
            double water = need * share;
            List<PowerDispatch.Row> rows = List.of(PowerDispatch.Row.hydro("hydro", cap, water, 0), fuelled("coal", offer, marginal()));
            PowerDispatch.Month m = PowerDispatch.dispatch(rows, DRAW, 1, voll);
            PowerDispatch.Row h = rows.get(0), f = rows.get(1);
            boolean held = true, served = true;
            for (int s = 0; s < PowerDispatch.HALVES; s++) {
                held &= h.gen[s] <= cap + 1e-9;
                if (m.load[s] <= f.kw) served &= m.unserved[s] == 0;
            }
            check("water at " + share + " x the shortfall: the water used is the water, within 1e-3 kWh", Math.abs(h.kwh - water) < 1e-3, h.kwh + " against " + water);
            check("...the peak's hydro output is at least the base's", h.gen[4] >= h.gen[0] && h.gen[5] >= h.gen[1]);
            check("...each half-block's hydro output is held to its cap", held);
            check("...no half-block the fuelled row could have served alone is left short", served);
            if (share > 1) check("...with water to spare nothing is left short at all", m.servedKwh == m.askedKwh || near(m.servedKwh, m.askedKwh, 1e-12));
            else check("...with too little water the top is still short", m.servedKwh < m.askedKwh);
        }
        List<PowerDispatch.Row> flood = List.of(PowerDispatch.Row.hydro("hydro", DRAW * 100, 1e15, 0));
        PowerDispatch.Month m = PowerDispatch.dispatch(flood, DRAW, 1, voll);
        check("a station with more water than the residual's energy runs no more than the residual", near(flood.get(0).kwh, m.askedKwh, 1e-9) && flood.get(0).kwh <= 1e15,
                flood.get(0).kwh + " against " + m.askedKwh);
    }

    /* ---------- 7 ---------- */

    static void meritOrder() {
        System.out.println("\n--- 7. the merit order by marginal cost ---");
        double m1 = marginal(), m2 = 2 * m1, voll = PowerDispatch.VOLL_A_KWH;
        PowerDispatch.Row cheap = fuelled("cheap", DRAW * 0.6, m1), dear = fuelled("dear", DRAW * 0.8, m2);
        PowerDispatch.Month m = PowerDispatch.dispatch(List.of(dear, cheap), DRAW, 1, voll);
        boolean first = true, cleared = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            first &= cheap.gen[s] == Math.min(cheap.kw, m.load[s]) && near(dear.gen[s], m.load[s] - cheap.gen[s], 1e-9);
            cleared &= m.price[s] == m2;
        }
        check("the cheaper row runs first (listed second), the dearer takes the rest", first);
        check("...and every half-block clears at the dearer row's marginal, the last it ran", cleared);

        PowerDispatch.Row cheapDearer = fuelled("cheap", cheap.kw, m1 * 3);
        PowerDispatch.Month n = PowerDispatch.dispatch(List.of(cheapDearer, dear), DRAW, 1, voll);
        boolean swapped = true, repriced = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            swapped &= dear.gen[s] == Math.min(dear.kw, n.load[s]) && near(cheapDearer.gen[s], n.load[s] - dear.gen[s], 1e-9);
            repriced &= n.price[s] == m1 * 3;
        }
        check("the fuel price moves: the first row's marginal x 3 puts the other first", swapped);
        check("...and the clearing price becomes the row now last", repriced);

        PowerDispatch.Row a = fuelled("a", DRAW * 0.5, m1), b = fuelled("b", DRAW * 0.9, m1);
        PowerDispatch.dispatch(List.of(a, b), DRAW, 1, voll);
        check("rows of equal marginal keep their list order", a.gen[0] == a.kw);
    }

    /* ---------- 8 ---------- */

    /** A row of the shipped buildings.json, as the file says it (the catalog's own lookup order: the working directory, then the jar). */
    static JsonObject jsonRow(String name) throws Exception {
        java.nio.file.Path here = java.nio.file.Path.of("buildings.json");
        try (Reader r = java.nio.file.Files.exists(here) ? java.nio.file.Files.newBufferedReader(here, StandardCharsets.UTF_8)
                : new InputStreamReader(PowerDispatchCheck.class.getResourceAsStream("/buildings.json"), StandardCharsets.UTF_8)) {
            for (var e : JsonParser.parseReader(r).getAsJsonObject().getAsJsonArray("buildings")) {
                JsonObject o = e.getAsJsonObject();
                if (o.has("name") && name.equals(o.get("name").getAsString())) return o;
            }
        }
        throw new IllegalStateException("no row " + name);
    }

    static double num(JsonObject o, String key) { return o.has(key) ? o.get(key).getAsDouble() : 0; }

    static String text(JsonObject o, String key) { return o.has(key) ? o.get(key).getAsString() : ""; }

    /** A template built from a row's own keys, whatever the catalog reads of them. */
    static BuildingsTemplate fromRow(String name) throws Exception {
        JsonObject o = jsonRow(name);
        return new BuildingsTemplate(name, BuildingType.ELECTRICITY).setProduction1(num(o, "production1")).setNameplate(num(o, "nameplate"))
                .setFuel(text(o, "fuel")).setHeatRate(num(o, "heatRate")).setVariableUpkeep(num(o, "variableUpkeep")).setVariable(text(o, "variable"));
    }

    static BuildingsTemplate catalogRow(String name) {
        List<BuildingsTemplate> all = new BuildingCatalog().load();
        if (all != null) for (BuildingsTemplate t : all) if (name.equals(t.getName())) return t;
        return null;
    }

    static void fuelPerKwh() throws Exception {
        System.out.println("\n--- 8. fuel = kWh x fuel a kWh ---");
        BuildingsTemplate coal = fromRow("Coal Power Plant");
        double expected = 8490 * PowerDispatch.MJ_PER_BTU / PowerDispatch.COAL_MJ_PER_TONNE, perKwh = coal.fuelPerKwh();
        check("the row says coal, 8,490 Btu a kWh, a 325 MW nameplate and $9.37 a MWh of upkeep",
                coal.getFuel() == Good.THERMAL_COAL && coal.getHeatRate() == 8490 && coal.getNameplate() == 325_000 && coal.getVariableUpkeep() == 9.37);
        check("the Coal Power Plant's fuel a kWh is its heat rate over the coal's heat content", perKwh == expected, perKwh + " against " + expected);
        check("...0.357 tonnes a MWh", Math.abs(perKwh * 1000 - 0.357) < 0.001, "" + perKwh * 1000);
        check("a station with no fuel burns none", fromRow("Wind Farm").fuelPerKwh() == 0 && fromRow("Hydro Station").fuelPerKwh() == 0);
        check("a fuel the model has no heat content for burns none (PW2 adds the rest)",
                new BuildingsTemplate("x", BuildingType.ELECTRICITY).setFuel("WOOD_PELLETS").setHeatRate(13_300).fuelPerKwh() == 0
                        && new BuildingsTemplate("x", BuildingType.ELECTRICITY).setFuel("NO_SUCH_GOOD").setHeatRate(13_300).getFuel() == null);
        PowerDispatch.Row row = PowerDispatch.Row.fuelled("coal", DRAW, marginal(), coal.getFuel(), perKwh, coal.getVariableUpkeep() / 1e6);
        PowerDispatch.dispatch(List.of(row), DRAW, 1, PowerDispatch.VOLL_A_KWH);
        check("the fuel a month burns is its kWh x fuel a kWh, to the bit", row.kwh > 0 && row.fuelUnits == row.kwh * perKwh, row.fuelUnits + " against " + row.kwh * perKwh);

        BuildingsTemplate shipped = catalogRow("Coal Power Plant");
        check("THE CATALOG HANDS THE COAL PLANT ITS POWER KEYS: fuel, heat rate, nameplate, upkeep",
                shipped != null && shipped.getFuel() == Good.THERMAL_COAL && shipped.getHeatRate() == 8490 && shipped.getNameplate() == 325_000
                        && shipped.getVariableUpkeep() == 9.37 && shipped.fuelPerKwh() == perKwh);
        BuildingsTemplate wind = catalogRow("Wind Farm"), hydro = catalogRow("Hydro Station");
        check("...the Wind Farm is wind at 20 MW, the Hydro Station 10 MW at $1.71 a MWh",
                wind != null && wind.isWind() && wind.getNameplate() == 20_000 && hydro != null && !hydro.isWind() && hydro.getNameplate() == 10_000 && hydro.getVariableUpkeep() == 1.71);
    }

    /* ---------- 9 ---------- */

    static void worksFirst() {
        System.out.println("\n--- 9. the works' surplus is taken first ---");
        double voll = PowerDispatch.VOLL_A_KWH, m1 = marginal();
        PowerDispatch.Row works = PowerDispatch.Row.mustRun("works", DRAW * 0.3), coal = fuelled("coal", DRAW * 2, m1);
        PowerDispatch.Row wind = PowerDispatch.Row.wind("wind", DRAW * 5);
        PowerDispatch.Month m = PowerDispatch.dispatch(List.of(wind, coal, works), DRAW, 1, voll);
        boolean full = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) full &= works.gen[s] == works.kw;
        check("the must-run row runs its full kW in every half-block, though the wind is listed first", full);
        check("...and its energy is its kW over the month's hours", near(works.kwh, works.kw * PowerDispatch.MONTH_HOURS, 1e-9));
        check("...and the wind took only what was left (nothing served twice)", generated(List.of(wind, coal, works), 0) <= m.load[0] + 1e-9);

        PowerDispatch.Row alone = PowerDispatch.Row.mustRun("works", loadAt(DRAW, 1, 0) * 1.001), rest = fuelled("coal", DRAW * 2, m1);
        PowerDispatch.Month n = PowerDispatch.dispatch(List.of(alone, rest), DRAW, 1, voll);
        check("a half-block the works alone cover clears at 0 and no fuelled row runs there", n.price[0] == 0 && n.price[1] == 0 && rest.gen[0] == 0 && rest.gen[1] == 0 && n.unserved[0] == 0);
        check("...the surplus past the load is curtailed and counted", n.curtailedKwh > 0);
        check("...and a half-block they do not cover clears at the fuelled row's marginal", n.price[2] == m1 && rest.gen[2] > 0);
        check("...the works earn their kWh x the half-blocks' prices", near(alone.earned, alone.gen[2] * PowerDispatch.halfHours(2) * m1 + alone.gen[3] * PowerDispatch.halfHours(3) * m1
                + alone.gen[4] * PowerDispatch.halfHours(4) * m1 + alone.gen[5] * PowerDispatch.halfHours(5) * m1, 1e-9));
    }

    /* ---------- 10 ---------- */

    static void struckBook() throws Exception {
        System.out.println("\n--- 10. the struck book ---");
        BuildingsTemplate coal = fromRow("Coal Power Plant");
        UtilitiesHandler fresh = new UtilitiesHandler();
        check("before any strike the book is the founding tariff, three prices alike", fresh.getPricePerWatt() == UtilitiesHandler.FOUNDING_TARIFF
                && fresh.getIndustryPricePerWatt() == UtilitiesHandler.FOUNDING_TARIFF && fresh.getWorksPricePerWatt() == UtilitiesHandler.FOUNDING_TARIFF
                && fresh.getPricerPerWatt() == UtilitiesHandler.FOUNDING_TARIFF && fresh.getStruckBook().ratio == 1);

        double works = 5_000, draw = 150_000;
        UtilitiesHandler h = new UtilitiesHandler();
        h.setWattsProduction(coal.getProduction1());
        h.setWattsConsumption(draw);
        h.setWorksPower(works);
        Map<BuildingsTemplate, Integer> standing = new LinkedHashMap<>();
        standing.put(coal, 1);
        h.setPlants(standing);
        h.setBilledElectricityDraw(draw);
        h.updateEnergyRatio();
        PowerDispatch.Month m = h.getLatestMonth();
        List<PowerDispatch.Row> rows = h.getLatestRows();
        PowerDispatch.Row worksRow = null, coalRow = null;
        for (PowerDispatch.Row r : rows) { if (r.name.equals("works")) worksRow = r; if (r.name.equals(coal.getName())) coalRow = r; }
        check("fixture: the works, the founding grid and the coal plant are the rows, and the month is served", rows.size() == 3 && worksRow != null && coalRow != null && m.ratio == 1 && h.getEnergyRatio() == 1);
        double perKwh = coal.fuelPerKwh(), clears = Good.THERMAL_COAL.worldImportPrice() * perKwh + coal.getVariableUpkeep() / 1e6 * PriceLevel.FACTOR;
        check("the coal plant's marginal is the world's coal at its fuel a kWh, and the upkeep at the exchange rate (no source handed in)",
                coalRow != null && near(coalRow.marginal, clears, 1e-12), coalRow == null ? "" : coalRow.marginal + " against " + clears);
        check("...every half-block clears there, and the energy price is that over (1 - losses)", near(m.energyPrice, clears / (1 - PowerDispatch.GRID_LOSSES), 1e-9), m.energyPrice + "");
        check("fixture: the works' surplus ran and the coal plant burned coal", worksRow != null && worksRow.kwh > 0 && coalRow != null && coalRow.fuelUnits > 0);

        h.strikePower();
        UtilitiesHandler.Book b = h.getStruckBook();
        double hours = PowerDispatch.MONTH_HOURS;
        check("commerce pays the energy and all the wires: (energy + TRANSMISSION + DISTRIBUTION) x month",
                near(b.commercePrice, (m.energyPrice + PowerDispatch.TRANSMISSION_A_KWH + PowerDispatch.DISTRIBUTION_A_KWH) * hours, 1e-12), b.commercePrice + "");
        check("industry pays the energy and the transmission alone: (energy + TRANSMISSION) x month",
                near(b.industryPrice, (m.energyPrice + PowerDispatch.TRANSMISSION_A_KWH) * hours, 1e-12), b.industryPrice + "");
        check("...the two classes differ by the distribution", b.commercePrice > b.industryPrice && h.getPricePerWatt() == b.commercePrice && h.getPricerPerWatt() == b.commercePrice);
        check("the works' price x their surplus is what their row earned", worksRow != null && near(h.getWorksPricePerWatt() * works, worksRow.earned, 1e-12), h.getWorksPricePerWatt() * works + " against " + (worksRow == null ? 0 : worksRow.earned));
        check("the book keeps the kWh by kind, the coal by good and the upkeep",
                near(b.worksKwh, worksRow.kwh, 1e-12) && near(b.fuelledKwh, coalRow.kwh, 1e-12) && b.gridKwh > 0 && near(b.fuel.getOrDefault("THERMAL_COAL", 0.0), coalRow.fuelUnits, 1e-12)
                        && near(b.upkeepWorld, coalRow.kwh * coalRow.upkeepPerKwh, 1e-12) && b.windKwh == 0 && b.hydroKwh == 0);
        check("the book shares nothing with the dispatch: a new object, arrays of its own", b.price != m.price && b.load != m.load && b.unserved != m.unserved);

        UtilitiesHandler.PowerState state = h.toPowerState();
        UtilitiesHandler again = new UtilitiesHandler();
        again.restorePowerState(state);
        UtilitiesHandler.Book c = again.getStruckBook();
        check("the save's round trip gives the same prices and the same book",
                c.commercePrice == b.commercePrice && c.industryPrice == b.industryPrice && c.worksPrice == b.worksPrice && c.energyPrice == b.energyPrice
                        && java.util.Arrays.equals(c.price, b.price) && c.fuel.equals(b.fuel) && c != b);
        again.restorePowerState(null);
        check("a save with no book leaves the founding tariff", again.getPricePerWatt() == UtilitiesHandler.FOUNDING_TARIFF);

        double before = h.getElectricityIncome();
        h.setFuelPaid(100, 50, 25);
        check("the fuel bill is the three paid figures summed", h.getFuelBill() == 175 && h.getFuelLocal() == 100 && h.getFuelImported() == 50 && h.getUpkeepPaid() == 25);
        check("the electricity income falls by exactly the fuel bill", h.getElectricityIncome() == before - 175, h.getElectricityIncome() + " against " + (before - 175));
        double scaled = b.commercePrice;
        h.redenominate(10);
        check("a redenomination scales the book's prices and the fuel paid", near(h.getPricePerWatt(), scaled * 10, 1e-12) && h.getFuelBill() == 1750);
        h.seedConstants(2);
        h.updateEnergyRatio();
        h.strikePower();
        check("seedConstants re-strikes the wires: the dial over the unit",
                near(h.getStruckBook().commercePrice, (h.getLatestMonth().energyPrice + PowerDispatch.TRANSMISSION_A_KWH / 2 + PowerDispatch.DISTRIBUTION_A_KWH / 2) * hours, 1e-12));
    }
}

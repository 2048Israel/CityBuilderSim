package ham.citybuildersim;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Power observations against native dispatch, including shortage, curtailment, fuel order, phase timing and saves.
 * WHY. A screen can look plausible while showing next month's fleet or calling row receipts profit. The fixtures cause
 * each condition through the real utility, and compare projections with its retained records rather than a copied market.
 */
public final class PowerViewCheck {
    static int fails;
    static final Gson JSON = new Gson();
    static void check(String label, boolean ok) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label);
        if (!ok) fails++;
    }
    static boolean near(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }
    static BuildingsTemplate row(String name) {
        return new BuildingCatalog().load().stream().filter(t -> t.getName().equals(name)).findFirst().orElseThrow();
    }
    static UtilitiesHandler fixture(Map<BuildingsTemplate, Long> plants, double draw) {
        UtilitiesHandler u = new UtilitiesHandler();
        u.setPlants(plants);
        u.setWattsProduction(plants.entrySet().stream().mapToDouble(e -> e.getKey().getProduction1() * e.getValue()).sum());
        u.setWattsConsumption(draw);
        u.updateEnergyRatio();
        u.strikePower();
        return u;
    }
    static PowerView.View view(UtilitiesHandler u) { return PowerView.of(u, id -> "Catalogue row " + id); }

    static String savedModel(Game g) {
        var tree = JSON.toJsonTree(g.prepareSaveForChecks(10, "purity").data()).getAsJsonObject();
        tree.remove("savedAt");
        return tree.toString();
    }

    static void matches(UtilitiesHandler u) {
        PowerView.View v = view(u);
        UtilitiesHandler.Book b = u.getStruckBook();
        check("asked, generated, delivered, curtailed and unserved retain their own meanings",
                v.askedKwh() == b.askedKwh && v.generatedKwh() == b.servedKwh
                && v.deliveredKwh() == b.servedKwh * (1 - PowerDispatch.GRID_LOSSES)
                && v.curtailedKwh() == b.curtailedKwh && v.unservedKwh() == u.getUnservedKwh());
        boolean halves = v.halves().size() == PowerDispatch.HALVES;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            PowerView.Half h = v.halves().get(s);
            halves &= h.askedKw() == b.load[s] && h.unservedKw() == b.unserved[s] && h.price() == b.price[s]
                    && h.hours() == PowerDispatch.halfHours(s);
        }
        check("all six half-blocks copy the model's load, shortage, clearing price and hours", halves);
        boolean rows = true;
        double kwh = 0, receipts = 0;
        List<PowerDispatch.Row> actual = u.getLatestMonth().getMeritOrder().stream().map(u.getLatestRows()::get).toList();
        for (int i = 0; i < actual.size(); i++) {
            PowerDispatch.Row r = actual.get(i);
            UtilitiesHandler.RowRecord p = v.rows().get(i).record();
            rows &= p.kwh() == r.kwh && p.fuelUnits() == r.fuelUnits && p.receipts() == r.earned && p.marginal() == r.marginal;
            for (int s = 0; s < PowerDispatch.HALVES; s++) rows &= p.runKw().get(s) == r.gen[s];
            kwh += p.kwh(); receipts += p.receipts();
        }
        check("every actual row run, fuel, marginal cost and clearing receipt survives in dispatch order", rows);
        check("row generation sums to the retained generated energy", near(kwh, v.generatedKwh()));
        double actualReceipts = actual.stream().mapToDouble(r -> r.earned).sum();
        check("row receipts sum to the model's receipts, before operating costs", near(receipts, actualReceipts));
        check("bill parts use the struck wires; industry excludes distribution",
                near(v.bill().commerce(), v.bill().energy() + v.bill().transmission() + v.bill().distribution())
                && near(v.bill().industry(), v.bill().energy() + v.bill().transmission()));
    }

    static void dispatchCases() {
        System.out.println("--- actual half-block shortage, curtailment and empty fleet ---");
        BuildingsTemplate wind = row("Wind Farm");
        UtilitiesHandler empty = fixture(Map.of(), 0);
        double grid = empty.getLatestRows().get(0).kw;
        check("empty built fleet retains only the founding grid, with zero energy and no shortage",
                view(empty).rows().size() == 1 && view(empty).askedKwh() == 0 && view(empty).unservedKwh() == 0);
        double peakMiddle = grid + wind.getProduction1() * (PowerDispatch.windAt(4) + PowerDispatch.windAt(5)) / 2;
        UtilitiesHandler shortHalf = fixture(Map.of(wind, 1L), peakMiddle / PowerDispatch.PLAN_PEAK);
        check("fixture causes a served windy peak and an unserved calm peak",
                shortHalf.getLatestMonth().unserved[4] == 0 && shortHalf.getLatestMonth().unserved[5] > 0);
        check("the projected short half clears at the model cap", view(shortHalf).halves().get(5).price() == PowerDispatch.VOLL_A_KWH);
        matches(shortHalf);
        UtilitiesHandler surplus = fixture(Map.of(wind, 1L), grid / 2);
        check("native wind has energy turned away", surplus.getCurtailedKwh() > 0);
        matches(surplus);
        UtilitiesHandler coal = fixture(Map.of(row("Coal Power Plant"), 1L), grid * 2);
        check("native coal burns nonzero fuel and receives money", view(coal).rows().stream().anyMatch(r -> r.record().fuelUnits() > 0 && r.record().receipts() > 0));
        matches(coal);
        UtilitiesHandler hydro = fixture(Map.of(row("Hydro Station"), 1L), grid * 2);
        hydro.setWorksPower(grid / 2); hydro.updateEnergyRatio(); hydro.strikePower();
        check("native hydro and works both contribute actual recorded generation", view(hydro).rows().stream().anyMatch(r -> r.record().kind() == PowerDispatch.Kind.HYDRO && r.record().kwh() > 0)
                && view(hydro).rows().stream().anyMatch(r -> r.record().id() == UtilitiesHandler.WORKS_ID && r.record().kwh() > 0));
        matches(hydro);
        boolean immutable = false;
        try { hydro.getLatestMonth().getMeritOrder().add(0); } catch (UnsupportedOperationException expected) { immutable = true; }
        check("dispatch-order observation cannot be mutated through its getter", immutable);
    }

    static void meritMoves() {
        System.out.println("--- the actual merit order moves with the fuel price ---");
        BuildingsTemplate coal = row("Coal Power Plant");
        // Two thermal fixture rows: the efficient one has higher variable upkeep. A fuel shock reverses their native marginal costs.
        BuildingsTemplate efficient = new BuildingsTemplate("Efficient thermal fixture", BuildingType.ELECTRICITY)
                .setId(new BuildingCatalog().load().stream().mapToInt(BuildingsTemplate::getId).max().orElseThrow() + 1)
                .setProduction1(coal.getProduction1()).setNameplate(coal.getNameplate()).setFuel("THERMAL_COAL")
                .setHeatRate(coal.getHeatRate() / 2).setVariableUpkeep(coal.getVariableUpkeep() * 2);
        Map<BuildingsTemplate, Long> plants = new LinkedHashMap<>(); plants.put(coal, 1L); plants.put(efficient, 1L);
        UtilitiesHandler u = fixture(plants, coal.getProduction1() * 2);
        u.setFuelPrice(g -> 0); u.updateEnergyRatio(); u.strikePower();
        PowerView.View cheap = view(u);
        u.setFuelPrice(g -> PowerDispatch.VOLL_A_KWH / coal.fuelPerKwh()); u.updateEnergyRatio(); u.strikePower();
        PowerView.View dear = view(u);
        check("fixture crosses both marginal costs and the view follows the dispatch's reversed row ids",
                cheap.rows().get(1).record().id() == coal.getId() && dear.rows().get(1).record().id() == efficient.getId());
        matches(u);
        check("earlier view remains immutable after the next dispatch", cheap.rows().get(1).record().marginal() < dear.rows().get(2).record().marginal());
    }

    static boolean saveChecks() {
        PowerPriceCheck.City c = PowerPriceCheck.town("view-save", false, 0);
        PowerPriceCheck.quietly(() -> c.g.simulateMonths(4));
        Game g = c.g;
        PowerView.View before = PowerView.of(g);
        String saved = JSON.toJson(before), history = JSON.toJson(g.getHistorySave().snapshot());
        boolean written = g.saveGame(10, "power observations").ok;
        Game loaded = new Game(g.getGameFiles());
        PowerPriceCheck.quietly(() -> loaded.loadGameSave(10));
        boolean same = saved.equals(JSON.toJson(PowerView.of(loaded)))
                && history.equals(JSON.toJson(loaded.getHistorySave().snapshot()));
        // The ending fleet and live fuel prices cannot rewrite the saved observation.
        UtilitiesHandler u = loaded.getServicesManager().getUtilitiesHandler();
        u.setPlants(Map.of()); u.setFuelPrice(x -> PowerDispatch.VOLL_A_KWH); u.updateEnergyRatio();
        boolean held = saved.equals(JSON.toJson(PowerView.of(loaded)));
        UtilitiesHandler.PowerState state = JSON.fromJson(JSON.toJson(u.toPowerState()), UtilitiesHandler.PowerState.class);
        state.struck.dispatch = null;
        state.pendingMonth = null; state.pendingDispatch = null;
        state.paidDispatchMonth = state.fuelPaidMonth = -1;
        u.restorePowerState(state);
        u.restorePendingPower();
        boolean honestOldSave = !PowerView.of(loaded).recorded() && PowerView.of(loaded).paid().month() < 0
                && PowerView.of(loaded).bill().commerce() == before.bill().commerce();
        return written && before.recorded() && before.generatedKwh() > 0 && same && held && honestOldSave;
    }

    static boolean pendingSaveChecks() {
        PowerPriceCheck.City c = PowerPriceCheck.town("view-pending", false, 0);
        PowerPriceCheck.quietly(() -> c.g.simulateMonths(3));
        c.u.setFuelPrice(g -> Good.THERMAL_COAL.worldImportPrice() * PowerPriceCheck.SHOCK);
        c.u.updateEnergyRatio();
        double actual = c.u.getLatestMonth().energyPrice;
        String month = JSON.toJson(c.u.getLatestMonth());
        String rows = JSON.toJson(c.u.getLatestRows());
        String pending = JSON.toJson(c.u.toPowerState().pendingDispatch);
        UtilitiesHandler.PowerState originalState = c.u.toPowerState();
        String struck = JSON.toJson(c.u.getStruckBook()), paid = JSON.toJson(PowerView.of(c.g).paid());
        String history = JSON.toJson(c.g.getHistorySave().snapshot());
        boolean caused = c.u.getLatestRows().stream().anyMatch(r -> r.fuel == Good.THERMAL_COAL && r.kwh > 0 && r.fuelUnits > 0)
                && c.u.toPowerState().pendingDispatch.month() == c.g.getMonth();
        boolean saved = c.g.saveGame(10, "pending power dispatch").ok;
        saved &= c.g.saveGame(10, "pending power dispatch again").ok;
        Game loaded = new Game(c.g.getGameFiles());
        PowerPriceCheck.quietly(() -> loaded.loadGameSave(10));
        UtilitiesHandler u = loaded.getServicesManager().getUtilitiesHandler();
        double restored = u.getLatestMonth().energyPrice;
        System.out.println("  pending dispatch energy price: saved " + actual + ", restored " + restored);
        boolean retained = actual == restored && month.equals(JSON.toJson(u.getLatestMonth()))
                && rows.equals(JSON.toJson(u.getLatestRows())) && pending.equals(JSON.toJson(u.toPowerState().pendingDispatch));
        boolean olderFacts = struck.equals(JSON.toJson(u.getStruckBook())) && paid.equals(JSON.toJson(PowerView.of(loaded).paid()))
                && history.equals(JSON.toJson(loaded.getHistorySave().snapshot()));
        // Reset future-only fixture inputs to the native catalogue and native world-price fallback in both twins.
        // The completed pending Month/rows remain byte-for-byte untouched by this alignment.
        c.u.setFuelPrice(null); u.setFuelPrice(null);
        c.g.getBuildingManager().getTemplateByName("House").setElectricityConsumption(
                (int) loaded.getBuildingManager().getTemplateByName("House").getElectricityConsumption());
        // RES3 bills the building-side residential draw. Align that future cached
        // sector input too, after replacing this fixture's unsaved template override.
        c.g.getEconomyManager().setElectricityConsumption();
        c.u.setWattsConsumption(u.getConsumption());
        boolean future = c.u.getConsumption() == u.getConsumption()
                && c.g.getBuildingManager().getTemplates().stream().allMatch(t ->
                        c.g.getBuildingManager().getQuantity(t.getId()) == loaded.getBuildingManager().getQuantity(t.getId()))
                && c.g.getSectors().all().stream().allMatch(s->s.electricity==loaded.getSectors().byKey(s.key()).electricity)
                && month.equals(JSON.toJson(c.u.getLatestMonth())) && rows.equals(JSON.toJson(c.u.getLatestRows()));
        PowerPriceCheck.Step live = PowerPriceCheck.run(c);
        PowerPriceCheck.Step back = PowerPriceCheck.run(new PowerPriceCheck.City("pending-back", loaded, false));
        boolean next = JSON.toJson(c.u.getStruckBook()).equals(JSON.toJson(u.getStruckBook()));
        boolean nativeBills = live.bills.equals(back.bills) && near(live.charges, live.bills.values().stream().mapToDouble(Double::doubleValue).sum())
                && near(back.charges, back.bills.values().stream().mapToDouble(Double::doubleValue).sum());
        if(!nativeBills)System.out.println("  native bill mismatch: live="+live.bills+", loaded="+back.bills+", charges="+live.charges+"/"+back.charges);
        boolean nativeFuel = near(live.localUnits + live.importedUnits, live.book.fuel.getOrDefault(Good.THERMAL_COAL.name(), 0.0))
                && near(back.localUnits + back.importedUnits, back.book.fuel.getOrDefault(Good.THERMAL_COAL.name(), 0.0))
                && near(live.fuelLocal + live.fuelImported, live.localCost + live.importedCost)
                && near(back.fuelLocal + back.fuelImported, back.localCost + back.importedCost);
        boolean audits = Math.abs(live.audit.residual) <= MoneyAudit.tolerance(live.audit.moved())
                && Math.abs(back.audit.residual) <= MoneyAudit.tolerance(back.audit.moved());
        System.out.println("  pending premise=" + caused + ", complete retained=" + retained + ", older facts=" + olderFacts + ", matching future fleet/draw=" + future
                + ", next tariff=" + next + ", native bills=" + nativeBills + ", native fuel=" + nativeFuel + ", audits=" + audits);
        UtilitiesHandler currency = new UtilitiesHandler();
        currency.restorePowerState(originalState); currency.restorePendingPower();
        UtilitiesHandler.PowerState beforeUnit = currency.toPowerState();
        currency.redenominate(.5);
        UtilitiesHandler.PowerState afterUnit = currency.toPowerState();
        boolean units = beforeUnit.pendingMonth.energyPrice > 0 && beforeUnit.pendingDispatch.rows().stream().anyMatch(r -> r.receipts() > 0 && r.fuelUnits() > 0)
                && afterUnit.pendingMonth.energyPrice == beforeUnit.pendingMonth.energyPrice * .5
                && afterUnit.pendingMonth.askedKwh == beforeUnit.pendingMonth.askedKwh;
        for (int i = 0; i < beforeUnit.pendingDispatch.rows().size(); i++) {
            var a = beforeUnit.pendingDispatch.rows().get(i); var b = afterUnit.pendingDispatch.rows().get(i);
            units &= a.sourceIndex() == b.sourceIndex() && a.fuelUnits() == b.fuelUnits() && a.kwh() == b.kwh()
                    && b.receipts() == a.receipts() * .5 && b.marginal() == a.marginal() * .5;
        }
        return saved && caused && actual > 0 && retained && olderFacts && future && next && nativeBills && nativeFuel && audits && units;
    }

    static void timingPurityAndFold() {
        System.out.println("--- phase capture, getter purity, history and yearly FLOW folds ---");
        PowerPriceCheck.City c = PowerPriceCheck.town("view-timing", false, 0);
        Game g = c.g;
        PowerPriceCheck.quietly(() -> g.simulateMonths(3));
        UtilitiesHandler u = c.u;
        int source = g.getMonth();
        double firm = BuildAdvice.supplyDemand(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER), Map.of())[0];
        PowerDispatch.Month pending = u.getLatestMonth();
        double[] prices = pending.price.clone();
        PowerPriceCheck.quietly(() -> g.simulateMonths(1));
        PowerView.View v = PowerView.of(g);
        check("struck record names its actual preceding dispatch month", v.dispatchMonth() == source && v.struckMonth() == source + 1);
        check("firm gauge retains the existing planner's supply at dispatch, against that dispatch's peak",
                v.firmKw() == firm && v.peakKw() == pending.load[PowerDispatch.PEAK * 2]);
        boolean held = true;
        for (int s = 0; s < prices.length; s++) held &= prices[s] == v.halves().get(s).price();
        check("the strike retains the preceding six clearing prices, not the next dispatch", held);
        check("fuel payment identifies its older dispatch and actual payment month separately",
                v.paid().dispatchMonth() == v.dispatchMonth() - 1 && v.paid().month() == v.struckMonth());
        String before = JSON.toJson(u.toPowerState());
        String modelBefore = savedModel(g);
        Map<String, Double> quotesBefore = new LinkedHashMap<>(ReadPathCheck.adviceReads(g));
        for (int i = 0; i < 100; i++) {
            PowerView.series(PowerView.of(g));
            BuildAdvice.supplyDemand(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER), Map.of());
        }
        check("repeated view reads leave all retained utility state unchanged", before.equals(JSON.toJson(u.toPowerState())));
        check("repeated view and firm-supplier reads leave serialized model, balances, investment and quotes unchanged",
                modelBefore.equals(savedModel(g)) && quotesBefore.equals(ReadPathCheck.adviceReads(g)));
        Map<String, Double> values = PowerView.series(v);
        HistorySave h = g.getHistorySave();
        boolean historyMatches = true;
        for (var e : values.entrySet()) {
            double[] series = h.raw(e.getKey());
            historyMatches &= series.length > 0 && series[series.length - 1] == e.getValue();
        }
        check("history records tariff, fuel payment, six prices and permanent-row flows from the retained book", historyMatches);
        Map<String, Object> fixture = new LinkedHashMap<>();
        List<Integer> axis = new ArrayList<>();
        Map<String, List<Double>> series = new LinkedHashMap<>();
        String energy = "powerFlow:generatedKwh", receipts = values.keySet().stream().filter(k -> k.endsWith(":receipts") && values.get(k) > 0).findFirst().orElseThrow();
        for (String key : List.of(energy, receipts, "powerPrice:energy")) series.put(key, new ArrayList<>());
        for (int m = 0; m < HistorySave.MONTHLY_KEPT + 24; m++) {
            axis.add(m);
            series.forEach((key, list) -> list.add(values.get(key)));
        }
        fixture.put("month", axis); fixture.put("powerSeries", series);
        HistorySave fold = JSON.fromJson(JSON.toJson(fixture), HistorySave.class);
        fold.foldOldYears();
        check("monthly-to-year folds actually occur", fold.raw(energy).length < axis.size());
        for (String key : List.of(energy, receipts)) {
            double sum = java.util.Arrays.stream(fold.raw(key)).sum();
            check(key + " is a FLOW and its yearly fold preserves the actual monthly total",
                    YearBook.kindOf(key) == YearBook.Kind.FLOW && near(sum, values.get(key) * axis.size()));
        }
        check("prices fold as rates, not annual bills", YearBook.kindOf("powerPrice:energy") == YearBook.Kind.RATE
                && fold.raw("powerPrice:energy")[0] == values.get("powerPrice:energy"));
        HistorySave snapshot = h.snapshot();
        double kwh = snapshot.raw(energy)[snapshot.raw(energy).length - 1];
        double price = snapshot.raw("powerPrice:energy")[snapshot.raw("powerPrice:energy").length - 1];
        h.redenominate(.5);
        check("currency reform scales only money/prices, and snapshots share no mutable lists",
                h.raw(energy)[h.raw(energy).length - 1] == kwh
                && h.raw("powerPrice:energy")[h.raw("powerPrice:energy").length - 1] == price * .5
                && snapshot.raw("powerPrice:energy")[snapshot.raw("powerPrice:energy").length - 1] == price);
    }

    /** Fixed native months cause departure and rejoin; synthetic tail aging only puts that actual history through its yearly fold. */
    static void rowLeaveRejoinHistory() {
        System.out.println("--- a permanent row leaves, stays absent and rejoins without a fabricated cost rate ---");
        PowerPriceCheck.City c = PowerPriceCheck.town("view-row-gap", false, 0);
        Game g = c.g;
        PowerPriceCheck.quietly(() -> g.simulateMonths(3));
        BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
        int id = coal.getId();
        String energy = "powerFlow:row:" + id + ":kwh", fuel = "powerFlow:row:" + id + ":fuel";
        String receipts = "powerMoney:row:" + id + ":receipts", marginal = "powerPrice:row:" + id + ":marginal";
        PowerView.Row observed = PowerView.of(g).rows().stream().filter(r -> r.record().id() == id).findFirst().orElseThrow();
        check("fixture starts with observed native coal generation, fuel, receipts and nonzero marginal cost",
                observed.record().kwh() > 0 && observed.record().fuelUnits() > 0 && observed.record().receipts() > 0 && observed.record().marginal() > 0);
        int first = g.getHistorySave().getMonth().size();
        long quantity = g.getBuildingManager().getQuantity(id);
        check("the entire observed catalogue row departs through the native retirement API",
                quantity > 0 && g.getBuildingManager().retire(coal, quantity) == quantity && g.getBuildingManager().getQuantity(id) == 0);
        double[][] expected = new double[5][3];
        boolean[] present = new boolean[5];
        for (int step = 0; step < 5; step++) {
            if (step == 3) {
                g.getBuildingManager().addStack(coal, quantity, true);
                check("the same permanent row rejoins through the native stack API", g.getBuildingManager().getQuantity(id) == quantity);
            }
            PowerPriceCheck.quietly(() -> g.simulateMonths(1));
            PowerView.View v = PowerView.of(g);
            PowerView.Row row = v.rows().stream().filter(r -> r.record().id() == id).findFirst().orElse(null);
            present[step] = row != null;
            if (row != null) {
                expected[step] = new double[] {row.record().kwh(), row.record().fuelUnits(), row.record().receipts()};
                check("observed running cost remains in the retained row at event step " + step, row.record().marginal() > 0);
            }
            check("actual tariff remains the retained model tariff at event step " + step,
                    v.bill().commerce() == c.u.getPricePerWatt() / PowerDispatch.MONTH_HOURS
                    && v.bill().energy() == c.u.getStruckBook().energyPrice);
        }
        check("the original completed dispatch precedes three absent strikes, then the rejoin dispatch",
                present[0] && !present[1] && !present[2] && !present[3] && present[4]
                && expected[0][0] > 0 && expected[4][0] > 0 && expected[4][1] > 0 && expected[4][2] > 0);
        HistorySave h = g.getHistorySave();
        String[] flows = {energy, fuel, receipts};
        for (int column = 0; column < flows.length; column++) {
            double[] actual = h.raw(flows[column]);
            boolean aligned = true;
            for (int step = 0; step < expected.length; step++) aligned &= actual[first + step] == expected[step][column];
            check(flows[column] + " records zero in the actual absent positions and aligns the rejoining flow", aligned);
        }
        check("row marginal cost is omitted from current history exports and series projection",
                !h.seriesByName().containsKey(marginal) && !PowerView.series(PowerView.of(g)).containsKey(marginal));
        HistorySave read = h.snapshot();
        double[] nativePrices = read.raw("powerPrice:energy");
        double laterPrice = nativePrices[nativePrices.length - 1];
        List<Integer> axis = new ArrayList<>(read.getMonth());
        int last = axis.get(axis.size() - 1);
        check("all actual departure/rejoin events lie in the first calendar year to be folded",
                CityCalendar.yearOf(axis.get(0)) == CityCalendar.yearOf(last));
        Map<String, List<Double>> aging = new LinkedHashMap<>();
        Map<String, Double> tails = new LinkedHashMap<>();
        for (var entry : read.seriesByName().entrySet()) if (entry.getKey().startsWith("power")) {
            List<Double> values = new ArrayList<>();
            for (Number n : entry.getValue()) values.add(n.doubleValue());
            aging.put(entry.getKey(), values);
            tails.put(entry.getKey(), YearBook.kindOf(entry.getKey()) == YearBook.Kind.FLOW ? 0 : values.get(values.size() - 1));
        }
        for (int month = last + 1; month <= last + HistorySave.MONTHLY_KEPT + 12; month++) {
            axis.add(month);
            aging.forEach((key, values) -> values.add(tails.get(key)));
        }
        Map<String, Object> aged = new LinkedHashMap<>(); aged.put("month", axis); aged.put("powerSeries", aging);
        HistorySave folded = JSON.fromJson(JSON.toJson(aged), HistorySave.class);
        folded.foldOldYears();
        check("the native departure/rejoin year actually folds", folded.yearlyPoints() > 0 && folded.monthsIn(0) > 1);
        for (String flow : flows) check(flow + " yearly FLOW equals the actual monthly total including the absent gap",
                YearBook.kindOf(flow) == YearBook.Kind.FLOW && near(folded.raw(flow)[0], java.util.Arrays.stream(h.raw(flow)).filter(Double::isFinite).sum()));
        int fillerInYear = folded.monthsIn(0) - nativePrices.length;
        double expectedPrice = (java.util.Arrays.stream(nativePrices).filter(Double::isFinite).sum() + laterPrice * fillerInYear)
                / (java.util.Arrays.stream(nativePrices).filter(Double::isFinite).count() + fillerInYear);
        check("the real global energy price retains its own annual RATE mean", near(folded.raw("powerPrice:energy")[0], expectedPrice));
        check("no omitted marginal RATE is exposed by folds or folded JSON",
                !folded.seriesByName().containsKey(marginal) && !JSON.toJson(folded).contains(marginal));
    }

    public static void main(String[] args) {
        dispatchCases(); meritMoves(); timingPurityAndFold(); rowLeaveRejoinHistory();
        System.out.println("--- actual save/load and missing old observations ---");
        check("power row/tariff/history observations survive the actual save/load, ending-state changes and older incomplete books", saveChecks());
        check("the saved closing dispatch feeds the next native month without being rebuilt from ending state", pendingSaveChecks());
        System.out.println("PowerViewCheck: " + (fails == 0 ? "all ok" : fails + " FAILED"));
        System.exit(fails);
    }
}

package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * The power screen's pure projection of a retained dispatch and its struck tariff.
 * WHY. Ending fleet, fuel prices and the next dispatch cannot explain an earlier month's clearing receipts.
 * Every figure here comes from the recorded book; no dispatch, credit or pricing policy runs on the read path.
 * Power is kW, energy kWh, money thousands of the city's currency, and prices thousands per kWh.
 */
public final class PowerView {
    private PowerView() { }

    public record Half(String name, double hours, double askedKw, double unservedKw, double price) { }
    public record Row(String name, UtilitiesHandler.RowRecord record) { }
    public record Bill(double energy, double transmission, double distribution, double commerce, double industry) { }
    public record Payment(int dispatchMonth, int month, double localFuel, double importedFuel, double upkeep) { }
    public record View(boolean cleared, boolean recorded, int dispatchMonth, int struckMonth, List<Row> rows,
            List<Half> halves, double askedKwh, double generatedKwh, double deliveredKwh, double unservedKwh,
            double curtailedKwh, double firmKw, double peakKw, Bill bill, Payment paid) {
        public View { rows = List.copyOf(rows); halves = List.copyOf(halves); }
    }

    public static View of(Game game) {
        return of(game.getServicesManager().getUtilitiesHandler(), id -> {
            BuildingsTemplate t = game.getBuildingManager().getTemplate(id);
            return t == null ? "Power plant" : t.getName();
        });
    }

    /** The same public observation path, with catalogue names supplied separately for native dispatch fixtures. */
    public static View of(UtilitiesHandler utility, IntFunction<String> names) {
        UtilitiesHandler.Book b = utility.toPowerState().struck;
        UtilitiesHandler.DispatchRecord d = b.dispatch;
        List<Row> rows = new ArrayList<>();
        if (d != null) for (UtilitiesHandler.RowRecord r : d.rows()) {
            String name = switch (r.id()) {
                case UtilitiesHandler.WORKS_ID -> "Works' surplus";
                case UtilitiesHandler.GRID_ID -> "Founding grid";
                case UtilitiesHandler.FIXTURE_ID -> "Other plants";
                default -> names.apply(r.id());
            };
            rows.add(new Row(name, r));
        }
        List<Half> halves = new ArrayList<>();
        String[] blocks = {"Base", "Shoulder", "Peak"};
        double peak = 0;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            halves.add(new Half(blocks[s / 2] + (s % 2 == 0 ? " · windy" : " · calm"),
                    PowerDispatch.halfHours(s), b.load[s], b.unserved[s], b.price[s]));
            peak = Math.max(peak, b.load[s]);
        }
        return new View(b.cleared, d != null, d == null ? -1 : d.month(), b.struckMonth, rows, halves,
                b.askedKwh, b.servedKwh, b.servedKwh * (1 - PowerDispatch.GRID_LOSSES), utility.getUnservedKwh(),
                b.curtailedKwh, d == null ? -1 : d.firmKw(), peak,
                new Bill(b.energyPrice, b.transmissionPrice, b.distributionPrice,
                        b.commercePrice / PowerDispatch.MONTH_HOURS, b.industryPrice / PowerDispatch.MONTH_HOURS),
                new Payment(utility.getPaidDispatchMonth(), utility.getFuelPaidMonth(), utility.getFuelLocal(), utility.getFuelImported(), utility.getUpkeepPaid()));
    }

    /** Bounded series keyed by permanent catalogue id, never by building or mutable title. Captured at the book's strike axis. */
    public static Map<String, Double> series(View v) {
        Map<String, Double> m = new LinkedHashMap<>();
        if (!v.recorded()) return m;
        m.put("powerLevel:dispatchMonth", (double) v.dispatchMonth());
        m.put("powerLevel:firmKw", v.firmKw()); m.put("powerLevel:peakKw", v.peakKw());
        m.put("powerFlow:askedKwh", v.askedKwh()); m.put("powerFlow:generatedKwh", v.generatedKwh());
        m.put("powerFlow:deliveredKwh", v.deliveredKwh()); m.put("powerFlow:unservedKwh", v.unservedKwh());
        m.put("powerFlow:curtailedKwh", v.curtailedKwh());
        m.put("powerMoney:localFuel", v.paid().localFuel()); m.put("powerMoney:importedFuel", v.paid().importedFuel());
        m.put("powerMoney:upkeep", v.paid().upkeep());
        m.put("powerLevel:paidDispatchMonth", (double) v.paid().dispatchMonth());
        m.put("powerLevel:paymentMonth", (double) v.paid().month());
        Bill b = v.bill();
        m.put("powerPrice:energy", b.energy()); m.put("powerPrice:transmission", b.transmission());
        m.put("powerPrice:distribution", b.distribution()); m.put("powerPrice:commerce", b.commerce());
        m.put("powerPrice:industry", b.industry());
        for (int s = 0; s < v.halves().size(); s++) {
            Half h = v.halves().get(s);
            m.put("powerPrice:half:" + s, h.price());
            m.put("powerLevel:load:" + s, h.askedKw()); m.put("powerLevel:unserved:" + s, h.unservedKw());
        }
        for (Row row : v.rows()) {
            UtilitiesHandler.RowRecord r = row.record();
            m.put("powerFlow:row:" + r.id() + ":kwh", r.kwh());
            m.put("powerFlow:row:" + r.id() + ":fuel", r.fuelUnits());
            m.put("powerMoney:row:" + r.id() + ":receipts", r.receipts());
            m.put("powerPrice:row:" + r.id() + ":marginal", r.marginal());
        }
        return m;
    }
}

package ham.citybuildersim;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The manufacturing margin study's probe (MA1, docs/studies/manufacturing-margin.md): a year of every
 * sector's books and exports, of the metal goods' prices and flows, and each January what the planner
 * weighs for every Manufacturing and Automotive works, summed month by month, tagged rows of one CSV at
 * each twelfth month.
 *
 * WHY: CU2's notes read the chained city's exports from one kept month (Automotive $1.64B and
 * Manufacturing $0.75B of $3.45B, 41% of GDP), which says that the two sectors carry the city and not
 * which works, which price and which cost make their margin. This reads the books beside the works
 * standing, each metal good's band and flows, and the planner's estimate of each works, on the default
 * playtest itself (run.sh's hook, as LS1's and CU1's), through public getters only; it changes nothing.
 */
public final class MarginProbe {

    static final Good[] GOODS = {Good.STEEL, Good.FABRICATED_STEEL, Good.MACHINERY, Good.CARS, Good.VANS,
            Good.ROLLING_STOCK, Good.TRUCKS, Good.LUBRICANTS, Good.WIRE_CABLE, Good.COPPER_ROD};
    static final String[] MAKERS = {Sectors.MANUFACTURING, Sectors.AUTOMOTIVE, Sectors.HEAVY_INDUSTRY};

    /** A sector's year: revenue, inputs, payroll, electricity, water, maintenance, propertyTax, interest, tax,
     *  net, workers, posts, exportValue, consumables. */
    private static final Map<String, double[]> books = new LinkedHashMap<>();
    /** A good's year: 0-4 prices summed (local, import, export, worldImportSet, worldExportSet), 5-7 the
     *  market's imported, exported, localFilled, then for each of MAKERS 4 figures: produced, exported,
     *  boughtLocal, imported. */
    private static final Map<Good, double[]> goods = new EnumMap<>(Good.class);
    private static PrintStream csv;
    private static double gdp, exports, imports, months;

    private MarginProbe() {}

    public static void month(Game g) {
        if (csv == null) open();
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
            if (m == null) continue;
            double exportValue = 0;
            for (Good good : Good.values()) {
                Sector.Output o = s.outputRow(good);
                if (o == null || o.exported <= 0) continue;
                GoodsMarket mk = g.getMarkets().get(good);
                if (mk != null && Double.isFinite(mk.exportPrice())) exportValue += o.exported * mk.exportPrice();
            }
            double[] a = books.computeIfAbsent(s.key(), k -> new double[14]);
            double[] v = {m.revenue(), m.inputs(), m.payroll(), m.electricity(), m.water(), m.maintenance(),
                    m.propertyTax(), m.interest(), m.tax(), m.netIncome(), s.getWorkers(), s.getPostsOffered(),
                    exportValue, s.consumablesBill()};
            for (int i = 0; i < v.length; i++) a[i] += v[i];
        }
        for (Good good : GOODS) {
            GoodsMarket mk = g.getMarkets().get(good);
            if (mk == null) continue;
            double[] a = goods.computeIfAbsent(good, k -> new double[8 + 4 * MAKERS.length]);
            a[0] += mk.getLocalPrice(); a[1] += fin(mk.importPrice()); a[2] += fin(mk.exportPrice());
            a[3] += fin(mk.getWorldImportSet()); a[4] += fin(mk.getWorldExportSet());
            a[5] += mk.getImported(); a[6] += mk.getExported(); a[7] += mk.getLocalFilled();
            for (int k = 0; k < MAKERS.length; k++) {
                Sector s = g.getSectors().byKey(MAKERS[k]);
                if (s == null) continue;
                Sector.Output o = s.outputRow(good);
                if (o != null) { a[8 + 4 * k] += o.produced; a[9 + 4 * k] += o.exported; }
                Sector.Input in = s.inputRow(good);
                if (in != null) { a[10 + 4 * k] += in.boughtLocal; a[11 + 4 * k] += in.imported; }
            }
        }
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        gdp += g.getEconomyManager().getMonthGdp();
        exports += na.getExports();
        imports += na.getTotalImports();
        months++;
        if (g.getMonth() % 12 == 0) print(g);
    }

    private static double fin(double x) { return Double.isFinite(x) ? x : 0; }

    private static void open() {
        String path = System.getProperty("margin.out", "margin.csv");
        try {
            csv = new PrintStream(path);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("cannot write " + path, e);
        }
    }

    private static String f(double x) { return String.format(Locale.ROOT, "%.6g", x); }

    private static void print(Game g) {
        int m = g.getMonth();
        LabourMarket lm = g.getLabourMarket();
        PopulationManager pm = g.getPopulationManager();
        long[] jobs = pm.getJobs();
        long[] vacant = pm.getJobVacancy();
        double staffed = 0, bill = 0;
        for (JobType j : JobType.values()) {
            double n = Math.max(0, jobs[j.ordinal()] - vacant[j.ordinal()]);
            staffed += n;
            bill += n * lm.getWage(j);
        }
        double rate = g.getMarkets() == null ? Double.NaN : g.getMarkets().getExchangeRate();
        csv.println("Y," + m + "," + f(g.getPriceIndex().getIndex()) + "," + f(staffed > 0 ? bill / staffed : Double.NaN)
                + "," + f(rate) + "," + f(gdp) + "," + f(exports) + "," + f(imports) + "," + pm.getPopulation()
                + "," + f(staffed) + "," + (int) months);
        for (Map.Entry<String, double[]> e : books.entrySet()) {
            StringBuilder r = new StringBuilder("S," + m + "," + e.getKey().replace(',', ';'));
            for (double x : e.getValue()) r.append(',').append(f(x));
            csv.println(r);
        }
        for (Map.Entry<Good, double[]> e : goods.entrySet()) {
            StringBuilder r = new StringBuilder("G," + m + "," + e.getKey());
            for (double x : e.getValue()) r.append(',').append(f(x));
            csv.println(r);
        }
        // The band with nothing traded (the nations' own, at the city's site), thousands of world dollars a unit,
        // beside the literals.
        WorldMarket wm = g.getWorldMarket();
        if (wm != null) {
            for (Good good : GOODS) {
                if (!wm.trades(good)) continue;
                double[] none = wm.band(good, 0, 0);
                csv.println("N," + m + "," + good + "," + f(none[0]) + "," + f(none[1]) + ","
                        + f(good.worldExportPrice()) + "," + f(good.worldImportPrice()));
            }
        }
        // Each January, every works of the two sectors: how many stand, what the planner says one more would clear a
        // month and costs, and its sales and goods bought at this month's prices (the maker's arithmetic, before its
        // running and standing costs), and its posts' wage bill at the city's wages.
        BusinessInvestment plans = g.getBusinessInvestment();
        for (String key : new String[] {Sectors.MANUFACTURING, Sectors.AUTOMOTIVE}) {
            Sector s = g.getSectors().byKey(key);
            if (s == null || plans == null) continue;
            for (BuildingsTemplate t : g.getBuildingManager().getTemplatesBySector(key)) {
                double sales = 0, bought = 0, wages = 0;
                for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
                    GoodsMarket mk = g.getMarkets().get(e.getKey());
                    if (mk == null) continue;
                    double p = Double.isFinite(mk.netExportPrice()) ? Math.max(mk.netExportPrice(), mk.getLocalPrice()) : mk.getLocalPrice();
                    sales += e.getValue() * p;
                }
                for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
                    GoodsMarket mk = g.getMarkets().get(e.getKey());
                    if (mk != null) bought += e.getValue() * mk.getLocalPrice();
                }
                for (JobType j : JobType.values()) wages += t.getJobs(j) * lm.getWage(j);
                double profit = Double.NaN, cost = Double.NaN;
                try {
                    profit = s.estimatedMonthlyProfit(t, plans);
                    cost = plans.getCostOf(t, 1);
                } catch (RuntimeException ex) {
                    // a template the planner cannot price this month (not yet researched): its NaNs say so
                }
                csv.println("T," + m + "," + key + "," + t.getName().replace(',', ';') + ","
                        + g.getBuildingManager().countByName(t.getName()) + "," + f(sales) + "," + f(bought) + ","
                        + f(wages) + "," + f(profit) + "," + f(cost) + "," + f(t.getConsumables()));
            }
        }
        csv.flush();
        books.clear();
        goods.clear();
        gdp = exports = imports = months = 0;
    }
}

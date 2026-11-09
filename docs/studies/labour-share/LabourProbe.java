package ham.citybuildersim;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The labour-share study's probe (LS1, docs/studies/spec-labour-share.md):
 * each sector's year of books, summed month by month, one CSV row a sector a
 * year, and one CITY row a year.
 *
 * WHY: HD1-L read Luxury Retail's payroll as 1% of its value added and
 * Construction's as 5%, from one month of one city. A share is a ratio of two
 * flows, so the study sums twelve months of each before dividing, on the
 * default playtest itself (run.sh calls this from a copy of
 * LongPlaytest.run(), one line after g.simulateMonths(1)), so the city
 * measured is the one the gate plays. It reads only public getters and
 * changes nothing.
 */
public final class LabourProbe {

    /** The flows summed over the year, in the order the CSV prints them. */
    static final String[] FLOWS = {"revenue", "salesTax", "inputs", "payroll", "electricity", "water",
            "maintenance", "propertyTax", "operatingIncome", "interest", "preTax", "tax", "net",
            "workers", "posts", "produced", "sold", "valueOfOutput"};

    private static PrintStream csv;
    private static final Map<String, double[]> year = new LinkedHashMap<>();
    private static final Map<String, Map<Good, double[]>> goods = new LinkedHashMap<>();
    private static final Map<String, String> labels = new LinkedHashMap<>();
    private static double gdp, hhWages, months;

    private LabourProbe() {}

    /** One month: add each sector's month to its year, and print the year at each twelfth month. */
    public static void month(Game g) {
        if (csv == null) open();
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
            double[] a = year.computeIfAbsent(s.key(), k -> new double[FLOWS.length]);
            labels.put(s.key(), s.label());
            double[] v = {m.revenue(), m.salesTaxPaid(), m.inputs(), m.payroll(), m.electricity(), m.water(),
                    m.maintenance(), m.propertyTax(), m.operatingIncome(), m.interest(), m.preTaxIncome(),
                    m.tax(), m.netIncome(), s.getWorkers(), s.getPostsOffered(), 0, 0, 0};
            Map<Good, double[]> byGood = goods.computeIfAbsent(s.key(), k -> new EnumMap<>(Good.class));
            for (Good good : Good.values()) {
                Sector.Output o = s.outputRow(good);
                if (o == null || o.produced <= 0) continue;
                double sold = o.soldLocal + o.exported;
                double price = s.stockPrice(good);
                v[15] += o.produced;
                v[16] += sold;
                v[17] += o.produced * price;
                double[] b = byGood.computeIfAbsent(good, k -> new double[3]);
                b[0] += o.produced;
                b[1] += o.produced * price;
                b[2] += o.costPerUnit * o.produced;
            }
            for (int i = 0; i < v.length; i++) a[i] += v[i];
        }
        gdp += g.getEconomyManager().getMonthGdp();
        hhWages += g.getHouseholds().getWages();
        months++;
        if (g.getMonth() % 12 == 0) print(g);
    }

    private static void open() {
        String path = System.getProperty("labour.out", "labour.csv");
        try {
            csv = new PrintStream(path);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("cannot write " + path, e);
        }
        StringBuilder h = new StringBuilder("month,sector,label,months");
        for (String f : FLOWS) h.append(',').append(f);
        h.append(",mainGood,mainUnits,mainPrice,mainCostPerUnit,gdp,hhWages,priceIndex,meanWage,costOfLiving,population");
        csv.println(h);
    }

    private static void print(Game g) {
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
        String city = String.format(",%.6g,%.6g,%.6g,%.6g,%.6g,%d", gdp, hhWages, g.getPriceIndex().getIndex(),
                staffed > 0 ? bill / staffed : Double.NaN, lm.getCostOfLiving(), pm.getPopulation());
        for (Map.Entry<String, double[]> e : year.entrySet()) {
            StringBuilder r = new StringBuilder();
            r.append(g.getMonth()).append(',').append(e.getKey()).append(',')
                    .append(labels.get(e.getKey()).replace(',', ' ')).append(',').append((int) months);
            for (double x : e.getValue()) r.append(',').append(String.format("%.6g", x));
            Good main = null;
            double[] best = null;
            for (Map.Entry<Good, double[]> b : goods.get(e.getKey()).entrySet()) {
                if (best == null || b.getValue()[1] > best[1]) { main = b.getKey(); best = b.getValue(); }
            }
            if (main == null) r.append(",,,,");
            else r.append(',').append(main).append(String.format(",%.6g,%.6g,%.6g", best[0],
                    best[1] / best[0], best[2] / best[0]));
            r.append(city);
            csv.println(r);
        }
        csv.flush();
        year.clear();
        goods.clear();
        gdp = hhWages = months = 0;
    }
}

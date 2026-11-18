package ham.citybuildersim;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The luxury-budget study's probe (LQ1, docs/studies/luxury-budget.md): the
 * households' luxury budget a year at a time, the city's and each cell's, split
 * into its wealth term and its surplus term, beside what the counter sold, the
 * shops' books and the city's accounts.
 *
 * WHY: LX1 put Luxury Retail's posts at BEA's payroll share of its margin, and
 * the margin is 10-30% of the business sectors' value added, so the shops took a
 * quarter of the old city's workers. Whether the budget is too large is a ratio
 * of flows (the budget over income, over GDP, over consumption), so the probe
 * sums twelve months of each before dividing, on the default playtest itself
 * (run.sh calls month() from a copy of LongPlaytest.run(), one line after
 * g.simulateMonths(1)). It reads getters and package fields and changes nothing;
 * the one private field it reads (the households' exchange rate) is read by
 * reflection.
 *
 * The split: Household.plan() strikes luxuryWant = spendFactor x
 * WEALTH_SPENT_A_MONTH x netWorth + LUXURY_SHARE_OF_SURPLUS x surplus, where
 * netWorth counts savings, the money abroad, the paper and the bonds at the
 * month's ratios, less debt. The probe strikes the wealth term again from the
 * same fields and takes the surplus term as the rest; the plan runs before the
 * counter in the same month (month-order: updateHouseholdAccounts, then
 * luxuryCounter.shop), so the month's end holds the plan the counter read.
 */
public final class LuxuryProbe {

    private static PrintStream city, cells;
    private static final String[] CITY = {"gdp", "consumption", "consGoods", "consHousing", "government",
            "investment", "netExports", "exports", "hhWages", "hhDisposable", "hhSpending",
            "luxWant", "luxWealth", "luxSurplus", "luxWantGated", "luxWantedPieces", "luxWantedValue",
            "luxSold", "luxSpend", "mealWant", "mealSpend", "mealsServed",
            "netWorth", "savings", "abroadValue", "paperValue", "bondsValue", "debt",
            "lrRevenue", "lrInputs", "lrPayroll", "lrUtilities", "lrNet", "lrWorkers", "lrPosts", "lrCoverage",
            "lrServed", "lrMargin", "lrLanded", "boutiques", "deptStores",
            "staffed", "sectorWorkers", "retailWorkers", "restaurantWorkers", "people", "investmentIncome"};
    private static final double[] sum = new double[CITY.length];
    private static final Map<String, double[]> byCell = new LinkedHashMap<>();
    private static final Map<String, String> cellInfo = new LinkedHashMap<>();
    private static final String[] CELL = {"households", "people", "luxWant", "luxWealth", "luxSurplus",
            "luxWantGated", "netWorth", "disposable", "afterFixed", "mealWant", "gatedOut"};
    private static double months;
    private static java.lang.reflect.Field localPerUsdField;

    private LuxuryProbe() {}

    /** One month: add it to the year, and print the year at each twelfth month. */
    public static void month(Game g) {
        if (city == null) open();
        HouseholdBalance hb = g.getHouseholdBalance();
        double sf = hb.getSpendFactor(), pr = hb.getPaperRatio(), br = hb.getBondRatio();
        double fx = localPerUsd(hb);
        double luxWant = 0, luxWealth = 0, luxSurplus = 0, luxGated = 0, mealWant = 0;
        double nw = 0, sav = 0, ab = 0, pa = 0, bo = 0, de = 0, inv = 0;
        for (Household c : hb.cells()) {
            if (c.isEmpty()) continue;
            double h = c.households;
            double abroadV = c.abroad * Math.max(0, fx), paperV = c.paper * Math.max(0, pr),
                    bondsV = c.bonds * Math.max(0, br);
            double netWorth = Math.max(0, c.savings + abroadV + paperV + bondsV - c.debt);
            double want = Math.max(0, c.luxuryWant);
            double wealth = Math.min(want, sf * HouseholdBalance.WEALTH_SPENT_A_MONTH * netWorth);
            double surplus = want - wealth;
            boolean gated = !c.canInvest() || c.lockout > 0 || c.isGoingShort();
            luxWant += want * h; luxWealth += wealth * h; luxSurplus += surplus * h;
            if (!gated) luxGated += want * h;
            mealWant += Math.max(0, c.mealWant) * h;
            nw += netWorth * h; sav += c.savings * h; ab += abroadV * h; pa += paperV * h; bo += bondsV * h;
            de += c.debt * h;
            inv += Math.max(0, c.investmentIncome) * h;
            String key = c.key();
            cellInfo.putIfAbsent(key, c.label().replace(',', ' ') + "," + c.tier() + "," + c.isRetired() + ","
                    + c.row() + "," + c.getClass().getSimpleName());
            double[] a = byCell.computeIfAbsent(key, k -> new double[CELL.length]);
            double[] v = {h, c.people(), want * h, wealth * h, surplus * h, gated ? 0 : want * h, netWorth * h,
                    c.disposable * h, c.afterFixed * h, Math.max(0, c.mealWant) * h, gated ? 1 : 0};
            for (int i = 0; i < v.length; i++) a[i] += v[i];
        }

        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        HouseholdAccounts ha = g.getHouseholds();
        ham.citybuildersim.sectors.LuxuryRetail lr = g.getSectors().luxuryRetail();
        SectorBooks.SectorMonth m = lr == null ? null : g.getSectorBooks().get(lr);
        double sectorWorkers = 0, retailWorkers = 0, restaurantWorkers = 0;
        for (Sector s : g.getSectors().all()) {
            sectorWorkers += s.getWorkers();
            if (s.key().equals("Retail")) retailWorkers += s.getWorkers();
            if (s.key().equals("Restaurants")) restaurantWorkers += s.getWorkers();
        }
        LabourMarket lm = g.getLabourMarket();
        PopulationManager pm = g.getPopulationManager();
        long[] jobs = pm.getJobs();
        long[] vacant = pm.getJobVacancy();
        double staffed = 0;
        for (JobType j : JobType.values()) staffed += Math.max(0, jobs[j.ordinal()] - vacant[j.ordinal()]);

        double[] v = {na.getGdp(), na.getConsumption(), na.getConsumptionGoods(), na.getConsumptionHousing(),
                na.getGovernment(), na.getInvestment(), na.getNetExports(), na.getExports(),
                ha.getWages(), ha.getDisposableIncome(), ha.getSpending(),
                luxWant, luxWealth, luxSurplus, luxGated, g.getLuxuryWanted(),
                g.getLuxuryWanted() * g.getLuxuryPrice(), g.getLuxuriesSold(), g.getLuxurySpend(),
                mealWant, g.getMealSpend(), g.getMealsServed(),
                nw, sav, ab, pa, bo, de,
                m == null ? 0 : m.revenue(), m == null ? 0 : m.inputs(), m == null ? 0 : m.payroll(),
                m == null ? 0 : m.electricity() + m.water() + m.maintenance(), m == null ? 0 : m.netIncome(),
                lr == null ? 0 : lr.getWorkers(), lr == null ? 0 : lr.getPostsOffered(),
                lr == null ? 0 : lr.getCoverage(), lr == null ? 0 : lr.getServed(),
                lr == null ? 0 : lr.getMargin(), lr == null ? 0 : lr.getLanded(),
                lr == null ? 0 : lr.buildings().countByName("Boutique"),
                lr == null ? 0 : lr.buildings().countByName("Department Store"),
                staffed, sectorWorkers, retailWorkers, restaurantWorkers, pm.getPopulation(), inv};
        for (int i = 0; i < v.length; i++) sum[i] += v[i];
        months++;
        if (g.getMonth() % 12 == 0) print(g, lm);
    }

    private static double localPerUsd(HouseholdBalance hb) {
        try {
            if (localPerUsdField == null) {
                localPerUsdField = HouseholdBalance.class.getDeclaredField("localPerUsd");
                localPerUsdField.setAccessible(true);
            }
            return localPerUsdField.getDouble(hb);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("the households' exchange rate moved", e);
        }
    }

    private static void open() {
        String base = System.getProperty("luxury.out", "luxury");
        try {
            city = new PrintStream(base + "-city.csv");
            cells = new PrintStream(base + "-cells.csv");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("cannot write " + base, e);
        }
        StringBuilder h = new StringBuilder("month,months");
        for (String f : CITY) h.append(',').append(f);
        h.append(",priceIndex,meanWage");
        city.println(h);
        StringBuilder c = new StringBuilder("month,months,key,label,tier,retired,row,kind");
        for (String f : CELL) c.append(',').append(f);
        cells.println(c);
    }

    private static void print(Game g, LabourMarket lm) {
        PopulationManager pm = g.getPopulationManager();
        long[] jobs = pm.getJobs();
        long[] vacant = pm.getJobVacancy();
        double staffed = 0, bill = 0;
        for (JobType j : JobType.values()) {
            double n = Math.max(0, jobs[j.ordinal()] - vacant[j.ordinal()]);
            staffed += n;
            bill += n * lm.getWage(j);
        }
        StringBuilder r = new StringBuilder().append(g.getMonth()).append(',').append((int) months);
        for (double x : sum) r.append(',').append(String.format("%.6g", x));
        r.append(String.format(",%.6g,%.6g", g.getPriceIndex().getIndex(), staffed > 0 ? bill / staffed : Double.NaN));
        city.println(r);
        for (Map.Entry<String, double[]> e : byCell.entrySet()) {
            StringBuilder c = new StringBuilder().append(g.getMonth()).append(',').append((int) months)
                    .append(',').append(e.getKey()).append(',').append(cellInfo.get(e.getKey()));
            for (double x : e.getValue()) c.append(',').append(String.format("%.6g", x));
            cells.println(c);
        }
        city.flush();
        cells.flush();
        java.util.Arrays.fill(sum, 0);
        byCell.clear();
        months = 0;
    }
}

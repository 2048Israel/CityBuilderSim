package ham.citybuildersim;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * ST1's probe: ScaleCheck section 6's town (the playtest's city at FREE_FIXTURE_MONTHS, sectors free), built exactly as the check
 * builds it and played ONE MONTH AT A TIME, month for month the trajectory OrderSearchCheck.playtestCity() and playtestRhythm()
 * give it (LongPlaytest.run(g, n) is a loop of n single months, so n steps of run(g, 1) are the same n months).
 * Never the real save folder, never a save: the town lives in a scratch folder of TempDirs.
 *
 * Usage: ST1Probe &lt;months&gt; &lt;out-prefix&gt; writes &lt;out-prefix&gt;.csv (one row a month) and &lt;out-prefix&gt;.tsv (events:
 * month TAB kind TAB text; kinds ADVICE, STAFF, VILLAGE, SCHOOLS, and a sector's key for each word its investors left).
 *
 * It compiles against 0.8.75 and main alike: nothing here names a class or member that is not in both. Before each action it sets
 * ham.citybuildersim.ST1.month to the completed months when that class exists (the switches' clock), by reflection.
 * -Dst1.off=a,b and -Dst1.from=N arm the switches of switches.patch (main only).
 *
 * Compile: javac -d probe-classes -cp target/classes ST1Probe.java (see run.sh's header).
 */
public final class ST1Probe {

    private ST1Probe() { }

    static PrintStream quiet, real;
    static PrintWriter csv, tsv;
    static Game g;
    static Field monthField;
    static boolean headed;
    static long startNanos;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        int months = Integer.parseInt(args[0]);
        String prefix = args[1];
        real = System.out;
        quiet = new PrintStream(new OutputStream() {
            @Override public void write(int b) { }
            @Override public void write(byte[] b, int off, int len) { }
        });
        try {
            monthField = Class.forName("ham.citybuildersim.ST1").getField("month");
        } catch (ReflectiveOperationException e) {
            monthField = null;
        }
        Path out = Paths.get(prefix).toAbsolutePath();
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        csv = new PrintWriter(Files.newBufferedWriter(Paths.get(out + ".csv"), StandardCharsets.UTF_8));
        tsv = new PrintWriter(Files.newBufferedWriter(Paths.get(out + ".tsv"), StandardCharsets.UTF_8));
        startNanos = System.nanoTime();
        Path root = TempDirs.create("st1");
        try {
            play(root, months);
        } finally {
            csv.close();
            tsv.close();
        }
        real.printf("DONE month %d, %,d people, %.1fs; st1.off=%s from %s%n", g.getMonth(), g.getPopulationManager().getPopulation(),
                (System.nanoTime() - startNanos) / 1e9, System.getProperty("st1.off", ""), System.getProperty("st1.from", "0"));
        System.exit(0);
    }

    /* ---------------------------- the town, as ScaleCheck section 6 and playtestCity build it ---------------------------- */

    static void play(Path root, int months) throws IOException {
        GameFiles grown = new GameFiles(root.resolve("grown"), root.resolve("grown-no-legacy"));
        g = new Game(grown, LongPlaytest.founding());
        LongPlaytest.out = quiet;
        System.setOut(quiet);
        g.playOnLiterals();
        g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());

        // OrderSearchCheck.playtestCity(), its opening, step for step:
        setMonth();
        g.run();
        g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
        g.setRolloverMode(LongPlaytest.ROLLOVER);
        g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
        g.setFundDial(LongPlaytest.FUND_DIAL);
        village("House", 40);
        village("Convenience Store", 3);
        village("Mixed Farm", 2);
        afterMonth();   // the founding's own row (month 1), before any month has run
        steps(3);
        village("House", 20);
        steps(4);
        village("Convenience Store", 2);
        village("Construction Depot", 1);
        steps(5);
        advise();
        steps(6);
        ensureSchools();

        // ...and playtestRhythm():
        int stop = 0;
        while (g.getMonth() < months) {
            stop++;
            int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
            steps(Math.min(skip, months - g.getMonth()));
            if (g.getMonth() >= months) break;
            ensureSchools();
            for (int move = 0; move < LongPlaytest.movesPerLook() && g.getMonth() < months; move++) {
                if (advise() == null) break;
                steps(1);
            }
            steps(Math.min(2, months - g.getMonth()));
        }
    }

    /** The switches' clock: the city's completed months, set before every action (a no-op on a tree with no ST1). */
    static void setMonth() {
        if (monthField == null) return;
        try {
            monthField.setInt(null, g.getMonth());
        } catch (IllegalAccessException e) {
            monthField = null;
        }
    }

    static void event(String kind, String text) {
        tsv.println(g.getMonth() + "\t" + kind + "\t" + (text == null ? "null" : text.replace('\t', ' ').replace('\n', ' ')));
    }

    static void village(String name, int quantity) {
        setMonth();
        boolean ok = LongPlaytest.villageBuild(g, name, quantity);
        event("VILLAGE", name + " x" + quantity + " -> " + ok);
    }

    static String advise() {
        setMonth();
        String a = LongPlaytest.advise(g);
        event("ADVICE", a);
        return a;
    }

    /** ensureSchools() is a no-op unless -Dplaytest.schools=true (the fixture does not set it); its orders are told if there are any. */
    static void ensureSchools() {
        setMonth();
        Map<String, Integer> before = new LinkedHashMap<>(LongPlaytest.schoolsOrdered);
        LongPlaytest.ensureSchools(g);
        if (!before.equals(LongPlaytest.schoolsOrdered)) event("SCHOOLS", before + " -> " + LongPlaytest.schoolsOrdered);
    }

    /** n single months, as LongPlaytest.run(g, n) plays them; a month that did not advance ends the burst, as run() returns there. */
    static void steps(int n) {
        for (int i = 0; i < n; i++) {
            int before = g.getMonth();
            setMonth();
            LongPlaytest.run(g, 1);
            if (g.getMonth() == before) {
                event("STALL", "the city could not advance a month");
                return;
            }
            afterMonth();
        }
    }

    static void afterMonth() {
        int m = g.getMonth();
        row();
        for (Map.Entry<String, String> e : g.getLastInvestments().entrySet()) event(e.getKey(), e.getValue());
        event("STAFF", staff());
        if (m % 100 == 0) {
            real.printf("  month %d, %,d people, treasury %.4g, %.0fs%n", m, g.getPopulationManager().getPopulation(), g.getCash(),
                    (System.nanoTime() - startNanos) / 1e9);
            csv.flush();
            tsv.flush();
        }
    }

    /** XS1's STAFF line: the posts withheld, the unemployed, and for each wage band its own workers, posts, supply and spare. */
    static String staff() {
        try {
            PopulationManager pm = g.getPopulationManager();
            double[] posts = pm.staffablePostsByBand(), own = pm.workforceByBand();
            double[] supply = posts == null ? null : pm.supplyByBand(posts);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.ROOT, "withheld %d unemployed %d workforce %d jobs %d offered %.3f |",
                    g.getBuildingManager().getPostsWithheld(), pm.getUnemployed(), pm.getWorkforce(), pm.getTotalJobs(),
                    g.getSectors().construction().getPostsOfferedShare()));
            for (int b = 0; posts != null && b < posts.length; b++) {
                sb.append(String.format(Locale.ROOT, " %s own %.0f posts %.0f supply %.0f spare %.0f;", WageBand.values()[b], own[b],
                        posts[b], supply[b], supply[b] - posts[b]));
            }
            for (JobType job : JobType.values()) {
                long w = g.getBuildingManager().getPostsWithheld(job);
                if (w > 0) sb.append(" withheld-").append(job).append(' ').append(w);
            }
            return sb.toString();
        } catch (RuntimeException | LinkageError e) {
            return "n/a " + e;
        }
    }

    /* ---------------------------------------------------- the rows ---------------------------------------------------- */

    /** The civic buildings counted by name (buildings.json's names). */
    static final String[] COUNTED = { "Police Station", "Police Headquarters", "Gravel Road", "Paved Road", "Elevated Highway",
            "Walk-in Clinic", "Community Health Centre", "General Hospital", "Elementary School", "Middle School", "High School",
            "Community College", "University", "House" };

    static final class Row {
        final List<String> names = new ArrayList<>();
        final List<Double> values = new ArrayList<>();
        void put(String name, Callable<? extends Number> f) {
            double v;
            try {
                Number n = f.call();
                v = n == null ? Double.NaN : n.doubleValue();
            } catch (Throwable t) {
                v = Double.NaN;
            }
            names.add(name);
            values.add(v);
        }
        void put(String name, double v) { names.add(name); values.add(v); }
    }

    static int count(String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        return t == null ? 0 : g.getBuildingManager().getQuantity(t.getId());
    }

    static void row() {
        Row r = new Row();
        PopulationManager pm = g.getPopulationManager();
        BuildingManager bm = g.getBuildingManager();
        EconomyManager em = g.getEconomyManager();
        NationalAccounts na = em.getNationalAccounts();
        Migration mig = g.getMigration();
        OutwardInvestment out = g.getOutwardInvestment();
        LabourMarket lm = g.getLabourMarket();

        r.put("month", g::getMonth);
        r.put("population", pm::getPopulation);
        r.put("capacity", g::getHouseholdCapacity);
        r.put("homes", bm::getTotalHomes);
        // The treasury: its cash, its debt, and its month (the national accounts' revenue and spending, the lines that pay the civic buildings).
        r.put("cash", g::getCash);
        r.put("debt", () -> g.getDebtManager().getAllPrincipal());
        r.put("rev", na::getTotalRevenue);
        r.put("spend", na::getTotalExpenses);
        r.put("balance", na::getBalance);
        r.put("tax", em::getTaxIncome);
        r.put("interest_out", em::getInterestAccrued);
        r.put("capital", na::getCapitalSpending);
        r.put("bill_care", em::getHealthcareBill);
        r.put("bill_schools", em::getEducationBill);
        r.put("bill_safety", em::getSafetyBill);
        r.put("bill_transit", em::getTransitBill);
        r.put("gdp", em::getMonthGdp);
        // The businesses: their tills, and their dollars abroad with this month's coupon.
        r.put("biz_cash", () -> {
            double t = 0;
            for (Sector s : g.getSectors().all()) t += s.getCash();
            return t;
        });
        r.put("cash_construction", () -> g.getSectors().construction().getCash());
        r.put("cash_realestate", () -> g.getSectors().realEstate().getCash());
        r.put("biz_usd", out::totalUsd);
        r.put("biz_abroad_local", out::totalLocalValue);
        r.put("coupon", () -> out.getInterestThisMonth());
        // Labour.
        r.put("unemployed", pm::getUnemployed);
        r.put("workforce", pm::getWorkforce);
        r.put("jobs", pm::getTotalJobs);
        r.put("unemp_rate", () -> {
            double w = pm.getWorkforce();
            return w > 0 ? pm.getUnemployed() / w : Double.NaN;
        });
        r.put("avg_wage", () -> {
            double f = pm.getJobsFilled();
            return f > 0 ? pm.getTotalWage() / f : Double.NaN;
        });
        for (JobType job : JobType.values()) r.put("wage_" + job, () -> lm.getWage(job));
        // People coming and going, the rent, and the affordability pull (ScaleCheck's Original.calm() reads it: 1 is no migrant priced out).
        r.put("arrivals", mig::getLastArrivals);
        r.put("departures", mig::getLastDepartures);
        r.put("avg_rent", () -> g.getSectors().realEstate().getAverageRentPaid());
        r.put("afford_pull", mig::getLastAffordabilityPull);
        r.put("price_index", () -> g.getPriceIndex().getIndex());
        r.put("inflation", () -> g.getPriceIndex().inflation());
        r.put("policy_rate", () -> g.getDebtManager().getPolicyRate());
        // Building: the posts the builders laid off, the order book in months of their work, and what stands.
        r.put("posts_withheld", () -> bm.getPostsWithheld());
        r.put("posts_offered", () -> g.getSectors().construction().getPostsOfferedShare());
        r.put("backlog_months", () -> {
            double forSites = g.getBuildingOutputAtEveryPost(), remaining = bm.getRemainingConstructionPoints();
            return forSites > 0 ? remaining / forSites : remaining > 0 ? Double.POSITIVE_INFINITY : 0;
        });
        for (String name : COUNTED) r.put("n_" + name.replace(' ', '_'), () -> count(name));
        r.put("n_schools", () -> count("Elementary School") + count("Middle School") + count("High School")
                + count("Community College") + count("University"));
        r.put("n_buildings", () -> {
            long t = 0;
            for (BuildingsTemplate bt : bm.getTemplates()) t += bm.getQuantity(bt.getId());
            return t;
        });
        r.put("audit_findings", () -> LongPlaytest.findings.size());

        if (!headed) {
            csv.println(String.join(",", r.names));
            headed = true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r.values.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(fmt(r.values.get(i)));
        }
        csv.println(sb);
    }

    static String fmt(double v) {
        if (Double.isNaN(v)) return "NaN";
        if (Double.isInfinite(v)) return v > 0 ? "Inf" : "-Inf";
        if (v == Math.rint(v) && Math.abs(v) < 1e15) return Long.toString((long) v);
        return Double.toString(v);
    }
}

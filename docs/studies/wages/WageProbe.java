package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * The wages study's probe (docs/studies/spec-wages.md): one seeded city founded
 * and played as the ensemble's AUTO2 plays it (tools/ensemble/Ensemble.java,
 * foundAndPlay), one CSV row a month of the whole wage ladder - every job's
 * wage and target, every band's posts, supply, multiple and pin, who is paid
 * the floor, the median wage, what living alone costs, the fees and the
 * migration.
 *
 * WHY A DRIVER OF ITS OWN: the ensemble's columns are the monetary model's;
 * the study needs the ladder's, and the floor moved at a month of its choosing.
 * Not game code: compiled against target/classes by run.sh, which may put a
 * patched LabourMarket (real-minimum.patch) ahead of it on the classpath.
 *
 *   java ... ham.citybuildersim.WageProbe <seed> <months> <out.csv>
 *   -Dwages.floor=1.0   the floor as a multiple of the founding unskilled rung (founding money)
 *   -Dwages.floorAt=0   the month it is set (0: before the first month)
 *
 * The city lives in a TempDirs folder, never the save folder (CLAUDE.md).
 */
public final class WageProbe {

    static final PrintStream real = System.out;
    static final PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static PrintWriter csv;
    static boolean header;
    static int endMonth;
    static boolean stuck;
    static double floorMultiple;
    static int floorAt;
    static boolean floorSet;
    static int seed;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        seed = Integer.parseInt(args[0]);
        endMonth = Integer.parseInt(args[1]);
        Path out = Paths.get(args[2]);
        floorMultiple = Double.parseDouble(System.getProperty("wages.floor", "1.0"));
        floorAt = Integer.getInteger("wages.floorAt", 0);
        System.setProperty("playtest.seed", Integer.toString(seed));
        try {
            java.lang.reflect.Field f = LongPlaytest.class.getDeclaredField("out");
            f.setAccessible(true);
            f.set(null, quiet);
        } catch (ReflectiveOperationException | RuntimeException e) { /* nothing to quiet */ }

        Path root = TempDirs.create("wageprobe");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        long started = System.currentTimeMillis();
        Game g;
        System.setOut(quiet);
        try {
            g = new Game(files, LongPlaytest.founding());
            g.run();
        } finally {
            System.setOut(real);
        }
        String threw = null;
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            csv = w;
            try {
                foundAndPlay(g);
            } catch (Throwable t) {
                threw = t.toString();
            } finally {
                System.setOut(real);
            }
        }
        real.printf("DONE seed %d floor %.3f@%d months %d of %d in %.0fs%s%n", seed, floorMultiple, floorAt,
                g.getMonth(), endMonth, (System.currentTimeMillis() - started) / 1000.0,
                threw == null ? (stuck ? " (stuck)" : "") : " THREW " + threw);
    }

    /* The founding and the rhythm: Ensemble.foundAndPlay's, AUTO2, line for line. */
    static void foundAndPlay(Game g) {
        System.setOut(quiet);
        g.getDebtManager().setInflationTarget(.02);
        g.getDebtManager().setAutopilot(true);
        g.setRolloverMode(LongPlaytest.ROLLOVER);
        g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
        g.setFundDial(LongPlaytest.FUND_DIAL);
        LongPlaytest.seedMarket(g, seed);
        if (LongPlaytest.FOUNDING == Founding.Preset.INSANE) LongPlaytest.borrowForTheVillage(g);
        LongPlaytest.villageBuild(g, "House", 40 + seed % 4);
        LongPlaytest.villageBuild(g, "Convenience Store", 3);
        LongPlaytest.villageBuild(g, "Mixed Farm", 2);
        run(g, 3 + (seed / 4) % 3);
        LongPlaytest.villageBuild(g, "House", 20 + (seed / 12) % 3);
        run(g, 4);
        LongPlaytest.villageBuild(g, "Convenience Store", 2);
        LongPlaytest.villageBuild(g, "Construction Depot", 1);
        run(g, 5);
        LongPlaytest.advise(g);
        run(g, 6);
        LongPlaytest.ensureSchools(g);
        int stop = 0;
        while (g.getMonth() < endMonth && !stuck) {
            stop++;
            int skip = switch (stop % 6) {
                case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120;
            };
            skip = Math.min(skip, LongPlaytest.longestSkip());
            run(g, skip);
            if (g.getMonth() >= endMonth || stuck) break;
            LongPlaytest.ensureSchools(g);
            for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                if (LongPlaytest.advise(g) == null) break;
                run(g, 1);
            }
            run(g, 2);
            if (stop % 11 == 0) {
                TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                double income = tax.getIncomeTaxRate();
                tax.setIncomeTaxRate(income > .25 ? .12 : income + .06);
            }
            if (stop % 17 == 0) {
                TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                double prop = tax.getPropertyTaxRate();
                tax.setPropertyTaxRate(prop > .03 ? .01 : prop + .01);
            }
            if (stop % 19 == 0) {
                DebtManager m = g.getDebtManager();
                if (m.foreignWindowOpen() && m.foreignRate() < m.getRate()) {
                    double ask = Math.max(5_000, g.getEconomyManager().getMonthGdp() * .5
                            / Math.max(.01, LongPlaytest.fxRate(g)));
                    if (LongPlaytest.BORROW_AT_HOME) {
                        g.handleLongBondLogic(ask * Math.max(.01, LongPlaytest.fxRate(g)), 20, 100);
                    } else {
                        g.handleForeignLogic("Term", ask, 20, 100, false);
                    }
                }
            }
            if (stop % 13 == 0) {
                LandParcel spare = g.getLandManager().getMarket().bestValue();
                if (spare != null && spare.localPrice(g.getForeignAccounts().getRate()) < g.getCash() * .1) {
                    g.buyLandParcel(spare.getId());
                }
            }
            if (stop % 19 == 0) {
                BuildingsTemplate house = LongPlaytest.template(g, "House");
                if (house != null && g.getBuildingManager().getQuantity(house.getId()) > 60) {
                    g.getBuildingManager().retire(house, 10);
                }
            }
        }
    }

    static void run(Game g, int months) {
        for (int i = 0; i < months && !stuck && g.getMonth() < endMonth; i++) {
            if (!floorSet && g.getMonth() >= floorAt) {
                g.getLabourMarket().setMinimumWage(LabourMarket.DEFAULT_MINIMUM_WAGE * floorMultiple);
                floorSet = true;
            }
            if (Boolean.getBoolean("wages.wanted")) {
                // migration-target.patch's draw: last month's refused posts, by reflection so the unpatched tree compiles.
                try {
                    java.lang.reflect.Field w = Migration.class.getDeclaredField("wantedDraw");
                    w.setAccessible(true);
                    w.set(null, g.getWantedPosts().getTotal());
                } catch (ReflectiveOperationException e) { /* not patched */ }
            }
            int before = g.getMonth();
            LongPlaytest.run(g, 1);
            if (g.getMonth() == before) { stuck = true; return; }
            write(g);
        }
    }

    static final String[] BAND = {"none", "dip", "col", "uni"};
    static final String[] JOB = {"nodip", "dip", "colh", "colb", "cole", "doc", "law", "fin", "sci", "tech", "pol"};

    static void write(Game g) {
        LabourMarket lm = g.getLabourMarket();
        PopulationManager pm = g.getPopulationManager();
        LinkedHashMap<String, Object> r = new LinkedHashMap<>();
        double floor = lm.cashMinimumWage();
        r.put("m", (double) g.getMonth());
        r.put("pop", (double) pm.getPopulation());
        r.put("workforce", (double) pm.getWorkforceForSave());
        r.put("unemp", pm.getUnemploymentRate());
        r.put("idx", g.getPriceIndex().getIndex());
        r.put("col", lm.getCostOfLiving());
        r.put("floor", floor);
        r.put("floor_real", lm.getMinimumWage());
        r.put("unit", LabourMarket.DEFAULT_MINIMUM_WAGE * lm.getCostOfLiving());

        double[] posts = pm.staffablePostsByBand();
        double[] supply = pm.supplyByBand();
        double[] own = pm.workforceByBand();
        for (WageBand b : WageBand.values()) {
            int i = b.ordinal();
            r.put("posts_" + BAND[i], posts[i]);
            r.put("supply_" + BAND[i], supply[i]);
            r.put("own_" + BAND[i], own[i]);
            r.put("mult_" + BAND[i], lm.getBandMultiple(b));
            r.put("tight_" + BAND[i], lm.getTightness(b));
            r.put("pinned_" + BAND[i], lm.isPinned(b) ? 1.0 : 0.0);
            r.put("surplus_" + BAND[i], pm.surplusInBand(b));
        }

        long[] jobs = pm.getJobs();
        long[] vacant = pm.getJobVacancy();
        double atFloor = 0, staffedAll = 0, bill = 0;
        List<double[]> paid = new ArrayList<>();
        for (JobType j : JobType.values()) {
            int i = j.ordinal();
            double wage = lm.getWage(j);
            double staffed = Math.max(0, jobs[i] - vacant[i]);
            r.put("w_" + JOB[i], wage);
            r.put("t_" + JOB[i], lm.targetWageAt(j, lm.getMinimumWage()));
            r.put("n_" + JOB[i], staffed);
            r.put("p_" + JOB[i], (double) jobs[i]);
            r.put("lic_" + JOB[i], lm.getLicenceMultiple(j));
            staffedAll += staffed;
            bill += staffed * wage;
            if (wage <= floor * 1.02) atFloor += staffed;
            paid.add(new double[] {wage, staffed});
        }
        paid.sort((a, b) -> Double.compare(a[0], b[0]));
        double half = staffedAll / 2, run = 0, median = Double.NaN;
        for (double[] p : paid) {
            run += p[1];
            if (run >= half && staffedAll > 0) { median = p[0]; break; }
        }
        r.put("median", median);
        r.put("mean", staffedAll > 0 ? bill / staffedAll : Double.NaN);
        r.put("staffed", staffedAll);
        r.put("at_floor", atFloor);

        HouseholdAccounts h = g.getHouseholds();
        r.put("rent_door", h.rentPerHousehold());
        r.put("shop_head", h.shoppingPerHead());
        r.put("fees_head", h.feesPerHead());
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        r.put("payslip_none", tax.payslipShare(WageBand.NONE));
        r.put("loan_rate", tax.getStudentLoanRate());
        r.put("policy_rate", g.getDebtManager().getPolicyRate());
        Education ed = g.getEducation();
        r.put("subsidy", ed.getTuitionSubsidy());
        for (EducationType t : new EducationType[] {EducationType.HIGH, EducationType.COLLEGE,
                EducationType.UNIVERSITY, EducationType.MEDICAL, EducationType.LAW,
                EducationType.BUSINESS, EducationType.ENGINEERING}) {
            String k = t.name().toLowerCase(Locale.ROOT);
            r.put("fee_" + k, ed.feeFor(t));
            r.put("enr_" + k, ed.getEnrolled(t));
        }
        r.put("diplomas", ed.getNewDiplomas());
        Migration mig = g.getMigration();
        r.put("arrivals", mig.getLastArrivals());
        r.put("departures", mig.getLastDepartures());
        r.put("work_departures", mig.getLastWorkDepartures());
        r.put("mig_target", mig.getLastTarget());
        r.put("mig_jobdraw", mig.getLastJobDraw());
        r.put("mig_homedraw", mig.getLastHomeDraw());
        r.put("pull_senior", mig.getLastSeniorPull());
        r.put("pull_afford", mig.getLastAffordabilityPull());
        r.put("pull_crime", mig.getLastCrimePull());
        r.put("rpj", mig.getLastResidentsPerJob());
        r.put("posts_all", (double) pm.getTotalJobs());
        r.put("studying", pm.getStudyingTotal());

        if (!header) {
            csv.println(String.join(",", r.keySet()));
            header = true;
        }
        StringBuilder b = new StringBuilder();
        for (Object v : r.values()) {
            if (b.length() > 0) b.append(',');
            double d = (Double) v;
            if (!Double.isNaN(d) && !Double.isInfinite(d)) b.append(String.format(Locale.ROOT, "%.7g", d));
        }
        csv.println(b);
        if (g.getMonth() % 240 == 0) {
            real.printf("  seed %d month %d pop %d unemp %.1f%%%n", seed, g.getMonth(), pm.getPopulation(),
                    100 * pm.getUnemploymentRate());
        }
    }
}

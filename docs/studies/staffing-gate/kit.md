# The staffing-gate study's kit

The prototypes of [the staffing-gate study](../spec-staffing-gate.md), kept here as text so the study's merge
changes only Markdown: a file to compile is copied out of its block below. None of it is game code and none of
it is in `src/`.

**To run it** (from the checkout's root, after `mvn -q compile` with `target/classpath.txt` written, which
`batch-check.sh` does):

1. Make a folder outside the repository, the kit, with `GateProbe.java` in it and `src/ham/citybuildersim/`
   holding copies of `Sector.java` and `Migration.java` from `src/main/java/ham/citybuildersim/`.
2. Apply the wages study's [`migration-target.patch`](../wages/migration-target.patch), then `gate.patch` below,
   to those copies (`sed 's#src/main/java/##g' X.patch | patch -p1` from the kit's `src/`).
3. `run.sh KIT OUT MONTHS SEEDS VARIANT...` (below) plays each variant on each seed into `OUT`. The runs in the
   study: `run.sh KIT OUT 2400 "0 1 ... 15"` with the variants and properties in [out.md](out.md)'s list, three at
   a time. Then `summarize.py OUT` prints table 1 from the CSVs, and `busts.py OUT` prints table 2.
4. The instrument (who the gate refuses, and when each large building first stands) is two more diffs on top of
   the kit, `instrument.patch` and `probe-instrument.patch`, run by `held.sh`: `held.sh OUT held S 480` and
   `held.sh OUT held12 S 480 -Dgate.draw=12` for seeds 0-3 (table 3, `heldsum.py OUT held`), and `first`, `first12`
   and `first0` (`-Dgate.min=0`) for seeds 0-7 to month 1200 (table 4, `firsts.py OUT first first12 first0`).

Every city is founded in a `TempDirs` folder by `GateProbe`, never the save folder. Each patch is neutral until
a property asks for it: with none set, the patched build's CSV is the unpatched build's, byte for byte, but for
the draw's two columns (measured: seed 0 to month 240; the instrumented build to month 480).

## The gate, as a prototype

### gate.patch: Sector's gate takes -Dgate.min and -Dgate.draw; Migration.drawFor() (on top of migration-target.patch)

```diff
--- a/src/main/java/ham/citybuildersim/Sector.java
+++ b/src/main/java/ham/citybuildersim/Sector.java
@@ -261,6 +261,15 @@
        ======================================================================= */
     public static final double MIN_STAFFABLE_TO_ORDER = .80;
 
+    /*
+     * STUDY PROTOTYPE (docs/studies/spec-staffing-gate.md), not game code.
+     * -Dgate.min=.6 moves the share passes() asks for; -Dgate.draw=12 counts,
+     * beside the spare workers, those migration would bring for the order's
+     * posts within that many months of its opening. Neutral unset.
+     */
+    static final double GATE_MIN = Double.parseDouble(System.getProperty("gate.min", Double.toString(MIN_STAFFABLE_TO_ORDER)));
+    static final int GATE_DRAW = Integer.getInteger("gate.draw", 0);
+
     /**
      * The share of a building's posts this city could actually fill today.
      *
@@ -418,7 +427,7 @@
          * can fill. "The 20% it allows can't be jobs nobody can fill."
          */
         public boolean passes() {
-            return unfillable == null && share >= MIN_STAFFABLE_TO_ORDER;
+            return unfillable == null && share >= GATE_MIN;
         }
 
         /** Why not, as the advisor's hold reason, for a building of this name. */
@@ -602,6 +611,21 @@
             comeFor[b] = migration != null ? migration.admits(band) : band.arrivalCeiling() > 0;
             if (wanted[b] > 0) fillable += Math.min(wanted[b], Math.max(0, spare[b]));
         }
+        // STUDY PROTOTYPE (docs/studies/spec-staffing-gate.md): beside the spare workers, the workers migration would
+        // bring for this order's posts within GATE_DRAW months of its opening (Migration.drawFor()), each band's drawn
+        // workers taking its own posts first and what is left of them stepping down to the bands below.
+        if (GATE_DRAW > 0 && migration != null) {
+            double[] draw = migration.drawFor(wanted, game.getLabourMarket(), people, GATE_DRAW);
+            double carry = 0;
+            fillable = 0;
+            for (int b = bands - 1; b >= 0; b--) {
+                double fromSpare = Math.min(wanted[b], Math.max(0, spare[b]));
+                carry += draw[b];
+                double fromDraw = Math.max(0, Math.min(wanted[b] - fromSpare, carry));
+                carry -= fromDraw;
+                fillable += fromSpare + fromDraw;
+            }
+        }
         // The lowest band nobody can fill is the one named: it is the one the fill runs out of first.
         // ...and a share within rounding of nothing is nothing (SHARE_ROUNDING).
         double share = fillable / total;
--- a/src/main/java/ham/citybuildersim/Migration.java
+++ b/src/main/java/ham/citybuildersim/Migration.java
@@ -1273,6 +1273,75 @@
         }
     }
 
+    /** STUDY PROTOTYPE: -Dgate.drawAll=true counts the homes' quarter of the draw too (homes built as the people come). */
+    static final boolean DRAW_ALL = Boolean.getBoolean("gate.drawAll");
+    /** STUDY PROTOTYPE: the last order's draw, for the probe. */
+    static double lastDrawWorkers, lastDrawPosts;
+
+    /**
+     * STUDY PROTOTYPE (docs/studies/spec-staffing-gate.md), not game code: the
+     * workers per band migration would bring for an order's posts within
+     * {@code window} months of its opening, for Sector.staffing()'s gate.
+     * The posts raise the target by the jobs' draw (JOB_WEIGHT x posts x last
+     * month's residents a post x the pulls on the jobs' draw; all of it with
+     * DRAW_ALL); a gap closes at ARRIVAL_RATE x last month's crowding a month,
+     * so {@code window} months close 1 - (1 - that)^window of it; the
+     * migration patch's brake cuts it as it cuts arrivals; the city's workers
+     * per resident turn people into workers; and they split by band as
+     * composeArrivals() splits a month's arrivals, with the order's posts
+     * added to each band's opportunity.
+     */
+    public double[] drawFor(double[] wanted, LabourMarket market, PopulationManager people, int window) {
+        int n = WageBand.values().length;
+        double[] out = new double[n];
+        if (people == null || window <= 0 || wanted == null) return out;
+        double posts = 0;
+        for (double w : wanted) posts += Math.max(0, w);
+        long pop = people.getPopulation();
+        if (posts <= 0 || pop <= 0) return out;
+        double jobPull = PULLS_ON_HOMES ? Math.min(1, lastAffordabilityPull * lastCrimePull)
+                : lastSeniorPull * lastAffordabilityPull * lastCrimePull;
+        double drawn = (DRAW_ALL ? 1 : JOB_WEIGHT) * posts * lastResidentsPerJob * jobPull;
+        double monthly = Math.max(0, Math.min(1, ARRIVAL_RATE * lastCrowding));
+        double close = 1 - Math.pow(1 - monthly, window);
+        double brake = 1;
+        if (BRAKE_HI > BRAKE_LO) {
+            double u = people.getUnemploymentRate();
+            brake = Math.max(0, Math.min(1, (BRAKE_HI - u) / (BRAKE_HI - BRAKE_LO)));
+        }
+        double workers = drawn * close * brake * people.getWorkforceForSave() / (double) pop;
+
+        double[] postsNow = people.staffablePostsByBand();
+        double[] here = people.supplyByBand();
+        double[] weight = new double[n];
+        double total = 0;
+        for (WageBand band : WageBand.values()) {
+            int b = band.ordinal();
+            double open = (postsNow == null ? 0 : postsNow[b]) + (b < wanted.length ? Math.max(0, wanted[b]) : 0);
+            double opportunity = opportunity(open, here == null ? 0 : here[b]);
+            double pull = band == WageBand.DIPLOMA ? 1 : reach(market == null ? 1 : market.bandPremium(band));
+            weight[b] = !admits(band) ? 0 : yearMix == null
+                    ? band.arrivalCeiling() * pull * opportunity
+                    : (yearMix.ordinary(band) + yearMix.ceiling(band) * pull) * opportunity;
+            total += weight[b];
+        }
+        int uni = WageBand.UNIVERSITY.ordinal();
+        for (EducationType type : EducationType.values()) {
+            if (!type.isProfessional()) continue;
+            JobType job = type.licenses();
+            if (job == null) continue;
+            double pull = admits(WageBand.UNIVERSITY) ? reach(market == null ? 1 : market.licencePremium(job)) : 0;
+            double w = (yearMix == null ? WageBand.UNIVERSITY.arrivalCeiling() : yearMix.university())
+                    * type.worldLicenceShare() * pull;
+            weight[uni] += w;
+            total += w;
+        }
+        for (int b = 0; b < n; b++) out[b] = total > 0 ? workers * weight[b] / total : 0;
+        lastDrawWorkers = workers;
+        lastDrawPosts = posts;
+        return out;
+    }
+
     /* ------------------------------- saving ------------------------------- */
 
     /*
```

## The probe and the runs

### GateProbe.java: one AUTO2 city, one CSV row a month (the wages study's WageProbe, without its floor, with the gate's columns)

```java
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
 * The staffing-gate study's probe (docs/studies/spec-staffing-gate.md): the
 * wages study's WageProbe (docs/studies/spec-wages.md) without its floor, plus
 * the gate's columns: one seeded city founded and played as the ensemble's
 * AUTO2 plays it (tools/ensemble/Ensemble.java, foundAndPlay), one CSV row a
 * month of the ladder, the migration, the staffing holds, the vacancies, the
 * unhoused and the output.
 *
 * Not game code: compiled by run.sh against a snapshot of target/classes, with
 * the patched Sector and Migration ahead of it for the gate's variants.
 *
 *   java ... ham.citybuildersim.GateProbe <seed> <months> <out.csv>
 *
 * The city lives in a TempDirs folder, never the save folder (CLAUDE.md).
 */
public final class GateProbe {

    static final PrintStream real = System.out;
    static final PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static PrintWriter csv;
    static boolean header;
    static int endMonth;
    static boolean stuck;
    static int seed;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        seed = Integer.parseInt(args[0]);
        endMonth = Integer.parseInt(args[1]);
        Path out = Paths.get(args[2]);
        System.setProperty("playtest.seed", Integer.toString(seed));
        try {
            java.lang.reflect.Field f = LongPlaytest.class.getDeclaredField("out");
            f.setAccessible(true);
            f.set(null, quiet);
        } catch (ReflectiveOperationException | RuntimeException e) { /* nothing to quiet */ }

        Path root = TempDirs.create("gateprobe");
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
        real.printf("DONE seed %d months %d of %d in %.0fs%s%n", seed,
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

    /** Migration's prototype draw (the gate patch), by reflection so the unpatched tree runs the probe too; NaN without it. */
    static double drawField(String name) {
        try {
            java.lang.reflect.Field f = Migration.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.getDouble(null);
        } catch (ReflectiveOperationException e) {
            return Double.NaN;
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

        // The gate's columns (this study).
        WantedPosts wp = g.getWantedPosts();
        r.put("holds", (double) wp.getHolds());
        r.put("asked", (double) wp.getAsked());
        r.put("crews_holds", (double) wp.getCrewsHolds());
        double vac = 0;
        for (long v : vacant) vac += v;
        r.put("vacant", vac);
        r.put("unhoused", g.unhousedShareOfCity());
        r.put("gdp", g.getEconomyManager().getMonthGdp());
        r.put("draw_workers", drawField("lastDrawWorkers"));
        r.put("draw_posts", drawField("lastDrawPosts"));
        double income = 0, payroll = 0, losing = 0;
        for (Sector sec : g.getSectors().all()) {
            income += sec.getNetIncome();
            payroll += sec.getPayroll();
            if (sec.getPostsOffered() > 0 && sec.getNetIncome() < 0) losing++;
        }
        r.put("firm_income", income);
        r.put("firm_payroll", payroll);
        r.put("sectors_losing", losing);

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
```

### run.sh: the variants on the seeds

```bash
#!/usr/bin/env bash
# The staffing-gate study's runs (docs/studies/spec-staffing-gate.md).
#
#   run.sh KIT OUT MONTHS SEEDS VARIANT...
#
# KIT is the folder holding GateProbe.java and src/ (Sector.java, Migration.java
# with the gate patch and the wages study's migration-target.patch applied); OUT
# a folder outside the repository; MONTHS the months each city plays; SEEDS a
# list such as "0 1 2 3"; each VARIANT is  name:kind[:key=value,key=value]
# with kind "today" (main as it is) or "proto" (the patched Sector and
# Migration, each neutral until a property asks), and the properties passed as
# -Dkey=value (gate.min=.6, gate.draw=12, wages.slack=.05,
# wages.brake=.06/.12, wages.leave=true). One CSV per run:
# OUT/<name>-s<seed>.csv. JOBS runs at once (default 3). Run from the
# checkout's root after `mvn -q compile` with target/classpath.txt written.
#
# Every city is founded in a TempDirs folder by GateProbe, never the save folder.
set -euo pipefail
KIT=$1; OUT=$2; MONTHS=$3; SEEDS=$4; shift 4
JOBS=${JOBS:-3}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
mkdir -p "$OUT/classes" "$OUT/today" "$OUT/proto" "$OUT/tmp"
DEPS=$(cat target/classpath.txt)
# A snapshot, so compiling the tree while the runs go cannot reach them.
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
javac -nowarn -d "$OUT/today" -cp "$CP" "$KIT/GateProbe.java"
javac -nowarn -d "$OUT/proto" -cp "$CP" "$KIT/src/ham/citybuildersim/Sector.java" \
    "$KIT/src/ham/citybuildersim/Migration.java" "$KIT/GateProbe.java"

one() {
    IFS=: read -r name kind extra <<< "$1"
    local seed=$2 props=""
    for kv in ${extra//,/ }; do props="$props -D$kv"; done
    java -Xmx2g "-Djava.io.tmpdir=$OUT/tmp" $props -cp "$OUT/$kind$SEP$CP" \
        ham.citybuildersim.GateProbe "$seed" "$MONTHS" "$OUT/$name-s$seed.csv" > "$OUT/$name-s$seed.log" 2>&1
    tail -1 "$OUT/$name-s$seed.log" | sed "s/^/$name /"
}
export -f one; export OUT MONTHS SEP CP
for v in "$@"; do for s in $SEEDS; do echo "$v $s"; done; done \
    | xargs -P "$JOBS" -L 1 bash -c 'one "$0" "$1"'
```

### summarize.py: table 1

```python
"""The staffing-gate study's tables: summarize.py OUT [BASE] - one markdown row a
variant, medians over its seeds. People and output are each seed's ratio to BASE
(default "today") on the same seed: the median, how many seeds are above their twin, and the seeds' range."""
import csv, glob, math, os, statistics, sys
from collections import defaultdict

out = sys.argv[1]
base = sys.argv[2] if len(sys.argv) > 2 else "today"

def load(path):
    rows = {}
    with open(path, newline="") as f:
        for r in csv.DictReader(f):
            m = int(float(r["m"]))
            rows[m] = {k: (float(v) if v not in ("", None) else math.nan) for k, v in r.items()}
    return rows

runs = defaultdict(dict)
for path in glob.glob(os.path.join(out, "*-s*.csv")):
    name, seed = os.path.basename(path)[:-4].rsplit("-s", 1)
    runs[name][int(seed)] = load(path)

def mean(rows, key, lo, hi=10**9):
    v = [r[key] for m, r in rows.items() if lo <= m <= hi and not math.isnan(r.get(key, math.nan))]
    return sum(v) / len(v) if v else math.nan

def at(rows, key, m):
    r = rows.get(m)
    return r[key] if r else math.nan

def med(v):
    v = [x for x in v if not math.isnan(x)]
    return statistics.median(v) if v else math.nan

def ratio_cell(name, key_fn, m):
    rs = []
    for seed, rows in runs[name].items():
        b = runs[base].get(seed)
        if not b:
            continue
        x, y = key_fn(rows, m), key_fn(b, m)
        if not math.isnan(x) and not math.isnan(y) and y > 0:
            rs.append(x / y - 1)
    if not rs:
        return "-"
    up = sum(1 for r in rs if r > .005)
    return f"{100*med(rs):+.0f}% ({up} of {len(rs)} up; {100*min(rs):+.0f} to {100*max(rs):+.0f})"

def pop(rows, m):
    return at(rows, "pop", m)

def real_gdp_head(rows, m):
    # a year's output per head at the month's prices over the index, averaged over the year to m
    v = []
    for k in range(m - 11, m + 1):
        r = rows.get(k)
        if r and r["pop"] > 0 and r["idx"] > 0:
            v.append(r["gdp"] / r["idx"] / r["pop"])
    return sum(v) / len(v) if v else math.nan

def infl(rows, lo, hi):
    a, b = rows.get(lo), rows.get(hi)
    if not a or not b or a["idx"] <= 0:
        return math.nan
    return 100 * ((b["idx"] / a["idx"]) ** (12 / (hi - lo)) - 1)

# Only whole runs: one still being written (or that stopped early) is left out and its seed with it.
full = max(max(rows) for name in runs for rows in runs[name].values())
for name in list(runs):
    runs[name] = {s: r for s, r in runs[name].items() if max(r) == full}
    if not runs[name]:
        del runs[name]
order = sorted(runs, key=lambda n: (n != base, n))
end = full
print(f"{out}: seeds per variant {', '.join(f'{n} {len(runs[n])}' for n in order)}; months to {end}")
print()
print("| variant | out of work, m240+ | m1200+ | young: m24-240 | workers a post | posts unfilled | holds a month | unhoused | people m240 | m480 | m1200 | m2400 | output a head m2400 | sectors losing | inflation |")
print("|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|")
for name in order:
    seeds = runs[name].values()
    u = med([mean(r, "unemp", 240) for r in seeds])
    u12 = med([mean(r, "unemp", 1200) for r in seeds])
    uy = med([mean(r, "unemp", 24, 240) for r in seeds])
    wpp = med([mean({m: {"x": x["workforce"] / x["posts_all"] if x["posts_all"] > 0 else math.nan} for m, x in r.items()}, "x", 240) for r in seeds])
    vac = med([mean({m: {"x": x["vacant"] / x["posts_all"] if x["posts_all"] > 0 else math.nan} for m, x in r.items()}, "x", 240) for r in seeds])
    holds = med([mean(r, "holds", 240) for r in seeds])
    unh = med([mean(r, "unhoused", 240) for r in seeds])
    los = med([mean(r, "sectors_losing", 240) for r in seeds])
    inf = med([infl(r, 240, end) for r in seeds])
    cells = [f"{100*u:.1f}%", f"{100*u12:.1f}%", f"{100*uy:.1f}%", f"{wpp:.2f}", f"{100*vac:.1f}%", f"{holds:.1f}", f"{100*unh:.2f}%"]
    for m in (240, 480, 1200, end):
        if name == base:
            cells.append(f"{med([pop(r, m) for r in seeds])/1000:.1f}k")
        else:
            cells.append(ratio_cell(name, pop, m))
    if name == base:
        cells.append(f"{med([real_gdp_head(r, end) for r in seeds]):.3g}")
    else:
        cells.append(ratio_cell(name, real_gdp_head, end))
    cells += [f"{los:.1f}", f"{inf:.2f}%"]
    print(f"| {name} | " + " | ".join(cells) + " |")
```

### busts.py: table 2

```python
"""busts.py OUT: (the posts' falls count each fall from a high of 500 posts or more, from month 24, once until a new high)
 each variant's out of work by window (median over seeds; worst seed), the months a seed spends
above 25% out of work, and the seeds whose people fell by 30% or more from their high (the month of the fall)."""
import csv, glob, os, statistics, sys
from collections import defaultdict
out = sys.argv[1]
runs = defaultdict(dict)
for p in glob.glob(os.path.join(out, "*-s*.csv")):
    n, s = os.path.basename(p)[:-4].rsplit("-s", 1)
    runs[n][int(s)] = {int(float(r["m"])): (float(r["pop"]), float(r["unemp"]), float(r["posts_all"])) for r in csv.DictReader(open(p))}
W = [(24, 240), (240, 480), (480, 720), (720, 1200), (1200, 2400)]
print("| variant | " + " | ".join(f"m{a}-{b}" for a, b in W) + " | months above 25%, median (worst) | seeds whose people fell 30% from their high | posts fell 30% from their high, seeds (falls) |")
print("|---|" + "---|" * (len(W) + 3))
for n in sorted(runs, key=lambda x: (x != "today", x)):
    cells = []
    for a, b in W:
        v = [statistics.mean(u for m, (p, u, q) in r.items() if a <= m < b) for r in runs[n].values()]
        cells.append(f"{100*statistics.median(v):.1f}% ({100*max(v):.0f})")
    hi = [sum(1 for m, (p, u, q) in r.items() if m >= 24 and u > .25) for r in runs[n].values()]
    falls = []
    for s, r in sorted(runs[n].items()):
        peak, worst = 0, None
        for m in sorted(r):
            p = r[m][0]
            peak = max(peak, p)
            if m >= 120 and p < .7 * peak and worst is None:
                worst = m
        if worst:
            falls.append(f"s{s} m{worst}")
    cells.append(f"{statistics.median(hi):.0f} ({max(hi)})")
    cells.append(", ".join(falls) or "none")
    # a fall of the posts: from a high, down 30% or more before a new high; counted once each
    nf, ns = 0, 0
    for s, r in runs[n].items():
        peak, fell, k = 0, False, 0
        for m in sorted(r):
            q = r[m][2]
            if q > peak:
                peak, fell = q, False
            elif m >= 24 and peak >= 500 and q < .7 * peak and not fell:
                fell, k = True, k + 1
        nf += k; ns += k > 0
    cells.append(f"{ns} ({nf})")
    print(f"| {n} | " + " | ".join(cells) + " |")
```

## The instrument

### instrument.patch: every single-building ask that fails on spare workers alone, and whether the draw would pass it (on top of gate.patch)

```diff
--- a/src/main/java/ham/citybuildersim/Sector.java
+++ b/src/main/java/ham/citybuildersim/Sector.java
@@ -269,6 +269,23 @@
      */
     static final double GATE_MIN = Double.parseDouble(System.getProperty("gate.min", Double.toString(MIN_STAFFABLE_TO_ORDER)));
     static final int GATE_DRAW = Integer.getInteger("gate.draw", 0);
+    /** STUDY INSTRUMENT (kit2 only): sector | building -> asks, months, min share, max share, asks the draw passes, first, last, month seen. */
+    static final java.util.Map<String, double[]> HELD = new java.util.TreeMap<>();
+    static final double[] FAILS = new double[101], DRAWS = new double[101];
+    static {
+        String path = System.getProperty("gate.held");
+        if (path != null) Runtime.getRuntime().addShutdownHook(new Thread(() -> {
+            try (java.io.PrintWriter w = new java.io.PrintWriter(path)) {
+                w.println("sector | building	asks	months	min share	max share	asks the draw passes	first	last");
+                for (var e : HELD.entrySet()) {
+                    double[] h = e.getValue();
+                    w.printf(java.util.Locale.ROOT, "%s	%.0f	%.0f	%.2f	%.2f	%.0f	%.0f	%.0f%n", e.getKey(), h[0], h[1], h[2], h[3], h[4], h[5], h[6]);
+                }
+                w.println("months	failed asks	of them the draw passes");
+                for (int i = 0; i < FAILS.length; i++) if (FAILS[i] > 0) w.printf(java.util.Locale.ROOT, "%d-%d	%.0f	%.0f%n", 24 * i, 24 * i + 23, FAILS[i], DRAWS[i]);
+            } catch (java.io.IOException e) { /* the instrument only */ }
+        }));
+    }
 
     /**
      * The share of a building's posts this city could actually fill today.
@@ -614,17 +631,33 @@
         // STUDY PROTOTYPE (docs/studies/spec-staffing-gate.md): beside the spare workers, the workers migration would
         // bring for this order's posts within GATE_DRAW months of its opening (Migration.drawFor()), each band's drawn
         // workers taking its own posts first and what is left of them stepping down to the bands below.
-        if (GATE_DRAW > 0 && migration != null) {
-            double[] draw = migration.drawFor(wanted, game.getLabourMarket(), people, GATE_DRAW);
-            double carry = 0;
-            fillable = 0;
+        double spareShare = fillable / total, drawShare = spareShare;
+        if (migration != null) {
+            double[] draw = migration.drawFor(wanted, game.getLabourMarket(), people, GATE_DRAW > 0 ? GATE_DRAW : 12);
+            double carry = 0, withDraw = 0;
             for (int b = bands - 1; b >= 0; b--) {
                 double fromSpare = Math.min(wanted[b], Math.max(0, spare[b]));
                 carry += draw[b];
                 double fromDraw = Math.max(0, Math.min(wanted[b] - fromSpare, carry));
                 carry -= fromDraw;
-                fillable += fromSpare + fromDraw;
+                withDraw += fromSpare + fromDraw;
             }
+            drawShare = withDraw / total;
+            if (GATE_DRAW > 0) fillable = withDraw;
+        }
+        // STUDY INSTRUMENT (kit2 only): every single-building ask that fails on spare workers alone.
+        if (count <= 1 && spareShare < MIN_STAFFABLE_TO_ORDER) {
+            int m = game.getMonth();
+            double[] h = HELD.computeIfAbsent(key() + " | " + t.getName(), k -> new double[] {0, 0, 9, -1, 0, m, m, -1});
+            h[0]++;
+            if (h[7] != m) { h[1]++; h[7] = m; }
+            h[2] = Math.min(h[2], spareShare);
+            h[3] = Math.max(h[3], spareShare);
+            if (drawShare >= MIN_STAFFABLE_TO_ORDER) h[4]++;
+            h[6] = m;
+            int w = Math.min(FAILS.length - 1, m / 24);
+            FAILS[w]++;
+            if (drawShare >= MIN_STAFFABLE_TO_ORDER) DRAWS[w]++;
         }
         // The lowest band nobody can fill is the one named: it is the one the fill runs out of first.
         // ...and a share within rounding of nothing is nothing (SHARE_ROUNDING).
```

### probe-instrument.patch: the month and the city's size when each building of 100 posts or more first stands

```diff
--- a/GateProbe.java
+++ b/GateProbe.java
@@ -35,6 +35,7 @@
     });
 
     static PrintWriter csv;
+    static final java.util.Map<String, Integer> firstSeen = new java.util.HashMap<>();
     static boolean header;
     static int endMonth;
     static boolean stuck;
@@ -296,6 +297,16 @@
         r.put("firm_payroll", payroll);
         r.put("sectors_losing", losing);
 
+        // STUDY INSTRUMENT (kit2 only): the month and the city's size when each building of 100 posts or more first stands.
+        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
+            if (t.getTotalJobs() < 100 || firstSeen.containsKey(t.getName())) continue;
+            if (g.getBuildingManager().getQuantity(t.getId()) > 0) {
+                firstSeen.put(t.getName(), g.getMonth());
+                real.printf("FIRST	%s	%d posts	month %d	people %d	out of work %.1f%%%n", t.getName(), t.getTotalJobs(),
+                        g.getMonth(), pm.getPopulation(), 100 * pm.getUnemploymentRate());
+            }
+        }
+
         if (!header) {
             csv.println(String.join(",", r.keySet()));
             header = true;
```

### held.sh: one instrumented city

```bash
#!/usr/bin/env bash
# held.sh OUT NAME SEED MONTHS [-Dprop...]: one city on the instrumented build in OUT/proto (the kit's Sector,
# Migration and GateProbe with instrument.patch and probe-instrument.patch on top, javac'd against OUT/classes,
# a snapshot of target/classes). Writes OUT/NAME-sSEED.csv, .log (its FIRST lines: when each building of 100
# posts or more first stands) and .held (every single-building ask that failed on spare workers alone).
set -euo pipefail
O=$1; n=$2; s=$3; m=$4; shift 4
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
CP="$O/proto$SEP$O/classes$SEP$(cat target/classpath.txt)"
mkdir -p "$O/tmp"
java -Xmx2g "-Djava.io.tmpdir=$O/tmp" "$@" "-Dgate.held=$O/$n-s$s.held" -cp "$CP" \
    ham.citybuildersim.GateProbe "$s" "$m" "$O/$n-s$s.csv" > "$O/$n-s$s.log" 2>&1
echo "$n $(tail -1 "$O/$n-s$s.log")"
```

### heldsum.py: table 3

```python
"""heldsum.py OUT NAME [N]: the buildings the gate refused on spare workers alone, over every seed's NAME-s*.held:
asks (summed), the seeds they were refused in, the share of their posts the spare could fill (lowest to highest),
how many of the asks a 12-month draw passes, and the first and last month refused. The N most-asked (default 25)."""
import glob, os, sys
from collections import defaultdict
out, name = sys.argv[1], sys.argv[2]
n = int(sys.argv[3]) if len(sys.argv) > 3 else 25
agg = defaultdict(lambda: [0, 0, 9, 0, 0, 10**9, 0])
seeds = 0
for p in sorted(glob.glob(os.path.join(out, f"{name}-s*.held"))):
    seeds += 1
    for line in open(p).read().splitlines()[1:]:
        f = line.split("\t")
        if len(f) < 8:
            continue
        a = agg[f[0]]
        a[0] += int(f[1]); a[1] += 1; a[2] = min(a[2], float(f[3])); a[3] = max(a[3], float(f[4]))
        a[4] += int(f[5]); a[5] = min(a[5], int(f[6])); a[6] = max(a[6], int(f[7]))
print(f"{seeds} seeds, {len(agg)} buildings refused at least once")
print()
print("| sector, building | asks | seeds | share the spare fills | asks a 12-month draw passes | first-last month |")
print("|---|---|---|---|---|---|")
for k, a in sorted(agg.items(), key=lambda kv: -kv[1][0])[:n]:
    print(f"| {k.replace(' | ', ', ')} | {a[0]} | {a[1]} | {a[2]:.2f}-{a[3]:.2f} | {a[4]} ({100*a[4]/a[0]:.0f}%) | {a[5]}-{a[6]} |")
tot = sum(a[0] for a in agg.values()); dr = sum(a[4] for a in agg.values())
print(f"| all | {tot} | | | {dr} ({100*dr/tot:.0f}%) | |")
```

### firsts.py: table 4

```python
"""firsts.py OUT NAME... : when each building of 100 posts or more first stands, from the FIRST lines of every
NAME-s*.log: per variant the median month and the median city then, and in how many seeds it stood at all."""
import glob, os, re, statistics, sys
from collections import defaultdict
out, names = sys.argv[1], sys.argv[2:]
data, seeds = {}, {}
for n in names:
    d = defaultdict(list)
    logs = sorted(glob.glob(os.path.join(out, f"{n}-s*.log")))
    seeds[n] = len(logs)
    for p in logs:
        for line in open(p, errors="replace"):
            if line.startswith("FIRST\t"):
                f = line.rstrip("\n").split("\t")
                posts = int(f[2].split()[0]); m = int(f[3].split()[1]); pop = int(f[4].split()[1])
                d[(f[1], posts)].append((m, pop))
    data[n] = d
keys = sorted({k for d in data.values() for k in d}, key=lambda k: statistics.median(m for m, p in data[names[0]].get(k, [(9999, 0)])))
print("| building (posts) | " + " | ".join(f"{n}: month, people (seeds)" for n in names) + " |")
print("|---|" + "---|" * len(names))
for k in keys:
    cells = []
    for n in names:
        v = data[n].get(k, [])
        cells.append(f"m{statistics.median(m for m, p in v):.0f}, {statistics.median(p for m, p in v)/1000:.1f}k ({len(v)} of {seeds[n]})" if v else f"- (0 of {seeds[n]})")
    print(f"| {k[0]} ({k[1]}) | " + " | ".join(cells) + " |")
```

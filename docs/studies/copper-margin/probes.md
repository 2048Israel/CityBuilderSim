# The copper margin study's probes (CU1)

The sources behind [spec-copper-margin.md](../spec-copper-margin.md), kept as text because CU1's merge is
documents only. None is in the build. To run one, copy its block to a file of the name its heading gives, in
one folder outside the tree, and follow the comment at its top. Every city is founded by `LongPlaytest` in a
`TempDirs` folder; none touches a save slot. The tables they printed are in [tables.md](tables.md).

## CopperProbe.java

The city probe: a year of the two copper sectors' books, each copper good's prices and flows, the band with nothing traded beside the band as set, the old copper, the planner's candidates, and every sector's year each tenth year. Called after every month of `LongPlaytest` by `run.sh`'s hook; public getters only.

```java
package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The copper margin study's probe (CU1, docs/studies/spec-copper-margin.md):
 * a year of the two copper sectors' books, of each copper good's prices and
 * flows, of the city's old copper and of what the planner weighs, summed
 * month by month, tagged rows of one CSV at each twelfth month.
 *
 * WHY: LS1 found Copper Refining buying its input at 52% of its sales from
 * the sector's books alone, which say that the margin is there and not which
 * works, which feed and which price make it. This reads the same books beside
 * the buildings standing, each good's band and flows, and the old copper's
 * pool, on the default playtest itself (run.sh's hook, as LS1's), through
 * public getters only; it changes nothing.
 */
public final class CopperProbe {

    static final Good[] GOODS = {Good.COPPER_CONC, Good.ANODE, Good.COPPER, Good.COPPER_SCRAP,
            Good.COPPER_ROD, Good.ACID, Good.WIRE_CABLE};
    static final String[] SECTORS = {Sectors.COPPER_REFINING, Sectors.COPPER_MINING};
    static final String[] BUILDINGS = {"Copper Mine & Mill", "Large Copper Mine", "Leach & SX-EW Plant",
            "Copper Smelter", "Electrolytic Refinery", "Scrap Copper Refinery", "Rod Mill", "Wire & Cable Works"};

    /** A sector's year: revenue, inputs, payroll, electricity, water, maintenance, propertyTax, interest, tax, net, workers, rate. */
    private static final Map<String, double[]> books = new LinkedHashMap<>();
    /** A good's year: 0-5 prices summed (local, import, export, netImport, netExport, bandIndex), 6-10 the market's
     *  imported, exported, localFilled, supply, demand, 11-16 refining's produced, soldLocal, exported, captive,
     *  boughtLocal, imported, 17-18 its sales and purchases at the month's prices, 19-21 mining's produced,
     *  soldLocal, exported, 22 refining's need. */
    private static final Map<Good, double[]> goods = new EnumMap<>(Good.class);
    /** Every sector's year: revenue, inputs, payroll, netIncome, workers (the A rows, each tenth year). */
    private static final Map<String, double[]> all = new LinkedHashMap<>();
    private static final double[] old = new double[5];
    private static PrintStream csv;
    private static double gdp, months;

    private CopperProbe() {}

    public static void month(Game g) {
        if (csv == null) open();
        Sector refining = g.getSectors().byKey(Sectors.COPPER_REFINING);
        Sector mining = g.getSectors().byKey(Sectors.COPPER_MINING);
        for (String key : SECTORS) {
            Sector s = g.getSectors().byKey(key);
            if (s == null) continue;
            SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
            double[] a = books.computeIfAbsent(key, k -> new double[12]);
            double[] v = {m.revenue(), m.inputs(), m.payroll(), m.electricity(), m.water(), m.maintenance(),
                    m.propertyTax(), m.interest(), m.tax(), m.netIncome(), s.getWorkers(), s.getOperatingRate()};
            for (int i = 0; i < v.length; i++) a[i] += v[i];
        }
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth m = g.getSectorBooks().get(s);
            if (m == null) continue;
            double[] a = all.computeIfAbsent(s.key(), k -> new double[5]);
            a[0] += m.revenue(); a[1] += m.inputs(); a[2] += m.payroll(); a[3] += m.netIncome(); a[4] += s.getWorkers();
        }
        for (Good good : GOODS) {
            GoodsMarket mk = g.getMarkets().get(good);
            if (mk == null) continue;
            double[] a = goods.computeIfAbsent(good, k -> new double[23]);
            double local = mk.getLocalPrice(), imp = mk.importPrice(), exp = mk.exportPrice();
            a[0] += local; a[1] += imp; a[2] += exp; a[3] += mk.netImportPrice(); a[4] += mk.netExportPrice();
            a[5] += mk.getPriceIndex();
            a[6] += mk.getImported(); a[7] += mk.getExported(); a[8] += mk.getLocalFilled();
            a[9] += mk.getSupply(); a[10] += mk.getDemand();
            if (refining != null) {
                Sector.Output o = refining.outputRow(good);
                if (o != null) {
                    a[11] += o.produced; a[12] += o.soldLocal; a[13] += o.exported; a[14] += o.captive;
                    a[17] += o.soldLocal * local + o.exported * exp;
                }
                Sector.Input in = refining.inputRow(good);
                if (in != null) {
                    a[15] += in.boughtLocal; a[16] += in.imported; a[22] += in.needed;
                    a[18] += in.boughtLocal * local + in.imported * imp;
                }
            }
            if (mining != null) {
                Sector.Output o = mining.outputRow(good);
                if (o != null) { a[19] += o.produced; a[20] += o.soldLocal; a[21] += o.exported; }
            }
        }
        if (refining instanceof CopperRefining cr && g.getOldMetal() != null && g.getOldMetal().copper() != null) {
            OldMetal.Pool pool = g.getOldMetal().copper();
            old[0] += pool.lastArisen(); old[1] += pool.lastBought(); old[2] += pool.lastLandfill();
            old[3] += cr.oldCopperPrice(); old[4] += pool.lastBought() * cr.oldCopperPrice();
        }
        gdp += g.getEconomyManager().getMonthGdp();
        months++;
        if (g.getMonth() % 12 == 0) print(g, refining);
    }

    private static void open() {
        String path = System.getProperty("copper.out", "copper.csv");
        try {
            csv = new PrintStream(path);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("cannot write " + path, e);
        }
    }

    private static String f(double x) { return String.format(Locale.ROOT, "%.6g", x); }

    private static void print(Game g, Sector refining) {
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
        // The world's real price of copper this year, a ratio to 2025's (Nations.goodPrice), and concentrate's.
        double copperLevel = Double.NaN, concLevel = Double.NaN, rodLevel = Double.NaN;
        Nations nations = g.getNations();
        if (nations != null) {
            int year = Nations.yearOf(m);
            for (int i = 1; i <= nations.goodCount(); i++) {
                String key = nations.goodKey(i);
                if (key.equals("COPPER")) copperLevel = nations.goodPrice(i, year);
                if (key.equals("COPPER_CONC")) concLevel = nations.goodPrice(i, year);
                if (key.equals("COPPER_ROD")) rodLevel = nations.goodPrice(i, year);
            }
        }
        csv.println("Y," + m + "," + f(g.getPriceIndex().getIndex()) + "," + f(staffed > 0 ? bill / staffed : Double.NaN)
                + "," + f(g.getEconomyManager().getExchangeRate()) + "," + f(gdp) + "," + pm.getPopulation()
                + "," + f(staffed) + "," + f(copperLevel) + "," + f(concLevel) + "," + f(rodLevel)
                + "," + (g.getWorldMarket() != null));
        for (Map.Entry<String, double[]> e : books.entrySet()) {
            StringBuilder r = new StringBuilder("S," + m + "," + e.getKey() + "," + (int) months);
            for (double x : e.getValue()) r.append(',').append(f(x));
            csv.println(r);
        }
        for (String name : BUILDINGS) csv.println("B," + m + "," + name + "," + g.getBuildingManager().countByName(name));
        WorldMarket wm = g.getWorldMarket();
        for (Map.Entry<Good, double[]> e : goods.entrySet()) {
            GoodsMarket mk = g.getMarkets().get(e.getKey());
            StringBuilder r = new StringBuilder("G," + m + "," + e.getKey());
            for (double x : e.getValue()) r.append(',').append(f(x));
            r.append(',').append(f(mk.getWorldImportSet())).append(',').append(f(mk.getWorldExportSet()))
                    .append(',').append(wm != null && wm.trades(e.getKey()));
            csv.println(r);
        }
        // The band with nothing traded (the nations' own, at the city's site) beside the band as set (with the city's
        // trade), thousands of world dollars a unit, and the literals: copper's goods every year, every traded good
        // each tenth year.
        if (wm != null) {
            for (Good good : Good.values()) {
                boolean copper = java.util.Arrays.asList(GOODS).contains(good);
                if (!wm.trades(good) || !(copper || m % 120 == 0)) continue;
                double[] none = wm.band(good, 0, 0);
                GoodsMarket mk = g.getMarkets().get(good);
                csv.println("N," + m + "," + good + "," + f(none[0]) + "," + f(none[1]) + ","
                        + f(mk == null ? Double.NaN : mk.getWorldExportSet()) + ","
                        + f(mk == null ? Double.NaN : mk.getWorldImportSet()) + ","
                        + f(good.worldExportPrice()) + "," + f(good.worldImportPrice()));
            }
        }
        StringBuilder o = new StringBuilder("O," + m);
        for (double x : old) o.append(',').append(f(x));
        csv.println(o);
        if (refining instanceof ProcessingSector ps) {
            for (SpreadPlanner.Candidate c : ps.appraise(g.getBusinessInvestment(), g)) {
                csv.println("C," + m + "," + c.template().getName() + "," + f(c.earns()) + "," + f(c.cost()) + ","
                        + f(c.score()) + "," + c.failed() + "," + String.valueOf(c.why()).replace(',', ';'));
            }
        }
        if (m % 120 == 0) {
            for (Map.Entry<String, double[]> e : all.entrySet()) {
                StringBuilder r = new StringBuilder("A," + m + "," + e.getKey().replace(',', ';'));
                for (double x : e.getValue()) r.append(',').append(f(x));
                csv.println(r);
            }
        }
        all.clear();
        csv.flush();
        books.clear();
        goods.clear();
        java.util.Arrays.fill(old, 0);
        gdp = months = 0;
    }
}
```

## run.sh

Plays `LongPlaytest` with the probe hooked in, on a copy; `ANCHOR=1` compiles `anchor.py`'s copy of `WorldMarket.java` beside it.

```bash
#!/usr/bin/env bash
# The copper margin study's runs (docs/studies/spec-copper-margin.md), after LS1's run.sh.
#
#   run.sh OUT NAME [LongPlaytest's arguments...]
#
# Plays LongPlaytest itself with CopperProbe.month(g) called after every month: one line added
# to a copy of LongPlaytest.java in OUT, after g.simulateMonths(1) in run(); the tree is not
# touched. MONTHS (default 4000) is -Dplaytest.months, SEED (default 0) -Dplaytest.seed;
# CBS_EARTH and CBS_WORLD name the baked data files. Writes OUT/NAME.csv and OUT/NAME.log.
# ANCHOR=1 compiles anchor.py's copy of WorldMarket.java beside the probe (EXTRA=-Dcu1.anchor=copper|all turns it on).
# Run from the checkout's root after batch-check.sh --fast. PROBE is the probe's folder.
#
# The city is founded in a TempDirs folder by LongPlaytest, never the save folder.
set -euo pipefail
OUT=$1; NAME=$2; shift 2
MONTHS=${MONTHS:-4000}; SEED=${SEED:-0}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";"; mkdir -p "$OUT"; OUT=$(cygpath -m "$OUT");; esac
HERE=${PROBE:-$(dirname "$0")}
mkdir -p "$OUT/classes" "$OUT/probe" "$OUT/src" "$OUT/tmp"
DEPS=$(cat target/classpath.txt)
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
if [ ! -f "$OUT/probe/ham/citybuildersim/CopperProbe.class" ]; then
    SRC=src/main/java/ham/citybuildersim/LongPlaytest.java
    [ "$(grep -c '^            g.simulateMonths(1);$' "$SRC")" = 1 ] || { echo "the hook line moved"; exit 2; }
    sed 's/^            g.simulateMonths(1);$/&\n            CopperProbe.month(g);/' "$SRC" > "$OUT/src/LongPlaytest.java"
    PATCHED=""
    if [ -n "${ANCHOR:-}" ]; then python "$HERE/anchor.py" src/main/java/ham/citybuildersim/WorldMarket.java "$OUT/src/WorldMarket.java"; PATCHED="$OUT/src/WorldMarket.java"; fi
    javac -nowarn -d "$OUT/probe" -cp "$CP" "$OUT/src/LongPlaytest.java" "$HERE/CopperProbe.java" $PATCHED
fi
DATA=""
[ -n "${CBS_EARTH:-}" ] && DATA="$DATA -Dcbs.earth=$CBS_EARTH"
[ -n "${CBS_WORLD:-}" ] && DATA="$DATA -Dcbs.world=$CBS_WORLD"
rc=0
java -Xmx3g "-Djava.io.tmpdir=$OUT/tmp" $DATA ${EXTRA:-} -Dplaytest.months="$MONTHS" -Dplaytest.seed="$SEED" \
    -Dcopper.out="$OUT/$NAME.csv" -cp "$OUT/probe$SEP$CP" ham.citybuildersim.LongPlaytest "$@" \
    > "$OUT/$NAME.log" 2>&1 || rc=$?
echo "$NAME exit $rc"
```

## table.py

The city probe's tables: `books`, `works`, `prices`, `flows`, `plan`, `bands`, `allgoods`, `mix`.

```python
"""The copper margin study's tables (CU1): reads CopperProbe's CSV, prints a year a row.

    python table.py RUN.csv [books|prices|flows|works|old|plan] [month,month,...|every N]
"""
import sys
from collections import defaultdict

path = sys.argv[1]
what = sys.argv[2] if len(sys.argv) > 2 else "books"
pick = sys.argv[3] if len(sys.argv) > 3 else "every 120"

Y, S, B, G, O, C = {}, defaultdict(dict), defaultdict(dict), defaultdict(dict), {}, defaultdict(list)
for line in open(path, encoding="utf-8", errors="replace"):
    p = line.rstrip("\n").split(",")
    m = int(p[1])
    if p[0] == "Y": Y[m] = p[2:]
    elif p[0] == "S": S[m][p[2]] = [float(x) for x in p[3:]]
    elif p[0] == "B": B[m][p[2]] = int(p[3])
    elif p[0] == "G": G[m][p[2]] = [float(x) if x not in ("true", "false") else x for x in p[3:]]
    elif p[0] == "O": O[m] = [float(x) for x in p[2:]]
    elif p[0] == "C": C[m].append(p[2:])

months = sorted(Y)
if pick.startswith("every"):
    n = int(pick.split()[1])
    first = min((m for m in months if S[m].get("Copper Refining", [0, 0])[1] > 0), default=months[-1])
    sel = [m for m in months if m % n == 0 or m == months[-1] or m == first]
else:
    sel = [int(x) for x in pick.split(",")]

SHORT = {"Copper Mine & Mill": "mine", "Large Copper Mine": "bigmine", "Leach & SX-EW Plant": "leach",
         "Copper Smelter": "smelt", "Electrolytic Refinery": "erefin", "Scrap Copper Refinery": "scrapref",
         "Rod Mill": "rod", "Wire & Cable Works": "wire"}
GOODS = ["COPPER_CONC", "ANODE", "COPPER", "COPPER_SCRAP", "COPPER_ROD", "ACID", "WIRE_CABLE"]


def pct(a, b): return "   -  " if not b else "%5.1f%%" % (100 * a / b)


if what == "books":
    print("month   revenue     in/rev  pay/rev  util/rev  tax+int/rev net/rev  workers  rate  | mining: revenue  in/rev net/rev workers | refining rev/GDP")
    for m in sel:
        r = S[m].get("Copper Refining"); k = S[m].get("Copper Mining"); gdp = float(Y[m][3])
        if not r: continue
        n = r[0]; rev = r[1]
        line = "%5d %10.4g  %s  %s  %s   %s    %s %8.0f %5.2f" % (
            m, rev, pct(r[2], rev), pct(r[3], rev), pct(r[4] + r[5], rev), pct(r[7] + r[8] + r[9], rev),
            pct(r[10], rev), r[11] / n, r[12] / n)
        if k: line += "  | %10.4g  %s %s %8.0f" % (k[1], pct(k[2], k[1]), pct(k[10], k[1]), k[11] / k[0])
        line += " | " + pct(rev, gdp) + "  va/GDP " + pct(rev - r[2] - r[4] - r[5], gdp)
        print(line)
elif what == "works":
    print("month  " + "  ".join("%8s" % SHORT[b] for b in SHORT) + "   pop    copper-level  price-index  rate")
    for m in sel:
        print("%5d  " % m + "  ".join("%8d" % B[m].get(b, 0) for b in SHORT) + "  %8s %8s %10s %8s" % (
            Y[m][4], Y[m][6], Y[m][0], Y[m][2]))
elif what == "prices":
    # each good's year-average local price over cathode's export price, and its band
    print("the year's mean prices, each over cathode's export price (a tonne): local [export..import], band index")
    for m in sel:
        cu = G[m]["COPPER"]; n = S[m]["Copper Refining"][0]; base = cu[2] / n
        row = "%5d cathode export %.5g |" % (m, base)
        for g in GOODS:
            a = G[m].get(g)
            if not a: continue
            row += " %s %.3f [%.3f..%.3f] i%.2f |" % (g.replace("COPPER_", "").lower()[:6], a[0] / n / base, a[2] / n / base, a[1] / n / base, a[5] / n)
        print(row)
elif what == "flows":
    print("the year's tonnes (thousands): market imported/exported/local-filled; refining produced/soldLocal/exported/captive, boughtLocal/imported; mining produced")
    for m in sel:
        print("month %d" % m)
        for g in GOODS:
            a = G[m].get(g)
            if not a: continue
            print("  %-13s mkt imp %9.1f exp %9.1f loc %9.1f | ref made %9.1f sold %9.1f exp %9.1f capt %9.1f | bought loc %9.1f imp %9.1f | mine made %9.1f | sales %10.4g buys %10.4g" % (
                g, a[6] / 1e3, a[7] / 1e3, a[8] / 1e3, a[11] / 1e3, a[12] / 1e3, a[13] / 1e3, a[14] / 1e3,
                a[15] / 1e3, a[16] / 1e3, a[19] / 1e3, a[17], a[18]))
        r = S[m].get("Copper Refining")
        if r:
            sales = sum(G[m][g][17] for g in GOODS if g in G[m]); buys = sum(G[m][g][18] for g in GOODS if g in G[m])
            o = O.get(m, [0] * 5)
            print("  books: revenue %.5g inputs %.5g | probe's sales at the month's prices %.5g, buys %.5g, old copper paid %.5g (%.1f kt of %.1f kt arisen)" % (
                r[1], r[2], sales, buys, o[4], o[1] / 1e3, o[0] / 1e3))
elif what == "plan":
    for m in sel:
        print("month %d" % m)
        for c in C[m]:
            print("  %-22s earns %10.4g cost %10.4g score %7.4f  %s %s" % (c[0], float(c[1]), float(c[2]), float(c[3]), c[4], c[5][:90]))
elif what == "bands":
    # the no-trade band's floor over its literal, each good over cathode's same ratio: 1 is parity with cathode
    N = defaultdict(dict)
    for line in open(path, encoding="utf-8", errors="replace"):
        p = line.rstrip("\n").split(",")
        if p[0] == "N": N[int(p[1])][p[2]] = [float(x) for x in p[3:]]
    print("each good's band over its literal band, over cathode's same ratio (1 = parity with cathode): nothing traded floor/ceiling | as set floor/ceiling")
    for m in sel:
        if m not in N or "COPPER" not in N[m]: continue
        cu = N[m]["COPPER"]; b0 = cu[0] / cu[4]
        row = "%5d cathode floor/literal %.4g |" % (m, b0)
        for g in GOODS:
            a = N[m].get(g)
            if not a: continue
            row += " %s %.2f/%.2f set %.2f/%.2f |" % (g.replace("COPPER_", "").lower()[:6], a[0] / a[4] / b0, a[1] / a[5] / b0, a[2] / a[4] / b0, a[3] / a[5] / b0)
        print(row)
elif what == "allgoods":
    import math, statistics
    N = defaultdict(dict)
    for line in open(path, encoding="utf-8", errors="replace"):
        p = line.rstrip("\n").split(",")
        if p[0] == "N": N[int(p[1])][p[2]] = [float(x) for x in p[3:]]
    for m in sel:
        if m not in N or len(N[m]) < 20: continue
        r = {g: a[0] / a[4] for g, a in N[m].items() if a[0] > 0 and a[4] > 0 and math.isfinite(a[0])}
        med = statistics.median(r.values())
        logs = sorted((math.log(v / med), g) for g, v in r.items())
        sd = statistics.pstdev([x for x, _ in logs])
        q = [logs[int(len(logs) * k)][0] for k in (0.1, 0.25, 0.75, 0.9)]
        print("%5d goods %d  median floor/literal %.4g  sd of log %.2f  p10 %.2f p25 %.2f p75 %.2f p90 %.2f x median | low: %s | high: %s" % (
            m, len(r), med, sd, *[math.exp(x) for x in q],
            " ".join("%s %.2f" % (g, math.exp(x)) for x, g in logs[:4]), " ".join("%s %.2f" % (g, math.exp(x)) for x, g in logs[-4:])))
elif what == "mix":
    # the city's year and the sectors that carry it: each sector's revenue less its inputs over the year's GDP, and its workers
    A = defaultdict(dict)
    for line in open(path, encoding="utf-8", errors="replace"):
        p = line.rstrip("\n").split(",")
        if p[0] == "A": A[int(p[1])][p[2]] = [float(x) for x in p[3:]]
    for m in sel:
        if m not in Y: continue
        gdp = float(Y[m][3]); n = 12.0
        top = sorted(A.get(m, {}).items(), key=lambda kv: -(kv[1][0] - kv[1][1]))[:7]
        print("%5d pop %8s staffed %8.0f GDP/yr %10.4g index %9.5g rate %9.5g | %s" % (
            m, Y[m][4], float(Y[m][5]), gdp, float(Y[m][0]), float(Y[m][2]),
            "; ".join("%s %.0f%% (%.0fk w)" % (k[:18], 100 * (v[0] - v[1]) / gdp, v[4] / n / 1e3) for k, v in top)))
```

## WorldDrift.java

The world probe: every traded good's band with nothing traded, at three sites, each fifth January 1900-2230, on the Historical path and on Random paths. No city.

```java
package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;

/**
 * The copper margin study's second probe (CU1, docs/studies/spec-copper-margin.md): the world market's band for
 * every traded good with nothing traded, at three sites, each fifth January from 1900 to 2230, on the Historical
 * path and on Random paths of several seeds. No city, no save: the nations' solve alone.
 *
 * WHY: one playtest shows one path at one site. Whether a chain's goods keep their prices' ratios is the world's
 * own doing (the January solve is the nations' alone), so it can be read without a city, over many paths.
 *
 *   java -Dcbs.world=... -cp ... ham.citybuildersim.WorldDrift OUT.csv [seed...]
 */
public final class WorldDrift {

    private WorldDrift() {}

    public static void main(String[] args) throws Exception {
        Path file = WorldData.locate();
        if (file == null) throw new IllegalStateException("no world file (-Dcbs.world)");
        WorldMarket.Base base = WorldMarket.Base.load(file);
        try (PrintStream out = new PrintStream(args[0])) {
            out.println("path,seed,site,year,good,floor,ceiling,literalExport,literalImport");
            run(file, base, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED, out);
            for (int i = 1; i < args.length; i++) run(file, base, Nations.Path.RANDOM, Long.parseLong(args[i]), out);
        }
    }

    private static void run(Path file, WorldMarket.Base base, Nations.Path path, long seed, PrintStream out) throws Exception {
        Nations nations;
        try (WorldData data = WorldData.open(file)) {
            nations = new Nations(data, path, seed);
        }
        String[] sites = {"standin", "halifax", "rotterdam"};
        WorldMarket[] markets = new WorldMarket[sites.length];
        for (int s = 0; s < sites.length; s++) {
            markets[s] = new WorldMarket(base, nations);
            if (s == 0) markets[s].setStandIn(Founding.DEFAULT_WORLD_SEED, false);
            else if (s == 1) markets[s].setSite(WorldMarket.REFERENCE_LAT, WorldMarket.REFERENCE_LON, false);
            else markets[s].setSite(51.92, 4.48, true);
        }
        for (int year = 1900; year <= 2230; year++) {
            for (int s = 0; s < sites.length; s++) {
                // Every January in turn: a solve starts from last year's answer, as the game's does.
                markets[s].solveYear(year);
                if (year % 5 != 0) continue;
                for (Good g : Good.values()) {
                    if (!markets[s].trades(g)) continue;
                    double[] b = markets[s].band(g, 0, 0);
                    out.println(String.format(Locale.ROOT, "%s,%d,%s,%d,%s,%.6g,%.6g,%.6g,%.6g", path, seed, sites[s], year, g,
                            b[0], b[1], g.worldExportPrice(), g.worldImportPrice()));
                }
            }
        }
        System.out.println(path + " " + seed + " done");
    }
}
```

## drift.py

The world probe's tables: `chain`, `works`, `spread`.

```python
"""The copper margin study's world tables (CU1): reads WorldDrift's CSV.

    python drift.py drift.csv chain [site]          copper's goods over cathode, by year
    python drift.py drift.csv works BUILDINGS.json [site]   every works' feed over its sales, at the literals and drifted
    python drift.py drift.csv spread [site]         how far every good's band wanders from the median good's
"""
import csv, json, math, statistics, sys
from collections import defaultdict

path, what = sys.argv[1], sys.argv[2]
rows = defaultdict(dict)   # (path, seed, site, year) -> good -> (floor, ceiling, litExp, litImp)
for r in csv.DictReader(open(path)):
    try:
        v = (float(r["floor"]), float(r["ceiling"]), float(r["literalExport"]), float(r["literalImport"]))
    except ValueError:
        continue
    rows[(r["path"], int(r["seed"]), r["site"], int(r["year"]))][r["good"]] = v

YEARS = [1900, 1950, 2000, 2025, 2050, 2100, 2150, 2200, 2230]
seeds = sorted({k[1] for k in rows if k[0] == "RANDOM"})


def q(xs, p):
    xs = sorted(xs)
    if not xs: return float("nan")
    i = (len(xs) - 1) * p
    lo, hi = int(math.floor(i)), int(math.ceil(i))
    return xs[lo] + (xs[hi] - xs[lo]) * (i - lo)


def spread(xs): return "%.2f [%.2f-%.2f]" % (statistics.median(xs), q(xs, .1), q(xs, .9)) if xs else "-"


if what == "chain":
    site = sys.argv[3] if len(sys.argv) > 3 else "standin"
    goods = ["COPPER_CONC", "ANODE", "COPPER_SCRAP", "COPPER_ROD", "WIRE_CABLE"]
    print("site %s: each good's floor over its literal export price, over cathode's same ratio (1 = the literals' ratio)" % site)
    print("Random: the median of %d paths [10th-90th]; H: the Historical path" % len(seeds))
    print("year  " + "  ".join("%-28s" % g for g in goods))
    for y in YEARS:
        line = "%d  " % y
        for g in goods:
            xs = []
            for s in seeds:
                b = rows.get(("RANDOM", s, site, y), {})
                if g in b and "COPPER" in b and b[g][0] > 0 and b["COPPER"][0] > 0:
                    xs.append(b[g][0] / b[g][2] / (b["COPPER"][0] / b["COPPER"][2]))
            h = rows.get(("HISTORICAL", 48213, site, y), {})
            hv = h[g][0] / h[g][2] / (h["COPPER"][0] / h["COPPER"][2]) if g in h and "COPPER" in h and h[g][0] > 0 else float("nan")
            line += "%-28s" % ("%s H %.2f" % (spread(xs), hv)) + "  "
        print(line)
elif what == "works":
    site = sys.argv[4] if len(sys.argv) > 4 else "standin"
    d = json.load(open(sys.argv[3], encoding="utf-8"))
    blds = d if isinstance(d, list) else d.get("buildings", d)
    only = sys.argv[5].split(",") if len(sys.argv) > 5 else None
    print("site %s: a works' feed over its sales, feed at the ceiling (imported) and output at the floor (exported), case A;" % site)
    print("literal, then Random's median of %d paths [10th-90th] and (H) the Historical path" % len(seeds))
    print("%-26s %-18s %7s | %-24s | %-24s | %-24s | %s" % ("works", "sector", "literal", "2000", "2100", "2200", "H 2050 / 2100 / 2200"))

    def ratio(b, t, lit=False):
        num = den = 0
        for g, u in t.get("uses", {}).items():
            if g not in b: return None
            num += u * (b[g][3] if lit else b[g][1])
        for g, u in t.get("makes", {}).items():
            if g not in b: return None
            den += u * (b[g][2] if lit else b[g][0])
        return num / den if den > 0 and math.isfinite(num) else None

    for t in blds:
        if not t.get("makes") or not t.get("uses"): continue
        if only and t["sector"] not in only: continue
        any_b = rows.get(("HISTORICAL", 48213, site, 2025), {})
        lit = ratio(any_b, t, True)
        if lit is None: continue
        cells = []
        for y in (2000, 2100, 2200):
            xs = [x for x in (ratio(rows.get(("RANDOM", s, site, y), {}), t) for s in seeds) if x is not None]
            cells.append(spread(xs))
        hs = [ratio(rows.get(("HISTORICAL", 48213, site, y), {}), t) for y in (2050, 2100, 2200)]
        print("%-26s %-18s %6.2f  | %-24s | %-24s | %-24s | %s" % (t["name"][:26], t["sector"][:18], lit, cells[0], cells[1], cells[2],
              " / ".join("%.2f" % h if h is not None else "-" for h in hs)))
elif what == "spread":
    site = sys.argv[3] if len(sys.argv) > 3 else "standin"
    print("site %s: the spread of every traded good's floor over its literal, around the median good's (sd of the log), Random's median of paths, and H" % site)
    for y in YEARS:
        sds = []
        for key in [("RANDOM", s, site, y) for s in seeds] + [("HISTORICAL", 48213, site, y)]:
            b = rows.get(key, {})
            r = [math.log(v[0] / v[2]) for v in b.values() if v[0] > 0 and v[2] > 0 and math.isfinite(v[0])]
            if len(r) < 10: continue
            med = statistics.median(r)
            sds.append(statistics.pstdev([x - med for x in r]))
        if sds: print("%d  Random median sd %.2f [%.2f-%.2f]  H %.2f" % (y, statistics.median(sds[:-1]), min(sds[:-1]), max(sds[:-1]), sds[-1]))
```

## Sellers.java

Who sets the band: every maker's mill price, the three cheapest landed sellers and the three best sinks, for cathode and rod.

```java
package ham.citybuildersim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The copper margin study's third probe (CU1): who sets the city's band for cathode and for rod. On the default
 * path at the stand-in site, in four years: every maker's mill price (lowest, median, highest, over the literal),
 * the three cheapest landed sellers and the three best sinks. No city, no save.
 */
public final class Sellers {
    public static void main(String[] args) throws Exception {
        Path file = WorldData.locate();
        WorldMarket.Base base = WorldMarket.Base.load(file);
        Nations nations;
        try (WorldData data = WorldData.open(file)) { nations = new Nations(data, Nations.Path.RANDOM, Nations.DEFAULT_SEED); }
        WorldMarket m = new WorldMarket(base, nations);
        m.setStandIn(Founding.DEFAULT_WORLD_SEED, false);
        Good[] goods = {Good.COPPER, Good.COPPER_ROD};
        for (int year = 1900; year <= 2200; year++) {
            m.solveYear(year);
            if (year != 2025 && year != 2060 && year != 2100 && year != 2200) continue;
            for (Good g : goods) {
                List<Double> mills = new ArrayList<>();
                for (int i = 1; i <= m.nationCount(); i++) { double p = m.millPrice(g, i); if (p > 0 && Double.isFinite(p)) mills.add(p); }
                Collections.sort(mills);
                double lit = g.worldExportPrice();
                double[] b = m.band(g, 0, 0);
                System.out.println(String.format(Locale.ROOT, "%d %-10s makers %d: mill price over the literal floor, lowest %.2f, median %.2f, highest %.2f | the site's floor %.2f, ceiling %.2f",
                        year, g, mills.size(), mills.get(0) / lit, mills.get(mills.size() / 2) / lit, mills.get(mills.size() - 1) / lit, b[0] / lit, b[1] / g.worldImportPrice()));
                WorldMarket.Curve c = m.curve(g);
                StringBuilder s = new StringBuilder("     cheapest landed sellers:");
                for (int k = 0; k < Math.min(3, c.sellers().size()); k++) {
                    WorldMarket.Curve.Seller x = c.sellers().get(k);
                    s.append(String.format(Locale.ROOT, " %s mill %.2f landed %.2f cap %.3g k units/mo;", m.name(x.nation()), x.mill() / lit, x.landed() / lit, x.capUnits() / 1e3));
                }
                System.out.println(s);
                s = new StringBuilder("     best sinks:");
                for (int k = 0; k < Math.min(3, c.sinks().size()); k++) {
                    WorldMarket.Curve.Sink x = c.sinks().get(k);
                    s.append(String.format(Locale.ROOT, " %s buys from %s, netback %.2f, pays %.2f, cap %.3g k units/mo;", m.name(x.buyer()), m.name(x.supplier()), x.netback() / lit, x.buyerPrice() / lit, x.capUnits() / 1e3));
                }
                System.out.println(s);
            }
        }
    }
}
```

## anchor.py

The prototype of the ways in section 6: writes a copy of `WorldMarket.java` whose curves take a re-anchored price scale at `refill()`.

```python
"""CU1's prototype of the recommended way: writes a copy of WorldMarket.java whose curves are re-anchored at refill().

    python anchor.py SRC/WorldMarket.java OUT/WorldMarket.java

A derived good's curve takes the price scale sigma * k, k chosen each January so that the good's band with nothing
traded, over its literal band, sits where its parent's does (the geometric mean of floor and ceiling). -Dcu1.anchor=copper
anchors copper's four derived goods to the cathode; =all every good that follows a Pink Sheet series to the series' own
good (tools/world/prices.tsv, mark 1 to mark 3); =path every good, so that the reference city's band with nothing
traded (Halifax, no berth: the calibration's) follows the good's real price path in every year, as it does in 2025.
=copperwire and =allwire add wire and cable, priced as rod plus
fabrication (the copper in it on the cathode's level). Unset, the copy plays as the original.
"""
import sys
src = open(sys.argv[1], encoding="utf-8").read()
old = "            made[gi] = new Curve(t.good, t.sigma, t.unitsPerTonne, worldUse(gi), n, fills[gi], price[gi].clone());"
assert src.count(old) == 1
src = src.replace(old, old.replace("t.sigma,", "t.sigma * cu1Anchor(gi),"))
tail = src.rstrip()
assert tail.endswith("}")
add = r'''
    /* ----- CU1's prototype: a derived good's curve re-anchored on its parent's (docs/studies/spec-copper-margin.md) ----- */
    private static final java.util.Map<String, String> CU1_COPPER = java.util.Map.of(
            "COPPER_CONC", "COPPER", "ANODE", "COPPER", "COPPER_SCRAP", "COPPER", "COPPER_ROD", "COPPER");
    private static final java.util.Map<String, String> CU1_ALL = new java.util.HashMap<>(CU1_COPPER);
    static {
        for (String g : new String[] {"PELLETS", "PIG_IRON", "SCRAP", "STEEL"}) CU1_ALL.put(g, "IRON");
        for (String g : new String[] {"COKING_COAL", "MET_COKE"}) CU1_ALL.put(g, "THERMAL_COAL");
        for (String g : new String[] {"BITUMEN", "COKE", "DIESEL", "FUEL_OIL", "JET", "LPG", "LUBRICANTS", "NAPHTHA", "PETROL"}) CU1_ALL.put(g, "CRUDE");
        for (String g : new String[] {"PULP", "PULPWOOD", "RESIDUES", "WOOD_PELLETS"}) CU1_ALL.put(g, "SAWLOGS");
        CU1_ALL.put("PROCESSED_MEAT", "MEAT");
        for (String g : new String[] {"BREAD", "BAKERY", "READY_MEALS"}) CU1_ALL.put(g, "GRAINS");
    }

    private double cu1Mid(int gi) {
        Traded t = base.traded[gi];
        double[] b = fills[gi].model(0, 0);
        double f = t.sigma * b[0] / t.good.worldExportPrice(), c = t.sigma * b[1] / t.good.worldImportPrice();
        boolean fo = f > 0 && Double.isFinite(f), co = c > 0 && Double.isFinite(c);
        return fo && co ? Math.sqrt(f * c) : fo ? f : co ? c : Double.NaN;
    }

    private double cu1Anchor(int gi) {
        String mode = System.getProperty("cu1.anchor", "");
        if (mode.isEmpty()) return 1;
        if (mode.equals("path")) {
            // Every good: the reference city's band with nothing traded follows the good's real price path, as it does in REFERENCE_YEAR.
            Traded t = base.traded[gi];
            double[] fl = base.referenceBand(t, price[gi], t.phi);
            double f = t.sigma * fl[0] / t.good.worldExportPrice(), c = t.sigma * fl[1] / t.good.worldImportPrice();
            boolean fo = f > 0 && Double.isFinite(f), co = c > 0 && Double.isFinite(c);
            double mid = fo && co ? Math.sqrt(f * c) : fo ? f : co ? c : Double.NaN;
            double target = levelTarget(gi) / level(nations, priceIdx[gi], REFERENCE_YEAR, baseYearRead());
            return mid > 0 && target > 0 && Double.isFinite(target) ? target / mid : 1;
        }
        if (mode.endsWith("wire") && base.traded[gi].good == Good.WIRE_CABLE) {
            // Wire and cable is rod plus fabrication: the copper in it (rod's literal value over its own, tonne for tonne) moves with the
            // cathode's level at the site, the rest stays on its own solve.
            int ci = base.indexOf[Good.COPPER.ordinal()];
            double share = (Good.COPPER_ROD.worldImportPrice() + Good.COPPER_ROD.worldExportPrice())
                    / (Good.WIRE_CABLE.worldImportPrice() + Good.WIRE_CABLE.worldExportPrice());
            double own = cu1Mid(gi), cu = cu1Mid(ci);
            return own > 0 && cu > 0 ? (share * cu + (1 - share) * own) / own : 1;
        }
        String parent = (mode.startsWith("all") ? CU1_ALL : CU1_COPPER).get(base.traded[gi].good.name());
        if (parent == null) return 1;
        int pi = base.indexOf[Good.valueOf(parent).ordinal()];
        if (pi < 0 || Double.isNaN(base.traded[pi].sigma)) return 1;
        double own = cu1Mid(gi), par = cu1Mid(pi);
        return own > 0 && par > 0 ? par / own : 1;
    }
}
'''
open(sys.argv[2], "w", encoding="utf-8", newline="\n").write(tail[:-1].rstrip() + "\n" + add)
```

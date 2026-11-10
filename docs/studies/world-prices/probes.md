# The world prices study's probes (WP1)

The sources behind [spec-world-prices.md](../spec-world-prices.md), kept as text because WP1's merge is documents only. None is in the build. To run one, copy its block to a file of the name its heading gives, in one folder outside the tree (the study's was the git-ignored `target/wp1/`, which the scripts' paths still say), and follow the comment at its top. `CBS_EARTH` and `CBS_WORLD` name the two baked data files. Every city is founded by `LongPlaytest` in a `TempDirs` folder; none touches a save slot, and the world probes found no city at all. The tables they printed are in [tables.md](tables.md).

## chain.py

The prototype. Writes a copy of `WorldMarket.java` with the chained January in it, each part behind a property (the list is its first lines); with none set the copy plays as main. `-Dwp1.chain=recipe -Dwp1.own=muv -Dwp1.root=path` is the January the study recommends, `-Dwp1.charge=cycle` the refining charge on top of it. Its tables (`WP1_SERIES`, `WP1_RECIPE`), the charge's record (`WP1_RECORD`, the evidence series of [tcrc.md](tcrc.md), the prototype's only) and the swing's dials are in the Java it writes.

```python
"""WP1's prototype of the chained January: writes a copy of WorldMarket.java whose made goods are priced off their inputs.

    python chain.py SRC/WorldMarket.java OUT/WorldMarket.java

The copy plays as the original unless a property turns a part on:

  -Dwp1.chain=series|recipe   the chain's table. series: the price file's followers, each wholly on its series' own good
                              (tools/world/prices.tsv, mark 1 on a series whose mark 3 is a good), wire and cable 70% rod.
                              recipe: each made good on its works' inputs at the literals' shares (buildings.json at the
                              mid-band literal prices), the rest of it on its own price.
  -Dwp1.see=landed|ces|mean   what an input costs a nation, over what it cost it in the base year. landed: the cheapest
                              maker's mill price landed there (the good as one commodity, as the city sees it); ces: the
                              nation's own price index of the good (its base-year suppliers); mean: the world's mean mill price.
  -Dwp1.own=muv               a made good's own part follows the manufactures index (machinery's series), not the series the price
                              file gives the good: the conversion is labour, energy and plant, whose prices are the manufactures'.
  -Dwp1.root=path             a good that follows nothing has its world supply scale set so that the reference city's band
                              with nothing traded stands on the good's price path in every year (CU1's path anchor, inside
                              the solve: a uniform move of the makers' prices is another lambda).
  -Dwp1.supply=world          a maker's supply grows with the world's use of the good, not with its own nation's output.
  -Dwp1.guard=1               the solve's guard alone: an iterate the accelerator throws out of the numbers goes back to the plain
                              step from the last state that had them. On with any other switch.
  -Dwp1.charge=cycle          concentrate and anode carry the treatment and refining charge: money off the copper in them,
                              the real yearly record's on the Historical path 2000-2026, off it a swing of its range and pace
                              priced on the manufactures index. The calibration reads the charge the literals were set on.

A made good's price in a nation is  sum(share x what its input costs there) + own x its own solved price,  in ratios to the
base year. Its makers' supply is then what they sell at that price (capacity follows the price to cost), so the market
clears by construction and the month's step, the fills and the save read it as they read any solved good.
"""
import sys

src = open(sys.argv[1], encoding="utf-8").read()


def swap(old, new, count=1):
    global src
    assert src.count(old) == count, (src.count(old), old)
    src = src.replace(old, new)


# 1. The calibration: a plain pass first (the freight scales a parent is read with), then the chained pass in the chain's order.
swap("""        private void calibrate(WorldData data) {
            Nations hist = new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
""", """        private void calibrate(WorldData data) {
            if (wp1Active() && wp1PhiA == null && !wp1Plain) {
                wp1Plain = true;
                calibrate(data);
                wp1Plain = false;
                wp1PhiA = new double[traded.length];
                for (int gi = 0; gi < traded.length; gi++) {
                    Traded t = traded[gi];
                    wp1PhiA[gi] = t.phi;
                    t.phi = t.sigma = t.refFloor = t.refCeiling = Double.NaN;
                    t.fit = Fit.NONE;
                }
                System.out.println("WP1: the plain calibration: " + fits[Fit.CALIBRATED.ordinal()] + " calibrated, " + fits[Fit.ONE_SIDED.ordinal()]
                        + " one-sided, " + fits[Fit.CLASS_MEDIAN.ordinal()] + " on their class's median");
                Arrays.fill(fits, 0);
            }
            Nations hist = new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
            double[] wp1Flat = new double[traded.length * n];
            double[][] wp1Seen = new double[traded.length][];
            Arrays.fill(wp1Flat, 1);
""")
swap("""            for (int gi = 0; gi < traded.length; gi++) {
                Traded t = traded[gi];
                Arrays.fill(phat, 1);
                lam[0] = 1;
                solve(t, gHat, null, true, level(hist, priceIdx[gi], REFERENCE_YEAR, baseYear), SOLVE_TOL, phat, lam, 0, work);
                for (int i = 0; i < n; i++) p[i] = t.v * phat[i];
""", """            for (int gi : wp1Plain ? wp1Natural() : wp1Chain().order) {
                Traded t = traded[gi];
                Arrays.fill(phat, 1);
                lam[0] = 1;
                work.wp1Supply = wp1SupplyGrowth(t, gHat);
                double wp1Level = level(hist, wp1Plain ? priceIdx[gi] : priceIdx[wp1Own(gi)], REFERENCE_YEAR, baseYear);
                if (wp1Plain || wp1Chain().own[gi] > 0) solve(t, gHat, null, true, wp1Level, SOLVE_TOL, phat, lam, 0, work);
                work.wp1Supply = null;
                if (!wp1Plain) wp1After(gi, phat, lam, 0, wp1Flat, wp1Seen, hist, REFERENCE_YEAR, wp1Level, true);
                System.arraycopy(phat, 0, wp1Flat, gi * n, n);
                for (int i = 0; i < n; i++) p[i] = t.v * phat[i];
""")
swap("""                    t.fit = imp || exp ? Fit.ONE_SIDED : Fit.NONE;
                }
                kept[gi] = p.clone();
""", """                    t.fit = imp || exp ? Fit.ONE_SIDED : Fit.NONE;
                }
                // WP1: a good that does not calibrate takes the plain pass's freight scale at once, so a good made of it reads the scale the game will.
                if (wp1PhiA != null && t.fit != Fit.CALIBRATED && t.fit != Fit.NONE && wp1PhiA[gi] > 0) t.phi = wp1PhiA[gi];
                kept[gi] = p.clone();
""")
swap("""                if (t.fit != Fit.CALIBRATED) t.phi = medians.getOrDefault(t.goodClass, overall);
""", """                if (t.fit != Fit.CALIBRATED && !(wp1PhiA != null && t.phi > 0)) t.phi = medians.getOrDefault(t.goodClass, overall);
""")

# 2. The solve: a maker's supply may grow with something other than its nation's output.
swap("""            lnMake[k] = log(t.make[makers[k]] * gHat[makers[k]]);
""", """            lnMake[k] = log(t.make[makers[k]] * (work.wp1Supply == null ? gHat[makers[k]] : work.wp1Supply[makers[k]]));
""")
swap("""        final Accel accel;
        /** The worst clearing error and the level error at the last pass, for the log. */
""", """        final Accel accel;
        /** WP1: each maker's supply growth for the solve in hand, null for its nation's output's. */
        double[] wp1Supply;
        /** WP1: the plain step from the last state that had its numbers, and whether there is one. */
        double[] wp1Good;
        boolean wp1Have;
        /** The worst clearing error and the level error at the last pass, for the log. */
""")

# 2b. The solve keeps to the numbers: an iterate the accelerator throws out of them goes back to the plain step.
swap("""            double size = Math.max(worst, levelError);
            if (size > ACCEL_BACK * lastWorst) work.accel.reset();
""", """            double size = Math.max(worst, levelError);
            if (WP1_GUARD) {
                if (!Double.isFinite(size)) {
                    if (work.wp1Have) System.arraycopy(work.wp1Good, 0, x, 0, dim);
                    else Arrays.fill(x, 0, dim, 0);
                    work.wp1Have = false;
                    work.accel.reset();
                    lastWorst = Double.POSITIVE_INFINITY;
                    wp1Guards++;
                    pass++;
                    continue;
                }
                if (work.wp1Good == null || work.wp1Good.length < dim) work.wp1Good = new double[dim];
                System.arraycopy(g, 0, work.wp1Good, 0, dim);
                work.wp1Have = true;
            }
            if (size > ACCEL_BACK * lastWorst) work.accel.reset();
""")
swap("""        work.accel.reset();
        int pass = 0;
        double lastWorst = Double.POSITIVE_INFINITY;
""", """        work.accel.reset();
        work.wp1Have = false;
        int pass = 0;
        double lastWorst = Double.POSITIVE_INFINITY;
""")

# 3. The January: the chain's order, and each good finished (anchored, or priced off its inputs) before the next reads it.
swap("""            for (int gi = 0; gi < goodCount; gi++) {
                double target = level(nations, priceIdx[gi], year, baseYearRead());
                double[] slice = new double[n];
                System.arraycopy(phat, gi * n, slice, 0, n);
                passes[gi] = solve(base.traded[gi], g, null, true, target, SOLVE_TOL, slice, lambda, gi, work);
                System.arraycopy(slice, 0, phat, gi * n, n);
            }
""", """            double[][] wp1Seen = new double[goodCount][];
            for (int gi : base.wp1Chain().order) {
                double target = level(nations, priceIdx[base.wp1Own(gi)], year, baseYearRead());
                double[] slice = new double[n];
                System.arraycopy(phat, gi * n, slice, 0, n);
                work.wp1Supply = base.wp1SupplyGrowth(base.traded[gi], g);
                if (warm >= 0) base.wp1Start(gi, slice, lambda, gi, target);
                passes[gi] = base.wp1Chain().own[gi] > 0 ? solve(base.traded[gi], g, null, true, target, SOLVE_TOL, slice, lambda, gi, work) : 0;
                work.wp1Supply = null;
                base.wp1After(gi, slice, lambda, gi, phat, wp1Seen, nations, year, target, false);
                System.arraycopy(slice, 0, phat, gi * n, n);
            }
""")

# 4. What a solved year implies: a made good's supply is what its makers sell at the chained price.
swap("""            for (int i : t.makers) {
                double ph = phat[gi * n + i];
""", """            double[] wp1Grow = base.wp1SupplyGrowth(t, gHat);
            boolean wp1Made = base.wp1Chain().parent[gi].length > 0;
            for (int i : t.makers) {
                double ph = phat[gi * n + i];
""")
swap("""                supply[gi][i] = t.s0[i] * gHat[i] * lambda[gi] * exp(SUPPLY_ELASTICITY * log(ph));
""", """                supply[gi][i] = wp1Made ? wp1Sales(t, i, a, spending) * 1000 / (t.v * ph)
                        : t.s0[i] * (wp1Grow == null ? gHat[i] : wp1Grow[i]) * lambda[gi] * exp(SUPPLY_ELASTICITY * log(ph));
""")

# 5. The Base's part: the chain, what an input costs a nation, the anchor, the charge.
swap("""        /** The reference city's {floor, ceiling} in US$ a tonne at freight scale rho, for prices p (makers' mill prices): the best netback and the cheapest landed cost, with nothing traded. */
""", r'''        /* ----- WP1's prototype: the chained January (docs/studies/spec-world-prices.md) ----- */

        private double[] wp1PhiA;
        private boolean wp1Plain;
        private Wp1Chain wp1Chain;

        Wp1Chain wp1Chain() {
            if (wp1Chain == null) wp1Chain = new Wp1Chain(this, WP1_CHAIN.equals("series") ? WP1_SERIES : WP1_CHAIN.equals("recipe") ? WP1_RECIPE : "");
            return wp1Chain;
        }

        private int[] wp1Natural() {
            int[] o = new int[traded.length];
            for (int i = 0; i < o.length; i++) o[i] = i;
            return o;
        }

        /** Each maker's supply growth under -Dwp1.supply=world: the world's use of the good over the base year's (every user's growth weighted by its base use); null for the nation's own. */
        double[] wp1SupplyGrowth(Traded t, double[] gHat) {
            if (!WP1_SUPPLY.equals("world")) return null;
            double num = 0, den = 0;
            for (int j : t.users) {
                num += t.use[j] * gHat[j];
                den += t.use[j];
            }
            double[] s = new double[n];
            Arrays.fill(s, den > 0 ? num / den : 1);
            return s;
        }

        /** The good whose price path a good's own part follows: itself, or under -Dwp1.own=muv, for a made good, the manufactures index's (machinery's series). */
        int wp1Own(int gi) {
            if (!WP1_OWN.equals("muv") || wp1Chain().parent[gi].length == 0) return gi;
            if (wp1Muv < 0) for (int k = 0; k < traded.length; k++) if (traded[k].good == Good.MACHINERY) wp1Muv = k;
            return wp1Muv;
        }

        private int wp1Muv = -1;

        /**
         * How far the reference city's band stands off target with the makers' prices moved as one by exp(x): ln(the band over the band the calibration
         * left it (the literals, where a side calibrated; 1 in the reference year)) less ln(target). -50 where the band is not positive, NaN where it is
         * not a number. A good one side of whose band calibrated is read by that side.
         */
        private double wp1Off(Traded t, double[] slice, double x, double[] p, double target) {
            double k = exp(x);
            for (int i = 0; i < n; i++) p[i] = t.v * slice[i] * k;
            double[] fl = referenceBand(t, p, t.phi);
            double f = t.sigma * fl[0] / t.refFloor, ce = t.sigma * fl[1] / t.refCeiling;
            boolean fo = f > 0 && Double.isFinite(f), co = ce > 0 && Double.isFinite(ce);
            double mid;
            if (t.fit != Fit.CALIBRATED) {
                boolean byCeiling = t.good.importable();
                if (Double.isNaN(byCeiling ? ce : f)) return Double.NaN;
                if (!(byCeiling ? co : fo)) return -50;
                mid = byCeiling ? ce : f;
            } else if (co && fo) {
                mid = Math.sqrt(f * ce);
            } else if (co) {
                mid = ce;
            } else {
                return Double.isNaN(ce) ? Double.NaN : -50;
            }
            return log(mid / target);
        }

        /** The Historical path's level in the reference year, a good: what the calibration's literal prices stand on. */
        private double[] wp1RefLevel;

        /**
         * A solve's start, moved as one onto the year's level: last year's answer left a root off its mean (the path anchor; a uniform move with the
         * supply scale beside it is the same equilibrium) and a made good on its chained price (its supply scale is its own part's and stays), so the
         * solve starts near where it will end. Nothing is kept beside the saved prices and scales, so a city read back starts where it would have.
         */
        void wp1Start(int gi, double[] slice, double[] lam, int lamAt, double target) {
            Wp1Chain c = wp1Chain();
            boolean made = c.parent[gi].length > 0;
            if (!(made ? c.own[gi] > 0 : WP1_ROOT.equals("path"))) return;
            Traded t = traded[gi];
            double mean = 0;
            for (int i : t.makers) mean += t.w[i] * slice[i];
            double k = target / mean;
            if (!(k > 0) || !Double.isFinite(k)) return;
            for (int i : t.makers) slice[i] *= k;
            if (!made) lam[lamAt] *= exp(-(SUPPLY_ELASTICITY + DEMAND_ELASTICITY) * log(k));
        }

        /**
         * Finishes a good's January after its own solve, in place: slice is its makers' prices over the base year's, lam[lamAt] its supply scale, flat every
         * good's finished prices (goods x nations; a parent's are already there, the chain's order sees to it), seen the year's cache of what each parent
         * costs each nation. A good that follows nothing is left alone, or under -Dwp1.root=path moved as one so the reference city's band stands on its
         * path; a made good becomes its inputs' cost there plus its own part.
         */
        void wp1After(int gi, double[] slice, double[] lam, int lamAt, double[] flat, double[][] seen, Nations nations, int year, double levelNow, boolean calibrating) {
            Wp1Chain c = wp1Chain();
            Traded t = traded[gi];
            if (c.parent[gi].length == 0) {
                if (calibrating) {
                    if (wp1RefLevel == null) wp1RefLevel = new double[traded.length];
                    wp1RefLevel[gi] = levelNow;
                    return;
                }
                if (!WP1_ROOT.equals("path") || !(t.sigma > 0) || !(t.phi > 0) || wp1RefLevel == null) return;
                double target = levelNow / wp1RefLevel[gi];
                if (!(target > 0) || !Double.isFinite(target)) return;
                // ln(the band over the calibration's) - ln(target) rises with a uniform move x = ln k of the prices, but not one for one (the freight does
                // not move): a secant in x kept inside the bracket it has found (Illinois), a bisection when it stalls
                double[] p = new double[n];
                double f0 = wp1Off(t, slice, 0, p, target);
                if (Double.isNaN(f0) || Math.abs(f0) < 1e-10) return;
                double lo, hi, flo, fhi;
                if (f0 > 0) { hi = 0; fhi = f0; lo = Double.NaN; flo = 0; } else { lo = 0; flo = f0; hi = Double.NaN; fhi = 0; }
                double x = -Math.max(-3, Math.min(3, f0));
                for (int it = 0; it < 12 && (Double.isNaN(lo) || Double.isNaN(hi)); it++) {
                    double f = wp1Off(t, slice, x, p, target);
                    if (Double.isNaN(f)) return;
                    if (f > 0) { hi = x; fhi = f; x -= Math.max(.5, Math.min(3, f)); } else { lo = x; flo = f; x += Math.max(.5, Math.min(3, -f)); }
                }
                if (Double.isNaN(lo) || Double.isNaN(hi)) return;
                double best = Math.abs(flo) < Math.abs(fhi) ? lo : hi;
                for (int it = 0; it < 40 && Math.min(Math.abs(flo), Math.abs(fhi)) > 1e-10; it++) {
                    boolean finite = flo > -40;
                    x = finite ? hi - fhi * (hi - lo) / (fhi - flo) : .5 * (lo + hi);
                    if (!(x > lo && x < hi)) x = .5 * (lo + hi);
                    double f = wp1Off(t, slice, x, p, target);
                    if (Double.isNaN(f)) break;
                    if (f > 0) { hi = x; fhi = f; if (finite) flo *= .5; } else { lo = x; flo = f; fhi *= .5; }
                    best = x;
                    if (Math.abs(f) < 1e-10) break;
                }
                double k = exp(best);
                for (int i : t.makers) slice[i] *= k;
                lam[lamAt] *= exp(-(SUPPLY_ELASTICITY + DEMAND_ELASTICITY) * best);
                return;
            }
            double[] cost = new double[n];
            for (int k = 0; k < c.parent[gi].length; k++) {
                int pg = c.parent[gi][k];
                if (seen[pg] == null) seen[pg] = wp1See(pg, flat);
                for (int i : t.makers) cost[i] += c.share[gi][k] * seen[pg][i];
            }
            double chi = wp1Chi(t.good, nations, year, calibrating);
            for (int i : t.makers) {
                double in = chi > 0 ? Math.max(WP1_CHARGE_LEAVES * cost[i], cost[i] - chi) : cost[i];
                double v = in + (c.own[gi] > 0 ? c.own[gi] * slice[i] : 0);
                slice[i] = v > 1e-6 && Double.isFinite(v) ? v : 1e-6;
            }
        }

        /** The Historical path's level in the reference year of the copper's price and of the manufactures index, set by the calibration. */
        private double wp1RefCopper, wp1RefMuv;

        /**
         * The charge off a unit of copper in good g, in the copper's base-year price; 0 for a good that carries none. Concentrate carries the treatment and
         * the refining charge, anode the refining charge. The charge is money, not a share: a smelter's fee, priced as every conversion is (on the
         * manufactures index) times its cycle; on the Historical path's record years it is the record's share of that year's copper price. The calibration
         * reads the charge the literals were set on (2024's benchmark), not the reference year's own.
         */
        double wp1Chi(Good g, Nations nations, int year, boolean calibrating) {
            if (!WP1_CHARGE.equals("cycle")) return 0;
            double part = g == Good.COPPER_CONC ? 1 : g == Good.ANODE ? WP1_REFINING_PART : 0;
            if (part == 0) return 0;
            int[] pi = priceIndices(nations, traded);
            int cu = -1, mv = -1;
            for (int k = 0; k < traded.length; k++) {
                if (traded[k].good == Good.COPPER) cu = k;
                if (traded[k].good == Good.MACHINERY) mv = k;
            }
            if (calibrating) {
                wp1RefCopper = level(nations, pi[cu], REFERENCE_YEAR, baseYear);
                wp1RefMuv = level(nations, pi[mv], REFERENCE_YEAR, baseYear);
                return part * WP1_CHARGE_LITERAL * wp1RefCopper;
            }
            int from = nations.path() == Nations.Path.HISTORICAL ? baseYear : baseYear - standInYears;
            int last = WP1_RECORD_FROM + WP1_RECORD.length - 1;
            if (nations.path() == Nations.Path.HISTORICAL && year >= WP1_RECORD_FROM && year <= last)
                return part * WP1_RECORD[year - WP1_RECORD_FROM] * level(nations, pi[cu], year, from);
            return part * wp1Swing(nations, year) * level(nations, pi[mv], year, from) / wp1RefMuv * wp1RefCopper;
        }

        /** What good pg costs each nation over what it cost it in the base year, from the good's finished prices in flat. */
        private double[] wp1See(int pg, double[] flat) {
            Traded t = traded[pg];
            int at = pg * n;
            double[] out = new double[n];
            if (WP1_SEE.equals("mean")) {
                double idx = 0;
                for (int m : t.makers) idx += t.w[m] * flat[at + m];
                Arrays.fill(out, idx);
                return out;
            }
            double phi = t.phi > 0 ? t.phi : PHI_FALLBACK;
            for (int i = 0; i < n; i++) {
                double now = Double.POSITIVE_INFINITY, then = Double.POSITIVE_INFINITY;
                for (int m : t.makers) {
                    double f = phi * freight[m * n + i];
                    now = Math.min(now, t.v * flat[at + m] + f);
                    then = Math.min(then, t.v + f);
                }
                out[i] = now < Double.POSITIVE_INFINITY && then > 0 ? now / then : 1;
            }
            if (WP1_SEE.equals("ces")) {
                double[] a = new double[n];
                for (int m : t.makers) a[m] = exp(-t.eps * log(flat[at + m]));
                for (int j : t.users) {
                    double s = t.ownPi[j] * a[j];
                    for (int q = t.bStart[j]; q < t.bStart[j + 1]; q++) s += t.bPi[q] * a[t.bMaker[q]];
                    if (s > 0) out[j] = exp(-log(s) / t.eps);
                }
            }
            return out;
        }

        /** The reference city's {floor, ceiling} in US$ a tonne at freight scale rho, for prices p (makers' mill prices): the best netback and the cheapest landed cost, with nothing traded. */
''')

# 6. The class's part: the properties, the tables, the charge, a maker's sales.
tail = src.rstrip()
assert tail.endswith("}")
add = r'''
    /* ----- WP1's prototype: the chained January (docs/studies/spec-world-prices.md) ----- */

    static final String WP1_CHAIN = System.getProperty("wp1.chain", ""), WP1_SEE = System.getProperty("wp1.see", "landed"),
            WP1_ROOT = System.getProperty("wp1.root", ""), WP1_OWN = System.getProperty("wp1.own", ""), WP1_SUPPLY = System.getProperty("wp1.supply", ""), WP1_CHARGE = System.getProperty("wp1.charge", "");

    static boolean wp1Active() { return !WP1_CHAIN.isEmpty() || !WP1_ROOT.isEmpty(); }

    /** The solve's guard: on with any other switch, or alone by -Dwp1.guard=1 (the tree's own solve has none). */
    static final boolean WP1_GUARD = "1".equals(System.getProperty("wp1.guard", WP1_CHAIN.isEmpty() && WP1_ROOT.isEmpty() && WP1_SUPPLY.isEmpty() ? "0" : "1"));
    /** How often the guard put an iterate back, this process. */
    static volatile long wp1Guards;

    /** The price file's followers, each wholly on its series' own good; wire and cable 70% rod (Jerus, 2026-11-09). */
    static final String WP1_SERIES = "COPPER_CONC=COPPER:1 ANODE=COPPER:1 COPPER_SCRAP=COPPER:1 COPPER_ROD=COPPER:1 "
            + "PELLETS=IRON:1 PIG_IRON=IRON:1 SCRAP=IRON:1 STEEL=IRON:1 COKING_COAL=THERMAL_COAL:1 MET_COKE=THERMAL_COAL:1 "
            + "BITUMEN=CRUDE:1 COKE=CRUDE:1 DIESEL=CRUDE:1 FUEL_OIL=CRUDE:1 JET=CRUDE:1 LPG=CRUDE:1 LUBRICANTS=CRUDE:1 NAPHTHA=CRUDE:1 PETROL=CRUDE:1 "
            + "PULP=SAWLOGS:1 PULPWOOD=SAWLOGS:1 RESIDUES=SAWLOGS:1 WOOD_PELLETS=SAWLOGS:1 PROCESSED_MEAT=MEAT:1 "
            + "BREAD=GRAINS:1 BAKERY=GRAINS:1 READY_MEALS=GRAINS:1 WIRE_CABLE=COPPER_ROD:0.70";

    /** Each made good on its works' inputs at the literals' shares (buildings.json at the mid-band literal prices); a good the file prices off another with no works between them stays wholly on it. */
    static final String WP1_RECIPE = "COPPER_CONC=COPPER:1 ANODE=COPPER:1 COPPER_SCRAP=COPPER:1 COPPER_ROD=COPPER:0.98 WIRE_CABLE=COPPER_ROD:0.70 "
            + "PELLETS=IRON:0.70,THERMAL_COAL:0.01 COKING_COAL=THERMAL_COAL:1 MET_COKE=COKING_COAL:0.74 PIG_IRON=IRON:0.36,MET_COKE:0.33 "
            + "SCRAP=PIG_IRON:1 STEEL=PIG_IRON:0.66,SCRAP:0.16 FABRICATED_STEEL=STEEL:0.38 MACHINERY=STEEL:0.08 "
            + "CARS=FABRICATED_STEEL:0.15,MACHINERY:0.11 VANS=FABRICATED_STEEL:0.11,MACHINERY:0.09 ROLLING_STOCK=MACHINERY:0.09,FABRICATED_STEEL:0.08 "
            + "TRUCKS=MACHINERY:0.10,FABRICATED_STEEL:0.07 CEMENT=LIMESTONE:0.34,THERMAL_COAL:0.12,AGGREGATE:0.04 LIME=LIMESTONE:0.21,THERMAL_COAL:0.07 "
            + "BITUMEN=CRUDE:0.85 COKE=CRUDE:0.85 DIESEL=CRUDE:0.85 FUEL_OIL=CRUDE:0.85 JET=CRUDE:0.85 LPG=CRUDE:0.85 LUBRICANTS=CRUDE:0.85 NAPHTHA=CRUDE:0.85 PETROL=CRUDE:0.85 "
            + "PULPWOOD=SAWLOGS:1 RESIDUES=SAWLOGS:1 LUMBER=SAWLOGS:0.52 PANELS=PULPWOOD:0.31 PULP=PULPWOOD:0.21,RESIDUES:0.08 PAPER=PULP:0.74,FUEL_OIL:0.07 "
            + "WOOD_PELLETS=RESIDUES:0.32 PROCESSED_MEAT=MEAT:0.40 READY_MEALS=MEAT:0.40 BREAD=CROPS:0.24 BAKERY=CROPS:0.24 SNACKS=CROPS:0.19";

    /** The chain: each modelled good's parents and their shares (none for a good that follows nothing), its own part, and the order the goods are solved in. */
    static final class Wp1Chain {
        final int[][] parent;
        final double[][] share;
        final double[] own;
        final int[] order;

        Wp1Chain(Base b, String table) {
            int count = b.traded.length;
            parent = new int[count][0];
            share = new double[count][0];
            own = new double[count];
            Arrays.fill(own, 1);
            for (String row : table.trim().split("\\s+")) {
                if (row.isEmpty()) continue;
                String[] kv = row.split("=");
                int gi = idx(b, kv[0]);
                if (gi < 0) continue;
                String[] ps = kv[1].split(",");
                int[] pp = new int[ps.length];
                double[] ss = new double[ps.length];
                int m = 0;
                double sum = 0;
                for (String p : ps) {
                    String[] a = p.split(":");
                    int pi = idx(b, a[0]);
                    if (pi < 0) continue;
                    pp[m] = pi;
                    ss[m] = Double.parseDouble(a[1]);
                    sum += ss[m++];
                }
                parent[gi] = Arrays.copyOf(pp, m);
                share[gi] = Arrays.copyOf(ss, m);
                own[gi] = m == 0 ? 1 : Math.max(0, 1 - sum);
                if (own[gi] < 1e-9) own[gi] = 0;
            }
            order = new int[count];
            boolean[] placed = new boolean[count];
            int at = 0;
            for (int gi = 0; gi < count; gi++) {
                if (parent[gi].length == 0) {
                    order[at++] = gi;
                    placed[gi] = true;
                }
            }
            while (at < count) {
                int before = at;
                for (int gi = 0; gi < count; gi++) {
                    if (placed[gi]) continue;
                    boolean ready = true;
                    for (int p : parent[gi]) ready &= placed[p];
                    if (ready) {
                        order[at++] = gi;
                        placed[gi] = true;
                    }
                }
                if (at == before) throw new IllegalStateException("WP1: the chain goes round");
            }
        }

        private static int idx(Base b, String key) {
            try {
                return b.indexOf[Good.valueOf(key).ordinal()];
            } catch (IllegalArgumentException e) {
                return -1;
            }
        }
    }

    /** What a maker sells at the prices in hand, thousand US$ of the base year: a = p-hat^-eps and spending each buyer's spending over its index, as deriveSolution has them. */
    private static double wp1Sales(Traded t, int i, double[] a, double[] spending) {
        double rr = t.ownX[i] * spending[i];
        for (int q = t.sStart[i]; q < t.sStart[i + 1]; q++) rr += t.sX[q] * spending[t.sBuyer[q]];
        return a[i] * rr;
    }

    /** The refining charge's part of the two charges together (2024's benchmark: US$80 a tonne at 26.5% copper and 96.5% paid is 14.2 cents a pound, beside 8.0). */
    static final double WP1_REFINING_PART = .36;

    /** The first year of WP1_RECORD. */
    static final int WP1_RECORD_FROM = 2000;

    /**
     * The two charges together over the copper price, each year 2000-2026: the study's evidence series (target/wp1/tcrc.md: USGS contract averages
     * 2000-2004, the annual benchmark after, 2005 between its neighbours). The prototype's only; what may ship is the note's section 7.
     */
    static final double[] WP1_RECORD = { .219, .268, .253, .149, .169, .128, .086, .052, .040, .089, .038, .039, .048, .058, .082, .119, .122, .092, .077,
            .082, .061, .039, .045, .063, .054, .013, 0 };

    /** The charge the literals were set on: 2024's benchmark over 2024's price (CU1, section 5). */
    static final double WP1_CHARGE_LITERAL = .054;

    /** The least a charge leaves of the copper in a good. */
    static final double WP1_CHARGE_LEAVES = .05;

    /**
     * The swing off the record: the charge over the reference year's copper price at the reference year's manufactures, its middle, its half-height and
     * the years between two large peaks (the record over the manufactures index, 2000-2024: 3.2 to 7.8%, large peaks in 1997, 2006, 2015 and 2024).
     */
    static final double WP1_SWING_MID = .055, WP1_SWING_HALF = .022, WP1_SWING_YEARS = 9;

    /** The swing in a year: a pure function of the path's seed and the year, so nothing is saved. The Historical path's peaks stand on the record's (1997 and every ninth year). */
    static double wp1Swing(Nations nations, int year) {
        double phase = nations.path() == Nations.Path.HISTORICAL ? -1997 : WP1_SWING_YEARS * new java.util.Random(World.mix(nations.seed() + 0x7C2CL)).nextDouble();
        double x = 2 * Math.PI * (year + phase) / WP1_SWING_YEARS;
        // a second, slower turn a third the height, so no two swings are the same
        return Math.max(0, WP1_SWING_MID + WP1_SWING_HALF * (.75 * Math.cos(x) + .45 * Math.cos(x / 2.7 + 1)));
    }
}
'''
open(sys.argv[2], "w", encoding="utf-8", newline="\n").write(tail[:-1].rstrip() + "\n" + add)
```

## WP1Drift.java

The world probe: every good's band with nothing traded at three sites, each fifth January from 1900 to 2230, on the Historical path and the Random paths named, and what each January's solve took. Compiled against `chain.py`'s copy.

```java
package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

/**
 * The world prices study's world probe (WP1, docs/studies/spec-world-prices.md), after CU1's WorldDrift: the world
 * market's band for every traded good with nothing traded, at three sites, each fifth January from 1900 to 2230, on
 * the Historical path and on Random paths of several seeds, and what each January's solve took. No city, no save.
 *
 * WHY: CU1's WorldDrift showed a chain's goods leaving their prices' ratios in the nations' own solve. This is the
 * same reading under chain.py's copy of WorldMarket, with the January's cost beside it: the first market on a path
 * does every solve (the other two sites take the process's cache), so its times are the solve's.
 *
 *   java -Dcbs.world=... [-Dwp1.chain=...] -cp ... ham.citybuildersim.WP1Drift OUT.csv [seed...]
 */
public final class WP1Drift {

    private WP1Drift() {}

    /** The Historical path's level of each priced good in the reference year: what the literal prices stand on. */
    private static final java.util.Map<String, Double> LITERAL_LEVEL = new java.util.HashMap<>();

    public static void main(String[] args) throws Exception {
        Path file = WorldData.locate();
        if (file == null) throw new IllegalStateException("no world file (-Dcbs.world)");
        long t0 = System.nanoTime();
        WorldMarket.Base base = WorldMarket.Base.load(file);
        System.out.println(String.format(Locale.ROOT, "the world read and calibrated in %.0f ms", (System.nanoTime() - t0) / 1e6));
        try (WorldData data = WorldData.open(file)) {
            Nations hist = new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
            for (int i = 1; i <= hist.goodCount(); i++)
                LITERAL_LEVEL.put(hist.goodKey(i), hist.goodPrice(i, WorldMarket.REFERENCE_YEAR) / hist.goodPrice(i, base.baseYear));
        }
        try (PrintStream out = new PrintStream(args[0])) {
            out.println("path,seed,site,year,good,floor,ceiling,literalExport,literalImport,level");
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
        java.util.Map<String, Integer> priced = new java.util.HashMap<>();
        for (int i = 1; i <= nations.goodCount(); i++) priced.put(nations.goodKey(i), i);
        double cold = 0;
        double[] warm = new double[2230 - 1900];
        long sweeps = 0;
        for (int year = 1900; year <= 2230; year++) {
            for (int s = 0; s < sites.length; s++) {
                // Every January in turn: a solve starts from last year's answer, as the game's does.
                markets[s].solveYear(year);
                if (s == 0) {
                    if (year == 1900) cold = markets[s].lastSolveMillis();
                    else warm[year - 1901] = markets[s].lastSolveMillis();
                    for (Good g : Good.values()) sweeps += markets[s].passes(g);
                }
                if (year % 5 != 0) continue;
                for (Good g : Good.values()) {
                    if (!markets[s].trades(g)) continue;
                    double[] b = markets[s].band(g, 0, 0);
                    // the path's level over the level the literals stand on: where a band on its path would be, over its literal
                    Integer pi = priced.get(g.name());
                    double level = pi == null ? 1 : nations.goodPrice(pi, year) / nations.goodPrice(pi, markets[s].baseYearRead()) / LITERAL_LEVEL.get(g.name());
                    out.println(String.format(Locale.ROOT, "%s,%d,%s,%d,%s,%.6g,%.6g,%.6g,%.6g,%.6g", path, seed, sites[s], year, g,
                            b[0], b[1], g.worldExportPrice(), g.worldImportPrice(), level));
                }
            }
        }
        Arrays.sort(warm);
        double sum = 0;
        for (double w : warm) sum += w;
        System.out.println(String.format(Locale.ROOT, "%s %d: the founding January %.1f ms; the %d after it mean %.2f, median %.2f, 95th %.2f, worst %.2f ms; %d sweeps in all",
                path, seed, cold, warm.length, sum / warm.length, warm[warm.length / 2], warm[(int) (warm.length * .95)], warm[warm.length - 1], sweeps));
    }
}
```

## drift.sh

One variant of the world probe on the twelve Random paths.

```bash
#!/usr/bin/env bash
# drift.sh NAME [java options...]   - the world probe under chain.py's WorldMarket: target/wp1/drift/NAME.csv and .log, twelve Random paths
set -u
NAME=$1; shift
D=target/wp1/drift; mkdir -p $D
CP="target/wp1/proto/pclasses;target/wp1/proto/classes;target/classes;$(cat target/classpath.txt)"
java -Xmx3g -Dcbs.world="${CBS_WORLD:?the baked world file}" "$@" -cp "$CP" ham.citybuildersim.WP1Drift $D/$NAME.csv 1 2 3 4 5 6 7 8 9 10 11 48213 > $D/$NAME.log 2>&1
echo "$NAME exit $?"
```

## wdrift.py

The world's tables from the probe's CSVs: followers over parents, roots over paths, a good's two sides, the width, the works' feed over sales, the calibration.

```python
"""WP1's world tables: reads WP1Drift's CSVs, one a variant.

    python wdrift.py follow SITE NAME=file.csv...   every follower's floor over its literal, over its parent's same ratio (1 = the literals' ratio):
                                                    Random's median of the paths at 2100 and 2200 [10th-90th at 2200], and the Historical path's 2100
    python wdrift.py path SITE NAME=file.csv...     every root's band (the geometric middle of floor and ceiling over their literals) over its path's level
    python wdrift.py sides SITE GOOD,GOOD NAME=file.csv...   a good's floor and ceiling over their literals, each over its path's level: Random's median at 2000, 2100, 2200
    python wdrift.py width SITE NAME=file.csv...    how far the goods' bands stand apart: the sd of log(floor over literal) across goods, by year
    python wdrift.py works BUILDINGS.json SITE NAME=file.csv...   a works' feed over its sales (feed at the ceiling, output at the floor): the literals',
                                                    then a variant Random's median at 2100 and 2200 and (H) the Historical path's 2100
    python wdrift.py lit NAME=file.csv...           the reference city in 2025 on the Historical path: the worst band over literal, each side
"""
import csv, math, statistics, sys
from collections import defaultdict

PAIRS = [("COPPER_CONC", "COPPER"), ("ANODE", "COPPER"), ("COPPER_SCRAP", "COPPER"), ("COPPER_ROD", "COPPER"), ("WIRE_CABLE", "COPPER_ROD"),
         ("PELLETS", "IRON"), ("PIG_IRON", "IRON"), ("SCRAP", "IRON"), ("STEEL", "IRON"), ("FABRICATED_STEEL", "STEEL"), ("MACHINERY", "STEEL"), ("CARS", "STEEL"),
         ("COKING_COAL", "THERMAL_COAL"), ("MET_COKE", "THERMAL_COAL"), ("CEMENT", "THERMAL_COAL"),
         ("PETROL", "CRUDE"), ("DIESEL", "CRUDE"), ("FUEL_OIL", "CRUDE"), ("NAPHTHA", "CRUDE"),
         ("PULPWOOD", "SAWLOGS"), ("PULP", "SAWLOGS"), ("LUMBER", "SAWLOGS"), ("PAPER", "PULP"), ("PROCESSED_MEAT", "MEAT"), ("BREAD", "GRAINS")]
ROOTS = ["COPPER", "IRON", "THERMAL_COAL", "CRUDE", "SAWLOGS", "GRAINS", "MEAT", "LUMBER"]


def read(path):
    rows = defaultdict(dict)
    for r in csv.DictReader(open(path)):
        try:
            v = (float(r["floor"]), float(r["ceiling"]), float(r["literalExport"]), float(r["literalImport"]), float(r.get("level") or "nan"))
        except ValueError:
            continue
        rows[(r["path"], int(r["seed"]), r["site"], int(r["year"]))][r["good"]] = v
    return rows


def q(xs, p):
    xs = sorted(xs)
    if not xs: return float("nan")
    i = (len(xs) - 1) * p
    lo, hi = int(math.floor(i)), int(math.ceil(i))
    return xs[lo] + (xs[hi] - xs[lo]) * (i - lo)


def ok(x): return x > 0 and math.isfinite(x)


what = sys.argv[1]
if what == "lit":
    for arg in sys.argv[2:]:
        nm, path = arg.split("=")
        b = read(path).get(("HISTORICAL", 48213, "halifax", 2025), {})
        worst = max(((max(abs(math.log(v[0] / v[2])) if ok(v[0]) and v[2] > 0 else 0, abs(math.log(v[1] / v[3])) if ok(v[1]) and v[3] > 0 else 0), g) for g, v in b.items()), default=(float("nan"), "-"))
        off = sum(1 for g, v in b.items() if (ok(v[0]) and v[2] > 0 and abs(v[0] / v[2] - 1) > 1e-3) or (ok(v[1]) and v[3] > 0 and abs(v[1] / v[3] - 1) > 1e-3))
        print("%-12s %d goods; %d stand more than 0.1%% off a literal; the worst %s, %.3g%% off" % (nm, len(b), off, worst[1], 100 * math.expm1(worst[0])))
    sys.exit()
WORKS = ["Copper Smelter", "Electrolytic Refinery", "Scrap Copper Refinery", "Rod Mill", "Wire & Cable Works", "Pelletising Plant", "Coke Ovens",
         "Blast Furnace", "Oxygen Steel Shop", "Electric Arc Furnace", "Fabrication Works", "Machine Works", "Assembly Plant", "Heavy Truck Plant",
         "Cement Plant", "Sawmill", "Pulp Mill", "Paper Mill", "Panel Mill", "Meat Works", "Bakery"]
if what == "works":
    import json
    d = json.load(open(sys.argv[2], encoding="utf-8"))
    blds = {t["name"]: t for t in (d if isinstance(d, list) else d.get("buildings", d))}
    site = sys.argv[3]
    runs = [(a.split("=")[0], read(a.split("=")[1])) for a in sys.argv[4:]]
    seeds = sorted({k[1] for k in runs[0][1] if k[0] == "RANDOM"})

    def feed(b, t, lit=False):
        num = den = 0
        for g, u in t.get("uses", {}).items():
            if g not in b: return None
            num += u * (b[g][3] if lit else b[g][1])
        for g, u in t.get("makes", {}).items():
            if g not in b: return None
            den += u * (b[g][2] if lit else b[g][0])
        return num / den if den > 0 and math.isfinite(num) and math.isfinite(den) else None

    print("site %s; a works' feed over its sales; each cell: Random's median at 2100, at 2200, and (H) the Historical path's 2100" % site)
    print("%-22s %7s " % ("works", "literal") + "".join("| %-19s" % nm for nm, _ in runs))
    for w in WORKS:
        t = blds.get(w)
        if not t: continue
        lit = feed(runs[0][1].get(("HISTORICAL", 48213, site, 2025), {}), t, True)
        if lit is None: continue
        line = "%-22s %6.2f  " % (w, lit)
        for nm, rows in runs:
            c = []
            for y in (2100, 2200):
                xs = [x for x in (feed(rows.get(("RANDOM", s, site, y), {}), t) for s in seeds) if x is not None]
                c.append(statistics.median(xs) if xs else float("nan"))
            h = feed(rows.get(("HISTORICAL", 48213, site, 2100), {}), t)
            line += "| %-19s" % ("%.2f %.2f H %.2f" % (c[0], c[1], h if h is not None else float("nan")))
        print(line)
    sys.exit()
site = sys.argv[2]
if what == "sides":
    goods = sys.argv[3].split(",")
    runs = [(a.split("=")[0], read(a.split("=")[1])) for a in sys.argv[4:]]
    seeds = sorted({k[1] for k in runs[0][1] if k[0] == "RANDOM"})
    print("site %s; floor over its literal over the path's level, then ceiling the same: Random's median at 2000, 2100, 2200" % site)
    print("%-18s" % "good" + "".join("| %-32s" % nm for nm, _ in runs))
    for g in goods:
        line = "%-18s" % g.lower()
        for nm, rows in runs:
            fs, cs = [], []
            for y in (2000, 2100, 2200):
                bs = [rows.get(("RANDOM", sd, site, y), {}).get(g) for sd in seeds]
                f = [b[0] / b[2] / b[4] for b in bs if b and b[2] > 0 and ok(b[0]) and ok(b[4])]
                c = [b[1] / b[3] / b[4] for b in bs if b and b[3] > 0 and ok(b[1]) and ok(b[4])]
                fs.append(statistics.median(f) if f else float("nan")); cs.append(statistics.median(c) if c else float("nan"))
            line += "| %-32s" % ("%.2f %.2f %.2f / %.2f %.2f %.2f" % (*fs, *cs))
        print(line)
    sys.exit()
runs = [(a.split("=")[0], read(a.split("=")[1])) for a in sys.argv[3:]]
seeds = sorted({k[1] for k in runs[0][1] if k[0] == "RANDOM"})


def ratio(b, g, p):
    if g not in b or p not in b: return None
    x = b[g][0] / b[g][2] / (b[p][0] / b[p][2]) if b[g][2] > 0 and b[p][2] > 0 and ok(b[g][0]) and ok(b[p][0]) else None
    return x if x and ok(x) else None


if what == "follow":
    print("site %s; each cell: Random's median at 2100, at 2200 [10th-90th], and (H) the Historical path's 2100" % site)
    print("%-28s" % "good over parent" + "".join("| %-34s" % nm for nm, _ in runs))
    for g, p in PAIRS:
        line = "%-28s" % ("%s/%s" % (g.lower(), p.lower()))
        for nm, rows in runs:
            cell = []
            for y in (2100, 2200):
                xs = [x for x in (ratio(rows.get(("RANDOM", s, site, y), {}), g, p) for s in seeds) if x]
                cell.append(xs)
            h = ratio(rows.get(("HISTORICAL", 48213, site, 2100), {}), g, p)
            a, b = cell
            line += "| %-34s" % ("%.2f %.2f [%.2f-%.2f] H %.2f" % (statistics.median(a) if a else float("nan"), statistics.median(b) if b else float("nan"), q(b, .1), q(b, .9), h or float("nan")))
        print(line)
elif what == "path":
    print("site %s; a root's band over its literals, over its path's level (1 = on the path): Random's median at 1900, 2000, 2100, 2200 [10th-90th at 2200]; (H) 2025 and 2100" % site)
    print("%-14s" % "good" + "".join("| %-44s" % nm for nm, _ in runs))
    def mid(b, g):
        if g not in b: return None
        f, c, le, li, lv = b[g]
        parts = [x for x in (f / le if le > 0 and ok(f) else None, c / li if li > 0 and ok(c) else None) if x]
        if not parts or not ok(lv): return None
        return math.exp(sum(math.log(x) for x in parts) / len(parts)) / lv
    for g in ROOTS:
        line = "%-14s" % g.lower()
        for nm, rows in runs:
            ms = []
            for y in (1900, 2000, 2100, 2200):
                xs = [x for x in (mid(rows.get(("RANDOM", s, site, y), {}), g) for s in seeds) if x]
                ms.append(xs)
            h = [mid(rows.get(("HISTORICAL", 48213, site, y), {}), g) or float("nan") for y in (2025, 2100)]
            line += "| %-44s" % ("%s [%.2f-%.2f] H %.2f %.2f" % (" ".join("%.2f" % (statistics.median(x) if x else float("nan")) for x in ms), q(ms[3], .1), q(ms[3], .9), h[0], h[1]))
        print(line)
elif what == "width":
    print("site %s; the sd across goods of log(floor over literal export), Random's median of the paths; and how many goods' bands are not numbers on any path" % site)
    print("%-6s" % "year" + "".join("| %-12s" % nm for nm, _ in runs))
    for y in (1900, 1950, 2000, 2050, 2100, 2150, 2200, 2230):
        line = "%-6d" % y
        for nm, rows in runs:
            sds, bad = [], 0
            for s in seeds:
                b = rows.get(("RANDOM", s, site, y), {})
                r = [math.log(v[0] / v[2]) for v in b.values() if ok(v[0]) and v[2] > 0]
                bad += sum(1 for v in b.values() if v[2] > 0 and not ok(v[0]))
                if len(r) > 10: sds.append(statistics.pstdev(r))
            line += "| %-12s" % ("%.2f%s" % (statistics.median(sds) if sds else float("nan"), " (%d)" % bad if bad else ""))
        print(line)
```

## charge.py

The refining charge's tables from the probe's CSV of the run with the cycle.

```python
"""WP1's tables of the refining charge: reads WP1Drift's CSV of the run with -Dwp1.charge=cycle.

    python charge.py BUILDINGS.json SITE cycle.csv

Concentrate and anode over cathode (each the geometric middle of floor and ceiling over their literals, so 1 is the literals' ratio):
the Historical path by year, and the Random paths' spread over every path and fifth year; then a Copper Smelter's feed over its
sales (concentrate at the ceiling, anode at the floor) over every path and fifth year.
"""
import csv, json, math, sys
from collections import defaultdict

blds = json.load(open(sys.argv[1], encoding="utf-8"))
blds = {t["name"]: t for t in (blds if isinstance(blds, list) else blds.get("buildings", blds))}
site = sys.argv[2]
rows = defaultdict(dict)
for r in csv.DictReader(open(sys.argv[3])):
    if r["site"] != site: continue
    try: v = (float(r["floor"]), float(r["ceiling"]), float(r["literalExport"]), float(r["literalImport"]))
    except ValueError: continue
    rows[(r["path"], int(r["seed"]), int(r["year"]))][r["good"]] = v


def mid(v): return math.sqrt(v[0] / v[2] * v[1] / v[3]) if min(v) > 0 else float("nan")


def q(xs, p):
    xs = sorted(x for x in xs if math.isfinite(x)); i = (len(xs) - 1) * p
    lo, hi = int(math.floor(i)), int(math.ceil(i))
    return xs[lo] + (xs[hi] - xs[lo]) * (i - lo)


def over(b, g): return mid(b[g]) / mid(b["COPPER"]) if g in b and "COPPER" in b else float("nan")


print("site %s; over cathode, 1 = the literals' ratio" % site)
print("the Historical path  " + " ".join("%5d" % y for y in range(1990, 2065, 5)))
for g in ("COPPER_CONC", "ANODE"):
    print("%-20s " % g.lower() + " ".join("%5.2f" % over(rows[("HISTORICAL", 48213, y)], g) for y in range(1990, 2065, 5)))
print("the Random paths, every path and fifth year: lowest, 10th, median, 90th, highest")
for g in ("COPPER_CONC", "ANODE"):
    xs = [over(b, g) for k, b in rows.items() if k[0] == "RANDOM"]
    print("%-20s " % g.lower() + " ".join("%5.2f" % q(xs, p) for p in (0, .1, .5, .9, 1)))
t = blds["Copper Smelter"]
xs = []
for k, b in rows.items():
    try:
        num = sum(u * b[g][1] for g, u in t["uses"].items()); den = sum(u * b[g][0] for g, u in t["makes"].items())
    except KeyError: continue
    if den > 0 and math.isfinite(num / den): xs.append(num / den)
lit = rows[("HISTORICAL", 48213, 2025)]
print("a Copper Smelter's feed over its sales, every path and fifth year (%d): lowest, 5th, 25th, median, 75th, 95th, highest; the literals' %.2f" % (
    len(xs), sum(u * lit[g][3] for g, u in t["uses"].items()) / sum(u * lit[g][2] for g, u in t["makes"].items())))
print("                     " + " ".join("%5.2f" % q(xs, p) for p in (0, .05, .25, .5, .75, .95, 1)))
```

## stops.py

Which good's solve stopped at its 2,000 sweeps, on which path and how often, from the probe's logs.

```python
import sys, re, collections
for f in sys.argv[1:]:
    c = collections.Counter(); out = []
    for line in open(f, encoding="utf-8", errors="replace"):
        m = re.match(r"WorldMarket: (\w+) stopped at \d+ sweeps, clearing error (\S+), level error (\S+)", line)
        if m:
            c[(m.group(1), "NaN" if "NaN" in m.group(2) else "open")] += 1
            continue
        m = re.match(r"(HISTORICAL|RANDOM) (\d+):", line)
        if m:
            if c: out.append(m.group(1)[0] + m.group(2) + " " + " ".join(f"{g}/{k}x{n}" for (g, k), n in sorted(c.items())))
            c = collections.Counter()
    print(f.split("/")[-1], "|", "; ".join(out) if out else "none")
```

## WP1Nan.java

Where a solve leaves the numbers: the first pass whose error is not finite, on one path and good.

```java
package ham.citybuildersim;

import java.nio.file.Path;
import java.util.Locale;

/** Where a good's solve first leaves the numbers on a path: the year, the path's level, the growth of its makers and users. */
public final class WP1Nan {
    public static void main(String[] args) throws Exception {
        Path file = WorldData.locate();
        WorldMarket.Base base = WorldMarket.Base.load(file);
        long seed = Long.parseLong(args[0]);
        Good good = Good.valueOf(args[1]);
        Nations nations;
        try (WorldData data = WorldData.open(file)) {
            nations = new Nations(data, args.length > 2 ? Nations.Path.HISTORICAL : Nations.Path.RANDOM, seed);
        }
        WorldMarket m = new WorldMarket(base, nations);
        m.setSite(WorldMarket.REFERENCE_LAT, WorldMarket.REFERENCE_LON, false);
        int bad = 0;
        for (int year = 1900; year <= 2230 && bad < 3; year++) {
            m.solveYear(year);
            double[] b = m.band(good, 0, 0);
            boolean nan = Double.isNaN(b[0]) || Double.isNaN(b[1]) || m.passes(good) >= 2000;
            if (nan || year % 40 == 0) {
                double lo = Double.POSITIVE_INFINITY, hi = 0; int zero = 0, n = nations.count();
                double[] g = WorldMarket.growth(nations, year, m.baseYearRead(), n);
                for (double x : g) { if (!(x > 0)) zero++; else { lo = Math.min(lo, x); hi = Math.max(hi, x); } }
                System.out.println(String.format(Locale.ROOT, "%d %s band %.4g..%.4g passes %d; growth %.3g..%.3g, %d not positive", year, good, b[0], b[1], m.passes(good), lo, hi, zero));
                if (nan) bad++;
            }
        }
    }
}
```

## WP1Tot.java

What the anchor does beside the city's prices: a root's mean mill price over its path, and the nations' terms of trade.

```java
package ham.citybuildersim;

import java.nio.file.Path;
import java.util.Locale;

/**
 * What the path anchor does to the nations' terms of trade (WN4 reads them): a root's mean mill price over its path's level, and the terms of trade
 * of the nations named, in 2025, 2100 and 2200 on one path.   WP1Tot SEED|H GOOD,GOOD ISO,ISO
 */
public final class WP1Tot {
    public static void main(String[] args) throws Exception {
        Path file = WorldData.locate();
        WorldMarket.Base base = WorldMarket.Base.load(file);
        Nations nations;
        try (WorldData data = WorldData.open(file)) {
            nations = args[0].equals("H") ? new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED) : new Nations(data, Nations.Path.RANDOM, Long.parseLong(args[0]));
        }
        WorldMarket m = new WorldMarket(base, nations);
        m.setSite(WorldMarket.REFERENCE_LAT, WorldMarket.REFERENCE_LON, false);
        int[] pi = WorldMarket.priceIndices(nations, base.traded);
        for (int year = 1900; year <= 2200; year++) {
            m.solveYear(year);
            if (year != 2025 && year != 2100 && year != 2200) continue;
            StringBuilder out = new StringBuilder(args[0] + " " + year + " mean over path:");
            for (String key : args[1].split(",")) {
                for (int gi = 0; gi < base.traded.length; gi++) {
                    WorldMarket.Traded t = base.traded[gi];
                    if (!t.good.name().equals(key)) continue;
                    double[] p = m.phat(gi);
                    double mean = 0, low = Double.POSITIVE_INFINITY;
                    for (int i : t.makers) { mean += t.w[i] * p[i]; low = Math.min(low, p[i]); }
                    double level = WorldMarket.level(nations, pi[gi], year, m.baseYearRead());
                    out.append(String.format(Locale.ROOT, " %s %.2f", key.toLowerCase(Locale.ROOT), mean / level));
                }
            }
            out.append("; terms of trade (log):");
            double[] tot = m.lnTermsOfTrade();
            for (String iso : args[2].split(","))
                for (int i = 1; i <= nations.count(); i++)
                    if (nations.iso3(i).equals(iso)) out.append(String.format(Locale.ROOT, " %s %+.2f", iso, tot[i - 1]));
            System.out.println(out);
        }
    }
}
```

## timing.sh

The January's cost: each variant alone and in turn, twice, on the Historical path and six Random ones.

```bash
#!/usr/bin/env bash
# timing.sh   - the January's cost, each variant alone and in turn: target/wp1/timing/NAME.log (the world probe on the Historical path and Random 1..6)
set -u
T=target/wp1/timing; mkdir -p $T
CP="target/wp1/proto/pclasses;target/wp1/proto/classes;target/classes;$(cat target/classpath.txt)"
run() { n=$1; shift; java -Xmx3g -Dcbs.world="${CBS_WORLD:?the baked world file}" "$@" -cp "$CP" ham.citybuildersim.WP1Drift $T/$n.csv 1 2 3 4 5 6 > $T/$n.log 2>&1; }
for round in 1 2; do
  run main$round
  run series$round -Dwp1.chain=series
  run recipe$round -Dwp1.chain=recipe -Dwp1.own=muv
  run seriespath$round -Dwp1.chain=series -Dwp1.root=path
  run recipepath$round -Dwp1.chain=recipe -Dwp1.own=muv -Dwp1.root=path
done
python - <<'PY'
import re, glob, statistics
for f in sorted(glob.glob("target/wp1/timing/*.log")):
    cal = cold = None; colds = []; warms = []; p95 = []; worst = []; sweeps = []
    for line in open(f):
        m = re.search(r"calibrated in (\d+) ms", line)
        if m: cal = int(m.group(1))
        m = re.match(r"(\w+) \d+: the founding January ([\d.]+) ms; the \d+ after it mean ([\d.]+), median ([\d.]+), 95th ([\d.]+), worst ([\d.]+) ms; (\d+) sweeps", line)
        if m:
            colds.append(float(m.group(2))); warms.append(float(m.group(4))); p95.append(float(m.group(5))); worst.append(float(m.group(6))); sweeps.append(int(m.group(7)))
    print("%-14s calibration %5s ms; founding January: the first %.0f ms, the six after it median %.1f; a January after: median %.1f, 95th %.1f, worst %.1f ms; %d sweeps a path" % (
        f.replace("\\", "/").split("/")[-1][:-4], cal, colds[0], statistics.median(colds[1:]), statistics.median(warms), statistics.median(p95), max(worst), statistics.median(sweeps)))
PY
```

## probe.py

The city probe: CU1's `CopperProbe.java` (in [../copper-margin/probes.md](../copper-margin/probes.md)) with every good's trade each tenth year and the other chains' works counted.

```python
"""WP1's city probe: CU1's CopperProbe.java with every good's trade a year and more works counted.

    python probe.py CU1/CopperProbe.java OUT/CopperProbe.java

Adds, each tenth year, an X row a good (the year's units imported and exported and their value at the month's prices)
and counts the works of the other chains beside copper's. The rows CU1's table.py reads are unchanged.
"""
import sys
src = open(sys.argv[1], encoding="utf-8").read()


def swap(old, new):
    global src
    assert src.count(old) == 1, old
    src = src.replace(old, new)


swap('''"Copper Smelter", "Electrolytic Refinery", "Scrap Copper Refinery", "Rod Mill", "Wire & Cable Works"};''',
     '''"Copper Smelter", "Electrolytic Refinery", "Scrap Copper Refinery", "Rod Mill", "Wire & Cable Works",
            "Pelletising Plant", "Coke Ovens", "Mini Blast Furnace", "Blast Furnace", "Oxygen Steel Shop", "Electric Arc Furnace",
            "Steel Mini-Mill", "Large Mini-Mill", "Fabrication Shop", "Fabrication Works", "Machine Works", "Vehicle Works",
            "Assembly Plant", "Commercial Vehicle Plant", "Heavy Truck Plant", "Locomotive Works", "Meat Works", "Bakery",
            "Industrial Bakery", "Sawmill", "Large Sawmill", "Pulp Mill", "Paper Mill", "Cement Plant", "Large Cement Plant"};
    /** Every good's year: units imported, units exported, and their value at the month's prices. */
    private static final Map<Good, double[]> trade = new EnumMap<>(Good.class);''')
swap('''        gdp += g.getEconomyManager().getMonthGdp();
''', '''        for (Good good : Good.values()) {
            GoodsMarket mk = g.getMarkets().get(good);
            if (mk == null) continue;
            double in = mk.getImported(), out = mk.getExported();
            if (!(in > 0) && !(out > 0)) continue;
            double[] a = trade.computeIfAbsent(good, k -> new double[4]);
            if (in > 0) { a[0] += in; a[2] += in * mk.importPrice(); }
            if (out > 0) { a[1] += out; a[3] += out * mk.exportPrice(); }
        }
        gdp += g.getEconomyManager().getMonthGdp();
''')
swap('''        all.clear();
''', '''        if (m % 120 == 0) {
            for (Map.Entry<Good, double[]> e : trade.entrySet()) {
                StringBuilder r = new StringBuilder("X," + m + "," + e.getKey());
                for (double x : e.getValue()) r.append(',').append(f(x));
                csv.println(r);
            }
        }
        trade.clear();
        all.clear();
''')
open(sys.argv[2], "w", encoding="utf-8", newline="\n").write(src)
```

## run.sh

One city: `LongPlaytest` itself with the probe called after every month, on `chain.py`'s copy of `WorldMarket.java`.

```bash
#!/usr/bin/env bash
# The world prices study's runs (docs/studies/spec-world-prices.md), after CU1's run.sh.
#
#   run.sh OUT NAME [LongPlaytest's arguments...]
#
# Plays LongPlaytest itself with CopperProbe.month(g) called after every month (one line added to a copy of
# LongPlaytest.java in OUT, after g.simulateMonths(1) in run()), on chain.py's copy of WorldMarket.java and probe.py's
# copy of CU1's CopperProbe.java; the tree is not touched. MONTHS (default 2400) is -Dplaytest.months, SEED (default 0)
# -Dplaytest.seed; CBS_EARTH and CBS_WORLD name the baked data files; EXTRA carries the -Dwp1.* properties (none: the
# copy plays as main). Writes OUT/NAME.csv and OUT/NAME.log. Run from the checkout's root after batch-check.sh --fast.
# PROBE is this folder, CU1 the folder CU1's probes were written to (docs/studies/copper-margin/probes.md).
#
# The city is founded in a TempDirs folder by LongPlaytest, never the save folder.
set -euo pipefail
OUT=$1; NAME=$2; shift 2
MONTHS=${MONTHS:-2400}; SEED=${SEED:-0}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";"; mkdir -p "$OUT"; OUT=$(cygpath -m "$OUT");; esac
HERE=${PROBE:-$(dirname "$0")}; CU1=${CU1:-$HERE/../cu1}
mkdir -p "$OUT/classes" "$OUT/probe" "$OUT/src" "$OUT/tmp"
DEPS=$(cat target/classpath.txt)
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
if [ ! -f "$OUT/probe/ham/citybuildersim/CopperProbe.class" ]; then
    SRC=src/main/java/ham/citybuildersim/LongPlaytest.java
    [ "$(grep -c '^            g.simulateMonths(1);$' "$SRC")" = 1 ] || { echo "the hook line moved"; exit 2; }
    sed 's/^            g.simulateMonths(1);$/&\n            CopperProbe.month(g);/' "$SRC" > "$OUT/src/LongPlaytest.java"
    python "$HERE/probe.py" "$CU1/CopperProbe.java" "$OUT/src/CopperProbe.java"
    python "$HERE/chain.py" src/main/java/ham/citybuildersim/WorldMarket.java "$OUT/src/WorldMarket.java"
    javac -nowarn -d "$OUT/probe" -cp "$CP" "$OUT/src/LongPlaytest.java" "$OUT/src/CopperProbe.java" "$OUT/src/WorldMarket.java"
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

## runset.sh

A set of cities side by side.

```bash
#!/usr/bin/env bash
# runset.sh OUT "EXTRA java options" name:seed [name:seed ...]   - plays the set side by side after the first has compiled the probe
set -u
OUT=$1; EXTRA_OPTS=$2; shift 2
export CBS_EARTH="${CBS_EARTH:?the baked earth file}"
export CBS_WORLD="${CBS_WORLD:?the baked world file}"
export PROBE=${PROBE:-target/wp1/cu1} MONTHS=${MONTHS:-2400}
first=1
for spec in "$@"; do
  name=${spec%%:*}; seed=${spec##*:}
  ( t0=$(date +%s); SEED=$seed EXTRA="$EXTRA_OPTS" bash "$PROBE/run.sh" "$OUT" "$name"; echo "$name took $(( $(date +%s) - t0 ))s" ) &
  if [ $first = 1 ]; then
    first=0
    for i in $(seq 1 600); do [ -f "$OUT/probe/ham/citybuildersim/CopperProbe.class" ] && break; sleep 1; done
    sleep 3
  fi
done
wait
echo "set done"
```

## city.py

The cities' tables from the probe's CSVs.

```python
"""WP1's city tables: reads the CSVs run.sh writes (CU1's CopperProbe rows and probe.py's X rows).

    python city.py summary M run.csv...     one line a run at month M: people, GDP, what carries it, trade
    python city.py trade M run.csv...       the goods a city sells and buys at month M, shares of its exports and imports
    python city.py copper M run.csv...      copper's works and books at month M
    python city.py years run.csv            a run by tenth year: people, GDP a head, exports over GDP, the largest sector
"""
import sys
from collections import defaultdict

what = sys.argv[1]


def read(path):
    r = {"Y": {}, "A": defaultdict(dict), "X": defaultdict(dict), "B": defaultdict(dict), "S": defaultdict(dict)}
    for line in open(path, encoding="utf-8", errors="replace"):
        p = line.rstrip("\n").split(",")
        if p[0] == "Y": r["Y"][int(p[1])] = p[2:]
        elif p[0] in ("A", "X", "S"): r[p[0]][int(p[1])][p[2]] = [float(x) for x in p[3:]]
        elif p[0] == "B": r["B"][int(p[1])][p[2]] = int(p[3])
    return r


def name(path): return path.replace("\\", "/").split("/")[-1].replace(".csv", "")


def year(r, m):
    y = r["Y"][m]
    gdp, pop, idx = float(y[3]), float(y[4]), float(y[0])
    a = r["A"].get(m, {})
    va = sorted(((v[0] - v[1]) / gdp, k) for k, v in a.items())[::-1]
    x = r["X"].get(m, {})
    exp = sum(v[3] for v in x.values()); imp = sum(v[2] for v in x.values())
    return gdp, pop, idx, va, x, exp, imp


COPPER = ("Copper Mining", "Copper Smelting", "Copper Refining", "Copper Products")

if what == "summary":
    m = int(sys.argv[2])
    print("%-14s %8s %9s %7s %6s %6s %6s | %s" % ("run", "people", "GDP/head", "real", "copper", "exp", "imp", "the sectors that carry it (value added over GDP)"))
    for path in sys.argv[3:]:
        r = read(path)
        mm = m if m in r["Y"] else max(k for k in r["Y"] if k % 120 == 0)
        gdp, pop, idx, va, x, exp, imp = year(r, mm)
        cu = sum(s for s, k in va if k in COPPER)
        print("%-14s %8.0f %9.0f %7.1f %5.0f%% %5.0f%% %5.0f%% | %s%s" % (name(path), pop, gdp / pop, gdp / pop / idx, 100 * cu, 100 * exp / gdp, 100 * imp / gdp,
              "; ".join("%s %.0f%%" % (k[:16], 100 * s) for s, k in va[:5]), "" if mm == m else "  (m%d)" % mm))
elif what == "trade":
    m = int(sys.argv[2])
    for path in sys.argv[3:]:
        r = read(path)
        mm = m if m in r["X"] else max(r["X"])
        gdp, pop, idx, va, x, exp, imp = year(r, mm)
        e = sorted(((v[3] / exp, g) for g, v in x.items() if v[3] > 0), reverse=True)
        i = sorted(((v[2] / imp, g) for g, v in x.items() if v[2] > 0), reverse=True)
        print("%-12s sells %3.0f%% of GDP: %s" % (name(path), 100 * exp / gdp, "; ".join("%s %.0f%%" % (g.lower(), 100 * s) for s, g in e[:8])))
        print("%-12s buys  %3.0f%% of GDP: %s" % ("", 100 * imp / gdp, "; ".join("%s %.0f%%" % (g.lower(), 100 * s) for s, g in i[:8])))
elif what == "copper":
    m = int(sys.argv[2])
    for path in sys.argv[3:]:
        r = read(path)
        mm = m if m in r["Y"] else max(k for k in r["Y"] if k % 120 == 0)
        gdp = float(r["Y"][mm][3])
        b = r["B"].get(mm, {})
        a = r["A"].get(mm, {})
        line = "%-12s " % name(path)
        line += " ".join("%s %d" % (k.split()[0].lower() if k != "Wire & Cable Works" else "wire", b.get(k, 0)) for k in ("Copper Smelter", "Electrolytic Refinery", "Rod Mill", "Wire & Cable Works"))
        for k in COPPER:
            v = a.get(k)
            if v and v[0] > 0: line += " | %s feed %.0f%% net %.0f%% VA %.1f%%" % (k.split()[1][:6].lower(), 100 * v[1] / v[0], 100 * v[3] / v[0], 100 * (v[0] - v[1]) / gdp)
        print(line)
elif what == "years":
    r = read(sys.argv[2])
    for m in sorted(k for k in r["Y"] if k % 240 == 0 and k in r["A"]):
        gdp, pop, idx, va, x, exp, imp = year(r, m)
        print("%5d people %8.0f GDP/head %8.0f real %6.1f exports %3.0f%% imports %3.0f%% | %s" % (m, pop, gdp / pop, gdp / pop / idx, 100 * exp / gdp, 100 * imp / gdp,
              "; ".join("%s %.0f%%" % (k[:16], 100 * s) for s, k in va[:4])))
```

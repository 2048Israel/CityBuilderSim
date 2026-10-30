package ham.citybuildersim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The world market: each traded good's price in each of the 196 nations on a
 * smooth spatial equilibrium (a CES gravity model), solved once a year, and
 * the band the city sees in it - the import ceiling it pays and the export
 * floor it nets - which move with the tonnes the city itself trades (batch
 * WN3; docs/studies/spec-nations.md 4).
 *
 * WHY THIS EXISTS. The city's world was a price list: Good.worldImportPrice()
 * and worldExportPrice(), two literals a good, the same in every city, every
 * year, whatever the city sold (spec-nations 1.1 and 11, item 4). A city that
 * floods a market found the market where it had left it. Now the nations make
 * and use each good in the amounts BACI says (WorldData), grow on the world's
 * path (Nations), and trade as the file's kernel says they do; the city is one
 * more trader that sells where its binding suppliers' best buyers net most and buys from the
 * cheapest landed sellers first, and its tonnes move the prices it gets. The
 * game's literals stay the prices (D12): each good has a price scale and a
 * freight scale (sigma, phi) set so that a Halifax city in 2025 on the
 * Historical path, trading nothing, sees exactly today's floor and ceiling; a
 * city elsewhere, or a year later, sees what the world makes of it.
 *
 * UNITS. The model's prices are US$ a tonne (the base year's dollars times the
 * good's real price path) and its quantities tonnes a year; the city's month
 * is converted at the edge (tonnes a year = 12 x units a month over the good's
 * units a tonne) and the band leaves in the game's money, thousands of world
 * dollars a game unit, sigma times the model's.
 *
 * THE JANUARY SOLVE is the nations' alone (the city is not in it): exact hat
 * algebra on the base year's flows, per good, with each maker's own mill price
 * p-hat and one world supply scale lambda that holds the mean mill price to the
 * good's real price path (section 2). The city moves its own prices by the
 * month's step (section 4) at its volume, so the solve is a pure function of
 * (file, path, seed, year) and harness cities on one world share it.
 *
 * THE BASE is everything that is the file's: the pairs in two layouts, the
 * freight table, the nodes, the calibration. It is immutable, built once a
 * process (Base.load caches by the file's path, size and time) and shared by
 * every market on that file. A WorldMarket is one city's: its site, its berth,
 * its solved year. It is not thread-safe; the Base is.
 *
 * Nothing in the game calls this yet (WN3's first slice): WorldMarketCheck
 * holds it, on the fixture and, when it is named, on the full file.
 */
public final class WorldMarket {

    /* =====================================================================
       THE DIALS - each with its source: (est.) is the game's estimate
       ===================================================================== */

    /** How close every maker's sales must come to its supply, and the price index to its path, for a January to stop: 0.01% (spec-nations 4.3). */
    public static final double SOLVE_TOL = 1e-4;

    /** The most sweeps a good's January takes before it stops and says so in the log. */
    public static final int SOLVE_MAX_PASSES = 2_000;

    /** Supply's elasticity to a maker's own mill price (est.: the nations prototype's ES). */
    public static final double SUPPLY_ELASTICITY = .4;

    /** Demand's elasticity to a buyer's price index (est.: the nations prototype's ED). */
    public static final double DEMAND_ELASTICITY = .5;

    /** A nation's haul to itself, km by land, no handling: the city's leg to its host (est.: the prototype's). */
    public static final double HOME_KM = 300;

    /** How many other nations' nodes, nearest by great circle, a city can enter the world through, besides its host. */
    public static final int ENTRY_NODES = 6;

    /** The farthest, by great circle, a node is reached by land from the city, km; a city with a berth reaches a coastal node by sea at any distance (est.). */
    public static final double MAX_LAND_KM = 2_500;

    /** The reference city's place, Halifax: where the game's literals are the world's prices (D12). */
    public static final double REFERENCE_LAT = 44.65, REFERENCE_LON = -63.57;

    /** The year the literals are set in (the Historical path's last year of data, seed-free). */
    public static final int REFERENCE_YEAR = 2025;

    /** The solved years kept in the process-wide cache, so harness cities on one world share a January. */
    public static final int SOLVES_KEPT = 64;

    /** The Base on the full file, read and calibrated, takes at most this many milliseconds. */
    public static final double BASE_MS = 2_000;

    /** The founding solve (year 2000, every good, from p-hat = 1) takes at most this many milliseconds. */
    public static final double SOLVE_COLD_MS = 600;

    /** A January (every good, from last year's answer) takes at most this many milliseconds. */
    public static final double SOLVE_WARM_MS = 60;

    /** A month's bands for every good take at most this many microseconds. */
    public static final double BAND_MONTH_US = 200;

    /** The steps Anderson's acceleration remembers: the solve is a fixed-point iteration that closes about a tenth of its distance a pass, and the last few steps tell it where it is going. */
    static final int ACCEL_MEMORY = 5;

    /** The solve forgets what the acceleration remembers when a pass's worst error is this many times the pass before's (the model has stopped being linear there). */
    static final double ACCEL_BACK = 10;

    /** The least share of a buyer's composite a binding supplier is taken to have, so that a supplier that sells the buyer next to nothing does not make its sink's slope infinite (est.). */
    static final double SHARE_FLOOR = 1e-3;

    /** The most a maker's log price moves in one pass, whatever its imbalance (a factor of e: the first passes of a cold solve start far away). */
    static final double MAX_STEP = 1;

    /** The tolerance of the re-solve the check judges the month's step against (the nations' solve with the city's tonnes delivered). */
    static final double RESOLVE_TOL = 1e-7;

    /** The least share of a buyer's demand a delivered city can leave to the nations in the re-solve (a floor, so spending stays above zero). */
    static final double RESOLVE_FLOOR = 1e-6;

    /** The range the freight scale is looked for in, a multiple of the file's reference freight (the scan then bisects between two neighbours). */
    static final double RHO_MIN = 1e-5, RHO_MAX = 1e5;

    /** The scan's steps across RHO_MIN to RHO_MAX, and the bisection's cap. */
    static final int RHO_SCAN = 80, RHO_BISECT = 200;

    /** The freight scale a world with no good that calibrates stands on (est.: the file's reference freight as it is). */
    static final double PHI_FALLBACK = 1;

    /** How a good came by its price scale and freight scale. */
    public enum Fit {
        /** Both sides are the city's and both literals are met exactly. */
        CALIBRATED,
        /** The city has one side of it; the freight scale is its class's median and the price scale meets that side. */
        ONE_SIDED,
        /** Two sides but no freight scale reproduces their ratio; the class median stands and the price scale meets the ceiling. */
        CLASS_MEDIAN,
        /** The city has neither side (or the world lacks the good): nothing to read. */
        NONE
    }

    /* =====================================================================
       THE MATH - StrictMath, so a solve is the same on every machine
       ===================================================================== */

    private static double exp(double x) { return StrictMath.exp(x); }
    private static double log(double x) { return StrictMath.log(x); }

    /* =====================================================================
       A TRADED GOOD - the base year's pairs, in two layouts, and the good's calibration
       ===================================================================== */

    /**
     * One traded good as the file gives it, copied into arrays: the kept pairs by seller (sStart, sBuyer, sX, sPi) and by buyer (bStart,
     * bMaker, bPi), the own pair apart (ownX, ownPi), make and use thousand US$ of the base year summed from the pairs, the base tonnes,
     * and (sigma, phi) once the Base has calibrated it. Nations are 0-based here; the pairs hold no own pair and no flow of 0.
     */
    static final class Traded {
        final Good good;
        final String key, goodClass;
        final int number, n;
        final double eps, v, unitsPerTonne;
        final double[] make, use, ownX, ownPi;
        final int[] sStart, sBuyer, bStart, bMaker;
        final double[] sX, sPi, bPi;
        final int[] makers, users;
        final double[] w, s0, d0;
        double sigma = Double.NaN, phi = Double.NaN;
        Fit fit = Fit.NONE;
        /** The reference city's floor and ceiling in the game's money, 2025, as the calibration left them. */
        double refFloor = Double.NaN, refCeiling = Double.NaN;

        private Traded(WorldData data, int number, Good good, int n) {
            WorldData.TradedGood tg = data.good(number);
            this.good = good;
            this.key = tg.key();
            this.goodClass = tg.goodClass();
            this.number = number;
            this.n = n;
            this.eps = tg.elasticity();
            this.v = tg.unitValue();
            this.unitsPerTonne = tg.unitsPerTonne();
            WorldData.Flows f = data.trade(number);
            int k = f.pairs();
            make = new double[n];
            use = new double[n];
            ownX = new double[n];
            ownPi = new double[n];
            int[] sCount = new int[n + 1], bCount = new int[n + 1];
            boolean[] keep = new boolean[k];
            for (int p = 0; p < k; p++) {
                int s = f.seller[p] - 1, b = f.buyer[p] - 1;
                double x = f.flow[p];
                if (s < 0 || s >= n || b < 0 || b >= n || !(x > 0) || !Double.isFinite(x)) continue;
                keep[p] = true;
                make[s] += x;
                use[b] += x;
                if (s == b) {
                    ownX[s] += x;
                } else {
                    sCount[s + 1]++;
                    bCount[b + 1]++;
                }
            }
            for (int i = 0; i < n; i++) {
                sCount[i + 1] += sCount[i];
                bCount[i + 1] += bCount[i];
            }
            int m = sCount[n];
            sStart = sCount;
            bStart = bCount;
            sBuyer = new int[m];
            sX = new double[m];
            sPi = new double[m];
            bMaker = new int[m];
            bPi = new double[m];
            int[] sFill = Arrays.copyOf(sCount, n), bFill = Arrays.copyOf(bCount, n);
            for (int p = 0; p < k; p++) {
                if (!keep[p]) continue;
                int s = f.seller[p] - 1, b = f.buyer[p] - 1;
                if (s == b) continue;
                double pi = f.flow[p] / use[b];
                sBuyer[sFill[s]] = b;
                sX[sFill[s]] = f.flow[p];
                sPi[sFill[s]++] = pi;
                bMaker[bFill[b]] = s;
                bPi[bFill[b]++] = pi;
            }
            int nm = 0, nu = 0;
            double total = 0;
            for (int i = 0; i < n; i++) {
                if (use[i] > 0) ownPi[i] = ownX[i] / use[i];
                if (make[i] > 0) { nm++; total += make[i]; }
                if (use[i] > 0) nu++;
            }
            makers = new int[nm];
            users = new int[nu];
            w = new double[n];
            s0 = new double[n];
            d0 = new double[n];
            nm = 0;
            nu = 0;
            for (int i = 0; i < n; i++) {
                if (make[i] > 0) {
                    makers[nm++] = i;
                    w[i] = make[i] / total;
                    s0[i] = make[i] * 1000 / v;
                }
                if (use[i] > 0) {
                    users[nu++] = i;
                    d0[i] = use[i] * 1000 / v;
                }
            }
        }

        /** The good as the file has it, or null when it is not one the market models (a key that is no Good, a unit value or units a tonne that is no number above 0, no maker or no user). */
        static Traded of(WorldData data, int number, int n) {
            WorldData.TradedGood tg = data.good(number);
            Good good;
            try {
                good = Good.valueOf(tg.key());
            } catch (IllegalArgumentException e) {
                return null;
            }
            if (!(tg.unitValue() > 0) || !Double.isFinite(tg.unitValue()) || !(tg.unitsPerTonne() > 0) || !Double.isFinite(tg.unitsPerTonne())
                    || !(tg.elasticity() > 0) || !Double.isFinite(tg.elasticity())) return null;
            Traded t = new Traded(data, number, good, n);
            return t.makers.length == 0 || t.users.length == 0 ? null : t;
        }
    }

    /* =====================================================================
       THE LEGS - the city's freight to the nations, from where it stands
       ===================================================================== */

    /**
     * What a site's freight comes to: its host nation (1-based), whether it has a berth, fc[j] the US$ a tonne from the city to nation j (the
     * cheapest way in), and fp[k * n + j] the nations' freight in the city's view, min(f[k][j], fc[k] + fc[j]), which is what keeps the floor under the ceiling.
     */
    record Legs(int host, boolean berth, double[] fc, double[] fp) { }

    /* =====================================================================
       THE BASE - the file's, immutable, once a process
       ===================================================================== */

    /**
     * What the file is to the market, copied into arrays: the nations and their nodes, the freight in US$ a tonne, every traded good's pairs and
     * its calibration. Built from an open WorldData; it outlives the file being closed except for one thing, the host of a site, which reads the
     * owner raster (the Base built by load keeps its file open for that, 64 tiles and a handle; one built by of(data) reads the data it was given,
     * which has to stay open while sites are set, and a closed one falls back to the nearest node).
     */
    public static final class Base {
        private static final Map<String, Base> LOADED = new HashMap<>();
        private static final Map<WorldData, Base> OF = new IdentityHashMap<>();

        final int n;
        final String[] iso, name;
        final double[] lat, lon, baseOutput;
        final boolean[] landlocked;
        final int baseYear;
        final double radius, seaUsd, landUsd, handlingUsd, landDetour, seaDetour;
        /** US$ a tonne between nations, row-major (from * n + to), 0 on the diagonal. */
        final double[] freight;
        /** For an owner number above n (an area), the nation that sells its land, 1-based, or 0. */
        final int[] areaSeller;
        final Traded[] traded;
        final int[] indexOf;
        final int refHost;
        final Legs reference;
        final long buildMillis;
        private final WorldData owners;
        /** How many goods calibrated, and how many were one-sided, fell back to the class median, or have no side. */
        final int[] fits = new int[Fit.values().length];

        /** Reads the file and keeps it open (the host lookups need it); one Base per file path, size and time, for the process. */
        public static synchronized Base load(Path file) throws IOException {
            Path abs = file.toAbsolutePath().normalize();
            String key = abs + "|" + Files.size(abs) + "|" + Files.getLastModifiedTime(abs).toMillis();
            Base cached = LOADED.get(key);
            if (cached != null) return cached;
            long t0 = System.nanoTime();
            WorldData data = WorldData.open(abs);
            Base b = new Base(data, t0);
            LOADED.put(key, b);
            return b;
        }

        /** A Base from an open file, one per file object for the process. The data stays the caller's to close once no site will be set (a closed one gives hosts by the nearest node). */
        public static Base of(WorldData data) {
            synchronized (OF) {
                Base cached = OF.get(data);
                if (cached == null) {
                    cached = new Base(data, System.nanoTime());
                    OF.put(data, cached);
                }
                return cached;
            }
        }

        private Base(WorldData data, long t0) {
            this.owners = data;
            n = data.nationCount();
            iso = new String[n];
            name = new String[n];
            lat = new double[n];
            lon = new double[n];
            landlocked = new boolean[n];
            baseOutput = new double[n];
            baseYear = data.tradeBaseYear();
            radius = dial(data, "EARTH_RADIUS_KM", 6371);
            seaUsd = dial(data, "SEA_USD_TKM", .003);
            landUsd = dial(data, "LAND_USD_TKM", .05);
            handlingUsd = dial(data, "HANDLING_USD_T", 10);
            landDetour = dial(data, "LAND_DETOUR", 1.3);
            seaDetour = dial(data, "SEA_DETOUR", 1.25);
            for (int i = 0; i < n; i++) {
                WorldData.Nation nation = data.nation(i + 1);
                iso[i] = nation.iso3();
                name[i] = nation.name();
                lat[i] = nation.nodeLat();
                lon[i] = nation.nodeLon();
                landlocked[i] = nation.landlocked();
                double out = data.pathValue("gdp_2015usd", i + 1, baseYear);
                baseOutput[i] = out > 0 && Double.isFinite(out) ? out : 0;
            }
            areaSeller = new int[data.nations().size() + 1];
            for (int number = n + 1; number <= data.nations().size(); number++) {
                String seller = data.nation(number).seller();
                int s = seller == null ? 0 : data.numberOf(seller);
                areaSeller[number] = s >= 1 && s <= n ? s : 0;
            }
            WorldData.Freight f = data.freight();
            freight = new double[n * n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int at = i * n + j;
                    double sea = f.sea[at], land = f.land[at];
                    freight[at] = i == j ? 0 : seaUsd * sea + landUsd * land + handlingUsd;
                    if (!Double.isFinite(freight[at])) freight[at] = Double.POSITIVE_INFINITY;
                }
            }
            List<Traded> list = new ArrayList<>();
            indexOf = new int[Good.values().length];
            Arrays.fill(indexOf, -1);
            for (int number = 1; number <= data.goodCount(); number++) {
                Traded t = Traded.of(data, number, n);
                if (t == null) continue;
                indexOf[t.good.ordinal()] = list.size();
                list.add(t);
            }
            traded = list.toArray(new Traded[0]);
            refHost = hostOf(REFERENCE_LAT, REFERENCE_LON);
            reference = legs(REFERENCE_LAT, REFERENCE_LON, false, refHost);
            calibrate(data);
            buildMillis = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("WorldMarket: " + data.fileName() + " read and calibrated: " + n + " nations, " + traded.length + " goods ("
                    + fits[Fit.CALIBRATED.ordinal()] + " calibrated, " + fits[Fit.ONE_SIDED.ordinal()] + " one-sided, " + fits[Fit.CLASS_MEDIAN.ordinal()]
                    + " on their class's median)");
        }

        private static double dial(WorldData data, String name, double fallback) {
            try {
                return data.dial(name);
            } catch (IllegalArgumentException e) {
                return fallback;
            }
        }

        /* ----- what a caller reads ----- */

        /** How many nations there are (the file's N). */
        public int nationCount() { return n; }

        /** The goods the market models, in the file's order. */
        public List<Good> goods() {
            List<Good> list = new ArrayList<>();
            for (Traded t : traded) list.add(t.good);
            return list;
        }

        /** Whether the file has this good, traded, scaled, and the city has a side of it to trade. */
        public boolean trades(Good g) {
            int gi = indexOf[g.ordinal()];
            return gi >= 0 && (g.importable() || g.exportable()) && !Double.isNaN(traded[gi].sigma);
        }

        /** How a good's scales were set. */
        public Fit fit(Good g) {
            int gi = indexOf[g.ordinal()];
            return gi < 0 ? Fit.NONE : traded[gi].fit;
        }

        /** A good's price scale: thousands of world dollars a game unit for a dollar a tonne; NaN for a good the market does not model. */
        public double sigma(Good g) {
            int gi = indexOf[g.ordinal()];
            return gi < 0 ? Double.NaN : traded[gi].sigma;
        }

        /** A good's freight scale: the multiple of the file's reference freight the city's view of it uses. */
        public double phi(Good g) {
            int gi = indexOf[g.ordinal()];
            return gi < 0 ? Double.NaN : traded[gi].phi;
        }

        /** The reference city's floor, 2025, Historical, in the game's money: the literal export price for a good that calibrated. */
        public double referenceFloor(Good g) {
            int gi = indexOf[g.ordinal()];
            return gi < 0 ? Double.NaN : traded[gi].refFloor;
        }

        /** The reference city's ceiling likewise. */
        public double referenceCeiling(Good g) {
            int gi = indexOf[g.ordinal()];
            return gi < 0 ? Double.NaN : traded[gi].refCeiling;
        }

        /** The time the Base took to build, milliseconds. */
        public long buildMillis() { return buildMillis; }

        /** The nation the reference city (Halifax) stands in, 1-based. */
        public int referenceHost() { return refHost; }

        /* ----- places ----- */

        /** The great circle distance in km between two points. */
        double gc(double lat1, double lon1, double lat2, double lon2) {
            double p1 = StrictMath.toRadians(lat1), p2 = StrictMath.toRadians(lat2);
            double dp = p2 - p1, dl = StrictMath.toRadians(lon2 - lon1);
            double s = StrictMath.sin(dp / 2), t = StrictMath.sin(dl / 2);
            double a = s * s + StrictMath.cos(p1) * StrictMath.cos(p2) * t * t;
            return 2 * radius * StrictMath.asin(Math.min(1, StrictMath.sqrt(a)));
        }

        /** The nation whose node is nearest a point, 1-based. */
        int nearest(double la, double lo) {
            int best = 0;
            double bestKm = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                double km = gc(la, lo, lat[i], lon[i]);
                if (km < bestKm) {
                    bestKm = km;
                    best = i;
                }
            }
            return best + 1;
        }

        /** The host of a site: the owner of the point when that is a nation, the nation that sells an area's land when it is one, else the nation with the nearest node (1-based). */
        public int hostOf(double la, double lo) {
            int owner = 0;
            try {
                owner = owners.ownerAt(la, lo);
            } catch (RuntimeException e) {
                owner = 0;
            }
            if (owner >= 1 && owner <= n) return owner;
            if (owner > n && owner < areaSeller.length && areaSeller[owner] > 0) return areaSeller[owner];
            return nearest(la, lo);
        }

        /**
         * A generated world's stand-in (D23): a coastal nation drawn by the seed, weighted by its output in the base year, which is where the world's trade is
         * (a uniform draw lands on a remote island one time in five). 1-based; a pure function of the seed.
         */
        public int standIn(long groundSeed) {
            double total = 0;
            for (int i = 0; i < n; i++) if (!landlocked[i]) total += baseOutput[i];
            if (!(total > 0)) {
                for (int i = 0; i < n; i++) if (!landlocked[i]) return i + 1;
                return 1;
            }
            double x = new Random(Nations.scramble(groundSeed, 0, 0, STAND_IN)).nextDouble() * total, run = 0;
            int last = 0;
            for (int i = 0; i < n; i++) {
                if (landlocked[i] || !(baseOutput[i] > 0)) continue;
                run += baseOutput[i];
                last = i;
                if (x < run) return i + 1;
            }
            return last + 1;
        }

        /** The legs of a site at (la, lo) in a host nation, with or without a berth (see Legs). */
        Legs legs(double la, double lo, boolean berth, int host) {
            double[] e = new double[n];
            Arrays.fill(e, Double.POSITIVE_INFINITY);
            e[host - 1] = HOME_KM * landUsd;
            double[] km = new double[n];
            Integer[] order = new Integer[n];
            for (int i = 0; i < n; i++) {
                km[i] = i == host - 1 ? Double.POSITIVE_INFINITY : gc(la, lo, lat[i], lon[i]);
                order[i] = i;
            }
            Arrays.sort(order, (x, y) -> km[x] != km[y] ? Double.compare(km[x], km[y]) : Integer.compare(x, y));
            for (int r = 0; r < Math.min(ENTRY_NODES, n - 1); r++) {
                int m = order[r];
                double byLand = km[m] <= MAX_LAND_KM ? km[m] * landDetour * landUsd : Double.POSITIVE_INFINITY;
                double bySea = berth && !landlocked[m] ? km[m] * seaDetour * seaUsd : Double.POSITIVE_INFINITY;
                double c = Math.min(byLand, bySea);
                if (c < Double.POSITIVE_INFINITY) e[m] = Math.min(e[m], c + handlingUsd);
            }
            double[] fc = new double[n];
            Arrays.fill(fc, Double.POSITIVE_INFINITY);
            for (int m = 0; m < n; m++) {
                if (e[m] == Double.POSITIVE_INFINITY) continue;
                for (int j = 0; j < n; j++) {
                    double c = e[m] + freight[m * n + j];
                    if (c < fc[j]) fc[j] = c;
                }
            }
            double[] fp = new double[n * n];
            for (int k = 0; k < n; k++) {
                for (int j = 0; j < n; j++) {
                    double through = fc[k] + fc[j];
                    double direct = freight[k * n + j];
                    fp[k * n + j] = Math.min(direct, through);
                }
            }
            return new Legs(host, berth, fc, fp);
        }

        /* ----- the calibration ----- */

        /**
         * Sets every good's (sigma, phi): the reference city (Halifax, no berth) in REFERENCE_YEAR on the Historical path, trading nothing, sees
         * floor = the literal export price and ceiling = the literal import price. The ceiling over the floor rises with phi from 1, so phi is
         * found by a scan and a bisection on its log and sigma follows; one-sided goods and goods that will not calibrate take their class's median.
         */
        private void calibrate(WorldData data) {
            Nations hist = new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
            double[] gHat = growth(hist, REFERENCE_YEAR, baseYear, n);
            int[] priceIdx = priceIndices(hist, traded);
            Work work = new Work(n);
            double[][] kept = new double[traded.length][];
            double[] phat = new double[n];
            double[] p = new double[n];
            double[] lam = new double[1];
            for (int gi = 0; gi < traded.length; gi++) {
                Traded t = traded[gi];
                Arrays.fill(phat, 1);
                lam[0] = 1;
                solve(t, gHat, null, true, level(hist, priceIdx[gi], REFERENCE_YEAR, baseYear), SOLVE_TOL, phat, lam, 0, work);
                for (int i = 0; i < n; i++) p[i] = t.v * phat[i];
                Good g = t.good;
                boolean imp = g.importable(), exp = g.exportable();
                if (imp && exp) {
                    double target = g.worldImportPrice() / g.worldExportPrice();
                    double rho = target > 1 ? findRho(t, p, target) : Double.NaN;
                    if (rho > 0) {
                        t.phi = rho;
                        t.fit = Fit.CALIBRATED;
                    } else {
                        t.fit = Fit.CLASS_MEDIAN;
                    }
                } else {
                    t.fit = imp || exp ? Fit.ONE_SIDED : Fit.NONE;
                }
                kept[gi] = p.clone();
            }
            Map<String, Double> medians = new HashMap<>();
            Map<String, List<Double>> byClass = new HashMap<>();
            List<Double> all = new ArrayList<>();
            for (Traded t : traded) {
                if (t.fit == Fit.CALIBRATED) {
                    byClass.computeIfAbsent(t.goodClass, c -> new ArrayList<>()).add(t.phi);
                    all.add(t.phi);
                }
            }
            double overall = all.isEmpty() ? PHI_FALLBACK : median(all);
            for (Map.Entry<String, List<Double>> e : byClass.entrySet()) medians.put(e.getKey(), median(e.getValue()));
            for (int gi = 0; gi < traded.length; gi++) {
                Traded t = traded[gi];
                if (t.fit == Fit.NONE) continue;
                if (t.fit != Fit.CALIBRATED) t.phi = medians.getOrDefault(t.goodClass, overall);
                double[] fl = referenceBand(t, kept[gi], t.phi);
                Good g = t.good;
                if (t.fit == Fit.CALIBRATED) {
                    t.sigma = g.worldExportPrice() / fl[0];
                } else if (g.importable()) {
                    t.sigma = g.worldImportPrice() / fl[1];
                } else {
                    t.sigma = fl[0] > 0 ? g.worldExportPrice() / fl[0] : Double.NaN;
                }
                if (!(t.sigma > 0) || !Double.isFinite(t.sigma)) {
                    t.sigma = Double.NaN;
                    t.fit = Fit.NONE;
                    continue;
                }
                t.refFloor = t.sigma * Math.max(0, fl[0]);
                t.refCeiling = t.sigma * Math.max(Math.max(0, fl[0]), fl[1]);
            }
            for (Traded t : traded) fits[t.fit.ordinal()]++;
        }

        /** The reference city's {floor, ceiling} in US$ a tonne at freight scale rho, for prices p (makers' mill prices): the best netback and the cheapest landed cost, with nothing traded. */
        double[] referenceBand(Traded t, double[] p, double rho) {
            double[] fc = reference.fc(), fp = reference.fp();
            double floor = Double.NEGATIVE_INFINITY, ceiling = Double.POSITIVE_INFINITY;
            double[] cheapest = new double[n];
            Arrays.fill(cheapest, Double.POSITIVE_INFINITY);
            for (int k : t.makers) {
                double pk = p[k];
                int row = k * n;
                for (int j : t.users) {
                    double c = pk + rho * fp[row + j];
                    if (c < cheapest[j]) cheapest[j] = c;
                }
                double landed = pk + rho * fc[k];
                if (landed < ceiling) ceiling = landed;
            }
            for (int j : t.users) {
                if (cheapest[j] == Double.POSITIVE_INFINITY) continue;
                double net = cheapest[j] - rho * fc[j];
                if (net > floor) floor = net;
            }
            return new double[] { floor, ceiling };
        }

        /** The freight scale at which the reference city's ceiling over its floor is target, or NaN when none in RHO_MIN..RHO_MAX does. */
        private double findRho(Traded t, double[] p, double target) {
            double lnMin = log(RHO_MIN), lnMax = log(RHO_MAX);
            double prev = lnMin;
            for (int s = 1; s <= RHO_SCAN; s++) {
                double cur = lnMin + (lnMax - lnMin) * s / RHO_SCAN;
                if (ratio(t, p, exp(cur)) >= target) {
                    double lo = prev, hi = cur;
                    for (int it = 0; it < RHO_BISECT; it++) {
                        double mid = .5 * (lo + hi);
                        if (mid == lo || mid == hi) break;
                        if (ratio(t, p, exp(mid)) >= target) hi = mid; else lo = mid;
                    }
                    return exp(hi);
                }
                prev = cur;
            }
            return Double.NaN;
        }

        private double ratio(Traded t, double[] p, double rho) {
            double[] b = referenceBand(t, p, rho);
            return b[0] > 0 ? b[1] / b[0] : Double.POSITIVE_INFINITY;
        }

        private static double median(List<Double> values) {
            List<Double> sorted = new ArrayList<>(values);
            sorted.sort(null);
            int m = sorted.size();
            return m % 2 == 1 ? sorted.get(m / 2) : .5 * (sorted.get(m / 2 - 1) + sorted.get(m / 2));
        }
    }

    /** Draws a stand-in's nation (Nations.scramble's "what"). */
    private static final long STAND_IN = 0x57A4D1;

    /* =====================================================================
       THE YEAR'S INPUTS - growth and the price path, from the nations
       ===================================================================== */

    /** Each nation's output in a year over its output in the base year; 1 where either is no number above 0. */
    static double[] growth(Nations nations, int year, int baseYear, int n) {
        double[] g = new double[n];
        for (int i = 0; i < n; i++) {
            double o = nations.year(i + 1, year).output(), b = nations.year(i + 1, baseYear).output();
            g[i] = o > 0 && b > 0 && Double.isFinite(o) && Double.isFinite(b) ? o / b : 1;
        }
        return g;
    }

    /** For each modelled good, its number in the nations' price layer (1-based), 0 where it has none. */
    static int[] priceIndices(Nations nations, Traded[] traded) {
        int[] idx = new int[traded.length];
        for (int gi = 0; gi < traded.length; gi++) {
            for (int g = 1; g <= nations.goodCount(); g++) {
                if (nations.goodKey(g).equals(traded[gi].key)) {
                    idx[gi] = g;
                    break;
                }
            }
        }
        return idx;
    }

    /** The mean mill price's target in a year over the base year's: the good's real price path, 1 where it has none. */
    static double level(Nations nations, int priceIdx, int year, int baseYear) {
        if (priceIdx <= 0) return 1;
        double now = nations.goodPrice(priceIdx, year), then = nations.goodPrice(priceIdx, baseYear);
        double t = now / then;
        return t > 0 && Double.isFinite(t) ? t : 1;
    }

    /* =====================================================================
       THE SOLVE - one good's January, the nations alone
       ===================================================================== */

    /** One solve's scratch, sized for n nations: reused across goods, never kept. */
    static final class Work {
        final double[] u, a, phi, h, x, g, lnMake;
        final Accel accel;
        /** The worst clearing error and the level error at the last pass, for the log. */
        double worst, levelError;

        Work(int n) {
            u = new double[n];
            a = new double[n];
            phi = new double[n];
            h = new double[n];
            lnMake = new double[n];
            x = new double[n + 1];
            g = new double[n + 1];
            accel = new Accel(n + 1);
        }
    }

    /**
     * One good's solve, in place: phat[0..n) is each nation's mill price over the base year's and lam[lamAt] the world's supply scale, both the start
     * and the answer. The unknowns are the makers' p-hat and lambda. With a = p-hat^-eps, a buyer's index change is Phi_j = sum_i pi_ij a_i, its
     * spending over the index (h_j) is gHat_j Phi_j^-kappa with kappa = (eps + 1 - ED) / eps, a maker's sales are a_i sum_j x_ij h_j and its supply
     * make_i gHat_i lambda p-hat_i^(1 + ES); the answer has every maker's sales within tol of its supply and, with pin, the mean mill price within tol
     * of level, lambda carrying the level (a dearer world supplies less).
     *
     * THE ITERATION. A pass takes the prices as they stand, works out every buyer's index and every maker's imbalance, and moves each maker's log price
     * by the Newton step that clears its own sales with the others' sales held - the step knows the maker's own pair, whose share of its own market's
     * index its price moves - and lambda by (index / level)^(ES + ED). That map closes only about a tenth of the distance a pass (the makers sell the
     * same tonnes to the same buyers, so a price moves what the others would have), and WN0 found plain Sinkhorn needs hundreds to thousands of sweeps
     * on this file; Anderson's acceleration (Accel) over the vector of log prices and lambda brings it to ten or so. The answer is whatever state the
     * last pass started from, so what is judged is the state returned.
     *
     * With pin false (the check's re-solve) lambda stays and the level is free; carve (nullable) is the share of each buyer's base demand the city has
     * taken, which leaves the nations' spending P-hat (gHat P-hat^-ED - carve). Returns the passes; the last errors are left in work.
     */
    static int solve(Traded t, double[] gHat, double[] carve, boolean pin, double level, double tol,
                     double[] phat, double[] lam, int lamAt, Work work) {
        final double eps = t.eps, kap = (eps + 1 - DEMAND_ELASTICITY) / eps, rho = 1 + 1 / eps, coef = eps + 1 + SUPPLY_ELASTICITY;
        final double levelPower = SUPPLY_ELASTICITY + DEMAND_ELASTICITY;
        final int[] makers = t.makers, users = t.users;
        final int mk = makers.length, dim = mk + (pin ? 1 : 0);
        final double[] u = work.u, a = work.a, phi = work.phi, h = work.h, x = work.x, g = work.g, lnMake = work.lnMake;
        final double fHigh = Math.log1p(tol), fLow = Math.log1p(-tol);
        Arrays.fill(a, 0);
        Arrays.fill(h, 0);
        Arrays.fill(phi, 0);
        for (int k = 0; k < mk; k++) {
            x[k] = log(phat[makers[k]]);
            lnMake[k] = log(t.make[makers[k]] * gHat[makers[k]]);
        }
        final double fixedLnLambda = log(lam[lamAt]);
        if (pin) x[mk] = fixedLnLambda;
        work.accel.reset();
        int pass = 0;
        double lastWorst = Double.POSITIVE_INFINITY;
        while (true) {
            // the state the vector x says: prices, the index each buyer faces and its spending over it
            double lnLambda = pin ? x[mk] : fixedLnLambda, idx = 0;
            for (int k = 0; k < mk; k++) {
                int i = makers[k];
                u[i] = x[k];
                a[i] = exp(-eps * u[i]);
                phat[i] = exp(u[i]);
                idx += t.w[i] * phat[i];
            }
            for (int j : users) {
                double s = t.ownPi[j] * a[j];
                for (int q = t.bStart[j]; q < t.bStart[j + 1]; q++) s += t.bPi[q] * a[t.bMaker[q]];
                phi[j] = s;
                double lp = log(s);
                double e1 = gHat[j] * exp(-kap * lp);
                double c = carve == null ? 0 : carve[j];
                h[j] = c > 0 ? Math.max(e1 - c * exp(-rho * lp), RESOLVE_FLOOR * e1) : e1;
            }
            // every maker's sales over its supply (in logs), the others' sales held, and the step that clears it
            double fMax = Double.NEGATIVE_INFINITY, fMin = Double.POSITIVE_INFINITY;
            for (int k = 0; k < mk; k++) {
                int i = makers[k];
                double rr = 0;
                for (int q = t.sStart[i]; q < t.sStart[i + 1]; q++) rr += t.sX[q] * h[t.sBuyer[q]];
                double own = t.ownX[i] * h[i], tot = rr + own;
                double f0 = log(tot) - lnMake[k] - lnLambda - coef * u[i];
                fMax = Math.max(fMax, f0);
                fMin = Math.min(fMin, f0);
                // Newton on the maker's own log price: sales over supply falls with it by coef, less the part of its own sales that its own price lifts
                // through the index of its own market (the own pair's share of Phi, kappa eps times the share of its sales at home)
                double df = -coef;
                if (t.ownPi[i] > 0) df += kap * eps * (own / tot) * (t.ownPi[i] * a[i] / phi[i]);
                if (!(df < -1e-3)) df = -coef;
                double un = u[i] - Math.max(-MAX_STEP, Math.min(MAX_STEP, f0 / df));
                g[k] = un;
            }
            double worst = Math.max(Math.expm1(fMax), -Math.expm1(fMin));
            double levelError = pin ? Math.abs(idx / level - 1) : 0;
            if (pin) g[mk] = x[mk] + levelPower * log(idx / level);
            work.worst = worst;
            work.levelError = levelError;
            if (fMax <= fHigh && fMin >= fLow && levelError <= tol) break;
            if (pass >= SOLVE_MAX_PASSES) {
                System.out.println("WorldMarket: " + t.key + " stopped at " + SOLVE_MAX_PASSES + " sweeps, clearing error " + worst + ", level error " + levelError);
                break;
            }
            double size = Math.max(worst, levelError);
            if (size > ACCEL_BACK * lastWorst) work.accel.reset();
            lastWorst = size;
            work.accel.mix(x, g, dim);
            pass++;
        }
        if (pin) lam[lamAt] = exp(x[mk]);
        return pass;
    }

    /**
     * Anderson acceleration of a fixed-point iteration x -> g(x): it keeps the last few steps and moves x to the point their linear model says has no
     * residual (the least-squares combination of the last differences), which turns a map that closes a tenth of its distance a pass into one that
     * closes most of it in a handful.
     */
    static final class Accel {
        final int m = ACCEL_MEMORY;
        final double[][] dF, dG;
        final double[] fPrev, gPrev, f, gamma, atb;
        final double[][] ata;
        int cols, next;
        boolean have;

        Accel(int maxDim) {
            dF = new double[m][maxDim];
            dG = new double[m][maxDim];
            fPrev = new double[maxDim];
            gPrev = new double[maxDim];
            f = new double[maxDim];
            gamma = new double[m];
            atb = new double[m];
            ata = new double[m][m];
        }

        void reset() {
            cols = 0;
            next = 0;
            have = false;
        }

        /** Replaces x with the next iterate, given g = G(x). */
        void mix(double[] x, double[] g, int dim) {
            for (int k = 0; k < dim; k++) f[k] = g[k] - x[k];
            if (m > 0 && have) {
                double[] cf = dF[next], cg = dG[next];
                for (int k = 0; k < dim; k++) {
                    cf[k] = f[k] - fPrev[k];
                    cg[k] = g[k] - gPrev[k];
                }
                next = (next + 1) % m;
                cols = Math.min(cols + 1, m);
            }
            System.arraycopy(f, 0, fPrev, 0, dim);
            System.arraycopy(g, 0, gPrev, 0, dim);
            have = true;
            if (cols == 0) {
                System.arraycopy(g, 0, x, 0, dim);
                return;
            }
            for (int p = 0; p < cols; p++) {
                for (int q = p; q < cols; q++) {
                    double s = 0;
                    for (int k = 0; k < dim; k++) s += dF[p][k] * dF[q][k];
                    ata[p][q] = ata[q][p] = s;
                }
                double s = 0;
                for (int k = 0; k < dim; k++) s += dF[p][k] * f[k];
                atb[p] = s;
            }
            double reg = 0;
            for (int p = 0; p < cols; p++) reg = Math.max(reg, ata[p][p]);
            reg *= 1e-10;
            for (int p = 0; p < cols; p++) ata[p][p] += reg;
            if (!cholesky(cols)) {
                reset();
                System.arraycopy(g, 0, x, 0, dim);
                return;
            }
            for (int k = 0; k < dim; k++) {
                double s = g[k];
                for (int p = 0; p < cols; p++) s -= gamma[p] * dG[p][k];
                x[k] = s;
            }
        }

        /** Solves ata gamma = atb for the first n unknowns by Cholesky; false if the matrix is not positive definite. */
        private boolean cholesky(int n) {
            for (int p = 0; p < n; p++) {
                for (int q = 0; q <= p; q++) {
                    double s = ata[p][q];
                    for (int k = 0; k < q; k++) s -= ata[p][k] * ata[q][k];
                    if (p == q) {
                        if (!(s > 0)) return false;
                        ata[p][p] = Math.sqrt(s);
                    } else {
                        ata[p][q] = s / ata[q][q];
                    }
                }
            }
            for (int p = 0; p < n; p++) {
                double s = atb[p];
                for (int k = 0; k < p; k++) s -= ata[p][k] * gamma[k];
                gamma[p] = s / ata[p][p];
            }
            for (int p = n - 1; p >= 0; p--) {
                double s = gamma[p];
                for (int k = p + 1; k < n; k++) s -= ata[k][p] * gamma[k];
                gamma[p] = s / ata[p][p];
            }
            return true;
        }
    }

    /* =====================================================================
       THE MARKET - one city's site, solved year and band
       ===================================================================== */

    /** A solved year kept for the next city on the same world: the answer and how many sweeps each good took. */
    private record Solved(double[] prices, double[] scales, int[] passes) { }

    /** What a solve is a function of: the file, the path and seed, the year, and the state it started from (its year and a fingerprint of its bits; -1 and 0 for cold). */
    private record SolveKey(Base base, Nations.Path path, long seed, int year, int warmYear, long start) { }

    private static final Map<SolveKey, Solved> SOLVES = new LinkedHashMap<>(16, .75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<SolveKey, Solved> eldest) { return size() > SOLVES_KEPT; }
    };

    /**
     * A water-filling with caps: sinks sorted by netback, best first, each with a slope (tonnes a year per US$ a tonne its netback may fall) and a cap
     * (the most tonnes a year it takes). At a level lam sink k takes min(cap, slope (net - lam)+), and the level for x tonnes is the lam at which the
     * sinks together take x. The total is piecewise linear in lam, bending where a sink joins (lam = its netback) and where it fills (lam = its netback
     * less cap over slope), so those events are sorted once and the level for any x is a binary search and a division. Past every cap the level is
     * -infinity. The purchase side uses it with the landed costs negated.
     */
    static final class Levels {
        final int len;
        final double[] net, slope, cap;
        private final int[] joinEvent;
        private final double[] evLam, evT, evSm, evSmn, evSc;

        Levels(double[] net, double[] slope, double[] cap, int len) {
            this.len = len;
            this.net = net;
            this.slope = slope;
            this.cap = cap;
            joinEvent = new int[len];
            int events = 2 * len;
            evLam = new double[events];
            evT = new double[events];
            evSm = new double[events];
            evSmn = new double[events];
            evSc = new double[events];
            double[] key = new double[events];
            int[] order = new int[events];
            for (int k = 0; k < len; k++) {
                key[k] = net[k];
                key[len + k] = net[k] - cap[k] / slope[k];
            }
            for (int e = 0; e < events; e++) order[e] = e;
            sortBy(order, events, key, true);
            double sm = 0, smn = 0, sc = 0, total = 0;
            for (int e = 0; e < events; e++) {
                int id = order[e], k = id < len ? id : id - len;
                total = Math.max(total, smn - sm * key[id] + sc);
                if (id < len) {
                    sm += slope[k];
                    smn += slope[k] * net[k];
                    joinEvent[k] = e;
                } else {
                    sm -= slope[k];
                    smn -= slope[k] * net[k];
                    sc += cap[k];
                }
                evLam[e] = key[id];
                evT[e] = total;
                evSm[e] = sm;
                evSmn[e] = smn;
                evSc[e] = sc;
            }
        }

        /** The most the sinks take together, tonnes a year. */
        double capacity() { return len == 0 ? 0 : evT[evT.length - 1]; }

        /** The level at which the sinks take x tonnes a year: the best netback for 0, -infinity past the caps. */
        double level(double x) {
            if (len == 0) return Double.NaN;
            if (!(x > 0)) return evLam[0];
            int lo = 0, hi = evT.length - 1;
            if (x > evT[hi]) return Double.NEGATIVE_INFINITY;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (evT[mid] >= x) hi = mid; else lo = mid + 1;
            }
            double sm = evSm[lo - 1];
            double lam = sm > 0 ? (evSmn[lo - 1] + evSc[lo - 1] - x) / sm : evLam[lo];
            return Math.min(evLam[lo - 1], Math.max(evLam[lo], lam));
        }

        /** What sink k takes at a level, tonnes a year. */
        double take(int k, double lam) {
            double v = slope[k] * (net[k] - lam);
            return v <= 0 ? 0 : Math.min(cap[k], v);
        }

        /** The tonnes a year at which sink k joins the fill. */
        double joinAt(int k) { return evT[joinEvent[k]]; }
    }

    /**
     * The city's freight sides of one good at its site. The sale side is the binding suppliers as sinks (sNation the supplier, sBuyer its best buyer,
     * whose netback and use are the sink's netback and cap); the purchase side is the makers by landed cost (bNation).
     */
    private static final class Fill {
        double[] buyerPrice;
        double slope;
        int ns, nb;
        int[] sNation, sBuyer, bNation;
        Levels sell, buy;
    }

    /** The result of a re-solve (see resolve): the month's step and the nations' answer with the city's tonnes delivered, and the price they were sold at (the served buyers' landed price before the sale, tonne-weighted), in US$ a tonne. */
    record Resolved(double step, double realised, double price, int served, int passes) { }

    /** Who took this month's exports and who sold this month's imports, by nation (index nation - 1), in game units. */
    public record Allocation(double[] exportUnitsTo, double[] importUnitsFrom) { }

    private final Base base;
    private final Nations nations;
    private final int n, goodCount;
    private final int[] priceIdx;
    private final Work work;
    private double[] phat, lambda, gHat;
    private int[] passes;
    private int year = -1;
    private long solveNanos;
    private double[][] price, demand, supply, elastic, aSolved, phiSolved;
    private Fill[] fills;
    private boolean fillsFresh;

    private double siteLat = REFERENCE_LAT, siteLon = REFERENCE_LON;
    private boolean berth;
    private Legs legs;

    /** A market on a Base for a world's nations, standing at the reference city (Halifax, no berth) until it is told where. */
    public WorldMarket(Base base, Nations nations) {
        if (nations.count() != base.n) throw new IllegalArgumentException("world market: the file has " + base.n + " nations and the path " + nations.count());
        this.base = base;
        this.nations = nations;
        this.n = base.n;
        this.goodCount = base.traded.length;
        this.priceIdx = priceIndices(nations, base.traded);
        this.work = new Work(n);
        this.phat = new double[goodCount * n];
        this.lambda = new double[goodCount];
        this.passes = new int[goodCount];
        Arrays.fill(phat, 1);
        Arrays.fill(lambda, 1);
        this.price = new double[goodCount][n];
        this.demand = new double[goodCount][n];
        this.supply = new double[goodCount][n];
        this.elastic = new double[goodCount][n];
        this.aSolved = new double[goodCount][n];
        this.phiSolved = new double[goodCount][n];
        this.fills = new Fill[goodCount];
        this.legs = base.reference;
    }

    /* ----- where the city stands ----- */

    /** Puts the city at a point (an Earth city), with or without a berth: its host is the owner of the point. */
    public void setSite(double lat, double lon, boolean berth) {
        siteLat = lat;
        siteLon = lon;
        this.berth = berth;
        legs = base.legs(lat, lon, berth, base.hostOf(lat, lon));
        fillsFresh = false;
        refill();
    }

    /** Puts the city where a generated world's ground seed says (D23): a coastal nation drawn by its output, at its node, that nation its host. */
    public void setStandIn(long groundSeed, boolean berth) {
        int host = base.standIn(groundSeed);
        siteLat = base.lat[host - 1];
        siteLon = base.lon[host - 1];
        this.berth = berth;
        legs = base.legs(siteLat, siteLon, berth, host);
        fillsFresh = false;
        refill();
    }

    /** Gives the city a berth, or takes it away; the legs are worked out again only if that changes it. */
    public void setBerth(boolean berth) {
        if (berth == this.berth) return;
        this.berth = berth;
        legs = base.legs(siteLat, siteLon, berth, legs.host());
        fillsFresh = false;
        refill();
    }

    /** The nation a generated world's ground seed stands the city in (D23; see Base.standIn): the one setStandIn uses, 1-based. */
    public int standIn(long groundSeed) { return base.standIn(groundSeed); }

    /** The nation the city stands in, 1-based. */
    public int host() { return legs.host(); }

    /** Whether the city has a berth. */
    public boolean berth() { return berth; }

    /* ----- the January solve ----- */

    /**
     * Solves every good for a year, from the last year solved (or from p-hat = 1 and lambda = 1 for the first), and reads the band structures at the
     * site. A year already solved is left alone. The answer is a pure function of (file, path, seed, year, the state it started from): a process-wide
     * cache of SOLVES_KEPT answers returns what a fresh solve from the same start would.
     */
    public void solveYear(int year) {
        if (year == this.year) return;
        long t0 = System.nanoTime();
        double[] g = growth(nations, year, base.baseYear, n);
        int warm = this.year;
        long start = warm < 0 ? 0 : fingerprint(phat, lambda);
        SolveKey key = new SolveKey(base, nations.path(), nations.seed(), year, warm, start);
        Solved hit;
        synchronized (SOLVES) {
            hit = SOLVES.get(key);
        }
        if (hit != null) {
            phat = hit.prices().clone();
            lambda = hit.scales().clone();
            passes = hit.passes().clone();
        } else {
            for (int gi = 0; gi < goodCount; gi++) {
                double target = level(nations, priceIdx[gi], year, base.baseYear);
                double[] slice = new double[n];
                System.arraycopy(phat, gi * n, slice, 0, n);
                passes[gi] = solve(base.traded[gi], g, null, true, target, SOLVE_TOL, slice, lambda, gi, work);
                System.arraycopy(slice, 0, phat, gi * n, n);
            }
            synchronized (SOLVES) {
                SOLVES.put(key, new Solved(phat.clone(), lambda.clone(), passes.clone()));
            }
        }
        gHat = g;
        this.year = year;
        deriveSolution();
        fillsFresh = false;
        refill();
        solveNanos = System.nanoTime() - t0;
    }

    /** The year solved, -1 before the first. */
    public int solvedYear() { return year; }

    /** The time the last solveYear took, milliseconds (the cache's answer included). */
    public double lastSolveMillis() { return solveNanos / 1e6; }

    /** The sweeps a good's last solve took (0 for a good the market does not model or from the cache's answer's own count). */
    public int passes(Good g) {
        int gi = base.indexOf[g.ordinal()];
        return gi < 0 ? 0 : passes[gi];
    }

    private static long fingerprint(double[] a, double[] b) {
        long h = World.mix(a.length + 31L * b.length);
        for (double d : a) h = World.mix(h + Double.doubleToLongBits(d));
        for (double d : b) h = World.mix(h + Double.doubleToLongBits(d));
        return h;
    }

    /* ----- what a solved year implies: prices, demand, supply, and then at the site the sorted sides ----- */

    private void deriveSolution() {
        for (int gi = 0; gi < goodCount; gi++) {
            Traded t = base.traded[gi];
            double[] a = work.a, phi = work.phi;
            Arrays.fill(a, 0);
            Arrays.fill(phi, 0);
            Arrays.fill(price[gi], Double.NaN);
            Arrays.fill(demand[gi], 0);
            Arrays.fill(supply[gi], 0);
            for (int i : t.makers) a[i] = exp(-t.eps * log(phat[gi * n + i]));
            for (int j : t.users) {
                double s = t.ownPi[j] * a[j];
                for (int q = t.bStart[j]; q < t.bStart[j + 1]; q++) s += t.bPi[q] * a[t.bMaker[q]];
                phi[j] = s;
                demand[gi][j] = t.d0[j] * gHat[j] * exp(DEMAND_ELASTICITY / t.eps * log(s));
            }
            // each supplier's demand-side elasticity, the sum over its markets of the weight of the market in its sales times the elasticity of a CES
            // seller's quantity there, eps + 1 - (eps + 1 - ED) s, s its share of the market's index: the local derivative of its sales
            double kap = (t.eps + 1 - DEMAND_ELASTICITY) / t.eps;
            double[] spending = new double[n];
            for (int j : t.users) spending[j] = gHat[j] * exp(-kap * log(phi[j]));
            for (int k : t.makers) {
                double num = 0, den = 0;
                if (t.ownX[k] > 0 && phi[k] > 0) {
                    double val = t.ownX[k] * a[k] * spending[k], share = t.ownPi[k] * a[k] / phi[k];
                    num += val * (t.eps + 1 - (t.eps + 1 - DEMAND_ELASTICITY) * share);
                    den += val;
                }
                for (int q = t.sStart[k]; q < t.sStart[k + 1]; q++) {
                    int j = t.sBuyer[q];
                    double val = t.sX[q] * a[k] * spending[j], share = t.sPi[q] * a[k] / phi[j];
                    num += val * (t.eps + 1 - (t.eps + 1 - DEMAND_ELASTICITY) * share);
                    den += val;
                }
                elastic[gi][k] = den > 0 ? num / den : t.eps + 1;
            }
            System.arraycopy(a, 0, aSolved[gi], 0, n);
            System.arraycopy(phi, 0, phiSolved[gi], 0, n);
            for (int i : t.makers) {
                double ph = phat[gi * n + i];
                price[gi][i] = t.v * ph;
                supply[gi][i] = t.s0[i] * gHat[i] * lambda[gi] * exp(SUPPLY_ELASTICITY * log(ph));
            }
        }
    }

    private void refill() {
        if (year < 0 || fillsFresh) return;
        for (int gi = 0; gi < goodCount; gi++) fills[gi] = fill(gi, price[gi], demand[gi], supply[gi]);
        fillsFresh = true;
    }

    /** The share a supplier k has of buyer j's composite in the solved state (its flow over the buyer's spending on the nations), 0 where the file keeps no pair. */
    private double pairShare(int gi, Traded t, int k, int j) {
        double pi = 0;
        if (k == j) {
            pi = t.ownPi[j];
        } else {
            for (int q = t.bStart[j]; q < t.bStart[j + 1]; q++) {
                if (t.bMaker[q] == k) {
                    pi = t.bPi[q];
                    break;
                }
            }
        }
        double phi = phiSolved[gi][j];
        return phi > 0 ? pi * aSolved[gi][k] / phi : 0;
    }

    /** One good's sorted sides at the site, for mill prices p, demand d and supply s (tonnes a year). */
    private Fill fill(int gi, double[] p, double[] d, double[] s) {
        Traded t = base.traded[gi];
        Fill f = new Fill();
        double phi = Double.isNaN(t.phi) ? PHI_FALLBACK : t.phi;
        double[] fc = legs.fc(), fp = legs.fp();
        double[] b = new double[n];
        Arrays.fill(b, Double.POSITIVE_INFINITY);
        int[] binder = new int[n];
        for (int k : t.makers) {
            double pk = p[k];
            int row = k * n;
            for (int j : t.users) {
                double c = pk + phi * fp[row + j];
                if (c < b[j]) {
                    b[j] = c;
                    binder[j] = k;
                }
            }
        }
        f.buyerPrice = b;
        int[] order = new int[n];
        double[] bestNet = new double[n];
        int[] bestBuyer = new int[n];
        Arrays.fill(bestBuyer, -1);
        double slope = 0;
        for (int j : t.users) {
            if (!(d[j] > 0) || !(b[j] < Double.POSITIVE_INFINITY)) continue;
            double nj = b[j] - phi * fc[j];
            if (!Double.isFinite(nj)) continue;
            slope += DEMAND_ELASTICITY * d[j] / b[j];
            int k = binder[j];
            if (bestBuyer[k] < 0 || nj > bestNet[k]) {
                bestBuyer[k] = j;
                bestNet[k] = nj;
            }
        }
        // THE SINKS OF A SALE are the binding suppliers (in this file every maker's base price is one unit value, so one supplier sets each buyer's
        // price): the city's tonnes displace its sales and its mills resell across their whole market, so what falls is its mill price. A supplier's
        // buyers fall in step with its best and never catch up: its sink takes the tonnes at its best buyer's netback, and no more than that buyer uses.
        // The slope is the supplier's own, mu_k = (its sales' demand-side elasticity + ES) S_k / p_k, the same the purchase fill uses for buying from it,
        // so selling into k's market and buying from k move k's price at one slope. A tonne sold into j comes out of j's whole composite, each supplier
        // losing its share pi_kj of it, so k loses only pi_kj of each tonne and its price falls by q pi_kj / mu_k: the sink's effective slope is
        // mu_k / pi_kj (the share from the solved state, floored at SHARE_FLOOR).
        int ns = 0;
        for (int k : t.makers) if (bestBuyer[k] >= 0 && p[k] > 0 && s[k] > 0) order[ns++] = k;
        sortBy(order, ns, bestNet, true);
        f.ns = ns;
        f.sNation = Arrays.copyOf(order, ns);
        f.sBuyer = new int[ns];
        double[] net = new double[ns], mu = new double[ns], cap = new double[ns];
        for (int m = 0; m < ns; m++) {
            int k = f.sNation[m], j = bestBuyer[k];
            f.sBuyer[m] = j;
            net[m] = bestNet[k];
            mu[m] = (elastic[gi][k] + SUPPLY_ELASTICITY) * s[k] / p[k] / Math.max(SHARE_FLOOR, pairShare(gi, t, k, j));
            cap[m] = d[j];
        }
        f.sell = new Levels(net, mu, cap, ns);
        // THE PURCHASE SIDE: the makers by landed cost, each at the same slope and no more than it makes; the levels run on the cost negated
        int nb = 0;
        double[] landed = new double[n];
        for (int i : t.makers) {
            if (!(p[i] > 0) || !(s[i] > 0)) continue;
            double li = p[i] + phi * fc[i];
            if (!Double.isFinite(li)) continue;
            landed[i] = li;
            order[nb++] = i;
            slope += SUPPLY_ELASTICITY * s[i] / p[i];
        }
        sortBy(order, nb, landed, false);
        f.nb = nb;
        f.bNation = Arrays.copyOf(order, nb);
        double[] negLanded = new double[nb], muBuy = new double[nb], capBuy = new double[nb];
        for (int m = 0; m < nb; m++) {
            int i = f.bNation[m];
            negLanded[m] = -landed[i];
            muBuy[m] = (elastic[gi][i] + SUPPLY_ELASTICITY) * s[i] / p[i];
            capBuy[m] = s[i];
        }
        f.buy = new Levels(negLanded, muBuy, capBuy, nb);
        f.slope = slope;
        return f;
    }

    /* ----- the city's month: the fills and the band ----- */

    /**
     * A good's floor and ceiling in US$ a tonne for x tonnes a year sold and m bought: the fills, less the world's answer within the year (x - m over
     * the world's slope). {floor, ceiling}, with the floor never above the ceiling; NaN on a side with no buyers or no sellers. Past every sink's cap the
     * floor is 0 (the clamp), and past every seller's output the ceiling is +infinity (the world cannot sell the city that much).
     */
    double[] model(int gi, double x, double m) {
        if (year < 0) return new double[] { Double.NaN, Double.NaN };
        refill();
        Fill f = fills[gi];
        double shift = f.slope > 0 ? (x - m) / f.slope : 0;
        double floor = f.ns > 0 ? Math.max(0, f.sell.level(x) - shift) : Double.NaN;
        double landed = f.nb > 0 ? -f.buy.level(m) : Double.NaN;
        if (f.nb > 0 && !Double.isFinite(landed)) landed = SCARCITY_MULTIPLE * -f.buy.level(0);   // past every seller's capacity: a bound, not infinity
        double ceiling = f.nb > 0 ? Math.max(Double.isNaN(floor) ? 0 : floor, landed - shift) : Double.NaN;
        return new double[] { floor, ceiling };
    }

    /**
     * Where the ceiling stops when the city buys more than every seller makes, as a multiple of the cheapest landed cost: past every cap the fill
     * has no level (infinity), and a market's strike needs a finite ceiling. A bound for a city the world cannot supply, never reached by one it can.
     */
    static final double SCARCITY_MULTIPLE = 4;

    /**
     * The band for a good in the game's money, thousands of world dollars a game unit: {floor, ceiling}, from last month's exports and imports in
     * game units. NaN on a side the Good does not have, and both for a good the market does not model or before a year is solved.
     */
    public double[] band(Good g, double exportUnitsLastMonth, double importUnitsLastMonth) {
        int gi = base.indexOf[g.ordinal()];
        if (gi < 0 || year < 0 || Double.isNaN(base.traded[gi].sigma)) return new double[] { Double.NaN, Double.NaN };
        Traded t = base.traded[gi];
        double x = 12 * Math.max(0, exportUnitsLastMonth) / t.unitsPerTonne, m = 12 * Math.max(0, importUnitsLastMonth) / t.unitsPerTonne;
        double[] b = model(gi, x, m);
        return new double[] { g.exportable() ? t.sigma * b[0] : Double.NaN, g.importable() ? t.sigma * b[1] : Double.NaN };
    }

    /**
     * Who took this month's exports and who sold this month's imports, from the same fills at those volumes: the tonnes a binding supplier's sink takes
     * are its slope times what its best buyer's netback leaves over the fill's, no more than that buyer uses, and they go to that buyer (its other
     * buyers fall in step and never catch up); a seller of the imports likewise by its own slope and landed cost, no more than it makes; each scaled so
     * they add to the volume exactly (past every cap, the caps scaled up). Zeros where a volume is 0 or the good has no buyers or no sellers.
     */
    public Allocation allocate(Good g, double exportUnits, double importUnits) {
        double[] to = new double[n], from = new double[n];
        int gi = base.indexOf[g.ordinal()];
        if (gi < 0 || year < 0) return new Allocation(to, from);
        refill();
        Traded t = base.traded[gi];
        Fill f = fills[gi];
        double x = 12 * Math.max(0, exportUnits) / t.unitsPerTonne, m = 12 * Math.max(0, importUnits) / t.unitsPerTonne;
        if (x > 0 && f.ns > 0 && g.exportable()) {
            double lam = f.sell.level(x), sum = 0;
            for (int k = 0; k < f.ns && f.sell.net[k] > lam; k++) {
                double q = f.sell.take(k, lam);
                to[f.sBuyer[k]] = q;
                sum += q;
            }
            scale(to, sum, exportUnits);
        }
        if (m > 0 && f.nb > 0 && g.importable()) {
            double lam = f.buy.level(m), sum = 0;
            for (int k = 0; k < f.nb && f.buy.net[k] > lam; k++) {
                double q = f.buy.take(k, lam);
                from[f.bNation[k]] = q;
                sum += q;
            }
            scale(from, sum, importUnits);
        }
        return new Allocation(to, from);
    }

    private static void scale(double[] a, double sum, double total) {
        if (!(sum > 0)) return;
        for (int i = 0; i < a.length; i++) a[i] = a[i] * total / sum;
    }

    /* ----- reads for the wiring and the screens ----- */

    /** Whether the market models this good and the city has a side of it. */
    public boolean trades(Good g) { return base.trades(g); }

    /** How many nations there are. */
    public int nationCount() { return n; }

    /** A nation's ISO code, 1-based. */
    public String iso3(int nation) { return base.iso[nation - 1]; }

    /** A nation's name. */
    public String name(int nation) { return base.name[nation - 1]; }

    /** The latitude of a nation's node. */
    public double nodeLat(int nation) { return base.lat[nation - 1]; }

    /** The longitude of a nation's node. */
    public double nodeLon(int nation) { return base.lon[nation - 1]; }

    /** A nation's mill price for a good in the game's money (sigma times the model's), NaN for a nation that makes none or a good not modelled. */
    public double millPrice(Good g, int nation) {
        int gi = base.indexOf[g.ordinal()];
        if (gi < 0 || year < 0) return Double.NaN;
        return base.traded[gi].sigma * price[gi][nation - 1];
    }

    /** What the cheapest landed seller asks a nation's buyers for a good, in the game's money; NaN where none reaches. */
    public double buyerPrice(Good g, int nation) {
        int gi = base.indexOf[g.ordinal()];
        if (gi < 0 || year < 0) return Double.NaN;
        refill();
        double b = fills[gi].buyerPrice[nation - 1];
        return b == Double.POSITIVE_INFINITY ? Double.NaN : base.traded[gi].sigma * b;
    }

    /* ----- the save ----- */

    /** Every modelled good's mill prices over the base year's, dense, good-major (goods x nations): what the save keeps of the solve. Empty before a year is solved. */
    public double[] savedPrices() { return year < 0 ? new double[0] : phat.clone(); }

    /** Every modelled good's supply scale, in the same order. */
    public double[] savedScales() { return year < 0 ? new double[0] : lambda.clone(); }

    /**
     * Restores a solved year from what the save kept, deriving everything without solving: the bands and allocations are bit for bit what the solve
     * gave. False, and nothing changed, if the lengths are not this file's goods and nations or a figure is not a number above 0.
     */
    public boolean restore(int year, double[] prices, double[] scales) {
        if (year < Nations.FIRST_YEAR || prices == null || scales == null || prices.length != goodCount * n || scales.length != goodCount) return false;
        for (double p : prices) if (!(p > 0) || !Double.isFinite(p)) return false;
        for (double s : scales) if (!(s > 0) || !Double.isFinite(s)) return false;
        phat = prices.clone();
        lambda = scales.clone();
        gHat = growth(nations, year, base.baseYear, n);
        this.year = year;
        Arrays.fill(passes, 0);
        deriveSolution();
        fillsFresh = false;
        refill();
        return true;
    }

    /* =====================================================================
       FOR THE CHECK - what a harness reads that the game does not
       ===================================================================== */

    Base base() { return base; }

    /** A good's index among the modelled, -1 if none. */
    int indexOf(Good g) { return base.indexOf[g.ordinal()]; }

    /** A good's mill prices over the base year's, as solved. */
    double[] phat(int gi) { return Arrays.copyOfRange(phat, gi * n, (gi + 1) * n); }

    double lambda(int gi) { return lambda[gi]; }

    double[] gHat() { return gHat.clone(); }

    /** The mean mill price's target for a good this year. */
    double levelTarget(int gi) { return level(nations, priceIdx[gi], year, base.baseYear); }

    /** A nation's use of a good this year (1-based), tonnes a year. */
    double demand(int gi, int nation) { return demand[gi][nation - 1]; }

    /** The world's use of a good this year, tonnes a year. */
    double worldUse(int gi) {
        double sum = 0;
        for (double d : demand[gi]) sum += d;
        return sum;
    }

    /** A good's city-side figures: {first netback, cheapest landed cost, the world's slope}, US$ a tonne and tonnes a year per US$ a tonne. */
    double[] extremes(int gi) {
        refill();
        Fill f = fills[gi];
        return new double[] { f.ns > 0 ? f.sell.net[0] : Double.NaN, f.nb > 0 ? -f.buy.net[0] : Double.NaN, f.slope };
    }

    /** The sinks the fill at x tonnes a year serves, in the order they join: rows of {the best buyer's nation (1-based), tonnes a year, its netback before the sale, the level at the end of the fill (-infinity past every cap), the binding supplier's nation}. */
    double[][] sellers(int gi, double x) {
        refill();
        Fill f = fills[gi];
        if (f.ns == 0) return new double[0][];
        double lam = f.sell.level(x);
        int last = 0;
        while (last + 1 < f.ns && f.sell.net[last + 1] > lam) last++;
        double[][] rows = new double[last + 1][];
        for (int k = 0; k <= last; k++) rows[k] = new double[] { f.sBuyer[k] + 1, f.sell.take(k, lam), f.sell.net[k], lam, f.sNation[k] + 1 };
        return rows;
    }

    /** A good's sinks at the site by netback, best first: {the best buyer's nation (1-based), slope (tonnes a year per US$ a tonne), its netback US$ a tonne, the binding supplier's nation, cap tonnes a year}. */
    double[][] sellSide(int gi) {
        refill();
        Fill f = fills[gi];
        double[][] rows = new double[f.ns][];
        for (int k = 0; k < f.ns; k++) rows[k] = new double[] { f.sBuyer[k] + 1, f.sell.slope[k], f.sell.net[k], f.sNation[k] + 1, f.sell.cap[k] };
        return rows;
    }

    /** A good's makers at the site by landed cost, cheapest first: {nation (1-based), slope, landed cost US$ a tonne, cap tonnes a year}. */
    double[][] buySide(int gi) {
        refill();
        Fill f = fills[gi];
        double[][] rows = new double[f.nb][];
        for (int k = 0; k < f.nb; k++) rows[k] = new double[] { f.bNation[k] + 1, f.buy.slope[k], -f.buy.net[k], f.buy.cap[k] };
        return rows;
    }

    /** What all a good's sinks take together at the site, and all its sellers sell: {tonnes a year, tonnes a year}. */
    double[] capacities(int gi) {
        refill();
        Fill f = fills[gi];
        return new double[] { f.sell.capacity(), f.buy.capacity() };
    }

    /** Forgets the solved years every market shares, so the next solve is a fresh one (a harness proves a solve repeats itself, not that the cache does). */
    static void forgetSolves() {
        synchronized (SOLVES) {
            SOLVES.clear();
        }
    }

    /** The landed price at the buyers the fill at x tonnes a year serves, before the sale, weighted by the tonnes; the best buyer's where it sells nothing. US$ a tonne. */
    double servedPrice(int gi, double x) {
        refill();
        Fill f = fills[gi];
        if (f.ns == 0) return Double.NaN;
        double lam = f.sell.level(x), num = 0, den = 0;
        for (int k = 0; k < f.ns && (k == 0 || f.sell.net[k] > lam); k++) {
            double q = f.sell.take(k, lam);
            num += q * f.buyerPrice[f.sBuyer[k]];
            den += q;
        }
        return den > 0 ? num / den : f.buyerPrice[f.sBuyer[0]];
    }

    /** The tonnes a year of sales at which the fill reaches a nation's buyers (1-based; it reaches them through the best buyer of their binding supplier), or NaN if it never does. */
    double joinsAt(int gi, int nation) {
        refill();
        Fill f = fills[gi];
        for (int k = 0; k < f.ns; k++) if (f.sBuyer[k] == nation - 1) return f.sell.joinAt(k);
        return Double.NaN;
    }

    /**
     * The check's re-solve of the month's step: the fill at x tonnes a year sold, then the nations' solve again with the tonnes the fill delivers
     * taken out of their buyers' demand (their spending on the nations' goods P-hat (gHat P-hat^-ED - q / D0), floored above 0), the supply scale
     * held and the level free (the world's answer), and the city's realised netback, the buyers' new landed prices less the city's freight to them,
     * weighted by the tonnes. step is the model's floor for x, the band's (the fill less x over the world's slope, clamped at 0).
     */
    Resolved resolve(int gi, double x) {
        refill();
        Traded t = base.traded[gi];
        Fill f = fills[gi];
        double phi = Double.isNaN(t.phi) ? PHI_FALLBACK : t.phi;
        double lam = f.sell.level(x);
        double step = Math.max(0, lam - x / f.slope);
        double[] carve = new double[n], q = new double[n];
        double served = 0, priceNum = 0, priceDen = 0;
        for (int k = 0; k < f.ns && (k == 0 || f.sell.net[k] > lam); k++) {
            int j = f.sBuyer[k];
            q[j] = f.sell.take(k, lam);
            carve[j] = q[j] / t.d0[j];
            if (q[j] > 0) {
                served++;
                priceNum += q[j] * f.buyerPrice[j];
                priceDen += q[j];
            }
        }
        double[] start = new double[n];
        System.arraycopy(phat, gi * n, start, 0, n);
        double[] lam1 = { lambda[gi] };
        Work w = new Work(n);
        int sweeps = solve(t, gHat, carve, false, 1, RESOLVE_TOL, start, lam1, 0, w);
        double[] p = new double[n];
        for (int i = 0; i < n; i++) p[i] = t.v * start[i];
        double[] fc = legs.fc(), fp = legs.fp();
        double[] b = new double[n];
        Arrays.fill(b, Double.POSITIVE_INFINITY);
        for (int k : t.makers) {
            for (int j : t.users) {
                double c = p[k] + phi * fp[k * n + j];
                if (c < b[j]) b[j] = c;
            }
        }
        double num = 0, den = 0;
        for (int j = 0; j < n; j++) {
            if (!(q[j] > 0)) continue;
            num += q[j] * (b[j] - phi * fc[j]);
            den += q[j];
        }
        return new Resolved(step, den > 0 ? num / den : Double.NaN, priceDen > 0 ? priceNum / priceDen : f.buyerPrice[f.sBuyer[0]], (int) served, sweeps);
    }

    /* =====================================================================
       SORTING - indices by a key, stable, no boxing
       ===================================================================== */

    /** Sorts idx[0..len) by key[idx], descending or ascending; equal keys keep the order they were given in. */
    static void sortBy(int[] idx, int len, double[] key, boolean descending) {
        mergeSort(idx, new int[len], 0, len, key, descending);
    }

    private static void mergeSort(int[] a, int[] tmp, int lo, int hi, double[] key, boolean desc) {
        if (hi - lo < 2) return;
        if (hi - lo <= 12) {
            for (int i = lo + 1; i < hi; i++) {
                int x = a[i], j = i - 1;
                while (j >= lo && before(key, x, a[j], desc)) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = x;
            }
            return;
        }
        int mid = (lo + hi) >>> 1;
        mergeSort(a, tmp, lo, mid, key, desc);
        mergeSort(a, tmp, mid, hi, key, desc);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) tmp[k++] = before(key, a[j], a[i], desc) ? a[j++] : a[i++];
        while (i < mid) tmp[k++] = a[i++];
        while (j < hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo);
    }

    private static boolean before(double[] key, int x, int y, boolean desc) {
        return desc ? key[x] > key[y] : key[x] < key[y];
    }
}

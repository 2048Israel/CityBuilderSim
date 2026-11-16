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
 * (file, path, seed, year) and harness cities on one world share it. CU2 chains
 * it (WorldChain; docs/studies/spec-world-prices.md 3): the goods are solved
 * inputs first, a root is solved and then anchored so the reference city's band
 * stands on the path, and a made good's price in a nation is its inputs' cost
 * landed there plus its own part, solved on the manufactures index, less the
 * copper charge where it carries one; its makers supply what they sell at that
 * price, so the market clears by construction.
 *
 * THE BASE is everything that is the file's: the pairs in two layouts, the
 * freight table, the nodes, the calibration. It is immutable, built once a
 * process (Base.load caches by the file's path, size and time) and shared by
 * every market on that file. A WorldMarket is one city's: its site, its berth,
 * its solved year. It is not thread-safe; the Base is, and so is every Curve curve() hands out (WN6: an immutable snapshot, published through a
 * volatile whenever the fills are rebuilt, for a screen's thread).
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

    /** The founding solve (the calendar's first year, every good, from p-hat = 1) takes at most this many milliseconds. */
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

    /** The two charges the literals were set on (treatment and refining), over that year's copper price: 2024's benchmark over 2024's price (CU1 section 5; call star 6 (a)). */
    static final double CHARGE_LITERAL = .054;

    /** The refining charge's part of the two charges (2024's benchmark: 8.0 of 22.2 cents a pound); anode carries this part, concentrate both. */
    static final double REFINING_PART = .36;

    /** The least a charge leaves of the copper in a good (a charge is taken off the copper's cost before the floor). */
    static final double CHARGE_LEAVES = .05;

    /** The least a made good's price ratio may be: a price is floored above zero. */
    static final double PRICE_FLOOR = 1e-6;

    /** The anchor's root-find stops within this of its target, in logs, or after ANCHOR_READS reads of the reference band, whichever comes first. */
    static final double ANCHOR_TOL = 1e-10;

    /** The most reads of the reference band one root's anchor takes. */
    static final int ANCHOR_READS = 40;

    /** The most steps the anchor takes while its bracket is open, each at most ANCHOR_STEP in the log of the move. */
    static final int ANCHOR_OPEN_STEPS = 12;

    /** The most the anchor moves the prices in one step of its search, in logs (a factor of e cubed). */
    static final double ANCHOR_STEP = 3;

    /** What the anchor's search reads where the band's side is not above 0 (a price below its freight): this far below the target, in logs, so the prices are moved up. */
    static final double ANCHOR_LOW = -50;

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
        /** The years the reader made before the file's first (WorldData.standInYears()): 100 for the full file. */
        final int standInYears;
        final double radius, seaUsd, landUsd, handlingUsd, landDetour, seaDetour;
        /** US$ a tonne between nations, row-major (from * n + to), 0 on the diagonal. */
        final double[] freight;
        /** For an owner number above n (an area), the nation that sells its land, 1-based, or 0. */
        final int[] areaSeller;
        final Traded[] traded;
        final int[] indexOf;
        /** The chain: which goods are made of which, their shares, and the order the January solves them in (CU2). */
        final WorldChain chain;
        /** The manufactures index's good (machinery) among the modelled, -1 if it is not modelled: a made good's own part follows its price path. */
        final int muv;
        /**
         * What the calibration leaves for the January's anchor and charge: each good's Historical price level in REFERENCE_YEAR over the base year (the
         * level the literals stand on), the same for copper and for the manufactures index (1 if not modelled), and the freight scale the plain pass gave
         * each good (for the check). Set by calibrate.
         */
        double[] refLevel, plainPhi;
        double refCopper = 1, refMuv = 1;
        final int refHost;
        final Legs reference;
        final long buildMillis;
        private final WorldData owners;
        /** How many goods calibrated, and how many were one-sided, fell back to the class median, or have no side. */
        final int[] fits = new int[Fit.values().length];
        /** Each nation's commodity share of exports (commodityShare), worked on first use. */
        private double[] commodity;

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
            standInYears = data.standInYears();
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
            chain = new WorldChain(traded, indexOf);
            muv = indexOf[Good.MACHINERY.ordinal()];
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

        /**
         * The share of a nation's base-year exports, by value, that are goods of the class "bulk" (the file's goods layer: bulk, processed, made): what a
         * commodity currency's terms of trade pass into its money (WN4; spec-exchange-rates 3.7). A nation's exports of a good are what it makes less what
         * it sells to itself (make - ownX, the pairs' sX summed by seller); one with no exports in the modelled goods has 0. Worked once and kept.
         */
        public synchronized double commodityShare(int nation) {
            if (nation < 1 || nation > n) throw new IllegalArgumentException("world market: no nation numbered " + nation);
            if (commodity == null) {
                double[] bulk = new double[n], all = new double[n];
                for (Traded t : traded) {
                    for (int i = 0; i < n; i++) {
                        double x = Math.max(0, t.make[i] - t.ownX[i]);
                        all[i] += x;
                        if ("bulk".equals(t.goodClass)) bulk[i] += x;
                    }
                }
                commodity = new double[n];
                for (int i = 0; i < n; i++) commodity[i] = all[i] > 0 ? bulk[i] / all[i] : 0;
            }
            return commodity[nation - 1];
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
         *
         * TWO PASSES (CU2; spec-world-prices 3.8). The plain pass solves each good alone on its own price path and fixes the freight scale of the
         * goods that do not calibrate (a made good reads its input's freight scale, and a class median is only known at the end). The chained pass then
         * goes in the chain's order and solves nothing: a root's prices are the plain pass's (the anchor is the identity in the reference year), a made
         * good's own part is the plain pass's prices times the two levels' ratio (a uniform move is another lambda, spec section 2), the rest of its
         * price is its inputs' cost, with the charge at the literal; the fit is the same, on those prices, and a good that does not calibrate keeps the
         * plain pass's freight scale.
         */
        private void calibrate(WorldData data) {
            Nations hist = new Nations(data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
            double[] gHat = growth(hist, REFERENCE_YEAR, baseYear, n);
            int[] priceIdx = priceIndices(hist, traded);
            refLevel = new double[traded.length];
            for (int gi = 0; gi < traded.length; gi++) refLevel[gi] = level(hist, priceIdx[gi], REFERENCE_YEAR, baseYear);
            int copper = indexOf[Good.COPPER.ordinal()];
            refCopper = copper < 0 ? 1 : refLevel[copper];
            refMuv = muv < 0 ? 1 : refLevel[muv];
            Work work = new Work(n);
            double[][] kept = new double[traded.length][];
            double[][] plain = new double[traded.length][];
            double[] phat = new double[n];
            double[] p = new double[n];
            double[] lam = new double[1];
            for (int gi = 0; gi < traded.length; gi++) {
                Traded t = traded[gi];
                Arrays.fill(phat, 1);
                lam[0] = 1;
                solve(t, gHat, null, true, refLevel[gi], SOLVE_TOL, phat, lam, 0, work);
                plain[gi] = phat.clone();
                for (int i = 0; i < n; i++) p[i] = t.v * phat[i];
                fit(t, p);
                kept[gi] = p.clone();
            }
            scale(kept);
            plainPhi = new double[traded.length];
            for (int gi = 0; gi < traded.length; gi++) plainPhi[gi] = traded[gi].phi;
            for (Traded t : traded) {
                t.phi = t.sigma = t.refFloor = t.refCeiling = Double.NaN;
                t.fit = Fit.NONE;
            }
            Arrays.fill(fits, 0);
            double[] flat = new double[traded.length * n];
            Arrays.fill(flat, 1);
            double[][] cache = new double[traded.length][];
            for (int gi : chain.order) {
                Traded t = traded[gi];
                double[] slice = plain[gi];
                if (chain.made(gi)) {
                    if (chain.own[gi] > 0) {
                        double k = refLevel[ownLevelGood(gi)] / refLevel[gi];
                        for (int i : t.makers) slice[i] *= k;
                    }
                    price(gi, slice, flat, cache, hist, REFERENCE_YEAR, true);
                }
                System.arraycopy(slice, 0, flat, gi * n, n);
                for (int i = 0; i < n; i++) p[i] = t.v * slice[i];
                fit(t, p);
                if (t.fit != Fit.CALIBRATED && t.fit != Fit.NONE && plainPhi[gi] > 0) t.phi = plainPhi[gi];
                kept[gi] = p.clone();
            }
            scale(kept);
            for (Traded t : traded) fits[t.fit.ordinal()]++;
        }

        /** One good's fit on its reference prices p (US$ a tonne): both sides calibrate its freight scale, one side leaves it to the class's median, none leaves it nothing. */
        private void fit(Traded t, double[] p) {
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
        }

        /**
         * Every good's freight scale where it has none (its class's median of the goods that calibrated, the overall median where its class has none),
         * then its price scale and the reference floor and ceiling, from the reference prices kept (US$ a tonne, good by good).
         */
        private void scale(double[][] kept) {
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
                if (t.fit != Fit.CALIBRATED && !(t.phi > 0)) t.phi = medians.getOrDefault(t.goodClass, overall);
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

        /* ----- the chain's January (CU2; docs/studies/spec-world-prices.md 3) ----- */

        /** The good whose price path a good's own part follows: its own, or for a made good with an own part the manufactures index's (machinery's); the good itself where machinery is not modelled. */
        int ownLevelGood(int gi) {
            return chain.made(gi) && chain.own[gi] > 0 && muv >= 0 ? muv : gi;
        }

        /**
         * What good pg costs each nation over what it cost it in the base year, from the good's finished prices in flat (goods x nations, as phat): the
         * cheapest maker's mill price landed there, over the same at base-year prices (the good as one commodity with one price and a freight: what the
         * city itself pays). 1 where no maker reaches the nation. The freight scale is the good's, PHI_FALLBACK where it has none.
         */
        double[] seen(int pg, double[] flat) {
            Traded t = traded[pg];
            int at = pg * n;
            double phi = t.phi > 0 ? t.phi : PHI_FALLBACK;
            double[] out = new double[n];
            for (int i = 0; i < n; i++) {
                double now = Double.POSITIVE_INFINITY, then = Double.POSITIVE_INFINITY;
                for (int m : t.makers) {
                    double f = phi * freight[m * n + i];
                    now = Math.min(now, t.v * flat[at + m] + f);
                    then = Math.min(then, t.v + f);
                }
                out[i] = now < Double.POSITIVE_INFINITY && then > 0 ? now / then : 1;
            }
            return out;
        }

        /** The price number of a good in a path's price layer (1-based), 0 where it has none. */
        private static int priceNumber(Nations nations, Good g) {
            for (int k = 1; k <= nations.goodCount(); k++) if (nations.goodKey(k).equals(g.name())) return k;
            return 0;
        }

        /**
         * The charge off a unit of copper in good g, in the copper's base-year price; 0 for a good that carries none. Concentrate carries both the
         * treatment and the refining charge, anode the refining charge (REFINING_PART of the two). The charge is money, not a share of the copper: a
         * smelter's fee, priced as every conversion is (on the manufactures index) times the swing of Nations.copperChargeSwing, or where the path has a
         * record of the year (Nations.copperChargeRecorded; the table is empty today) that year's share of the copper price. The calibration reads the
         * charge the literals were set on (CHARGE_LITERAL, 2024's benchmark), not the reference year's own.
         */
        double charge(Good g, Nations nations, int year, boolean calibrating) {
            double part = g == Good.COPPER_CONC ? 1 : g == Good.ANODE ? REFINING_PART : 0;
            if (part == 0) return 0;
            if (calibrating) return part * CHARGE_LITERAL * refCopper;
            int from = nations.path() == Nations.Path.HISTORICAL ? baseYear : baseYear - standInYears;
            if (nations.copperChargeRecorded(year)) return part * nations.copperChargeRecord(year) * levelOf(nations, Good.COPPER, year, from);
            return part * nations.copperChargeSwing(year) * levelOf(nations, Good.MACHINERY, year, from) / refMuv * refCopper;
        }

        /** A modelled good's price level in a year over the base year read, 1 where the good is not modelled. */
        private double levelOf(Nations nations, Good g, int year, int from) {
            return indexOf[g.ordinal()] < 0 ? 1 : level(nations, priceNumber(nations, g), year, from);
        }

        /**
         * Makes a made good's price in slice (its makers' mill prices over the base year's; its own solved part on entry, its price on leaving): in each
         * maker's nation the sum of each input's share times what the input costs there (seen, kept in cache for the year: a parent is finished before a
         * child asks, the chain's order sees to it), less the charge where the good carries one (never below CHARGE_LEAVES of its inputs' cost), plus its
         * own share of its own part. A price is floored above zero. flat is every good's finished prices (goods x nations).
         */
        void price(int gi, double[] slice, double[] flat, double[][] cache, Nations nations, int year, boolean calibrating) {
            Traded t = traded[gi];
            double[] cost = new double[n];
            for (int k = 0; k < chain.parent[gi].length; k++) {
                int pg = chain.parent[gi][k];
                if (cache[pg] == null) cache[pg] = seen(pg, flat);
                for (int i : t.makers) cost[i] += chain.share[gi][k] * cache[pg][i];
            }
            double chi = charge(t.good, nations, year, calibrating), own = chain.own[gi];
            for (int i : t.makers) {
                double in = chi > 0 ? Math.max(CHARGE_LEAVES * cost[i], cost[i] - chi) : cost[i];
                double v = in + (own > 0 ? own * slice[i] : 0);
                slice[i] = v > PRICE_FLOOR && Double.isFinite(v) ? v : PRICE_FLOOR;
            }
        }

        /**
         * A solve's start, moved as one onto the year's level: last year's answer left a root off its mean (the anchor) and a made good on its chained
         * price, so every maker's price is scaled by target over the makers' w-weighted mean, and a root's lambda by the same move's power (a uniform move
         * with the supply scale beside it is the same equilibrium; a made good's lambda is its own part's and stays). Only a root, or a made good with an
         * own part, moves. Nothing is kept beside the saved prices and scales, so a city read back starts where it would have.
         */
        void moveOntoLevel(int gi, double[] slice, double[] lam, int lamAt, double target) {
            boolean made = chain.made(gi);
            if (made && !(chain.own[gi] > 0)) return;
            Traded t = traded[gi];
            double mean = 0;
            for (int i : t.makers) mean += t.w[i] * slice[i];
            double k = target / mean;
            if (!(k > 0) || !Double.isFinite(k)) return;
            for (int i : t.makers) slice[i] *= k;
            if (!made) lam[lamAt] *= exp(-(SUPPLY_ELASTICITY + DEMAND_ELASTICITY) * log(k));
        }

        /**
         * How far the reference city's band stands off target with the makers' prices (slice) moved as one by exp(x): the log of the band over the band
         * the calibration left (the literals where a side calibrated, 1 in the reference year) less the log of target, a two-sided good read by the
         * geometric middle of its sides and a one-sided one by its calibrated side. NaN where a side read is not a number, negative infinity where it
         * is not above 0.
         */
        private double off(Traded t, double[] slice, double x, double[] p, double target) {
            double k = exp(x);
            for (int i = 0; i < n; i++) p[i] = t.v * slice[i] * k;
            double[] fl = referenceBand(t, p, t.phi);
            if (t.fit == Fit.CALIBRATED) {
                double f = t.sigma * fl[0] / t.refFloor, ce = t.sigma * fl[1] / t.refCeiling;
                if (Double.isNaN(f) || Double.isNaN(ce)) return Double.NaN;
                if (!(f > 0 && Double.isFinite(f) && ce > 0 && Double.isFinite(ce))) return Double.NEGATIVE_INFINITY;
                return log(Math.sqrt(f * ce) / target);
            }
            double s = t.good.importable() ? t.sigma * fl[1] / t.refCeiling : t.sigma * fl[0] / t.refFloor;
            if (Double.isNaN(s)) return Double.NaN;
            return s > 0 && Double.isFinite(s) ? log(s / target) : Double.NEGATIVE_INFINITY;
        }

        /**
         * A root's anchor (CU1's star 5, spec-world-prices 3.5): after its solve, its makers' prices are moved as one, its supply scale with them (a
         * uniform move with lambda times exp(-(ES + ED) x) is the same equilibrium), until the reference city's band with nothing traded, over the band
         * the calibration left it, equals the path's level over the level the literals stand on (levelNow over refLevel). The band does not move one
         * for one with the prices (the freight does not move), so the move x, in logs, is found by a bracketed root-find, from xStart (last year's move:
         * the band moves with the prices, so a first step of one for one lands near, and the bracket then opens by doubling steps if it has not closed).
         * At most ANCHOR_READS reads of the band in all. Returns the move applied, 0 where there is none: a good with no scales, a year the band cannot be
         * read in (a side not above 0, or not a number, at the solve's own prices: a price below its freight, which is left as the solve had it), or a
         * bracket that does not close.
         */
        double anchor(int gi, double[] slice, double[] lam, int lamAt, double levelNow, double xStart) {
            Traded t = traded[gi];
            if (refLevel == null || !(t.sigma > 0) || !(t.phi > 0)) return 0;
            double target = levelNow / refLevel[gi];
            if (!(target > 0) || !Double.isFinite(target)) return 0;
            double[] p = new double[n];
            double f0 = off(t, slice, 0, p, target);
            int reads = 1;
            if (!Double.isFinite(f0)) return 0;
            double bx = 0, bf = f0, x = 0, f = f0;
            if (Double.isFinite(xStart) && xStart != 0) {
                x = xStart;
                f = off(t, slice, x, p, target);
                reads++;
                if (Double.isNaN(f)) return 0;
                f = Math.max(f, ANCHOR_LOW);
                if (Math.abs(f) < Math.abs(bf)) {
                    bx = x;
                    bf = f;
                }
            }
            double lo = Double.NaN, hi = Double.NaN, flo = 0, fhi = 0;
            if (f > 0) {
                hi = x;
                fhi = f;
            } else {
                lo = x;
                flo = f;
            }
            double step = Math.max(-ANCHOR_STEP, Math.min(ANCHOR_STEP, -f));
            for (int open = 0; open < ANCHOR_OPEN_STEPS && Math.abs(bf) >= ANCHOR_TOL && (Double.isNaN(lo) || Double.isNaN(hi)) && reads < ANCHOR_READS; open++) {
                x += step;
                f = off(t, slice, x, p, target);
                reads++;
                if (Double.isNaN(f)) return 0;
                f = Math.max(f, ANCHOR_LOW);
                if (Math.abs(f) < Math.abs(bf)) {
                    bx = x;
                    bf = f;
                }
                if (f > 0) {
                    hi = x;
                    fhi = f;
                } else {
                    lo = x;
                    flo = f;
                }
                step = Math.max(-ANCHOR_STEP, Math.min(ANCHOR_STEP, 2 * step));
            }
            if (Math.abs(bf) >= ANCHOR_TOL) {
                if (Double.isNaN(lo) || Double.isNaN(hi)) return 0;
                // the secant kept inside the bracket (Illinois: an end that stays twice running has its value halved), a bisection where the low end is
                // the floor reading
                int kept = 0;
                while (reads < ANCHOR_READS) {
                    boolean secant = flo > ANCHOR_LOW + 10;
                    double xn = secant ? hi - fhi * (hi - lo) / (fhi - flo) : .5 * (lo + hi);
                    if (!(xn > lo && xn < hi)) xn = .5 * (lo + hi);
                    if (xn == lo || xn == hi) break;
                    double fn = off(t, slice, xn, p, target);
                    reads++;
                    if (Double.isNaN(fn)) break;
                    fn = Math.max(fn, ANCHOR_LOW);
                    if (Math.abs(fn) < Math.abs(bf)) {
                        bx = xn;
                        bf = fn;
                    }
                    if (fn > 0) {
                        hi = xn;
                        fhi = fn;
                        if (kept == -1 && secant) flo *= .5;
                        kept = -1;
                    } else {
                        lo = xn;
                        flo = fn;
                        if (kept == 1) fhi *= .5;
                        kept = 1;
                    }
                    if (Math.abs(fn) < ANCHOR_TOL) break;
                }
            }
            if (bx == 0) return 0;
            double k = exp(bx);
            for (int i : t.makers) slice[i] *= k;
            lam[lamAt] *= exp(-(SUPPLY_ELASTICITY + DEMAND_ELASTICITY) * bx);
            return bx;
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
        /** The plain step from the last pass whose error was a number, and whether there is one: what the guard puts back (see solve). */
        double[] good;
        boolean have;
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
        return solve(t, gHat, null, carve, pin, level, tol, phat, lam, lamAt, work);
    }

    /**
     * The same solve with each maker's supply growth given apart from its nation's output (supplyGrowth, nullable: null is gHat, the nation's output
     * over the base year's; demand reads gHat either way). A made good's makers supply what they sell at the chained price (CU2), a scale a maker.
     *
     * THE GUARD. Anderson's acceleration can throw an iterate out of the numbers, and every pass after it is not a number either: before the guard the
     * solve ran to its sweeps and the next January started from the same prices (on drawn worlds 9 and 10 crude's solve left the numbers in 2165 and
     * 2191 and never came back; spec-world-prices 3.7). A pass whose size is not finite puts back the plain step from the last pass whose size was
     * (or the base year's prices and scale, log 0, if none), resets the acceleration and counts as a sweep; guards() counts them for the process.
     */
    static int solve(Traded t, double[] gHat, double[] supplyGrowth, double[] carve, boolean pin, double level, double tol,
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
            lnMake[k] = log(t.make[makers[k]] * (supplyGrowth == null ? gHat[makers[k]] : supplyGrowth[makers[k]]));
        }
        final double fixedLnLambda = log(lam[lamAt]);
        if (pin) x[mk] = fixedLnLambda;
        work.accel.reset();
        work.have = false;
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
            if (!Double.isFinite(size)) {
                if (work.have) System.arraycopy(work.good, 0, x, 0, dim);
                else Arrays.fill(x, 0, dim, 0);
                work.have = false;
                work.accel.reset();
                lastWorst = Double.POSITIVE_INFINITY;
                GUARDS.incrementAndGet();
                pass++;
                continue;
            }
            if (work.good == null || work.good.length < dim) work.good = new double[dim];
            System.arraycopy(g, 0, work.good, 0, dim);
            work.have = true;
            if (size > ACCEL_BACK * lastWorst) work.accel.reset();
            lastWorst = size;
            work.accel.mix(x, g, dim);
            pass++;
        }
        if (pin) lam[lamAt] = exp(x[mk]);
        return pass;
    }

    /** How many passes the solve's guard has put back, this process (see solve): 0 on a solve that never left the numbers. */
    static final java.util.concurrent.atomic.AtomicLong GUARDS = new java.util.concurrent.atomic.AtomicLong();

    /** How many times the solve's guard has put an iterate back in this process. */
    static long guards() { return GUARDS.get(); }

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

        /**
         * A good's floor and ceiling in US$ a tonne for x tonnes a year sold and m bought: the fills, less the world's answer within the year (x - m over
         * the world's slope). {floor, ceiling}, with the floor never above the ceiling; NaN on a side with no buyers or no sellers. Past every sink's cap
         * the floor is 0 (the clamp), and past every seller's output the ceiling is the scarcity bound (SCARCITY_MULTIPLE times the cheapest landed cost).
         * The one formula: WorldMarket.band() and Curve.floorAt/ceilingAt both read it (WN6).
         */
        double[] model(double x, double m) {
            double shift = slope > 0 ? (x - m) / slope : 0;
            double floor = ns > 0 ? Math.max(0, sell.level(x) - shift) : Double.NaN;
            double landed = nb > 0 ? -buy.level(m) : Double.NaN;
            if (nb > 0 && !Double.isFinite(landed)) landed = SCARCITY_MULTIPLE * -buy.level(0);   // past every seller's capacity: a bound, not infinity
            double ceiling = nb > 0 ? Math.max(Double.isNaN(floor) ? 0 : floor, landed - shift) : Double.NaN;
            return new double[] { floor, ceiling };
        }

        /** Who took a month's exports, by nation (index nation - 1, n of them), game units: the one formula allocate() and Curve.buyersAt() read (WN6). */
        double[] buyers(Good g, double unitsPerTonne, int n, double exportUnits) {
            double[] to = new double[n];
            double x = 12 * Math.max(0, exportUnits) / unitsPerTonne;
            if (x > 0 && ns > 0 && g.exportable()) {
                double lam = sell.level(x), sum = 0;
                for (int k = 0; k < ns && sell.net[k] > lam; k++) {
                    double q = sell.take(k, lam);
                    to[sBuyer[k]] = q;
                    sum += q;
                }
                scale(to, sum, exportUnits);
            }
            return to;
        }

        /** Who sold a month's imports, by nation (index nation - 1, n of them), game units: the one formula allocate() and Curve.sellersAt() read (WN6). */
        double[] sellers(Good g, double unitsPerTonne, int n, double importUnits) {
            double[] from = new double[n];
            double m = 12 * Math.max(0, importUnits) / unitsPerTonne;
            if (m > 0 && nb > 0 && g.importable()) {
                double lam = buy.level(m), sum = 0;
                for (int k = 0; k < nb && buy.net[k] > lam; k++) {
                    double q = buy.take(k, lam);
                    from[bNation[k]] = q;
                    sum += q;
                }
                scale(from, sum, importUnits);
            }
            return from;
        }
    }

    /** The result of a re-solve (see resolve): the month's step and the nations' answer with the city's tonnes delivered, and the price they were sold at (the served buyers' landed price before the sale, tonne-weighted), in US$ a tonne. */
    record Resolved(double step, double realised, double price, int served, int passes) { }

    /** Who took this month's exports and who sold this month's imports, by nation (index nation - 1), in game units. */
    public record Allocation(double[] exportUnitsTo, double[] importUnitsFrom) { }

    /**
     * One good's curve at the city's site, frozen when the fills were last rebuilt (WN6; docs/studies/spec-nations.md 8, the Trade screen's Partners
     * board): everything the screen draws of "where the next tonne goes" - the export curve, what the next tonne nets against how much the city sells,
     * the month's line and the world's, the points where each buyer joins, the buyers at what the city sells now, the band's floor and ceiling named by
     * their nations - read from one snapshot, by a thread that is not the model's.
     * <p>
     * WHY. The screens had the band (two numbers) and the allocation (an array), and anything more - the sinks, where each joins, who sets the level -
     * was in the private fills, readable only by a harness on the model's thread. Recomputing it in a screen is the failure CLAUDE.md names, and
     * reading the live fills from another thread reads half a rebuild. So the market publishes an immutable Curve for each good through a volatile
     * every time it rebuilds the fills (solveYear, restore, a new site or berth), and every figure here is the SAME formula band() and allocate()
     * read - floorAt/ceilingAt are band()'s two ends and buyersAt/sellersAt are allocate()'s two arrays, to the bit, because they are the same code.
     * <p>
     * UNITS. Prices (netbacks, landed costs, floors, ceilings, freights, levels) are in the band's unit: thousands of world dollars a game unit, sigma
     * times the model's US$ a tonne, what band() returns. Volumes in every method and record are game units a month, as band() and allocate() take
     * them; a share is of the world's use of the good a year (worldUseTonnesYear). Nations are 1-based, as Nations and the map number them; the
     * arrays of buyersAt/sellersAt are by index nation - 1, as allocate()'s are. A sink's freight is the buyer's price less its netback, and a
     * seller's its landed cost less its mill price; both are kept. The curve does not know the city's own trade: a screen holds that and asks at a volume.
     */
    public static final class Curve {
        /** Where the points() grid starts, as a share of the world's use of the good a year (WN6). */
        public static final double POINTS_FROM_SHARE = 1e-5;
        /** Where the points() grid stops at the latest, as a share of the world's use a year: sooner if the sale side's caps end first (WN6). */
        public static final double POINTS_TO_SHARE = .30;
        /** How many points() draws (WN6: the chart's resolution). */
        public static final int POINTS = 48;

        /** One sink of the sale side: a binding supplier whose best buyer takes the city's tonnes. */
        public record Sink(int supplier, int buyer, double netback, double freight, double buyerPrice, double slopeUnits, double capUnits,
                           double joinsAtUnits, double joinsAtShare) { }

        /** One seller of the purchase side: a maker landed at the city. */
        public record Seller(int nation, double mill, double landed, double freight, double slopeUnits, double capUnits,
                             double joinsAtUnits, double joinsAtShare) { }

        /**
         * The level of one side at a volume, and the nation that sets it. On the sale side level is the netback every taking buyer is brought down to
         * (levelAt), nation the buyer of the sink that joined last, supplier its binding supplier; on the purchase side level is the landed cost
         * every taking seller is brought up to (costLevelAt), nation the seller that joined last, supplier the same. With nothing taking (a
         * volume of 0) the level is the first sink's netback and nation names the first sink: the nation the next tonne goes to. taking is how many are
         * taking; nation and supplier are 0 and level NaN on a side that has none. A level is -infinity (sale) or +infinity (purchase) past every cap.
         */
        public record Level(double level, int nation, int supplier, int taking) { }

        /** One point of the chart: a share of the world's use, the same in game units a month, the month's line (netAt) and the world's line with no imports (floorAt(units, 0)). */
        public record Point(double share, double units, double net, double world) { }

        private final Good good;
        private final double sigma, unitsPerTonne, worldUse;
        private final int n;
        private final Fill fill;
        private final List<Sink> sinks;
        private final List<Seller> sellers;

        private Curve(Good good, double sigma, double unitsPerTonne, double worldUse, int n, Fill fill, double[] mill) {
            this.good = good;
            this.sigma = sigma;
            this.unitsPerTonne = unitsPerTonne;
            this.worldUse = worldUse;
            this.n = n;
            this.fill = fill;
            double perMonth = unitsPerTonne / 12;
            Sink[] s = new Sink[fill.ns];
            for (int k = 0; k < fill.ns; k++) {
                int j = fill.sBuyer[k];
                double net = fill.sell.net[k], joins = fill.sell.joinAt(k);
                s[k] = new Sink(fill.sNation[k] + 1, j + 1, sigma * net, sigma * (fill.buyerPrice[j] - net), sigma * fill.buyerPrice[j],
                        fill.sell.slope[k] * perMonth / sigma, fill.sell.cap[k] * perMonth, joins * perMonth, worldUse > 0 ? joins / worldUse : Double.NaN);
            }
            sinks = List.of(s);
            Seller[] b = new Seller[fill.nb];
            for (int k = 0; k < fill.nb; k++) {
                int i = fill.bNation[k];
                double landed = -fill.buy.net[k], joins = fill.buy.joinAt(k);
                b[k] = new Seller(i + 1, sigma * mill[i], sigma * landed, sigma * (landed - mill[i]), fill.buy.slope[k] * perMonth / sigma,
                        fill.buy.cap[k] * perMonth, joins * perMonth, worldUse > 0 ? joins / worldUse : Double.NaN);
            }
            sellers = List.of(b);
        }

        public Good good() { return good; }

        /** The price scale: the band's unit is sigma times US$ a tonne. */
        public double sigma() { return sigma; }

        /** Game units in a tonne. */
        public double unitsPerTonne() { return unitsPerTonne; }

        /** The world's use of the good in the year solved: every nation's demand summed (the nations' own, without the city's), tonnes a year. */
        public double worldUseTonnesYear() { return worldUse; }

        /** The world's use of the good in game units a month. */
        public double worldUseUnitsMonth() { return worldUse * unitsPerTonne / 12; }

        /** The world's answer within the year, in the model's own unit: tonnes a year (sold less bought) it takes to move the world price one US$ a tonne. */
        public double worldSlope() { return fill.slope; }

        /** The world's answer in the screen's units: game units a month (sold less bought) that move the band one unit. */
        public double worldSlopeUnits() { return fill.slope * unitsPerTonne / 12 / sigma; }

        /** A volume in game units a month as a share of the world's use a year (the share points() and the records use). */
        public double shareOfWorldUse(double unitsMonth) { return worldUse > 0 ? 12 * unitsMonth / unitsPerTonne / worldUse : Double.NaN; }

        /** What the sale side's sinks take together, game units a month (past it the floor is 0). */
        public double saleCapacityUnits() { return fill.sell.capacity() * unitsPerTonne / 12; }

        /** What the purchase side's sellers sell together, game units a month (past it the ceiling is the scarcity bound). */
        public double purchaseCapacityUnits() { return fill.buy.capacity() * unitsPerTonne / 12; }

        /** The sale side's sinks, best netback first (the order they join). */
        public List<Sink> sinks() { return sinks; }

        /** The purchase side's sellers, cheapest landed first (the order they join). */
        public List<Seller> sellers() { return sellers; }

        private double tonnes(double units) { return 12 * Math.max(0, units) / unitsPerTonne; }

        /** The band, {floor, ceiling}: WorldMarket.band()'s own body (it calls this), so the two agree to the bit. */
        double[] band(double exportUnitsMonth, double importUnitsMonth) {
            double[] b = fill.model(tonnes(exportUnitsMonth), tonnes(importUnitsMonth));
            return new double[] { good.exportable() ? sigma * b[0] : Double.NaN, good.importable() ? sigma * b[1] : Double.NaN };
        }

        /** The month's line: what the fills alone net at this volume of exports (the level), before the world's answer; -infinity past every cap, NaN with no buyers. */
        public double netAt(double exportUnitsMonth) {
            return good.exportable() && fill.ns > 0 ? sigma * fill.sell.level(tonnes(exportUnitsMonth)) : Double.NaN;
        }

        /** The band's floor at last month's exports and imports: exactly band()[0]. */
        public double floorAt(double exportUnitsMonth, double importUnitsMonth) { return band(exportUnitsMonth, importUnitsMonth)[0]; }

        /** The band's ceiling at last month's exports and imports: exactly band()[1]. */
        public double ceilingAt(double exportUnitsMonth, double importUnitsMonth) { return band(exportUnitsMonth, importUnitsMonth)[1]; }

        /** The world's answer within the year at these volumes, in the band's unit: what floorAt and ceilingAt are both lowered by, (sold - bought) over the world's slope. */
        public double worldAnswerAt(double exportUnitsMonth, double importUnitsMonth) {
            return fill.slope > 0 ? sigma * (tonnes(exportUnitsMonth) - tonnes(importUnitsMonth)) / fill.slope : 0;
        }

        /** Who takes this month's exports, game units by nation (index nation - 1): exactly allocate()'s exportUnitsTo. */
        public double[] buyersAt(double exportUnitsMonth) { return fill.buyers(good, unitsPerTonne, n, exportUnitsMonth); }

        /** Who sells this month's imports, game units by nation (index nation - 1): exactly allocate()'s importUnitsFrom. */
        public double[] sellersAt(double importUnitsMonth) { return fill.sellers(good, unitsPerTonne, n, importUnitsMonth); }

        /** How many sinks take at a volume of exports: the first ones in join order whose netback is above the level (the same test buyersAt makes). */
        public int buyersTaking(double exportUnitsMonth) {
            double x = tonnes(exportUnitsMonth);
            if (!good.exportable() || !(x > 0) || fill.ns == 0) return 0;
            double lam = fill.sell.level(x);
            int k = 0;
            while (k < fill.ns && fill.sell.net[k] > lam) k++;
            return k;
        }

        /** The level every taking buyer nets at this volume of exports, and the buyer that joined last (what sets the floor). */
        public Level levelAt(double exportUnitsMonth) {
            if (!good.exportable() || fill.ns == 0) return new Level(Double.NaN, 0, 0, 0);
            int taking = buyersTaking(exportUnitsMonth), k = Math.max(0, taking - 1);
            return new Level(netAt(exportUnitsMonth), fill.sBuyer[k] + 1, fill.sNation[k] + 1, taking);
        }

        /** The landed cost every taking seller asks at this volume of imports, and the seller that joined last (what sets the ceiling); at 0, the cheapest landed seller. */
        public Level costLevelAt(double importUnitsMonth) {
            if (!good.importable() || fill.nb == 0) return new Level(Double.NaN, 0, 0, 0);
            double m = tonnes(importUnitsMonth), lam = fill.buy.level(m);
            int taking = 0;
            if (m > 0) while (taking < fill.nb && fill.buy.net[taking] > lam) taking++;
            int k = Math.max(0, taking - 1);
            return new Level(sigma * -lam, fill.bNation[k] + 1, fill.bNation[k] + 1, taking);
        }

        /** The next k buyers not yet taking at this volume of exports, in the order they join, each with the volume (and share of the world's use) it joins at. */
        public List<Sink> nextBuyers(double exportUnitsMonth, int k) {
            int from = buyersTaking(exportUnitsMonth);
            return sinks.subList(Math.min(from, sinks.size()), Math.min(sinks.size(), from + Math.max(0, k)));
        }

        /** The chart's grid, POINTS of them. */
        public List<Point> points() { return points(POINTS); }

        /**
         * The chart's grid: count points log-spaced in the share of the world's use from POINTS_FROM_SHARE to POINTS_TO_SHARE or the sale side's
         * capacity, whichever is less, each {share, game units a month, the month's line (netAt), the world's line with no imports (floorAt(units, 0))}.
         * A point past the level's end (netAt not finite) is left out; empty with no buyers or no world use.
         */
        public List<Point> points(int count) {
            if (!good.exportable() || fill.ns == 0 || !(worldUse > 0) || count < 1) return List.of();
            double hi = Math.min(POINTS_TO_SHARE, fill.sell.capacity() / worldUse);
            if (!(hi > 0)) return List.of();
            double lo = Math.min(POINTS_FROM_SHARE, hi);
            Point[] out = new Point[count];
            int made = 0;
            for (int i = 0; i < count; i++) {
                double share = count == 1 ? hi : StrictMath.exp(StrictMath.log(lo) + (StrictMath.log(hi) - StrictMath.log(lo)) * i / (count - 1));
                double units = share * worldUse * unitsPerTonne / 12, net = netAt(units);
                if (!Double.isFinite(net)) continue;
                out[made++] = new Point(share, units, net, floorAt(units, 0));
            }
            return List.of(Arrays.copyOf(out, made));
        }
    }

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
    /** Each good's curve (null for a good with no price scale), rebuilt with the fills and published through this field for the screen's thread (WN6). */
    private volatile Curve[] curves;

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

    /**
     * The year growth() and level() read as the base (EP1): the trade table's year (Base.baseYear, 2019) on Historical, whose stand-in century holds the
     * file's first year's world, so its founding ratio is what it was; on a path that is not Historical, that year less the stand-in's years (Base.standInYears,
     * from the data: 1919). WHY: Random's path begins from the file's first year's world in the calendar's first year, 1900, not 2000, so the base year
     * has to sit as far into the path as it did before EP1 (19 years on) or a founding city meets a world 119 years of drawn growth away from the
     * table its flows are scaled from.
     */
    int baseYearRead() {
        return nations.path() == Nations.Path.HISTORICAL ? base.baseYear : base.baseYear - base.standInYears;
    }

    /* ----- the January solve ----- */

    /**
     * Solves every good for a year, from the last year solved (or from p-hat = 1 and lambda = 1 for the first), and reads the band structures at the
     * site. A year already solved is left alone. The answer is a pure function of (file, path, seed, year, the state it started from): a process-wide
     * cache of SOLVES_KEPT answers returns what a fresh solve from the same start would.
     *
     * CU2: the goods go in the chain's order (WorldChain), each finished before a good made of it reads it. A root's start is moved onto the year's level,
     * it is solved as always and then anchored (Base.anchor), so the reference city's band stands on its path whatever the makers' spread. A made good with
     * an own part is solved on the manufactures index's level, and every made good is then priced off its inputs (Base.price: what they cost each nation
     * landed, less the copper charge where the good carries one, plus its own part); its makers supply what they sell at that price (deriveSolution).
     */
    public void solveYear(int year) {
        if (year == this.year) return;
        long t0 = System.nanoTime();
        double[] g = growth(nations, year, baseYearRead(), n);
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
            // the chain's order: a root is solved and anchored, a made good is its inputs' cost plus its own part, and each is finished (copied into phat)
            // before a good made of it reads it
            WorldChain chain = base.chain;
            double[][] seenCache = new double[goodCount][];
            for (int gi : chain.order) {
                Traded t = base.traded[gi];
                double target = level(nations, priceIdx[base.ownLevelGood(gi)], year, baseYearRead());
                double[] slice = new double[n];
                System.arraycopy(phat, gi * n, slice, 0, n);
                if (!chain.made(gi)) {
                    // last year's anchor move: the saved prices are last year's anchored ones, their mean over last year's level
                    double xStart = 0;
                    if (warm >= 0) {
                        double mean = 0;
                        for (int i : t.makers) mean += t.w[i] * slice[i];
                        double move = log(mean / level(nations, priceIdx[gi], warm, baseYearRead()));
                        xStart = Double.isFinite(move) ? move : 0;
                        base.moveOntoLevel(gi, slice, lambda, gi, target);
                    }
                    passes[gi] = solve(t, g, null, true, target, SOLVE_TOL, slice, lambda, gi, work);
                    base.anchor(gi, slice, lambda, gi, target, xStart);
                } else {
                    boolean ownPart = chain.own[gi] > 0;
                    if (warm >= 0 && ownPart) base.moveOntoLevel(gi, slice, lambda, gi, target);
                    passes[gi] = ownPart ? solve(t, g, null, true, target, SOLVE_TOL, slice, lambda, gi, work) : 0;
                    base.price(gi, slice, phat, seenCache, nations, year, false);
                }
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
            // a root's makers supply as the solve has it; a made good's supply what they sell at the chained price (capacity follows the price to cost),
            // in the tonnes a year the sales buy: so its market clears by construction and a reload derives the same with no new saved field
            boolean made = base.chain.made(gi);
            for (int i : t.makers) {
                double ph = phat[gi * n + i];
                price[gi][i] = t.v * ph;
                if (made) {
                    double sales = t.ownX[i] * spending[i];
                    for (int q = t.sStart[i]; q < t.sStart[i + 1]; q++) sales += t.sX[q] * spending[t.sBuyer[q]];
                    supply[gi][i] = a[i] * sales * 1000 / (t.v * ph);
                } else {
                    supply[gi][i] = t.s0[i] * gHat[i] * lambda[gi] * exp(SUPPLY_ELASTICITY * log(ph));
                }
            }
        }
    }

    private void refill() {
        if (year < 0 || fillsFresh) return;
        for (int gi = 0; gi < goodCount; gi++) fills[gi] = fill(gi, price[gi], demand[gi], supply[gi]);
        // THE CURVES, from the fills just made: each is immutable (a fill and its levels are never written after fill() returns), and the array is
        // published whole, so the screen's thread reads either the last rebuild or this one, never half of one (WN6).
        Curve[] made = new Curve[goodCount];
        for (int gi = 0; gi < goodCount; gi++) {
            Traded t = base.traded[gi];
            if (Double.isNaN(t.sigma)) continue;
            made[gi] = new Curve(t.good, t.sigma, t.unitsPerTonne, worldUse(gi), n, fills[gi], price[gi].clone());
        }
        curves = made;
        // Fresh only now (WN5b): marked before the curves were built, a Curve that threw would have left band() reading the last rebuild's curve as this one's.
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
        return fills[gi].model(x, m);
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
        refill();
        return curves[gi].band(exportUnitsLastMonth, importUnitsLastMonth);
    }

    /**
     * Who took this month's exports and who sold this month's imports, from the same fills at those volumes: the tonnes a binding supplier's sink takes
     * are its slope times what its best buyer's netback leaves over the fill's, no more than that buyer uses, and they go to that buyer (its other
     * buyers fall in step and never catch up); a seller of the imports likewise by its own slope and landed cost, no more than it makes; each scaled so
     * they add to the volume exactly (past every cap, the caps scaled up). Zeros where a volume is 0 or the good has no buyers or no sellers.
     */
    public Allocation allocate(Good g, double exportUnits, double importUnits) {
        int gi = base.indexOf[g.ordinal()];
        if (gi < 0 || year < 0) return new Allocation(new double[n], new double[n]);
        refill();
        Fill f = fills[gi];
        double upt = base.traded[gi].unitsPerTonne;
        return new Allocation(f.buyers(g, upt, n, exportUnits), f.sellers(g, upt, n, importUnits));
    }

    private static void scale(double[] a, double sum, double total) {
        if (!(sum > 0)) return;
        for (int i = 0; i < a.length; i++) a[i] = a[i] * total / sum;
    }

    /* ----- reads for the wiring and the screens ----- */

    /** Whether the market models this good and the city has a side of it. */
    public boolean trades(Good g) { return base.trades(g); }

    /**
     * A good's curve at the site (WN6): null for a good the market does not model or one with no price scale, and before a year is solved. Immutable and
     * safe to read from the screen's thread; it is the snapshot of the last rebuild of the fills (a solved year, a restore, a new site or berth), each
     * of which publishes the new curves before it returns, so a city just loaded has its curves before its first month.
     */
    public Curve curve(Good g) {
        Curve[] c = curves;
        int gi = base.indexOf[g.ordinal()];
        return c == null || gi < 0 ? null : c[gi];
    }

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
     * Each nation's terms of trade in the year solved, in logs against the base year (index nation - 1; WN4, spec-exchange-rates 3.7): the log of its
     * export price index over its import price index. A nation's exports weight each good's mill price over the base year's (phat, which is exactly
     * those ratios) by its base-year exports of the good (make - ownX of the Traded arrays: what it makes less what it sells to itself); its imports
     * weight each good's world price ratio, the export-value-weighted mean of the makers' ratios, by its base-year imports of it (use - ownX). The
     * pairs' own flows (sX by seller, bMaker by buyer) would add up to the same; the whole-nation arrays are what the calibration keeps, and need no
     * walk of the pairs. A nation with no exports or no imports in the modelled goods has 0; so has every nation before a year is solved (every price
     * is still the base year's).
     */
    public double[] lnTermsOfTrade() {
        double[] expValue = new double[n], expSum = new double[n], impValue = new double[n], impSum = new double[n];
        for (int gi = 0; gi < goodCount; gi++) {
            Traded t = base.traded[gi];
            double worldSum = 0, worldWeight = 0, makeSum = 0, makeWeight = 0;
            for (int i = 0; i < n; i++) {
                double p = phat[gi * n + i];
                double x = Math.max(0, t.make[i] - t.ownX[i]);
                if (x > 0 && p > 0) {
                    worldSum += x * p;
                    worldWeight += x;
                    expValue[i] += x * p;
                    expSum[i] += x;
                }
                if (t.make[i] > 0 && p > 0) {
                    makeSum += t.make[i] * p;
                    makeWeight += t.make[i];
                }
            }
            double world = worldWeight > 0 ? worldSum / worldWeight : makeWeight > 0 ? makeSum / makeWeight : 1;
            for (int i = 0; i < n; i++) {
                double m = Math.max(0, t.use[i] - t.ownX[i]);
                if (m > 0) {
                    impValue[i] += m * world;
                    impSum[i] += m;
                }
            }
        }
        double[] ln = new double[n];
        for (int i = 0; i < n; i++) {
            if (!(expSum[i] > 0) || !(impSum[i] > 0)) continue;
            ln[i] = log((expValue[i] / expSum[i]) / (impValue[i] / impSum[i]));
        }
        return ln;
    }

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
        gHat = growth(nations, year, baseYearRead(), n);
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
    double levelTarget(int gi) { return level(nations, priceIdx[gi], year, baseYearRead()); }

    /** A good's makers' supply this year, tonnes a year, a copy: what they sell at the solved price, a root's as the solve has it. */
    double[] supply(int gi) { return supply[gi].clone(); }

    /** The charge this year's copper carries into a good (Base.charge): 0 for a good that carries none. */
    double charge(Good g) { return base.charge(g, nations, year, false); }

    /** The level of the price path a good's own part follows this year (the manufactures index's for a made good with an own part): the target of its solve. */
    double ownLevelTarget(int gi) { return level(nations, priceIdx[base.ownLevelGood(gi)], year, baseYearRead()); }

    /** The level a root's reference band is anchored to this year: its path's level over the level the literals stand on. */
    double anchorTarget(int gi) { return levelTarget(gi) / base.refLevel[gi]; }

    /** The manufactures index's good (machinery) among the modelled, -1 if it is not modelled. */
    int muv() { return base.muv; }

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
        // a made good's makers each supply what they sell at its chained price (a scale a maker, lambda 1), so the start clears exactly
        double[] lam1 = { lambda[gi] }, growth = null;
        if (base.chain.made(gi)) {
            lam1[0] = 1;
            growth = new double[n];
            for (int i : t.makers) {
                double ph = phat[gi * n + i];
                growth[i] = supply[gi][i] * t.v * ph / 1000 / (t.make[i] * exp((1 + SUPPLY_ELASTICITY) * log(ph)));
            }
        }
        Work w = new Work(n);
        int sweeps = solve(t, gHat, growth, carve, false, 1, RESOLVE_TOL, start, lam1, 0, w);
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

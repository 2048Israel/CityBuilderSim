# WorldMarket.java - 1,696 lines · 93 methods · 28 constants · model

`ham/citybuildersim/WorldMarket.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The world market: each traded good's price in each of the 196 nations on a
> smooth spatial equilibrium (a CES gravity model), solved once a year, and
> the band the city sees in it - the import ceiling it pays and the export
> floor it nets - which move with the tonnes the city itself trades (batch
> WN3; docs/studies/spec-nations.md 4).
> 
> WHY THIS EXISTS. The city's world was a price list: Good.worldImportPrice()
> and worldExportPrice(), two literals a good, the same in every city, every
> year, whatever the city sold (spec-nations 1.1 and 11, item 4). A city that
> floods a market found the market where it had left it. Now the nations make
> and use each good in the amounts BACI says (WorldData), grow on the world's
> path (Nations), and trade as the file's kernel says they do; the city is one
> more trader that sells where its binding suppliers' best buyers net most and buys from the
> cheapest landed sellers first, and its tonnes move the prices it gets. The
> game's literals stay the prices (D12): each good has a price scale and a
> freight scale (sigma, phi) set so that a Halifax city in 2025 on the
> Historical path, trading nothing, sees exactly today's floor and ceiling; a
> city elsewhere, or a year later, sees what the world makes of it.
> 
> UNITS. The model's prices are US$ a tonne (the base year's dollars times the
> good's real price path) and its quantities tonnes a year; the city's month
> is converted at the edge (tonnes a year = 12 x units a month over the good's
> units a tonne) and the band leaves in the game's money, thousands of world
> dollars a game unit, sigma times the model's.
> 
> THE JANUARY SOLVE is the nations' alone (the city is not in it): exact hat
> algebra on the base year's flows, per good, with each maker's own mill price
> p-hat and one world supply scale lambda that holds the mean mill price to the
> good's real price path (section 2). The city moves its own prices by the
> month's step (section 4) at its volume, so the solve is a pure function of
> (file, path, seed, year) and harness cities on one world share it.
> 
> THE BASE is everything that is the file's: the pairs in two layouts, the
> freight table, the nodes, the calibration. It is immutable, built once a
> process (Base.load caches by the file's path, size and time) and shared by
> every market on that file. A WorldMarket is one city's: its site, its berth,
> its solved year. It is not thread-safe; the Base is.
> 
> Nothing in the game calls this yet (WN3's first slice): WorldMarketCheck
> holds it, on the fixture and, when it is named, on the full file.

**Uses:** [Good](Good.md) (22), [WorldData](WorldData.md) (15), [Nations](Nations.md) (12), [World](World.md) (3), [Freight](Freight.md) (1)

**Used by (6):** [Game](Game.md), [Markets](Markets.md), [PartnerTrade](PartnerTrade.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [WorldMarketCheck](WorldMarketCheck.md)

## Sections

| line | section |
|---:|---|
| 59 | THE DIALS - each with its source: (est.) is the game's estimate |
| 144 | THE MATH - StrictMath, so a solve is the same on every machine |
| 151 | A TRADED GOOD - the base year's pairs, in two layouts, and the good's calibration |
| 274 | THE LEGS - the city's freight to the nations, from where it stands |
| 284 | THE BASE - the file's, immutable, once a process |
| 411 | · what a caller reads |
| 465 | · places |
| 564 | · the calibration |
| 697 | THE YEAR'S INPUTS - growth and the price path, from the nations |
| 733 | THE SOLVE - one good's January, the nations alone |
| 953 | THE MARKET - one city's site, solved year and band |
| 1109 | · where the city stands |
| 1150 | · the January solve |
| 1211 | · what a solved year implies: prices, demand, supply, and then at the site the sorted sides |
| 1367 | · the city's month: the fills and the band |
| 1445 | · reads for the wiring and the screens |
| 1481 | · the save |
| 1508 | FOR THE CHECK - what a harness reads that the game does not |
| 1661 | SORTING - indices by a key, stable, no boxing |

## Enum constants

| line | constant | says |
|---:|---|---|
| 135 | `WorldMarket.Fit.CALIBRATED` | Both sides are the city's and both literals are met exactly. |
| 137 | `WorldMarket.Fit.ONE_SIDED` | The city has one side of it; the freight scale is its class's median and the price scale meets that side. |
| 139 | `WorldMarket.Fit.CLASS_MEDIAN` | Two sides but no freight scale reproduces their ratio; the class median stands and the price scale meets the ceiling. |
| 141 | `WorldMarket.Fit.NONE` | The city has neither side (or the world lacks the good): nothing to read. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `WorldMarket.SOLVE_TOL` | `1e-4` | How close every maker's sales must come to its supply, and the price index to its path, for a January to stop: 0.01% (spec-nations 4.3). |
| 67 | `WorldMarket.SOLVE_MAX_PASSES` | `2_000` | The most sweeps a good's January takes before it stops and says so in the log. |
| 70 | `WorldMarket.SUPPLY_ELASTICITY` | `.4` | Supply's elasticity to a maker's own mill price (est.: the nations prototype's ES). |
| 73 | `WorldMarket.DEMAND_ELASTICITY` | `.5` | Demand's elasticity to a buyer's price index (est.: the nations prototype's ED). |
| 76 | `WorldMarket.HOME_KM` | `300` | A nation's haul to itself, km by land, no handling: the city's leg to its host (est.: the prototype's). |
| 79 | `WorldMarket.ENTRY_NODES` | `6` | How many other nations' nodes, nearest by great circle, a city can enter the world through, besides its host. |
| 82 | `WorldMarket.MAX_LAND_KM` | `2_500` | The farthest, by great circle, a node is reached by land from the city, km; a city with a berth reaches a coastal node by sea at any distance (est.). |
| 85 | `WorldMarket.REFERENCE_LAT` | `44.65, REFERENCE_LON = - 63.57` | The reference city's place, Halifax: where the game's literals are the world's prices (D12). |
| 88 | `WorldMarket.REFERENCE_YEAR` | `2025` | The year the literals are set in (the Historical path's last year of data, seed-free). |
| 91 | `WorldMarket.SOLVES_KEPT` | `64` | The solved years kept in the process-wide cache, so harness cities on one world share a January. |
| 94 | `WorldMarket.BASE_MS` | `2_000` | The Base on the full file, read and calibrated, takes at most this many milliseconds. |
| 97 | `WorldMarket.SOLVE_COLD_MS` | `600` | The founding solve (year 2000, every good, from p-hat = 1) takes at most this many milliseconds. |
| 100 | `WorldMarket.SOLVE_WARM_MS` | `60` | A January (every good, from last year's answer) takes at most this many milliseconds. |
| 103 | `WorldMarket.BAND_MONTH_US` | `200` | A month's bands for every good take at most this many microseconds. |
| 106 | `WorldMarket.ACCEL_MEMORY` | `5` | The steps Anderson's acceleration remembers: the solve is a fixed-point iteration that closes about a tenth of its distance a pass, and the last few steps tell it where it is going. |
| 109 | `WorldMarket.ACCEL_BACK` | `10` | The solve forgets what the acceleration remembers when a pass's worst error is this many times the pass before's (the model has stopped being linear there). |
| 112 | `WorldMarket.SHARE_FLOOR` | `1e-3` | The least share of a buyer's composite a binding supplier is taken to have, so that a supplier that sells the buyer next to nothing does not make its sink's slope infinite (est.). |
| 115 | `WorldMarket.MAX_STEP` | `1` | The most a maker's log price moves in one pass, whatever its imbalance (a factor of e: the first passes of a cold solve start far away). |
| 118 | `WorldMarket.RESOLVE_TOL` | `1e-7` | The tolerance of the re-solve the check judges the month's step against (the nations' solve with the city's tonnes delivered). |
| 121 | `WorldMarket.RESOLVE_FLOOR` | `1e-6` | The least share of a buyer's demand a delivered city can leave to the nations in the re-solve (a floor, so spending stays above zero). |
| 124 | `WorldMarket.RHO_MIN` | `1e-5, RHO_MAX = 1e5` | The range the freight scale is looked for in, a multiple of the file's reference freight (the scan then bisects between two neighbours). |
| 127 | `WorldMarket.RHO_SCAN` | `80, RHO_BISECT = 200` | The scan's steps across RHO_MIN to RHO_MAX, and the bisection's cap. |
| 130 | `WorldMarket.PHI_FALLBACK` | `1` | The freight scale a world with no good that calibrates stands on (est.: the file's reference freight as it is). |
| 295 | `WorldMarket.Base.LOADED` | `new HashMap<>()` |  |
| 296 | `WorldMarket.Base.OF` | `new IdentityHashMap<>()` |  |
| 695 | `WorldMarket.STAND_IN` | `0x57A4D1` | Draws a stand-in's nation (Nations.scramble's "what"). |
| 963 | `WorldMarket.SOLVES` | `new LinkedHashMap<>(16,.75f, true) { @ Override protected boolean removeEldes...` |  |
| 1390 | `WorldMarket.SCARCITY_MULTIPLE` | `4` | Where the ceiling stops when the city buys more than every seller makes, as a multiple of the cheapest landed cost: past every cap the fill has no level (infinity), and a market's strike needs a finite ceiling. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 161 | `final Good good` |  |
| 162 | `final String key, goodClass` |  |
| 163 | `final int number, n` |  |
| 164 | `final double eps, v, unitsPerTonne` |  |
| 165 | `final double[] make, use, ownX, ownPi` |  |
| 166 | `final int[] sStart, sBuyer, bStart, bMaker` |  |
| 167 | `final double[] sX, sPi, bPi` |  |
| 168 | `final int[] makers, users` |  |
| 169 | `final double[] w, s0, d0` |  |
| 170 | `double sigma` |  |
| 171 | `Fit fit` |  |
| 173 | `double refFloor` | The reference city's floor and ceiling in the game's money, 2025, as the calibration left them. |
| 298 | `final int n` |  |
| 299 | `final String[] iso, name` |  |
| 300 | `final double[] lat, lon, baseOutput` |  |
| 301 | `final boolean[] landlocked` |  |
| 302 | `final int baseYear` |  |
| 303 | `final double radius, seaUsd, landUsd, handlingUsd, landDetour, seaDetour` |  |
| 305 | `final double[] freight` | US$ a tonne between nations, row-major (from * n + to), 0 on the diagonal. |
| 307 | `final int[] areaSeller` | For an owner number above n (an area), the nation that sells its land, 1-based, or 0. |
| 308 | `final Traded[] traded` |  |
| 309 | `final int[] indexOf` |  |
| 310 | `final int refHost` |  |
| 311 | `final Legs reference` |  |
| 312 | `final long buildMillis` |  |
| 313 | `private final WorldData owners` |  |
| 315 | `final int[] fits` | How many goods calibrated, and how many were one-sided, fell back to the class median, or have no side. |
| 739 | `final double[] u, a, phi, h, x, g, lnMake` |  |
| 740 | `final Accel accel` |  |
| 742 | `double worst, levelError` | The worst clearing error and the level error at the last pass, for the log. |
| 856 | `final int m` |  |
| 857 | `final double[][] dF, dG` |  |
| 858 | `final double[] fPrev, gPrev, f, gamma, atb` |  |
| 859 | `final double[][] ata` |  |
| 860 | `int cols, next` |  |
| 861 | `boolean have` |  |
| 976 | `final int len` |  |
| 977 | `final double[] net, slope, cap` |  |
| 978 | `private final int[] joinEvent` |  |
| 979 | `private final double[] evLam, evT, evSm, evSmn, evSc` |  |
| 1055 | `double[] buyerPrice` |  |
| 1056 | `double slope` |  |
| 1057 | `int ns, nb` |  |
| 1058 | `int[] sNation, sBuyer, bNation` |  |
| 1059 | `Levels sell, buy` |  |
| 1068 | `private final Base base` |  |
| 1069 | `private final Nations nations` |  |
| 1070 | `private final int n, goodCount` |  |
| 1071 | `private final int[] priceIdx` |  |
| 1072 | `private final Work work` |  |
| 1073 | `private double[] phat, lambda, gHat` |  |
| 1074 | `private int[] passes` |  |
| 1075 | `private int year` |  |
| 1076 | `private long solveNanos` |  |
| 1077 | `private double[][] price, demand, supply, elastic, aSolved, phiSolved` |  |
| 1078 | `private Fill[] fills` |  |
| 1079 | `private boolean fillsFresh` |  |
| 1081 | `private double siteLat` |  |
| 1082 | `private boolean berth` |  |
| 1083 | `private Legs legs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1640 | **type** `public final class WorldMarket` | The world market: each traded good's price in each of the 196 nations on a smooth spatial equilibrium (a CES gravity model), solved once a year, and the band the city sees in it - the import ceiling it pays and the ex... |

### THE DIALS - each with its source: (est.) is the game's estimate (lines 59-143)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 10 | **type** `public enum Fit` | How a good came by its price scale and freight scale. |

### THE MATH - StrictMath, so a solve is the same on every machine (lines 144-150)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 1 | `private static double exp(double x)` |  |
| 149 | 1 | `private static double log(double x)` |  |

### A TRADED GOOD - the base year's pairs, in two layouts, and the good's calibration (lines 151-273)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 113 | **type** `static final class Traded` | One traded good as the file gives it, copied into arrays: the kept pairs by seller (sStart, sBuyer, sX, sPi) and by buyer (bStart, bMaker, bPi), the own pair apart (ownX, ownPi), make and use thousand US$ of the base ... |
| 175 | 82 | `private Traded(WorldData data, int number, Good good, int n)` _(in WorldMarket.Traded)_ |  |
| 259 | 13 | `static Traded of(WorldData data, int number, int n)` _(in WorldMarket.Traded)_ | The good as the file has it, or null when it is not one the market models (a key that is no Good, a unit value or units a tonne that is no number above 0, no maker or no user). |

### THE LEGS - the city's freight to the nations, from where it stands (lines 274-283)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 1 | **type** `record Legs(int host, boolean berth, double[] fc, double[] fp)` | What a site's freight comes to: its host nation (1-based), whether it has a berth, fc[j] the US$ a tonne from the city to nation j (the cheapest way in), and fp[k * n + j] the nations' freight in the city's view, min(... |

### THE BASE - the file's, immutable, once a process (lines 284-696)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 399 | **type** `public static final class Base` | What the file is to the market, copied into arrays: the nations and their nodes, the freight in US$ a tonne, every traded good's pairs and its calibration. |
| 318 | 11 | `public static synchronized Base load(Path file) throws IOException` _(in WorldMarket.Base)_ | Reads the file and keeps it open (the host lookups need it); one Base per file path, size and time, for the process. |
| 331 | 10 | `public static Base of(WorldData data)` _(in WorldMarket.Base)_ | A Base from an open file, one per file object for the process. |
| 342 | 60 | `private Base(WorldData data, long t0)` _(in WorldMarket.Base)_ |  |
| 403 | 7 | `private static double dial(WorldData data, String name, double fallback)` _(in WorldMarket.Base)_ |  |
| 414 | 1 | `public int nationCount()` _(in WorldMarket.Base)_ | How many nations there are (the file's N). |
| 417 | 5 | `public List<Good> goods()` _(in WorldMarket.Base)_ | The goods the market models, in the file's order. |
| 424 | 4 | `public boolean trades(Good g)` _(in WorldMarket.Base)_ | Whether the file has this good, traded, scaled, and the city has a side of it to trade. |
| 430 | 4 | `public Fit fit(Good g)` _(in WorldMarket.Base)_ | How a good's scales were set. |
| 436 | 4 | `public double sigma(Good g)` _(in WorldMarket.Base)_ | A good's price scale: thousands of world dollars a game unit for a dollar a tonne; NaN for a good the market does not model. |
| 442 | 4 | `public double phi(Good g)` _(in WorldMarket.Base)_ | A good's freight scale: the multiple of the file's reference freight the city's view of it uses. |
| 448 | 4 | `public double referenceFloor(Good g)` _(in WorldMarket.Base)_ | The reference city's floor, 2025, Historical, in the game's money: the literal export price for a good that calibrated. |
| 454 | 4 | `public double referenceCeiling(Good g)` _(in WorldMarket.Base)_ | The reference city's ceiling likewise. |
| 460 | 1 | `public long buildMillis()` _(in WorldMarket.Base)_ | The time the Base took to build, milliseconds. |
| 463 | 1 | `public int referenceHost()` _(in WorldMarket.Base)_ | The nation the reference city (Halifax) stands in, 1-based. |
| 468 | 7 | `double gc(double lat1, double lon1, double lat2, double lon2)` _(in WorldMarket.Base)_ | The great circle distance in km between two points. |
| 477 | 12 | `int nearest(double la, double lo)` _(in WorldMarket.Base)_ | The nation whose node is nearest a point, 1-based. |
| 491 | 11 | `public int hostOf(double la, double lo)` _(in WorldMarket.Base)_ | The host of a site: the owner of the point when that is a nation, the nation that sells an area's land when it is one, else the nation with the nearest node (1-based). |
| 507 | 17 | `public int standIn(long groundSeed)` _(in WorldMarket.Base)_ | A generated world's stand-in (D23): a coastal nation drawn by the seed, weighted by its output in the base year, which is where the world's trade is (a uniform draw lands on a remote island one time in five). |
| 526 | 37 | `Legs legs(double la, double lo, boolean berth, int host)` _(in WorldMarket.Base)_ | The legs of a site at (la, lo) in a host nation, with or without a berth (see Legs). |
| 571 | 65 | `private void calibrate(WorldData data)` _(in WorldMarket.Base)_ | Sets every good's (sigma, phi): the reference city (Halifax, no berth) in REFERENCE_YEAR on the Historical path, trading nothing, sees floor = the literal export price and ceiling = the literal import price. |
| 638 | 22 | `double[] referenceBand(Traded t, double[] p, double rho)` _(in WorldMarket.Base)_ | The reference city's {floor, ceiling} in US$ a tonne at freight scale rho, for prices p (makers' mill prices): the best netback and the cheapest landed cost, with nothing traded. |
| 662 | 18 | `private double findRho(Traded t, double[] p, double target)` _(in WorldMarket.Base)_ | The freight scale at which the reference city's ceiling over its floor is target, or NaN when none in RHO_MIN..RHO_MAX does. |
| 681 | 4 | `private double ratio(Traded t, double[] p, double rho)` _(in WorldMarket.Base)_ |  |
| 686 | 6 | `private static double median(List<Double> values)` _(in WorldMarket.Base)_ |  |

### THE YEAR'S INPUTS - growth and the price path, from the nations (lines 697-732)

| line | len | member | says |
|---:|---:|---|---|
| 702 | 8 | `static double[] growth(Nations nations, int year, int baseYear, int n)` | Each nation's output in a year over its output in the base year; 1 where either is no number above 0. |
| 712 | 12 | `static int[] priceIndices(Nations nations, Traded[] traded)` | For each modelled good, its number in the nations' price layer (1-based), 0 where it has none. |
| 726 | 6 | `static double level(Nations nations, int priceIdx, int year, int baseYear)` | The mean mill price's target in a year over the base year's: the good's real price path, 1 where it has none. |

### THE SOLVE - one good's January, the nations alone (lines 733-952)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 17 | **type** `static final class Work` | One solve's scratch, sized for n nations: reused across goods, never kept. |
| 744 | 10 | `Work(int n)` _(in WorldMarket.Work)_ |  |
| 773 | 76 | `static int solve(Traded t, double[] gHat, double[] carve, boolean pin, double level, double tol, double[] phat, double[] lam, i...` | One good's solve, in place: phat[0..n) is each nation's mill price over the base year's and lam[lamAt] the world's supply scale, both the start and the answer. |
| 855 | 97 | **type** `static final class Accel` | Anderson acceleration of a fixed-point iteration x -> g(x): it keeps the last few steps and moves x to the point their linear model says has no residual (the least-squares combination of the last differences), which t... |
| 863 | 10 | `Accel(int maxDim)` _(in WorldMarket.Accel)_ |  |
| 874 | 5 | `void reset()` _(in WorldMarket.Accel)_ |  |
| 881 | 43 | `void mix(double[] x, double[] g, int dim)` _(in WorldMarket.Accel)_ | Replaces x with the next iterate, given g = G(x). |
| 926 | 25 | `private boolean cholesky(int n)` _(in WorldMarket.Accel)_ | Solves ata gamma = atb for the first n unknowns by Cholesky; false if the matrix is not positive definite. |

### THE MARKET - one city's site, solved year and band (lines 953-1108)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 1 | **type** `private record Solved(double[] prices, double[] scales, int[] passes)` | A solved year kept for the next city on the same world: the answer and how many sweeps each good took. |
| 961 | 1 | **type** `private record SolveKey(Base base, Nations.Path path, long seed, int year, int warmYear, long start)` | What a solve is a function of: the file, the path and seed, the year, and the state it started from (its year and a fingerprint of its bits; -1 and 0 for cold). |
| 975 | 74 | **type** `static final class Levels` | A water-filling with caps: sinks sorted by netback, best first, each with a slope (tonnes a year per US$ a tonne its netback may fall) and a cap (the most tonnes a year it takes). |
| 981 | 40 | `Levels(double[] net, double[] slope, double[] cap, int len)` _(in WorldMarket.Levels)_ |  |
| 1023 | 1 | `double capacity()` _(in WorldMarket.Levels)_ | The most the sinks take together, tonnes a year. |
| 1026 | 13 | `double level(double x)` _(in WorldMarket.Levels)_ | The level at which the sinks take x tonnes a year: the best netback for 0, -infinity past the caps. |
| 1041 | 4 | `double take(int k, double lam)` _(in WorldMarket.Levels)_ | What sink k takes at a level, tonnes a year. |
| 1047 | 1 | `double joinAt(int k)` _(in WorldMarket.Levels)_ | The tonnes a year at which sink k joins the fill. |
| 1054 | 7 | **type** `private static final class Fill` | The city's freight sides of one good at its site. |
| 1063 | 1 | **type** `record Resolved(double step, double realised, double price, int served, int passes)` | The result of a re-solve (see resolve): the month's step and the nations' answer with the city's tonnes delivered, and the price they were sold at (the served buyers' landed price before the sale, tonne-weighted), in ... |
| 1066 | 1 | **type** `public record Allocation(double[] exportUnitsTo, double[] importUnitsFrom)` | Who took this month's exports and who sold this month's imports, by nation (index nation - 1), in game units. |
| 1086 | 22 | `public WorldMarket(Base base, Nations nations)` | A market on a Base for a world's nations, standing at the reference city (Halifax, no berth) until it is told where. |

### where the city stands (lines 1109-1149)

| line | len | member | says |
|---:|---:|---|---|
| 1112 | 8 | `public void setSite(double lat, double lon, boolean berth)` | Puts the city at a point (an Earth city), with or without a berth: its host is the owner of the point. |
| 1122 | 9 | `public void setStandIn(long groundSeed, boolean berth)` | Puts the city where a generated world's ground seed says (D23): a coastal nation drawn by its output, at its node, that nation its host. |
| 1133 | 7 | `public void setBerth(boolean berth)` | Gives the city a berth, or takes it away; the legs are worked out again only if that changes it. |
| 1142 | 1 | `public int standIn(long groundSeed)` | The nation a generated world's ground seed stands the city in (D23; see Base.standIn): the one setStandIn uses, 1-based. |
| 1145 | 1 | `public int host()` | The nation the city stands in, 1-based. |
| 1148 | 1 | `public boolean berth()` | Whether the city has a berth. |

### the January solve (lines 1150-1210)

| line | len | member | says |
|---:|---:|---|---|
| 1157 | 34 | `public void solveYear(int year)` | Solves every good for a year, from the last year solved (or from p-hat = 1 and lambda = 1 for the first), and reads the band structures at the site. |
| 1193 | 1 | `public int solvedYear()` | The year solved, -1 before the first. |
| 1196 | 1 | `public double lastSolveMillis()` | The time the last solveYear took, milliseconds (the cache's answer included). |
| 1199 | 4 | `public int passes(Good g)` | The sweeps a good's last solve took (0 for a good the market does not model or from the cache's answer's own count). |
| 1204 | 6 | `private static long fingerprint(double[] a, double[] b)` |  |

### what a solved year implies: prices, demand, supply, and then at the site the sorted sides (lines 1211-1366)

| line | len | member | says |
|---:|---:|---|---|
| 1213 | 45 | `private void deriveSolution()` |  |
| 1259 | 5 | `private void refill()` |  |
| 1266 | 15 | `private double pairShare(int gi, Traded t, int k, int j)` | The share a supplier k has of buyer j's composite in the solved state (its flow over the buyer's spending on the nations), 0 where the file keeps no pair. |
| 1283 | 83 | `private Fill fill(int gi, double[] p, double[] d, double[] s)` | One good's sorted sides at the site, for mill prices p, demand d and supply s (tonnes a year). |

### the city's month: the fills and the band (lines 1367-1444)

| line | len | member | says |
|---:|---:|---|---|
| 1374 | 11 | `double[] model(int gi, double x, double m)` | A good's floor and ceiling in US$ a tonne for x tonnes a year sold and m bought: the fills, less the world's answer within the year (x - m over the world's slope). |
| 1396 | 8 | `public double[] band(Good g, double exportUnitsLastMonth, double importUnitsLastMonth)` | The band for a good in the game's money, thousands of world dollars a game unit: {floor, ceiling}, from last month's exports and imports in game units. |
| 1411 | 28 | `public Allocation allocate(Good g, double exportUnits, double importUnits)` | Who took this month's exports and who sold this month's imports, from the same fills at those volumes: the tonnes a binding supplier's sink takes are its slope times what its best buyer's netback leaves over the fill'... |
| 1440 | 4 | `private static void scale(double[] a, double sum, double total)` |  |

### reads for the wiring and the screens (lines 1445-1480)

| line | len | member | says |
|---:|---:|---|---|
| 1448 | 1 | `public boolean trades(Good g)` | Whether the market models this good and the city has a side of it. |
| 1451 | 1 | `public int nationCount()` | How many nations there are. |
| 1454 | 1 | `public String iso3(int nation)` | A nation's ISO code, 1-based. |
| 1457 | 1 | `public String name(int nation)` | A nation's name. |
| 1460 | 1 | `public double nodeLat(int nation)` | The latitude of a nation's node. |
| 1463 | 1 | `public double nodeLon(int nation)` | The longitude of a nation's node. |
| 1466 | 5 | `public double millPrice(Good g, int nation)` | A nation's mill price for a good in the game's money (sigma times the model's), NaN for a nation that makes none or a good not modelled. |
| 1473 | 7 | `public double buyerPrice(Good g, int nation)` | What the cheapest landed seller asks a nation's buyers for a good, in the game's money; NaN where none reaches. |

### the save (lines 1481-1507)

| line | len | member | says |
|---:|---:|---|---|
| 1484 | 1 | `public double[] savedPrices()` | Every modelled good's mill prices over the base year's, dense, good-major (goods x nations): what the save keeps of the solve. |
| 1487 | 1 | `public double[] savedScales()` | Every modelled good's supply scale, in the same order. |
| 1493 | 14 | `public boolean restore(int year, double[] prices, double[] scales)` | Restores a solved year from what the save kept, deriving everything without solving: the bands and allocations are bit for bit what the solve gave. |

### FOR THE CHECK - what a harness reads that the game does not (lines 1508-1660)

| line | len | member | says |
|---:|---:|---|---|
| 1512 | 1 | `Base base()` |  |
| 1515 | 1 | `int indexOf(Good g)` | A good's index among the modelled, -1 if none. |
| 1518 | 1 | `double[] phat(int gi)` | A good's mill prices over the base year's, as solved. |
| 1520 | 1 | `double lambda(int gi)` |  |
| 1522 | 1 | `double[] gHat()` |  |
| 1525 | 1 | `double levelTarget(int gi)` | The mean mill price's target for a good this year. |
| 1528 | 1 | `double demand(int gi, int nation)` | A nation's use of a good this year (1-based), tonnes a year. |
| 1531 | 5 | `double worldUse(int gi)` | The world's use of a good this year, tonnes a year. |
| 1538 | 5 | `double[] extremes(int gi)` | A good's city-side figures: {first netback, cheapest landed cost, the world's slope}, US$ a tonne and tonnes a year per US$ a tonne. |
| 1545 | 11 | `double[][] sellers(int gi, double x)` | The sinks the fill at x tonnes a year serves, in the order they join: rows of {the best buyer's nation (1-based), tonnes a year, its netback before the sale, the level at the end of the fill (-infinity past every cap)... |
| 1558 | 7 | `double[][] sellSide(int gi)` | A good's sinks at the site by netback, best first: {the best buyer's nation (1-based), slope (tonnes a year per US$ a tonne), its netback US$ a tonne, the binding supplier's nation, cap tonnes a year}. |
| 1567 | 7 | `double[][] buySide(int gi)` | A good's makers at the site by landed cost, cheapest first: {nation (1-based), slope, landed cost US$ a tonne, cap tonnes a year}. |
| 1576 | 5 | `double[] capacities(int gi)` | What all a good's sinks take together at the site, and all its sellers sell: {tonnes a year, tonnes a year}. |
| 1583 | 5 | `static void forgetSolves()` | Forgets the solved years every market shares, so the next solve is a fresh one (a harness proves a solve repeats itself, not that the cache does). |
| 1590 | 12 | `double servedPrice(int gi, double x)` | The landed price at the buyers the fill at x tonnes a year serves, before the sale, weighted by the tonnes; the best buyer's where it sells nothing. |
| 1604 | 6 | `double joinsAt(int gi, int nation)` | The tonnes a year of sales at which the fill reaches a nation's buyers (1-based; it reaches them through the best buyer of their binding supplier), or NaN if it never does. |
| 1617 | 43 | `Resolved resolve(int gi, double x)` | The check's re-solve of the month's step: the fill at x tonnes a year sold, then the nations' solve again with the tonnes the fill delivers taken out of their buyers' demand (their spending on the nations' goods P-hat... |

### SORTING - indices by a key, stable, no boxing (lines 1661-1696)

| line | len | member | says |
|---:|---:|---|---|
| 1666 | 3 | `static void sortBy(int[] idx, int len, double[] key, boolean descending)` | Sorts idx[0..len) by key[idx], descending or ascending; equal keys keep the order they were given in. |
| 1670 | 22 | `private static void mergeSort(int[] a, int[] tmp, int lo, int hi, double[] key, boolean desc)` |  |
| 1693 | 3 | `private static boolean before(double[] key, int x, int y, boolean desc)` |  |


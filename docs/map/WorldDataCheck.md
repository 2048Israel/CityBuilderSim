# WorldDataCheck.java - 1,384 lines · 45 methods · 30 constants · harnesses

`ham/citybuildersim/WorldDataCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The nations' world file's reader (batch WN0; the project's spec-nations.md
> 3.3): WorldData's reading of world-1.bin held to tools/world/worldread.py
> figure for figure, and the file's own promises held to the file: its sums
> close, its freight table keeps the triangle rule, its base year is the
> smooth equilibrium its prefs say, its borders own every cell of land.
> 
> WHY. The nations' world puts a country's borders, trade, people, output,
> money and distance in the game, and a wrong figure there is a city whose
> neighbour trades with the wrong nation or pays the wrong price for a tonne.
> The reader is checked against the Python that wrote the file and reads it
> independently; the file against the rules FORMAT.txt states of it, which the
> pipeline could break without the reader noticing.
> 
> THE DATA. The full world-1.bin is not in the repository; it is the asset of
> the GitHub release world-1, which tools/earth/get-earth.bat fetches into
> data/ with "world-1" as its second argument (WorldData.DATA_FILE). This runs
> on a fixture cut from it by tools/world/fixture.py - twelve nations
> renumbered 1..12 and the Rotterdam window's owner tiles - a resource beside
> the classes (world/world-1-fixture.bin), with what worldread.py read from it
> and from the full file beside it (world/world-1-fixture.expect). The full
> file's sections run when it is named, -Dcbs.world=<path> (WorldData.locate()),
> or lies at data/world-1.bin, and say so in one line when it is not.
> 
> What this has to prove:
>   1. THE FILE: the fixture opens; its layers are FORMAT.txt's, in order; its
>      SHA-256 and size are the expect file's; its meta's "of" names the full
>      file's identity when the full file is present.
>   2. THE READER IS worldread.py'S: every expect line of the scope (nation,
>      path, rate, price, tile, cell, freight, flow, make, lines, line) equals
>      what WorldData reads, exactly.
>   3. EVERY UN MEMBER AND THE THREE: the 193 member states and TWN, HKG and
>      MAC are the full file's nations, numbered in iso3 order; the fixture's
>      are its twelve.
>   4. THE WORLD'S SUMS CLOSE: for every good and nation, make is its row's
>      flows and use its column's; the world's make is its use; every flow is
>      above 0; every nation has its own pair.
>   5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE: cost(a, c) is never more than
>      cost(a, b) + cost(b, c), 0 on the diagonal, symmetric, every entry
>      finite; the closure of the edges is the stored table.
>   6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS: the kernel
>      solved from a = b = 1 gives every pair's flow, and each pair's cost is
>      the kernel's; on the fixture each BACI sum is the file's flow where the
>      pair is kept.
>   7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE: in every owner
>      tile with a blob, every cell the earth file's landmass layer calls land
>      has an owner.
>   8. THE MONEYS AND PATHS ARE WHOLE: every nation's money has a rate in
>      every month of FIRST_YEAR-01 (the stand-in century's) to 2025-12,
>      people are known from FIRST_YEAR to 2100, output and the exchange rate
>      to 2025, and a figure is NaN exactly where its mark is 0; each good's
>      price path ends at 1 in 2025.
>   9. THE STAND-IN CENTURY (EP1): the file begins in 2000 and the reader in
>      1900. Before a series' first figure a real series holds it and the two
>      nominal ones (cpi, gdp_usd) follow the US price level; a good's price
>      and a money's rate hold theirs; a nation's money at month 0 is its
>      2000-01 money; everything made is marked an estimate; the file's own
>      years read as they did.
> 
> Every fixture causes its condition: a fixture's twelve nations trade with one
> another in every good, its edges are a whole freight table, and its window's
> ... (1 more lines in the source)

**Uses:** [WorldData](WorldData.md) (177), [EarthData](EarthData.md) (11), [Freight](Freight.md) (3), [EarthCheck](EarthCheck.md) (3), [Money](Money.md) (3), [PriceLevel](PriceLevel.md) (2), [Nations](Nations.md) (1)

**Used by (11):** [DenominationCheck](DenominationCheck.md), [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md), [NationsCheck](NationsCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [WorldMarketCheck](WorldMarketCheck.md), [WorldMeasuresCheck](WorldMeasuresCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 307 | HELPERS |
| 377 | 1. THE FILE |
| 436 | 2. THE READER IS worldread.py's |
| 542 | 3. EVERY UN MEMBER AND THE THREE |
| 593 | 4. THE WORLD'S SUMS CLOSE |
| 641 | 5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE |
| 725 | 6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS |
| 928 | 7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE |
| 983 | 8. THE MONEYS AND PATHS ARE WHOLE |
| 1060 | 9. THE STAND-IN CENTURY (EP1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `WorldDataCheck.EXPECT` | `"world/world-1-fixture.expect"` | The fixture's expected figures, beside the classes (the fixture itself is WorldData.FIXTURE). |
| 86 | `WorldDataCheck.FIXTURE_NAME` | `"world-1-fixture", FULL_NAME = "world-1"` | A fixture's meta name row, and the full file's. |
| 89 | `WorldDataCheck.SUM_TOL` | `1e-9` | A sum, a make or a use, agrees with the flows it is the sum of within this share of it (FORMAT.txt, trade: 1e-9). |
| 92 | `WorldDataCheck.TRIANGLE_SLACK` | `1e-9` | The triangle rule holds within this share of the dearer side (floating point adds the same route in a different order). |
| 95 | `WorldDataCheck.TABLE_TOL` | `1e-9` | The freight table is symmetric, and is its edges' closure, within this share of the figure. |
| 98 | `WorldDataCheck.MARGIN_TOL` | `1e-12` | The solve's margins meet when no make or use is off by more than this share of it (FORMAT.txt, THE KERNEL: 1e-12). |
| 101 | `WorldDataCheck.FLOW_TOL` | `1e-9` | The solve gives the file's flow within this share of it (FORMAT.txt: 1e-9), plus MARGIN_ULPS of its margin over the flow (see there). |
| 108 | `WorldDataCheck.MARGIN_ULPS` | `1e-15` | The room a flow is given beyond FLOW_TOL, as this share of the smaller of its row's make and its column's use, over the flow: 4 ulps of the margin. |
| 111 | `WorldDataCheck.COST_TOL` | `1e-9` | A pair's cost is elasticity x ln(1 + freight / unit value) within this share of it. |
| 114 | `WorldDataCheck.BACI_TOL` | `1e-6` | BACI's own sum for a pair is the file's flow within this share of it (the file's flow is BACI's, as the CSV is summed in a second pass). |
| 117 | `WorldDataCheck.SWEEP_CAP` | `100_000` | The most passes (every nation's a and b solved together, the others held) the solve may take for one good before it is called not to converge. |
| 120 | `WorldDataCheck.CHANGE_TOL` | `1e-9` | The solve may stop when no a or b moved by more than this share in a pass, and every margin is within MARGIN_TOL / TARGET_DIVISOR (econ_trade.py's rule). |
| 123 | `WorldDataCheck.TARGET_DIVISOR` | `100` | A margin must be within MARGIN_TOL over this before the solve may stop: the margin floor of double precision is far below MARGIN_TOL. |
| 126 | `WorldDataCheck.CHANGE_FLOOR` | `1e-15` | The solve stops at once when no a or b moved by more than this share in a pass: they have stopped moving, at the floor of double precision. |
| 129 | `WorldDataCheck.STALE_PASSES` | `8` | ...or when this many passes after it may stop have moved them no less than the least so far: a nation whose own pair is nearly all of its make and use keeps a rounding wobble. |
| 132 | `WorldDataCheck.TWELVE` | `List.of("AUS", "BEL", "CAN", "CHE", "CHN", "COL", "DEU", "FRA", "GBR", "JPN",...` | The nations of the fixture, in iso3 order (FORMAT.txt, THE FIXTURE). |
| 135 | `WorldDataCheck.THE_THREE` | `List.of("TWN", "HKG", "MAC")` | The three the 193 UN members do not hold, that the full file's N = 196 adds: Taiwan, Hong Kong and Macau. |
| 138 | `WorldDataCheck.UN_MEMBER_COUNT` | `193` | How many member states the UN has (since South Sudan joined in 2011). |
| 141 | `WorldDataCheck.WORLD_BANK_REGIONS` | `7` | How many regions the World Bank's countries list sorts the nations in (FORMAT.txt, nations). |
| 144 | `WorldDataCheck.FULL_NATIONS` | `UN_MEMBER_COUNT + THE_THREE.size()` | How many nations the full file holds. |
| 151 | `WorldDataCheck.UN_MEMBERS` | `"AFG AGO ALB AND ARE ARG ARM ATG AUS AUT AZE " + "BDI BEL BEN BFA BGD BGR BHR...` | The United Nations' 193 member states by ISO 3166 alpha-3 code, A to Z: the list on the UN's member states page (https://www.un.org/en/about-us/member-states), written here and not read from the file, so that the file... |
| 179 | `WorldDataCheck.LAST_MONTH` | `WorldData.monthNumber(2025, 12)` | The last month the rates and the figures run to: 2025-12. |
| 182 | `WorldDataCheck.FILE_MONTH_ZERO` | `WorldData.monthNumber(WorldData.FILE_FIRST_YEAR, 1)` | The file's own month 0, 2000-01, as WorldData numbers months from FIRST_YEAR: where the stand-in century ends and the file begins. |
| 185 | `WorldDataCheck.PEOPLE_LAST` | `2100, FIGURES_LAST = 2025` | The people run to this year; the figures (output, the exchange rate) to FIGURES_LAST. |
| 188 | `WorldDataCheck.FIGURE_SERIES` | `List.of("gdp_2015usd", "gdp_usd", "gdp_ppp", "rate")` | The series that have a figure for every nation to FIGURES_LAST. |
| 191 | `WorldDataCheck.FREIGHT_DIALS` | `List.of("SEA_USD_TKM", "LAND_USD_TKM", "HANDLING_USD_T", "LAND_PER_SEA")` | The dials the freight cost and the kernel read (FORMAT.txt, trade and freight). |
| 194 | `WorldDataCheck.DAY` | `Pattern.compile("\\d{4}-\\d{2}-\\d{2}")` | A day as the meta writes it. |
| 198 | `WorldDataCheck.out` | `System.out` | Where the harness prints: the console. |
| 1065 | `WorldDataCheck.NOMINAL` | `List.of("cpi", "gdp_usd")` | The paths' series the stand-in carries back on the US price level; it holds every other. |
| 1068 | `WorldDataCheck.LEAD_TOL` | `1e-12` | The lead's figure is the rule's within this share of it (a product and a quotient of the same doubles: a rounding or two). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 196 | `static int fails` |  |
| 207 | `int n, bad` |  |
| 208 | `String first` |  |
| 228 | `final String name` |  |
| 229 | `final WorldData data` |  |
| 230 | `final Map<String, List<String[]>> lines` |  |
| 731 | `int sweeps` |  |
| 732 | `double margin` |  |
| 733 | `double[] x` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1305 | **type** `public class WorldDataCheck` | The nations' world file's reader (batch WN0; the project's spec-nations.md 3.3): WorldData's reading of world-1.bin held to tools/world/worldread.py figure for figure, and the file's own promises held to the file: its... |
| 200 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 206 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 210 | 7 | `void see(boolean ok, String what)` _(in WorldDataCheck.Tally)_ |  |
| 219 | 6 | `static void tally(String label, Tally t)` |  |
| 227 | 14 | **type** `static final class Scope` | One file read: the fixture, or the full file, with the expect file's lines of its scope by kind. |
| 232 | 4 | `Scope(String name, WorldData data)` _(in WorldDataCheck.Scope)_ |  |
| 237 | 1 | `String label(String s)` _(in WorldDataCheck.Scope)_ |  |
| 239 | 1 | `List<String[]> of(String kind)` _(in WorldDataCheck.Scope)_ |  |
| 243 | 3 | **type** `interface Judge` | Judges one expect line: null where WorldData reads what it says, else what differs. |
| 244 | 1 | `String judge(String[] f) throws Exception` _(in WorldDataCheck.Judge)_ |  |
| 247 | 50 | `public static void main(String[] args) throws Exception` |  |
| 299 | 7 | `static byte[] resource(String name) throws IOException` | A resource beside the classes, or failing that the source tree's copy from the working directory; null if neither. |

### HELPERS (lines 307-376)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 3 | `static boolean same(double a, double b)` | Whether two figures are the same: equal, or both NaN. |
| 317 | 5 | `static double rel(double a, double b)` | The share of the larger by which two figures differ; 0 where they are equal, infinite where either is not finite and they are not equal. |
| 324 | 3 | `static String differs(String what, String expected, String read)` | What differs between a text expected and read, null where nothing does. |
| 329 | 3 | `static String differs(String what, double expected, double read)` | What differs between a figure expected and read, null where they are the same double. |
| 334 | 4 | `static String firstOf(String...differences)` | The first of these that is not null. |
| 340 | 4 | `static int nation(WorldData d, String token)` | A nation as an expect line writes it: its iso3, or its number; 0 where neither is one. |
| 346 | 4 | `static int month(String token)` | A month as an expect line writes it: its number in the FILE's count (FORMAT.txt: month 0 is 2000-01), or yyyy-mm; as WorldData numbers it, from FIRST_YEAR (EP1). |
| 352 | 3 | `static String sci(double x)` | A tolerance as a short label: 1e-09. |
| 357 | 13 | `static void each(Scope s, String label, String kind, Judge judge)` | One tally over an expect kind's lines of the scope, each judged; at least one line has to be there. |
| 372 | 4 | `static String[] one(Scope s, String kind)` | The expect file's one line of a kind in the scope, null if it has none or more than one. |

### 1. THE FILE (lines 377-435)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 54 | `static void theFile(List<Scope> scopes, Scope fixture, Scope full)` |  |

### 2. THE READER IS worldread.py's (lines 436-541)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 85 | `static void theReader(List<Scope> scopes)` |  |
| 527 | 4 | `static WorldData reopen(Scope s, int cacheTiles) throws IOException` | A second reader of a scope's file, with an LRU of the given tiles: the fixture's resource again, or the full file by its path. |
| 533 | 8 | `static String vertex(String what, String token, int stored)` | What differs between a vertex as the expect line writes it - in the file's integer units, or as degrees with a point - and the one read. |

### 3. EVERY UN MEMBER AND THE THREE (lines 542-592)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 46 | `static void theNations(List<Scope> scopes)` |  |

### 4. THE WORLD'S SUMS CLOSE (lines 593-640)

| line | len | member | says |
|---:|---:|---|---|
| 597 | 43 | `static void theSums(List<Scope> scopes)` |  |

### 5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE (lines 641-724)

| line | len | member | says |
|---:|---:|---|---|
| 645 | 68 | `static void theFreight(List<Scope> scopes)` |  |
| 715 | 9 | `static boolean edgesOk(Scope s, WorldData.Freight f)` | Whether a scope's freight edges are well formed. |

### 6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS (lines 725-927)

| line | len | member | says |
|---:|---:|---|---|
| 730 | 5 | **type** `static final class Solved` | One good's kernel solved from a = b = 1: the passes it took, the worst margin, and the flows. |
| 743 | 69 | `static Solved solve(WorldData.Flows f, int n)` | FORMAT.txt's THE KERNEL: x_ij = a_i b_j exp(pref_ij - cost_ij) with the rows summing to make and the columns to use, solved from a = b = 1. |
| 814 | 12 | `static double residual(WorldData.Flows f, int n, boolean[] present, double[] kd, double[] kernel, int[] rowCount, int[] rowAt, ...` | The largest share by which a nation's row or column, at the current a and b, is off its make or use. |
| 831 | 18 | `static double[] nodeSolve(double kd, double rowSum, double colSum, double make, double use, double aOld)` | The pair (a_i, b_i) that meets nation i's row and column exactly, the others held: a (kd b + R) = make and b (kd a + C) = use, R and C the off-diagonal sums of K b and K a, kd the own pair's K (0 where it has none); t... |
| 851 | 5 | `static void add(double[] total, double[] keep, int i, double x)` | Adds x to total[i] keeping the rounding it loses in keep[i] (Neumaier's sum), so a row of 196 flows adds up to the last ulp of the larger. |
| 858 | 4 | `static double margin(double got, double want)` | How far a margin is from the make or use it is meant to be, as a share of that. |
| 863 | 64 | `static void theKernel(List<Scope> scopes)` |  |

### 7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE (lines 928-982)

| line | len | member | says |
|---:|---:|---|---|
| 932 | 50 | `static void theCells(List<Scope> scopes) throws IOException` |  |

### 8. THE MONEYS AND PATHS ARE WHOLE (lines 983-1059)

| line | len | member | says |
|---:|---:|---|---|
| 987 | 72 | `static void theMoneysAndPaths(List<Scope> scopes)` |  |

### 9. THE STAND-IN CENTURY (EP1) (lines 1060-1384)

| line | len | member | says |
|---:|---:|---|---|
| 1071 | 4 | `static int firstFigure(WorldData d, String series, int nation)` | The first year from the file's first that the series has a figure for the nation; -1 where it has none. |
| 1077 | 3 | `static double carried(double v0, int y0, int y)` | What the rule makes of a nominal series' lead year y, given the figure v0 of its first year y0: that figure on the US price level. |
| 1082 | 3 | `static boolean madeMark(double v, int mark)` | The mark a made value has: an estimate, or none where it is NaN. |
| 1086 | 101 | `static void theStandInCentury(List<Scope> scopes, byte[] fixtureBytes) throws Exception` |  |
| 1193 | 100 | `static void theStandInOnAHole(Scope fixture, byte[] fixtureBytes) throws Exception` | The conditions the fixture does not have, caused: the fixture's bytes with cells made NaN (a row with no figure at all, a row whose first figure is later than the file's first year, for a real and for a nominal series... |
| 1295 | 5 | `static String utf(java.nio.ByteBuffer b)` | A short-prefixed UTF-8 string, as the layers write names. |
| 1302 | 14 | `static void noPaths(byte[] raw, String series, int nation, int from, int to)` | A paths layer's inflated bytes with a series' figures for a nation (1-based) made NaN, mark 0, in the years from to to. |
| 1318 | 15 | `static void noPrices(byte[] raw, int good, int from, int to)` | A prices layer's inflated bytes with a good's prices (1-based) made NaN, mark 0, in the years from to to. |
| 1335 | 14 | `static void noRates(byte[] raw, int money, int from, int to)` | A moneys layer's inflated bytes with a money's rates (1-based) made NaN, mark 0, in the file's months from to to (the file's count, month 0 being 2000-01). |
| 1354 | 30 | `static byte[] withLayer(byte[] file, String layer, java.util.function.UnaryOperator<byte[]> change) throws Exception` | A file's bytes with one whole layer's inflated bytes changed: the new blob is appended to the file and the layer's header entry points at it (the old blob is left where it was, unread). |


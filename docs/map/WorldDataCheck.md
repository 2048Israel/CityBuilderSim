# WorldDataCheck.java - 1,048 lines · 35 methods · 27 constants · harnesses

`ham/citybuildersim/WorldDataCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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
>      every month of 2000-01 to 2025-12, people are known to 2100, output and
>      the exchange rate to 2025, and a figure is NaN exactly where its mark
>      is 0; each good's price path ends at 1 in 2025.
> 
> Every fixture causes its condition: a fixture's twelve nations trade with one
> another in every good, its edges are a whole freight table, and its window's
> tiles are the coast of the Netherlands and Belgium.

**Uses:** [WorldData](WorldData.md) (106), [EarthData](EarthData.md) (10), [Freight](Freight.md) (3), [EarthCheck](EarthCheck.md) (3), [Money](Money.md) (1)

**Used by (3):** [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md), [NationsCheck](NationsCheck.md)

## Sections

| line | section |
|---:|---|
| 296 | HELPERS |
| 366 | 1. THE FILE |
| 425 | 2. THE READER IS worldread.py's |
| 531 | 3. EVERY UN MEMBER AND THE THREE |
| 582 | 4. THE WORLD'S SUMS CLOSE |
| 630 | 5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE |
| 714 | 6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS |
| 917 | 7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE |
| 972 | 8. THE MONEYS AND PATHS ARE WHOLE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `WorldDataCheck.EXPECT` | `"world/world-1-fixture.expect"` | The fixture's expected figures, beside the classes (the fixture itself is WorldData.FIXTURE). |
| 79 | `WorldDataCheck.FIXTURE_NAME` | `"world-1-fixture", FULL_NAME = "world-1"` | A fixture's meta name row, and the full file's. |
| 82 | `WorldDataCheck.SUM_TOL` | `1e-9` | A sum, a make or a use, agrees with the flows it is the sum of within this share of it (FORMAT.txt, trade: 1e-9). |
| 85 | `WorldDataCheck.TRIANGLE_SLACK` | `1e-9` | The triangle rule holds within this share of the dearer side (floating point adds the same route in a different order). |
| 88 | `WorldDataCheck.TABLE_TOL` | `1e-9` | The freight table is symmetric, and is its edges' closure, within this share of the figure. |
| 91 | `WorldDataCheck.MARGIN_TOL` | `1e-12` | The solve's margins meet when no make or use is off by more than this share of it (FORMAT.txt, THE KERNEL: 1e-12). |
| 94 | `WorldDataCheck.FLOW_TOL` | `1e-9` | The solve gives the file's flow within this share of it (FORMAT.txt: 1e-9), plus MARGIN_ULPS of its margin over the flow (see there). |
| 101 | `WorldDataCheck.MARGIN_ULPS` | `1e-15` | The room a flow is given beyond FLOW_TOL, as this share of the smaller of its row's make and its column's use, over the flow: 4 ulps of the margin. |
| 104 | `WorldDataCheck.COST_TOL` | `1e-9` | A pair's cost is elasticity x ln(1 + freight / unit value) within this share of it. |
| 107 | `WorldDataCheck.BACI_TOL` | `1e-6` | BACI's own sum for a pair is the file's flow within this share of it (the file's flow is BACI's, as the CSV is summed in a second pass). |
| 110 | `WorldDataCheck.SWEEP_CAP` | `100_000` | The most passes (every nation's a and b solved together, the others held) the solve may take for one good before it is called not to converge. |
| 113 | `WorldDataCheck.CHANGE_TOL` | `1e-9` | The solve may stop when no a or b moved by more than this share in a pass, and every margin is within MARGIN_TOL / TARGET_DIVISOR (econ_trade.py's rule). |
| 116 | `WorldDataCheck.TARGET_DIVISOR` | `100` | A margin must be within MARGIN_TOL over this before the solve may stop: the margin floor of double precision is far below MARGIN_TOL. |
| 119 | `WorldDataCheck.CHANGE_FLOOR` | `1e-15` | The solve stops at once when no a or b moved by more than this share in a pass: they have stopped moving, at the floor of double precision. |
| 122 | `WorldDataCheck.STALE_PASSES` | `8` | ...or when this many passes after it may stop have moved them no less than the least so far: a nation whose own pair is nearly all of its make and use keeps a rounding wobble. |
| 125 | `WorldDataCheck.TWELVE` | `List.of("AUS", "BEL", "CAN", "CHE", "CHN", "COL", "DEU", "FRA", "GBR", "JPN",...` | The nations of the fixture, in iso3 order (FORMAT.txt, THE FIXTURE). |
| 128 | `WorldDataCheck.THE_THREE` | `List.of("TWN", "HKG", "MAC")` | The three the 193 UN members do not hold, that the full file's N = 196 adds: Taiwan, Hong Kong and Macau. |
| 131 | `WorldDataCheck.UN_MEMBER_COUNT` | `193` | How many member states the UN has (since South Sudan joined in 2011). |
| 134 | `WorldDataCheck.WORLD_BANK_REGIONS` | `7` | How many regions the World Bank's countries list sorts the nations in (FORMAT.txt, nations). |
| 137 | `WorldDataCheck.FULL_NATIONS` | `UN_MEMBER_COUNT + THE_THREE.size()` | How many nations the full file holds. |
| 144 | `WorldDataCheck.UN_MEMBERS` | `"AFG AGO ALB AND ARE ARG ARM ATG AUS AUT AZE " + "BDI BEL BEN BFA BGD BGR BHR...` | The United Nations' 193 member states by ISO 3166 alpha-3 code, A to Z: the list on the UN's member states page (https://www.un.org/en/about-us/member-states), written here and not read from the file, so that the file... |
| 172 | `WorldDataCheck.LAST_MONTH` | `WorldData.monthNumber(2025, 12)` | The last month the rates and the figures run to: 2025-12. |
| 175 | `WorldDataCheck.PEOPLE_LAST` | `2100, FIGURES_LAST = 2025` | The people run to this year; the figures (output, the exchange rate) to FIGURES_LAST. |
| 178 | `WorldDataCheck.FIGURE_SERIES` | `List.of("gdp_2015usd", "gdp_usd", "gdp_ppp", "rate")` | The series that have a figure for every nation to FIGURES_LAST. |
| 181 | `WorldDataCheck.FREIGHT_DIALS` | `List.of("SEA_USD_TKM", "LAND_USD_TKM", "HANDLING_USD_T", "LAND_PER_SEA")` | The dials the freight cost and the kernel read (FORMAT.txt, trade and freight). |
| 184 | `WorldDataCheck.DAY` | `Pattern.compile("\\d{4}-\\d{2}-\\d{2}")` | A day as the meta writes it. |
| 188 | `WorldDataCheck.out` | `System.out` | Where the harness prints: the console. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 186 | `static int fails` |  |
| 197 | `int n, bad` |  |
| 198 | `String first` |  |
| 218 | `final String name` |  |
| 219 | `final WorldData data` |  |
| 220 | `final Map<String, List<String[]>> lines` |  |
| 720 | `int sweeps` |  |
| 721 | `double margin` |  |
| 722 | `double[] x` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 976 | **type** `public class WorldDataCheck` | The nations' world file's reader (batch WN0; the project's spec-nations.md 3.3): WorldData's reading of world-1.bin held to tools/world/worldread.py figure for figure, and the file's own promises held to the file: its... |
| 190 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 196 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 200 | 7 | `void see(boolean ok, String what)` _(in WorldDataCheck.Tally)_ |  |
| 209 | 6 | `static void tally(String label, Tally t)` |  |
| 217 | 14 | **type** `static final class Scope` | One file read: the fixture, or the full file, with the expect file's lines of its scope by kind. |
| 222 | 4 | `Scope(String name, WorldData data)` _(in WorldDataCheck.Scope)_ |  |
| 227 | 1 | `String label(String s)` _(in WorldDataCheck.Scope)_ |  |
| 229 | 1 | `List<String[]> of(String kind)` _(in WorldDataCheck.Scope)_ |  |
| 233 | 3 | **type** `interface Judge` | Judges one expect line: null where WorldData reads what it says, else what differs. |
| 234 | 1 | `String judge(String[] f) throws Exception` _(in WorldDataCheck.Judge)_ |  |
| 237 | 49 | `public static void main(String[] args) throws Exception` |  |
| 288 | 7 | `static byte[] resource(String name) throws IOException` | A resource beside the classes, or failing that the source tree's copy from the working directory; null if neither. |

### HELPERS (lines 296-365)

| line | len | member | says |
|---:|---:|---|---|
| 301 | 3 | `static boolean same(double a, double b)` | Whether two figures are the same: equal, or both NaN. |
| 306 | 5 | `static double rel(double a, double b)` | The share of the larger by which two figures differ; 0 where they are equal, infinite where either is not finite and they are not equal. |
| 313 | 3 | `static String differs(String what, String expected, String read)` | What differs between a text expected and read, null where nothing does. |
| 318 | 3 | `static String differs(String what, double expected, double read)` | What differs between a figure expected and read, null where they are the same double. |
| 323 | 4 | `static String firstOf(String...differences)` | The first of these that is not null. |
| 329 | 4 | `static int nation(WorldData d, String token)` | A nation as an expect line writes it: its iso3, or its number; 0 where neither is one. |
| 335 | 4 | `static int month(String token)` | A month as an expect line writes it: its number, or yyyy-mm. |
| 341 | 3 | `static String sci(double x)` | A tolerance as a short label: 1e-09. |
| 346 | 13 | `static void each(Scope s, String label, String kind, Judge judge)` | One tally over an expect kind's lines of the scope, each judged; at least one line has to be there. |
| 361 | 4 | `static String[] one(Scope s, String kind)` | The expect file's one line of a kind in the scope, null if it has none or more than one. |

### 1. THE FILE (lines 366-424)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 54 | `static void theFile(List<Scope> scopes, Scope fixture, Scope full)` |  |

### 2. THE READER IS worldread.py's (lines 425-530)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 85 | `static void theReader(List<Scope> scopes)` |  |
| 516 | 4 | `static WorldData reopen(Scope s, int cacheTiles) throws IOException` | A second reader of a scope's file, with an LRU of the given tiles: the fixture's resource again, or the full file by its path. |
| 522 | 8 | `static String vertex(String what, String token, int stored)` | What differs between a vertex as the expect line writes it - in the file's integer units, or as degrees with a point - and the one read. |

### 3. EVERY UN MEMBER AND THE THREE (lines 531-581)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 46 | `static void theNations(List<Scope> scopes)` |  |

### 4. THE WORLD'S SUMS CLOSE (lines 582-629)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 43 | `static void theSums(List<Scope> scopes)` |  |

### 5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE (lines 630-713)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 68 | `static void theFreight(List<Scope> scopes)` |  |
| 704 | 9 | `static boolean edgesOk(Scope s, WorldData.Freight f)` | Whether a scope's freight edges are well formed. |

### 6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS (lines 714-916)

| line | len | member | says |
|---:|---:|---|---|
| 719 | 5 | **type** `static final class Solved` | One good's kernel solved from a = b = 1: the passes it took, the worst margin, and the flows. |
| 732 | 69 | `static Solved solve(WorldData.Flows f, int n)` | FORMAT.txt's THE KERNEL: x_ij = a_i b_j exp(pref_ij - cost_ij) with the rows summing to make and the columns to use, solved from a = b = 1. |
| 803 | 12 | `static double residual(WorldData.Flows f, int n, boolean[] present, double[] kd, double[] kernel, int[] rowCount, int[] rowAt, ...` | The largest share by which a nation's row or column, at the current a and b, is off its make or use. |
| 820 | 18 | `static double[] nodeSolve(double kd, double rowSum, double colSum, double make, double use, double aOld)` | The pair (a_i, b_i) that meets nation i's row and column exactly, the others held: a (kd b + R) = make and b (kd a + C) = use, R and C the off-diagonal sums of K b and K a, kd the own pair's K (0 where it has none); t... |
| 840 | 5 | `static void add(double[] total, double[] keep, int i, double x)` | Adds x to total[i] keeping the rounding it loses in keep[i] (Neumaier's sum), so a row of 196 flows adds up to the last ulp of the larger. |
| 847 | 4 | `static double margin(double got, double want)` | How far a margin is from the make or use it is meant to be, as a share of that. |
| 852 | 64 | `static void theKernel(List<Scope> scopes)` |  |

### 7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE (lines 917-971)

| line | len | member | says |
|---:|---:|---|---|
| 921 | 50 | `static void theCells(List<Scope> scopes) throws IOException` |  |

### 8. THE MONEYS AND PATHS ARE WHOLE (lines 972-1048)

| line | len | member | says |
|---:|---:|---|---|
| 976 | 72 | `static void theMoneysAndPaths(List<Scope> scopes)` |  |


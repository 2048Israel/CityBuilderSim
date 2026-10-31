# NationsMap.java - 1,019 lines · 64 methods · 36 constants · model

`ham/citybuildersim/NationsMap.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The nations on the map, without the toolkit: the borders simplified and cut to a view, each nation's dot and the name written on its land, what lies
> under a point, and the card a click opens - everything ui/MapView draws the nations by, as arithmetic a harness can hold (GlobeCheck 9 to 12 and
> MapCheck 19 hold it), as LandMap and GlobeFrame hold the land office's and the globe's.
> 
> WHY THIS EXISTS (batch WN2; docs/studies/spec-nations.md 6.1 and 8, board 1). The map had no nations and no borders; WN0 baked Natural Earth's lines
> and a nation for every 15" cell into world-1.bin and WN1 gave each nation a year, and Jerus asked for "a dot at each nation's node, its area by
> output", "a toggle on the map ui", "a map button to ... toggle borders", "each nation stats on their land ... canada would show gdp and ...
> population" and "clicking would show more stats based on the leaderboard". The map must not get slower (MP1 made it fast), so nothing here walks the
> file's vertices or its 15" cells in a frame: the lines are kept once at five levels of detail (LOD_KM), each with the cap that holds it, so a frame
> culls to the lines whose cap meets the view and projects only the vertices a pixel could tell apart; the labels are tried on a coarse grid of
> owners, a byte read where ownerAt() on 15" cells would inflate tiles. Nothing in the month reads this; it is the screen's.
> 
> THE LEVELS OF DETAIL. A line's vertex has a significance, the km it stands off the chord of the Douglas-Peucker range it was chosen to split,
> made no greater than its parent's, so a level keeps the vertices whose significance exceeds its tolerance and each level's vertices are a subset of
> the one before (never more), every vertex a level drops within its tolerance of the segment that spans it. The distance is measured in three
> dimensions, from the vertex to the chord between the two kept vertices on the sphere of RADIUS_M, not on a flat approximation: the screen draws the
> straight chord between two projected vertices, and an orthographic projection is a plain projection of the three-dimensional point, so the
> chord's three-dimensional miss bounds what the eye sees; a 2,000 km stretch of the 49th parallel, straight in latitude and longitude, bows 120 km
> off its chord on the globe, and a flat measure would have dropped every vertex along it. A frame takes the coarsest level whose tolerance is no more
> than LOD_PX of a pixel at the view's km a pixel.
> 
> THE COARSE GRID is the owner layer at COARSE_PER_DEGREE cells a degree: each cell takes the owner of the 15" cell at its centre (the fine cell
> CENTRE of the cell's FINE_PER_COARSE along each side), and a tile with one owner fills its cells with it. It is deterministic and it is a sample, not
> a vote; the land's edge is where it is least exact, and the sea of a coastal tile that is one nation's belongs to that nation, as in the owner
> layer. A label is tried on it; a click reads the exact ownerAt().
> 
> ONE THREAD. Built once per world file (of(), on the map's worker thread), it is immutable but for three caches - the globe's borders for the last
> view, the lines crossing the last box of the city's plane, and the last year's figures and ranks - which are used from the screen's thread alone.
> It keeps the WorldData it was built from for the exact ownerAt(), so the caller keeps that open.

**Uses:** [WorldData](WorldData.md) (13), [Nations](Nations.md) (13), [EarthProj](EarthProj.md) (9), [GlobeFrame](GlobeFrame.md) (8), [World](World.md) (2), [TradeLanes](TradeLanes.md) (2), [MapFrame](MapFrame.md) (1)

**Used by (4):** [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [TradeLanes](TradeLanes.md)

## Sections

| line | section |
|---:|---|
| 45 | THE DIALS |
| 148 | WHAT THE MAP KEEPS |
| 421 | THE BORDERS - cut to the view |
| 503 | THE BORDERS ON THE CITY'S PLANE - only where one crosses the city's ground |
| 683 | THE NATIONS' DOTS AND LABELS - on the globe |
| 801 | THE HOVER AND THE CARD |
| 901 | THE WORDS |
| 959 | A YEAR'S FIGURES AND RANKS - computed once for a Nations and a year |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `NationsMap.LOD_KM` | `{ 0, 1, 4, 16, 64 }` | The levels of detail of a line, as the tolerance in km each keeps its vertices within of the file's own: 0 is the file's, then 1, 4, 16 and 64 km. |
| 53 | `NationsMap.LOD_PX` | `0.5` | A frame takes the coarsest level whose tolerance is no more than this many pixels at the view's km a pixel: half a pixel, under which nobody sees the difference. |
| 56 | `NationsMap.COARSE_PER_DEGREE` | `8` | The coarse owner grid's cells a degree: 8 (2,880 x 1,440 bytes), a cell 30 of the owner layer's 15" cells across, 14 km at the equator. |
| 59 | `NationsMap.DRAW_MARGIN_PX` | `8` | A stretch of a line more than this many pixels off the canvas is dropped, and the run is cut there: 8, so a dash pattern does not start on the canvas's own edge. |
| 62 | `NationsMap.CAP_SLACK_RAD` | `0.005` | The slack added to a view's cap and a line's, radians: 0.005 (32 km), so a line the chord of its own vertices only grazes is not culled. |
| 65 | `NationsMap.DOT_BASE_PX` | `1.6` | A dot's radius is this many pixels... |
| 68 | `NationsMap.DOT_PER_ROOT_PX` | `1.9` | ...plus this many for each root of a trillion US$ of output (1.9)... |
| 71 | `NationsMap.DOT_MIN_PX` | `2.5` | ...and never less than this (2.5), so the smallest nation is still a dot to point at... |
| 74 | `NationsMap.DOT_MAX_PX` | `11` | ...and never more than this (11), so the largest does not cover its neighbours. |
| 77 | `NationsMap.LABEL_LINE_PX` | `12` | A label's second line, the figures, stands this many pixels under the first, the name (the board's 12 px for its 11 px name and 9.5 px figures). |
| 80 | `NationsMap.LABEL_H_PX` | `26` | A label's box is the wider line's width by this many pixels: 26, the two lines and the room round them. |
| 83 | `NationsMap.LABEL_ASCENT_PX` | `11` | A label's box starts this many pixels above the name's baseline (the 11 px name's height), so the box is the name, the figures and what hangs below. |
| 86 | `NationsMap.LABEL_AREA_TIMES` | `2` | A nation is written on only if its coarse area, in the view's pixels, is at least this many times the label's box: 2. |
| 89 | `NationsMap.LABEL_SAMPLES_X` | `5` | A label's box is tried at a grid of this many points across... |
| 92 | `NationsMap.LABEL_SAMPLES_Y` | `3` | ...by this many down: 3, fifteen points. |
| 95 | `NationsMap.LABEL_OWN_SHARE` | `0.6` | At least this share of the box's points must lie on the nation's own coarse cells: 0.6 (nine of fifteen). |
| 98 | `NationsMap.HIT_SLOP_PX` | `3` | A click or a hover lands on a dot within its radius and this many pixels more: 3. |
| 101 | `NationsMap.CLICK_FOR_CARD` | `"Click for its card"` | The last line of a nation's hover, which says what a click does (the board's). |
| 104 | `NationsMap.BORDER_WORDS` | `"A border"` | The legend's words for a border line (the board's). |
| 107 | `NationsMap.NATION_WORDS` | `"A nation, by its economy"` | The legend's words for a nation's dot (the board's wording, the dot's area by output). |
| 110 | `NationsMap.BORDER_ARGB` | `0xffffffff` | A border line's colour, ARGB: white, drawn at BORDER_ALPHA and dashed. |
| 113 | `NationsMap.BORDER_ALPHA` | `0.7` | A border line's opacity: 0.7. |
| 116 | `NationsMap.BORDER_DASH_PX` | `{ 4, 3 }` | A border line's dash, in pixels drawn and pixels left: 4 and 3 (the board's). |
| 119 | `NationsMap.DOT_ARGB` | `0xffe6edf3` | A nation's dot, ARGB: the board's pale blue-white (e6edf3)... |
| 122 | `NationsMap.DOT_ALPHA` | `0.85` | ...at this opacity: 0.85. |
| 125 | `NationsMap.DOT_EDGE_ARGB` | `0xff0b1118` | ...with this edge, ARGB: the map's dark (0b1118), so a dot reads on ice and on sea alike. |
| 128 | `NationsMap.BORDERS_MS` | `3` | A globe view's borders - the cull, the level, the projection and the cut - take no more than this, ms of the main thread's CPU (the wall clock where the JVM's CPU clock is coarse): 3 - measured 0.96 ms on the PC's slo... |
| 131 | `NationsMap.MARKS_MS` | `0.4` | A view's dots and labels - the projection, the tests and the placing - take no more than this, ms of the main thread's CPU (or the wall clock): 0.4 - measured 0.104 ms on the PC's slower view (the North Atlantic at 10... |
| 134 | `NationsMap.BUILD_MS` | `600` | Building the map from the world file - the levels, the caps and the coarse grid, which inflates every blob tile once - takes no more than this, ms of the wall clock, on the worker thread: 600 - measured 147 to 189 ms ... |
| 137 | `NationsMap.FINE_PER_COARSE` | `WorldData.OWNER_CELLS / COARSE_PER_DEGREE` | The 15" cells along a coarse cell's side. |
| 140 | `NationsMap.CENTRE` | `FINE_PER_COARSE / 2` | The 15" cell, along a coarse cell's side, whose owner the coarse cell takes: the middle one (15 of 0 to 29). |
| 143 | `NationsMap.R_KM` | `EarthProj.RADIUS_M / 1000` | The sphere's radius, km. |
| 145 | `NationsMap.THREE` | `new MathContext(3, RoundingMode.HALF_UP)` |  |
| 146 | `NationsMap.THOUSAND` | `BigDecimal.valueOf(1000), HUNDRED = BigDecimal.valueOf(100)` |  |
| 824 | `NationsMap.GDP_WORDS` | `"GDP, US$ of 2015"` | The GDP row's label: the file's output is real, US$ of 2015. |
| 836 | `NationsMap.BUYS_WORDS` | `"Buys from us this year", SELLS_WORDS = "Sells us this year", HAUL_WORDS = "T...` | The words of the card's trade rows: what the nation bought of the city this year, what it sold the city, and the haul in tonnes (WN3, board 1). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 152 | `private final WorldData file` |  |
| 153 | `private final int n` | the nations, 1..n |
| 154 | `private final String[] iso3, names, region` | the nations, 1..n |
| 155 | `private final double[] nodeLat, nodeLon, labelLat, labelLon` | by number: a nation's iso3 and region, and a nation's or an area's name |
| 156 | `private final int lineCount` |  |
| 157 | `private final double[][] lat, lon` | [level][vertex]: the lines' vertices in degrees, a line's run from start[level][line] |
| 158 | `private final int[][] start` | [level][vertex]: the lines' vertices in degrees, a line's run from start[level][line] |
| 159 | `private final double[] capX, capY, capZ, capR, capCos, capSin` | [level][line + 1] |
| 160 | `private final byte[] coarse` | a line's cap: unit centre, radius in radians, and the radius's cosine and sine |
| 161 | `private final double[] areaKm2` | the coarse owner grid, rows from the north |
| 163 | `private final Cut cut` | by owner: the coarse area, km2 |
| 164 | `private GlobeFrame cachedView` |  |
| 165 | `private Runs cachedRuns` |  |
| 166 | `private double[] crossKey` |  |
| 167 | `private int[] crossLines` |  |
| 168 | `private Table table` |  |
| 427 | `public final double[] xs, ys` |  |
| 428 | `public final int[] starts` |  |
| 430 | `public final int lines, considered, level` | The lines in the file, the lines whose cap met the view, and the level of detail drawn. |
| 616 | `private double[] xs` |  |
| 617 | `private int[] starts` |  |
| 618 | `private int points, runs` |  |
| 619 | `private double xmin, ymin, xmax, ymax` |  |
| 620 | `private boolean open` |  |
| 965 | `final Nations nations` |  |
| 966 | `final int year` |  |
| 967 | `final double[] output, people, head` |  |
| 968 | `final int[] rankOutput, rankPeople, rankHead` |  |
| 969 | `final int[] order` | the nations' numbers, largest output first, ties by number |
| 970 | `final String[] names, figures` | the nations' numbers, largest output first, ties by number |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 977 | **type** `public final class NationsMap` | The nations on the map, without the toolkit: the borders simplified and cut to a view, each nation's dot and the name written on its land, what lies under a point, and the card a click opens - everything ui/MapView dr... |

### THE DIALS (lines 45-147)

### WHAT THE MAP KEEPS (lines 148-420)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 3 | `public static NationsMap of(WorldData file)` | The map of a world file: the lines at their levels, the caps, the coarse owner grid and the areas. |
| 175 | 86 | `private NationsMap(WorldData file)` |  |
| 266 | 38 | `private static void significance(double[] x, double[] y, double[] z, int len, double[] sig, int off, int[] stackA, int[] stackB...` | Each vertex's significance into sig[off ..]: the endpoints infinite; the others the km the vertex stands, in three dimensions, off the chord of the range it is the farthest of, no more than its parent's. |
| 306 | 24 | `private void cap(int line, double[] x, double[] y, double[] z, int len)` | A line's cap: the unit vector of its vertices' mean (its first vertex's if they cancel), and the largest angle from it to a vertex. |
| 332 | 23 | `private void fillCoarse()` | The coarse grid from the owner layer a tile at a time, and each owner's area from it. |
| 357 | 1 | `public int count()` | How many nations the map holds (the file's N). |
| 360 | 1 | `public int lineCount()` | How many border lines the file holds. |
| 363 | 1 | `public String iso3(int nation)` | A nation's ISO code, 1..count(). |
| 366 | 4 | `public int numberOf(String code)` | The number of the nation with that ISO code; 0 if there is none. |
| 372 | 3 | `public String ownerName(int owner)` | The nation's or the area's name for an owner number; null for 0 (no one) and for a number the file does not have. |
| 377 | 1 | `public double nodeLat(int nation)` | A nation's largest place, where its dot stands. |
| 378 | 1 | `public double nodeLon(int nation)` |  |
| 381 | 1 | `public double labelLat(int nation)` | Natural Earth's label point of a nation, where its name is written. |
| 382 | 1 | `public double labelLon(int nation)` |  |
| 385 | 1 | `public double areaKm2(int owner)` | An owner's area on the coarse grid, km2. |
| 388 | 8 | `int coarseOwner(double atLat, double atLon)` | The owner of the coarse cell holding a point, 0 for no one: a byte read (the cell holds its north and west edges, as the owner layer's does). |
| 398 | 3 | `public int ownerAt(double atLat, double atLon)` | The exact owner of a point (the owner layer's 15" cell): a nation's or an area's number, 0 for no one. |
| 403 | 5 | `static int levelFor(double kmPx)` | The level a view at this km a pixel takes: the coarsest whose tolerance is no more than LOD_PX of a pixel. |
| 410 | 1 | `int vertices(int line, int level)` | How many vertices a line keeps at a level. |
| 413 | 1 | `double lat(int line, int level, int k)` | A line's vertex k at a level, degrees. |
| 414 | 1 | `double lon(int line, int level, int k)` |  |
| 417 | 3 | `public static boolean drawsOn(World.Key key)` | Whether borders and nations are drawn on a world: only the real Earth's (a generated world has no nations; its partners wait for WN3). |

### THE BORDERS - cut to the view (lines 421-502)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 19 | **type** `public static final class Runs` | Runs of vertices in pixels: run k is xs, ys from starts[k] to starts[k + 1]; the counts a harness reads beside them. |
| 432 | 8 | `Runs(double[] xs, double[] ys, int[] starts, int lines, int considered, int level)` _(in NationsMap.Runs)_ |  |
| 441 | 1 | `public int runs()` _(in NationsMap.Runs)_ |  |
| 443 | 1 | `public int vertices()` _(in NationsMap.Runs)_ |  |
| 452 | 8 | `public Runs borders(GlobeFrame v)` | The borders in a globe view (the PICTURE and GROUND tiers; in the PLANE the screen draws planeBorders(), and this gives nothing): the lines whose cap meets the view's, at the level the view's km a pixel takes, each ve... |
| 462 | 34 | `Runs bordersAt(GlobeFrame v, int level)` | The same at a level of the caller's choice, and not kept (a harness prints what the file's own would draw). |
| 498 | 4 | `static double viewRadius(GlobeFrame v)` | The angle from a view's centre to the farthest point it shows, radians: its corner's if all four are on the planet, a quarter turn once one is off it. |

### THE BORDERS ON THE CITY'S PLANE - only where one crosses the city's ground (lines 503-682)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 52 | `public int[] crossing(EarthProj plane, double x0, double y0, double x1, double y1)` | The lines, by index, with a segment meeting a box of the plane's plots (the caller passes the box of the city's land and its offers): the lines whose cap meets the box's, tested at the file's own level in plot coordin... |
| 566 | 15 | `static boolean segmentMeets(double ax, double ay, double bx, double by, double x0, double y0, double x1, double y1)` | Whether the segment (ax, ay)-(bx, by) meets the box (Liang-Barsky, boundary included). |
| 586 | 24 | `public Runs planeBorders(EarthProj plane, MapFrame f, int[] lines)` | Those lines on the plane's screen: each vertex through plotOf() and the frame's screenX() and screenY(), at the level a pixel of PLOT_M / 1000 / scale() km takes, cut to the canvas as the globe's are. |
| 615 | 67 | **type** `static final class Cut` | Collects runs of pixels, each segment clipped to the canvas widened by DRAW_MARGIN_PX (Liang-Barsky): a segment wholly outside is dropped and the run it was in is cut there; a segment that leaves ends at the margin, o... |
| 622 | 9 | `void reset(double w, double h)` _(in NationsMap.Cut)_ |  |
| 633 | 1 | `void gap()` _(in NationsMap.Cut)_ | The run in progress ends (a vertex was on the far side, or the line did). |
| 636 | 1 | `int runCount()` _(in NationsMap.Cut)_ | The runs begun so far (TradeLanes reads where each of its stroke widths' runs begin). |
| 638 | 28 | `void segment(double ax, double ay, double bx, double by)` _(in NationsMap.Cut)_ |  |
| 667 | 8 | `private void add(double x, double y)` _(in NationsMap.Cut)_ |  |
| 676 | 5 | `Runs finish(int lines, int considered, int level)` _(in NationsMap.Cut)_ |  |

### THE NATIONS' DOTS AND LABELS - on the globe (lines 683-800)

| line | len | member | says |
|---:|---:|---|---|
| 691 | 1 | **type** `public record Mark(int nation, double x, double y, double r, boolean labelled, double lx, double ly, String...` | A nation's dot on the screen, centre (x, y) and radius r, and where its name is written: (lx, ly) the centre and baseline of the name, the figures under it, drawn only if `labelled` (lx and ly are NaN if not). |
| 694 | 4 | `static double radius(double output)` | A dot's radius from a nation's output, real US$ of 2015: DOT_BASE_PX plus DOT_PER_ROOT_PX a root of a trillion, between DOT_MIN_PX and DOT_MAX_PX. |
| 707 | 34 | `public List<Mark> marks(GlobeFrame v, Nations nations, int year, ToDoubleFunction<String> nameW, ToDoubleFunction<String> figur...` | The dots and labels of a globe view (none in the PLANE tier), the nations largest output first (ties by number), for each nation whose node is on the visible side and within the canvas and its radius. |
| 743 | 11 | `private double ownShare(EarthProj.Ortho o, double w, double h, double m, double[] box, int nation, double[] xy, double[] ll)` | The share of a box's LABEL_SAMPLES_X by LABEL_SAMPLES_Y points (the middles of equal cells) that the ortho's inverse puts on the nation's coarse cells. |
| 755 | 7 | `private static boolean overlaps(double[][] placed, int count, double[] box)` |  |
| 764 | 4 | `public static double[] labelBox(Mark mark, ToDoubleFunction<String> nameW, ToDoubleFunction<String> figuresW)` | A labelled mark's box on the screen, {x0, y0, x1, y1}: the wider of the two lines across, centred on the label point, LABEL_H_PX tall from LABEL_ASCENT_PX above the name's baseline. |
| 773 | 14 | `public Mark dotAt(List<Mark> marks, double sx, double sy)` | The dot under a screen point: the mark whose radius and HIT_SLOP_PX it falls within (the nearest centre wins, then the smaller number); null if none, or if there are no marks. |
| 792 | 8 | `public int nationAt(GlobeFrame v, List<Mark> marks, double sx, double sy)` | The nation under a screen point: the dot whose radius and HIT_SLOP_PX it falls within (the nearest centre wins, then the smaller number), else the exact owner under the point (the ortho's inverse onto ownerAt()) when ... |

### THE HOVER AND THE CARD (lines 801-900)

| line | len | member | says |
|---:|---:|---|---|
| 806 | 4 | `private String distanceWords(int nation, double cityLat, double cityLon)` | The distance row's words: km from the city to the nation's node, along the great circle, whole km. |
| 812 | 4 | `public List<String> hoverWords(Nations nations, int nation, int year, double cityLat, double cityLon)` | What hovering a nation says: its name, "GDP US$2.24T . |
| 818 | 1 | **type** `public record Row(String label, String value, String rank)` | One line of the card: its label, its value and the rank beside it ("9th of 196"; empty where it has none). |
| 821 | 1 | **type** `public record Card(String name, String region, String distance, List<Row> rows)` | A nation's card: its name, its World Bank region (empty for none), its distance from the city and the rows. |
| 831 | 3 | `public Card card(Nations nations, int nation, int year, double cityLat, double cityLon, double dollarsAUsd)` | A nation's card for a year: its output, people and output a head, each with its rank among the nations; this year's growth and prices; its money and rate, and, when dollarsAUsd (the game dollar's price in US$, D$ a US... |
| 843 | 3 | `public Card card(Nations nations, int nation, int year, double cityLat, double cityLon, double dollarsAUsd, TradeLanes.YearToDa...` | The same, with the trade of the year so far (WN3: the city's money, thousands, and the tonnes each way): when it is given and the money is not both nothing, three rows follow - BUYS_WORDS, SELLS_WORDS and HAUL_WORDS -... |
| 852 | 19 | `public Card card(Nations nations, int nation, int year, double cityLat, double cityLon, double dollarsAUsd, TradeLanes.YearToDa...` | The same, with the money's rate this month (WN4: WorldMoneys.rateOfNation(), units a US$): when it is a number above 0 the Its money row shows it and the game dollar's worth of one of it from it, else the year's rate ... |
| 873 | 5 | `private static String percent(double share, boolean signed)` | A share as a percent to one decimal ("-" for NaN), with a sign if asked: "+1.6%". |
| 883 | 7 | `private static String moneyWords(Nations.Year y, double dollarsAUsd, double monthRate)` | "CAD 1.37 a US$ . |
| 891 | 3 | `private static String markWords(int mark)` |  |
| 896 | 4 | `public static String ordinal(int k)` | 1st, 2nd, 3rd, 4th ... |

### THE WORDS (lines 901-958)

| line | len | member | says |
|---:|---:|---|---|
| 909 | 1 | `public static String usd(double v)` | US$ as words: "US$2.24T" from a trillion (two decimals), "US$580B", "US$58.1B" and "US$5.81B" from a billion (three significant figures, a trailing ".0" dropped), the same in M from a million, and below that "US$" and... |
| 912 | 9 | `static String money(String sign, double v)` | Money as words in any sign, as usd() writes the US dollar's: "D$25M", and below a million the whole number, "D$580,000"; "-" for NaN. |
| 923 | 1 | `public static String city(double thousands)` | The city's money, given in thousands (the trade's unit), as words: 25,000 is "D$25M"; "-" for NaN. |
| 926 | 4 | `public static String haulWords(double tonnesOut, double tonnesIn)` | The haul as words: the tonnes sold and the tonnes bought, each as people() writes a count, " t out" and " t in", only those that are more than none; "-" for neither. |
| 932 | 9 | `public static String people(double v)` | People as words: "1.43B", "41.5M", "390k", "57k" (three significant figures, a trailing ".0" dropped), below a thousand the whole number. |
| 943 | 6 | `private static String three(double x)` | A number of [1, 1000) to three significant figures with a trailing ".0" dropped; null if it rounds up to 1000. |
| 951 | 7 | `static String sig3(double v)` | A positive figure to three significant figures: thousands separated from 1,000 ("54,300"), a trailing zero kept from 1 up ("1.30") and dropped under it ("0.86"). |

### A YEAR'S FIGURES AND RANKS - computed once for a Nations and a year (lines 959-1019)

| line | len | member | says |
|---:|---:|---|---|
| 964 | 50 | **type** `private static final class Table` | The figures of a year for all the nations, and each one's rank: the count of nations strictly larger, plus one (ties share it). |
| 972 | 29 | `Table(Nations nations, int year)` _(in NationsMap.Table)_ |  |
| 1003 | 1 | `private static double key(double v)` _(in NationsMap.Table)_ | A figure as it ranks: NaN last. |
| 1005 | 8 | `private static int[] ranks(double[] v)` _(in NationsMap.Table)_ |  |
| 1015 | 4 | `private Table table(Nations nations, int year)` |  |


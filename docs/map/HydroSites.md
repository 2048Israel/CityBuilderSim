# HydroSites.java - 440 lines · 31 methods · 13 constants · model

`ham/citybuildersim/HydroSites.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> The city's hydro sites: where a river and a drop lie on the ground it owns - the places a Hydro Station can be ordered
> (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.1).
> 
> WHY THIS EXISTS. Canada's power was mostly hydro from 1900, and the game had only coal and wind: nothing in it could
> tell a river from a lake, or read a drop. The Earth has the rivers (GRWL's centrelines, each vertex with its width in
> metres: EarthData.rivers()) and the slope of the land (ET1: each plot's relief class), but no heights on land
> (spec-earth E8 ruled them out), so a river's own fall, which is what a real head is, is not in the data. Its banks
> are. At every plot along a river the rise of the ground on each side is added up out to REACH plots from the water,
> each plot at its class's least grade, and the head is the lower bank's - a dam backs the river up no higher than its
> lower abutment. The flow comes from the width by hydraulic geometry. A generated world, and an earth-1 file, has no
> relief: no drop anywhere, no site, and the card says why.
> 
> ONE SITE A CELL, THE BEST POINT. The world is cut into CELL squares (128 plots, 3.84 km); a cell's site is its best
> point on any river - the most mean power, a tie to the lower y and then the lower x - and it is the city's when the
> plot at that point is the city's, as a field goes whole with its centre. A site is defined on the world, not on the
> ground owned (a cell's best reads every river sample in the cell, owned or not), so buying more of a river never
> merges two sites into one and never lowers what the city had. A site holds as many stations as its flow allows
> (slots()), and counts only on a landmass the city has linked (refresh(): ET5's rule for works).
> 
> DERIVED, NEVER SAVED. As Landmasses' books are: a pure function of the saved rectangles and the frozen Earth data,
> worked out again at every land change and at load (LandManager.landChanged()); no save field, no format bump.
> recount() reads nothing on a world with no relief, and on Earth only the 1-degree tiles round the cells the city owns
> ground in. The ground's reads are World.reliefAt()'s, kept a tile at a time.
> 
> THE CORE, scan(), takes a relief and the river's segments and nothing of the Earth, so HydroCheck can lay a valley on
> synthetic ground and prove each rule; the Earth and the city are only where the segments and the wanted cells come from.

**Uses:** [World](World.md) (8), [EarthData](EarthData.md) (4), [EarthProj](EarthProj.md) (3), [EarthTerrain](EarthTerrain.md) (2), [CityLand](CityLand.md) (1), [LandGrid](LandGrid.md) (1), [Landmasses](Landmasses.md) (1)

**Used by (3):** [BuildCard](BuildCard.md), [HydroCheck](HydroCheck.md), [LandManager](LandManager.md)

## Sections

| line | section |
|---:|---|
| 80 | THE PHYSICS - the flow, the power and a bank's rise |
| 136 | THE CORE - a river's segments and a relief, one site a cell |
| 208 | THE CITY'S SITES - recount() and refresh() |
| 414 | READS - plain getters; none fills anything |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `HydroSites.REACH` | `10` | How far each bank is read from the water, in plots: 10 (300 m), a valley's side as a dam's abutment reaches it. |
| 48 | `HydroSites.CELL` | `128` | The world's site cells, in plots a side: 128 (3.84 km, four tiles), aligned to the world's plot grid, where a river holds one site. |
| 51 | `HydroSites.HEAD_MIN_M` | `10` | The least head a site needs, metres: 10 - under it there is no drop, and the sample is no candidate. |
| 54 | `HydroSites.LEAST_GRADE` | `{ 0, 0.10, 0.30, 0.60 }` | The least grade (rise over run) of each relief class, indexed FLAT, FOOTHILL, STEEP, VERY_STEEP: 0, 0.10, 0.30 and 0.60, the edges World's classes are cut at (ET1; LandMap.GRADE_WORDS says them), so a bank reads each ... |
| 57 | `HydroSites.WIDTH_CAP_M` | `550` | The widest river read as flowing water, metres: 550, the width HG_COEFF gives Niagara's mean flow (about 5,800 m3/s) - GRWL keeps reservoirs and lake-like reaches a kilometre and more wide (Shawinigan's best, 1,088 m)... |
| 60 | `HydroSites.HG_COEFF` | `7.2` | Hydraulic geometry's coefficient, w = 7.2 Q^0.5 (a width in metres from the mean flow in cubic metres a second): Moody and Troutman 2002, quoted from memory, not re-read. |
| 63 | `HydroSites.EFFICIENCY` | `0.85` | Water to wire, the share of the water's power that reaches the grid: 0.85. |
| 66 | `HydroSites.G` | `9.81` | Gravity, metres a second squared. |
| 69 | `HydroSites.HOURS_A_MONTH` | `730` | The hours of a month the game's power figures are made at: 730 (the Wind Farm's arithmetic: kW x 730 h, over the unit). |
| 72 | `HydroSites.KWH_PER_UNIT` | `720` | The kilowatt-hours one unit of electricity is a month: 720 (derived from the Steel Foundry's documented 450 kWh a tonne; the Wind Farm's comment in BuildingManager). |
| 75 | `HydroSites.TILE_PAD_DEG` | `0.02` | A sample's reading is widened by this many degrees when the 1-degree tiles round a cell are found: 0.02 (2.2 km), past the curve of a cell's edge between the points it is found from. |
| 78 | `HydroSites.SEGMENT_CELLS_ASKED` | `16` | A segment whose box spans more than this many cells is kept without asking each: 16. |
| 156 | `HydroSites.BEST` | `(a, b) -> { int c = Double.compare(b.meanKw(), a.meanKw()); if(c = = 0) c = L...` | Whether one sample is better than another: the most mean power, then the lower y, then the lower x, then the greater head and width - a total order, so no tie depends on the order the segments are listed in. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 225 | `private List<Site> owned` | The sites on ground the city owns (their dams' plots are its), on every landmass. |
| 228 | `private List<Site> linked` | ...and those on a landmass the city has linked: the ones a station can be ordered against. |
| 231 | `private String readKey` | The Earth's rivers last read, kept while the tiles they are read from stay the same: the key (world, tiles) and the segments through the projection. |
| 232 | `private List<Segment> readSegments` |  |
| 235 | `private int segmentsRead` | How many river segments the last read looked at (those that meet a wanted cell), 0 where nothing was read: HydroCheck's "a generated world reads nothing at all". |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 399 | **type** `public final class HydroSites` | The city's hydro sites: where a river and a drop lie on the ground it owns - the places a Hydro Station can be ordered (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.1). |

### THE PHYSICS - the flow, the power and a bank's rise (lines 80-135)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 4 | `public static double dischargeM3s(double widthM)` | The mean flow of a river of this width, cubic metres a second: (min(width, WIDTH_CAP_M) / HG_COEFF) squared. |
| 91 | 3 | `public static double meanKw(double widthM, double headM)` | The mean power of a river of this width falling this far, kW: EFFICIENCY x G x Q x H (water's thousand kilograms a cubic metre makes the kilowatts). |
| 96 | 3 | `public static double leastGrade(byte relief)` | The least grade of a relief class (FLAT 0 to VERY_STEEP 0.60); a byte that is no class reads FLAT. |
| 101 | 3 | `public static double unitsOfKw(double kw)` | The kilowatt-hours a month's worth of a mean power makes, in the game's electricity units: kW x HOURS_A_MONTH / KWH_PER_UNIT. |
| 106 | 3 | **type** `public interface Relief` | One plot's relief, a World.FLAT to World.VERY_STEEP: World.reliefAt() for the city, a lambda for a fixture. |
| 107 | 1 | `byte at(long x, long y)` _(in HydroSites.Relief)_ |  |
| 111 | 3 | **type** `public interface Cells` | The cells a read is asked for, by their column and row (a plot's is Math.floorDiv(plot, CELL)). |
| 112 | 1 | `boolean has(long cx, long cy)` _(in HydroSites.Cells)_ |  |
| 120 | 9 | `public static double bankRiseM(Relief relief, double px, double py, double nx, double ny, double halfWidthPlots)` | The rise of one bank, metres: for k = 1 to REACH the plot at the point p, out along the unit direction (nx, ny) by halfWidthPlots + k - 0.5 plots (the centre of the kth plot past the water's edge), floored to plot coo... |
| 131 | 4 | `public static double headM(double leftRiseM, double rightRiseM)` | The head at a point, metres: the lower bank's rise, and nothing under HEAD_MIN_M. |

### THE CORE - a river's segments and a relief, one site a cell (lines 136-207)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 1 | **type** `public record Segment(double ax, double ay, double bx, double by, double widthA, double widthB)` | A river segment in plots, its ends and the river's true width at each, metres (EarthData.Run.width(), not EarthTerrain's half-widths, clamped at 0.75 plots). |
| 144 | 10 | **type** `public record Site(long x, long y, double widthM, double headM, double meanKw, int landmass)` | A site: the dam's plot, the river's width and the head there, the mean power, and the landmass of its bank (0 where the world has none: it reads as home). |
| 147 | 1 | `public double units()` _(in HydroSites.Site)_ | What it makes a month, in the game's electricity units. |
| 150 | 3 | `public int slots(double stationUnits)` _(in HydroSites.Site)_ | How many stations of this many units each it holds: floor(its units over theirs); 0 for a station of nothing. |
| 165 | 3 | `private static long key(long cx, long cy)` |  |
| 176 | 31 | `public static List<Site> scan(Relief relief, List<Segment> segments, Cells wanted)` | Each wanted cell's site, by the cells' rows and then columns: its best sample over every river segment given. |

### THE CITY'S SITES - recount() and refresh() (lines 208-413)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 3 | **type** `public interface Owns` | Whether the city owns a plot: CityLand.ownsPlot() for the city, a lambda for a fixture. |
| 214 | 1 | `boolean at(long x, long y)` _(in HydroSites.Owns)_ |  |
| 218 | 5 | `public static List<Site> keepOwned(List<Site> sites, Owns owns)` | The sites whose dam's plot is owned: a site is the city's when the plot at its best point is, as a field goes whole with its centre. |
| 238 | 1 | `public HydroSites()` | A bare one: no sites. |
| 246 | 16 | `void recount(World world, CityLand land)` | Works the city's sites out again (derived, never read by a getter): empty when the world is not Earth, the Earth data has no relief (a generated world, an earth-1 file) or the land owns no plot. |
| 268 | 8 | `public static List<Site> sitesOnWorld(World world, long x0, long y0, long x1, long y1)` | The sites a harness reads off the world itself, with no city (so every cell in the box is wanted and every site is kept): the sites of the cells that meet the box of plots [x0, x1) x [y0, y1), each with its landmass. |
| 278 | 10 | `private List<Site> read(World world, Set<Long> cells)` | The sites of these cells, from the Earth's rivers and relief, each given its landmass. |
| 290 | 13 | `static int landmassBeside(World world, Site s)` | The landmass of the first dry plot beside a dam, nearest first (rings of plots, the lower y then x on a ring): 0 where there is none, which reads as home. |
| 310 | 22 | `private List<Segment> rivers(World world, EarthTerrain earth, Set<Long> cells)` | The rivers near these cells as segments in plots with their true widths: the 1-degree tiles that cover each cell (widened by REACH and TILE_PAD_DEG), read with EarthData.riversIn() (each segment once), each vertex thr... |
| 334 | 15 | `private static List<Segment> project(EarthProj proj, List<EarthData.Run> runs)` | The river runs' consecutive vertices as segments in plots, widths in metres as stored; a segment with an end that does not project is left out. |
| 351 | 7 | `private static boolean touches(Segment s, Set<Long> cells)` | Whether a segment's box (a plot widened) meets a wanted cell: asked a cell at a time for a short one, assumed for a long one. |
| 364 | 23 | `private static void tilesOfCell(EarthProj proj, long cx, long cy, Set<Integer> out)` | Adds the 1-degree tiles that cover a cell widened by REACH plots to `out`: its corners and edge midpoints through the projection, the longitudes unwrapped about the first (as EarthTerrain.districtWater() finds its til... |
| 389 | 9 | `void refresh(Landmasses lms)` | Works the linked sites out again: the owned ones whose landmass is linked (Landmasses.linked(): home, 0 and what a port or a join reaches). |
| 400 | 7 | `void reset()` | Forgets everything, the rivers kept too (LandManager's reset: a new city, a load): no sites until recount(). |
| 409 | 4 | `void setForTest(List<Site> sites)` | Harnesses only: puts these sites in as the city's, owned and linked by fiat (as CityLand.setCentreRelief sets a fixture's hills). |

### READS - plain getters; none fills anything (lines 414-440)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 1 | `public List<Site> sites()` | The sites the city owns the dams of, on every landmass, by their cells' rows and columns. |
| 422 | 1 | `public List<Site> linked()` | The sites on a linked landmass: where a station can be ordered. |
| 425 | 5 | `public int slots(double stationUnits)` | How many stations of this many units each the linked sites hold in all: each site's floor(units / stationUnits), summed. |
| 432 | 5 | `public Site best()` | The linked site with the most mean power, or null for none: what the card names. |
| 439 | 1 | `public int segmentsRead()` | How many river segments the last read looked at (0 where nothing was read). |


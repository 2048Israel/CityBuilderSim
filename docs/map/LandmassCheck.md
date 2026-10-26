# LandmassCheck.java - 2,492 lines · 78 methods · 10 constants · harnesses

`ham/citybuildersim/LandmassCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the
> Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city has not linked is owned,
> counted and priced as ever but is no room for anyone except a sea terminal, and no works stands on its fields; a terminal
> ordered for an island takes the island's room, two standing terminals link it, nothing unlinks, and where the terminals
> stand is saved.
> 
> WHY THIS EXISTS. Until ET5 the model's ground was one pool with no place in it: an island bought across a strait was
> room for any building, and the Rotterdam city could "build" on Goeree-Overflakkee from its own streets. Nothing could
> say so, because nothing read a plot's landmass. This holds the reader, the books and the rule against fixtures that
> CAUSE each condition (an island square found by search with the model's own reader, bought through the harness door
> LandManager.buyRectForTest(); a city founded on the island, so that the mainland is across the water), and every
> figure worked out again from the model's constants and getters, never typed.
> 
> WHAT IT HAS TO PROVE:
> 
>   0. A GENERATED WORLD HAS NO LANDMASSES. hasLandmasses() false, the city a single landmass, nothing read (plotsRead()
>      0), every offer's unlinked figure 0, and the free ground, the share used, the zones and the coast test the old
>      expressions written out inline - on a land of three holdings far apart, on the default seed after 120 months, and
>      on a city founded on the Earth whose ground lies on its home landmass.
>   1. THE READER. tileLandmass() is landmassAt() at every plot; every dry plot on a cell the layer calls no land takes
>      the nearest land cell's id within LANDMASS_REACH, as a brute-force loop over the data says (such plots exist, one
>      resolves to the island); idsNear() holds every id a tile's plots resolve to, the reach's included (the gate's premise);
>      two worlds of one key agree; home is the id the founding card reads at the snapped site.
>   2. THE BOOKS BY LANDMASS. Each landmass's dry plots by relief, forest, shore and first holding equal a brute-force
>      count over every owned plot, a shore across a tile's edge included; they add up to the land's dry ground to the plot; a recount after a purchase is a
>      fresh recount; the names are "Home" and "Island 2"; the linked set (closure()) is home, the landmasses a join
>      reaches and, once a linked one has a port, every landmass with one.
>   3. FREE GROUND IS LINKED ONLY. A city with the island square and its twin without it have the same free ground,
>      share used, zone size and overFull within FREE_SLACK; what fits city-wide is refused; the unlinked free ground is
>      the square's dry plots; the investors' refusal ends with the clause, word for word.
>   4. ONLY A PORT ON UNLINKED GROUND. A House past the linked free ground is NO_LAND though the island would hold it;
>      a works on a field centred on an unlinked plot is NO_DEPOSIT and the field is in no site count, a field centred on
>      a lake plot of an island included (4b: the gate reads the plot's 15" cell, asked of a made-up field on a fresh water plot of the island, the fixture holding none); the same field at home is a deposit.
>      A desalination plant needs shore on a linked landmass, and an island with no shore gives a terminal NO_COAST (7).
>   5. TWO PORTS LINK. A terminal ordered for the island (the card's pick) takes the island's room, exactly, and moves
>      nothing the linked buildings stand on; on site or standing alone it links nothing; with home's standing it links
>      the island and the linked free ground rises by the island's unbuilt ground; a stopped shell never links; a third
>      landmass is linked by one terminal once a linked one has a port; the island's room, not the pool's, is the gate,
>      in an order and in a run; an order short of cash keeps its pick.
>   6. DEMOLITION. The port that links a landmass cannot be demolished (demolishable() leaves it out, demolish() says
>      "it is the port that links Island 2"); an island's port keeps its ground on the island, by the demolition's id,
>      until the demolition is done - across a save too - and then its room returns exactly; a shell likewise.
>   7. THE CHOICE. The card lists the landmasses with shore, home first; its pick is one order's, spent by an order and
>      forgotten at the settle; the rule (home, else the linked landmass with shore and most ground, else none) never
>      takes an island no port links; a pick without shore is passed over; the quote is asked for the landmass the order is for,
>      so a pick standing steers the card's quote and not an order by the rule.
>   8. THE PICKERS. An all-island offer has no buildable ground and is never the best for room, a shortfall or a
>      deposit - and would be without its split; ISLAND is tagged by the share; a shortfall only the island's dry ground
>      would cover buys no island.
>   9. THE SAVE. The placements under one key, absent for a city on one landmass; read back, resaved identical, a
>      reload equal to the city played on; a save without the key reads every terminal at home; a bad row is passed over.
>  10. THE MAP (ET5 slice C). A real CityMap of the two-landmass city: no building box, street, plot of the runs or siding on a plot
>      of ground no port links, no piece of ground holding one, the districts' free plots the linked ground's to the plot (a loop over
>      every owned plot); a terminal ordered for the island has its work on the island's shore (its shore plot reads 648, its quay is
>      owned salt, nothing short), one at home links the island and the districts are measured again, a piece holds the island's tiles,
>      the site index is built again and the sidecar's land stamp differs between the two linked sets; a generated city's sidecar
>      carries the land's own stamp; LandMap.unlinkedRuns() is a loop over the plots; an island city's map counts exactly the sites
>      the model's works gate leaves and draws no works on the field across the water; the terminals are laid on the landmass each
>      is counted for, a leaving unit keeps its work, and the count falling takes the newest work on its landmass; home filled to what
>      the model allows with the island unlinked draws nothing on the island.
> ... (12 more lines in the source)

**Uses:** [World](World.md) (179), [Landmasses](Landmasses.md) (143), [Game](Game.md) (94), [CityMap](CityMap.md) (53), [Resource](Resource.md) (42), [LandParcel](LandParcel.md) (33), [BuildingsTemplate](BuildingsTemplate.md) (33), [LandManager](LandManager.md) (26), [LongPlaytest](LongPlaytest.md) (25), [CityLand](CityLand.md) (20), [EarthData](EarthData.md) (14), [Founding](Founding.md) (12), [TilePainter](TilePainter.md) (11), [CityShore](CityShore.md) (11), [Deposit](Deposit.md) (10), [LandMarket](LandMarket.md) (10), [EarthTerrain](EarthTerrain.md) (9), [LandmassView](LandmassView.md) (9), [Ports](Ports.md) (9), [GameFiles](GameFiles.md) (8), [ConstructionControl](ConstructionControl.md) (7), [EarthSite](EarthSite.md) (6), [EarthCheck](EarthCheck.md) (5), [AutoBuilder](AutoBuilder.md) (5), [GridOffers](GridOffers.md) (5), [LandMap](LandMap.md) (4), [LandGrid](LandGrid.md) (3), [DistrictPlan](DistrictPlan.md) (3), [Zoning](Zoning.md) (2), [BuildingsStacks](BuildingsStacks.md) (2)... and 7 more

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 135 | THE FIXTURES |
| 223 | MAIN |
| 259 | 0. A GENERATED WORLD HAS NO LANDMASSES |
| 338 | 1. THE READER |
| 471 | THE FIXTURE CITIES |
| 515 | 2. THE BOOKS BY LANDMASS |
| 741 | 3. FREE GROUND IS LINKED ONLY |
| 813 | 4. ONLY A PORT ON UNLINKED GROUND |
| 925 | 8. THE PICKERS |
| 1035 | 4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND |
| 1138 | 5. TWO PORTS LINK |
| 1366 | 6. DEMOLITION |
| 1445 | 7. THE CHOICE |
| 1549 | 9. THE SAVE |
| 1654 | 10. THE MAP |
| 2164 | 12. GROWN ACROSS LANDMASSES |
| 2370 | 13. THE FULL FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 95 | `LandmassCheck.out` | `System.out` |  |
| 96 | `LandmassCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 99 | `LandmassCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 102 | `LandmassCheck.FREE_SLACK` | `1.0` | Two square-foot figures of one ground are the same within a square foot (the two landmass counts and the land's sum differ in the last bits of a double). |
| 105 | `LandmassCheck.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The area of a plot, square feet, as the books reckon it. |
| 108 | `LandmassCheck.SQUARE` | `64` | The side of an island square, in plots. |
| 111 | `LandmassCheck.RLAT` | `51.90625, RLON = 4.48125` | Rotterdam, the fixture's window: where the city is founded. |
| 114 | `LandmassCheck.ON_THE_ISLAND` | `{ { 51.77, 4.15 }, { 51.75, 4.20 }, { 51.80, 4.10 } }` | Where a city on Goeree-Overflakkee is founded (the mainland is then across the water): candidate spots, the first that serves. |
| 1187 | `LandmassCheck.GSON` | `new com.google.gson.Gson()` |  |
| 2390 | `LandmassCheck.RUN_MONTHS` | `1200` | The months the Rotterdam run plays. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `static int fails` |  |
| 141 | `final Game g` |  |
| 142 | `final GameFiles files` |  |
| 143 | `final LandManager lm` |  |
| 144 | `final World w` |  |
| 145 | `final World.Key key` |  |
| 289 | `static int coastChecked` | How many sea terminals and desalination plants the last oldExpressions() asked hasCoastFor() about (the five terminals and the plants). |
| 477 | `World.Key key` |  |
| 478 | `City a, b` |  |
| 479 | `Square withShore, withoutShore, homeSalt` |  |
| 480 | `LandParcel island` |  |
| 481 | `boolean ok` |  |
| 521 | `final long[] relief` |  |
| 522 | `long forest, shore` |  |
| 523 | `int firstHolding` |  |
| 840 | `City city` |  |
| 841 | `FieldAt away, home` |  |
| 842 | `double[] spot` |  |
| 1451 | `int standing, onSite, shell` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 2401 | **type** `public class LandmassCheck` | The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city h... |
| 116 | 4 | `static void check(String label, boolean ok)` |  |
| 121 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 126 | 5 | `static void quietly(Runnable r)` |  |
| 133 | 1 | `static boolean near(double a, double b)` | Two square-foot figures within FREE_SLACK. |

### THE FIXTURES (lines 135-222)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 29 | **type** `static final class City` | A city on a fixture world: its game, its land office and its world. |
| 147 | 12 | `City(String label, World.Key key, int houses)` _(in LandmassCheck.City)_ |  |
| 160 | 1 | `Landmasses books()` _(in LandmassCheck.City)_ |  |
| 163 | 5 | `LandParcel buy(Square s)` _(in LandmassCheck.City)_ | Buys a square (as an offer of it would be, at no price) and returns the parcel. |
| 171 | 1 | **type** `record Square(long x0, long y0, int id, long onId, long dry, long salt, long shore, boolean pure)` | A square of SQUARE x SQUARE plots, measured by the model's own reader: what lies on a landmass in it. |
| 174 | 17 | `static Square measure(World w, long x0, long y0, int id)` | Measures the square at (x0, y0) for landmass `id`: its dry plots on it, its salt, its shore (a flat or foothill dry plot of it beside salt in the square) and whether every dry plot of it is `id`'s. |
| 192 | 3 | `private static boolean saltIn(World w, long x0, long y0, long x, long y)` |  |
| 201 | 14 | `static Square squareWith(World w, int id, long sx, long sy, int tiles, Predicate<Square> serves)` | The nearest square (tile-aligned, SQUARE plots a side) to (sx, sy) that serves, searched out to `tiles` tiles each way with the model's reader and bounded - a FAIL for the caller, never a hang. |
| 217 | 5 | `static long[] plotOf(World w, double lat, double lon)` | The plot of a latitude and longitude on a world. |

### MAIN (lines 223-258)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 31 | `public static void main(String[] args) throws Exception` |  |

### 0. A GENERATED WORLD HAS NO LANDMASSES (lines 259-337)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 23 | `static boolean oldExpressions(String what, LandManager lm, Game g)` | The old expressions of the free ground, written out from the public getters: what a city with one landmass reads. |
| 291 | 46 | `static void generatedWorld()` |  |

### 1. THE READER (lines 338-470)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 23 | `static int bruteResolved(EarthData d, double lat, double lon)` | The nearest land cell's id by a loop over the data's own landmassAt(): the cell holding the point, or the least dr squared + dc squared within LANDMASS_REACH, the lowest id on a tie, 0 for none. |
| 367 | 103 | `static void theReader(EarthData d, World.Key key)` |  |

### THE FIXTURE CITIES (lines 471-514)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 7 | **type** `static final class Fixtures` | The squares and cities sections 2 to 8 share. |
| 484 | 30 | `static Fixtures fixtures(World.Key key)` |  |

### 2. THE BOOKS BY LANDMASS (lines 515-740)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 6 | **type** `static final class Brute` | One landmass's owned ground counted plot by plot over the box of the city's ground. |
| 524 | 1 | `long dry()` _(in LandmassCheck.Brute)_ |  |
| 527 | 21 | `static TreeMap<Integer, Brute> bruteBooks(World w, CityLand land, int home)` |  |
| 549 | 3 | `private static boolean ownedSalt(World w, CityLand land, long x, long y)` |  |
| 553 | 80 | `static void theBooks(Fixtures f)` |  |
| 635 | 10 | `static boolean sameAsBrute(List<Landmasses.Row> rows, TreeMap<Integer, Brute> brute)` | Whether the rows are a brute-force count's: each landmass's dry plots by relief, forest, shore and (off home) first holding. |
| 650 | 19 | `static void settledShore(Fixtures f)` | A block of home's coast bought beside the island: its interior tiles are wholly owned with wholly owned neighbours, so their shore can never change and the next recount adds it without reading them - and the count is ... |
| 671 | 1 | **type** `record EdgePair(long ax, long ay, long bx, long by)` | Two plots across a tile's edge: a dry flat or foothill plot of home's at the edge of its tile, and the salt plot in the tile beside it. |
| 677 | 18 | `static EdgePair edgePair(World w, int home, long sx, long sy, int tiles, int dx, int dy)` | The nearest pair of plots to (sx, sy), searched out `tiles` tiles each way with the model's reader and bounded: a dry flat or foothill plot of home's on the (dx, dy) side of its tile, with a salt plot straight across ... |
| 702 | 32 | `static void shoreAcrossTileEdges(Fixtures f)` | The shore at a tile's edge: a dry plot on the edge of its tile with owned salt across it, in the tile beside, is shore though its own tile holds no owned salt. |
| 735 | 5 | `static String describe(List<Landmasses.Row> rows)` |  |

### 3. FREE GROUND IS LINKED ONLY (lines 741-812)

| line | len | member | says |
|---:|---:|---|---|
| 745 | 67 | `static void freeGroundIsLinked(Fixtures f)` |  |

### 4. ONLY A PORT ON UNLINKED GROUND (lines 813-924)

| line | len | member | says |
|---:|---:|---|---|
| 818 | 1 | **type** `record FieldAt(Resource resource, Deposit field, int landmass, double km)` | A field of a sited resource centred on a dry plot of a landmass but the city's home, nearest to the site, found by search. |
| 820 | 17 | `static FieldAt nearestField(World w, Resource r, int notLandmass, boolean offHome)` |  |
| 839 | 5 | **type** `static final class IslandCity` | The city on the island whose mainland holds a field of iron: found by search over the candidate spots. |
| 845 | 17 | `static IslandCity onTheIsland(String id)` |  |
| 864 | 5 | `static LandParcel buyAround(City c, long x, long y)` | Buys a square round a plot (as an offer would be) on a city; the parcel. |
| 870 | 54 | `static void onlyAPort(Fixtures f, String dataId)` |  |

### 8. THE PICKERS (lines 925-1034)

| line | len | member | says |
|---:|---:|---|---|
| 929 | 105 | `static void thePickers(Fixtures f, String dataId)` |  |

### 4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND (lines 1035-1137)

| line | len | member | says |
|---:|---:|---|---|
| 1040 | 19 | `static FieldAt lakeCentred(World w, int home, long[] near)` | A field of a sited resource centred on a water plot that is not the sea's (a lake, a river), whose 15" cell the layer puts on a landmass but home's: the nearest to a plot, found by search over the cells round it. |
| 1064 | 67 | `static void lakeCentredField(Fixtures f)` | The review of slice A, (b): the works gate reads a field's landmass from its centre plot's 15" cell resolved by the D2 rule, not from the plot, so a field centred on a lake plot of an island (landmassAt() gives a wate... |
| 1133 | 4 | `static CityLand.Piece pieceAt(Resource r, long x, long y, int sites)` | A piece of the ground holding a made-up field of `sites` sites centred on plot (x, y): what the works gate is asked of. |

### 5. TWO PORTS LINK (lines 1138-1365)

| line | len | member | says |
|---:|---:|---|---|
| 1142 | 5 | `static BuildingsTemplate tpl(City c, String name)` |  |
| 1149 | 1 | `static BuildingsTemplate own(City c, BuildingsTemplate t)` | A template as this city has it (a city's templates are its own; another city's object is never handed to its buildings). |
| 1151 | 1 | `static int standing(City c, BuildingsTemplate t)` |  |
| 1153 | 4 | `static int onSite(City c, BuildingsTemplate t)` |  |
| 1159 | 4 | `static boolean until(City c, java.util.function.BooleanSupplier done, int most)` | Months played quietly until `done` holds, at most `most`; whether it does. |
| 1165 | 5 | `static Game.BuildResult order(City c, BuildingsTemplate t, int n, boolean instant, int asked)` | An order, quietly: `asked` is Landmasses.CHOSEN, RULE or a landmass's id. |
| 1172 | 4 | `static Game.BuildResult orderOn(City c, BuildingsTemplate t, int n, boolean instant, int landmass)` | An order of a terminal for a landmass by the card's path: the pick, then the order. |
| 1178 | 8 | `static City portCity(Fixtures f, String label, boolean homeShore)` | A city of forty houses that owns the island square (shore on it) and, when asked, a stretch of home's coast. |
| 1190 | 1 | `static String stateOf(City c)` | The saved placements as JSON: what the save writes under "landmasses" (null for none). |
| 1193 | 15 | `static Square thirdLandmass(World w, EarthData d)` | The third landmass for the closure's third leg: the first cell of a landmass in the tile west of Goeree, and a square about it with dry ground and shore. |
| 1209 | 156 | `static void twoPortsLink(Fixtures f, EarthData data)` |  |

### 6. DEMOLITION (lines 1366-1444)

| line | len | member | says |
|---:|---:|---|---|
| 1370 | 74 | `static void demolition(Fixtures f)` |  |

### 7. THE CHOICE (lines 1445-1548)

| line | len | member | says |
|---:|---:|---|---|
| 1450 | 11 | **type** `static final class StubStock implements Landmasses.Stock` | What Landmasses reads of the buildings, for a unit test of the rule: one terminal template and fixed counts. |
| 1452 | 1 | `public int[] portTemplates()` _(in LandmassCheck.StubStock)_ |  |
| 1453 | 1 | `public boolean known(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1454 | 1 | `public int standing(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1455 | 1 | `public int onSite(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1456 | 1 | `public int shell(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1457 | 1 | `public List<int[]> demolitions(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1458 | 1 | `public double landSqFt(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1459 | 1 | `public int cargo(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1462 | 86 | `static void theChoice(Fixtures f)` |  |

### 9. THE SAVE (lines 1549-1653)

| line | len | member | says |
|---:|---:|---|---|
| 1553 | 75 | `static void theSave(Fixtures f) throws Exception` |  |
| 1630 | 12 | `static void diffPaths(String path, com.google.gson.JsonElement a, com.google.gson.JsonElement b, java.util.List<String> out, in...` | The paths at which two saves' JSON differ, down to the leaf (at most `most`): what jsonDiff() names only by its top key. |
| 1643 | 4 | `static void assertSaved(City c, int slot)` |  |
| 1648 | 5 | `static GameFiles.Result quietlyResult(java.util.function.Supplier<GameFiles.Result> s)` |  |

### 10. THE MAP (lines 1654-2163)

| line | len | member | says |
|---:|---:|---|---|
| 1659 | 3 | `static boolean unlinkedPlot(World w, CityLand land, Landmasses lms, long x, long y)` | Whether plot (x, y) is owned dry ground on a landmass the model has not linked: ground the map must draw nothing on. |
| 1668 | 28 | `static int[] drawnOnUnlinked(CityMap map, World w, CityLand land, Landmasses lms, long tx0, long ty0, int n)` | What the map draws on unlinked ground, counted by brute force over the n x n tiles from (tx0, ty0) with the painter's own inputs (CityMap.tileInput()): {plots under building boxes, street plots, plots of the runs, sid... |
| 1698 | 16 | `static int boxesOnLandmass(CityMap map, World w, int id, long tx0, long ty0, int n)` | How many buildings the map draws with their first plot on landmass `id` over the n x n tiles from (tx0, ty0): every box but a terminal's work and a part of a building counted on another tile. |
| 1716 | 18 | `static long freePlots(CityMap map, World w, Landmasses lms, boolean linkedOnly)` | The plots the map's districts could build on by brute force over the tiles of the owned box: owned, buildable, and - when `linkedOnly` - on a landmass the model links. |
| 1736 | 5 | `static long districtsFree(CityMap map)` | The free plots the map's districts hold, summed. |
| 1743 | 3 | `static boolean ownedDryOn(World w, CityLand land, int id, long x, long y)` | Whether plot (x, y) is owned dry ground of landmass `id`, by the model's own readers. |
| 1754 | 18 | `static int[] givenToPlansAndRuns(CityMap map, World w, CityLand land, int id, Square sq)` | What the district plans and the runs are given of landmass `id`'s owned dry ground, a plot at a time over the square: {plots looked at, those in a district the map holds, those the plan's input (CityMap.planInputHere(... |
| 1774 | 12 | `static long landStampOfSidecar(byte[] deflated) throws java.io.IOException` | The land stamp a sidecar's header carries (after the magic, the format, the world, the month, its own stamp and the site). |
| 1788 | 16 | `static int[] worksOnField(CityMap m, Deposit field, Resource r)` | The sites of a field as the map lists them (CityMap.siteList()), and how many carry a works: {sites listed, sites with a works}. |
| 1806 | 6 | `static Landmasses.Picture linkedWith(Landmasses.Picture p, int id)` | The picture with a landmass added to its linked set and its stamp moved: what a link makes of it, handed to a map by hand. |
| 1813 | 350 | `static void theMap(Fixtures f) throws Exception` |  |

### 12. GROWN ACROSS LANDMASSES (lines 2164-2369)

| line | len | member | says |
|---:|---:|---|---|
| 2173 | 56 | `static String audit(Game g)` | The model's books of the terminals and the ground, audited at one look: the linked free ground fits the linked owned ground less the linked built ground; the ground the unlinked landmasses' terminals hold is what the ... |
| 2231 | 5 | `static int buildingsOf(Game g)` | The buildings standing, every template's. |
| 2237 | 132 | `static void grown(Fixtures f)` |  |

### 13. THE FULL FILE (lines 2370-2492)

| line | len | member | says |
|---:|---:|---|---|
| 2375 | 13 | `static EarthData theEarth()` | The Earth's data where it is named or lies at its place, or null - and the one line saying so. |
| 2392 | 74 | `static void fullFile(EarthData file)` |  |
| 2467 | 17 | `private static void look(Game g, int[] looks, String[] first)` |  |
| 2486 | 6 | `static double[] dryArea(double km2)` | The five areas of an offer of this many square kilometres of dry ground. |


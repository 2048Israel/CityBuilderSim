# LandmassCheck.java - 2,915 lines · 83 methods · 10 constants · harnesses

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
> ... (21 more lines in the source)

**Uses:** [World](World.md) (184), [Landmasses](Landmasses.md) (170), [LandmassView](LandmassView.md) (147), [Game](Game.md) (98), [CityMap](CityMap.md) (53), [Resource](Resource.md) (42), [BuildingsTemplate](BuildingsTemplate.md) (41), [LandManager](LandManager.md) (40), [LandParcel](LandParcel.md) (39), [LandMap](LandMap.md) (29), [LongPlaytest](LongPlaytest.md) (28), [CityLand](CityLand.md) (25), [EarthData](EarthData.md) (14), [LandMarket](LandMarket.md) (13), [Ports](Ports.md) (13), [Founding](Founding.md) (12), [TilePainter](TilePainter.md) (11), [CityShore](CityShore.md) (11), [Deposit](Deposit.md) (10), [BuildCard](BuildCard.md) (10), [EarthTerrain](EarthTerrain.md) (9), [GameFiles](GameFiles.md) (8), [ZoningView](ZoningView.md) (8), [GridOffers](GridOffers.md) (7), [ConstructionControl](ConstructionControl.md) (7), [EarthSite](EarthSite.md) (6), [EarthCheck](EarthCheck.md) (5), [AutoBuilder](AutoBuilder.md) (5), [Notice](Notice.md) (5), [LandGrid](LandGrid.md) (3)... and 12 more

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 144 | THE FIXTURES |
| 232 | MAIN |
| 277 | THE PROBE: --probe <folder> |
| 356 | 0. A GENERATED WORLD HAS NO LANDMASSES |
| 435 | 1. THE READER |
| 568 | THE FIXTURE CITIES |
| 612 | 2. THE BOOKS BY LANDMASS |
| 838 | 3. FREE GROUND IS LINKED ONLY |
| 910 | 4. ONLY A PORT ON UNLINKED GROUND |
| 1022 | 8. THE PICKERS |
| 1132 | 4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND |
| 1235 | 5. TWO PORTS LINK |
| 1463 | 6. DEMOLITION |
| 1542 | 7. THE CHOICE |
| 1646 | 9. THE SAVE |
| 1751 | 10. THE MAP |
| 2261 | 11. THE WORDS |
| 2587 | 12. GROWN ACROSS LANDMASSES |
| 2793 | 13. THE FULL FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `LandmassCheck.out` | `System.out` |  |
| 105 | `LandmassCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 108 | `LandmassCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 111 | `LandmassCheck.FREE_SLACK` | `1.0` | Two square-foot figures of one ground are the same within a square foot (the two landmass counts and the land's sum differ in the last bits of a double). |
| 114 | `LandmassCheck.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The area of a plot, square feet, as the books reckon it. |
| 117 | `LandmassCheck.SQUARE` | `64` | The side of an island square, in plots. |
| 120 | `LandmassCheck.RLAT` | `51.90625, RLON = 4.48125` | Rotterdam, the fixture's window: where the city is founded. |
| 123 | `LandmassCheck.ON_THE_ISLAND` | `{ { 51.77, 4.15 }, { 51.75, 4.20 }, { 51.80, 4.10 } }` | Where a city on Goeree-Overflakkee is founded (the mainland is then across the water): candidate spots, the first that serves. |
| 1284 | `LandmassCheck.GSON` | `new com.google.gson.Gson()` |  |
| 2813 | `LandmassCheck.RUN_MONTHS` | `1200` | The months the Rotterdam run plays. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `static int fails` |  |
| 150 | `final Game g` |  |
| 151 | `final GameFiles files` |  |
| 152 | `final LandManager lm` |  |
| 153 | `final World w` |  |
| 154 | `final World.Key key` |  |
| 386 | `static int coastChecked` | How many sea terminals and desalination plants the last oldExpressions() asked hasCoastFor() about (the five terminals and the plants). |
| 574 | `World.Key key` |  |
| 575 | `City a, b` |  |
| 576 | `Square withShore, withoutShore, homeSalt` |  |
| 577 | `LandParcel island` |  |
| 578 | `boolean ok` |  |
| 618 | `final long[] relief` |  |
| 619 | `long forest, shore` |  |
| 620 | `int firstHolding` |  |
| 937 | `City city` |  |
| 938 | `FieldAt away, home` |  |
| 939 | `double[] spot` |  |
| 1548 | `int standing, onSite, shell` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 101 | 2815 | **type** `public class LandmassCheck` | The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city h... |
| 125 | 4 | `static void check(String label, boolean ok)` |  |
| 130 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 135 | 5 | `static void quietly(Runnable r)` |  |
| 142 | 1 | `static boolean near(double a, double b)` | Two square-foot figures within FREE_SLACK. |

### THE FIXTURES (lines 144-231)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 29 | **type** `static final class City` | A city on a fixture world: its game, its land office and its world. |
| 156 | 12 | `City(String label, World.Key key, int houses)` _(in LandmassCheck.City)_ |  |
| 169 | 1 | `Landmasses books()` _(in LandmassCheck.City)_ |  |
| 172 | 5 | `LandParcel buy(Square s)` _(in LandmassCheck.City)_ | Buys a square (as an offer of it would be, at no price) and returns the parcel. |
| 180 | 1 | **type** `record Square(long x0, long y0, int id, long onId, long dry, long salt, long shore, boolean pure)` | A square of SQUARE x SQUARE plots, measured by the model's own reader: what lies on a landmass in it. |
| 183 | 17 | `static Square measure(World w, long x0, long y0, int id)` | Measures the square at (x0, y0) for landmass `id`: its dry plots on it, its salt, its shore (a flat or foothill dry plot of it beside salt in the square) and whether every dry plot of it is `id`'s. |
| 201 | 3 | `private static boolean saltIn(World w, long x0, long y0, long x, long y)` |  |
| 210 | 14 | `static Square squareWith(World w, int id, long sx, long sy, int tiles, Predicate<Square> serves)` | The nearest square (tile-aligned, SQUARE plots a side) to (sx, sy) that serves, searched out to `tiles` tiles each way with the model's reader and bounded - a FAIL for the caller, never a hang. |
| 226 | 5 | `static long[] plotOf(World w, double lat, double lon)` | The plot of a latitude and longitude on a world. |

### MAIN (lines 232-276)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 40 | `public static void main(String[] args) throws Exception` |  |

### THE PROBE: --probe <folder> (lines 277-355)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 13 | `static java.nio.file.Path newestSave(java.nio.file.Path folder) throws java.io.IOException` | The newest save under a folder: a .json file that is no history, the latest written; null for none. |
| 305 | 50 | `static int probe(String folder) throws Exception` | What a kept city owns across the water, from its save alone (ET5, the roll-out's check on whether the playtests' cities reach another landmass): the newest save under the folder read through DataSave and CityLand.rest... |

### 0. A GENERATED WORLD HAS NO LANDMASSES (lines 356-434)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 23 | `static boolean oldExpressions(String what, LandManager lm, Game g)` | The old expressions of the free ground, written out from the public getters: what a city with one landmass reads. |
| 388 | 46 | `static void generatedWorld()` |  |

### 1. THE READER (lines 435-567)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 23 | `static int bruteResolved(EarthData d, double lat, double lon)` | The nearest land cell's id by a loop over the data's own landmassAt(): the cell holding the point, or the least dr squared + dc squared within LANDMASS_REACH, the lowest id on a tie, 0 for none. |
| 464 | 103 | `static void theReader(EarthData d, World.Key key)` |  |

### THE FIXTURE CITIES (lines 568-611)

| line | len | member | says |
|---:|---:|---|---|
| 573 | 7 | **type** `static final class Fixtures` | The squares and cities sections 2 to 8 share. |
| 581 | 30 | `static Fixtures fixtures(World.Key key)` |  |

### 2. THE BOOKS BY LANDMASS (lines 612-837)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 6 | **type** `static final class Brute` | One landmass's owned ground counted plot by plot over the box of the city's ground. |
| 621 | 1 | `long dry()` _(in LandmassCheck.Brute)_ |  |
| 624 | 21 | `static TreeMap<Integer, Brute> bruteBooks(World w, CityLand land, int home)` |  |
| 646 | 3 | `private static boolean ownedSalt(World w, CityLand land, long x, long y)` |  |
| 650 | 80 | `static void theBooks(Fixtures f)` |  |
| 732 | 10 | `static boolean sameAsBrute(List<Landmasses.Row> rows, TreeMap<Integer, Brute> brute)` | Whether the rows are a brute-force count's: each landmass's dry plots by relief, forest, shore and (off home) first holding. |
| 747 | 19 | `static void settledShore(Fixtures f)` | A block of home's coast bought beside the island: its interior tiles are wholly owned with wholly owned neighbours, so their shore can never change and the next recount adds it without reading them - and the count is ... |
| 768 | 1 | **type** `record EdgePair(long ax, long ay, long bx, long by)` | Two plots across a tile's edge: a dry flat or foothill plot of home's at the edge of its tile, and the salt plot in the tile beside it. |
| 774 | 18 | `static EdgePair edgePair(World w, int home, long sx, long sy, int tiles, int dx, int dy)` | The nearest pair of plots to (sx, sy), searched out `tiles` tiles each way with the model's reader and bounded: a dry flat or foothill plot of home's on the (dx, dy) side of its tile, with a salt plot straight across ... |
| 799 | 32 | `static void shoreAcrossTileEdges(Fixtures f)` | The shore at a tile's edge: a dry plot on the edge of its tile with owned salt across it, in the tile beside, is shore though its own tile holds no owned salt. |
| 832 | 5 | `static String describe(List<Landmasses.Row> rows)` |  |

### 3. FREE GROUND IS LINKED ONLY (lines 838-909)

| line | len | member | says |
|---:|---:|---|---|
| 842 | 67 | `static void freeGroundIsLinked(Fixtures f)` |  |

### 4. ONLY A PORT ON UNLINKED GROUND (lines 910-1021)

| line | len | member | says |
|---:|---:|---|---|
| 915 | 1 | **type** `record FieldAt(Resource resource, Deposit field, int landmass, double km)` | A field of a sited resource centred on a dry plot of a landmass but the city's home, nearest to the site, found by search. |
| 917 | 17 | `static FieldAt nearestField(World w, Resource r, int notLandmass, boolean offHome)` |  |
| 936 | 5 | **type** `static final class IslandCity` | The city on the island whose mainland holds a field of iron: found by search over the candidate spots. |
| 942 | 17 | `static IslandCity onTheIsland(String id)` |  |
| 961 | 5 | `static LandParcel buyAround(City c, long x, long y)` | Buys a square round a plot (as an offer would be) on a city; the parcel. |
| 967 | 54 | `static void onlyAPort(Fixtures f, String dataId)` |  |

### 8. THE PICKERS (lines 1022-1131)

| line | len | member | says |
|---:|---:|---|---|
| 1026 | 105 | `static void thePickers(Fixtures f, String dataId)` |  |

### 4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND (lines 1132-1234)

| line | len | member | says |
|---:|---:|---|---|
| 1137 | 19 | `static FieldAt lakeCentred(World w, int home, long[] near)` | A field of a sited resource centred on a water plot that is not the sea's (a lake, a river), whose 15" cell the layer puts on a landmass but home's: the nearest to a plot, found by search over the cells round it. |
| 1161 | 67 | `static void lakeCentredField(Fixtures f)` | The review of slice A, (b): the works gate reads a field's landmass from its centre plot's 15" cell resolved by the D2 rule, not from the plot, so a field centred on a lake plot of an island (landmassAt() gives a wate... |
| 1230 | 4 | `static CityLand.Piece pieceAt(Resource r, long x, long y, int sites)` | A piece of the ground holding a made-up field of `sites` sites centred on plot (x, y): what the works gate is asked of. |

### 5. TWO PORTS LINK (lines 1235-1462)

| line | len | member | says |
|---:|---:|---|---|
| 1239 | 5 | `static BuildingsTemplate tpl(City c, String name)` |  |
| 1246 | 1 | `static BuildingsTemplate own(City c, BuildingsTemplate t)` | A template as this city has it (a city's templates are its own; another city's object is never handed to its buildings). |
| 1248 | 1 | `static int standing(City c, BuildingsTemplate t)` |  |
| 1250 | 4 | `static int onSite(City c, BuildingsTemplate t)` |  |
| 1256 | 4 | `static boolean until(City c, java.util.function.BooleanSupplier done, int most)` | Months played quietly until `done` holds, at most `most`; whether it does. |
| 1262 | 5 | `static Game.BuildResult order(City c, BuildingsTemplate t, int n, boolean instant, int asked)` | An order, quietly: `asked` is Landmasses.CHOSEN, RULE or a landmass's id. |
| 1269 | 4 | `static Game.BuildResult orderOn(City c, BuildingsTemplate t, int n, boolean instant, int landmass)` | An order of a terminal for a landmass by the card's path: the pick, then the order. |
| 1275 | 8 | `static City portCity(Fixtures f, String label, boolean homeShore)` | A city of forty houses that owns the island square (shore on it) and, when asked, a stretch of home's coast. |
| 1287 | 1 | `static String stateOf(City c)` | The saved placements as JSON: what the save writes under "landmasses" (null for none). |
| 1290 | 15 | `static Square thirdLandmass(World w, EarthData d)` | The third landmass for the closure's third leg: the first cell of a landmass in the tile west of Goeree, and a square about it with dry ground and shore. |
| 1306 | 156 | `static void twoPortsLink(Fixtures f, EarthData data)` |  |

### 6. DEMOLITION (lines 1463-1541)

| line | len | member | says |
|---:|---:|---|---|
| 1467 | 74 | `static void demolition(Fixtures f)` |  |

### 7. THE CHOICE (lines 1542-1645)

| line | len | member | says |
|---:|---:|---|---|
| 1547 | 11 | **type** `static final class StubStock implements Landmasses.Stock` | What Landmasses reads of the buildings, for a unit test of the rule: one terminal template and fixed counts. |
| 1549 | 1 | `public int[] portTemplates()` _(in LandmassCheck.StubStock)_ |  |
| 1550 | 1 | `public boolean known(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1551 | 1 | `public int standing(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1552 | 1 | `public int onSite(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1553 | 1 | `public int shell(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1554 | 1 | `public List<int[]> demolitions(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1555 | 1 | `public double landSqFt(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1556 | 1 | `public int cargo(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1559 | 86 | `static void theChoice(Fixtures f)` |  |

### 9. THE SAVE (lines 1646-1750)

| line | len | member | says |
|---:|---:|---|---|
| 1650 | 75 | `static void theSave(Fixtures f) throws Exception` |  |
| 1727 | 12 | `static void diffPaths(String path, com.google.gson.JsonElement a, com.google.gson.JsonElement b, java.util.List<String> out, in...` | The paths at which two saves' JSON differ, down to the leaf (at most `most`): what jsonDiff() names only by its top key. |
| 1740 | 4 | `static void assertSaved(City c, int slot)` |  |
| 1745 | 5 | `static GameFiles.Result quietlyResult(java.util.function.Supplier<GameFiles.Result> s)` |  |

### 10. THE MAP (lines 1751-2260)

| line | len | member | says |
|---:|---:|---|---|
| 1756 | 3 | `static boolean unlinkedPlot(World w, CityLand land, Landmasses lms, long x, long y)` | Whether plot (x, y) is owned dry ground on a landmass the model has not linked: ground the map must draw nothing on. |
| 1765 | 28 | `static int[] drawnOnUnlinked(CityMap map, World w, CityLand land, Landmasses lms, long tx0, long ty0, int n)` | What the map draws on unlinked ground, counted by brute force over the n x n tiles from (tx0, ty0) with the painter's own inputs (CityMap.tileInput()): {plots under building boxes, street plots, plots of the runs, sid... |
| 1795 | 16 | `static int boxesOnLandmass(CityMap map, World w, int id, long tx0, long ty0, int n)` | How many buildings the map draws with their first plot on landmass `id` over the n x n tiles from (tx0, ty0): every box but a terminal's work and a part of a building counted on another tile. |
| 1813 | 18 | `static long freePlots(CityMap map, World w, Landmasses lms, boolean linkedOnly)` | The plots the map's districts could build on by brute force over the tiles of the owned box: owned, buildable, and - when `linkedOnly` - on a landmass the model links. |
| 1833 | 5 | `static long districtsFree(CityMap map)` | The free plots the map's districts hold, summed. |
| 1840 | 3 | `static boolean ownedDryOn(World w, CityLand land, int id, long x, long y)` | Whether plot (x, y) is owned dry ground of landmass `id`, by the model's own readers. |
| 1851 | 18 | `static int[] givenToPlansAndRuns(CityMap map, World w, CityLand land, int id, Square sq)` | What the district plans and the runs are given of landmass `id`'s owned dry ground, a plot at a time over the square: {plots looked at, those in a district the map holds, those the plan's input (CityMap.planInputHere(... |
| 1871 | 12 | `static long landStampOfSidecar(byte[] deflated) throws java.io.IOException` | The land stamp a sidecar's header carries (after the magic, the format, the world, the month, its own stamp and the site). |
| 1885 | 16 | `static int[] worksOnField(CityMap m, Deposit field, Resource r)` | The sites of a field as the map lists them (CityMap.siteList()), and how many carry a works: {sites listed, sites with a works}. |
| 1903 | 6 | `static Landmasses.Picture linkedWith(Landmasses.Picture p, int id)` | The picture with a landmass added to its linked set and its stamp moved: what a link makes of it, handed to a map by hand. |
| 1910 | 350 | `static void theMap(Fixtures f) throws Exception` |  |

### 11. THE WORDS (lines 2261-2586)

| line | len | member | says |
|---:|---:|---|---|
| 2266 | 1 | `static String km2Words(double sqFt)` | A km2 figure as the report line writes it. |
| 2269 | 3 | `static boolean islandPort(City c, BuildingsTemplate gc)` | The landmass kinds of terminal ordered by the card's path: the general cargo terminal, on the island. |
| 2273 | 313 | `static void theWords(Fixtures f)` |  |

### 12. GROWN ACROSS LANDMASSES (lines 2587-2792)

| line | len | member | says |
|---:|---:|---|---|
| 2596 | 56 | `static String audit(Game g)` | The model's books of the terminals and the ground, audited at one look: the linked free ground fits the linked owned ground less the linked built ground; the ground the unlinked landmasses' terminals hold is what the ... |
| 2654 | 5 | `static int buildingsOf(Game g)` | The buildings standing, every template's. |
| 2660 | 132 | `static void grown(Fixtures f)` |  |

### 13. THE FULL FILE (lines 2793-2915)

| line | len | member | says |
|---:|---:|---|---|
| 2798 | 13 | `static EarthData theEarth()` | The Earth's data where it is named or lies at its place, or null - and the one line saying so. |
| 2815 | 74 | `static void fullFile(EarthData file)` |  |
| 2890 | 17 | `private static void look(Game g, int[] looks, String[] first)` |  |
| 2909 | 6 | `static double[] dryArea(double km2)` | The five areas of an offer of this many square kilometres of dry ground. |


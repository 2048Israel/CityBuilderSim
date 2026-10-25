# LandmassCheck.java - 1,913 lines · 67 methods · 10 constants · harnesses

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
>  12. GROWN ACROSS LANDMASSES. A city that owns the island for 24 months, then the two terminals by the card's path,
>      then 96 months: every month the books are consistent (the linked free ground fits, the unlinked terminals' ground
>      is the placements', the placements are the buildings' counts, and no works stands on a field across the water: getSites() is
>      the pieces on linked ground, counted apart, and the works standing fit them) - the 24 months of a city that grows, and of a
>      city on the island itself that owns the mainland's field of iron across the water.
>  13. THE FULL FILE. The delta's islands read Rotterdam's landmass in the model's reader, Goeree does not; PEI is apart,
>      Halifax and Dartmouth one, Medellin on 2; Rotterdam played 1,200 months, the books audited at every look.
> 
> Every premise is asserted before what rests on it. The full-file section prints one line and skips without
> ... (1 more lines in the source)

**Uses:** [World](World.md) (123), [Landmasses](Landmasses.md) (122), [Game](Game.md) (81), [Resource](Resource.md) (35), [LandParcel](LandParcel.md) (30), [BuildingsTemplate](BuildingsTemplate.md) (28), [LandManager](LandManager.md) (26), [LongPlaytest](LongPlaytest.md) (25), [EarthData](EarthData.md) (14), [CityLand](CityLand.md) (13), [LandMarket](LandMarket.md) (10), [EarthTerrain](EarthTerrain.md) (9), [LandmassView](LandmassView.md) (9), [Ports](Ports.md) (9), [Founding](Founding.md) (8), [Deposit](Deposit.md) (8), [GameFiles](GameFiles.md) (7), [ConstructionControl](ConstructionControl.md) (7), [EarthSite](EarthSite.md) (6), [EarthCheck](EarthCheck.md) (5), [AutoBuilder](AutoBuilder.md) (5), [GridOffers](GridOffers.md) (5), [Zoning](Zoning.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [ReloadCheck](ReloadCheck.md) (2), [EarthProj](EarthProj.md) (1), [LandGrid](LandGrid.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [GameVersion](GameVersion.md) (1), [SaveFileCheck](SaveFileCheck.md) (1)... and 2 more

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 124 | THE FIXTURES |
| 212 | MAIN |
| 247 | 0. A GENERATED WORLD HAS NO LANDMASSES |
| 326 | 1. THE READER |
| 459 | THE FIXTURE CITIES |
| 503 | 2. THE BOOKS BY LANDMASS |
| 729 | 3. FREE GROUND IS LINKED ONLY |
| 801 | 4. ONLY A PORT ON UNLINKED GROUND |
| 913 | 8. THE PICKERS |
| 1023 | 4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND |
| 1126 | 5. TWO PORTS LINK |
| 1354 | 6. DEMOLITION |
| 1433 | 7. THE CHOICE |
| 1537 | 9. THE SAVE |
| 1642 | 12. GROWN ACROSS LANDMASSES |
| 1791 | 13. THE FULL FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `LandmassCheck.out` | `System.out` |  |
| 85 | `LandmassCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 88 | `LandmassCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 91 | `LandmassCheck.FREE_SLACK` | `1.0` | Two square-foot figures of one ground are the same within a square foot (the two landmass counts and the land's sum differ in the last bits of a double). |
| 94 | `LandmassCheck.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The area of a plot, square feet, as the books reckon it. |
| 97 | `LandmassCheck.SQUARE` | `64` | The side of an island square, in plots. |
| 100 | `LandmassCheck.RLAT` | `51.90625, RLON = 4.48125` | Rotterdam, the fixture's window: where the city is founded. |
| 103 | `LandmassCheck.ON_THE_ISLAND` | `{ { 51.77, 4.15 }, { 51.75, 4.20 }, { 51.80, 4.10 } }` | Where a city on Goeree-Overflakkee is founded (the mainland is then across the water): candidate spots, the first that serves. |
| 1175 | `LandmassCheck.GSON` | `new com.google.gson.Gson()` |  |
| 1811 | `LandmassCheck.RUN_MONTHS` | `1200` | The months the Rotterdam run plays. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `static int fails` |  |
| 130 | `final Game g` |  |
| 131 | `final GameFiles files` |  |
| 132 | `final LandManager lm` |  |
| 133 | `final World w` |  |
| 134 | `final World.Key key` |  |
| 277 | `static int coastChecked` | How many sea terminals and desalination plants the last oldExpressions() asked hasCoastFor() about (the five terminals and the plants). |
| 465 | `World.Key key` |  |
| 466 | `City a, b` |  |
| 467 | `Square withShore, withoutShore, homeSalt` |  |
| 468 | `LandParcel island` |  |
| 469 | `boolean ok` |  |
| 509 | `final long[] relief` |  |
| 510 | `long forest, shore` |  |
| 511 | `int firstHolding` |  |
| 828 | `City city` |  |
| 829 | `FieldAt away, home` |  |
| 830 | `double[] spot` |  |
| 1439 | `int standing, onSite, shell` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 1833 | **type** `public class LandmassCheck` | The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city h... |
| 105 | 4 | `static void check(String label, boolean ok)` |  |
| 110 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 115 | 5 | `static void quietly(Runnable r)` |  |
| 122 | 1 | `static boolean near(double a, double b)` | Two square-foot figures within FREE_SLACK. |

### THE FIXTURES (lines 124-211)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 29 | **type** `static final class City` | A city on a fixture world: its game, its land office and its world. |
| 136 | 12 | `City(String label, World.Key key, int houses)` _(in LandmassCheck.City)_ |  |
| 149 | 1 | `Landmasses books()` _(in LandmassCheck.City)_ |  |
| 152 | 5 | `LandParcel buy(Square s)` _(in LandmassCheck.City)_ | Buys a square (as an offer of it would be, at no price) and returns the parcel. |
| 160 | 1 | **type** `record Square(long x0, long y0, int id, long onId, long dry, long salt, long shore, boolean pure)` | A square of SQUARE x SQUARE plots, measured by the model's own reader: what lies on a landmass in it. |
| 163 | 17 | `static Square measure(World w, long x0, long y0, int id)` | Measures the square at (x0, y0) for landmass `id`: its dry plots on it, its salt, its shore (a flat or foothill dry plot of it beside salt in the square) and whether every dry plot of it is `id`'s. |
| 181 | 3 | `private static boolean saltIn(World w, long x0, long y0, long x, long y)` |  |
| 190 | 14 | `static Square squareWith(World w, int id, long sx, long sy, int tiles, Predicate<Square> serves)` | The nearest square (tile-aligned, SQUARE plots a side) to (sx, sy) that serves, searched out to `tiles` tiles each way with the model's reader and bounded - a FAIL for the caller, never a hang. |
| 206 | 5 | `static long[] plotOf(World w, double lat, double lon)` | The plot of a latitude and longitude on a world. |

### MAIN (lines 212-246)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 30 | `public static void main(String[] args) throws Exception` |  |

### 0. A GENERATED WORLD HAS NO LANDMASSES (lines 247-325)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 23 | `static boolean oldExpressions(String what, LandManager lm, Game g)` | The old expressions of the free ground, written out from the public getters: what a city with one landmass reads. |
| 279 | 46 | `static void generatedWorld()` |  |

### 1. THE READER (lines 326-458)

| line | len | member | says |
|---:|---:|---|---|
| 331 | 23 | `static int bruteResolved(EarthData d, double lat, double lon)` | The nearest land cell's id by a loop over the data's own landmassAt(): the cell holding the point, or the least dr squared + dc squared within LANDMASS_REACH, the lowest id on a tie, 0 for none. |
| 355 | 103 | `static void theReader(EarthData d, World.Key key)` |  |

### THE FIXTURE CITIES (lines 459-502)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 7 | **type** `static final class Fixtures` | The squares and cities sections 2 to 8 share. |
| 472 | 30 | `static Fixtures fixtures(World.Key key)` |  |

### 2. THE BOOKS BY LANDMASS (lines 503-728)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 6 | **type** `static final class Brute` | One landmass's owned ground counted plot by plot over the box of the city's ground. |
| 512 | 1 | `long dry()` _(in LandmassCheck.Brute)_ |  |
| 515 | 21 | `static TreeMap<Integer, Brute> bruteBooks(World w, CityLand land, int home)` |  |
| 537 | 3 | `private static boolean ownedSalt(World w, CityLand land, long x, long y)` |  |
| 541 | 80 | `static void theBooks(Fixtures f)` |  |
| 623 | 10 | `static boolean sameAsBrute(List<Landmasses.Row> rows, TreeMap<Integer, Brute> brute)` | Whether the rows are a brute-force count's: each landmass's dry plots by relief, forest, shore and (off home) first holding. |
| 638 | 19 | `static void settledShore(Fixtures f)` | A block of home's coast bought beside the island: its interior tiles are wholly owned with wholly owned neighbours, so their shore can never change and the next recount adds it without reading them - and the count is ... |
| 659 | 1 | **type** `record EdgePair(long ax, long ay, long bx, long by)` | Two plots across a tile's edge: a dry flat or foothill plot of home's at the edge of its tile, and the salt plot in the tile beside it. |
| 665 | 18 | `static EdgePair edgePair(World w, int home, long sx, long sy, int tiles, int dx, int dy)` | The nearest pair of plots to (sx, sy), searched out `tiles` tiles each way with the model's reader and bounded: a dry flat or foothill plot of home's on the (dx, dy) side of its tile, with a salt plot straight across ... |
| 690 | 32 | `static void shoreAcrossTileEdges(Fixtures f)` | The shore at a tile's edge: a dry plot on the edge of its tile with owned salt across it, in the tile beside, is shore though its own tile holds no owned salt. |
| 723 | 5 | `static String describe(List<Landmasses.Row> rows)` |  |

### 3. FREE GROUND IS LINKED ONLY (lines 729-800)

| line | len | member | says |
|---:|---:|---|---|
| 733 | 67 | `static void freeGroundIsLinked(Fixtures f)` |  |

### 4. ONLY A PORT ON UNLINKED GROUND (lines 801-912)

| line | len | member | says |
|---:|---:|---|---|
| 806 | 1 | **type** `record FieldAt(Resource resource, Deposit field, int landmass, double km)` | A field of a sited resource centred on a dry plot of a landmass but the city's home, nearest to the site, found by search. |
| 808 | 17 | `static FieldAt nearestField(World w, Resource r, int notLandmass, boolean offHome)` |  |
| 827 | 5 | **type** `static final class IslandCity` | The city on the island whose mainland holds a field of iron: found by search over the candidate spots. |
| 833 | 17 | `static IslandCity onTheIsland(String id)` |  |
| 852 | 5 | `static LandParcel buyAround(City c, long x, long y)` | Buys a square round a plot (as an offer would be) on a city; the parcel. |
| 858 | 54 | `static void onlyAPort(Fixtures f, String dataId)` |  |

### 8. THE PICKERS (lines 913-1022)

| line | len | member | says |
|---:|---:|---|---|
| 917 | 105 | `static void thePickers(Fixtures f, String dataId)` |  |

### 4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND (lines 1023-1125)

| line | len | member | says |
|---:|---:|---|---|
| 1028 | 19 | `static FieldAt lakeCentred(World w, int home, long[] near)` | A field of a sited resource centred on a water plot that is not the sea's (a lake, a river), whose 15" cell the layer puts on a landmass but home's: the nearest to a plot, found by search over the cells round it. |
| 1052 | 67 | `static void lakeCentredField(Fixtures f)` | The review of slice A, (b): the works gate reads a field's landmass from its centre plot's 15" cell resolved by the D2 rule, not from the plot, so a field centred on a lake plot of an island (landmassAt() gives a wate... |
| 1121 | 4 | `static CityLand.Piece pieceAt(Resource r, long x, long y, int sites)` | A piece of the ground holding a made-up field of `sites` sites centred on plot (x, y): what the works gate is asked of. |

### 5. TWO PORTS LINK (lines 1126-1353)

| line | len | member | says |
|---:|---:|---|---|
| 1130 | 5 | `static BuildingsTemplate tpl(City c, String name)` |  |
| 1137 | 1 | `static BuildingsTemplate own(City c, BuildingsTemplate t)` | A template as this city has it (a city's templates are its own; another city's object is never handed to its buildings). |
| 1139 | 1 | `static int standing(City c, BuildingsTemplate t)` |  |
| 1141 | 4 | `static int onSite(City c, BuildingsTemplate t)` |  |
| 1147 | 4 | `static boolean until(City c, java.util.function.BooleanSupplier done, int most)` | Months played quietly until `done` holds, at most `most`; whether it does. |
| 1153 | 5 | `static Game.BuildResult order(City c, BuildingsTemplate t, int n, boolean instant, int asked)` | An order, quietly: `asked` is Landmasses.CHOSEN, RULE or a landmass's id. |
| 1160 | 4 | `static Game.BuildResult orderOn(City c, BuildingsTemplate t, int n, boolean instant, int landmass)` | An order of a terminal for a landmass by the card's path: the pick, then the order. |
| 1166 | 8 | `static City portCity(Fixtures f, String label, boolean homeShore)` | A city of forty houses that owns the island square (shore on it) and, when asked, a stretch of home's coast. |
| 1178 | 1 | `static String stateOf(City c)` | The saved placements as JSON: what the save writes under "landmasses" (null for none). |
| 1181 | 15 | `static Square thirdLandmass(World w, EarthData d)` | The third landmass for the closure's third leg: the first cell of a landmass in the tile west of Goeree, and a square about it with dry ground and shore. |
| 1197 | 156 | `static void twoPortsLink(Fixtures f, EarthData data)` |  |

### 6. DEMOLITION (lines 1354-1432)

| line | len | member | says |
|---:|---:|---|---|
| 1358 | 74 | `static void demolition(Fixtures f)` |  |

### 7. THE CHOICE (lines 1433-1536)

| line | len | member | says |
|---:|---:|---|---|
| 1438 | 11 | **type** `static final class StubStock implements Landmasses.Stock` | What Landmasses reads of the buildings, for a unit test of the rule: one terminal template and fixed counts. |
| 1440 | 1 | `public int[] portTemplates()` _(in LandmassCheck.StubStock)_ |  |
| 1441 | 1 | `public boolean known(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1442 | 1 | `public int standing(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1443 | 1 | `public int onSite(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1444 | 1 | `public int shell(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1445 | 1 | `public List<int[]> demolitions(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1446 | 1 | `public double landSqFt(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1447 | 1 | `public int cargo(int template)` _(in LandmassCheck.StubStock)_ |  |
| 1450 | 86 | `static void theChoice(Fixtures f)` |  |

### 9. THE SAVE (lines 1537-1641)

| line | len | member | says |
|---:|---:|---|---|
| 1541 | 75 | `static void theSave(Fixtures f) throws Exception` |  |
| 1618 | 12 | `static void diffPaths(String path, com.google.gson.JsonElement a, com.google.gson.JsonElement b, java.util.List<String> out, in...` | The paths at which two saves' JSON differ, down to the leaf (at most `most`): what jsonDiff() names only by its top key. |
| 1631 | 4 | `static void assertSaved(City c, int slot)` |  |
| 1636 | 5 | `static GameFiles.Result quietlyResult(java.util.function.Supplier<GameFiles.Result> s)` |  |

### 12. GROWN ACROSS LANDMASSES (lines 1642-1790)

| line | len | member | says |
|---:|---:|---|---|
| 1651 | 56 | `static String audit(Game g)` | The model's books of the terminals and the ground, audited at one look: the linked free ground fits the linked owned ground less the linked built ground; the ground the unlinked landmasses' terminals hold is what the ... |
| 1709 | 5 | `static int buildingsOf(Game g)` | The buildings standing, every template's. |
| 1715 | 75 | `static void grown(Fixtures f)` |  |

### 13. THE FULL FILE (lines 1791-1913)

| line | len | member | says |
|---:|---:|---|---|
| 1796 | 13 | `static EarthData theEarth()` | The Earth's data where it is named or lies at its place, or null - and the one line saying so. |
| 1813 | 74 | `static void fullFile(EarthData file)` |  |
| 1888 | 17 | `private static void look(Game g, int[] looks, String[] first)` |  |
| 1907 | 6 | `static double[] dryArea(double km2)` | The five areas of an offer of this many square kilometres of dry ground. |


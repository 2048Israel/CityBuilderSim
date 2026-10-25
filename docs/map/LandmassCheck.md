# LandmassCheck.java - 975 lines · 33 methods · 8 constants · harnesses

`ham/citybuildersim/LandmassCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The city's ground counted by landmass, and the island rule, checked (ET5, slice A; the ET5 design): a landmass is read
> from the Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city has not linked
> is owned, counted and priced as ever but is no room for anyone, and no works stands on its fields.
> 
> WHY THIS EXISTS. Until ET5 the model's ground was one pool with no place in it: an island bought across a strait was
> room for any building, and the Rotterdam city could "build" on Goeree-Overflakkee from its own streets. Nothing could
> say so, because nothing read a plot's landmass. This holds the reader, the books and the rule against fixtures that
> CAUSE each condition (an island square found by search with the model's own reader, bought through the harness door
> LandManager.buyRectForTest(); a city founded on the island, so that the mainland is across the water), and every
> figure worked out again from the model's constants and getters, never typed.
> 
> WHAT IT HAS TO PROVE (this slice: sections 0 to 4 and 8; the ports, the save, the map and the words are the other slices'):
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
>      a works on a field centred on an unlinked plot is NO_DEPOSIT and the field is in no site count; the same field at
>      home is a deposit. (Section 4's coast assertions are the ports slice's.)
>   8. THE PICKERS. An all-island offer has no buildable ground and is never the best for room, a shortfall or a
>      deposit; ISLAND is tagged by the share; a shortfall only the island's dry ground would cover buys no island.
> 
> Every premise is asserted before what rests on it. Each full-file section prints one line and skips without
> -Dcbs.earth (none here: the fixture holds Rotterdam's two landmasses).

**Uses:** [World](World.md) (103), [Landmasses](Landmasses.md) (34), [LandParcel](LandParcel.md) (25), [Resource](Resource.md) (24), [LandManager](LandManager.md) (22), [Game](Game.md) (17), [LandMarket](LandMarket.md) (10), [CityLand](CityLand.md) (9), [EarthTerrain](EarthTerrain.md) (8), [LandmassView](LandmassView.md) (8), [Founding](Founding.md) (6), [EarthData](EarthData.md) (6), [EarthCheck](EarthCheck.md) (5), [AutoBuilder](AutoBuilder.md) (5), [Deposit](Deposit.md) (5), [GridOffers](GridOffers.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [EarthSite](EarthSite.md) (4), [GameFiles](GameFiles.md) (2), [Zoning](Zoning.md) (2), [EarthProj](EarthProj.md) (1), [LandGrid](LandGrid.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 99 | THE FIXTURES |
| 185 | MAIN |
| 213 | 0. A GENERATED WORLD HAS NO LANDMASSES |
| 292 | 1. THE READER |
| 425 | THE FIXTURE CITIES |
| 469 | 2. THE BOOKS BY LANDMASS |
| 695 | 3. FREE GROUND IS LINKED ONLY |
| 769 | 4. ONLY A PORT ON UNLINKED GROUND |
| 881 | 8. THE PICKERS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `LandmassCheck.out` | `System.out` |  |
| 60 | `LandmassCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 63 | `LandmassCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 66 | `LandmassCheck.FREE_SLACK` | `1.0` | Two square-foot figures of one ground are the same within a square foot (the two landmass counts and the land's sum differ in the last bits of a double). |
| 69 | `LandmassCheck.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The area of a plot, square feet, as the books reckon it. |
| 72 | `LandmassCheck.SQUARE` | `64` | The side of an island square, in plots. |
| 75 | `LandmassCheck.RLAT` | `51.90625, RLON = 4.48125` | Rotterdam, the fixture's window: where the city is founded. |
| 78 | `LandmassCheck.ON_THE_ISLAND` | `{ { 51.77, 4.15 }, { 51.75, 4.20 }, { 51.80, 4.10 } }` | Where a city on Goeree-Overflakkee is founded (the mainland is then across the water): candidate spots, the first that serves. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |
| 105 | `final Game g` |  |
| 106 | `final LandManager lm` |  |
| 107 | `final World w` |  |
| 108 | `final World.Key key` |  |
| 243 | `static int coastChecked` | How many sea terminals and desalination plants the last oldExpressions() asked hasCoastFor() about (the five terminals and the plants). |
| 431 | `World.Key key` |  |
| 432 | `City a, b` |  |
| 433 | `Square withShore, withoutShore, homeSalt` |  |
| 434 | `LandParcel island` |  |
| 435 | `boolean ok` |  |
| 475 | `final long[] relief` |  |
| 476 | `long forest, shore` |  |
| 477 | `int firstHolding` |  |
| 796 | `City city` |  |
| 797 | `FieldAt away, home` |  |
| 798 | `double[] spot` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 920 | **type** `public class LandmassCheck` | The city's ground counted by landmass, and the island rule, checked (ET5, slice A; the ET5 design): a landmass is read from the Earth's data at every plot, the city's books are kept by landmass, ground on a landmass t... |
| 80 | 4 | `static void check(String label, boolean ok)` |  |
| 85 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 90 | 5 | `static void quietly(Runnable r)` |  |
| 97 | 1 | `static boolean near(double a, double b)` | Two square-foot figures within FREE_SLACK. |

### THE FIXTURES (lines 99-184)

| line | len | member | says |
|---:|---:|---|---|
| 104 | 27 | **type** `static final class City` | A city on a fixture world: its game, its land office and its world. |
| 110 | 11 | `City(String label, World.Key key, int houses)` _(in LandmassCheck.City)_ |  |
| 122 | 1 | `Landmasses books()` _(in LandmassCheck.City)_ |  |
| 125 | 5 | `LandParcel buy(Square s)` _(in LandmassCheck.City)_ | Buys a square (as an offer of it would be, at no price) and returns the parcel. |
| 133 | 1 | **type** `record Square(long x0, long y0, int id, long onId, long dry, long salt, long shore, boolean pure)` | A square of SQUARE x SQUARE plots, measured by the model's own reader: what lies on a landmass in it. |
| 136 | 17 | `static Square measure(World w, long x0, long y0, int id)` | Measures the square at (x0, y0) for landmass `id`: its dry plots on it, its salt, its shore (a flat or foothill dry plot of it beside salt in the square) and whether every dry plot of it is `id`'s. |
| 154 | 3 | `private static boolean saltIn(World w, long x0, long y0, long x, long y)` |  |
| 163 | 14 | `static Square squareWith(World w, int id, long sx, long sy, int tiles, Predicate<Square> serves)` | The nearest square (tile-aligned, SQUARE plots a side) to (sx, sy) that serves, searched out to `tiles` tiles each way with the model's reader and bounded - a FAIL for the caller, never a hang. |
| 179 | 5 | `static long[] plotOf(World w, double lat, double lon)` | The plot of a latitude and longitude on a world. |

### MAIN (lines 185-212)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 23 | `public static void main(String[] args) throws Exception` |  |

### 0. A GENERATED WORLD HAS NO LANDMASSES (lines 213-291)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 23 | `static boolean oldExpressions(String what, LandManager lm, Game g)` | The old expressions of the free ground, written out from the public getters: what a city with one landmass reads. |
| 245 | 46 | `static void generatedWorld()` |  |

### 1. THE READER (lines 292-424)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 23 | `static int bruteResolved(EarthData d, double lat, double lon)` | The nearest land cell's id by a loop over the data's own landmassAt(): the cell holding the point, or the least dr squared + dc squared within LANDMASS_REACH, the lowest id on a tie, 0 for none. |
| 321 | 103 | `static void theReader(EarthData d, World.Key key)` |  |

### THE FIXTURE CITIES (lines 425-468)

| line | len | member | says |
|---:|---:|---|---|
| 430 | 7 | **type** `static final class Fixtures` | The squares and cities sections 2 to 8 share. |
| 438 | 30 | `static Fixtures fixtures(World.Key key)` |  |

### 2. THE BOOKS BY LANDMASS (lines 469-694)

| line | len | member | says |
|---:|---:|---|---|
| 474 | 6 | **type** `static final class Brute` | One landmass's owned ground counted plot by plot over the box of the city's ground. |
| 478 | 1 | `long dry()` _(in LandmassCheck.Brute)_ |  |
| 481 | 21 | `static TreeMap<Integer, Brute> bruteBooks(World w, CityLand land, int home)` |  |
| 503 | 3 | `private static boolean ownedSalt(World w, CityLand land, long x, long y)` |  |
| 507 | 80 | `static void theBooks(Fixtures f)` |  |
| 589 | 10 | `static boolean sameAsBrute(List<Landmasses.Row> rows, TreeMap<Integer, Brute> brute)` | Whether the rows are a brute-force count's: each landmass's dry plots by relief, forest, shore and (off home) first holding. |
| 604 | 19 | `static void settledShore(Fixtures f)` | A block of home's coast bought beside the island: its interior tiles are wholly owned with wholly owned neighbours, so their shore can never change and the next recount adds it without reading them - and the count is ... |
| 625 | 1 | **type** `record EdgePair(long ax, long ay, long bx, long by)` | Two plots across a tile's edge: a dry flat or foothill plot of home's at the edge of its tile, and the salt plot in the tile beside it. |
| 631 | 18 | `static EdgePair edgePair(World w, int home, long sx, long sy, int tiles, int dx, int dy)` | The nearest pair of plots to (sx, sy), searched out `tiles` tiles each way with the model's reader and bounded: a dry flat or foothill plot of home's on the (dx, dy) side of its tile, with a salt plot straight across ... |
| 656 | 32 | `static void shoreAcrossTileEdges(Fixtures f)` | The shore at a tile's edge: a dry plot on the edge of its tile with owned salt across it, in the tile beside, is shore though its own tile holds no owned salt. |
| 689 | 5 | `static String describe(List<Landmasses.Row> rows)` |  |

### 3. FREE GROUND IS LINKED ONLY (lines 695-768)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 69 | `static void freeGroundIsLinked(Fixtures f)` |  |

### 4. ONLY A PORT ON UNLINKED GROUND (lines 769-880)

| line | len | member | says |
|---:|---:|---|---|
| 774 | 1 | **type** `record FieldAt(Resource resource, Deposit field, int landmass, double km)` | A field of a sited resource centred on a dry plot of a landmass but the city's home, nearest to the site, found by search. |
| 776 | 17 | `static FieldAt nearestField(World w, Resource r, int notLandmass, boolean offHome)` |  |
| 795 | 5 | **type** `static final class IslandCity` | The city on the island whose mainland holds a field of iron: found by search over the candidate spots. |
| 801 | 17 | `static IslandCity onTheIsland(String id)` |  |
| 820 | 5 | `static LandParcel buyAround(City c, long x, long y)` | Buys a square round a plot (as an offer would be) on a city; the parcel. |
| 826 | 54 | `static void onlyAPort(Fixtures f, String dataId)` |  |

### 8. THE PICKERS (lines 881-975)

| line | len | member | says |
|---:|---:|---|---|
| 885 | 82 | `static void thePickers(Fixtures f, String dataId)` |  |
| 969 | 6 | `static double[] dryArea(double km2)` | The five areas of an offer of this many square kilometres of dry ground. |


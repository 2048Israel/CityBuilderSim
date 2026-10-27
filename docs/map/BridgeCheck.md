# BridgeCheck.java - 967 lines · 39 methods · 8 constants · harnesses

`ham/citybuildersim/BridgeCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> The long bridges checked (ET6, 2026-10-26; docs/queue/ET6-notes.md): the survey of a crossing on grids of its own making and
> on the Earth, what a span costs, the order in a played city on two landmasses, the bridge that links, the bridge nobody
> can take down, the save, a generated world that has no far end, and the real strait. Not part of the game.
> 
> WHY. Until ET6 a landmass was linked by two ports and nothing else, and a bridge was the map's affair alone: a highway
> crossed 1,020 m of fresh or shallow water because CityRuns drew it so. A long bridge is the model's: an order between two
> landmasses that the model finds a crossing for, prices by length and depth, builds as one contract and then links. Every
> rule of that is caused here on ground a fixture lays out plot by plot (GridGround: the survey is a function of a small
> interface, so the water, the banks, the depths and the ownership are the harness's), from both sides of each threshold
> and against the model's own constants, never numbers typed.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SURVEY. A straight SHALLOW channel W plots wide between the city's own shore and an owned island gives the chain
>      the step arithmetic says, (W + 1) steps of 30 m and two corner plots; a 45-degree channel likewise, in diagonal steps;
>      fresh and shallow water mixed is one chain; one deep plot in the only channel is refusal 1, with that plot's depth and
>      its distance from the far shore; a deep plot the chain can go round gives the longer detour; a near shore of steep or
>      unowned ground, or ground on a landmass no one has linked, is no landing (and foothill is); refusal 2 both ways (no owned
>      ground on the far landmass, and owned ground that touches no water); refusal 3 both ways (found, but over the cap, at the
>      cap's own plot; and cut by the search window); refusal 4 on one landmass; the corner list replays to the chain; the
>      words of all four are the card's, word for word. The worst case, a bay as wide as a bridge can be, is timed.
>   2. THE PRICE. The deck rate is the base rate in the shallows and on fresh water, the Confederation Bridge's derived rate at
>      its 35 m, linear between and beyond and never under the base; a metre costs more in rail than in highway than in road at
>      every depth, and more in deeper water; a span is its steps' lengths at the rate of their ends' depths; points are
>      dollars over the thousands the file counts in and the founding price a point. The three records of the notes are printed.
>   3. THE ORDER, in a played city on two landmasses (Rotterdam with Goeree-Overflakkee's square, the fixture of LandmassCheck;
>      the crossing is handed to the survey through Game.setBridgeGroundForTest(), because the strait the fixture holds
>      between the two squares is not a crossing the harness can cause): with no path the order is NO_BRIDGE before land and
>      money, no bond offered, nothing moved; the quote's span points are BridgeWorks' arithmetic at the template's founding
>      price; ordered, the island stays unlinked every month the bridge is on site, which takes the builders months; stood,
>      the island is linked, its free ground counts as room and the linked set's version moved; an order of two is two spans
>      on one survey.
>   4. NO DEMOLITION. A bridge that stands cannot be demolished (demolishable() 0, the words), and the island is still linked.
>   5. THE SAVE. A standing bridge and one on site survive a save and a load with the island linked, the resave is the same
>      bytes but for its time and name, the key is absent with no bridge, and a save with the key stripped loads with none.
>   6. A GENERATED WORLD. Every bridge card refuses with refusal 4 and nothing else in the city moves (a month played with and
>      without asking is the same).
>   7. THE FULL FILE, when earth-2.bin is named (-Dcbs.earth=<path>): a city at Borden-Carleton, Prince Edward Island, that buys
>      Cape Jourimain, New Brunswick, across the Northumberland Strait is surveyed - its length against the real 12.9 km, its
>      depths, or the refusal and why - and so is the Rotterdam delta's strait to Goeree-Overflakkee; the survey answers inside
>      SURVEY_BUDGET_MS.
> 
> Every premise is asserted before what rests on it.

**Uses:** [BridgeWorks](BridgeWorks.md) (105), [World](World.md) (101), [BridgeSurvey](BridgeSurvey.md) (83), [LandmassCheck](LandmassCheck.md) (83), [Game](Game.md) (35), [Bridges](Bridges.md) (27), [BridgeView](BridgeView.md) (23), [Landmasses](Landmasses.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (10), [BuildCard](BuildCard.md) (9), [EarthData](EarthData.md) (8), [Founding](Founding.md) (5), [EarthCheck](EarthCheck.md) (3), [LandManager](LandManager.md) (3), [GameFiles](GameFiles.md) (3), [BuildAdvice](BuildAdvice.md) (3), [EarthTerrain](EarthTerrain.md) (2), [RunWorks](RunWorks.md) (2), [CityMap](CityMap.md) (2), [ConstructionControl](ConstructionControl.md) (2), [ReloadCheck](ReloadCheck.md) (2), [EarthSite](EarthSite.md) (2), [BuildingType](BuildingType.md) (1), [GameVersion](GameVersion.md) (1), [SaveFileCheck](SaveFileCheck.md) (1)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 97 | THE GROUND - a grid of the harness's own making |
| 263 | MAIN |
| 295 | 1. THE SURVEY |
| 465 | 2. THE PRICE |
| 533 | THE PLAYED CITY - Rotterdam with Goeree-Overflakkee's square, two landmasses |
| 574 | 3. THE ORDER |
| 750 | 4. NO DEMOLITION |
| 773 | 5. THE SAVE |
| 843 | 6. A GENERATED WORLD |
| 904 | 7. THE FULL FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `BridgeCheck.out` | `System.out` |  |
| 62 | `BridgeCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 65 | `BridgeCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 73 | `BridgeCheck.SURVEY_BUDGET_MS` | `10_000` | How long a survey may take on the Earth, milliseconds: 10,000. |
| 76 | `BridgeCheck.BAY_BUDGET_MS` | `10_000` | The worst case of a synthetic bay a bridge can be as long as, milliseconds: the survey of 1.7 million plots of open shallow water. |
| 102 | `BridgeCheck.DEEP_M` | `100` | The depth the grid gives a plot nobody set: deep sea. |
| 200 | `BridgeCheck.ISLAND` | `2, HOME = 1` | The far island's landmass id and home's, in every grid here. |
| 909 | `BridgeCheck.SHORE_SQUARE` | `128` | Plots a side of the squares the full-file crossings buy at each shore. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 109 | `final int home` |  |
| 110 | `int[] linked` |  |
| 111 | `final Map<Long, BridgeSurvey.Tile> tiles` |  |
| 112 | `final Map<Long, double[]> depths` |  |
| 113 | `long tileReads` |  |
| 222 | `final Landmasses lms` |  |
| 748 | `static LandmassCheck.City bridgeCityForSection4` | The city section 3 played, for section 4. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 910 | **type** `public class BridgeCheck` | The long bridges checked (ET6, 2026-10-26; docs/queue/ET6-notes.md): the survey of a crossing on grids of its own making and on the Earth, what a span costs, the order in a played city on two landmasses, the bridge th... |
| 78 | 4 | `static void check(String label, boolean ok)` |  |
| 83 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 89 | 1 | `static boolean same(double a, double b)` | Two figures are one within a hundredth of a metre or a part in a billion. |
| 91 | 5 | `static void quietly(Runnable r)` |  |

### THE GROUND - a grid of the harness's own making (lines 97-262)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 90 | **type** `static class GridGround implements BridgeSurvey.Ground` | A BridgeSurvey.Ground laid out plot by plot: what is not set is deep sea, unowned. |
| 115 | 4 | `GridGround(int home, int...linked)` _(in BridgeCheck.GridGround)_ |  |
| 120 | 13 | `private BridgeSurvey.Tile tileOf(long x, long y, boolean make)` _(in BridgeCheck.GridGround)_ |  |
| 135 | 11 | `GridGround plot(long x, long y, byte cover, byte relief, int landmass, boolean owned, double depth)` _(in BridgeCheck.GridGround)_ | Sets a plot. |
| 148 | 4 | `GridGround dry(long x0, long y0, long x1, long y1, int landmass, byte relief, boolean owned)` _(in BridgeCheck.GridGround)_ | Owned or unowned dry ground of a landmass and relief over [x0, x1) x [y0, y1). |
| 154 | 4 | `GridGround water(long x0, long y0, long x1, long y1, byte cover, double depth)` _(in BridgeCheck.GridGround)_ | Water over [x0, x1) x [y0, y1): FRESH (depth 0), SHALLOW or SALT, `depth` metres. |
| 160 | 4 | `byte coverAt(long x, long y)` _(in BridgeCheck.GridGround)_ | The cover of a plot as set (SALT where nobody did). |
| 165 | 1 | `public int home()` _(in BridgeCheck.GridGround)_ |  |
| 167 | 1 | `public int[] linkedIds()` _(in BridgeCheck.GridGround)_ |  |
| 169 | 10 | `public long[] ownedTiles()` _(in BridgeCheck.GridGround)_ |  |
| 180 | 11 | `public void tile(long tx, long ty, BridgeSurvey.Tile out)` _(in BridgeCheck.GridGround)_ |  |
| 192 | 5 | `public double depthM(long x, long y)` _(in BridgeCheck.GridGround)_ |  |
| 206 | 3 | `static GridGround channel(int w, int rows, byte cover, double depth)` | A straight channel of `w` plots along x between the island (west) and home (east), `rows` rows wide (y from 0), banks of dry unowned ground on its north and south so that the water is the only way across. |
| 211 | 8 | `static GridGround channelInto(GridGround g, int homeId, int islandId, int w, int rows, byte cover, double depth)` | ...laid on a ground already made, with the landmasses' ids as that ground's game has them. |
| 221 | 12 | **type** `static final class LiveGround extends GridGround` | The ground the played city's survey is handed (Game.setBridgeGroundForTest()): the harness's terrain and ownership, the game's own home and linked set, read live. |
| 224 | 4 | `LiveGround(Landmasses lms)` _(in BridgeCheck.LiveGround)_ |  |
| 229 | 1 | `public int home()` _(in BridgeCheck.LiveGround)_ |  |
| 231 | 1 | `public int[] linkedIds()` _(in BridgeCheck.LiveGround)_ |  |
| 235 | 14 | `static List<long[]> replay(long[] corners)` | The chain a result's corner list names, plot by plot: each leg walked in its direction one plot at a time. |
| 251 | 8 | `static double lengthOf(List<long[]> path)` | The length of a replayed chain in metres: 30 a step along a row or column, 30 x sqrt(2) a diagonal. |
| 261 | 1 | `static String namer(int id)` | The names a harness sentence calls the landmasses: "Island 2" for the island, home's own words for home. |

### MAIN (lines 263-294)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 27 | `public static void main(String[] args) throws Exception` |  |

### 1. THE SURVEY (lines 295-464)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 165 | `static void theSurvey()` |  |

### 2. THE PRICE (lines 465-532)

| line | len | member | says |
|---:|---:|---|---|
| 469 | 63 | `static void thePrice()` |  |

### THE PLAYED CITY - Rotterdam with Goeree-Overflakkee's square, two landmasses (lines 533-573)

| line | len | member | says |
|---:|---:|---|---|
| 538 | 3 | **type** `record Island(World.Key key, LandmassCheck.Square square)` | The fixture: the world, and the island square found by search with the model's own reader (LandmassCheck's). |
| 539 | 1 | `int id()` _(in BridgeCheck.Island)_ |  |
| 542 | 9 | `static Island islandFor(World.Key key)` |  |
| 553 | 6 | `static LandmassCheck.City bridgeCity(Island f, String label)` | A city of forty houses and two builders' depots that owns the island square (unlinked, no bridge, nothing placed). |
| 561 | 6 | `static GridGround seam(LandmassCheck.City c, Island f, int w, byte cover, double depth)` | Hands the city's survey a channel of `w` plots between the island and home (the game's own ids and linked set), `cover` water `depth` metres deep. |
| 569 | 4 | `static Game.BuildResult orderBridge(LandmassCheck.City c, BuildingsTemplate t, int n, boolean instant, int toward)` | An order of a bridge, quietly, by the card's path: the far landmass picked, then the order. |

### 3. THE ORDER (lines 574-749)

| line | len | member | says |
|---:|---:|---|---|
| 578 | 155 | `static void theOrder(Island f)` |  |
| 734 | 5 | `static<T> T quiet(java.util.function.Supplier<T> work)` |  |
| 741 | 5 | `static Game.BuildResult quietBuild(Game g, BuildingsTemplate t, int n)` | An order of the city's, quietly. |

### 4. NO DEMOLITION (lines 750-772)

| line | len | member | says |
|---:|---:|---|---|
| 754 | 18 | `static void noDemolition(Island f)` |  |

### 5. THE SAVE (lines 773-842)

| line | len | member | says |
|---:|---:|---|---|
| 777 | 65 | `static void theSave(Island f) throws Exception` |  |

### 6. A GENERATED WORLD (lines 843-903)

| line | len | member | says |
|---:|---:|---|---|
| 847 | 56 | `static void generatedWorld()` |  |

### 7. THE FULL FILE (lines 904-967)

| line | len | member | says |
|---:|---:|---|---|
| 916 | 27 | `static BridgeSurvey.Result crossing(World.Key key, String label, double[] founding, double[] home, double[] farPoint, double re...` | A city founded at `founding`, owning a square of ground about each of the two points - its own shore (`home`) and the far one - and the survey of a Road Bridge toward the far landmass (the real ground, no seam): print... |
| 944 | 23 | `static void theEarth()` |  |


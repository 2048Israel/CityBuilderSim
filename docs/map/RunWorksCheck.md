# RunWorksCheck.java - 1,302 lines · 40 methods · 8 constants · harnesses

`ham/citybuildersim/RunWorksCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> Highways and rail on the slopes (ET4, slices A and B; docs/studies/spec-earth2-terrain.md 4.3, decision D10): a
> highway's or a railway line's ground comes from every class of ground in the city's proportions, not from the
> buildings' pool alone, so a mountain city's highways use its steep ground as real ones do; and an order for one pays
> for the cuts, the viaducts and the tunnels that ground needs, at the city's own mix of it.
> 
> WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure the model's own getters
> (sections 1 to 6 are slice A's, the land; 7 to 12 slice B's, the price of the works; 13 and 14 slice C's, the map):
> 
>   1. WHICH TEMPLATES ARE RUNS. Game.isRun() over the whole catalogue agrees with what the map draws as a highway or
>      as track (BuildingVisual.Type's road() and track()); the Elevated Highway, the Rail Spur and the Freight Line are
>      the runs, and the Rail Terminal and Depot (yards), the roads and the transit lines are buildings; no template
>      with a site is a run.
>   2. A RUN'S GROUND OVER THE CLASSES IN THE CITY'S PROPORTIONS. Mixed ground, 40% flat, 30% foothill, 20% steep, 10%
>      very steep: an Elevated Highway, a Rail Spur and a Freight Line are ordered, and the runs' ground on each class
>      is their land times the class's share of the owned ground; buildings still stand on the flat first after them,
>      works still on the steep first of what the runs leave, and the idle steep ground is down by the runs' steep share.
>   3. THE ROOM FOR A RUN. A city whose flat and foothill are full of buildings and whose steep ground is idle: the
>      ground free for a run is the arithmetic of the two constraints from the getters, an order a highway over it is
>      refused (NO_LAND) and one within it taken, and the buildings still fit after; a run whose share of the flat
>      pushes buildings onto the foothill raises the foothill prepared by exactly the rise in what stands on it, and
>      the next House is quoted its own pad only; a run in a run of orders is invoiced as it is charged.
>   4. THE PRICE AND THE BOOKS. A business's run plot is priced class by class at the city's proportions and given back
>      at the same; the books put a sector's track, and the city's highways, at the runs' mix, and summed over the
>      sectors, the categories and the ground no one holds each class is the split's own.
>   5. A FLAT CITY TO THE BIT. A city with neither foothill nor steep ground reads a run as a building: the ground free,
>      the price, the ground in use, the utilisation and the preparation are the same doubles as the buildings' path.
>   6. SAVED. landRunsSqFt goes through a save and a load to the bit; a save without it reads the standing runs' ground.
>   7. THE RATES. RunWorks' dollars a m2 on each class against their constants: none on the flat, a highway's foothill
>      a pad's, foothill under steep under very steep, a railway's a highway's times RAIL_FACTOR and above it on every
>      class above flat; the rate at a mix is the weighted sum written from the rates, and the points are its arithmetic.
>   8. A RUN OVER EACH CLASS. A city of one class of ground, an Elevated Highway and a Freight Line quoted: the works
>      are the land at the class's rate in points at the template's own founding price, their money the builders'
>      price of those points, and the quote exceeds the same city's flat quote by exactly that money, taxed with the work.
>   9. THE MIX. A city of all four classes: the works at the rate of its mix of dry ground, the mix the quote reports,
>      a railway's a m2 above a highway's by RAIL_FACTOR; and what is no run carries no works at all.
>  10. BUILDERS' WORK IN THE BOOKS. The city's highways and the Freight sector's Freight Line through the real paths:
>      the works owed on the site before the first building finishes, billed to Construction and earned as the crews
>      deliver them, the money audit and the GDP identity closing month by month; the investor's price includes them.
>  11. A FLAT CITY TO THE BIT. On all-flat ground a run's quote, its order, the books and the investor's price are what
>      they were before ET4: no works, no line, the counters at nothing.
>  12. THE SCREEN'S WORDS. BuildScreen.worksWords() reads the quote's fields, the mix among them; nothing on flat ground.
>  13. ON THE MAP. A tile with an east-west highway and a north-south track each crossing flat, steep, very steep and flat
>      ground: Painted.bridge is true on exactly the runs' steep plots and Painted.tunnel on exactly their very steep plots,
>      neither on the flat; at 8 px a plot (and 4) a flat plot of the highway has its surface in the middle, a viaduct's
>      plot is cased in the deck's colour and the track's has its deck beside the bed, a tunnel's plot is its ground with
>      the run's dashed line, and TUNNEL_PORTAL pixels lie at each tunnel's two ends and nowhere inside it; a plot of the
>      same relief with no run is drawn as it is without the runs; in the blocks' views a tunnel is its ground and the
>      run's colour on every other plot; the hover says "a viaduct" and "a tunnel".
>  14. THE STREETS' RULE DID NOT MOVE. A street plot on fresh water is still a bridge and no tunnel, a street plot on steep
>      or very steep ground neither (the plans never lay one there), a run plot on foothill neither, and a run over fresh
>      water a bridge and never a tunnel, whatever relief the plot carries.
> 
> WHY. ET2 made the land a pool of three classes with buildings on the flat and foothill only and works on any, and a
> highway, a building of the pool, could therefore never stand on a steep slope; the spec says it does (4.3). Each
> section works its figure out from the getters, never from a number copied from a run.

**Uses:** [Game](Game.md) (95), [LandManager](LandManager.md) (83), [RunWorks](RunWorks.md) (73), [World](World.md) (61), [TileRaster](TileRaster.md) (40), [TilePainter](TilePainter.md) (28), [BuildingsTemplate](BuildingsTemplate.md) (24), [SlopeCheck](SlopeCheck.md) (19), [Sectors](Sectors.md) (18), [BuildingVisual](BuildingVisual.md) (11), [BuildingManager](BuildingManager.md) (9), [BuildingType](BuildingType.md) (6), [LandMap](LandMap.md) (6), [EconomyManager](EconomyManager.md) (4), [SitePreparation](SitePreparation.md) (4), [GameFiles](GameFiles.md) (3), [Sector](Sector.md) (2), [Mining](Mining.md) (2), [CityLand](CityLand.md) (2), [SectorStatementCheck](SectorStatementCheck.md) (2), [Construction](Construction.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [LandClassCheck](LandClassCheck.md) (1), [BuildCard](BuildCard.md) (1), [Money](Money.md) (1), [DistrictPlan](DistrictPlan.md) (1)

**Used by (1):** [RunRenders](RunRenders.md)

## Sections

| line | section |
|---:|---|
| 154 | fixtures |
| 220 | 1. WHICH TEMPLATES ARE RUNS |
| 262 | 2. A RUN'S GROUND OVER THE CLASSES IN THE CITY'S PROPORTIONS |
| 334 | 3. THE ROOM FOR A RUN |
| 423 | 4. THE PRICE AND THE BOOKS |
| 523 | 5. A FLAT CITY TO THE BIT |
| 593 | 6. SAVED |
| 639 | slice B: the price of the cuts, the viaducts and the tunnels |
| 669 | 7. THE RATES |
| 742 | 8. A RUN OVER EACH CLASS OF GROUND |
| 808 | 9. THE CITY'S MIX OF GROUND |
| 881 | 10. BUILDERS' WORK IN THE BOOKS |
| 979 | 11. A FLAT CITY TO THE BIT |
| 1037 | 12. THE SCREEN'S WORDS |
| 1098 | 13. ON THE MAP, AND 14. THE STREETS' RULE (slice C; spec-earth2-terrain 4.3: "a run on very steep ground is drawn as a |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 114 | `RunWorksCheck.CASH` | `1e10` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 117 | `RunWorksCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 120 | `RunWorksCheck.OWNED` | `24_000_000` | The ground a fixture city owns, square feet: a Freight Line (2.4 million) and the buildings beside it fit in it. |
| 122 | `RunWorksCheck.HOUSE` | `"House", HIGHWAY = "Elevated Highway", SPUR = "Rail Spur", LINE = "Freight Line"` |  |
| 644 | `RunWorksCheck.BUSINESS_CASH` | `4e6` | The cash a business is given to pay a run's order from by fiat, thousands: the Freight Line's price and more, in a figure small enough for the sectors' statements to be exact to the cent. |
| 886 | `RunWorksCheck.HIGHWAY_DEPOTS` | `20, FREIGHT_DEPOTS = 100` | The depots a fixture's builders are given for 10: enough that a few highways (a Freight Line) take them some years, not centuries. |
| 1103 | `RunWorksCheck.T` | `TilePainter.TILE` |  |
| 1106 | `RunWorksCheck.HY` | `6, HX0 = 8, TX = 4, BARE = 20` | The fixture's rows and columns: the highway along row HY from column HX0, the track down column TX, and row BARE with no run. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `static int fails` |  |
| 72 | `static PrintStream out` |  |
| 73 | `static PrintStream quiet` |  |
| 125 | `static GameFiles files` | The save folder of the fixture last founded (hills()), which section 6 saves to and loads from. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 1234 | **type** `public class RunWorksCheck` | Highways and rail on the slopes (ET4, slices A and B; docs/studies/spec-earth2-terrain.md 4.3, decision D10): a highway's or a railway line's ground comes from every class of ground in the city's proportions, not from... |
| 75 | 4 | `static void check(String label, boolean ok)` |  |
| 80 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 85 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 91 | 5 | `static void quietly(Runnable r)` |  |
| 98 | 3 | `static boolean near(double a, double b)` | The two figures agree to a rounding of the split's arithmetic, relative to the larger. |
| 102 | 5 | `static boolean near(double[] a, double[] b)` |  |
| 109 | 3 | `static boolean same(double a, double b)` | The two figures are the same double, to the bit. |
| 127 | 26 | `public static void main(String[] args) throws Exception` |  |

### fixtures (lines 154-219)

| line | len | member | says |
|---:|---:|---|---|
| 158 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 171 | 18 | `static Game hills(String label, double owned, double flat, double foothill, double steep, double veryStep)` | A city founded as a harness founds one, OWNED square feet of dry ground (its founded size where `owned` is 0, which is what a save keeps the ground by relief of: the relief must add up to the centre's own) of the shar... |
| 191 | 5 | `static Game.BuildResult order(Game g, BuildingsTemplate t, int n)` | The city's own order, as the build screen places it. |
| 198 | 6 | `static boolean businessOrder(Game g, String sector, BuildingsTemplate t, int n)` | A business's order, built by hand from cash it is given. |
| 206 | 7 | `static void closeBooks(Game g)` | The books as a month's close leaves them (Game.recordMonth(): the land's price and its classes pushed, the sheets pushed, the books taken), without playing the month. |
| 215 | 4 | `static double[] shares(LandManager lm)` | The share of the city's owned dry ground that is flat, foothill and steep (steep and very steep together): what a run's ground is spread by. |

### 1. WHICH TEMPLATES ARE RUNS (lines 220-261)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 37 | `static void whichAreRuns()` |  |

### 2. A RUN'S GROUND OVER THE CLASSES IN THE CITY'S PROPORTIONS (lines 262-333)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 67 | `static void spreadOverTheClasses()` |  |

### 3. THE ROOM FOR A RUN (lines 334-422)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 84 | `static void theRoomForARun()` |  |

### 4. THE PRICE AND THE BOOKS (lines 423-522)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 95 | `static void thePriceAndTheBooks()` |  |

### 5. A FLAT CITY TO THE BIT (lines 523-592)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 65 | `static void aFlatCityToTheBit()` |  |

### 6. SAVED (lines 593-638)

| line | len | member | says |
|---:|---:|---|---|
| 597 | 41 | `static void saved() throws Exception` |  |

### slice B: the price of the cuts, the viaducts and the tunnels (lines 639-668)

| line | len | member | says |
|---:|---:|---|---|
| 647 | 4 | `static void flatten(Game g)` | The city's ground made all flat, as SlopeCheck 4 does it: the relief is read live, so the same order can be asked again as a flat city's. |
| 653 | 5 | `static double[] reliefOf(Game g)` | The city's own dry ground by relief (CityLand.setCentreRelief()'s five figures, km2), to put back after a fixture has been flattened. |
| 660 | 3 | `static double perPoint(BuildingsTemplate t)` | The founding cash a point of a template's own work, thousands: what RunWorks.points() is struck at. |
| 665 | 3 | `static double usdPerM2Of(Game.BuildQuote q, BuildingsTemplate t)` | The dollars a m2 of the run's land that a quote's works come to: its points at the founding price a point, over the land it priced. |

### 7. THE RATES (lines 669-741)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 68 | `static void theRates()` |  |

### 8. A RUN OVER EACH CLASS OF GROUND (lines 742-807)

| line | len | member | says |
|---:|---:|---|---|
| 746 | 61 | `static void overEachClass()` |  |

### 9. THE CITY'S MIX OF GROUND (lines 808-880)

| line | len | member | says |
|---:|---:|---|---|
| 812 | 68 | `static void theMix()` |  |

### 10. BUILDERS' WORK IN THE BOOKS (lines 881-978)

| line | len | member | says |
|---:|---:|---|---|
| 888 | 90 | `static void inTheBooks()` |  |

### 11. A FLAT CITY TO THE BIT (lines 979-1036)

| line | len | member | says |
|---:|---:|---|---|
| 983 | 53 | `static void aFlatCityPaysNoWorks()` |  |

### 12. THE SCREEN'S WORDS (lines 1037-1097)

| line | len | member | says |
|---:|---:|---|---|
| 1042 | 10 | `static String worksWords(Game.BuildQuote q)` | BuildScreen.worksWords(), a package-private static of the build screen, read through reflection: it must not need a toolkit. |
| 1053 | 44 | `static void theScreensWords()` |  |

### 13. ON THE MAP, AND 14. THE STREETS' RULE (slice C; spec-earth2-terrain 4.3: "a run on very steep ground is drawn as a (lines 1098-1302)

| line | len | member | says |
|---:|---:|---|---|
| 1109 | 3 | `static byte band(int k)` | A fixture's relief at k along an axis: flat to 9, steep 10 to 15, very steep 16 to 23, flat from 24. |
| 1114 | 8 | `public static TilePainter.Input mapTile()` | One owned tile of dry grass: relief in bands along x (and, down the track's column, along y), an east-west highway on row HY and a north-south track on column TX. |
| 1123 | 5 | `static TilePainter.Painted paintTile(TilePainter.Input in)` |  |
| 1129 | 5 | `static int[] rasterAt(TilePainter.Input in, TilePainter.Painted p, int px)` |  |
| 1136 | 3 | `static int pixel(int[] img, int px, int plotX, int plotY, int dx, int dy)` | One pixel of plot (plotX, plotY), local (dx, dy), at px pixels a plot. |
| 1141 | 5 | `static int portals(int[] img, int px, int plotX, int plotY)` | How many pixels of plot (plotX, plotY) are TUNNEL_PORTAL. |
| 1148 | 5 | `static int portals(int[] img)` | How many pixels of the whole image are TUNNEL_PORTAL. |
| 1154 | 113 | `static void onTheMap()` |  |
| 1268 | 34 | `static void theStreetsRuleDidNotMove()` |  |


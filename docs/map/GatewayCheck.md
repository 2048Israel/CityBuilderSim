# GatewayCheck.java - 1,060 lines · 36 methods · 6 constants · harnesses

`ham/citybuildersim/GatewayCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> A city's gateway is the way the world is.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. A STRIP OF LAND RUNNING ONE KNOWN WAY gives the gateway on that
>      bearing, at the city's radius, with every returned bearing a multiple
>      of the dial's step.
> 
>   2. AN ISLAND FOUNDING has no gateway: land only within a small disc round
>      the site is short of the reach.
> 
>   3. TWO EQUAL ARMS, east and south, tie; the gateway is one of the two,
>      chosen by the founding site's hash (two sites in the same cell that pick
>      differently are shown when the offsets find them, else the site's own
>      pick is asserted one of the two).
> 
>   4. A HALF-PLANE OF LAND WEST ties every westward bearing, and the gateway
>      is the middle of that wide arc (180), not the first westward bearing.
> 
>   5. THE THRESHOLD is exact: a strip one cell short of the reach has no
>      gateway, and a strip that just reaches has one.
> 
>   6. THE DEFAULT WORLD is deterministic. If a gateway is found its point
>      lies on the edge and on land; if none is found, the best land run is
>      really short of the reach.
> 
>   7. TWELVE REAL GENERATED WORLDS fire the mechanic at least once, so the
>      rule is not only a fixture's.
> 
>   8. A FULL TIE IS NOT DUE EAST FOR EVERYONE: an all-land ground, 24
>      founding sites, gives every bearing a multiple of the step and at
>      least 6 distinct bearings among them.
> 
>   9. THE EARTH'S RULE: one larger place due north wins the north bearing;
>      a nearer place whose way is sea is passed over for a farther clear
>      one; an empty or null list falls back to the land rule.
> 
>  10. THE SPINE IS LAID TOWARD THE GATEWAY: with gateway 90 (south) the
>      first plots run down the hub's column, then the opposite arm; trimming
>      takes the opposite arm first. With gateway 0 (east) the same on the
>      row.
> 
>  11. NO GATEWAY LAYS AS BEFORE: the row's two arms alternate first.
> 
>  12. THE SIDECAR KEEPS IT: FORMAT 8 writes and reads the gateway back; a
>      FORMAT 7 sidecar reads it unknown, and the next layRuns finds it
>      again.
> 
>  13. WHAT FINDING ONE COSTS: one real generated world, timed and printed.
> 
>  14. THE WORLD LINE: a square city with a gateway and both nets laid has a
>      world line for each net whose gate arm is laid; each line's point is
>      the first plot the city does not own along the gate arm's heading, and
>      its bearing is the gateway. With NO_GATEWAY there are none.
> 
>  15. THE CACHE: two calls with nothing changed return equal lists; after a
>      month's runs move changes(), the lines are recomputed equal to a fresh
>      map's, not the same list.
> 
>  16. THE LINE TO THE WORLD LEAVES ON LAND, ON A GENERATED COAST (FX2): a
> ... (44 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (70), [CityMap](CityMap.md) (59), [Gateways](Gateways.md) (52), [LandMap](LandMap.md) (37), [World](World.md) (27), [MapCheck](MapCheck.md) (20), [BuildingVisual](BuildingVisual.md) (19), [Founding](Founding.md) (9), [TrainSchedule](TrainSchedule.md) (7), [EarthData](EarthData.md) (7), [BuildingCatalog](BuildingCatalog.md) (2), [TrackCurves](TrackCurves.md) (2), [SeaRoutes](SeaRoutes.md) (1), [EarthTerrainCheck](EarthTerrainCheck.md) (1), [EarthTerrain](EarthTerrain.md) (1), [Mining](Mining.md) (1)

**Used by (4):** [BridgeMapCheck](BridgeMapCheck.md), [IslandNetCheck](IslandNetCheck.md), [MapCheck](MapCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 267 | · 1. a strip of land east |
| 283 | · 2. an island founding |
| 293 | · 3. a tie |
| 323 | · 4. a half-plane of land west |
| 330 | · 5. the threshold |
| 341 | · 6. the default world |
| 367 | · 7. twelve real generated worlds |
| 378 | · 8. a full tie is not due east for everyone |
| 395 | · 9. the Earth's rule |
| 413 | · 10. the spine is laid toward the gateway |
| 418 | · 11. no gateway lays as before |
| 436 | · 12. the sidecar keeps it |
| 459 | · 13. what finding one costs |
| 468 | · 14. the world line |
| 501 | · 15. the cache |
| 531 | FX2: the line to the world on land, the label in view, the undrawn highway |
| 800 | · the label (section 18) |
| 862 | · two lines leaving together (section 20) |
| 936 | · the highway that reaches no street (section 19) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 122 | `GatewayCheck.COAST_TRACK_TIMES` | `10` | The coast fixture's railway: each track type's count times this, 10 - enough track that the railway's gate arm reaches the coast and waits at the sea (section 16). |
| 125 | `GatewayCheck.HALIFAX_LAT` | `44.656780322078724, HALIFAX_LON = - 63.613890005401984` | Jerus's founding site, Halifax (section 17), in degrees. |
| 128 | `GatewayCheck.MOST_HIGHWAYS` | `20` | The most Elevated Highways section 19 tries when it looks for the count that reaches a street. |
| 131 | `GatewayCheck.EPS` | `1e-9` | The tolerance of a position in plots or pixels, well below anything either measures. |
| 803 | `GatewayCheck.VIEW_W` | `1000, VIEW_H = 600, LABEL_W = 110, LABEL_H = 13` | The view and the label it is tried with: 1,000 x 600 pixels, a label 110 wide and 13 high above its baseline. |
| 865 | `GatewayCheck.LABEL_BOTH_W` | `LABEL_W * LandMap.WORLD_BOTH_WORDS.length() /(double) LandMap.WORLD_WORDS.len...` | The shared label's width in the harness: WORLD_BOTH_WORDS in the font that makes WORLD_WORDS LABEL_W wide (monospaced: by the characters). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 119 | `static int fails` |  |
| 140 | `final long x0, y0, x1, y1` |  |
| 940 | `final long x0, y0` |  |
| 941 | `final int w, h` |  |
| 942 | `final byte[] fixed, marks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 117 | 944 | **type** `public class GatewayCheck` | A city's gateway is the way the world is. |
| 133 | 4 | `static void check(String label, boolean ok)` |  |
| 139 | 19 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: a harness fixture for the spine. |
| 142 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in GatewayCheck.BoxGround)_ |  |
| 146 | 3 | `public int at(long x, long y, int dir)` _(in GatewayCheck.BoxGround)_ |  |
| 150 | 3 | `public long version()` _(in GatewayCheck.BoxGround)_ |  |
| 154 | 3 | `public long[] box()` _(in GatewayCheck.BoxGround)_ |  |
| 160 | 3 | `static BuildingVisual.Type[] gatewayTypes()` | The game's building templates, as the map draws them. |
| 165 | 4 | `static int highwayType(BuildingVisual.Type[] types)` | The highway type's id. |
| 171 | 4 | `static int trackType(BuildingVisual.Type[] types)` | The first railway line type's id. |
| 177 | 10 | `static CityMap gatewayCity(BuildingVisual.Type[] types, long highway, long track)` | A square city on the default world's dry place with `highway` highways and `track` railway lines, its gateway found. |
| 189 | 14 | `static int bestRun(Gateways.Ground ground, long ci, long cj)` | The longest run of land cells out from a cell, over every bearing - the harness's own look at the same ground. |
| 205 | 54 | `static void spineCase(int gateway, String toward, String opposite, boolean vertical, long siteX, long siteY, long cell)` | Lays a fresh spine toward `gateway` on a square box and proves the arm order: the toward arm first, the opposite arm only when it waits, and the opposite arm taken back first. |
| 260 | 270 | `public static void main(String[] args)` |  |

### FX2: the line to the world on land, the label in view, the undrawn highway (lines 531-799)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 5 | `static String armText(CityRuns.Net n, int a)` | An arm as one line of text: its index, heading in degrees and end, or "none". |
| 545 | 12 | `static int[] fr0Walk(CityMap m, CityRuns.Net n, int a)` | FR0's walk, written out: from net n's arm a's end (the hub while the arm has no steps) along its heading, up to CityMap.DISTRICT plots, to the first plot the city does not own. |
| 559 | 10 | `static double nearestOnRoute(TrainSchedule.Route r, double x, double y)` | The least distance in plots from (x, y) to the route's polyline (FX5: a rounded route passes by its turns' points). |
| 571 | 16 | `static int offTrack(World w, CityRuns runs, TrainSchedule.Route r, int[] samples)` | How many of a route's points, read every WORLD_STEP along each leg, lie on neither dry ground nor a plot the railway carries (track, track over a highway, a yard); the points read are in samples[0]. |
| 595 | 91 | `static void worldLinesOnLand(String what, CityMap m)` | The checks every FX2 fixture runs: each of the map's world lines starts on a dry plot the city does not own, at the end of an arm's track within WORLD_GAP plots of it over the city's own dry ground, and runs out over ... |
| 688 | 9 | `static long[] coastCounts(BuildingVisual.Type[] types)` | The coast fixture's counts: the shore's town with every track type's count times COAST_TRACK_TIMES and YARDS_ADDED more of each Rail Terminal. |
| 699 | 22 | `static void coastSection()` | Section 16: the default world's founding site one district north, three districts a side, drawn canonically on its coast. |
| 729 | 24 | `static void coastMonths()` | Section 16's coast over its months (FX2's "over a real run's months": the played city of MapCheck 1 lays no highway and no track in its 120): the same city from nothing, its counts rising evenly to coastCounts() over ... |
| 755 | 44 | `static void halifaxSection()` | Section 17: a square city at Jerus's founding site on the Earth, when earth-2.bin is named. |

### the label (section 18) (lines 800-861)

| line | len | member | says |
|---:|---:|---|---|
| 806 | 4 | `static boolean inView(double x, double y, double w, double h)` | Whether a label whose baseline starts at (x, y) lies inside a w x h view by LandMap.WORLD_LABEL_PAD on every side - the harness's own reading of the rule. |
| 812 | 5 | `static double offPart(double x, double y, double x0, double y0, double x1, double y1)` | How far (x, y) lies from the segment (x0, y0) to (x1, y1), in pixels. |
| 819 | 15 | `static void labelLeaving(String what, double x0, double y0, double x1, double y1)` | One line leaving the view: FR0's spot clips, and worldLabelAt() puts the label whole inside the view, its baseline's left end on the part, at the first place along it, from FR0's spot back, that fits. |
| 836 | 25 | `static void labelSection()` | Section 18: LandMap.worldLabelAt() on pure fixtures. |

### two lines leaving together (section 20) (lines 862-935)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 4 | `static boolean clearOf(double[] a, double wa, double[] b, double wb, double gap)` | Whether two label boxes (baselines' left ends a and b, widths wa and wb, LABEL_H high above their baselines) are clear of each other by `gap` pixels, across or down - the harness's own reading of the rule. |
| 874 | 61 | `static void togetherSection()` | Section 20: two world lines leaving together. |

### the highway that reaches no street (section 19) (lines 936-1060)

| line | len | member | says |
|---:|---:|---|---|
| 939 | 66 | **type** `static final class BoxFill` | A whole-box fill of a map's runs: the plots from (x0, y0), w x h, their codes and marks. |
| 944 | 10 | `BoxFill(CityMap m)` _(in GatewayCheck.BoxFill)_ |  |
| 956 | 3 | `boolean hw(int g)` _(in GatewayCheck.BoxFill)_ | Whether plot index g carries a highway (a railway over one included). |
| 961 | 5 | `long drawn()` _(in GatewayCheck.BoxFill)_ | Highway plots drawn. |
| 968 | 7 | `boolean rampOnCellLine(int g)` _(in GatewayCheck.BoxFill)_ | Whether plot g shows a ramp on a cell line: M_RAMP, and its coordinate along the highway (x on a row, y on a column) a multiple of CityRuns.CELL. |
| 977 | 27 | `long[] pieces()` _(in GatewayCheck.BoxFill)_ | {pieces of highway (8 neighbours), highway plots in a piece with no ramp on a cell line}. |
| 1007 | 3 | `static long modelHighwayPlots(CityMap m)` | The highway plots the model has (its Elevated Highways' plots), as MapCheck counts them. |
| 1012 | 4 | `static boolean hasHighwayLine(CityMap m)` | Whether a map has a world line for the highways. |
| 1018 | 13 | `static void drawnWhole(String what, CityMap m)` | A reaching city's highways as the map draws them: every plot in a piece with a ramp on a cell line, and the drawn plots and the undrawn ones the model's. |
| 1033 | 27 | `static void undrawnSection()` | Section 19: gatewayCity() with highways and no track. |


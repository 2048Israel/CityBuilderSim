# GatewayCheck.java - 1,185 lines · 40 methods · 9 constants · harnesses

`ham/citybuildersim/GatewayCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
> ... (59 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (77), [CityMap](CityMap.md) (66), [Gateways](Gateways.md) (52), [LandMap](LandMap.md) (37), [World](World.md) (29), [MapCheck](MapCheck.md) (28), [BuildingVisual](BuildingVisual.md) (22), [Founding](Founding.md) (11), [TrainSchedule](TrainSchedule.md) (7), [EarthData](EarthData.md) (7), [BuildingCatalog](BuildingCatalog.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [TrackCurves](TrackCurves.md) (2), [SeaRoutes](SeaRoutes.md) (1), [EarthTerrainCheck](EarthTerrainCheck.md) (1), [EarthTerrain](EarthTerrain.md) (1), [Mining](Mining.md) (1)

**Used by (4):** [BridgeMapCheck](BridgeMapCheck.md), [IslandNetCheck](IslandNetCheck.md), [MapCheck](MapCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 306 | · 1. a strip of land east |
| 322 | · 2. an island founding |
| 332 | · 3. a tie |
| 362 | · 4. a half-plane of land west |
| 369 | · 5. the threshold |
| 380 | · 6. the default world |
| 406 | · 7. twelve real generated worlds |
| 417 | · 8. a full tie is not due east for everyone |
| 434 | · 9. the Earth's rule |
| 452 | · 10. the spine is laid toward the gateway |
| 457 | · 11. no gateway lays as before |
| 475 | · 12. the sidecar keeps it |
| 498 | · 13. what finding one costs |
| 507 | · 14. the world line |
| 540 | · 15. the cache |
| 571 | FX2: the line to the world on land, the label in view, the undrawn highway |
| 840 | · the label (section 18) |
| 902 | · two lines leaving together (section 20) |
| 976 | · the highway that reaches no street (section 19) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 138 | `GatewayCheck.COAST_TRACK_TIMES` | `10` | The coast fixture's railway: each track type's count times this, 10 - enough track that the railway's gate arm reaches the coast and waits at the sea (section 16). |
| 141 | `GatewayCheck.COAST_SAVED_AT` | `100` | The month section 21 saves the coast city at: past the month its main line first runs on beyond an arm that waits at the sea, between 55 and 60 on this world (CC1-M's loads at every fifth month: the first to come back... |
| 144 | `GatewayCheck.COAST_HELD` | `10` | The months section 21 plays past MapCheck.MONTHS with the counts held. |
| 147 | `GatewayCheck.HALIFAX_LAT` | `44.656780322078724, HALIFAX_LON = - 63.613890005401984` | Jerus's founding site, Halifax (section 17), in degrees. |
| 150 | `GatewayCheck.MOST_HIGHWAYS` | `20` | The most Elevated Highways section 19 tries when it looks for the count that reaches a street. |
| 153 | `GatewayCheck.EPS` | `1e-9` | The tolerance of a position in plots or pixels, well below anything either measures. |
| 194 | `GatewayCheck.GROUND_BEFORE_SL1` | `{ 430_556, 2_368_060, 2_152_782, 107_639 }` | The four sea terminals' ground in square feet, ids 94-97, as it was before SL1 (2026-10-28, main at 0.8.66): a Tanker Terminal, a Bulk Terminal, a Container Terminal and a General Cargo Terminal. |
| 843 | `GatewayCheck.VIEW_W` | `1000, VIEW_H = 600, LABEL_W = 110, LABEL_H = 13` | The view and the label it is tried with: 1,000 x 600 pixels, a label 110 wide and 13 high above its baseline. |
| 905 | `GatewayCheck.LABEL_BOTH_W` | `LABEL_W * LandMap.WORLD_BOTH_WORDS.length() /(double) LandMap.WORLD_WORDS.len...` | The shared label's width in the harness: WORLD_BOTH_WORDS in the font that makes WORLD_WORDS LABEL_W wide (monospaced: by the characters). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `static int fails` |  |
| 162 | `final long x0, y0, x1, y1` |  |
| 980 | `final long x0, y0` |  |
| 981 | `final int w, h` |  |
| 982 | `final byte[] fixed, marks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 133 | 1053 | **type** `public class GatewayCheck` | A city's gateway is the way the world is. |
| 155 | 4 | `static void check(String label, boolean ok)` |  |
| 161 | 19 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: a harness fixture for the spine. |
| 164 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in GatewayCheck.BoxGround)_ |  |
| 168 | 3 | `public int at(long x, long y, int dir)` _(in GatewayCheck.BoxGround)_ |  |
| 172 | 3 | `public long version()` _(in GatewayCheck.BoxGround)_ |  |
| 176 | 3 | `public long[] box()` _(in GatewayCheck.BoxGround)_ |  |
| 182 | 3 | `static BuildingVisual.Type[] gatewayTypes()` | The game's building templates, as the map draws them. |
| 197 | 5 | `static BuildingVisual.Type[] gatewayTypesBeforeSL1()` | ...with the four terminals on GROUND_BEFORE_SL1's ground: section 21's. |
| 204 | 4 | `static int highwayType(BuildingVisual.Type[] types)` | The highway type's id. |
| 210 | 4 | `static int trackType(BuildingVisual.Type[] types)` | The first railway line type's id. |
| 216 | 10 | `static CityMap gatewayCity(BuildingVisual.Type[] types, long highway, long track)` | A square city on the default world's dry place with `highway` highways and `track` railway lines, its gateway found. |
| 228 | 14 | `static int bestRun(Gateways.Ground ground, long ci, long cj)` | The longest run of land cells out from a cell, over every bearing - the harness's own look at the same ground. |
| 244 | 54 | `static void spineCase(int gateway, String toward, String opposite, boolean vertical, long siteX, long siteY, long cell)` | Lays a fresh spine toward `gateway` on a square box and proves the arm order: the toward arm first, the opposite arm only when it waits, and the opposite arm taken back first. |
| 299 | 271 | `public static void main(String[] args)` |  |

### FX2: the line to the world on land, the label in view, the undrawn highway (lines 571-839)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 5 | `static String armText(CityRuns.Net n, int a)` | An arm as one line of text: its index, heading in degrees and end, or "none". |
| 585 | 12 | `static int[] fr0Walk(CityMap m, CityRuns.Net n, int a)` | FR0's walk, written out: from net n's arm a's end (the hub while the arm has no steps) along its heading, up to CityMap.DISTRICT plots, to the first plot the city does not own. |
| 599 | 10 | `static double nearestOnRoute(TrainSchedule.Route r, double x, double y)` | The least distance in plots from (x, y) to the route's polyline (FX5: a rounded route passes by its turns' points). |
| 611 | 16 | `static int offTrack(World w, CityRuns runs, TrainSchedule.Route r, int[] samples)` | How many of a route's points, read every WORLD_STEP along each leg, lie on neither dry ground nor a plot the railway carries (track, track over a highway, a yard); the points read are in samples[0]. |
| 635 | 91 | `static void worldLinesOnLand(String what, CityMap m)` | The checks every FX2 fixture runs: each of the map's world lines starts on a dry plot the city does not own, at the end of an arm's track within WORLD_GAP plots of it over the city's own dry ground, and runs out over ... |
| 728 | 9 | `static long[] coastCounts(BuildingVisual.Type[] types)` | The coast fixture's counts: the shore's town with every track type's count times COAST_TRACK_TIMES and YARDS_ADDED more of each Rail Terminal. |
| 739 | 22 | `static void coastSection()` | Section 16: the default world's founding site one district north, three districts a side, drawn canonically on its coast. |
| 769 | 24 | `static void coastMonths()` | Section 16's coast over its months (FX2's "over a real run's months": the played city of MapCheck 1 lays no highway and no track in its 120): the same city from nothing, its counts rising evenly to coastCounts() over ... |
| 795 | 44 | `static void halifaxSection()` | Section 17: a square city at Jerus's founding site on the Earth, when earth-2.bin is named. |

### the label (section 18) (lines 840-901)

| line | len | member | says |
|---:|---:|---|---|
| 846 | 4 | `static boolean inView(double x, double y, double w, double h)` | Whether a label whose baseline starts at (x, y) lies inside a w x h view by LandMap.WORLD_LABEL_PAD on every side - the harness's own reading of the rule. |
| 852 | 5 | `static double offPart(double x, double y, double x0, double y0, double x1, double y1)` | How far (x, y) lies from the segment (x0, y0) to (x1, y1), in pixels. |
| 859 | 15 | `static void labelLeaving(String what, double x0, double y0, double x1, double y1)` | One line leaving the view: FR0's spot clips, and worldLabelAt() puts the label whole inside the view, its baseline's left end on the part, at the first place along it, from FR0's spot back, that fits. |
| 876 | 25 | `static void labelSection()` | Section 18: LandMap.worldLabelAt() on pure fixtures. |

### two lines leaving together (section 20) (lines 902-975)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 4 | `static boolean clearOf(double[] a, double wa, double[] b, double wb, double gap)` | Whether two label boxes (baselines' left ends a and b, widths wa and wb, LABEL_H high above their baselines) are clear of each other by `gap` pixels, across or down - the harness's own reading of the rule. |
| 914 | 61 | `static void togetherSection()` | Section 20: two world lines leaving together. |

### the highway that reaches no street (section 19) (lines 976-1185)

| line | len | member | says |
|---:|---:|---|---|
| 979 | 66 | **type** `static final class BoxFill` | A whole-box fill of a map's runs: the plots from (x0, y0), w x h, their codes and marks. |
| 984 | 10 | `BoxFill(CityMap m)` _(in GatewayCheck.BoxFill)_ |  |
| 996 | 3 | `boolean hw(int g)` _(in GatewayCheck.BoxFill)_ | Whether plot index g carries a highway (a railway over one included). |
| 1001 | 5 | `long drawn()` _(in GatewayCheck.BoxFill)_ | Highway plots drawn. |
| 1008 | 7 | `boolean rampOnCellLine(int g)` _(in GatewayCheck.BoxFill)_ | Whether plot g shows a ramp on a cell line: M_RAMP, and its coordinate along the highway (x on a row, y on a column) a multiple of CityRuns.CELL. |
| 1017 | 27 | `long[] pieces()` _(in GatewayCheck.BoxFill)_ | {pieces of highway (8 neighbours), highway plots in a piece with no ramp on a cell line}. |
| 1047 | 3 | `static long modelHighwayPlots(CityMap m)` | The highway plots the model has (its Elevated Highways' plots), as MapCheck counts them. |
| 1052 | 4 | `static boolean hasHighwayLine(CityMap m)` | Whether a map has a world line for the highways. |
| 1058 | 13 | `static void drawnWhole(String what, CityMap m)` | A reaching city's highways as the map draws them: every plot in a piece with a ramp on a cell line, and the drawn plots and the undrawn ones the model's. |
| 1073 | 27 | `static void undrawnSection()` | Section 19: gatewayCity() with highways and no track. |
| 1102 | 3 | `static CityMap readBack(CityMap m, byte[] sidecar, int month)` | A map read back from a sidecar, on the live map's ground, as section 12 reads one. |
| 1107 | 8 | `static boolean sameStops(CityRuns a, CityRuns b)` | Whether every arm of both nets in two runs was last stopped by the same thing (CityRuns.Arm.stoppedBy). |
| 1122 | 63 | `static void coastAcrossASave()` | Section 21 (CC1-M): section 16's coast city built month by month to COAST_SAVED_AT, its sidecar written and read back, and the live map and the one read back reconciled together on to MapCheck.MONTHS and COAST_HELD mo... |


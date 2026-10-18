# GatewayCheck.java - 955 lines · 33 methods · 5 constants · harnesses

`ham/citybuildersim/GatewayCheck.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

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
> ... (28 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (70), [CityMap](CityMap.md) (59), [Gateways](Gateways.md) (52), [World](World.md) (27), [MapCheck](MapCheck.md) (20), [BuildingVisual](BuildingVisual.md) (19), [LandMap](LandMap.md) (10), [Founding](Founding.md) (9), [EarthData](EarthData.md) (7), [TrainSchedule](TrainSchedule.md) (6), [BuildingCatalog](BuildingCatalog.md) (2), [SeaRoutes](SeaRoutes.md) (1), [EarthTerrainCheck](EarthTerrainCheck.md) (1), [EarthTerrain](EarthTerrain.md) (1), [Mining](Mining.md) (1)

## Sections

| line | section |
|---:|---|
| 251 | · 1. a strip of land east |
| 267 | · 2. an island founding |
| 277 | · 3. a tie |
| 307 | · 4. a half-plane of land west |
| 314 | · 5. the threshold |
| 325 | · 6. the default world |
| 351 | · 7. twelve real generated worlds |
| 362 | · 8. a full tie is not due east for everyone |
| 379 | · 9. the Earth's rule |
| 397 | · 10. the spine is laid toward the gateway |
| 402 | · 11. no gateway lays as before |
| 420 | · 12. the sidecar keeps it |
| 443 | · 13. what finding one costs |
| 452 | · 14. the world line |
| 485 | · 15. the cache |
| 514 | FX2: the line to the world on land, the label in view, the undrawn highway |
| 769 | · the label (section 18) |
| 831 | · the highway that reaches no street (section 19) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 106 | `GatewayCheck.COAST_TRACK_TIMES` | `10` | The coast fixture's railway: each track type's count times this, 10 - enough track that the railway's gate arm reaches the coast and waits at the sea (section 16). |
| 109 | `GatewayCheck.HALIFAX_LAT` | `44.656780322078724, HALIFAX_LON = - 63.613890005401984` | Jerus's founding site, Halifax (section 17), in degrees. |
| 112 | `GatewayCheck.MOST_HIGHWAYS` | `20` | The most Elevated Highways section 19 tries when it looks for the count that reaches a street. |
| 115 | `GatewayCheck.EPS` | `1e-9` | The tolerance of a position in plots or pixels, well below anything either measures. |
| 772 | `GatewayCheck.VIEW_W` | `1000, VIEW_H = 600, LABEL_W = 110, LABEL_H = 13` | The view and the label it is tried with: 1,000 x 600 pixels, a label 110 wide and 13 high above its baseline. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `static int fails` |  |
| 124 | `final long x0, y0, x1, y1` |  |
| 835 | `final long x0, y0` |  |
| 836 | `final int w, h` |  |
| 837 | `final byte[] fixed, marks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 101 | 855 | **type** `public class GatewayCheck` | A city's gateway is the way the world is. |
| 117 | 4 | `static void check(String label, boolean ok)` |  |
| 123 | 19 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: a harness fixture for the spine. |
| 126 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in GatewayCheck.BoxGround)_ |  |
| 130 | 3 | `public int at(long x, long y, int dir)` _(in GatewayCheck.BoxGround)_ |  |
| 134 | 3 | `public long version()` _(in GatewayCheck.BoxGround)_ |  |
| 138 | 3 | `public long[] box()` _(in GatewayCheck.BoxGround)_ |  |
| 144 | 3 | `static BuildingVisual.Type[] gatewayTypes()` | The game's building templates, as the map draws them. |
| 149 | 4 | `static int highwayType(BuildingVisual.Type[] types)` | The highway type's id. |
| 155 | 4 | `static int trackType(BuildingVisual.Type[] types)` | The first railway line type's id. |
| 161 | 10 | `static CityMap gatewayCity(BuildingVisual.Type[] types, long highway, long track)` | A square city on the default world's dry place with `highway` highways and `track` railway lines, its gateway found. |
| 173 | 14 | `static int bestRun(Gateways.Ground ground, long ci, long cj)` | The longest run of land cells out from a cell, over every bearing - the harness's own look at the same ground. |
| 189 | 54 | `static void spineCase(int gateway, String toward, String opposite, boolean vertical, long siteX, long siteY, long cell)` | Lays a fresh spine toward `gateway` on a square box and proves the arm order: the toward arm first, the opposite arm only when it waits, and the opposite arm taken back first. |
| 244 | 269 | `public static void main(String[] args)` |  |

### FX2: the line to the world on land, the label in view, the undrawn highway (lines 514-768)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 5 | `static String armText(CityRuns.Net n, int a)` | An arm as one line of text: its index, heading in degrees and end, or "none". |
| 528 | 12 | `static int[] fr0Walk(CityMap m, CityRuns.Net n, int a)` | FR0's walk, written out: from net n's arm a's end (the hub while the arm has no steps) along its heading, up to CityMap.DISTRICT plots, to the first plot the city does not own. |
| 542 | 16 | `static int offTrack(World w, CityRuns runs, TrainSchedule.Route r, int[] samples)` | How many of a route's points, read every WORLD_STEP along each leg, lie on neither dry ground nor a plot the railway carries (track, track over a highway, a yard); the points read are in samples[0]. |
| 566 | 89 | `static void worldLinesOnLand(String what, CityMap m)` | The checks every FX2 fixture runs: each of the map's world lines starts on a dry plot the city does not own, at the end of an arm's track within WORLD_GAP plots of it over the city's own dry ground, and runs out over ... |
| 657 | 9 | `static long[] coastCounts(BuildingVisual.Type[] types)` | The coast fixture's counts: the shore's town with every track type's count times COAST_TRACK_TIMES and YARDS_ADDED more of each Rail Terminal. |
| 668 | 22 | `static void coastSection()` | Section 16: the default world's founding site one district north, three districts a side, drawn canonically on its coast. |
| 698 | 24 | `static void coastMonths()` | Section 16's coast over its months (FX2's "over a real run's months": the played city of MapCheck 1 lays no highway and no track in its 120): the same city from nothing, its counts rising evenly to coastCounts() over ... |
| 724 | 44 | `static void halifaxSection()` | Section 17: a square city at Jerus's founding site on the Earth, when earth-1.bin is named. |

### the label (section 18) (lines 769-830)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 4 | `static boolean inView(double x, double y, double w, double h)` | Whether a label whose baseline starts at (x, y) lies inside a w x h view by LandMap.WORLD_LABEL_PAD on every side - the harness's own reading of the rule. |
| 781 | 5 | `static double offPart(double x, double y, double x0, double y0, double x1, double y1)` | How far (x, y) lies from the segment (x0, y0) to (x1, y1), in pixels. |
| 788 | 15 | `static void labelLeaving(String what, double x0, double y0, double x1, double y1)` | One line leaving the view: FR0's spot clips, and worldLabelAt() puts the label whole inside the view, its baseline's left end on the part, at the first place along it, from FR0's spot back, that fits. |
| 805 | 25 | `static void labelSection()` | Section 18: LandMap.worldLabelAt() on pure fixtures. |

### the highway that reaches no street (section 19) (lines 831-955)

| line | len | member | says |
|---:|---:|---|---|
| 834 | 66 | **type** `static final class BoxFill` | A whole-box fill of a map's runs: the plots from (x0, y0), w x h, their codes and marks. |
| 839 | 10 | `BoxFill(CityMap m)` _(in GatewayCheck.BoxFill)_ |  |
| 851 | 3 | `boolean hw(int g)` _(in GatewayCheck.BoxFill)_ | Whether plot index g carries a highway (a railway over one included). |
| 856 | 5 | `long drawn()` _(in GatewayCheck.BoxFill)_ | Highway plots drawn. |
| 863 | 7 | `boolean rampOnCellLine(int g)` _(in GatewayCheck.BoxFill)_ | Whether plot g shows a ramp on a cell line: M_RAMP, and its coordinate along the highway (x on a row, y on a column) a multiple of CityRuns.CELL. |
| 872 | 27 | `long[] pieces()` _(in GatewayCheck.BoxFill)_ | {pieces of highway (8 neighbours), highway plots in a piece with no ramp on a cell line}. |
| 902 | 3 | `static long modelHighwayPlots(CityMap m)` | The highway plots the model has (its Elevated Highways' plots), as MapCheck counts them. |
| 907 | 4 | `static boolean hasHighwayLine(CityMap m)` | Whether a map has a world line for the highways. |
| 913 | 13 | `static void drawnWhole(String what, CityMap m)` | A reaching city's highways as the map draws them: every plot in a piece with a ramp on a cell line, and the drawn plots and the undrawn ones the model's. |
| 928 | 27 | `static void undrawnSection()` | Section 19: gatewayCity() with highways and no track. |


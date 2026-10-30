# Gateways.java - 207 lines · 11 methods · 4 constants · model

`ham/citybuildersim/Gateways.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The city's two gateways - the highway's and the railway's - as one point on
> the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, with
> the longest run of land out from the founding site, and the point at the
> city's radius on it; pure, found once by the map and kept in its runs
> (CityRuns.gateway(), saved in the sidecar).
> 
> WHY THIS EXISTS (batch FR0; spec-freight-and-infrastructure.md 4.1,
> decision D13). Before it a run stopped where the city's owned ground
> stopped and nothing said which way the world was, so the line that carries
> the city's trade to the world did not reach the world. The sea already has
> the mirror of this - SeaRoutes finds the offing, the bearing with the longest
> run of open sea from a berth. This is the land's offing, from the founding
> site itself; on the Earth it is the bearing toward the nearest larger
> place, along land (4.1). The rail gateway shares the highway's bearing.

**Uses:** [SeaRoutes](SeaRoutes.md) (12), [World](World.md) (5), [EarthData](EarthData.md) (2), [EarthTerrain](EarthTerrain.md) (1), [LandMap](LandMap.md) (1)

**Used by (2):** [CityMap](CityMap.md), [GatewayCheck](GatewayCheck.md)

## Sections

| line | section |
|---:|---|
| 47 | THE GROUND |
| 57 | A GATEWAY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `Gateways.BEARING_STEP` | `SeaRoutes.BEARING_STEP` | The bearings tried for a gateway, every this many degrees: 5 (spec 4.1; SeaRoutes.BEARING_STEP). |
| 30 | `Gateways.RAY_CELLS` | `SeaRoutes.RAY_CELLS` | The longest run of land a bearing is measured to, in cells: 400, 192 km (SeaRoutes.RAY_CELLS; the prototype's). |
| 33 | `Gateways.PLACE_CELLS` | `SeaRoutes.RAY_CELLS` | How far the Earth's rule looks for a larger place, in cells: the offing's 400, 192 km (SeaRoutes.RAY_CELLS); only the land on the place's own bearing is read, and only out to the reach. |
| 36 | `Gateways.GATEWAY_M` | `SeaRoutes.OFFING_M` | How far past the city's radius the land must run for a gateway, in metres: 10 km, the offing's own margin (SeaRoutes.OFFING_M), so an island founding with less has no gateway (spec 4.1). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 22 | 186 | **type** `public final class Gateways` | The city's two gateways - the highway's and the railway's - as one point on the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, with the longest run of land out from the founding site, and the point at... |
| 24 | 1 | `private Gateways()` |  |
| 40 | 3 | **type** `public interface Ground` | A ground's cells of SeaRoutes.CELL plots: true where the cell is land, so a harness fixture can cause each condition. |
| 41 | 1 | `boolean land(long ci, long cj)` _(in Gateways.Ground)_ |  |
| 45 | 1 | **type** `public record Gateway(int bearing, long x, long y, int run)` | A gateway: the bearing, the point on the city's edge in world plots, and the run of land cells that won it. |

### THE GROUND (lines 47-56)

| line | len | member | says |
|---:|---:|---|---|
| 52 | 4 | `public static Ground ground(World world)` | The world's ground as a Ground: land is a cell that is not sea in SeaRoutes' grid (off the world is sea, so the world's edge ends a land run). |

### A GATEWAY (lines 57-207)

| line | len | member | says |
|---:|---:|---|---|
| 62 | 3 | `public static Gateway of(World world, long siteX, long siteY, double radius)` | The gateway on the world's ground at the founding site with a city of this radius, in plots: on the Earth, the nearest larger place's way; elsewhere the land rule. |
| 79 | 19 | `public static Gateway of(Ground ground, long siteX, long siteY, double radius)` | The gateway on a ground: the bearing, every BEARING_STEP degrees, with the longest run of land cells out from the founding site's cell, measured the way SeaRoutes.run() measures sea (x = cos, y = sin, up to RAY_CELLS). |
| 106 | 18 | `public static Gateway of(Ground ground, long siteX, long siteY, double radius, List<long[]> larger)` | The Earth's gateway on a ground: the first candidate place in `larger` (nearest the site first) whose bearing - atan2 of its offset, x = cos and y = sin as run() reads it, rounded to the nearest BEARING_STEP and mod 3... |
| 132 | 22 | `public static List<long[]> largerPlaces(World world, long siteX, long siteY)` | The candidate larger places for the Earth's rule, nearest the site first (ties by the data's own order): empty unless the world is the Earth; else every place of its EarthData with a population strictly larger than th... |
| 156 | 4 | `public static int reachCells(double radius)` | The reach, in cells, that the no-gateway rule requires of a land run for a city of this radius: at least one, and never past RAY_CELLS. |
| 168 | 22 | `private static int tieBearing(int[] runs, int bestRun, long siteX, long siteY)` | The tied bearing among the widest arcs of tied bearings: the middle of one chosen by the founding site's hash, floorMod by the number of widest arcs. |
| 192 | 3 | `private static int run(Ground ground, long ci, long cj, int deg)` | The run of land cells out from a cell on a bearing, in cells, up to RAY_CELLS; the world's edge, or any non-land cell, ends it. |
| 197 | 10 | `private static int run(Ground ground, long ci, long cj, int deg, int most)` | ...up to `most` cells. |


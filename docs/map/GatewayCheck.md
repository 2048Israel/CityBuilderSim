# GatewayCheck.java - 466 lines · 12 methods · 0 constants · harnesses

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
> WHY THIS EXISTS: batch FR0 finds the land's mirror of SeaRoutes' offing,
> ... (2 more lines in the source)

**Uses:** [Gateways](Gateways.md) (52), [CityRuns](CityRuns.md) (30), [CityMap](CityMap.md) (22), [World](World.md) (11), [BuildingVisual](BuildingVisual.md) (11), [Founding](Founding.md) (5), [BuildingCatalog](BuildingCatalog.md) (2), [MapCheck](MapCheck.md) (2), [SeaRoutes](SeaRoutes.md) (1)

## Sections

| line | section |
|---:|---|
| 210 | · 1. a strip of land east |
| 226 | · 2. an island founding |
| 236 | · 3. a tie |
| 266 | · 4. a half-plane of land west |
| 273 | · 5. the threshold |
| 284 | · 6. the default world |
| 310 | · 7. twelve real generated worlds |
| 321 | · 8. a full tie is not due east for everyone |
| 338 | · 9. the Earth's rule |
| 356 | · 10. the spine is laid toward the gateway |
| 361 | · 11. no gateway lays as before |
| 379 | · 12. the sidecar keeps it |
| 402 | · 13. what finding one costs |
| 411 | · 14. the world line |
| 444 | · 15. the cache |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `static int fails` |  |
| 83 | `final long x0, y0, x1, y1` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 72 | 395 | **type** `public class GatewayCheck` | A city's gateway is the way the world is. |
| 76 | 4 | `static void check(String label, boolean ok)` |  |
| 82 | 19 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: a harness fixture for the spine. |
| 85 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in GatewayCheck.BoxGround)_ |  |
| 89 | 3 | `public int at(long x, long y, int dir)` _(in GatewayCheck.BoxGround)_ |  |
| 93 | 3 | `public long version()` _(in GatewayCheck.BoxGround)_ |  |
| 97 | 3 | `public long[] box()` _(in GatewayCheck.BoxGround)_ |  |
| 103 | 3 | `static BuildingVisual.Type[] gatewayTypes()` | The game's building templates, as the map draws them. |
| 108 | 4 | `static int highwayType(BuildingVisual.Type[] types)` | The highway type's id. |
| 114 | 4 | `static int trackType(BuildingVisual.Type[] types)` | The first railway line type's id. |
| 120 | 10 | `static CityMap gatewayCity(BuildingVisual.Type[] types, long highway, long track)` | A square city on the default world's dry place with `highway` highways and `track` railway lines, its gateway found. |
| 132 | 14 | `static int bestRun(Gateways.Ground ground, long ci, long cj)` | The longest run of land cells out from a cell, over every bearing - the harness's own look at the same ground. |
| 148 | 54 | `static void spineCase(int gateway, String toward, String opposite, boolean vertical, long siteX, long siteY, long cell)` | Lays a fresh spine toward `gateway` on a square box and proves the arm order: the toward arm first, the opposite arm only when it waits, and the opposite arm taken back first. |
| 203 | 263 | `public static void main(String[] args)` |  |


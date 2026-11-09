# IslandRailCheck.java - 535 lines · 27 methods · 6 constants · harnesses

`ham/citybuildersim/IslandRailCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The railway has a piece of its own on each piece of the city's ground the water parts, its track dealt by built weight and its yards on the
> islands (RI1).
> 
> WHAT THIS HAS TO PROVE (the sections below are numbered to match):
> 
>   1. EACH LINKED ISLAND HAS A RAILWAY HUB AND TRACK OF ITS OWN. A home square and two islands (A, B) parted by sea, a Pieces of three,
>      and a dry square C the runs' ground does not own (an unlinked island: not owned, so in no piece). With CityRuns.islandRail off the
>      islands have highways and the railway is one piece with no track off home (the premise: the fault); on, each island has a rail hub -
>      a railway line crossing (RAIL_AT mod CELL in both coordinates) on its own dry ground, the nearest to the piece's anchor - and track
>      from it, every rail plot is on owned dry ground, and C has none.
> 
>   2. THE TRACK IS DEALT BY WEIGHT AS THE HIGHWAYS ARE. With no yards each island's track is the floor of the model's track times its weight
>      over the total (the home piece counted at least 1), the home piece the rest - against the rule's own formula fed the fixture's
>      weights; a month whose weights moved and whose track did not (the ground's version moved, so the railway is laid again) moves
>      nothing; more track grows every piece to its new share; less track takes plots back from the newest end (every arm laid is a prefix
>      of what it was) to the new shares and no further.
> 
>   3. YARDS. The model asks 3 yards: yard 0 at home beside the mine, yards 1 and 2 on A's and B's own track, nearest their hubs (their
>      middles on their pieces); an island with a yard has its line grown to its edges, till every arm waits, as a station's line is at home;
>      the freight station is the home yard though an island yard is nearer the main line's end (the premise: the fixture causes it), and
>      wayTo() of an island yard is null (the home yard's is not).
> 
>   4. SIDINGS. A bulk works on island A within SIDING_REACH of A's line gets a siding from A's line, every plot of it on A's dry ground;
>      with islandRail off (the premise) A has no line and the works gets none.
> 
>   5. A PURCHASE JOINING AN ISLAND TO HOME FREEZES ITS RAILWAY. A is joined to the home piece (B becomes the map's second piece): the month
>      it joins moves nothing; as the track grows, A's rail net is exactly as it was and home goes on growing, B's share dealt on.
> 
>   6. ONE PIECE IS THE RAILWAY AS IT WAS. On a ground in one piece (ONE_PIECE) islandRail on and off give the same signature() and the same
>      rail plots, yards and sidings.
> 
>   7. THE SIDECAR KEEPS EVERY PIECE'S RAILWAY. Runs with island rail, yards and a siding, written and read back, are same(), with the
>      pieces' hubs and counts and the corridors' pieces and roles, fill a box alike, and laid again to the same inputs move nothing.
> 
> WHY THIS EXISTS: CL5 gave every piece of the city's ground the water parts a highway net of its own and left the railway the home
> piece's alone, so a city built on islands had no track and no yard on any but the founding site's (Jerus ticked "Rail on islands",
> 2026-10-31: CL5-C's call 1, "the railway on other islands: its own track and yards from each piece's hub"). A fixture CAUSES each case:
> a ground of islands, weights, a mine, a works, a purchase.

**Uses:** [CityRuns](CityRuns.md) (91), [IslandNetCheck](IslandNetCheck.md) (25), [CityMap](CityMap.md) (3), [Pieces](Pieces.md) (1)

## Sections

| line | section |
|---:|---|
| 214 | 1. each linked island has a railway hub and track of its own |
| 251 | 2. the track is dealt by weight as the highways are |
| 308 | 3. yards |
| 384 | 4. sidings |
| 422 | 5. a purchase joining an island to home freezes its railway |
| 461 | 6. one piece is the railway as it was |
| 490 | 7. the sidecar |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `IslandRailCheck.SITE` | `IslandNetCheck.SITE` |  |
| 66 | `IslandRailCheck.ISLE` | `150` | An island's half-side in plots. |
| 69 | `IslandRailCheck.GAP_FAR` | `CityRuns.SIDING_REACH + CityRuns.CELL, GAP_NEAR = 3L * CityRuns.CELL` | Sea between the home square and an island that is wider than a siding reaches (section 4), and one an island's yard is nearer the main line's end than the home yard is (section 3). |
| 72 | `IslandRailCheck.WEIGHTS` | `{ 3, 2, 1 }` | The pieces' built weights: home, A, B. |
| 149 | `IslandRailCheck.NO_YARDS` | `new ArrayList<>()` |  |
| 150 | `IslandRailCheck.NONE` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |
| 76 | `final long[] weights` |  |
| 77 | `final long[][] anchors` |  |
| 78 | `final List<Land.Dry> lands` |  |
| 80 | `boolean aJoined` | A purchase joined island A to the home piece. |
| 110 | `final long gap` |  |
| 111 | `final Land.Dry home, isleA, isleB` |  |
| 112 | `final long[] boxHome, boxA, boxB, boxC, anchorA, anchorB` |  |
| 113 | `final Land sea` |  |
| 114 | `final Sea pieces` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 482 | **type** `public class IslandRailCheck` | The railway has a piece of its own on each piece of the city's ground the water parts, its track dealt by built weight and its yards on the islands (RI1). |
| 58 | 4 | `static void check(String label, boolean ok)` |  |
| 75 | 32 | **type** `static final class Sea implements CityRuns.Pieces` | The city's ground in three pieces, and in two once a purchase joins A to home: B is then the map's second piece. |
| 82 | 5 | `Sea(long[] weights, long[][] anchors, Land.Dry...lands)` _(in IslandRailCheck.Sea)_ |  |
| 88 | 1 | `public int count()` _(in IslandRailCheck.Sea)_ |  |
| 90 | 6 | `public int pieceOf(long x, long y)` _(in IslandRailCheck.Sea)_ |  |
| 97 | 4 | `public long weight(int p)` _(in IslandRailCheck.Sea)_ |  |
| 102 | 4 | `public long[] hub(int p)` _(in IslandRailCheck.Sea)_ |  |
| 109 | 39 | **type** `static final class Fix` | The fixture: a home square with island A east of it and island B west, `gap` plots of sea between, and C, a dry square north that nobody owns. |
| 116 | 19 | `Fix(long gap)` _(in IslandRailCheck.Fix)_ |  |
| 137 | 6 | `CityRuns lay(long highway, long track, List<int[]> yards, int spurs, List<long[]> mines, List<long[]> works)` _(in IslandRailCheck.Fix)_ | A fresh runs for a city whose world line leaves east, laid to `track` (and `highway`) with the yards, spurs, mines and works given. |
| 144 | 3 | `void again(CityRuns runs, long highway, long track, List<int[]> yards, int spurs, List<long[]> mines, List<long[]> works)` _(in IslandRailCheck.Fix)_ |  |
| 153 | 3 | `static long in(CityRuns.Net n, long[] box)` | The plots of the net's in the box {x0, y0, x1, y1}. |
| 158 | 3 | `static boolean inBox(long x, long y, long[] box)` | Whether plot (x, y) is in the box {x0, y0, x1, y1}. |
| 163 | 3 | `static long share(long track, long w, long total)` | The rule's share of `track` for a piece of weight w among weights summing to `total` (CityRuns.deal(): floor(track * w / total)). |
| 168 | 3 | `static long total(long[] w)` | The weights' total, the home piece counted at least 1. |
| 173 | 3 | `static double boxDistance(long[] box, long x, long y)` | The distance from plot (x, y) to the box {x0, y0, x1, y1}: 0 inside it. |
| 178 | 12 | `static double nearestCrossing(Land sea, long[] box, long ax, long ay)` | The distance from (x, y) to the nearest railway line crossing on the dry ground of box (its corners and sides are dry: the fixture's islands are squares). |
| 192 | 4 | `static boolean armsWait(CityRuns.Net n, int p)` | The plots of piece p of the net's that are a SPINE arm's of that piece, all four arms' ends: each arm of the piece's corridors waits (stoppedBy is not OPEN). |
| 197 | 16 | `public static void main(String[] args)` |  |

### 1. each linked island has a railway hub and track of its own (lines 214-250)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 35 | `static void section1()` |  |

### 2. the track is dealt by weight as the highways are (lines 251-307)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 55 | `static void section2()` |  |

### 3. yards (lines 308-383)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 58 | `static void section3()` |  |
| 369 | 3 | `static long[] boxOf(Fix f, int p)` | Piece p's square of ground. |
| 374 | 4 | `static boolean sameArms(CityRuns.Net a, CityRuns.Net b)` | Whether two nets have the same arms, stretch for stretch. |
| 380 | 3 | `static long trackIn(CityRuns.Net rail, long[] y)` | The rail plots inside a yard's box. |

### 4. sidings (lines 384-421)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 36 | `static void section4()` |  |

### 5. a purchase joining an island to home freezes its railway (lines 422-460)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 37 | `static void section5()` |  |

### 6. one piece is the railway as it was (lines 461-489)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 27 | `static void section6()` |  |

### 7. the sidecar (lines 490-535)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 44 | `static void section7()` |  |


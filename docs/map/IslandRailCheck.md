# IslandRailCheck.java - 1,004 lines · 46 methods · 9 constants · harnesses

`ham/citybuildersim/IslandRailCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

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
>      wayTo() of an island yard is null (the home yard's is not). Yard 0 is home's though a mine on island A beside its line puts A's box
>      first in the nearest-a-mine order (the premise), and it is the freight station.
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
>   8. THE ISLAND LINKED BY A PORT (a real map; RI1 slice B, ET5's "a linked island's hub at its port"). A real CityMap through a Game: Rotterdam's
>      island (648) bought, a General Cargo Terminal ordered on it and a second on home's coast, the city given a railway (two Rail Terminals and
>      Freight Lines handed to it), and home filled so that the houses once the island is linked are drawn there. The island's piece has the
>      terminal for its anchor - the middle of its box; its highway hub and its rail hub are the line crossings nearest the terminal (against a
>      brute force over the piece's tiles) and nearer it than the crossing nearest the founding site was (the old rule, the premise); the island has
>      track of its own, all on its ground, and a yard once the model has a second; a piece with no terminal (a second parcel of the island's, apart)
>      has its hub nearest the founding site, and the home piece's anchor is its hub. Needs the Earth fixture (EarthCheck.FIXTURE): without it the
>      sections 8 and 9 say they were skipped and count no fail.
> 
>   9. NO PORT, NO ANCHOR; UNLINKED, NOTHING. The same city before the link, the terminal on the island and none at home: no piece holds the island
>      and no rail or highway plot lies on it (ET5's rule), the railway is the home piece's alone (a premise: it has track and a yard at home), and
>      the home piece's anchor is its hub.
> 
>   10. A BRIDGE-LINKED ISLAND ON REAL MAP GROUND keeps the founding-side hub (RI1 call 3), with its own track and yard.
>      BridgeMapCheck's real channel is wider than a short crossing: only the bridge lays track across its water.
> 
> WHY THIS EXISTS: CL5 gave every piece of the city's ground the water parts a highway net of its own and left the railway the home
> piece's alone, so a city built on islands had no track and no yard on any but the founding site's (Jerus ticked "Rail on islands",
> 2026-10-31: CL5-C's call 1, "the railway on other islands: its own track and yards from each piece's hub"). A fixture CAUSES each case:
> a ground of islands, weights, a mine, a works, a purchase.

**Uses:** [CityRuns](CityRuns.md) (113), [World](World.md) (49), [LandmassCheck](LandmassCheck.md) (45), [IslandNetCheck](IslandNetCheck.md) (25), [CityMap](CityMap.md) (24), [BridgeMapCheck](BridgeMapCheck.md) (11), [Game](Game.md) (7), [CityShore](CityShore.md) (7), [Landmasses](Landmasses.md) (4), [EarthCheck](EarthCheck.md) (3), [HaulageCheck](HaulageCheck.md) (3), [BuildingVisual](BuildingVisual.md) (3), [CityLand](CityLand.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [Pieces](Pieces.md) (1), [EarthTerrain](EarthTerrain.md) (1), [EarthData](EarthData.md) (1), [Founding](Founding.md) (1)

**Used by (1):** [IslandRailRenders](IslandRailRenders.md)

## Sections

| line | section |
|---:|---|
| 234 | 1. each linked island has a railway hub and track of its own |
| 271 | 2. the track is dealt by weight as the highways are |
| 328 | 3. yards |
| 425 | 4. sidings |
| 463 | 5. a purchase joining an island to home freezes its railway |
| 502 | 6. one piece is the railway as it was |
| 531 | 7. the sidecar |
| 577 | the map-level fixture: a real CityMap of the two-landmass city (sections 8 and 9) |
| 853 | 8. the island linked by a port |
| 937 | 9. no port, no anchor; unlinked, nothing |
| 951 | 10. a bridge-linked piece on real map ground (FB12, RI1-F) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 80 | `IslandRailCheck.SITE` | `IslandNetCheck.SITE` |  |
| 83 | `IslandRailCheck.ISLE` | `150` | An island's half-side in plots. |
| 86 | `IslandRailCheck.GAP_FAR` | `CityRuns.SIDING_REACH + CityRuns.CELL, GAP_NEAR = 3L * CityRuns.CELL` | Sea between the home square and an island that is wider than a siding reaches (section 4), and one an island's yard is nearer the main line's end than the home yard is (section 3). |
| 89 | `IslandRailCheck.WEIGHTS` | `{ 3, 2, 1 }` | The pieces' built weights: home, A, B. |
| 166 | `IslandRailCheck.NO_YARDS` | `new ArrayList<>()` |  |
| 167 | `IslandRailCheck.NONE` | `new ArrayList<>()` |  |
| 580 | `IslandRailCheck.YARDS` | `2, LINES = 12, HIGHWAYS = 12` | Yards (Rail Terminals) and track (Freight Lines) handed to the city of sections 8 and 9: a second yard for an island, and track enough for each piece to be dealt some. |
| 583 | `IslandRailCheck.ISLAND_SQUARES` | `3` | The side, in squares of 64 plots, of the block of the island the city buys (its port's square is the north-west one): 3. |
| 586 | `IslandRailCheck.LINK_MONTHS` | `36` | Months after the link the fixture gives the island's piece to have weight and a railway hub. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `static int fails` |  |
| 93 | `final long[] weights` |  |
| 94 | `final long[][] anchors` |  |
| 95 | `final List<Land.Dry> lands` |  |
| 97 | `boolean aJoined` | A purchase joined island A to the home piece. |
| 127 | `final long gap` |  |
| 128 | `final Land.Dry home, isleA, isleB` |  |
| 129 | `final long[] boxHome, boxA, boxB, boxC, anchorA, anchorB` |  |
| 130 | `final Land sea` |  |
| 131 | `final Sea pieces` |  |
| 590 | `World w` |  |
| 591 | `LandmassCheck.City c` |  |
| 593 | `LandmassCheck.Square isle, farIsle, homeSea` | The island's square with a shore (the port's), a second parcel of the island apart from it (no terminal), and a square of home's open coast. |
| 595 | `long[] box, farBox, seaBox` | The squares' boxes {x0, y0, x1, y1}, in plots. |
| 596 | `boolean found, homeLandFound` |  |
| 598 | `boolean islandPort, unlinked, railed` | What the map was before the link (section 9). |
| 599 | `int piecesBefore, islandPieceBefore, farPieceBefore, seaPieceBefore` |  |
| 600 | `long railPlotsBefore, railOnIsland, highwaysOnIsland` |  |
| 601 | `int railPiecesBefore, yardsBefore` |  |
| 602 | `boolean homeAnchorBefore` |  |
| 604 | `boolean linkOrdered, linked, settled` | And after it (section 8). |
| 607 | `static Port port` |  |
| 608 | `static boolean portTried` |  |
| 682 | `public Game game` |  |
| 683 | `public CityMap map` |  |
| 685 | `public int piece, railPiece` | The island's piece on the map (CityMap's pieces), and its piece in the railway net (CityRuns.Net.pieceHub, piecePlots). |
| 687 | `public long[] mid, railHub` | The berthed terminal's box middle {x0 + w/2, y0 + h/2}, in plots, and the island's railway hub {x, y}. |
| 689 | `public List<long[]> tiles` | The tiles {tx, ty} of the island's square the piece holds. |
| 691 | `public List<long[]> yards` | The yards {x, y, w, h} the railway has on the island's piece. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 934 | **type** `public class IslandRailCheck` | The railway has a piece of its own on each piece of the city's ground the water parts, its track dealt by built weight and its yards on the islands (RI1). |
| 75 | 4 | `static void check(String label, boolean ok)` |  |
| 92 | 32 | **type** `static final class Sea implements CityRuns.Pieces` | The city's ground in three pieces, and in two once a purchase joins A to home: B is then the map's second piece. |
| 99 | 5 | `Sea(long[] weights, long[][] anchors, Land.Dry...lands)` _(in IslandRailCheck.Sea)_ |  |
| 105 | 1 | `public int count()` _(in IslandRailCheck.Sea)_ |  |
| 107 | 6 | `public int pieceOf(long x, long y)` _(in IslandRailCheck.Sea)_ |  |
| 114 | 4 | `public long weight(int p)` _(in IslandRailCheck.Sea)_ |  |
| 119 | 4 | `public long[] hub(int p)` _(in IslandRailCheck.Sea)_ |  |
| 126 | 39 | **type** `static final class Fix` | The fixture: a home square with island A east of it and island B west, `gap` plots of sea between, and C, a dry square north that nobody owns. |
| 133 | 19 | `Fix(long gap)` _(in IslandRailCheck.Fix)_ |  |
| 154 | 6 | `CityRuns lay(long highway, long track, List<int[]> yards, int spurs, List<long[]> mines, List<long[]> works)` _(in IslandRailCheck.Fix)_ | A fresh runs for a city whose world line leaves east, laid to `track` (and `highway`) with the yards, spurs, mines and works given. |
| 161 | 3 | `void again(CityRuns runs, long highway, long track, List<int[]> yards, int spurs, List<long[]> mines, List<long[]> works)` _(in IslandRailCheck.Fix)_ |  |
| 170 | 3 | `static long in(CityRuns.Net n, long[] box)` | The plots of the net's in the box {x0, y0, x1, y1}. |
| 175 | 3 | `static boolean inBox(long x, long y, long[] box)` | Whether plot (x, y) is in the box {x0, y0, x1, y1}. |
| 180 | 3 | `static long share(long track, long w, long total)` | The rule's share of `track` for a piece of weight w among weights summing to `total` (CityRuns.deal(): floor(track * w / total)). |
| 185 | 3 | `static long total(long[] w)` | The weights' total, the home piece counted at least 1. |
| 190 | 3 | `static double boxDistance(long[] box, long x, long y)` | The distance from plot (x, y) to the box {x0, y0, x1, y1}: 0 inside it. |
| 195 | 12 | `static double nearestCrossing(Land sea, long[] box, long ax, long ay)` | The distance from (x, y) to the nearest railway line crossing on the dry ground of box (its corners and sides are dry: the fixture's islands are squares). |
| 209 | 4 | `static boolean armsWait(CityRuns.Net n, int p)` | The plots of piece p of the net's that are a SPINE arm's of that piece, all four arms' ends: each arm of the piece's corridors waits (stoppedBy is not OPEN). |
| 214 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1. each linked island has a railway hub and track of its own (lines 234-270)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 35 | `static void section1()` |  |

### 2. the track is dealt by weight as the highways are (lines 271-327)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 55 | `static void section2()` |  |

### 3. yards (lines 328-424)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 79 | `static void section3()` |  |
| 410 | 3 | `static long[] boxOf(Fix f, int p)` | Piece p's square of ground. |
| 415 | 4 | `static boolean sameArms(CityRuns.Net a, CityRuns.Net b)` | Whether two nets have the same arms, stretch for stretch. |
| 421 | 3 | `static long trackIn(CityRuns.Net rail, long[] y)` | The rail plots inside a yard's box. |

### 4. sidings (lines 425-462)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 36 | `static void section4()` |  |

### 5. a purchase joining an island to home freezes its railway (lines 463-501)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 37 | `static void section5()` |  |

### 6. one piece is the railway as it was (lines 502-530)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 27 | `static void section6()` |  |

### 7. the sidecar (lines 531-576)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 44 | `static void section7()` |  |

### the map-level fixture: a real CityMap of the two-landmass city (sections 8 and 9) (lines 577-852)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 17 | **type** `static final class Port` | The city of sections 8 and 9, built once: LandmassCheck's section 10 city (Rotterdam, the island of 648, home's open coast), played through its unlinked month and its linked months. |
| 611 | 68 | `static Port port() throws Exception` | The Earth fixture's world and the squares, or null when the fixture is not beside the classes. |
| 681 | 12 | **type** `public static final class PortIsland` | What a tool needs of section 8's city to draw it (IslandRailRenders): the game, its map, the island's map piece and rail-net piece, the terminal's box middle, the island's rail hub, its tiles and its yards. |
| 695 | 26 | `public static PortIsland portIsland() throws Exception` | Section 8's city (Rotterdam, the island of 648 linked by its General Cargo Terminal, a railway with yards, buildings on the island) for a tool, built once as port() builds it; null where the Earth fixture is not besid... |
| 723 | 4 | `static boolean owned(CityLand land, LandmassCheck.Square s)` | Whether the city owns a plot of the square (every fourth plot looked at). |
| 729 | 8 | `static void fillHome(LandmassCheck.City c, BuildingsTemplate house, double left)` | Houses ordered at once until the linked ground free is below `left` square feet. |
| 739 | 5 | `static boolean settled(LandmassCheck.City c)` | Whether the island's piece has weight and a railway hub and a highway hub (a net of each on it) on the map the city has now. |
| 746 | 12 | `static int pieceNearest(CityMap map, long[] box, long x, long y)` | The map piece of the nearest land tile (squared tile distance, then row, then column) in the box to plot (x, y): a loop over the box's tiles, not the model's ring search; -1 for none. |
| 760 | 7 | `static int islandPiece(CityMap map, Port p)` | The piece of the island the oldest berthed terminal on it stands on (pieceNearest to its box middle); -1 for none. |
| 769 | 12 | `static int pieceOnIsland(CityMap map, World w, long[] box)` | The map piece that holds a tile of the box any sample of which (the model's four) reads the island's landmass, 648; -1 for none: what no piece holds while no port links the island. |
| 783 | 9 | `static int pieceIn(CityMap map, long[] box)` | The map piece that holds a tile of the box {x0, y0, x1, y1} (plots, tile-aligned); -1 for none. |
| 794 | 9 | `static List<long[]> tilesOf(CityMap map, int p, long[] box)` | The tiles {tx, ty} of the box that map piece p holds. |
| 805 | 4 | `static boolean ownedDry(CityMap map, long x, long y)` | Whether plot (x, y) is owned dry ground to the runs. |
| 811 | 11 | `static long[] nearestHighway(CityMap map, List<long[]> tiles, long rtx, long rty)` | The highway line crossing of the tile of `tiles` nearest tile (rtx, rty) - squared tile distance, then row, then column, as MapPieces keys them - whose crossing is owned dry ground; null for none. |
| 824 | 11 | `static long[] nearestRail(CityMap map, List<long[]> tiles, long ax, long ay)` | The railway line crossing of the tiles nearest plot (ax, ay) that is owned dry ground; null for none. |
| 837 | 7 | `static int netOf(CityRuns.Net n, CityMap map, int m)` | The net's piece (its index among the net's hubs) whose hub lies on map piece m; -1 for none. |
| 845 | 3 | `static double dist(long[] a, long[] b)` |  |
| 849 | 3 | `static void skipped()` |  |

### 8. the island linked by a port (lines 853-936)

| line | len | member | says |
|---:|---:|---|---|
| 854 | 82 | `static void section8() throws Exception` |  |

### 9. no port, no anchor; unlinked, nothing (lines 937-950)

| line | len | member | says |
|---:|---:|---|---|
| 938 | 12 | `static void section9() throws Exception` |  |

### 10. a bridge-linked piece on real map ground (FB12, RI1-F) (lines 951-1004)

| line | len | member | says |
|---:|---:|---|---|
| 954 | 50 | `static void section10()` | BridgeMapCheck's real channel, with track and two yards: only a bridge crosses the water. |


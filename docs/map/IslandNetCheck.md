# IslandNetCheck.java - 833 lines · 32 methods · 1 constants · harnesses

`ham/citybuildersim/IslandNetCheck.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> A highway net never stops while the city has ground, and a net is laid on every piece of ground the water parts (CL5).
> 
> WHAT THIS HAS TO PROVE (the sections below are numbered to match):
> 
>   1. A STUCK NET OPENS ITS NEXT CORRIDOR BY A LINK ARM. A ground whose four
>      spine arms stop at its edges short of APART, no line crossing the net,
>      and more of the city's dry ground a line away that a link reaches: with
>      CityRuns.linkArms off the highways stall (highwayShort() > 0: the
>      premise, the fixture causes it); with it on a LINK corridor opens, its
>      link leaves the net at its start and stands on its line at its end, the
>      corridor's two line arms open there, every plot is on owned dry ground,
>      and the model's plots are all laid. No link is a stub: each stands on
>      its line, or is the piece's newest corridor still growing.
> 
>   1b. A LINK FINDS ITS WAY ROUND WATER. Two dry squares joined by a neck a
>      few plots wide that is on neither the hub's row nor its column, sea
>      round them all: with link arms off the highways stall (the premise);
>      with them on the link leaves the net, runs through the neck in a few
>      legs and stands on the line beyond it, the second link goes on to the
>      far square, every plot is on dry owned ground, nothing is a stub, and
>      the model's plots are all laid.
> 
>   2. TWO ISLANDS EACH WITH BUILDINGS GET A NET EACH, NONE ACROSS THE SEA.
>      Two dry squares parted by sea (SALT, which no arm crosses) and a Pieces
>      stub of two pieces, weights 3:1: each has highway plots, the split is
>      the shares (floor(T * weight / total) for the far piece, the home
>      piece the rest; a piece may be a plot or two under its share where a
>      45-degree step would lay two), no highway plot is on salt or unowned
>      ground, highwayShort() is 0. With ONE_PIECE on the same ground the far
>      island has none (the premise: today's fault). A piece that cannot hold
>      its share is made up by the other (the deal's D).
> 
>   3. GROWTH AND TRIM ARE NEWEST-END, PIECE BY PIECE (H4). The plots grown in
>      steps and taken back in steps: each month's arms are the next month's
>      (nothing laid moves) while growing and the reverse while trimming; the
>      far piece's hub plot is the last of its piece to go.
> 
>   4. A PURCHASE JOINING TWO PIECES MOVES NOTHING. Section 2's ground, then a
>      land bridge between the islands and the stub reporting one piece: the
>      month it joins moves nothing; as the plots grow every stretch laid is
>      still there, the younger net frozen, the home net grown on.
> 
>   5. THE SIDECAR KEEPS EVERY PIECE'S NET. Written and read at the current
>      FORMAT it is same() and fills a box alike, with its pieces' hubs,
>      corridors' pieces and roles and piece counts; a net of one piece
>      written at FORMAT 11 and read back is same() as it was. A net with a
>      link part-grown, written and read back, plans its way again (the plan
>      is never saved) and grows on to its line.
> 
>   6. NOTHING ELSE MOVED. On a ground where the rule never sticks (a large
>      square) layTo() with ONE_PIECE gives the same stretches() and
>      signature() with the link arms on and off.
> 
>   7. THE MAP FINDS ITS PIECES AND EVERY BUILT PIECE HAS A HIGHWAY. A real
>      CityMap (CityMap.canonical()) on a land of three islands of 2 x 2
>      districts, dry in the default world and a district of unowned ground
>      apart, buildings dealt over them: its MapPieces finds three pieces, the
>      home one (the founding site's) 0 and the others by their first tile;
>      their weights are the plots the buildings stand on, worked again from
>      the districts; their hubs the tile nearest the site; the ground is
> ... (15 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (187), [CityMap](CityMap.md) (31), [World](World.md) (22), [MapCheck](MapCheck.md) (11), [BuildingVisual](BuildingVisual.md) (7), [CityLand](CityLand.md) (7), [Pieces](Pieces.md) (3), [LandParcel](LandParcel.md) (2), [BuildingCatalog](BuildingCatalog.md) (1), [Founding](Founding.md) (1), [GatewayCheck](GatewayCheck.md) (1), [Timing](Timing.md) (1)

## Sections

| line | section |
|---:|---|
| 247 | · 1. a stuck net opens its next corridor by a link arm |
| 305 | · 1b. a link finds its way round water |
| 379 | · 2. two islands each with buildings get a net each, none across the sea |
| 425 | · 3. growth and trim are newest-end, piece by piece (H4) |
| 457 | · 4. a purchase joining two pieces moves nothing |
| 483 | · 5. the sidecar keeps every piece's net |
| 551 | · 6. nothing else moved |
| 575 | · 7. the map finds its pieces and every built piece has a highway |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `IslandNetCheck.SITE` | `128L * CityRuns.CELL + CityRuns.HIGHWAY_AT` | The founding site: a highway line crossing, so that the hub is the site itself. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `static int fails` |  |
| 105 | `final long x0, y0, x1, y1` |  |
| 106 | `final boolean seaAround` |  |
| 107 | `final List<Dry> lands` |  |
| 108 | `long version` |  |
| 142 | `int count` |  |
| 143 | `final long[] weights` |  |
| 144 | `final long[][] hubs` |  |
| 145 | `final List<Land.Dry> pieces` |  |
| 147 | `boolean joined` | After a purchase joined them: every owned dry plot is piece 0. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 89 | 745 | **type** `public class IslandNetCheck` | A highway net never stops while the city has ground, and a net is laid on every piece of ground the water parts (CL5). |
| 93 | 4 | `static void check(String label, boolean ok)` |  |
| 102 | 37 | **type** `static final class Land implements CityRuns.Ground` | A ground: dry (World.GRASS) where one of its pieces of land says so, salt (World.SALT) where it says sea inside its box, unowned (-1) elsewhere. |
| 103 | 1 | **type** `interface Dry` _(in IslandNetCheck.Land)_ |  |
| 103 | 1 | `boolean at(long x, long y)` _(in IslandNetCheck.Land.Dry)_ |  |
| 110 | 4 | `Land(long x0, long y0, long x1, long y1, boolean seaAround)` _(in IslandNetCheck.Land)_ |  |
| 115 | 4 | `Land add(Dry d)` _(in IslandNetCheck.Land)_ |  |
| 121 | 3 | `static Dry rect(long x0, long y0, long x1, long y1)` _(in IslandNetCheck.Land)_ | Ground owned in the plots x0..x1 by y0..y1 (inclusive). |
| 125 | 5 | `public int at(long x, long y, int dir)` _(in IslandNetCheck.Land)_ |  |
| 131 | 3 | `public long version()` _(in IslandNetCheck.Land)_ |  |
| 135 | 3 | `public long[] box()` _(in IslandNetCheck.Land)_ |  |
| 141 | 26 | **type** `static final class Isles implements CityRuns.Pieces` | A Pieces stub: the city's ground in the pieces a test says. |
| 149 | 6 | `Isles(long[] weights, long[][] hubs, Land.Dry...pieces)` _(in IslandNetCheck.Isles)_ |  |
| 156 | 1 | `public int count()` _(in IslandNetCheck.Isles)_ |  |
| 158 | 4 | `public int pieceOf(long x, long y)` _(in IslandNetCheck.Isles)_ |  |
| 163 | 1 | `public long weight(int p)` _(in IslandNetCheck.Isles)_ |  |
| 165 | 1 | `public long[] hub(int p)` _(in IslandNetCheck.Isles)_ |  |
| 169 | 5 | `static long coveredIn(CityRuns.Net n, long x0, long y0, long x1, long y1)` | The highway plots the net covers in the box x0..x1 by y0..y1, counted plot by plot. |
| 176 | 7 | `static long offGround(CityRuns.Net n, Land g)` | The highway plots the net covers inside the box that are not owned dry ground. |
| 185 | 6 | `static CityRuns lay(Land g, long plots, CityRuns.Pieces pieces)` | A fresh runs for a city with no gateway (the spine's order of hw.py's), laid to `plots` highways with the pieces given. |
| 193 | 3 | `static void again(CityRuns runs, Land g, long plots, CityRuns.Pieces pieces)` | ...laid again to `plots`. |
| 198 | 12 | `static Map<String, List<long[]>> armsOf(CityRuns.Net n)` | The net's arms by start and heading, each as its stretches {x0, y0, heading, steps}: a key can stand for the arm that has no stretches. |
| 212 | 14 | `static boolean prefix(Map<String, List<long[]>> small, Map<String, List<long[]>> big)` | Whether every arm of `small` is a prefix of the same arm of `big`: all its stretches the same but the last, which only grows. |
| 228 | 7 | `static int corridorOf(CityRuns.Net n, int role, boolean reaching)` | The first corridor of a role, or -1. |
| 237 | 5 | `static int corridorsOf(CityRuns.Net n, int role)` | How many corridors of a role the net has. |
| 243 | 339 | `public static void main(String[] args)` |  |
| 584 | 3 | `static long[] crossingOf(long tx, long ty)` | The highway line crossing of tile (tx, ty). |
| 589 | 11 | `static boolean dryDistrict(World world, long dx, long dy, Map<Long, Boolean> seen)` | Whether the district (dx, dy) of the world is dry at every plot of a stride of 8 (the plots 8 and 24 into each tile among them: the tile rule's samples). |
| 602 | 15 | `static long[] dryStrip(World world, long[] place, int w, int h)` | The north-west district {column, row} of a strip of w x h dry districts nearest the district of `place`, or null. |
| 619 | 3 | `static long coveredOn(CityRuns.Net n, long[] box)` | The highway plots the net covers on an island {x0, y0, x1, y1} (exclusive upper ends), counted plot by plot. |
| 624 | 140 | `static void mapPieces()` | Section 7: a CityMap on a land of three islands, the pieces it finds, the nets the runs lay on them, a purchase that joins two, and what the pieces cost. |
| 766 | 25 | `static void timePieces(BuildingVisual.Type[] types, long seed)` | What the pieces cost on a city of billions (MapCheck section 5's copy, 5 billion people): the first flood and one purchase's update. |
| 792 | 5 | `private static int countWeighted(CityMap.MapPieces mp)` |  |
| 799 | 3 | `static boolean linkDirect(CityRuns.Arm link)` | Whether the link wasted no step: its steps are the plots between its start and its end, the larger of its two distances (a step moves one plot each way at most). |
| 804 | 10 | `static boolean linkJoined(CityRuns.Net n, CityRuns.Arm link)` | Whether the link is one unbroken way: it starts on a plot of the net that is not its own, every stretch starts where the one before it ended, and the last ends where the arm stands. |
| 819 | 14 | `static int stubs(CityRuns.Net n)` | How many LINK corridors are stubs: a link that does not stand on its line and is not the newest corridor of its piece (still growing), or has no step and is not the newest. |


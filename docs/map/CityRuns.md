# CityRuns.java - 4,242 lines · 214 methods · 48 constants · model

`ham/citybuildersim/CityRuns.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map draws them from and keeps in its sidecar.
> 
> WHY THIS EXISTS (0.7.89, batch RD3; the project's spec-roads-and-ports.md
> 2.7 and 2.8, a port of its prototype roads-prototype/hw.py). From 0.7.72 to
> 0.7.88 each district laid its own highway and track from its first road
> tile's hub, both ways and across itself once (CityMap's THE NETWORK): a
> city's six highways were six stubs, each crossing itself - the dark + with
> buildings inside its arms in Jerus's screenshot, and at 0.7.88 a short
> highway floating inside a block. Jerus: "highways that prefer going
> straight and must be connected". Here a NET (the highways, or the railway)
> is laid city-wide:
> 
>   LINES     a highway rides the street line through the middle of a cell,
>             HIGHWAY_AT plots in, so it never takes an arterial's row; the
>             railway the line a quarter in, RAIL_AT, so it never shares a row
>             with a highway;
>   SPINE     from its HUB - the highways' line crossing at the founding site,
>             the railway's at the city's mine nearest it (spec 2.8: "laid as
>             runs from the rail terminals, which sit in estate cells nearest
>             the mines") - four ARMS along its row and its column; then
>             parallel CORRIDORS one district (APART) apart, nearest the hub
>             first, each starting where it crosses the net already laid on
>             the city's ground (an interchange);
>   GROWTH    each plot the model adds goes to the shortest arm of the
>             earliest corridor that can still grow, the gateway's arm before
>             the rest (GATEWAY); what it takes away comes off the newest end -
>             the latest corridor's longest arm, the gateway's last - so no
>             plot laid ever moves (H4);
>   THE COST  (H2: "heavily favours being straight and few junctions"): a step
>             costs STRAIGHT_COST, a 45-degree turn TURN_COST plots of
>             straight, a junction JUNCTION_COST. An arm goes straight while it
>             can; at the sea, or fresh water wider than it bridges, it turns
>             45 degrees to the side with more ground and runs along the shore
>             (the alternative is a dead end); it turns back onto its heading
>             where the way home runs TURN_COST plots (each step home lays one
>             plot where the staircase lays two, so the turn pays); at the
>             city's edge it waits for the ground ahead, unless the ground
>             beside it runs JUNCTION_COST plots at 45 degrees (a coast or a
>             valley: what another corridor's junction would serve); meeting
>             the net running across it, it crosses (an interchange) only where
>             the way beyond runs JUNCTION_COST plots, else waits there in a T;
>             meeting it running its own way, it joins it and stops; a new
>             corridor opens only when no arm can grow. The prototype's rule -
>             45 degrees at the sea alone, waiting at every edge - is this
>             one's stiffest case;
>   LINKS     (CL5) a corridor opens where its line crosses the net, and a net
>             whose arms stop at the sea or the city's edge before they are
>             APART long has no line crossing it: Jerus's Bergen laid 8% of its
>             highways. Where no line crosses, the HIGHWAYS open a LINK
>             corridor all the same: one arm, the link, over a PLANNED way -
>             a search of the piece's dry ground, from its net to the nearest
>             plot on a line it has no corridor on, straightened into a few
>             legs of the eight headings and laid plot by plot (openLink();
>             the first version ran straight from the net toward the line
>             and left a stub at every sea); where it stands on the line the
>             corridor's two arms open, along the line, as any corridor's do.
>             The highways' alone: the railway grows no corridors (WAYS below);
>   PIECES    (CL5) the city's ground the water parts is in pieces (Pieces),
>             and an arm crosses no sea, so the founding site's net never
>             reached another island the city built on. Each piece that holds
> ... (58 more lines in the source)

**Uses:** [World](World.md) (49), [CityMap](CityMap.md) (30), [InterchangeGround](InterchangeGround.md) (12), [TilePainter](TilePainter.md) (6), [Pieces](Pieces.md) (6), [TrainSchedule](TrainSchedule.md) (6), [DistrictPlan](DistrictPlan.md) (5), [BuildingVisual](BuildingVisual.md) (2), [TileRaster](TileRaster.md) (2)

**Used by (17):** [CityMap](CityMap.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [InterchangeGround](InterchangeGround.md), [IslandNetCheck](IslandNetCheck.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 135 | THE DIALS |
| 210 | THE GROUND THE RUNS READ |
| 275 | THE WITNESS (batch FX7) |
| 367 | A NET: ITS HUB, CORRIDORS AND ARMS |
| 530 | · pieces: whose a corridor, an arm and a plot is (CL5) |
| 816 | WHERE A NET'S PLOTS ARE |
| 946 | GROWING A NET |
| 1437 | · links: a corridor that opens where no line crosses the net (CL5) |
| 1903 | TAKING FROM THE NEWEST END |
| 2059 | THE RAILWAY'S YARDS |
| 2338 | THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES |
| 2685 | · boxes: a grid of them, the works merged where they touch, the works in groups |
| 2868 | · the sidings |
| 3066 | · a further yard, and its branch |
| 3259 | THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) |
| 3494 | · the trains' way (0.8.2, batch FR1; spec-freight 4.4) |
| 3652 | THE MONTH: TO THE MODEL'S COUNTS |
| 3831 | READING THEM: A BOX OF PLOTS |
| 4062 | THE SIDECAR |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 140 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 143 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 146 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 149 | `CityRuns.GATEWAY_UNKNOWN` | `- 2` | A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). |
| 152 | `CityRuns.NO_GATEWAY` | `- 1` | A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). |
| 155 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 158 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 161 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 164 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 167 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 170 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 173 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 176 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7); and (FX2) where each arm of the spine first crosses one. |
| 179 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 182 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 185 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 188 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 191 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 193 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 195 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 197 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 199 | `CityRuns.F_RESERVE` | `8` | ...and (batch FR7, D19) an interchange's reserve, a plan's ground only (fill() with yardsOver; InterchangeGround): street ground the plan keeps clear of lots, buildings and streets (DistrictPlan.FIXED_RESERVE) - never... |
| 202 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 204 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 206 | `CityRuns.M_DIAG_SHIFT` | `2` | ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... |
| 208 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 247 | `CityRuns.ONE_PIECE` | `new Pieces() { @ Override public int count() { return 1; } @ Override public ...` | The city as one piece (what a caller with no pieces passes): the founding site's net alone, as before CL5. |
| 255 | `CityRuns.SPINE` | `0` | A corridor's role (CL5), in its fourth entry: a piece's SPINE, the row and the column through its hub (a home net's corridors 0 and 1)... |
| 257 | `CityRuns.CROSSING` | `1` | ...a CROSSING, a line one district out that crosses the net already laid (newCorridor(); and the railway's placeholder corridors, which run along no line of their own)... |
| 259 | `CityRuns.LINK` | `2` | ...or a LINK, a line opened by a link arm from the net's plot nearest it, where no line crosses the net (openLink()). |
| 273 | `CityRuns.LINK_SEARCH` | `1<<21` | The most plots a link's search visits (CL5, linkWay()): 1 << 21, about 2 million, the net's own plots among them - its visited table holds up to twice that many slots, an index of 4 bytes each, and the plots found 9 b... |
| 692 | `CityRuns.RESERVES_KEPT` | `8192` | The most regions' sites kept at once: 8,192 - a screen's tiles and the plans' frames, many times over. |
| 951 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 1083 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |
| 1514 | `CityRuns.LinkSearch.SOURCE` | `9` | The move of a plot the search began from; the others hold 1 + the heading that found them. |
| 1516 | `CityRuns.LinkSearch.ORDER` | `{ 0, 2, 4, 6, 1, 3, 5, 7 }` | The order a plot's neighbours are tried in: straight ways, then the 45-degree ones. |
| 2087 | `CityRuns.YARD_LONG` | `(int) Math.ceil(2 * TrainSchedule.LOCO_PLOTS + Math.ceil(TrainSchedule.UNIT_T...` | How long a yard is laid along its line (FX5 Y1; Jerus, 2026-10-20: "a long train will be out of the depot", "the rail depots would have to be redesigned to accomdoate such large trains"): a unit train's length rounded... |
| 2097 | `CityRuns.TIER_LENGTHS` | `tierLengths()` | The lengths, longest first, a yard of footprint {w, h} is laid at along a row (w along) or a column (h along): each kind of train's length rounded up and its two ladders - a unit train's YARD_LONG (48), an intermodal ... |
| 2100 | `CityRuns.TIERS` | `TIER_LENGTHS.length + 1` | ...how many: one for each length and one for the footprint's own. |
| 2398 | `CityRuns.BRANCH` | `2, SIDING = 3` | The corridors of the arms laid for a train's way: a branch to a further yard (2) and a siding into a works (3). |
| 2401 | `CityRuns.SIDING_REACH` | `CityMap.DISTRICT` | A siding serves a works within this of the main line, in plots: one district (CityMap.DISTRICT, 256; spec-freight 4.3, "the largest BULK works within one district of the line"). |
| 2404 | `CityRuns.MAIN_LINE_REACH` | `1L<<20` | How far past the plots it has the main line of a city with a station is grown, at most, in plots: 1,048,576 (2^20) - some four thousand districts, past any city's ground, so the line waits at the city's edge and not a... |
| 2407 | `CityRuns.SERVED_WITHIN` | `1` | A works the main line touches or runs beside, within this many plots, has the railway at its door and needs no siding: 1. |
| 2410 | `CityRuns.GROUP_LINK` | `CityMap.DISTRICT / 2` | A group of rail-served works is the works whose box middles lie within this of each other, link by link: half a district, 128 plots. |
| 2413 | `CityRuns.YARD_APART` | `CityMap.DISTRICT` | A further yard goes beside a group of works that lies more than this from every yard laid: a district, 256 plots (FR7: "more than one district from every yard laid"). |
| 2416 | `CityRuns.BRANCH_FROM` | `CityMap.DISTRICT` | A group of works farther than this from the track is reached by a branch off the main line, else the yard sits on the track nearest it: a district, 256 plots. |
| 2688 | `CityRuns.BOX_GRID` | `64` | The boxes' grid: 64 plots a side. |
| 2921 | `CityRuns.SIDING_TRIES` | `200` | The most works laySidings() tries in one run: 200. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 266 | `static boolean linkArms` | Whether the highways grow link arms (CL5, openLink()): true; a harness turns it off to show its fixture stalls without one (the premise: a net whose arms stop short of APART opens no corridor). |
| 312 | `private Witness witness` | The witness of the month being laid, or null for none (the default): the map sets it for the length of layTo() and clears it. |
| 373 | `final int corridor, home` |  |
| 374 | `final long sx, sy` |  |
| 375 | `int head` |  |
| 376 | `long x, y` |  |
| 377 | `int steps` |  |
| 378 | `boolean joined` |  |
| 380 | `int segs` | Its stretches: each one's heading, steps, and start (the plot before its first step). |
| 381 | `int[] segHead` |  |
| 382 | `long[] segX` |  |
| 384 | `final List<long[]> crossings` | Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. |
| 386 | `int known` | Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). |
| 387 | `long knownAt` |  |
| 389 | `int stoppedBy` | What stopped it the last time it waited with no way along (transient): OPEN (nothing, or another reason), EDGE or SEA. |
| 395 | `int[][] plan` | A link arm's way (CL5, openLink()): its legs as {heading, steps} each, the leg it is on and the step it is at in it, and the ground's version it was last known to hold at (transient, never saved: a link read back from... |
| 396 | `int planLeg, planStep` |  |
| 397 | `long planAt` |  |
| 426 | `final int kind, at, bridge` |  |
| 428 | `boolean started, hubLaid` | Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). |
| 429 | `long hubX, hubY` |  |
| 434 | `final List<long[]> corridors` | Its corridors, {0 a row \| 1 a column, the line's coordinate, the piece, the role (SPINE, CROSSING or LINK)}, in the order opened (CL5: the piece and the role; an entry a harness adds with two entries is piece 0, a CRO... |
| 435 | `final List<Arm> arms` |  |
| 436 | `final List<Interchange> interchanges` |  |
| 438 | `long plots` | Plots laid: the union of its arms' plots and its hub's. |
| 443 | `final List<long[]> pieceHubs` | Its pieces' hubs (CL5), {x, y} each, piece 0's first (the net's own, hubX and hubY, kept as start() and the sidecar set it): a net on another piece of the city's ground is opened by layTo(), the highways' alone, from ... |
| 450 | `long[] piecePlots` | The plots laid by each piece's arms (CL5): credited in lay() to the piece of the arm that lays a plot nothing of the net covered yet, debited in trim() to the piece of the arm taken back, never below 0. |
| 452 | `long stuckAt` | The ground's version at which it last could lay no more (transient): it waits until the ground moves. |
| 454 | `long[] pieceStuck` | ...for each piece but piece 0 (CL5, transient). |
| 456 | `int[][] byPiece` | armsOf()'s index of the arms by piece, and the arms' count and last arm it was built at (transient, never saved). |
| 457 | `int byPieceSize` |  |
| 458 | `Arm byPieceLast` |  |
| 460 | `final Map<Long, long[]> buckets` | Its stretches by district: district key -> {arm << 24 \| stretch} entries, and how many. |
| 461 | `final Map<Long, Integer> bucketN` |  |
| 463 | `Witness witness` | The witness told of each change to its plots (THE WITNESS, batch FX7), or null: set by CityRuns.setWitness() for the length of a month's layTo(), never saved. |
| 661 | `final Net highways` |  |
| 662 | `final Net rail` |  |
| 665 | `int gateway` | The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 668 | `final List<long[]> yards` | The railway's yards: each {x0, y0, w, h} in world plots, in the order laid (FX5 Y1: up to YARD_LONG plots along its line, so its box is not always its type's footprint: yardAsked()). |
| 680 | `long highwayShort, trackSurplus` | What the model has that the runs could not lay: highway plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan) - and (FR7 2026-10-18, batch FR7 slice 3) the track plots the mo... |
| 681 | `int yardsShort` |  |
| 684 | `private Ground reserveGround` | The ground the interchanges' reserves are found over, and the works they keep clear of (batch FR7, InterchangeGround): bound by layTo() and the map, never saved. |
| 685 | `private InterchangeGround.Guard reserveGuard` |  |
| 688 | `private final Map<List<Long>, List<InterchangeGround.Site>> reserveKept` | The sites found, by the region asked about, against the stamp they were found at: the runs, the ground's and the works' versions - none kept past a change of any. |
| 689 | `private long reserveStamp` |  |
| 756 | `long version` | A stamp of everything laid: what the map's plans and views are kept against. |
| 759 | `private boolean doubleMain` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 762 | `private List<int[]> mainLineCache` | mainLine()'s last answer, and the version and the main arm it was computed at. |
| 763 | `private long mainLineCacheAt` |  |
| 764 | `private int mainLineCacheArm` |  |
| 767 | `private boolean highwaysReachCache` | highwaysReach()'s last answer, and the version it was computed at. |
| 768 | `private long highwaysReachCacheAt` |  |
| 771 | `private int mainArm` | The rail net's arm the main line runs out along and the trains come in on: the arm the railway's world line leaves the city's ground from (CityMap.worldLines() sets it, FX2), or -1 for the gate arm. |
| 1518 | `final Net n, other` |  |
| 1519 | `final Ground g` |  |
| 1520 | `final int p, goalKind` |  |
| 1521 | `final long goalV, hubX, hubY, most` |  |
| 1522 | `long[] ekey` |  |
| 1523 | `byte[] emove` |  |
| 1524 | `int used` |  |
| 1525 | `int[] slot` |  |
| 1526 | `final Map<Long, byte[]> tiles` |  |
| 1527 | `long lastTile` |  |
| 1528 | `byte[] lastDry` |  |
| 1530 | `int foundKind` | The line the goal found is on. |
| 1531 | `long foundV` |  |
| 2142 | `private final List<int[]> yardAsked` | The footprints the yards were laid for, in the order of `yards` (the first yardAsked.size() of them; the rest were added by hand and were asked for their boxes' own). |
| 2425 | `private long railStamp` | layRail()'s last stamp, and whether it has one: everything it reads, so it lays nothing twice for the same inputs. |
| 2426 | `private boolean railStampSet` |  |
| 2435 | `final List<long[]> mines, works` |  |
| 2436 | `private List<long[]> bulk, line` |  |
| 2437 | `private BoxGrid blocked` |  |
| 2438 | `private Groups groups` |  |
| 2439 | `private final Map<Long, List<long[]>> spots` |  |
| 2440 | `private final Map<Long, Integer> spotAt` |  |
| 2442 | `private final Map<Long, List<List<long[]>>> spotTiers` | Each size's boxes by tier, unsorted (spotsOnTrack()), and how many tiers are sorted into `spots` so far: a tier is sorted - each box weighed against every mine - only once every box before it failed to fit (FX5: five ... |
| 2443 | `private final Map<Long, Integer> tiersSorted` |  |
| 2692 | `private final Map<Long, List<long[]>> cells` |  |
| 2796 | `final double[][] g` |  |
| 2797 | `final boolean[] served` |  |
| 2798 | `private final Map<Long, List<Integer>> cells` |  |
| 2799 | `private int next` |  |
| 3285 | `private long[] station` | freightStation()'s last answer, the version, the gateway and the main arm it was found at. |
| 3286 | `private long stationAt` |  |
| 3287 | `private int stationAtGateway` |  |
| 3288 | `private int stationAtArm` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 133 | 4110 | **type** `public final class CityRuns` | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map... |

### THE DIALS (lines 135-209)

### THE GROUND THE RUNS READ (lines 210-274)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 8 | **type** `public interface Ground` | The ground runs are laid on: the city map's (CityMap.RunGround). |
| 217 | 1 | `int at(long x, long y, int dir)` _(in CityRuns.Ground)_ | The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. |
| 219 | 1 | `long version()` _(in CityRuns.Ground)_ | A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. |
| 221 | 1 | `long[] box()` _(in CityRuns.Ground)_ | The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. |
| 225 | 3 | `static boolean dry(int g)` | Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. |
| 235 | 10 | **type** `public interface Pieces` | The city's ground in pieces the water parts (CL5): the map's, worked out again when its ground moves. |
| 237 | 1 | `int count()` _(in CityRuns.Pieces)_ | The pieces: piece 0 is the home piece, the founding site's. |
| 239 | 1 | `int pieceOf(long x, long y)` _(in CityRuns.Pieces)_ | The piece of world plot (x, y), -1 for none. |
| 241 | 1 | `long weight(int p)` _(in CityRuns.Pieces)_ | Piece p's built weight (the map: the buildings drawn on it); 0 for none. |
| 243 | 1 | `long[] hub(int p)` _(in CityRuns.Pieces)_ | Piece p's hub {x, y}: a highway line crossing (both coordinates HIGHWAY_AT mod CELL) on its dry ground, nearest the founding site; null for none. |

### THE WITNESS (batch FX7) (lines 275-366)

| line | len | member | says |
|---:|---:|---|---|
| 306 | 4 | **type** `public interface Witness` | Told, before a change, which district it reaches. |
| 308 | 1 | `void before(long districtX, long districtY)` _(in CityRuns.Witness)_ | District (districtX, districtY) in the world's districts (plot / CityMap.DISTRICT, rounded down) is about to change. |
| 315 | 5 | `public void setWitness(Witness w)` | Sets the witness for the runs' nets and yards (null for none). |
| 322 | 4 | `static void saw(Net n, long x, long y)` | Tells net n's witness, if it has one, that plot (x, y) is about to change. |
| 328 | 6 | `static void sawBox(Witness wit, long x0, long y0, long w, long h)` | ...that every district the w x h plots from (x0, y0) touch is about to change. |
| 336 | 24 | `static void sawStretch(Net n, Arm a, int s)` | ...that every district stretch s of arm a lies in is about to change (its plots, and a 45-degree step's corners). |
| 362 | 4 | `static void sawArm(Net n, Arm a)` | ...that everything of arm a (all its stretches) is about to go. |

### A NET: ITS HUB, CORRIDORS AND ARMS (lines 367-529)

| line | len | member | says |
|---:|---:|---|---|
| 372 | 48 | **type** `static final class Arm` | One arm: from its start along its corridor, its straight stretches in the order laid. |
| 399 | 4 | `Arm(int corridor, int home, long sx, long sy)` _(in CityRuns.Arm)_ |  |
| 405 | 4 | `void push(int h)` _(in CityRuns.Arm)_ | A step along h from where it stands: its stretch lengthened, or a new one begun. |
| 410 | 9 | `void append(int h, int steps, long fromX, long fromY)` _(in CityRuns.Arm)_ |  |
| 422 | 1 | **type** `record Interchange(long x, long y, int corridor)` | A corridor's start: where it began on the net (an interchange; the spine's at the hub). |
| 425 | 104 | **type** `public static final class Net` | One net: the highways or the railway. |
| 465 | 1 | `Net(int kind, int at, int bridge)` _(in CityRuns.Net)_ |  |
| 468 | 1 | `public long plots()` _(in CityRuns.Net)_ | Plots laid. |
| 470 | 1 | `public int corridors()` _(in CityRuns.Net)_ | Corridors opened. |
| 472 | 1 | `public int interchanges()` _(in CityRuns.Net)_ | Interchanges: corridors' starts (the hub's among them) and arms' crossings. |
| 474 | 6 | `public List<long[]> interchangePlots()` _(in CityRuns.Net)_ | ...as {x, y}: a harness's. |
| 481 | 1 | `public int arms()` _(in CityRuns.Net)_ | Arms. |
| 483 | 1 | `public long[] hub()` _(in CityRuns.Net)_ | Its hub, {x, y}, or null before it starts. |
| 485 | 5 | `public int bends()` _(in CityRuns.Net)_ | Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. |
| 491 | 1 | `public int joined()` _(in CityRuns.Net)_ | Arms ended where they met the net (joined). |
| 494 | 5 | `public List<long[]> stretches()` _(in CityRuns.Net)_ | Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. |
| 506 | 10 | `void reset()` _(in CityRuns.Net)_ | Back to a net never started, the hub with it (FR6's playtest): a reset that kept its hub read back from a save as a fresh net's 0, 0, and the map was not the same map across the save (the default run, m1186, after its... |
| 518 | 1 | `public int pieces()` _(in CityRuns.Net)_ | The pieces: one for a net not opened on another piece of the ground (CL5). |
| 521 | 4 | `public long[] pieceHub(int p)` _(in CityRuns.Net)_ | Piece p's hub {x, y}: the net's own for piece 0; null for a piece the net has not got. |
| 527 | 1 | `public long piecePlots(int p)` _(in CityRuns.Net)_ | The plots piece p's arms laid (CL5; piecePlots): a net of one piece's are all of them. |

### pieces: whose a corridor, an arm and a plot is (CL5) (lines 530-815)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 3 | `static int corridorPiece(long[] c)` | The piece of corridor entry c: its third entry; an entry a harness adds with two is piece 0. |
| 538 | 3 | `static int corridorRole(long[] c)` | The role of corridor entry c: its fourth entry; an entry a harness adds with two is a CROSSING. |
| 543 | 3 | `static int pieceOf(Net n, Arm a)` | The piece arm a belongs to: its corridor's (0 for an arm whose corridor the net does not list). |
| 548 | 3 | `static boolean isSpine(Net n, int ci)` | Whether corridor ci is a SPINE: corridors 0 and 1, the home piece's, or one a piece opened with its hub. |
| 559 | 22 | `static int[] armsOf(Net n, int p)` | The indexes of piece p's arms, in index order (CL5): kept on the net and built again when its arms change (their count, or the last arm). |
| 583 | 4 | `static long count(Net n, int p)` | The plots piece p has: the net's own for a net of one piece (every plot is its), else the count piecePlots keeps. |
| 589 | 4 | `static void credit(Net n, int p, long d)` | Credits piece p with d plots newly laid. |
| 595 | 3 | `static void debit(Net n, int p, long d)` | Debits piece p of d plots taken back, never below 0 (the drift at a meeting of two pieces' arms: piecePlots). |
| 600 | 3 | `static long[] hubOf(Net n, int p)` | Piece p's hub {x, y}: the net's own for piece 0. |
| 605 | 3 | `static long stuckAt(Net n, int p)` | The ground's version at which piece p last could lay no more. |
| 610 | 4 | `static void setStuck(Net n, int p, long v)` | ...set. |
| 619 | 8 | `static boolean hubArm(Net n, Arm a)` | Whether arm a is the arm that lays its piece's hub plot, a piece but the home piece: the first arm of the piece's row spine, east from the plot before the hub, so that its first step lays the hub (openPiece()) and the... |
| 629 | 4 | `static int linkArm(Net n, int ci)` | The link arm of LINK corridor ci: the first arm of that corridor, which openLink() laid before the line's two; -1 for none. |
| 635 | 4 | `static boolean isLink(Net n, int i)` | Whether arm i is the link arm of a LINK corridor. |
| 641 | 7 | `static boolean linkReaches(Net n, int link)` | Whether the link arm at index link stands on its corridor's line: it has stepped, and its row (or column) is the line's. |
| 653 | 7 | `static boolean dormant(Net n, int i)` | Whether arm i is not grown now (CL5): a LINK corridor's link once it stands on its line, and the corridor's two line arms while it does not (a link taken back is grown again first). |
| 695 | 4 | `public void setReserveGuard(InterchangeGround.Guard guard)` | Sets the works a reserve may not take (the map's mines and shore works). |
| 705 | 4 | `public void layReserves(Ground g)` | Binds the ground the interchanges' reserves are found over and forgets the sites kept: called by layTo() every month, and by the map when it has read its runs back. |
| 717 | 15 | `public List<InterchangeGround.Site> reservesIn(long x0, long y0, int w, int h)` | The interchange sites whose box meets the w x h plots from (x0, y0), each with whether it got its reserve (InterchangeGround): found when asked, over the region and a margin, and kept against the runs, the ground and ... |
| 734 | 13 | `public List<InterchangeGround.Site> reserves()` | Every interchange site of the highways, over the box their plots lie in: a harness's, for a city a screen's size - a city of billions asks reservesIn() of a plan or a tile. |
| 749 | 5 | `public List<InterchangeGround.Site> reservesLaid()` | The reserves that were laid (Site.got()), of reserves(). |
| 773 | 1 | `public Net highways()` |  |
| 774 | 1 | `public Net rail()` |  |
| 775 | 1 | `public List<long[]> yards()` |  |
| 776 | 1 | `public long highwayShort()` |  |
| 779 | 1 | `public long highwayUndrawn()` | The highway plots the model has that the map draws nowhere: those the runs could not lay (highwayShort()), and (FX2) all the net's while it reaches no street (highwaysReach()), laid but not drawn - the legend's. |
| 781 | 1 | `public long trackSurplus()` | The track plots the model has that the railway's ways do not use and the map does not draw (FR7): the model's less the main line's, the sidings' and the branches' - the legend's "plots of track more than the line uses". |
| 782 | 1 | `public int yardsShort()` |  |
| 783 | 1 | `public long version()` |  |
| 786 | 1 | `public boolean doubleMain()` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 789 | 1 | `public void setDoubleMain(boolean doubleMain)` | Sets whether the main line is double track (the map's, from its Freight Line count). |
| 792 | 1 | `public int gateway()` | The gateway's bearing: GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 795 | 5 | `void setGateway(int bearing)` | Sets the gateway's bearing (the map's, once it has looked). |
| 802 | 1 | `public void setMainArm(int arm)` | Sets the arm the main line runs out along (the map's, from where the railway's world line leaves the city's ground): -1 for the gate arm. |
| 809 | 6 | `public int mainArm()` | The rail net's main arm: -1 with no gateway or before the railway starts; else the arm the map set (setMainArm()) while it is an arm of the net with steps laid, else the gate arm. |

### WHERE A NET'S PLOTS ARE (lines 816-945)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 3 | `static long districtKey(long x, long y)` |  |
| 825 | 12 | `static void file(Net n, int a, int s, long x, long y)` | Files stretch s of arm a under the district of plot (x, y). |
| 839 | 20 | `static boolean onStretch(Arm a, int s, long px, long py)` | Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. |
| 861 | 14 | `static int coverer(Net n, long x, long y, int not)` | The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. |
| 877 | 3 | `public static boolean covered(Net n, long x, long y)` | Whether net n has a plot at (x, y). |
| 893 | 14 | `public boolean highwaysReach()` | Whether the highways net is drawn: it has started and it has a ramp onto an arterial where stretchInto() marks M_RAMP - a straight stretch's step on a multiple of CELL that is every RAMP_EVERY-th cell line, or an arm ... |
| 909 | 12 | `static boolean onRampLine(Arm a, int s)` | Whether straight stretch s of arm a has a step (1 to its steps) on a ramp line: its coordinate along the stretch (x going east or west, y going north or south) a multiple of CELL whose cell line is every RAMP_EVERY-th... |
| 929 | 16 | `static long firstLineStep(Arm a)` | The step (1 to its steps) at which arm a's straight stretches first put a plot on a cell line along their heading - x a multiple of CELL going east or west, y going north or south - or -1 for none: where an arm of the... |

### GROWING A NET (lines 946-1436)

| line | len | member | says |
|---:|---:|---|---|
| 960 | 36 | `static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why)` | The steps along heading d from (x, y) the net may take, up to k (or a bridge's past it), and into why[0] what stops it: ground the city does not own, the sea, water wider than its bridge, a staircase's corner off the ... |
| 998 | 9 | `static void lay(Net n, int a, int s, long x, long y)` | Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back), and credited to the piece of the arm. |
| 1016 | 49 | `static boolean start(Net n, Ground g, long ax, long ay, int gateway)` | Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. |
| 1067 | 14 | `static int gateArm(Net n, int gateway)` | The spine's arm toward the gateway: -1 when there is none or the net has not started; else the index of the arm of corridor 0 or 1 whose home heading's angle (heading h is h * 45 degrees, x = cos, y = sin, as DX/DY) i... |
| 1093 | 3 | `static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows net n toward `target` plots (hw.py's grow()): its hub laid first, then each plot to the shortest arm of the earliest corridor that can still grow - the gateway's arm (gateArm()) before the rest of its corridor -... |
| 1105 | 55 | `static boolean growPiece(Net n, Net other, int p, long target, Ground g, long ax, long ay, int gateway)` | ...piece p's net grown to `target` plots of its own (CL5; count()): only the arms of that piece's corridors, its lines measured from its hub, its wait on the ground its own (stuckAt()). |
| 1167 | 13 | `static List<Integer> spineOrder(Net n, int gate)` | The spine's arms in the order the railway's main line is grown (FR7, batch FR7 slice 3): the gate arm, then the other arm of its corridor, then the other corridor's by index. |
| 1196 | 29 | `static boolean growMainLine(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows the railway's main line toward `target` plots (FR7, batch FR7 slice 3): its hub laid first, then the gate arm alone, until it waits - at the city's edge, where the world line takes it on. |
| 1235 | 87 | `static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why)` | One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS (the city's edge, the sea with no way along it, a T it does not cross, the other net's run along its way); JOINED (it met the net running its own w... |
| 1331 | 3 | `static boolean newCorridor(Net n, Ground g)` | The next corridor (hw.py's _new_corridor()): the parallel line k districts out, k from 1, that crosses the net already laid on the city's ground - at its plot there nearest the hub, an interchange - the nearest of tho... |
| 1336 | 35 | `static boolean newCorridor(Net n, Ground g, int p)` | ...piece p's: the lines measured from its hub, the net those of its corridors' arms, a line "had" when a corridor of this piece is on it. |
| 1373 | 7 | `static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2)` | hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. |
| 1382 | 3 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v)` | The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. |
| 1387 | 49 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v, int piece)` | ...among piece p's arms, nearest its hub (CL5). |

### links: a corridor that opens where no line crosses the net (CL5) (lines 1437-1902)

| line | len | member | says |
|---:|---:|---|---|
| 1440 | 4 | `static boolean hasLine(Net n, int p, int kind, long v)` | Whether piece p of net n has a corridor on the row (kind 0) or column (1) at v already. |
| 1459 | 19 | `static boolean openLink(Net n, Net other, Ground g, int p)` | The next corridor, where no line crosses piece p's net (CL5; the highways': growPiece() calls it when newCorridor() finds none): a LINK corridor, over a way planned by linkWay() from the piece's net to the nearest plo... |
| 1480 | 1 | **type** `record LinkWay(long sx, long sy, int kind, long v, int[][] legs)` | A planned way for a link: where it starts (a plot of the net), the line it ends on (a row, kind 0, or a column, kind 1, at v) and its legs, {heading, steps} each. |
| 1499 | 4 | `static LinkWay linkWay(Net n, Net other, Ground g, int p, long[][] from, int goalKind, long goalV, long[] hub, long most)` | The way a link takes (CL5): a search of the piece's dry ground, from `from` (every plot of the piece's net, or the end of one link arm replanning) to the nearest plot on a line; the way then straightened into legs. |
| 1512 | 226 | **type** `private static final class LinkSearch` | One search for a link's way (linkWay()): its visited plots in a table of open addressing - the plots found in order, each a long key (x in the high word, y in the low) and a byte for the move that found it, the order ... |
| 1533 | 7 | `LinkSearch(Net n, Net other, Ground g, int p, int goalKind, long goalV, long[] hub, long most)` _(in CityRuns.LinkSearch)_ |  |
| 1541 | 3 | `static long key(long x, long y)` _(in CityRuns.LinkSearch)_ |  |
| 1546 | 6 | `int probe(long key)` _(in CityRuns.LinkSearch)_ | The slot key hashes to, probing on: the one holding it, or the empty one it would go in. |
| 1554 | 18 | `void insertAt(int h, long key, byte move)` _(in CityRuns.LinkSearch)_ | Enters key at the empty slot h (from probe()) with its move, growing the table at half full. |
| 1574 | 20 | `boolean dry(long x, long y)` _(in CityRuns.LinkSearch)_ | Whether plot (x, y) is the city's dry ground, read once. |
| 1596 | 3 | `boolean alongOther(long x, long y, long nx, long ny)` _(in CityRuns.LinkSearch)_ | Whether a move from (x, y) to (nx, ny) runs along the other net: both plots are its. |
| 1601 | 13 | `boolean goal(long x, long y)` _(in CityRuns.LinkSearch)_ | Whether plot (x, y), popped, is a goal: sets foundKind and foundV. |
| 1615 | 40 | `LinkWay run(long[] xs, long[] ys)` _(in CityRuns.LinkSearch)_ |  |
| 1657 | 5 | `int parent(int e)` _(in CityRuns.LinkSearch)_ | The entry that found entry e: the plot one step back along its move. |
| 1674 | 22 | `int[][] legs(long[] px, long[] py, int m)` _(in CityRuns.LinkSearch)_ | The path px[0..m], py[0..m] straightened into legs {heading, steps}: from the plot it is at, the farthest later plot of the path that one or two legs reach - an octile way, the 45-degree leg of min(\|dx\|, \|dy\|) steps a... |
| 1698 | 4 | `static int heading(int sx, int sy)` _(in CityRuns.LinkSearch)_ | The heading of the move (sx, sy), each -1, 0 or 1 and not both 0. |
| 1704 | 18 | `int[][] octile(long x, long y, long dx, long dy, int first)` _(in CityRuns.LinkSearch)_ | The octile way from (x, y) by (dx, dy) as one or two legs, or null where neither order is free; `first` is the path's heading there. |
| 1724 | 13 | `boolean free(long x, long y, int h, int steps)` _(in CityRuns.LinkSearch)_ | Whether `steps` steps along heading h from (x, y) are all free: each plot, and a 45-degree step's corner, dry and not covered by the net, and no move along the other net. |
| 1751 | 28 | `static int stepLink(Net n, Net other, int i, Arm a, Ground g, long target)` | Link arm i's next step along its plan (CL5): one plot a step, laid as step() lays it - the witness first, push(), lay() of a 45-degree step's corner and its plot - and HELD where a 45-degree step needs two plots and t... |
| 1781 | 12 | `static boolean planHolds(Ground g, Arm a)` | Whether every plot left of arm a's plan, and each 45-degree step's corner, is still the city's dry ground. |
| 1795 | 10 | `static boolean replan(Net n, Net other, Ground g, Arm a)` | Plans link arm a's way again, from its end to its own corridor's line (the search of linkWay(), one source, the one line); false where it finds none. |
| 1807 | 18 | `static long[][] plotsOf(Net n, int p)` | Piece p's plots as {xs, ys}: its hub's if laid (the home piece's) and every step of its arms and each 45-degree step's corner, in arm order. |
| 1835 | 28 | `static void openLine(Net n, int i, PriorityQueue<Integer> live)` | The link arm i stands on its line: the link stops (growPiece() does not grow it again while it stands there) and the corridor's two arms open, both ways along the line, with an interchange at the meeting. |
| 1865 | 8 | `static void dropMeeting(Net n, int ci)` | Takes off the interchanges of LINK corridor ci past its first (its start's): the meeting with the line, once its link is taken back off the line. |
| 1883 | 19 | `static int openPiece(Net n, long hx, long hy)` | Opens a net on another piece of the city's ground (CL5), from its hub (hx, hy) - a highway line crossing on dry ground: two SPINE corridors of the new piece, the row y = hy and the column x = hx, four arms, and an int... |

### TAKING FROM THE NEWEST END (lines 1903-2058)

| line | len | member | says |
|---:|---:|---|---|
| 1911 | 3 | `static boolean trim(Net n, long target, int gateway)` | Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last. |
| 1927 | 69 | `static boolean trimPiece(Net n, int p, long target, int gateway)` | ...piece p's net taken back to `target` plots of its own (CL5; count()): only its arms, in the reverse of growPiece()'s order - the latest corridor first, in it the longest arm, and the arm that grew first last of its... |
| 1998 | 13 | `static boolean[] lateArms(Net n, int p, int gate)` | The arms taken back after the rest of their corridor (trimPiece()): the gate arm, a piece's hub arm and a LINK corridor's link, which grew first. |
| 2013 | 11 | `static PriorityQueue<Integer> newestQueue(Net n, int p, boolean[] late)` | Piece p's arms that have steps, the newest end first: the latest corridor, in it the late arm last, then the longest, then the higher index. |
| 2026 | 4 | `static boolean corridorEmpty(Net n, int ci)` | Whether every arm of corridor ci has no steps. |
| 2032 | 5 | `static void closeCorridor(Net n, int ci)` | Closes the net's last corridor ci: its arms (not always the net's last: a link's two line arms are laid after the corridors opened since), and its interchanges. |
| 2039 | 19 | `static void rebuildBuckets(Net n)` | Files every stretch of net n again (after a corridor is closed, or the runs read back). |

### THE RAILWAY'S YARDS (lines 2059-2337)

| line | len | member | says |
|---:|---:|---|---|
| 2102 | 10 | `private static int[] tierLengths()` |  |
| 2114 | 3 | `static int tierAlong(int own, int t)` | The footprint's own length along the line, `own`, at tier t: TIER_LENGTHS[t] (never less than the footprint's), the footprint's own at the last. |
| 2119 | 3 | `static boolean sameTier(int own, int t)` | Whether tier t is a length the tier before it already is (so it adds no boxes): a yard whose footprint is already as long as the interior has two tiers. |
| 2128 | 5 | `static int[] boxSize(boolean row, int w, int h, int along)` | A yard's box on a row (its line along x) or a column, `along` plots long, as {w, h}: across the footprint's own across where the length is the footprint's own (a Rail Depot is 8 x 7 whichever way its line runs, as it ... |
| 2135 | 5 | `public int[] yardAsked(int i)` | The footprint yard i was laid for, {w, h}: the size it was asked for, which it keeps whatever length the ground gave it (a yard added to `yards` by hand, as a harness does, was asked for its box's own). |
| 2145 | 6 | `private void addYard(long[] box, int[] asked)` | Lays a yard: its box, and the footprint it was asked for. |
| 2166 | 12 | `private void keepYards(List<int[]> sizes)` | Takes the yards back to the longest run, from the first, that were laid for the sizes the model now asks (FR7 slice 3, 2026-10-18): a Rail Terminal's box and a Rail Depot's are not one size, and until now every yard w... |
| 2180 | 5 | `List<long[]> yardSpots(int w, int h, List<long[]> mines)` | Every box a yard could take, the nearest a mine first: the YARD_LONG boxes, then (where none fits, in the order of the tiers) the cell's interior's, then the footprint's own; two a cell the track runs straight through... |
| 2187 | 11 | `private List<long[]> nearestMineFirst(List<long[]> tier, List<long[]> mines)` | One tier's boxes, the nearest a mine first (the hub with no mine): yardSpots()'s order within a tier. |
| 2200 | 12 | `List<long[]> yardSpotsNear(int w, int h, double gx, double gy)` | ...the nearest the point (gx, gy) first (FR7 slice 3: a further yard goes beside a group of works, not beside the mine nearest the hub), the tiers in order as above. |
| 2214 | 9 | `private static List<long[]> sortedBy(List<long[]> out, double[] key)` | The boxes by key, the least first; on a tie the northern, then the western. |
| 2229 | 71 | `private List<List<long[]>> spotsOnTrack(int w, int h)` | Every box a yard of footprint w x h could take on the track, unsorted, by tier (TIERS lists, the longest first): two a cell the track runs straight through on its line, one at either end of it; a siding is no line for... |
| 2306 | 13 | `static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h, int along)` | A cell's yard boxes across its quarter line (a row's, or a column's), `along` plots long (boxSize()): covering the line across, inside the interior across, at either end of the cell along - from the interior's first p... |
| 2321 | 16 | `boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid)` | Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site, and (FR7) no siding laid across it. |

### THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES (lines 2338-2684)

| line | len | member | says |
|---:|---:|---|---|
| 2419 | 1 | **type** `private record Way(long sx, long sy, int[][] legs, int plots)` | A way laid for a train: its start plot on the net, its legs {heading, steps}, and the plots it lays. |
| 2422 | 1 | **type** `private record Branch(long px, long py, int heading, int steps, long[] box)` | A branch to a yard: where it leaves the main line, its heading, its steps, and the yard's box. |
| 2434 | 69 | **type** `private final class Ctx` | What one layRail() run builds on demand and its steps share: the bulk works merged where they touch, a grid of every box a way must keep clear of (a city of billions has some two hundred thousand mined sites), the mai... |
| 2445 | 4 | `Ctx(List<long[]> mines, List<long[]> works)` _(in CityRuns.Ctx)_ |  |
| 2451 | 4 | `List<long[]> bulk()` _(in CityRuns.Ctx)_ | The bulk works' boxes, merged where they touch (mergeTouching()). |
| 2457 | 4 | `BoxGrid blocked()` _(in CityRuns.Ctx)_ | Every mine's and works' box, in a grid. |
| 2463 | 4 | `List<long[]> line()` _(in CityRuns.Ctx)_ | The main line's plots a siding or a branch may leave from (linePlots()), as the yards stand now. |
| 2469 | 7 | `Groups groups()` _(in CityRuns.Ctx)_ | The works' groups, served as the yards laid are. |
| 2478 | 4 | `void yardLaid(long[] y)` _(in CityRuns.Ctx)_ | Another yard laid: the line has a box on it, and the groups beside it are served. |
| 2484 | 18 | `long[] nearestMineSpot(int w, int h, Ground g)` _(in CityRuns.Ctx)_ | The next yard box of w x h on the track nearest a mine that fits (yardSpots(), kept for the run: the old rule, each size's list gone through once); null for none. |
| 2514 | 31 | `private void layRail(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<long[]...` | Lays the railway to the model's track plots, yards and spurs (layTo()'s rail half; each yard YARD_LONG along its line, or as short as the ground allows, THE RAILWAY'S YARDS): the derived arms off, the main line grown ... |
| 2547 | 12 | `private long railStampOf(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<lo...` | A hash of everything layRail() reads: the track plots, the yards' sizes, the spurs, the ground's version, the gateway, the hub's seed, the mines and works, and the highways laid. |
| 2567 | 14 | `private void dropWays()` | Takes off what an earlier run laid beyond the main line: the arms of the BRANCH and SIDING corridors and any corridor past the spine's (a net grown before FR7 has them as dead ends), and then the spine's arms past the... |
| 2591 | 17 | `private boolean cleanSpine()` | Takes the spine's arms past the main line's end back to nothing: the main line is the gate arm and, where the gate arm waits at the sea, the arms after it as far as the first that waits anywhere else (spineOrder(), gr... |
| 2610 | 15 | `static long countPlots(Net n)` | The plots of net n: its hub's, if laid, and the union of its arms' (each 45-degree step's corner too). |
| 2632 | 24 | `static void recountPieces(Net n)` | Counts each piece's plots afresh (CL5; piecePlots): the hub's to piece 0, each other plot to the piece of the lowest-index arm covering it. |
| 2658 | 3 | `private void wayCorridors()` | The corridors a branch and a siding belong to, there once a way is laid: placeholders after the spine's two (they run along no line of their own). |
| 2663 | 21 | `private int layWay(int corridor, long sx, long sy, int[][] legs)` | Lays a way from the net's plot (sx, sy) as an arm of `corridor`: each leg's plots, one by one, counted where nothing else of the net has them; the arm's index. |

### boxes: a grid of them, the works merged where they touch, the works in groups (lines 2685-2867)

| line | len | member | says |
|---:|---:|---|---|
| 2691 | 39 | **type** `private static final class BoxGrid` | A grid of boxes {x0, y0, x1, y1} (inclusive) BOX_GRID plots a side: whether one covers a plot, or meets another box. |
| 2695 | 3 | `BoxGrid(List<long[]>...lists)` _(in CityRuns.BoxGrid)_ |  |
| 2699 | 3 | `private static long key(long gx, long gy)` _(in CityRuns.BoxGrid)_ |  |
| 2703 | 7 | `void add(long[] b)` _(in CityRuns.BoxGrid)_ |  |
| 2711 | 6 | `boolean covers(long x, long y)` _(in CityRuns.BoxGrid)_ |  |
| 2719 | 10 | `boolean meets(long x0, long y0, long x1, long y1)` _(in CityRuns.BoxGrid)_ | Whether any box meets the plots x0..x1, y0..y1. |
| 2738 | 44 | `static List<long[]> mergeTouching(List<long[]> boxes)` | The works boxes {x0, y0, x1, y1} with those that touch (a gap of at most one plot between them) merged, transitively, into their bounding box - a large pit on twenty sites is one works. |
| 2784 | 3 | `private static long areaOf(long[] w)` | A works box's plots. |
| 2795 | 72 | **type** `private static final class Groups` | The works' groups (FR7 slice 3): works are one group while their box middles lie within GROUP_LINK of each other, link by link; a group is as large as its boxes' plots, its middle their plots' mean. |
| 2801 | 47 | `Groups(List<long[]> works)` _(in CityRuns.Groups)_ |  |
| 2850 | 10 | `void serve(double x, double y)` _(in CityRuns.Groups)_ | Marks every group within YARD_APART of (x, y) served. |
| 2862 | 4 | `double[] largest()` _(in CityRuns.Groups)_ | The largest group no yard serves, its middle {x, y}; null for none. |

### the sidings (lines 2868-3065)

| line | len | member | says |
|---:|---:|---|---|
| 2883 | 36 | `private void laySidings(int wanted, Ctx ctx, Ground g, long[] budget)` | Lays a siding for each of `wanted` Rail Spurs (FR7, D14): the largest bulk works (by its box's plots) within SIDING_REACH of the main line that has none and that the line does not already touch, a track from the neare... |
| 2924 | 15 | `private List<long[]> linePlots()` | The plots of the main line a siding or a branch may leave from, {x, y, axis (0 east-west, 1 north-south)}: the spine's arms past their start plots, on a stretch running straight east-west or north-south, and not insid... |
| 2940 | 4 | `private boolean inAYard(long x, long y)` |  |
| 2946 | 4 | `private static double distanceTo(long[] w, long x, long y)` | The distance from plot (x, y) to a works box {x0, y0, x1, y1}: 0 inside it, else along the axes or the corner. |
| 2957 | 29 | `private Way sidingTo(long[] w, List<long[]> line, Ground g, Ctx ctx)` | The shortest siding from the line to works w, or null: every plot of the line, its nearest plot beside the box, and the two ways of one turn, in order of length (then straight before turned, then the line's order), th... |
| 2988 | 8 | `private static int[][] legsOf(long px, long py, long ex, long ey, boolean northSouthFirst)` | The legs from (px, py) to (ex, ey): east-west then north-south, or (`northSouthFirst`) the other way; a leg of no length left out. |
| 3002 | 12 | `private boolean wayClear(long px, long py, int[][] legs, Ground g, BoxGrid blocked)` | Whether a way's plots, after its start, are all free: owned dry ground, in no works' or mine's box, no yard, off every highway's plot and its verge (the plots beside it), and on no track of the net. |
| 3016 | 8 | `public List<long[]> sidings()` | The sidings laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the SIDING corridor with steps. |
| 3026 | 5 | `public int sidingsLaid()` | How many sidings the runs have laid, into mined sites: the Rail Spurs they took (batch FR7 slice 5; the map deals the rest of the spurs to the plans' works, CityMap.sidingsOf()). |
| 3040 | 15 | `public java.util.Set<Long> mainLineCells(int side)` | The cells of a grid of `side` plots a side, each as (cx << 32) ^ (cy & 0xffffffff), whose own plots (not their first row or column, which the districts share) hold a plot of the main line - the spine arms of the corri... |
| 3057 | 8 | `public List<long[]> branches()` | The branches laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the BRANCH corridor with steps. |

### a further yard, and its branch (lines 3066-3258)

| line | len | member | says |
|---:|---:|---|---|
| 3069 | 13 | `private double distanceToNet(double x, double y)` | The distance from the point (x, y) to the railway laid (its hub and its spine's and branches' stretches), the segment each stretch is. |
| 3093 | 26 | `private boolean placeFurther(int[] size, Ground g, Ctx ctx, long[] budget)` | A further yard (FR7): beside the largest group of works a district from every yard laid (Groups.largest()), not beside the mine nearest the hub. |
| 3121 | 16 | `private boolean yardFree(long[] b, Ground g, Ctx ctx)` | yardFits() with the mines' boxes in the run's grid, for the many boxes a further yard weighs. |
| 3144 | 8 | `private void reachYard(long[] yard, Ground g, Ctx ctx, long[] budget)` | The branch for a yard laid already that the track does not reach (the branches are laid afresh with the rest): the shortest straight arm off the main line to its box, paid for out of `budget`; none if the yard has tra... |
| 3160 | 8 | `private Branch bestBranch(int w, int h, double gx, double gy, Ground g, Ctx ctx, long budget)` | The best branch to a new yard of w x h beside the point (gx, gy): from each plot of the main line whose cross line is a railway line (RAIL_AT), along that line toward the point, to a yard box in the cells beside the p... |
| 3170 | 27 | `private Branch bestBranchAt(int w, int h, int tier, double gx, double gy, Ground g, Ctx ctx, long budget)` | ...at one tier (tierAlong()): the yard boxes of that length only; null where none is free, within `budget` and reached by a branch. |
| 3199 | 21 | `private Branch branchTo(long[] box, Ground g, Ctx ctx)` | The shortest branch to a yard's box from the main line, or null: from a plot of the line on a railway line through the box, along it, as branchSteps() clears. |
| 3228 | 24 | `private int branchSteps(long px, long py, int heading, long[] box, Ground g, BoxGrid blocked)` | The steps of a branch from (px, py) along `heading` to the far edge of a yard's box, or -1: the box wholly beyond the plot, its line through it, and every plot owned dry ground, in no mine's or works' box nor another ... |
| 3254 | 4 | `private boolean trackIn(long[] box)` | Whether any plot of the box holds track of the net. |

### THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) (lines 3259-3493)

| line | len | member | says |
|---:|---:|---|---|
| 3271 | 12 | `public long[] freightStation()` | The freight station: the yard nearest the rail net's main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm; its x, y), by the yard box's middle, the first in yards' order on a tie. |
| 3291 | 14 | `private long[] findStation()` | The freight station found afresh (freightStation()'s rule). |
| 3307 | 13 | `private long[] anchorInYard(long[] y)` | The rail net's plot inside a freight station's box nearest its middle; null when the box has none. |
| 3326 | 29 | `static int[] locate(Net n, long x, long y)` | The arm and step of the lowest-index arm of n covering plot (x, y); null if none. |
| 3369 | 16 | `static List<int[]> chain(Net n, int a, int k)` | The arms from step k of arm a back toward the hub, as {arm, step}: a's first, then each next the arm and step its predecessor starts on (locate() of its start plot), ending on an arm of corridor 0 or 1. |
| 3398 | 45 | `public List<int[]> mainLine()` | The main line as ranges of steps, each {arm, from, to} inclusive (step 0 an arm's start plot). |
| 3449 | 44 | `private static long mainInto(Arm a, int from, int to, long x0, long y0, int w, int h, byte[] marks, boolean doubleMain)` | Marks one main-line range's plots inside the box with M_MAIN, and with M_DOUBLE where doubleMain; returns a hash of the range when any of it lies in the box, else 0. |

### the trains' way (0.8.2, batch FR1; spec-freight 4.4) (lines 3494-3651)

| line | len | member | says |
|---:|---:|---|---|
| 3511 | 5 | `public double[][] wayTo(long[] yard)` | The railway's way from its main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm) to a yard, as points in plots, each a plot's middle, at the ends and every turn: in along the main... |
| 3523 | 3 | `public double[][] wayToPlot(long px, long py)` | ...to a plot of the net (FR7 slice 3): the way wayTo() finds to a yard's anchor, to any plot - the end of a siding, where a bulk unit train runs straight in from the world line without a yard. |
| 3534 | 38 | `public double[][] wayOn(Net n, int fromArm, long px, long py)` | ...on any net, from the end of arm fromArm (FR5, slice B: the trucks' highway way is this walk on the highways net, from the arm its world line leaves from): wayToPlot() is wayOn(rail, mainArm(), px, py) exactly. |
| 3580 | 3 | `public double[][] highwayWayTo(int fromArm, long px, long py)` | The highways' way from the end of arm fromArm (the arm their world line leaves the city's ground from) to a plot of the highways net, as wayToPlot() finds the railway's (FR5, slice B: the trucks' routes): wayOn(highwa... |
| 3592 | 16 | `public List<double[][]> highwayPolylines()` | The highways' arms as polylines (FX5 slice D), each {xs, ys} in plots: the points of the arm at its ends and at every turn (armPoints()) at the middles of their plots (x + 0.5, y + 0.5), the corners the map rounds wit... |
| 3615 | 9 | `public List<long[]> highwayRamps()` | The highways' ramps at their arms' ends (fill() marks one at each arm that has steps): {x, y, the arm's index} in plots, in the arms' order; none while the highways reach no street (highwaysReach()). |
| 3626 | 10 | `static void armPoints(Arm a, int from, int to, List<long[]> out)` | Adds to out arm a's plots at steps from and to (from <= to) and at every turn between, in the order of its steps. |
| 3638 | 13 | `static long[] plotAt(Arm a, int k)` | The plot at step k of arm a: its start at 0, its end at a.steps and past. |

### THE MONTH: TO THE MODEL'S COUNTS (lines 3652-3830)

| line | len | member | says |
|---:|---:|---|---|
| 3665 | 5 | `public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g, long fx, long fy, long ...` | Lays the runs to the model's plots of highway and of track, and its yards (w x h each), over the ground: the highways grown, or taken back from their newest end, from the founding site (fx, fy); the railway laid from ... |
| 3677 | 4 | `public boolean layTo(long highwayPlots, long trackPlots, List<int[]> yardSizes, int spurs, Ground g, long fx, long fy, long rx,...` | ...with a yard of its own size each (`yardSizes`, {w, h} in the order the map lays them: Rail Terminals, then Rail Depots), `spurs` Rail Spurs, each a siding into a bulk works, and the bulk works' boxes `works`, each ... |
| 3688 | 13 | `public boolean layTo(long highwayPlots, long trackPlots, List<int[]> yardSizes, int spurs, Ground g, long fx, long fy, long rx,...` | ...with the city's ground in pieces (CL5; ONE_PIECE for none): the highways are dealt between the pieces that hold buildings, each with a net of its own from its own hub (layHighways()). |
| 3724 | 69 | `private void layHighways(long plots, Ground g, long fx, long fy, Pieces pieces)` | The highways to the model's `plots`, dealt between the city's pieces of ground (CL5). |
| 3799 | 17 | `private static void openPieces(Net n, Ground g, Pieces pieces)` | Opens a net for each map piece but the home piece (piece 0) that has weight and a hub on dry ground of its own, and none yet: no net's hub lies on it already (openPiece()). |
| 3818 | 12 | `long signature()` | A hash of everything laid. |

### READING THEM: A BOX OF PLOTS (lines 3831-4061)

| line | len | member | says |
|---:|---:|---|---|
| 3845 | 3 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver)` | What the runs lay on the w x h plots from world plot (x0, y0), row by row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's ground: n... |
| 3855 | 3 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver, boolean reserves)` | ...with or without the interchanges' reserves (F_RESERVE; THE RUNS DATED, batch FX7): the reserves are derived from the runs, the ground and the works about them and found when asked, so the map's dated record of what... |
| 3864 | 72 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver, boolean reserves, boolean reach)` | ...and with the highways filled in as if highwaysReach() were `reach` (batch FX7): what the runs lay today, as they were drawn the month before the highways first reached a street, or the month they ceased to. |
| 3944 | 32 | `public long frameHash(long x0, long y0, int w, int h)` | A hash of what the runs lay on the w x h plots from (x0, y0) - each stretch's steps there, the hub, the arms' ends (their ramps), the yards - without drawing them: what a district's plan is kept against (CityMap.ownSt... |
| 3978 | 14 | `static long[] range(Arm a, int s, long x0, long y0, int w, int h)` | The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. |
| 3993 | 6 | `private static void put(byte[] fixed, int w, long x, long y, byte code)` |  |
| 4001 | 34 | `private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code)` | One stretch's plots inside the box, into fixed and marks; a hash of its part there. |
| 4037 | 3 | `static int diagCode(int h)` | A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. |
| 4042 | 5 | `public byte at(long x, long y)` | What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0 - no highway while the net reaches no street (highwaysReach(), FX2). |
| 4054 | 7 | `public List<long[]> highwayDistricts()` | The districts the highways' stretches are filed under (batch FX7, THE RUNS DATED), each {world column, world row} (plot / CityMap.DISTRICT, rounded down): every district a highway plot lies in, and - after a net was t... |

### THE SIDECAR (lines 4062-4242)

| line | len | member | says |
|---:|---:|---|---|
| 4067 | 3 | `public int runs()` | The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). |
| 4072 | 3 | `void write(DataOutputStream out) throws IOException` | Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards (and since FORMAT 10 the footprints they were laid for); what was short. |
| 4082 | 46 | `void write(DataOutputStream out, int format) throws IOException` | ...in the sidecar format given: from FORMAT 10 (FX5 Y1) each yard's box is followed, after all the boxes, by the footprint it was laid for, two ints (w, h) each - the size it was asked for, which a box laid long or sh... |
| 4130 | 3 | `static CityRuns read(DataInputStream in) throws IOException` | The runs write() wrote, read back; null when they do not add up. |
| 4135 | 80 | `static CityRuns read(DataInputStream in, int format) throws IOException` | ...from a sidecar of the format given: before FORMAT 10 a yard's footprint is its box's own width and height, as it was laid then; before FORMAT 12 the net is one piece (CL5). |
| 4217 | 25 | `public boolean same(CityRuns o)` | Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. |


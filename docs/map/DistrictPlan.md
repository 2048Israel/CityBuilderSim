# DistrictPlan.java - 3,354 lines · 120 methods · 67 constants · model

`ham/citybuildersim/DistrictPlan.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it, the cells it opens and the layout of each, every street with its kind and width, and a box for every building - the same plan from the same inputs on any machine.
> 
> WHY THIS EXISTS (0.7.87, batch RD1; the project's spec-roads-and-ports.md
> 2.3 to 2.6 and 2.9, a port of its prototype roads-prototype/proto2.py).
> Jerus's screenshot of his city at 0.7.86 showed a tile of buildings with no
> road beside a tile that was a maze of road with a shop in each hole, walls
> of buildings along every tile edge and a highway crossing itself. The map
> dealt a district's road plots onto a few road tiles and drew them one for
> one as lanes and a fill, and no street grid that reaches every building
> fits in a paved city's 12% of road at 30 m plots (spec 1). The spec's fix is
> streets by need, road by surface: the plan lays the streets the buildings
> need - one network, + junctions on a grid, every building within reach -
> and draws the model's road plots exactly, as what those streets are made of.
> Nothing in the model reads it (CityMap's banner); the painter draws
> from it since 0.7.88 (batch RD2).
> 
> THE FRAME. A district is 256 x 256 plots and 8 x 8 cells; a CELL is a tile,
> its west column and north row its ARTERIALS, its interior 31 x 31. The plan
> works on 257 x 257 plots: the district and the first column and row of its
> east and south neighbours, where its own edge cells' east and south
> arterials run (the prototype's district held both its edge lines too).
> 
> THE PLAN, in the prototype's order:
>   ORDER   the cells with room (CELL_ROOM_LEAST) ranked outward from the hub,
>           its cell nearest the founding site: each next the nearest that
>           touches one taken, with a hashed nudge (NUDGE), so the open cells
>           are one piece;
>   BANDS   the buildings in three bands - what follows people, industry, the
>           outer kinds - each largest first, the first band's types dealt
>           round its cells in turn from a hashed start a type (MIXED_SLOT);
>           each building at the first spot it fits in its band's open cells,
>           touching a street first, then within REACH; a cell opened when none
>           holds it: a HOMES cell (long blocks, streets every 8 plots across
>           them and 16 along) for the first band, an ESTATE cell (one spine)
>           for the others; in a homes cell two or four blocks MERGED for a
>           building wider than a block;
>   JOIN    the street pieces joined to the largest along the lattice, and
>           (0.7.88, ACROSS DISTRICTS) to the streets of the districts
>           before it in the map's order where they meet its frame's edge;
>   SURFACE the model's gravel and paved (and any highway plots given:
>           none from the map since 0.7.89) as the streets' surface: every
>           street half width, arterials first, then by its cell's rank,
>           then full width; a street the surface does not reach is a TRACK;
>   LADDER  a road-rich district (surface left over) squares its homes cells
>           nearest the hub (SQUARE_PLOTS a cell) and then makes boulevards of
>           the arterials nearest the hub (BOULEVARD_PLOTS a cell), a step not
>           taken that would leave a building out.
> The spec's rules the prototype did not draw are added where its sections
> say (the game's rules; Input.asPrototype leaves them out, and with the
> prototype's hashes the plan is then the prototype's, box for box):
>   WATER   a street bridges fresh water up to STREET_BRIDGE plots, an
>           arterial up to ARTERIAL_BRIDGE (2.4);
>   CUTS    where water or the city's edge cuts a cell (H2), a street the cut
>           leaves as a dead end is joined to its neighbour along the cut; a
>           piece it leaves shorter than a block, touching nothing, is not
>           laid (Jerus: "no stray roads"); a merge or a closed spine that
>           would part the network is not made; the join looks along the
>           lattice, then over the ground;
>   FIXED   no building touches a highway plot, corners included (H5);
>           streets pass beneath a highway and cross a railway, never along;
> ... (107 more lines in the source)

**Uses:** [TilePainter](TilePainter.md) (23), [World](World.md) (18), [BuildingVisual](BuildingVisual.md) (17), [CityRuns](CityRuns.md) (2), [CityMap](CityMap.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (11):** [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [DistrictPicture](DistrictPicture.md), [EarthTerrainCheck](EarthTerrainCheck.md), [JunctionCheck](JunctionCheck.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 178 | THE GEOMETRY |
| 266 | · the inputs' codes |
| 296 | · a street plot's code |
| 341 | THE INPUTS |
| 436 | THE PLAN |
| 696 | THE BUILDER: ONE PLAN AT A LADDER'S STEP |
| 885 | · the growth order (spec 2.4) |
| 943 | · the bands (spec 2.4, R5, R6) |
| 1053 | · one build |
| 1495 | · opening a cell (spec 2.3) |
| 1570 | · an estate cell's streets (0.7.90, ESTATE LINES) |
| 1991 | · a cell's room and reach, in bit rows |
| 2672 | · the join: one network |
| 2703 | · access roads (batch FR7, slice 4; spec-freight 4.5, D17) |
| 3222 | · the surface (spec 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 183 | `DistrictPlan.SIDE` | `CityMap.DISTRICT` | Plots on a district's side: CityMap.DISTRICT, 256. |
| 186 | `DistrictPlan.FRAME` | `SIDE + 1` | Plots on the plan's side: the district and the first column (row) of its east (south) neighbour, where its edge cells' last arterial runs - 257. |
| 189 | `DistrictPlan.AREA` | `FRAME * FRAME` | Plots in the frame. |
| 192 | `DistrictPlan.CELL` | `World.TILE` | A cell's side, arterial to arterial: a tile, World.TILE, 32 plots (960 m) - its west column and north row its arterials (spec 2.3). |
| 195 | `DistrictPlan.INTERIOR` | `CELL - 1` | A cell's interior: 31 plots a side, between its arterials. |
| 198 | `DistrictPlan.CELLS_A_SIDE` | `SIDE / CELL` | Cells on a district's side: 8. |
| 201 | `DistrictPlan.CELLS` | `CELLS_A_SIDE * CELLS_A_SIDE` | Cells in a district: 64. |
| 204 | `DistrictPlan.LATTICE` | `TilePainter.JUNCTION_APART` | The street lattice: lines every TilePainter.JUNCTION_APART (8) plots, the + junction floor - a cell's west arterial on one, so the lattice runs on from cell to cell. |
| 207 | `DistrictPlan.REACH` | `TilePainter.REACH` | A building is within reach of a street when one lies within this many plots of it, across corners: TilePainter.REACH, 4. |
| 210 | `DistrictPlan.STREETS_AT` | `{ LATTICE - 1, 2 * LATTICE - 1, 3 * LATTICE - 1 }` | Where a cell's streets run, in plots into its interior: 7, 15 and 23 - the lattice's lines inside it (spec 2.3's long blocks of 15 x 7, square blocks of 7 x 7). |
| 213 | `DistrictPlan.MIDDLE` | `2 * LATTICE - 1` | ...and a homes cell's cross street and an estate cell's spine: the middle line, 15 - long blocks 15 plots long either side of it. |
| 216 | `DistrictPlan.LINE_FIRST` | `LATTICE - 1` | An estate cell's streets lie on interior lines from this one (0.7.90, ESTATE LINES): 7, LATTICE - 1 - so where one meets a neighbour's street across an arterial, the + it makes is LATTICE or more from the ring's corne... |
| 219 | `DistrictPlan.LINE_LAST` | `INTERIOR - LATTICE` | ...to this one: 23, INTERIOR - LATTICE, the same from the far corner. |
| 222 | `DistrictPlan.STRIP_LEAST` | `LINE_FIRST` | A strip's least depth between an estate cell's streets: 7, LINE_FIRST - so its streets are LATTICE or more apart, and so are the + junctions two of them could make on one arterial. |
| 225 | `DistrictPlan.STREET_BRIDGE` | `TilePainter.STREET_BRIDGE` | A street's longest crossing of fresh water: TilePainter.STREET_BRIDGE, 6 plots (spec 2.4, today's street bridge). |
| 228 | `DistrictPlan.ARTERIAL_BRIDGE` | `TilePainter.MAX_BRIDGE [ BuildingVisual.HIGHWAY ]` | An arterial's: a highway's, TilePainter.MAX_BRIDGE[HIGHWAY], 14 plots (spec 2.4) - wider water is a landmass's edge (spec 3). |
| 231 | `DistrictPlan.CELL_ROOM_LEAST` | `120` | A cell is opened only with at least this many plots of dry owned ground off the highways in its interior: 120 of 961, the prototype's. |
| 234 | `DistrictPlan.NUDGE` | `0.6` | The hashed nudge on a cell's distance from the hub, in cells: up to 0.6, the prototype's - the open cells' edge ragged, not a disc. |
| 237 | `DistrictPlan.MIXED_SLOT` | `640` | The first band's buildings are dealt round one slot for every this many plots of their footprints: 640, the prototype's - about a homes cell's room after its streets, so the slots are about its cells and a cell holds ... |
| 240 | `DistrictPlan.SQUARE_PLOTS` | `56` | The surface the ladder's square blocks take a homes cell: 56 plots, the prototype's - a square cell's streets (3 rows and 3 columns, 177 plots) less a long-block cell's (121). |
| 243 | `DistrictPlan.BOULEVARD_PLOTS` | `100` | ...and a boulevard a cell: 100 plots, the prototype's - its arterials' second row, less what neighbouring boulevards share. |
| 246 | `DistrictPlan.MERGE_GROUPS` | `{ { 15, 15 }, { 15, 23 }, { 15, 31 }, { 31, 15 }, { 31, 31 } }` | The groups of long blocks a homes cell merges for a building wider than a block, {across the blocks, along them} in plots: two blocks, three, four, two across, the whole cell (the prototype's; spec 2.3: 15 x 15, 15 x ... |
| 249 | `DistrictPlan.MERGE_DRY` | `0.95` | A group of blocks is merged only when more than this share of it is dry ground or street: 0.95, the prototype's. |
| 252 | `DistrictPlan.BLOCK_DEPTH` | `LATTICE - 1` | A building wider than this, in a homes cell, may merge blocks: 7, a block's depth. |
| 255 | `DistrictPlan.JOIN_ROUNDS` | `40` | The most pieces the join joins, one at a time: 40, the prototype's. |
| 258 | `DistrictPlan.JOIN_DX` | `{ 0, 0, 1, - 1 }, JOIN_DY = { 1, - 1, 0, 0 }` | The join's steps in the prototype's order: south, north, east, west - so of two ways as short it takes the prototype's. |
| 261 | `DistrictPlan.HALF` | `0.5` | A street's least surface, in plots of its right of way: a half, 15 m (spec 2.5). |
| 264 | `DistrictPlan.FULL` | `1.0` | ...and its full width, a whole plot, 30 m. |
| 269 | `DistrictPlan.FIXED_HIGHWAY` | `BuildingVisual.HIGHWAY` | A fixed plot: an Elevated Highway's (BuildingVisual.HIGHWAY). |
| 272 | `DistrictPlan.FIXED_RAIL` | `TilePainter.RAIL` | ...a railway's track (TilePainter.RAIL). |
| 275 | `DistrictPlan.FIXED_YARD` | `CityRuns.F_YARD` | ...a railway yard's ground, a Rail Terminal the city's runs drew on its track (0.7.89, CityRuns.F_YARD): no street crosses it, no building stands on it. |
| 278 | `DistrictPlan.FIXED_RESERVE` | `CityRuns.F_RESERVE` | ...an interchange's reserve (batch FR7, D19, CityRuns.F_RESERVE): a cloverleaf's 19 x 19 plots or a trumpet's 11 x 11, street ground - no lot, no building, no street across it but the highways and their slips, which t... |
| 284 | `DistrictPlan.SITE_FIELD` | `1` | A site with nothing on it: a field, built on last. |
| 287 | `DistrictPlan.SITE_MINED` | `2` | A site a mine or well stands on: its own. |
| 290 | `DistrictPlan.CLOSED` | `0` | A cell's layout: not opened. |
| 292 | `DistrictPlan.HOMES` | `1` | ...homes: long blocks (or square, the ladder's), for what follows people. |
| 294 | `DistrictPlan.ESTATE` | `2` | ...an estate: one spine, two strips, for industry and the outer kinds. |
| 299 | `DistrictPlan.NONE` | `0` | A plot's street, its low three bits: none. |
| 301 | `DistrictPlan.TRACK` | `1` | ...a track: a street the city has bought no road for (spec 2.5, R4). |
| 303 | `DistrictPlan.GRAVEL` | `2` | ...gravel. |
| 305 | `DistrictPlan.PAVED` | `3` | ...paved. |
| 307 | `DistrictPlan.HIGHWAY` | `4` | ...an Elevated Highway's plots given to the plan as a street's surface (Input.highway: CityMap's deal did so from 0.7.72, its plans to 0.7.88; since 0.7.89 the runs lay every one and the map gives none). |
| 309 | `DistrictPlan.SEAM` | `5` | ...a seam: a street on the district's edge whose surface is the neighbour's (SEAMS). |
| 311 | `DistrictPlan.UNDER` | `6` | ...a street passing beneath a highway (spec 2.7: elevated). |
| 313 | `DistrictPlan.KIND_MASK` | `7` | The kind's bits. |
| 315 | `DistrictPlan.WIDTH_SHIFT` | `3` | Its width, bits 3 and 4: 1 half, 2 full; 0 for none (a track, a seam, beneath a highway). |
| 317 | `DistrictPlan.ROLE_SHIFT` | `5` | Its role, bits 5 to 7. |
| 319 | `DistrictPlan.ROLE_STREET` | `1` | ...a cell's street. |
| 321 | `DistrictPlan.ROLE_ARTERIAL` | `2` | ...an arterial: a cell's ring. |
| 323 | `DistrictPlan.ROLE_BOULEVARD` | `3` | ...an arterial of a boulevard cell, either row (spec 2.5). |
| 325 | `DistrictPlan.ROLE_SHORE` | `4` | ...a street along a cut, joining a dead end to its neighbour (H2). |
| 327 | `DistrictPlan.ROLE_JOIN` | `5` | ...a street the join laid along the lattice. |
| 329 | `DistrictPlan.ROLE_ACCESS` | `6` | ...an access road (batch FR7, D17): the street from a sea terminal's, a yard's or a Truck Depot's gate to the nearest highway ramp or, failing one, the arterial grid - an arterial in all but its name, paved first. |
| 332 | `DistrictPlan.ACCESS_NEW` | `3` | The cost of laying a plot of access road where there is no street, against 1 for a plot that already is one: 3 - so a road follows the streets there are, and crosses open ground only where that is a good deal shorter. |
| 335 | `DistrictPlan.ACCESS_RAMP_DISTRICTS` | `2` | How far from the work it serves a highway ramp may lie, in districts, for its access road to go there and not to the arterial grid: 2 (spec-freight 4.5: "the nearest highway ramp if one lies within two districts"). |
| 337 | `DistrictPlan.BRIDGE` | `1<<8` | A bridge over fresh water, bit 8. |
| 339 | `DistrictPlan.CROSSING` | `1<<9` | A level crossing of a railway, bit 9. |
| 472 | `DistrictPlan.ACCESS_RAMP` | `1, ACCESS_ARTERIAL = 2, ACCESS_HAD = 3` |  |
| 572 | `DistrictPlan.BUILDERS` | `new ThreadLocal<>()` | KEPT (0.7.94, batch RD7): each thread's builder, its arrays - about 4 MB - and the ladder's turns - about 0.4 MB each, one a step of the longest chain it has climbed - made once and used plan after plan, rather than m... |
| 757 | `DistrictPlan.Builder.WORDS` | `(FRAME + 63) / 64` | The streets row by row as bits, WORDS longs a row: what a cell's reach is read from. |
| 831 | `DistrictPlan.Builder.WORN` | `1<<30` | The stamps' ceiling: a builder is replaced past it, half the int's range, far beyond what a plan stamps (a dense plan some tens of thousands). |
| 1573 | `DistrictPlan.Builder.FLIP` | `1<<31` | estateLines()'s flag: the cell's streets run the other way from its own direction (a cell the ground cuts). |
| 1589 | `DistrictPlan.Builder.FULL_LINES` | `new java.util.concurrent.ConcurrentHashMap<>()` | Each layout a full cell takes for a building's shape, its ring whole: {across, down, boulevard, the cell's direction} to estateLines()'s answer - the same whatever lies about it (no street beyond its ring is nearer it... |
| 1698 | `DistrictPlan.Builder.H_STREET` | `1, H_ARTERIAL = 2, V_STREET = 4, V_ARTERIAL = 8, LAY_REFUSED = 15, LAY_UNDER ...` | LAY CODES (0.7.94, batch RD7): what layPlot() makes of each plot, read from the input once a plan - the plot refused to a street running east-west (H_STREET), to an arterial so (H_ARTERIAL), and north-south (V_STREET,... |
| 2156 | `DistrictPlan.Builder.LAST_WORD` | `(1L<<(FRAME - 64 *(WORDS - 1))) - 1` | A row's last word's plots in the frame: bit 0 to FRAME - 1 - 64 x (WORDS - 1). |
| 2216 | `DistrictPlan.Builder.WINDOW` | `(1L<<(INTERIOR + 2 * REACH)) - 1` | A window's row as bits, one a plot: a cell's interior and the REACH plots either side of it. |
| 2603 | `DistrictPlan.Builder.SPLIT_NEAR` | `CELL` | splits()'s first walk keeps within this many plots of the box: a cell (NEAR FIRST). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 358 | `public long seed` | The world's seed. |
| 360 | `public long x0, y0` | The frame's first plot in the world: the district's north-west plot. |
| 362 | `public double hubX, hubY` | The hub, in the frame's plots: the founding site (spec 2.4; on another landmass, later, its first port). |
| 364 | `public final byte[] terrain` | The ground, a World class a plot, row by row over the frame. |
| 366 | `public final boolean[] owned` | Which plots the city owns. |
| 368 | `public final byte[] fixed` | The city's highway and railway plots: FIXED_HIGHWAY, FIXED_RAIL or a yard's FIXED_YARD, laid before the plan (the city's runs since 0.7.89, CityRuns; CityMap's network's plan's until 0.7.88). |
| 370 | `public final byte[] site` | The resource sites: SITE_FIELD, or SITE_MINED where a mine or well stands. |
| 372 | `public final List<int[]> mines` | The mines and wells of the district standing on their sites, each {x0, y0, x1, y1} inclusive in the frame's plots: counted apart from the reach (PlanCheck), as MapCheck 8 counted them. |
| 374 | `public final List<int[]> works` | The works that need an access road (batch FR7, D17), each box {x, y, w, h} in the frame's plots: the railway's yards and the shore's sea terminals (the map's; they stand on FIXED_YARD ground). |
| 376 | `public boolean[] ramp` | A highway's plot of the frame that carries a ramp (CityRuns.M_RAMP), by plot; null for none: where a street beneath it makes a ramp an access road can end at. |
| 378 | `public BuildingVisual.Type[] types` | The types, by id (BuildingVisual.table()). |
| 380 | `public int[] counts` | The buildings to place, by type id: every drawn type the district holds but its mines and wells on sites. |
| 382 | `public double gravel, paved, highway` | The road plots its streets are made of, by kind: the model's Gravel and Paved Roads, and Elevated Highway plots to lay as a street's surface (none from the map since 0.7.89: the city's runs lay them, and count what th... |
| 384 | `public final boolean[] surfaces` | Whether this district surfaces its seams: north, east, south and west, then its corners north-west, north-east, south-east and south-west (SEAMS). |
| 386 | `public final boolean[] seamOpen` | A seam's plot this district does not surface, which the district that does leaves without a street (0.7.88, ONE-SIDED SEAMS): set by the map from the plans before this one in its order; false for all, every seam is la... |
| 388 | `public final boolean[] anchor` | A plot of the frame's edge a district before this one in the map's order lays a street on (0.7.88, ACROSS DISTRICTS), a street on the city's network: what this district's streets join to; none for the first district, ... |
| 390 | `public final boolean[] partedAnchor` | ...and one whose street is not on the network yet - the ground parted its piece from it in that district's frame, and no district before this one laying the plot has it on (0.7.88, PARTED): joined here, where this dis... |
| 392 | `public boolean root` | Whether this is the first district in the map's order: its largest piece is the network's (0.7.88). |
| 394 | `public boolean asPrototype` | The prototype's own rules only - no water rule, no streets along a cut, every seam surfaced: PlanCheck's replay of proto2.py. |
| 396 | `public boolean bandsApart` | The estate bands kept apart, as 0.7.87 to 0.7.91 drew them - no outer kind in industry's cells (SHARED ESTATES): PlanCheck's comparison. |
| 398 | `public Hashes hashes` | The hashes, or null for the world's. |
| 441 | `public final byte[] cellKind` | Each cell's layout (CLOSED, HOMES or ESTATE), by ci + cj x 8. |
| 443 | `public final boolean[] cellAcross` | ...whether its long blocks run east-west (else north-south). |
| 445 | `public final int[] cellRank` | ...its rank in the order opened, or -1. |
| 447 | `public final boolean[] cellSquare` | ...whether the ladder squared it (a homes cell's + grid at 8). |
| 449 | `public final boolean[] cellBoulevard` | ...whether its arterials are a boulevard. |
| 451 | `public final boolean[] cellMerged` | ...whether blocks were merged in it for a building wider than one (homes), or its spine closed for one wider than a strip (an estate). |
| 453 | `public final int[] cellStreets` | ...an estate cell's streets (0.7.90, ESTATE LINES): bit o for a street o plots into its interior, running north-south where cellAcross, else east-west; 0 for a homes cell, or an estate cell whose spine was closed. |
| 456 | `public short[] street` | Each plot's street: its kind, width, role and flags (KIND_MASK, WIDTH_SHIFT, ROLE_SHIFT, BRIDGE, CROSSING); 0 for none. |
| 459 | `public int buildings` | The buildings placed: each one's box {x, y, w, h} in the frame's plots and its type id. |
| 460 | `public int[] bx` |  |
| 463 | `public int[] overflow` | What the plan could not place, by type id (R7: the map carries it to the next district with room since 0.7.88, and packs what none holds at the city's edge). |
| 471 | `public final List<int[]> accessRoads` | The access roads laid (batch FR7, D17), one for each work that had none: its box {x, y, w, h}, the gate (the road's first plot, beside the box), the road's end plot, and what it ends on - ACCESS_RAMP (a ramp's plot, a... |
| 475 | `public boolean[] accessAt` | The plots the access roads run over, by plot (a boulevard's among them, which keeps its own role); null when none was laid. |
| 478 | `public double surplus` | The surface left over when every step of the ladder is taken: road the plan has no street for (0 where the ladder holds it). |
| 481 | `public final double[] leftover` | ...by kind, [0, gravel, paved, highway] (BuildingVisual's): each kind's road less its surface, so surface and leftover make the input's road kind by kind (0.7.88; MapCheck sums it). |
| 484 | `public double budget` | The road its streets are made of: the input's, by kind, and in all. |
| 487 | `public int squares, boulevards, cellsOpen, homesCells` | How many cells the ladder squared and made boulevards of, as it asked (the prototype's figures); the cells opened, its homes cells. |
| 490 | `public int joins, shoreJoins, strays, mergesRefused, onFields` | Pieces the join joined; dead ends joined along a cut; street plots taken up as strays; merges refused for splitting the network; buildings placed on a field. |
| 493 | `public int seamsSurfaced` | One-sided seams this district surfaced from its road left over (0.7.88, ONE-SIDED SEAMS). |
| 496 | `public int joinsOut, partedOut` | Its pieces joined to the streets of the districts before it (0.7.88, ACROSS DISTRICTS), and those no way over its ground joins. |
| 499 | `public boolean[] parted` | Each street plot of the frame that is not on the city's network (0.7.88, PARTED): its piece - with the parted streets of the districts before it that it touches - touches no anchor, nor is the first district's largest. |
| 502 | `public int builds` | The plans drawn to climb the ladder (1 to a few). |
| 505 | `public int mixedSlots` | The slots the first band's types are dealt round (MIXED_SLOT): one more building moves only those placed after it while they are as many (MapCheck 2). |
| 644 | `int g, i, kNext, opened, homes, placed, nMine, nShared` |  |
| 645 | `boolean exhausted` |  |
| 646 | `int[] mine, px, py, pw, ph, pt, fails, failsF, mergeFails, cellRank` |  |
| 647 | `byte[] occ` |  |
| 648 | `boolean[] str, art, under, blvd, reachStale, cellCut, holdsNone, cellAcross, cellSquare, cellBoulevard, cel...` |  |
| 649 | `byte[] role, cellKind` |  |
| 650 | `int[] cellStreets` |  |
| 651 | `long[] rowBits` |  |
| 652 | `int[][] free, freeF, near, touch, failW, failH, failFW, failFH, mergeFailW, mergeFailH` |  |
| 653 | `final List<int[]> unplacedAt` |  |
| 654 | `int shoreJoins, strays, mergesRefused, onFields` |  |
| 702 | `Input in` | The plan's input, its rules and hashes: set by start() for each plan (KEPT). |
| 703 | `boolean proto` |  |
| 704 | `Hashes hash` |  |
| 706 | `boolean busy` | Whether a plan is being made on it: a make() within a make() is given a builder of its own. |
| 708 | `final boolean[] dry` | Dry owned ground: grass, forest or sand. |
| 710 | `final boolean[] blocked` | Plots no building may take: not dry, a highway, its verge (H5), track, a mine's site; and a field, until the last pass. |
| 712 | `final boolean[] fieldOk` | A field's plots a building may take once nothing else holds it: dry, off the highways, their verges and the track. |
| 714 | `final boolean[] cellField` | ...and the cells with any in their interior. |
| 716 | `final short[] hRun` | Fresh water's run along its row and its column, at each fresh plot. |
| 718 | `final int[][] openRows` | Each cell's interior plots no building is barred from (not `blocked`), a bit a plot by row as free[] holds them: a cell's free ground until it opens (0.7.90, ESTATE LINES). |
| 720 | `final int[][] fieldRows` | ...and its field plots a building may take once nothing else holds it (fieldOk), likewise (0.7.94, BITS). |
| 722 | `int[] order` | The cells in the order they open (spec 2.4), each ci + cj x 8. |
| 724 | `final int[][] bandType` | Each band's buildings in the order placed: type id, across, down. |
| 726 | `int mixedSlots` | The first band's slots (MIXED_SLOT). |
| 730 | `final byte[] occ` | Each plot: -1 no building may stand there (blocked, or a street), 0 free, 1 a building's (since 0.7.94 a byte: no reader asks which building). |
| 731 | `final boolean[] str` |  |
| 732 | `final byte[] role` |  |
| 733 | `final int[][] free` |  |
| 735 | `final int[][] freeF` | Each cell's free rows with its fields' plots free too: where a building with no other place may stand (spec 2.6). |
| 736 | `final boolean[] reachStale` |  |
| 738 | `final int[][] failW` | Shapes a cell is known to hold no box of, {across, down} pairs (a larger one holds none either), until its streets change. |
| 739 | `final int[] fails` |  |
| 741 | `final int[][] failFW` | ...and those it holds none of on its fields too. |
| 742 | `final int[] failsF` |  |
| 744 | `final int[][] mergeFailW` | ...and shapes no group of its blocks merges for: for good (a building in a group stays, the ground does not dry). |
| 745 | `final int[] mergeFails` |  |
| 746 | `final int[] changed` |  |
| 747 | `int nChanged` |  |
| 749 | `final boolean[] stub` | A dead end's stub, while the cut's join looks for its neighbour. |
| 751 | `final boolean[] cellCut` | Whether each cell's layout was laid whole, or the ground cut it (a plot refused): only a cut cell's merge can part the network. |
| 752 | `boolean refused` |  |
| 754 | `final int[] markA` | Marks for walks, by stamp, so none needs clearing; and a queue. |
| 755 | `int stamp` |  |
| 758 | `final long[] rowBits` |  |
| 760 | `final int[] runF` | tryPlace()'s free runs, worked out a row at a time as asked. |
| 761 | `int tryStamp` |  |
| 773 | `int[] shapeSlot, scanFrom, scanNFrom, scanEpochAt` | SCAN (0.7.89): for each cell, with its fields or not, and each shape the bands place (shapeSlot, nShapes), the first row tryPlace() need look from for a box touching a street - the row it found one at, past the last r... |
| 774 | `int nShapes` |  |
| 775 | `final int[] scanEpoch` |  |
| 776 | `int scanClock` |  |
| 784 | `int[] shapeW, shapeH` | Each shape's across and down, by shapeSlot. |
| 792 | `boolean[] holdsNone` | The fails memo (failW, failFW) as a table (0.7.89): whether cell c, with its fields or not, is known to hold no box of each shape - a shape no smaller than one it held none of, either way round, as the lists say - rea... |
| 810 | `int[] skipFrom` | SKIP (0.7.94): for each shape (shapeSlot), how far into its band's cells the table (holdsNone, without fields) says no cell holds a box of it - every cell before is known to hold none - so a building looks from there. |
| 812 | `final int[][][] anyT` | Each cell's touch and reach rows over a box's width, by width, kept until its reach moves: worked out again when its epoch (anyAt) is not the cell's (anyEpoch; 0.7.89, the arrays kept from build to build). |
| 813 | `final int[][] anyAt` |  |
| 814 | `final int[] anyEpoch` |  |
| 816 | `final long[][] reachRows` | reachCell()'s rows: the streets, two to step between and those one plot from one (0.7.89: kept, not made at each call). |
| 818 | `int[] shoreFrom, shoreQueue` | shore()'s walk over a cell's ring box: where each plot was reached from, and the queue (kept, as reachRows). |
| 1189 | `DistrictPlan bOut` | A BUILD'S RUNNING STATE (0.7.94, batch RD7): what build() kept in its locals until 0.7.93 - the plan so far, the ladder's step, the buildings placed, the next cell in the order, the cells opened and homes cells, wheth... |
| 1190 | `int bSquares, bBoulevards, bLimit, bPlaced, bNext, bOpened, bHomes, bNMine, bNShared` |  |
| 1191 | `boolean bExhausted, bShare, bByHomes` |  |
| 1192 | `int[] bMine, bTurnAt` |  |
| 1193 | `Turn[] bTurns` |  |
| 1194 | `List<int[]> bUnplaced` |  |
| 1326 | `int campusGroup` | campusStands()' reading: the group read, how far, whether one stood, and the undoing it was read at. |
| 1327 | `boolean campusAny` |  |
| 1342 | `final boolean[] campusCells` | The cells the refinery's units stand in (campusStands()), kept from call to call. |
| 1449 | `Turn[] turnPool` | The builder's turns (KEPT): the next one free, made the first time. |
| 1450 | `int turnsUsed` |  |
| 1592 | `final java.util.HashMap<LinesKey, Integer> cutLines` | ...and each answer for a cell that is not full, by everything it is worked out from - the cell, the building's shape, its direction and boulevard, its free ground and the streets in its window (LinesKey) - for the lad... |
| 1596 | `final long[] v` |  |
| 1597 | `final int hash` |  |
| 1604 | `final int[] linesFree` | estateLines()'s working rows: the cell's free ground, the window's streets, its touch and reach. |
| 1605 | `final long[] simS` |  |
| 1701 | `final byte[] layCode` | Each plot's lay code (LAY CODES). |
| 1988 | `int[] stubAt` | shore()'s stub, its plots (stubAt, nStub), and its walk's stamp a plot (shoreSeen against shoreStamp). |
| 1989 | `int nStub, shoreStamp` |  |
| 2152 | `final long[] openBits` | start()'s rows of the plots no building is barred from and of the fields, and each column's fresh run's first row. |
| 2153 | `final int[] runFrom` |  |
| 2159 | `final long[] vergeRows` | The verges' bits a row (VERGES), and the highways' rows spread along (vergeWideRow()). |
| 2162 | `int[] fixedList` | The plots of the city's runs (Input.fixed not 0), in plot order: their lay codes made apart (fixedCodes()). |
| 2163 | `int nFixed` |  |
| 2182 | `final byte[] occStart` | occ as a build begins: -1 where blocked, else 0 (start()). |
| 2184 | `final long[] hwRows` | The highways' plots, a bit each by row as rowBits (start()), and in plot order (hwList, nHighway); the parted streets of the districts before it likewise (paRows); whether a plot of the edge is an anchor. |
| 2185 | `int[] hwList` |  |
| 2186 | `int nHighway` |  |
| 2187 | `boolean anyAnchor` |  |
| 2190 | `final int[] laidRows` | freshCell()'s rows of the plots laid. |
| 2606 | `int[] splitEdge` | splits()'s streets about the box. |
| 2662 | `int[] placedX, placedY, placedW, placedH, placedT` | A build's buildings placed so far, each one's box and type: kept from build to build (0.7.89), the plan's own copies made of them. |
| 2665 | `final int[] targetMark` | reachFrom()'s targets: each plot's mark (its call's stamp) and how many times it is a target. |
| 2666 | `int targetStamp` |  |
| 2669 | `final int[] scratchLab` | The join's, the parting's and the surface's working arrays, a plot each, kept from build to build (0.7.90): each written before it is read (pieces() and markParted() set a label of -1 on each plot they label - the onl... |
| 2670 | `final boolean[] scratchParted` |  |
| 2706 | `int[] accDist, accPrev, accSeen` | access()'s search: each plot's distance from the work and the plot it was reached from, stamped (accSeen) so none is cleared; the queue by distance modulo 4 (an edge costs 1 to ACCESS_NEW, so four buckets are a Dial's... |
| 2707 | `int accStamp` |  |
| 2708 | `int[][] accQ` |  |
| 2709 | `int[] accN` |  |
| 2904 | `final int[] ordered` | streetsInOrder()'s plots and their count; and each piece's first plot (pieces()) and its place among them. |
| 2905 | `int nOrdered` |  |
| 2906 | `int[] pieceRep` |  |
| 2909 | `final int[] joinSeen` | The join's walks' marks, by stamp (0.7.94: no clearing of the way back before each): a plot reached when joinSeen holds the walk's. |
| 2910 | `int joinStamp` |  |
| 2959 | `int joinTail` | The join's walk's queue (queue[]) end: its steps add to it. |
| 3148 | `int partedOutCount` |  |
| 3151 | `int[] partedAt` | The join's parted pieces' first plots (scratchParted), listed so they are cleared one by one rather than the frame (0.7.94). |
| 3152 | `int nPartedAt` |  |
| 3196 | `int[] partSize` | markParted()'s pieces: each one's plots with a street code, and whether it touches an anchor. |
| 3197 | `boolean[] partOn` |  |
| 3313 | `int sN, sOpen, sAll` | surface()'s counts: the streets it surfaces, its one-sided seams, every street. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 176 | 3179 | **type** `public final class DistrictPlan` | One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it, the cells it opens and the layout of each, every street with its kin... |

### THE GEOMETRY (lines 178-265)

### the inputs' codes (lines 266-295)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 1 | `static boolean works(byte f)` | Whether a fixed code is a works' own ground (a yard's, an interchange's reserve): no building and no street on it. |

### a street plot's code (lines 296-340)

### THE INPUTS (lines 341-435)

| line | len | member | says |
|---:|---:|---|---|
| 346 | 8 | **type** `public interface Hashes` | The hashes a plan is drawn with: the world's, or (PlanCheck's replay) the prototype's own. |
| 348 | 1 | `double cell(int ci, int cj)` _(in DistrictPlan.Hashes)_ | A cell's nudge on its distance from the hub. |
| 350 | 1 | `double dir(int ci, int cj)` _(in DistrictPlan.Hashes)_ | A cell's direction: under a half its long blocks run east-west. |
| 352 | 1 | `double deal(int type)` _(in DistrictPlan.Hashes)_ | A type's start in the first band's deal. |
| 356 | 44 | **type** `public static final class Input` | Everything a plan is drawn from: filled by CityMap.planInput(), or by hand in a harness. |
| 402 | 8 | `static Hashes worldHashes(long seed, long x0, long y0)` | The world's hashes for a district: its cells' by their place in the world, its types' by the district. |
| 412 | 3 | `static double unit(long h)` | A hash as a number in [0, 1). |
| 425 | 10 | `static boolean along(byte[] fixed, int p, boolean horizontal)` | Whether a highway or railway plot p runs along a street through it going east-west (horizontal) or north-south, rather than across it. |

### THE PLAN (lines 436-695)

| line | len | member | says |
|---:|---:|---|---|
| 507 | 1 | `private DistrictPlan()` |  |
| 510 | 3 | `public boolean isStreet(int p)` | Whether frame plot p is a street of any kind (beneath a highway included). |
| 515 | 3 | `public int kindAt(int p)` | A plot's street kind (NONE to UNDER). |
| 520 | 4 | `public double widthAt(int p)` | A plot's surface in plots of its right of way: 0, HALF or FULL. |
| 526 | 3 | `public int roleAt(int p)` | A plot's role (ROLE_STREET to ROLE_JOIN), 0 for none. |
| 531 | 5 | `public int overflowCount()` | The buildings it could not place. |
| 543 | 15 | `public static DistrictPlan make(Input in)` | The plan of a district (spec 2.9): the first plan with long blocks; the surface it leaves over squares the homes cells nearest the hub and then makes boulevards of the arterials nearest it - the prototype's plan_ladde... |
| 575 | 41 | `private static DistrictPlan ladder(Builder b)` | The ladder (make()) on builder b, started on its input. |
| 618 | 3 | `static void forgetFullLines()` | The layouts full estate cells were found to take, forgotten (ESTATE LINES' FULL_LINES): for PlanCheck, which shows a plan is the same worked out afresh. |
| 623 | 6 | `static int[] chain(int k, boolean squares)` | The steps after step k of a chain of the ladder, by what each halves: the boulevards (3 to 1, as make() halves them) or, with none, the squares. |
| 643 | 13 | **type** `static final class Turn` | A build's state where a later step of the ladder turns from it (THE LADDER'S TURNS, 0.7.89): every array and count a build changes, as they stood just before it opened a cell - the cell a boulevard's step after it wou... |
| 658 | 5 | `static int[] copyInto(int[] src, int n, int[] dst)` | src's first n into dst, a new array where dst is null or shorter (KEPT): dst, or the array made. |
| 664 | 5 | `static byte[] copyInto(byte[] src, byte[] dst)` |  |
| 670 | 5 | `static boolean[] copyInto(boolean[] src, boolean[] dst)` |  |
| 676 | 5 | `static long[] copyInto(long[] src, long[] dst)` |  |
| 683 | 5 | `static int[][] copyRows(int[][] rows, int[][] dst)` | Each row of rows into dst's (rows all made: a cell's free, reach or touch rows). |
| 690 | 5 | `static int[][] copyLists(int[][] lists, int[] counts, int[][] dst)` | Each cell's list of shapes, its first counts[c] of them, into dst's (a list with none left as it is: a build makes it afresh before it adds one). |

### THE BUILDER: ONE PLAN AT A LADDER'S STEP (lines 696-3354)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 2654 | **type** `private static final class Builder` |  |
| 779 | 3 | `void rescan(int c)` _(in DistrictPlan.Builder)_ | Cell c's rows to be scanned from the top again (SCAN): its room grew, or its reach moved. |
| 795 | 7 | `void forgetFails(int c)` _(in DistrictPlan.Builder)_ | What cell c was known not to hold, forgotten: its reach grew or its streets were taken up. |
| 821 | 1 | `Builder()` _(in DistrictPlan.Builder)_ | A builder, its arrays made: start() sets it on a plan's input. |
| 824 | 5 | `boolean worn()` _(in DistrictPlan.Builder)_ | Whether a count it stamps with has grown so far it could come round (KEPT): replaced by a new builder before the next plan. |
| 834 | 42 | `void start(Input in)` _(in DistrictPlan.Builder)_ | The plan of input `in` begun on this builder (KEPT): every array its plan reads of the ground filled afresh - what the constructor did until 0.7.93. |
| 878 | 6 | `void finish()` _(in DistrictPlan.Builder)_ | The plan made: what it held of its input let go (KEPT). |
| 887 | 48 | `int[] growthOrder()` _(in DistrictPlan.Builder)_ |  |
| 937 | 5 | `static boolean nearer(double[] dist, int a, int b)` _(in DistrictPlan.Builder)_ | Whether cell a comes before cell b in the growth order: nearer the hub (with its nudge), then the west column, then the north row. |
| 945 | 4 | `static int bandOf(BuildingVisual.Type t)` _(in DistrictPlan.Builder)_ |  |
| 972 | 7 | `static int campusFirst(BuildingVisual.Type[] types, int a, int b)` _(in DistrictPlan.Builder)_ | The order of two types in a band: a refinery unit before anything else, then (0.8.0) each works group in turn, else none (0). |
| 980 | 72 | `void bands()` _(in DistrictPlan.Builder)_ |  |
| 1062 | 107 | `DistrictPlan build(int nSquare, int nBlvd, int limit, Turn from, int[] turnAt, boolean byHomes, Turn[] turns)` _(in DistrictPlan.Builder)_ | A plan at a ladder's step: nSquare homes cells squared, nBlvd boulevards; null as soon as more than `limit` buildings have no place. |
| 1171 | 7 | `DistrictPlan let(DistrictPlan p)` _(in DistrictPlan.Builder)_ | A build's plan returned, what the build held let go (its turns, its plan so far). |
| 1197 | 94 | `boolean placeOne(int g, int i, boolean looked)` _(in DistrictPlan.Builder)_ | Building i of band g placed (`looked`: gone on with from a turn, its look over its band's cells made before it): whether the build is to stop, more than bLimit left out. |
| 1300 | 5 | `int campusNext(DistrictPlan out, int group)` _(in DistrictPlan.Builder)_ | The campus's next cell (THE CAMPUS): the index in the order, from the next to open on, of the first closed cell touching (eight ways: a side or a corner) a cell a refinery unit already stands in - since 0.8.0 a unit o... |
| 1307 | 17 | `boolean campusStands(int group)` _(in DistrictPlan.Builder)_ | Whether a unit of a group stands yet (the refinery's, or since 0.8.0 a works group's), the cells they stand in marked in campusCells - read on from the last look while the same group's and nothing placed was undone (c... |
| 1330 | 10 | `boolean nearCampus(int c)` _(in DistrictPlan.Builder)_ | Whether cell c is one of campusCells or touches one, eight ways (a side or a corner). |
| 1345 | 55 | `Turn turn(DistrictPlan out, int g, int i, int kNext, int opened, int homes, int placed, boolean exhausted, int[] mine, int nMin...` _(in DistrictPlan.Builder)_ | This build's state as it stands, at building i of band g with the cell order[kNext] about to open (THE LADDER'S TURNS). |
| 1402 | 45 | `void resume(Turn t, DistrictPlan out)` _(in DistrictPlan.Builder)_ | This build put back as turn t stood, out its plan so far; what is stamped (walks, SCAN, the touch rows by width) worked out afresh. |
| 1452 | 7 | `Turn nextTurn()` _(in DistrictPlan.Builder)_ |  |
| 1461 | 3 | `void freeTurns()` _(in DistrictPlan.Builder)_ | Every turn free again: a new chain of the ladder begins (the last chain's are done with). |
| 1466 | 5 | `void setStr(int p, boolean on)` _(in DistrictPlan.Builder)_ | A street laid on plot p, or taken up: the plot and its bit. |
| 1473 | 10 | `long window(int y, int wx, int n)` _(in DistrictPlan.Builder)_ | The street bits of row y from plot wx, n of them (n < 64), bit b for plot wx + b; none past the frame. |
| 1485 | 9 | `void put(int c, int x, int y, int w, int h)` _(in DistrictPlan.Builder)_ | A building on box (x, y, w, h) of cell c: its plots taken, the cell's free rows with them. |
| 1498 | 71 | `void openCell(DistrictPlan out, int c, byte kind, int rank, boolean square, boolean bl, int w, int h)` _(in DistrictPlan.Builder)_ | Cell c opened as `kind` at its rank, squared or a boulevard as the ladder's step asks: its ring and its streets laid - an estate cell's sized to the w x h building it opens for (ESTATE LINES). |
| 1582 | 5 | `static int sizedLines(int depth)` _(in DistrictPlan.Builder)_ | The lines strips sized to two rows of a building `depth` plots deep back to back would put streets on (ESTATE LINES): a strip of max(2 x depth, STRIP_LEAST) plots from the arterial, a street, the next, while a street'... |
| 1595 | 7 | **type** `static final class LinesKey` _(in DistrictPlan.Builder)_ | What estateLines() reads of a cell that is not full, as a key. |
| 1598 | 1 | `LinesKey(long[] v)` _(in DistrictPlan.Builder.LinesKey)_ |  |
| 1599 | 1 | `public int hashCode()` _(in DistrictPlan.Builder.LinesKey)_ |  |
| 1600 | 1 | `public boolean equals(Object o)` _(in DistrictPlan.Builder.LinesKey)_ |  |
| 1619 | 54 | `int estateLines(int c, boolean across, boolean bl, int w, int h)` _(in DistrictPlan.Builder)_ | ESTATE LINES: which lines of cell c's interior its streets take - and FLIP when they run the other way from `across` - for the w x h building it opens for, its ring laid: of the prototype's spine (MIDDLE) and the stri... |
| 1675 | 3 | `boolean streetAt(int x, int y)` _(in DistrictPlan.Builder)_ | Whether plot (x, y) is a street now (not beneath a highway): what estateLines() reads a ring whole by. |
| 1680 | 6 | `int lineKind(int x, int y, boolean horizontal)` _(in DistrictPlan.Builder)_ | What lay() would make of plot (x, y) on a cell's street running east-west (horizontal) or north-south, laying nothing: 0 refused, 1 a street, 2 a street beneath a highway (layPlot()'s rules, the game's). |
| 1712 | 70 | `int holds(int x0, int y0, boolean across, int lines, int w, int h)` _(in DistrictPlan.Builder)_ | How many w x h boxes cell c (its interior from x0, y0) holds with its streets on `lines`, running north-south when `across`: its free ground (linesFree) less the streets, its touch and reach from the streets about it ... |
| 1790 | 5 | `boolean lay(int x, int y, boolean horizontal, boolean arterial, byte r)` _(in DistrictPlan.Builder)_ | A street plot at (x, y) of a line running east-west (horizontal) or north-south: owned, not the sea - the prototype's _street - and, with the spec's rules, not a mine's site, fresh water only as a bridge no wider than... |
| 1796 | 17 | `boolean layPlot(int x, int y, boolean horizontal, boolean arterial, byte r)` _(in DistrictPlan.Builder)_ |  |
| 1821 | 47 | `int prune()` _(in DistrictPlan.Builder)_ | NO STRAY ROADS (Jerus: "all roads connected, no stray roads"): of the plots a cell's opening laid, each piece of the whole network smaller than LATTICE plots - a street the ground cut down to less than a block, touchi... |
| 1870 | 8 | `int streetNeighbours(int p)` _(in DistrictPlan.Builder)_ | A plot's street neighbours, beneath a highway included. |
| 1880 | 7 | `boolean cutAt(int x, int y)` _(in DistrictPlan.Builder)_ | Whether a plot is ground the cut runs along: not the city's, the sea, fresh water no street crosses, or past the frame. |
| 1897 | 89 | `int shore(int bx0, int by0, int bx1, int by1)` _(in DistrictPlan.Builder)_ | WHERE THE GROUND CUTS A CELL (spec 2.4, H2: streets favour the grid but need not keep it). |
| 2001 | 15 | `void freshCell(int c)` _(in DistrictPlan.Builder)_ | A cell's free rows as it opens (BITS, 0.7.94): its ground no building is barred from (openRows) less the streets its opening laid (the changed plots) - what refreeCell() reads of occ, since a cell not open holds no bu... |
| 2018 | 20 | `void groundRow(int y)` _(in DistrictPlan.Builder)_ | Row y of the ground (THE GROUND, A ROW AT A TIME): dry, barred, its lay code from the ground, the runs' plots, the highways' and the parted streets' bits, an anchor. |
| 2040 | 8 | `void fixedPlot(int p, int x, int base, byte f)` _(in DistrictPlan.Builder)_ | A plot of the city's runs: listed, and a highway's in its row's bits and listed too. |
| 2050 | 12 | `void freshRow(int y)` _(in DistrictPlan.Builder)_ | Row y's fresh water runs (THE GROUND, A ROW AT A TIME): each plot of a run its length along the row, a street refused along one wider than its bridge. |
| 2064 | 12 | `void freshColumns(int y)` _(in DistrictPlan.Builder)_ | ...and down each column, read at row y: a column's run goes on where its plot is fresh water, and ends where it is not (runFrom, its first row). |
| 2078 | 3 | `void freshColumnsEnd()` _(in DistrictPlan.Builder)_ | ...the runs still open at the frame's last row ended there. |
| 2083 | 5 | `void columnRun(int x, int end)` _(in DistrictPlan.Builder)_ | Column x's run from runFrom[x] to the row before `end`: its length on each plot, a street refused down one wider than its bridge. |
| 2090 | 15 | `void fieldRow(int y)` _(in DistrictPlan.Builder)_ | Row y's fields (spec 2.6) and the rest (THE GROUND, A ROW AT A TIME): barred with the verges, occ as a build begins, the field plots, both as bits. |
| 2107 | 9 | `void cellRows(int c)` _(in DistrictPlan.Builder)_ | Cell c's interior rows of free ground and of fields, from the rows' bits, and whether it has a field. |
| 2124 | 12 | `void vergeWideRow(int y)` _(in DistrictPlan.Builder)_ | VERGES (H5, 0.7.94): the plots on or beside a highway plot, corners included - no building's - as bits a row: the highways' bits (hwRows) spread a plot either way along the row (vergeWideRow()), then a row either way ... |
| 2137 | 5 | `void vergeRow(int y)` _(in DistrictPlan.Builder)_ |  |
| 2144 | 6 | `static long bitsOf(long[] rows, int y, int x0)` _(in DistrictPlan.Builder)_ | The INTERIOR bits of row y of `rows` (WORDS longs a row) from plot x0: a cell's interior row (x0 from 1 to SIDE - INTERIOR). |
| 2166 | 14 | `void fixedCodes()` _(in DistrictPlan.Builder)_ | Each plot of a run's lay code (LAY CODES): a run along a line refuses it that way, a highway crossed is laid beneath - worked out for those plots alone, after start() gave them the ground's. |
| 2193 | 14 | `void refreeCell(int c)` _(in DistrictPlan.Builder)_ | A cell's free rows from occ: bit i of row j for interior plot (x0 + i, y0 + j). |
| 2209 | 5 | `void staleAround(int c)` _(in DistrictPlan.Builder)_ | The cells whose reach a change of c's streets may move: it and the eight about it, their reach worked out again when next asked (reachCell() forgets what a cell was known not to hold if its reach grew). |
| 2219 | 3 | `static long spread(long a)` _(in DistrictPlan.Builder)_ | A window's row of bits spread a plot either way along it, kept to the window. |
| 2224 | 34 | `void reachCell(int c)` _(in DistrictPlan.Builder)_ | A cell's reach rows: plots within REACH of a street, across corners, and those one plot from one - the prototype's reach_map, over the cell's interior and the REACH plots about it. |
| 2260 | 5 | `static int runs(int r, int w)` _(in DistrictPlan.Builder)_ | Rows where a run of w set bits starts: bit i set where bits i .. |
| 2267 | 7 | `static int anyIn(int r, int w)` _(in DistrictPlan.Builder)_ | ...and where a box w wide starting there holds any set bit. |
| 2282 | 99 | `long tryPlace(int c, int w, int h, boolean fields)` _(in DistrictPlan.Builder)_ | The first spot in cell c where a w x h building fits on free ground within reach of a street - touching one first (a plot of it one from a street, across corners), then within REACH - in rows from the cell's north-wes... |
| 2389 | 31 | `boolean closeSpine(DistrictPlan out, int c)` _(in DistrictPlan.Builder)_ | An estate cell taken whole (spec 2.3: "a building over 15 plots ... |
| 2422 | 15 | `static int remember(int[][] ws, int[][] hs, int n, int c, int w, int h)` _(in DistrictPlan.Builder)_ | A shape a cell holds no box of, kept with the others it is no larger than: those larger than it go (they hold none either). |
| 2444 | 57 | `boolean mergeFor(DistrictPlan out, int c, int w, int h)` _(in DistrictPlan.Builder)_ | In a homes cell: the street segments inside the first group of long blocks (MERGE_GROUPS, in order) that holds the building, has no building in it and is more than MERGE_DRY dry ground or street, returned to ground - ... |
| 2508 | 59 | `boolean splits(int gx, int gy, int gw, int gh)` _(in DistrictPlan.Builder)_ | Whether taking up the streets inside box (gx, gy, gw, gh) would part streets outside it that reach each other now: the streets beside the box, each that the first reaches through the network, must still reach it witho... |
| 2575 | 26 | `boolean meets(int p, int gx, int gy, int gw, int gh, int near, int apart)` _(in DistrictPlan.Builder)_ | Whether a walk from street plot p, round box (gx, gy, gw, gh), meets a plot joined to the near walk's first (markB holding `near`). |
| 2609 | 12 | `boolean ringWhole(int gx, int gy, int gw, int gh)` _(in DistrictPlan.Builder)_ | Whether every plot of the ring just outside the box (its corners among them) is in the frame and a street or beneath a highway: a closed loop of street round the box, joining every street that meets it. |
| 2623 | 3 | `boolean reachFrom(int p0, int gx, int gy, int gw, int gh, int[] mark, int st, int[] targets)` _(in DistrictPlan.Builder)_ | Marks the streets reached from plot p0 with stamp st in mark, four-connected, none inside box (gx, gy, gw, gh) when gx >= 0; whether every plot of targets (when given) was reached, the walk stopping when they are. |
| 2628 | 32 | `boolean reachFrom(int p0, int gx, int gy, int gw, int gh, int[] mark, int st, int[] targets, int rx0, int ry0, int rx1, int ry1)` _(in DistrictPlan.Builder)_ | ...keeping within plots (rx0, ry0) to (rx1, ry1), inclusive. |
| 2675 | 11 | `boolean joinable(int a, boolean horizontal)` _(in DistrictPlan.Builder)_ | Whether a lattice step onto plot a going horizontally (or not) may be laid: the join's ground. |
| 2688 | 7 | `boolean groundJoinable(int a, boolean horizontal)` _(in DistrictPlan.Builder)_ | Whether a step onto plot a going horizontally (or not) may be laid off the lattice, where the join's lattice finds no way: as joinable(), on any plot (H2: a street follows the ground where it must). |
| 2697 | 5 | `void layJoin(int a)` _(in DistrictPlan.Builder)_ | Lays a plot the join or the mines' reach took: a street, or beneath a highway. |
| 2738 | 55 | `void access(DistrictPlan out, int placed)` _(in DistrictPlan.Builder)_ | ACCESS ROADS. |
| 2795 | 7 | `int gate(int[] gates, int n, int x, int y)` _(in DistrictPlan.Builder)_ | Adds plot (x, y) to the gates if it is one the road may use (inside the frame's own plots, ground a street may take either way); the count. |
| 2804 | 6 | `boolean accessible(int a, boolean horizontal)` _(in DistrictPlan.Builder)_ | Whether the access road may enter plot a going horizontally (or not): a plot of the frame's own, the ground a street may take, and a highway's only where a street passes beneath it already. |
| 2812 | 5 | `boolean accessEnd(int a, boolean ramps, int cx, int cy)` _(in DistrictPlan.Builder)_ | Whether plot a is where a road ends: a ramp's (a highway plot carrying a ramp with a street beneath, within reach of the work at (cx, cy)), or an arterial street. |
| 2823 | 39 | `int accessSearch(int[] gates, int n, boolean ramps, int cx, int cy)` _(in DistrictPlan.Builder)_ | The nearest end from the gates, by plot cost (Dial's algorithm: costs 1 and ACCESS_NEW), as the plot it ends on with accPrev leading back to a gate (its own accPrev -1); -1 for none. |
| 2864 | 1 | `int accCost(int a)` _(in DistrictPlan.Builder)_ | What entering plot a costs the access road: 1 where a street (or one beneath a highway) already is, ACCESS_NEW where it would be laid. |
| 2866 | 6 | `void accPush(int d, int a)` _(in DistrictPlan.Builder)_ |  |
| 2881 | 5 | `int streetsInOrder(long[] alsoRows)` _(in DistrictPlan.Builder)_ | THE STREETS IN ORDER (0.7.94, batch RD7): every street plot - and beneath a highway, and with `alsoRows` its plots too - into ordered[], in plot order, read from the street bits a row of 64 at a time (rowBits; a highw... |
| 2888 | 14 | `int streetsInRow(int y, long[] alsoRows, int n)` _(in DistrictPlan.Builder)_ | ...row y's, into ordered[] from n: the count after them (a row a call, THE GROUND, A ROW AT A TIME's reason). |
| 2913 | 3 | `int[] pieces(int[] lab)` _(in DistrictPlan.Builder)_ | The street pieces, four-connected (beneath a highway included): each plot's label into lab, the sizes returned. |
| 2918 | 26 | `int[] pieces(int[] lab, boolean parted)` _(in DistrictPlan.Builder)_ | ...with the parted streets of the districts before it (Input.partedAnchor) as streets of the pieces too (joinOut()) when `parted`; each piece's first plot in plot order into pieceRep, its place in ordered[] into pieceAt. |
| 2946 | 11 | `int pieceStep(int[] lab, int u, int n, int sp, boolean[] also)` _(in DistrictPlan.Builder)_ | pieces()'s step (a plot a call): plot u's neighbours of piece n - streets, beneath a highway, or of `also` - not yet labelled, labelled and stacked above sp. |
| 2962 | 15 | `int joinStep(int u, int way, int pc, int st, int[] lab, int[] prev)` _(in DistrictPlan.Builder)_ | joinFromSmall()'s walk's step (a plot a call): plot u's neighbours along the lattice (way 0) or over the ground, from piece pc, by walk st - the first street of another piece reached, or -1, the rest queued. |
| 2979 | 18 | `int joinOutStep(int u, int way, int pc, int st, int[] lab, int[] prev, boolean[] touches)` _(in DistrictPlan.Builder)_ | joinOut()'s walk's step: as joinStep(), the walk ending at a street of a piece that touches an anchor, or beside an anchor. |
| 2999 | 8 | `int seedPiece(int pc, int[] lab, int[] prev, int[] q, int st)` _(in DistrictPlan.Builder)_ | A walk's start: piece pc's plots, in plot order, into queue q from its first (pieces()), each reached by walk st with no way back. |
| 3018 | 31 | `int join()` _(in DistrictPlan.Builder)_ | ONE NETWORK (the prototype's _join): while the streets are in more than one piece, breadth first from the largest along the lattice's lines to the nearest other piece, and the way laid - over a long block's middle lin... |
| 3058 | 34 | `int joinFromSmall()` _(in DistrictPlan.Builder)_ | The join with the game's rules: each piece but the largest, the smallest first, looks for the nearest other piece - along the lattice, then over the ground (H2) - from its own plots, which is the same way back as the ... |
| 3110 | 37 | `int joinOut()` _(in DistrictPlan.Builder)_ | ACROSS DISTRICTS (spec 2.4: "a district whose first cell does not touch an open cell of its inner neighbour opens an arterial run to it along the lattice"; 0.7.88, batch RD2 - RD1 had no neighbour's plan to join to). |
| 3154 | 5 | `void markPartedPiece(int p)` _(in DistrictPlan.Builder)_ |  |
| 3160 | 4 | `void clearParted()` _(in DistrictPlan.Builder)_ |  |
| 3166 | 28 | `void markParted(DistrictPlan out)` _(in DistrictPlan.Builder)_ | PARTED (0.7.88): out.parted - each street plot whose piece, over the plan's final streets and the parted streets of the districts before it, touches no anchor, nor is the first district's largest piece. |
| 3200 | 13 | `int partedStep(DistrictPlan out, int u, int n, int sp)` _(in DistrictPlan.Builder)_ | markParted()'s step (a plot a call): plot u of piece n counted, whether it touches an anchor, its neighbours of the piece not yet labelled labelled and stacked above sp. |
| 3215 | 6 | `boolean nearAnchor(int p)` _(in DistrictPlan.Builder)_ | Whether plot p is, or is beside (four ways), a plot of the frame's edge a district before this one lays a street on. |
| 3225 | 13 | `boolean surfaces(int x, int y)` _(in DistrictPlan.Builder)_ | Whether this district surfaces a plot: off its seams, or on a seam it comes first on (Input.surfaces). |
| 3247 | 64 | `void surface(DistrictPlan out)` _(in DistrictPlan.Builder)_ | The model's road as the streets' surface (the prototype's, over its street plots): arterials first, then by their cell's rank from the hub, then row by row - every street HALF while the road lasts, a TRACK where it do... |
| 3316 | 22 | `void surfaceRow(DistrictPlan out, int y, int[] start)` _(in DistrictPlan.Builder)_ | surface()'s row y (a row a call): each street every street's (scratchLab); one it surfaces with its bucket (scratchPrev, pieceStack), counted in start[]; a one-sided seam (ordered). |
| 3340 | 13 | `void code(DistrictPlan out, int p)` _(in DistrictPlan.Builder)_ | Plot p's street code made whole: its kind (a seam with none, beneath a highway), role, bridge and crossing. |


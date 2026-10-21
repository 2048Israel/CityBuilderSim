# DistrictPlan.java - 4,993 lines · 179 methods · 87 constants · model

`ham/citybuildersim/DistrictPlan.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
>   LAYOUT  (batch CL2) a homes cell's streets are one of three layouts, by
>           a hash of the world's seed and the cell's place: the comb (a
>           squared cell: the whole square) as before, DROPPED crossings, or
>           BRICK (see a homes cell's layout, in the builder); a cell a
>           highway, railway, yard or reserve crosses keeps the comb; a merge
>           in one of the last two takes up the dead ends it leaves, or is
>           undone;
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
> ... (158 more lines in the source)

**Uses:** [BuildingVisual](BuildingVisual.md) (30), [TilePainter](TilePainter.md) (27), [World](World.md) (22), [CityMap](CityMap.md) (2), [CityRuns](CityRuns.md) (2), [BuildingType](BuildingType.md) (1)

**Used by (13):** [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [DistrictPicture](DistrictPicture.md), [EarthTerrainCheck](EarthTerrainCheck.md), [JunctionCheck](JunctionCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 229 | THE GEOMETRY |
| 266 | · a homes cell's layout (CL2) |
| 355 | · the inputs' codes |
| 385 | · a street plot's code |
| 450 | THE INPUTS |
| 581 | THE PLAN |
| 902 | THE BUILDER: ONE PLAN AT A LADDER'S STEP |
| 1120 | · the order of arrival (batch CL3) |
| 1713 | · the ground bought (batch FX6) |
| 1825 | · the growth order (spec 2.4) |
| 1883 | · the bands (spec 2.4, R5, R6) |
| 2005 | · one build |
| 2502 | · opening a cell (spec 2.3) |
| 2583 | · a homes cell's layout (CL2, 2026-10-20) |
| 2820 | · an estate cell's streets (0.7.90, ESTATE LINES) |
| 3241 | · a cell's room and reach, in bit rows |
| 4090 | · the join: one network |
| 4121 | · access roads (batch FR7, slice 4; spec-freight 4.5, D17) |
| 4291 | · sidings (batch FR7, slice 5; spec-freight 4.3, D14) |
| 4861 | · the surface (spec 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 234 | `DistrictPlan.SIDE` | `CityMap.DISTRICT` | Plots on a district's side: CityMap.DISTRICT, 256. |
| 237 | `DistrictPlan.FRAME` | `SIDE + 1` | Plots on the plan's side: the district and the first column (row) of its east (south) neighbour, where its edge cells' last arterial runs - 257. |
| 240 | `DistrictPlan.AREA` | `FRAME * FRAME` | Plots in the frame. |
| 243 | `DistrictPlan.CELL` | `World.TILE` | A cell's side, arterial to arterial: a tile, World.TILE, 32 plots (960 m) - its west column and north row its arterials (spec 2.3). |
| 246 | `DistrictPlan.INTERIOR` | `CELL - 1` | A cell's interior: 31 plots a side, between its arterials. |
| 249 | `DistrictPlan.CELLS_A_SIDE` | `SIDE / CELL` | Cells on a district's side: 8. |
| 252 | `DistrictPlan.CELLS` | `CELLS_A_SIDE * CELLS_A_SIDE` | Cells in a district: 64. |
| 255 | `DistrictPlan.LATTICE` | `TilePainter.JUNCTION_APART` | The street lattice: lines every TilePainter.JUNCTION_APART (8) plots, the + junction floor - a cell's west arterial on one, so the lattice runs on from cell to cell. |
| 258 | `DistrictPlan.REACH` | `TilePainter.REACH` | A building is within reach of a street when one lies within this many plots of it, across corners: TilePainter.REACH, 4. |
| 261 | `DistrictPlan.STREETS_AT` | `{ LATTICE - 1, 2 * LATTICE - 1, 3 * LATTICE - 1 }` | Where a cell's streets run, in plots into its interior: 7, 15 and 23 - the lattice's lines inside it (spec 2.3's long blocks of 15 x 7, square blocks of 7 x 7). |
| 264 | `DistrictPlan.MIDDLE` | `2 * LATTICE - 1` | ...and a homes cell's cross street and an estate cell's spine: the middle line, 15 - long blocks 15 plots long either side of it. |
| 269 | `DistrictPlan.LAYOUT_TODAY` | `0, LAYOUT_DROPPED = 1, LAYOUT_BRICK = 2` | A homes cell's street layout, chosen by layoutOf(): TODAY the comb (a squared cell: the whole square) as ever, DROPPED some of its crossings left out, BRICK its cross street's pieces at alternating lines (a squared ce... |
| 272 | `DistrictPlan.TODAY_SHARE` | `1.0 / 3` | The share of homes cells, long-block and squared alike, that keep today's layout: a third (Jerus, 2026-10-20: "A third stays"). |
| 275 | `DistrictPlan.DROPPED_SHARE` | `1.0 / 3` | ...and the share that drop crossings (the rest are brick), likewise: a third. |
| 278 | `DistrictPlan.HALF_DROP` | `0.25` | The chance a dropped long-block cell also loses half of a long street (its block beside it 15 deep): 0.25. |
| 281 | `DistrictPlan.HOLE_MOST` | `49` | The most plots of a long-block cell a lost half of a long street may leave beyond REACH of every street (a green hole in the block): 49, one 7 x 7. |
| 284 | `DistrictPlan.PIECE_DROP` | `0.22` | The chance a dropped squared cell loses each of its 24 street pieces: 0.22. |
| 287 | `DistrictPlan.BRICK_LINES` | `{ { 11, 19 }, { 9, 21 }, { 7, 23 }, { 7, 19 }, { 11, 23 } }` | The lines a brick long-block cell's cross street may alternate between, in plots into the interior: a pair for the cell's hash to pick from, 8 or more apart so no two junctions on a long street are nearer than the + f... |
| 290 | `DistrictPlan.BRICK_BAND_LINES` | `{ 11, 19 }` | ...and the two lines a brick squared cell's cross streets take in the bands between those at 7, 15 and 23: 11 and 19. |
| 293 | `DistrictPlan.DROPPED_TRIES` | `2` | The draws a dropped long-block cell makes to keep its holes within HOLE_MOST: 2, the second without the half of a long street. |
| 296 | `DistrictPlan.PIECES_TRIES` | `3` | ...and a dropped squared cell makes to leave nothing beyond REACH: 3, then the whole square. |
| 299 | `DistrictPlan.SEG_FROM` | `{ 0, 8, 16, 24 }, SEG_TO = { 6, 14, 22, 30 }` | The ends of a line's four pieces between its crossings (the ring, the lines at 7, 15, 23, the ring), in plots into the interior: from 0, 8, 16, 24 to 6, 14, 22, 30. |
| 302 | `DistrictPlan.LINE_FIRST` | `LATTICE - 1` | An estate cell's streets lie on interior lines from this one (0.7.90, ESTATE LINES): 7, LATTICE - 1 - so where one meets a neighbour's street across an arterial, the + it makes is LATTICE or more from the ring's corne... |
| 305 | `DistrictPlan.LINE_LAST` | `INTERIOR - LATTICE` | ...to this one: 23, INTERIOR - LATTICE, the same from the far corner. |
| 308 | `DistrictPlan.STRIP_LEAST` | `LINE_FIRST` | A strip's least depth between an estate cell's streets: 7, LINE_FIRST - so its streets are LATTICE or more apart, and so are the + junctions two of them could make on one arterial. |
| 311 | `DistrictPlan.STREET_BRIDGE` | `TilePainter.STREET_BRIDGE` | A street's longest crossing of fresh water: TilePainter.STREET_BRIDGE, 6 plots (spec 2.4, today's street bridge). |
| 314 | `DistrictPlan.ARTERIAL_BRIDGE` | `TilePainter.MAX_BRIDGE [ BuildingVisual.HIGHWAY ]` | An arterial's: a highway's, TilePainter.MAX_BRIDGE[HIGHWAY], 14 plots (spec 2.4) - wider water is a landmass's edge (spec 3). |
| 317 | `DistrictPlan.CELL_ROOM_LEAST` | `120` | A cell is opened only with at least this many plots of dry owned ground off the highways in its interior: 120 of 961, the prototype's. |
| 320 | `DistrictPlan.NUDGE` | `0.6` | The hashed nudge on a cell's distance from the hub, in cells: up to 0.6, the prototype's - the open cells' edge ragged, not a disc. |
| 323 | `DistrictPlan.MIXED_SLOT` | `640` | The first band's buildings are dealt round one slot for every this many plots of their footprints: 640, the prototype's - about a homes cell's room after its streets, so the slots are about its cells and a cell holds ... |
| 326 | `DistrictPlan.LONG_BLOCK_STREETS` | `121` | A long-block cell's street plots inside its ring: 121 - a square cell's 177 (3 rows and 3 columns) less SQUARE_PLOTS. |
| 329 | `DistrictPlan.SQUARE_PLOTS` | `56` | The surface the ladder's square blocks take a homes cell: 56 plots, the prototype's - a square cell's streets (3 rows and 3 columns, 177 plots) less a long-block cell's (121, LONG_BLOCK_STREETS). |
| 332 | `DistrictPlan.BOULEVARD_PLOTS` | `100` | ...and a boulevard a cell: 100 plots, the prototype's - its arterials' second row, less what neighbouring boulevards share. |
| 335 | `DistrictPlan.MERGE_GROUPS` | `{ { 15, 15 }, { 15, 23 }, { 15, 31 }, { 31, 15 }, { 31, 31 } }` | The groups of long blocks a homes cell merges for a building wider than a block, {across the blocks, along them} in plots: two blocks, three, four, two across, the whole cell (the prototype's; spec 2.3: 15 x 15, 15 x ... |
| 338 | `DistrictPlan.MERGE_DRY` | `0.95` | A group of blocks is merged only when more than this share of it is dry ground or street: 0.95, the prototype's. |
| 341 | `DistrictPlan.BLOCK_DEPTH` | `LATTICE - 1` | A building wider than this, in a homes cell, may merge blocks: 7, a block's depth. |
| 344 | `DistrictPlan.JOIN_ROUNDS` | `40` | The most pieces the join joins, one at a time: 40, the prototype's. |
| 347 | `DistrictPlan.JOIN_DX` | `{ 0, 0, 1, - 1 }, JOIN_DY = { 1, - 1, 0, 0 }` | The join's steps in the prototype's order: south, north, east, west - so of two ways as short it takes the prototype's. |
| 350 | `DistrictPlan.HALF` | `0.5` | A street's least surface, in plots of its right of way: a half, 15 m (spec 2.5). |
| 353 | `DistrictPlan.FULL` | `1.0` | ...and its full width, a whole plot, 30 m. |
| 358 | `DistrictPlan.FIXED_HIGHWAY` | `BuildingVisual.HIGHWAY` | A fixed plot: an Elevated Highway's (BuildingVisual.HIGHWAY). |
| 361 | `DistrictPlan.FIXED_RAIL` | `TilePainter.RAIL` | ...a railway's track (TilePainter.RAIL). |
| 364 | `DistrictPlan.FIXED_YARD` | `CityRuns.F_YARD` | ...a railway yard's ground, a Rail Terminal the city's runs drew on its track (0.7.89, CityRuns.F_YARD): no street crosses it, no building stands on it. |
| 367 | `DistrictPlan.FIXED_RESERVE` | `CityRuns.F_RESERVE` | ...an interchange's reserve (batch FR7, D19, CityRuns.F_RESERVE): a cloverleaf's 19 x 19 plots or a trumpet's 11 x 11, street ground - no lot, no building, no street across it but the highways and their slips, which t... |
| 373 | `DistrictPlan.SITE_FIELD` | `1` | A site with nothing on it: a field, built on last. |
| 376 | `DistrictPlan.SITE_MINED` | `2` | A site a mine or well stands on: its own. |
| 379 | `DistrictPlan.CLOSED` | `0` | A cell's layout: not opened. |
| 381 | `DistrictPlan.HOMES` | `1` | ...homes: long blocks (or square, the ladder's), for what follows people. |
| 383 | `DistrictPlan.ESTATE` | `2` | ...an estate: one spine, two strips, for industry and the outer kinds. |
| 388 | `DistrictPlan.NONE` | `0` | A plot's street, its low three bits: none. |
| 390 | `DistrictPlan.TRACK` | `1` | ...a track: a street the city has bought no road for (spec 2.5, R4). |
| 392 | `DistrictPlan.GRAVEL` | `2` | ...gravel. |
| 394 | `DistrictPlan.PAVED` | `3` | ...paved. |
| 396 | `DistrictPlan.HIGHWAY` | `4` | ...an Elevated Highway's plots given to the plan as a street's surface (Input.highway: CityMap's deal did so from 0.7.72, its plans to 0.7.88; since 0.7.89 the runs lay every one and the map gives none). |
| 398 | `DistrictPlan.SEAM` | `5` | ...a seam: a street on the district's edge whose surface is the neighbour's (SEAMS). |
| 400 | `DistrictPlan.UNDER` | `6` | ...a street passing beneath a highway (spec 2.7: elevated). |
| 402 | `DistrictPlan.KIND_MASK` | `7` | The kind's bits. |
| 404 | `DistrictPlan.WIDTH_SHIFT` | `3` | Its width, bits 3 and 4: 1 half, 2 full; 0 for none (a track, a seam, beneath a highway). |
| 406 | `DistrictPlan.ROLE_SHIFT` | `5` | Its role, bits 5 to 7. |
| 408 | `DistrictPlan.ROLE_STREET` | `1` | ...a cell's street. |
| 410 | `DistrictPlan.ROLE_ARTERIAL` | `2` | ...an arterial: a cell's ring. |
| 412 | `DistrictPlan.ROLE_BOULEVARD` | `3` | ...an arterial of a boulevard cell, either row (spec 2.5). |
| 414 | `DistrictPlan.ROLE_SHORE` | `4` | ...a street along a cut, joining a dead end to its neighbour (H2). |
| 416 | `DistrictPlan.ROLE_JOIN` | `5` | ...a street the join laid along the lattice. |
| 418 | `DistrictPlan.ROLE_ACCESS` | `6` | ...an access road (batch FR7, D17): the street from a sea terminal's, a yard's or a Truck Depot's gate to the nearest highway ramp or, failing one, the arterial grid - an arterial in all but its name, paved first. |
| 421 | `DistrictPlan.ACCESS_NEW` | `3` | The cost of laying a plot of access road where there is no street, against 1 for a plot that already is one: 3 - so a road follows the streets there are, and crosses open ground only where that is a good deal shorter. |
| 424 | `DistrictPlan.ACCESS_RAMP_DISTRICTS` | `2` | How far from the work it serves a highway ramp may lie, in districts, for its access road to go there and not to the arterial grid: 2 (spec-freight 4.5: "the nearest highway ramp if one lies within two districts"). |
| 427 | `DistrictPlan.SIDING_PLOT` | `TilePainter.SIDING_PLOT` | A plot of a siding the plan laid (batch FR7 slice 5, D14), by plot in DistrictPlan.siding... |
| 429 | `DistrictPlan.SIDING_END` | `TilePainter.SIDING_END` | ...and the first plot of a siding, beside the works' box, where its loading shed stands. |
| 432 | `DistrictPlan.DEFAULT_SIDING_PLOTS` | `72` | The plots a siding may have when its input does not say (Input.sidingPlots): 72, a Rail Spur's track in buildings.json. |
| 435 | `DistrictPlan.SIDING_CROSSING` | `3` | What a siding pays to cross a street at grade, over the 1 of a plot of open ground: 3 - a level crossing is a cost, so a siding takes the open ground beside a street where that is not much longer. |
| 438 | `DistrictPlan.SIDING_ALONG` | `8` | ...to run along a street plot after street plot, over a plot's own cost: 8 - a siding never takes a street's place, so it crosses and leaves it (the search also never turns on a street). |
| 441 | `DistrictPlan.SIDING_TURN` | `2` | ...and what a turn costs it, so a siding is straight or turns few times, not a staircase: 2. |
| 444 | `DistrictPlan.SIDING_EDGE` | `2` | The room a siding's plots keep from the frame's edge, in plots: 2, so no siding plot lies on the edge row or column the districts share or beside it, and the tile beside a district's never needs the siding of the dist... |
| 446 | `DistrictPlan.BRIDGE` | `1<<8` | A bridge over fresh water, bit 8. |
| 448 | `DistrictPlan.CROSSING` | `1<<9` | A level crossing of a railway, bit 9. |
| 633 | `DistrictPlan.ACCESS_RAMP` | `1, ACCESS_ARTERIAL = 2, ACCESS_HAD = 3` |  |
| 757 | `DistrictPlan.BUILDERS` | `new ThreadLocal<>()` | KEPT (0.7.94, batch RD7): each thread's builder, its arrays - about 4 MB - and the ladder's turns - about 0.4 MB each, one a step of the longest chain it has climbed - made once and used plan after plan, rather than m... |
| 965 | `DistrictPlan.Builder.WORDS` | `(FRAME + 63) / 64` | The streets row by row as bits, WORDS longs a row: what a cell's reach is read from. |
| 1039 | `DistrictPlan.Builder.WORN` | `1<<30` | The stamps' ceiling: a builder is replaced past it, half the int's range, far beyond what a plan stamps (a dense plan some tens of thousands). |
| 2823 | `DistrictPlan.Builder.FLIP` | `1<<31` | estateLines()'s flag: the cell's streets run the other way from its own direction (a cell the ground cuts). |
| 2839 | `DistrictPlan.Builder.FULL_LINES` | `new java.util.concurrent.ConcurrentHashMap<>()` | Each layout a full cell takes for a building's shape, its ring whole: {across, down, boulevard, the cell's direction} to estateLines()'s answer - the same whatever lies about it (no street beyond its ring is nearer it... |
| 2948 | `DistrictPlan.Builder.H_STREET` | `1, H_ARTERIAL = 2, V_STREET = 4, V_ARTERIAL = 8, LAY_REFUSED = 15, LAY_UNDER ...` | LAY CODES (0.7.94, batch RD7): what layPlot() makes of each plot, read from the input once a plan - the plot refused to a street running east-west (H_STREET), to an arterial so (H_ARTERIAL), and north-south (V_STREET,... |
| 3406 | `DistrictPlan.Builder.LAST_WORD` | `(1L<<(FRAME - 64 *(WORDS - 1))) - 1` | A row's last word's plots in the frame: bit 0 to FRAME - 1 - 64 x (WORDS - 1). |
| 3466 | `DistrictPlan.Builder.WINDOW` | `(1L<<(INTERIOR + 2 * REACH)) - 1` | A window's row as bits, one a plot: a cell's interior and the REACH plots either side of it. |
| 4021 | `DistrictPlan.Builder.SPLIT_NEAR` | `CELL` | splits()'s first walk keeps within this many plots of the box: a cell (NEAR FIRST). |
| 4298 | `DistrictPlan.Builder.SD_BUCKETS` | `16` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 467 | `public long seed` | The world's seed. |
| 469 | `public long x0, y0` | The frame's first plot in the world: the district's north-west plot. |
| 471 | `public double hubX, hubY` | The hub, in the frame's plots: the founding site (spec 2.4; on another landmass, later, its first port). |
| 473 | `public final byte[] terrain` | The ground, a World class a plot, row by row over the frame. |
| 475 | `public final boolean[] owned` | Which plots the city owns. |
| 477 | `public int[] boughtAt` | Per plot of the frame (AREA, row by row), the month the city bought it (0 for the centre's ground, and for any plot it does not own), or null when it bought none of the frame's ground (or for a harness's input): with ... |
| 479 | `public final byte[] fixed` | The city's highway and railway plots: FIXED_HIGHWAY, FIXED_RAIL or a yard's FIXED_YARD, and (batch FR7) an interchange's reserve FIXED_RESERVE, laid before the plan (the city's runs since 0.7.89, CityRuns; CityMap's n... |
| 481 | `public final byte[] site` | The resource sites: SITE_FIELD, or SITE_MINED where a mine or well stands. |
| 483 | `public final List<int[]> mines` | The mines and wells of the district standing on their sites, each {x0, y0, x1, y1} inclusive in the frame's plots: counted apart from the reach (PlanCheck), as MapCheck 8 counted them. |
| 485 | `public final List<int[]> works` | The works that need an access road (batch FR7, D17), each box {x, y, w, h} in the frame's plots: the railway's yards and the shore's sea terminals (the map's; they stand on FIXED_YARD ground). |
| 487 | `public boolean[] ramp` | A highway's plot of the frame that carries a ramp (CityRuns.M_RAMP), by plot; null for none: where a street beneath it makes a ramp an access road can end at. |
| 489 | `public int sidings` | How many sidings this district's share of the city's Rail Spurs lays (batch FR7 slice 5, D14; CityMap.sidingsOf()): the plan lays one to each of this many of the largest bulk works it placed, to the runs' railway in i... |
| 491 | `public int sidingPlots` | ...and the plots one may have: a Rail Spur's own track (CityMap: the model's spur track over its spurs, 72), as the runs' sidings are paid for out of the spurs' plots - a works whose siding would be longer is passed o... |
| 493 | `public BuildingVisual.Type[] types` | The types, by id (BuildingVisual.table()). |
| 495 | `public int[] counts` | The buildings to place, by type id: every drawn type the district holds but its mines and wells on sites (and, with a log, the plan's own: the log is squared with them, THE ORDER OF ARRIVAL). |
| 497 | `public double gravel, paved, highway` | The road plots its streets are made of, by kind: the model's Gravel and Paved Roads, and Elevated Highway plots to lay as a street's surface (none from the map since 0.7.89: the city's runs lay them, and count what th... |
| 499 | `public final boolean[] surfaces` | Whether this district surfaces its seams: north, east, south and west, then its corners north-west, north-east, south-east and south-west (SEAMS). |
| 501 | `public final boolean[] seamOpen` | A seam's plot this district does not surface, which the district that does leaves without a street (0.7.88, ONE-SIDED SEAMS): set by the map from the plans before this one in its order; false for all, every seam is la... |
| 503 | `public final boolean[] anchor` | A plot of the frame's edge a district before this one in the map's order lays a street on (0.7.88, ACROSS DISTRICTS), a street on the city's network: what this district's streets join to; none for the first district, ... |
| 505 | `public final boolean[] partedAnchor` | ...and one whose street is not on the network yet - the ground parted its piece from it in that district's frame, and no district before this one laying the plot has it on (0.7.88, PARTED): joined here, where this dis... |
| 507 | `public boolean root` | Whether this is the first district in the map's order: its largest piece is the network's (0.7.88). |
| 509 | `public boolean asPrototype` | The prototype's own rules only - no water rule, no streets along a cut, every seam surfaced: PlanCheck's replay of proto2.py. |
| 511 | `public boolean bandsApart` | The estate bands kept apart, as 0.7.87 to 0.7.91 drew them - no outer kind in industry's cells (SHARED ESTATES): PlanCheck's comparison. |
| 513 | `public boolean combOnly` | Every homes cell laid as 0.8.11 laid them all, the comb or the whole square (CL2, a homes cell's layout): PlanCheck's comparison. |
| 515 | `public Hashes hashes` | The hashes, or null for the world's. |
| 521 | `public int[] logMonth, logType, logChange` | THE LOG (batch CL3): the district's count changes in the order they came, a record each - its month, the type id and the change (+ come, - gone) - months never falling, every type the district counts (its roads among ... |
| 523 | `public int firstBatch` | ...how many of its first records are one batch, placed in today's order (the bands, largest first, the ladder): a canonical map's deal, or a sidecar's from before FORMAT 9 - so its first plan is today's, box for box. |
| 532 | `public int[] carry` | What the district before it in the map's order could not place (R7), as {month, code} pairs in the order they came - the month each came in, so the spill keeps its time; null for none. |
| 586 | `public final byte[] cellKind` | Each cell's layout (CLOSED, HOMES or ESTATE), by ci + cj x 8. |
| 588 | `public final boolean[] cellAcross` | ...whether its long blocks run east-west (else north-south). |
| 590 | `public final int[] cellRank` | ...its rank in the order opened, or -1. |
| 592 | `public final boolean[] cellSquare` | ...whether the ladder squared it (a homes cell's + grid at 8). |
| 594 | `public final byte[] cellLayout` | ...a homes cell's street layout (CL2: LAYOUT_TODAY, LAYOUT_DROPPED or LAYOUT_BRICK); LAYOUT_TODAY for every other cell. |
| 596 | `public final boolean[] cellBoulevard` | ...whether its arterials are a boulevard. |
| 598 | `public final boolean[] cellMerged` | ...whether blocks were merged in it for a building wider than one (homes), or its spine closed for one wider than a strip (an estate). |
| 600 | `public final int[] cellStreets` | ...an estate cell's streets (0.7.90, ESTATE LINES): bit o for a street o plots into its interior, running north-south where cellAcross, else east-west; 0 for a homes cell, or an estate cell whose spine was closed. |
| 603 | `public short[] street` | Each plot's street: its kind, width, role and flags (KIND_MASK, WIDTH_SHIFT, ROLE_SHIFT, BRIDGE, CROSSING); 0 for none. |
| 606 | `public int buildings` | The buildings placed: each one's box {x, y, w, h} in the frame's plots and its type id. |
| 607 | `public int[] bx` |  |
| 610 | `public int[] overflow` | What the plan could not place, by type id (R7: the map carries it to the next district with room since 0.7.88, and packs what none holds at the city's edge). |
| 624 | `public int[] overflowLog` | ...and what leaves this district for the next as {month, code} pairs (batch CL3): what the map carries on (Input.carry); null for none. |
| 632 | `public final List<int[]> accessRoads` | The access roads laid (batch FR7, D17), one for each work that had none: its box {x, y, w, h}, the gate (the road's first plot, beside the box), the road's end plot, and what it ends on - ACCESS_RAMP (a ramp's plot, a... |
| 636 | `public boolean[] accessAt` | The plots the access roads run over, by plot (a boulevard's among them, which keeps its own role); null when none was laid. |
| 645 | `public final List<int[]> sidings` | The sidings laid (batch FR7 slice 5, D14), one for each works that was given one: its box {x, y, w, h}, the siding's first plot (beside the box, where its shed stands), its last (beside the railway), the railway plot ... |
| 648 | `public byte[] siding` | The plots the sidings run over, by plot: SIDING_PLOT, SIDING_END at each shed's; null when none was laid. |
| 651 | `public int sidingsAsked, sidingsNoWorks, sidingsNoRail, sidingsAtDoor, sidingsNoGate, sidingsNoWay` | The sidings the plan was asked for and the bulk works that got none, by why (batch FR7 slice 5): the plan placed no bulk works to serve, there is no railway plot in its frame, the works has the railway at its door, no... |
| 654 | `public double surplus` | The surface left over when every step of the ladder is taken: road the plan has no street for (0 where the ladder holds it). |
| 657 | `public final double[] leftover` | ...by kind, [0, gravel, paved, highway] (BuildingVisual's): each kind's road less its surface, so surface and leftover make the input's road kind by kind (0.7.88; MapCheck sums it). |
| 660 | `public double budget` | The road its streets are made of: the input's, by kind, and in all. |
| 663 | `public int squares, boulevards, cellsOpen, homesCells` | How many cells the ladder squared and made boulevards of, as it asked (the prototype's figures; in a plan that goes on past its first batch, the first batch's as it asked and every cell opened after it by the road of ... |
| 666 | `public int joins, shoreJoins, strays, mergesRefused, onFields` | Pieces the join joined; dead ends joined along a cut; street plots taken up as strays; merges refused for splitting the network; buildings placed on a field. |
| 669 | `public int mergesUndone` | Merges in a homes cell of a varied layout made and then undone (CL2): the stubs they left could not all be taken up, or a building near them would have lost its street within REACH. |
| 672 | `public int seamsSurfaced` | One-sided seams this district surfaced from its road left over (0.7.88, ONE-SIDED SEAMS). |
| 675 | `public int joinsOut, partedOut` | Its pieces joined to the streets of the districts before it (0.7.88, ACROSS DISTRICTS), and those no way over its ground joins. |
| 678 | `public boolean[] parted` | Each street plot of the frame that is not on the city's network (0.7.88, PARTED): its piece - with the parted streets of the districts before it that it touches - touches no anchor, nor is the first district's largest. |
| 681 | `public int builds` | The plans drawn to climb the ladder (1 to a few). |
| 684 | `public int mixedSlots` | The slots the first band's types are dealt round (MIXED_SLOT): one more building moves only those placed after it while they are as many (MapCheck 2). |
| 850 | `int g, i, kNext, opened, homes, placed, nMine, nShared` |  |
| 851 | `boolean exhausted` |  |
| 852 | `int[] mine, px, py, pw, ph, pt, fails, failsF, mergeFails, cellRank` |  |
| 853 | `byte[] occ` |  |
| 854 | `boolean[] str, art, under, blvd, reachStale, cellCut, holdsNone, cellAcross, cellSquare, cellBoulevard, cel...` |  |
| 855 | `byte[] role, cellKind, cellLayout` |  |
| 856 | `int[] cellStreets` |  |
| 857 | `long[] rowBits` |  |
| 858 | `int[][] free, freeF, near, touch, failW, failH, failFW, failFH, mergeFailW, mergeFailH` |  |
| 859 | `final List<int[]> unplacedAt` |  |
| 860 | `int shoreJoins, strays, mergesRefused, mergesUndone, onFields` |  |
| 908 | `Input in` | The plan's input, its rules and hashes: set by start() for each plan (KEPT). |
| 909 | `boolean proto` |  |
| 910 | `Hashes hash` |  |
| 912 | `boolean busy` | Whether a plan is being made on it: a make() within a make() is given a builder of its own. |
| 914 | `final boolean[] owns` | The plots the plan has as ground so far: Input.owned, less what the city bought after the month the plan stands on (THE GROUND BOUGHT, FX6), which growGround() adds as the events reach its month; every read of the cit... |
| 916 | `final boolean[] dry` | Dry owned ground: grass, forest or sand. |
| 918 | `final boolean[] blocked` | Plots no building may take: not dry, a highway, its verge (H5), track, a mine's site; and a field, until the last pass. |
| 920 | `final boolean[] fieldOk` | A field's plots a building may take once nothing else holds it: dry, off the highways, their verges and the track. |
| 922 | `final boolean[] cellField` | ...and the cells with any in their interior. |
| 924 | `final short[] hRun` | Fresh water's run along its row and its column, at each fresh plot. |
| 926 | `final int[][] openRows` | Each cell's interior plots no building is barred from (not `blocked`), a bit a plot by row as free[] holds them: a cell's free ground until it opens (0.7.90, ESTATE LINES). |
| 928 | `final int[][] fieldRows` | ...and its field plots a building may take once nothing else holds it (fieldOk), likewise (0.7.94, BITS). |
| 930 | `int[] order` | The cells in the order they open (spec 2.4), each ci + cj x 8. |
| 932 | `final int[][] bandType` | Each band's buildings in the order placed: type id, across, down. |
| 934 | `int mixedSlots` | The first band's slots (MIXED_SLOT). |
| 938 | `final byte[] occ` | Each plot: -1 no building may stand there (blocked, or a street), 0 free, 1 a building's (since 0.7.94 a byte: no reader asks which building). |
| 939 | `final boolean[] str` |  |
| 940 | `final byte[] role` |  |
| 941 | `final int[][] free` |  |
| 943 | `final int[][] freeF` | Each cell's free rows with its fields' plots free too: where a building with no other place may stand (spec 2.6). |
| 944 | `final boolean[] reachStale` |  |
| 946 | `final int[][] failW` | Shapes a cell is known to hold no box of, {across, down} pairs (a larger one holds none either), until its streets change. |
| 947 | `final int[] fails` |  |
| 949 | `final int[][] failFW` | ...and those it holds none of on its fields too. |
| 950 | `final int[] failsF` |  |
| 952 | `final int[][] mergeFailW` | ...and shapes no group of its blocks merges for: for good (a building in a group stays, the ground does not dry). |
| 953 | `final int[] mergeFails` |  |
| 954 | `final int[] changed` |  |
| 955 | `int nChanged` |  |
| 957 | `final boolean[] stub` | A dead end's stub, while the cut's join looks for its neighbour. |
| 959 | `final boolean[] cellCut` | Whether each cell's layout was laid whole, or the ground cut it (a plot refused): only a cut cell's merge can part the network. |
| 960 | `boolean refused` |  |
| 962 | `final int[] markA` | Marks for walks, by stamp, so none needs clearing; and a queue. |
| 963 | `int stamp` |  |
| 966 | `final long[] rowBits` |  |
| 968 | `final int[] runF` | tryPlace()'s free runs, worked out a row at a time as asked. |
| 969 | `int tryStamp` |  |
| 981 | `int[] shapeSlot, scanFrom, scanNFrom, scanEpochAt` | SCAN (0.7.89): for each cell, with its fields or not, and each shape the bands place (shapeSlot, nShapes), the first row tryPlace() need look from for a box touching a street - the row it found one at, past the last r... |
| 982 | `int nShapes` |  |
| 983 | `final int[] scanEpoch` |  |
| 984 | `int scanClock` |  |
| 992 | `int[] shapeW, shapeH` | Each shape's across and down, by shapeSlot. |
| 1000 | `boolean[] holdsNone` | The fails memo (failW, failFW) as a table (0.7.89): whether cell c, with its fields or not, is known to hold no box of each shape - a shape no smaller than one it held none of, either way round, as the lists say - rea... |
| 1018 | `int[] skipFrom` | SKIP (0.7.94): for each shape (shapeSlot), how far into its band's cells the table (holdsNone, without fields) says no cell holds a box of it - every cell before is known to hold none - so a building looks from there. |
| 1020 | `final int[][][] anyT` | Each cell's touch and reach rows over a box's width, by width, kept until its reach moves: worked out again when its epoch (anyAt) is not the cell's (anyEpoch; 0.7.89, the arrays kept from build to build). |
| 1021 | `final int[][] anyAt` |  |
| 1022 | `final int[] anyEpoch` |  |
| 1024 | `final long[][] reachRows` | reachCell()'s rows: the streets, two to step between and those one plot from one (0.7.89: kept, not made at each call). |
| 1026 | `int[] shoreFrom, shoreQueue` | shore()'s walk over a cell's ring box: where each plot was reached from, and the queue (kept, as reachRows). |
| 1144 | `boolean logged, goesOn` | Whether the plan reads a log, and whether it goes on past its first batch: events to place, or road come after it. |
| 1146 | `boolean ev` | Whether the events are being placed (the far side, the homes' rule and the ladder decided once apply). |
| 1148 | `int batchBuildings` | The buildings of the first batch. |
| 1150 | `int[] bcount` | The first batch's counts by type, the carry that joins it included: what bands() sorts - the input's own counts for a plan with neither a log nor a carry. |
| 1152 | `boolean[] evType` | The types the events place (for SCAN's shapes, which bands() makes). |
| 1154 | `double rG, rP, rH` | The road the surface spends, by kind: the input's, or the first batch's while the ladder climbs on it. |
| 1156 | `int[] fpW, fpH` | Each type's footprint, bands()'. |
| 1158 | `int nInst` | The instances, by id in the order made: type, month, band and index in it, and where the placed box is in the placed arrays (-1 unplaced, -2 gone). |
| 1159 | `int[] iType, iMonth, iG, iI, iAt` |  |
| 1161 | `boolean[] iCarried, iBatch, iGone, iExcl` | ...whether carried here, in the first batch, gone within it, or the plan's newest beyond its counts (never placed). |
| 1163 | `int nEv` | The events: the instance, whether it leaves (else it comes), the road at its month and the month, in all. |
| 1164 | `int[] evInst, evMonth` |  |
| 1165 | `boolean[] evGone` |  |
| 1166 | `double[] evRoad` |  |
| 1171 | `int[] ofl, iOver` | The overflow log being built (OVERFLOW LOG below): its {month, code} pairs, nOfl of them, or null when it is made from the first batch alone; and each instance's pair in it (-1 for none), while the events are placed. |
| 1172 | `int nOfl` |  |
| 1174 | `int[][] live` | Each type's live instances of its own, the newest last; and readLog()'s tally of a month's arrivals by type and the types it has. |
| 1175 | `int[] nLive, want, dealTypes` |  |
| 1177 | `int[][] bandInst` | Each band's index to its instance. |
| 1179 | `int[][] batchAt` | Where each first-batch building went in the placed arrays, while place() makes it for the events (-1 for none). |
| 1181 | `final int[][] keepMine` | Each band's cells as place() left them (bMine, bNMine, bNShared after its last building). |
| 1182 | `final int[] keepN` |  |
| 1184 | `int bFar` | The order's last cell the far pointer has not taken: the near pointer (bNext) stops where they meet. |
| 1186 | `double evRoadNow` | The road at the event being placed, all kinds. |
| 1188 | `final boolean[] mineAt` | Each cell that holds a mine or a site, or a site-bound building the plan placed (homesCell()). |
| 1190 | `boolean[] placedGone` | Which placed boxes have left (the events); null when none can. |
| 1432 | `double batchG, batchP, batchH` | The first batch's road, when the plan goes on past it. |
| 1726 | `int dueMonth` | The smallest Input.boughtAt among the owned plots the plan does not have as ground yet (Integer.MAX_VALUE for none): growGround() has nothing to add for a month no later than it. |
| 1728 | `int[] grown` | growGround()'s plots added, kept between calls. |
| 2174 | `DistrictPlan bOut` | A BUILD'S RUNNING STATE (0.7.94, batch RD7): what build() kept in its locals until 0.7.93 - the plan so far, the ladder's step, the buildings placed, the next cell in the order, the cells opened and homes cells, wheth... |
| 2175 | `int bSquares, bBoulevards, bLimit, bPlaced, bNext, bOpened, bHomes, bNMine, bNShared` |  |
| 2176 | `boolean bExhausted, bShare, bByHomes` |  |
| 2177 | `int[] bMine, bTurnAt` |  |
| 2178 | `Turn[] bTurns` |  |
| 2179 | `List<int[]> bUnplaced` |  |
| 2329 | `int campusGroup` | campusStands()' reading: the group read, how far, whether one stood, and the undoing it was read at. |
| 2330 | `boolean campusAny` |  |
| 2345 | `final boolean[] campusCells` | The cells the refinery's units stand in (campusStands()), kept from call to call. |
| 2456 | `Turn[] turnPool` | The builder's turns (KEPT): the next one free, made the first time. |
| 2457 | `int turnsUsed` |  |
| 2842 | `final java.util.HashMap<LinesKey, Integer> cutLines` | ...and each answer for a cell that is not full, by everything it is worked out from - the cell, the building's shape, its direction and boulevard, its free ground and the streets in its window (LinesKey) - for the lad... |
| 2846 | `final long[] v` |  |
| 2847 | `final int hash` |  |
| 2854 | `final int[] linesFree` | estateLines()'s working rows: the cell's free ground, the window's streets, its touch and reach. |
| 2855 | `final long[] simS` |  |
| 2951 | `final byte[] layCode` | Each plot's lay code (LAY CODES). |
| 3238 | `int[] stubAt` | shore()'s stub, its plots (stubAt, nStub), and its walk's stamp a plot (shoreSeen against shoreStamp). |
| 3239 | `int nStub, shoreStamp` |  |
| 3402 | `final long[] openBits` | start()'s rows of the plots no building is barred from and of the fields, and each column's fresh run's first row. |
| 3403 | `final int[] runFrom` |  |
| 3409 | `final long[] vergeRows` | The verges' bits a row (VERGES), and the highways' rows spread along (vergeWideRow()). |
| 3412 | `int[] fixedList` | The plots of the city's runs (Input.fixed not 0), in plot order: their lay codes made apart (fixedCodes()). |
| 3413 | `int nFixed` |  |
| 3432 | `final byte[] occStart` | occ as a build begins: -1 where blocked, else 0 (start()). |
| 3434 | `final long[] hwRows` | The highways' plots, a bit each by row as rowBits (start()), and in plot order (hwList, nHighway); the parted streets of the districts before it likewise (paRows); whether a plot of the edge is an anchor. |
| 3435 | `int[] hwList` |  |
| 3436 | `int nHighway` |  |
| 3437 | `boolean anyAnchor` |  |
| 3440 | `final int[] laidRows` | freshCell()'s rows of the plots laid. |
| 3764 | `int[] undoAt` | The plots a tentative merge has taken up, in the order taken (the group's streets, row by row, then its stubs'), and each as it was (stateOf()), to be put back. |
| 3765 | `int nUndo` |  |
| 3804 | `int[] cellBuilding` | Each cell's placed buildings as a list - cellBuilding[c] the newest, nextBuilding[b] the one placed before b in its cell, -1 for the end - built on as buildings are placed and read like campusStands()' cells: from the... |
| 3805 | `int buildingsSeen, buildingsGen` |  |
| 3830 | `int[] mergeStub` | The dead ends a merge would leave (stubsFree()) and take up, by plot, in the order taken: the first nMergeStub. |
| 3831 | `int nMergeStub` |  |
| 4024 | `int[] splitEdge` | splits()'s streets about the box. |
| 4080 | `int[] placedX, placedY, placedW, placedH, placedT` | A build's buildings placed so far, each one's box and type: kept from build to build (0.7.89), the plan's own copies made of them. |
| 4083 | `final int[] targetMark` | reachFrom()'s targets: each plot's mark (its call's stamp) and how many times it is a target. |
| 4084 | `int targetStamp` |  |
| 4087 | `final int[] scratchLab` | The join's, the parting's and the surface's working arrays, a plot each, kept from build to build (0.7.90): each written before it is read (pieces() and markParted() set a label of -1 on each plot they label - the onl... |
| 4088 | `final boolean[] scratchParted` |  |
| 4124 | `int[] accDist, accPrev, accSeen` | access()'s search: each plot's distance from the work and the plot it was reached from, stamped (accSeen) so none is cleared; the queue by distance modulo 4 (an edge costs 1 to ACCESS_NEW, so four buckets are a Dial's... |
| 4125 | `int accStamp` |  |
| 4126 | `int[][] accQ` |  |
| 4127 | `int[] accN` |  |
| 4294 | `int[] sdDist, sdPrev, sdSeen` | sidings()'s search: the cost to each state (a plot and the heading it was entered going, plot x 4 + heading) and the state it was reached from, stamped (sdSeen) so none is cleared; the queue by cost modulo SD_BUCKETS ... |
| 4295 | `int sdStamp` |  |
| 4296 | `int[][] sdQ` |  |
| 4297 | `int[] sdN` |  |
| 4300 | `byte[] sdLaid` | The siding being laid, a byte a plot as DistrictPlan.siding. |
| 4302 | `int sdLast, sdState, sdRail` | A search's end: the last plot of the way before the railway, the state it was entered in, and the railway plot it meets. |
| 4543 | `final int[] ordered` | streetsInOrder()'s plots and their count; and each piece's first plot (pieces()) and its place among them. |
| 4544 | `int nOrdered` |  |
| 4545 | `int[] pieceRep` |  |
| 4548 | `final int[] joinSeen` | The join's walks' marks, by stamp (0.7.94: no clearing of the way back before each): a plot reached when joinSeen holds the walk's. |
| 4549 | `int joinStamp` |  |
| 4598 | `int joinTail` | The join's walk's queue (queue[]) end: its steps add to it. |
| 4787 | `int partedOutCount` |  |
| 4790 | `int[] partedAt` | The join's parted pieces' first plots (scratchParted), listed so they are cleared one by one rather than the frame (0.7.94). |
| 4791 | `int nPartedAt` |  |
| 4835 | `int[] partSize` | markParted()'s pieces: each one's plots with a street code, and whether it touches an anchor. |
| 4836 | `boolean[] partOn` |  |
| 4952 | `int sN, sOpen, sAll` | surface()'s counts: the streets it surfaces, its one-sided seams, every street. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 227 | 4767 | **type** `public final class DistrictPlan` | One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it, the cells it opens and the layout of each, every street with its kin... |

### THE GEOMETRY (lines 229-265)

### a homes cell's layout (CL2) (lines 266-354)

### the inputs' codes (lines 355-384)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 1 | `static boolean works(byte f)` | Whether a fixed code is a works' own ground (a yard's, an interchange's reserve): no building and no street on it. |

### a street plot's code (lines 385-449)

### THE INPUTS (lines 450-580)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 8 | **type** `public interface Hashes` | The hashes a plan is drawn with: the world's, or (PlanCheck's replay) the prototype's own. |
| 457 | 1 | `double cell(int ci, int cj)` _(in DistrictPlan.Hashes)_ | A cell's nudge on its distance from the hub. |
| 459 | 1 | `double dir(int ci, int cj)` _(in DistrictPlan.Hashes)_ | A cell's direction: under a half its long blocks run east-west. |
| 461 | 1 | `double deal(int type)` _(in DistrictPlan.Hashes)_ | A type's start in the first band's deal. |
| 465 | 69 | **type** `public static final class Input` | Everything a plan is drawn from: filled by CityMap.planInput(), or by hand in a harness. |
| 536 | 8 | `static Hashes worldHashes(long seed, long x0, long y0)` | The world's hashes for a district: its cells' by their place in the world, its types' by the district. |
| 546 | 3 | `static double unit(long h)` | A hash as a number in [0, 1). |
| 551 | 3 | `static double layoutHash(long seed, long cellX, long cellY, int salt)` | A hash in [0, 1) of the world's seed, a homes cell's place in the world (its north-west plot, cellX and cellY) and a salt: a pure function of the plan's inputs (CL2, a homes cell's layout). |
| 556 | 4 | `static byte layoutOf(long seed, long cellX, long cellY, boolean square)` | A homes cell's street layout (LAYOUT_TODAY, LAYOUT_DROPPED or LAYOUT_BRICK) from the world's seed and the cell's north-west plot in the world, by the shares TODAY_SHARE and DROPPED_SHARE: the same layout for the same ... |
| 570 | 10 | `static boolean along(byte[] fixed, int p, boolean horizontal)` | Whether a highway or railway plot p runs along a street through it going east-west (horizontal) or north-south, rather than across it. |

### THE PLAN (lines 581-901)

| line | len | member | says |
|---:|---:|---|---|
| 686 | 1 | `private DistrictPlan()` |  |
| 689 | 3 | `public boolean isStreet(int p)` | Whether frame plot p is a street of any kind (beneath a highway included). |
| 694 | 3 | `public int kindAt(int p)` | A plot's street kind (NONE to UNDER). |
| 699 | 4 | `public double widthAt(int p)` | A plot's surface in plots of its right of way: 0, HALF or FULL. |
| 705 | 3 | `public int roleAt(int p)` | A plot's role (ROLE_STREET to ROLE_JOIN), 0 for none. |
| 710 | 5 | `public int overflowCount()` | The buildings it could not place. |
| 728 | 15 | `public static DistrictPlan make(Input in)` | The plan of a district (spec 2.9): the first plan with long blocks; the surface it leaves over squares the homes cells nearest the hub and then makes boulevards of the arterials nearest it - the prototype's plan_ladde... |
| 765 | 14 | `private static DistrictPlan ladder(Builder b)` | The ladder (make()) on builder b, started on its input. |
| 781 | 41 | `private static DistrictPlan ladderOn(Builder b)` | The ladder proper: the plan at the first step, then the steps its surplus pays for. |
| 824 | 3 | `static void forgetFullLines()` | The layouts full estate cells were found to take, forgotten (ESTATE LINES' FULL_LINES): for PlanCheck, which shows a plan is the same worked out afresh. |
| 829 | 6 | `static int[] chain(int k, boolean squares)` | The steps after step k of a chain of the ladder, by what each halves: the boulevards (3 to 1, as make() halves them) or, with none, the squares. |
| 849 | 13 | **type** `static final class Turn` | A build's state where a later step of the ladder turns from it (THE LADDER'S TURNS, 0.7.89): every array and count a build changes, as they stood just before it opened a cell - the cell a boulevard's step after it wou... |
| 864 | 5 | `static int[] copyInto(int[] src, int n, int[] dst)` | src's first n into dst, a new array where dst is null or shorter (KEPT): dst, or the array made. |
| 870 | 5 | `static byte[] copyInto(byte[] src, byte[] dst)` |  |
| 876 | 5 | `static boolean[] copyInto(boolean[] src, boolean[] dst)` |  |
| 882 | 5 | `static long[] copyInto(long[] src, long[] dst)` |  |
| 889 | 5 | `static int[][] copyRows(int[][] rows, int[][] dst)` | Each row of rows into dst's (rows all made: a cell's free, reach or touch rows). |
| 896 | 5 | `static int[][] copyLists(int[][] lists, int[] counts, int[][] dst)` | Each cell's list of shapes, its first counts[c] of them, into dst's (a list with none left as it is: a build makes it afresh before it adds one). |

### THE BUILDER: ONE PLAN AT A LADDER'S STEP (lines 902-4993)

| line | len | member | says |
|---:|---:|---|---|
| 906 | 4087 | **type** `private static final class Builder` |  |
| 987 | 3 | `void rescan(int c)` _(in DistrictPlan.Builder)_ | Cell c's rows to be scanned from the top again (SCAN): its room grew, or its reach moved. |
| 1003 | 7 | `void forgetFails(int c)` _(in DistrictPlan.Builder)_ | What cell c was known not to hold, forgotten: its reach grew or its streets were taken up. |
| 1029 | 1 | `Builder()` _(in DistrictPlan.Builder)_ | A builder, its arrays made: start() sets it on a plan's input. |
| 1032 | 5 | `boolean worn()` _(in DistrictPlan.Builder)_ | Whether a count it stamps with has grown so far it could come round (KEPT): replaced by a new builder before the next plan. |
| 1042 | 22 | `void start(Input in)` _(in DistrictPlan.Builder)_ | The plan of input `in` begun on this builder (KEPT): every array its plan reads of the ground filled afresh - what the constructor did until 0.7.93. |
| 1074 | 25 | `void ground()` _(in DistrictPlan.Builder)_ | THE GROUND, A ROW AT A TIME (0.7.94, batch RD7): dry, barred, the highways' and the parted streets' plots as bits a row, and the plots of the city's runs; then H5's verges from the highways' bits (VERGES), the fresh w... |
| 1101 | 18 | `void finish()` _(in DistrictPlan.Builder)_ | The plan made: what it held of its input let go (KEPT). |
| 1193 | 5 | `void spendInput()` _(in DistrictPlan.Builder)_ | The road spent is the input's. |
| 1200 | 3 | `static double dealHash(long seed, long x0, long y0, int month)` _(in DistrictPlan.Builder)_ | A hash in [0, 1) of the world's seed, the district's place and the month, for the start of a month's deal (the same for the same inputs on any machine). |
| 1205 | 18 | `int newInst(int t, int month, boolean carried, boolean batch)` _(in DistrictPlan.Builder)_ | A new instance (the first batch's when `batch`); its id. |
| 1225 | 5 | `void push(int t, int id)` _(in DistrictPlan.Builder)_ | Instance id of an own building of type t standing now, on the type's stack. |
| 1232 | 3 | `int pop(int t)` _(in DistrictPlan.Builder)_ | The newest live instance of type t, taken off its stack; -1 for none. |
| 1237 | 14 | `void addEvent(int id, boolean gone, double road, int month)` _(in DistrictPlan.Builder)_ | An event: instance id coming or (gone) leaving, at `month`, whose road is `road`. |
| 1253 | 4 | `double roadTotal(long[] road)` _(in DistrictPlan.Builder)_ | The road of a month, all kinds: the log's gravel and paved, and its highway to the input's own. |
| 1259 | 3 | `boolean placeable(int t)` _(in DistrictPlan.Builder)_ | Whether type t is one the plan places: a drawn type of the table (not a road, a railway line or a building at sea). |
| 1268 | 17 | `void deal(int month, int k, double road)` _(in DistrictPlan.Builder)_ | The month's arrivals dealt (the tally in want[] of the types in dealTypes[0 .. |
| 1291 | 139 | `void readLog()` _(in DistrictPlan.Builder)_ | The log read (batch CL3; see the section's note): the instances and events, the first batch's counts (bcount) and road, whether the plan goes on past the first batch (goesOn). |
| 1435 | 17 | `void mapBatch()` _(in DistrictPlan.Builder)_ | Each band's index to the instance it is, for the first batch: the type's first-batch instances in the order made, taken in turn by the band's buildings of the type (bands() makes them in an order of its own). |
| 1466 | 18 | `int[] firstBatchLog(boolean mark)` _(in DistrictPlan.Builder)_ | The buildings left out of the first batch as {month, type} pairs, by month and then in the order they were left out: each its instance's month (the first batch's record's, a carried one's pair's), 0 for a plan with no... |
| 1486 | 7 | `void beginOverflow()` _(in DistrictPlan.Builder)_ | The overflow log begun from the first batch's, for the events to add to (buildArrivals()). |
| 1495 | 6 | `int appendOverflow(int month, int code)` _(in DistrictPlan.Builder)_ | A pair appended to the overflow log; its place in the list. |
| 1503 | 4 | `int[] overflowLog()` _(in DistrictPlan.Builder)_ | What the district sends on as {month, code} pairs (DistrictPlan.overflowLog); null for none. |
| 1509 | 15 | `int streetPlotsLaid()` _(in DistrictPlan.Builder)_ | The street plots laid so far that this district surfaces (the seams it does not are the other district's). |
| 1526 | 7 | `boolean nearMine(int c)` _(in DistrictPlan.Builder)_ | Whether cell c, or any of the eight about it, holds a mine, a site or a site-bound building the plan placed. |
| 1535 | 9 | `int homesCell(DistrictPlan out)` _(in DistrictPlan.Builder)_ | The cell a homes cell opens in (HOMES KEEP OFF MINES): the first closed cell from bNext in the order that is not near a mine, and with none, the first closed one - taken off the order; -1 when no closed cell is left. |
| 1546 | 3 | `static int cellOfPlot(int x, int y)` _(in DistrictPlan.Builder)_ | Cell of plot (x, y) of the frame, or -1 off the cells' interiors and rings (the column and row 0). |
| 1551 | 14 | `void markMines()` _(in DistrictPlan.Builder)_ | The cells that hold a mine or a site, from the input and the first batch's site-bound buildings (mineAt). |
| 1567 | 10 | `void vacate(int k)` _(in DistrictPlan.Builder)_ | Placed box k taken away: its plots free again, its cell's free rows given back and what it was known not to hold forgotten. |
| 1579 | 15 | `void leave(int id, int month)` _(in DistrictPlan.Builder)_ | Instance id leaving at `month`: its box freed, or it taken off the list of those without a place - and, if the overflow log has a pair for it, the departure appended there (the next district's carry), the pair itself ... |
| 1601 | 111 | `DistrictPlan buildArrivals(int nSquare, int nBlvd)` _(in DistrictPlan.Builder)_ | The plan with its events placed (batch CL3): the first batch at the ladder's step (nSquare, nBlvd), built again but not finished, then each event in its order - a building at the first place it fits in its band's cell... |
| 1731 | 27 | `void groundAtStart()` _(in DistrictPlan.Builder)_ | owns filled for a new plan (start()): the input's owned ground, less the plots bought after the month the first batch stands on, which the events add; dueMonth set. |
| 1764 | 60 | `void growGround(int month)` _(in DistrictPlan.Builder)_ | The plan's ground grown to the month an arrival comes in (Integer.MAX_VALUE: to all of Input.owned): the plots bought before it added, the ground worked out again, the free rows of the open cells given the new plots, ... |
| 1827 | 48 | `int[] growthOrder()` _(in DistrictPlan.Builder)_ |  |
| 1877 | 5 | `static boolean nearer(double[] dist, int a, int b)` _(in DistrictPlan.Builder)_ | Whether cell a comes before cell b in the growth order: nearer the hub (with its nudge), then the west column, then the north row. |
| 1885 | 4 | `static int bandOf(BuildingVisual.Type t)` _(in DistrictPlan.Builder)_ |  |
| 1912 | 7 | `static int campusFirst(BuildingVisual.Type[] types, int a, int b)` _(in DistrictPlan.Builder)_ | The order of two types in a band: a refinery unit before anything else, then (0.8.0) each works group in turn, else none (0). |
| 1920 | 84 | `void bands()` _(in DistrictPlan.Builder)_ |  |
| 2014 | 4 | `DistrictPlan build(int nSquare, int nBlvd, int limit, Turn from, int[] turnAt, boolean byHomes, Turn[] turns)` _(in DistrictPlan.Builder)_ | A plan at a ladder's step: nSquare homes cells squared, nBlvd boulevards; null as soon as more than `limit` buildings have no place. |
| 2024 | 93 | `DistrictPlan place(int nSquare, int nBlvd, int limit, Turn from, int[] turnAt, boolean byHomes, Turn[] turns)` _(in DistrictPlan.Builder)_ | build()'s placing (CL3: split from its finish, complete(), so the order of arrival can place its events on the state it leaves): the plan so far with every building of the first batch placed, or null as soon as more t... |
| 2123 | 30 | `DistrictPlan complete(DistrictPlan out, int nSquare, int nBlvd)` _(in DistrictPlan.Builder)_ | build()'s finish (CL3: split from its placing, place()): the plan returned with the buildings placed (the first bPlaced of the placed arrays), those left out counted by type and as {month, type} pairs, the streets joi... |
| 2155 | 8 | `DistrictPlan let(DistrictPlan p)` _(in DistrictPlan.Builder)_ | A build's plan returned, what the build held let go (its turns, its plan so far). |
| 2182 | 111 | `boolean placeOne(int g, int i, boolean looked)` _(in DistrictPlan.Builder)_ | Building i of band g placed (`looked`: gone on with from a turn, its look over its band's cells made before it): whether the build is to stop, more than bLimit left out. |
| 2302 | 5 | `int campusNext(DistrictPlan out, int group)` _(in DistrictPlan.Builder)_ | The campus's next cell (THE CAMPUS): the index in the order, from the next to open on, of the first closed cell touching (eight ways: a side or a corner) a cell a refinery unit already stands in - since 0.8.0 a unit o... |
| 2309 | 18 | `boolean campusStands(int group)` _(in DistrictPlan.Builder)_ | Whether a unit of a group stands yet (the refinery's, or since 0.8.0 a works group's), the cells they stand in marked in campusCells - read on from the last look while the same group's and nothing placed was undone (c... |
| 2333 | 10 | `boolean nearCampus(int c)` _(in DistrictPlan.Builder)_ | Whether cell c is one of campusCells or touches one, eight ways (a side or a corner). |
| 2348 | 57 | `Turn turn(DistrictPlan out, int g, int i, int kNext, int opened, int homes, int placed, boolean exhausted, int[] mine, int nMin...` _(in DistrictPlan.Builder)_ | This build's state as it stands, at building i of band g with the cell order[kNext] about to open (THE LADDER'S TURNS). |
| 2407 | 47 | `void resume(Turn t, DistrictPlan out)` _(in DistrictPlan.Builder)_ | This build put back as turn t stood, out its plan so far; what is stamped (walks, SCAN, the touch rows by width) worked out afresh. |
| 2459 | 7 | `Turn nextTurn()` _(in DistrictPlan.Builder)_ |  |
| 2468 | 3 | `void freeTurns()` _(in DistrictPlan.Builder)_ | Every turn free again: a new chain of the ladder begins (the last chain's are done with). |
| 2473 | 5 | `void setStr(int p, boolean on)` _(in DistrictPlan.Builder)_ | A street laid on plot p, or taken up: the plot and its bit. |
| 2480 | 10 | `long window(int y, int wx, int n)` _(in DistrictPlan.Builder)_ | The street bits of row y from plot wx, n of them (n < 64), bit b for plot wx + b; none past the frame. |
| 2492 | 9 | `void put(int c, int x, int y, int w, int h)` _(in DistrictPlan.Builder)_ | A building on box (x, y, w, h) of cell c: its plots taken, the cell's free rows with them. |
| 2505 | 77 | `void openCell(DistrictPlan out, int c, byte kind, int rank, boolean square, boolean bl, int w, int h)` _(in DistrictPlan.Builder)_ | Cell c opened as `kind` at its rank, squared or a boulevard as the ladder's step asks: its ring and its streets laid - a homes cell's by the layout its place hashes to (a homes cell's layout, CL2), an estate cell's si... |
| 2600 | 6 | `boolean crossed(int x0, int y0)` _(in DistrictPlan.Builder)_ | Whether a highway, railway, yard or reserve (in.fixed) has a plot in the cell whose interior starts at (x0, y0), its ring included: such a cell keeps the comb, which crosses them as it always has (CL2). |
| 2608 | 3 | `double cellHash(long cellX, long cellY, int salt)` _(in DistrictPlan.Builder)_ | A hash for the homes cell whose north-west plot in the world is (cellX, cellY): layoutHash on this plan's seed. |
| 2613 | 7 | `void layStretch(int x0, int y0, boolean across, boolean alongLong, int line, int from, int to, byte st)` _(in DistrictPlan.Builder)_ | Street plots from..to of the cell's line `line`, along its long streets (alongLong) or across them, in the cell's own axes, laid in the map's. |
| 2622 | 6 | `static void mark(long[] g, boolean alongLong, int line, int from, int to)` _(in DistrictPlan.Builder)_ | The same plots marked in g, a bit row a line of the cell and its ring (bit u + 1 of row v + 1). |
| 2630 | 9 | `static long[] ringMarks()` _(in DistrictPlan.Builder)_ | A cell's marks with its ring on every side. |
| 2641 | 17 | `static int uncovered(long[] g)` _(in DistrictPlan.Builder)_ | How many plots of the cell's interior lie more than REACH from every marked plot, across corners. |
| 2667 | 55 | `void homesLayout(int x0, int y0, boolean across, byte layout, long cellX, long cellY, byte st)` _(in DistrictPlan.Builder)_ | The streets of a long-block homes cell of the layout LAYOUT_DROPPED or LAYOUT_BRICK, in place of the comb. |
| 2728 | 45 | `static long settle(long e)` _(in DistrictPlan.Builder)_ | The 24 pieces of a squared cell's three long streets and three cross streets (bit 4j + k the kth of the jth long street, bit 12 + 4i + k of the ith cross street) left once every junction with one street is taken out, ... |
| 2775 | 3 | `static int node(long e, int i, int j)` _(in DistrictPlan.Builder)_ | The streets that meet junction (i, j) of a squared cell, of the pieces e. |
| 2787 | 32 | `void squareLayout(int x0, int y0, boolean across, byte layout, long cellX, long cellY, byte st)` _(in DistrictPlan.Builder)_ | The streets of a squared cell (a boulevard's interior too) of the layout LAYOUT_DROPPED or LAYOUT_BRICK, in place of the whole square. |
| 2832 | 5 | `static int sizedLines(int depth)` _(in DistrictPlan.Builder)_ | The lines strips sized to two rows of a building `depth` plots deep back to back would put streets on (ESTATE LINES): a strip of max(2 x depth, STRIP_LEAST) plots from the arterial, a street, the next, while a street'... |
| 2845 | 7 | **type** `static final class LinesKey` _(in DistrictPlan.Builder)_ | What estateLines() reads of a cell that is not full, as a key. |
| 2848 | 1 | `LinesKey(long[] v)` _(in DistrictPlan.Builder.LinesKey)_ |  |
| 2849 | 1 | `public int hashCode()` _(in DistrictPlan.Builder.LinesKey)_ |  |
| 2850 | 1 | `public boolean equals(Object o)` _(in DistrictPlan.Builder.LinesKey)_ |  |
| 2869 | 54 | `int estateLines(int c, boolean across, boolean bl, int w, int h)` _(in DistrictPlan.Builder)_ | ESTATE LINES: which lines of cell c's interior its streets take - and FLIP when they run the other way from `across` - for the w x h building it opens for, its ring laid: of the prototype's spine (MIDDLE) and the stri... |
| 2925 | 3 | `boolean streetAt(int x, int y)` _(in DistrictPlan.Builder)_ | Whether plot (x, y) is a street now (not beneath a highway): what estateLines() reads a ring whole by. |
| 2930 | 6 | `int lineKind(int x, int y, boolean horizontal)` _(in DistrictPlan.Builder)_ | What lay() would make of plot (x, y) on a cell's street running east-west (horizontal) or north-south, laying nothing: 0 refused, 1 a street, 2 a street beneath a highway (layPlot()'s rules, the game's). |
| 2962 | 70 | `int holds(int x0, int y0, boolean across, int lines, int w, int h)` _(in DistrictPlan.Builder)_ | How many w x h boxes cell c (its interior from x0, y0) holds with its streets on `lines`, running north-south when `across`: its free ground (linesFree) less the streets, its touch and reach from the streets about it ... |
| 3040 | 5 | `boolean lay(int x, int y, boolean horizontal, boolean arterial, byte r)` _(in DistrictPlan.Builder)_ | A street plot at (x, y) of a line running east-west (horizontal) or north-south: owned, not the sea - the prototype's _street - and, with the spec's rules, not a mine's site, fresh water only as a bridge no wider than... |
| 3046 | 17 | `boolean layPlot(int x, int y, boolean horizontal, boolean arterial, byte r)` _(in DistrictPlan.Builder)_ |  |
| 3071 | 47 | `int prune()` _(in DistrictPlan.Builder)_ | NO STRAY ROADS (Jerus: "all roads connected, no stray roads"): of the plots a cell's opening laid, each piece of the whole network smaller than LATTICE plots - a street the ground cut down to less than a block, touchi... |
| 3120 | 8 | `int streetNeighbours(int p)` _(in DistrictPlan.Builder)_ | A plot's street neighbours, beneath a highway included. |
| 3130 | 7 | `boolean cutAt(int x, int y)` _(in DistrictPlan.Builder)_ | Whether a plot is ground the cut runs along: not the city's, the sea, fresh water no street crosses, or past the frame. |
| 3147 | 89 | `int shore(int bx0, int by0, int bx1, int by1)` _(in DistrictPlan.Builder)_ | WHERE THE GROUND CUTS A CELL (spec 2.4, H2: streets favour the grid but need not keep it). |
| 3251 | 15 | `void freshCell(int c)` _(in DistrictPlan.Builder)_ | A cell's free rows as it opens (BITS, 0.7.94): its ground no building is barred from (openRows) less the streets its opening laid (the changed plots) - what refreeCell() reads of occ, since a cell not open holds no bu... |
| 3268 | 20 | `void groundRow(int y)` _(in DistrictPlan.Builder)_ | Row y of the ground (THE GROUND, A ROW AT A TIME): dry, barred, its lay code from the ground, the runs' plots, the highways' and the parted streets' bits, an anchor. |
| 3290 | 8 | `void fixedPlot(int p, int x, int base, byte f)` _(in DistrictPlan.Builder)_ | A plot of the city's runs: listed, and a highway's in its row's bits and listed too. |
| 3300 | 12 | `void freshRow(int y)` _(in DistrictPlan.Builder)_ | Row y's fresh water runs (THE GROUND, A ROW AT A TIME): each plot of a run its length along the row, a street refused along one wider than its bridge. |
| 3314 | 12 | `void freshColumns(int y)` _(in DistrictPlan.Builder)_ | ...and down each column, read at row y: a column's run goes on where its plot is fresh water, and ends where it is not (runFrom, its first row). |
| 3328 | 3 | `void freshColumnsEnd()` _(in DistrictPlan.Builder)_ | ...the runs still open at the frame's last row ended there. |
| 3333 | 5 | `void columnRun(int x, int end)` _(in DistrictPlan.Builder)_ | Column x's run from runFrom[x] to the row before `end`: its length on each plot, a street refused down one wider than its bridge. |
| 3340 | 15 | `void fieldRow(int y)` _(in DistrictPlan.Builder)_ | Row y's fields (spec 2.6) and the rest (THE GROUND, A ROW AT A TIME): barred with the verges, occ as a build begins, the field plots, both as bits. |
| 3357 | 9 | `void cellRows(int c)` _(in DistrictPlan.Builder)_ | Cell c's interior rows of free ground and of fields, from the rows' bits, and whether it has a field. |
| 3374 | 12 | `void vergeWideRow(int y)` _(in DistrictPlan.Builder)_ | VERGES (H5, 0.7.94): the plots on or beside a highway plot, corners included - no building's - as bits a row: the highways' bits (hwRows) spread a plot either way along the row (vergeWideRow()), then a row either way ... |
| 3387 | 5 | `void vergeRow(int y)` _(in DistrictPlan.Builder)_ |  |
| 3394 | 6 | `static long bitsOf(long[] rows, int y, int x0)` _(in DistrictPlan.Builder)_ | The INTERIOR bits of row y of `rows` (WORDS longs a row) from plot x0: a cell's interior row (x0 from 1 to SIDE - INTERIOR). |
| 3416 | 14 | `void fixedCodes()` _(in DistrictPlan.Builder)_ | Each plot of a run's lay code (LAY CODES): a run along a line refuses it that way, a highway crossed is laid beneath - worked out for those plots alone, after start() gave them the ground's. |
| 3443 | 14 | `void refreeCell(int c)` _(in DistrictPlan.Builder)_ | A cell's free rows from occ: bit i of row j for interior plot (x0 + i, y0 + j). |
| 3459 | 5 | `void staleAround(int c)` _(in DistrictPlan.Builder)_ | The cells whose reach a change of c's streets may move: it and the eight about it, their reach worked out again when next asked (reachCell() forgets what a cell was known not to hold if its reach grew). |
| 3469 | 3 | `static long spread(long a)` _(in DistrictPlan.Builder)_ | A window's row of bits spread a plot either way along it, kept to the window. |
| 3474 | 34 | `void reachCell(int c)` _(in DistrictPlan.Builder)_ | A cell's reach rows: plots within REACH of a street, across corners, and those one plot from one - the prototype's reach_map, over the cell's interior and the REACH plots about it. |
| 3510 | 5 | `static int runs(int r, int w)` _(in DistrictPlan.Builder)_ | Rows where a run of w set bits starts: bit i set where bits i .. |
| 3517 | 7 | `static int anyIn(int r, int w)` _(in DistrictPlan.Builder)_ | ...and where a box w wide starting there holds any set bit. |
| 3532 | 99 | `long tryPlace(int c, int w, int h, boolean fields)` _(in DistrictPlan.Builder)_ | The first spot in cell c where a w x h building fits on free ground within reach of a street - touching one first (a plot of it one from a street, across corners), then within REACH - in rows from the cell's north-wes... |
| 3639 | 31 | `boolean closeSpine(DistrictPlan out, int c)` _(in DistrictPlan.Builder)_ | An estate cell taken whole (spec 2.3: "a building over 15 plots ... |
| 3672 | 15 | `static int remember(int[][] ws, int[][] hs, int n, int c, int w, int h)` _(in DistrictPlan.Builder)_ | A shape a cell holds no box of, kept with the others it is no larger than: those larger than it go (they hold none either). |
| 3699 | 54 | `boolean mergeFor(DistrictPlan out, int c, int w, int h)` _(in DistrictPlan.Builder)_ | In a homes cell: the street segments inside the first group of long blocks (MERGE_GROUPS, in order) that holds the building, has no building in it and is more than MERGE_DRY dry ground or street, returned to ground - ... |
| 3755 | 7 | `void takeUp(int p)` _(in DistrictPlan.Builder)_ | Plot p taken up as a merge takes up a street: no street, no arterial, no role, free ground unless blocked. |
| 3768 | 3 | `int stateOf(int p)` _(in DistrictPlan.Builder)_ | Plot p's state as takeUp() changes it: its role (bits 0 to 2), art (8), blvd (16), under (32), str (64) and occ + 1 (bits 7 and 8). |
| 3773 | 9 | `void takeUpKept(int p)` _(in DistrictPlan.Builder)_ | Plot p taken up, its state kept for putBack(). |
| 3784 | 12 | `void putBack()` _(in DistrictPlan.Builder)_ | Every plot takeUpKept() took up put back as it was, the last first. |
| 3798 | 4 | `boolean reaches(int x, int y, int w, int h)` _(in DistrictPlan.Builder)_ | Whether some street lies within REACH of the box (x, y, w, h), across corners - the reach PlanCheck holds every building to (a street beneath a highway is not one) - read from the street bits a row at a time. |
| 3808 | 15 | `void indexBuildings()` _(in DistrictPlan.Builder)_ | cellBuilding and nextBuilding brought up to the buildings placed so far. |
| 3825 | 3 | `static boolean nearBox(int bx, int by, int bw, int bh, int x0, int y0, int x1, int y1)` _(in DistrictPlan.Builder)_ | Whether the box (bx, by, bw, bh) lies within REACH + 1 of the plots x0..x1 by y0..y1: a street taken up there may have been the box's. |
| 3841 | 37 | `boolean stubsFree(int c, int gx, int gy, int gw, int gh)` _(in DistrictPlan.Builder)_ | The dead ends a merge of the blocks in box (gx, gy, gw, gh) of cell c, a homes cell of a varied layout (CL2), would leave, found before anything is taken up: each street plot beside the box that would lose a street to... |
| 3886 | 33 | `boolean mergeVaried(int c, int gx, int gy, int gw, int gh)` _(in DistrictPlan.Builder)_ | A merge of the blocks in box (gx, gy, gw, gh) of cell c, a homes cell of a varied layout (CL2), whose stubs stubsFree() has found: the box's streets taken up as mergeFor() takes them up, then the stubs, tentatively - ... |
| 3926 | 59 | `boolean splits(int gx, int gy, int gw, int gh)` _(in DistrictPlan.Builder)_ | Whether taking up the streets inside box (gx, gy, gw, gh) would part streets outside it that reach each other now: the streets beside the box, each that the first reaches through the network, must still reach it witho... |
| 3993 | 26 | `boolean meets(int p, int gx, int gy, int gw, int gh, int near, int apart)` _(in DistrictPlan.Builder)_ | Whether a walk from street plot p, round box (gx, gy, gw, gh), meets a plot joined to the near walk's first (markB holding `near`). |
| 4027 | 12 | `boolean ringWhole(int gx, int gy, int gw, int gh)` _(in DistrictPlan.Builder)_ | Whether every plot of the ring just outside the box (its corners among them) is in the frame and a street or beneath a highway: a closed loop of street round the box, joining every street that meets it. |
| 4041 | 3 | `boolean reachFrom(int p0, int gx, int gy, int gw, int gh, int[] mark, int st, int[] targets)` _(in DistrictPlan.Builder)_ | Marks the streets reached from plot p0 with stamp st in mark, four-connected, none inside box (gx, gy, gw, gh) when gx >= 0; whether every plot of targets (when given) was reached, the walk stopping when they are. |
| 4046 | 32 | `boolean reachFrom(int p0, int gx, int gy, int gw, int gh, int[] mark, int st, int[] targets, int rx0, int ry0, int rx1, int ry1)` _(in DistrictPlan.Builder)_ | ...keeping within plots (rx0, ry0) to (rx1, ry1), inclusive. |
| 4093 | 11 | `boolean joinable(int a, boolean horizontal)` _(in DistrictPlan.Builder)_ | Whether a lattice step onto plot a going horizontally (or not) may be laid: the join's ground. |
| 4106 | 7 | `boolean groundJoinable(int a, boolean horizontal)` _(in DistrictPlan.Builder)_ | Whether a step onto plot a going horizontally (or not) may be laid off the lattice, where the join's lattice finds no way: as joinable(), on any plot (H2: a street follows the ground where it must). |
| 4115 | 5 | `void layJoin(int a)` _(in DistrictPlan.Builder)_ | Lays a plot the join or the mines' reach took: a street, or beneath a highway. |
| 4156 | 55 | `void access(DistrictPlan out, int placed)` _(in DistrictPlan.Builder)_ | ACCESS ROADS. |
| 4213 | 7 | `int gate(int[] gates, int n, int x, int y)` _(in DistrictPlan.Builder)_ | Adds plot (x, y) to the gates if it is one the road may use (inside the frame's own plots, ground a street may take either way); the count. |
| 4222 | 6 | `boolean accessible(int a, boolean horizontal)` _(in DistrictPlan.Builder)_ | Whether the access road may enter plot a going horizontally (or not): a plot of the frame's own, the ground a street may take, and a highway's only where a street passes beneath it already. |
| 4230 | 5 | `boolean accessEnd(int a, boolean ramps, int cx, int cy)` _(in DistrictPlan.Builder)_ | Whether plot a is where a road ends: a ramp's (a highway plot carrying a ramp with a street beneath, within reach of the work at (cx, cy)), or an arterial street. |
| 4241 | 39 | `int accessSearch(int[] gates, int n, boolean ramps, int cx, int cy)` _(in DistrictPlan.Builder)_ | The nearest end from the gates, by plot cost (Dial's algorithm: costs 1 and ACCESS_NEW), as the plot it ends on with accPrev leading back to a gate (its own accPrev -1); -1 for none. |
| 4282 | 1 | `int accCost(int a)` _(in DistrictPlan.Builder)_ | What entering plot a costs the access road: 1 where a street (or one beneath a highway) already is, ACCESS_NEW where it would be laid. |
| 4284 | 6 | `void accPush(int d, int a)` _(in DistrictPlan.Builder)_ |  |
| 4332 | 65 | `void sidings(DistrictPlan out, int placed)` _(in DistrictPlan.Builder)_ | SIDINGS (D14). |
| 4399 | 6 | `boolean sidingBefore(int a, int b)` _(in DistrictPlan.Builder)_ | Whether the work at plot index `a` comes before the one at `b` in the sidings' order: more plots, then further north, then further west. |
| 4407 | 3 | `boolean railAt(int x, int y)` _(in DistrictPlan.Builder)_ | Whether plot (x, y) of the frame is a plot of the runs' railway (FIXED_RAIL). |
| 4412 | 8 | `int sidingGate(int[] gates, int[] heads, int n, int x, int y, int heading)` _(in DistrictPlan.Builder)_ | Adds plot (x, y) to the gates, entered going `heading` (0 north, 1 east, 2 south, 3 west: away from the box), if the siding may start there: open ground, not a street. |
| 4427 | 9 | `boolean sidingOpen(int a)` _(in DistrictPlan.Builder)_ | Whether a siding may take plot a: SIDING_EDGE inside the frame's edge, dry owned ground, no building's, no runs' plot (track, yard, reserve, highway), no mine's site, no other siding's, and no plot beside a highway (i... |
| 4438 | 3 | `boolean crossesStreet(int b, int heading)` _(in DistrictPlan.Builder)_ | Whether the street at plot b goes across a siding moving `heading` (a street at right angles to it beside b), so that the siding crosses it and does not run along it. |
| 4450 | 46 | `boolean sidingSearch(int[] gates, int[] heads, int n)` _(in DistrictPlan.Builder)_ | The cheapest way from the gates to the railway, by Dial's algorithm over states (plot, heading): true with sdState the state of the way's last plot (the railway beside it at sdRail), sdLast its plot, and sdPrev leadin... |
| 4498 | 6 | `boolean sidingEnd(int b, int heading)` _(in DistrictPlan.Builder)_ | Whether plot b, entered going `heading`, is a plot of the railway the siding may meet: one the frame's own (not its edge row or column), met from the side. |
| 4505 | 6 | `void sdPush(int d, int s)` _(in DistrictPlan.Builder)_ |  |
| 4520 | 5 | `int streetsInOrder(long[] alsoRows)` _(in DistrictPlan.Builder)_ | THE STREETS IN ORDER (0.7.94, batch RD7): every street plot - and beneath a highway, and with `alsoRows` its plots too - into ordered[], in plot order, read from the street bits a row of 64 at a time (rowBits; a highw... |
| 4527 | 14 | `int streetsInRow(int y, long[] alsoRows, int n)` _(in DistrictPlan.Builder)_ | ...row y's, into ordered[] from n: the count after them (a row a call, THE GROUND, A ROW AT A TIME's reason). |
| 4552 | 3 | `int[] pieces(int[] lab)` _(in DistrictPlan.Builder)_ | The street pieces, four-connected (beneath a highway included): each plot's label into lab, the sizes returned. |
| 4557 | 26 | `int[] pieces(int[] lab, boolean parted)` _(in DistrictPlan.Builder)_ | ...with the parted streets of the districts before it (Input.partedAnchor) as streets of the pieces too (joinOut()) when `parted`; each piece's first plot in plot order into pieceRep, its place in ordered[] into pieceAt. |
| 4585 | 11 | `int pieceStep(int[] lab, int u, int n, int sp, boolean[] also)` _(in DistrictPlan.Builder)_ | pieces()'s step (a plot a call): plot u's neighbours of piece n - streets, beneath a highway, or of `also` - not yet labelled, labelled and stacked above sp. |
| 4601 | 15 | `int joinStep(int u, int way, int pc, int st, int[] lab, int[] prev)` _(in DistrictPlan.Builder)_ | joinFromSmall()'s walk's step (a plot a call): plot u's neighbours along the lattice (way 0) or over the ground, from piece pc, by walk st - the first street of another piece reached, or -1, the rest queued. |
| 4618 | 18 | `int joinOutStep(int u, int way, int pc, int st, int[] lab, int[] prev, boolean[] touches)` _(in DistrictPlan.Builder)_ | joinOut()'s walk's step: as joinStep(), the walk ending at a street of a piece that touches an anchor, or beside an anchor. |
| 4638 | 8 | `int seedPiece(int pc, int[] lab, int[] prev, int[] q, int st)` _(in DistrictPlan.Builder)_ | A walk's start: piece pc's plots, in plot order, into queue q from its first (pieces()), each reached by walk st with no way back. |
| 4657 | 31 | `int join()` _(in DistrictPlan.Builder)_ | ONE NETWORK (the prototype's _join): while the streets are in more than one piece, breadth first from the largest along the lattice's lines to the nearest other piece, and the way laid - over a long block's middle lin... |
| 4697 | 34 | `int joinFromSmall()` _(in DistrictPlan.Builder)_ | The join with the game's rules: each piece but the largest, the smallest first, looks for the nearest other piece - along the lattice, then over the ground (H2) - from its own plots, which is the same way back as the ... |
| 4749 | 37 | `int joinOut()` _(in DistrictPlan.Builder)_ | ACROSS DISTRICTS (spec 2.4: "a district whose first cell does not touch an open cell of its inner neighbour opens an arterial run to it along the lattice"; 0.7.88, batch RD2 - RD1 had no neighbour's plan to join to). |
| 4793 | 5 | `void markPartedPiece(int p)` _(in DistrictPlan.Builder)_ |  |
| 4799 | 4 | `void clearParted()` _(in DistrictPlan.Builder)_ |  |
| 4805 | 28 | `void markParted(DistrictPlan out)` _(in DistrictPlan.Builder)_ | PARTED (0.7.88): out.parted - each street plot whose piece, over the plan's final streets and the parted streets of the districts before it, touches no anchor, nor is the first district's largest piece. |
| 4839 | 13 | `int partedStep(DistrictPlan out, int u, int n, int sp)` _(in DistrictPlan.Builder)_ | markParted()'s step (a plot a call): plot u of piece n counted, whether it touches an anchor, its neighbours of the piece not yet labelled labelled and stacked above sp. |
| 4854 | 6 | `boolean nearAnchor(int p)` _(in DistrictPlan.Builder)_ | Whether plot p is, or is beside (four ways), a plot of the frame's edge a district before this one lays a street on. |
| 4864 | 13 | `boolean surfaces(int x, int y)` _(in DistrictPlan.Builder)_ | Whether this district surfaces a plot: off its seams, or on a seam it comes first on (Input.surfaces). |
| 4886 | 64 | `void surface(DistrictPlan out)` _(in DistrictPlan.Builder)_ | The model's road as the streets' surface (the prototype's, over its street plots): arterials first, then by their cell's rank from the hub, then row by row - every street HALF while the road lasts, a TRACK where it do... |
| 4955 | 22 | `void surfaceRow(DistrictPlan out, int y, int[] start)` _(in DistrictPlan.Builder)_ | surface()'s row y (a row a call): each street every street's (scratchLab); one it surfaces with its bucket (scratchPrev, pieceStack), counted in start[]; a one-sided seam (ordered). |
| 4979 | 13 | `void code(DistrictPlan out, int p)` _(in DistrictPlan.Builder)_ | Plot p's street code made whole: its kind (a seam with none, beneath a highway), role, bridge and crossing. |


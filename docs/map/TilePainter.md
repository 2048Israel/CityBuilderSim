# TilePainter.java - 564 lines · 20 methods · 36 constants · model

`ham/citybuildersim/TilePainter.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Paints one tile of the city map: from the tile's ground, what the city owns of it, its district's street plan through it - each street's kind, width and role, and every building's box - the city's highways and railway through it and the resource sites under it, what each plot is - the same picture from the same inputs, on any machine.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6, a port
> of the design's MapProto). Jerus's mockup (city-map.html) grew a whole city
> step by step - roads from their ends, plots beside them - and stamped every
> object with the step it appeared. A city of ten billion people cannot be
> grown that way or stored plot by plot, so the map stores only counts by
> district (CityMap), and this paints a 32 x 32-plot tile when the screen
> needs it.
> 
> ON THE DISTRICT'S PLAN SINCE 0.7.88 (batch RD2; the project's
> spec-roads-and-ports.md 2.9). From 0.7.64 to 0.7.87 a tile was painted from
> what CityMap's deal gave it - its share of its district's buildings and
> road plots - and grew its roads itself: main streets to "ports" on its
> edges, a grid, then lanes every two plots and a fill beside every road,
> none of it on the tile's edge ring. Jerus's screenshot of his city showed
> what that made: a tile of buildings with no road beside a tile that was a
> maze of road with a shop in each hole, walls of buildings along every tile
> edge, and a highway crossing itself (spec 1). Now the district is planned
> whole (DistrictPlan: cells, one street network, the model's road drawn as
> the streets' surface, a box for every building; CityMap's THE DRAWN PLANS),
> and a tile only paints its share of the plan, in order:
> 
>   SITES     a resource's sites are its fields on the ground; a mine or
>             well stands on its own site, the whole of it;
>   RUNS      the city's highways and railway track through it, plot for
>             plot (since 0.7.89 the city's runs, CityRuns: elevated
>             highways with their ramps, the track bridging them, a mine on
>             its site beneath; THE NETWORK's plan's in CityMap until 0.7.88);
>   STREETS   the plan's: each plot's surface - gravel, paved (or, to
>             0.7.88, an Elevated Highway's plots no run took), half or
>             full width - or a TRACK where the city has bought no road;
>             an arterial's, a boulevard's or a cell's street; a bridge
>             over fresh water. A street crossing the track keeps the track
>             and takes the street's surface (a level crossing); one
>             beneath a highway passes under it (spec 2.7: elevated);
>   BUILDINGS every box the plan placed on the tile, each on its type's own
>             land, the Rail Terminals the runs set on their track as yards
>             (since 0.7.89), the terminals and tank farms the city's shore
>             holds (since 0.7.97, CityShore), and those the city had no
>             room for, packed at its edge without a street (R7);
>   AT SEA    (since 0.7.97) a terminal's quay, a platform's jacket, its
>             wells and its 500 m ring, a crude pipeline (AT SEA below).
> 
> No deal, no road tiles, no lanes, no fill and no edge ring: a street
> crosses a tile's edge wherever its line does (spec 8.5), so nothing is a
> "port" any more (spec 8.3).
> 
> Pure: no state between calls, nothing read from the game. MapCheck holds
> what it draws, on the drawn plots themselves.

**Uses:** [BuildingVisual](BuildingVisual.md) (8), [DistrictPlan](DistrictPlan.md) (8), [World](World.md) (4), [CityRuns](CityRuns.md) (1), [CityMap](CityMap.md) (1)

**Used by (11):** [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [DistrictPlan](DistrictPlan.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PlanCheck](PlanCheck.md), [ReadPathCheck](ReadPathCheck.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 67 | · what a plot is |
| 86 | · the works at sea (0.7.97) |
| 110 | · a site's state |
| 121 | · a street's code (0.7.88) |
| 140 | THE NETWORK'S RULES, IN PLOTS (0.7.72, batch N3; the plan's since |
| 174 | THE INPUTS AND THE PICTURE |
| 387 | THE PAINT |
| 468 | AT SEA (0.7.97, batch O13; runs/spec-oil.md 2.12, the research's 3.3 |
| 541 | · the mines on their sites |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `TilePainter.TILE` | `World.TILE` | Plots on a tile's side: World.TILE, 32. |
| 65 | `TilePainter.PLOTS` | `TILE * TILE` | Plots on a tile: 1,024. |
| 70 | `TilePainter.EMPTY` | `0` | Nothing on it. |
| 72 | `TilePainter.ROAD` | `1` | A road: a street of the plan, or a highway. |
| 74 | `TilePainter.BUILDING` | `2` | A building. |
| 76 | `TilePainter.FIELD` | `4` | A resource's site with nothing on it. |
| 78 | `TilePainter.RAIL` | `5` | A railway's track (0.7.72): its Painted.road is 0, or the kind of the street that crosses the track there. |
| 81 | `TilePainter.RAIL_OVER` | `CityRuns.F_RAIL_OVER` | A plot of the runs where the railway bridges a highway (0.7.89, CityRuns.F_RAIL_OVER; spec 2.8): Input.fixed's code, the highway's plot with the track over it. |
| 84 | `TilePainter.TRACK` | `4` | A road plot's kind past BuildingVisual's GRAVEL, PAVED and HIGHWAY (Painted.road, 0.7.88): a TRACK, a street the city has bought no road for (spec 2.5, R4). |
| 89 | `TilePainter.QUAY` | `1` | A plot's work at sea (Input.sea, Painted.sea's low two bits): a terminal's quay, out over the water from its box (CityShore)... |
| 91 | `TilePainter.JACKET` | `2` | ...an offshore platform's jacket... |
| 93 | `TilePainter.WELL` | `3` | ...a platform well, on the middle of its sea site. |
| 95 | `TilePainter.SEA_WORK` | `3` | The low two bits of Painted.sea. |
| 97 | `TilePainter.SEA_RING` | `4` | Painted.sea's bit for a plot a platform's safety ring crosses... |
| 99 | `TilePainter.SEA_PIPE` | `8` | ...and for one a crude pipeline crosses (buried: it takes no plot, and is drawn over what stands there). |
| 102 | `TilePainter.PLATFORM_ZONE_M` | `500` | A platform's safety zone, drawn as a faint ring about its jacket: 500 m (the research's 3.3 [W32]). |
| 105 | `TilePainter.RING_PLOTS` | `PLATFORM_ZONE_M / World.PLOT_M` | ...its radius in plots: 16.7. |
| 108 | `TilePainter.JACKET_PLOTS` | `2` | A jacket's side, in plots: 2, 60 m - mockup 3's platform, 12 px at 6 m a pixel (72 m), in whole plots (star O13-6). |
| 113 | `TilePainter.UNOWNED` | `0` | A site on ground the city does not own. |
| 115 | `TilePainter.UNWORKED` | `1` | ...on the city's ground, its holding not yet worked. |
| 117 | `TilePainter.WORKING` | `2` | ...in the holding being worked: drawn half grey. |
| 119 | `TilePainter.WORKED_OUT` | `3` | ...in a holding worked out: drawn grey (the mockup's star 7). |
| 124 | `TilePainter.S_KIND` | `7` | A plot's street as its district's plan gives it the painter (CityMap.Drawn, Input.street), a byte: its kind in the low three bits - DistrictPlan's NONE to UNDER... |
| 126 | `TilePainter.S_WIDTH_SHIFT` | `3` | ...its surface's width in bits 3 and 4: 1 half (15 m), 2 full (30 m), 0 none... |
| 128 | `TilePainter.S_ROLE_SHIFT` | `5` | ...its role in bits 5 and 6: 0 a cell's street (or one along a cut, or the join's), S_ARTERIAL, S_BOULEVARD... |
| 129 | `TilePainter.S_ARTERIAL` | `1, S_BOULEVARD = 2` |  |
| 131 | `TilePainter.S_BRIDGE` | `0x80` | ...and a bridge over fresh water in bit 7. |
| 134 | `TilePainter.STREET` | `1` | A painted street plot's role (Painted.role): a cell's street... |
| 136 | `TilePainter.ARTERIAL` | `2` | ...an arterial, on a cell's ring (every 32 plots, 960 m)... |
| 138 | `TilePainter.BOULEVARD` | `3` | ...a boulevard's, either row of a boulevard cell's arterials. |
| 152 | `TilePainter.JUNCTION_APART` | `8` | The + junction floor: 8 plots - eight House footprints (a House's 8,000 sq ft is drawn on one whole plot, BuildingVisual.footprint()), Jerus's "about 8 houses' length" from one + junction to the next (star N3-1). |
| 155 | `TilePainter.REACH` | `JUNCTION_APART / 2` | A building is near a road when one lies within this many plots of it, across corners: 4, half the junction floor - a block between streets at the floor is 7 plots across, and its middle plot 4 from them (star N3-2). |
| 163 | `TilePainter.STREET_BRIDGE` | `6` | A street's longest crossing of fresh water, gravel or paved: 6 plots (180 m), the mockup's paved bridge (its star 1); gravel's too since 0.7.77 (batch N5; Jerus, 2026-10-08: "gravel road bridge rivers sure"). |
| 166 | `TilePainter.MAX_BRIDGE` | `{ 0, STREET_BRIDGE, STREET_BRIDGE, 14 }` | The longest crossing of fresh water, in plots, by road kind: gravel and paved STREET_BRIDGE, a highway 14 (the mockup's star 1) - an arterial's since 0.7.87 (DistrictPlan.ARTERIAL_BRIDGE). |
| 169 | `TilePainter.RAIL_BRIDGE` | `14` | The longest crossing of fresh water a railway's track makes: 14, as a highway's (star N3-6). |
| 172 | `TilePainter.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 181 | `public long seed` | The world's seed. |
| 183 | `public long tx, ty` | The tile's column and row: plots / TILE. |
| 185 | `public final byte[] terrain` | Its ground, a World class a plot, in rows. |
| 187 | `public final boolean[] owned` | Whether the city owns each plot. |
| 189 | `public final byte[] street` | Each plot's street from its district's plan, in the S_ codes (0.7.88): the district's own, and on its first column and row the street the district west or north of it lays there where it lays it (CityMap.tileInput());... |
| 191 | `public final byte[] beyond` | ...and the street on each plot just outside the tile, north, east, south and west (TILE each, along the edge): what a street at the edge joins to, as the plans have it. |
| 193 | `public final byte[] fixed` | Each plot of the city's runs (CityRuns.fill(); CityMap's network's until 0.7.88): a highway's (BuildingVisual.HIGHWAY), the railway's track (RAIL) or the track bridging a highway (RAIL_OVER), 0 for none... |
| 195 | `public final byte[] fixedBeyond` | ...and those just outside the tile, as beyond. |
| 197 | `public final byte[] marks` | Each plot's marks from the runs (0.7.89): a highway's ramp (CityRuns.M_RAMP), a 45-degree stretch's (M_DIAG and its heading)... |
| 199 | `public final byte[] marksBeyond` | ...and those just outside the tile, as beyond. |
| 201 | `public BuildingVisual.Type[] types` | The types, by id (BuildingVisual.table()). |
| 204 | `public int buildings` | The plan's buildings on it (0.7.88): how many, and each one's box as CityMap.box() packs it (x, y, w - 1, h - 1 at five bits each, PACKED_BIT when packed without a street) and its type id, two ints a building. |
| 205 | `public int[] boxes` |  |
| 207 | `public int[] counts` | ...its buildings by type id (a mine or well standing on a site is the site's, not counted): what a hover and a harness read. |
| 210 | `public int sites` | How many sites lie on it. |
| 212 | `public int[] siteX0` | Each site's square, in plots of the tile (clipped to it): its first column and row, its last. |
| 214 | `public int[] siteKind` | ...its resource's ordinal, its state (UNOWNED to WORKED_OUT), and the type id of the mine or well on it, or -1. |
| 216 | `public long[] siteKey` | ...and the site itself, its square's first plot in the world packed (x << 32 \| y), unclipped: one site seen from the tiles it spans (0.7.64). |
| 219 | `public final byte[] sea` | Each plot's work at sea (0.7.97): a terminal's QUAY, a platform's JACKET, a platform WELL; 0 for none. |
| 221 | `public int rings` | The platforms whose safety ring (RING_PLOTS about its jacket's middle) may cross the tile: how many, and each one's middle in the tile's plots (it may lie off the tile). |
| 222 | `public double[] ringX` |  |
| 224 | `public int pipes` | The crude pipelines that may cross it: how many, and each one's ends in the tile's plots, {from, to}. |
| 225 | `public double[] pipeAX` |  |
| 300 | `public final byte[] use` | Each plot's use: EMPTY, ROAD, BUILDING, FIELD or RAIL. |
| 302 | `public final byte[] road` | A road plot's kind: BuildingVisual's GRAVEL, PAVED or HIGHWAY, or TRACK (0.7.88); on a RAIL plot the kind of the street crossing the track there, or 0. |
| 304 | `public final byte[] width` | A street plot's surface (0.7.88): 1 half width (15 m), 2 full (30 m), 0 for a track, a highway's own plot or none. |
| 306 | `public final byte[] role` | A street plot's role (0.7.88): STREET, ARTERIAL or BOULEVARD; 0 for a highway's own plot or none. |
| 308 | `public final boolean[] beneath` | A highway's plot a street passes beneath (0.7.88; spec 2.7: elevated). |
| 310 | `public final byte[] run` | What the city's runs lay on each plot, as Input.fixed (0.7.89): their track and highways under a building too - a yard over its track, a mine on its site beneath a highway or across a track. |
| 312 | `public final boolean[] bridge` | A road plot (or the track) that bridges fresh water. |
| 314 | `public final short[] bld` | The building on a plot, its index + 1; 0 for none. |
| 316 | `public final short[] site` | The site a plot lies in, its index + 1; 0 for none. |
| 318 | `public int[] bx` | The buildings: each one's box in plots and type id. |
| 320 | `public int[] bsite` | The site a mine or well stands on, or -1. |
| 322 | `public boolean[] bpacked` | Whether it was packed without a street, the city having no room for it in any district's plan (R7). |
| 324 | `public int buildings, packed` | How many buildings were drawn, and how many of them packed (R7). |
| 326 | `public final int[] streets` | Street plots laid by kind [0, gravel, paved, highway, track] - a crossing of the track among them - and their surface in half plots by kind [0, gravel, paved, highway]. |
| 328 | `public int highwayPlots, railLaid, crossings` | A highway's own plots laid, the railway's track laid (since 0.7.89 under a building too: a yard, a mine), and the plots where a street crosses the track. |
| 330 | `public final byte[] sea` | What each plot carries at sea (0.7.97): its work (SEA_WORK's QUAY, JACKET or WELL), and SEA_RING and SEA_PIPE where a platform's ring or a pipe crosses it. |
| 332 | `public int quayPlots, jacketPlots, wellPlots, ringPlots, pipePlots` | The plots of quay, of jacket and of platform well drawn, and those a ring or a pipe crosses: what MapCheck sums over the tiles to see each drawn once. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 508 | **type** `public final class TilePainter` | Paints one tile of the city map: from the tile's ground, what the city owns of it, its district's street plan through it - each street's kind, width and role, and every building's box - the city's highways and railway... |
| 59 | 1 | `private TilePainter()` |  |

### what a plot is (lines 67-85)

### the works at sea (0.7.97) (lines 86-109)

### a site's state (lines 110-120)

### a street's code (0.7.88) (lines 121-139)

### THE NETWORK'S RULES, IN PLOTS (0.7.72, batch N3; the plan's since (lines 140-173)

### THE INPUTS AND THE PICTURE (lines 174-386)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 117 | **type** `public static final class Input` | What a tile is painted from: filled by CityMap (tileInput()), or by hand in a harness. |
| 228 | 5 | `public void clearSea()` _(in TilePainter.Input)_ | Clears the works at sea; the arrays are reused. |
| 235 | 6 | `public void addRing(double x, double y)` _(in TilePainter.Input)_ | Adds a platform's ring about (x, y), in the tile's plots. |
| 243 | 8 | `public void addPipe(double ax, double ay, double bx, double by)` _(in TilePainter.Input)_ | Adds a pipe from (ax, ay) to (bx, by), in the tile's plots. |
| 253 | 4 | `public void clearBuildings()` _(in TilePainter.Input)_ | Clears the buildings; the arrays are reused. |
| 259 | 7 | `public void addBuilding(int box, int type)` _(in TilePainter.Input)_ | Adds a building: its packed box and type id. |
| 268 | 1 | `public void clearSites()` _(in TilePainter.Input)_ | Clears the sites; the arrays are reused. |
| 271 | 3 | `public void addSite(int x0, int y0, int x1, int y1, int kind, int state, int mine)` _(in TilePainter.Input)_ | Adds a site: its square (clipped to the tile), resource, state and the mine on it (-1 for none). |
| 276 | 13 | `public void addSite(int x0, int y0, int x1, int y1, int kind, int state, int mine, long key)` _(in TilePainter.Input)_ | ...with the site's own key (siteKey). |
| 291 | 4 | `public boolean anyOwned()` _(in TilePainter.Input)_ | Whether any of its plots is owned. |
| 298 | 70 | **type** `public static final class Painted` | A painted tile: what each plot is, and every building on it. |
| 334 | 16 | `void reset()` _(in TilePainter.Painted)_ |  |
| 351 | 11 | `int add(int x0, int y0, int w, int h, int type, int siteIndex, boolean packedOne)` _(in TilePainter.Painted)_ |  |
| 364 | 3 | `public boolean isRoad(int i)` _(in TilePainter.Painted)_ | Whether plot i holds a street or a highway: a road plot, or the track where a street crosses it. |
| 370 | 10 | `public static int kindOf(int code)` | A street code's painted kind: GRAVEL, PAVED, HIGHWAY (a highway's plots laid as a street) or TRACK - a seam no district surfaced is a track - and 0 for none or a street beneath a highway. |
| 382 | 4 | `public static byte roleOf(int code)` | A street code's role as painted: STREET, ARTERIAL or BOULEVARD. |

### THE PAINT (lines 387-467)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 75 | `public static void paint(Input in, Painted p)` | Paints a tile from its inputs into p. |

### AT SEA (0.7.97, batch O13; runs/spec-oil.md 2.12, the research's 3.3 (lines 468-540)

| line | len | member | says |
|---:|---:|---|---|
| 484 | 34 | `static void atSea(Input in, Painted p)` | The works at sea on the tile, into p.sea and its counts. |
| 520 | 5 | `public static boolean ringCrosses(double cx, double cy, double x, double y)` | Whether a ring of RING_PLOTS about (cx, cy) crosses the plot whose corner is (x, y): it passes between the plot's nearest point and its farthest corner. |
| 527 | 13 | `public static boolean segmentCrosses(double ax, double ay, double bx, double by, double x, double y)` | Whether the segment from (ax, ay) to (bx, by) runs through the plot whose corner is (x, y): some length of it inside the plot (Liang-Barsky). |

### the mines on their sites (lines 541-564)

| line | len | member | says |
|---:|---:|---|---|
| 544 | 20 | `private static void standMines(Input in, Painted p)` | Each site with a mine or well on it: the mine on the whole of the site's plots here, whoever owns the ground and whatever it is - the field is the city's (0.7.64: an offshore well stands on its water, and a site of a ... |


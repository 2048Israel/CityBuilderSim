# Roads, islands and ships: a design study

*2026-10-08. A read-only design study. Samuel, with a screenshot of his city's map: "you are going to do ui mockup and design study but not really, you are not touching code, you are just thinking and designing how code should be, basically see the issue? and on top of that buildings get misplaced all the time ... quite alot is going to be added so you need to take into account alot, you are also going to be handling how ships and routes will be handled. basically the first issue is endless + juncitons, and well that highways should mostly be straight and connected to highways, and that all roads should be connected, and to build in another island you need a port atleast two, so some way to track that."*

*His list after the grid (`after-the-grid.md` §3) still stands and this answers it again: highways connected to each other, straight, curving only at the ocean, T and + junctions very sparing; ordinary roads "love being a continuous + junction", at least about 8 houses' length from one + junction to the next (a floor, not a requirement); all roads connect to the main network; every building near a road; rail connects to the rail network, terminals near mines.*

*The code was read, not changed, from the public repository at 0.7.79 (commit f85bcfa): `TilePainter` (1,571 lines), `CityMap` (3,003), `BuildingVisual`, `TileRaster`, `InfrastructureManager`, `GameVersion`, `MapCheck`, `buildings.json`. The design notes read: `after-the-grid.md`, `spec-grid.md`, `the-land-as-a-block-grid.md`, `the-world-the-water-and-the-map.md`, `spec-earth.md`, `spec-zoning.md`, `brief-oil-and-ports-design.md`, `oil-and-ports-research.md`. The prototypes (Python, in `claude/roads-prototype/` beside this note) plan a district under the proposed rules from the game's own `buildings.json` and MapCheck's fixture of Jerus's city, grow highways on a coast, and label landmasses and sea routes. The mockups are the artifact "Roads, Islands & Ships Study". Figures marked (est.) are estimates; the rest are measured from the code or the prototypes.*

---

## 0. In one page

**Why the map looks like the screenshot.** Five causes, all in the map code; nothing in the model is wrong (§1):
1. A district's road plots are all dealt onto a few **road tiles**; the rest of its tiles get buildings and no road (the left tile).
2. On a road tile the grid is a fixed 8-plot lattice, and whatever road is left goes into **lanes every 2 plots and then a fill beside every road**: the maze, with every shop an island (the right tile).
3. A road may cross a tile's **one-plot edge ring** only at one point a side, so buildings fill the ring: a wall of buildings along every tile edge, two walls back to back where tiles meet.
4. An Elevated Highway is **6.5 plots of land**, highways are dealt **per district**, and each district's plan lays its few plots both ways from a hub: a stub that crosses itself (the black + top right).
5. Underneath it all: the map draws the model's **road plots one for one**. Since 0.7.70 a paved road takes 26 plots for 1,200 trips where gravel takes 46 for 900, so a city built paved has about 12% of its ground in road, and no street grid that reaches every building fits in 12% at 30 m plots.

**The fix in one sentence: streets by need, road by surface.** The map lays the **streets** the buildings need (where they run: one network, + junctions on a grid, every building within reach), and draws the city's **road** (the model's Gravel Roads, Paved Roads and Elevated Highways, exactly) as what those streets are: paved or gravel, narrow or full width, or a dirt track where the city has bought no road (§2).

**Cells.** A tile (960 m) becomes a **cell** with an arterial along its west and north edges, so arterials run every 960 m and cells nest in districts exactly. Each cell the city opens gets one layout by what it holds:
- **homes cells** (what follows people): long blocks of 15 × 7 plots, + junctions every 8 plots one way and 16 the other; two blocks merge for a hospital or a university;
- **estate cells** (industry, farms, utilities, plants): one spine street and two strips either side, the big buildings side by side along them, each touching a street (T junctions, no +).

Cells open outward from the hub, homes first, then industry, then the outer kinds, so the streets are one piece and a city reads as town, works and fields.

**Measured on a district of Jerus's city** (the prototype, his buildings at 70% of 54.5 km² of dry ground, 3,617 buildings):
- one network; every + junction 8 or more plots from the next; every building within 4 plots of a street, 83% of them touching one;
- with his roads: 49 cells, 13 of them squared to the + grid at 8 and 26 with boulevards, because his road (22.5% of his ground) is more than the streets need (15.8%);
- the same city built paved: every street paved, 52% of them narrow;
- with half that road: 26% of the streets drawn as tracks.

**Highways.** One network, grown corridor by corridor and never moved: the founding site's row first (a small city's highway is one straight line; Jerus's six are 1.2 km of it), then its column (the one interchange), then parallel corridors one district apart. Straight and few junctions are heavily favoured, not forced (Samuel, 2026-10-09): an arm curves where straight would meet water it cannot bridge or leave the city's ground, and a corridor may begin or end in a T. No building touches a highway: a plot of verge each side. Elevated, so streets pass beneath; ramps only where an arterial crosses (§2.7).

**Islands.** A **landmass** is the city's ground the sea cuts off (and fresh water wider than a highway bridge, 420 m). Home is the founding site's. Another landmass is **linked** once it has a port and a linked landmass has one too, so the first island takes two ports and each after it one more. Ground on an unlinked landmass is owned, counted and priced as today, but nothing can be built on it except the port. Ground split off by a river or lake links by a **Highway Bridge** instead (highway only, fresh water only). The model keeps a small table of the landmasses the city owns ground on; the Earth pack gets a landmass layer (§3).

**Ships and routes.** A route is found once on a 480 m grid of the sea and pulled straight: world trade from its berth out to **the offing** (past the city's ground, toward the most open sea) and on into the abyss; the city's own ports directly to each other. Boats are the oil study's calls (tonnes ÷ one ship's cargo), drawn only on screen; ferries are shuttle fleets sized from the people crossing (§4).

**Order.** The roads are map-only and can go now: three batches, traces byte-identical (§6). One model change is decided: the ground split into lots and streets, so a city past about 80% full still draws every building beside a street (★M). Islands wait for the Earth pack and ride with the ports.

---

## 1. What the map does today, and why it looks like that

The screenshot is two tiles of his city at about 19 px a plot: the left tile holds a water plant, a factory and a utility with no road at all; the right tile is grey road with a shop or a house in each hole; buildings line every tile edge; a dark highway + sits in the top-right corner with buildings inside its arms.

### 1.1 How a tile is painted (0.7.72, batch N3)
- **The model has no positions.** It counts buildings by type; a road is a building whose land is its road plots (Gravel Road 450,000 sq ft = 46 plots and 900 trips; Paved Road 250,000 = 26 and 1,200; Elevated Highway 63,000 = 6.5, drawn as 7, and 1,500 trips; `buildings.json`, `BuildingVisual.cells()`).
- **`CityMap`** places each type's count into 7.68 km districts, then **deals** a district's buildings and road plots onto its 64 tiles (`deal()`, banner at line 1292).
- **The network (line 1755):** a district's roads go to its **road tiles**, a connected set grown from the tile nearest the founding site while the main streets joining them cost no more than `MAINS_SHARE` (0.5) of its road. Its streets are dealt to road tiles only, up to `ROAD_LEAN_MOST` (1.25) times its share, and what is left goes round again with no cap.
- **Buildings (line 1558)** go "onto the road tiles while the district has any", then "the rest where there is room": onto tiles with no road.
- **`TilePainter.paint()` (line 450)** lays the plan's highways and main streets, the large buildings, the grid (`JUNCTION_APART` 8: lines at 4, 12, 20, 28), then **lanes** (`LANE_FEET` 2, 4, 6: three T's per segment, `layLanes()`, line 755), then a **fill** of whatever road is left beside the roads (`fillRoad()`, line 827), then the small buildings.
- **The edge ring:** lanes and the fill may not use a tile's outer ring of plots (`fillable()`, line 892); a road crosses an edge only at one point a side (`PORTS_MOST` 1), where the main street meets it. Small buildings take the ring.
- **Highways:** each district's plan lays its own highway plots from its first road tile's hub along its main row, both ways, at most 4 turns, "both ways spent, it runs across its hub once (the one + it makes)" (line 1784).

### 1.2 The five faults
| # | What the screenshot shows | Cause |
|---|---|---|
| 1 | The left tile: a water plant, a factory and a utility, no road | Buildings go to tiles with no road once the road tiles are full (line 1558). Road tiles are limited by `MAINS_SHARE`, so a district has few. |
| 2 | The right tile: a maze, every shop an island | All the district's road lands on its road tiles; the grid takes 240 plots of a tile, the lanes up to 252 more, the fill the rest, beside every road. |
| 3 | Rows of buildings along every tile edge, double where tiles meet | The edge ring: no lane or fill on it, one crossing a side. |
| 4 | A dark + top right, buildings inside it | Highways dealt per district; each lays its few plots both ways from its hub and crosses itself once. Jerus's city has 6 Elevated Highways: 39 plots, 1.2 km in all. |
| 5 | (underneath) | Road plots are drawn one for one: see 1.3. |

### 1.3 Why exact road plots cannot do it
- **Jerus's city** (MapCheck's fixture): 385 Gravel Roads, 81 Paved Roads, 6 Elevated Highways: 20,013 road plots, **22.5%** of the ground his buildings and roads stand on. That is close to a full grid at 8 (23.4% of a tile) and close to real cities (UN-Habitat asks for at least 30% of a neighbourhood's land in streets [U1]).
- **The same trips on Paved Roads** (0.7.70's advice now prefers them; the screenshot's roads are paved grey): 370 roads, **about 12%** of the ground. A street that reaches 4 plots either side serves at most 9 plots across, so even parallel streets with no cross street take 11%; joined into one network, about 14% at the least. **Exact plots on a paved city cannot put every building near a road.**
- **Paving frees ground:** since 0.7.70 a Gravel Road paved retires and frees "the 200,000 sq ft between the two" (`GameVersion` 0.7.70, RoadCheck 461). On the map, a street paved would get shorter. Streets drawn by need (§2) keep it where it was.

---

## 2. The roads

### 2.1 Streets by need, road by surface
Two questions, answered by two different things:
- **Where streets run** is the map's: the street network the buildings need, one piece, + junctions on a grid, every building within reach.
- **What each street is** is the model's: its Gravel Roads, Paved Roads and Elevated Highways, drawn exactly, as the surface of those streets: paved or gravel, half or full width, or a track where the city has paid for none.

A street's plot row is its **right of way**: 30 m, drawn as verge with the surface down its middle. The surface is counted in plots: a full-width street takes one plot of road a plot of street, a half-width (15 m) one takes half. Over a district, **the surface drawn equals the road plots dealt to it, kind by kind** (in the prototype the two match to within half a plot). Highways are drawn by length, their plots exactly.

### 2.2 Four levels
| Level | Where | Junctions | Paid by |
|---|---|---|---|
| **Highways** | corridors through the city (§2.7) | interchanges where corridors cross; ramps at every other arterial | Elevated Highways, plot for plot |
| **Arterials** | the west and north edges of every open cell: every 32 plots (960 m) | + at every cell corner | surface: paved first |
| **Streets** | inside a cell, by its layout (§2.3) | + every 8 plots one way, 16 the other (homes); T at a spine (estates) | surface: paved near the hub, gravel out |
| **Tracks** | streets the surface does not reach | as the street | nothing: the model's base streets (`BASE_CAPACITY`) |

### 2.3 Cells
**A cell is a tile** (32 × 32 plots, 960 m): its west column and north row are its arterials, its interior is 31 × 31. Cells nest in districts (8 × 8), so a district's plan is whole. The plan opens cells, and each open cell gets one layout:

- **Homes cell** (homes, shops, offices, schools, health, safety). **Long blocks** of 15 × 7 plots: streets every 8 plots across the blocks and every 16 along them, the direction by the cell's own hash, so neighbouring cells turn. Every plot of a 7-plot block is within 4 of a street (REACH 4, today's). A General Hospital (7 × 7) fits a block. A building wider than one (a University 8 × 8, a Regional Medical Centre 12 × 12, a Penitentiary 12 × 11) **merges two or four blocks** across the street between them: 15 × 15, 15 × 31 or the whole 31 × 31.
- **Estate cell** (industry; and the outer kinds: farms, utilities, plants, rail). **One spine street** across the middle and **two strips** of 15 × 31; the big buildings packed side by side along a strip, each touching the spine or an arterial; small works in the gaps within reach. A building over 15 plots (a Grain Farm 16 × 15, a Livestock Farm 23 × 23, a Rail Terminal 24 × 24) takes the whole cell, the spine closed.

Measured on the test district: a homes cell's streets are 18% of its ground, an estate cell's 9%; over the district with Jerus's mix, **15.8%** of the ground the buildings and streets stand on.

### 2.4 Opening cells: one network
- **Growth order.** A district's cells are ranked outward from its **hub**: its cell nearest the founding site (on another landmass, nearest that landmass's first port). Each next cell is the nearest to the hub that touches one already open (a hashed nudge keeps the edge ragged), so the open cells are always one piece.
- **Bands.** Cells are taken in that order by **band**: first what follows people (homes cells), then industry, then the outer kinds (estate cells). A city reads as town, then works, then fields and plants.
- **Mixed.** Within the homes band each type is dealt round the band's cells in turn, from a hashed start a type, so a cell holds homes, shops, a school and a clinic rather than one kind.
- **Across districts.** An arterial row on a district's edge is drawn when the cell on either side is open, so neighbouring districts join. A district whose first cell does not touch an open cell of its inner neighbour opens an arterial run to it along the lattice (the prototype's join step; on the test district it was never needed).
- **Water.** A street bridges fresh water up to 6 plots, an arterial up to 14 (today's `STREET_BRIDGE` and the highway's `MAX_BRIDGE`); wider water is a landmass's edge (§3).
- **Where the ground cuts a cell** (H2: streets, like highways, favour the grid but need not keep it). A cell cut by water, protected forest or the city's edge keeps its lattice where its ground is whole. Along the cut its streets follow the ground: a street along the shore or the city's edge, curving with it. A street the cut would leave as a dead end is joined to its neighbour in a T or a loop. A curve is drawn smooth; in plots it is a staircase. The + floor of 8 still holds.
- **Checked after every plan** (prototype): one network; the nearest + junction to any other along a street 8 plots or more; no building beyond reach; no building on a street; surface = road plots by kind. Added for N4: no building plot touches a highway plot (H5).

### 2.5 The surface: the model's road, exactly
Over a district, in order (arterials first, then streets by their cell's rank from the hub):
1. **Every street at half width** (15 m), paved while the paved plots last, then gravel.
2. **What is left widens them to full width** (30 m), the same order.
3. **Still more (a road-rich city):** the homes cells nearest the hub change from long blocks to **square blocks** (the + grid at 8), one cell for about 56 plots; then the arterials nearest the hub become **boulevards** (two plots), about 100 plots a cell. A step is not taken if it would leave a building without a place.
4. **Short of road:** the streets the surface does not reach, from the outside in, are **tracks**: a dashed brown line on the verge.

| On the test district, 3,617 buildings | Jerus's roads | the same trips paved | half that |
|---|---|---|---|
| road plots (surface) | 9,388 (8,408 gravel, 981 paved) | 4,484 paved | 2,242 paved |
| cells (homes) | 49 (15) | 48 (13) | 48 (13) |
| streets, plots | 9,802 | 6,052 | 6,052 |
| square-block cells / boulevard cells | 13 / 26 | 0 / 0 | 0 / 0 |
| full / half width / track, plots | 8,974 / 828 / 0 | 2,915 / 3,137 / 0 | 0 / 4,483 / 1,569 |
| one network; + floor; out of reach | 1; 8; 0 | 1; 8; 0 | 1; 8; 0 |

So Jerus's gravel city draws a dense + grid with boulevards at its heart; the paved one draws narrow paved streets on long blocks; a city short of road shows tracks at its edge. All three draw every building and every road plot it has.

### 2.6 Buildings
- **One for one, on their own land, as today** (`BuildingVisual.footprint()`).
- **In their band's cells, largest first**, each at the first spot it fits, touching a street first, then within 4 plots; shops and offices prefer blocks on an arterial, homes the quiet streets.
- **Mines and wells** stand on their sites, as now; a resource's sites are fields, built on last.
- **No building beside a highway** (H5; Samuel, 2026-10-09: "buildings cannot be adjacent to highway"): no building plot touches a highway plot, on a side or at a corner. See §2.7.
- **No building on a street, on the edge of a tile for want of a road, or out of reach.** The edge ring goes: arterials run along tile edges, and a street crosses a tile edge wherever its line does.
- **Forest under a building is cleared** on the map (the zoning study's Z7).
- **A district fuller than its plan holds** (★R7): the buildings that do not fit go to the next district with room, as `CityMap` already moves a type's change; a city with no room anywhere packs them at its edge, without a street, and the map's legend says how many ("12 buildings packed without a street: the city is short of ground for streets"). How full a district the plan can draw (§2.10) is the reason for ★M.

### 2.7 Highways
**Corridors.** A highway rides a street line through the middle of a cell (16 plots in), so it never takes an arterial's row:
1. **The spine:** the founding site's row, both ways.
2. When both of its arms stand at the city's edge, **the site's column**, both ways: the one interchange at the site.
3. Then **parallel corridors one district (256 plots, 7.68 km) apart**, nearest the site first, each starting where it crosses a highway already laid on the city's ground (an interchange).

**Growth.** Each month's new highway plots go to the shortest arm of the earliest corridor that can still grow. They are **laid as runs and saved in the map's sidecar**, so a highway never moves. Demolition takes from the newest end.

**Straight by preference, not by rule** (H2; Samuel, 2026-10-09: "it doesnt have to be straight forever, its just heavily favours being straight and few junctions"). Each step an arm takes has a cost: straight on, 1; a 45-degree turn, about 40 plots of straight; a junction, about 200 (est., dials to tune). The arm takes the cheapest way that keeps it on the city's ground and off water it cannot bridge:
- with the way clear, it goes straight;
- at the sea, or water wider than 14 plots, it turns and curves along the shore, back to its heading where the way is clear;
- at the city's edge it waits, unless the city's ground runs on beside it (a city along a coast or a valley), where it may curve to follow it;
- a curve is drawn smooth.

The prototype's version (45 degrees only, turning only at the sea, waiting at every edge) is the stiffest case of this rule.

**Nothing beside it** (H5; Samuel, 2026-10-09: "buildings cannot be adjacent to highway"). A highway's corridor is three plots wide: the highway, and one plot of verge each side, kept clear of buildings, corners included. A verge may carry a street (a frontage road where a cell's layout puts one there) or stay grass; streets crossing beneath and ramps are not buildings. The plan lays the highway runs before it opens cells, so a cell beside a highway places its buildings off the verge; a building that would touch one moves along, or to the next cell, as any building that does not fit. The verges are street ground, not lots (★M). Jerus's 1.2 km of highway takes about 80 plots of verge.

**Junctions.** An arm meeting a highway running the other way crosses it: an interchange. Meeting one running its own way, it joins it and stops. A corridor may also begin or end in a T on another highway where that is cheaper than crossing; junctions stay few because each costs about 200 plots of straight. Highways are elevated: streets and arterials pass beneath with no junction; **ramps** where an arterial crosses, every other one (1.92 km), and at an arm's end.

**Measured** (the prototype, a coast with a bay, the city's ground growing round the site):

| Highways | km | City's radius | Corridors | Interchanges |
|---|---|---|---|---|
| 6 (Jerus's) | 1.2 | 4 km | 1 (the spine's row) | 0 |
| 92 | 18 | 12 km | 1 | 0 |
| 400 | 78 | 22 km | 2 (the row's east arm runs 45 degrees along the bay) | 1 |
| 1,385 | 270 | 34 km | 7 | 11 |

### 2.8 Rail, pipelines and what else is coming
- **Rail** (N3's rules stay): its own corridors on the street line a quarter in (8 plots), so it never shares a row with a highway; laid as runs from the rail terminals, which sit in estate cells nearest the mines; level crossings with streets, bridges over highways.
- **Pipelines** (the oil study): drawn over their corridors as a dashed line; buried, so they take no plots.
- **The refinery as a campus** (oil Q11): an estate cell whose strips hold the units, a service street between them.
- **Ports and tank farms:** estate cells on the shore, the berths over owned sea (§3, §4).
- **Offshore platforms:** on their sea sites, as wells on theirs; no street.
- **Zoning** (Z1: zones are amounts): a zoned sector's buildings fill cells next to each other in their band; the map tints those cells.
- **The forest law:** protected and working forest takes no buildings and opens no cells; open forest is cleared under buildings.
- **The Earth:** real coasts and rivers come in through the ground the plan reads; nothing else changes. Ice is never opened.
- **Buying from the map** (M6): unchanged; a purchase repaints as today.

### 2.9 Where it lives, and what it costs
- **`DistrictPlan`** (new, pure): from a district's ground, ownership, building counts, road plots by kind, and the city's highway and rail runs, it gives the cells, their layouts, the streets with kind and width, and every building's box. Same inputs, same plan, on any machine (hashes, no clocks).
- **Kept** like today's `RoadTiles` and `Dealt` (64 districts, least recently used), made again when its inputs change, off the interface thread like the map's snapshot.
- **`TilePainter`** paints a tile from its district's plan: no deal, no road tiles, no lanes, no fill, no edge ring.
- **The highway and rail runs** are city-wide state in the sidecar (FORMAT 5): a few hundred runs.
- **Cost:** the Python prototype takes 1 to 5 s a district; the Java should be well under 100 ms (est.), but it must be measured against MapCheck's 80 ms screen.
- **Stability:** as today, one more building moves only those placed after it in its district; a step of the surface ladder (§2.5) relays the cells it changes.

### 2.10 How full a district the plan can draw
On flat ground, every plot dry and owned (the prototype, 64 cells):

| Model's fill of the dry ground | Jerus's city | the same city paved |
|---|---|---|
| 75% | all placed | all placed |
| 80% | all placed | 3.5% of building ground has no place |
| 85% | all placed | 9.0% |
| 92% (Jerus's own) | 4.9% | 16.7% |

The plan draws at most about **78 to 80%** of a district's dry ground as buildings and streets; the rest is arterials' corners, the gaps between buildings, and blocks' odd ends. On the test district's ground (a river, a bay, a corner not bought) Jerus's mix loses 3.2% at 85% and 8.6% at 92%. Real cities sit in that range: streets take 25 to 30% of land and lots less than three quarters [U1]. **The model lets a city cover 100% of its dry ground.** Hence ★M.

### ★M. Lots and streets in the model (decided: yes)
A city's dry ground would be two pools:
- **street ground:** the share the plan's streets take (measured: homes cells 18%, estate cells 9% of their ground);
- **lots:** the rest; buildings take lots.

The road buildings stand on street ground; past it, their ground comes out of lots (wider roads). The investors' "free ground" becomes free lots. Effects:
- **Jerus's city (gravel):** about none, because his road already takes more ground than his streets need;
- **a paved city:** about 4% more ground for the same buildings (est.: streets 15.8% of the ground against its road's 12.2%);
- **paving** keeps its street's ground (the 200,000 sq ft are no longer freed);
- **every trace moves.**

Samuel, 2026-10-09: "yes you can widen the space if needed". Its own batch after the map batches, once the Java plan has measured the split on real saves. Until it ships, the map's fallback (§2.6) draws what does not fit packed at the edge.

---

## 3. Islands

### 3.1 Landmasses
- **A landmass** is dry ground the sea cuts off. Fresh water cuts it too where it is wider than a highway bridge (14 plots, 420 m). Narrower water is bridged by arterials.
- **A Highway Bridge** (I4; Samuel, 2026-10-09: highway only, fresh water only) spans a river or lake wider than that. It is a highway building, priced by length (to research in its batch), that must join the highway network on both banks, and it links the landmasses either side. The sea is crossed by ports only.
- **Home** is the founding site's landmass.
- **Where it comes from:** the Earth pack gets a **landmass layer**: each land cell's component at 15″, found offline by the pack tool after taking out HydroLAKES lakes and GRWL rivers 420 m wide or more (E1). A plot takes its data cell's landmass. A plot made dry by the seed's fraying, whose cell is sea, takes the nearest land cell's, so no islet of a few plots becomes a landmass. Size: an id a run of the coast mask, about 1 to 2 MB (est.).

### 3.2 The rule: two ports for the first island
- **Linked:** home always; any other landmass once **it has a port** and **a linked landmass has a port** (with a sea route between them, §4), or once **a Highway Bridge** joins it to a linked landmass. So the first island takes two ports (one at home, one there), and each island after it one more.
- **Unlinked ground** is bought, owned, counted and priced as today, and nothing can be built on it **except a port**.
- **A port** needs owned dry shore and the owned sea in front of it, on the landmass it serves.
- **"A port at least two":** a port at home and one on the island, as Samuel confirmed (I2).

### 3.3 What the model keeps
- **The books by landmass:** a holding's five areas (exact, plot by plot, as today) also split its dry plots by landmass. Most holdings have one; a coastal offer two.
- **`Landmasses`** (new, saved): each landmass the city owns ground on: its id, a name (from the pack's nearest place name, else "Island 2"), dry ground owned, ground used, its ports by kind, linked or not.
- **Free ground** for the investors, the automatic builder and the test player is the free ground on **linked** landmasses only.
- **Where a building goes:** home while it has room; then the linked landmass with the most free ground. The map places it in that landmass's districts. Removals go in reverse.
- **Later, the crossing (★I6):** people and goods crossing between landmasses are a load on the ports, the way trips are a load on the roads: an island's residents work off it in proportion to the jobs elsewhere, and its workers live off it in proportion to the homes elsewhere. Ferry berths carry people, cargo berths goods; past their capacity the crossing slows like a road. A mechanism, not a factor.

### 3.4 Prototype
A coast, the city's ground round its site, and two islands: **Home** 263.2 km² owned; **Isle A** 49.5 km² (all of it); **Isle B** 12.0 km² of its 18.6. With a port at home and one on Isle A, Isle A is linked and Isle B is not: its ground is hatched, and only a port can go there.

---

## 4. Ships and routes

### 4.1 Routes
- **Found once** on a grid of the sea at 16 plots (480 m), with A* over sea cells, three times the cost one cell from the shore (ships keep off it), then pulled straight (a point kept only where the straight line to the next would cross land).
- **Kept, not saved:** made again when ports or land change; a few dozen per city.
- **The city's own ports** are joined directly: ferries and coastal cargo. In the prototype the ferry home → Isle A is one straight leg of 4.8 km; the cargo route to a wharf on Isle A's far side goes round it in three legs, 16.4 km.
- **World trade** sails from its berth to **the offing**: the point, the city's radius plus 10 km out, on the bearing with the longest run of open sea from the port (rays every 5 degrees). Then it goes on along that bearing **into the abyss**, fading over a few km. In the prototype: 19.4 km to the offing, bearing 40 degrees. A call's line is spread a few degrees by its own hash, so boats do not queue on one line.
- **Water bodies:** two ports link only if a route joins them. On the Earth, the pack's sea components say so at once (the Caspian is its own).
- **Shallow water:** a berth's class is the largest ship its depth allows (`World.depthAt()`, 0.7.79), as the oil study set.

### 4.2 Boats
- **Calls** (the oil study's §4.3): tonnes a month ÷ one ship's cargo, by ship class, for world trade and for cargo between the city's own ports. A call is a schedule entry: route, class, direction, and an arrival hashed in the month.
- **Ferries are fleets, not calls.** Fleet = sailings needed at the peak hour × the round trip in hours. Example (est.): a town of 20,000 on Isle A with 9,000 workers and 3,000 jobs there; 6,000 cross each way a day, 60% in two peak hours = 1,800 an hour; ferries of 1,000 people, 4.8 km at 15 knots, a 40-minute round trip → 2 ferries.
- **Drawn only on screen:** a boat's place is a pure function of the clock; the map asks which routes cross the view and draws those boats. A city with no sea trade has no boats.
- **The clock:** one month = 60 s for calls (the oil study's Q8). Ferries shuttle at a fixed visual pace, a crossing about 8 s, since 60 s a month would make them blur.
- **Colours** (the oil study's): imports arriving full blue, exports orange, an empty leg grey, ferries white.

---

## 5. The screens
Board 9 of the artifact draws these.
- **The map:** streets as verge and surface (gravel tan, paved grey, a track brown and dashed); arterials and boulevards; highways dark with a dashed centre and ramps. A hover on a street names it: "Paved street, 15 m · arterial", or "Track: the city has bought no road here". Unlinked ground is hatched amber, with a chip on its island: "Isle B · 12.0 km² · not linked: needs a port here (home has one)".
- **The legend** adds Tracks and, when it applies, "n buildings packed without a street".
- **Land office and the map's offer card:** an offer on an unlinked landmass is tagged ISLAND, with a line: "On Isle B, which no port links: only a port can be built there until it has one".
- **Land tab, a LANDMASSES table:** name, dry ground owned, free, ports, linked; later the crossing against its ports' capacity.
- **Build › Port:** the card asks where (each landmass with owned shore and sea) and says what it links ("Isle B becomes the city's 3rd linked landmass").
- **The investors' words:** "no land: 12.0 km² is free on Isle B, which no port links".
- **Inbox:** a purchase of ground on an unlinked landmass gives a notice with "Build a port there ›".

---

## 6. Batches
| Batch | What | Traces |
|---|---|---|
| **N4** | `DistrictPlan`, pure: cells, bands, growth order, layouts, the surface ladder, placement, the join; PlanCheck asserts §2.4's checks on Jerus's fixture, the playtest's months and the test ground | identical |
| **N5** | The painter on the plan: road tiles, lanes, fill and the edge ring go; the overflow rule; the sidecar FORMAT 5; MapCheck rewritten (+ floor, one network, reach, surface = road plots by kind, buildings one for one, the 80 ms screen); hover and legend words | identical |
| **N6** | Highways and rail on corridors: runs in the sidecar, the cost rule (straight by preference), interchanges, T's and ramps, the verges (H5); MapCheck 8 rewritten | identical |
| **M1** (★M) | Lots and streets in the model; paving keeps its ground | every trace moves |
| **I1** (with the Earth's E1 to E3) | The landmass layer, books by landmass, `Landmasses`, the linked rule, free ground on linked landmasses, the words, the map's hatching, the office's tags, the inbox | moves only where a city owns an island |
| **I2** (with the ports run) | Ports and a Ferry Terminal built on a chosen landmass's shore; linking; the Build card's chooser | moves |
| **B1** (I4, with I1) | The Highway Bridge: a highway building over fresh water wider than 14 plots, joined to the highway network on both banks, linking the landmasses either side | moves where one is built |
| **S1** | Routes and boats: the route finder, the offing, calls and ferry fleets, drawing | identical |
| **I3** (★I6) | The crossing as a load on the ports; coastal shipping between the city's own ports (oil Q17) | moves |

N4 to N6 and S1 are the map's alone ("nothing in the model reads it", `CityMap`'s banner) and can go in any order after N4.

---

## 7. ★ Decisions (Samuel, 2026-10-09)

"all your recomendations are good", with H2, I2, M and I4 as he set them below. Every other item is decided as recommended.
**Roads**
1. **R1. Streets by need, road by surface** (§2.1): the map lays the streets the buildings need, and draws the model's road plots exactly as their surface. **Recommend yes.** The alternative, road plots one for one, cannot reach every building in a paved city (§1.3).
2. **R2. Cells:** a tile becomes a cell with arterials on its west and north edges; homes cells of long blocks (+ every 8 and 16), estate cells with a spine. **Recommend yes.**
3. **R3. The surface ladder:** half width, then full, then square blocks near the hub, then boulevards; short of road, tracks from the outside in. **Recommend yes.**
4. **R4. Tracks** for streets the city has bought no road for, as the model's base streets. **Recommend yes**, so a city short of road shows it. Alternative: draw them as half-width streets anyway and say so only in the hover.
5. **R5. Bands:** homes nearest the hub, then industry, then the outer kinds, in each district. **Recommend yes.**
6. **R6. Mixed homes cells:** each type dealt round the cells in turn. **Recommend yes.**
7. **R7. Overflow:** buildings a district cannot hold go to the next with room; a city with none packs them at its edge, counted in the legend. **Recommend yes.**
8. **M. Lots and streets in the model** (§2.10): **yes** ("you can widen the space if needed"), as its own batch after N4 to N6, once the Java plan has measured the split on real saves. Every trace moves.

**Highways**
9. **H1. One corridor at a time:** the site's row, its column, then corridors one district apart. **Recommend yes.**
10. **H2. Straight and few junctions heavily favoured, not forced:** "the highways can be just curves or just T junctions it doesnt have to be straight forever, its just heavily favours being straight and few junctions but it doesnt have to be, same with roads". Highways and streets take the cheapest way, straight by far the cheapest; curves along shores and the city's edge, T's where cheaper than crossing (§2.4, §2.7).
11. **H3. Elevated:** streets pass beneath; ramps only at every other arterial and at the ends. **Recommend yes.**
12. **H5. No building beside a highway** (Samuel's rule): a plot of verge each side, corners included; a verge may carry a frontage street (§2.7).
13. **H4. Runs saved, never moved.** **Recommend yes.**

**Islands**
14. **I1. A landmass** is ground the sea cuts off, and fresh water wider than 420 m. **Recommend yes.**
15. **I2. Linked** = a port on it and a port on a linked landmass: two for the first island, one for each after. **Yes** (the reading was right).
16. **I3. Unlinked ground:** owned and counted; nothing built but a port. **Recommend yes.**
17. **I4. A Highway Bridge:** highway only, on fresh water only (rivers and lakes), spanning water wider than 14 plots and linking the ground either side; the sea takes ports. Priced by length, researched in its batch (B1).
18. **I5. Home first,** then the linked landmass with the most free ground. **Recommend yes.**
19. **I6. The crossing as a load on the ports** (ferries for people, cargo berths for goods). **Recommend yes, as the batch after the ports.**
20. **I7. The landmass layer goes into the Earth pack** (E1). **Recommend yes.**

**Ships**
21. **S1. Routes** on a 480 m sea grid, pulled straight, kept and not saved. **Recommend yes.**
22. **S2. The offing:** world trade sails out on the most open bearing and fades into the abyss. **Recommend yes.**
23. **S3. Ferries as shuttle fleets** sized from the people crossing. **Recommend yes.**
24. **S4. The clock:** 60 s a month for calls; ferries at their own pace. **Recommend yes.**

---

## 8. Found on the way
1. **Paving shortens a street on today's map:** it frees 200,000 sq ft (0.7.70). Under R1 the street stays and only its surface changes; ★M would keep the ground too.
2. **The highway is tiny:** Jerus's 6 Elevated Highways are 1.2 km. Drawn as one straight line, that reads as a start, not a stub.
3. **`TilePainter`'s "ports"** are road crossings on a tile edge (`PORTS_MOST`, `ports()`). The word goes with them in N5, before sea ports arrive.
4. **`CityMap`'s deal is gone in N5:** the plan places each building at a box. `MapCheck 2`'s "one more house moves one tile's count by one" becomes "one more house moves only buildings placed after it in its district".
5. **Arterials sit on tile edges, so a tile's west column and north row are road wherever the cell is open.** The old ring rule existed only because tiles grew their own roads; the plan is district-wide.
6. **Forest under buildings** is drawn as forest today; the map should clear it, as the zoning study decided for the model.
7. **The screen's 80 ms is unmeasured for the plan:** the prototype is Python. N4 must time a district plan in Java before N5 paints from it.

---

## Sources
- [U1] UN-Habitat, *A New Strategy of Sustainable Neighbourhood Planning: Five principles* (2014), principle 1: at least 30% of land in streets and at least 18 km of street per km². https://unhabitat.org/sites/default/files/download-manager-files/A%20New%20Strategy%20of%20Sustainable%20Neighbourhood%20Planning%20Five%20principles.pdf
- Ship classes, cargo per call and the call rule: `oil-and-ports-research.md` §4 and its sources [P].
- The game's figures: `buildings.json`, `CityMap`, `TilePainter`, `BuildingVisual`, `MapCheck`, `GameVersion` at 0.7.79 (commit f85bcfa).

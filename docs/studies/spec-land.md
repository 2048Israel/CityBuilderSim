# The land, the water, the world's deposits, the painted map and fuel: the design, 2026-10-06

Read-only study of 0.7.54 (`/home/claude/cbs-0754`), built on batch I's land premium (crowding × the world's USD price level).

**Prototypes,** in `/home/claude/scratch-land/`:
- `proto/WorldProto` — the world;
- `TerrainBench`, `CalBench` and `NodeBench` — timings;
- `LandProto` — the centre, lanes and offers;
- `MapProto` — the districts, deal, painter and raster;
- `probe/LandProbe` — the 0.7.54 model on the three research cities.

Their outputs are in `out-*.txt`, and `preview-15.png` shows 6 × 6 painted tiles.

**Figures.** Every figure is one of three kinds:
- measured in the prototypes (timings are steady state after warm-up, on 2 cores);
- derived, with the sum shown;
- a design constant, with its reason.

## 1. Summary for Jerus

1. **The world.** A flat square the size of the Earth (511.2M km²), made from one seed. The city sits at its middle, on a coast, with a lake and a river beside it.
2. **Resources.** Seven resources lie in real fields, each with a size and an amount in tonnes. The world's totals are counted once and kept to the tonne as the city buys and digs.
3. **Lanes.** Each side (N, E, S, W) is split into 10 wedges fanning out from the founding site. Each wedge has one offer: the next band of real ground. So there are 10 offers a side. They are never rerolled, and buying one moves that lane's edge out and lists the next band.
4. **Offer size.** An offer is 1 to 18 times one block or 1% of the city, whichever is larger. A new city sees today's listing, and reaching 10B takes about 410 to 440 purchases.
5. **Prices.** Batch I's ground price for dry land, fresh water at 45% of it and sea at 8% (the mockup's figures), and iron and oil at world prices. The other five resources are free until their industries exist.
6. **Fresh water.** It yields 121,600 units a month per km²: the world's river flow per km² of river. So one water plant needs 0.49 km² of lake or river. The old wells' 8,000 units stay outside the limit, and old cities keep what they already pump.
7. **Desalination.** It needs owned sea, makes what a water plant makes, costs twice as much and draws 10,880 kW (3.5 kWh a m³).
8. **The map.** Stored as 7.7 km districts in a sidecar file of 0.8 to 9.2 MB at 10B, and painted tile by tile with your mockup's rules. Adding a building moves no other in the median case. A new tile costs 0.19 ms, and the cost follows the screen, never the population.
9. **Fuel.** Oil wells (a new Oil sector) feed a refinery (a new Refining sector). It replaces the fuel your drivers and railway import: US$27M a month in your city today, about 15M litres.
10. **Old saves.** Their land becomes the centre, a square holding the same dry ground. The world is generated around it from a seed taken from the save, and the mines keep exactly the ore the save records.
11. **Batches.** Six of them: J1a world, J1b land, J2 water, J3 map data, J4 map and land office, K fuel. The traces move only at J1b, J2 and K.

## 2. The design

### 2.1 The world
**The grid ★4.** Four sizes, each nested in the next by a power of two:

| unit | plots | size |
|---|---|---|
| plot | 1 | 30 m |
| tile | 32 | 0.96 km |
| district | 256 | 7.68 km, 58.98 km² |
| world cell | 2,048 | 61.44 km, 3,774.9 km² |

The world is 368 × 368 cells: 511.2M km², within 0.2% of the Earth's 510.1M. Plot coordinates are an `int` per axis, 0 to 753,663, with the city at the middle; a plot index is a `long`. Every value comes from `mix()` (SplitMix64, as in `LandMarket.scramble`) of the seed, the field, the octave and the lattice point. Java's floating point is strict, so it is the same on every machine. Finer where it matters: the cells carry the totals and the far view, while near the city every function is evaluated per 30 m plot and fields are generated only for the cells under the view or an offer.

**Terrain.** Value noise with smoothstep, each octave's amplitude 0.55 times the one above.
- **Elevation:** 0.85 × octaves 2^16 to 2^9 plots (1,966 to 15 km), plus 0.15 × octaves 2^8 to 2^4. Below θ_sea is sea; θ_sea is the 71st percentile of 4 samples per cell, so the sea is 71% of the world as on Earth. Measured: land is 29.0%, and θ_sea varies from 0.546 to 0.571 over 5 seeds, so each world computes its own.
- **Lakes:** octaves 2^10 to 2^5, fresh from the constant 0.695 upward. That is the 96.3rd percentile of land samples, because lakes cover 3.7% of non-glaciated land (Verpoorter 2014). Measured 0.6950 to 0.6961.
- **Forest:** octaves 2^8 to 2^5, from 0.595 upward. That is the 69th percentile, because forest covers 31% of land (FAO 2020). Measured 0.5942 to 0.5959.
- **Beach:** land within 0.0015 of θ_sea.
- **The founding river:** the mockup's random walk. It starts at a lake 64 to 90 plots away, opposite the sea, passes 8 to 20 plots from the site, and moves 3 plots a step; its half-width grows from 1 to 1.95 plots. It is rebuilt from the seed at load in 0.3 to 0.6 ms. Elsewhere, fresh water is lakes.

**The founding site.** Take the cells that are 25 to 75% land, start at the nearest to the middle, and spiral out from it at 17 plots. The first point that passes all of these becomes the site:
- dry for 600 m round;
- at least 60% land in 48 samples within 5 km;
- sea within 2.1 km but not within 1.35 km;
- ★ an iron field within 2 km, as the mockup put deposits near the site "so the first mines come early". This passes 92% of the time at the density below.

Measured over 3 seeds: 10 to 200 ms, after 2,147 to 6,936 checks.

**Terrain costs.**
- A tile: 65 to 70 µs. Each octave's lattice is computed once per tile, and the coarse octaves are interpolated bilinearly between its corners. It agrees with the point function on 99.9 to 100% of plots.
- The point function: 650 to 930 ns a plot.
- A 256-pixel far-view node image: 13 to 30 ms. That is before the integer-step rewrite that made a tile 5 times cheaper.

**Deposits** (`Resource`, 7 kinds).
- **Fields:** a Poisson count per world cell, λ × the cell's land km².
- **Sites per field:** ⌊(1−u)^(−1/1.5)⌋, capped at 512. That gives "most small, a few huge", with a mean of 2.524 (measured).
- **Tonnes:** a cell's total is drawn without drawing any field (count × mean × a richness of 0.6 to 1.4). Its fields come lazily from the cell's own stream and share that total by sites, the last one taking the remainder, so they sum exactly (worst measured error: 0).
- **Placement:** a field centred in the sea is redrawn up to 8 times. Oil keeps its first place, because offshore oil is real.

| resource | fields per km² of land | site km² | tonnes a site | map cover | reason |
|---|---|---|---|---|---|
| iron ore ★5 | 0.2 | 0.03716, an Iron Mine's land | 15 Mt | 1.88% | 7.59 Mt per km² of land (measured), the same as today's listing at a 10-block floor (7.39 Mt, simulated) |
| oil | 0.02 | 0.04047, 10-acre spacing | 150,000 t | 0.20% | 30 years of a 100 bbl/day well (415 t a month). Your 90 km² would hold about 2 fields |
| stone | 0.01 | 0.1 | 13.5 Mt | 0.25% | a quarry 50 m deep at 2.7 t/m³ |
| coal | 0.002 | 1.0 | 6.5 Mt | 0.50% | a seam 5 m thick at 1.3 t/m³ |
| copper | 0.0005 | 0.5 | 300,000 t of metal | 0.06% | 120 Mt of ore per km² at 0.5% |
| uranium | 0.0002 | 0.1 | 200 t | 0.005% | 1 Mt of ore per km² at 0.2% |
| forest | the forest class | – | 13,700 m³ a km² | 31% | FAO 2020: 557 bn m³ on 4.06 bn ha |

**World totals** (measured, seed 4127): iron 1.13e15 t, oil 1.12e12, stone 5.05e13, coal 4.86e12, copper 5.6e10, uranium 1.5e7. The pass takes 29 to 30 ms over 135,424 cells × 6 kinds. Forest's total is its area × 13,700 m³ a km². Only θ_sea and the 7 totals are stored, at founding, so loading needs no world pass; the harness recomputes them to check.

**Conservation.** For each kind:

W = unowned + the city's remaining + the city's extracted

- The city's initial amount O is the centre's plus each purchase's, as listed.
- Extracted, E, is stored as one double a kind.
- Unowned is W − O, and remaining is O − E.

**Depletion** runs in acquisition order (★12): the centre first, then each purchase. The map greys a purchase's fields once E has passed them, and half-greys the purchase being worked. Forest's stored depletion falls by 1/240 a month, a 20-year time constant, so 95% grows back within a 60-year rotation. Nothing cuts timber until a timber industry exists, so the harness tests regrowth on a fixture.

### 2.2 The city's land
**The centre** is a square around the founding site holding the dry ground: 3,000,000 sq ft for a new city, or `landOwned` for an old save. It is sized in one pass: dry ground is counted ring by ring (L∞), and the square stops at the ring where the running sum reaches the target.
- New city: 7 ms, half-side 0.28 km, 0.31 km² in all, 10% of it water.
- Jerus's 89.63 km² dry: 560 to 851 ms with the point sampler, 96 to 242 km² in all depending on the coast.

**Lanes ★1.** Every point belongs to one side and one lane, seen from the site:
- side: N, E, S or W, by the larger of |x| and |y|;
- radius: r = max(|x|, |y|);
- lane: ⌊(t+1)·5⌋, where t is x/|y| or y/|x|, read across the side.

The city owns the point when r ≤ ρ_centre or r ≤ ρ[side][lane]. A band of a lane from r1 to r2 holds 0.1·(r2² − r1²), so 40 equal lanes make a square with straight frontiers, as in the mockup. Testing ownership costs 47 to 56 ns a plot; a tile wholly inside or outside is tested once.

**Nothing rolls into the centre ★3.** The centre is the land the city was founded or converted with, and it is fixed. A side is just its 10 lanes, each holding its purchases in order.

**Offers.** One per lane, ten a side. Each is the next band of its lane, from r1 (the lane's frontier) to r2 = √(r1² + 10·a).
- **Size ★2:** a = m × max(1 block, 1% of the city's whole area). The multiple m is drawn as today's listing draws it: 55% between 1 and 2.5, 30% between 2.5 and 5, 15% between 5 and 18; mean 3.81. A new city's unit is a block, so it sees today's listing; past 0.929 km² the unit is 1% of the city. Measured: offers average 8.9 to 11.0% of a new city and 3.0 to 3.7% of a large one.
- **Contents,** fixed at listing: its km², and the km² that are dry, fresh, sea and forest, from 64 × 64 area-uniform samples; per resource, the sites and tonnes of every field centred in it. A field belongs whole to the band that holds its centre (★12). The 40 offers took 106 to 234 ms with the point sampler.
- **Buying:** buying lane l of side s sets ρ[s][l] to that offer's r2, records the purchase, and lists the lane's next band. The other 39 do not move. Offer ids only grow.
- **Mostly sea:** an offer over 70% sea stays listed, because sea is cheap, but it is tagged "mostly sea" and never counts as "best". At Jerus's size, seed 4127 (a peninsula) had 30 such offers of 40; seed 77 had none.

**Price ★6.** In US$, fixed at listing, rounded to US$5k as today:

price = ground$ × (dry + 0.45·fresh + 0.08·sea) × 10,763,910 + Σ tonnes × world export price × in-ground share × the world price level

- ground$ is batch I's `groundUsdPerSqFt`, and 10,763,910 is the square feet in a km².
- Iron's in-ground share is 1/350: today's $0.40 a tonne against the world floor of $140.
- Oil's is 5%, by iron's own rule of "a year and a half of a working site's profit". A well clears about US$210k a month (415 t × US$500, less staff and power). Eighteen months of that is US$3.8M a site, or US$25 a tonne, which is 5% of US$500.
- A kind with no `Good` yet is priced at 0.

**Area in km² everywhere** (`LandManager.km2Words`). The city's size, "City 241.6 km² · 89.6 dry", heads the land office and NEEDS YOU's GROUND row.

### 2.3 Water
**Fresh yield ★7:** 121,600 units a month per km² of owned lake or river. Derived as the world's renewable river runoff (about 42,700 km³ a year, Shiklomanov) over the world's river and stream area (773,000 km², Allen & Pavelsky 2018): 5.524e7 m³ a km² a year, or 4.603e6 m³ a month, or 121,600 units of 37.854 m³.

So one Water Treatment Plant (60,000 units) needs 0.49 km². Jerus's measured demand of 237,857 units needs 1.96 km², which is 2.2% of his land. The squares near the site measured 0.9 to 4.1% fresh water, so dense cities outgrow their fresh water and have to buy lakes or desalinate.

**How plants draw:**
- fresh cap = 121,600 × owned fresh km² + `freshRights`;
- production = (8,000 + desal nameplate) × fill + min(fresh nameplate × fill, cap).

The 8,000 of legacy wells is groundwater, outside the cap; at zero fill production is 8,000, as today. With the cap not binding, this is today's formula exactly. The cap refills each month and nothing is stored.

**Past the limit:** fresh plants run at the cap and the rest of their nameplate idles. Rationing goes through `waterRatio`, unchanged, and throttles commercial and industrial output. The Services page says: "fresh water limit: 62% of the plants' nameplate idle · buy lake or river, or desalinate".

**The Desalination Plant ★8** (id 73, category WATER, `"source": "SEA"`):
- **As the water plant:** output 60,000, staff 29, water 20, land 800,000 sq ft, road load 15.
- **Cost:** cashCost 131,568, materials 4,872, points 88,000 — twice the water plant. Seawater reverse osmosis (SWRO) costs 2 to 5 times a conventional plant; the game's plant is already US$5.6M per MGD, which makes desalination US$11M per MGD.
- **Electricity: 10,880 kW.** That is 2,271,246 m³ a month × 3.5 kWh/m³ (the middle of SWRO's 3 to 4) ÷ 730.5 h. The water plant itself works out at 0.29 kWh/m³, a real conventional figure, so the ratio is honest. The running cost is the grid it needs: 1.34 wind farms, or 3.9% of a coal plant.
- **Gate:** owned sea above 0 km². Otherwise the order is refused as `NO_COAST`, with a page like no-deposit's.

**The gauge** is still `getWaterServed()`. `UtilitiesHandler` gains `getFreshCap`, `getFreshNameplate`, `getFreshDrawn`, `getDesalNameplate` and `getDesalOutput`. BuildAdvice values a fresh plant at min(production1, the fresh headroom left), so a capped city is offered desalination; a desalination plant is valued at production1 if the city owns sea, and 0 if not.

### 2.4 Deposits in the economy
**Iron.** `LandManager` keeps its API:
- `getIronDeposits()` returns the owned iron sites (centre + purchases);
- `getIronReserveTonnes()` returns O − E;
- `extractIron(t)` adds to E.

`Mining`, `hasDepositFor` and `minesCommitted` behave as now; in K, `minesCommitted` counts only templates that make IRON. The pool stays: one mine per site, one reserve. On the map, mines stand on the first sites in acquisition order that are not worked out.

**Conversion ★9:**
- The save's `ironDeposits` become the centre's sites, raised to cover the mines standing and on order. This covers mines with no recorded deposits.
- Tonnes are the save's `ironReserveTonnes`, never more. A worked-out city keeps 0 and its mines retire by today's rule.
- The map draws the centre's generated iron fields, scaled to that total. A centre with none gets one legacy field at a seeded dry plot at least 1 km from the site.
- E starts at 0.

The other five resources wait in the world, the offers and the map. Oil comes into use in K.

### 2.5 The map: data
**The sidecar.** One record per district, in `slot-NN-map.bin`, deflated and written through `GameFiles` with its .tmp/.bak swap. Each record holds:
- dx and dy (int16);
- owned dry plots;
- plots used (double);
- iron and oil sites;
- 76 counts by building type (int32).

The header holds the magic, the seed, the month and a stamp, also written to the JSON as `mapStamp`. A missing or mismatched sidecar is rebuilt canonically, and the picture shifts once.

| measured with Jerus's counts | 5B (× 9,814) | 10B (× 19,629) |
|---|---|---|
| districts | 15,129 | 29,929 |
| heap | 5.4 MB | 10.8 MB |
| sidecar, raw / deflated | 4.66 / 0.40 MB | 9.22 / 0.78 MB |
| write / read | 217 / 20 ms | 298 / 28 ms |

A city that grew down its own path falls between the raw and deflated sizes.

**Owned dry plots** are recounted from 32 × 32 samples in every district a purchase touches: about 300 at 10B (17,600 km² ÷ 58.98).

**From counts to districts, stably ★12.**
- **Each month,** once construction completes, `CityMap.reconcile(counts)` places each type's change: new buildings go to the first district with room (owned dry plots less used, at land ÷ 9,687.5 sq ft a plot); districts are tried nearest first; farms, utilities, mines and wells, rail and car plants are tried farthest first; removals go in reverse, and nothing placed ever moves. Measured at 10B, with running totals and a cursor each way: ≤ 0.76 ms a month (3.5 ms for the first call, which sums), and the sums are exact.
- **Canonical allocation,** for an old save or a lost sidecar: each type in proportion in every district, inner first, with the remainder by largest remainder. 48 to 53 ms at 5B and 10B, exact.
- **Roads:** Gravel Road, Paved Road and Elevated Highway counts become each tile's road budget, at 46.5, 25.8 and 6.5 plots each. The mockup's two world highways (mockup ★5) use no budget.
- **Mines and wells** go to the districts holding their sites, in acquisition order.

**The deal** splits a district into its tiles with spec-scale's per-building hash, weighted by each tile's owned dry plots through an alias table; mines and wells are weighted by sites. One more building changes one tile by one, and it costs 40 to 59 µs a district of 9,300 buildings.

**The pyramid** sums 10 visual classes, used plots and owned plots, 2 × 2, built at load: 7 levels and 5,146 nodes at 5B (31 to 42 ms), 8 levels and 10,160 nodes at 10B (41 to 131 ms). When a district changes, its ancestors are updated.

### 2.6 The map: painter, levels of detail, caches, the two views
**The tile painter** (`TilePainter`, pure) takes the seed, the tile, its 1,024 terrain bytes and ownership flags, its counts and road budget, which neighbours have roads, and the fields under it. It applies your mockup's rules, made local to the tile.
- **Ports:** 1 to 3 on each edge shared with a neighbour that has roads, hashed from the shared edge, at least 3 plots from a corner and 5 apart. Measured: 64 of 64 edges agreed, and no road touched a border away from a port.
- **Growth:** ends enter from the ports, or one is seeded inside; paved ends are picked twice as often as gravel; each step runs 3 to 8 plots (paved 4 to 8), stopping at a join, the edge ring, sea, or within 2 plots of a parallel road; then it goes straight 40%, turns 20%, makes a T 28%, or a + 12%; bridges (mockup ★1): paved up to 6 plots, highway up to 14, gravel never, and never over sea; when every end is dead, a T branches off an existing road; growth stops when the budget is spent; the 3 plots ahead of each live end are kept free, so no road is blocked.
- **Buildings ★11:** candidates are owned, dry, and not road, reserved or field; they are ordered by row from the road, then the plot's hash, with forest last (mockup ★10); each building (t, j) takes a hashed preferred place, u^1.5 × the candidate count, and probes forward to the first plot that takes its footprint, in the order of those places.
- **Footprints (mockup ★8):** 1 × 1 for most; industry 2 × 2 (60%) or 3 × 2; schools and utilities 2 × 2; hospitals 3 × 2; farms 3 to 5 a side, on grass only.
- **Look:** home daycare and home care are drawn as homes (★10); flats are the apartment types, drawn darker (mockup ★9, now by type); mines and wells stand on their fields with a gravel spur (mockup ★6); worked-out fields are grey (mockup ★7).

**Measured at Jerus's density** (146 buildings a tile):
- painting takes 95 to 100 µs a tile, drawing 141.3 buildings and dropping none;
- roads: 163 road plots laid of a budget of 205;
- placement: 44% of buildings beside a road, 15% in row 2;
- one more building moves others: median 0, 75th percentile 1, 90th 2, 99th 9, mean 0.71; none at all in 71% of cases;
- one more road building re-lays its tile (73 moved). Roads are 3.3% of buildings, so that happens about once per 30 buildings added.

**Raster** (`TileRaster`) fills an `int[]` for `PixelWriter.setPixels`: 24 to 31 µs at 4 px a plot (128 px), 81 to 134 µs at 8 px, 8 µs at 1 px.

**Levels of detail** (the mockup's thresholds):
- **L0, 3.2 px a plot or more:** painted tiles as images at 4 px (8 px above 6 px a plot), drawn scaled, with outlines from 6 px.
- **L1, 1.4 to 3.2 px:** the painted tile at 1 px a plot, or an 8 × 8 block image from its counts until it is painted.
- **L2, below 1.4 px:** 256-pixel node images. They draw from tile counts at L6–L7 and district counts above, with terrain from the octaves at least a pixel wide, and gravel hidden.

Zoom runs from the whole world (about 25 km a pixel) to 16 px a plot, by 1.25 a notch at the cursor. Drag pans.

**Canvas and caches** (`ui/MapView`, one JavaFX `Canvas`, ≤ 48 MB in all, ★12):

| cache | size |
|---|---|
| terrain per tile | LRU 2,048, 2 MB |
| painted tiles | 1,024, about 6 MB |
| tile images at 4 px | 300 × 64 KB, 19 MB |
| tile images at 8 px | 32 × 256 KB, 8 MB |
| node images | 48 × 256 KB, 12 MB |
| fields | 64 cells |

A district change repaints only the tiles whose counts changed, and a purchase repaints the districts it touched. A cached frame is about 300 `drawImage` calls plus overlays.

New tiles paint in the background on the FX thread, at most 8 ms a frame: about 40 tiles at 0.19 ms each (terrain 68 + paint 97 + raster 25 µs). Meanwhile the next coarser image stands in. A cold L0 screen at 1,389 × 868 is about 200 tiles with a margin: 38 ms, spread over about 5 frames.

**Two views share one `MapView`** and its caches:
- the land office's small map, 600 × 400, showing the whole city with the offers hatched;
- Expand ⤢, filling the content area (about 1,313 × 790), with pan, zoom, a legend and a hover card (terrain, owner or offer, fields and tonnes). Clicking an offer band selects it in the side list, and Esc closes.

Overlays: the centre's outline, each lane's edge, the 40 offer bands, deposit icons at L1 and L2, and a scale bar.

### 2.7 Fuel ★13 (batch K)
**Goods:**
- CRUDE, in tonnes: import 0.60, export 0.50, freight 0.075 (three quarters of the wedge, by `Good`'s rule), stockable. US$550 a tonne is about US$75 a barrel at 7.33 barrels a tonne.
- FUEL, in litres: import = `CAR_FUEL_PER_JOURNEY` ÷ `LITRES_PER_JOURNEY` = 0.0016667 (1.2 L a journey: 15 km at 8 L/100 km, Motoring's own figures). Export 0.0007 (wholesale at US$75 crude), freight 0.000725, stockable.

**Oil Well** (id 74, category MINING, sector Oil):
- makes 415 t of CRUDE a month (100 barrels a day);
- needs an unworked owned oil site (`hasDepositFor` by good);
- cash 2,000, materials 50, points 400, land 87,120 sq ft (a 2-acre pad);
- power 30, water 5, 3 jobs (2 NO_DIPLOMA, 1 DIPLOMA).

**Oil Refinery** (id 75, category HEAVY_INDUSTRY, sector Refining), a modular plant of 2,000 barrels a day:
- uses 8,300 t of CRUDE a month (2,000 × 30.44 ÷ 7.33);
- makes 8,300,000 L of FUEL: 1,000 L a tonne, or 86% of a barrel's 1,165 L as transport fuels;
- land 870,000 sq ft (20 acres); cash 60,000, materials 1,000, points 30,000;
- power 1,000, water 250, 100 jobs (50 NO_DIPLOMA, 30 DIPLOMA, 15 COLLEGE_ENGINEERING, 5 UNIV_SCIENCE);
- stock 25M L (3 months).

**Sectors** (both appended to `Sectors.KEYS`):
- **Oil** has `Mining`'s shape: `groundLimit` reads the oil reserve; it plans one well a month while unworked sites remain; wells retire when the oil runs out.
- **Refining** has `HeavyIndustry`'s shape: it plans while FUEL's local price clears above its cost at nameplate (`estimatedMonthlyProfit`).

**Buyers:**
- **Households** (`Motoring`, at 6d): `Markets.draw(FUEL, null, Trade.HOUSEHOLDS, drivers × 40 × 1.2)`. The refiners' shelf goes first and the rest is imported, so the bill follows the world price level: +13.7% in Jerus's city at 1.137.
- **Rail** draws FUEL as a buyer, at 18 L a tonne hauled (`WORLD_FUEL_PER_TONNE` ÷ the import price). `bookImportedService("Fuel")` goes, as its comment foresaw.
- **The refinery** takes the wells' crude first and imports the rest.

**Audit:**
- "+ households FuelFunded" and "− households FuelImports" carry only the imported part (`getHouseholdFuelImports()`).
- The domestic part is Refining's sales to households, already audited, like the domestic half of a car.
- `TradeByGood` lists FUEL with HOUSEHOLDS among its buyers, in place of `HOUSEHOLDS_FUEL`.

**Imports replaced and jobs** (measured in Jerus's city at month 1,851):

| | journeys or litres | US$ a month |
|---|---|---|
| households | 4,546,300 journeys, 5.46M L | 9.105M |
| railway | 9.52M L (at the import price × 1.137) | 18.04M |
| total | about 15M L | 27.1M |

That is 1.8 refineries (180 jobs), needing 15,000 t of crude: 36 wells (108 jobs) if the crude is local.

**Build cards:** the Mining tab gains Oil Well, with an oil no-deposit page; Heavy Industry gains Oil Refinery; BuildCard and BuildMenuCheck cover both. Oil counts in offer prices from K on; offers listed before keep theirs.

### 2.8 The land office and the Build shortcut (at 1,389 × 868 the content area is about 1,273 × 720 above the fold)
**Layout:**
- **Head,** as now: "Land office" with its (i), the pay toggle, and "‹ Build". The pieces are the house's: `Pieces.chipStrip` for the sides, `grid`/`gridCell` for the rows, `tag` for BEST VALUE, `stepChip` for Expand, and `offerCard` on the funding page.
- **Hero row, 400 px:** the small map on the left (600 × 400, with Expand). On the right (657 px): THE CITY: "241.6 km² · 89.6 dry · 1.7 fresh · 150.3 sea"; the world line: "of the world's 1.13 Pt of iron, the city owns 0.00045%"; four side chips, each with its best tag; the chosen side's 10 offers as 28 px rows. Each row shows the lane (1 to 10, left to right, facing out), km², a dry/fresh/sea bar, deposits as icons with sites and Mt or kt, the price in the toggle's money, the price per dry km², and Buy (Fund when short). Clicking a side on the map selects it, hovering a row lights its band, and BEST VALUE works as today.
- **Below:** THE GROUND bar, the four worth cards (Ore gains oil), details, and the funding page, as now. "Buy the next N" becomes "Buy the best N".

**The Build shortcut ★14.** LAND FREE gains "Buy the best land · 3.4 km² · US$12.1M ›", and the no-land page gains "Buy the best: 3.4 km² for US$12.1M". Both buy `Game.bestOffer(need)`:
- in general, the most dry km² per US$ among affordable offers that are not mostly sea, the nearer one on a tie;
- from the no-land page, the cheapest offer that covers the shortfall, if one does;
- on a deposit refusal, the most iron (or oil) sites per US$;
- on `NO_COAST`, the cheapest offer with sea.

When the city is short of money, the funding page opens, sized to that offer.

### 2.9 The save: SAVE_FORMAT 31 (from J1b)
**Fields added to `DataSave`:**
- **J1a:** `worldSeed` (long).
- **J1b:** `worldSeaTheta` (double); `worldTotals` (double[7]); `landCentre` (half-side in plots, the five km², `sites[7]`, `tonnes[7]`, the legacy field's x, y and sites); `landLanes` (double[40], each lane's frontier in plots); `landPurchases` (a list: side, lane, r1, r2, month, priceUsd, paidLocal, the five km², `sites[7]`, `tonnes[7]`); `landOffers` (40: id, side, lane, r1, r2, the five km², `sites[7]`, `tonnes[7]`, priceUsd, listedMonth); `depletion` (double[7]); `nextOfferId` (int). The five km² are total, dry, fresh, sea and forest.
- **J2:** `freshRights` (double).
- **J3:** `mapStamp` (long).

**Changed or retired:**
- Kept: `landOwned` (now the derived dry sq ft, checked on load), `landBlocksPurchased` (now counting purchases) and `landMarketPrices`.
- Read only to convert: `landListing`, `ironDeposits` and `ironReserveTonnes`.

**Why the bump:** a format 30 save's listing and pooled ore would load wrongly. The conversion runs at `saveFormat` ≤ 30, and an older build refuses a 31 save (`isFromNewerBuild`).

**Conversion ★9:**
- **Seed:** mix64 of the city name's hash ^ the bits of foundingCash ^ the bits of landOwned ^ the month.
- **Site:** the founding search, plus one test: the L∞ square holding `landOwned` must be at least 80% dry, with the sea within half its side + 2 km. If none of the nearest 64 coastal cells passes, the driest site found is used.
- **Centre:** sized to `landOwned`, with its ore as in §2.4.
- **Offers:** 40 listed at today's prices. The old 9 parcels go, once.
- **Water:** `freshRights` = max(0, the fresh plants' nameplate − 121,600 × the centre's fresh km²).
- **Map:** the canonical allocation, and E = 0.

Jerus's centre took 560 to 851 ms with the point sampler; the tile sampler should take about a tenth of that (64 ns against 650 a plot). A 5B or 10B save takes 2.5 to 2.7 s, once.

**Identity:** a city saved and reloaded must give back every field above and the sidecar byte for byte, and its next month must play to the same population, cash, offers and map.

### 2.10 Scale at 5B and 10B
| | 5B | 10B | source |
|---|---|---|---|
| purchases to reach it | 392–427 | 411–444 | derived: 18 in blocks to 0.929 km², then ln(A/0.929)/0.0368, A = the area dry or with its water |
| offers listed | 40 | 40 | fixed |
| districts / pyramid nodes | 15,129 / 5,146 | 29,929 / 10,160 | measured |
| owned iron / oil sites | ~444k / 44k | ~888k / 89k | derived: 0.505 and 0.0505 a km² of land |
| iron fields under a 1% offer | ~1,760 | ~3,520 | derived: 0.2 × 8,797 or 17,593 km² dry |
| heap: districts / pyramid / world grid | 5.4 / 0.5 / 0.54 MB | 10.8 / 1.0 / 0.54 MB | measured / 12 longs a node / 135,424 floats |
| map caches | ≤ 48 MB | ≤ 48 MB | a budget, whatever the size |
| JSON added, purchases and offers | ~145 KB | ~150 KB | derived: ~330 B a record (Gson's names and 17-digit doubles) |
| sidecar | 0.40–4.66 MB | 0.78–9.22 MB | measured |
| month time added | ≤ 0.76 ms reconcile, O(1) depletion | the same | measured at 10B |
| a frame | ~200 tiles at L0, or 48 node images | the same | follows the screen |

At 10B the centre is 1,211 km a half-side and 70% water, at 1.76M km² dry (measured, seed 4127). That is the world's coastline, and spec-scale §9's question.

## 3. Build order
**Every batch:** one Opus implementer with a fresh context, in `/home/claude/cbs`, with a version bump and a `GameVersion` entry. The gate: both builds silent; the suite green except HealthCheck; BuildMenuCheck; `tools.Stale` at 0; `endings-check.sh`; the playtest re-baselined only where stated, each move explained, then rerun byte-identical. `tools.Maps` is left to the docs pass.

**J1a: the world (0.7.56). No gameplay change.**
- **Files:** `World` (new): `World(long seed)` and `static World of(long)` (lazy, cached); `terrainAt(long x, long y)`, `tileTerrain(long tx, long ty, byte[])`, `regionTerrain(long x0, long y0, int stride, int n, byte[])`; `foundingX()`, `foundingY()`, `river()`; `fieldsInCell(int, Resource)`, `cellTotal(int, Resource)`; `computeTotals()`, `seaTheta()`; §2.1's constants, each with its sentence; `Resource` (enum: unit, density, site km², tonnes a site, colour, `Good` or null, in-ground share) and `Deposit` (record); `Founding`: `worldSeed`, `DEFAULT_WORLD_SEED = 4127` (the mockup's default) and `withWorldSeed()`; `ui/FoundingScreen`: a World field with a dice, random on open. Also `DataSave.worldSeed` and `Game.getWorld()`.
- **WorldCheck (new)** asserts: the same seed gives the same terrain, site, river and fields, and two seeds differ; sea is 71 ± 1% and land 29%; lakes are 3.7% and forest 31% of land (± 0.5 points); the site passes its four tests; each cell's fields sum exactly to its total; the totals pass is deterministic and takes ≤ 200 ms (measured 30); tile terrain matches the point function on ≥ 99.5% of plots, at ≤ 0.5 ms a tile (measured 0.07); a world built twice is identical. NewGameCheck covers the seed, SaveFileCheck `worldSeed`.
- **Trace:** byte-identical to `pt0755`.

**J1b: the land on the world (0.7.57, SAVE_FORMAT 31).**
- **Files:** `CityLand` (new): the centre, lanes and purchases; `owns`, `sideLane`, `bandKm2`, `extend`; totals by kind; `LandParcel` becomes the offer (side, lane, r1, r2, the five km², `sites[]`, `tonnes[]`), so `Game.buyLandParcel(id)` and the funding pages keep working; `LandMarket`: 40 offers; `offersOn(side)`, `listNext(side, lane)`, `offerKm2()` (★2), `price()` (★6), `bestValue()` (dry km² per US$), `richest(Resource)`, `cheapest()`, `cheapestWithSea()`. `LISTING_SIZE` becomes `OFFERS_A_SIDE = 10`, and `getMinBlocks` becomes the unit; `LandManager`: owned dry, fresh and sea from `CityLand`; `extract(Resource, t)`; depletion; iron as in §2.4; forest regrowth in `clearMonth`; `LandConversion` (new, §2.9); `Game`: conversion in the load path; `getCityLand()`; `landShelf()` over the 40 by price per dry km²; `bestOffer(need)` (★14); J1b's `DataSave` fields; an interim `ui/LandScreen`: the 3 × 3 shelf shows the best 9 of the 40, each naming its side and lane, in km².
- **Premises, before → after:** LandCheck 5 to 5f: "nine plots are listed" → "ten offers a side"; "the ground still costs $0.70" → batch I's price at founding; "parcels are blocks and grow" → the size rule; "some plot has iron" → the world's fields; "the listing survives a save, old format included" → the conversion; MiningCheck 2: a deposit comes from a bought offer with iron; BuildAdviceCheck: the office's price is the best offer per dry sq ft; FundCheck, MoneyCheck and TreasuryCheck: offer ids; ScaleCheck: the copy scales the centre (`LandConversion.restate`); ReadPathCheck and SaveFileCheck: the new fields.
- **New assertions:** world totals are conserved through a purchase and through extraction; deposits deplete in acquisition order; after a purchase and a save, the other 39 offers are field-for-field identical; a lane's next offer starts at the last one's r2; the three research saves convert with dry sq ft within 1e-9, iron sites ≥ mines, and tonnes exact; a saved and reloaded city is identical.
- **Playtest:** "room to grow" (utilisation > 0.85) buys `bestValue()`; "a deposit" buys `richest(IRON)` when sites ≤ mines committed; `build()` buys `cheapest()` while short; every 13th stop buys `bestValue()` under 10% of cash. Purchases to reach your size fall from 210 to 143–169 (derived).
- **Trace:** moves in land, iron sites (0.5 a km² against about 2), mines, steel and everything downstream. Re-baseline `pt0757`, then run the 16-seed ensemble.

**J2: water and desalination (0.7.58).**
- **Files:** `UtilitiesHandler` (the cap and §2.3's getters) and `ServicesManager` (fresh and sea production kept apart); `BuildingsTemplate` (`"source"`) and `buildings.json` (id 73); `Game` (`hasCoastFor`, `BuildResult.NO_COAST`, `getFreshRights`); `ui/BuildScreen` (`showNoCoastMenu`), `BuildAdvice` (the water unit), and `CityNeeds` with `ui/ServicesScreen` (the limit's line); `DataSave.freshRights`.
- **WaterCheck (new)** asserts: the cap is the constant × owned fresh km² + rights; a capped city rations; buying fresh water lifts the cap by exactly its share; desalination ignores the cap, is refused `NO_COAST` without sea and allowed with it, and adds 10,880 kW of draw; a converted save's rights are as defined. Also InfrastructureCheck's water premises, BuildingDataCheck (74 buildings) and BuildMenuCheck (74 of 74).
- **Playtest:** when the fresh cap binds, build desalination if the city owns sea; otherwise buy the most fresh km² per US$, or, with no fresh water on offer, the cheapest sea and then desalination.
- **Trace:** moves through water, power and the treasury. Re-baseline `pt0758`.

**J3: the map's data and painter (0.7.59). Model only.**
- **Files:** `CityMap` (new): districts, `reconcile`, `canonical`, `deal`, the pyramid, `readSidecar`, `writeSidecar`; `TilePainter` and `TileRaster` (new, pure), and `BuildingVisual` (★10); `GameFiles.mapFile(slot)` and `write(Path, byte[])`; `Game`: the monthly reconcile, save and load, and a rebuild on a stale stamp; `DataSave.mapStamp`.
- **MapCheck (new, headless)** asserts: the districts sum to the model's counts every month of a 120-month city; an added building moves no placed one and changes one tile count; shared ports agree, and no road meets a border away from a port; the same inputs paint the same pixels; one more building moves a median of 0 others; the sidecar round-trips byte for byte, and a stale stamp rebuilds; the performance bound: a 1,389 × 868 L0 screen (200 tiles) paints and rasters in ≤ 80 ms (derived: 38 at the measured 0.19 ms a tile), and its time for Jerus's city × 1 and × 10,000 differs by a ratio ≤ 1.5; the reconcile takes ≤ 5 ms a month at 10B (measured 0.76).
- **Trace:** byte-identical, since nothing in the model reads the map.

**J4: the map view, the land office and the Build shortcut (0.7.60). UI.**
- **Files:** `ui/MapView` (new): the Canvas, levels of detail, §2.6's caches, pan, zoom, hover, the background queue; `ui/LandScreen` (§2.8, keeping the worth cards, details and funding); `ui/BuildScreen` (LAND FREE's shortcut and the no-land button); `Icons` (MAP, EXPAND); km² on Build's needs line and LAND FREE; `Pieces` (a `mapCard`, if needed).
- **Checks:** BuildMenuCheck; a `LandScreen` words probe (the side rows, built as `plot()` is today); MapCheck on `MapView`'s pure half (transforms and the choice of level).
- **Trace:** byte-identical. Your PC check covers the look and a 60 fps pan.

**K: fuel (0.7.61).**
- **Files:** `Good` (CRUDE, FUEL); `sectors/Oil.java` and `sectors/Refining.java` (new), with `Sectors` (KEYS, accessors); `buildings.json` (ids 74 and 75); `Motoring` and `HouseholdAccounts` (the drawn fuel), and `sectors/Rail` (uses FUEL); `MoneyAudit` (the imported part only) and `Sectors.tradeByGood`; `Game` (`minesCommitted` and `hasDepositFor` by good); `LandMarket` (oil priced); `BuildCard` and `ui/BuildScreen` (the cards, the oil no-deposit page).
- **OilCheck (new)** asserts: a well lifts only owned oil, depletes it, and retires when it runs out; the refinery takes local crude first, and households and the railway take local fuel first; fuel's money audit: in every month of a city with wells and a refinery the audit closes (relative < 1e-10), the imported part equals FuelImports, and the domestic sales sit on Refining's statement; with no refinery, households pay today's bill × the world price level. Also the fuel premises in CarCheck, RailCheck and ForeignCheck, and BuildMenuCheck (76 of 76).
- **Playtest:** buy `richest(OIL)` per US$ when fuel imports exceed 1% of GDP and oil sites ≤ wells committed. Wells and refineries are left to the investors, so the playtest tests the private response.
- **Trace:** moves. Fuel rises 13.7% at a world price level of 1.137, then falls as refineries come; imports fall; two new sectors. Re-baseline `pt0761`, then run the ensemble.

## 4. ★ decisions
1. **Lanes.** Each side is 10 square-polar wedges, and an offer is the next band in its lane. Only lanes give 10 standing offers a side that all touch the city, never reroll, and keep the city compact and square when bought evenly; the mockup's pinwheel strips can't hold 10.
2. **Size rule:** m × max(1 block, 1% of the city), with m drawn as today. A new city sees today's listing exactly; the mean offer is 3.8%; 10B takes 411–444 purchases.
3. **Nothing rolls.** The centre is the founded or converted land, fixed, per your "no side rolls".
4. **The world.** Cells are 61.44 km, not 64, so plot, tile, district and cell nest by powers of two. The world is a flat square of 368² cells, the Earth's area within 0.2%, with the city in the middle. The founding site has an iron field within 2 km, and away from it fresh water is lakes only.
5. **Iron: 0.5 sites a km² of land, 15 Mt a site.** That keeps the ore a km² carries (7.59 Mt, measured) on a quarter of the sites, so deposits read as places (1.9% of the map). A city your size would hold 45 sites for your 21 mines; your converted city keeps its 155.
6. **Prices:** batch I's ground. Fresh water 45% and sea 8% (the mockup's figures). Iron in the ground at 1/350 of its world floor (today's $0.40). Oil at 5% (iron's rule, "a year and a half of a working site's profit"). Resources with no industry are free.
7. **Fresh water:** 121,600 units a month per km², the world's runoff over the world's river area. The wells' 8,000 sit outside the cap, and old saves get rights for what they already pump.
8. **Desalination:** the water plant's output and staff, twice its cost (the low end of the real 2 to 5 times), and 10,880 kW (3.5 kWh/m³). Any owned sea opens it.
9. **Conversion.** The centre holds the old dry ground, at a site at least 80% dry. Iron is the save's sites (at least the mines standing and on order) and its tonnes, none invented. The seed comes from the save, and the old 9 parcels go.
10. **The mockup's footprints, not true land.** True land filled 92% of plots, a carpet, against the 20 to 28% you liked. Flats are the apartment types; home daycare is drawn as a home.
11. **Each building's own hashed place, u^1.5, in the road-first order.** Exponent 2 put 52% of buildings beside roads but moved a mean of 2.9 per new building; exponent 1 gave 32% and 0.47; 1.5 gives 44% and 0.71.
12. **The map's history in districts.** New buildings go to the first district with room, inner first; farms, utilities, mines and plants go outer first. A field belongs to the band holding its centre. Depletion follows acquisition order. Caches stay within 48 MB.
13. **Fuel.** Two new sectors: Oil (wells, category MINING) and Refining (HEAVY_INDUSTRY), so no new `BuildingType`. Households' fuel follows the world price level, as rail's and every good's already does. Wells and refineries are left to the investors.
14. **"Best"** is the most dry km² per US$, the nearer on a tie, never mostly sea; a deposit or coast refusal uses its own measure.
15. **The world seed.** `Founding.defaults()` uses 4127, so the harnesses stay deterministic. The founding screen proposes a random seed, and old saves derive theirs.

The mockup's ★ calls: 1, 5, 6, 7, 8, 10 and 12 stand. 2 and 3 give way to lanes and no roll. 4 changes: sea offers are listed, never best. 9 changes: flats are the apartment types. 11 gives way to real prices.

## 5. Risks
- **JavaFX costs are unmeasured.** The prototype has no JavaFX, so `setPixels` on 128 px, about 300 `drawImage` calls a frame and a 1,313 × 790 Canvas are budgeted, not timed. J4's PC check must confirm 60 fps when panning a cached view.
- **The traces move three times** (J1b, J2, K): ore comes on a quarter of the sites; the water cap binds dense cities; refineries earn the band's rent at the ceiling, so expect a fast build-out. The 16-seed ensemble may shift its fails. Explain each re-baseline before the next batch.
- **Coastal centres.** At seed 4127, your 89.6 km² centre would be 63% water. The 80%-dry search fixes saves of a city's size, but at 5B and 10B the city meets the world's 71% sea, and the driest-site fallback may still be mostly water.
- **The painter under-draws roads:** 163 of a 205-plot budget, because road ends die at coasts and edges. One more road building re-lays its tile, about once per 30 buildings added.
- **Far node images take 13 to 30 ms** before the integer rewrite, so a cold far screen takes about 1 s to fill.
- **A lost or mismatched sidecar** is rebuilt canonically, and the picture shifts once. Path-dependent cities compress worse: up to 9.2 MB at 10B.
- **The world's iron is 1,400 times the Earth's** (1.13e15 t against about 8e11 t of resources). That is the game's mining balance carried over, not geology.
- **Gameplay at 10B** is unchanged: travel, the reach of services, and two highways (spec-scale §9).

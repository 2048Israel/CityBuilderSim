# Earth 2: slope, shallow sea and bridges: a design study

*2026-10-12. A read-only design study: no game code was changed. From Samuel's brief `earth-2-brief.md` (his decisions of 2026-10-12): buildability follows slope, not altitude ("the foot of a big mountain stays buildable (Medellín) ... most of the Himalayas isn't"); mountains drawn as three greys with snow, "only the lightest buildable on"; foothills resisted "until flat space runs short", by mechanisms and not by "modifiers"; each land type with its own price on a sector's land line; mines, quarries, oil, highways and rail on any ground, priced by slope; a shallow sea that highway, rail and gravel-road bridges may cross ("Halifax's two harbour bridges are the model"); bridges all or nothing; and a check of why his Nova Scotia cities had oil and iron all round them.*

*What was read: the code at 0.7.135 (main 1afe9c0), not changed: `World`, `EarthTerrain`, `EarthData`, `EarthSite`, `Geology`, `CityLand`, `GridConversion`, `LandManager`, `LandMarket`, `Zoning`, `BusinessInvestment`, `Game` (`buildFor`, `consider`, `interestTest`, `bestOffer`), `BuildCard`, `BuildAdvice`, `AutoBuilder`, `SpreadPlanner`, `ExtractionSector`, `Oil`, `CityRuns`, `DistrictPlan`, `TilePainter`, `TileRaster`, `LandMap`, `MapTiles`, `GlobePicture`, `SectorStatements`, `EconomyManager`, `GameVersion`, `GameFiles` and the harnesses that hold them; the studies `spec-earth.md`, `spec-globe.md`, `spec-roads-and-ports.md`; Samuel's two Nova Scotia saves (slots 1 and 2, read-only copies).*

*What was measured: the whole land's slope from NASADEM (all 14,520 tiles, 136 GB streamed), GEBCO 2026's depth over every sea tile, a landmass layer over the whole Earth, and **earth-2.bin baked** (§2.8). Fourteen sites were measured four ways. The Nova Scotia deposits were redrawn from the game's own code (`earth2/DepositProbe.java`). The pipeline is in `tools/earth/` (`slope_tiles.py`, `depth.py`, `landmass.py`, `bake2.py`, `check2.py`, `FORMAT-2.txt`, `ATTRIBUTION-2.txt`, `bake-earth-2.bat`); the prototypes and figures are in `earth2/`; the mockups are `earth2/earth2-study.html` and the artifact "Earth 2 Study". Figures marked (est.) are estimates, each with its reason; [S#] are the sources at the end.*

---

## 0. In one page

**The data is done, and all of it is public domain.**
- **Slope comes from NASADEM** (NASA, no restriction [S3]), measured over 90 m. Measured at 30 m, radar noise and tree tops put 15% "foothill" on the flat Amazon; over 90 m it is 0.3%.
- **Depth comes from GEBCO 2026** (public domain, credit [S1]), which earth-1 already uses.
- **Copernicus DEM fails your rule**: it asks for an indemnity and a liability sentence passed on to players, the same reasons HydroRIVERS failed. AW3D30 and FABDEM fail too (§2.1).
- **earth-2.bin is baked**: 136,924,697 bytes. It holds earth-1's ten layers byte for byte, plus slope, shallow sea, depth and landmasses, and all 30 of its checks pass. **The release `earth-2` is yours to make** (§2.8).

**Slope decides, not altitude.** Four classes by grade, at the FAO's class edges [S9]:

| class | grade | on the map | buildings |
|---|---|---|---|
| flat | under 10% | its cover colour | yes |
| foothill | 10–30% | lightest grey | yes, dearer |
| steep | 30–60% | mid grey | no |
| very steep | 60% and over | dark grey | no |
| snow | (earth-1's ice) | white | no; wins over slope |

The checks:
- Medellín's centre (1,482 m) is 98% flat.
- El Alto (4,025 m) is 100% flat.
- The cell at Everest's summit is 94% very steep.
- In a 30 km box, Medellín is 18% flat, 47% foothill, 31% steep and 4% very steep.

**Foothills are resisted by cost, not by a coefficient.**
- **Preparing a foothill site is builders' work:** earth moved and walls built, quoted like any construction (§4.1).
- **Ground is used flat first**, as bare ground is used before forest today.
- **Foothill ground is priced at the going price less that work**, never below the base price of unwanted ground (§5).
- **While the city's land is cheap,** a foothill plot costs more all in than a flat one, so every whole-cost test, the payback ranking and the land office's pickers choose flat. Once crowding has raised the price of flat ground past the base price plus the preparation, foothill costs the same and is used. That is "until flat space runs short", arrived at by arithmetic.

**Each class has its own price.** A sector's land line gets a note: flat, foothill and (for a mine) steep, each with its km² and its price per m² (§5.3).

**Mines, quarries, wells, highways and rail go on any land.**
- Mines take steep ground first.
- Highways and rail pay for the works the city's ground needs: cuts and fills on foothill, viaducts on steep, tunnels on very steep. Rail pays more than a highway (§4.3).

**Shallow sea: water 30 m deep or less (est.)** is a new class.
- The Halifax Narrows is a 20 m sill 300–400 m wide [S14].
- At this threshold, a third of the sea within 20 km of land is bridgeable, and 98% of all the sea is not.

**Bridges come in two kinds (§6):**
- **Short crossings, up to 420 m for a highway or rail and 180 m for a street:** the map's runs lay them as they lay fresh-water bridges today, and only where the city's own ground lies beyond. **Halifax's Macdonald and MacKay bridges are this kind.**
- **Long crossings are orders on Build:** a Highway, Rail or Road Bridge.
  - Before anything is laid, a continuous path of shallow or fresh water must reach dry land on the far side.
  - The whole span is judged and priced as one order. Deep water anywhere on the path refuses it.
  - A long bridge links two **landmasses**. This finally builds the roads study's island rule (decided 2026-10-09, never built), with the bridge as a second way to link besides two ports.
  - The model example is the Confederation Bridge: 12.9 km over 24 m of water to Prince Edward Island, which earth-2 holds as its own landmass of 5,597 km².

**Real depth replaces E4's guess.**
- E4 makes the North Sea 80 km off Rotterdam 150 m deep; GEBCO says 34 m.
- Off Valparaíso, E4 says 91 m where the sea is 2.4 km deep.
- Oil platforms then stand where the real sea allows (§7).

**Nova Scotia (§8): the deposits follow the geology table exactly, and the table is the problem.**
- Both of your cities are on the real Earth, and every field they own matches the code to the tonne.
- Every province under Nova Scotia is classed correctly.
- But Earth uses the generated world's densities, rescaled within a narrow range. Oil's weight can only rise with known petroleum, never fall. So a fold belt gets an iron field every ~70 km², and the Scotian Shelf about 90 oil fields within 150 km of Halifax.
- That is not "huge and rare". **EG1** would make deposits follow the real geology's own numbers. It is your call, and it waits under Pending.

**Batches (§10).** ET1 to ET6 go at the end of the queue, after FR4:

| batch | what |
|---|---|
| ET1 | the file and the drawing |
| ET2 | the buildable rule and site preparation |
| ET3 | prices and the land line |
| ET4 | highways and rail by slope |
| ET5 | landmasses |
| ET6 | bridges |

**21 decisions (§11)**, each with a recommendation. ET1 to ET6 follow the recommendations until you answer.

---

## 1. What the game does today

- **Ground is one class a plot** (`World` L98–128): grass, forest, fresh, salt, sand, ice, desert. One rule, `World.dry()` L134, says what is buildable: grass, forest, sand and desert.
  - On Earth the class is drawn from earth-1's per-cell shares with seeded detail. This happens in two places that must agree: `EarthTerrain.draw()` L525 for tiles and `classAt()` L797 for points and far views.
  - Plots are never saved. A holding's five areas (total, dry, fresh, sea, forest), counted plot by plot, are saved in `landCentre`, `landHoldings` and `landOffers`.
- **There is no height on Earth.** spec-earth's decision E8 ruled heights out of gameplay; "slope rules can come later".
- **Land has one price.**
  - The market price is a base (`LandMarket.BASE_PRICE_PER_SQ_FT`, $0.70 a sq ft) times a crowding premium; inside the city, times a scarcity multiplier.
  - An offer is priced on dry + 0.45 × fresh + 0.08 × sea, so ice costs nothing.
  - Grass, forest, beach and desert cost the same. Zoning prices by sector, not by kind of ground.
- **Nothing in the model chooses a place.**
  - Land is amounts ("AMOUNTS, NOT PLACES"). The only place choices are which land-office offers to buy (`Game.bestOffer`, `LandMarket.bestValue` and the like), and all of them judge price per dry km².
  - Which plot a building stands on is the map's alone (`CityMap`, `DistrictPlan`).
- **No preparation cost exists.** Building on forest clears it in an order of use (bare ground first, then forest) at no cost.
- **Roads and rail are capacity units with land in square feet, not lengths.**
  - The map's `CityRuns` lays as many highway and track plots as the model has land for.
  - It bridges FRESH water up to 14 plots (420 m) for a highway or rail and 6 plots (180 m) for a street, and only if the city's own ground lies beyond. SALT always stops a run.
  - The roads study's Highway Bridge (its B1), its landmasses and its two-port rule were decided on 2026-10-09 and never built. Its batch names I1 and I2 have since gone to the materials batches.
- **Depth on Earth is a guess** (`EarthTerrain.depthAt()` L1835): 140 m × the distance to the coast / 54,167 m. `Oil.isShallow()` lets a platform stand in up to 150 m of it.

---

## 2. The data

### 2.1 Sources and licences

Your rule: anything shipped must be sellable with no licence fee and credits only; otherwise use a public-domain source. Each licence was read in its own text on 2026-10-12 (quotes and URLs in `earth2/` and the data report's §1).

| source | verdict | the deciding text |
|---|---|---|
| **GEBCO 2026 Grid** (15″, land and sea) | **passes**: public domain, with credit | "placed in the public domain and may be used free of charge ... Commercially exploit ... Users must: Acknowledge the source"; not for navigation [S1]. earth-1 already uses it. ATTRIBUTION-2.txt gives the credit in the GEBCO_2026 page's own longer form. |
| **NASADEM** (1″) | **passes**: no restriction (CC0) | NASA: "Unless the content is marked with a use restriction or license, data provided from a NASA-led mission are licensed as Creative Commons Zero" [S2]. NASADEM's page: "openly shared, without restriction" [S3]. |
| SRTM v3 (1″) | passes | the same NASA policy. Used as the fallback mirror. |
| NOAA ETOPO 2022 | CC0 on NOAA's terms; **not used** | NOAA waives its own rights [S5], but ETOPO's land is built from Copernicus DEM and FABDEM, and NOAA cannot waive those. |
| **Copernicus DEM GLO-30/90** | **fails "credits only"** | free and commercial, but Art. 6(c) requires a set liability sentence in any notice; 6(e) binds every later user (players) to the same obligations; Art. 7: "The User will protect the Licensor ... against any liability claim"; Art. 9 ends the licence on breach [S6]. The same features failed HydroRIVERS (spec-earth E6). Used here only for comparison. |
| ALOS AW3D30 (JAXA) | fails | "commercial purposes without a fee; however, the user needs to notify JAXA thereof in advance" [S7] |
| FABDEM | fails | CC BY-NC-SA 4.0 [S8] |
| ArcticDEM | passes (CC BY 4.0), **not baked** | the way to measure slope north of 61 N later [S20] |

**How NASADEM was reached.** NASA's own server needs an Earthdata login. Two public mirrors need none: Microsoft's Planetary Computer, the one used [S4], and ESA STEP's copy of SRTMGL1, the fallback.

### 2.2 Slope: measured over 90 m

**Why not 30 m, though a plot is 30 m.** A 30 m surface model measures radar noise and tree tops as slope. Share of land at 10% grade or more:

| site | NASADEM at 30 m | Copernicus at 30 m | NASADEM over 90 m |
|---|---|---|---|
| flat Amazon floodplain near Manaus | 14.7% | 20.8% | **0.3%** |
| Rotterdam | 2.0% | 4.7% | **0.0%** |
| Halifax (its drumlins are real) | 18.1% | 25.3% | **9.0%** |

**The method.** NASADEM's 1″ heights are averaged to 3″ (90 m), and slope is taken by Horn's 3×3 method on that grid. The cost of measuring this way: rugged country loses 2 to 6 points of "very steep" against 30 m (Medellín 4.1% against 9.4%).

**Public domain costs nothing in accuracy.** At 90 m, NASADEM and Copernicus agree to a mean of 0.4–0.8 points by class, 2.5 at worst.

**Coverage.**
- NASADEM covers 61 N to 56 S. Its catalogue has a 60–61 N row that NASA's page omits.
- North and south of that (11,849 tiles), the slope is **an estimate**: GEBCO's 15″ slope through a table calibrated on NASADEM at 45–60 degrees.
- Tested on 1,143 measured tiles at 55–60 N, the estimate is off by a mean of 3.0, 2.9, 1.5 and 0.45 points a tile (flat, foothill, steep, very steep).
- Greenland and Antarctica are mostly earth-1's ice, which wins over slope.
- A bit per tile (`slope.est`) marks every estimated tile.

**The whole run** took 67 minutes here: 14,520 tiles, 136.1 GB streamed, none failed.

### 2.3 Thresholds

The three edges, 10%, 30% and 60%, are the FAO's slope-class edges ("Strongly sloping 10–15, Moderately steep 15–30, Steep 30–60, Very steep > 60" [S9]).

- **Buildings stop at 30%.** Hillside codes stop there: "Land with an average slope of 30 percent or more shall not be developed", with special rules from 15% [S10].
- **Highways and rail climb steep ground only by works.** Highways climb at up to 6–7% [S11]. Adhesion railways stop near 4%, and the record is 13.5% (Lisbon's tram) [S11].

**The sites:** share of land in each class, %, flat / foothill / steep / very steep, for a box about 30 km square, measured over 90 m.

| site | classes | site | classes |
|---|---|---|---|
| Medellín | 17.6 / 47.4 / 30.9 / 4.1 | Halifax | 90.9 / 9.0 / 0.1 / 0.0 |
| Bogotá | 61.7 / 28.2 / 8.9 / 1.2 | Rotterdam | 100 / 0 / 0 / 0 |
| Kathmandu valley | 30.1 / 28.4 / 33.7 / 7.8 | Rio de Janeiro | 63.6 / 19.5 / 14.3 / 2.6 |
| Everest | 11.0 / 24.9 / 27.7 / 36.5 | Hong Kong | 27.6 / 42.7 / 28.5 / 1.2 |
| La Paz / El Alto | 48.9 / 28.7 / 20.9 / 1.5 | Denver / Front Range | 78.1 / 15.0 / 6.7 / 0.3 |
| Innsbruck | 10.6 / 19.6 / 44.5 / 25.3 | Wellington | 20.9 / 48.5 / 28.9 / 1.7 |
| Upper Engadin | 5.8 / 20.8 / 45.0 / 28.4 | Amazon near Manaus | 99.7 / 0.3 / 0 / 0 |

**Spot cells (3×3 cells of 15″):**
- Medellín's centre, 1,482 m: 98% flat.
- Kathmandu's Durbar Square, 1,306 m: 97% flat.
- El Alto airport, 4,025 m: 100% flat.
- Denver, 1,598 m: 96% flat.
- La Paz's Plaza Murillo, 3,650 m: 41% flat, 49% foothill, 10% steep.
- Everest's summit cell: 94% very steep.

Slope, not altitude, decides, as you asked. One caveat: the real Medellín climbs onto ground this calls steep (its hillside comunas). The 30% refusal is a planner's rule, not a description of every city.

**Sensitivity at Medellín** (flat / foothill / steep / very steep, %):

| thresholds | classes |
|---|---|
| 8/25/50 | 13.1 / 42.5 / 34.5 / 9.9 |
| 10/30/60 (chosen) | 17.6 / 47.4 / 30.9 / 4.1 |
| 15/35/70 | 31.8 / 41.2 / 25.7 / 1.2 |

The thresholds are baked into the file's shares, so changing them means baking again (about an hour here).

### 2.4 How fine the slope is stored

**Storage.** The file stores shares per cell, as earth-1 does. The game draws each 30 m plot from those shares with seeded detail, ranked, as it draws forest.

**What each resolution keeps.** Share of Medellín's valley-floor cells (cells measured at least 60% flat) that stay flat:

| storage | flat kept | the slope layer's size |
|---|---|---|
| 15″ | 0.87 | 204 MB (projected) |
| **30″ (baked)** | **0.76** | **57.2 MB** |
| 1′ | 0.62 | – |
| 2′ | 0.47: the valley's shape is gone | – |

`earth2/medellin-resolution.png` shows the four. **30″ still draws the valleys of Medellín, Kathmandu and the Inn** (`earth2/medellin.png`, `everest.png`, `innsbruck.png`). 15″ would make the slope alone four times earth-1 (D3).

**The packing, one byte a cell:**
- bits 0–2: the share at or above foothill, in sevenths;
- bits 3–5: the share at or above steep, in sevenths;
- bits 6–7: the very steep part of the steep, in thirds.

A share from 25 to 100 samples is only good to about a tenth anyway, and this costs 38% less than two 4-bit shares.

### 2.5 Depth and the shallow sea

**Halifax.**
- GEBCO reads 17–21 m along the line of each bridge: 21 m deepest under the Macdonald, 18 m under the MacKay.
- The Bedford Institute of Oceanography (BIO): "the Narrows constitutes a sill 20 metres in depth and 300 to 400 metres wide" [S14].
- Bedford Basin: GEBCO 64 m, BIO 71 m [S15].
- The bridges [S16]:
  - Macdonald: 1.3 km long, 762 m of suspended spans.
  - MacKay: 1.2 km long, 740 m of suspended spans.
  - Main spans of 441 m and 426 m (Wikipedia).

**How 15″ resolves a channel.** A 15″ cell there is 463 × 329 m, so the Narrows is 1 to 1.5 cells wide. earth-1's sea share already draws its shape. In the baked shallow layer, the Narrows' cells at the Macdonald Bridge are 15/15 shallow and Bedford Basin's middle is 0/15 (`earth2/halifax.png`).

**The threshold: 30 m (est.).** Real long sea bridges mostly stand in 5–25 m of water.
- GEBCO gives 24 m for the Confederation Bridge's deepest water.
- Foundations in deeper water are records described as hard: Rio–Antirrio's strait "reaches 65 m" [S17].
- 30 m keeps the Narrows with a margin for GEBCO's smoothing, and refuses open straits.

How much sea is bridgeable:

| no deeper than | 10 m | 20 m | **30 m** | 50 m | 100 m | 200 m |
|---|---|---|---|---|---|---|
| share of all sea | 0.6% | 1.3% | **2.0%** | 3.2% | 5.4% | 7.2% |
| share of sea within 20 km of land | 14.9% | 26.1% | **34.3%** | 44.1% | 57.4% | 66.9% |

**The longest bridge: 36 km (est.)**, from the Hangzhou Bay Bridge (35.7 km), the longest wholly over sea. For scale:
- Lake Pontchartrain, 38.4 km (a lake);
- Jiaozhou Bay, 26.7 km;
- Confederation, 12.9 km;
- Øresund, 7.8 km [S18].

The record main span is 2,023 m (the 1915 Çanakkale Bridge [S19]), which bears on D15.

**Two layers, two jobs.**
- **`shallow`, at 15″:** the share of a cell's sea no deeper than 30 m (0–15). It carries the detail that crossings need.
- **`depth`, at 1′ (1.85 km):** the cell's mean sea depth, by the metre to 200 m, then in 10 m steps to 750 m, then one code for deeper. It is for oil fields and platforms; it reads 4 m at the Macdonald Bridge, where its cell is mostly shore. At 15″ it would cost more than the rest of the file (252 MB).

### 2.6 Landmasses

The roads study's decision I7 put "the landmass layer in the Earth pack"; Earth 2 is that pack.

**How the layer was built** (5.8 minutes for the whole Earth, `tools/earth/landmass.py`):
- A 15″ cell is land where earth-1's sea and lake levels sum to 7/15 or less, the same side of the coast that `EarthTerrain` draws.
- Rivers 420 m wide or more cut the land.
- Cells join their four neighbours, across tile edges and the 180th meridian.

**What it found:**
- 113,291 landmasses; 19,868 of at least 1 km²; 288 of at least 1,000 km².
- The five largest: Afro-Eurasia (Suez does not cut it), the Americas, Antarctica, Australia, Greenland.
- Prince Edward Island is landmass 103, 5,597 km² (real about 5,660).
- **Halifax, Dartmouth and Bedford are one landmass**, joined by land round the head of Bedford Basin, so a bridge over the Narrows is a shortcut, not a link. McNabs and Lawlor islands are one landmass of 3.3 km².

**Its limit:** a channel narrower than a cell does not separate land. Manhattan, Long Island and Montreal Island come out joined to the mainland. Those crossings are short, and the map's runs make them anyway.

**Size and format:** 6.7 MB (the roads study guessed 1–2 MB, but every islet of a cell gets an id). Each landmass's place name is its most populous Natural Earth place.

### 2.7 One file, not an add-on

**earth-2.bin carries earth-1's ten layers byte for byte, plus the new ones** (`tools/earth/FORMAT-2.txt` gives every byte):

| layer | grid | content | size |
|---|---|---|---|
| `slope` | 30″, a byte | the packed shares (§2.4) | 57.17 MB |
| `slope.est` | a bit a 1-degree tile | which tiles are estimated | 0.001 MB |
| `shallow` | 15″, a byte | share of the cell's sea no deeper than 30 m | 7.75 MB |
| `depth` | 1′, a byte | the cell's mean sea depth (§2.5) | 12.48 MB |
| `landmass` | 15″, palette ids | each land cell's landmass | 6.68 MB |
| `landmasses` | text | id, area, cells, place name | 0.08 MB |
| earth-1's ten layers | as earth-1 | byte for byte | 52.75 MB |
| **earth-2.bin** | | | **136.92 MB** |

**The container is earth-1's, unchanged** (`EARTH-1\n`, layout 1), so `EarthData` reads it as it is. The file is named earth-2 in its meta layer and identified by its SHA-256.

**Why one file:**
- A save names one Earth.
- The reader, the release and `get-earth.bat` stay single.
- earth-1's coasts provably do not move (check2 compares every blob).

The cost is that a player downloads earth-1's 53 MB again inside it. Old Earth saves are refused either way, which you have accepted (D18).

### 2.8 The bake, and the release

**The baked file:** 136,924,697 bytes, SHA-256 `b48adce10c2a5f65f7670d9b617a96566b6d3aae8cafcb7ee3432eaefb78ae9a`, built 2026-10-12.
- **check2's 30 checks all pass:** earth-1's layers byte for byte; the shares nested; no slope on water and no depth on land; Medellín's valley floor 100% flat; Everest 47% very steep within 15 km; the Narrows shallow; Halifax and Dartmouth one landmass, McNabs its own.
- **It is not in git**, as earth-1 is not.
- **The release `earth-2` is yours to make on GitHub's website**, as you made `earth-1`. Its assets are earth-2.bin, `tools/earth/earth-2.sha256` and `tools/earth/ATTRIBUTION-2.txt`.
- **Your PC can bake its own copy instead:** `tools/earth/bake-earth-2.bat`, double-clicked.
  - It needs about 12 GB free, downloads about 140 GB (about 3 hours at 100 Mbit/s, est.) and resumes after a stop.
  - A re-bake gives another hash (the meta records the date). **Whichever file you release defines `earth-2.sha256`.**

### 2.9 Load time

**Reading:**
- A founding card (5 km) reads one or two 1-degree tiles: 3–10 KB of the new layers beside 2–11 KB of earth-1's.
- A city's 50 km reads 4–6 tiles: 18–37 KB new.

**Inflating is what costs.** A tile with every new layer inflated holds about 250 KB in `EarthData`'s cache, against earth-1's 116 KB. That holds only if the landmass layer is kept as bytes and a palette, not widened to ints. The 10 MB cache then holds about 40 such tiles instead of about 86; ET1 measures whether it should grow.

**Opening** the file reads four more indexes once, about 0.1–0.15 MB (est.).

---

## 3. The ground: relief beside cover

### 3.1 Two planes, not more classes

**The two planes.**
- **Cover** keeps today's classes: grass, forest, fresh, salt, sand, ice, desert, plus one new water class, SHALLOW (§6.1).
- **Relief** is a second value a plot carries: FLAT, FOOTHILL, STEEP or VERY_STEEP. Water and ice have none.

**How a plot gets its relief.** `EarthTerrain.draw()` and `classAt()` both draw it from the slope shares, with seeded noise ranked through the same `quantile()`/`below()` as forest, so a cell's share comes out right. E7 added ice and desert the same way.

**Why a plane and not classes:**
- Folding relief into the class byte would multiply the classes: forest on foothill, desert on steep, grass on very steep.
- Every reader that counts classes (`tileCounts`, `km2Of`, `DistrictPicture`, `GlobePicture`, `TileRaster`) would have to learn twelve more.
- With a plane, the readers that care about relief read it, and those that don't stay as they are.

**What wins:**
- **Ice wins over slope.** Snow on a mountain is earth-1's ice, white, refused.
- **Water has no relief.**
- **Forest keeps its cover:** the forest law and Forestry count it on any slope (D5 is how it is drawn).

**Generated worlds stay flat:** relief FLAT everywhere, no SHALLOW. Every generated city is then byte for byte as today, and so is the default playtest trace (D19).

### 3.2 What may stand where

| ground | buildings | mines, quarries, wells | highways, rail | roads (streets) |
|---|---|---|---|---|
| flat | yes | yes | yes | yes |
| foothill | yes, with preparation (§4.1) | yes | yes, with cuts and fills | yes |
| steep | **no** | yes | yes, viaducts and walls | no |
| very steep | **no** | yes | yes, tunnels | no |
| ice | no | yes (D9) | crosses, as today | no |
| shallow sea / fresh water | no | wells at sea, as today | short crossing or a bridge order (§6) | short crossing |

**Two rules replace `dry()` for building:**
- `World.buildable(cover, relief)`: dry cover, and relief FLAT or FOOTHILL.
- `World.workable(cover, relief)`: any land, ice included, for works that may stand anywhere.

`dry()` keeps its meaning (land that is not ice), so the areas saved today keep theirs.

### 3.3 The books by relief

A holding's five areas stay as they are. Beside them, its dry ground splits by relief: flat, foothill, steep and very steep, in km², plus its shallow sea.

**This goes under a new Gson key**, parallel to `landHoldings`, `landOffers` and `landCentre`.
- **Why not widen the rows:** `fromPurchaseRow` returns null on a row of the wrong width, so wider rows would refuse every save, generated ones included.
- **What a missing key means:** all flat. That is true of every generated city.

### 3.4 Founding

**The snap** moves a click to the nearest plot with buildable ground round it (`SITE_DRY_PLOTS` read as buildable), not just dry.

**The card gains three lines:**
- the ground within 5 km by relief ("Flat 62% · foothill 30% · steep 8%");
- the nearest shallow water and the depth offshore;
- the landmass, by its place name, and its size.

**A new refusal:** a spot with no buildable ground within reach is refused, with words that say so and point to the nearest buildable ground. See the mockup's board 3 for the Himalayan spot.

---

## 4. What slope does to cost

### 4.1 Preparing a foothill site is builders' work

**The work.** A level building pad on a slope is cut on its upper side and filled on its lower side, with a wall where terraces meet.

**The arithmetic.** For a pad W wide across the contours on a grade g, with balanced cut and fill:
- earth moved per m² of pad = g × W / 4;
- wall face per m² = g (one wall the height of the pad's drop, per pad).

**For a plot (W = 30 m) at a foothill's typical grade of 17% (est.: the class runs from 10% to 30%, and slopes thin out with grade):**
- 1.3 m³ of earth moved per m²;
- 0.17 m² of wall per m².

ET2 measures the class's real mean grade from the site histograms.

**Where it enters the game.**
- **The quote:** preparation is added to the builders' quote in points, by area of foothill ground used. Its cost and its time come from the builders' own prices and capacity, as any construction does.
- **The unit rates** (points a m³ of earth moved, a m² of wall) are researched from published construction costs in ET2's brief, each with its source.
- **No coefficient** is laid on top: a foothill pad costs what its earthworks cost.

**For scale (est.):**
- At about US$15 a m³ for earthworks and about US$500 a m² of wall face (est., to be sourced in ET2), a foothill m² costs about US$105 to prepare.
- The base price of unwanted ground is $7.53 a m² ($0.70 a sq ft). The crowding premium can lift the going price up to 120 times that (`CROWDING_CEILING`).
- So foothill pays only once crowding has lifted flat ground to about 15 times the base (est.), a city well past its first districts.

### 4.2 Flat first, by order and by price

The model has no positions, so "flat first" is two things, both mechanisms.

**An order of use.** `LandManager.allocate()` takes flat ground first and foothill after, as `clearFor()` takes bare ground before forest today. A sector's land is then a mix of flat and foothill amounts, and the foothill part carries its preparation.

**A price per class** (§5). It does three things:
- A foothill offer is cheaper, but its buildable ground costs more to use.
- Every chooser that values ground then values it by class, plus preparation: `BusinessInvestment.groundCostOf()`, `Game.buildFor()`, `BuildAdvice.landValue()` (and so `orderLife()` and automatic building's payback ranking), `LandMarket.bestValue()`, `Game.bestOffer()` and `AutoBuilder.shortcutOffer()`.
- `Game.interestTest()` and `SpreadPlanner`'s whole cost then see a dearer foothill building and order it later, or not at all, until flat ground is short.

**The map follows the model.** `DistrictPlan` puts a sector's flat amount on flat plots and its foothill amount on foothill plots, so where buildings stand on the map agrees with the books (est.: a ranking of cells by relief inside the existing cell order).

### 4.3 Highways and rail by slope

**What a road is in the model.** Roads and rail are capacity units with land, not lengths, and the runs that place them are the map's.

**The expected mix.** A highway or rail order pays for the works its land needs at the city's own mix of ground:
- extra cost = road land × Σ over classes (share of the city's buildable-and-steep ground in that class × the works per m² that class needs);
- the works: cuts and fills on foothill, walls and viaducts on steep, tunnels on very steep.

**The rates (est., from published cost studies, researched in ET4's brief):**
- Mountain highways commonly cost 1.5 to 3 times flat ones.
- Tunnels cost tens of millions of dollars a km.

**Rail costs more than a highway on every class above flat.** Its grade limit (2–4% against 6–7%) makes it longer and more often on structures.

**Their land comes from all ground.** It is spread over the classes in the city's proportions, not taken from the buildable pool alone, so a mountain city's highways use its steep ground as real ones do.

**On the map**, `CityRuns` may cross steep and very steep ground. A run on very steep ground is drawn as a tunnel (portals, a dashed line), on steep ground as a viaduct.

This is an approximation, and said so: the real works depend on where the line runs. Moving the runs into the model is the honest alternative and far larger (D10).

### 4.4 Mines, quarries and wells on any ground

**Today:** a works needs owned field sites (`LandManager.fits()`) and the template's land from the one dry pool every building uses.

**After ET2:**
- Their land comes from all of the city's ground: steep and very steep first, since nothing else can use it; then foothill; then flat.
- Ice is allowed (D9). Kumtor in Kyrgyzstan mines through a glacier.
- Preparation for a mine is not charged: a pit and its benches are dug as part of the mine's own cost.

**What it means in play:** a mountain city's mines no longer compete with its houses for the valley floor.

---

## 5. The land line: a price for each class

### 5.1 The residual price

**The rule.** The going price (market × crowding, × scarcity inside the city) stays the price of **flat** ground. For the others:
- **Foothill:** the going price less its preparation per m², never below the base price of unwanted ground (`BASE_PRICE_PER_SQ_FT`).
- **Steep and very steep:** the base price. Only mines, quarries, wells, highways and rail use them, and none of them would pay more for ground they could equally put elsewhere.
- **Ice:** priced at 0, as today.
- **Shallow and deep sea:** as sea is today (0.08 of dry). Bridges pay for their crossing in their own price (§6.4).
- **Zones** apply their share to each class's price, as they do to the one price today.

**Why it is not a modifier.** This is how real land is valued: developed value less the cost of developing it, the "residual" method. The price falls out of the preparation's real cost.

**What it does over a city's life (est.):**
- **Early on,** the going price is low, so foothill sits at the base price, and building on it costs the base price plus its preparation, well above flat. The city builds flat.
- **As crowding lifts the going price** past the base price plus the preparation (about US$110 a m² in §4.1's estimate), foothill costs the same all in as flat. From then on both are used.
- **That is "the game should avoid foothills until flat space runs short"**, with no dial saying when.

### 5.2 Offers

`LandMarket.price()` prices an offer by class, and `groundKm2()` becomes the offer's value:

flat × going price + foothill × foothill price + steep × base + fresh × 0.45 + sea × 0.08.

Every picker that compares offers per dry km² (`bestValue`, `bestOffer`, `cheapest`, `shortcutOffer`) compares them per **buildable km², counting preparation**. A cheap offer that is mostly mountain then loses to a dearer one on the valley floor, unless the city wants a mine there. The deposit pickers (`DEPOSIT`) keep valuing a field's sites.

### 5.3 The statement's land note

`SectorStatements.sheet()` L489 shows "Land" with no note. It gets **note 11**, its land by class:

> **11. Land.** At today's prices, by the ground it stands on:
> Flat 1.24 km² at D$41.20 a m² · D$51.1M
> Foothill 0.31 km² at D$27.80 a m² · D$8.6M
> Steep (its mines) 0.80 km² at D$0.75 a m² · D$0.6M

**What has to change:**
- `SectorBooks.SectorMonth` keeps each class's area and price, under new keys.
- `EconomyManager.landValueOf()` and `assessedLandValueOf()` value by class.
- The equity statement's "Its land at the start, at today's price" revalues by class.

The mockup's board 4 shows a services sector's note and a mining sector's.

---

## 6. Shallow sea and bridges

### 6.1 The class

**SHALLOW** is a new water class: sea no deeper than 30 m (est., D11).
- **How a plot gets it:** drawn from the `shallow` share with seeded detail, as sea is drawn from the sea share.
- **On the map:** a lighter blue than deep sea (`earth2/halifax.png`).
- **The salt rule:** `World.salt(t)` covers both SALT and SHALLOW, so every rule that asks "is this the sea?" (ports, the coast, `SeaRoutes`, sea fields, the 0.08 price) is unchanged.
- **What SHALLOW changes:** only crossings, and the sea's look.

### 6.2 Short crossings: the runs' rule, widened

`CityRuns.clear()` bridges FRESH water today: a highway or rail up to 14 plots (420 m), a street up to 6 plots (180 m), and only if the city's own non-sea ground lies beyond. That condition is already all or nothing.

**The widening:** the same rule, with SHALLOW alongside FRESH. Deep sea still stops a run, and the run turns along the shore.

**Halifax's two bridges are this kind.** The Narrows is 300–400 m wide and 20 m deep, inside 420 m and 30 m. A Halifax city that owns both shores sees its highways cross where the real bridges stand.

**What the model sees:** nothing new. Short crossings are paid inside the highway's own cost, as fresh-water crossings are today.

### 6.3 Long crossings: an order, all or nothing

**What can be ordered.** Build gets three bridge cards: **Highway Bridge**, **Rail Bridge** and **Road Bridge** (gravel or paved). A Road Bridge is a street-sized crossing for a city without highways. Each is ordered between two places:
- **a near end:** owned buildable ground on a linked landmass, at the crossing's shore;
- **a far end:** dry land across the water, owned or bought with the order (the landing plot).

**The path test (in the model, which may read `World` and `CityLand` but never the map).**
- **What must exist:** a continuous chain of plots from the near shore to dry land on the far side, each FRESH or SHALLOW, with no SALT (deep) plot anywhere on it.
- **The cap:** no longer than 36 km (est., D13).
- **How it is found:** the shortest such chain, from the city's coast toward the chosen far shore.
- **Nothing is laid until the whole path exists and the whole order is paid for.** It is then built as one contract by the builders, its plots appearing together when the contract completes. There is no half-bridge into the sea.

**The refusals, word for word as the card shows them** (board 5):
- "No bridgeable path: deep water (41 m) 2.1 km out. A bridge needs water 30 m deep or less all the way across."
- "The far side is not dry land."
- "Longer than a bridge can be: 41.2 km against 36 km."
- "Both ends are on Halifax's landmass: a long bridge must link two landmasses (a short crossing is laid by the highways themselves)."

**Fresh water** follows the same rule. Your 2026-10-09 rule (the highway bridge on fresh water only) widens to fresh water and shallow sea, for highway, rail and road.

### 6.4 What a bridge costs

**The order's price** is its length times a cost per m by kind (rail above highway above road) and by depth (taller piers in deeper water), plus its two landings.

**The rates (est.)** are researched in ET6's brief from built bridges. For scale:
- the Confederation Bridge cost about C$1.0 billion (1997) for 12.9 km, about C$78,000 a m;
- spans this long are among the dearest structures a city buys.

The cost goes through the builders' quote, so a long bridge takes their capacity for years, as real ones did.

### 6.5 What a long bridge does in the model: it links landmasses

**The island rule, as decided on 2026-10-09** (roads study §3; your I1, I2, I4, I5, I7):
- A landmass is ground the sea cuts off.
- Home is the founding site's landmass.
- Another landmass is **linked** once it has a port and a linked landmass has one too.
- Ground on an unlinked landmass is owned, counted and priced as today, but only a port can stand on it.
- Free ground for the investors, automatic building and the test player is the free ground on linked landmasses.

**Earth 2 adds the bridge** as the second way to link: a long bridge from a linked landmass links the one it reaches.

**ET5 builds the rule.** Its batch names were taken by the materials batches, so it is named ET5 here. **ET6 builds the bridges**, replacing the roads study's B1.

**A long bridge must link two landmasses (D14).** Within one landmass the model has no distance, so a long same-landmass bridge would be a cost with no effect. Halifax's bridges are short crossings and need no order.

### 6.6 How CityRuns and DistrictPlan honour a built bridge

**The saved list.** A built bridge is saved in the model: its kind, its two ends in plots and its length.

**On the map:**
- `CityRuns` lays the run of its kind along it (a highway bridge joins the highway network at both landings; a rail bridge joins the rail).
- `DistrictPlan` keeps its landings clear.
- `TileRaster.DECK` draws it.

The map reads the model; the model never reads the map.

### 6.7 One deep gap? Later, with research

**The option:** a single suspension span of up to about 2 km over water deeper than 30 m. It would admit Akashi- and Great Belt-type crossings.

**Recommendation: not now.** With the 1900 start, the long suspension span is a natural research item (equipment that arrives with its history). Until then shallow water is the rule (D15).

---

## 7. Real depth replaces E4's rule

**E4's rule** (140 m × distance to the coast / 54,167 m) against GEBCO:

| place | km out | GEBCO | E4 |
|---|---|---|---|
| Rotterdam, North Sea | 40 / 80 | 27 / 34 m | 69 / 150 m |
| Galveston, Texas shelf | 40 / 80 | 16 / 23 m | 107 / 210 m |
| Persian Gulf off Ras Tanura | 40 / 80 | 32 / 66 m | 106 / 209 m |
| Scotian Shelf off Halifax | 100 / 150 | 237 / 95 m | 259 / 388 m |
| Valparaíso | 20 / 40 | 656 / 2,369 m | 44 / 91 m |

**What E4 gets wrong:**
- **Broad shelves come out 3 to 9 times too deep**, so no platform stands past about 58 km where the real sea is 20–40 m.
- **Narrow shelves come out far too shallow:** off Chile, a platform could stand on 2.4 km of water.

**The fix:** `EarthTerrain.depthAt()` reads the `depth` layer (1′), and `SHELF_WIDTH_M` and the coast-distance rule retire. `Oil.isShallow()` (150 m) and `OilView` read real depth. The generated world keeps its own rule.

**What moves:** which sea fields take platforms. Rotterdam's North Sea gets more; a narrow shelf gets fewer. That moves the Earth playtest trace, and EarthFoundingCheck 5 (which asserts the old rule's arithmetic) is rewritten (D17).

A naming note for ET1: `Oil.isShallow()` means "a platform can stand here" and is unrelated to the new SHALLOW class. Rename it `platformDepth()` or similar, so the two are not confused.

---

## 8. Nova Scotia: why the deposits looked wrong

### 8.1 It was the real Earth

Your saves, read-only:
- **Slot 1 "Halifax":** 0.7.121, format 38, Earth, 44.65 N 63.60 W, seed 758,809,812.
- **Slot 2 "Scotia":** 0.7.131, format 39, Earth, 45.17 N 61.72 W, seed 284,719,415. That spot is on the Eastern Shore of Guysborough County, 55 km from Antigonish.

**The code hasn't changed.** The field-drawing code is the same from 0.7.121 to 0.7.135 (git diff). `earth2/DepositProbe.java` redrew both worlds at 0.7.135, and every holding with fields matches the save to the site and the tonne:
- Halifax: 4 holdings, 152 iron sites, 2.12 Gt, of which 1.75 Gt has been dug.
- Scotia: 2 holdings, 213 iron sites, 4.08 Gt.

Neither city owned any oil, coal, stone or copper, but oil fields lay all round both.

### 8.2 Every province is classed right

| USGS province | class | oil | iron | coal |
|---|---|---|---|---|
| 5219 Appalachian Foldbelt (the mainland, Halifax) | fold belt | 0.603 | 0.722 | 0.788 |
| 5221 Magdalen Basin (Stellarton, Springhill, Joggins) | basin | 1.207, its sea too | 0.361 | 1.577 |
| 5222 Fundy Basin | basin | 1.207, its sea too | 0.361 | 1.577 |
| 5220 Sidney Basin (sea only in the raster) | basin | 1.207 | 0.361 | 1.577 |
| 5217 Scotian Shelf | shelf | 1.810 at sea | 0 | 0 |

(Multiplier = weight × Z; oil also × its petroleum factor.) `Geology.HAND` has no Nova Scotia row because the name rules class them correctly.

**Two small data faults:**
- The onshore Sydney coalfield rasterises into 5219 rather than 5220, which halves its coal.
- The Scotian Shelf's known petroleum is 0 in the USGS file, despite Sable and Deep Panuke. That understates it a little; it does not overstate it.

### 8.3 What was drawn, and what the geology predicts

Fields within 50 / 150 / 300 km:

| spot | iron | oil (on land + at sea) | coal |
|---|---|---|---|
| Halifax, your key | 45 / 417 / 941 | 14 / 155 (53 + 102) / 661 | 2 / 11 / 20 |
| Eastern Shore, your key | 72 / 320 / 699 | 8 (all at sea) / 182 / 646 | 0 / 3 / 15 |
| Calgary (control) | 38–118 within 50 | 37–44 within 50, all on land | |
| Mesabi / Pilbara (controls) | 456–608 within 50 | | |
| Generated world, the same seeds | 17–161 within 50 | 0–13 within 50 | |

**The table alone predicts the same counts.** Within 150 km of Halifax it gives iron 400, oil 54.5 on land plus 106 at sea, and coal 7. So the placement follows the table.

**The one sampling flaw:** a cluster's centre lands anywhere in its 61 km cell, which carries the shelf's sea weight into the fold belt's nearshore water. 7 of the 8 offshore fields within 50 km of slot 2 lie there.

### 8.4 Why it looks wrong: the weights' design

1. **The base densities are the generated world's gameplay densities.** A fold belt's iron comes out at one field per ~70 km², each 0.3–0.4 Gt.
2. **The weights only rescale within a narrow range.**
   - A fold belt's oil weight is 1, so the Appalachians count like the Zagros.
   - Iron is 0 only on shelves.
   - Onshore Nova Scotia gets a fifth of Alberta's oil and a quarter of the Pilbara's iron.
3. **Known petroleum can only raise oil, never lower it.** A province with none gets its class's full weight.
4. **The shelf's weight of 3 covers about 25,000 km² of sea within 150 km of Halifax**, about 91 fields.

### 8.5 The recommendation: EG1, deposits where the geology says (D20, your call)

**What EG1 would do:**
- **Oil follows known petroleum.** A province's oil is drawn in proportion to its known petroleum (and, where the USGS assessed it, its undiscovered estimate). A province with none gets a small allowance, not its class's full weight.
- **Iron goes to its geology.** It is drawn on shields and on provinces with recorded iron. The USGS Mineral Resources Data System is public domain and records deposits by location; using it is a data question for EG1's brief.
- **Fewer, bigger fields everywhere**, as you asked on 2026-10-08 ("huge and rare"). The world's totals are kept, so a field is rarer and larger.
- **A cluster stays in its province's own cells.**
- **The two data faults are fixed by hand rows:** the Sydney coalfield, and the Scotian Shelf's petroleum.

**What it changes:** every Earth field, Z, the odds and the Earth trace. It belongs with your review of the industrial calls ("all the industrial calls get reviewed later"), so **EG1 waits under Pending** rather than in the queue.

---

## 9. The save, the file, and the renderers

### 9.1 The save

**Formats:**
- `SAVE_FORMAT` goes to 40.
- An Earth save below 40 is refused, by an Earth-only floor as `ICE_FORMAT` did. Its identity ("earth-1:…") already refuses it.
- The general floor stays 37, so generated saves keep loading.

**New keys:**
- each holding's, offer's and the centre's areas by relief (§3.3);
- each sector's land by class (§5.3);
- the bridges (§6.6);
- the landmasses table (ET5).

A missing key means flat and none, which is right for every generated city. No conversion code is written (your rule until the game is published).

### 9.2 Shipping earth-2 instead of earth-1

`EarthData` (`EARTH_1`, `DATA_FILE`, the identity prefix "earth-2:"), `EarthSite`'s words ("about 137 MB"), five places in `FoundingScreen`, `get-earth.bat` (its three names), `Play CityBuilderSim.bat`, `Build EXE.bat`, `fixture.py`, and a new fixture cut from earth-2 for EarthCheck. The container needs no reader change; the new layers need their readers.

### 9.3 The renderers

**Colours, words and legend.**
- `TileRaster.GROUND` gains three greys and the shallow blue; the relief plane is read where ground is painted.
- `LandMap` gains the words: hover, legend, `earthWords` with the relief and depth.
- Board 2 has every entry. For example: "Foothill · 10–30% grade · buildable; preparing a site costs more" and "Shallow sea · 30 m or less · a bridge can cross it".

**Forest on a slope (D5).** The recommendation keeps forest green, shaded toward its slope's grey, so the forest the model counts is visible and the slope still reads. Drawing grey over everything (as `earth2/medellin.png` does) would hide forest that Forestry and the forest law count. The mockup's board 1 shows both on Medellín.

**The far views.**
- Far nodes and the globe's GROUND tier read the 30″ shares at their stride.
- The globe's picture (a class byte a 4′ texel) gains the relief classes, so mountain ranges show grey from orbit and snow stays white (est.: a texel takes its dominant class).

**Time budgets.** A tile within `TILE_MS` (0.3 ms), a far node and the screen within 80 ms, a globe frame within 100 ms. E7 added two classes inside these budgets; ET1 measures the relief plane against them.

**Which harnesses move:**
- EarthCheck (the new fixture);
- EarthTerrainCheck 2–5, 9 and a new section for relief in the shape of 15;
- EarthFoundingCheck 2, 3, 5 (rewritten) and 6;
- MapCheck 10, 11 and 13;
- GlobeCheck's printed frames (Halifax is one of its spots).

---

## 10. Batches

**Where they go:** at the end of the queue, after FR4, as you decided.

**A second Earth playtest.** The Earth playtest (`--earth rotterdam`) is 100% flat, so it cannot show slope. Each of ET2 to ET4 adds a hilly Earth run, `--earth medellin` (est.: Medellín's 47% foothill exercises every rule), as a trace of its own.

| Batch | What | Traces |
|---|---|---|
| **ET1** | **earth-2 in the game, and drawn.** `EarthData` reads earth-2 (needs the release); the relief plane and SHALLOW drawn in `draw()` and `classAt()`; `World.buildable`/`workable`/`salt` (no rule uses relief yet); real depth from the `depth` layer, E4's rule retired, platforms on real depth; the map's greys, snow and shallow blue, legend and hover; far views and the globe picture; the founding card's lines (§3.4); the books by relief under new keys; `SAVE_FORMAT` 40 with the Earth floor; `get-earth.bat` and both .bat files; the fixture | default identical; Earth moves (platforms on real depth) |
| **ET2** | **Buildable by slope.** Buildings on flat and foothill only; `allocate` flat then foothill; site preparation as builders' work (rates researched in its brief); mines, quarries and wells on any land, steep first; the founding snap and refusal on buildable ground; the hilly Earth run added | default identical; Rotterdam likely identical (flat); Medellín new |
| **ET3** | **Prices by class and the land line.** The residual price; offers and every picker by buildable km² with preparation; zones by class; sector books by class; the statement's note 11 and the equity row; the land office's and offer card's classes | default identical; Earth moves where a city holds foothill |
| **ET4** | **Highways and rail by slope.** The works by the city's mix (rates researched); rail above highway; their land from all classes; runs across steep ground drawn as viaducts and tunnels | default identical; Earth moves where a city holds slopes |
| **ET5** | **Landmasses and the island rule** (the roads study's §3, decided 2026-10-09). The landmass layer read; the books by landmass; `Landmasses` (saved); linked by two ports; free ground on linked landmasses only; ports built on a chosen landmass's shore; the hatching, tags, words and inbox notice | moves only where a city owns ground on another landmass (the Rotterdam delta's Goeree-Overflakkee is one; measured in ET5) |
| **ET6** | **Bridges.** Short crossings over SHALLOW by the runs' rule; Highway, Rail and Road Bridge orders with the all-or-nothing path test, priced by length and depth (rates researched); linking landmasses; the saved list; runs and plans honour them; the Build cards and refusals | moves only where one is built |

**Under Pending, for you:**
- **ET-D:** the 21 decisions below.
- **ET-R:** the release `earth-2`. ET1 cannot ship without it.
- **EG1:** deposits where the geology says (D20).

**What can go first:** ET1 needs only the release. ET2 and ET3 can ride together if one implementer prefers it. ET5 must come before ET6.

---

## 11. ★ Decisions

**The data**
1. **D1. Slope from NASADEM, measured over 90 m**; north of 61 N and south of 56 S an estimate from GEBCO, marked by tile; ArcticDEM (CC BY) later to measure the north. **Recommend yes.** All of it is public domain; Copernicus fails your rule (§2.1).
2. **D2. Thresholds 10%, 30% and 60% grade**, the FAO's class edges. **Recommend yes.** Changing them means baking again, about an hour.
3. **D3. The slope stored at 30″** (57 MB; earth-2.bin 137 MB). **Recommend 30″.** The alternative, 15″ (204 MB; about 284 MB in all), keeps Medellín's valley floor sharper (0.87 flat against 0.76).

**The ground**
4. **D4. Four relief classes:** flat in its cover colour; foothill lightest grey, buildable; steep mid grey and very steep dark grey, refused; snow is earth-1's ice, white, and wins over slope. **Recommend yes.** This confirms the brief's assumption about which grey is buildable.
5. **D5. Forest on a slope stays forest green, shaded toward its slope's grey.** **Recommend yes.** The model still counts it, and the forest law and Forestry use it. The alternative is greys over everything; board 1 shows both.
6. **D6. Buildings on flat and foothill only;** steep, very steep and ice refused. **Recommend yes.**
7. **D7. Preparing a foothill site is builders' work** (earth moved and walls, priced from sourced rates). **Recommend yes.** It is a mechanism, not a coefficient.
8. **D8. A price for each class:** flat at the going price; foothill at the going price less its preparation, never below the base; steep at the base; the statement's note 11 by class. **Recommend yes.** It makes the city build flat first until crowding makes foothill worth it.
9. **D9. Mines, quarries and wells on any land, ice included,** taking steep ground first. **Recommend yes.**
10. **D10. Highways and rail priced by the city's mix of ground** (cuts and fills, viaducts, tunnels), rail dearer than highway, their land from all classes. **Recommend yes.** The alternative, the runs moved into the model, is a far larger change.

**Water and bridges**
11. **D11. SHALLOW sea at 30 m or less (est.),** a lighter blue. **Recommend yes.** The Narrows (20 m) is in, and open straits are out.
12. **D12. Short crossings** (highway or rail up to 420 m, street up to 180 m) **of fresh or shallow water are laid by the runs' rule**, only with the city's ground beyond, and paid inside the road. **Recommend yes.** Halifax's two bridges are this kind.
13. **D13. Long crossings are orders:** Highway, Rail and Road Bridge; a continuous shallow or fresh path to dry land must exist first; the whole span priced and built as one contract; deep water refuses; capped at 36 km (est.). **Recommend yes.**
14. **D14. A long bridge must link two landmasses.** **Recommend yes**, until the model has distance.
15. **D15. A deep gap crossed by one suspension span (to about 2 km):** **recommend not now;** later, with research.
16. **D16. The island rule as decided on 2026-10-09,** with the bridge as a second way to link and the landmass layer from earth-2. **Recommend yes.** It widens your rule "the only bridge is a highway bridge, over fresh water" to highway, rail and road, over fresh water and shallow sea.
17. **D17. Real depth replaces E4's guess;** platforms stand on real depth. **Recommend yes.**

**The file, the worlds, the deposits**
18. **D18. One file, earth-2.bin, released as `earth-2`;** old Earth saves refused. **Recommend yes.**
19. **D19. Generated worlds stay flat,** with no relief and no shallow sea. **Recommend yes.** The default trace stays byte for byte through ET1 to ET6.
20. **D20. EG1, deposits where the geology says:** oil by known petroleum; iron on shields and recorded deposits; fewer, bigger fields; clusters inside their province; the two Nova Scotia faults fixed. **Recommend yes, as its own batch after your industrial review.** It waits under Pending.
21. **D21. Founding on buildable ground:** the snap and the refusal read buildable; the card shows relief, water and landmass. **Recommend yes.**

---

## 12. Found on the way, and what could not be checked

**Found on the way:**
1. **E4's depth is badly wrong both ways** (§7). Until ET1, an Earth city off Chile can drill in kilometres of water, and one in the North Sea cannot reach its own shelf.
2. **The Sydney coalfield sits in the wrong province**, and the Scotian Shelf records no petroleum (§8.2).
3. **The roads study's island batches I1 and I2 lost their names** to the materials batches without being renamed or queued. They are ET5 here.
4. **`docs/map` is dated 0.7.123**, so its line numbers lag the tree. The docs pass owes a regeneration.

**What could not be checked, said plainly:**
- **NASADEM's gap-filling** used GDEM and AW3D30 in places, according to its page. NASA distributes the whole product "without restriction", and that statement is what this study relies on. The filled pixels could not be separated, because the layer that marks them is not in the mirror.
- **The mirror itself:** the Planetary Computer's NASADEM was not compared pixel for pixel with NASA's own (that needs a login). It decodes to NASADEM's grid with sane heights.
- **ETOPO's land licence chain**, and **GEBCO's land inputs** (SRTM15+): each publisher's stated terms were relied on.
- **Buildings in the heights:** NASADEM measures the surface, buildings included. Averaging to 90 m removes most of that (the Amazon reads 0.3% foothill), but a dense high-rise district may still read as foothill. This was not measured.
- **Small glaciers:** earth-1's ice bit marks a 2′ cell only when it is at least half snow and ice, so a small glacier is missed.
- **The Narrows' depth** is from a 1974 BIO report, and the bridges' main spans are Wikipedia's.
- **The estimates:** the 30 m threshold, the 36 km cap, the 17% foothill grade and the preparation, road-works and bridge rates are estimates. Each batch's brief researches its rates.

---

## Sources

- [S1] GEBCO, "Terms of use for the GEBCO Grid and derived information products": https://www.gebco.net/data-products/gridded-bathymetry/terms-of-use. Credit: GEBCO Bathymetric Compilation Group 2026, The GEBCO_2026 Grid, doi:10.5285/4f68d5c7-45eb-f999-e063-7086abc036fa
- [S2] NASA Earthdata, "Data Use Guidance": https://www.earthdata.nasa.gov/engage/open-data-services-software-policies/data-use-guidance
- [S3] LP DAAC, NASADEM_HGT v001: https://lpdaac.usgs.gov/products/nasadem_hgtv001/ (NASA JPL 2020, doi:10.5067/MEASURES/NASADEM/NASADEM_HGT.001)
- [S4] Microsoft Planetary Computer, NASADEM: https://planetarycomputer.microsoft.com/dataset/nasadem; ESA STEP SRTMGL1 mirror: https://step.esa.int/auxdata/dem/SRTMGL1/
- [S5] NOAA NCEI, ETOPO 2022: https://www.ncei.noaa.gov/products/etopo-global-relief-model (metadata gov.noaa.ngdc.mgg.dem:etopo_2022, use constraints)
- [S6] Copernicus WorldDEM-30 and -90 licences: https://dataspace.copernicus.eu/sites/default/files/media/files/2025-06/copernicus_contributing_mission_data_access_v2_cop_dem_licenses.pdf
- [S7] JAXA, Terms of Use of Research Data: https://earth.jaxa.jp/en/data/policy/
- [S8] FABDEM: https://data.bris.ac.uk/data/dataset/s5hqmjcdj8yo2ibzi9b4ew3sn
- [S9] FAO, *Guidelines for soil description*, 4th ed. (2006), Table 7: https://www.fao.org/3/a0541e/a0541e.pdf
- [S10] Concord, California, Municipal Code 18.300 (Hillside Protection): https://www.codepublishing.com/CA/Concord/html/Concord18/Concord18300.html
- [S11] "Grade (slope)", Wikipedia: https://en.wikipedia.org/wiki/Grade_(slope)
- [S12] USGS, Geologic Provinces of the World (WEP_PRVG): https://doi.org/10.5066/P9RMSU2R
- [S13] earth-1's sources and credits: `tools/earth/ATTRIBUTION.txt`
- [S14] McGonigal, Loucks and Ingraham, *Halifax Narrows: Sample Current Meter Data 1970-71*, BIO Data Series BI-D-74-5 (1974): https://waves-vagues.dfo-mpo.gc.ca/Library/49307.pdf
- [S15] Bedford Institute of Oceanography, Bedford Basin Monitoring Program: https://www.bio.gc.ca/science/monitoring-monitorage/bbmp-pobb/description-en.php
- [S16] Halifax Harbour Bridges: https://halifaxharbourbridges.ca/about/macdonaldhistory/ and https://halifaxharbourbridges.ca/about/mackayhistory/
- [S17] "Rio–Antirrio bridge", Wikipedia: https://en.wikipedia.org/wiki/Rio%E2%80%93Antirrio_bridge
- [S18] "List of longest bridges", Wikipedia: https://en.wikipedia.org/wiki/List_of_longest_bridges; "Confederation Bridge": https://en.wikipedia.org/wiki/Confederation_Bridge
- [S19] "1915 Çanakkale Bridge", Wikipedia: https://en.wikipedia.org/wiki/1915_%C3%87anakkale_Bridge
- [S20] Polar Geospatial Center, ArcticDEM on AWS: https://registry.opendata.aws/pgc-arcticdem/

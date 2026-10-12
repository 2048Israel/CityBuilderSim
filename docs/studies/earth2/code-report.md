# Earth 2: CODE agent report

Tree: /home/claude/citybuildersim at 0.7.135 (main 1afe9c0; `GameVersion.VERSION` L6185). Line numbers are this tree's. The docs/map indexes are dated 0.7.123, so I read sources and used `Where`. The model was built without JavaFX into /home/claude/e2work/classes. Nothing under `src/` was changed. New files:
- `docs/studies/earth2/DepositProbe.java` (the probe)
- `docs/studies/earth2/deposit-probe-output.txt` (its full output)

earth-1.bin was downloaded to /home/claude/e2data/earth-1.bin under a temporary name. Its SHA-256 matched `tools/earth/earth-1.sha256` (b754a28f…c13a) before I renamed it.

## Part 1: the code Earth 2 touches

**1. Ground kinds**
- **The classes.** `World.java` L98-128 defines GRASS 0, FOREST 1, FRESH 2, SALT 3, SAND 4, ICE 5 ("never dry, never built on… counted in a holding's whole area only") and DESERT 6 ("dry ground every rule treats as grass"). `CLASSES = 7`.
- **The one dry rule.** `World.dry()` L134 (grass, forest, sand, desert) is the single test of buildable ground.
- **How a plot gets its class on Earth.** `EarthTerrain.draw()` L525-648 is the tile rule.
  - Shares come from a 10×10 lattice blended through `EarthProj.blend`.
  - SALT where the sea share plus `COAST_FRAY`×coast noise (unranked) reaches `SEA_LEVEL` 0.5.
  - FRESH from GRWL rivers, the small-lake blobs, or the fresh noise ranked under the lake share.
  - ICE and DESERT by `covered()` of land cover's bit, interpolated, plus their own noise.
  - SAND where the plot is next to sea and the bare-ground bit is set.
  - FOREST where the forest noise, ranked through `quantile()`/`below()`, falls under the forest share.
  - GRASS for the rest.
  - A second copy of the same rule exists for points and far views: `classAt()` L797-838, with `farTerrain()` L1082/L1100 and the cameras `plane()`, `grid()` L1475 and `globe()` L1539.
- **Where a slope class would go.** It would have to be drawn in both `draw()` and `classAt()` from a new data layer, the same way E7 added ice and desert.
- **What "saved as drawn" means.** The plots themselves are never saved; they are a pure function of the World.Key. What is saved is each holding's areas: five areas (`CityLand.TOTAL/DRY/FRESH/SEA/FOREST` L70-85), counted plot by plot.
  - Counting code: `CityLand.groundOf()` L866, `unownedClasses()` L870, `tileCounts()` L851 (a `long[World.CLASSES]`), and the centre's `drawnBooks()` L338. All of these go through `GridConversion.km2Of()` L487, where ice counts only in TOTAL.
  - Where the areas are saved:
    - `landCentre` (`CENTRE_FIELDS` 24, L954)
    - `landHoldings` (`LandParcel.PURCHASE_FIELDS` 31, L254)
    - `landOffers` (`OFFER_FIELDS` 29, L251)
  - `CityLand.restore()` L1011 replays the rectangles into `LandGrid` and does not count again. `LandGrid` itself is never saved.
- **`LandManager` "THE GROUND BY TYPE" (L1128) is not about ground kinds.** It covers deposit types (hematite/magnetite and so on), the packing in `fits()`, and `depletionByType`.

**2. Land price and the land line**
- **What the city pays the outside world.** `LandMarket`:
  - Base price `BASE_PRICE_PER_SQ_FT` .0007 (k$) at L111.
  - Crowding premium: `crowdingPremium()` L508, with `CROWDING_MIDPOINT` 4000/km², `STEEPNESS` 5.3 and `CEILING` 120.
  - Restruck in `update()` L410-427.
- **What businesses pay inside the city.** The going price is market price × `scarcityMultiplier()` L455 (allocated against free ground, 0.65 to 1.90).
- **How an offer is priced.** `LandMarket.price()` L635 applies `groundKm2()` L655:
  - DRY + 0.45×FRESH + 0.08×SEA (`FRESH_PRICE_SHARE` L262, `SEA_PRICE_SHARE` L265).
  - Ice is in TOTAL only, so it is priced at 0.
  - Ore is added at its in-ground share.
- **Grass, forest, beach and desert already share one price everywhere.** There is no per-type price today.
- **Zoning prices by sector, not by land type.** A zone's ground costs `priceShare` × the going price (`Zoning` L1-80; `LandManager.priceFor(sqFt, sector)` L1673; `EconomyManager.groundPriceFor()` L314).
- **What a sector's books hold.**
  - `EconomyManager.landValueOf()` L277 = `getLandSqFtBySector` (`BuildingManager` L6431: template land × count) × the going price.
  - `assessedLandValueOf()` L304 is the same with zones applied.
  - `SectorBooks.SectorMonth` keeps one `landSqFt` and one `landPrice` (L264-265).
- **What the statement shows.**
  - `SectorStatements.sheet()` L489: `b.line(LAND, "Land", now.land(), then.land(), 0)`. Note 0 means no land note exists. The notes in use are 5, 6, 7, 8, 9 and 10 (L339, L341, L472), so a land note would be number 11.
  - The equity statement has one land row, "Its land at the start, at today's price" (`landRevalued()` L662, `equity()` L679-700).
- **Where a per-class price would plug in.**
  - `LandManager.allocate()` L1637 and `clearFor()` L1952 would need to track each sector's land by class, as amounts (the model has no positions).
  - SectorMonth would need area and price by class.
  - `landValueOf`/`assessedLandValueOf` would value by class.
  - `sheet()` would add a LAND note listing each class with km² and price per m².
  - `LandMarket.groundKm2`/`price` would price each class.

**3. Building costs and the whole-cost test**
- **How a building's cost is made up.**
  - `BusinessInvestment.totalCostOf()` L1184 = `withBuildersTax`(non-material cost × q + materials beyond the yard × price) + `groundCostOf()` L1197 (land sq ft × going price, or the zone's price). `getCostOf()` is at L1207.
  - `Game.buildFor()` L7597 charges `quoteBuild()` (L6192) plus `landManager.priceFor(landNeeded, sector)` at L7625, then calls `allocate` at L7641.
- **No site-preparation cost exists.** Forest clearing costs no money. `clearFor` puts built ground on bare ground first, then open forest, and the wood is lost until Forestry; this is an order of use, not a price.
- **The whole-cost tests.**
  - `Game.consider()` L4507 uses `largestSliceThatCarries`.
  - `Game.interestTest()` L4980: the earnings must reach `PROFIT_OVER_INTEREST` 1.25 × interest on the whole cost.
  - `BuildCard.appraise()` L1108 runs the gates ore, licence, staff, land, pays, interest.
  - `SpreadPlanner` gates (L289-362; `Gate.LAND`; `bestBlockedByLand` L383) are given "its ground with it" as cost.
  - `ExtractionSector` plans mines on the whole cost.
- **AutoBuilder.** It orders `BuildAdvice`'s cards, ranked by order over life.
  - `BuildAdvice.orderLife()` L1549 = quote + `landValue()` L971 + running cost × `lifeFactor`. `landValue` prices free ground at min(going price, office price) and anything beyond at the office's price (`LandManager.getOfficePricePerSqFt()` L443, the best-value offer).
  - It buys ground through `AutoBuilder.groundFor()` L1243 and `shortcutOffer()` L1293: the cheapest bare-ground offer that covers the lack, else the most dry km² per USD.
- **Where a slope preparation cost would enter, so flat ground wins by cost.** As a per-sq-ft charge on the foothill part of `allocate`, with flat used first (as bare ground is used before forest). The charge would then appear in:
  - `groundCostOf`/`totalCostOf`
  - `Game.buildFor` and the city's own build (L7180-7195)
  - `BuildAdvice.landValue`
  - The offer valuations: `bestValue` L787, `Game.bestOffer()` L1888, `shortcutOffer`

  Then `interestTest`, the payback ranking and the office pickers all respond with no coefficient.
- **Nothing in the model picks plots for a building.** Zones are "AMOUNTS, NOT PLACES".
  - The only place choices in the model are which offers to buy: `Game.bestOffer` (ROOM, SHORTFALL, DEPOSIT, COAST), `LandMarket.cheapest/bestValue/bestFresh/cheapestWith/richest`, `AutoBuilder.shortcutOffer`, and LongPlaytest's player.
  - All of them compare price per dry km², with all dry ground equal.
  - `GridOffers` lays out six offers a side, each the innermost free ground of its lane (geometry only).
  - Placement on the map is map-only: `CityMap` districts, `DistrictPlan`, `CityRuns`, `CityShore` ("nothing in the model reads it").
  - `LandManager.fits()` L1446 packs works onto field pieces by site count.

**4. Roads, highways, rail and bridges**
- **In the model, roads and rail are capacity units, not lengths.** From buildings.json, priced with no km or plot rate:

  | id | template | capacity | cash | points | materials | land (sq ft) |
  |---|---|---|---|---|---|---|
  | 29 | Gravel Road | 900 | 2326 | 1400 | 193 | 450,000 |
  | 13 | Paved Road | 1200 | 5400 | 4000 | 450 | 250,000 |
  | 30 | Elevated Highway | 1500 | 9600 | 10000 | 800 | 63,000 |
  | 62 | Rail Spur | 50,000 rail | 140,000 | – | – | 700,000 |
  | 63 | Freight Line | 200,000 rail | 520,000 | – | – | 2,400,000 |

  The reasoning for three road kinds is at `BuildingManager` L1270-1320.
- **CityRuns is map-only.**
  - `CityMap.layRuns()` (~L2900) lays as many highway and track plots as the model's land, month by month on owned ground. The ground it reads is `RunGround` L2797, which returns -1 for ground the city does not own.
  - Runs prefer straight lines: `TURN_COST` 40 and `JUNCTION_COST` 200.
  - Fresh-water bridges are judged in `CityRuns.clear()` L367-405:
    - The run crosses FRESH only if the water is no wider than `n.bridge` and the city's own non-sea ground lies beyond.
    - Highways and rail bridge up to 14 plots / 420 m (`TilePainter.MAX_BRIDGE` L167, `RAIL_BRIDGE` L170). Streets and gravel bridge up to 6 plots / 180 m (`STREET_BRIDGE` L163).
    - SALT always stops a run, which then turns along the shore.
    - ICE is treated as ground by the runs.
- **DistrictPlan.**
  - `dry = owned & World.dry(t)`; anything else blocks buildings (`groundRow()` L1956).
  - Street lay code: refused on SALT or unowned ground; ICE is not refused.
  - Bridges: `STREET_BRIDGE` and `ARTERIAL_BRIDGE` (L217/L220), flag `BRIDGE` L315, `cutAt()` L1817.
- **Not built yet.**
  - Islands and landmasses, and the two-port rule (spec-roads 3; queue rows I1/I2).
  - The Highway Bridge B1. earth-1 has no landmass layer.
- **What an all-or-nothing bridge order needs.**
  - A model-side order (a template, or an order kind with a span cost) going through `Game.buildFor` or the city's build and one contract in `BuildingManager`.
  - A planner test of a continuous crossable path over `World.terrainAt` and `CityLand` ownership. The model may read the world; it may not read the map.
  - A saved bridge list (`DataSave`/load path).
  - `CityRuns.clear` and `DistrictPlan` honouring built bridges (and shallow sea), with `TileRaster.DECK` L81 drawing them.

**5. Sea depth**
- **The Earth rule.** `EarthTerrain.depthAt()` L1835: 0 unless the plot is SALT; otherwise `SHELF_BREAK_M` (140, `World` L234) × distance to the coast (`coastDistanceM` to the nearest 15″ cell under `COAST_BELOW`) / `SHELF_WIDTH_M` 54,167 (L1806, measured by `measureShelfWidthM`). Results are cached.
- **The generated rule.** `World.depthAt()` L755 is linear in elevation down to `shelfTheta` (`SHELF_SHARE` .0886).
- **Who uses depth:**
  - `LandManager.isSeaField` L671 and the sea-site count L909 (sea against dry fields, the two oil pools).
  - `LandManager.seaFields()` L934 carries depth to `Oil.isShallow()` L797 (`PLATFORM_MAX_DEPTH_M` 150, L696), which decides where a platform may stand.
  - `OilView` L423 (wet and dry wells on the page).
  - Harnesses: WellCheck, EarthFoundingCheck 5-6, ReadPathCheck.
- **Who does not use depth.** CityShore, SeaRoutes (SALT at 480 m cells, `SALT_LEAST` 12/16) and Ports ("Depth does not set the class") read none.
- **What each caller needs.** A depth in metres at a field's centre plot (platforms) and sea against not-sea. A shallow-sea bridge class would be a new reader.

**6. Mines, quarries and oil today**
- **The field.** A field's centre is drawn off the sea (non-oil, up to `SEA_REDRAWS` 8 tries), so it may lie on grass, forest, fresh water, ice or desert.
- **A works needs two things:**
  - Owned sites on a field: `Game.hasDepositFor()` L7465 → `LandManager.fits()`.
  - The template's land, taken from the city's free dry pool by amount, the same pool every building uses (`canAllocate`/`allocate`). Examples: Iron Mine 400,000 sq ft; Large Surface Mine 43 M; Quarry 1.08 M; Oil Well 87,120.
- **Offshore.** The Offshore Platform and Platform Well (98, 99) take no land and need a sea field no deeper than 150 m.
- **What "any ground" changes.** A mine's land must stop coming from the buildable dry pool, or steep ground would refuse it.

**7. Save and data**
- **Formats.** `SAVE_FORMAT` 39 (`GameVersion` L6855), `OLDEST_LOADABLE_FORMAT` 37 (L6883), `EarthSite.EARTH_FORMAT` 38 (L66), `ICE_FORMAT` 39 (L75).
- **How a save names its Earth.** `EarthData.identity()` L342 is the literal "earth-1:" plus the file's SHA-256. `EARTH_1` is at L78. An Earth save whose identity is not this build's is already refused (`EarthSite.loadProblem()` L204 → `otherDataWords`; `DataSave.earthProblem()` L575).
- **Finding and checking the file.** `EarthData.locate()` L259 (`-Dcbs.earth`, else `DATA_FILE` "data/earth-1.bin" L85). The reader checks `MAGIC` "EARTH-1\n" L91 and `FORMAT` 1 L94 (checks at L275-277).
- **How layers are read.** Layers are looked up by name (`layer()`, `raw()`, `at()` L450). Blobs inflate into an LRU of `CACHE_BYTES` 10 MB (L97). A new layer is generic to read: bake it, list it in FORMAT.txt, and add it to the reader and fixture.
- **What a founding reads.** `EarthSite.measure()` L421: the snap (`siteNear`, `SITE_DRY_PLOTS`), the dry/fresh/sea/ice plots within 5 km, the province multipliers, the nearest iron (`IRON_RINGS` 16), the refusals (no site, ice) and the warnings (desert, no fresh water). `gameData()` L101.
- **What shipping earth-2 instead of earth-1 touches:**
  - `EarthData`: `EARTH_1`, `DATA_FILE`, `MAGIC`/`FORMAT`, the identity prefix.
  - The words in `EarthSite` (L144/L217 "earth-1.bin (about 53 MB)").
  - `FoundingScreen` L565/593/1245/1270/1327 (`EarthData.EARTH_1`).
  - `tools/earth`: `get-earth.bat` FILE/HASHFILE/URL (its header says "change the three names… together with EarthData.DATA_FILE"), a new `earth-2.sha256`, `ATTRIBUTION.txt`, FORMAT.txt, `earthread.py`, `fixture.py`, and the `src/main/resources/earth/earth-1-fixture.{bin,expect}` it writes.
  - `Play CityBuilderSim.bat` L46-51 and `Build EXE.bat` L91-179, which copy `data\earth-1.bin` and its attribution.
  - `Geology.HAND` is checked against earth-1's province rows (`of()` refuses on a mismatch). Z, the odds and `SHELF_WIDTH_M` were all measured on earth-1.
  - Earth-1 saves are refused by identity. A format bump with an Earth-only floor, as E7 did, is still needed for any changed records (see risk 5).

**8. Renderers**
- **Ground colours live in `TileRaster.GROUND` (L69), indexed by class, not in Palette.** Bridge decks use `DECK` (L81).
- **Names and legend.** `LandMap.groundWords()` L360, `hoverWords()` L469, `earthWords()` L492 (nearest place and province), `legend()` L578 and `planeLegend()` L591.
- **Far views.** `MapTiles.nodeTerrain()` L316 → `EarthTerrain.farTerrain()`.
- **District pictures.** `DistrictPicture` ranks plot codes with GROUND first; the far node shows the ground there.
- **The globe.** `GlobePicture` stores a class byte per 4′ texel (5,400×2,700, 14.6 MB). `frame()` L184 maps class → colours.
- **What adding grounds means.** Three greys, snow and shallow sea become new class values, or a second plane that every consumer reads (see risk 1). Each new class needs a colour, words and a legend entry.
- **Time budgets these must stay within:**

  | budget | value | where |
  |---|---|---|
  | `MapCheck.SCREEN_MS` | 80 ms | L299 (L0 screen; Earth far nodes over Rotterdam L3591; L3922) |
  | `RECONCILE_MS` | 5 ms | L305 |
  | `BOAT_FRAME_MS` | 0.5 ms | L2770 |
  | `MiniMapFrame.STAMP_MS` | 8 ms | L53 |
  | `GlobeCheck.FRAME_MS` | 100 ms | L106 |
  | `EarthTerrainCheck.TILE_MS` | 0.3 ms a tile | L145 |
  | `EarthTerrainCheck.CELLS_PASS_MS` | 1000 ms | L148 |
  | `EarthCheck.INFLATE_MS` | 1 ms | L97 |

**9. Harnesses that would hold Earth 2, and what slope or depth moves in each**
- **EarthCheck:** sections 4-9, the fixture and file layout as `earthread.py` reads it. New layers mean a new fixture and new expected values.
- **EarthTerrainCheck:**
  - 2 (shares are the data's), 3/4 (same key gives same ground; seeds move detail only), 5 (`TILE_MS`), 7 (rule pieces), 9 (cells pass), 15 (ice and desert: shares, frayed edges, the card's ice). The slope classes would follow the shape of section 15.
  - 10-14 (classes, Z, odds, fields by class) move only if the Geology weights change.
- **EarthFoundingCheck:** 2-3 (the card and its refusals), 5 (asserts the coast-distance depth rule and `SHELF_WIDTH_M` arithmetic; this gets rewritten), 6 (founds, plays and drills at sea).
- **MapCheck:** 10 (the map on Earth, far nodes within `SCREEN_MS`), 11 (district pictures), 13 (the globe on screen, the legend by tier).
- **GlobeCheck:** `FIXTURE_PRINTS` L112 and `FULL_PRINTS` L118 are hashes of rendered frames, and Halifax 44.65,-63.57 is one of the full spots. They move with any class change. Section 6 holds the picture's land against the far layers.
- **LandCheck:** offers and price, the office in km², best offer per need, saved offers, and 26 (ground by type).
- **Also affected:** ZoningCheck, SectorStatementCheck (the land note), WellCheck (shallow fields), BuildCardCheck, AutoBuildCheck and InvestCheck (whole cost), SaveFileCheck and ReadPathCheck (new keys).
- **Traces.** The baselines are pt07133 (default) and pte07133 (`--earth rotterdam`, `GameVersion` L6105). A slope, depth or price batch moves pte. pt stays byte for byte if the generated world draws none of it.

## Part 2: the Nova Scotia deposit check

**Verdict: both of Samuel's runs were the real Earth, and the deposits match the geology table exactly as it is written.** Neither `Geology.HAND` nor the sampling is wrong around Nova Scotia; the cause is the design of the weights.

**Which world it was.** Read-only copies of his saves:
- slot-01 "Halifax": 0.7.121, format 38, `worldKind` EARTH, 44.65/-63.6, seed 758809812, earth-1:b754…
- slot-02 "Scotia": 0.7.131, format 39, EARTH, 45.1695/-61.7246, seed 284719415.

slot-02's spot is not Pictou County. It is on the Eastern Shore of Guysborough County, 55 km from Antigonish and 85 km from New Glasgow: Meguma gold country, not coal.

**The fields have not changed between his builds and this one.**
- `git diff` 0.7.121→HEAD: `cellsPass`, `seaAt`, `Deposit` and the pool/cluster code in World are unchanged. I6/I7/I8 only priced coal, stone and copper and added `goodOf`; E7 added classes.
- The probe redrew both keys at 0.7.135. Every holding with fields in both saves matches the save to the site and the tonne:
  - Halifax: 4 holdings, 152 iron sites, 2.12 Gt.
  - Scotia: 2 holdings, 213 iron sites, 4.08 Gt.
  - Neither city owned any oil, coal, stone or copper. Depletion: iron 1.75 Gt (Halifax) and 69.7 Mt hematite (Scotia).
- landHoldings columns (31 wide): 0-6 side, place, level, x0, y0, x1, y1 (plots); 7 month bought; 8 price (k USD); 9 paid locally; 10-14 km² total/dry/fresh/sea/forest; 15-21 sites by Resource (IRON, OIL, STONE, COAL, COPPER, URANIUM, FOREST); 22-28 amounts; 29 offer id; 30 month listed.

**The provinces under Nova Scotia** (earth-1 geology at 0.25°; multiplier = w×Z, ×petroleum factor for oil):

| province | class (rules = hand) | land/sea samples | oil | iron | coal | copper |
|---|---|---|---|---|---|---|
| 5219 Appalachian Foldbelt | fold belt | 2156/1153 | 0.603 | 0.722 | 0.788 | 2.929 |
| 5221 Magdalen Basin | basin | 1911/1939 | 1.207, sea too | 0.361 | 1.577 | 0.220 |
| 5222 Fundy Basin | basin | 335/555 | 1.207, sea too | 0.361 | 1.577 | 0.220 |
| 5220 Sidney Basin | basin | 0/165, sea only | 1.207 | 0.361 | 1.577 | 0.220 |
| 5217 Scotian Shelf | shelf | 1/4134 | 1.810, sea | 0 | 0 | 0 |

- The Scotian Shelf's known petroleum (KWN_PET) is 0 in WEP_PRVG, so its petroleum factor is 1.0. Sable and Deep Panuke gas would have raised it to about 1.15.
- Controls: Alberta Basin oil 3.024 (31,101 MMBOE, factor 2.51); Superior and Pilbara/Gascoyne shields iron 2.888.

**Every class is right.** Halifax, Windsor and Lake Ainslie sit on 5219. Stellarton, Springhill and Joggins sit on 5221. Londonderry and Torbrook iron sit on 5222. Sable Island and Deep Panuke sit on 5217. HAND has no Nova Scotia row because the name rules already class these correctly.

The one data imperfection: the onshore Sydney coalfield (46.14, -60.19) rasterises into 5219 (coal 0.788), not 5220. The Sidney Basin polygon only shows at sea.

**Drawn fields: counts within 50 / 150 / 300 km**

| spot (seed) | iron | oil (on land/fresh + at sea) | coal | stone | copper |
|---|---|---|---|---|---|
| Halifax, slot-01 key (758809812) | 45 / 417 / 941 | 14 (8+6) / 155 (53+102) / 661 (145+516) | 2 / 11 / 20 | 0 / 37 / 91 | 4 / 7 / 7 |
| Halifax, seeds 1 / 42 / 20261012 | 53, 60, 92 within 50 | 5, 20, 5 within 50 (0, 1, 5 on land); 134, 154, 141 within 150 (48, 46, 31 on land) | 0–6 within 150 | | |
| Eastern Shore, slot-02 key (284719415) | 72 / 320 / 699 | 8 (0+8) / 182 (26+156) / 646 | 0 / 3 / 15 | 9 / 56 / 71 | none |
| Calgary, 3 seeds | 38–118 within 50 | 37–44 / 257–336 / 1031–1134, all on land | | | |
| Mesabi, 3 seeds | 476–608 / ~4,100 / ~16,000 | 0 / 0–5 / 19–41 | | | |
| Pilbara, 3 seeds | 456–604 / ~3,950 / ~12,700 | 0 / 0–3 / ~170 | | | |
| Generated, same 5 seeds, round each world's own site | 17–161 within 50 | 0–13 within 50; 7–105 within 150 | 0 within 50 | | |

Nearest fields to the slot-01 site: hematite at 17.3 km (28 sites, 391 Mt); oil at 24.1 km; coal at 33 km.

**What the geology alone predicts** (km² × base density × multiplier ÷ FIELD_SCALE):
- Within 150 km of Halifax: iron 400; oil 54.5 on land + 105.9 at sea (91.2 of it the shelf's); coal 7.1. These match the drawn counts above.
- Within 50 km: iron 66; oil 5.5 + 3.5.

So the placement follows the table. There is one second-order sampling smear: a cluster is centred uniformly within its 61 km cell, so the shelf's sea weight lands in nearshore water belonging to the fold belt. 7 of the 8 offshore oil fields within 50 km of the slot-02 spot are fold-belt water.

**Why "oil all around, iron everywhere": the weights.**
1. The base densities are the generated world's gameplay densities.
   - Iron is 0.2 old fields/km² × w × Z ÷ 10. In a fold belt that is one field per ~70 km², each ~20–30 sites of 15 Mt, so about 0.3–0.4 Gt a field.
   - Oil is one field per ~830 km² of fold-belt land.
2. The weights only rescale within a narrow range.
   - Fold-belt oil w = 1, so the Appalachians count like the Zagros.
   - Iron is 0 only on shelves.
   - Onshore Nova Scotia oil comes out 1/5 of Alberta's; iron is 1/4 of the Pilbara's.
3. Known petroleum only ever raises oil (factor ≥ 1). A province with none gets the class's full weight.
4. The shelf takes w = 3 over its whole sea: about 25,000 km² within 150 km of Halifax, about 91 fields.

**For the orchestrator.** The game's save folder on Windows is %APPDATA%\CityBuilderSim\ (`GameFiles.resolveDirectory()` L193-205; `APP_NAME` L54). Files are `slot-NN.json` with a `slot-NN-map.bin` sidecar, plus `autosave.json`. A save says which world it is in `worldKind` ("EARTH"; null means the generated world), `earthLat`, `earthLon`, `earthData` and `worldSeed`.

## What Earth 2 would have to change, file by file
- **tools/earth** (bake.py, FORMAT.txt, earthread.py, fixture.py, sources.py, ATTRIBUTION.txt, get-earth.bat, earth-2.sha256), plus Play/Build EXE .bat: the new layers (a slope class and a depth or shallow class per cell), the release earth-2 and its new fixture.
- **EarthData:** identity constant, DATA_FILE, MAGIC/FORMAT, identity prefix.
- **EarthTerrain:** the slope class in `draw()` and `classAt()`; `depthAt()` from data (`SHELF_WIDTH_M` and the coast-distance rule retire); a shallow-sea test.
- **World:** new classes (or a slope plane), `CLASSES`, and a predicate that separates "buildable" (flat, foothill) from "workable" (steep: mines, roads, rail).
- **EarthSite:** card lines (slope mix, depth), a refusal for snow/steep, a format floor like `ICE_FORMAT`.
- **CityLand, GridConversion, LandParcel:** areas by slope class, under a new saved key.
- **LandMarket:** `price`/`groundKm2` per class; `bestValue` judged per buildable km² per USD.
- **LandManager:** `allocate` uses flat, then foothill with a preparation cost; sector land by class; mines and roads drawing on steep ground.
- **Cost and choice code:** `BusinessInvestment.totalCostOf`/`groundCostOf`; `Game.buildFor`/`quoteBuild`/`bestOffer`; `BuildAdvice.landValue`/`orderLife`; `AutoBuilder.groundFor`/`shortcutOffer`.
- **Books:** `EconomyManager.landValueOf`/`assessedLandValueOf`; `SectorBooks.SectorMonth`; `SectorStatements.sheet` (LAND note 11) and `equity`.
- **Bridges:** a bridge order (buildings.json template plus `BuildingManager`), its path test in the model, a saved list (`DataSave`), honoured by `CityRuns.clear`/`DistrictPlan`/`TilePainter`.
- **Oil:** `isShallow` reads real depth.
- **Geology:** only if the Nova Scotia verdict is acted on (weights, a petroleum gate, Z/odds re-measured).
- **GameVersion:** `SAVE_FORMAT` 40, with the floor kept at 37 if generated saves load correctly.
- **Renderers:** `TileRaster.GROUND`, `LandMap` (words, legend, `earthWords` with slope and depth), `DistrictPicture`, `GlobePicture`, ui (FoundingScreen card, LandScreen, StatementView, MapView legend).
- **Harnesses:** the ones in Part 1, item 9.

## Risks
1. **Slope × cover.** Folding slope into class bytes multiplies the classes (forest on foothill, desert on steep). A separate slope plane means every class consumer reads two values: `tileCounts`, `km2Of`, `DistrictPicture`, `GlobePicture`, `TileRaster`.
2. **The model has no positions.** "Flat first" can only be an allocation order plus a price step on amounts, as bare-then-forest is now. Which plot a building stands on stays map-only.
3. **Roads and rail are capacity units in the model; runs and bridges are map-only.** Pricing per plot by slope needs either an amount-based expected slope mix or moving the runs into the model. A bridge order cannot read CityRuns; its test must run on `World` plus `CityLand`. Islands and B1 do not exist yet.
4. **Steep or snow not dry removes it from DRY and the dry pool.** Mines, highways and rail on "any ground" then need a pool of their own. The founding snap and test 1 (`SITE_DRY_PLOTS`) will refuse or move mountain spots.
5. **Save widths.** Widening `AREAS` changes all three record widths. `fromPurchaseRow`/`fromOfferRow` return null on the wrong width, so the floor would have to rise for every save, generated ones included. A new Gson key avoids that, and earth-1 Earth saves are refused by identity anyway.
6. **Traces and prints.** pte07133, GlobeCheck's `FIXTURE_PRINTS`/`FULL_PRINTS` and EarthTerrainCheck's shares and seed tests all move. Changing the Geology weights also moves Z, the odds and every Earth field.
7. **Budgets.** The extra layer read must keep within `TILE_MS` 0.3 ms, the 80 ms far node and screen, and the 100 ms globe frame. The 10 MB LRU and the 53 MB file will grow.
8. **Depth.** Real bathymetry changes which sea fields take platforms (`Oil.isShallow`, EarthFoundingCheck 5-6). A bridgeable shallow-sea threshold is a different, much smaller depth than the platform's 150 m.
9. **Data.**
   - earth-1 already uses GEBCO for the ocean mask (sources.py records "public domain… acknowledge… not for navigation"). I did not check that licence text against Samuel's rule; that is the data agent's job.
   - SRTM covers only 60N–56S, so slope further north needs another source. Not checked.

## Not checked
- The other harnesses: I ran no AllChecks, only the probe.
- docs/map is stale (0.7.123); the line numbers above are this tree's.
- I did not open Samuel's other saves (slot-03/04, autosave).
- KWN_PET for the Scotian Shelf is taken from the file. I did not check it against the USGS source.

# The land as a block grid: the design, 2026-10-07 (revised for Jerus's answers)

Read-only study of 0.7.64 plus its docs pass (`/home/claude/cbs`). It replaces spec-land §2.2's lanes of wedges; everything else in spec-land stands. Jerus decided on 2026-10-07:
- six offers a side;
- a new city's first offers in 120 m blocks;
- converted cities snapped to blocks, with the books following the map.

**Prototypes,** in `/home/claude/scratch-grid/proto/src/ham/citybuildersim/`, compiled against `/home/claude/cbs/build/classes`:
- `GridQuad`: owned ground as a region quadtree;
- `GridLand`: the grid, the frontier, the places and their offers;
- `GridSim`: buying evenly, on one side, or for best value, to 10B;
- `GridBench` and `GridExact`: timings and the cost of exact counts;
- `GridConvert`: the saves on the grid;
- `GridRender`: the PNGs.

The words probe is `scratch-grid/words/.../ui/GridWords.java`.

Outputs are in `scratch-grid/out/` (six a side: `six-*.txt`, `new6.txt`, `words.txt`, `exact-30000.txt`); renders are in `scratch-grid/renders/`. Seeds: 4127 (the default), 77 and 709115276 (Jerus's world). Benchmarks are medians of 7 runs on 2 shared cores; whole-run times are single runs.

## 1. Summary for Jerus
1. **A block** is a square of one fixed grid lined up with the map's 7.68 km districts: 120 m, 240 m, 480 m, 960 m, and so on, doubling. A city's blocks are the biggest of which six fit across it: 120 m when new, 960 m at your old city's size, 123 to 246 km at 10 billion.
2. **An offer** is a rectangle of whole blocks against the city's edge, one block wide and two deep (two by four when there's room). It takes in any finer steps beside it, so there's never a gap you can't buy.
3. **Six a side, never rerolled.** Each side has six places, left to right. Buy one and its place lists the nearest free ground in it: a notch first, otherwise the next rectangle straight out. The other 23 never move.
4. **Corners** go to the side whose diagonal they fall on, as now; their notches come up first, so the city fills out.
5. **A new city** is 0.30 km² of 120 m blocks and lists 19 offers, each 120 × 240 m (three of the old blocks): 4 or 5 fit on each edge. All 24 are listed by the 4th to 12th purchase.
6. **Size:** an offer is about 2 to 3% of the city. Your old city's size takes 195 to 344 purchases, and 10 billion takes 512 to 785 (today's lanes take 411 to 444).
7. **Shape:** bought evenly, the city fills 60 to 80% of its bounding box. Bought on one side only, it grows as a stepped fan (46 to 58%).
8. **Your live city** (133 purchases) is redrawn in 240 m blocks: every block you owned most of, plus every block holding one of your fields. Its dry ground grows by 0.13 km² (+1.8%) and its sea shrinks by 0.08 km², and the books follow the map.
9. **What stays exact:** your 13 iron sites, 161 Mt, your mines, and the two fields you own part of, site by site. What you paid stays on record, and no money moves for the redraw.
10. **Land reads in m² and km²:** under 0.01 km² in m² (a House is 76.2 m²), with ground prices per m².

## 2. The design
### 2.1 The grid
**Levels ★1.** A level-k block is 2^k plots a side, aligned to the world: block (bx, by) covers plots [bx·2^k, (bx+1)·2^k). The world is 753,664 plots across (23 × 2^15), so it divides into whole blocks at every level up to 15 (`MAX_LEVEL`, 983 km).

| k | 2 | 3 | 4 | 5 (tile) | 6 | 7 | 8 (district) | 11 (world cell) | 12 | 13 |
|---|---|---|---|---|---|---|---|---|---|---|
| side | 120 m | 240 m | 480 m | 960 m | 1.92 km | 3.84 km | 7.68 km | 61.44 km | 122.9 km | 245.8 km |
| km² | 0.0144 | 0.0576 | 0.23 | 0.92 | 3.69 | 14.7 | 59.0 | 3,775 | 15,099 | 60,398 |

**The city's level, for six a side ★2:** k = max(2, ⌊log₂(√P / 6)⌋), where P is the plots owned of every kind.
- This is the largest block of which six fit across a square of the city's area, so each of a side's six places is one or two blocks wide.
- `MIN_LEVEL` 2: 120 m (0.0144 km², 155,000 sq ft) is the smallest block at least one of today's 100,000 sq ft blocks, Jerus's "one block smallest".
- Measured (seed 4127, bought evenly), the level at each size of dry ground:

| dry ground | new | 3 km² | 30 km² | 89.6 km² | 300–1,000 km² | 3,000 km² | 10,000 km² | 30,000–100,000 km² | 176,000–300,000 km² | 5B and 10B |
|---|---|---|---|---|---|---|---|---|---|---|
| level | 2 | 3 | 4 | 6 | 8 | 9 | 10 | 11 | 12 | 13 |

**Coarse beside fine.** An offer's rectangle may cover ground the city owns, but its ground is only the plots not yet owned. So a coarse offer takes in the finer steps beside it, and outer rows and columns the city wholly owns are trimmed off.
- Any unowned block touching the city is frontier, in one side's quarter and one place's lane, so every gap can be offered.
- Bought evenly (three runs to 10B), enclosed ground was 0 at 45 of 48 marks; at the other three it was 0.23 to 0.27% of the city, and at 10B on seed 4127 it was under a standing offer.
- Bought for best value, the player leaves enclosed water (up to 5.1% of the city at times). It stays offerable, like today's lake lanes.

### 2.2 Sides, places and the offers
**Side and place ★3.** A block's side is read from its centre, seen from the founding site's centre: the larger of |dx| and |dy|, ties to North or South. Its place is ⌊(t+1)·3⌋, where t = across/out, so there are six lanes a side. Corner blocks on the diagonal go to North or South.

**The frontier of side s at level k:** the level-k blocks in s's quarter that are not wholly owned and touch owned ground (partly owned, or with an owned plot across an edge). A block is *taken* when it meets a standing offer's rectangle.

**Listing place (s, j) ★4:**
1. **Seed:** the free frontier block in lane j nearest the site, ties to the lane's middle. With none, the side's free block nearest the lane's middle. With none at level k, the same at k−1, but never below 2. With none at all, the place waits empty and is retried after every purchase.
2. **Rectangle:** w = round(√P / 6 / 2^k) blocks across, at least 1 and at most ⌊F_s / 6⌋ when that is 1 or more (F_s is the side's frontier blocks); d = 2w deep ★5. The columns are centred on the seed, the odd one toward the lane's middle.
3. **Clip:** while it meets a standing offer, drop the columns on that offer's side of the seed, or stop the rows below it. Trim the outer rows and columns the city wholly owns. Hold to 2:1.
4. **Contents:** its unowned plots, and every field whose centre plot lies in them, whole (spec-land ★12), priced as spec-land ★6.

**Buying** claims exactly the rectangle's unowned plots and lists the place's next offer. The other 23 never move, because standing rectangles never meet.

**The next in the same place ★6** is the lane's innermost free ground: the rectangle straight out from the one bought, or a notch first. A plain straight-out rule grew fingers (one side bought 100 times filled 42% of its bounding box, against 56%) and left 211,000 km² enclosed at 10B (`out/new-r0.txt`, `out/scale-even-L0-R0*`).

**Checked after every one of 4,533 purchases** (7 runs, six a side): 0 overlapping pairs, 0 offers not touching the city, 0 over 2:1, and 0 failed clips.

| six a side, bought evenly / for best value | seed 4127 | seed 77 | seed 709115276 |
|---|---|---|---|
| all 24 first listed at purchase | 4 / 12 | 5 / 10 | 4 / 12 |
| purchases to 89.6 km² dry (Jerus's old city) | 305 / 205 | 238 / 195 | 344 / 213 |
| …to 5B (879,666 km² dry) / 10B (1,759,331) | 744, 780 / 545, 563 | 703, 754 / 483, 512 | 749, 785 / 562, 577 |
| bought, share of the city: mean (median) | 2.18% (1.88) / 2.92% (2.79) | 2.20% (1.88) / 3.19% (3.02) | 2.19% (1.91) / 2.80% (2.63) |
| empty places, mean over all purchases | 0.01 / 0.40 | 0.02 / 0.06 | 0.02 / 0.14 |
| bounding-box fill at 10B | 71% / 30% | 65% / 43% | 67% / 25% |

**The offers standing** (even):
- the founding 19 are 0.0288 km² each, 9.5% of the city;
- from 1 km² to 10B they are 0.23 to 8.5% of the city, with a mean of 1.9 to 3.8% at every mark;
- at 10B they are 15,099 to 483,184 km².

**Bounding-box fill:**
- bought evenly: 61% at 30 purchases, 72% at 300, and 60 to 80% at every mark past 75;
- on the North side only: 46 to 58% from purchase 28 to 10B (562 purchases), a stepped fan;
- for best value, the city follows the dry land (17 to 66%).

**Compared:** ten a side gave offers about 1% of the city and 1,282 to 1,920 purchases to 10B (`out/scale-final-*`).

### 2.3 Owned land at scale
**The tree ★7.** Owned ground is a region quadtree over world plots: one root of 2^20 plots, with int arrays for children, state and holding. A node is one of:
- EMPTY;
- FULL, one holding (four equal FULL children merge);
- MIXED;
- OWNED, wholly owned by several holdings, so `cover()` answers ALL without descending.

`fill(rect, h)` claims only unowned plots. So the tree is never saved: it is rebuilt at load by replaying the centre's rectangles, then the purchases in order. Queries: `owner(x, y)`, `cover(level, bx, by)`, `unowned(rect)`, `tileFlags(tx, ty, out)` and `leaves()`.

| measured (`out/six-bench-*.txt`) | 18 km² (157 purchases) | 10B even, 3 seeds (754–785) | 10B best value (563) |
|---|---|---|---|
| tree nodes / FULL leaves | 877 / 525 | 3,373–3,649 / 2,429–2,650 | 2,825 / 1,798 |
| `owner()` a plot, random / at the edge | 49 / 42 ns | 27–31 / 18–22 ns | 28 / 31 ns |
| a mixed tile's 1,024 flags, from the leaves (plot by plot) | 0.23 µs (14 µs) | no tile mixed: 0.02 µs | 0.04 µs |
| rebuild from the rectangles | 0.10 ms | 0.38–0.44 ms | 0.27 ms |
| save rows (Gson, 31 values a row) | 187 B a purchase | 205–222 B; 0.16–0.17 MB; offers 5.1–5.7 KB | 218 B; 0.12 MB; 4.2 KB |

Spec-land's lanes took 47 to 56 ns to test a plot. A whole run to 10B took 1.5 to 2.3 s, including every listing.

**The books follow the map ★8.** A holding's five km² (all, dry, fresh, sea, forest) are its plots counted one by one with `World.tileTerrain` (measured 60.2 µs a tile; 124 to 133 ms for an offer of 2,048 tiles).
- Exact for every offer up to level 9: a 2 × 4 offer of 15.36 km blocks is 2,048 tiles, which covers cities under about 34,000 km² in all.
- Past that, an offer is listed from 64 × 64 samples of its unowned part, because exact counts cost 0.3 to 0.5 s at levels 10 and 11 (8,192 tiles, measured) and 8 to 31 s at level 13 (derived at 60 µs a tile). Measured at 31,000 km² dry (`out/exact-30000.txt`, 24 offers), sampled dry against exact: within 0.4% on 8 land offers, −1.6% on one, and 3.4 km² and 11 km² short on two coastal ones. So there, the map and the books can differ by that much.

### 2.4 The map
L2 draws one building per model building, on each tile's free plots counted plot by plot (`fixL2-notes.md`). On the grid:
- **Ownership flags:** `CityMap.tileOwnership()` becomes `cover(5)`, then `tileFlags()` from the leaves (0.02 µs a whole tile, 0.23 µs a mixed one) in place of 1,024 lane tests. `measure()` loses its per-plot ownership test.
- **Whole districts:** from level 8 (a city over about 2,100 km² in all) every new purchase is whole districts. A district is then all owned or none: its room is simply its dry plots, and partly owned districts remain only in the older inner city. From level 5 (about 33 km²) an edge is whole tiles; below that, edges run along straight block lines.
- **`syncLand()`** recounts exactly the districts under the new rectangle; L2's deal stays inside a district's own tiles.
- **`ownership()`/`boxOwnership()`** become `cover(8)`; `outerReach()` becomes the owned bounding box; `holdingOf()` becomes `owner()`; `laneIndex()` goes; the sidecar's land stamp becomes the holdings' count and a hash of their rectangles.
- **A deposit site's ground is room ★9,** as the code has it since 0.7.64: sites are room, the painter builds on them last, and a mine takes its own site's plots (`CityMap.measure()`, `TilePainter`). L2's notes' "off sites" described the painter's order. The model's owned ground includes the sites, so a map that left them out could not place every model building. Nothing changes on the grid.
- **`LandMap`:** `pick()` is `owner()` plus 24 rectangle tests; offers are rectangles; the owned outline is horizontal and vertical runs from the leaves.
- **`MapView`'s overlay:** hatched rectangles numbered by place, a crisp stepped outline, and faint block lines where blocks are at least 6 px.

### 2.5 The land office and the units
**Rows,** as now, one per standing offer, left to right facing out:
- "lane" becomes "#", the place from 1 to 6;
- "size" stays the offer's area, and its tooltip gives the blocks: "1 × 2 blocks of 120 m · 0.0288 km² · 0.0273 dry, 0.0015 fresh" (306 px; the longest, at 10B, is 403 px);
- an empty place shows one muted row: "no room on this edge yet: it lists when the city grows here" (283 of 645 px);
- a side listing fewer than six names its count in its chip ("North · 4", 43.5 px).

**Units ★10.**
- **Areas** the player reads are in m² below 0.01 km², three figures and grouped ("76.2 m²", "7,430 m²"), and in km² from there ("0.0301 km²", "1,760,000 km²"), through one `LandManager.areaWords()`.
- **Ground prices** read per m² ("D$20,882/m²", "US$7.53/m²"). An offer row keeps its price a dry km².
- **The investors' words** become "no land - needs 743 m², 301 m² free"; the playtest's `t-house` trace carries them.
- **Measured** (`out/words.txt`, 101 strings, the shipped Plex faces, the docs pass's slots at 1,389 × 868): 0 over. Every slotted string is no wider than the one it replaces, except the grouped "1,760,000 km²" (+17 px, inside its slot); the investors' wrapping line is up to 5 px wider. The press is ≤ 171 of 225 px, the LAND head ≤ 185 of 206 px, and GROUND FREE and the prices ≤ 109 of 162 px.
- **Dead code** goes: `LandScreen.sqFt()` (no caller) and `LandManager.COST_GROWTH_PER_BLOCK` (unread).

### 2.6 Converting the saves (SAVE_FORMAT 32)
**Format 31** (0.7.57 whole fields; 0.7.58 to 0.7.63 shared site by site; 0.7.64 whole) ★11:
1. **Read the old ground** with `LegacyLand`, today's lane geometry kept read-only. A save written by 0.7.58 to 0.7.63 holds each field site by site (each site with the ground holding its own centre); any other holds whole fields.
2. **Snap one level finer than the city's offers** (240 m for Jerus's city, whose offers are 480 m). The deciding points are each whole field's centre plot and each site of a field held in part, each marked as the save's or not. A block holding both kinds splits into its four children, down to the plot. Otherwise a block is the city's:
   - when a point in it is the city's;
   - not when a point in it is another's;
   - else when the save owned half its plots or more.
3. **One holding,** the converted centre: the snapped blocks. Its sites and amounts are the save's, summed exactly, with E as saved. Its five km² are the drawn plots (★8), so the books follow the map. The purchases fold into it, as `restate()` does.
4. **A field held in part** keeps its owned sites (`landPartFields`: kind, cell, index, owned sites). Its others go a site at a time to the offer whose ground holds each site's plot.
5. **The price paid ★12.** The converted holding keeps the save's purchase records as history (Jerus's: 133 purchases, US$125.2M listed, D$164.3M paid) and `landBlocksPurchased`.
   - No money moves. The snap redraws what was bought rather than buying or selling, so ground it adds is not charged and ground it takes is not refunded.
   - With the larger books the city's crowding falls 1.8%, and the offers listed at conversion are priced on that.
6. **24 offers are listed afresh,** the one-time exception to "never rerolled". `freshRights` is carried, and the map is rebuilt canonically.

**Format 30 and older:** J1b's site search; a centre of blocks of the city's level, the last block split down to the plot, holding the save's dry ground to within a plot; iron as the save's (at least its mines), with a legacy field when the world put none in the centre. The other kinds are the drawn plots. `restate()` draws the same.

**Measured** (`out/six-conv-*.txt`; each save loaded by 0.7.64 first). The books change in this direction:

| save | as saved | snapped at | dry | fresh | sea | forest | tree | fields | ms (draw + offers) |
|---|---|---|---|---|---|---|---|---|---|
| jerus-1007a autosave (0.7.63, m416, 133 purchases, iron 13 sites 160.6 Mt, 3 mines) | 8.30 km² (7.22 dry) | 240 m | +0.130 km² (+1.81%) | +0.015 (+5.1%) | −0.078 (−9.9%) | 0 | 253 nodes / 71 leaves | 0 change hands; 3 blocks taken for its fields, 1 split; 2 held in part (6 of 8 sites its own, 0 on the wrong side) | 7 + 11 |
| slot-03 (0.7.63, m212, the same land) | the same | 240 m | the same | | | | | the same | 15 + 12 |
| jerus-old (0.7.49, m1851, iron 155 / 508 Mt) | 107.8 km² (89.63 dry) | centre, 960 m | +1 plot (+0.0009 km²) | 0 | +0.380 (+2.1%) | −1.71 (−12.6%) | 177 / 37 | iron the save's | 31 + 19 |
| city600 (0.7.38) | 3.20 km² (3.03 dry) | centre, 240 m | +0.0002 | −0.0095 (−5.7%) | 0 | −0.029 (−2.7%) | 141 / 22 | legacy iron field | 3 + 12 |
| city2400 (0.7.38) | 40.98 km² (33.29 dry) | centre, 960 m | +0.0007 | 0 | +0.772 (+10%) | 0 | 149 / 22 | iron the save's | 15 + 24 |

Forest's timber is its area × 13,700 m³, so it follows its area (nothing cuts timber yet). Snapping Jerus's city at 120 m would add 1.07% dry and 14% sea; at 480 m, +4.5% dry and +56% fresh (`out/six-snap-live-L*.txt`).

### 2.7 The test player on the grid
The rules stay; only "lane" reads "place":
- **`keepGroundAhead()`:** `bestOffer(room)`, the most dry km² per US$, never mostly sea, the nearer on a tie, at most a tenth of the cash a look;
- **`ironWhenNeeded()`:** `cheapestWith(IRON)`, from the cash or the funding page's bond;
- **`build()`:** the cheapest offer covering a shortfall;
- **room past 0.85:** best value;
- **every 13th stop:** best value under a tenth of the cash;
- **empty places** are skipped.

Offers of 2 to 3% of the city against today's 3 to 4% mean slightly more purchases a look. The founding's first look (m31) needs about 11 offers of 0.0288 km² where it bought 9 lanes' bands (0.327 km²).

## 3. Build order
**Every batch:** one Opus implementer with a fresh context (about 2 hours), in `/home/claude/cbs`, with a version bump and a `GameVersion` entry.
- **The gate:** both builds silent; the suite green except HealthCheck; BuildMenuCheck; `tools.Stale` firm at 0; `endings-check.sh`; the playtest as stated, then rerun byte-identical.
- **Port the prototypes;** don't retype them. `tools.Maps` is left to the docs pass.

**M1: the grid, pure (0.7.65).**
- **Files:** `LandGrid` (from `GridQuad`: `fill`, `owner`, `cover`, `unowned`, `tileFlags`, `leaves`, `census`, `replay`; `MIN_LEVEL` 2, `MAX_LEVEL` 15, `FACE_BLOCKS` 6); `GridOffers` (from `GridLand`: side and place, frontier, seed, rectangle, clip, trim, 2:1; `PLACES` 6).
- **GridCheck asserts:**
  - `fill` and `owner` match a brute-force raster on 200 fixtures; four equal leaves merge; `cover` is exact for MIXED and OWNED; replay rebuilds the tree node for node;
  - over a 600-purchase city bought evenly, after every purchase: no standing pair overlaps, every offer touches the city, whole blocks at ≤ 2:1, and the other 23 unchanged;
  - a notch is seeded before ground farther out;
  - `owner` ≤ 100 ns (measured 18 to 49).
- **Trace:** byte-identical.

**M2: the conversion, pure (0.7.66).**
- **Files:** `LegacyLand` (today's lane geometry, moved out of `CityLand`, which delegates to it, so nothing moves); `GridConversion` (from `GridConvert`: the snap, the deciding points, part fields by version, the books from drawn plots, the format-30 centre to the plot).
- **ConversionCheck** on copies of the five saves asserts §2.6's table:
  - sites and tonnes to the bit, and no field changing hands;
  - part sites on the right side;
  - the books equal the drawn plots;
  - the format-30 dry ground within one plot.
- **Trace:** byte-identical.

**M3: the land on the grid (0.7.67, SAVE_FORMAT 32).**
- **Files:**
  - `CityLand`: holdings as rectangles on a `LandGrid`, books as plot counts (★8), part fields;
  - `LandParcel`: an offer is side, place, level, x0, y0, x1, y1, the five km², sites, amounts, price and month listed;
  - `LandMarket`: six places, null when there's no room; `offerKm2`, `sizeMultiple` and `unitKm2` go;
  - `LandConversion`: M2's conversion at load for formats ≤ 31, `restate()` on blocks;
  - `CityMap` and `LandMap`: §2.4's reads;
  - `DataSave` and `Game`: `landCentreRects`, the 24-wide `landCentre`, the 31-wide `landHoldings`, 24 `landOffers`, `landPartFields`, `landConverted`; `landLanes` and `landPurchases` are read only to convert;
  - `LongPlaytest`'s words.
- **Premises:**
  - LandCheck 1: "owned = STARTING_SQ_FT" → "the centre's drawn dry plots" (+1.7%: 315 plots, 3,051,569 sq ft);
  - LandCheck 5, 5b, 5e, 5f, 13, 17, 20, 21, 22 and 23 → six places, rectangles, the innermost rule, the level rule, rows, the books to the plot;
  - also MiningCheck 2, BuildAdviceCheck 7, MapCheck 1's fixture, ScaleCheck 2, SaveFileCheck, ReadPathCheck and NewGameCheck.
- **New assertions:** W conserved through purchase, extraction and conversion; each field held once, or by sites; a converted save, reloaded, identical; an empty place lists once there is room.
- **Trace moves from month 1,** because the founding ground is 1.7% larger. The founding listing becomes 19 offers of 0.0288 km² where it was 40 of 0.0093 to 0.167 km²; purchases stay near today's (195 to 344 to 89.6 km²); the first iron may come earlier, since a place reaches the founding field 26 plots south about two offers out. Re-baseline `pt0767`, then run the ensemble (label `0767`) against `0764`.

**M4: the units (0.7.68).**
- **Files:** `LandManager.areaWords()` and price words per m² (§2.5) through `ui/LandScreen`, `ui/SummaryScreen`, `ui/BuildScreen`, `BuildCard`, `Inbox`, `ui/ConstructionScreen`, `BusinessInvestment` and the console logs; the dead code goes.
- **Checks:** a words probe from `GridWords` (all 101 within their slots).
- **Trace:** `t-house` moves in its words only ("needs 743 m²" for "needs 8,000 sq ft"); every number is byte-identical. Re-baseline `pt0768` with each diff shown to be words; no ensemble, since the model is unchanged.

**M5: the land office and the map view (0.7.69).**
- **Files:** `ui/LandScreen` (§2.5's rows, chips, empty place and (i)); `ui/MapView` (§2.4's overlay); `ui/BuildScreen` (the shortcut's "North 3").
- **Checks:** a words probe; MapCheck on `MapView`'s pure half (outline runs, rectangles in frame); BuildMenuCheck.
- **Trace:** byte-identical. Jerus's PC check covers the look and a 60 fps pan.

## 4. ★ decisions
1. **World-aligned power-of-two blocks,** 120 m to 983 km. They nest in tiles, districts and cells.
2. **Six blocks across the city** set the level, so each place is one or two blocks wide.
3. **Sides and places** are today's quarters and lanes, at six lanes a side.
4. **Innermost free ground in the lane first:** compact, and it leaves no gap that can't be offered.
5. **Offers w × 2w,** the long side outward: the largest six a side allow at 2:1.
6. **The next in the same place** is the lane's next free ground.
7. **A quadtree,** rebuilt from the rectangles and never saved.
8. **The books are the drawn plots,** counted one by one up to level 9, sampled past it.
9. **A deposit site's ground is room,** built on last; a mine takes its site.
10. **m² below 0.01 km², km² above, prices per m².**
11. **Format-31 saves snap one level finer than their offers;** fields decide; one converted holding.
12. **No money moves on conversion;** what was paid stays on record.

## 5. Still to decide, and risks
1. **Exact books past level 9:** counting a 10B city's offers plot by plot would take 8 to 31 s each. They are sampled there (off by up to 1.6% of an offer's dry ground on land; a few km² on coasts), unless Jerus wants the count run in the background.
2. **Places stuck on water:** a best-value player leaves water standing, so up to 0.40 places wait empty on average.
3. **The units change the playtest's words,** in M4.
4. **JavaFX costs are unmeasured,** as in spec-land §5. The overlay is now rectangles and runs, which is cheaper than polygons.

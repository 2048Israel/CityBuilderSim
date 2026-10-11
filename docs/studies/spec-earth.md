# The world as planet Earth, and buying land from the map: a design study

*2026-10-08. A read-only design study. Samuel: "you are going to do the ui mockup if nescessary and the design study, no code touching, but you can read all you want, basically its the earth stuff" (Jerus's list after the grid, `after-the-grid.md` §6: "make the world planet Earth and pick a spot on it. The seed then decides only building placement and the like"), "also i want so that on the map itself you can click buy that parcel of land instead of having to exit the map and buy it".*

*The code was read, not changed, from the public repository at 0.7.79 (commit f85bcfa, 2026-10-08 13:30): `World`, `Founding`, `ui/FoundingScreen`, `ui/MapView`, `ui/LandScreen`, `Game`'s land purchase, the class maps in `docs/map/`. The prototypes (Python, in `earth-prototype.py` beside this note) measured the data budget on a real global land mask, worked the projection, and painted real coast with the detail rule. Data licences come from a research pass (sources at the end). The mockups are the artifact "Earth & Map Study": ten boards in the house style. Figures marked (est.) are estimates; the rest are measured or derived, with the sum shown.*

*Samuel answered every question the same evening (§5, §5.1). The text and the boards follow his answers.*

---

## 0. In one page

**The Earth fits the world as it is.** The world is already a flat square the size of the Earth (368 cells of 61.44 km, 511.2M km² against the Earth's 510.1M). Put the Earth on it with an **equal-area projection centred on the founding site**:
- the city stays at the middle of the square, and every constant (`World.CELLS`, the plot, tile, district and cell) stays;
- **every km² the land office sells is a real km² of the Earth**, everywhere on the map;
- shapes are true to 0.15% at 500 km and 0.6% at 1,000 km;
- the square holds **91.1%** of the planet. The rest (8.9%, the far side past 13,905 km) is beyond the world's edge, and the square's corners are off the planet, read as sea as "beyond the world's edge" already is.

**The seam in the code is narrow.** Outside `World` itself, the model reads the world through about ten methods (`tileTerrain`, `terrainAt`, `regionTerrain`, `seaTheta`, `totals`, `foundingX`/`Y`, `fieldsInCell`, `cellTotal`, `searchSites`, `seed`; counted with grep, harnesses aside) and finds it through `World.of(seed)`. An Earth world can stand behind the same surface, keyed by its spot instead of a seed alone.

**The coast is cheap; height is what costs.** A global land mask at 30″ (about 1 km), run-length coded, **measured 0.71 MB**. Each halving of the cell costs 2.4 times more (measured 120″ → 60″ → 30″). So a 15″ coast (about 460 m) is about 1.7 MB, and even 3″ (90 m) about 13 MB (est.). The whole pack, with lakes, rivers, height, forest, geology and names, is **about 21–38 MB** (est.), inside the 20–60 MB first guessed.

**The data decides, the seed frays.** A plot is sea where the data's land share, smoothed, plus the seed's noise falls under one half. Where a data cell and its neighbours agree, the noise cannot flip it. On a district beside the test site **95.7% of plots match their data cell**; the rest lie within a cell of the shore.

**The seed still matters.** It lays every field of ore and oil (densest where the Earth's real ones are), picks which plots of a forest cell are trees, frays the shore, and lays out the town. Same spot and seed, same city.

**Founding.** Always on the Earth, for now. The player clicks any spot, on a coast or inland. The game moves it to the nearest dry plot with room for the founding blocks and says how far.

A site card says what is round it and under it: how far the nearest lake or river is, with a line to it, and the nearest field of each resource. The seed moves to an UNDERGROUND field.

**Old saves become unloadable** (Samuel's call), with a save-format bump. A city keeps the Earth pack it was founded on, by name, so a coast never moves under a city.

**Buying from the map.** Today a click on an offer in the expanded map picks it and the head says "— Esc to buy it in the office". Instead, a click opens **the offer's card on the map**:
- its size, ground, deposits, price and what it leaves in cash;
- the pay toggle;
- **Buy** (Enter).

A purchase redraws the city in place and lists the place's next offer. Short of money, the card issues the land office's 20-year bond for the gap and buys, or opens the funding page and comes back to the map. **Shift+drag** boxes an area's offers, **Shift-click** adds or drops one, and Ctrl+A takes every offer, all into one Buy. Every word and every purchase goes through the land office's own code, so the map and the office can never disagree.

**Order.** Buying from the map is small, independent and screen-only: it can go first. The Earth is four batches after it (§4).

---

## 1. What is there today

**The world** (`World`, 1,129 lines). Every value is a pure function of one seed, through SplitMix64 and StrictMath, so a world is the same on every machine.
- **Terrain:** octaves of value noise, with the sea level set so 71% of samples are sea.
- **Classes:** lakes and forest at fixed percentiles; five classes, GRASS, FOREST, FRESH, SALT and SAND.
- **The founding site:** a spiral search for a site that passes four tests:
  - dry for 600 m;
  - 60% land within 5 km;
  - the sea within 2.1 km but not within 1.35 km;
  - an iron field within 2 km.
- **The founding lake and river:** a random walk, so every new city has fresh water beside it.
- **Fields:** a Poisson count per cell for each resource, at the resource's density times the cell's land.
- **Cost:** built on first use in about half a second; the world's totals are counted once, in about 30 ms.

**The founding page** (`ui/FoundingScreen`). THE WORLD is a seed field, a dice, and "The same number makes the same coast, lakes and ore." A new page rolls a new seed.

**The map** (`ui/MapView`). One canvas:
- small (600 × 400) in the land office, where a click picks a side or an offer and its row lights;
- expanded over the whole window (Expand), where drag pans, the wheel zooms, a hover card names what is under the pointer, and a click picks.

The expanded head then says `picked: North 3 · 0.0288 km² · US$462k — Esc to buy it in the office` (`MapView.picked()`). The expanded pane covers the window's header, so the cash is not on screen while the player looks at land.

**Buying** (`ui/LandScreen`). A row's Buy calls `buyOrFund(ids)`:
- if `Game.landNeedsFunding(ids)`, it opens `showLandFunding(ids)`, the funding page sized to the gap;
- else it calls `Game.buyLandParcels(ids)` and redraws.

Every word on a row is worked out without a node (`row()`, `sizeWords()`, `perDryKm2()`, the tags), so another screen can reuse them.

---

## 2. The Earth

### 2.1 The projection
**Lambert azimuthal equal-area**, centred on the founding site, north up, on a sphere of the Earth's mean radius (6,371.0088 km). The city's plot (0, 0) is the site, exactly as the world's middle is today. In a projection centred on a point, shapes stretch with distance from it and areas never change.

| from the city | shape error | a distance reads |
|---|---|---|
| 100 km | 0.006% | 0.001% short |
| 500 km | 0.15% | 0.03% short |
| 1,000 km | 0.62% | 0.10% short |
| 2,000 km | 2.5% | 0.41% short |
| 5,000 km | 17% | 2.5% short |

- **The square and the planet.** The projection maps the whole Earth to a disc of radius 12,742 km. The world's square (half-side 11,305 km) holds 91.1% of the planet: everything within 13,905 km along the axes, and farther toward the corners. The 45.6M km² it leaves out are the far side of the planet from the city. The corners, 46.7M km², are off the disc; they read as SALT, as plots off the world do today.
- **The world's totals** are counted over the square, so "of the world's iron" means the square's.
- **Why equal-area and not equidistant:** the land office sells km², and blocks are squares of the world's grid. Under an equal-area projection a 120 m block is 0.0144 km² of real ground anywhere. Distances near the city, the only ones the model uses (pipelines, ports), are true to 0.1% within 1,000 km.
- **Cost:** a tile is 960 m. Across it the projection bends by about (0.96 km)² ÷ 6,371 km ≈ 0.14 m, against a plot of 30 m, so a tile needs the projection at its four corners only, then bilinear interpolation (est.). The trigonometry is StrictMath's, as everything in `World` is.

### 2.2 The Earth pack
One read-only file in the game. It is built once, offline, by a tool in `tools/`, never by the game.

| layer | grid | source | licence | size |
|---|---|---|---|---|
| sea and land | 15″ (460 m) | ETOPO 2022 | CC0, US government | ≈ 1.7 MB (from the measured 30″) |
| lakes | 15″, as a third class | HydroLAKES v1.0 (1.4M lakes ≥ 10 ha) | CC BY 4.0 | ≈ 1–2 MB (est.) |
| rivers, with widths | lines | GRWL v01.01: river centrelines with widths measured at mean flow (rivers about 30 m wide and more) | CC BY 4.0 | ≈ 5–10 MB (est.) |
| height and sea depth | 2′ (3.7 km), 8-bit, finer steps near sea level | ETOPO 2022 | CC0 | ≈ 10–18 MB (est.) |
| forest share | 2′, 4-bit | MODIS MCD12Q1 (or CGLS-LC100 tree cover) | no restrictions (MODIS) | ≈ 2–5 MB (est.) |
| where fields are likelier | 1°, a byte per resource | USGS MRDS, petroleum provinces and copper (public domain); GEM coal mines (CC BY 4.0); GLiM 0.5° (CC BY 3.0); uranium written by hand | as listed | 0.45 MB |
| place names, for search | points | Natural Earth populated places | public domain | 0.6 MB |
| **the pack** | | | | **≈ 21–38 MB** |

- **Measured:** GLOBE's 30″ global land mask has 772,857 land/sea changes along its rows. Coded as runs it is 1.09 MB, and 0.71 MB compressed. At 60″ and 120″ it is 0.30 and 0.12 MB, so a halving costs 2.4 times. That fits a coastline of fractal dimension about 1.25.
- **Avoid:**
  - OpenStreetMap's land polygons: ODbL, and data a player can pull out of the game is likely a derivative database under share-alike;
  - GSHHG: LGPL;
  - MERIT DEM and FABDEM: non-commercial;
  - IAEA UDEPO: no licence stated;
  - HydroRIVERS: free to sell, but its licence asks for more than credits (an end-user licence with its terms, indemnifying WWF, records open to WWF for two years, and WWF may end it), which fails Samuel's test (E6).
- **Credits:** each CC BY source needs a line on a credits screen (HydroLAKES, GRWL, GEM, GLiM). No fee is payable for any source in the table. Natural Earth and NOAA ask for none and should get one anyway.
- **Finer coast later (★E2):** a 3″ (90 m) coast would make the city's own first blocks real rather than seeded. It is about 13 MB for the sea alone (est.), plus lakes. Its sources (ESA WorldCover water, CC BY 4.0, or the Copernicus DEM water mask with its notice) mean processing tens of GB once. It would ship as a second pack for new cities only (§2.7).

### 2.3 The ground at a plot
For a plot, the projection gives a latitude and longitude. Then:
- **Land share:** the sea/land layer, bilinear between the four nearest data cells: 1 deep inland, 0 at sea, between near a shore.
- **Sea:** land share + 0.9 × (the seed's noise − ½) < ½. The noise is the game's own value noise, octaves from 480 m to 60 m. Its reach is ±0.45, so a plot whose data cell and neighbours agree is never flipped; only the ground within about a cell of a shore takes the seed's shape. The prototype used 0.9. In the game it is a dial with the sentence above it, and a harness asserts the "never flipped" half.
- **Fresh water:** inside a lake of the pack, or within a river's half-width of its line. The width is GRWL's, measured at mean flow. The founding lake and river walk go: a city has the Earth's rivers or none.
- **Beach (SAND):** land just above the shore line, as the elevation band does today.
- **Forest:** the 2′ cell's forest share s says how much; the game's existing forest field says which plots. A plot is forest where the field is above its own (1 − s) quantile. Each cell's forest share is the data's, and the forest keeps today's look.
- **Grass:** the rest.
- **Ice (E14, decided):** a sixth class from MODIS's permanent snow and ice: land, never dry, never built on, never a founding site. It counts in a holding's whole area only, and the painter gives it its own white.

The other five classes are today's. Nothing downstream (the land's five areas, water, the painter, the map) needs to know the ground came from the Earth, but for ice.

### 2.4 Fields: the seed, weighted by the Earth's geology
Each resource keeps its density and site size (`Resource`). A cell's mean count becomes the density × the cell's land × p ÷ p̄:
- p is the pack's prior for that resource in the cell's 1° square;
- p̄ is its mean over the world's land.

So the world's total of each resource is unchanged in expectation; only where it lies moves. The whole-field rule, SEA_REDRAWS (oil may lie offshore) and depletion are as today. Geology alone does not make fields bigger or rarer. E15 (decided) does that: each resource's field count comes from the real number of known deposits, each field bigger by the same factor, the world's tonnes unchanged (§5.1). The test site in northern Sweden lies in iron country, so its seed is likelier to put iron near it.

### 2.5 Founding on the Earth
Boards 1 to 3.
- **THE WORLD row:** the Earth only, for now (E1). It shows the spot (a map thumbnail, the nearest place's name, its coordinates, what is round it), "Choose the spot ›" and "A random spot". The seed moves to **UNDERGROUND**, with its dice. A new page opens on a random spot with fresh water near, as it opens on a random seed today.
- **The picker:** the Earth in an equal-earth projection, with search (Natural Earth's names). A click zooms in. The founding search spirals out from the click (`searchSites` already spirals, cell by cell) and stops at the first plot that passes the one test left.
- **The test site:** a click off Luleå found a site 1.53 km away after 37 tries. It has 70.6% land within 5 km and the sea 1.38 km away; 63% of that land is forest.
- **The site card:** the test; the sea and the nearest lake or river, each with its distance and direction and a line to it on the map; the forest; the nearest field of each resource the seed put round it; Found here. "Move it toward the water" re-runs the search from the nearest fresh water.
- **The tests (E3, E4, decided):** one is left: the site and eight points 600 m round it are dry ground, not ice, so the founding blocks fit. The coast tests and the iron test go. A coastal city simply buys sea; an inland one can't build ports or desalination until it does.

### 2.6 Fresh water
Rivers and lakes from the pack are FRESH ground, so the fresh-water cap (121,600 units a month per km² of owned lake or river) works unchanged. What changes is that a new city is no longer sure of a lake. Today's founding walk always puts one 1.9–2.7 km away, and a default city starts owning 0.011 km² of it. The test site's nearest river is 25 km off in the demo data, so its first water plant would stand idle until it buys river ground or builds a desalination plant. Decided (E13): the site card warns and points to the nearest water; the founding is still allowed.

### 2.7 Saves
- **New fields in the founding record:**
  - the spot (latitude and longitude of the site);
  - the pack's name ("earth-1").
  The seed stays.
- **`World.of()` keys on all three** for an Earth city, as it keys on the seed today.
- **An old save** is refused (E10): every city founded before the Earth, his slots and Jerus's included. The conversion code goes with it (E16).
- **The pack's name is part of the city.** A later pack (a finer coast, corrected data) is offered to new cities only, and the game keeps every pack a save names. So a coast never moves under a city that stands on it.
- **`SAVE_FORMAT`:** bumped with the batch that writes the spot (E11, decided), so an older build refuses an Earth city and this build refuses an older city.

### 2.8 What the Earth does not bring
- **No climate:** latitude changes nothing in the model in this design. Any effect later should be a mechanism (heating in winter, a growing season), never a multiplier on top. Samuel ruled out made-up factors (E12, decided).
- **No other cities, borders or countries:** names are only for finding a spot. They are not drawn on the city's map.
- **No gameplay from height:** it shades the far map and gives the sea's depth, which the oil and ports study asked for (Q7, a depth per sea block). Slope rules can come later.

---

## 3. Buying from the map

Boards 4 to 8. The city is the default founding (21 blocks of 120 m) plus three purchases: 27 blocks, 0.3888 km², 640 people, May 2006, on the real coast near Luleå at 10 px a plot. Its 17 offers keep the grid's rules: none overlaps, every one touches the city, each is 1 × 2 blocks outward, and each block is on its own side (checked in the prototype).

The ground price is the game's formula:
- 640 people on 0.3888 km² is 1,646 a km²;
- crowding × 2.07, the world level × 1.031;
- US$7.53 × 1.031 × 2.07 = US$16.04 a m²;
- so an offer of 0.0288 km² is US$462.0k, D$326.7k at D$0.7072 a dollar.

### 3.1 The design
- **Hover (board 4):** the hover card as today (the ground, whose it is, the price, the fields), with a last line in blue: **"Click to buy it here"**.
- **Click (board 5):** the offer lights pink and **its card** opens beside it:
  - the place and its tag (BEST VALUE, NEW, MOST ORE, MOSTLY SEA, as the rows);
  - its blocks and km²;
  - the ground bar (dry, fresh, sea) and "room for 38 houses" (28,800 m² ÷ the House's 743 m²);
  - the price in the toggle's money, large, with the dollars listed and the ground a m² under it;
  - a dry km² (D$11.3M);
  - cash before → after (D$18.70M → D$18.37M);
  - the pay toggle;
  - **Buy North 2 · D$326.7k**, with Enter;
  - "Open in the land office ›".
- **Esc** closes the card; a second Esc closes the map, as today.
- **The head:**
  - shows the cash and the pay toggle, because the expanded pane covers the window's header;
  - loses "— Esc to buy it in the office";
  - its hint becomes "click an offer to buy it".
- **Bought (board 6):** the blocks turn the city's, the edge redraws, and a receipt shows by the ground: "Bought North 2 · D$326.7k · 0.0288 km², all dry · the city is 0.4176 km² now · North 2 lists its next ground". The place's next offer is drawn tagged NEW, and the head's cash moves. The map already repaints on a purchase (`MapTiles.version()`), so this is the existing path.
- **Short (board 7):** South 2 holds a whole iron field:
  - the price is US$37.58M: ground US$0.46M, plus 90 Mt × US$0.40 × 1.031;
  - that is D$26.58M against D$18.70M cash, short D$7.88M;
  - the card shows the field (6 sites, 90.0 Mt) and rings its sites on the map wherever they lie;
  - **the card issues the bond** (B1, decided): "Issue a 20-year bond and buy · D$7.9M". It is the land office's own first offer: `Game.BUILD_BOND_YEARS` (20), the gap rounded up to `BUILD_BOND_GRANULE`. Under it are the quote's yield and coupon, the coupon a month and the face due at the end (the board's rates are illustrative);
  - beside it, "6-month note" (the office's second offer) and "All the ways to pay ›", the funding page, which comes back to the map at the same view (B5);
  - paying from the vault, the same two come as the dollar bond and note, while the foreign window is open.
- **A box (board 11):** Shift+drag draws a box (B7, recommended); every offer whose free ground it touches is picked. Ctrl+A or "Select every offer" in the head picks all of them. The card sums them; short, it offers one bond for the whole gap.
- **Several (board 8):** shift-click adds or drops an offer. The card becomes "3 offers":
  - North 1, 2 and 4 as chips, each with its ×;
  - 0.0864 km², room for 116 houses;
  - D$980.2k, cash → D$17.72M;
  - **Buy 3 offers**, each bought as its own row's Buy would buy it, in the order picked.
- **The small map** in the office stays as it is: a click picks, and the row beside it has the Buy.

### 3.2 For the implementer (est. names)
- **`MapView.click()`:** while expanded, an offer under the click opens the card (shift: toggles it in the picks) instead of only calling `picker.picked()`. A click on the city or bare ground closes the card.
- **The card's words** are `LandScreen.row(parcel, …)`'s and `sizeWords()`'s, the same record the office's rows draw from. For several, `LandScreen.next()`'s pattern sums the picks.
- **The bond in the card** is `LandScreen.offers(ids)`'s first offer, its quote and its action (`handleLongBondForCash`, then `buyOnTheLoan`), so the card and the funding page issue the same paper.
- **The box:** with Shift down, a press-and-drag draws the box instead of panning; on release, every listed offer whose free ground meets it joins the picks (`LandMap`'s offer shapes already give each offer's free ground).
- **The card's button** calls `LandScreen.buyOrFund(ids)`. Two changes there:
  - it must not collapse the map when it buys;
  - when it opens the funding page from the map, the page must return to the map, and `buyOnTheLoan()` re-expands it at the frame it left (`MapFrame` kept).
- **Enter:** the expanded canvas already takes keys while focused (`+`, `-`, `0`). Enter on it buys the card's offer or picks. No global key handler.
- **No new model state.** The picks live in the view and are dropped on a month, a load or a collapse; a pick no longer listed (bought elsewhere) falls out.
- **Check by eye on the PC,** as all of `MapView` is (it has no harness). Worth adding to MapCheck: the card's figures equal the office row's for the same offer.

---

## 4. Batches

| batch | what | traces |
|---|---|---|
| **M6** (first) | buying from the map: the card, Enter, shift-click, Shift+drag and Ctrl+A, the head's cash and toggle, the bond in the card, the funding page and back | identical: screen only |
| **E1** | the pack tool (offline, in `tools/`) and the pack "earth-1", with each resource's real deposit count (E15); a reader for it; EarthCheck: the pack's land share is the Earth's 29%, the sea class holds at 1,000 sampled points against the source, the reader is the same on every machine | identical |
| **E2** | the Earth as a second world behind `World`'s surface (the projection, the ground rule and ice, fields with priors at the real counts, totals); pure. WorldCheck's sections for both kinds; the detail rule's "never flipped" assertion | identical: nothing founds on it yet |
| **E3** | founding on the Earth: the record's new fields, the save and load, `SAVE_FORMAT`, older saves refused (and the conversion code deleted, E16), the founding page's row and the picker, the site card. `Founding.defaults()` moves to one fixed Earth spot with iron and fresh water near, so the harnesses and the playtest play the game players get | every trace moves |
| **E4** | the map's far views from the pack (relief shading, depth), the credits screen | identical |

---

## 5. Decisions (Samuel, 2026-10-08, 22:39)

**The Earth**
1. **E1. Always the Earth, for now.** No "imagined world" choice yet ("no, later but for now always earth"). The seeded generator stays in the code for later.
2. **E2. A 460 m (15″) coast in the first pack.** ("sure 460m")
3. **E3. No iron test: real geology decides.** ("drop it, let real geology") How big and how rare fields are is the new E15.
4. **E4. The player chooses the location; no coast needed.** ("it doesnt have to be on a coast, the player just chooses location") The founding tests drop to one: dry ground, not ice, for 600 m round, room for the founding blocks. The game moves the spot to the nearest plot that passes and says how far (§2.5).
5. **E5. Fields follow real geology,** the world's totals unchanged.
6. **E6. Rivers: the best data that is free to sell with credits only.** Samuel: HydroRIVERS "only if i can sell the game without paying a license just mentioning the credits ... otherwise natural earth".
   - **HydroRIVERS fails that test.** There is no fee and selling is allowed, but its licence (HydroSHEDS v1, Appendix A) asks for more than credits:
     - an end-user licence at least as protective, with no reverse engineering;
     - indemnifying WWF;
     - records kept two years and open to WWF;
     - and WWF may end it at its discretion.
   - **GRWL in its place** (Global River Widths from Landsat, Allen & Pavelsky, CC BY 4.0: credits only; E6b, decided). Its river centrelines carry widths measured from satellite images at mean flow, so a river is drawn as wide as it is.
7. **E7. Today's fresh-water rule:** the cap a km² of owned lake or river.
8. **E8. Height for the map's shading and the sea's depth only.**
9. **E9. Place names only to search;** none drawn, no other cities.
10. **E10. Old saves become unloadable.** ("old saves can become unloadable no issue") That includes every city founded before the Earth: his slots and Jerus's.
11. **E11. Bump `SAVE_FORMAT`.**
12. **E13. Fresh water: warn and point, never forbid.** The site card says how far the nearest lake or river is and draws a line to it on the map. A city can still be founded with none near ("still let you build a city with fresh water no where close if you still choose").
13. **E14. Ice is a sixth ground class:** land, never dry, never built on, never a founding site.

**Buying from the map**

14. **B1. A click opens the offer's card, and the card can issue a bond when the cash is short.** ("click brings up menu, and to issue bond if doesnt have cash")
15. **B1b. Drag to select on the map:** take a whole area's offers at once, or pick specific ones. ("you should be able to drag and select in the map to buy all the land parcel or specific ones") How the drag shares the mouse with panning is B7.
16. **B2. Enter buys the card's offers.**
17. **B3. Shift-click adds or drops an offer.**
18. **B4. Only listed offers.**
19. **B5. The funding page stays one click away and comes back to the map.**
20. **B6. The office's small map stays as it is.**

### 5.1 The last five (Samuel, 2026-10-08, 22:47: "all your recommendations")

21. **E6b. Rivers from GRWL** (CC BY 4.0, credits only, widths measured at mean flow). Natural Earth's are not used.
22. **E12. No climate in the first version;** later only mechanisms (winter heating, a growing season), never a multiplier.
23. **E15. Fields as rare as the Earth's.** The pack tool counts the known deposits of each resource in the USGS data it is built from. That count sets each resource's fields in the world, and each field grows by the same factor its count fell, so the world's tonnes stay. Today's figures, for scale:

    | resource | a field per | mean field |
    |---|---|---|
    | iron | 5 km² of land | 2.5 sites, 38 Mt |
    | oil | 50 km² | 2.5 sites, 0.38 Mt |
    | stone | 100 km² | 2.5 sites, 34 Mt |
    | coal | 500 km² | 2.5 sites, 16 Mt |
    | copper | 2,000 km² | 2.5 sites, 0.76 Mt of metal |
    | uranium | 5,000 km² | 2.5 sites, 505 t |

    For the implementer:
    - `World.MAX_SITES` (512) must rise with the factor, or the biggest fields are cut off.
    - Whole fields are sold whole, so a field's price grows with it.
    - The founding economy loses iron near most cities. The site card shows the nearest field of each resource, and ore is imported until the city reaches one. Watch for that in the ensemble.
    - Uranium has no USGS count in the stack; its count is written by hand from published geology.
24. **B7. Shift+drag draws the selection box;** plain drag still pans. The box takes every offer whose free ground it touches. "Select every offer" in the head, or Ctrl+A, takes them all.
25. **E16. The conversion code goes** in E3's batch, with the save-format bump: `LegacyLand`, `LandConversion`, `GridConversion`, their checks and `conversion-saves.json`.

Nothing is open.

## 6. Found on the way

1. **The expanded map hides the cash.** The pane covers the window's header, so while looking at land the player cannot see what the city has. The mockups put the cash and the pay toggle in the map's head.
2. **"— Esc to buy it in the office"** (`MapView.picked()`) is the line this replaces.
3. **Every seeded city has fresh water by construction.** The founding walk always puts a lake 1.9–2.7 km away, and the default city owns 0.011 km² of it. On the Earth that is no longer so (E13).
4. **"Of the world's iron"** (the office's world line) becomes the square's iron: 91.1% of the planet.
5. **Rivers and coast must come from one family of data.** In the prototype, Natural Earth's Lule river stops about 25 km inland, where GLOBE's mask already calls the estuary sea. HydroRIVERS is built on HydroSHEDS' 15″ grid and HydroLAKES is linked to its network, which is why E6 recommends them with a 15″ coast.
6. **A purchase lowers the next listing's price.** The price is crowding by people per owned km², so ground bought lowers it: 640 people on 0.4176 km² prices ground at about US$13.45 a m² against US$16.04. Whether an offer's price is struck when it lists or re-struck each month decides what the next North 2 shows (to check: `LandMarket`'s listing).

---

## Sources

- NOAA NCEI, ETOPO 2022 (15″, 30″, 60″), CC0: https://www.ncei.noaa.gov/products/etopo-global-relief-model · https://catalog.data.gov/dataset/etopo-2022-15-arc-second-global-relief-model
- GEBCO grid and terms (fallback): https://www.gebco.net/data-products/gridded-bathymetry-data
- Natural Earth terms (public domain): https://www.naturalearthdata.com/about/terms-of-use/
- GSHHG (LGPL): https://www.soest.hawaii.edu/pwessel/gshhg/
- OSM land polygons, ODbL FAQ: https://osmdata.openstreetmap.de/data/land-polygons.html · https://osmfoundation.org/wiki/Licence/Licence_and_Legal_FAQ
- ESA WorldCover (CC BY 4.0): https://esa-worldcover.org/en/data-access
- Copernicus DEM licence: https://documentation.dataspace.copernicus.eu/APIs/SentinelHub/Data/DEM/resources/license/License-COPDEM-30.pdf
- HydroLAKES (CC BY 4.0): https://www.hydrosheds.org/products/hydrolakes
- HydroRIVERS and the HydroSHEDS licence (TechDoc v1.4): https://www.hydrosheds.org/products/hydrorivers · https://data.hydrosheds.org/file/technical-documentation/HydroSHEDS_TechDoc_v1_4.pdf
- MODIS MCD12Q1: https://developers.google.com/earth-engine/datasets/catalog/MODIS_061_MCD12Q1
- USGS MRDS: https://mrdata.usgs.gov/mrds/
- USGS petroleum provinces: https://data.usgs.gov/datacatalog/data/USGS:60ad2fd7d34e4043c850edb3
- USGS global copper database: https://www.usgs.gov/publications/spatial-database-global-assessment-undiscovered-copper-resources-chapter-z-global
- GEM Global Coal Mine Tracker (CC BY 4.0): https://api.globalenergymonitor.org/catalog/metadata/coal-mines
- GLiM 0.5° (CC BY 3.0): https://doi.pangaea.de/10.1594/PANGAEA.788537
- IAEA UDEPO: https://nucleus.iaea.org/Pages/udepo.aspx
- GLOBE land mask, as packaged by `global-land-mask` (the prototype's data): https://pypi.org/project/global-land-mask/

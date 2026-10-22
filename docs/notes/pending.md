# Pending for the project record

Lines the docs pass could not write to the project itself. The next Cowork
session moves them over and empties this file. Newest at the top. It holds
todo lines for the claude.ai project, which the sessions that write them
cannot reach. The changelog is no longer the project's: since 2026-10-20 it
is `changelog.md` beside this file, and its blocks that waited here moved
there.

## 0.8.11 to 0.8.20 - for `claude/todo.md`

- Section 0, under the manual's entry: its headline numbers moved - the build 0.8.20, SAVE_FORMAT 43 (XR1; the
  oldest loadable 43: older saves are refused), 126 harnesses (127 run with the playtest); 143 buildings and the
  twenty-four sectors as they were. Pages the manual carries that the range changed: Borrow as an auction, with bills
  of 1 to 12 months, bonds of 2 to 50 years in lines and the amortizing bond, the cap chips, the term loan and the
  underwriting spread gone (CD2); the curve from the dial the market expects (CD1); the D$ as a float on parity's
  trend, Trade › The currency (XR1); the freight screens, Build's Infrastructure and Freight tabs and Policy › Taxes ›
  Port dues (FR4); the map's avenues, crescents and curves (CL4), rail curves and real-length trains (FX5).
- What each batch left open is under Pending in QUEUE.md (FR4-C, EG1-C, CL2-C, CL3-C, CD1-C, FX6-C, CD2-C, FX5-C,
  XR1-C, CL4-C; TD1 left nothing).
- The docs pass of 2026-10-22 flagged, for a code batch or Jerus (it changes no code):
  - Government's debt page still names its kinds "Notes", "Serial bonds" and "Term loans"
    (GovernmentScreen.paperKinds()' strings), where CD2 sells bills, bonds and the amortizing bond - CD6's screens?
  - Freight's operations page draws Haulage's and the quay's figures twice: FR4's division strip, and below it the
    ITS OWN FIGURES grid of Freight.ownLines(), which still lists them under the railway - meant, or a leftover?
  - The suite ran in 800 and 917 s on the PC on 2026-10-22 (no other batch's JVM), against the "about nine minutes" README
    and CLAUDE.md give; a slower PC that day, or a suite grown by seven harnesses since?
  - The 2026-10-19 pass's four flags (Freight.getKept(), MapView's legend rows, MapCheck's trackSurplus() labels,
    BuildingManager's RAIL example) are still open; the Rail build card's owner was FR4's, done.

## 0.8.4 to 0.8.10 - for `claude/todo.md`

- Section 0, under the manual's entry: its headline numbers moved - the build 0.8.10, SAVE_FORMAT 40 (FR2, the
  oldest loadable 40), 143 buildings (the Truck Depot 139, the Heavy Truck Plant 140, the Rail Depot 141, the Bulk
  Berth 142), 119 harnesses; the sectors still twenty-four, Freight in Rail's place, with its Haulage and Ports
  divisions; a new dial, the port dues (`TaxPolicy.portDues`, FR3).
- What each batch left open is under Pending in QUEUE.md (FR2-C, FR7-C, FR3-C, FR5-C, FR6-C, FX3-C, TM1-C).
- The docs pass of 2026-10-19 flagged, for a code batch (it changes no code):
  - `Freight.getKept()` may read low since FR6: the railway's bill on the city's own bulk is in what it subtracts
    (rHaulage) but not in the cross-border truck bill it subtracts from (TODO(docs) in Freight.java).
  - The Rail build card still says "Owned by the rail sector" (ui/BuildScreen); the sector is Freight - FR4's?
  - MapView's plane legend has 52 entries since FR1's train against LEGEND_ROWS 17, so the last starts a fourth
    column before the zones; checked by eye.
  - MapCheck's two printed trackSurplus() labels still say "what the runs could not lay" (since FR7: the track no
    train's way uses); BuildingManager's RAIL banner works its example on a Freight Line of 100,000 t, the
    balance file's is 200,000 (since 2026-09-16).

## Jerus's answers of 2026-10-18 - for `claude/todo.md`

- CONFIRMs closed ("rec all"): FR0's, FR1's and FX2's stars, all as built; I14 ★3, ★4, ★6, ★7 as built; I12 ★2
  (copper in tonnes of the good) as built. Left: whether I12 ★5's three lines read well, with his eye check.
- CONFIRM, the zone palettes: one, the map's per sector, and Zoning reads it - batch ZT1, queued (docs/queue/ZT1.md).
- CD1's design call: the curve of spec-city-debt §3.2 (§8.1 records it). research-1900 §11 and research-design §8: all rec.
- Queued: CC1-M (the map lost on a save) as its own batch, and I2b (the whole-cost test on every order). Settled: the
  plantation's size (the Woodlot), a 10B city's books (sampled), spec-scale ★3 (batch I's crowding premium).
- QUEUE.md's FX1-2 row removed: `batch/FX1-with-preflight` (e821d7f) was already in main.

## 0.8.1 - for `claude/todo.md`

- ~~FR0, the map's network~~ - done 2026-10-18 (0.8.1); what it did not build is FR0-L under QUEUE.md's Pending
  (access roads D17, sidings into the bulk works D14, the cloverleaf's and trumpet's ground D19, kerb corners).
- CHECK (Jerus, on the PC): FR0-C - the world line and its label on the expanded map (JavaFX, never run in the
  cloud), the rail, yards and junctions at the near view, beside docs/queue/FR0-notes/*.png; and his calls on the
  stars (the gateway arm grown first, ★3; the yards' looks, ★4-5; TRANSPORT's colour #5e7d8c, ★6).
- ~~FOUND: EarthTerrainCheck 8's Earth screen bound red on the cloud machine~~ - settled 2026-10-18: 27.8 ms on the
  PC in FR0's ship suite against MapCheck.SCREEN_MS's 80; a shared VM's timing, not the batches'.
- FOUND (the FR0 docs pass): tools.Stale does not see a record's components, so a comment naming one reads as an
  unresolved member (gemini-notes.md's Prose fixes says so; CityMap's Type.campus(), sites(), shore() and sea() are
  four of its soft leads). Teach Stale the record header, or leave the soft list as it is.
- FOUND (the FR0 docs pass): MapCheck's sections 1, 8 and 14 print titles joined with + (1's with MONTHS), so
  docs/harnesses.md shows no heading for them - 8 and 14 want one literal each, 1 a reading in tools.Maps (the
  I14 pass's line named 14 alone). And MapCheck.CPU_STEP_MS's sentence calls 0.1 ms "a twentieth of the quickest
  stamp timed", where the comment above it gives the quickest stamp as 0.4 ms: a quarter, or another stamp?
- FOUND (the FR0 docs pass), TileRaster, none of it behaviour: its header draws a track plot below 4 px in
  RAIL_DARK, but raster()'s plotColour() still fills RAIL_LINE there (the view never asks raster() below 4 px; a
  harness can) - which is meant?; RAIL_DASH is read by nothing since 0.8.1; FR0's three one-line dial banners at
  the top take in older dials under them (JUNCTIONS over TICKS_FROM to LINES_FROM, YARDS over SMALL_MOST to
  PACKED_DIM), a move of declarations; and CityMap's RailPicture, railPicture(), wagonTracksOf() and
  freightStation() sit inside THE OIL AT SEA, between Pipe and AtSea (left for FR1, which reworks them).
- Section 0, under the manual's entry: the build is 0.8.1; the harnesses are 112 (113 run with the playtest), 108
  at 0.8.0; save format still 39 - for the manual's next edition (a milestone).

## 0.8.0 - for `claude/todo.md`

- ~~I14 (UI), 0.8.0: the map and the release~~ - done 2026-10-13 (0.8.0): with it I5's 20-site works drawn as one
  mine, I6's coal mines and preparation plant and I7's Dimension Stone Quarry drawn as plain MINING, and I9's ★10
  (coal's, stone's and copper's first types placed nowhere); W1's note stays open (below).
- CHECK (Jerus, on the PC): the materials on the map - a pan over an industrial city, a zone's tint - beside
  docs/queue/I14-notes/png/ (QUEUE.md Pending, I14-C; fixI14-notes §8).
- CONFIRM: a plantation drawn on a cell's 31 x 31 plots of its 105 x 105 (I14 ★3); the arc furnaces and mini-mills
  not gathered into steel's campus (★4); sited works grey in the far views (★6); no toggle for the zones' tint (★7).
- W1's walled street piece over a dense iron cluster stays open (QUEUE.md Pending, W1-P).
- CONFIRM: the map's zone tint is LandMap.ZONE_COLOURS, a palette apart from ZoningScreen.ZONE_COLOURS, so a zone's
  colour on the map is not its colour on Policy > Zoning (the I14 docs pass) - one palette, or two on purpose?
- FOUND (the cloud orchestrator, 2026-10-13): EarthTerrainCheck 8's Earth screen bound (MapCheck.SCREEN_MS, 80 ms)
  went red on the cloud machine mid-session, on main without I12 too (81 ms at d4e12dc; 105-109 ms on the batches'
  trees), having passed in the session's first two full runs: a timing on a shared VM, not a batch's. On the PC it
  should pass; if it does not, the bound or the Earth screen's raster wants a look. MapCheck 14's title is two joined
  strings, so docs/harnesses.md shows no heading for it (the docs pass, left: outside I14's change).
- Section 0, under the manual's entry: the build is 0.8.0, the release that finishes industry (save format still 39,
  harnesses still 108) - for the manual's next edition (a milestone).

## 0.7.143 - for `claude/todo.md`

- CHECK (Jerus, on the PC at 1,389 x 868): the nine materials Operations pictures by eye, beside
  docs/queue/I12-notes/png/ and fixI12-words.txt (QUEUE.md Pending, I12-C).
- CONFIRM: copper drawn in tonnes of the good, not of copper contained as the mockups say (I12 ★2: the model holds no
  copper content); the three lines over a works (I12 ★5).
- Section 0, under the manual's entry: the build is 0.7.143 (the manual's header says 0.7.135; save format still 39,
  harnesses still 108); the nine materials industries' Operations pages open on a picture now, for its next edition.

# AP2 notes: slot 9's slow months and the slow paused window

Jerus, 2026-11-22: "save slot 9, look at it, ui was getting slow even when paused, and also well
months were slowin down drastically". Built by Claude (Opus) on `batch/AP2`, cut from origin/main
efca2c5fd (0.9.190). The evidence (the slot's copy, the drivers, the profiles) is in the AP2 side
session's notes folder, `AP2-evidence/`.

Slot 9 (saved 2026-11-22 20:03 by 0.9.189, format 61): month 1130, 17,279,459 people, 321,345
buildings, 151 districts, automatic building on. Main (0.9.190, HC1, floor 62) refuses format 61,
so the slot was measured on tag 0.9.189 with AP2's six changed files compiled over it (none of them
differs between 0.9.189 and efca2c5fd).

## 1. What made the months slow

12 months after 2 warm, headless, under JFR: mean 5,907 ms, median 3,052, max 12,929. GC was
negligible. The slow months (1131, 1135, 1136, 1139, 1140, 1143: 8-15 s) are the months the city's
ground moves:

- **The highways' link searches** (`CityRuns.openLink` -> `linkWay` -> `LinkSearch.run`). A piece
  whose link failed waits for the ground to move (`stuckAt`), so every purchase reruns it. Two or
  three searches a month, all failing, 3.46 million plots visited (one at the `LINK_SEARCH` cap of
  2,097,152). 81% of a search was `EarthTerrain.draw`: the runs' ground (`CityMap.RunGround`) kept
  `RUN_TILES_KEPT` = 256 tiles, and a breadth-first ring wider than that drew the same tiles again
  at every step. A search month drew about 90,000 tiles for 4,046-4,384 distinct ones.
- **`CityLand.centreStamp()`**, read by `RunGround.version()` at every plot the search reads,
  hashed every centre block each time: 21% of a land month once the tiles were kept.
- **The bridge surveys** of the islands notice (`Inbox.takeIslands` -> `BuildAdvice.shown` ->
  `linkOf` -> `Game.bridgeSurvey` -> `BridgeGround.mayHold`). The notice re-surveys each unlinked
  island whenever the land's stamp moves, and each survey asked `World.idsNear` again for every
  tile of its corridor. That is about 30% of a land month and about 1 s of a quiet one.
- **The auction's bidders** (`OutwardInvestment.idleWealth` -> `mayHold` ->
  `BusinessDebtManager.getPrincipal`) scanned the bank's 366 loans once per sector, at every auction
  a works' financing quotes: 3.4% of a quiet month, and a quarter of the Build Overview's advice.

## 2. What made the paused window slow

AP1's SaveProbe, with a paused section added (`PausedProbe`), on slot 9 with the clock stopped:

- Idle frames are fine (p50 16 ms, p95 17 ms) on every screen.
- A screen drawn again with no month: Build 935 ms (1,905 ms to show it), Sectors 155 ms, Land
  41 ms, People 35 ms, the rest 17-27 ms.
- Build's cost is the Overview's model. Headless, `CityNeeds.measure` takes about 4 ms and
  `BuildAdvice.suggest` about 590 ms for three suggestions. Most of that is the works' financing:
  `WorksValue.relief` -> `financing` -> `AutoBuilder.bondQuote` -> `Game.runAuction` (the yield
  curve, the bidders). The rest is `EquipmentFit` and `NewUnit`.
- On the FX thread, `MapView.tick` -> `islandHatch` -> `LandMap.unlinkedRuns` reads `idsNear`
  (now kept) and the tiles' terrain.

So the paused window is slow when a screen is drawn again, Build above all, not while it sits.

## 3. What changed (every figure the same to the bit)

- **`CityMap.RunGround`**: the three tile-keyed maps keep `tilesKept` tiles, which is
  `RUN_TILES_KEPT` (256), or `RUN_SEARCH_TILES_KEPT` (8,192, a new dial) while a link's search runs.
  `searching(false)` trims them back to 256, letting go the tiles read least recently. `ownedAt`'s
  clearing on a version change is untouched. What is kept changes, never what is read.
  Hooks for the harness: `runTilesHeld()`, `runTilesDrawn()`.
- **`CityRuns.Ground.searching(boolean)`**: a new default method that does nothing.
  `linkWay()` wraps its search in `searching(true)` / `finally searching(false)`.
- **`CityLand.centreStamp()`** is kept between the centre's changes. `centreEdits`, transient
  and never saved, is raised after each change to `centreRects`: the founding's rings, the
  founding water, a restatement's `take()`, and `restore()`. The hash is kept with the count it
  was read at. The count is read before the blocks and raised after they change, so a kept hash is
  never older than its count, even read from another thread. `centreStampDrawn()` is the hash
  afresh.
- **`EarthTerrain.idsNear()`** is kept a tile (`IDS_KEPT` = 100,000, cleared when full, under a
  lock, as `resolved` is). The landmass layer is frozen per world, so a tile's ids never change.
  The array is shared, and no caller writes into it (all of them binary-search or iterate it).
  `idsNearDrawn()` is the read from the cells.
- **`BusinessDebtManager.getPrincipalsByKey()`**: every sector's `getPrincipal()`, in
  `Sectors.KEYS` order, from one pass over the loans. Each sector's loans are summed in the book's
  order from nothing, then its bonds are added, as `getPrincipal()` adds them, so each figure is
  the same to the bit. `OutwardInvestment.idleWealth` and `allotmentWeights` read it once a call,
  in place of a scan per sector; `bidOf` for one sector is unchanged.
- **The checks**: a new section in each of `GatewayCheck`, `LandCheck`, `LandmassCheck` and
  `HoldersCheck`, the harness that owns each mechanic (5). No new harness, so `AllChecks` is
  untouched.

## 4. The calls

- ★1 **Keep more only while a search runs.** Raising `RUN_TILES_KEPT` for good would hold 8,192
  tiles of terrain, about 8 MB, plus the ownership maps, on every city all the time. Raising it for
  the search alone holds them for a second, and the month's other readers keep their 256.
  Recommended, and taken.
- ★2 **A count, not a clear.** The centre changes at four places, all inside `CityLand`, and
  `centreRects` never leaves it writable. A count raised at each is the smallest change. A later
  change site that forgets the count would keep a stale stamp, so `LandCheck`'s new section restates a
  centre and reads it again.
- ★3 **Not narrowed: the stuck search itself.** After the tile fix, a piece's failed link search
  still reruns at every land purchase (about 1.2-1.7 s, in 6 of the 14 months measured). Backing it
  off (not again until the ground near the piece moves, or after N months) changes when links
  open, so it is a behaviour call for Jerus, not a performance fix. Left for Jerus (7).
- ★4 **Not narrowed: the landmass re-measure.** When a landmass is linked (624 and 1630 in 1135,
  2801 in 1139: islands joining, not an oscillation), `CityMap.landmassPicture` re-measures all 151
  districts, about 1.7 s. Only the districts on the islands that changed would need it, but
  `measure` feeds `syncLand`'s counts and the site index, and proving the narrower pass exact is a
  batch of its own. Left for Jerus (7).
- ★5 **Not narrowed: the Overview's auctions.** The principal pass took the Overview's model from
  about 590 ms to about 540 ms; the rest is the auctions' quotes. Holding them across draws while
  paused would need a stamp of everything an auction reads (rates, tills, the book). That is a
  batch of its own. Left for Jerus (7).

## 5. The checks

Each kept read gets a section in the harness that owns its mechanic. Every kept figure is compared
with the read afresh (the `*Drawn` hooks), and every fixture causes what it tests:

- **`GatewayCheck`**, "a link's search keeps every tile it reads, and reads the same ground (AP2)",
  17 assertions. A square city owning 576 tiles, over `RUN_TILES_KEPT`. Two passes, one plot a tile:
  the usual keep draws all 576 again; inside `searching(true)` the second pass draws none, and the
  tiles' keep and the ownership keep each hold 576. `searching(false)` gives each keep back to 256
  or fewer (the raw ownership keep too, `runOwnedHeld()`: empty here, since it is filled only while
  the city holds a picture of its landmasses), with the same values, also against `World.tileTerrain`. A `linkWay()` on a counted ground opens and closes the search once and finds
  the same way as on a plain ground.
- **`LandCheck`** 31, "the centre's stamp is kept between its changes and moves with them (AP2)",
  10 assertions. A founding, its copy, a restatement to more ground and one back to less: the kept
  stamp equals `centreStampDrawn()` each time and moves with each change.
- **`LandmassCheck`** 14, "a tile's landmass ids are kept, the same as read from the cells (AP2)",
  8 assertions. On main's Earth fixture, the 9,409 tiles round Rotterdam: the same ids as read from
  the cells, the same array asked twice, tiles with one id and with two. The clear at `IDS_KEPT` is
  forced, and the answers after it are still the same.
- **`HoldersCheck`** 16, "what each sector owes, from one pass over the book, is getPrincipal() to
  the bit (AP2)", 11 assertions. `theBusinesses()`'s city after 24 months: every sector's figure
  equals `getPrincipal()` in its raw bits, and stays equal in each of the 12 months after (25 loans
  in 3 sectors at the last). Both sides of `owed > 0` occur. The city issues no business bonds, so
  a face is laid over its own bond market for Retail, beside its loans, and for Construction, alone.
  Both figures, bonds added, are `getPrincipal()`'s to the bit.

Each mutation of the model turns its section red (`AP2-evidence/mutations/`):

| mutation | FAILs |
|---|---:|
| `centreEdits` not raised | 5 |
| the ids' key a wrong tile | 4 |
| the book summed backwards | 1 (Industry, last bit) |
| no trim after the search | 1 |

The playtest's trace (`-Dplaytest.trace`, its eight CSVs over 2,405 months) is byte for byte the same
on main (efca2c5fd) and on AP2: main 196 s, AP2 191 s. The md5 of each, the same on both:

| file | md5 |
|---|---|
| t-bank.csv | d2bdbc41eca459a745494dc64d21ee5c |
| t-bonds.csv | 22fb7f806269e714190407c2da4739ae |
| t-borrow.csv | 614258128cd4f1adedfe38318d310f82 |
| t-build.csv | 3b2166462c519211374dedfa0a6a290c |
| t-house.csv | dc1f903ac782cb7c9d41a5343b81eef0 |
| t-labour.csv | f6c4ce6c8d7d0fd8f5e66804a6447f02 |
| t-month.csv | 42cea75746b2cbdd2be1395063fd181f |
| t-pop.csv | 13769b31a38a590e1613c9827aa987c8 |

The pick (`tools/pick.sh origin/main HEAD`): 81 of 244 harnesses. `./batch-check.sh --pick` exits 3
(compile OK, maps regenerated, Stale 0 firm). The only reds are main's known reds, in
OrderDoorCheck, FundCheck, HoldersCheck, ReloadCheck and KeptCheck. Not ALL, so no dry run is owed. Review: "needed - threading:
+volatile in CityLand.java, +synchronized in EarthTerrain.java". The count and the kept pair are
volatile, and the pair is one array written whole, so any thread reads a count with the hash taken
at it. The ids' map is under a lock, as the terrain's other memories (`tiles`, `reliefTiles`,
`resolved`) are, since a world's terrain is read from more than one thread.

## 6. The months on slot 9

Before, 0.9.189; after, 0.9.189 with AP2's six files. 12 months after 2 warm, the same laptop, JFR on:

| | mean | median | max |
|---|---:|---:|---:|
| 0.9.189 | 5,907 ms | 3,052 ms | 12,929 ms |
| AP2 | 2,537 ms | 2,354 ms | 4,801 ms |

| month | 1131 | 1132 | 1133 | 1134 | 1135 | 1136 | 1137 | 1138 | 1139 | 1140 | 1141 | 1142 | 1143 | 1144 |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 0.9.189 (s) | 15.0 | 3.7 | 3.1 | 2.5 | 12.9 | 10.8 | 2.5 | 1.6 | 12.1 | 8.1 | 1.9 | 1.8 | 11.3 | 2.2 |
| AP2 (s) | 6.4 | 2.7 | 2.6 | 2.2 | 4.8 | 3.2 | 1.9 | 1.2 | 4.4 | 2.4 | 1.4 | 1.2 | 3.7 | 1.5 |

- Every month's population is the same in both runs. The whole log (the auto-builder's orders and
  their kr figures, the notices, the market's lines) has the same md5 once the timing lines are
  taken out: the world market's solve time is the only line that differs. That md5 is
  aa3edd1da5e26a46b2357f961b64ab0c in both, over 882 lines
  (`AP2-evidence/perf/months-on.txt` and `months-ap2-final.txt`, filtered of the `month`,
  `window`, JFR, load and "world market solved" lines).
- What is left in a land month (1135, 4.8 s): the stuck link search, 36% (★3); the landmass
  re-measure, 34% (★4); the bridge surveys, about 7%.
- The paused Overview's model: about 590 ms -> about 540 ms (★5).

## 7. Left for Jerus

- The stuck highway link search reruns its full 2-million-plot search at every land purchase (★3):
  whether a waiting link should back off.
- The re-measure of every district when an island is linked (★4) and the Overview's auction quotes
  while paused (★5): each a performance batch of its own if wanted.

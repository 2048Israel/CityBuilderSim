# ET5 - the design as built (the panel's synthesis, 2026-10-25)

Kept for ET6, whose bridges fill the `Joins` slot of §2.5. Written read-only before any code by the design workflow
(three designers, three judges, one synthesis); line numbers are main 0.8.47's. Where the slices settled a name
differently (`Landmasses.Picture`, `Landmasses.fieldLandmass`, `quoteBuildOn`), `docs/queue/ET5-notes.md` says so.


Branch batch/ET5 = main e89120ea (0.8.47, SAVE_FORMAT 46). Read-only; no JVM was run. **✓** = re-read in the source for this design.
**Base: design-exact** (judges 7 / 7 / 6): one reader shared by the model and the map, the books by landmass derived from it,
pooled ground, a guard that runs today's expressions on every one-landmass city. **Grafted from design-faithful:** the works gate
(D9), the city grown across landmasses inside the suite, the `--probe` of a kept save, the format bump, the report line only when
it applies. **From design-risk:** the `Joins` slot for ET6, the eager refresh, the searched fixtures, `FREE_SLACK`, the trace `cmp`
after every slice, the review greps, the bounded grid cache, "Island N", the ISLAND tag's share.
Settled, not reopened: D1 (a) and D19 (a generated world is one landmass, home, always linked; the default trace byte for byte,
and every one-landmass city's), D16 (a landmass as earth-2's layer holds it), the closure ET6 extends.

**The shape.** (1) `World.landmassAt`/`tileLandmass`: a dry plot's id at its blend point, a dry plot on a no-land cell taking
the nearest land cell's; 0 off Earth, and 0 reads as home. (2) `Landmasses` (new, owned by LandManager like `zoning`): derived
from the reader at every land change and at load (each landmass's owned ground by class, its shore, the linked closure); saved,
only the port placements while one is off home. (3) LandManager's free-ground arithmetic reads a linked view of owned, allocated,
relief and forest, each less the unlinked landmasses' part, behind `anyUnlinked()`. (4) A port order resolves its landmass once
in `buildStack`; an unlinked island's port takes that island's own room. (5) A works never stands on a field centred on unlinked
ground. (6) The map reads the model's picture; while the city stands on one landmass the picture is null and the map runs today's code.

## 0. The judges' fatal flaws, checked in the source

| # | flaw (judge) | verdict | mended by |
|---|---|---|---|
| F1 | risk links a landmass when its port is ordered; a shell keeps the link | ✓ `Ports.berths` counts `getQuantity`, `berthsOnSite` the stacks (Ports.java:184, :209) | a port links once standing (quantity − closingOf); a shell or a site never links (§2.5) |
| F2 | risk's shore test is per holding | ✓ today's test is `getSeaKm2() > 0` (Game.java:8189) | the shore is a plot count by adjacency (D10); one landmass keeps today's line |
| F3 | risk keeps `AutoBuilder.overFull` city-wide | ✓ AutoBuilder.java:1490-1493 | `overFull` reads the linked getters |
| F4 | risk: masking `RunGround.ownedAt` keeps unlinked ground out of pieces | ✓ wrong: `MapPieces.isLand` reads `g.owner` itself (CityMap.java:4656-4672) | `isLand` masked too (§4) |
| F5 | exact and risk leave works on island deposits | ✓ `hasDepositFor`/`sitesFor` read city-wide sites (Game.java:8069-8081, :8114), `buildFor` only them (:8207-8211) | D9's gate in LandManager's four site readers and the map's site index |
| F6 | faithful's `strands()` sees building rows only | design-specific; moot under pooled ground | exact's cap: no owned landmass may leave the linked set |
| F7 | faithful's saved choice redirects automatic port orders | valid | the choice is one-shot, unsaved, cleared at the month's settle; AutoBuilder and BuildAdvice pass `RULE` |
| F8 | exact does not bump the format | valid (an old Earth city's island ground would load as unlinked under built ground) | `SAVE_FORMAT` 47 with an Earth-only floor (D12) |
| F9 | exact recounts the books (DataSave.java:272-276) and its cost is unbounded | **half rejected**: that reason ✓ is about offers moving with pricing rules; a *saved* split would let a later D2 rule part the model from the map, which reads the live reader. The cost half ✓ (tile 50944 holds {0, 1, 648}) | a per-tile near-ids gate, a plots-read counter asserted 0 at the founded fixture city, `LANDMASS_TILE_MS` |
| F10 | exact's twins equal "to the bit"; no home shore | valid | `FREE_SLACK` 1 sq ft; bit-equality only against today's expression on one landmass; home salt found by search |
| F11 | exact's `reconcile()` would write the key on a generated city | valid | a template's row exists only while one of its entries is off home |
| F12 | `Timing.within` in a harness outside `Timing.HARNESSES` | ✓ Timing.java:50-52, TimingCheck.java:418-427 | `LANDMASS_TILE_MS` sits in EarthTerrainCheck, already listed |
| F13 | faithful's D6(c) rows, `Demolition.landmass`, a 17-file slice | valid | not taken; demolitions tracked by `Demolition.id` (ConstructionControl.java:753 ✓) inside Landmasses |
| F14 | faithful edits `join()`, `terrainChunk` and `CityRuns.Ground` (3 stubs) | valid | no interface change; the landmass test never stored in `terrainChunk`'s bits |
| F15 | risk's report line, `linkIslands`, `TILE_COUNTS` widened | valid | the line only when the city owns ground off home; no `linkIslands`; CityLand untouched |
| F16 | all three: `MapPieces.ensure` scans only new purchases | ✓ CityMap.java:4583-4592 | `seenLinks`: a moved linked stamp floods from the start |
| F17 | "the site is the world's centre" | ✓ wrong: the site is snapped (`snapped`, EarthSite.java:613) | moot: the card reads the same World reader at the snapped site plot |
| F18 | exact's door island lands on home; its strait founding and Medellín real run measure nothing | accepted from the judge's read of the fixture (probe.py) | every fixture found by search, its premise asserted (§6) |
| F19 | risk's real run founded at the strait cannot link (fresh shores) | accepted | the suite's grown city puts its ports on searched salt squares |
| F20 | **found here**: exact's "standing = the oldest of `here`" lets a stopped shell link (an island port stopped, then a home port standing, reads the island's entry as standing) | ✓ one shell per template (ConstructionControl.java:718-724) | four segments per template: standing, on site, shell, leaving by demolition id (§2.5) |

## 1. Decisions D2-D17 (★ = Jerus's call, §9)

- **D2** A dry plot on a 0 cell takes the nearest land cell by index distance (dr² + dc²), the bake's own naming rule (tools/earth/landmass.py:274-276 ✓), within `LANDMASS_REACH` = 8 cells either way; a tie to the lowest id; none in reach reads 0 = home (★). Integer arithmetic, resolved once per cell.
- **D3** The blend point (`proj.blend(x0, y0, 32)`, as cover and relief are drawn). Home = `landmassAt(siteX, siteY)` on the snapped site; the card reads the same call (EarthSite.java:686 ✓), so card == home by construction.
- **D4** A lock-free `EarthData.landmassGrid(tile)` (16 kept, ~1 MB) and `EarthTerrain.keptLandmass` beside `keptRelief`, never computed inside `tileTerrain`/`tileRelief`. CACHE_BYTES untouched (ET1-C 1 stays his).
- **D5** Books by landmass, **derived**: for each id off home the city owns dry ground on, its flat, foothill, steep, very-steep and forest plots, its first holding; for every id, its shore plots. Home is the remainder of the saved books. A pure function of the saved rectangles and the frozen data, so a reload is exact by construction.
- **D6 ★** Pooled ground use: U = Σ over unlinked L of (dry on L − ports' ground on L) makes the gate exact; only ports are placed.
- **D7** The linked view inside LandManager; the public owned, relief and forest getters and the market's `update` stay city-wide ("owned, counted and priced as today").
- **D8** The pickers count linked buildable ground; DEPOSIT skips offers with unlinked dry ground; COAST and the shelf's order unchanged; ISLAND first when unlinked dry ≥ `ISLAND_TAG_SHARE` (0.5) of the offer's dry.
- **D9** Gated: a field whose centre plot is on an unlinked landmass gives no site to a mine, quarry or well; desalination needs shore on a linked landmass. Pools, fresh rights and the forest harvest stay city-wide (★, ET5-C1).
- **D10** Any of the five terminals links, once it **stands**. Shore on L = owned flat or foothill dry plots of L 4-adjacent to an owned `World.salt` plot (the map's `shoreSpot` premise, CityMap.java:5433). Once the city owns ground off home this is home's test too (★). The card's choice is one-shot (`CHOSEN`); the rule (`RULE`): home if it has shore, else the linked L with shore and the most dry owned (tie lowest id), else NO_COAST, never an unlinked L. Demolition takes the newest standing unit whose loss unlinks nothing. AutoBuilder and the test player never link (★).
- **D11 ★** Refused: `demolishable(t)` caps a port so that no owned landmass leaves the linked set (the builders'-last-depot shape, Game.java:7247-7260 ✓).
- **D12** `SAVE_FORMAT` 47; `EarthSite.LANDMASS_FORMAT` 47 and `BEFORE_LANDMASS_WORDS`; `OLDEST_LOADABLE_FORMAT` stays 45 (ET2's shape, EarthSite.java:248-257 ✓). If a fixes batch takes 47 first, ET5 takes the next at its merge. The trace never prints the format ✓.
- **D13** Unlinked plots are not owned to the runs, the pieces and the plans; `join()`, the hub rule and the railway unchanged.
- **D14** Linked ground dealt by district distance as today (I5 on the map: ★ with D6); unlinked plots never room; ports per (type, landmass) from the picture.
- **D15** The hatch plot-exact from a model mask; a standing notice per landmass, key `island:<id>`; names "Home", the data's name as the card writes it, else "Island N" (N from 2, in the order first owned; ★); the tree's dash, the zone and island clauses joined by "; ".
- **D16 ★** Zones sized on linked owned ground; the forest law keeps its share of linked forest. Zoning.java untouched.
- **D17** A package-private `LandManager.buyRectForTest(x0, y0, x1, y1, month)`: the rectangle measured as `LandMarket.measure` measures an offer, bought through `buyParcel`'s body (LandManager.java:583 ✓) at no price.

## 2. The model, file by file

**2.1 EarthData.** `public static final class LandmassGrid { int id(int r, int c); }` (a uniform id, or palette ids with a byte or char index copied out of the blob; never `paletteOf`). `landmassGrid(int tile)`: decoded once, kept in `LANDMASS_GRIDS_KEPT` = 16 (synchronized get and put; reads lock-free). `landmassRow(lat)`/`landmassCol(lon)` use `landmassAt`'s arithmetic (EarthData.java:892-897); `landmassAt`/`landmassCell` unchanged (EarthCheck pins them).
**2.2 EarthTerrain.** `LANDMASS_REACH` = 8. `int resolvedCell(long row, long col)` (D2; kept per cell, cleared past 65,536). `int[] keptLandmass(tx, ty)`, synchronized LRU of `RELIEF_KEPT`, from `keptTile`'s cover at the blend: water 0, a dry plot its cell's id or `resolvedCell`. `tileLandmass(tx, ty, int[] out)`, `landmassAt(x, y)`. `int[] idsNear(tx, ty)`: sorted non-zero ids of the land cells under the tile's blend box widened by REACH cells (a cell window, no plot read).
**2.3 World.** `hasLandmasses()` = Earth with `data().hasRelief()`; `tileLandmass`, `landmassAt`, `idsNear` delegate, else 0s and an empty array.
**2.4 EarthSite.** `measure()` passes `d.landmass(w.landmassAt(sx, sy))`; `landmassNear` and `LANDMASS_RINGS` go (only :686 calls them ✓).
**2.5 Landmasses (new, model).** Wired by Game to a `Stock`: per template `standing(t)` = quantity − closingOf (ConstructionControl.java:770 ✓), `onSite(t)` (its stack's under construction), `shell(t)` (`shellOf`), `demolitions(t)` (ids and sizes), `landSqFt(t)`, `portTemplates()`.
- *Derived* — `recount(World, CityLand)`: home; if `World.hasLandmasses()` is false, done. If the 1° tiles under the owned box widened by REACH hold no id but 0 and home, done (Medellín). Else for each owned 32-plot tile whose `idsNear` ⊆ {home}: skipped; the rest read plot by plot (ownership by the grid's `cover(5)`/`tileFlags`, as `RunGround.ownedAt` does, CityMap.java:4398 ✓; class and relief by `tileTerrain`/`tileRelief`, `World.dry` as `CityLand.tally` counts, :1012 ✓). Per id off home: flat, foothill, steep, very steep, forest, first holding. Per id (home too) **only when not `single()`**: shore plots. Kept in a TreeMap; `plotsRead()` counts the per-plot reads. Near-ids kept per tile for the city.
- `offerSplit(World, CityLand, LandParcel)`: the same over an offer's unowned plots, the gate first. Re-run for every offer at every land change and link change, so in memory equals after a load.
- *Saved* — per port template with an entry off home, a `PortRow {int template; int[] standing, onSite, shell}` (landmass ids, oldest first) and `Leaving {int demolition; int template; int[] landmasses}`. Events: `ordered(t, L, n)` appends to onSite (processBuildOrder); `demolishing(t, n, d.id)` moves the n newest standing units whose loss keeps the closure (placeDemolition); `shellDemolishing(t, d.id)` moves the shell (demolishShell); `restarted(t)` moves the shell to onSite's oldest end (restartShell); `demolished(d.id)` drops it (after the release at Game.java:7641 ✓). `reconcile()` at the settle's end and at load: completions take the oldest onSite to standing (a template's units on site finish oldest first, by rule); a stop moves the newest onSite past the stack's count to the shell; then each segment is fitted to its count (short: home at the oldest end; long: drop the newest); leaving ids no longer on site dropped, missing ones read home; a row all home is dropped. Lists are materialized only when an order lands off home.
- *The closure* — `static SortedSet<Integer> closure(int home, SortedMap<Integer,Integer> standingPorts, Joins joins)`: {home}; until stable, add every L a join reaches from a linked one, and, when some linked landmass has a standing port, every L with one. `interface Joins { int[] joinedTo(int id); Joins NONE = …; }` is ET6's slot. `refresh()` after every event, recount and reconcile, eagerly (never in a getter): the closure, then U's sums (dry, relief by class, forest, ports' ground = every segment's entries on unlinked L × `landSqFt`), `version++` when the closure moved.
- *Reads* — `home()`, `single()` (no owned plot off home), `anyUnlinked()`, `linked(id)` (0 and home linked), `unlinkedDrySqFt()`, `unlinkedReliefSqFt(r)`, `unlinkedForestSqFt()`, `unlinkedPortsSqFt()`, `unlinkedFreeSqFt(id)`, `portRoomSqFt(id)` = flat + foothill on L − ports on L (≥ 0), `padSqFt(id, sqFt)` = the rise of max(0, ports − flat) on L, `shorePlots(id)`, `portsOn(id)` by `Ports.Cargo`, `rows()` (home first, then by first holding), `portLandmass(t, asked)` (`CHOSEN` = −1, `RULE` = −2, `NO_LANDMASS`), `freeToGo(t)`, `choose(t, id)`/`chosen(t)` (transient), `version()`, `State toState()` (fresh; null with no row), `restore(State)`.
**2.6 LandParcel.** Transient `unlinkedDryKm2`, `unlinkedBuildableKm2` (the dry when `allFlat`, else flat + foothill), `unlinkedLargest` (id); 0 from every constructor; in no row.
**2.7 LandMarket.** `setLandmasses`; `measure()` (:586) and `restoreOffers` call `offerSplit`; `restateOffers()`. Behind `> 0`: `buildableKm2`/`buildableSqFt` (:728-737) subtract the unlinked buildable; `buildableKm2PerUsd` takes its dry shortcut only when it is 0 (:728-757); `rankPerUsd`'s dry branch (:765-766) uses dry less unlinked; `anyBuildable` and `bestValue` (:923) follow; `cheapestWith` (DEPOSIT) skips an offer with unlinked dry. `cheapestWithSea` (:877) and `landShelf` unchanged.
**2.8 LandManager.** `landmasses = new Landmasses()` (a bare one is single).
- Private `linkedOwned()`, `linkedAllocated()`, `linkedRelief(r)`, `linkedForest()`: `!landmasses.anyUnlinked() ? <the field or call, as written today> : max(0, field − unlinked part)`.
- Switched to them, and only these: `getBuildableSqFt`, `freeSqFt`, `keptFromBuildingSqFt`, `protectedSqFt`, `openSqFt` (:2355-2378 ✓), `heldByZone` (`targetSqFt(linkedOwned())`, :2389 ✓), `zoneSizeOf`, `runRoom` (:2437), `split` and `slopeSqFt`'s private callers, `flatOnly`, `idleSlopeSqFt`, `getSlopeShare`, `getUsedShare` (:433 ✓), `foothillToPrepareSqFt`, `foothillDisplacedByRunSqFt`, the forest clearing's reads. Public owned, relief, forest, sea and fresh getters and `market.update` stay city-wide.
- New: `getLinkedOwnedSqFt`, `getLinkedAllocatedSqFt`, `getUnlinkedFreeSqFt`, `canAllocateOn(id, sqFt)` = sqFt ≤ portRoom, `allocateOn(id, sqFt)` (allocated += sqFt; no zone, clearing or pad), `landChanged()` (recount, restate offers, refresh; from `buyParcel`, `setOwnedSqFt`, the founding's and the load's installs of `land`), `buyRectForTest`, `getLandmasses`, `landmassState`, `restoreLandmasses`.
- D9, behind `anyUnlinked()`: `unlinkedSites(r)` = Σ of `typedPieces(r)` (CityLand.java:868 ✓) whose field is non-null and centred on an unlinked plot, kept like `seaSites` (:922) against (groundKey, `version()`); `getSites(r)`, `getSites(r, dry)` (dry fields only; sea fields stay the sea's), `getSites(r, type)` less them; `fits()` (:1481) gives those pieces 0 free sites.
**2.9 Game.**
- *Coast:* `hasCoastFor(t, n)` (:8188) → `hasCoastFor(t, n, CHOSEN)`: no coast needed → true; `single()` → `landManager.getSeaKm2() > 0` (today's line); a port → `portLandmass(t, asked) != NO_LANDMASS`; desalination → a linked L with shore.
- *Order:* `buildStack(t, n, nc)` (:8310) → `buildStack(t, n, nc, CHOSEN)`; the overload resolves L once after the deposit test; for a port on an unlinked L the land gate is `canAllocateOn`, else today's. `processBuildOrder(t, n, nc, L)` (its only caller ✓) allocates by `allocateOn` or `allocate`, then `landmasses.ordered(t, L, n)` when not (single and L == home), and clears the choice. `quoteBuild` resolves L the same way and prices `padSqFt` for an unlinked port. `runAhead` (:8456): unlinked ports against `canAllocateOn(L)` less `aheadOn[L]`, never `ground`.
- *Demolition:* `demolishable` takes `min(n, freeToGo(t))` for ports; `demolish()` words its refusal ("it is the port that links Island 2"); hooks in `placeDemolition` (:7284), `demolishShell` (:7319, keep `addDemolition`'s return), `restartShell` (:7366), after :7641's release; `settleConstructionControl` (:7580, SimulationEngine.java:91 ✓, the one seam) ends with `reconcile()`, clears the choices, and restates offers when `version()` moved.
- *Reads:* `choosePortLandmass(t, id)`, `portLandmassChoices(t)` (owned landmasses with shore, home first), `freeGroundFor(t)`, `getLandmassPicture()` (null when single). `refreshLand` (:4244) hands `setUnlinkedLand(getUnlinkedFreeSqFt(), name)`; the words at :4980, :5468 and the receipt (:1801-1815) take the clause.
**2.10 Callers.** AutoBuilder :1332 ✓ and `orderable` (:514) pass `RULE`; `overFull` (:1490 ✓) reads the linked getters; `lacks` and `shortcutOffer` follow. BuildAdvice `portTerminal` (:2097) reads `RULE`. BusinessInvestment `landReason` (:455) appends "; " + `unlinkedWords`. BuildCard: `freeGroundFor(t)` at :530/:1146/:1241, `landGate` the clause, `verdict` CHOSEN. LongPlaytest: no rule change (CHOSEN = RULE: no card).
**The guard, all of it.** Generated world: `hasLandmasses()` false, so `recount` returns at once, `single()` always, no row, no key, every offer's figure 0, the picture null. One-landmass Earth city: the same, Medellín at the 1° gate. Every new branch returns the old expression, character for character, when its guard fails.

## 3. The save

- Key `DataSave.landmasses` (`Landmasses.State { List<PortRow> ports; List<Leaving> leaving; }`, public static final classes with no-arg constructors, the `Zoning.ZoneState` shape, Zoning.java:219 ✓). Javadoc: "Absent on an older save and on every city with no port off its home landmass: every port at home." Written beside `setZoning` (Game.java:13884 ✓), read after `restoreZoning` (:17546 ✓), after `setAllocatedSqFt(built)` (:17533 ✓). Restore skips rows of unknown templates, then `landChanged()`, `reconcile()`, `refresh()`.
- Nothing else is saved: the books by landmass are recounted (D5). TreeMaps and int arrays only; no double.
- Format (D12) in ET2's shape; `loadProblem` refuses `format < LANDMASS_FORMAT` with `BEFORE_LANDMASS_WORDS` ("…before its ground was counted by landmass: ground across the water would load as ground to build on…"). EarthTerrainCheck's floor chain (:1432-1461) gains its link; GameVersion gets "47 - LANDMASSES" in 46's shape.
- Harnesses: SaveFileCheck.onEarth gains an ET5 block (key present only off home, resave identical but `savedAt`, key stripped reads all home and loads, a bad row skipped, a format-46 Earth save refused with the words); ReadPathCheck `readEverything` reads `rows()`, `linked`, `portLandmassChoices`, `hasCoastFor(t, n, L)`, `getUnlinkedFreeSqFt`, the picture, LandmassView, and `afterALoad` the stripped key; SaveSlotCheck 7c.1 walks `State` (fresh per save); NewGameCheck.snapshot gets a landmasses line; ReloadCheck 2/4 unchanged and green.

## 4. The map (the map reads the model's picture; null when single, and then every new path is skipped)

- `record LandmassPicture(int home, int[] linked, long[] portsByTypeAndLandmass, long stamp)`, handed by `Game.reconcileMap()` (:1414) beside `railPicture`. A plot is masked when its `World.tileLandmass` id is neither 0, home nor in `linked`: a stale picture never unmasks new ground.
- **Masks:** `buildableOwnership(tx, ty, own)` (= `tileOwnership` less masked plots) in `measure()` (:767) and `readGround`'s `in.owned` (:6545), so `place`, `roomiest`, DistrictPlan and R7's packing see no room there; the painter keeps `tileOwnership` (owned ground draws as owned, then hatched). `RunGround.ownedAt` (:4398) ANDs the mask into its kept tile; `version()` (:4423) folds `p.stamp` only when `p` is non-null; a new `atRaw(x, y, d)` reads ownership unmasked. `MapPieces.isLand`: a sample on a masked plot is not land, tested apart from `terrainChunk`'s bits; `ensure()` floods from the start when `seenLinks` moved.
- **Works:** `tallySites` skips a field centred on a masked plot; `siteIndex = null` on a stamp change.
- **Shore:** `shoreSpot(t, mines, L)` reads `atRaw` and refuses plots off L in both loops (:5461, :5468); `layShore` (:5365) lays per (type, L) from the picture and removes the newest work of the type whose shore plot is on L (`CityShore.removeNewest` with a predicate). Tank farms, reserves and desalination on linked shore only. A work's landmass is its shore plot's (derived; CityShore's sidecar unchanged).
- A stamp change re-measures every district (once a link). `measureNew` keeps a district holding owned masked ground, with no room, so the shore reaches it. `writeSidecar`'s land stamp folds `p.stamp` when non-null (no `CityMap.FORMAT` bump).
- `LandMap.unlinkedRuns(Landmasses, World, CityLand)` (row runs of owned dry plots on unlinked landmasses, kept on `version()` + the land stamp) and `LandMap.unlinkedChips` (per landmass, the mean of its owned tiles).

**CL5's pieces squared** (understanding §5; the notes carry this table: CL5's pieces stay CL5's, ET5 only hides unlinked ground from them):

| case | ET5 |
|---|---|
| A unowned land; B fresh ≤ 420 m; C fresh/shallow 420-1,020 m; F under a cell | one landmass or two linked: unchanged (C a stuck net until ET6) |
| D 1,020-1,440 m (Goeree); E deep < 510 m; H ≥ 1,440 m | the unlinked side masked: never in a piece. Both linked: one piece may stand, a net cannot cross salt (arms stop at salt, CityRuns.java:969-986): ET5-C7 |
| G a frayed islet | the model's D2 id through `World.tileLandmass`; the map has no rule of its own |
| I a dry neck the layer splits | each plot its cell's id; the unlinked half masked from runs and pieces, so runs stop at the edge |
| J an island holding only its port | masked: no piece, weight or net; its work on the island's shore, its count in a linked district (ET5-C3) |

## 5. The screens and the words (all text in a new pure `LandmassView`; every figure through a model getter)

| element | file.method | form |
|---|---|---|
| LANDMASSES table | LandScreen.showLandMenu between `groundPanel` (:205) and WORTH (:207) | `SectorScreen.oilTable`'s shape (:4995); heads landmass, dry owned, free, ports, linked; linked rows share one free figure ("free on linked ground"), unlinked rows their unbuilt dry and "only a port"; amber "no: needs a port here (home has one)"; shown at 2 rows or more |
| strip | LandScreen.strip/groundFigures (:338-381) | free follows the linked figure by D7; "after the best N" adds `size − unlinked dry`; a striped `Pieces.Segment` and one caption line per unlinked landmass |
| ISLAND tag | LandScreen.row (:745) | first, by D8's share; the line "On Island 2, which no port links: only a port can be built there until it has one" whenever > 0, beside `LandClassView.offerLine`, so `mapCard` carries it |
| hatch, chip | MapView.overlays (:1336), plane path | amber `ImagePattern` as `hatchPattern` (:1597) over `unlinkedRuns`, cached on `version()`; chip "Island 2 · 12.0 km² · not linked: needs a port here (home has one)" in `newTag`'s style |
| hover, legend | LandMap.ownerWords (:467), hoverWords, planeLegend (:813) | "On Island 2, which no port links: only a port can be built here"; legend "Not linked: only a port" when any |
| investors | BusinessInvestment.landReason, Game :4980/:5468, BuildCard.landGate, Inbox.landLockBody | "; 12.0 km² is free on Island 2, which no port links" (`wordKind` still "no land") |
| inbox | Inbox.takeMonth; UserInterface.dealLabel/deal (:6152/:6178 ✓, exact-key switches) | per owned unlinked L `take(game, month, "island:" + id, …)`; one `startsWith("island:")` before each switch: "Build a port there ›" → `choosePortLandmass(portFor(L), id)` then `openOnBuilding(BuildAdvice.ROADS, name)` |
| receipt | Game.buyLandParcel (:1801-1815) | "… - on Island 2, which no port links" |
| port card | BuildScreen.card after the BERTHS payback (:790-791), before `orderControls` (:803) | `Pieces.chipStrip` of `portLandmassChoices` (hidden when only home); `linkWords`: "Island 2 becomes the city's 2nd linked landmass" / "links nothing until home has a port too" / "links Island 2, which has a port" |
| refusals | orderPress (:1878), quoteVerdict (:1900), showNoCoastMenu (:4930), showNoLandMenu (:5033), whatItDoes (:4096) | "…on Island 2"; NO_LAND adds the unlinked lines and a door to the port card; "own some sea in front of dry shore, on the landmass it serves" |

## 6. The harness: LandmassCheck (`landmass`; one line in AllChecks.HARNESSES; README's count +1)

**Fixtures** (earth-2-fixture, Rotterdam 51.90625, 4.48125; the skeleton: `EarthCheck.resource(FIXTURE)` → `EarthTerrain.use` →
`World.Key.earth` → `GameFiles.scratch("landmass")` → `newGame(Founding.defaults().withWorldKey(..))`, `LongPlaytest.cleanUp`;
never `new Game()` or `Files.createTempDirectory`). `squareWith(world, id, salt)` searches out from a start cell, bounded (a FAIL,
never a hang), for a 64 × 64-plot square holding dry plots of `id` (and, when asked, owned-to-be salt 4-adjacent to them), using the
model's reader: **island-with-shore** from 648's cell 51.848 N 3.998 E (salt only at Goeree's north-west tip, about 51.84 N,
3.90-3.94 and 4.025-4.05 E); **island-without-shore** on Overflakkee from 51.75 N 4.20 E (fresh Haringvliet; premise
`shorePlots(648) == 0`); **home salt** from 51.906 N 4.2667 E. Bought by `buyRectForTest`. Twin B = the same city without the
island square. `FREE_SLACK` = 1 sq ft on every two-landmass comparison. Full-file sections print one line and skip without
`-Dcbs.earth`. Every premise is asserted before what rests on it.

0. **A generated world has no landmasses** (IslandNetCheck 7's three islands, and the default seed after 120 months): `hasLandmasses()` false, `single()`, `plotsRead() == 0`, no save key, every offer's figure 0, the picture null; `getAvailableSqFt`, `getUsedShare`, `freeFor`, `hasCoastFor` (five terminals and desalination) `==` today's expressions written out inline. The same on the fixture's founded Rotterdam city (single).
1. **The reader** (tiles 50943, 50944): `tileLandmass` == `landmassAt` at every plot; every dry plot on a 0 cell == a brute-force nearest-cell loop (premises: such plots exist; one resolves to 648); two Worlds of one key agree; home == 1 == the card's id; the card == `World.landmassAt` at the snapped site for each EarthFoundingCheck fixture site.
2. **The books by landmass**: per id == brute force over owned plots; Σ ids + home == the books' DRY to the plot (plot counts, integers); relief and forest by class; a recount after a purchase == a fresh recount; the founded city reads 0 plots (`plotsRead() == 0`; premise: no non-home cell within reach of its owned tiles); names "Home", "Island 2".
3. **Free ground is linked only** (A with the island square, B without): `getAvailableSqFt`, `freeFor` each Use, `freeForAnyone(WORKS, RUN)`, `getUsedShare`, `plotsAvailableFor`, `zoneSizeOf`, `AutoBuilder.overFull` agree within slack; `canAllocate(B.free + one plot)` refused on A though owned − allocated fits; `getUnlinkedFreeSqFt` == the square's buildable plots × `KM2_PER_PLOT` × `SQ_FT_PER_KM2` within slack; `landReason` ends with `unlinkedWords(…)` word for word.
4. **Only a port on unlinked ground**: a House past linked free gets NO_LAND; a works on a field centred on an unlinked plot gets NO_DEPOSIT (premise: a field of a SITED resource centred on a dry plot of an off-home landmass in the fixture, found by search over the fixture tiles; its square bought) and `getSites` excludes it; desalination with shore only on 648 gets NO_COAST; the no-shore square's terminal gets NO_COAST.
5. **Two ports link**: a General Cargo Terminal on 648 (`choosePortLandmass`): placed, its land out of `portRoomSqFt(648)` exactly, linked free unchanged within slack, 648 unlinked; on site (`simulateMonths` until it stands) it does not link, nor standing alone; a home terminal on the home salt square standing links 648 and linked free rises by 648's buildable less its port within slack; with no home port two islands each holding one do not link (`closure`); a third landmass (10297 or 12025 with shore, searched; a skip line if none) links with one port; **a shell never links** (F20: island port stopped, home port ordered and standing, 648 unlinked); `runAhead` with two island ports counts both against 648's room; NEEDS_FUNDING, then cash, then the order still lands on 648.
6. **Demolition**: the linking port's `demolishable` leaves it out, `demolish` refused with the words; `demolishShell` likewise; an island port demolished keeps its ground on 648 until the demolition completes, then 648's room returns exactly; leaving tracked by id across a save.
7. **The choice**: `portLandmassChoices` lists the landmasses with shore, home first; a choice set, then the AutoBuilder's order, lands on home (`RULE`); the choice is gone after the order and after the settle; the rule's order (home with shore, the linked L with the most dry, NO_COAST), never unlinked.
8. **The pickers**: an all-648 offer has buildable 0 and is never `bestOffer` ROOM or SHORTFALL, `bestValue` or DEPOSIT; ISLAND tagged by the share; `LongPlaytest.build`'s buying loop under a shortfall buys 0 island offers.
9. **The save**: the §3 list on the island city, plus twins played 2 months to the cent and a reload equal to the city played on (ReloadCheck's shape).
10. **The map** (a real CityMap of A): no building box, street or run plot on an unlinked plot, no piece holding one, no works drawn on an unlinked field (brute force); the port's work's shore plot reads 648, its quay on owned salt, `shoreShort` 0; after the link the districts are measured again, a piece holds 648's tiles and the site index is rebuilt; the sidecar stamp differs between linked sets; a generated city's sidecar byte-identical with the picture null; `unlinkedRuns` == brute force.
11. **The words**: every LandmassView string, the notice key, body and button, the card's three link sentences, rebuilt from getters.
12. **Grown across landmasses** (in the suite): A with both salt squares; 24 months of its investors and AutoBuilder with 648 owned and unlinked, then the two terminals by the card's path, then 96 months; every month asserted: linked free ≤ linked owned − linked allocated + slack, unlinked ports' ground == Σ placements on unlinked × land, placements == the stack's counts, no works on an unlinked field; every 12 months the map's no-box check. Then home filled by a fixture (orders until linked free < 648's buildable / 2): the map's districts on 648 hold buildings, none on an unlinked plot. Printed: refusals carrying the clause, the month 648 linked, the map's buildings on 648 (a count of 0 is a finding where no premise rests on it).
13. **The full file**: Voorne-Putten (51.85, 4.20), Rozenburg (51.905, 4.25), IJsselmonde (51.86, 4.55), Hoeksche Waard (51.78, 4.43) read Rotterdam's id, Goeree (51.77, 4.15) not; PEI (101) apart from the mainland; Halifax and Dartmouth one; Medellín on 2. Rotterdam played 1,200 months in SlopeCheck 9's shape (:836) with section 12's invariants at every look; printed: looks owning unlinked ground, offers off home, ISLAND offers, refusals with the clause.
- **`LandmassCheck --probe <folder>`** (a mode, not a section): the newest save in the folder read through DataSave and `CityLand.restore`/`restoreOffers` with no Game (so main's format-46 save is not refused), recounted; one line: "owned off home: 0.000 km² (ids …); offers off home: n of m". Writes nothing.
- **Elsewhere:** EarthTerrainCheck: `LANDMASS_TILE_MS` = 0.3, a fresh coastal tile's `keptLandmass` (median over the fixture's palette tiles) through `Timing.within`. The save harnesses of §3.
- **Mutations** (each must turn LandmassCheck red before its slice is accepted): the linked view off; the DEPOSIT skip off; the D9 gate off; linked by one port; a shell counted standing; `ordered()` skipped; the choice ignored; the map's mask off in `measure`; `seenLinks` off.

## 7. The slices (Sonnet 5.5 at xhigh, one at a time; Opus reviews each diff as a stranger's and commits; one JVM at a time)

Every brief carries: the guard rule (each new branch returns today's expression character for character when its guard
fails); "commit nothing; edit no GameVersion.VERSION, QUEUE.md, changelog, Zoning.java or WHAT IS BITING code"; "run only the
harnesses named, `./batch-check.sh <names>`, one JVM at a time"; "a harness's premise is never moved; a red premise is reported".

**A. The reader, the books, the linked pool and the works gate.**
- *Files and methods:* EarthData (§2.1); EarthTerrain (§2.2); World (§2.3); EarthSite.measure :686 (§2.4); Landmasses (new: the derived half of §2.5: `recount`, `offerSplit`, the near-ids gate, `plotsRead`, `rows`, names, `home`, `single`, `anyUnlinked`, `linked` = home and 0 only, `closure` and `Joins` written and unit-tested, the unlinked sums); LandParcel (§2.6); LandMarket (§2.7); LandManager (§2.8 whole, but `canAllocateOn`/`allocateOn` left for B); Game (wiring at founding and load, `refreshLand`, the words at :4980/:5468); BusinessInvestment.landReason; AutoBuilder.overFull; LandmassCheck 0-4 and 8 plus its AllChecks line; EarthTerrainCheck's `LANDMASS_TILE_MS`.
- *Behaviour:* an island bought by the door is owned, counted and priced as today, its ground no room for anyone; the investors' words name it; no works stands on its fields.
- *Guard:* §2's guard. *Run:* `--fast`; LandmassCheck, LandCheck, LandClassCheck, SlopeCheck, RunWorksCheck, MiningCheck, QuarryingCheck, OilCheck, ZoningCheck, AutoBuildCheck, InvestCheck, EarthCheck, EarthTerrainCheck, EarthFoundingCheck, ReadPathCheck, NewGameCheck, TimingCheck; then the default trace `cmp` against before (§8): a moved trace stops the batch.
- *Not:* CityLand, CityMap, ui/, ports, DataSave, GameVersion, SaveFileCheck. *Review greps:* `git diff --stat -- src/main/java/ham/citybuildersim/CityLand.java` empty; every switched LandManager method reads only the four accessors; no `Timing.within` in LandmassCheck; no getter fills a cache.

**B. Ports, linking, demolition and the save.**
- *Files and methods:* Landmasses (the saved half of §2.5: rows, segments, events, `reconcile`, `refresh`, `freeToGo`, `portLandmass`, `choose`, `State`); LandManager (`canAllocateOn`, `allocateOn`, `landmassState`, `restoreLandmasses`); Game (§2.9 whole: `hasCoastFor` 3-arg, the `buildStack` overload, `processBuildOrder(…, L)`, `quoteBuild`, `runAhead`, `demolishable`, `demolish`'s words, the hooks in `placeDemolition`, `demolishShell`, `restartShell`, after :7641, the settle's end, `choosePortLandmass`, `portLandmassChoices`, `freeGroundFor`, `getLandmassPicture`, save :13884 and load :17546); AutoBuilder :1332 and `orderable`; BuildAdvice.portTerminal; BuildCard (:530, :1146, :1241, `landGate`, `verdict`); DataSave (`landmasses`); GameVersion (`SAVE_FORMAT` and its list entry only); EarthSite (`LANDMASS_FORMAT`, `loadProblem`, the words); SaveFileCheck, ReadPathCheck, SaveSlotCheck, NewGameCheck, EarthTerrainCheck's chain; LandmassCheck 5-7, 9, 12 (model asserts) and 13.
- *Behaviour:* §1 D10-D12; a port on unlinked ground takes that island's room; two standing ports link; nothing unlinks.
- *Guard:* `single()` → `hasCoastFor` is today's line; no row is made for an order at home on a single city; the key is null without a row.
- *Run:* LandmassCheck, PortCheck, BuildAdviceCheck, BuildCardCheck, WaterCheck, LandlordPortCheck, ConstructionControlCheck, AutoBuildCheck, SaveFileCheck, ReadPathCheck, SaveSlotCheck, ReloadCheck, NewGameCheck, EarthTerrainCheck; then the suite (`AllChecks -f` in the background, waited on, never polled) and the default trace `cmp`.
- *Not:* CityMap, ui/, LandManager's linked view (A's), ConstructionControl's saved classes. *Review:* PortCheck 6, BuildAdviceCheck 13, WaterCheck 11 and LandCheck L2070 keep their "owns sea" premises (one landmass); every hook is inside `Ports.isPort(t)`.

**C. The map.**
- *Files and methods:* CityMap (`LandmassPicture` intake beside `railPicture`, `buildableOwnership` in `measure` and `readGround`, `RunGround.ownedAt`/`version`/`atRaw`, `MapPieces.isLand`/`ensure`'s `seenLinks`, `tallySites` and `siteIndex` reset, `shoreSpot(t, mines, L)`, `layShore` per landmass, `measureNew`, the re-measure on a stamp change, `writeSidecar`'s stamp); CityShore (`removeNewest` with a predicate); Game.reconcileMap's hand-over; LandMap (`unlinkedRuns`, `unlinkedChips`); LandmassCheck 10 and 12's map checks.
- *Guard:* `picture == null` → each new branch skipped and every stamp and version as today. *Run:* LandmassCheck, MapCheck, IslandNetCheck, PlanCheck, GatewayCheck, RailDrawCheck, TruckRouteCheck, KeptCheck; `./batch-check.sh --timing` at the end (MapCheck's RECONCILE_MS, MAP_MONTH_MS, SIDECAR_MS hard); the default trace `cmp`.
- *Not:* the model, `CityRuns.Ground` (no new abstract method: IslandNetCheck, GatewayCheck and MainLineCheck stub it), `terrainChunk`'s layout, DistrictPlan's rules, `CityMap.FORMAT`, ui/.

**D. The screens, the words, the probe and the record.**
- *Files and methods:* LandmassView (new, pure: heads, widths, rows, `offerLine`, `unlinkedLines`, `chipWords`, `linkWords`, `noticeTitle`/`noticeBody`, `portFor`, `ISLAND_TAG_SHARE`, `unlinkedWords`, `receiptWords`); LandScreen (table, strip, tag); MapView (hatch, chip); LandMap (hover, legend); BuildScreen (chooser, refusals, `whatItDoes`); Inbox and UserInterface (`island:` notices); Game.buyLandParcel's receipt; LongPlaytest (one line after ITS GROUND AT THE END, :5946, printed only when the city owns ground off home: "ITS LANDMASSES AT THE END: Home 177.7 km², Island 2 3.1 km² (not linked); terminals off home 0"); LandmassCheck 11 and `--probe`; README's harness count; docs/queue/ET5-ship.md.
- *Guard:* every screen element appears only at 2 rows or more, or when a figure is > 0; the report line only off home. *Run:* LandmassCheck, InboxCheck, BuildCardCheck, StaleCheck, `--fast`; then §8's final runs. By eye on the fixture's island city saved to slot 10 only.
- *Not:* the model's figures (every one through a getter), slots 1-9.

## 8. The playtests (one JVM at a time, nothing else running)

- **Before** (the branch is main now): `target/trace/run.sh` → target/et5/before-default.txt; `LongPlaytest --earth rotterdam` and `--earth medellin` with `-Dplaytest.keep=target/et5/kept-<city>-before` → before-rotterdam.txt, before-medellin.txt.
- **After A, B and C:** the default trace `cmp`, byte for byte but the run-time line. A move stops the batch: find the unguarded expression; no ensemble on it.
- **Final, after D:** all three again (`-Dplaytest.keep=…-after` for the Earth two); `LandmassCheck --probe` on the four kept saves; the suite with earth-2 named (`JDK_JAVA_OPTIONS`); `./batch-check.sh --timing`.
- **Predicted:** default identical (no ensemble, EN1; the notes say so). Medellín identical (tile {0, 2}: the 1° gate). Rotterdam identical unless the probe shows ground or offers off home: Goeree is est. 22-25 km SSW and the city grows N and W. If the trace moves, the notes give the month and the probe's line, and the run's trace is its own baseline.
- **ET5-ship.md** records the three cmps, the probe lines, what was red before, the CL5 table (§4), the ★ calls and ET5-C.

## 9. The ★ calls for Jerus, and ET5-C

**★ calls** (each recommended as built; a "no" is a small follow-up batch):
1. **D6 pooled ground use**, and the map dealing linked ground by district distance (I5's "home while it has room, then the linked landmass with the most free" is not drawn exactly). Recommend yes: the rule's gate is exact without it; per-landmass ground used waits for lots in the model (★M).
2. **D9 the pools stay city-wide**: a works never stands on an unlinked field, but a mine at home digs the city's whole remaining tonnes, fresh rights count an island's lakes, Forestry cuts an island's wood. Recommend yes for ET5 (ET5-C1).
3. **D10 a port links once it stands**, any of the five terminals. Recommend yes (I2's "once it has a port"; a ferry waits for I6).
4. **D10 home's coast test becomes the shore count** once the city owns ground off home (owned dry shore beside owned sea, the spec's words), stricter than today's "any owned sea". Recommend yes.
5. **D10 the AutoBuilder and the test player never build a port to link an island.** Recommend yes: two terminals are the player's choice, and the traces stay still.
6. **D11 unlinking refused.** Recommend yes: under pooled ground nobody knows what stands on a re-unlinked island.
7. **D16 zones and the forest law on linked ground.** Recommend yes: a zone holds ground to build on.
8. **D15 names**: "Island N" for a landmass the data leaves unnamed (the spec's word), not ET1's "unnamed". Recommend "Island N".
9. **D2 past 3.7 km a dry plot is home's.** Recommend yes: the layer does not call it land.

**ET5-C** (left undone, by name, with the reason):
1. **ET5-C1 Pools by landmass**: remaining tonnes (`remainingFor`), fresh rights (lakes are 0 cells in the layer), Forestry's wood (`cuttableForest`, the FOREST pool, sectors/Forestry.java:484); sea fields stay the sea's. A split of the typed pools is a batch of its own.
2. **ET5-C2 I5 on the map and per-landmass ground used** (D6 b/c): `place()`'s cursors and a saved ledger; the model is pooled.
3. **ET5-C3 An unlinked island port's count** sits in a linked district's room (the map's counts are per type); its work stands on the island's shore.
4. **ET5-C4 A linked island's hub at its port** (DistrictPlan.java:572, spec-city-layout:199) and **the railway on linked islands** (CL5-C call 1, his).
5. **ET5-C5 The sea route between two ports** (I2's "with a sea route between them", spec-roads:218, §4): routes are the map's (`SeaRoutes`); the model would need the pack's sea components.
6. **ET5-C6 Ferries and the crossing** (I6): a new `Ports.Cargo` widens the saved arrays and every by-kind loop.
7. **ET5-C7 Two linked landmasses in one piece** (cases D and E): a stuck net, harmless; ET6's bridges take it.
8. **ET5-C8 The NO_COAST shortcut** buying sea in front of the chosen landmass's shore: `cheapestWithSea` is city-wide.
9. **ET5-C9 An island port's foothill pad** is priced but not credited to `preparedSqFt` when the island links; **the card's choice is not saved** (a reload forgets it; the next order goes by the rule).
10. **ET5-C10 Linking by the AutoBuilder, BuildAdvice and the test player** (★5): a measure such as "a port that links Island 2".
11. **ET5-C11 The demolition card's chooser** (which landmass's port goes) and **allowing unlinking** (★6).
12. **ET5-C12 The model's quay** (`QUAY_PLOTS`, `opensToSea`): the model's shore is adjacency only, so the map may still count a port short.
13. **ET5-C13 The hatch on the globe path** (MapView.java:1968) and MiniMapFrame; **the Ports page by landmass** (InfrastructureScreen.portsPage, FreightView); neither in the spec's list.
14. **ET5-C14 His generated cities' other-island districts** (C6, spec-city-layout:200) stay buildable under D1 (a).
15. **Still his:** ET1-C call 1 (CACHE_BYTES) and call 2 (the continents' names, "Mumbai's").

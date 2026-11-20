# SQ1b - the two notes of SQ1's review (2026-11-20)

The brief is SQ1b's row in `QUEUE.md`, from `docs/queue/SQ1-notes.md` (REV-SQ1-1, SHIP, two notes). A month's
material draw is a long end to end, and `Game.buildStack` has a harness past 2^31. The default playtest's trace is
main's, byte for byte (section 4). No save-format bump.

## 1. The findings (main at `44e0aa664`, 0.9.162)

**Note 1: the draw.** `Game.drawMaterials` (Game.java:9476) read `int wanted = (int) Math.round(units)`. `Math.round`
returns a long, so the cast wraps: a month's draw of 3 x 2^31 + 12,345 units became a negative count, the
`wanted <= 0` line returned an empty `Markets.Draw`, and the whole draw went unbought (the materials market not
drawn on, the builders' salvage not taken, `monthlyMaterialImports` and its bill not raised) and unbooked
(`materialsConsumed`). The same int ran on down the path, and each was an int in a different way:

| where (main) | the int | what a draw past 2^31 did to it |
|---|---|---|
| Game.java:9476, 9478 | `int wanted`, `int fromYard` | the draw above; a draw between 2^31 and 2^32 wraps negative, past 2^32 to a small positive |
| Game.java:9589 (`deliverYardToSites`) | `takeFromYard((int) Math.round(needed))` | an order whose material passes 2^31 delivered **nothing** of the yard to its sites (a negative count takes none), the yard kept its stock and the order's contract owed the yard's share again |
| Game.java:11534, 11545 (`buildRunInvoice`) | `Math.min((int) Math.round(q.materialsNeeded), yard)` | the yard the first order of a run empties stayed full for the second, so the run's invoice priced its second order on units the first had used |
| Game.java:145, DataSave.java:2795 | `int materialsConsumed`, saved | `materialsConsumed += wanted` is a compound assignment, which narrows without a word; the month's tally wrapped, and ScaleCheck's `TOP` list scales this key K times over in a copy |
| BuildingManager.java:38, 8732 | `int constructionMaterials`, `takeFromYard(int)` returning an int | the yard's stock and the take on it; Game.java:9092-9124 passed it on to the quote as `int yardHolds` |

**Note 2: `buildStack`.** BuildingStackCheck's section 6 ordered through `BuildingManager.addStack`, which has none
of `buildStack`'s checks: the quote (`quoteBuildOn`), the ground (`canAllocate`, `allocate`), the cash charge
(`treasuryPays`), the yard's delivery to the sites and the contract it books. All of them take a `long quantity`
since SQ1, and none had been run on a stack past 2^31.

## 2. What was built

- `Game.drawMaterials`: `long wanted`, `long fromYard`, no cast. It is package-private now (was private; section 8
  draws through it yard first, which is what the month's maintenance does and nothing public reaches).
- `Game.deliverYardToSites`: `long fromYard`, `takeFromYard(Math.round(needed))`.
- `Game.buildRunInvoice`: `long yard`, both reductions on a long; `quoteBuild`'s four overloads take `long yardHolds`;
  `getConstructionMaterials()` returns a long.
- `BuildingManager`: the yard (`constructionMaterials`) is a long, with its getter, setter, `handleConstructionMaterials`,
  and `takeFromYard(long)` returning a long.
- `Game.materialsConsumed` and `DataSave`'s `materialsConsumed` and `constructionMaterials` (field, getter, setter) are
  longs. **No format bump**: an old save's JSON integers read into a long (the same as SQ1's stacks), nothing reads a
  new save with an older build, and the loaders are `Gson` fields.
- `BuildingStackCheck`: sections 7 and 8 (below), the header's list (8, 9) and a WHY paragraph. The compiler found
  no other site: no cast, no `int x = <long>` is left on the path; `-Xlint:lossy-conversions` was not needed because
  the one silent form, `materialsConsumed += wanted`, is a long field now.

## 3. The harness, `BuildingStackCheck` sections 7 and 8

It stays in BuildingStackCheck rather than a new SQ1bCheck: the fixture (a founded town, `ScaleCheck.scale` of it to
a House stack just under the ceiling) is the one section 6 builds, and a second harness would build it again
(about 40 s). The copy is scaled again from the founded town's save (section 6 saves over its own).

**Section 7, an order through `Game.buildStack`.** The order is 322,122,548 Houses (an int still holds the order; the
stack passes the ceiling with it; its material, 10 units each, is 1.5 x 2^31). The yard holds 108 units
(three months' of 36), the cash is the fixture's (`setCashForTest(2 x the quote)`: the order is not a funding
question). It reads back:

- the quote (`quoteBuildOn`): quantity, `materialsNeeded` = per House x order, the yard's, the plant's and the world's
  share summing to it, the ground (`landNeeded` = square feet x order), a finite total past 2^31 dollars;
- the invoice of a run of two (a House order, then Convenience Stores), `buildRunInvoice` on the yard and on none:
  the two differ by exactly the first order's own price difference, because the second is quoted on the yard the first
  emptied in both; and a run of one is its quote;
- the order, through `buildStack`: SUCCESS; the cash fell by the quote's total; the receipt (`getTotalBuildingCost`,
  `getMaterialsUsed`, `getBuildQuantity`); the free ground fell by the square feet of every House; the order is on
  site beside the copy's own sites and the stack with what stands is past 2^31 - 1; the yard's whole stock went to
  the sites the day it was placed and `materialsConsumed` took it; the sites owe the rest of the material.

**Section 8, a month's draw.** One month of this copy draws 3.98e7 units on the market (K times a three-month town's
few hundred): not past the ceiling by itself, which is why the draws past it are driven through the methods the month
calls: `drawSiteMaterials` with the builders' salvage stock (5.96e9 units here) and three int ranges more (the market is
drawn on for 6.44e9), and `drawMaterials(units, true)`, the repair's, yard first. It reads `materialsConsumed` (whole),
the salvage taken, the market's `getDrawn()`, the yard, and the book through a save and a load.

**Which int each assertion catches** (the old code put back one at a time; BuildingStackCheck alone, otherwise
green):

| the old int put back | assertions that go red |
|---|---|
| `drawMaterials`: `long wanted = (int) Math.round(units)` | 7 of section 8: the books took 0 of 12,399,684,367; the salvage and the market were not drawn on; the repair's yard stayed at 108 |
| `deliverYardToSites`: `takeFromYard((int) Math.round(needed))` | 3 of section 7: the yard still 108 not 0, the books 0 not 108, the sites' owed material is the whole order |
| `buildRunInvoice`: `(int) Math.round(q.materialsNeeded)` | 1 of section 7: the run's invoice does not move with the yard by the first order's share |
| `materialsConsumed` an int again | 3 of section 8: the tally reads -485,217,521 against 12,399,684,367 |
| SQ1's own int, `BuildingsStacks.getQuantity()`/`getUnderConstruction()` cast to int | 25 in all, one of them in section 7 (the stack with what stands is past 2^31 - 1) |

The quote's, the ground's and the cash's assertions (quote total, `landNeeded`, the free ground, the charge, the
receipt) do **not** go red with any of these: they are doubles from the template's constants and the quote, with no int
on them, and they were already right past 2^31. They stand in the harness as reads of the long path `buildStack` takes
(SQ1's brief for this harness: "reads back its quote, ground and invoice"), and a regression there is caught by them;
the ones that catch the **old int** are the yard's, the books', the owed material's and the run invoice's.

## 4. The trace

The default playtest (`-Dplaytest.trace`, 2,405 months, one JVM at a time) on main's classes (`44e0aa664`, built
from `git archive` in a scratch folder) and on this branch's (`f95f424d7`, the code final): **the eight files are
byte-identical** (`bank`, `bonds`, `borrow`, `build`, `house`, `labour`, `month`, `pop`; 899,230, 5,275,413, 2,205,
698,136, 897,482, 781,494, 965,996 and 444,487 bytes), and the two reports, 565 lines each, are identical but for the
objects' identity hash codes (`LandParcel@6a8658ff`), the clock's stamps and the run time (1,598 s against 1,133 s,
main's first; the laptop was shared with other sessions). Both exit 0. The default city never draws 2^31 units, so every widened line holds the same value it did.
**No ensemble** (docs/contract.md 4: an unchanged trace does not run it).


## 5. Calls

- ★ **The yard is a long too**, though `BASE_MATERIALS` is 36 a month and nothing scales it (it cannot pass 2^31 in any
  run; ScaleCheck's `TOP` leaves the works yard unscaled). The draw takes from it first and the take's result is the
  books' figure, so an int yard would leave `takeFromYard` an int on a long path with a bounded narrowing inside it; the
  row says no int left. It costs one saved field's type (`constructionMaterials`), read from an old save as it was.
  The alternative (a long take on an int yard, `(int)` of a value bounded by the stock) is the smaller diff; if that is
  preferred, `takeFromYard`, the setter and DataSave's field go back to int and the `yard` variables of
  `buildRunInvoice` stay long.
- ★ **`drawMaterials` is package-private**, not reached by reflection: the month's maintenance call (`yardFirst`)
  is not reachable from anything public, and a harness that reflects into a private method is a harness that breaks
  silently on a rename.
- ★ **No new harness class**: sections 7 and 8 in BuildingStackCheck, where section 6's fixture and the ceiling constants
  already are.
- **Left as they are, bounded by design:** `BusinessInvestment.orderSize` returns an int (an order's size is an int at
  the game's own call sites, and "the cast saturates", its own comment) and `yardCovers` clamps to
  `Integer.MAX_VALUE`; neither is on the draw's path (both read a count of buildings, not units).
- A month of this fixture is not past 2^31 units by itself (3.98e7). A city whose crews draw 2^31 a month is some fifty
  times this copy; the claim of the row ("at ten-billion scale") was not reproduced here by a natural month, only by
  the draw driven through the same methods. Said in the harness's section 8, so that nobody reads it as proof of the month.

## 6. Checks

- `./batch-check.sh --fast`: compile OK (about 75 s), maps regenerated (committed), Stale 0 firm findings.
- `BuildingStackCheck` (alone): "All checks passed.", eight sections, about 45 s. The mutations of section 3, each one
  old int put back and the harness rerun, are its evidence that the new assertions are not vacuous.
- `ReadPathCheck`: OK. `ScaleCheck`: OK (its copies scale `materialsConsumed` K times over and load it into the long).
  `SaveFileCheck`: known red only, its one line main's own in `docs/notes/gate.txt` ("fixture: somebody could not afford
  a basket even at full shelves"); the old-shape sections pass.
- The default playtest's trace against main's: section 4.
- **Not run here:** the whole suite (the pick is ALL: BuildingManager.java has 109 readers), which is the dry run's, and
  the timing harnesses (nightly).


## 7. For the orchestrator

- **The pick** (`sh tools/pick.sh origin/main HEAD`): ALL, BuildingManager.java (109 readers); **review: needed,
  saves: DataSave.java.** What the review has to look at: two saved fields change type, `DataSave.constructionMaterials`
  (the yard) and `DataSave.materialsConsumed` (the month's tally), int to long, fields, getters and setters, nothing
  else in the file; the load path (`Game` 21088, 22094) and the save (`Game` 17656, 17680) are unchanged lines that now
  widen; an old save's JSON integers read into the long (SaveFileCheck's old-shape sections pass). No money formula
  moved: no bank, bond, treasury or fund line is touched; the yard's stock and the draw's tally are unit counts.
- No ensemble (the trace is main's). A dry run is owed (pick ALL); no known-reds row is needed by anything run here.
- `docs/map/*`, `docs/dials.md` and `docs/harnesses.md` are regenerated and committed with the batch.


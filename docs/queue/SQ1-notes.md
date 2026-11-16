# SQ1 - a building stack past 2^31 (2026-11-16)

The brief is `docs/queue/SQ1.md` (queued by ST3, `docs/queue/ST3-notes.md`, section 2). A building stack's counts
are a long. The default playtest's trace is main's, byte for byte. No save-format bump.

## 1. The call: a long, not a second stack at the ceiling

The brief asked for whichever changes less and reads more plainly. A long.

- **A second stack** would be the smaller diff in `BuildingsStacks` and the larger fault in everything around
  it: `BuildingManager.getStack(template)` returns the first stack of a template and its 186 callers
  (`getUnderConstruction()`, the crews' order, the contracts, `ConstructionControl`) would each have to say which of
  two stacks they mean; the save is one number a template id (`DataSave.buildings`, `underConstructionById`), so
  it would write a sum and split it again on the load; and a contract, a progress figure and a material owed
  belong to one stack, not to a ceiling's worth of it. That is a new rule in every reader.
- **A long** is one type change that the compiler follows: no formula moves, the sums that were already
  `(long) stack.getQuantity() * per` become plain, and a saved int array reads into a long array with no
  conversion (Gson). It cost 58 non-harness files' types and 60 harness files' (the diff, new check aside, is
  1,046 and 441 lines of `int` to `long`, `Integer` to `Long`, `IntToDoubleFunction` to `LongToDoubleFunction`;
  `git diff -U0` less the type tokens leaves about a hundred lines, section 4).

## 2. What is a long now

- `BuildingsStacks`: `quantity`, `underConstruction`, `lastFinished`, the unfitted counts (`TreeMap<String, Long>`,
  `getUnfittedView()`); `startConstruction`, `addQuantity`, `removeQuantity`, `increaseQuantity`, `resumeShell`,
  `setUnderConstruction`, `setUnfitted` take a long; `getQuantity`, `getUnderConstruction`, `getLastFinished`,
  `getUnfitted` return one. The constructor's `initialQuantity` stays an int, as an order's size does at the game's
  own call sites (a stack passes 2^31 by two orders, or by the scaler).
- `BuildingManager`: `getQuantity(id)`, `retire`, `addStack`, `countByName`, `underConstructionByName`,
  `standingWhere`, `getUnderConstructionBySector`, `getUnderConstructionById` and `...Array` (`long[]`), the
  `Completion` record, the crews' wait arithmetic (`orderPoints`, `ruleWait`, `goesFirst`, the on-site sums).
- The saved fields, `DataSave`: `buildings` and `underConstructionById` (`long[]`), `unfittedById`
  (`Map<Integer, Map<String, Long>>`); the legacy positional `underConstruction` int array stays int (old
  saves only, never written). `Game`'s save and load paths read and write the long. **No format bump**: an old
  save's JSON integers read into the long fields (SaveFileCheck's old-shape sections pass).
- The orders and plans that carry a count of buildings: `BusinessInvestment.Decision.quantity` (a retirement is a
  share of a stack held, so it can pass 2^31), `Game.quotePave`/`paveRoads`/`landNeededFor`/`quoteBuild`,
  `Game.largestSlice` and the order search (its midpoints were already long), `BuildAdvice` (every
  `Map<BuildingsTemplate, Integer>` in the tree is a `Map<BuildingsTemplate, Long>`; `count()` returns `long[]`),
  `AutoBuilder`, `WorksValue`, `Sector.staffing`/`staffableCount`, `ConstructionControl`'s counts, `Bridges.Stock`,
  the oil, refinery and research views, `BuildCard`/`BuildScreen`'s typed orders and counts, `LandManager`'s
  `restoreIron`/`restoreSites`/`hasUnminedDeposit` arguments, `CityRuns.layTo`'s spurs.

## 3. The narrowings that are left, each bounded

There is no `(int)` on a count that can pass the ceiling. The ones that remain index a list the count is never
larger than, or are the bounded half of an overload:

- `Sector.staffableCount(t, int)` calls the long one and returns a result never above its argument;
- `Landmasses.fitRow` and `Oil.platformView` take `subList` of a count that is at most the list's size;
- two harness fixtures (`OrderSearchCheck`'s `BitSet`, `MapCheck`'s `subList`) cast a value bounded by their own
  small `q` or `slots`.

**The silent half.** `int total += <long>` compiles and narrows without a word. The slice that did the harness
files found it with `javac -Xlint:lossy-conversions` on a scratch run; it showed 12 sites in the model's views
(`BuildCard`, `BuildAdvice.linkedTerminals`, the refinery, oil, research and summary views) and about 20 in the
harnesses, all widened. The lint is clean now but for `DistrictPlan`'s three `int` to `byte` (not this batch's, main's).
Boxed equality was the other: `assertEquals(label, Object, Object)` with a long actual and an `int` expected is a
`Long.equals(Integer)` and false; `SaveFileCheck` and `EquipmentCheck`'s literals are `1L`/`2L`/`141L`.

## 4. How it was reviewed

The diff less its type tokens (`long`/`int`, `Long`/`Integer`, `1L`/`1`) leaves 88 removed and 100 added lines,
read one by one: the recomputed midpoints of the order search (`((long) pass + fail) >>> 1`, which were long already),
`64 - Long.numberOfLeadingZeros` for `32 - Integer.numberOfLeadingZeros` in `deskCallsMost` (the same bit count
below 2^31), `Math.round` for `(int) Math.round` where it counts buildings, the two `(Number) ... .longValue()` in the
harness pair-builders, `IntToDoubleFunction` to `LongToDoubleFunction`, and the one-line overloads. Nothing changes a
formula. What a stranger should still read (pick's review line): `ui/BankScreen.java` (`countByName("Commercial Bank")`
is a long, two `int standing` locals widened), `DataSave.java` (the saved arrays) and `ui/UserInterface.java`
(`buildingsOnSite`, `sitesOnShow`).

## 5. What the checks showed

- **The trace.** `-Dplaytest.trace` on the default playtest (2,400 months, exit 0, "All checks passed.") against
  main's reference (08c8727d, CU2's builder's `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-08c8727d/`): all
  eight files (`bank`, `bonds`, `borrow`, `build`, `house`, `labour`, `month`, `pop`) are identical under `cmp`. So
  nothing below the ceiling moved, and the ensemble is not rerun (the trace is main's).
- **BuildingStackCheck** (new, `AllChecks` after ScaleCheck): 47 lines, all OK on this branch, about a minute.
  1 a stack three under the ceiling and an order of ten finished makes it 2^31 + 6, two orders of 2^31 - 1 on one
  stack are on site as twice that and finish whole, scrapping floors at none; 2 a Bakery stack's unfitted count rides
  to twice 2^31 - 1 and falls with the stack; 3 two orders of 2^31 - 1 Houses add exactly their dwellings, their
  capacity (household places) and two orders of Convenience Stores each kind of post times 2 x (2^31 - 1), worked
  out apart in `BigInteger`; 4 `ScaleCheck.scale()` at the K that puts the founded town's House stack just over the
  ceiling: the copy loads, the stack, the homes, the places and the posts are K times the town's; 5 the same from a
  save written with the order on site: the on-site count K times over, past the ceiling; 6 a copy K'd to
  just under the ceiling takes an order that carries it past, saves, loads whole, and plays a month.
  The fixture causes the condition (it asserts the stack passes 2^31 - 1 where it claims to) and the harness
  asserts against the model's own getters and the templates' own figures; no ScaleCheck premise is touched.
- **Red on main's int, by hand** (the harness compiles against main's classes; its orders are int-sized):
  `javac -cp <main's classes> BuildingStackCheck.java` and run: 24 lines FAIL in sections 1 to 6, among them
  "an order of ten finished: the stack stands 7 past it  -2,147,483,642 against 2,147,483,654", "two orders of
  2^31 - 1 on one stack: the sites hold twice that  -2 against 4,294,967,294", "the copy loads (an int stack could not
  read it)", "its House stack is K times the founded town's  0 against 2,147,483,683", "every post is K times the
  founded town's  0 against 5,538,247,393", "its Houses on site are K times the town's  -1 against 2,147,483,720". The
  first run on main then stopped on a NullPointerException in section 6 (the copy had not loaded, so it had no
  House template); section 6 now returns there instead, so the harness is red cleanly.
- **The suite** (`./batch-check.sh --pick`, which picks ALL: `BuildingsStacks`, `BuildingManager` and `Game` are
  shared): see section 7.

## 6. Calls taken

- **A long, not a second stack** (section 1; recommended, taken).
- **Orders are long too** (recommended, taken). The brief names the stack's quantity, the on-site count and the
  callers; an order's size reaches the stack through `startConstruction` and a retirement is a share of a stack
  held, so `Decision.quantity` and the plan maps follow the counts they are added to. The alternative, int orders
  and a clamp where a share of a stack becomes an order, would have put a `(int) Math.min(x, Integer.MAX_VALUE)` in
  the planners, which is the narrowing the brief is about.
- **The unfitted counts are long** (taken): only a works' stack carries one, but `Game.unfitTheFounding()` sets a count
  to `stack.getQuantity()`, and "never more than stand" is the rule that clamps it.
- **The harness copies at a K of its own** (taken) rather than ScaleCheck's `TARGET_PEOPLE / pop`: ScaleCheck's K puts
  the House stack near 2e8, not past 2^31; the harness picks K from the town's House count so the condition is
  caused. ScaleCheck itself is untouched in its premise (only its types).
- **The month at that size** (section 6's last line) is in the harness because the brief says a stack "past 2^31
  reads back whole"; a copy that loads and cannot play a month is not a city. It passes.

## 7. The pick, the dry run

**Runtime traps the compiler cannot see.** The first `./batch-check.sh --pick` (ALL, 211 harnesses) found four NEW reds:
three were the widening, one was main's.

- `LandmassCheck`: `ClassCastException: Integer cannot be cast to Long`. `Landmasses.unitsOn` read
  `leaving.get(d[0])`, `leaving` a `TreeMap<Integer, Leave>`, and `d` the `{demolition id, buildings}` array that
  the stock interface now hands as a `long[]`: `d[0]`, an id, boxed to a Long. Now `leaving.get((int) d[0])`.
- `BankFailureCheck`, `RefineryCheck`: `(int) retire.invoke(...)` on the private `Game.retire`, which returns a
  long now, and `(Integer) seen[5]` of a captured `found.quantity()`: both reflective or boxed casts, now `long`/`Long`.
- `KeptCheck`: "no month makes more than a tenth of the nodes the first draw made (the most made in one: 46 of 400;"
  is **main's**: `AllChecks -f KeptCheck` on main's own classes (this worktree before SQ1's first edit, 08c8727d, run
  here) prints the same line and counts, and the known-reds file has the same fault at 47 of 378 and 40 of 384, the
  counts the trace had before LF1b moved it. It is in the ship file's `## known-reds` against FB2, the fix queued.
- To look for the same trap everywhere, `target/sq1scan/Sq1Scan.java` (a throwaway, not committed) walks every source
  with the compiler's tree API and reports any `Map.get/containsKey/remove/getOrDefault`, `Collection.contains/remove`,
  `indexOf` or `equals` whose argument boxes to a different numeric type than the key (a `long` against an `Integer`
  key returns null from a HashMap and throws from a TreeMap). It found the one above and nothing else; the
  reflective call sites (67) were read against the changed signatures: only `Game.retire`.
- The second full pick, on the fixed code: one NEW red, `LandNationsCheck`, "The real quote did not release its
  worker" (a thread the fixture waits for, under a suite at 80-98% CPU). Alone it passes (`./batch-check.sh
  LandNationsCheck`, exit 0) and it passed in the first full pick: a load flake, not SQ1's (nothing in it reads a stack).
  The other runs: `LandmassCheck`, `RefineryCheck`, `BankFailureCheck` and `BuildingStackCheck` together, exit 0.

**One fault in the process, for the orchestrator (not fixed here).** `docs/batch.md` step 5's template writes the
`## known-reds` section with a header row, `Harness | part of the red line | queued fix | date | cause`, and
`tools/known-reds.sh` reads that row as a malformed record (its third field has a space) and `batch-check.sh` stops
with "malformed known-reds row" and exit 1. This ship file has the rows only.

**The stranger's review (a saved field changed type, so it came before the dry run).** REV-SQ1-1 (Opus): SHIP, no
findings, two notes, both queued in the ship file as SQ1b: (1) `Game.drawMaterials`' `(int) Math.round(units)`
(Game.java:9318, main's) wraps a month's draw of material past 2^31 units at ten-billion scale; (2) `Game.buildStack`
(its quote, its land, its finance) has no harness past 2^31: BuildingStackCheck section 6 orders through
`BuildingManager.addStack`. The process fault above is queued as KR1.

**The dry run.** `sh tools/gate.sh SQ1 --dry-run` (the gate's fresh clone, its own data paths), exit 0, 2026-11-16
18:02 to 19:14. Kept as `/tmp/cbs-gate-SQ1-cIAvNoDp` (`ship.log`, `unnamed.txt` empty). It was of the branch at
`a1e2b25fa` (the notes and the ship file's queued rows were the last commits before it) merged into main at
`bdb28a46c` (0.9.128 with R6-G's docs), nine commits of batch/SQ1, nothing kept: "suite known red only - 5 harnesses":
DeathRecordCheck, InvestCheck, KeptCheck, MapCheck, PrizeCheck, all on the registry's recorded lines (KeptCheck's
46 of 400 by this ship file's row). LandNationsCheck's worker line did not come back. After it only this file and the
ship file changed (the gate-run line, then the marker dropped in a commit of its own).

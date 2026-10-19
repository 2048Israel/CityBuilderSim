# TM1 - notes: soft timing, two batches' checks at once (2026-10-18)

The batch's record beside its brief, `TM1.md`. It is tooling: no game change,
no version bump, shipped untagged.

## What was built

- **`Timing`** (new, in the flat package): the one helper every timing
  assertion goes through. `Timing.within(figure, budget[, unit])` and
  `Timing.below(...)` replace `figure <= budget` and `figure < budget`; each
  harness's assertion helper prints `Timing.verdict(ok)` in place of
  `ok ? "OK" : "FAIL"`. Under `-Dcbs.timing=soft` a miss answers true and the
  assertion's line reads `SOFT - 116 ms, budget 80 ms`; the harness does not
  count it. A condition's other part failing is FAIL as ever, a NaN figure
  fails soft or hard, and every verdict clears the misses, so a soft miss is
  never told on a later line. Without the property every expression is the
  old one. The property is read in `Timing.soft()` and nowhere else.
- **`AllChecks`** hands `-Dcbs.timing=...` to each harness JVM (a child
  inherits no system property, only `JDK_JAVA_OPTIONS`), counts the SOFT
  lines in every log under `-f` and says how many under the summary, and
  takes `--timing`: only `Timing.HARNESSES`.
- **`tools/jvms.ps1`** counts the other batches' JVMs and the game's
  processes on the PC: `java.exe`/`javaw.exe` running a `ham.citybuildersim.`
  class, the game being the launcher `ham.citybuildersim.CityBuilderSim`, the
  jar (`Play CityBuilderSim.bat` runs `-jar`, whose command line names no
  class) or `CityBuilderSim.exe`.
- **`Batch check.bat`** and **`batch-check.sh`** time soft by themselves when
  either count is above 0 as the suite starts, and say so in a line;
  `--timing` waits (15 s a look, up to 30 minutes) for the other batches'
  JVMs, says so when the game runs, and runs the timing harnesses alone and
  hard, nothing else. `batch-check.sh` joins its classpath with `;` under
  MINGW/MSYS/Cygwin, strips the CR Java prints on Windows before it greps
  the summary, and counts with `ps` elsewhere.
- **`ship.sh`**: after the suite on the merge, `batch-check.sh --timing`;
  a red there stops the ship as any red does.
- **`TimingCheck`** (new harness): see below.

## Item 1: every timing assertion in the suite

21 assertions in 8 harnesses, each now through `Timing`:

| harness | line (before) | what is timed | budget |
|---|---|---|---|
| MapCheck | 2150/2153 | a 1,389 x 868 L0 screen from its plans, at every size | `SCREEN_MS` 80 |
| MapCheck | 2199 | a month's change at 5B and 10B, main thread's CPU | `RECONCILE_MS` 5 |
| MapCheck | 2851 | the view's own path for the L0 screen, x 1 and x 10,000 | `SCREEN_MS` |
| MapCheck | 3187 | a frame's boats at 10B: L0, L1, the prototype's 8 districts | `BOAT_FRAME_MS` 0.5 |
| MapCheck | 3387 | a frame's trains at 10B: L0, L1 (with the calls' count, not timing) | `TRAIN_FRAME_MS` 0.5 |
| MapCheck | 3665 | Rotterdam's window as a far node, on the worker | `SCREEN_MS` |
| MapCheck | 3952/3972 | every stride's far node over Rotterdam (full file only) | `SCREEN_MS` |
| MapCheck | 4304 | a far node over the city, pictures or shares | `SCREEN_MS` |
| MapCheck | 4652 | the minimap's stamp, thread's CPU | `MiniMapFrame.STAMP_MS` 8 |
| MapCheck | 5761 | the L0 screen over the industry, and its slowest tile, CPU | `SCREEN_MS` |
| EarthTerrainCheck | 499 | a tile's ground, each window (section 5) | `TILE_MS` 0.3 |
| EarthTerrainCheck | 715 | the Earth screen at Rotterdam (section 8) | `MapCheck.SCREEN_MS` |
| EarthTerrainCheck | 776 | the cells pass, CPU (section 9) | `CELLS_PASS_MS` 1000 |
| EarthCheck | 699 | a 15" tile inflates, median of 31 rounds | `INFLATE_MS` 1 (in us) |
| GlobeCheck | 800 | a frame of the expanded map from the planet's picture, CPU | `FRAME_MS` 100 |
| PlanCheck | 939 | the dense screen's slowest district plan | `PLAN_MS` 40 |
| PlanCheck | 1252 | each district planned (the copies) | `PLAN_MS` |
| WorldCheck | 367 | the totals pass | `TOTALS_MS` 200 |
| WorldCheck | 391 | a tile | `TILE_MS` 0.5 |
| ScaleCheck | 624 | a free copy's median month (strict <) | `MONTH_MEDIAN_MS` 1500 |
| GridCheck | 617 | `owner()` of a plot, median of the runs | `OWNER_NS` 100 (ns) |

The `*_MS` dials that are not assertions: `MapView.FRAME_MS` and
`MiniMap.FRAME_MS` (the game's own per-frame budgets), `TimeChart.SETTLE_MS`
(a redraw delay), `MapCheck.CPU_STEP_MS` (whether the CPU clock is fine
enough to time on; TimingCheck's one allowance). `GridCheck.OWNER_NS` was the
one timing dial without `_MS` in its name.

Timed and printed, never asserted: ConversionCheck's draws and list,
EarthFoundingCheck's card, GatewayCheck's gateway, GlobeCheck 6's draw,
GridCheck's purchases, LandCheck's parallel count, PortCheck's calls and
frames, PlanCheck's inputs and cold screen, ScaleCheck's first months,
WorldCheck's founding, and the run times AllChecks and LongPlaytest print.

## The harness

`TimingCheck`, in the suite before StaleCheck. Its fixture is a miniature
harness that prints through `Timing.verdict` as the suite's now do, and its
one timing assertion is a measured figure against `FIXTURE_BUDGET_MS`, zero,
the fixture's own: no real dial is ever made to miss. It holds, in seven
sections: the miss fails and counts hard (the property unset or "hard");
soft, its line says SOFT with the figure and the budget and counts nothing,
in the unit the assertion names, `below` missing on an equal figure; only a
clock is soft (a false condition beside a miss, on either side of `&&`, is
FAIL and counted, a told or swallowed miss is cleared, NaN fails, two misses
share one line); soft is exactly "soft"; across the JVM boundary, a child
started by `AllChecks.command` exits 0 with a SOFT line when the parent is
soft and 1 with FAIL when not; the suite goes through it (no harness
compares a figure against a `*_MS` or `*_NS` dial but through `Timing`, bar
MapCheck's clock-resolution test; the harnesses calling it are exactly
`Timing.HARNESSES`, each printing through `verdict`; the property's string is
in `Timing.java` alone; `AllChecks` runs them all); and with no miss held,
`verdict` is exactly "OK" or "FAIL" in every mode. A new timing assertion
written as a bare comparison against a dial fails section 6.

## The slices

DEEPSEEK_API_KEY is not set on the PC, so the slices a brief could pin down
went to Agent subagents on Sonnet at extra effort (Jerus, 2026-10-18), as
`docs/deepseek.md` hands them to DeepSeek. Claude wrote `Timing`, the
`AllChecks` change, the scripts and the record.

- **B, the 21 call sites and 8 helpers.** The review changed nothing: only
  the assertion lines and the helpers' verdict moved, each the old expression
  without the property, and the two accumulators (MapCheck 2150, 3952) have
  no other assertion between them and the check they feed, as the brief
  asked it to confirm.
- **C, TimingCheck.** The review found one fault, and it was in the brief's
  side: `Timing`'s figure format printed a budget of 1 as "1.0", so section
  3's "3.0 ms, budget 1 ms" could not hold. The format was mended at the
  source (a whole number bare, three significant places below 1, never an
  exponent) and the expectation made "3 ms"; the figure's pattern lost the
  exponent it allowed. The rest stood: its own lines never go soft, the
  property is restored in a `finally`, the child JVM's `JDK_JAVA_OPTIONS` is
  removed so the parent's property is its only source.

## What the runs showed

- **The baseline**, `main` at 5dcc1f0 in a temp clone, alone but for another
  batch's suite that started beside it: 113 harnesses green in 761 s, Stale
  0 firm findings. Nothing was red before.
- **Soft, under load** (2026-10-18, 20:46): FR2's ensemble running, plus a
  40-thread CPU burner in a JVM the scripts count as another batch's (a
  scratch class, not in the tree), `Batch check.bat GridCheck WorldCheck
  EarthTerrainCheck` printed `timing soft - 2 other batch JVM(s) running`
  and `3 timing assertions SOFT, not counted (-Dcbs.timing=soft):
  EarthTerrainCheck 3`, and passed. The three: Rotterdam's tile 0.457 ms and
  Paris's 0.348 ms against `TILE_MS` 0.3, and the cells pass 1019 ms of the
  wall clock against `CELLS_PASS_MS` 1000 (this JVM cannot read a thread's
  CPU finely enough, so section 9 times the wall). Beside the ensemble alone
  the same harnesses had missed nothing: one other run is light load.
- **Counting runs:** every `java` started from the PATH is two `java.exe`
  processes on the PC (the Oracle javapath shim, and the JVM it starts under
  itself), and a suite's harness a third, so `jvms.ps1` counts a matching
  process only when its parent does not match: runs, not processes.

## Left undone

Under Pending as TM1-C, for Jerus's review:

- The first two batches whose suites run at once on the PC are the next
  ones; whether either reports a SOFT line that a hard run alone does not
  clear is the mechanism's real test.
- The cloud workbench keeps its one-JVM lock: it has two cores
  (`workbench/HANDOVER.md`), so the machine is short there, not only the
  timing. Lifting it is his call.
- DeepSeek's `agent.py` runs a harness hard, with no look at the machine;
  `docs/deepseek.md` says to rerun a timing red through the check scripts.

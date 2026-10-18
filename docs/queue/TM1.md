# TM1 - two batches' checks at once on the PC: soft timing (2026-10-18)

The brief for batch TM1. `QUEUE.md` links here. Jerus asked how two batches could check at the same time on the PC without taking turns, and said yes to this answer: "queue the soft-timing change as a small tooling batch, and put it after FX3". It is tooling, with no game change and no version bump. The orchestrator ships it with `--no-tag`.

## Why

**The machine is not what is short.** On 2026-10-18, FX2 and FR1 waited on each other, and on Jerus's game, under a "one game JVM at a time" rule. The PC has 14 cores, 20 threads and 32 GB, and two suites side by side fit in that easily.

**What clashes is the timing assertions:**
- `MapCheck.SCREEN_MS`;
- MapCheck's frame times: `FRAME_MS`, `BOAT_FRAME_MS`, `TRAIN_FRAME_MS`, the minimap's `STAMP_MS`;
- EarthTerrainCheck 8's screen and 9's `CELLS_PASS_MS`.

Under another JVM's load, or Jerus's game, these went red. For example, EarthTerrainCheck 8 measured 116 ms against its 80 ms budget with the game running, and 27.8 ms without it. Every other assertion is indifferent to load.

## What to build

1. **Find every wall-clock or CPU-time assertion in the suite.** `docs/dials.md` lists about fifteen `*_MS` dials: FRAME_MS, TILE_MS, SCREEN_MS, CPU_STEP_MS, BOAT_FRAME_MS, TRAIN_FRAME_MS, TOTALS_MS, STAMP_MS, SETTLE_MS, RECONCILE_MS, PLAN_MS, MONTH_MEDIAN_MS, INFLATE_MS, CELLS_PASS_MS and others. Grep the harnesses too, for timings asserted without a dial. List them in the notes.
2. **Soft timing.** Under a system property, `-Dcbs.timing=soft`, a timing assertion prints its figure, its budget and `SOFT` instead of `FAIL`, and does not count in `fails`. Without the property, every assertion is exactly as today. The property is read in one helper that every timing assertion goes through, so a new timing check cannot forget it. Never change a budget.
3. **`Batch check.bat` decides it by itself.** At the suite's start, it checks for another batch JVM: a `java.exe` whose command line matches `ham.citybuildersim`, not counting the game, `ham.citybuildersim.CityBuilderSim`. If there is one, or the game is running, it sets soft timing and says so in one line. A new flag, `--timing`, runs only the timing harnesses, hard, and is what the ship gate runs.
4. **The ship gate keeps timing hard.** Before the merge's commit, the orchestrator runs the timing harnesses alone, hard, with no other batch JVM running and the game closed if it can be. Then it commits, tags and pushes. Write this step into `ship.sh`, and into the PC's shipping, which merges in a temp clone, checks with `Batch check.bat` and pushes from there. A timing red at the gate is a finding, as today.
5. **`batch-check.sh` in Git Bash on Windows.** It joins the classpath with ':' (`CP="target/classes:..."`), and Java on Windows needs ';'. Pick the separator by the platform (`uname -s` matching MINGW or MSYS), so the PC's sessions can use it as the cloud does. Give it the same automatic soft timing as `Batch check.bat`.
6. **A harness for the mechanism.** Use a fixture whose timing assertion is made to miss, for example with a budget of zero in the fixture itself, never a real dial's. Assert that it fails with the property off, and is SOFT and uncounted with it on. Also assert that the suite's other assertions are untouched by the property.
7. **The record:**
   - CLAUDE.md's "one JVM at a time on the machine" becomes: batches check side by side; timing is soft while another batch or the game runs, and hard only at the ship gate.
   - The same rule in `docs/deepseek.md`, if it says it, and `README.md`'s *The checks* if they name it.
   - `TM1-notes.md` with the list from item 1.

## Done

- Two batches' suites run at once on the PC and neither waits.
- A timing assertion is SOFT while the other runs, and hard and green when the timing harnesses run alone.
- The suite is green with the property off.
- `batch-check.sh` runs in Git Bash on the PC.
- The record is updated.

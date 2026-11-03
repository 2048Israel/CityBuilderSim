# GT1 - Faster gates (0.9.3, 2026-11-02)

Jerus, 2026-11-01: "do we need 4k months?", and why a gate takes 45-60
minutes. Brief: `docs/queue/GT1.md`. A process batch (one in, one out, below).
Lane 2's first tonight, built on FC1 (RL1) and merged with main once FC1 landed.

## Where a gate's time goes (measured 2026-11-02)

- **The suite runs one harness at a time on the PC.** `AllChecks` starts no
  harness beside another while the commit charge is over `COMMIT_PCT` (75%,
  SP1); tonight it stood at 96% (54.1 of 56.4 GB; 47 `claude` processes hold
  19.1 GB, 20 `java` 5.1). FC1's full suite said "168 start(s) held" of 170,
  and its gate's suite took 6,074 s against 2,387-2,454 s at the gates before.
  So the suite is the sum of every harness's time. The 75% rule stays (a suite
  died at 43 of 54 GB on 10-21): ★1.
- **The timing reds are the load's, mostly.** Run alone on a quiet PC both of
  main's reds pass: SaveSlotCheck's due month 17.2 ms (budget 25), MapCheck's
  month at 10 billion about 4.3 ms (budget 5). At FC1's gate, beside the lanes'
  runs, main's own read 51.0 and 9.23 ms, and two more MapCheck lines and one
  KeptCheck line missed too. Every gate then pays the paired rounds (~20 min).
- **The long playtest** was 149 s alone at 4,000 months, 81 s at 2,400 (the
  same commit, the same load); in the suite's serial sum a small share.
- **Nothing recorded a harness's time** in a `-f` run, so "measure its own
  share first" took log timestamps. `AllChecks` now writes
  `target/checks/times.tsv` (seconds, harness, exit; the longest first) on
  every `-q`/`-f` run: the gate's suite leaves it beside its logs.

## What was built

1. **Main green on timing, in the code.** Both figures halved where a quiet
   run can show it, so a loaded gate has room:
   - *The month that autosaves* (SaveWriter.CLOCK_MS 25): what held the clock
     was the history's copy (about 500 lists of 6,000 entries, 1.06 ms), a new
     Gson per loan list (0.6 ms) and every good asked eight questions for every
     sector (0.31 ms). The history's snapshot is now owed
     (`HistorySave.snapshotLater()`): `SaveWriter.Job.write()` copies it as its
     first act, and every method that changes the series `settle()`s first, so
     the copy is the history as it stood either way. One Gson for the loans; a
     sector's state visits only the goods it has a row or carried figure for
     (the four unit getters read nothing else). The saved bytes are the same
     (SHA-256 of save, history and map at month 185 and 14 months on). Alone,
     alternated with the old code: median 17.2 to 8.2 ms; due-minus-plain over
     ten runs each 4.2 to 2.9 ms.
   - *The map's month at 10 billion* (MapCheck.RECONCILE_MS 5): the worst
     bounded month is the first after the +0.05% jump, when about 3,000
     placements land in some 2,400 districts, and four passes over all 29,255
     districts followed: the log's look at each one's flag (~29% of the
     month), the pieces' look at each one's version (~14%), thousands of
     reserves keyed and hashed (~15%), and every walk step reading a
     district's object. Now `add()` queues the districts it dirties and the
     types it touched (`logQueue`, `District.touch`, `TOUCHED_MAX` 8; a new
     district or the canonical deal reads them all), `moved()` queues a
     district for the weighing and keeps `freeOf`/`freeRoadOf`, the free plots
     by index, which the walks read first; only the reserves dropped and found
     again are keyed; the journal's 64 KB arrays are kept (`KINDS_KEPT` 16) and
     the pieces' chunks cached (`CHUNK_CACHE` 256), so a month makes 100-140 KB
     of garbage, not 400-700. The sidecar's bytes and totals matched the old
     code's after each of 26 months at 5B and 10B. MapCheck 5: the 10B worst
     month 2.3-3 ms quiet (5.8-8 before), the steady months about halved; under
     load the first month still read up to 5.9 ms once (★2).
   - Reviewed as a stranger's: every version bump goes through `moved()`,
     every count through `add()` or `logAll`, the walks' arrays through every
     `measure()`; no sort falls between the month's two placing passes (the
     month's districts are kept by index now); the kept arrays never leave the
     journal and `CityRuns.fill()` clears them whole. `HistorySave`'s lock:
     every method that writes the series settles first (`cutAt` and `slim`
     write fresh copies), the getters' lists are written by no caller.
2. **The playtest's length.** `LongPlaytest.TARGET_MONTHS` defaults to 2,400
   (`LAYOUT_STARTS` follows it; `MARKET_MONTHS` 600/1200/2400 all inside).
   The same city: the MARKET blocks at m600, m1200 and m2400 are the 4,000-month
   run's line for line. The full 4,000 run once a night on main's tip:
   `sh tools/nightly.sh` (a temp clone of origin/main, built, played with the
   main tree's earth-2.bin and world-1.bin, one verdict line to
   `$HOME/.cbs-nightly.txt`, the clone deleted). `day.sh` prints it as its
   `nightly` line; `--due` says `nightly due` from 01:00 to 07:00 until today's
   has run, and the hourly check-in starts it in the background
   (`docs/orchestrator.md`); `ship.sh` copies it into `gate.txt` as
   `# nightly: ...`. In the home folder, not `.git`, because `ship.sh` runs in
   a temp clone on the PC. First run: `2026-11-02 20:53 0.9.2 5602c6d0 4,000
   months: OK in 212 s`.
3. **Invariants on the playtest.** A survey of every harness found one section
   that founds and plays a city only to read every-month laws: ConservationCheck's
   food, electricity and water (a 156-month city). They are now
   `LongPlaytest.conservation()`, called by `audit()` after each month `run()`
   plays (the food law's opening stock taken in `run()` before the month, after
   the player's move), their premises (bread sold, the stock moving, unbilled
   draw) findings at the run's end. Caused from the other side: a law broken on
   purpose at 120 months fired (electricity 122 times, food 33) and exited 1.
   The 22 harnesses that borrow `run()`/`audit()` on cities of their own now
   carry the laws too; all green, CentralBankCheck (lane 1) among them.
4. **Shared grown cities: none qualified.** No two harnesses play the same
   founding, rhythm and month at 100+ months. Near misses: MapCheck and
   PlanCheck share `MapCheck.playedCity` but not `buildersShortOn`;
   CopperRefiningCheck and LandmassCheck play Rotterdam to 1,000 and 1,200
   months, each watching every month; the probe harnesses stop at different
   months. The repeats are inside harnesses and short: DeskCheck and
   FloatLinkCheck build `readyCity` 11 times (60-96 months; 20 s and 16 s in
   all), HoldersCheck one city 4 times (16 s). ★3.
5. **README's harness count.** README states no count (and no "nine minutes");
   `docs/deepseek.md`'s line about bumping it goes. StaleCheck's front-door
   rule has nothing to read there.

## The checks

- Assertions: the only harness file changed is ConservationCheck, 30 assertion
  lines to 24 (its six food/electricity/water lines), 13 s to 8 s; the playtest
  gains them as three every-month laws and four end-of-run premises. Every other
  harness's source is untouched.
- The playtest at 2,400: green, 2,405 months in 51-147 s by tonight's load;
  its conservation line: bread made 54,925,176 sold 54,831,285 over 2,404
  months, stock 0 to 143,845, electricity and water matched every month.
- Named runs, all exit 0: the 22 audit borrowers with ConservationCheck and the
  playtest (1,475 s); SaveSlotCheck, SaveFileCheck, ReadPathCheck, ReloadCheck,
  ReplayCheck, HistoryCheck, SectorStatementCheck, LongPlaytest on the autosave
  change; the 18 map harnesses P2 ran before and after its change, each as
  before; `--pick` at the end, exit 0: 39 harnesses (the 35 picked, TimingCheck
  and the map and save ones named) in 1,733 s, 38 starts held, 4 timing lines
  soft under the load (EarthTerrainCheck 1, SaveSlotCheck 1, MapCheck 2), the
  10B month's worst 3.86 ms there. `pick.sh`'s review line (saves, threading)
  is the review above.
- ship-selftest 109/109, wait-selftest 24/24 (before and after the scripts),
  pick-selftest 13/13.
- The ensemble: not run. The model moves nowhere (the reads, the save's speed
  and the map's are the same results); the playtest's trace is shorter, not
  different: the m600/m1200/m2400 blocks match main's 4,000-month run.
- The gate's time after: GT1's own gate, whose `times.tsv` is the first record
  of each harness's share. Before: FC1's, suite 6,074 s, timing 589 s, three
  timing harnesses through the paired rounds.

## One in, one out

`git diff --numstat origin/main` over the scripts, `tools/`, `AllChecks`,
`CLAUDE.md`, `docs/batch.md`, `docs/orchestrator.md` and `docs/deepseek.md`:
119 added, 104 removed (the notes and the ship file left out). `tools/nightly.sh` (73) and the `nightly` lines are the additions;
the scripts' long WHY paragraphs (`ship.sh`, `batch-check.sh`, `day.sh`,
`wait.sh`, `pick.sh`, 96 lines) moved verbatim to
`docs/notes/batch-history.md`, "The scripts' headers", each header keeping a
one-line WHY.

## The calls

- **★1 The commit charge.** Recommended: the orchestrator archives finished
  sessions (about 0.4 GB each) until the PC sits under 75%, so the suite runs
  three at once again: the largest saving there is, about two thirds of the
  suite. GT1 leaves `COMMIT_PCT` as it is.
- **★2 Timing at a loaded gate.** Recommended: no budget moves and the paired
  rounds stay; GT1 halves the two figures, so a gate under ordinary load stays
  inside them, and a heavier one is judged against main in the same minutes,
  as now. The lines a loaded gate still misses (FC1's: MapCheck's sidecar and
  FX-side month, KeptCheck's chrome; MapCheck 5's first 10B month) are
  Pending as GT1-T, to halve the same way if he wants fewer paired rounds.
- **★3 The in-harness repeats.** Recommended: leave them; building once and
  loading would save about 30 s and make a reload's exactness the premise of
  each fixture.

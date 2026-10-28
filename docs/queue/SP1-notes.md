# SP1 - the suite three at a time, each harness JVM under a memory cap (no tag, 2026-10-27)

The batch's record beside its brief, `SP1.md`. Tooling and `docs/`: no game
change, no version, no trace moved, no ensemble. Built on RL1's branch (RL1's
rule), its slices coded by Sonnet 5.5 at xhigh and reviewed and committed by
Opus.

## 1. What was there

`AllChecks` ran about 150 harnesses one after another, each its own JVM with no
`-Xmx`: 1,160 s at main's last gate, 31-32 min on a shared day, with 14 cores
mostly idle. Every JVM committed its default initial heap at once (a 64th of
the RAM, 508 MB) and could grow to 8 GB. **Memory is the PC's limit, more than
the brief's figures said:** today the commit limit is 41.6 GB (it was 54 GB on
2026-10-21; the pagefile is smaller), and with Jerus's other sessions it sits
at 75-86% before any suite of this batch's (Claude's processes 10.5 GB, Edge
3 GB, two other lanes' suites).

## 2. What changed

1. **The cap, measured** (item 1). Every harness run under a 4 GB ceiling with
   a GC log, two at a time, earth-2.bin named: no harness committed more than
   1.5 GB of heap (SaveSlotCheck) or kept more than 0.6 GB after a collection
   (MapCheck); the next were StaleCheck 0.9 GB committed, KeptCheck 0.7,
   EarthFoundingCheck 0.6. So `AllChecks.HEAP_MB` = 2048: no harness's run
   changes (G1 never wanted more than 1.5 GB), and none can take 8 GB.
2. **The start heap** (found on the way): `AllChecks.HEAP_START_MB` = 64, the
   harness's `-Xms`. Peak commit by default against 64, same harness: BooksCheck
   616 MB against 167, LongPlaytest 794 against 607, EarthTerrainCheck 721
   against 757 (it grows anyway); run times within the machine's noise (two
   rounds). Most harnesses are BooksCheck's kind. The runner JVM itself
   (`batch-check.sh`) starts at `-Xms64m`: it committed 616 MB doing nothing,
   163 MB now.
3. **N at a time** (item 2): `AllChecks.PARALLEL` = 3, a fixed pool; the longest
   first, `LONGEST_FIRST` from the measured times (MapCheck 192 s, GlobeCheck 72,
   LongPlaytest 55, EarthTerrainCheck 52, then BridgeMapCheck, GatewayCheck,
   MainLineCheck, EducationCheck, 41 to 30); the output in `HARNESSES` order,
   each harness printed when it and all before it are done, so `-f`, `-q`, the
   summary and the against-line are as before. Each harness's seconds are its
   own process's. `-Dcbs.parallel=N` (in `JDK_JAVA_OPTIONS`) sets one run's N;
   never more than half the cores, so the 2-core cloud stays one at a time. A
   shutdown hook takes the running harnesses with a stopped run.
4. **The guard** (found on the way): `AllChecks.COMMIT_PCT` = 75. No harness
   starts beside another of the run while the machine's commit charge is over
   75% (on Windows the MXBean's swap figures are the commit limit and what is
   left of it, checked against Win32_OperatingSystem); a run with nothing of its
   own running always starts the next, so it never stalls. The suite's last
   line says `memory: N start(s) held ...` when any was held, and
   `batch-check.sh` repeats it. WHY: three at once with only `-Xmx` took the PC
   from 74.6% to 85.6% in three minutes beside one other lane (stopped there,
   by the brief's 85% rule); a static N cannot know what the other lanes and
   sessions hold. The guard was 80% first: the first three (MapCheck,
   GlobeCheck, LongPlaytest, the longest and among the heaviest) started at
   79.8%, each at -Xms64m, and as they grew took the PC to 85.9% within a
   minute (stopped again). At 75% the same three end near 82%, and a second lane
   reading the same commit charge holds its own starts.
5. **`timing.tsv`** (item 3): `Timing.record` appends under an exclusive
   `FileLock` on the file (a channel opened for writing, not APPEND, which
   cannot be locked on Windows), at its end, so harnesses running at once never
   interleave rows. The file, its columns and its readers are unchanged.
6. **`--timing` stays one at a time** (item 5): a hard run, `--timing` and
   plain mode (inherited output) run one at a time whatever N says; so do the
   gate's paired rounds, which are `--timing`.
7. **Item 7, the paired judging** (Jerus, 2026-10-27 21:00: "Yes, add it to
   SP1"). `tools/timing-pair.awk`'s judge mode: a timing red over main's by
   more than 15% is still not the batch's when main's own paired median misses
   the same budget too, or the batch's is within it (at most it; a budget that
   is no number leaves the margin alone). Each pass says why after its figures
   ("within 15% of main's", "the batch within the budget", "main over the
   budget too"), so the Gate-timing line (now "not the batch's: ...") and
   `gate.txt`'s `# timing red not the batch's (paired): ...` lines word each.
   WHY: SC1, a printout in ScaleCheck, stopped twice under a shifting load:
   MapCheck's month at 5B, main 1.81 ms against 2.35 (+30%), both within 5 ms;
   KeptCheck's chrome, main 10.4 against 12.3 (+18.5%), both over 8 ms. And
   the rounds alternate which side goes first (main, batch; then batch, main):
   KeptCheck run 8 times, main and MP1 in ABBA order, was slower straight after
   a switch of build (6.2-6.9 ms against 5.2-5.5). Self-tests: `ship-selftest.sh`
   (the stub's budget 40 became 42, so the margin cases keep main within it
   and the batch over it) passes main over the budget too (61 against 81),
   the batch at its budget (42 against 21), stops main within and the batch a
   hair over (31 against 43), and holds the alternating order; TimingCheck 9
   holds both new passes against the awk directly.

## 3. Item 4 (★1): not taken - it does not hold

The gate runs the timing harnesses twice: soft in the suite, hard in
`--timing`. Skipping them in the suite needs the timing step to judge their
other lines exactly as the suite does, and with a hard timing step it cannot:

- the verdict would hold (a recorded line is taken off the residual, a NEW one
  is no clock's and stops unless named), but
- `gate.txt`, the next batch's record, is written from the suite's soft lines.
  A hard run's lines differ from them on any harness with a timing miss: its
  FAIL line, and its `exit N` and `N FAILED` counts (main's MapCheck today:
  `exit 1` soft, `exit 3` hard). The record would then hold counts no batch's
  soft run prints, and every batch would see main's red as NEW.

It would hold if the gate's timing step ran soft and judged the clock from
`timing.tsv` (status SOFT for a miss): a redesign of TM3's step, shipped today.
What it would save: the timing harnesses take 371 s alone (main's gate); in a
three-wide suite they are about a third of that, two minutes of a gate.
Recommendation: not now; the pending row says how, if the gate's minutes ever
matter more than the step's stability.

## 4. One in, one out (item 6)

`git diff --numstat` against RL1's tip, the generated maps and dials, this
batch's notes and ship file left out. **Scripts and rules: 77 lines added, 63
removed** - `batch-check.sh` +7/-27 (out: the "who else runs" look and its
soft-run line, a PowerShell start per run, unread since BP1 made every run
soft; jvms.ps1 stays for `Batch check.bat` and `deepseek/agent.py`), `ship.sh`
+17/-14 (out: the suite's `JDK_JAVA_OPTIONS=...-Dcbs.timing=soft`, which
batch-check.sh has set itself since BP1; in: item 7's wording and order),
`tools/timing-pair.awk` +12/-3, `tools/ship-selftest.sh` +15/-6,
`docs/batch.md` +21/-8, `docs/tree.md` +4/-4, `jvms.ps1` +1/-1. **The Java:
138 added, 28 removed** (`AllChecks` +112/-13, the pool, the guard, five dials
and their WHY; `Timing` +8/-4; `TimingCheck` +18/-11). In all 215 and 91.

## 5. Calls

- **★1 Item 4, the timing harnesses once per gate.** Recommendation (the
  brief's): yes if it holds. **Taken: no** - it does not hold (section 3).
- **★2 N above 3.** Recommendation (the brief's): only if the measured peak
  leaves room with two lanes running. **Taken: 3, no higher** - the peak
  leaves no room even for 3 without the guard, and with it N is what the
  machine allows: three below 75%, one above.
- Taken without a call, for the reader to overturn: the start heap and the
  guard (sections 2.2 and 2.4), which the brief did not name; the guard's 75%,
  ten points under the 85% the brief stops a run at, since the first three
  grow by about 3 GB (7 points) after they start.

## 6. Checks

- `sh tools/ship-selftest.sh` 109 of 109 (106 before: item 7's three cases;
  the alternating order is held in the turns case). `sh tools/wait-selftest.sh`
  24 of 24 (`wait.sh` untouched).
- `./batch-check.sh TimingCheck KnownRedsCheck` exit 0, two at once; a subset
  of 8 harnesses 9 s at three at once against 16 s at `-Dcbs.parallel=1`,
  printed in `HARNESSES` order both times; a run without soft timing one at a
  time. The guard both ways on a subset: forced (`COMMIT_PCT` 0) the five ran
  one after another and the last line said `memory: 4 start(s) held`; at 100
  no line.
- `./batch-check.sh TimingCheck` exit 0 after item 7 (section 9 all OK).
- The measurement of section 2 (every harness, two at a time, a GC log each,
  earth-2.bin named, under a 4 GB ceiling): 148 of 149 ran (KeptCheck's JVM
  lost a class to a compile beside it and was stopped); MapCheck failed only
  main's recorded red.
- **The full suite at N=3 against N=1 on one tree: not done.** Three attempts
  at three at once were stopped by the brief's 85% rule: 16:19 (85.6%, three
  at once with `-Xmx` only), 16:48 (85.2%, one harness of mine beside two
  other lanes' suites), 17:27 (85.9%, the first three started at 79.8% under
  the guard's first 80%). From 17:30 to 22:40 the PC stayed at 80-96% with no
  JVM of this batch's (at 19:40 Jerus's game, 2.3 GB, and NetBeans 1 GB; the
  gates of MP1 and SC1; the limit grew from 41.6 to 46.6 GB at 96% and fell
  back to 43.3), so neither run could start under it. `--pick` waits on the
  same and on RL1's merge (section 7).

## 7. Left

- **SP1-C, the N=3 against N=1 suite**: both full suites back to back on one
  tree, the same pass and fail lines, the two times and the peak commit
  charge, when the PC is under about 78% for the length of both (about 45
  minutes). The runner and the sampler are scripts of this session's; the
  gate's own suite, three at once under the guard, is the first real run.
- **SP1-E, Jerus's PC**: the commit limit is 41.6 GB (54 GB on 2026-10-21; it
  grows only when nearly full), and on 2026-10-27 the PC sat at 80-96% from
  17:00 to 22:40, 82.4% at 17:50 with no JVM running at all (Claude's 40
  processes 11.9 GB, Edge 2.9 GB, about 9 GB outside any process). Under the
  85% rule no suite fits then, not even one at a time; above 75% the guard runs
  a suite one at a time. A larger fixed pagefile (System Properties, Advanced,
  Performance, Virtual memory: say 32 GB) is his setting to make, not a
  batch's, and gives three at once their room.
- **SP1-D, item 4's redesign** (section 3), if wanted. Recommendation: not now.

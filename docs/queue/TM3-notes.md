# TM3 - the gate's timing judged side by side, main against the batch (no tag, 2026-10-27)

The batch's record beside its brief, `TM3.md`. It is tooling: no game change,
no version, no trace moved, no ensemble (the default playtest's trace is
byte-identical; nothing in the model, a save or a screen was touched). Run
whole on Sonnet 5.5 at xhigh, Opus at the gate only (BP1 item 7).

## 1. What was there

The gate judged a timing red once: the merge's `--timing` run, hard, then one
run of main's, minutes later, and a red counted as the batch's unless main's
run showed the same line (digits masked). On 2026-10-26 HY1, MS2 and CC1-S
each stopped on map lines main itself misses under Jerus's running game (the
FX-side map work at 5B read 59.9 ms against 58; main's own run of it had read
75.5 earlier that day), and two shipped only through a hand-patched copy of
`ship.sh`. `--red-ok` covered the suite step's reds and nothing at the timing
step. MapCheck's "the view adds nothing a tile" compared two measured times
without `Timing`, so it stayed hard in the soft suite, went red on CC1-S's merge
and was written to `gate.txt` as a recorded red.

## 2. What changed

- **Figures read, not guessed** (brief 3). `Timing.verdict(ok, label)` is the
  verdict every timing harness prints through now (a one-line change in
  each of the eleven, `KeptCheck`'s three with a local label); with
  `-Dcbs.timing.tsv=FILE` it appends a row for every figure the assertion
  judged: harness (the calling class), label, measured, budget, unit, status
  (OK, SOFT, FAIL) and the figure's place in its assertion (1..n for a
  condition that holds several). `AllChecks` forwards the property to the
  harness JVMs, `batch-check.sh` names `target/checks/timing.tsv` for every
  suite run (the old file removed first) and `TimingCheck` 6 holds that
  no harness prints an unlabelled verdict. `Batch check.bat` does not judge
  timing and is untouched.
- **Main and the batch timed in turn** (brief 1). When the hard run has a
  residual red (neither main's recorded nor the suite step's), `ship.sh`
  reads the missed figures from `timing.tsv`, finds the harnesses behind them
  and runs `./batch-check.sh --timing HARNESS...` with `CBS_TIMING_PAIRED=1`
  (no quiet minute: the rounds are to share one load) in main's worktree and
  in the merge's tree, main first, **three rounds** (`CBS_TIMING_ROUNDS`, at
  least two). TM1's soft wait and TM2's quiet minute before the hard run stand.
- **The margin** (brief 2). `tools/timing-pair.awk`, which holds the
  arithmetic and the dial (15%), takes each timing red's rows and passes it
  unless the batch's median figure is more than 15% over main's. The dial is
  moved for a test by `CBS_TIMING_MARGIN`, nowhere else. A red passes named in
  the merge's `Gate-timing:` line with both medians, and in `gate.txt` only as
  `# timing red within 15% of main's (paired): ...`, never a recorded red.
  What stops: a median over the margin, a side with fewer than two figures
  (main has no such assertion, a round did not run), a red with no missed
  figure behind it (not a clock's). A harness's own `exit N` / `N FAILED` lines
  pass only with a timing red of that harness that passes and nothing else red
  in it.
- **`--red-ok` reaches the timing step** (brief 5). With `--red-ok "his
  words"` the lines the judge stops on pass: `Gate-timing:` says `passed on
  Jerus's word: <words>: <harness [figures or why]>` for each and `gate.txt`
  carries them as `#` lines. Nothing passes without it, and it passes the lines
  in front of it, naming them, never a recorded one.
- **Wall-clock comparisons are timing** (brief 6). The search (every assertion
  line of every harness that reads a clock, and the assertion labels that say
  faster, slower or cheaper) found one: MapCheck's "...and no more than section
  5's at either size: the view adds nothing a tile", now
  `Timing.within(view[0], modelMs[0]) && Timing.within(view[1], modelMs[1])`.
  The other clock reads in the suite are printed, not asserted (`LandCheck`'s
  fast and slow, `PortCheck`'s "shown, not held", `HydroCheck`, `ConversionCheck`,
  `SaveSlotCheck`'s old way), compared with a `*_MS` dial through `Timing`, or
  `MapCheck`'s one allowed resolution test. `TimingCheck` 6f now fails any
  assertion line that compares a variable named `...Ms`, `...Ns` or `...Nanos`
  with `<`, `<=`, `>` or `>=` without `Timing.` on it.
- **`ship.sh` survives its own merge** (item 7). It copies itself to a temp
  file and re-execs before anything is merged, keeping its arguments and its
  `cd` to the clone; a branch that changes `ship.sh` is said in a line. The new
  one runs from the next ship. `CBS_SHIP_SHELL` is the shell the copy runs
  under (the self-test runs it under dash).
- `docs/batch.md` (the gate's step 6, step 5's `--red-ok`, the calls BP1 left 5,
  lesson d, the note on the copy) and the README's checks say the same.

## 3. The checks

`sh tools/ship-selftest.sh`: **94 of 94**, no JVM. Its timing cases are rewritten
with stub figures: a red where the batch is as fast as main passes, named with
the medians, in three turns (`main-timing main main-timing main ...`, six
paired runs, no quiet minute); a batch 10% slower passes; 30% slower stops with
the medians (41 against 53, +29.3%); one slow round in three is not the
median; the margin moves with `CBS_TIMING_MARGIN`; `--red-ok` passes the 30%
red and a red that is no clock's, each named; a paired round that did not run
leaves nothing to compare and stops; a red with no figure behind it stops with
no paired round; the suite's red is recorded and the timing reds are `#` lines
only, in a real ship; the worktree of main is gone on every path. The new
`ship.sh` case rewrites `ship.sh` in place under the running gate and asserts
the checks ran from the copy; with the copy switched off
(`CBS_SHIP_COPY=nocopy`) that one case fails and the other 93 pass, so it
proves the guard. (Git unlinks a file and writes a new one, so a merge alone
does not corrupt a shell here; the in-place rewrite is what the case causes.)

CHECKS_PLACEHOLDER

## 4. Calls (★), each built as recommended

- ★1 **Three rounds, not two.** The brief says at least two; two make the
  median a mean, which one spike on either side moves by half. Three is a real
  median for about a minute and a half more of the harness. Recommended, built;
  `CBS_TIMING_ROUNDS` moves it.
- ★2 **A red that is no clock's stops, and is no longer compared with main's
  run.** The old step compared every residual line (digits masked) with main's
  whole timing run, so a flake that main also showed passed. A flake in a timing
  harness that only the hard run shows, and not the soft suite step, is rare
  and costs a rerun; the new step does not run main's whole timing for it.
  Recommended, built.
- ★3 **`--red-ok` names no lines in advance.** Jerus's words ("Ship it") name
  none, so the gate cannot match them to lines; it passes what stops it at that
  step and writes the lines and figures into `Gate-timing:` and `gate.txt`'s `#`
  lines. A stricter rule (words that must name the harness) can come when he
  asks. Recommended, built.
- ★4 **A figure compared with a measured budget** (`Timing.within(view,
  modelMs)`) is paired on the view's figure alone, the model's time being main's
  and the batch's own alike. Recommended, built.

## 5. What the first real gate should say

`ship.sh` prints `timing red on the merge (<harnesses>): 3 rounds ... in turn`,
a line a round, then `timing    red on the merge, judged side by side with main
(3 rounds ..., margin 15%), within it: <harness [main X ms, batch Y ms (+Z%),
budget B ms]>`. `target/checks/pairs.tsv` has the rounds' figures,
`pair-judged.txt` the verdicts and `pair-main-N.out` / `pair-batch-N.out` each
run. Its notes should say what the paired step read.

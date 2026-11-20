# GB3 notes - ensembles run on the gate box first

Jerus, 2026-11-20, "Gatebox first":

1. Gates and the nightly always come first on the box; an ensemble never holds
   a ship up for long.
2. An ensemble goes to the box unless two or more heavy jobs (gate, nightly,
   ensemble) are already running or waiting there.
3. Otherwise, and whenever the box is down, it runs on the laptop (GB1's
   fallback: `jerus` is mailed).

Built by Claude (Opus, xhigh) on GB1's road to the box and GB2's lock.

## What was measured, before anything switched

One ensemble of the same commit on both machines: BO1-S's `cbac2d8dc` with its
`JAVA_OPTS` extras (docs/ensemble/0.9.162.md, line 4) and main's data files,
whose md5 sums are equal on the two. The box ran it as a probe outside the
repository; it gave way to every gate that came (three did: PZ1's, GB2's tests
and dry run).

| | laptop (0.9.162.md) | gatebox |
|---|---|---|
| Java | Oracle 21.0.4, Intel i7 | Temurin 21.0.13, Ryzen 7 9850X3D |
| runs at once | 5, beside other work | 8 |
| wall | 115.9 min | 17.5 min of runs (19.4 with one restart after a gate) |
| AUTO2, 2,400 months | | 238 s a run, 277 s the longest |
| memory | | 8.7 GB at the peak, load 11.6 |

At 16 at once, the 16 ZERO_SLACK seeds took 310 s a run (351 s the longest)
against 198 s at 8, using 16.9 GB. That is 28% more runs an hour, but each run
is 57% longer, and a run is what a gate that comes may wait for. Hence
`BOX_JOBS=8`.

The scores are the box's runs scored on the laptop against 0.9.161.md, like
the original:

- 47 of the 50 rows are equal. Every verdict, every median and every maximum
  matches, and `summary.json` matches in 100 of its 101 figures.
- The three rows that differ are all ZERO_SLACK minimums, from one seed:
  - S1: 0.13% on the laptop, 0.34% on the box;
  - the window's peak yoy: 7.33% and 7.53%;
  - rent at m2040 against the twin: -3.43% and 3.69%.
- The box reproduces itself: the 16 ZERO_SLACK CSVs, rerun at 16 at once, are
  byte for byte those of the run at 8.

So the difference is the platform (the JDK's or the CPU's last bit) in one
chaotic seed at 0%, not noise on the box. It is within the ensemble's own
noise: one seed of 16 moved within the spread. The switch went ahead.

## What changed and why

- **`tools/ensemble/ensemble.sh`.** With a box named (`CBS_GATE_HOST`, else
  `~/.cbs-gate-box`, as for the gate), it hands its runs to
  `tools/gate-box.sh --ensemble <label>`. It does this after the run's folder
  exists, so `--stop` here finds it.
  - `ENS_PART=runs` is the box's half: no pandas, no score. It exits 5 when a
    gate came before the runs were done.
  - `ENS_PART=score` is the home half: it scores the copied folder, with no
    snapshot, driver or runs.
  - `ENS_YIELD` names a file whose presence makes a run that has not started
    exit at once.
  - `snapshot.txt` and `baseline.md` name the host and Java.
  - EN2's special case went, which made `docs/ensemble/EN2.md` the newest
    baseline while its version (0.9.111) was the newest. It has been dead
    since 0.9.113.md.
- **`tools/gate-box.sh --ensemble`** is the laptop half. The box runs the
  pushed commit, so it refuses two cases, naming `CBS_GATE_HOST=` for a working
  tree run here:
  - src, pom.xml or tools/ensemble holds changes not committed;
  - HEAD is not on origin.

  It runs on the laptop, compiled first and saying why, in these cases:
  - `JDK_JAVA_OPTIONS` holds anything but main's data files;
  - `JAVA_OPTS` names data files (the box has its own copies of main's);
  - no box answers (`jerus` mailed, as GB1);
  - the box answers `@@cbs-full`: "the gate box has gate X, nightly running or
    waiting (Jerus, 2026-11-20: the laptop is the overflow when two are)".

  `SEEDS`, `POLICIES`, `RESUME` and `JAVA_OPTS` travel as `e` words, a
  whitelist the box exports. When the runs are done, the box's folder comes
  home to `$ENSEMBLE_OUT/<label>/`, all but classes, driver and tmp, in place
  of what was there. It is then scored here by `ENS_PART=score`, so
  `baseline.md` is where it always was and the scoring is unchanged.
- **The serve names its job.** Its command line is `--serve gate ID`,
  `dry-run ID`, `nightly`, `ensemble LABEL` or `stop`. `heavy()` reads the
  box's serves from `ps`, scoped to this HOME through `/proc/PID/environ` so a
  self-test's faked box and the real one never count each other. It drops a
  serve's own forks by their parent, and counts a bare `--serve` (a commit
  before GB3) as a gate. Because the list is made of processes, there is no
  stale marker after a crash, as with GB2's flock.
- **The ensemble's admission (rule 2).** The serve counts the box's heavy jobs
  under `~/cbs/heavy.lock`, so two ensembles arriving together count each
  other once. At `FULL=2` it answers `@@cbs-full` and the laptop takes the
  ensemble.
- **The box's queue (rule 1).** `--ensemble-here`, the serve's child:
  - It takes `~/cbs/ensemble.lock`: one ensemble at a time.
  - It waits until no gate or nightly is running or waiting, then takes
    `~/cbs/box.lock` exclusive, as a night does. Only then does it compile.
  - It runs `ENS_PART=runs`. A watcher touches the yield file when a gate or
    night appears.
  - The runs not started skip, and those started finish. Then it lets the
    lock go, waits again, and resumes (`RESUME=1` from the second pass on).
  - A gate's longest wait is one run: 277 s for AUTO2 at 8 at once.
  - A dropped caller TERMs it, and its trap stops the runs
    (`ensemble.sh --stop`).
- **GB2's waiter** now names what holds the box, for example "the gate box is
  held (ensemble HS2); this gate waits for it", where it said the nightly
  always.
- **`tools/ensemble/stop.ps1`.** `--stop <label>` here also ends the run's
  laptop half and its ssh (`--ensemble <label>`, `--serve ensemble <label>`).
  The box then stops its runs on the dropped connection.

## Calls taken (each the recommended one)

1. **The scoring stays on the laptop.** The box has no pandas and no pip, and
   installing either needs sudo or a download. The box plays; the laptop
   scores, with the same score.py and pandas as before. "The scoring is
   unchanged" literally.
2. **The pushed commit is enforced, not assumed.** A dirty tree or an unpushed
   HEAD stops with what to do, rather than running an older commit than the
   builder thinks.
3. **A gate waits for the runs already started, and never runs beside them.**
   Killing them would waste up to eight runs a gate. Running beside them would
   time the gate's harnesses under load (R2c-P's red, 2026-11-18).
4. **Gates among themselves are as GB2 left them:** shared, so two gates run
   at once on the box. Rule 2 counts both.
5. **The laptop's heavy slot stays the orchestrator's** to keep (Git Bash has
   no flock). The box's keeps itself.

## Checks

- **On the laptop**, at afdb29258: `batch-check.sh --fast` exit 0;
  `batch-check.sh EnsembleCheck`, the pick's one harness, exit 0;
  `tools/ensemble/ensemble-selftest.sh` 6 of 6 (again after the score line's
  mend); `tools/ship-selftest.sh` 195 of 195 beside HS2's ensemble, the
  `flock` and `/proc` cases skipped (Git Bash has neither).
- **On the box**, the same tree on e2f399d7c in a test copy:
  `tools/ship-selftest.sh` 205 of 205 in 2 min 21 s. That includes GB3's 13
  cases, among them the queue's: two heavy jobs send an ensemble home, and a
  real gate meets a playing ensemble, waits "held (ensemble EN7)", ships after
  the runs started, and the ensemble resumes. `ensemble-selftest.sh` there ran
  3 of 6, with the three that score skipped (no pandas).
- **Real ensembles through the box** from this worktree, at afdb29258 pushed:
  - `SEEDS="0 1" POLICIES="ZERO_HOT AUTO2"`: compiled on the box, 4 runs at 8
    at once, copied home and scored here in 3 min 19 s, batch verdict PASS.
    `baseline.md` says "Run on gatebox", and `snapshot.txt` names the box's
    data and Java.
  - A first try without AUTO2 played and came home, but score.py refused it
    for want of the AUTO2 twins. That is its own rule, unchanged.
  - `ensemble.sh --stop GB3-stop` here, with four AUTO2 runs playing on the
    box, stopped the laptop half and its ssh. The box's serve then ended its
    runs: no java and no serve were left there, and the locks were free.
    HS2's ensemble on the laptop was untouched.
- The box's probe and test files were removed afterwards (`~/cbs/ens*`, the
  test copy, the live runs' folders).

## One in, one out

`git diff --numstat origin/main` over the process files, the notes and ship
file left out: +315 -82.

- The scripts, +105 -23: `tools/gate-box.sh` +80 -12,
  `tools/ensemble/ensemble.sh` +21 -9, `stop.ps1` +4 -2.
- The self-tests, +170 -50: `score-selftest.py` +107 -47, `ship-selftest.sh`
  +63 -3.
- The docs, +40 -9: the contract +9 -1, the orchestrator +7 -3, the
  ensemble's README +20 -3, README +2 -1, the tree +2 -1.
- It is a new mechanism: the box's queue and the ensemble's road there. Three
  things went: EN2's special case (dead since 0.9.113), GB2's two fixed
  waiter lines, and the "five at a time" text. The laptop route stays, as the
  overflow and for when the box is down.

## Left for Jerus

- **The baselines change machine.** From GB3 on, ensembles are run on the box.
  The first box ensemble is scored against a laptop baseline, so a ZERO_SLACK
  minimum or maximum may move by one seed. That is the platform's drift
  measured above, within noise and no verdict's cause. From then on, box
  baselines meet box runs.
- **pandas on the box** would let the gate run the scorer's own cases too.
  Today the box's run of `ensemble-selftest.sh` skips the three that score.
  The laptop runs all six. It needs pip, which is not installed (sudo).

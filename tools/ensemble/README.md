# The monetary ensemble

The check of the game's monetary model. It plays 16 seeded cities under each of
5 policies (AUTO2 for 2,400 months, ZERO_SLACK, ZERO_HOT, SHOCK and HIGH), one
JVM and one CSV a run, and `score.py` scores the 80 CSVs against the criteria
in its header (A1 to R3, T1, T2, M1, F1) into a table and a `summary.json`.
`Ensemble.java` is the driver (a city founded and played as LongPlaytest does,
one CSV row a month), `ensemble.sh` the runner, `score.py` the scorer, and
`stop.ps1` what stops a run on Windows.

## The rule (EN1, Jerus, 2026-10-20)

Run it only for a batch whose playtest trace moves. A batch whose trace is
byte-identical to main's skips it and says so in its notes. It compares with
the newest `docs/ensemble/<version>.md` and never reruns main. A batch that
moved the trace commits its run's `baseline.md` as `docs/ensemble/<batch id>.md`
on its branch; `ship.sh` names it `docs/ensemble/<version>.md` in the merge, by
the version it sets at the gate (BP1, 2026-10-24), and that file is the next
batch's baseline.

## Running it

On the PC, in Git Bash at the root of the checkout or worktree:

    mvn -q compile                                              # target/classes must be built
    python -m pip install --target "$HOME/.cache/cbs-ensemble/py" pandas   # once
    tools/ensemble/ensemble.sh en1                              # the label: the batch's id

Run it in the background and wait for it; it prints about six lines. Python runs
with `-I`, which ignores `PYTHONPATH`, so pandas is found through `ENSEMBLE_PYLIB`.
In the cloud it is the same command; pandas for the system Python needs no pip
step, and the workbench's JVM lock is taken for each run, one at a time.

The classes are copied to the run's folder at the first start, so compiling the
tree while it runs cannot reach it. Starting the same label again resumes: runs
with a CSV and a times file are kept, the snapshot is kept, the rest are run.
Use a new label (or `RESUME=0`) for a fresh run.

## Environment (all optional)

| variable | default | meaning |
|---|---|---|
| `JOBS` | 5; 1 where the cloud lock applies | runs at once |
| `SEEDS` | `0..15` | seeds to run |
| `POLICIES` | `AUTO2 ZERO_SLACK ZERO_HOT SHOCK HIGH` | policies, longest first; the others are read against AUTO2, so keep it |
| `BASELINE` | newest `docs/ensemble/<version>.md` | a `summary.json` or such a `.md`; `none` for none |
| `JAVA_OPTS` | none | passed to every run, e.g. `-Densemble.months=720` |
| `ENSEMBLE_OUT` | `target/ensemble` | the folder the runs go under |
| `PYTHON` | `python3` if it runs, else `python` | the interpreter for `score.py` |
| `ENSEMBLE_PYLIB` | `$HOME/.cache/cbs-ensemble/py` | where `pip --target` put pandas |
| `RESUME` | 1 | 1 keeps finished runs, 0 reruns them |
| `ENSEMBLE_DEPS` | Maven's `target/classpath.txt` | a file holding the dependency classpath, instead of Maven's |

## What it writes, under `$ENSEMBLE_OUT/<label>/`

- `score.md`: the table and the line `criteria passed: n/m`. A criterion with
  nothing to judge says VACUOUS, and one Jerus retired says RETIRED with his
  words (`score.py`'s THE PREMISES RETIRED; H1 since CD1, 2026-10-21): both
  are printed with their numbers and left out of n and m.
- `baseline.md`: that table in the form `docs/ensemble/<version>.md` keeps, with
  a paragraph naming the version, commit, date and label, and the `summary.json`.
- `summary.json`: what `--baseline` reads (T1, T2 and F1 compare with it).
- `<POLICY>-<seed>.csv`, `times/`, `times.txt`, `progress.txt` (a `DONE` line
  a run, or `NO DONE <policy> <seed>`), `logs/` (a log a run, `javac.txt`,
  `score.txt`), `snapshot.txt`, `classes/`, `deps.txt`, `driver/`, `tmp/`.

Exit status: 0 scored; 1 build or driver compile failure (`build first` means
`target/classes` is missing); 2 scoring failed or pandas is missing; 3 runs are
missing (what is there is still scored). `score.py` needs the AUTO2 runs, the twins.

## Cost

About 80 CPU-minutes: a run takes about 130 s for AUTO2, 118 for ZERO_SLACK, 17
for ZERO_HOT and SHOCK, 9 for HIGH. At five at a time that is about 20 minutes
on the PC; in the cloud, one at a time, over an hour.

## Stopping it

    tools/ensemble/ensemble.sh --stop en1

A task manager that stops the script leaves `xargs` and the JVMs running. `--stop`
ends the run's own shell, its `xargs`, the runs' shells and their JVMs, found by
the run's folder in their command lines, so the suite and other runs are left
alone. Do not edit `ensemble.sh` while a run is going: bash reads a script as it runs it.

# The monetary ensemble

Sixteen seeded cities under five policies produce 80 CSVs. `Ensemble.java`
plays LongPlaytest's founding and model, `ensemble.sh` snapshots and runs it,
`score.py` scores the CSVs, and `stop.ps1` stops a Windows run.

## When to run

Run once on the final model when the default playtest trace moves, excluding
its runtime line (EN1). Compare against the newest `docs/ensemble/<version>.md`.
Commit `baseline.md` as `docs/ensemble/<batch id>.md`, with the version in its
heading and introductory line replaced by the ship file's next-version token.
The publisher fills the token and renames the baseline (BP1).
Only M1 (money audit) and F1 (findings against the baseline) stop shipping.
Other criteria retain their scores and changed verdicts as information (EI1).

## Run and resume

In Git Bash at the checkout or worktree root, compile with `mvn -q compile`,
then run `tools/ensemble/ensemble.sh <batch id>` in the background and wait.
Python uses `-I` and finds pandas through `ENSEMBLE_PYLIB`. Install it once
with `python -m pip install --target "$HOME/.cache/cbs-ensemble/py" pandas`.
The cloud lock restricts runs to one JVM where it applies.

A label keeps its first classes snapshot. Restarting it keeps completed CSVs
and times, rerunning the rest. A changed data setting needs a fresh label.
When `JDK_JAVA_OPTIONS` is empty, the runner discovers the main checkout's
`data/earth-2.bin` and `data/world-1.bin`, each if present. Explicit `JAVA_OPTS`
properties override discovered defaults. The baseline names the snapshot's
data settings, including inherited options, or says none (EN2).
EN2.md takes precedence over same-version/older scores until a later version is scored.

## Optional environment

| variable | default | meaning |
|---|---|---|
| `JOBS` | 5, or 1 with the cloud lock | simultaneous runs |
| `SEEDS` | `0..15` | seeds |
| `POLICIES` | `AUTO2 ZERO_SLACK ZERO_HOT SHOCK HIGH` | policies, keeping AUTO2 twins |
| `BASELINE` | newest baseline, including EN2 | Markdown or summary JSON, or `none` |
| `JAVA_OPTS` | empty | extra JVM arguments, such as `-Densemble.months=720` |
| `ENSEMBLE_OUT` | `target/ensemble` | output parent |
| `PYTHON` | working python3, else python | scorer interpreter |
| `ENSEMBLE_PYLIB` | `$HOME/.cache/cbs-ensemble/py` | pandas location |
| `RESUME` | 1 | keep completed runs, or 0 to rerun |
| `ENSEMBLE_DEPS` | Maven's classpath | caller's dependency classpath file |

## Outputs and verification

Under `$ENSEMBLE_OUT/<label>/`, `score.md` holds the criteria, changed
verdicts and hard-only batch verdict. VACUOUS and RETIRED rows keep their
figures and are excluded from the passed count. `baseline.md` combines the
provenance, table and fenced `summary.json` used by the next comparison.
Old JSON without criterion readings reports an unavailable previous verdict.

The same folder keeps each policy-seed CSV and log, `times/`, `times.txt`,
`progress.txt` (DONE or NO DONE per run), `snapshot.txt`, `classes/`,
`deps.txt`, `driver/`, `tmp/` and compiler/scorer logs under `logs/`.
Exit 0 passes, 1 fails setup/compilation, 2 fails scoring/dependencies,
3 has missing runs, and 4 fails hard criteria. Exits 0, 3 and 4 keep scores.
Run `sh tools/ensemble/ensemble-selftest.sh` after runner or scorer edits.
A typical PC run costs about 80 CPU-minutes, 20 minutes five at a time.

Stop with `tools/ensemble/ensemble.sh --stop <label>`. It ends that run's
shells, xargs and JVMs by its folder, preserving other runs. Resume with the
same label. Keep the script fixed while a run is active because Bash reads
it during execution.

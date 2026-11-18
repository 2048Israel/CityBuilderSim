# GR1 - timing judged nightly, not at the gate

Jerus, 2026-11-11, asked whether a missed speed budget should still stop a
batch from shipping: "Judge it nightly (Recommended)". After GF1, on the same
files.

## What the game wants

The gate no longer times anything hard and runs no paired rounds: a clean gate
is the merge, the suite (timing soft, as now) and the records, about 22
minutes. Speed is judged once a night on main's tip, and a real slowdown
becomes a queued fix that names the batch that brought it.

WHY: measured 2026-11-07 to 11-11, rounds ran on 39 of 79 gate runs and took
33.7 of 80.9 gate-hours. Main's merges carry 128 timing verdicts and none
records a batch at fault. About a dozen gate runs ever stopped on timing and
none led to faster game code (GD3's and BO1's were the laptop's load, 5.6 hours
between them). Players get a build only when Jerus pushes one, so a slowdown
found the next morning costs a queued fix, not a release.

## What to build

- **`ship.sh`**: step 9's hard timing run and the rounds go: the
  `batch-check.sh --timing` call, the residual and its judging by
  `timing-pair.awk`, `target/main-timing`, the `Gate-timing:` line, and
  `gate.txt`'s `# timing` and `# figure` lines. `--red-line` then names suite
  lines only. The suite still runs timing soft and says how many assertions
  were SOFT.
- **`tools/nightly.sh`** gains the timing, after its 4,000 months, in the same
  clone of main's tip: `./batch-check.sh --timing` (hard, one harness at a
  time), its figures (`target/checks/timing.tsv`) kept per machine and per
  night under `$HOME/.cbs-timing/`, with the commit they are of. A machine's
  figures are never compared with another machine's.
- **The verdict of a night**, for every timing assertion: inside its budget;
  or over it as last night's was (main's standing miss: named, no alarm); or
  **slower**: over its budget tonight where last night's was inside, or over
  the budget and more than the margin (15%, `CBS_TIMING_MARGIN`) over last
  night's. A slower line is confirmed before it is said: its harness alone,
  last night's commit and tonight's in turn, judged as `timing-pair.awk` judges
  a pair today. Keep the awk: the rounds move here, where they cost no gate.
- **Naming the batch.** For a confirmed slower line the first-parent merges
  between the two nights are searched by halving (the harness alone, paired
  with the last good commit), and the night's verdict names the merge: the
  assertion, both figures, the budget, the batch's id and its version. One
  line in `$HOME/.cbs-nightly.txt` beside the playtest's, the detail in the
  log; `tools/day.sh` shows it in the morning as it shows a red playtest.
- **A first night on a machine** records, and judges against the budgets alone
  ("no figure of last night's here"). That is the gate box's baseline when it
  arrives, and the laptop's after this ships.
- **The load.** On the laptop other work may run at night. The nightly waits
  for a quiet machine as `--timing` waits today, up to a dial, then runs and
  says it ran loaded. A loaded night's slower lines are confirmed by pairs like
  any other; its figures are kept marked loaded and are nobody's baseline.
- **The docs**: `docs/orchestrator.md`, "The gate" 6 goes and 4-5 lose their
  timing sentences; `docs/batch.md`'s first paragraph and `CLAUDE.md`'s Checks
  bullet say where timing is judged now; `ship.sh`'s header loses its timing
  steps. `batch-check.sh --timing` stays: it is the nightly's, and anyone's by
  hand.

Not in this batch: who queues the fix that a night names (the morning's line is
read by whoever keeps the gate; the rule goes in the contract, written last),
the gate box's setup, and the known reds and the pick (GR2).

## The checks

- `tools/ship-selftest.sh`: its timing cases go or move. A gate whose stub
  suite passes ends in a tag with no timing step.
- A self-test for the night's verdict on made-up figure files, no JVM: a
  standing miss, a new miss, a rise inside the budget, a first night, a loaded
  night; and the search over five stub merges names the right one.
- `TimingCheck` stays as it is.

## Done means

The self-tests pass and `./batch-check.sh --pick` exits 0 or 3. A real
`tools/gate.sh --dry-run` of this branch ends with no timing step, and one real
`sh tools/nightly.sh` on the laptop writes its figures and its line. One in,
one out (`CLAUDE.md`): this batch should remove more than it adds; the count in
the notes. The ship file last.

## How it reaches main

No gate run (Jerus, 2026-11-11: "these three batches shouldnt need a gate no?
sure a fresh session"). The dry run above, by the launcher in a fresh clone, is
the full run: its notes say where its kept log is and which two commits it was
of (the branch's and main's). A fresh Claude session, the gatekeeper for GF1,
GR1 and GR2, reviews the diff against this brief, runs the self-tests and
`./batch-check.sh --fast` on the merge, and merges it untagged; a fault comes
back as a numbered list. The ship file is a process batch's (a `## changelog`
block headed as CX2's was, "no tag (process)", and no `{next}`), pushed last.
Cut from main once GF1 is on it.

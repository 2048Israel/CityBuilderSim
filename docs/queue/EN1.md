# EN1 - the ensemble: only when the trace moves, a stored baseline, and premises that mean something (2026-10-20)

The brief for batch EN1. `QUEUE.md` links here. It is tooling, with no game change and no version bump. The orchestrator ships it with `--no-tag`.

Jerus asked whether the 80-run ensemble (16 seeds x 5 policies) is worth it, and said yes to the answer: "yes, do that".

## Why

Over FR2, FR7, FR3 and FR6 (2026-10-18/19) the ensemble never changed a decision:
- **Every run scored 22 of 26, the same as main.**
- **The faults were caught elsewhere.** FR2's were caught by harnesses (CentralBankCheck, ScaleCheck, AutoBuildCheck's village), FR3's save gap by the playtest's reload check, and FX4's currency by Jerus playing.
- **The same 4 premises fail on main itself**, so they say nothing.
- **It costs.** A run takes 35-45 minutes of machine time, and a batch often ran it twice: once on main for a baseline, once on its branch.

The ensemble is still the only check that sees a failure under one policy or one seed, so it stays for the batches that change the economy's balance.

## What to build

1. **The rule, in CLAUDE.md** ("How a batch is done", steps 3-5) and wherever the tree states it, such as README's *The checks*. The ensemble runs only for a batch whose playtest trace moves. A map, screen or fix batch with a byte-identical trace skips it, and says so in its notes.
2. **The runner in the tree.** It now lives only in `workbench/workbench-delta-20261010.zip` (runs/ensemble/: run.sh, score.py, Ensemble.java), plus each session's own copy adapted for the PC, runpc.sh. Bring it into `tools/ensemble/`. It must run on the PC (';' classpath, `target/classes`, gson 2.10.1 from the .m2, Python run as `python -I` with pandas installed by `pip --target`; see the PC memory note "ensemble-on-the-pc") and in the cloud. It runs from a class snapshot, and runs several runners at once, as FR3 and FR6 did. Add one line to the check scripts' header naming it.
3. **A stored baseline.** A trace-moving batch commits its final `score.md`, the scores and not the run CSVs, as `docs/ensemble/<version>.md` at its merge. The next batch compares against the newest one there, and never reruns main to get a baseline. Seed it now: the scores of main's model today come from FR6's final run, fr6s1, which EG1 is using as its baseline. FR6's session scratchpad is under `C:\Users\Jerus\AppData\Local\Temp\claude\...objective-cerf-997b53...\scratchpad\ens\out\fr6s1\`. If it is gone, run the ensemble once on main.
4. **The 4 premises that fail on main.** Name each one, say what it asserts and why it fails on main, and sort it into one of three kinds:
   - the model is wrong, so a fix batch is queued;
   - the premise is out of date, so it is rewritten against the model's own constants, as a harness is;
   - it no longer applies, so it is retired, but only by Jerus's word. That is a ★ call for him, with the numbers in the question.
   The aim is that 26 of 26, or the new count, means something.
5. **The record:** `docs/queue/EN1-notes.md`, EN1's row out of QUEUE.md, and the ★ calls under Pending (EN1-C).

## Done

- the runner runs from `tools/ensemble/` on the PC, five at a time;
- the baseline file is seeded;
- the rule is in CLAUDE.md;
- the 4 premises are each explained, with a fix, a rewrite, or a ★ call;
- the suite is green apart from main's ScaleCheck line;
- pushed for the orchestrator to ship untagged.

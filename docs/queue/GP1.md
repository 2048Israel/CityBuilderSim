# GP1 - the gate starts at once: no quiet minute, and a ready branch picked up when its ship file lands

Queued 2026-10-27 from a side session with Jerus, on his word. The first of
four process batches (GP1, RL1, SP1, BM1); MP1 keeps its place after MS3.

## Jerus, 2026-10-27 (the requirement)

> i feel like before batches could get done fairly quickly, now each time a
> new batch takes longer and longer ... i feel like the beaurcracy has grown
> way too much or perhaps its nescessary idk

Shown a plan and an independent read-only review of it, he took the
review's order: "yes please, do it as soon as humanly possible".

## Why

Measured from git on 2026-10-27:
- Median first commit to shipped: 1-2 h on most days Oct 12-23, 3-6 h on
  most days Oct 24-27.
- From the ship file pushed to the merge: a median of about 60 min over the
  last 16 gates (23-228 min). `ship.sh`'s own run is 22-25 min of it
  (`gate.txt` history: suite 1,011-1,934 s, timing 313-444 s). The rest is
  the gate not starting: the hourly :07 check-in, or a wait on Jerus's word.
  FW8 took 1 min after its last code commit, then 96 min to merge.
- The quiet minute (TM2, `quiet_minute` in `batch-check.sh`) waits up to
  `CBS_QUIET_CAP_SECS` = 1,800 s for no other batch's JVM. With two lanes
  the other lane is often running checks. TM3 now judges a hard timing red
  side by side with main in the same minutes, which is what the quiet
  minute only approximated.

## What GP1 does

1. **The quiet minute out.** `--timing` runs at once. A red under load goes
   to TM3's paired rounds as it does now. Remove `quiet_minute` and what
   serves only it: `tools/quiet-selftest.sh`, the `CBS_QUIET_*` dials, the
   `-Quiet` mode of `tools/jvms.ps1` if nothing else uses it, TM2's prose in
   `docs/batch.md`, `ship.sh`'s header (step 9) and `docs/tree.md`. Keep
   `jvms.ps1`'s count if the soft-or-hard choice at a run's start still
   needs it.
2. **A ready branch picked up when its ship file lands.** The orchestrator
   keeps one background wait that ends when an unmerged `batch/` branch on
   origin gains its `docs/queue/<id>-ship.md` (a `wait.sh` mode, or
   `day.sh --due` in a loop: the batch's call), as a batch session's
   `wait.sh --row` waits today: no tokens while waiting, a gate starting
   within minutes. It is re-armed after each gate. The hourly check-in
   stays, as the fallback for a stuck lane. `docs/batch.md`, "The day",
   says how it is armed.
3. **One in, one out** (Jerus, 2026-10-27, for every process batch): GP1
   removes at least as many lines of script and rules as it adds. Its notes
   give the count.

## Scope and checks

Tooling and `docs/`, no game code; ships `--no-tag`. After a change to
`ship.sh`, `sh tools/ship-selftest.sh`. The new wait is tried against a
throwaway local origin (as `ship-selftest.sh` builds one): a ship file
pushed ends it, other pushes do not.

## Calls

- If the quiet minute still earns its place somewhere (say, the first hard
  run before any pairing), say where with figures. Recommendation: remove it.

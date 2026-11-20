# GB1 notes - the gate runs on the gate box

Jerus, 2026-11-20, after the gate box passed its overnight soak: set up the gate
pipeline on it, "through memwatch and ... started and all via headless agents",
and when the box cannot be reached, "notify me then run on laptop". He also
approved a write deploy key for the box ("Yes, add it").

## The box

`gatebox`: Ryzen 7 9850X3D (8 cores, 16 threads, 5.6 GHz), 32 GB DDR5-6000,
Ubuntu Server 26.04.1, OpenJDK 21.0.13, Maven 3.9.12, git 2.53, on Wi-Fi at
`192.168.0.16` (also `gatebox.local`). Set up outside the repository, nothing
of it tracked:

- the laptop's key logs in as `jerus` with no password; `~/.cbs-gate-box` on
  the laptop names the box, `jerus@192.168.0.16` then `jerus@gatebox.local`;
- `~/cbs/main` on the box, a clone of `origin` over SSH with the deploy key
  "gatebox gate (write)" (`~/.ssh/gatebox_github`, GitHub's host key checked
  against its published fingerprint), `data/earth-2.bin` and `world-1.bin`
  copied in (sha256 as the laptop's); git's name and address as the laptop's;
- `~/cbs/tools/<commit>`, a worktree per caller's commit of `tools/`, pruned
  after three days; `~/cbs/runs`, the runs, on disk (the box's `/tmp` is tmpfs);
- the laptop's `~/.cbs-gate-times.tsv` seeded there, so the first suite orders
  longest first;
- Maven from Ubuntu, and Eclipse Temurin 21.0.13+9 in `~/cbs/jdk` (the game's
  own runtime, `Build EXE.bat`'s; sha256 checked against Adoptium's release),
  named by `~/cbs/gate.env`, which the box's half reads before each gate.
  Ubuntu's OpenJDK ships no `legal/java.base/LICENSE`, and CreditsCheck asserts
  this JVM's.

## What was found

- `tools/gate.sh`, `ship.sh`, `batch-check.sh` and `check-data.sh` already ran
  on Linux (the `SEP`, `uname` and guardian branches): nothing in the publisher
  needed changing.
- The bus is the one thing the box lacks: `tools/bus.sh` does nothing where
  there is no `cbs-bus`, so a gate there would have left MemWatch blind.
- A run directory is a path on the machine that ran it, and a kept run records
  its `origin` URL; a laptop run and a box run cannot reuse each other's
  evidence (`gate-reuse.sh` refuses, and the suite runs fresh).

## What changed and why

- **`tools/gate-box.sh`** (new): the gate on the box.
  - The laptop's half tries each named box (`ConnectTimeout` 8 s) and sends the
    call's words, base64, on stdin. `-F BODY` and `--red-lines FILE` send the
    file's bytes, not its path. `CBS_ME` and `CBS_GATE_DIRECTORY`'s name go too.
  - The box's half fetches `origin` and runs the caller's own commit of
    `tools/` (so a branch's `--branch-gate` dry run tests its own `gate.sh`)
    with `TMPDIR=~/cbs/runs`.
  - The box's `CBS_BUS` is `gate-box.sh` itself: each bus call becomes one
    `@@cbs-bus` line in the stream, posted here through `tools/bus.sh` with its
    body's bytes. MemWatch's `state\gate.txt` and the batch's file read as from
    a gate here, with `machine=gatebox` and the box's log path.
  - A beat every 5 s through the one writer. A caller that is gone (its process
    ended, so the connection closed) stops the gate there, as SR1c's caller's
    exit does here.
  - At the end, the run's files but its clone are copied to the laptop's temp
    directory, and the last line says where.
  - No box answering: an urgent info mail to `jerus` ("The gate box cannot be
    reached (...): ID's gate runs on the laptop.") and the gate runs here.
  - `--stop DIR` stops here when DIR is here, else on the box.
- **`tools/gate.sh`**: two lines, the hand-off when a box is named and the call
  is not already on the box (`CBS_GATE_ON_BOX`).
- **`tools/bus.sh`**: its comment, the box's relay.
- **`tools/ship-selftest.sh`**: `CBS_GATE_HOST=` for every case (none may reach
  the real box), `CBS_GATE_ON_BOX` and `CBS_GATE_RELAY` unset. GB1's cases use
  a fake `ssh` that runs the box's half here:
  - the first of two boxes failing, the second taken;
  - a ship through the box: title with quotes and two spaces, a body file, the
    steps in order with the box's log, the run copied home;
  - a stop through the box: its exit, one urgent mail holding all 14 unnamed
    lines, the clone kept there and not copied;
  - no box: the mail to `jerus` and a ship here.
- **Docs**: the contract's section 2 records the decision and drops "the gate
  box's part" from what is still his. `docs/orchestrator.md`'s heavy slot is
  per machine and its run folders are the box's. `CLAUDE.md`'s Ship line names
  the box.

## Calls taken (each the recommended one)

1. The gate's whole call goes to the box, not the suite alone. The publisher
   (merge, records, tag, one atomic push) is unchanged and runs where it
   checks; the box pushes with its own write deploy key, Jerus's yes.
2. The box runs the caller's commit of `tools/`, not the box's main, so a
   publisher edit's `--branch-gate` dry run still tests the branch's own
   scripts (contract 5.5).
3. A dropped connection stops the gate, as a caller's exit does here (SR1c),
   rather than leaving a gate nobody watches to publish.
4. The heavy slot is per machine: an ensemble here beside a gate on the box
   meets nothing of it.

## Checks

- `tools/ship-selftest.sh` on the box at 8ae9642e: 180 of 180, GB1's groups
  with every earlier case, in 1 m 44 s. Its first run there failed GB1's ship
  and stop cases only: the faked box's home had no git name for the merge, so
  that home now has one.
- On the laptop, the first 70 cases passed. Then it was stopped, since BO1-S's
  real gate was in its suite there (the contract, 4).
- A forwarded `--stop` of a run that does not exist, on the real box: the
  box's message came back with exit 2, in 4.1 s. That covers the per-commit
  tools worktree, the stream and the exit code.
- The full suite on the box under gate conditions (data named from
  `~/cbs/main`, Temurin): known red only, 12 harnesses, exit 3, 9 m 44 s.
  BO1-S's laptop gate was at 48 harnesses after an hour.
- On Ubuntu's OpenJDK, CreditsCheck alone is red ("java.base's LICENSE is
  among them"); hence Temurin.
- Two earlier probes misled and are not how a gate runs:
  - a `data/` link in the working folder reds SaveFileCheck's "no world file"
    fixtures;
  - no data named at all reds GoldStandard, Credit, Mortgage, BuildCard,
    Foreign, DebtMoneys, Curve, Fund and SaveFileCheck's IP charges.
- GB1's dry run through the box (`--branch-gate --dry-run --no-tag`, run
  `/home/jerus/cbs/runs/cbs-gate-GB1-FulzIwBm`): exit 0. It passed `--fast` in
  18 s and the self-test 180 of 180. The run was copied home.
- Its relayed bus calls (logged here, as BO1-S's real gate held
  `state\gate.txt`): merge with `machine=gatebox` and the box's log, suite,
  maps, suite (the self-test), done passed.
- `tools/pick.sh`: NONE of 228 harnesses; review not needed (no money, saves or
  threading).

## One in, one out

`git diff --numstat origin/main` (17f8a4c0) over the process files, the notes
and ship file left out: +150 -13.

- `tools/gate-box.sh` is 94 new lines, its self-test cases 35, and the docs net
  +6.
- It is a new mechanism, and nothing it replaces could go: the laptop keeps
  its Windows guardian (`tools/gate-process.ps1` and `gate.sh`'s Windows
  branches, about 130 lines) because Jerus chose the fallback, "then run on
  laptop".
- Over by 137. If he ever drops the fallback, those 130 lines go with it.

## Left for Jerus

- **HA1-CLOCK** waited on "the quiet gate box"; the box is here. Its row stays
  under Pending until your review moves it (the contract, section 6).
- **The nightly** (`tools/nightly.sh`, hard timing) would judge speed best on
  the quiet box. Its schedule is still yours (asked 2026-11-17); moving it is
  a batch of its own.
- **MemWatch** shows the box (its heat, load, memory and the gate running
  there) through its own repository's session, started beside this batch.
- **The suite's CPU guard** holds harness starts above 80% CPU on the box as
  on the laptop. A box that runs nothing else could run wider; tuning that is
  a call of its own.
- **The first gate of GB1** runs on the laptop with main's `gate.sh`. That
  `gate.sh` cannot see the box's kept dry run, so it reruns the compile, maps,
  Stale and self-test there. Every gate after it runs on the box.

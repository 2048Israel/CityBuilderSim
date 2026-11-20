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
  longest first.

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

(filled in below as they ran)

## One in, one out

(counted below)

## Left for Jerus

- **HA1-CLOCK** waited on "the quiet gate box"; the box is here. Its row stays
  under Pending until your review moves it (the contract, section 6).
- **The nightly** (`tools/nightly.sh`, hard timing) would judge speed best on
  the quiet box. Its schedule is still yours (asked 2026-11-17); moving it is
  a batch of its own.
- **MemWatch** shows the box (its heat, load, memory and the gate running
  there) through its own repository's session, started beside this batch.

# GB2 notes - the nightly runs on the gate box

Jerus, 2026-11-20, after GB1 moved the gate: "the nightly timing run needs to
also move to the gatebox". As with the gate, when the box cannot be reached he
is notified and the night runs on the laptop. He was away; nothing here needed
him.

## What was found

- The nightly's laptop-only parts already had Linux branches, never run:
  `nightly_owner` reads `/proc/PID/stat` and the boot id, `nightly_lock` uses
  `mv -T`, `timing_quiet` asks `pgrep` and `/proc/loadavg` (under 20% of the
  cores), and `cygpath` is only called where it exists. The box's `awk` is GNU
  Awk 5.3, so `timing-pair.awk` and `observations.awk` run as they do here.
  Nothing in `nightly.sh` or `nightly-timing.sh` needed a Linux fix.
- `nightly.sh` takes its checkout from its working folder, and GB1's serving
  half ran from `~/cbs/main`, whose working tree is never updated: a night
  there would have run the box clone's stale `nightly-timing.sh`.
- GB1's relay (`CBS_BUS` = `gate-box.sh`, each bus call an `@@cbs-bus` line in
  the stream) is not the gate's own: the night's red mail goes home through it
  unchanged.
- A gate's `active` file (`~/cbs/runs/*/active`) stays behind when a gate is
  killed, and the nightly would then wait on it every night; a `flock` dies
  with its holder.

## What changed and why

- **`tools/nightly.sh`**: one line, the hand-off, as `gate.sh`'s: a box named
  (`CBS_GATE_HOST`, else `~/.cbs-gate-box`) and not already on the box execs
  `tools/gate-box.sh --nightly`. Its header says so in the same lines.
- **`tools/gate-box.sh`**: the program is a word of the call (`p`), `gate.sh`
  or `nightly.sh`; everything else is GB1's.
  - The laptop's half: `--nightly` sends no arguments; the sender is
    `CBS_ME`, else `nightly`. With no box answering it mails `jerus` "The gate
    box cannot be reached (...): the nightly runs on the laptop." and runs the
    night here.
  - After the stream, the box's `.cbs-nightly.txt`, `.cbs-nightly.log`,
    `.cbs-nightly-tier-green` and `.cbs-observations/` are copied to `$HOME`
    here, where `day.sh` and MemWatch read them. In the verdict, the
    observations' path is rewritten to the copy here and the timing figures'
    path names the box (`jerus@192.168.0.16:.cbs-timing/gatebox/...`). The
    timing store stays per machine (GR1), so it is not copied.
  - The serving half runs the nightly from its tools worktree, the caller's
    commit, as it runs the gate.
  - **The box's lock**, `~/cbs/box.lock`: a gate holds it shared and the
    nightly exclusive, each for its whole run. A `--stop` takes none. A
    waiter prints why it waits, then a line a minute; a caller that is gone
    ends the wait on that line. A second nightly is refused at once
    (`~/cbs/nightly.lock`, exit 3), not queued for a second night.
- **`tools/ship-selftest.sh`**: GB2's cases on GB1's faked box. The box's tools
  are `gate-box.sh` and `bus.sh` beside a stub night, entered through the real
  `tools/nightly.sh`:
  - the hand-off, the night run from its own checkout, its exit kept;
  - the verdict, log and records home with their paths;
  - the red mail relayed;
  - no box: the mail to `jerus` and the night run here;
  - where `flock` exists (the box, not Git Bash): a second nightly refused,
    and a gate waiting while the night holds the box, then shipping.
  The fake `ssh` runs a `cd && ...` command with the box's home.
- **Docs**: `docs/orchestrator.md`'s nightly paragraph, the contract's gate-box
  bullet (section 2), and `CLAUDE.md`'s and the README's lines on `--timing`.

## Calls taken

1. **Which way round** (the brief asked): first come holds the box, the other
   waits. The nightly holds the box for its whole run, not only its hard
   timing. Recommended, and taken:
   - the 4,000-month playtest and the nightly tier (its harnesses in
     parallel, KeptCheck among them) are as heavy as an ensemble;
   - a gate's suite beside them risks the harness time-outs that stopped
     R2c-P's dry run (KeptCheck at 1,740 s beside HN1's ensemble), and a
     stopped gate costs a rerun;
   - the night is once a day and short on the box (figures below);
   - a gate is never stopped for the night, and the night never times beside
     a gate.
2. The night waits for a gate without a bound, as a gate waits for the night.
   Each has its own bound: the gate's harness time-outs, the night's steps.
   The orchestrator serialises gates, so a waiting night gets the box in the
   first gap.
3. The box's first night is its own baseline (GR1's "a first night on a
   machine records, and judges against the budgets alone"): its timing store
   is `~/.cbs-timing/gatebox`, new. The laptop's store, `Jerusalem`, stays for
   the nights the box is down.
4. The box's `.cbs-nightly-tier-green` and `.cbs-observations/` were seeded
   from the laptop's (outside the repository, as GB1 seeded
   `.cbs-gate-times.tsv`), so a red tier or a missed observation on the box's
   first night names merges since the laptop's last night, not "no earlier
   run". The copy home keeps the laptop's current for a night the box is down.

## Checks

(filled in below as they ran)

## One in, one out

(count below)

## Left for Jerus

- **A daily start** (asked 2026-11-17) is still his: mailed today with the
  options and a recommendation; nothing was added.

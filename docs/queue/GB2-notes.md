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
- The box keeps UTC and the laptop AST (-0400); the real night's verdict said
  13:23 when it came home at 09:23. A night started after 20:00 here would be
  dated tomorrow, and `day.sh`, which asks whether a verdict of today's exists,
  would then skip the next night. Setting the box's zone needs `sudo`. A gate
  there is dated in UTC too, but MemWatch reads a box gate's `ship.log` as UTC
  and shifts it, so the gate keeps UTC.
- GB1's relay garbled box paths (MemWatch's session found it): Git Bash
  converts a native program's arguments, so a relayed `log=/home/jerus/...`
  reached `cbs-bus.exe`, and `state\gate.txt`, as
  `C:/Program Files/Git/home/jerus/...`. The self-test's bus is a sh stub, so
  nothing caught it. The night's relayed mail goes through the same `post()`.

## What changed and why

- **`tools/nightly.sh`**: one line, the hand-off, as `gate.sh`'s: a box named
  (`CBS_GATE_HOST`, else `~/.cbs-gate-box`) and not already on the box execs
  `tools/gate-box.sh --nightly`. Its header says so in the same lines.
- **`tools/gate-box.sh`**: the program is a word of the call (`p`), `gate.sh`
  or `nightly.sh`; everything else is GB1's. A second new word (`z`), sent
  for a night only, carries the caller's UTC offset, and the box runs the
  night with `TZ` set from it, so its verdict is dated as `day.sh` asks.
  - `post()` calls `bus.sh` with `MSYS_NO_PATHCONV=1` and
    `MSYS2_ARG_CONV_EXCL='*'` (Git for Windows honours the first, MSYS2 the
    second), so relayed paths arrive as the box wrote them. It is set there
    only: a local `--file` still needs the conversion, and a relayed call
    sends its body on stdin, never a local path. `bus.sh` passes its words
    straight to the exe and writes no file of its own.
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
  - the night's dates in the caller's zone (+0900 there, the fake box on UTC);
  - the verdict, log and records home with their paths;
  - the red mail relayed;
  - no box: the mail to `jerus` and the night run here;
  - where `flock` exists (the box, not Git Bash): a second nightly refused,
    and a gate waiting while the night holds the box, then shipping.
  The fake `ssh` runs a `cd && ...` command with the box's home.
- **`tools/day.sh`**: `nightly due` also asks that the last night ran on
  another `VERSION` than main's (Jerus's rule, below). Main's `VERSION` and
  the last verdict's date and version are read before `--due`. The full
  report says when the next night waits for a new `VERSION`. `CBS_DAY_HOUR`
  sets the hour for the self-test.
- **`tools/wait-selftest.sh`**, which covers `day.sh`: five cases. No night
  yet; another version due at 03:00; not at 12:00; not twice a night; main's
  own version skipped.
- **Docs**: `docs/orchestrator.md`'s nightly paragraph (his decision, his
  words and the WHY), the contract's gate-box bullet (section 2), and
  `CLAUDE.md`'s and the README's lines on `--timing`.

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

- **On the box**: `tools/nightly-selftest.sh` (at a1e91a15), 67 PASS,
  "nightly selftest OK"; `tools/ship-selftest.sh`, 191 of 191 in 2 min 18 s
  at a1e91a15 and 192 of 192 in 111 s at 83c2dc29 (the `z` word), GB2's lock
  cases among them.
- **On the laptop** (at the merge with main, ad104e91, and the `z` word):
  `batch-check.sh --fast` exit 0; `tools/ship-selftest.sh` 188 of 188 in
  37 min beside HS2's ensemble, and 188 of 188 again at e001d0f9, its `flock`
  cases skipped, as Git Bash has no `flock`.
- **One real night**, main's tip 919c1fa41, 2026-11-20 08:56-09:23, started by
  `sh tools/nightly.sh` here (the orchestrator held the box's gates):
  "nightly: on the gate box jerus@192.168.0.16, tools at cbe266812", exit 1.
  - The verdict, log, tier-green and observations arrived in `$HOME` here;
    `day.sh` read the verdict. Its paths came out as meant:
    `/c/Users/Jerus/.cbs-observations/misses.tsv` and
    `jerus@192.168.0.16:.cbs-timing/gatebox/2026-11-20-919c1fa41-...`. Its
    stamp, 13:23, was the box's UTC: the `z` word above came after this night.
  - 4,000 months OK in 1,020 s. The laptop's last, on 11-18, took 699 s, on an
    older main (HS1, OP1 and BO1-S's owners came since); not compared further.
  - The nightly tier: 40 harnesses in 416 s, the known reds, and one NEW red,
    PrizeCheck section 12 ("at most one lab a field a month"). It is **main's,
    not the box's**: red the same way on the laptop at 919c1fa41. PrizeCheck
    is in `AllChecks.NIGHTLY`, so no gate has run it since the last green tier
    (873de9427). Told to the orchestrator; not bisected here.
  - Observations: 7 misses. Timing: `loaded=0`, so the night is the box's
    baseline. 168 lines OK, none SLOWER. One first-night miss: MapCheck's
    sidecar, written and read back in 714.6 ms against `SIDECAR_MS` (500 ms).
    A second such night makes it a standing miss; its budget on this machine is
    the orchestrator's to take up.
- **The dry run**, `sh tools/gate.sh GB2 --branch-gate --dry-run --no-tag` on
  the box at e001d0f9 against main 44e0aa66: exit 0, run
  `/home/jerus/cbs/runs/cbs-gate-GB2-YTx8IWE8`. No harness reads what changed;
  the fast check, the nightly self-test, the publisher's (192 of 192) and the
  wait self-test (25 of 25) ran. A first one, at 83c2dc29 before Jerus's
  schedule and the relay fix, was green too (`cbs-gate-GB2-24Ro7Vbj`).
- **The relay's paths, for real:** that dry run's relayed state put
  `log: /home/jerus/cbs/runs/cbs-gate-GB2-YTx8IWE8/ship.log` in
  `state\gate.txt`, where the gate before it had
  `C:/Program Files/Git/home/jerus/...`. Still converted, and not GB2's to
  change: a box path a session names in its own `bus.sh` call on the laptop,
  such as the ready line's `run=` (contract 6). `bus.sh` cannot turn the
  conversion off for every call, as a laptop gate's `log=` is a local path
  MemWatch needs converted. GB2's own ready line set `MSYS_NO_PATHCONV=1`.
- **`tools/wait-selftest.sh`** on the laptop: 25 of 25, the five new cases
  among them; on main's `day.sh` the "skipped" case would fail.
- **HA1-CLOCK**, not folded in: on the quiet box, SaveSlotCheck's median due
  month is 17.2 ms against `CLOCK_MS` (25 ms), where HA1 found about 29 ms on
  the laptop. The budget holds on the box with room, so nothing is changed.
  Setting it from the box's figures is HA1's call, item 3.

## One in, one out

`git diff --numstat` from the main the branch last took (42881af6; PZ1 since
touches none of these files) over the process files, the notes and ship file
left out: +118 -36.

- `tools/gate-box.sh` +38 -15, `tools/nightly.sh` +6 -6, `tools/day.sh` +7 -4;
  the self-tests' cases +51 -3 (`ship-selftest.sh` +37 -2, `wait-selftest.sh`
  +14 -1); the docs +16 -8.
- It is a new mechanism: the box's lock, the program as a word, the records
  home. Nothing it replaces could go, as the laptop still runs the night when
  the box is down, so `nightly.sh`'s Windows branches stay.

## The daily start (his call, asked 2026-11-17, decided 2026-11-20)

Jerus, 2026-11-20, in the orchestrating session's chat: "yes i think it should
run every night IF the version number has changed". Earlier that day he had
answered "B" to the four options mailed to him: A, the orchestrator on
`nightly due`; B, a laptop task at 02:30; C, MemWatch at a set hour; D, a cron
on the box.

**Chosen: B, the laptop's task, with the version test in `day.sh`.**
- A cron on the box runs with the laptop asleep. But the laptop has no SSH
  server, so the box cannot send its verdict or its red mail home. The laptop
  would have to fetch both, a second copy of the night's mail and verdict
  logic.
- A night started on the laptop comes home through GB1's relay like any call:
  the verdict copied, the mail posted, the laptop's own run when the box is
  down. It needs the laptop awake at 02:30, as the orchestrator, MemWatch and
  the builders already need it.
- The test is in `day.sh`, so a start by hand on `nightly due` follows the
  same rule.

**Registered** by the orchestrating session on his "yes do it now"
(2026-11-20): daily at 02:30 from 2026-11-21. Its command, `sh.exe -lc`:

    cd ~/Documents/NetBeansProjects/CityBuilderSim && sh tools/day.sh --due | grep -q ^nightly && V=$(grep -m1 String.VERSION.= src/main/java/ham/citybuildersim/GameVersion.java | tr -dc 0-9.) && { read d t L r < ~/.cbs-nightly.txt; } 2>/dev/null; [ x$V != x$L ] && sh tools/nightly.sh

- `nightly due` comes once a day, 01:00 to 07:00, while no verdict of today's
  exists and the last verdict's `VERSION`, its third field (kept so), is not
  main's.
- The task's own test, the checkout's `VERSION` against that third field, stays
  as a guard. It reads the working tree, which the orchestrator pulls after
  each ship; `day.sh` reads `origin/main` after its fetch.
- A night still running when the other starter comes is refused: by the box's
  `nightly.lock`, or by the laptop's `.cbs-nightly.lock` when the box is down.
- Until GB2 is on main, the task's guard alone applies the version rule, and a
  night runs main's `nightly.sh`, on the laptop.

## Left for Jerus

Nothing: the task is registered.

# BW1 notes - the workflow says where it is: state and mail through MemWatch's bus

Built 2026-11-18 by a Claude session (Opus, xhigh), headless through MemWatch's
launcher, to `docs/queue/BW1.md`. Scripts and docs only; untagged.

## What was found

- MemWatch's side was there (`cbs-bus gate`, `state`, `send`, SPEC.md 3, 4
  and 7); nothing in the workflow called it.
- `cbs-bus` wants a sender for every command, `gate` and `state` too
  (`CBS_ME`, `--as`, or a registered folder). `ship.sh` runs in a clone under
  the temp folder, which no one registers, so a gate started without `CBS_ME`
  (by hand) would have failed every call. `tools/gate.sh` sets `CBS_ME=gate`
  when it is unset; the nightly sends as `nightly` then.
- `cbs-bus gate` merges keys into the file while the batch is the same, so a
  second run of one batch would show the last run's `outcome` all through its
  suite: the gate's first post clears the end keys.
- `--run` must be a positive integer. The gate's N is this batch's kept run
  folders (`cbs-gate-ID-XXXXXXXX`) beside this one, plus one: the first gate
  of a batch on a machine is run 1, a dry run counts.
- The maps are made inside `batch-check.sh`, which prints its maps line only
  when they are done, and a change to `batch-check.sh` makes the gate run the
  whole suite. So `ship.sh` reads the check's output as it streams: the maps
  step begins at the suite's verdict line (at a fast check's compile line).
- The unnamed lines come from the Java judge with CRLF; the mail drops the CR.
- WC1's notes counted five stop messages in `ship.sh` citing `docs/batch.md`;
  there were four (the missing ship file, a ship file for another id, the
  VERSION line, records edited by the branch). All four cite
  `docs/contract.md` now.

## What changed and why

- **`tools/bus.sh`**, the one way in: its arguments to `cbs-bus` (`$CBS_BUS`,
  else `C:\Users\Jerus\Tools\MemWatch\cbs-bus.exe`), nothing where there is
  none, a call cut off after 60 s, exit 0 always; a failing call prints one
  `bus ... failed (exit N)` line on stderr, which the gate keeps in its log.
- **`tools/gate.sh`**: before `ship.sh` starts, `gate ID merge --run N` with
  `commit`, `base`, `version` (main's), `machine`, `log` (`C:/...` on the PC),
  `dry_run`, and the end keys empty; a real gate moves the batch to `gating`.
  At the end, `done` or `stopped` with `outcome`, `published` (main moved on
  origin, from the refs before and after), `reused` (the `--reuse` run unless
  refused), `known_reds` (the entries that passed lines), `skipped` (harnesses
  skipped for want of JavaFX) and `timed_out` (harnesses whose red line is a
  timeout), and `version` when it published. Then one message to the batch:
  on a publication `info`, "ID shipped as V" (untagged: "ID shipped untagged
  on V"); on a stop `instruction`, urgent, its body the stop's reason, what to
  do, the log, and every unnamed line. A real gate's batch file becomes
  `shipped` with its version, `sent-back` with the reason, or `ready` again
  with a note when the stop was not the batch's.
- **The outcomes**: `tagged`, `pushed` (published untagged), `passed` (a green
  dry run); stopped: `new red`, `conflict`, `records` (a stop before the
  checks began: the ship file, the branch, the version), `hung` (a check that
  did not finish), `cancelled` (`--stop`, the caller, a signal), `push
  refused` (main moved, or the push failed), `failed` (any other stop during
  the checks). They are read from the log's last STOPPED line and the
  cancellation report, so they work with every publisher and when `ship.sh`
  was killed.
- **`ship.sh`**: `step merge` (with the version it would set), `suite` as a
  check starts, `maps` (above), `suite` again before the changed scripts'
  self-tests, `tag`, `push`; only under the launcher (`$CBS_GATE_RUN/launcher`),
  never on a direct run, and only where the tree has `tools/bus.sh`. A change
  to `tools/bus.sh` runs `ship-selftest.sh`; a change to any
  `tools/nightly*.sh` runs `nightly-selftest.sh` (`nightly-timing.sh` had no
  self-test of its own name, so it ran none).
- **The night**: `nightly_timing` keeps the batches it names (`SLOWER_BY`);
  `nightly_mail` (in `nightly-timing.sh`, so the self-test can call it) sends
  the verdict, with a first line naming the batch, to `jerus` and the gate's
  keeper (`CBS_GATE_KEEPER`, else `orchestrator`) when the playtest is red
  (naming the batch whose merge is main's tip) or a line is slower (naming the
  batch the bisection found).
- **The docs**: the contract's section 3 has the batch's `building`,
  `blocked` and `held` lines, 5.6 the `ready` line and the stop's mail, 6 what
  the gate posts; `docs/orchestrator.md`'s waking is cut to the fallback it is
  (an app session no mail wakes), the nightly's mail, the prompt naming the
  state lines, no relaying of a send-back, and its gate step 5 shorter;
  `docs/tree.md` has `tools/bus.sh`.

## Calls taken (each the recommended one)

1. A dry run moves only the gate's file, never the batch's own: a dry run is
   the builder's check, and `gating` or `sent-back` would mislabel a batch
   still building. Its stop still mails the batch, as Done means asks.
2. A green dry run sends no mail: the builder that started it is waiting on
   it in the foreground.
3. A stop that is not the batch's (`cancelled`, `push refused`) puts the
   batch's file back to `ready` with a note, and its mail says there is
   nothing to mend; it is still one urgent instruction, as the brief says.
4. The night mails only a red playtest or a slower line, the brief's words:
   a red nightly tier or an incomplete timing run alone is not mailed (it is
   in the verdict and `day.sh` as before). Say if a red tier should mail too.

## Checks

- `./batch-check.sh --fast` on the merge of main 2d895d646 (0.9.149): compile
  OK, maps regenerated, Stale 0 firm findings, tree clean but the notes and
  ship file; after the rules' cuts, `./batch-check.sh StaleCheck` at
  aec321fd0 and again at f05c19035 (the heavy-slot bullet): compile OK,
  StaleCheck OK, Stale 0 firm, tree clean. The pick (`sh tools/pick.sh
  origin/main HEAD`) is NONE: no game code; review not needed (no money,
  saves or threading in the diff).
- `sh tools/ship-selftest.sh .` whole, at 2ade01672: 168 of 168. After the
  `pid` and `started` keys and the two `bus.sh` cases cut (the gate's BT3 and
  BT4 cover them), its setup and BW1 cases alone: 19 of 19. The gate runs it
  whole again (the changed scripts' self-tests).
- Its BW1 cases, on a stub `cbs-bus` named by `CBS_BUS` that logs every call
  and message: BT1, a tagged gate, posts merge, suite, maps, tag, push, done
  in order, each with the two commits, the first with main's version, the
  machine, the log, the pid and the start; done tagged, published=yes; the
  batch's file gating then shipped with its version; one info message
  "BT1 shipped as V". BT2, 14 NEW red lines: stopped, outcome=new red,
  published=no; one urgent instruction holding all 14 unnamed lines; the
  batch sent back. BT3, no `cbs-bus`: the gate ships the same, tells nothing.
  BT4, a stub exiting 2: the gate ships, every call made, the failure in its
  log. The cancelled gate's case (`--stop` on a held stage) ends stopped,
  cancelled, published=no, one urgent message, no state lines (a dry run).
- `sh tools/nightly-selftest.sh`: OK, with its new cases: a green night sends
  nothing; a red playtest sends two messages, to `jerus` and `orchestrator`,
  naming ST5, the batch whose merge is the tip; a slower line names ST3, the
  batch the bisection found.
- No self-test reaches MemWatch's real bus: both set `CBS_BUS` to a path that
  does not exist before any case.
- On the laptop: `sh tools/bus.sh state BW1 building ...` wrote
  `cbs\state\batches\BW1.txt` (step building, the builder, model, session and
  branch). Main 28ee126e8 (0.9.150: R2c-P, WG1-R, R1f) merged first, touching
  none of BW1's files, `--fast` green on the merge (891532002).
- `sh tools/gate.sh BW1 --branch-gate --dry-run --no-tag`, on the
  orchestrator's go: **exit 0**, kept run `/tmp/cbs-gate-BW1-quCobx0m`
  (15:21-15:55): `--fast` green, `nightly selftest OK`, `selftest: 167 of
  167 passed`, the tree as it was. `cbs\state\gate.txt`, watched every two
  seconds, went merge (15:21:16), suite (15:21:37), maps (15:22:17), suite
  again for the self-tests (15:22:38), done (15:54:57): run 1, both commits,
  version 0.9.150, machine, `C:/...` log, dry_run yes, pid, started, then
  outcome passed, published no, known_reds 0. No message, and the batch's
  own file left at building.
- The same, stopped by hand past its maps step (`sh tools/gate.sh --stop
  /tmp/cbs-gate-BW1-QFKNpj23`, exit 130): `gate.txt` went merge, suite, maps,
  stopped, run 2, outcome cancelled, published no. Exactly one message, in
  BW1's own mailbox (`bus\BW1`, the only message file written in the bus
  since the run began): an urgent instruction, "BW1's dry run stopped,
  cancelled: stopped by --stop; origin refs unmoved", "Nothing of the batch's
  to mend.", the log, "0 unnamed line(s):". It woke this session through the
  mail hook, as a launched builder's would be.

## One in, one out

`git diff --numstat origin/main` (28ee126e8) over the process files, the notes
and ship file left out: 211 added, 78 removed, 133 more.

| file | + | - |
|---|---|---|
| `docs/contract.md` | 9 | 3 |
| `docs/orchestrator.md` | 51 | 59 |
| `docs/tree.md` | 2 | 0 |
| `ship.sh` | 19 | 8 |
| `tools/bus.sh` (new) | 11 | 0 |
| `tools/gate.sh` | 45 | 1 |
| `tools/nightly-timing.sh` | 12 | 2 |
| `tools/nightly.sh` | 2 | 1 |
| `tools/nightly-selftest.sh` | 11 | 0 |
| `tools/ship-selftest.sh` | 49 | 4 |

The rules a session reads balance, 62 in and 62 out (the orchestrator's call
of 2026-11-18, on the first count of 14 more): the contract's state lines, the
stop's mail and the gate's posts (+6) and `docs/tree.md`'s entry (+2), against
`docs/orchestrator.md` eight lines shorter, with the heavy slot's bullet the
orchestrator gave (no ensemble beside a dry run or a gate, +2). Its waking is
cut to its fallback (the check-in's minute and re-arming out, MemWatch's
drawing in), its gate
step 5 from seven lines to four (the self-tests by name are `ship.sh`'s
`selftests()`), its relaying of a send-back gone, and the rest of its prose
tightened with no rule, decision or quote out. Gone as said elsewhere: the
docs pass's `.md`-only `--fast` (the contract, 6), `Batch check.bat`'s no
`--pick` and no exit 3 (`CLAUDE.md`), "the older text is in git history".
The scripts (+77) and their self-tests (+56) the orchestrator
accepts as the new capability Jerus queued: nothing in the tree told MemWatch
anything, and the log scraping it replaces is MemWatch's to drop (SPEC 7).

## Left

- A gate whose own shell is killed outright (not stopped: `kill -9`, a power
  cut) posts no end: `gate.txt` keeps its last step. Its `pid` key lets
  MemWatch see the gate is gone.
- MemWatch reading `state\`, who starts a chain's next batch and how the gate
  box reaches the folder stay outside this batch, as the brief says.

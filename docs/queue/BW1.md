# BW1 - the workflow says where it is: state and mail through MemWatch's bus

Jerus, 2026-11-11, in the MemWatch session, on the direction "the workflow
writes its state; MemWatch draws it": "yes what you said, im loving it"; and on
messages: "the orchestrator can message codex batch through memwatch, and codex
reads it via memwatch". MemWatch's side was built the same day: the bus and its
`cbs-bus.exe`, the launcher that starts and resumes Claude and Codex sessions,
and the Messages window. Its design note says what is left: "nothing in the
game's workflow uses the bus yet. Teaching the batch docs, `ship.sh` and the
orchestrator to post state and mail is the workflow sessions' part." This is
that part. After GR2, on the same files.

## What exists (the MemWatch repository, `SPEC.md` 3, 4 and 7)

`C:\Users\Jerus\Tools\MemWatch\cbs-bus.exe`, called by full path. `state <id>
<step> [--waits-on X,Y] [--note TEXT] [key=value ...]` writes a batch's file,
`gate <id> <step> [--run N] [key=value ...]` the gate's, and `send <to>
[--kind K] [--urgent] [--re ID] (--body TEXT | --file F | -)` a message.
Everything is `key: value` text under `C:\Users\Jerus\cbs`, and unknown keys
are kept. A session the launcher started is resumed when mail reaches it,
urgent mail at once. The sender is `CBS_ME`, or `--as <name>`.

## What to build

- **One way in, `tools/bus.sh`**: it finds `cbs-bus` (`$CBS_BUS`, else the
  path above) and passes its arguments on. Where there is none (the gate box,
  a cloud session) it does nothing and exits 0. A failing call never fails a
  gate or a check: state and mail are told, never depended on.
- **The gate says where it is.** `tools/gate.sh` and `ship.sh` call `gate <id>
  <step> --run N` as each step begins (merge, suite, maps, tag, push) and
  `done` or `stopped` at the end, with these keys: `commit` and `base` (the two
  commits), `version`, `machine`, `log` (the kept log), and at the end
  `outcome` (tagged; or stopped: new red, conflict, records, cancelled, hung,
  push refused), `published` (yes or no), `reused` (the run it took, if any),
  `skipped` and `timed_out` (harness names) and `known_reds` (how many entries
  passed lines). The batch's own file moves with it: `gating`, then `shipped`
  with its version, or `sent-back`.
- **The gate's outcome is mail.** At its end the launcher sends the batch's
  mailbox one message. On a tag, `info`: "<id> shipped as <version>". On a
  stop, `instruction` and urgent, its body the stop's reason and the whole list
  of unnamed lines. A builder the launcher started is resumed by it, so a
  send-back needs no person and no second session.
- **A batch says where it is** (`docs/batch.md` and the builders' prompt):
  `state <id> building` when it starts, with `builder`, `model`, `session` and
  `branch`; `ready` when its ship file is pushed, with `commit`, `base`, `run`
  (its kept dry-run log, when it made one), `files` (the source files it
  changed) and `moves_playtest` (yes or no); `blocked` or `held` with a note.
- **The night is mail too.** `tools/nightly.sh` sends its verdict to `jerus`
  and to whoever keeps the gate when the playtest is red or a timing line is
  slower (GR1), naming the batch.
- **The docs** lose what this replaces as far as it is replaced: the
  ready-branch wait and the hourly check-in of `docs/orchestrator.md` stay only
  as the fallback they are.

Not in this batch: who starts the next batch of a chain and what the
orchestrator becomes (WC1's open questions), MemWatch reading `state\` (its
own repository), and how the gate box reaches the folder.

## The checks

`tools/ship-selftest.sh` with a stub for `cbs-bus` named by `CBS_BUS`. Every
step's call is made, in order, with the two commits. A stopped gate ends in
`stopped`, `published=no` and one urgent message that holds every unnamed line;
a tag ends in `done` and one `info`. With no `cbs-bus` at all the gate runs and
ships the same, and a stub that exits 2 changes nothing either.

## Done means

The self-tests pass. A real `tools/gate.sh --dry-run` on the laptop shows its
steps one after another in `C:\Users\Jerus\cbs\state\gate.txt`, and a dry run
stopped by hand leaves `stopped` there and one message, in the batch's mailbox
and no other. One in, one out (`CLAUDE.md`), the count in the notes. The ship
file last. It reaches main through the gate as it then is: no game code, so the
harnesses it changes and the script self-tests.

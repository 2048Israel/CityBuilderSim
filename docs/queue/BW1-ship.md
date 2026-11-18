<!-- gate-run: /tmp/cbs-gate-BW1-quCobx0m -->
## changelog
### BW1 — the workflow says where it is — 2026-11-18, ready, see docs/queue/BW1-notes.md
The workflow tells MemWatch where it is instead of MemWatch guessing from logs: `tools/bus.sh` is its one way to
`cbs-bus`, doing nothing where there is none and never failing a gate or a check. `tools/gate.sh` and `ship.sh` post
each step the gate begins (merge, suite, maps, tag, push) to `state\gate.txt` with the commits, main's version, the
machine, the log, the gate's pid and start, then done or stopped with the outcome, published, reused, skipped,
timed_out and known_reds; a real gate moves the batch's own file to gating, then shipped with its version or
sent-back. A publication is one info message to the batch, a stop one urgent instruction holding its reason and every
unnamed line. The nightly mails `jerus` and the gate's keeper when the playtest is red or a timing line slower, naming
the batch. The contract gives the builders' state lines (building, ready, blocked, held); `docs/orchestrator.md`'s
waking is cut to its fallback; `ship.sh`'s stops cite `docs/contract.md`. Scripts and docs only.

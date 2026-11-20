<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-GB1-FulzIwBm -->
<!-- readiness: provisional -->
## changelog
### GB1 — the gate runs on the gate box — 2026-11-20, ready, see docs/queue/GB1-notes.md
The gate and its dry runs run on `gatebox`, the quiet Ryzen box beside the laptop (Jerus, 2026-11-20). `tools/gate.sh`
hands its whole call to the new `tools/gate-box.sh`, which runs this checkout's commit of `tools/` there over SSH, its
runs in `~/cbs/runs`, started headless exactly as before. Its output comes back as written and its bus calls are posted
here, so MemWatch's gate and batch state read as before, with `machine=gatebox`. The run's files, but not its clone, are
copied home at the end. A dropped connection stops the gate there, as its caller's exit does. When no box answers,
`jerus` is mailed and the gate runs on the laptop ("notify me then run on laptop"). The box pushes with its own write
deploy key. The heavy slot is per machine. Scripts and docs only.

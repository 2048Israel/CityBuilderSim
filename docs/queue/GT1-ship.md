## changelog
### {next} — GT1, faster gates: the playtest at 2,400 months, the full 4,000 nightly, main's two timing reds halved — 2026-11-02, ready, see docs/queue/GT1-notes.md
The gate plays the long playtest for 2,400 months (81 s against 149 at 4,000; the same city through m2400). The full
4,000 run once a night on main's tip: `tools/nightly.sh`, which the hourly check-in starts when `day.sh --due` says
`nightly due` (01:00-07:00), its verdict in `day.sh` and the next gate's `gate.txt`. Main's two timing reds halved
where a quiet run shows it: the month that autosaves 17.2 to 8.2 ms (the history copied on the writer's thread), the
map's month at 10 billion 5.8-8 to 2.3-3 ms (only the districts a month changed are read). ConservationCheck's food,
electricity and water laws ride on every month of the playtest, its own 156-month city gone. No two harnesses grow
the same city, so none is shared. README states no harness count; `AllChecks` writes each harness's time to
`target/checks/times.tsv`. Found: the PC's commit charge (96%) runs the gate's suite one harness at a time.
## pending
| GT1-T | The timing lines main misses only at a loaded gate, each inside its budget run quiet: at FC1's gate MapCheck's sidecar (SIDECAR_MS 500), its FX-side month at 5B (19 ms) and KeptCheck's kept chrome (8 ms); and MapCheck 5's first month at 10B after GT1, up to 5.9 ms loaded. A gate pays the paired rounds (~20 min) whenever one misses. Halve them in code as GT1 did two ({next}) | GT1 ★2: halve them, or let the paired rounds judge them | [GT1 notes, The calls](docs/queue/GT1-notes.md) |

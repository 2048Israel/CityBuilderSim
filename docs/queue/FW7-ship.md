## changelog
### FW7 — a known red told from a NEW one — 2026-10-25, ready, no tag (tooling), see docs/queue/FW7-notes.md
`AllChecks -q` once showed a second red inside a harness that was already
red (HealthCheck's upkeep check) as only "HealthCheck FAILED". Now
`AllChecks -q` and `-f` read main's record of its reds (`docs/notes/gate.txt`,
which BP1 began and only `ship.sh` writes) and say `known red` or `NEW red: N
of M red lines` of each failed harness, and `-f` tags every red line `[known]`
or `[NEW]`. `batch-check.sh` hands AllChecks the record it judges against,
fails a run where the two judges count differently, and gains `--judge LOG`;
`ship.sh`'s gate line says `known red only` or `NEW red`. KnownRedsCheck (new)
causes both cases with a fixture harness and judges its logs through the
script. The model is untouched: no trace moved, no ensemble.

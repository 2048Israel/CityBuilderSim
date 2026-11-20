<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-GB2-24Ro7Vbj -->
<!-- readiness: provisional -->
## changelog
### GB2 — the nightly runs on the gate box — 2026-11-20, ready, see docs/queue/GB2-notes.md
The nightly (`tools/nightly.sh`: the 4,000-month playtest, the nightly tier and the hard timing) runs on `gatebox`,
the quiet box that judges speed best (Jerus, 2026-11-20: "the nightly timing run needs to also move to the gatebox").
It hands its night to `tools/gate-box.sh --nightly`, GB1's way to the box: this checkout's commit of `tools/` runs
there, and its red mail comes home through GB1's relay. The box is held whole: a gate holds `~/cbs/box.lock` shared
and the night exclusive, so whichever comes first runs and the other waits. A second night is refused. The verdict,
log, tier and observation records come home, where `day.sh` reads them, and the timing figures stay per machine. The
box's first night is its baseline. The box, on UTC, runs a night in the caller's time zone, so it is dated as `day.sh`
asks. When no box answers, `jerus` is mailed and the night runs on the laptop. It starts by itself, a laptop task at
02:30 on `day.sh`'s `nightly due`, now only while the last night ran on another `VERSION` (Jerus, 2026-11-20: "it
should run every night IF the version number has changed"). GB1's relay passes the box's paths to MemWatch unconverted
(Git Bash had made `/home/jerus/...` `C:/Program Files/Git/home/jerus/...`). Scripts and docs only.

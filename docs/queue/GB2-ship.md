<!-- readiness: provisional -->
## changelog
### GB2 — the nightly runs on the gate box — 2026-11-20, ready, see docs/queue/GB2-notes.md
The nightly (`tools/nightly.sh`: the 4,000-month playtest, the nightly tier and the hard timing) runs on `gatebox`,
the quiet box that judges speed best (Jerus, 2026-11-20: "the nightly timing run needs to also move to the gatebox").
It hands its night to `tools/gate-box.sh --nightly`, GB1's way to the box: this checkout's commit of `tools/` runs
there, and its red mail comes home through GB1's relay. The box is held whole: a gate holds `~/cbs/box.lock` shared
and the night exclusive, so whichever comes first runs and the other waits. A second night is refused. The verdict,
log, tier and observation records come home, where `day.sh` reads them, and the timing figures stay per machine. The
box's first night is its baseline. The box, on UTC, runs each call in the caller's time zone, so a night is dated as
`day.sh` asks. When no box answers, `jerus` is mailed and the night runs on the laptop. How the nightly starts is
unchanged: his "B", a laptop task at 02:30, is his to register (the line is in the notes). Scripts and docs only.

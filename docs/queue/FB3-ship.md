<!-- readiness: provisional -->
<!-- gate-run: /tmp/cbs-gate-FB3-CP19hJD1 -->
## changelog
### {next} — FB3, reloads that read as they were saved — 2026-11-17, ready, see docs/queue/FB3-notes.md
A loaded city now reads as it was saved in four more places. The shops' want and means are put back always, zeros too: a city saved in its
second month, when its shops want nothing, came back wanting what the load's rebuild struck (0.0625; MoneyDatesCheck). An older save's
contracts on site are read after the load strikes the builders' wages, not at the founding ladder before it, so a House either side of the
rental rebate's line gets the loaded city's share (.0000625 against 0; InvestCheck green, a premise line that the line lies between). The
draws made between the presses cross a save, so a hand order keeps its seller's row; the month closes its own before its autosave, which a
load would otherwise fill twice. The hot money's peaks cross a save, so the Trade card's "the most that was ever here" is the city's.
Groceries, housing, luxury and meals now begin each month's trades afresh, where they grew for the city's whole life. ReloadCheck causes the
stop, the hand order and the limestone carry no checkpoint reaches, asks that its skips had an outbreak and a sick month, and loads the
month-end autosave; SaveFileCheck places the hot money by name (HZ1). No save format bump; the playtest's trace is unchanged.
## queued
| QW1 | LandNationsCheck's hover line, "a real level-12 seller split yields to the next hover within eight frames", waits on the wall clock (a 10 s `await`, then eight quoter frames) inside a correctness harness, so it misses with an ensemble beside: 10,001.66 ms in FB3's dry run, 3.42 ms alone. ★ Recommended: keep the correctness half (the split yields, the next hover begins) as a count, not a clock, and move the frames bound to the timing harnesses (`--timing`, judged nightly) | docs/queue/FB3-notes.md, "The dry run" |
## known-reds
LandNationsCheck | a real level-12 seller split yields to the next hover within eight frames | QW1 | 2026-11-17 | 10,001.66 ms in FB3's dry run beside LX1's ensemble, the next hover's measure never begun inside the 10 s await; green alone on the branch at 3.42 ms: a wall-clock line in a correctness harness, not FB3's code

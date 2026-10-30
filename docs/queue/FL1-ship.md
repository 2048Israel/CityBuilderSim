## changelog
### {next} — FL1, a works making a flow good stops running at a loss — 2026-10-30, ready (Jerus: "Ship, fix next (Recommended)"), see docs/queue/FL1-notes.md
A works that makes a flow good - rod, wire and cable, pig iron, steel, pellets - no longer buys its inputs for exports
that sell under them. WN3's order rule held a works' inputs to what its month would make only for goods that keep, so
with the world file named Rotterdam's Rod Mills went on importing cathode at 120.1 to ship rod at 68.8 after a January
re-solve (GDP -997k), and Medellin's sank rod's world price to 3.33 against cathode at 443 (GDP -60.7M). Now a flow
good's home demand and its works' own need are made at any price; the spare is exported only while it nets over the
building's own inputs (an input the sector makes itself at what not using it saves), and on the world's slope only so
far as keeps next month's price 2% over that cost; a by-product comes out of its run. SlopeCheck and LandmassCheck
green; no negative-GDP month left in Rotterdam, Medellin or the playtest's founding (7, 16 and 0 before); the rule cuts
rod in 37-122 months of a run. Ensemble 23 of 26 against 0.8.76 (R2 newly red, R3 passing); with the world file named,
SaveFileCheck's bank reads 28 more equity after a load on FL1's path, queued.
## pending
| FL1-R | SaveFileCheck's bank after a load, with `world-1.bin` named ({next}): the fund city's bank reads 64,614.92 of equity just after its load against 64,586.70 live, no month played, its paid-in, retained and preferred unchanged; green on slice A's model, red since the by-product's run share and the captive input's cost moved the city's path. Not traced: which of the bank's cash reserves, net loans or securities is rebuilt from what a save does not keep | a finance batch to trace it (recommended: after EM2, which works in the bank's lane) | [FL1-notes §5](docs/queue/FL1-notes.md) |
| FL1-E | The ensemble's R2 on FL1 with HD2 ({next}): a 15% rate's unemployment over its twin, median +0.32 points against the +0.50 asked (FL1 on WN3 +1.35, HD2 alone +2.86), its seeds -1.84 to +8.46 | WN3-R's monetary study (recommended: read it there, with R3) | [FL1-notes §4](docs/queue/FL1-notes.md) |
| FL1-G | The gate names `world-1.bin` beside `earth-2.bin` ({next}): WN3's world market has never run at a gate, so WN3's two reds, FL1's fix and FL1-R are seen only in batch sessions that name both | the orchestrator's (`docs/orchestrator.md`, The PC; recommended: name both) | [FL1-notes §5](docs/queue/FL1-notes.md) |
| FL1-N | A flow good the world will not buy is still made at the rate and its unsold lost ({next}): cutting it to last month's demand could leave a buyer short where it cannot be imported either | a later batch on the order | [FL1-notes §3](docs/queue/FL1-notes.md) |

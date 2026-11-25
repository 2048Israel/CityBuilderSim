<!-- readiness: provisional -->
## changelog
### {next} — R1g, the 1900 city grows — 2026-11-24, ready, see docs/queue/R1g-notes.md
A city founded the 1900 way grows now, and the founding screen and the playtest (the gate's audit) found it by default: today's city under
`-Dplaytest.founded=today`. It had stalled at a few hundred people, because 1901's wages paid for today's homes (rent 0.61 of take-home at
month 60) and today's diploma posts met 1901's workers. Its homes are built to 1900's standard, and their cost moves with the households' pay,
on its last twelve months' mean, capped at today's home (Jerus: "its cost moves with WAGES"; `-Dr1g.walk=off` holds it at 0.29 of today's).
With no school, the young learn the year's trade at work as apprentices. On 0.9.174 the playtest's 1900 city held 1,852,269 people at month
2,400, its rent .307 of take-home on average. The walk's months are saved, with no format bump. Village1900Check proves the stall, each fix
and the walk. The city's repairs to its own buildings are government consumption now, paid or owed: their imported material came off GDP
with nothing against it, so a 1900 city whose building stopped read GDP below zero (GdpCheck). The foreign contractor's points are earned at
his price the month he builds them, their material on top, out of the contracts whose payers paid it, never more: the 1900 village, whose own crews are a handful,
earned them at its sites' rate and imported them at his, and read GDP below zero at month 15 (ForeignCrewsCheck). The 1900 playtest is the
slower (38 minutes for 2,400 months on the PC against 18). A farm fitting its last tractor now clears its old captive feed, so its crop
agrees after a reload.
## pending
| R1g-H | revisit the 1900 home once HO1 (owning a home) ships ({next}) | Jerus, 2026-11-20: "perhaps try once people mortgages exist? idk" | [R1g notes §6](docs/queue/R1g-notes.md#6-jeruss-pick-the-homes-cost-moves-with-wages-09174) |
## queued
| PB1 | The 1900 city's unsettled fees, and LongPlaytest's own-line bounds on the city the gate now plays (R1g's sweep, 2026-11-21): the fees a row is billed for treatment and tuition that go to a cell too small to settle (`HouseholdBalance.java:1274`) run at 524 and 239 ppm of what moved in the 1900 city's months 1-240 and 241-480 (404 and 287 with R1g's switches off), against today's city's 96 and 37 in the same months, so the gate's playtest, the 1900 city since R1g, reads a mean monthly share of 86 ppm against `OWN_BOUND_UNSETTLED_FEES` (3e-5, set on today's city, `LongPlaytest.java:1368-1369`). Find why the 1900 path's fees go unsettled at four to eight times today's share, and set the nine bounds again from the 1900 city's own runs (the gate's 2,400 months, the nightly's 4,000), unless HL2 has deleted them first | [R1g notes §6, the m2405 red](docs/queue/R1g-notes.md#the-m2405-red-households-unsettledfees-traced-pb1) |
## known-reds
LongPlaytest | the households' own line + households UnsettledFees ran past its bound | PB1 | 2026-11-21 | main's latent fault (HL1's named line, HL2 the split) that only the 1900 path reaches at four to eight times today's share; the gate's playtest founds the 1900 city since R1g, and the bound was set on today's: held 69.3 ppm, the mean 86.1, against 30 (R1g notes §6)
HousingDoorCheck | fixture: the window holds an order the door would have refused for the bank's capital, put to the owners (MC1) | FS11 | 2026-11-21 | the 1900 founding moves the watched city: no order in its window meets the asserted bank-capital refusal; the same branch with -Dplaytest.founded=today passes. The housing and cash-identity assertions pass (R1g notes section 7)

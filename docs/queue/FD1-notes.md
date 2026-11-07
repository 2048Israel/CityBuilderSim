# FD1 - notes: Freight keeps its last station while it has a station's freight ({next}, 2026-11-07)

*{next}, 2026-11-07. [The brief](FD1.md); MF1's ★2, from its trace of FX7's ★4 (Jerus: "Into MF1"). Built on FX9's tip 7152ea8d (RL1). The model moves: the trace moves, so the ensemble ran (section 4). Save format unchanged. One Sonnet slice ([its brief](FD1/slice-A.md)); the probe is beside these notes ([FD1/FD1Probe.java](FD1/FD1Probe.java)).*

## 1. Counted first, on today's model

The project's rule: count how often it fires in a real run. BA1 (build ahead), RT1 (retirement in money) and R1b (the motor trucks) all touched Freight's investment after MF1 counted, so the count was taken again before anything changed, with a probe that logs every building a sector sells and every order, by type (`FD1/FD1Probe.java`, a scratch copy of `LongPlaytest` with a monthly hook; `FD1Ens` runs it on the ensemble's seeds).

| run | last station sold | with a first station's freight (30,000 t) | bought again | within 120 months | the same month |
|---|---|---|---|---|---|
| MF1, default playtest, 3,140 months (before BA1, RT1, R1b) | 16 | - | 16 | 16 (3 to 93 months) | 4 |
| default playtest, 4,000 months, today | 4 (m1039, m1171, m3166, m3844) | 3 | 3 (27, 79, 646 months) | 2 | 0 |
| the ensemble's 16 AUTO2 seeds, 2,400 months each, today | 16 (in 9 seeds) | 16 | 16 (0 to 529 months) | 11 | 2 |

**It moved; it has not gone.** In the default playtest it fell from 16 to 4, the lags from months to years; across the seeds it is as common as MF1 found it, two of the sixteen sold and ordered again in the same month.

**The cause is one rule.** Every last-station sale, playtest and seeds, was the DISTRESS rule (`BusinessInvestment.planDistressRetirement()`: 24 months of losses, the sector overdrawn), which sells a quarter of the sector's biggest sellable holding, used or not, and for a lone station that is the railway. `Freight.mayRetire()` kept the last station only while a spur or a line stood (FR7). With neither, the station went, and `plan()` ordered a first station again as soon as the freight offered reached its rung (`firstStation().getStationTonnes() * MIN_LINE_UTILISATION`, 30,000 tonnes a month) and the money gate passed.

**Selling the station mends little of the loss.** The sector is the railway, the trucks and the berths' crews together; its interest on its bonds stays after the sale, and the railway's own bill was well over its running cost at m1171 and m3844 (under it at m1039 and m3166):

| sale | tonnes offered | rail bill | rail running | the sector's interest | its net | the plot | bought again for |
|---|---|---|---|---|---|---|---|
| m1039 | 122,319 | 1,243 | 2,113 | 3,336 | -4,841 | 0 (abandoned) | 339,090 (m1066) |
| m1171 | 52,602 | 15,250 | 5,439 | 14,537 | -9,464 | 20,486 | 990,354 (m1250) |
| m3166 | 10,940 | 1,802,995 | 2,889,070 | 98,135,207 | -191,765,916 | 62,077,206 | (m3812, 646 months later) |
| m3844 | 103,672 | 261,153,911 | 30,739,505 | 504,404,725 | -709,959,440 | 197,892,911 | not by m4005: "borrowing ban, 132 more months" |

($k, the month's figures.) Selling the station stops no interest and ends the one division that earned: m3844's railway billed $261m a month against $31m running. The one sale with too little freight to buy a station for (m3166, 10,940 tonnes) is the one the city rightly did without for 646 months.

**Other buildings.** Across the seeds the other types a sector sells and orders again within 12 months are a few in a hundred (Wood Pellet Mill 14 of 315, Mini Blast Furnace 13 of 256, Rod Mill 2 of 227), within 120 months about half: the spare-capacity rule's cycle through a boom and a slump, not a rule selling what the same month's planner orders. Left as it is.

## 2. What was built

The brief's second way, the one the figures support: **the last station is kept while the city has a station's freight for it**. The brief's first way (no sale while the orders would buy it back within a horizon, for every building) was not taken: outside the railway the sales bought back within a year are a few in a hundred, and a horizon would have to predict the planner.

- `Freight.mayRetire()` (`sectors/Freight.java`): the last station (one standing) is refused while a spur or a line stands, as since FR7, **or while `wouldOrderAgain(t)`**. Both retirement rules ask it, so the distress rule, the one that sold it, now finds nothing of the railway's to sell.
- `Freight.wouldOrderAgain(station)`: whether selling the station would leave the stations short by the first station's rung. That is the stations' shortfall with its tonnes added back (`shortfalls().station() + station.getStationTonnes()`) against `firstStation().getStationTonnes() * MIN_LINE_UTILISATION`. It is plan()'s own rung, so the guard and the order cannot drift.
- `Freight.firstStation()`: the catalogue's smallest station, the loop plan() had, which plan() now calls.
- `RailDepotCheck` 9, eleven assertions, every figure from the templates and the constants:
  - a lone depot offered the rung is kept, and a tonne under it may go;
  - the premise from both sides: no station and the rung's tonnes orders a Rail Depot, a tonne under orders nothing;
  - the distress rule (`DISTRESS_LOSS_MONTHS` of losses, overdrawn) sells nothing at the rung and sells the depot a tonne under;
  - a lone terminal is kept the same way;
  - with two depots standing one may go;
  - a spur, a Truck Depot and null are no station.

  Three of its assertions were red on the old rule: the slice ran them with the guard's new condition taken out. Section 8 is as it was; its fixtures offer nothing.

## 3. Measured after

The same probe on the mended model:

| run | last station sold | with a first station's freight | bought again | within 120 months |
|---|---|---|---|---|
| default playtest, 4,000 months | 2 (m1070, 5,192 t; m3034, 15,449 t) | 0 | 1 (59 months, the freight back) | 1 |
| the 16 AUTO2 seeds | 3 (8,523, 11,428 and 27,872 t) | 0 | 2 (29 and 52 months) | 2 |

What is left is the sale the rule means to allow: the freight gone under a station's rung, the station sold, one ordered when the freight is back. The playtest's audit is clean over the 4,000 months ("Nothing. Every month passed the audit and every reload matched").

The rest of the railway's sales follow the city's own path, which moved from m1039: over 4,000 months 46 Rail Depots sold against 61, 77 Rail Spurs against 58, nearly all in the slump after m2400, as before.

## 4. The checks and the ensemble

- **The ensemble** (`docs/ensemble/FD1.md`, 80 of 80 runs, 13.5 min): 23 of 26 criteria, as the 0.9.26 baseline. The three reds are its own (A2, A3, S1), at its medians: A2's longest spell 16 months, A3's deflation spell 5 against 4, S1's gap 2.00%. AUTO2's final population is 603,462 against 602,188. No run has a LongPlaytest finding.
- **`--pick`** (24 harnesses, LongPlaytest among them for Freight's 44 readers). All green but LongPlaytest, exit 4: its one finding `a building on the map moved 2 time(s), months 1203-1810` (worst, m1810, district (-1,0): 99 of its 914 boxes). That is the layout watch's class, which DM2 mends (the wall ways dated; BA1-D and LD2-D passed the same line by name on their paths). FD1's diff touches no map code. The city's path moves from m1039, where the station is now kept, so the map's moves fall in other months. FX9's move counter (`FX9/FX9Moves.java`, 2,400 months) counts 272 boxes in 60 month pairs on this path, against FX9's 147 in 27. Two of them fall in the watch's 13-month windows (m1200, m1800); main's do not.
- **RailDepotCheck**, RailCheck, HomeRailCheck green. Three of section 9's assertions were red on the old rule.

## 5. Left

- The playtest's watch red on this path: DM2's class, passed by name (Jerus, 2026-11-04: a red outside the batch's own code), in `## pending` as FD1-D, DM2 mending it.
- The probe's counts outside the railway: the sales bought back within a year are a few in a hundred. That is the spare-capacity rule's cycle; nothing is queued for it.

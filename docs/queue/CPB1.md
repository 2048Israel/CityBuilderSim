# CPB1 — Cathode buffered at a negative feed spread

HW1 finds this on the combined HW1/BF1 economic path. The monthly
production and national-account code is unchanged from main 0.9.102,
commit 1e5dffa8. Independently compiled whole-main code reproduces both
negative-GDP states. Orchestrator 20261114-180838 directs passing this
outside-batch finding by name with a queued fix, under Jerus's 11-04 rule.

## Cause

CopperRefining inherits the monthly stock/input planner from Sector.
Sector.getPlannedOutput, lines 2577–2587, targets
getStockCapacity(g) * DUMP_THRESHOLD when local demand is zero.
DUMP_THRESHOLD is 0.8. That buffer target has no input-spread gate.
Sector.forecastOrder at 3011–3018 includes this planned buffer in the
share ordered, even when getExportBoundOutput is zero. The share reaches
inputToMake at 3158–3162 and bid at 2736–2737. Markets calls planOrders
at 173, imports the missing feed at 382–384 and produces stock at 208.

ProcessingSector's capital planner and SpreadPlanner's earnings gate
judge building investment. They do not veto this recurring buffer fill.
ProcessingSector.shipUnsoldStock at 114–123 gates sale of existing stock
separately. Its dump rule can release stock past the buffer while the
next monthly input plan refills it.

The resulting imported anode is more valuable than the cathode made
for an empty local market. EconomyManager 2282 sums the struck import
cost into raw imports. NationalAccounts 617 books the change in held
cathode at the current price, 648 includes raw imports in NX, and 689
sums C + I + G + NX. C, I and G remain positive in both failing cities.
The larger negative NX from feed imports makes total GDP negative.

## Replays

The final ensemble is HW1-bf1-final, immutable a0efd26d. AUTO2 and
ZERO_SLACK share the failing seed paths before the switch at m1800.
Seed 3 first reports negative GDP at m735. Seed 9 first does at m1514.

| Seed/month | C | I | G | NX | GDP |
|---|---:|---:|---:|---:|---:|
| 3 / 735 | 1893.825249 | 93670.692371 | 1144.337949 | -105892.083067 | -9183.227498 |
| 9 / 1514 | 65059.173243 | 502700.930228 | 33638.197345 | -614359.380296 | -12961.079479 |

Both policies' bounded reproductions retain their original GDP values
in target/checks/hw1-gdp-{AUTO2,ZERO_SLACK}-{3,9}.log and matching CSVs
under target/hw1-gdp. The retained pre-month scratch saves are
target/hw1-gdp/AUTO2-3/data/saves/slot-10.json at m734 and
AUTO2-9/data/saves/slot-10.json at m1513, with their history companions.

Copying each save to two independent scratch roots and pressing one
month with the complete 0.9.102 model and the HW1 snapshot produces
identical components and negative totals on both models. The loaded
totals are -9183.460216829 and -12994.955525359. Loading changes a small
government/import amount from the uninterrupted run, so the direct
equality is between the two models on the same copied state.
Logs are target/checks/hw1-gdp-replay-{main0102,hw1}-{3,9}.log.
Main's source and compiled classes are retained at
target/hw1-gdp/main-0102, with its successful compile log
target/checks/hw1-gdp-main-0102-build.log.

At pre-m734 cathode has zero local demand, anode costs 3.033784105
per unit and cathode's export/local price is 2.694686929.
At pre-m1513 the corresponding anode price is 8.596353592 and
cathode 8.216458133, again with zero local demand.

## Fixture and done condition

The reduced whole-main probe is
target/hw1-gdp/probe-src/CopperBufferProbe.java, with output
target/checks/hw1-gdp-buffer-main0102.log. It uses a real Electrolytic
Refinery, an empty yard and zero local cathode demand. Cathode's
world import/export prices are 10/9 and anode's are 11/10.
The input cost per cathode is 11.056701031. planOrders nevertheless
chooses share 1, bids for 9750 anode units and plans 9700 cathode,
with zero export-bound output. The normal Markets.clearMonth,
strikeSectors and updateNationalAccounts path then books 9700 held
cathode, imports 107264.328309080, inventory 87300 and
GDP -19964.328309080.

Turn that premise into a hard regression. Prevent the unprofitable
no-demand buffer fill and its imported feed while preserving actual
domestic/captive demand, profitable buffer replenishment, existing-stock
sales, the input/output identity and the raw GDP finding. Check the real
market path and the two seeded cases through their failing windows.
Keep the existing stock, flow, captive-use and national-account laws.

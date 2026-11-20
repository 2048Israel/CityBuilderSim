# OE1 - a first plant weighed at the rate it will run at

The order's estimate for a sector with nothing running (`QUEUE.md`; the brief is DQ1's notes §7). Built by a
Claude session, headless, on `batch/OE1`, cut from origin/main e2f399d7c (0.9.162).

## 1. Found

- **The rate an empty sector reads.** `Sector.getOperatingRate()` is the fill of the sector's posts times the city's
  power, water, roads, health and freight. With no posts the fill is 1, so a sector with nothing running read
  its staffing as whole, its roads as the sector's last ratio, and its freight as 1 (no tonnes, no legs), and
  every order of a first plant weighed that (`BusinessInvestment.operatingRateOf()`, `Sector.estimatedMonthlyProfit()`,
  `BusinessInvestment.estimatedMakerProfit()` line 1172, the readers in `sectors/`, SpreadPlanner's game frame).
- **What it cost.** DQ1 §7: BankCheck 19's Lime Kiln was weighed at 0.97 and ran at 0.52-0.61 (staff 0.80-0.89 x
  health 0.86 x freight 0.68-0.80); AUTO2's Fabrication Shop at 0.81, and it ran at 0.51-0.81. On main
  YoungBankCheck's town (section 3, run on the old estimate) weighed nine first plants at their sectors' rates of
  0.83-0.92; the five that ran three months alone ran at 0.41-0.87 (Vehicle Works 0.886 weighed, 0.595 run on
  average; Construction Materials Plant 0.888 and 0.694; Bottling Plant 0.829 and 0.737).
- **The spell.** The town's spell under `Bank.CAPITAL_RATIO` began with the write-off of a sector of one plant:
  lowest ratio -0.2334 at month 105, 19 months under 0.08 in one spell, 19 months in resolution, 5,190 people at
  month 121 (the two YoungBankCheck rows in `docs/notes/known-reds.txt` name OE1).

## 2. What changed

- **`Sector.plannedRate(NewUnit)`** (Sector.java 956, banner "A FIRST PLANT IS WEIGHED AT THE RATE IT WILL RUN AT"):
  the rate an order weighs one more building at. A sector that runs nothing (`runsNothing()`, line 238: no posts
  set by the month's `updateWages`) weighs its first plant at `firstPlantRate(u)` (974); any other sector at its own
  rate, as before. `plannedRate(u, running)` (961) takes the reader's own rate for a running sector (the
  refiners' `plantRate()`).
- **`firstPlantRate(u)`** is the month's own product over the new building: `rateOf()` (928), the one product
  `getOperatingRate()` now calls too, with the staffing as `fillOf()` (223, the read `updateWages` makes, now one
  member) over the building's own posts (`NewUnit.getJobs()`), the roads as `roadsFor()` (982:
  `InfrastructureManager.throughputFor()` over the template's own traffic, as the month's road ratio reads a mix),
  and the freight as `firstPlantFreight()` (1464).
- **`firstPlantFreight(u)`**: 1 for a plant with no bulk legs (`bulkLegs()`, the loops `hasBulkLegs()` ran, now over
  the building's own makes and uses), else the haulage half at `Haulage.openingShare()` (Haulage.java 1216: the
  newest share, with teams), through the same `haulageAt()` `getHaulageRatio()` now calls. `getHaulageRatio()` is
  unchanged in behaviour.
- **The readers.** `estimatedMonthlyProfit(NewUnit)` (3671) and `estimatedMakerProfit()` (BusinessInvestment 1172);
  CoalMining 346, ExtractionSector 208, FoodProcessing 407, GoldMining 87, Manufacturing 300, Oil 1611, Quarrying
  363; SpreadPlanner's `City.rate(NewUnit)` (384, default `rate()`, the game frame's override at 581) read by
  `earns()` (407) and ProcessingSector's `estimate()` (222, 248) and `weigh()` (397). ProcessingSector's
  `lastEarns`/`lastOneEarns` keep `rate()`: they weigh a standing plant.

## 3. The calls (★ = the recommended way, taken)

- ★ **The staffing is the city's fill over the plant's own posts**, not the staffing gate's share. Measured at
  YoungBankCheck's first orders (a probe, not committed): `Staffing.share` (with a year's draw) read 0.84-0.96 and
  `shareNow` 0.17-0.63, against runs of 0.41-0.87; the fill read sat nearest the runs (the Vehicle Works weighed
  0.725, the Construction Materials Plant 0.739). The gate answers whether the city can staff the order, a
  different question, and it already holds an order it cannot.
- ★ **The vans are whole**: a first plant's vans are bought with it (`fleetCapital` in the order's cost), so its
  van half opens at 1; the haulage half reads Haulage's opening share, FR8's "a works just opened runs slower",
  a true zero room when the fleet is short and nothing grew.
- ★ **The roads are the plant's own mix** (`BuildingsTemplate.loadOf()` per `Traffic` stream), as the month's
  `EconomyManager.setRoadRatio()` reads one, not the empty sector's last ratio.
- ★ **"Nothing running" is the posts test**, the read the month's own rate stands on: a sector with a plant on site
  and none open still weighs its first plant (the probe's m15 Fabrication Shop, ordered while the m9 Works was still building).
- ★ **A running sector is untouched.** `plannedRate()` returns the sector's own rate whenever it has posts: run on
  the old estimate (a probe switch, removed), the new YoungBankCheck gives main's figures to the digit.
- **ExtractionSector 314** is a local `rate` nothing reads; left as it is.

## 4. The measure: YoungBankCheck

YoungBankCheck (rewritten: its header, §1's second line, and a new §3) on the mend, against the same harness on
the old estimate (the probe switch, removed before any commit):

| | old estimate (main's figures to the digit) | OE1 |
|---|---|---|
| §1 lowest ratio | -0.2334 at month 105 | 0.0812 at month 78 |
| §1 months under 0.08 (spells; begun by a lone plant) | 19 (1; 1) | 0 (0; 0) |
| §2 months in resolution; people at month 121 | 19; 5,190 | 0; 5,499 |
| §3 five first plants that ran 3 months alone: missed their runs by, in all, as weighed | 0.676 | 0.250 |
| ...and at their sectors' own rates | 0.676 | 0.757 |
| verdict | 3 FAILED | all passed |

§3 causes the young town's first orders (every sector that runs nothing is read before each month; a new site
there is its first order) and reads each one's weighed rate against the months its plant ran alone (12 at
most, `RUN_MONTHS`; at least 3 to count, `RUN_MONTHS_AT_LEAST`). On the mend: the Vehicle Works weighed at 0.725
(its sector's own rate 0.887) ran at 0.597 on average; the Construction Materials Plant 0.739 (0.886) ran at
0.714; the Bottling Plant 0.678 (0.845) at 0.653; the Fabrication Works 0.893 (0.918) at 0.821; the Contact
Centre 0.824 (0.831) at 0.825. Its three lines: a first plant ordered and run 3 months alone; none weighed further
from its run than its sector's own rate; and in all nearer, the line the old estimate fails (0.676 either way,
since it weighed every first plant at its sector's rate).

The estimate still weighs high, by 0.03-0.13: what the plant meets once it opens, the town's fill thinned by its
own posts and its first months' freight, is the month's, not the order's. The bank's spell is gone: main's began
at Materials' second plant, ordered at month 60 while the sector ran nothing and weighed at the empty sector's
0.85; on the mend its first plant is weighed at 0.739 (run 0.714), and the town's orders after it are its own (the
Bottling Plant at month 60, main's at 102).

On the 0.9.167 merge (HS2's two adult stages moved the town): lowest 0.1147 at month 62, 0 months under, 0 in
resolution, 4,711 people at month 121; four first plants ran three months alone, missing their runs by 0.326 in
all as weighed against 0.723 at their sectors' own rates; all passed.

The AUTO2 measure (the bank's months under `CAPITAL_RATIO` to m600, sixteen seeds, DQ1 §1's method) is §8's.

## 5. Harnesses the mend moved

- **The by-hand estimates.** CementConcreteCheck §4, CopperRefiningCheck §6 and OilCheck rebuilt an order's
  estimate to the bit with the sector's own rate; a first plant's now weighs the frame's rate for the unit. Each
  now reads the model's: `city.rate(city.unit(t))` (the two `weigh` helpers),
  `plannedRate(plans.newUnit(kiln))` (CementConcreteCheck 285) and
  `plannedRate(plans.newUnit(refinery), plantRate())` (OilCheck, two lines).
- **AutoBuildCheck's two fixtures** stood on the played cities' paths, and each now causes its condition:
  - §4, "the saved city turned people away from general care for its fee" (SZ1's): the decades' city on the mend
    let everybody it had a bed for pay (affordability 1 at month 600). Before the save it plays a month at
    `TaxPolicy.MAX_HEALTH_FEE_SCALE`, the fees then put back (and told to the service, as the month tells it):
    248 people priced out, carried through the load to the cent; its 36 months after still order (17).
  - §7, "the young city is short of general care": retired of its clinics the city keeps the founding's care,
    1,200 places. At month 60 it had 1,206 people on the old estimate and 1,193 on the mend (a probe of this
    fixture on both, the switch removed): a threshold crossed by chance, not a smaller city. The loop that plays
    on with the pass off now also plays until the city has outgrown the founding's care, within the same year:
    1,224 people at month 61. The section's assertions after it pass as before.

## 6. Left

- **OE1-A** (pending): `BuildAdvice.fieldEarnings()` (lines 2199 and 2247) weighs an offer's iron fields at the
  mines' own rate. A field offered to a city with no mine reads the empty sector. R1g and FB13 hold BuildAdvice;
  `plannedRate()` there is a one-word change once they have shipped.

## 7. Checks

- `./batch-check.sh --fast`: exit 0 (compile, maps, Stale 0 firm, the tree only this batch's).
- Each harness alone, `./batch-check.sh <name>`, exit 0 on the final code: YoungBankCheck, AutoBuildCheck,
  CementConcreteCheck, OilCheck, CopperRefiningCheck (19.5 minutes: its seed-1 child ran to its end, so the
  known-reds row CL7 holds for it passed too).
- On the model as committed (95cb16832), before the harness edits, each alone and green: CoalMining,
  CopperMining, FoodProcessing, Quarrying, SulphideLeach, Haulage, Manufacturing, NewUnit, WoodProducts,
  GoldMining, Materials, Bank, PolicyPreview and BuildAdvice. Red there: CementConcrete, CopperRefining and Oil
  (§5's by-hand estimates) and AutoBuild (§5's two fixtures), each mended and green above.
- AutoBuildCheck's one OBSERVED line, "fixture: it borrowed, on the funding page's bond, at least once", not
  reached, is main's too (the kept gate runs of FB14 and SQ1b print it).
- The old estimate: YoungBankCheck 3 FAILED (§4), the red the mend's harness must cause.
- The pick, `sh tools/pick.sh origin/main HEAD`: ALL 228 harnesses (BusinessInvestment.java, 59 readers); review
  "not needed - no money, saves or threading in the diff". So a dry run, after the ensemble.
- Merges: origin/main at 0.9.165 (bb1a1aa08), 0.9.167 (170372943, HS2) and 64ec45fbe (GB3, no game code);
  only the generated maps conflicted each time (main's taken, then regenerated). `--fast` exit 0 on each.
  On the 0.9.165 merge YoungBankCheck gave the same figures to the digit; on the 0.9.167 merge YoungBankCheck
  and AutoBuildCheck passed alone (§4's line; the young city 1,236 people against the founding's 1,200).
- The known-reds rows that name OE1 (YoungBankCheck's two) are vetoed at OE1's gate (`tools/known-reds.sh`): its
  section 2 has to pass, and passes.

## 8. The ensemble

`docs/ensemble/OE1.md`: 80 of 80 runs on the gate box (label OE1c, classes of d15352526, 0.9.167; the baseline's
`JAVA_OPTS` extra columns, all 86 of its columns; the box's data files, md5-equal to the laptop's), against
`docs/ensemble/0.9.167.md`: **25 of 26** criteria (main 23; H1 retired, not counted), hard 10 of 10 (M1 0 months
and F1 0 runs in every policy), batch verdict PASS. Changed: AUTO2 T2 and ZERO_SLACK T2, FAIL to PASS (the final
population's median 2,584,466 against main's 1,820,652, and 1,657,203 against 1,308,226). Still failing, as on
main: AUTO2 A2, the longest spell of inflation over 10% from m240, 14 months [9 to 35] against 20 [7 to 33].
Two earlier starts are not scored: label OE1 on 0.9.165, stopped at 13 of 80 when HS2 shipped 0.9.167 and moved
the trace (contract 4: the second to ship redoes it), and OE1b on the laptop, stopped at the orchestrator's word
when GB3 brought the box.

**The AUTO2 bank, months under `CAPITAL_RATIO` to m600:**

| | median [min to max] | seeds with any | months in all (spells) |
|---|---|---|---|
| main 0.9.167 (the baseline's own runs, HS2's kept CSVs) | 0.5 [0 to 9] | 8 of 16 | 35 (19) |
| OE1 | 1.5 [0 to 15] | 11 of 16 | 52 (28) |
| DQ1's, on 0.9.128 | 2 [0 to 17] | 11 of 16 | 65 (20) |

The measure is worse than main's. Its spells, read DQ1 §1's way (each against the six months before it: the
bank's write-offs against the trading desk's losses), from the sixteen AUTO2 seeds replayed to m600 with the two
flows as columns (label OE1-bank, `bank_writeoffs` and `bank_trading`; on the laptop, the box holding two jobs;
its capital ratio gives OE1c's months under seed for seed): **47 months in 24 desk spells** and 5 months in 4
write-off spells. Of those four, two have write-offs of any size (seed 2 m491, $407k; seed 13 m361, $1,192k);
the other two have the desk in profit and $6k and $1k written off (seed 1 m223, seed 11 m80). No spell falls in
months 81-89, where DQ1 found six of its eight write-off spells (the young town's Fabrication Shop). OE1's desk
spells cluster in months 177-245 (17 of 24).

Main's 35 months are not classified, because its CSVs hold no bank flows. So what moved from main is told only
in OE1's half: its write-offs, the mend's own field, are 2 spells of any size in 16 seeds; its desk spells are
the rest. DQ1 §8 leaves the desk unqueued (the trading desk's marks on a young city's few listed companies;
`RISK_EQUITY` light). Main's sixteen to m600 with the flows (about 25 minutes on the laptop) would split main's
35 months the same way; that is the orchestrator's call.

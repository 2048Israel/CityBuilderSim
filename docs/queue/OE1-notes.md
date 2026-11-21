

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
- **The staffing hold weighs a first plant staffed** (5fbcc3468, after the first dry run, §9): `Sector.staffed()`
  reads an estimate with `firstPlantRate()`'s staffing whole (the other five ratios as they are), and
  `Game.noteStaffingHold()` takes the hold's margin through it, so a first plant short of a band asks for the wage
  that would staff it.
- **An exact packing leaves nobody** (f4bbb183e, the orchestrator's call, §9): `FamilyModel.packingRounding()`,
  MoneyAudit's rule in households (`RELATIVE_TOLERANCE` of the size, never under `ULP_STEPS` of a double's steps),
  and two snaps under it: a match pass whose need ends under it at the doors' size or the pass's places everyone,
  and `noteUnplaced()`'s families' leftover (the seekers' part taken off the whole) is nobody under it. Every reader
  of `getDoubledUpHouseholds()` and `getStillUnplaced()` reads 0 when the packing is exact.
- **A car drawn is a car paid for** (b16fd8a5d, the orchestrator's call after the ensemble's F1, §11):
  `HouseholdBalance.takeCars(units, price, wantPrice, ceiling)` reads each household's want in whole cars at
  `wantPrice` and bills it at `price`; `Motoring.month` passes the quote's per-car price, the price the draw was
  sized at, so the take's want is the draw's units and every car drawn is placed and paid for at the draw's own
  price. The three-argument form (the second-hand market's, one price for both) reads both at `price`, as before.
  It also moves who gets the cars, not what is paid, in a month a car plant's shelf runs short: the quote blends
  the wanted cars over shelf and world and the draw the affordable ones, so the draw's price is the cheaper by
  real money there (a shelf holding every affordable car but not every wanted one: the shelf's own price). The old
  take, read at that cheaper price, wanted more cars than were drawn and spread them pro rata; the new one gives
  each household the whole cars it was sized for at the quote's price (the review's finding, §11).

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
- ★ **The staffing hold weighs a first plant staffed** (the orchestrator: "stands"). The hold's margin is what a
  raise that staffs the building is worth, and read at the fill of the bands it is short in, a first plant short
  of a band never carried its interest and never asked (WantedPostsCheck, §9). Whole, not the fill of its other
  bands: a band with spare hands fills its posts, so the two differ by little, and whole is main's own read for a
  first plant there. A running sector's hold reads its own rate, as on main.
- ★ **The take reads its want at the draw's price, rather than handing the refused car back** (the orchestrator:
  "pick the one that keeps every identity named"). The draw stays one booking: Markets.draw() has already struck
  the market's record and the draw log, the seller's sale (Automotive's pending sales to households, the line the
  households' in-transit reads), its stock and the world's import, and the month's drawn count. A hand-back would
  reverse each of the five, and each is read by an identity (the households', the pools' DOMESTIC and TRADE scopes,
  `tradeByGood`, the shelf). Read at the price the draw was sized at, the take's want is `affordable` by
  construction (`carsWanted()` is the same sum over the same cells, unchanged between the two), so the scale is 1
  and nothing is left over. The bill stays the draw's own per-car price, so the households pay what the sellers
  booked; where that price is a few bits dearer than the want's, the excess over a household's cash is lent, which
  the bank is told as before.
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

On the final code (f4bbb183e: §9's two mends, on fa889c423's main): lowest 0.1224 at month 54, 0 months under, 0
in resolution, 7,263 people at month 121; six first plants ran three months alone, missing their runs by 0.485 in
all as weighed against 0.829 at their sectors' own rates (the Vehicle Works weighed 0.779 against its sector's
0.902, run 0.609; the Construction Materials Plant 0.708 against 0.884, run 0.733; the Bottling Plant 0.668
against 0.839, run 0.714); all passed.

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
- **FoodProcessingCheck's Meat Works fixture** (FPK1) set the old rate's four ratios to make nameplate. It now
  also fills every post, sets the road load to nil and puts every city leg on the hired teams, then restores all
  three; its nameplate line reads `plannedRate(newUnit(meat))` (§10).
- **WantedPostsCheck's `marginOf()`** and §5's `earns5` read the hold's earnings as the hold now does, through
  `Sector.staffed()` (§2): the margin the harness works out is `Game.noteStaffingHold()`'s.
- **HousingCheck, a new section, "an exact packing leaves nobody doubled up"**, causes the dust by hand: 301.6
  adults and 0.8 out of work, whose households the model sums to 227.00000000000003, matched on the load path
  (`house()` then `noteUnplaced()`, as Game's reload runs it) against 227 six-person doors. Its fixture line holds
  the sum within `packingRounding()` of the doors; its law, nobody doubled up and nobody without a door, failed
  without the two snaps (2.842e-14 doubled up; the snaps commented out, compiled, run, put back) and passes with
  them. The month's path runs the sharing valve between its two passes, which absorbed this dust; the played
  city's was the families' and the seekers' both (4.1e-15 and 1.0e-14 at DenominationCheck's m21).

- **CarCheck, a new section, "a car drawn is a car placed and paid for, on the whole-car edge"**, at the end of the
  harness on the buying city. Every household is at its cushion but one, a 352-household cell, whose money is
  set (a bisection over its spare money, then over the doubles) so that it can buy one car at the price the draw
  is sized at and none at the price it is struck at. The shelf is empty and the world's price is put at that edge
  (0.99000020493000010), so the quote for 3 cars at the shelf's price reads a car a bit cheaper (…000000).
  The fixture line holds it, and a second checks that the month played is the one found: the probe's ceiling,
  one car drawn, the edge's price imported. Its law, the car placed and the households' money moved by the
  import less the bank's loan to the cent, failed on the old call (0 placed of 1 drawn; held +0.000000 against
  -0.990000; Motoring's line put back, compiled, run, restored) and passes with the mend.

## 6. Left

- **OE1-A** (pending): `BuildAdvice.fieldEarnings()` (lines 2199 and 2247) weighs an offer's iron fields at the
  mines' own rate. A field offered to a city with no mine reads the empty sector. R1g and FB13 hold BuildAdvice;
  `plannedRate()` there is a one-word change once they have shipped.
- **AS1** (queued, the orchestrator's): AuctionCheck §11 causes the auction sale it counts instead of reading one
  off LongPlaytest's 1,200 months (§9).

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
- The first dry run (8f1466249) was red in six harnesses (§9). On its two mends (f4bbb183e, the merge of
  fa889c423 under them): `--fast` exit 0; the pick ALL 230 (BusinessInvestment.java, 61 readers), review not
  needed; each alone, exit 0: WantedPostsCheck, DenominationCheck, EquityCheck, PassThroughCheck, AuctionCheck,
  YoungBankCheck, AutoBuildCheck, CopperRefiningCheck and HousingCheck.
- The car mend (b16fd8a5d, §11): `--fast` exit 0 (and again on 02e7138d7, the review's comments); CarCheck alone,
  its one red CI1's (`## known-reds`) and its new section green, and that section red on the old call (§5). Review
  needed (money): REV-OE1-M, SHIP (§11).

## 8. The ensemble

`docs/ensemble/OE1.md`: OE1e, 80 of 80 runs on the gate box (classes of 398057923: OE1 with the car mend, merged
with 0.9.171; the baseline's `JAVA_OPTS` extra columns; the box's data files). Scored against the newest baseline,
`docs/ensemble/0.9.171.md` (the orchestrator's correction; the committed baseline is this scoring): **25 of 26**
criteria (H1 retired, not counted), hard 10 of 10 (M1 0 months and F1 0 runs in every policy), batch verdict
PASS. Changed: AUTO2 A1, inflation from m240, FAIL to PASS (2.91% [2.58% to 3.50%], 13 of 16 in band, against
2.86%, 10 of 16). Against `docs/ensemble/0.9.167.md`, as first ordered (`target/ensemble/OE1e`'s own scoring): 25 of
26, hard 10 of 10, PASS; changed AUTO2 T2 and ZERO_SLACK T2, FAIL to PASS (final population 2,226,328 against
1,820,652, and 1,467,861 against 1,308,226). Still failing on both, as on both baselines: AUTO2 A2, the longest
spell of inflation over 10% from m240, 16 months [10 to 39] against 18 [6 to 33] and 20 [7 to 33].

Against OE1d, its run before the car mend (§11), 72 of the 80 runs are the same to the byte. AUTO2 6 moves from
m341, 12 from m1356 and 3 from m1612, with their ZERO_SLACK twins, and SHOCK 6 and HIGH 6 from m341: the shelf
running short or a household on the whole-car edge, the two cases where the take now differs (§2; which, not traced).
Earlier labels, not the shipped figures: OE1 (0.9.165, stopped at 13 of 80), OE1b (the laptop, stopped),
OE1c (0.9.167, before §9's mends: 25 of 26, PASS), OE1d (0.9.171, before the car mend: 23 of 26, F1 FAIL, §11).

**The AUTO2 bank, months under `CAPITAL_RATIO` to m600:**

| | median [min to max] | seeds with any | months in all (spells) |
|---|---|---|---|
| main 0.9.171 (MC1's runs, copied from the box) | 0 [0 to 8] | 5 of 16 | 20 (11) |
| main 0.9.167 (HS2's kept CSVs) | 0.5 [0 to 9] | 8 of 16 | 35 (19) |
| OE1e | 0.5 [0 to 15] | 8 of 16 | 49 (19) |
| OE1c (§9's mends not yet in) | 1.5 [0 to 15] | 11 of 16 | 52 (28) |
| DQ1's, on 0.9.128 | 2 [0 to 17] | 11 of 16 | 65 (20) |

OE1e's months are OE1d's seed for seed: the car mend first moves AUTO2 at m341 in seed 6, which has no spell. The
measure is worse than main's on both baselines. The split is DQ1 §1's: each spell read against the six months
before it, the bank's write-offs against the trading desk's losses, $100k the least either counts at. The sixteen
AUTO2 seeds were replayed to m600 on the box with the two flows as columns (`-Densemble.months=600`,
`bank_writeoffs` and `bank_trading`): OE1's on 398057923 (label OE1e-bank), main's on b2f27a7bd, 0.9.171 (label
main171-bank, a throwaway worktree, removed). Each replay gives its ensemble's months under, seed for seed.

| spells to m600 | main 0.9.171 | main 0.9.167 | OE1e |
|---|---|---|---|
| desk (the trading desk's losses) | 17 months in 9 | 23 months in 12 | 25 months in 9 |
| write-off (the bank's write-offs) | none | 5 months in 3 | 14 months in 4 |
| neither, under $100k both | 3 months in 2 | 7 months in 4 | 10 months in 6 |

The desk is near main's (9 spells each on 0.9.171, 25 months against 17). **The write-offs are OE1's**: seed 4
m438 (3 months, $2,349k in the six months to it), seed 12 m317 and m319 (1 and 4 months, $920k and $1,051k) and
seed 15 m393 (6 months, $1,399k). Main 0.9.171 has none, and 0.9.167 had three smaller ones ($155k-$386k). The
borrowers behind them are not traced. None falls in DQ1's months 81-89 (the young town's lone Fabrication Shop,
the write-off this batch's mend is for). Seed 10's m83 and m88 spells there are "neither": write-offs $0-1k and
the desk up. Main 0.9.171 has seed 10's m83 spell too, 2 months, the same kind.

## 9. The first dry run (8f1466249, main fa889c423)

Red in six harnesses, 34 NEW lines (the kept run: `Temp/cbs-gate-OE1-eF1zgPLy`). Each ran alone on the mend and
on the old estimate (the probe switch, removed): all passed on the old one, except PassThroughCheck's line main
already carries (FS10). The causes:

- **WantedPostsCheck, 6 lines: OE1's own.** `Game.noteStaffingHold()` reads the hold's margin through
  `estimatedMonthlyProfit()`, so a first Machine Works in a city with no spare college or university hand was
  weighed at the fill of the very bands it was held for, fell under its interest, and asked nothing ("fixture: the
  Machine Works carries its interest at prime, so it asks" and the five after it). Mended: the hold weighs a first
  plant staffed (§2, §3 ★).
- **DenominationCheck, 11 lines: main's knife-edge, reached on OE1's path.** `RealEstate.mayRetire()` refuses while
  `FamilyModel.getDoubledUpHouseholds() > 0`, and the match left a residue there. A probe of the TP1 twins (the
  harness's own twelve-nation world fixture, reformed in July at m19): before m24 the plain twin read 0.0 doubled
  up and the lopped one 7.1e-15, 120 Houses each, 7 months of losses, 305 people against 820 places; the plain
  twin's landlords sold 30 Houses and paid off their loan, the lopped twin's did not, and from there the year's
  trade tables parted by 0.11. Mended at the source, the orchestrator's call (§2), with HousingCheck's new
  section (§5).
- **EquityCheck, 1 line, FS14's harness.** "...and every share of it is held, at home or abroad" is domestic plus
  abroad equal to in issue, which holds while the desk and the city hold none of Retail; its fixture's comment says
  so. On OE1's path they held 0.000153 of Retail's 289.97 shares when Retail first had a book.
- **PassThroughCheck, 3 lines, FS10's harness.** The fixture's twins are a played city; the shock twin's vault ended
  12 months on US$56k over its twin's, against the US$51k it bought (the premise is extra at most bought).
- **AuctionCheck §11, 1 line.** LongPlaytest's city on automatic building booked 0 auction sales in 1,200 months
  on OE1's path, against 9 ($68.8M) on the old estimate; FS15 kept `sales > 0` as a certainty of 1,200 months.
- **CopperRefiningCheck §11, 1 line, FS8's harness.** Seed 1 at m2400 stood 10 Rod Mills (with 4 Copper Smelters,
  3 Electrolytic Refineries, 6 Scrap Copper Refineries and 5 Wire & Cable Works: a home chain main's seed 1 does
  not have) against `ROD_MILLS_STANDING` 1. OE1's read reaches a sector only while it runs nothing, so the mills
  after its first plant are main's planner on a moved path.

The orchestrator's calls: mend the first two; pass Equity to FS14, PassThrough to FS10 and CopperRefining to FS8
by name; AuctionCheck §11 to a new queued row. On the two mends (f4bbb183e), each alone, all six pass, so no
known-reds row is needed: WantedPosts, Denomination, Equity, PassThrough (main's FS10 line too), Auction (§11:
7 sales, $50.1M) and CopperRefining (10.4 minutes; seed 1 at m2400 stands 1 Rod Mill, no smelter or refinery,
9 Scrap Copper Refineries and 2 Wire & Cable Works, and FS8's line, the rule cutting an order, passes too: 53
months). Three of the six read played paths that moved between OE1's two models and back; the ensemble is
due again on the mends (§8). AS1, the orchestrator's row for AuctionCheck §11, is queued in the ship file all
the same: one city's 1,200 months booked 9, 0 and 7 sales on three models.

## 10. The second dry run (97759c438, main f4fb7dc65)

The gate box ran the mends merged with CPB2 (run `cbs-gate-OE1-gPH3XytA`): exit 1, eight lines in three
harnesses. Each reproduces here on the merge and passes in CPB2's kept run of main.

- **FoodProcessingCheck, OE1's own, mended (5dde0bf02).** "fixture: a Meat Works on a farm's floor is an ordinary
  business" read -$207.29 a month; main reads +$265.59. FPK1's fixture fixes every term but the meat price, and
  it set the rate through the sector's four ratios: "runs at nameplate, as a plant that is not built does on
  paper". That is the old estimate, the one OE1 replaces. With nothing running, the estimate now weighs the first
  plant's rate, which takes the lived-in city's fill rates (0.84 of the Works' posts), its roads (0.86) and its
  vans (0.61): 0.44 of nameplate. The fixture now also fills every post, sets the road load to nil and puts every
  city leg on the hired teams, which carry theirs whole. It puts all three back afterwards. Its nameplate line
  reads the rate the estimate weighs (`plannedRate`). The result is 1.00, $275.63 on the herd against -$128.16
  off a ship, and the whole harness passes.
- **CarCheck, 1 line, Motoring's arithmetic.** "a month of car sales is a month of real money" is
  `imported <= paid` taken exactly. Every one of the 11 cars came off a ship. `paid` is takeCars' sum of the
  cells' cars at `cost / units`, and `imported` is the draw's `importCost()`; the two differ by 1.78e-15 on
  13.0788 (13.07876547080566 against 13.078765470805662). Main's city has the same 11 cars and $13k with the
  dust the other way. It is not OE1's code; OE1 only moved the path to it.
- **OutsideCheck, 1 line, its fixture's pool.** "fixture: the city has more workers than posts" needs a pool of
  out-of-work adults; the fixture adds half the adults again, up to four times, two months each. On OE1's path
  each wave's arrivals are gone in the two months (1,901 adults to 1,307, 1,960 to 1,345, and so on). The labour
  force stays at exactly the posts filled (1,417 of 1,768: the posts left are tiers nobody holds), so no pool
  forms. Main's city made its pool by one worker (1,440 of 1,621 filled, 1 out of work), which is the "barely
  enough" the fixture's 0.7.7 comment already met. It is not OE1's code: the path moved (a Fabrication Works of
  828 posts where main has a Fabrication Shop of 405).

The orchestrator's calls: CarCheck passes by name to a new queued row, CI1 (Motoring strikes the import half
from the spend's own sum, so paid and imported are one number by construction). A source mend here was not one
line: `householdCarImports` feeds MoneyAudit's domestic and trade scopes and `tradeByGood`. OutsideCheck passes
by name to FS16, which is in flight and whose sweep owns it; the orchestrator gives FS16 the pool's figures.
Both rows are in the ship file's `## known-reds` (known-reds.sh exit 0).

## 11. The ensemble on 0.9.171 (OE1d) and its one finding

OE1d: 80 of 80 runs on the gate box (classes of cdb8b15b3, OE1 merged with 0.9.171; the baseline's `JAVA_OPTS`
extra columns), against `docs/ensemble/0.9.167.md`. The result is exit 4: criteria 23 of 26, hard 8 of 10, batch
verdict FAIL. T2 goes from FAIL to PASS in AUTO2 and ZERO_SLACK, as on OE1c. F1 goes from PASS to FAIL in the
same two policies, 1 of 16 each with one cause: ZERO_SLACK 6 is AUTO2 6's twin until m1800, and both flag "the
households' money was not conserved" at m655.

**The trace.** A local replay of AUTO2 6 to m656 (a scratch copy of Ensemble.java printing the households' strike)
gives +11.42 that no line names, 0.034% of what moved. The seed's other months are 1e-11. The households'
in-transit at the strike holds it, in one term: Automotive's pending sales to households, 296.84. Motoring.month
(Motoring.java 347-354) draws `affordable` = `carsWanted(quote.cost() / wanted)` = 26 cars, and Automotive books
all 26 as sold to households (Sector.java 2156). `takeCars(26, took.cost() / 26)` then reads each cell's want in
whole cars at the draw's own per-car price, 11.41706, dearer than the quote's, and wants 25.
`scale = Math.min(1, units / total)` (HouseholdBalance.java 3065) places those 25 and charges $285.43. The 26th
car, 296.84 / 26 = 11.41706, is on Automotive's books, in no household's garage, and paid for by nobody: the
residual exactly.

**OE1's part.** The same replay with OE1's packing snap switched off (a scratch FamilyModel whose
`packingRounding()` is -1) agrees on the households' savings to 1e-8 and on every pool line at m655. The car price
is 11.1654 against 11.1772, so 25 are drawn and 25 placed, and the residual is 1e-11. The snap moved the car
market 0.1% on the way to m655 and put one cell on a whole-car edge. The gap is Motoring's: the draw and the take
are struck at two prices with whole cars between them, so the units the market books need not be the units the
households take. It sits on the same lines as CarCheck's red (CI1).

**The mend** (b16fd8a5d, the orchestrator's call: "a car drawn is a car paid for"; §2, §3 ★). The take reads each
household's want at the quote's per-car price, the price `affordable` was read at, so it wants the draw's units
and places them all, billed at the draw's own price. CarCheck's new section causes the edge and fails on the old
call (§5). CarCheck's red (CI1's, `## known-reds`) is unmoved: the mend leaves the paid-vs-imported dust to CI1.
The ensemble reruns on the mend.

**The review** (REV-OE1-M on b16fd8a5d, the orchestrator's reviewer): SHIP, one finding, not P1. Money closes:
the take's total is `affordable` = `took.units()`, the scale 1, the bill `took.cost()` to ulps; the draw's price is
never above the quote's in exact arithmetic, so a loan past a household's line is dust only. The reach is clean (the
three-argument form is `(u, p, p, c)`; `clearUsedCars()` and CarCheck's calls are unchanged), and the new section is
sound. The finding: the WHY in HouseholdBalance and CarCheck called the two prices one price in exact arithmetic,
which is wrong for a draw spanning shelf and world, where the draw's is the cheaper by real money. There the old
take spread the cars pro rata and the new one gives whole cars to the households sized at the quote's price, so
the mend changes who gets the cars in every month a car plant's shelf runs short, not only on the edge (§2). Both
comments corrected (comments only, so OE1e stands on 398057923's classes); the ensemble's shift in car ownership
is expected from it.

**The rerun** (OE1e on 398057923, §8): F1 0 of 16 in every policy, against both baselines. AUTO2 6 and its
ZERO_SLACK twin no longer flag m655.

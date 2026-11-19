# HS1: money follows people (2026-11-19)

Brief: `QUEUE.md`'s HS1 row; the design `docs/studies/spec-households.md` 3.1-3.3, the save 4,
the premises and the new harness 5. Built in one session from main 6b0aa202e (0.9.154); FS1
(docs only) merged before the gate.

## What was built

- **The month's flows carry their money** (`HouseholdBalance`, banner MONEY FOLLOWS PEOPLE).
  At the demographics the month's rates are struck from `PopulationCohorts` (`setFlows`: retiring,
  ageing senior to elder, dying as an adult, a senior and an elder, leaving, and the evicted as a
  share of the off-EI cell), saved, and used up at the next census by `planFlows`. Before the pool,
  `carryTheFlows` moves each flow's households out of the cell they leave with that cell's average
  of every stock (savings, debt, shares by company, dollars abroad, student debt, cars, paper,
  bonds): a retirement into the retired cell of the same shape and former tier; a partner's death
  leaves the survivor everything (a working couple's to the single parent or single adult cell, a
  senior or elder couple's to the alone cell); a death alone is an estate; a leaver's stocks leave
  the city. The census's pool then nets only what no flow explained (`moveStock`, `followThePeople`).
- **Estates** (`passOn`): the debt is settled from the savings (booked through `totalRepaid()` and
  the bank's RepaidByHouseholds), any shortfall written off with the bank's other write-offs, the
  student loan with the graduates' slot; the rest passes in kind to the working households of the
  dead's former tier by households x grown-ups, or to every working cell when the tier has none.
- **Leavers take their money out** through the same lines as the pool's leavers (`lastTakenAway`,
  LeftWith, the paper to the desk, the bonds to the world), so HL1's identity closes unchanged.
- **The retired keep their former tier**: `RetiredHousehold(shape, former)`, 24 cells (four
  retired shapes by six tiers), each its own pool group (`Household.STOCK_GROUPS = ROWS + 6`).
  The census's retired count is split across the tiers by the cells' own households after the
  flows (`retiredMix`). 99 cells in all.
- **OH1**: a cell's pool weight is its grown-ups, or its people when it has none (`poolWeight`): an
  orphan cell's shares reach the pool and the register stays whole. Since the stranger's review
  the people weigh only for what a child can hold (savings, shares, money abroad, paper, bonds):
  an orphan cell never claims a debt, a student loan or a car. ShortSellingCheck's two
  known-reds rows queued to HS1 are green and `ship.sh` retires them.
- The screens: PeopleScreen's grid and mosaic show the retired by former tier ("Senior couple,
  formerly Skilled"); `Game.rowDoors` weighs a retired shape's doors by the tier's share.
- Three no-argument readings for the ensemble's extra columns:
  `getRetiredToWorkingNetWorth()`, `getSeniorsAloneToWorkingNetWorth()`,
  `getElderCouplesToWorkingNetWorth()` (the study's 2.6, savings less debt).
- **The save**: `SAVE_FORMAT` 57 and `OLDEST_LOADABLE_FORMAT` 57. A format-56 save's retired cells
  are keyed by shape alone, so `restoreCells` (by key) would load their money into no cell: read
  wrongly, not incompletely. New in the save: `householdFlows` (the struck rates, 7 slots).

## The calls

| ★ | call | taken, and why |
|---|---|---|
| ★ | the retired's tier | `tier()` stays null (every reader of a working tier is unchanged) and `formerTier()` names it; one pool group per former tier, so the pool's reshuffles also keep old money in its tier |
| ★ | the retired's tier mix | derived each census from the cells' own households after the flows, not saved as 24 figures (the study's 4): the cells are already in the save, so a 24-figure copy could only disagree with them. With no retired yet, the working census's grown-ups; with none of those, the unskilled |
| ★ | the flows' rates | the pyramid's own (`deathShare`, `promotedShare`, `getLeftShare`), applied per cell to its households x grown-ups of the band; struck at the demographics, carried at the next census, saved between |
| | ageing senior to elder | traced as a flow, the same as a retirement, so an elder keeps a senior's money |
| | not traced | students, prisoners, orphans and the owners: their own rules move them; the unemployed retire and inherit with the unskilled |
| ★ | a working couple's survivor | to the single-parent cell when the couple had children, else the single adult; a flatshare's death is an estate (no survivor holds the dead's books) |
| ★ | an estate's debt | settled from the estate's savings first, the rest written off; the study left it open |
| ★ | an estate with no working household anywhere | not made: the stocks stay where they are (the pool's rule), rather than the study's escheat to the treasury, which would be a new money line HL1's identity does not have |
| | the evicted | stored as a share of the off-EI cell's people, so a redenomination or ScaleCheck's scaling needs nothing |
| ★ | OH1 | weigh an orphan cell by its headcount; orphans bring no buffer from the arrivals |
| | the floor | raised to 57 (the old-saves rule: an old save's retired money would load into no cell) |

## The folded rows

- **OH1** (the orphans' shares in a move): mended by `poolWeight`. ShortSellingCheck 7 is green on
  R0b's city; its two known-reds rows (queued to HS1) leave with the merge.
- **OR1** (the children of an adult hired back go with them): already mended on main by OX1
  (`docs/queue/OX1-notes.md`); nothing new. DeathRecordCheck here is exit 3, its one red main's
  known red queued to OX2.
- **BO1-S** (the owner line switched on once the retired have an income): not switched on. The
  cause measured for its being off is the founding's retired having no income (their disposable is
  0 until the pension opens) and the owners banking the dividends; HS1 changes neither (it moves
  money the retired already have, not their income). It stays as its row says, for after the
  pension.

## The premises restated (the study's 5)

- **HouseholdCheck**: the retired are 24 cells; its fixtures build `RetiredHousehold(shape,
  UNSKILLED)` and look the seniors and elders up at the unskilled tier (with no working census
  that is the fallback); the count is ninety-nine cells, `11*6 + 4*6 + 3+1+3+1+1`.
- **OutsideCheck**'s closing fixture, restated: it closed the biggest employer by template, which on
  HS1's moved fixture city is Construction's 12 depots, and the depots' posts are withheld when
  there is no work (`BuildingManager.getTotalJobs` less `getPostsWithheld`), so closing them cost no
  posts; closing the next works alone was undone by a building wave (344 posts). It now skips
  Construction when choosing, and closes every stack of the chosen works and the depots too
  (490 jobs lost). Found doing it, not mended: `BuildingManager.retire()` scraps from the first stack
  of a template only and returns, even when that stack is empty; its callers (Game's shedding
  plant and closing paths) share that. Queued below.
- **SaveFileCheck**: the college town strikes flows and the reload holds them slot by slot, and a
  month later the households' savings, which the flows carried, are the same. Without
  `restoreFlows` these and the played city's parity go red.
- **ReadPathCheck**: green; the flows' save is a plain double array on DataSave's own path.

## HouseholdFlowCheck (new, on RestaurantsCheck's pattern)

Eight sections from the study's 5, each fixture causing its flow: (1) a quiet month moves nothing;
(2) a retirement and an ageing on carry the cell's average into the former tier; (3) a partner's
death leaves the survivor everything; (4) an estate reaches the tier's working households (the debt
repaid from the savings, the shortfall written off, the heirs by grown-ups, every working cell
where the tier has none); (5) a leaver takes the average out (the evicted their own fixture);
(6) every stock conserved, all the flows at once; (7) the orphans weigh by their people; (8) a
real run, 60 months of RestaurantsCheck's fixture city. Green, 126 OK.

**With the old reading put back by hand** (no flows carried, the pool weighing by grown-ups):
66 FAIL, in every section but the quiet month, which is the control.

Section 8's real run (a small city, so thin): retiring 0.57 households carrying 10.36 of savings,
ageing on 0.64 / 3.56, a partner dies 1.04 / 9.28, dying alone 1.08 / 3.17, nobody left; the pool
64.30. The flows moved 29.1% of the savings that changed cells. Estates passed 3.17 to heirs.
Net worth a household against a working one's at month 63: the retired 0.23, seniors alone
0.42, elder couples 0.00 (none yet).

## The checks

| check | result |
|---|---|
| `--fast` (on the merge with main 1041df81b) | green, 0 firm Stale findings |
| HouseholdCheck, HouseholdFlowCheck, OutsideCheck, ReadPathCheck | green |
| ShortSellingCheck | green: OH1's two known reds mended |
| DeathRecordCheck | exit 3: main's known red only (queued to OX2) |
| SaveFileCheck | one red, named in the ship file (below) |
| LongPlaytest, default, with the trace | every month's audit and the money fixtures pass; exit 1 on one finding (below) |
| `tools/pick.sh origin/main HEAD` | ALL, 225 harnesses; review needed (saves: DataSave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT) |

**SaveFileCheck's fund-draw fixture** ("fixture: over the default it owes what it sells for, and
paid some late"): on HS1's moved college town the fund holds 44,441 Retail shares, no bid stands
and it paid nothing late in 12 months, so the fixture's condition never arises. A moved played
city: a known-reds row to FS17, FS1's sweep of the saves (FS1 shipped while HS1 was built). A
per-month world bid in the fixture did not cause it and was taken out.

## The default trace against main's

Main's: `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-6b0aa202/` (clean 6b0aa202e, exit 0, made
for HS1 by the orchestrator). HS1's: `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-HS1-6ddda1cf/`
(6ddda1cff, `-Dcbs.parallel=1 -Dplaytest.trace`, 2,404 months; the log was
`target/checks/LongPlaytest.log`). The commits after it to b338ee4ba are prose, three readings,
a check's printout and the findings' FAIL lines. The stranger's review's mends (62e8221ea) do
change the model, a little: orphans claim no debt, and a death with no working cell at the start of
the month is an estate. The ensemble runs on the final model.

| month | people, main / HS1 | GDP a month, main / HS1 | seniors, main / HS1 | unemployment |
|---|---|---|---|---|
| 60 | 814 / 815 | 134 / 135 | 87 / 87 | 0 / 0 |
| 240 | 1,465 / 1,494 | 157 / 170 | 113 / 124 | .035 / .039 |
| 480 | 5,703 / 8,899 | 2,519 / 3,935 | 428 / 680 | 0 / 0 |
| 720 | 18,692 / 27,722 | 22,301 / 40,794 | 1,469 / 2,173 | 0 / .010 |
| 960 | 25,037 / 32,192 | 29,034 / 75,050 | 1,954 / 2,569 | .321 / .097 |
| 1,200 | 42,588 / 38,550 | 131,392 / 145,876 | 3,326 / 3,052 | .076 / .162 |
| 1,440 | 210,132 / 35,244 | 3.03M / 188,010 | 16,705 / 2,791 | .002 / .103 |
| 1,920 | 1,315,157 / 111,663 | 61.4M / 6.98M | 104,567 / 8,925 | .060 / .094 |
| 2,400 | 2,453,909 / 447,253 | 1.20B / 534M | | |

The trace moves beyond runtime. HS1's city grows faster to month 960. It then stalls for about
700 months at 30,000-39,000 people with 10-16% unemployed, and takes off about 550 months after
main's. What binds and why: the next section. The 80 runs: "The ensemble", below.

**The households' identity (HL1's own lines) over the run:** every month's audit closed, as did
every money fixture. CensusStranded and CensusCreated fired in 0 of 2,404 months (main: 4 and
15): OH1 is mended in the played city too. Unfunded fell from main's 448 ppm of what moved
(428.9M, fired 1,602 months) to 0.02 ppm (3,713, 426 months): the retired now pay their bills from
money that followed them. PublicWagesRestruck rose from 0.16 ppm to 158 ppm (27.1M, 1,204
months); that is the public wages' own line, closed by it, on a city that spent its middle
centuries stalled.

**Its one finding:** "industry stands farther out than the homes", month 2,400 only: industry 4.70
km from the founding site (493 boxes), the homes' band 4.64 km (5,409). This is CL7's class (main's
seeds 1, 3 and 9 reach it, and `docs/notes/known-reds.txt` names it). The suite used to judge
every finding as "LongPlaytest | (no FAIL line)", which CL7's row could not match. Since b338ee4ba
(the orchestrator's word) LongPlaytest prints each finding as a FAIL line with its text, so CL7's
row, `LongPlaytest | industry stands farther out than the homes | CL7`, names it by its exact line.
Judged by `KnownReds.java` on two fixtures, a suite holding only that line exits 3 (known red);
one that also holds `FAIL money was not conserved 3 time(s), months 5-9` exits 4, with that
line NEW on its own. CopperRefiningCheck echoes its child's findings without the token, so they
stay the child's. nightly.sh's red line reads "RED, exit 1: FAIL industry stands...", one word
more.

## The stall, m720-m1700 (the orchestrator's question, 2026-11-19)

The question was what binds from about m960, and whether it is the design or a fault in the
flows. Four runs of the default playtest, each with `-Dplaytest.trace` and a households' probe
(trace-hh.csv; not committed), traces in `C:/Users/Jerus/AppData/Local/Temp/cbs-probe-*`:
- **main**: 6b0aa202e, which reproduces main's trace to the person.
- **HS1**.
- Two HS1 probes in a scratch worktree:
  - **stay**: the leavers' stocks, debt and all, go to the established of their tier like an
    estate, so nothing leaves the city.
  - **flows off**: carryTheFlows returns at once. That is main's pooling on HS1's ninety-nine
    cells, OH1 included.

| month | main | flows off | HS1 | stay |
|---|---|---|---|---|
| 720 | 18,692 | 26,543 | 27,722 | 23,808 |
| 960 | 25,037 | 26,706 | 32,192 | 31,697 |
| 1,200 | 42,588 | 39,290 | 38,550 | 32,040 |
| 1,440 | 210,132 | 98,068 | 35,244 | 37,595 |
| 1,680 | 701,523 | 407,834 | 51,781 | 65,462 |
| 1,920 | 1,315,157 | 1,093,412 | 111,663 | 168,345 |
| 2,400 | 2,453,909 | 2,151,433 | 447,253 | 880,392 |

**What does not bind**, judged in all four runs and against main at the same months. Money below
is real, over each run's own price index.
- **Homes.** Capacity is the people's: 32,490 for 32,192 at m960. From m1320 the landlord holds
  on "housing ahead of jobs".
- **Migration's pull and push.** The affordability and crime pulls sit at 1. People stand at
  2.43-2.45 a job against main's 2.39-2.41.
- **The retired's share of the people.** 7.9-8.0% in all four runs.
- **The bank.** 5,000-13,000 of lending room goes unused through the stall.
- **Demand a head.** Spending a head is 0.032-0.036 in HS1's stall, against 0.034-0.037 in main's
  own stall (m720-1080).
- **The policy rate on its own.** The flows-off city took off through 8-10%.

**What binds: jobs, which wait on the city's own building.** The posts are flat:
- HS1's four tiers stand at 4,415 / 5,120 / 3,160 / 335 from m840 to m1080.
- The stay run's jobs are 12,865, to the job, from m960 to m1200.
- HS1's construction trade (9,120 of capacity) has nothing ordered from m780 to m1740.

BusinessInvestment expands on demand it can see: the landlord watches jobs, the shops their
customers, the makers their market. People follow jobs, so a flat city stays flat until something
from outside moves it. In every run that something is the city building on borrowed money:
- **Main:** city debt goes from 5,650 to 186,000 at m1200-1320.
- **Flows off:** from 1,530 to 47,700 over the same months.
- **HS1:** it stays at 2,700-8,600 until m1680, and the city's borrowing turns at m1764-1827
  (bonds of D$8.3M and D$12.5M).

**Why HS1's city did not build sooner.**
- It went into the m840 bust owing 21,700 at m760, for 32,234 people, against the flows-off
  city's 9,800 for 27,143. Interest took 31% of its taxes, against 13%.
- The policy rate went to 12.3%. Its cash was negative from m780 and the advances rose to 7,500.
- It was in arrears for 227 months, m868-1094, at worst 4,432.
- Then it paid down, from 13.3 months of GDP to 3.8 by m1320, with nothing built.
- The stay run tells the same story: 331 months in arrears (m734-1163, at worst 7,071), and debt
  paid down to 0.3 months of GDP.
- The runs without the flows fell into arrears too, but shorter and shallower: main for 51
  months (m932-982, at worst 1,983), after crashing to 32% unemployed at m960, and flows off for
  126 months (m704-994, at worst 2,188). Both built from m1200.

**Where the flows come in.** The two runs with them share the boom's shape:
- Dearer money: a policy rate of 14-22% at m560-720 against 9.5-14%, and a mortgage quote of
  0.15-0.23 against 0.11-0.155.
- Landlords declining on the rent-cover test, and homes lagging the people (housing pressure
  1.6-1.74 against 1.2-1.4).
- Then a late catch-up at the top, in m680-760: HS1's construction capacity went from 2,320 to
  9,120, and the city's debt from 4,200 to 21,700.

I could not pin why the boom's prices run hotter with the money following people to one line.
Two candidates:
- The retired, who in main could not pay up to 41% of their bills, now pay them (the identity's
  Unfunded fell from 448 ppm to 0.02).
- The working's want over disposable income is lower: 1.05-1.15, against up to 1.6.

**Verdict: not a fault in the flows; nothing mended.**
- The census and the flows read the same step: setFlows runs in advanceDemographics after
  families.rebuild, and the census at the next month's top (startOfMonthUpdate), so nobody is
  moved twice.
- The households' identity closes in every month of every run.
- The leavers' money is not what binds: the stay run stalls the same way.

The stall is the game's own trap. The city's growth waits on the city's borrowing, and the city
borrowed at the top of a boom. In this seed HS1's flows make the trap likelier, through the
boom's prices. Two runs a side cannot tell a systematic effect from the path: there is one
chance in six that the two flows runs are the two late ones by luck.

The ensemble can tell. Against 0.9.154's AUTO2:
- Final population 2,628,909 [1,115,743 to 6,587,878].
- Every seed reaching 40,000 people, by m814 [506 to 1,501].

The ensemble answered, against 0.9.157's own 80 (the model main runs now; "The ensemble", below).
HS1's AUTO2 towns take off at main's pace: 16/16 reach 40,000 people, by m786 against m788, and
the medians to 400,000 and 1M people are within 41 months of main's. Two seeds of sixteen stall
the way this one does (0 and 2), against main's one (14). So the trap is the game's, and this
seed's path is one of its two in HS1; nothing in the flows needs a mend for it.

## The stranger's review (2026-11-19, before the ensemble)

A fresh agent read the diff and the code around it: saves, flows, estates, the identity. It found
no bug in any of the four areas.
- **Saves:** the flows and the 24 retired cells round-trip by key, and the orphan weighting keeps
  no state. A format-56 save is refused (floor 57).
- **Flows:** no cell goes negative, nothing is counted twice, and every division is guarded.
- **Estates:** the settlement, the write-offs and the heirs' shares are right. Written-off debt is
  booked to the bank (`getWrittenOff`) and the dead's student loans to the treasury
  (`getStudentDebtTakenAway`).
- **The identity:** every carried stock is on one line, once, in its month.

It raised three risks and two nits. Mended in 62e8221ea, each with its line in HouseholdFlowCheck
where it is a behaviour:
- **Risk: orphans claimed debt.** `poolWeight` weighed an orphan cell by its people for every
  stock, so a growing orphan cell claimed the pool's debt, student loans and cars, which
  OrphanHousehold's "no credit" says it cannot hold. Now `Stock.notAChildsToClaim()` keeps those
  three with the grown-ups. HouseholdFlowCheck 7 asserts the orphans claim none, and that the
  couples' cars are kept or left, none made: 30 against 30.
- **Risk: a death with no estate.** With no working cell at the start of the month the dead made
  no estate, and the pool handed their debt to the living. Every death is an estate now, and
  passOn sends it out of the city when nobody is left to inherit, as the rule says.
- **Nit:** `getEstatePassed()` counted estates that went out of the city. Now it counts only what
  heirs were handed.
- **Nit:** `restoreFlows` let a saved NaN through. It reads as none waiting.

Left as it is: **the thin retired cells.** A retired cell of a small tier sits under half a
household (`EMPTY_CELL`) for a while, and `foldEmptyCells` spreads what the flows carried into it,
to its own former tier first. That is the fold's rule for every empty cell. It costs the first
retirees of a thin tier their money only until the cell crosses half a household, and the identity
sees every dollar of it.

After the mends and SZ1's merge (4e45a8b08), `./batch-check.sh --fast` is green, and so are, one at
a time, HouseholdFlowCheck, ShortSellingCheck, OutsideCheck and HouseholdCheck. SaveFileCheck is
known red only: its one FAIL line is the FS17 fixture row this batch's ship file names.

## The ensemble (2026-11-19, on ed9a1ad35)

80 of 80 runs on the classes of ed9a1ad35 (main's 0.9.158 merged), scored against
`docs/ensemble/0.9.157.md`. That is RN1-H's 80 on ecfdcfae8, the model main runs now (QN1 kept
its traces byte-identical). The runs carried the three extras. The baseline is committed as
`docs/ensemble/HS1.md`. The seed-paired figures below are read from the two sides' raw CSVs
(`target/hs1-stall/ens_*.py`, not committed).

**Hard: M1 passes in all five policies; F1 fails in one.**
ZERO_HOT has 1/16 runs with "households with no home", against main's 0/16. It is seed 0 in
months 205-207, with 1.3 to 1.6 households unplaced; the watch's line is one. I replayed it to
month 230 with the homes and the land as extra columns (`target/hs1-nohome/`). The replay matches
the ensemble's run, 1,290 and 1,747 people at months 120 and 240.
- **The wall.** Homes stand at 227 from month 80 to 207 while the town grows from 1,101 to 1,424
  people, all of them doubling up (163 to 216 households doubled).
- **No land.** The city's free land is 2,683 sq ft from month 80 to 204, with all it owns
  allocated. Its cash is below zero from month 60 to 160.
- **The escape.** At month 205 the city's owned land rises by 136,000 sq ft. Homes follow at 208
  (290) and 213 (353), and everybody is placed from 208.

That is FB11's class (queued: held towns, why no works pays), as TQ1's seed 0 at month 182 was.
The landlords are held for land while nothing buys ground, and the watch crosses its line in the
months between the ground bought and the homes built. Main's seed 0 never reaches the wall: from
month 80 it sits at 1,045-1,171 people with 34-37% unemployed. TQ1's precedent passes F1 by name
with FB11; that is the orchestrator's call.

**Information.**

| | HS1 | main (0.9.157) |
|---|---|---|
| T1: AUTO2 seeds reaching 40,000 people | 16/16, m786 [495 to 1,729] | 16/16, m788 [402 to 1,500] |
| AUTO2 to 100k / 400k / 1M people, median month | 1,206 / 1,250 / 1,549 | 1,105 / 1,291 / 1,526 |
| ...seeds reaching 400k and 1M | 14/16 | 15/16 |
| T2: AUTO2 final population | 2,012,020 [150,805 to 5,696,867]: FAIL, 65% | 3,101,332 [152,930 to 5,006,382] |
| ZERO_SLACK / ZERO_HOT final population | 1,748,153 / 7,685 | 2,025,150 / 6,728 |
| SHOCK / HIGH final population | 42,194 / 31,386 | 46,532 / 27,684 |

Seed by seed, HS1's final population over main's has a median of 0.79, 0.77, 0.81, 0.89 and 1.10
(AUTO2, ZERO_SLACK, ZERO_HOT, SHOCK, HIGH). HS1's town is the larger in 6, 5, 7, 7 and 10 seeds
of 16. AUTO2's T2 is the paths' spread, not a slower city: the milestones stand at main's months,
and the gap is carried by seeds 0 and 2. Seed 0 holds under 100,000 people from m739 to m2295,
and seed 2 reaches 40,000 only at m1729 (150,805 and 277,768 people at m2400). Main has one such
stall, seed 14 (152,930). The final ratio is below 0.75 in 8 AUTO2 seeds and above 1 in 6.

**Unemployment is higher, and that is the design.** The mean over m720-1800, median over seeds:
- AUTO2 6.9% against 5.3%, SHOCK 7.4% against 5.4%, HIGH 14.2% against 7.6%.
- ZERO_SLACK runs AUTO2's path to m1800. ZERO_HOT's runs end before m720.

In HIGH and SHOCK the gap opens at m480-960, when HS1's towns are the larger. In HIGH they hold
28,900 people against 26,800 at m480-720, and 8.4% are unemployed against 3.2%: more people, not
fewer jobs. Why:
- **The pull.** The senior care pull (`Migration.seniorCarePull`, `SENIOR_CARE_PULL` .30) reads
  the senior care coverage times `Game.careAffordability`: the share of the seniors whose cell
  paid (`carePaid()`).
- **In main.** The pool drained the retired cells. In the default playtest 7-18% of what the
  retired wanted went unfunded from m840 to m1440 (trace-hh, rUnfunded over rWant; 15-40% with
  the flows off). The pull stood at 1.06-1.30 by 120-month window, at the cap only in m480-720
  (1.09-1.23 with the flows off).
- **In HS1.** The retired keep their money and pay, and nothing they want goes unfunded. The pull
  stands at its cap, 1.30, from month 120 in both HS1 runs (HS1 and stay).
- **The effect.** The pull lifts the homes' draw (`Migration.monthlyNet`, WG2), so a town draws
  more people to its homes than its jobs hold.

That is the batch's point, the retired paying from their own money, reaching migration through a
rule that was already there. The ensemble's `served_senior_care` counts beds only (1 on both
sides), so this mechanism is read from the probe pair. The ensemble's unemployment is what it
predicts.

**Debt and arrears.** The ensemble has no tax or arrears column. The city's debt over a month's
GDP stands for debt over taxes. Its months on the central bank's advances, and with negative
cash, stand for arrears. Medians:

| | AUTO2 | SHOCK | HIGH | ZERO_HOT |
|---|---|---|---|---|
| debt's peak to m1440, months of GDP | 15.8 / 19.6 | 15.3 / 17.1 | 14.6 / 17.1 | 13.0 / 16.1 |
| months on advances | 196 / 150.5 | 142.5 / 103.5 | 131 / 103.5 | 153.5 / 157 |
| months with negative cash | 148.5 / 147 | 113 / 91.5 | 113 / 90 | 129 / 129 |

(HS1 / main; ZERO_SLACK is AUTO2's to m1800.) HS1's city owes less and spends more months short
of cash: the default playtest's stall in small, a city paying down rather than building, not one
borrowing more.

**The policy rate**, mean over m560-1440, median: 4.0% against 4.1% (AUTO2), 4.0% against 4.8%
(SHOCK), 12.4% against 12.9% (HIGH). It is not what moves.

**The three extras**, AUTO2 median at m120 / 600 / 1,200 / 2,400. Each is per household, savings
less debt (`Household.netWorth()`), over a working household's:
- **retired:** 0.11 / 0.21 / 0.67 / 0.61. The study's 2.6, main's default playtest on the same
  measure: 0.13 / 0.36 / 0.94 / 2.48.
- **seniors alone:** 0.11 / 0.18 / 0.62 / 0.48. Main's on the same measure: 0.02 at m1,200 (5.1
  against 230.7, the study's 2.6).
- **elder couples:** 0.05 / 0.18 / 0.62 / 0.89. The study gives main's on a wider net worth only:
  0.38 / 0.88 / 2.27 / 12.5.

A senior living alone now holds what a couple does, 0.62 of a working household each at m1,200,
where main's held 2%. The survivor keeps the couple's money, and an estate goes to the
established of the former tier, not to the pool. The toy's rule B (0.86-1.30) is per grown-up,
and these readings are per household.

**Verdict.**
- M1 passes in every policy.
- F1's one finding is FB11's class, for the orchestrator to pass by name.
- The rest is information. The towns take off at main's pace. Unemployment runs 1.6-6.6 points
  higher because the retired pay for their care, and the senior care pull draws people to the
  homes. The final population is within the paths' spread, except AUTO2's T2 at 65%, which two
  stalls of sixteen carry.
- No fault was found in the flows, and nothing was mended.

## The dry run's reds (2026-11-19)

The first dry run (`sh tools/gate.sh HS1 --dry-run` on 26f3f8d31, kept folder
`/tmp/cbs-gate-HS1-T3IhSIS5`) stopped on 37 unnamed lines in 11 harnesses. One is a money
residual, traced to main's car draw and queued as CQ1. The other ten are played-city fixtures that
HS1's moved path no longer causes. Each is in the class of a sweep queued in
`docs/studies/flaky-suite.md` section 6; BusinessServicesCheck, which has no sweep of its own,
joins FS15 (the orchestrator: no new row for a moved fixture). Every line is a known-reds row in
the ship file. The kept suite log, rejudged against them, gives exit 3:
known reds only, 18 harnesses.

**The residual: SlopeCheck 9, CQ1.** The Medellin run's households' identity was out by +14.1176
at m861. I traced it with a probe that replays the run with an instrumented `HouseholdBalance`
printing every stage of the month (`target/hs1-leak/`, not committed).
- **The car.** The +14.1176 is one car, at 14.117593.
- **The draw.** `Motoring.month()` (Motoring.java:343-354) quotes the cars wanted at the asking
  price, then sizes the draw by `carsWanted(perCar)` at the quote's price per car (347). The draw
  (350) takes 50 cars at a higher average.
- **The take.** `HouseholdBalance.takeCars()` (2902) re-counts whole cars at that average. Its
  scale (2922) caps the take at the draw and never lifts it, so the cells take 49.
- **The gap.** The car sold and not taken sits in the households' transit at the month's close.
  The audit strikes it there.
- **Whose bug.** `Motoring.java` and the car methods are main's, untouched by HS1. HS1's path is
  only the first to reach it.
- **The fix, CQ1.** Quote again at the count it would draw, until `carsWanted` at that price
  holds it, then draw that. It moves every car-buying city, so it is its own batch (the
  orchestrator, 2026-11-19).

**The ten fixtures.** Each played city is moved by HS1's flows, so the condition its fixture
reads no longer arises:

| harness | what HS1's path does to it | queued |
|---|---|---|
| ChildcareCheck 2 | the advice plans Large x5 at 2.4404; the harness's weigh() picks Childcare Centre x7, reading a different lack | FS15 |
| EssentialWorksCheck (two fixtures) | on ChildcareCheck's town, their compound conditions no longer hold | FS15 |
| ExchangeRateCheck 8b | FS10's class (the vault empty before suspension) with a moved figure: par 3.3935 at the 1926 return, against main's 2.6128 | FS10 |
| GroceryCheck | a year at 50% hands voucher holders and their twins both 99.9% of their baskets | FS11 |
| FundCheck, the withdrawal dial | the desk's bid sells 0.0000 of both holdings at the step | FS13 |
| HoldersCheck 2 | a month's paper sale moves the coupon's split by 6e-6 | FS14 |
| HoldersCheck 9 | the bank's term paper is gone within 3 months, so the central bank's purchase reads a different sale (the book 320.31 against 320.07), and no middle stands | FS14 |
| HoldersCheck 13 | the bank holds no paid paper with that discount at the cancelled sale | FS14 |
| PriceLevelCheck 9 | the horizon ends at month 94 and the h.* series are compared a month past it (worst 0.89; main's own line is 0.25) | FS2 |
| KeptCheck 40 | the construction page hands back 499 of its first draw's 559 nodes (89%) | FS4 |
| BusinessServicesCheck | its 24 played months on two Contact Centres: the sector's own plan orders a third, 900 seats against 600 | FS15 |

For FS15: BusinessServicesCheck should assert the seats against the centres standing, or read
them before the months play. Nothing in the source changed after the dry run.

## Open, for the orchestrator

1. **The ensemble**: done, above. M1 passes. F1's one finding (ZERO_HOT seed 0, months 205-207)
   is FB11's held land: pass it by name with FB11, as TQ1's? AUTO2's T2 (information) FAILS at
   65%, carried by two stalls of sixteen.
2. **LongPlaytest's finding**: done, b338ee4ba (FAIL lines; CL7's row matches it). **The
   stall**: above, with the ensemble's answer.
3. **SB1**, queued: `BuildingManager.retire()`'s first-stack return.
4. **The dry run's reds**: done, above. CQ1 (the car draw) is queued, as the orchestrator
   agreed. BusinessServicesCheck's played seats join FS15, on the orchestrator's word. The gate
   reuses the kept dry run.

For Jerus: HS1-A (pending), the senior care pull at its cap.

## Commits

bff15aa14 the core; dfd8d4538 HouseholdFlowCheck and the census seam; 48e261d68 OutsideCheck's
closing fixture; 6ddda1cff SaveFileCheck's flows round trip; 8a19d313d the cells' count in the
prose; b6c4c1932 the three readings; c8da97ab3 main merged (FS1); 9fb10bf4d the maps;
b338ee4ba LongPlaytest's FAIL lines; 1d2aaa100 the stall; 62e8221ea the review's mends; 4e45a8b08
main merged (SZ1, 0.9.155); 425c69bc7 the maps; 116e6fc83 the review in the notes; 62972fd34
main merged (HL1-R, 0.9.156); 2c0163238 the maps; acf3a0606 main merged (RN1-H and QN1,
0.9.158); ed9a1ad35 the maps; 4867b8769 the ensemble's baseline and its reading; 26f3f8d31
HS1-A; then the dry run's reds, in these notes and the ship file.

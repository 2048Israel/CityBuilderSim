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
  orphan cell's shares reach the pool and the register stays whole. ShortSellingCheck's two
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
`target/checks/LongPlaytest.log`). The model has not changed since; the commits after it are
prose, three readings and a check's printout.

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
500 months at 35,000-39,000 people with 10-16% unemployed, and takes off about 400 months after
main's. One run of a chaotic city cannot say whether that is HS1's or the path's: the 80-run
ensemble is owed.

**The households' identity (HL1's own lines) over the run:** every month's audit closed, as did
every money fixture. CensusStranded and CensusCreated fired in 0 of 2,404 months (main: 4 and
15): OH1 is mended in the played city too. Unfunded fell from main's 448 ppm of what moved
(428.9M, fired 1,602 months) to 0.02 ppm (3,713, 426 months): the retired now pay their bills from
money that followed them. PublicWagesRestruck rose from 0.16 ppm to 158 ppm (27.1M, 1,204
months); that is the public wages' own line, closed by it, on a city that spent its middle
centuries stalled.

**Its one finding:** "industry stands farther out than the homes", month 2,400 only: industry 4.70
km from the founding site (493 boxes), the homes' band 4.64 km (5,409). This is CL7's class (main's
seeds 1, 3 and 9 reach it, and `docs/notes/known-reds.txt` names it). But the suite judges a
finding as "LongPlaytest | (no FAIL line)", which CL7's row does not match, so at a gate it reads
NEW. Put to the orchestrator: see the end.

## Open, for the orchestrator

1. **The ensemble** is owed (the trace moves beyond runtime): asked as "HS1 ensemble?". Besides
   its own scoring, read the extra columns `retired_nw=getHouseholdBalance.getRetiredToWorkingNetWorth`,
   `alone_nw=getHouseholdBalance.getSeniorsAloneToWorkingNetWorth` and
   `elder_nw=getHouseholdBalance.getElderCouplesToWorkingNetWorth` against the study's 2.6
   (main, savings less debt: 0.13 / 0.36 / 0.94 / 2.48 at months 120 / 600 / 1,200 / 2,400) and
   its toy's rule B (0.86-1.30 a grown-up).
2. **LongPlaytest's finding** in the default run (CL7's class, above) reads "(no FAIL line)" to the
   judge. A known-reds row whose part is "(no FAIL line)" would pass every LongPlaytest finding,
   the money audit's included, so I have not written one. Recommended: LongPlaytest prints each
   finding as a FAIL line, so CL7's existing row matches as written. That is two lines in
   LongPlaytest's findings loop, which is SZ1's file, so it waits for the orchestrator's word. The
   ensemble may also show HS1's late take-off to be this run's path and not the batch's.
3. **SB1**, queued: `BuildingManager.retire()`'s first-stack return.

Nothing is left for Jerus.

## Commits

bff15aa14 the core; dfd8d4538 HouseholdFlowCheck and the census seam; 48e261d68 OutsideCheck's
closing fixture; 6ddda1cff SaveFileCheck's flows round trip; 8a19d313d the cells' count in the
prose; b6c4c1932 the three readings; c8da97ab3 main merged (FS1); 9fb10bf4d the maps.

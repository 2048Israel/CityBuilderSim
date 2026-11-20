# HS2 — two adult stages — notes

Built by a headless Opus session from the orchestrator's brief (docs/studies/spec-households.md 3.4), on
batch/HS2 cut from 7117fef6b (0.9.159), merged with main at 0.9.160 (R1e). ★ marks a design call of the batch.

## What was built

- **The pyramid** (`PopulationCohorts`, new `AdultStage`): the adults are two buckets inside `AgeBand.ADULT`, the
  young 18-34 and the established 35-69. `get(ADULT)` is their sum (`syncAdults()`), so every existing caller reads
  what it read. The young move up at 1/204 a month (`AdultStage.YOUNG.monthlyOutflowRate()`), the established retire
  at 1/420, both at the adult mortality; retirement stays at 70 (the study's ★ call 5). Two compartments in a row: the
  adult years are an Erlang spread, not an exponential one. Every eighteen-year-old is young. Arrivals and leavers
  move in the stages' own proportions. `getPromoted(TEEN)` and `applyBandFlow` mean what they meant (R1g reads them).
- **The families** (`FamilyModel`): each stage forms households out of its own people. The young take 7 shapes
  (`AdultStage.forms()`: SINGLE_ADULT, COUPLE, SINGLE_PARENT, COUPLE_BABY, COUPLE_CHILD, COUPLE_BABY_CHILD,
  SHARED_ADULTS), the established all 11. The families' adults split pro rata to the pyramid's stages; the children by
  the ★ shares `YOUNG_SHARE_OF_BABIES` ½, `YOUNG_SHARE_OF_CHILDREN` ¼, `YOUNG_SHARE_OF_TEENS` 0 (the study: Canada's
  mean age at a birth is 31.9 [S§7], so a baby's parents straddle 35, a child's mostly do not, a teen's never; the
  dial's own sentence: a first birth at 31, two in five mothers under 30 at a birth, [S1]). `households[shape][tier]`
  stays the whole city and `young[][]` is the young part (★ call 1), so every reader of a shape or a tier (the doors,
  the rent, the screens) reads what it read. Only the households' books ask for a stage.
- **The children one stage cannot place** (★ call 7, found by DeathRecordCheck): the young build first and the
  children their builder leaves are offered to the established's before the orphan section; next month the
  established's kept households draw on the young's spare children (their own first, the young's next, their own
  children coming home last). `FamilyModel.offerChildren()`, `keepDrawing()`.
- **The households' books** (`HouseholdBalance`, `WorkingHousehold`): 42 young working cells after the established's
  66 (141 cells in all), keyed "COUPLE:SKILLED:YOUNG" and the like. **The pool's group is (tier, stage)**: the young's
  stock groups are `WorkingHousehold.youngGroup(tier)`, six of their own (`STOCK_GROUPS` = rows + 12). The census is
  by stage (`HouseholdBalance.Census`; `FamilyModel` implements it). **Ageing carries the money as HS1's flows do**: a
  new flow, MOVING_UP, takes the young who turn 35 whole into the established cell of their shape and tier with every
  stock; the established retire at their own share (FLOW_RETIRE_ESTABLISHED), the young retire none; both die at the
  adults'. Estates go to the heirs' stage first (★ call 5). HL1's identity closes every month.
- **The out of work** (`Unemployment`) retire at the pyramid's retiring share over all its adults, not the flat 1/624.
- **The People screen**: the adult band's tooltip gives the two stages; a cell's ledger says "established, 35-69"
  where the young hold some of the cell, with how many young households keep books of their own.
- **The save**: SAVE_FORMAT 58, and the floor rises to 58 (`GameVersion`'s entry): a 57 save would load wrongly, all
  its working households established and no retiring share waiting, so nobody would retire out of a working household
  the first month back and the young cells would fill from the pool's strangers' money. No conversion. New in the
  save: the cohorts' two stages after their seven scalars (the band's adults their sum, ★ call 12), the young cells
  by their own keys, and the households' flows with the stages' two shares.
- **A latent defect of main's, found on the way** (★ call 9): `Offending.steal` took the theft first and credited it
  to the offenders' cells after; `creditByWeight` pays nothing when every weighted cell is empty, so in a city whose
  only live cells were the retired the month's proceeds went to nobody and HL1's identity lost them (about −5e-4 a
  month, relative 5e-5). HS2's young cells, split off the working ones, made a small city's working cells empty all at
  once. The theft now asks `HouseholdBalance.creditableWeight()` first and steals nothing when it is zero (CrimeCheck's
  new lines). The tiny probe city that showed the breach closes every month since.
- **HouseholdStageCheck** (new, after HouseholdFlowCheck in AllChecks): 1 residence of 204 and 420 months; 2 ageing
  carries the money (a fixture of the books, and 24 months of the probes' default city, HL1 closing each one); 3 no
  teen shape among the young, and the offer; 4 the pool's group is (tier, stage); 5 the 108 working cells.

## The month's cost at 66 and 108 cells

The probes' default city (`LongPlaytest.probeCity`, `probePlay`), 24 months timed after 6 warm at each mark, on the
PC with other sessions running (a month's figure moves by a third between runs of the same build: main's m600 was 41.7
ms in one run and 29.5 in the next). Main's tree is 24b7bfcbc (66 working cells, 99 in all); HS2's is the final model
(108 working, 141). "Books" is `HouseholdBalance.advanceMonth`, "families" `FamilyModel.rebuild`, both by `nanoTime`
taps in a copy of Game.java compiled beside the tree (target/timing, never committed). The same run, one after the other:

| | main m600 | HS2 m600 | main m2400 | HS2 m2400 |
|---|---|---|---|---|
| people on the probe's path | 13,378 | 20,490 | 262,242 | 638,601 |
| live working cells | 41 of 66 | 67 of 108 | 53 of 66 | 83 of 108 |
| a month, mean / median | 29.5 / 25.3 ms | 71.1 / 57.0 ms | 51.9 / 47.9 ms | 101.0 / 92.0 ms |
| the books | 8.0 ms | 23.4 ms | 12.7 ms | 36.0 ms |
| the families | 0.02 ms | 0.07 ms | 0.04 ms | 0.07 ms |
| cells folded a month | 24.4 | 30.4 | 14.2 | 23.1 |

The month roughly doubles. Two causes, and they cannot be separated on one path:

- **The city on this path is bigger** (1.5 times the people at m600, 2.4 times at m2400), so it has more businesses and
  more bonds. The probe's city is chaotic: main's own reaches 13,378 or 23,413 at m600 depending only on how
  `probePlay` is chunked, so one path's size says nothing. The ensemble is what reads the population (The ensemble).
- **Main's two loops over every cell for every bond.** Flight Recorder on both builds puts the gap in two places:
  - **The books' fold** (`foldEmptyCells`). It spreads every stock of a cell that empties, bond by bond, over every
    cell of its group. HS2's young cells are smaller and empty more often.
  - **The bond market's month** (`BondMarket.takeMonth`): 432 samples on main against 2,775.
    - `OrderBook.rest` inserts each household cell's order by a linear scan (58 samples against 663).
    - The settle recounts a cell's bonds at every fill (83 against 792).
- **One mend** (★ call 11): `spread()` reads each cell's holding once instead of up to four times. Its figures are the
  same to the bit (the probe's people the same to the person at both marks; HouseholdCheck, HouseholdFlowCheck and
  DenominationCheck green). The books went 30.3 -> 23.1 ms at m600 and 43.8 -> 37.7 ms at m2400.
- **Left alone:** the order book's insertion and the settle's recount are main's market code, which other batches
  share. They are the next place to look if the month's cost matters (recommended: a batch of its own, measured on a
  saved city at both cell counts), queued as OBK1 in the ship file.

## The checks

All with `-Dcbs.timing=soft`, one JVM at a time. The fixtures are GameFiles.scratch cities, never a save slot.

- **The named harnesses, 24** (target/HS2-named2.txt, on 7d25eddb9): the families, the books, the pyramid, the saves,
  crime, health, schooling, housing, the owners and the interface (OwnerCheck, StaffingDrawCheck, ArrivalMixCheck,
  CrimeCheck, DeathRecordCheck, SicknessCheck, OutsideCheck, HousingCheck, HousingDoorCheck, KeptCheck, ScaleCheck,
  HealthCheck, EducationCheck, PopulationCheck, HoldersCheck, ChildcareCheck, CausesOfDeathCheck, HouseholdStageCheck,
  HouseholdMemoryCheck, HouseholdCheck, HouseholdFlowCheck, SaveFileCheck, ReadPathCheck, DenominationCheck). Results:
  - **17 passed.**
  - **Main's known reds only:** ChildcareCheck, PopulationCheck, EducationCheck (EDU1) and DeathRecordCheck (OX2).
  - **CausesOfDeathCheck:** 9 reloads played on were no longer the unbroken city to the bit. HS2's own: mended (★ call
    12, 02051b49c), and green since.
  - **HoldersCheck and KeptCheck:** a played city moved under HS2's households, so a fixture's premise stopped being
    caused. They are named in the ship file under the sweeps that own their classes:
    - HoldersCheck section 10's fixture "every till could pay its share, so nothing was cut", FS14. Real Estate's till
      is -0.0057 while it owes nothing; the settle's own lines pass. Main's three section-2 lines (HS1's 6e-6 split)
      pass on HS2's city.
    - KeptCheck section 69's node count (39 of 378 in one month, KBP1's class) and section 71's Healthcare page
      (1454x684 kept against 1446x700 new, a scroll bar, KRT1's class), FS5. HS2 touches no interface code but the
      People screen's two lines.
- **Since, one at a time, all exit 0:** HouseholdStageCheck, CausesOfDeathCheck, SaveFileCheck, ReadPathCheck,
  HouseholdMemoryCheck (after the save's mend); HouseholdCheck, HouseholdFlowCheck, DenominationCheck (after the fold's).
- `./batch-check.sh --fast` on 88ca7ce05: compile OK, the maps regenerated (472339e67), Stale 0 firm findings, the tree
  clean but the notes and the ship file.
- **`./batch-check.sh --timing`** (the timing harnesses alone and hard, target/HS2-timing.txt, on 472339e67): exit 4
  with 7 harnesses red on 49 lines. MapCheck has 11, EarthTerrainCheck 6, GlobeCheck 2, PlanCheck 3, SaveSlotCheck 3,
  WorldMarketCheck 3 and KeptCheck's kept months.
  - **The machine was loaded** (other builders running). WorldMarketCheck's warm January, a solver HS2 does not
    touch, took 68.0 ms against main's 17.2 at the 11-18 nightly. KeptCheck's kept month took 19.4 ms against 4.5.
  - **The one that could have been HS2's month**, SaveSlotCheck's due month (102.1 ms against CLOCK_MS 25; the nightly
    had main at 31.5, already over), was run on both builds' classes back to back under the same load, twice each.
    Main's was 131.4 and 124.9 ms, HS2's 125.5 and 111.6 ms. The months between them were 55.0 and 52.6 ms on main,
    42.4 and 42.7 on HS2. Not HS2's.
  - Speed is judged nightly on main (the contract).
- **The review (REV-HS2, on 00d7d1fce): SHIP**, with no P1 and no findings in money, saves, demography or harnesses.
  Its three small items, mended in 358f3dd1e (--fast):
  - The People screen's tooltip computed the settled young share in ui/; it reads
    `PopulationCohorts.settledYoungShareOfAdults()` now.
  - The ledger's `youngHomes >= .5` restated HouseholdBalance.EMPTY_CELL; it asks `!youngCell.isEmpty()`.
  - HouseholdStageCheck's reload-parity line covered the cohorts alone. A section-1 line now saves and restores a
    played FamilyModel: both stages' households and formed record, every shape and tier, equal to the bit, and the
    restored model saves the same array again.
- **The merge of 0.9.161 (OP1, b0c496015):** the conflicts were the generated docs alone, taken from main and
  regenerated by --fast. SAVE_FORMAT 58 and the floor 58 stand. Run singly afterwards, all exit 0:
  HouseholdStageCheck, HouseholdCheck, HouseholdFlowCheck, HouseholdMemoryCheck, SaveFileCheck, ReadPathCheck,
  CausesOfDeathCheck and DenominationCheck.

## The default trace against main's

Main's, fresh: origin/main 24b7bfcbc (0.9.160, the classes of a detached worktree there), `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-24b7bfcbc/`. HS2's:
e131ea6cd, the final model, `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-HS2-e131ea6cd/`. Both are LongPlaytest with
`-Dplaytest.trace`, timing soft. Main's exits 1 on its one known finding ("an old works moved", m2400, RN1-H-M's
row). HS2's passes every check, "All checks passed".

| month | people, main / HS2 | GDP a month, main / HS2 | adults | seniors | households | unemployment |
|---|---|---|---|---|---|---|
| 60 | 909 / 954 | 122 / 114 | 503 / 532 | 97 / 100 | 327 / 348 | 0.000 / 0.002 |
| 240 | 1,298 / 3,673 | 240 / 476 | 618 / 1,771 | 107 / 285 | 406 / 1,079 | 0.015 / 0.001 |
| 480 | 7,713 / 8,641 | 5,581 / 7,942 | 3,626 / 4,253 | 591 / 540 | 2,331 / 2,502 | 0.000 / 0.000 |
| 720 | 11,838 / 32,068 | 33,426 / 64,098 | 5,519 / 15,801 | 932 / 2,008 | 3,654 / 8,753 | 0.000 / 0.109 |
| 960 | 29,753 / 30,946 | 62,185 / 105,223 | 13,859 / 15,177 | 2,340 / 1,960 | 7,757 / 6,425 | 0.240 / 0.431 |
| 1,200 | 40,217 / 27,372 | 193,757 / 206,759 | 18,598 / 13,388 | 3,183 / 1,746 | 11,462 / 7,656 | 0.115 / 0.104 |
| 1,440 | 60,451 / 38,309 | 1.14M / 519,248 | 27,878 / 18,737 | 4,828 / 2,438 | 18,064 / 10,862 | 0.060 / 0.094 |
| 1,920 | 747,080 / 505,005 | 49.73M / 53.64M | 343,556 / 246,558 | 59,614 / 32,459 | 218,829 / 143,616 | 0.067 / 0.070 |
| 2,400 | 1,658,853 / 1,392,242 | 715.60M / 926.97M | 765,052 / 679,834 | 132,566 / 89,537 | 474,474 / 383,154 | 0.111 / 0.130 |

**The trace moves beyond runtime.** HS2's city leads to m720, stalls at 27,000-38,000 people from m720 to m1440 (main
does not stall), takes off with main, and ends at 0.84 of main's people with 1.30 of its GDP. One path says little
about size: the probes' own city is 13,378 or 23,413 at m600 on main depending on how the play is chunked. The
ensemble reads size.

**What the stages change, and why: fewer seniors per adult.** HS2's seniors per adult hold at 0.13 from m480 to the
end (0.126 to 0.132), against main's 0.17 (0.163 to 0.175), through growth and stall alike.
- **The settled shares are not the cause.** The settled city's shares barely move: seniors 0.1181 against 0.1200 of
  the city, adults 0.6051 against 0.5994 (`equilibriumShare`, both trees).
- **The cause is the played city's youth.** Its teens are 0.205 of its adults (the settled share is 0.106), so
  eighteen-year-olds enter the adults at about twice the settled rate. Every one of them is young for 17 years
  before they can retire. Under main's one bucket an adult retires at a flat 1/624 from the month they turn 18.
- **The sum.** A city with a young intake twice the settled one, losing 2.5 per thousand a month on net in m960-m1200
  (main's 0.56), is mostly young adults, and the young do not retire.
- **On the probes' city** the retiring share over all adults is 0.89 of 1/624 at m244 and m605 (young 0.40 of the
  adults against the settled 0.344), 0.97 at m1204 and 0.99 at m2405 (young 0.33).

That is the Erlang note's consequence: the two stages in a row are an Erlang spread of the adult years. A young city
retires fewer than an exponential would make it, and a settled one the same. The ensemble carries both readings
(`getCohorts.getYoungShareOfAdults`, `getCohorts.getRetiringShareOfAdults`) over its 80 runs.

**The households' identity (HL1's lines) over the run.** Every month's audit closed, as did every money fixture.
- **CensusStranded and CensusCreated** fired in 0 months of either run.
- **Unfunded** fell from main's 7.0M (16.4 ppm of what moved, fired in 427 months) to 6,546 (0.012 ppm, 828 months).
- **PublicWagesRestruck** is 0.63 ppm against 0.44 (fired in 978 months in each).
- **BondFaceOrphaned** is 0 in both.

## The ensemble

(Waiting for the go: "HS2 ensemble?")

## The calls

★ marks a design call. The brief's own: the buckets 18-34 and 35-69, retirement at 70, the rates 1/204 and 1/420, the
7 and 11 shapes, the shares ½, ¼, 0, the pool's group (tier, stage), SAVE_FORMAT + 1.

1. ★ **The whole matrix stays the city's** (`households[][]` both stages, `young[][]` the young part), so no reader of
   a shape or a tier changed. Recommended: keep.
2. ★ **Competing risks off the opening balance, the established first**: a month's deaths and promotions are struck on
   the stage's opening count, and the young who move up land in a stage already done, so nobody moves twice.
3. ★ **Every eighteen-year-old is young; arrivals and leavers in the stages' proportions.** `ArrivalMix` is R1g's
   file, so the arrivals' age mix by stage waits for a batch that reads it. Recommended: queue it after R1g ships.
4. ★ **Moving up is a flow, not the pool**: the cell's average of every stock, as a retiree carries hers. The pool
   would hand a young couple's money to whichever young cell grew.
5. ★ **The heirs' stage**: the study names a retiree's heirs (the established) and an established adult's (the
   young); a young adult's go to the established, their parents. The out of work's to either.
6. ★ **The out of work retire at the pyramid's share** over all its adults (they are of either stage).
7. ★ **The children the young cannot place go to the established before the orphans** (`offerChildren`), and the
   established keep the households they formed with them (`keepDrawing`). Without it DeathRecordCheck's mill town had
   7.79 orphans before its closing against main's 2.61, children with adults in the city to take them: two greedy
   passes leave two remainders, and the young's seven shapes leave the larger. With it, 2.19; a city with no young
   adults orphans exactly what main's one builder does on the same pyramid (175.00 babies, 14.06 children, bare). The
   bare 36-month model of DeathRecordCheck's section 4 settles more slowly from its cold start (54 orphans at m36
   against main's 6.6, nearly all teens, whom only the established's re-formers take); its lines pass, and main's
   known red there (OX2) passes too. Recommended: keep; the reverse offer (the established's leftover babies to the
   young) is not built, as the established form every shape the one builder did.
8. ★ **The floor rises to 58** (the old-saves rule; the reason is GameVersion's).
9. ★ **The theft's guard**: a main defect HS2 exposed; Offending is in no other batch's list.
10. **The People screen's lines** are the least the stages need on show; a stage's own panel is a later batch's.
11. ★ **The fold reads each cell's holding once** (`HouseholdBalance.spread`), the same figures to the bit; the bond
    book's insertion and the settle's recount, main's market code, are left (The month's cost).
12. ★ **The save carries both stages** (the young, then the established, after the cohorts' seven scalars; the band's
    adults their sum). The first build saved the young alone and struck the established at load as the adults less
    them, a bit off: CausesOfDeathCheck's nine reloads played on stopped being the unbroken city to the bit (cohorts'
    BABY 148.70473419007664 against ...766 at m3). HouseholdStageCheck section 1 now asserts the round trip to the bit
    on stages whose subtraction loses the bit (1,000.1 and 2,000.2), and fails with the subtraction put back.

## Mutation tests

- One adult bucket put back (PopulationCohorts' ADULT branch the generic band's, the teens into `band[ADULT]`):
  HouseholdStageCheck 16 FAIL, in sections 1 and 2.
- The young's stock group put back to the tier's (`WorkingHousehold.stockGroup()` = `row()`): 6 FAIL, in sections 4
  and 5.
- The established struck at load as the adults less the young (the first build's save): HouseholdStageCheck's
  round-trip line, 1 FAIL.
- The offer removed: HouseholdStageCheck's two "not orphans" lines (457.81 orphan babies against the young's 400,
  238.87 children against 225) and HouseholdMemoryCheck's identity and its fixture line.
- The young's formed record dropped at load (FamilyModel.restore reads the young's households and skips
  formedYoung): HouseholdStageCheck's families round-trip line, 1 FAIL.

## The dry run

(After the mends' go.)

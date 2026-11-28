# ASH4 notes - four checks from Ashbourne

Jerus's Ashbourne findings 5, 12, 11 and 16 (`docs/queue/ASH-findings.md`; brief `docs/queue/ASH4.md`). Each cause was
found first, then mended or recorded as real; `AshbourneCheck` causes each condition on a fixture of its own.

His save was read only through copies on scratch: `slot-02.json` and `slot-02-history.json` copied to `target/ash/`,
read with `tools.SaveDump` and two throwaway readers there (the save is format 66; nothing loaded it). His saves folder
was never written.

## 1. What was found

### Finding 5, the households' deposits into one bond: a bug, mended

- **The cause is the cushion's own figure.** Every "savings past the cushion" path (the city's paper at the auction and
  its settle, a business's bonds, shares, money abroad, an offering, a car paid in cash, the meals and the luxuries, and
  the shares sold to refill it) read `SHARE_CUSHION_MONTHS * max(0, c.disposable)`. `disposable` is wages, a pension
  or a benefit, shared out by `earningWeight` (`HouseholdBalance.splitIncome`). The owner families' weight is 0
  (`OwnerHousehold.earningWeight()`), and dividends never enter `disposable`, so their cushion was 0 and their whole
  balance counted as spare.
- **The settle and the other paths were not the cause.** `Game.householdsTakeTheirShare` takes at most the spare read
  fresh, `HouseholdBalance.buyAtIssue` splits it over the cells by each cell's own spare, and the other paths each read
  the live `c.savings` after the settle. None double-counts an excess.
- **In his save** (month 241, the copy): the owner cell, 6 households, holds Cr 7.23M of the households' Cr 10.29M of
  deposits (70%), Cr 1,204k each, with a take-home of 0 and a planned spend of Cr 75.1k a month. The take-home
  cushions of every cell come to Cr 0.78M (7.6% of deposits): about the Cr 0.94M Ashbourne kept at month 196.
- **The mend** (★1): `HouseholdBalance.cushion(c)` is `SHARE_CUSHION_MONTHS` of the larger of take-home and everyday
  spending (`c.everyday`, the plan's basket and what income adds to it, without the wealth term), and all nine paths
  read it (seven in `HouseholdBalance`, two in `Exchange`). A saver's cushion is what it was. The first mend read the
  whole plan (`c.planned`), and on the save's cells its cushions came to Cr 2.20M (21% of deposits) instead of
  Cr 0.78M. A format-66 save holds no everyday figure, so the mended total on the save is not measured. On the
  fixture town the owners' cushion is three months of 29.43 a month, against 39.23 for the whole plan.
- **What is left, for Jerus (★2):** the owners still put about 90% of their balance into one big issue. The cushion
  is now 3 months of their spending, and D32's rule (Jerus, 2026-10-20: a fifth of an issue per point of spread over
  the deposit rate, up to the issue, out of the spare) takes all the rest when the issue is large against the spare.
  On the fixture town the owners held 88.1% of deposits and paid 97.1% of the households' part. A share-of-wealth
  limit on one issue would be a change to D32, and so his call. It is not made here.

### Finding 5's mend and the city's growth: a defect of the first mend, mended (REV-ASH4's second look)

- **What the second look found.** The first ensemble passed, but growth fell. AUTO2's median population at month
  720 went from 93,798 to 50,364 over its 16 seeds, and HIGH's final population fell too.
- **The noise first.** Three null draws are main (`39fcbee71`) with `SHARE_CUSHION_MONTHS` moved by 1e-9
  (3.000000001, 3.000000002, 2.999999999). Each ran AUTO2's 16 seeds to month 720, paired seed by seed with ASH1's
  baseline runs. The scripts are in `target/ash/growth/` (`paired.py`, `geo.py`), not in the tree. A butterfly
  moves this city's growth a long way: the medians at month 720 are 73,330, 97,555 and 84,934 against 93,798, and
  single seeds range from 0.23 to 2.96 times main.
- **The first mend was outside all three nulls.**
  - The climb from 3,000 to 10,000 people took a median 21 months longer; the nulls moved it by +4.5, -10.5 and +6.5.
  - The per-seed geometric mean of population was x0.93, 0.81, 0.78 and 0.82 at months 360, 480, 600 and 720.
    At the first three it was under every null; the nulls' lowest were 1.04, 0.95 and 0.92.
- **The cause: the cushion read the plan's wealth term.**
  - `planned` is `min(want, spendable)`, and `want` carries `spendFactor * WEALTH_SPENT_A_MONTH * net worth`. The
    code's own comment in `Household.plan` calls this the grocer's want the shops refuse: "AND THE FORTUNE ALSO ASKS
    FOR WATCHES", a fortune asking its grocer for months of food nobody eats.
  - Three months of that is a cushion that grows with the fortune. On the fixture town the owners' whole plan is
    39.23 a month against 29.43 of everyday spending, so the first mend's cushion was a third larger than three months
    of what they spend, and every unit of wealth they gained raised it further.
  - It held back, from shares, the city's paper and money abroad, savings the real law counts as spare. This is the
    first kind of defect the review named: a cushion figure that is not spending.
  - The mend is ★1's `everyday`. AshbourneCheck holds the real law on the production `plan()`.
- **One seed, month by month** (seed 14, main against the first mend, the first helper's probe in
  `target/ash/growth/out/`). The first mend led until month 180, then main took off and it stalled: 2,160 homes
  against 4,334 at month 240. Throughout, the cushion was 3-11% of the households' savings. One seed's path is its own
  chaos, and it showed no single channel. The channel by which the larger cushion slowed the climb (the shares, the
  city's paper, or the money abroad that it held back) was not isolated.
- **After the mend** (ASH4b, the ensemble below, the same 16 seeds):
  - The take-off is inside the nulls: to 3,000 people -2.5 months, to 10,000 -7.0, to 40,000 +2.5 (the nulls' ranges
    are -7.5 to +4.0, -10.5 to +6.5 and -11.0 to +11.5).
  - The geometric mean is x1.04, 0.94, 0.84 and 0.89 at months 360, 480, 600 and 720, against the nulls' 1.04-1.21,
    0.95-1.06, 0.92-1.04 and 0.78-1.11. That is inside at 360 and 720, and 1 and 8 points under the nulls' lowest at
    480 and 600. AUTO2's median at month 720 is 70,579.
  - **What is left is not resolved.** Three nulls cannot place an effect of 5-10%. The owners now keep three months
    of their spending where they kept nothing, and by the real law a buffer slows them a little. Whether that is the
    residue at months 480-600 is not established.

### Finding 12, the yen bond bought back at 27.37: real in the model; the rate is the stand-in century's (EP1b)

- **The price is the yen curve's, and the conversion is sound.** `Game.quoteBuybackAtRest` prices the piece at
  `DebtManager.marketValue`, which is the yen curve at `worldRate("JPY") + countryPremium()` (the premium once) pricing the
  piece's own flows. Principal and flows convert at one rate: on the fixture, with the D$ 30% weaker, the principal in
  D$ moves by the cross's ratio and the price per 100 at the same rate does not move (1e-9).
- **The level is the rate's, and the premium's, reconciled to the cent.** In his history `worldMoney.policyJPY` is 13.00%
  at month 205 (January 1917), and the US$'s is 14.50%.
  - **The premium, worked by hand from the save** (a helper, no JVM): owed Cr 992,435, the buyback's own line, which is
    ¥72M at the month's cross (the history's `foreignDebtUsd` 685.07 = ¥72,000 / 105.0991). Trailing exports Cr 1,087.30k a
    month, rebuilt from the history's `exportsAbroad` with ForeignAccounts' trailing mean; the rebuild gives the saved
    figure at month 241 to 0.0005. No default scar (`foreignStanding` [0, -1]). So: .16 x (.6 x solvency .0095078 + .4 x
    service .0106488) = **0.15943%**.
  - **The piece:** the log's "Borrowed ¥72.0M abroad: 30-year term bond" at month 157, 3.5%, so 312 months left at 205,
    retired whole (the series reads 0 at 206).
  - **AshbourneCheck 2 rebuilds it** on a town of its own. The piece is booked by `DebtManager.addLongTermBond` in yen and
    aged 48 of its own months (`processMonth`). The yen is handed at 13%, and the trade is set so the model's own
    `countryPremium()` reads Ashbourne's owing against Ashbourne's exports; it gives 0.15943% to 1e-7.
  - **The result:** the production `Game.quoteBuybackAtRest` quotes **27.3723** per 100, and Ashbourne paid **27.3723**
    (271,652 / 992,435). The tolerance is the record's half-cent of a point: with no premium the price is 27.7352, and a
    basis point more gives 27.3498, both outside it. At the Bank of Japan's 5.11% the same piece is worth 65.74.
- **Reality:**
  - The New York Fed's discount rate was 3.75% from April to November 1917 (FRED, M13009USM156NNBR, NBER Macrohistory).
  - The Bank of Japan's official rate in 1917 was 1.4 sen a day, about 5.11% a year. That figure is from memory. The
    source that holds it, the *Statistical Yearbook of Imperial Japan* 1928, table 99 (Bank of Japan rates from
    16 March 1917), could not be read online.
  - Jerus's 5-6% stands.
- **The cause:** before January 2000, `PolicyRates` runs its Taylor rule on the US CPI (EP1's stand-in century). The
  US CPI rose about 17% in 1917, so the rule took every bank to 13-16%, where the real banks held 3.75-6%.
- **The owner:** EP1b (queued: "the eight banks' real rates") replays the real century and retires the stand-in where
  the file reaches. Nothing is mended here, and no new row.

### Finding 11, the power load fell while the town grew: real

- **The units.** The dispatch's load is the buildings' draw: each building's whole-number kW from `buildings.json`
  (`BuildingsTemplate.powerBought()`; a House 1 kW, a Lime Kiln 690 kW), less any steam share, times the season
  (1.076 in January) and over the 5% losses, cut into blocks (`PowerDispatch.dispatch`). Both months are January, and
  the two loads are the same unit.
- **What fell.** Taking the season out, the underlying draw was 9,354 kW at months 192-193, then 8,664 kW at month 194
  (-690: one Lime Kiln), and 8,022 kW at month 195. It then rose to 8,146 kW by month 205 as homes were added.
  - Cement & Concrete's workers went 119 -> 77 -> 38.9 over months 193-195, one kiln's 45 posts at a time.
  - Two kilns are -1,380 kW, and the homes added +172 kW: the whole -12.9%.
  - The policy rate was 2.9-3.9% when they closed. The 11.7% was month 205's own.
- **The price is a scarcity price.** With no substation the wires carry the founding grid's 10 MW. A half-block over
  them clears at VOLL, and the rest at the free grid's 0; the month's price is the average over the kWh served.
  - Month 193's draw leaves two of the three blocks over the wires, and month 205's one.
  - So the price falls 78.5% while the load falls 12.9%; AshbourneCheck 3 strikes both.
  - The Diesel Station stranded behind the 10 MW wires is findings 1 and 2, ASH1's.
- **Noted, not mended:** a building's draw does not follow its staffing or output; it is a fixed kW per building. In
  Ashbourne the construction workers rose 123 -> 479 at month 194 with no change in load. A design simplification,
  queued as PL1 (`docs/queue/PL1.md`), the orchestrator's call (BP1-C): real load follows output.

### Finding 16, households merge by 8% when a building boom ends: a defect, its mend queued as HF1

- **The cause:**
  - The families are built from the adults who work (`FamilyModel`, "THE PEOPLE OUTSIDE THE FAMILIES", 2026-09-11).
  - An adult laid off this month leaves the family this month (`FamilyModel.rebuild` takes `outOfWork + studying`
    out before any family is built), and the city's share of children goes with him.
  - Last month's households are kept (`keepFormed`) but shrink by the whole shortfall at once.
  - The rule reads this month's unemployment (`PopulationManager.getUnemployed()`), with no pace and nothing smoothed.
    `REFORMING_EACH_MONTH` (1%) covers only households that re-form on their own.
- **What the "households" figure is.** The history's series (`HistorySave`), the People screen and `RealEstate`'s
  household count read `FamilyModel.totalHouseholds()`, the families alone. The laid-off are one-adult households
  outside them, looking for a door, so `householdsSeekingDoors()` rises.
- **On the fixture** (AshbourneCheck 4): 16% of the jobs gone takes the families -7.75% and the households seeking a
  door +4.3% in the one month. Nothing moves after (under 0.1% a month). The jobs back, the families rise +7.72% in
  one month. Ashbourne: -80 households for -189 builders, -176 for -445 (about 0.4 a laid-off adult).
- **Reality:** a lost job does not dissolve a household. After 2007, household formation fell over years, mostly
  through young adults forming fewer households of their own, while older households stayed as they were (Cleveland
  Fed, Economic Commentary 2012-12; FRBSF Economic Letter 2016-05).
- **★3, queued as HF1, not mended here:** the mend changes Jerus's design of 2026-09-11 (the out of work as their own
  household structure, built from working adults so the out of work share no tier's wages), and it moves the people
  side, housing, `RealEstate` and the money side's cells: a batch of its own. Brief: `docs/queue/HF1.md`.
  AshbourneCheck 4 asserts the cause and labels the split "the defect HF1 mends"; HF1 restates it to the real law.

## 2. What changed

- `HouseholdBalance.cushion(Household c)`, new, beside `SHARE_CUSHION_MONTHS`, with its WHY. It replaces
  `SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)` at all nine sites:
  - in `HouseholdBalance`, seven: `wantOf` (shares), `takeCars`, `luxuryBudgetOf`, `mealBudgetOf`, `investAbroad`,
    `spareFor` (the city's paper and the businesses' bonds through `bondSpare`) and `offeringWant`;
  - in `Exchange`, two: `shortOfCushion` and `spareOf`.
- `Household.everyday`, new (REV-ASH4's third look): the plan's everyday spending, struck by `plan()` beside `planned`,
  zeroed by `clearWorking()`, scaled by `scale()`, and nowhere else written. `cushion()` reads it where the first mend
  read `planned`. It is saved as one more cell slot (`CELL_SLOTS_BEFORE_EVERYDAY`, then `CELL_SLOTS`), like
  `planned`, so the month after a load reads the cushion an unbroken run does. An older save is read at its own
  width, without a format bump, and reads 0 until the month plans: for one month the cushion is the take-home alone,
  which is the rule the save was written under.
- `AshbourneCheck`, new, in `AllChecks.HARNESSES`, with four sections, one per finding (below).
- `CarCheck`'s fixtures that set "the cushion and two cars" read `HouseholdBalance.cushion(c)`, so the premise holds
  under the mend. Its three reds are main's known reds (HT1-H) before and after, unchanged.
- `docs/queue/HF1.md`, the brief for finding 16's mend.
- `BusinessServicesCheck`'s register of a cell's slots gains the everyday slot.
- `docs/queue/PH1.md`, the brief for main's phantom cell, which parts DenominationCheck's twins on this path
  (section 4).
- **The held figures (AP5-AP8):** the mend adds no writer of anything they hold (the labour figures, the posts, the
  warrant, the order book's order, the build quote's abroad terms). It adds a read of `c.everyday` to the spare,
  and every spare is computed fresh on each call: `spareForPaper`, `bondSpare`, `Exchange.spareOf` and
  `shortOfCushion`. `everyday`'s only writers are `plan()`, `clearWorking()`, `scale()` and the load.
  The auction's `Bidders` takes one snapshot per quote, with no writer between.

## 3. The calls

- ★1 **The cushion is three months of take-home or of everyday spending, whichever is greater, on every path.** A
  transaction balance pays the months ahead's outgoings (Baumol-Tobin: money held in proportion to spending).
  - Why the greater: it never lowers a saver's cushion. A household that spends less than it takes home keeps the old
    cushion, so the change falls only on those who spend more than they take home: the owner families, the retired
    with no pension, and a family drawing on its wealth.
  - Why every path: one balance, one cushion. A share path that still saw the owners' whole balance as spare would be
    the same defect on another path.
  - **Everyday spending, not the whole plan** (REV-ASH4's third look; finding 5's growth loss, below). The plan's
    `want` and `planned` carry a wealth term, `WEALTH_SPENT_A_MONTH` of net worth, which the code's own comment calls
    the grocer's want the shops refuse ("AND THE FORTUNE ALSO ASKS FOR WATCHES"). A cushion of three months of it grows
    with the fortune. The real law is a buffer stock measured in spending: the Fed's SHED counts emergency savings as
    "three months of expenses" (55% of US adults had them in 2025, *Economic Well-Being of U.S. Households in 2025*),
    and Carroll's buffer-stock household (QJE 1997) holds a target ratio of wealth to its permanent income and spends
    everything past it. So `everyday` is `subsistence + spendFactor * fromIncome`, capped by what the cell can spend,
    struck in `plan()` beside `planned`.
- ★2 **D32's appetite is left as it is**, for Jerus (pending row ASH4-A).
- ★3 **Finding 16 is queued, not mended** (HF1): it moves his 2026-09-11 design and four subsystems.
- **PH1 is queued, not mended:** DenominationCheck's twins part on the mend's path from a fault of main's (section
  4), already passed by name by DK1's rows. A mend here would move the whole fixture's path and the default trace
  again, outside this batch's findings.
- **Finding 12 gets no new row:** EP1b owns the century's rates.
- **Finding 11 is real.** The fixed draw per building is not its cause; it is queued as PL1, the orchestrator's call (BP1-C, 11-28).
- **AshbourneCheck numbers its sections 1-4 in the brief's order** (5, 12, 11, 16), each titled with its finding.
- **The review line** of `tools/pick.sh` says "not needed", but the mend moves what the households pay for the city's
  paper, bonds and shares. I asked the orchestrator for a stranger's review at mends-at, as the brief has it.

## 4. The checks

- **AshbourneCheck: 65 assertions, all OK.**
  - Section 1: the lineup town at month 61 has 5 owner households, savings 1,356.96 each (thousands), take-home 0,
    everyday spending 29.43 (the whole plan 39.23), cushion 88.29. 116 of 141 cells take home at least what they
    spend every month, so their cushions are unchanged. What the mend keeps back, 444.84k of 7,698.91k of deposits
    (5.8%), is within three months of the spending take-home does not cover (446.61k). Re-planned through `plan()` at
    twice their savings, the owners' plan rises 38.79 -> 43.25 and their everyday spending and cushion do not move
    (28.89, 86.68); their spare grows by all of the wealth added; twice the everyday spending is twice the cushion.
    Under the first mend's cushion (`planned`) five of these go red. An ask of 4 times the spare allots the
    households face 6,532k for 6,414k, and the production settle takes all of it. The owners' savings fall
    1,356.96 -> 111.27, above their cushion. No cell pays out of its cushion. The cells' payments equal the settle's
    figure to the cent, and the next month's money audit residual is 0.
  - Section 2: the yen piece's fair per 100 is 98.7023 three ways, to 1e-12. With the D$ 30% weaker, the principal
    moves x1.4286 (the cross) and the price at the same rate does not move. Ashbourne's own piece, rebuilt (section 1
    above, finding 12), is quoted 27.3723 by the production buyback against the 27.3723 it paid: the gap is 0.000016
    of a point, inside an absolute half-cent that no premium (0.3629 off) and a basis point more (0.0224 off) both miss.
    (REV-ASH4's second look: the first tolerance went through `close()`, relative above 1, so it was 0.137 at 27.37 and
    let the basis point through.) Month 205 is before `PolicyRates.TABLE_FIRST_MONTH`
    (1200). (REV-ASH4's first P1: the first version priced synthetic flows with no premium, inside 3 points.)
  - Section 3: two kilns draw 2 x 690 kW. Without the kilns, and with 61 more houses, the draw moves by exactly
    -1,319 kW. Ashbourne's 9,354 and 8,146 kW leave two blocks and one over the 10 MW wires, so the price falls 78.5%
    while the load falls 12.9%.
  - Section 4: the families move -7.75% in the layoff month, under 0.1% a month after, and +7.72% when the jobs return.
    The households seeking a door rise. Nobody is lost: the families' and the outside adults add up to the cohorts',
    to 1e-6.
- **`./batch-check.sh --fast`: green.**
- **The cushion's readers and the cell's width, after the mend, one at a time** (each harness's own `main`, at
  `618d894be` plus BusinessServicesCheck's register): SaveFileCheck, HouseholdCheck, RestaurantsCheck,
  BusinessServicesCheck, HoldersCheck, BondCheck, AuctionCheck, CarCheck, CardCheck, ExchangeCheck, EquityCheck,
  OwnerCheck, FundCheck, MarketCheck, CapitalGainsCheck, ShortSellingCheck, FloatLinkCheck, BasketYearCheck,
  DenominationCheck and LongPlaytest. The logs are in `target/ash/h/`.
  - All green but for known reds. HoldersCheck has 3 and CarCheck 3, all exact lines in `docs/notes/gate.txt`
    (HT1-H). DenominationCheck has 15, every one passed by name by DK1's rows in `docs/notes/known-reds.txt`.
  - BusinessServicesCheck's register of a cell's slots, which counts them by hand, gains the everyday slot (+1). It
    was red at the width before and is green after.
  - LongPlaytest: "All checks passed" over 2,400 months, the money audit included (1,869 s).
- **DenominationCheck on the mend's path: main's fault, traced, queued as PH1.**
  - On main (`39fcbee71`'s classes) its reformed twin stays the same city for a year. Its 8 reds are a decade on,
    population among them, and known-reds.txt's DK1 rows pass them.
  - On the branch the twins part within the year: 7 reds 1e-7 to 6e-6 apart a year on, and about 1% a decade on.
  - A helper's probe stepped both twins month by month over 10,397 figures: every cell's savings, everyday spending,
    plan, cushion, shares and basis, the share desk's fills and prices, and the city's cash, output and bank.
  - **Where they part.** Everything holds within 3.4e-13 through month +5. Then `Unemployment.reconcile`
    (`Unemployment.java:398-399`, `else if (total < pool) offEi += pool - total`, an exact comparison of two equal
    sums) leaves one ULP, 1.137e-13 adults, in one twin's off-EI group.
  - **How it grows.** The census makes a cell of 2^-43 households of it. `followThePeople` clears a cell only at or
    below zero (`HouseholdBalance.java:2276-2277`), so the phantom survives. In month +7, 102.9 households come off EI
    into that cell, and the settle's hunger count (`HouseholdBalance.java:1487-1492`) counts them at the phantom's
    per-household outcome: 11.24 more people hungry in one twin. The work ratio, every sector, Retail's price and
    the treasury follow.
  - **Proof.** Setting that one residue back to 0 keeps every figure within 3e-15 through month +12.
  - **Not the batch's code.** The reform scales `everyday` (`Household.redenominate`, then `scale()`); every cell's
    everyday spending and cushion match the twin's with no gap right after it; and `plan()` strikes `everyday` again
    each month. The cushion changed only the path: 596 adults in the pool at the reform, against main's 379. On main
    the same 2^-43 residues appear at months +2 and +3, in both twins alike.
  - The mend is main's: queued as PH1 (`docs/queue/PH1.md`). It is not this batch's, because it moves the whole
    fixture's path and the default trace.
- **The default trace moves.** The default `LongPlaytest` with `-Dplaytest.trace` ran on main `22b6eb96f` (classes built
  on scratch from `git archive`) and on the first mend (`306552e27`). All eight files differ from the first year: in
  month 6 the households' savings go 22.327 -> 22.337, and in month 10 the city's cash and its tax move. So the
  80-run ensemble is run. The mend (`618d894be`, merged with ASH1, whose own ensemble shipped at 0.9.218) differs
  from both. Its main column would be `39fcbee71`, and that was not traced: the ensemble was run on the mend
  regardless. All three runs end with "All checks passed".

  | trace | main `22b6eb96f` | the first mend | the mend `618d894be` |
  |---|---|---|---|
  | t-bank.csv | 6ede455267023c0721bbc430e5d85c6e | daa9ee239e3f0bd946531935e1e115be | fdcdf1b9f20dca7809d8cba7500d0114 |
  | t-bonds.csv | 31d688038b45d088318307ab47345251 | 9f2e5abd2d2ab8ed7f1e4d961d2ff6e6 | a9807e29fc10ee0eed791bb6ed09e417 |
  | t-borrow.csv | ad17225a89ac4910256628b34f9e707b | f5b6a2d7fdc18f852065ef6763f2501d | 8bc613daeacba7ffb36bb7b313cc09e0 |
  | t-build.csv | 6cff5eaf4b95e1170e9cf4698b1fcaf4 | e69975157218e13b06d5d193e38d2ef4 | e07ca5ef1f2bd12204ba39d1f320d634 |
  | t-house.csv | 66a37acc01890e7c405dff9c6eacebd6 | d630b9d45d638eb08536654c9af7d294 | 4f54435f8d9984023bcf0e3aa5b90782 |
  | t-labour.csv | f000ef90f546b8ed4658e078784bfed0 | 12da256991ec5c2795438d7515e382d3 | 2d1d9c51c7666685f50f978d96ff0251 |
  | t-month.csv | 9de355db849f31706f8b4dc4c93dbdcb | 0901c85a74055af1ec2657c9253c6d95 | f4dfee819ac674b51e81327b0961697e |
  | t-pop.csv | 86d0749981733a5339b1dc2e1a33a91b | d5b52aca8eb6abbba2085141f82025c8 | d31cb019cfacb876c43042add822c11a |

- **On ASH1's main, under the first mend** (merged at `caed78d5e`; ASH1 `39fcbee71` changed the POWER row, the grid
  step and the grid's posts), AshbourneCheck had 57 OK. Finding 11 measures the same: draws 4,257 -> 5,637 -> 4,318 kW, and Ashbourne's two
  draws leave 2 and 1 blocks over the wires, the price -78.5%. Finding 5 measures the same: the owners held 86.7% and
  paid 96.5%. The buyback quotes 27.3723. CBK3 has not shipped; finding 5 is measured again on its main when it does.

- **The ensemble: PASS** (`docs/ensemble/ASH4.md`, label ASH4b). It ran on the gate box at `618d894be`, the
  mend, and yielded to CBK3's dry run partway through: 80 of 80 runs. It was scored here against the newest baseline,
  `docs/ensemble/0.9.218.md` (ASH1's).
  - **Hard criteria, 10 of 10:** M1 is 0 months of failed money audit in every policy, and F1 is 0 of 16 runs with a
    LongPlaytest finding in every policy. 22 of 25 criteria pass.
  - **One changed verdict, info:** HIGH's T2, final population, is 50,097 [28,732 to 155,024] against 74,812
    [22,414 to 168,964], under the 75% line (56,109). The first mend had 39,690. The row was FAIL in 0.9.216's own
    scoring (56,156) and PASS in 0.9.218's, so HIGH's final population swings by a third between neighbouring models.
    HIGH has no null draws here, so its move is not placed. It does not stop a ship.
  - HIGH's R2, which changed under the first mend, is PASS again: 0.72% against 1.46%. AUTO2's A1 and A2 are FAIL in
    the baseline as here.
  - **Final populations elsewhere:** AUTO2 6.82M against 8.18M, ZERO_SLACK 4.82M against 5.26M, ZERO_HOT 7,058
    against 6,191, SHOCK 147,116 against 177,650. All pass T2.
  - **The first mend's ensemble** (`caed78d5e`, label ASH4, 31.9 min) also passed, 10 of 10 hard. Its growth loss is
    the section above. Its baseline file is replaced by this one.

## 5. For Jerus

- **ASH4-A, D32's appetite** (finding 5, what is left). With the cushion mended, an owner family keeps 3 months of
  its spending. One large city issue still takes the rest of its balance: on the fixture, 90% of the owners' deposits,
  because the households bid a fifth of the issue per point of spread, up to their spare. Real portfolios do not put
  most of their liquid wealth into one 30-year issue in a month. Capping the share of a household's wealth in one
  issue would change your D32 (2026-10-20, "no half-of-the-issue cap"). Not done.
- **HF1** changes your design of 2026-09-11, the out of work as their own household structure. Its brief says how.

## 6. For the manual

Not this batch's (no edition). When the next edition comes:

- **The cushion:** "Households keep three months of what they take home, or of what they spend every month if that is more,
  before they buy shares, the city's paper or a business's bonds, send money abroad, or pay cash for a car or a night
  out. The owner families, who live on dividends, keep three months of their everyday spending; what their fortune
  buys besides is not counted, so a richer family keeps the same cushion and invests the rest."
- **The yen bond and the other foreign moneys:** foreign paper is priced on its own money's curve. In the years before
  2000 that curve is the stand-in century's rule, until EP1b.

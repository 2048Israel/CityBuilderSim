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
- **The mend** (★1): `HouseholdBalance.cushion(c)` is `SHARE_CUSHION_MONTHS` of the larger of take-home and the
  month's outgoings. The outgoings are `c.everyday`: the recurring bills first (`c.bills()`), then the plan's basket
  and what income adds to it, without the wealth term. A household that takes home more than it pays out keeps the
  cushion it had.
  - It decides only how savings are held. The five paths that move savings between holdings read it: shares, the
    city's paper and the businesses' bonds, money abroad, an offering, and the shares sold to refill it.
  - What a household spends keeps main's rule, `HouseholdBalance.spendingCushion(c)`, three months of take-home: the
    car want and the cash part of a car's bill, the luxuries and the meals. That was the orchestrator's call on the
    bills' ensemble (2026-11-28, by check reality): a precautionary buffer shifts the portfolio, not the grocery bill.
  - The first mend read the whole plan (`c.planned`). On the save's cells its cushions came to Cr 2.20M (21% of
    deposits) instead of Cr 0.78M.
  - A format-66 save holds no outgoings figure, so the mended total on the save is not measured.
  - On the fixture town the owners pay no bills of their own. Their cushion is three months of 46.89 a month, against
    63.62 for the whole plan (the merged tip, section 4).
- **What is left, for Jerus (★2):** the owners still put about 94% of their balance into one big issue. The cushion
  is now 3 months of their spending, and D32's rule (Jerus, 2026-10-20: a fifth of an issue per point of spread over
  the deposit rate, up to the issue, out of the spare) takes all the rest when the issue is large against the spare.
  On the fixture town the owners held 82.8% of deposits and paid 96.1% of the households' part. A share-of-wealth
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
    of their outgoings where they kept nothing. Whether the residue at months 480-600 is that cushion's effect or
    noise is not established, and its size is not checked against any real measure.
- **After the bills** (ASH4c, REV-ASH4's fourth look, the ensemble in section 4, the same 16 seeds): **the climb is
  slower than all three nulls allow; by month 2,400 the cities stand where ASH4b's did.**
  - The take-off is later: to 3,000 people +11.0 months, to 10,000 +24.0, to 40,000 +36.5 (the nulls' ranges are
    -7.5 to +4.0, -10.5 to +6.5 and -11.0 to +11.5).
  - The geometric mean is x0.97, 0.86, 0.82 and 0.73 at months 360, 480, 600 and 720. That is under the nulls'
    lowest at 480, 600 and 720 (0.95, 0.92, 0.78). AUTO2's median at month 720 is 60,695 (main 93,798).
  - At month 2,400: AUTO2's median population is 6.75M (ASH4b 6.82M, main 8.18M). Unemployment over the last 120
    months is 0.101 (ASH4b 0.102, main 0.105). Two of the 16 cities fall 10% or more from their peak after month
    1,200 (ASH4b two, main none). The nulls ran to month 720 only, so these are not placed.
  - **The likely channel, not isolated.** The cushion gates more than paper. The luxury, meal and car budgets
    (`HouseholdBalance.luxuryBudgetOf`, `mealBudgetOf`, `wantOf`, `takeCars`) are bought only out of savings past
    it, as are shares, the city's paper and money abroad.
    - The bills raise the cushion of every cell that pays out more than it takes home, the rent first. That is
      the poor, whose bills and groceries pass their take-home, and the cells with investment income.
    - So the rent now holds back some of their meals, watches and cars. A real buffer-stock household spends out of
      what it holds past its target (Carroll's buffer-stock saving), so the sign is the real one.
    - Which reader carries the slower climb was not isolated. Its size is not checked against any real measure, and
      nothing here says the model's 24 months are the real world's.
- **After the spending fix** (ASH4d, `e91b8dfe8`: the car, luxury and meal budgets keep main's rule, ★1; the same 16
  seeds): **the climb is back within the nulls' noise.**
  - The take-off: to 3,000 people -4.5 months, to 10,000 +6.5, to 40,000 +20.5 (the nulls' ranges -7.5 to +4.0,
    -10.5 to +6.5, -11.0 to +11.5).
  - The geometric mean is x1.04, 0.93, 0.93 and 0.97 at months 360, 480, 600 and 720 (the nulls' 1.04-1.21,
    0.95-1.06, 0.92-1.04, 0.78-1.11). AUTO2's median at month 720 is 78,481 (main 93,798).
  - Two of the seven measures are outside the three nulls' range: the climb to 40,000 (9 months past the latest)
    and month 480 (2 points under the lowest).
    - Three draws bound a narrow range. Each null is itself outside the other two's range on four or five of the
      seven measures.
    - Against the nulls' mean and spread, no measure of ASH4d is two spreads off: the largest is 1.9 (to 40,000),
      then 1.4 (month 480), and the rest are under 1. ASH4c's were 3.3 (to 40,000), 2.7, 2.6, 2.5 and 2.2; ASH4b's,
      which the third look let stand, reached 2.2 at month 600.
    - The sign leans the same way on most measures (a little later, a little lower). A small drag on the savings
      paths is not ruled out, and its size is not checked against any real measure.
  - At month 2,400: AUTO2's median is 7.89M (main 8.18M, ASH4b 6.82M), and unemployment over the last 120 months is
    0.103 (main 0.105). Four of the 16 cities fall 10% or more from a peak after month 1,200 (main none, ASH4b and
    ASH4c two each). That is not placed: the nulls ran to month 720.

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
  `SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)` at the five sites that decide how savings are held:
  - in `HouseholdBalance`, three: `investAbroad`, `spareFor` (the city's paper and the businesses' bonds through
    `bondSpare`, and so the city's auction bids and the bond market's) and `offeringWant`;
  - in `Exchange`, two: `shortOfCushion` (the shares sold to refill it) and `spareOf` (the shares bought).
- `HouseholdBalance.spendingCushion(Household c)`, new: main's `SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)`,
  unchanged, read by the four sites where money is spent: `wantOf` (the car want, and so the used cars),
  `takeCars` (the cash part of the bill), `luxuryBudgetOf` and `mealBudgetOf`. A helper listed every reader, direct and
  through `spareFor`, `bondSpare`, `spareForPaper`, `shortOfCushion` and `spareOf`, up to its decision. Exchange's own
  `cushion(int)` is a company's and reads neither.
- `Household.everyday`, new: the month's outgoings, struck by `plan()` beside `planned`, zeroed by `clearWorking()`,
  scaled by `scale()`, and written nowhere else. REV-ASH4's third look made it the plan without the wealth term. Its
  fourth look put the bills in first: `Household.bills()`, new, is `max(0, disposable - afterFixed)`, what `settle()`
  or `restrike()` took off the take-home before anything was spent (the rent, the fees, the card's interest and the
  minimum paid on it, the student loan). Both are struck before `plan()` reads them (`HouseholdBalance` 1591 then
  1634; 5516 then 5520). `cushion()` reads it where the first mend
  read `planned`. It is saved as one more cell slot (`CELL_SLOTS_BEFORE_EVERYDAY`, then `CELL_SLOTS`), like
  `planned`, so the month after a load reads the cushion an unbroken run does. An older save is read at its own
  width, without a format bump, and reads 0 until the month plans: for one month the cushion is the take-home alone,
  which is the rule the save was written under.
- `AshbourneCheck`, new, in `AllChecks.HARNESSES`, with four sections, one per finding (below).
- `SaveFileCheck`, two sections of its own (`outgoingsChecks()`, REV-ASH4's fourth look): the outgoings across a save
  with a settlement pending, and a save of this format at the old width (section 4). Its full city's cells carry
  their outgoings and cushion across its save too.
- `AshbourneCheck.town(String)`: SaveFileCheck borrows the town on a scratch folder of its own, and the harness's
  streams are set where they are declared.
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

- ★1 **The cushion is three months of take-home or of the month's outgoings, whichever is greater, wherever savings
  are held; what is spent keeps take-home's.**
  A transaction balance pays the months ahead's outgoings (Baumol-Tobin: money held in proportion to spending).
  - Why the greater: it never lowers a saver's cushion. A household that spends less than it takes home keeps the old
    cushion, so the change falls only on those who spend more than they take home: the owner families, the retired
    with no pension, and a family drawing on its wealth.
  - Why every savings path: one balance, one cushion. A share path that still saw the owners' whole balance as spare
    would be the same defect on another path.
  - **Not what is spent** (the orchestrator's call on the bills' ensemble, 2026-11-28, by check reality). The bills'
    cushion was read by the car, luxury and meal budgets too, so the rent held back meals, watches and cars. The
    city's climb fell outside the nulls, and the default city stalled at 611k against main's 4.27M (section 4). A
    precautionary buffer shifts the portfolio, deposits against paper, not the grocery bill. So those four sites read
    `spendingCushion()`, main's rule to the bit, and AshbourneCheck asserts it.
  - **Everyday spending, not the whole plan** (REV-ASH4's third look; finding 5's growth loss, below). The plan's
    `want` and `planned` carry a wealth term, `WEALTH_SPENT_A_MONTH` of net worth, which the code's own comment calls
    the grocer's want the shops refuse ("AND THE FORTUNE ALSO ASKS FOR WATCHES"). A cushion of three months of it grows
    with the fortune. The real law is a buffer stock measured in spending: the Fed's SHED counts emergency savings as
    "three months of expenses" (55% of US adults had them in 2025, *Economic Well-Being of U.S. Households in 2025*),
    and Carroll's buffer-stock household (QJE 1997) holds a target ratio of wealth to its permanent income and spends
    everything past it.
  - **The bills first** (REV-ASH4's fourth look). The third look's `everyday` was the basket alone. A family with no
    take-home and no investment income (an owner family without dividends, a retiree without a pension) kept three
    months of groceries, so a bond could take next month's rent. Where income paid more than the bills, a higher rent
    lowered the reserve, by `spendFactor * MARGINAL_PROPENSITY` of the raise. SHED's "expenses" are all of them. The usual emergency-fund
    rule, three to six months of essential expenses, counts housing and utilities before food. So `everyday` is
    `bills() + min(subsistence + spendFactor * fromIncome, spendable)`, and a cushion is never less than three months
    of the bills: AshbourneCheck asserts it on every cell.
  - **The one place a higher rent still lowers the outgoings.** Where the income term is positive and uncapped, a
    rent raise of 1 adds 1 in bills and takes `spendFactor * 0.8` from the income term. That is a net fall only above
    a spend factor of 1.25, which needs a real deposit rate below -25% (`SPEND_CEILING` 1.5). There the plan itself
    cuts the rest of its spending by more than the rent rose, and the cushion follows the outgoings while still
    holding three months of the bills. With no take-home and no investment income the income term is zero, and the
    cushion rises by three months of the raise, exactly.
- ★2 **D32's appetite is left as it is**, for Jerus (pending row ASH4-A).
- ★3 **Finding 16 is queued, not mended** (HF1): it moves his 2026-09-11 design and four subsystems.
- **FC3 is queued, not mended:** the bills' ensemble's one F1 run is main's construction fault (section 4). Its mend
  moves every city that hands a site abroad, outside this batch's findings. The orchestrator's call: it passes by
  name, owned by FC3, if the merged run still shows it. The merged run does not (ZERO_HOT's F1 0 of 16), so the ship
  file files no FC3 row.
- **WM1 is queued, not mended:** the merged ensemble's one F1 path is CBK3's audit reading the window against a cap
  that the same month's peg break repriced after the last settle struck it (section 4). Its mend is in the bank's
  audit, outside this batch's findings. It passes by name under gate rule 3, as FC3 would have: the ship file's
  known-reds row names WM1. Whether it passes is the orchestrator's call.
- **PH1 is queued, not mended:** DenominationCheck's twins part on the mend's path from a fault of main's (section
  4), already passed by name by DK1's rows. A mend here would move the whole fixture's path and the default trace
  again, outside this batch's findings.
- **Finding 12 gets no new row:** EP1b owns the century's rates.
- **Finding 11 is real.** The fixed draw per building is not its cause; it is queued as PL1, the orchestrator's call (BP1-C, 11-28).
- **AshbourneCheck numbers its sections 1-4 in the brief's order** (5, 12, 11, 16), each titled with its finding.
- **The review line** of `tools/pick.sh` says "not needed", but the mend moves what the households pay for the city's
  paper, bonds and shares. I asked the orchestrator for a stranger's review at mends-at, as the brief has it.

## 4. The checks

- **AshbourneCheck: 81 assertions, all OK** at the merged tip (`a5bd4d878`, main 0.9.219 with CBK3). At `e91b8dfe8`,
  before the merge, it was 81 OK too, on figures CBK3's bank moves: there the owners were 5 households at 824.10.
  - Section 1: the lineup town at month 61 has 2 owner households, savings 2,792.74 each (thousands), take-home 0,
    no bills of their own, outgoings 46.89 a month (the whole plan 63.62), cushion 140.66.
    - 116 of 141 cells take home at least what they pay out every month, so their cushions are unchanged. No cell's
      cushion is under three months of its bills.
    - What the mend keeps back, 285.03k of 6,742.26k of deposits (4.2%), is within three months of the outgoings
      take-home does not cover (308.38k).
    - Re-planned through `plan()` at twice their savings, the owners' plan rises 62.84 -> 72.01, while their outgoings
      and cushion do not move (45.92, 137.77). Their spare grows by all of the wealth added, and twice the outgoings is
      twice the cushion. Under the first mend's cushion (`planned`) five of these go red.
    - Struck again through the production `restrike()` and `plan()` as a family with no take-home and no investment
      income, paying a rent of 46.89: bills 46.89, groceries 0.02, cushion 140.72, exactly three months of both. Twice
      the rent adds 140.66, three months of the raise. What it may put into a bond leaves three months of its bills.
      Under the third look's outgoings (the basket alone) five of these go red.
    - **What is spent keeps main's rule** (`spendingKeepsTheOldRule`). The cell whose outgoings pass its take-home by
      the most is the owners'. Held at savings of 70.33, halfway between its two cushions (0 and 140.66):
      - its luxury budget is 33.47 and its meal budget 16.73, each `min(want, savings past three months of
        take-home)` to the bit, and what it may put into a bond is 0;
      - it wants 2 cars at 17.5824, as many as with its outgoings set to nothing (the cushion then take-home's, exactly);
      - `takeCars` gives each household 0.1333 of a car, paid in cash to the bit (2.3443), nothing lent.
      - Red by mutation (at `e91b8dfe8`): with the four sites on `cushion()` again, four lines go red: the luxuries
        and meals read 0, no car is wanted. With only the car's cash line on it, the car is lent (4.27) and that line
        goes red.
    - An ask of 4 times the spare allots the households face 5,520k for 5,476k, and the production settle takes all
      of it. The owners' savings fall 2,792.74 -> 161.72, above their cushion: they held 82.8% of the households'
      deposits and paid 96.1% of what the households paid. No cell pays out of its cushion, the cells' payments equal
      the settle's figure to the cent, and the next month's money audit residual is 0.
  - Section 2: the yen piece's fair per 100 is 98.7023 three ways, to 1e-12. With the D$ 30% weaker, the principal
    moves x1.4286 (the cross) and the price at the same rate does not move. Ashbourne's own piece, rebuilt (section 1
    above, finding 12), is quoted 27.3723 by the production buyback against the 27.3723 it paid: the gap is 0.000016
    of a point, inside an absolute half-cent that no premium (0.3629 off) and a basis point more (0.0224 off) both miss.
    (REV-ASH4's second look: the first tolerance went through `close()`, relative above 1, so it was 0.137 at 27.37 and
    let the basis point through.) Month 205 is before `PolicyRates.TABLE_FIRST_MONTH`
    (1200). (REV-ASH4's first P1: the first version priced synthetic flows with no premium, inside 3 points.)
  - Section 3: two kilns draw 2 x 690 kW (the town 6,083 kW, 7,463 kW with them). Without the kilns, and with 96
    more houses, the draw moves by exactly -1,284 kW. Ashbourne's 9,354 and 8,146 kW leave two blocks and one over the 10 MW wires, so the price falls 78.5%
    while the load falls 12.9%.
  - Section 4: the families move -7.75% in the layoff month, under 0.1% a month after, and +7.72% when the jobs return.
    The households seeking a door rise. Nobody is lost: the families' and the outside adults add up to the cohorts',
    to 1e-6.
- **SaveFileCheck's two ASH4 sections** (REV-ASH4's fourth look; the whole harness exit 0 at the merged tip).
  - **Across a save, with a settlement pending.** AshbourneCheck's town sells a 30-year bond between the presses at
    four times the households' spare, and is saved with the households' allotment not yet settled, then loaded.
    - All 141 cells' outgoings and cushions cross to the bit. The owners have 46.8863 of outgoings on a take-home of
      0, and a cushion of 140.6588.
    - The allotment (cash and face) and the households' spare for paper match the live city's to the bit.
    - The production settlement takes 5,476.3225k in both, and every cell's savings after it match to the bit.
    - The same save loaded blind to the slot (every cell's outgoings struck out) makes the owners pay 2,635.04 each
      against 2,631.02.
  - **A save of this format from before the slot.** Today's save is rewritten with each cell's last slot dropped,
    the width a format-67 save written before ASH4 has, and loaded.
    - Every other position of every cell is where the full-width load puts it, to the bit.
    - Its cells read no outgoings, so every cell's cushion is three months of its take-home, the rule it was written
      under: the owners' is 0 against today's 140.66.
    - The month after, the plan strikes the outgoings again, and the money audit closes.
    - The fixture is built from today's save rather than one written by main's build, so it differs from today's save
      in the cells' width alone. The width is what the cells' loader (`restoreCells`) reads. Main's other fields are
      not exercised by it.
  - **Red by mutation:** a loader that reads past the slot and leaves 0 turns red the outgoings, the cushions, the
    spare and both settlement parities.
  - SaveFileCheck's full city also carries every cell's outgoings and cushion across its save.
- **`./batch-check.sh --fast`: green.**
- **The cushion's readers and the cell's width, on the merged tip, one at a time** (each harness's own `main`, at
  `a5bd4d878`; SaveFileCheck and AshbourneCheck above): BankRulesCheck, CarCheck, LuxuryRetailCheck, RestaurantsCheck,
  HouseholdCheck, BusinessServicesCheck, HoldersCheck, BondCheck, AuctionCheck, CardCheck, ExchangeCheck, EquityCheck,
  OwnerCheck, FundCheck, MarketCheck, CapitalGainsCheck, ShortSellingCheck, FloatLinkCheck, BasketYearCheck,
  DenominationCheck and LongPlaytest. The logs are in `target/ash/h3/`; the bills' run (`c3e284b1f`) is in
  `target/ash/h/`.
  - All green but for known reds. HoldersCheck has 4, CarCheck 1 and FundCheck 1, every one HT1-H's row in
    `docs/notes/known-reds.txt`. DenominationCheck has 16, 7 a year on and 9 a decade on, every one passed by name
    by DK1's rows (below). `./batch-check.sh` on the four: exit 3, known reds only.
  - BusinessServicesCheck's register of a cell's slots, which counts them by hand, gains the everyday slot (+1). It
    was red at the width before and is green after.
  - LongPlaytest: "All checks passed" over 2,400 months, the money audit included, and no finding (1,940 s; the
    spending fix's 1,464 s before the merge).
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
  - On the bills' path (`c3e284b1f`) the twins stay the same city for the year again. The fault is still main's,
    and any path may meet it, so PH1 stays queued.
  - On the merged tip (`a5bd4d878`) they part within the year again: 7 reds a year on, 9 a decade on. Main 0.9.219's
    own run (its classes built on scratch) is green a year on, with 9 reds a decade on. So the year's parting is on
    this batch's path, as on the third look's. It was not traced again. Every label is a DK1 part in
    `docs/notes/known-reds.txt`, and PH1 owns the class.
- **The default trace moves.** The default `LongPlaytest` with `-Dplaytest.trace` ran on main `22b6eb96f` (classes built
  on scratch from `git archive`) and on the first mend (`306552e27`). All eight files differ from the first year: in
  month 6 the households' savings go 22.327 -> 22.337, and in month 10 the city's cash and its tax move. So the
  80-run ensemble is run. The mend (`618d894be`, merged with ASH1, whose own ensemble shipped at 0.9.218) differs
  from both, the bills (`c3e284b1f`) from all three, and the spending fix (`e91b8dfe8`) from all four. Its main column would be `39fcbee71`, and that was not
  traced: the ensemble was run on the mend regardless. All five runs end with "All checks passed".
- **On the merged tip** the default trace ran on main 0.9.219 (`bbdb5fc08`, with CBK3; classes built on scratch) and on
  ASH4 merged with it (`a5bd4d878`). All eight files differ, and both runs end with "All checks passed" (2,009 s and
  1,940 s).

  | trace | main 0.9.219 `bbdb5fc08` | merged `a5bd4d878` |
  |---|---|---|
  | t-bank.csv | 06f5e52b70d26ef718312f43827c0685 | 2f89dfc7cd16bb718414105a14268ba1 |
  | t-bonds.csv | 9ed120b19e0ecc1a62296b4012df94ea | 460cc922f0558b21a5f3b6fa1ba1d83f |
  | t-borrow.csv | c87d567686deb691c85ac53ad23e47f7 | 37c136ad977714f1bafab4dfaf32f915 |
  | t-build.csv | b1ba2dcf969de44591c5d2db138f015b | 7808405ef4d60f007e52a3c5e874a38c |
  | t-house.csv | e1d7a1b4b241ce0b4520ddc7bcbde827 | 4d9f320eb6aba14952aa2cbb259c9cfc |
  | t-labour.csv | 723a0c4e3f825fc49b464163adeb89e3 | 536bfaee438b4dce6ce20d23e01c5898 |
  | t-month.csv | 6f7a0008b9e310c37226e4d86ffd262c | 0ed107434e65b030392f336c5f75849e |
  | t-pop.csv | 2243deb50f586248f10ef60abac81404 | 08a242b638eb300abeb67ea9291b1871 |

  - Its population every 200 months to 2,400: main 7k, 14k, 72k, 219k, 526k, 1,209k, 2,676k, 3,130k, 3,874k,
    4,364k, 5,184k, 5,722k; merged 5k, 8k, 24k, 98k, 229k, 520k, 1,191k, 1,880k, 2,851k, 3,799k, 4,900k, 6,064k.
  - The merged city climbs later and ends 6% higher. 2 banks have failed by month 2,400, against main's 5.
    Unemployment at month 2,400 is 14.8%, against 11.3%, and business debt is 701 a head, against 605.
  - It is one seed's path. The 80 runs' climb on the same model is ahead of main's (the ensemble below).

  | trace | main `22b6eb96f` | the first mend | the mend `618d894be` | the bills `c3e284b1f` | spending `e91b8dfe8` |
  |---|---|---|---|---|---|
  | t-bank.csv | 6ede455267023c0721bbc430e5d85c6e | daa9ee239e3f0bd946531935e1e115be | fdcdf1b9f20dca7809d8cba7500d0114 | 4a1d3597ba0bb77e9fdd2e083f4b1b4b | c6efc531bcbfa623fce47e8edecb5598 |
  | t-bonds.csv | 31d688038b45d088318307ab47345251 | 9f2e5abd2d2ab8ed7f1e4d961d2ff6e6 | a9807e29fc10ee0eed791bb6ed09e417 | 3ec752ba160428db46d142eb37ef9b44 | 059117f2dbe04b454878cdd68f6f565a |
  | t-borrow.csv | ad17225a89ac4910256628b34f9e707b | f5b6a2d7fdc18f852065ef6763f2501d | 8bc613daeacba7ffb36bb7b313cc09e0 | f796f6ad172c529cc34c61cd2d6177d6 | c51c4a91847478422e99aef5763f76a8 |
  | t-build.csv | 6cff5eaf4b95e1170e9cf4698b1fcaf4 | e69975157218e13b06d5d193e38d2ef4 | e07ca5ef1f2bd12204ba39d1f320d634 | 1e3b3ace79566597cfcabab0bff09dbc | d73f20c5bc8efbb675b87121260030cb |
  | t-house.csv | 66a37acc01890e7c405dff9c6eacebd6 | d630b9d45d638eb08536654c9af7d294 | 4f54435f8d9984023bcf0e3aa5b90782 | 767b9d029dc9ead29ff39afd9c90c284 | 68f249a6a85c0f6d9364268b7ad4912f |
  | t-labour.csv | f000ef90f546b8ed4658e078784bfed0 | 12da256991ec5c2795438d7515e382d3 | 2d1d9c51c7666685f50f978d96ff0251 | d4bca77acdf31ad3510311c42b70dd8a | e82a27d8216877bc84f11715c2bca5a7 |
  | t-month.csv | 9de355db849f31706f8b4dc4c93dbdcb | 0901c85a74055af1ec2657c9253c6d95 | f4dfee819ac674b51e81327b0961697e | d15069149b0d993db61185cfe2ae462f | 9a44cf6ad6cd1d33a3a89deecbf50959 |
  | t-pop.csv | 86d0749981733a5339b1dc2e1a33a91b | d5b52aca8eb6abbba2085141f82025c8 | d31cb019cfacb876c43042add822c11a | 4414f03549cb581bd21ec0b18331390e | ed3f1935f84a9d28a7d5f03eccfc6bb3 |

- **The default city on the bills' path stalls.** The traced run's population, every 200 months to 2,400, against
  main's (`22b6eb96f`):
  - the bills: 2k, 5k, 11k, 37k, 83k, 184k, 375k, 601k, 689k, 781k, 739k, 611k;
  - main: 3k, 9k, 23k, 58k, 132k, 243k, 555k, 1,045k, 2,003k, 2,505k, 3,471k, 4,267k. ASH4b reached 6.22M and the
    first mend 2.88M.
  - From month 1,600, unemployment runs 10-15%, against main's 9% and under. Jobs fall from 433k at month 2,000 to
    302k, and no one arrives after month 2,200.
  - Sixteen banks have failed by month 2,400, against main's 2. Business debt is 1,270 a head, against main's 476,
    and the luxury trade's net revenue goes to zero and below.
  - It is one seed's path, and nothing the audit counts as a finding. The 80 runs do not show it as the rule: at
    month 2,400 the bills' AUTO2 cities stand where ASH4b's did (section 1). The bank failures are not placed: no
    null ran the default trace.
  - **With the spending fix (`e91b8dfe8`) it does not stall:** 2k, 6k, 13k, 34k, 106k, 310k, 724k, 1,332k, 2,141k,
    3,128k, 4,075k, 5,437k.
    - Three banks have failed by month 2,400, against main's 2. Unemployment at month 2,400 is 12.2%, against main's
      8.6% and ASH4b's 12.8%. Business debt is 978 a head, against main's 476.
    - LongPlaytest: "All checks passed" and no finding (1,464 s).

- **On ASH1's main, under the first mend** (merged at `caed78d5e`; ASH1 `39fcbee71` changed the POWER row, the grid
  step and the grid's posts), AshbourneCheck had 57 OK. Finding 11 measures the same: draws 4,257 -> 5,637 -> 4,318 kW, and Ashbourne's two
  draws leave 2 and 1 blocks over the wires, the price -78.5%. Finding 5 measures the same: the owners held 86.7% and
  paid 96.5%. The buyback quotes 27.3723. On CBK3's main (merged at `a5bd4d878`) finding 5 was measured again: the
  owners held 82.8% and paid 96.1%, and the buyback still quotes 27.3723 (above).

- **The ensemble on the merged tip: FAIL, hard 6 of 10, by one path of a fault of main's** (`docs/ensemble/ASH4.md`,
  label ASH4e). It ran on the gate box at `a5bd4d878` (0.9.219 with CBK3), 80 of 80 runs: 7.9 min, then 26.3 min
  after it yielded to HG1's dry run. It was scored here against `docs/ensemble/0.9.219.md`, and 19 of 26 criteria
  pass.
  - **M1** is 0 months of failed money audit in every policy.
  - **F1** is 1 of 16 in AUTO2, ZERO_SLACK, SHOCK and HIGH, and 0 in ZERO_HOT. It is one path: AUTO2 seed 3 to month
    148, which the other three replay until their policies start. The finding is CBK3's "the window lent beyond
    eligible collateral after haircuts": window $9,888.30 against collateral $8,974.84.
  - **The trace** (a helper's probe, AUTO2 seed 3 to month 150, printing every read of the cap):
    - Month 148's last settle (`Game.java` ~14982) struck the window at $9,888.30 under a cap of $19,610.98. The cap
      is business loans $7,117.19 x0.70, family loans $12.48 x0.15, and the bank's city paper $15,396.92 x0.95.
    - The money audit is struck next. Then `foreign.repriceCurrency()` (~15060) breaks the peg: the vault is empty, the
      rate goes 1.00 to 2.0076, and the curve leaves the US-tied 1.5% for the inflation rule's path on 18.6%
      inflation (the policy rate 24.4% at month 149).
    - The city paper is worth $4,200.98, down 73%, so the cap LongPlaytest reads is $8,974.84. Nothing was lent past
      the cap. Month 149's first settle cuts the window to the new cap, $8,324.45, and the bank repays $1,563.85.
    - `git diff origin/main` is empty for Bank, CentralBank, DebtManager, Game, ForeignAccounts and LongPlaytest.
      Main's own seed 3 breaks its peg too, at month 153, but its window of $4,288 is covered by its business loans
      alone. ASH4's path breaks it five months sooner, with a window about twice main's.
    - Queued as WM1 (`docs/queue/WM1.md`), whose mend judges the window against the cap it can be held to. Under gate
      rule 3 it passes by name in the ship file's known-reds, owned by WM1. Whether it passes is the orchestrator's
      call.
  - FC3's class, ZERO_HOT seed 3's negative GDP, is not met on this path.
  - **Growth.** All 16 AUTO2 seeds reach 40,000 people, at a median month of 532 [466 to 734], against main's 595 [526
    to 1,126]. The final populations are all above main's, and all pass T2:
    - AUTO2 7.21M against 6.75M, ZERO_SLACK 5.27M against 5.08M, ZERO_HOT 15,341 against 13,698;
    - SHOCK 217,392 against 197,311, HIGH 96,493 against 86,550.
  - **Changed verdicts, all info:**
    - AUTO2's A1 goes FAIL to PASS: 2.80% a year, 13 of 16 in band.
    - AUTO2's A3 goes PASS to FAIL: one seed's deflation spell from month 240 is 12 months long, against main's 4 at
      most. Every final population is at least 93.00% of its peak.
    - SHOCK's K2 goes PASS to FAIL: inflation over months 720-840 is 3.09% a year against the 3.00% line. It is -0.17
      points under its twin's.
    - ZERO_HOT's H2 goes VACUOUS to PASS.
    - ZERO_HOT's, SHOCK's and HIGH's T2 go FAIL to PASS: the baseline's own T2 failed against its predecessor.
  - **Bank failures, cumulative (info):** AUTO2 76 against 52, ZERO_SLACK 74 against 51, ZERO_HOT 33 against 28,
    SHOCK 40 against 51, HIGH 40 against 49. 14 of 16 runs have failures in every policy, as on main.
  - None is placed: no null ran on 0.9.219. Neither the sign nor the size of any of these is validated by a real
    measure.
- **The ensemble on the spending fix: PASS, hard 10 of 10** (label ASH4d). It ran on the
  gate box at `e91b8dfe8`, 80 of 80 runs in 32.3 min, and was scored here against `docs/ensemble/0.9.218.md`.
  22 of 25 criteria pass.
  - **M1** is 0 months of failed money audit in every policy. **F1** is 0 of 16 in every policy: FC3's class is
    not met on this path.
  - **Changed verdicts, all info:**
    - AUTO2's A1 goes FAIL to PASS: inflation from month 240 is 2.84% a year, 15 of 16 in band.
    - HIGH's R2 goes PASS to FAIL: unemployment over its twin is 0.47 points, against 1.46.
    - HIGH's T2 goes PASS to FAIL: 52,600 [31,843 to 237,300] against 74,812. ASH4b had 50,097 and ASH4c 40,412.
    - HIGH has no null draws here, so these are not placed. Neither their sign nor their size is validated by any
      real measure.
  - **Final populations elsewhere:** AUTO2 7.89M against 8.18M, ZERO_SLACK 5.55M against 5.26M, ZERO_HOT 6,154
    against 6,191, SHOCK 152,427 against 177,650. All pass T2.
  - This run is on the branch before CBK3's merge, so that its growth pairs with the nulls (section 1). The run that
    counts is the one on the merged tip.
- **The ensemble on the bills: FAIL, hard 9 of 10, by one run of a fault of main's** (label ASH4c). It ran on the gate box at `c3e284b1f`, 80 of 80 runs in 30.5 min, and was scored here against
  `docs/ensemble/0.9.218.md`. 20 of 25 criteria pass.
  - **M1** is 0 months of failed money audit in every policy.
  - **F1** is 0 of 16 everywhere but ZERO_HOT, 1 of 16. Seed 3 raises "negative monthly GDP" in month 275 only:
    -85.08 (C 157, Iconstr 199, Istock 119, G 198, NX -759; imports: food 13, materials 400, raw 958; exports 612).
    Rerun here, the same run reads -85.08 to the cent.
  - **The cause is FC2b's handover and R1g's early premium** (a helper's probe, `target/ash/zh/`):
    - `BuildingsStacks.handAbroad` moves a payer's whole home contract to the foreign crews, where the handover was
      for 5,336 of the Pulp Mill's 90,400 points.
    - `advanceConstruction` then earns the home crews' share at `(contractValue - abroadValue) * fh`, which is 0.
      Yet they still draw `(materialsOwed - abroadMat) * fh` of material.
    - From m270 to m276 the sites drew 221-270 units a month with no revenue against them, imported for the most
      part. A Distribution Substation was drained the same way by `earnAhead`. GDP fell from about 1,100 to -85.
  - **Not this batch's code.** ASH4's diff (Household, HouseholdBalance, Exchange) touches no site, contract or
    account. The cushion changed only the path that reaches the fault.
  - **The mend is queued as FC3** (`docs/queue/FC3.md`), with ForeignChoiceCheck's fixture that causes it on main.
  - **Whether it passes is the orchestrator's call.** Under `docs/contract.md`'s gate rule 3, and as with HT1's F1 on
    2026-11-22, a red that comes from main passes by name only on that call. The orchestrator made it on 2026-11-28:
    ZERO_HOT seed 3 passes by name, with FC3 as its owner in the ship file's known-reds, if the merged run still shows
    it.
  - **Changed verdicts, info:** SHOCK's T2 is 119,464 [73,201 to 364,815] against 177,650 (67%). HIGH's T2 is 40,412
    [30,875 to 90,769] against 74,812 (ASH4b 50,097). Neither is placed, and neither is validated by any real
    measure: HIGH and SHOCK have no null draws here.
  - **Final populations elsewhere:** AUTO2 6.75M against 8.18M, ZERO_SLACK 4.68M against 5.26M, ZERO_HOT 5,561
    against 6,191. All pass T2.
- **The third look's ensemble: PASS** (label ASH4b). It ran on the gate box at `618d894be`, the
  mend, and yielded to CBK3's dry run partway through: 80 of 80 runs. It was scored here against the newest baseline,
  `docs/ensemble/0.9.218.md` (ASH1's).
  - **Hard criteria, 10 of 10:** M1 is 0 months of failed money audit in every policy, and F1 is 0 of 16 runs with a
    LongPlaytest finding in every policy. 22 of 25 criteria pass.
  - **One changed verdict, info:** HIGH's T2, final population, is 50,097 [28,732 to 155,024] against 74,812
    [22,414 to 168,964], under the 75% line (56,109). The first mend had 39,690. The row was FAIL in 0.9.216's own
    scoring (56,156) and PASS in 0.9.218's, so HIGH's final population swings by a third between neighbouring models.
    HIGH has no null draws here, so its move is not placed. It may be the cushion's effect or HIGH's own swing, and
    neither its sign nor its size is validated by any real measure. It does not stop a ship.
  - HIGH's R2, which changed under the first mend, is PASS again: 0.72% against 1.46%. AUTO2's A1 and A2 are FAIL in
    the baseline as here.
  - **Final populations elsewhere:** AUTO2 6.82M against 8.18M, ZERO_SLACK 4.82M against 5.26M, ZERO_HOT 7,058
    against 6,191, SHOCK 147,116 against 177,650. All pass T2.
  - **The first mend's ensemble** (`caed78d5e`, label ASH4, 31.9 min) also passed, 10 of 10 hard. Its growth loss is
    in section 1. `docs/ensemble/ASH4.md` holds the merged tip's run (ASH4e), in place of all four.

## 5. For Jerus

- **ASH4-A, D32's appetite** (finding 5, what is left). With the cushion mended, an owner family keeps 3 months of
  its spending. One large city issue still takes the rest of its balance: on the fixture, 94% of the owners' deposits,
  because the households bid a fifth of the issue per point of spread, up to their spare. Real portfolios do not put
  most of their liquid wealth into one 30-year issue in a month. Capping the share of a household's wealth in one
  issue would change your D32 (2026-10-20, "no half-of-the-issue cap"). Not done.
- **HF1** changes your design of 2026-09-11, the out of work as their own household structure. Its brief says how.

## 6. For the manual

Not this batch's (no edition). When the next edition comes:

- **The cushion:** "Households keep three months of what they take home, or of what they pay out every month (their rent and bills, then their spending) if that is more,
  before they buy shares, the city's paper or a business's bonds, or send money abroad. The owner families, who live
  on dividends, keep three months of what they pay out; what their fortune buys besides is not counted, so a richer
  family keeps the same cushion and invests the rest. What a household spends, on a car, a night out or something
  fine, it spends out of what it holds past three months of its take-home."
- **The yen bond and the other foreign moneys:** foreign paper is priced on its own money's curve. In the years before
  2000 that curve is the stand-in century's rule, until EP1b.

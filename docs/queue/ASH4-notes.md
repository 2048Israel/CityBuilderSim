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
- **The mend** (★1): `HouseholdBalance.cushion(c)` is `SHARE_CUSHION_MONTHS` of the larger of take-home and planned
  spending (`c.planned`), and all nine paths read it (seven in `HouseholdBalance`, two in `Exchange`). On the save's
  cells the cushions come to Cr 2.20M (21% of deposits) instead of Cr 0.78M. A saver's cushion is what it was.
- **What is left, for Jerus (★2):** the owners still put about 90% of their balance into one big issue. The cushion
  is now 3 months of their spending, and D32's rule (Jerus, 2026-10-20: a fifth of an issue per point of spread over
  the deposit rate, up to the issue, out of the spare) takes all the rest when the issue is large against the spare.
  On the fixture town the owners held 86.7% of deposits and paid 96.5% of the households' part. A share-of-wealth
  limit on one issue would be a change to D32, and so his call. It is not made here.

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
- `AshbourneCheck`, new, in `AllChecks.HARNESSES`, with four sections, one per finding (below).
- `CarCheck`'s fixtures that set "the cushion and two cars" read `HouseholdBalance.cushion(c)`, so the premise holds
  under the mend. Its three reds are main's known reds (HT1-H) before and after, unchanged.
- `docs/queue/HF1.md`, the brief for finding 16's mend.
- **The held figures (AP5-AP8):** the mend adds no writer of anything they hold (the labour figures, the posts, the
  warrant, the order book's order, the build quote's abroad terms). It adds a read of `c.planned` to the spare, and
  every spare is computed fresh on each call: `spareForPaper`, `bondSpare`, `Exchange.spareOf` and `shortOfCushion`.
  The auction's `Bidders` takes one snapshot per quote, with no writer between.

## 3. The calls

- ★1 **The cushion is three months of take-home or of planned spending, whichever is greater, on every path.** A
  transaction balance pays the months ahead's outgoings (Baumol-Tobin: money held in proportion to spending).
  - Why the greater: it never lowers a saver's cushion. A household that spends less than it takes home keeps the old
    cushion, so the change falls only on those who spend more than they take home: the owner families, the retired
    with no pension, and a family drawing on its wealth.
  - Why every path: one balance, one cushion. A share path that still saw the owners' whole balance as spare would be
    the same defect on another path.
  - `c.planned` over `c.want`: planned is what the cell will spend after its means. The two were equal in every
    Ashbourne cell.
- ★2 **D32's appetite is left as it is**, for Jerus (pending row ASH4-A).
- ★3 **Finding 16 is queued, not mended** (HF1): it moves his 2026-09-11 design and four subsystems.
- **Finding 12 gets no new row:** EP1b owns the century's rates.
- **Finding 11 is real.** The fixed draw per building is not its cause; it is queued as PL1, the orchestrator's call (BP1-C, 11-28).
- **AshbourneCheck numbers its sections 1-4 in the brief's order** (5, 12, 11, 16), each titled with its finding.
- **The review line** of `tools/pick.sh` says "not needed", but the mend moves what the households pay for the city's
  paper, bonds and shares. I asked the orchestrator for a stranger's review at mends-at, as the brief has it.

## 4. The checks

- **AshbourneCheck: 57 assertions, all OK.**
  - Section 1: the lineup town at month 61 has 5 owner households, savings 1,197.05 each (thousands), take-home 0,
    planned 36.27, cushion 108.80; 116 of 141 cells take home at least what they plan, so their cushions are unchanged. An ask of 4 times
    the spare allots the households face 5,639k for 5,573k, and the production settle takes all of it. The owners'
    savings fall 1,197.05 -> 121.48, above their cushion. No cell pays out of its cushion. The cells' payments equal
    the settle's figure to the cent, and the next month's money audit residual is 0.
  - Section 2: the yen piece's fair per 100 is 98.7023 three ways, to 1e-12. With the D$ 30% weaker, the principal
    moves x1.4286 (the cross) and the price at the same rate does not move. Ashbourne's own piece, rebuilt (section 1
    above, finding 12), is quoted 27.3723 by the production buyback against the 27.3723 it paid, inside a half-cent that
    no premium (27.7352) and a basis point more (27.3498) both miss. Month 205 is before `PolicyRates.TABLE_FIRST_MONTH`
    (1200). (REV-ASH4's first P1: the first version priced synthetic flows with no premium, inside 3 points.)
  - Section 3: two kilns draw 2 x 690 kW. Without the kilns, and with 61 more houses, the draw moves by exactly
    -1,319 kW. Ashbourne's 9,354 and 8,146 kW leave two blocks and one over the 10 MW wires, so the price falls 78.5%
    while the load falls 12.9%.
  - Section 4: the families move -7.75% in the layoff month, under 0.1% a month after, and +7.72% when the jobs return.
    The households seeking a door rise. Nobody is lost: the families' and the outside adults add up to the cohorts',
    to 1e-6.
- **`./batch-check.sh --fast`: green.**
- **The cushion's readers, in one run, one at a time** (`-Dcbs.parallel=1`): AshbourneCheck, AuctionCheck,
  HoldersCheck, FundCheck, ExchangeCheck, EquityCheck, CarCheck, CardCheck, BasketYearCheck, DenominationCheck,
  FloatLinkCheck, MarketCheck, OwnerCheck, CapitalGainsCheck, ShortSellingCheck and LongPlaytest.
  - The verdict is exit 3: known reds only, in DenominationCheck (DK1), HoldersCheck (HT1-H) and CarCheck (HT1-H).
  - LongPlaytest: "All checks passed" over 2,400 months, the money audit included.
- **The default trace moves.** The default `LongPlaytest` with `-Dplaytest.trace` ran on main `22b6eb96f` (classes built
  on scratch from `git archive`) and on the branch's model (`306552e27`; the model is unchanged since). All eight files
  differ, from the first year: in month 6 the households' savings 22.327 -> 22.337, and in month 10 the city's cash and
  its tax. Both runs end with "All checks passed". So the 80-run ensemble is run (below).

  | trace | main `22b6eb96f` | ASH4 |
  |---|---|---|
  | t-bank.csv | 6ede455267023c0721bbc430e5d85c6e | daa9ee239e3f0bd946531935e1e115be |
  | t-bonds.csv | 31d688038b45d088318307ab47345251 | 9f2e5abd2d2ab8ed7f1e4d961d2ff6e6 |
  | t-borrow.csv | ad17225a89ac4910256628b34f9e707b | f5b6a2d7fdc18f852065ef6763f2501d |
  | t-build.csv | 6cff5eaf4b95e1170e9cf4698b1fcaf4 | e69975157218e13b06d5d193e38d2ef4 |
  | t-house.csv | 66a37acc01890e7c405dff9c6eacebd6 | d630b9d45d638eb08536654c9af7d294 |
  | t-labour.csv | f000ef90f546b8ed4658e078784bfed0 | 12da256991ec5c2795438d7515e382d3 |
  | t-month.csv | 9de355db849f31706f8b4dc4c93dbdcb | 0901c85a74055af1ec2657c9253c6d95 |
  | t-pop.csv | 86d0749981733a5339b1dc2e1a33a91b | d5b52aca8eb6abbba2085141f82025c8 |

## 5. For Jerus

- **ASH4-A, D32's appetite** (finding 5, what is left). With the cushion mended, an owner family keeps 3 months of
  its spending. One large city issue still takes the rest of its balance: on the fixture, 90% of the owners' deposits,
  because the households bid a fifth of the issue per point of spread, up to their spare. Real portfolios do not put
  most of their liquid wealth into one 30-year issue in a month. Capping the share of a household's wealth in one
  issue would change your D32 (2026-10-20, "no half-of-the-issue cap"). Not done.
- **HF1** changes your design of 2026-09-11, the out of work as their own household structure. Its brief says how.

## 6. For the manual

Not this batch's (no edition). When the next edition comes:

- **The cushion:** "Households keep three months of what they take home, or of what they spend if that is more,
  before they buy shares, the city's paper or a business's bonds, send money abroad, or pay cash for a car or a night
  out. The owner families, who live on dividends, keep three months of their spending."
- **The yen bond and the other foreign moneys:** foreign paper is priced on its own money's curve. In the years before
  2000 that curve is the stand-in century's rule, until EP1b.

# AshbourneCheck.java - 480 lines · 17 methods · 8 constants · harnesses

`ham/citybuildersim/AshbourneCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Four findings from Jerus's Ashbourne (slot 2, months 193-241), each caused on a fixture of this harness's own (ASH4;
> docs/queue/ASH-findings.md, docs/queue/ASH4-notes.md).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. FINDING 5 - THE HOUSEHOLDS' DEPOSITS INTO ONE BOND (a mend). In Ashbourne six owner families held 70% of the households'
>      deposits with a take-home of 0 (OwnerHousehold.earningWeight() is 0: they live on dividends), so the old cushion (three months
>      of take-home) was 0 and one 30-year bond took 91% of the deposits. The cushion is now three months of what a household
>      takes home or plans to spend, the greater (HouseholdBalance.cushion()): a saver's is what it was, the owners' is no longer
>      nothing, and no cell pays a bond out of its cushion, through the production sale and the production settlement.
> 
>   2. FINDING 12 - THE YEN BOND BOUGHT BACK AT 27.37 (no mend: the price is the yen curve's; the yen RATE is the stand-in
>      century's, which EP1b retires). The piece is booked in yen, valued on the yen's curve at the yen's rate plus the country
>      premium once, its flows and its principal convert at one rate (so a move of the D$ does not move its price per 100), and
>      the level of that price is the rate's alone: 13% (the stand-in's January 1917) prices a 3.5% bond of 312 months where
>      Ashbourne saw it, 5.11% (the Bank of Japan's discount rate of 1917) prices it over 60.
> 
>   3. FINDING 11 - THE LOAD FELL WHILE THE TOWN GREW (real: two Lime Kilns closed; the load is the buildings' draw, one unit).
>      The city's draw is the sum of its buildings' powerBought(), in kW in both months; and the struck price on the founding grid
>      alone is the scarcity price on the half-blocks the wires leave unserved, so 13% less load is 77% less price when it
>      brings a block under the wires.
> 
>   4. FINDING 16 - HOUSEHOLDS MERGE 8% WHEN A BOOM ENDS (the cause found; the mend is a queued batch, HF1: the families keep
>      their out-of-work adults). The families are built from the adults who WORK, so a laid-off adult leaves the family the
>      month the job goes and becomes a one-adult household looking for a door: the whole move comes in one month at full pace,
>      and comes back in one when the jobs do.
> 
> Every fixture CAUSES its condition; every figure is read off the model's own getters and constants.

**Uses:** [PowerDispatch](PowerDispatch.md) (27), [Game](Game.md) (10), [HouseholdBalance](HouseholdBalance.md) (8), [DebtMoneysCheck](DebtMoneysCheck.md) (8), [WorldMoneys](WorldMoneys.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [Household](Household.md) (3), [Debt](Debt.md) (3), [MoneyAudit](MoneyAudit.md) (3), [PayTier](PayTier.md) (3), [FamilyModel](FamilyModel.md) (3), [AgeBand](AgeBand.md) (3), [LongTermBond](LongTermBond.md) (2), [Nations](Nations.md) (2), [PolicyRates](PolicyRates.md) (2), [FamilyStructure](FamilyStructure.md) (2), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [OwnerHousehold](OwnerHousehold.md) (1), [PriceLevel](PriceLevel.md) (1), [DebtManager](DebtManager.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Buyback](Buyback.md) (1), [YieldCurve](YieldCurve.md) (1), [BuildingManager](BuildingManager.md) (1), [ServicesManager](ServicesManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [HouseholdMemoryCheck](HouseholdMemoryCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 114 | 1. finding 5: the households' deposits into one bond |
| 195 | 2. finding 12: the yen bond bought back at 27.37 |
| 306 | 3. finding 11: the load fell while the town grew |
| 405 | 4. finding 16: households merge when a boom ends |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 206 | `AshbourneCheck.KEEPS_30` | `0.70` | The D$ keeps this much of its worth in US$ in the fall section 2 moves it by (DebtMoneysCheck.KEEPS's, 30% rather than 10%). |
| 209 | `AshbourneCheck.STAND_IN_YEN` | `.13` | Ashbourne's yen policy rate in January 1917, the stand-in century's (worldMoney.policyJPY in its history). |
| 212 | `AshbourneCheck.BOJ_1917` | `.0511` | The Bank of Japan's discount rate of 1917, 1.4 sen a day (the US: New York Fed 3.75% in 1917, FRED M13009USM156NNBR). |
| 215 | `AshbourneCheck.ASHBOURNE_PER_100` | `27.37` | What Ashbourne's buyback paid, per 100 (docs/queue/ASH-findings.md, 12). |
| 309 | `AshbourneCheck.DRAW_193` | `9_354` | The draw Ashbourne's dispatch read in month 193, kW: its underlying draw before the losses and the season. |
| 312 | `AshbourneCheck.DRAW_205` | `8_146` | ...and in month 205, after the two Lime Kilns closed. |
| 429 | `AshbourneCheck.END` | `12, BACK = 20, MONTHS = 24` | The month the boom ends, the months the jobs stay gone and the month they are back. |
| 432 | `AshbourneCheck.LAID` | `400` | Adults of the town out of work the months the boom is off: 400 unskilled jobs gone, 16% of the town's 2,500. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `static int fails` |  |
| 42 | `static PrintStream out` |  |
| 43 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 442 | **type** `public class AshbourneCheck` | Four findings from Jerus's Ashbourne (slot 2, months 193-241), each caused on a fixture of this harness's own (ASH4; docs/queue/ASH-findings.md, docs/queue/ASH4-notes.md). |
| 45 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 50 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 56 | 5 | `static void quietly(Runnable r)` |  |
| 62 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 69 | 18 | `static Game town()` | AuctionCheck's lineup city, this harness's own copy: given the money and the ground, a bank and three hundred houses, sixty months played. |
| 89 | 5 | `static double settle(Game g) throws Exception` | The production settlement of the households' part of a sale (Game.householdsTakeTheirShare, private), what it took from them. |
| 95 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. finding 5: the households' deposits into one bond (lines 114-194)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 79 | `static void section1(Game g) throws Exception` |  |

### 2. finding 12: the yen bond bought back at 27.37 (lines 195-305)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 6 | `static double[] bullet(double face, double couponAYear, int months)` | A bond of this face paying this coupon a year, monthly, for this many months and the face with the last: the shape Debt.remainingCashFlows() returns. |
| 217 | 88 | `static void section2()` |  |

### 3. finding 11: the load fell while the town grew (lines 306-404)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 5 | `static int blocksOver(PowerDispatch.Month m, double wires)` | How many blocks of the month's three the wires leave short at this draw, off the dispatch's own half-blocks. |
| 321 | 4 | `static PowerDispatch.Month struck(double draw, double season, double wires)` |  |
| 326 | 78 | `static void section3(Game g)` |  |

### 4. finding 16: households merge when a boom ends (lines 405-480)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 7 | `static double[] jobs(double unskilled)` |  |
| 415 | 5 | `static double sum(double[] a)` |  |
| 422 | 5 | `static double familyAdults(FamilyModel f)` | The adults the families hold: every shape's households times the adults in one. |
| 434 | 46 | `static void section4()` |  |


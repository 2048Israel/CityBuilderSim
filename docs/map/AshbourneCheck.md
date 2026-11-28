# AshbourneCheck.java - 522 lines · 17 methods · 13 constants · harnesses

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
>      premium once, and its flows and its principal convert at one rate (so a move of the D$ does not move its price per 100).
>      Ashbourne's own piece (Y72M, 3.5%, sold at month 157, 312 months left at 205), at the stand-in's 13% and the premium its
>      owing and exports give (0.1594%, worked by hand from its save), is priced by the production buyback at what Ashbourne
>      paid, 27.37, inside the half-cent a quarter of a basis point of premium would miss; at the Bank of Japan's 5.11% of 1917
>      the same piece is worth over 60.
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

**Uses:** [PowerDispatch](PowerDispatch.md) (27), [Game](Game.md) (11), [DebtMoneysCheck](DebtMoneysCheck.md) (10), [HouseholdBalance](HouseholdBalance.md) (8), [Debt](Debt.md) (4), [WorldMoneys](WorldMoneys.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Household](Household.md) (3), [MoneyAudit](MoneyAudit.md) (3), [PayTier](PayTier.md) (3), [FamilyModel](FamilyModel.md) (3), [AgeBand](AgeBand.md) (3), [DebtManager](DebtManager.md) (2), [Buyback](Buyback.md) (2), [Nations](Nations.md) (2), [PolicyRates](PolicyRates.md) (2), [FamilyStructure](FamilyStructure.md) (2), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [OwnerHousehold](OwnerHousehold.md) (1), [PriceLevel](PriceLevel.md) (1), [LongTermBond](LongTermBond.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [YieldCurve](YieldCurve.md) (1), [BuildingManager](BuildingManager.md) (1), [ServicesManager](ServicesManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [HouseholdMemoryCheck](HouseholdMemoryCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 116 | 1. finding 5: the households' deposits into one bond |
| 197 | 2. finding 12: the yen bond bought back at 27.37 |
| 348 | 3. finding 11: the load fell while the town grew |
| 447 | 4. finding 16: households merge when a boom ends |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 200 | `AshbourneCheck.KEEPS_30` | `0.70` | The D$ keeps this much of its worth in US$ in the fall section 2 moves it by (DebtMoneysCheck.KEEPS's, 30% rather than 10%). |
| 203 | `AshbourneCheck.STAND_IN_YEN` | `.13` | Ashbourne's yen policy rate in January 1917, the stand-in century's (worldMoney.policyJPY in its history, month 205). |
| 206 | `AshbourneCheck.BOJ_1917` | `.0511` | The Bank of Japan's discount rate of 1917, 1.4 sen a day (the US: New York Fed 3.75% in 1917, FRED M13009USM156NNBR). |
| 209 | `AshbourneCheck.ASH_FACE_YEN` | `72_000, ASH_COUPON =.035` | Ashbourne's yen piece, as its save's log has it ("Borrowed Y72.0M abroad: 30-year term bond", month 157): the face in thousands of yen, the coupon a year. |
| 212 | `AshbourneCheck.ASH_SOLD` | `157, ASH_TERM = 360, ASH_BOUGHT_BACK = 205` | ...sold at month 157 for 360 months, and bought back whole at month 205, with 312 months left. |
| 215 | `AshbourneCheck.ASH_OWED` | `992.435, ASH_PAID = 271.652` | What the buyback took and paid, Cr thousands, its own line: "Bought back Cr 992,435 of TERM paper for Cr 271,652 (27.37 per 100 on average, stop 27.37)". |
| 221 | `AshbourneCheck.ASH_EXPORTS` | `1087.3017` | Ashbourne's trailing month of exports at month 205, Cr thousands: ForeignAccounts' trailing mean (COVER_WINDOW) rebuilt from its history's exportsAbroad, the rebuild giving the saved figure at month 241 (1774.4850) to... |
| 229 | `AshbourneCheck.ASH_PREMIUM` | `.0015943` | Ashbourne's country premium at month 205, worked by hand from those figures, independently of the price (its save's foreignStanding is [0, -1]: never a default, no scar): MAX_COUNTRY_PREMIUM .16 x (SOLVENCY_WEIGHT .6 ... |
| 232 | `AshbourneCheck.PER_100_TOLERANCE` | `.005` | The record rounds the price to the cent of a point; a basis point of premium moves this piece 0.0225, so a premium a quarter of a basis point wrong is outside. |
| 351 | `AshbourneCheck.DRAW_193` | `9_354` | The draw Ashbourne's dispatch read in month 193, kW: its underlying draw before the losses and the season. |
| 354 | `AshbourneCheck.DRAW_205` | `8_146` | ...and in month 205, after the two Lime Kilns closed. |
| 471 | `AshbourneCheck.END` | `12, BACK = 20, MONTHS = 24` | The month the boom ends, the months the jobs stay gone and the month they are back. |
| 474 | `AshbourneCheck.LAID` | `400` | Adults of the town out of work the months the boom is off: 400 unskilled jobs gone, 16% of the town's 2,500. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |
| 44 | `static PrintStream out` |  |
| 45 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 482 | **type** `public class AshbourneCheck` | Four findings from Jerus's Ashbourne (slot 2, months 193-241), each caused on a fixture of this harness's own (ASH4; docs/queue/ASH-findings.md, docs/queue/ASH4-notes.md). |
| 47 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 52 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 58 | 5 | `static void quietly(Runnable r)` |  |
| 64 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 71 | 18 | `static Game town()` | AuctionCheck's lineup city, this harness's own copy: given the money and the ground, a bank and three hundred houses, sixty months played. |
| 91 | 5 | `static double settle(Game g) throws Exception` | The production settlement of the households' part of a sale (Game.householdsTakeTheirShare, private), what it took from them. |
| 97 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. finding 5: the households' deposits into one bond (lines 116-196)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 79 | `static void section1(Game g) throws Exception` |  |

### 2. finding 12: the yen bond bought back at 27.37 (lines 197-347)

| line | len | member | says |
|---:|---:|---|---|
| 234 | 61 | `static void section2()` |  |
| 297 | 50 | `static void rebuilt()` | Ashbourne's buyback, rebuilt on a town of its own: its piece, its premium, its rate, priced by the production quote. |

### 3. finding 11: the load fell while the town grew (lines 348-446)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 5 | `static int blocksOver(PowerDispatch.Month m, double wires)` | How many blocks of the month's three the wires leave short at this draw, off the dispatch's own half-blocks. |
| 363 | 4 | `static PowerDispatch.Month struck(double draw, double season, double wires)` |  |
| 368 | 78 | `static void section3(Game g)` |  |

### 4. finding 16: households merge when a boom ends (lines 447-522)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 7 | `static double[] jobs(double unskilled)` |  |
| 457 | 5 | `static double sum(double[] a)` |  |
| 464 | 5 | `static double familyAdults(FamilyModel f)` | The adults the families hold: every shape's households times the adults in one. |
| 476 | 46 | `static void section4()` |  |


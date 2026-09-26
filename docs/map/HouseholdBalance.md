# HouseholdBalance.java - 4,111 lines · 208 methods · 43 constants · model

`ham/citybuildersim/HouseholdBalance.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The households' balance sheet: what they have saved, what they owe, and what
> happens in the month they cannot cover the shop.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Retail demand was `min(storeCoverage, population)` - a headcount, with no
> reference whatever to what anybody earned. So the shops sold the same basket
> to an unskilled single adult on $552 and to an Elite household on $18,673,
> and the household screen carried a five-line paragraph admitting it:
> 
>     "Nothing in the model ties spending to income yet - rent is the same per
>      head for everyone and so is the weekly shop - so a poor household is
>      charged what a rich one is. Those deficits are a missing budget
>      constraint, not a result."
> 
> A deficit that nothing funds is money from nowhere. This is the constraint.
> 
> ==================== THE WATERFALL ====================
> 
> Jerus's design, and it is the right shape: a household short of money does
> not simply eat less. It spends its savings, then it borrows, and only when
> both are gone does it go without - at which point going without is a health
> problem rather than an accounting one.
> 
>     payslip  ->  rent  ->  fees (healthcare, tuition, interest)  ->  food
> 
>     food short?   savings first, then credit, then go hungry
>     food spare?   pay down the debt first, then bank it
> 
>     ...and the clinic's fee is the one bill that gives way before food
>     (2026-09-19): a household that has run through its income, its
>     savings, its shares and its credit and would still eat less skips
>     the care bill first, and that share of its people goes untreated.
>     See Household.affordCare().
> 
> Debt before hunger and repayment before saving are both deliberate: they are
> what a household actually does, and each is the direction that makes the
> failure arrive slowly enough for the player to see it coming. The waterfall
> itself lives in Household.settle(); this class decides what each household
> is handed and adds up what they did.
> 
> ==================== SIXTY-EIGHT CELLS, NOT SEVEN ROWS ====================
> 
> Jerus, 2026-09-10: "I need it per household and pay tier type."
> 
> Until then the stocks were kept per pay tier - seven rows - and the header
> here argued that the shape could not carry a stock because FamilyModel
> rebuilds every household from scratch each month: "a stock attached to
> 'unskilled couple with a teen' would be a stock of nothing, handed to a
> different set of people every month." The tier survived; the shape did not.
> 
> It does now, because the money follows the people. Every cell of the family
> matrix is a Household object with its own savings, debt and credit line, and
> when the monthly rebuild moves households between cells - a child ages into
> a teen, five singles take a flatshare, a worker retires - their money moves
> with them. See followThePeople(). What used to be seven rows of arrays is
> sixty-eight objects this class holds, sums and strikes; the row getters the
> screens read are sums of the cells, and the tier is a view rather than the
> unit.
> 
> ... (17 more lines in the source)

**Uses:** [Household](Household.md) (188), [Equity](Equity.md) (14), [FamilyStructure](FamilyStructure.md) (12), [PayTier](PayTier.md) (11), [UnemployedHousehold](UnemployedHousehold.md) (7), [AgeBand](AgeBand.md) (7), [OutwardInvestment](OutwardInvestment.md) (5), [WorkingHousehold](WorkingHousehold.md) (4), [Bank](Bank.md) (4), [StudentHousehold](StudentHousehold.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [PrisonerHousehold](PrisonerHousehold.md) (3), [TaxPolicy](TaxPolicy.md) (2), [Restaurants](Restaurants.md) (2), [Exchange](Exchange.md) (2), [BondMarket](BondMarket.md) (2), [RetiredHousehold](RetiredHousehold.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (33):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [CrimeCheck](CrimeCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [Motoring](Motoring.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 97 | · the dials |
| 152 | AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT |
| 217 | ...AND WHAT IT SPENDS EATING OUT (2026-09-18) |
| 243 | AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) |
| 259 | AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) |
| 288 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 361 | BANKRUPTCY |
| 404 | · the cells |
| 459 | THE PRICE AT THE CLINIC DOOR (2026-09-19) |
| 539 | · the student loan's rate (2026-09-21) |
| 553 | · the bank's account fee (0.7.7) |
| 630 | · the month |
| 683 | EVERY HOUSEHOLD, OR ONE OF THEM |
| 760 | THE MONTH |
| 841 | · ...AND A MEAL OUT IS FOOD (2026-09-18) |
| 1090 | THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS |
| 1312 | A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) |
| 1498 | · EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16) |
| 1607 | THE CARS (2026-09-16) |
| 1683 | AND THEY BORROW FOR IT (2026-09-17) |
| 1768 | THE SECOND-HAND MARKET (2026-09-17) |
| 1904 | WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) |
| 2060 | · the second-hand market |
| 2125 | THE LUXURY COUNTER (2026-09-17) |
| 2226 | THE TABLE (2026-09-18) |
| 2511 | THE MARKET |
| 2580 | THE WORLD'S PAPER |
| 2598 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) |
| 2688 | · THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17) |
| 2785 | THE CITY'S PAPER, AT HOME (0.7.1) |
| 3026 | THE BUSINESSES' BONDS, AT HOME (0.7.12) |
| 3285 | · the people outside the families |
| 3339 | · what the bank is owed |
| 3415 | · reading |
| 3487 | THEFT (2026-09-11) |
| 3547 | THE OFFER |
| 3722 | · saving |
| 3887 | · THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `HouseholdBalance.ROWS` | `Household.ROWS` | One row per pay tier, the retired, and since 2026-09-11 the out of work, the students and the orphans - the same shape as HouseholdAccounts. |
| 95 | `HouseholdBalance.ROWS_BEFORE_OUTSIDE` | `Household.RETIRED_ROW + 1` | The rows before the people outside the families had books: the six tiers and the retired. |
| 109 | `HouseholdBalance.CREDIT_LIMIT_MONTHS` | `6` | How many months of take-home a household can owe before the lender stops. |
| 128 | `HouseholdBalance.RISK_SLOPE` | `.015` | Extra spread per month of income owed. |
| 131 | `HouseholdBalance.MAX_RATE` | `.36` | Nobody is charged more than this, however deep they are. |
| 150 | `HouseholdBalance.MARGINAL_PROPENSITY` | `.80` | How much of the money above subsistence a household spends. |
| 195 | `HouseholdBalance.WEALTH_SPENT_A_MONTH` | `.0033` | What share of its net worth a household spends in a month, over and above what it spends out of income. |
| 215 | `HouseholdBalance.LUXURY_SHARE_OF_SURPLUS` | `.5` | What share of the income a household does NOT spend on food goes over a luxury counter instead of into the bank. |
| 230 | `HouseholdBalance.MEAL_SHARE_OF_SURPLUS` | `.25` |  |
| 241 | `HouseholdBalance.MEAL_SHARE_OF_WEALTH` | `.5` | ...and the share of a FORTUNE'S monthly spend that goes on a table. |
| 257 | `HouseholdBalance.MOST_MEALS_EATEN_OUT` | `1 / 3.0` |  |
| 331 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provisional - Jerus's num... |
| 334 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 337 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 359 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 393 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 396 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 399 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 402 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1347 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1644 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1656 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1681 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1740 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1766 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1825 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1834 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 2082 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2628 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 2821 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point - so two and a half points of spread would take half an issue. |
| 2824 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and never more than this share of one issue: half, because a bond market with no bank in it is no longer the city's bank's market. |
| 3561 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3564 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 3780 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 3783 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 3786 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 3789 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 3792 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 3802 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 3812 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 3821 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 3833 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim on the households' o... |
| 3836 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_BONDS + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month's meals eaten out, t... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 412 | `private final Household[] cells` | Every meaningful cell of the family matrix, in one fixed order: the working shapes by declaration, each across the six tiers, then the two retired shapes. |
| 413 | `private final int[][] index` |  |
| 415 | `private final java.util.List<Household> view` |  |
| 416 | `private final int[] unemployedIndex` |  |
| 417 | `private int studentIndex` |  |
| 418 | `private final int[] orphanIndex` |  |
| 419 | `private int prisonerIndex` |  |
| 427 | `private ToDoubleFunction<Household> outsideCensus` | How many households are in each cell the family matrix does not hold - the out of work, the students, the orphans. |
| 438 | `private ToDoubleFunction<Household> rentShares` | The share of a door's rent one household of a cell pays: 1 alone in its own home, a fifth sharing, half doubled up, none with no door. |
| 453 | `private ToDoubleFunction<Household> outsideDependants` | Dependants living in each cell the family matrix does not hold. |
| 494 | `private double[] careBillByRow` | This month's treatment fees by row, as the households were billed them, or null for a caller that does not price care. |
| 497 | `private double[] careBillFullByRow` | ...and the treatment bill the same rows would have faced at full service. |
| 500 | `private double lastCareSkipped` | The care bills the households skipped this month, summed over the city: what they ate instead. |
| 546 | `private double studentLoanRate` |  |
| 564 | `private double accountFee` |  |
| 565 | `private ToDoubleFunction<Household> housedShares` |  |
| 632 | `private double lastWrittenOff` |  |
| 633 | `private double lastLeaving` |  |
| 634 | `private double lastEvicted` |  |
| 635 | `private double lastStudentDebtTakenAway` |  |
| 636 | `private double lastTakenAway` |  |
| 637 | `private double graduating` |  |
| 638 | `private double lastGraduated` |  |
| 639 | `private double lastDepositInterest` |  |
| 640 | `private double lastDelivered` |  |
| 641 | `private double plannedSpend` |  |
| 642 | `private double hungryPeople` |  |
| 643 | `private double totalPeople` |  |
| 1131 | `private int[] bondStockIds` | The bonds' ids in the order stocks() last listed them, one stock each from its named slot. |
| 1350 | `private int lastCellsFolded` | Cells the census left under EMPTY_CELL this month that still held something, and were emptied. |
| 1604 | `private final double[] lastSharesTakenAway` | Shares of each company that left the city with their holders this month. |
| 1837 | `private final double[] carsToReplace` | What died this month, per cell, waiting to be replaced. |
| 1840 | `private double lastCarsTakenAway` | ...and the cars whose owners left the city. |
| 2329 | `private double lastMealsBought, lastMealSpend, lastMealsEaten` |  |
| 2340 | `private double lastLuxuriesBought, lastLuxurySpend` |  |
| 2453 | `private double lastUsedOffered, lastUsedTraded, lastUsedSpend, lastUsedFinanced, lastUsedPrice, lastUsedNew...` |  |
| 2491 | `private double lastUsedLoanFees` |  |
| 2493 | `private double lastCarsBought` |  |
| 2494 | `private double lastCarsFinanced` |  |
| 2508 | `private double lastCarLoanFees` |  |
| 2519 | `private Exchange exchange` |  |
| 2520 | `private Equity register` |  |
| 2521 | `private Bank bank` |  |
| 2522 | `private Household.Liquidity liquidity` |  |
| 2631 | `private double minMove` | The same floor in today's money. |
| 2657 | `private double localPerUsd` | Local currency per dollar, this month: what the paper abroad is worth here. |
| 2666 | `private double lastAbroadTakenAway` | Dollars the households that left this month took with them. |
| 2836 | `private PaperDesk paperDesk` |  |
| 2851 | `private double paperRatio` | What a dollar of face of the households' paper is worth this month: the households' book at the curve over its face (DebtManager .householdBookRatio()), struck once a month by Game before the households' strike and SA... |
| 2868 | `private double spendFactor` | The month's spend factor (0.7.3): what share of its spending above subsistence every household plans, struck by Game once a month on the real deposit rate (Game.spendFactor()) before the households plan - see the bann... |
| 3041 | `private BondMarket bondMarket` |  |
| 3054 | `private double bondRatio` | What a dollar of face of the households' bonds is worth this month: every cell's holdings at the bonds' value over their face, struck by the market at its step and SAVED, because the plan reads it and the load path mu... |
| 3063 | `private double lastBondsTakenAway` | The face that left the city this month with its holders, every bond together. |
| 3103 | `private java.util.Map<String, Household> cellIndex` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 4026 | **type** `public class HouseholdBalance` | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. |

### the dials (lines 97-151)

### AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT (lines 152-216)

### ...AND WHAT IT SPENDS EATING OUT (2026-09-18) (lines 217-242)

### AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) (lines 243-258)

### AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) (lines 259-287)

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 288-360)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 5 | `public static double spendFactor(double realDepositRate)` | The share of its spending above subsistence a household keeps at this real deposit rate (annual, a fraction): 1 at zero, less when saving pays, more when it costs, between SPEND_FLOOR and SPEND_CEILING. |

### BANKRUPTCY (lines 361-403)

### the cells (lines 404-458)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 3 | `public void setOutsideCensus(ToDoubleFunction<Household> census)` |  |
| 440 | 3 | `public void setRentShares(ToDoubleFunction<Household> shares)` |  |
| 455 | 3 | `public void setOutsideDependants(ToDoubleFunction<Household> kin)` |  |

### THE PRICE AT THE CLINIC DOOR (2026-09-19) (lines 459-538)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 4 | `public void setCareBills(double[] treatmentBilled, double[] treatmentAtFullService)` | Hands the strike the month's treatment bills, by row: what the households were billed (HouseholdAccounts.getRowCareBilled) and what the same rows would have been billed at full service (getRowCareFull). |
| 518 | 10 | `public double carePaidShare(int row)` | Of a row's people, the share who paid for care at the last strike - people-weighted over its cells, 1 for a row with nobody in it. |
| 530 | 5 | `public double[] carePaidShares()` | The same, for every row at once, in row order. |
| 537 | 1 | `public double getCareSkipped()` | What the city's households skipped of their care bills this month, in money - and ate instead. |

### the student loan's rate (2026-09-21) (lines 539-552)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 3 | `public void setStudentLoanRate(double annual)` | Sets the annual rate the graduates are charged this month. |

### the bank's account fee (0.7.7) (lines 553-629)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 1 | `public void setAccountFee(double perHousehold)` |  |
| 568 | 1 | `public double getAccountFee()` |  |
| 580 | 7 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | THE BANK'S CAPITAL RULE FOR THE MONTH (0.7.8), from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(), set by Game at the top of the month before any family borrows: each cell may owe at most what it owes ... |
| 589 | 1 | `public double lossAllowance()` | What the bank sets aside against the families' debt (0.7.8), summed cell by cell - see Household.lossAllowance(). |
| 592 | 1 | `public double debtInTrouble()` | ...and how much of their debt is in cells that are in trouble. |
| 594 | 3 | `public void setHousedShares(ToDoubleFunction<Household> shares)` |  |
| 599 | 4 | `private double accountFeeFor(Household c, double households)` | The fee one household of this cell is charged, when the cell holds this many: nothing in a cell too empty to strike, which is paid nothing and pays nothing. |
| 611 | 9 | `public double[] accountFeesByRow(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | What each row's households will be charged in account fees this month, on this census: the figure the household books show on their own line (HouseholdAccounts.setAccountFees()), struck BEFORE the cells settle - the b... |
| 622 | 1 | `public double totalAccountFees()` | What the households paid the bank in account fees this month: the sum the bank takes. |
| 625 | 1 | `public double totalLoanFees()` | ...and the loan fees the bank added to what they owe on the credit they drew this month. |
| 628 | 1 | `public double getStudentLoanRate()` | The annual rate the cells were last told. |

### the month (lines 630-682)

| line | len | member | says |
|---:|---:|---|---|
| 645 | 37 | `public HouseholdBalance()` |  |

### EVERY HOUSEHOLD, OR ONE OF THEM (lines 683-759)

| line | len | member | says |
|---:|---:|---|---|
| 691 | 4 | `public Household cell(FamilyStructure shape, PayTier tier)` | The cell for this shape at this tier. |
| 697 | 6 | `public Household cell(FamilyStructure shape)` | The cell for a retired shape, which has no tier. |
| 705 | 3 | `public UnemployedHousehold unemployed(UnemployedHousehold.Status status)` | The out-of-work cell in this situation. |
| 710 | 1 | `public StudentHousehold students()` | The students' cell. |
| 713 | 4 | `public OrphanHousehold orphans(AgeBand band)` | The orphans of a child band, or null for a band that has none. |
| 719 | 1 | `public PrisonerHousehold prisoners()` | The prisoners' ledger. |
| 722 | 1 | `public java.util.List<Household> cells()` | Every cell, in the fixed order. |
| 724 | 1 | `public int cellCount()` |  |
| 726 | 3 | `public void forEach(Consumer<Household> action)` |  |
| 731 | 5 | `public double sum(ToDoubleFunction<Household> figure)` | Adds a figure up across every cell. |
| 738 | 5 | `public double sumRow(int row, ToDoubleFunction<Household> figure)` | ...or across one row: a tier's cells, or the retired. |
| 745 | 3 | `public double rowHouseholds(int row)` | Households in the row: the denominator of every per-household row figure. |
| 750 | 9 | `private double perHousehold(int row, ToDoubleFunction<Household> perCell)` | A row total, per household of the row. |

### THE MONTH (lines 760-1089)

| line | len | member | says |
|---:|---:|---|---|
| 790 | 230 | `public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, ...` | months owed sit on (Bank.householdRate() since 0.7.7; the city's own borrowing rate before it) |
| 1026 | 5 | `private static double[] padRows(double[] rows)` | A seven-row array - the tiers and the retired, from a caller written before the people outside the families had rows - padded with empty rows. |
| 1033 | 16 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Who lives in each cell now, in cell order. |
| 1060 | 13 | `private double[] splitIncome(double[] fresh, double[] rowTotal)` | A row's take-home, per household of each of its cells. |
| 1075 | 14 | `private double[] splitByPeople(double[] fresh, double[] rowTotal)` | A row's fees, per household of each of its cells, by the people in them. |

### THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS (lines 1090-1311)

| line | len | member | says |
|---:|---:|---|---|
| 1134 | 4 | **type** `private interface Stock` | One stock a household carries, for followThePeople() to move. |
| 1135 | 1 | `double get(Household c)` _(in HouseholdBalance.Stock)_ |  |
| 1136 | 1 | `void set(Household c, double perHousehold)` _(in HouseholdBalance.Stock)_ |  |
| 1146 | 80 | `private java.util.List<Stock> stocks(int[] named)` | Every stock a household carries, in the order followThePeople() moves them and foldEmptyCells() folds them. |
| 1237 | 74 | `private void followThePeople(double[] fresh, double[] buffer)` | Moves every stock with the people, then sets every cell's count. |

### A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) (lines 1312-1606)

| line | len | member | says |
|---:|---:|---|---|
| 1358 | 1 | `int foldEmptyCells()` | Empties every cell under EMPTY_CELL into the cells that are not. |
| 1360 | 22 | `private int foldEmptyCells(java.util.List<Stock> stocks)` |  |
| 1390 | 23 | `private boolean spread(Stock s, double amount, int group)` | One stock's amount onto the cells that are not empty, in one stock group (or the whole city, for -1): pro rata to what each holds of it, or by households when none holds any. |
| 1415 | 4 | `boolean holdsAnything(Household c)` | True when the cell holds any stock at all, which an empty cell must not. |
| 1421 | 1 | `public int getCellsFolded()` | Cells emptied this month that still held something when the census left them under EMPTY_CELL. |
| 1437 | 27 | `private double[] carryGraduatesLoans(double[] before)` | The graduates' student loans, handed to the working families before the census moves anything. |
| 1477 | 125 | `private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival)` | One stock through the pool: released by the cells that shrank at their own average, claimed by the cells that grew - within the row first, then across the city - and what nobody claimed returned. |
| 1605 | 1 | `public double getSharesTakenAway(int company)` |  |

### THE CARS (2026-09-16) (lines 1607-1682)

### AND THEY BORROW FOR IT (2026-09-17) (lines 1683-1767)

### THE SECOND-HAND MARKET (2026-09-17) (lines 1768-1903)

| line | len | member | says |
|---:|---:|---|---|
| 1842 | 1 | `public double getCarsTakenAway()` |  |
| 1845 | 1 | `public double totalCars()` | Every car in the city. |
| 1848 | 4 | `public double carsPerHousehold()` | Cars per household, 0 to 1 - what the road reads. |
| 1863 | 12 | `public double wearOutCars()` | Fifteen years on, every car in the city is scrapped. |
| 1897 | 6 | `public double carsWanted(double price, double ceiling)` | What the households would buy this month at this price, before the market says how many there are. |

### WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) (lines 1904-2059)

| line | len | member | says |
|---:|---:|---|---|
| 1936 | 49 | `private double wantOf(int i, double price, double ceiling)` | What one cell would buy this month, in whole cars. |
| 1999 | 60 | `public double takeCars(double units, double price, double ceiling)` | Hands out the cars the market actually had, pro rata over who wanted them, and takes the money out of savings. |

### the second-hand market (lines 2060-2124)

| line | len | member | says |
|---:|---:|---|---|
| 2111 | 13 | `private double offerOf(int i, double floorPrice)` | What one cell would put up for sale this month, in whole cars. |

### THE LUXURY COUNTER (2026-09-17) (lines 2125-2225)

| line | len | member | says |
|---:|---:|---|---|
| 2152 | 4 | `public double luxuriesWanted(double price)` | What the city's households would buy at this price, in whole pieces. |
| 2158 | 5 | `private double luxuryBudget()` | What the city's households would put over a counter, in money. |
| 2174 | 7 | `private double luxuryBudgetOf(Household c)` | What one cell would buy, in whole pieces. |
| 2195 | 30 | `public double takeLuxuries(double units, double price)` | Hands out the pieces the shops actually had, pro rata over who wanted them, and takes the money out of savings. |

### THE TABLE (2026-09-18) (lines 2226-2510)

| line | len | member | says |
|---:|---:|---|---|
| 2244 | 16 | `public double mealsWanted(double price)` | What the city's households would eat out at this price, in whole meals. |
| 2262 | 5 | `private double mealBudget()` | What the city's households would put on a table, in money. |
| 2281 | 7 | `private double mealBudgetOf(Household c)` | What one cell would spend eating out. |
| 2300 | 28 | `public double takeMeals(double meals, double price)` | Hands out the meals the kitchens actually served, pro rata over who wanted them, takes the money out of savings and REMEMBERS THE MEALS. |
| 2332 | 1 | `public double getMealsEaten()` | Meals the city ate out in the month the hunger measure just read. |
| 2335 | 1 | `public double getMealsBought()` | Meals the households ate out this month. |
| 2338 | 1 | `public double getMealSpend()` | ...and what they paid for them. |
| 2343 | 1 | `public double getLuxuriesBought()` | Pieces the households took this month. |
| 2346 | 1 | `public double getLuxurySpend()` | ...and what they paid for them. |
| 2349 | 1 | `public double getLuxuryWant()` | What every household in the city would like to put over a counter this month. |
| 2352 | 5 | `public double carsOffered(double floorPrice)` | What the city's households would put up for sale at this floor. |
| 2380 | 72 | `public double clearUsedCars(double newPrice, double ceiling)` | Clears the month's second-hand market: strikes a price, moves the cars that find a buyer, and pays the households that sold them. |
| 2457 | 1 | `public double getUsedCarsOffered()` | Cars put up for sale this month, whether or not anybody took them. |
| 2460 | 1 | `public double getUsedCarsTraded()` | ...and the ones that found a buyer. |
| 2463 | 1 | `public double getUsedCarPrice()` | What they went for. |
| 2476 | 3 | `public double getUsedCarShare()` | ...as a share of what a NEW one cost the month it was struck, which is the only honest denominator and is between USED_CAR_FLOOR and USED_CAR_CEILING by construction. |
| 2481 | 1 | `public double getUsedCarSpend()` | What the buyers paid, all in. |
| 2487 | 1 | `public double getUsedCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month. |
| 2490 | 1 | `public double getUsedCarLoanFees()` | ...and the bank's fee on that, added to what the buyers owe (0.7.7). |
| 2497 | 1 | `public double getCarsBought()` | Cars the households actually took this month. |
| 2504 | 1 | `public double getCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month or the money audit sees a pool fall for no reason. |
| 2507 | 1 | `public double getCarLoanFees()` | ...and the bank's fee on it, added to what the buyers owe rather than paid (0.7.7). |

### THE MARKET (lines 2511-2579)

| line | len | member | says |
|---:|---:|---|---|
| 2524 | 6 | `public void setMarket(Exchange exchange, Equity register, Bank bank)` |  |
| 2536 | 15 | `private void rebuildLiquidity()` | The waterfall's two sales, in one object: the city's paper to the bank's desk first (0.7.1), then shares on the exchange. |
| 2558 | 1 | `Household[] cellsForMarket()` | The cells, for the exchange and the bond market to post their orders from (0.7.12 round 2: each household type is its own participant). |
| 2561 | 5 | `public void splitShares(int company, double k)` | A split or consolidation: every household's count of the company by the factor. |
| 2568 | 4 | `public double marketValueOfShares()` | What the households' shares are worth at the exchange's price - the last trade on each book, or fair value before one. |
| 2574 | 5 | `public double totalSold()` | What the households raised this month selling shares to cover the shop. |

### THE WORLD'S PAPER (lines 2580-2597)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) (lines 2598-2784)

| line | len | member | says |
|---:|---:|---|---|
| 2634 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |
| 2654 | 1 | `private boolean owes(Household c)` | WHETHER A CELL OWES ANYTHING, READ AT THE SAME FLOOR (0.7.12 round 6). |
| 2659 | 3 | `public void setExchangeRate(double localPerUsd)` |  |
| 2663 | 1 | `public double getExchangeRate()` |  |
| 2680 | 70 | `public void investAbroad(double depositRate, double worldRate, double rate)` | Every household decides where to keep its idle money: what is past the cushion goes abroad towards the target share, a little a month; what is abroad comes home when the target falls. |
| 2752 | 5 | `public double totalAbroadUsd()` | Dollars every household holds abroad. |
| 2759 | 1 | `public double totalAbroadValue()` | ...worth this much at home, at the month's rate. |
| 2762 | 5 | `public double getSentAbroad()` | Sent abroad this month, all households, local money. |
| 2769 | 5 | `public double getBroughtHome()` | Brought home this month - to keep to the target or to eat - all households, local money. |
| 2776 | 5 | `public double getForeignInterest()` | What the world paid the households this month, rolled abroad. |
| 2783 | 1 | `public double getAbroadTakenAway()` | Dollars that left with the households that left this month. |

### THE CITY'S PAPER, AT HOME (0.7.1) (lines 2785-3025)

| line | len | member | says |
|---:|---:|---|---|
| 2831 | 4 | **type** `public interface PaperDesk` | Where the households' paper is sold, for what, and what it does to the paper on the other side. |
| 2833 | 1 | `void buy(double face, double cash)` _(in HouseholdBalance.PaperDesk)_ | The desk buys this much face of the households' paper for this much cash. |
| 2838 | 4 | `public void setPaperDesk(PaperDesk desk)` |  |
| 2853 | 3 | `public void setPaperRatio(double ratio)` |  |
| 2857 | 1 | `public double getPaperRatio()` |  |
| 2870 | 3 | `public void setSpendFactor(double factor)` |  |
| 2875 | 1 | `public double getSpendFactor()` | The factor the last plan was struck at. |
| 2878 | 1 | `public double totalPaper()` | Every cell's paper, at face - DebtManager.householdPrincipal() from the other side. |
| 2881 | 1 | `public double marketValueOfPaper()` | ...at this month's market value. |
| 2884 | 5 | `public double totalPaperSold()` | Sold to the desk this month, all households, in cash. |
| 2891 | 5 | `public double totalPaperIncome()` | Coupons and principal the city paid the households on their paper this month. |
| 2897 | 3 | `private boolean mayBuyPaper(Household c)` |  |
| 2901 | 3 | `private double spareFor(Household c)` |  |
| 2906 | 4 | `public static double paperShareAt(double yield, double depositRate)` | The share of an issue yielding this much the households would take, before asking whether they can pay. |
| 2912 | 5 | `public double spareForPaper()` | What every eligible household could put into paper: savings past the cushion, in total. |
| 2924 | 17 | `public double buyAtIssue(double cash, double facePerCash)` | The households buy at issue: this much cash in total, from each eligible cell pro rata to what is past its cushion, for this much face per dollar. |
| 2943 | 3 | `private double paperWeight(Household c, double total)` | Pro rata to the paper held: what a dollar of face in the households' book is spread as, cell by cell. |
| 2948 | 13 | `public void creditPaperCoupon(double total)` | A coupon on the households' share, into savings and the month's investment income, pro rata to the paper. |
| 2963 | 13 | `public void creditPaperPrincipal(double face)` | Principal on their share, into savings with the paper down by the same, pro rata. |
| 2978 | 10 | `public void creditPaperBuyback(double face, double price)` | A bond bought back: this much of their face off the paper, this much of the price into savings, pro rata. |
| 2990 | 9 | `private double sellPaperToDesk(Household c, double needPer)` | One cell sells to the desk for up to needPer a household. |
| 3009 | 16 | `public double sellPaperForSpread(double bookYield, double depositRate)` | THE SPREAD HAS GONE: when the households' paper yields no more than the bank pays savers, the target for it is nothing, and they sell a little a month - HOME_SPEED of it, the pace the dollars abroad come home at - int... |

### THE BUSINESSES' BONDS, AT HOME (0.7.12) (lines 3026-3284)

| line | len | member | says |
|---:|---:|---|---|
| 3043 | 4 | `public void setBondMarket(BondMarket market)` |  |
| 3056 | 3 | `public void setBondRatio(double ratio)` |  |
| 3060 | 1 | `public double getBondRatio()` |  |
| 3064 | 1 | `public double getBondsTakenAway()` |  |
| 3067 | 1 | `public double totalBonds()` | Every cell's bonds, at face: the households' face in every bond. |
| 3070 | 1 | `public double marketValueOfBonds()` | ...at this month's value. |
| 3073 | 5 | `public double totalBondsSold()` | Sold this month, in cash, and the coupons and principal received. |
| 3079 | 5 | `public double totalBondIncome()` |  |
| 3086 | 1 | `public double getBonds(int row)` | One row's bonds, per household of the row. |
| 3089 | 5 | `public double bondFaceHeld(int id)` | Every cell's face in one bond, together. |
| 3096 | 7 | `public Household cellByKey(String key)` | The cell with this key, or null - from an index built once, because the markets ask on every fill. |
| 3106 | 1 | `public double bondSpare()` | What every eligible household could put into bonds: savings past the cushion - the city's paper's rule. |
| 3109 | 1 | `public double bondSpare(Household c)` | ...one cell's, every household of it: nothing for a cell that may not buy (the city's paper's eligibility). |
| 3118 | 19 | `public double payForBonds(int id, double cash, double face)` | A new issue's households' part, at issue: this much cash out of every eligible cell's savings past its cushion, pro rata, for this much face of the bond, which each cell holds in the same proportion. |
| 3146 | 26 | `public void creditBondCoupons(java.util.Map<String, Double> dueByCell)` | THE MONTH'S COUPONS, to the cells that held the bonds at the record date (BondMarket.strikeCoupons()): into savings and the month's investment income. |
| 3174 | 13 | `public double creditBondPrincipal(int id)` | One bond's principal at maturity: every cell paid its own face of it, into savings, and the bond gone from it. |
| 3194 | 14 | `public void writeDownBonds(java.util.Collection<Integer> ids, double keep)` | A default on one issuer's bonds together: every cell's face of each down to this share, and its total recounted once - a slice writes an issuer's bonds down every month it is past the curve's floor, and a recount per ... |
| 3210 | 12 | `public double writeDownBonds(int id, double keep)` | A default on one bond: every cell's face of it down to this share. |
| 3226 | 10 | `public java.util.Map<String, java.util.Map<String, Double>> bondsByCellToSave()` | Every cell's bonds, per household, by the cell's key and the bond's id: the save's (0.7.12 round 2). |
| 3242 | 16 | `public void restoreBondsByCell(java.util.Map<String, java.util.Map<String, Double>> saved)` | ...back, by name: a cell this build does not have is dropped, as the cell arrays' rule is, and a bond id that does not parse is skipped. |
| 3269 | 15 | `public void claimPooledBonds(java.util.Map<Integer, Double> poolById)` | A SAVE FROM ROUND 1 held the households' bonds as one pool, each cell with a claim on it at face (Household.bonds): the pool is handed to the cells by those claims - every bond's households' face split across the cell... |

### the people outside the families (lines 3285-3338)

| line | len | member | says |
|---:|---:|---|---|
| 3288 | 1 | `public double getEvicted()` | Out-of-work households that lost their home this month. |
| 3291 | 1 | `public double totalStudentBorrowed()` | Student loans drawn this month, all students - the treasury's money out. |
| 3294 | 1 | `public double totalStudentRepaid()` | ...and repaid by graduates, the treasury's money back: principal, which is what the journal's line carries. |
| 3297 | 1 | `public double totalStudentInterest()` | ...and the interest the graduates paid on top of it this month: the treasury's revenue. |
| 3300 | 3 | `public double studentInterestAt(double annualRate)` | What a month's interest would come to at an annual rate, on the balances that are charged it - for a screen previewing a rate. |
| 3305 | 1 | `public double totalStudentDebt()` | What every household owes the treasury in student loans. |
| 3308 | 3 | `public double totalGraduateDebt()` | ...and the part of it that is in repayment: the graduates' balances, which the rate is charged on. |
| 3313 | 1 | `public double getStudentDebtTakenAway()` | Student loans that left the city with graduates who left. |
| 3334 | 1 | `public void setGraduates(double people)` | THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS. |
| 3337 | 1 | `public double getLastGraduated()` | Students who left the student body with what they carried, at the last month's census. |

### what the bank is owed (lines 3339-3414)

| line | len | member | says |
|---:|---:|---|---|
| 3342 | 1 | `public double getWrittenOff()` | Written off this month, which is the bank's loss. |
| 3345 | 1 | `public double getTakenAway()` | Savings that left the city with the households that left. |
| 3348 | 1 | `public double getBankrupt(int row)` | Households discharged this month, and the share of them leaving the city. |
| 3349 | 1 | `public double getLeavingCity()` |  |
| 3352 | 1 | `public boolean isLockedOut(int row)` | True when any cell of the row the bank has stopped lending to is still locked out. |
| 3355 | 7 | `public int getLockout(int row)` | The longest lockout standing in the row. |
| 3364 | 1 | `public double bookOwed()` | Everything the households owe the bank. |
| 3382 | 32 | `public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, doub...` | The plan alone, without settling a month. |

### reading (lines 3415-3486)

| line | len | member | says |
|---:|---:|---|---|
| 3418 | 1 | `public double getSpendingCapacity()` | What the shops can sell next month, in money. |
| 3421 | 1 | `public double getWantedSpend()` | What they would spend if money were no object - the demand behind the cap. |
| 3424 | 1 | `public double getSubsistence(int row)` | One basket a head, per household of the row: what going short is measured against. |
| 3434 | 11 | `public double[] plannedShare()` | The share of each row's planned spend, for splitting what retail actually sold back across the rows. |
| 3447 | 3 | `public double getHungerRate()` | Share of the city going short of food, 0-1. |
| 3451 | 1 | `public double getHungryPeople()` |  |
| 3462 | 1 | `public double getDeliveredShare()` | The share of what households planned to buy that the shops could hand over - and therefore which of the two hungers is biting. |
| 3475 | 11 | `public void creditDepositInterest(double total)` | Interest the bank paid on what these households have saved. |

### THEFT (2026-09-11) (lines 3487-3546)

| line | len | member | says |
|---:|---:|---|---|
| 3506 | 16 | `public double takeFromSavings(double total)` | Takes up to `total` from the households' savings, each cell in proportion to what it has saved. |
| 3529 | 14 | `public double creditByWeight(double total, double[] weight)` | Credits `total` to the cells in proportion to a weight per cell, in the cells' order - what the offenders' households took home. |
| 3545 | 1 | `public double getDepositInterest()` | What the bank paid the city's savers this month. |

### THE OFFER (lines 3547-3721)

| line | len | member | says |
|---:|---:|---|---|
| 3579 | 28 | `public double subscribe(int company, double offered, double price)` | Puts an offering to every household, and takes up what they will buy. |
| 3618 | 10 | `public void grantFounders(int company, double shares)` | Hands out a company's founding shares to the people who founded it. |
| 3634 | 15 | `public double creditDividend(int company, double perShare)` | Pays every household its dividend, straight into its savings. |
| 3651 | 5 | `public double sharesHeld(int company)` | Shares of this company the city's households hold between them. |
| 3658 | 5 | `public double totalDividends()` | What the households were paid in dividends this month. |
| 3666 | 1 | `public double getSavings(int row)` | ---- the row, per household of it: what the screens and the fixtures read ---- |
| 3669 | 1 | `public double getHouseholds(int row)` | Households this row was struck for - the multiplier on every per-row figure. |
| 3670 | 1 | `public double getDebt(int row)` |  |
| 3671 | 1 | `public double getAfterFixed(int row)` |  |
| 3672 | 1 | `public double getInterest(int row)` |  |
| 3673 | 1 | `public double getDrawn(int row)` |  |
| 3676 | 1 | `public double getUnfunded(int row)` | What this row wanted, could not fund, and did not get. |
| 3679 | 4 | `public boolean isCutOff(int row)` | True when the bank has stopped lending to any cell of this row - ceiling or lockout. |
| 3683 | 1 | `public double getBorrowed(int row)` |  |
| 3684 | 1 | `public double getRepaid(int row)` |  |
| 3685 | 1 | `public double getBanked(int row)` |  |
| 3686 | 1 | `public double getWant(int row)` |  |
| 3687 | 1 | `public double getPlanned(int row)` |  |
| 3688 | 1 | `public double getRate(int row)` |  |
| 3691 | 4 | `public boolean isGoingShort(int row)` | True when this row is buying less food than it wants. |
| 3697 | 1 | `public double totalSavings()` | City totals, for the headline lines on the screen. |
| 3698 | 1 | `public double totalDebt()` |  |
| 3699 | 1 | `public double totalInterest()` |  |
| 3708 | 4 | `public double averageRate()` | What the families' credit lines cost them this month on average: each cell's rate weighted by what it owes - the bank's household rate plus RISK_SLOPE for every month of income owed, capped at MAX_RATE (Household.sett... |
| 3714 | 1 | `public double totalBorrowed()` | New lending to families this month - the bank's money out the door. |
| 3717 | 1 | `public double totalRepaid()` | ...and what came back. |
| 3720 | 1 | `public double totalNetWorth()` | What every household in the city has, less what it owes. |

### saving (lines 3722-4111)

| line | len | member | says |
|---:|---:|---|---|
| 3742 | 36 | `public double[] toSaveArray()` | The row array an older build reads: ROWS*8+3, per household of the row. |
| 3839 | 5 | `public String[] cellKeys()` | The name of every cell, in the order toCellSaveArray() writes them. |
| 3846 | 27 | `public double[] toCellSaveArray()` | CELL_SLOTS per cell, in cellKeys() order, then the three city figures. |
| 3885 | 121 | `public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies)` | Puts the cells back, by name. |
| 4020 | 43 | `public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Puts a ROW array back, seeding every cell of the row with the row's position: the save from a build that kept the stocks per tier. |
| 4065 | 1 | `public void restore(double[] saved)` | The row array alone, with no census: the cells wait for the plan to count them. |
| 4067 | 24 | `public void reset()` |  |
| 4098 | 12 | `public void redenominate(double scale)` | The households' stocks and this month's working, in the new unit. |


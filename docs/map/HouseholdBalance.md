# HouseholdBalance.java - 3,760 lines · 176 methods · 43 constants · model

`ham/citybuildersim/HouseholdBalance.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Household](Household.md) (152), [Equity](Equity.md) (16), [FamilyStructure](FamilyStructure.md) (11), [PayTier](PayTier.md) (10), [UnemployedHousehold](UnemployedHousehold.md) (7), [AgeBand](AgeBand.md) (7), [OutwardInvestment](OutwardInvestment.md) (5), [WorkingHousehold](WorkingHousehold.md) (4), [Exchange](Exchange.md) (4), [Bank](Bank.md) (4), [StudentHousehold](StudentHousehold.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [PrisonerHousehold](PrisonerHousehold.md) (3), [TaxPolicy](TaxPolicy.md) (2), [Restaurants](Restaurants.md) (2), [RetiredHousehold](RetiredHousehold.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (29):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [CrimeCheck](CrimeCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [Motoring](Motoring.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 97 | · the dials |
| 153 | AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT |
| 218 | ...AND WHAT IT SPENDS EATING OUT (2026-09-18) |
| 244 | AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) |
| 260 | AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) |
| 289 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 362 | BANKRUPTCY |
| 405 | · the cells |
| 460 | THE PRICE AT THE CLINIC DOOR (2026-09-19) |
| 540 | · the student loan's rate (2026-09-21) |
| 557 | · the month |
| 610 | EVERY HOUSEHOLD, OR ONE OF THEM |
| 687 | THE MONTH |
| 766 | · ...AND A MEAL OUT IS FOOD (2026-09-18) |
| 1011 | THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS |
| 1188 | A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) |
| 1371 | · EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16) |
| 1480 | THE CARS (2026-09-16) |
| 1556 | AND THEY BORROW FOR IT (2026-09-17) |
| 1641 | THE SECOND-HAND MARKET (2026-09-17) |
| 1777 | WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) |
| 1927 | · the second-hand market |
| 1992 | THE LUXURY COUNTER (2026-09-17) |
| 2093 | THE TABLE (2026-09-18) |
| 2368 | THE MARKET |
| 2539 | THE WORLD'S PAPER |
| 2557 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) |
| 2629 | · THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17) |
| 2726 | THE CITY'S PAPER, AT HOME (0.7.1) |
| 2967 | · the people outside the families |
| 3021 | · what the bank is owed |
| 3096 | · reading |
| 3168 | THEFT (2026-09-11) |
| 3228 | THE OFFER |
| 3391 | · saving |
| 3543 | · THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `HouseholdBalance.ROWS` | `Household.ROWS` | One row per pay tier, the retired, and since 2026-09-11 the out of work, the students and the orphans - the same shape as HouseholdAccounts. |
| 95 | `HouseholdBalance.ROWS_BEFORE_OUTSIDE` | `Household.RETIRED_ROW + 1` | The rows before the people outside the families had books: the six tiers and the retired. |
| 109 | `HouseholdBalance.CREDIT_LIMIT_MONTHS` | `6` | How many months of take-home a household can owe before the lender stops. |
| 118 | `HouseholdBalance.BASE_SPREAD` | `.03` | What a household pays over the risk-free rate, before any risk premium. |
| 129 | `HouseholdBalance.RISK_SLOPE` | `.015` | Extra spread per month of income owed. |
| 132 | `HouseholdBalance.MAX_RATE` | `.36` | Nobody is charged more than this, however deep they are. |
| 151 | `HouseholdBalance.MARGINAL_PROPENSITY` | `.80` | How much of the money above subsistence a household spends. |
| 196 | `HouseholdBalance.WEALTH_SPENT_A_MONTH` | `.0033` | What share of its net worth a household spends in a month, over and above what it spends out of income. |
| 216 | `HouseholdBalance.LUXURY_SHARE_OF_SURPLUS` | `.5` | What share of the income a household does NOT spend on food goes over a luxury counter instead of into the bank. |
| 231 | `HouseholdBalance.MEAL_SHARE_OF_SURPLUS` | `.25` |  |
| 242 | `HouseholdBalance.MEAL_SHARE_OF_WEALTH` | `.5` | ...and the share of a FORTUNE'S monthly spend that goes on a table. |
| 258 | `HouseholdBalance.MOST_MEALS_EATEN_OUT` | `1 / 3.0` |  |
| 332 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provisional - Jerus's num... |
| 335 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 338 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 360 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 394 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 397 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 400 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 403 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1223 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1517 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1529 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1554 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1613 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1639 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1698 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1707 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 1949 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2587 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 2762 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point - so two and a half points of spread would take half an issue. |
| 2765 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and never more than this share of one issue: half, because a bond market with no bank in it is no longer the city's bank's market. |
| 3242 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3245 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 3449 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 3452 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 3455 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 3458 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 3461 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 3471 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 3481 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 3490 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 3493 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_PAPER + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month's meals eaten out, t... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 413 | `private final Household[] cells` | Every meaningful cell of the family matrix, in one fixed order: the working shapes by declaration, each across the six tiers, then the two retired shapes. |
| 414 | `private final int[][] index` |  |
| 416 | `private final java.util.List<Household> view` |  |
| 417 | `private final int[] unemployedIndex` |  |
| 418 | `private int studentIndex` |  |
| 419 | `private final int[] orphanIndex` |  |
| 420 | `private int prisonerIndex` |  |
| 428 | `private ToDoubleFunction<Household> outsideCensus` | How many households are in each cell the family matrix does not hold - the out of work, the students, the orphans. |
| 439 | `private ToDoubleFunction<Household> rentShares` | The share of a door's rent one household of a cell pays: 1 alone in its own home, a fifth sharing, half doubled up, none with no door. |
| 454 | `private ToDoubleFunction<Household> outsideDependants` | Dependants living in each cell the family matrix does not hold. |
| 495 | `private double[] careBillByRow` | This month's treatment fees by row, as the households were billed them, or null for a caller that does not price care. |
| 498 | `private double[] careBillFullByRow` | ...and the treatment bill the same rows would have faced at full service. |
| 501 | `private double lastCareSkipped` | The care bills the households skipped this month, summed over the city: what they ate instead. |
| 547 | `private double studentLoanRate` |  |
| 559 | `private double lastWrittenOff` |  |
| 560 | `private double lastLeaving` |  |
| 561 | `private double lastEvicted` |  |
| 562 | `private double lastStudentDebtTakenAway` |  |
| 563 | `private double lastTakenAway` |  |
| 564 | `private double graduating` |  |
| 565 | `private double lastGraduated` |  |
| 566 | `private double lastDepositInterest` |  |
| 567 | `private double lastDelivered` |  |
| 568 | `private double plannedSpend` |  |
| 569 | `private double hungryPeople` |  |
| 570 | `private double totalPeople` |  |
| 1226 | `private int lastCellsFolded` | Cells the census left under EMPTY_CELL this month that still held something, and were emptied. |
| 1477 | `private final double[] lastSharesTakenAway` | Shares of each company that left the city with their holders this month. |
| 1710 | `private final double[] carsToReplace` | What died this month, per cell, waiting to be replaced. |
| 1713 | `private double lastCarsTakenAway` | ...and the cars whose owners left the city. |
| 2196 | `private double lastMealsBought, lastMealSpend, lastMealsEaten` |  |
| 2207 | `private double lastLuxuriesBought, lastLuxurySpend` |  |
| 2318 | `private double lastUsedOffered, lastUsedTraded, lastUsedSpend, lastUsedFinanced, lastUsedPrice, lastUsedNew...` |  |
| 2354 | `private double lastCarsBought` |  |
| 2355 | `private double lastCarsFinanced` |  |
| 2376 | `private Exchange exchange` |  |
| 2377 | `private Equity register` |  |
| 2378 | `private Bank bank` |  |
| 2379 | `private Household.Liquidity liquidity` |  |
| 2590 | `private double minMove` | The same floor in today's money. |
| 2598 | `private double localPerUsd` | Local currency per dollar, this month: what the paper abroad is worth here. |
| 2607 | `private double lastAbroadTakenAway` | Dollars the households that left this month took with them. |
| 2777 | `private PaperDesk paperDesk` |  |
| 2792 | `private double paperRatio` | What a dollar of face of the households' paper is worth this month: the households' book at the curve over its face (DebtManager .householdBookRatio()), struck once a month by Game before the households' strike and SA... |
| 2809 | `private double spendFactor` | The month's spend factor (0.7.3): what share of its spending above subsistence every household plans, struck by Game once a month on the real deposit rate (Game.spendFactor()) before the households plan - see the bann... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 3675 | **type** `public class HouseholdBalance` | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. |

### the dials (lines 97-152)

### AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT (lines 153-217)

### ...AND WHAT IT SPENDS EATING OUT (2026-09-18) (lines 218-243)

### AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) (lines 244-259)

### AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) (lines 260-288)

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 289-361)

| line | len | member | says |
|---:|---:|---|---|
| 346 | 5 | `public static double spendFactor(double realDepositRate)` | The share of its spending above subsistence a household keeps at this real deposit rate (annual, a fraction): 1 at zero, less when saving pays, more when it costs, between SPEND_FLOOR and SPEND_CEILING. |

### BANKRUPTCY (lines 362-404)

### the cells (lines 405-459)

| line | len | member | says |
|---:|---:|---|---|
| 430 | 3 | `public void setOutsideCensus(ToDoubleFunction<Household> census)` |  |
| 441 | 3 | `public void setRentShares(ToDoubleFunction<Household> shares)` |  |
| 456 | 3 | `public void setOutsideDependants(ToDoubleFunction<Household> kin)` |  |

### THE PRICE AT THE CLINIC DOOR (2026-09-19) (lines 460-539)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 4 | `public void setCareBills(double[] treatmentBilled, double[] treatmentAtFullService)` | Hands the strike the month's treatment bills, by row: what the households were billed (HouseholdAccounts.getRowCareBilled) and what the same rows would have been billed at full service (getRowCareFull). |
| 519 | 10 | `public double carePaidShare(int row)` | Of a row's people, the share who paid for care at the last strike - people-weighted over its cells, 1 for a row with nobody in it. |
| 531 | 5 | `public double[] carePaidShares()` | The same, for every row at once, in row order. |
| 538 | 1 | `public double getCareSkipped()` | What the city's households skipped of their care bills this month, in money - and ate instead. |

### the student loan's rate (2026-09-21) (lines 540-556)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 3 | `public void setStudentLoanRate(double annual)` | Sets the annual rate the graduates are charged this month. |
| 555 | 1 | `public double getStudentLoanRate()` | The annual rate the cells were last told. |

### the month (lines 557-609)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 37 | `public HouseholdBalance()` |  |

### EVERY HOUSEHOLD, OR ONE OF THEM (lines 610-686)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 4 | `public Household cell(FamilyStructure shape, PayTier tier)` | The cell for this shape at this tier. |
| 624 | 6 | `public Household cell(FamilyStructure shape)` | The cell for a retired shape, which has no tier. |
| 632 | 3 | `public UnemployedHousehold unemployed(UnemployedHousehold.Status status)` | The out-of-work cell in this situation. |
| 637 | 1 | `public StudentHousehold students()` | The students' cell. |
| 640 | 4 | `public OrphanHousehold orphans(AgeBand band)` | The orphans of a child band, or null for a band that has none. |
| 646 | 1 | `public PrisonerHousehold prisoners()` | The prisoners' ledger. |
| 649 | 1 | `public java.util.List<Household> cells()` | Every cell, in the fixed order. |
| 651 | 1 | `public int cellCount()` |  |
| 653 | 3 | `public void forEach(Consumer<Household> action)` |  |
| 658 | 5 | `public double sum(ToDoubleFunction<Household> figure)` | Adds a figure up across every cell. |
| 665 | 5 | `public double sumRow(int row, ToDoubleFunction<Household> figure)` | ...or across one row: a tier's cells, or the retired. |
| 672 | 3 | `public double rowHouseholds(int row)` | Households in the row: the denominator of every per-household row figure. |
| 677 | 9 | `private double perHousehold(int row, ToDoubleFunction<Household> perCell)` | A row total, per household of the row. |

### THE MONTH (lines 687-1010)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 226 | `public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, ...` |  |
| 947 | 5 | `private static double[] padRows(double[] rows)` | A seven-row array - the tiers and the retired, from a caller written before the people outside the families had rows - padded with empty rows. |
| 954 | 16 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Who lives in each cell now, in cell order. |
| 981 | 13 | `private double[] splitIncome(double[] fresh, double[] rowTotal)` | A row's take-home, per household of each of its cells. |
| 996 | 14 | `private double[] splitByPeople(double[] fresh, double[] rowTotal)` | A row's fees, per household of each of its cells, by the people in them. |

### THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS (lines 1011-1187)

| line | len | member | says |
|---:|---:|---|---|
| 1052 | 4 | **type** `private interface Stock` | One stock a household carries, for followThePeople() to move. |
| 1053 | 1 | `double get(Household c)` _(in HouseholdBalance.Stock)_ |  |
| 1054 | 1 | `void set(Household c, double perHousehold)` _(in HouseholdBalance.Stock)_ |  |
| 1064 | 55 | `private java.util.List<Stock> stocks(int[] named)` | Every stock a household carries, in the order followThePeople() moves them and foldEmptyCells() folds them. |
| 1130 | 57 | `private void followThePeople(double[] fresh, double[] buffer)` | Moves every stock with the people, then sets every cell's count. |

### A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) (lines 1188-1479)

| line | len | member | says |
|---:|---:|---|---|
| 1234 | 1 | `int foldEmptyCells()` | Empties every cell under EMPTY_CELL into the cells that are not. |
| 1236 | 19 | `private int foldEmptyCells(java.util.List<Stock> stocks)` |  |
| 1263 | 23 | `private boolean spread(Stock s, double amount, int group)` | One stock's amount onto the cells that are not empty, in one stock group (or the whole city, for -1): pro rata to what each holds of it, or by households when none holds any. |
| 1288 | 4 | `boolean holdsAnything(Household c)` | True when the cell holds any stock at all, which an empty cell must not. |
| 1294 | 1 | `public int getCellsFolded()` | Cells emptied this month that still held something when the census left them under EMPTY_CELL. |
| 1310 | 27 | `private double[] carryGraduatesLoans(double[] before)` | The graduates' student loans, handed to the working families before the census moves anything. |
| 1350 | 125 | `private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival)` | One stock through the pool: released by the cells that shrank at their own average, claimed by the cells that grew - within the row first, then across the city - and what nobody claimed returned. |
| 1478 | 1 | `public double getSharesTakenAway(int company)` |  |

### THE CARS (2026-09-16) (lines 1480-1555)

### AND THEY BORROW FOR IT (2026-09-17) (lines 1556-1640)

### THE SECOND-HAND MARKET (2026-09-17) (lines 1641-1776)

| line | len | member | says |
|---:|---:|---|---|
| 1715 | 1 | `public double getCarsTakenAway()` |  |
| 1718 | 1 | `public double totalCars()` | Every car in the city. |
| 1721 | 4 | `public double carsPerHousehold()` | Cars per household, 0 to 1 - what the road reads. |
| 1736 | 12 | `public double wearOutCars()` | Fifteen years on, every car in the city is scrapped. |
| 1770 | 6 | `public double carsWanted(double price, double ceiling)` | What the households would buy this month at this price, before the market says how many there are. |

### WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) (lines 1777-1926)

| line | len | member | says |
|---:|---:|---|---|
| 1809 | 49 | `private double wantOf(int i, double price, double ceiling)` | What one cell would buy this month, in whole cars. |
| 1871 | 55 | `public double takeCars(double units, double price, double ceiling)` | Hands out the cars the market actually had, pro rata over who wanted them, and takes the money out of savings. |

### the second-hand market (lines 1927-1991)

| line | len | member | says |
|---:|---:|---|---|
| 1978 | 13 | `private double offerOf(int i, double floorPrice)` | What one cell would put up for sale this month, in whole cars. |

### THE LUXURY COUNTER (2026-09-17) (lines 1992-2092)

| line | len | member | says |
|---:|---:|---|---|
| 2019 | 4 | `public double luxuriesWanted(double price)` | What the city's households would buy at this price, in whole pieces. |
| 2025 | 5 | `private double luxuryBudget()` | What the city's households would put over a counter, in money. |
| 2041 | 7 | `private double luxuryBudgetOf(Household c)` | What one cell would buy, in whole pieces. |
| 2062 | 30 | `public double takeLuxuries(double units, double price)` | Hands out the pieces the shops actually had, pro rata over who wanted them, and takes the money out of savings. |

### THE TABLE (2026-09-18) (lines 2093-2367)

| line | len | member | says |
|---:|---:|---|---|
| 2111 | 16 | `public double mealsWanted(double price)` | What the city's households would eat out at this price, in whole meals. |
| 2129 | 5 | `private double mealBudget()` | What the city's households would put on a table, in money. |
| 2148 | 7 | `private double mealBudgetOf(Household c)` | What one cell would spend eating out. |
| 2167 | 28 | `public double takeMeals(double meals, double price)` | Hands out the meals the kitchens actually served, pro rata over who wanted them, takes the money out of savings and REMEMBERS THE MEALS. |
| 2199 | 1 | `public double getMealsEaten()` | Meals the city ate out in the month the hunger measure just read. |
| 2202 | 1 | `public double getMealsBought()` | Meals the households ate out this month. |
| 2205 | 1 | `public double getMealSpend()` | ...and what they paid for them. |
| 2210 | 1 | `public double getLuxuriesBought()` | Pieces the households took this month. |
| 2213 | 1 | `public double getLuxurySpend()` | ...and what they paid for them. |
| 2216 | 1 | `public double getLuxuryWant()` | What every household in the city would like to put over a counter this month. |
| 2219 | 5 | `public double carsOffered(double floorPrice)` | What the city's households would put up for sale at this floor. |
| 2247 | 70 | `public double clearUsedCars(double newPrice, double ceiling)` | Clears the month's second-hand market: strikes a price, moves the cars that find a buyer, and pays the households that sold them. |
| 2322 | 1 | `public double getUsedCarsOffered()` | Cars put up for sale this month, whether or not anybody took them. |
| 2325 | 1 | `public double getUsedCarsTraded()` | ...and the ones that found a buyer. |
| 2328 | 1 | `public double getUsedCarPrice()` | What they went for. |
| 2341 | 3 | `public double getUsedCarShare()` | ...as a share of what a NEW one cost the month it was struck, which is the only honest denominator and is between USED_CAR_FLOOR and USED_CAR_CEILING by construction. |
| 2346 | 1 | `public double getUsedCarSpend()` | What the buyers paid, all in. |
| 2352 | 1 | `public double getUsedCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month. |
| 2358 | 1 | `public double getCarsBought()` | Cars the households actually took this month. |
| 2365 | 1 | `public double getCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month or the money audit sees a pool fall for no reason. |

### THE MARKET (lines 2368-2538)

| line | len | member | says |
|---:|---:|---|---|
| 2381 | 6 | `public void setMarket(Exchange exchange, Equity register, Bank bank)` |  |
| 2393 | 12 | `private void rebuildLiquidity()` | The waterfall's two sales, in one object: the city's paper to the bank's desk first (0.7.1), then shares on the exchange. |
| 2415 | 28 | `public double buyShares(Exchange exchange, Equity register, Bank bank, int company, double fraction, double capacity)` | The households buy shares of one company on the exchange, each cell with money past its cushion putting a share of the excess in, pro rata when the desk cannot sell them all they want. |
| 2453 | 33 | `public double buyShares(Exchange exchange, Equity register, Bank bank, int[] companies, double fraction, double[] capacity)` | The same, across several companies in order of preference: each cell's month's money goes into the first while the desk can sell it, then the next. |
| 2488 | 5 | `public void splitShares(int company, double k)` | A split or consolidation: every household's count of the company by the factor. |
| 2495 | 9 | `public double sharesWanted(double fraction)` | What the households would put into shares this month, in cash, before the desk says how much it can sell. |
| 2511 | 14 | `public double tenderShares(int company, double fraction, double price)` | A company's tender: every household sells this share of what it holds of the company, at this price, into its savings. |
| 2527 | 4 | `public double marketValueOfShares()` | What the households' shares are worth at the exchange's quote, or at book with no exchange. |
| 2533 | 5 | `public double totalSold()` | What the households raised this month selling shares to cover the shop. |

### THE WORLD'S PAPER (lines 2539-2556)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) (lines 2557-2725)

| line | len | member | says |
|---:|---:|---|---|
| 2593 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |
| 2600 | 3 | `public void setExchangeRate(double localPerUsd)` |  |
| 2604 | 1 | `public double getExchangeRate()` |  |
| 2621 | 70 | `public void investAbroad(double depositRate, double worldRate, double rate)` | Every household decides where to keep its idle money: what is past the cushion goes abroad towards the target share, a little a month; what is abroad comes home when the target falls. |
| 2693 | 5 | `public double totalAbroadUsd()` | Dollars every household holds abroad. |
| 2700 | 1 | `public double totalAbroadValue()` | ...worth this much at home, at the month's rate. |
| 2703 | 5 | `public double getSentAbroad()` | Sent abroad this month, all households, local money. |
| 2710 | 5 | `public double getBroughtHome()` | Brought home this month - to keep to the target or to eat - all households, local money. |
| 2717 | 5 | `public double getForeignInterest()` | What the world paid the households this month, rolled abroad. |
| 2724 | 1 | `public double getAbroadTakenAway()` | Dollars that left with the households that left this month. |

### THE CITY'S PAPER, AT HOME (0.7.1) (lines 2726-2966)

| line | len | member | says |
|---:|---:|---|---|
| 2772 | 4 | **type** `public interface PaperDesk` | Where the households' paper is sold, for what, and what it does to the paper on the other side. |
| 2774 | 1 | `void buy(double face, double cash)` _(in HouseholdBalance.PaperDesk)_ | The desk buys this much face of the households' paper for this much cash. |
| 2779 | 4 | `public void setPaperDesk(PaperDesk desk)` |  |
| 2794 | 3 | `public void setPaperRatio(double ratio)` |  |
| 2798 | 1 | `public double getPaperRatio()` |  |
| 2811 | 3 | `public void setSpendFactor(double factor)` |  |
| 2816 | 1 | `public double getSpendFactor()` | The factor the last plan was struck at. |
| 2819 | 1 | `public double totalPaper()` | Every cell's paper, at face - DebtManager.householdPrincipal() from the other side. |
| 2822 | 1 | `public double marketValueOfPaper()` | ...at this month's market value. |
| 2825 | 5 | `public double totalPaperSold()` | Sold to the desk this month, all households, in cash. |
| 2832 | 5 | `public double totalPaperIncome()` | Coupons and principal the city paid the households on their paper this month. |
| 2838 | 3 | `private static boolean mayBuyPaper(Household c)` |  |
| 2842 | 3 | `private double spareFor(Household c)` |  |
| 2847 | 4 | `public static double paperShareAt(double yield, double depositRate)` | The share of an issue yielding this much the households would take, before asking whether they can pay. |
| 2853 | 5 | `public double spareForPaper()` | What every eligible household could put into paper: savings past the cushion, in total. |
| 2865 | 17 | `public double buyAtIssue(double cash, double facePerCash)` | The households buy at issue: this much cash in total, from each eligible cell pro rata to what is past its cushion, for this much face per dollar. |
| 2884 | 3 | `private double paperWeight(Household c, double total)` | Pro rata to the paper held: what a dollar of face in the households' book is spread as, cell by cell. |
| 2889 | 13 | `public void creditPaperCoupon(double total)` | A coupon on the households' share, into savings and the month's investment income, pro rata to the paper. |
| 2904 | 13 | `public void creditPaperPrincipal(double face)` | Principal on their share, into savings with the paper down by the same, pro rata. |
| 2919 | 10 | `public void creditPaperBuyback(double face, double price)` | A bond bought back: this much of their face off the paper, this much of the price into savings, pro rata. |
| 2931 | 9 | `private double sellPaperToDesk(Household c, double needPer)` | One cell sells to the desk for up to needPer a household. |
| 2950 | 16 | `public double sellPaperForSpread(double bookYield, double depositRate)` | THE SPREAD HAS GONE: when the households' paper yields no more than the bank pays savers, the target for it is nothing, and they sell a little a month - HOME_SPEED of it, the pace the dollars abroad come home at - int... |

### the people outside the families (lines 2967-3020)

| line | len | member | says |
|---:|---:|---|---|
| 2970 | 1 | `public double getEvicted()` | Out-of-work households that lost their home this month. |
| 2973 | 1 | `public double totalStudentBorrowed()` | Student loans drawn this month, all students - the treasury's money out. |
| 2976 | 1 | `public double totalStudentRepaid()` | ...and repaid by graduates, the treasury's money back: principal, which is what the journal's line carries. |
| 2979 | 1 | `public double totalStudentInterest()` | ...and the interest the graduates paid on top of it this month: the treasury's revenue. |
| 2982 | 3 | `public double studentInterestAt(double annualRate)` | What a month's interest would come to at an annual rate, on the balances that are charged it - for a screen previewing a rate. |
| 2987 | 1 | `public double totalStudentDebt()` | What every household owes the treasury in student loans. |
| 2990 | 3 | `public double totalGraduateDebt()` | ...and the part of it that is in repayment: the graduates' balances, which the rate is charged on. |
| 2995 | 1 | `public double getStudentDebtTakenAway()` | Student loans that left the city with graduates who left. |
| 3016 | 1 | `public void setGraduates(double people)` | THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS. |
| 3019 | 1 | `public double getLastGraduated()` | Students who left the student body with what they carried, at the last month's census. |

### what the bank is owed (lines 3021-3095)

| line | len | member | says |
|---:|---:|---|---|
| 3024 | 1 | `public double getWrittenOff()` | Written off this month, which is the bank's loss. |
| 3027 | 1 | `public double getTakenAway()` | Savings that left the city with the households that left. |
| 3030 | 1 | `public double getBankrupt(int row)` | Households discharged this month, and the share of them leaving the city. |
| 3031 | 1 | `public double getLeavingCity()` |  |
| 3034 | 1 | `public boolean isLockedOut(int row)` | True when any cell of the row the bank has stopped lending to is still locked out. |
| 3037 | 7 | `public int getLockout(int row)` | The longest lockout standing in the row. |
| 3046 | 1 | `public double bookOwed()` | Everything the households owe the bank. |
| 3064 | 31 | `public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, doub...` | The plan alone, without settling a month. |

### reading (lines 3096-3167)

| line | len | member | says |
|---:|---:|---|---|
| 3099 | 1 | `public double getSpendingCapacity()` | What the shops can sell next month, in money. |
| 3102 | 1 | `public double getWantedSpend()` | What they would spend if money were no object - the demand behind the cap. |
| 3105 | 1 | `public double getSubsistence(int row)` | One basket a head, per household of the row: what going short is measured against. |
| 3115 | 11 | `public double[] plannedShare()` | The share of each row's planned spend, for splitting what retail actually sold back across the rows. |
| 3128 | 3 | `public double getHungerRate()` | Share of the city going short of food, 0-1. |
| 3132 | 1 | `public double getHungryPeople()` |  |
| 3143 | 1 | `public double getDeliveredShare()` | The share of what households planned to buy that the shops could hand over - and therefore which of the two hungers is biting. |
| 3156 | 11 | `public void creditDepositInterest(double total)` | Interest the bank paid on what these households have saved. |

### THEFT (2026-09-11) (lines 3168-3227)

| line | len | member | says |
|---:|---:|---|---|
| 3187 | 16 | `public double takeFromSavings(double total)` | Takes up to `total` from the households' savings, each cell in proportion to what it has saved. |
| 3210 | 14 | `public double creditByWeight(double total, double[] weight)` | Credits `total` to the cells in proportion to a weight per cell, in the cells' order - what the offenders' households took home. |
| 3226 | 1 | `public double getDepositInterest()` | What the bank paid the city's savers this month. |

### THE OFFER (lines 3228-3390)

| line | len | member | says |
|---:|---:|---|---|
| 3260 | 28 | `public double subscribe(int company, double offered, double price)` | Puts an offering to every household, and takes up what they will buy. |
| 3299 | 10 | `public void grantFounders(int company, double shares)` | Hands out a company's founding shares to the people who founded it. |
| 3315 | 15 | `public double creditDividend(int company, double perShare)` | Pays every household its dividend, straight into its savings. |
| 3332 | 5 | `public double sharesHeld(int company)` | Shares of this company the city's households hold between them. |
| 3339 | 5 | `public double totalDividends()` | What the households were paid in dividends this month. |
| 3347 | 1 | `public double getSavings(int row)` | ---- the row, per household of it: what the screens and the fixtures read ---- |
| 3350 | 1 | `public double getHouseholds(int row)` | Households this row was struck for - the multiplier on every per-row figure. |
| 3351 | 1 | `public double getDebt(int row)` |  |
| 3352 | 1 | `public double getAfterFixed(int row)` |  |
| 3353 | 1 | `public double getInterest(int row)` |  |
| 3354 | 1 | `public double getDrawn(int row)` |  |
| 3357 | 1 | `public double getUnfunded(int row)` | What this row wanted, could not fund, and did not get. |
| 3360 | 4 | `public boolean isCutOff(int row)` | True when the bank has stopped lending to any cell of this row - ceiling or lockout. |
| 3364 | 1 | `public double getBorrowed(int row)` |  |
| 3365 | 1 | `public double getRepaid(int row)` |  |
| 3366 | 1 | `public double getBanked(int row)` |  |
| 3367 | 1 | `public double getWant(int row)` |  |
| 3368 | 1 | `public double getPlanned(int row)` |  |
| 3369 | 1 | `public double getRate(int row)` |  |
| 3372 | 4 | `public boolean isGoingShort(int row)` | True when this row is buying less food than it wants. |
| 3378 | 1 | `public double totalSavings()` | City totals, for the headline lines on the screen. |
| 3379 | 1 | `public double totalDebt()` |  |
| 3380 | 1 | `public double totalInterest()` |  |
| 3383 | 1 | `public double totalBorrowed()` | New lending to families this month - the bank's money out the door. |
| 3386 | 1 | `public double totalRepaid()` | ...and what came back. |
| 3389 | 1 | `public double totalNetWorth()` | What every household in the city has, less what it owes. |

### saving (lines 3391-3760)

| line | len | member | says |
|---:|---:|---|---|
| 3411 | 36 | `public double[] toSaveArray()` | The row array an older build reads: ROWS*8+3, per household of the row. |
| 3496 | 5 | `public String[] cellKeys()` | The name of every cell, in the order toCellSaveArray() writes them. |
| 3503 | 26 | `public double[] toCellSaveArray()` | CELL_SLOTS per cell, in cellKeys() order, then the three city figures. |
| 3541 | 117 | `public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies)` | Puts the cells back, by name. |
| 3672 | 43 | `public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Puts a ROW array back, seeding every cell of the row with the row's position: the save from a build that kept the stocks per tier. |
| 3717 | 1 | `public void restore(double[] saved)` | The row array alone, with no census: the cells wait for the plan to count them. |
| 3719 | 22 | `public void reset()` |  |
| 3748 | 11 | `public void redenominate(double scale)` | The households' stocks and this month's working, in the new unit. |


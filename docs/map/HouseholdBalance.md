# HouseholdBalance.java - 3,259 lines · 147 methods · 36 constants · model

`ham/citybuildersim/HouseholdBalance.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Household](Household.md) (128), [Equity](Equity.md) (16), [FamilyStructure](FamilyStructure.md) (11), [PayTier](PayTier.md) (10), [UnemployedHousehold](UnemployedHousehold.md) (7), [AgeBand](AgeBand.md) (7), [WorkingHousehold](WorkingHousehold.md) (4), [Exchange](Exchange.md) (4), [Bank](Bank.md) (4), [OutwardInvestment](OutwardInvestment.md) (4), [StudentHousehold](StudentHousehold.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [PrisonerHousehold](PrisonerHousehold.md) (3), [TaxPolicy](TaxPolicy.md) (2), [Restaurants](Restaurants.md) (2), [RetiredHousehold](RetiredHousehold.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (25):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Household](Household.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [Motoring](Motoring.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 97 | · the dials |
| 153 | AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT |
| 218 | ...AND WHAT IT SPENDS EATING OUT (2026-09-18) |
| 244 | AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) |
| 260 | AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) |
| 298 | BANKRUPTCY |
| 341 | · the cells |
| 396 | THE PRICE AT THE CLINIC DOOR (2026-09-19) |
| 476 | · the student loan's rate (2026-09-21) |
| 493 | · the month |
| 546 | EVERY HOUSEHOLD, OR ONE OF THEM |
| 623 | THE MONTH |
| 702 | · ...AND A MEAL OUT IS FOOD (2026-09-18) |
| 939 | THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS |
| 1151 | · EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16) |
| 1260 | THE CARS (2026-09-16) |
| 1336 | AND THEY BORROW FOR IT (2026-09-17) |
| 1421 | THE SECOND-HAND MARKET (2026-09-17) |
| 1557 | WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) |
| 1707 | · the second-hand market |
| 1772 | THE LUXURY COUNTER (2026-09-17) |
| 1873 | THE TABLE (2026-09-18) |
| 2148 | THE MARKET |
| 2302 | THE WORLD'S PAPER |
| 2320 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) |
| 2392 | · THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17) |
| 2489 | · the people outside the families |
| 2543 | · what the bank is owed |
| 2613 | · reading |
| 2685 | THEFT (2026-09-11) |
| 2745 | THE OFFER |
| 2908 | · saving |
| 3050 | · THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12) |

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
| 296 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 330 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 333 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 336 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 339 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1297 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1309 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1334 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1393 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1419 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1478 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1487 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 1729 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2350 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 2759 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 2762 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 2966 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 2969 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 2972 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 2975 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 2978 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 2988 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 2998 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 3001 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_CARE + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month's meals eaten out, t... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 349 | `private final Household[] cells` | Every meaningful cell of the family matrix, in one fixed order: the working shapes by declaration, each across the six tiers, then the two retired shapes. |
| 350 | `private final int[][] index` |  |
| 352 | `private final java.util.List<Household> view` |  |
| 353 | `private final int[] unemployedIndex` |  |
| 354 | `private int studentIndex` |  |
| 355 | `private final int[] orphanIndex` |  |
| 356 | `private int prisonerIndex` |  |
| 364 | `private ToDoubleFunction<Household> outsideCensus` | How many households are in each cell the family matrix does not hold - the out of work, the students, the orphans. |
| 375 | `private ToDoubleFunction<Household> rentShares` | The share of a door's rent one household of a cell pays: 1 alone in its own home, a fifth sharing, half doubled up, none with no door. |
| 390 | `private ToDoubleFunction<Household> outsideDependants` | Dependants living in each cell the family matrix does not hold. |
| 431 | `private double[] careBillByRow` | This month's treatment fees by row, as the households were billed them, or null for a caller that does not price care. |
| 434 | `private double[] careBillFullByRow` | ...and the treatment bill the same rows would have faced at full service. |
| 437 | `private double lastCareSkipped` | The care bills the households skipped this month, summed over the city: what they ate instead. |
| 483 | `private double studentLoanRate` |  |
| 495 | `private double lastWrittenOff` |  |
| 496 | `private double lastLeaving` |  |
| 497 | `private double lastEvicted` |  |
| 498 | `private double lastStudentDebtTakenAway` |  |
| 499 | `private double lastTakenAway` |  |
| 500 | `private double graduating` |  |
| 501 | `private double lastGraduated` |  |
| 502 | `private double lastDepositInterest` |  |
| 503 | `private double lastDelivered` |  |
| 504 | `private double plannedSpend` |  |
| 505 | `private double hungryPeople` |  |
| 506 | `private double totalPeople` |  |
| 1257 | `private final double[] lastSharesTakenAway` | Shares of each company that left the city with their holders this month. |
| 1490 | `private final double[] carsToReplace` | What died this month, per cell, waiting to be replaced. |
| 1493 | `private double lastCarsTakenAway` | ...and the cars whose owners left the city. |
| 1976 | `private double lastMealsBought, lastMealSpend, lastMealsEaten` |  |
| 1987 | `private double lastLuxuriesBought, lastLuxurySpend` |  |
| 2098 | `private double lastUsedOffered, lastUsedTraded, lastUsedSpend, lastUsedFinanced, lastUsedPrice, lastUsedNew...` |  |
| 2134 | `private double lastCarsBought` |  |
| 2135 | `private double lastCarsFinanced` |  |
| 2156 | `private Exchange exchange` |  |
| 2157 | `private Equity register` |  |
| 2158 | `private Bank bank` |  |
| 2159 | `private Household.Liquidity liquidity` |  |
| 2353 | `private double minMove` | The same floor in today's money. |
| 2361 | `private double localPerUsd` | Local currency per dollar, this month: what the paper abroad is worth here. |
| 2370 | `private double lastAbroadTakenAway` | Dollars the households that left this month took with them. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 3174 | **type** `public class HouseholdBalance` | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. |

### the dials (lines 97-152)

### AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT (lines 153-217)

### ...AND WHAT IT SPENDS EATING OUT (2026-09-18) (lines 218-243)

### AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) (lines 244-259)

### AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) (lines 260-297)

### BANKRUPTCY (lines 298-340)

### the cells (lines 341-395)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 3 | `public void setOutsideCensus(ToDoubleFunction<Household> census)` |  |
| 377 | 3 | `public void setRentShares(ToDoubleFunction<Household> shares)` |  |
| 392 | 3 | `public void setOutsideDependants(ToDoubleFunction<Household> kin)` |  |

### THE PRICE AT THE CLINIC DOOR (2026-09-19) (lines 396-475)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 4 | `public void setCareBills(double[] treatmentBilled, double[] treatmentAtFullService)` | Hands the strike the month's treatment bills, by row: what the households were billed (HouseholdAccounts.getRowCareBilled) and what the same rows would have been billed at full service (getRowCareFull). |
| 455 | 10 | `public double carePaidShare(int row)` | Of a row's people, the share who paid for care at the last strike - people-weighted over its cells, 1 for a row with nobody in it. |
| 467 | 5 | `public double[] carePaidShares()` | The same, for every row at once, in row order. |
| 474 | 1 | `public double getCareSkipped()` | What the city's households skipped of their care bills this month, in money - and ate instead. |

### the student loan's rate (2026-09-21) (lines 476-492)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 3 | `public void setStudentLoanRate(double annual)` | Sets the annual rate the graduates are charged this month. |
| 491 | 1 | `public double getStudentLoanRate()` | The annual rate the cells were last told. |

### the month (lines 493-545)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 37 | `public HouseholdBalance()` |  |

### EVERY HOUSEHOLD, OR ONE OF THEM (lines 546-622)

| line | len | member | says |
|---:|---:|---|---|
| 554 | 4 | `public Household cell(FamilyStructure shape, PayTier tier)` | The cell for this shape at this tier. |
| 560 | 6 | `public Household cell(FamilyStructure shape)` | The cell for a retired shape, which has no tier. |
| 568 | 3 | `public UnemployedHousehold unemployed(UnemployedHousehold.Status status)` | The out-of-work cell in this situation. |
| 573 | 1 | `public StudentHousehold students()` | The students' cell. |
| 576 | 4 | `public OrphanHousehold orphans(AgeBand band)` | The orphans of a child band, or null for a band that has none. |
| 582 | 1 | `public PrisonerHousehold prisoners()` | The prisoners' ledger. |
| 585 | 1 | `public java.util.List<Household> cells()` | Every cell, in the fixed order. |
| 587 | 1 | `public int cellCount()` |  |
| 589 | 3 | `public void forEach(Consumer<Household> action)` |  |
| 594 | 5 | `public double sum(ToDoubleFunction<Household> figure)` | Adds a figure up across every cell. |
| 601 | 5 | `public double sumRow(int row, ToDoubleFunction<Household> figure)` | ...or across one row: a tier's cells, or the retired. |
| 608 | 3 | `public double rowHouseholds(int row)` | Households in the row: the denominator of every per-household row figure. |
| 613 | 9 | `private double perHousehold(int row, ToDoubleFunction<Household> perCell)` | A row total, per household of the row. |

### THE MONTH (lines 623-938)

| line | len | member | says |
|---:|---:|---|---|
| 651 | 218 | `public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, ...` |  |
| 875 | 5 | `private static double[] padRows(double[] rows)` | A seven-row array - the tiers and the retired, from a caller written before the people outside the families had rows - padded with empty rows. |
| 882 | 16 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Who lives in each cell now, in cell order. |
| 909 | 13 | `private double[] splitIncome(double[] fresh, double[] rowTotal)` | A row's take-home, per household of each of its cells. |
| 924 | 14 | `private double[] splitByPeople(double[] fresh, double[] rowTotal)` | A row's fees, per household of each of its cells, by the people in them. |

### THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS (lines 939-1259)

| line | len | member | says |
|---:|---:|---|---|
| 980 | 4 | **type** `private interface Stock` | One stock a household carries, for followThePeople() to move. |
| 981 | 1 | `double get(Household c)` _(in HouseholdBalance.Stock)_ |  |
| 982 | 1 | `void set(Household c, double perHousehold)` _(in HouseholdBalance.Stock)_ |  |
| 995 | 80 | `private void followThePeople(double[] fresh, double[] buffer)` | Moves every stock with the people, then sets every cell's count. |
| 1090 | 27 | `private double[] carryGraduatesLoans(double[] before)` | The graduates' student loans, handed to the working families before the census moves anything. |
| 1130 | 125 | `private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival)` | One stock through the pool: released by the cells that shrank at their own average, claimed by the cells that grew - within the row first, then across the city - and what nobody claimed returned. |
| 1258 | 1 | `public double getSharesTakenAway(int company)` |  |

### THE CARS (2026-09-16) (lines 1260-1335)

### AND THEY BORROW FOR IT (2026-09-17) (lines 1336-1420)

### THE SECOND-HAND MARKET (2026-09-17) (lines 1421-1556)

| line | len | member | says |
|---:|---:|---|---|
| 1495 | 1 | `public double getCarsTakenAway()` |  |
| 1498 | 1 | `public double totalCars()` | Every car in the city. |
| 1501 | 4 | `public double carsPerHousehold()` | Cars per household, 0 to 1 - what the road reads. |
| 1516 | 12 | `public double wearOutCars()` | Fifteen years on, every car in the city is scrapped. |
| 1550 | 6 | `public double carsWanted(double price, double ceiling)` | What the households would buy this month at this price, before the market says how many there are. |

### WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) (lines 1557-1706)

| line | len | member | says |
|---:|---:|---|---|
| 1589 | 49 | `private double wantOf(int i, double price, double ceiling)` | What one cell would buy this month, in whole cars. |
| 1651 | 55 | `public double takeCars(double units, double price, double ceiling)` | Hands out the cars the market actually had, pro rata over who wanted them, and takes the money out of savings. |

### the second-hand market (lines 1707-1771)

| line | len | member | says |
|---:|---:|---|---|
| 1758 | 13 | `private double offerOf(int i, double floorPrice)` | What one cell would put up for sale this month, in whole cars. |

### THE LUXURY COUNTER (2026-09-17) (lines 1772-1872)

| line | len | member | says |
|---:|---:|---|---|
| 1799 | 4 | `public double luxuriesWanted(double price)` | What the city's households would buy at this price, in whole pieces. |
| 1805 | 5 | `private double luxuryBudget()` | What the city's households would put over a counter, in money. |
| 1821 | 7 | `private double luxuryBudgetOf(Household c)` | What one cell would buy, in whole pieces. |
| 1842 | 30 | `public double takeLuxuries(double units, double price)` | Hands out the pieces the shops actually had, pro rata over who wanted them, and takes the money out of savings. |

### THE TABLE (2026-09-18) (lines 1873-2147)

| line | len | member | says |
|---:|---:|---|---|
| 1891 | 16 | `public double mealsWanted(double price)` | What the city's households would eat out at this price, in whole meals. |
| 1909 | 5 | `private double mealBudget()` | What the city's households would put on a table, in money. |
| 1928 | 7 | `private double mealBudgetOf(Household c)` | What one cell would spend eating out. |
| 1947 | 28 | `public double takeMeals(double meals, double price)` | Hands out the meals the kitchens actually served, pro rata over who wanted them, takes the money out of savings and REMEMBERS THE MEALS. |
| 1979 | 1 | `public double getMealsEaten()` | Meals the city ate out in the month the hunger measure just read. |
| 1982 | 1 | `public double getMealsBought()` | Meals the households ate out this month. |
| 1985 | 1 | `public double getMealSpend()` | ...and what they paid for them. |
| 1990 | 1 | `public double getLuxuriesBought()` | Pieces the households took this month. |
| 1993 | 1 | `public double getLuxurySpend()` | ...and what they paid for them. |
| 1996 | 1 | `public double getLuxuryWant()` | What every household in the city would like to put over a counter this month. |
| 1999 | 5 | `public double carsOffered(double floorPrice)` | What the city's households would put up for sale at this floor. |
| 2027 | 70 | `public double clearUsedCars(double newPrice, double ceiling)` | Clears the month's second-hand market: strikes a price, moves the cars that find a buyer, and pays the households that sold them. |
| 2102 | 1 | `public double getUsedCarsOffered()` | Cars put up for sale this month, whether or not anybody took them. |
| 2105 | 1 | `public double getUsedCarsTraded()` | ...and the ones that found a buyer. |
| 2108 | 1 | `public double getUsedCarPrice()` | What they went for. |
| 2121 | 3 | `public double getUsedCarShare()` | ...as a share of what a NEW one cost the month it was struck, which is the only honest denominator and is between USED_CAR_FLOOR and USED_CAR_CEILING by construction. |
| 2126 | 1 | `public double getUsedCarSpend()` | What the buyers paid, all in. |
| 2132 | 1 | `public double getUsedCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month. |
| 2138 | 1 | `public double getCarsBought()` | Cars the households actually took this month. |
| 2145 | 1 | `public double getCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month or the money audit sees a pool fall for no reason. |

### THE MARKET (lines 2148-2301)

| line | len | member | says |
|---:|---:|---|---|
| 2161 | 7 | `public void setMarket(Exchange exchange, Equity register, Bank bank)` |  |
| 2178 | 28 | `public double buyShares(Exchange exchange, Equity register, Bank bank, int company, double fraction, double capacity)` | The households buy shares of one company on the exchange, each cell with money past its cushion putting a share of the excess in, pro rata when the desk cannot sell them all they want. |
| 2216 | 33 | `public double buyShares(Exchange exchange, Equity register, Bank bank, int[] companies, double fraction, double[] capacity)` | The same, across several companies in order of preference: each cell's month's money goes into the first while the desk can sell it, then the next. |
| 2251 | 5 | `public void splitShares(int company, double k)` | A split or consolidation: every household's count of the company by the factor. |
| 2258 | 9 | `public double sharesWanted(double fraction)` | What the households would put into shares this month, in cash, before the desk says how much it can sell. |
| 2274 | 14 | `public double tenderShares(int company, double fraction, double price)` | A company's tender: every household sells this share of what it holds of the company, at this price, into its savings. |
| 2290 | 4 | `public double marketValueOfShares()` | What the households' shares are worth at the exchange's quote, or at book with no exchange. |
| 2296 | 5 | `public double totalSold()` | What the households raised this month selling shares to cover the shop. |

### THE WORLD'S PAPER (lines 2302-2319)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) (lines 2320-2488)

| line | len | member | says |
|---:|---:|---|---|
| 2356 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |
| 2363 | 3 | `public void setExchangeRate(double localPerUsd)` |  |
| 2367 | 1 | `public double getExchangeRate()` |  |
| 2384 | 70 | `public void investAbroad(double depositRate, double worldRate, double rate)` | Every household decides where to keep its idle money: what is past the cushion goes abroad towards the target share, a little a month; what is abroad comes home when the target falls. |
| 2456 | 5 | `public double totalAbroadUsd()` | Dollars every household holds abroad. |
| 2463 | 1 | `public double totalAbroadValue()` | ...worth this much at home, at the month's rate. |
| 2466 | 5 | `public double getSentAbroad()` | Sent abroad this month, all households, local money. |
| 2473 | 5 | `public double getBroughtHome()` | Brought home this month - to keep to the target or to eat - all households, local money. |
| 2480 | 5 | `public double getForeignInterest()` | What the world paid the households this month, rolled abroad. |
| 2487 | 1 | `public double getAbroadTakenAway()` | Dollars that left with the households that left this month. |

### the people outside the families (lines 2489-2542)

| line | len | member | says |
|---:|---:|---|---|
| 2492 | 1 | `public double getEvicted()` | Out-of-work households that lost their home this month. |
| 2495 | 1 | `public double totalStudentBorrowed()` | Student loans drawn this month, all students - the treasury's money out. |
| 2498 | 1 | `public double totalStudentRepaid()` | ...and repaid by graduates, the treasury's money back: principal, which is what the journal's line carries. |
| 2501 | 1 | `public double totalStudentInterest()` | ...and the interest the graduates paid on top of it this month: the treasury's revenue. |
| 2504 | 3 | `public double studentInterestAt(double annualRate)` | What a month's interest would come to at an annual rate, on the balances that are charged it - for a screen previewing a rate. |
| 2509 | 1 | `public double totalStudentDebt()` | What every household owes the treasury in student loans. |
| 2512 | 3 | `public double totalGraduateDebt()` | ...and the part of it that is in repayment: the graduates' balances, which the rate is charged on. |
| 2517 | 1 | `public double getStudentDebtTakenAway()` | Student loans that left the city with graduates who left. |
| 2538 | 1 | `public void setGraduates(double people)` | THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS. |
| 2541 | 1 | `public double getLastGraduated()` | Students who left the student body with what they carried, at the last month's census. |

### what the bank is owed (lines 2543-2612)

| line | len | member | says |
|---:|---:|---|---|
| 2546 | 1 | `public double getWrittenOff()` | Written off this month, which is the bank's loss. |
| 2549 | 1 | `public double getTakenAway()` | Savings that left the city with the households that left. |
| 2552 | 1 | `public double getBankrupt(int row)` | Households discharged this month, and the share of them leaving the city. |
| 2553 | 1 | `public double getLeavingCity()` |  |
| 2556 | 1 | `public boolean isLockedOut(int row)` | True when any cell of the row the bank has stopped lending to is still locked out. |
| 2559 | 7 | `public int getLockout(int row)` | The longest lockout standing in the row. |
| 2568 | 1 | `public double bookOwed()` | Everything the households owe the bank. |
| 2586 | 26 | `public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, doub...` | The plan alone, without settling a month. |

### reading (lines 2613-2684)

| line | len | member | says |
|---:|---:|---|---|
| 2616 | 1 | `public double getSpendingCapacity()` | What the shops can sell next month, in money. |
| 2619 | 1 | `public double getWantedSpend()` | What they would spend if money were no object - the demand behind the cap. |
| 2622 | 1 | `public double getSubsistence(int row)` | One basket a head, per household of the row: what going short is measured against. |
| 2632 | 11 | `public double[] plannedShare()` | The share of each row's planned spend, for splitting what retail actually sold back across the rows. |
| 2645 | 3 | `public double getHungerRate()` | Share of the city going short of food, 0-1. |
| 2649 | 1 | `public double getHungryPeople()` |  |
| 2660 | 1 | `public double getDeliveredShare()` | The share of what households planned to buy that the shops could hand over - and therefore which of the two hungers is biting. |
| 2673 | 11 | `public void creditDepositInterest(double total)` | Interest the bank paid on what these households have saved. |

### THEFT (2026-09-11) (lines 2685-2744)

| line | len | member | says |
|---:|---:|---|---|
| 2704 | 16 | `public double takeFromSavings(double total)` | Takes up to `total` from the households' savings, each cell in proportion to what it has saved. |
| 2727 | 14 | `public double creditByWeight(double total, double[] weight)` | Credits `total` to the cells in proportion to a weight per cell, in the cells' order - what the offenders' households took home. |
| 2743 | 1 | `public double getDepositInterest()` | What the bank paid the city's savers this month. |

### THE OFFER (lines 2745-2907)

| line | len | member | says |
|---:|---:|---|---|
| 2777 | 28 | `public double subscribe(int company, double offered, double price)` | Puts an offering to every household, and takes up what they will buy. |
| 2816 | 10 | `public void grantFounders(int company, double shares)` | Hands out a company's founding shares to the people who founded it. |
| 2832 | 15 | `public double creditDividend(int company, double perShare)` | Pays every household its dividend, straight into its savings. |
| 2849 | 5 | `public double sharesHeld(int company)` | Shares of this company the city's households hold between them. |
| 2856 | 5 | `public double totalDividends()` | What the households were paid in dividends this month. |
| 2864 | 1 | `public double getSavings(int row)` | ---- the row, per household of it: what the screens and the fixtures read ---- |
| 2867 | 1 | `public double getHouseholds(int row)` | Households this row was struck for - the multiplier on every per-row figure. |
| 2868 | 1 | `public double getDebt(int row)` |  |
| 2869 | 1 | `public double getAfterFixed(int row)` |  |
| 2870 | 1 | `public double getInterest(int row)` |  |
| 2871 | 1 | `public double getDrawn(int row)` |  |
| 2874 | 1 | `public double getUnfunded(int row)` | What this row wanted, could not fund, and did not get. |
| 2877 | 4 | `public boolean isCutOff(int row)` | True when the bank has stopped lending to any cell of this row - ceiling or lockout. |
| 2881 | 1 | `public double getBorrowed(int row)` |  |
| 2882 | 1 | `public double getRepaid(int row)` |  |
| 2883 | 1 | `public double getBanked(int row)` |  |
| 2884 | 1 | `public double getWant(int row)` |  |
| 2885 | 1 | `public double getPlanned(int row)` |  |
| 2886 | 1 | `public double getRate(int row)` |  |
| 2889 | 4 | `public boolean isGoingShort(int row)` | True when this row is buying less food than it wants. |
| 2895 | 1 | `public double totalSavings()` | City totals, for the headline lines on the screen. |
| 2896 | 1 | `public double totalDebt()` |  |
| 2897 | 1 | `public double totalInterest()` |  |
| 2900 | 1 | `public double totalBorrowed()` | New lending to families this month - the bank's money out the door. |
| 2903 | 1 | `public double totalRepaid()` | ...and what came back. |
| 2906 | 1 | `public double totalNetWorth()` | What every household in the city has, less what it owes. |

### saving (lines 2908-3259)

| line | len | member | says |
|---:|---:|---|---|
| 2928 | 36 | `public double[] toSaveArray()` | The row array an older build reads: ROWS*8+3, per household of the row. |
| 3004 | 5 | `public String[] cellKeys()` | The name of every cell, in the order toCellSaveArray() writes them. |
| 3011 | 25 | `public double[] toCellSaveArray()` | CELL_SLOTS per cell, in cellKeys() order, then the three city figures. |
| 3048 | 111 | `public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies)` | Puts the cells back, by name. |
| 3173 | 43 | `public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Puts a ROW array back, seeding every cell of the row with the row's position: the save from a build that kept the stocks per tier. |
| 3218 | 1 | `public void restore(double[] saved)` | The row array alone, with no census: the cells wait for the plan to count them. |
| 3220 | 20 | `public void reset()` |  |
| 3247 | 11 | `public void redenominate(double scale)` | The households' stocks and this month's working, in the new unit. |


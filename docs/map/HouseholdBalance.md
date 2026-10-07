# HouseholdBalance.java - 4,642 lines · 242 methods · 48 constants · model

`ham/citybuildersim/HouseholdBalance.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [Household](Household.md) (211), [Equity](Equity.md) (14), [FamilyStructure](FamilyStructure.md) (12), [PayTier](PayTier.md) (11), [UnemployedHousehold](UnemployedHousehold.md) (7), [AgeBand](AgeBand.md) (7), [OutwardInvestment](OutwardInvestment.md) (5), [WorkingHousehold](WorkingHousehold.md) (4), [Bank](Bank.md) (4), [StudentHousehold](StudentHousehold.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [PrisonerHousehold](PrisonerHousehold.md) (3), [TaxPolicy](TaxPolicy.md) (2), [Restaurants](Restaurants.md) (2), [Exchange](Exchange.md) (2), [BondMarket](BondMarket.md) (2), [RetiredHousehold](RetiredHousehold.md) (1), [Retail](Retail.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (41):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [CrimeCheck](CrimeCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [Motoring](Motoring.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [SaveFileCheck](SaveFileCheck.md), [ShadowBasket](ShadowBasket.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 97 | · the dials |
| 152 | AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT |
| 217 | ...AND WHAT IT SPENDS EATING OUT (2026-09-18) |
| 243 | AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) |
| 259 | AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) |
| 288 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 362 | BANKRUPTCY |
| 405 | · the cells |
| 460 | THE PRICE AT THE CLINIC DOOR (2026-09-19) |
| 540 | · the student loan's rate (2026-09-21) |
| 554 | · the bank's account fee (0.7.7) |
| 631 | · the month |
| 686 | EVERY HOUSEHOLD, OR ONE OF THEM |
| 763 | THE MONTH |
| 844 | · ...AND A MEAL OUT IS FOOD (2026-09-18) |
| 1128 | THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS |
| 1350 | A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) |
| 1536 | · EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16) |
| 1645 | THE CARS (2026-09-16) |
| 1724 | AND THEY BORROW FOR IT (2026-09-17) |
| 1809 | THE SECOND-HAND MARKET (2026-09-17) |
| 1983 | WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) |
| 2139 | · the second-hand market |
| 2204 | GROCERIES AT A PRICE, AND FOOD ASSISTANCE (0.7.43) |
| 2394 | · who goes without, read for the screens (0.7.45) |
| 2439 | THE LUXURY COUNTER (2026-09-17) |
| 2540 | THE TABLE (2026-09-18) |
| 2825 | THE MARKET |
| 2894 | THE WORLD'S PAPER |
| 2912 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) |
| 3002 | · THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17) |
| 3099 | THE CITY'S PAPER, AT HOME (0.7.1) |
| 3350 | THE BUSINESSES' BONDS, AT HOME (0.7.12) |
| 3609 | · the people outside the families |
| 3684 | · what the bank is owed |
| 3761 | · reading |
| 3862 | THEFT (2026-09-11) |
| 3922 | THE OFFER |
| 4147 | · saving |
| 4358 | · THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12) |

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
| 332 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provisional - Jerus's num... |
| 335 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 338 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 360 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 394 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 397 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 400 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 403 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1385 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1682 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1694 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1722 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1781 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1807 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1866 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1875 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 2161 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2243 | `HouseholdBalance.FOOD_ASSISTANCE_MEANS_SHARE` | `.5` | A household is eligible for food assistance when a basket a head would take more than this share of what it has after its fixed bills and its investment income over the year: half. |
| 2257 | `HouseholdBalance.MEANS_INCOME_MONTHS` | `12` | Months the means test's investment income is smoothed over, as an exponential average - a twelfth of the gap a month, the year Expectations smooths inflation over (0.7.45; the UI spec's B16). |
| 2942 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 3139 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point - so two and a half points of spread would take half an issue. |
| 3142 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and never more than this share of one issue: half, because a bond market with no bank in it is no longer the city's bank's market. |
| 3936 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3939 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 4213 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 4216 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 4219 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 4222 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 4225 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 4235 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 4245 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 4254 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 4266 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim on the households' o... |
| 4280 | `HouseholdBalance.CELL_SLOTS_BEFORE_GROCERIES` | `CELL_SLOTS_BEFORE_BONDS + 1` | ...and the last sale's groceries, appended 0.7.43: the baskets each household got, the baskets it asked for at the price, the baskets it needed, and the food assistance paid on them - written at the bottom of the mont... |
| 4288 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEANS_INCOME` | `CELL_SLOTS_BEFORE_GROCERIES + 4` | ...and the investment income the food assistance means test reads, smoothed over MEANS_INCOME_MONTHS, appended 0.7.45. |
| 4298 | `HouseholdBalance.CELL_SLOTS_BEFORE_AFTER_FIXED` | `CELL_SLOTS_BEFORE_MEANS_INCOME + 1` | ...and before each cell's income after its fixed bills was carried (A4). |
| 4301 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_AFTER_FIXED + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month's meals eaten out, t... |

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
| 565 | `private double accountFee` |  |
| 566 | `private ToDoubleFunction<Household> housedShares` |  |
| 633 | `private double lastWrittenOff` |  |
| 634 | `private double lastLeaving` |  |
| 635 | `private double lastEvicted` |  |
| 636 | `private double lastStudentDebtTakenAway` |  |
| 637 | `private double lastTakenAway` |  |
| 638 | `private double graduating` |  |
| 639 | `private double lastGraduated` |  |
| 640 | `private double lastDepositInterest` |  |
| 641 | `private double lastDelivered` |  |
| 642 | `private double plannedSpend` |  |
| 643 | `private double hungryPeople` |  |
| 644 | `private double totalPeople` |  |
| 646 | `private double hungryAtFullShelves` | ...of whom hungry even had the shops handed over all that was planned: the money half (0.7.27). |
| 1169 | `private int[] bondStockIds` | The bonds' ids in the order stocks() last listed them, one stock each from its named slot. |
| 1388 | `private int lastCellsFolded` | Cells the census left under EMPTY_CELL this month that still held something, and were emptied. |
| 1642 | `private final double[] lastSharesTakenAway` | Shares of each company that left the city with their holders this month. |
| 1878 | `private final double[] carsToReplace` | What died this month, per cell, waiting to be replaced. |
| 1881 | `private double lastCarsTakenAway` | ...and the cars whose owners left the city. |
| 2260 | `private double satiationPrice` | The price a full basket is still wanted at, in today's money: told by Retail at its sale and by Game where the households plan (Retail.getSatiationPrice()). |
| 2263 | `private double foodAssistance` | The food assistance dial, 0 to 1: the share of an eligible household's baskets the treasury's voucher covers. |
| 2266 | `private double lastFoodAssistancePaid, lastFedByAssistance` | What the treasury paid toward the households' groceries at the last sale, and the baskets that bought. |
| 2643 | `private double lastMealsBought, lastMealSpend, lastMealsEaten` |  |
| 2654 | `private double lastLuxuriesBought, lastLuxurySpend` |  |
| 2767 | `private double lastUsedOffered, lastUsedTraded, lastUsedSpend, lastUsedFinanced, lastUsedPrice, lastUsedNew...` |  |
| 2805 | `private double lastUsedLoanFees` |  |
| 2807 | `private double lastCarsBought` |  |
| 2808 | `private double lastCarsFinanced` |  |
| 2822 | `private double lastCarLoanFees` |  |
| 2833 | `private Exchange exchange` |  |
| 2834 | `private Equity register` |  |
| 2835 | `private Bank bank` |  |
| 2836 | `private Household.Liquidity liquidity` |  |
| 2945 | `private double minMove` | The same floor in today's money. |
| 2971 | `private double localPerUsd` | Local currency per dollar, this month: what the paper abroad is worth here. |
| 2980 | `private double lastAbroadTakenAway` | Dollars the households that left this month took with them. |
| 3154 | `private PaperDesk paperDesk` |  |
| 3169 | `private double paperRatio` | What a dollar of face of the households' paper is worth this month: the households' book at the curve over its face (DebtManager .householdBookRatio()), struck once a month by Game before the households' strike and SA... |
| 3186 | `private double spendFactor` | The month's spend factor (0.7.3): what share of its spending above subsistence every household plans, struck by Game once a month on the real deposit rate (Game.spendFactor()) before the households plan - see the bann... |
| 3365 | `private BondMarket bondMarket` |  |
| 3378 | `private double bondRatio` | What a dollar of face of the households' bonds is worth this month: every cell's holdings at the bonds' value over their face, struck by the market at its step and SAVED, because the plan reads it and the load path mu... |
| 3387 | `private double lastBondsTakenAway` | The face that left the city this month with its holders, every bond together. |
| 3427 | `private java.util.Map<String, Household> cellIndex` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 4557 | **type** `public class HouseholdBalance` | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. |

### the dials (lines 97-151)

### AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT (lines 152-216)

### ...AND WHAT IT SPENDS EATING OUT (2026-09-18) (lines 217-242)

### AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) (lines 243-258)

### AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) (lines 259-287)

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 288-361)

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

### the student loan's rate (2026-09-21) (lines 540-553)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 3 | `public void setStudentLoanRate(double annual)` | Sets the annual rate the graduates are charged this month. |

### the bank's account fee (0.7.7) (lines 554-630)

| line | len | member | says |
|---:|---:|---|---|
| 568 | 1 | `public void setAccountFee(double perHousehold)` |  |
| 569 | 1 | `public double getAccountFee()` |  |
| 581 | 7 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | THE BANK'S CAPITAL RULE FOR THE MONTH (0.7.8), from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(), set by Game at the top of the month before any family borrows: each cell may owe at most what it owes ... |
| 590 | 1 | `public double lossAllowance()` | What the bank sets aside against the families' debt (0.7.8), summed cell by cell - see Household.lossAllowance(). |
| 593 | 1 | `public double debtInTrouble()` | ...and how much of their debt is in cells that are in trouble. |
| 595 | 3 | `public void setHousedShares(ToDoubleFunction<Household> shares)` |  |
| 600 | 4 | `private double accountFeeFor(Household c, double households)` | The fee one household of this cell is charged, when the cell holds this many: nothing in a cell too empty to strike, which is paid nothing and pays nothing. |
| 612 | 9 | `public double[] accountFeesByRow(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | What each row's households will be charged in account fees this month, on this census: the figure the household books show on their own line (HouseholdAccounts.setAccountFees()), struck BEFORE the cells settle - the b... |
| 623 | 1 | `public double totalAccountFees()` | What the households paid the bank in account fees this month: the sum the bank takes. |
| 626 | 1 | `public double totalLoanFees()` | ...and the loan fees the bank added to what they owe on the credit they drew this month. |
| 629 | 1 | `public double getStudentLoanRate()` | The annual rate the cells were last told. |

### the month (lines 631-685)

| line | len | member | says |
|---:|---:|---|---|
| 648 | 37 | `public HouseholdBalance()` |  |

### EVERY HOUSEHOLD, OR ONE OF THEM (lines 686-762)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 4 | `public Household cell(FamilyStructure shape, PayTier tier)` | The cell for this shape at this tier. |
| 700 | 6 | `public Household cell(FamilyStructure shape)` | The cell for a retired shape, which has no tier. |
| 708 | 3 | `public UnemployedHousehold unemployed(UnemployedHousehold.Status status)` | The out-of-work cell in this situation. |
| 713 | 1 | `public StudentHousehold students()` | The students' cell. |
| 716 | 4 | `public OrphanHousehold orphans(AgeBand band)` | The orphans of a child band, or null for a band that has none. |
| 722 | 1 | `public PrisonerHousehold prisoners()` | The prisoners' ledger. |
| 725 | 1 | `public java.util.List<Household> cells()` | Every cell, in the fixed order. |
| 727 | 1 | `public int cellCount()` |  |
| 729 | 3 | `public void forEach(Consumer<Household> action)` |  |
| 734 | 5 | `public double sum(ToDoubleFunction<Household> figure)` | Adds a figure up across every cell. |
| 741 | 5 | `public double sumRow(int row, ToDoubleFunction<Household> figure)` | ...or across one row: a tier's cells, or the retired. |
| 748 | 3 | `public double rowHouseholds(int row)` | Households in the row: the denominator of every per-household row figure. |
| 753 | 9 | `private double perHousehold(int row, ToDoubleFunction<Household> perCell)` | A row total, per household of the row. |

### THE MONTH (lines 763-1127)

| line | len | member | says |
|---:|---:|---|---|
| 796 | 262 | `public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, ...` | months owed sit on (Bank.householdRate() since 0.7.7; the city's own borrowing rate before it) |
| 1064 | 5 | `private static double[] padRows(double[] rows)` | A seven-row array - the tiers and the retired, from a caller written before the people outside the families had rows - padded with empty rows. |
| 1071 | 16 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Who lives in each cell now, in cell order. |
| 1098 | 13 | `private double[] splitIncome(double[] fresh, double[] rowTotal)` | A row's take-home, per household of each of its cells. |
| 1113 | 14 | `private double[] splitByPeople(double[] fresh, double[] rowTotal)` | A row's fees, per household of each of its cells, by the people in them. |

### THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS (lines 1128-1349)

| line | len | member | says |
|---:|---:|---|---|
| 1172 | 4 | **type** `private interface Stock` | One stock a household carries, for followThePeople() to move. |
| 1173 | 1 | `double get(Household c)` _(in HouseholdBalance.Stock)_ |  |
| 1174 | 1 | `void set(Household c, double perHousehold)` _(in HouseholdBalance.Stock)_ |  |
| 1184 | 80 | `private java.util.List<Stock> stocks(int[] named)` | Every stock a household carries, in the order followThePeople() moves them and foldEmptyCells() folds them. |
| 1275 | 74 | `private void followThePeople(double[] fresh, double[] buffer)` | Moves every stock with the people, then sets every cell's count. |

### A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) (lines 1350-1644)

| line | len | member | says |
|---:|---:|---|---|
| 1396 | 1 | `int foldEmptyCells()` | Empties every cell under EMPTY_CELL into the cells that are not. |
| 1398 | 22 | `private int foldEmptyCells(java.util.List<Stock> stocks)` |  |
| 1428 | 23 | `private boolean spread(Stock s, double amount, int group)` | One stock's amount onto the cells that are not empty, in one stock group (or the whole city, for -1): pro rata to what each holds of it, or by households when none holds any. |
| 1453 | 4 | `boolean holdsAnything(Household c)` | True when the cell holds any stock at all, which an empty cell must not. |
| 1459 | 1 | `public int getCellsFolded()` | Cells emptied this month that still held something when the census left them under EMPTY_CELL. |
| 1475 | 27 | `private double[] carryGraduatesLoans(double[] before)` | The graduates' student loans, handed to the working families before the census moves anything. |
| 1515 | 125 | `private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival)` | One stock through the pool: released by the cells that shrank at their own average, claimed by the cells that grew - within the row first, then across the city - and what nobody claimed returned. |
| 1643 | 1 | `public double getSharesTakenAway(int company)` |  |

### THE CARS (2026-09-16) (lines 1645-1723)

### AND THEY BORROW FOR IT (2026-09-17) (lines 1724-1808)

### THE SECOND-HAND MARKET (2026-09-17) (lines 1809-1982)

| line | len | member | says |
|---:|---:|---|---|
| 1883 | 1 | `public double getCarsTakenAway()` |  |
| 1886 | 1 | `public double totalCars()` | Every car in the city. |
| 1899 | 7 | `public double[] commuteWorkersByRow()` | The working cells' commuters by row: households times grown-ups. |
| 1908 | 9 | `public double[] commuteCarsByRow()` | ...and the cars they hold by row, at most one per grown-up: households times the lesser of the two. |
| 1919 | 6 | `public double captiveShare()` | The share of those commuters with no car of their own; 1 with no commuters. |
| 1927 | 4 | `public double carsPerHousehold()` | Cars per household, 0 to 1 - what the road reads. |
| 1942 | 12 | `public double wearOutCars()` | Fifteen years on, every car in the city is scrapped. |
| 1976 | 6 | `public double carsWanted(double price, double ceiling)` | What the households would buy this month at this price, before the market says how many there are. |

### WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) (lines 1983-2138)

| line | len | member | says |
|---:|---:|---|---|
| 2015 | 49 | `private double wantOf(int i, double price, double ceiling)` | What one cell would buy this month, in whole cars. |
| 2078 | 60 | `public double takeCars(double units, double price, double ceiling)` | Hands out the cars the market actually had, pro rata over who wanted them, and takes the money out of savings. |

### the second-hand market (lines 2139-2203)

| line | len | member | says |
|---:|---:|---|---|
| 2190 | 13 | `private double offerOf(int i, double floorPrice)` | What one cell would put up for sale this month, in whole cars. |

### GROCERIES AT A PRICE, AND FOOD ASSISTANCE (0.7.43) (lines 2204-2393)

| line | len | member | says |
|---:|---:|---|---|
| 2268 | 1 | `public void setSatiationPrice(double price)` |  |
| 2269 | 1 | `public double getSatiationPrice()` |  |
| 2271 | 3 | `public void setFoodAssistance(double share)` |  |
| 2274 | 1 | `public double getFoodAssistance()` |  |
| 2277 | 1 | `public double getFoodAssistancePaid()` | What the treasury paid toward the groceries at the last sale: every voucher, up to the baskets got at the price charged. |
| 2280 | 1 | `public double getFedByAssistance()` | ...and the baskets that bought, at the price charged. |
| 2287 | 8 | `public double groceryDemandOf(Household c, double price)` | What one of these households would buy at this price, in baskets: the curve in the banner. |
| 2297 | 5 | `public double groceriesWanted(double price)` | What the city's households would buy at this price, in baskets: every cell's demand times its households. |
| 2304 | 5 | `public double groceriesNeeded()` | The baskets the city's households need, one a head: what the demand is a share of. |
| 2325 | 18 | `public void allocateGroceries(double sold, double pAlloc, double pCharged)` | Hands out the baskets the shops sold, and pays the vouchers on them. |
| 2350 | 3 | `public double foodAssistanceFor(Household c, double price)` | The voucher one of these households holds at this shelf price: the dial's share of its baskets at the price, when they would take more than FOOD_ASSISTANCE_MEANS_SHARE of what it has after its fixed bills and its inve... |
| 2355 | 8 | `double foodAssistanceFor(Household c, double price, double share)` | ...at a dial of the caller's: the Policy tab's preview (PolicyPreview.foodAssistanceAt()). |
| 2371 | 8 | `public double foodAssistanceAt(double share, double price)` | What the vouchers would have paid at the last sale at another dial: each eligible household's voucher at that share, up to the baskets it got at the price - the same rule allocateGroceries() pays by, against the baske... |
| 2381 | 5 | `public double householdsEligibleForFoodAssistance(double price)` | Households whose baskets would take more than FOOD_ASSISTANCE_MEANS_SHARE of their means at this price: who a voucher would go to. |
| 2388 | 5 | `public double[] foodAssistanceByRow()` | What the treasury paid toward each row's groceries at the last sale, row by row: the cells that were paid, summed. |

### who goes without, read for the screens (0.7.45) (lines 2394-2438)

| line | len | member | says |
|---:|---:|---|---|
| 2400 | 7 | **type** `public record GroceryRow(int row, double need, double asked, double got, double households, double eligible...` | The baskets one row of households needed at the last sale, asked for at the price, and got; its households, those a voucher would go to at a price, and those a voucher was paid to. |
| 2403 | 1 | `public double gotShare()` _(in HouseholdBalance.GroceryRow)_ | Baskets got per basket needed: 1 is fed. |
| 2405 | 1 | `public double askedShare()` _(in HouseholdBalance.GroceryRow)_ | Baskets asked for at the price per basket needed: under 1 is priced out. |
| 2409 | 17 | `public GroceryRow[] groceriesByRow(double price)` | Every row's groceries at the last sale, with who a voucher would go to at this price (ROWS order). |
| 2428 | 5 | `public double getHouseholdsAssisted()` | The households a voucher was paid to at the last sale. |
| 2435 | 3 | `public double foodAssistanceBasketsAt(double share, double price)` | The baskets the vouchers would have paid for at the last sale at another dial: foodAssistanceAt() at the price, in baskets. |

### THE LUXURY COUNTER (2026-09-17) (lines 2439-2539)

| line | len | member | says |
|---:|---:|---|---|
| 2466 | 4 | `public double luxuriesWanted(double price)` | What the city's households would buy at this price, in whole pieces. |
| 2472 | 5 | `private double luxuryBudget()` | What the city's households would put over a counter, in money. |
| 2488 | 7 | `private double luxuryBudgetOf(Household c)` | What one cell would buy, in whole pieces. |
| 2509 | 30 | `public double takeLuxuries(double units, double price)` | Hands out the pieces the shops actually had, pro rata over who wanted them, and takes the money out of savings. |

### THE TABLE (2026-09-18) (lines 2540-2824)

| line | len | member | says |
|---:|---:|---|---|
| 2558 | 16 | `public double mealsWanted(double price)` | What the city's households would eat out at this price, in whole meals. |
| 2576 | 5 | `private double mealBudget()` | What the city's households would put on a table, in money. |
| 2595 | 7 | `private double mealBudgetOf(Household c)` | What one cell would spend eating out. |
| 2614 | 28 | `public double takeMeals(double meals, double price)` | Hands out the meals the kitchens actually served, pro rata over who wanted them, takes the money out of savings and REMEMBERS THE MEALS. |
| 2646 | 1 | `public double getMealsEaten()` | Meals the city ate out in the month the hunger measure just read. |
| 2649 | 1 | `public double getMealsBought()` | Meals the households ate out this month. |
| 2652 | 1 | `public double getMealSpend()` | ...and what they paid for them. |
| 2657 | 1 | `public double getLuxuriesBought()` | Pieces the households took this month. |
| 2660 | 1 | `public double getLuxurySpend()` | ...and what they paid for them. |
| 2663 | 1 | `public double getLuxuryWant()` | What every household in the city would like to put over a counter this month. |
| 2666 | 5 | `public double carsOffered(double floorPrice)` | What the city's households would put up for sale at this floor. |
| 2694 | 72 | `public double clearUsedCars(double newPrice, double ceiling)` | Clears the month's second-hand market: strikes a price, moves the cars that find a buyer, and pays the households that sold them. |
| 2771 | 1 | `public double getUsedCarsOffered()` | Cars put up for sale this month, whether or not anybody took them. |
| 2774 | 1 | `public double getUsedCarsTraded()` | ...and the ones that found a buyer. |
| 2777 | 1 | `public double getUsedCarPrice()` | What they went for. |
| 2790 | 3 | `public double getUsedCarShare()` | ...as a share of what a NEW one cost the month it was struck, which is the only honest denominator and is between USED_CAR_FLOOR and USED_CAR_CEILING by construction. |
| 2795 | 1 | `public double getUsedCarSpend()` | What the buyers paid, all in. |
| 2801 | 1 | `public double getUsedCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month. |
| 2804 | 1 | `public double getUsedCarLoanFees()` | ...and the bank's fee on that, added to what the buyers owe (0.7.7). |
| 2811 | 1 | `public double getCarsBought()` | Cars the households actually took this month. |
| 2818 | 1 | `public double getCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month or the money audit sees a pool fall for no reason. |
| 2821 | 1 | `public double getCarLoanFees()` | ...and the bank's fee on it, added to what the buyers owe rather than paid (0.7.7). |

### THE MARKET (lines 2825-2893)

| line | len | member | says |
|---:|---:|---|---|
| 2838 | 6 | `public void setMarket(Exchange exchange, Equity register, Bank bank)` |  |
| 2850 | 15 | `private void rebuildLiquidity()` | The waterfall's two sales, in one object: the city's paper to the bank's desk first (0.7.1), then shares on the exchange. |
| 2872 | 1 | `Household[] cellsForMarket()` | The cells, for the exchange and the bond market to post their orders from (0.7.12 round 2: each household type is its own participant). |
| 2875 | 5 | `public void splitShares(int company, double k)` | A split or consolidation: every household's count of the company by the factor. |
| 2882 | 4 | `public double marketValueOfShares()` | What the households' shares are worth at the exchange's price - the last trade on each book, or fair value before one. |
| 2888 | 5 | `public double totalSold()` | What the households raised this month selling shares to cover the shop. |

### THE WORLD'S PAPER (lines 2894-2911)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) (lines 2912-3098)

| line | len | member | says |
|---:|---:|---|---|
| 2948 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |
| 2968 | 1 | `private boolean owes(Household c)` | WHETHER A CELL OWES ANYTHING, READ AT THE SAME FLOOR (0.7.12 round 6). |
| 2973 | 3 | `public void setExchangeRate(double localPerUsd)` |  |
| 2977 | 1 | `public double getExchangeRate()` |  |
| 2994 | 70 | `public void investAbroad(double depositRate, double worldRate, double rate)` | Every household decides where to keep its idle money: what is past the cushion goes abroad towards the target share, a little a month; what is abroad comes home when the target falls. |
| 3066 | 5 | `public double totalAbroadUsd()` | Dollars every household holds abroad. |
| 3073 | 1 | `public double totalAbroadValue()` | ...worth this much at home, at the month's rate. |
| 3076 | 5 | `public double getSentAbroad()` | Sent abroad this month, all households, local money. |
| 3083 | 5 | `public double getBroughtHome()` | Brought home this month - to keep to the target or to eat - all households, local money. |
| 3090 | 5 | `public double getForeignInterest()` | What the world paid the households this month, rolled abroad. |
| 3097 | 1 | `public double getAbroadTakenAway()` | Dollars that left with the households that left this month. |

### THE CITY'S PAPER, AT HOME (0.7.1) (lines 3099-3349)

| line | len | member | says |
|---:|---:|---|---|
| 3149 | 4 | **type** `public interface PaperDesk` | Where the households' paper is sold, for what, and what it does to the paper on the other side. |
| 3151 | 1 | `void buy(double face, double cash)` _(in HouseholdBalance.PaperDesk)_ | The desk buys this much face of the households' paper for this much cash. |
| 3156 | 4 | `public void setPaperDesk(PaperDesk desk)` |  |
| 3171 | 3 | `public void setPaperRatio(double ratio)` |  |
| 3175 | 1 | `public double getPaperRatio()` |  |
| 3188 | 3 | `public void setSpendFactor(double factor)` |  |
| 3193 | 1 | `public double getSpendFactor()` | The factor the last plan was struck at. |
| 3196 | 1 | `public double totalPaper()` | Every cell's paper, at face - DebtManager.householdPrincipal() from the other side. |
| 3199 | 1 | `public double marketValueOfPaper()` | ...at this month's market value. |
| 3202 | 5 | `public double totalPaperSold()` | Sold to the desk this month, all households, in cash. |
| 3209 | 5 | `public double totalPaperIncome()` | Coupons and principal the city paid the households on their paper this month. |
| 3215 | 3 | `private boolean mayBuyPaper(Household c)` |  |
| 3219 | 3 | `private double spareFor(Household c)` |  |
| 3224 | 4 | `public static double paperShareAt(double yield, double depositRate)` | The share of an issue yielding this much the households would take, before asking whether they can pay. |
| 3230 | 5 | `public double spareForPaper()` | What every eligible household could put into paper: savings past the cushion, in total. |
| 3242 | 17 | `public double buyAtIssue(double cash, double facePerCash)` | The households buy at issue: this much cash in total, from each eligible cell pro rata to what is past its cushion, for this much face per dollar. |
| 3261 | 3 | `private double paperWeight(Household c, double total)` | Pro rata to the paper held: what a dollar of face in the households' book is spread as, cell by cell. |
| 3266 | 13 | `public void creditPaperCoupon(double total)` | A coupon on the households' share, into savings and the month's investment income, pro rata to the paper. |
| 3281 | 13 | `public void creditPaperPrincipal(double face)` | Principal on their share, into savings with the paper down by the same, pro rata. |
| 3302 | 10 | `public void creditPaperBuyback(double face, double price)` | A bond bought back: this much of their face off the paper, this much of the price into savings, pro rata. |
| 3314 | 9 | `private double sellPaperToDesk(Household c, double needPer)` | One cell sells to the desk for up to needPer a household. |
| 3333 | 16 | `public double sellPaperForSpread(double bookYield, double depositRate)` | THE SPREAD HAS GONE: when the households' paper yields no more than the bank pays savers, the target for it is nothing, and they sell a little a month - HOME_SPEED of it, the pace the dollars abroad come home at - int... |

### THE BUSINESSES' BONDS, AT HOME (0.7.12) (lines 3350-3608)

| line | len | member | says |
|---:|---:|---|---|
| 3367 | 4 | `public void setBondMarket(BondMarket market)` |  |
| 3380 | 3 | `public void setBondRatio(double ratio)` |  |
| 3384 | 1 | `public double getBondRatio()` |  |
| 3388 | 1 | `public double getBondsTakenAway()` |  |
| 3391 | 1 | `public double totalBonds()` | Every cell's bonds, at face: the households' face in every bond. |
| 3394 | 1 | `public double marketValueOfBonds()` | ...at this month's value. |
| 3397 | 5 | `public double totalBondsSold()` | Sold this month, in cash, and the coupons and principal received. |
| 3403 | 5 | `public double totalBondIncome()` |  |
| 3410 | 1 | `public double getBonds(int row)` | One row's bonds, per household of the row. |
| 3413 | 5 | `public double bondFaceHeld(int id)` | Every cell's face in one bond, together. |
| 3420 | 7 | `public Household cellByKey(String key)` | The cell with this key, or null - from an index built once, because the markets ask on every fill. |
| 3430 | 1 | `public double bondSpare()` | What every eligible household could put into bonds: savings past the cushion - the city's paper's rule. |
| 3433 | 1 | `public double bondSpare(Household c)` | ...one cell's, every household of it: nothing for a cell that may not buy (the city's paper's eligibility). |
| 3442 | 19 | `public double payForBonds(int id, double cash, double face)` | A new issue's households' part, at issue: this much cash out of every eligible cell's savings past its cushion, pro rata, for this much face of the bond, which each cell holds in the same proportion. |
| 3470 | 26 | `public void creditBondCoupons(java.util.Map<String, Double> dueByCell)` | THE MONTH'S COUPONS, to the cells that held the bonds at the record date (BondMarket.strikeCoupons()): into savings and the month's investment income. |
| 3498 | 13 | `public double creditBondPrincipal(int id)` | One bond's principal at maturity: every cell paid its own face of it, into savings, and the bond gone from it. |
| 3518 | 14 | `public void writeDownBonds(java.util.Collection<Integer> ids, double keep)` | A default on one issuer's bonds together: every cell's face of each down to this share, and its total recounted once - a slice writes an issuer's bonds down every month it is past the curve's floor, and a recount per ... |
| 3534 | 12 | `public double writeDownBonds(int id, double keep)` | A default on one bond: every cell's face of it down to this share. |
| 3550 | 10 | `public java.util.Map<String, java.util.Map<String, Double>> bondsByCellToSave()` | Every cell's bonds, per household, by the cell's key and the bond's id: the save's (0.7.12 round 2). |
| 3566 | 16 | `public void restoreBondsByCell(java.util.Map<String, java.util.Map<String, Double>> saved)` | ...back, by name: a cell this build does not have is dropped, as the cell arrays' rule is, and a bond id that does not parse is skipped. |
| 3593 | 15 | `public void claimPooledBonds(java.util.Map<Integer, Double> poolById)` | A SAVE FROM ROUND 1 held the households' bonds as one pool, each cell with a claim on it at face (Household.bonds): the pool is handed to the cells by those claims - every bond's households' face split across the cell... |

### the people outside the families (lines 3609-3683)

| line | len | member | says |
|---:|---:|---|---|
| 3612 | 1 | `public double getEvicted()` | Out-of-work households that lost their home this month. |
| 3615 | 1 | `public double totalStudentBorrowed()` | Student loans drawn this month, all students - the treasury's money out. |
| 3618 | 1 | `public double totalStudentRepaid()` | ...and repaid by graduates, the treasury's money back: principal, which is what the journal's line carries. |
| 3621 | 1 | `public double totalStudentInterest()` | ...and the interest the graduates paid on top of it this month: the treasury's revenue. |
| 3624 | 3 | `public double studentInterestAt(double annualRate)` | What a month's interest would come to at an annual rate, on the balances that are charged it - for a screen previewing a rate. |
| 3629 | 1 | `public double totalStudentDebt()` | What every household owes the treasury in student loans. |
| 3632 | 3 | `public double totalGraduateDebt()` | ...and the part of it that is in repayment: the graduates' balances, which the rate is charged on. |
| 3637 | 1 | `public double getStudentDebtTakenAway()` | Student loans that left the city with graduates who left. |
| 3658 | 1 | `public void setGraduates(double people)` | THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS. |
| 3661 | 1 | `public double getLastGraduated()` | Students who left the student body with what they carried, at the last month's census. |
| 3664 | 1 | `public double getGraduating()` | The students who finished and wait for the next census to carry their loans (setGraduates()). |
| 3675 | 1 | `public double[] graduatesToSave()` | ...AND BOTH ACROSS A SAVE (0.7.63). |
| 3678 | 5 | `public void restoreGraduates(double[] saved)` | ...and back; null, a save from before 0.7.63, leaves both at nothing, as every load until then did. |

### what the bank is owed (lines 3684-3760)

| line | len | member | says |
|---:|---:|---|---|
| 3687 | 1 | `public double getWrittenOff()` | Written off this month, which is the bank's loss. |
| 3690 | 1 | `public double getTakenAway()` | Savings that left the city with the households that left. |
| 3693 | 1 | `public double getBankrupt(int row)` | Households discharged this month, and the share of them leaving the city. |
| 3694 | 1 | `public double getLeavingCity()` |  |
| 3697 | 1 | `public boolean isLockedOut(int row)` | True when any cell of the row the bank has stopped lending to is still locked out. |
| 3700 | 7 | `public int getLockout(int row)` | The longest lockout standing in the row. |
| 3709 | 1 | `public double bookOwed()` | Everything the households owe the bank. |
| 3727 | 33 | `public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, doub...` | The plan alone, without settling a month. |

### reading (lines 3761-3861)

| line | len | member | says |
|---:|---:|---|---|
| 3764 | 1 | `public double getSpendingCapacity()` | What the shops can sell next month, in money. |
| 3767 | 1 | `public double getWantedSpend()` | What they would spend if money were no object - the demand behind the cap. |
| 3770 | 1 | `public double getSubsistence(int row)` | One basket a head, per household of the row: what going short is measured against. |
| 3783 | 12 | `public double[] plannedShare()` | The share of each row's groceries, for splitting what retail actually sold back across the rows. |
| 3797 | 3 | `public double getHungerRate()` | Share of the city going short of food, 0-1. |
| 3801 | 1 | `public double getHungryPeople()` |  |
| 3809 | 3 | `public double getHungerPricedOut()` | Of getHungerRate(), the share of the city short of a basket that could not afford one at the price - the money half, at full shelves - and the share the shelves left short (0.7.45): the two add to the rate. |
| 3814 | 1 | `public double getHungerShortOfStock()` | ...and the rest of it: the shelves ran short. |
| 3825 | 1 | `public double getHungryAtFullShelves()` | Of getHungryPeople(), the ones who would have gone hungry even with the shelves full: the baskets they asked for at the price (0.7.43; their own plan until then), every one handed over, came up short of a basket - the... |
| 3837 | 1 | `public double getDeliveredShare()` | The share of what households asked for at the price (0.7.43; what they planned to buy until then) that the shops could hand over - and therefore which of the two hungers is biting. |
| 3850 | 11 | `public void creditDepositInterest(double total)` | Interest the bank paid on what these households have saved. |

### THEFT (2026-09-11) (lines 3862-3921)

| line | len | member | says |
|---:|---:|---|---|
| 3881 | 16 | `public double takeFromSavings(double total)` | Takes up to `total` from the households' savings, each cell in proportion to what it has saved. |
| 3904 | 14 | `public double creditByWeight(double total, double[] weight)` | Credits `total` to the cells in proportion to a weight per cell, in the cells' order - what the offenders' households took home. |
| 3920 | 1 | `public double getDepositInterest()` | What the bank paid the city's savers this month. |

### THE OFFER (lines 3922-4146)

| line | len | member | says |
|---:|---:|---|---|
| 3954 | 28 | `public double subscribe(int company, double offered, double price)` | Puts an offering to every household, and takes up what they will buy. |
| 3993 | 10 | `public void grantFounders(int company, double shares)` | Hands out a company's founding shares to the people who founded it. |
| 4009 | 15 | `public double creditDividend(int company, double perShare)` | Pays every household its dividend, straight into its savings. |
| 4035 | 6 | `public double surrenderShares(int company)` | EVERY CELL GIVES UP ITS SHARES OF ONE COMPANY, for nothing: a failed bank's resolution (0.7.14; Equity.takeAllForCity() - the old owners are wiped out, CDIC). |
| 4043 | 5 | `public double sharesHeld(int company)` | Shares of this company the city's households hold between them. |
| 4050 | 5 | `public double totalDividends()` | What the households were paid in dividends this month. |
| 4058 | 1 | `public double getSavings(int row)` | ---- the row, per household of it: what the screens and the fixtures read ---- |
| 4061 | 1 | `public double getHouseholds(int row)` | Households this row was struck for - the multiplier on every per-row figure. |
| 4062 | 1 | `public double getDebt(int row)` |  |
| 4063 | 1 | `public double getAfterFixed(int row)` |  |
| 4065 | 1 | `public double getDisposable(int row)` | What one household of the row had to spend this month, from its own ledger (0.7.27: Policy's pension lines foot on it). |
| 4067 | 1 | `public double getFixedCosts(int row)` | ...and its rent, fees and interest: what came off that before the shop - the ledger's own, so the three foot. |
| 4068 | 1 | `public double getInterest(int row)` |  |
| 4084 | 14 | `public double disposableWithRowMoved(int row, double rowChange)` | What one household of a row would have had to spend with the row's income moved by `rowChange` (0.7.36, the Policy spec's M11): the move split across the row's cells by advanceMonth()'s own rule - splitIncome(), by ea... |
| 4098 | 1 | `public double getDrawn(int row)` |  |
| 4101 | 1 | `public double getUnfunded(int row)` | What this row wanted, could not fund, and did not get. |
| 4104 | 4 | `public boolean isCutOff(int row)` | True when the bank has stopped lending to any cell of this row - ceiling or lockout. |
| 4108 | 1 | `public double getBorrowed(int row)` |  |
| 4109 | 1 | `public double getRepaid(int row)` |  |
| 4110 | 1 | `public double getBanked(int row)` |  |
| 4111 | 1 | `public double getWant(int row)` |  |
| 4112 | 1 | `public double getPlanned(int row)` |  |
| 4113 | 1 | `public double getRate(int row)` |  |
| 4116 | 4 | `public boolean isGoingShort(int row)` | True when this row is buying less food than it wants. |
| 4122 | 1 | `public double totalSavings()` | City totals, for the headline lines on the screen. |
| 4123 | 1 | `public double totalDebt()` |  |
| 4124 | 1 | `public double totalInterest()` |  |
| 4133 | 4 | `public double averageRate()` | What the families' credit lines cost them this month on average: each cell's rate weighted by what it owes - the bank's household rate plus RISK_SLOPE for every month of income owed, capped at MAX_RATE (Household.sett... |
| 4139 | 1 | `public double totalBorrowed()` | New lending to families this month - the bank's money out the door. |
| 4142 | 1 | `public double totalRepaid()` | ...and what came back. |
| 4145 | 1 | `public double totalNetWorth()` | What every household in the city has, less what it owes. |

### saving (lines 4147-4642)

| line | len | member | says |
|---:|---:|---|---|
| 4167 | 44 | `public double[] toSaveArray()` | The row array an older build reads: ROWS*8+5 since 0.7.27 (ROWS*8+3 before), per household of the row. |
| 4304 | 5 | `public String[] cellKeys()` | The name of every cell, in the order toCellSaveArray() writes them. |
| 4311 | 33 | `public double[] toCellSaveArray()` | CELL_SLOTS per cell, in cellKeys() order, then the three city figures. |
| 4356 | 143 | `public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies)` | Puts the cells back, by name. |
| 4515 | 53 | `public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Puts a ROW array back, seeding every cell of the row with the row's position: the save from a build that kept the stocks per tier. |
| 4577 | 8 | `private void seedGroceries(Household c)` | The last sale's baskets for a household from a save before 0.7.43, which carries the money measure its hunger was struck on: its plan times the share the shops handed over, against subsistence - turned into baskets by... |
| 4587 | 1 | `public void restore(double[] saved)` | The row array alone, with no census: the cells wait for the plan to count them. |
| 4589 | 29 | `public void reset()` |  |
| 4625 | 16 | `public void redenominate(double scale)` | The households' stocks and this month's working, in the new unit. |


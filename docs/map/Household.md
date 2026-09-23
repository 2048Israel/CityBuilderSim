# Household.java - 1,153 lines · 96 methods · 8 constants · model

`ham/citybuildersim/Household.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> Every household of one shape at one pay tier, as one ledger.
> 
> ==================== WHY A CLASS AND NOT A ROW ====================
> 
> Jerus, 2026-09-10: "I need it per household and pay tier type... create an
> object, being the basic, and then each extends... that way it's a lot easier
> to sum everything up, or modify across all."
> 
> Before this the households' money was seven rows - one per pay tier and one
> for the retired - so an unskilled single adult and an unskilled large family
> drew on the same savings and owed the same debt, and the screen admitted it by
> giving every shape in a tier the tier's position. The two households whose
> money differs most were the same number.
> 
> This is one CELL of the family matrix with its own books: how many households
> it holds, what each of them has saved and owes, whether the bank has stopped
> lending to them, and what last month did to them. Sixty-eight of them - eleven
> working shapes across six tiers, and two retired shapes with no tier - and
> HouseholdBalance is the thing that holds them all, sums them, and strikes them
> every month.
> 
> THE BASE CLASS IS THE LEDGER. Everything a household has or owes lives here,
> so anything that has to be summed across the city, or changed for everybody at
> once - a new stock, a redenomination, a stock that has to follow people when
> they change shape - is written once and applies to every cell. The subclasses
> say only what differs: where the income comes from, and who in the household
> carries the money.
> 
> ==================== PER HOUSEHOLD, NOT PER CELL ====================
> 
> Every money figure here is PER HOUSEHOLD of the cell, in the game's
> thousands, and the cell's total is that figure times `households`. The reason
> is the same one HouseholdBalance has always given: a total would be diluted
> every month the city grew, and a screen would show a city getting poorer for
> growing. A newcomer arrives with what a household like theirs has.
> 
> ==================== THE CELL SURVIVES THE REBUILD ====================
> 
> FamilyModel re-allocates every household from scratch each month - "nobody
> keeps the family they had last month" - which was the argument for keeping
> the stocks per tier rather than per cell. It is answered by HouseholdBalance
> .followThePeople(): when a cell loses households, their money goes into a
> pool, and the cells that gain draw on it, so a child ageing into a teen moves
> a family's savings from one cell to the next rather than losing them. Nothing
> in this class needs to know that; it just carries the position.
> 
> ==================== ADDING A STOCK ====================
> 
> A future stock - savings held abroad, a pension pot - is a field here, a
> line in redenominate(), a slot in HouseholdBalance's cell save array, and
> an entry in followThePeople()'s list so it moves with the people. Four
> places, all of them named, none of them in a screen. The shares in the
> city's companies were the first to go in that way, the same evening.

**Uses:** [HouseholdBalance](HouseholdBalance.md) (19), [FamilyStructure](FamilyStructure.md) (3), [PayTier](PayTier.md) (2), [Equity](Equity.md) (1)

**Used by (27):** [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [Offending](Offending.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PrisonerHousehold](PrisonerHousehold.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [SaveFileCheck](SaveFileCheck.md), [ShadowBasket](ShadowBasket.md), [StudentHousehold](StudentHousehold.md), [UnemployedHousehold](UnemployedHousehold.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 96 | THE LOAN'S RATE (2026-09-21) |
| 133 | · the position |
| 284 | WHO CAN AFFORD THE CLINIC (2026-09-19) |
| 375 | · last month's working, per household |
| 438 | · what differs |
| 497 | · reading |
| 504 | A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) |
| 662 | · the month |
| 693 | · · what the lender charges this one |
| 700 | · · the bills, in order |
| 709 | · · settle what they actually spent |
| 850 | · AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 878 | · AND THE FORTUNE ALSO ASKS FOR WATCHES (2026-09-17) |
| 913 | · AND WHAT IT WOULD SPEND EATING OUT (2026-09-18) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `Household.RETIRED_ROW` | `PayTier.values().length` | The row the retired sum into, after the six tiers. |
| 71 | `Household.UNEMPLOYED_ROW` | `RETIRED_ROW + 1` | The out of work: on EI, off it, and those who have lost their home. |
| 73 | `Household.STUDENT_ROW` | `RETIRED_ROW + 2` | Full-time students, living on a grant, their savings and a student loan. |
| 75 | `Household.ORPHAN_ROW` | `RETIRED_ROW + 3` | Children no family holds, by band. |
| 81 | `Household.PRISON_ROW` | `RETIRED_ROW + 4` | Adults serving a sentence (2026-09-11). |
| 83 | `Household.ROWS` | `RETIRED_ROW + 5` | Every row: the six tiers, the retired, and the four above. |
| 85 | `Household.ROWS_BEFORE_PRISON` | `ROWS - 1` | The rows a save from before the prisons carries. |
| 94 | `Household.STUDENT_LOAN_MONTHS` | `114` | How long a graduate takes to repay a student loan, in months: nine and a half years, the Canada Student Loan standard term (Alberta Student Aid's repayment page; the six-month grace is not modelled). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 131 | `protected final FamilyStructure shape` | The family shape, or null for a household that is not one: the unemployed, a student, an orphan. |
| 139 | `double savings` | What one of these households has in the bank. |
| 142 | `double debt` | ...and what it owes the bank on its revolving credit. |
| 161 | `double investmentIncome` | Dividends and foreign coupons received since this cell last planned a month. |
| 164 | `int lockout` | Months this cell cannot borrow, after a discharge. |
| 173 | `double studentDebt` | What one of these households owes the treasury on student loans. |
| 185 | `final double[] shares` | Shares held in each of the city's companies, per household of the cell, indexed as Equity.COMPANIES. |
| 188 | `double dividends` | What the shares paid this month, per household. |
| 203 | `double abroad` | DOLLARS held abroad by one of these households: the world's paper, bought with savings past the cushion when the world pays more than the bank, sold when the bank pays more or the household needs the money. |
| 218 | `double paper` | THE CITY'S OWN PAPER, AT HOME (0.7.1): face held, per household of the cell, in local money - the fourth asset beside the bank balance, the shares and the dollars abroad. |
| 243 | `double cars` | CARS THIS HOUSEHOLD OWNS, per household of the cell, 0 to 1 (2026-09-16). |
| 249 | `double luxuryWant` | What this household would spend on luxuries this month, per household. |
| 255 | `double mealWant` | ...and what it would spend eating out, per household. |
| 280 | `double mealsEaten` | MEALS this household ate out last month, per household. |
| 321 | `double carePaid` | Of this household's people, the share who paid for care at the last strike: 1 for all of them. |
| 324 | `double spendable` | What one of these households could fund next month: the plan's own figure, kept for the care test. |
| 327 | `double careSkipped` | The care bill this household skipped at the last strike, per household - what it ate instead. |
| 363 | `double carsSold` | ...and the ones this cell sold into the second-hand market this month, in total rather than per household. |
| 366 | `double sentAbroad, broughtHome, foreignInterest` | This month's, per household, in local money: sent abroad, brought home, and earned there (rolled, not paid home). |
| 373 | `double households` | Households this cell was last struck for - the multiplier on every per-household figure, and the count the next month's census is compared against to see who moved. |
| 382 | `double disposable` | Take-home this month: wages or pension, after tax and contributions. |
| 383 | `double afterFixed` |  |
| 384 | `double interest` |  |
| 385 | `double drawn` |  |
| 386 | `double unfunded` |  |
| 387 | `double borrowed` |  |
| 388 | `double repaid` |  |
| 389 | `double banked` |  |
| 390 | `double want` |  |
| 391 | `double planned` |  |
| 392 | `double rate` |  |
| 393 | `double subsistence` |  |
| 396 | `double bankrupt` | Households of this cell discharged this month - a count, not money. |
| 399 | `double sold` | Shares sold this month to cover the shop, per household, in cash. |
| 402 | `double paperSold, paperIncome` | The city's paper sold this month - to cover the shop or because the spread went - and its coupons and principal received, per household, in cash (0.7.1). |
| 405 | `double studentBorrowed, studentRepaid` | Student loan drawn this month, and repaid (principal), per household. |
| 408 | `double studentInterest` | Interest charged on the student loan this month, per household: paid with the instalment, and the treasury's. |
| 411 | `double studentLoanRate` | The annual rate the treasury charges a graduate on the loan: the city's one policy, told to every cell by HouseholdBalance before it settles. |
| 414 | `double evicted` | Households of this cell that lost their home this month - a count, not money. |
| 417 | `double rentShare` | What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. |
| 530 | `double dependants` | Dependants who live in one of these households but are not its shape. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1096 | **type** `public abstract class Household` | Every household of one shape at one pay tier, as one ledger. |

### THE LOAN'S RATE (2026-09-21) (lines 96-132)

### the position (lines 133-283)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 1 | `public double mealsEaten()` |  |

### WHO CAN AFFORD THE CLINIC (2026-09-19) (lines 284-374)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 1 | `public double carePaid()` |  |
| 330 | 1 | `public double spendable()` |  |
| 331 | 1 | `public double careSkipped()` |  |
| 345 | 11 | `double affordCare(double fullBill, double paidBill)` | Decides how much of its care bill this household pays, after the month is settled and the plan is struck - the rule in the banner above. |

### last month's working, per household (lines 375-437)

| line | len | member | says |
|---:|---:|---|---|
| 420 | 13 | **type** `interface Liquidity` | Somewhere a household short of money can sell shares before it borrows. |
| 422 | 1 | `double sell(Household cell, double needPer)` _(in Household.Liquidity)_ |  |
| 431 | 1 | `default double sellPaper(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the city's paper, first (0.7.1): sold to the bank's desk at the households' book ratio for exactly what is short, or everything held if that is less. |
| 434 | 3 | `protected Household(FamilyStructure shape)` |  |

### what differs (lines 438-496)

| line | len | member | says |
|---:|---:|---|---|
| 441 | 1 | `public abstract PayTier tier()` | The pay tier, or null for a household with no earner. |
| 444 | 1 | `public abstract int row()` | The row this cell sums into: the tier's index, or RETIRED_ROW. |
| 446 | 1 | `public abstract boolean isRetired()` |  |
| 458 | 1 | `public abstract int grownUps()` | Who in the household carries the money: the earners, or the pensioners. |
| 465 | 1 | `public double earningWeight()` | Who in the household the row's INCOME is split by. |
| 468 | 1 | `public boolean canBeEvicted()` | True for a cell whose households lose their home when they cannot pay for it. |
| 475 | 1 | `public int stockGroup()` | The row whose people this cell's people most often ARE, for the money to follow them: its own row, for everybody but the out of work. |
| 482 | 1 | `protected double baskets()` | Baskets of food one of these households has to buy: one a head, for everybody but a prisoner, whom the city feeds (the prisons' upkeep). |
| 488 | 1 | `protected boolean debtFrozen()` | True when the debt is frozen: no interest charged, nothing discharged, nothing borrowed. |
| 495 | 1 | `public boolean canInvest()` | True when the household decides what to do with its savings - shares, paper abroad, an offering. |

### reading (lines 497-503)

| line | len | member | says |
|---:|---:|---|---|
| 499 | 1 | `public FamilyStructure shape()` |  |
| 502 | 1 | `public int size()` | People in one of these households, as its SHAPE declares them. |

### A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) (lines 504-661)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 1 | `public double headcount()` | People in one of these households, counting anybody who came with them. |
| 536 | 1 | `public double getDependants()` | ...of whom this many live here without being its shape's own. |
| 538 | 4 | `public String label()` |  |
| 544 | 3 | `public String key()` | "COUPLE_TEEN:SKILLED", or the shape alone for the retired. |
| 548 | 1 | `public double households()` |  |
| 550 | 1 | `public boolean isEmpty()` | Under HouseholdBalance.EMPTY_CELL households: too few to strike, to hold anything or to be paid. |
| 552 | 1 | `public double people()` | People in the whole cell - its households times what each of them holds. |
| 554 | 1 | `public double studentDebt()` |  |
| 555 | 1 | `public double studentBorrowed()` |  |
| 556 | 1 | `public double studentRepaid()` |  |
| 558 | 1 | `public double studentInterest()` | The month's interest on the student loan, per household - paid on top of studentRepaid(). |
| 559 | 1 | `public double totalStudentDebt()` |  |
| 560 | 1 | `public double evicted()` |  |
| 561 | 1 | `public double rentShare()` |  |
| 564 | 1 | `public double savings()` | Per household. |
| 565 | 1 | `public double debt()` |  |
| 566 | 1 | `public int lockout()` |  |
| 569 | 1 | `public double shares(int company)` | Shares held in this company, per household. |
| 570 | 1 | `public double totalShares(int company)` |  |
| 573 | 1 | `public double dividends()` | This month's dividends, per household. |
| 576 | 1 | `public double abroad()` | Dollars held abroad, per household. |
| 578 | 1 | `public double paper()` | The city's paper held, per household, at face (0.7.1). |
| 580 | 1 | `public double paperSold()` | ...sold this month, and its coupons and principal received, per household, in cash. |
| 581 | 1 | `public double paperIncome()` |  |
| 582 | 1 | `public double cars()` |  |
| 584 | 1 | `public double totalCars()` | Every car this cell's households own between them. |
| 586 | 1 | `public double abroadValue(double localPerUsd)` | ...worth this much at home, per household, at a rate. |
| 587 | 1 | `public double sentAbroad()` |  |
| 588 | 1 | `public double broughtHome()` |  |
| 589 | 1 | `public double foreignInterest()` |  |
| 592 | 1 | `public double sold()` | Shares sold this month to cover the shop, per household, in cash. |
| 593 | 1 | `public boolean isLockedOut()` |  |
| 594 | 1 | `public double disposable()` |  |
| 595 | 1 | `public double afterFixed()` |  |
| 596 | 1 | `public double interest()` |  |
| 597 | 1 | `public double drawn()` |  |
| 598 | 1 | `public double unfunded()` |  |
| 599 | 1 | `public double borrowed()` |  |
| 600 | 1 | `public double repaid()` |  |
| 601 | 1 | `public double banked()` |  |
| 602 | 1 | `public double want()` |  |
| 603 | 1 | `public double planned()` |  |
| 604 | 1 | `public double rate()` |  |
| 605 | 1 | `public double subsistence()` |  |
| 608 | 1 | `public double luxuryWant()` | What this household would put over a luxury counter this month. |
| 611 | 1 | `public double carsSold()` | Cars this cell sold second-hand this month, in total. |
| 612 | 1 | `public double bankrupt()` |  |
| 615 | 1 | `public boolean isCutOff()` | True when the bank has stopped lending to this cell - ceiling or lockout. |
| 643 | 1 | `public boolean isGoingShort()` | Whether this household planned to spend less than it wanted to. |
| 646 | 1 | `public double totalSavings()` | The cell's totals: the per-household figure times the households. |
| 647 | 1 | `public double totalAbroad()` |  |
| 648 | 1 | `public double totalPaper()` |  |
| 649 | 1 | `public double totalDebt()` |  |
| 650 | 1 | `public double totalInterest()` |  |
| 651 | 1 | `public double totalBorrowed()` |  |
| 652 | 1 | `public double totalRepaid()` |  |
| 653 | 1 | `public double totalPlanned()` |  |
| 654 | 1 | `public double totalWant()` |  |
| 660 | 1 | `public double netWorth()` | Net worth of one of these households: what it has less what it owes. |

### the month (lines 662-1153)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 4 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` | Settles a month: the bills in order, the shop against what was actually spent, and savings, then credit, then going without. |
| 687 | 102 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` |  |
| 795 | 3 | `double plan(double localPerUsd)` | Plans the next month, without settling one. |
| 803 | 3 | `double plan(double localPerUsd, double paperRatio)` | (0.7.1): the households' book at the curve over its face |
| 814 | 149 | `double plan(double localPerUsd, double paperRatio, double spendFactor)` | household plans (0.7.3): HouseholdBalance .spendFactor() on the month's real deposit rate, 1 at no real return - see AND WHAT IT SPENDS ANSWERS THE REAL RATE below |
| 965 | 13 | `void restrike(double disposablePer, double rentPerHome, double feesPer, double foodPricePerHead, double riskFreeAnnual)` | Re-strikes the fixed part of the month for the plan alone: the load path. |
| 987 | 27 | `protected double fundShortfall(double still, double disposablePer)` | What is still short after savings, the city's paper, the paper abroad and the shares: the revolving credit line, up to its ceiling. |
| 1016 | 1 | `protected double planningRoom()` | What the plan may count on borrowing. |
| 1019 | 1 | `protected double studentRepayment()` | The month's student-loan instalment (principal), per household. |
| 1022 | 1 | `protected double studentInterestDue()` | The month's interest on the student loan at the city's rate, per household. |
| 1029 | 1 | `public double studentInterestAt(double annualRate)` | What a month's interest on the loan would be at an annual rate - the same figure studentInterestDue() charges, for a screen previewing a rate the city has not set. |
| 1032 | 5 | `public double creditRoom(double disposablePer)` | What the bank will still lend one of these: the ceiling less what is owed, or nothing. |
| 1047 | 30 | `double discharge()` | Whoever cannot carry it any more: a share of the cell discharges. |
| 1079 | 9 | `void clearWorking()` | Nothing to strike: the working is blank, the position stands. |
| 1090 | 7 | `void clearAll()` | The cell is empty: no position either. |
| 1099 | 49 | `void redenominate(double scale)` | Everything in money, in the new unit. |
| 1150 | 3 | `public String toString()` |  |


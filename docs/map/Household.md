# Household.java - 1,047 lines · 88 methods · 8 constants · model

`ham/citybuildersim/Household.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Used by (25):** [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [Offending](Offending.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PrisonerHousehold](PrisonerHousehold.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [SaveFileCheck](SaveFileCheck.md), [ShadowBasket](ShadowBasket.md), [StudentHousehold](StudentHousehold.md), [UnemployedHousehold](UnemployedHousehold.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 96 | THE LOAN'S RATE (2026-09-21) |
| 133 | · the position |
| 269 | WHO CAN AFFORD THE CLINIC (2026-09-19) |
| 359 | · last month's working, per household |
| 410 | · what differs |
| 469 | · reading |
| 476 | A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) |
| 626 | · the month |
| 657 | · · what the lender charges this one |
| 664 | · · the bills, in order |
| 673 | · · settle what they actually spent |
| 781 | · AND THE FORTUNE ALSO ASKS FOR WATCHES (2026-09-17) |
| 813 | · AND WHAT IT WOULD SPEND EATING OUT (2026-09-18) |

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
| 228 | `double cars` | CARS THIS HOUSEHOLD OWNS, per household of the cell, 0 to 1 (2026-09-16). |
| 234 | `double luxuryWant` | What this household would spend on luxuries this month, per household. |
| 240 | `double mealWant` | ...and what it would spend eating out, per household. |
| 265 | `double mealsEaten` | MEALS this household ate out last month, per household. |
| 305 | `double carePaid` | Of this household's people, the share who paid for care at the last strike: 1 for all of them. |
| 308 | `double spendable` | What one of these households could fund next month: the plan's own figure, kept for the care test. |
| 311 | `double careSkipped` | The care bill this household skipped at the last strike, per household - what it ate instead. |
| 347 | `double carsSold` | ...and the ones this cell sold into the second-hand market this month, in total rather than per household. |
| 350 | `double sentAbroad, broughtHome, foreignInterest` | This month's, per household, in local money: sent abroad, brought home, and earned there (rolled, not paid home). |
| 357 | `double households` | Households this cell was last struck for - the multiplier on every per-household figure, and the count the next month's census is compared against to see who moved. |
| 366 | `double disposable` | Take-home this month: wages or pension, after tax and contributions. |
| 367 | `double afterFixed` |  |
| 368 | `double interest` |  |
| 369 | `double drawn` |  |
| 370 | `double unfunded` |  |
| 371 | `double borrowed` |  |
| 372 | `double repaid` |  |
| 373 | `double banked` |  |
| 374 | `double want` |  |
| 375 | `double planned` |  |
| 376 | `double rate` |  |
| 377 | `double subsistence` |  |
| 380 | `double bankrupt` | Households of this cell discharged this month - a count, not money. |
| 383 | `double sold` | Shares sold this month to cover the shop, per household, in cash. |
| 386 | `double studentBorrowed, studentRepaid` | Student loan drawn this month, and repaid (principal), per household. |
| 389 | `double studentInterest` | Interest charged on the student loan this month, per household: paid with the instalment, and the treasury's. |
| 392 | `double studentLoanRate` | The annual rate the treasury charges a graduate on the loan: the city's one policy, told to every cell by HouseholdBalance before it settles. |
| 395 | `double evicted` | Households of this cell that lost their home this month - a count, not money. |
| 398 | `double rentShare` | What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. |
| 502 | `double dependants` | Dependants who live in one of these households but are not its shape. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 990 | **type** `public abstract class Household` | Every household of one shape at one pay tier, as one ledger. |

### THE LOAN'S RATE (2026-09-21) (lines 96-132)

### the position (lines 133-268)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 1 | `public double mealsEaten()` |  |

### WHO CAN AFFORD THE CLINIC (2026-09-19) (lines 269-358)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 1 | `public double carePaid()` |  |
| 314 | 1 | `public double spendable()` |  |
| 315 | 1 | `public double careSkipped()` |  |
| 329 | 11 | `double affordCare(double fullBill, double paidBill)` | Decides how much of its care bill this household pays, after the month is settled and the plan is struck - the rule in the banner above. |

### last month's working, per household (lines 359-409)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 4 | **type** `interface Liquidity` | Somewhere a household short of money can sell shares before it borrows. |
| 403 | 1 | `double sell(Household cell, double needPer)` _(in Household.Liquidity)_ |  |
| 406 | 3 | `protected Household(FamilyStructure shape)` |  |

### what differs (lines 410-468)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 1 | `public abstract PayTier tier()` | The pay tier, or null for a household with no earner. |
| 416 | 1 | `public abstract int row()` | The row this cell sums into: the tier's index, or RETIRED_ROW. |
| 418 | 1 | `public abstract boolean isRetired()` |  |
| 430 | 1 | `public abstract int grownUps()` | Who in the household carries the money: the earners, or the pensioners. |
| 437 | 1 | `public double earningWeight()` | Who in the household the row's INCOME is split by. |
| 440 | 1 | `public boolean canBeEvicted()` | True for a cell whose households lose their home when they cannot pay for it. |
| 447 | 1 | `public int stockGroup()` | The row whose people this cell's people most often ARE, for the money to follow them: its own row, for everybody but the out of work. |
| 454 | 1 | `protected double baskets()` | Baskets of food one of these households has to buy: one a head, for everybody but a prisoner, whom the city feeds (the prisons' upkeep). |
| 460 | 1 | `protected boolean debtFrozen()` | True when the debt is frozen: no interest charged, nothing discharged, nothing borrowed. |
| 467 | 1 | `public boolean canInvest()` | True when the household decides what to do with its savings - shares, paper abroad, an offering. |

### reading (lines 469-475)

| line | len | member | says |
|---:|---:|---|---|
| 471 | 1 | `public FamilyStructure shape()` |  |
| 474 | 1 | `public int size()` | People in one of these households, as its SHAPE declares them. |

### A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) (lines 476-625)

| line | len | member | says |
|---:|---:|---|---|
| 505 | 1 | `public double headcount()` | People in one of these households, counting anybody who came with them. |
| 508 | 1 | `public double getDependants()` | ...of whom this many live here without being its shape's own. |
| 510 | 4 | `public String label()` |  |
| 516 | 3 | `public String key()` | "COUPLE_TEEN:SKILLED", or the shape alone for the retired. |
| 520 | 1 | `public double households()` |  |
| 522 | 1 | `public double people()` | People in the whole cell - its households times what each of them holds. |
| 524 | 1 | `public double studentDebt()` |  |
| 525 | 1 | `public double studentBorrowed()` |  |
| 526 | 1 | `public double studentRepaid()` |  |
| 528 | 1 | `public double studentInterest()` | The month's interest on the student loan, per household - paid on top of studentRepaid(). |
| 529 | 1 | `public double totalStudentDebt()` |  |
| 530 | 1 | `public double evicted()` |  |
| 531 | 1 | `public double rentShare()` |  |
| 534 | 1 | `public double savings()` | Per household. |
| 535 | 1 | `public double debt()` |  |
| 536 | 1 | `public int lockout()` |  |
| 539 | 1 | `public double shares(int company)` | Shares held in this company, per household. |
| 540 | 1 | `public double totalShares(int company)` |  |
| 543 | 1 | `public double dividends()` | This month's dividends, per household. |
| 546 | 1 | `public double abroad()` | Dollars held abroad, per household. |
| 547 | 1 | `public double cars()` |  |
| 549 | 1 | `public double totalCars()` | Every car this cell's households own between them. |
| 551 | 1 | `public double abroadValue(double localPerUsd)` | ...worth this much at home, per household, at a rate. |
| 552 | 1 | `public double sentAbroad()` |  |
| 553 | 1 | `public double broughtHome()` |  |
| 554 | 1 | `public double foreignInterest()` |  |
| 557 | 1 | `public double sold()` | Shares sold this month to cover the shop, per household, in cash. |
| 558 | 1 | `public boolean isLockedOut()` |  |
| 559 | 1 | `public double disposable()` |  |
| 560 | 1 | `public double afterFixed()` |  |
| 561 | 1 | `public double interest()` |  |
| 562 | 1 | `public double drawn()` |  |
| 563 | 1 | `public double unfunded()` |  |
| 564 | 1 | `public double borrowed()` |  |
| 565 | 1 | `public double repaid()` |  |
| 566 | 1 | `public double banked()` |  |
| 567 | 1 | `public double want()` |  |
| 568 | 1 | `public double planned()` |  |
| 569 | 1 | `public double rate()` |  |
| 570 | 1 | `public double subsistence()` |  |
| 573 | 1 | `public double luxuryWant()` | What this household would put over a luxury counter this month. |
| 576 | 1 | `public double carsSold()` | Cars this cell sold second-hand this month, in total. |
| 577 | 1 | `public double bankrupt()` |  |
| 580 | 1 | `public boolean isCutOff()` | True when the bank has stopped lending to this cell - ceiling or lockout. |
| 608 | 1 | `public boolean isGoingShort()` | Whether this household planned to spend less than it wanted to. |
| 611 | 1 | `public double totalSavings()` | The cell's totals: the per-household figure times the households. |
| 612 | 1 | `public double totalAbroad()` |  |
| 613 | 1 | `public double totalDebt()` |  |
| 614 | 1 | `public double totalInterest()` |  |
| 615 | 1 | `public double totalBorrowed()` |  |
| 616 | 1 | `public double totalRepaid()` |  |
| 617 | 1 | `public double totalPlanned()` |  |
| 618 | 1 | `public double totalWant()` |  |
| 624 | 1 | `public double netWorth()` | Net worth of one of these households: what it has less what it owes. |

### the month (lines 626-1047)

| line | len | member | says |
|---:|---:|---|---|
| 642 | 4 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` | Settles a month: the bills in order, the shop against what was actually spent, and savings, then credit, then going without. |
| 651 | 86 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` |  |
| 743 | 119 | `double plan(double localPerUsd)` | Plans the next month, without settling one. |
| 864 | 13 | `void restrike(double disposablePer, double rentPerHome, double feesPer, double foodPricePerHead, double riskFreeAnnual)` | Re-strikes the fixed part of the month for the plan alone: the load path. |
| 885 | 27 | `protected double fundShortfall(double still, double disposablePer)` | What is still short after savings, the paper abroad and the shares: the revolving credit line, up to its ceiling. |
| 914 | 1 | `protected double planningRoom()` | What the plan may count on borrowing. |
| 917 | 1 | `protected double studentRepayment()` | The month's student-loan instalment (principal), per household. |
| 920 | 1 | `protected double studentInterestDue()` | The month's interest on the student loan at the city's rate, per household. |
| 927 | 1 | `public double studentInterestAt(double annualRate)` | What a month's interest on the loan would be at an annual rate - the same figure studentInterestDue() charges, for a screen previewing a rate the city has not set. |
| 930 | 5 | `public double creditRoom(double disposablePer)` | What the bank will still lend one of these: the ceiling less what is owed, or nothing. |
| 945 | 30 | `double discharge()` | Whoever cannot carry it any more: a share of the cell discharges. |
| 977 | 8 | `void clearWorking()` | Nothing to strike: the working is blank, the position stands. |
| 987 | 6 | `void clearAll()` | The cell is empty: no position either. |
| 995 | 47 | `void redenominate(double scale)` | Everything in money, in the new unit. |
| 1044 | 3 | `public String toString()` |  |


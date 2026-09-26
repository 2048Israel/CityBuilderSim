# Household.java - 1,339 lines · 115 methods · 8 constants · model

`ham/citybuildersim/Household.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [HouseholdBalance](HouseholdBalance.md) (17), [Bank](Bank.md) (6), [FamilyStructure](FamilyStructure.md) (3), [PayTier](PayTier.md) (2), [Equity](Equity.md) (1)

**Used by (31):** [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [Offending](Offending.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PrisonerHousehold](PrisonerHousehold.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [SaveFileCheck](SaveFileCheck.md), [ShadowBasket](ShadowBasket.md), [StudentHousehold](StudentHousehold.md), [UnemployedHousehold](UnemployedHousehold.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 96 | THE LOAN'S RATE (2026-09-21) |
| 133 | · the position |
| 344 | WHO CAN AFFORD THE CLINIC (2026-09-19) |
| 435 | · last month's working, per household |
| 518 | · what differs |
| 577 | · reading |
| 584 | A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) |
| 753 | · the month |
| 787 | · · what the lender charges this one |
| 798 | · · the bills, in order |
| 807 | · · settle what they actually spent |
| 977 | · AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 1005 | · AND THE FORTUNE ALSO ASKS FOR WATCHES (2026-09-17) |
| 1040 | · AND WHAT IT WOULD SPEND EATING OUT (2026-09-18) |

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
| 174 | `double capitalCeiling` | THE MOST THIS CELL MAY OWE THIS MONTH ON THE BANK'S CAPITAL (0.7.8), per household: what it owed when the bank's rule was set at the top of the month, grown by the month's limit - infinite while the bank lends freely. |
| 183 | `double studentDebt` | What one of these households owes the treasury on student loans. |
| 195 | `final double[] shares` | Shares held in each of the city's companies, per household of the cell, indexed as Equity.COMPANIES. |
| 198 | `double dividends` | What the shares paid this month, per household. |
| 213 | `double abroad` | DOLLARS held abroad by one of these households: the world's paper, bought with savings past the cushion when the world pays more than the bank, sold when the bank pays more or the household needs the money. |
| 228 | `double paper` | THE CITY'S OWN PAPER, AT HOME (0.7.1): face held, per household of the cell, in local money - the fourth asset beside the bank balance, the shares and the dollars abroad. |
| 248 | `double bonds` | CORPORATE BONDS (0.7.12): the face this cell holds, per household of it, in the businesses' bonds, all together - the fifth asset, beside the bank balance, the shares, the dollars abroad and the city's paper. |
| 251 | `final java.util.TreeMap<Integer, Double> bondFace` | ...bond by bond: the face per household of the cell, by the bond's id (CorporateBond.id()). |
| 303 | `double cars` | CARS THIS HOUSEHOLD OWNS, per household of the cell, 0 to 1 (2026-09-16). |
| 309 | `double luxuryWant` | What this household would spend on luxuries this month, per household. |
| 315 | `double mealWant` | ...and what it would spend eating out, per household. |
| 340 | `double mealsEaten` | MEALS this household ate out last month, per household. |
| 381 | `double carePaid` | Of this household's people, the share who paid for care at the last strike: 1 for all of them. |
| 384 | `double spendable` | What one of these households could fund next month: the plan's own figure, kept for the care test. |
| 387 | `double careSkipped` | The care bill this household skipped at the last strike, per household - what it ate instead. |
| 423 | `double carsSold` | ...and the ones this cell sold into the second-hand market this month, in total rather than per household. |
| 426 | `double sentAbroad, broughtHome, foreignInterest` | This month's, per household, in local money: sent abroad, brought home, and earned there (rolled, not paid home). |
| 433 | `double households` | Households this cell was last struck for - the multiplier on every per-household figure, and the count the next month's census is compared against to see who moved. |
| 442 | `double disposable` | Take-home this month: wages or pension, after tax and contributions. |
| 443 | `double afterFixed` |  |
| 444 | `double interest` |  |
| 445 | `double drawn` |  |
| 446 | `double unfunded` |  |
| 447 | `double borrowed` |  |
| 448 | `double repaid` |  |
| 449 | `double banked` |  |
| 450 | `double want` |  |
| 451 | `double planned` |  |
| 452 | `double rate` |  |
| 453 | `double subsistence` |  |
| 456 | `double bankrupt` | Households of this cell discharged this month - a count, not money. |
| 459 | `double sold` | Shares sold this month to cover the shop, per household, in cash. |
| 462 | `double paperSold, paperIncome` | The city's paper sold this month - to cover the shop or because the spread went - and its coupons and principal received, per household, in cash (0.7.1). |
| 465 | `double bondsSold, bondIncome` | Its bonds sold this month - in the waterfall or at the market's step - and their coupons and principal received, per household, in cash (0.7.12). |
| 468 | `double studentBorrowed, studentRepaid` | Student loan drawn this month, and repaid (principal), per household. |
| 471 | `double studentInterest` | Interest charged on the student loan this month, per household: paid with the instalment, and the treasury's. |
| 474 | `double studentLoanRate` | The annual rate the treasury charges a graduate on the loan: the city's one policy, told to every cell by HouseholdBalance before it settles. |
| 477 | `double evicted` | Households of this cell that lost their home this month - a count, not money. |
| 480 | `double accountFee` | The bank's account fee this household paid this month, with its other fixed bills (0.7.7); nothing without a home. |
| 483 | `double loanFee` | The bank's fee on what it borrowed this month, added to what it owes rather than paid (0.7.7): Bank.LOAN_FEE of the credit drawn and the cars financed. |
| 486 | `double rentShare` | What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. |
| 610 | `double dependants` | Dependants who live in one of these households but are not its shape. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1282 | **type** `public abstract class Household` | Every household of one shape at one pay tier, as one ledger. |

### THE LOAN'S RATE (2026-09-21) (lines 96-132)

### the position (lines 133-343)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 1 | `public double bondFace(int id)` | The face per household this cell holds of one bond. |
| 263 | 4 | `void setBondFace(int id, double perHousehold)` | ...set, per household, and the total with it; nothing held is not kept. |
| 269 | 3 | `void putBondFace(int id, double perHousehold)` | ...without the total, for a caller that sets many and recounts once (HouseholdBalance's coupons, principal and write-downs): the same sum, not the square of the bonds held. |
| 274 | 5 | `void recountBonds()` | The total, from the bonds themselves: after anything that moved many at once. |
| 342 | 1 | `public double mealsEaten()` |  |

### WHO CAN AFFORD THE CLINIC (2026-09-19) (lines 344-434)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 1 | `public double carePaid()` |  |
| 390 | 1 | `public double spendable()` |  |
| 391 | 1 | `public double careSkipped()` |  |
| 405 | 11 | `double affordCare(double fullBill, double paidBill)` | Decides how much of its care bill this household pays, after the month is settled and the plan is struck - the rule in the banner above. |

### last month's working, per household (lines 435-517)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 24 | **type** `interface Liquidity` | Somewhere a household short of money can sell shares before it borrows. |
| 491 | 1 | `double sell(Household cell, double needPer)` _(in Household.Liquidity)_ |  |
| 500 | 1 | `default double sellPaper(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the city's paper, first (0.7.1): sold to the bank's desk at the households' book ratio for exactly what is short, or everything held if that is less. |
| 511 | 1 | `default double sellBonds(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the bonds (0.7.12), after the paper and the dollars abroad: asked on each bond's order book at the price that makes the buyer's yield this household's borrowing rate, and filled only by what rests there at that... |
| 514 | 3 | `protected Household(FamilyStructure shape)` |  |

### what differs (lines 518-576)

| line | len | member | says |
|---:|---:|---|---|
| 521 | 1 | `public abstract PayTier tier()` | The pay tier, or null for a household with no earner. |
| 524 | 1 | `public abstract int row()` | The row this cell sums into: the tier's index, or RETIRED_ROW. |
| 526 | 1 | `public abstract boolean isRetired()` |  |
| 538 | 1 | `public abstract int grownUps()` | Who in the household carries the money: the earners, or the pensioners. |
| 545 | 1 | `public double earningWeight()` | Who in the household the row's INCOME is split by. |
| 548 | 1 | `public boolean canBeEvicted()` | True for a cell whose households lose their home when they cannot pay for it. |
| 555 | 1 | `public int stockGroup()` | The row whose people this cell's people most often ARE, for the money to follow them: its own row, for everybody but the out of work. |
| 562 | 1 | `protected double baskets()` | Baskets of food one of these households has to buy: one a head, for everybody but a prisoner, whom the city feeds (the prisons' upkeep). |
| 568 | 1 | `protected boolean debtFrozen()` | True when the debt is frozen: no interest charged, nothing discharged, nothing borrowed. |
| 575 | 1 | `public boolean canInvest()` | True when the household decides what to do with its savings - shares, paper abroad, an offering. |

### reading (lines 577-583)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 1 | `public FamilyStructure shape()` |  |
| 582 | 1 | `public int size()` | People in one of these households, as its SHAPE declares them. |

### A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) (lines 584-752)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 1 | `public double headcount()` | People in one of these households, counting anybody who came with them. |
| 616 | 1 | `public double getDependants()` | ...of whom this many live here without being its shape's own. |
| 618 | 4 | `public String label()` |  |
| 624 | 3 | `public String key()` | "COUPLE_TEEN:SKILLED", or the shape alone for the retired. |
| 628 | 1 | `public double households()` |  |
| 630 | 1 | `public boolean isEmpty()` | Under HouseholdBalance.EMPTY_CELL households: too few to strike, to hold anything or to be paid. |
| 632 | 1 | `public double people()` | People in the whole cell - its households times what each of them holds. |
| 634 | 1 | `public double studentDebt()` |  |
| 635 | 1 | `public double studentBorrowed()` |  |
| 636 | 1 | `public double studentRepaid()` |  |
| 638 | 1 | `public double studentInterest()` | The month's interest on the student loan, per household - paid on top of studentRepaid(). |
| 639 | 1 | `public double totalStudentDebt()` |  |
| 640 | 1 | `public double evicted()` |  |
| 641 | 1 | `public double rentShare()` |  |
| 644 | 1 | `public double savings()` | Per household. |
| 645 | 1 | `public double debt()` |  |
| 646 | 1 | `public int lockout()` |  |
| 649 | 1 | `public double shares(int company)` | Shares held in this company, per household. |
| 650 | 1 | `public double totalShares(int company)` |  |
| 653 | 1 | `public double dividends()` | This month's dividends, per household. |
| 656 | 1 | `public double abroad()` | Dollars held abroad, per household. |
| 658 | 1 | `public double paper()` | The city's paper held, per household, at face (0.7.1). |
| 660 | 1 | `public double paperSold()` | ...sold this month, and its coupons and principal received, per household, in cash. |
| 661 | 1 | `public double paperIncome()` |  |
| 663 | 1 | `public double bonds()` | Its bonds, per household, at face, all together (0.7.12; the cell's own since round 2 - see bonds). |
| 664 | 1 | `public double bondsSold()` |  |
| 665 | 1 | `public double bondIncome()` |  |
| 666 | 1 | `public double cars()` |  |
| 668 | 1 | `public double totalCars()` | Every car this cell's households own between them. |
| 670 | 1 | `public double abroadValue(double localPerUsd)` | ...worth this much at home, per household, at a rate. |
| 671 | 1 | `public double sentAbroad()` |  |
| 672 | 1 | `public double broughtHome()` |  |
| 673 | 1 | `public double foreignInterest()` |  |
| 676 | 1 | `public double sold()` | Shares sold this month to cover the shop, per household, in cash. |
| 677 | 1 | `public boolean isLockedOut()` |  |
| 678 | 1 | `public double disposable()` |  |
| 679 | 1 | `public double afterFixed()` |  |
| 680 | 1 | `public double interest()` |  |
| 681 | 1 | `public double drawn()` |  |
| 682 | 1 | `public double unfunded()` |  |
| 683 | 1 | `public double borrowed()` |  |
| 685 | 1 | `public double loanFee()` | The bank's fee on this month's borrowing, added to the debt (0.7.7). |
| 687 | 1 | `public double accountFee()` | The bank's account fee this month (0.7.7). |
| 688 | 1 | `public double repaid()` |  |
| 689 | 1 | `public double banked()` |  |
| 690 | 1 | `public double want()` |  |
| 691 | 1 | `public double planned()` |  |
| 692 | 1 | `public double rate()` |  |
| 693 | 1 | `public double subsistence()` |  |
| 696 | 1 | `public double luxuryWant()` | What this household would put over a luxury counter this month. |
| 699 | 1 | `public double carsSold()` | Cars this cell sold second-hand this month, in total. |
| 700 | 1 | `public double bankrupt()` |  |
| 703 | 1 | `public boolean isCutOff()` | True when the bank has stopped lending to this cell - ceiling or lockout. |
| 731 | 1 | `public boolean isGoingShort()` | Whether this household planned to spend less than it wanted to. |
| 734 | 1 | `public double totalSavings()` | The cell's totals: the per-household figure times the households. |
| 735 | 1 | `public double totalAbroad()` |  |
| 736 | 1 | `public double totalPaper()` |  |
| 737 | 1 | `public double totalBonds()` |  |
| 738 | 1 | `public double totalDebt()` |  |
| 739 | 1 | `public double totalInterest()` |  |
| 740 | 1 | `public double totalBorrowed()` |  |
| 741 | 1 | `public double totalAccountFees()` |  |
| 742 | 1 | `public double totalLoanFees()` |  |
| 743 | 1 | `public double totalRepaid()` |  |
| 744 | 1 | `public double totalPlanned()` |  |
| 745 | 1 | `public double totalWant()` |  |
| 751 | 1 | `public double netWorth()` | Net worth of one of these households: what it has less what it owes. |

### the month (lines 753-1339)

| line | len | member | says |
|---:|---:|---|---|
| 772 | 4 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` | Settles a month: the bills in order, the shop against what was actually spent, and savings, then credit, then going without. |
| 781 | 123 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` |  |
| 910 | 3 | `double plan(double localPerUsd)` | Plans the next month, without settling one. |
| 918 | 3 | `double plan(double localPerUsd, double paperRatio)` | (0.7.1): the households' book at the curve over its face |
| 929 | 3 | `double plan(double localPerUsd, double paperRatio, double spendFactor)` | household plans (0.7.3): HouseholdBalance .spendFactor() on the month's real deposit rate, 1 at no real return - see AND WHAT IT SPENDS ANSWERS THE REAL RATE below |
| 940 | 150 | `double plan(double localPerUsd, double paperRatio, double spendFactor, double bondRatio)` | this month (0.7.12): every cell's holdings together at the bonds' value over their face - in the net worth the wealth term reads, and not in what can be spent, because a bond sells only when somebody buys it |
| 1092 | 12 | `void restrike(double disposablePer, double rentPerHome, double feesPer, double foodPricePerHead, double riskFreeAnnual)` | Re-strikes the fixed part of the month for the plan alone: the load path. |
| 1113 | 44 | `protected double fundShortfall(double still, double disposablePer)` | What is still short after savings, the city's paper, the paper abroad and the shares: the revolving credit line, up to its ceiling. |
| 1165 | 1 | `protected double planningRoom()` | What the plan may count on borrowing: the credit room, less the fee drawing it would add (0.7.7), for everyone but a student. |
| 1168 | 4 | `public double capitalRoom()` | What the bank's capital lets this cell draw this month, per household: up to its capitalCeiling. |
| 1174 | 3 | `public double lendableRoom(double disposablePer)` | What the bank will actually lend one of these this month: its credit room, and no more than its capital allows (0.7.8). |
| 1179 | 3 | `public double monthsOwed()` | Months of income this household owes - and with a debt and no income at all, past any ceiling, as discharge() reads it. |
| 1184 | 4 | `public double lossAllowance()` | What the bank sets aside against this cell's debt, in total (0.7.8): Bank.householdAllowance() on its months owed - a year's loss on a frozen debt, which nothing discharges while it is frozen. |
| 1190 | 3 | `public double debtInTrouble()` | This cell's debt, in total, when it is in trouble (Bank.householdWatched()); nothing when it is not. |
| 1195 | 1 | `protected double studentRepayment()` | The month's student-loan instalment (principal), per household. |
| 1198 | 1 | `protected double studentInterestDue()` | The month's interest on the student loan at the city's rate, per household. |
| 1205 | 1 | `public double studentInterestAt(double annualRate)` | What a month's interest on the loan would be at an annual rate - the same figure studentInterestDue() charges, for a screen previewing a rate the city has not set. |
| 1208 | 5 | `public double creditRoom(double disposablePer)` | What the bank will still lend one of these: the ceiling less what is owed, or nothing. |
| 1223 | 30 | `double discharge()` | Whoever cannot carry it any more: a share of the cell discharges. |
| 1255 | 11 | `void clearWorking()` | Nothing to strike: the working is blank, the position stands. |
| 1268 | 7 | `void clearAll()` | The cell is empty: no position either. |
| 1277 | 57 | `void redenominate(double scale)` | Everything in money, in the new unit. |
| 1336 | 3 | `public String toString()` |  |


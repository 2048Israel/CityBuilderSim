# Household.java - 1,478 lines · 124 methods · 12 constants · model

`ham/citybuildersim/Household.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
> lending to them, and what last month did to them. Ninety-nine of them - eleven
> working shapes across six tiers, four retired shapes across the six tiers they
> retired from (HS1), and the nine outside the families with the owners - and
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

**Uses:** [HouseholdBalance](HouseholdBalance.md) (17), [Bank](Bank.md) (6), [PayTier](PayTier.md) (3), [FamilyStructure](FamilyStructure.md) (3), [Currency](Currency.md) (2), [Equity](Equity.md) (1)

**Used by (48):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BasketYearCheck](BasketYearCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [Forms1900Check](Forms1900Check.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HoldersCheck](HoldersCheck.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdFlowCheck](HouseholdFlowCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [InvestCheck](InvestCheck.md), [KeptBench](KeptBench.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [Offending](Offending.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [OwnerHousehold](OwnerHousehold.md), [PeopleScreen](PeopleScreen.md), [PrisonerHousehold](PrisonerHousehold.md), [PrizeCheck](PrizeCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [SaveFileCheck](SaveFileCheck.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [StudentHousehold](StudentHousehold.md), [UnemployedHousehold](UnemployedHousehold.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 110 | THE LOAN'S RATE (2026-09-21) |
| 147 | · the position |
| 368 | WHO CAN AFFORD THE CLINIC (2026-09-19) |
| 410 | GROCERIES AT A PRICE (0.7.43; the project's spec-inflation.md 2.6) |
| 508 | · last month's working, per household |
| 603 | · what differs |
| 669 | · reading |
| 676 | A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) |
| 846 | · the month |
| 880 | · · what the lender charges this one |
| 891 | · · the bills, in order |
| 900 | · · settle what they actually spent |
| 1081 | · AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 1109 | · AND THE FORTUNE ALSO ASKS FOR WATCHES (2026-09-17) |
| 1144 | · AND WHAT IT WOULD SPEND EATING OUT (2026-09-18) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `Household.RETIRED_ROW` | `PayTier.values().length` | The row the retired sum into, after the six tiers. |
| 72 | `Household.UNEMPLOYED_ROW` | `RETIRED_ROW + 1` | The out of work: on EI, off it, and those who have lost their home. |
| 74 | `Household.STUDENT_ROW` | `RETIRED_ROW + 2` | Full-time students, living on a grant, their savings and a student loan. |
| 76 | `Household.ORPHAN_ROW` | `RETIRED_ROW + 3` | Children no family holds, by band. |
| 82 | `Household.PRISON_ROW` | `RETIRED_ROW + 4` | Adults serving a sentence (2026-09-11). |
| 91 | `Household.OWNER_ROW` | `RETIRED_ROW + 5` | The owner families (BO1): one two-adult household per company that has listed and not gone broke. |
| 93 | `Household.ROWS` | `RETIRED_ROW + 6` | Every row: the six tiers, the retired, and the five above. |
| 95 | `Household.STOCK_GROUPS` | `ROWS + 2 * PayTier.values().length` | Every stock group (stockGroup()): the rows', then the retired by the tier they left, one a tier (HS1), then the young working households by their tier (HS2). |
| 97 | `Household.ROWS_BEFORE_PRISON` | `RETIRED_ROW + 4` | The rows a save from before the prisons carries (pinned: ROWS has moved on). |
| 99 | `Household.ROWS_BEFORE_OWNER` | `RETIRED_ROW + 5` | The rows a save from before the owner families carries. |
| 108 | `Household.STUDENT_LOAN_MONTHS` | `114` | How long a graduate takes to repay a student loan, in months: nine and a half years, the Canada Student Loan standard term (Alberta Student Aid's repayment page; the six-month grace is not modelled). |
| 1217 | `Household.SHORT_ROUNDING` | `1e-12` | The share of a month's gap under which what is still short after savings, the paper, the money abroad, the bonds and the shares is the subtractions' rounding, and nothing is borrowed for it (settle(), A HAIR STILL SHO... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 145 | `protected final FamilyStructure shape` | The family shape, or null for a household that is not one: the unemployed, a student, an orphan. |
| 153 | `double savings` | What one of these households has in the bank. |
| 156 | `double debt` | ...and what it owes the bank on its revolving credit. |
| 175 | `double investmentIncome` | Dividends and foreign coupons received since this cell last planned a month. |
| 183 | `double meansIncome` | ...and the same, smoothed over HouseholdBalance.MEANS_INCOME_MONTHS (0.7.45): what the food assistance means test reads, so a coupon month does not take a household out of assistance and the next month put it back. |
| 186 | `int lockout` | Months this cell cannot borrow, after a discharge. |
| 196 | `double capitalCeiling` | THE MOST THIS CELL MAY OWE THIS MONTH ON THE BANK'S CAPITAL (0.7.8), per household: what it owed when the bank's rule was set at the top of the month, grown by the month's limit - infinite while the bank lends freely. |
| 205 | `double studentDebt` | What one of these households owes the treasury on student loans. |
| 217 | `final double[] shares` | Shares held in each of the city's companies, per household of the cell, indexed as Equity.COMPANIES. |
| 220 | `double dividends` | What the shares paid this month, per household. |
| 235 | `double abroad` | DOLLARS held abroad by one of these households: the world's paper, bought with savings past the cushion when the world pays more than the bank, sold when the bank pays more or the household needs the money. |
| 250 | `double paper` | THE CITY'S OWN PAPER, AT HOME (0.7.1): face held, per household of the cell, in local money - the fourth asset beside the bank balance, the shares and the dollars abroad. |
| 270 | `double bonds` | CORPORATE BONDS (0.7.12): the face this cell holds, per household of it, in the businesses' bonds, all together - the fifth asset, beside the bank balance, the shares, the dollars abroad and the city's paper. |
| 273 | `final java.util.TreeMap<Integer, Double> bondFace` | ...bond by bond: the face per household of the cell, by the bond's id (CorporateBond.id()). |
| 325 | `double cars` | CARS THIS HOUSEHOLD OWNS, per household of the cell, 0 to 1 (2026-09-16). |
| 331 | `double luxuryWant` | What this household would spend on luxuries this month, per household. |
| 337 | `double mealWant` | ...and what it would spend eating out, per household. |
| 364 | `double mealsEaten` | MEALS this household ate out last month, per household. |
| 405 | `double carePaid` | Of this household's people, the share who paid for care at the last strike: 1 for all of them. |
| 408 | `double spendable` | What one of these households could fund next month: the plan's own figure, kept for the care test. |
| 431 | `double need` | Baskets one of these households needs a month: baskets(), struck by plan(). |
| 434 | `double foodMoney` | The money one of these households could put to food at the till: the plan's spendable and its food assistance voucher. |
| 437 | `double voucher` | The food assistance voucher one of these households holds this month, in money: HouseholdBalance.foodAssistanceFor(), set before plan(). |
| 440 | `double groceriesGot` | Baskets one of these households got at the last sale - a position, read at the next month's top. |
| 443 | `double groceriesAsked` | ...the baskets it asked for at the price the shelf charged, which it would have had at full shelves - the money half of the hunger. |
| 446 | `double groceriesNeed` | ...the baskets it needed at that sale: `need`, or nothing for a cell too empty to be struck - what the hunger is measured against. |
| 449 | `double assistance` | ...and what the treasury paid toward them, per household: the voucher, up to the baskets got at the price. |
| 460 | `double careSkipped` | The care bill this household skipped at the last strike, per household - what it ate instead. |
| 496 | `double carsSold` | ...and the ones this cell sold into the second-hand market this month, in total rather than per household. |
| 499 | `double sentAbroad, broughtHome, foreignInterest` | This month's, per household, in local money: sent abroad, brought home, and earned there (rolled, not paid home). |
| 506 | `double households` | Households this cell was last struck for - the multiplier on every per-household figure, and the count the next month's census is compared against to see who moved. |
| 518 | `double disposable` | Take-home this month: wages or pension, after tax and contributions. |
| 519 | `double afterFixed` |  |
| 520 | `double interest` |  |
| 521 | `double drawn` |  |
| 522 | `double unfunded` |  |
| 523 | `double borrowed` |  |
| 524 | `double repaid` |  |
| 525 | `double banked` |  |
| 526 | `double want` |  |
| 527 | `double planned` |  |
| 528 | `double rate` |  |
| 529 | `double subsistence` |  |
| 532 | `double bankrupt` | Households of this cell discharged this month - a count, not money. |
| 535 | `double seizedSavings` | Savings the month's bankrupt share repaid the bank with, per household of the cell (HL1): part of `repaid`, nothing in a month nobody discharges. |
| 538 | `double sold` | Shares sold this month to cover the shop, per household, in cash. |
| 541 | `double paperSold, paperIncome` | The city's paper sold this month - to cover the shop or because the spread went - and its coupons and principal received, per household, in cash (0.7.1). |
| 544 | `double bondsSold, bondIncome` | Its bonds sold this month - in the waterfall or at the market's step - and their coupons and principal received, per household, in cash (0.7.12). |
| 547 | `double studentBorrowed, studentRepaid` | Student loan drawn this month, and repaid (principal), per household. |
| 550 | `double studentInterest` | Interest charged on the student loan this month, per household: paid with the instalment, and the treasury's. |
| 553 | `double studentLoanRate` | The annual rate the treasury charges a graduate on the loan: the city's one policy, told to every cell by HouseholdBalance before it settles. |
| 559 | `boolean studentLoansOpen` | Whether the treasury makes student loans yet (R0): told to every cell with the rate, true for a cell no city holds. |
| 562 | `double evicted` | Households of this cell that lost their home this month - a count, not money. |
| 565 | `double accountFee` | The bank's account fee this household paid this month, with its other fixed bills (0.7.7); nothing without a home. |
| 568 | `double loanFee` | The bank's fee on what it borrowed this month, added to what it owes rather than paid (0.7.7): Bank.LOAN_FEE of the credit drawn and the cars financed. |
| 571 | `double rentShare` | What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. |
| 702 | `double dependants` | Dependants who live in one of these households but are not its shape. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 1420 | **type** `public abstract class Household` | Every household of one shape at one pay tier, as one ledger. |

### THE LOAN'S RATE (2026-09-21) (lines 110-146)

### the position (lines 147-367)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 1 | `public double bondFace(int id)` | The face per household this cell holds of one bond. |
| 285 | 4 | `void setBondFace(int id, double perHousehold)` | ...set, per household, and the total with it; nothing held is not kept. |
| 291 | 3 | `void putBondFace(int id, double perHousehold)` | ...without the total, for a caller that sets many and recounts once (HouseholdBalance's coupons, principal and write-downs): the same sum, not the square of the bonds held. |
| 296 | 5 | `void recountBonds()` | The total, from the bonds themselves: after anything that moved many at once. |
| 366 | 1 | `public double mealsEaten()` |  |

### WHO CAN AFFORD THE CLINIC (2026-09-19) (lines 368-409)

### GROCERIES AT A PRICE (0.7.43; the project's spec-inflation.md 2.6) (lines 410-507)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 1 | `public double need()` |  |
| 452 | 1 | `public double foodMoney()` |  |
| 453 | 1 | `public double voucher()` |  |
| 454 | 1 | `public double groceriesGot()` |  |
| 455 | 1 | `public double groceriesAsked()` |  |
| 456 | 1 | `public double groceriesNeed()` |  |
| 457 | 1 | `public double assistance()` |  |
| 462 | 1 | `public double carePaid()` |  |
| 463 | 1 | `public double spendable()` |  |
| 464 | 1 | `public double careSkipped()` |  |
| 478 | 11 | `double affordCare(double fullBill, double paidBill)` | Decides how much of its care bill this household pays, after the month is settled and the plan is struck - the rule in the banner above. |

### last month's working, per household (lines 508-602)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 24 | **type** `interface Liquidity` | Somewhere a household short of money can sell shares before it borrows. |
| 576 | 1 | `double sell(Household cell, double needPer)` _(in Household.Liquidity)_ |  |
| 585 | 1 | `default double sellPaper(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the city's paper, first (0.7.1): sold to the bank's desk at the households' book ratio for exactly what is short, or everything held if that is less. |
| 596 | 1 | `default double sellBonds(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the bonds (0.7.12), after the paper and the dollars abroad: asked on each bond's order book at the price that makes the buyer's yield this household's borrowing rate, and filled only by what rests there at that... |
| 599 | 3 | `protected Household(FamilyStructure shape)` |  |

### what differs (lines 603-668)

| line | len | member | says |
|---:|---:|---|---|
| 606 | 1 | `public abstract PayTier tier()` | The pay tier, or null for a household with no earner. |
| 609 | 1 | `public abstract int row()` | The row this cell sums into: the tier's index, or RETIRED_ROW. |
| 611 | 1 | `public abstract boolean isRetired()` |  |
| 623 | 1 | `public abstract int grownUps()` | Who in the household carries the money: the earners, or the pensioners. |
| 630 | 1 | `public double earningWeight()` | Who in the household the row's INCOME is split by. |
| 633 | 1 | `public boolean canBeEvicted()` | True for a cell whose households lose their home when they cannot pay for it. |
| 640 | 1 | `public int stockGroup()` | The row whose people this cell's people most often ARE, for the money to follow them: its own row, for everybody but the out of work. |
| 647 | 1 | `protected double baskets()` | Baskets of food one of these households has to buy: one a head, for everybody but a prisoner, whom the city feeds (the prisons' upkeep). |
| 653 | 1 | `protected boolean debtFrozen()` | True when the debt is frozen: no interest charged, nothing discharged, nothing borrowed. |
| 660 | 1 | `public boolean canInvest()` | True when the household decides what to do with its savings - shares, paper abroad, an offering. |
| 667 | 1 | `public boolean tradesShares()` | True when the household buys and sells shares by the households' rules: at an offering and on the exchange. |

### reading (lines 669-675)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 1 | `public FamilyStructure shape()` |  |
| 674 | 1 | `public int size()` | People in one of these households, as its SHAPE declares them. |

### A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) (lines 676-845)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 1 | `public double headcount()` | People in one of these households, counting anybody who came with them. |
| 708 | 1 | `public double getDependants()` | ...of whom this many live here without being its shape's own. |
| 710 | 4 | `public String label()` |  |
| 716 | 3 | `public String key()` | "COUPLE_TEEN:SKILLED", or the shape alone for the retired. |
| 720 | 1 | `public double households()` |  |
| 722 | 1 | `public boolean isEmpty()` | Under HouseholdBalance.EMPTY_CELL households: too few to strike, to hold anything or to be paid. |
| 724 | 1 | `public double people()` | People in the whole cell - its households times what each of them holds. |
| 726 | 1 | `public double studentDebt()` |  |
| 727 | 1 | `public double studentBorrowed()` |  |
| 728 | 1 | `public double studentRepaid()` |  |
| 730 | 1 | `public double studentInterest()` | The month's interest on the student loan, per household - paid on top of studentRepaid(). |
| 731 | 1 | `public double totalStudentDebt()` |  |
| 732 | 1 | `public double evicted()` |  |
| 733 | 1 | `public double rentShare()` |  |
| 736 | 1 | `public double savings()` | Per household. |
| 737 | 1 | `public double debt()` |  |
| 738 | 1 | `public int lockout()` |  |
| 741 | 1 | `public double shares(int company)` | Shares held in this company, per household. |
| 742 | 1 | `public double totalShares(int company)` |  |
| 745 | 1 | `public double dividends()` | This month's dividends, per household. |
| 748 | 1 | `public double abroad()` | Dollars held abroad, per household. |
| 750 | 1 | `public double paper()` | The city's paper held, per household, at face (0.7.1). |
| 752 | 1 | `public double paperSold()` | ...sold this month, and its coupons and principal received, per household, in cash. |
| 753 | 1 | `public double paperIncome()` |  |
| 755 | 1 | `public double bonds()` | Its bonds, per household, at face, all together (0.7.12; the cell's own since round 2 - see bonds). |
| 756 | 1 | `public double bondsSold()` |  |
| 757 | 1 | `public double bondIncome()` |  |
| 758 | 1 | `public double cars()` |  |
| 760 | 1 | `public double totalCars()` | Every car this cell's households own between them. |
| 762 | 1 | `public double abroadValue(double localPerUsd)` | ...worth this much at home, per household, at a rate. |
| 763 | 1 | `public double sentAbroad()` |  |
| 764 | 1 | `public double broughtHome()` |  |
| 765 | 1 | `public double foreignInterest()` |  |
| 768 | 1 | `public double sold()` | Shares sold this month to cover the shop, per household, in cash. |
| 769 | 1 | `public boolean isLockedOut()` |  |
| 770 | 1 | `public double disposable()` |  |
| 771 | 1 | `public double afterFixed()` |  |
| 772 | 1 | `public double interest()` |  |
| 773 | 1 | `public double drawn()` |  |
| 774 | 1 | `public double unfunded()` |  |
| 775 | 1 | `public double borrowed()` |  |
| 777 | 1 | `public double loanFee()` | The bank's fee on this month's borrowing, added to the debt (0.7.7). |
| 779 | 1 | `public double accountFee()` | The bank's account fee this month (0.7.7). |
| 780 | 1 | `public double repaid()` |  |
| 781 | 1 | `public double banked()` |  |
| 782 | 1 | `public double want()` |  |
| 783 | 1 | `public double planned()` |  |
| 784 | 1 | `public double rate()` |  |
| 785 | 1 | `public double subsistence()` |  |
| 788 | 1 | `public double luxuryWant()` | What this household would put over a luxury counter this month. |
| 791 | 1 | `public double carsSold()` | Cars this cell sold second-hand this month, in total. |
| 792 | 1 | `public double bankrupt()` |  |
| 793 | 1 | `public double seizedSavings()` |  |
| 796 | 1 | `public boolean isCutOff()` | True when the bank has stopped lending to this cell - ceiling or lockout. |
| 824 | 1 | `public boolean isGoingShort()` | Whether this household planned to spend less than it wanted to. |
| 827 | 1 | `public double totalSavings()` | The cell's totals: the per-household figure times the households. |
| 828 | 1 | `public double totalAbroad()` |  |
| 829 | 1 | `public double totalPaper()` |  |
| 830 | 1 | `public double totalBonds()` |  |
| 831 | 1 | `public double totalDebt()` |  |
| 832 | 1 | `public double totalInterest()` |  |
| 833 | 1 | `public double totalBorrowed()` |  |
| 834 | 1 | `public double totalAccountFees()` |  |
| 835 | 1 | `public double totalLoanFees()` |  |
| 836 | 1 | `public double totalRepaid()` |  |
| 837 | 1 | `public double totalPlanned()` |  |
| 838 | 1 | `public double totalWant()` |  |
| 844 | 1 | `public double netWorth()` | Net worth of one of these households: what it has less what it owes. |

### the month (lines 846-1478)

| line | len | member | says |
|---:|---:|---|---|
| 865 | 4 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` | Settles a month: the bills in order, the shop against what was actually spent, and savings, then credit, then going without. |
| 874 | 134 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` |  |
| 1014 | 3 | `double plan(double localPerUsd)` | Plans the next month, without settling one. |
| 1022 | 3 | `double plan(double localPerUsd, double paperRatio)` | (0.7.1): the households' book at the curve over its face |
| 1033 | 3 | `double plan(double localPerUsd, double paperRatio, double spendFactor)` | household plans (0.7.3): HouseholdBalance .spendFactor() on the month's real deposit rate, 1 at no real return - see AND WHAT IT SPENDS ANSWERS THE REAL RATE below |
| 1044 | 157 | `double plan(double localPerUsd, double paperRatio, double spendFactor, double bondRatio)` | this month (0.7.12): every cell's holdings together at the bonds' value over their face - in the net worth the wealth term reads, and not in what can be spent, because a bond sells only when somebody buys it |
| 1203 | 12 | `void restrike(double disposablePer, double rentPerHome, double feesPer, double foodPricePerHead, double riskFreeAnnual)` | Re-strikes the fixed part of the month for the plan alone: the load path. |
| 1227 | 44 | `protected double fundShortfall(double still, double disposablePer)` | What is still short after savings, the city's paper, the paper abroad and the shares: the revolving credit line, up to its ceiling. |
| 1279 | 1 | `protected double planningRoom()` | What the plan may count on borrowing: the credit room, less the fee drawing it would add (0.7.7), for everyone but a student. |
| 1282 | 4 | `public double capitalRoom()` | What the bank's capital lets this cell draw this month, per household: up to its capitalCeiling. |
| 1288 | 3 | `public double lendableRoom(double disposablePer)` | What the bank will actually lend one of these this month: its credit room, and no more than its capital allows (0.7.8). |
| 1293 | 3 | `public double monthsOwed()` | Months of income this household owes - and with a debt and no income at all, past any ceiling, as discharge() reads it. |
| 1298 | 4 | `public double lossAllowance()` | What the bank sets aside against this cell's debt, in total (0.7.8): Bank.householdAllowance() on its months owed - a year's loss on a frozen debt, which nothing discharges while it is frozen. |
| 1304 | 3 | `public double debtInTrouble()` | This cell's debt, in total, when it is in trouble (Bank.householdWatched()); nothing when it is not. |
| 1309 | 1 | `protected double studentRepayment()` | The month's student-loan instalment (principal), per household. |
| 1312 | 1 | `protected double studentInterestDue()` | The month's interest on the student loan at the city's rate, per household. |
| 1319 | 1 | `public double studentInterestAt(double annualRate)` | What a month's interest on the loan would be at an annual rate - the same figure studentInterestDue() charges, for a screen previewing a rate the city has not set. |
| 1322 | 5 | `public double creditRoom(double disposablePer)` | What the bank will still lend one of these: the ceiling less what is owed, or nothing. |
| 1337 | 48 | `double discharge()` | Whoever cannot carry it any more: a share of the cell discharges. |
| 1387 | 12 | `void clearWorking()` | Nothing to strike: the working is blank, the position stands. |
| 1401 | 9 | `void clearAll()` | The cell is empty: no position either. |
| 1412 | 59 | `void redenominate(double scale)` | Everything in money, in the new unit. |
| 1473 | 5 | `public String toString()` |  |


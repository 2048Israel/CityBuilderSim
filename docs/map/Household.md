# Household.java - 1,586 lines · 138 methods · 12 constants · model

`ham/citybuildersim/Household.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Bank](Bank.md) (23), [CellSpread](CellSpread.md) (20), [HouseholdBalance](HouseholdBalance.md) (13), [Equity](Equity.md) (4), [PayTier](PayTier.md) (3), [FamilyStructure](FamilyStructure.md) (3), [Currency](Currency.md) (2)

**Used by (55):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BasketYearCheck](BasketYearCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CarCheck](CarCheck.md), [CardCheck](CardCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [DebtStatistics](DebtStatistics.md), [DebtStatisticsCheck](DebtStatisticsCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [Forms1900Check](Forms1900Check.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HoldersCheck](HoldersCheck.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdFlowCheck](HouseholdFlowCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [InvestCheck](InvestCheck.md), [KeptBench](KeptBench.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [Offending](Offending.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [OwnerHousehold](OwnerHousehold.md), [PeopleScreen](PeopleScreen.md), [PrisonerHousehold](PrisonerHousehold.md), [PrizeCheck](PrizeCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [SaveFileCheck](SaveFileCheck.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [StudentHousehold](StudentHousehold.md), [UnemployedHousehold](UnemployedHousehold.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 110 | THE LOAN'S RATE (2026-09-21) |
| 147 | · the position |
| 432 | WHO CAN AFFORD THE CLINIC (2026-09-19) |
| 474 | GROCERIES AT A PRICE (0.7.43; the project's spec-inflation.md 2.6) |
| 572 | · last month's working, per household |
| 667 | · what differs |
| 733 | · reading |
| 740 | A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) |
| 910 | · the month |
| 945 | · · what the lender charges this one |
| 956 | · · the bills, in order |
| 966 | · · settle what they actually spent |
| 1197 | · AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 1225 | · AND THE FORTUNE ALSO ASKS FOR WATCHES (2026-09-17) |
| 1260 | · AND WHAT IT WOULD SPEND EATING OUT (2026-09-18) |

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
| 1332 | `Household.SHORT_ROUNDING` | `1e-12` | The share of a month's gap under which what is still short after savings, the paper, the money abroad, the bonds and the shares is the subtractions' rounding, and nothing is borrowed for it (settle(), A HAIR STILL SHO... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 145 | `protected final FamilyStructure shape` | The family shape, or null for a household that is not one: the unemployed, a student, an orphan. |
| 153 | `double savings` | What one of these households has in the bank. |
| 156 | `double debt` | ...and what it owes the bank on its revolving credit. |
| 159 | `double revolving, lateDebt, lateShare, shutOut` | Shares of households carrying cards, late, and excluded; lateDebt is part of debt (HC1). |
| 161 | `double minimumPaid, emergencyBill, cardWrittenOff, minimumDue, depositIncome` | The settled month's minimum principal, emergency bill, and net write-off, per household. |
| 163 | `double bankruptcyLeaving, bankruptcyCash` | Next census's debt-free departing share, and its remaining cash per original household. |
| 231 | `double investmentIncome` | Dividends and foreign coupons received since this cell last planned a month. |
| 239 | `double meansIncome` | ...and the same, smoothed over HouseholdBalance.MEANS_INCOME_MONTHS (0.7.45): what the food assistance means test reads, so a coupon month does not take a household out of assistance and the next month put it back. |
| 250 | `double capitalCeiling` | THE MOST THIS CELL MAY OWE THIS MONTH ON THE BANK'S CAPITAL (0.7.8), per household: what it owed when the bank's rule was set at the top of the month, grown by the month's limit - infinite while the bank lends freely. |
| 259 | `double studentDebt` | What one of these households owes the treasury on student loans. |
| 271 | `final double[] shares` | Shares held in each of the city's companies, per household of the cell, indexed as Equity.COMPANIES. |
| 274 | `double dividends` | What the shares paid this month, per household. |
| 277 | `final double[] basis` | Average acquisition cost in each company, per household (HT1); a split leaves it standing. |
| 279 | `double lossCarried` | Net capital losses available against later gains, per household. |
| 281 | `final double[] taxShares` | Encumbered subset of shares, and its limited, interest-free unpaid gains tax (HT1). |
| 282 | `final double[] taxClaim` |  |
| 284 | `double taxedGains` | Gains taxed in the current month, eligible for a same-month loss refund. |
| 299 | `double abroad` | DOLLARS held abroad by one of these households: the world's paper, bought with savings past the cushion when the world pays more than the bank, sold when the bank pays more or the household needs the money. |
| 314 | `double paper` | THE CITY'S OWN PAPER, AT HOME (0.7.1): face held, per household of the cell, in local money - the fourth asset beside the bank balance, the shares and the dollars abroad. |
| 334 | `double bonds` | CORPORATE BONDS (0.7.12): the face this cell holds, per household of it, in the businesses' bonds, all together - the fifth asset, beside the bank balance, the shares, the dollars abroad and the city's paper. |
| 337 | `final java.util.TreeMap<Integer, Double> bondFace` | ...bond by bond: the face per household of the cell, by the bond's id (CorporateBond.id()). |
| 389 | `double cars` | CARS THIS HOUSEHOLD OWNS, per household of the cell, 0 to 1 (2026-09-16). |
| 395 | `double luxuryWant` | What this household would spend on luxuries this month, per household. |
| 401 | `double mealWant` | ...and what it would spend eating out, per household. |
| 428 | `double mealsEaten` | MEALS this household ate out last month, per household. |
| 469 | `double carePaid` | Of this household's people, the share who paid for care at the last strike: 1 for all of them. |
| 472 | `double spendable` | What one of these households could fund next month: the plan's own figure, kept for the care test. |
| 495 | `double need` | Baskets one of these households needs a month: baskets(), struck by plan(). |
| 498 | `double foodMoney` | The money one of these households could put to food at the till: the plan's spendable and its food assistance voucher. |
| 501 | `double voucher` | The food assistance voucher one of these households holds this month, in money: HouseholdBalance.foodAssistanceFor(), set before plan(). |
| 504 | `double groceriesGot` | Baskets one of these households got at the last sale - a position, read at the next month's top. |
| 507 | `double groceriesAsked` | ...the baskets it asked for at the price the shelf charged, which it would have had at full shelves - the money half of the hunger. |
| 510 | `double groceriesNeed` | ...the baskets it needed at that sale: `need`, or nothing for a cell too empty to be struck - what the hunger is measured against. |
| 513 | `double assistance` | ...and what the treasury paid toward them, per household: the voucher, up to the baskets got at the price. |
| 524 | `double careSkipped` | The care bill this household skipped at the last strike, per household - what it ate instead. |
| 560 | `double carsSold` | ...and the ones this cell sold into the second-hand market this month, in total rather than per household. |
| 563 | `double sentAbroad, broughtHome, foreignInterest` | This month's, per household, in local money: sent abroad, brought home, and earned there (rolled, not paid home). |
| 570 | `double households` | Households this cell was last struck for - the multiplier on every per-household figure, and the count the next month's census is compared against to see who moved. |
| 582 | `double disposable` | Take-home this month: wages or pension, after tax and contributions. |
| 583 | `double afterFixed` |  |
| 584 | `double interest` |  |
| 585 | `double drawn` |  |
| 586 | `double unfunded` |  |
| 587 | `double borrowed` |  |
| 588 | `double repaid` |  |
| 589 | `double banked` |  |
| 590 | `double want` |  |
| 591 | `double planned` |  |
| 592 | `double rate` |  |
| 593 | `double subsistence` |  |
| 596 | `double bankrupt` | Households of this cell discharged this month - a count, not money. |
| 599 | `double seizedSavings` | Savings the month's bankrupt share repaid the bank with, per household of the cell (HL1): part of `repaid`, nothing in a month nobody discharges. |
| 602 | `double sold` | Shares sold this month to cover the shop, per household, in cash. |
| 605 | `double paperSold, paperIncome` | The city's paper sold this month - to cover the shop or because the spread went - and its coupons and principal received, per household, in cash (0.7.1). |
| 608 | `double bondsSold, bondIncome` | Its bonds sold this month - in the waterfall or at the market's step - and their coupons and principal received, per household, in cash (0.7.12). |
| 611 | `double studentBorrowed, studentRepaid` | Student loan drawn this month, and repaid (principal), per household. |
| 614 | `double studentInterest` | Interest charged on the student loan this month, per household: paid with the instalment, and the treasury's. |
| 617 | `double studentLoanRate` | The annual rate the treasury charges a graduate on the loan: the city's one policy, told to every cell by HouseholdBalance before it settles. |
| 623 | `boolean studentLoansOpen` | Whether the treasury makes student loans yet (R0): told to every cell with the rate, true for a cell no city holds. |
| 626 | `double evicted` | Households of this cell that lost their home this month - a count, not money. |
| 629 | `double accountFee` | The bank's account fee this household paid this month, with its other fixed bills (0.7.7); nothing without a home. |
| 632 | `double loanFee` | The bank's fee on what it borrowed this month, added to what it owes rather than paid (0.7.7): Bank.LOAN_FEE of the credit drawn and the cars financed. |
| 635 | `double rentShare` | What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. |
| 766 | `double dependants` | Dependants who live in one of these households but are not its shape. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 1528 | **type** `public abstract class Household` | Every household of one shape at one pay tier, as one ledger. |

### THE LOAN'S RATE (2026-09-21) (lines 110-146)

### the position (lines 147-431)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 1 | `public double revolving()` |  |
| 166 | 1 | `public double lateDebt()` |  |
| 167 | 1 | `public double lateShare()` |  |
| 168 | 1 | `public double shutOut()` |  |
| 169 | 1 | `public double minimumPaid()` |  |
| 170 | 1 | `public double minimumDue()` |  |
| 171 | 1 | `public double emergencyBill()` |  |
| 172 | 1 | `public double cardWrittenOff()` |  |
| 175 | 4 | `double cardAccess(double income)` | Available customers after the bank's income/access test, excluding children and prisoners. |
| 181 | 5 | `private double cardRate(double base, double income)` | The rate is on holders' own balances, capped at the criminal-rate default. |
| 188 | 4 | `double minimumPrincipal()` | Principal part of the fixed minimum, after its interest. |
| 194 | 9 | `private void cardArrears(double spare)` | Probability and balance tails move into arrears at an annual hazard. |
| 205 | 7 | `void boundCards()` | Reassert subset bounds after a census pools wallets from different shapes. |
| 340 | 1 | `public double bondFace(int id)` | The face per household this cell holds of one bond. |
| 349 | 4 | `void setBondFace(int id, double perHousehold)` | ...set, per household, and the total with it; nothing held is not kept. |
| 355 | 3 | `void putBondFace(int id, double perHousehold)` | ...without the total, for a caller that sets many and recounts once (HouseholdBalance's coupons, principal and write-downs): the same sum, not the square of the bonds held. |
| 360 | 5 | `void recountBonds()` | The total, from the bonds themselves: after anything that moved many at once. |
| 430 | 1 | `public double mealsEaten()` |  |

### WHO CAN AFFORD THE CLINIC (2026-09-19) (lines 432-473)

### GROCERIES AT A PRICE (0.7.43; the project's spec-inflation.md 2.6) (lines 474-571)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 1 | `public double need()` |  |
| 516 | 1 | `public double foodMoney()` |  |
| 517 | 1 | `public double voucher()` |  |
| 518 | 1 | `public double groceriesGot()` |  |
| 519 | 1 | `public double groceriesAsked()` |  |
| 520 | 1 | `public double groceriesNeed()` |  |
| 521 | 1 | `public double assistance()` |  |
| 526 | 1 | `public double carePaid()` |  |
| 527 | 1 | `public double spendable()` |  |
| 528 | 1 | `public double careSkipped()` |  |
| 542 | 11 | `double affordCare(double fullBill, double paidBill)` | Decides how much of its care bill this household pays, after the month is settled and the plan is struck - the rule in the banner above. |

### last month's working, per household (lines 572-666)

| line | len | member | says |
|---:|---:|---|---|
| 638 | 24 | **type** `interface Liquidity` | Somewhere a household short of money can sell shares before it borrows. |
| 640 | 1 | `double sell(Household cell, double needPer)` _(in Household.Liquidity)_ |  |
| 649 | 1 | `default double sellPaper(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the city's paper, first (0.7.1): sold to the bank's desk at the households' book ratio for exactly what is short, or everything held if that is less. |
| 660 | 1 | `default double sellBonds(Household cell, double needPer)` _(in Household.Liquidity)_ | ...and the bonds (0.7.12), after the paper and the dollars abroad: asked on each bond's order book at the price that makes the buyer's yield this household's borrowing rate, and filled only by what rests there at that... |
| 663 | 3 | `protected Household(FamilyStructure shape)` |  |

### what differs (lines 667-732)

| line | len | member | says |
|---:|---:|---|---|
| 670 | 1 | `public abstract PayTier tier()` | The pay tier, or null for a household with no earner. |
| 673 | 1 | `public abstract int row()` | The row this cell sums into: the tier's index, or RETIRED_ROW. |
| 675 | 1 | `public abstract boolean isRetired()` |  |
| 687 | 1 | `public abstract int grownUps()` | Who in the household carries the money: the earners, or the pensioners. |
| 694 | 1 | `public double earningWeight()` | Who in the household the row's INCOME is split by. |
| 697 | 1 | `public boolean canBeEvicted()` | True for a cell whose households lose their home when they cannot pay for it. |
| 704 | 1 | `public int stockGroup()` | The row whose people this cell's people most often ARE, for the money to follow them: its own row, for everybody but the out of work. |
| 711 | 1 | `protected double baskets()` | Baskets of food one of these households has to buy: one a head, for everybody but a prisoner, whom the city feeds (the prisons' upkeep). |
| 717 | 1 | `protected boolean debtFrozen()` | True when the debt is frozen: no interest charged, nothing discharged, nothing borrowed. |
| 724 | 1 | `public boolean canInvest()` | True when the household decides what to do with its savings - shares, paper abroad, an offering. |
| 731 | 1 | `public boolean tradesShares()` | True when the household buys and sells shares by the households' rules: at an offering and on the exchange. |

### reading (lines 733-739)

| line | len | member | says |
|---:|---:|---|---|
| 735 | 1 | `public FamilyStructure shape()` |  |
| 738 | 1 | `public int size()` | People in one of these households, as its SHAPE declares them. |

### A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15) (lines 740-909)

| line | len | member | says |
|---:|---:|---|---|
| 769 | 1 | `public double headcount()` | People in one of these households, counting anybody who came with them. |
| 772 | 1 | `public double getDependants()` | ...of whom this many live here without being its shape's own. |
| 774 | 4 | `public String label()` |  |
| 780 | 3 | `public String key()` | "COUPLE_TEEN:SKILLED", or the shape alone for the retired. |
| 784 | 1 | `public double households()` |  |
| 786 | 1 | `public boolean isEmpty()` | Under HouseholdBalance.EMPTY_CELL households: too few to strike, to hold anything or to be paid. |
| 788 | 1 | `public double people()` | People in the whole cell - its households times what each of them holds. |
| 790 | 1 | `public double studentDebt()` |  |
| 791 | 1 | `public double studentBorrowed()` |  |
| 792 | 1 | `public double studentRepaid()` |  |
| 794 | 1 | `public double studentInterest()` | The month's interest on the student loan, per household - paid on top of studentRepaid(). |
| 795 | 1 | `public double totalStudentDebt()` |  |
| 796 | 1 | `public double evicted()` |  |
| 797 | 1 | `public double rentShare()` |  |
| 800 | 1 | `public double savings()` | Per household. |
| 801 | 1 | `public double debt()` |  |
| 802 | 1 | `public int lockout()` |  |
| 805 | 1 | `public double shares(int company)` | Shares held in this company, per household. |
| 806 | 1 | `public double totalShares(int company)` |  |
| 809 | 1 | `public double dividends()` | This month's dividends, per household. |
| 812 | 1 | `public double abroad()` | Dollars held abroad, per household. |
| 814 | 1 | `public double paper()` | The city's paper held, per household, at face (0.7.1). |
| 816 | 1 | `public double paperSold()` | ...sold this month, and its coupons and principal received, per household, in cash. |
| 817 | 1 | `public double paperIncome()` |  |
| 819 | 1 | `public double bonds()` | Its bonds, per household, at face, all together (0.7.12; the cell's own since round 2 - see bonds). |
| 820 | 1 | `public double bondsSold()` |  |
| 821 | 1 | `public double bondIncome()` |  |
| 822 | 1 | `public double cars()` |  |
| 824 | 1 | `public double totalCars()` | Every car this cell's households own between them. |
| 826 | 1 | `public double abroadValue(double localPerUsd)` | ...worth this much at home, per household, at a rate. |
| 827 | 1 | `public double sentAbroad()` |  |
| 828 | 1 | `public double broughtHome()` |  |
| 829 | 1 | `public double foreignInterest()` |  |
| 832 | 1 | `public double sold()` | Shares sold this month to cover the shop, per household, in cash. |
| 833 | 1 | `public boolean isLockedOut()` |  |
| 834 | 1 | `public double disposable()` |  |
| 835 | 1 | `public double afterFixed()` |  |
| 836 | 1 | `public double interest()` |  |
| 837 | 1 | `public double drawn()` |  |
| 838 | 1 | `public double unfunded()` |  |
| 839 | 1 | `public double borrowed()` |  |
| 841 | 1 | `public double loanFee()` | The bank's fee on this month's borrowing, added to the debt (0.7.7). |
| 843 | 1 | `public double accountFee()` | The bank's account fee this month (0.7.7). |
| 844 | 1 | `public double repaid()` |  |
| 845 | 1 | `public double banked()` |  |
| 846 | 1 | `public double want()` |  |
| 847 | 1 | `public double planned()` |  |
| 848 | 1 | `public double rate()` |  |
| 849 | 1 | `public double subsistence()` |  |
| 852 | 1 | `public double luxuryWant()` | What this household would put over a luxury counter this month. |
| 855 | 1 | `public double carsSold()` | Cars this cell sold second-hand this month, in total. |
| 856 | 1 | `public double bankrupt()` |  |
| 857 | 1 | `public double seizedSavings()` |  |
| 860 | 1 | `public boolean isCutOff()` | True when the bank has stopped lending to this cell - ceiling or lockout. |
| 888 | 1 | `public boolean isGoingShort()` | Whether this household planned to spend less than it wanted to. |
| 891 | 1 | `public double totalSavings()` | The cell's totals: the per-household figure times the households. |
| 892 | 1 | `public double totalAbroad()` |  |
| 893 | 1 | `public double totalPaper()` |  |
| 894 | 1 | `public double totalBonds()` |  |
| 895 | 1 | `public double totalDebt()` |  |
| 896 | 1 | `public double totalInterest()` |  |
| 897 | 1 | `public double totalBorrowed()` |  |
| 898 | 1 | `public double totalAccountFees()` |  |
| 899 | 1 | `public double totalLoanFees()` |  |
| 900 | 1 | `public double totalRepaid()` |  |
| 901 | 1 | `public double totalPlanned()` |  |
| 902 | 1 | `public double totalWant()` |  |
| 908 | 1 | `public double netWorth()` | Net worth of one of these households: what it has less what it owes. |

### the month (lines 910-1586)

| line | len | member | says |
|---:|---:|---|---|
| 929 | 4 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` | Settles a month: the bills in order, the shop against what was actually spent, and savings, then credit, then going without. |
| 938 | 186 | `void settle(double disposablePer, double rentPerHome, double feesPer, double spentPer, double foodPricePerHead, double riskFree...` |  |
| 1130 | 3 | `double plan(double localPerUsd)` | Plans the next month, without settling one. |
| 1138 | 3 | `double plan(double localPerUsd, double paperRatio)` | (0.7.1): the households' book at the curve over its face |
| 1149 | 3 | `double plan(double localPerUsd, double paperRatio, double spendFactor)` | household plans (0.7.3): HouseholdBalance .spendFactor() on the month's real deposit rate, 1 at no real return - see AND WHAT IT SPENDS ANSWERS THE REAL RATE below |
| 1160 | 157 | `double plan(double localPerUsd, double paperRatio, double spendFactor, double bondRatio)` | this month (0.7.12): every cell's holdings together at the bonds' value over their face - in the net worth the wealth term reads, and not in what can be spent, because a bond sells only when somebody buys it |
| 1319 | 11 | `void restrike(double disposablePer, double rentPerHome, double feesPer, double foodPricePerHead, double riskFreeAnnual)` | Re-strikes the fixed part of the month for the plan alone: the load path. |
| 1342 | 6 | `protected double fundShortfall(double still, double disposablePer)` | What is still short after savings, the city's paper, the paper abroad and the shares: the revolving credit line, up to its ceiling. |
| 1350 | 47 | `private double fundCardShortfall(double still, double disposablePer)` | The bank's card draw, distinct from the student's tuition/living loan. |
| 1405 | 1 | `protected double planningRoom()` | What the plan may count on borrowing: the credit room, less the fee drawing it would add (0.7.7), for everyone but a student. |
| 1408 | 4 | `public double capitalRoom()` | What the bank's capital lets this cell draw this month, per household: up to its capitalCeiling. |
| 1414 | 3 | `public double lendableRoom(double disposablePer)` | What the bank will actually lend one of these this month: its credit room, and no more than its capital allows (0.7.8). |
| 1419 | 3 | `public double monthsOwed()` | Months of income this household owes - and with a debt and no income at all, past any credit ceiling. |
| 1424 | 4 | `public double lossAllowance()` | What the bank sets aside against this cell's debt, in total (0.7.8): Bank.cardAllowance() on its late balances - a year's loss on a frozen debt, which nothing discharges while it is frozen. |
| 1430 | 1 | `public double debtInTrouble()` | This cell's debt, in total, behind on its card minimums; nothing when it is not. |
| 1433 | 1 | `protected double studentRepayment()` | The month's student-loan instalment (principal), per household. |
| 1436 | 1 | `protected double studentInterestDue()` | The month's interest on the student loan at the city's rate, per household. |
| 1443 | 1 | `public double studentInterestAt(double annualRate)` | What a month's interest on the loan would be at an annual rate - the same figure studentInterestDue() charges, for a screen previewing a rate the city has not set. |
| 1446 | 5 | `public double creditRoom(double disposablePer)` | What the bank will still lend one of these: the ceiling less what is owed, or nothing. |
| 1459 | 23 | `double chargeOff()` | A share of late balances charges off after allowing for monthly cures. |
| 1484 | 13 | `void clearWorking()` | Nothing to strike: the working is blank, the position stands. |
| 1499 | 14 | `void clearAll()` | The cell is empty: no position either. |
| 1515 | 64 | `void redenominate(double scale)` | Everything in money, in the new unit. |
| 1581 | 5 | `public String toString()` |  |


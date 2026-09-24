# Bank.java - 4,396 lines · 288 methods · 34 constants · model

`ham/citybuildersim/Bank.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The city's commercial bank: every loan in it, and every default.
> 
> ==================== WHY THIS EXISTS ====================
> 
> There were three lenders in this game and none of them was anybody. A sector
> short of cash borrowed from `BusinessDebtManager`, the city sold bonds to a
> market, and - since the household balance sheet went in - a family short of
> the shop borrowed from nobody at all. Money arrived from outside the city and
> interest disappeared out of it, and when a sector was written down in a
> restructure the loss was written off against nothing.
> 
> Jerus: "we need to add a commercial bank, which will be the one everyone,
> everyone lends money from, the one earning all the interest, and the one
> getting billed the horrible bankruptcies."
> 
> So there is one now, and it is a building. What changes is not the arithmetic
> of any single loan - the rates and schedules are the same - but that the
> money has somewhere to come from and somewhere to go, and that somewhere can
> run out and can be bankrupted.
> 
> ==================== WHAT SETS THE PRICE ====================
> 
> Two limits, and the tighter one binds - Jerus's answer, and it is the right
> one because the two fail differently:
> 
>   DEPOSITS. A bank lends a multiple of what the city has banked with it -
>   households' savings and the sectors' cash. A city with no savings is a city
>   with nothing to lend, whatever it has built.
> 
>   BRANCHES. One building can only carry so much of a loan book. A city with
>   deep savings and one branch is a city queueing at one counter.
> 
> Past the tighter of the two the bank does not refuse - it funds the rest
> at the central bank's window (abroad, until 0.7.0) and prices what that
> money costs it into what it charges. Until 0.7.7 it also charged a STRAIN
> PREMIUM, up to eighteen points on every rate in the city once the book
> passed 80% of capacity and the full eighteen with no branch at all - the
> "credit at a punitive rate" a city with no bank was asked to pay. It is
> gone: a loan is priced from what it costs to make (WHAT A LOAN COSTS,
> below), a city with no branch is priced the same way with the window as
> its marginal money, and strain() survives only as a measure.
> 
> ==================== WHAT IS AND IS NOT INSIDE ====================
> 
> The bank holds cash and it is one of MoneyAudit's pools, so lending to a
> SECTOR or to the CITY is now an internal transfer that cancels - the money
> moves from the bank's account to theirs and back with interest, and none of
> it enters or leaves the city any more. That is a strictly better identity
> than the one it replaces, where both ends were the outside world.
> 
> HOUSEHOLDS ARE STILL OUTSIDE IT, as they have always been - wages leave the
> audited system and rent and shopping come back into it - so lending to a
> family is a real outflow and its repayments are real inflows. Their savings
> are counted as deposits for the capacity above WITHOUT being held as the
> bank's cash, which is the one deliberate fudge here and is stated rather than
> hidden: the alternative is to make every household a pool, which is a
> different and much larger model.
> 
> A write-off costs the bank its BOOK, not its cash. Writing off a loan is the
> loss of a receivable; the money went out the door months ago.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (10), [CentralBank](CentralBank.md) (4), [Ladder](Ladder.md) (3), [DebtManager](DebtManager.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Equity](Equity.md) (1)

**Used by (31):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [DebtManager](DebtManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [Motoring](Motoring.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [SummaryScreen](SummaryScreen.md), [TimeSkipReport](TimeSkipReport.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 67 | · the dials |
| 145 | · what a dollar of book WEIGHS |
| 230 | · the position |
| 250 | · the month's working |
| 304 | · the month |
| 359 | · carry |
| 419 | WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7) |
| 500 | · the four parts |
| 644 | · what the four parts remember |
| 850 | · the arithmetic |
| 877 | THE TRADING DESK |
| 953 | · the desk held to its capital (0.7.8) |
| 1564 | · the flows |
| 1639 | THE CITY'S PAPER CHANGES HANDS (0.7.1) |
| 1793 | FEES (0.7.7) |
| 1861 | THE FUNDING SIDE |
| 2097 | · · SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY. |
| 2150 | WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. |
| 2282 | THE THREE STATEMENTS |
| 2394 | · tax |
| 2508 | · LAST MONTH, KEPT ON PURPOSE |
| 2631 | THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 2699 | · the allowance |
| 2950 | · what it holds |
| 3061 | · what it does with profit |
| 3154 | · its own shares |
| 3286 | · what it lends |
| 3353 | · the save |
| 3505 | · reading |
| 3911 | WHAT THE BANK TAB READS (0.7.9) |
| 3940 | · the interest, by who paid it |
| 3977 | · what moved it between two presses |
| 4014 | · its year of statements |
| 4194 | · its rates, in a ladder |
| 4233 | · in words |
| 4288 | · what its book weighs |
| 4316 | · its funding |
| 4340 | · another branch |
| 4361 | · how its equity moved |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3070 | `Bank.Payout.NO_BANK` |  |
| 3070 | `Bank.Payout.FAILED` |  |
| 3070 | `Bank.Payout.UNDER_MINIMUM` |  |
| 3070 | `Bank.Payout.REBUILDING` |  |
| 3070 | `Bank.Payout.PAYING` |  |
| 3070 | `Bank.Payout.RETURNING` |  |
| 4027 | `Bank.Line.INTEREST` |  |
| 4027 | `Bank.Line.FROM_BUSINESSES` |  |
| 4027 | `Bank.Line.FROM_HOUSEHOLDS` |  |
| 4027 | `Bank.Line.FROM_CITY` |  |
| 4027 | `Bank.Line.FROM_CARRY` |  |
| 4027 | `Bank.Line.FROM_RESERVES` |  |
| 4027 | `Bank.Line.DISCOUNT` |  |
| 4028 | `Bank.Line.SAVERS` |  |
| 4028 | `Bank.Line.WINDOW` |  |
| 4028 | `Bank.Line.NET_INTEREST` |  |
| 4029 | `Bank.Line.FEES` |  |
| 4029 | `Bank.Line.ACCOUNT_FEES` |  |
| 4029 | `Bank.Line.LOAN_FEES_PAID` |  |
| 4029 | `Bank.Line.LOAN_FEES_OWED` |  |
| 4030 | `Bank.Line.PROVISIONS` |  |
| 4030 | `Bank.Line.WRITE_OFFS` |  |
| 4030 | `Bank.Line.TRADING` |  |
| 4030 | `Bank.Line.PAPER_GAINS` |  |
| 4030 | `Bank.Line.REVENUE` |  |
| 4031 | `Bank.Line.COSTS` |  |
| 4031 | `Bank.Line.PAYROLL` |  |
| 4031 | `Bank.Line.UPKEEP` |  |
| 4031 | `Bank.Line.PRE_TAX` |  |
| 4031 | `Bank.Line.TAX` |  |
| 4031 | `Bank.Line.NET` |  |
| 4032 | `Bank.Line.DIVIDENDS` |  |
| 4032 | `Bank.Line.BUYBACKS` |  |
| 4032 | `Bank.Line.ISSUED` |  |
| 4032 | `Bank.Line.RETAINED` |  |
| 4033 | `Bank.Line.BOOK` |  |
| 4033 | `Bank.Line.EQUITY` |  |
| 4291 | `Bank.Book.BUSINESSES` |  |
| 4291 | `Bank.Book.CITY` |  |
| 4291 | `Bank.Book.FAMILIES` |  |
| 4291 | `Bank.Book.CARRY` |  |
| 4291 | `Bank.Book.DESK` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `Bank.LEVERAGE` | `6` | How many times its deposits the bank will lend. |
| 104 | `Bank.CAPITAL_RATIO` | `.08` | Equity a bank must hold against its risk-weighted book. |
| 117 | `Bank.DEPOSITS_PER_BRANCH` | `250_000` | How much of a city's savings one branch can gather. |
| 140 | `Bank.PAID_IN_PER_BRANCH` | `32_000` | What the shareholders put up when a branch opens. |
| 168 | `Bank.RISK_CITY` | `.20` | The city cannot default on its own paper. |
| 171 | `Bank.RISK_BUSINESS` | `1.00` | A business can be restructured, and in this game regularly is. |
| 174 | `Bank.RISK_HOUSEHOLD` | `1.00` | ...and a family can be discharged. |
| 190 | `Bank.RISK_CARRY` | `1.00` | A foreign carry borrower, against the risk weights above. |
| 193 | `Bank.SHORTEST_WEIGHT` | `.40` | What a loan repaying tomorrow weighs against one repaying never. |
| 196 | `Bank.LONG_TERM_MONTHS` | `60` | Months of remaining term at which a loan weighs its full amount. |
| 211 | `Bank.EASY_STRAIN` | `.80` | How much of its capacity the bank lends before it counts itself full: the carry trade is lent only the room below it, and a branch is worth what it adds below it. |
| 228 | `Bank.BUILD_AT_STRAIN` | `.70` | Where the private sector starts building, which is BEFORE the bank is full. |
| 479 | `Bank.BASE_LOSS_RATE` | `.004` | What a sound loan is expected to lose a year through the cycle, as a share of the book: 0.4%, about what Canada's big banks provision for credit losses in a normal year (RBC, 2025). |
| 482 | `Bank.COST_WINDOW_MONTHS` | `12` | Months of payroll and upkeep the running costs are measured over: a year, so one month's building bill is not a price. |
| 485 | `Bank.PRIME_TERM_MONTHS` | `BusinessDebtManager.LOAN_TERM_MONTHS` | The term prime is struck at: a business loan's, BusinessDebtManager.LOAN_TERM_MONTHS, which takes the short end's term premium. |
| 498 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 891 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 1179 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 1338 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 1820 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 1823 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 2207 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - set so that this bank... |
| 2210 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of funding bid for it ... |
| 2213 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 2702 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not lend it another dol... |
| 2705 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 2708 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 2953 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 2956 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 2959 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 3050 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point ceiling and the 3.5-p... |
| 3064 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 3067 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 3289 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit at the target (lend... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 120 | `private double depositsPerBranch` | The same, in today's money - reformed with every other figure. |
| 143 | `private double paidInPerBranch` | The same, in today's money. |
| 232 | `private double cash` |  |
| 233 | `private double branches` |  |
| 234 | `private double deposits` |  |
| 237 | `private double householdDeposits` | The two halves of that, because they are paid separately. |
| 238 | `private double sectorDeposits` |  |
| 241 | `private double sectorBook` | What is lent out, by whom it is owed. |
| 242 | `private double cityBook` |  |
| 243 | `private double householdBook` |  |
| 246 | `private double sectorWeighted` | The same three, weighted for risk and remaining term. |
| 247 | `private double cityWeighted` |  |
| 248 | `private double householdWeighted` |  |
| 252 | `private double interestEarned` |  |
| 253 | `private double writeOffs` |  |
| 254 | `private double payroll` |  |
| 255 | `private double upkeep` |  |
| 256 | `private double lentToHouseholds` |  |
| 257 | `private double repaidByHouseholds` |  |
| 258 | `private double fundingCost` |  |
| 301 | `private double placementIncome` | WHAT ITS SPARE CASH EARNS: THE POLICY RATE, AT THE CENTRAL BANK (0.7.0). |
| 302 | `private double openingEquity` |  |
| 316 | `private double carryBook` | The fourth book: what foreigners have borrowed to take abroad. |
| 317 | `private double carryLent, carryRepaid, carryInterest` |  |
| 647 | `private double lastWindowShare` | The funding blend and the running-cost rate, struck at the last close. |
| 648 | `private double lastRunningCost` |  |
| 654 | `private final double[] costRing` | The record the running costs are struck from: a year of payroll and upkeep beside the book they served, in rings indexed by closed months. |
| 655 | `private final double[] costBookRing` |  |
| 656 | `private int pricedMonths` |  |
| 745 | `private double lastCostOfFunds` | What money cost this bank over the month that just closed. |
| 894 | `private double securities` | The desk's inventory, at the exchange's mark. |
| 902 | `private double tradingIncome` | The trading result so far this month: cash from what it sold less cash for what it bought, plus the change in what it holds at the mark, plus the dividends its inventory was paid. |
| 914 | `private double markChange` | The part of the trading result that is the re-mark: every change in what the inventory is carried at this month, summed. |
| 1054 | `private double foreignDeposits` |  |
| 1092 | `private double hotMoneyIn, hotMoneyOut` |  |
| 1144 | `private boolean inResolution` | Failed, and not yet put back on its feet. |
| 1145 | `private int failures` |  |
| 1157 | `private double resolutionLossThisMonth` | ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. |
| 1158 | `private double resolutionLossLifetime` |  |
| 1350 | `private double domesticCapitalScale` | The same, in today's money. |
| 1428 | `private double dividendsPaid` |  |
| 1471 | `private double capitalInjected` |  |
| 1472 | `private double capitalFromHome` |  |
| 1473 | `private double bailoutReceived` |  |
| 1474 | `private double foundingSettlement` |  |
| 1546 | `private double branchesCapitalised` |  |
| 1577 | `private double internalInterest` |  |
| 1654 | `private double paperGains` | The gain or loss on the city's paper that changed hands this month. |
| 1657 | `private double paperBoughtFromHouseholds` | What the desk paid households for their paper this month: money leaving the pools for a household, which MoneyAudit declares. |
| 1660 | `private double paperSoldToCentralBank, paperBoughtFromCentralBank` | What the central bank paid it for paper this month, and what it paid the central bank. |
| 1674 | `private double unearnedDiscount` | THE UNEARNED DISCOUNT (0.7.1): the part of what the bank paid under face for the city's paper it has not yet accreted into income, carried as a liability against the book - so equity does not jump by the discount the ... |
| 1726 | `private double buybackGains` | Gains less losses on paper the treasury bought back, over the city's life. |
| 1826 | `private double accountFeeBase` | ACCOUNT_FEE in today's unit - reseeded and reformed with the other money constants. |
| 1835 | `private double accountFees, loanFeesPaid, loanFeesOwed` | The month's fees: accounts, loans paid in cash (a business's), and loans added to what is owed (a household's). |
| 1937 | `private double depositRate` | PAYING FOR DEPOSITS is the other half of what a bank IS. |
| 1938 | `private double depositInterestToHouseholds` |  |
| 1939 | `private double depositInterestToSectors` |  |
| 1949 | `private double depositInterestToForeign` | ...and what the hot money is paid for parking here. |
| 1957 | `private boolean depositPayoutHeld` |  |
| 1984 | `private double fundingRate` | THE PRICE OF WHOLESALE MONEY WAS TWO DIALS until 0.7.0: FUNDING_SPREAD, two points over the risk-free rate "for being a bank rather than a treasury", and FUNDING_STRETCH, six points more at a reach of one deposit book... |
| 2236 | `private double chosenDepositRate` | The rate the bank chose this month, before rule 2 asked whether its margin could pay it. |
| 2274 | `private int monthsPayoutHeld` | HOW OFTEN RULE 2 HELD THE SAVERS UNDER THE CHOSEN RATE - counted for the run, not saved, for the reason the bid-up's counter was: a rule that never binds looks exactly like one that does not exist. |
| 2421 | `private double taxPaid` | Jerus: "quick question banks are taxed right?" |
| 2422 | `private double profitLastMonth` |  |
| 2491 | `private double struckProfit, carriedLate, restoredLate` | PROFIT THAT LANDS AFTER THE CLOSE (0.7.7). |
| 2492 | `private boolean closedThisMonth` |  |
| 2527 | `private double lastPayroll, lastUpkeep, lastInterest, lastBook` |  |
| 2530 | `private double lastKept` | Last month's interest margin and fees - what its book KEPT, which the branch test asks of a counter since 0.7.7. |
| 2812 | `private final java.util.Map<String, Double> sectorAllowance` | Each sector's allowance by name, what each held when the month opened, and what each was written off by this month. |
| 2813 | `private final java.util.Map<String, Double> openingSectorAllowance` |  |
| 2814 | `private final java.util.Map<String, Double> writtenOffBySector` |  |
| 2816 | `private final java.util.Set<String> sectorsWatched` | The sectors whose books are in stage 2, as the last provide() found them. |
| 2818 | `private final java.util.Map<String, Double> stageTwoShares` | ...and the share of each sector's book in stage 2, firm by firm (0.7.8). |
| 2821 | `private double householdAllowance, openingHouseholdAllowance, householdWrittenOff, householdWatchedDebt` | The families' allowance, the same three, and how much of their debt is in cells in trouble. |
| 2824 | `private double openingAllowance, allowanceUsed` | The whole allowance as the month opened, and how much of the month's write-offs it covered. |
| 2962 | `private final double[] lossRing` | The last year's provisions and weighted book, a ring of the months the bank had a branch. |
| 2963 | `private final double[] riskRing` |  |
| 2964 | `private int lossMonths` |  |
| 2966 | `private double worstLossRate` | The worst year's provisions over its average weighted book it has recorded: the loss the buffer is sized to take. |
| 3149 | `private double payoutProfit, payoutExcess, payoutOverTarget` | What the month's payout read: the profit after tax it was paid on, what the bank held over the top of its band, and over its target. |
| 3185 | `private double sharesBoughtBack, sharesIssued` | THE DESK DEALS IN THE BANK'S OWN SHARES BY ITS CAPITAL RULE (0.7.8): it buys them back from whoever sells while the bank is at or over its own target (buysBackOwnShares()) and only with what it holds over it (buybackR... |
| 3272 | `private final double[] dividendRing` | The owners' year: dividends and buybacks, a ring of months with this one in it. |
| 3273 | `private final double[] buybackRing` |  |
| 3274 | `private int payoutMonths` |  |
| 3951 | `private double interestFromBusinesses, interestFromCity, interestFromHouseholds, discountAccreted` | The month's interest by who paid it: the businesses, the city's coupons, the families' credit lines, and the discount on the city's paper as it is earned. |
| 3985 | `private double treasuryBuybackGain` | What the treasury buying its paper back gained the bank (negative: lost it) since the month opened. |
| 3994 | `private double allowanceOpened` | The allowance a save from before 0.7.8 was given on load (openAllowance()): its equity fell by it between two presses. |
| 4002 | `private double bailoutsLifetime` | What the city has put into it in rescues over its life (receiveBailout()), carried in the solvency record since 0.7.9 - a save from before counts from its load. |
| 4011 | `private boolean monthKnown` | True once the month's lines are a month's: one played, or lines a save carried. |
| 4090 | `private final double[][] statementRing` | The months before this one, a year of them less this one: each filed whole at the top of the month after (startMonth()), when everything booked after its close is in it. |
| 4091 | `private int statementsFiled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 4332 | **type** `public class Bank` | The city's commercial bank: every loan in it, and every default. |

### the dials (lines 67-144)

### what a dollar of book WEIGHS (lines 145-229)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 4 | `public static double maturityWeight(double remainingMonths)` | How much of its face a loan with this long left to run counts for. |

### the position (lines 230-249)

### the month's working (lines 250-303)

### the month (lines 304-358)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 18 | `public void refresh(double branches, double householdSavings, double sectorCash, double sectorBook, double cityBook, double hou...` | Re-reads the city and re-prices credit. |
| 353 | 5 | `public void setWeightedBook(double sector, double city, double household)` | The same book, weighed by what each loan actually is. |

### carry (lines 359-418)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 6 | `public void lendCarry(double amount)` | Lend to a foreigner who is about to take it abroad. |
| 379 | 7 | `public void repayCarry(double amount)` | ...and they bring it home. |
| 396 | 6 | `public void takeCarryInterest(double amount)` | The coupon, which arrives from abroad. |
| 412 | 1 | `public void setCarryBook(double amount)` | The carry book, set from the stock that owns it. |
| 414 | 1 | `public double getCarryBook()` |  |
| 415 | 1 | `public double getCarryLent()` |  |
| 416 | 1 | `public double getCarryRepaid()` |  |
| 417 | 1 | `public double getCarryInterest()` |  |

### WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7) (lines 419-499)

### the four parts (lines 500-643)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 3 | `public double windowShare()` | The share of the bank's marginal money that comes from the window, 0 to 1: struck at the close from how the month ended funded - what it owed the window over everything it had borrowed, 0 while it holds reserves - and... |
| 525 | 4 | `public double fundsTransferPrice(double policyAnnual, int months)` | THE FUNDS-TRANSFER PRICE: what a dollar lent for this many months costs the bank - the policy rate, plus the window's penalty on the share of its money that is the window's, plus the city's term premium for the term (... |
| 547 | 1 | `public double runningCostRate()` | THE RUNNING COSTS: the last year's payroll and upkeep over the book they served, a year - or over what the bank's capital could carry at its own target (capitalTarget(); a fixed 11% until 0.7.8), whichever is larger. |
| 580 | 1 | `public double expectedLossRate()` | THE EXPECTED LOSS: BASE_LOSS_RATE, what a sound book loses a year through the cycle. |
| 583 | 3 | `public static double requiredReturn()` | The return the bank's owners are priced to want: Equity.requiredYield() at the world's rate, 12.5% at the defaults. |
| 596 | 4 | `public double capitalCharge(double policyAnnual, int months, double riskWeight)` | THE CAPITAL CHARGE: the equity a loan of this risk ties up at the bank's own capital target (capitalTarget(), since 0.7.8; the minimum plus a fixed three points before it), times what that equity costs over what the s... |
| 602 | 4 | `public double loanRate(double policyAnnual, int months, double riskWeight)` | A loan of this term and risk weight: the four parts, added up. |
| 608 | 3 | `public double prime(double policyAnnual)` | PRIME: what a sound business pays - the four parts at RISK_BUSINESS, for a business loan's term. |
| 613 | 3 | `public double householdRate(double policyAnnual)` | What a household's credit line starts from: the four parts at RISK_HOUSEHOLD, revolving, so no term premium. |
| 623 | 3 | `public double lendingRate(double policyAnnual)` | What a good credit pays to borrow here: prime, since 0.7.7. |
| 639 | 4 | `public double carryRate(double policyAnnual)` | What the carry trade pays to borrow here: the same costs, at RISK_CARRY and short, since the money is taken abroad and wanted back on demand - with NO expected loss, because Jerus's call is that a carry borrower never... |

### what the four parts remember (lines 644-849)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 16 | `private void strikePrices()` | Adds the month that has just closed to the rings and strikes the two parts that read flows. |
| 677 | 10 | `public double[] pricingHistoryToSave()` | The record, for the save: the count, then the two cost rings. |
| 689 | 9 | `public void restorePricingHistory(double[] in)` | ...and back. |
| 742 | 1 | `public double marginalCostOfFunds()` | WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND. |
| 764 | 8 | `private double blendedCostOfFunds()` | The two tranches, blended at the weights the bank actually used them. |
| 774 | 75 | `public void startMonth()` | Clears the month's flows. |

### the arithmetic (lines 850-876)

| line | len | member | says |
|---:|---:|---|---|
| 862 | 1 | `public double getBook()` | Everything lent, carry included. |
| 871 | 5 | `public double getWeightedBook()` | The book as CAPACITY sees it: risk- and maturity-weighted. |

### THE TRADING DESK (lines 877-952)

| line | len | member | says |
|---:|---:|---|---|
| 917 | 5 | `public void markSecurities(double value)` | The exchange re-marks the inventory. |
| 924 | 5 | `public void deskPays(double cash)` | The desk paid cash for shares. |
| 931 | 5 | `public void deskReceives(double cash)` | ...and was paid for shares it sold. |
| 938 | 5 | `public void receiveDividend(double amount)` | Dividends on the inventory: income, and cash. |
| 945 | 1 | `public void restoreSecurities(double value)` | The load path puts the mark back without calling it income. |
| 947 | 1 | `public double getSecurities()` |  |
| 948 | 1 | `public double getTradingIncome()` |  |
| 951 | 1 | `public double getMarkChange()` | What re-marking the inventory did to this month's trading result. |

### the desk held to its capital (0.7.8) (lines 953-1563)

| line | len | member | says |
|---:|---:|---|---|
| 1001 | 6 | `public double deskCanCarry(double inventory, double price, double mark)` | HOW MANY OF A COMPANY'S SHARES THE DESK MAY STILL BUY ON THE CAPITAL IT HAS: as many as leave the bank at or over its target (targetEquity()) with them on its books - the shares carried at the weight the weighted book... |
| 1009 | 4 | `public double weightingRelief()` | How much lighter the weighting makes the book. |
| 1023 | 12 | `public double capacity()` | The most the bank can lend before it is funding itself at the central bank's window (abroad, until 0.7.0). |
| 1048 | 5 | `public double depositsGathered()` | What the branches let it gather, out of what the city has to bank - PLUS whatever the world has parked here, which needed no branch at all. |
| 1057 | 1 | `public double getForeignDeposits()` | Hot money currently funding this bank, in local money. |
| 1060 | 4 | `public void setForeignDeposits(double amount)` | Told by Game each month, from CapitalFlows. |
| 1072 | 5 | `public void receiveHotMoney(double amount)` | Hot money arriving, as cash. |
| 1086 | 5 | `public void returnHotMoney(double amount)` | ...and leaving, which is the whole danger. |
| 1094 | 1 | `public double getHotMoneyIn()` |  |
| 1095 | 1 | `public double getHotMoneyOut()` |  |
| 1109 | 4 | `public double hotFundingShare()` | The share of the bank's funding that could leave at any time. |
| 1115 | 3 | `public double capitalLimit()` | What its capital supports, on the risk-weighted book. |
| 1120 | 3 | `public boolean capitalBound()` | True when it is the capital that binds rather than the funding. |
| 1130 | 4 | `public double capitalRatio()` | Equity against the risk-weighted book - the ratio a regulator reads. |
| 1160 | 1 | `public boolean isInsolvent()` |  |
| 1162 | 1 | `public double getResolutionLoss()` | Over the city's life. |
| 1164 | 1 | `public double getResolutionLossThisMonth()` | This month only. |
| 1165 | 1 | `public int getFailures()` |  |
| 1202 | 4 | `public double[] solvencyToSave()` | The solvency record, for the save. |
| 1207 | 8 | `public void restoreSolvency(double[] state)` |  |
| 1240 | 24 | `public void resolveIfFailed()` | The bank fails, and its creditors take the loss. |
| 1274 | 22 | `public double resolutionExitEquity()` | The equity at which the freeze lifts, however the bank gets there. |
| 1319 | 8 | `public double recapitalisationNeeded()` | What it would take to put the bank back on its feet. |
| 1353 | 5 | `public double domesticCapitalShare()` | The share of new paid-in capital the city's own savers can find. |
| 1382 | 13 | `public void injectCapital(double amount)` | Shareholders' money, put in as capital - and WHOSE shareholders. |
| 1406 | 7 | `public void injectCapital(double fromHome, double fromAbroad)` | Shareholders' money, put in as capital, and whose: the households' part came out of their savings through the register, the rest from the world. |
| 1421 | 6 | `public void payDividend(double amount)` | Pays the owners. |
| 1431 | 1 | `public double getDividendsPaid()` | What it paid its shareholders this month. |
| 1462 | 8 | `public void receiveBailout(double amount)` | The same money, from the TREASURY rather than from shareholders. |
| 1480 | 1 | `public double getFoundingSettlement()` | The one-off jump in the bank's cash when the city's first branch opens and it takes over the standing loan book. |
| 1483 | 1 | `public double getCapitalInjected()` | Shareholders' money from OUTSIDE the city. |
| 1492 | 1 | `public double getCapitalFromHome()` | ...and from the city's own savers. |
| 1495 | 1 | `public double getBailoutReceived()` | The treasury's money, from inside it. |
| 1509 | 36 | `public double openBranches(double nowStanding)` | Opens whatever branches have been built since last month, and says what capital they need. |
| 1547 | 1 | `public double getBranchesCapitalised()` |  |
| 1548 | 1 | `public void setBranchesCapitalised(double n)` |  |
| 1558 | 5 | `public double strain()` | Book over capacity. |

### the flows (lines 1564-1638)

| line | len | member | says |
|---:|---:|---|---|
| 1570 | 6 | `public void takeInterest(double amount)` | Interest arriving from a SECTOR or the CITY - both inside the audit, so this is a transfer between pools and declares nothing. |
| 1580 | 1 | `public double getInternalInterest()` | The part of the month's interest that came from inside the city's pools. |
| 1589 | 6 | `public void takeDiscount(double amount)` | Paper bought below par: income the bank earns without any cash moving. |
| 1614 | 3 | `public double sellPaperBack(double price, double principal)` | THE TREASURY BUYS ITS PAPER BACK FROM THE BANK (0.7.0), which is who holds it: the price arrives as cash, the book drops by the principal at once rather than at the next refresh, and the difference is the bank's gain ... |
| 1625 | 13 | `public double sellPaperBack(double price, double principal, double unearned)` | ...and since 0.7.1 at amortised cost: the unearned discount riding on the face that leaves goes with it, so the gain is the price less what the book carried the paper at - face less the discount not yet earned. |

### THE CITY'S PAPER CHANGES HANDS (0.7.1) (lines 1639-1792)

| line | len | member | says |
|---:|---:|---|---|
| 1662 | 1 | `public double getPaperSoldToCentralBank()` |  |
| 1663 | 1 | `public double getPaperBoughtFromCentralBank()` |  |
| 1676 | 1 | `public void setUnearnedDiscount(double amount)` |  |
| 1677 | 1 | `public double getUnearnedDiscount()` |  |
| 1678 | 1 | `public double getPaperGains()` |  |
| 1679 | 1 | `public double getPaperBoughtFromHouseholds()` |  |
| 1681 | 6 | `private void addToCityBook(double face)` |  |
| 1689 | 10 | `public double buyPaperFromHouseholds(double price, double face, double unearned)` | A household sells this face to the desk for this price. |
| 1701 | 11 | `public double sellPaperToCentralBank(double price, double face, double unearned)` | The central bank buys this face from the bank's book, in money it made. |
| 1714 | 10 | `public double buyPaperFromCentralBank(double price, double face, double unearned)` | ...and sells it back: the bank pays, and the face returns to its book. |
| 1727 | 1 | `public double getBuybackGains()` |  |
| 1730 | 4 | `public void takeRepayment(double amount)` | Principal coming back. |
| 1736 | 4 | `public void lend(double amount)` | Money out the door. |
| 1742 | 5 | `public void lendToHouseholds(double amount)` | Lending to a family, which crosses the audit's boundary and is counted. |
| 1749 | 4 | `public void takeFromHouseholds(double principal, double interest)` | ...and from a household, which is outside, so both halves are declared. |
| 1762 | 4 | `public void writeOff(double amount)` | A loan that will not be repaid. |
| 1773 | 5 | `public void writeOffSector(String sector, double amount)` | ...on a business's book, by name (0.7.8), so the month's provide() can draw it against the allowance that sector's book opened the month holding. |
| 1780 | 5 | `public void writeOffHouseholds(double amount)` | ...and on the families' book, the debts of those discharged this month. |
| 1787 | 5 | `public void payRunning(double payroll, double upkeep)` | Wages and running costs, which are real money leaving. |

### FEES (0.7.7) (lines 1793-1860)

| line | len | member | says |
|---:|---:|---|---|
| 1829 | 4 | `public double accountFee(double priceIndex)` | A month's account fee per housed household at this price index, in today's money - nothing in a city with no branch, which has no bank to hold an account at. |
| 1838 | 5 | `public void takeAccountFees(double amount)` | The households' account fees, in cash from outside the pools. |
| 1845 | 5 | `public void takeLoanFees(double amount)` | Loan fees a business paid out of its proceeds: cash from another pool. |
| 1852 | 4 | `public void bookLoanFees(double amount)` | Loan fees added to what the households owe: income now, no cash until they repay, and the book carries them from the next refresh. |
| 1857 | 1 | `public double getAccountFees()` |  |
| 1858 | 1 | `public double getLoanFeesPaid()` |  |
| 1859 | 1 | `public double getLoanFeesOwed()` |  |

### THE FUNDING SIDE (lines 1861-2149)

| line | len | member | says |
|---:|---:|---|---|
| 1952 | 1 | `public double depositRate()` | The rate savers are being paid, a year: the month's payout over the deposits - the rate the bank chose, unless its interest margin could not pay it (isDepositPayoutHeld()). |
| 1955 | 1 | `public boolean isDepositPayoutHeld()` | True when rule 2 of WHAT TO PAY SAVERS held the savers under the rate the bank chose - its margin, after its running costs or at the savers' share, could not pay it. |
| 1959 | 1 | `public double getDepositInterestToHouseholds()` |  |
| 1960 | 1 | `public double getDepositInterestToSectors()` |  |
| 1961 | 1 | `public double getDepositInterestToForeign()` |  |
| 1962 | 4 | `public double depositInterest()` |  |
| 1968 | 4 | `public double netInterestMargin()` | What it charges borrowers, less what it pays savers. |
| 1987 | 1 | `public double borrowings()` | Everything it owes: the mirror of a negative cash position. |
| 1997 | 1 | `public double depositFunding()` | The cheap tranche - the city's own money, lent back out. |
| 2000 | 1 | `public double wholesaleFunding()` | ...and the part it had to go to the window for. |
| 2007 | 1 | `public double fundingRate()` | What the bank is paying for the money it did not have: the window's rate, policy plus CentralBank.WINDOW_PENALTY. |
| 2020 | 129 | `public void fundToCover(double policyAnnual)` | Settles the funding for the month: charge for what was borrowed, then borrow what is short or repay what is spare. |

### WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. (lines 2150-2281)

| line | len | member | says |
|---:|---:|---|---|
| 2221 | 7 | `public double fundingPosition()` | How the bank is funded, 0 to 1: 0 while it holds reserves (its cash is positive), 1 once it is borrowing at the window, and in between the share of what its branches gathered that it has lent out. |
| 2230 | 4 | `public double depositShare()` | The share of the policy rate the bank's funding asks it to pass on: between DEPOSIT_SHARE_FLUSH and DEPOSIT_SHARE_AT_WINDOW, by fundingPosition(). |
| 2237 | 1 | `public double getChosenDepositRate()` |  |
| 2249 | 23 | `private double chooseDepositRate(double policyAnnual)` | The month's deposit interest, in money. |
| 2275 | 1 | `public int getMonthsPayoutHeld()` |  |
| 2278 | 1 | `public double getFundingCost()` | What the window charged this month, paid to the central bank. |
| 2280 | 1 | `public double getPlacementIncome()` | What its reserves earned at the central bank this month, at the policy rate. |

### THE THREE STATEMENTS (lines 2282-2393)

| line | len | member | says |
|---:|---:|---|---|
| 2305 | 1 | `public double cashReserves()` | Cash it is actually sitting on. |
| 2312 | 1 | `public double totalAssets()` | Total assets: the loan book net of what it has set aside against it (netLoans(), since 0.7.8), whatever cash it has not lent, and what the desk holds. |
| 2315 | 1 | `public double netLoans()` | The loans as the balance sheet carries them (0.7.8): what is owed, less the allowance for what will not come back. |
| 2318 | 1 | `public double shortSecurities()` | ...and a desk that is short owes the shares: a liability at the mark. |
| 2345 | 3 | `public double totalLiabilities()` | What the bank owes: what it borrowed to fund its book (past its deposits, at the central bank's window since 0.7.0), and the hot money. |
| 2357 | 1 | `public double equity()` | The residual - and, once the two above are written out, simply the book plus the cash position. |
| 2360 | 1 | `public double getOpeningEquity()` | Equity as it stood at the top of the month. |
| 2365 | 1 | `public double interestIncome()` | What every borrower paid it this month. |
| 2368 | 3 | `public double netInterestIncome()` | ...less what it paid savers and what it paid the window. |
| 2373 | 1 | `public double feeIncome()` | ...plus its fees, since 0.7.7: the accounts, and the loans written. |
| 2383 | 1 | `public double afterLosses()` | ...less the provision for the loans that will not come back (0.7.8): what the allowance rose by, and whatever the month wrote off that it had not already set aside - provisions(). |
| 2386 | 1 | `public double operatingExpenses()` | ...less the tellers and the lights. |
| 2389 | 1 | `public double afterTrading()` | ...plus what the desk made or lost. |
| 2392 | 1 | `public double profitBeforeTax()` | What it made before the city took its share. |

### tax (lines 2394-2507)

| line | len | member | says |
|---:|---:|---|---|
| 2437 | 1 | `public double getProfitLastMonth()` | Profit the tax is charged on: the month that has just finished - and, since 0.7.7, what the month before it earned after its own close (getCarriedLate(); see PROFIT THAT LANDS AFTER THE CLOSE). |
| 2440 | 43 | `public void closeMonth()` | Called at the end of the month, once the profit is final. |
| 2495 | 3 | `public double lateProfit()` | What this month earned after its close: the part of its profit next month's tax and dividend will carry. |
| 2500 | 1 | `public double getCarriedLate()` | What last month earned after its close, inside getProfitLastMonth(). |
| 2503 | 4 | `public void restoreLateProfit(double value)` | The load path: what the saved month earned after its close. |

### LAST MONTH, KEPT ON PURPOSE (lines 2508-2630)

| line | len | member | says |
|---:|---:|---|---|
| 2532 | 7 | `public double[] lastMonthToSave()` |  |
| 2563 | 17 | `public void restoreLastMonth(double[] state)` | ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the cost-of-funds floor. |
| 2582 | 1 | `public void setProfitLastMonth(double value)` | Restored from the save, so next month taxes the right figure. |
| 2594 | 1 | `public void setDepositRate(double rate)` | What savers were paid in the month this save was taken in. |
| 2597 | 1 | `public double getTaxPaid()` | What the city took this month. |
| 2610 | 5 | `public double chargeTax(double annualProfitRate)` | Hands the city its share of last month's profit. |
| 2617 | 3 | `private static double taxOn(double profit, double profitTaxRate)` | The city's share of a month's profit at this rate: nothing on a loss. |
| 2627 | 3 | `public double getProfitAfterTaxLastMonth(double profitTaxRate)` | Last month's profit AFTER the tax it will be charged at this rate: what the owners are paid a share of (Game.payDividends()) and what the register records (Equity.recordMonth()), as every sector's own net income is. |

### THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 2631-2698)

### the allowance (lines 2699-2949)

| line | len | member | says |
|---:|---:|---|---|
| 2711 | 3 | `public static double lossIfDefaulted(double principal, double assets)` | What the BACKSTOP would cost the bank on a business owing this against these assets (BusinessDebtManager.restructure()): everything owed past BusinessDebtManager.RESTRUCTURE_TARGET of its assets - all of it, for a sec... |
| 2716 | 4 | `public static boolean sectorWatched(double principal, double assets)` | A business borrower in trouble: owing past SECTOR_WATCH_LEVERAGE of its assets, or anything at all against none. |
| 2729 | 6 | `public static double stageTwoShare(double principal, double assets)` | The share of a sector's firms, by what they owe, past the watch line: the curve's spread of fortunes (BusinessDebtManager.ASSET_VOLATILITY) read at SECTOR_WATCH_LEVERAGE instead of the default point, N(ln(L / SECTOR_W... |
| 2763 | 3 | `public static double sectorAllowance(double principal, double assets)` | The allowance a business's book holds, read off the curve its firms default on (0.7.8) - the same PD(L) that writes the month's slice off (BusinessDebtManager.defaultProbability()), so the allowance is what the slices... |
| 2776 | 13 | `public static double sectorAllowance(double owed, double principal, double assets)` | ...ON WHAT IT OWES NOW, READ AT ITS QUARTER (0.7.8): the curve read at principal over assets - the averages of its last quarter's readings (BusinessDebtManager.quarterPrincipal(), quarterAssets()) - and the loss struc... |
| 2791 | 3 | `public static boolean householdWatched(double monthsOwed)` | A household cell in trouble: owing past HOUSEHOLD_WATCH_MONTHS of its income. |
| 2802 | 8 | `public static double householdAllowance(double debt, double monthsOwed)` | The allowance a household cell's debt holds: its year's expected loss while sound; once it is in trouble its whole debt - a discharge writes all of it off (Household.discharge()) - scaled from nothing at HOUSEHOLD_WAT... |
| 2844 | 17 | `public void provide(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | THE MONTH'S PROVISION: sets every book's allowance from its borrowers as they stand now, and draws the month's write-offs against what each book held when the month opened. |
| 2869 | 11 | `public void openAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | ...and a bank that has never held one: the allowance its borrowers call for, set up WITHOUT a provision - the month it opens on holds it from its start. |
| 2881 | 21 | `private void strikeAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` |  |
| 2904 | 5 | `public double getAllowance()` | Everything set aside against the book. |
| 2911 | 1 | `public double getSectorAllowance()` | ...against the businesses' book, all of it. |
| 2913 | 1 | `public double getSectorAllowance(String sector)` | ...against one sector's. |
| 2915 | 1 | `public double getWrittenOff(String sector)` | What this month wrote off one sector's book - its defaulted firms' slice, or the backstop (0.7.8: the Bank tab's "this month", beside the allowance). |
| 2917 | 1 | `public double getHouseholdAllowance()` | ...against the families'. |
| 2919 | 1 | `public int getStage(String sector)` | 2 when that sector's book is in trouble - most of its firms past the watch line - 1 when most are sound. |
| 2921 | 1 | `public double getStageTwoShare(String sector)` | The share of that sector's book in stage 2, as the last provide() struck it (stageTwoShare()). |
| 2923 | 1 | `public int getHouseholdStage()` | 2 when any family's line is in trouble, 1 when none is. |
| 2925 | 1 | `public double getHouseholdWatchedDebt()` | The families' debt in the cells that are in trouble. |
| 2927 | 1 | `public java.util.Set<String> getSectorsWatched()` | The sectors whose books are in stage 2. |
| 2929 | 1 | `public int getBooksWatched()` | How many of its books are in stage 2: each sector's, and the families' as one (0.7.9, the Bank tab's count of borrowers in trouble). |
| 2931 | 1 | `public double getOpeningAllowance()` | The allowance the month opened with. |
| 2939 | 1 | `public double provisions()` | THE PROVISION, the income statement's line: what the allowance rose by this month and whatever was written off that it had not set aside - which is the allowance's move plus every write-off. |
| 2942 | 1 | `public double getAllowanceUsed()` | The month's write-offs that the allowance had already set aside. |
| 2945 | 1 | `public double getWriteOffsBeyondAllowance()` | ...and the part it had not, which reached the statement the month it was written off. |
| 2948 | 1 | `public double getProvisionCharge()` | The part of the provision that went into the allowance: its rise less what the write-offs drew out of it. |

### what it holds (lines 2950-3060)

| line | len | member | says |
|---:|---:|---|---|
| 2969 | 10 | `private void recordLosses()` | Files the month that has just closed into the loss record. |
| 2996 | 8 | `public double trailingLossRate()` | The last twelve recorded months' provisions over their average weighted book - or over what its branches' founding capital is built to carry at the minimum (branches x paidInPerBranch / CAPITAL_RATIO), whichever is la... |
| 3006 | 1 | `public double getWorstLossRate()` | The worst year it has lived through, as the capital target reads it. |
| 3053 | 1 | `public double capitalBuffer()` | Its buffer over the minimum: the worst year it has recorded, never less than CONSERVATION_BUFFER nor more than MAX_BUFFER. |
| 3056 | 1 | `public double capitalTarget()` | THE TARGET it chooses: the city's minimum and its own buffer. |
| 3059 | 1 | `public double capitalTop()` | ...and the top of its band. |

### what it does with profit (lines 3061-3153)

| line | len | member | says |
|---:|---:|---|---|
| 3070 | 1 | **type** `public enum Payout` | What the bank does with its profit, as its capital stands. |
| 3073 | 1 | `public double targetEquity()` | The equity its target calls for on the book it has. |
| 3083 | 3 | `public double topEquity()` | The equity at the top of its band - and NEVER LESS THAN WHAT ITS STANDING BRANCHES WERE FOUNDED WITH, paidInPerBranch each: the capital a counter is opened with is what the running costs are already priced on (strikeP... |
| 3088 | 4 | `public double excessCapital()` | What it holds past the top of its band: what it returns, a twelfth a month. |
| 3094 | 8 | `public Payout payoutStance()` | Where its capital puts it, for the words and the rules. |
| 3104 | 10 | `public String payoutDecision()` | ...in words, for the Bank tab. |
| 3124 | 7 | `public double dividendDue(double profitAfterTax)` | WHAT IT PAYS ITS OWNERS this month, on last month's profit after tax. |
| 3139 | 8 | `public double payOwners(double profitAfterTax)` | Pays its owners what dividendDue() says, and keeps what the rule read - the profit it was paid on, the excess over the top and the room over the target - so the month's decision can be read back. |
| 3150 | 1 | `public double getPayoutProfit()` |  |
| 3151 | 1 | `public double getPayoutExcess()` |  |
| 3152 | 1 | `public double getPayoutOverTarget()` |  |

### its own shares (lines 3154-3285)

| line | len | member | says |
|---:|---:|---|---|
| 3188 | 6 | `public void buyBackOwnShares(double paid)` | The desk bought the bank's own shares back and cancelled them: cash out, equity down, no income. |
| 3196 | 5 | `public void issueOwnShares(double received)` | ...and issued new ones: cash in, equity up, no income. |
| 3207 | 4 | `public boolean buysBackOwnShares()` | True when the desk buys the bank's own shares back from whoever sells: standing, and at or over its own capital target. |
| 3222 | 4 | `public boolean issuesOwnShares()` | ...and when it issues new ones to whoever buys: standing, lending, and UNDER its own target - raising the capital its rule says it is short of, and never while it holds what it wants. |
| 3237 | 6 | `public double spareCapital(double inventory)` | WHAT IT HOLDS OVER ITS TARGET: its equity less targetEquity(), with the desk's inventory carried at `inventory` rather than at the securities line's last mark - the line lags the desk's deals within a month until the ... |
| 3245 | 1 | `public double spareCapital()` | ...on the books as they stand: equity() less targetEquity(). |
| 3264 | 3 | `public double buybackRoom(double inventory)` | THE MOST IT MAY SPEND BUYING ITS OWN SHARES BACK NOW: what it holds over its target, spareCapital(inventory), so that no purchase takes it under the target - a month's buybacks never exceed the capital over target at ... |
| 3268 | 1 | `public double getSharesBoughtBack()` |  |
| 3269 | 1 | `public double getSharesIssued()` |  |
| 3277 | 1 | `public double dividendsOverYear()` | Dividends over the last twelve months, this one included. |
| 3279 | 1 | `public double buybacksOverYear()` | ...and its own shares bought back. |
| 3282 | 3 | `public double returnOnEquity()` | This month's net income over the equity it opened with, a year: the return a bank is read by. |

### what it lends (lines 3286-3352)

| line | len | member | says |
|---:|---:|---|---|
| 3300 | 12 | `public double lendingGrowthLimit()` | HOW FAST THE BANK LETS A BORROWER'S DEBT GROW THIS MONTH, on the capital it has: no limit at or over its target (and with no branch - a city with no bank is lent to from outside); none under the minimum or failed; in ... |
| 3314 | 6 | `public boolean lendsOnlyToKeepBorrowersGoing()` | True when it lends only what keeps its existing borrowers going: under the minimum, or failed. |
| 3322 | 4 | `public double lendingLimit()` | The growth of the book the capital rule allows this month, in money: infinite when it lends freely. |
| 3328 | 8 | `public String lendingStance()` | ...in words. |

### the save (lines 3353-3504)

| line | len | member | says |
|---:|---:|---|---|
| 3364 | 18 | `public java.util.Map<String, double[]> allowanceToSave()` | The allowance, book by book (0.7.8): each sector's name, and HOUSEHOLD_BOOK for the families, to {the allowance, what it held when the month opened, what the month wrote off, and whether it is in trouble - 1 or 0 for ... |
| 3384 | 29 | `public boolean restoreAllowance(java.util.Map<String, double[]> saved)` | ...and back. |
| 3414 | 5 | `private static double sum(java.util.Map<String, Double> m)` |  |
| 3427 | 12 | `public double[] capitalRecordToSave()` | The record the target and the owners' year are struck from (0.7.8): the months recorded, the worst year, the rings of provisions and of the weighted book, the owners' month count and the rings of dividends and buybacks. |
| 3440 | 11 | `public void restoreCapitalRecord(double[] in)` |  |
| 3461 | 18 | `public double[] monthLinesToSave()` | THE MONTH'S STATEMENT LINES (0.7.8), for the save. |
| 3481 | 23 | `public void restoreMonthLines(double[] v)` | ...and back. |

### reading (lines 3505-3910)

| line | len | member | says |
|---:|---:|---|---|
| 3507 | 1 | `public double getCash()` |  |
| 3508 | 1 | `public double getDeposits()` |  |
| 3509 | 1 | `public double getBranches()` |  |
| 3510 | 1 | `public double getSectorBook()` |  |
| 3511 | 1 | `public double getCityBook()` |  |
| 3512 | 1 | `public double getHouseholdBook()` |  |
| 3513 | 1 | `public double getInterestEarned()` |  |
| 3514 | 1 | `public double getWriteOffs()` |  |
| 3515 | 1 | `public double getPayroll()` |  |
| 3516 | 1 | `public double getUpkeep()` |  |
| 3517 | 1 | `public double getLentToHouseholds()` |  |
| 3518 | 1 | `public double getRepaidByHouseholds()` |  |
| 3528 | 3 | `public double getNetIncome()` | The month's profit: interest earned, less the cost of the money, less the loans that died, less the cost of running the place. |
| 3540 | 31 | `public boolean wantsBranch()` | True when the city should be opening another counter. |
| 3600 | 41 | `public boolean branchWouldPayForItself()` | Whether the counter earns more than it costs to keep open. |
| 3657 | 7 | `public double bookAnotherBranchWouldCarry()` | The loan book one more branch would take onto the bank's own account. |
| 3676 | 8 | `public double capacityWith(double branchCount)` | Capacity this bank would have with a given number of branches. |
| 3697 | 7 | `public double headroom()` | How much more it could lend before it counts itself full (EASY_STRAIN): what the carry trade may take - and, since 0.7.8, no more than keeps the bank at its own capital target. |
| 3705 | 1 | `public void setCash(double value)` |  |
| 3707 | 55 | `public void reset()` |  |
| 3782 | 119 | `public void redenominate(double scale)` | Every figure on the bank's balance sheet, in the new unit. |
| 3904 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### WHAT THE BANK TAB READS (0.7.9) (lines 3911-3939)

### the interest, by who paid it (lines 3940-3976)

| line | len | member | says |
|---:|---:|---|---|
| 3958 | 9 | `public void takeInterest(double fromCity, double fromBusinesses)` | The businesses' interest and the city's coupons, settled together: the same cash and income as takeInterest() on their sum, to the bit, and each kept by who paid it. |
| 3969 | 1 | `public double getInterestFromBusinesses()` | What the businesses paid it in interest this month. |
| 3971 | 1 | `public double getInterestFromCity()` | ...the city, in coupons on the paper the bank holds. |
| 3973 | 1 | `public double getInterestFromHouseholds()` | ...the families, on their credit lines. |
| 3975 | 1 | `public double getDiscountAccreted()` | ...and the discount on the city's paper it earned this month, which no cash carries. |

### what moved it between two presses (lines 3977-4013)

| line | len | member | says |
|---:|---:|---|---|
| 3986 | 1 | `public double getTreasuryBuybackGain()` |  |
| 3995 | 1 | `public double getAllowanceOpened()` |  |
| 4003 | 1 | `public double getBailoutsLifetime()` |  |
| 4012 | 1 | `public boolean isMonthKnown()` |  |

### its year of statements (lines 4014-4193)

| line | len | member | says |
|---:|---:|---|---|
| 4026 | 9 | **type** `public enum Line` | The lines of a month's statement the Bank tab sets beside last month's and adds up over a year, every one money: the interest and who paid it; what savers and the window were paid; fees and their three kinds; provisio... |
| 4037 | 1 | `public double revenue()` | What it earned before provisions and costs: net interest, fees, the desk and the city's paper - what its costs are read against. |
| 4045 | 1 | `public double getRetained()` | What it kept of the month's profit once its owners were paid: net income less the dividend and its own shares bought back. |
| 4048 | 35 | `public double thisMonth(Line line)` | A line as this month stands. |
| 4093 | 5 | `private void fileStatement()` |  |
| 4100 | 1 | `public boolean knowsLastMonth()` | True when last month is on file: a month was played before this one, or a save carried it. |
| 4103 | 4 | `public double lastMonth(Line line)` | A line as last month ended, everything booked after its close included. |
| 4109 | 1 | `public int monthsInYear()` | How many months the year's figures cover: this one and those on file, YEAR_MONTHS at most. |
| 4112 | 8 | `public double overYear(Line line)` | A line added up over monthsInYear(), this month included - for the flows; the two stocks want averageOverYear(). |
| 4122 | 1 | `public double averageOverYear(Line line)` | ...and averaged over them: a month's worth. |
| 4129 | 4 | `public double returnOnEquityOverYear()` | What it earned over the year, at a yearly rate, on the equity it held on average: the return a bank is read by, and steadier than a month's (returnOnEquity()). |
| 4139 | 4 | `public double provisionRateOverYear()` | Provisions over the year, at a yearly rate, as a share of the book it held on average: its credit losses as a bank reports them - a sound book's is BASE_LOSS_RATE. |
| 4149 | 4 | `public double netInterestMarginOverYear()` | Net interest income over the year, at a yearly rate, on the book it held on average: its net interest margin, steadier than a month's (netInterestMargin()). |
| 4159 | 4 | `public double costShareOverYear()` | Its staff and branches over the year as a share of what it earned before them (revenue()): the efficiency ratio, about 50-60% at a real bank. |
| 4165 | 10 | `public double[] statementYearToSave()` | The year of statements, for the save: how many are filed, how many lines each, then the ring's months in slot order. |
| 4182 | 11 | `public void restoreStatementYear(double[] in)` | ...and back. |

### its rates, in a ladder (lines 4194-4232)

| line | len | member | says |
|---:|---:|---|---|
| 4206 | 15 | **type** `public record Ladder(double policy, double savers, double saversChose, double saversShare, double fundingPo...` | THE LADDER OF ITS RATES at one policy rate, read at one moment: the policy rate; what savers were paid, the rate the bank chose, the share of the policy rate its funding asks it to pass on and the funding position tha... |
| 4211 | 1 | `public double saversOverPolicy()` _(in Bank.Ladder)_ | Savers' rate less the policy rate: under it by the bank's margin on a deposit. |
| 4213 | 1 | `public double transferOverPolicy()` _(in Bank.Ladder)_ | The funds-transfer price over the policy rate: the window's penalty on its share, and the term premium. |
| 4215 | 1 | `public double primeOverTransfer()` _(in Bank.Ladder)_ | Prime over the funds-transfer price: the running costs, the expected loss and the capital charge. |
| 4217 | 1 | `public double parts()` _(in Bank.Ladder)_ | The four parts added up in prime's own order - which is prime. |
| 4219 | 1 | `public double overPrime(double rate)` _(in Bank.Ladder)_ | A borrower's rate over prime: the step each borrower's rung is labelled with - its own risk, or for the carry trade the costs it does not carry. |
| 4223 | 9 | `public Ladder ladder(double policyAnnual)` | The ladder at this policy rate. |

### in words (lines 4233-4287)

| line | len | member | says |
|---:|---:|---|---|
| 4244 | 24 | `public String status()` | THE BANK'S STATE IN ONE SENTENCE, with the figure that decides it: the first thing the Bank tab says. |
| 4270 | 17 | `public String targetReason()` | Why its capital target is what it is, in words: the cap, its worst year, or the standard buffer and why. |

### what its book weighs (lines 4288-4315)

| line | len | member | says |
|---:|---:|---|---|
| 4291 | 1 | **type** `public enum Book` | The five things on its books that capacity weighs: the four it lends on and the desk's shares. |
| 4299 | 1 | **type** `public record WeightRow(Book book, double face, double term, double risk, double weighted)` | One row of what the book weighs: its face; the share of it its remaining term counts for (maturityWeight(), on average over its loans - 1 where nothing runs off); its risk weight; and what it weighs, face x term x risk. |
| 4302 | 8 | `public java.util.List<WeightRow> weightTable()` | Every row, the desk's shares included: the weighted column foots to getWeightedBook(). |
| 4311 | 4 | `private static WeightRow weightRow(Book book, double face, double risk, double weighted)` |  |

### its funding (lines 4316-4339)

| line | len | member | says |
|---:|---:|---|---|
| 4319 | 1 | `public double getHouseholdDeposits()` | What the families have banked with it. |
| 4321 | 1 | `public double getSectorDeposits()` | ...and what the businesses hold in credit. |
| 4324 | 1 | `public double getDepositsPerBranch()` | How much of a city's savings one branch reaches, in today's money: DEPOSITS_PER_BRANCH, reformed with every other figure. |
| 4326 | 1 | `public double getPaidInPerBranch()` | What its owners put up when a branch opens, in today's money: PAID_IN_PER_BRANCH, reformed. |
| 4329 | 1 | `public double branchReach()` | How much of the city's own savings its branches can reach: the branches standing times getDepositsPerBranch(). |
| 4331 | 1 | `public double localDeposits()` | The city's own savings with it: everything banked, less the world's. |
| 4333 | 1 | `public double localDepositsReached()` | ...the part its branches reach - which, with the world's, is depositsGathered(). |
| 4335 | 1 | `public double localDepositsBeyondReach()` | ...and the part they do not: savings only another branch would reach. |
| 4338 | 1 | `public double fundingLimit()` | What its funding would carry: what its branches gathered, lent LEVERAGE times over - the second of capacity()'s two limits. |

### another branch (lines 4340-4360)

| line | len | member | says |
|---:|---:|---|---|
| 4343 | 1 | `public double capacityAnotherBranchWouldAdd()` | The capacity one more branch would add, the capital it would open with counted: nothing when the deposits are the limit and the branches already reach them all. |
| 4346 | 1 | `public double overflowPastComfortable()` | The weighted book past what the bank comfortably carries, EASY_STRAIN of its capacity: what another branch could take onto its own account. |
| 4349 | 1 | `public double runningCostPerBranch()` | What one branch cost to run last month: its payroll and upkeep over the branches standing. |
| 4357 | 3 | `public double keptPerBranch()` | What one branch's share of the book kept last month: its book per branch at last month's kept margin (net interest and fees over the book) - what branchWouldPayForItself() weighs against runningCostPerBranch(). |

### how its equity moved (lines 4361-4396)

| line | len | member | says |
|---:|---:|---|---|
| 4378 | 10 | **type** `public record EquityMovement(double opening, double kept, double fromShareholders, double fromCity, double ...` | HOW ITS EQUITY MOVED since the month opened, every cause named: what it kept (net income); capital put in by its shareholders at home and abroad, and by the city in a rescue; the founding settlement, the month the cit... |
| 4383 | 4 | `public double residual()` _(in Bank.EquityMovement)_ | What none of the causes explains. |
| 4390 | 5 | `public EquityMovement equityMovement()` | The month's movement, as it stands. |


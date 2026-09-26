# Bank.java - 5,643 lines · 371 methods · 49 constants · model

`ham/citybuildersim/Bank.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (13), [Sectors](Sectors.md) (7), [CentralBank](CentralBank.md) (4), [Mortgage](Mortgage.md) (3), [Ladder](Ladder.md) (3), [DebtManager](DebtManager.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Equity](Equity.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (35):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildScreen](BuildScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [DebtManager](DebtManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [SummaryScreen](SummaryScreen.md), [TimeSkipReport](TimeSkipReport.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 67 | · the dials |
| 175 | · what a dollar of book WEIGHS |
| 277 | · the position |
| 319 | · the month's working |
| 373 | · the month |
| 492 | THE CORPORATE BONDS IT HOLDS (0.7.12) |
| 583 | · carry |
| 643 | WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7) |
| 728 | · the four parts |
| 880 | THE BANK PRICES CONCENTRATION (0.7.12) |
| 1181 | · what the four parts remember |
| 1407 | · the arithmetic |
| 1437 | THE TRADING DESK |
| 1515 | · the desk held to its capital (0.7.8) |
| 2169 | · the flows |
| 2244 | THE CITY'S PAPER CHANGES HANDS (0.7.1) |
| 2398 | FEES (0.7.7) |
| 2466 | THE FUNDING SIDE |
| 2702 | · · SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY. |
| 2755 | WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. |
| 2887 | THE THREE STATEMENTS |
| 2999 | · tax |
| 3116 | · LAST MONTH, KEPT ON PURPOSE |
| 3245 | THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 3324 | · the allowance |
| 3626 | · what it holds |
| 3737 | · the leverage ratio (0.7.11, round 2) |
| 3817 | · what it does with profit |
| 3918 | · its own shares |
| 4080 | · what it lends |
| 4164 | · the save |
| 4321 | · reading |
| 4471 | · a branch that does not pay is closed (0.7.11, round 2) |
| 4852 | WHAT THE BANK TAB READS (0.7.9) |
| 4884 | · the interest, by who paid it |
| 4921 | · what moved it between two presses |
| 4958 | · its year of statements |
| 5146 | · its balance sheet (0.7.13) |
| 5324 | · its rates, in a ladder |
| 5375 | · in words |
| 5437 | · what its book weighs |
| 5480 | · its funding |
| 5504 | · another branch |
| 5525 | · how its equity moved |
| 5560 | · its equity, in two parts |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3826 | `Bank.Payout.NO_BANK` |  |
| 3826 | `Bank.Payout.FAILED` |  |
| 3826 | `Bank.Payout.UNDER_MINIMUM` |  |
| 3826 | `Bank.Payout.REBUILDING` |  |
| 3826 | `Bank.Payout.PAYING` |  |
| 3826 | `Bank.Payout.RETURNING` |  |
| 4971 | `Bank.Line.INTEREST` |  |
| 4971 | `Bank.Line.FROM_BUSINESSES` |  |
| 4971 | `Bank.Line.FROM_HOUSEHOLDS` |  |
| 4971 | `Bank.Line.FROM_CITY` |  |
| 4971 | `Bank.Line.FROM_CARRY` |  |
| 4971 | `Bank.Line.FROM_RESERVES` |  |
| 4971 | `Bank.Line.DISCOUNT` |  |
| 4972 | `Bank.Line.SAVERS` |  |
| 4972 | `Bank.Line.WINDOW` |  |
| 4972 | `Bank.Line.NET_INTEREST` |  |
| 4973 | `Bank.Line.FEES` |  |
| 4973 | `Bank.Line.ACCOUNT_FEES` |  |
| 4973 | `Bank.Line.LOAN_FEES_PAID` |  |
| 4973 | `Bank.Line.LOAN_FEES_OWED` |  |
| 4974 | `Bank.Line.PROVISIONS` |  |
| 4974 | `Bank.Line.WRITE_OFFS` |  |
| 4974 | `Bank.Line.TRADING` |  |
| 4974 | `Bank.Line.PAPER_GAINS` |  |
| 4974 | `Bank.Line.REVENUE` |  |
| 4975 | `Bank.Line.COSTS` |  |
| 4975 | `Bank.Line.PAYROLL` |  |
| 4975 | `Bank.Line.UPKEEP` |  |
| 4975 | `Bank.Line.PRE_TAX` |  |
| 4975 | `Bank.Line.TAX` |  |
| 4975 | `Bank.Line.NET` |  |
| 4976 | `Bank.Line.DIVIDENDS` |  |
| 4976 | `Bank.Line.BUYBACKS` |  |
| 4976 | `Bank.Line.ISSUED` |  |
| 4976 | `Bank.Line.RETAINED` |  |
| 4977 | `Bank.Line.BOOK` |  |
| 4977 | `Bank.Line.EQUITY` |  |
| 4979 | `Bank.Line.FROM_BONDS` | 0.7.12's: the coupons on its bonds, its underwriting fees and its gains on bonds. |
| 4979 | `Bank.Line.UNDERWRITING` |  |
| 4979 | `Bank.Line.BOND_GAINS` |  |
| 5162 | `Bank.Sheet.RESERVES` |  |
| 5162 | `Bank.Sheet.BUSINESS_LOANS` |  |
| 5162 | `Bank.Sheet.INTERIM` |  |
| 5162 | `Bank.Sheet.MORTGAGES` |  |
| 5162 | `Bank.Sheet.FAMILIES` |  |
| 5162 | `Bank.Sheet.CARRY` |  |
| 5162 | `Bank.Sheet.CITY_PAPER` |  |
| 5162 | `Bank.Sheet.BONDS` |  |
| 5162 | `Bank.Sheet.ALLOWANCE` |  |
| 5162 | `Bank.Sheet.DESK` |  |
| 5163 | `Bank.Sheet.ASSETS` |  |
| 5164 | `Bank.Sheet.DEPOSIT_FUNDING` |  |
| 5164 | `Bank.Sheet.WINDOW` |  |
| 5164 | `Bank.Sheet.FOREIGN_DEPOSITS` |  |
| 5164 | `Bank.Sheet.DESK_SHORT` |  |
| 5164 | `Bank.Sheet.UNEARNED_DISCOUNT` |  |
| 5165 | `Bank.Sheet.LIABILITIES` |  |
| 5166 | `Bank.Sheet.EQUITY` |  |
| 5167 | `Bank.Sheet.HOUSEHOLD_DEPOSITS` |  |
| 5167 | `Bank.Sheet.SECTOR_DEPOSITS` |  |
| 5168 | `Bank.Sheet.PAID_IN` |  |
| 5168 | `Bank.Sheet.RETAINED` |  |
| 5440 | `Bank.Book.BUSINESSES` |  |
| 5440 | `Bank.Book.CITY` |  |
| 5440 | `Bank.Book.FAMILIES` |  |
| 5440 | `Bank.Book.CARRY` |  |
| 5440 | `Bank.Book.DESK` |  |
| 5440 | `Bank.Book.MORTGAGES` |  |
| 5440 | `Bank.Book.BONDS` |  |
| 5440 | `Bank.Book.CONCENTRATION` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `Bank.LEVERAGE` | `6` | How many times its deposits the bank will lend. |
| 104 | `Bank.CAPITAL_RATIO` | `.08` | Equity a bank must hold against its risk-weighted book. |
| 134 | `Bank.LEVERAGE_RATIO_MIN` | `.03` | Equity a bank must hold against everything it has lent, whatever that weighs: 3%, Basel III's leverage ratio (0.7.11, round 2). |
| 147 | `Bank.DEPOSITS_PER_BRANCH` | `250_000` | How much of a city's savings one branch can gather. |
| 170 | `Bank.PAID_IN_PER_BRANCH` | `32_000` | What the shareholders put up when a branch opens. |
| 198 | `Bank.RISK_CITY` | `.20` | The city cannot default on its own paper. |
| 201 | `Bank.RISK_BUSINESS` | `1.00` | A business can be restructured, and in this game regularly is. |
| 204 | `Bank.RISK_HOUSEHOLD` | `1.00` | ...and a family can be discharged. |
| 220 | `Bank.RISK_CARRY` | `1.00` | A foreign carry borrower, against the risk weights above. |
| 237 | `Bank.RISK_INSURED_MORTGAGE` | `0.0` | An insured mortgage (0.7.11): 0%. |
| 240 | `Bank.SHORTEST_WEIGHT` | `.40` | What a loan repaying tomorrow weighs against one repaying never. |
| 243 | `Bank.LONG_TERM_MONTHS` | `60` | Months of remaining term at which a loan weighs its full amount. |
| 258 | `Bank.EASY_STRAIN` | `.80` | How much of its capacity the bank lends before it counts itself full: the carry trade is lent only the room below it, and a branch is worth what it adds below it. |
| 275 | `Bank.BUILD_AT_STRAIN` | `.70` | Where the private sector starts building, which is BEFORE the bank is full. |
| 707 | `Bank.BASE_LOSS_RATE` | `.004` | What a sound loan is expected to lose a year through the cycle, as a share of the book: 0.4%, about what Canada's big banks provision for credit losses in a normal year (RBC, 2025). |
| 710 | `Bank.COST_WINDOW_MONTHS` | `12` | Months of payroll and upkeep the running costs are measured over: a year, so one month's building bill is not a price. |
| 713 | `Bank.PRIME_TERM_MONTHS` | `BusinessDebtManager.LOAN_TERM_MONTHS` | The term prime is struck at: a business loan's, BusinessDebtManager.LOAN_TERM_MONTHS, which takes the short end's term premium. |
| 726 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 951 | `Bank.IRB_CONFIDENCE` | `.999` | The confidence the IRB capital is struck at: 99.9% of years, BCBS (2006) paragraph 272. |
| 954 | `Bank.IRB_CORRELATION_HIGH` | `.24` | The IRB corporate correlation for the soundest firms: 24%, BCBS (2006) paragraph 272. |
| 957 | `Bank.IRB_CORRELATION_LOW` | `.12` | ...and for the riskiest: 12%, the same paragraph. |
| 960 | `Bank.IRB_CORRELATION_DECAY` | `50` | How fast the correlation falls from the one to the other as the default rate rises: the 50 in exp(-50 x PD), the same paragraph. |
| 963 | `Bank.IRB_MATURITY_YEARS` | `2.5` | The effective maturity of a corporate exposure under the foundation IRB approach: 2.5 years, BCBS (2006) paragraph 318 - where the maturity adjustment reads 1 / (1 - 1.5 b). |
| 966 | `Bank.IRB_MATURITY_A` | `.11852` | The maturity adjustment's slope, b(PD) = (0.11852 - 0.05478 ln PD)^2: its constant, BCBS (2006) paragraph 272. |
| 969 | `Bank.IRB_MATURITY_B` | `.05478` | ...and its log coefficient, the same paragraph. |
| 984 | `Bank.SECTOR_CORRELATION_MULTIPLIER` | `1.25` | HOW MUCH MORE CORRELATED THE FIRMS OF ONE INDUSTRY ARE than the IRB's corporate correlation, which is struck for a book spread across every industry: 1.25, the asset value correlation multiplier Basel III applies to e... |
| 1012 | `Bank.IRB_QUANTILE` | `inverseNormal(IRB_CONFIDENCE)` | The standard normal quantile at IRB_CONFIDENCE, G(0.999) in the IRB formula: struck once. |
| 1453 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 1782 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 1943 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 2425 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 2428 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 2812 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - set so that this bank... |
| 2815 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of funding bid for it ... |
| 2818 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 3327 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not lend it another dol... |
| 3330 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 3333 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 3629 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 3632 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 3635 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 3726 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point ceiling and the 3.5-p... |
| 3820 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 3823 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 4002 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into an issue on the las... |
| 4083 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit at the target (lend... |
| 4479 | `Bank.BRANCH_CLOSE_MONTHS` | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | Closed months in a row the book must fail to keep its branches' staff before the bank closes one: BusinessInvestment.DISTRESS_LOSS_MONTHS, the two years a landlord or a maker loses money before it sells plant it is us... |
| 5172 | `Bank.SHEET_ASSETS` | `{ Sheet.RESERVES, Sheet.BUSINESS_LOANS, Sheet.INTERIM, Sheet.MORTGAGES, Sheet...` | The asset lines, in the page's order: they sum to totalAssets(). |
| 5176 | `Bank.SHEET_LIABILITIES` | `{ Sheet.DEPOSIT_FUNDING, Sheet.WINDOW, Sheet.FOREIGN_DEPOSITS, Sheet.DESK_SHO...` | ...and the liability lines: they sum to totalLiabilities(). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private double depositsPerBranch` | The same, in today's money - reformed with every other figure. |
| 173 | `private double paidInPerBranch` | The same, in today's money. |
| 279 | `private double cash` |  |
| 280 | `private double branches` |  |
| 281 | `private double deposits` |  |
| 284 | `private double householdDeposits` | The two halves of that, because they are paid separately. |
| 285 | `private double sectorDeposits` |  |
| 288 | `private double sectorBook` | What is lent out, by whom it is owed. |
| 289 | `private double cityBook` |  |
| 290 | `private double householdBook` |  |
| 298 | `private double mortgageBook` | The insured mortgages inside sectorBook (0.7.11): part of what the businesses owe, weighed at RISK_INSURED_MORTGAGE rather than RISK_BUSINESS, so the weight table shows them as their own row. |
| 299 | `private double mortgageWeighted` |  |
| 307 | `private final double[] loansBySector` | ...and what the businesses owe it sector by sector, in Sectors.KEYS order, with the interim financing (InterimLoan) apart (0.7.13): the Balance sheet page's detail, set by Game.refreshBank() from the same loans sector... |
| 308 | `private final double[] interimBySector` |  |
| 309 | `private double interimBook` |  |
| 312 | `private double insuranceClaims` | What the treasury paid it this month on insured mortgages the month's defaults wrote down (0.7.11): cash for a claim on its book, so no income and no loss. |
| 315 | `private double sectorWeighted` | The same three, weighted for risk and remaining term. |
| 316 | `private double cityWeighted` |  |
| 317 | `private double householdWeighted` |  |
| 321 | `private double interestEarned` |  |
| 322 | `private double writeOffs` |  |
| 323 | `private double payroll` |  |
| 324 | `private double upkeep` |  |
| 325 | `private double lentToHouseholds` |  |
| 326 | `private double repaidByHouseholds` |  |
| 327 | `private double fundingCost` |  |
| 370 | `private double placementIncome` | WHAT ITS SPARE CASH EARNS: THE POLICY RATE, AT THE CENTRAL BANK (0.7.0). |
| 371 | `private double openingEquity` |  |
| 385 | `private double carryBook` | The fourth book: what foreigners have borrowed to take abroad. |
| 386 | `private double carryLent, carryRepaid, carryInterest` |  |
| 512 | `private double bondBook` | What its bonds cost it, less what defaults took: their carrying value, on its book. |
| 514 | `private double bondWeighted` | ...weighed as loans to their issuers. |
| 516 | `private double bondFace` | ...and their face. |
| 518 | `private double bondGains, underwritingFees, interestFromBonds` | The month's gain on bonds sold or repaid over what they cost it, its underwriting fees, and its coupons. |
| 1047 | `private final java.util.Map<String, Exposure> exposures` |  |
| 1048 | `private final java.util.Map<String, Double> concentrationMarginal` |  |
| 1049 | `private double concentrationHerfindahl, concentrationAddOn, concentrationWeighted, concentrationExposure` |  |
| 1184 | `private double lastWindowShare` | The funding blend and the running-cost rate, struck at the last close. |
| 1185 | `private double lastRunningCost` |  |
| 1191 | `private final double[] costRing` | The record the running costs are struck from: a year of payroll and upkeep beside the book they served, in rings indexed by closed months. |
| 1192 | `private final double[] costBookRing` |  |
| 1193 | `private int pricedMonths` |  |
| 1282 | `private double lastCostOfFunds` | What money cost this bank over the month that just closed. |
| 1456 | `private double securities` | The desk's inventory, at the exchange's mark. |
| 1464 | `private double tradingIncome` | The trading result so far this month: cash from what it sold less cash for what it bought, plus the change in what it holds at the mark, plus the dividends its inventory was paid. |
| 1476 | `private double markChange` | The part of the trading result that is the re-mark: every change in what the inventory is carried at this month, summed. |
| 1640 | `private double foreignDeposits` |  |
| 1678 | `private double hotMoneyIn, hotMoneyOut` |  |
| 1747 | `private boolean inResolution` | Failed, and not yet put back on its feet. |
| 1748 | `private int failures` |  |
| 1760 | `private double resolutionLossThisMonth` | ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. |
| 1761 | `private double resolutionLossLifetime` |  |
| 1955 | `private double domesticCapitalScale` | The same, in today's money. |
| 2033 | `private double dividendsPaid` |  |
| 2076 | `private double capitalInjected` |  |
| 2077 | `private double capitalFromHome` |  |
| 2078 | `private double bailoutReceived` |  |
| 2079 | `private double foundingSettlement` |  |
| 2151 | `private double branchesCapitalised` |  |
| 2182 | `private double internalInterest` |  |
| 2259 | `private double paperGains` | The gain or loss on the city's paper that changed hands this month. |
| 2262 | `private double paperBoughtFromHouseholds` | What the desk paid households for their paper this month: money leaving the pools for a household, which MoneyAudit declares. |
| 2265 | `private double paperSoldToCentralBank, paperBoughtFromCentralBank` | What the central bank paid it for paper this month, and what it paid the central bank. |
| 2279 | `private double unearnedDiscount` | THE UNEARNED DISCOUNT (0.7.1): the part of what the bank paid under face for the city's paper it has not yet accreted into income, carried as a liability against the book - so equity does not jump by the discount the ... |
| 2331 | `private double buybackGains` | Gains less losses on paper the treasury bought back, over the city's life. |
| 2431 | `private double accountFeeBase` | ACCOUNT_FEE in today's unit - reseeded and reformed with the other money constants. |
| 2440 | `private double accountFees, loanFeesPaid, loanFeesOwed` | The month's fees: accounts, loans paid in cash (a business's), and loans added to what is owed (a household's). |
| 2542 | `private double depositRate` | PAYING FOR DEPOSITS is the other half of what a bank IS. |
| 2543 | `private double depositInterestToHouseholds` |  |
| 2544 | `private double depositInterestToSectors` |  |
| 2554 | `private double depositInterestToForeign` | ...and what the hot money is paid for parking here. |
| 2562 | `private boolean depositPayoutHeld` |  |
| 2589 | `private double fundingRate` | THE PRICE OF WHOLESALE MONEY WAS TWO DIALS until 0.7.0: FUNDING_SPREAD, two points over the risk-free rate "for being a bank rather than a treasury", and FUNDING_STRETCH, six points more at a reach of one deposit book... |
| 2841 | `private double chosenDepositRate` | The rate the bank chose this month, before rule 2 asked whether its margin could pay it. |
| 2879 | `private int monthsPayoutHeld` | HOW OFTEN RULE 2 HELD THE SAVERS UNDER THE CHOSEN RATE - counted for the run, not saved, for the reason the bid-up's counter was: a rule that never binds looks exactly like one that does not exist. |
| 3026 | `private double taxPaid` | Jerus: "quick question banks are taxed right?" |
| 3027 | `private double profitLastMonth` |  |
| 3099 | `private double struckProfit, carriedLate, restoredLate` | PROFIT THAT LANDS AFTER THE CLOSE (0.7.7). |
| 3100 | `private boolean closedThisMonth` |  |
| 3135 | `private double lastPayroll, lastUpkeep, lastInterest, lastBook` |  |
| 3138 | `private double lastKept` | Last month's interest margin and fees - what its book KEPT, which the branch test asks of a counter since 0.7.7. |
| 3465 | `private final java.util.Map<String, Double> sectorAllowance` | Each sector's allowance by name, what each held when the month opened, and what each was written off by this month. |
| 3466 | `private final java.util.Map<String, Double> openingSectorAllowance` |  |
| 3467 | `private final java.util.Map<String, Double> writtenOffBySector` |  |
| 3469 | `private final java.util.Set<String> sectorsWatched` | The sectors whose books are in stage 2, as the last provide() found them. |
| 3471 | `private final java.util.Map<String, Double> stageTwoShares` | ...and the share of each sector's book in stage 2, firm by firm (0.7.8). |
| 3474 | `private double householdAllowance, openingHouseholdAllowance, householdWrittenOff, householdWatchedDebt` | The families' allowance, the same three, and how much of their debt is in cells in trouble. |
| 3477 | `private double openingAllowance, allowanceUsed` | The whole allowance as the month opened, and how much of the month's write-offs it covered. |
| 3589 | `private final java.util.Map<String, double[]> allowanceReadings` |  |
| 3638 | `private final double[] lossRing` | The last year's provisions and weighted book, a ring of the months the bank had a branch. |
| 3639 | `private final double[] riskRing` |  |
| 3640 | `private int lossMonths` |  |
| 3642 | `private double worstLossRate` | The worst year's provisions over its average weighted book it has recorded: the loss the buffer is sized to take. |
| 3913 | `private double payoutProfit, payoutExcess, payoutOverTarget` | What the month's payout read: the profit after tax it was paid on, what the bank held over the top of its band, and over its target. |
| 3949 | `private double sharesBoughtBack, sharesIssued` | THE DESK DEALS IN THE BANK'S OWN SHARES BY ITS CAPITAL RULE (0.7.8): it buys them back from whoever sells while the bank is at or over its own target (buysBackOwnShares()) and only with what it holds over it (buybackR... |
| 4066 | `private final double[] dividendRing` | The owners' year: dividends and buybacks, a ring of months with this one in it. |
| 4067 | `private final double[] buybackRing` |  |
| 4068 | `private int payoutMonths` |  |
| 4482 | `private int uncoveredMonths` | Closed months in a row the book has not kept its branches' staff (branchesCoverTheirStaff()); struck at closeMonth(), carried in lastMonthToSave(). |
| 4895 | `private double interestFromBusinesses, interestFromCity, interestFromHouseholds, discountAccreted` | The month's interest by who paid it: the businesses, the city's coupons, the families' credit lines, and the discount on the city's paper as it is earned. |
| 4929 | `private double treasuryBuybackGain` | What the treasury buying its paper back gained the bank (negative: lost it) since the month opened. |
| 4938 | `private double allowanceOpened` | The allowance a save from before 0.7.8 was given on load (openAllowance()): its equity fell by it between two presses. |
| 4946 | `private double bailoutsLifetime` | What the city has put into it in rescues over its life (receiveBailout()), carried in the solvency record since 0.7.9 - a save from before counts from its load. |
| 4955 | `private boolean monthKnown` | True once the month's lines are a month's: one played, or lines a save carried. |
| 5042 | `private final double[][] statementRing` | The months before this one, a year of them less this one: each filed whole at the top of the month after (startMonth()), when everything booked after its close is in it. |
| 5043 | `private int statementsFiled` |  |
| 5254 | `private final double[][] sheetRing` | The sheet at the top of each of the last YEAR_MONTHS months - every line, then the loans and the interim financing by sector - filed at startMonth() before anything moves, so the oldest is the sheet exactly a year bef... |
| 5255 | `private int sheetsFiled` |  |
| 5597 | `private double paidInOpening, retainedOpening` | ITS EQUITY, IN TWO PARTS (0.7.13, round 2). |
| 5598 | `private boolean splitKnown` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 5579 | **type** `public class Bank` | The city's commercial bank: every loan in it, and every default. |

### the dials (lines 67-174)

### what a dollar of book WEIGHS (lines 175-276)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 4 | `public static double maturityWeight(double remainingMonths)` | How much of its face a loan with this long left to run counts for. |

### the position (lines 277-318)

### the month's working (lines 319-372)

### the month (lines 373-491)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 28 | `public void refresh(double branches, double householdSavings, double sectorCash, double sectorBook, double cityBook, double hou...` | Re-reads the city and re-prices credit. |
| 432 | 5 | `public void setWeightedBook(double sector, double city, double household)` | The same book, weighed by what each loan actually is. |
| 443 | 4 | `public void setMortgageBook(double insured, double weighted)` | ...and the insured mortgages inside the businesses' book (0.7.11), which the weight table shows at RISK_INSURED_MORTGAGE. |
| 449 | 1 | `public double getMortgageBook()` | The insured mortgages it holds, part of getSectorBook(). |
| 458 | 8 | `public void setSectorLoans(double[] loans, double[] interim)` | ...and the rest of the businesses' book by sector, with the interim financing apart (0.7.13): for each sector in Sectors.KEYS order, its loans that are neither an insured mortgage nor interim financing, and its interi... |
| 468 | 1 | `public double getLoansToSector(int sector)` | What the sector at this index of Sectors.KEYS owes it outside its insured mortgages and its interim financing. |
| 470 | 1 | `public double getInterimToSector(int sector)` | ...and its interim financing. |
| 472 | 1 | `public double getInterimBook()` | The interim financing it holds, every sector's: part of getSectorBook(). |
| 474 | 1 | `public double getBusinessLoans()` | What the businesses owe it outside the insured mortgages and the interim financing: the sector book less both. |
| 483 | 5 | `public void receiveInsuranceClaim(double amount)` | THE CITY PAYS AN INSURED LOSS (0.7.11): cash from the treasury for the part of a borrower's write-down that came off an insured mortgage. |
| 490 | 1 | `public double getInsuranceClaims()` | What the treasury paid it on insured mortgages this month. |

### THE CORPORATE BONDS IT HOLDS (0.7.12) (lines 492-582)

| line | len | member | says |
|---:|---:|---|---|
| 525 | 5 | `public void setBondBook(double carrying, double weighted, double face)` | The bonds on its book as the market holds them - what they cost it, weighed, and their face - set by Game.refreshBank() beside the other books. |
| 532 | 6 | `public void buyBond(double cost, double weighted)` | It buys a bond: cash out, the bond on its book at what it paid, and what that weighs (RISK_BUSINESS for the months left, as Game.refreshBank() weighs it). |
| 540 | 7 | `public void sellBond(double proceeds, double cost)` | It sells one: cash in, what it cost off its book, the difference a gain or a loss this month. |
| 549 | 1 | `public void redeemBond(double face, double cost)` | ...or its issuer repays it at maturity: the face in, what it cost off its book. |
| 552 | 5 | `public void bondsWrittenDown(double cost)` | A default took this much of what its bonds cost it off its book: the write-off is booked with the issuer's loans' (writeOffSector()). |
| 559 | 7 | `public void takeBondCoupons(double amount)` | The coupons on its bonds this month: interest from a business, inside the pools. |
| 568 | 5 | `public void takeUnderwriting(double fee)` | Its fee for bringing an issue - the issue's costs, out of the proceeds: a transfer between two pools. |
| 575 | 1 | `public double getBondBook()` | What its bonds cost it, on its book; their face; what they weigh. |
| 576 | 1 | `public double getBondFace()` |  |
| 577 | 1 | `public double getBondWeighted()` |  |
| 579 | 1 | `public double getBondGains()` | The month's gain on bonds sold or repaid, its underwriting fees, and its coupons. |
| 580 | 1 | `public double getUnderwritingFees()` |  |
| 581 | 1 | `public double getInterestFromBonds()` |  |

### carry (lines 583-642)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 6 | `public void lendCarry(double amount)` | Lend to a foreigner who is about to take it abroad. |
| 603 | 7 | `public void repayCarry(double amount)` | ...and they bring it home. |
| 620 | 6 | `public void takeCarryInterest(double amount)` | The coupon, which arrives from abroad. |
| 636 | 1 | `public void setCarryBook(double amount)` | The carry book, set from the stock that owns it. |
| 638 | 1 | `public double getCarryBook()` |  |
| 639 | 1 | `public double getCarryLent()` |  |
| 640 | 1 | `public double getCarryRepaid()` |  |
| 641 | 1 | `public double getCarryInterest()` |  |

### WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7) (lines 643-727)

### the four parts (lines 728-879)

| line | len | member | says |
|---:|---:|---|---|
| 742 | 3 | `public double windowShare()` | The share of the bank's marginal money that comes from the window, 0 to 1: struck at the close from how the month ended funded - what it owed the window over everything it had borrowed, 0 while it holds reserves - and... |
| 753 | 4 | `public double fundsTransferPrice(double policyAnnual, int months)` | THE FUNDS-TRANSFER PRICE: what a dollar lent for this many months costs the bank - the policy rate, plus the window's penalty on the share of its money that is the window's, plus the city's term premium for the term (... |
| 775 | 1 | `public double runningCostRate()` | THE RUNNING COSTS: the last year's payroll and upkeep over the book they served, a year - or over what the bank's capital could carry at its own target (capitalTarget(); a fixed 11% until 0.7.8), whichever is larger. |
| 808 | 1 | `public double expectedLossRate()` | THE EXPECTED LOSS: BASE_LOSS_RATE, what a sound book loses a year through the cycle. |
| 811 | 3 | `public static double requiredReturn()` | The return the bank's owners are priced to want: Equity.requiredYield() at the world's rate, 12.5% at the defaults. |
| 825 | 4 | `public double capitalCharge(double policyAnnual, int months, double riskWeight)` | THE CAPITAL CHARGE: the equity a loan of this risk ties up at the bank's own capital target (capitalPerDollar(); the target is capitalTarget() since 0.7.8, and before that the minimum plus a fixed three points), times... |
| 845 | 3 | `public double capitalPerDollar(double riskWeight)` | THE EQUITY A DOLLAR LENT TIES UP at the bank's own target: its risk weight times capitalTarget(), and never less than the leverage requirement on the same dollar, leverageTarget() (0.7.11, round 2). |
| 857 | 4 | `public double capitalPerDollar(double riskWeight, String sector)` | ...A DOLLAR LENT TO THIS SECTOR (0.7.12): the same formula with the capital its concentration adds - concentrationPerDollar(), the marginal add-on at the minimum, carried at the bank's own target in the proportion its... |
| 863 | 4 | `public double capitalCharge(double policyAnnual, int months, double riskWeight, String sector)` | THE CAPITAL CHARGE ON A LOAN TO THIS SECTOR (0.7.12): capitalPerDollar() with its concentration, at the owners' return over the money. |
| 875 | 4 | `public double concentrationCharge(double policyAnnual, int months, String sector)` | WHAT THE BOOK'S CONCENTRATION ADDS TO A LOAN TO THIS SECTOR, a year: its capital charge with the concentration less its capital charge without - the one formula, read twice. |

### THE BANK PRICES CONCENTRATION (0.7.12) (lines 880-1180)

| line | len | member | says |
|---:|---:|---|---|
| 987 | 5 | `public static double irbCorrelation(double pd)` | The IRB corporate asset correlation at this default rate, R(PD): 24% for the soundest, 12% for the riskiest, blended by exp(-50 PD). |
| 994 | 4 | `public static double effectiveCorrelation(double pd, double herfindahl)` | ...and the book's, at its sector Herfindahl index: R(PD) x (1 + (SECTOR_CORRELATION_MULTIPLIER - 1) x H). |
| 1000 | 10 | `public static double inverseNormal(double p)` | The inverse of the standard normal distribution, by bisection on BusinessDebtManager.normalCdf() - the game's one N - to the last bit. |
| 1015 | 5 | `public static double maturityAdjustment(double pd)` | The maturity adjustment at IRB_MATURITY_YEARS: (1 + (M - 2.5) b) / (1 - 1.5 b), b = (IRB_MATURITY_A - IRB_MATURITY_B ln PD)^2. |
| 1022 | 8 | `public static double irbCapital(double pd, double lgd, double rho)` | THE IRB CAPITAL a dollar needs at the minimum, K(PD, LGD, rho): the loss at the 99.9th percentile year less the expected loss, times the maturity adjustment. |
| 1032 | 11 | `public static double irbCapitalSlope(double pd, double lgd, double rho)` | ...its slope in the correlation, dK/drho: what the Euler rule's second term reads. |
| 1045 | 1 | **type** `public record Exposure(double amount, double pd, double lgd)` | One sector's exposure as the concentration reads it: what the bank stands to lose on it, its default rate, and the loss given default of what it holds. |
| 1057 | 44 | `public void setConcentration(java.util.Map<String, Exposure> bySector)` | THE BOOK'S CONCENTRATION, struck from every sector's exposure: its Herfindahl index, the add-on at the minimum and the weight it adds, and each sector's marginal add-on by the Euler rule. |
| 1103 | 3 | `public double concentrationPerDollar(String sector)` | A dollar lent to this sector: the capital its concentration adds at the minimum, the Euler rule's marginal add-on. |
| 1108 | 1 | `public double getConcentrationHerfindahl()` | The book's sector Herfindahl index, the add-on at the minimum, the weight it adds and the exposure it was struck on. |
| 1109 | 1 | `public double getConcentrationAddOn()` |  |
| 1110 | 1 | `public double getConcentrationWeighted()` |  |
| 1111 | 1 | `public double getConcentrationExposure()` |  |
| 1113 | 1 | `public Exposure getExposure(String sector)` | One sector's exposure as the book was struck, or null. |
| 1116 | 4 | `public double loanRate(double policyAnnual, int months, double riskWeight)` | A loan of this term and risk weight: the four parts, added up. |
| 1122 | 3 | `public double prime(double policyAnnual)` | PRIME: what a sound business pays - the four parts at RISK_BUSINESS, for a business loan's term. |
| 1143 | 5 | `public double insuredMortgageRate(double policyAnnual)` | AN INSURED MORTGAGE (0.7.11): what the money costs for its term - the funds-transfer price at Mortgage.MORTGAGE_TERM_MONTHS, the curve's ten-year point - running the bank, and the capital it ties up; no expected loss,... |
| 1150 | 3 | `public double householdRate(double policyAnnual)` | What a household's credit line starts from: the four parts at RISK_HOUSEHOLD, revolving, so no term premium. |
| 1160 | 3 | `public double lendingRate(double policyAnnual)` | What a good credit pays to borrow here: prime, since 0.7.7. |
| 1176 | 4 | `public double carryRate(double policyAnnual)` | What the carry trade pays to borrow here: the same costs, at RISK_CARRY and short, since the money is taken abroad and wanted back on demand - with NO expected loss, because Jerus's call is that a carry borrower never... |

### what the four parts remember (lines 1181-1406)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 16 | `private void strikePrices()` | Adds the month that has just closed to the rings and strikes the two parts that read flows. |
| 1214 | 10 | `public double[] pricingHistoryToSave()` | The record, for the save: the count, then the two cost rings. |
| 1226 | 9 | `public void restorePricingHistory(double[] in)` | ...and back. |
| 1279 | 1 | `public double marginalCostOfFunds()` | WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND. |
| 1301 | 8 | `private double blendedCostOfFunds()` | The two tranches, blended at the weights the bank actually used them. |
| 1311 | 95 | `public void startMonth()` | Clears the month's flows. |

### the arithmetic (lines 1407-1436)

| line | len | member | says |
|---:|---:|---|---|
| 1419 | 1 | `public double getBook()` | Everything lent, carry included. |
| 1428 | 8 | `public double getWeightedBook()` | The book as CAPACITY sees it: risk- and maturity-weighted. |

### THE TRADING DESK (lines 1437-1514)

| line | len | member | says |
|---:|---:|---|---|
| 1479 | 5 | `public void markSecurities(double value)` | The exchange re-marks the inventory. |
| 1486 | 5 | `public void deskPays(double cash)` | The desk paid cash for shares. |
| 1493 | 5 | `public void deskReceives(double cash)` | ...and was paid for shares it sold. |
| 1500 | 5 | `public void receiveDividend(double amount)` | Dividends on the inventory: income, and cash. |
| 1507 | 1 | `public void restoreSecurities(double value)` | The load path puts the mark back without calling it income. |
| 1509 | 1 | `public double getSecurities()` |  |
| 1510 | 1 | `public double getTradingIncome()` |  |
| 1513 | 1 | `public double getMarkChange()` | What re-marking the inventory did to this month's trading result. |

### the desk held to its capital (0.7.8) (lines 1515-2168)

| line | len | member | says |
|---:|---:|---|---|
| 1575 | 18 | `public double deskCanCarry(double inventory, double price, double mark)` | HOW MANY OF A COMPANY'S SHARES THE DESK MAY STILL BUY ON THE CAPITAL IT HAS: as many as leave the bank at or over its target (targetEquity()) with them on its books - the shares carried at the weight the weighted book... |
| 1595 | 4 | `public double weightingRelief()` | How much lighter the weighting makes the book. |
| 1609 | 12 | `public double capacity()` | The most the bank can lend before it is funding itself at the central bank's window (abroad, until 0.7.0). |
| 1634 | 5 | `public double depositsGathered()` | What the branches let it gather, out of what the city has to bank - PLUS whatever the world has parked here, which needed no branch at all. |
| 1643 | 1 | `public double getForeignDeposits()` | Hot money currently funding this bank, in local money. |
| 1646 | 4 | `public void setForeignDeposits(double amount)` | Told by Game each month, from CapitalFlows. |
| 1658 | 5 | `public void receiveHotMoney(double amount)` | Hot money arriving, as cash. |
| 1672 | 5 | `public void returnHotMoney(double amount)` | ...and leaving, which is the whole danger. |
| 1680 | 1 | `public double getHotMoneyIn()` |  |
| 1681 | 1 | `public double getHotMoneyOut()` |  |
| 1695 | 4 | `public double hotFundingShare()` | The share of the bank's funding that could leave at any time. |
| 1701 | 3 | `public double capitalLimit()` | What its capital supports, on the risk-weighted book - at the book's present mix, under the larger of the two requirements since round 2 of 0.7.11 (weightedBookSupportedBy()). |
| 1716 | 5 | `private double weightedBookSupportedBy(double equity)` | THE WEIGHTED BOOK THIS MUCH EQUITY SUPPORTS: equity over CAPITAL_RATIO while the risk-based minimum is the larger. |
| 1723 | 3 | `public boolean capitalBound()` | True when it is the capital that binds rather than the funding. |
| 1733 | 4 | `public double capitalRatio()` | Equity against the risk-weighted book - the ratio a regulator reads. |
| 1763 | 1 | `public boolean isInsolvent()` |  |
| 1765 | 1 | `public double getResolutionLoss()` | Over the city's life. |
| 1767 | 1 | `public double getResolutionLossThisMonth()` | This month only. |
| 1768 | 1 | `public int getFailures()` |  |
| 1805 | 4 | `public double[] solvencyToSave()` | The solvency record, for the save. |
| 1810 | 8 | `public void restoreSolvency(double[] state)` |  |
| 1843 | 24 | `public void resolveIfFailed()` | The bank fails, and its creditors take the loss. |
| 1877 | 23 | `public double resolutionExitEquity()` | The equity at which the freeze lifts, however the bank gets there. |
| 1923 | 9 | `public double recapitalisationNeeded()` | What it would take to put the bank back on its feet. |
| 1958 | 5 | `public double domesticCapitalShare()` | The share of new paid-in capital the city's own savers can find. |
| 1987 | 13 | `public void injectCapital(double amount)` | Shareholders' money, put in as capital - and WHOSE shareholders. |
| 2011 | 7 | `public void injectCapital(double fromHome, double fromAbroad)` | Shareholders' money, put in as capital, and whose: the households' part came out of their savings through the register, the rest from the world. |
| 2026 | 6 | `public void payDividend(double amount)` | Pays the owners. |
| 2036 | 1 | `public double getDividendsPaid()` | What it paid its shareholders this month. |
| 2067 | 8 | `public void receiveBailout(double amount)` | The same money, from the TREASURY rather than from shareholders. |
| 2085 | 1 | `public double getFoundingSettlement()` | The one-off jump in the bank's cash when the city's first branch opens and it takes over the standing loan book. |
| 2088 | 1 | `public double getCapitalInjected()` | Shareholders' money from OUTSIDE the city. |
| 2097 | 1 | `public double getCapitalFromHome()` | ...and from the city's own savers. |
| 2100 | 1 | `public double getBailoutReceived()` | The treasury's money, from inside it. |
| 2114 | 36 | `public double openBranches(double nowStanding)` | Opens whatever branches have been built since last month, and says what capital they need. |
| 2152 | 1 | `public double getBranchesCapitalised()` |  |
| 2153 | 1 | `public void setBranchesCapitalised(double n)` |  |
| 2163 | 5 | `public double strain()` | Book over capacity. |

### the flows (lines 2169-2243)

| line | len | member | says |
|---:|---:|---|---|
| 2175 | 6 | `public void takeInterest(double amount)` | Interest arriving from a SECTOR or the CITY - both inside the audit, so this is a transfer between pools and declares nothing. |
| 2185 | 1 | `public double getInternalInterest()` | The part of the month's interest that came from inside the city's pools. |
| 2194 | 6 | `public void takeDiscount(double amount)` | Paper bought below par: income the bank earns without any cash moving. |
| 2219 | 3 | `public double sellPaperBack(double price, double principal)` | THE TREASURY BUYS ITS PAPER BACK FROM THE BANK (0.7.0), which is who holds it: the price arrives as cash, the book drops by the principal at once rather than at the next refresh, and the difference is the bank's gain ... |
| 2230 | 13 | `public double sellPaperBack(double price, double principal, double unearned)` | ...and since 0.7.1 at amortised cost: the unearned discount riding on the face that leaves goes with it, so the gain is the price less what the book carried the paper at - face less the discount not yet earned. |

### THE CITY'S PAPER CHANGES HANDS (0.7.1) (lines 2244-2397)

| line | len | member | says |
|---:|---:|---|---|
| 2267 | 1 | `public double getPaperSoldToCentralBank()` |  |
| 2268 | 1 | `public double getPaperBoughtFromCentralBank()` |  |
| 2281 | 1 | `public void setUnearnedDiscount(double amount)` |  |
| 2282 | 1 | `public double getUnearnedDiscount()` |  |
| 2283 | 1 | `public double getPaperGains()` |  |
| 2284 | 1 | `public double getPaperBoughtFromHouseholds()` |  |
| 2286 | 6 | `private void addToCityBook(double face)` |  |
| 2294 | 10 | `public double buyPaperFromHouseholds(double price, double face, double unearned)` | A household sells this face to the desk for this price. |
| 2306 | 11 | `public double sellPaperToCentralBank(double price, double face, double unearned)` | The central bank buys this face from the bank's book, in money it made. |
| 2319 | 10 | `public double buyPaperFromCentralBank(double price, double face, double unearned)` | ...and sells it back: the bank pays, and the face returns to its book. |
| 2332 | 1 | `public double getBuybackGains()` |  |
| 2335 | 4 | `public void takeRepayment(double amount)` | Principal coming back. |
| 2341 | 4 | `public void lend(double amount)` | Money out the door. |
| 2347 | 5 | `public void lendToHouseholds(double amount)` | Lending to a family, which crosses the audit's boundary and is counted. |
| 2354 | 4 | `public void takeFromHouseholds(double principal, double interest)` | ...and from a household, which is outside, so both halves are declared. |
| 2367 | 4 | `public void writeOff(double amount)` | A loan that will not be repaid. |
| 2378 | 5 | `public void writeOffSector(String sector, double amount)` | ...on a business's book, by name (0.7.8), so the month's provide() can draw it against the allowance that sector's book opened the month holding. |
| 2385 | 5 | `public void writeOffHouseholds(double amount)` | ...and on the families' book, the debts of those discharged this month. |
| 2392 | 5 | `public void payRunning(double payroll, double upkeep)` | Wages and running costs, which are real money leaving. |

### FEES (0.7.7) (lines 2398-2465)

| line | len | member | says |
|---:|---:|---|---|
| 2434 | 4 | `public double accountFee(double priceIndex)` | A month's account fee per housed household at this price index, in today's money - nothing in a city with no branch, which has no bank to hold an account at. |
| 2443 | 5 | `public void takeAccountFees(double amount)` | The households' account fees, in cash from outside the pools. |
| 2450 | 5 | `public void takeLoanFees(double amount)` | Loan fees a business paid out of its proceeds: cash from another pool. |
| 2457 | 4 | `public void bookLoanFees(double amount)` | Loan fees added to what the households owe: income now, no cash until they repay, and the book carries them from the next refresh. |
| 2462 | 1 | `public double getAccountFees()` |  |
| 2463 | 1 | `public double getLoanFeesPaid()` |  |
| 2464 | 1 | `public double getLoanFeesOwed()` |  |

### THE FUNDING SIDE (lines 2466-2754)

| line | len | member | says |
|---:|---:|---|---|
| 2557 | 1 | `public double depositRate()` | The rate savers are being paid, a year: the month's payout over the deposits - the rate the bank chose, unless its interest margin could not pay it (isDepositPayoutHeld()). |
| 2560 | 1 | `public boolean isDepositPayoutHeld()` | True when rule 2 of WHAT TO PAY SAVERS held the savers under the rate the bank chose - its margin, after its running costs or at the savers' share, could not pay it. |
| 2564 | 1 | `public double getDepositInterestToHouseholds()` |  |
| 2565 | 1 | `public double getDepositInterestToSectors()` |  |
| 2566 | 1 | `public double getDepositInterestToForeign()` |  |
| 2567 | 4 | `public double depositInterest()` |  |
| 2573 | 4 | `public double netInterestMargin()` | What it charges borrowers, less what it pays savers. |
| 2592 | 1 | `public double borrowings()` | Everything it owes: the mirror of a negative cash position. |
| 2602 | 1 | `public double depositFunding()` | The cheap tranche - the city's own money, lent back out. |
| 2605 | 1 | `public double wholesaleFunding()` | ...and the part it had to go to the window for. |
| 2612 | 1 | `public double fundingRate()` | What the bank is paying for the money it did not have: the window's rate, policy plus CentralBank.WINDOW_PENALTY. |
| 2625 | 129 | `public void fundToCover(double policyAnnual)` | Settles the funding for the month: charge for what was borrowed, then borrow what is short or repay what is spare. |

### WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. (lines 2755-2886)

| line | len | member | says |
|---:|---:|---|---|
| 2826 | 7 | `public double fundingPosition()` | How the bank is funded, 0 to 1: 0 while it holds reserves (its cash is positive), 1 once it is borrowing at the window, and in between the share of what its branches gathered that it has lent out. |
| 2835 | 4 | `public double depositShare()` | The share of the policy rate the bank's funding asks it to pass on: between DEPOSIT_SHARE_FLUSH and DEPOSIT_SHARE_AT_WINDOW, by fundingPosition(). |
| 2842 | 1 | `public double getChosenDepositRate()` |  |
| 2854 | 23 | `private double chooseDepositRate(double policyAnnual)` | The month's deposit interest, in money. |
| 2880 | 1 | `public int getMonthsPayoutHeld()` |  |
| 2883 | 1 | `public double getFundingCost()` | What the window charged this month, paid to the central bank. |
| 2885 | 1 | `public double getPlacementIncome()` | What its reserves earned at the central bank this month, at the policy rate. |

### THE THREE STATEMENTS (lines 2887-2998)

| line | len | member | says |
|---:|---:|---|---|
| 2910 | 1 | `public double cashReserves()` | Cash it is actually sitting on. |
| 2917 | 1 | `public double totalAssets()` | Total assets: the loan book net of what it has set aside against it (netLoans(), since 0.7.8), whatever cash it has not lent, and what the desk holds. |
| 2920 | 1 | `public double netLoans()` | The loans as the balance sheet carries them (0.7.8): what is owed, less the allowance for what will not come back. |
| 2923 | 1 | `public double shortSecurities()` | ...and a desk that is short owes the shares: a liability at the mark. |
| 2950 | 3 | `public double totalLiabilities()` | What the bank owes: what it borrowed to fund its book (past its deposits, at the central bank's window since 0.7.0), and the hot money. |
| 2962 | 1 | `public double equity()` | The residual - and, once the two above are written out, simply the book plus the cash position. |
| 2965 | 1 | `public double getOpeningEquity()` | Equity as it stood at the top of the month. |
| 2970 | 1 | `public double interestIncome()` | What every borrower paid it this month. |
| 2973 | 3 | `public double netInterestIncome()` | ...less what it paid savers and what it paid the window. |
| 2978 | 1 | `public double feeIncome()` | ...plus its fees, since 0.7.7: the accounts, and the loans written. |
| 2988 | 1 | `public double afterLosses()` | ...less the provision for the loans that will not come back (0.7.8): what the allowance rose by, and whatever the month wrote off that it had not already set aside - provisions(). |
| 2991 | 1 | `public double operatingExpenses()` | ...less the tellers and the lights. |
| 2994 | 1 | `public double afterTrading()` | ...plus what the desk made or lost. |
| 2997 | 1 | `public double profitBeforeTax()` | What it made before the city took its share. |

### tax (lines 2999-3115)

| line | len | member | says |
|---:|---:|---|---|
| 3042 | 1 | `public double getProfitLastMonth()` | Profit the tax is charged on: the month that has just finished - and, since 0.7.7, what the month before it earned after its own close (getCarriedLate(); see PROFIT THAT LANDS AFTER THE CLOSE). |
| 3045 | 46 | `public void closeMonth()` | Called at the end of the month, once the profit is final. |
| 3103 | 3 | `public double lateProfit()` | What this month earned after its close: the part of its profit next month's tax and dividend will carry. |
| 3108 | 1 | `public double getCarriedLate()` | What last month earned after its close, inside getProfitLastMonth(). |
| 3111 | 4 | `public void restoreLateProfit(double value)` | The load path: what the saved month earned after its close. |

### LAST MONTH, KEPT ON PURPOSE (lines 3116-3244)

| line | len | member | says |
|---:|---:|---|---|
| 3140 | 10 | `public double[] lastMonthToSave()` |  |
| 3174 | 20 | `public void restoreLastMonth(double[] state)` | ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the cost-of-funds floor. |
| 3196 | 1 | `public void setProfitLastMonth(double value)` | Restored from the save, so next month taxes the right figure. |
| 3208 | 1 | `public void setDepositRate(double rate)` | What savers were paid in the month this save was taken in. |
| 3211 | 1 | `public double getTaxPaid()` | What the city took this month. |
| 3224 | 5 | `public double chargeTax(double annualProfitRate)` | Hands the city its share of last month's profit. |
| 3231 | 3 | `private static double taxOn(double profit, double profitTaxRate)` | The city's share of a month's profit at this rate: nothing on a loss. |
| 3241 | 3 | `public double getProfitAfterTaxLastMonth(double profitTaxRate)` | Last month's profit AFTER the tax it will be charged at this rate: what the owners are paid a share of (Game.payDividends()) and what the register records (Equity.recordMonth()), as every sector's own net income is. |

### THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 3245-3323)

### the allowance (lines 3324-3625)

| line | len | member | says |
|---:|---:|---|---|
| 3336 | 3 | `public static double lossIfDefaulted(double principal, double assets)` | What the BACKSTOP would cost the bank on a business owing this against these assets (BusinessDebtManager.restructure()): everything owed past BusinessDebtManager.RESTRUCTURE_TARGET of its assets - all of it, for a sec... |
| 3341 | 4 | `public static boolean sectorWatched(double principal, double assets)` | A business borrower in trouble: owing past SECTOR_WATCH_LEVERAGE of its assets, or anything at all against none. |
| 3354 | 6 | `public static double stageTwoShare(double principal, double assets)` | The share of a sector's firms, by what they owe, past the watch line: the curve's spread of fortunes (BusinessDebtManager.ASSET_VOLATILITY) read at SECTOR_WATCH_LEVERAGE instead of the default point, N(ln(L / SECTOR_W... |
| 3392 | 3 | `public static double sectorAllowance(double principal, double assets)` | The allowance a business's book holds, read off the curve its firms default on (0.7.8) - the same PD(L) that writes the month's slice off (BusinessDebtManager.defaultProbability()), so the allowance is what the slices... |
| 3405 | 3 | `public static double sectorAllowance(double owed, double principal, double assets)` | ...ON WHAT IT OWES NOW, READ AT ITS QUARTER (0.7.8): the curve read at principal over assets - the averages of its last quarter's readings (BusinessDebtManager.quarterPrincipal(), quarterAssets()) - and the loss struc... |
| 3418 | 13 | `public static double sectorAllowance(double owed, double principal, double assets, double lossGivenDefault)` | ...AT A LOSS GIVEN DEFAULT OF ITS OWN (0.7.12): a loan's for what the sector owes the bank, a bond's for the sector's bonds it holds (BusinessDebtManager, RECOVERIES BY INSTRUMENT, since round 2; round 1 read the two ... |
| 3439 | 3 | `private static double floorAt(double lgd)` | THE FLOOR IS THE SOUND BOOK'S LOSS, at this loss given default (0.7.12): BASE_LOSS_RATE is a sound LOAN's loss, so at a loan's loss given default it is BASE_LOSS_RATE, and a dollar that loses more when it defaults - a... |
| 3444 | 3 | `public static boolean householdWatched(double monthsOwed)` | A household cell in trouble: owing past HOUSEHOLD_WATCH_MONTHS of its income. |
| 3455 | 8 | `public static double householdAllowance(double debt, double monthsOwed)` | The allowance a household cell's debt holds: its year's expected loss while sound; once it is in trouble its whole debt - a discharge writes all of it off (Household.discharge()) - scaled from nothing at HOUSEHOLD_WAT... |
| 3497 | 17 | `public void provide(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | THE MONTH'S PROVISION: sets every book's allowance from its borrowers as they stand now, and draws the month's write-offs against what each book held when the month opened. |
| 3522 | 11 | `public void openAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | ...and a bank that has never held one: the allowance its borrowers call for, set up WITHOUT a provision - the month it opens on holds it from its start. |
| 3534 | 27 | `private void strikeAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` |  |
| 3563 | 5 | `public double getAllowance()` | Everything set aside against the book. |
| 3570 | 1 | `public double getSectorAllowance()` | ...against the businesses' book, all of it. |
| 3572 | 1 | `public double getSectorAllowance(String sector)` | ...against one sector's. |
| 3585 | 4 | `public double[] getAllowanceReading(String sector)` | What the allowance on a sector was struck on, as Game.bankReadings() handed it over at the month's provision: {the quarter's principal, its assets, what it owes that nobody insures, the loans' loss given default, the ... |
| 3591 | 1 | `public double getWrittenOff(String sector)` | What this month wrote off one sector's book - its defaulted firms' slice, or the backstop (0.7.8: the Bank tab's "this month", beside the allowance). |
| 3593 | 1 | `public double getHouseholdAllowance()` | ...against the families'. |
| 3595 | 1 | `public int getStage(String sector)` | 2 when that sector's book is in trouble - most of its firms past the watch line - 1 when most are sound. |
| 3597 | 1 | `public double getStageTwoShare(String sector)` | The share of that sector's book in stage 2, as the last provide() struck it (stageTwoShare()). |
| 3599 | 1 | `public int getHouseholdStage()` | 2 when any family's line is in trouble, 1 when none is. |
| 3601 | 1 | `public double getHouseholdWatchedDebt()` | The families' debt in the cells that are in trouble. |
| 3603 | 1 | `public java.util.Set<String> getSectorsWatched()` | The sectors whose books are in stage 2. |
| 3605 | 1 | `public int getBooksWatched()` | How many of its books are in stage 2: each sector's, and the families' as one (0.7.9, the Bank tab's count of borrowers in trouble). |
| 3607 | 1 | `public double getOpeningAllowance()` | The allowance the month opened with. |
| 3615 | 1 | `public double provisions()` | THE PROVISION, the income statement's line: what the allowance rose by this month and whatever was written off that it had not set aside - which is the allowance's move plus every write-off. |
| 3618 | 1 | `public double getAllowanceUsed()` | The month's write-offs that the allowance had already set aside. |
| 3621 | 1 | `public double getWriteOffsBeyondAllowance()` | ...and the part it had not, which reached the statement the month it was written off. |
| 3624 | 1 | `public double getProvisionCharge()` | The part of the provision that went into the allowance: its rise less what the write-offs drew out of it. |

### what it holds (lines 3626-3736)

| line | len | member | says |
|---:|---:|---|---|
| 3645 | 10 | `private void recordLosses()` | Files the month that has just closed into the loss record. |
| 3672 | 8 | `public double trailingLossRate()` | The last twelve recorded months' provisions over their average weighted book - or over what its branches' founding capital is built to carry at the minimum (branches x paidInPerBranch / CAPITAL_RATIO), whichever is la... |
| 3682 | 1 | `public double getWorstLossRate()` | The worst year it has lived through, as the capital target reads it. |
| 3729 | 1 | `public double capitalBuffer()` | Its buffer over the minimum: the worst year it has recorded, never less than CONSERVATION_BUFFER nor more than MAX_BUFFER. |
| 3732 | 1 | `public double capitalTarget()` | THE TARGET it chooses: the city's minimum and its own buffer. |
| 3735 | 1 | `public double capitalTop()` | ...and the top of its band. |

### the leverage ratio (0.7.11, round 2) (lines 3737-3816)

| line | len | member | says |
|---:|---:|---|---|
| 3759 | 1 | `public double exposure()` | THE EXPOSURE MEASURE the leverage ratio is struck on: everything on its balance sheet at the value the sheet carries it at, whatever it weighs - totalAssets(). |
| 3762 | 4 | `public double leverageRatio()` | Equity over the exposure measure: the leverage ratio a regulator reads. |
| 3776 | 1 | `public double leverageTarget()` | ITS OWN LEVERAGE TARGET: LEVERAGE_RATIO_MIN scaled by the buffer it chose on the risk side - LEVERAGE_RATIO_MIN x capitalTarget() / CAPITAL_RATIO. |
| 3779 | 1 | `public double leverageTop()` | ...and the top of its band on the same measure: LEVERAGE_RATIO_MIN x capitalTop() / CAPITAL_RATIO. |
| 3796 | 3 | `public double minimumEquity()` | THE MINIMUM THE CITY REQUIRES, IN MONEY: the larger of the risk-based one, CAPITAL_RATIO of the weighted book, and the leverage one, LEVERAGE_RATIO_MIN of the exposure. |
| 3801 | 3 | `public boolean leverageBinds()` | True when the leverage requirement is the larger - when a bank's zero-weighted assets are what its capital is short against. |
| 3806 | 1 | `public double bindingRatio()` | Its capital as a ratio on the measure that binds: the leverage ratio when leverageBinds(), the risk-based capitalRatio() otherwise - the figure the Bank tab's bar and status read. |
| 3809 | 1 | `public double bindingMinimum()` | The minimum on the binding measure: LEVERAGE_RATIO_MIN or CAPITAL_RATIO. |
| 3812 | 1 | `public double bindingTarget()` | Its target on the binding measure: leverageTarget() or capitalTarget(). |
| 3815 | 1 | `public double bindingTop()` | ...and the top of its band on it: leverageTop() or capitalTop(). |

### what it does with profit (lines 3817-3917)

| line | len | member | says |
|---:|---:|---|---|
| 3826 | 1 | **type** `public enum Payout` | What the bank does with its profit, as its capital stands. |
| 3833 | 3 | `public double targetEquity()` | The equity its target calls for on the book it has: the larger of its target on the weighted book and its leverage target on the exposure (0.7.11, round 2 - minimumEquity() says why). |
| 3845 | 4 | `public double topEquity()` | The equity at the top of its band - and NEVER LESS THAN WHAT ITS STANDING BRANCHES WERE FOUNDED WITH, paidInPerBranch each: the capital a counter is opened with is what the running costs are already priced on (strikeP... |
| 3851 | 4 | `public double excessCapital()` | What it holds past the top of its band: what it returns, a twelfth a month. |
| 3857 | 9 | `public Payout payoutStance()` | Where its capital puts it, for the words and the rules. |
| 3868 | 10 | `public String payoutDecision()` | ...in words, for the Bank tab. |
| 3888 | 7 | `public double dividendDue(double profitAfterTax)` | WHAT IT PAYS ITS OWNERS this month, on last month's profit after tax. |
| 3903 | 8 | `public double payOwners(double profitAfterTax)` | Pays its owners what dividendDue() says, and keeps what the rule read - the profit it was paid on, the excess over the top and the room over the target - so the month's decision can be read back. |
| 3914 | 1 | `public double getPayoutProfit()` |  |
| 3915 | 1 | `public double getPayoutExcess()` |  |
| 3916 | 1 | `public double getPayoutOverTarget()` |  |

### its own shares (lines 3918-4079)

| line | len | member | says |
|---:|---:|---|---|
| 3952 | 6 | `public void buyBackOwnShares(double paid)` | The desk bought the bank's own shares back and cancelled them: cash out, equity down, no income. |
| 3960 | 5 | `public void issueOwnShares(double received)` | ...and issued new ones: cash in, equity up, no income. |
| 3971 | 4 | `public boolean buysBackOwnShares()` | True when the desk buys the bank's own shares back from whoever sells: standing, and at or over its own capital target. |
| 3996 | 4 | `public boolean issuesOwnShares()` | ...and when it issues new ones to whoever buys: standing, lending, and UNDER its own target - raising the capital its rule says it is short of, and never while it holds what it wants. |
| 4014 | 3 | `public double spareCapital(double inventory)` | WHAT IT HOLDS OVER ITS TARGET: its equity less targetEquity(), with the desk's inventory carried at `inventory` rather than at the securities line's last mark - the line lags the desk's deals within a month until the ... |
| 4019 | 6 | `private double spareOnRisk(double inventory)` | Its spare capital against its target on the weighted book, the inventory carried at `inventory`. |
| 4031 | 6 | `private double spareOnLeverage(double inventory)` | ...and against its leverage target on the exposure (0.7.11, round 2). |
| 4039 | 1 | `public double spareCapital()` | ...on the books as they stand: equity() less targetEquity(). |
| 4058 | 3 | `public double buybackRoom(double inventory)` | THE MOST IT MAY SPEND BUYING ITS OWN SHARES BACK NOW: what it holds over its target, spareCapital(inventory), so that no purchase takes it under the target - a month's buybacks never exceed the capital over target at ... |
| 4062 | 1 | `public double getSharesBoughtBack()` |  |
| 4063 | 1 | `public double getSharesIssued()` |  |
| 4071 | 1 | `public double dividendsOverYear()` | Dividends over the last twelve months, this one included. |
| 4073 | 1 | `public double buybacksOverYear()` | ...and its own shares bought back. |
| 4076 | 3 | `public double returnOnEquity()` | This month's net income over the equity it opened with, a year: the return a bank is read by. |

### what it lends (lines 4080-4163)

| line | len | member | says |
|---:|---:|---|---|
| 4094 | 26 | `public double lendingGrowthLimit()` | HOW FAST THE BANK LETS A BORROWER'S DEBT GROW THIS MONTH, on the capital it has: no limit at or over its target (and with no branch - a city with no bank is lent to from outside); none under the minimum or failed; in ... |
| 4122 | 7 | `public boolean lendsOnlyToKeepBorrowersGoing()` | True when it lends only what keeps its existing borrowers going: under the minimum, or failed. |
| 4131 | 6 | `public double lendingLimit()` | The growth of the book the capital rule allows this month, in money: infinite when it lends freely. |
| 4139 | 8 | `public String lendingStance()` | ...in words. |

### the save (lines 4164-4320)

| line | len | member | says |
|---:|---:|---|---|
| 4175 | 18 | `public java.util.Map<String, double[]> allowanceToSave()` | The allowance, book by book (0.7.8): each sector's name, and HOUSEHOLD_BOOK for the families, to {the allowance, what it held when the month opened, what the month wrote off, and whether it is in trouble - 1 or 0 for ... |
| 4195 | 29 | `public boolean restoreAllowance(java.util.Map<String, double[]> saved)` | ...and back. |
| 4225 | 5 | `private static double sum(java.util.Map<String, Double> m)` |  |
| 4238 | 12 | `public double[] capitalRecordToSave()` | The record the target and the owners' year are struck from (0.7.8): the months recorded, the worst year, the rings of provisions and of the weighted book, the owners' month count and the rings of dividends and buybacks. |
| 4251 | 11 | `public void restoreCapitalRecord(double[] in)` |  |
| 4272 | 20 | `public double[] monthLinesToSave()` | THE MONTH'S STATEMENT LINES (0.7.8), for the save. |
| 4294 | 26 | `public void restoreMonthLines(double[] v)` | ...and back. |

### reading (lines 4321-4470)

| line | len | member | says |
|---:|---:|---|---|
| 4323 | 1 | `public double getCash()` |  |
| 4324 | 1 | `public double getDeposits()` |  |
| 4325 | 1 | `public double getBranches()` |  |
| 4326 | 1 | `public double getSectorBook()` |  |
| 4327 | 1 | `public double getCityBook()` |  |
| 4328 | 1 | `public double getHouseholdBook()` |  |
| 4329 | 1 | `public double getInterestEarned()` |  |
| 4330 | 1 | `public double getWriteOffs()` |  |
| 4331 | 1 | `public double getPayroll()` |  |
| 4332 | 1 | `public double getUpkeep()` |  |
| 4333 | 1 | `public double getLentToHouseholds()` |  |
| 4334 | 1 | `public double getRepaidByHouseholds()` |  |
| 4344 | 3 | `public double getNetIncome()` | The month's profit: interest earned, less the cost of the money, less the loans that died, less the cost of running the place. |
| 4356 | 44 | `public boolean wantsBranch()` | True when the city should be opening another counter. |
| 4429 | 41 | `public boolean branchWouldPayForItself()` | Whether the counter earns more than it costs to keep open. |

### a branch that does not pay is closed (0.7.11, round 2) (lines 4471-4851)

| line | len | member | says |
|---:|---:|---|---|
| 4490 | 6 | `public boolean branchesCoverTheirStaff()` | Whether what the book kept last month covers what its branches cost - branchWouldPayForItself()'s own two inputs, keptPerBranch() against runningCostPerBranch(), read the other way. |
| 4524 | 3 | `public boolean closesBranch()` | THE BRANCH TEST RUN IN REVERSE, AND IT WAS MISSING. |
| 4529 | 1 | `public int getUncoveredMonths()` | Closed months in a row the book has not kept its branches' staff. |
| 4546 | 7 | `public double bookAnotherBranchWouldCarry()` | The loan book one more branch would take onto the bank's own account. |
| 4565 | 8 | `public double capacityWith(double branchCount)` | Capacity this bank would have with a given number of branches. |
| 4586 | 10 | `public double headroom()` | How much more it could lend before it counts itself full (EASY_STRAIN): what the carry trade may take - and, since 0.7.8, no more than keeps the bank at its own capital target. |
| 4597 | 1 | `public void setCash(double value)` |  |
| 4599 | 76 | `public void reset()` |  |
| 4695 | 147 | `public void redenominate(double scale)` | Every figure on the bank's balance sheet, in the new unit. |
| 4845 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### WHAT THE BANK TAB READS (0.7.9) (lines 4852-4883)

### the interest, by who paid it (lines 4884-4920)

| line | len | member | says |
|---:|---:|---|---|
| 4902 | 9 | `public void takeInterest(double fromCity, double fromBusinesses)` | The businesses' interest and the city's coupons, settled together: the same cash and income as takeInterest() on their sum, to the bit, and each kept by who paid it. |
| 4913 | 1 | `public double getInterestFromBusinesses()` | What the businesses paid it in interest this month. |
| 4915 | 1 | `public double getInterestFromCity()` | ...the city, in coupons on the paper the bank holds. |
| 4917 | 1 | `public double getInterestFromHouseholds()` | ...the families, on their credit lines. |
| 4919 | 1 | `public double getDiscountAccreted()` | ...and the discount on the city's paper it earned this month, which no cash carries. |

### what moved it between two presses (lines 4921-4957)

| line | len | member | says |
|---:|---:|---|---|
| 4930 | 1 | `public double getTreasuryBuybackGain()` |  |
| 4939 | 1 | `public double getAllowanceOpened()` |  |
| 4947 | 1 | `public double getBailoutsLifetime()` |  |
| 4956 | 1 | `public boolean isMonthKnown()` |  |

### its year of statements (lines 4958-5145)

| line | len | member | says |
|---:|---:|---|---|
| 4970 | 11 | **type** `public enum Line` | The lines of a month's statement the Bank tab sets beside last month's and adds up over a year, every one money: the interest and who paid it; what savers and the window were paid; fees and their three kinds; provisio... |
| 4983 | 1 | `public double revenue()` | What it earned before provisions and costs: net interest, fees, the desk and the city's paper - what its costs are read against. |
| 4994 | 1 | `public double getRetained()` | What it kept of the month's profit once its owners were paid: net income less the dividend and its own shares bought back. |
| 4997 | 38 | `public double thisMonth(Line line)` | A line as this month stands. |
| 5045 | 5 | `private void fileStatement()` |  |
| 5052 | 1 | `public boolean knowsLastMonth()` | True when last month is on file: a month was played before this one, or a save carried it. |
| 5055 | 4 | `public double lastMonth(Line line)` | A line as last month ended, everything booked after its close included. |
| 5061 | 1 | `public int monthsInYear()` | How many months the year's figures cover: this one and those on file, YEAR_MONTHS at most. |
| 5064 | 8 | `public double overYear(Line line)` | A line added up over monthsInYear(), this month included - for the flows; the two stocks want averageOverYear(). |
| 5074 | 1 | `public double averageOverYear(Line line)` | ...and averaged over them: a month's worth. |
| 5081 | 4 | `public double returnOnEquityOverYear()` | What it earned over the year, at a yearly rate, on the equity it held on average: the return a bank is read by, and steadier than a month's (returnOnEquity()). |
| 5091 | 4 | `public double provisionRateOverYear()` | Provisions over the year, at a yearly rate, as a share of the book it held on average: its credit losses as a bank reports them - a sound book's is BASE_LOSS_RATE. |
| 5101 | 4 | `public double netInterestMarginOverYear()` | Net interest income over the year, at a yearly rate, on the book it held on average: its net interest margin, steadier than a month's (netInterestMargin()). |
| 5111 | 4 | `public double costShareOverYear()` | Its staff and branches over the year as a share of what it earned before them (revenue()): the efficiency ratio, about 50-60% at a real bank. |
| 5117 | 10 | `public double[] statementYearToSave()` | The year of statements, for the save: how many are filed, how many lines each, then the ring's months in slot order. |
| 5134 | 11 | `public void restoreStatementYear(double[] in)` | ...and back. |

### its balance sheet (0.7.13) (lines 5146-5323)

| line | len | member | says |
|---:|---:|---|---|
| 5161 | 9 | **type** `public enum Sheet` | THE LINES OF ITS BALANCE SHEET, as the model books it (THE THREE STATEMENTS): what totalAssets() and totalLiabilities() sum, line by line, the allowance negative, and equity() the residual; then the two deposits it co... |
| 5196 | 26 | `public double sheet(Sheet line)` | A line of its balance sheet as it stands: its reserves at the central bank (the cash it is not borrowing; its placements abroad came home to the central bank in 0.7.0); what the businesses owe it outside the insured m... |
| 5229 | 1 | `public double sheetResidual()` | What its lines leave unexplained: the asset lines, less the liability lines, less equity. |
| 5232 | 1 | `public double yearAgoResidual()` | ...and the same of the sheet a year ago; nothing when none is on file. |
| 5235 | 1 | `public double liabilitiesAndEquity()` | The other side of the sheet: its liabilities and its equity together. |
| 5238 | 1 | `public double yearAgoLiabilitiesAndEquity()` | ...and a year ago; nothing when none is on file. |
| 5240 | 6 | `private static double residual(java.util.function.ToDoubleFunction<Sheet> at)` |  |
| 5257 | 10 | `private void fileSheet()` |  |
| 5269 | 1 | `public boolean knowsYearAgo()` | True when the sheet a year before this one is on file: a year played in this build, or a save that carried one. |
| 5272 | 3 | `public double yearAgo(Sheet line)` | A line of the sheet a year before this one; nothing when none is on file. |
| 5277 | 3 | `public double yearAgoLoansToSector(int sector)` | What the sector at this index of Sectors.KEYS owed it a year ago, outside its insured mortgages and interim financing. |
| 5282 | 4 | `public double yearAgoInterimToSector(int sector)` | ...and its interim financing then. |
| 5288 | 11 | `public double[] sheetYearToSave()` | The year of sheets, for the save: how many are filed, the lines and the sectors each holds, then the ring's months in slot order. |
| 5306 | 17 | `public void restoreSheetYear(double[] in)` | ...and back. |

### its rates, in a ladder (lines 5324-5374)

| line | len | member | says |
|---:|---:|---|---|
| 5340 | 20 | **type** `public record Ladder(double policy, double savers, double saversChose, double saversShare, double fundingPo...` | THE LADDER OF ITS RATES at one policy rate, read at one moment: the policy rate; what savers were paid, the rate the bank chose, the share of the policy rate its funding asks it to pass on and the funding position tha... |
| 5346 | 1 | `public double saversOverPolicy()` _(in Bank.Ladder)_ | Savers' rate less the policy rate: under it by the bank's margin on a deposit. |
| 5348 | 1 | `public double transferOverPolicy()` _(in Bank.Ladder)_ | The funds-transfer price over the policy rate: the window's penalty on its share, and the term premium. |
| 5350 | 1 | `public double primeOverTransfer()` _(in Bank.Ladder)_ | Prime over the funds-transfer price: the running costs, the expected loss and the capital charge. |
| 5352 | 1 | `public double parts()` _(in Bank.Ladder)_ | The four parts added up in prime's own order - which is prime. |
| 5354 | 1 | `public double overPrime(double rate)` _(in Bank.Ladder)_ | A borrower's rate over prime: the step each borrower's rung is labelled with - its own risk, or for the carry trade the costs it does not carry. |
| 5356 | 1 | `public double mortgageTransferOverPolicy()` _(in Bank.Ladder)_ | An insured mortgage's money over the policy rate: the window's penalty on its share, and the ten-year term premium. |
| 5358 | 1 | `public double mortgageOverTransfer()` _(in Bank.Ladder)_ | An insured mortgage's rate over its money: running the bank, and the capital its leverage requirement ties up (round 2). |
| 5362 | 12 | `public Ladder ladder(double policyAnnual)` | The ladder at this policy rate. |

### in words (lines 5375-5436)

| line | len | member | says |
|---:|---:|---|---|
| 5390 | 27 | `public String status()` | THE BANK'S STATE IN ONE SENTENCE, with the figure that decides it: the first thing the Bank tab says. |
| 5419 | 17 | `public String targetReason()` | Why its capital target is what it is, in words: the cap, its worst year, or the standard buffer and why. |

### what its book weighs (lines 5437-5479)

| line | len | member | says |
|---:|---:|---|---|
| 5440 | 1 | **type** `public enum Book` | The eight things on its books that capacity weighs: the four it lends on, the insured mortgages inside the businesses' (0.7.11), the desk's shares, and since 0.7.12 the businesses' bonds it holds and the weight the bo... |
| 5448 | 1 | **type** `public record WeightRow(Book book, double face, double term, double risk, double weighted)` | One row of what the book weighs: its face; the share of it its remaining term counts for (maturityWeight(), on average over its loans - 1 where nothing runs off); its risk weight; and what it weighs, face x term x risk. |
| 5457 | 17 | `public java.util.List<WeightRow> weightTable()` | Every row, the desk's shares included: the weighted column foots to getWeightedBook(). |
| 5475 | 4 | `private static WeightRow weightRow(Book book, double face, double risk, double weighted)` |  |

### its funding (lines 5480-5503)

| line | len | member | says |
|---:|---:|---|---|
| 5483 | 1 | `public double getHouseholdDeposits()` | What the families have banked with it. |
| 5485 | 1 | `public double getSectorDeposits()` | ...and what the businesses hold in credit. |
| 5488 | 1 | `public double getDepositsPerBranch()` | How much of a city's savings one branch reaches, in today's money: DEPOSITS_PER_BRANCH, reformed with every other figure. |
| 5490 | 1 | `public double getPaidInPerBranch()` | What its owners put up when a branch opens, in today's money: PAID_IN_PER_BRANCH, reformed. |
| 5493 | 1 | `public double branchReach()` | How much of the city's own savings its branches can reach: the branches standing times getDepositsPerBranch(). |
| 5495 | 1 | `public double localDeposits()` | The city's own savings with it: everything banked, less the world's. |
| 5497 | 1 | `public double localDepositsReached()` | ...the part its branches reach - which, with the world's, is depositsGathered(). |
| 5499 | 1 | `public double localDepositsBeyondReach()` | ...and the part they do not: savings only another branch would reach. |
| 5502 | 1 | `public double fundingLimit()` | What its funding would carry: what its branches gathered, lent LEVERAGE times over - the second of capacity()'s two limits. |

### another branch (lines 5504-5524)

| line | len | member | says |
|---:|---:|---|---|
| 5507 | 1 | `public double capacityAnotherBranchWouldAdd()` | The capacity one more branch would add, the capital it would open with counted: nothing when the deposits are the limit and the branches already reach them all. |
| 5510 | 1 | `public double overflowPastComfortable()` | The weighted book past what the bank comfortably carries, EASY_STRAIN of its capacity: what another branch could take onto its own account. |
| 5513 | 1 | `public double runningCostPerBranch()` | What one branch cost to run last month: its payroll and upkeep over the branches standing. |
| 5521 | 3 | `public double keptPerBranch()` | What one branch's share of the book kept last month: its book per branch at last month's kept margin (net interest and fees over the book) - what branchWouldPayForItself() weighs against runningCostPerBranch(). |

### how its equity moved (lines 5525-5559)

| line | len | member | says |
|---:|---:|---|---|
| 5542 | 10 | **type** `public record EquityMovement(double opening, double kept, double fromShareholders, double fromCity, double ...` | HOW ITS EQUITY MOVED since the month opened, every cause named: what it kept (net income); capital put in by its shareholders at home and abroad, and by the city in a rescue; the founding settlement, the month the cit... |
| 5547 | 4 | `public double residual()` _(in Bank.EquityMovement)_ | What none of the causes explains. |
| 5554 | 5 | `public EquityMovement equityMovement()` | The month's movement, as it stands. |

### its equity, in two parts (lines 5560-5643)

| line | len | member | says |
|---:|---:|---|---|
| 5601 | 1 | `public boolean knowsEquitySplit()` | True when this bank has kept its equity in two parts since it was founded; false on a save from before 0.7.13's round 2. |
| 5604 | 3 | `public double paidInThisMonth()` | What its owners and the city put in this month: its offerings at home and abroad, new shares, less shares bought back, and a rescue. |
| 5609 | 4 | `public double retainedThisMonth()` | ...and everything else that moved its equity this month: its net income, less its dividend, the founding settlement, what its creditors absorbed, the treasury's buybacks, an older save's allowance. |
| 5615 | 1 | `public double paidInCapital()` | Its paid-in capital as it stands. |
| 5618 | 1 | `public double retainedEarnings()` | Its retained earnings as they stand - the Balance sheet page's, not getRetained()'s month's payout. |
| 5621 | 3 | `public double equitySplitResidual()` | What the two parts leave unexplained against equity(): nothing when every cause is routed, and nothing when !knowsEquitySplit(). |
| 5626 | 1 | `public double paidInOpening()` | Its paid-in capital at the top of the month, for the save; nothing when !knowsEquitySplit(). |
| 5629 | 1 | `public double retainedOpening()` | ...and its retained earnings. |
| 5636 | 6 | `public void restoreEquitySplit(Double paidIn, Double retained)` | The load path: the two counters at the top of the saved month, after the month's lines are restored (restoreMonthLines()). |


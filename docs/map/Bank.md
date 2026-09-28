# Bank.java - 6,317 lines · 425 methods · 58 constants · model

`ham/citybuildersim/Bank.java` - generated 2026-09-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (13), [Sectors](Sectors.md) (7), [CentralBank](CentralBank.md) (4), [Mortgage](Mortgage.md) (3), [Ladder](Ladder.md) (3), [DebtManager](DebtManager.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Equity](Equity.md) (1), [TreasuryFund](TreasuryFund.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (37):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildScreen](BuildScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [SummaryScreen](SummaryScreen.md), [TimeSkipReport](TimeSkipReport.md), [UserInterface](UserInterface.md)

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
| 1412 | · the arithmetic |
| 1442 | THE TRADING DESK |
| 1520 | · the desk held to its capital (0.7.8) |
| 2140 | THE CITY'S CAPITAL (0.7.14): A RESOLUTION, AND THE PREFERRED |
| 2754 | · the flows |
| 2829 | THE CITY'S PAPER CHANGES HANDS (0.7.1) |
| 2983 | FEES (0.7.7) |
| 3051 | THE FUNDING SIDE |
| 3287 | · · SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY. |
| 3340 | WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. |
| 3472 | THE THREE STATEMENTS |
| 3584 | · tax |
| 3701 | · LAST MONTH, KEPT ON PURPOSE |
| 3830 | THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 3909 | · the allowance |
| 4211 | · what it holds |
| 4322 | · the leverage ratio (0.7.11, round 2) |
| 4402 | · what it does with profit |
| 4524 | · its own shares |
| 4689 | · what it lends |
| 4773 | · the save |
| 4940 | · reading |
| 5090 | · a branch that does not pay is closed (0.7.11, round 2) |
| 5494 | WHAT THE BANK TAB READS (0.7.9) |
| 5526 | · the interest, by who paid it |
| 5563 | · what moved it between two presses |
| 5600 | · its year of statements |
| 5788 | · its balance sheet (0.7.13) |
| 5969 | · its rates, in a ladder |
| 6020 | · in words |
| 6082 | · what its book weighs |
| 6125 | · its funding |
| 6149 | · another branch |
| 6170 | · how its equity moved |
| 6213 | · its equity, in two parts |

## Enum constants

| line | constant | says |
|---:|---|---|
| 4411 | `Bank.Payout.NO_BANK` |  |
| 4411 | `Bank.Payout.FAILED` |  |
| 4411 | `Bank.Payout.UNDER_MINIMUM` |  |
| 4411 | `Bank.Payout.REBUILDING` |  |
| 4411 | `Bank.Payout.PAYING` |  |
| 4411 | `Bank.Payout.RETURNING` |  |
| 5613 | `Bank.Line.INTEREST` |  |
| 5613 | `Bank.Line.FROM_BUSINESSES` |  |
| 5613 | `Bank.Line.FROM_HOUSEHOLDS` |  |
| 5613 | `Bank.Line.FROM_CITY` |  |
| 5613 | `Bank.Line.FROM_CARRY` |  |
| 5613 | `Bank.Line.FROM_RESERVES` |  |
| 5613 | `Bank.Line.DISCOUNT` |  |
| 5614 | `Bank.Line.SAVERS` |  |
| 5614 | `Bank.Line.WINDOW` |  |
| 5614 | `Bank.Line.NET_INTEREST` |  |
| 5615 | `Bank.Line.FEES` |  |
| 5615 | `Bank.Line.ACCOUNT_FEES` |  |
| 5615 | `Bank.Line.LOAN_FEES_PAID` |  |
| 5615 | `Bank.Line.LOAN_FEES_OWED` |  |
| 5616 | `Bank.Line.PROVISIONS` |  |
| 5616 | `Bank.Line.WRITE_OFFS` |  |
| 5616 | `Bank.Line.TRADING` |  |
| 5616 | `Bank.Line.PAPER_GAINS` |  |
| 5616 | `Bank.Line.REVENUE` |  |
| 5617 | `Bank.Line.COSTS` |  |
| 5617 | `Bank.Line.PAYROLL` |  |
| 5617 | `Bank.Line.UPKEEP` |  |
| 5617 | `Bank.Line.PRE_TAX` |  |
| 5617 | `Bank.Line.TAX` |  |
| 5617 | `Bank.Line.NET` |  |
| 5618 | `Bank.Line.DIVIDENDS` |  |
| 5618 | `Bank.Line.BUYBACKS` |  |
| 5618 | `Bank.Line.ISSUED` |  |
| 5618 | `Bank.Line.RETAINED` |  |
| 5619 | `Bank.Line.BOOK` |  |
| 5619 | `Bank.Line.EQUITY` |  |
| 5621 | `Bank.Line.FROM_BONDS` | 0.7.12's: the coupons on its bonds, its underwriting fees and its gains on bonds. |
| 5621 | `Bank.Line.UNDERWRITING` |  |
| 5621 | `Bank.Line.BOND_GAINS` |  |
| 5804 | `Bank.Sheet.RESERVES` |  |
| 5804 | `Bank.Sheet.BUSINESS_LOANS` |  |
| 5804 | `Bank.Sheet.INTERIM` |  |
| 5804 | `Bank.Sheet.MORTGAGES` |  |
| 5804 | `Bank.Sheet.FAMILIES` |  |
| 5804 | `Bank.Sheet.CARRY` |  |
| 5804 | `Bank.Sheet.CITY_PAPER` |  |
| 5804 | `Bank.Sheet.BONDS` |  |
| 5804 | `Bank.Sheet.ALLOWANCE` |  |
| 5804 | `Bank.Sheet.DESK` |  |
| 5805 | `Bank.Sheet.ASSETS` |  |
| 5806 | `Bank.Sheet.DEPOSIT_FUNDING` |  |
| 5806 | `Bank.Sheet.WINDOW` |  |
| 5806 | `Bank.Sheet.FOREIGN_DEPOSITS` |  |
| 5806 | `Bank.Sheet.DESK_SHORT` |  |
| 5806 | `Bank.Sheet.UNEARNED_DISCOUNT` |  |
| 5807 | `Bank.Sheet.LIABILITIES` |  |
| 5808 | `Bank.Sheet.EQUITY` |  |
| 5809 | `Bank.Sheet.HOUSEHOLD_DEPOSITS` |  |
| 5809 | `Bank.Sheet.SECTOR_DEPOSITS` |  |
| 5810 | `Bank.Sheet.PAID_IN` |  |
| 5810 | `Bank.Sheet.RETAINED` |  |
| 5812 | `Bank.Sheet.PREFERRED` | The city's preferred at par (0.7.14): equity's third part, beside paid in and retained. |
| 6085 | `Bank.Book.BUSINESSES` |  |
| 6085 | `Bank.Book.CITY` |  |
| 6085 | `Bank.Book.FAMILIES` |  |
| 6085 | `Bank.Book.CARRY` |  |
| 6085 | `Bank.Book.DESK` |  |
| 6085 | `Bank.Book.MORTGAGES` |  |
| 6085 | `Bank.Book.BONDS` |  |
| 6085 | `Bank.Book.CONCENTRATION` |  |

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
| 1458 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 1806 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 1999 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 2212 | `Bank.PREFERRED_MIN_SHARE` | `.01` | The least preferred the bank asks for, of its risk-weighted assets: 1%, TARP's "not less than 1% of its risk-weighted assets". |
| 2215 | `Bank.PREFERRED_MAX_SHARE` | `.03` | ...and the most: 3%, TARP's "not more than ... |
| 2218 | `Bank.PREFERRED_RATE` | `.05` | The preferred's dividend, a year, until its fifth anniversary: 5%, TARP's "cumulative dividends at a rate of 5% per annum until the fifth anniversary". |
| 2221 | `Bank.PREFERRED_STEP_RATE` | `.09` | ...and after it: 9%, TARP's "thereafter at a rate of 9% per annum". |
| 2224 | `Bank.PREFERRED_STEP_MONTHS` | `60` | The months to the step: the fifth anniversary. |
| 2227 | `Bank.PREFERRED_CONSENT_MONTHS` | `36` | The months the city's consent binds - no buyback, no rise in the common dividend a share: three years, TARP's "until the third anniversary". |
| 2230 | `Bank.PREFERRED_REDEEM_MONTHS` | `36` | When a block is redeemed whole: its third anniversary, the first TARP's term sheet allows it from anything but new common ("may not be redeemed for a period of three years ... |
| 2233 | `Bank.WARRANT_SHARE` | `.15` | The warrants' reach, of the preferred's amount: 15%, TARP's "aggregate market price equal to 15% of the Senior Preferred amount". |
| 2236 | `Bank.WARRANT_TERM_MONTHS` | `120` | The warrants' term, in months: ten years, TARP's. |
| 3010 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 3013 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 3397 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - set so that this bank... |
| 3400 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of funding bid for it ... |
| 3403 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 3912 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not lend it another dol... |
| 3915 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 3918 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 4214 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 4217 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 4220 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 4311 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point ceiling and the 3.5-p... |
| 4405 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 4408 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 4611 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into an issue on the las... |
| 4692 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit at the target (lend... |
| 5098 | `Bank.BRANCH_CLOSE_MONTHS` | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | Closed months in a row the book must fail to keep its branches' staff before the bank closes one: BusinessInvestment.DISTRESS_LOSS_MONTHS, the two years a landlord or a maker loses money before it sells plant it is us... |
| 5816 | `Bank.SHEET_ASSETS` | `{ Sheet.RESERVES, Sheet.BUSINESS_LOANS, Sheet.INTERIM, Sheet.MORTGAGES, Sheet...` | The asset lines, in the page's order: they sum to totalAssets(). |
| 5820 | `Bank.SHEET_LIABILITIES` | `{ Sheet.DEPOSIT_FUNDING, Sheet.WINDOW, Sheet.FOREIGN_DEPOSITS, Sheet.DESK_SHO...` | ...and the liability lines: they sum to totalLiabilities(). |

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
| 1461 | `private double securities` | The desk's inventory, at the exchange's mark. |
| 1469 | `private double tradingIncome` | The trading result so far this month: cash from what it sold less cash for what it bought, plus the change in what it holds at the mark, plus the dividends its inventory was paid. |
| 1481 | `private double markChange` | The part of the trading result that is the re-mark: every change in what the inventory is carried at this month, summed. |
| 1645 | `private double foreignDeposits` |  |
| 1683 | `private double hotMoneyIn, hotMoneyOut` |  |
| 1755 | `private boolean inResolution` | Failed, and not yet put back on its feet. |
| 1756 | `private int failures` |  |
| 1765 | `private double shortfallThisMonth, shortfallLifetime` | THE HOLE WHEN IT FAILED (0.7.14): its equity below nothing the month it went under, this month's and over its life - what the city's resolution then pays, with the capital to reopen. |
| 1784 | `private double resolutionLossThisMonth` | ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. |
| 1785 | `private double resolutionLossLifetime` |  |
| 2011 | `private double domesticCapitalScale` | The same, in today's money. |
| 2089 | `private double dividendsPaid` |  |
| 2248 | `double par` |  |
| 2249 | `double arrears` |  |
| 2250 | `int issued` |  |
| 2251 | `double capPerShare` |  |
| 2252 | `double warrantShares` |  |
| 2253 | `double strike` |  |
| 2254 | `int warrantsExpire` |  |
| 2255 | `boolean warrantsOut` |  |
| 2278 | `private final java.util.List<Preferred> preferred` |  |
| 2282 | `private int month` | The city's month, for the preferred's anniversaries: set by Game at the top of each month and on load. |
| 2285 | `private double preferredIn, preferredOut, preferredDividendsThisMonth, preferredAccruedThisMonth` | The month's causes (0.7.14), cleared at startMonth(), saved with the month's lines. |
| 2286 | `private double warrantsBoughtBackThisMonth, ownersWiped, preferredCancelled` |  |
| 2288 | `private double preferredDividendsLifetime, preferredRedeemedLifetime, warrantsBoughtBackLifetime` | ...and over its life. |
| 2291 | `private double repaymentRaisedThisMonth, repaymentRaisedLifetime` | What its offerings of new common raised to repay the city, this month and over its life, and the months a repayment - a due block, or the warrants - was left part-paid because the public did not take the whole. |
| 2292 | `private int repaymentShortLifetime` |  |
| 2661 | `private double capitalInjected` |  |
| 2662 | `private double capitalFromHome` |  |
| 2663 | `private double bailoutReceived` |  |
| 2664 | `private double foundingSettlement` |  |
| 2736 | `private double branchesCapitalised` |  |
| 2767 | `private double internalInterest` |  |
| 2844 | `private double paperGains` | The gain or loss on the city's paper that changed hands this month. |
| 2847 | `private double paperBoughtFromHouseholds` | What the desk paid households for their paper this month: money leaving the pools for a household, which MoneyAudit declares. |
| 2850 | `private double paperSoldToCentralBank, paperBoughtFromCentralBank` | What the central bank paid it for paper this month, and what it paid the central bank. |
| 2864 | `private double unearnedDiscount` | THE UNEARNED DISCOUNT (0.7.1): the part of what the bank paid under face for the city's paper it has not yet accreted into income, carried as a liability against the book - so equity does not jump by the discount the ... |
| 2916 | `private double buybackGains` | Gains less losses on paper the treasury bought back, over the city's life. |
| 3016 | `private double accountFeeBase` | ACCOUNT_FEE in today's unit - reseeded and reformed with the other money constants. |
| 3025 | `private double accountFees, loanFeesPaid, loanFeesOwed` | The month's fees: accounts, loans paid in cash (a business's), and loans added to what is owed (a household's). |
| 3127 | `private double depositRate` | PAYING FOR DEPOSITS is the other half of what a bank IS. |
| 3128 | `private double depositInterestToHouseholds` |  |
| 3129 | `private double depositInterestToSectors` |  |
| 3139 | `private double depositInterestToForeign` | ...and what the hot money is paid for parking here. |
| 3147 | `private boolean depositPayoutHeld` |  |
| 3174 | `private double fundingRate` | THE PRICE OF WHOLESALE MONEY WAS TWO DIALS until 0.7.0: FUNDING_SPREAD, two points over the risk-free rate "for being a bank rather than a treasury", and FUNDING_STRETCH, six points more at a reach of one deposit book... |
| 3426 | `private double chosenDepositRate` | The rate the bank chose this month, before rule 2 asked whether its margin could pay it. |
| 3464 | `private int monthsPayoutHeld` | HOW OFTEN RULE 2 HELD THE SAVERS UNDER THE CHOSEN RATE - counted for the run, not saved, for the reason the bid-up's counter was: a rule that never binds looks exactly like one that does not exist. |
| 3611 | `private double taxPaid` | Jerus: "quick question banks are taxed right?" |
| 3612 | `private double profitLastMonth` |  |
| 3684 | `private double struckProfit, carriedLate, restoredLate` | PROFIT THAT LANDS AFTER THE CLOSE (0.7.7). |
| 3685 | `private boolean closedThisMonth` |  |
| 3720 | `private double lastPayroll, lastUpkeep, lastInterest, lastBook` |  |
| 3723 | `private double lastKept` | Last month's interest margin and fees - what its book KEPT, which the branch test asks of a counter since 0.7.7. |
| 4050 | `private final java.util.Map<String, Double> sectorAllowance` | Each sector's allowance by name, what each held when the month opened, and what each was written off by this month. |
| 4051 | `private final java.util.Map<String, Double> openingSectorAllowance` |  |
| 4052 | `private final java.util.Map<String, Double> writtenOffBySector` |  |
| 4054 | `private final java.util.Set<String> sectorsWatched` | The sectors whose books are in stage 2, as the last provide() found them. |
| 4056 | `private final java.util.Map<String, Double> stageTwoShares` | ...and the share of each sector's book in stage 2, firm by firm (0.7.8). |
| 4059 | `private double householdAllowance, openingHouseholdAllowance, householdWrittenOff, householdWatchedDebt` | The families' allowance, the same three, and how much of their debt is in cells in trouble. |
| 4062 | `private double openingAllowance, allowanceUsed` | The whole allowance as the month opened, and how much of the month's write-offs it covered. |
| 4174 | `private final java.util.Map<String, double[]> allowanceReadings` |  |
| 4223 | `private final double[] lossRing` | The last year's provisions and weighted book, a ring of the months the bank had a branch. |
| 4224 | `private final double[] riskRing` |  |
| 4225 | `private int lossMonths` |  |
| 4227 | `private double worstLossRate` | The worst year's provisions over its average weighted book it has recorded: the loss the buffer is sized to take. |
| 4519 | `private double payoutProfit, payoutExcess, payoutOverTarget` | What the month's payout read: the profit after tax it was paid on, what the bank held over the top of its band, and over its target. |
| 4555 | `private double sharesBoughtBack, sharesIssued` | THE DESK DEALS IN THE BANK'S OWN SHARES BY ITS CAPITAL RULE (0.7.8): it buys them back from whoever sells while the bank is at or over its own target (buysBackOwnShares()) and only with what it holds over it (buybackR... |
| 4675 | `private final double[] dividendRing` | The owners' year: dividends and buybacks, a ring of months with this one in it. |
| 4676 | `private final double[] buybackRing` |  |
| 4677 | `private int payoutMonths` |  |
| 5101 | `private int uncoveredMonths` | Closed months in a row the book has not kept its branches' staff (branchesCoverTheirStaff()); struck at closeMonth(), carried in lastMonthToSave(). |
| 5537 | `private double interestFromBusinesses, interestFromCity, interestFromHouseholds, discountAccreted` | The month's interest by who paid it: the businesses, the city's coupons, the families' credit lines, and the discount on the city's paper as it is earned. |
| 5571 | `private double treasuryBuybackGain` | What the treasury buying its paper back gained the bank (negative: lost it) since the month opened. |
| 5580 | `private double allowanceOpened` | The allowance a save from before 0.7.8 was given on load (openAllowance()): its equity fell by it between two presses. |
| 5588 | `private double bailoutsLifetime` | What the city has put into it in rescues over its life - resolutions since 0.7.14 (takeResolutionCapital()), gifts before - carried in the solvency record since 0.7.9; a save from before counts from its load. |
| 5597 | `private boolean monthKnown` | True once the month's lines are a month's: one played, or lines a save carried. |
| 5684 | `private final double[][] statementRing` | The months before this one, a year of them less this one: each filed whole at the top of the month after (startMonth()), when everything booked after its close is in it. |
| 5685 | `private int statementsFiled` |  |
| 5899 | `private final double[][] sheetRing` | The sheet at the top of each of the last YEAR_MONTHS months - every line, then the loans and the interim financing by sector - filed at startMonth() before anything moves, so the oldest is the sheet exactly a year bef... |
| 5900 | `private int sheetsFiled` |  |
| 6255 | `private double paidInOpening, retainedOpening` | ITS EQUITY, IN TWO PARTS (0.7.13, round 2). |
| 6256 | `private boolean splitKnown` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 6253 | **type** `public class Bank` | The city's commercial bank: every loan in it, and every default. |

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

### what the four parts remember (lines 1181-1411)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 16 | `private void strikePrices()` | Adds the month that has just closed to the rings and strikes the two parts that read flows. |
| 1214 | 10 | `public double[] pricingHistoryToSave()` | The record, for the save: the count, then the two cost rings. |
| 1226 | 9 | `public void restorePricingHistory(double[] in)` | ...and back. |
| 1279 | 1 | `public double marginalCostOfFunds()` | WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND. |
| 1301 | 8 | `private double blendedCostOfFunds()` | The two tranches, blended at the weights the bank actually used them. |
| 1311 | 100 | `public void startMonth()` | Clears the month's flows. |

### the arithmetic (lines 1412-1441)

| line | len | member | says |
|---:|---:|---|---|
| 1424 | 1 | `public double getBook()` | Everything lent, carry included. |
| 1433 | 8 | `public double getWeightedBook()` | The book as CAPACITY sees it: risk- and maturity-weighted. |

### THE TRADING DESK (lines 1442-1519)

| line | len | member | says |
|---:|---:|---|---|
| 1484 | 5 | `public void markSecurities(double value)` | The exchange re-marks the inventory. |
| 1491 | 5 | `public void deskPays(double cash)` | The desk paid cash for shares. |
| 1498 | 5 | `public void deskReceives(double cash)` | ...and was paid for shares it sold. |
| 1505 | 5 | `public void receiveDividend(double amount)` | Dividends on the inventory: income, and cash. |
| 1512 | 1 | `public void restoreSecurities(double value)` | The load path puts the mark back without calling it income. |
| 1514 | 1 | `public double getSecurities()` |  |
| 1515 | 1 | `public double getTradingIncome()` |  |
| 1518 | 1 | `public double getMarkChange()` | What re-marking the inventory did to this month's trading result. |

### the desk held to its capital (0.7.8) (lines 1520-2139)

| line | len | member | says |
|---:|---:|---|---|
| 1580 | 18 | `public double deskCanCarry(double inventory, double price, double mark)` | HOW MANY OF A COMPANY'S SHARES THE DESK MAY STILL BUY ON THE CAPITAL IT HAS: as many as leave the bank at or over its target (targetEquity()) with them on its books - the shares carried at the weight the weighted book... |
| 1600 | 4 | `public double weightingRelief()` | How much lighter the weighting makes the book. |
| 1614 | 12 | `public double capacity()` | The most the bank can lend before it is funding itself at the central bank's window (abroad, until 0.7.0). |
| 1639 | 5 | `public double depositsGathered()` | What the branches let it gather, out of what the city has to bank - PLUS whatever the world has parked here, which needed no branch at all. |
| 1648 | 1 | `public double getForeignDeposits()` | Hot money currently funding this bank, in local money. |
| 1651 | 4 | `public void setForeignDeposits(double amount)` | Told by Game each month, from CapitalFlows. |
| 1663 | 5 | `public void receiveHotMoney(double amount)` | Hot money arriving, as cash. |
| 1677 | 5 | `public void returnHotMoney(double amount)` | ...and leaving, which is the whole danger. |
| 1685 | 1 | `public double getHotMoneyIn()` |  |
| 1686 | 1 | `public double getHotMoneyOut()` |  |
| 1700 | 4 | `public double hotFundingShare()` | The share of the bank's funding that could leave at any time. |
| 1706 | 3 | `public double capitalLimit()` | What its capital supports, on the risk-weighted book - at the book's present mix, under the larger of the two requirements since round 2 of 0.7.11 (weightedBookSupportedBy()). |
| 1721 | 5 | `private double weightedBookSupportedBy(double equity)` | THE WEIGHTED BOOK THIS MUCH EQUITY SUPPORTS: equity over CAPITAL_RATIO while the risk-based minimum is the larger. |
| 1728 | 3 | `public boolean capitalBound()` | True when it is the capital that binds rather than the funding. |
| 1738 | 4 | `public double capitalRatio()` | Equity against the risk-weighted book - the ratio a regulator reads. |
| 1768 | 1 | `public double getShortfallThisMonth()` | The hole it failed with this month; nothing in a month it did not fail. |
| 1770 | 1 | `public double getShortfallLifetime()` | ...and over its life. |
| 1772 | 1 | `public boolean isInResolution()` | True while it waits for the city: failed, frozen, not yet resolved. |
| 1787 | 1 | `public boolean isInsolvent()` |  |
| 1789 | 1 | `public double getResolutionLoss()` | Over the city's life. |
| 1791 | 1 | `public double getResolutionLossThisMonth()` | This month only. |
| 1792 | 1 | `public int getFailures()` |  |
| 1835 | 6 | `public double[] solvencyToSave()` | The solvency record, for the save. |
| 1842 | 10 | `public void restoreSolvency(double[] state)` |  |
| 1879 | 26 | `public void resolveIfFailed()` | The bank fails: it is frozen, and it waits for the city. |
| 1915 | 4 | `private double exitTolerance(double moved)` | How near its exit level a frozen bank's equity must be to count as there: a hair of the largest figure that struck it - its balance sheet, or the capital just paid in - what those sums round by. |
| 1931 | 23 | `public double resolutionExitEquity()` | The equity at which the freeze lifts, however the bank gets there. |
| 1979 | 9 | `public double recapitalisationNeeded()` | What it would take to put the bank back on its feet. |
| 2014 | 5 | `public double domesticCapitalShare()` | The share of new paid-in capital the city's own savers can find. |
| 2043 | 13 | `public void injectCapital(double amount)` | Shareholders' money, put in as capital - and WHOSE shareholders. |
| 2067 | 7 | `public void injectCapital(double fromHome, double fromAbroad)` | Shareholders' money, put in as capital, and whose: the households' part came out of their savings through the register, the rest from the world. |
| 2082 | 6 | `public void payDividend(double amount)` | Pays the owners. |
| 2092 | 1 | `public double getDividendsPaid()` | What it paid its shareholders this month. |
| 2129 | 10 | `public void takeResolutionCapital(double amount)` | The city's capital in a resolution (0.7.14): the treasury's money, for every common share (Game.resolveBank()). |

### THE CITY'S CAPITAL (0.7.14): A RESOLUTION, AND THE PREFERRED (lines 2140-2753)

| line | len | member | says |
|---:|---:|---|---|
| 2247 | 30 | **type** `public static final class Preferred` | One block of the city's senior preferred, as it was bought: what is outstanding at par, the dividends accrued on it and unpaid, the month it was issued, the common dividend a share a month it may not rise past while t... |
| 2257 | 1 | `Preferred()` _(in Bank.Preferred)_ |  |
| 2259 | 1 | `public double par()` _(in Bank.Preferred)_ |  |
| 2260 | 1 | `public double arrears()` _(in Bank.Preferred)_ |  |
| 2261 | 1 | `public int issued()` _(in Bank.Preferred)_ |  |
| 2262 | 1 | `public double capPerShare()` _(in Bank.Preferred)_ |  |
| 2263 | 1 | `public double warrantShares()` _(in Bank.Preferred)_ |  |
| 2264 | 1 | `public double strike()` _(in Bank.Preferred)_ |  |
| 2265 | 1 | `public int warrantsExpire()` _(in Bank.Preferred)_ |  |
| 2266 | 1 | `public boolean warrantsOut()` _(in Bank.Preferred)_ |  |
| 2269 | 1 | `public double rate(int month)` _(in Bank.Preferred)_ | Its dividend rate this month: PREFERRED_RATE, PREFERRED_STEP_RATE from the fifth anniversary. |
| 2272 | 1 | `public boolean inConsent(int month)` _(in Bank.Preferred)_ | True while the city's consent binds: outstanding, and under three years old. |
| 2275 | 1 | `public boolean due(int month)` _(in Bank.Preferred)_ | True once it is due to be redeemed: outstanding, at or past its third anniversary. |
| 2295 | 1 | `public void setMonth(int month)` | The city's month, for the preferred's anniversaries. |
| 2296 | 1 | `public int getMonth()` |  |
| 2299 | 5 | `public double preferredOutstanding()` | The city's preferred outstanding at par, every block: an equity line of its own ("Preferred shares (the city)"). |
| 2306 | 1 | `public java.util.List<Preferred> getPreferred()` | Every block, oldest first. |
| 2309 | 5 | `public double getPreferredArrears()` | The preferred dividends owed and unpaid, every block together: not a liability on the sheet until paid, as preferred arrears are not. |
| 2316 | 4 | `public boolean inConsentPeriod()` | True while any block's consent binds: no buyback, no rise in the common dividend a share. |
| 2322 | 5 | `public boolean wantsPreferred()` | True when a standing bank is under its minimum and would ask the city for preferred. |
| 2329 | 1 | `private double preferredNeed()` | What it takes back to its target: recapitalisationNeeded() for a standing bank under its minimum. |
| 2337 | 6 | `public double preferredOfferSize()` | WHAT IT ASKS THE CITY FOR: what takes it back to its target, held between PREFERRED_MIN_SHARE and PREFERRED_MAX_SHARE of its risk-weighted assets (getWeightedBook()). |
| 2345 | 3 | `public boolean preferredOfferCapped()` | True when 3% of its risk-weighted assets does not reach its target: it asks for 3%, and the rest is left to its own share issues. |
| 2350 | 3 | `public double preferredOfferShortOfTarget()` | ...and what the 3% leaves short of the target. |
| 2364 | 16 | `public double issuePreferred(double par, double price, double capPerShare)` | THE CITY BUYS ITS PREFERRED: cash in at par, an equity line of its own, warrants on WARRANT_SHARE of the par at `price` - last month's price of a share, which is their strike - for WARRANT_TERM_MONTHS. |
| 2402 | 24 | `public double payPreferredDividends()` | THE MONTH'S PREFERRED DIVIDEND: every block accrues a twelfth of its rate on its par, into its own arrears, and the bank pays what it can of them, oldest block first - standing, and only from what it holds over its ta... |
| 2458 | 33 | `public double[] redeemDuePreferred(java.util.function.DoubleUnaryOperator offering)` | THE BLOCKS AT THEIR THIRD ANNIVERSARY ARE REDEEMED WHOLE (0.7.14; Jerus: "Sell new shares to repay"), oldest first, at the term sheet's price - "All redemptions of the Senior Preferred shall be at 100% of its issue pr... |
| 2510 | 31 | `public double repurchaseWarrants(double price, double sigma, double riskFree, java.util.function.DoubleUnaryOperator offering)` | THE CITY'S WARRANTS BOUGHT BACK, once no preferred is left: every warrant still out, at its fair value (warrantValue(), Black-Scholes) - the term sheet's "Following the redemption in whole of the Senior Preferred held... |
| 2543 | 1 | `public double getRepaymentRaisedThisMonth()` | What the offerings to repay the city raised this month, and over the bank's life. |
| 2544 | 1 | `public double getRepaymentRaisedLifetime()` |  |
| 2546 | 1 | `public int getRepaymentShortLifetime()` | How often a repayment was left part-paid, the public not taking the whole offering: once a month for each due block short, and once for the warrants. |
| 2557 | 12 | `public double exerciseExpiredWarrants(double price)` | WARRANTS AT THEIR EXPIRY, still out: exercised if in the money, cashless - the city takes new shares worth what the warrants are over the strike, warrantShares x (price - strike) / price - and gone if not. |
| 2571 | 5 | `private double warrantValue(Preferred p, double price, double sigma, double riskFree)` | What one block's warrants are worth: Black-Scholes on the shares they reach, for the term they have left. |
| 2578 | 5 | `public double warrantValue(double price, double sigma, double riskFree)` | Every block's warrants still out, at their value: the city's fund's mark on them. |
| 2585 | 5 | `public double warrantSharesOut()` | How many shares the warrants still out reach. |
| 2598 | 6 | `public double[] cancelPreferred()` | THE HOLE TOOK THEM: every block of preferred and its warrants cancelled at a failure - they were capital. |
| 2612 | 5 | `public void wipeOwners()` | THE OLD OWNERS ARE WIPED OUT: their paid-in capital is written off against the losses, before the city's capital goes in - losses fall on common shareholders first (CDIC). |
| 2619 | 8 | `public void splitShares(double k)` | A split or consolidation of its shares: the dividend cap a share and the warrants' strike by the inverse, their count by the factor. |
| 2628 | 1 | `public double getPreferredIn()` |  |
| 2629 | 1 | `public double getPreferredRedeemedThisMonth()` |  |
| 2630 | 1 | `public double getPreferredDividendsThisMonth()` |  |
| 2631 | 1 | `public double getPreferredAccruedThisMonth()` |  |
| 2632 | 1 | `public double getWarrantsBoughtBackThisMonth()` |  |
| 2633 | 1 | `public double getOwnersWipedThisMonth()` |  |
| 2634 | 1 | `public double getPreferredCancelledThisMonth()` |  |
| 2635 | 1 | `public double getPreferredDividendsLifetime()` |  |
| 2636 | 1 | `public double getPreferredRedeemedLifetime()` |  |
| 2637 | 1 | `public double getWarrantsBoughtBackLifetime()` |  |
| 2640 | 4 | `public double[] preferredRecordToSave()` | The preferred's record, for the save: the arrears (the blocks carry their own; this is their sum, for reading), the three lifetime figures and what its offerings to repay raised. |
| 2646 | 14 | `public void restorePreferred(java.util.List<Preferred> blocks, double[] record)` | ...and back, with the blocks. |
| 2670 | 1 | `public double getFoundingSettlement()` | The one-off jump in the bank's cash when the city's first branch opens and it takes over the standing loan book. |
| 2673 | 1 | `public double getCapitalInjected()` | Shareholders' money from OUTSIDE the city. |
| 2682 | 1 | `public double getCapitalFromHome()` | ...and from the city's own savers. |
| 2685 | 1 | `public double getBailoutReceived()` | The treasury's money - a resolution's since 0.7.14, a gift before - from inside it. |
| 2699 | 36 | `public double openBranches(double nowStanding)` | Opens whatever branches have been built since last month, and says what capital they need. |
| 2737 | 1 | `public double getBranchesCapitalised()` |  |
| 2738 | 1 | `public void setBranchesCapitalised(double n)` |  |
| 2748 | 5 | `public double strain()` | Book over capacity. |

### the flows (lines 2754-2828)

| line | len | member | says |
|---:|---:|---|---|
| 2760 | 6 | `public void takeInterest(double amount)` | Interest arriving from a SECTOR or the CITY - both inside the audit, so this is a transfer between pools and declares nothing. |
| 2770 | 1 | `public double getInternalInterest()` | The part of the month's interest that came from inside the city's pools. |
| 2779 | 6 | `public void takeDiscount(double amount)` | Paper bought below par: income the bank earns without any cash moving. |
| 2804 | 3 | `public double sellPaperBack(double price, double principal)` | THE TREASURY BUYS ITS PAPER BACK FROM THE BANK (0.7.0), which is who holds it: the price arrives as cash, the book drops by the principal at once rather than at the next refresh, and the difference is the bank's gain ... |
| 2815 | 13 | `public double sellPaperBack(double price, double principal, double unearned)` | ...and since 0.7.1 at amortised cost: the unearned discount riding on the face that leaves goes with it, so the gain is the price less what the book carried the paper at - face less the discount not yet earned. |

### THE CITY'S PAPER CHANGES HANDS (0.7.1) (lines 2829-2982)

| line | len | member | says |
|---:|---:|---|---|
| 2852 | 1 | `public double getPaperSoldToCentralBank()` |  |
| 2853 | 1 | `public double getPaperBoughtFromCentralBank()` |  |
| 2866 | 1 | `public void setUnearnedDiscount(double amount)` |  |
| 2867 | 1 | `public double getUnearnedDiscount()` |  |
| 2868 | 1 | `public double getPaperGains()` |  |
| 2869 | 1 | `public double getPaperBoughtFromHouseholds()` |  |
| 2871 | 6 | `private void addToCityBook(double face)` |  |
| 2879 | 10 | `public double buyPaperFromHouseholds(double price, double face, double unearned)` | A household sells this face to the desk for this price. |
| 2891 | 11 | `public double sellPaperToCentralBank(double price, double face, double unearned)` | The central bank buys this face from the bank's book, in money it made. |
| 2904 | 10 | `public double buyPaperFromCentralBank(double price, double face, double unearned)` | ...and sells it back: the bank pays, and the face returns to its book. |
| 2917 | 1 | `public double getBuybackGains()` |  |
| 2920 | 4 | `public void takeRepayment(double amount)` | Principal coming back. |
| 2926 | 4 | `public void lend(double amount)` | Money out the door. |
| 2932 | 5 | `public void lendToHouseholds(double amount)` | Lending to a family, which crosses the audit's boundary and is counted. |
| 2939 | 4 | `public void takeFromHouseholds(double principal, double interest)` | ...and from a household, which is outside, so both halves are declared. |
| 2952 | 4 | `public void writeOff(double amount)` | A loan that will not be repaid. |
| 2963 | 5 | `public void writeOffSector(String sector, double amount)` | ...on a business's book, by name (0.7.8), so the month's provide() can draw it against the allowance that sector's book opened the month holding. |
| 2970 | 5 | `public void writeOffHouseholds(double amount)` | ...and on the families' book, the debts of those discharged this month. |
| 2977 | 5 | `public void payRunning(double payroll, double upkeep)` | Wages and running costs, which are real money leaving. |

### FEES (0.7.7) (lines 2983-3050)

| line | len | member | says |
|---:|---:|---|---|
| 3019 | 4 | `public double accountFee(double priceIndex)` | A month's account fee per housed household at this price index, in today's money - nothing in a city with no branch, which has no bank to hold an account at. |
| 3028 | 5 | `public void takeAccountFees(double amount)` | The households' account fees, in cash from outside the pools. |
| 3035 | 5 | `public void takeLoanFees(double amount)` | Loan fees a business paid out of its proceeds: cash from another pool. |
| 3042 | 4 | `public void bookLoanFees(double amount)` | Loan fees added to what the households owe: income now, no cash until they repay, and the book carries them from the next refresh. |
| 3047 | 1 | `public double getAccountFees()` |  |
| 3048 | 1 | `public double getLoanFeesPaid()` |  |
| 3049 | 1 | `public double getLoanFeesOwed()` |  |

### THE FUNDING SIDE (lines 3051-3339)

| line | len | member | says |
|---:|---:|---|---|
| 3142 | 1 | `public double depositRate()` | The rate savers are being paid, a year: the month's payout over the deposits - the rate the bank chose, unless its interest margin could not pay it (isDepositPayoutHeld()). |
| 3145 | 1 | `public boolean isDepositPayoutHeld()` | True when rule 2 of WHAT TO PAY SAVERS held the savers under the rate the bank chose - its margin, after its running costs or at the savers' share, could not pay it. |
| 3149 | 1 | `public double getDepositInterestToHouseholds()` |  |
| 3150 | 1 | `public double getDepositInterestToSectors()` |  |
| 3151 | 1 | `public double getDepositInterestToForeign()` |  |
| 3152 | 4 | `public double depositInterest()` |  |
| 3158 | 4 | `public double netInterestMargin()` | What it charges borrowers, less what it pays savers. |
| 3177 | 1 | `public double borrowings()` | Everything it owes: the mirror of a negative cash position. |
| 3187 | 1 | `public double depositFunding()` | The cheap tranche - the city's own money, lent back out. |
| 3190 | 1 | `public double wholesaleFunding()` | ...and the part it had to go to the window for. |
| 3197 | 1 | `public double fundingRate()` | What the bank is paying for the money it did not have: the window's rate, policy plus CentralBank.WINDOW_PENALTY. |
| 3210 | 129 | `public void fundToCover(double policyAnnual)` | Settles the funding for the month: charge for what was borrowed, then borrow what is short or repay what is spare. |

### WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. (lines 3340-3471)

| line | len | member | says |
|---:|---:|---|---|
| 3411 | 7 | `public double fundingPosition()` | How the bank is funded, 0 to 1: 0 while it holds reserves (its cash is positive), 1 once it is borrowing at the window, and in between the share of what its branches gathered that it has lent out. |
| 3420 | 4 | `public double depositShare()` | The share of the policy rate the bank's funding asks it to pass on: between DEPOSIT_SHARE_FLUSH and DEPOSIT_SHARE_AT_WINDOW, by fundingPosition(). |
| 3427 | 1 | `public double getChosenDepositRate()` |  |
| 3439 | 23 | `private double chooseDepositRate(double policyAnnual)` | The month's deposit interest, in money. |
| 3465 | 1 | `public int getMonthsPayoutHeld()` |  |
| 3468 | 1 | `public double getFundingCost()` | What the window charged this month, paid to the central bank. |
| 3470 | 1 | `public double getPlacementIncome()` | What its reserves earned at the central bank this month, at the policy rate. |

### THE THREE STATEMENTS (lines 3472-3583)

| line | len | member | says |
|---:|---:|---|---|
| 3495 | 1 | `public double cashReserves()` | Cash it is actually sitting on. |
| 3502 | 1 | `public double totalAssets()` | Total assets: the loan book net of what it has set aside against it (netLoans(), since 0.7.8), whatever cash it has not lent, and what the desk holds. |
| 3505 | 1 | `public double netLoans()` | The loans as the balance sheet carries them (0.7.8): what is owed, less the allowance for what will not come back. |
| 3508 | 1 | `public double shortSecurities()` | ...and a desk that is short owes the shares: a liability at the mark. |
| 3535 | 3 | `public double totalLiabilities()` | What the bank owes: what it borrowed to fund its book (past its deposits, at the central bank's window since 0.7.0), and the hot money. |
| 3547 | 1 | `public double equity()` | The residual - and, once the two above are written out, simply the book plus the cash position. |
| 3550 | 1 | `public double getOpeningEquity()` | Equity as it stood at the top of the month. |
| 3555 | 1 | `public double interestIncome()` | What every borrower paid it this month. |
| 3558 | 3 | `public double netInterestIncome()` | ...less what it paid savers and what it paid the window. |
| 3563 | 1 | `public double feeIncome()` | ...plus its fees, since 0.7.7: the accounts, and the loans written. |
| 3573 | 1 | `public double afterLosses()` | ...less the provision for the loans that will not come back (0.7.8): what the allowance rose by, and whatever the month wrote off that it had not already set aside - provisions(). |
| 3576 | 1 | `public double operatingExpenses()` | ...less the tellers and the lights. |
| 3579 | 1 | `public double afterTrading()` | ...plus what the desk made or lost. |
| 3582 | 1 | `public double profitBeforeTax()` | What it made before the city took its share. |

### tax (lines 3584-3700)

| line | len | member | says |
|---:|---:|---|---|
| 3627 | 1 | `public double getProfitLastMonth()` | Profit the tax is charged on: the month that has just finished - and, since 0.7.7, what the month before it earned after its own close (getCarriedLate(); see PROFIT THAT LANDS AFTER THE CLOSE). |
| 3630 | 46 | `public void closeMonth()` | Called at the end of the month, once the profit is final. |
| 3688 | 3 | `public double lateProfit()` | What this month earned after its close: the part of its profit next month's tax and dividend will carry. |
| 3693 | 1 | `public double getCarriedLate()` | What last month earned after its close, inside getProfitLastMonth(). |
| 3696 | 4 | `public void restoreLateProfit(double value)` | The load path: what the saved month earned after its close. |

### LAST MONTH, KEPT ON PURPOSE (lines 3701-3829)

| line | len | member | says |
|---:|---:|---|---|
| 3725 | 10 | `public double[] lastMonthToSave()` |  |
| 3759 | 20 | `public void restoreLastMonth(double[] state)` | ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the cost-of-funds floor. |
| 3781 | 1 | `public void setProfitLastMonth(double value)` | Restored from the save, so next month taxes the right figure. |
| 3793 | 1 | `public void setDepositRate(double rate)` | What savers were paid in the month this save was taken in. |
| 3796 | 1 | `public double getTaxPaid()` | What the city took this month. |
| 3809 | 5 | `public double chargeTax(double annualProfitRate)` | Hands the city its share of last month's profit. |
| 3816 | 3 | `private static double taxOn(double profit, double profitTaxRate)` | The city's share of a month's profit at this rate: nothing on a loss. |
| 3826 | 3 | `public double getProfitAfterTaxLastMonth(double profitTaxRate)` | Last month's profit AFTER the tax it will be charged at this rate: what the owners are paid a share of (Game.payDividends()) and what the register records (Equity.recordMonth()), as every sector's own net income is. |

### THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 3830-3908)

### the allowance (lines 3909-4210)

| line | len | member | says |
|---:|---:|---|---|
| 3921 | 3 | `public static double lossIfDefaulted(double principal, double assets)` | What the BACKSTOP would cost the bank on a business owing this against these assets (BusinessDebtManager.restructure()): everything owed past BusinessDebtManager.RESTRUCTURE_TARGET of its assets - all of it, for a sec... |
| 3926 | 4 | `public static boolean sectorWatched(double principal, double assets)` | A business borrower in trouble: owing past SECTOR_WATCH_LEVERAGE of its assets, or anything at all against none. |
| 3939 | 6 | `public static double stageTwoShare(double principal, double assets)` | The share of a sector's firms, by what they owe, past the watch line: the curve's spread of fortunes (BusinessDebtManager.ASSET_VOLATILITY) read at SECTOR_WATCH_LEVERAGE instead of the default point, N(ln(L / SECTOR_W... |
| 3977 | 3 | `public static double sectorAllowance(double principal, double assets)` | The allowance a business's book holds, read off the curve its firms default on (0.7.8) - the same PD(L) that writes the month's slice off (BusinessDebtManager.defaultProbability()), so the allowance is what the slices... |
| 3990 | 3 | `public static double sectorAllowance(double owed, double principal, double assets)` | ...ON WHAT IT OWES NOW, READ AT ITS QUARTER (0.7.8): the curve read at principal over assets - the averages of its last quarter's readings (BusinessDebtManager.quarterPrincipal(), quarterAssets()) - and the loss struc... |
| 4003 | 13 | `public static double sectorAllowance(double owed, double principal, double assets, double lossGivenDefault)` | ...AT A LOSS GIVEN DEFAULT OF ITS OWN (0.7.12): a loan's for what the sector owes the bank, a bond's for the sector's bonds it holds (BusinessDebtManager, RECOVERIES BY INSTRUMENT, since round 2; round 1 read the two ... |
| 4024 | 3 | `private static double floorAt(double lgd)` | THE FLOOR IS THE SOUND BOOK'S LOSS, at this loss given default (0.7.12): BASE_LOSS_RATE is a sound LOAN's loss, so at a loan's loss given default it is BASE_LOSS_RATE, and a dollar that loses more when it defaults - a... |
| 4029 | 3 | `public static boolean householdWatched(double monthsOwed)` | A household cell in trouble: owing past HOUSEHOLD_WATCH_MONTHS of its income. |
| 4040 | 8 | `public static double householdAllowance(double debt, double monthsOwed)` | The allowance a household cell's debt holds: its year's expected loss while sound; once it is in trouble its whole debt - a discharge writes all of it off (Household.discharge()) - scaled from nothing at HOUSEHOLD_WAT... |
| 4082 | 17 | `public void provide(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | THE MONTH'S PROVISION: sets every book's allowance from its borrowers as they stand now, and draws the month's write-offs against what each book held when the month opened. |
| 4107 | 11 | `public void openAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | ...and a bank that has never held one: the allowance its borrowers call for, set up WITHOUT a provision - the month it opens on holds it from its start. |
| 4119 | 27 | `private void strikeAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` |  |
| 4148 | 5 | `public double getAllowance()` | Everything set aside against the book. |
| 4155 | 1 | `public double getSectorAllowance()` | ...against the businesses' book, all of it. |
| 4157 | 1 | `public double getSectorAllowance(String sector)` | ...against one sector's. |
| 4170 | 4 | `public double[] getAllowanceReading(String sector)` | What the allowance on a sector was struck on, as Game.bankReadings() handed it over at the month's provision: {the quarter's principal, its assets, what it owes that nobody insures, the loans' loss given default, the ... |
| 4176 | 1 | `public double getWrittenOff(String sector)` | What this month wrote off one sector's book - its defaulted firms' slice, or the backstop (0.7.8: the Bank tab's "this month", beside the allowance). |
| 4178 | 1 | `public double getHouseholdAllowance()` | ...against the families'. |
| 4180 | 1 | `public int getStage(String sector)` | 2 when that sector's book is in trouble - most of its firms past the watch line - 1 when most are sound. |
| 4182 | 1 | `public double getStageTwoShare(String sector)` | The share of that sector's book in stage 2, as the last provide() struck it (stageTwoShare()). |
| 4184 | 1 | `public int getHouseholdStage()` | 2 when any family's line is in trouble, 1 when none is. |
| 4186 | 1 | `public double getHouseholdWatchedDebt()` | The families' debt in the cells that are in trouble. |
| 4188 | 1 | `public java.util.Set<String> getSectorsWatched()` | The sectors whose books are in stage 2. |
| 4190 | 1 | `public int getBooksWatched()` | How many of its books are in stage 2: each sector's, and the families' as one (0.7.9, the Bank tab's count of borrowers in trouble). |
| 4192 | 1 | `public double getOpeningAllowance()` | The allowance the month opened with. |
| 4200 | 1 | `public double provisions()` | THE PROVISION, the income statement's line: what the allowance rose by this month and whatever was written off that it had not set aside - which is the allowance's move plus every write-off. |
| 4203 | 1 | `public double getAllowanceUsed()` | The month's write-offs that the allowance had already set aside. |
| 4206 | 1 | `public double getWriteOffsBeyondAllowance()` | ...and the part it had not, which reached the statement the month it was written off. |
| 4209 | 1 | `public double getProvisionCharge()` | The part of the provision that went into the allowance: its rise less what the write-offs drew out of it. |

### what it holds (lines 4211-4321)

| line | len | member | says |
|---:|---:|---|---|
| 4230 | 10 | `private void recordLosses()` | Files the month that has just closed into the loss record. |
| 4257 | 8 | `public double trailingLossRate()` | The last twelve recorded months' provisions over their average weighted book - or over what its branches' founding capital is built to carry at the minimum (branches x paidInPerBranch / CAPITAL_RATIO), whichever is la... |
| 4267 | 1 | `public double getWorstLossRate()` | The worst year it has lived through, as the capital target reads it. |
| 4314 | 1 | `public double capitalBuffer()` | Its buffer over the minimum: the worst year it has recorded, never less than CONSERVATION_BUFFER nor more than MAX_BUFFER. |
| 4317 | 1 | `public double capitalTarget()` | THE TARGET it chooses: the city's minimum and its own buffer. |
| 4320 | 1 | `public double capitalTop()` | ...and the top of its band. |

### the leverage ratio (0.7.11, round 2) (lines 4322-4401)

| line | len | member | says |
|---:|---:|---|---|
| 4344 | 1 | `public double exposure()` | THE EXPOSURE MEASURE the leverage ratio is struck on: everything on its balance sheet at the value the sheet carries it at, whatever it weighs - totalAssets(). |
| 4347 | 4 | `public double leverageRatio()` | Equity over the exposure measure: the leverage ratio a regulator reads. |
| 4361 | 1 | `public double leverageTarget()` | ITS OWN LEVERAGE TARGET: LEVERAGE_RATIO_MIN scaled by the buffer it chose on the risk side - LEVERAGE_RATIO_MIN x capitalTarget() / CAPITAL_RATIO. |
| 4364 | 1 | `public double leverageTop()` | ...and the top of its band on the same measure: LEVERAGE_RATIO_MIN x capitalTop() / CAPITAL_RATIO. |
| 4381 | 3 | `public double minimumEquity()` | THE MINIMUM THE CITY REQUIRES, IN MONEY: the larger of the risk-based one, CAPITAL_RATIO of the weighted book, and the leverage one, LEVERAGE_RATIO_MIN of the exposure. |
| 4386 | 3 | `public boolean leverageBinds()` | True when the leverage requirement is the larger - when a bank's zero-weighted assets are what its capital is short against. |
| 4391 | 1 | `public double bindingRatio()` | Its capital as a ratio on the measure that binds: the leverage ratio when leverageBinds(), the risk-based capitalRatio() otherwise - the figure the Bank tab's bar and status read. |
| 4394 | 1 | `public double bindingMinimum()` | The minimum on the binding measure: LEVERAGE_RATIO_MIN or CAPITAL_RATIO. |
| 4397 | 1 | `public double bindingTarget()` | Its target on the binding measure: leverageTarget() or capitalTarget(). |
| 4400 | 1 | `public double bindingTop()` | ...and the top of its band on it: leverageTop() or capitalTop(). |

### what it does with profit (lines 4402-4523)

| line | len | member | says |
|---:|---:|---|---|
| 4411 | 1 | **type** `public enum Payout` | What the bank does with its profit, as its capital stands. |
| 4418 | 3 | `public double targetEquity()` | The equity its target calls for on the book it has: the larger of its target on the weighted book and its leverage target on the exposure (0.7.11, round 2 - minimumEquity() says why). |
| 4430 | 4 | `public double topEquity()` | The equity at the top of its band - and NEVER LESS THAN WHAT ITS STANDING BRANCHES WERE FOUNDED WITH, paidInPerBranch each: the capital a counter is opened with is what the running costs are already priced on (strikeP... |
| 4436 | 4 | `public double excessCapital()` | What it holds past the top of its band: what it returns, a twelfth a month. |
| 4442 | 9 | `public Payout payoutStance()` | Where its capital puts it, for the words and the rules. |
| 4453 | 10 | `public String payoutDecision()` | ...in words, for the Bank tab. |
| 4473 | 11 | `public double dividendDue(double profitAfterTax)` | WHAT IT PAYS ITS OWNERS this month, on last month's profit after tax. |
| 4492 | 3 | `public double payOwners(double profitAfterTax)` | Pays its owners what dividendDue() says, and keeps what the rule read - the profit it was paid on, the excess over the top and the room over the target - so the month's decision can be read back. |
| 4504 | 13 | `public double payOwners(double profitAfterTax, double sharesInIssue)` | ...and with the shares in issue, for the city's consent (0.7.14): while a block of its preferred is under three years old the common dividend a share does not rise past what it was the year before the city bought it (... |
| 4520 | 1 | `public double getPayoutProfit()` |  |
| 4521 | 1 | `public double getPayoutExcess()` |  |
| 4522 | 1 | `public double getPayoutOverTarget()` |  |

### its own shares (lines 4524-4688)

| line | len | member | says |
|---:|---:|---|---|
| 4558 | 6 | `public void buyBackOwnShares(double paid)` | The desk bought the bank's own shares back and cancelled them: cash out, equity down, no income. |
| 4566 | 5 | `public void issueOwnShares(double received)` | ...and issued new ones: cash in, equity up, no income. |
| 4577 | 7 | `public boolean buysBackOwnShares()` | True when the desk buys the bank's own shares back from whoever sells: standing, and at or over its own capital target. |
| 4605 | 4 | `public boolean issuesOwnShares()` | ...and when it issues new ones to whoever buys: standing, lending, and UNDER its own target - raising the capital its rule says it is short of, and never while it holds what it wants. |
| 4623 | 3 | `public double spareCapital(double inventory)` | WHAT IT HOLDS OVER ITS TARGET: its equity less targetEquity(), with the desk's inventory carried at `inventory` rather than at the securities line's last mark - the line lags the desk's deals within a month until the ... |
| 4628 | 6 | `private double spareOnRisk(double inventory)` | Its spare capital against its target on the weighted book, the inventory carried at `inventory`. |
| 4640 | 6 | `private double spareOnLeverage(double inventory)` | ...and against its leverage target on the exposure (0.7.11, round 2). |
| 4648 | 1 | `public double spareCapital()` | ...on the books as they stand: equity() less targetEquity(). |
| 4667 | 3 | `public double buybackRoom(double inventory)` | THE MOST IT MAY SPEND BUYING ITS OWN SHARES BACK NOW: what it holds over its target, spareCapital(inventory), so that no purchase takes it under the target - a month's buybacks never exceed the capital over target at ... |
| 4671 | 1 | `public double getSharesBoughtBack()` |  |
| 4672 | 1 | `public double getSharesIssued()` |  |
| 4680 | 1 | `public double dividendsOverYear()` | Dividends over the last twelve months, this one included. |
| 4682 | 1 | `public double buybacksOverYear()` | ...and its own shares bought back. |
| 4685 | 3 | `public double returnOnEquity()` | This month's net income over the equity it opened with, a year: the return a bank is read by. |

### what it lends (lines 4689-4772)

| line | len | member | says |
|---:|---:|---|---|
| 4703 | 26 | `public double lendingGrowthLimit()` | HOW FAST THE BANK LETS A BORROWER'S DEBT GROW THIS MONTH, on the capital it has: no limit at or over its target (and with no branch - a city with no bank is lent to from outside); none under the minimum or failed; in ... |
| 4731 | 7 | `public boolean lendsOnlyToKeepBorrowersGoing()` | True when it lends only what keeps its existing borrowers going: under the minimum, or failed. |
| 4740 | 6 | `public double lendingLimit()` | The growth of the book the capital rule allows this month, in money: infinite when it lends freely. |
| 4748 | 8 | `public String lendingStance()` | ...in words. |

### the save (lines 4773-4939)

| line | len | member | says |
|---:|---:|---|---|
| 4784 | 18 | `public java.util.Map<String, double[]> allowanceToSave()` | The allowance, book by book (0.7.8): each sector's name, and HOUSEHOLD_BOOK for the families, to {the allowance, what it held when the month opened, what the month wrote off, and whether it is in trouble - 1 or 0 for ... |
| 4804 | 29 | `public boolean restoreAllowance(java.util.Map<String, double[]> saved)` | ...and back. |
| 4834 | 5 | `private static double sum(java.util.Map<String, Double> m)` |  |
| 4847 | 12 | `public double[] capitalRecordToSave()` | The record the target and the owners' year are struck from (0.7.8): the months recorded, the worst year, the rings of provisions and of the weighted book, the owners' month count and the rings of dividends and buybacks. |
| 4860 | 11 | `public void restoreCapitalRecord(double[] in)` |  |
| 4881 | 24 | `public double[] monthLinesToSave()` | THE MONTH'S STATEMENT LINES (0.7.8), for the save. |
| 4907 | 32 | `public void restoreMonthLines(double[] v)` | ...and back. |

### reading (lines 4940-5089)

| line | len | member | says |
|---:|---:|---|---|
| 4942 | 1 | `public double getCash()` |  |
| 4943 | 1 | `public double getDeposits()` |  |
| 4944 | 1 | `public double getBranches()` |  |
| 4945 | 1 | `public double getSectorBook()` |  |
| 4946 | 1 | `public double getCityBook()` |  |
| 4947 | 1 | `public double getHouseholdBook()` |  |
| 4948 | 1 | `public double getInterestEarned()` |  |
| 4949 | 1 | `public double getWriteOffs()` |  |
| 4950 | 1 | `public double getPayroll()` |  |
| 4951 | 1 | `public double getUpkeep()` |  |
| 4952 | 1 | `public double getLentToHouseholds()` |  |
| 4953 | 1 | `public double getRepaidByHouseholds()` |  |
| 4963 | 3 | `public double getNetIncome()` | The month's profit: interest earned, less the cost of the money, less the loans that died, less the cost of running the place. |
| 4975 | 44 | `public boolean wantsBranch()` | True when the city should be opening another counter. |
| 5048 | 41 | `public boolean branchWouldPayForItself()` | Whether the counter earns more than it costs to keep open. |

### a branch that does not pay is closed (0.7.11, round 2) (lines 5090-5493)

| line | len | member | says |
|---:|---:|---|---|
| 5109 | 6 | `public boolean branchesCoverTheirStaff()` | Whether what the book kept last month covers what its branches cost - branchWouldPayForItself()'s own two inputs, keptPerBranch() against runningCostPerBranch(), read the other way. |
| 5143 | 3 | `public boolean closesBranch()` | THE BRANCH TEST RUN IN REVERSE, AND IT WAS MISSING. |
| 5148 | 1 | `public int getUncoveredMonths()` | Closed months in a row the book has not kept its branches' staff. |
| 5165 | 7 | `public double bookAnotherBranchWouldCarry()` | The loan book one more branch would take onto the bank's own account. |
| 5184 | 8 | `public double capacityWith(double branchCount)` | Capacity this bank would have with a given number of branches. |
| 5205 | 10 | `public double headroom()` | How much more it could lend before it counts itself full (EASY_STRAIN): what the carry trade may take - and, since 0.7.8, no more than keeps the bank at its own capital target. |
| 5216 | 1 | `public void setCash(double value)` |  |
| 5218 | 83 | `public void reset()` |  |
| 5321 | 163 | `public void redenominate(double scale)` | Every figure on the bank's balance sheet, in the new unit. |
| 5487 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### WHAT THE BANK TAB READS (0.7.9) (lines 5494-5525)

### the interest, by who paid it (lines 5526-5562)

| line | len | member | says |
|---:|---:|---|---|
| 5544 | 9 | `public void takeInterest(double fromCity, double fromBusinesses)` | The businesses' interest and the city's coupons, settled together: the same cash and income as takeInterest() on their sum, to the bit, and each kept by who paid it. |
| 5555 | 1 | `public double getInterestFromBusinesses()` | What the businesses paid it in interest this month. |
| 5557 | 1 | `public double getInterestFromCity()` | ...the city, in coupons on the paper the bank holds. |
| 5559 | 1 | `public double getInterestFromHouseholds()` | ...the families, on their credit lines. |
| 5561 | 1 | `public double getDiscountAccreted()` | ...and the discount on the city's paper it earned this month, which no cash carries. |

### what moved it between two presses (lines 5563-5599)

| line | len | member | says |
|---:|---:|---|---|
| 5572 | 1 | `public double getTreasuryBuybackGain()` |  |
| 5581 | 1 | `public double getAllowanceOpened()` |  |
| 5589 | 1 | `public double getBailoutsLifetime()` |  |
| 5598 | 1 | `public boolean isMonthKnown()` |  |

### its year of statements (lines 5600-5787)

| line | len | member | says |
|---:|---:|---|---|
| 5612 | 11 | **type** `public enum Line` | The lines of a month's statement the Bank tab sets beside last month's and adds up over a year, every one money: the interest and who paid it; what savers and the window were paid; fees and their three kinds; provisio... |
| 5625 | 1 | `public double revenue()` | What it earned before provisions and costs: net interest, fees, the desk and the city's paper - what its costs are read against. |
| 5636 | 1 | `public double getRetained()` | What it kept of the month's profit once its owners were paid: net income less the dividend and its own shares bought back. |
| 5639 | 38 | `public double thisMonth(Line line)` | A line as this month stands. |
| 5687 | 5 | `private void fileStatement()` |  |
| 5694 | 1 | `public boolean knowsLastMonth()` | True when last month is on file: a month was played before this one, or a save carried it. |
| 5697 | 4 | `public double lastMonth(Line line)` | A line as last month ended, everything booked after its close included. |
| 5703 | 1 | `public int monthsInYear()` | How many months the year's figures cover: this one and those on file, YEAR_MONTHS at most. |
| 5706 | 8 | `public double overYear(Line line)` | A line added up over monthsInYear(), this month included - for the flows; the two stocks want averageOverYear(). |
| 5716 | 1 | `public double averageOverYear(Line line)` | ...and averaged over them: a month's worth. |
| 5723 | 4 | `public double returnOnEquityOverYear()` | What it earned over the year, at a yearly rate, on the equity it held on average: the return a bank is read by, and steadier than a month's (returnOnEquity()). |
| 5733 | 4 | `public double provisionRateOverYear()` | Provisions over the year, at a yearly rate, as a share of the book it held on average: its credit losses as a bank reports them - a sound book's is BASE_LOSS_RATE. |
| 5743 | 4 | `public double netInterestMarginOverYear()` | Net interest income over the year, at a yearly rate, on the book it held on average: its net interest margin, steadier than a month's (netInterestMargin()). |
| 5753 | 4 | `public double costShareOverYear()` | Its staff and branches over the year as a share of what it earned before them (revenue()): the efficiency ratio, about 50-60% at a real bank. |
| 5759 | 10 | `public double[] statementYearToSave()` | The year of statements, for the save: how many are filed, how many lines each, then the ring's months in slot order. |
| 5776 | 11 | `public void restoreStatementYear(double[] in)` | ...and back. |

### its balance sheet (0.7.13) (lines 5788-5968)

| line | len | member | says |
|---:|---:|---|---|
| 5803 | 11 | **type** `public enum Sheet` | THE LINES OF ITS BALANCE SHEET, as the model books it (THE THREE STATEMENTS): what totalAssets() and totalLiabilities() sum, line by line, the allowance negative, and equity() the residual; then the two deposits it co... |
| 5840 | 27 | `public double sheet(Sheet line)` | A line of its balance sheet as it stands: its reserves at the central bank (the cash it is not borrowing; its placements abroad came home to the central bank in 0.7.0); what the businesses owe it outside the insured m... |
| 5874 | 1 | `public double sheetResidual()` | What its lines leave unexplained: the asset lines, less the liability lines, less equity. |
| 5877 | 1 | `public double yearAgoResidual()` | ...and the same of the sheet a year ago; nothing when none is on file. |
| 5880 | 1 | `public double liabilitiesAndEquity()` | The other side of the sheet: its liabilities and its equity together. |
| 5883 | 1 | `public double yearAgoLiabilitiesAndEquity()` | ...and a year ago; nothing when none is on file. |
| 5885 | 6 | `private static double residual(java.util.function.ToDoubleFunction<Sheet> at)` |  |
| 5902 | 10 | `private void fileSheet()` |  |
| 5914 | 1 | `public boolean knowsYearAgo()` | True when the sheet a year before this one is on file: a year played in this build, or a save that carried one. |
| 5917 | 3 | `public double yearAgo(Sheet line)` | A line of the sheet a year before this one; nothing when none is on file. |
| 5922 | 3 | `public double yearAgoLoansToSector(int sector)` | What the sector at this index of Sectors.KEYS owed it a year ago, outside its insured mortgages and interim financing. |
| 5927 | 4 | `public double yearAgoInterimToSector(int sector)` | ...and its interim financing then. |
| 5933 | 11 | `public double[] sheetYearToSave()` | The year of sheets, for the save: how many are filed, the lines and the sectors each holds, then the ring's months in slot order. |
| 5951 | 17 | `public void restoreSheetYear(double[] in)` | ...and back. |

### its rates, in a ladder (lines 5969-6019)

| line | len | member | says |
|---:|---:|---|---|
| 5985 | 20 | **type** `public record Ladder(double policy, double savers, double saversChose, double saversShare, double fundingPo...` | THE LADDER OF ITS RATES at one policy rate, read at one moment: the policy rate; what savers were paid, the rate the bank chose, the share of the policy rate its funding asks it to pass on and the funding position tha... |
| 5991 | 1 | `public double saversOverPolicy()` _(in Bank.Ladder)_ | Savers' rate less the policy rate: under it by the bank's margin on a deposit. |
| 5993 | 1 | `public double transferOverPolicy()` _(in Bank.Ladder)_ | The funds-transfer price over the policy rate: the window's penalty on its share, and the term premium. |
| 5995 | 1 | `public double primeOverTransfer()` _(in Bank.Ladder)_ | Prime over the funds-transfer price: the running costs, the expected loss and the capital charge. |
| 5997 | 1 | `public double parts()` _(in Bank.Ladder)_ | The four parts added up in prime's own order - which is prime. |
| 5999 | 1 | `public double overPrime(double rate)` _(in Bank.Ladder)_ | A borrower's rate over prime: the step each borrower's rung is labelled with - its own risk, or for the carry trade the costs it does not carry. |
| 6001 | 1 | `public double mortgageTransferOverPolicy()` _(in Bank.Ladder)_ | An insured mortgage's money over the policy rate: the window's penalty on its share, and the ten-year term premium. |
| 6003 | 1 | `public double mortgageOverTransfer()` _(in Bank.Ladder)_ | An insured mortgage's rate over its money: running the bank, and the capital its leverage requirement ties up (round 2). |
| 6007 | 12 | `public Ladder ladder(double policyAnnual)` | The ladder at this policy rate. |

### in words (lines 6020-6081)

| line | len | member | says |
|---:|---:|---|---|
| 6035 | 27 | `public String status()` | THE BANK'S STATE IN ONE SENTENCE, with the figure that decides it: the first thing the Bank tab says. |
| 6064 | 17 | `public String targetReason()` | Why its capital target is what it is, in words: the cap, its worst year, or the standard buffer and why. |

### what its book weighs (lines 6082-6124)

| line | len | member | says |
|---:|---:|---|---|
| 6085 | 1 | **type** `public enum Book` | The eight things on its books that capacity weighs: the four it lends on, the insured mortgages inside the businesses' (0.7.11), the desk's shares, and since 0.7.12 the businesses' bonds it holds and the weight the bo... |
| 6093 | 1 | **type** `public record WeightRow(Book book, double face, double term, double risk, double weighted)` | One row of what the book weighs: its face; the share of it its remaining term counts for (maturityWeight(), on average over its loans - 1 where nothing runs off); its risk weight; and what it weighs, face x term x risk. |
| 6102 | 17 | `public java.util.List<WeightRow> weightTable()` | Every row, the desk's shares included: the weighted column foots to getWeightedBook(). |
| 6120 | 4 | `private static WeightRow weightRow(Book book, double face, double risk, double weighted)` |  |

### its funding (lines 6125-6148)

| line | len | member | says |
|---:|---:|---|---|
| 6128 | 1 | `public double getHouseholdDeposits()` | What the families have banked with it. |
| 6130 | 1 | `public double getSectorDeposits()` | ...and what the businesses hold in credit. |
| 6133 | 1 | `public double getDepositsPerBranch()` | How much of a city's savings one branch reaches, in today's money: DEPOSITS_PER_BRANCH, reformed with every other figure. |
| 6135 | 1 | `public double getPaidInPerBranch()` | What its owners put up when a branch opens, in today's money: PAID_IN_PER_BRANCH, reformed. |
| 6138 | 1 | `public double branchReach()` | How much of the city's own savings its branches can reach: the branches standing times getDepositsPerBranch(). |
| 6140 | 1 | `public double localDeposits()` | The city's own savings with it: everything banked, less the world's. |
| 6142 | 1 | `public double localDepositsReached()` | ...the part its branches reach - which, with the world's, is depositsGathered(). |
| 6144 | 1 | `public double localDepositsBeyondReach()` | ...and the part they do not: savings only another branch would reach. |
| 6147 | 1 | `public double fundingLimit()` | What its funding would carry: what its branches gathered, lent LEVERAGE times over - the second of capacity()'s two limits. |

### another branch (lines 6149-6169)

| line | len | member | says |
|---:|---:|---|---|
| 6152 | 1 | `public double capacityAnotherBranchWouldAdd()` | The capacity one more branch would add, the capital it would open with counted: nothing when the deposits are the limit and the branches already reach them all. |
| 6155 | 1 | `public double overflowPastComfortable()` | The weighted book past what the bank comfortably carries, EASY_STRAIN of its capacity: what another branch could take onto its own account. |
| 6158 | 1 | `public double runningCostPerBranch()` | What one branch cost to run last month: its payroll and upkeep over the branches standing. |
| 6166 | 3 | `public double keptPerBranch()` | What one branch's share of the book kept last month: its book per branch at last month's kept margin (net interest and fees over the book) - what branchWouldPayForItself() weighs against runningCostPerBranch(). |

### how its equity moved (lines 6170-6212)

| line | len | member | says |
|---:|---:|---|---|
| 6187 | 12 | **type** `public record EquityMovement(double opening, double kept, double fromShareholders, double fromCity, double ...` | HOW ITS EQUITY MOVED since the month opened, every cause named: what it kept (net income); capital put in by its shareholders at home and abroad, and by the city in a rescue; the founding settlement, the month the cit... |
| 6193 | 5 | `public double residual()` _(in Bank.EquityMovement)_ | What none of the causes explains. |
| 6206 | 6 | `public EquityMovement equityMovement()` | The month's movement, as it stands. |

### its equity, in two parts (lines 6213-6317)

| line | len | member | says |
|---:|---:|---|---|
| 6259 | 1 | `public boolean knowsEquitySplit()` | True when this bank has kept its equity in two parts since it was founded; false on a save from before 0.7.13's round 2. |
| 6268 | 4 | `public double paidInThisMonth()` | What its owners and the city put in this month: its offerings at home and abroad, new shares, less shares bought back, and the city's capital in a resolution - and since 0.7.14, less the old owners' paid-in written of... |
| 6282 | 5 | `public double retainedThisMonth()` | ...and everything else that moved its equity this month: its net income, less its dividend, the founding settlement, what its creditors absorbed (before 0.7.14), the treasury's buybacks, an older save's allowance - an... |
| 6289 | 1 | `public double paidInCapital()` | Its paid-in capital as it stands. |
| 6292 | 1 | `public double retainedEarnings()` | Its retained earnings as they stand - the Balance sheet page's, not getRetained()'s month's payout. |
| 6295 | 3 | `public double equitySplitResidual()` | What the parts leave unexplained against equity() - paid in, retained and, since 0.7.14, the city's preferred: nothing when every cause is routed, and nothing when !knowsEquitySplit(). |
| 6300 | 1 | `public double paidInOpening()` | Its paid-in capital at the top of the month, for the save; nothing when !knowsEquitySplit(). |
| 6303 | 1 | `public double retainedOpening()` | ...and its retained earnings. |
| 6310 | 6 | `public void restoreEquitySplit(Double paidIn, Double retained)` | The load path: the two counters at the top of the saved month, after the month's lines are restored (restoreMonthLines()). |


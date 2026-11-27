# Bank.java - 7,129 lines · 518 methods · 79 constants · model

`ham/citybuildersim/Bank.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

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
>   BRANCHES were the second limit until 0.7.19: one branch reached
>   DEPOSITS_PER_BRANCH of the city's savings, a founding-money constant, so
>   a city with deep savings and one branch was a city queueing at one
>   counter. Savings reach the bank wherever its branches are since then
>   (online banking, Jerus's choice) - the limit is what the city has banked
>   with it - and a branch is a counter for its CUSTOMERS, each one past
>   the first, the city's charter, paying for itself from their fees (THE
>   BRANCHES, BY THEIR CUSTOMERS). The second limit now is its CAPITAL, the
>   book its equity carries (capitalLimit(); WHAT ACTUALLY CONSTRAINS A
>   BANK, below).
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
> ... (8 more lines in the source)

**Uses:** [BankRules](BankRules.md) (15), [BusinessDebtManager](BusinessDebtManager.md) (13), [DebtManager](DebtManager.md) (8), [Sectors](Sectors.md) (7), [Currency](Currency.md) (4), [PriceLevel](PriceLevel.md) (3), [Mortgage](Mortgage.md) (3), [Ladder](Ladder.md) (3), [PayTier](PayTier.md) (2), [Equity](Equity.md) (1), [TreasuryFund](TreasuryFund.md) (1), [CentralBank](CentralBank.md) (1), [MoneyAudit](MoneyAudit.md) (1)

**Used by (80):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankRules](BankRules.md), [BankRulesCheck](BankRulesCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessInvestment](BusinessInvestment.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CarCheck](CarCheck.md), [CardCheck](CardCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [DataSave](DataSave.md), [DebtManager](DebtManager.md), [DebtStatistics](DebtStatistics.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [ForestryCheck](ForestryCheck.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GoldStandardCheck](GoldStandardCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HousingMarketCheck](HousingMarketCheck.md), [Inbox](Inbox.md), [InterestAbroadCheck](InterestAbroadCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LongPlaytest](LongPlaytest.md), [MarketCheck](MarketCheck.md), [ModernRowsCheck](ModernRowsCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewUnitCheck](NewUnitCheck.md), [OutsideCheck](OutsideCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [ShortSellingCheck](ShortSellingCheck.md), [SummaryScreen](SummaryScreen.md), [TimeSkipReport](TimeSkipReport.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [WoodProductsCheck](WoodProductsCheck.md), [YoungBankCheck](YoungBankCheck.md)

## Sections

| line | section |
|---:|---|
| 85 | · the dials |
| 204 | · what a dollar of book WEIGHS |
| 297 | · the position |
| 339 | · the month's working |
| 396 | · the month |
| 550 | THE CORPORATE BONDS IT HOLDS (0.7.12) |
| 641 | · carry |
| 701 | WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7) |
| 791 | · the four parts |
| 945 | THE BANK PRICES CONCENTRATION (0.7.12) |
| 1290 | · what the four parts remember |
| 1531 | · the arithmetic |
| 1561 | THE TRADING DESK |
| 1639 | · the desk held to its capital (0.7.8) |
| 2273 | THE CITY'S CAPITAL (0.7.14): A RESOLUTION, AND THE PREFERRED |
| 2897 | · the flows |
| 2972 | THE CITY'S PAPER CHANGES HANDS (0.7.1) |
| 3284 | FEES (0.7.7) |
| 3359 | THE FUNDING SIDE |
| 3719 | · · SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY. |
| 3768 | WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. |
| 3916 | THE THREE STATEMENTS |
| 4028 | · tax |
| 4153 | · LAST MONTH, KEPT ON PURPOSE |
| 4306 | THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 4385 | · the allowance |
| 4664 | · what it holds |
| 4806 | · the leverage ratio (0.7.11, round 2) |
| 4890 | · what it does with profit |
| 5012 | · its own shares |
| 5177 | · what it lends |
| 5267 | · the save |
| 5446 | · reading |
| 5473 | THE BRANCHES, BY THEIR CUSTOMERS (0.7.19) |
| 5741 | · a branch whose fees do not cover it is closed (0.7.19) |
| 6190 | WHAT THE BANK TAB READS (0.7.9) |
| 6222 | · the interest, by who paid it |
| 6259 | · what moved it between two presses |
| 6296 | · its year of statements |
| 6513 | · its balance sheet (0.7.13) |
| 6608 | · when the lines do not add up (BI1, 2026-11-20) |
| 6733 | · its rates, in a ladder |
| 6786 | · in words |
| 6853 | · what its book weighs |
| 6896 | · its funding |
| 6928 | · another branch |
| 6942 | · how its equity moved |
| 7011 | · its equity, in two parts |

## Enum constants

| line | constant | says |
|---:|---|---|
| 4899 | `Bank.Payout.NO_BANK` |  |
| 4899 | `Bank.Payout.FAILED` |  |
| 4899 | `Bank.Payout.UNDER_MINIMUM` |  |
| 4899 | `Bank.Payout.REBUILDING` |  |
| 4899 | `Bank.Payout.PAYING` |  |
| 4899 | `Bank.Payout.RETURNING` |  |
| 6309 | `Bank.Line.INTEREST` |  |
| 6309 | `Bank.Line.FROM_BUSINESSES` |  |
| 6309 | `Bank.Line.FROM_HOUSEHOLDS` |  |
| 6309 | `Bank.Line.FROM_CITY` |  |
| 6309 | `Bank.Line.FROM_CARRY` |  |
| 6309 | `Bank.Line.FROM_RESERVES` |  |
| 6309 | `Bank.Line.DISCOUNT` |  |
| 6310 | `Bank.Line.SAVERS` |  |
| 6310 | `Bank.Line.WINDOW` |  |
| 6310 | `Bank.Line.NET_INTEREST` |  |
| 6311 | `Bank.Line.FEES` |  |
| 6311 | `Bank.Line.ACCOUNT_FEES` |  |
| 6311 | `Bank.Line.LOAN_FEES_PAID` |  |
| 6311 | `Bank.Line.LOAN_FEES_OWED` |  |
| 6312 | `Bank.Line.PROVISIONS` |  |
| 6312 | `Bank.Line.WRITE_OFFS` |  |
| 6312 | `Bank.Line.TRADING` |  |
| 6312 | `Bank.Line.PAPER_GAINS` |  |
| 6312 | `Bank.Line.REVENUE` |  |
| 6313 | `Bank.Line.COSTS` |  |
| 6313 | `Bank.Line.PAYROLL` |  |
| 6313 | `Bank.Line.UPKEEP` |  |
| 6313 | `Bank.Line.PRE_TAX` |  |
| 6313 | `Bank.Line.TAX` |  |
| 6313 | `Bank.Line.NET` |  |
| 6314 | `Bank.Line.DIVIDENDS` |  |
| 6314 | `Bank.Line.BUYBACKS` |  |
| 6314 | `Bank.Line.ISSUED` |  |
| 6314 | `Bank.Line.RETAINED` |  |
| 6315 | `Bank.Line.BOOK` |  |
| 6315 | `Bank.Line.EQUITY` |  |
| 6317 | `Bank.Line.FROM_BONDS` | 0.7.12's: the coupons on its bonds, its underwriting fees and its gains on bonds. |
| 6317 | `Bank.Line.UNDERWRITING` |  |
| 6317 | `Bank.Line.BOND_GAINS` |  |
| 6318 | `Bank.Line.WHOLESALE_ABROAD` |  |
| 6529 | `Bank.Sheet.RESERVES` |  |
| 6529 | `Bank.Sheet.BUSINESS_LOANS` |  |
| 6529 | `Bank.Sheet.INTERIM` |  |
| 6529 | `Bank.Sheet.MORTGAGES` |  |
| 6529 | `Bank.Sheet.FAMILIES` |  |
| 6529 | `Bank.Sheet.CARRY` |  |
| 6529 | `Bank.Sheet.CITY_PAPER` |  |
| 6529 | `Bank.Sheet.BONDS` |  |
| 6529 | `Bank.Sheet.ALLOWANCE` |  |
| 6529 | `Bank.Sheet.DESK` |  |
| 6530 | `Bank.Sheet.ASSETS` |  |
| 6531 | `Bank.Sheet.DEPOSIT_FUNDING` |  |
| 6531 | `Bank.Sheet.WINDOW` |  |
| 6531 | `Bank.Sheet.FOREIGN_DEPOSITS` |  |
| 6531 | `Bank.Sheet.DESK_SHORT` |  |
| 6531 | `Bank.Sheet.UNEARNED_DISCOUNT` |  |
| 6532 | `Bank.Sheet.LIABILITIES` |  |
| 6533 | `Bank.Sheet.EQUITY` |  |
| 6534 | `Bank.Sheet.HOUSEHOLD_DEPOSITS` |  |
| 6534 | `Bank.Sheet.SECTOR_DEPOSITS` |  |
| 6535 | `Bank.Sheet.PAID_IN` |  |
| 6535 | `Bank.Sheet.RETAINED` |  |
| 6537 | `Bank.Sheet.PREFERRED` | The city's preferred at par (0.7.14): equity's third part, beside paid in and retained. |
| 6538 | `Bank.Sheet.WHOLESALE_ABROAD` |  |
| 6538 | `Bank.Sheet.WINDOW_INTEREST_DUE` |  |
| 6856 | `Bank.Book.BUSINESSES` |  |
| 6856 | `Bank.Book.CITY` |  |
| 6856 | `Bank.Book.FAMILIES` |  |
| 6856 | `Bank.Book.CARRY` |  |
| 6856 | `Bank.Book.DESK` |  |
| 6856 | `Bank.Book.MORTGAGES` |  |
| 6856 | `Bank.Book.BONDS` |  |
| 6856 | `Bank.Book.CONCENTRATION` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `Bank.LEVERAGE` | `6` | How many times its deposits the bank will lend. |
| 123 | `Bank.CAPITAL_RATIO` | `.08` | Equity a bank must hold against its risk-weighted book. |
| 153 | `Bank.LEVERAGE_RATIO_MIN` | `.03` | Equity a bank must hold against everything it has lent, whatever that weighs: 3%, Basel III's leverage ratio (0.7.11, round 2). |
| 199 | `Bank.PAID_IN_PER_BRANCH` | `32_000 * PriceLevel.FACTOR` | What the shareholders put up when a branch opens. |
| 227 | `Bank.RISK_CITY` | `.20` | The city cannot default on its own paper. |
| 230 | `Bank.RISK_BUSINESS` | `1.00` | A business can be restructured, and in this game regularly is. |
| 233 | `Bank.RISK_HOUSEHOLD` | `1.00` | ...and a family can be discharged. |
| 249 | `Bank.RISK_CARRY` | `1.00` | A foreign carry borrower, against the risk weights above. |
| 266 | `Bank.RISK_INSURED_MORTGAGE` | `0.0` | An insured mortgage (0.7.11): 0%. |
| 269 | `Bank.SHORTEST_WEIGHT` | `.40` | What a loan repaying tomorrow weighs against one repaying never. |
| 272 | `Bank.LONG_TERM_MONTHS` | `60` | Months of remaining term at which a loan weighs its full amount. |
| 287 | `Bank.EASY_STRAIN` | `.80` | How much of its capacity the bank lends before it counts itself full: the carry trade is lent only the room below it. |
| 767 | `Bank.BASE_LOSS_RATE` | `.004` | What a sound loan is expected to lose a year through the cycle, as a share of the book: 0.4%, about what Canada's big banks provision for credit losses in a normal year (RBC, 2025). |
| 770 | `Bank.LOSS_WATCH` | `2` | How many times BASE_LOSS_RATE a year of provisions runs at before the Bank tab's CREDIT LOSSES is a warning: twice what a sound book loses (0.7.33; the screen's own literal until then). |
| 773 | `Bank.COST_WINDOW_MONTHS` | `12` | Months of payroll and upkeep the running costs are measured over: a year, so one month's building bill is not a price. |
| 776 | `Bank.PRIME_TERM_MONTHS` | `BusinessDebtManager.LOAN_TERM_MONTHS` | The term prime is struck at: a business loan's, BusinessDebtManager.LOAN_TERM_MONTHS, which takes the short end's term premium. |
| 789 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 1016 | `Bank.IRB_CONFIDENCE` | `.999` | The confidence the IRB capital is struck at: 99.9% of years, BCBS (2006) paragraph 272. |
| 1019 | `Bank.IRB_CORRELATION_HIGH` | `.24` | The IRB corporate correlation for the soundest firms: 24%, BCBS (2006) paragraph 272. |
| 1022 | `Bank.IRB_CORRELATION_LOW` | `.12` | ...and for the riskiest: 12%, the same paragraph. |
| 1025 | `Bank.IRB_CORRELATION_DECAY` | `50` | How fast the correlation falls from the one to the other as the default rate rises: the 50 in exp(-50 x PD), the same paragraph. |
| 1028 | `Bank.IRB_MATURITY_YEARS` | `2.5` | The effective maturity of a corporate exposure under the foundation IRB approach: 2.5 years, BCBS (2006) paragraph 318 - where the maturity adjustment reads 1 / (1 - 1.5 b). |
| 1031 | `Bank.IRB_MATURITY_A` | `.11852` | The maturity adjustment's slope, b(PD) = (0.11852 - 0.05478 ln PD)^2: its constant, BCBS (2006) paragraph 272. |
| 1034 | `Bank.IRB_MATURITY_B` | `.05478` | ...and its log coefficient, the same paragraph. |
| 1049 | `Bank.SECTOR_CORRELATION_MULTIPLIER` | `1.25` | HOW MUCH MORE CORRELATED THE FIRMS OF ONE INDUSTRY ARE than the IRB's corporate correlation, which is struck for a book spread across every industry: 1.25, the asset value correlation multiplier Basel III applies to e... |
| 1077 | `Bank.IRB_QUANTILE` | `inverseNormal(IRB_CONFIDENCE)` | The standard normal quantile at IRB_CONFIDENCE, G(0.999) in the IRB formula: struck once. |
| 1219 | `Bank.CARD_NEUTRAL_RATE` | `.2135` | Bank of Canada card APR at a neutral policy rate (S2, spec-credit-housing). |
| 1221 | `Bank.CARD_SPREAD` | `CARD_NEUTRAL_RATE - DebtManager.NEUTRAL_RATE - BASE_LOSS_RATE -(CAPITAL_RATIO...` | Spread over a sound bank's neutral household line, before borrower risk. |
| 1224 | `Bank.CARD_RATE_CAP` | `.35` | Canada's criminal APR ceiling, effective January 2025 (S29). |
| 1226 | `Bank.CARD_MINIMUM` | `.03` | Issuers' common Canadian minimum (FCAC, S5). |
| 1228 | `Bank.CARD_MIN_PRINCIPAL` | `.01` | Principal paid alongside the interest when that exceeds the percentage minimum. |
| 1230 | `Bank.CARD_SHOCK_CHANCE` | `1.0 / 6` | Estimated unexpected expenses: two in a year. |
| 1232 | `Bank.CARD_SHOCK_MONTHS` | `.75` | Estimated unexpected bill in months of take-home. |
| 1234 | `Bank.CARD_PAYDOWN` | `.12` | Estimated spare income used for additional repayment. |
| 1236 | `Bank.CARD_CURE` | `.15` | Estimated share of late balances catching up each month. |
| 1238 | `Bank.CARD_CHARGE_OFF_MONTHS` | `12 * 2.85 / 3.82` | Stock/hazard equivalent of Fed 3.82% charge-offs / 2.85% delinquency (S3). |
| 1240 | `Bank.CARD_BOTTOM_ACCESS` | `.60` | Estimated lender acceptance in the bottom tier. |
| 1242 | `Bank.CARD_OTHER_ACCESS` | `.95` | Estimated lender acceptance above the bottom tier. |
| 1577 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 1937 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 2132 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000 * PriceLevel.FACTOR` | Deposits at which half of new bank capital is found at home. |
| 2345 | `Bank.PREFERRED_MIN_SHARE` | `.01` | The least preferred the bank asks for, of its risk-weighted assets: 1%, TARP's "not less than 1% of its risk-weighted assets". |
| 2348 | `Bank.PREFERRED_MAX_SHARE` | `.03` | ...and the most: 3%, TARP's "not more than ... |
| 2351 | `Bank.PREFERRED_RATE` | `.05` | The preferred's dividend, a year, until its fifth anniversary: 5%, TARP's "cumulative dividends at a rate of 5% per annum until the fifth anniversary". |
| 2354 | `Bank.PREFERRED_STEP_RATE` | `.09` | ...and after it: 9%, TARP's "thereafter at a rate of 9% per annum". |
| 2357 | `Bank.PREFERRED_STEP_MONTHS` | `60` | The months to the step: the fifth anniversary. |
| 2360 | `Bank.PREFERRED_CONSENT_MONTHS` | `36` | The months the city's consent binds - no buyback, no rise in the common dividend a share: three years, TARP's "until the third anniversary". |
| 2363 | `Bank.PREFERRED_REDEEM_MONTHS` | `36` | When a block is redeemed whole: its third anniversary, the first TARP's term sheet allows it from anything but new common ("may not be redeemed for a period of three years ... |
| 2366 | `Bank.WARRANT_SHARE` | `.15` | The warrants' reach, of the preferred's amount: 15%, TARP's "aggregate market price equal to 15% of the Senior Preferred amount". |
| 2369 | `Bank.WARRANT_TERM_MONTHS` | `120` | The warrants' term, in months: ten years, TARP's. |
| 3313 | `Bank.ACCOUNT_FEE` | `.012 * PriceLevel.FACTOR` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 3316 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 3471 | `Bank.BUSINESS_COLLATERAL_HAIRCUT` | `.30` | Secured business/mortgage loan haircut: BoC SLF secured business loans; ELA mortgages (CBK3 notes). |
| 3473 | `Bank.HOUSEHOLD_COLLATERAL_HAIRCUT` | `.85` | Unsecured household loan haircut: BoC SLF's unsecured consumer portfolio. |
| 3475 | `Bank.GOVERNMENT_COLLATERAL_HAIRCUT` | `.05` | Government paper haircut: the Fed's conservative long-duration Treasury margin. |
| 3477 | `Bank.WINDOW_COLLATERAL_SHARE` | `1` | The window's maximum share of collateral after haircuts. |
| 3479 | `Bank.WHOLESALE_TERM_MONTHS` | `12` | Replacement wholesale funding has a one-year term, qualifying as stable funding under NSFR. |
| 3482 | `Bank.LOAN_STABLE_SHARE` | `.85` | Basel NSFR required stable funding for ordinary performing long loans. |
| 3484 | `Bank.MORTGAGE_STABLE_SHARE` | `.65` | Basel NSFR required stable funding for low-risk residential mortgages. |
| 3486 | `Bank.GOVERNMENT_STABLE_SHARE` | `.05` | Basel NSFR required stable funding for Level 1 government paper. |
| 3488 | `Bank.HOUSEHOLD_STABLE_SHARE` | `.95` | Basel NSFR available stable funding from stable retail deposits. |
| 3490 | `Bank.BUSINESS_STABLE_SHARE` | `.50` | Basel NSFR available stable funding from short non-financial corporate deposits. |
| 3825 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - set so that this bank... |
| 3828 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of funding bid for it ... |
| 3831 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 4388 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Risk-staging reference at the original 90% business loan/assets ceiling. |
| 4392 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 4667 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 4685 | `Bank.BUFFER_BEFORE_L8` | `0` | The least buffer a bank holds over the minimum before Basel III (L8): none was required, so its buffer is its worst year alone (BIS, 12 Sep 2010: the conservation buffer is Basel III's). |
| 4701 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 4704 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 4795 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point ceiling and the 3.5-p... |
| 4893 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 4896 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 5099 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into an issue on the las... |
| 5180 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit at the target (lend... |
| 5566 | `Bank.CUSTOMERS_PER_BRANCH` | `16_000` | The customers one branch serves: 16,000, TD's clients per branch - "approximately 16 million clients in Canadian Personal and Business banking" through "more than 1,000 branches" (TD, corporate information, as of Apri... |
| 6542 | `Bank.SHEET_ASSETS` | `{ Sheet.RESERVES, Sheet.BUSINESS_LOANS, Sheet.INTERIM, Sheet.MORTGAGES, Sheet...` | The asset lines, in the page's order: they sum to totalAssets(). |
| 6546 | `Bank.SHEET_LIABILITIES` | `{ Sheet.DEPOSIT_FUNDING, Sheet.WINDOW, Sheet.FOREIGN_DEPOSITS, Sheet.DESK_SHO...` | ...and the liability lines: they sum to totalLiabilities(). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 75 | `private final BankRules rules` |  |
| 202 | `private double paidInPerBranch` | The same, in today's money - struck at the expected price level since 0.7.42 (seedConstants(), from Game.restrikeMoneyConstants()). |
| 299 | `private double cash` |  |
| 300 | `private double branches` |  |
| 301 | `private double deposits` |  |
| 304 | `private double householdDeposits` | The two halves of that, because they are paid separately. |
| 305 | `private double sectorDeposits` |  |
| 308 | `private double sectorBook` | What is lent out, by whom it is owed. |
| 309 | `private double cityBook` |  |
| 310 | `private double householdBook` |  |
| 318 | `private double mortgageBook` | The insured mortgages inside sectorBook (0.7.11): part of what the businesses owe, weighed at RISK_INSURED_MORTGAGE rather than RISK_BUSINESS, so the weight table shows them as their own row. |
| 319 | `private double mortgageWeighted` |  |
| 327 | `private final double[] loansBySector` | ...and what the businesses owe it sector by sector, in Sectors.KEYS order, with the interim financing (InterimLoan) apart (0.7.13): the Balance sheet page's detail, set by Game.refreshBank() from the same loans sector... |
| 328 | `private final double[] interimBySector` |  |
| 329 | `private double interimBook` |  |
| 332 | `private double insuranceClaims` | What the treasury paid it this month on insured mortgages the month's defaults wrote down (0.7.11): cash for a claim on its book, so no income and no loss. |
| 335 | `private double sectorWeighted` | The same three, weighted for risk and remaining term. |
| 336 | `private double cityWeighted` |  |
| 337 | `private double householdWeighted` |  |
| 341 | `private double interestEarned` |  |
| 342 | `private double writeOffs` |  |
| 343 | `private double payroll` |  |
| 344 | `private double upkeep` |  |
| 345 | `private double lentToHouseholds` |  |
| 346 | `private double repaidByHouseholds` |  |
| 347 | `private double fundingCost` |  |
| 350 | `private double windowOwedCost` | The part on the actual advance still held past the active tranche, saved with the month's statement lines. |
| 393 | `private double placementIncome` | WHAT ITS SPARE CASH EARNS: THE POLICY RATE, AT THE CENTRAL BANK (0.7.0). |
| 394 | `private double openingEquity` |  |
| 408 | `private double carryBook` | The fourth book: what foreigners have borrowed to take abroad. |
| 409 | `private double carryLent, carryRepaid, carryInterest` |  |
| 570 | `private double bondBook` | What its bonds cost it, less what defaults took: their carrying value, on its book. |
| 572 | `private double bondWeighted` | ...weighed as loans to their issuers. |
| 574 | `private double bondFace` | ...and their face. |
| 576 | `private double bondGains, underwritingFees, interestFromBonds` | The month's gain on bonds sold or repaid over what they cost it, its underwriting fees, and its coupons. |
| 1112 | `private final java.util.Map<String, Exposure> exposures` |  |
| 1113 | `private final java.util.Map<String, Double> concentrationMarginal` |  |
| 1114 | `private double concentrationHerfindahl, concentrationAddOn, concentrationExposure` |  |
| 1293 | `private double lastWindowShare` | The funding blend and the running-cost rate, struck at the last close. |
| 1294 | `private double lastRunningCost` |  |
| 1300 | `private final double[] costRing` | The record the running costs are struck from: a year of payroll and upkeep beside the book they served, in rings indexed by closed months. |
| 1301 | `private final double[] costBookRing` |  |
| 1302 | `private int pricedMonths` |  |
| 1394 | `private double lastCostOfFunds` | What money cost this bank over the month that just closed. |
| 1580 | `private double securities` | The desk's inventory, at the exchange's mark. |
| 1588 | `private double tradingIncome` | The trading result so far this month: cash from what it sold less cash for what it bought, plus the change in what it holds at the mark, plus the dividends its inventory was paid. |
| 1600 | `private double markChange` | The part of the trading result that is the re-mark: every change in what the inventory is carried at this month, summed. |
| 1776 | `private double foreignDeposits` |  |
| 1814 | `private double hotMoneyIn, hotMoneyOut` |  |
| 1886 | `private boolean inResolution` | Failed, and not yet put back on its feet. |
| 1887 | `private int failures` |  |
| 1896 | `private double shortfallThisMonth, shortfallLifetime` | THE HOLE WHEN IT FAILED (0.7.14): its equity below nothing the month it went under, this month's and over its life - what the city's resolution then pays, with the capital to reopen. |
| 1915 | `private double resolutionLossThisMonth` | ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. |
| 1916 | `private double resolutionLossLifetime` |  |
| 2144 | `private double domesticCapitalScale` | The same, in today's money. |
| 2222 | `private double dividendsPaid` |  |
| 2381 | `double par` |  |
| 2382 | `double arrears` |  |
| 2383 | `int issued` |  |
| 2384 | `double capPerShare` |  |
| 2385 | `double warrantShares` |  |
| 2386 | `double strike` |  |
| 2387 | `int warrantsExpire` |  |
| 2388 | `boolean warrantsOut` |  |
| 2420 | `private final java.util.List<Preferred> preferred` |  |
| 2424 | `private int month` | The city's month, for the preferred's anniversaries: set by Game at the top of each month and on load. |
| 2427 | `private double preferredIn, preferredOut, preferredDividendsThisMonth, preferredAccruedThisMonth` | The month's causes (0.7.14), cleared at startMonth(), saved with the month's lines. |
| 2428 | `private double warrantsBoughtBackThisMonth, ownersWiped, preferredCancelled` |  |
| 2430 | `private double preferredDividendsLifetime, preferredRedeemedLifetime, warrantsBoughtBackLifetime` | ...and over its life. |
| 2433 | `private double repaymentRaisedThisMonth, repaymentRaisedLifetime` | What its offerings of new common raised to repay the city, this month and over its life, and the months a repayment - a due block, or the warrants - was left part-paid because the public did not take the whole. |
| 2434 | `private int repaymentShortLifetime` |  |
| 2803 | `private double capitalInjected` |  |
| 2804 | `private double capitalFromHome` |  |
| 2805 | `private double bailoutReceived` |  |
| 2806 | `private double foundingSettlement` |  |
| 2878 | `private double branchesCapitalised` |  |
| 2910 | `private double internalInterest` |  |
| 2987 | `private double paperGains` | The gain or loss on the city's paper that changed hands this month. |
| 2990 | `private double paperBoughtFromHouseholds` | What the desk paid households for their paper this month: money leaving the pools for a household, which MoneyAudit declares. |
| 2997 | `private double paperBoughtFromFirms` | ...and what it paid the businesses for theirs (EM1): a company asks its till for the cushion and the buyback it bids for, and what its dollars abroad cannot cover it raises by selling the city's paper to the desk (Out... |
| 3000 | `private double paperSoldToCentralBank, paperBoughtFromCentralBank` | What the central bank paid it for paper this month, and what it paid the central bank. |
| 3014 | `private double unearnedDiscount` | THE UNEARNED DISCOUNT (0.7.1): the part of what the bank paid under face for the city's paper it has not yet accreted into income, carried as a liability against the book - so equity does not jump by the discount the ... |
| 3169 | `private double discountRestruck` | The month's gain or loss from the pooled discount being re-struck: a cause of the equity's movement, routed to retained earnings and outside getPaperGains(). |
| 3173 | `private double buybackGains` | Gains less losses on paper the treasury bought back, over the city's life. |
| 3251 | `private double upkeepPerLater` | This month's operating cost a later branch carries, and whether the month's operating cost left the charter out. |
| 3252 | `private boolean charterExempt` |  |
| 3281 | `private double operatingCost` | The branches' operating cost this month, inside getUpkeep(): what leaves the audited pools rather than reaching the builders. |
| 3319 | `private double accountFeeBase` | ACCOUNT_FEE in today's unit - reseeded and reformed with the other money constants, and struck at the expected price level since 0.7.42. |
| 3328 | `private double accountFees, loanFeesPaid, loanFeesOwed` | The month's fees: accounts, loans paid in cash (a business's), and loans added to what is owed (a household's). |
| 3338 | `private double customers` | The households paying this month's account fee (0.7.19): the bank's customers, whom its branches share. |
| 3382 | `private double depositRate` |  |
| 3383 | `private double depositInterestToHouseholds` |  |
| 3384 | `private double depositInterestToSectors` |  |
| 3394 | `private double depositInterestToForeign` | ...and what the hot money is paid for parking here. |
| 3402 | `private boolean depositPayoutHeld` |  |
| 3452 | `private double fundingRate` | THE PRICE OF WHOLESALE MONEY WAS TWO DIALS until 0.7.0: FUNDING_SPREAD, two points over the risk-free rate "for being a bank rather than a treasury", and FUNDING_STRETCH, six points more at a reach of one deposit book... |
| 3492 | `private java.util.function.DoubleSupplier settledWindow` |  |
| 3497 | `private java.util.function.DoubleSupplier windowInterestDue` |  |
| 3504 | `private double foreignWholesale, foreignWholesaleIn, foreignWholesaleOut` |  |
| 3505 | `private double foreignFundingCost, windowFundingCost` |  |
| 3506 | `private double foreignFundingRate` |  |
| 3507 | `private double lastForeignShare` |  |
| 3509 | `private java.util.function.DoubleSupplier governmentCollateral` |  |
| 3854 | `private double chosenDepositRate` | The rate the bank chose this month, before rule 2 asked whether its margin could pay it. |
| 3906 | `private int monthsPayoutHeld` | HOW OFTEN RULE 2 HELD THE SAVERS UNDER THE CHOSEN RATE - counted for the run, not saved, for the reason the bid-up's counter was: a rule that never binds looks exactly like one that does not exist. |
| 4055 | `private double taxPaid` | Jerus: "quick question banks are taxed right?" |
| 4056 | `private double profitLastMonth` |  |
| 4136 | `private double struckProfit, carriedLate, restoredLate` | PROFIT THAT LANDS AFTER THE CLOSE (0.7.7). |
| 4137 | `private boolean closedThisMonth` |  |
| 4172 | `private double lastPayroll, lastUpkeep, lastInterest, lastBook` |  |
| 4175 | `private double lastOperating, lastUpkeepPerLater` | Last month's operating cost and what one later branch carried of it (revised 0.7.19): see laterBranchCost(). |
| 4178 | `private double lastKept` | Last month's interest margin and fees - what its book KEPT, which the branch test asked of a counter from 0.7.7 to 0.7.18; still struck and saved, and read by no rule since. |
| 4503 | `private final java.util.Map<String, Double> sectorAllowance` | Each sector's allowance by name, what each held when the month opened, and what each was written off by this month. |
| 4504 | `private final java.util.Map<String, Double> openingSectorAllowance` |  |
| 4505 | `private final java.util.Map<String, Double> writtenOffBySector` |  |
| 4507 | `private final java.util.Set<String> sectorsWatched` | The sectors whose books are in stage 2, as the last provide() found them. |
| 4509 | `private final java.util.Map<String, Double> stageTwoShares` | ...and the share of each sector's book in stage 2, firm by firm (0.7.8). |
| 4512 | `private double householdAllowance, openingHouseholdAllowance, householdWrittenOff, householdWatchedDebt` | The families' allowance, the same three, and how much of their debt is in cells in trouble. |
| 4515 | `private double openingAllowance, allowanceUsed` | The whole allowance as the month opened, and how much of the month's write-offs it covered. |
| 4627 | `private final java.util.Map<String, double[]> allowanceReadings` |  |
| 4688 | `private boolean conservationBufferRequired` | Whether the Basel III conservation buffer is the city's rule this month: true until Game says otherwise, as in every city that knows L8. |
| 4707 | `private final double[] lossRing` | The last year's provisions and weighted book, a ring of the months the bank had a branch. |
| 4708 | `private final double[] riskRing` |  |
| 4709 | `private int lossMonths` |  |
| 4711 | `private double worstLossRate` | The worst year's provisions over its average weighted book it has recorded: the loss the buffer is sized to take. |
| 5007 | `private double payoutProfit, payoutExcess, payoutOverTarget` | What the month's payout read: the profit after tax it was paid on, what the bank held over the top of its band, and over its target. |
| 5043 | `private double sharesBoughtBack, sharesIssued` | THE DESK DEALS IN THE BANK'S OWN SHARES BY ITS CAPITAL RULE (0.7.8): it buys them back from whoever sells while the bank is at or over its own target (buysBackOwnShares()) and only with what it holds over it (buybackR... |
| 5163 | `private final double[] dividendRing` | The owners' year: dividends and buybacks, a ring of months with this one in it. |
| 5164 | `private final double[] buybackRing` |  |
| 5165 | `private int payoutMonths` |  |
| 5658 | `private transient java.util.function.DoubleSupplier newBranchExtra` | THE NEW BRANCH'S OWN CREW (QN1, 2026-11-19; docs/queue/QN1.md, REV-R2e-6 finding 4). |
| 5758 | `private int uncoveredMonths` | Closed months in a row the fees have not covered the branches past the first; struck at closeMonth(), carried in lastMonthToSave(). |
| 6233 | `private double interestFromBusinesses, interestFromCity, interestFromHouseholds, discountAccreted` | The month's interest by who paid it: the businesses, the city's coupons, the families' credit lines, and the discount on the city's paper as it is earned. |
| 6267 | `private double treasuryBuybackGain` | What the treasury buying its paper back gained the bank (negative: lost it) since the month opened. |
| 6276 | `private double allowanceOpened` | The allowance a save from before 0.7.8 was given on load (openAllowance()): its equity fell by it between two presses. |
| 6284 | `private double bailoutsLifetime` | What the city has put into it in rescues over its life - resolutions since 0.7.14 (takeResolutionCapital()), gifts before - carried in the solvency record since 0.7.9; a save from before counts from its load. |
| 6293 | `private boolean monthKnown` | True once the month's lines are a month's: one played, or lines a save carried. |
| 6382 | `private final double[][] statementRing` | The months before this one, a year of them less this one: each filed whole at the top of the month after (startMonth()), when everything booked after its close is in it. |
| 6383 | `private int statementsFiled` |  |
| 6663 | `private final double[][] sheetRing` | The sheet at the top of each of the last YEAR_MONTHS months - every line, then the loans and the interim financing by sector - filed at startMonth() before anything moves, so the oldest is the sheet exactly a year bef... |
| 6664 | `private int sheetsFiled` |  |
| 7055 | `private double paidInOpening, retainedOpening` | ITS EQUITY, IN TWO PARTS (0.7.13, round 2). |
| 7056 | `private boolean splitKnown` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 7057 | **type** `public class Bank` | The city's commercial bank: every loan in it, and every default. |
| 76 | 1 | `public BankRules rules()` |  |
| 77 | 1 | `public double windowSpread()` |  |
| 78 | 1 | `public double mortgageLoanToCost()` |  |
| 79 | 1 | `public double businessLoanToAssets()` |  |
| 80 | 1 | `public double capitalMinimum()` |  |
| 81 | 1 | `public double leverageMinimum()` |  |
| 82 | 1 | `public double reserveRequired()` |  |
| 83 | 1 | `public double usableDeposits()` |  |

### the dials (lines 85-203)

### what a dollar of book WEIGHS (lines 204-296)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 4 | `public static double maturityWeight(double remainingMonths)` | How much of its face a loan with this long left to run counts for. |

### the position (lines 297-338)

### the month's working (lines 339-395)

### the month (lines 396-549)

| line | len | member | says |
|---:|---:|---|---|
| 418 | 28 | `public void refresh(double branches, double householdSavings, double sectorCash, double sectorBook, double cityBook, double hou...` | Re-reads the city and re-prices credit. |
| 455 | 5 | `public void setWeightedBook(double sector, double city, double household)` | The same book, weighed by what each loan actually is. |
| 466 | 4 | `public void setMortgageBook(double insured, double weighted)` | ...and the insured mortgages inside the businesses' book (0.7.11), which the weight table shows at RISK_INSURED_MORTGAGE. |
| 472 | 1 | `public double getMortgageBook()` | The insured mortgages it holds, part of getSectorBook(). |
| 481 | 8 | `public void setSectorLoans(double[] loans, double[] interim)` | ...and the rest of the businesses' book by sector, with the interim financing apart (0.7.13): for each sector in Sectors.KEYS order, its loans that are neither an insured mortgage nor interim financing, and its interi... |
| 492 | 10 | **type** `public record HeldBooks(double deposits, double sectorBook, double cityBook, double householdBook, double m...` | The held local-money book amounts read by the next startMonth's sheet and opening equity. |
| 495 | 4 | `{ ... }` _(in Bank.HeldBooks)_ |  |
| 499 | 1 | `public double[] loansBySector()` _(in Bank.HeldBooks)_ |  |
| 500 | 1 | `public double[] interimBySector()` _(in Bank.HeldBooks)_ |  |
| 503 | 4 | `public HeldBooks heldBooksForSave()` |  |
| 509 | 15 | `public void restoreHeldBooks(HeldBooks saved)` | Put the held books over the load's final refresh and deposit reconstruction, without settling anything. |
| 526 | 1 | `public double getLoansToSector(int sector)` | What the sector at this index of Sectors.KEYS owes it outside its insured mortgages and its interim financing. |
| 528 | 1 | `public double getInterimToSector(int sector)` | ...and its interim financing. |
| 530 | 1 | `public double getInterimBook()` | The interim financing it holds, every sector's: part of getSectorBook(). |
| 532 | 1 | `public double getBusinessLoans()` | What the businesses owe it outside the insured mortgages and the interim financing: the sector book less both. |
| 541 | 5 | `public void receiveInsuranceClaim(double amount)` | THE CITY PAYS AN INSURED LOSS (0.7.11): cash from the treasury for the part of a borrower's write-down that came off an insured mortgage. |
| 548 | 1 | `public double getInsuranceClaims()` | What the treasury paid it on insured mortgages this month. |

### THE CORPORATE BONDS IT HOLDS (0.7.12) (lines 550-640)

| line | len | member | says |
|---:|---:|---|---|
| 583 | 5 | `public void setBondBook(double carrying, double weighted, double face)` | The bonds on its book as the market holds them - what they cost it, weighed, and their face - set by Game.refreshBank() beside the other books. |
| 590 | 6 | `public void buyBond(double cost, double weighted)` | It buys a bond: cash out, the bond on its book at what it paid, and what that weighs (RISK_BUSINESS for the months left, as Game.refreshBank() weighs it). |
| 598 | 7 | `public void sellBond(double proceeds, double cost)` | It sells one: cash in, what it cost off its book, the difference a gain or a loss this month. |
| 607 | 1 | `public void redeemBond(double face, double cost)` | ...or its issuer repays it at maturity: the face in, what it cost off its book. |
| 610 | 5 | `public void bondsWrittenDown(double cost)` | A default took this much of what its bonds cost it off its book: the write-off is booked with the issuer's loans' (writeOffSector()). |
| 617 | 7 | `public void takeBondCoupons(double amount)` | The coupons on its bonds this month: interest from a business, inside the pools. |
| 626 | 5 | `public void takeUnderwriting(double fee)` | Its fee for bringing an issue - the issue's costs, out of the proceeds: a transfer between two pools. |
| 633 | 1 | `public double getBondBook()` | What its bonds cost it, on its book; their face; what they weigh. |
| 634 | 1 | `public double getBondFace()` |  |
| 635 | 1 | `public double getBondWeighted()` |  |
| 637 | 1 | `public double getBondGains()` | The month's gain on bonds sold or repaid, its underwriting fees, and its coupons. |
| 638 | 1 | `public double getUnderwritingFees()` |  |
| 639 | 1 | `public double getInterestFromBonds()` |  |

### carry (lines 641-700)

| line | len | member | says |
|---:|---:|---|---|
| 653 | 6 | `public void lendCarry(double amount)` | Lend to a foreigner who is about to take it abroad. |
| 661 | 7 | `public void repayCarry(double amount)` | ...and they bring it home. |
| 678 | 6 | `public void takeCarryInterest(double amount)` | The coupon, which arrives from abroad. |
| 694 | 1 | `public void setCarryBook(double amount)` | The carry book, set from the stock that owns it. |
| 696 | 1 | `public double getCarryBook()` |  |
| 697 | 1 | `public double getCarryLent()` |  |
| 698 | 1 | `public double getCarryRepaid()` |  |
| 699 | 1 | `public double getCarryInterest()` |  |

### WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7) (lines 701-790)

### the four parts (lines 791-944)

| line | len | member | says |
|---:|---:|---|---|
| 805 | 3 | `public double windowShare()` | The share of the bank's marginal money that comes from the window, 0 to 1: struck at the close from how the month ended funded - what it owed the window over everything it had borrowed, 0 while it holds reserves - and... |
| 816 | 6 | `public double fundsTransferPrice(double policyAnnual, int months)` | THE FUNDS-TRANSFER PRICE: what a dollar lent for this many months costs the bank - the policy rate, plus the window's penalty on the share of its money that is the window's, plus the city's term premium for the term (... |
| 840 | 1 | `public double runningCostRate()` | THE RUNNING COSTS: the last year's payroll and upkeep over the book they served, a year - or over what the bank's capital could carry at its own target (capitalTarget(); a fixed 11% until 0.7.8), whichever is larger. |
| 873 | 1 | `public double expectedLossRate()` | THE EXPECTED LOSS: BASE_LOSS_RATE, what a sound book loses a year through the cycle. |
| 876 | 3 | `public static double requiredReturn()` | The return the bank's owners are priced to want: Equity.requiredYield() at the world's rate, 12.5% at the defaults. |
| 890 | 4 | `public double capitalCharge(double policyAnnual, int months, double riskWeight)` | THE CAPITAL CHARGE: the equity a loan of this risk ties up at the bank's own capital target (capitalPerDollar(); the target is capitalTarget() since 0.7.8, and before that the minimum plus a fixed three points), times... |
| 910 | 3 | `public double capitalPerDollar(double riskWeight)` | THE EQUITY A DOLLAR LENT TIES UP at the bank's own target: its risk weight times capitalTarget(), and never less than the leverage requirement on the same dollar, leverageTarget() (0.7.11, round 2). |
| 922 | 4 | `public double capitalPerDollar(double riskWeight, String sector)` | ...A DOLLAR LENT TO THIS SECTOR (0.7.12): the same formula with the capital its concentration adds - concentrationPerDollar(), the marginal add-on at the minimum, carried at the bank's own target in the proportion its... |
| 928 | 4 | `public double capitalCharge(double policyAnnual, int months, double riskWeight, String sector)` | THE CAPITAL CHARGE ON A LOAN TO THIS SECTOR (0.7.12): capitalPerDollar() with its concentration, at the owners' return over the money. |
| 940 | 4 | `public double concentrationCharge(double policyAnnual, int months, String sector)` | WHAT THE BOOK'S CONCENTRATION ADDS TO A LOAN TO THIS SECTOR, a year: its capital charge with the concentration less its capital charge without - the one formula, read twice. |

### THE BANK PRICES CONCENTRATION (0.7.12) (lines 945-1289)

| line | len | member | says |
|---:|---:|---|---|
| 1052 | 5 | `public static double irbCorrelation(double pd)` | The IRB corporate asset correlation at this default rate, R(PD): 24% for the soundest, 12% for the riskiest, blended by exp(-50 PD). |
| 1059 | 4 | `public static double effectiveCorrelation(double pd, double herfindahl)` | ...and the book's, at its sector Herfindahl index: R(PD) x (1 + (SECTOR_CORRELATION_MULTIPLIER - 1) x H). |
| 1065 | 10 | `public static double inverseNormal(double p)` | The inverse of the standard normal distribution, by bisection on BusinessDebtManager.normalCdf() - the game's one N - to the last bit. |
| 1080 | 5 | `public static double maturityAdjustment(double pd)` | The maturity adjustment at IRB_MATURITY_YEARS: (1 + (M - 2.5) b) / (1 - 1.5 b), b = (IRB_MATURITY_A - IRB_MATURITY_B ln PD)^2. |
| 1087 | 8 | `public static double irbCapital(double pd, double lgd, double rho)` | THE IRB CAPITAL a dollar needs at the minimum, K(PD, LGD, rho): the loss at the 99.9th percentile year less the expected loss, times the maturity adjustment. |
| 1097 | 11 | `public static double irbCapitalSlope(double pd, double lgd, double rho)` | ...its slope in the correlation, dK/drho: what the Euler rule's second term reads. |
| 1110 | 1 | **type** `public record Exposure(double amount, double pd, double lgd)` | One sector's exposure as the concentration reads it: what the bank stands to lose on it, its default rate, and the loss given default of what it holds. |
| 1122 | 43 | `public void setConcentration(java.util.Map<String, Exposure> bySector)` | THE BOOK'S CONCENTRATION, struck from every sector's exposure: its Herfindahl index, the add-on at the minimum and the weight it adds, and each sector's marginal add-on by the Euler rule. |
| 1167 | 3 | `public double concentrationPerDollar(String sector)` | A dollar lent to this sector: the capital its concentration adds at the minimum, the Euler rule's marginal add-on. |
| 1172 | 1 | `public double getConcentrationHerfindahl()` | The book's sector Herfindahl index, the add-on at the minimum, the weight it adds and the exposure it was struck on. |
| 1173 | 1 | `public double getConcentrationAddOn()` |  |
| 1174 | 1 | `public double getConcentrationWeighted()` |  |
| 1175 | 1 | `public double getConcentrationExposure()` |  |
| 1177 | 1 | `public Exposure getExposure(String sector)` | One sector's exposure as the book was struck, or null. |
| 1180 | 4 | `public double loanRate(double policyAnnual, int months, double riskWeight)` | A loan of this term and risk weight: the four parts, added up. |
| 1186 | 3 | `public double prime(double policyAnnual)` | PRIME: what a sound business pays - the four parts at RISK_BUSINESS, for a business loan's term. |
| 1207 | 5 | `public double insuredMortgageRate(double policyAnnual)` | AN INSURED MORTGAGE (0.7.11): what the money costs for its term - the funds-transfer price at Mortgage.MORTGAGE_TERM_MONTHS, the curve's ten-year point - running the bank, and the capital it ties up; no expected loss,... |
| 1214 | 3 | `public double householdRate(double policyAnnual)` | What a household's credit line starts from: the four parts at RISK_HOUSEHOLD, revolving, so no term premium. |
| 1245 | 3 | `public double cardRate(double policyAnnual)` | The card's base price; Household adds its holders' own risk and enforces the cap. |
| 1250 | 5 | `public static double cardAccess(double income, PayTier tier, double shutOut)` | No card without positive income; a charge-off excludes its share, not its neighbours. |
| 1257 | 5 | `public static double cardAllowance(double debt, double late)` | Performing balances' annual loss plus late balances' charge-off before cure. |
| 1269 | 3 | `public double lendingRate(double policyAnnual)` | What a good credit pays to borrow here: prime, since 0.7.7. |
| 1285 | 4 | `public double carryRate(double policyAnnual)` | What the carry trade pays to borrow here: the same costs, at RISK_CARRY and short, since the money is taken abroad and wanted back on demand - with NO expected loss, because Jerus's call is that a carry borrower never... |

### what the four parts remember (lines 1290-1530)

| line | len | member | says |
|---:|---:|---|---|
| 1305 | 19 | `private void strikePrices()` | Adds the month that has just closed to the rings and strikes the two parts that read flows. |
| 1326 | 10 | `public double[] pricingHistoryToSave()` | The record, for the save: the count, then the two cost rings. |
| 1338 | 9 | `public void restorePricingHistory(double[] in)` | ...and back. |
| 1391 | 1 | `public double marginalCostOfFunds()` | WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND. |
| 1413 | 8 | `private double blendedCostOfFunds()` | The two tranches, blended at the weights the bank actually used them. |
| 1423 | 107 | `public void startMonth()` | Clears the month's flows. |

### the arithmetic (lines 1531-1560)

| line | len | member | says |
|---:|---:|---|---|
| 1543 | 1 | `public double getBook()` | Everything lent, carry included. |
| 1552 | 8 | `public double getWeightedBook()` | The book as CAPACITY sees it: risk- and maturity-weighted. |

### THE TRADING DESK (lines 1561-1638)

| line | len | member | says |
|---:|---:|---|---|
| 1603 | 5 | `public void markSecurities(double value)` | The exchange re-marks the inventory. |
| 1610 | 5 | `public void deskPays(double cash)` | The desk paid cash for shares. |
| 1617 | 5 | `public void deskReceives(double cash)` | ...and was paid for shares it sold. |
| 1624 | 5 | `public void receiveDividend(double amount)` | Dividends on the inventory: income, and cash. |
| 1631 | 1 | `public void restoreSecurities(double value)` | The load path puts the mark back without calling it income. |
| 1633 | 1 | `public double getSecurities()` |  |
| 1634 | 1 | `public double getTradingIncome()` |  |
| 1637 | 1 | `public double getMarkChange()` | What re-marking the inventory did to this month's trading result. |

### the desk held to its capital (0.7.8) (lines 1639-2272)

| line | len | member | says |
|---:|---:|---|---|
| 1699 | 4 | `public double deskCanCarry(double inventory, double price, double mark)` | HOW MANY OF A COMPANY'S SHARES THE DESK MAY STILL BUY ON THE CAPITAL IT HAS: as many as leave the bank at or over its target (targetEquity()) with them on its books - the shares carried at the weight the weighted book... |
| 1705 | 18 | `double deskCapitalCanCarry(double inventory, double price, double mark)` | Capital-only share room, kept separate so an actual posting can name the cash limit that binds it. |
| 1725 | 3 | `public double stableDeskRoom()` | Cash plus core deposits left for customer market-making after hot money leaves. |
| 1730 | 4 | `public double weightingRelief()` | How much lighter the weighting makes the book. |
| 1745 | 12 | `public double capacity()` | The most the bank can lend before it is funding itself at the central bank's window (abroad, until 0.7.0). |
| 1771 | 4 | `public double depositsGathered()` | What the bank has gathered: everything the city has banked with it - the households' savings and the businesses' cash - PLUS whatever the world has parked here. |
| 1779 | 1 | `public double getForeignDeposits()` | Hot money currently funding this bank, in local money. |
| 1782 | 4 | `public void setForeignDeposits(double amount)` | Told by Game each month, from CapitalFlows. |
| 1794 | 5 | `public void receiveHotMoney(double amount)` | Hot money arriving, as cash. |
| 1808 | 5 | `public void returnHotMoney(double amount)` | ...and leaving, which is the whole danger. |
| 1816 | 1 | `public double getHotMoneyIn()` |  |
| 1817 | 1 | `public double getHotMoneyOut()` |  |
| 1831 | 4 | `public double hotFundingShare()` | The share of the bank's funding that could leave at any time. |
| 1837 | 3 | `public double capitalLimit()` | What its capital supports, on the risk-weighted book - at the book's present mix, under the larger of the two requirements since round 2 of 0.7.11 (weightedBookSupportedBy()). |
| 1852 | 5 | `private double weightedBookSupportedBy(double equity)` | THE WEIGHTED BOOK THIS MUCH EQUITY SUPPORTS: equity over CAPITAL_RATIO while the risk-based minimum is the larger. |
| 1859 | 3 | `public boolean capitalBound()` | True when it is the capital that binds rather than the funding. |
| 1869 | 4 | `public double capitalRatio()` | Equity against the risk-weighted book - the ratio a regulator reads. |
| 1899 | 1 | `public double getShortfallThisMonth()` | The hole it failed with this month; nothing in a month it did not fail. |
| 1901 | 1 | `public double getShortfallLifetime()` | ...and over its life. |
| 1903 | 1 | `public boolean isInResolution()` | True while it waits for the city: failed, frozen, not yet resolved. |
| 1918 | 1 | `public boolean isInsolvent()` |  |
| 1920 | 1 | `public double getResolutionLoss()` | Over the city's life. |
| 1922 | 1 | `public double getResolutionLossThisMonth()` | This month only. |
| 1923 | 1 | `public int getFailures()` |  |
| 1966 | 6 | `public double[] solvencyToSave()` | The solvency record, for the save. |
| 1973 | 10 | `public void restoreSolvency(double[] state)` |  |
| 2010 | 26 | `public void resolveIfFailed()` | The bank fails: it is frozen, and it waits for the city. |
| 2046 | 4 | `private double exitTolerance(double moved)` | How near its exit level a frozen bank's equity must be to count as there: a hair of the largest figure that struck it - its balance sheet, or the capital just paid in - what those sums round by. |
| 2062 | 23 | `public double resolutionExitEquity()` | The equity at which the freeze lifts, however the bank gets there. |
| 2110 | 9 | `public double recapitalisationNeeded()` | What it would take to put the bank back on its feet. |
| 2147 | 5 | `public double domesticCapitalShare()` | The share of new paid-in capital the city's own savers can find. |
| 2176 | 13 | `public void injectCapital(double amount)` | Shareholders' money, put in as capital - and WHOSE shareholders. |
| 2200 | 7 | `public void injectCapital(double fromHome, double fromAbroad)` | Shareholders' money, put in as capital, and whose: the households' part came out of their savings through the register, the rest from the world. |
| 2215 | 6 | `public void payDividend(double amount)` | Pays the owners. |
| 2225 | 1 | `public double getDividendsPaid()` | What it paid its shareholders this month. |
| 2262 | 10 | `public void takeResolutionCapital(double amount)` | The city's capital in a resolution (0.7.14): the treasury's money, for every common share (Game.resolveBank()). |

### THE CITY'S CAPITAL (0.7.14): A RESOLUTION, AND THE PREFERRED (lines 2273-2896)

| line | len | member | says |
|---:|---:|---|---|
| 2380 | 39 | **type** `public static final class Preferred implements Cloneable` | One block of the city's senior preferred, as it was bought: what is outstanding at par, the dividends accrued on it and unpaid, the month it was issued, the common dividend a share a month it may not rise past while t... |
| 2390 | 1 | `Preferred()` _(in Bank.Preferred)_ |  |
| 2393 | 7 | `Preferred copy()` _(in Bank.Preferred)_ | A copy that shares nothing with this block (UP1, 2026-10-22): the autosave's, while the live block's arrears and par go on changing. |
| 2401 | 1 | `public double par()` _(in Bank.Preferred)_ |  |
| 2402 | 1 | `public double arrears()` _(in Bank.Preferred)_ |  |
| 2403 | 1 | `public int issued()` _(in Bank.Preferred)_ |  |
| 2404 | 1 | `public double capPerShare()` _(in Bank.Preferred)_ |  |
| 2405 | 1 | `public double warrantShares()` _(in Bank.Preferred)_ |  |
| 2406 | 1 | `public double strike()` _(in Bank.Preferred)_ |  |
| 2407 | 1 | `public int warrantsExpire()` _(in Bank.Preferred)_ |  |
| 2408 | 1 | `public boolean warrantsOut()` _(in Bank.Preferred)_ |  |
| 2411 | 1 | `public double rate(int month)` _(in Bank.Preferred)_ | Its dividend rate this month: PREFERRED_RATE, PREFERRED_STEP_RATE from the fifth anniversary. |
| 2414 | 1 | `public boolean inConsent(int month)` _(in Bank.Preferred)_ | True while the city's consent binds: outstanding, and under three years old. |
| 2417 | 1 | `public boolean due(int month)` _(in Bank.Preferred)_ | True once it is due to be redeemed: outstanding, at or past its third anniversary. |
| 2437 | 1 | `public void setMonth(int month)` | The city's month, for the preferred's anniversaries. |
| 2438 | 1 | `public int getMonth()` |  |
| 2441 | 5 | `public double preferredOutstanding()` | The city's preferred outstanding at par, every block: an equity line of its own ("Preferred shares (the city)"). |
| 2448 | 1 | `public java.util.List<Preferred> getPreferred()` | Every block, oldest first. |
| 2451 | 5 | `public double getPreferredArrears()` | The preferred dividends owed and unpaid, every block together: not a liability on the sheet until paid, as preferred arrears are not. |
| 2458 | 4 | `public boolean inConsentPeriod()` | True while any block's consent binds: no buyback, no rise in the common dividend a share. |
| 2464 | 5 | `public boolean wantsPreferred()` | True when a standing bank is under its minimum and would ask the city for preferred. |
| 2471 | 1 | `private double preferredNeed()` | What it takes back to its target: recapitalisationNeeded() for a standing bank under its minimum. |
| 2479 | 6 | `public double preferredOfferSize()` | WHAT IT ASKS THE CITY FOR: what takes it back to its target, held between PREFERRED_MIN_SHARE and PREFERRED_MAX_SHARE of its risk-weighted assets (getWeightedBook()). |
| 2487 | 3 | `public boolean preferredOfferCapped()` | True when 3% of its risk-weighted assets does not reach its target: it asks for 3%, and the rest is left to its own share issues. |
| 2492 | 3 | `public double preferredOfferShortOfTarget()` | ...and what the 3% leaves short of the target. |
| 2506 | 16 | `public double issuePreferred(double par, double price, double capPerShare)` | THE CITY BUYS ITS PREFERRED: cash in at par, an equity line of its own, warrants on WARRANT_SHARE of the par at `price` - last month's price of a share, which is their strike - for WARRANT_TERM_MONTHS. |
| 2544 | 24 | `public double payPreferredDividends()` | THE MONTH'S PREFERRED DIVIDEND: every block accrues a twelfth of its rate on its par, into its own arrears, and the bank pays what it can of them, oldest block first - standing, and only from what it holds over its ta... |
| 2600 | 33 | `public double[] redeemDuePreferred(java.util.function.DoubleUnaryOperator offering)` | THE BLOCKS AT THEIR THIRD ANNIVERSARY ARE REDEEMED WHOLE (0.7.14; Jerus: "Sell new shares to repay"), oldest first, at the term sheet's price - "All redemptions of the Senior Preferred shall be at 100% of its issue pr... |
| 2652 | 31 | `public double repurchaseWarrants(double price, double sigma, double riskFree, java.util.function.DoubleUnaryOperator offering)` | THE CITY'S WARRANTS BOUGHT BACK, once no preferred is left: every warrant still out, at its fair value (warrantValue(), Black-Scholes) - the term sheet's "Following the redemption in whole of the Senior Preferred held... |
| 2685 | 1 | `public double getRepaymentRaisedThisMonth()` | What the offerings to repay the city raised this month, and over the bank's life. |
| 2686 | 1 | `public double getRepaymentRaisedLifetime()` |  |
| 2688 | 1 | `public int getRepaymentShortLifetime()` | How often a repayment was left part-paid, the public not taking the whole offering: once a month for each due block short, and once for the warrants. |
| 2699 | 12 | `public double exerciseExpiredWarrants(double price)` | WARRANTS AT THEIR EXPIRY, still out: exercised if in the money, cashless - the city takes new shares worth what the warrants are over the strike, warrantShares x (price - strike) / price - and gone if not. |
| 2713 | 5 | `private double warrantValue(Preferred p, double price, double sigma, double riskFree)` | What one block's warrants are worth: Black-Scholes on the shares they reach, for the term they have left. |
| 2720 | 5 | `public double warrantValue(double price, double sigma, double riskFree)` | Every block's warrants still out, at their value: the city's fund's mark on them. |
| 2727 | 5 | `public double warrantSharesOut()` | How many shares the warrants still out reach. |
| 2740 | 6 | `public double[] cancelPreferred()` | THE HOLE TOOK THEM: every block of preferred and its warrants cancelled at a failure - they were capital. |
| 2754 | 5 | `public void wipeOwners()` | THE OLD OWNERS ARE WIPED OUT: their paid-in capital is written off against the losses, before the city's capital goes in - losses fall on common shareholders first (CDIC). |
| 2761 | 8 | `public void splitShares(double k)` | A split or consolidation of its shares: the dividend cap a share and the warrants' strike by the inverse, their count by the factor. |
| 2770 | 1 | `public double getPreferredIn()` |  |
| 2771 | 1 | `public double getPreferredRedeemedThisMonth()` |  |
| 2772 | 1 | `public double getPreferredDividendsThisMonth()` |  |
| 2773 | 1 | `public double getPreferredAccruedThisMonth()` |  |
| 2774 | 1 | `public double getWarrantsBoughtBackThisMonth()` |  |
| 2775 | 1 | `public double getOwnersWipedThisMonth()` |  |
| 2776 | 1 | `public double getPreferredCancelledThisMonth()` |  |
| 2777 | 1 | `public double getPreferredDividendsLifetime()` |  |
| 2778 | 1 | `public double getPreferredRedeemedLifetime()` |  |
| 2779 | 1 | `public double getWarrantsBoughtBackLifetime()` |  |
| 2782 | 4 | `public double[] preferredRecordToSave()` | The preferred's record, for the save: the arrears (the blocks carry their own; this is their sum, for reading), the three lifetime figures and what its offerings to repay raised. |
| 2788 | 14 | `public void restorePreferred(java.util.List<Preferred> blocks, double[] record)` | ...and back, with the blocks. |
| 2812 | 1 | `public double getFoundingSettlement()` | The one-off jump in the bank's cash when the city's first branch opens and it takes over the standing loan book. |
| 2815 | 1 | `public double getCapitalInjected()` | Shareholders' money from OUTSIDE the city. |
| 2824 | 1 | `public double getCapitalFromHome()` | ...and from the city's own savers. |
| 2827 | 1 | `public double getBailoutReceived()` | The treasury's money - a resolution's since 0.7.14, a gift before - from inside it. |
| 2841 | 36 | `public double openBranches(double nowStanding)` | Opens whatever branches have been built since last month, and says what capital they need. |
| 2879 | 1 | `public double getBranchesCapitalised()` |  |
| 2880 | 1 | `public void setBranchesCapitalised(double n)` |  |
| 2891 | 5 | `public double strain()` | Book over capacity. |

### the flows (lines 2897-2971)

| line | len | member | says |
|---:|---:|---|---|
| 2903 | 6 | `public void takeInterest(double amount)` | Interest arriving from a SECTOR or the CITY - both inside the audit, so this is a transfer between pools and declares nothing. |
| 2913 | 1 | `public double getInternalInterest()` | The part of the month's interest that came from inside the city's pools. |
| 2922 | 6 | `public void takeDiscount(double amount)` | Paper bought below par: income the bank earns without any cash moving. |
| 2947 | 3 | `public double sellPaperBack(double price, double principal)` | THE TREASURY BUYS ITS PAPER BACK FROM THE BANK (0.7.0), which is who holds it: the price arrives as cash, the book drops by the principal at once rather than at the next refresh, and the difference is the bank's gain ... |
| 2958 | 13 | `public double sellPaperBack(double price, double principal, double unearned)` | ...and since 0.7.1 at amortised cost: the unearned discount riding on the face that leaves goes with it, so the gain is the price less what the book carried the paper at - face less the discount not yet earned. |

### THE CITY'S PAPER CHANGES HANDS (0.7.1) (lines 2972-3283)

| line | len | member | says |
|---:|---:|---|---|
| 3002 | 1 | `public double getPaperSoldToCentralBank()` |  |
| 3003 | 1 | `public double getPaperBoughtFromCentralBank()` |  |
| 3016 | 1 | `public void setUnearnedDiscount(double amount)` |  |
| 3017 | 1 | `public double getUnearnedDiscount()` |  |
| 3018 | 1 | `public double getPaperGains()` |  |
| 3019 | 1 | `public double getPaperBoughtFromHouseholds()` |  |
| 3020 | 1 | `public double getPaperBoughtFromFirms()` |  |
| 3022 | 6 | `private void addToCityBook(double face)` |  |
| 3030 | 5 | `public double buyPaperFromHouseholds(double price, double face, double unearned)` | A household sells this face to the desk for this price. |
| 3043 | 5 | `public double buyPaperFromFirms(double price, double face, double unearned)` | A business sells this face to the desk for this price (CD2, slice 4): the households' trade, and the same book entry. |
| 3057 | 4 | `public double buyPaperFromWorld(double price, double face, double unearned)` | The world sells this face to the desk for this price (CD2, slice 5): the households' trade, whatever the bank's room (as it buys theirs), and the same book entry. |
| 3063 | 8 | `private double takePaperOntoTheBook(double price, double face, double unearned)` | The desk pays this price and the face joins its book with the unearned discount riding on it: the month's gain or loss is the difference. |
| 3073 | 11 | `public double sellPaperToCentralBank(double price, double face, double unearned)` | The central bank buys this face from the bank's book, in money it made. |
| 3086 | 10 | `public double buyPaperFromCentralBank(double price, double face, double unearned)` | ...and sells it back: the bank pays, and the face returns to its book. |
| 3123 | 4 | `public void restrikeDiscount(double gain)` | THE DISCOUNT RE-STRUCK (BK1): the part of the month's gains on the city's paper that is the pooled discount re-blending at a reopening, not a trade. |
| 3162 | 5 | `public void restrikeCarriedDiscount(double gain)` | THE DISCOUNT RE-STRUCK WHERE IT HAPPENS (BD1): the gain or loss from the pooled discount moving the book's figure for the bank (DebtManager.bankUnearnedDiscount()) by more or less than the trade that moved it carried,... |
| 3170 | 1 | `public double getDiscountRestruck()` |  |
| 3174 | 1 | `public double getBuybackGains()` |  |
| 3177 | 4 | `public void takeRepayment(double amount)` | Principal coming back. |
| 3183 | 4 | `public void lend(double amount)` | Money out the door. |
| 3189 | 5 | `public void lendToHouseholds(double amount)` | Lending to a family, which crosses the audit's boundary and is counted. |
| 3196 | 4 | `public void takeFromHouseholds(double principal, double interest)` | ...and from a household, which is outside, so both halves are declared. |
| 3209 | 4 | `public void writeOff(double amount)` | A loan that will not be repaid. |
| 3220 | 5 | `public void writeOffSector(String sector, double amount)` | ...on a business's book, by name (0.7.8), so the month's provide() can draw it against the allowance that sector's book opened the month holding. |
| 3227 | 5 | `public void writeOffHouseholds(double amount)` | ...and on the families' book, the debts of those discharged this month. |
| 3234 | 3 | `public void payRunning(double payroll, double upkeep)` | Wages and running costs, which are real money leaving. |
| 3244 | 5 | `public void payRunning(double payroll, double repairs, double operating, double operatingPerLaterBranch)` | ...and with the operating cost one LATER branch carries (0.7.19, revised: the charter is exempt from it - see THE BRANCHES, BY THEIR CUSTOMERS). |
| 3273 | 6 | `public void payRunning(double payroll, double repairs, double operating)` | Wages, the branches' repairs and their operating costs (0.7.19): upkeep is the repairs and the running costs together, which is what every reader of it - the profit, the running-cost rate a loan is priced on, the bran... |
| 3282 | 1 | `public double getOperatingCost()` |  |

### FEES (0.7.7) (lines 3284-3358)

| line | len | member | says |
|---:|---:|---|---|
| 3322 | 4 | `public double accountFee(double priceIndex)` | A month's account fee per housed household, the base times this factor (Game passes 1 since 0.7.42: the base is struck at the expected price level; it passed the price index), in today's money - nothing in a city with... |
| 3331 | 5 | `public void takeAccountFees(double amount)` | The households' account fees, in cash from outside the pools. |
| 3339 | 1 | `public void setCustomers(double households)` |  |
| 3340 | 1 | `public double getCustomers()` |  |
| 3343 | 5 | `public void takeLoanFees(double amount)` | Loan fees a business paid out of its proceeds: cash from another pool. |
| 3350 | 4 | `public void bookLoanFees(double amount)` | Loan fees added to what the households owe: income now, no cash until they repay, and the book carries them from the next refresh. |
| 3355 | 1 | `public double getAccountFees()` |  |
| 3356 | 1 | `public double getLoanFeesPaid()` |  |
| 3357 | 1 | `public double getLoanFeesOwed()` |  |

### THE FUNDING SIDE (lines 3359-3767)

| line | len | member | says |
|---:|---:|---|---|
| 3397 | 1 | `public double depositRate()` | The rate savers are being paid, a year: the month's payout over the deposits - the rate the bank chose, unless its interest margin could not pay it (isDepositPayoutHeld()). |
| 3400 | 1 | `public boolean isDepositPayoutHeld()` | True when rule 2 of WHAT TO PAY SAVERS held the savers under the rate the bank chose - its margin, after its running costs or at the savers' share, could not pay it. |
| 3404 | 1 | `public double getDepositInterestToHouseholds()` |  |
| 3405 | 1 | `public double getDepositInterestToSectors()` |  |
| 3406 | 1 | `public double getDepositInterestToForeign()` |  |
| 3407 | 4 | `public double depositInterest()` |  |
| 3428 | 1 | `public double earningAssets()` | Everything that earns it interest, as it stands: its loans, the bonds and the city's paper it holds, the carry and the families' credit lines (getBook()), and its reserves at the central bank, which earn the policy ra... |
| 3436 | 4 | `public double netInterestMargin()` | What it charges borrowers and earns on its reserves, less what it pays savers, the window and foreign wholesale creditors, at a yearly rate on the assets that earn it interest (earningAssets()): a bank's whole busines... |
| 3455 | 1 | `public double borrowings()` | Everything it owes: the mirror of a negative cash position. |
| 3465 | 1 | `public double depositFunding()` | The cheap tranche - the city's own money, lent back out. |
| 3468 | 1 | `public double wholesaleFunding()` | Funding past deposits, split between the collateralised window and foreign wholesale. |
| 3494 | 1 | `public void setSettledWindow(java.util.function.DoubleSupplier outstanding)` | The central bank's actual claim, including the inherited claim of a newly loaded city. |
| 3495 | 1 | `public double getWindowOutstanding()` |  |
| 3498 | 1 | `public void setWindowInterestDue(java.util.function.DoubleSupplier due)` |  |
| 3499 | 1 | `public double getWindowInterestDue()` |  |
| 3502 | 1 | `public void payWindowInterest(double paid)` | Cash collected by the window; its expense was already accrued by fundToCover(). |
| 3510 | 1 | `public void setGovernmentCollateral(java.util.function.DoubleSupplier value)` |  |
| 3513 | 9 | `public double windowCollateral()` | Eligible domestic loans net of allowances and government paper at market, after haircuts; no equity or carry. |
| 3524 | 10 | `public double windowFunding()` | Window tranche: collateralised and with every dollar of equity inventory kept out of it. |
| 3536 | 1 | `public double windowCreditLimit()` | The ceiling on total issued principal, separate from the current funding request. |
| 3539 | 8 | `public double requiredStableFunding()` | NSFR approximation by book: the equity desk is wholly stable-funded, with no overnight allowance. |
| 3549 | 4 | `public double coreStableFunding()` | Own capital and core deposits; hot money and overnight advances have no stable value. |
| 3555 | 1 | `public double getForeignWholesale()` | The foreign wholesale liability actually settled, distinct from hot deposits. |
| 3556 | 1 | `public double getForeignWholesaleIn()` |  |
| 3557 | 1 | `public double getForeignWholesaleOut()` |  |
| 3558 | 1 | `public double getForeignFundingCost()` |  |
| 3559 | 1 | `public double getForeignFundingRate()` |  |
| 3560 | 1 | `public double foreignShare()` |  |
| 3561 | 1 | `public double getWindowFundingCost()` |  |
| 3564 | 7 | `public void settleForeignFunding(double window)` | Refinance the residual abroad; net cash already carries the borrowing, so only its gross pool moves. |
| 3573 | 4 | `public double[] fundingToSave()` | Stocks and flows kept together so loading cannot reconstruct a month's financing. |
| 3578 | 13 | `public void restoreFunding(double[] saved)` |  |
| 3593 | 1 | `public void setForeignFundingRate(double annual)` | Foreign lenders' term quote in local money, including the city's credit spread. |
| 3600 | 1 | `public double fundingRate()` | The weighted annual price of its external funding: the collateralised window at policy plus its saved spread and foreign term money at its local-money quote. |
| 3613 | 6 | `public void fundToCover(double policyAnnual)` | Settles the funding for the month: charge for what was borrowed, then borrow what is short or repay what is spare. |
| 3628 | 139 | `public void fundToCover(double policyAnnual, double windowDeferred, double windowHeld)` | ...and the advance the window holds past that tranche, which the last settle asked back and the central bank's reserves could not take (CentralBank.getWindowDeferred()): owed all the same, and priced at the window's r... |

### WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. (lines 3768-3915)

| line | len | member | says |
|---:|---:|---|---|
| 3839 | 7 | `public double fundingPosition()` | How the bank is funded, 0 to 1: 0 while it holds reserves (its cash is positive), 1 once it is borrowing at the window, and in between the share of what its branches gathered that it has lent out. |
| 3848 | 4 | `public double depositShare()` | The share of the policy rate the bank's funding asks it to pass on: between DEPOSIT_SHARE_FLUSH and DEPOSIT_SHARE_AT_WINDOW, by fundingPosition(). |
| 3855 | 1 | `public double getChosenDepositRate()` |  |
| 3867 | 19 | `private double chooseDepositRate(double policyAnnual)` | The month's deposit interest, in money. |
| 3897 | 7 | `public double depositRateAt(double policyAnnual)` | RULE 1 ON ITS OWN, at a policy rate of the caller's (0.7.36): the rate the bank would choose for its savers next month - the rate its funding asks for (depositShare() of the dial, under the window's), moved a sixth of... |
| 3907 | 1 | `public int getMonthsPayoutHeld()` |  |
| 3910 | 1 | `public double getFundingCost()` | What the window charged this month, paid to the central bank. |
| 3912 | 1 | `public double getWindowOwedCost()` | ...of which on the advance the last settle left owed past the tranche, the central bank's reserves short of its repayment. |
| 3914 | 1 | `public double getPlacementIncome()` | What its reserves earned at the central bank this month, at the policy rate. |

### THE THREE STATEMENTS (lines 3916-4027)

| line | len | member | says |
|---:|---:|---|---|
| 3939 | 1 | `public double cashReserves()` | Cash it is actually sitting on. |
| 3946 | 1 | `public double totalAssets()` | Total assets: the loan book net of what it has set aside against it (netLoans(), since 0.7.8), whatever cash it has not lent, and what the desk holds. |
| 3949 | 1 | `public double netLoans()` | The loans as the balance sheet carries them (0.7.8): what is owed, less the allowance for what will not come back. |
| 3952 | 1 | `public double shortSecurities()` | ...and a desk that is short owes the shares: a liability at the mark. |
| 3979 | 3 | `public double totalLiabilities()` | What the bank owes: what it borrowed to fund its book (past its deposits, at the central bank's window since 0.7.0), and the hot money. |
| 3991 | 1 | `public double equity()` | The residual - and, once the two above are written out, simply the book plus the cash position. |
| 3994 | 1 | `public double getOpeningEquity()` | Equity as it stood at the top of the month. |
| 3999 | 1 | `public double interestIncome()` | What every borrower paid it this month. |
| 4002 | 3 | `public double netInterestIncome()` | ...less what it paid savers and what it paid the window. |
| 4007 | 1 | `public double feeIncome()` | ...plus its fees, since 0.7.7: the accounts, and the loans written. |
| 4017 | 1 | `public double afterLosses()` | ...less the provision for the loans that will not come back (0.7.8): what the allowance rose by, and whatever the month wrote off that it had not already set aside - provisions(). |
| 4020 | 1 | `public double operatingExpenses()` | ...less the tellers and the lights. |
| 4023 | 1 | `public double afterTrading()` | ...plus what the desk made or lost. |
| 4026 | 1 | `public double profitBeforeTax()` | What it made before the city took its share. |

### tax (lines 4028-4152)

| line | len | member | says |
|---:|---:|---|---|
| 4071 | 1 | `public double getProfitLastMonth()` | Profit the tax is charged on: the month that has just finished - and, since 0.7.7, what the month before it earned after its own close (getCarriedLate(); see PROFIT THAT LANDS AFTER THE CLOSE). |
| 4074 | 54 | `public void closeMonth()` | Called at the end of the month, once the profit is final. |
| 4140 | 3 | `public double lateProfit()` | What this month earned after its close: the part of its profit next month's tax and dividend will carry. |
| 4145 | 1 | `public double getCarriedLate()` | What last month earned after its close, inside getProfitLastMonth(). |
| 4148 | 4 | `public void restoreLateProfit(double value)` | The load path: what the saved month earned after its close. |

### LAST MONTH, KEPT ON PURPOSE (lines 4153-4305)

| line | len | member | says |
|---:|---:|---|---|
| 4180 | 15 | `public double[] lastMonthToSave()` |  |
| 4219 | 25 | `public void restoreLastMonth(double[] state)` | ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the cost-of-funds floor. |
| 4246 | 1 | `public void setProfitLastMonth(double value)` | Restored from the save, so next month taxes the right figure. |
| 4258 | 1 | `public void setDepositRate(double rate)` | What savers were paid in the month this save was taken in. |
| 4261 | 1 | `public double getTaxPaid()` | What the city took this month. |
| 4274 | 5 | `public double chargeTax(double annualProfitRate)` | Hands the city its share of last month's profit. |
| 4281 | 3 | `private static double taxOn(double profit, double profitTaxRate)` | The city's share of a month's profit at this rate: nothing on a loss. |
| 4292 | 3 | `public double taxAt(double annualProfitRate)` | What chargeTax() would take at the top of next month at this rate (0.7.36): the profit the month just closed on, taxed at it. |
| 4302 | 3 | `public double getProfitAfterTaxLastMonth(double profitTaxRate)` | Last month's profit AFTER the tax it will be charged at this rate: what the owners are paid a share of (Game.payDividends()) and what the register records (Equity.recordMonth()), as every sector's own net income is. |

### THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 4306-4384)

### the allowance (lines 4385-4663)

| line | len | member | says |
|---:|---:|---|---|
| 4395 | 3 | `public static double lossIfDefaulted(double principal, double assets)` | What the BACKSTOP would cost the bank on a business owing this against these assets (BusinessDebtManager.restructure()): everything owed past BusinessDebtManager.RESTRUCTURE_TARGET of its assets - all of it, for a sec... |
| 4400 | 4 | `public static boolean sectorWatched(double principal, double assets)` | A business borrower in trouble: owing past SECTOR_WATCH_LEVERAGE of its assets, or anything at all against none. |
| 4413 | 6 | `public static double stageTwoShare(double principal, double assets)` | The share of a sector's firms, by what they owe, past the watch line: the curve's spread of fortunes (BusinessDebtManager.ASSET_VOLATILITY) read at SECTOR_WATCH_LEVERAGE instead of the default point, N(ln(L / SECTOR_W... |
| 4451 | 3 | `public static double sectorAllowance(double principal, double assets)` | The allowance a business's book holds, read off the curve its firms default on (0.7.8) - the same PD(L) that writes the month's slice off (BusinessDebtManager.defaultProbability()), so the allowance is what the slices... |
| 4464 | 3 | `public static double sectorAllowance(double owed, double principal, double assets)` | ...ON WHAT IT OWES NOW, READ AT ITS QUARTER (0.7.8): the curve read at principal over assets - the averages of its last quarter's readings (BusinessDebtManager.quarterPrincipal(), quarterAssets()) - and the loss struc... |
| 4477 | 13 | `public static double sectorAllowance(double owed, double principal, double assets, double lossGivenDefault)` | ...AT A LOSS GIVEN DEFAULT OF ITS OWN (0.7.12): a loan's for what the sector owes the bank, a bond's for the sector's bonds it holds (BusinessDebtManager, RECOVERIES BY INSTRUMENT, since round 2; round 1 read the two ... |
| 4498 | 3 | `private static double floorAt(double lgd)` | THE FLOOR IS THE SOUND BOOK'S LOSS, at this loss given default (0.7.12): BASE_LOSS_RATE is a sound LOAN's loss, so at a loan's loss given default it is BASE_LOSS_RATE, and a dollar that loses more when it defaults - a... |
| 4535 | 17 | `public void provide(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | THE MONTH'S PROVISION: sets every book's allowance from its borrowers as they stand now, and draws the month's write-offs against what each book held when the month opened. |
| 4560 | 11 | `public void openAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` | ...and a bank that has never held one: the allowance its borrowers call for, set up WITHOUT a provision - the month it opens on holds it from its start. |
| 4572 | 27 | `private void strikeAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched)` |  |
| 4601 | 5 | `public double getAllowance()` | Everything set aside against the book. |
| 4608 | 1 | `public double getSectorAllowance()` | ...against the businesses' book, all of it. |
| 4610 | 1 | `public double getSectorAllowance(String sector)` | ...against one sector's. |
| 4623 | 4 | `public double[] getAllowanceReading(String sector)` | What the allowance on a sector was struck on, as Game.bankReadings() handed it over at the month's provision: {the quarter's principal, its assets, what it owes that nobody insures, the loans' loss given default, the ... |
| 4629 | 1 | `public double getWrittenOff(String sector)` | What this month wrote off one sector's book - its defaulted firms' slice, or the backstop (0.7.8: the Bank tab's "this month", beside the allowance). |
| 4631 | 1 | `public double getHouseholdAllowance()` | ...against the families'. |
| 4633 | 1 | `public int getStage(String sector)` | 2 when that sector's book is in trouble - most of its firms past the watch line - 1 when most are sound. |
| 4635 | 1 | `public double getStageTwoShare(String sector)` | The share of that sector's book in stage 2, as the last provide() struck it (stageTwoShare()). |
| 4637 | 1 | `public int getHouseholdStage()` | 2 when any family's line is in trouble, 1 when none is. |
| 4639 | 1 | `public double getHouseholdWatchedDebt()` | The families' debt in the cells that are in trouble. |
| 4641 | 1 | `public java.util.Set<String> getSectorsWatched()` | The sectors whose books are in stage 2. |
| 4643 | 1 | `public int getBooksWatched()` | How many of its books are in stage 2: each sector's, and the families' as one (0.7.9, the Bank tab's count of borrowers in trouble). |
| 4645 | 1 | `public double getOpeningAllowance()` | The allowance the month opened with. |
| 4653 | 1 | `public double provisions()` | THE PROVISION, the income statement's line: what the allowance rose by this month and whatever was written off that it had not set aside - which is the allowance's move plus every write-off. |
| 4656 | 1 | `public double getAllowanceUsed()` | The month's write-offs that the allowance had already set aside. |
| 4659 | 1 | `public double getWriteOffsBeyondAllowance()` | ...and the part it had not, which reached the statement the month it was written off. |
| 4662 | 1 | `public double getProvisionCharge()` | The part of the provision that went into the allowance: its rise less what the write-offs drew out of it. |

### what it holds (lines 4664-4805)

| line | len | member | says |
|---:|---:|---|---|
| 4691 | 1 | `public void hearCapitalRule(boolean basel3)` | Hears which capital rule stands this month: true from the month L8 is known (always, in a city founded the default way). |
| 4694 | 1 | `public boolean conservationBufferRequired()` | Whether the Basel III conservation buffer is the rule this month (L8). |
| 4697 | 2 | `public double bufferFloor()` | The least buffer the rule asks over the minimum this month: CONSERVATION_BUFFER once L8 is known, BUFFER_BEFORE_L8 before. |
| 4714 | 10 | `private void recordLosses()` | Files the month that has just closed into the loss record. |
| 4741 | 8 | `public double trailingLossRate()` | The last twelve recorded months' provisions over their average weighted book - or over what its branches' founding capital is built to carry at the minimum (branches x paidInPerBranch / CAPITAL_RATIO), whichever is la... |
| 4751 | 1 | `public double getWorstLossRate()` | The worst year it has lived through, as the capital target reads it. |
| 4798 | 1 | `public double capitalBuffer()` | Its buffer over the minimum: the worst year it has recorded, never less than the rule's floor (bufferFloor(): CONSERVATION_BUFFER from L8) nor more than MAX_BUFFER. |
| 4801 | 1 | `public double capitalTarget()` | THE TARGET it chooses: the city's minimum and its own buffer. |
| 4804 | 1 | `public double capitalTop()` | ...and the top of its band. |

### the leverage ratio (0.7.11, round 2) (lines 4806-4889)

| line | len | member | says |
|---:|---:|---|---|
| 4828 | 1 | `public double exposure()` | THE EXPOSURE MEASURE the leverage ratio is struck on: everything on its balance sheet at the value the sheet carries it at, whatever it weighs - totalAssets(). |
| 4831 | 4 | `public double leverageRatio()` | Equity over the exposure measure: the leverage ratio a regulator reads. |
| 4843 | 4 | `public double leverageTarget()` | ITS OWN LEVERAGE TARGET: scale the leverage minimum by the risk target over the original CAPITAL_RATIO. |
| 4849 | 4 | `public double leverageTop()` | ...and the top of its band, retaining its management cushion over that independent baseline. |
| 4869 | 3 | `public double minimumEquity()` | THE MINIMUM THE CITY REQUIRES, IN MONEY: the larger of the risk-based one, CAPITAL_RATIO of the weighted book, and the leverage one, LEVERAGE_RATIO_MIN of the exposure. |
| 4874 | 3 | `public boolean leverageBinds()` | True when the leverage requirement is the larger - when a bank's zero-weighted assets are what its capital is short against. |
| 4879 | 1 | `public double bindingRatio()` | Its capital as a ratio on the measure that binds: the leverage ratio when leverageBinds(), the risk-based capitalRatio() otherwise - the figure the Bank tab's bar and status read. |
| 4882 | 1 | `public double bindingMinimum()` | The minimum on the binding measure: LEVERAGE_RATIO_MIN or CAPITAL_RATIO. |
| 4885 | 1 | `public double bindingTarget()` | Its target on the binding measure: leverageTarget() or capitalTarget(). |
| 4888 | 1 | `public double bindingTop()` | ...and the top of its band on it: leverageTop() or capitalTop(). |

### what it does with profit (lines 4890-5011)

| line | len | member | says |
|---:|---:|---|---|
| 4899 | 1 | **type** `public enum Payout` | What the bank does with its profit, as its capital stands. |
| 4906 | 3 | `public double targetEquity()` | The equity its target calls for on the book it has: the larger of its target on the weighted book and its leverage target on the exposure (0.7.11, round 2 - minimumEquity() says why). |
| 4918 | 4 | `public double topEquity()` | The equity at the top of its band - and NEVER LESS THAN WHAT ITS STANDING BRANCHES WERE FOUNDED WITH, paidInPerBranch each: the capital a counter is opened with is what the running costs are already priced on (strikeP... |
| 4924 | 4 | `public double excessCapital()` | What it holds past the top of its band: what it returns, a twelfth a month. |
| 4930 | 9 | `public Payout payoutStance()` | Where its capital puts it, for the words and the rules. |
| 4941 | 10 | `public String payoutDecision()` | ...in words, for the Bank tab. |
| 4961 | 11 | `public double dividendDue(double profitAfterTax)` | WHAT IT PAYS ITS OWNERS this month, on last month's profit after tax. |
| 4980 | 3 | `public double payOwners(double profitAfterTax)` | Pays its owners what dividendDue() says, and keeps what the rule read - the profit it was paid on, the excess over the top and the room over the target - so the month's decision can be read back. |
| 4992 | 13 | `public double payOwners(double profitAfterTax, double sharesInIssue)` | ...and with the shares in issue, for the city's consent (0.7.14): while a block of its preferred is under three years old the common dividend a share does not rise past what it was the year before the city bought it (... |
| 5008 | 1 | `public double getPayoutProfit()` |  |
| 5009 | 1 | `public double getPayoutExcess()` |  |
| 5010 | 1 | `public double getPayoutOverTarget()` |  |

### its own shares (lines 5012-5176)

| line | len | member | says |
|---:|---:|---|---|
| 5046 | 6 | `public void buyBackOwnShares(double paid)` | The desk bought the bank's own shares back and cancelled them: cash out, equity down, no income. |
| 5054 | 5 | `public void issueOwnShares(double received)` | ...and issued new ones: cash in, equity up, no income. |
| 5065 | 7 | `public boolean buysBackOwnShares()` | True when the desk buys the bank's own shares back from whoever sells: standing, and at or over its own capital target. |
| 5093 | 4 | `public boolean issuesOwnShares()` | ...and when it issues new ones to whoever buys: standing, lending, and UNDER its own target - raising the capital its rule says it is short of, and never while it holds what it wants. |
| 5111 | 3 | `public double spareCapital(double inventory)` | WHAT IT HOLDS OVER ITS TARGET: its equity less targetEquity(), with the desk's inventory carried at `inventory` rather than at the securities line's last mark - the line lags the desk's deals within a month until the ... |
| 5116 | 6 | `private double spareOnRisk(double inventory)` | Its spare capital against its target on the weighted book, the inventory carried at `inventory`. |
| 5128 | 6 | `private double spareOnLeverage(double inventory)` | ...and against its leverage target on the exposure (0.7.11, round 2). |
| 5136 | 1 | `public double spareCapital()` | ...on the books as they stand: equity() less targetEquity(). |
| 5155 | 3 | `public double buybackRoom(double inventory)` | THE MOST IT MAY SPEND BUYING ITS OWN SHARES BACK NOW: what it holds over its target, spareCapital(inventory), so that no purchase takes it under the target - a month's buybacks never exceed the capital over target at ... |
| 5159 | 1 | `public double getSharesBoughtBack()` |  |
| 5160 | 1 | `public double getSharesIssued()` |  |
| 5168 | 1 | `public double dividendsOverYear()` | Dividends over the last twelve months, this one included. |
| 5170 | 1 | `public double buybacksOverYear()` | ...and its own shares bought back. |
| 5173 | 3 | `public double returnOnEquity()` | This month's net income over the equity it opened with, a year: the return a bank is read by. |

### what it lends (lines 5177-5266)

| line | len | member | says |
|---:|---:|---|---|
| 5191 | 5 | `public double lendingGrowthLimit()` | HOW FAST THE BANK LETS A BORROWER'S DEBT GROW THIS MONTH, on the capital it has: no limit at or over its target (and with no branch - a city with no bank is lent to from outside); none under the minimum or failed; in ... |
| 5198 | 25 | `private double growthLimitOn(double equity)` | lendingGrowthLimit()'s curve on this much equity and the book as it stands: equity() for the month's rule, less the month's known losses for the band's share (MC2, bandShare()). |
| 5225 | 7 | `public boolean lendsOnlyToKeepBorrowersGoing()` | True when it lends only what keeps its existing borrowers going: under the minimum, or failed. |
| 5234 | 6 | `public double lendingLimit()` | The growth of the book the capital rule allows this month, in money: infinite when it lends freely. |
| 5242 | 8 | `public String lendingStance()` | ...in words. |

### the save (lines 5267-5445)

| line | len | member | says |
|---:|---:|---|---|
| 5278 | 18 | `public java.util.Map<String, double[]> allowanceToSave()` | The allowance, book by book (0.7.8): each sector's name, and HOUSEHOLD_BOOK for the families, to {the allowance, what it held when the month opened, what the month wrote off, and whether it is in trouble - 1 or 0 for ... |
| 5298 | 29 | `public boolean restoreAllowance(java.util.Map<String, double[]> saved)` | ...and back. |
| 5328 | 5 | `private static double sum(java.util.Map<String, Double> m)` |  |
| 5341 | 12 | `public double[] capitalRecordToSave()` | The record the target and the owners' year are struck from (0.7.8): the months recorded, the worst year, the rings of provisions and of the weighted book, the owners' month count and the rings of dividends and buybacks. |
| 5354 | 11 | `public void restoreCapitalRecord(double[] in)` |  |
| 5375 | 28 | `public double[] monthLinesToSave()` | THE MONTH'S STATEMENT LINES (0.7.8), for the save. |
| 5405 | 40 | `public void restoreMonthLines(double[] v)` | ...and back. |

### reading (lines 5446-5472)

| line | len | member | says |
|---:|---:|---|---|
| 5448 | 1 | `public double getCash()` |  |
| 5449 | 1 | `public double getDeposits()` |  |
| 5450 | 1 | `public double getBranches()` |  |
| 5451 | 1 | `public double getSectorBook()` |  |
| 5452 | 1 | `public double getCityBook()` |  |
| 5453 | 1 | `public double getHouseholdBook()` |  |
| 5454 | 1 | `public double getInterestEarned()` |  |
| 5455 | 1 | `public double getWriteOffs()` |  |
| 5456 | 1 | `public double getPayroll()` |  |
| 5457 | 1 | `public double getUpkeep()` |  |
| 5458 | 1 | `public double getLentToHouseholds()` |  |
| 5459 | 1 | `public double getRepaidByHouseholds()` |  |
| 5469 | 3 | `public double getNetIncome()` | The month's profit: interest earned, less the cost of the money, less the loans that died, less the cost of running the place. |

### THE BRANCHES, BY THEIR CUSTOMERS (0.7.19) (lines 5473-5740)

| line | len | member | says |
|---:|---:|---|---|
| 5574 | 1 | `public boolean wantsBranch()` | True when the city should be opening another counter: a city with loans and no bank at all always wants its first, the charter; after that, only while there are customers for another and their fees would cover it - se... |
| 5577 | 46 | `public boolean wantsBranch(double standing)` | ...counting these branches standing - see THE BRANCHES, BY THEIR CUSTOMERS. |
| 5625 | 1 | `public boolean customersForAnother()` | Whether there are customers for another branch: more than CUSTOMERS_PER_BRANCH for every branch standing. |
| 5626 | 3 | `public boolean customersForAnother(double standing)` |  |
| 5637 | 1 | `public boolean feesWouldCoverAnother()` | Whether the month's fees would still cover every branch with one more, at what each carries (revised 0.7.19): the charter's cost and a later branch's for each of the rest - fees >= (branches + 1) x a later branch's co... |
| 5638 | 4 | `public boolean feesWouldCoverAnother(double standing)` |  |
| 5661 | 1 | `public void setNewBranchExtra(java.util.function.DoubleSupplier extra)` | The investors hand in C_new - C for the branch they would open (BusinessInvestment.newBranchExtra()); null for none. |
| 5664 | 1 | `double newBranchExtra()` | C_new - C: what a new branch's own crew costs over a later branch's cached cost; 0 with none handed in. |
| 5667 | 4 | `public double newBranchCost()` | C_new: what the branch opened next would cost a month, a later branch's cost and its own crew's difference; 0 with nothing known to cost. |
| 5672 | 3 | `public double laterBranchCost()` |  |
| 5677 | 1 | `public double feesPerBranch()` | What one branch takes in fees this month: the month's account fees over the branches standing. |
| 5680 | 1 | `public double feesPerBranchWithAnother()` | ...and another branch would, with it standing: the fees over one more. |
| 5681 | 1 | `public double feesPerBranchWithAnother(double standing)` |  |
| 5684 | 1 | `public double customersPerBranch()` | The customers one branch serves this month: the customers over the branches standing. |
| 5693 | 5 | `public double branchesTheFeesCover()` | The most branches the month's fees cover at what each cost last month: the charter at its cost, the rest at a later branch's (revised 0.7.19) - n of them cost n later branches less the charter's exempt operating cost ... |
| 5704 | 1 | `public int branchesToClose()` | How many branches close this month (0.7.19): every one past what the month's fees cover, at once, and never the first. |
| 5707 | 4 | `public int branchesToClose(double standing)` | ...counting these branches standing. |
| 5716 | 1 | `public String branchHoldReason()` | Why no branch is opening, in the investment advisor's words (0.7.19), or null when one would. |
| 5717 | 15 | `public String branchHoldReason(double standing)` |  |
| 5734 | 6 | `public String branchOpenReason(double standing)` | ...and the case for one that would: its customers and its fees against its cost. |

### a branch whose fees do not cover it is closed (0.7.19) (lines 5741-6189)

| line | len | member | says |
|---:|---:|---|---|
| 5761 | 1 | `public boolean closesBranch()` | Whether any branch closes this month: see branchesToClose(). |
| 5762 | 1 | `public boolean closesBranch(double standing)` |  |
| 5765 | 1 | `public int getUncoveredMonths()` | Closed months in a row the fees have not covered the branches past the first. |
| 5779 | 8 | `public double capacityWith(double branchCount)` | Capacity this bank would have with a given number of branches. |
| 5800 | 10 | `public double headroom()` | How much more it could lend before it counts itself full (EASY_STRAIN): what the carry trade may take - and, since 0.7.8, no more than keeps the bank at its own capital target. |
| 5844 | 1 | `public double projectHeadroom()` | Growth commitments fit the target even above it (BF1). |
| 5847 | 1 | `public double settledProjectHeadroom()` | ...once the month's loan book has settled and its losses are on it: headroom() at or over the target, the band's share under it (MC2). |
| 5849 | 9 | `private double growthHeadroom(double loss)` |  |
| 5860 | 1 | `public double leverageHeadroom()` | Insured growth consumes exposure, with no risk weight or assumed business-loan mix: the room to the leverage target over it, the band's share under it (MC2, projectHeadroom()). |
| 5863 | 1 | `public double projectLeverageHeadroom()` | The same leverage room before the month's known losses have reached the loan book. |
| 5865 | 7 | `private double insuredHeadroom(double loss)` |  |
| 5884 | 6 | `private double bandShare(double equityLeft, double toMinimum)` | THE BAND'S SHARE IN MONEY (MC2): the growth limit's share of the book the binding measure counts, as lendingLimit() takes it, on this much equity - RATIONED_GROWTH x x/(1-x) of the exposure while the leverage ratio bi... |
| 5891 | 7 | `private double unsettledCreditLoss()` |  |
| 5899 | 1 | `public void setCash(double value)` |  |
| 5901 | 87 | `public void reset()` |  |
| 6008 | 173 | `public void redenominate(double scale)` | Every figure on the bank's balance sheet, in the new unit. |
| 6184 | 5 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

### WHAT THE BANK TAB READS (0.7.9) (lines 6190-6221)

### the interest, by who paid it (lines 6222-6258)

| line | len | member | says |
|---:|---:|---|---|
| 6240 | 9 | `public void takeInterest(double fromCity, double fromBusinesses)` | The businesses' interest and the city's coupons, settled together: the same cash and income as takeInterest() on their sum, to the bit, and each kept by who paid it. |
| 6251 | 1 | `public double getInterestFromBusinesses()` | What the businesses paid it in interest this month. |
| 6253 | 1 | `public double getInterestFromCity()` | ...the city, in coupons on the paper the bank holds. |
| 6255 | 1 | `public double getInterestFromHouseholds()` | ...the families, on their credit lines. |
| 6257 | 1 | `public double getDiscountAccreted()` | ...and the discount on the city's paper it earned this month, which no cash carries. |

### what moved it between two presses (lines 6259-6295)

| line | len | member | says |
|---:|---:|---|---|
| 6268 | 1 | `public double getTreasuryBuybackGain()` |  |
| 6277 | 1 | `public double getAllowanceOpened()` |  |
| 6285 | 1 | `public double getBailoutsLifetime()` |  |
| 6294 | 1 | `public boolean isMonthKnown()` |  |

### its year of statements (lines 6296-6512)

| line | len | member | says |
|---:|---:|---|---|
| 6308 | 12 | **type** `public enum Line` | The lines of a month's statement the Bank tab sets beside last month's and adds up over a year, every one money: the interest and who paid it; what savers and the window were paid; fees and their three kinds; provisio... |
| 6322 | 1 | `public double revenue()` | What it earned before provisions and costs: net interest, fees, the desk and the city's paper - what its costs are read against. |
| 6333 | 1 | `public double getRetained()` | What it kept of the month's profit once its owners were paid: net income less the dividend and its own shares bought back. |
| 6336 | 39 | `public double thisMonth(Line line)` | A line as this month stands. |
| 6385 | 5 | `private void fileStatement()` |  |
| 6392 | 1 | `public boolean knowsLastMonth()` | True when last month is on file: a month was played before this one, or a save carried it. |
| 6395 | 4 | `public double lastMonth(Line line)` | A line as last month ended, everything booked after its close included. |
| 6401 | 1 | `public int monthsInYear()` | How many months the year's figures cover: this one and those on file, YEAR_MONTHS at most. |
| 6404 | 8 | `public double overYear(Line line)` | A line added up over monthsInYear(), this month included - for the flows; the two stocks want averageOverYear(). |
| 6414 | 1 | `public double averageOverYear(Line line)` | ...and averaged over them: a month's worth. |
| 6421 | 4 | `public double returnOnEquityOverYear()` | What it earned over the year, at a yearly rate, on the equity it held on average: the return a bank is read by, and steadier than a month's (returnOnEquity()). |
| 6431 | 4 | `public double provisionRateOverYear()` | Provisions over the year, at a yearly rate, as a share of the book it held on average: its credit losses as a bank reports them - a sound book's is BASE_LOSS_RATE. |
| 6442 | 4 | `public double netInterestMarginOverYear()` | Net interest income over the year, at a yearly rate, on the assets that earned it interest on average (averageEarningAssetsOverYear()): its net interest margin, steadier than a month's (netInterestMargin()). |
| 6456 | 8 | `public double averageEarningAssetsOverYear()` | Its earning assets (earningAssets()) averaged over the months the year's flows cover (monthsInYear()): this month's, and those of the sheets filed at the close of the months before it, read from the year of balance sh... |
| 6466 | 6 | `private static double earningLines(double[] filedSheet)` | The eight lines of one filed sheet that earn interest, added: what earningAssets() was when it was filed. |
| 6478 | 4 | `public double costShareOverYear()` | Its staff and branches over the year as a share of what it earned before them (revenue()): the efficiency ratio, about 50-60% at a real bank. |
| 6484 | 10 | `public double[] statementYearToSave()` | The year of statements, for the save: how many are filed, how many lines each, then the ring's months in slot order. |
| 6501 | 11 | `public void restoreStatementYear(double[] in)` | ...and back. |

### its balance sheet (0.7.13) (lines 6513-6607)

| line | len | member | says |
|---:|---:|---|---|
| 6528 | 12 | **type** `public enum Sheet` | THE LINES OF ITS BALANCE SHEET, as the model books it (THE THREE STATEMENTS): what totalAssets() and totalLiabilities() sum, line by line, the allowance negative, and equity() the residual; then the two deposits it co... |
| 6566 | 30 | `public double sheet(Sheet line)` | A line of its balance sheet as it stands: its reserves at the central bank (the cash it is not borrowing; its placements abroad came home to the central bank in 0.7.0); what the businesses owe it outside the insured m... |
| 6603 | 1 | `public double sheetResidual()` | What its lines leave unexplained: the asset lines, less the liability lines, less equity. |
| 6606 | 1 | `public double yearAgoResidual()` | ...and the same of the sheet a year ago; nothing when none is on file. |

### when the lines do not add up (BI1, 2026-11-20) (lines 6608-6732)

| line | len | member | says |
|---:|---:|---|---|
| 6620 | 3 | `public static boolean addsUp(double residual, double size)` | Whether a residual is within what MoneyAudit.tolerance() allows a book of this size; a residual or a size that is not a finite number is not, so the page says so rather than hiding it. |
| 6625 | 1 | `public double sheetSize()` | The size of the sheet the lines are read against: every line's magnitude, each carrying its own rounding into the sum (the allowance is negative on the page). |
| 6628 | 1 | `public double yearAgoSheetSize()` | ...the sheet a year ago; nothing when none is on file. |
| 6631 | 1 | `public boolean sheetAddsUp()` | True when its lines add up within MoneyAudit.tolerance() of the sheet's size: the page's "Its lines do not add up" is its opposite. |
| 6634 | 1 | `public boolean yearAgoSheetAddsUp()` | ...and the sheet a year ago; true when none is on file, which the page has nothing to say of. |
| 6636 | 6 | `private static double sizeOf(java.util.function.ToDoubleFunction<Sheet> at)` |  |
| 6644 | 1 | `public double liabilitiesAndEquity()` | The other side of the sheet: its liabilities and its equity together. |
| 6647 | 1 | `public double yearAgoLiabilitiesAndEquity()` | ...and a year ago; nothing when none is on file. |
| 6649 | 6 | `private static double residual(java.util.function.ToDoubleFunction<Sheet> at)` |  |
| 6666 | 10 | `private void fileSheet()` |  |
| 6678 | 1 | `public boolean knowsYearAgo()` | True when the sheet a year before this one is on file: a year played in this build, or a save that carried one. |
| 6681 | 3 | `public double yearAgo(Sheet line)` | A line of the sheet a year before this one; nothing when none is on file. |
| 6686 | 3 | `public double yearAgoLoansToSector(int sector)` | What the sector at this index of Sectors.KEYS owed it a year ago, outside its insured mortgages and interim financing. |
| 6691 | 4 | `public double yearAgoInterimToSector(int sector)` | ...and its interim financing then. |
| 6697 | 11 | `public double[] sheetYearToSave()` | The year of sheets, for the save: how many are filed, the lines and the sectors each holds, then the ring's months in slot order. |
| 6715 | 17 | `public void restoreSheetYear(double[] in)` | ...and back. |

### its rates, in a ladder (lines 6733-6785)

| line | len | member | says |
|---:|---:|---|---|
| 6749 | 22 | **type** `public record Ladder(double policy, double savers, double saversChose, double saversShare, double fundingPo...` | THE LADDER OF ITS RATES at one policy rate, read at one moment: the policy rate; what savers were paid, the rate the bank chose, the share of the policy rate its funding asks it to pass on and the funding position tha... |
| 6755 | 1 | `public double saversOverPolicy()` _(in Bank.Ladder)_ | Savers' rate less the policy rate: under it by the bank's margin on a deposit. |
| 6757 | 1 | `public double transferOverPolicy()` _(in Bank.Ladder)_ | The funds-transfer price over the policy rate: the window's penalty on its share, and the term premium. |
| 6759 | 1 | `public double primeOverTransfer()` _(in Bank.Ladder)_ | Prime over the funds-transfer price: the running costs, the expected loss and the capital charge. |
| 6761 | 1 | `public double parts()` _(in Bank.Ladder)_ | The four parts added up in prime's own order - which is prime. |
| 6763 | 1 | `public double overPrime(double rate)` _(in Bank.Ladder)_ | A borrower's rate over prime: the step each borrower's rung is labelled with - its own risk, or for the carry trade the costs it does not carry. |
| 6765 | 1 | `public double mortgageTransferOverPolicy()` _(in Bank.Ladder)_ | An insured mortgage's money over the policy rate: the window's penalty on its share, and the ten-year term premium. |
| 6767 | 1 | `public double mortgageOverTransfer()` _(in Bank.Ladder)_ | An insured mortgage's rate over its money: running the bank, and the capital its leverage requirement ties up (round 2). |
| 6769 | 1 | `public double mortgageRunning()` _(in Bank.Ladder)_ | ...the first of those two: what running the bank adds to an insured mortgage (0.7.33; the Bank tab worked it out until then), so its money, this and mortgageCapital add up to mortgage. |
| 6773 | 12 | `public Ladder ladder(double policyAnnual)` | The ladder at this policy rate. |

### in words (lines 6786-6852)

| line | len | member | says |
|---:|---:|---|---|
| 6801 | 27 | `public String status()` | THE BANK'S STATE IN ONE SENTENCE, with the figure that decides it: the first thing the Bank tab says. |
| 6830 | 22 | `public String targetReason()` | Why its capital target is what it is, in words: the cap, its worst year, or the standard buffer and why. |

### what its book weighs (lines 6853-6895)

| line | len | member | says |
|---:|---:|---|---|
| 6856 | 1 | **type** `public enum Book` | The eight things on its books that capacity weighs: the four it lends on, the insured mortgages inside the businesses' (0.7.11), the desk's shares, and since 0.7.12 the businesses' bonds it holds and the weight the bo... |
| 6864 | 1 | **type** `public record WeightRow(Book book, double face, double term, double risk, double weighted)` | One row of what the book weighs: its face; the share of it its remaining term counts for (maturityWeight(), on average over its loans - 1 where nothing runs off); its risk weight; and what it weighs, face x term x risk. |
| 6873 | 17 | `public java.util.List<WeightRow> weightTable()` | Every row, the desk's shares included: the weighted column foots to getWeightedBook(). |
| 6891 | 4 | `private static WeightRow weightRow(Book book, double face, double risk, double weighted)` |  |

### its funding (lines 6896-6927)

| line | len | member | says |
|---:|---:|---|---|
| 6899 | 1 | `public double getHouseholdDeposits()` | What the families have banked with it. |
| 6901 | 1 | `public double getSectorDeposits()` | ...and what the businesses hold in credit. |
| 6913 | 5 | `public void restoreDeposits(double household, double sector)` | The deposit book as the month last set it, put back by the load path (FW2, 2026-10-24; F-S1-1). |
| 6920 | 1 | `public double getPaidInPerBranch()` | What its owners put up when a branch opens, in today's money: PAID_IN_PER_BRANCH, reformed, and struck at the expected price level since 0.7.42 (seedConstants()). |
| 6923 | 1 | `public double localDeposits()` | The city's own savings with it: everything banked, less the world's - all of it gathered since 0.7.19 (depositsGathered()). |
| 6926 | 1 | `public double fundingLimit()` | What its funding would carry: what it gathered, lent LEVERAGE times over - the second of capacity()'s two limits. |

### another branch (lines 6928-6941)

| line | len | member | says |
|---:|---:|---|---|
| 6931 | 1 | `public double capacityAnotherBranchWouldAdd()` | The capacity one more branch would add: the capital it would open with, since 0.7.19 - it brings no deposits the bank does not already reach. |
| 6934 | 1 | `public double runningCostPerBranch()` | What a branch cost to run last month on average: the payroll, the repairs and (since 0.7.19) the operating cost the branches past the charter paid, over the branches standing. |
| 6937 | 4 | `public double feeCover()` | This month's fees against this month's running costs, over every branch: what each branch past the first is held to (0.7.19). |

### how its equity moved (lines 6942-7010)

| line | len | member | says |
|---:|---:|---|---|
| 6962 | 34 | **type** `public record EquityMovement(double opening, double kept, double fromShareholders, double fromCity, double ...` | HOW ITS EQUITY MOVED since the month opened, every cause named: what it kept (net income); capital put in by its shareholders at home and abroad, and by the city in a rescue; the founding settlement, the month the cit... |
| 6968 | 6 | `public double residual()` _(in Bank.EquityMovement)_ | What none of the causes explains. |
| 6976 | 8 | `public double scale()` _(in Bank.EquityMovement)_ | The size of the figures residual() is made of, each as a magnitude: what MoneyAudit.tolerance() reads it against (0.7.54). |
| 6994 | 1 | `public boolean addsUp()` _(in Bank.EquityMovement)_ | True when the causes explain the month's change in equity within MoneyAudit.tolerance() of scale() - the rule of the sheet and its split (addsUp(), BI1) - and the page's "Something moved its equity without telling it"... |
| 7003 | 7 | `public EquityMovement equityMovement()` | The month's movement, as it stands. |

### its equity, in two parts (lines 7011-7129)

| line | len | member | says |
|---:|---:|---|---|
| 7059 | 1 | `public boolean knowsEquitySplit()` | True when this bank has kept its equity in two parts since it was founded; false on a save from before 0.7.13's round 2. |
| 7068 | 4 | `public double paidInThisMonth()` | What its owners and the city put in this month: its offerings at home and abroad, new shares, less shares bought back, and the city's capital in a resolution - and since 0.7.14, less the old owners' paid-in written of... |
| 7085 | 6 | `public double retainedThisMonth()` | ...and everything else that moved its equity this month: its net income, less its dividend, the founding settlement, what its creditors absorbed (before 0.7.14), the treasury's buybacks, an older save's allowance - an... |
| 7093 | 1 | `public double paidInCapital()` | Its paid-in capital as it stands. |
| 7096 | 1 | `public double retainedEarnings()` | Its retained earnings as they stand - the Balance sheet page's, not getRetained()'s month's payout. |
| 7099 | 3 | `public double equitySplitResidual()` | What the parts leave unexplained against equity() - paid in, retained and, since 0.7.14, the city's preferred: nothing when every cause is routed, and nothing when !knowsEquitySplit(). |
| 7104 | 3 | `public double equitySplitSize()` | The size of what the split is read against: the magnitudes of its three parts and of equity, each carrying its own rounding into the sum; nothing when !knowsEquitySplit(). |
| 7109 | 1 | `public boolean equitySplitAddsUp()` | True when the parts add up to equity within MoneyAudit.tolerance() of the split's size (see sheetAddsUp()); true when !knowsEquitySplit(), which the page has nothing to say of. |
| 7112 | 1 | `public double paidInOpening()` | Its paid-in capital at the top of the month, for the save; nothing when !knowsEquitySplit(). |
| 7115 | 1 | `public double retainedOpening()` | ...and its retained earnings. |
| 7122 | 6 | `public void restoreEquitySplit(Double paidIn, Double retained)` | The load path: the two counters at the top of the saved month, after the month's lines are restored (restoreMonthLines()). |


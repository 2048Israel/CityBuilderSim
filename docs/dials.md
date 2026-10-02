# The dials

Generated 2026-10-02 by `ham.citybuildersim.tools.Dials` - every `static final` constant in the tree, with the comment that explains it. Do not edit; regenerate with `Regenerate maps.bat`.

**1,557 constants in 247 files.**

## model (725 constants)

### AgeBand.java ([map](map/AgeBand.md))

| line | constant | value | says |
|---:|---|---|---|
| 180 | `AgeBand.MAX_MONTHLY_MORTALITY` | `.05` | No band may lose more than this in a single month, whatever modifies it. |

### Bank.java ([map](map/Bank.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `Bank.LEVERAGE` | `6` | How many times its deposits the bank will lend. |
| 113 | `Bank.CAPITAL_RATIO` | `.08` | Equity a bank must hold against its risk-weighted book. |
| 143 | `Bank.LEVERAGE_RATIO_MIN` | `.03` | Equity a bank must hold against everything it has lent, whatever that weighs: 3%, Basel III's leverage ratio (0.7.11, round 2). |
| 187 | `Bank.PAID_IN_PER_BRANCH` | `32_000` | What the shareholders put up when a branch opens. |
| 215 | `Bank.RISK_CITY` | `.20` | The city cannot default on its own paper. |
| 218 | `Bank.RISK_BUSINESS` | `1.00` | A business can be restructured, and in this game regularly is. |
| 221 | `Bank.RISK_HOUSEHOLD` | `1.00` | ...and a family can be discharged. |
| 237 | `Bank.RISK_CARRY` | `1.00` | A foreign carry borrower, against the risk weights above. |
| 254 | `Bank.RISK_INSURED_MORTGAGE` | `0.0` | An insured mortgage (0.7.11): 0%. |
| 257 | `Bank.SHORTEST_WEIGHT` | `.40` | What a loan repaying tomorrow weighs against one repaying never. |
| 260 | `Bank.LONG_TERM_MONTHS` | `60` | Months of remaining term at which a loan weighs its full amount. |
| 275 | `Bank.EASY_STRAIN` | `.80` | How much of its capacity the bank lends before it counts itself full: the carry trade is lent only the room below it. |
| 717 | `Bank.BASE_LOSS_RATE` | `.004` | What a sound loan is expected to lose a year through the cycle, as a share of the book: 0.4%, about what Canada's big banks provision for credit losses in a normal year (RBC, 2025). |
| 720 | `Bank.LOSS_WATCH` | `2` | How many times BASE_LOSS_RATE a year of provisions runs at before the Bank tab's CREDIT LOSSES is a warning: twice what a sound book loses (0.7.33; the screen's own literal until then). |
| 723 | `Bank.COST_WINDOW_MONTHS` | `12` | Months of payroll and upkeep the running costs are measured over: a year, so one month's building bill is not a price. |
| 726 | `Bank.PRIME_TERM_MONTHS` | `BusinessDebtManager.LOAN_TERM_MONTHS` | The term prime is struck at: a business loan's, BusinessDebtManager.LOAN_TERM_MONTHS, which takes the short end's term premium. |
| 739 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 964 | `Bank.IRB_CONFIDENCE` | `.999` | The confidence the IRB capital is struck at: 99.9% of years, BCBS (2006) paragraph 272. |
| 967 | `Bank.IRB_CORRELATION_HIGH` | `.24` | The IRB corporate correlation for the soundest firms: 24%, BCBS (2006) paragraph 272. |
| 970 | `Bank.IRB_CORRELATION_LOW` | `.12` | ...and for the riskiest: 12%, the same paragraph. |
| 973 | `Bank.IRB_CORRELATION_DECAY` | `50` | How fast the correlation falls from the one to the other as the default rate rises: the 50 in exp(-50 x PD), the same paragraph. |
| 976 | `Bank.IRB_MATURITY_YEARS` | `2.5` | The effective maturity of a corporate exposure under the foundation IRB approach: 2.5 years, BCBS (2006) paragraph 318 - where the maturity adjustment reads 1 / (1 - 1.5 b). |
| 979 | `Bank.IRB_MATURITY_A` | `.11852` | The maturity adjustment's slope, b(PD) = (0.11852 - 0.05478 ln PD)^2: its constant, BCBS (2006) paragraph 272. |
| 982 | `Bank.IRB_MATURITY_B` | `.05478` | ...and its log coefficient, the same paragraph. |
| 997 | `Bank.SECTOR_CORRELATION_MULTIPLIER` | `1.25` | HOW MUCH MORE CORRELATED THE FIRMS OF ONE INDUSTRY ARE than the IRB's corporate correlation, which is struck for a book spread across every industry: 1.25, the asset value correlation multiplier Ba... |
| 1025 | `Bank.IRB_QUANTILE` | `inverseNormal(IRB_CONFIDENCE)` | The standard normal quantile at IRB_CONFIDENCE, G(0.999) in the IRB formula: struck once. |
| 1474 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 1823 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 2016 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 2229 | `Bank.PREFERRED_MIN_SHARE` | `.01` | The least preferred the bank asks for, of its risk-weighted assets: 1%, TARP's "not less than 1% of its risk-weighted assets". |
| 2232 | `Bank.PREFERRED_MAX_SHARE` | `.03` | ...and the most: 3%, TARP's "not more than ... |
| 2235 | `Bank.PREFERRED_RATE` | `.05` | The preferred's dividend, a year, until its fifth anniversary: 5%, TARP's "cumulative dividends at a rate of 5% per annum until the fifth anniversary". |
| 2238 | `Bank.PREFERRED_STEP_RATE` | `.09` | ...and after it: 9%, TARP's "thereafter at a rate of 9% per annum". |
| 2241 | `Bank.PREFERRED_STEP_MONTHS` | `60` | The months to the step: the fifth anniversary. |
| 2244 | `Bank.PREFERRED_CONSENT_MONTHS` | `36` | The months the city's consent binds - no buyback, no rise in the common dividend a share: three years, TARP's "until the third anniversary". |
| 2247 | `Bank.PREFERRED_REDEEM_MONTHS` | `36` | When a block is redeemed whole: its third anniversary, the first TARP's term sheet allows it from anything but new common ("may not be redeemed for a period of three years ... |
| 2250 | `Bank.WARRANT_SHARE` | `.15` | The warrants' reach, of the preferred's amount: 15%, TARP's "aggregate market price equal to 15% of the Senior Preferred amount". |
| 2253 | `Bank.WARRANT_TERM_MONTHS` | `120` | The warrants' term, in months: ten years, TARP's. |
| 3070 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 3073 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 3462 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - s... |
| 3465 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of... |
| 3468 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 4023 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not... |
| 4026 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 4029 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 4325 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 4328 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 4331 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 4422 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point c... |
| 4516 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 4519 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 4722 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into... |
| 4803 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit... |
| 5177 | `Bank.CUSTOMERS_PER_BRANCH` | `16_000` | The customers one branch serves: 16,000, TD's clients per branch - "approximately 16 million clients in Canadian Personal and Business banking" through "more than 1,000 branches" (TD, corporate inf... |
| 6000 | `Bank.SHEET_ASSETS` | `{ Sheet.RESERVES, Sheet.BUSINESS_LOANS, Sheet.INTERIM, Sheet.MORTGAGES, Sheet.FAMILIES,...` | The asset lines, in the page's order: they sum to totalAssets(). |
| 6004 | `Bank.SHEET_LIABILITIES` | `{ Sheet.DEPOSIT_FUNDING, Sheet.WINDOW, Sheet.FOREIGN_DEPOSITS, Sheet.DESK_SHORT, Sheet....` | ...and the liability lines: they sum to totalLiabilities(). |

### BondMarket.java ([map](map/BondMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 121 | `BondMarket.BANK` | `"bank"` | The bank's desk, on the book. |
| 123 | `BondMarket.WORLD` | `"world"` | The world, on the book. |
| 125 | `BondMarket.CELL` | `"household:"` | One household cell on the book - bidding, asking, or selling in the waterfall: this, then its key. |
| 127 | `BondMarket.FUND` | `"city"` | The city's fund, by its rule (0.7.14; TreasuryFund). |
| 129 | `BondMarket.FUND_HAND` | `"city:hand"` | ...and by the player's hand. |
| 144 | `BondMarket.DEFAULT_RATE_FLOOR` | `Bank.BASE_LOSS_RATE / BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | THE LEAST A DEFAULT RATE IS READ AT: the through-the-cycle default rate a sound loan is priced for, Bank.BASE_LOSS_RATE over a loan's loss given default, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT... |
| 147 | `BondMarket.TERM_YEARS` | `CorporateBond.TERM_MONTHS / 12.0` | The term in years, for spreading an issue's costs over its life. |
| 150 | `BondMarket.CHEAPER_BY` | `1e-11` | The arithmetic a bond's all-in cost may sit over the loan's and still be "no dearer": a billionth of a point a year - the bisection's own resolution, not a margin (plan()). |

### BuildAdvice.java ([map](map/BuildAdvice.md))

| line | constant | value | says |
|---:|---|---|---|
| 53 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 68 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Roads & transit", HEALTHCARE = "Healthcare", EDUCATION = "Educati...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 719 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 722 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |

### BuildCard.java ([map](map/BuildCard.md))

| line | constant | value | says |
|---:|---|---|---|
| 136 | `BuildCard.KILOWATTS` | `" kW"` | The words after a power plant's figure (0.7.28): kilowatts, the model's unit and a rate - "units a month" until then, which was neither. |
| 214 | `BuildCard.GROUP_ORDER` | `{ "Homes", "Groceries", "The bank's branches", "Food mills", "Food processing", "Steel"...` | The market groups, in the order a page lays them out. |
| 768 | `BuildCard.LAND_WORDS` | `{ "no land", "short of homes" }` | The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. |
| 770 | `BuildCard.STAFF_WORDS` | `{ "could staff" }` | ...STAFF's. |
| 772 | `BuildCard.LICENCE_WORDS` | `{ "licence", "licences" }` | ...LICENCE's: whole words, so the plural as well. |
| 774 | `BuildCard.ORE_WORDS` | `{ "deposit", "ore" }` | ...ORE's. |
| 776 | `BuildCard.SUPPLY_WORDS` | `{ "fabricates", "will not sell" }` | ...SUPPLY's. |
| 778 | `BuildCard.CREDIT_WORDS` | `{ "borrowing ban", "bank", "default point", "down payment", "lender", "mortgage" }` | ...CREDIT's. |
| 780 | `BuildCard.MONEY_WORDS` | `{ "below cost", "no margin", "worth sinking", "worth building", "cost more than", "noth...` | ...MONEY's. |
| 784 | `BuildCard.ENOUGH_WORDS` | `{ "ahead of", "already", "covers all", "months of work queued", "months of work on site...` | ...ENOUGH's. |

### BuildLog.java ([map](map/BuildLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 35 | `BuildLog.KEEP_MONTHS` | `DemolitionLog.KEEP_MONTHS` | How long a completion stays on the panel. |
| 38 | `BuildLog.MAX_ENTRIES` | `40` | Hard cap, so a city building constantly cannot grow this without limit. |

### BuildingCatalog.java ([map](map/BuildingCatalog.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `BuildingCatalog.FILE_NAME` | `"buildings.json"` |  |

### BuildingManager.java ([map](map/BuildingManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3111 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 4314 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 4337 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 5361 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BuildingsTemplate.java ([map](map/BuildingsTemplate.md))

| line | constant | value | says |
|---:|---|---|---|
| 543 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 546 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

### BusinessDebtManager.java ([map](map/BusinessDebtManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 184 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | The default point: a firm owing more than this multiple of its assets is not getting repaid, and both sides know it. |
| 217 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 240 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What the backstop leaves a restructured sector owing, as a multiple of its assets. |
| 327 | `BusinessDebtManager.ASSET_VOLATILITY` | `.25` | The one-year volatility of a firm's assets: sigma in PD(L), the spread of fortunes among the firms inside a sector. |
| 330 | `BusinessDebtManager.DEFAULT_HORIZON_MONTHS` | `12` | The horizon a default probability is quoted over, in months: one year, the convention of KMV's EDF and of every rating agency's default rate. |
| 377 | `BusinessDebtManager.LOAN_RECOVERY` | `.75` | What a bank loan gives back of each dollar that defaults: 75%, the midpoint of the 70-80% that first-lien senior secured bank loans recovered on average in Moody's Ultimate Recovery Database and S&... |
| 380 | `BusinessDebtManager.BOND_RECOVERY` | `.45` | What a bond gives back of each dollar that defaults: 45%, the midpoint of the 40-50% that senior unsecured bonds recovered on average in the same Moody's and S&P data, 1987-2024. |
| 383 | `BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | `1 - LOAN_RECOVERY` | What the bank loses on a dollar of a sector's loans that defaults: 1 - LOAN_RECOVERY, derived. |
| 386 | `BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT` | `1 - BOND_RECOVERY` | ...and what a bondholder loses on a dollar of its bonds: 1 - BOND_RECOVERY, derived. |
| 477 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 522 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the borrower's expected loss, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 524 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` | The most write-downs DEFAULT_SURCHARGE is charged for: a record adds three points at the most. |
| 527 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` | How long a business loan runs, interest only, before its principal is due: three years, and it keeps the rate it was written at for all of them. |
| 537 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 1876 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 1878 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 1880 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 1882 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 1884 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 1886 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2374 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 2961 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 2963 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3380 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BusinessInvestment.java ([map](map/BusinessInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 47 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 50 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 53 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 56 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 59 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site - every sector but the landlords, who hold work by the month (0.7.17; withinMonthsOfWork()). |
| 103 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, in months of the builders' work. |
| 106 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 332 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 335 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 338 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 349 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |

### CapitalFlows.java ([map](map/CapitalFlows.md))

| line | constant | value | says |
|---:|---|---|---|
| 51 | `CapitalFlows.APPETITE` | `3.0` | Foreign money held, per point of excess return, as a multiple of a year's output. |
| 71 | `CapitalFlows.MAX_SPREAD` | `.25` | Excess return above which appetite stops growing: 25 points since 0.7.2, so a 30% dial in a 5% world actually draws money and cutting it sends that money home (provisional, Jerus's number to settle). |
| 74 | `CapitalFlows.ARRIVAL_SPEED` | `.08` | How much of the gap to its target the stock closes in a month, coming in. |
| 77 | `CapitalFlows.DEPARTURE_SPEED` | `.20` | ...and going out, which is faster, because leaving is always faster. |
| 91 | `CapitalFlows.MIN_STOCK` | `1` | Below this much foreign money, the flow is not worth modelling. |
| 127 | `CapitalFlows.MATERIAL_MONTHS` | `.5` | Months of output below which the hot money is too small to break anything. |
| 160 | `CapitalFlows.PANIC_BACKING` | `.25` | Reserves needed to back the hot money, as a share of it. |
| 163 | `CapitalFlows.PANIC_DEPRECIATION` | `.12` | A twelve-month fall in the currency past this reads as a run. |
| 166 | `CapitalFlows.PANIC_MONTHS` | `18` | How long a break lasts before money will look at the city again. |
| 178 | `CapitalFlows.PANIC_EXIT` | `.33` | The share that leaves each month while confidence is broken. |
| 234 | `CapitalFlows.CARRY_FULL_SPREAD` | `.02` | At this spread or better, the world wants all the spare book there is. |
| 237 | `CapitalFlows.CARRY_MAX_SHARE` | `.90` | ...and never quite all of it, because a bank at its limit lends to nobody. |
| 240 | `CapitalFlows.CARRY_BORROW_SPEED` | `.06` | How fast the book fills, and empties. |
| 241 | `CapitalFlows.CARRY_REPAY_SPEED` | `.20` |  |

### CareType.java ([map](map/CareType.md))

| line | constant | value | says |
|---:|---|---|---|
| 114 | `CareType.SENIOR_NEED_AGAINST_AN_ELDER` | `.19` | 5.5% of 70-84 in care against 29.6% of the over-85s - see placesPerHead. |

### CentralBank.java ([map](map/CentralBank.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `CentralBank.WINDOW_PENALTY` | `.0025` | What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so bo... |
| 101 | `CentralBank.DEFAULT_ADVANCES_MONTHS` | `6` | The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past ... |
| 104 | `CentralBank.MAX_ADVANCES_CEILING` | `36` | The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. |
| 107 | `CentralBank.REVENUE_MONTHS` | `12` | How many months of the treasury's revenue the ceiling is averaged over. |
| 110 | `CentralBank.MAX_QE_SHARE` | `1.0` | The most of the city's term paper the holdings dial may aim at: all of it since 0.7.15, half before - Jerus: "central bank bond holding can ve 100% if one wants", a backstop for when the bank and i... |
| 113 | `CentralBank.FULL_COMPRESSION_SHARE` | `.5` | The holding at which the term premium is wholly compressed: half the term paper, where it has been since 0.7.1 - MAX_QE_SHARE's value until the dial went past it (DebtManager.compression()). |
| 116 | `CentralBank.QE_SPEED` | `.25` | The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying towar... |
| 119 | `CentralBank.QE_COMPRESSION` | `1.0` | How much of the term premium a holding of FULL_COMPRESSION_SHARE takes away: all of it, at 1. |

### ChartModel.java ([map](map/ChartModel.md))

| line | constant | value | says |
|---:|---|---|---|
| 37 | `ChartModel.MIN_SPAN` | `6` | The fewest months the main chart can be zoomed down to: half a year, seven points. |
| 40 | `ChartModel.RANGES` | `{ 12, 60, 120, 600 }` | The range buttons, in months: one, five, ten and fifty years; "All" is the whole history. |
| 43 | `ChartModel.RANGE_NAMES` | `{ "1Y", "5Y", "10Y", "50Y", "All" }` | What the range buttons say, in RANGES' order, then the whole history's. |
| 46 | `ChartModel.ALL` | `Integer.MAX_VALUE` | The range that means the whole history. |
| 49 | `ChartModel.DEFAULT_RANGE` | `120` | The range a chart opens on: ten years, what City History drew before it had buttons. |
| 52 | `ChartModel.ZOOM_STEP` | `0.85` | What one notch of the wheel leaves in view, zooming in; zooming out is its inverse. |
| 55 | `ChartModel.YEAR_LABEL_PX` | `46` | The least room, in pixels, between two year labels: a "2141" and a gap. |
| 58 | `ChartModel.MONTH_LABEL_PX` | `36` | The least room between two month labels: a "Mar" and a gap. |
| 61 | `ChartModel.YEAR_STEPS` | `{ 1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000 }` | The year steps the axis may label, smallest first: every year, every second, every fifth... |
| 64 | `ChartModel.MONTH_STEPS` | `{ 1, 2, 3 }` | The month steps a zoomed-in axis may label between its years: monthly, two-monthly, quarterly - no coarser, or ten years across a wide screen would be half-years. |
| 67 | `ChartModel.AXIS_PAD` | `.06` | Headroom a value axis leaves above and below what is drawn, as a share of the range: a line at its extreme is not drawn along the frame. |

### CityCalendar.java ([map](map/CityCalendar.md))

| line | constant | value | says |
|---:|---|---|---|
| 28 | `CityCalendar.EPOCH_YEAR` | `2000` | The year month 1 falls in. |
| 30 | `CityCalendar.MONTHS` | `{ "January", "February", "March", "April", "May", "June", "July", "August", "September"...` |  |
| 35 | `CityCalendar.SHORT_MONTHS` | `{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }` |  |
| 41 | `CityCalendar.DAYS` | `{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }` | Days in each month of a common year; February is corrected below. |

### CityNeeds.java ([map](map/CityNeeds.md))

| line | constant | value | says |
|---:|---|---|---|
| 104 | `CityNeeds.PLAIN` | `new Words() { @ Override public String people(double count) { return Formats.INSTANCE.c...` | The same four shapes without the toolkit (Formats), for a harness and a log. |
| 126 | `CityNeeds.NETWORK_YELLOW` | `.75, NETWORK_RED = 1` | A network is listed from three-quarters of its capacity, red once it is over. |
| 129 | `CityNeeds.GENERAL_YELLOW` | `.80, GENERAL_RED =.50` | General care is watched hardest: it moves the sick rate. |
| 132 | `CityNeeds.OTHER_CARE_YELLOW` | `.70, OTHER_CARE_RED =.40` | Childcare and senior care kill at the ends of life; a young city legitimately has neither for a while. |
| 135 | `CityNeeds.PLOTS_WATCHED` | `120` | Burial plots are watched once fewer than this many months are left. |
| 137 | `CityNeeds.PLOTS_YELLOW` | `24, PLOTS_RED = 6` | ...listed under two years of plots, red under six months. |
| 140 | `CityNeeds.SCHOOLS_YELLOW` | `.90, SCHOOLS_RED =.60` | The basic ladder's bottleneck, taught. |
| 143 | `CityNeeds.SEATS_YELLOW` | `1.05, SEATS_RED = 2` | A school above the ladder: who would come over its seats. |
| 146 | `CityNeeds.SEATS_FLOOR` | `25` | A class's worth: fewer would-be students than this and a school is not a row (measured: 3 would-be law students in a city of 1,650). |
| 149 | `CityNeeds.CRIME_YELLOW` | `1.2, CRIME_RED = 1.5` | Crime against Canada's rate. |
| 152 | `CityNeeds.CELLS_YELLOW` | `1, CELLS_RED = 25` | The caught, not held. |
| 160 | `CityNeeds.SICK_YELLOW` | `.06, SICK_RED =.12` | The sick rate, the share of the workforce off sick (0.7.28): the left panel's OFF SICK lines (SummaryScreen's literals until then), here so the Services screen colours the same figure by the same l... |
| 169 | `CityNeeds.FALLS_DUE_MONTHS` | `3` | FALLS DUE (0.7.24): paper due within this many months is red, as the bottom strip drew its maturity chip until it left the frame (its maturity chip, FinancesScreen's until 0.7.32 and its urgency() ... |
| 177 | `CityNeeds.FALLS_DUE_SOON_MONTHS` | `12` | ...and paper due within this many months is amber: the strip's other rule (gap <= 12), named in 0.7.32 so the Finances tab's NEXT DUE, its book and its strip colour a maturity by one line. |
| 187 | `CityNeeds.SERVICE_FELT` | `.12, SERVICE_CONSTRAINED =.25` | DEBT SERVICE, A SHARE OF THE TAKE (0.7.32): what the city pays its lenders against what it collects is comfortable under SERVICE_FELT, felt from there, and constrained past SERVICE_CONSTRAINED - th... |
| 197 | `CityNeeds.YEAR_WALL` | `.5` | A WALL ON THE LADDER (0.7.32): a calendar year whose payments - coupons and principal - pass this share of a year of revenue is one a city meets by refinancing before it arrives. |
| 399 | `CityNeeds.GROUND_YELLOW` | `LandManager.BLOCK_SQ_FT` | Free ground under which NEEDS YOU lists the GROUND row: a block, 100,000 sq ft. |

### ConstructionControl.java ([map](map/ConstructionControl.md))

| line | constant | value | says |
|---:|---|---|---|
| 110 | `ConstructionControl.STANDARD_HOURS` | `40` | The normal working week the report measures against, in hours. |
| 113 | `ConstructionControl.OVERTIME_HOURS` | `50` | The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). |
| 116 | `ConstructionControl.OVERTIME_RATE` | `1.5` | What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). |
| 119 | `ConstructionControl.WEEKS_A_MONTH` | `52.0 / 12.0` | Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. |
| 122 | `ConstructionControl.OVERTIME_WEEKS_ENDING` | `{ 2, 4, 6, 8, 10 }` | Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. |
| 125 | `ConstructionControl.OVERTIME_PRODUCTIVITY` | `{ 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 }` | Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). |
| 128 | `ConstructionControl.OVERTIME_WAGE_BILL` | `(STANDARD_HOURS +(OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDARD_HOURS` | The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. |
| 233 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,4... |

### Consumption.java ([map](map/Consumption.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `Consumption.FILE_NAME` | `"consumption.json"` |  |
| 117 | `Consumption.ENGEL_A` | `1.0` | The share at subsistence: all of it. |
| 120 | `Consumption.ENGEL_B` | `(ENGEL_A -.24) / Math.log(90)` | How fast the share falls with log income. |
| 127 | `Consumption.SATIATION` | `1.15` | How much past need a household eats when money has stopped being the constraint. |
| 144 | `Consumption.ENGEL_MIN_SHARE` | `ENGEL_B` | Where the curve stops being a description of anything, DERIVED. |
| 155 | `Consumption.TIME_WEIGHT` | `.40` | How hard the time axis pushes, per dependant an earner carries. |
| 158 | `Consumption.KCAL_A_MONTH` | `2_000 * 30` | What one person needs in a month, in calories. |

### CorporateBond.java ([map](map/CorporateBond.md))

| line | constant | value | says |
|---:|---|---|---|
| 76 | `CorporateBond.TERM_MONTHS` | `Mortgage.MORTGAGE_TERM_MONTHS` | The term every bond is issued at: ten years, the benchmark tenor of investment-grade corporate issuance, and the term the model already prices at (the insured mortgage's, Mortgage.MORTGAGE_TERM_MON... |
| 79 | `CorporateBond.COUPONS_A_YEAR` | `12` | Coupons a year: one a month, the model's month; real bonds pay twice a year. |

### Crime.java ([map](map/Crime.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `Crime.CANADA_OFFICERS_PER_100K` | `180` | Police officers per 100,000 people, Canada, 2025 (StatCan: 75,107 officers). |
| 74 | `Crime.FULL_OFFICERS_PER_100K` | `2 * CANADA_OFFICERS_PER_100K` | Full coverage: twice Canada's level. |
| 77 | `Crime.CANADA_CRIMES_PER_100K` | `5_585` | Police-reported crime per 100,000 people a year, Canada, 2025. |
| 80 | `Crime.CANADA_HOMICIDES_PER_100K` | `1.61` | Homicides per 100,000 people a year, Canada, 2025. |
| 83 | `Crime.CANADA_PRISONERS_PER_100K` | `127` | Prisoners per 100,000 people, Canada, about - what the Services screen's prisons say beside the city's (0.7.28; a literal in the screen until then). |
| 86 | `Crime.MAX_DETERRENCE` | `.90` | What full coverage takes off. |
| 94 | `Crime.DETERRENCE_POWER` | `1.5` | The curve between none and full. |
| 97 | `Crime.NO_POLICE_WEIGHT` | `2` | Every adult at liberty, tempted this much more with no police at all. |
| 100 | `Crime.VIOLENT_SHARE` | `.25` | A quarter of crime is violent. |
| 103 | `Crime.MONTHS_OFF_PER_VIOLENT` | `.5` | Months off work per violent crime, on the sick rate. |
| 106 | `Crime.KILLED_PER_VIOLENT` | `CANADA_HOMICIDES_PER_100K /(CANADA_CRIMES_PER_100K * VIOLENT_SHARE)` | Of violent crimes, the share that kill: Canada's homicides over its violent crime, 0.115%. |
| 110 | `Crime.THEFT_WAGE_SHARE` | `.25` | What one property crime takes, as a share of a month's unskilled wage. |
| 113 | `Crime.FROM_HOUSEHOLDS` | `.5` | ...of which this much from households, the rest from the businesses. |
| 116 | `Crime.CAUGHT_AT_FULL` | `.09` | The share of crimes that end in a sentence at full coverage; straight down to none with no police. |
| 119 | `Crime.SENTENCE_MONTHS` | `6` | Months a sentence lasts. |
| 156 | `Crime.CAUSES` | `Cause.values().length` |  |
| 227 | `Crime.K` | `strikeK()` | Crimes a month per point of pressure. |
| 462 | `Crime.STATE_LENGTH` | `SENTENCE_MONTHS + 5 + CAUSES + 13 + 2 + 5` |  |

### Currency.java ([map](map/Currency.md))

| line | constant | value | says |
|---:|---|---|---|
| 69 | `Currency.FOREIGN_NAME` | `"US dollar"` | The world's money, which the game holds exactly one of. |
| 70 | `Currency.FOREIGN_CODE` | `"USD"` |  |
| 71 | `Currency.FOREIGN_SYMBOL` | `"US$"` |  |
| 74 | `Currency.FOREIGN_CENT_SYMBOL` | `"US\u00a2"` | ...and its hundredth, for what one local dollar buys once it is worth less than one of them. |
| 87 | `Currency.SYMBOL` | `"$"` | Written alone, where nothing foreign is in sight: "$" for every city, derived or typed. |
| 90 | `Currency.DERIVED_NOUN` | `"dollar"` | What a derived currency is: "<City> dollar". |
| 93 | `Currency.CODE_LENGTH` | `3` | Exactly this many letters A-Z in a code, as ISO 4217 has. |
| 96 | `Currency.CODE_PAD` | `'X'` | What a derived code is padded with when the name has fewer than three letters A-Z: ISO 4217's own letter for money that belongs to no country (XAU, XDR). |
| 99 | `Currency.MAX_NAME_LENGTH` | `32` | The longest name a player may type for their money: a line on the trade tab's rate, not a policy. |
| 110 | `Currency.DANZIK` | `new Currency("Danzik dollar", "Danzik dollars", "DZD", SYMBOL, "D$")` | THE DANZIK DOLLAR: every city's money until 0.7.10, and so the money of every save written before then, which has no currency of its own on file (Founding.legacy()). |

### DebtManager.java ([map](map/DebtManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 72 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 141 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 144 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 147 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 150 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 193 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 317 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 352 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 355 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 358 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 495 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 498 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 501 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 508 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 511 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 514 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 517 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 520 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 523 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1039 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1042 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 1297 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1300 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1303 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1306 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1309 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1312 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TERM_PREMIUM_...` | The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. |
| 1797 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### DebtQuote.java ([map](map/DebtQuote.md))

| line | constant | value | says |
|---:|---|---|---|
| 203 | `DebtQuote.FORMAT` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### DecisionLog.java ([map](map/DecisionLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `DecisionLog.TAX` | `"tax"` | A tax rate, an offset or the farmland relief. |
| 59 | `DecisionLog.PROMISE` | `"promise"` | A promise: the wage floor, pensions, EI, schools, health, the fare, a standing subsidy. |
| 62 | `DecisionLog.CENTRAL_BANK` | `"central bank"` | The central bank's dials: the rate, the rule, the target, its holdings, its advances. |
| 65 | `DecisionLog.CURRENCY` | `"currency"` | The money itself: a reform, the vault bought or sold, how land is paid for. |
| 68 | `DecisionLog.BORROWING` | `"borrowing"` | The city's paper: an issue, a buyback, the rollover's setting, a default abroad. |
| 71 | `DecisionLog.BANK` | `"bank"` | The commercial bank: a rescue, the preferred offer, the rescue setting. |
| 74 | `DecisionLog.FUND` | `"fund"` | The city's fund: its dial, and the hand on it. |
| 77 | `DecisionLog.CONSTRUCTION` | `"construction"` | The construction queue (0.7.22): the order, rushes, cancels, restarts, demolitions, buy-outs. |

### DemolitionLog.java ([map](map/DemolitionLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 30 | `DemolitionLog.KEEP_MONTHS` | `24` | How long a demolition stays on the panel. |
| 33 | `DemolitionLog.MAX_ENTRIES` | `40` | Hard cap, so a city demolishing constantly cannot grow this without limit. |

### Denomination.java ([map](map/Denomination.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `Denomination.UNLOCK_AT` | `10.0` | How far prices have to have risen before the button appears. |
| 99 | `Denomination.FACTORS` | `{ 10, 100, 1000 }` | The factors the player may choose between. |
| 111 | `Denomination.MAX_UNIT` | `1e12` | The ceiling on the unit, and it is a numeric guard rather than a policy. |

### EconomyManager.java ([map](map/EconomyManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 530 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, BuildingTy...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 542 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 706 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 708 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 710 | `EconomyManager.NRRP_CAP` | `6.3` | ...and its most a unit, in founding thousands: $6,300. |
| 712 | `EconomyManager.NRRP_FULL_BELOW` | `350` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 714 | `EconomyManager.NRRP_NONE_FROM` | `450` | ...and none for a unit worth this or more: $450,000. |
| 1949 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### Education.java ([map](map/Education.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 245 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 258 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 261 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 279 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 288 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 988 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

### Equity.java ([map](map/Equity.md))

| line | constant | value | says |
|---:|---|---|---|
| 108 | `Equity.COMPANIES` |  | The companies, in register order: every sector in the registry's order, then the bank. |
| 109 | `Equity.BANK` |  |  |
| 126 | `Equity.PAYOUT` | `.40` | The share of a positive month's net income paid to the owners - of what it leaves after the principal repaid, since round 2 of 0.7.11 (dividendDue()) - every company's but the bank's, which pays by... |
| 129 | `Equity.FOUNDING_PRICE` | `1.0` | A founding share: a thousand dollars, in the game's thousands. |
| 161 | `Equity.RECORD_MONTHS` | `12` | Months on the books before a company has a record to be judged on. |
| 164 | `Equity.GOOD_MONTHS` | `9` | Profitable months of the last twelve that make a good year. |
| 167 | `Equity.BAD_MONTHS` | `6` | ...and the most a bad year has. |
| 170 | `Equity.BASE_EQUITY_SHARE` | `.30` | What a steady business keeps as equity: the rest is leverage. |
| 173 | `Equity.RISK_SLOPE` | `.20` | How much the target rises per unit of income swing (std dev over \|mean\|). |
| 175 | `Equity.MAX_EQUITY_SHARE` | `.70` |  |
| 178 | `Equity.NEW_EQUITY_SHARE` | `.50` | A new company's plans are this much equity, whatever its assets say. |
| 181 | `Equity.HORIZON_YEARS` | `3` | In good times, the years of expansion a company raises for ahead. |
| 184 | `Equity.UNDER_TARGET` | `.10` | Under target by this much before a normal year raises instead of borrows. |
| 187 | `Equity.FOREIGN_PREMIUM` | `.03` | What the world wants over its own rate to buy a share here, annual. |
| 966 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 969 | `Equity.SLOTS_BEFORE_PAID` | `SLOTS_BEFORE_DESK + 2` | ...and before the dividends actually paid (0.7.12 round 2). |
| 972 | `Equity.SLOTS_BEFORE_CITY` | `SLOTS_BEFORE_PAID + RECORD_MONTHS + 1` | ...and before the city's fund (0.7.14): the ring of dividends paid and its count, appended. |
| 975 | `Equity.SLOTS` | `SLOTS_BEFORE_CITY + 3` | The city's shares, its rescue book and what it has been paid, appended (0.7.14). |

### Exchange.java ([map](map/Exchange.md))

| line | constant | value | says |
|---:|---|---|---|
| 132 | `Exchange.SPREAD` | `.02` | The desk's ask over its bid, as a share of fair value. |
| 135 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 138 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 141 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a yield at least this far over the deposit rate, annual. |
| 149 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtSer... |
| 152 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 170 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 185 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 188 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 191 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). |
| 257 | `Exchange.POSITION_LIMIT` | `.25` | The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). |
| 268 | `Exchange.BOOK_LIMIT` | `.50` | ...and in all companies' together: the book's aggregate limit. |
| 271 | `Exchange.SPLIT_AT` | `100` | A share priced at this many times its founding price is split; at one over it, consolidated. |
| 281 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the market treats the company as worthless. |
| 297 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 317 | `Exchange.DESK` | `"bank"` | The bank's trading desk. |
| 319 | `Exchange.WORLD` | `"world"` | The world's investors. |
| 321 | `Exchange.EMIGRANTS` | `"emigrants"` | The month's leavers, selling on the way out. |
| 323 | `Exchange.CELL` | `BondMarket.CELL` | One household cell: this, then its key (the bond market's prefix, deliberately). |
| 325 | `Exchange.FUND` | `BondMarket.FUND` | The city's fund, by its rule (0.7.14; TreasuryFund) - the bond market's name, deliberately. |
| 327 | `Exchange.FUND_HAND` | `BondMarket.FUND_HAND` | ...and by the player's hand: its own name, so the two never trade with each other and the hand's sale can reach the rescue book. |
| 713 | `Exchange.BOUND_CAPITAL` | `0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3` | Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. |
| 1317 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3, BY_FUND = 4` | Who sells, by class: the desk, the world, the leavers, a household cell, and since 0.7.14 the city's fund - for the fill rate by seller. |
| 1645 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 1647 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

### FamilyModel.java ([map](map/FamilyModel.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `FamilyModel.REFORMING_EACH_MONTH` | `.01` | The share of households that re-form on their own each month. |
| 155 | `FamilyModel.SEEKERS` | `Seeker.values().length` |  |
| 996 | `FamilyModel.STUDIO_MAX_SIZE` | `2` | The largest unit that counts as a studio. |
| 1621 | `FamilyModel.MAX_SHARING` | `.85` | Not everybody doubles up, however dear the rent. |
| 1682 | `FamilyModel.COUPLED_SENIORS` | `.55` | What share of a retired band lives as a couple rather than alone. |
| 1683 | `FamilyModel.COUPLED_ELDERS` | `.25` |  |
| 1799 | `FamilyModel.LEGACY_SHAPES` | `{ "SENIOR_ALONE", "SENIOR_COUPLE", "SINGLE_ADULT", "COUPLE", "SINGLE_PARENT", "COUPLE_B...` | The shapes a save written before the names travelled must be read with. |
| 1821 | `FamilyModel.OUTSIDE_SLOTS` | `outsideSlots(AgeBand.values().length)` | What the people outside the families add to the save: see toSaveArray(). |
| 1866 | `FamilyModel.KIN_SLOTS` | `kinSlots(AgeBand.values().length)` |  |
| 1883 | `FamilyModel.MEMORY_SLOTS` | `FamilyStructure.values().length * PayTier.values().length + 1 + 4` | ...and what the households remember: the formed matrix, whether there is one, the month's four counts. |

### ForeignAccounts.java ([map](map/ForeignAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 105 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 127 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 130 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 133 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 143 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 181 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 183 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 261 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 321 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 384 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 387 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 497 | `ForeignAccounts.RATE_PULL` | `4.0` | How far the city's own rate is above the world's - in real terms since 0.7.2 - and what that is worth to the currency. |
| 500 | `ForeignAccounts.MAX_RATE_PRESSURE` | `4.0` | A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 p... |
| 932 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |
| 1486 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the sp... |
| 1489 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 1504 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb. |

### Formats.java ([map](map/Formats.md))

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |

### Founding.java ([map](map/Founding.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `Founding.INSANE_LAND_COUPON` | `.03` | Insane's coupon on the land it owes for, a year: 3%, Jerus's number. |
| 68 | `Founding.INSANE_LAND_YEARS` | `20` | Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the five term loans (LongTermBond.MATURITIES). |
| 76 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - two-thirds of the founding village at a new city's invoices (three-quarters until the builders' sales tax went into them, 0.7.19), so the city borrows from it... |
| 79 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 82 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 85 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 159 | `Founding.MIN_CASH` | `6_000` | The least a city may be founded with in its treasury, in thousands: D$6M, ten houses and a shop at a new city's invoices - D$5.37M since the builders' sales tax went into them (0.7.19; it was D$5M,... |
| 162 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 165 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 168 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 173 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 176 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 325 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "Constructi...` | The founding village: the playtest's hand-built settlement, name and count. |
| 329 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

### FundLedger.java ([map](map/FundLedger.md))

| line | constant | value | says |
|---:|---|---|---|
| 68 | `FundLedger.ACTIVITY_ROWS` | `1000` | The newest activity rows kept: the played research cities made 0-5 a month, and 37 in the busiest month after a pay-in, so this is some fifteen years of a quiet fund and two of a busy one. |
| 71 | `FundLedger.DUST` | `OrderBook.DUST` | Anything this small, in units or money, is the arithmetic's dust: a lot holding less is closed, a fill this small is no trade (OrderBook.DUST). |
| 74 | `FundLedger.WRITE_DOWN_ROW` | `1e-3` | A write-down that takes less than this share of a bond's face is booked on its lot but given no row: a restructure keeping all but a sliver of the face (the 600-month research city's year made 24 s... |
| 77 | `FundLedger.BARGAIN` | `.05` | A buy that paid at least this share less than what it bought was worth at the step - a share's fair value, a bond's value - was a bargain, and its lot and its row say so (the spec's 5: the 600-mont... |
| 82 | `FundLedger.BUY` | `"BUY", SELL = "SELL", LAPSED = "LAPSED", DIVIDEND = "DIVIDEND", COUPON = "COUPON", MATU...` | What an activity row records. |
| 88 | `FundLedger.SHARES` | `"SHARES", RESCUE_BOOK = "RESCUE", BOND = "BOND"` | What a lot is: a company's market book, a rescue book, a bond. |
| 95 | `FundLedger.PREFERRED_KEY` | `"P:Bank", WARRANTS_KEY = "W:Bank"` | The preferred and the warrants, which are rows and not lots. |

### FundView.java ([map](map/FundView.md))

| line | constant | value | says |
|---:|---|---|---|
| 35 | `FundView.STALE_MONTHS` | `3` | A share's last trade older than this many months is called stale on every page that shows it (the spec's B3: a price is its last trade, however old). |
| 38 | `FundView.MIN_RECORDED_PRICE` | `.01` | The least a price per founding share, as the history records it to four places, can be and still carry three significant figures: a move off less is not shown (the spec's B6 - a consolidated share ... |
| 41 | `FundView.FOLD_UNDER` | `1` | A holding worth less than this, money, is folded into "and N more" on the Portfolio page: a thousand dollars. |
| 44 | `FundView.MOVE_MONTHS` | `12` | The months a hit's sparkline and its move cover: a year. |
| 47 | `FundView.SHARE` | `"share", RESCUE = "rescue", BOND = "bond", PREFERRED = "preferred", WARRANTS = "warrants"` | What a holding is. |
| 50 | `FundView.CLOSED` | `"closed"` | A search hit that is a lot the fund closed on a bond no longer on any book: found only when named (the spec's D1). |
| 389 | `FundView.HELD` | `"YOUR HOLDINGS", SHARES_SECTION = "SHARES", BONDS_SECTION = "BONDS"` | What a search hit is for: the market's sections when the box is empty. |
| 775 | `FundView.TRADES` | `"Trades", INCOME = "Income", MONEY = "Money in and out", EVENTS = "Events"` | The activity page's chips: what each kind of row is filed under. |

### Game.java ([map](map/Game.md))

| line | constant | value | says |
|---:|---|---|---|
| 559 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 862 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 865 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town... |
| 868 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 5813 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 6579 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 6737 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 6859 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 8346 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 8358 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 8361 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 8376 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 8385 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, W... |
| 8393 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 10116 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |
| 11432 | `Game.EARNED_FARES` | `"Transit fares: on the cash, not the budget"` | The fares' step's words: in EARNED and in the cash, not in the budget. |

### GameFiles.java ([map](map/GameFiles.md))

| line | constant | value | says |
|---:|---|---|---|
| 54 | `GameFiles.APP_NAME` | `"CityBuilderSim"` |  |
| 56 | `GameFiles.SAVE_FILE` | `"save.json"` |  |
| 57 | `GameFiles.HISTORY_FILE` | `"history.json"` |  |
| 71 | `GameFiles.SLOT_COUNT` | `10` |  |
| 72 | `GameFiles.AUTOSAVE_SLOT` | `0` |  |
| 73 | `GameFiles.SAVES_FOLDER` | `"saves"` |  |
| 76 | `GameFiles.LEGACY_FOLDER` | `"YourGame"` | The folder the game used before this class existed. |

### GameLog.java ([map](map/GameLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 52 | `GameLog.LOG_FILE` | `"log.txt"` |  |
| 53 | `GameLog.PREVIOUS_LOG_FILE` | `"log-previous.txt"` |  |
| 62 | `GameLog.MAX_BYTES` | `2_000_000` | Above this, the file is rotated mid-run. |
| 64 | `GameLog.STAMP` | `DateTimeFormatter.ofPattern("HH:mm:ss")` |  |

### GamePrefs.java ([map](map/GamePrefs.md))

| line | constant | value | says |
|---:|---|---|---|
| 32 | `GamePrefs.FILE` | `"settings.json"` |  |
| 118 | `GamePrefs.DEFAULT_PINNED_LEFT` | `"realGdp"` | The two small charts pinned at the top of the Reports page, by series name - the name HistorySave and the page's own list know a line by. |
| 121 | `GamePrefs.DEFAULT_PINNED_RIGHT` | `"population"` | ...and the right-hand one, the population. |

### GameVersion.java ([map](map/GameVersion.md))

| line | constant | value | says |
|---:|---|---|---|
| 2445 | `GameVersion.VERSION` | `"0.7.39"` | Bump on release. |
| 2949 | `GameVersion.SAVE_FORMAT` | `30` | The save shape. |
| 2952 | `GameVersion.FIRST_SECTOR_FORMAT` | `21` | The first format a sector can be read out of. |
| 2954 | `GameVersion.NAME` | `"CityBuilderSim"` |  |

### GoodsMarket.java ([map](map/GoodsMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 52 | `GoodsMarket.STOCK_RELEASE_MONTHS` | `6` | How many months it would take to release the whole stockpile into the market. |
| 55 | `GoodsMarket.NO_CEILING_MULTIPLE` | `2` | Where the price sits in a band with no ceiling: up to this multiple of the floor. |
| 90 | `GoodsMarket.TREND_MONTHS` | `36` | The longest window any good plans over - see Good.planningMonths() for how many of these months a good actually reads. |

### Health.java ([map](map/Health.md))

| line | constant | value | says |
|---:|---|---|---|
| 69 | `Health.WELL_SERVED_RATE` | `.03` | Absence when general care covers everybody. |
| 72 | `Health.UNTREATED_RATE` | `.18` | Absence with no general care at all. |
| 75 | `Health.MAX_SICK_RATE` | `.45` | No month loses more of the workforce than this, outbreak included. |
| 97 | `Health.UNBURIED_WEIGHT` | `20` | Multiplier on the unburied share of the population. |
| 100 | `Health.MAX_UNBURIED_SICKNESS` | `.15` | However many are lying about, this is the most it can cost. |
| 111 | `Health.OUTBREAK_CHANCE` | `1 / 60.0` | Chance per month that an outbreak begins, when none is running. |
| 114 | `Health.OUTBREAK_MIN_PEAK` | `.08` | Extra absence at the peak of an outbreak, before coverage blunts it. |
| 115 | `Health.OUTBREAK_MAX_PEAK` | `.25` |  |
| 118 | `Health.OUTBREAK_DECAY` | `.55` | What is left of the peak each following month. |
| 121 | `Health.OUTBREAK_FLOOR` | `.005` | Below this the outbreak is over. |
| 131 | `Health.OUTBREAK_MITIGATION` | `.60` | How much of an outbreak's peak good coverage can take off. |
| 134 | `Health.SEED` | `411_902_537_009L` | Fixed, like LandMarket's. |
| 175 | `Health.HUNGER_WEIGHT` | `.15` | How much sickness a city that cannot feed itself carries. |
| 176 | `Health.MAX_HUNGER_SICKNESS` | `.08` |  |

### Healthcare.java ([map](map/Healthcare.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `Healthcare.FOUNDING_CITY` | `1200` | How many residents the founding endowment was meant to serve. |
| 69 | `Healthcare.FOUNDING_PLOTS` | `2500` | Graves in the old churchyard. |
| 128 | `Healthcare.GENERAL_FEE` | `.010` |  |
| 129 | `Healthcare.CHILDCARE_FEE` | `.150` |  |
| 130 | `Healthcare.SENIOR_FEE` | `.300` |  |
| 167 | `Healthcare.BURIAL_FEE` | `3.000` |  |
| 168 | `Healthcare.CREMATION_FEE` | `.900` |  |
| 246 | `Healthcare.BURIAL_SAVING_MONTHS` | `120` | How long a household is assumed to be putting money aside for a funeral. |
| 265 | `Healthcare.MAX_BACKLOG_MONTHS` | `24` | How far behind a city can get before it starts improvising. |
| 305 | `Healthcare.CHILDCARE_SWING` | `40` | Children, and it is enormous - per Jerus, "really really really". |
| 326 | `Healthcare.SENIOR_SWING` | `1.35` | Seniors, and it stays gentle. |
| 342 | `Healthcare.ELDER_SWING` | `1.80` | And what it is worth to the over-85s, which is more. |
| 353 | `Healthcare.CHILDCARE_BIRTH_BONUS` | `1.0` | How much more a city with childcare gives birth. |
| 815 | `Healthcare.STRAINED` | `.90` | Past this share of the ovens' throughput, say so. |
| 818 | `Healthcare.PLOT_WARNING_MONTHS` | `60` | Warn once the ground will not last this long at the current rate. |
| 870 | `Healthcare.STATE_BEFORE_FULL_BILL` | `12` | How many figures the state carried before the full-service bill was appended (2026-09-19). |
| 873 | `Healthcare.STATE_BEFORE_COVERAGE` | `13` | ...and before the three coverages were, the same day. |

### Household.java ([map](map/Household.md))

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

### HouseholdAccounts.java ([map](map/HouseholdAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 412 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 415 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 416 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 417 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 419 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 420 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 423 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 994 | `HouseholdAccounts.STATE_SCALARS` | `18, STATE_ROWS = 19` | Scalars and row arrays in the statement's state since 0.7.7. |
| 997 | `HouseholdAccounts.SCALARS_BEFORE_ACCOUNT_FEES` | `17, ROWS_BEFORE_ACCOUNT_FEES = 18` | ...and the shape before the bank's account fee was a line on it (0.7.7). |
| 1000 | `HouseholdAccounts.SCALARS_BEFORE_HEALTH` | `16, ROWS_BEFORE_HEALTH = 15` | ...and the shape before the health premium was a line on it (2026-09-19). |
| 1003 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

### HouseholdBalance.java ([map](map/HouseholdBalance.md))

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
| 331 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provi... |
| 334 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 337 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 359 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 393 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 396 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 399 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 402 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1362 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1659 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1671 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1696 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1755 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1781 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1840 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1849 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 2097 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2643 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 2840 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point - so two and a half points of spread would take half an... |
| 2843 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and never more than this share of one issue: half, because a bond market with no bank in it is no longer the city's bank's market. |
| 3596 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3599 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 3873 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 3876 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 3879 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 3882 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 3885 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 3895 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 3905 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 3914 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 3926 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim ... |
| 3929 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_BONDS + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month'... |

### Inbox.java ([map](map/Inbox.md))

| line | constant | value | says |
|---:|---|---|---|
| 39 | `Inbox.KEEP_MONTHS` | `24` | How long a resolved notice stays readable. |

### InfrastructureManager.java ([map](map/InfrastructureManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 51 | `InfrastructureManager.BASE_CAPACITY` | `400` | The streets that already exist, before the city builds anything. |
| 54 | `InfrastructureManager.FREE_FLOW` | `.9` | Up to this share of capacity, traffic moves freely. |
| 57 | `InfrastructureManager.MIN_THROUGHPUT` | `.35` | However bad it gets, the city does not stop moving entirely. |
| 60 | `InfrastructureManager.STRAINED` | `.85` | Utilisation past which the network is worth warning about. |
| 186 | `InfrastructureManager.TRANSIT_MAX_SHARE` | `.65` | The most of its commuters any city can ever put on transit. |
| 189 | `InfrastructureManager.TRANSIT_NEEDS_ROAD` | `2.0` | Transit capacity a city can use, per unit of road capacity under it. |
| 192 | `InfrastructureManager.BULK_HIGHWAY_RELIEF` | `.40` | What a fully grade-separated network takes off a tonne of bulk. |
| 195 | `InfrastructureManager.GOODS_HIGHWAY_RELIEF` | `.15` | ...and off a crate of goods, which shares fewer junctions to begin with. |
| 218 | `InfrastructureManager.RAIL_ROAD_RELIEF` | `.75` | ...AND RAIL TAKES THE LONG HAUL OFF IT ALTOGETHER, which is the third mode and the only one the city does not own. |
| 264 | `InfrastructureManager.CAR_LOAD_AT_SATURATION` | `3.0` | What a city where every household owns a car asks of the road, against the same city where none does. |
| 282 | `InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK` | `.75` | How many of the people who own a car get on the tram anyway once the road is completely gridlocked. |
| 285 | `InfrastructureManager.JAM_MEMORY` | `.25` | How fast the remembered commute catches up with this month's. |

### InterimLoan.java ([map](map/InterimLoan.md))

| line | constant | value | says |
|---:|---|---|---|
| 29 | `InterimLoan.TYPE` | `"INTERIM-LOAN"` | The type a save carries it under; Game's load reads it back as this class. |

### LabourMarket.java ([map](map/LabourMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 77 | `LabourMarket.DEFAULT_MINIMUM_WAGE` | `PayTier.UNSKILLED.getMonthlyWage()` | Where the minimum wage starts, and therefore what the whole ladder is anchored to on month one. |
| 106 | `LabourMarket.MIN_SETTABLE_SHARE` | `.25` | The lowest the dial goes, as a share of the unskilled wage. |
| 109 | `LabourMarket.MAX_SETTABLE_MULTIPLE` | `5.0` | The highest the dial goes, as a multiple of the unskilled wage. |
| 112 | `LabourMarket.MIN_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE` | Bounds on the dial, so the screen cannot ask for a negative wage. |
| 114 | `LabourMarket.MAX_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE` |  |
| 147 | `LabourMarket.ELASTICITY` | `.5` | How hard a shortage pushes the wage. |
| 158 | `LabourMarket.MAX_MULTIPLE` | `4.0` | How far above base a wage can climb. |
| 161 | `LabourMarket.MIN_MULTIPLE` | `.70` | ...and how far below, before the minimum wage catches it anyway. |
| 192 | `LabourMarket.ADJUST_RATE` | `.12` | How much of the gap to its target a wage closes each month. |
| 202 | `LabourMarket.PINNED_TOLERANCE` | `.02` | How far from its floor a wage counts as PINNED. |
| 328 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 344 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |

### LandManager.java ([map](map/LandManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 48 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 51 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 54 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 88 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres. |
| 120 | `LandManager.COST_GROWTH_PER_BLOCK` | `.02` | Each block bought makes the next this much dearer - annexing outward. |
| 132 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 500 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### LandMarket.java ([map](map/LandMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `LandMarket.LISTING_SIZE` | `9` | Plots on offer at any one time. |
| 80 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands of US dollars. |
| 151 | `LandMarket.PREMIUM_PER_BLOCK_OWNED` | `.008` | Each block already owned makes the next offer this much dearer. |
| 154 | `LandMarket.PREMIUM_PER_1000_PEOPLE` | `.05` | ...and so does each thousand residents. |
| 169 | `LandMarket.IRON_PRICE_PER_TONNE` | `.0004` | What the seller charges for the ore, per tonne in the ground, in thousands of US dollars. |
| 178 | `LandMarket.SQ_FT_PER_DEPOSIT` | `400_000` | Land a single mine occupies, and therefore the room one deposit needs. |
| 181 | `LandMarket.EXTRA_DEPOSIT_CHANCE` | `.28` | Chance that a parcel with ore has one MORE site, each time it is asked. |
| 184 | `LandMarket.MAX_DEPOSITS` | `4` | However big the tract, this many sites is the most it will ever carry. |
| 221 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 224 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 236 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 250 | `LandMarket.MIN_BLOCKS` | `1` | The smallest thing the land office will sell, ever: one city block. |
| 265 | `LandMarket.BLOCKS_PER_FLOOR_STEP` | `40` | Blocks the city must already own before the floor rises another block. |
| 272 | `LandMarket.MAX_MIN_BLOCKS` | `15` | A ceiling on the floor. |
| 285 | `LandMarket.SEED` | `705_398_211_733L` | Fixed seed. |
| 634 | `LandMarket.FIELDS_PER_PARCEL` | `5` | Fields written per parcel. |
| 650 | `LandMarket.LISTING_FORMAT_MARKER` | `- FIELDS_PER_PARCEL` | Marks a listing written with deposit counts, and says how wide it is - in LOCAL money, as every listing was until 0.7.6 (USD_LISTING_MARKER). |
| 658 | `LandMarket.USD_LISTING_MARKER` | `- 100 - FIELDS_PER_PARCEL` | Marks a listing whose prices are US DOLLARS (0.7.6), as wide as the one before it. |

### LongTermBond.java ([map](map/LongTermBond.md))

| line | constant | value | says |
|---:|---|---|---|
| 24 | `LongTermBond.MATURITIES` | `{ 10, 20, 30, 40, 50 }` | The only terms a term loan is issued at, in years: five benchmark maturities, so the curve is five points a player can read. |
| 27 | `LongTermBond.REFUSAL` | `"Term loans are issued at 10, 20, 30, 40 or 50 years."` | What the treasury is told when it asks for any other term. |

### Migration.java ([map](map/Migration.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `Migration.TARGET_LABOUR_SLACK` | `.125` | Workers per post the city aims to have, over and above one each. |
| 91 | `Migration.MIN_ADULT_SHARE` | `.25` | Below this the arithmetic stops meaning anything - a city that is 10% adults would demand ten residents per job and grow without limit. |
| 100 | `Migration.JOB_WEIGHT` | `.75` | Jobs are the main factor, per Jerus. |
| 101 | `Migration.HOME_WEIGHT` | `.25` |  |
| 122 | `Migration.SENIOR_CARE_PULL` | `.30` | How much more attractive full senior coverage makes the city. |
| 187 | `Migration.SURPLUS_DEPARTURE_RATE` | `.02` | What share of a band's unemployable surplus leaves each month, once its wage has stopped falling. |
| 220 | `Migration.OPPORTUNITY_FLOOR` | `.15` | What share of a band still moves here when there is no work at its level. |
| 276 | `Migration.PRICED_OUT_WEIGHT` | `1.0` | How much of the affordability excess turns into people not coming. |
| 329 | `Migration.CRIME_PULL_WEIGHT` | `.1` | How much of the crime excess turns into people not coming: a tenth off at twice Canada's rate. |
| 332 | `Migration.CRIME_DEPARTURE_RATE` | `.0005` | The share of the city that leaves a month for each whole Canada of crime over Canada's. |
| 359 | `Migration.ARRIVAL_RATE` | `.15` | How much of the gap closes each month. |
| 360 | `Migration.DEPARTURE_RATE` | `.05` |  |
| 363 | `Migration.DECLINE_MONTHS` | `12` | Consecutive months of falling wages before a tier's people give up. |
| 367 | `Migration.TIERS` | `PayTier.values().length` |  |
| 1221 | `Migration.HISTORY_SLOTS` | `TIERS * DECLINE_MONTHS + TIERS + 1` | The wage history, its streaks and the months recorded: the array before 0.7.27. |
| 1224 | `Migration.LAST_SCALARS` | `16` | The month's scalars that follow it, in toSaveArray()'s order. |

### MoneyAudit.java ([map](map/MoneyAudit.md))

| line | constant | value | says |
|---:|---|---|---|
| 54 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 304 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank, and since 0.7.14 the city's fund (on the end, so every other pool keeps its place). |

### Mortgage.java ([map](map/Mortgage.md))

| line | constant | value | says |
|---:|---|---|---|
| 81 | `Mortgage.MORTGAGE_TERM_MONTHS` | `120` | The term the rate is fixed for, in months: ten years, the term CMHC's 1.20 debt coverage is asked on (1.30 under ten), and the point of the bank's curve it is priced at (Bank.insuredMortgageRate()). |
| 84 | `Mortgage.MORTGAGE_AMORTIZATION_MONTHS` | `480` | The amortization, in months: forty years, the longest CMHC insures standard rental housing over - and no longer than the economic life of the building, which in this game does not wear out. |
| 87 | `Mortgage.MORTGAGE_MAX_LOAN_TO_COST` | `.85` | The most a mortgage lends against the building's cost: 85%, CMHC's highest loan-to-value for standard rental housing - so the landlord puts at least 15% from its own funds. |
| 90 | `Mortgage.MORTGAGE_DEBT_COVERAGE` | `1.20` | The lender's test: the building's net operating income must cover the mortgage's monthly payment this many times - CMHC's minimum debt coverage ratio, 1.20, on a term of ten years or more. |
| 101 | `Mortgage.PREMIUM_RATE` | `.05` | CMHC's premium on a standard rental loan at an LTV of 85% or less, as a share of the loan: 5.00%. |
| 104 | `Mortgage.PREMIUM_SURCHARGE` | `.0025` | The premium's surcharge for each PREMIUM_SURCHARGE_STEP_YEARS of amortization past PREMIUM_BASE_YEARS: 0.25% of the loan (the same schedule), so 0.75% at forty years. |
| 107 | `Mortgage.PREMIUM_BASE_YEARS` | `25` | The amortization the base premium is struck for, in years: 25; a longer one pays PREMIUM_SURCHARGE a step. |
| 110 | `Mortgage.PREMIUM_SURCHARGE_STEP_YEARS` | `5` | How many years of amortization past PREMIUM_BASE_YEARS each step of the surcharge covers: five, up to forty. |
| 299 | `Mortgage.Decision.DOWN_PAYMENT` | `"down payment"` | Trimmed, or refused, because its own funds could not put the down payment on it. |
| 301 | `Mortgage.Decision.LENDERS_TEST` | `"lender's test"` | ...because its rent would not cover the payment MORTGAGE_DEBT_COVERAGE times. |

### NationalAccounts.java ([map](map/NationalAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 680 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

### OrderBook.java ([map](map/OrderBook.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `OrderBook.DUST` | `1e-9` | Anything smaller than this is the arithmetic's dust, not a quantity: an order this small is not rested and a fill this small is not a trade. |

### OutwardInvestment.java ([map](map/OutwardInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `OutwardInvestment.APPETITE` | `40` | The share of a sector's wealth it holds abroad, per unit of spread. |
| 74 | `OutwardInvestment.MAX_SHARE` | `.9` | Never more than this. |
| 81 | `OutwardInvestment.OUT_SPEED` | `.04` | How much of the gap to its target moves abroad in a month. |
| 84 | `OutwardInvestment.HOME_SPEED` | `.10` | ...and how much comes home in a month, which is faster, because it is needed. |
| 108 | `OutwardInvestment.MIN_MOVE` | `1e-9` |  |

### PopulationCohorts.java ([map](map/PopulationCohorts.md))

| line | constant | value | says |
|---:|---|---|---|
| 59 | `PopulationCohorts.BIRTHS_PER_1000_PER_YEAR` | `15.0` | Births per thousand residents per year. |
| 488 | `PopulationCohorts.LEGACY_BANDS` | `{ "BABY", "CHILD", "TEEN", "ADULT", "SENIOR" }` | The bands a save written before names existed must be read with. |

### PopulationManager.java ([map](map/PopulationManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 491 | `PopulationManager.BAND_BASE` | `bandBases()` | Each band's base as a multiple of the unskilled floor: what its ungated posts pay at the going rate (LabourMarket.ratioOf()). |
| 1213 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### PriceIndex.java ([map](map/PriceIndex.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 36 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 55 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |

### Rollover.java ([map](map/Rollover.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `Rollover.NETTING_MONTHS` | `12` | How far back the surplus that nets a maturity is read, in months: Jerus's "net of last year's surplus" - a year. |
| 99 | `Rollover.BILL_MONTHS` | `12` | The note TWELVE_MONTH_BILL rolls into, in months: twelve, the longest note the treasury sells (the Finances tab's notes run 3 to 12 months). |

### SalesTaxLedger.java ([map](map/SalesTaxLedger.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `SalesTaxLedger.TAXABLE_SALES` | `0, IMPORT_TAX = 1, ZERO_RATED = 2, CREDITED_INPUT = 3, PAYABLE = 4, CREDIT = 5, SLOTS = 6` | slots in a row |

### Sector.java ([map](map/Sector.md))

| line | constant | value | says |
|---:|---|---|---|
| 231 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 321 | `Sector.Staffing.ANY` | `new Staffing(1, new double [ 0 ], new double [ 0 ], new double [ 0 ], new boolean [ 0 ])` | Always staffable: a building with no posts, or nobody to ask. |
| 645 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 651 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 680 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 694 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 1548 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 1551 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

### SectorFlow.java ([map](map/SectorFlow.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `SectorFlow.THROTTLES` | `{ "staffed", "power", "water", "roads", "well", "vans" }` | The six throttles in getOperatingRate()'s order, as the screen names them. |

### Sectors.java ([map](map/Sectors.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = "Construct...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a... |
| 76 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERIALS, BUSIN...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |
| 243 | `Sectors.HOUSEHOLDS` | `"Households"` | The name the households' own imports are kept under among a good's buyers: the cars they buy from the world (Game.getHouseholdCarImports()). |

### Sickness.java ([map](map/Sickness.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `Sickness.RING` | `13` | Slots in each band's ring: 0 is under a month, 12 is a year or more. |
| 51 | `Sickness.DEADLY_FROM` | `2` | The first slot that can die: sick for more than two months. |
| 61 | `Sickness.EXTRA_SICKNESS` | `1.0` | How much sicker babies and seniors are than the city, with none of their own care: twice. |
| 64 | `Sickness.RECOVERY_UNTREATED` | `.5` | The share of the sick who get better in a month, with no general care... |
| 67 | `Sickness.RECOVERY_SERVED` | `.9` | ...and with general care for everybody. |
| 70 | `Sickness.MAX_SHARE` | `Health.MAX_SICK_RATE` | No band has more of itself sick than the city rate may. |

### SocialSecurity.java ([map](map/SocialSecurity.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `SocialSecurity.DEFAULT_CONTRIBUTION_RATE` | `.0595` | Taken off every wage, at the real CPP employee rate. |
| 70 | `SocialSecurity.CONTRIBUTION_RATE` | `DEFAULT_CONTRIBUTION_RATE` | Kept as the default so a screen or a harness written against the constant still names the right number. |
| 86 | `SocialSecurity.DEFAULT_PENSION_REPLACEMENT` | `.45` | What the pension replaces, as a share of an unskilled wage. |
| 89 | `SocialSecurity.PENSION_REPLACEMENT` | `DEFAULT_PENSION_REPLACEMENT` |  |

### StudentHousehold.java ([map](map/StudentHousehold.md))

| line | constant | value | says |
|---:|---|---|---|
| 32 | `StudentHousehold.UNLIMITED` | `1e15` | A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. |

### TaxPolicy.java ([map](map/TaxPolicy.md))

| line | constant | value | says |
|---:|---|---|---|
| 82 | `TaxPolicy.DEFAULT_INCOME_TAX` | `.15` | Where income tax started before it was a dial. |
| 92 | `TaxPolicy.DEFAULT_PROPERTY_TAX` | `.015` | 1.5% a year, near the real-world average. |
| 95 | `TaxPolicy.MAX_PROPERTY_TAX` | `.10` | Nobody has ever paid 100% property tax and the game should not model it. |
| 98 | `TaxPolicy.MAX_INCOME_TAX` | `.60` | Above this, income tax stops being a policy and starts being confiscation. |
| 107 | `TaxPolicy.MAX_OFFSET` | `.30` | How far a band or sector may be moved from its tax's base rate, either way. |
| 156 | `TaxPolicy.DEFAULT_FARMLAND_RELIEF` | `1.0` |  |
| 159 | `TaxPolicy.FARM_SECTOR` | `Sectors.AGRICULTURE` | The sector whose ground the relief applies to. |
| 202 | `TaxPolicy.MAX_CONTRIBUTION` | `.20` | Nobody hands over more than a fifth of a wage, whatever the deficit. |
| 205 | `TaxPolicy.MAX_REPLACEMENT` | `1.00` | A pension of more than one unskilled wage is a wage, not a pension. |
| 247 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currenc... |
| 261 | `TaxPolicy.DEFAULT_FIXED_GRANT` | `DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage()` | THE GRANT FOLLOWS PRICES (0.7.19): the default grant as a FIXED amount, in founding thousands - $525 a month, the Canada Student Grant (2026-27) this basis's share was read off, so at founding, whe... |
| 264 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 267 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 270 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 436 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.FIXED` | Where the grant starts: a fixed real amount, kept up with the price index (0.7.19; the founding rule, a share of the unskilled wage, until then). |
| 439 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 442 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 445 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 448 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 451 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 454 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 457 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so ... |
| 489 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 697 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 700 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 703 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 706 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 811 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 814 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | Past this nobody rides at all, as a multiple of the default. |
| 855 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 1225 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 1228 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 1231 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 1234 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 1237 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 1240 | `TaxPolicy.STATE_BEFORE_REAL_GRANT` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | ...and one from before the real FIXED grant (0.7.19): a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |
| 1243 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_REAL_GRANT + 1` | This build's array: one slot on top saying the FIXED amount is real, in founding money (0.7.19) - see realiseFixedGrant(). |

### Trade.java ([map](map/Trade.md))

| line | constant | value | says |
|---:|---|---|---|
| 25 | `Trade.WORLD` | `"world"` | The other side of every export and every import. |
| 28 | `Trade.HOUSEHOLDS` | `"households"` | The households, as a buyer of the basket. |
| 31 | `Trade.CITY` | `"city"` | The treasury, as a buyer of its own buildings' materials and repairs. |

### TreasuryFund.java ([map](map/TreasuryFund.md))

| line | constant | value | says |
|---:|---|---|---|
| 111 | `TreasuryFund.MAX_DIAL` | `3.0` | The most the dial takes: three times the year's surplus - Jerus's "max 300%"; past 100% the extra comes from the treasury's cash. |
| 114 | `TreasuryFund.EQUITY_WEIGHT` | `.70` | The share of the market book in company shares: 70%, the Government Pension Fund Global's strategic equity weight (nbim.no/en/investments); the rest is the businesses' bonds. |
| 117 | `TreasuryFund.REBALANCE_OVER` | `.74` | Rebalanced when its equity share passes this: 74%, the GPFG mandate's "If the equity share ... |
| 120 | `TreasuryFund.REBALANCE_UNDER` | `.04` | ...or falls this far under EQUITY_WEIGHT: four points, the same mandate's "more than four percentage points lower than the weight in the strategic benchmark index". |
| 123 | `TreasuryFund.OWNERSHIP_LIMIT` | `.10` | The most of any one company's shares the rule holds: 10%, the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares in an individual company" (14 May 2018). |
| 126 | `TreasuryFund.TRANSFER_RATE` | `.03` | What the fund pays the budget a year, of its whole value: 3%, Norway's fiscal rule - transfers follow the fund's expected real return, 3% since 2017 (regjeringen.no). |
| 129 | `TreasuryFund.YEAR_MONTHS` | `12` | A year, in months: the transfer's twelfth and the surplus's year. |
| 132 | `TreasuryFund.VOLATILITY_MONTHS` | `12` | The months of the bank share's price history its volatility is read over, for the warrants' value: a year of the monthly prices the history keeps (HistorySave's share prices). |
| 135 | `TreasuryFund.OFFER_AGAIN_MONTHS` | `3` | A declined offer comes back after this many months while the bank is still under its minimum: a quarter (Jerus, round 2: "Quarter") - and the bank asks no sooner after an accepted one either, whose... |

### Unemployment.java ([map](map/Unemployment.md))

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Unemployment.EI_MONTHS` | `12` | Months a claim lasts. |
| 76 | `Unemployment.DEFAULT_PREMIUM_RATE` | `.0163` | The employee premium, 2026: $1.63 per $100 of insurable earnings. |
| 79 | `Unemployment.DEFAULT_BENEFIT_RATE` | `.55` | What EI replaces: 55% of insurable earnings. |
| 82 | `Unemployment.MAX_INSURABLE_MULTIPLE` | `68_900.0 / 12 / 3_460` | Maximum insurable earnings, 2026: $68,900 a year, over the $3,460 unskilled median the ladder is anchored on. |
| 89 | `Unemployment.LEAVE_WHEN_BROKE` | `.25` | The share of the evicted who leave the city rather than stay on the street. |
| 98 | `Unemployment.UNHOUSED_MORTALITY` | `3.7` | How much faster the unhoused die: 3.7x. |
| 101 | `Unemployment.UNHOUSED_SICKNESS` | `3.7` | ...and how much faster they get sick. |
| 541 | `Unemployment.STATE_LENGTH` | `EI_MONTHS * 2 + 6 + PayTier.values().length * 2 + 12` |  |

### UtilitiesHandler.java ([map](map/UtilitiesHandler.md))

| line | constant | value | says |
|---:|---|---|---|
| 39 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 49 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 502 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

### WorldEconomy.java ([map](map/WorldEconomy.md))

| line | constant | value | says |
|---:|---|---|---|
| 72 | `WorldEconomy.DEFAULT_MEAN_INFLATION` | `.01` | What the world's inflation averages. |
| 75 | `WorldEconomy.INFLATION_SPREAD` | `.03` | How far either side of the mean a draw can land. |
| 78 | `WorldEconomy.MIN_MEAN_INFLATION` | `.0` | The most and least a city may be founded with. |
| 79 | `WorldEconomy.MAX_MEAN_INFLATION` | `.08` |  |
| 87 | `WorldEconomy.FOUNDING_CHOICES` | `{ 0, DEFAULT_MEAN_INFLATION,.02,.0333,.05 }` | The worlds the founding screen offers, as its five chips: none, the default, twice it, the mean every city grew up in before 2026-09-13 (LEGACY_MEAN_INFLATION), and five. |
| 138 | `WorldEconomy.PERSISTENCE` | `.90` | How much of last month's rate survives into this one. |
| 193 | `WorldEconomy.LOW_BIAS` | `2.0` | How far the uniform draw is bent toward the bottom of the band. |
| 195 | `WorldEconomy.SEED` | `0x5F3A91C7L` |  |
| 239 | `WorldEconomy.TREND_INFLATION` | `.0` | The world's long-run trend, against which the wandering rate is a cycle. |
| 242 | `WorldEconomy.TREND_PULL` | `.006` | How hard the level is pulled back to trend. |
| 346 | `WorldEconomy.LEVEL_RING` | `13` |  |
| 411 | `WorldEconomy.LEGACY_MEAN_INFLATION` | `.0333` | The mean every city founded before 2026-09-13 grew up in. |

### YearBook.java ([map](map/YearBook.md))

| line | constant | value | says |
|---:|---|---|---|
| 59 | `YearBook.MONTHS_A_YEAR` | `12` |  |
| 60 | `YearBook.MONTHS_A_DECADE` | `120` |  |
| 91 | `YearBook.MONEY` | `"{the city's money}"` | Where a note names the city's money. |
| 242 | `YearBook.RULES` | `rules()` |  |
| 243 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 480 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1029 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1032 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1035 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |
| 1045 | `YearBook.FINANCIAL_EQUITY` | `0` | The bank's equity under this, in thousands, is a financial crisis: the bank has failed. |
| 1048 | `YearBook.RECESSION_GROWTH` | `0` | Real output over a rolling year against the year before, as a fraction, under this is a recession: under zero, a fall. |
| 1051 | `YearBook.CURRENCY_MOVE` | `2` | The exchange rate past this multiple of itself a year before is a currency crisis: the currency halved. |
| 1054 | `YearBook.INFLATION_EPISODE` | `.25` | Prices rising faster than this a year is an inflation. |
| 1057 | `YearBook.DEFLATION_EPISODE` | `-.10` | Prices falling faster than this a year (a negative rate) is a deflation. |
| 1060 | `YearBook.EPIDEMIC_SICK` | `.10` | More of the workforce off sick than this is an epidemic. |
| 1063 | `YearBook.TREASURY_CASH` | `0` | The treasury's cash under this, in thousands, is a treasury crisis: it is overdrawn. |
| 1066 | `YearBook.SLUMP_UNEMPLOYMENT` | `.20` | More of the labour force out of work than this is a slump. |
| 1240 | `YearBook.CHRONIC_MONTHS` | `120` | An episode still running after this many months is chronic: City History lists it after the others that are running, and leads with it only when it runs alone (0.7.37). |

## sectors (40 constants)

### Agriculture.java ([map](map/Agriculture.md))

| line | constant | value | says |
|---:|---|---|---|
| 127 | `Agriculture.BAKED_KG_PER_TONNE` | `525` | Kilograms of bread and bakery goods a tonne of crops becomes. |
| 130 | `Agriculture.BAKED_KG_A_HEAD` | `5.0` | What one person eats of the city's own baking a month: 3.5kg of bread, 1.5kg of the rest. |
| 300 | `Agriculture.FIRST_FARM_UTILISATION` | `.5` | Half a farm's nameplate, a month, before the first one is sunk. |

### Automotive.java ([map](map/Automotive.md))

| line | constant | value | says |
|---:|---|---|---|
| 109 | `Automotive.MAX_SHARE_OF_LOCAL_SUPPLY` | `.25` | The most of the city's WHOLE fabrication output one new plant may want. |

### Construction.java ([map](map/Construction.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The core crew: the smallest share of its posts construction keeps on when it has no work, and lays the rest off. |

### FoodProcessing.java ([map](map/FoodProcessing.md))

| line | constant | value | says |
|---:|---|---|---|
| 99 | `FoodProcessing.FILLED` | `.85` | How full a new plant has to be before anybody finances one. |

### LuxuryRetail.java ([map](map/LuxuryRetail.md))

| line | constant | value | says |
|---:|---|---|---|
| 92 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop charges over what the piece cost it when nobody would buy at its price: the mark-up at a position of nothing, and the lowest the margin can strike (0.7.19: the position is the buyers AT... |
| 106 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop charges as the buyers at its price outnumber its counters without limit: the position's limit of one. |
| 235 | `LuxuryRetail.MARGIN_STEPS` | `60` | Bisection steps for the margin's fixed point: 2.75 / 2^60 is far below a cent's grain on any landed cost. |

### Materials.java ([map](map/Materials.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `Materials.FIRST_PLANT_UTILISATION` | `.5` | Half a plant's nameplate, a month, before the first one is sunk. |

### Rail.java ([map](map/Rail.md))

| line | constant | value | says |
|---:|---|---|---|
| 132 | `Rail.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 135 | `Rail.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 156 | `Rail.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 175 | `Rail.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 178 | `Rail.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 209 | `Rail.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 212 | `Rail.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 215 | `Rail.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 237 | `Rail.WORLD_FUEL_PER_TONNE` | `.03` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |

### RealEstate.java ([map](map/RealEstate.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `RealEstate.TARGET_RENT_BURDEN` | `.30` | What share of a working household's income rent should take. |
| 99 | `RealEstate.REFERENCE_EARNERS` | `2` | The household the yardstick is struck against: a couple both working... |
| 102 | `RealEstate.REFERENCE_HOME_CAPACITY` | `4` | ...in a home for four. |
| 110 | `RealEstate.LANDLORD_YIELD` | `.058` | What a landlord wants back each year for what the building cost. |
| 118 | `RealEstate.MAINTENANCE_PER_YEAR` | `.01` | Repairs: one percent a year of what a building cost to put up, placed as a real order with the builders in the building's own inputs. |
| 121 | `RealEstate.SCARCITY_ELASTICITY` | `1.0` | How hard rent answers a shortage of front doors. |
| 124 | `RealEstate.LEASE_MONTHS` | `12` | The lease, in months, which is also the lag. |
| 127 | `RealEstate.BUILD_MARGIN` | `.33` | The margin a new building has to clear over its own costs to be worth putting up. |
| 130 | `RealEstate.MIN_HOMES_FOR_A_MARKET` | `10` | Below this many front doors the ratio stops meaning anything. |

### Restaurants.java ([map](map/Restaurants.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `Restaurants.MEALS_A_PERSON_MONTH` | `90` | Meals one person eats in a month: three a day, thirty days. |
| 87 | `Restaurants.PERSON_MONTHS_PER_MEAL` | `1 / MEALS_A_PERSON_MONTH` | ...and the same fact the other way up, which is what the arithmetic wants. |
| 118 | `Restaurants.MARGIN_FLOOR` | `3.0` | What a kitchen with empty tables charges over what the food cost it. |
| 121 | `Restaurants.MARGIN_CEILING` | `8.0` | ...and what a kitchen with a queue at the door charges. |
| 156 | `Restaurants.KITCHEN_COVER_MONTHS` | `1` | Months of food a kitchen keeps. |

### Retail.java ([map](map/Retail.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Retail.STORE_COVER_MONTHS` | `2.5` | Months of recent sales a store tries to keep on the shelf. |
| 52 | `Retail.RETAIL_MARKUP` | `1.50` | What the shops add to what their stock cost them. |
| 55 | `Retail.REPRICE_SPEED` | `.25` | How fast the shelf catches up with the invoice. |
| 58 | `Retail.OPENING_SELL_PRICE` | `.3` | The price the game opened at, and the floor it will not go below. |
| 66 | `Retail.MAX_SCARCITY_MULTIPLE` | `1.6` | How far above cost-plus a total shortage can push the shelf price. |
| 69 | `Retail.COMFORTABLE_DELIVERY` | `.95` | Delivery share at which scarcity stops adding anything. |
| 109 | `Retail.SHELF` | `{ Good.GRAINS, Good.BREAD, Good.DAIRY_EGGS, Good.VEGETABLES, Good.FRUIT, Good.MEAT, Goo...` |  |

## interface (622 constants)

### BankScreen.java ([map](map/BankScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 116 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the Overview; BANK_PAGES, the pages behind it |
| 118 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the Overview that is lit until the player picks another; the rail's bank icon resets to it. |
| 122 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "History" }` | The six pages behind the Overview, in the chip strip's order: the balance sheet beside the income statement since 0.7.13. |
| 126 | `BankScreen.OVERVIEW` | `"Overview"` | The strip (0.7.33): the Overview first - it was the landing, with a button back to it at the foot of every page - then the six. |
| 128 | `BankScreen.CHIPS` | `{ OVERVIEW, "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "Histo...` | ...the strip's seven chips: the Overview, then BANK_PAGE_NAMES in their order. |
| 131 | `BankScreen.CHIP_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.SECTOR, Icons.BANK, Icons.STAFF, Ic...` | ...and each chip's icon. |
| 156 | `BankScreen.FRAME_CHROME` | `190` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and, on a page, the status strip - until the frame is laid out and its own height is read (0.7.33). |
| 159 | `BankScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentSc... |
| 162 | `BankScreen.SET_ASIDE` | `"#setAside", PRICING = "#pricing", LANDLORDS = "#landlords", FAMILIES = "#families", CA...` | The scroll targets a door can land on: Lending's cards and its pricing, Funding's carry card, the details folds. |
| 310 | `BankScreen.LEAD_INFO` | `"Every loan in the city is its money, priced from what it costs the bank to make." + "T...` | The tab's (i): the Overview's lead (the spec's L2), with what the Overview shows. |
| 317 | `BankScreen.PAGE_INFO` | `{ "Its income statement, and what it did with the profit: a walk from the interest it e...` | Each page's (i), in BANK_PAGE_NAMES' order: what it holds (the landing's row blurbs until 0.7.33). |
| 928 | `BankScreen.LADDER_INFO` | `"From the price of money to what each borrower pays.Every step up is a cost " + "the ba...` | THE LADDER's (i): the landing's note (the spec's L10), with what the parts are. |
| 1315 | `BankScreen.NOTHING` | `.0005` | A step under half a dollar is nothing: it is left off the walk and named under it. |
| 1423 | `BankScreen.PROVISIONS_INFO` | `"A provision is money set aside for loans expected to go bad: a year's " + "expected lo...` | The provisions' note (the spec's T, Profit 790), behind the step's tooltip and the statement's line. |
| 1460 | `BankScreen.RATIOS_INFO` | `"The margin is what it charges less what it pays for its money, on everything lent." + ...` | The ratios' note (Profit 874), behind MARGIN's and COSTS' (i). |
| 1926 | `BankScreen.OWNS` | `List.of(new SheetPart(Bank.Sheet.BUSINESS_LOANS, "Loans to the businesses", Palette.BUS...` | What it owns, in the bars' order (the spec's section 3): the businesses' loans first, its reserves last; the allowance is the net tick. |
| 1938 | `BankScreen.OWES` | `List.of(new SheetPart(Bank.Sheet.DEPOSIT_FUNDING, "Lent past its own cash, on its depos...` | ...and what it owes, then its owners' (equity last). |
| 2191 | `BankScreen.BESIDE_INFO` | `"Counted for what it can lend and not held on its sheet in full: the families' " + "sav...` | BESIDE THE SHEET's (i) (Balance sheet 1161). |
| 2485 | `BankScreen.BOOK_INFO` | `"Everything it has lent, by who owes it: the businesses' loans and the bonds of theirs ...` | Who-owes-it note (Lending 1225), behind THE BOOK's (i). |
| 3125 | `BankScreen.LEND_AGAINST_INFO` | `"Savings reach the bank wherever its branches are - online - so it lends " + "against e...` | What it can lend against (Funding 1681), behind the banked bar's (i). |
| 3579 | `BankScreen.STAKE_INFO` | `"A resolution makes every share the city's, in its fund's rescue book, which its rule "...` | The city's stake's note (Capital 2002). |
| 3674 | `BankScreen.MOVED_INFO` | `"Equity moves by what it earned, what it was given and what it paid out, and by " + "no...` | The equity's note (Capital 1990): Jerus's rule on a plug. |
| 3728 | `BankScreen.RESCUES_INFO` | `"When a bank loses more than it owns, the city resolves it: the old owners lose " + "ev...` | The rescues' note (Capital 2027). |
| 3949 | `BankScreen.RESCUE_INFO` | `"Its owners lose everything: the households' shares and the world's pass to the city " ...` | The rescue's note (Rescue 2119). |

### BuildScreen.java ([map](map/BuildScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 187 | `BuildScreen.BUILD_HOME` | `BuildAdvice.OVERVIEW` | Where Build opens (BUILD_HOME), and which category the player was last looking at (buildCategory). |
| 322 | `BuildScreen.CITY_DOT` | `Palette.MONEY` | The colour of "only the city builds these": the money blue - the city's own account. |
| 325 | `BuildScreen.INVESTOR_DOT` | `Palette.BUSINESS` | The colour of "investors build these too": the business violet. |
| 1439 | `BuildScreen.JOB_RING` | `58` | A ring's size on the Overview's tiles (its stroke is 6 px). |
| 1980 | `BuildScreen.NEED_CARD` | `300` | A card's width, on every Build page since 0.7.25 (a city category's only, in 0.7.24). |
| 3120 | `BuildScreen.RECEIPTS` | `5` | How many purchases the receipt keeps. |

### ConstructionScreen.java ([map](map/ConstructionScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 50 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 202 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 216 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 547 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 552 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |

### FinancesScreen.java ([map](map/FinancesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 102 | `FinancesScreen.FINANCE_HOME` | `"Overview"` | The page an area opens on when none is named: the first of any area's pages that is called this. |
| 106 | `FinancesScreen.AREAS` | `{ "The position", "The book", "Borrow", "Money", "The bond market", "The city's fund" }` | The six areas, in the hub's order. |
| 108 | `FinancesScreen.AREA_ICONS` | `{ Icons.FINANCES, Icons.PAPER, Icons.COIN, Icons.BANKNOTE, Icons.REPORTS, Icons.SAFE }` | ...and each one's icon, on its card. |
| 111 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "Debt service", "Home & abroad", "Your rate" }` | The position's four pages (0.7.32: "The ladder" became the hub's hero). |
| 113 | `FinancesScreen.POSITION_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.TRADE, Icons.POLICY }` | ...and their icons on the chips. |
| 115 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece" }` | The book is one page since 0.7.32: Buy back is on each piece's card. |
| 117 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` | Borrow's two pages: the city's own paper at home, and dollars from the world. |
| 119 | `FinancesScreen.BORROW_ICONS` | `{ Icons.HOMES, Icons.TRADE }` | ...and their icons on the chips. |
| 121 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 123 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 125 | `FinancesScreen.BOND_ICONS` | `{ Icons.REPORTS, Icons.PAPER }` | ...and their icons on the chips. |
| 127 | `FinancesScreen.FUND_PAGES` | `FundScreen.PAGES` | The city's fund as a brokerage (0.7.39; "Holdings" and "By hand" from 0.7.14): FundScreen's four pages. |
| 129 | `FinancesScreen.FUND_ICONS` | `FundScreen.ICONS` | ...and their icons on the chips. |
| 178 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupon at all.T...` | The three kinds of paper, short to long, in the ladder's order: the note, the serial bond and the term loan. |
| 205 | `FinancesScreen.KIND_NAMES` | `{ "Notes", "Serial bonds", "Term loans" }` | The ladder's three kinds, as the key and a tooltip say them. |
| 230 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 233 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height... |
| 236 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentSc... |
| 371 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "The ladder i...` | The tab's (i): what the hub is, in a breath. |
| 377 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balance and the...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 641 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " + "everythi...` | What resolving a failed bank does (the spec's T41). |
| 882 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, " + "becaus...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 892 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " + "changes."` | The ladder with nothing on it (the spec's T11). |
| 896 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "refinancing i...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 901 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 904 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1141 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month notes" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1144 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't cover the...` | Each setting's one line (the spec's T35). |
| 1152 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and what the c...` | ...and each in full, on its chip (the old block's sentences). |
| 1163 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's " + "cash,...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1281 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1284 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays frozen until yo...` | Each setting's one line (the spec's T40). |
| 1290 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose " + "every...` | The rescue's (i): both settings in full (the old block's T40). |
| 1506 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to " + "build...` | THE BALANCE's (i): the spec's T7. |
| 1673 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 1676 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is " + "repa...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 1722 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the coupon is an expense; the principal is a balance-sheet movement." + "Both lea...` | LAST MONTH's (i): the spec's T14. |
| 1754 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city " + "earns...` | FOREIGN's (i): the spec's T16. |
| 1785 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none of " + "it ...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 1833 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the " + "exchan...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 1855 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports " + "they w...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 1973 | `FinancesScreen.CURVE_MONTHS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The curve's maturities, in months. |
| 1975 | `FinancesScreen.CURVE_NAMES` | `{ "3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "40y", "50y" }` | ...and how they are written under it. |
| 1978 | `FinancesScreen.CURVE_INFO` | `"The note is the floor - the dial, or what the bank's money costs it, whichever " + "is...` | THE CURVE's (i): the spec's T24, and the world's curve. |
| 2013 | `FinancesScreen.CurveChart.W` | `720, H = 190, LEFT = 44, RIGHT = 16, TOP = 26, FOOT = 22` | Its size and its margins, in pixels: a card's picture, at a fixed size. |
| 2145 | `FinancesScreen.NOTE_INFO` | `"No coupon at all - the lender's return was the discount, taken out of the proceeds " +...` | A note's (i): the spec's T28. |
| 2149 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, " + "the c...` | A price against face, in words (the spec's T33, D16). |
| 2309 | `FinancesScreen.TERMS_INFO` | `"Each column is the rate this paper would cost at that term today - for the " + "amount...` | The terms' (i): the spec's T55. |
| 2316 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " + "smalles...` | The ask's (i): the spec's T56. |
| 2320 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here, with the ladder it would build.Nothing "...` | The quote's empty state: the spec's T57. |
| 2324 | `FinancesScreen.PROCEEDS_INFO` | `"Paper is sold in lots and the face is grossed up for the discount, so the " + "proceed...` | Why the proceeds are not the ask: the spec's T58. |
| 2329 | `FinancesScreen.BUYERS_INFO` | `"The households first, when it pays them more than the bank does: up to %s of " + "an i...` | Who buys it: the spec's T61, its first half. |
| 2336 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "devaluation...` | Where the dollars go: the spec's T62. |
| 2598 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate and " + "eno...` | The default page's (i). |
| 2739 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's - plus " ...` | M2's (i): the spec's T69. |
| 2829 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no more " + "...` | No bond outstanding: the spec's T70. |
| 2834 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has not " + "t...` | The prices' (i): the spec's T71. |
| 2839 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when it " + "me...` | The order books' (i): the spec's T74. |
| 2847 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 2849 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY_DARK }` | ...and their colours on it. |
| 3006 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since " + "whate...` | A bond's book's (i): the spec's T75, T77 and T78. |

### FoundingScreen.java ([map](map/FoundingScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 69 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 112 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 115 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |

### FundScreen.java ([map](map/FundScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `FundScreen.AREA` | `"The city's fund"` | The area's name on Finances. |
| 92 | `FundScreen.PAGES` | `{ "Portfolio", "Search", "Activity", "Rules & cash" }` | The fund's four pages (0.7.39; "Holdings" and "By hand" until then). |
| 94 | `FundScreen.ICONS` | `{ Icons.SAFE, Icons.EXCHANGE, Icons.REPORTS, Icons.SETTINGS }` | ...and their icons on the chips. |
| 104 | `FundScreen.ACTIVITY_MONTHS` | `60` | The months of the record Activity shows before "show older", and how many more each press adds. |
| 107 | `FundScreen.SECURITY_ROWS` | `12` | The rows of a security's own record under its book. |
| 110 | `FundScreen.CLOSED_ROWS` | `8` | The closed lots the Portfolio's CLOSED card lists before its fold. |
| 113 | `FundScreen.MONEY_STEPS` | `{ 1_000, 10_000, 100_000, 1_000_000 }` | The money steps: the ticket's on a buy by amount and on a bond's face, and PAY IN, DRAW OUT's; a quantity of shares steps by 1, 10, 100 and 1,000 instead. |
| 116 | `FundScreen.LEFT` | `812, RIGHT = 412` | The security page's two columns at the 1,389 window: the picture and its facts, then the position and the ticket. |
| 156 | `FundScreen.FAIR` | `"fair", BID = "bid", ASK = "ask", LAST = "last", OWN = "own"` | The ticket's prices. |
| 412 | `FundScreen.SHARES` | `"COMPANY SHARES", RESCUE = "THE RESCUE BOOK", BONDS = "BONDS"` | The holdings' groups, in the page's order. |
| 962 | `FundScreen.HERO_INFO` | `"Everything the fund holds at the marks every other holder uses, and its cash.The " + "...` | THE CITY'S FUND's (i). |
| 1075 | `FundScreen.COL_NAME` | `250, COL_UNITS = 120, COL_PRICE = 110, COL_VALUE = 110, COL_AVG = 110, COL_PNL = 160` | HOLDINGS' columns: the name, units, price, worth, average cost, unrealized, and the share of the fund's bar. |
| 1078 | `FundScreen.HOLDINGS_INFO` | `"Every lot the fund holds, at its mark, with what it cost by the average-cost " + "meth...` | HOLDINGS' (i). |
| 1271 | `FundScreen.POP_MILLIS` | `220` | How long a payment's pop takes each way. |
| 1298 | `FundScreen.KINDS_INFO` | `"Each kind of holding since the fund began: what the city put into it(bought on " + "th...` | SINCE IT BEGAN, BY KIND's (i). |
| 1393 | `FundScreen.FILTERS` | `{ "All", "Shares", "Bonds", "Held" }` | Search's chips. |
| 1396 | `FundScreen.SEARCH_INFO` | `"Every company listed on the exchange, every business's bond still outstanding, and " +...` | Search's (i): the spec's D1. |
| 1804 | `FundScreen.TICKET_INFO` | `"An order goes on the book at the next month's step, after every other participant " + ...` | THE ORDER TICKET's (i). |
| 2049 | `FundScreen.GROUPS` | `{ "All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS }` | Activity's chips: every row, or one group of them (FundView.groupOf()). |
| 2215 | `FundScreen.RESCUE_BOOK_INFO` | `"What the city holds from rescuing its bank: the shares it took when it " + "resolved i...` | The rescue book's (i). |

### GovernmentScreen.java ([map](map/GovernmentScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 93 | `GovernmentScreen.GOV_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.REPORTS }` | Each page's icon on its chip (0.7.31). |
| 116 | `GovernmentScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with SURPLUS's change, the pages, and their gaps - until the frame is laid out and its own height ... |
| 119 | `GovernmentScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - SectorScreen... |
| 122 | `GovernmentScreen.BRIDGE` | `"#bridge", BALANCE = "#balance"` | The scroll targets on the Overview: the bridge card and the balance. |
| 218 | `GovernmentScreen.HEAD_INFO` | `"What the city took in and paid out last month, and what its money did." + "EARNED is t...` | The tab's (i): what the five figures are, in a breath. |
| 676 | `GovernmentScreen.SURPLUS_INFO` | `"It took in more than it spent.A surplus pays down debt or buys the next " + "thing wit...` | P1's rest: what a surplus is for. |
| 680 | `GovernmentScreen.DEFICIT_INFO` | `"It spent more than it took in.That gap is borrowed, and next month's " + "interest is ...` | ...and what a deficit costs. |
| 783 | `GovernmentScreen.BRIDGE_INFO` | `"EARNED is the header's figure: taxes and fees less the running programmes " + "- inter...` | The bridge card's (i): P2, rewritten, and P3. |
| 796 | `GovernmentScreen.DIALS_INFO` | `"EARNED is read at today's tax rates; the budget was struck at the month's." + "A dial ...` | The dials' step's tooltip (the spec's B7). |
| 801 | `GovernmentScreen.RESIDUAL_INFO` | `"What the named steps do not explain: timing between the books and the money, " + "and ...` | The "not accounted for" step's tooltip. |
| 982 | `GovernmentScreen.ARREARS_INFO` | `"What the ceiling cut: owed, with no interest on it, and paid down out of the " + "firs...` | P6: the arrears. |
| 1023 | `GovernmentScreen.ECONOMY_INFO` | `"A month of revenue against a year of output is the only honest way to " + "compare a b...` | P8: why a month against a year. |
| 1028 | `GovernmentScreen.NO_YEAR` | `"There is no output recorded yet, so nothing here can be put in " + "proportion.A month...` | P7: no month of output yet. |
| 1168 | `GovernmentScreen.LIST_COLUMNS` | `{ 100, 60, 70 }` | The column heads' widths: a month, of the budget, of GDP. |
| 1171 | `GovernmentScreen.LIST_DOOR` | `96` | ...and the door's at the end of a row: "set it ›", "Land office ›". |
| 1429 | `GovernmentScreen.FUND_INFO` | `String.format("A twelfth of %.0f%% of everything the city's fund holds, " + "from its c...` | P20: the fund's transfer. |
| 1725 | `GovernmentScreen.PRINCIPAL_INFO` | `"Not on the list above, and deliberately: repaying principal is not " + "spending.The m...` | P27: principal is not spending. |
| 1730 | `GovernmentScreen.TERM_INFO` | `"A term loan pays its coupon every month and the whole face at the end, so the " + "mon...` | P28: term loans. |
| 1735 | `GovernmentScreen.SERIAL_INFO` | `"Serial bonds amortise - a slice of principal falls due every anniversary, so " + "both...` | P29: serial bonds. |
| 1739 | `GovernmentScreen.NOTES_INFO` | `"Notes carry no interest at all - the lender's return was the discount, taken " + "out ...` | P31: notes. |
| 1744 | `GovernmentScreen.COUPON_INFO` | `"The interest on the budget is struck from what the city actually paid over a " + "comp...` | P30: the coupon and the budget disagreeing. |
| 1840 | `GovernmentScreen.BOTH_DEFICIT_INFO` | `"Both run at a deficit on purpose.What the deficit buys is on the " + "Services tab, in...` | P32, as it is true: both cost the city more than they charge. |
| 1844 | `GovernmentScreen.NOT_BOTH_INFO` | `"Care and the schools are meant to run at a deficit: what it buys is on the " + "Servic...` | ...and when one does not (B16: fees with no bill). |
| 1883 | `GovernmentScreen.PENSIONS_INFO` | `"It is pay-as-you-go: this month's workers pay this month's pensioners, so " + "the gap...` | P33: pay-as-you-go. |
| 1966 | `GovernmentScreen.LAYERS_INFO` | `"What GDP is made of over the years, as City History draws it in layers: " + "consumpti...` | The layers' caption's (i). |
| 2043 | `GovernmentScreen.STOCK_INFO` | `"Stock built up is output that has been made and not yet sold, so it counts " + "the mo...` | P34: stock built up. |
| 2053 | `GovernmentScreen.GOVERNMENT_INFO` | `"Services with no market price, valued at cost: the utilities' staff, " + "care, and po...` | P35, rewritten (B5): what G is, as the model counts it. |
| 2090 | `GovernmentScreen.MOM_INFO` | `"Month-on-month annualised is one month multiplied up - compounded, so a city " + "grow...` | P37, on the strip's month on month. |
| 2095 | `GovernmentScreen.YOY_INFO` | `"This month's output against the same month a year ago, in today's money - " + "nominal...` | P36's point, on the strip's year on year: nominal, where the header's tile is real. |

### HistoryScreen.java ([map](map/HistoryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 173 | `HistoryScreen.TO_CHART` | `"chart", TO_HARD_TIMES = "hard times"` | The two places a draw can be asked to scroll to: the row above the big chart, and the hard times' heading. |
| 199 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheSectors(withTheMarket(new Trace[] { new Trace("gd...` |  |
| 537 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and how fast it ...` |  |
| 798 | `HistoryScreen.FRAME_CHROME` | `160` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 801 | `HistoryScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out - BankScreen's and Trade's. |
| 872 | `HistoryScreen.LEAD_INFO` | `"Every month the city has lived, and what it did.The big chart draws the lines " + "you...` | The title's (i): the old lead, and how the big chart is read. |
| 878 | `HistoryScreen.YEAR_BOOK_INFO` | `"Writes the whole run out as plain text - every series the history " + "keeps, folded o...` | What "Write the year book" writes (the old SEND THIS RUN TO SOMEBODY paragraph). |
| 1280 | `HistoryScreen.SMALL_CHART` | `120` | How tall a pinned chart's plot is: 150 until 0.7.37, 120 since, so the big plot ends above the fold at 1,389 x 868 (D2). |
| 1519 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1522 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1525 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1592 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart's plot is on the page; the lanes and the overview are under it. |
| 1595 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 2145 | `HistoryScreen.MOVE_MILLIS` | `600` | How long a figure takes to slide to a month's new reading, in milliseconds - the range bar's dot; the figure counts on over SectorScreen's own time. |
| 2324 | `HistoryScreen.AXES_INFO` | `"Lines measured in the same thing are drawn against each other on a real axis; " + "two...` | PICK WHAT TO DRAW's (i): how the lines share axes. |
| 2524 | `HistoryScreen.HARD_TIMES_SHOWN` | `5` | At most this many hard times are listed in view; the rest are counted, and in the details. |
| 2527 | `HistoryScreen.DECISIONS_SHOWN` | `8` | At most this many decisions are listed in view. |
| 2530 | `HistoryScreen.KINDS` | `{ "recession", "depression", "slump", "epidemic", "financial", "currency", "inflation",...` | The kinds of episode, in the order the details list them. |
| 2533 | `HistoryScreen.KIND_NAMES` | `{ "Recessions", "Depressions", "Slumps", "Epidemics", "Financial crises", "Currency cri...` | ...and what the details call each, in that order. |
| 2843 | `HistoryScreen.AT_END` | `1e-6` | How near an end of its band a price must be to be AT it - a rounding hair of the band (the market strikes an end exactly). |
| 2846 | `HistoryScreen.BAND_ROOM` | `1.25` | The scale a good's row is drawn on: the band from 0 to 1, and room past its ceiling for the month's trade. |
| 2849 | `HistoryScreen.GOODS_INFO` | `"What a unit costs here this month, against what the world pays for one and " + "what i...` | The section's (i). |

### Icons.java ([map](map/Icons.md))

| line | constant | value | says |
|---:|---|---|---|
| 50 | `Icons.BUILD` | `"M12 10h.01 M12 14h.01 M12 6h.01 M16 10h.01 M16 14h.01 M16 6h.01" + "M8 10h.01 M8 14h.0...` | A building. |
| 67 | `Icons.LAND` | `"M2 4 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0" + "M14 5 l3-3 3 3 M14 10 l3-3 3 3 M17 14V2 M17 14...` | A tent and trees - ground, rather than a map of it. |
| 72 | `Icons.POPULATION` | `"M17 21a5 5 0 0 0-10 0" + "M22 10.5a3.5 3.5 0 0 0-5.507-2.868" + "M7.507 7.632A3.5 3.5 ...` | Three people, one in front. |
| 81 | `Icons.SERVICES` | `"M2 9.5a5.5 5.5 0 0 1 9.591-3.676.56.56 0 0 0.818 0A5.49 5.49 0 0 1 22 9.5" + "c0 2.29-...` | A heart with a pulse through it. |
| 87 | `Icons.SECTOR` | `"M12 16h.01 M16 16h.01 M8 16h.01" + "M3 19a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V8.5a.5.5 0 0 ...` | A factory. |
| 94 | `Icons.GOVERNMENT` | `"M10 18v-7 M14 18v-7 M18 18v-7 M6 18v-7 M3 22h18" + "M11.119 2.205a2 2 0 0 1 1.762 0l7....` | A parliament, columns and all. |
| 99 | `Icons.FINANCES` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H...` | A coin with a dollar in it. |
| 115 | `Icons.BANK` | `"M3 22h18 M4 18v-7 M9 18v-7 M15 18v-7 M20 18v-7" + "M2 18h20" + "M11.5 2.4a1 1 0 0 1 1 ...` | A bank: a pediment on columns, with a doorway. |
| 128 | `Icons.INFRASTRUCTURE` | `"M3 19 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0" + "M15 5 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0" + "M9 1...` | A route: two waypoints and the road that winds between them. |
| 134 | `Icons.TRADE` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 ...` | A globe. |
| 140 | `Icons.POLICY` | `"M12 3v18 M7 21h10" + "M3 7h1a17 17 0 0 0 8-2 17 17 0 0 0 8 2h1" + "M19 8 l3 8 a5 5 0 0...` | Scales. |
| 147 | `Icons.REPORTS` | `"M3 3v16a2 2 0 0 0 2 2h16" + "M19 9 l-5 5 -4-4 -3 3"` | A line on axes. |
| 152 | `Icons.MENU` | `"M4 7h16 M4 12h16 M4 17h16"` | Three lines: the game menu, at the foot of the rail since 0.7.21 (it was the gear). |
| 155 | `Icons.MAIL` | `"M4 6h16v12H4z M4 7l8 6 8-6"` | An envelope: the inbox, in the header since 0.7.21. |
| 158 | `Icons.INFO` | `"M3 12 a9 9 0 1 0 18 0 a9 9 0 1 0 -18 0" + "M12 11v5 M12 8h.01"` | A circled i: the (i) that opens a line's full text (0.7.21; Pieces.infoButton()). |
| 172 | `Icons.OVERVIEW` | `"M4 4h7v7H4z M13 4h7v7h-7z M4 13h7v7H4z M13 13h7v7h-7z"` | Four squares: the Build overview. |
| 175 | `Icons.UTILITIES` | `"M13 3L5 13h6l-1 8 8-10h-6l1-8z"` | A bolt: Utilities. |
| 178 | `Icons.ROADS` | `"M8 3L5 21 M16 3l3 18 M12 4v3 M12 10.5v3 M12 17v3"` | Two kerbs and a dashed line: Roads & transit. |
| 181 | `Icons.HEALTH` | `"M9 3h6v6h6v6h-6v6H9v-6H3V9h6z"` | A cross: Healthcare. |
| 184 | `Icons.EDUCATION` | `"M2 9l10-5 10 5-10 5z M6 11v5c0 1.5 2.7 3 6 3s6-1.5 6-3v-5 M22 9v6"` | A mortarboard: Education. |
| 187 | `Icons.SAFETY` | `"M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z M9 12l2 2 4-4"` | A shield with a tick: Safety. |
| 190 | `Icons.HOMES` | `"M4 20V10l8-6 8 6v10z M10 20v-5h4v5"` | A house: Homes. |
| 193 | `Icons.SHOPS` | `"M4 9l1.5-5h13L20 9" + "M4 9h16v1.5a2.7 2.7 0 0 1-5.3 0 2.7 2.7 0 0 1-5.4 0A2.7 2.7 0 0...` | An awning over a shopfront: Shops. |
| 198 | `Icons.INDUSTRY` | `"M3 20h18 M4 20V10l5 3V10l5 3V7h3l.6-3h1.2l.6 3v13"` | Saw-tooth roofs and a chimney: Industry. |
| 201 | `Icons.OFFICES` | `"M5 20V4h10v16 M15 9h4v11 M3 20h18" + "M8 7h1 M11 7h1 M8 10h1 M11 10h1 M8 13h1 M11 13h1...` | An office block: Offices. |
| 205 | `Icons.FARMS` | `"M12 21V9" + "M12 9c-2.4-.8-3.6-2.8-3.6-5.4 2.4.3 3.6 2.4 3.6 5.4z" + "M12 9c2.4-.8 3.6...` | An ear of wheat: Farms. |
| 214 | `Icons.RAIL` | `"M7 3h10a2 2 0 0 1 2 2v9a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V5a2 2 0 0 1 2-2z" + "M5 10h14 M9...` | A train's face: Rail. |
| 218 | `Icons.VEHICLES` | `"M5 16v-4l2-5h10l2 5v4z M3 12h18" + "M6 17 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0" + "M14 17 a2...` | A car: Vehicles. |
| 223 | `Icons.LUXURY` | `"M6 4h12l3 5-9 11L3 9z M3 9h18 M9 4l3 16 3-16"` | A cut stone: Luxury shops. |
| 226 | `Icons.FOOD` | `"M7 3v7a2 2 0 0 0 2 2v9 M5 3v5 M9 3v5 M17 21V3c-2 1-3.2 4-3.2 8H17"` | A fork and a knife: Restaurants. |
| 229 | `Icons.CRANE` | `"M6 21V5 M3 5h17 M6 5l4-3 M18 5v5 M17 10h2v2h-2z M3 21h7"` | A tower crane: what is on site - the overview's tiles and the construction tab. |
| 232 | `Icons.COIN` | `"M4 12 a8 8 0 1 0 16 0 a8 8 0 1 0 -16 0" + "M14.5 9.2c-.6-.8-1.5-1.2-2.5-1.2-1.4 0-2.5....` | A coin: the header's money block. |
| 237 | `Icons.ALERT` | `"M12 4l9 16H3z M12 10v4 M12 17h.01"` | A warning triangle: the header's "Needs you" chip. |
| 240 | `Icons.STAFF` | `"M8.5 7 a3.5 3.5 0 1 0 7 0 a3.5 3.5 0 1 0 -7 0" + "M5 21c0-4 3-6.5 7-6.5s7 2.5 7 6.5 M1...` | A person: a Build card's staff bar, the posts the city can't fill. |
| 244 | `Icons.PIN` | `"M12 17v5" + "M9 10.76a2 2 0 0 1-1.11 1.79l-1.78.9A2 2 0 0 0 5 15.24V16a1 1 0 0 0 1 1h1...` | A pin: the drawer that stays open (Lucide's pin). |
| 250 | `Icons.CLOSE` | `"M6 6l12 12 M18 6L6 18"` | A cross: close. |
| 257 | `Icons.ORE` | `"M4 20L16 8 M9.5 4.5Q19.5 4.5 19.5 14.5Q17 7 9.5 4.5z"` | A pick: ore - the land office's Ore card (0.7.26). |
| 267 | `Icons.BIRTH` | `"M9 7a3 3 0 1 0 6 0a3 3 0 1 0-6 0 M6 20c0-4 3-6 6-6s6 2 6 6 M19 3v4 M17 5h4"` | A head and shoulders with a plus: born. |
| 270 | `Icons.DEATH` | `"M7 21V9a5 5 0 0 1 10 0v12 M4 21h16 M12 10v5 M10 12h4"` | A headstone on the ground: died. |
| 273 | `Icons.ARRIVE` | `"M3 12h11 M10 8l4 4-4 4 M15 4h5v16h-5"` | An arrow into a door: moved in. |
| 276 | `Icons.LEAVE` | `"M21 12H10 M17 8l4 4-4 4 M9 4H4v16h5"` | An arrow out of a door: moved out. |
| 279 | `Icons.CHILD` | `"M10 6a2 2 0 1 0 4 0a2 2 0 1 0-4 0 M8 20v-6l4-3 4 3v6"` | A small figure: a child, the orphans' tile. |
| 282 | `Icons.TICK` | `"M5 12.5l4.5 4.5L19 7.5"` | A tick: nothing needed (the "Needs you" chip, a ring's middle). |
| 291 | `Icons.DROP` | `"M12 3c-3.5 5-6 8-6 11a6 6 0 0 0 12 0c0-3-2.5-6-6-11z"` | A drop: water. |
| 294 | `Icons.CANE` | `"M9 8a3 3 0 0 1 6 0v13"` | A walking cane, its crook at the top: senior care. |
| 297 | `Icons.CELL` | `"M5 4h14v16H5z M9 4v16 M12 4v16 M15 4v16"` | A door of bars: the prisons. |
| 300 | `Icons.BUS` | `"M8 6v6 M15 6v6 M2 12h19.6" + " M18 18h3s0.5 -1.7 0.8 -2.8c0.1 -0.4 0.2 -0.8 0.2 -1.2 0...` | A bus, from the side (0.7.29, Lucide's bus): the Infrastructure tab's Transit page. |
| 306 | `Icons.LORRY` | `"M14 18V6a2 2 0 0 0 -2 -2H4a2 2 0 0 0 -2 2v11a1 1 0 0 0 1 1h2 M15 18H9" + " M19 18h2a1 ...` | A lorry (0.7.29, Lucide's truck): the Infrastructure tab's Freight page, and the fleets on it. |
| 319 | `Icons.MILL` | `"M9.5 21l1-8h3l1 8z M11 21v-2.5h2V21" + " M12 10L6.5 4.5 M12 10l5.5-5.5 M12 10l-4.5 4.5...` | A windmill, its sails crossed over the tower: Industry, the food mills. |
| 323 | `Icons.CAN` | `"M6 6.5 a6 2.5 0 1 0 12 0 a6 2.5 0 1 0 -12 0" + " M6 6.5v11 a6 2.5 0 0 0 12 0v-11 M6 12...` | A tin, its lid an ellipse: Food Processing. |
| 327 | `Icons.INGOT` | `"M2 18h20l-3-6H5z M5 12l2.5-4h9l2.5 4"` | An ingot, its top face over its side: Heavy Industry, the steel. |
| 330 | `Icons.GEAR` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2.34 0 0 1 ...` | A gear: Manufacturing (the settings gear's outline, drawn since 0.7.30). |
| 340 | `Icons.PICK` | `ORE` | A pick: Mining, and an investors' word about ore (the land office's ORE). |
| 351 | `Icons.PAPER` | `"M15 2H6a2 2 0 0 0 -2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2 -2V7z" + " M14 2v4a2 2 0 0 0 2 ...` | A sheet of paper with its lines: the city's paper, The book. |
| 355 | `Icons.BANKNOTE` | `"M4 6h16a2 2 0 0 1 2 2v8a2 2 0 0 1 -2 2H4a2 2 0 0 1 -2 -2V8a2 2 0 0 1 2 -2z" + " M10 12...` | A banknote: the money itself, M0 and M2. |
| 359 | `Icons.SAFE` | `"M5 3h14a2 2 0 0 1 2 2v12a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2z" + " M7.5 ...` | A safe on two feet, its dial and its handle: the city's fund. |
| 363 | `Icons.EXCHANGE` | `"M8 3L4 7l4 4 M4 7h16 M16 21l4-4-4-4 M20 17H4"` | Two arrows passing, one each way (Lucide's arrow-left-right): money changed from one currency to the other - the Trade tab's exchange (0.7.35). |
| 475 | `Icons.SETTINGS` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2.34 0 0 1 ...` | A gear. |

### InfrastructureScreen.java ([map](map/InfrastructureScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 97 | `InfrastructureScreen.INFRA_PAGES` | `{ "Roads", "Transit", "The railway", "Freight" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 101 | `InfrastructureScreen.INFRA_ICONS` | `{ Icons.ROADS, Icons.BUS, Icons.RAIL, Icons.LORRY }` | Each page's icon on its chip (0.7.29): the road, the bus, the train and the lorry. |
| 112 | `InfrastructureScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). |
| 115 | `InfrastructureScreen.CURVE_MAX` | `2.6` | The flow curve's x scale: the use of the road from nothing to this, fixed so the dot's motion reads month to month; the floor starts at FREE_FLOW / MIN_THROUGHPUT, 257%. |
| 278 | `InfrastructureScreen.UNKNOWN_YET` | `"known after a month: the save predates 0.7.29"` | What a dash means, where one is shown. |
| 418 | `InfrastructureScreen.FLOW_INFO` | `String.format("Every business in the city multiplies its output by its " + "flow, and s...` | The flow's (i): the old page's paragraph under its big figure. |
| 507 | `InfrastructureScreen.WALK_INFO` | `"Every building makes trips - its staff to and from work, its goods and its " + "bulk i...` | The walk's (i). |
| 514 | `InfrastructureScreen.CARS_INFO` | `String.format("A fully motorised city asks %.1f times the commuter road a city where " ...` | The car row's (i): the old page's car paragraph (P4). |
| 522 | `InfrastructureScreen.NO_CARS_INFO` | `"Nobody in this city owns one yet, so a commuter costs the road exactly " + "one trip a...` | ...and with nobody driving (P3). |
| 527 | `InfrastructureScreen.COSTS_INFO` | `"\"Costs\" is what one trip of this kind asks of the street once it is on " + "it: a lo...` | The stream cards' "costs" (P2, rewritten: a commuter is not always one). |
| 644 | `InfrastructureScreen.FlowCurve.W` | `270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5` | The plot's size, and the room for the axes' figures at its left and under it. |
| 831 | `InfrastructureScreen.NO_TRANSIT_INFO` | `"This city has built no transit at all.A Bus Network is the cheap rung " + "and a Metro...` | The no-transit card's (i) (P6). |
| 837 | `InfrastructureScreen.CEILINGS_INFO` | `"A city that builds a metro and no streets gets a metro nobody can reach, " + "and no c...` | The ceilings' (i) (P7). |
| 951 | `InfrastructureScreen.FARE_LADDER` | `520` | The fare's ladder, and its effects under it: the card is one of two across the page. |
| 981 | `InfrastructureScreen.FARE_INFO` | `"A FARE IS A PRICE AND NOT A CHARGE, which is what makes this different " + "from a cli...` | The fare's (i) (P9). |
| 989 | `InfrastructureScreen.PREVIEW_INFO` | `"The riders line is this month's ceilings at the new fare, which is the " + "honest hal...` | The preview's (i) (P10). |
| 1084 | `InfrastructureScreen.QUOTE_INFO` | `"Of what a lorry would charge for the same tonne.The railway is a " + "private business...` | The quote's (i) (P12). |
| 1091 | `InfrastructureScreen.NO_TRACK_INFO` | `"Nobody has laid a line in this city, so every tonne that leaves " + "it leaves by lorr...` | No track (P11). |
| 1096 | `InfrastructureScreen.LINE_RULE_INFO` | `String.format("It will not lay a line it cannot fill to %.0f%%, which is why a town doe...` | The line rule (P17). |
| 1102 | `InfrastructureScreen.BILL_INFO` | `"The month's lorry bill is what the tonnes that crossed the city's boundary " + "would ...` | The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). |
| 1198 | `InfrastructureScreen.RELIEF_INFO` | `String.format("A tonne that leaves by train does not drive across the city to leave by ...` | The relief's (i) (P15). |
| 1232 | `InfrastructureScreen.BIGGER_INFO` | `"It bills under three quarters of what the rule allows.It is allowed to " + "charge for...` | The too-big railway (P16). |
| 1324 | `InfrastructureScreen.BAND_INFO` | `"Every traded good's price has a cost of MOVING it inside the gap between " + "what the...` | The bars' (i) (P18 and P20, for bars rather than a grid). |
| 1463 | `InfrastructureScreen.LORRY_INFO` | `String.format("A vehicle moves about %,.0f tonnes a month and lasts %.0f years.The " + ...` | The lorries' (i) (P23). |

### Ladder.java ([map](map/Ladder.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `Ladder.READING` | `118` | How wide the reading is held, so the slider does not shift as the figure's width does. |
| 68 | `Ladder.BUTTON` | `28` | How wide and tall each step button is: square, and room for the glyph. |
| 71 | `Ladder.GAP` | `10` | The gap between the buttons, the slider and the reading. |
| 235 | `Ladder.Marks.INSET` | `8` | Half the slider's thumb: the track's travel starts and ends this far in. |

### LandScreen.java ([map](map/LandScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 279 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + "plot they...` | The page's (i): who pays for the ground, and who does not. |
| 288 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 643 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expensive land ...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 664 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, and every mine draws on the city's tonnes" + " together....` | The Ore card's (i). |
| 679 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " now shows up...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 692 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square foot of ground, as each month recorded" + " it - in U...` | The chart's (i). |
| 1139 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + " -fx-backg...` | A worth card's style, its edge's colour last. |
| 1350 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the land.On the...` | The dollar offers' (i), the 0.7.13 note word for word. |

### Levers.java ([map](map/Levers.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `Levers.EFFECTS` | `420` | How wide the effects column is held beside the ladder. |

### Money.java ([map](map/Money.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Money.formatter` | `withoutNegativeZero(NumberFormat.getNumberInstance(Locale.CANADA))` | Thousands separators, Canadian style; every figure below goes through it. |

### Palette.java ([map](map/Palette.md))

| line | constant | value | says |
|---:|---|---|---|
| 62 | `Palette.STAGE` | `"#0b1118"` | The middle of the window - the darkest thing, and the biggest. |
| 65 | `Palette.PANEL` | `"#101924"` | The strips around it: the two side panels (a drawer and an overlay since 0.7.24), the header, the rail and the construction tab. |
| 68 | `Palette.RAISED` | `"#152130"` | A block lifted off a panel: a card, a header tile, an open section. |
| 71 | `Palette.PINNED` | `"#1a2839"` | The vitals block, and anything that should read as pinned rather than raised - and a popover. |
| 74 | `Palette.FIELD` | `STAGE` | Inside a control, and inside the inbox: darker than the panel it sits on - the window's own ground. |
| 77 | `Palette.CONTROL` | `"#1a2839"` | A button at rest: the mockups' raised. |
| 80 | `Palette.HOVER` | `"#22334a"` | A button under the pointer: a step above CONTROL in its hue (the mockups draw no hover). |
| 87 | `Palette.EDGE` | `"#24354a"` | Panel against stage. |
| 90 | `Palette.HAIRLINE` | `EDGE` | A quieter rule: inside a panel, under a heading, around a control. |
| 93 | `Palette.CONTROL_EDGE` | `"#33475f"` | The border of a control that can be pressed: a step up from the line, in its hue (the mockups draw none). |
| 112 | `Palette.TEXT` | `"#e6edf3"` | Text: titles, figures, the date. |
| 115 | `Palette.TEXT_2` | `"#a9b8c9"` | Secondary: the label on the left of a row, a card's second line. |
| 118 | `Palette.TEXT_3` | `"#8496ab"` | Captions: a note, a unit, a lead under a title (at least 4.5:1 on the panel). |
| 121 | `Palette.TEXT_MAX` | `TEXT` | The date. |
| 124 | `Palette.TEXT_HEAD` | `TEXT` | A screen title, a section heading, a figure that is the answer. |
| 127 | `Palette.TEXT_BODY` | `TEXT` | Ordinary text and ordinary figures. |
| 130 | `Palette.TEXT_MUTED` | `TEXT_3` | A caption, a unit, a subtitle, a note under a figure. |
| 133 | `Palette.TEXT_LABEL` | `TEXT_2` | The label on the left of a row. |
| 136 | `Palette.TEXT_FAINT` | `TEXT_3` | Disabled, or a heading that is not the one you are on. |
| 139 | `Palette.TEXT_SPENT` | `"#5f7189"` | Struck through, settled, over with - a resolved notice, an old entry. |
| 150 | `Palette.GOOD` | `"#3fb950"` | It is going the right way: on target, covered, in surplus. |
| 153 | `Palette.GOOD_MONEY` | `GOOD` | Money going the right way - the same green since 0.7.21, which has one. |
| 156 | `Palette.WARN` | `"#e3b341"` | It is not wrong yet, and it will be - near a limit, or good news with a cost. |
| 159 | `Palette.BAD` | `"#f85149"` | It is costing the city something now: past a limit, overdrawn, unhoused, failing. |
| 162 | `Palette.ON_FILL` | `STAGE` | What sits on a verdict's fill - a play button, a rating, a confirm in the mockups' style. |
| 165 | `Palette.BAD_SOFT` | `"#ff9e9e"` | The same, in small type where full strength reads as shouting. |
| 168 | `Palette.BAD_TEXT` | `"#ffd9d4"` | Text sitting on the alert ground below. |
| 171 | `Palette.ALERT_GROUND` | `"#331d1d"` | Behind something wrong. |
| 174 | `Palette.ALERT_GROUND_LOUD` | `"#3b1f1f"` | Behind something wrong and unread. |
| 177 | `Palette.ALERT_EDGE` | `"#c0392b"` | The edge of either. |
| 189 | `Palette.ACCENT` | `"#5aa9ff"` | The active tab, a live link, the heading of an open section - the money blue, which the mockups' links are. |
| 192 | `Palette.ACCENT_FILL` | `"#2f6fa8"` | Filled: the primary button that carries white text. |
| 195 | `Palette.ACCENT_LIGHT` | `"#8cc4ff"` | The accent under the pointer: a link hovered, a slider's thumb (the mockups' link hover). |
| 198 | `Palette.CONFIRM` | `"#2f7d52"` | Filled: the button that commits - pays, builds, sets the policy. |
| 201 | `Palette.CONFIRM_GROUND` | `"#13291d"` | Behind a confirmation that has already happened. |
| 216 | `Palette.PEOPLE` | `"#2ec4b6"` | People and services: People, Services, Infrastructure. |
| 219 | `Palette.MONEY` | `"#5aa9ff"` | Money and policy: Government, Finances, the bank, Policy, History. |
| 222 | `Palette.BUSINESS` | `"#a78bfa"` | Business and trade: Sectors, Trade & the world. |
| 225 | `Palette.BUILDING` | `"#f17cb0"` | Building and land: Build, the land office. |
| 234 | `Palette.CATEGORIES` | `{ MONEY, PEOPLE, BUILDING, BUSINESS, "#c9b68f" }` | Categories told apart (0.7.21): the Government's two rings, whose three biggest revenue slices were three steps of one blue and read as one. |
| 245 | `Palette.ORE` | `"#c9b68f"` | Ore (0.7.26): the land office's deposits - a plot's ORE tag, the sand stripe on the ground bar, the Ore card. |
| 270 | `Palette.MONEY_LIGHT` | `"#a9d1ff", MONEY_DARK = "#2f80d9"` | The money blue, a step lighter and a step darker: the money area's second and third lines. |
| 273 | `Palette.PEOPLE_LIGHT` | `"#93e2da", PEOPLE_DARK = "#1c968b"` | The people teal, lighter and darker. |
| 276 | `Palette.BUSINESS_LIGHT` | `"#d3c5fd", BUSINESS_DARK = "#8669e8"` | The business violet, lighter and darker. |
| 279 | `Palette.BUILDING_LIGHT` | `"#f9bcd7", BUILDING_DARK = "#cf5590"` | The building pink, lighter and darker. |
| 282 | `Palette.LINE_COLOURS` | `13` | How many lines one chart can draw before a colour repeats: the five CATEGORIES and two steps of each of the four areas. |
| 353 | `Palette.REVENUE_RAMP` | `{ "#afd5fe", "#8dbef1", "#6aa6e4", "#468fd6", "#1577c8" }` | Money coming in. |
| 358 | `Palette.SPENDING_RAMP` | `{ "#efca9f", "#deaf78", "#cd954f", "#bc7a19", "#aa6000" }` | Money going out. |
| 377 | `Palette.LADDER` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | The three instruments on the maturity ladder, short to long. |
| 388 | `Palette.GDP_LAYERS` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | GDP's three stacked layers on the Reports page (0.7.6) - consumption, investment, government, bottom to top - on the same three validated steps as the maturity ladder, and for the same reason: they... |
| 393 | `Palette.RAMP_REST` | `"#5c6b75"` | Everything too small to have its own step. |
| 396 | `Palette.RING` | `26` | How thick a donut's ring is drawn, and how wide the hole is. |
| 426 | `Palette.SIZE_TITLE` | `20` | A screen's title. |
| 429 | `Palette.SIZE_LEAD` | `17` | A figure that is the point of its panel. |
| 432 | `Palette.SIZE_SECTION` | `14` | A section heading inside a screen. |
| 435 | `Palette.SIZE_HEADING` | `12` | A panel's own heading. |
| 438 | `Palette.SIZE_BODY` | `11` | Body text, and the figure in a row. |
| 441 | `Palette.SIZE_LABEL` | `10` | The label in a row, and a button in a dense list. |
| 444 | `Palette.SIZE_CAPTION` | `9` | A caption under something, and a unit after something. |
| 452 | `Palette.GAP_TIGHT` | `4` |  |
| 453 | `Palette.GAP` | `8` |  |
| 454 | `Palette.GAP_LOOSE` | `12` |  |
| 455 | `Palette.GAP_SECTION` | `20` |  |
| 458 | `Palette.RADIUS` | `4` | Corner of a block, a chip, a control. |
| 461 | `Palette.RADIUS_TIGHT` | `3` | Corner of something small - a row, a badge. |
| 471 | `Palette.RAIL` | `76` | The navigation rail, at the window's left edge: an icon over its name (0.7.21). |
| 474 | `Palette.HEADER` | `84` | The header across the top: the clock, the money block, five headline tiles, the "Needs you" chip, the rating and the inbox (0.7.21; the money block and the chip 0.7.24). |
| 477 | `Palette.CITY_PANEL` | `290` | The city panel, not counting the rail: the drawer over the stage's left since 0.7.24. |
| 480 | `Palette.BUILD_PANEL` | `280` | The construction panel down the right: over the stage while open, a tab while folded, since 0.7.24. |
| 483 | `Palette.BUILD_ROW` | `400` | A building row, so the price column lines up down the list. |
| 486 | `Palette.INBOX` | `470` | The inbox, sized to the 62-character lines the notices are written at. |
| 592 | `Palette.Fonts.FOLDER` | `"/fonts/"` | Where the files sit on the classpath. |
| 595 | `Palette.Fonts.SYSTEM_FACE` | `"System"` | The platform's own face, for words when Plex Sans did not load. |
| 598 | `Palette.Fonts.FIGURE_FACE` | `"Courier New"` | The face figures used before 0.7.21, and use again when Plex Mono did not load. |

### PeopleScreen.java ([map](map/PeopleScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 104 | `PeopleScreen.OUT_OF_WORK_SHORT` | `.03` | Under this share out of work, nobody is spare: amber, "jobs going unfilled". |
| 107 | `PeopleScreen.OUT_OF_WORK_HIGH` | `.15` | Over this, high: amber. |
| 110 | `PeopleScreen.OUT_OF_WORK_FAR` | `.25` | Over this, far too many adults with nothing to do: red. |
| 120 | `PeopleScreen.GOING_SHORT_WARN` | `.01` | Over this share of the city eating less than a basket, GOING SHORT is amber; the dashboard's HUNGRY reads it too (0.7.27). |
| 123 | `PeopleScreen.GOING_SHORT_BAD` | `.10` | ...and over this, red. |
| 221 | `PeopleScreen.COUNT_MILLIS` | `600` | How long the headcount counts and the month's bars grow when a month lands (Jerus's "fun": the number moves). |
| 245 | `PeopleScreen.STILL_FAKED` | `"What this model does not do yet:\n" + "· Nobody has an age.Each band holds a mean, so ...` | What the model does not do yet: the four lines of the old page's eight that are still true (0.7.27). |
| 481 | `PeopleScreen.BORN` | `Palette.PEOPLE_LIGHT, MOVED_IN = Palette.PEOPLE` | The born and the arrived: the people teal's light step and the teal. |
| 484 | `PeopleScreen.DIED_OF_AGE` | `Palette.PEOPLE_DARK, DIED_OF_ILLNESS = Palette.BUSINESS, DIED_KILLED = Palette.BUILDING...` | The dead by cause: of age the dark teal, of illness the violet, the killed the pink, the aged out a light blue. |
| 488 | `PeopleScreen.LEFT_WORK` | `Palette.PEOPLE_DARK, LEFT_BROKE = Palette.MONEY, LEFT_CRIME = Palette.BUILDING` | The leavers by why: for want of work the dark teal, broke the blue, driven out by crime the pink. |
| 876 | `PeopleScreen.CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + " -fx-backg...` | The Build card ground every card on both pages stands on. |
| 1013 | `PeopleScreen.PYRAMID_HALF` | `200` | How wide half of the widest band is drawn, at most. |
| 1326 | `PeopleScreen.WHY_INFO` | `"The city draws people the way Migration strikes it each month: every post " + "support...` | WHY PEOPLE COME's (i): what the bridge is. |
| 1690 | `PeopleScreen.TILE_MIN` | `56` | The narrowest a tile is drawn; anything smaller folds into "+n more". |
| 1803 | `PeopleScreen.Strip.TILE_TALL` | `62` | How tall a strip is. |
| 1935 | `PeopleScreen.OUTSIDE_INFO` | `"Families are built from the adults who work, and their children." + "Everybody else is...` | OUTSIDE THE FAMILIES' (i): who they are, the old page's words. |
| 2165 | `PeopleScreen.LADDER_INFO` | `"open = posts this band can be put into(licensed posts are on the " + "chips under the ...` | The ladder's (i): the old legend. |
| 2630 | `PeopleScreen.IN_DOLLARS` | `"Every figure on this page is in dollars, not the thousands the rest of the " + "game c...` | The page's (i): what its money is counted in. |
| 2635 | `PeopleScreen.PAID_IN_ORDER` | `"The order is fixed: the payslip first, then rent, then the fees, and " + "the shop tak...` | The order a household pays in, and what happens when it comes up short: the verdict's (i). |
| 2828 | `PeopleScreen.BESIDE` | `1200` | The narrowest page that sets the open cell's books beside the grid; under it they stack. |
| 3104 | `PeopleScreen.CARS_INFO` | `"Bought out of savings past the same cushion a share is, so income " + "decides who can...` | The cars' (i): the old note. |
| 3174 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 3175 | `PeopleScreen.SHAPE_COL` | `168` |  |
| 3306 | `PeopleScreen.BELOW_THE_RULE` | `"Below the rule: the retired, on a pension and no wage; the out of work, " + "on EI for...` | The section captions' (i): who is below the rule, and what their one cell says. |

### Pieces.java ([map](map/Pieces.md))

| line | constant | value | says |
|---:|---|---|---|
| 118 | `Pieces.LIMIT_CELL` | `190` | How wide a limit cell is, its padding included; its note wraps inside it. |
| 768 | `Pieces.POPOVER_WIDTH` | `360` | How wide a popover's text wraps. |
| 771 | `Pieces.MORE_IN_THE_MANUAL` | `"More in the manual"` | The line a popover may end with: the third layer's door, with no link until the manual has one. |
| 813 | `Pieces.INFO_SIZE` | `16` | How big the (i) is drawn: its 24-unit grid at this many pixels. |
| 919 | `Pieces.TILE_GAP` | `10` | The gap between cards in a row or a grid: Build's cards in their flow, the land office's shelf. |
| 1058 | `Pieces.PAGE_WIDE` | `1480` | The widest a redrawn page is laid out - Build's Overview and categories since 0.7.24, the land office since 0.7.26 - so a 1,920 window does not stretch a row of cards across the glass. |
| 1690 | `Pieces.Waterfall.FIGURE_ROOM` | `16, NAME_ROOM = 12, ICON = 13` | The figures' line over the plot, the least the names under it take, and an icon's size. |
| 1908 | `Pieces.CAUSE_LABEL_ROOM` | `70` | How wide a part must be drawn to carry its name and figure under it; a narrower one is keyed. |
| 1934 | `Pieces.CauseBar.GAP` | `2, UNDER = 3, KEY_GAP = 12, KEY_ROW = 3` | Between the parts, under the bar, and between the key's entries and rows. |
| 2212 | `Pieces.EffectScale.TALL` | `44, Y = 22, DOT = 10` | Its height, how far down it the line runs, and the mark's size. |
| 2288 | `Pieces.CohortBars.AXIS` | `15` | Room under the bars for the near and far names. |
| 2549 | `Pieces.ScaleRows.ROW` | `26, GAP = 10, RULE_NAMES = 16, TAG_GAP = 6, ICON = 12` | A row's least height, the gap either side of the bar, the room the rules' names take over the rows, the gap before a tag, and an icon's size. |
| 3032 | `Pieces.EVERYTHING_ELSE` | `"Everything else"` | What topSlices() calls the slices past its ramp, folded into one. |
| 3179 | `Pieces.RANK_BAND` | `12, RANK_ROW = 36` | How tall a ranked bar's band is, and the least a row takes. |
| 3182 | `Pieces.RANK_KEY` | `"rankBars.key"` | The property a ranked bar's row carries its line's key under, so a screen can find the row to scroll to. |
| 3343 | `Pieces.BRIDGE_TILE` | `200, BRIDGE_BAR = 56, BRIDGE_FIGURE = 64` | A bridge's tiles' width, its step bars' and its figures' (0.7.31). |
| 3346 | `Pieces.BRIDGE_NOTHING` | `.5` | A step under this, in the model's thousands, is nothing: half a thousand, below which signedTight() writes "$0". |
| 3565 | `Pieces.Columns.TOP` | `34, FOOT = 30, GAP = 8` | The room over the plot for a tag and a figure, under it for a label and its second line, and the gap between columns. |
| 3801 | `Pieces.BAND_SCALE` | `1.6` | How far past the top of its band a ratio's bar runs, as a multiple of the top: the band sits in the left of it, so a ratio well over its band reads as full (BankScreen's capital band's since 0.7.9). |
| 3866 | `Pieces.RUNG_ROW` | `30, RUNG_BAND = 12` | A rung's least height, and its bar's band. |
| 4055 | `Pieces.ACTION_TALL` | `40` | An action button's height on a card, where it is the thing the card is for. |
| 4058 | `Pieces.ACTION_INLINE` | `32` | ...beside a heading, where it shares a row with words: Build's "Build all three", the land office's "Buy the next 5". |
| 4061 | `Pieces.DOOR_TALL` | `30` | A door pill's height. |
| 4510 | `Pieces.MirrorRows.ROW` | `28, BAR = 12, ICON = 18, GAP = 8` | A row's least height, its bar's thickness, an icon's size and the gaps between the columns. |
| 4735 | `Pieces.DivergingBars.ROW` | `30, BAR = 12` | A row's least height and its bar's thickness. |
| 5105 | `Pieces.RangeBar.TRACK` | `6, DOT = 10, TICK_W = 5, TICK_H = 14, PAD = 6` | The track's height, the dot's size, the tick's width and height, and the room either end so the dot is never cut. |

### PolicyScreen.java ([map](map/PolicyScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `PolicyScreen.TAXES` | `"Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises"` | The four areas, by what kind of lever each is - Jerus picked the grouping. |
| 101 | `PolicyScreen.AREAS` | `{ TAXES, WAGES, MONEY, PROMISES }` | ...as the head's chips list them. |
| 104 | `PolicyScreen.AREA_ICONS` | `{ Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION }` | ...and each one's icon: the coin, the staff, the bank, the people. |
| 109 | `PolicyScreen.POLICY_HOME` | `"Everything"` | The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. |
| 126 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 128 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 129 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 130 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Health", "Schools", "Subsidies" }` |  |
| 133 | `PolicyScreen.TAX_ICONS` | `{ Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES }` | Each page's icon on its tab, in the pages' order. |
| 135 | `PolicyScreen.WAGE_ICONS` | `{ Icons.STAFF }` | ...Wages' one, the staff. |
| 137 | `PolicyScreen.MONEY_ICONS` | `{ Icons.BANK, Icons.BANKNOTE }` | ...Money's: the bank for the policy rate, the banknote for the currency reform. |
| 139 | `PolicyScreen.PROMISE_ICONS` | `{ Icons.CANE, Icons.STAFF, Icons.HEALTH, Icons.EDUCATION, Icons.SECTOR }` | ...and the promises': the cane, the staff, health, education and the sector icon for the subsidies. |
| 151 | `PolicyScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. |
| 154 | `PolicyScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). |
| 157 | `PolicyScreen.TRAY_CHROME` | `90` | How tall the staged tray is taken to be until it is laid out. |
| 288 | `PolicyScreen.LEAD_INFO` | `"Every number the city sets for itself, and what each one is doing this month." + "Ever...` | The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). |
| 479 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 482 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 485 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 647 | `PolicyScreen.BUDGET_INFO` | `"Last month's surplus or deficit, and what it would be with this change: every " + "lin...` | THE BUDGET's (i). |
| 656 | `PolicyScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press.THE BUDGET is " + "struc...` | P20, the tray's (i). |
| 1015 | `PolicyScreen.NOTHING_BINDING_INFO` | `"Nothing is binding.Every lever has room to move, no sector is shut " + "out of credit,...` | P2: what the hub says when nothing is binding. |
| 1060 | `PolicyScreen.TAX_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST }` | The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. |
| 1069 | `PolicyScreen.TAX_NAMES` | `{ "Profit", "Sales", "Wage", "Property" }` | The four taxes by name, profit to property: their cards, the bar's parts and their pages. |
| 1351 | `PolicyScreen.CAVEAT` | `"Nothing here knows that the new rate changes what anybody does next month - a " + "bus...` | What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. |
| 1355 | `PolicyScreen.CAVEAT_LINE` | `"Struck on this month's books; behaviour is not projected."` | The caveat's line on a card. |
| 1366 | `PolicyScreen.TAKE_INFO` | `"Last month, by tax.Each has a rate of its own, and three of the four a " + "move off i...` | P3, THE TAX TAKE's (i). |
| 1462 | `PolicyScreen.EVERY_TAX_INFO` | `"Moves all three rates to one number.A sector or wage band you've set apart " + "keeps ...` | P5: every tax at once, its (i). |
| 1468 | `PolicyScreen.PROPERTY_NOT` | `"Property is not on this dial: it is charged on value rather than on income, " + "and t...` | P6. |
| 1517 | `PolicyScreen.PROFIT_INFO` | `"Charged on what each sector earned before tax.A sector that lost money " + "pays nothi...` | P8, the profit tax's head. |
| 1522 | `PolicyScreen.LOSS_INFO` | `"A sector that lost money paid no profit tax at all, which is why the " + "first line f...` | P4. |
| 1526 | `PolicyScreen.SALES_INFO` | `"Charged on VALUE ADDED - what a sector sells, less the tax it already " + "paid on wha...` | P10, the sales tax's head. |
| 1532 | `PolicyScreen.WAGE_INFO` | `"Taken off every payroll in the city before the household sees it.The " + "jobs are gro...` | P12, the wage tax's head. |
| 1537 | `PolicyScreen.PROPERTY_INFO` | `"Charged on what land and buildings are assessed at, whether or not the " + "owner earn...` | P14, the property tax's head. |
| 1542 | `PolicyScreen.PROPERTY_STEP_INFO` | `"A twentieth of a point a step rather than the quarter the other three " + "get, becaus...` | P15. |
| 1547 | `PolicyScreen.REFUND_INFO` | `"\"In refund\" means a sector's credits on what it bought exceed the tax " + "on what i...` | P11. |
| 1553 | `PolicyScreen.ROLL_INFO` | `"The power and water plants are the city's own and exempt.Raising what " + "land sells ...` | P16. |
| 1558 | `PolicyScreen.SALES_SCALED` | `"Scaled, not recomputed: each sector's net remittance moves by the ratio of its " + "ne...` | The sales tax's preview, scaled (the spec's D11, B6). |
| 1595 | `PolicyScreen.OFFSET_INFO` | `"A move is in POINTS off the tax's own rate, so a row left at zero is taxed " + "at exa...` | P9, the offsets' note. |
| 1664 | `PolicyScreen.BANK_INFO` | `"The commercial bank's profit is taxed at Retail's rate - a Commercial Bank is a " + "c...` | The bank's line (B5, D12). |
| 1906 | `PolicyScreen.PAYSLIP_INFO` | `"Only the first is a tax and only the first is set here - the other three " + "are prom...` | P13. |
| 1937 | `PolicyScreen.FARMLAND_INFO` | `"A field is worth what a developer would pay for it and grows what a " + "farmer can gr...` | P17. |
| 1944 | `PolicyScreen.FARMLAND_CAVEAT` | `"Exact against today's land price.What it actually decides is whether " + "the next fie...` | P19. |
| 2091 | `PolicyScreen.FLOOR_INFO` | `"Every wage in the city is a multiple of this number, so moving it moves " + "all of th...` | P22, the floor's (i). |
| 2097 | `PolicyScreen.PINNED_WORDS` | `"At least one skill level is oversupplied AND cannot get any cheaper, so " + "those wor...` | P23, both forms. |
| 2102 | `PolicyScreen.FREE_WORDS` | `"No skill level is pinned against the floor, so the labour market is " + "clearing on p...` | ...the other: no band pinned. |
| 2107 | `PolicyScreen.FLOOR_CAVEAT` | `"Once wages have walked there, which takes about a year.Nothing about " + "this preview...` | P24. |
| 2245 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2248 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2251 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2254 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2257 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2260 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2286 | `PolicyScreen.OVER_INFO` | `"Hot money follows this difference in, and leaves the day it closes." + "It is also wha...` | P28. |
| 2292 | `PolicyScreen.REAL_INFO` | `"The currency follows this one: a dial under inflation is a real rate " + "the world is...` | P28's second half: the real differential. |
| 2424 | `PolicyScreen.HAND_INFO` | `"Jerus's autopilot: the rule can hold the dial.Every month, before anything is " + "pri...` | P32. |
| 2428 | `PolicyScreen.REPRICE_INFO` | `"This does not reprice a single bond the city has already sold - every " + "coupon on t...` | P34: the dial card's caveat. |
| 2605 | `PolicyScreen.CEILING_INFO` | `String.format("When the treasury runs dry the central bank advances the gap in money it...` | P36. |
| 2641 | `PolicyScreen.SWING_INFO` | `"Prices here have more than doubled and come back at some point." + "Wages, rents and e...` | P30. |
| 2646 | `PolicyScreen.REFORMED_INFO` | `"The index is measured against the FOUNDING basket in founding money, " + "and stays co...` | P31, once the money has been reformed. |
| 2837 | `PolicyScreen.COVER_INFO` | `"The rest is general revenue - the same pot the schools and the hospitals " + "come out...` | P41 and P47. |
| 3002 | `PolicyScreen.EI_INFO` | `"EI only pays the first twelve months, so a long bust costs less in EI than " + "a shor...` | P57. |
| 3326 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 3329 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 3365 | `PolicyScreen.SHARE_INFO` | `"The city's share of every course fee.Households pay the rest out of a " + "month's wag...` | P48. |
| 3372 | `PolicyScreen.BURDEN_INFO` | `String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is a " + "co...` | P49. |
| 3378 | `PolicyScreen.PRICE_INFO` | `"The founding tuition table times this, before the city's share comes off." + "The tabl...` | P50. |
| 3386 | `PolicyScreen.SCHOOL_CAVEAT` | `"Against the courses being taken now, each kind's students at its own " + "price - and ...` | P51/P52: the schools' preview caveat. |
| 3731 | `PolicyScreen.LOAN_INFO` | `"Charged on a graduate's balance while they repay it, and on nothing while " + "they st...` | P55. |
| 3737 | `PolicyScreen.LOAN_CAVEAT` | `"Against the balances the graduates owe today.The instalment itself does " + "not chang...` | P56. |
| 3782 | `PolicyScreen.SUBSIDY_INFO` | `"A protected sector is topped up to break-even every month it loses money, " + "so it n...` | P69. |

### SectorScreen.java ([map](map/SectorScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 86 | `SectorScreen.SECTOR_HOME` | `"Operations"` | The page a business opens on, and falls back to for a page it does not know. |
| 93 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` | The five pages, in the strip's order: the names other screens open a business's books by. |
| 97 | `SectorScreen.PAGE_ICONS` | `{ Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR }` | Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. |
| 109 | `SectorScreen.FRAME_CHROME` | `276` | How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own... |
| 112 | `SectorScreen.LIST_CHROME` | `190` | ...and the list's: its head and its four figures (0.7.30). |
| 115 | `SectorScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page ... |
| 118 | `SectorScreen.COUNT_MILLIS` | `400` | How long a figure takes to count from last month's to this month's (0.7.30). |
| 121 | `SectorScreen.SWEEP_MILLIS` | `700` | ...and the plant's ring to sweep up from nothing (0.7.30). |
| 191 | `SectorScreen.LIST_INFO` | `"What the city's businesses kept this month, after tax - each card one business, " + "w...` | What the list's (i) holds: the old caption, and what a card does. |
| 198 | `SectorScreen.NOTHING_YET_LIST` | `"The sector books are written when a month closes.This city has not " + "closed one sin...` | The empty books' sentence (the old list's alert). |
| 202 | `SectorScreen.NOTHING_YET_SECTOR` | `"This city has not closed a month since it was loaded, so there is no " + "statement to...` | ...and a business's (the old page's alert). |
| 206 | `SectorScreen.NO_WORD_YET_LIST` | `"Nothing recorded since the city was loaded or founded: a month on, each " + "card says...` | The list's one line while no business has a word for the month (0.7.34), in place of each card's own. |
| 378 | `SectorScreen.RUNNING_INFO` | `"How much of what its plants could make each business made this month: its " + "posts f...` | RUNNING AT's (i). |
| 491 | `SectorScreen.SPARK_WIDTH` | `120` | The width the sparkline takes on a card (0.7.30; 90 on the old list). |
| 494 | `SectorScreen.SPARK_HEIGHT` | `28` | ...and its height (0.7.30; 22 on the old list). |
| 497 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |
| 1520 | `SectorScreen.OPERATING_INFO` | `"What the business made from trading, before it pays for the ground it " + "stands on o...` | Operating income's (i): the old page's sentence under it. |
| 1524 | `SectorScreen.REFUND_INFO` | `"The sales tax line is a REFUND this month: the credit on what this sector " + "bought ...` | The sales tax refund's (i). |
| 2488 | `SectorScreen.CONTROL_INFO` | `"None of this is yours to set.These are private companies deciding for " + "themselves ...` | WHAT YOU CONTROL's (i): the old page's last paragraph. |
| 2493 | `SectorScreen.WAITING_INFO` | `"This business wants to build and there is nowhere to put it.It is the one " + "refusal...` | The waiting-on-ground alert's (i). |
| 2832 | `SectorScreen.FLOW_INFO` | `"Each row's money is the month the books closed on - the Income page's - and its " + "u...` | The flow's money and units: which month each is (SectorFlow's two months). |
| 2898 | `SectorScreen.FOLD_PAST` | `6` | The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). |

### ServicesScreen.java ([map](map/ServicesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `ServicesScreen.OVERVIEW` | `"Overview", BOOKS = "Books"` | The page every system opens on (0.7.28), and the last chip of each, its books. |
| 107 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 110 | `ServicesScreen.SERVICE_HOME` | `OVERVIEW` | ...and every system opens on its Overview (0.7.28; General care until then). |
| 136 | `ServicesScreen.FRAME_CHROME` | `268` | How much of the stage the fixed frame takes above the page's scroller: the head, the systems, the four figures with their sparklines and changes, the pages, and their gaps. |
| 804 | `ServicesScreen.SICK_INFO` | `String.format("The bar is today's sick rate split into what makes it, in points of the ...` | The sick rate's (i): what the bar is, its floor and its cap, and why it is a cost of output and not of wages. |
| 1045 | `ServicesScreen.BURIAL_INFO` | `"A household that can save a plot's price over ten years chooses burial; " + "the ones ...` | The burial choice (the death care page's note). |
| 1151 | `ServicesScreen.LONG_SICK_INFO` | `String.format("Everybody sick this month, by how long they have been ill: the left bar ...` | The long sick's (i): who can die of it, at what chance by age (the elders' too, which the old note left out). |
| 1160 | `ServicesScreen.RECOVERY_INFO` | `String.format("The share of the sick who get better in a month: %.0f%% with no general ...` | What care cures, in words. |
| 1407 | `ServicesScreen.PLOTS_INFO` | `"Plots are consumed permanently — the land never comes back, and a cemetery " + "cannot...` | Plots are permanent (the ground's note). |
| 1411 | `ServicesScreen.CREMATORIA_INFO` | `"A rate rather than a stock, and it needs almost no land — which makes it " + "the answ...` | The crematoria's note. |
| 1735 | `ServicesScreen.DIPLOMA_INFO` | `"Teens age out at a steady rate and the ones who were in school leave with " + "a diplo...` | The diplomas' (i): the teens' note, and what the figure is (and is not). |
| 1743 | `ServicesScreen.LADDER_INFO` | `String.format("The ladder covers the minimum of its three stages, not the average — the...` | The basic ladder's notes: the minimum of three, and the four-and-three split. |
| 1751 | `ServicesScreen.PROFESSIONS_INFO` | `"A band row on the People screen can say the city has eight hundred " + "graduates and ...` | The professions page's sentence. |
| 1966 | `ServicesScreen.GATES_INFO` | `String.format("The funnel is why that many and not more.Who could enrol holds the level...` | The gates' (i): why that many and not more, as the old page said it under its five lines. |
| 1978 | `ServicesScreen.COULD_ENROL` | `"Who holds the level this course takes, in the workforce - and for a " + "professional ...` | Who could enrol, in words (the old page's note). |
| 1982 | `ServicesScreen.RETURN_INFO_PREFIX` | `"How much better off somebody is for doing it - 0 means not worth it.\n\n"` | The wage return's (i) opens on this, then says what the return is measured against (returnNote()). |
| 2311 | `ServicesScreen.POWER_INFO` | `"Power is counted in kilowatts, a rate - the screens wrote watts until " + "0.7.28, a t...` | The power row's (i): the unit, the staff discount, one workforce, who is billed. |
| 2321 | `ServicesScreen.BROWNOUT_INFO` | `"Every industrial and commercial building's output is cut in proportion " + "— a browno...` | A brownout's (i), as the grid's old alert said it. |
| 2326 | `ServicesScreen.WATER_INFO` | `"Water is counted in units of 10,000 gallons a month.The people draw " + "their own(res...` | The water row's (i). |
| 2512 | `ServicesScreen.CAUSES_INFO` | `"Every adult at liberty is counted once, at the heaviest reason they have.The" + " last...` | The causes' (i). |
| 2518 | `ServicesScreen.STOLEN_INFO` | `"What is stolen goes to the offenders' households.The killings are next" + " month's de...` | What is stolen, and the injured (the old "what it did" note). |
| 2522 | `ServicesScreen.CAUGHT_INFO` | `"Anybody caught with no staffed cell free stays on the street and keeps" + " offending....` | Anybody caught with no cell (the prisons' note). |
| 2527 | `ServicesScreen.OFFICERS_INFO` | `crime -> String.format("%s officers per 100,000 people.Canada has %s; full coverage is ...` | The officers against Canada, and the founding constabulary. |

### Statement.java ([map](map/Statement.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 150 | `Statement.BOOK_NOW` | `116` |  |
| 152 | `Statement.BOOK_THEN` | `104` |  |
| 443 | `Statement.CLOSED` | `"\u25b8"` |  |
| 445 | `Statement.OPENED` | `"\u25be"` |  |

### SummaryScreen.java ([map](map/SummaryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 160 | `SummaryScreen.PANEL_LABEL` | `Palette.TEXT_LABEL` |  |
| 161 | `SummaryScreen.PANEL_VALUE` | `Palette.TEXT_HEAD` |  |
| 162 | `SummaryScreen.PANEL_GOOD` | `Palette.GOOD` |  |
| 163 | `SummaryScreen.PANEL_WARN` | `Palette.WARN` |  |
| 164 | `SummaryScreen.PANEL_BAD` | `Palette.BAD` |  |
| 407 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "safety", "re...` | Every section key, so open-all does not have to be kept in step by hand. |
| 495 | `SummaryScreen.WORDS` | `new CityNeeds.Words() { @ Override public String people(double count) { return Money.pe...` | The interface's own words for a figure, which the needs are read in (CityNeeds.Words). |

### TimeChart.java ([map](map/TimeChart.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `TimeChart.AXIS_W` | `64` | How wide a y-axis's labels are held. |
| 64 | `TimeChart.NO_AXIS_W` | `14` | The margin on a side with no axis. |
| 67 | `TimeChart.BAND_ROW` | `20` | The row above the plot that recessions' names sit in. |
| 70 | `TimeChart.TIME_ROW` | `20` | The row under the plot that the years sit in. |
| 73 | `TimeChart.EPISODE_ROW` | `18` | One row of the episode lane. |
| 76 | `TimeChart.FLAG_ROW` | `24` | The decision lane. |
| 79 | `TimeChart.SMALL_FLAG_ROW` | `18` | ...on a small chart handed decisions (0.7.38): the same lane, smaller. |
| 82 | `TimeChart.OVERVIEW` | `52` | The overview strip under the lanes. |
| 85 | `TimeChart.HANDLE` | `7` | How close, in pixels, the pointer must be to the overview window's edge to take it. |
| 88 | `TimeChart.FLAG_GAP` | `16` | A flag closer than this many pixels to the first of the group before it is drawn in that group, as one flag with the count. |
| 91 | `TimeChart.RECESSION_SHADE` | `.10` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 94 | `TimeChart.DRAG_SLOP` | `3` | How far a press may wander and still be a click rather than a drag. |
| 97 | `TimeChart.SETTLE_MS` | `280` | How long the window must rest before the page under the chart is redrawn for it, in milliseconds. |
| 100 | `TimeChart.CARD_W` | `300` | The card's widest. |
| 103 | `TimeChart.NOTCH` | `40` | A wheel notch, in the pixels JavaFX reports it as. |
| 106 | `TimeChart.WHEEL_OWNER` | `"TimeChart.wheel"` | Which node owns the wheel: the window's page-scroll filter leaves a wheel over this alone (UserInterface). |
| 1174 | `TimeChart.CARD_DECISIONS` | `10` | At most this many decisions are listed on a flag's card; the rest are counted. |
| 1346 | `TimeChart.MEASURE` | `new javafx.scene.text.Text()` | One Text node, reused to measure a string's width in a font (textWidth()). |

### TradeScreen.java ([map](map/TradeScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 97 | `TradeScreen.OVERVIEW` | `"Overview", MONTH = "The month", GOODS = "What we trade", CURRENCY = "The currency", RE...` | The five pages, in the strip's order. |
| 101 | `TradeScreen.PAGES` | `{ OVERVIEW, MONTH, GOODS, CURRENCY, RESERVES }` | ...as the strip lists them. |
| 104 | `TradeScreen.PAGE_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.LORRY, Icons.EXCHANGE, Icons.SAFE }` | ...and each one's icon: the tiles, the coin, the lorry, the two arrows, the safe. |
| 107 | `TradeScreen.TRADE_HOME` | `OVERVIEW` | The page the tab opens on, and the one the rail's trade icon resets to. |
| 122 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` | The currency's two pages until 0.7.35: a door that names the first lands on The currency. |
| 125 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` | The reserves' three pages until 0.7.35: a door that names the first lands on The reserves. |
| 160 | `TradeScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and the five figures - until the frame is laid out and its own height is read. |
| 163 | `TradeScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - BankScreen's... |
| 166 | `TradeScreen.GROW_MILLIS` | `500` | How long the bars take to grow out of their axis when a month lands. |
| 169 | `TradeScreen.OVERVIEW_GOODS` | `8, PAGE_GOODS = 12` | The Overview shows this many goods; What we trade this many before "+N more" (the spec's D6). |
| 172 | `TradeScreen.EXCHANGE_CARD` | `"#exchange", HOLDINGS = "#holdings", FORCES = "#forces", DEBT = "#debt", LEAVE = "#leav...` | The scroll targets a door can land on. |
| 355 | `TradeScreen.LEAD_INFO` | `"What the city sells, what it buys, and what it owes in somebody else's money." + "The ...` | The tab's (i): the landing's lead (the spec's L2), and what the Overview shows. |
| 360 | `TradeScreen.PAGE_INFO` | `{ "Every dollar that crossed the city's edge this month, and which way: a walk from wha...` | Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). |
| 403 | `TradeScreen.NOT_COUNTED` | `"not counted yet"` | What a figure of the month says until a month has turned since the city was loaded or founded (ForeignAccounts.isMonthCounted()). |
| 406 | `TradeScreen.NOT_COUNTED_NOTE` | `"since the city was loaded or founded: a month on, it is"` | ...and its note. |
| 761 | `TradeScreen.GAUGES_INFO` | `"Always here, deliberately understated, and the same three every month - so a " + "play...` | The section's (i): the banner's reason (the spec's L8). |
| 805 | `TradeScreen.FUEL` | `"Fuel for the railway"` | The railway's fuel, as a row (the spec's D25: a word of the implementer's). |
| 911 | `TradeScreen.TRADE_INFO` | `"Every good the city's businesses sold abroad and bought abroad this month, from " + "t...` | WHAT WE TRADE's (i). |
| 918 | `TradeScreen.NOTHING_CROSSED` | `"Nothing crossed the city's edge this month.No exports, no imports, nothing " + "borrow...` | The empty month's whole (the spec's M2). |
| 979 | `TradeScreen.FREIGHT_AFTER_LOAD` | `"Just loaded: the railway's freight on each good is struck when the month " + "turns, s...` | The prices just after a load (the spec's B14, Infrastructure's open question, shown and not fixed). |
| 1013 | `TradeScreen.RATE_INFO` | `"How many of the city's dollars one US dollar costs.Higher is a weaker currency: " + "i...` | THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). |
| 1067 | `TradeScreen.COVER_SCALE` | `ForeignAccounts.COMFORTABLE_COVER * 2` | The cover gauge's scale: twice the comfortable line, a year of imports. |
| 1094 | `TradeScreen.PARITY_SCALE` | `ForeignAccounts.PARITY_FAR * 1.5` | The parity gauge's scale either side: half as far again as PARITY_FAR, so the red band shows. |
| 1128 | `TradeScreen.COVER_INFO` | `String.format("Import cover is the oldest test there is: if every dollar of " + "earnin...` | The gauges' (i)s: what each one measures (the cover sentence, the spec's C1; the others the landing's notes). |
| 1134 | `TradeScreen.BACKING_INFO` | `String.format("The vault against the foreign money parked in the city's bank, " + "whic...` | ...the backing gauge's: the vault against the money that can leave, and where a run becomes likely. |
| 1138 | `TradeScreen.PARITY_INFO` | `String.format("How far the rate sits from parity - where a basket costs the same " + "h...` | ...and the parity gauge's: the one parity rule's two lines, and where else they are read. |
| 1170 | `TradeScreen.NOTHING` | `.0005` | Under half a dollar is nothing: a step that small is named, not drawn. |
| 1242 | `TradeScreen.CURRENT_INFO` | `"What the city earned from the world by selling it things, less what it spent buying " ...` | The current account's note (the spec's A1). |
| 1247 | `TradeScreen.FINANCIAL_INFO` | `"Borrowing abroad and foreign money parking here are both inflows, and neither is " + "...` | The financial account's note (A2). |
| 1252 | `TradeScreen.INCOME_INFO` | `"Interest and dividends: what the businesses' and the households' paper abroad paid " +...` | The income line's note (B3). |
| 1257 | `TradeScreen.SURPLUS_INFO` | `"A surplus month: the world owes the city a little more than it did, and that is what "...` | The month's closing words (the spec's D17). |
| 1261 | `TradeScreen.DEFICIT_INFO` | `"A deficit month has to be settled in somebody else's money: out of the vault, or by " ...` | ...and a deficit month's. |
| 1325 | `TradeScreen.MONTH_INFO` | `"Every dollar that crossed the city's edge this month, and which way.Steps: each " + "c...` | The hero's (i). |
| 1331 | `TradeScreen.NOT_SAVED_INFO` | `"The month's flows across the edge - what was sold and bought abroad, the income, " + "...` | Why a loaded city reads nothing yet (the spec's B1; D4 awaits Jerus). |
| 1337 | `TradeScreen.HAND_INFO` | `"Below the line, and deliberately: an intervention does not earn or spend anything " + ...` | The treasury's hand (the spec's A6). |
| 1342 | `TradeScreen.VALUATION_INFO` | `"%s of foreign claims were written off this month.It improves what the city owes " + "t...` | The valuation change (M5, A4; D15: a chip, not an alert). |
| 1478 | `TradeScreen.RIVER_INFO` | `"Band width is money.The two sides balance because they must: what came in and what " +...` | The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). |
| 1603 | `TradeScreen.HOLDINGS_INFO` | `"The stocks the flows add up to - the rough shape of an international investment " + "p...` | The holdings' (i) (the spec's A5). |
| 1835 | `TradeScreen.RECORD_INFO` | `"One month says whether a mill was staffed.The run says whether the city earns its " + ...` | The record's sentence (the spec's H1). |
| 1896 | `TradeScreen.WORLD_INFO` | `"Every world price is quoted in the world's money and converted at the rate.So a " + "w...` | The world prices' note (the spec's G3). |
| 2163 | `TradeScreen.FORCES_INFO` | `"One reading: what the next month does to the rate, on the accounts as they stand " + "...` | The forces card's (i) (the spec's F1). |
| 2169 | `TradeScreen.COMES_TO_INFO` | `"The push is what the month is doing to the currency; the pull is the basket " + "dragg...` | WHICH COMES TO's (i) (F3's two notes). |
| 2283 | `TradeScreen.VAULT_MOVED_INFO` | `"The vault is kept in dollars, so its dollar figure stays put and its local figure " + ...` | What the currency does to the vault (the spec's E2 note, both ways). |
| 2357 | `TradeScreen.ONE_POT_INFO` | `"The vault is one pot — the game does not tag a dollar as borrowed or earned, and it " ...` | WHOSE IT IS's (i) (the spec's R4, and B11: the method's name is out of it). |

### UserInterface.java ([map](map/UserInterface.md))

| line | constant | value | says |
|---:|---|---|---|
| 279 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 429 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 441 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 442 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 463 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 1280 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1678 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1753 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1756 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1759 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1771 | `UserInterface.DATE_WIDTH` | `164` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five... |
| 1774 | `UserInterface.DATE_SIZE` | `17` | The date's size in the clock (0.7.24; it was 19). |
| 1777 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1780 | `UserInterface.SPARK_WIDTH` | `72` | A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). |
| 1782 | `UserInterface.SPARK_HEIGHT` | `16` | ...and its height, the label's row (0.7.24; it was 30). |
| 1785 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1788 | `UserInterface.TILE_FIGURE` | `15` | The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). |
| 1790 | `UserInterface.TILE_LABEL` | `10.5` | The size of a tile's label, and of the money block's. |
| 1792 | `UserInterface.TILE_CHANGE` | `10.5` | The size of a tile's change line (0.7.24; it was 11). |
| 2152 | `UserInterface.MONEY_FIGURE` | `28` | The cash in the money block: the mockups' 28 px. |
| 2721 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 2722 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 2723 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 2726 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 2729 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 3072 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 3075 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 3150 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3199 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3644 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 3831 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4507 | `UserInterface.CONSTRUCTION_TAB` | `44` | How wide the construction panel's tab is. |
| 4765 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 4908 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 4911 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 4912 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 4915 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 5356 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 5643 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 5646 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 5649 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

## harnesses (136 constants)

### AgricultureCheck.java ([map](map/AgricultureCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `AgricultureCheck.MIXED` | `"Mixed Farm"` |  |
| 57 | `AgricultureCheck.GRAIN` | `"Grain Farm"` |  |
| 58 | `AgricultureCheck.GLASS` | `"Greenhouse Complex"` |  |

### AllChecks.java ([map](map/AllChecks.md))

| line | constant | value | says |
|---:|---|---|---|
| 27 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "NewGameCheck", "CalendarCheck", "BooksCheck", "WaterCheck", "Po...` | In the order they are cheapest to fail. |

### BondCheck.java ([map](map/BondCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 116 | `BondCheck.ISSUER` | `Sectors.CONSTRUCTION` | The sector every played fixture's bond is issued by: sound, owing nothing, with plant to borrow against. |

### BuildCardCheck.java ([map](map/BuildCardCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 615 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind.BUILDING }...` | The examples: a word shaped as the model files it, and the kind it must read as. |

### BusinessServicesCheck.java ([map](map/BusinessServicesCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `BusinessServicesCheck.CONTACT` | `"Contact Centre"` | The three rungs, and the band each is meant to employ. |
| 59 | `BusinessServicesCheck.SHARED` | `"Shared Services Centre"` |  |
| 60 | `BusinessServicesCheck.OFFICE` | `"Engineering Services Office"` |  |

### CentralBankCheck.java ([map](map/CentralBankCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 167 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the window's i...` |  |
| 170 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1072 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |

### ConstructionControlCheck.java ([map](map/ConstructionControlCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 111 | `ConstructionControlCheck.RE` | `Sectors.REAL_ESTATE` |  |

### ConsumptionCheck.java ([map](map/ConsumptionCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 38 | `ConsumptionCheck.LADDER` | `{ 1, 2, 5, 10, 20, 27, 50, 90, 200, 500, 2_000 }` |  |

### CreditCheck.java ([map](map/CreditCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 20 | `CreditCheck.IND` | `Sectors.INDUSTRY` |  |

### CurrencyCheck.java ([map](map/CurrencyCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 97 | `CurrencyCheck.OLD_DIAL_STOP` | `.25` | The dial's stop until 0.7.2, for the assertion that it is gone. |
| 100 | `CurrencyCheck.OLD_SPREAD_STOP` | `.06` | The hot money's old stop, likewise: six points. |
| 103 | `CurrencyCheck.OLD_MAX_RATE` | `100` | The currency's guard above until 0.7.3, a hundred local dollars to one of theirs - for the assertions that a rate goes past it. |
| 106 | `CurrencyCheck.OLD_MIN_RATE` | `.01` | ...and below, a hundredth. |
| 527 | `CurrencyCheck.Before.broken` | `new int [ 5 ]` |  |

### EducationCheck.java ([map](map/EducationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `EducationCheck.OUT` | `System.out` |  |
| 64 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### ExchangeCheck.java ([map](map/ExchangeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 87 | `ExchangeCheck.W` | `DebtManager.WORLD_BASE_RATE` |  |
| 88 | `ExchangeCheck.N` | `Equity.COMPANIES.length` |  |
| 89 | `ExchangeCheck.HURDLE` | `W + Equity.FOREIGN_PREMIUM` |  |

### FoodProcessingCheck.java ([map](map/FoodProcessingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `FoodProcessingCheck.MEAT_WORKS` | `"Meat Works"` |  |
| 65 | `FoodProcessingCheck.SNACKS` | `"Snack & Oils Plant"` |  |
| 66 | `FoodProcessingCheck.BOTTLING` | `"Bottling Plant"` |  |

### FundLedgerCheck.java ([map](map/FundLedgerCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 314 | `FundLedgerCheck.ISSUER` | `"Mining"` | The issuer of section 3's one bond: a sector's name, which BondMarket.writeDown() finds an issuer's bonds by. |

### HistoryCheck.java ([map](map/HistoryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 38 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 39 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### HouseholdMemoryCheck.java ([map](map/HouseholdMemoryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 42 | `HouseholdMemoryCheck.WORKING` | `java.util.Arrays.stream(FamilyStructure.values()).filter(s -> ! s.isRetired() & & s ! =...` |  |

### HousingCheck.java ([map](map/HousingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `HousingCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### InboxCheck.java ([map](map/InboxCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `InboxCheck.OUT` | `System.out` |  |
| 46 | `InboxCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### LabourCheck.java ([map](map/LabourCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `LabourCheck.OUT` | `System.out` |  |
| 34 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 746 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 749 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |

### LongPlaytest.java ([map](map/LongPlaytest.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `LongPlaytest.TARGET_MONTHS` | `4000` |  |
| 55 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 161 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 163 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 202 | `LongPlaytest.FAR_FROM_PARITY` | `2.0` | How far from parity, as a multiple of it, a weak currency has to be to count as far from it. |
| 218 | `LongPlaytest.dialPath` | `new java.util.ArrayList<>()` | Every month's policy rate, for the dial's min, median and max over the run. |
| 220 | `LongPlaytest.inflationPath` | `new java.util.ArrayList<>()` | Every month's inflation reading, once the index has a year to read, for its median. |
| 229 | `LongPlaytest.spendPath` | `new java.util.ArrayList<>()` | Every month's spend factor (0.7.3): the share of their spending above subsistence the households planned at, on the month's real deposit rate - HouseholdBalance.getSpendFactor() after the month. |
| 247 | `LongPlaytest.bankNii` | `new double [ 12 ], bankFees = new double [ 12 ], bankOther = new double [ 12 ], bankOpe...` | THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for the checkpoint line that prints its price build-up beside its margin, its cost ratio, its fee share and its return on equity - ... |
| 264 | `LongPlaytest.bankYears` | `new java.util.ArrayList<>()` | THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own target averaged over each year, its return on the equity it opened the year with, its provisions over the loans it made (the ... |
| 287 | `LongPlaytest.watchSpell` | `new java.util.HashMap<>()` |  |
| 300 | `LongPlaytest.refusedOnPrice` | `new java.util.TreeMap<>()` |  |
| 310 | `LongPlaytest.houseReasons` | `new java.util.TreeMap<>()` |  |
| 354 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 509 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 521 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 525 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 560 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 630 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 641 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 642 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 643 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 644 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 645 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 646 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 647 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 650 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 653 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 654 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 655 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 656 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 659 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 660 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 661 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 662 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 725 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 726 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 727 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 728 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 730 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 734 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they... |
| 736 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 737 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1026 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1735 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasional"))` | True when this run is played by somebody paying attention. |
| 1742 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1768 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(System.getPr...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1796 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").trim().toUp...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 1863 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 1866 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 2198 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 2206 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE").trim()....` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2233 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").trim())` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 2236 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT").trim())` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 2239 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 2264 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2283 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2299 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(System.getPrope...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2314 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueOf(System.g...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2330 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.valueOf(System....` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2405 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 2850 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 3065 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 3097 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 3630 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 3638 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |

### ManufacturingCheck.java ([map](map/ManufacturingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 62 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 63 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |

### MonetaryCheck.java ([map](map/MonetaryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 479 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 482 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 485 | `MonetaryCheck.HELD_FROM` | `ForeignAccounts.SETTLING_MONTHS + 1` | The month the dial is held from: the first in which the currency may move. |
| 498 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 501 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |

### MortgageCheck.java ([map](map/MortgageCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 113 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |

### NewGameCheck.java ([map](map/NewGameCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 970 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 971 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |

### PolicyPreviewCheck.java ([map](map/PolicyPreviewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 102 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "500" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders. |

### PopulationCheck.java ([map](map/PopulationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `PopulationCheck.ADULT_MIX` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share these fixtures run at. |

### RestaurantsCheck.java ([map](map/RestaurantsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `RestaurantsCheck.DINER` | `"Diner"` |  |
| 72 | `RestaurantsCheck.RESTAURANT` | `"Restaurant"` |  |

### RestructureCheck.java ([map](map/RestructureCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 514 | `RestructureCheck.DOLLAR_ASK` | `5_000` | What the dollar round trip borrows: small against what its city earns, so the world's premium stays low. |

### SectorBooksCheck.java ([map](map/SectorBooksCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `SectorBooksCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### SectorFlowCheck.java ([map](map/SectorFlowCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `SectorFlowCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 92 | `SectorFlowCheck.ORDERS` | `{ { "House", "400" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders, and whether each went on site. |
| 98 | `SectorFlowCheck.built` | `new java.util.LinkedHashMap<>()` | ...whether each went on site, as city() found it. |

### StaleCheck.java ([map](map/StaleCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 158 | `StaleCheck.PLANTED` | `""" package ham.citybuildersim; /** * A fixture with three defects planted in it. * * I...` | Three planted lies: a stranded javadoc, a file that is not there, and a header out by one. |
| 185 | `StaleCheck.SOUND` | `""" package ham.citybuildersim; /** * A fixture with nothing wrong with it. * * The one...` | The same shapes, all of them true. |

### TradeCostCheck.java ([map](map/TradeCostCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `TradeCostCheck.DELIVERED` | `{ { Good.CROPS,.44,.28 }, { Good.GRAINS,.00080,.00050 }, { Good.BREAD,.00250,.00160 }, ...` | The delivered prices, as they were on 2026-09-16 before a line of this was written, hard-coded on purpose. |

### TreasuryCheck.java ([map](map/TreasuryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### YearBookCheck.java ([map](map/YearBookCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |
| 895 | `YearBookCheck.COMMA_CO` | `"Acme, Inc."` | Two companies on the fixture's register, named the way a CSV has to quote. |
| 897 | `YearBookCheck.QUOTE_CO` | `"The \"Good\" Co"` | ...and the second, with a double quote in its name, which the CSV doubles. |

## tools (34 constants)

### HarnessMap.java ([map](map/HarnessMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `HarnessMap.HELPERS` | `Set.of("assertTrue", "check", "close", "report", "assertEquals", "same", "within", "sam...` |  |
| 35 | `HarnessMap.SECTION` | `Pattern.compile("^\\s*(?:\\\\n)?\\s*(?:---\|===)+\\s*(.*?)\\s*(?:---\|===)+\\s*(?:\\\\n)?...` |  |

### JavaScan.java ([map](map/JavaScan.md))

| line | constant | value | says |
|---:|---|---|---|
| 324 | `JavaScan.NOT_A_METHOD_NAME` | `Set.of("if", "for", "while", "switch", "catch", "synchronized", "return", "new", "super...` |  |
| 327 | `JavaScan.MODIFIERS` | `Set.of("public", "private", "protected", "static", "final", "abstract", "synchronized",...` |  |

### ManualToMarkdown.java ([map](map/ManualToMarkdown.md))

| line | constant | value | says |
|---:|---|---|---|
| 73 | `ManualToMarkdown.GENERATED` | `"<!-- Derived from the published manual by ham.citybuildersim.tools.ManualToMarkdown" +...` | The Markdown's first line: says the file is generated, and renders as nothing on GitHub. |
| 77 | `ManualToMarkdown.STANDALONE_HEAD` | `"<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n" + "<meta name...` | What --wrap writes in front of the page's own title, links and style. |
| 81 | `ManualToMarkdown.KNOWN_TAGS` | `Set.of("html", "head", "body", "meta", "title", "link", "style", "div", "nav", "main", ...` | Every tag the page and its wrappers use; any other is passed through as its text, with a warning. |
| 87 | `ManualToMarkdown.KNOWN_CLASSES` | `Set.of("shell", "navtitle", "navnum", "eyebrow", "standfirst", "vitals", "vital", "sech...` | Every class the page uses; an element with another is rendered as its bare tag, with a warning. |
| 92 | `ManualToMarkdown.BLOCKS` | `Set.of("head", "title", "link", "meta", "style", "div", "nav", "main", "header", "foote...` | The elements that are blocks in Markdown; everything else is inline text inside one. |
| 97 | `ManualToMarkdown.VOID` | `Set.of("br", "link", "meta", "hr", "img", "input", "wbr", "base", "col", "area", "embed...` | Elements with no end tag. |
| 101 | `ManualToMarkdown.RAW` | `Set.of("style", "script", "title", "textarea")` | Elements whose content is text, never tags. |
| 104 | `ManualToMarkdown.HEAD_TAGS` | `Set.of("title", "link", "meta", "style", "base", "script")` | The elements that belong in a head: what --wrap moves there, and how the service's wrapper is recognised. |
| 111 | `ManualToMarkdown.EM_OPEN` | `'\uE001', EM_CLOSE = '\uE002', STRONG_OPEN = '\uE003', STRONG_CLOSE = '\uE004', BREAK =...` | Inline Markdown is built as text in which these private-use characters stand for the constructs whose spelling depends on what ends up beside them, and it is resolved once a whole paragraph or cell... |
| 116 | `ManualToMarkdown.ENTITY_LIKE` | `Pattern.compile("&(#[0-9]+\|#[xX][0-9a-fA-F]+\|[A-Za-z][A-Za-z0-9]*);")` | Text that Markdown would read as a character reference and decode a second time. |
| 458 | `ManualToMarkdown.CP1252` | `"\u20AC\u0081\u201A\u0192\u201E\u2026\u2020\u2021\u02C6\u2030\u0160\u2039\u0152\u008D\u...` | What HTML reads &#128; to &#159; as: the Windows-1252 characters, not the C1 controls. |
| 853 | `ManualToMarkdown.Ctx.PLAIN` | `new Ctx(false, false, false, false)` |  |
| 1207 | `ManualToMarkdown.ENTITIES` | `entities()` |  |
| 1224 | `ManualToMarkdown.ENTITY_TABLE` | `""" AElig C6 AMP 26 Aacute C1 Abreve 102 Acirc C2 Acy 410 Afr 1D504 Agrave C0 Alpha 391...` | Every named character reference HTML defines (the WHATWG list, html.spec.whatwg.org/entities.json), the forms that end in a semicolon: the name, then its code point in hex, or two joined by a plus. |

### MonthOrder.java ([map](map/MonthOrder.md))

| line | constant | value | says |
|---:|---|---|---|
| 30 | `MonthOrder.DEFAULT` | `"Game.nextMonth,Game.startOfMonthUpdate,SimulationEngine.simulateMonth," + "SimulationE...` |  |

### SourceTree.java ([map](map/SourceTree.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `SourceTree.AREAS` | `{ "model", "sectors", "interface", "harnesses", "tools" }` |  |

### Stale.java ([map](map/Stale.md))

| line | constant | value | says |
|---:|---|---|---|
| 251 | `Stale.JAVA_REF` | `Pattern.compile("\\b([A-Z][A-Za-z0-9]*)\\.java\\b")` |  |
| 259 | `Stale.DOC_REF` | `Pattern.compile("\\b(docs(?:/[A-Za-z0-9_-]+)*/[a-z0-9][a-z0-9-]*\\.md)\\b")` | A document is only reported when it is written as a path under docs/ with a lower-case name. |
| 265 | `Stale.SHAPE_NAMES` | `Set.of("Name", "NAME", "Something", "Foo", "Bar", "Baz", "ClassName", "Class", "Screen"...` | Names that are written as a shape rather than as a file: "&lt;Name&gt;Screen.java", "*Check.java", "NAME.md". |
| 327 | `Stale.NUMBERS` | `numbers()` |  |
| 328 | `Stale.NUM` | `alternation()` |  |
| 331 | `Stale.SAID` | `"(?<![A-Za-z-])(" + NUM + ")"` | A number is only a number when a letter or a hyphen does not run into it: "fifty-six" is 56, never six. |
| 333 | `Stale.BANNERS` | `Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+(?:top-level\\s+\|class-level\\s+)?b...` |  |
| 334 | `Stale.SECTIONS` | `Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+sections?\\b")` |  |
| 335 | `Stale.HARNESSES` | `Pattern.compile("(?i)\\b" + SAID + "\\s+harness(?:es)?\\b")` |  |
| 336 | `Stale.UI_FILES` | `Pattern.compile("(?i)\\b" + SAID + "\\s+files\\b")` |  |
| 337 | `Stale.SECTORS` | `Pattern.compile("(?i)\\b" + SAID + "\\s+(?:private\\s+)?sectors?(?:\\s+classes)?\\b")` |  |
| 533 | `Stale.LIBRARY` | `Set.of("println", "runLater", "equals", "hashCode", "toString", "compareTo", "format", ...` | Names that appear in prose here and belong to the JDK or JavaFX, not to the tree. |
| 541 | `Stale.PLACEHOLDERS` | `Set.of("foo", "bar", "baz", "doSomething")` | Names written as an example of a member rather than as one: "see foo()" is the shape, not a claim. |
| 549 | `Stale.CALL` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\.([a-zA-Z][A-Za-z0-9_]*)\\(\\)\|\\b([a-z][A-Za...` | A member is named in prose as name() - the empty parentheses, with nothing between them and no space before them, are what makes it a member and not an English word with a parenthesis after it. |


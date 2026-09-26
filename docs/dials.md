# The dials

Generated 2026-09-26 by `ham.citybuildersim.tools.Dials` - every `static final` constant in the tree, with the comment that explains it. Do not edit; regenerate with `Regenerate maps.bat`.

**953 constants in 223 files.**

## model (590 constants)

### AgeBand.java ([map](map/AgeBand.md))

| line | constant | value | says |
|---:|---|---|---|
| 180 | `AgeBand.MAX_MONTHLY_MORTALITY` | `.05` | No band may lose more than this in a single month, whatever modifies it. |

### Bank.java ([map](map/Bank.md))

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
| 668 | `Bank.BASE_LOSS_RATE` | `.004` | What a sound loan is expected to lose a year through the cycle, as a share of the book: 0.4%, about what Canada's big banks provision for credit losses in a normal year (RBC, 2025). |
| 671 | `Bank.COST_WINDOW_MONTHS` | `12` | Months of payroll and upkeep the running costs are measured over: a year, so one month's building bill is not a price. |
| 674 | `Bank.PRIME_TERM_MONTHS` | `BusinessDebtManager.LOAN_TERM_MONTHS` | The term prime is struck at: a business loan's, BusinessDebtManager.LOAN_TERM_MONTHS, which takes the short end's term premium. |
| 687 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 912 | `Bank.IRB_CONFIDENCE` | `.999` | The confidence the IRB capital is struck at: 99.9% of years, BCBS (2006) paragraph 272. |
| 915 | `Bank.IRB_CORRELATION_HIGH` | `.24` | The IRB corporate correlation for the soundest firms: 24%, BCBS (2006) paragraph 272. |
| 918 | `Bank.IRB_CORRELATION_LOW` | `.12` | ...and for the riskiest: 12%, the same paragraph. |
| 921 | `Bank.IRB_CORRELATION_DECAY` | `50` | How fast the correlation falls from the one to the other as the default rate rises: the 50 in exp(-50 x PD), the same paragraph. |
| 924 | `Bank.IRB_MATURITY_YEARS` | `2.5` | The effective maturity of a corporate exposure under the foundation IRB approach: 2.5 years, BCBS (2006) paragraph 318 - where the maturity adjustment reads 1 / (1 - 1.5 b). |
| 927 | `Bank.IRB_MATURITY_A` | `.11852` | The maturity adjustment's slope, b(PD) = (0.11852 - 0.05478 ln PD)^2: its constant, BCBS (2006) paragraph 272. |
| 930 | `Bank.IRB_MATURITY_B` | `.05478` | ...and its log coefficient, the same paragraph. |
| 945 | `Bank.SECTOR_CORRELATION_MULTIPLIER` | `1.25` | HOW MUCH MORE CORRELATED THE FIRMS OF ONE INDUSTRY ARE than the IRB's corporate correlation, which is struck for a book spread across every industry: 1.25, the asset value correlation multiplier Ba... |
| 973 | `Bank.IRB_QUANTILE` | `inverseNormal(IRB_CONFIDENCE)` | The standard normal quantile at IRB_CONFIDENCE, G(0.999) in the IRB formula: struck once. |
| 1397 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 1726 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 1887 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 2369 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 2372 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 2756 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - s... |
| 2759 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of... |
| 2762 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 3271 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not... |
| 3274 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 3277 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 3573 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 3576 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 3579 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 3670 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point c... |
| 3764 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 3767 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 3946 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into... |
| 4027 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit... |
| 4423 | `Bank.BRANCH_CLOSE_MONTHS` | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | Closed months in a row the book must fail to keep its branches' staff before the bank closes one: BusinessInvestment.DISTRESS_LOSS_MONTHS, the two years a landlord or a maker loses money before it ... |

### BondMarket.java ([map](map/BondMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 111 | `BondMarket.BANK` | `"bank"` | The bank's desk, on the book. |
| 113 | `BondMarket.WORLD` | `"world"` | The world, on the book. |
| 115 | `BondMarket.CELL` | `"household:"` | One household cell on the book - bidding, asking, or selling in the waterfall: this, then its key. |
| 127 | `BondMarket.DEFAULT_RATE_FLOOR` | `Bank.BASE_LOSS_RATE / BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | THE LEAST A DEFAULT RATE IS READ AT: the through-the-cycle default rate a sound loan is priced for, Bank.BASE_LOSS_RATE over a loan's loss given default, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT... |
| 130 | `BondMarket.TERM_YEARS` | `CorporateBond.TERM_MONTHS / 12.0` | The term in years, for spreading an issue's costs over its life. |
| 133 | `BondMarket.CHEAPER_BY` | `1e-11` | The arithmetic a bond's all-in cost may sit over the loan's and still be "no dearer": a billionth of a point a year - the bisection's own resolution, not a margin (plan()). |

### BuildLog.java ([map](map/BuildLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `BuildLog.KEEP_MONTHS` | `DemolitionLog.KEEP_MONTHS` | How long a completion stays on the panel. |
| 37 | `BuildLog.MAX_ENTRIES` | `40` | Hard cap, so a city building constantly cannot grow this without limit. |

### BuildingCatalog.java ([map](map/BuildingCatalog.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `BuildingCatalog.FILE_NAME` | `"buildings.json"` |  |

### BuildingManager.java ([map](map/BuildingManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 53 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3295 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 3318 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 4256 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 1813 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 1815 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 1817 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 1819 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 1821 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 1823 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2311 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 2897 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 2899 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3316 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BusinessInvestment.java ([map](map/BusinessInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 48 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 51 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 54 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 57 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site. |
| 97 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, expressed as months of the city's whole construction output. |
| 100 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 254 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 257 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 260 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 269 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |

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
| 88 | `CentralBank.WINDOW_PENALTY` | `.0025` | What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so bo... |
| 91 | `CentralBank.DEFAULT_ADVANCES_MONTHS` | `6` | The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past ... |
| 94 | `CentralBank.MAX_ADVANCES_CEILING` | `36` | The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. |
| 97 | `CentralBank.REVENUE_MONTHS` | `12` | How many months of the treasury's revenue the ceiling is averaged over. |
| 100 | `CentralBank.MAX_QE_SHARE` | `.5` | The most of the city's term paper the holdings dial may aim at: past half the market the central bank IS the market. |
| 103 | `CentralBank.QE_SPEED` | `.25` | The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying towar... |
| 106 | `CentralBank.QE_COMPRESSION` | `1.0` | How much of the term premium the maximum holding takes away: all of it, at 1. |

### CityCalendar.java ([map](map/CityCalendar.md))

| line | constant | value | says |
|---:|---|---|---|
| 28 | `CityCalendar.EPOCH_YEAR` | `2000` | The year month 1 falls in. |
| 30 | `CityCalendar.MONTHS` | `{ "January", "February", "March", "April", "May", "June", "July", "August", "September"...` |  |
| 35 | `CityCalendar.SHORT_MONTHS` | `{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }` |  |
| 41 | `CityCalendar.DAYS` | `{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }` | Days in each month of a common year; February is corrected below. |

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
| 74 | `CorporateBond.TERM_MONTHS` | `Mortgage.MORTGAGE_TERM_MONTHS` | The term every bond is issued at: ten years, the benchmark tenor of investment-grade corporate issuance, and the term the model already prices at (the insured mortgage's, Mortgage.MORTGAGE_TERM_MON... |
| 77 | `CorporateBond.COUPONS_A_YEAR` | `12` | Coupons a year: one a month, the model's month; real bonds pay twice a year. |

### Crime.java ([map](map/Crime.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `Crime.CANADA_OFFICERS_PER_100K` | `180` | Police officers per 100,000 people, Canada, 2025 (StatCan: 75,107 officers). |
| 74 | `Crime.FULL_OFFICERS_PER_100K` | `2 * CANADA_OFFICERS_PER_100K` | Full coverage: twice Canada's level. |
| 77 | `Crime.CANADA_CRIMES_PER_100K` | `5_585` | Police-reported crime per 100,000 people a year, Canada, 2025. |
| 80 | `Crime.CANADA_HOMICIDES_PER_100K` | `1.61` | Homicides per 100,000 people a year, Canada, 2025. |
| 83 | `Crime.MAX_DETERRENCE` | `.90` | What full coverage takes off. |
| 91 | `Crime.DETERRENCE_POWER` | `1.5` | The curve between none and full. |
| 94 | `Crime.NO_POLICE_WEIGHT` | `2` | Every adult at liberty, tempted this much more with no police at all. |
| 97 | `Crime.VIOLENT_SHARE` | `.25` | A quarter of crime is violent. |
| 100 | `Crime.MONTHS_OFF_PER_VIOLENT` | `.5` | Months off work per violent crime, on the sick rate. |
| 103 | `Crime.KILLED_PER_VIOLENT` | `CANADA_HOMICIDES_PER_100K /(CANADA_CRIMES_PER_100K * VIOLENT_SHARE)` | Of violent crimes, the share that kill: Canada's homicides over its violent crime, 0.115%. |
| 107 | `Crime.THEFT_WAGE_SHARE` | `.25` | What one property crime takes, as a share of a month's unskilled wage. |
| 110 | `Crime.FROM_HOUSEHOLDS` | `.5` | ...of which this much from households, the rest from the businesses. |
| 113 | `Crime.CAUGHT_AT_FULL` | `.09` | The share of crimes that end in a sentence at full coverage; straight down to none with no police. |
| 116 | `Crime.SENTENCE_MONTHS` | `6` | Months a sentence lasts. |
| 153 | `Crime.CAUSES` | `Cause.values().length` |  |
| 224 | `Crime.K` | `strikeK()` | Crimes a month per point of pressure. |
| 459 | `Crime.STATE_LENGTH` | `SENTENCE_MONTHS + 5 + CAUSES + 13 + 2 + 5` |  |

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
| 59 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 61 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 119 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 122 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 125 | `DebtManager.MAX_INFLATION_TARGET` | `.10` | The top of the target's dial: past ten per cent a year a target stops anchoring anything. |
| 128 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 166 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 290 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 325 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 328 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 331 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 468 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 471 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 474 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 481 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 484 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 487 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 490 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 493 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 496 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1079 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1082 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1085 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1088 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1091 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1094 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TERM_PREMIUM_...` | The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. |
| 1477 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### DebtQuote.java ([map](map/DebtQuote.md))

| line | constant | value | says |
|---:|---|---|---|
| 157 | `DebtQuote.FORMAT` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### DemolitionLog.java ([map](map/DemolitionLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 24 | `DemolitionLog.KEEP_MONTHS` | `24` | How long a demolition stays on the panel. |
| 27 | `DemolitionLog.MAX_ENTRIES` | `40` | Hard cap, so a city demolishing constantly cannot grow this without limit. |

### Denomination.java ([map](map/Denomination.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `Denomination.UNLOCK_AT` | `10.0` | How far prices have to have risen before the button appears. |
| 99 | `Denomination.FACTORS` | `{ 10, 100, 1000 }` | The factors the player may choose between. |
| 111 | `Denomination.MAX_UNIT` | `1e12` | The ceiling on the unit, and it is a numeric guard rather than a policy. |

### EconomyManager.java ([map](map/EconomyManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 521 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, BuildingTy...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 533 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1568 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### Education.java ([map](map/Education.md))

| line | constant | value | says |
|---:|---|---|---|
| 79 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 235 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 248 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 251 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 269 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 278 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 957 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

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
| 837 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 840 | `Equity.SLOTS_BEFORE_PAID` | `SLOTS_BEFORE_DESK + 2` | ...and before the dividends actually paid (0.7.12 round 2). |
| 843 | `Equity.SLOTS` | `SLOTS_BEFORE_PAID + RECORD_MONTHS + 1` | The ring of dividends paid and its count, appended. |

### Exchange.java ([map](map/Exchange.md))

| line | constant | value | says |
|---:|---|---|---|
| 123 | `Exchange.SPREAD` | `.02` | The desk's ask over its bid, as a share of fair value. |
| 126 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 129 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 132 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a yield at least this far over the deposit rate, annual. |
| 140 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtSer... |
| 143 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 161 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 176 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 179 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 182 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). |
| 248 | `Exchange.POSITION_LIMIT` | `.25` | The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). |
| 259 | `Exchange.BOOK_LIMIT` | `.50` | ...and in all companies' together: the book's aggregate limit. |
| 262 | `Exchange.SPLIT_AT` | `100` | A share priced at this many times its founding price is split; at one over it, consolidated. |
| 272 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the market treats the company as worthless. |
| 288 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 308 | `Exchange.DESK` | `"bank"` | The bank's trading desk. |
| 310 | `Exchange.WORLD` | `"world"` | The world's investors. |
| 312 | `Exchange.EMIGRANTS` | `"emigrants"` | The month's leavers, selling on the way out. |
| 314 | `Exchange.CELL` | `BondMarket.CELL` | One household cell: this, then its key (the bond market's prefix, deliberately). |
| 669 | `Exchange.BOUND_CAPITAL` | `0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3` | Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. |
| 1078 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3` | Who sells, by class: the desk, the world, the leavers, a household cell - for the fill rate by seller. |
| 1347 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 1349 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

### FamilyModel.java ([map](map/FamilyModel.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `FamilyModel.REFORMING_EACH_MONTH` | `.01` | The share of households that re-form on their own each month. |
| 155 | `FamilyModel.SEEKERS` | `Seeker.values().length` |  |
| 996 | `FamilyModel.STUDIO_MAX_SIZE` | `2` | The largest unit that counts as a studio. |
| 1482 | `FamilyModel.MAX_SHARING` | `.85` | Not everybody doubles up, however dear the rent. |
| 1543 | `FamilyModel.COUPLED_SENIORS` | `.55` | What share of a retired band lives as a couple rather than alone. |
| 1544 | `FamilyModel.COUPLED_ELDERS` | `.25` |  |
| 1660 | `FamilyModel.LEGACY_SHAPES` | `{ "SENIOR_ALONE", "SENIOR_COUPLE", "SINGLE_ADULT", "COUPLE", "SINGLE_PARENT", "COUPLE_B...` | The shapes a save written before the names travelled must be read with. |
| 1682 | `FamilyModel.OUTSIDE_SLOTS` | `outsideSlots(AgeBand.values().length)` | What the people outside the families add to the save: see toSaveArray(). |
| 1727 | `FamilyModel.KIN_SLOTS` | `kinSlots(AgeBand.values().length)` |  |
| 1744 | `FamilyModel.MEMORY_SLOTS` | `FamilyStructure.values().length * PayTier.values().length + 1 + 4` | ...and what the households remember: the formed matrix, whether there is one, the month's four counts. |

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

### Formats.java ([map](map/Formats.md))

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |

### Founding.java ([map](map/Founding.md))

| line | constant | value | says |
|---:|---|---|---|
| 53 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - three-quarters of the founding village at a new city's invoices, so the city borrows from its first months, for the rest of it and for every big work. |
| 56 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 59 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 62 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 121 | `Founding.MIN_CASH` | `5_000` | The least a city may be founded with in its treasury, in thousands: D$5M, ten houses and a shop at a new city's invoices. |
| 124 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 127 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 130 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 135 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 138 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 279 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "Constructi...` | The founding village: the playtest's hand-built settlement, name and count. |
| 283 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

### Game.java ([map](map/Game.md))

| line | constant | value | says |
|---:|---|---|---|
| 495 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 768 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 771 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town... |
| 774 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 3994 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 4736 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4891 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 6028 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 6040 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 6043 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 6058 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 6067 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, W... |
| 6075 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 7729 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 1359 | `GameVersion.VERSION` | `"0.7.12"` | Bump on release. |
| 1818 | `GameVersion.SAVE_FORMAT` | `27` | The save shape. |
| 1821 | `GameVersion.FIRST_SECTOR_FORMAT` | `21` | The first format a sector can be read out of. |
| 1823 | `GameVersion.NAME` | `"CityBuilderSim"` |  |

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
| 946 | `HouseholdAccounts.STATE_SCALARS` | `18, STATE_ROWS = 19` | Scalars and row arrays in the statement's state since 0.7.7. |
| 949 | `HouseholdAccounts.SCALARS_BEFORE_ACCOUNT_FEES` | `17, ROWS_BEFORE_ACCOUNT_FEES = 18` | ...and the shape before the bank's account fee was a line on it (0.7.7). |
| 952 | `HouseholdAccounts.SCALARS_BEFORE_HEALTH` | `16, ROWS_BEFORE_HEALTH = 15` | ...and the shape before the health premium was a line on it (2026-09-19). |
| 955 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

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
| 1347 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1644 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1656 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1681 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1740 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1766 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1825 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1834 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 2082 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2628 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 2821 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point - so two and a half points of spread would take half an... |
| 2824 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and never more than this share of one issue: half, because a bond market with no bank in it is no longer the city's bank's market. |
| 3561 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3564 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 3780 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 3783 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 3786 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 3789 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 3792 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 3802 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 3812 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 3821 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 3833 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim ... |
| 3836 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_BONDS + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month'... |

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
| 67 | `LabourMarket.DEFAULT_MINIMUM_WAGE` | `PayTier.UNSKILLED.getMonthlyWage()` | Where the minimum wage starts, and therefore what the whole ladder is anchored to on month one. |
| 96 | `LabourMarket.MIN_SETTABLE_SHARE` | `.25` | The lowest the dial goes, as a share of the unskilled wage. |
| 99 | `LabourMarket.MAX_SETTABLE_MULTIPLE` | `5.0` | The highest the dial goes, as a multiple of the unskilled wage. |
| 102 | `LabourMarket.MIN_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE` | Bounds on the dial, so the screen cannot ask for a negative wage. |
| 104 | `LabourMarket.MAX_SETTABLE` | `PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE` |  |
| 137 | `LabourMarket.ELASTICITY` | `.5` | How hard a shortage pushes the wage. |
| 148 | `LabourMarket.MAX_MULTIPLE` | `4.0` | How far above base a wage can climb. |
| 151 | `LabourMarket.MIN_MULTIPLE` | `.70` | ...and how far below, before the minimum wage catches it anyway. |
| 161 | `LabourMarket.ADJUST_RATE` | `.12` | How much of the gap to its target a wage closes each month. |
| 171 | `LabourMarket.PINNED_TOLERANCE` | `.02` | How far from its floor a wage counts as PINNED. |
| 297 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 313 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |

### LandManager.java ([map](map/LandManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 48 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 61 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres. |
| 93 | `LandManager.COST_GROWTH_PER_BLOCK` | `.02` | Each block bought makes the next this much dearer - annexing outward. |
| 105 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 468 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### LandMarket.java ([map](map/LandMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `LandMarket.LISTING_SIZE` | `9` | Plots on offer at any one time. |
| 80 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands of US dollars. |
| 148 | `LandMarket.PREMIUM_PER_BLOCK_OWNED` | `.008` | Each block already owned makes the next offer this much dearer. |
| 151 | `LandMarket.PREMIUM_PER_1000_PEOPLE` | `.05` | ...and so does each thousand residents. |
| 166 | `LandMarket.IRON_PRICE_PER_TONNE` | `.0004` | What the seller charges for the ore, per tonne in the ground, in thousands of US dollars. |
| 175 | `LandMarket.SQ_FT_PER_DEPOSIT` | `400_000` | Land a single mine occupies, and therefore the room one deposit needs. |
| 178 | `LandMarket.EXTRA_DEPOSIT_CHANCE` | `.28` | Chance that a parcel with ore has one MORE site, each time it is asked. |
| 181 | `LandMarket.MAX_DEPOSITS` | `4` | However big the tract, this many sites is the most it will ever carry. |
| 218 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 221 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 233 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 247 | `LandMarket.MIN_BLOCKS` | `1` | The smallest thing the land office will sell, ever: one city block. |
| 262 | `LandMarket.BLOCKS_PER_FLOOR_STEP` | `40` | Blocks the city must already own before the floor rises another block. |
| 269 | `LandMarket.MAX_MIN_BLOCKS` | `15` | A ceiling on the floor. |
| 282 | `LandMarket.SEED` | `705_398_211_733L` | Fixed seed. |
| 598 | `LandMarket.FIELDS_PER_PARCEL` | `5` | Fields written per parcel. |
| 614 | `LandMarket.LISTING_FORMAT_MARKER` | `- FIELDS_PER_PARCEL` | Marks a listing written with deposit counts, and says how wide it is - in LOCAL money, as every listing was until 0.7.6 (USD_LISTING_MARKER). |
| 622 | `LandMarket.USD_LISTING_MARKER` | `- 100 - FIELDS_PER_PARCEL` | Marks a listing whose prices are US DOLLARS (0.7.6), as wide as the one before it. |

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
| 184 | `Migration.SURPLUS_DEPARTURE_RATE` | `.02` | What share of a band's unemployable surplus leaves each month, once its wage has stopped falling. |
| 217 | `Migration.OPPORTUNITY_FLOOR` | `.15` | What share of a band still moves here when there is no work at its level. |
| 273 | `Migration.PRICED_OUT_WEIGHT` | `1.0` | How much of the affordability excess turns into people not coming. |
| 326 | `Migration.CRIME_PULL_WEIGHT` | `.1` | How much of the crime excess turns into people not coming: a tenth off at twice Canada's rate. |
| 329 | `Migration.CRIME_DEPARTURE_RATE` | `.0005` | The share of the city that leaves a month for each whole Canada of crime over Canada's. |
| 356 | `Migration.ARRIVAL_RATE` | `.15` | How much of the gap closes each month. |
| 357 | `Migration.DEPARTURE_RATE` | `.05` |  |
| 360 | `Migration.DECLINE_MONTHS` | `12` | Consecutive months of falling wages before a tier's people give up. |
| 364 | `Migration.TIERS` | `PayTier.values().length` |  |

### MoneyAudit.java ([map](map/MoneyAudit.md))

| line | constant | value | says |
|---:|---|---|---|
| 53 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 302 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank. |

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
| 82 | `NationalAccounts.HISTORY_MONTHS` | `120` |  |
| 660 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

### OrderBook.java ([map](map/OrderBook.md))

| line | constant | value | says |
|---:|---|---|---|
| 62 | `OrderBook.DUST` | `1e-9` | Anything smaller than this is the arithmetic's dust, not a quantity: an order this small is not rested and a fill this small is not a trade. |

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
| 462 | `PopulationCohorts.LEGACY_BANDS` | `{ "BABY", "CHILD", "TEEN", "ADULT", "SENIOR" }` | The bands a save written before names existed must be read with. |

### PopulationManager.java ([map](map/PopulationManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 909 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### PriceIndex.java ([map](map/PriceIndex.md))

| line | constant | value | says |
|---:|---|---|---|
| 32 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 35 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 54 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |

### SalesTaxLedger.java ([map](map/SalesTaxLedger.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `SalesTaxLedger.TAXABLE_SALES` | `0, IMPORT_TAX = 1, ZERO_RATED = 2, CREDITED_INPUT = 3, PAYABLE = 4, CREDIT = 5, SLOTS = 6` | slots in a row |

### Sector.java ([map](map/Sector.md))

| line | constant | value | says |
|---:|---|---|---|
| 231 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 431 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 437 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 466 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 480 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 1261 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 1264 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

### Sectors.java ([map](map/Sectors.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = "Construct...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a... |
| 75 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERIALS, BUSIN...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |

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
| 30 | `StudentHousehold.UNLIMITED` | `1e15` | A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. |

### TaxPolicy.java ([map](map/TaxPolicy.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `TaxPolicy.DEFAULT_INCOME_TAX` | `.15` | Where income tax started before it was a dial. |
| 71 | `TaxPolicy.DEFAULT_PROPERTY_TAX` | `.015` | 1.5% a year, near the real-world average. |
| 74 | `TaxPolicy.MAX_PROPERTY_TAX` | `.10` | Nobody has ever paid 100% property tax and the game should not model it. |
| 77 | `TaxPolicy.MAX_INCOME_TAX` | `.60` | Above this, income tax stops being a policy and starts being confiscation. |
| 86 | `TaxPolicy.MAX_OFFSET` | `.30` | How far a band or sector may be moved from its tax's base rate, either way. |
| 135 | `TaxPolicy.DEFAULT_FARMLAND_RELIEF` | `1.0` |  |
| 138 | `TaxPolicy.FARM_SECTOR` | `Sectors.AGRICULTURE` | The sector whose ground the relief applies to. |
| 177 | `TaxPolicy.MAX_CONTRIBUTION` | `.20` | Nobody hands over more than a fifth of a wage, whatever the deficit. |
| 180 | `TaxPolicy.MAX_REPLACEMENT` | `1.00` | A pension of more than one unskilled wage is a wage, not a pension. |
| 213 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currenc... |
| 216 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 219 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 222 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 339 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.WAGE_SHARE` | Where the grant starts: the founding rule, a share of the unskilled wage. |
| 342 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 345 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 348 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 351 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 354 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 357 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 360 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so ... |
| 389 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 558 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 561 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 564 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 567 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 644 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 647 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | Past this nobody rides at all, as a multiple of the default. |
| 688 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 953 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 956 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 959 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 962 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 965 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 968 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | This build's array: a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |

### Trade.java ([map](map/Trade.md))

| line | constant | value | says |
|---:|---|---|---|
| 25 | `Trade.WORLD` | `"world"` | The other side of every export and every import. |
| 28 | `Trade.HOUSEHOLDS` | `"households"` | The households, as a buyer of the basket. |
| 31 | `Trade.CITY` | `"city"` | The treasury, as a buyer of its own buildings' materials and repairs. |

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
| 521 | `Unemployment.STATE_LENGTH` | `EI_MONTHS * 2 + 6 + PayTier.values().length * 2 + 12` |  |

### UtilitiesHandler.java ([map](map/UtilitiesHandler.md))

| line | constant | value | says |
|---:|---|---|---|
| 39 | `UtilitiesHandler.BASE_WATER_SUPPLY` | `8000` | What the city can draw before it builds anything: the legacy wells and the old municipal intake. |
| 49 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 468 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

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
| 238 | `YearBook.RULES` | `rules()` |  |
| 239 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 476 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 904 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 907 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 910 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |

## sectors (39 constants)

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
| 50 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The smallest share of payroll construction pays when it has no work. |

### FoodProcessing.java ([map](map/FoodProcessing.md))

| line | constant | value | says |
|---:|---|---|---|
| 99 | `FoodProcessing.FILLED` | `.85` | How full a new plant has to be before anybody finances one. |

### LuxuryRetail.java ([map](map/LuxuryRetail.md))

| line | constant | value | says |
|---:|---|---|---|
| 86 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop with nobody in it charges over what the piece cost it. |
| 96 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop with a queue charges. |

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

## interface (171 constants)

### BankScreen.java ([map](map/BankScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 76 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the landing; BANK_PAGES, the pages behind it |
| 78 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the landing that is lit until the player picks another; the rail's bank icon resets to it. |
| 82 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Lending", "Funding", "Capital & owners", "History" }` | The five pages behind the landing, in the chip strip's order. |
| 372 | `BankScreen.LADDER_BAR` | `190` | How wide the ladder's bars run at the highest rate on it. |

### BuildScreen.java ([map](map/BuildScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 190 | `BuildScreen.BUILD_HOME` | `"Residential"` | Which category the player was last looking at. |

### FinancesScreen.java ([map](map/FinancesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 77 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 80 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 82 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 83 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 85 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 87 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 106 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupon at all.T...` |  |
| 520 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |

### FoundingScreen.java ([map](map/FoundingScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |

### GovernmentScreen.java ([map](map/GovernmentScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` |  |

### HistoryScreen.java ([map](map/HistoryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 119 | `HistoryScreen.MAX_PLOT_POINTS` | `400` | Above this many points a line is bucket-averaged; see bucketSize(). |
| 131 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheMarket(new Trace[] { new Trace("gdp", "GDP", "MON...` |  |
| 312 | `HistoryScreen.TRACE_COLOURS` | `{ "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454", "#ce93d8", "#4dd0e1", "#d4e157", "#c8b0a5" }` | Eight, then it wraps - and the legend swatch uses the same list. |
| 353 | `HistoryScreen.GRAPH` | `760` | How wide this one screen runs. |
| 358 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and how fast it ...` |  |
| 828 | `HistoryScreen.SMALL_CHART` | `150` | How tall a pinned chart is. |
| 1013 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1016 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1019 | `HistoryScreen.SMALL_Y_AXIS` | `56` | How wide a small chart's y-axis is held when a stack is drawn behind it, so the two plots line up. |
| 1022 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1194 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart is. |
| 1202 | `HistoryScreen.Y_AXIS` | `76` | How wide each y-axis is held when there are two. |
| 1205 | `HistoryScreen.RECESSION_SHADE` | `0.12` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 1208 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 2426 | `HistoryScreen.TABLE_WIDTH` | `660` | How wide the paragraph above the buyback table wraps. |

### Icons.java ([map](map/Icons.md))

| line | constant | value | says |
|---:|---|---|---|
| 46 | `Icons.BUILD` | `"M12 10h.01 M12 14h.01 M12 6h.01 M16 10h.01 M16 14h.01 M16 6h.01" + "M8 10h.01 M8 14h.0...` | A building. |
| 63 | `Icons.LAND` | `"M2 4 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0" + "M14 5 l3-3 3 3 M14 10 l3-3 3 3 M17 14V2 M17 14...` | A tent and trees - ground, rather than a map of it. |
| 68 | `Icons.POPULATION` | `"M17 21a5 5 0 0 0-10 0" + "M22 10.5a3.5 3.5 0 0 0-5.507-2.868" + "M7.507 7.632A3.5 3.5 ...` | Three people, one in front. |
| 77 | `Icons.SERVICES` | `"M2 9.5a5.5 5.5 0 0 1 9.591-3.676.56.56 0 0 0.818 0A5.49 5.49 0 0 1 22 9.5" + "c0 2.29-...` | A heart with a pulse through it. |
| 83 | `Icons.SECTOR` | `"M12 16h.01 M16 16h.01 M8 16h.01" + "M3 19a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V8.5a.5.5 0 0 ...` | A factory. |
| 90 | `Icons.GOVERNMENT` | `"M10 18v-7 M14 18v-7 M18 18v-7 M6 18v-7 M3 22h18" + "M11.119 2.205a2 2 0 0 1 1.762 0l7....` | A parliament, columns and all. |
| 95 | `Icons.FINANCES` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H...` | A coin with a dollar in it. |
| 111 | `Icons.BANK` | `"M3 22h18 M4 18v-7 M9 18v-7 M15 18v-7 M20 18v-7" + "M2 18h20" + "M11.5 2.4a1 1 0 0 1 1 ...` | A bank: a pediment on columns, with a doorway. |
| 124 | `Icons.INFRASTRUCTURE` | `"M3 19 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0" + "M15 5 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0" + "M9 1...` | A route: two waypoints and the road that winds between them. |
| 130 | `Icons.TRADE` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 ...` | A globe. |
| 136 | `Icons.POLICY` | `"M12 3v18 M7 21h10" + "M3 7h1a17 17 0 0 0 8-2 17 17 0 0 0 8 2h1" + "M19 8 l3 8 a5 5 0 0...` | Scales. |
| 143 | `Icons.REPORTS` | `"M3 3v16a2 2 0 0 0 2 2h16" + "M19 9 l-5 5 -4-4 -3 3"` | A line on axes. |
| 148 | `Icons.SETTINGS` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2.34 0 0 1 ...` | A gear. |

### Ladder.java ([map](map/Ladder.md))

| line | constant | value | says |
|---:|---|---|---|
| 55 | `Ladder.READING` | `118` | How wide the reading is held, so the slider does not shift as the figure's width does. |
| 58 | `Ladder.BUTTON` | `28` | How wide and tall each step button is: square, and room for the glyph. |
| 61 | `Ladder.GAP` | `10` | The gap between the buttons, the slider and the reading. |

### Money.java ([map](map/Money.md))

| line | constant | value | says |
|---:|---|---|---|
| 48 | `Money.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | Thousands separators, Canadian style; every figure below goes through it. |

### Palette.java ([map](map/Palette.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Palette.STAGE` | `"#111a24"` | The middle of the window - the darkest thing, and the biggest. |
| 52 | `Palette.PANEL` | `"#1c262b"` | The four strips around it: both side panels, the date bar, the debt bar. |
| 55 | `Palette.RAISED` | `"#26343b"` | A block lifted off a panel: the date and cash boxes, an open section. |
| 58 | `Palette.PINNED` | `"#223038"` | The vitals block, and anything that should read as pinned rather than raised. |
| 61 | `Palette.FIELD` | `"#17212c"` | Inside a control, and inside the inbox: darker than the panel it sits on. |
| 64 | `Palette.CONTROL` | `"#22303c"` | A button at rest. |
| 71 | `Palette.EDGE` | `"#37474f"` | Panel against stage. |
| 74 | `Palette.HAIRLINE` | `"#33404b"` | A quieter rule: inside a panel, under a heading, around a control. |
| 77 | `Palette.CONTROL_EDGE` | `"#4a5c68"` | The border of a control that can be pressed. |
| 89 | `Palette.TEXT_MAX` | `"#ffffff"` | The date, and nothing else. |
| 92 | `Palette.TEXT_HEAD` | `"#eceff1"` | A screen title, a section heading, a figure that is the answer. |
| 95 | `Palette.TEXT_BODY` | `"#c3ccd3"` | Ordinary text and ordinary figures. |
| 98 | `Palette.TEXT_MUTED` | `"#8fa3b0"` | A caption, a unit, a subtitle, a note under a figure. |
| 101 | `Palette.TEXT_LABEL` | `"#78909c"` | The label on the left of a row. |
| 104 | `Palette.TEXT_FAINT` | `"#7d8f9c"` | Disabled, or a heading that is not the one you are on. |
| 107 | `Palette.TEXT_SPENT` | `"#6b7a84"` | Struck through, settled, over with - a resolved notice, an old entry. |
| 118 | `Palette.GOOD` | `"#5fd68a"` | It is going the right way. |
| 121 | `Palette.GOOD_MONEY` | `"#8fe0aa"` | Money going the right way - a little paler, because it is bigger type. |
| 124 | `Palette.WARN` | `"#ffb454"` | It is not wrong yet, and it will be. |
| 127 | `Palette.BAD` | `"#ff6b6b"` | It is costing the city something now. |
| 130 | `Palette.BAD_SOFT` | `"#ff9e9e"` | The same, in small type where full strength reads as shouting. |
| 133 | `Palette.BAD_TEXT` | `"#ffd9d4"` | Text sitting on the alert ground below. |
| 136 | `Palette.ALERT_GROUND` | `"#331d1d"` | Behind something wrong. |
| 139 | `Palette.ALERT_GROUND_LOUD` | `"#3b1f1f"` | Behind something wrong and unread. |
| 142 | `Palette.ALERT_EDGE` | `"#c0392b"` | The edge of either. |
| 154 | `Palette.ACCENT` | `"#5cb8ff"` | The active tab, a live link, the heading of an open section. |
| 157 | `Palette.ACCENT_FILL` | `"#2f6fa8"` | Filled: the primary button, the advance-a-month control. |
| 160 | `Palette.CONFIRM` | `"#2f7d52"` | Filled: the button that commits - pays, builds, sets the policy. |
| 163 | `Palette.CONFIRM_GROUND` | `"#13291d"` | Behind a confirmation that has already happened. |
| 172 | `Palette.SERIES` | `{ "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454", "#ce93d8", "#4dd0e1", "#d4e157", "#c8b0a5" }` |  |
| 203 | `Palette.REVENUE_RAMP` | `{ "#afd5fe", "#8dbef1", "#6aa6e4", "#468fd6", "#1577c8" }` | Money coming in. |
| 208 | `Palette.SPENDING_RAMP` | `{ "#efca9f", "#deaf78", "#cd954f", "#bc7a19", "#aa6000" }` | Money going out. |
| 227 | `Palette.LADDER` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | The three instruments on the maturity ladder, short to long. |
| 238 | `Palette.GDP_LAYERS` | `{ REVENUE_RAMP [ 0 ], REVENUE_RAMP [ 2 ], REVENUE_RAMP [ 4 ] }` | GDP's three stacked layers on the Reports page (0.7.6) - consumption, investment, government, bottom to top - on the same three validated steps as the maturity ladder, and for the same reason: they... |
| 243 | `Palette.RAMP_REST` | `"#5c6b75"` | Everything too small to have its own step. |
| 246 | `Palette.RING` | `26` | How thick a donut's ring is drawn, and how wide the hole is. |
| 260 | `Palette.MONO` | `"'Courier New'"` | Figures. |
| 264 | `Palette.GLYPH` | `"'Segoe UI Symbol', 'Segoe UI', sans-serif"` | Glyphs - the rail, the envelope, the round buttons. |
| 267 | `Palette.SIZE_HEADLINE` | `28` | The date and the cash: the two figures readable from across the room. |
| 270 | `Palette.SIZE_TITLE` | `20` | A screen's title. |
| 273 | `Palette.SIZE_LEAD` | `17` | A figure that is the point of its panel. |
| 276 | `Palette.SIZE_SECTION` | `14` | A section heading inside a screen. |
| 279 | `Palette.SIZE_HEADING` | `12` | A panel's own heading. |
| 282 | `Palette.SIZE_BODY` | `11` | Body text, and the figure in a row. |
| 285 | `Palette.SIZE_LABEL` | `10` | The label in a row, and a button in a dense list. |
| 288 | `Palette.SIZE_CAPTION` | `9` | A caption under something, and a unit after something. |
| 296 | `Palette.GAP_TIGHT` | `4` |  |
| 297 | `Palette.GAP` | `8` |  |
| 298 | `Palette.GAP_LOOSE` | `12` |  |
| 299 | `Palette.GAP_SECTION` | `20` |  |
| 302 | `Palette.RADIUS` | `4` | Corner of a block, a chip, a control. |
| 305 | `Palette.RADIUS_TIGHT` | `3` | Corner of something small - a row, a badge. |
| 315 | `Palette.RAIL` | `46` | The navigation rail, on the city panel's ground. |
| 318 | `Palette.CITY_PANEL` | `290` | The city panel, not counting the rail. |
| 321 | `Palette.BUILD_PANEL` | `280` | The construction panel down the right. |
| 324 | `Palette.STRIP` | `72` | The strip under the stage holding the dome and the time controls. |
| 327 | `Palette.BUILD_ROW` | `400` | A building row, so the price column lines up down the list. |
| 330 | `Palette.INBOX` | `470` | The inbox, sized to the 62-character lines the notices are written at. |

### PeopleScreen.java ([map](map/PeopleScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 754 | `PeopleScreen.PYRAMID_BAR` | `200` | How wide the age bars are drawn. |
| 1531 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 1532 | `PeopleScreen.SHAPE_COL` | `168` |  |

### Pieces.java ([map](map/Pieces.md))

| line | constant | value | says |
|---:|---|---|---|
| 626 | `Pieces.TILE_WIDTH` | `250` | A tile's width. |
| 628 | `Pieces.TILE_HEIGHT` | `232` | A tile's height, the same for a card and a plot. |
| 630 | `Pieces.TILE_GAP` | `10` | The gap between tiles. |

### PolicyScreen.java ([map](map/PolicyScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 78 | `PolicyScreen.POLICY_HOME` | `"Everything"` | null is the landing |
| 95 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 97 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 98 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 99 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Health", "Schools", "Subsidies" }` |  |
| 180 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 183 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 186 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 2185 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2188 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2191 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2194 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2197 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2200 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2864 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 2867 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 2902 | `PolicyScreen.SCHOOL_FIGURES` | `150` | How wide the four figures beside a school kind's ladder are held. |

### SectorScreen.java ([map](map/SectorScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `SectorScreen.SECTOR_HOME` | `"Operations"` |  |
| 71 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` |  |
| 245 | `SectorScreen.CARD_LEFT` | `280` | The width the card's name and blurb are held to, so the sparkline and the figures always have their room (0.7.4). |
| 248 | `SectorScreen.SPARK_WIDTH` | `90` | The sparkline's width on a sector's card (0.7.4): a word's size, between the blurb and the figures. |
| 251 | `SectorScreen.SPARK_HEIGHT` | `22` | ...and its height, a line of caption and a half. |
| 254 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |

### ServicesScreen.java ([map](map/ServicesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 85 | `ServicesScreen.SERVICE_HOME` | `"General care"` |  |
| 125 | `ServicesScreen.INFRA_PAGES` | `{ "Roads", "Transit", "The railway", "Freight" }` |  |

### Statement.java ([map](map/Statement.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 132 | `Statement.BOOK_NOW` | `116` |  |
| 134 | `Statement.BOOK_THEN` | `104` |  |
| 416 | `Statement.CLOSED` | `"\u25b8"` |  |
| 418 | `Statement.OPENED` | `"\u25be"` |  |

### SummaryScreen.java ([map](map/SummaryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 148 | `SummaryScreen.PANEL_LABEL` | `"#78909c"` |  |
| 149 | `SummaryScreen.PANEL_VALUE` | `"#eceff1"` |  |
| 150 | `SummaryScreen.PANEL_GOOD` | `"#5fd68a"` |  |
| 151 | `SummaryScreen.PANEL_WARN` | `"#ffb454"` |  |
| 152 | `SummaryScreen.PANEL_BAD` | `"#ff6b6b"` |  |
| 377 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "safety", "re...` | Every section key, so open-all does not have to be kept in step by hand. |

### TradeScreen.java ([map](map/TradeScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `TradeScreen.TRADE_HOME` | `"The picture"` | null is the landing |
| 73 | `TradeScreen.TRADE_MONTH_PAGES` | `{ "The picture", "The two accounts" }` |  |
| 74 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` |  |
| 75 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` |  |
| 76 | `TradeScreen.TRADE_GOODS_PAGES` | `{ "In and out", "Since founding" }` |  |

### UserInterface.java ([map](map/UserInterface.md))

| line | constant | value | says |
|---:|---|---|---|
| 193 | `UserInterface.STAGE` | `"#111a24"` | The middle of the window: the blackish blue everything else sits on. |
| 315 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 325 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed slider sticks to. |
| 326 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 347 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 1020 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1418 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1434 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads in the quiet grey. |
| 1437 | `UserInterface.STRIP_INFLATION_ALARM` | `.10` | Inflation past this, or deflation past its negative, reads red: prices are running away, or collapsing. |
| 1440 | `UserInterface.STRIP_RATE_QUIET` | `.05` | The currency within this of its parity (ForeignAccounts.deviationFromParity) reads grey, and so does one stronger than parity by any amount. |
| 1443 | `UserInterface.STRIP_RATE_ALARM` | `.25` | Weaker than parity by more than this reads red: a currency well below what its basket is worth abroad is the thing the player should notice. |
| 1768 | `UserInterface.STRIP_QUIET` | `"#78909c"` | The strip's quiet colour: the cash trend's muted grey. |
| 1771 | `UserInterface.STRIP_FIGURE` | `"-fx-font-family: 'Courier New'; -fx-font-size: 14px;" + " -fx-font-weight: bold;"` | A small figure on the strip: Courier, so the digits hold their columns, at the population's weight. |
| 1775 | `UserInterface.STRIP_CAPTION` | `"-fx-font-family: 'Courier New'; -fx-font-size: 11px;"` | ...and the caption under it, at the size of the anchors' own captions. |
| 2080 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3204 | `UserInterface.RAIL_WIDTH` | `46` | Wide enough for a glyph and its highlight, narrow enough to be an edge. |
| 3207 | `UserInterface.STRIP_HEIGHT` | `72` | The strip under the stage that holds the dome and the time controls. |
| 3579 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |

## harnesses (119 constants)

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

### BusinessServicesCheck.java ([map](map/BusinessServicesCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `BusinessServicesCheck.CONTACT` | `"Contact Centre"` | The three rungs, and the band each is meant to employ. |
| 59 | `BusinessServicesCheck.SHARED` | `"Shared Services Centre"` |  |
| 60 | `BusinessServicesCheck.OFFICE` | `"Engineering Services Office"` |  |

### CentralBankCheck.java ([map](map/CentralBankCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 123 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the window's i...` |  |
| 126 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |

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
| 61 | `EducationCheck.OUT` | `System.out` |  |
| 62 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

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
| 691 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 694 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |

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
| 350 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 493 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 505 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 509 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 544 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 614 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 625 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 626 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 627 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 628 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 629 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 630 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 631 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 634 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 637 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 638 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 639 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 640 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 643 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 644 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 645 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 646 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 709 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 710 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 711 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 712 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 714 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 718 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they... |
| 720 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 721 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1009 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1718 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasional"))` | True when this run is played by somebody paying attention. |
| 1725 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1751 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(System.getPr...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1772 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").trim().toUp...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 1817 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 1820 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 1977 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 1996 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2015 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2031 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(System.getPrope...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2046 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueOf(System.g...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2062 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.valueOf(System....` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2137 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 2582 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 2792 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 2824 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |

### ManufacturingCheck.java ([map](map/ManufacturingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 62 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 63 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |

### MonetaryCheck.java ([map](map/MonetaryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 471 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 474 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 477 | `MonetaryCheck.HELD_FROM` | `ForeignAccounts.SETTLING_MONTHS + 1` | The month the dial is held from: the first in which the currency may move. |
| 490 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 493 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |

### MortgageCheck.java ([map](map/MortgageCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 109 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 110 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |

### NewGameCheck.java ([map](map/NewGameCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 837 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 838 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |

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
| 71 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### YearBookCheck.java ([map](map/YearBookCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |

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


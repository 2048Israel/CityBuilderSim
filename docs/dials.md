# The dials

Generated 2026-10-29 by `ham.citybuildersim.tools.Dials` - every `static final` constant in the tree, with the comment that explains it. Do not edit; regenerate with `Regenerate maps.bat`.

**4,699 constants in 446 files.**

## model (2363 constants)

### AgeBand.java ([map](map/AgeBand.md))

| line | constant | value | says |
|---:|---|---|---|
| 180 | `AgeBand.MAX_MONTHLY_MORTALITY` | `.05` | No band may lose more than this in a single month, whatever modifies it. |

### Auction.java ([map](map/Auction.md))

| line | constant | value | says |
|---:|---|---|---|
| 32 | `Auction.MAX_TAIL` | `.01` | The default highest yield the city takes over fair at its own auction: one point (D27, Jerus's to move). |
| 35 | `Auction.COUPON_STEP` | `.00125` | A new bond line's coupon is the clearing yield rounded DOWN to this, an eighth of a point, so the paper sells near par (D2). |
| 38 | `Auction.BENCHMARK_LIFE` | `.9` | A bond line takes reopenings while it has this share of its term left, nine tenths (D14). |
| 41 | `Auction.BILL_MONTHS` | `{ 1, 3, 6, 12 }` | The terms a bill is sold at, in months (D3). |
| 44 | `Auction.AMORTIZING_YEARS` | `{ 5, 10, 15, 20, 25, 30 }` | The terms an amortizing bond is sold at, in years (D3). |
| 47 | `Auction.STOP_TOLERANCE` | `1e-7` | How close the bisection finds the stop, in yield: a hundredth of a basis point is 1e-6, this is a tenth of that. |
| 50 | `Auction.MAX_STEPS` | `200` | The bisection halves a gap of under a unit of yield to STOP_TOLERANCE in a few dozen steps; this is the belt over the braces. |
| 78 | `Auction.Cap.DEFAULT` | `tail(MAX_TAIL)` | The default: fair plus MAX_TAIL. |
| 81 | `Auction.Cap.FILL` | `new Cap(Mode.FILL, Double.NaN)` | No highest yield: the stop rises until the bids cover the issue, or no rule bids more. |

### AuctionTally.java ([map](map/AuctionTally.md))

| line | constant | value | says |
|---:|---|---|---|
| 28 | `AuctionTally.EPSILON` | `Auction.STOP_TOLERANCE` | How far over fair a tail must be to count as over a point: the stop is found to Auction.STOP_TOLERANCE, so a tail that is the cap's own one point, to the arithmetic's last digits, is not over it. |

### AutoBuilder.java ([map](map/AutoBuilder.md))

| line | constant | value | says |
|---:|---|---|---|
| 174 | `AutoBuilder.DEFAULT_SLACK` | `.15` | The spare margin a new city is given (Jerus, 2026-10-08: "maintain say 15% surplus service of everything"). |
| 182 | `AutoBuilder.DEFAULT_DEBT_LIMIT` | `2.40` | The debt limit a new city is given, all the city owes over a year of its output (THE DEBT LIMIT): 240% (0.7.101, Jerus's decision A6 of 2026-10-10, "have the default at 240%"). |
| 189 | `AutoBuilder.OLD_DEFAULT_DEBT_LIMIT` | `.60` | The default from 0.7.81 to 0.7.100 (star N6-2): an older save that carries exactly it loads at DEFAULT_DEBT_LIMIT (star P2-5) - every save since 0.7.81 writes its limit whether or not the dial was ... |
| 192 | `AutoBuilder.SLACK_MOST` | `.50` | The spare margin's slider runs from none - just enough - to this: half again what the city uses of every service (star N4-3). |
| 201 | `AutoBuilder.DEBT_LIMIT_MOST` | `6.0` | The debt limit's slider runs from none - with any debt it builds nothing - to six years of GDP (0.7.101, star P2-4): Jerus, "one should have the option for beyond 300%" - twice the old top, so a ci... |
| 204 | `AutoBuilder.STEP` | `.01` | The spare margin's step: a whole per cent (the debt limit's until 0.7.81). |
| 207 | `AutoBuilder.DEBT_STEP` | `.10` | The debt limit's step (0.7.101, star P2-4): ten per cent of a year's GDP - sixty steps to DEBT_LIMIT_MOST, as N6-4's five per cents were to three years; DEFAULT_DEBT_LIMIT and OLD_DEFAULT_DEBT_LIMI... |
| 210 | `AutoBuilder.LOG_MOST` | `40` | The most of its orders the log keeps, newest kept (BuildLog's cap). |
| 404 | `AutoBuilder.HELD_BACK` | `"Automatic building holds it back; "` | What the advice card's line opens with (PT1-M3); the inbox's words for the payback follow. |
| 642 | `AutoBuilder.PAID_MONTHS` | `12` | Months of monthPaid() the payback's figure averages: a year, as a lender reads a city's accounts - a year of its own recurring revenue, the sale of its property left out, as a city's debt limit is ... |
| 1447 | `AutoBuilder.Ground.NONE` | `new Ground(List.of(), 0, true)` | No ground to buy: the order fits what is free. |

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
| 3111 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 3114 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 3503 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - s... |
| 3506 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of... |
| 3509 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 4064 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not... |
| 4067 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 4070 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 4366 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 4369 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 4372 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 4463 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point c... |
| 4557 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 4560 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 4763 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into... |
| 4844 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit... |
| 5218 | `Bank.CUSTOMERS_PER_BRANCH` | `16_000` | The customers one branch serves: 16,000, TD's clients per branch - "approximately 16 million clients in Canadian Personal and Business banking" through "more than 1,000 branches" (TD, corporate inf... |
| 6041 | `Bank.SHEET_ASSETS` | `{ Sheet.RESERVES, Sheet.BUSINESS_LOANS, Sheet.INTERIM, Sheet.MORTGAGES, Sheet.FAMILIES,...` | The asset lines, in the page's order: they sum to totalAssets(). |
| 6045 | `Bank.SHEET_LIABILITIES` | `{ Sheet.DEPOSIT_FUNDING, Sheet.WINDOW, Sheet.FOREIGN_DEPOSITS, Sheet.DESK_SHORT, Sheet....` | ...and the liability lines: they sum to totalLiabilities(). |

### BoatSchedule.java ([map](map/BoatSchedule.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BoatSchedule.MONTH_SECONDS` | `60` | Seconds of boats a game month: 60 (the research's Q8: about one boat every 4 s at district zoom in the big city). |
| 60 | `BoatSchedule.LEG_SECONDS` | `4` | Seconds a boat takes to run its lane, in or out: 4 (spec-oil 2.10, the UI's). |
| 63 | `BoatSchedule.BERTH_SECONDS` | `24 / 730.5 * MONTH_SECONDS` | Seconds a boat lies at the quay: about 24 hours of a 730.5-hour month [P33], about 2 s. |
| 66 | `BoatSchedule.LANE_PLOTS` | `1536` | A lane's length, quay to the map's sea, in plots: 1,536 (six districts, spec-oil 2.10). |
| 69 | `BoatSchedule.SALT` | `0xB0A7_5417L` | The salt the calls' hashes start from. |
| 72 | `BoatSchedule.GROUP_BITS` | `7, ARRIVAL_BITS = 36, ROUTE_SHIFT = GROUP_BITS + ARRIVAL_BITS` | A call's key: its route + 1 (0 for none) from bit ROUTE_SHIFT up, its arrival in ARRIVAL_BITS ticks of a month, its group in the low GROUP_BITS. |
| 74 | `BoatSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ARRIVAL_MASK =(1L<<ARRIVAL_BITS) - 1` | The masks that take a key's group and its arrival back out, once shifted down. |
| 76 | `BoatSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |

### BondMarket.java ([map](map/BondMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 122 | `BondMarket.BANK` | `"bank"` | The bank's desk, on the book. |
| 124 | `BondMarket.WORLD` | `"world"` | The world, on the book. |
| 126 | `BondMarket.CELL` | `"household:"` | One household cell on the book - bidding, asking, or selling in the waterfall: this, then its key. |
| 128 | `BondMarket.FUND` | `"city"` | The city's fund, by its rule (0.7.14; TreasuryFund). |
| 130 | `BondMarket.FUND_HAND` | `"city:hand"` | ...and by the player's hand. |
| 145 | `BondMarket.DEFAULT_RATE_FLOOR` | `Bank.BASE_LOSS_RATE / BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT` | THE LEAST A DEFAULT RATE IS READ AT: the through-the-cycle default rate a sound loan is priced for, Bank.BASE_LOSS_RATE over a loan's loss given default, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT... |
| 148 | `BondMarket.TERM_YEARS` | `CorporateBond.TERM_MONTHS / 12.0` | The term in years, for spreading an issue's costs over its life. |
| 151 | `BondMarket.CHEAPER_BY` | `1e-11` | The arithmetic a bond's all-in cost may sit over the loan's and still be "no dearer": a billionth of a point a year - the bisection's own resolution, not a margin (plan()). |

### BridgeSurvey.java ([map](map/BridgeSurvey.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `BridgeSurvey.MAX_LENGTH_M` | `36_000` | The longest bridge that can be ordered, metres: 36,000 (est., D13: the Hangzhou Bay Bridge, 36 km, the longest over water). |
| 37 | `BridgeSurvey.SEARCH_FACTOR` | `1.25` | How far past the cap the search looks, as a multiple of it: 1.25, so a crossing a little over the cap is told its length, and the window past that is "over 45.0 km". |
| 40 | `BridgeSurvey.SEARCH_LIMIT_M` | `SEARCH_FACTOR * MAX_LENGTH_M` | The cost the search is limited to, metres: SEARCH_FACTOR times MAX_LENGTH_M (45 km); also the margin, in plots, the far landmass's owned box is widened by on every side. |
| 43 | `BridgeSurvey.ORTHOGONAL_DM` | `(int) Math.round(World.PLOT_M * 10)` | A step along a row or a column, in decimetres (a plot is 30 m). |
| 46 | `BridgeSurvey.DIAGONAL_DM` | `(int) Math.round(World.PLOT_M * 10 * Math.sqrt(2))` | A step along a diagonal, in decimetres: 30 x sqrt(2) m, 424.26 rounded. |
| 49 | `BridgeSurvey.LIMIT_DM` | `(int) Math.round(SEARCH_LIMIT_M * 10)` | The cost limit in decimetres. |
| 52 | `BridgeSurvey.MARGIN_PLOTS` | `(long) Math.ceil(SEARCH_LIMIT_M / World.PLOT_M)` | The margin the window is widened by, in plots. |
| 154 | `BridgeSurvey.WALL` | `0, WATER = 1, DEEP = 2, FAR = 3, NEAR = 4` | The plot codes the search reads a classified plot by. |
| 157 | `BridgeSurvey.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }` | The direction's step in plots and its cost in decimetres: E, SE, S, SW, W, NW, N, NE (index 8 marks a source). |
| 158 | `BridgeSurvey.DY` | `{ 0, 1, 1, 1, 0, - 1, - 1, - 1 }` |  |
| 159 | `BridgeSurvey.SOURCE` | `8` |  |

### BridgeWorks.java ([map](map/BridgeWorks.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `BridgeWorks.BASE_DEPTH_M` | `5` | The depth, metres, at and under which the deck costs the base rate: 5, a viaduct over shallows and over fresh water, whose depth the World does not hold (World.depthAt() is 0 on a lake and a river). |
| 37 | `BridgeWorks.BASE_USD_PER_M2` | `RunWorks.DECK_USD_PER_M2` | The deck a m2 at BASE_DEPTH_M or less, and on fresh water: RunWorks.DECK_USD_PER_M2 (US DOT's 2025 planning costs, $250 a sq ft, about $2,690 a m2). |
| 44 | `BridgeWorks.CONFEDERATION_COST_CAD` | `1.3e9` | The Confederation Bridge's cost as built, Canadian dollars: C$1.3 billion (Wikipedia's figure for the cost as built, about 30% over the C$840 million projected; the C$1.0 billion some sources give,... |
| 47 | `BridgeWorks.CONFEDERATION_LENGTH_M` | `12_900` | The Confederation Bridge's length, metres: 12,900 (Prince Edward Island to New Brunswick, over the Northumberland Strait). |
| 50 | `BridgeWorks.CONFEDERATION_DECK_M` | `11` | ...and its deck's width, metres: 11 (two lanes and shoulders). |
| 53 | `BridgeWorks.CONFEDERATION_DEPTH_M` | `35` | ...and the depth its piers stand in at the deepest, metres: 35 (about; the deepest water of the strait's crossing). |
| 56 | `BridgeWorks.CAD_CPI_1997_TO_2025` | `1.79` | Canada's consumer prices from 1997, the year the bridge opened, to 2025: about 1.79 (Statistics Canada, table 18-10-0005). |
| 59 | `BridgeWorks.USD_PER_CAD_2025` | `0.72` | US dollars a Canadian dollar in 2025: 0.72. |
| 62 | `BridgeWorks.CONFEDERATION_USD_PER_M2` | `CONFEDERATION_COST_CAD * CAD_CPI_1997_TO_2025 * USD_PER_CAD_2025 /(CONFEDERATION_LENGTH...` | The Confederation's deck a m2 in 2025 US dollars, its cost over its deck's area: about $11,800. |
| 66 | `BridgeWorks.HIGHWAY_DECK_WIDTH_M` | `RunWorks.DECK_WIDTH_M` | A highway bridge's deck, metres: RunWorks.DECK_WIDTH_M (23.2, the Interstate standard's four lanes and shoulders). |
| 69 | `BridgeWorks.ROAD_DECK_WIDTH_M` | `2 * 3.66 + 2 * 2.44` | A road bridge's deck, metres: two lanes of 3.66 m (12 ft) and two shoulders of 2.44 m (8 ft), the usual rural two-lane section, 12.2 m, the Confederation's 11 m beside it. |
| 76 | `BridgeWorks.RAIL_OVER_HIGHWAY` | `1.42` | How many times a highway's metre a railway's costs, 1.42: the game's own scale in RunWorks's javadoc - the Freight Line's $2,330 a m2 of its land over the Elevated Highway's $1,640. |

### Bridges.java ([map](map/Bridges.md))

| line | constant | value | says |
|---:|---|---|---|
| 29 | `Bridges.STANDING` | `0` | A bridge that stands. |
| 32 | `Bridges.ON_SITE` | `1` | ...one the builders are at work on. |
| 35 | `Bridges.SHELL` | `2` | ...one a stopped order left standing as a shell. |
| 249 | `Bridges.NONE_CHOSEN` | `Integer.MIN_VALUE` | What chosen() says when the card has picked nothing: the rule decides. |

### BuildAdvice.java ([map](map/BuildAdvice.md))

| line | constant | value | says |
|---:|---|---|---|
| 93 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 121 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Infrastructure", HEALTHCARE = "Healthcare", EDUCATION = "Educatio...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 1065 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 1068 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |
| 1071 | `BuildAdvice.SLACK` | `BusinessInvestment.TARGET_HEADROOM` | The slack an order is sized with past its projection: the businesses' own headroom (BusinessInvestment.TARGET_HEADROOM). |
| 1074 | `BuildAdvice.HORIZON` | `BusinessInvestment.PLANNING_HORIZON` | Months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); the opening's wait is held to their BusinessInvestment.MAX_ORDER_MONTHS. |
| 1087 | `BuildAdvice.NOW` | `new Ahead(0, 1, 0)` | Today's demand, no slack: what NEEDS YOU reads. |
| 1509 | `BuildAdvice.LIFE_MONTHS` | `Game.BUILD_BOND_YEARS * 12` | Months a road is weighed over (0.7.70): the funding page's bond term, Game.BUILD_BOND_YEARS - a long-lived asset "paid for over the years the city uses it". |
| 1719 | `BuildAdvice.LIFE_YEARS` | `LIFE_MONTHS / 12` | The years of a building's life the served figure reads: LIFE_MONTHS in whole years. |

### BuildCard.java ([map](map/BuildCard.md))

| line | constant | value | says |
|---:|---|---|---|
| 148 | `BuildCard.KILOWATTS` | `" kW"` | The words after a power plant's figure (0.7.28): kilowatts, the model's unit and a rate - "units a month" until then, which was neither. |
| 245 | `BuildCard.GROUP_ORDER` | `{ "Homes", "Groceries", "Filling stations", "The bank's branches", "Food mills", "Food ...` | The market groups, in the order a page lays them out. |
| 474 | `BuildCard.ROAD_PER` | `"trip over " + BuildAdvice.LIFE_MONTHS / 12 + " years, with its land"` | What a road card's first bar is per (0.7.70): a trip it takes off the road, over its life, its ground in it - BuildAdvice.lifetime() over BuildAdvice.unit(); the bar's (i) says the rest. |
| 1014 | `BuildCard.DRY_OIL` | `"dry oil"` | A DEPOSIT gate's word for a land Oil Well (0.7.103): its count is of dry sites (Game.sitesFor()). |
| 1017 | `BuildCard.NO_FEED` | `"no feed: no crude unit stands to run its stream"` | What a conversion unit's card says in place of a loss with no crude unit standing to feed it (0.7.103). |
| 1463 | `BuildCard.NOTHING_TO_WASH` | `"no raw coal goes unwashed"` | What a washing plant's feed gate says while its sector's coking mines make no raw coal its plants do not wash (CoalMining.unwashed()). |
| 1755 | `BuildCard.LAND_WORDS` | `{ "no land", "short of homes" }` | The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. |
| 1757 | `BuildCard.STAFF_WORDS` | `{ "could staff" }` | ...STAFF's. |
| 1759 | `BuildCard.LICENCE_WORDS` | `{ "licence", "licences" }` | ...LICENCE's: whole words, so the plural as well. |
| 1761 | `BuildCard.ORE_WORDS` | `{ "deposit", "ore", "forest", "field", "fields", "worked out" }` | ...ORE's: the ground has none - and since 0.7.138 Forestry's, the forest it may cut (none, or too little for E); since 0.7.142 a field of its type and a resource worked out, named as itself ("no co... |
| 1763 | `BuildCard.SUPPLY_WORDS` | `{ "fabricates", "will not sell" }` | ...SUPPLY's. |
| 1765 | `BuildCard.CREDIT_WORDS` | `{ "borrowing ban", "bank", "default point", "down payment", "lender", "mortgage" }` | ...CREDIT's. |
| 1767 | `BuildCard.MONEY_WORDS` | `{ "below cost", "no margin", "worth sinking", "worth building", "cost more than", "noth...` | ...MONEY's. |
| 1771 | `BuildCard.ENOUGH_WORDS` | `{ "ahead of", "already", "covers all", "months of work queued", "months of work on site...` | ...ENOUGH's. |

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
| 70 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 5052 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 5697 | `BuildingManager.DEATH_CARE_FIRST` | `"Death care first while the dead wait"` | The page's words for the rule, which Plan.line() opens with: what it does and when. |
| 5878 | `BuildingManager.BUILDERS_FIRST` | `"The builders' own depots first"` | The page's words for the rule, which Plan.line() opens with when a depot is on site. |
| 6926 | `BuildingManager.BASE_MATERIALS` | `36` | The public works yard: it produces this many units of materials a month on its own. |
| 8113 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BuildingVisual.java ([map](map/BuildingVisual.md))

| line | constant | value | says |
|---:|---|---|---|
| 72 | `BuildingVisual.HOME` | `0` | Homes: houses, and the flats drawn darker. |
| 74 | `BuildingVisual.SHOP` | `1` | Shops: convenience and grocery stores, the bank's branches, boutiques, diners. |
| 76 | `BuildingVisual.OFFICE` | `2` | Offices: business services. |
| 78 | `BuildingVisual.INDUSTRY` | `3` | Industry: bakeries to steel, construction and the car plants. |
| 80 | `BuildingVisual.FARM` | `4` | Farms: on open grass only. |
| 82 | `BuildingVisual.UTILITY` | `5` | Utilities: power and water. |
| 84 | `BuildingVisual.SCHOOL` | `6` | Schools, colleges and universities. |
| 86 | `BuildingVisual.HEALTH` | `7` | Health: clinics, hospitals, care and the cemeteries. |
| 88 | `BuildingVisual.SAFETY` | `8` | Safety: police and the jails. |
| 90 | `BuildingVisual.MINE` | `9` | Mines and wells: on their resource's sites. |
| 92 | `BuildingVisual.TRANSPORT` | `10` | Transport: the railway's yards, the sea terminals and the transit networks (0.8.1, batch FR0; spec-freight 4.2, D15). |
| 95 | `BuildingVisual.CLASSES` | `11` | How many classes: eleven - what the pyramid sums a node's buildings by (spec-land 2.5). |
| 98 | `BuildingVisual.CLASS_NAMES` | `{ "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities", "Schools", "Health", "...` | The classes' names, as the legend writes them (the mockup's). |
| 102 | `BuildingVisual.FILL` | `{ 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e, 0xffd0587a, 0...` | Each class's fill, 0xAARRGGBB - the mockup's TYPES. |
| 106 | `BuildingVisual.EDGE` | `{ 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d, 0xff86304a, 0...` | ...and its edge, drawn from 6 px a plot (the mockup's). |
| 110 | `BuildingVisual.FLATS_FILL` | `0xffd07a44` | Flats' fill: the mockup's FLATS, a darker orange than a house. |
| 113 | `BuildingVisual.FLATS_EDGE` | `0xff7e4119` | ...and their edge. |
| 116 | `BuildingVisual.CAMPUS_FILL` | `0xff4a3f5c` | The refinery's units (0.7.97, campus()): mockup 3's refinery, #4a3f5c, a darker violet than industry's, so the campus reads as one... |
| 119 | `BuildingVisual.CAMPUS_EDGE` | `0xff7a6a90` | ...edged in its #7a6a90. |
| 122 | `BuildingVisual.TERMINAL_FILL` | `0xff2e3640` | A sea terminal's apron (0.7.97, berth()): mockup 3's quays' grey, #2e3640... |
| 125 | `BuildingVisual.TERMINAL_EDGE` | `0xff4a5866` | ...edged in its #4a5866. |
| 128 | `BuildingVisual.TANKS_FILL` | `0xff2a3540` | A tank farm's tanks (0.7.97, the refiners' Tank Farm and the city's Strategic Reserve): mockup 3's tanks, #2a3540... |
| 131 | `BuildingVisual.TANKS_EDGE` | `0xff7f8ca6` | ...ringed in its #7f8ca6. |
| 136 | `BuildingVisual.PLAIN` | `0` | A type's look: as its class draws it, a box of its class's colours (every type but the materials'). |
| 138 | `BuildingVisual.PIT` | `1` | ...an open pit on its sites: benches stepping down to its floor, in its resource's colour (the iron mines, the surface coal mines, the sulphide copper mines). |
| 140 | `BuildingVisual.SHAFT` | `2` | ...an underground mine on its site: the site's yard and spoil, its headframe in the middle (the coking coal's mine). |
| 142 | `BuildingVisual.QUARRY` | `3` | ...a quarry on its sites: straight benches cut in the stone (the three quarries). |
| 144 | `BuildingVisual.LEACH` | `4` | ...a leach plant on its site: its heaps in rows and its pond (the oxide copper's Leach & SX-EW Plant). |
| 146 | `BuildingVisual.WASH` | `5` | ...a coal wash plant: a dark works with its conveyor (the Coal Preparation Plant). |
| 148 | `BuildingVisual.CAMP` | `6` | ...a logging camp: a clearing and its log stacks (Forestry's crews). |
| 150 | `BuildingVisual.STAND` | `7` | ...planted forest: trees in rows on its ground (the Tree Plantation and the Woodlot). |
| 152 | `BuildingVisual.WORKS` | `8` | ...a processing works: its industry's own colours (GROUP_FILL), placed with the rest of its industry's as one campus. |
| 155 | `BuildingVisual.LOOKS` | `9` | How many looks. |
| 158 | `BuildingVisual.LOOK_NAMES` | `{ "", "Open pit", "Underground mine", "Quarry", "Leach plant", "Coal wash plant", "Logg...` | Each look as the legend names it (PLAIN and WORKS by their class and their industry instead). |
| 162 | `BuildingVisual.LOOK_FILL` | `{ 0, 0xff6e5a4a, 0xffa39783, 0xffcfc8b4, 0xff5fae93, 0xff3a3a42, 0xffb58c5c, 0xff3f6a36...` | Each look's fill, 0xAARRGGBB: a pit's and a quarry's from their resource's colour (pitColours()), the rest their own - a shaft's yard, the heaps' green, the wash plant's coal, the camp's sawdust, t... |
| 165 | `BuildingVisual.LOOK_EDGE` | `{ 0, 0xff3d3129, 0xff5c5347, 0xff8d8673, 0xff2f6f5b, 0xff17171b, 0xff6d4c2a, 0xff2c4d26...` | ...and its edge from 6 px a plot. |
| 168 | `BuildingVisual.NO_GROUP` | `0` | A works' campus (GROUPS): none... |
| 170 | `BuildingVisual.REFINERY` | `1` | ...the refinery's units (0.7.97, campus())... |
| 172 | `BuildingVisual.STEEL` | `2` | ...Iron & Steel's works (sectors.HeavyIndustry: the arc furnaces and mini-mills, the Scrap Yard, the integrated route)... |
| 174 | `BuildingVisual.CEMENT` | `3` | ...Cement & Concrete's kilns and cement plants... |
| 176 | `BuildingVisual.COPPER_WORKS` | `4` | ...Copper Refining's smelters, refineries and rod mills... |
| 178 | `BuildingVisual.WOOD` | `5` | ...and Wood Products' sawmills and mills. |
| 181 | `BuildingVisual.GROUPS` | `6` | How many groups, NO_GROUP among them. |
| 184 | `BuildingVisual.GROUP_SECTORS` | `{ null, null, Sectors.HEAVY_INDUSTRY, Sectors.CEMENT_CONCRETE, Sectors.COPPER_REFINING,...` | The sector each processing works' group is (Sectors' keys), by group; none for NO_GROUP and the refinery's, which campus() reads. |
| 188 | `BuildingVisual.GROUP_NAMES` | `{ "", "Refinery", "Iron & steel works", "Cement works", "Copper works", "Wood mills" }` | Each group's works as the legend names them. |
| 191 | `BuildingVisual.GROUP_FILL` | `{ 0, CAMPUS_FILL, 0xff6b4a42, 0xffb4b0a6, 0xffb0663a, 0xff8a6a44 }` | Each group's fill: the refinery's CAMPUS_FILL, steel's rust, cement's grey, copper's own and the mills' timber. |
| 194 | `BuildingVisual.GROUP_EDGE` | `{ 0, CAMPUS_EDGE, 0xffa0705c, 0xff76726a, 0xff6e3b1d, 0xff5a4128 }` | ...and its edge from 6 px a plot. |
| 199 | `BuildingVisual.NOT_A_ROAD` | `0` | Not a road. |
| 201 | `BuildingVisual.GRAVEL` | `1` | A gravel road: no freight grade. |
| 203 | `BuildingVisual.PAVED` | `2` | A paved road: a freight grade under one. |
| 205 | `BuildingVisual.HIGHWAY` | `3` | An elevated highway: a freight grade of one. |
| 208 | `BuildingVisual.DRAWN_AS_HOMES` | `{ 22 }` | The permanent ids of the care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Care Service (22) - still drawn, one for one, on its own land (0.7.64). |
| 211 | `BuildingVisual.RAIL_TERMINALS` | `{ 64, 141 }` | The permanent ids of the rail types drawn as a yard beside the track, not as track (0.7.72): the Rail Terminal (64), and since FR7 the Rail Depot (141), the small goods yard a railway starts with. |
| 214 | `BuildingVisual.RAIL_DEPOT` | `141` | The permanent id of the Rail Depot (FR7 slice 3): the small yard of RAIL_TERMINALS, drawn at its own footprint with a few sidings, not a Rail Terminal's fan of eight. |
| 222 | `BuildingVisual.FREIGHT_LINES` | `{ 63 }` | The permanent ids of the railway lines whose track makes the main line double (0.8.1, batch FR0; spec-freight 4.3): the Freight Line (63). |
| 232 | `BuildingVisual.NOT_GATHERED` | `{ 11, 12 }` | The permanent ids of the works drawn in their group's colours but placed as any industry, not gathered into its campus (0.8.0, batch I14): Iron & Steel's Electric Arc Furnace (11) and Steel Mini-Mi... |
| 235 | `BuildingVisual.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. |
| 243 | `BuildingVisual.BERTH_PLOTS_MOST` | `2_368_060 / SQ_FT_PER_PLOT` | The most plots a sea terminal's box is drawn on (SL1, 2026-10-28): 2,368,060 sq ft over a plot's square feet, about 244 - the Bulk Terminal's ground before SL1 made each terminal a large modern rea... |
| 403 | `BuildingVisual.PIT_DUST` | `0xffb8a58a` | The dust a pit's benches are blended toward: dry spoil, #b8a58a. |
| 406 | `BuildingVisual.PIT_SHADE` | `0xff2a2620` | ...and the shade its floor is: #2a2620. |
| 511 | `BuildingVisual.ORDERS` | `java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>())` |  |

### BuildingsTemplate.java ([map](map/BuildingsTemplate.md))

| line | constant | value | says |
|---:|---|---|---|
| 1063 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 1066 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 1118 | `BusinessDebtManager.DEBT_KINDS` | `{ "Bank loans", "Bonds", "Mortgages", "Interim financing" }` | The four kinds of debt, in the order the statements print them. |
| 1136 | `BusinessDebtManager.DUE_HORIZONS` | `{ 12, 60 }` | The two horizons R2 reads, in settles: a year and five. |
| 1139 | `BusinessDebtManager.OWED` | `0, WITHIN_YEAR = 1, WITHIN_FIVE = 2, RATE = 3, RUNS_TO = 4` | debtByKind()'s rows: what is owed, what falls due within a year and within five, the rate it pays a year weighted by what is owed, and the month the last of it falls due (0 with none) - the last tw... |
| 1211 | `BusinessDebtManager.BORROWED` | `0, REPAID = 1, WRITTEN_OFF = 2` | debtMovedByKind()'s rows: borrowed (the principal written, a bond's face), repaid, written off. |
| 2066 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 2068 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 2070 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 2072 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 2074 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 2076 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2570 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 3158 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 3160 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3581 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BusinessInvestment.java ([map](map/BusinessInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 52 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 55 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 58 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 61 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 64 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site - every sector but the landlords, who hold work by the month (0.7.17; withinMonthsOfWork()); the refiners count no crude unit on s... |
| 113 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, in months of the builders' work. |
| 116 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 498 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 501 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 504 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 515 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |
| 1146 | `BusinessInvestment.REAL_HURDLE_FLOOR` | `.25` | The least of the rate a project is tested against, however much inflation its owners expect: a quarter of it. |

### Buyback.java ([map](map/Buyback.md))

| line | constant | value | says |
|---:|---|---|---|
| 47 | `Buyback.SLICES` | `10` | How many equal slices each holder cuts its holding into: the more there are, the finer the ladder of prices the holder's rule draws (D12). |

### CallTree.java ([map](map/CallTree.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `CallTree.RATE_VOL_FLOOR` | `0.0075` | The least volatility of the short rate the tree is priced at, a year (D23): 0.75 points, so a calm city that has hardly moved its rate still pays for the option. |
| 48 | `CallTree.VOL_MONTHS` | `60` | How many months of the city's own rate the volatility is measured over (D23): five years of monthly changes. |
| 51 | `CallTree.VOL_MIN_CHANGES` | `12` | The fewest monthly changes the volatility is measured from: under a year's worth (13 months of the rate) the city has no history worth the name and prices at the floor. |
| 54 | `CallTree.FIRST_CALL_10Y` | `5` | A callable 10-year may first be called this many years after it is sold (D22). |
| 57 | `CallTree.FIRST_CALL_LONG` | `10` | ...and a callable 20-, 30- or 50-year this many (D22). |
| 60 | `CallTree.CALLABLE_MIN_YEARS` | `10` | The shortest term a bond may be sold callable at, in years (D22, Jerus: "any bond of ten years or more"). |
| 63 | `CallTree.CALL_PRICE` | `100` | What a call pays, per 100 of the principal called: par (D22). |
| 66 | `CallTree.WORTH_CALLING` | `0.03` | A call is worth making when the present value it saves passes this share of the face (D24): 3%, the threshold US municipal refunding practice uses. |
| 69 | `CallTree.REFUSAL` | `"Only bonds of 10 years or more can be sold callable, at 10, 20, 30 or 50 years."` | What the treasury is told when it asks for a callable bond that is not one of the terms that may be. |
| 72 | `CallTree.COUPON_TOLERANCE` | `1e-12` | The bisection finds a coupon to a hundred-millionth of a point: far under the eighth of a point a coupon is rounded to. |
| 75 | `CallTree.MAX_FIT_STEPS` | `80` | The most Newton steps the fit of one theta takes before it settles for the bisection's bracket. |

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
| 318 | `CapitalFlows.CARRY_FULL_SPREAD` | `.02` | At this spread or better, the world wants all the spare book there is. |
| 321 | `CapitalFlows.CARRY_MAX_SHARE` | `.90` | ...and never quite all of it, because a bank at its limit lends to nobody. |
| 324 | `CapitalFlows.CARRY_BORROW_SPEED` | `.06` | How fast the book fills, and empties. |
| 325 | `CapitalFlows.CARRY_REPAY_SPEED` | `.20` |  |

### CareType.java ([map](map/CareType.md))

| line | constant | value | says |
|---:|---|---|---|
| 143 | `CareType.SENIOR_NEED_AGAINST_AN_ELDER` | `.19` | 5.5% of 70-84 in care against 29.6% of the over-85s - see placesPerHead. |
| 146 | `CareType.CHILDCARE_UNDER_SIX` | `.32` | A baby's place in childcare: the 32% of Canada's children aged 0 to 5 in centre-based child care in 2025 (Statistics Canada, Child care arrangements, 2025, The Daily 2025-10-21). |
| 149 | `CareType.CHILDCARE_SIX_TO_TWELVE` | `.35 *.52` | A child's place: 35% of school-attending 6-to-12s in child care, 52% of them in before- or after-school programmes (Statistics Canada, The Daily 2026-03-12), so 0.35 x 0.52 = 18.2% - derived here, ... |

### CentralBank.java ([map](map/CentralBank.md))

| line | constant | value | says |
|---:|---|---|---|
| 105 | `CentralBank.WINDOW_PENALTY` | `.0025` | What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so bo... |
| 108 | `CentralBank.DEFAULT_ADVANCES_MONTHS` | `6` | The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past ... |
| 111 | `CentralBank.MAX_ADVANCES_CEILING` | `36` | The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. |
| 114 | `CentralBank.REVENUE_MONTHS` | `12` | How many months of the treasury's revenue the ceiling is averaged over. |
| 117 | `CentralBank.MAX_QE_SHARE` | `1.0` | The most of the city's term paper the holdings dial may aim at: all of it since 0.7.15, half before - Jerus: "central bank bond holding can ve 100% if one wants", a backstop for when the bank and i... |
| 120 | `CentralBank.FULL_COMPRESSION_SHARE` | `.5` | The holding at which the term premium is wholly compressed: half the term paper, where it has been since 0.7.1 - MAX_QE_SHARE's value until the dial went past it (DebtManager.compression()). |
| 123 | `CentralBank.QE_SPEED` | `.25` | The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying towar... |
| 126 | `CentralBank.QE_COMPRESSION` | `1.0` | How much of the term premium a holding of FULL_COMPRESSION_SHARE takes away: all of it, at 1. |

### ChartModel.java ([map](map/ChartModel.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `ChartModel.MIN_SPAN` | `6` | The fewest months the main chart can be zoomed down to: half a year, seven points. |
| 43 | `ChartModel.RANGES` | `{ 12, 60, 120, 600 }` | The range buttons, in months: one, five, ten and fifty years; "All" is the whole history. |
| 46 | `ChartModel.RANGE_NAMES` | `{ "1Y", "5Y", "10Y", "50Y", "All" }` | What the range buttons say, in RANGES' order, then the whole history's. |
| 49 | `ChartModel.ALL` | `Integer.MAX_VALUE` | The range that means the whole history. |
| 52 | `ChartModel.DEFAULT_RANGE` | `120` | The range a chart opens on: ten years, what City History drew before it had buttons. |
| 55 | `ChartModel.ZOOM_STEP` | `0.85` | What one notch of the wheel leaves in view, zooming in; zooming out is its inverse. |
| 58 | `ChartModel.YEAR_LABEL_PX` | `46` | The least room, in pixels, between two year labels: a "2141" and a gap. |
| 61 | `ChartModel.MONTH_LABEL_PX` | `36` | The least room between two month labels: a "Mar" and a gap. |
| 64 | `ChartModel.YEAR_STEPS` | `{ 1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000 }` | The year steps the axis may label, smallest first: every year, every second, every fifth... |
| 67 | `ChartModel.MONTH_STEPS` | `{ 1, 2, 3 }` | The month steps a zoomed-in axis may label between its years: monthly, two-monthly, quarterly - no coarser, or ten years across a wide screen would be half-years. |
| 70 | `ChartModel.AXIS_PAD` | `.06` | Headroom a value axis leaves above and below what is drawn, as a share of the range: a line at its extreme is not drawn along the frame. |

### CityCalendar.java ([map](map/CityCalendar.md))

| line | constant | value | says |
|---:|---|---|---|
| 28 | `CityCalendar.EPOCH_YEAR` | `2000` | The year month 1 falls in. |
| 30 | `CityCalendar.MONTHS` | `{ "January", "February", "March", "April", "May", "June", "July", "August", "September"...` |  |
| 35 | `CityCalendar.SHORT_MONTHS` | `{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }` |  |
| 41 | `CityCalendar.DAYS` | `{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }` | Days in each month of a common year; February is corrected below. |

### CityLand.java ([map](map/CityLand.md))

| line | constant | value | says |
|---:|---|---|---|
| 77 | `CityLand.SIDES` | `LegacyLand.SIDES` | Sides of the city, seen from the founding site: north, east, south and west, in that order - LegacyLand.SIDES (spec-land star 1). |
| 80 | `CityLand.SIDE_NAMES` | `LegacyLand.SIDE_NAMES` | The sides' names, in their order: LegacyLand's. |
| 83 | `CityLand.TOTAL` | `0` | Where a record keeps its whole area, in square kilometres - its ice (0.7.127) counted here and nowhere else. |
| 86 | `CityLand.DRY` | `1` | ...its dry ground: what buildings stand on, and what the city's square feet count (World.dry(): the desert's too, since 0.7.127). |
| 89 | `CityLand.FRESH` | `2` | ...its fresh water: lakes and the founding river. |
| 92 | `CityLand.SEA` | `3` | ...its sea. |
| 95 | `CityLand.FOREST` | `4` | ...and its forest, which is dry ground too. |
| 98 | `CityLand.AREAS` | `5` | How many areas a record keeps: total, dry, fresh, sea and forest. |
| 101 | `CityLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 107 | `CityLand.RELIEF_FLAT` | `World.FLAT` | Where a record keeps its dry ground by relief, in square kilometres (ET1, spec-earth2-terrain 3.3), beside the five areas, which stay as they are: its flat ground (World.FLAT; all of a generated wo... |
| 110 | `CityLand.RELIEF_FOOTHILL` | `World.FOOTHILL` | ...its foothill (World.FOOTHILL)... |
| 113 | `CityLand.RELIEF_STEEP` | `World.STEEP` | ...its steep ground (World.STEEP)... |
| 116 | `CityLand.RELIEF_VERY_STEEP` | `World.VERY_STEEP` | ...its very steep (World.VERY_STEEP): these four add up to the DRY area, plot for plot... |
| 119 | `CityLand.RELIEF_SHALLOW` | `World.RELIEFS` | ...and its shallow sea (World.SHALLOW), a part of its SEA. |
| 122 | `CityLand.RELIEF_AREAS` | `World.RELIEFS + 1` | How many figures a relief record keeps: flat, foothill, steep, very steep and shallow sea, 5 - the width of landCentreRelief and of a row of landHoldingsRelief and landOffersRelief. |
| 125 | `CityLand.CENTRE` | `0` | The centre's holding on the grid: 0; the k-th purchase is holding k. |
| 219 | `CityLand.FRESH_REACH` | `(long) Math.ceil(World.RIVER_LAKE_NEAR + World.RIVER_LAKE_SPAN + World.LAKE_R_MIN + Wor...` | How far the founding takes blocks for its fresh water, in plots from the site, L-infinity: the founding lake's far shore at the most (World.RIVER_LAKE_NEAR + RIVER_LAKE_SPAN + LAKE_R_MIN + LAKE_R_S... |
| 567 | `CityLand.NEW_FIELDS_FORMAT` | `36` | The first save format whose land says which holdings hold the old world's fields (oldWorldHoldings()): 36. |
| 921 | `CityLand.CELLS_KEPT` | `256` | How many cells' fields are kept: 256 - the nine round a site for every resource, and the cells a large holding reaches. |
| 923 | `CityLand.CELLS` | `new LinkedHashMap<>(64, 0.75f, true) { @ Override protected boolean removeEldestEntry(M...` |  |
| 988 | `CityLand.TILE_COUNTS_KEPT` | `65_536` | Tiles' counts kept, by world and tile: 65,536 (a few megabytes) - the whole tiles an offer's count reads, which the next city on the same world's ground, or the same city drawn again, reads again. |
| 991 | `CityLand.PARALLEL_TILES` | `2_048` | A rectangle of this many tiles or more is counted over the machine's cores: 2,048 (about an eighth of a second's reading on one core). |
| 995 | `CityLand.TILE_COUNTS` | `new LinkedHashMap<>(1024, 0.75f, true) { @ Override protected boolean removeEldestEntry...` |  |
| 1005 | `CityLand.WIDE` | `World.CLASSES + RELIEF_AREAS` | How wide a count of plots is (ET1): World.CLASSES plots by class, then RELIEF_AREAS more - the dry plots by relief (World.dry() classes, FLAT to VERY_STEEP) and the shallow sea's (SHALLOW), counted... |
| 1148 | `CityLand.CENTRE_FIELDS` | `AREAS + 2 * KINDS + 3 + 2` | How wide the centre's record is: 24 (spec-grid 3, M3; 25 with a half-side before, format 31). |
| 1151 | `CityLand.RECT_FIELDS` | `4` | How wide a centre rectangle's record is: x0, y0, x1, y1 - its plots, half-open. |

### CityMap.java ([map](map/CityMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 160 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 163 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 166 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 169 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 172 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 175 | `CityMap.PLAN_FILL` | `0.68` | The share of a district's owned plots its buildings' boxes may take: 0.68 (CL3), a full plan's boxes over its ground on Jerus's densest districts (the CL1 study, section 5.3) - so a district is ful... |
| 178 | `CityMap.NO_LOG` | `new int [ 0 ]` | A district's empty log: the one array every district without records shares. |
| 181 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL, Resource.COAL, Resource.STONE, Resource.COPPER }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5); since 0.8.0 (batch I14) coal, stone and copper after them, every resource a works on ... |
| 184 | `CityMap.SLOTS` |  | The site slots (0.8.0, batch I14): each SITED resource's types of field in Resource.typesOf()'s order - iron's hematite and magnetite, oil's one, coal's thermal and coking, stone's limestone and ha... |
| 187 | `CityMap.SLOT_RESOURCE` |  | ...each slot's resource... |
| 190 | `CityMap.SLOT_TYPE` |  | ...and its type of field. |
| 202 | `CityMap.OLD_SITED` | `2` | The sites a district the sidecar wrote before FORMAT 7: iron's and oil's, SITED's until 0.8.0. |
| 205 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 208 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 211 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 214 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 217 | `CityMap.FORMAT` | `13` | The sidecar's format: 13 since CC1-M, each arm of the runs followed by what last stopped it (CityRuns.Arm.stoppedBy: OPEN, EDGE or SEA), which the first re-lay after a load reads to know how far th... |
| 220 | `CityMap.OLDEST_READ` | `4` | The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. |
| 223 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 763 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1355 | `CityMap.CAMPUS_REACH` | `64` | The most districts a works group's units may stand across, in the map's order, and still have a campus a new one goes to first: 64 (0.8.0) - past it, a city whose works of a group stand in every di... |
| 1812 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.dy).thenCom...` | Nearest the founding site first, then north to south, west to east. |
| 2220 | `CityMap.PLANS_KEPT` | `64` | How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan wa... |
| 2223 | `CityMap.PACKED_BIT` | `1<<20` | A packed box's flag (Drawn.boxes): drawn without a street, R7's packing at the city's edge. |
| 2226 | `CityMap.SIDING_END_BIT` | `1<<24` | A siding plot of a Drawn (Drawn.siding) whose bit this is: the siding's first, beside the works' box, where its loading shed stands (batch FR7 slice 5). |
| 2229 | `CityMap.AVENUE_MARK_SHIFT` | `17` | Where a Drawn avenue plot's mark starts (Drawn.avenue): above the frame's plot, which is under 2^17 (a frame has 66,049 of them). |
| 2232 | `CityMap.CURVE_BAND_BIT` | `1<<20` | A band plot of a DrawnCurve (DrawnCurve.plots) whose bit this is: a plot the curve's band reaches that is not its own. |
| 2279 | `CityMap.BOX_TYPE_SHIFT` | `21` | Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). |
| 2592 | `CityMap.M_WEST` | `0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4` | A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, ... |
| 2613 | `CityMap.AROUND` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 }, { - 1, - 1 }, { 1, - 1 }, { 1, 1 }, { - 1...` | The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). |
| 2616 | `CityMap.CORNER_DIRS` | `{ { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } }` | Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. |
| 2756 | `CityMap.CHAIN_BAND` | `128` | THE CHAIN'S BANDS (0.7.89, batch RD3). |
| 2759 | `CityMap.BANDS_KEPT` | `16` | The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. |
| 3447 | `CityMap.INTERIOR_MOST` | `World.TILE - 1` | A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. |
| 3503 | `CityMap.PICTURES_KEPT` | `128` | How many districts' pictures are kept, the last used: 128 (star MM1-2) - a far view of up to 128 built districts (Jerus's city has 93) in DistrictPicture.BYTES each, 0.14 MB, within the view's 48 M... |
| 3694 | `CityMap.WORLD_GAP` | `2 * CityRuns.LOOK` | How far ahead of a track's end the city's edge may lie for its line to the world to start there, in plots: 12, twice CityRuns.LOOK - an arm waits LOOK short of the edge, a 45-degree arm sooner (FX2). |
| 3697 | `CityMap.WORLD_REACH` | `4 * DISTRICT` | How far the world line is drawn at most, in plots: 1,024 (30.7 km), four districts; it stops sooner at the first plot that is not dry ground (FX2). |
| 3700 | `CityMap.WORLD_STEP` | `0.25` | How finely a way out is read, in plots: a quarter, as MapCheck reads the trains' routes (FX2). |
| 4266 | `CityMap.TRUCK_ROUTE_LEAST` | `4` | The least a highway route runs along the highways, in plots, past the world line's own: shorter is the arm the line leaves from, where the trucks have no road to drive. |
| 4269 | `CityMap.TRUCK_ROUTES_MOST` | `40` | The most highway routes kept, 40: one to each ramp, the arms' order, a city of many arms is cut here (a route is a lane of trucks, and the legend would not read more). |
| 4272 | `CityMap.TRUCK_ACCESS_FADE` | `3` | How far an access road's trucks fade in off the street grid, in plots: 3. |
| 4450 | `CityMap.TRIP_REACH` | `DISTRICT` | The plots about a district's frame, on every side, its trips look for a ramp over: DISTRICT (256), the three districts across about it. |
| 4453 | `CityMap.TRIP_STREET_COST` | `3` | What a truck pays to cross a street plot that is not an arterial's, boulevard's or access road's (those cost 1) in the trips' street search: 3, so a truck takes the arterials unless the other way i... |
| 4456 | `CityMap.TRIP_BOX` | `DistrictPlan.FRAME + 2 * TRIP_REACH` | The side of the box a district's street search runs in, in plots: its frame grown by TRIP_REACH on every side. |
| 4462 | `CityMap.NO_PLOT` | `Long.MIN_VALUE` |  |
| 4894 | `CityMap.RUN_LINES_KEPT` | `4096` | Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. |
| 4897 | `CityMap.RUN_TILES_KEPT` | `256` | ...whole tiles, for a 45-degree way: 256. |
| 5094 | `CityMap.PIECE_CHUNK_SHIFT` | `6` | Tiles a side of a chunk of the pieces' tables (CL5): 2^6 = 64, a table of 4,096 per chunk, and a district (8 tiles) is never split by one. |
| 5096 | `CityMap.PIECE_CHUNK_MASK` | `(1L<<PIECE_CHUNK_SHIFT) - 1` |  |
| 5099 | `CityMap.PIECE_SAMPLE_AT` | `8, PIECE_SAMPLE_EVERY = 16` | Where in a tile its four samples lie (CL5; IslandProbe's tile rule): the first 8 plots in and each of the others 16 further, the centres of its four quarters. |
| 5102 | `CityMap.PIECE_HOME_REACH` | `8` | How many tiles from the founding site's tile the home piece is looked for, ring by ring, when its own tile is not land (CL5). |
| 5557 | `CityMap.WAYS_WORKS_DISTRICTS` | `1000` | Up to this many districts the map weighs its mined sites each month for the railway's sidings and further yards: 1,000 (Jerus's city has 93). |
| 6202 | `CityMap.ANY_LANDMASS` | `Integer.MIN_VALUE` | ET5: no landmass asked for: the search runs as it always did, over the ground the runs can use. |
| 6302 | `CityMap.SHUT_REACH` | `SeaRoutes.OPEN_M / World.PLOT_M / SeaRoutes.CELL` | The flood's reach in cells (SeaRoutes.opensToSea()): a water whose cells all lie within it of one another opens to nothing, whichever of them a flood starts from. |
| 6745 | `CityMap.MASK_TILES_KEPT` | `1<<16` | The tiles whose masks are kept: 65,536 (a tile's mask is a reference, and a boolean array only for a tile holding masked ground). |
| 6876 | `CityMap.AtSea.NONE` | `new AtSea(List.of(), List.of())` | None. |
| 6966 | `CityMap.BridgeRun.ROAD` | `0` | A street-sized deck that belongs to no net... |
| 6968 | `CityMap.BridgeRun.HIGHWAY` | `1` | ...a highway's, which joins the highways... |
| 6970 | `CityMap.BridgeRun.RAIL` | `2` | ...and the railway's. |
| 6992 | `CityMap.ROAD_BRIDGE_STREET` | `(byte)(DistrictPlan.PAVED \|(1<<TilePainter.S_WIDTH_SHIFT))` | A road bridge's plot as a plan's street code: paved, half a plot wide (a street's surface, so that its deck shows beside it), a cell's street, no bridge bit - the painter reads the water under it. |
| 8158 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned buildable plots. |
| 8161 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 8164 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned buildable plots. |

### CityNeeds.java ([map](map/CityNeeds.md))

| line | constant | value | says |
|---:|---|---|---|
| 151 | `CityNeeds.PLAIN` | `new Words() { @ Override public String people(double count) { return Formats.INSTANCE.c...` | The same four shapes without the toolkit (Formats), for a harness and a log. |
| 173 | `CityNeeds.NETWORK_YELLOW` | `.75, NETWORK_RED = 1` | A network is listed from three-quarters of its capacity, red once it is over. |
| 183 | `CityNeeds.HIGHWAY_MIN_FILL` | `.60` | THE HIGHWAYS ARE LISTED (FR7, 2026-10-18) once the through trips with no highway to take them would fill this share of one more highway at free flow: a highway is not built to stand idle, the railw... |
| 186 | `CityNeeds.HIGHWAY_RED_FILL` | `1.0` | ...and red once those trips would fill a whole highway: a highway's worth of through traffic is on the streets. |
| 189 | `CityNeeds.GENERAL_YELLOW` | `.80, GENERAL_RED =.50` | General care is watched hardest: it moves the sick rate. |
| 192 | `CityNeeds.OTHER_CARE_YELLOW` | `.70, OTHER_CARE_RED =.40` | Childcare and senior care kill at the ends of life; a young city legitimately has neither for a while. |
| 195 | `CityNeeds.PLOTS_WATCHED` | `120` | Burial plots are watched once fewer than this many months are left. |
| 197 | `CityNeeds.PLOTS_YELLOW` | `24, PLOTS_RED = 6` | ...listed under two years of plots, red under six months. |
| 200 | `CityNeeds.SCHOOLS_YELLOW` | `.90, SCHOOLS_RED =.60` | The basic ladder's bottleneck, its coverage (read as served since 0.7.41; "taught" until then). |
| 203 | `CityNeeds.SEATS_YELLOW` | `1.05, SEATS_RED = 2` | A school above the ladder: who would come and be hired (wanted(), 0.7.51; who would come until then) over its seats. |
| 206 | `CityNeeds.SEATS_FLOOR` | `25` | A class's worth: fewer students than this the city would get and hire (would-be students until 0.7.51) and a school is not a row (measured: 3 would-be law students in a city of 1,650). |
| 209 | `CityNeeds.CRIME_YELLOW` | `1.2, CRIME_RED = 1.5` | Crime against Canada's rate. |
| 212 | `CityNeeds.CELLS_YELLOW` | `1, CELLS_RED = 25` | The caught, not held. |
| 220 | `CityNeeds.SICK_YELLOW` | `.06, SICK_RED =.12` | The sick rate, the share of the workforce off sick (0.7.28): the left panel's OFF SICK lines (SummaryScreen's literals until then), here so the Services screen colours the same figure by the same l... |
| 229 | `CityNeeds.FALLS_DUE_MONTHS` | `3` | FALLS DUE (0.7.24): paper due within this many months is red, as the bottom strip drew its maturity chip until it left the frame (its maturity chip, FinancesScreen's until 0.7.32 and its urgency() ... |
| 237 | `CityNeeds.FALLS_DUE_SOON_MONTHS` | `12` | ...and paper due within this many months is amber: the strip's other rule (gap <= 12), named in 0.7.32 so the Finances tab's NEXT DUE, its book and its strip colour a maturity by one line. |
| 247 | `CityNeeds.SERVICE_FELT` | `.12, SERVICE_CONSTRAINED =.25` | DEBT SERVICE, A SHARE OF THE TAKE (0.7.32): what the city pays its lenders against what it collects is comfortable under SERVICE_FELT, felt from there, and constrained past SERVICE_CONSTRAINED - th... |
| 257 | `CityNeeds.YEAR_WALL` | `.5` | A WALL ON THE LADDER (0.7.32): a calendar year whose payments - coupons and principal - pass this share of a year of revenue is one a city meets by refinancing before it arrives. |
| 291 | `CityNeeds.SERVED` | `"served", SHORT = "short", TIGHT = "tight", ENOUGH = "enough"` | The gauges' words (0.7.41), the same on every screen: what the figure is, and its verdict's three words. |
| 315 | `CityNeeds.SERVED_AS_MULTIPLE` | `5` | From this served share up (5 = five times what is asked) a share prints as a multiple, "30×", not a per cent: in a town of 40 a power plant read 13333% served (PT1). |
| 635 | `CityNeeds.GROUND_YELLOW` | `LandManager.BLOCK_SQ_FT` | Free ground under which NEEDS YOU lists the GROUND row: a block, 100,000 sq ft. |
| 638 | `CityNeeds.TRUST_RED` | `.5` | Trust in the central bank under which a fall is red in the PRICES row: half - under it, what people expect is more recent prices than the bank's target. |
| 973 | `CityNeeds.FIRST_SCHOOL_SHARE` | `.5` | A first school above the ladder is listed once the students it would get and hire fill this share of the smallest that teaches it - the firms' first-plant share (Materials.FIRST_PLANT_UTILISATION, ... |

### CityRuns.java ([map](map/CityRuns.md))

| line | constant | value | says |
|---:|---|---|---|
| 147 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 150 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 153 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 156 | `CityRuns.GATEWAY_UNKNOWN` | `- 2` | A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). |
| 159 | `CityRuns.NO_GATEWAY` | `- 1` | A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). |
| 162 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 165 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 168 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 171 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 174 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 177 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 180 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 183 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7); and (FX2) where each arm of the spine first crosses one. |
| 186 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 189 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 192 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 195 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 198 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 200 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 202 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 204 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 206 | `CityRuns.F_RESERVE` | `8` | ...and (batch FR7, D19) an interchange's reserve, a plan's ground only (fill() with yardsOver; InterchangeGround): street ground the plan keeps clear of lots, buildings and streets (DistrictPlan.FI... |
| 209 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 211 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 213 | `CityRuns.M_DIAG_SHIFT` | `2` | ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... |
| 215 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 254 | `CityRuns.ONE_PIECE` | `new Pieces() { @ Override public int count() { return 1; } @ Override public int pieceO...` | The city as one piece (what a caller with no pieces passes): the founding site's net alone, as before CL5. |
| 262 | `CityRuns.SPINE` | `0` | A corridor's role (CL5), in its fourth entry: a piece's SPINE, the row and the column through its hub (a home net's corridors 0 and 1)... |
| 264 | `CityRuns.CROSSING` | `1` | ...a CROSSING, a line one district out that crosses the net already laid (newCorridor(); and the railway's placeholder corridors, which run along no line of their own)... |
| 266 | `CityRuns.LINK` | `2` | ...or a LINK, a line opened by a link arm from the net's plot nearest it, where no line crosses the net (openLink()). |
| 280 | `CityRuns.LINK_SEARCH` | `1<<21` | The most plots a link's search visits (CL5, linkWay()): 1 << 21, about 2 million, the net's own plots among them - its visited table holds up to twice that many slots, an index of 4 bytes each, and... |
| 703 | `CityRuns.RESERVES_KEPT` | `8192` | The most regions' sites kept at once: 8,192 - a screen's tiles and the plans' frames, many times over. |
| 962 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and bridgeable water wider than the net bridges, or with deep sea in it). |
| 1100 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |
| 1531 | `CityRuns.LinkSearch.SOURCE` | `9` | The move of a plot the search began from; the others hold 1 + the heading that found them. |
| 1533 | `CityRuns.LinkSearch.ORDER` | `{ 0, 2, 4, 6, 1, 3, 5, 7 }` | The order a plot's neighbours are tried in: straight ways, then the 45-degree ones. |
| 2104 | `CityRuns.YARD_LONG` | `(int) Math.ceil(2 * TrainSchedule.LOCO_PLOTS + Math.ceil(TrainSchedule.UNIT_TONNES / Tr...` | How long a yard is laid along its line (FX5 Y1; Jerus, 2026-10-20: "a long train will be out of the depot", "the rail depots would have to be redesigned to accomdoate such large trains"): a unit tr... |
| 2114 | `CityRuns.TIER_LENGTHS` | `tierLengths()` | The lengths, longest first, a yard of footprint {w, h} is laid at along a row (w along) or a column (h along): each kind of train's length rounded up and its two ladders - a unit train's YARD_LONG ... |
| 2117 | `CityRuns.TIERS` | `TIER_LENGTHS.length + 1` | ...how many: one for each length and one for the footprint's own. |
| 2415 | `CityRuns.BRANCH` | `2, SIDING = 3` | The corridors of the arms laid for a train's way: a branch to a further yard (2) and a siding into a works (3). |
| 2418 | `CityRuns.SIDING_REACH` | `CityMap.DISTRICT` | A siding serves a works within this of the main line, in plots: one district (CityMap.DISTRICT, 256; spec-freight 4.3, "the largest BULK works within one district of the line"). |
| 2421 | `CityRuns.MAIN_LINE_REACH` | `1L<<20` | How far past the plots it has the main line of a city with a station is grown, at most, in plots: 1,048,576 (2^20) - some four thousand districts, past any city's ground, so the line waits at the c... |
| 2424 | `CityRuns.SERVED_WITHIN` | `1` | A works the main line touches or runs beside, within this many plots, has the railway at its door and needs no siding: 1. |
| 2427 | `CityRuns.GROUP_LINK` | `CityMap.DISTRICT / 2` | A group of rail-served works is the works whose box middles lie within this of each other, link by link: half a district, 128 plots. |
| 2430 | `CityRuns.YARD_APART` | `CityMap.DISTRICT` | A further yard goes beside a group of works that lies more than this from every yard laid: a district, 256 plots (FR7: "more than one district from every yard laid"). |
| 2433 | `CityRuns.BRANCH_FROM` | `CityMap.DISTRICT` | A group of works farther than this from the track is reached by a branch off the main line, else the yard sits on the track nearest it: a district, 256 plots. |
| 2705 | `CityRuns.BOX_GRID` | `64` | The boxes' grid: 64 plots a side. |
| 2938 | `CityRuns.SIDING_TRIES` | `200` | The most works laySidings() tries in one run: 200. |
| 3905 | `CityRuns.BRIDGE_ROAD` | `CityMap.BridgeRun.ROAD, BRIDGE_HIGHWAY = CityMap.BridgeRun.HIGHWAY, BRIDGE_RAIL = CityM...` | A long bridge's kinds (CityMap.BridgeRun's): a street-sized deck that belongs to no net, a highway's and the railway's. |
| 3908 | `CityRuns.BRIDGE_LEGS` | `1024, BRIDGE_LEG = 1<<20` | The most corners a bridge's path may have and the longest leg of it, in plots: 1,024 legs of 1,048,576 plots, so that its steps fit an int. |
| 3911 | `CityRuns.BRIDGE_NET` | `new Net(RAILWAY, RAIL_AT, 0)` | A net for a bridge's stretches to be read as, so that stretchInto() marks no highway's ramp on them: the railway's, which never takes one. |

### CityShore.java ([map](map/CityShore.md))

| line | constant | value | says |
|---:|---|---|---|
| 73 | `CityShore.QUAY_PLOTS` | `{ 10, 10, 14, 6 }` | A terminal's quay out over the sea, in plots, by Ports.Cargo's ordinal: the research's quay a berth (4.1) at its middle, in whole plots of 30 m - a tanker berth's 270 to 345 m [P1] 10, a bulk berth... |
| 76 | `CityShore.CLEAR` | `1` | Plots kept clear between two works, and between a work and a highway (H5's verge): one. |
| 79 | `CityShore.SEARCH_TILES_MOST` | `4096` | The most tiles one search looks at before it counts the work short: 4,096 - 64 districts' worth, about 0.3 s, so a city of billions with no free shore never pays more. |
| 82 | `CityShore.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west: the sea's side of a work, its quay's heading. |
| 138 | `CityShore.CELL_SHIFT` | `3` | Plots on the index's cell, as a shift: 8 - a General Cargo Terminal's box with its verge (5 x 6) touches four cells at most, the largest work's (a container terminal's 16 x 15) a dozen. |
| 141 | `CityShore.CELLS_MOST` | `64` | The most cells one work may be entered in; a work that spans more (a box or a quay no layout makes: a corrupt sidecar's) is kept apart and asked of every plot, as the loop asked it. |

### ClockPace.java ([map](map/ClockPace.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `ClockPace.REDRAW_EVERY` | `.12` | The least time between one redraw of the open screen and the next while the clock runs, in seconds (moved here from ui.UserInterface by UP2, its comment with it). |
| 46 | `ClockPace.REDRAW_SPACING` | `3` | How many times a redraw's cost the next one waits (UP2): 3, so redraws take at most a third of the thread. |
| 49 | `ClockPace.REDRAW_COST_MAX` | `.5` | The most a redraw is counted to have cost, in seconds (UP2): the pulse that ends the measure can be late (a minimised window), and a gap of minutes is a screen that never moves. |
| 52 | `ClockPace.MONTH_FRAME_MS` | `40` | The time one frame may spend running months, in milliseconds (UP2): the loop stops when the months run in it pass this, but the first month always runs. |
| 66 | `ClockPace.FRAME_CLAMP` | `.25` | The most real time one frame is worth to the game's clock, in seconds. |
| 69 | `ClockPace.SHORT_WINDOW` | `2` | The running time the speed achieved is read over, in seconds (UP2); until a full window has run the header reads the rung alone. |
| 72 | `ClockPace.SHORT_BY` | `.1` | How far under the rung the speed achieved must fall, as a share of the rung, before the header says so (UP2): more than a tenth. |

### ConstructionControl.java ([map](map/ConstructionControl.md))

| line | constant | value | says |
|---:|---|---|---|
| 115 | `ConstructionControl.STANDARD_HOURS` | `40` | The normal working week the report measures against, in hours. |
| 118 | `ConstructionControl.OVERTIME_HOURS` | `50` | The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). |
| 121 | `ConstructionControl.OVERTIME_RATE` | `1.5` | What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). |
| 124 | `ConstructionControl.WEEKS_A_MONTH` | `52.0 / 12.0` | Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. |
| 127 | `ConstructionControl.OVERTIME_WEEKS_ENDING` | `{ 2, 4, 6, 8, 10 }` | Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. |
| 130 | `ConstructionControl.OVERTIME_PRODUCTIVITY` | `{ 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 }` | Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). |
| 133 | `ConstructionControl.OVERTIME_WAGE_BILL` | `(STANDARD_HOURS +(OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDARD_HOURS` | The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. |
| 249 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,4... |
| 260 | `ConstructionControl.SALVAGE_SHARE` | `0.25` | The share of a building's construction material its demolition recovers as material: 20 to 30 percent of building-related construction and demolition debris was recovered for processing and recycli... |
| 359 | `ConstructionControl.PAVE_FROM` | `"Gravel Road"` | The road that can be paved (0.7.70): a Gravel Road, and nothing else. |
| 362 | `ConstructionControl.PAVE_TO` | `"Paved Road"` | ...and what it is paved to: a Paved Road. |

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

### Debt.java ([map](map/Debt.md))

| line | constant | value | says |
|---:|---|---|---|
| 479 | `Debt.EFFECTIVE_RATE_FLOOR` | `- 0.5` | The lowest effective rate effectiveRateOf() will report: a premium so deep it costs the issuer less than nothing, a year. |
| 482 | `Debt.EFFECTIVE_RATE_CEILING` | `5.0` | The highest, as getYieldToMaturity()'s bracket always was: 500% a year. |
| 649 | `Debt.AUCTION` | `"auction"` | How a sale at auction reads on the piece. |
| 652 | `Debt.CENTRAL_BANK_AT_PAR` | `"the central bank, at its par"` | ...the central bank's own par, rolled into a line or a new piece at issue. |
| 655 | `Debt.CENTRAL_BANK_ADD_ON` | `"the central bank's add-on"` | ...and its non-competitive add-on to a sale. |
| 658 | `Debt.ROLLOVER` | `"the rollover"` | ...and the treasury's rollover of what fell due. |

### DebtManager.java ([map](map/DebtManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 72 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 156 | `DebtManager.CURRENCY_WEIGHT` | `.25` | A quarter point of rate for each 1% the D$ sits past its target: the open-economy Taylor rule's weight on the exchange rate (the study's star 13, R39). |
| 218 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way, at the default 2% target: a one-point real rate (ruleRate() adds the target's distance from the default since 0.7.42 - its aim's, at a s... |
| 221 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 224 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 227 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 273 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target, at Standard - since 0.7.52 the dial beside the target sets another, never as little as one (HOW STRICT, below). |
| 324 | `DebtManager.STRICTEST_AIM` | `Expectations.TOLERANCE` | How far under the target the very strict bank aims, a fraction a year: Expectations.TOLERANCE, the furthest under it inflation can sit and still count as on target - so hitting the aim costs no trust. |
| 327 | `DebtManager.LOOSEST_BAND` | `2 * Expectations.TOLERANCE` | The band over the target the very loose bank lets be, a fraction a year (either side of it until 0.7.81; under it, it cuts as Standard does): twice Expectations.TOLERANCE, so its top is a miss trus... |
| 330 | `DebtManager.STRICTEST_WEIGHT` | `2.0` | The very strict bank's weight on the gap from its aim: TAYLOR_WEIGHT's margin over one, doubled. |
| 333 | `DebtManager.LOOSEST_WEIGHT` | `1.25` | The very loose bank's weight on the gap past its band: TAYLOR_WEIGHT's margin over one, halved - still over one (the Taylor principle). |
| 599 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 634 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 637 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say (public since CD1: YieldCurve clamps every forward at it). |
| 640 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 800 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 803 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 806 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 813 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 816 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 819 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 822 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 825 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 828 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1378 | `DebtManager.BOOK_KINDS` | `{ "NOTE", "SERIAL", "TERM", "FLOAT", "LINKED" }` | The kinds of paper the interest is booked by, as Debt.getType() calls them: the notes, the serial bonds, the bonds, the floaters, the linkers. |
| 1537 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1540 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 1815 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1818 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1821 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1824 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1827 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1830 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TERM_PREMIUM_...` | The table, at 10, 20, 30, 40 and 50 years: the curve's points (40 is no longer a term a bond is sold at, CD2, but it still shapes the line between 30 and 50). |
| 2320 | `DebtManager.A_POINT` | `0.01` | A point on the curve (CD6): the rise in every rate that valueOfAPoint() prices a piece at, 1% a year. |
| 2367 | `DebtManager.CURVE_TENORS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The maturities, in months, the curve is drawn and kept at: three months to fifty years. |
| 2370 | `DebtManager.CURVE_KEPT` | `25` | The months of the curve kept: today, a year ago and two years ago. |
| 2373 | `DebtManager.CURVE_ROW` | `2 + 2 * CURVE_TENORS.length` | A curve-book row: the month, the dial, the city's zero rate at each of CURVE_TENORS, the world's at each. |
| 3009 | `DebtManager.FAIR_COUPON_STEPS` | `100` | The halvings of the bisection above: far past what a double keeps of a bracket of one unit of yield. |
| 3122 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### DebtQuote.java ([map](map/DebtQuote.md))

| line | constant | value | says |
|---:|---|---|---|
| 302 | `DebtQuote.FORMAT` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### DecisionLog.java ([map](map/DecisionLog.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `DecisionLog.TAX` | `"tax"` | A tax rate, an offset or the farmland relief. |
| 60 | `DecisionLog.PROMISE` | `"promise"` | A promise: the wage floor, pensions, EI, schools, health, the fare, a standing subsidy. |
| 63 | `DecisionLog.CENTRAL_BANK` | `"central bank"` | The central bank's dials: the rate, the rule, the target, its holdings, its advances. |
| 66 | `DecisionLog.CURRENCY` | `"currency"` | The money itself: a reform, the vault bought or sold, how land is paid for. |
| 69 | `DecisionLog.BORROWING` | `"borrowing"` | The city's paper: an issue, a buyback, the rollover's setting, a default abroad. |
| 72 | `DecisionLog.BANK` | `"bank"` | The commercial bank: a rescue, the preferred offer, the rescue setting. |
| 75 | `DecisionLog.FUND` | `"fund"` | The city's fund: its dial, and the hand on it. |
| 78 | `DecisionLog.CONSTRUCTION` | `"construction"` | The construction queue (0.7.22): the order, rushes, cancels, restarts, demolitions, buy-outs. |
| 81 | `DecisionLog.RESERVE` | `"reserve"` | The city's strategic reserve (0.7.85): a fill ordered, a release set or stopped. |
| 84 | `DecisionLog.ZONING` | `"zoning"` | Zoning (0.7.109, batch Z2): a zone set, re-sized, re-priced or taken off, and the forest law - its own kind, where Z1 filed it under TAX; a zone's tax rows stay TAX, being the sector's own offsets. |

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

### Deposit.java ([map](map/Deposit.md))

| line | constant | value | says |
|---:|---|---|---|
| 95 | `Deposit.SITE_PLACES` | `places()` | The grid points a field's sites stand on, nearest the centre first, in site widths east and south: every point within PLACES_BOX of the centre each way sorted by its squared distance, then from nor... |
| 98 | `Deposit.PLACES_BOX` | `(int) Math.ceil(Math.sqrt(World.MAX_SITES / Math.PI)) + 1` | The box the site table is sorted in, in site widths each way: sqrt(World.MAX_SITES / pi) rounded up, and one more - 14 for the old world's 512 sites, 42 for 5,120, 71 for an Earth field's 15,360. |
| 101 | `Deposit.PLACES_REACH` | `reaches()` | How far, in site widths each way (L-infinity), the first k + 1 sites reach from the centre. |
| 192 | `Deposit.GRADE_SHARES` | `{ 1.0 / 3, 1.0 / 3, 1.0 / 3 }` | The share of fields of each grade, in Grade's order: a third each (est., spec-oil 2.2 and 6 - to confirm; the research gives a grade a field, not the world's mix). |
| 195 | `Deposit.GRADE_SALT` | `0x6A7DE5L` | What makes a field's grade a draw of its own, apart from its turn()'s. |
| 244 | `Deposit.TYPE_SHARES` | `{.75,.25,.85,.15,.80,.20,.70,.30, 1 }` | The share of its resource's fields of each type, in FieldType's order: the first of each resource's two the study's (docs/studies/spec-materials.md 2.4, the study's 1.4: hematite .75, thermal .85, ... |
| 247 | `Deposit.TYPE_SALT` | `0x7E1DA5L` | What makes a field's type a draw of its own, apart from its turn()'s and its grade()'s. |

### DistrictPicture.java ([map](map/DistrictPicture.md))

| line | constant | value | says |
|---:|---|---|---|
| 48 | `DistrictPicture.PLOTS_A_PIXEL` | `TileRaster.BLOCK_FAR` | Plots a pixel takes, a side: 8 (240 m, the study's), TileRaster.BLOCK_FAR - the far view's blocks at L2, so a picture's pixels lie on the blocks the closer view draws. |
| 51 | `DistrictPicture.SIDE` | `World.DISTRICT / PLOTS_A_PIXEL` | Pixels a side: 32, a district's World.DISTRICT plots at PLOTS_A_PIXEL. |
| 54 | `DistrictPicture.PIXELS` | `SIDE * SIDE` | Pixels in a picture: 1,024. |
| 57 | `DistrictPicture.PLOTS_IN` | `PLOTS_A_PIXEL * PLOTS_A_PIXEL` | The plots a pixel is picked from: 64. |
| 62 | `DistrictPicture.GROUND` | `0` | Nothing the picture draws: the ground, owned or not - the far node shows its own there. |
| 64 | `DistrictPicture.FIRST_CLASS` | `1` | A building of a class, HOME to TRANSPORT: its class + FIRST_CLASS (BuildingVisual.FILL's colour). |
| 66 | `DistrictPicture.FLATS` | `FIRST_CLASS + BuildingVisual.CLASSES` | ...the flats (BuildingVisual.FLATS_FILL)... |
| 68 | `DistrictPicture.CAMPUS` | `FLATS + 1, TERMINAL = FLATS + 2, TANKS = FLATS + 3` | ...the refinery's units (CAMPUS_FILL), a sea terminal (TERMINAL_FILL) and a tank farm (TANKS_FILL): each type's own fill, as the painter draws it (BuildingVisual.Type.fill()). |
| 70 | `DistrictPicture.WORKS` | `TANKS + 1` | ...since 0.8.0 (batch I14) each processing industry's works, Iron & Steel's to Wood Products' (BuildingVisual.GROUP_FILL, STEEL to WOOD), at WORKS + its group - STEEL... |
| 72 | `DistrictPicture.WASH` | `WORKS + BuildingVisual.GROUPS - BuildingVisual.STEEL, CAMP = WASH + 1, STAND = WASH + 2` | ...and the coal wash plant, a logging camp and planted forest (BuildingVisual.LOOK_FILL): the materials' boxes that are not on a site (a mine on its site is the plan's SITE_MINED, Mines and wells). |
| 74 | `DistrictPicture.PAVED` | `STAND + 1` | A paved street of the plan, in TileRaster's colour below LINES_FROM px a plot (ROAD_SMALL's paved: its casing) - its gravel streets and tracks are the ground's, hidden as the far view's blocks hide... |
| 76 | `DistrictPicture.HIGHWAY` | `STAND + 2, RAIL = STAND + 3` | ...and a highway's plot - the city's runs' Elevated Highway, or a street of highway plots - and the railway's track: ROAD_SMALL's highway and RAIL_DARK. |
| 78 | `DistrictPicture.CODES` | `RAIL + 1` | How many codes. |
| 81 | `DistrictPicture.COLOUR` | `new int [ CODES ]` | Each code's colour, 0xAARRGGBB - the painter's (BuildingVisual's fills, TileRaster's roads below LINES_FROM); 0 for GROUND, which is the node's own. |
| 152 | `DistrictPicture.BYTES` | `PIXELS + 16 + 24` | The bytes it holds, about: its codes, their array's header and the picture's own. |

### DistrictPlan.java ([map](map/DistrictPlan.md))

| line | constant | value | says |
|---:|---|---|---|
| 305 | `DistrictPlan.SIDE` | `CityMap.DISTRICT` | Plots on a district's side: CityMap.DISTRICT, 256. |
| 308 | `DistrictPlan.FRAME` | `SIDE + 1` | Plots on the plan's side: the district and the first column (row) of its east (south) neighbour, where its edge cells' last arterial runs - 257. |
| 311 | `DistrictPlan.AREA` | `FRAME * FRAME` | Plots in the frame. |
| 314 | `DistrictPlan.CELL` | `World.TILE` | A cell's side, arterial to arterial: a tile, World.TILE, 32 plots (960 m) - its west column and north row its arterials (spec 2.3). |
| 317 | `DistrictPlan.INTERIOR` | `CELL - 1` | A cell's interior: 31 plots a side, between its arterials. |
| 320 | `DistrictPlan.CELLS_A_SIDE` | `SIDE / CELL` | Cells on a district's side: 8. |
| 323 | `DistrictPlan.CELLS` | `CELLS_A_SIDE * CELLS_A_SIDE` | Cells in a district: 64. |
| 326 | `DistrictPlan.LATTICE` | `TilePainter.JUNCTION_APART` | The street lattice: lines every TilePainter.JUNCTION_APART (8) plots, the + junction floor - a cell's west arterial on one, so the lattice runs on from cell to cell. |
| 329 | `DistrictPlan.REACH` | `TilePainter.REACH` | A building is within reach of a street when one lies within this many plots of it, across corners: TilePainter.REACH, 4. |
| 332 | `DistrictPlan.STREETS_AT` | `{ LATTICE - 1, 2 * LATTICE - 1, 3 * LATTICE - 1 }` | Where a cell's streets run, in plots into its interior: 7, 15 and 23 - the lattice's lines inside it (spec 2.3's long blocks of 15 x 7, square blocks of 7 x 7). |
| 335 | `DistrictPlan.MIDDLE` | `2 * LATTICE - 1` | ...and a homes cell's cross street and an estate cell's spine: the middle line, 15 - long blocks 15 plots long either side of it. |
| 340 | `DistrictPlan.LAYOUT_TODAY` | `0, LAYOUT_DROPPED = 1, LAYOUT_BRICK = 2, LAYOUT_CRESCENT = 3` | A homes cell's street layout, chosen by layoutOf(): TODAY the comb (a squared cell: the whole square) as ever, DROPPED some of its crossings left out, BRICK its cross street's pieces at alternating... |
| 343 | `DistrictPlan.TODAY_SHARE` | `1.0 / 3` | The share of homes cells, long-block and squared alike, that keep today's layout: a third (Jerus, 2026-10-20: "A third stays"). |
| 346 | `DistrictPlan.DROPPED_SHARE` | `1.0 / 3` | ...and the share that drop crossings (the rest are brick), likewise: a third. |
| 349 | `DistrictPlan.CRESCENT_FROM` | `16` | How far from the founding site a long-block homes cell's middle must lie, in cells (Euclidean, CELL plots a cell), to be laid as a crescent (CL4, crescentOf()): 16 - two districts, 15 km, past wher... |
| 352 | `DistrictPlan.CRESCENT_SHARE` | `0.5` | The share of the long-block homes cells beyond CRESCENT_FROM, whose layout is not the comb, that are crescents: a half - so beyond it a third of the cells keep the comb (Jerus's "a third stays"), a... |
| 355 | `DistrictPlan.CRESCENT_SALT` | `120` | The salt of the crescent's hash in layoutHash(): 120, beyond the layouts' own (0, 11 to 16, 21, 22, 31, 40 to 111); the bulge's is the next, CRESCENT_SALT + 1. |
| 358 | `DistrictPlan.ARC_SHIFT` | `3` | How far the crescent's arc stands from the cell's cross line (the line u = MIDDLE) at its ends, in plots: 3 - its ends stand at MIDDLE - bulge x 3 and it bulges toward the other side, so the arc st... |
| 361 | `DistrictPlan.ARC_RADIUS` | `(INTERIOR + 1) / Math.sqrt(2)` | The crescent's arc radius, in plots: (INTERIOR + 1) / sqrt 2 - a quarter of a circle whose chord runs from the ring at v = -1 to the ring at v = INTERIOR, so it turns 90 degrees and meets each ring... |
| 364 | `DistrictPlan.HALF_DROP` | `0.25` | The chance a dropped long-block cell also loses half of a long street (its block beside it 15 deep): 0.25. |
| 367 | `DistrictPlan.HOLE_MOST` | `49` | The most plots of a long-block cell a lost half of a long street may leave beyond REACH of every street (a green hole in the block): 49, one 7 x 7. |
| 370 | `DistrictPlan.PIECE_DROP` | `0.22` | The chance a dropped squared cell loses each of its 24 street pieces: 0.22. |
| 373 | `DistrictPlan.BRICK_LINES` | `{ { 11, 19 }, { 9, 21 }, { 7, 23 }, { 7, 19 }, { 11, 23 } }` | The lines a brick long-block cell's cross street may alternate between, in plots into the interior: a pair for the cell's hash to pick from, 8 or more apart so no two junctions on a long street are... |
| 376 | `DistrictPlan.BRICK_BAND_LINES` | `{ 11, 19 }` | ...and the two lines a brick squared cell's cross streets take in the bands between those at 7, 15 and 23: 11 and 19. |
| 379 | `DistrictPlan.DROPPED_TRIES` | `2` | The draws a dropped long-block cell makes to keep its holes within HOLE_MOST: 2, the second without the half of a long street. |
| 382 | `DistrictPlan.PIECES_TRIES` | `3` | ...and a dropped squared cell makes to leave nothing beyond REACH: 3, then the whole square. |
| 385 | `DistrictPlan.SEG_FROM` | `{ 0, 8, 16, 24 }, SEG_TO = { 6, 14, 22, 30 }` | The ends of a line's four pieces between its crossings (the ring, the lines at 7, 15, 23, the ring), in plots into the interior: from 0, 8, 16, 24 to 6, 14, 22, 30. |
| 388 | `DistrictPlan.LINE_FIRST` | `LATTICE - 1` | An estate cell's streets lie on interior lines from this one (0.7.90, ESTATE LINES): 7, LATTICE - 1 - so where one meets a neighbour's street across an arterial, the + it makes is LATTICE or more f... |
| 391 | `DistrictPlan.LINE_LAST` | `INTERIOR - LATTICE` | ...to this one: 23, INTERIOR - LATTICE, the same from the far corner. |
| 394 | `DistrictPlan.STRIP_LEAST` | `LINE_FIRST` | A strip's least depth between an estate cell's streets: 7, LINE_FIRST - so its streets are LATTICE or more apart, and so are the + junctions two of them could make on one arterial. |
| 397 | `DistrictPlan.STREET_BRIDGE` | `TilePainter.STREET_BRIDGE` | A street's longest crossing of bridgeable water (fresh, and since ET6 the shallow sea): TilePainter.STREET_BRIDGE, 6 plots (spec 2.4, today's street bridge). |
| 405 | `DistrictPlan.ARTERIAL_BRIDGE` | `14` | An arterial's: 14 plots (420 m; spec 2.4), its own since ET6 (spec-earth2-terrain 6.2, D12): it was a highway's, TilePainter.MAX_BRIDGE[HIGHWAY], until the highway's and the railway's widened to 34... |
| 408 | `DistrictPlan.CELL_ROOM_LEAST` | `120` | A cell is opened only with at least this many plots of dry owned ground off the highways in its interior: 120 of 961, the prototype's. |
| 411 | `DistrictPlan.NUDGE` | `0.6` | The hashed nudge on a cell's distance from the hub, in cells: up to 0.6, the prototype's - the open cells' edge ragged, not a disc. |
| 420 | `DistrictPlan.FOOTHILL_RANK` | `1.0` | How much further from the hub, in cells, a cell wholly on foothill ranks than a cell wholly flat (ET2, spec-earth2-terrain 4.2, flat first): a cell's distance gains this times its foothill share of... |
| 423 | `DistrictPlan.MIXED_SLOT` | `640` | The first band's buildings are dealt round one slot for every this many plots of their footprints: 640, the prototype's - about a homes cell's room after its streets, so the slots are about its cel... |
| 426 | `DistrictPlan.LONG_BLOCK_STREETS` | `121` | A long-block cell's street plots inside its ring: 121 - a square cell's 177 (3 rows and 3 columns) less SQUARE_PLOTS. |
| 429 | `DistrictPlan.SQUARE_PLOTS` | `56` | The surface the ladder's square blocks take a homes cell: 56 plots, the prototype's - a square cell's streets (3 rows and 3 columns, 177 plots) less a long-block cell's (121, LONG_BLOCK_STREETS). |
| 432 | `DistrictPlan.BOULEVARD_PLOTS` | `100` | ...and a boulevard a cell: 100 plots, the prototype's - its arterials' second row, less what neighbouring boulevards share. |
| 435 | `DistrictPlan.MERGE_GROUPS` | `{ { 15, 15 }, { 15, 23 }, { 15, 31 }, { 31, 15 }, { 31, 31 } }` | The groups of long blocks a homes cell merges for a building wider than a block, {across the blocks, along them} in plots: two blocks, three, four, two across, the whole cell (the prototype's; spec... |
| 438 | `DistrictPlan.MERGE_DRY` | `0.95` | A group of blocks is merged only when more than this share of it is dry ground or street: 0.95, the prototype's. |
| 441 | `DistrictPlan.BLOCK_DEPTH` | `LATTICE - 1` | A building wider than this, in a homes cell, may merge blocks: 7, a block's depth. |
| 444 | `DistrictPlan.JOIN_ROUNDS` | `40` | The most pieces the join joins, one at a time: 40, the prototype's. |
| 447 | `DistrictPlan.JOIN_DX` | `{ 0, 0, 1, - 1 }, JOIN_DY = { 1, - 1, 0, 0 }` | The join's steps in the prototype's order: south, north, east, west - so of two ways as short it takes the prototype's. |
| 450 | `DistrictPlan.WALL_MOST` | `2 * LATTICE` | The most plots of buildings the way to a street piece they wall in may cross, to be kept clear of them (FW11): 16, two lattice blocks' depth; a piece that needs more is left apart, as it was before. |
| 453 | `DistrictPlan.WALL_TRIES` | `3` | The most times a plan is made again with the ways to the pieces its buildings wall in kept clear (FW11): 3; a piece still walled in then is left apart, as before. |
| 456 | `DistrictPlan.HALF` | `0.5` | A street's least surface, in plots of its right of way: a half, 15 m (spec 2.5). |
| 459 | `DistrictPlan.FULL` | `1.0` | ...and its full width, a whole plot, 30 m. |
| 464 | `DistrictPlan.FIXED_HIGHWAY` | `BuildingVisual.HIGHWAY` | A fixed plot: an Elevated Highway's (BuildingVisual.HIGHWAY). |
| 467 | `DistrictPlan.FIXED_RAIL` | `TilePainter.RAIL` | ...a railway's track (TilePainter.RAIL). |
| 470 | `DistrictPlan.FIXED_YARD` | `CityRuns.F_YARD` | ...a railway yard's ground, a Rail Terminal the city's runs drew on its track (0.7.89, CityRuns.F_YARD): no street crosses it, no building stands on it. |
| 473 | `DistrictPlan.FIXED_RESERVE` | `CityRuns.F_RESERVE` | ...an interchange's reserve (batch FR7, D19, CityRuns.F_RESERVE): a cloverleaf's 19 x 19 plots or a trumpet's 11 x 11, street ground - no lot, no building, no street across it but the highways and ... |
| 479 | `DistrictPlan.SITE_FIELD` | `1` | A site with nothing on it: a field, built on last. |
| 482 | `DistrictPlan.SITE_MINED` | `2` | A site a mine or well stands on: its own. |
| 485 | `DistrictPlan.CLOSED` | `0` | A cell's layout: not opened. |
| 487 | `DistrictPlan.HOMES` | `1` | ...homes: long blocks (or square, the ladder's), for what follows people. |
| 489 | `DistrictPlan.ESTATE` | `2` | ...an estate: one spine, two strips, for industry and the outer kinds. |
| 494 | `DistrictPlan.NONE` | `0` | A plot's street, its low three bits: none. |
| 496 | `DistrictPlan.TRACK` | `1` | ...a track: a street the city has bought no road for (spec 2.5, R4). |
| 498 | `DistrictPlan.GRAVEL` | `2` | ...gravel. |
| 500 | `DistrictPlan.PAVED` | `3` | ...paved. |
| 502 | `DistrictPlan.HIGHWAY` | `4` | ...an Elevated Highway's plots given to the plan as a street's surface (Input.highway: CityMap's deal did so from 0.7.72, its plans to 0.7.88; since 0.7.89 the runs lay every one and the map gives ... |
| 504 | `DistrictPlan.SEAM` | `5` | ...a seam: a street on the district's edge whose surface is the neighbour's (SEAMS). |
| 506 | `DistrictPlan.UNDER` | `6` | ...a street passing beneath a highway (spec 2.7: elevated). |
| 508 | `DistrictPlan.KIND_MASK` | `7` | The kind's bits. |
| 510 | `DistrictPlan.WIDTH_SHIFT` | `3` | Its width, bits 3 and 4: 1 half, 2 full; 0 for none (a track, a seam, beneath a highway). |
| 512 | `DistrictPlan.ROLE_SHIFT` | `5` | Its role, bits 5 to 7. |
| 514 | `DistrictPlan.ROLE_STREET` | `1` | ...a cell's street. |
| 516 | `DistrictPlan.ROLE_ARTERIAL` | `2` | ...an arterial: a cell's ring. |
| 518 | `DistrictPlan.ROLE_BOULEVARD` | `3` | ...an arterial of a boulevard cell, either row (spec 2.5). |
| 520 | `DistrictPlan.ROLE_SHORE` | `4` | ...a street along a cut, joining a dead end to its neighbour (H2). |
| 522 | `DistrictPlan.ROLE_JOIN` | `5` | ...a street the join laid along the lattice. |
| 524 | `DistrictPlan.ROLE_ACCESS` | `6` | ...an access road (batch FR7, D17): the street from a sea terminal's, a yard's or a Truck Depot's gate to the nearest highway ramp or, failing one, the arterial grid - an arterial in all but its na... |
| 527 | `DistrictPlan.ACCESS_NEW` | `3` | The cost of laying a plot of access road where there is no street, against 1 for a plot that already is one: 3 - so a road follows the streets there are, and crosses open ground only where that is ... |
| 530 | `DistrictPlan.ACCESS_RAMP_DISTRICTS` | `2` | How far from the work it serves a highway ramp may lie, in districts, for its access road to go there and not to the arterial grid: 2 (spec-freight 4.5: "the nearest highway ramp if one lies within... |
| 533 | `DistrictPlan.SIDING_PLOT` | `TilePainter.SIDING_PLOT` | A plot of a siding the plan laid (batch FR7 slice 5, D14), by plot in DistrictPlan.siding... |
| 535 | `DistrictPlan.SIDING_END` | `TilePainter.SIDING_END` | ...and the first plot of a siding, beside the works' box, where its loading shed stands. |
| 538 | `DistrictPlan.DEFAULT_SIDING_PLOTS` | `72` | The plots a siding may have when its input does not say (Input.sidingPlots): 72, a Rail Spur's track in buildings.json. |
| 541 | `DistrictPlan.SIDING_CROSSING` | `3` | What a siding pays to cross a street at grade, over the 1 of a plot of open ground: 3 - a level crossing is a cost, so a siding takes the open ground beside a street where that is not much longer. |
| 544 | `DistrictPlan.SIDING_ALONG` | `8` | ...to run along a street plot after street plot, over a plot's own cost: 8 - a siding never takes a street's place, so it crosses and leaves it (the search also never turns on a street). |
| 547 | `DistrictPlan.SIDING_TURN` | `2` | ...and what a turn costs it, so a siding is straight or turns few times, not a staircase: 2. |
| 550 | `DistrictPlan.SIDING_EDGE` | `2` | The room a siding's plots keep from the frame's edge, in plots: 2, so no siding plot lies on the edge row or column the districts share or beside it, and the tile beside a district's never needs th... |
| 552 | `DistrictPlan.BRIDGE` | `1<<8` | A bridge over bridgeable water (fresh, or since ET6 the shallow sea), bit 8. |
| 554 | `DistrictPlan.CROSSING` | `1<<9` | A level crossing of a railway, bit 9. |
| 559 | `DistrictPlan.AVENUE_DX` | `{ 1, - 1, - 1, 1 }, AVENUE_DY = { 1, 1, - 1, - 1 }` | The four avenues' headings, (dx, dy) in plots by arm: south-east, south-west, north-west, north-east - CityRuns.diagCode()'s numbering, 0 to 3, the arm in a plot's mark. |
| 562 | `DistrictPlan.AVENUE_BRIDGE_STEPS` | `(int)(ARTERIAL_BRIDGE / Math.sqrt(2))` | The longest run over bridgeable water a bridge on an avenue carries, in steps along it: ARTERIAL_BRIDGE / sqrt 2 rounded down, 9 - a step is sqrt 2 plots, so 9 steps are 12.7 plots and 10 are over 14. |
| 565 | `DistrictPlan.AVENUE_APART` | `LATTICE` | The least steps between two junctions kept on one arm (the + floor along an avenue): LATTICE. |
| 775 | `DistrictPlan.ARCS` | `{ new ArcShape(- 1), new ArcShape(1) }` | The two crescent shapes, bulge -1 and +1 (arc()). |
| 854 | `DistrictPlan.ACCESS_RAMP` | `1, ACCESS_ARTERIAL = 2, ACCESS_HAD = 3` |  |
| 894 | `DistrictPlan.Curve.CRESCENT` | `1, SHORE = 2` | A crescent's arc (geom: its circle {centre x, centre y, radius, the angle from the centre to the arc's start, the signed sweep}, as TrackCurves.Bend has them; radius = the circle's) and a street al... |
| 942 | `DistrictPlan.NO_CURVES` | `List.of()` | No curves: a plan that lays none (the prototype's, Input.latticeOnly) has this list. |
| 1070 | `DistrictPlan.BUILDERS` | `new ThreadLocal<>()` | KEPT (0.7.94, batch RD7): each thread's builder, its arrays - about 4 MB - and the ladder's turns - about 0.4 MB each, one a step of the longest chain it has climbed - made once and used plan after... |
| 1288 | `DistrictPlan.Builder.WORDS` | `(FRAME + 63) / 64` | The streets row by row as bits, WORDS longs a row: what a cell's reach is read from. |
| 1362 | `DistrictPlan.Builder.WORN` | `1<<30` | The stamps' ceiling: a builder is replaced past it, half the int's range, far beyond what a plan stamps (a dense plan some tens of thousands). |
| 3117 | `DistrictPlan.Builder.AV_PLOTS` | `0, AV_UNDER = 1, AV_RAIL = 2, AV_BRIDGED = 3, AV_BROKEN = 4, AV_WATER = 5, AV_STOPS = 6...` | The slots of avN, the counts the plan's avenue* fields are made from: plots laid, beneath a highway, across the railway, bridging, not laid at a yard, reserve or mine, at water too wide, cell stree... |
| 3119 | `DistrictPlan.Builder.AVK_OFF` | `0, AVK_BROKEN = 1, AVK_WATER = 2, AVK_UNDER = 3, AVK_STREET = 4` | What avKind() makes of a staircase plot: not laid (unowned, salt water), not laid at a yard, reserve or mine's site, not laid at water too wide, beneath a highway, a street. |
| 3923 | `DistrictPlan.Builder.FLIP` | `1<<31` | estateLines()'s flag: the cell's streets run the other way from its own direction (a cell the ground cuts). |
| 3939 | `DistrictPlan.Builder.FULL_LINES` | `new java.util.concurrent.ConcurrentHashMap<>()` | Each layout a full cell takes for a building's shape, its ring whole: {across, down, boulevard, the cell's direction} to estateLines()'s answer - the same whatever lies about it (no street beyond i... |
| 4048 | `DistrictPlan.Builder.H_STREET` | `1, H_ARTERIAL = 2, V_STREET = 4, V_ARTERIAL = 8, LAY_REFUSED = 15, LAY_UNDER = 16` | LAY CODES (0.7.94, batch RD7): what layPlot() makes of each plot, read from the input once a plan - the plot refused to a street running east-west (H_STREET), to an arterial so (H_ARTERIAL), and no... |
| 4522 | `DistrictPlan.Builder.LAST_WORD` | `(1L<<(FRAME - 64 *(WORDS - 1))) - 1` | A row's last word's plots in the frame: bit 0 to FRAME - 1 - 64 x (WORDS - 1). |
| 4582 | `DistrictPlan.Builder.WINDOW` | `(1L<<(INTERIOR + 2 * REACH)) - 1` | A window's row as bits, one a plot: a cell's interior and the REACH plots either side of it. |
| 5141 | `DistrictPlan.Builder.SPLIT_NEAR` | `CELL` | splits()'s first walk keeps within this many plots of the box: a cell (NEAR FIRST). |
| 5418 | `DistrictPlan.Builder.SD_BUCKETS` | `16` |  |
| 6180 | `DistrictPlan.Builder.PARTED_BUCKETS` | `2 *(CELLS + 1) + 1` | surface()'s buckets a side of the river: the access roads', the arterials' by cell rank and the rest's (FW3). |

### EarthData.java ([map](map/EarthData.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `EarthData.EARTH_2` | `"earth-2:c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b"` | The file this build reads, as a save will name it: earth-2 as the Earth 2 study baked it on 2026-10-12 at 15" (tools/earth/earth-2.sha256); the earth-1 it holds the layers of is "earth-1:b754a28f9f... |
| 103 | `EarthData.DATA_FILE` | `"data/earth-2.bin"` | Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored); the game runs without it and offers the generated world (EarthSite.game... |
| 106 | `EarthData.PROPERTY` | `"cbs.earth"` | The system property that names the file instead (-Dcbs.earth=<path>), as the harnesses' runs on the full file and LongPlaytest --earth take it. |
| 109 | `EarthData.MAGIC` | `"EARTH-1\n"` | The bytes the first eight of the file are: they name the container's layout, which earth-2.bin shares with earth-1.bin, not the file. |
| 112 | `EarthData.FORMAT` | `1` | The layout's version this reads (FORMAT.txt). |
| 115 | `EarthData.CACHE_BYTES` | `10_000_000` | The inflated tiles kept, in bytes: spec-earth 5's heap budget of about 10 MB (64 1-degree tiles of sea and lake at 115 KB, and the rest). |
| 118 | `EarthData.KIND_TILES` | `0` | A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner. |
| 120 | `EarthData.KIND_BANDS` | `1` | A layer of 180 1-degree bands of latitude, numbered lat0 + 90. |
| 122 | `EarthData.KIND_WHOLE` | `2` | A layer held whole, one blob. |
| 125 | `EarthData.TILES` | `180 * 360` | The tiles in a KIND_TILES layer. |
| 128 | `EarthData.SHARE_LEVELS` | `15` | The sea and lake layers' levels: a cell's share is its value over this. |
| 130 | `EarthData.FAR_LEVELS` | `255` | The far view's levels: a cell's share is its value over this. |
| 133 | `EarthData.SLOPE_LEVELS` | `7` | The slope layer's levels in each of the foothill and steep shares (3 bits): a share is the value over this (FORMAT-2.txt). |
| 135 | `EarthData.VERY_STEEP_LEVELS` | `3` | The slope layer's levels in the very steep part of the steep ground (2 bits): the part is the value over this. |
| 137 | `EarthData.DEPTH_METRE_CODES` | `200` | A sea depth code of this or less is that many metres (0 is no sea, 1 to this the metres). |
| 139 | `EarthData.DEPTH_STEP_M` | `10` | Above DEPTH_METRE_CODES each code is this many metres deeper: 201 is 210 m, 254 is 740 m. |
| 141 | `EarthData.DEPTH_DEEPEST_CODE` | `255` | The sea depth code for the deepest sea the layer tells apart. |
| 143 | `EarthData.DEPTH_DEEPEST_M` | `750` | What the deepest code, 255, stands for and depthM() answers for it: 750 m or deeper (the layer does not say how much deeper). |
| 145 | `EarthData.SLOPE` | `"slope"` | The slope layer's name: a file has the earth-2 layers when it has this one (hasRelief()). |
| 147 | `EarthData.LANDMASS` | `"landmass"` | The landmass layer's name: a palette blob a tile, not a byte grid (landmassAt()). |
| 150 | `EarthData.LC_FOREST` | `0x0F` | Land cover's forest level, 0 to SHARE_LEVELS: the forest's share of the cell's land. |
| 152 | `EarthData.LC_BEACH` | `0x10` | Land cover's bit for bare ground at 2% of the land or more (the beach rule). |
| 154 | `EarthData.LC_DESERT` | `0x20` | Land cover's bit for bare ground at half the land or more: the founding card's desert warning (EarthSite, 0.7.121). |
| 156 | `EarthData.LC_ICE` | `0x40` | Land cover's bit for snow and ice at half the land or more: what the founding card refuses (EarthSite, 0.7.121). |
| 158 | `EarthData.LC_NO_DATA` | `0x80` | Land cover's bit for land WorldCover has no data on (south of 60 S, and GEBCO-only land). |
| 161 | `EarthData.RIVER_UNITS` | `100_000` | A river vertex's position is stored in these units a degree. |
| 163 | `EarthData.LAKE_UNITS` | `10_000` | A small lake's position is stored in these units a degree. |
| 165 | `EarthData.SMALL_LAKE_KM2` | `0.1` | A small lake's area at code 0, km2; each code is an eighth of an octave more (FORMAT.txt). |
| 167 | `EarthData.LAKE_CODES_AN_OCTAVE` | `8` | A small lake's area codes an octave. |
| 170 | `EarthData.RIVER` | `0` | GRWL's flag for a river. |
| 172 | `EarthData.TIDAL` | `2` | GRWL's flag for a tidal river. |
| 174 | `EarthData.CANAL` | `3` | GRWL's flag for a canal. |
| 184 | `EarthData.EIGHTH_OCTAVES` | `{ 0x1.0p0, 0x1.172b83c7d517bp0, 0x1.306fe0a31b715p0, 0x1.4bfdad5362a27p0, 0x1.6a09e667f...` | 2^(k/8) for k = 0 to 7, each the double nearest the true value (worked to 60 digits): a small lake's area is SMALL_LAKE_KM2 x 2^(code/8), and StrictMath.pow misses the nearest double by one unit at... |
| 188 | `EarthData.LAKE_AREA` | `new double [ 256 ]` | The area a small lake's code stands for, km2, code by code. |
| 864 | `EarthData.SHALLOW_M` | `30` | The depth, in metres, the shallow layer's share counts the sea to: 30 (FORMAT-2's depth.SHALLOW_M; the study's D11, ET1): the sea World.SHALLOW is drawn of. |
| 933 | `EarthData.LANDMASS_GRIDS_KEPT` | `16` | How many landmass tiles landmassGrid() keeps read out: 16, about a megabyte (a 15" tile is 57,600 cells, a byte each). |

### EarthProj.java ([map](map/EarthProj.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `EarthProj.RADIUS_M` | `6_371_007` | The sphere's radius in metres: WGS 84's authalic radius, the sphere with the ellipsoid's own area, so an equal-area map of it keeps the Earth's real km2 (spec-earth 2). |
| 64 | `EarthProj.REACH_M` | `2 * RADIUS_M` | How far out in the plane the planet reaches, in metres: the antipode maps to the circle of twice the radius, and past it the world's square is off the planet. |
| 67 | `EarthProj.BLEND_MAX_LAT` | `75` | The latitude, in degrees, above which a tile projects every point exactly instead of blending its corners: a blend bends from the truth by about side^2 x tan(lat) / 8R, 6.8 cm for a 960 m tile at 7... |

### EarthSite.java ([map](map/EarthSite.md))

| line | constant | value | says |
|---:|---|---|---|
| 72 | `EarthSite.CARD_PLOTS` | `EarthTerrain.SNAP_PLOTS` | The card's reach, in plots: EarthTerrain.SNAP_PLOTS, 167 (5 km, spec-earth 2) - the snap's and the ground's. |
| 75 | `EarthSite.SEARCH_HITS` | `8` | Places a search offers at most: 8, as many as the list under the field shows. |
| 78 | `EarthSite.RANDOM_TRIES` | `64` | Places Random spot tries, in its seed's order, before it gives up: 64 (the data's 7,342 are towns and cities, nearly all on dry ground). |
| 81 | `EarthSite.IRON_RINGS` | `16` | How far the nearest iron is looked for, in world cells round the site's: 16 (983 km); past it the card says there is none that near. |
| 84 | `EarthSite.EARTH_FORMAT` | `38` | The save format a city on the real Earth is first written in: 38 (GameVersion's A CITY ON THE REAL EARTH) - a build before it would put the city on its seed's generated ground, so it refuses the sa... |
| 93 | `EarthSite.ICE_FORMAT` | `39` | The save format the real Earth is first drawn in with ice and desert: 39 (0.7.127, batch E7; GameVersion's ICE AND DESERT ON THE REAL EARTH). |
| 103 | `EarthSite.DEPOSITS_FORMAT` | `41` | The save format the real Earth's fields are first drawn on its geology in: 41 (0.8.12, batch EG1; GameVersion's DEPOSITS WHERE THE GEOLOGY SAYS). |
| 116 | `EarthSite.SLOPE_FORMAT` | `46` | The save format the real Earth's founding site is first snapped to buildable ground in: 46 (ET2, spec-earth2-terrain 3.4; GameVersion's BUILDABLE GROUND, FOUNDED ON). |
| 124 | `EarthSite.LANDMASS_FORMAT` | `47` | The save format the city's ground is first counted by landmass in: 47 (ET5; Landmasses, GameVersion's LANDMASSES). |
| 127 | `EarthSite.RANDOM_SALT` | `0x5B07A11DL` | The stream Random spot draws its order from. |
| 178 | `EarthSite.GET_EARTH` | `"tools/earth/get-earth.bat"` | The script that fetches the file, from the folder the game runs in (the folder DATA_FILE is looked for from): there in a copy of the repository, not in the built game, which carries the file in its... |
| 280 | `EarthSite.BEFORE_ICE_WORDS` | `"This city was founded on the real Earth before ice was a kind of ground(0.7.127): its ...` | The load's words for a city on the real Earth saved before ice was a kind of ground (ICE_FORMAT). |
| 284 | `EarthSite.BEFORE_DEPOSITS_WORDS` | `"This city was founded on the real Earth before its oil, iron and other fields were dra...` | The load's words for a city on the real Earth saved before its fields were drawn on the geology (DEPOSITS_FORMAT). |
| 289 | `EarthSite.BEFORE_SLOPE_WORDS` | `"This city was founded on the real Earth before slope decided where a building may stan...` | The load's words for a city on the real Earth saved before its founding site was snapped to ground a building may stand on (SLOPE_FORMAT). |
| 294 | `EarthSite.BEFORE_LANDMASS_WORDS` | `"This city was founded on the real Earth before its ground was counted by landmass: gro...` | The load's words for a city on the real Earth saved before its ground was counted by landmass (LANDMASS_FORMAT). |
| 561 | `EarthSite.NO_SITE_WORDS` | `"No dry ground within 5 km to found a city on: the spot is in the sea, or on an island ...` | The card's words for a spot with no site: in the sea, or on an island too small (a spot among steep slopes has SLOPE_WORDS). |
| 564 | `EarthSite.SLOPE_WORDS` | `"No ground a building may stand on within 5 km: the slopes here are steep."` | The card's words for a spot among steep slopes (ET2): the first sentence of noSiteWords(), the nearest buildable ground after it. |
| 567 | `EarthSite.ICE_WORDS` | `"On ice: snow and ice cover half the ground here or more, and no city is founded on an ...` | ...on ice. |
| 570 | `EarthSite.DESERT_WORDS` | `"A desert: bare ground covers half the land here or more."` | The warning for a desert. |
| 573 | `EarthSite.NO_FRESH_WORDS` | `"No river or lake within 5 km: a water plant needs one, so the city's first water is fr...` | The warning where there is no fresh water within the card's reach and the sea is near. |
| 576 | `EarthSite.NO_WATER_WORDS` | `"No river, lake or sea within 5 km: the city has nowhere near to draw its water from."` | ...and where there is neither. |

### EarthTerrain.java ([map](map/EarthTerrain.md))

| line | constant | value | says |
|---:|---|---|---|
| 145 | `EarthTerrain.NOISE_HI` | `4` | The noise's widest octave, 2^4 plots (480 m): just over the data's 15" cell (463 m), so the detail drawn lives below what the data resolves (spec-earth 3). |
| 148 | `EarthTerrain.NOISE_LO` | `1` | ...down to 2^1 plots (60 m), two plots, the finest a plot-centred noise can carry (spec-earth 3). |
| 151 | `EarthTerrain.SEA_LEVEL` | `0.5` | The sea's share from which the coast is drawn, 0.5: the data's half-contour (spec-earth 3) - and what an Earth world gives as its sea's level (World.seaTheta()), having no heights. |
| 163 | `EarthTerrain.COAST_FRAY` | `1.0` | How far the coast noise frays the coast: 1.0 (spec-earth 3's COAST_FRAY) - sea where share + 1.0 x (noise - 0.5) reaches SEA_LEVEL, the noise unranked, so the coast is the interpolated share's half... |
| 172 | `EarthTerrain.BLOB_FRAY` | `1.0` | How far a small lake's edge is frayed: 1.0 (★E2, the coast's own rule on a lake's disc) - a plot at distance d from a lake of radius r is in it where (d/r)^2 + 1.0 x (rank - 0.5) is under 1, so the... |
| 175 | `EarthTerrain.COVER_LEVEL` | `0.5` | The bit's share from which a plot is ice, or desert (0.7.127, batch E7; spec-globe 3.9): 0.5, its half-contour - each kind's land-cover bit read 0 or 1 at its 2' cell and interpolated between the c... |
| 184 | `EarthTerrain.COVER_FRAY` | `COAST_FRAY` | How far each kind's own noise frays the edge of ice and of the desert (0.7.127, batch E7; spec-globe 3.9: "tested against 1/2 plus its own noise, as the coast is"): COAST_FRAY, 1.0 - ice where the ... |
| 187 | `EarthTerrain.QUANTILE_SAMPLES` | `1<<18` | The noise's samples the ranking table holds: 2^18 (spec-earth 3's QUANTILE), at fixed places, one table for every class and every world. |
| 190 | `EarthTerrain.LATTICE_STEP` | `4` | Plots between the points a tile reads its shares at: 4 (120 m), so a tile's 34 x 34 region holds 10 x 10 of them on the world's own grid of them (spec-earth 3: a 10 x 10 lattice). |
| 193 | `EarthTerrain.LATTICE` | `World.TILE / LATTICE_STEP + 2` | Points a side of a tile's lattice: TILE / LATTICE_STEP + 2, 10 - the region's ring and its far edge. |
| 196 | `EarthTerrain.RIVER_MIN_HALF` | `0.75` | A river's least half-width, in plots: 0.75, so a 30 m river - half a plot each side of its line - leaves no gaps (spec-earth 3). |
| 199 | `EarthTerrain.SNAP_PLOTS` | `167` | How far the founding snaps from the spot, in plots: 167 (5 km, the founding card's reach, spec-earth 2); a spot with no site within it has none, and E4's screen refuses it. |
| 202 | `EarthTerrain.BEYOND_PLOTS` | `1667` | How far past the spot the nearest buildable ground is looked for when the snap finds none (ET2, spec-earth2-terrain 3.4): 1,667 plots (50 km); past it the founding card says there is none. |
| 205 | `EarthTerrain.BEYOND_STEP` | `8` | The step of that search (ET2): a lattice of spots 8 plots (240 m) apart, each tried by the founding's test 1, so the nearest it reports is within a lattice step of the true nearest - cheap where th... |
| 208 | `EarthTerrain.CELL_SAMPLES` | `4` | Points a side of a world cell the cells pass reads: 4, so 16 a cell of the 4' layers (spec-earth 4). |
| 211 | `EarthTerrain.FAR_STRIDE` | `256` | The stride, in plots, from which a region reads the 4' far layers instead of the 15" shares: 256 (7.68 km, a district), about a 4' cell's 7.4 km (★E2: wider pixels would inflate a 15" tile for ever... |
| 214 | `EarthTerrain.WATER_STRIDE` | `16` | The stride under which a region draws rivers and small lakes: 16 plots (480 m), the noise's widest octave (★E2): past it a pixel is wider than any detail of the ground's own, and the far view's riv... |
| 223 | `EarthTerrain.WATER_PAD_DEG` | `0.25` | How far round a district its rivers are looked for, in degrees of latitude: 0.25 (27.8 km; ★E2) - a river is stored in every 1-degree tile its centreline's box touches, so a district reads the tile... |
| 226 | `EarthTerrain.WIDEST_RIVER_M` | `40_650` | The widest river in earth-1, in metres: 40,650, measured over all its tiles (EarthTerrainCheck 9 holds it, and WATER_PAD_DEG to it, when the file is named). |
| 229 | `EarthTerrain.TILES_KEPT` | `1024` | Tiles of ground kept for terrainAt(), lineTerrain() and the snap: 1,024 (1 MB), the snap's 5 km disc and a screen's worth. |
| 232 | `EarthTerrain.DISTRICTS_KEPT` | `256` | Districts' rivers and small lakes kept, projected: 256. |
| 235 | `EarthTerrain.DATA_TILES_KEPT` | `64` | 1-degree tiles' rivers and small lakes kept, projected: 64. |
| 247 | `EarthTerrain.RELIEF_SHIFT` | `2` | How much wider the relief's noise is than the cover noises' (ET1, spec-earth2-terrain 3.1): its octaves are theirs scaled by 2^RELIEF_SHIFT, 2^3 plots (240 m) down to 2^6 (1.9 km) at 2, so a slope ... |
| 250 | `EarthTerrain.RELIEF_SCALE` | `1<<RELIEF_SHIFT` | The relief noise's lattice scale as a divisor of plot coordinates: 2^RELIEF_SHIFT, 4 (the noise is the cover noises' at x / 4, y / 4). |
| 253 | `EarthTerrain.RELIEF_KEPT` | `1024` | Tiles of relief kept for reliefAt() and tileRelief(): 1,024 (1 MB), a tile of ground's partner (TILES_KEPT). |
| 256 | `EarthTerrain.FAR_BLOCK` | `16` | Cells a side of the block a far pixel reads the slope of from FAR_STRIDE: 16 (4', 7.4 km), the 4' far layers' cell - 15 of them a side of a 1-degree tile, 225 a tile. |
| 259 | `EarthTerrain.F_COAST` | `0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL, F_ICE = 0x1CE5L, F...` | The fields' keys: what makes one class's noise another's. |
| 262 | `EarthTerrain.F_RELIEF_DITHER` | `0x4E11D17L` | The stream a far pixel's relief dither is drawn from: the camera's dither (the forest's) remixed with it, for the two must not be one number, or the steep ground would be the forested. |
| 265 | `EarthTerrain.SHARE_AREA` | `(double) EarthData.SHARE_LEVELS * EarthData.SHARE_LEVELS` | A share layer's levels squared, 15 x 15: what bilinearProduct() of two of them is over to be a share of the area (ET1). |
| 268 | `EarthTerrain.QUANTILE_SALT` | `0x0A1E5A3DL` | The stream the ranking table's samples are drawn from. |
| 271 | `EarthTerrain.AMP_N` | `new double [ NOISE_HI + 1 ]` | Each octave's amplitude over the sum of them, octave k at AMP_N[k]. |
| 274 | `EarthTerrain.SLOPE_F` | `new double [ 256 ], SLOPE_S = new double [ 256 ], SLOPE_V = new double [ 256 ]` | A slope byte's shares of the land at or above foothill, steep and very steep, by byte (ET1): EarthData's decoders, the steep share held within the foothill share and the very steep within the steep... |
| 277 | `EarthTerrain.SMOOTH` | `new double [ NOISE_HI + RELIEF_SHIFT + 1 ][]` | The smoothstep at a plot's centre within octave k's lattice cell, SMOOTH[k][f] for f = 0 .. |
| 302 | `EarthTerrain.SOURCES` | `new HashMap<>()` | The readers use() has made available, by their identity: where source() looks for a key's data first. |
| 347 | `EarthTerrain.COAST` | `0, FRESH = 1, FOREST = 2, BLOB = 3, ICE = 4, DESERT = 5, SHALLOW = 6, RELIEF = 7` | Each noise field's row in os: the coast's, fresh water's, the forest's, the small lakes' blobs', (0.7.127) the ice's and the desert's edges', and (ET1) the shallow sea's and the relief's. |
| 450 | `EarthTerrain.SPHERE_GAIN` | `Math.sqrt(35.0 / 26)` | The gain on each octave's deviation from its mean in the sphere's noise: sqrt(35 / 26), 1.160 (MS3). |
| 453 | `EarthTerrain.LATTICE_Z` | `0x165667B19E3779F9L` | The third lattice coordinate's hash constant, beside World.lattice()'s two: an odd number the other two are not. |
| 664 | `EarthTerrain.Scratch.RING` | `World.TILE + 2, RING_AREA = RING * RING, AREA = World.TILE * World.TILE` | A tile with its ring of one plot each side, RING plots a side (RING_AREA in all); a tile alone, AREA plots. |
| 713 | `EarthTerrain.SCRATCH` | `ThreadLocal.withInitial(Scratch : : new)` | Each thread's own working arrays and readers, so tiles drawn on several threads at once share none. |
| 1305 | `EarthTerrain.LANDMASS_REACH` | `8` | How many 15" cells either way (east-west and north-south, about 3.7 km north-south) a dry plot on a cell the layer calls no land looks for the nearest land cell: 8. |
| 1308 | `EarthTerrain.RESOLVED_KEPT` | `65_536` | Cells whose nearest land cell is kept (resolvedCell()) before the memory is cleared: 65,536. |
| 1639 | `EarthTerrain.FarShares.OF` | `new WeakHashMap<>()` |  |
| 1641 | `EarthTerrain.FarShares.NONE` | `new byte [ 0 ]` | A tile built and found flat or without land, kept in place of an array. |
| 1734 | `EarthTerrain.NO_WATER` | `new Water()` | A district's water where it lies off the world or off the planet: no river and no small lake. |
| 1848 | `EarthTerrain.DITHER_STRIDE` | `1<<NOISE_HI` | A far node's forest is dithered by a hash from this stride, in plots: 2^NOISE_HI, 16 (480 m) - where no octave of the forest's noise is wider than a pixel, so the ranked noise stands at its mean an... |
| 1851 | `EarthTerrain.FAR_RIVER_HALF_PX` | `RIVER_MIN_HALF` | A river's least half-width on a far node, in pixels: RIVER_MIN_HALF, 0.75 - a far pixel is to the node what a plot is to a tile, so a river is stroked at least 1.5 px wide, past the 0.71 px (half a... |
| 1866 | `EarthTerrain.FAR_RIVER_SHARE` | `30.0 / 2500` | The least width, as a share of a far pixel, at which a river is stroked: 30 / 2,500, 0.012 (★E5) - the narrowest river in the data, GRWL's 30 m (spec-earth 1), at the study's far render of 2.5 km a... |
| 1869 | `EarthTerrain.FAR_BLEND_PX` | `8` | From WATER_STRIDE a far node's pixels take their latitude and longitude from blocks of this many a side, each blended from its four corners (EarthProj.blend(), the tile's way): 8 - measured, at wor... |
| 1881 | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | `64` | From this stride, in plots, a far node places a 1-degree tile's river vertices and small lakes bilinear between the tile's four corners, projected exactly, rather than projecting each: 64 (★E5) - m... |
| 1884 | `EarthTerrain.NODE_EDGE_POINTS` | `32` | Points along each edge of a far node its box of 1-degree tiles is found from: 32 - at the widest node (1,024 plots a pixel, 3,932 km a side) one every 123 km, where a straight edge strays from its ... |
| 1887 | `EarthTerrain.LONGEST_SEGMENT_M` | `17_915` | The longest river segment in earth-1, in metres on the sphere: 17,915, measured over all its tiles (MapCheck 10 holds it when the file is named). |
| 1899 | `EarthTerrain.RIM_M` | `(double) LONGEST_SEGMENT_M * LONGEST_SEGMENT_M /(4 * EarthProj.RADIUS_M)` | A river segment with an end this close to the planet's rim is not stroked, in metres: LONGEST_SEGMENT_M^2 / 4R, 12.6 (★E5). |
| 1902 | `EarthTerrain.F_DITHER` | `0xD17E4L` | The far view's dither's stream. |
| 2037 | `EarthTerrain.FAR_COUNTS` | `ThreadLocal.withInitial(() -> new long [ 3 ])` | How many river segments and small lakes the last far node on this thread drew from, and how many 1-degree tiles it read: what a harness prints. |
| 2296 | `EarthTerrain.Camera.NO_SHIFT` | `{ 0 }` | The one shift a camera whose x does not wrap draws a shape at: none, the shape where it is. |
| 2376 | `EarthTerrain.GLOBE_DITHER_DEG` | `4.0 / 60` | The globe's dither cell (spec-globe 3.5: "the dither's hash comes from a fixed latitude-longitude grid at the pixel's scale, so the forest does not shimmer as the globe turns"): rows of GLOBE_DITHE... |
| 2379 | `EarthTerrain.DITHER_WIDENS` | `new double [ 30 ]` | The latitudes past which a dither cell is 2^m times as wide in longitude as tall, m = 1, 2, ...: where cos lat falls to 2^-m. |
| 2386 | `EarthTerrain.F_GLOBE_DITHER` | `0x610BED17L` | The globe's dither's stream (the far node's F_DITHER is the plane's, by plot and the world's seed). |
| 2409 | `EarthTerrain.M_A_DEGREE` | `Math.PI * EarthProj.RADIUS_M / 180` | How many metres of ground a degree of latitude is on the sphere: pi R / 180. |
| 2773 | `EarthTerrain.FOOTPRINT_POINTS` | `3` | Points a side of a 1-degree tile its footprint is read at: 3, its corners, the middles of its edges and its centre. |
| 2849 | `EarthTerrain.COAST_BELOW` | `(int) Math.ceil(SEA_LEVEL * EarthData.SHARE_LEVELS)` | The sea layer's levels under which a 15" cell is the coast's land side: the share under SEA_LEVEL, 0 to 7 of 15 - the land of the landmass layer (tools/earth/FORMAT-2.txt). |
| 2852 | `EarthTerrain.SEA_LEAST_DEPTH_M` | `1` | The shallowest a sea plot is, metres: 1 (FORMAT-2: "a sea plot is never shallower than 1 m in the game"), so World.depthAt() over 0 is still how a sea plot is told from land. |
| 2855 | `EarthTerrain.DEPTHS_KEPT` | `4096` | Depths kept, by plot: 4,096 (a field's depth is asked again every time the city's ground changes). |

### EconomyManager.java ([map](map/EconomyManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 295 | `EconomyManager.PASS_THROUGH` | `1 / 36.0` | How much of the gap, in logs, between the D$ the city's goods were last struck at and today's D$ they close in a month: a thirty-sixth, so half of a move in two years and 29% of it in the first - t... |
| 838 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, BuildingTy...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 852 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1061 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 1063 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 1065 | `EconomyManager.NRRP_CAP` | `6.3` | ...and its most a unit, in founding thousands: $6,300. |
| 1067 | `EconomyManager.NRRP_FULL_BELOW` | `350` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 1069 | `EconomyManager.NRRP_NONE_FROM` | `450` | ...and none for a unit worth this or more: $450,000. |
| 2569 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### Education.java ([map](map/Education.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 251 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 264 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 267 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 285 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 294 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 1023 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

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
| 1020 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 1023 | `Equity.SLOTS_BEFORE_PAID` | `SLOTS_BEFORE_DESK + 2` | ...and before the dividends actually paid (0.7.12 round 2). |
| 1026 | `Equity.SLOTS_BEFORE_CITY` | `SLOTS_BEFORE_PAID + RECORD_MONTHS + 1` | ...and before the city's fund (0.7.14): the ring of dividends paid and its count, appended. |
| 1029 | `Equity.SLOTS_BEFORE_PAID_IN` | `SLOTS_BEFORE_CITY + 3` | ...and before its paid-in capital (0.7.75): the city's shares, its rescue book and what it has been paid, appended (0.7.14). |
| 1032 | `Equity.SLOTS` | `SLOTS_BEFORE_PAID_IN + 3` | Its founders' book, what its buybacks paid and whether the two were derived, appended (0.7.75, R3; SAVE_FORMAT 33). |
| 1035 | `Equity.PAID_IN_FORMAT` | `33` | The first save format that keeps a company's paid-in capital (0.7.75, R3): an older save's is derived at the load (restore(), SectorBooks.derivePaidIn()). |

### Exchange.java ([map](map/Exchange.md))

| line | constant | value | says |
|---:|---|---|---|
| 133 | `Exchange.SPREAD` | `.02` | The desk's ask over its bid, as a share of fair value. |
| 136 | `Exchange.STALE_MARK_MONTHS` | `12` | A share's last trade marks the city's holding for a year; older, the register's own fair value, which the rule asks at and bids TreasuryFund.RULE_PREMIUM over (C4). |
| 139 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 142 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 145 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a yield at least this far over the deposit rate, annual. |
| 153 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of its costs before it buys back: operating cost and, since round 2 of 0.7.11, its debt service - the interest and the principal it repaid (Companies.monthlyDebtSer... |
| 156 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 174 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 189 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 192 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 195 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). |
| 261 | `Exchange.POSITION_LIMIT` | `.25` | The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). |
| 272 | `Exchange.BOOK_LIMIT` | `.50` | ...and in all companies' together: the book's aggregate limit. |
| 275 | `Exchange.SPLIT_AT` | `100` | A share priced at this many times its founding price is split; at one over it, consolidated. |
| 285 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the market treats the company as worthless. |
| 301 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 321 | `Exchange.DESK` | `"bank"` | The bank's trading desk. |
| 323 | `Exchange.WORLD` | `"world"` | The world's investors. |
| 325 | `Exchange.EMIGRANTS` | `"emigrants"` | The month's leavers, selling on the way out. |
| 327 | `Exchange.CELL` | `BondMarket.CELL` | One household cell: this, then its key (the bond market's prefix, deliberately). |
| 329 | `Exchange.FUND` | `BondMarket.FUND` | The city's fund, by its rule (0.7.14; TreasuryFund) - the bond market's name, deliberately. |
| 331 | `Exchange.FUND_HAND` | `BondMarket.FUND_HAND` | ...and by the player's hand: its own name, so the two never trade with each other and the hand's sale can reach the rescue book. |
| 734 | `Exchange.BOUND_CAPITAL` | `0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3` | Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. |
| 1361 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3, BY_FUND = 4` | Who sells, by class: the desk, the world, the leavers, a household cell, and since 0.7.14 the city's fund - for the fill rate by seller. |
| 1689 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 1691 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

### Expectations.java ([map](map/Expectations.md))

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Expectations.ADAPT_MONTHS` | `12` | Months recent inflation is smoothed over, as an exponential average: a twelfth of the gap a month. |
| 76 | `Expectations.TOLERANCE` | `.01` | How far smoothed inflation may sit from the target, a fraction a year, and still count as on target: one point. |
| 79 | `Expectations.MISS_SCALE` | `.04` | The miss past TOLERANCE that costs credibility at the full LOSS_MONTHS speed: four points more, five off target in all. |
| 82 | `Expectations.LOSS_MONTHS` | `24` | Months over which credibility falls toward KMIN on a full miss nobody leans against: a twenty-fourth of the distance a month, half of it gone in about seventeen. |
| 85 | `Expectations.GAIN_MONTHS` | `60` | Months over which credibility climbs toward KMAX on target: a sixtieth of the distance a month, about five years to rebuild. |
| 88 | `Expectations.KMIN` | `.25` | The least anyone believes the bank: a quarter of expected inflation stays the target however long it misses (.1 was measured to lock the founding at 10%). |
| 91 | `Expectations.KMAX` | `.95` | The most anyone believes it: a twentieth of expected inflation is always recent experience. |
| 94 | `Expectations.KSEED` | `.80` | Where a new city, and a save from before the anchor, starts: a bank believed more than not, with something to earn. |
| 97 | `Expectations.EXPECTED_FLOOR` | `-.01` | The least inflation anybody expects, a fraction a year: the floor under the deflation attractor (minus one per cent). |
| 227 | `Expectations.PEG_BREAK_DENT` | `.5` | The share of the way to KMIN that credibility falls the month a peg breaks (XR2, est.): a bank that promised a rate and could not keep it is believed half as much as it was above the floor. |
| 262 | `Expectations.SAVE_SLOTS` | `8` | The slots a save carries: credibility, smoothed, expected, level, seeded - then the lean, the level the month's constants are struck at, and (0.7.45) the month's move in credibility. |

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

### FloatingRateNote.java ([map](map/FloatingRateNote.md))

| line | constant | value | says |
|---:|---|---|---|
| 38 | `FloatingRateNote.MATURITIES` | `{ 2, 5 }` | The only terms a floating-rate note is issued at, in years (D25). |
| 41 | `FloatingRateNote.REFUSAL` | `"Floating-rate notes are issued at 2 or 5 years."` | What the treasury is told when it asks for any other term. |

### ForeignAccounts.java ([map](map/ForeignAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 108 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 129 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's deficit. |
| 132 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the month's deficit deep reserves can absorb. |
| 170 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 172 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 224 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 321 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 412 | `ForeignAccounts.TREND_LEVEL` | `1 / 6.0` | How much of a month's jump in parity the trend takes at once: Holt's level, so a steady inflation gap passes into the rate one for one and a single month's spike moves it a sixth of itself. |
| 415 | `ForeignAccounts.TREND_SLOPE` | `1 / 24.0` | ...and how fast its slope learns, a twenty-fourth of the level's change a month: about two years. |
| 418 | `ForeignAccounts.PERSISTENCE` | `2.5` | How many years the market expects a real rate gap to last, so a sustained point of gap holds the D$ about 2.5% dearer (or cheaper, when negative). |
| 421 | `ForeignAccounts.GAP_CAP` | `.35` | The most a real rate gap can move the level either way, a soft cap (tanh): markets do not believe a seventeen-point gap lasts. |
| 424 | `ForeignAccounts.GAP_IMPACT` | `.6` | The share of a change in the gap's level that the rate shows in its month: Kearns and Manners' 1.5% a point on the day (R16). |
| 427 | `ForeignAccounts.GAP_BUILD_HALF_LIFE` | `9` | ...and the rest builds with this half-life, in months, so the peak comes within one to three years: delayed overshooting (R15). |
| 430 | `ForeignAccounts.BALANCE_WEIGHT` | `.5` | How far a year's overall balance, against a year's GDP, moves the level: a deficit of a tenth of GDP cheapens the D$ 5%. |
| 433 | `ForeignAccounts.BALANCE_CAP` | `.20` | ...at most this far either way. |
| 436 | `ForeignAccounts.RISK_PREMIUM` | `.10` | An empty vault's discount on the D$, falling to nothing at THIN_COVER months of imports, which is its safe cover: one dial, one meaning (the oldest rule of thumb, R31). |
| 439 | `ForeignAccounts.MARKET_SPEED` | `.5` | How much of a change in the balance's and the cover's levels reaches the rate in its month. |
| 442 | `ForeignAccounts.MARKET_SIGMA` | `.022` | The market's own swing a month, about 8% a year: a real float's (Jerus, star 1). |
| 445 | `ForeignAccounts.MARKET_HALF_LIFE` | `42` | ...fading by half in this many months, the half-life of real currencies' gaps from parity (Rogoff, R1). |
| 448 | `ForeignAccounts.OPEN_ENOUGH` | `.10` | The openness, trade over GDP, at which the market and the fundamentals reach the rate in full; a less open city feels them in proportion. |
| 451 | `ForeignAccounts.INTERVENTION_IMPACT` | `2.0` | How far a purchase of dollars moves the rate: 2% per 1% of a year's GDP, a sale the other way (the IMF's 1.7-2.0%, R33; Jerus, star 7). |
| 454 | `ForeignAccounts.INTERVENTION_HALF_LIFE` | `6` | ...fading by half in this many months. |
| 1067 | `ForeignAccounts.BAND_WIDTH` | `.03` | Where a band opens, this share either side of its centre: the study's board 2 (est.; Singapore's band is thought to be about 2% either side, R36). |
| 1070 | `ForeignAccounts.MAX_BAND_WIDTH` | `.25` | The widest a band can be set, a share either side of the centre. |
| 1073 | `ForeignAccounts.MAX_BAND_SLOPE` | `.25` | The steepest a band's centre can crawl, a log a year, either way. |
| 1076 | `ForeignAccounts.VAULT_FLOOR` | `1` | Months of imports the vault must keep under a peg or a band: under it the hold gives way (the usual rule of thumb is three months of cover; the study's prototype breaks at one). |
| 1079 | `ForeignAccounts.OVERSHOOT` | `.25` | A broken peg lands at the shadow plus this share of the gap from the peg again, and fades as the market's swings do (RatesModel, the study's prototype). |
| 1082 | `ForeignAccounts.BREAK_BASE` | `.001` | A peg's chance of breaking by doubt in a month at no gap at all (0.1%). |
| 1085 | `ForeignAccounts.BREAK_SLOPE` | `.03` | ...rising by this much for each BREAK_GAP the shadow sits past the peg beyond the first BREAK_GAP (3% a month at a 20% gap). |
| 1088 | `ForeignAccounts.BREAK_GAP` | `.10` | ...the gap, a log, under which doubt adds nothing to BREAK_BASE (10%). |
| 1091 | `ForeignAccounts.PEG_SALT` | `0x5DEECE66D1B54A33L` | The salt that makes the peg's draws a stream of their own (odd). |
| 1430 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the float reads. |
| 2018 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the sp... |
| 2021 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 2037 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb - and ... |

### Formats.java ([map](map/Formats.md))

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |
| 35 | `Formats.WHOLE_DOLLARS_MOST` | `0x1p53` | The most dollars cash() prints whole (0.7.54): 2^53, about nine quadrillion, the last whole number a double holds exactly. |

### Founding.java ([map](map/Founding.md))

| line | constant | value | says |
|---:|---|---|---|
| 107 | `Founding.INSANE_LAND_COUPON` | `.03` | Insane's coupon on the land it owes for, a year: 3%, Jerus's number. |
| 110 | `Founding.INSANE_LAND_YEARS` | `20` | Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the six bond maturities (LongTermBond.MATURITIES). |
| 130 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - two-thirds of the founding village at a new city's invoices (three-quarters until the builders' sales tax went into them, 0.7.19), so the city borrows from it... |
| 133 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 136 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 139 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 213 | `Founding.MIN_CASH` | `6_000` | The least a city may be founded with in its treasury, in thousands: D$6M, ten houses and a shop at a new city's invoices - D$5.37M since the builders' sales tax went into them (0.7.19; it was D$5M,... |
| 216 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 219 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 222 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 227 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 230 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 235 | `Founding.DEFAULT_WORLD_SEED` | `4127` | The world a founding stands on when nobody rolls another: 4127, the map mockup's default seed (city-map.html), so every harness and the playtest found on the same ground. |
| 238 | `Founding.ROLLED_SEED_MAX` | `999_999_999` | The largest seed the founding screen's dice rolls: 999,999,999, nine digits a player can read back and type. |
| 496 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "Constructi...` | The founding village: the playtest's hand-built settlement, name and count. |
| 500 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

### FreightView.java ([map](map/FreightView.md))

| line | constant | value | says |
|---:|---|---|---|
| 228 | `FreightView.RAIL_BILLED_AS` | `"Rail freight"` | The line the railway's bill is raised under on a shipper's input rows (Freight.haul()). |

### FuelSplit.java ([map](map/FuelSplit.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `FuelSplit.LAST_FUEL_FORMAT` | `33` | The last save format that can carry FUEL: 0.7.75's. |
| 66 | `FuelSplit.FUEL` | `"FUEL"` | FUEL's saved name, which no Good carries any more. |
| 69 | `FuelSplit.HELD_AT` | `15` | Where the national accounts' goods held begin in the saved array (EconomyManager.getNationalAccountsState()). |

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
| 36 | `FundView.STALE_MONTHS` | `3` | A share's last trade older than this many months is called stale on every page that shows it (the spec's B3: a price is its last trade, however old - and since 0.7.48 the city's own holding is mark... |
| 39 | `FundView.MIN_RECORDED_PRICE` | `.01` | The least a price per founding share, as the history records it to four places, can be and still carry three significant figures: a move off less is not shown (the spec's B6 - a consolidated share ... |
| 42 | `FundView.FOLD_UNDER` | `1` | A holding worth less than this, money, is folded into "and N more" on the Portfolio page: a thousand dollars. |
| 45 | `FundView.MOVE_MONTHS` | `12` | The months a hit's sparkline and its move cover: a year. |
| 48 | `FundView.SHARE` | `"share", RESCUE = "rescue", BOND = "bond", PREFERRED = "preferred", WARRANTS = "warrants"` | What a holding is. |
| 51 | `FundView.CLOSED` | `"closed"` | A search hit that is a lot the fund closed on a bond no longer on any book: found only when named (the spec's D1). |
| 396 | `FundView.HELD` | `"YOUR HOLDINGS", SHARES_SECTION = "SHARES", BONDS_SECTION = "BONDS"` | What a search hit is for: the market's sections when the box is empty. |
| 799 | `FundView.TRADES` | `"Trades", INCOME = "Income", MONEY = "Money in and out", EVENTS = "Events"` | The activity page's chips: what each kind of row is filed under. |

### Game.java ([map](map/Game.md))

| line | constant | value | says |
|---:|---|---|---|
| 243 | `Game.MARKET_SEED_SALT` | `0x4D41524B45545852L` | Salt for the D$'s market seed, so the market's path is not the world's own seed spelled again ("MARKETXR"). |
| 732 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 854 | `Game.FOUNDING_YARD` | `"Builders' Yard"` | The builders' building a city is founded with (PT1-M4): see foundingYard(). |
| 1130 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 1133 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town... |
| 1136 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2498 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 5320 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 5323 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 6898 | `Game.NO_GROUND_AHEAD` | `new GroundAhead(0, 0, 0, 0)` |  |
| 7100 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 8254 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 8783 | `Game.BRIDGE_FOR_GOOD` | `"A bridge stands for good: taking one down is not built yet"` | Why a bridge cannot be demolished (ET6-C): one that stands is for good until the demolition of a bridge is built. |
| 10186 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 10341 | `Game.NO_DOLLAR_FLOAT_OR_LINKED` | `"Floating-rate notes and inflation-linked bonds are issued at home only."` | What the treasury is told when it asks for a dollar floater or linker. |
| 10377 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 10508 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 12292 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 12304 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 12307 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 12322 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 12333 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003). |
| 13942 | `Game.SHORTFALL_DUST` | `1e-9` | A shortfall smaller than this share of an order's cost is rounding, not a want of cash: buildFor() borrows nothing for it. |
| 14332 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 62 | `GameLog.LOG_FILE` | `"log.txt"` |  |
| 63 | `GameLog.PREVIOUS_LOG_FILE` | `"log-previous.txt"` |  |
| 76 | `GameLog.MAX_BYTES` | `2_000_000` | The cap on ordinary output: once either stream has written more than this to the file, the file says so and from then takes failures alone, FAILURE_BYTES more of them (0.7.50; until then, nothing m... |
| 82 | `GameLog.FAILURE_BYTES` | `200_000` | How much more the file takes past the cap, in failures alone (0.7.50): a tenth of the cap. |
| 89 | `GameLog.FIRST_LINES` | `4` | How many of a failure's lines make it the same failure as one already written past the cap: what it says (without the time), the exception and its message, and the first two frames. |
| 91 | `GameLog.STAMP` | `DateTimeFormatter.ofPattern("HH:mm:ss")` |  |

### GamePrefs.java ([map](map/GamePrefs.md))

| line | constant | value | says |
|---:|---|---|---|
| 32 | `GamePrefs.FILE` | `"settings.json"` |  |
| 118 | `GamePrefs.DEFAULT_PINNED_LEFT` | `"realGdp"` | The two small charts pinned at the top of the Reports page, by series name - the name HistorySave and the page's own list know a line by. |
| 121 | `GamePrefs.DEFAULT_PINNED_RIGHT` | `"population"` | ...and the right-hand one, the population. |

### GameVersion.java ([map](map/GameVersion.md))

| line | constant | value | says |
|---:|---|---|---|
| 7372 | `GameVersion.VERSION` | `"0.8.69"` | Bump on release. |
| 8214 | `GameVersion.SAVE_FORMAT` | `48` | The save shape. |
| 8256 | `GameVersion.OLDEST_LOADABLE_FORMAT` | `48` | The oldest save format this build loads: a batch that bumps SAVE_FORMAT because an older save would load wrongly raises this to the new format in the same batch, so that save is refused cleanly (is... |
| 8264 | `GameVersion.NAME` | `"Civic Ledger"` | The game's name, on the title screen and in the window's title (BR1, 2026-10-27: Jerus chose Civic Ledger, the name itch.io and the releases already wore). |

### Gateways.java ([map](map/Gateways.md))

| line | constant | value | says |
|---:|---|---|---|
| 27 | `Gateways.BEARING_STEP` | `SeaRoutes.BEARING_STEP` | The bearings tried for a gateway, every this many degrees: 5 (spec 4.1; SeaRoutes.BEARING_STEP). |
| 30 | `Gateways.RAY_CELLS` | `SeaRoutes.RAY_CELLS` | The longest run of land a bearing is measured to, in cells: 400, 192 km (SeaRoutes.RAY_CELLS; the prototype's). |
| 33 | `Gateways.PLACE_CELLS` | `SeaRoutes.RAY_CELLS` | How far the Earth's rule looks for a larger place, in cells: the offing's 400, 192 km (SeaRoutes.RAY_CELLS); only the land on the place's own bearing is read, and only out to the reach. |
| 36 | `Gateways.GATEWAY_M` | `SeaRoutes.OFFING_M` | How far past the city's radius the land must run for a gateway, in metres: 10 km, the offing's own margin (SeaRoutes.OFFING_M), so an island founding with less has no gateway (spec 4.1). |

### Geology.java ([map](map/Geology.md))

| line | constant | value | says |
|---:|---|---|---|
| 114 | `Geology.W_IRON` | `{ 1, 4, 1, 0.5, 0, 0.5, 1 }` | Iron's weight by class: four on a shield, nothing on a shelf, half in a rift or basin (spec-earth 4). |
| 117 | `Geology.W_OIL` | `{ 0.5, 0.05, 1, 2, 3, 2, 0.5 }` | Oil's weight by class: three on a shelf, two in a rift or basin, a twentieth on a shield (spec-earth 4). |
| 120 | `Geology.W_COAL` | `{ 0.5, 0.1, 1, 1, 0, 2, 0.5 }` | Coal's weight by class: two in a basin, a tenth on a shield, nothing on a shelf (spec-earth 4). |
| 123 | `Geology.W_COPPER` | `{ 1, 1, 4, 1, 0, 0.3, 1 }` | Copper's weight by class: four in a fold belt, three tenths in a basin, nothing on a shelf (spec-earth 4). |
| 126 | `Geology.W_URANIUM` | `{ 1, 2, 0.5, 1, 0, 1.5, 1 }` | Uranium's weight by class: two on a shield, one and a half in a basin, half in a fold belt (spec-earth 4). |
| 129 | `Geology.W_STONE` | `{ 1, 2, 2, 1, 0, 1, 1 }` | Stone's weight by class: two on a shield or in a fold belt, nothing on a shelf (spec-earth 4). |
| 143 | `Geology.Z_IRON` | `0.9706` | Iron's Z: the land over the land x iron's weight (a shield's four, an allowance elsewhere, a district's twenty) north of ANTARCTIC_LAT, 0.9706 on earth-1 with the hand tables, EG1 (E3's 0.7219). |
| 146 | `Geology.Z_OIL` | `0.3616` | Oil's Z: the land over the land x oil's weight (the allowance and the known petroleum a km2) north of ANTARCTIC_LAT, 0.3616 on earth-1 with the hand tables, EG1 (E3's 0.6033, with its petroleum fac... |
| 149 | `Geology.Z_COAL` | `0.7884` | Coal's Z: the land over the land x W_COAL north of ANTARCTIC_LAT, 0.7884 on earth-1 with the hand tables, EG1 (the Sydney patch moves a little land). |
| 152 | `Geology.Z_COPPER` | `0.7323` | Copper's Z: the land over the land x W_COPPER north of ANTARCTIC_LAT, 0.7323 on earth-1 with the hand tables, EG1 (E3's 0.7322). |
| 155 | `Geology.Z_URANIUM` | `0.7518` | Uranium's Z: the land over the land x W_URANIUM north of ANTARCTIC_LAT, 0.7518 on earth-1 with the hand tables, EG1. |
| 158 | `Geology.Z_STONE` | `0.6915` | Stone's Z: the land over the land x W_STONE north of ANTARCTIC_LAT, 0.6915 on earth-1 with the hand tables, EG1. |
| 161 | `Geology.ANTARCTIC_LAT` | `- 60` | The latitude south of which Z's sums leave the ground out: 60 S, the study's "Antarctica excluded" (★E3-4: its ice holds what no city founded on the ice can reach, and E4 refuses ice). |
| 164 | `Geology.OIL_SCALE_MMBOE` | `1000` | E3's petroleum factor's scale, in million barrels of oil equivalent: 1,000 - oil was x (1 + log10(1 + known / 1,000)) (spec-earth 4): x2.13 under the Anglo-Dutch Basin's 12,640. |
| 178 | `Geology.OIL_ALLOWANCE` | `0.1` | A province's oil without known petroleum: a tenth of its class's weight (EG1; spec-earth2-terrain 8.5, est.). |
| 181 | `Geology.IRON_ALLOWANCE` | `0.1` | Iron off a shield and outside a recorded district: a tenth of its class's weight (EG1, est.). |
| 184 | `Geology.IRON_DISTRICT_WEIGHT` | `20` | Iron's weight in a recorded iron district: twenty, five times a shield's (EG1, est.). |
| 194 | `Geology.COKING_RATIO` | `{ 1, 0.5, 3, 1, 1, 1, 1 }` | Coking coal's odds ratio by class: three in a fold belt, a half on a shield, one elsewhere (spec-materials 2.4). |
| 197 | `Geology.LIMESTONE_RATIO` | `{ 1, 0.25, 0.5, 1, 2, 2, 1 }` | Limestone's odds ratio by class: two in a basin or on a shelf, a half in a fold belt, a quarter on a shield (spec-materials 2.4). |
| 200 | `Geology.COKING_ODDS` | `0.135296` | Coking coal's odds where the ratio is one: 0.135296 on earth-1 with the hand tables, EG1 (six figures), so its Earth-wide share is THERMAL's complement, 15%. |
| 203 | `Geology.LIMESTONE_ODDS` | `4.10885` | Limestone's odds where the ratio is one: 4.10885 on earth-1 with the hand tables, EG1 (six figures), so its Earth-wide share is TYPE_SHARES' 70%: .892 in a basin, .507 on a shield. |
| 214 | `Geology.HAND` | `""" 9 \| 5080 \| Chukchi Shelf OCS \| ocean \| shelf \| a shelf: "OCS" matched the ocean rul...` | The hand-checked classes, a row a province: row \| code \| name \| the rules' class \| the class here \| why. |
| 508 | `Geology.TABLE` | `parse()` | The hand-checked table, parsed, in its rows' order. |
| 537 | `Geology.KNOWN_HAND` | `""" 5001 \| Northern Alaska \| 24000 \| Prudhoe Bay, Kuparuk and the North Slope's gas(est...` | The known petroleum the file holds none of, a province a row: code \| name \| known MMBOE \| why. |
| 584 | `Geology.KNOWN` | `parseKnown()` | The known hand table, parsed, in its rows' order. |
| 609 | `Geology.DISTRICT_HAND` | `""" Hamersley Province(Pilbara) \| -22.7 \| 118.6 \| 180 \| Tom Price, Paraburdoo, Newman, ...` | The recorded iron districts, a row each: name \| latitude \| longitude \| radius km \| what. |
| 712 | `Geology.DISTRICTS` | `parseDistricts()` | The recorded iron districts, parsed, in their rows' order (districtAt() returns an index into it). |
| 739 | `Geology.PATCH_HAND` | `""" Sydney coalfield \| 46.05 \| 46.35 \| -60.45 \| -59.75 \| 5220 \| the onshore Sydney coal...` | The patches over the province grid, a row each: name \| south \| north \| west \| east \| code \| why. |
| 747 | `Geology.PATCHES` | `parsePatches()` | The patches, parsed, in their rows' order. |
| 839 | `Geology.DISTRICT_BIN_MARGIN` | `0.01` | The margin a district's bins are widened by, in degrees: a hundredth, so a point on a disc's edge is never missed for rounding. |
| 842 | `Geology.DIST_COS` |  | Each district's centre as a unit vector, and the cosine of its angular radius. |
| 845 | `Geology.DISTRICT_BINS` |  | The districts each 1-degree bin holds (row 90 + latitude's floor, column 180 + longitude's floor, row x 360 + column), null for a bin that holds none. |
| 899 | `Geology.PATCH_BOX` | `patchBoxes()` | Each patch's box as {south, north, west, east}, in PATCHES' order. |
| 902 | `Geology.PATCH_ALL` | `patchAll()` | ...and the box that holds them all, {south, north, west, east}: the cheap test provinceAt() makes first. |
| 929 | `Geology.OF` | `new IdentityHashMap<>()` | Each reader's geology, made once (of()). |

### GlobeFrame.java ([map](map/GlobeFrame.md))

| line | constant | value | says |
|---:|---|---|---|
| 105 | `GlobeFrame.PICTURE` | `0, GROUND = 1, PLANE = 2` | The tiers, by the scale (spec-globe 3.1): the planet's picture, the ground through the globe, the city's plane. |
| 108 | `GlobeFrame.HANDOFF_PLOTS` | `16` | The handoff from the globe to the city's plane, in plots a pixel: 16 (spec-globe 3.2, Jerus's Q6) - 0.48 km a pixel, where the two part by a third of a pixel on the expanded map, a power of two the... |
| 111 | `GlobeFrame.HANDOFF_KM` | `HANDOFF_PLOTS * World.PLOT_M / 1000` | ...in km a pixel: HANDOFF_PLOTS x World.PLOT_M, 0.48. |
| 114 | `GlobeFrame.REACH_KM` | `300` | Closer than the handoff the view draws the city's plane while its centre is within this of the city, in km, and the GROUND tier beyond it - it stops no wheel and no pan since MS1 (spec-globe 3.4's ... |
| 117 | `GlobeFrame.REACH_MARGIN_KM` | `50` | ...or within this of the city's own ground and its offers, in km, where they reach farther than REACH_KM: 50 (spec-globe 3.4's est., ★E8 - the ground the land office shows round what it sells; wher... |
| 120 | `GlobeFrame.ROLL_NOTCHES` | `2` | The roll eases from north up to the plane's up over this many notches of the wheel before the handoff: 2 (spec-globe 3.3's est., ★E8) - at most 2.7 degrees within REACH_KM of a city at Halifax's la... |
| 123 | `GlobeFrame.ZOOM_STEP` | `MapFrame.ZOOM_STEP` | A notch of the wheel zooms by this much, at the pointer: the map's, MapFrame.ZOOM_STEP. |
| 126 | `GlobeFrame.FIT_SHARE` | `0.9` | Fit shows the whole planet across this share of the view's shorter side: 0.9, as the founding globe's widest view (spec-globe 3.1: 17.6 km a pixel on the expanded map). |
| 129 | `GlobeFrame.POLE_LAT` | `89` | The view's centre is turned no nearer a pole than this, in degrees: 89, the founding globe's (FoundingScreen's turn()) - at the pole every longitude is the centre's and north has no direction. |
| 132 | `GlobeFrame.PICTURE_FROM_KM` | `GlobePicture.texelKm(GlobePicture.ARCMIN)` | The PICTURE tier draws down to the planet's picture's texel, in km a pixel: a GlobePicture.ARCMIN texel's height on the ground, 7.41 (spec-globe 3.1). |
| 135 | `GlobeFrame.CROSSFADE_NOTCHES` | `1` | The swap crossfades over this many notches of the wheel inside the handoff: 1 (spec-globe 3.2's est., ★E9) - the globe laid over the plane from whole at the handoff to gone a notch in, hiding the t... |
| 138 | `GlobeFrame.ROLL_EASE_KM` | `REACH_MARGIN_KM` | Past the reach the roll eases back to north up over this many km (★E9; E8's ★3): REACH_MARGIN_KM, the margin the reach keeps round the city's ground - so a pan across the reach's edge at the plane'... |
| 141 | `GlobeFrame.EDGE_FROM_PX` | `4` | The city's edge is drawn through the globe's camera once the city is at least this many pixels across: 4 (spec-globe 3.7's est.) - under it the ring alone marks it. |
| 144 | `GlobeFrame.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding screen's globe, in pixels: 560 x 270 (ui/FoundingScreen's right column, 0.7.121; GlobeFrame's since 0.7.135, so a harness draws what it draws). |
| 162 | `GlobeFrame.FOUNDING_HANDOFF_KM` | `halfPixelKm(FOUNDING_W, FOUNDING_H)` | The founding globe hands its view to the ground drawn round its centre at this, km a pixel (★E9, spec-globe 3.2's choice): halfPixelKm() of its canvas, 2.32 - under half a pixel at the swap, as the... |
| 695 | `GlobeFrame.SHAPE_HANDOVER` | `1.25` | The city's plane's shape distortion, k / h, past which the near view is drawn from the charts instead: 1.25 (MS3). |
| 698 | `GlobeFrame.SHAPE_BAND_KM` | `100` | The band past the hand-over over which the sphere's noise is blended in from the plane's, km along the ground: 100 (MS3) - the noise's widest octave is 480 m (the relief's 1.9 km), so over 100 km t... |
| 701 | `GlobeFrame.CHART_DEG` | `10` | The charts' anchors' lattice, degrees of latitude between rows (and about as many of longitude on the equator): 10 (MS3) - the view's chart is the anchor nearest its centre, within 1,300 km of ever... |
| 1077 | `GlobeFrame.AT_THE_CENTRE` | `" at the centre"` | What the globe's scale bar adds to its length (spec-globe 3.7: "true at the view's centre only, and says so"). |

### GlobePicture.java ([map](map/GlobePicture.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `GlobePicture.ARCMIN` | `4` | The picture's texel, in minutes of arc: 4, the far layers' own cell (spec-globe 3.6) - 5,400 x 2,700 texels, a byte each, 14.6 MB. |
| 67 | `GlobePicture.FIRST_ARCMIN` | `12` | The picture made first, in minutes of arc: 12, three far cells a side, a ninth of the texels (spec-globe 3.6's est., ★E8) - the founding globe's own picture's texel until 0.7.135, when it came to d... |
| 70 | `GlobePicture.BAND_DEG` | `10` | The picture is made a band of this many degrees of latitude at a time, one camera each: 10 (★E8) - each band reads the 1-degree tiles' rivers within WATER_PAD_DEG of it once, and the bands go one a... |
| 76 | `GlobePicture.CLASS_BITS` | `3` | The bits of a texel's code that hold its World class: 3 (World.CLASSES is 8, 1 << CLASS_BITS - checked below); its relief is above them. |
| 79 | `GlobePicture.CLASS_MASK` | `(1<<CLASS_BITS) - 1` | A code's class: its low CLASS_BITS bits. |
| 82 | `GlobePicture.CODES` | `World.RELIEFS<<CLASS_BITS` | How many codes there are: World.RELIEFS x World.CLASSES, 32 - the length of a table of colours a texel indexes (TileRaster.codeColours()). |
| 209 | `GlobePicture.KEPT` | `new HashMap<>()` | The pictures made, by the data's identity and texel: once for each Earth file, never saved. |
| 331 | `GlobePicture.SPACE` | `TileRaster.VOID` | Space round the globe: the map's own void (TileRaster.VOID), as the plane draws past the planet's rim. |
| 363 | `GlobePicture.MAKING` | `new HashMap<>()` | The identities whose pictures are being made, and who to tell as each is kept. |

### GoodsMarket.java ([map](map/GoodsMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 52 | `GoodsMarket.STOCK_RELEASE_MONTHS` | `6` | How many months it would take to release the whole stockpile into the market. |
| 55 | `GoodsMarket.NO_CEILING_MULTIPLE` | `2` | Where the price sits in a band with no ceiling: up to this multiple of the floor. |
| 90 | `GoodsMarket.TREND_MONTHS` | `36` | The longest window any good plans over - see Good.planningMonths() for how many of these months a good actually reads. |

### GridConversion.java ([map](map/GridConversion.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `GridConversion.CONVERTED` | `0` | The holding the converted ground is on the grid: 0, the centre's (purchases made after it are 1, 2, ...). |
| 68 | `GridConversion.SHARED_FROM` | `"0.7.58"` | The first build that held a field site by site, each site with the ground holding its own centre: 0.7.58 (batch J1c). |
| 71 | `GridConversion.SHARED_TO` | `"0.7.63"` | ...and the last: 0.7.63. |
| 74 | `GridConversion.WHOLE_PLOT` | `1e-9` | A billionth of a plot: a figure of ground within it of a whole number of plots is that number - square kilometres are stored as plots x World.KM2_PER_PLOT, which rounds. |

### GridOffers.java ([map](map/GridOffers.md))

| line | constant | value | says |
|---:|---|---|---|
| 55 | `GridOffers.SIDES` | `CityLand.SIDES` | Sides of the city: North, East, South and West, in CityLand's order. |
| 58 | `GridOffers.PLACES` | `6` | Places on a side, each with one offer standing: six, left to right facing out (Jerus, 2026-10-07). |
| 61 | `GridOffers.DEPTH_OVER_WIDTH` | `2` | An offer's rows deep over its blocks across: two, the long side outward - the largest six a side allow at 2:1 (spec-grid star 5). |
| 64 | `GridOffers.CLIP_TRIES` | `4096` | How many times a listing may clip against the standing offers: 4,096, far past the 24 there are, so a loop that never settles stops. |
| 67 | `GridOffers.OUT` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 } }` | Each side's outward step, {dx, dy}: North up, East right, South down, West left (y runs south). |
| 70 | `GridOffers.ACROSS` | `{ { 1, 0 }, { 0, 1 }, { - 1, 0 }, { 0, - 1 } }` | Each side's step across, left to right facing out. |

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
| 71 | `Healthcare.FOUNDING_CITY` | `1200` | How many residents the founding endowment was meant to serve. |
| 74 | `Healthcare.FOUNDING_PLOTS` | `2500` | Graves in the old churchyard. |
| 141 | `Healthcare.GENERAL_FEE` | `.010` |  |
| 142 | `Healthcare.CHILDCARE_FEE` | `.150` |  |
| 143 | `Healthcare.SENIOR_FEE` | `.300` |  |
| 180 | `Healthcare.BURIAL_FEE` | `3.000` |  |
| 181 | `Healthcare.CREMATION_FEE` | `.900` |  |
| 259 | `Healthcare.BURIAL_SAVING_MONTHS` | `120` | How long a household is assumed to be putting money aside for a funeral. |
| 278 | `Healthcare.MAX_BACKLOG_MONTHS` | `24` | How far behind a city can get before it starts improvising. |
| 318 | `Healthcare.CHILDCARE_SWING` | `40` | Children, and it is enormous - per Jerus, "really really really". |
| 339 | `Healthcare.SENIOR_SWING` | `1.35` | Seniors, and it stays gentle. |
| 355 | `Healthcare.ELDER_SWING` | `1.80` | And what it is worth to the over-85s, which is more. |
| 366 | `Healthcare.CHILDCARE_BIRTH_BONUS` | `1.0` | How much more a city with childcare gives birth. |
| 829 | `Healthcare.STRAINED` | `.90` | Past this share of the ovens' throughput, say so. |
| 832 | `Healthcare.PLOT_WARNING_MONTHS` | `60` | Warn once the ground will not last this long at the current rate. |
| 890 | `Healthcare.STATE_BEFORE_FULL_BILL` | `12` | How many figures the state carried before the full-service bill was appended (2026-09-19). |
| 893 | `Healthcare.STATE_BEFORE_COVERAGE` | `13` | ...and before the three coverages were, the same day. |
| 896 | `Healthcare.STATE_BEFORE_SERVED` | `16` | ...and before the three kinds' served counts were (A3, 2026-10). |

### HistorySave.java ([map](map/HistorySave.md))

| line | constant | value | says |
|---:|---|---|---|
| 87 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 969 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "deaths", "a...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 991 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

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
| 497 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 500 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 501 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 502 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 504 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 505 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 508 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 1100 | `HouseholdAccounts.STATE_SCALARS` | `19, STATE_ROWS = 20` | Scalars and row arrays in the statement's state since 0.7.49. |
| 1103 | `HouseholdAccounts.SCALARS_BEFORE_FUEL` | `18, ROWS_BEFORE_FUEL = 19` | ...and the shape before the households' fuel was a line on it (0.7.7 to 0.7.48). |
| 1106 | `HouseholdAccounts.SCALARS_BEFORE_ACCOUNT_FEES` | `17, ROWS_BEFORE_ACCOUNT_FEES = 18` | ...and the shape before the bank's account fee was a line on it (0.7.7). |
| 1109 | `HouseholdAccounts.SCALARS_BEFORE_HEALTH` | `16, ROWS_BEFORE_HEALTH = 15` | ...and the shape before the health premium was a line on it (2026-09-19). |
| 1112 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

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
| 332 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provi... |
| 335 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 338 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 360 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 394 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 397 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 400 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 403 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1385 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1682 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1694 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1722 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1781 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1807 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1866 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1875 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 2190 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2272 | `HouseholdBalance.FOOD_ASSISTANCE_MEANS_SHARE` | `.5` | A household is eligible for food assistance when a basket a head would take more than this share of what it has after its fixed bills and its investment income over the year: half. |
| 2286 | `HouseholdBalance.MEANS_INCOME_MONTHS` | `12` | Months the means test's investment income is smoothed over, as an exponential average - a twelfth of the gap a month, the year Expectations smooths inflation over (0.7.45; the UI spec's B16). |
| 2987 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 3187 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point. |
| 3190 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and, for a BUSINESS issue, never more than this share of one: half, because a bond market with no bank in it is no longer the bank's market (BondMarket). |
| 3978 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3981 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 4255 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 4258 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 4261 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 4264 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 4267 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 4277 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 4287 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 4296 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 4308 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim ... |
| 4322 | `HouseholdBalance.CELL_SLOTS_BEFORE_GROCERIES` | `CELL_SLOTS_BEFORE_BONDS + 1` | ...and the last sale's groceries, appended 0.7.43: the baskets each household got, the baskets it asked for at the price, the baskets it needed, and the food assistance paid on them - written at th... |
| 4330 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEANS_INCOME` | `CELL_SLOTS_BEFORE_GROCERIES + 4` | ...and the investment income the food assistance means test reads, smoothed over MEANS_INCOME_MONTHS, appended 0.7.45. |
| 4340 | `HouseholdBalance.CELL_SLOTS_BEFORE_AFTER_FIXED` | `CELL_SLOTS_BEFORE_MEANS_INCOME + 1` | ...and before each cell's income after its fixed bills was carried (A4). |
| 4343 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_AFTER_FIXED + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month'... |

### HydroSites.java ([map](map/HydroSites.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `HydroSites.REACH` | `10` | How far each bank is read from the water, in plots: 10 (300 m), a valley's side as a dam's abutment reaches it. |
| 48 | `HydroSites.CELL` | `128` | The world's site cells, in plots a side: 128 (3.84 km, four tiles), aligned to the world's plot grid, where a river holds one site. |
| 51 | `HydroSites.HEAD_MIN_M` | `10` | The least head a site needs, metres: 10 - under it there is no drop, and the sample is no candidate. |
| 54 | `HydroSites.LEAST_GRADE` | `{ 0, 0.10, 0.30, 0.60 }` | The least grade (rise over run) of each relief class, indexed FLAT, FOOTHILL, STEEP, VERY_STEEP: 0, 0.10, 0.30 and 0.60, the edges World's classes are cut at (ET1; LandMap.GRADE_WORDS says them), s... |
| 57 | `HydroSites.WIDTH_CAP_M` | `550` | The widest river read as flowing water, metres: 550, the width HG_COEFF gives Niagara's mean flow (about 5,800 m3/s) - GRWL keeps reservoirs and lake-like reaches a kilometre and more wide (Shawini... |
| 60 | `HydroSites.HG_COEFF` | `7.2` | Hydraulic geometry's coefficient, w = 7.2 Q^0.5 (a width in metres from the mean flow in cubic metres a second): Moody and Troutman 2002, quoted from memory, not re-read. |
| 63 | `HydroSites.EFFICIENCY` | `0.85` | Water to wire, the share of the water's power that reaches the grid: 0.85. |
| 66 | `HydroSites.G` | `9.81` | Gravity, metres a second squared. |
| 69 | `HydroSites.HOURS_A_MONTH` | `730` | The hours of a month the game's power figures are made at: 730 (the Wind Farm's arithmetic: kW x 730 h, over the unit). |
| 72 | `HydroSites.KWH_PER_UNIT` | `720` | The kilowatt-hours one unit of electricity is a month: 720 (derived from the Steel Foundry's documented 450 kWh a tonne; the Wind Farm's comment in BuildingManager). |
| 75 | `HydroSites.TILE_PAD_DEG` | `0.02` | A sample's reading is widened by this many degrees when the 1-degree tiles round a cell are found: 0.02 (2.2 km), past the curve of a cell's edge between the points it is found from. |
| 78 | `HydroSites.SEGMENT_CELLS_ASKED` | `16` | A segment whose box spans more than this many cells is kept without asking each: 16. |
| 156 | `HydroSites.BEST` | `(a, b) -> { int c = Double.compare(b.meanKw(), a.meanKw()); if(c = = 0) c = Long.compar...` | Whether one sample is better than another: the most mean power, then the lower y, then the lower x, then the greater head and width - a total order, so no tie depends on the order the segments are ... |

### Inbox.java ([map](map/Inbox.md))

| line | constant | value | says |
|---:|---|---|---|
| 39 | `Inbox.KEEP_MONTHS` | `24` | How long a resolved notice stays readable. |
| 42 | `Inbox.LANDLOCK_REPEAT_MONTHS` | `12` | How long the "nowhere to build" notice stands before the wait is told again in a new one (PT1-M2): a year. |
| 279 | `Inbox.BODY_COLUMNS` | `62` | A body line's most characters (0.7.73): the measure the bodies written by hand keep, inside the list's width in its mono face. |

### IndustryPicture.java ([map](map/IndustryPicture.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `IndustryPicture.SALVAGE` | `"salvage"` | ...the forest cleared for building, Forestry's salvage cut. |
| 95 | `IndustryPicture.LORRIES` | `"lorries"` | ...the lorries' diesel. |
| 105 | `IndustryPicture.ABROAD` | `"ABROAD", WAREHOUSE = "WAREHOUSE", LOST = "LOST"` | The takers that are not a party: the world, the warehouse, what was lost. |
| 117 | `IndustryPicture.LEAST_PART` | `.5` | The least part of a feed, a sale or the old metal the picture draws, in its good's unit: under half a unit its figure reads "0 t", and it is left out (I11's ★11: Iron & Steel buys a sliver of its o... |
| 475 | `IndustryPicture.HEIGHT` | `420` | The picture's height on the page (the materials mockups' 420 px at 1,389 x 868). |
| 478 | `IndustryPicture.LEAST_WIDTH` | `RefineryView.LEAST_WIDTH` | The least width it is laid out at (RefineryView's: the 1,280 window's card is about 1,094). |
| 481 | `IndustryPicture.TOP` | `60` | Room above the columns for their headings and a line of notes (the mockups' 60). |
| 484 | `IndustryPicture.HEAD_Y` | `14, SUB_Y = 27, NOTE_Y = 46` | ...the headings' baselines, their lines' and the notes' (the mockups' 14, 27 and 46). |
| 487 | `IndustryPicture.PAD` | `4` | ...and the margin under and over what the columns hold. |
| 490 | `IndustryPicture.SOURCE_ZONE` | `214` | Where the sources' bar stands: its words, an icon and a field's vessel at its left (the mockups' 196 to 232). |
| 493 | `IndustryPicture.SOURCE_W` | `12` | The sources' bar (the mockups' 10 and 12). |
| 496 | `IndustryPicture.VESSEL_W` | `22, VESSEL_H = 40, VESSEL_GAP = 12` | A field's vessel, and its gap to the bar (the mockups' 22 x 40, 12). |
| 499 | `IndustryPicture.FIELD_ROOM` | `44` | The least room a field's row takes: its vessel and three lines of words (the mockups' 42). |
| 502 | `IndustryPicture.BUYER_ZONE` | `236` | The takers' room at the right: the bar, an icon, a name and its figure, a line of words (RefineryView's). |
| 505 | `IndustryPicture.BUYER_W` | `12` | The takers' bar. |
| 508 | `IndustryPicture.UNIT_W` | `60` | A works' box (the mockups' 54 to 64). |
| 511 | `IndustryPicture.TANK_W` | `150` | The tank's width: a product's name and its figure inside its band (the mockups' 136 to 156). |
| 514 | `IndustryPicture.TANK_TO_TAKERS` | `60` | The room between the tank and the takers' bar, for the ribbons to cross (the mockups' 44 to 60). |
| 517 | `IndustryPicture.UNIT_GAP` | `42` | The gap over a works' box: its three lines of words - its name, its run, one more in a word. |
| 520 | `IndustryPicture.FIRST_RANK_GAP` | `70, LAST_RANK_ROOM = 130` | ...the room between the sources' bar and the first rank of works, for the ribbons to turn; and the room the last rank's words have before the tank. |
| 523 | `IndustryPicture.CAPITAL` | `.74, LETTER =.6` | The share of its size a letter of the picture's words is set at, at most: a capital's and a lower-case letter's or a figure's (IBM Plex Sans's average is .55, measured at 1,389 x 868 by the batch's... |
| 526 | `IndustryPicture.SOURCE_GAP` | `22, FIELD_GAP = 14, BUYER_GAP = 12, BLOCK_GAP = 22` | The gap between the sources, between the fields' rows, between the takers, and over the warehouse's block. |
| 529 | `IndustryPicture.BLOCK_WORDS` | `16` | The room under the warehouse's block for its line: its baseline 13 px under, and the line's descent. |
| 532 | `IndustryPicture.ROOF_ROOM` | `48` | The room over the tank for its roof: the furnace's stack and flame, the tallest. |
| 535 | `IndustryPicture.FOOT_ROOM` | `8` | The room under the tank for its foot (the mockups' 7), when no warehouse's block stands under it. |
| 538 | `IndustryPicture.SOURCE_SPACING` | `30, LABEL_SPACING = 31` | The least a source's or a taker's label centre stands from the next (the mockups' 30 and 31). |
| 541 | `IndustryPicture.WORDS_IN_BAND` | `12` | The least band a product's name and figure are written in (the mockups' 12). |
| 544 | `IndustryPicture.FIGURE_IN_BAND` | `52, FIGURE_ROOM = 56` | The room a product's figure has at the right of its band, and a taker's at the right of its name. |
| 547 | `IndustryPicture.PASS_GAP` | `3` | The gap over a band through a works' column (RefineryView's PASS_GAP). |
| 550 | `IndustryPicture.LEAST_NODE` | `1.5, ON_SITE_NODE = 6` | The least a node is drawn, so a trickle shows; and a kind only on site, drawn dashed with nothing through it. |
| 553 | `IndustryPicture.ICON_ROOM` | `22` | The least a box is drawn with an icon in it (the mockups' 22). |
| 556 | `IndustryPicture.RIBBON` | `.5, HATCHED =.95` | The opacity of a ribbon, and of an imported one, hatched (the mockups' .5 and .95). |
| 1509 | `IndustryPicture.STRIP_MOST` | `6` | The most cells the strip holds: six at about 200 px each at 1,389 x 868. |
| 1512 | `IndustryPicture.STRIP_HEAD` | `"THIS MONTH, CELL BY CELL"` | The strip's heading. |
| 1524 | `IndustryPicture.STRIP_WORDS` | `"a field's bar what is left of it · a product's: used here, shipped(pale), into the war...` | The strip's line: what its bars show. |

### IndustryView.java ([map](map/IndustryView.md))

| line | constant | value | says |
|---:|---|---|---|
| 72 | `IndustryView.INDUSTRIES` | `List.of(Sectors.HEAVY_INDUSTRY, Sectors.MINING, Sectors.COAL_MINING, Sectors.QUARRYING,...` | The nine industries the view draws, in Sectors.KEYS' order. |
| 626 | `IndustryView.POWER` | `"#ffd866"` | Power, a ribbon to or from the grid. |
| 628 | `IndustryView.CAPTIVE` | `RefineryView.UNIT_EDGE` | A part from or to the industry's own works. |
| 630 | `IndustryView.HOME` | `"#6fa8dc"` | A part bought or sold in the city. |
| 632 | `IndustryView.OLD_METAL` | `"#9a8f7e"` | The city's old metal. |
| 634 | `IndustryView.WORLD` | `RefineryView.HOT` | A part from or to the world: imported, drawn hatched (the spec's 2.13), or shipped. |

### InflationLinkedBond.java ([map](map/InflationLinkedBond.md))

| line | constant | value | says |
|---:|---|---|---|
| 46 | `InflationLinkedBond.MATURITIES` | `{ 10, 30 }` | The only terms an inflation-linked bond is issued at, in years (D25). |
| 49 | `InflationLinkedBond.REFUSAL` | `"Inflation-linked bonds are issued at 10 or 30 years."` | What the treasury is told when it asks for any other term. |

### InfrastructureManager.java ([map](map/InfrastructureManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 51 | `InfrastructureManager.BASE_CAPACITY` | `400` | The streets that already exist, before the city builds anything. |
| 54 | `InfrastructureManager.FREE_FLOW` | `.9` | Up to this share of capacity, traffic moves freely. |
| 57 | `InfrastructureManager.MIN_THROUGHPUT` | `.35` | However bad it gets, the city does not stop moving entirely. |
| 60 | `InfrastructureManager.STRAINED` | `.85` | Utilisation past which the network is worth warning about. |
| 193 | `InfrastructureManager.TRANSIT_MAX_SHARE` | `.65` | The share of every group of commuters a city's lines reach (0.7.49): the car-less and the owners alike, so also the most of its commuters any city can ever put on transit. |
| 196 | `InfrastructureManager.TRANSIT_NEEDS_ROAD` | `2.0` | Transit capacity a city can use, per unit of road capacity under it. |
| 199 | `InfrastructureManager.BULK_HIGHWAY_RELIEF` | `.40` | What a fully grade-separated network takes off a tonne of bulk. |
| 202 | `InfrastructureManager.GOODS_HIGHWAY_RELIEF` | `.15` | ...and off a crate of goods, which shares fewer junctions to begin with. |
| 225 | `InfrastructureManager.RAIL_ROAD_RELIEF` | `.75` | ...AND RAIL TAKES THE LONG HAUL OFF IT ALTOGETHER, which is the third mode and the only one the city does not own. |
| 271 | `InfrastructureManager.CAR_LOAD_AT_SATURATION` | `3.0` | What a city where every household owns a car asks of the road, against the same city where none does. |
| 289 | `InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK` | `.75` | How many of the people who own a car get on the tram anyway once the road is completely gridlocked. |
| 292 | `InfrastructureManager.JAM_MEMORY` | `.25` | How fast the remembered commute catches up with this month's. |
| 604 | `InfrastructureManager.MODE_SPREAD` | `1.0 / 3` | How a cell splits between two costs: all on the bus at half the drive's cost, all in the car at twice it, the straight line between. |
| 912 | `InfrastructureManager.THROUGH_MAX_SHARE` | `.55` | The most of a city's load that is through trips - long trips across it and the freight to its gates - in the largest cities, a share of the load past the gates'. |
| 923 | `InfrastructureManager.THROUGH_HALF_AREA_KM2` | `83.4` | The built-up area, in km2, at which the through share is half of THROUGH_MAX_SHARE. |
| 1016 | `InfrastructureManager.STREET_VEHICLES_A_DAY_AT_CAPACITY` | `450` | Vehicles a day that cross a gravel or paved street plot when the streets are at their whole capacity (a utilisation of 1): this batch's scale, struck so that the quiet line, 200 a day, is a utilisa... |
| 1019 | `InfrastructureManager.GRAVEL_QUIET_VEHICLES_A_DAY` | `200` | The load, in vehicles a day, under which a gravel road is repaired at its flat share (Minnesota, FR7 R5): 200. |
| 1022 | `InfrastructureManager.GRAVEL_QUIET_UTILISATION` | `GRAVEL_QUIET_VEHICLES_A_DAY / STREET_VEHICLES_A_DAY_AT_CAPACITY` | The street utilisation at the quiet line: the load GRAVEL_QUIET_VEHICLES_A_DAY is of a street's whole capacity. |

### InterchangeGround.java ([map](map/InterchangeGround.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `InterchangeGround.CLOVERLEAF` | `0, TRUMPET = 1` | A reserve's two kinds: a cloverleaf at a highway x highway crossing, a trumpet at an arm that ends at another highway. |
| 52 | `InterchangeGround.CLOVERLEAF_SIDE` | `19` | A cloverleaf's reserve, in plots a side: 19, 570 m square, about 32 ha - about 20 acres a quadrant (spec-freight 4.6, [R82]). |
| 55 | `InterchangeGround.TRUMPET_SIDE` | `11` | A trumpet's reserve, in plots a side: 11, about 11 ha (spec-freight 4.6, est.). |
| 58 | `InterchangeGround.REACH` | `TRUMPET_SIDE` | How far a box reaches from its junction plot, in plots: a cloverleaf's 9 either way - the most of the two kinds (a trumpet's runs 11 out on its stub's side). |
| 87 | `InterchangeGround.SX` | `{ 0, 1, 0, - 1 }, SY = { - 1, 0, 1, 0 }` | North, east, south, west as steps. |

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
| 330 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 351 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |
| 396 | `LabourMarket.EXPECTED_SHARE` | `.5` | The share of a wage's monthly indexing taken from expected inflation; the rest chases the index at the old speed's share. |

### LandClassView.java ([map](map/LandClassView.md))

| line | constant | value | says |
|---:|---|---|---|
| 160 | `LandClassView.GROUPED` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### LandConversion.java ([map](map/LandConversion.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `LandConversion.CENTRE_DRY_MIN` | `0.8` | The least share of a converted city's centre that is dry ground: 80% (spec-land 2.9), so a played city is not put on a peninsula two-thirds sea. |
| 59 | `LandConversion.CENTRE_SEA_KM` | `2` | ...with the sea within its half-side and this many kilometres: 2, a city on a coast. |
| 62 | `LandConversion.LAST_FORMAT_BEFORE` | `30` | The last save format whose land is one figure, converted from it: 30, the format before the land was on the world. |
| 65 | `LandConversion.LAST_LANES_FORMAT` | `31` | The last save format whose land is lanes, snapped to blocks at load: 31 (0.7.57 to 0.7.66; GameVersion.SAVE_FORMAT 32 holds blocks). |
| 68 | `LandConversion.SAME_GROUND_PLOTS` | `1` | How near a save's square feet must be to its land's dry ground to be the same ground: within one plot of it (0.7.67; a part in a billion before, when a centre held its figure exactly) - a figure se... |
| 71 | `LandConversion.SITE_CELLS` | `64` | How many coastal cells' sites are tried, nearest the world's middle first: 64. |
| 74 | `LandConversion.LEGACY_FIELD_KM` | `1` | A legacy iron field stands at least this far from the site: 1 km (spec-land 2.4). |
| 77 | `LandConversion.LEGACY_DRAWS` | `64` | Draws for the legacy field's dry plot: 64. |
| 80 | `LandConversion.LEGACY_STREAM` | `0x1E6AC7L` | The stream the legacy field's plot is drawn from. |

### LandGrid.java ([map](map/LandGrid.md))

| line | constant | value | says |
|---:|---|---|---|
| 44 | `LandGrid.TOP` | `20` | The root's level: 2^20 plots (1,048,576) a side, the least power of two the world's World.SIDE (753,664 plots) fits in. |
| 47 | `LandGrid.MIN_LEVEL` | `2` | The finest block an offer or a new city's centre is drawn in: level 2, four plots (120 m) a side - 0.0144 km2, the smallest block holding one of spec-land's 100,000 sq ft blocks, Jerus's "one block... |
| 50 | `LandGrid.MAX_LEVEL` | `15` | The coarsest: level 15, 2^15 plots (983 km) a side - the largest block the world, 23 x 2^15 plots a side, divides into whole (spec-grid star 1). |
| 53 | `LandGrid.FACE_BLOCKS` | `6` | Blocks of the city's level that fit across a square of its area: six, so each of a side's six places is one or two blocks wide (spec-grid star 2). |
| 56 | `LandGrid.EMPTY` | `0` | A node's state: none of its plots owned... |
| 59 | `LandGrid.FULL` | `1` | ...all of them, by one holding... |
| 62 | `LandGrid.MIXED` | `2` | ...some of them: it has four children... |
| 65 | `LandGrid.OWNED` | `3` | ...or all of them, by more than one holding: its children kept, so a plot's owner is still found, and cover() says ALL without descending. |
| 68 | `LandGrid.NONE` | `0` | What cover() says of a block: none of its plots owned... |
| 71 | `LandGrid.SOME` | `1` | ...some of them... |
| 74 | `LandGrid.ALL` | `2` | ...or all of them. |
| 77 | `LandGrid.ROOT` | `1L<<TOP` | The plots a side of the root: 2^TOP. |

### LandManager.java ([map](map/LandManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 76 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 79 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 82 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 93 | `LandManager.SQ_FT_PER_KM2` | `SQ_M_PER_KM2 / SQ_M_PER_SQ_FT` | Square feet in a square kilometre: a million square metres over a square foot's, 10,763,910.4 (0.7.57). |
| 112 | `LandManager.M2_WORDS_BELOW` | `10_000` | Areas under this many square metres read in square metres (0.7.68): a hundredth of a square kilometre, so a building's plot reads "743 m\u00b2" (a House's 8,000 square feet) and not "0.000743 km\u0... |
| 176 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres; since 0.7.67 the figure a new city's centre of whole blocks is drawn to hold, and it owns the dry plots drawn, a little more (CityLand.fou... |
| 185 | `LandManager.FOUNDING_FRESH_UNITS` | `60_000` | A new city's fresh water, in units a month: what one Water Treatment Plant treats, its 60,000 units of 10,000 gallons (buildings.json id 10's production1) - Jerus's A24 (0.7.106, 2026-10-10: "found... |
| 188 | `LandManager.FOUNDING_FRESH_KM2` | `FOUNDING_FRESH_UNITS / UtilitiesHandler.FRESH_UNITS_PER_KM2` | ...as lake and river ground in its centre, in square kilometres: FOUNDING_FRESH_UNITS over what one yields a month (UtilitiesHandler.FRESH_UNITS_PER_KM2), 0.4934 km2 (CityLand.found()). |
| 240 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 327 | `LandManager.FOREST_REGROWTH` | `1.0 / 240` | Forest's stored depletion falls by this share a month: 1/240, a twenty-year time constant, so 95% of what is cut grows back within a sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). |
| 1045 | `LandManager.TWO_POOLS_FORMAT` | `35` | The first save format that carries the two pools: its depletion's oil the ground pool's, and the offshore pool's E beside it. |
| 2562 | `LandManager.CLEARING_ROUNDING` | `1e-9` | A share of the city's dry ground that is a rounding and not ground: what the month's clearing ignores past the built ground's own (settleClearing()). |
| 3083 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### LandMap.java ([map](map/LandMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 46 | `LandMap.OUTSIDE` | `0, CENTRE = 1, BOUGHT = 2, OFFER = 3` | Whose a plot is: nobody's, the centre's, a purchase's, or an offer's. |
| 95 | `LandMap.NORTH_EDGE` | `0, EAST_EDGE = 1, SOUTH_EDGE = 2, WEST_EDGE = 3` | The four edges a run of the outline lies on, by the side of the city's ground it bounds: its north edge, east, south and west. |
| 368 | `LandMap.YOUR_GROUND` | `"Your ground"` | "Your ground", the picked ground's name on the card and the hover card. |
| 371 | `LandMap.ONE_BLOCK` | `YOUR_GROUND + ": one block"` | The hover card's line over one block that may be picked (BL1). |
| 408 | `LandMap.CLICK_TO_PICK` | `"Click to pick it, drag to pick more"` | The hover card's last line over ground the player may pick (BL1), in blue as CLICK_TO_BUY is: how to pick it and more. |
| 484 | `LandMap.NO_UNLINKED` | `new UnlinkedKept(null, 0, 0, null, List.of(), List.of())` |  |
| 747 | `LandMap.SHALLOW_WORDS` | `"Shallow sea · 30 m or less"` | The shallow sea's words (ET1, spec-earth2-terrain 9.3): its depth (bridges over it are ET6's), the hover's and the legend's. |
| 766 | `LandMap.RELIEF_NAMES` | `{ "Flat", "Foothill", "Steep", "Very steep" }` | Each relief's name as the legend and the hover's first line give it, by World's relief (FLAT, FOOTHILL, STEEP, VERY_STEEP): flat ground is named by its cover, so FLAT's is only for completeness. |
| 769 | `LandMap.GRADE_WORDS` | `{ "flat, under 10% grade", "10–30% grade", "30–60% grade", "60% grade or more" }` | Each relief's grade, the hover's words after its name (World's thresholds: 10%, 30% and 60%): only what is true now - what may be built on it and what preparing it costs are later batches'. |
| 772 | `LandMap.FOREST_ON_WORDS` | `{ "Forest", "Forest on a foothill", "Forest on steep ground", "Forest on very steep gro...` | Forest as the hover names it on each relief (FLAT: the forest's own word). |
| 775 | `LandMap.FOREST_SLOPE_WORDS` | `"Forest on a slope"` | The legend's entry for forest on a slope: its swatch the steep slope's shading (TileRaster.colour()), the three being one family. |
| 917 | `LandMap.CARD_FIELDS` | `3` | The most fields the expanded map's hover card lists: 3 (MapView's since 0.7.61; the model's since 0.7.135, its card being hoverWords()'s). |
| 984 | `LandMap.NOT_THE_CITYS` | `"Not the city's"` | Whose a point on the globe is beyond the city's plane and its reach (spec-globe 3.7). |
| 1018 | `LandMap.RING` | `0xff8cc4ff` | The city's ring on the globe: the founding globe's site mark's light blue (Palette.ACCENT_LIGHT, #8cc4ff). |
| 1021 | `LandMap.CITY_RING_WORDS` | `"The city"` | The ring's legend words. |
| 1024 | `LandMap.EDGE_WORDS` | `"The city's edge"` | The city's edge's legend words (its swatch white, as it is drawn). |
| 1027 | `LandMap.OFFER_WORDS` | `"On offer"` | The offers' legend words (their swatch the offers' pink, #f6a6c9). |
| 1030 | `LandMap.OFFER_EDGE` | `0xfff6a6c9` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 1033 | `LandMap.WORLD_WORDS` | `"to the world ›"` | The world line's label (spec-freight 4.1: "labelled 'to the world ›'"). |
| 1036 | `LandMap.WORLD_BOTH_WORDS` | `"road and rail to the world ›"` | The label two world lines share where their labels would be drawn on one spot and nothing along either line clears them (FX5; the highway's and the railway's, which leave together): one label namin... |
| 1039 | `LandMap.WORLD_ALPHA` | `0.45` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"); ui/MapView's WORLD_LINE_ALPHA reads this one, so the line and its legend swatch are one dial. |
| 1042 | `LandMap.WORLD_LABEL_PAD` | `6` | The gap kept between the world line's label and the view's edge, in pixels: 6 (FX2). |
| 1045 | `LandMap.WORLD_LABEL_BACK` | `60` | How far back from the far end of the world line's part in view its label first sits, in pixels: 60, as FR0 drew it (FX2). |
| 1197 | `LandMap.TRAIN_WORDS` | `"Train: wagons in the ships' colours"` | The legend's train (0.8.2, batch FR1): its wagons in the ships' four colours, a locomotive at each end. |
| 1258 | `LandMap.ZONE_ALPHA` | `0.22` | A zone's districts are tinted in its sector's colour this much over the map: 22%, a low alpha (spec-zoning 4: "the map tints a zone's districts"). |
| 1265 | `LandMap.ZONE_COLOURS` | `{ 0xfff2c94c, 0xffeaa572, 0xffb39ddb, 0xffffb74d, 0xffe57373, 0xffd4825a, 0xffbdbdbd, 0...` | Each sector's zone colour, in Sectors.KEYS' order: bright, so a low alpha shows over the ground - its industry's own where the map has one (Forestry's green, Quarrying's stone, Copper's teal). |
| 1284 | `LandMap.ZONE_GROUND_APART` | `20` | The least a zone's colour may differ from each colour of Policy > Zoning's own ground (built ground's grey, the land office's pink, the forest's three greens), in CIE76 units (colourGap): a zone's ... |
| 1330 | `LandMap.UNLINKED_COLOUR` | `0xffe3b341` | The legend's colour for the hatch over ground no port links: the warning amber (Palette.WARN's #e3b341). |
| 1349 | `LandMap.TRACK_ENTRY` | `"Unmade street"` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for, brown dashes on its verge. |
| 1364 | `LandMap.OFF_PLANET_WORDS` | `"Off the planet: the map runs on past the antipode, the point opposite the city"` | The hover past the planet's rim, where the map's square runs on beyond the antipode (spec-earth 2: the far node draws it as the void). |
| 1367 | `LandMap.COMPASS` | `{ "north", "north-east", "east", "south-east", "south", "south-west", "west", "north-we...` | The eight directions a place is named from, clockwise from north. |
| 1370 | `LandMap.KIND_WORDS` | `{ "oceanic", "a shield", "a fold belt", "a rift", "a shelf", "a basin", "unclassed" }` | A geologic class in the hover's words, in Geology.Kind's order: ocean, shield, fold belt, rift, shelf, basin, other. |
| 1438 | `LandMap.CLASS_ONE` | `{ "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School", "Health", "Safety...` | One building of each class, as the hover card names a type it has no name for. |
| 1508 | `LandMap.QUAY_WORDS` | `"Quay: a terminal's berth, where its ships come alongside"` | A terminal's quay in the hover (0.7.97): where its ships come alongside. |
| 1511 | `LandMap.JACKET_WORDS` | `"Offshore platform: its wells drilled from the jacket"` | An offshore platform's jacket in the hover (0.7.97). |
| 1514 | `LandMap.WELL_WORDS` | `"Platform well, on its sea site"` | ...a platform well, on its sea site. |
| 1517 | `LandMap.RING_WORDS` | `"A platform's 500 m safety zone"` | ...the ring of a platform's safety zone (the research's 3.3 [W32]). |
| 1520 | `LandMap.PIPE_WORDS` | `"Crude pipeline, buried: the platforms' crude ashore without tankers"` | ...a crude pipeline, buried (spec-roads-and-ports.md 2.8). |
| 1523 | `LandMap.ACCESS_WORDS` | `" · access road"` | An access road in the hover (batch FR7, D17): the street from a port's, a yard's or a depot's gate to the highway's ramp or the arterial grid. |
| 1526 | `LandMap.RAMP_WORDS` | `" · a ramp"` | A highway's ramp in the hover (0.7.89; spec 2.7). |
| 1529 | `LandMap.RAIL_OVER_WORDS` | `" · the railway over it"` | ...and the railway bridging it (0.7.89; spec 2.8). |
| 1532 | `LandMap.TRACK_WORDS` | `"Unmade street: the city has bought no road here"` | An unmade street's words in the hover (0.7.88; spec 5; "Track: ..." until FX5, with TRACK_ENTRY). |
| 1535 | `LandMap.HALF_WORDS` | `"15 m", FULL_WORDS = "30 m"` | A street's widths in the hover: half (15 m) and full (30 m), a plot's 30 m right of way (spec 2.1). |
| 1538 | `LandMap.PACKED_WORDS` | `" · packed without a street"` | A building packed without a street, in the hover (R7). |
| 2117 | `LandMap.Picks.BLOCK_BOUGHT_NOTE` | `"1 block dropped: the city bought it"` | The note when the city bought picked blocks (the player's buying or its own); one block. |
| 2120 | `LandMap.Picks.BLOCKS_BOUGHT_NOTE` | `"%d blocks dropped: the city bought them"` | ...two or more blocks at once; %d how many. |
| 2123 | `LandMap.Picks.BLOCK_UNJOINED_NOTE` | `"1 block dropped: no longer joined to the city"` | The note when picked blocks no longer join the city - the blocks or offers they joined through were dropped or bought away; one block. |
| 2126 | `LandMap.Picks.BLOCKS_UNJOINED_NOTE` | `"%d blocks dropped: no longer joined to the city"` | ...two or more blocks at once; %d how many. |
| 2129 | `LandMap.Picks.BLOCK_RESIZED_NOTE` | `"1 block dropped: the city's blocks changed size"` | The note when the city's blocks changed size (its level rose with its ground) and the picked blocks were the old ones'; one block. |
| 2132 | `LandMap.Picks.BLOCKS_RESIZED_NOTE` | `"%d blocks dropped: the city's blocks changed size"` | ...two or more blocks at once; %d how many. |
| 2135 | `LandMap.Picks.BOUGHT_NOTE` | `"%s dropped: the city bought its ground"` | The note when the city bought a pick's ground (the player's buying or the city's own); %s the offer's name, "North 3". |
| 2138 | `LandMap.Picks.BOUGHT_MANY_NOTE` | `"%d picks dropped: the city bought their ground"` | ...two or more picks at once; %d how many. |
| 2141 | `LandMap.Picks.UNOFFERED_NOTE` | `"%s dropped: no longer offered"` | The note when a pick's ground is for sale under no offer now; %s the offer's name. |
| 2144 | `LandMap.Picks.UNOFFERED_MANY_NOTE` | `"%d picks dropped: no longer offered"` | ...two or more picks at once; %d how many. |
| 2147 | `LandMap.Picks.CARRIED_NOTE` | `"%s carried onto %s, listed again"` | The note when a pick's offer left the shelf and its ground is offered again, under another id: carried onto it; %s the pick's name or "2 picks", %s the offer's name or "2 offers". |
| 2155 | `LandMap.BOX_BLOCKS_MOST` | `2_000` | The most blocks one box takes (BL1): 2,000, the flood from the city's ground into the box stopping there, the nearest first - a city is some six to twelve blocks across, so this is several cities o... |

### LandMarket.java ([map](map/LandMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 94 | `LandMarket.OFFERS_A_SIDE` | `GridOffers.PLACES` | Offers standing on each side of the city: six, one a place (GridOffers.PLACES; Jerus, 2026-10-07, "six offers a side"). |
| 97 | `LandMarket.OFFERS` | `CityLand.SIDES * OFFERS_A_SIDE` | Offers standing in all: OFFERS_A_SIDE on each of the four sides, twenty-four. |
| 111 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands of US dollars. |
| 239 | `LandMarket.CROWDING_MIDPOINT` | `4_000` | People per square kilometre of the city's land at which the crowding premium is half way to its ceiling (0.7.55): about 60x the base. |
| 247 | `LandMarket.CROWDING_STEEPNESS` | `5.3` | How sharply the premium climbs through the midpoint: the curve's power (0.7.55). |
| 255 | `LandMarket.CROWDING_CEILING` | `120` | The most crowding can multiply the ground's price by, however crowded the city (0.7.55). |
| 262 | `LandMarket.FRESH_PRICE_SHARE` | `0.45` | What a square kilometre of fresh water sells for, against dry ground: 45% (the map mockup's figure, spec-land star 6). |
| 265 | `LandMarket.SEA_PRICE_SHARE` | `0.08` | ...and of sea: 8% (the mockup's), the reach a desalination plant needs and nothing else does. |
| 316 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 319 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 331 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |

### LandParcel.java ([map](map/LandParcel.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `LandParcel.MOSTLY_SEA` | `0.70` | The share of an offer's area that, when sea, tags it "mostly sea": 70% - listed, because sea is cheap, but never the best value (spec-land 2.2). |
| 308 | `LandParcel.RECT_FIELDS` | `7` | Where a record keeps the rectangle: side, place, level, x0, y0, x1, y1 - seven. |
| 311 | `LandParcel.OFFER_FIELDS` | `1 + RECT_FIELDS + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide an offer's record is: its id, its rectangle (side, place, level, x0, y0, x1, y1), its five areas, its sites, its amounts, its price and the month it was listed - 29. |
| 314 | `LandParcel.PURCHASE_FIELDS` | `RECT_FIELDS + 3 + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide a holding's record is: its rectangle, the month bought, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 31 (spec-grid 3, M3). |

### LandmassView.java ([map](map/LandmassView.md))

| line | constant | value | says |
|---:|---|---|---|
| 29 | `LandmassView.ISLAND_TAG_SHARE` | `0.5` | The share of an offer's dry ground that must lie on landmasses the city has not linked for the land office to tag the offer ISLAND (before BEST VALUE and the rest): half. |
| 50 | `LandmassView.ISLAND_TAG` | `"ISLAND"` | The tag the land office puts on an offer that is mostly ground no port links. |
| 53 | `LandmassView.NOT_LINKED_CLAUSE` | `"which no port links"` | The words for ground on a landmass no port links, as a clause: "which no port links". |
| 56 | `LandmassView.LEGEND_WORDS` | `"Not linked: only a port"` | The map's legend entry for the hatch over ground no port links. |
| 59 | `LandmassView.ONLY_A_PORT` | `"only a port can be built there until it has one"` | What only a port can do on ground no port links, as a sentence's end: "only a port can be built there until it has one". |
| 114 | `LandmassView.TABLE_TITLE` | `"LANDMASSES"` | The table's section head. |
| 117 | `LandmassView.TABLE_HINT` | `"where the city's ground is · only a port can be built where none links"` | The table's hint, at the right of its head. |
| 120 | `LandmassView.TABLE_MIN_ROWS` | `2` | The table is drawn for a city that owns ground on this many landmasses or more (home counts): 2. |
| 123 | `LandmassView.HEADS` | `{ "landmass", "dry owned", "free", "ports", "linked" }` | The table's heads, in its columns' order. |
| 126 | `LandmassView.WIDTHS` | `{ 0, 92, 230, 190, 290 }` | The columns' widths in pixels (the first, 0, takes the rest). |
| 129 | `LandmassView.RIGHT` | `{ false, true, false, false, false }` | Which columns' cells lie to the right. |
| 276 | `LandmassView.SEGMENT_KEY` | `"free, no port links it"` | The key's words for that segment. |
| 311 | `LandmassView.CHOOSER_LABEL` | `"Where"` | The chooser's label, before its chips. |
| 396 | `LandmassView.NO_SHORE_WAY` | `"own some sea in front of dry shore, on the landmass it serves: the Land office ›"` | ...and the way out under it. |
| 431 | `LandmassView.PORT_DOOR` | `"Build a port there ›"` | The label of the door from the refusal to the port card. |
| 438 | `LandmassView.NOTICE_KEY` | `"island:"` | A standing notice's key prefix: "island:" and the landmass's id. |
| 456 | `LandmassView.NOTICE_BUTTON` | `PORT_DOOR` | The button on that notice. |
| 459 | `LandmassView.NOTICE_BUTTON_NO_SHORE` | `"Go to the Land office ›"` | The button on that notice where the city owns no shore on the landmass: a terminal cannot stand there yet, and sea is bought at the Land office. |

### Landmasses.java ([map](map/Landmasses.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Landmasses.HOME_NAME` | `"Home"` | A landmass's name for the landmass the city stands on. |
| 52 | `Landmasses.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The plot's area in square feet, as LandManager turns the land's square kilometres into them. |
| 55 | `Landmasses.GATE_MARGIN_PLOTS` | `160` | The plots a margin round the owned ground's box that the 1-degree gate widens it by (it sets the cells' reach, about 124 plots, and some more): 160. |
| 58 | `Landmasses.GATE_STEP_PLOTS` | `256` | The spacing, in plots, at which the 1-degree gate samples the edges of a box for its latitudes and longitudes: 256. |
| 61 | `Landmasses.GATE_MARGIN_DEG` | `0.05` | The degrees the 1-degree gate widens a box's latitudes and longitudes by: 0.05 (twelve cells, past EarthTerrain.LANDMASS_REACH). |
| 64 | `Landmasses.GATE_TILES_MOST` | `100` | The most 1-degree tiles the gate looks through; a box over more is read by the tiles. |
| 67 | `Landmasses.GATE_LAT_MOST` | `85` | The latitude past which the gate gives up (a box near a pole is not a box in latitude and longitude). |
| 70 | `Landmasses.NEAR_KEPT` | `100_000` | The tiles whose window of cells (World.idsNear()) is remembered before the memory is cleared: 100,000 (about 8 MB). |
| 619 | `Landmasses.CHOSEN` | `- 1` | portLandmass()'s `asked`: the card's choice if there is one and it will do, else the rule. |
| 622 | `Landmasses.RULE` | `- 2` | ...the rule alone: the automatic builder and the advice never link an island by choosing it (ET5 D10). |
| 625 | `Landmasses.NO_LANDMASS` | `- 3` | portLandmass()'s answer when no landmass will do: the order is NO_COAST. |

### LegacyLand.java ([map](map/LegacyLand.md))

| line | constant | value | says |
|---:|---|---|---|
| 47 | `LegacyLand.SIDES` | `4` | Sides of the city, seen from the founding site: north, east, south and west, in that order - the larger of \|x\| and \|y\| says which (spec-land star 1). |
| 50 | `LegacyLand.LANES` | `10` | Lanes on a side: ten wedges fanning out from the site, each with one offer standing until 0.7.66 (spec-land star 1). |
| 53 | `LegacyLand.SIDE_NAMES` | `{ "North", "East", "South", "West" }` | The sides' names, in their order. |
| 56 | `LegacyLand.LANE_SHARE` | `1.0 / LANES` | A lane's share of its side's r squared: a tenth - a side within radius r holds r^2 plots, so a band of a lane from r1 to r2 holds this x (r2^2 - r1^2). |
| 59 | `LegacyLand.AREAS` | `5` | Where a record keeps its whole area, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA and FOREST, in that order. |
| 62 | `LegacyLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 65 | `LegacyLand.CENTRE_FIELDS` | `1 + AREAS + 2 * KINDS + 3 + 2` | How wide a format-31 centre's record is: its half-side, its five areas, its sites, its amounts, the legacy field's plot and sites, and the site's plot - 25 (CityLand.CENTRE_FIELDS until 0.7.66; 24,... |
| 68 | `LegacyLand.PURCHASE_FIELDS` | `7 + AREAS + 2 * KINDS + 2` | How wide a format-31 purchase's record is: side, lane, r1, r2, the month, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 28 (LandParcel... |
| 332 | `LegacyLand.PROFILE_SPAN` | `1024` | Samples across a centre's profile from the site to twice the radius its dry ground would need if all of it were dry: 1,024 - the stride is one plot (every plot counted, a tile at a time) up to abou... |
| 500 | `LegacyLand.PROFILES_KEPT` | `8` | How many profiles are kept: 8, the city's and a few a conversion tried. |
| 502 | `LegacyLand.PROFILES` | `new LinkedHashMap<>(16, 0.75f, true) { @ Override protected boolean removeEldestEntry(M...` |  |

### LongTermBond.java ([map](map/LongTermBond.md))

| line | constant | value | says |
|---:|---|---|---|
| 37 | `LongTermBond.MATURITIES` | `{ 2, 5, 10, 20, 30, 50 }` | The only terms a bond is issued at, in years: six benchmark maturities, so each trades as a line. |
| 40 | `LongTermBond.REFUSAL` | `"Bonds are issued at 2, 5, 10, 20, 30 or 50 years."` | What the treasury is told when it asks for any other term. |

### MapFrame.java ([map](map/MapFrame.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `MapFrame.ZOOM_STEP` | `1.25` | A notch of the wheel zooms by this much, at the pointer: 1.25 (spec-land 2.6). |
| 36 | `MapFrame.MOST_PX_A_PLOT` | `16` | The closest the view comes: 16 px a plot (spec-land 2.6), a plot's buildings and roads drawn at the raster's own widths. |
| 39 | `MapFrame.L0_FROM` | `3.2` | L0 from here: 3.2 px a plot (the mockup's): buildings one by one. |
| 42 | `MapFrame.L1_FROM` | `1.4` | L1 from here to L0: 1.4 px a plot (the mockup's): blocks of four plots; below it, blocks of eight. |
| 45 | `MapFrame.BIG_TILES_FROM` | `6` | Past this the L0 tiles are rastered at BIG_TILE_PX: 6 px a plot (spec-land 2.6), where TileRaster begins to edge the buildings (EDGE_FROM) and case the roads. |
| 48 | `MapFrame.SMALL_TILE_PX` | `4` | Pixels a plot an L0 tile is rastered at up to BIG_TILES_FROM: 4 (spec-land 2.6), TileRaster's first width with every road's line drawn. |
| 51 | `MapFrame.BIG_TILE_PX` | `8` | ...and past it: 8 (spec-land 2.6). |
| 54 | `MapFrame.MIDDLE_TILE_PX` | `2` | Pixels a plot an L1 tile is rastered at: 2 - the blocks of four drawn at the middle view's own scale, as J3b's renders drew them (round(1.74) at Jerus's expanded view), so a road is a line, not a b... |
| 57 | `MapFrame.FAR_TILE_PX` | `1` | ...and an L2 tile: 1, a pixel a plot, drawn smaller than it is. |
| 60 | `MapFrame.NEAR_TILES_MOST` | `1024` | L2 draws painted tiles while the view holds no more than this many (star): 1,024 - a city the size of Jerus's (about 350 tiles fitted into the land office's small map) is drawn from its painted str... |
| 63 | `MapFrame.NODE_PX` | `128` | A far node image's side, in pixels: 128 (star) - shown between 128 and 256 px, so about 96 of them at most fill a 1,345 x 806 view. |
| 66 | `MapFrame.FIT_MARGIN` | `0.94` | A fitted view leaves this much of itself round what it fits: 0.94 of the view (J3b's renders), a margin of 3% each side. |
| 69 | `MapFrame.OPENING_MARGIN` | `0.35` | The land office opens on the city's own ground with this share of its half-size round it each way (star): 0.35 - the near part of every offer is in view, where fitting every offer whole would shrin... |
| 72 | `MapFrame.L0` | `0, L1 = 1, L2 = 2, FAR = 3` | The levels. |
| 75 | `MapFrame.BLOCK_LINES_FROM` | `6` | The city's block lines are drawn from here: a block at least 6 px across on screen (spec-grid 2.4), so the lines never crowd the ground they mark. |
| 78 | `MapFrame.PLACE_LABEL_FROM` | `11` | An offer's place is written on it from here: its box at least 11 px each way on screen (0.7.69, star) - a digit of Plex Mono at 11 px is 6.6 px wide and 7.7 px of capital, so it stands clear of the... |
| 81 | `MapFrame.OPENING_OFFER_PX` | `PLACE_LABEL_FROM + 1` | The land office opens close enough that its smallest offer is this many px across (0.7.79, star O3-2; LandMap.open()): PLACE_LABEL_FROM and a pixel for the grid's phase, since a box PLACE_LABEL_FRO... |
| 89 | `MapFrame.CLIP_PX` | `4` | How far past the view's edges the overlay is clipped, in px: 4 - a clipped box's 2 px edge, and a line's square cap, stay off the screen. |
| 176 | `MapFrame.KEY_PX_A_SECOND` | `600` | A held key moves the ground this many screen pixels a second: 600 (spec-globe 3.11's est.) - the same at every zoom, so the expanded map's 1,345 pixels cross in a little over two seconds. |
| 179 | `MapFrame.KEY_STEP_MOST_S` | `0.1` | A frame of the pump moves the keys' ground no more than this many seconds' worth: 0.1 (★E9) - a frame held up (a far node coloured, a collection) steps the view a sixth of the screen at most, not t... |

### MapTiles.java ([map](map/MapTiles.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `MapTiles.TERRAIN_KEPT` | `2048` | Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. |
| 37 | `MapTiles.PAINTED_KEPT` | `1` | Painted tiles kept, with their inputs: 1, the hover's (star RD2-5; 64 until 0.7.87) - about 31 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512). |
| 40 | `MapTiles.BUDGET_MB` | `48` | What every cache of the view together may hold, in MB: 48 (spec-land 2.6). |
| 43 | `MapTiles.FAR_HIGHWAY_LIGHTEN` | `0.4` | How much the far view's highway line is lightened toward white against the dark ground (spec-freight 4.8): 40%. |
| 46 | `MapTiles.DESIGN_W` | `1345, DESIGN_H = 806` | The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.... |
| 435 | `MapTiles.PICTURE_FROM_PX` | `4` | A district is drawn from its picture (DistrictPicture) on a far node where it is at least this many of the node's pixels across: 4 (the study's 8.2) - a node's pixel is shown 1 to 2 screen pixels w... |
| 438 | `MapTiles.EDGE` | `0xffffffff` | The city's edge over a far node's pixels: white at 0.55, the view's edge's (MapView's EDGE_ALPHA, since 0.7.61), on the inner ring of the pixels drawn as the city's and on any holding ground of it ... |
| 441 | `MapTiles.EDGE_ALPHA` | `0.55` | ...its white's alpha: 0.55, the view's own edge's since 0.7.61 - ui/MapView's EDGE_ALPHA reads this one since 0.7.128, so the two are one dial. |
| 450 | `MapTiles.SITES_AT_MOST` | `1` | The expanded map's far node tints the world's deposits' sites as the plane's tiles do (TileRaster.siteTint(): the resource's colour, speckled, grey where worked) on a node of this many plots a pixe... |
| 453 | `MapTiles.SITE_PAD` | `16` | How far past a node's box a site's district is looked for, in plots: 16 - a site is under 8 plots wide and is listed under the district of its centre. |
| 720 | `MapTiles.NO_DISTRICT` | `- 1, VOID_PIXEL = - 2` | KeptNode's district index at a pixel with no district (no city ground there, or ground no district holds), and at one past the planet's rim on Earth, which nodePixels() draws the void. |
| 723 | `MapTiles.NO_PICTURE` | `0x4E4F504943L, NO_LAND = 0x4C414E44L, NO_RUNS = 0x4E4F52554E53L` | nodeKey()'s fixed marks: a district whose picture is not current, the land's key where the city has no land, the runs' where it has none. |

### MediumTermBond.java ([map](map/MediumTermBond.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `MediumTermBond.YEARS` | `Auction.AMORTIZING_YEARS` | The terms an amortizing bond is sold at, in years (D3): Auction.AMORTIZING_YEARS. |

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

### MiniMapFrame.java ([map](map/MiniMapFrame.md))

| line | constant | value | says |
|---:|---|---|---|
| 44 | `MiniMapFrame.STAMP_W` | `66, STAMP_H = 44` | The stamp at the rail's foot, in pixels: 66 x 44 (the study's 8.3, est.: as wide as the rail's 76 allows inside its edge and a pixel of border either side, 3 by 2 as the preview is). |
| 47 | `MiniMapFrame.PREVIEW_W` | `288, PREVIEW_H = 192` | The preview beside it on hover, in pixels: 288 x 192 (the study's 8.3, est.; the mockup's panel). |
| 50 | `MiniMapFrame.MARGIN` | `0.1` | The margin round the built-up ground and the centre, each way, as a share of their longer side (star MM2-2): 0.1 - the city 83% of the frame, a tenth of its size round it, so its white edge (MapTil... |
| 53 | `MiniMapFrame.STAMP_MS` | `8` | The most the stamp may cost the screen's thread when the map's version moves - its frame found and its far node drawn - in ms of the thread's CPU (MapCheck 12): 8, a frame's painting (spec-land 2.6... |
| 114 | `MiniMapFrame.WEST` | `0, NORTH = 1, EAST = 2, SOUTH = 3` | The sides ownedEdge() finds. |
| 241 | `MiniMapFrame.NO_CORNER` | `Long.MIN_VALUE` | What corner() says when no multiple of the stride will do. |

### MoneyAudit.java ([map](map/MoneyAudit.md))

| line | constant | value | says |
|---:|---|---|---|
| 54 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 309 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank, and since 0.7.14 the city's fund (on the end, so every other pool keeps its place). |
| 433 | `MoneyAudit.CENT` | `.01` | A harness's cent: 0.01 of a unit of a thousand dollars, the least a money comparison has allowed. |
| 436 | `MoneyAudit.RELATIVE_TOLERANCE` | `1e-12` | ...and the share of the figures compared that their own rounding is allowed past it: a part in a trillion. |
| 449 | `MoneyAudit.ULP_STEPS` | `64` | ...and the least a floor allows, in a double's steps at the size of the figures compared (0.7.64; 8 in 0.7.63): the most an identity held to a floor under a cent was measured to miss by, in steps o... |

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
| 359 | `Mortgage.Decision.DOWN_PAYMENT` | `"down payment"` | Trimmed, or refused, because its own funds could not put the down payment on it. |
| 361 | `Mortgage.Decision.LENDERS_TEST` | `"lender's test"` | ...because its rent would not cover the payment MORTGAGE_DEBT_COVERAGE times. |

### Motoring.java ([map](map/Motoring.md))

| line | constant | value | says |
|---:|---|---|---|
| 97 | `Motoring.CAR_FUEL_PER_JOURNEY` | `.002` | What a journey to work by car burns, in world money: $2.00, fifteen kilometres at eight litres a hundred and about $1.65 a litre. |
| 111 | `Motoring.LITRES_PER_JOURNEY` | `1.2` | ...and the litres in it (0.7.62): fifteen kilometres at eight litres a hundred, the same journey. |

### NationalAccounts.java ([map](map/NationalAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 231 | `NationalAccounts.HELD` | `{ Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.PETROL, Good.DIESEL, Good.LPG, Good.N...` | The goods the fifth term measures, in the order the save keeps their units (slots 15 on, EconomyManager.getNationalAccountsState()): a new one goes on the end. |
| 238 | `NationalAccounts.HELD_BEFORE_FUEL` | `3` | ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. |
| 241 | `NationalAccounts.HELD_WITH_FUEL` | `4` | ...and how many a save from 0.7.62 to 0.7.75 carries: the three and FUEL, which FuelSplit makes PETROL and DIESEL. |
| 244 | `NationalAccounts.NOT_HELD` | `Good.CARS` | ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. |
| 1152 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

### Nations.java ([map](map/Nations.md))

| line | constant | value | says |
|---:|---|---|---|
| 53 | `Nations.DEFAULT_PATH` | `Path.HISTORICAL` | The path a founding takes when nobody picks one: Historical, the founding screen's first chip (board 4). |
| 56 | `Nations.DEFAULT_SEED` | `48_213L` | The path's seed when nobody rolls one: 48,213, the founding board's (spec-nations, board 4), so every harness and the playtest meet one world. |
| 63 | `Nations.FIRST_YEAR` | `CityCalendar.EPOCH_YEAR` | The year a path begins in, January of it: the city's calendar's (CityCalendar.EPOCH_YEAR). |
| 66 | `Nations.HISTORY_LAST_YEAR` | `2025` | The last year the file's history runs to (output, prices, rates): Historical replays it to here at most, and the game's own rule goes on [R11]. |
| 69 | `Nations.PEOPLE_LAST_YEAR` | `2100` | The last year of the UN's people, the medium variant [R13]; past it people are held and a Historical nation's growth eases. |
| 72 | `Nations.FRONTIER` | `"USA"` | The nation whose output a head is the frontier the others converge to: the United States [R11]. |
| 75 | `Nations.FRONTIER_GROWTH` | `.015` | The frontier's growth a head a year (est.: the US's grew about 1.3% a year 2000-2024 [R11], about 2% over the long run). |
| 78 | `Nations.CONVERGENCE` | `.02` | The share of the gap to the frontier (in logs) a nation closes a year: Barro and Sala-i-Martin's 2% [R29]. |
| 81 | `Nations.CATCH_UP_MAX` | `.02` | The most the catch-up adds to the trend (est.: 2% of the whole gap would give the poorest about 10% a year; the typical developing nation grew about two points faster than the US in 2000-2019 [R11]). |
| 84 | `Nations.SD_RICH` | `.018` | One standard deviation of a rich nation's yearly growth: the G7's, 2000-2019 [R11]. |
| 87 | `Nations.SD_EMERGING` | `.027` | One standard deviation of an emerging economy's yearly growth: fifteen large ones, 2000-2019 [R11]. |
| 90 | `Nations.EMERGING_GAP` | `1.7` | The gap to the frontier, in logs, at which a nation swings like an emerging economy (est.: the G7's output a head is about 5.5 times those fifteen's). |
| 93 | `Nations.SD_MAX` | `.045` | The most a year's whole swing is, one standard deviation, however poor the nation (est.). |
| 96 | `Nations.WORLD_SHARE_RICH` | `.27` | The share of a rich nation's output swings the world factor explains: 27% in the industrial nations, about 11% across 106 [R30]. |
| 99 | `Nations.WORLD_SD` | `SD_RICH * Math.sqrt(WORLD_SHARE_RICH)` | The world factor's size, one standard deviation: that share of a rich nation's variance, so a fixed size that is 12% of an emerging one's [R30]. |
| 102 | `Nations.REGION_SHARE` | `.10` | The share of an emerging economy's variance a region's factor is (est., after [R30]). |
| 105 | `Nations.REGION_SD` | `SD_EMERGING * Math.sqrt(REGION_SHARE)` | A region's factor's size, one standard deviation, drawn per region and year. |
| 108 | `Nations.OWN_PERSISTENCE` | `.3` | The share of this year's own shock that carries into the next: 0.3 (spec-nations 7.2). |
| 111 | `Nations.CRISIS_RATE` | `.017` | The chance a year a nation not in a crisis starts one: Barro's falls of 15% or more in output a head, 1.7% of country-years over a century [R32]. |
| 114 | `Nations.CRISIS_DEPTH_MIN` | `.15` | The shallowest a crisis is, the share of output a head lost over its years: Barro's threshold [R32]. |
| 117 | `Nations.CRISIS_DEPTH_MAX` | `.30` | The deepest a crisis is (est.: Barro and Ursua's mean fall about 22%). |
| 120 | `Nations.CRISIS_YEARS` | `3` | The years a crisis runs, the fall spread evenly over them (est.: "over a few years" [R32]). |
| 123 | `Nations.HISTORICAL_SHOCK_SHARE` | `.5` | The size of a Historical nation's shocks after its data, against Random's: world, region and own (est.: spec 7.1, "sized as Random's but smaller"); it draws no crisis. |
| 126 | `Nations.LONG_RUN_GROWTH` | `.01` | The growth a head a Historical nation eases to past 2100 (the leaderboard's D8; the nations study's D14: about what the world has sustained over the long run). |
| 129 | `Nations.EASE_YEARS` | `50` | The years past 2100 a Historical nation's growth takes to ease from its 2091-2100 average to LONG_RUN_GROWTH. |
| 132 | `Nations.LONG_RUN_WINDOW` | `10` | The years of growth a head, to 2100, the ease starts from (the leaderboard's D8). |
| 135 | `Nations.NO_DATA_SHARE` | `.1` | The share of the frontier's output a head a nation with no output figure at all starts from (est.). |
| 138 | `Nations.LOW_INCOME_MAX` | `890` | A nation below this output a head (US$ of 2015) is low income (est.: the World Bank's FY2026 US$1,135 of 2024 over the US GDP deflator since 2015). |
| 141 | `Nations.LOWER_MIDDLE_MAX` | `3_510` | A nation below this is lower middle income (est.: the World Bank's FY2026 US$4,495 of 2024, deflated to 2015). |
| 144 | `Nations.UPPER_MIDDLE_MAX` | `10_890` | A nation below this is upper middle income, above it high (est.: the World Bank's FY2026 US$13,935 of 2024, deflated to 2015). |
| 147 | `Nations.INFLATION_MEAN` | `{.02,.035,.055,.065 }` | A year's mean inflation by income group, high, upper middle, lower middle, low (est., after the WDI's medians 2000-2019 [R11]). |
| 150 | `Nations.INFLATION_SD` | `{.012,.025,.035,.045 }` | One standard deviation of a year's inflation innovation by income group, in the same order (est., after the WDI's medians 2000-2019 [R11]). |
| 153 | `Nations.INFLATION_PERSISTENCE` | `.6` | The share of this year's inflation, above or below its group's mean, that carries into the next (est., after [R11]). |
| 156 | `Nations.INFLATION_FLOOR` | `-.05` | The lowest a drawn year's inflation is, a shade of deflation (est.); a published year is the file's whatever it was. |
| 159 | `Nations.DOLLAR` | `"USD"` | The US dollar's code: its rate is 1 always, and a money's rate is against it. |
| 162 | `Nations.RATE_SD` | `.10` | A floating money's yearly change against the dollar beyond relative parity, one standard deviation (est.). |
| 165 | `Nations.PRICE_PERSISTENCE` | `.7` | The share of a good's price shock that carries into the next year: 0.7 (spec-nations 7.2). |
| 168 | `Nations.MIN_PRICE_SWING` | `.03` | The least a good's yearly price swing, one standard deviation of its log change, is taken to be (est.: manufactured goods move little). |
| 171 | `Nations.DEFAULT_REGION` | `"East Asia & Pacific"` | The region of a nation the file gives none (Taiwan, which WDI lacks): East Asia & Pacific's. |
| 174 | `Nations.WORLD` | `1, REGION = 2, OWN = 3, CRISIS = 4, CRISIS_DEPTH = 5, INFLATION = 6, RATE = 7, PRICE = 8` | The kinds of draw, and where a draw that belongs to no nation sits among the nations' numbers. |
| 175 | `Nations.WORLD_NATION` | `0, REGION_BASE = 1000, GOOD_BASE = 2000` |  |
| 199 | `Nations.NO_DRAWS` | `new Draws(0, 0, 0, 0, false)` |  |

### NationsMap.java ([map](map/NationsMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 50 | `NationsMap.LOD_KM` | `{ 0, 1, 4, 16, 64 }` | The levels of detail of a line, as the tolerance in km each keeps its vertices within of the file's own: 0 is the file's, then 1, 4, 16 and 64 km. |
| 53 | `NationsMap.LOD_PX` | `0.5` | A frame takes the coarsest level whose tolerance is no more than this many pixels at the view's km a pixel: half a pixel, under which nobody sees the difference. |
| 56 | `NationsMap.COARSE_PER_DEGREE` | `8` | The coarse owner grid's cells a degree: 8 (2,880 x 1,440 bytes), a cell 30 of the owner layer's 15" cells across, 14 km at the equator. |
| 59 | `NationsMap.DRAW_MARGIN_PX` | `8` | A stretch of a line more than this many pixels off the canvas is dropped, and the run is cut there: 8, so a dash pattern does not start on the canvas's own edge. |
| 62 | `NationsMap.CAP_SLACK_RAD` | `0.005` | The slack added to a view's cap and a line's, radians: 0.005 (32 km), so a line the chord of its own vertices only grazes is not culled. |
| 65 | `NationsMap.DOT_BASE_PX` | `1.6` | A dot's radius is this many pixels... |
| 68 | `NationsMap.DOT_PER_ROOT_PX` | `1.9` | ...plus this many for each root of a trillion US$ of output (1.9)... |
| 71 | `NationsMap.DOT_MIN_PX` | `2.5` | ...and never less than this (2.5), so the smallest nation is still a dot to point at... |
| 74 | `NationsMap.DOT_MAX_PX` | `11` | ...and never more than this (11), so the largest does not cover its neighbours. |
| 77 | `NationsMap.LABEL_LINE_PX` | `12` | A label's second line, the figures, stands this many pixels under the first, the name (the board's 12 px for its 11 px name and 9.5 px figures). |
| 80 | `NationsMap.LABEL_H_PX` | `26` | A label's box is the wider line's width by this many pixels: 26, the two lines and the room round them. |
| 83 | `NationsMap.LABEL_ASCENT_PX` | `11` | A label's box starts this many pixels above the name's baseline (the 11 px name's height), so the box is the name, the figures and what hangs below. |
| 86 | `NationsMap.LABEL_AREA_TIMES` | `2` | A nation is written on only if its coarse area, in the view's pixels, is at least this many times the label's box: 2. |
| 89 | `NationsMap.LABEL_SAMPLES_X` | `5` | A label's box is tried at a grid of this many points across... |
| 92 | `NationsMap.LABEL_SAMPLES_Y` | `3` | ...by this many down: 3, fifteen points. |
| 95 | `NationsMap.LABEL_OWN_SHARE` | `0.6` | At least this share of the box's points must lie on the nation's own coarse cells: 0.6 (nine of fifteen). |
| 98 | `NationsMap.HIT_SLOP_PX` | `3` | A click or a hover lands on a dot within its radius and this many pixels more: 3. |
| 101 | `NationsMap.CLICK_FOR_CARD` | `"Click for its card"` | The last line of a nation's hover, which says what a click does (the board's). |
| 104 | `NationsMap.BORDER_WORDS` | `"A border"` | The legend's words for a border line (the board's). |
| 107 | `NationsMap.NATION_WORDS` | `"A nation, by its economy"` | The legend's words for a nation's dot (the board's wording, the dot's area by output). |
| 110 | `NationsMap.BORDER_ARGB` | `0xffffffff` | A border line's colour, ARGB: white, drawn at BORDER_ALPHA and dashed. |
| 113 | `NationsMap.BORDER_ALPHA` | `0.7` | A border line's opacity: 0.7. |
| 116 | `NationsMap.BORDER_DASH_PX` | `{ 4, 3 }` | A border line's dash, in pixels drawn and pixels left: 4 and 3 (the board's). |
| 119 | `NationsMap.DOT_ARGB` | `0xffe6edf3` | A nation's dot, ARGB: the board's pale blue-white (e6edf3)... |
| 122 | `NationsMap.DOT_ALPHA` | `0.85` | ...at this opacity: 0.85. |
| 125 | `NationsMap.DOT_EDGE_ARGB` | `0xff0b1118` | ...with this edge, ARGB: the map's dark (0b1118), so a dot reads on ice and on sea alike. |
| 128 | `NationsMap.BORDERS_MS` | `3` | A globe view's borders - the cull, the level, the projection and the cut - take no more than this, ms of the main thread's CPU (the wall clock where the JVM's CPU clock is coarse): 3 - measured 0.9... |
| 131 | `NationsMap.MARKS_MS` | `0.4` | A view's dots and labels - the projection, the tests and the placing - take no more than this, ms of the main thread's CPU (or the wall clock): 0.4 - measured 0.104 ms on the PC's slower view (the ... |
| 134 | `NationsMap.BUILD_MS` | `600` | Building the map from the world file - the levels, the caps and the coarse grid, which inflates every blob tile once - takes no more than this, ms of the wall clock, on the worker thread: 600 - mea... |
| 137 | `NationsMap.FINE_PER_COARSE` | `WorldData.OWNER_CELLS / COARSE_PER_DEGREE` | The 15" cells along a coarse cell's side. |
| 140 | `NationsMap.CENTRE` | `FINE_PER_COARSE / 2` | The 15" cell, along a coarse cell's side, whose owner the coarse cell takes: the middle one (15 of 0 to 29). |
| 143 | `NationsMap.R_KM` | `EarthProj.RADIUS_M / 1000` | The sphere's radius, km. |
| 145 | `NationsMap.THREE` | `new MathContext(3, RoundingMode.HALF_UP)` |  |
| 146 | `NationsMap.THOUSAND` | `BigDecimal.valueOf(1000), HUNDRED = BigDecimal.valueOf(100)` |  |
| 824 | `NationsMap.GDP_WORDS` | `"GDP, US$ of 2015"` | The GDP row's label: the file's output is real, US$ of 2015. |
| 836 | `NationsMap.BUYS_WORDS` | `"Buys from us this year", SELLS_WORDS = "Sells us this year", HAUL_WORDS = "The haul, t...` | The words of the card's trade rows: what the nation bought of the city this year, what it sold the city, and the haul in tonnes (WN3, board 1). |

### OilView.java ([map](map/OilView.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `OilView.FORECAST_YEARS` | `10` | Years the wells' chart looks ahead (mockup 2's "the next ten are pale"). |
| 66 | `OilView.PLATFORMS_LISTED` | `3` | Platforms the wells card names one by one and the chart draws apart; past them the rest are one line and one series. |
| 69 | `OilView.MONTHS_A_YEAR` | `YearBook.MONTHS_A_YEAR` | The months in a year the forecast averages. |
| 72 | `OilView.LITRES_A_BARREL` | `158.987` | A barrel's litres, 158.987 (RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY is 30.44 days of it): the reserves in barrels. |
| 622 | `OilView.PAGE_AT_1389` | `1234` | The page's width at the 1,389 x 868 window (the refinery picture's card's 1,234). |
| 625 | `OilView.BOX_PAD_X` | `12, BOX_PAD_Y = 10, BOX_EDGE = 1` | A box's padding across and down, and its border (mockup 2's .box: 10 px 12 px, 1 px). |
| 628 | `OilView.BOX_GAP` | `10, TILE_GAP = 8` | The gap between the boxes and between the figures' tiles (mockup 2's 10 and 8). |
| 631 | `OilView.WELLS_W` | `548` | The wells' box's width (mockup 2's 548); the units' box takes the rest of the row. |
| 634 | `OilView.CHART_W` | `WELLS_W - 2 * BOX_PAD_X - 2 * BOX_EDGE` | The chart's width: the wells' box inside its padding and border (mockup 2's 522). |
| 637 | `OilView.TILE_PAD_X` | `12, CARD_PAD_X = 10, CARD_GAP = 8` | A figure tile's padding across (mockup 2's .fig: 12 px), and a wells card's (.wc: 10 px). |
| 640 | `OilView.FIGURE_SIZE` | `21, TILE_WORDS = 11, CARD_WORDS = 11, CARD_HEAD = 12, CELL = 11.5, CELL_NOTE = 10.5, HE...` | The sizes the page sets its words in (mockup 2's): a figure, a tile's line, a card's line, a table's cells and its notes. |
| 644 | `OilView.DOT` | `10, DOT_GAP = 6` | A series' or a product's swatch, and the gap after it (mockup 2's .dot: 10 px; .plat: 6 px). |
| 647 | `OilView.PILL_PAD_X` | `6, PILL_GAP = 6` | A pill's padding across and the gap after it (mockup 2's .pill: 6 px, and the cell's space). |
| 650 | `OilView.CELL_PAD_X` | `6` | A table cell's padding across (mockup 2's td: 6 px). |
| 662 | `OilView.CHART_HEIGHT` | `146` | The chart's height on the page (mockup 2's 146 px). |
| 665 | `OilView.AXIS_W` | `40, YEARS_H = 20, CHART_TOP = 10, CHART_RIGHT = 8` | Room at the left for the scale's figures, under the bars for the years, over them, and at the right (mockup 2's 40, 20, 10, 8). |
| 668 | `OilView.BAR_GAP` | `3, NOW_OPACITY =.95, AHEAD_OPACITY =.32` | The gap between bars, and a bar's opacity now and ahead (mockup 2's 3 px, .95 and .32). |
| 671 | `OilView.GRIDLINES` | `5` | Gridlines the scale aims at. |
| 674 | `OilView.YEAR_EVERY` | `5` | Every how many bars a year is written under the axis (mockup 2's five). |
| 677 | `OilView.CHART_WORDS` | `9.5` | The faces' sizes: the scale's and the years' figures, the now line's words (mockup 2's 9.5). |
| 680 | `OilView.LAND` | `"#c9b68f"` | The land wells' colour (mockup 2's, the ore's), and the platforms' (mockup 2's two blues; a lighter and a darker step of the same hue for a third and the rest, ★ O12-6). |
| 682 | `OilView.PLATFORM_COLOURS` | `{ "#4fa3c7", "#2c6f8f", "#86c6e2" }` | ...Platforms A, B and C's colours, in the order the series list them (mockup 2's two blues, then the lighter step)... |
| 684 | `OilView.OTHER_PLATFORMS` | `"#1d4b62"` | ...and the rest together's, the darker step of the same hue. |
| 687 | `OilView.GRID` | `"#1d2b3c", ACCENT = "#5aa9ff"` | A gridline, and the now line (mockup 2's). |
| 754 | `OilView.NOTHING_LIFTED` | `"no well stands, and none would lift"` | What the chart says with nothing to draw. |
| 778 | `OilView.AHEAD_WORDS` | `"if nothing new is built ›"` | ...and under them. |
| 828 | `OilView.NOT_COUNTED` | `"not counted until a month runs"` | What the page says while the month is not counted. |
| 844 | `OilView.TEXT` | `RefineryView.TEXT, GOOD = "#3fb950", BAD = "#f85149", WARN = RefineryView.HOT` | The colour of a figure that is the answer, of good news and bad, and of a warning. |
| 899 | `OilView.FACT` | `RefineryView.TEXT_2` | The figure's colour on a card: the secondary grey (mockup 2's .kv b). |
| 1022 | `OilView.UNIT_HEADS` | `{ "Unit · what it turns into what", "b/d", "running", "spread", "state" }` | The table's heads, in its columns' order (mockup 2's). |
| 1025 | `OilView.UNIT_WIDTHS` | `{ 0, 64, 62, 76, 210 }` | The columns' widths (b/d, running, spread, the state; the first takes the rest - 238 px in the units' box's 650 at the 1,389 window). |
| 1028 | `OilView.PILL` | `RefineryView.TEXT_2, PILL_BUILDING = ACCENT, PILL_GOOD = GOOD, PILL_GATE = WARN, PILL_B...` | The pills' colours: neutral, under way, good, a gate the player can clear, a loss (mockup 2's .pill, .b, .w, .r). |
| 1085 | `OilView.NO_BREAK` | `'\u00a0'` | The space inside a table's item, where a line does not break: "fuel oil 7" stays whole. |
| 1183 | `OilView.PRODUCT_HEADS` | `{ "Product", "price here", "world", "× crude", "made here", "used here", "imported", "e...` | The table's heads, in its columns' order (mockup 2's). |
| 1187 | `OilView.PRODUCT_WIDTHS` | `{ 150, 76, 76, 58, 72, 72, 72, 72, 0 }` | The columns' widths (the product, the seven figures; where it went takes the rest - 560 px in the box's 1,208 at the 1,389 window). |
| 1190 | `OilView.PRODUCTS_WORDS` | `"this month · litres(M a million, k a thousand); crude, bitumen and coke in" + " tonnes...` | The products table's line over it. |
| 1194 | `OilView.PRODUCTS_RIGHT` | `"world: halfway between the import and the export price · × crude: the" + " world price...` | ...and at its right. |
| 1198 | `OilView.CRUDE` | `RefineryView.CRUDE` | Crude's colour in the table (mockup 1's). |
| 1221 | `OilView.HALF` | `.5` | Under half a unit, a figure is written as nothing (RefineryView.figure() would write "0"). |
| 1274 | `OilView.LADDER_STEPS` | `100` | The steps a lever's ladder takes from nothing to the most it may be set to: a hundredth of its reach a step (★ O12-8). |
| 1293 | `OilView.NO_RESERVE` | `"No Strategic Reserve stands.Build one under Build › Industry › Oil storage: the city" ...` | What the reserve's section says when none stands and none is held. |

### OldMetal.java ([map](map/OldMetal.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `OldMetal.RECOVERY` | `.85` | The share of the steel in what the city throws out that comes back as old steel: 85% for construction and cars (the study's 2.7, [I52]). |
| 74 | `OldMetal.STEEL_A_CAR` | `.9` | Tonnes of steel in a car scrapped (the study's 2.7, [I63]). |
| 77 | `OldMetal.STEEL_A_MATERIALS_UNIT` | `.3` | Tonnes of steel a unit of a demolished building's construction material holds: ★est., the study's 9.3 - about 3 t in a House's ten units. |
| 80 | `OldMetal.OFFCUTS_A_TONNE_FABRICATED` | `.05` | Tonnes of offcuts a tonne of fabricated steel made leaves: the Fabrication Shop's 1,260 t of steel into 1,200 (the study's 2.7). |
| 83 | `OldMetal.STEEL_A_SET` | `300` | Tonnes of steel in a wagon set worn out: a set's weight, a locomotive and what it pulls (Good.ROLLING_STOCK's 300 t). |
| 86 | `OldMetal.PAY_SHARE` | `.70` | What a yard pays the sellers for a tonne of old steel, a share of scrap's export price: 70% (Jerus's Q8, 2026-10-08). |
| 89 | `OldMetal.LINE` | `"Old metal"` | The yards' purchase, named on their input line. |
| 92 | `OldMetal.SOLD` | `"Old metal sold"` | ...and the sellers' sale, named in their other revenue. |
| 121 | `OldMetal.COPPER_A_CAR` | `.023` | Tonnes of copper in a car scrapped: 23 kg, a petrol car's (the study's 4.1, [K45]). |
| 124 | `OldMetal.COPPER_A_MATERIALS_UNIT` | `.1` | Tonnes of copper a unit of a demolished building's construction material holds: ★est., spec-materials 2.9 (0.1 t a unit). |

### OrderBook.java ([map](map/OrderBook.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `OrderBook.DUST` | `1e-9` | Anything smaller than this is the arithmetic's dust, not a quantity: an order this small is not rested and a fill this small is not a trade. |

### OutwardInvestment.java ([map](map/OutwardInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 79 | `OutwardInvestment.APPETITE` | `40` | The share of a sector's wealth it holds abroad, per unit of spread. |
| 82 | `OutwardInvestment.MAX_SHARE` | `.9` | Never more than this. |
| 89 | `OutwardInvestment.OUT_SPEED` | `.04` | How much of the gap to its target moves abroad in a month. |
| 92 | `OutwardInvestment.HOME_SPEED` | `.10` | ...and how much comes home in a month, which is faster, because it is needed. |
| 116 | `OutwardInvestment.MIN_MOVE` | `1e-9` |  |

### PartnerTrade.java ([map](map/PartnerTrade.md))

| line | constant | value | says |
|---:|---|---|---|
| 51 | `PartnerTrade.HISTORY_ROUND` | `100` | The places a table is rounded to when it goes to the history: hundredths of a unit and of a thousand. |

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

### Ports.java ([map](map/Ports.md))

| line | constant | value | says |
|---:|---|---|---|
| 97 | `Ports.SEA_FREIGHT_SHARE_LIQUID` | `.08` | Liquid bulk by sea against a lorry, at 5,000 km: .08, the mean of the VLCC, Suezmax, Aframax-LR and MR rows of the rule. |
| 100 | `Ports.SEA_FREIGHT_SHARE_DRY_BULK` | `.07` | Dry bulk by sea against a lorry, at 5,000 km: .07, the mean of the Capesize and Panamax rows. |
| 103 | `Ports.SEA_FREIGHT_SHARE_CONTAINER` | `.16` | Containers by sea against a lorry, at 5,000 km: .16, the deep-sea box row. |
| 106 | `Ports.SEA_FREIGHT_SHARE_GENERAL` | `.20` | General cargo by sea against a lorry, at 5,000 km: .20, the 5,500 t ship's row. |
| 134 | `Ports.TONNES_A_TEU` | `9` | A loaded TEU's cargo, in tonnes: 9 (the research's 8-10 [P43][P44][P45]; spec-oil 2.9). |
| 137 | `Ports.FEEDER_TEU` | `3_000` | A feeder's boxes: 3,000 TEU, the top of [P43]'s feeder class ("under 3,000"), est. |
| 189 | `Ports.WORTH_A_BERTH` | `ham.citybuildersim.sectors.Freight.MIN_LINE_UTILISATION` | The share of a berth's month the uncovered tonnes of its kind must reach before the test player orders one (spec-oil 5's O9 row): the railway's own line, Freight.MIN_LINE_UTILISATION. |
| 192 | `Ports.KINDS` | `Cargo.values().length` | The kinds of cargo a terminal takes, the length of every by-kind array here. |
| 194 | `Ports.STREAMS` | `Traffic.values().length` | The streams of traffic (Traffic), the length of every by-stream array here. |
| 381 | `Ports.ROOM_TOLERANCE` | `1e-9` | How near a cargo may come to the free room and still fit: a billionth, a store's rounding (the refiners keep exactly an MR's cargo free since 0.7.105, sectors.Refining.CARGO_KEPT_FREE). |
| 389 | `Ports.BULK_TERMINAL_SHIPS` | `java.util.List.of(Ship.PANAMAX, Ship.CAPESIZE)` | The classes the Bulk Terminal takes, and the dry bulk fleet while no dry bulk berth stands: Panamax and Capesize (FR3 call 6, docs/queue/FR3.md 2; the rule before FR3). |

### PriceIndex.java ([map](map/PriceIndex.md))

| line | constant | value | says |
|---:|---|---|---|
| 36 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 39 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 58 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |
| 88 | `PriceIndex.GROCERIES` | `0, RENT = 1, MEALS = 2, LUXURY = 3, SERVICES = 4` | The components, in the order every array here keeps them. |
| 91 | `PriceIndex.COMPONENTS` | `5` | How many there are. |
| 94 | `PriceIndex.COMPONENT_NAMES` | `{ "groceries", "rent", "meals", "luxury", "services" }` | Their names, for the year book and the screens. |
| 97 | `PriceIndex.HEALTH_FEE` | `0, TUITION = 1, FARE = 2, ACCOUNT_FEE = 3` | The fee lines inside SERVICES, in the order the fee arrays here keep them. |
| 100 | `PriceIndex.FEE_LINES` | `4` | How many there are. |
| 103 | `PriceIndex.WEIGHT_MONTHS` | `12` | Months of spending a basket's weights are struck on: the trailing year. |
| 106 | `PriceIndex.REBASE_MONTHS` | `120` | Months between one basket and the next: ten years. |
| 109 | `PriceIndex.LUXURY_WEIGHT_CAP` | `.15` | The most of the basket luxury may be, the excess spread pro rata over the rest: 15%. |
| 148 | `PriceIndex.RENT_CAP` | `1 / 3.0` | The most of the basket rent may be in the level the currency's parity is struck from: a third, about its share in real consumer price indexes (Jerus, 2026-10-21, XR1, "Basket, rent capped"). |
| 576 | `PriceIndex.SLOTS_BEFORE_CHAIN` | `5 + WINDOW + 5` | The slots a save carried before the chained basket (0.7.43): the two-component basket, the ring, the marks and the settling count. |
| 579 | `PriceIndex.CHAIN_MARKER` | `743` | What follows them in a save from 0.7.43 on, so an older array - which ends there - is told apart from one that goes on. |
| 582 | `PriceIndex.CHAIN_TAIL_0743` | `4 + COMPONENTS * 3 + FEE_LINES * 3 + 1 + WEIGHT_MONTHS *(COMPONENTS + FEE_LINES)` | The chained basket's slots behind the marker as 0.7.43 wrote them: the link, its month, the pending flag, the bases, weights and last prices, the fee lines', and the trailing year. |

### RefineryView.java ([map](map/RefineryView.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `RefineryView.TANK_ORDER` | `{ Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS, Good.FUE...` | The products in the tank's order, top to bottom: light to heavy, as the column's cuts boil (mockup 1), and the coker's coke last. |
| 71 | `RefineryView.COLUMN` | `{ Stream.GAS, Stream.LIGHT_NAPHTHA, Stream.HEAVY_NAPHTHA, Stream.KEROSENE, Stream.DIESE...` | The straight-run cuts the column holds, top to bottom: the slate's CUT_ order; the heavy crude's residue is drawn as the residue's. |
| 75 | `RefineryView.LITRES_A_TONNE_DRAWN` | `RefineryFlow.RESIDUE_LITRES_PER_TONNE` | Litres a tonne of bitumen and coke are drawn at: the residue's, RefineryFlow.RESIDUE_LITRES_PER_TONNE - the weight the flow strikes them at, so a unit's ribbons balance to the litre. |
| 175 | `RefineryView.End.FURNACES` | `new End(null, null, null)` | The furnaces, where the residue no unit takes is burned: an end with no cut, unit or product. |
| 556 | `RefineryView.HEIGHT` | `404` | The picture's height on the page (mockup 1's 404 px at 1,389 x 868). |
| 559 | `RefineryView.LEAST_WIDTH` | `1000` | The least width it is laid out at: a second rank's line of words fits before the tank (the 1,280 window's card is about 1,094). |
| 562 | `RefineryView.TOP` | `64` | Room above the columns for their headings: three lines over the column. |
| 565 | `RefineryView.PAD` | `4` | ...and the margin under and over what the columns hold. |
| 568 | `RefineryView.SOURCE_ZONE` | `182` | The sources' room at the left: an icon, three lines of words and the bar at its right edge. |
| 571 | `RefineryView.SOURCE_W` | `10` | The sources' bar. |
| 574 | `RefineryView.BUYER_ZONE` | `236` | The takers' room at the right: the bar, an icon, a name and its figure, a line of words. |
| 577 | `RefineryView.BUYER_W` | `12` | The takers' bar. |
| 580 | `RefineryView.COLUMN_W` | `82` | The column's width (mockup 1's 82). |
| 583 | `RefineryView.UNIT_W` | `56` | A unit's box (mockup 1's 66, narrower for the second rank beside the first). |
| 586 | `RefineryView.TANK_W` | `140` | The tank's width: a product's name and its figure inside its band, and a block's line under it. |
| 589 | `RefineryView.COLUMN_AT` | `.10, FIRST_RANK_AT =.29, SECOND_RANK_AT =.42, TANK_AT =.69` | Where the column, each rank of units and the tank stand, as a share of the room between the sources' bar and the takers': a rank's line of words fits between its box and the tank. |
| 592 | `RefineryView.UNIT_GAP` | `18` | The gap over a unit's box: its line of words (mockup 1's 18). |
| 595 | `RefineryView.PASS_GAP` | `3` | The gap between bands that run through the units' columns, and between a column's bands of different ends. |
| 598 | `RefineryView.SOURCE_GAP` | `26, BUYER_GAP = 12, BLOCK_GAP = 22` | The gap between the sources, between the takers, and between the tank's blocks (made, from the tanks, imported). |
| 601 | `RefineryView.LABEL_SPACING` | `33` | The least a taker's label centre stands from the next (mockup 1's 33). |
| 604 | `RefineryView.WORDS_IN_BAND` | `11` | The least band a word is written in: a cut's name in the column, a product's in the tank. |
| 607 | `RefineryView.LEAST_NODE` | `1.5` | The least a node is drawn, so a trickle shows. |
| 612 | `RefineryView.GROUND` | `"#152130"` | The ground the picture sits on: the card's (ui/Palette.RAISED), and a word's halo. |
| 614 | `RefineryView.CAP` | `"#101924"` | A roof, a cap, a dome. |
| 616 | `RefineryView.FRAME` | `"#3a4f6a"` | Their edges, and the column's and the tank's frame. |
| 618 | `RefineryView.CRUDE` | `"#a07d4a"` | Crude, in a ribbon and a source's bar. |
| 620 | `RefineryView.UNIT_FILL` | `"#1b2a3d", UNIT_EDGE = "#4a6283"` | A unit's box and its edge. |
| 622 | `RefineryView.FURNACE_FILL` | `"#2a1f1a", FURNACE_EDGE = "#6b4a3a", FLARE = "#ffb454"` | The furnaces' box and its edge, and the flare. |
| 624 | `RefineryView.TEXT` | `"#e6edf3", TEXT_2 = "#a9b8c9", TEXT_3 = "#8496ab"` | The words: the head's, the body's, the faint. |
| 626 | `RefineryView.HOT` | `"#e3b341"` | A word that something is imported. |
| 628 | `RefineryView.ON_BAND` | `"#0b1118"` | The words on a pale band. |
| 1220 | `RefineryView.AFTER_UNIT_GAP` | `6` | The gap under a unit's box before a band runs on. |
| 1223 | `RefineryView.BLOCK_WORDS` | `16` | The room under each of the tank's blocks for its line ("IMPORTED" and its litres): its baseline 13 px under, and the line's descent. |
| 1226 | `RefineryView.IMPORT_EDGE` | `"#5a6d84"` | The edge of the tank's imported block (mockup 1's). |
| 1229 | `RefineryView.FURNACE_WORDS` | `"burned %s · no diesel to cut it"` | The furnaces' line under their name. |
| 1232 | `RefineryView.NO_UNITS` | `"NO CONVERSION UNIT · EACH CUT SOLD AS IT IS"` | The units' heading when none stands. |
| 1235 | `RefineryView.FIGURE_IN_BAND` | `46` | The room a product's figure has at the right of its band in the tank. |
| 1238 | `RefineryView.BLOCK_FIGURE` | `52` | ...and a block's litres at the right of its line under it. |
| 1241 | `RefineryView.FIGURE_ROOM` | `58` | The room a figure has at the right of a band or a taker's name. |
| 1519 | `RefineryView.LIST_MOST` | `3` | The most products a taker's line names before "and more". |
| 1571 | `RefineryView.STRIP_HEAD` | `"THIS MONTH, BY PRODUCT"` | The strip's heading words. |
| 1574 | `RefineryView.STRIP_WORDS` | `"litres(M a million, k a thousand), bitumen and coke in tonnes · bar: taken here," + " ...` | ...and its line. |
| 1592 | `RefineryView.CELL_LINES` | `{ "made here", "imported", "exported", "tanks", "price here" }` | The strip's line names, in a cell's order. |

### Resource.java ([map](map/Resource.md))

| line | constant | value | says |
|---:|---|---|---|
| 181 | `Resource.NO_TYPES` | `java.util.List.of(Deposit.FieldType.NONE), IRON_TYPES = java.util.List.of(Deposit.Field...` | Each resource's field types as typesOf() hands them out: NONE alone, or the two of iron, coal, copper and stone, the commoner first. |

### Rollover.java ([map](map/Rollover.md))

| line | constant | value | says |
|---:|---|---|---|
| 106 | `Rollover.NETTING_MONTHS` | `12` | How far back the surplus that nets a maturity is read, in months: Jerus's "net of last year's surplus" - a year. |
| 109 | `Rollover.BILL_MONTHS` | `12` | The bill TWELVE_MONTH_BILL rolls into, in months: twelve, the longest bill the treasury sells (bills run 1 to 12 months, Auction.BILL_MONTHS). |

### RunWorks.java ([map](map/RunWorks.md))

| line | constant | value | says |
|---:|---|---|---|
| 31 | `RunWorks.SQ_FT_PER_M2` | `1 / LandManager.SQ_M_PER_SQ_FT` | Square feet in a square metre. |
| 34 | `RunWorks.RUN_WIDTH_M` | `SitePreparation.PAD_WIDTH_M` | The width of a run across the ground, metres: one plot (30 m), as a foothill pad's (SitePreparation.PAD_WIDTH_M). |
| 41 | `RunWorks.FOOTHILL_USD_PER_M2` | `SitePreparation.USD_PER_M2` | Foothill: a bench cut and filled across the slope, with its wall where it meets the ground above - a pad's arithmetic on the class's mean grade over the run's width, which is a pad's (SitePreparati... |
| 48 | `RunWorks.DECK_USD_PER_M2` | `250 * SQ_FT_PER_M2` | A bridge's deck, US dollars a m2: Wisconsin DOT's 2025 planning cost to replace a structure is $220 a sq ft of deck (wisconsindot.gov, "bos-cost", June 2025) and Colorado DOT's 2025 cost data book'... |
| 54 | `RunWorks.DECK_WIDTH_M` | `2 *(2 * 3.66 + 3.05 + 1.22)` | The deck a highway's viaduct carries, metres: four lanes of 3.66 m (12 ft), an outside shoulder of 3.05 m (10 ft) and an inside one of 1.22 m (4 ft) each way, the Interstate standard's - 23.2 m of ... |
| 57 | `RunWorks.STEEP_USD_PER_M2` | `DECK_USD_PER_M2 * DECK_WIDTH_M / RUN_WIDTH_M` | Steep: the run carried on a viaduct, its deck over the run's width, about $2,080 a m2 of the run's land. |
| 66 | `RunWorks.TUNNEL_BORE_USD_PER_KM` | `35e6` | One bore of a tunnel, US dollars a km: the European Commission's rail unit-cost study finds tunnels cost 25-30 million euros a km on average and over 80 million in very complex cases (Assessment of... |
| 69 | `RunWorks.TUNNEL_BORES` | `2` | The bores a highway's tunnel needs: two, one a direction, its four lanes two to a bore. |
| 72 | `RunWorks.VERY_STEEP_USD_PER_M2` | `TUNNEL_BORES * TUNNEL_BORE_USD_PER_KM /(1000 * RUN_WIDTH_M)` | Very steep: the run in tunnel, its bores over the run's width, about $2,330 a m2 of the run's land. |
| 78 | `RunWorks.HIGHWAY_GRADE` | `0.06` | The steepest grade a highway climbs, 6%: the Interstate standard's in mountainous terrain (AASHTO's geometric design policy; WSDOT Design Manual M 22-01, chapter 1220). |
| 84 | `RunWorks.RAIL_GRADE` | `0.022` | ...and a main-line railway, 2.2%: the practicable limit the Baltimore and Ohio set in the 1830s, and the ruling grades of the western US mountain main lines, about 2 to 2.4% (Trains, "Mountain rail... |
| 92 | `RunWorks.RAIL_FACTOR` | `HIGHWAY_GRADE / RAIL_GRADE` | How many times a highway's works a m2 of a railway's land needs on any class above flat, about 2.7: a line gains a height in no less than the height over its grade of length, so to cross the same g... |

### SalesTaxLedger.java ([map](map/SalesTaxLedger.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `SalesTaxLedger.TAXABLE_SALES` | `0, IMPORT_TAX = 1, ZERO_RATED = 2, CREDITED_INPUT = 3, PAYABLE = 4, CREDIT = 5, SLOTS = 6` | slots in a row |

### SaveWriter.java ([map](map/SaveWriter.md))

| line | constant | value | says |
|---:|---|---|---|
| 36 | `SaveWriter.CLOCK_MS` | `25` | The most the month that autosaves may hold the clock, in milliseconds: the fill of the save and the snapshot of the history. |
| 39 | `SaveWriter.FLOOR_SECONDS` | `30` | The least real time, in seconds, between two autosaves (Jerus, 2026-10-22: "At most every 30 s"); a month due inside it waits for the next due month past it. |
| 112 | `SaveWriter.SHARED` | `new SaveWriter()` | The one writer every city in the JVM shares: what shared() hands out. |

### SeaLanes.java ([map](map/SeaLanes.md))

| line | constant | value | says |
|---:|---|---|---|
| 30 | `SeaLanes.Legs.ON_LINE` | `1e-6` | How far off a kept leg's line, in plots, a leg's start may lie and be on it: a join point is worked out in doubles. |

### SeaRoutes.java ([map](map/SeaRoutes.md))

| line | constant | value | says |
|---:|---|---|---|
| 94 | `SeaRoutes.CELL` | `16` | The sea grid's cell, in plots: 16, 480 m (spec 4.1; the prototype's CG). |
| 97 | `SeaRoutes.SAMPLE` | `4` | A cell's ground is read at a sample every this many plots: 4, so 4 x 4 samples a cell. |
| 100 | `SeaRoutes.SALT_LEAST` | `12` | A cell is sea when at least this many of its 16 samples are salt: 12, three quarters (star O13-5; the prototype's 95% shut a 300 m narrows). |
| 103 | `SeaRoutes.SHORE_COST` | `3` | A shore cell's cost against an open one: 3 (spec 4.1: "three times the cost one cell from the shore"). |
| 106 | `SeaRoutes.OFFING_M` | `10_000` | How far past the city's radius the offing lies: 10 km (spec 4.1). |
| 109 | `SeaRoutes.BEARING_STEP` | `5` | The bearings tried for the offing, every this many degrees: 5 (spec 4.1). |
| 112 | `SeaRoutes.RAY_CELLS` | `400` | The longest run of sea a bearing is measured to, in cells: 400, 192 km (the prototype's). |
| 115 | `SeaRoutes.NEAR_CELLS` | `6` | How far round a berth its first sea cell is looked for, in cells: 6 (the prototype's nearest_sea()). |
| 118 | `SeaRoutes.ABYSS_M` | `3_000` | How far past the offing a boat sails into the abyss, fading: 3 km - "fading over a few km" (spec 4.1); less where land comes sooner (abyssRun()). |
| 121 | `SeaRoutes.SPREAD_DEG` | `3` | The most a call's last leg is turned either way, in degrees: 3 - "a few degrees" (spec 4.1). |
| 124 | `SeaRoutes.EXPANSIONS_MOST` | `250_000` | The most cells a route's A* looks at before it takes the straight lane: 250,000 - a way of about 1,000 km through open sea. |
| 127 | `SeaRoutes.JOIN_M` | `OFFING_M` | How far by water a terminal looks for a lane to join, in metres: OFFING_M, the offing's own margin (SL1, 2026-10-28). |
| 130 | `SeaRoutes.JOIN_CELLS` | `(int) Math.ceil(JOIN_M / World.PLOT_M / CELL)` | ...in cells of CELL plots, rounded up: 21 (no cell of a join's search is farther from the berth's start in a straight line). |
| 133 | `SeaRoutes.REFIND_M` | `OFFING_M / 2` | How much the city's radius may grow before the routes are found again, in metres: OFFING_M / 2, 5 km - they are found at the radius rounded up to a multiple of it (band(); SL1, 2026-10-28). |
| 136 | `SeaRoutes.BLOCK` | `64` | Cells a side of a block of the grid read at once: 64, 1,024 plots (a block a World.regionTerrain() call). |
| 423 | `SeaRoutes.OPEN_M` | `OFFING_M` | How far out a berth's water must reach for a terminal to stand on it, in metres: OFFING_M, the offing's own margin past the city (star O13-5). |
| 602 | `SeaRoutes.Router.SIDE` | `2 * JOIN_CELLS + 1` | A join's scratch window: JOIN_CELLS either way of the berth's start cell. |

### Sector.java ([map](map/Sector.md))

| line | constant | value | says |
|---:|---|---|---|
| 246 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 336 | `Sector.Staffing.ANY` | `new Staffing(1, new double [ 0 ], new double [ 0 ], new double [ 0 ], new boolean [ 0 ])` | Always staffable: a building with no posts, or nobody to ask. |
| 653 | `Sector.CONSUMABLES` | `"Consumables"` | The consumables' name on a sector's input line. |
| 740 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 746 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 775 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 789 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 803 | `Sector.LOADS_A_VAN_MONTH` | `TONNES_PER_VAN / Good.VANS.tonnesPerUnit()` | The loads a van-month carries: TONNES_PER_VAN at a van's own weight, three tonnes (Good.VANS.tonnesPerUnit()) - forty. |
| 806 | `Sector.KM_A_LOAD` | `50` | How far a load goes, there and back: fifty kilometres (docs/studies/spec-oil.md 2.5, est.; to confirm). |
| 809 | `Sector.DIESEL_LITRES_PER_100_KM` | `12` | What a laden van or truck burns: twelve litres of diesel a hundred kilometres (docs/studies/spec-oil.md 2.5, est.; to confirm). |
| 812 | `Sector.DIESEL_LITRES_A_VAN_MONTH` | `LOADS_A_VAN_MONTH * KM_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | ...so a van-month burns LOADS_A_VAN_MONTH x KM_A_LOAD x DIESEL_LITRES_PER_100_KM / 100 litres: 240. |
| 1093 | `Sector.BORROWING_COST_MOST_MONTHS` | `Mortgage.MORTGAGE_AMORTIZATION_MONTHS` | The longest life a cost of borrowing is expensed over, in months: a mortgage's amortization, forty years (0.7.102, A16) - the clock's length. |
| 2225 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 2228 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

### SectorBooks.java ([map](map/SectorBooks.md))

| line | constant | value | says |
|---:|---|---|---|
| 464 | `SectorBooks.Debt.KINDS` | `BusinessDebtManager.DEBT_KINDS` | The four kinds, as the statements name them: BusinessDebtManager.DEBT_KINDS. |
| 796 | `SectorBooks.DEBT_ROWS` | `12` | The rows a saved Debt is written as: six arrays, then moved's three and movedAtClose's three. |
| 841 | `SectorBooks.SHARES_SLOTS` | `7` | The slots a saved Shares is written as: shares, outstanding, price, fair value, traded (1 or 0), dividend, dividend a year. |

### SectorFlow.java ([map](map/SectorFlow.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `SectorFlow.THROTTLES` | `{ "staffed", "power", "water", "roads", "well", "freight" }` | The six throttles in getOperatingRate()'s order, as the screen names them (the sixth, "freight", was "vans" until FR2). |

### SectorStatements.java ([map](map/SectorStatements.md))

| line | constant | value | says |
|---:|---|---|---|
| 94 | `SectorStatements.NOTICE` | `.001` | A residual line - NOT ACCOUNTED FOR - is shown from a dollar (the cash page's rule since 0.7.30): under that it is the floating point. |
| 185 | `SectorStatements.THOUSANDS_UNTIL` | `10_000_000` | D2: a statement is in $ thousands - the model's own unit - until its largest figure passes seven digits, and then in $ millions, so the landlords' $1.2B a month does not run to ten. |
| 325 | `SectorStatements.REVENUE` | `"revenue", SALES_TAX = "salesTax", NET_REVENUE = "netRevenue", INPUTS = "inputs", GROSS...` | The statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by (Table.row()). |
| 333 | `SectorStatements.SUBSIDY` | `"subsidy", ARREARS = "arrears", DEPOSIT_INTEREST = "depositInterest", COUPONS = "coupon...` | The outside-the-trading-result lines' ids, in their order (F1; since 0.7.75 the bonds written off and F2's three). |
| 339 | `SectorStatements.FINANCE_NOTE` | `5` | The finance costs' note. |
| 341 | `SectorStatements.OUTSIDE_NOTE` | `6` | The outside-the-trading-result note. |
| 374 | `SectorStatements.OUTSIDE_IDS` | `{ SUBSIDY, ARREARS, DEPOSIT_INTEREST, COUPONS, FOREIGN_INTEREST, FORGIVEN, WRITTEN_OFF,...` | The outside lines' row ids, in outside()'s order. |
| 378 | `SectorStatements.OUTSIDE_LABELS` | `{ "Subsidy from the city", "Arrears the city paid", "Interest on its bank balance", "Co...` | ...and their labels, in the same order. |
| 460 | `SectorStatements.ASSETS_HEAD` | `"assetsHead", CURRENT_HEAD = "currentHead", CASH = "cash", STOCK = "stock", RECEIVABLES...` | The balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. |
| 472 | `SectorStatements.STOCK_NOTE` | `7, BUILDINGS_NOTE = 8, ABROAD_NOTE = 9, CAPITAL_NOTE = 10, LAND_NOTE = 11` | The stock's note, the buildings' and what it holds abroad - and since 0.7.75 its share capital's (R3), and since ET3 its land's, by the ground it stands on. |
| 548 | `SectorStatements.OPERATING_HEAD` | `"operatingHead", NET_INCOME = "netIncome", PAID_EARLIER = "paidEarlier", BORROWING_PAID...` | The cash flow statement's row ids, which the screens and SectorStatementCheck look a row up by. |
| 648 | `SectorStatements.EQ_START` | `"eq.start", EQ_PROFIT = "eq.profit", EQ_OUTSIDE = "eq.outside", EQ_FOUNDED = "eq.founde...` | The statement of changes in equity's row ids, which the screens and SectorStatementCheck look a row up by. |
| 654 | `SectorStatements.CAPITAL` | `0, KEPT = 1` | The equity statement's two columns, each row's parts: share capital, and what it kept and revalued. |
| 657 | `SectorStatements.REVALUED_IDS` | `{ EQ_STOCK, EQ_LAND, EQ_BUILDINGS, EQ_ABROAD }` | R6's four parts, ids: what the prices did to what it began the month with. |
| 730 | `SectorStatements.LAND_CLASSES` | `3` | The classes of ground the books value land on (ET3): flat, foothill, and the steep and very steep ground only works stand on. |
| 733 | `SectorStatements.LAND_IDS` | `{ "land.flat", "land.foothill", "land.steep" }` | Note 11's rows, by class: ids and labels. |
| 734 | `SectorStatements.LAND_LABELS` | `{ "Flat", "Foothill", "Steep(its mines)" }` |  |
| 737 | `SectorStatements.LAND_AREA` | `0, LAND_PRICE = 1` | In a land note's row, `parts` holds {square kilometres, the price a square metre}; `now` is its value, and the rows add to the sheet's land. |
| 901 | `SectorStatements.B_INCOME_HEAD` | `"b.incomeHead", B_OTHER_INTEREST = "b.otherInterest", B_INTEREST = "b.interest", B_EXPE...` | The bank's statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by. |
| 909 | `SectorStatements.BANK_INTEREST` | `{ { "From the businesses", Bank.Line.FROM_BUSINESSES }, { "On the businesses' bonds it ...` | The interest income lines: {label, line}. |
| 919 | `SectorStatements.BANK_FEES` | `{ { "On the families' accounts", Bank.Line.ACCOUNT_FEES }, { "On the businesses' new lo...` | ...and the fees. |
| 984 | `SectorStatements.BS_ASSETS_HEAD` | `"bs.assetsHead", BS_LOANS_HEAD = "bs.loansHead", BS_GROSS = "bs.gross", BS_NET = "bs.ne...` | The bank's balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. |

### Sectors.java ([map](map/Sectors.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = "Construct...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a... |
| 95 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERIALS, BUSIN...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |
| 323 | `Sectors.HOUSEHOLDS` | `"Households"` | The name the households' own imports are kept under among a good's buyers: the cars they buy from the world (Game.getHouseholdCarImports()). |
| 326 | `Sectors.CITY` | `"City"` | ...and the city's, among crude's buyers and sellers: its strategic reserve (0.7.85; StrategicReserve). |

### ShipShapes.java ([map](map/ShipShapes.md))

| line | constant | value | says |
|---:|---|---|---|
| 31 | `ShipShapes.LENGTH_M` | `245` | The Aframax's length, the research's one ship drawn to scale (4.4; mockup 3): 245 m. |
| 34 | `ShipShapes.AFRAMAX_T` | `75_000` | ...and its cargo, the class's (Ports.Ship.AFRAMAX): what the cube root is taken of. |
| 37 | `ShipShapes.BEAM` | `42.0 / 245` | A hull's beam over its length: mockup 3's Aframax, 42 m on 245. |
| 40 | `ShipShapes.BOOST` | `1.6` | Boats are drawn this many times their size below BIG_TILES_FROM px a plot: 1.6 (the research's 4.4, mockup 3's legend). |
| 43 | `ShipShapes.TRUE_FROM` | `MapFrame.BIG_TILES_FROM` | ...and at true size from here, px a plot: MapFrame.BIG_TILES_FROM, 6 - the close zoom. |
| 46 | `ShipShapes.LEAST_PX` | `7` | The shortest a boat is drawn whole, in px: 7 - BEAM of it is 1.2 px, its deck and bridge under a pixel each; shorter, its hull is a dart of its colour (spec-oil 6's fallback, "dots at L1"; star O13... |
| 49 | `ShipShapes.IMPORT` | `0xff5aa9ff` | An import coming in full: the research's blue, mockup 3's #5aa9ff. |
| 52 | `ShipShapes.EXPORT` | `0xfff2a65a` | An export going out full: mockup 3's orange, #f2a65a. |
| 55 | `ShipShapes.EMPTY` | `0xff8496ab` | An empty leg, riding high: mockup 3's grey, #8496ab. |
| 58 | `ShipShapes.BOXES` | `0xffd6dde4` | A box ship, loaded both ways: mockup 3's white, #d6dde4. |
| 61 | `ShipShapes.HULL_LADEN` | `0xff1a2836, HULL_EMPTY = 0xff22303e` | A hull's fill, laden and empty: mockup 3's #1a2836 and #22303e. |
| 64 | `ShipShapes.BRIDGE` | `0xffc3ccd3` | The bridge at the stern: mockup 3's #c3ccd3. |

### ShortTermTBill.java ([map](map/ShortTermTBill.md))

| line | constant | value | says |
|---:|---|---|---|
| 48 | `ShortTermTBill.TERMS` | `Auction.BILL_MONTHS` | The terms a bill is sold at, in months (D3): Auction.BILL_MONTHS. |

### Sickness.java ([map](map/Sickness.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `Sickness.RING` | `13` | Slots in each band's ring: 0 is under a month, 12 is a year or more. |
| 51 | `Sickness.DEADLY_FROM` | `2` | The first slot that can die: sick for more than two months. |
| 61 | `Sickness.EXTRA_SICKNESS` | `1.0` | How much sicker babies and seniors are than the city, with none of their own care: twice. |
| 64 | `Sickness.RECOVERY_UNTREATED` | `.5` | The share of the sick who get better in a month, with no general care... |
| 67 | `Sickness.RECOVERY_SERVED` | `.9` | ...and with general care for everybody. |
| 70 | `Sickness.MAX_SHARE` | `Health.MAX_SICK_RATE` | No band has more of itself sick than the city rate may. |

### SitePreparation.java ([map](map/SitePreparation.md))

| line | constant | value | says |
|---:|---|---|---|
| 28 | `SitePreparation.MEAN_FOOTHILL_GRADE` | `0.19` | The foothill class's mean grade (10% to 30%), 19%: fitted here from the study's measured class shares by an exponential fall of land with grade between the class edges - 17.9% to 19.7% at every one... |
| 31 | `SitePreparation.PAD_WIDTH_M` | `30` | The width of a pad across the contours, metres: one plot (30 m). |
| 34 | `SitePreparation.EARTH_M3_PER_M2` | `MEAN_FOOTHILL_GRADE * PAD_WIDTH_M / 4` | Earth moved a m2 of pad, m3: balanced cut and fill on a pad W wide on a grade g moves g x W / 4 (spec 4.1), 1.425. |
| 37 | `SitePreparation.WALL_M2_PER_M2` | `MEAN_FOOTHILL_GRADE` | Wall face a m2 of pad, m2: one wall the height of the pad's drop, g m2 of face per m2 of pad (spec 4.1). |
| 44 | `SitePreparation.EARTH_USD_PER_M3` | `20` | US dollars a m3 of earth, balanced cut and fill with compaction and no haul off site: US$15-30 a cubic yard (about $20-39 a m3) is the commonest 2026 benchmark (snaptrude.com/blog/cut-fill-earthwor... |
| 51 | `SitePreparation.WALL_USD_PER_M2` | `430` | US dollars a m2 of retaining wall, face area: a segmental block wall is US$15-35 a sq ft in national guides (fixr.com/costs/retaining-wall-building) and $70-140 for an engineered wall with geogrid ... |
| 54 | `SitePreparation.USD_PER_M2` | `EARTH_M3_PER_M2 * EARTH_USD_PER_M3 + WALL_M2_PER_M2 * WALL_USD_PER_M2` | US dollars to prepare a m2 of foothill: its earth and its wall at their rates (about $110). |

### SocialSecurity.java ([map](map/SocialSecurity.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `SocialSecurity.DEFAULT_CONTRIBUTION_RATE` | `.0595` | Taken off every wage, at the real CPP employee rate. |
| 70 | `SocialSecurity.CONTRIBUTION_RATE` | `DEFAULT_CONTRIBUTION_RATE` | Kept as the default so a screen or a harness written against the constant still names the right number. |
| 86 | `SocialSecurity.DEFAULT_PENSION_REPLACEMENT` | `.45` | What the pension replaces, as a share of an unskilled wage. |
| 89 | `SocialSecurity.PENSION_REPLACEMENT` | `DEFAULT_PENSION_REPLACEMENT` |  |

### StreetCurves.java ([map](map/StreetCurves.md))

| line | constant | value | says |
|---:|---|---|---|
| 24 | `StreetCurves.CURVE_RADIUS` | `2` | The radius the corners of a street along a cut are rounded with, in plots: 2 (60 m) - the railway's is 3 (TrackCurves.RADIUS), a street's turn at the near view is tighter; where a leg is shorter th... |
| 27 | `StreetCurves.FIT` | `0.75` | How far the simplified line of a path may lie from the middles of its plots, in plots: three quarters (Douglas-Peucker) - more than a 45-degree staircase's corners lie from the line through its ste... |
| 30 | `StreetCurves.BAND_HALF` | `0.5` | The half width of the band a street's curve is drawn with, in plots: a half, the widest surface a street has (DistrictPlan.FULL, a whole plot) - a plot whose square lies nearer the line than this i... |
| 33 | `StreetCurves.EDGE` | `1e-9` | The margin a distance must be inside BAND_HALF to count, so a plot beside a line along a plot's middles (exactly BAND_HALF away) is not reached, whatever the last bit of the arithmetic says. |

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
| 108 | `TaxPolicy.MAX_OFFSET` | `.30` | How far a band or sector may be moved from its tax's base rate, either way. |
| 157 | `TaxPolicy.DEFAULT_FARMLAND_RELIEF` | `1.0` |  |
| 160 | `TaxPolicy.FARM_SECTOR` | `Sectors.AGRICULTURE` | The sector whose ground the relief applies to. |
| 203 | `TaxPolicy.MAX_CONTRIBUTION` | `.20` | Nobody hands over more than a fifth of a wage, whatever the deficit. |
| 206 | `TaxPolicy.MAX_REPLACEMENT` | `1.00` | A pension of more than one unskilled wage is a wage, not a pension. |
| 248 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currenc... |
| 263 | `TaxPolicy.DEFAULT_FIXED_GRANT` | `DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage()` | THE GRANT FOLLOWS PRICES (0.7.19): the default grant as a FIXED amount, in founding thousands - $525 a month, the Canada Student Grant (2026-27) this basis's share was read off, so at founding, whe... |
| 266 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 269 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 272 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 292 | `TaxPolicy.DEFAULT_FOOD_ASSISTANCE` | `0` | No voucher: the city it was. |
| 295 | `TaxPolicy.MAX_FOOD_ASSISTANCE` | `1.00` | A voucher for every basket an eligible household eats is the most there is. |
| 472 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.FIXED` | Where the grant starts: a fixed real amount, kept up with prices - the expected price level since 0.7.42, the price index from 0.7.19 (the founding rule, a share of the unskilled wage, until then). |
| 475 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 478 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 481 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 484 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 487 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 490 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 493 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so ... |
| 525 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 737 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 740 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 743 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 746 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 866 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 869 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | The dial's cap, $50 a ride, twenty times the default, in founding money: past it no car owner rides while a journey's fuel costs under half of it, and the commuters with no car of their own still d... |
| 910 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 1031 | `TaxPolicy.DEFAULT_FUEL_DUTY` | `0` | No duty: the city it was (Jerus's A7: default 0). |
| 1034 | `TaxPolicy.MAX_FUEL_DUTY` | `.001` | The dial's top, a dollar a litre at founding prices, in thousands: past the dearest duty the research found, the Netherlands' EUR 0.789 a litre of petrol (2024; runs/research-pump.md 6 [30]) - a ce... |
| 1073 | `TaxPolicy.DEFAULT_PORT_DUES` | `1.0` | The default due: the berths' cost recovered over their life at the city's rate (D9). |
| 1076 | `TaxPolicy.MAX_PORT_DUES` | `2.0` | The dial's top: twice the default. |
| 1441 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 1444 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 1447 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 1450 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 1453 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 1456 | `TaxPolicy.STATE_BEFORE_REAL_GRANT` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | ...and one from before the real FIXED grant (0.7.19): a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |
| 1459 | `TaxPolicy.STATE_BEFORE_REAL_FARE` | `STATE_BEFORE_REAL_GRANT + 1` | ...and one from before the real fare (0.7.42): one slot on top saying the FIXED amount is real, in founding money (0.7.19) - see realiseFixedGrant(). |
| 1462 | `TaxPolicy.STATE_BEFORE_FOOD_ASSISTANCE` | `STATE_BEFORE_REAL_FARE + 1` | ...and one from before food assistance (0.7.43): one slot on top saying the fare is real, in founding money (0.7.42) - see realiseFare(). |
| 1465 | `TaxPolicy.STATE_BEFORE_FUEL_DUTY` | `STATE_BEFORE_FOOD_ASSISTANCE + 1` | ...and one from before the fuel duty (0.7.103): the food assistance dial on top (0.7.43). |
| 1468 | `TaxPolicy.STATE_BEFORE_PORT_DUES` | `STATE_BEFORE_FUEL_DUTY + 1` | ...and one from before the port dues dial (FR3): the fuel duty on top (0.7.103). |
| 1471 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_PORT_DUES + 1` | This build's array: the port dues dial on top (FR3). |

### TempDirs.java ([map](map/TempDirs.md))

| line | constant | value | says |
|---:|---|---|---|
| 42 | `TempDirs.BASE_NAME` | `"citybuildersim-tmp"` | The folder, under the system temp folder, that holds one folder per JVM. |

### TilePainter.java ([map](map/TilePainter.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `TilePainter.TILE` | `World.TILE` | Plots on a tile's side: World.TILE, 32. |
| 67 | `TilePainter.PLOTS` | `TILE * TILE` | Plots on a tile: 1,024. |
| 72 | `TilePainter.EMPTY` | `0` | Nothing on it. |
| 74 | `TilePainter.ROAD` | `1` | A road: a street of the plan, or a highway. |
| 76 | `TilePainter.BUILDING` | `2` | A building. |
| 78 | `TilePainter.FIELD` | `4` | A resource's site with nothing on it. |
| 80 | `TilePainter.RAIL` | `5` | A railway's track (0.7.72): its Painted.road is 0, or the kind of the street that crosses the track there. |
| 83 | `TilePainter.RAIL_OVER` | `CityRuns.F_RAIL_OVER` | A plot of the runs where the railway bridges a highway (0.7.89, CityRuns.F_RAIL_OVER; spec 2.8): Input.fixed's code, the highway's plot with the track over it. |
| 86 | `TilePainter.M_MAIN` | `32` | A plot of the main line, from the rail gateway to the freight station: drawn as the main line, and an arterial or boulevard crossing it on a bridge (spec 4.3, D18). |
| 88 | `TilePainter.M_DOUBLE` | `64` | A main-line plot drawn as double track: the city has a Freight Line (spec 4.3). |
| 90 | `TilePainter.SIDING_PLOT` | `1, SIDING_END = 2` | A plot of a siding laid inside a district's plan (Input.siding; batch FR7 slice 5, D14), and (SIDING_END) its first plot, beside the works' box, which carries the loading shed. |
| 93 | `TilePainter.M_SHED` | `128` | A siding's last plot, beside the works' box: drawn with the siding's loading shed, a small mark in TRANSPORT's colours (FR7 2026-10-18, CityRuns.SIDING; and a district plan's siding's SIDING_END, s... |
| 96 | `TilePainter.TRACK` | `4` | A road plot's kind past BuildingVisual's GRAVEL, PAVED and HIGHWAY (Painted.road, 0.7.88): a TRACK, a street the city has bought no road for (spec 2.5, R4). |
| 101 | `TilePainter.QUAY` | `1` | A plot's work at sea (Input.sea, Painted.sea's low two bits): a terminal's quay, out over the water from its box (CityShore)... |
| 103 | `TilePainter.JACKET` | `2` | ...an offshore platform's jacket... |
| 105 | `TilePainter.WELL` | `3` | ...a platform well, on the middle of its sea site. |
| 107 | `TilePainter.SEA_WORK` | `3` | The low two bits of Painted.sea. |
| 109 | `TilePainter.SEA_RING` | `4` | Painted.sea's bit for a plot a platform's safety ring crosses... |
| 111 | `TilePainter.SEA_PIPE` | `8` | ...and for one a crude pipeline crosses (buried: it takes no plot, and is drawn over what stands there). |
| 114 | `TilePainter.PLATFORM_ZONE_M` | `500` | A platform's safety zone, drawn as a faint ring about its jacket: 500 m (the research's 3.3 [W32]). |
| 117 | `TilePainter.RING_PLOTS` | `PLATFORM_ZONE_M / World.PLOT_M` | ...its radius in plots: 16.7. |
| 120 | `TilePainter.JACKET_PLOTS` | `2` | A jacket's side, in plots: 2, 60 m - mockup 3's platform, 12 px at 6 m a pixel (72 m), in whole plots (star O13-6). |
| 125 | `TilePainter.UNOWNED` | `0` | A site on ground the city does not own. |
| 127 | `TilePainter.UNWORKED` | `1` | ...on the city's ground, its holding not yet worked. |
| 129 | `TilePainter.WORKING` | `2` | ...in the holding being worked: drawn half grey. |
| 131 | `TilePainter.WORKED_OUT` | `3` | ...in a holding worked out: drawn grey (the mockup's star 7). |
| 136 | `TilePainter.S_KIND` | `7` | A plot's street as its district's plan gives it the painter (CityMap.Drawn, Input.street), a byte: its kind in the low three bits - DistrictPlan's NONE to UNDER... |
| 138 | `TilePainter.S_WIDTH_SHIFT` | `3` | ...its surface's width in bits 3 and 4: 1 half (15 m), 2 full (30 m), 0 none... |
| 140 | `TilePainter.S_ROLE_SHIFT` | `5` | ...its role in bits 5 and 6: 0 a cell's street (or one along a cut, or the join's), S_ARTERIAL, S_BOULEVARD, S_ACCESS... |
| 142 | `TilePainter.S_ARTERIAL` | `1, S_BOULEVARD = 2, S_ACCESS = 3` | ...those roles' values, an arterial's 1 and a boulevard's 2 (DistrictPlan's ROLE_ARTERIAL and ROLE_BOULEVARD), and (batch FR7) an access road's 3 (ROLE_ACCESS)... |
| 144 | `TilePainter.S_BRIDGE` | `0x80` | ...and a bridge over fresh water in bit 7. |
| 147 | `TilePainter.STREET` | `1` | A painted street plot's role (Painted.role): a cell's street... |
| 149 | `TilePainter.ARTERIAL` | `2` | ...an arterial, on a cell's ring (every 32 plots, 960 m)... |
| 151 | `TilePainter.BOULEVARD` | `3` | ...a boulevard's, either row of a boulevard cell's arterials... |
| 153 | `TilePainter.ACCESS` | `4` | ...and (batch FR7, D17) an access road, the street from a port's, a yard's or a depot's gate to the highway's ramp or the arterial grid: an arterial for the junctions it makes. |
| 167 | `TilePainter.JUNCTION_APART` | `8` | The + junction floor: 8 plots - eight House footprints (a House's 8,000 sq ft is drawn on one whole plot, BuildingVisual.footprint()), Jerus's "about 8 houses' length" from one + junction to the ne... |
| 170 | `TilePainter.REACH` | `JUNCTION_APART / 2` | A building is near a road when one lies within this many plots of it, across corners: 4, half the junction floor - a block between streets at the floor is 7 plots across, and its middle plot 4 from... |
| 180 | `TilePainter.STREET_BRIDGE` | `6` | A street's longest crossing of bridgeable water (fresh, and since ET6 the shallow sea; World.bridgeable()), gravel or paved: 6 plots (180 m), the mockup's paved bridge (its star 1); gravel's too si... |
| 190 | `TilePainter.MAX_BRIDGE` | `{ 0, STREET_BRIDGE, STREET_BRIDGE, 34 }` | The longest crossing of bridgeable water, in plots, by road kind: gravel and paved STREET_BRIDGE, a highway 34 plots (1,020 m; est., spec-earth2-terrain 6.2 / D12, Jerus 2026-10-12 "all rec" - 14 p... |
| 193 | `TilePainter.RAIL_BRIDGE` | `34` | The longest crossing of bridgeable water a railway's track makes: 34 plots (1,020 m; est., spec-earth2-terrain 6.2 / D12), as a highway's (star N3-6) - 14 until ET6, which Halifax's two bridges out... |
| 196 | `TilePainter.PART_BIT` | `1<<21` | A building's box in Input.boxes with this bit set is the part, on the tile, of a building counted on another (FX5 Y2): a yard whose box spans tiles is counted on the tile holding its first plot, an... |
| 199 | `TilePainter.RING` | `TILE + 2 * REACH` | Plots: the side of Input.ring, the tile and REACH plots about it on every side (FX5). |
| 207 | `TilePainter.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west. |
| 210 | `TilePainter.RESERVE_INTS` | `8` | The ints Input.reserve holds a reserve in: kind, stub, junction x and y, box x0, y0, w, h. |
| 318 | `TilePainter.Input.CV_OWN` | `1, CV_BAND = 2, CV_END = 4` | A plot's curve mark (curve, curveRing; batch CL4, slice D): CV_OWN, a plot a curve is laid on (DistrictPlan.Curve.plots()); CV_BAND, a plot its band reaches that is not its own, kept free of buildi... |
| 331 | `TilePainter.Input.CURVE_HEAD` | `3` | The numbers before a curve's geometry in curveGeom: its kind, its radius and the count of numbers after. |
| 396 | `TilePainter.Input.YARD_INTS` | `10` | The ints Input.yard holds a yard in (FX5 Y2): its box (x0, y0, w, h), its type id, its layout (TileRaster.yardLayout()'s east-west, line, span start and end), and 1 when it is the freight station. |
| 423 | `TilePainter.Input.BEND_DOUBLES` | `10` | The ints Input.bend holds a bend in (FX5): its net, corner x and y, incoming and outgoing headings (4), radius, tangent length, and 1 on double track. |

### TileRaster.java ([map](map/TileRaster.md))

| line | constant | value | says |
|---:|---|---|---|
| 135 | `TileRaster.GROUND` | `{ 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa, 0xffdfe7ee, 0xffd2b48c, 0...` | Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend; and since 0.7.127 (batch E7; spec-globe 3.9) ICE, the founding globe's ice, 0xdfe7ee, and DESER... |
| 144 | `TileRaster.RELIEF_GREY` | `{ 0, 0xffc8c3b8, 0xff9b968c, 0xff6c6862 }` | The grey each relief paints on open dry ground (ET1 slice D; spec-earth2-terrain 9.3; Jerus 2026-10-12, D4), by World's relief (FLAT, FOOTHILL, STEEP, VERY_STEEP): FLAT none - flat ground keeps its... |
| 152 | `TileRaster.FOREST_MIX` | `{ 0, 0.50, 0.50, 0.60 }` | How far forest on a slope is shaded toward its slope's grey (D5, the study's way B; the mockup's mixes, estimated by eye on Medellin): FLAT none, foothill and steep half the way, very steep 0.60 - ... |
| 155 | `TileRaster.COVER_RELIEF` | `new int [ World.RELIEFS * World.CLASSES ]` | Every cover's colour at every relief, by relief x CLASSES + cover: the table colour() reads - GROUND on the FLAT row, built once. |
| 193 | `TileRaster.ROAD` | `{ 0, 0xffbdb8ae, 0xffa4aab0, 0xff3b4048 }` | Each road kind's colour (gravel, paved, highway): the mockup's MAP, but gravel's. |
| 196 | `TileRaster.GRAVEL_GRAIN` | `0xff9a958b` | The loose stone's speckle on a gravel street from 4 px a plot (the near view, L0): a shade darker than its grey. |
| 199 | `TileRaster.GRAVEL_GRAIN_SHARE` | `.18` | The share of a gravel street's pixels speckled: 18%. |
| 202 | `TileRaster.RAIL_LINE` | `0xff707070` | The railway's line, and its casing from 4 px a plot (0.7.72) until 0.8.1 drew the track as rail; since, a track plot's colour in raster() below LINES_FROM px a plot alone (plotColour()): openstreet... |
| 205 | `TileRaster.RAIL_DASH` | `0xffffffff` | ...and its dashes, white on every other plot along it until 0.8.1: carto's rail dash. |
| 210 | `TileRaster.BALLAST` | `0xff6f675c` | The track's ballast bed (spec-freight 4.3's grey-brown #6f675c). |
| 212 | `TileRaster.SLEEPER` | `0xff4a3f35` | A sleeper across the bed (spec-freight 4.3's #4a3f35). |
| 214 | `TileRaster.STEEL` | `0xffc9ced3` | The rails' steel (spec-freight 4.3's #c9ced3). |
| 216 | `TileRaster.RAIL_DARK` | `0xff3a3632` | The line the track is at the middle and far views (spec-freight 4.3's dark #3a3632). |
| 219 | `TileRaster.RAILS_FROM` | `6` | Pixels a plot from which the track is two rails on sleepers (the 8-px raster). |
| 221 | `TileRaster.BED` | `0.52` | A single track's bed, a share of the plot: the paved road's width, as the track has today. |
| 223 | `TileRaster.BED_DOUBLE` | `0.86` | Double track's bed, a share of the plot: the highway's. |
| 225 | `TileRaster.GAUGE` | `0.24` | Between a track's two rails, a share of the plot (at 8 px: the rails a pixel either side of the track's middle). |
| 227 | `TileRaster.TRACKS_APART` | `0.44` | Between double track's two tracks, middle to middle. |
| 229 | `TileRaster.SLEEPERS_EVERY` | `0.25` | A sleeper every quarter plot (spec-freight 4.3: "sleepers across it every 0.25 plot"). |
| 231 | `TileRaster.CROSSBUCK` | `0xffffffff` | A level crossing's white crossbuck (spec-freight 4.3 "Crossings"). |
| 233 | `TileRaster.BARRIER` | `0xffd04a3c` | ...and its barrier's red (spec-freight 4.3 "Crossings"). |
| 238 | `TileRaster.JUNCTIONS_FROM` | `6` | Pixels a plot from which junctions are drawn (the 8-px raster): signals, roundabouts, diamonds and stacks, and since FR7 kerb corners and interchanges' loops; a highway's end comes down to the grou... |
| 240 | `TileRaster.JUNCTION_BOX` | `0xff8e949a` | A signalled junction's box, a shade darker than paved. |
| 242 | `TileRaster.ZEBRA` | `0xfff2f2f2` | A crossing's stripes and stop lines. |
| 244 | `TileRaster.ZEBRA_DEPTH` | `0.3` | Plots: a zebra crossing's depth along its arm. |
| 246 | `TileRaster.ROUNDABOUT_RADIUS` | `0.75` | Plots: a roundabout's outer radius, spec: "a circle of 1.5 plots" - about a crossing one plot wide; since FX5 a wider crossing's ring is that and half of the rectangle's longer side less a plot more. |
| 248 | `TileRaster.ROUNDABOUT_ISLAND` | `0.35` | Plots: its planted middle's radius, widened as the ring is. |
| 250 | `TileRaster.ISLAND` | `0xff6f9a4f` | The planted middle. |
| 252 | `TileRaster.SLIP_LENGTH` | `2` | Plots: a diamond's slip road along the verge each side of its street. |
| 254 | `TileRaster.JUNCTION_RUN` | `4` | Plots: the least run of junction streets through a plot along an axis for a road to cross there (FX3): a road up to three plots wide - a cell's boulevard, two deep, beside the next cell's road, as ... |
| 256 | `TileRaster.ROUNDABOUT_ROADS` | `5` | Roads: the fewest that leave a crossing (counted by Crossings.roadsLeaving(): each run of streets along a side of its rectangle, each slanted street off a corner) for it to be a roundabout, not a s... |
| 259 | `TileRaster.TICKS_FROM` | `2` | Pixels a plot from which the blocks' view draws the track's cross-ticks (the L1 raster). |
| 262 | `TileRaster.DECK` | `0xff5d4c3c` | A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. |
| 267 | `TileRaster.TUNNEL_PORTAL` | `0xff090b0d` | A tunnel's portal (ET4): the near black band across a highway or a railway where it goes into the hill or comes out, on the last plot of the tunnel at its edge, a little wider than the run (its cro... |
| 270 | `TileRaster.TUNNEL_DASH` | `0.6` | The share of a plot a tunnel's dashed line fills along the run, from the plot's start: 0.6 (the highway's centre line is .55), in the run's own colour - the highway's surface, the track's steel - a... |
| 273 | `TileRaster.TUNNEL_PORTAL_DEPTH` | `0.25` | A portal's depth along the run, a share of a plot: 0.25, a pixel at least - 1 px at 4 px a plot, 2 at 8. |
| 282 | `TileRaster.UNOWNED_DIM` | `0.4` | Ground no one in the city owns is drawn this much of the way to the map's background: 40%. |
| 285 | `TileRaster.VOID` | `0xff16222c` | The map's background, which unowned ground is dimmed toward: the mockup's --map-void. |
| 288 | `TileRaster.SITE_TINT` | `0.36` | A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). |
| 291 | `TileRaster.SITE_SPECKLE` | `0.75` | ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). |
| 294 | `TileRaster.SPECKLE_SHARE` | `0.15` | The share of a site's plots speckled: 15%. |
| 297 | `TileRaster.WORKED_GREY` | `0xff929496` | A worked-out site's grey: the mockup's mined grey. |
| 300 | `TileRaster.RING` | `0xff2b2d31` | A mine's ring and centre mark: the mockup's ring. |
| 303 | `TileRaster.INSET_FROM` | `3` | Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). |
| 306 | `TileRaster.EDGE_FROM` | `6` | ...and from which it is edged: 6 (the mockup's outlines). |
| 309 | `TileRaster.ROAD_WIDTH` | `{ 0, 0.30, 0.52, 0.86 }` | A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its ... |
| 312 | `TileRaster.ROAD_LEAST_PX` | `{ 0, 1, 2, 3 }` | ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). |
| 315 | `TileRaster.HIGHWAY_CASE` | `0xff1d2126` | A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... |
| 318 | `TileRaster.CENTRE_LINE` | `0xfff0cf5a` | ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). |
| 321 | `TileRaster.LINES_FROM` | `4` | Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. |
| 337 | `TileRaster.YARD_GROUND` | `0xff8c8478` | A yard's ground, light ballast (spec-freight 4.3's yard). |
| 339 | `TileRaster.YARD_TRACKS` | `8` | The fan's sidings (spec-freight 4.3: 6 to 10). |
| 341 | `TileRaster.DEPOT_TRACKS` | `3` | A Rail Depot's sidings (FR7 slice 3, "2 to 3 sidings and a shed"): 3 - its box is 8 plots by 7, a Rail Terminal's 24 by 24. |
| 359 | `TileRaster.YARD_TRACK_APART` | `1.5` | Plots between the fan's tracks, middle to middle. |
| 361 | `TileRaster.YARD_LADDER` | `3` | Plots at each end over which the fan closes to the line. |
| 363 | `TileRaster.SHED_DEPTH` | `2.5, SHED_LENGTH = 0.6` | The freight shed: plots deep, and its share of the fan's length. |
| 365 | `TileRaster.ORE_HEAP` | `0xffc9b68f` | A bulk yard's stockpiles, the spec's sand colour. |
| 367 | `TileRaster.CONTAINER` | `0xffd6dde4` | A container yard's boxes, the boats' white. |
| 369 | `TileRaster.GANTRY` | `0xff2b2f36` | Its two cranes. |
| 371 | `TileRaster.WAGON` | `0xff7b4b3a` | A wagon standing. |
| 373 | `TileRaster.WAGON_LENGTH` | `0.5` | Plots: a wagon about 15 m. |
| 376 | `TileRaster.SMALL_MOST` | `0.92` | A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before)... |
| 379 | `TileRaster.SET_BACK` | `0.07` | ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). |
| 383 | `TileRaster.VERGE` | `0xffbfd39a` | A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). |
| 386 | `TileRaster.TRACK_DASH` | `0xff8a6a3c` | A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). |
| 389 | `TileRaster.SURFACE` | `{ 0, DistrictPlan.HALF, DistrictPlan.FULL }` | A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_... |
| 392 | `TileRaster.PACKED_DIM` | `0.35` | A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, ... |
| 653 | `TileRaster.YARD_CANVAS` | `ThreadLocal.withInitial(() -> new int [ 0 ])` |  |
| 897 | `TileRaster.HEAP_LEAST` | `2` | The least share of the ground past a bulk yard's fan, in pixels either way, a heap is drawn on: 2. |
| 931 | `TileRaster.LOOK_FROM` | `3` | Pixels a plot from which a materials building is drawn as its look rather than a box of its colour: 3 (TileRaster's INSET_FROM; the L0 image is 4). |
| 934 | `TileRaster.BENCHES` | `6` | The benches a pit or a quarry steps down by from its rim to its middle: 6. |
| 937 | `TileRaster.POND` | `0xff4f8fbf` | A leach plant's pond: #4f8fbf. |
| 940 | `TileRaster.HEADFRAME` | `0xff2b2622, WINDING = 0xff8a7f72` | A shaft's headframe: #2b2622, and the winding house in it #8a7f72. |
| 943 | `TileRaster.CONVEYOR` | `0xff9a9aa2` | The wash plant's conveyor: #9a9aa2. |
| 946 | `TileRaster.LOGS` | `0xff5a3e22` | A camp's log stacks: #5a3e22. |
| 949 | `TileRaster.CROWN` | `0xff6a9a55` | Planted forest's crowns, in rows over its darker floor: #6a9a55. |
| 952 | `TileRaster.RIDGE_DARK` | `0.18` | How much darker a works' roof ridges are than its fill: 18%. |
| 1035 | `TileRaster.QUAY_DECK` | `0xff3a4655` | A quay's deck: mockup 3's jetty, #3a4655... |
| 1038 | `TileRaster.QUAY_EDGE` | `0xff5a6676` | ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. |
| 1041 | `TileRaster.JACKET_FILL` | `0xff2a3540` | A platform's jacket: mockup 3's #2a3540... |
| 1044 | `TileRaster.OIL_TAN` | `0xffc9b68f` | ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). |
| 1047 | `TileRaster.RING_ALPHA` | `0.25` | A platform's 500 m ring over the water: the tan at the mockup's 25%... |
| 1050 | `TileRaster.RING_DASH_PX` | `3` | ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). |
| 1053 | `TileRaster.PIPE_ALPHA` | `0.5` | A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). |
| 1055 | `TileRaster.PIPE_ON_PX` | `2, PIPE_OFF_PX = 4` | ...its dash, 2 px on and 4 off along the pipe (the mockup's "2 4"). |
| 1170 | `TileRaster.BLOCK_ALPHA` | `{ 0.4, 0.6, 0.8, 0.96 }` | A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... |
| 1173 | `TileRaster.BLOCK_FILLS` | `{ 0.12, 0.28, 0.5 }` | ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). |
| 1176 | `TileRaster.BLOCK_MIDDLE` | `4` | Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... |
| 1179 | `TileRaster.BLOCK_FAR` | `8` | ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. |
| 1290 | `TileRaster.HIGHWAY_LEAST_PX` | `2` | The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). |
| 1608 | `TileRaster.NOTCH` | `1<<24` | What marks an outer plot in avenueNotches(): bit 24, over the arm's code (low two bits) and the codes of the two steps either side of it (bits 8 to 15 and 16 to 23). |
| 2461 | `TileRaster.RAMP_WIDTH` | `ROAD_WIDTH [ BuildingVisual.GRAVEL ]` | A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). |
| 2598 | `TileRaster.Crossings.N` | `TilePainter.RING, R = TilePainter.REACH` | The side of Input.ring and how far it reaches past the tile on every side, in plots: TilePainter.RING and TilePainter.REACH. |
| 2600 | `TileRaster.Crossings.NONE` | `new Crossings()` | A tile with no junction street about it: no crossings. |
| 3142 | `TileRaster.LOOP_RADIUS` | `2.5, LOOP_AT = 5.0` | A loop's radius, in plots, and how far from the crossing's middle along each axis a cloverleaf's loop is centred (batch FR7): 2.5 and 5.0, so a loop fills its quadrant of the 19-plot reserve. |
| 3145 | `TileRaster.TRUMPET_RADIUS` | `2.0, TRUMPET_OUT = 5.5, TRUMPET_ACROSS = 3.0` | A trumpet's loop: its radius and how far out along its stub and across it its centre lies, in plots - 2.0, and 5.5 and 3.0, inside the 11-plot reserve. |
| 3148 | `TileRaster.LOOP_WIDTH` | `0.34` | A loop's and a slip's road across, as a share of a plot: 0.34, a pixel or two wider than a gravel street's, so it reads as a ramp at 8 px a plot and not as a street. |
| 3151 | `TileRaster.SLIP_OFF` | `0.45, SLIP_OUT = 9.0, TRUMPET_SLIP_OUT = 10.0` | Where a slip leaves its highway, in plots from the junction's middle: just off the highway's band (0.45) and (along it) 9.0 out for a cloverleaf, 10.0 for a trumpet's stub. |
| 3235 | `TileRaster.KERB_RADIUS` | `0.25` | A kerb corner's radius, in plots: 0.25 (7.5 m), the corner of a + of two streets in a homes cell - and of a street's T on an arterial there. |
| 3238 | `TileRaster.ESTATE_T_RADIUS` | `0.5` | An estate T's corner radius, in plots: 0.5 (15 m) - wide, where trucks turn (spec-freight 4.6: "a T with wide radius corners"). |
| 3452 | `TileRaster.SIDING_SHED` | `0.7` | A siding's loading shed, the share of a plot it fills (FR7 slice 3): 0.7, a small block at the siding's end beside the works. |
| 3802 | `TileRaster.HIGHWAY_LEG` | `2` | How far the straight legs of a highway bend are drawn past its tangent points, in plots: two - past the last plot the bend replaces (a plot and a half out), where the plots' own bands take over. |
| 4054 | `TileRaster.ROAD_SMALL` | `{ 0, 0xffbdb8ae, 0xff6a7077, 0xff3b4048 }` | A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it ... |

### TimeSkipReport.java ([map](map/TimeSkipReport.md))

| line | constant | value | says |
|---:|---|---|---|
| 334 | `TimeSkipReport.AUTO_SALES_MOST` | `5000` | The most automatic sales a report keeps: a skip of centuries is not a ledger of them. |

### Timing.java ([map](map/Timing.md))

| line | constant | value | says |
|---:|---|---|---|
| 60 | `Timing.PROPERTY` | `"cbs.timing"` | The system property that makes timing soft: {@code -Dcbs.timing=soft}; anything else, or none, is hard. |
| 63 | `Timing.SOFT` | `"soft"` | The property's value that makes timing soft. |
| 66 | `Timing.HARD` | `"hard"` | The property's value the gate passes to say hard out loud (any value but SOFT is hard). |
| 69 | `Timing.HARNESSES` | `List.of("WorldCheck", "MapCheck", "ScaleCheck", "PlanCheck", "EarthCheck", "GlobeCheck"...` | The harnesses that hold a timing assertion: what {@code AllChecks --timing} runs, and the ship gate (TimingCheck holds it to the sources). |
| 74 | `Timing.SOFT_LINE` | `Pattern.compile("\\sSOFT - ")` | A harness's assertion line that went soft, as {@link #verdict} words it: what AllChecks counts in a run's logs. |
| 86 | `Timing.TSV_PROPERTY` | `"cbs.timing.tsv"` | The system property that names the file every timing assertion appends its figure to, {@code -Dcbs.timing.tsv=target/checks/timing.tsv} (TM3); none, and nothing is written. |
| 89 | `Timing.misses` | `new ArrayList<>()` | The misses since the last verdict, each "figure unit, budget budget unit": what the next verdict prints. |
| 95 | `Timing.readings` | `new ArrayList<>()` | The figures judged since the last verdict. |

### TrackCurves.java ([map](map/TrackCurves.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `TrackCurves.RADIUS` | `3` | A curve's radius on the line, in plots: 3 (90 m), a few plots at the near view (Jerus, 2026-10-20); real freight curves are 150 to 300 m, wider than a corridor's corner here. |
| 36 | `TrackCurves.STEP` | `0.25` | The routes' sampling along an arc or a ladder, in plots: a quarter, as MapCheck reads the trains' routes. |
| 39 | `TrackCurves.RAILWAY` | `0, HIGHWAY = 1` | A bend's net: the railway's 0 and (FX5 slice D) the highway's 1 - its own numbering, not CityRuns's (whose HIGHWAYS is 0 and RAILWAY 1). |
| 42 | `TrackCurves.HIGHWAY_RADIUS` | `4` | A highway bend's radius, in plots: 4 - a carriageway is broader than a railway's bed, and a 45-degree joint's tangent is then 1.66 plots (FX5 slice D: "a plot and a half"), so the arc leaves the st... |
| 45 | `TrackCurves.STRAIGHT` | `1e-6` | The turns of a polyline smaller than this, in radians, are straight: a millionth. |
| 175 | `TrackCurves.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | Plots beside one another: north, east, south, west, as TilePainter has them. |
| 213 | `TrackCurves.ON_PATH` | `0.75` | How far a plot's middle may lie from a leg's line, in plots, and still be a plot of the highway on it: three quarters - a staircase's corner plot lies 0.71 from its stretch's line. |

### Trade.java ([map](map/Trade.md))

| line | constant | value | says |
|---:|---|---|---|
| 25 | `Trade.WORLD` | `"world"` | The other side of every export and every import. |
| 28 | `Trade.HOUSEHOLDS` | `"households"` | The households, as a buyer of the basket. |
| 31 | `Trade.CITY` | `"city"` | The treasury, as a buyer of its own buildings' materials and repairs. |

### TradeLanes.java ([map](map/TradeLanes.md))

| line | constant | value | says |
|---:|---|---|---|
| 37 | `TradeLanes.MIN_LANE` | `300` | A partner whose exports and imports together come to less than this, in the city's money, thousands a month, gets no lane: 300, the board's D$0.3M. |
| 40 | `TradeLanes.LANE_STEP` | `2` | A lane is sampled along its great circle every this many degrees: 2 (222 km), so a frame's chords sag by under a pixel at the closest view that draws them. |
| 43 | `TradeLanes.BUNDLE_DETOUR` | `0.15` | A partner joins a nearer one's lane only if the route through it is within this share longer than the direct great circle: .15. |
| 46 | `TradeLanes.LANE_MIN_PX` | `1` | A lane's width is at least this many pixels... |
| 49 | `TradeLanes.LANE_MAX_PX` | `8` | ...and at most this many, for the largest edge's sum (whole pixels, so a frame strokes one path a width). |
| 52 | `TradeLanes.BUCKETS` | `2 * LANE_MAX_PX` | The width classes a frame strokes by: the sales' LANE_MAX_PX, then the purchases' (a bucket is a colour and a whole width). |
| 55 | `TradeLanes.ARROWS_MAX` | `12` | A generated world's map shows at most this many partners as arrows, the largest by money; the rest are counted and left out. |
| 58 | `TradeLanes.ARROW_INSET_PX` | `14` | An arrow's tip stands this many pixels inside the canvas's edge. |
| 61 | `TradeLanes.ARROW_GAP_PX` | `30` | Two arrows stand at least this many pixels apart along the edge: 30, so the widest pair of strokes and their heads do not overlap. |
| 64 | `TradeLanes.LANE_FRAME_MS` | `0.5` | A frame's lanes - the cull, the projection and the cut, for 196 partners all over MIN_LANE - take no more than this, ms of the main thread's CPU (or the wall clock): 0.5. |
| 67 | `TradeLanes.LANE_BUILD_MS` | `2` | Building the lanes of a month for 196 partners all over MIN_LANE takes no more than this, ms of the same clock: 2. |
| 70 | `TradeLanes.EPS` | `1e-9` | The slack of the tree's comparisons, radians and unit lengths: a billionth, so a rounding never splits a vertex from itself. |
| 73 | `TradeLanes.STAND_ON` | `1e-7` | A partner this near a vertex of the tree, radians, stands on it and needs no lane: 1e-7 (0.6 m) - an arc cosine cannot tell two nearer places apart. |
| 75 | `TradeLanes.R_M` | `EarthProj.RADIUS_M` |  |

### TrainSchedule.java ([map](map/TrainSchedule.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `TrainSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trains a game month: the boats' (spec-freight 4.4, "the clock is the boats'"). |
| 87 | `TrainSchedule.TRAIN_PLOTS_A_SECOND` | `50` | A train's mean pace along its route, in plots a second: 50 (a visual pace; its easing doubles it at the world line's end, a plot and a half a frame at 8 px and 60 frames a second). |
| 90 | `TrainSchedule.LEG_SECONDS` | `6` | The shortest leg, in or out, in seconds: 6 (spec-freight 4.4, a visual pace), for a route of 300 plots or less. |
| 93 | `TrainSchedule.LEG_MOST` | `10` | The longest leg, in seconds: 10, for a route of 500 plots or more, so a long way stays inside the 60 s month (a visit is two legs and a stand). |
| 96 | `TrainSchedule.DWELL_SECONDS` | `3` | Seconds a train stands on its siding before its wagons are counted: 3 (spec-freight 4.4). |
| 99 | `TrainSchedule.DWELL_A_WAGON` | `0.025` | ...and the seconds a wagon adds to its stand: 0.025, so a unit train of 80 stands 5 s and a mixed train of 15 3.375 s - the longer the train, the longer it takes to load or empty. |
| 102 | `TrainSchedule.WORLD_PLOTS` | `64` | A route's part along the world line, past the city's ground, in plots: 64, about 1.9 km, over which a train fades in. |
| 105 | `TrainSchedule.UNIT_TONNES` | `8_000` | A unit train's payload, hoppers or tank wagons: 8,000 t (est., between Europe's 4,000 t block trains [R47] and a 12,100 t North American coal train [R39]). |
| 108 | `TrainSchedule.MIXED_TONNES` | `1_500` | A mixed (manifest) train's payload: 1,500 t (Europe's route limits run 1,300 to 2,400 t [R45]). |
| 111 | `TrainSchedule.INTERMODAL_BOXES` | `100` | An intermodal train's boxes: 100 (a European 750 m train is 50 wagons [R46]; Union Pacific's averaged 173 [R40]). |
| 114 | `TrainSchedule.WAGON_TONNES` | `100` | Tonnes a wagon carries, for the count of wagons drawn: 100 (spec-freight 4.4, "the payload / 100 t"). |
| 117 | `TrainSchedule.BOXES_A_WAGON` | `2` | Boxes a flat wagon carries: 2, so an intermodal train is 50 wagons, as [R46]'s. |
| 120 | `TrainSchedule.CAR_PLOTS` | `TileRaster.WAGON_LENGTH` | A wagon's length in plots: TileRaster.WAGON_LENGTH, about 15 m. |
| 123 | `TrainSchedule.LOCO_PLOTS` | `0.7` | A locomotive's length in plots: 0.7, about 21 m. |
| 126 | `TrainSchedule.MOST_A_MONTH` | `30` | The most trains a month drawn one for one: 30, a train about every 2 s (spec-freight 4.4's worked example, 25, "reads as a busy line, not a blur"). |
| 129 | `TrainSchedule.SALT` | `0x7A41_45EDL` | The salt the trains' hashes start from. |
| 132 | `TrainSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A train's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 134 | `TrainSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<ARRIVAL_BITS...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 136 | `TrainSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |

### TrainShapes.java ([map](map/TrainShapes.md))

| line | constant | value | says |
|---:|---|---|---|
| 30 | `TrainShapes.LOCO` | `0xff2b2f36` | A locomotive, dark (spec-freight 4.4). |
| 33 | `TrainShapes.WIDTH` | `0.42` | A vehicle's drawn width in plots: 0.42, about the track bed's (TileRaster.BED 0.52), so the train covers its rails. |
| 36 | `TrainShapes.GAP` | `0.08` | The gap left between two vehicles, in plots: 0.08, so the wagons read one by one from MARKS_FROM in. |
| 39 | `TrainShapes.CARS_FROM` | `MapFrame.L0_FROM` | Pixels a plot from which a train is drawn vehicle by vehicle: MapFrame.L0_FROM, the near view; below it, a dash. |
| 42 | `TrainShapes.MARKS_FROM` | `8` | Pixels a plot from which a wagon shows its kind's mark: 8, where a wagon is 4 px long. |
| 45 | `TrainShapes.HEAP` | `TileRaster.ORE_HEAP` | A hopper's heap of ore: TileRaster.ORE_HEAP, the yards' stockpiles' sand. |

### TreasuryFund.java ([map](map/TreasuryFund.md))

| line | constant | value | says |
|---:|---|---|---|
| 121 | `TreasuryFund.MAX_DIAL` | `3.0` | The most the dial takes: three times the year's surplus - Jerus's "max 300%"; past 100% the extra comes from the treasury's cash. |
| 124 | `TreasuryFund.EQUITY_WEIGHT` | `.70` | The share of the market book in company shares: 70%, the Government Pension Fund Global's strategic equity weight (nbim.no/en/investments); the rest is the businesses' bonds. |
| 127 | `TreasuryFund.REBALANCE_OVER` | `.74` | Rebalanced when its equity share passes this: 74%, the GPFG mandate's "If the equity share ... |
| 130 | `TreasuryFund.REBALANCE_UNDER` | `.04` | ...or falls this far under EQUITY_WEIGHT: four points, the same mandate's "more than four percentage points lower than the weight in the strategic benchmark index". |
| 133 | `TreasuryFund.RULE_PREMIUM` | `Exchange.SPREAD / 2` | The rule bids at the desk's ask: the cheapest price anybody stands ready to sell at; at fair value it met nobody (C3). |
| 136 | `TreasuryFund.OWNERSHIP_LIMIT` | `.10` | The most of any one company's shares the rule holds: 10%, the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares in an individual company" (14 May 2018). |
| 139 | `TreasuryFund.TRANSFER_RATE` | `.03` | Norway's fiscal rule, a year of the fund's whole value: 3% - transfers follow the fund's expected real return, 3% since 2017 (regjeringen.no). |
| 142 | `TreasuryFund.YEAR_MONTHS` | `12` | A year, in months: the transfer's twelfth and the surplus's year. |
| 145 | `TreasuryFund.WITHDRAWAL_STEP` | `TRANSFER_RATE / YEAR_MONTHS` | The withdrawal dial's step, a share of the fund's whole value a month: a twelfth of TRANSFER_RATE, so Norway's rule is one step (Jerus, 2026-10-05: "even 0 or 10% a month"). |
| 148 | `TreasuryFund.DEFAULT_WITHDRAWAL_STEPS` | `1` | A new city's and an older save's withdrawal: Norway's rule, the transfer paid since 0.7.14 to the bit. |
| 151 | `TreasuryFund.MAX_WITHDRAWAL_STEPS` | `40` | The most it withdraws: 10% of its value a month. |
| 154 | `TreasuryFund.VOLATILITY_MONTHS` | `12` | The months of the bank share's price history its volatility is read over, for the warrants' value: a year of the monthly prices the history keeps (HistorySave's share prices). |
| 157 | `TreasuryFund.OFFER_AGAIN_MONTHS` | `3` | A declined offer comes back after this many months while the bank is still under its minimum: a quarter (Jerus, round 2: "Quarter") - and the bank asks no sooner after an accepted one either, whose... |

### TruckSchedule.java ([map](map/TruckSchedule.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `TruckSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trucks a game month: the boats' (spec-freight 4.7, as the trains). |
| 87 | `TruckSchedule.LEG_SECONDS` | `6` | The shortest leg a truck takes to run its route, in or out, in seconds: 6 (spec-freight 4.7, a visual pace, the trains'), for a route of 300 plots or less. |
| 90 | `TruckSchedule.LEG_MOST` | `TrainSchedule.LEG_MOST` | The longest leg, in seconds: 10, the trains' (TrainSchedule.LEG_MOST), for a route of 500 plots or more, so a trip of the whole city's length stays inside the month (a visit is two legs and a stand). |
| 93 | `TruckSchedule.DWELL_SECONDS` | `2` | Seconds a truck stands at its gate, loading or unloading: 2 (spec-freight 4.7). |
| 96 | `TruckSchedule.WORLD_PLOTS` | `TrainSchedule.WORLD_PLOTS` | A route's part along the world line, past the city's ground, in plots: 64, as the trains' (TrainSchedule.WORLD_PLOTS), over which a truck fades in. |
| 99 | `TruckSchedule.TRUCK_PLOTS` | `0.55` | An articulated truck's length in plots: 0.55, 16.5 m of a plot's 30 m (TrainSchedule.CAR_PLOTS, a wagon, is TileRaster.WAGON_LENGTH for scale). |
| 102 | `TruckSchedule.MOST_A_MONTH` | `1000` | The most trucks a month drawn one for one: 1,000, a truck about every 0.06 s of a 60 s month (spec-freight 4.7; the roads take many more marks than the rails). |
| 105 | `TruckSchedule.SALT` | `0x7C4B_E5E7L` | The salt the trucks' hashes start from. |
| 108 | `TruckSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A truck's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 110 | `TruckSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<ARRIVAL_BITS...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 112 | `TruckSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |
| 512 | `TruckSchedule.CATCHMENT_DISTRICTS` | `DistrictPlan.ACCESS_RAMP_DISTRICTS` | How far, in districts (Chebyshev, by dx and dy), a district's works' tonnes look for a district with trips to ride: 2, how far a work looks for a ramp (DistrictPlan.ACCESS_RAMP_DISTRICTS). |

### TruckShapes.java ([map](map/TruckShapes.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `TruckShapes.LENGTH` | `TruckSchedule.TRUCK_PLOTS` | A truck's length in plots: the schedule's (TruckSchedule.TRUCK_PLOTS, 0.55, a 16.5 m articulated lorry), which frames the trucks by it. |
| 42 | `TruckShapes.MIN_PX` | `4` | The fewest pixels a truck is drawn long: 4 (the review, FR5). |
| 45 | `TruckShapes.WIDTH` | `0.22` | A truck's drawn width in plots: 0.22, a lorry's proportions on the road bed, narrower than a train's (TrainShapes.WIDTH 0.42). |
| 48 | `TruckShapes.CAB_SHARE` | `0.30` | The cab's share of the truck's length: 0.30, the tractor ahead of its trailer. |
| 51 | `TruckShapes.CAB_NARROW` | `0.80` | The cab's width over the trailer's: 0.80, so the silhouette steps in where the tractor starts and reads as a tractor and a trailer. |
| 59 | `TruckShapes.LANE` | `0.2` | How far right of the road's middle a truck drives, in plots: 0.2 (the review, FR5), so the trucks going in and those coming out pass side by side and do not draw over each other on the centre line ... |
| 62 | `TruckShapes.HOME` | `0xffa78bfa` | A tonne bought at home (works to works, loaded), neither an import nor an export: Palette.BUSINESS, the business area's violet, #a78bfa - the works' own colour, and no other mark on the map's freight. |

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
| 42 | `UtilitiesHandler.BASE_GRID_KW` | `10000` | The kilowatts the grid starts with, before a power plant is built (setWattsProduction()). |
| 69 | `UtilitiesHandler.WATER_PER_PERSON` | `.3` | Per-resident draw, in units of 10,000 gallons/month. |
| 112 | `UtilitiesHandler.FRESH_UNITS_PER_KM2` | `121_600` | Units of fresh water a month one square kilometre of owned lake or river yields: the world's renewable river runoff (about 42,700 km3 a year, Shiklomanov) over the world's river and stream area (77... |
| 711 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

### World.java ([map](map/World.md))

| line | constant | value | says |
|---:|---|---|---|
| 81 | `World.PLOT_M` | `30` | A plot's side, in metres: 30, the map's smallest square and the mockup's cell. |
| 84 | `World.KM2_PER_PLOT` | `PLOT_M * PLOT_M / 1e6` | A plot's area in square kilometres: 30 m squared, 0.0009. |
| 87 | `World.TILE` | `32` | Plots on a tile's side: 32, 0.96 km - the unit the map paints (spec-land 2.6). |
| 90 | `World.DISTRICT` | `256` | Plots on a district's side: 256, 7.68 km - the unit the map stores (spec-land 2.5). |
| 93 | `World.CELL` | `2048` | Plots on a world cell's side: 2,048, 61.44 km - the unit the world's totals and far view are counted in, so plot, tile, district and cell nest by powers of two (spec-land star 4). |
| 96 | `World.CELLS` | `368` | World cells on the world's side: 368, so the world is 511.2 million km2, within 0.2% of the Earth's 510.1 million. |
| 99 | `World.SIDE` | `(long) CELL * CELLS` | Plots on the world's side: 2,048 x 368, 753,664 (22,610 km). |
| 102 | `World.CELL_KM2` | `(CELL * PLOT_M / 1000) *(CELL * PLOT_M / 1000)` | A world cell's area in square kilometres: 61.44 km squared, 3,774.87. |
| 107 | `World.GRASS` | `0` | Open ground: dry and buildable. |
| 110 | `World.FOREST` | `1` | Forest: dry and buildable - a building on it clears it, and the forest law may keep it from building (LandManager, ZONES AND THE FOREST LAW, 0.7.107) - and standing timber (Resource.FOREST). |
| 113 | `World.FRESH` | `2` | Fresh water: a lake, or the founding river. |
| 116 | `World.SALT` | `3` | Salt water: the sea, and everything beyond the world's edge. |
| 119 | `World.SAND` | `4` | Beach: dry land within BEACH_BAND of the sea's level. |
| 122 | `World.ICE` | `5` | Ice (0.7.127, batch E7; spec-earth's E14, docs/studies/spec-globe.md 3.9): land under snow and ice - never dry, never built on, never a founding site - counted in a holding's whole area only. |
| 125 | `World.DESERT` | `6` | Desert (0.7.127, batch E7; spec-globe Q1): dry ground every rule treats as grass - only its colour and its name differ. |
| 128 | `World.SHALLOW` | `7` | Shallow sea (ET1, spec-earth2-terrain 6.1; Jerus 2026-10-12, D11): sea no deeper than 30 m - salt water every rule that asks "is this the sea?" treats as SALT (salt()), lighter on the map and the o... |
| 131 | `World.CLASSES` | `8` | How many classes the ground has: 8, GRASS to SHALLOW - the length of an array a class indexes (0.7.127; five until then, seven until ET1, and a generated world draws only the first five). |
| 134 | `World.DRY_CLASSES` | `1<<GRASS \| 1<<FOREST \| 1<<SAND \| 1<<DESERT` | The dry classes, a bit each: grass, forest, the beach and the desert. |
| 179 | `World.FLAT` | `0` | Flat ground (ET1, spec-earth2-terrain 3.1): under a 10% grade, the relief of every plot of a generated world (D19) and of the Earth's water, ice and anything else the slope plane says nothing of - ... |
| 182 | `World.FOOTHILL` | `1` | Foothill (ET1; D4): a grade of 10% to under 30% (5.7 to 16.7 degrees) - buildable, with preparation (a later batch prices it). |
| 185 | `World.STEEP` | `2` | Steep (ET1; D4): a grade of 30% to under 60% (16.7 to 31 degrees) - no building stands on it (buildable()); works that may stand anywhere do (workable()). |
| 188 | `World.VERY_STEEP` | `3` | Very steep (ET1; D4): a grade of 60% or more (31 degrees and over) - no building stands on it (buildable()). |
| 191 | `World.RELIEFS` | `4` | How many relief classes there are: 4, FLAT to VERY_STEEP - the length of an array a relief indexes. |
| 225 | `World.OCTAVE_FALLOFF` | `0.55` | Each octave's amplitude against the one above it: 0.55, a little rougher than classic fractal noise's half (the mockup's terrain, scaled to plots). |
| 228 | `World.ELEV_COARSE_HI` | `16` | Elevation's coarse octaves run from 2^16 plots (1,966 km): continents and oceans. |
| 231 | `World.ELEV_COARSE_LO` | `9` | ...down to 2^9 plots (15 km): coasts and bays. |
| 234 | `World.ELEV_FINE_HI` | `8` | Elevation's fine octaves run from 2^8 plots (7.7 km)... |
| 237 | `World.ELEV_FINE_LO` | `4` | ...down to 2^4 plots (480 m): headlands, coves and islets. |
| 240 | `World.ELEV_COARSE_WEIGHT` | `0.85` | The coarse octaves' share of the elevation: 0.85, so the coast is the continents' and the fine octaves only fray it. |
| 243 | `World.ELEV_FINE_WEIGHT` | `0.15` | The fine octaves' share: the rest, 0.15. |
| 246 | `World.LAKE_HI` | `10` | The lake field's octaves run from 2^10 plots (30.7 km)... |
| 249 | `World.LAKE_LO` | `5` | ...down to 2^5 plots (960 m). |
| 252 | `World.FOREST_HI` | `8` | The forest field's octaves run from 2^8 plots (7.7 km)... |
| 255 | `World.FOREST_LO` | `5` | ...down to 2^5 plots (960 m). |
| 258 | `World.SEA_SHARE` | `0.71` | The share of the world that is sea: 71%, the Earth's. |
| 270 | `World.SEA_SAMPLES` | `4` | Elevation samples a world cell the sea's level is found from: 4, one at a hashed point in each quarter of the cell (541,696 in all). |
| 273 | `World.LAKE_SHARE` | `0.037` | Lakes' share of land: 3.7%, of the Earth's non-glaciated land (Verpoorter 2014). |
| 283 | `World.LAKE_THETA` | `0.7092` | The lake field's level from which ground is a lake: 0.7092, the 96.3rd percentile of the field over land (LAKE_SHARE) - measured 0.7088 to 0.7095 over five seeds, a million points each, off the lat... |
| 286 | `World.FOREST_SHARE` | `0.31` | Forest's share of land: 31% (FAO 2020). |
| 289 | `World.FOREST_THETA` | `0.5676` | The forest field's level from which land is forest: 0.5676, the 69th percentile of the field over land (FOREST_SHARE), measured 0.5672 to 0.5679 as LAKE_THETA was (the design's 0.595 makes 24% of l... |
| 292 | `World.BEACH_BAND` | `0.0015` | How far above the sea's level, in the elevation field, land is beach: 0.0015. |
| 306 | `World.SHELF_SHARE` | `0.0886` | The continental shelf's share of the sea: 8.86% (est., the shelf's share of the ocean's area, Harris et al. |
| 309 | `World.SHELF_BREAK_M` | `140` | The depth of the shelf's edge, in metres: 140 (est., the mean shelf break - spec-oil 2.7 and 6, to confirm); depthAt() is this at shelfTheta(). |
| 327 | `World.SITE_CELL_LAND_MIN` | `1` | The least land, in quarters of a cell's SEA_SAMPLES, a cell needs to be searched for a site: 1, 25%. |
| 330 | `World.SITE_CELL_LAND_MAX` | `3` | The most: 3, 75% - a coast, not open sea or an interior. |
| 333 | `World.SITE_STEP` | `17` | The spiral's step: 17 plots (510 m) between rings, and the ring's points about that far apart (six a ring per ring). |
| 336 | `World.SITE_RINGS` | `70` | Rings in a cell's spiral: 70, out to 1,173 plots (35 km), past the cell's own half-width of 1,024. |
| 339 | `World.SITE_DRY_PLOTS` | `20` | Test 1: the site and eight points this far round it are dry ground, in plots: 20 (600 m), room for a new city's centre - since 0.7.67 rings of 120 m blocks round the site's own, on the default worl... |
| 342 | `World.SITE_LAND_SHARE` | `0.6` | Test 2: of SITE_LAND_SAMPLES points within 5 km, at least this share are land: 60%. |
| 345 | `World.SITE_LAND_SAMPLES` | `48` | ...over 48 points, at 1.5, 3 and 5 km in turn round the compass. |
| 348 | `World.SITE_LAND_RADII` | `{ 50, 100, 167 }` | ...at these radii, in plots: 50, 100 and 167 (1.5, 3 and 5 km). |
| 351 | `World.SITE_SEA_NOT_WITHIN` | `45` | Test 3: no sea within this many plots in 16 directions: 45 (1.35 km). |
| 354 | `World.SITE_SEA_WITHIN` | `70` | ...but sea within this many in at least one: 70 (2.1 km) - a coast with room for a town. |
| 365 | `World.SITE_IRON_KM` | `2` | Test 4 (spec-land star), the conversion's alone since 0.7.99: an iron field's centre within this many km of the old world's fields (legacyFieldsInCell()) - the mockup put deposits near the site "so... |
| 376 | `World.RIVER_SEA_DIRECTIONS` | `32` | Directions the sea is looked for in, round the site: 32. |
| 379 | `World.RIVER_SEA_REACH` | `100` | ...out to this many plots, from SITE_SEA_NOT_WITHIN: 100 (3 km). |
| 382 | `World.RIVER_SEA_STEP` | `5` | ...in steps of this many plots: 5. |
| 385 | `World.RIVER_LAKE_TRIES` | `32` | Tries at a lake whose shore is dry: 32. |
| 388 | `World.RIVER_LAKE_FAN` | `1.7` | The lake's bearing from the site: opposite the sea, give or take half of this many radians: 1.7. |
| 391 | `World.RIVER_LAKE_NEAR` | `64` | The lake's centre this many plots from the site, at the least: 64 (1.9 km)... |
| 394 | `World.RIVER_LAKE_SPAN` | `26` | ...plus up to this many: 26, so 64 to 90 plots (1.9 to 2.7 km). |
| 397 | `World.LAKE_R_MIN` | `11` | The lake's radius in plots, at the least: 11 (330 m)... |
| 400 | `World.LAKE_R_SPAN` | `6` | ...plus up to this many: 6, so 11 to 17 plots. |
| 403 | `World.LAKE_SHORE` | `1.6` | The lake's shore is tested this many radii out: 1.6... |
| 406 | `World.LAKE_SHORE_RISE` | `0.003` | ...for ground this far above the sea's level: 0.003, two beaches. |
| 409 | `World.RIVER_PASS_MIN` | `8` | The river passes the site at least this many plots to one side: 8 (240 m)... |
| 412 | `World.RIVER_PASS_SPAN` | `12` | ...plus up to this many: 12, so 8 to 20 plots. |
| 415 | `World.RIVER_PASS_REACHED` | `10` | Within this many plots of its passing point the river turns for the sea: 10. |
| 418 | `World.RIVER_STEP` | `3` | Plots the river moves a step: 3 (90 m). |
| 421 | `World.RIVER_WANDER` | `0.75` | Its heading wanders by up to half this many radians a step: 0.75... |
| 424 | `World.RIVER_STEER` | `0.25` | ...and turns this share of the way to its target a step: 0.25. |
| 427 | `World.RIVER_MAX_STEPS` | `500` | The most steps it takes: 500 (45 km). |
| 430 | `World.RIVER_MOUTH` | `0.002` | It ends where the elevation is this far under the sea's level: 0.002. |
| 433 | `World.RIVER_HALF_WIDTH` | `1.0` | Its half-width at the lake, in plots: 1 (a 60 m river)... |
| 436 | `World.RIVER_WIDENS` | `0.95` | ...growing by this many plots to the sea: 0.95, so 1.95 at the mouth (117 m). |
| 469 | `World.FIELD_TAIL` | `1.5` | How heavy the tail of a field's size is: 1.5, so P(sites >= k) = k^-1.5 - most small, a few huge. |
| 472 | `World.LEGACY_MAX_SITES` | `512` | The most sites one of the old world's fields holds: 512 (the cap of the tail's draw, sites()). |
| 475 | `World.MEAN_SITES` | `meanSites()` | The mean sites one of the old world's fields: the sum of k^-1.5 for k = 1 to LEGACY_MAX_SITES, 2.524 - exact for the capped tail, since P(sites >= k) = k^-1.5. |
| 478 | `World.RICHNESS_MIN` | `0.6` | A cell's richness, its total against its count's mean, at the least: 0.6... |
| 481 | `World.RICHNESS_SPAN` | `0.8` | ...plus up to this: 0.8, so 0.6 to 1.4, with a mean of one. |
| 484 | `World.SEA_REDRAWS` | `8` | Times a field centred in the sea is drawn again: 8, since ore lies under land. |
| 487 | `World.POISSON_NORMAL_ABOVE` | `40` | Above this mean a cell's count is drawn as a rounded normal rather than by inversion: 40. |
| 490 | `World.FOREST_M3_PER_KM2` | `13_700` | Standing timber a square kilometre of forest, in cubic metres: 13,700 (FAO 2020: 557 billion m3 on 4.06 billion hectares). |
| 538 | `World.FIELD_SCALE` | `10` | How many of the old world's fields one field stands for: 10 (Jerus, 2026-10-08: "a tenth as many fields, each ten times bigger", the world's totals as they were). |
| 569 | `World.EARTH_FIELD_SCALE` | `3` | On Earth a field stands for FIELD_SCALE x this of the old world's fields: three, so the Earth's fields are a third as many, each three times wider (EG1; Jerus, 2026-10-08, "huge and rare"; est.). |
| 572 | `World.MAX_SITES` | `FIELD_SCALE * EARTH_FIELD_SCALE * LEGACY_MAX_SITES` | The most sites one field holds, on Earth: FIELD_SCALE x EARTH_FIELD_SCALE of the old world's largest, 15,360 (a generated world's, 5,120, are its first places). |
| 575 | `World.CLUSTER_FIELDS` | `10` | The fields a cluster holds on the mean: 10 (★W1-2, est.: Jerus's "very big clusters" - ten fields a cluster, as a field is ten of the old). |
| 578 | `World.POOL_CLUSTERS` | `10` | The clusters a pool of land holds on the mean, at the least: 10 (★W1-2, est.): enough that a pool's clusters lie where the draws put them, not one a pool on a lattice - a pool a quarter land still ... |
| 581 | `World.FIELD_INDEX_FROM` | `1<<16` | The first number a field is listed under in its cell: 65,536, past any old world's field's (a cell held at most 868 of them on the default world, iron's). |
| 584 | `World.POOLS_KEPT` | `256` | Pools kept, by resource and place: 256 (an iron pool is about 300 fields, some 15 KB). |
| 587 | `World.POOL_SALT` | `0x5EB0C1A5L` | The stream a pool is drawn from. |
| 594 | `World.WORLDS_KEPT` | `4` | How many worlds World.of() keeps: 4, the city's and a few the founding screen looked at (about 0.3 MB each). |
| 596 | `World.KEPT` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeEldestEntry(Ma...` |  |
| 630 | `World.KEPT_EARTH` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeEldestEntry(Ma...` | Earth worlds kept as World.of(key) is asked for them: WORLDS_KEPT, apart from the generated, so those are kept as they were. |
| 758 | `World.F_ELEV` | `0x1111, F_ELEV_FINE = F_ELEV ^ 0x55, F_LAKE = 0x2222, F_FOREST = 0x3333` | The fields' keys: what makes one field's octaves another's. |
| 761 | `World.AMP` | `new double [ ELEV_COARSE_HI + 1 ]` | OCTAVE_FALLOFF to the power n, by repeated multiplication - the order fbm() has always summed in. |
| 777 | `World.NORM_COARSE` | `norm(ELEV_COARSE_HI, ELEV_COARSE_LO), NORM_FINE = norm(ELEV_FINE_HI, ELEV_FINE_LO), NOR...` |  |
| 1268 | `World.SMOOTH` | `new double [ ELEV_COARSE_HI + 1 ][]` | The smoothstep at a plot's centre within an octave's lattice cell, for every wavelength 2^k: SMOOTH[k][f] for f = 0 .. |
| 1287 | `World.SCRATCH` | `ThreadLocal.withInitial(TileScratch : : new)` |  |
| 1429 | `World.LineScratch.CORNERS` | `64` | The corners it keeps: 64 slots, picked by the top six bits of the corner's hash. |
| 1446 | `World.LINE_SCRATCH` | `ThreadLocal.withInitial(LineScratch : : new)` | Each thread's LineScratch, made on its first line. |
| 1755 | `World.POOL_SIDE` | `new int [ Resource.values().length ]` | Each resource's pool side in cells, in Resource's order (poolCells()). |
| 1808 | `World.NO_POOL` | `new Pool(new long [ 0 ], new long [ 0 ], new int [ 0 ], new double [ 0 ], 0, 0)` | The pool of a place whose land cells hold no old draw of the resource: no field, no tonnes. |

### WorldData.java ([map](map/WorldData.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `WorldData.DATA_FILE` | `"data/world-1.bin"` | Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored). |
| 73 | `WorldData.PROPERTY` | `"cbs.world"` | The system property that names the file instead (-Dcbs.world=<path>), as the harness's run on the full file takes it. |
| 76 | `WorldData.FIXTURE` | `"world/world-1-fixture.bin"` | The fixture, a resource beside the classes: the full file cut by tools/world/fixture.py to twelve nations and the Rotterdam window (WorldDataCheck's, with world-1-fixture.expect beside it). |
| 79 | `WorldData.MAGIC` | `"WORLD-1\n"` | The bytes the first eight of the file are: the earth container's layout, under its own name. |
| 82 | `WorldData.FORMAT` | `1` | The layout's version this reads (FORMAT.txt). |
| 85 | `WorldData.KIND_TILES` | `EarthData.KIND_TILES` | A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner (EarthData's). |
| 87 | `WorldData.KIND_WHOLE` | `EarthData.KIND_WHOLE` | A layer held whole, one blob. |
| 89 | `WorldData.TILES` | `EarthData.TILES` | The tiles in a KIND_TILES layer. |
| 91 | `WorldData.META` | `"meta", NATIONS = "nations", MONEYS = "moneys", PATHS = "paths", GOODS = "goods", TRADE...` |  |
| 95 | `WorldData.LAYERS` | `List.of(META, NATIONS, MONEYS, PATHS, GOODS, TRADE, FREIGHT, OWNER, LINES, PRICES)` | The layers, in the file's order (FORMAT.txt, LAYERS). |
| 98 | `WorldData.OWNER_CELLS` | `240` | The owner layer's cells a degree: 240, 15", the earth file's landmass layer's grid. |
| 101 | `WorldData.OWNER_TILE_BYTES` | `OWNER_CELLS * OWNER_CELLS` | The bytes of an owner tile with two owners or more: a byte a cell, OWNER_CELLS squared. |
| 104 | `WorldData.MAX_OWNERS` | `255` | The most owners the layer's byte holds: nations and areas together are numbered up to this (FORMAT.txt, NUMBERS). |
| 107 | `WorldData.CACHE_TILES` | `64` | The inflated owner tiles kept, 64 of 57,600 bytes: a screen or a scan reads the same few tiles over and over. |
| 110 | `WorldData.FIRST_YEAR` | `2000` | The year month 0 is in: months are numbered from January of it. |
| 113 | `WorldData.LINE_UNITS` | `100_000` | A lines layer's coordinate is stored in these units a degree. |
| 116 | `WorldData.MARK_NONE` | `0, MARK_ESTIMATE = 1, MARK_PROJECTED = 2, MARK_PUBLISHED = 3` | The marks (see the class comment). |
| 119 | `WorldData.KIND_NATION` | `"nation", KIND_AREA = "area"` | A nations row's kind for a nation, and for an area. |
| 122 | `WorldData.LAND_PER_SEA` | `"LAND_PER_SEA"` | The meta dial that weighs a land km against a sea km in the freight cost (FORMAT.txt, freight). |
| 125 | `WorldData.HEAD_BYTES` | `16 + 64 *(2 + 255 + 33)` | The first eight bytes' length, the format version, the layer count, and the room a layer's header row takes at most: the head read to find the layers. |

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
| 253 | `WorldEconomy.TREND_INFLATION` | `.0` | The world's long-run trend, against which the wandering rate is a cycle. |
| 256 | `WorldEconomy.TREND_PULL` | `.006` | How hard the level is pulled back to trend. |
| 360 | `WorldEconomy.LEVEL_RING` | `13` |  |
| 425 | `WorldEconomy.LEGACY_MEAN_INFLATION` | `.0333` | The mean every city founded before 2026-09-13 grew up in. |

### WorldMarket.java ([map](map/WorldMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `WorldMarket.SOLVE_TOL` | `1e-4` | How close every maker's sales must come to its supply, and the price index to its path, for a January to stop: 0.01% (spec-nations 4.3). |
| 67 | `WorldMarket.SOLVE_MAX_PASSES` | `2_000` | The most sweeps a good's January takes before it stops and says so in the log. |
| 70 | `WorldMarket.SUPPLY_ELASTICITY` | `.4` | Supply's elasticity to a maker's own mill price (est.: the nations prototype's ES). |
| 73 | `WorldMarket.DEMAND_ELASTICITY` | `.5` | Demand's elasticity to a buyer's price index (est.: the nations prototype's ED). |
| 76 | `WorldMarket.HOME_KM` | `300` | A nation's haul to itself, km by land, no handling: the city's leg to its host (est.: the prototype's). |
| 79 | `WorldMarket.ENTRY_NODES` | `6` | How many other nations' nodes, nearest by great circle, a city can enter the world through, besides its host. |
| 82 | `WorldMarket.MAX_LAND_KM` | `2_500` | The farthest, by great circle, a node is reached by land from the city, km; a city with a berth reaches a coastal node by sea at any distance (est.). |
| 85 | `WorldMarket.REFERENCE_LAT` | `44.65, REFERENCE_LON = - 63.57` | The reference city's place, Halifax: where the game's literals are the world's prices (D12). |
| 88 | `WorldMarket.REFERENCE_YEAR` | `2025` | The year the literals are set in (the Historical path's last year of data, seed-free). |
| 91 | `WorldMarket.SOLVES_KEPT` | `64` | The solved years kept in the process-wide cache, so harness cities on one world share a January. |
| 94 | `WorldMarket.BASE_MS` | `2_000` | The Base on the full file, read and calibrated, takes at most this many milliseconds. |
| 97 | `WorldMarket.SOLVE_COLD_MS` | `600` | The founding solve (year 2000, every good, from p-hat = 1) takes at most this many milliseconds. |
| 100 | `WorldMarket.SOLVE_WARM_MS` | `60` | A January (every good, from last year's answer) takes at most this many milliseconds. |
| 103 | `WorldMarket.BAND_MONTH_US` | `200` | A month's bands for every good take at most this many microseconds. |
| 106 | `WorldMarket.ACCEL_MEMORY` | `5` | The steps Anderson's acceleration remembers: the solve is a fixed-point iteration that closes about a tenth of its distance a pass, and the last few steps tell it where it is going. |
| 109 | `WorldMarket.ACCEL_BACK` | `10` | The solve forgets what the acceleration remembers when a pass's worst error is this many times the pass before's (the model has stopped being linear there). |
| 112 | `WorldMarket.SHARE_FLOOR` | `1e-3` | The least share of a buyer's composite a binding supplier is taken to have, so that a supplier that sells the buyer next to nothing does not make its sink's slope infinite (est.). |
| 115 | `WorldMarket.MAX_STEP` | `1` | The most a maker's log price moves in one pass, whatever its imbalance (a factor of e: the first passes of a cold solve start far away). |
| 118 | `WorldMarket.RESOLVE_TOL` | `1e-7` | The tolerance of the re-solve the check judges the month's step against (the nations' solve with the city's tonnes delivered). |
| 121 | `WorldMarket.RESOLVE_FLOOR` | `1e-6` | The least share of a buyer's demand a delivered city can leave to the nations in the re-solve (a floor, so spending stays above zero). |
| 124 | `WorldMarket.RHO_MIN` | `1e-5, RHO_MAX = 1e5` | The range the freight scale is looked for in, a multiple of the file's reference freight (the scan then bisects between two neighbours). |
| 127 | `WorldMarket.RHO_SCAN` | `80, RHO_BISECT = 200` | The scan's steps across RHO_MIN to RHO_MAX, and the bisection's cap. |
| 130 | `WorldMarket.PHI_FALLBACK` | `1` | The freight scale a world with no good that calibrates stands on (est.: the file's reference freight as it is). |
| 295 | `WorldMarket.Base.LOADED` | `new HashMap<>()` |  |
| 296 | `WorldMarket.Base.OF` | `new IdentityHashMap<>()` |  |
| 695 | `WorldMarket.STAND_IN` | `0x57A4D1` | Draws a stand-in's nation (Nations.scramble's "what"). |
| 963 | `WorldMarket.SOLVES` | `new LinkedHashMap<>(16,.75f, true) { @ Override protected boolean removeEldestEntry(Map...` |  |
| 1390 | `WorldMarket.SCARCITY_MULTIPLE` | `4` | Where the ceiling stops when the city buys more than every seller makes, as a multiple of the cheapest landed cost: past every cap the fill has no level (infinity), and a market's strike needs a fi... |

### YearBook.java ([map](map/YearBook.md))

| line | constant | value | says |
|---:|---|---|---|
| 69 | `YearBook.MONTHS_A_YEAR` | `12` |  |
| 70 | `YearBook.MONTHS_A_DECADE` | `120` |  |
| 101 | `YearBook.MONEY` | `"{the city's money}"` | Where a note names the city's money. |
| 284 | `YearBook.RULES` | `rules()` |  |
| 285 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 565 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1153 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1156 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1159 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |
| 1171 | `YearBook.FINANCIAL_EQUITY` | `0` | The bank's equity under this, in thousands, is a financial crisis: the bank has failed. |
| 1174 | `YearBook.RECESSION_GROWTH` | `0` | Real output over a rolling year against the year before, as a fraction, under this is a recession: under zero, a fall. |
| 1177 | `YearBook.CURRENCY_MOVE` | `2` | The exchange rate past this multiple of itself a year before is a currency crisis: the currency halved. |
| 1180 | `YearBook.INFLATION_EPISODE` | `.25` | Prices rising faster than this a year is an inflation. |
| 1183 | `YearBook.DEFLATION_EPISODE` | `-.10` | Prices falling faster than this a year (a negative rate) is a deflation. |
| 1186 | `YearBook.EPIDEMIC_OUTBREAK` | `Health.OUTBREAK_FLOOR` | An epidemic is named while an outbreak runs - more of the workforce off sick than its care explains - not while a city short of care is as sick as it always is (A8). |
| 1189 | `YearBook.TREASURY_CASH` | `0` | The treasury's cash under this, in thousands, is a treasury crisis: it is overdrawn. |
| 1192 | `YearBook.SLUMP_UNEMPLOYMENT` | `.20` | More of the labour force out of work than this is a slump. |
| 1369 | `YearBook.CHRONIC_MONTHS` | `120` | An episode still running after this many months is chronic: City History lists it after the others that are running, and leads with it only when it runs alone (0.7.37). |

### YieldCurve.java ([map](map/YieldCurve.md))

| line | constant | value | says |
|---:|---|---|---|
| 39 | `YieldCurve.DRIFT_MONTHS` | `6` | The time constant of the dial's expected return to the rule, in months (D15, Jerus's to move): the gap between where the dial stands and where the rule would put it closes by a factor of e in this ... |
| 42 | `YieldCurve.HORIZON` | `600` | The months the curve is built to: fifty years, the longest term loan; the curve holds its last zero rate past it. |

### Zoning.java ([map](map/Zoning.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `Zoning.MAX_PRICE_SHARE` | `1` | The most of the going business price a zone's ground is sold at: the going price itself (Z3: "0-100%"). |
| 59 | `Zoning.MAX_SIZE_SHARE` | `1` | The most of the city's dry ground one zone may be: all of it. |

### ZoningView.java ([map](map/ZoningView.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `ZoningView.SHORT_SHARE` | `1e-6` | A zone holds less than it wants by more than this share of its want: SHORT (Z10's "holds 12.4 of 40 km²"), not a rounding. |

## sectors (174 constants)

### Agriculture.java ([map](map/Agriculture.md))

| line | constant | value | says |
|---:|---|---|---|
| 128 | `Agriculture.BAKED_KG_PER_TONNE` | `525` | Kilograms of bread and bakery goods a tonne of crops becomes. |
| 131 | `Agriculture.BAKED_KG_A_HEAD` | `5.0` | What one person eats of the city's own baking a month: 3.5kg of bread, 1.5kg of the rest. |
| 322 | `Agriculture.FIRST_FARM_UTILISATION` | `.5` | Half a farm's nameplate, a month, before the first one is sunk. |

### Automotive.java ([map](map/Automotive.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `Automotive.MAX_SHARE_OF_LOCAL_SUPPLY` | `.25` | The most of the city's WHOLE fabrication output one new plant may want. |
| 122 | `Automotive.LUBRICANT_LITRES_A_VEHICLE` | `8` | Lubricants a vehicle built takes (0.7.83, batch O6; docs/studies/spec-oil.md 2.5, est., to confirm): eight litres - its first fill of engine, gearbox and axle oils and the line's own - on the car p... |
| 131 | `Automotive.LUBRICANT_LITRES_A_TRUCK` | `40` | Lubricants a heavy truck built takes (FR2, 2026-10-18; docs/queue/FR2.md section 2, est.): forty litres of engine, gearbox and axle oils for its fifteen tonnes of tractor and trailer, on the Heavy ... |

### CementConcrete.java ([map](map/CementConcrete.md))

| line | constant | value | says |
|---:|---|---|---|
| 47 | `CementConcrete.CEMENT_PLANT` | `118` | The Cement Plant's permanent id in buildings.json (0.7.131): 25,000 t of cement a month. |
| 50 | `CementConcrete.LARGE_CEMENT_PLANT` | `119` | The Large Cement Plant's (0.7.131): 125,000 t of cement a month - Edison's long kilns, 1902. |
| 53 | `CementConcrete.LIME_KILN` | `120` | The Lime Kiln's (0.7.131): 18,000 t of quicklime a month. |

### CoalMining.java ([map](map/CoalMining.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `CoalMining.SURFACE_MINE` | `111` | The Surface Coal Mine's permanent id in buildings.json (0.7.126): 100,000 t of thermal coal a month on one thermal site. |
| 91 | `CoalMining.LARGE_SURFACE_MINE` | `112` | The Large Surface Mine's (0.7.126): a dragline pit, 800,000 t of thermal coal a month on four sites of one thermal field. |
| 94 | `CoalMining.UNDERGROUND_MINE` | `113` | The Underground Coal Mine's (0.7.126): a longwall, 250,000 t of raw coal a month on one coking site. |
| 97 | `CoalMining.PREPARATION_PLANT` | `114` | The Coal Preparation Plant's (0.7.126): washes 500,000 t of raw coal a month. |
| 100 | `CoalMining.COKING_YIELD` | `.55` | The coking coal a tonne of raw coal washes into: .55, the study's 3.3 (275,000 t of 500,000). |
| 103 | `CoalMining.THERMAL_YIELD` | `.20` | ...the thermal coal it washes into, the middlings: .20, the study's 3.3 (100,000 t of 500,000). |
| 106 | `CoalMining.SPOIL_YIELD` | `.25` | ...and the spoil, the rest, to the tip: .25, the study's 3.3 (125,000 t of 500,000). |

### Construction.java ([map](map/Construction.md))

| line | constant | value | says |
|---:|---|---|---|
| 59 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The core crew: the smallest share of its posts construction keeps on when it has no work, and lays the rest off. |

### CopperMining.java ([map](map/CopperMining.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `CopperMining.MINE_AND_MILL` | `121` | The Copper Mine & Mill's permanent id in buildings.json (0.7.133): 2,200 t of concentrate a month on one sulphide site. |
| 73 | `CopperMining.LARGE_MINE` | `122` | The Large Copper Mine's (0.7.133): 26,400 t of concentrate a month on eight sites of one sulphide field. |
| 76 | `CopperMining.LEACH_PLANT` | `123` | The Leach & SX-EW Plant's (0.7.133): 1,000 t of cathode a month on one oxide site, of 3,500 t of acid. |
| 79 | `CopperMining.CONC_DIG` | `.341` | Tonnes of a sulphide field's copper a tonne of concentrate takes: .341 (spec 2.4; the study's 4.3 - 150,000 t of ore at 0.5% into 2,200 t of concentrate). |
| 82 | `CopperMining.LEACH_DIG` | `1.4` | Tonnes of an oxide field's copper a tonne of leached cathode takes: 1.4 (spec 2.4; the study's 4.3 - 280,000 t of ore at 0.5% into 1,000 t). |

### CopperRefining.java ([map](map/CopperRefining.md))

| line | constant | value | says |
|---:|---|---|---|
| 77 | `CopperRefining.SMELTER` | `124` | The Copper Smelter's permanent id in buildings.json (0.7.133): 9,750 t of anode and 29,700 t of acid a month from 33,000 t of concentrate. |
| 80 | `CopperRefining.ELECTROLYTIC_REFINERY` | `125` | The Electrolytic Refinery's (0.7.133): 9,700 t of cathode a month from 9,750 t of anode. |
| 83 | `CopperRefining.SCRAP_REFINERY` | `126` | The Scrap Copper Refinery's (0.7.133): 4,000 t of cathode a month from 4,400 t of the city's old copper and scrap. |
| 86 | `CopperRefining.ROD_MILL` | `127` | The Rod Mill's (0.7.133): 8,000 t of wire rod a month from 8,016 t of cathode. |

### FoodProcessing.java ([map](map/FoodProcessing.md))

| line | constant | value | says |
|---:|---|---|---|
| 99 | `FoodProcessing.FILLED` | `.85` | How full a new plant has to be before anybody finances one. |

### Forestry.java ([map](map/Forestry.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `Forestry.LOGGING_CAMP` | `129` | The Logging Camp's permanent id in buildings.json (0.7.138): 1,000 m3 of sawlogs and 1,000 of pulpwood a month. |
| 101 | `Forestry.HARVESTING_OPERATION` | `130` | The Harvesting Operation's (0.7.138): 6,250 and 6,250, a harvester and forwarder crew. |
| 104 | `Forestry.TREE_PLANTATION` | `131` | The Tree Plantation's (0.7.138): 1,000 ha planted on Forestry's zone. |
| 107 | `Forestry.WOODLOT` | `132` | The Woodlot's (0.7.138; Jerus's A12): 100 ha, a tenth of a plantation. |
| 110 | `Forestry.FOREST_CREW_MONTHS` | `120` | E (Jerus, 2026-10-08, Z13: "yes 120"): a crew only while the cuttable forest lasts this many months of every crew's cut with it - a crew's working life, not its payback (spec-zoning 3.4). |
| 113 | `Forestry.PLANTED_M3_A_HA_A_YEAR` | `20` | What a planted stand grows, cubic metres a hectare a year (the study's 5.1 and 5.7 [W4]: plantations 15-40, the managed Nordic 4-6): a plantation's 1,000 ha 1,667 m3 a month, a woodlot's 100 ha 167. |
| 116 | `Forestry.SAWLOG_SHARE` | `.5` | The share of a cubic metre cut that is a sawlog, the rest pulpwood: the harvest about 50:50 sawlog to pulpwood (the study's 5.1 [W14][W15]) - a crew's rows and the salvage cut alike. |
| 119 | `Forestry.SALVAGE_LINE` | `"Salvage cut"` | The line the builders' bill for a salvage cut is booked under on Forestry's inputs (Sector.billForService()). |
| 122 | `Forestry.SQ_FT_PER_HA` | `LandManager.SQ_FT_PER_KM2 / 100` | Square feet in a hectare: a hundredth of a square kilometre. |

### Freight.java ([map](map/Freight.md))

| line | constant | value | says |
|---:|---|---|---|
| 156 | `Freight.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 159 | `Freight.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 180 | `Freight.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 199 | `Freight.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 202 | `Freight.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 233 | `Freight.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 236 | `Freight.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 239 | `Freight.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 275 | `Freight.WORLD_FUEL_PER_TONNE` | `.03` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |
| 287 | `Freight.FUEL_LITRES_PER_TONNE` | `18` | Litres of fuel a tonne hauled burns: eighteen. |
| 292 | `Freight.PORT_YARD_SHARE` | `.45` | The most of the BULK tonnes that crossed a berth the port yard takes to rail (FR6, D7): Montreal's rail share, on the port's own railway on the quay (spec R70). |
| 298 | `Freight.CITY_LITRES_A_TONNE` | `Haulage.DIESEL_LITRES_A_TONNE / 3` | Litres of diesel the railway burns on a city tonne (FR6): a third of a truck's city leg (Haulage.DIESEL_LITRES_A_TONNE), as rail costs about a third of a truck a tonne-kilometre (spec R50, R51); FU... |
| 1036 | `Freight.BULK_WORKS_MIN_TONNES` | `TONNES_PER_SET * MIN_LINE_UTILISATION` | The bulk a building has to make and use between them, tonnes a month at nameplate, to be a bulk works (FR7): MIN_LINE_UTILISATION of a wagon set's month, as a line is not laid below that share of i... |

### Haulage.java ([map](map/Haulage.md))

| line | constant | value | says |
|---:|---|---|---|
| 77 | `Haulage.TONNES_A_LOAD` | `25` | Tonnes in a full load: an EU 40 t articulated truck's payload (spec R9). |
| 80 | `Haulage.TONNES_A_TRUCK_MONTH` | `3000` | Tonnes a truck carries in a month on one shift: 6-7 loads a ten-hour shift over 22 days, rounded down to 3,000 (spec 3.2; FR2.md 2 has the sources). |
| 83 | `Haulage.DIESEL_LITRES_PER_100_KM` | `33` | What a laden tractor-trailer burns, litres of diesel a hundred kilometres (spec R3, R23): a van's is twelve (Sector.DIESEL_LITRES_PER_100_KM). |
| 86 | `Haulage.DIESEL_LITRES_A_TONNE` | `Sector.KM_A_LOAD / TONNES_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | Litres of diesel a tonne carried burns: a city leg's fifty kilometres a load (Sector.KM_A_LOAD) over a load's 25 t at 33 L a 100 km, .66. |
| 89 | `Haulage.STREET_WEAR` | `1.10` | How much faster a truck past the depots' room wears: a tenth, D6's +10% upkeep built as wear because a truck parked on the street is a truck nobody maintains. |
| 98 | `Haulage.DEPOT_ORDER_MARGIN` | `.5` | How far past the depots' room the fleet goes before another depot is ordered: half a depot's room, so a new yard opens half full. |
| 105 | `Haulage.FLEET_RECOVERY_MONTHS` | `24` | Months over which the trucks' cost past their wear is recovered (FR2's ★10, the review): the time a city takes to grow into a new works, so a month's purchases are not one month's bill. |
| 108 | `Haulage.MIN_TRUCKS_A_MONTH` | `1` | The fewest trucks a month's order is capped at (bid()): one - a haulier can always buy a truck, however small its fleet. |
| 111 | `Haulage.DRIVERS_A_TRUCK` | `1` | Drivers a truck the depots keep needs: one, on one shift (spec 3.2; US fleets run 0.93 a truck, the spec's R2). |
| 114 | `Haulage.BILLED_AS` | `"Haulage"` | What a bill is called on the shipper's input line. |
| 1027 | `Haulage.MOVE_FIELDS` | `{ "tonnes", "rail", "sea", "lorry", "truck", "siding", "yard" }` | The seven figures of a move, in the order a save keeps them. |
| 1030 | `Haulage.MOVE_PREFIX` | `"haul.move."` | The save keys' prefix (Freight's extras); the figure names avoid the scaler's intensive words (ScaleCheck), so a scaled copy's tonnes are scaled. |
| 1033 | `Haulage.SHARE_PREFIX` | `"haul.share."` | The save keys' prefix for a sector's dealt share (FR8, deal()), the sector's key after it; "share" in the name keeps the scaler's hands off a share (ScaleCheck). |
| 1036 | `Haulage.STANDING_PREFIX` | `"haul.standing."` | ...and for the tonnes it was dealt, its next standing demand: tonnes, so scaled with the city, and so no intensive word in the name (ScaleCheck). |

### HeavyIndustry.java ([map](map/HeavyIndustry.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `HeavyIndustry.ARC_FURNACE` | `11` | The Electric Arc Furnace's permanent id in buildings.json, the Steel Foundry's until 0.7.120 (batch I3; spec-materials 4): what the harnesses and the test player find it by. |
| 88 | `HeavyIndustry.MINI_MILL` | `12` | The Steel Mini-Mill's permanent id in buildings.json (0.7.120). |
| 91 | `HeavyIndustry.SCRAP_YARD` | `101` | The Scrap Yard's permanent id in buildings.json (0.7.123, batch I4): the city's old steel into scrap. |
| 94 | `HeavyIndustry.PELLETISING_PLANT` | `102` | The Pelletising Plant's permanent id (0.7.123): ore into pellets. |
| 97 | `HeavyIndustry.COKE_OVENS` | `103` | The Coke Ovens' permanent id (0.7.123): coking coal into coke, and power. |
| 100 | `HeavyIndustry.MINI_BLAST_FURNACE` | `104` | The Mini Blast Furnace's permanent id (0.7.123): ore, coke and limestone into pig iron. |
| 103 | `HeavyIndustry.BLAST_FURNACE` | `105` | The Blast Furnace's permanent id (0.7.123): the big furnace, its sinter plant and its top-gas turbine. |
| 106 | `HeavyIndustry.OXYGEN_STEEL_SHOP` | `106` | The Oxygen Steel Shop's permanent id (0.7.123): pig iron, scrap and lime into steel. |
| 109 | `HeavyIndustry.LARGE_MINI_MILL` | `107` | The Large Mini-Mill's permanent id (0.7.123): the big arc-furnace plant. |

### LuxuryRetail.java ([map](map/LuxuryRetail.md))

| line | constant | value | says |
|---:|---|---|---|
| 92 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop charges over what the piece cost it when nobody would buy at its price: the mark-up at a position of nothing, and the lowest the margin can strike (0.7.19: the position is the buyers AT... |
| 106 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop charges as the buyers at its price outnumber its counters without limit: the position's limit of one. |
| 115 | `LuxuryRetail.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a shop's charged margin moves toward the fixed point its buyers strike in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 250 | `LuxuryRetail.MARGIN_STEPS` | `60` | Bisection steps for the margin's fixed point: 2.75 / 2^60 is far below a cent's grain on any landed cost. |

### Manufacturing.java ([map](map/Manufacturing.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `Manufacturing.LUBRICANT_LITRES_A_TONNE_OF_MACHINERY` | `10` | Lubricants a tonne of machinery made takes: ten litres (spec-oil 2.5, est.). |
| 91 | `Manufacturing.LUBRICANT_LITRES_A_TONNE_FABRICATED` | `2` | ...and a tonne of fabricated steel: two litres (spec-oil 2.5, est.). |
| 108 | `Manufacturing.WIRE_CABLE_WORKS` | `128` | The Wire & Cable Works' permanent id in buildings.json (0.7.133): 2,000 t of wire and cable a month from 2,000 t of rod. |

### Materials.java ([map](map/Materials.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `Materials.FIRST_PLANT_UTILISATION` | `.5` | Half a plant's nameplate, a month, before the first one is sunk. |

### Mining.java ([map](map/Mining.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `Mining.IRON_MINE` | `14` | The Iron Mine's permanent id in buildings.json (0.7.120, batch I3; spec-materials 4): what the harnesses, the test player and the build advice find it by - a name is a label, and the chains rename ... |
| 48 | `Mining.LARGE_OPEN_PIT` | `108` | The Large Open-Pit Mine's permanent id (0.7.124, batch I5): 500,000 t of hematite ore a month on twenty sites of one field. |
| 51 | `Mining.MAGNETITE_MINE` | `109` | The Magnetite Mine & Concentrator's (0.7.124): 20,000 t of concentrate a month from 60,000 t of rock, on one magnetite site. |
| 54 | `Mining.LARGE_MAGNETITE` | `110` | The Large Magnetite Complex's (0.7.124): 400,000 t of concentrate a month from 1.2 Mt of rock, on twenty sites of one magnetite field. |

### Oil.java ([map](map/Oil.md))

| line | constant | value | says |
|---:|---|---|---|
| 144 | `Oil.LAND_KEEPS_A_YEAR` | `.9` | A land well keeps nine tenths of its lift a year: it loses 10% a year (the research's 3.2, est.; [W6]). |
| 147 | `Oil.PLATFORM_PLATEAU_MONTHS` | `36` | A platform well holds its lift for a plateau of three years (the research's 3.1: two or three [W6][W16]; spec-oil 2.6)... |
| 150 | `Oil.PLATFORM_KEEPS_A_YEAR` | `.915` | ...and then keeps 91.5% of it a year: it loses 8.5% a year, the IEA's shallow-offshore rate [W6][W16]. |
| 153 | `Oil.NAMEPLATE_BARRELS_A_DAY` | `100` | A well's nameplate is a hundred barrels a day (BuildingManager, FUEL: 415 t a month at 7.33 barrels a tonne). |
| 156 | `Oil.WORN_OUT_BARRELS_A_DAY` | `10` | A well lifting under ten barrels a day is worn out and retired (the research's 3.2): 41.5 t a month of a 415 t well's. |
| 159 | `Oil.WORN_OUT_SHARE` | `WORN_OUT_BARRELS_A_DAY / NAMEPLATE_BARRELS_A_DAY` | ...the share of its nameplate a well is retired under: WORN_OUT_BARRELS_A_DAY over NAMEPLATE_BARRELS_A_DAY, a tenth. |
| 162 | `Oil.VINTAGE_KEY` | `"vintages."` | The extras' key a vintage is saved under: vintages.<month opened>.<kind>, its count the value. |
| 186 | `Oil.LAND_LIFE` | `firstWornOut(WellKind.LAND), PLATFORM_LIFE = firstWornOut(WellKind.PLATFORM)` | Each kind's life in months, worked out once from its profile (firstWornOut()). |
| 696 | `Oil.PLATFORM_MAX_DEPTH_M` | `150` | The deepest sea a platform's jacket stands in, in metres: 150, the fixed platform's limit (the research's 3.2; spec-oil 2.7). |
| 699 | `Oil.PIPE_LIFE_MONTHS` | `480` | A pipe's working life, in months: 480, 40 years (the research's 4.5, est.) - what the pipeline rule counts the field's months to at most. |
| 702 | `Oil.PIPE_PAYBACK` | `1.25` | The pipeline rule's margin: the freight a pipe saves over the field's life must repay its cost 1.25 times (the research's 4.5; spec-oil 2.11). |
| 705 | `Oil.SHUTTLE_TANKERS` | `"Shuttle tankers"` | The shuttle tankers' name on the Oil sector's input line. |
| 708 | `Oil.PLATFORM_KEY` | `"platforms."` | The extras' key a platform is saved under: platforms.<place>.<cell>.<index>.<month opened>, the wells in its slots the value. |
| 711 | `Oil.PIPELINE_KEY` | `"pipelines."` | ...and a pipeline: pipelines.<place>.<cell>.<index>.<month ordered>, its kilometres the value. |

### PortDivision.java ([map](map/PortDivision.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `PortDivision.DESIGN_USE` | `Ports.WORTH_A_BERTH` | The share of a berth's month the dues and the rule's floor are worked at (FR3.md call 4): Ports.WORTH_A_BERTH, .60, the test player's own threshold for ordering a berth. |
| 66 | `PortDivision.PORT_LIFE_MONTHS` | `360` | Months over which a berth's cost is recovered by the dues: 360, thirty years, the term a landlord port leases a terminal for (est., FR3.md 2 [R91][R92]). |
| 69 | `PortDivision.BILLED_AS` | `"Port handling"` | What the handling is called on the shipper's input line (FR3.md 1). |
| 72 | `PortDivision.REVENUE_NAME` | `"Port handling billed"` | ...and on Freight's revenue (Freight.nameOtherRevenue()), beside "Haulage billed". |
| 75 | `PortDivision.DUES_NAME` | `"Port dues"` | ...and the dues, on Freight's input line and the treasury's revenue. |
| 77 | `PortDivision.KINDS` | `Ports.Cargo.values().length` |  |

### Quarrying.java ([map](map/Quarrying.md))

| line | constant | value | says |
|---:|---|---|---|
| 81 | `Quarrying.QUARRY` | `115` | The Quarry's permanent id in buildings.json (0.7.131): 40,000 t a month on one stone site of either type. |
| 84 | `Quarrying.LARGE_QUARRY` | `116` | The Large Quarry's (0.7.131): 250,000 t a month on three sites of one stone field. |
| 87 | `Quarrying.DIMENSION_STONE_QUARRY` | `117` | The Dimension Stone Quarry's (0.7.131): 1,000 t of blocks a month on one hard-rock site. |

### RealEstate.java ([map](map/RealEstate.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `RealEstate.TARGET_RENT_BURDEN` | `.30` | What share of a working household's income rent should take. |
| 99 | `RealEstate.REFERENCE_EARNERS` | `2` | The household the yardstick is struck against: a couple both working... |
| 102 | `RealEstate.REFERENCE_HOME_CAPACITY` | `4` | ...in a home for four. |
| 110 | `RealEstate.LANDLORD_YIELD` | `.058` | What a landlord wants back each year for what the building cost. |
| 133 | `RealEstate.MAINTENANCE_PER_YEAR` | `.01` | Repairs: one percent a year of what a building cost to put up, placed as a real order with the builders in the building's own inputs. |
| 136 | `RealEstate.SCARCITY_ELASTICITY` | `1.0` | How hard rent answers a shortage of front doors. |
| 139 | `RealEstate.LEASE_MONTHS` | `12` | The lease, in months, which is also the lag. |
| 142 | `RealEstate.BUILD_MARGIN` | `.33` | The margin a new building has to clear over its own costs to be worth putting up. |
| 145 | `RealEstate.MIN_HOMES_FOR_A_MARKET` | `10` | Below this many front doors the ratio stops meaning anything. |

### RefineryFlow.java ([map](map/RefineryFlow.md))

| line | constant | value | says |
|---:|---|---|---|
| 77 | `RefineryFlow.HYDROGEN_MADE` | `1400` | A barrel's hydrogen a reformer makes, in standard cubic feet, the middle of the research's 1.3 range [R6]. |
| 80 | `RefineryFlow.HYDROGEN_USED` | `1850` | ...and a barrel of gas oil a hydrocracker uses [R7]. |
| 83 | `RefineryFlow.RESIDUE_LITRES_PER_TONNE` | `1010` | Litres in a tonne of residue: fuel oil's [P35] (Good.FUEL_OIL), what the coke's and the bitumen's weights are struck at. |
| 86 | `RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY` | `30.44 * 158.987` | A litre a month of a barrel a day: 30.44 days of 158.987 L - how the units' sizes in barrels a day become their feed in litres (spec-oil 2.3). |
| 191 | `RefineryFlow.FLOW_ORDER` | `{ Stream.GAS_OIL, Stream.RESIDUE, Stream.HEAVY_NAPHTHA, Stream.CRACKED_GAS }` | The streams in the order they are solved (3 in the header). |

### Refining.java ([map](map/Refining.md))

| line | constant | value | says |
|---:|---|---|---|
| 114 | `Refining.CRUDE_LITRES_PER_TONNE` | `1165` | Litres in a tonne of crude [P35]: the slate is struck in litres, the crude bought in tonnes. |
| 121 | `Refining.MEDIUM_VOL_PCT` | `{ 1.5, 6.0, 12.0, 12.0, 22.0, 24.0, 22.5 }` | The straight-run cuts of a MEDIUM crude, vol% (the research's 1.1, the blend): gas, light naphtha, heavy naphtha, kerosene, diesel, gas oil and residue, in that order (the CUT_ indexes); normalised... |
| 124 | `Refining.MEDIUM_CUTS` | `normalised(MEDIUM_VOL_PCT)` | ...each a share of the barrel, MEDIUM_VOL_PCT over its sum (the column normalised, as the prototype does). |
| 127 | `Refining.CUT_GAS` | `0, CUT_LIGHT_NAPHTHA = 1, CUT_HEAVY_NAPHTHA = 2, CUT_KEROSENE = 3, CUT_DIESEL = 4, CUT_...` | The cuts' places in MEDIUM_CUTS. |
| 131 | `Refining.LIGHT_VOL_PCT` | `{ 4.1, 8.4, 15.9, 13.9, 25.6, 21.3, 11.4 }` | The straight-run cuts of a LIGHT crude, vol%, in the CUT_ order: Brent, 38 degrees API [R1] (the research's 1.1; they sum to 100.6). |
| 134 | `Refining.HEAVY_VOL_PCT` | `{ 0.3, 5.1, 10.2, 13.8, 9.4, 24.3, 36.9 }` | ...of a HEAVY crude: Maya, 21.5 degrees API [R2], its 15.3 of naphtha split one to two light to heavy as the blend's is (spec-oil 2.2). |
| 137 | `Refining.CUTS` | `{ normalised(LIGHT_VOL_PCT), MEDIUM_CUTS, normalised(HEAVY_VOL_PCT) }` | Each grade's cuts, in Deposit.Grade's order, each column normalised to one (0.7.79): LIGHT_VOL_PCT's, MEDIUM_CUTS itself, HEAVY_VOL_PCT's. |
| 140 | `Refining.MEDIUM_MIX` | `mixOf(Deposit.Grade.MEDIUM)` | A run all of MEDIUM crude, the research's blend: the mix of imports, of a refinery that took no crude, and of every crude unit until 0.7.79. |
| 150 | `Refining.RESIDUE_PER_DIESEL` | `3` | Litres of residue a litre of diesel cuts into fuel oil (Q5): three to one, so four litres of fuel oil. |
| 153 | `Refining.PRODUCTS` | `{ Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRI...` | The products a crude unit makes, in the order a screen lists them: the two the city burns first. |
| 157 | `Refining.BOUGHT_HERE` | `{ Good.PETROL, Good.DIESEL }` | The products something in the city buys (spec-oil 2.5, O1): the drivers' petrol and the railway's diesel - what the gate is struck on. |
| 357 | `Refining.RUN_KEY` | `"run.share"` | The run's share in the saved extras (THE RUN). |
| 360 | `Refining.RUN_HISTORY_KEY` | `"run.history."` | ...and each of the last months' shares, the oldest at 0 (THE RUN). |
| 674 | `Refining.LUBRICANTS_WORLD_SHARE` | `.01` | The share of a city's crude, litre for litre, the world takes in lubricants a month: the real share of crude that becomes lubricants, about 1% [R37] (docs/studies/spec-oil.md 6; the research's risk... |
| 785 | `Refining.CRUDE_COVER_MONTHS` | `1` | Months of the crude units' run kept on hand in a Tank Farm's room (★ spec-oil 2.8): one, a month's run. |
| 827 | `Refining.CARGO_KEPT_FREE` | `ham.citybuildersim.Ports.Ship.MR.tonnes()` | Tonnes of a farm's room kept free for the next cargo (R2): an MR's, the smallest tanker's (Ports.Ship.MR). |
| 1037 | `Refining.RUN_PACE` | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | The most of nameplate the crude units' run moves in a month (★ the brief's example, not measured): BusinessInvestment.MAX_RETIREMENT_FRACTION, the quarter the spare-capacity rule sheds at most. |
| 1337 | `Refining.PACKAGE_MOST_UNITS` | `12` | The most conversion units a crude unit is weighed with: the prototype's twelve rounds (spread.py, its package) - more than one of each of the seven kinds. |
| 1340 | `Refining.KIND_NAMES` |  | Each kind's name, in Kind's order: what the idle months are saved by. |

### Restaurants.java ([map](map/Restaurants.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `Restaurants.MEALS_A_PERSON_MONTH` | `90` | Meals one person eats in a month: three a day, thirty days. |
| 88 | `Restaurants.PERSON_MONTHS_PER_MEAL` | `1 / MEALS_A_PERSON_MONTH` | ...and the same fact the other way up, which is what the arithmetic wants. |
| 119 | `Restaurants.MARGIN_FLOOR` | `3.0` | What a kitchen with empty tables charges over what the food cost it. |
| 122 | `Restaurants.MARGIN_CEILING` | `8.0` | ...and what a kitchen with a queue at the door charges. |
| 131 | `Restaurants.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a kitchen's charged margin moves toward the one its queue strikes in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 169 | `Restaurants.KITCHEN_COVER_MONTHS` | `1` | Months of food a kitchen keeps. |

### Retail.java ([map](map/Retail.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `Retail.STORE_COVER_MONTHS` | `2.5` | Months of recent sales a store tries to keep on the shelf. |
| 64 | `Retail.RETAIL_MARKUP` | `1.50` | What the shops add to what their stock cost them. |
| 67 | `Retail.OPENING_SELL_PRICE` | `.3` | The price the game opened at, and the floor it will not go below - struck at the expected price level since 0.7.42 (seedConstants()). |
| 82 | `Retail.SATIATION_MULTIPLE` | `1.5` | How far over the opening floor a full basket is still wanted: 1.5x. |
| 90 | `Retail.GROCERY_ELASTICITY` | `.4` | How far a household's baskets fall with their price above satiation, an elasticity: .4, the middle of the measured food-at-home range (USDA ERS -0.3 to -0.6; Andreyeva et al. |
| 93 | `Retail.CLEARING_CAP` | `1.5` | The most the shelf's target goes over its floor however short the shops are: half again. |
| 96 | `Retail.CLEAR_SPEED` | `1.0 / 6` | The share of the way to its target, in logs, the shelf moves in a month: a sixth - about six months to clear, slower than the old quarter. |
| 99 | `Retail.FLOOR_CATCH_UP` | `.5` | How much of the gap to its floor a shelf under the floor closes in a month: half. |
| 102 | `Retail.CLEARING_BAND` | `50` | The clearing price is looked for between the shelf price over this and the shelf price times it. |
| 105 | `Retail.CLEARING_STEPS` | `50` | Halvings (in logs) of that band the search takes: fifty, far finer than a cent's grain. |
| 121 | `Retail.SUPPLIER_CREDIT_MONTHS` | `1` | How much the shops' suppliers will wait for, in months of the stock for the sale the shops expect, at what it costs to bring in: one - a month's terms, repaid at the next strike out of the sale the... |
| 179 | `Retail.SHELF` | `{ Good.GRAINS, Good.BREAD, Good.DAIRY_EGGS, Good.VEGETABLES, Good.FRUIT, Good.MEAT, Goo...` |  |
| 879 | `Retail.PUMP_MARGIN` | `.12` | What a station adds to the wholesale price it paid a litre, before the sales tax: twelve per cent (runs/research-pump.md 7: Canada's 10.4 c/L in 2025, about 10% of the wholesale ex tax [6]; the US ... |
| 887 | `Retail.QUEUE_MARGIN` | `.18` | ...and on the litres past what the stations can sell: eighteen per cent, the top of the research's range - a rural or post-spike forecourt's (runs/research-pump.md 2, 7). |
| 970 | `Retail.FuelSale.NONE` | `new FuelSale(0, 0, 0, 0, 0, 0)` | No sale: no litres asked, or no market or city to sell through. |
| 1277 | `Retail.SUPPLIER_CREDIT_KEY` | `"supplierCredit."` | The prefix the suppliers' credit is saved under among the extras. |

### SpreadPlanner.java ([map](map/SpreadPlanner.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `SpreadPlanner.FEED_GATE` | `Freight.MIN_LINE_UTILISATION` | The least share of one building's feed the spare stream must cover for it to be weighed at all: the railway's MIN_LINE_UTILISATION, nothing built to stand idle (spec-oil 2.4). |
| 88 | `SpreadPlanner.IDLE_MONTHS` | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | Months a kind must stand idle running before its sector may sell it back (spec-oil 2.4): six, RETIREMENT_LOSS_MONTHS - as long as the spare-capacity rule waits on losses. |
| 91 | `SpreadPlanner.IDLE_KEY` | `"idleMonths."` | The prefix of each kind's idle months in a client's saved extras. |
| 421 | `SpreadPlanner.ON_ITS_BORROWING` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash) { return ...` | THE RULE TO 0.7.111, KEPT AS A NAMED HISTORY: on what the order would borrow, its cost less the sector's cash - Game.consider()'s own test until 0.7.113, which asked nothing of an order the till pa... |
| 436 | `SpreadPlanner.ON_ITS_WHOLE_COST` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash) { return ...` | THE RULE IN FORCE SINCE 0.7.112 (batch I2; Jerus's decision A, 2026-10-08: "test every order on its whole cost, whoever pays"): on the whole cost - its ground at its zone's price with it (BusinessI... |
| 443 | `SpreadPlanner.DEFAULT_MONEY_GATE` | `ON_ITS_WHOLE_COST` | The money gate every SpreadPlanner tests its sector's orders by unless a check or a probe sets another: ON_ITS_WHOLE_COST since 0.7.112 (decision A). |

### WoodProducts.java ([map](map/WoodProducts.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `WoodProducts.SAWMILL` | `133` | The Sawmill's permanent id in buildings.json (0.7.140): 2,080 m3 of lumber and 1,640 m3 of residues a month from 4,000 m3 of sawlogs. |
| 68 | `WoodProducts.LARGE_SAWMILL` | `134` | The Large Sawmill's (0.7.140): 20,020 m3 of lumber and 15,785 m3 of residues a month from 38,500 m3 of sawlogs. |
| 71 | `WoodProducts.PANEL_MILL` | `135` | The Panel Mill's (0.7.140): 15,000 m3 of oriented strand board a month from 27,750 m3 of pulpwood. |
| 74 | `WoodProducts.PULP_MILL` | `136` | The Pulp Mill's (0.7.140): 11,000 t of pulp a month from 28,000 m3 of pulpwood and 21,500 m3 of residues, and 13,500 kW sold. |
| 77 | `WoodProducts.PAPER_MILL` | `137` | The Paper Mill's (0.7.140): 5,500 t of paper a month from 5,775 t of pulp and 850,000 litres of fuel oil. |
| 80 | `WoodProducts.PELLET_MILL` | `138` | The Wood Pellet Mill's (0.7.140): 6,000 t of pellets a month from 13,200 m3 of residues. |

## interface (968 constants)

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
| 438 | `BankScreen.LEAD_INFO` | `"Every loan in the city is its money, priced from what it costs the bank to make." + "T...` | The tab's (i): the Overview's lead (the spec's L2), with what the Overview shows. |
| 445 | `BankScreen.PAGE_INFO` | `{ "Its income statement, and what it did with the profit: a walk from the interest it e...` | Each page's (i), in BANK_PAGE_NAMES' order: what it holds (the landing's row blurbs until 0.7.33). |
| 1083 | `BankScreen.LADDER_INFO` | `"From the price of money to what each borrower pays.Every step up is a cost " + "the ba...` | THE LADDER's (i): the landing's note (the spec's L10), with what the parts are. |
| 1470 | `BankScreen.NOTHING` | `.0005` | A step under half a dollar is nothing: it is left off the walk and named under it. |
| 1578 | `BankScreen.PROVISIONS_INFO` | `"A provision is money set aside for loans expected to go bad: a year's " + "expected lo...` | The provisions' note (the spec's T, Profit 790), behind the step's tooltip and the statement's line. |
| 1615 | `BankScreen.RATIOS_INFO` | `"The margin is what it charges less what it pays for its money, on everything lent." + ...` | The ratios' note (Profit 874), behind MARGIN's and COSTS' (i). |
| 2081 | `BankScreen.OWNS` | `List.of(new SheetPart(Bank.Sheet.BUSINESS_LOANS, "Loans to the businesses", Palette.BUS...` | What it owns, in the bars' order (the spec's section 3): the businesses' loans first, its reserves last; the allowance is the net tick. |
| 2093 | `BankScreen.OWES` | `List.of(new SheetPart(Bank.Sheet.DEPOSIT_FUNDING, "Lent past its own cash, on its depos...` | ...and what it owes, then its owners' (equity last). |
| 2346 | `BankScreen.BESIDE_INFO` | `"Counted for what it can lend and not held on its sheet in full: the families' " + "sav...` | BESIDE THE SHEET's (i) (Balance sheet 1161). |
| 2640 | `BankScreen.BOOK_INFO` | `"Everything it has lent, by who owes it: the businesses' loans and the bonds of theirs ...` | Who-owes-it note (Lending 1225), behind THE BOOK's (i). |
| 3280 | `BankScreen.LEND_AGAINST_INFO` | `"Savings reach the bank wherever its branches are - online - so it lends " + "against e...` | What it can lend against (Funding 1681), behind the banked bar's (i). |
| 3734 | `BankScreen.STAKE_INFO` | `"A resolution makes every share the city's, in its fund's rescue book, which its rule "...` | The city's stake's note (Capital 2002). |
| 3829 | `BankScreen.MOVED_INFO` | `"Equity moves by what it earned, what it was given and what it paid out, and by " + "no...` | The equity's note (Capital 1990): Jerus's rule on a plug. |
| 3883 | `BankScreen.RESCUES_INFO` | `"When a bank loses more than it owns, the city resolves it: the old owners lose " + "ev...` | The rescues' note (Capital 2027). |
| 4104 | `BankScreen.RESCUE_INFO` | `"Its owners lose everything: the households' shares and the world's pass to the city " ...` | The rescue's note (Rescue 2119). |

### Branding.java ([map](map/Branding.md))

| line | constant | value | says |
|---:|---|---|---|
| 21 | `Branding.FOLDER` | `"/icons/"` | The folder on the classpath the icon files are in. |
| 24 | `Branding.SIZES` | `{ 16, 20, 24, 32, 40, 48, 64, 128, 256 }` | Every size the PNGs come in; the stage is given them all and the platform picks. |
| 27 | `Branding.ICO_SIZES` | `{ 16, 20, 24, 32, 40, 48, 64, 256 }` | The sizes icon.ico carries for the exe: Explorer's and the taskbar's at each display scale, and 256 for large icons. |
| 30 | `Branding.ICO` | `FOLDER + "icon.ico"` | The exe's icon on the classpath; Build EXE.bat passes the same file from the source tree. |
| 39 | `Branding.RULE` | `.0467, TOP_DROP =.413, LOW_DROP =.56, BEND_BACK =.2` | The title art's proportions, in ems of the name's font size, from the baseline and from the name's two ends (tools/branding/make_title.py draws the store art from the same numbers): the rules' thic... |
| 40 | `Branding.CURVE` | `{.0667, 0,.133, -.08,.227, -.24 }` |  |
| 41 | `Branding.CLIMB_X` | `.467, CLIMB_Y = -.747, DOT =.06, RING =.0333` |  |

### BuildScreen.java ([map](map/BuildScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 211 | `BuildScreen.BUILD_HOME` | `BuildAdvice.OVERVIEW` | Where Build opens (BUILD_HOME), and which category the player was last looking at (buildCategory). |
| 383 | `BuildScreen.CITY_DOT` | `Palette.MONEY` | The colour of "only the city builds these": the money blue - the city's own account. |
| 386 | `BuildScreen.INVESTOR_DOT` | `Palette.BUSINESS` | The colour of "investors build these too": the business violet. |
| 612 | `BuildScreen.LAND_FREE_CELL` | `236` | LAND FREE's width with its shortcut under it: the cell's own 190 and room for the shortcut's words on one line. |
| 927 | `BuildScreen.PAVE_HEAD` | `"PAVE TO A PAVED ROAD"` | The paving's heading on the Gravel Road card. |
| 930 | `BuildScreen.PAVE_TAG` | `"paving beats a new Paved Road"` | ...its tag, when paving beats a new Paved Road over its life a trip. |
| 933 | `BuildScreen.PAVE_INFO` | `"Paving lays a Paved Road on a gravel road's ground: a Paved Road's price, less the" + ...` | ...and the (i) on its two figures: what it costs and why it may win. |
| 1043 | `BuildScreen.ROAD_TAG` | `"cheapest per trip over its life"` | A road card's first tag (0.7.70): the cheapest a trip off the road over its life, its ground in it - BuildCard.ROAD_PER, shortened. |
| 1046 | `BuildScreen.ROAD_OFF` | `"trip off the road"` | ...and what its land bar is per: a trip it takes off the road (BuildAdvice.unit()). |
| 2422 | `BuildScreen.JOB_RING` | `58` | A ring's size on the Overview's tiles (its stroke is 6 px). |
| 3090 | `BuildScreen.AUTO_CHIP` | `"Automatic building on ›"` | Build's heading chip on every page but the Overview while it is on: the way back to its cards. |
| 3093 | `BuildScreen.AUTO_INFO` | `"Turned on, it orders every month what this page advises for the city's works - " + "po...` | The section's (i). |
| 3119 | `BuildScreen.AUTO_DIAL` | `(1389 - Palette.RAIL - 36 - 20) / 3.0 - 24` | A dial's width in its card at the 1,389 window: a third of the page less the card's padding. |
| 3260 | `BuildScreen.AUTO_CAP_TITLE` | `"THE HIGHEST YIELD ITS BOND WILL TAKE"` | The cap block's caption and (i) on automatic building's debt card (CD2, D28 and D30). |
| 3261 | `BuildScreen.AUTO_CAP_INFO` | `"The most automatic building will pay to sell the " + Game.BUILD_BOND_YEARS + "-year bo...` |  |
| 3271 | `BuildScreen.AUTO_CAP_LINE` | `"A sale that falls short at the cap keeps its ask open, offered again every month; " + ...` | The line under the chips: what a short sale does here (D30). |
| 3275 | `BuildScreen.ASKS_INFO` | `"An order whose bond sold short at the cap waits here for its money, and the rest " + "...` | THE OPEN ASKS' heading (i). |
| 3483 | `BuildScreen.NEED_CARD` | `300` | A card's width, on every Build page since 0.7.25 (a city category's only, in 0.7.24). |
| 4839 | `BuildScreen.RECEIPTS` | `5` | How many purchases the receipt keeps. |
| 5140 | `BuildScreen.PAYBACK_LINE` | `2 * NEED_CARD` | The pay-back line's width on the no-deposit page (0.7.101): two of Build's need cards, about as wide as the WHY section's hand-wrapped lines above it. |
| 5709 | `BuildScreen.FUND_CAP_INFO` | `"The previews above are the auction for the cash this run is short of, at the " + "high...` | The cap block's (i) on the funding page (CD2, D30). |

### ChartFitBench.java ([map](map/ChartFitBench.md))

| line | constant | value | says |
|---:|---|---|---|
| 48 | `ChartFitBench.LABELS` | `{ "Unemployment rate", "Median household income", "Consumer price index", "Bank lending...` | The line names City History offers, as long as the real ones; the first lineCount of these are picked. |
| 56 | `ChartFitBench.AREAS` | `{ Palette.MONEY, Palette.PEOPLE, Palette.BUSINESS, Palette.BUILDING }` | The four areas' colours in turn, which Palette.lineColours() spreads over the lines as City History does. |
| 59 | `ChartFitBench.MONTHS` | `240` | The months in the history the chart is handed. |

### ConstructionScreen.java ([map](map/ConstructionScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 60 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 212 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 226 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 594 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 599 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |
| 945 | `ConstructionScreen.PAVING_WORDS` | `"%,d paving · no stop"` | The Paved Road site's words while it paves gravel roads (0.7.70), where Cancel would be: how many, and that it runs on. |
| 948 | `ConstructionScreen.PAVING_TIP` | `"Some of these Paved Roads pave gravel roads, which carry their traffic until each" + "...` | ...and why. |

### FinancesScreen.java ([map](map/FinancesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 106 | `FinancesScreen.FINANCE_HOME` | `"Overview"` | The page an area opens on when none is named: the first of any area's pages that is called this. |
| 110 | `FinancesScreen.AREAS` | `{ "The position", "The book", "Borrow", "Money", "The bond market", "The city's fund" }` | The six areas, in the hub's order. |
| 112 | `FinancesScreen.AREA_ICONS` | `{ Icons.FINANCES, Icons.PAPER, Icons.COIN, Icons.BANKNOTE, Icons.REPORTS, Icons.SAFE }` | ...and each one's icon, on its card. |
| 115 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "Debt service", "Home & abroad", "Your rate" }` | The position's four pages (0.7.32: "The ladder" became the hub's hero). |
| 117 | `FinancesScreen.POSITION_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.TRADE, Icons.POLICY }` | ...and their icons on the chips. |
| 124 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece" }` | The book is one page since 0.7.32, a desk of lines since CD6. |
| 126 | `FinancesScreen.CLOSE_PAGE` | `"Close it now"` | The page Close it now opens on, under The book (CD4): one piece's reverse auction, its ticket, its ladder and its call. |
| 128 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` | Borrow's two pages: the city's own paper at home, and dollars from the world. |
| 130 | `FinancesScreen.BORROW_ICONS` | `{ Icons.HOMES, Icons.TRADE }` | ...and their icons on the chips. |
| 132 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 134 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 136 | `FinancesScreen.BOND_ICONS` | `{ Icons.REPORTS, Icons.PAPER }` | ...and their icons on the chips. |
| 138 | `FinancesScreen.FUND_PAGES` | `FundScreen.PAGES` | The city's fund as a brokerage (0.7.39; "Holdings" and "By hand" from 0.7.14): FundScreen's four pages. |
| 140 | `FinancesScreen.FUND_ICONS` | `FundScreen.ICONS` | ...and their icons on the chips. |
| 204 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Bills", "BILL", ShortTermTBill.TERMS, 1000, "months", "Sold a...` | The five kinds of paper the city sells, in the board's tile order: bills, bonds, the amortizing bond (CD2), then the floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md 2.4 and... |
| 314 | `FinancesScreen.KIND_NAMES` | `{ "Bills", "Amortizing bonds", "Bonds" }` | The ladder's three kinds, in DebtManager.LADDER_KINDS' order, as the key and a tooltip say them. |
| 385 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 388 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height... |
| 391 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentSc... |
| 541 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "The ladder i...` | The tab's (i): what the hub is, in a breath. |
| 547 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balance and the...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 817 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " + "everythi...` | What resolving a failed bank does (the spec's T41). |
| 1104 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, " + "becaus...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 1114 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " + "changes."` | The ladder with nothing on it (the spec's T11). |
| 1118 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "refinancing i...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 1123 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 1126 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1367 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month bills" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1370 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't cover the...` | Each setting's one line (the spec's T35). |
| 1378 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and what the c...` | ...and each in full, on its chip (the old block's sentences). |
| 1389 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's " + "cash,...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1409 | `FinancesScreen.ROLLOVER_CAP_TITLE` | `"THE HIGHEST YIELD IT WILL TAKE"` | The rollover's cap block: its caption, and its (i), which says what the cap is and what a short sale does (D28, D29). |
| 1410 | `FinancesScreen.ROLLOVER_CAP_INFO` | `"The most the rollover will pay to sell what it refinances: fair plus a " + "point(the ...` |  |
| 1536 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1539 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays frozen until yo...` | Each setting's one line (the spec's T40). |
| 1545 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose " + "every...` | The rescue's (i): both settings in full (the old block's T40). |
| 1763 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to " + "build...` | THE BALANCE's (i): the spec's T7. |
| 1772 | `FinancesScreen.PAPER_VALUES_INFO` | `"The same paper three ways.Its face is what falls due.What the books " + "carry it at i...` | The line under THE BALANCE's bars: three readings of the same paper. |
| 1946 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 1949 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is " + "repa...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 1998 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the interest is an expense; the principal is a balance-sheet movement." + "The in...` | LAST MONTH's (i): the spec's T14. |
| 2006 | `FinancesScreen.UPLIFT_INFO` | `"A linked bond's principal follows the price index, and the books catch up with " + "it...` | The inflation uplift line's (i) on LAST MONTH (CD5, spec-city-debt.md 5.4). |
| 2053 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city " + "earns...` | FOREIGN's (i): the spec's T16. |
| 2084 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none of " + "it ...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 2138 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the " + "exchan...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 2160 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports " + "they w...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 2322 | `FinancesScreen.CURVE_INFO` | `"Each point is what the city pays to borrow for that term: the zero rate.The short end ...` | THE CITY'S CURVE's (i): the spec's T24, and what each line on the chart is (CD6, board 5). |
| 2427 | `FinancesScreen.CurveChart.LEFT` | `46, RIGHT = 14, TOP = 18, FOOT = 26` |  |
| 2504 | `FinancesScreen.EXPECT_INFO` | `"The dial the market expects, month by month, is the path the whole curve is built on."...` | WHAT THE MARKET EXPECTS THE DIAL TO DO's (i): the path the curve is built on (CD6, board 5, note A). |
| 2511 | `FinancesScreen.LEAN` | `.0005` | The gap under which two rates are the same to the eye in the page's sentences: a twentieth of a point. |
| 2567 | `FinancesScreen.PARTS_INFO` | `"The ten-year zero rate is the sum of its parts, to rounding: the dial the market expec...` | THE 10-YEAR RATE's (i): what the bar is made of. |
| 2609 | `FinancesScreen.REAL_INFO` | `"A linked bond pays a real coupon on a principal that grows with the price index, so wh...` | REAL RATES's (i): what the two lines are and what the dots are. |
| 2763 | `FinancesScreen.NOTE_INFO` | `"No coupon.The lender's return was the discount, taken out of the proceeds at " + "issu...` | A bill's (i): the spec's T28. |
| 2770 | `FinancesScreen.CARRIED_INFO` | `"What the books hold the piece at, and the rate they charge it at.It starts " + "at wha...` | The "carried · effective" line on a line's page: its (i). |
| 2777 | `FinancesScreen.LINKED_BOOKS_INFO` | `"What the books hold the piece at, and the rate they charge it at.The books " + "follow...` | The same line on an inflation-linked bond's card (CD6): its rate is a real rate. |
| 2784 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, " + "the c...` | A price against face, in words (the spec's T33, D16). |
| 2791 | `FinancesScreen.CALLABLE_INFO` | `"The city sold this line with the right to call it: from its first call date " + "it ca...` | A callable line's (i) on its card (CD4, spec-city-debt.md 2.6 and 6.4). |
| 2796 | `FinancesScreen.FLOATING_INFO` | `"The coupon is the policy rate plus the margin the sale fixed, a twelfth of it " + "eve...` | A floating-rate note's coupon line's (i) on its card (CD5, spec-city-debt.md 2.4). |
| 2803 | `FinancesScreen.INDEXED_INFO` | `"The real coupon is paid every month on the principal as the price index has " + "moved...` | An inflation-linked bond's coupon line's (i) on its card (CD5, spec-city-debt.md 2.5). |
| 2810 | `FinancesScreen.WORTH_CALLING_INFO` | `"The present value the call would save: what this line's remaining payments " + "are wo...` | "Worth calling"'s (i) (D24). |
| 2816 | `FinancesScreen.DESK_CURVE_INFO` | `"Today's zero curve is the line: what the city pays to borrow for each term.Each " + "l...` | THE CURVE card's (i) (CD6, board 1): what the dots are and what is left off. |
| 2823 | `FinancesScreen.DESK_BARS_INFO` | `"Carried is what the books say the city owes: what it raised, plus the discount " + "am...` | THE THREE BARS' (i): the board's paragraph. |
| 2832 | `FinancesScreen.GAIN_INFO` | `"Green is a gain and red a loss.It is what the books carry the line at less what " + "c...` | The (i) beside a gain or a loss on a line's page (CD6, Jerus 2026-10-23): what the colours mean. |
| 2840 | `FinancesScreen.DESK_INFO` | `"If closed now: the carrying value less the worth, in D$ and as a share of the " + "car...` | THE DESK's (i): the two readings under the table. |
| 2920 | `FinancesScreen.BOOK_KIND_NAMES` | `{ "bills", "amortizing bonds", "bonds", "floating-rate notes", "inflation-linked bonds" }` | The kinds of paper as the desk names them, in DebtManager.BOOK_KINDS' order. |
| 3088 | `FinancesScreen.CLOSE_CELL` | `206` | The width of the desk's If closed now column (CD6): the gain or loss, and the Close it now button beside it. |
| 3091 | `FinancesScreen.CLOSE_FIGURE` | `134` | The width of the gain or loss in that cell: "−D$725.3M · −100.0%" at the figure's size. |
| 3094 | `FinancesScreen.CLOSE_BUTTON` | `66, CLOSE_GAP = 6` | The Close it now button's width, and the gap between it and the figure. |
| 3292 | `FinancesScreen.LINE_PRICE_INFO` | `"The price is the market's: what the line's remaining payments are worth on " + "each m...` | THE PRICE card's (i): board 2, note A. |
| 3299 | `FinancesScreen.LINE_CLOSE_INFO` | `"Worth is what closing the line would cost at its fair price; a buyback pays " + "more,...` | IF YOU CLOSED IT TODAY's (i): board 2, note B. |
| 3308 | `FinancesScreen.LINE_BOOKS_INFO` | `"The books in full: what the line is carried at when the month opens, the " + "interest...` | THE NEXT SIX MONTHS' (i): board 2, note C. |
| 3315 | `FinancesScreen.LINE_SALES_INFO` | `"Each sale into the line: when, how much face, what it raised, the price it " + "made f...` | ITS SALES, AND ITS RISK's (i). |
| 3321 | `FinancesScreen.LINE_PAGE` | `"A line"` | The name of a line's page, under The book: no chip's, open while its line is on the books (lineOpenNow()). |
| 3510 | `FinancesScreen.THREE_NARROW` | `1000` | The width under which a line's page stacks its three cards (CD6): the books' table, then the other two side by side. |
| 3741 | `FinancesScreen.DeskCurve.H` | `232, LEFT = 46, RIGHT = 14, TOP = 12, FOOT = 26` |  |
| 3836 | `FinancesScreen.DeskBars.H` | `158, LEFT = 84, RIGHT = 76, TOP = 26, ROW = 42, BAR = 26` |  |
| 3837 | `FinancesScreen.DeskBars.ROWS` | `{ "at face", "carried", "worth today" }` |  |
| 3889 | `FinancesScreen.PathChart.H` | `212, LEFT = 40, RIGHT = 16, TOP = 14, FOOT = 26` |  |
| 4011 | `FinancesScreen.CLOSE_INFO` | `"Closing a piece early is a reverse auction, not one price.Name the face you want " + "...` | THE TICKET's (i): the spec's 6.2. |
| 4020 | `FinancesScreen.OFFERS_INFO` | `"Each bar is one slice a holder offers: its length is the slice's face, the number " + ...` | THE OFFERS' (i). |
| 4026 | `FinancesScreen.COMPARE_INFO` | `"One buyback, read three ways.Against carrying value is the figure the books keep, " + ...` | THE THREE COMPARISONS' (i): the spec's 6.3. |
| 4034 | `FinancesScreen.CALL_INFO` | `"From its first call date the city can pay every holder 100 for each 100 of face, " + "...` | THE CALL's (i): the spec's 6.4 and D22 to D24. |
| 4418 | `FinancesScreen.BuybackLadder.LEFT` | `74, FACE_ROOM = 76, MARKS = 134, TOP = 26, FOOT = 10, FIRST_W = 720, LEAST_W = 440` | The margins, in pixels: the prices at the left; at the right the room for a slice's face (FACE_ROOM) and for the marks' names (MARKS). |
| 4583 | `FinancesScreen.TERMS_INFO` | `"Each column is today's fair yield for this paper at that term: what the curve " + "say...` | The terms' (i): the spec's T55, on the auction's fair yield (CD2). |
| 4593 | `FinancesScreen.CAP_INFO` | `"The most the city will pay to sell this: fair plus a point, a yield you name, or " + "...` | THE HIGHEST YIELD's (i): the spec's 4.1 and 4.3 in two sentences. |
| 4601 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " + "smalles...` | The ask's (i): the spec's T56. |
| 4605 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here - at home the auction itself, run on " + ...` | The quote's empty state: the spec's T57. |
| 4610 | `FinancesScreen.PROCEEDS_INFO` | `"A bill's ask is the cash it should raise, and its face is the lot that brings " + "it,...` | Why a bill's proceeds are not the ask: the spec's T58 (a bill's ask is the cash; a bond's is the face). |
| 4616 | `FinancesScreen.BUYERS_INFO` | `"Each bidder bids by the rule it follows elsewhere in the game.The households " + "firs...` | Who bids: the spec's T61 and 4.2 (CD2). |
| 4626 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "devaluation...` | Where the dollars go: the spec's T62. |
| 4798 | `FinancesScreen.CALLABLE_SALE_INFO` | `"A bond of ten years or more can be sold callable: from its first call date " + "(five ...` | The CALLABLE switch's (i) (CD4, spec-city-debt.md 2.6 and D22 to D24). |
| 4858 | `FinancesScreen.ASK_TYPED_INFO` | `"Type an amount in dollars: digits, with or without commas, a decimal " + "point if you...` | HOW MUCH's (i): what the box takes - the case rules - and what the buttons do. |
| 4866 | `FinancesScreen.ASK_WORDS` | `java.util.regex.Pattern.compile("(?:[A-Za-z]{0,3}\\$)?\\s*((?:\\d{1,3}(?:,\\d{3})+\|\\d+...` | A typed amount: an optional mark, digits (grouped by commas or not), an optional fraction, an optional unit. |
| 5379 | `FinancesScreen.STOP_INFO` | `"The one yield at which the bids first cover the issue - never under fair, never " + "o...` | STOPS AT's (i): the stop and the tail (the spec's 4.3). |
| 5384 | `FinancesScreen.COVER_INFO` | `"What was bid up to your highest yield, each bidder counted at most the issue, " + "ove...` | BIDS' (i): the cover. |
| 5548 | `FinancesScreen.AuctionChart.WHO_COLOURS` | `{ Palette.PEOPLE, Palette.BUSINESS, Palette.ORE, Palette.MONEY }` | The bidders' colours in Auction.Fill's order, as the holders' bar has them. |
| 5550 | `FinancesScreen.AuctionChart.WHO` | `{ "households", "the firms", "the world", "the bank" }` | ...and their names, in the same order. |
| 5552 | `FinancesScreen.AuctionChart.H` | `240, LEFT = 62, RIGHT = 16, TOP = 16, FOOT = 26, FIRST_W = 720, LEAST_W = 420` | Its height and its margins, in pixels; the width it is first drawn at, and the least it is drawn at. |
| 5554 | `FinancesScreen.AuctionChart.SAMPLES` | `60` | How many yields the bids are read at across the axis. |
| 5742 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate and " + "eno...` | The default page's (i). |
| 5883 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's - plus " ...` | M2's (i): the spec's T69. |
| 5973 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no more " + "...` | No bond outstanding: the spec's T70. |
| 5978 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has not " + "t...` | The prices' (i): the spec's T71. |
| 5983 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when it " + "me...` | The order books' (i): the spec's T74. |
| 5991 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 5993 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY_DARK }` | ...and their colours on it. |
| 6150 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since " + "whate...` | A bond's book's (i): the spec's T75, T77 and T78. |

### FoundingScreen.java ([map](map/FoundingScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 128 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 176 | `FoundingScreen.CARDS` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "founding-card"); t.s...` | The worker the card is measured on, one at a time. |
| 227 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 230 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |
| 233 | `FoundingScreen.SEED_FIELD` | `200` | The World's field: room for a seed of nineteen digits and a sign in the figures' face at 14 px (0.7.56). |
| 236 | `FoundingScreen.PATH_SEED_FIELD` | `130` | The path's seed field (WN1): 130 px, room for a rolled seed's nine digits in the figures' face at 14 px, so the row of chips, field and dice fits the Earth's left column (LEFT). |
| 239 | `FoundingScreen.EARTH_PANEL` | `1200` | The panel on Earth (0.7.121): two columns, the city's and the world's, 40 apart - 1,200 px, inside a 1,280 window with the page's margins. |
| 242 | `FoundingScreen.COLUMN_GAP` | `40` | Between the two columns on Earth. |
| 245 | `FoundingScreen.GLOBE_W` | `GlobeFrame.FOUNDING_W` | The globe's width, the right column's: 560 px (GlobeFrame.FOUNDING_W since 0.7.135, so a harness draws what it draws). |
| 248 | `FoundingScreen.GLOBE_H` | `GlobeFrame.FOUNDING_H` | The globe's height: 270 px, so the page still fits an 868-pixel window with the longest card under it (the words probe's, 0.7.121; GlobeFrame.FOUNDING_H since 0.7.135). |
| 251 | `FoundingScreen.LEFT` | `EARTH_PANEL - 2 * PANEL_PAD - COLUMN_GAP - GLOBE_W` | The left column's width on Earth: what the panel leaves. |
| 702 | `FoundingScreen.NO_SPOT_WORDS` | `"None yet.Drag the globe or hold W, A, S and D to turn it, zoom with the wheel or - and...` | The card's line before a spot is picked; how the globe is used. |
| 1086 | `FoundingScreen.Globe.MOST_KM_PX` | `2 * EarthProj.RADIUS_M / 1000 /(GlobeFrame.FIT_SHARE * GLOBE_H)` | The widest view: the whole planet, 12,742 km, across GlobeFrame.FIT_SHARE (nine tenths) of the globe's height. |
| 1089 | `FoundingScreen.Globe.GROUND_KM_PX` | `GlobeFrame.FOUNDING_HANDOFF_KM` | From here closer the ground is drawn round the view's centre, not through the globe: GlobeFrame.FOUNDING_HANDOFF_KM, 2.32 km a pixel (★E9; 4 until 0.7.135). |
| 1092 | `FoundingScreen.Globe.LEAST_KM_PX` | `5 * World.PLOT_M / 1000.0` | The closest view: 150 m a pixel, five plots (spec-earth 2's 150 m view). |
| 1095 | `FoundingScreen.Globe.PICK_KM_PX` | `0.3` | How close a pick from the search or Random spot opens: 0.3 km a pixel, the 5 km round the site 33 pixels across. |
| 1098 | `FoundingScreen.Globe.STEP` | `MapFrame.ZOOM_STEP` | A wheel notch's zoom, the map's: MapFrame.ZOOM_STEP. |
| 1101 | `FoundingScreen.Globe.SPACE` | `0xff0b1118` | Space round the globe: the page's stage. |
| 1104 | `FoundingScreen.Globe.COLOURS` | `GlobePicture.colours(false)` | The ground's colours, as the map draws them where the city owns it (GlobePicture.colours()): the founding globe dims nothing. |

### FundScreen.java ([map](map/FundScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `FundScreen.AREA` | `"The city's fund"` | The area's name on Finances. |
| 93 | `FundScreen.PAGES` | `{ "Portfolio", "Search", "Activity", "Rules & cash" }` | The fund's four pages (0.7.39; "Holdings" and "By hand" until then). |
| 95 | `FundScreen.ICONS` | `{ Icons.SAFE, Icons.EXCHANGE, Icons.REPORTS, Icons.SETTINGS }` | ...and their icons on the chips. |
| 105 | `FundScreen.ACTIVITY_MONTHS` | `60` | The months of the record Activity shows before "show older", and how many more each press adds. |
| 108 | `FundScreen.SECURITY_ROWS` | `12` | The rows of a security's own record under its book. |
| 111 | `FundScreen.CLOSED_ROWS` | `8` | The closed lots the Portfolio's CLOSED card lists before its fold. |
| 114 | `FundScreen.MONEY_STEPS` | `{ 1_000, 10_000, 100_000, 1_000_000 }` | The money steps: the ticket's on a buy by amount and on a bond's face, and PAY IN, DRAW OUT's; a quantity of shares steps by 1, 10, 100 and 1,000 instead. |
| 117 | `FundScreen.LEFT` | `812, RIGHT = 412` | The security page's two columns at the 1,389 window: the picture and its facts, then the position and the ticket. |
| 157 | `FundScreen.FAIR` | `"fair", BID = "bid", ASK = "ask", LAST = "last", OWN = "own"` | The ticket's prices. |
| 417 | `FundScreen.SHARES` | `"COMPANY SHARES", RESCUE = "THE RESCUE BOOK", BONDS = "BONDS"` | The holdings' groups, in the page's order. |
| 971 | `FundScreen.HERO_INFO` | `"Everything the fund holds at the marks every other holder uses, and its cash.The " + "...` | THE CITY'S FUND's (i). |
| 1084 | `FundScreen.COL_NAME` | `250, COL_UNITS = 120, COL_PRICE = 110, COL_VALUE = 110, COL_AVG = 110, COL_PNL = 160` | HOLDINGS' columns: the name, units, price, worth, average cost, unrealized, and the share of the fund's bar. |
| 1087 | `FundScreen.HOLDINGS_INFO` | `"Every lot the fund holds, at its mark, with what it cost by the average-cost " + "meth...` | HOLDINGS' (i). |
| 1280 | `FundScreen.POP_MILLIS` | `220` | How long a payment's pop takes each way. |
| 1307 | `FundScreen.KINDS_INFO` | `"Each kind of holding since the fund began: what the city put into it(bought on " + "th...` | SINCE IT BEGAN, BY KIND's (i). |
| 1402 | `FundScreen.FILTERS` | `{ "All", "Shares", "Bonds", "Held" }` | Search's chips. |
| 1405 | `FundScreen.SEARCH_INFO` | `"Every company listed on the exchange, every business's bond still outstanding, and " +...` | Search's (i): the spec's D1. |
| 1813 | `FundScreen.TICKET_INFO` | `"An order goes on the book at the next month's step, after every other participant " + ...` | THE ORDER TICKET's (i). |
| 2059 | `FundScreen.GROUPS` | `{ "All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS }` | Activity's chips: every row, or one group of them (FundView.groupOf()). |
| 2203 | `FundScreen.WITHDRAWAL_LADDER` | `520` | The withdrawal's ladder: the card runs the page's width, the dial at the left and what it would do beside it. |
| 2221 | `FundScreen.WITHDRAWAL_CAVEAT` | `"next month's on the fund as it stands - its prices and what the book takes will move it"` | The withdrawal's caveat, under what it would do. |
| 2223 | `FundScreen.WITHDRAWAL_CAVEAT_INFO` | `"Next month's withdrawal is struck on what the fund is worth at the top of " + "next mo...` | ...and the sentence behind it, its (i). |
| 2331 | `FundScreen.RESCUE_BOOK_INFO` | `"What the city holds from rescuing its bank: the shares it took when it " + "resolved i...` | The rescue book's (i). |

### GovernmentScreen.java ([map](map/GovernmentScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 93 | `GovernmentScreen.GOV_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.REPORTS }` | Each page's icon on its chip (0.7.31). |
| 116 | `GovernmentScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with SURPLUS's change, the pages, and their gaps - until the frame is laid out and its own height ... |
| 119 | `GovernmentScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - SectorScreen... |
| 122 | `GovernmentScreen.BRIDGE` | `"#bridge", BALANCE = "#balance"` | The scroll targets on the Overview: the bridge card and the balance. |
| 218 | `GovernmentScreen.HEAD_INFO` | `"What the city took in and paid out last month, and what its money did." + "EARNED is t...` | The tab's (i): what the five figures are, in a breath. |
| 376 | `GovernmentScreen.TRAILING_MONTHS` | `12` | Every "of GDP" on this tab reads the trailing year of its line where History records it (B7, 0.7.47): the last TRAILING_MONTHS months, once the line has that many. |
| 379 | `GovernmentScreen.LINE_KEYS` | `java.util.Map.ofEntries(java.util.Map.entry("Business tax", List.of("taxBusiness", "tax...` | The History series each budget line is, where it records one - the line's own figure each month (a series of something else, like the care's and the schools' net cost against their gross lines, is ... |
| 396 | `GovernmentScreen.REVENUE_KEYS` | `List.of("revenue")` | What was taken in: History's revenue. |
| 398 | `GovernmentScreen.SURPLUS_KEYS` | `List.of("surplus")` | ...the balance: History's surplus, negative for a deficit. |
| 418 | `GovernmentScreen.RETIRED_GAIN_KEY` | `"retiredGain"` | ...the gain (loss) on debt retired, which the balance carries and spending does not (CD4, CD3's ★5). |
| 758 | `GovernmentScreen.SURPLUS_INFO` | `"It took in more than it spent.A surplus pays down debt or buys the next " + "thing wit...` | P1's rest: what a surplus is for. |
| 762 | `GovernmentScreen.DEFICIT_INFO` | `"It spent more than it took in.That gap is borrowed, and next month's " + "interest is ...` | ...and what a deficit costs. |
| 901 | `GovernmentScreen.BRIDGE_INFO` | `"EARNED is the header's figure: taxes and fees less the running programmes " + "- inter...` | The bridge card's (i): P2, rewritten, and P3. |
| 917 | `GovernmentScreen.DIALS_INFO` | `"EARNED is read at today's tax rates; the budget was struck at the month's." + "A dial ...` | The dials' step's tooltip (the spec's B7). |
| 922 | `GovernmentScreen.RESIDUAL_INFO` | `"What the named steps do not explain: timing between the books and the money, " + "and ...` | The "not accounted for" step's tooltip. |
| 928 | `GovernmentScreen.NOT_CASH_INTEREST_INFO` | `"The budget charges the discount wearing off the city's paper as " + "interest, a littl...` | The not-cash interest step's tooltip: the discount wearing off the city's paper, charged to the budget and paid to nobody yet. |
| 934 | `GovernmentScreen.NOT_CASH_GAIN_INFO` | `"Buying paper back for less than it is carried at books a gain, and for " + "more a los...` | The not-cash gain step's tooltip: the gain on paper bought back, booked against what it was carried at, paid by nobody. |
| 939 | `GovernmentScreen.NOT_CASH_UPLIFT_INFO` | `"A linked bond's principal follows the price index, and the budget " + "charges what th...` | The not-cash uplift step's tooltip (CD5): the indexation of the linked bonds, charged to the budget and paid in the bond's last month. |
| 946 | `GovernmentScreen.RETIRED_GAIN_INFO` | `"Paper bought back before it falls due is booked at what it was carried " + "at less th...` | The gain (loss) on debt retired: the line under the budget's operating lines, and what is in the balance. |
| 1151 | `GovernmentScreen.ARREARS_INFO` | `"What the ceiling cut: owed, with no interest on it, and paid down out of the " + "firs...` | P6: the arrears. |
| 1192 | `GovernmentScreen.ECONOMY_INFO` | `"A year of revenue against a year of output is the only honest way to " + "compare a bu...` | P8: why a month against a year. |
| 1199 | `GovernmentScreen.NO_YEAR` | `"There is no output recorded yet, so nothing here can be put in " + "proportion.A month...` | P7: no month of output yet. |
| 1374 | `GovernmentScreen.LIST_COLUMNS` | `{ 100, 60, 70 }` | The column heads' widths: a month, of the budget, of GDP. |
| 1377 | `GovernmentScreen.LIST_DOOR` | `96` | ...and the door's at the end of a row: "set it ›", "Land office ›". |
| 1639 | `GovernmentScreen.FUND_INFO` | `String.format("The withdrawal dial's share of everything the city's fund holds, a " + "...` | P20: the fund's transfer. |
| 1996 | `GovernmentScreen.PRINCIPAL_INFO` | `"Not on the list above, and deliberately: repaying principal is not " + "spending.The m...` | P27: principal is not spending. |
| 2001 | `GovernmentScreen.TERM_INFO` | `"A term loan pays its coupon every month and the whole face at the end, so the " + "mon...` | P28: term loans. |
| 2006 | `GovernmentScreen.SERIAL_INFO` | `"Serial bonds amortise - a slice of principal falls due every anniversary, so " + "both...` | P29: serial bonds. |
| 2010 | `GovernmentScreen.FLOATING_INFO` | `"A floating-rate note pays the policy rate of the month plus a margin fixed at " + "the...` | Floating-rate notes (CD5, spec-city-debt.md 2.4). |
| 2016 | `GovernmentScreen.LINKED_INFO` | `"An inflation-linked bond pays a real coupon on a principal that follows the " + "price...` | Inflation-linked bonds (CD5, spec-city-debt.md 2.5). |
| 2022 | `GovernmentScreen.NOTES_INFO` | `"A note pays no coupon.The lender's return is the discount, taken out of the " + "proce...` | P31: notes. |
| 2028 | `GovernmentScreen.COUPON_INFO` | `"The coupons on the budget are struck from what the city actually paid over a " + "comp...` | P30: the coupon and the budget disagreeing. |
| 2168 | `GovernmentScreen.BOTH_DEFICIT_INFO` | `"Both run at a deficit on purpose.What the deficit buys is on the " + "Services tab, in...` | P32, as it is true: both cost the city more than they charge. |
| 2172 | `GovernmentScreen.NOT_BOTH_INFO` | `"Care and the schools are meant to run at a deficit: what it buys is on the " + "Servic...` | ...and when one does not (B16: fees with no bill). |
| 2211 | `GovernmentScreen.PENSIONS_INFO` | `"It is pay-as-you-go: this month's workers pay this month's pensioners, so " + "the gap...` | P33: pay-as-you-go. |
| 2294 | `GovernmentScreen.LAYERS_INFO` | `"What GDP is made of over the years, as City History draws it in layers: " + "consumpti...` | The layers' caption's (i). |
| 2371 | `GovernmentScreen.STOCK_INFO` | `"Stock built up is output that has been made and not yet sold, so it counts " + "the mo...` | P34: stock built up. |
| 2381 | `GovernmentScreen.GOVERNMENT_INFO` | `"Services with no market price, valued at cost: the utilities' staff, " + "care, the sc...` | P35, rewritten (B5): what G is, as the model counts it. |
| 2419 | `GovernmentScreen.MOM_INFO` | `"Month-on-month annualised is one month multiplied up - compounded, so a city " + "grow...` | P37, on the strip's month on month. |
| 2424 | `GovernmentScreen.YOY_INFO` | `"This month's output against the same month a year ago, in today's money - " + "nominal...` | P36's point, on the strip's year on year: nominal, where the header's tile is real. |

### HistoryScreen.java ([map](map/HistoryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 173 | `HistoryScreen.TO_CHART` | `"chart", TO_HARD_TIMES = "hard times"` | The two places a draw can be asked to scroll to: the row above the big chart, and the hard times' heading. |
| 200 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheSectors(withTheMarket(new Trace[] { new Trace("gd...` |  |
| 577 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and how fast it ...` |  |
| 848 | `HistoryScreen.FRAME_CHROME` | `160` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 851 | `HistoryScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out - BankScreen's and Trade's. |
| 854 | `HistoryScreen.SLACK` | `2` | What a canvas leaves unused of the width it is given (0.7.40), so a pixel's rounding never makes it wider than what holds it. |
| 944 | `HistoryScreen.LEAD_INFO` | `"Every month the city has lived, and what it did.The big chart draws the lines " + "you...` | The title's (i): the old lead, and how the big chart is read. |
| 950 | `HistoryScreen.YEAR_BOOK_INFO` | `"Writes the whole run out as plain text - every series the history " + "keeps, folded o...` | What "Write the year book" writes (the old SEND THIS RUN TO SOMEBODY paragraph). |
| 1352 | `HistoryScreen.SMALL_CHART` | `120` | How tall a pinned chart's plot is: 150 until 0.7.37, 120 since, so the big plot ends above the fold at 1,389 x 868 (D2). |
| 1597 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1600 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1603 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1670 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart's plot is on the page; the lanes and the overview are under it. |
| 1673 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 1951 | `HistoryScreen.FULL_PAD_TOP` | `14, FULL_PAD_SIDE = 22, FULL_PAD_BOTTOM = 12` | The full-screen pane's padding: above the chart, beside it on each side, and below it, in pixels; the chart gets the window less these. |
| 2282 | `HistoryScreen.MOVE_MILLIS` | `600` | How long a figure takes to slide to a month's new reading, in milliseconds - the range bar's dot; the figure counts on over SectorScreen's own time. |
| 2461 | `HistoryScreen.AXES_INFO` | `"Lines measured in the same thing are drawn against each other on a real axis; " + "two...` | PICK WHAT TO DRAW's (i): how the lines share axes. |
| 2668 | `HistoryScreen.HARD_TIMES_SHOWN` | `5` | At most this many hard times are listed in view; the rest are counted, and in the details. |
| 2671 | `HistoryScreen.DECISIONS_SHOWN` | `8` | At most this many decisions are listed in view. |
| 2674 | `HistoryScreen.KINDS` | `{ "recession", "depression", "slump", "epidemic", "financial", "currency", "inflation",...` | The kinds of episode, in the order the details list them. |
| 2677 | `HistoryScreen.KIND_NAMES` | `{ "Recessions", "Depressions", "Slumps", "Epidemics", "Financial crises", "Currency cri...` | ...and what the details call each, in that order. |
| 2987 | `HistoryScreen.AT_END` | `1e-6` | How near an end of its band a price must be to be AT it - a rounding hair of the band (the market strikes an end exactly). |
| 2990 | `HistoryScreen.BAND_ROOM` | `1.25` | The scale a good's row is drawn on: the band from 0 to 1, and room past its ceiling for the month's trade. |
| 2993 | `HistoryScreen.GOODS_INFO` | `"What a unit costs here this month, against what the world pays for one and " + "what i...` | The section's (i). |

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
| 214 | `Icons.RAIL` | `"M7 3h10a2 2 0 0 1 2 2v9a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V5a2 2 0 0 1 2-2z" + "M5 10h14 M9...` | A train's face: Rail, and the Freight sector whose railway it is. |
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
| 370 | `Icons.WELL` | `"M3 21h18 M8 21l3-9 3 9 M2 9l14-4 3 3-14 4z M16 5v3 M5 12v4"` | A land well, its beam nodding over the pad: the pictogram's land wells. |
| 373 | `Icons.PLATFORM` | `"M2 20c2 0 2-1.5 4-1.5S8 20 10 20s2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5" + " M6 18V10h12v...` | A platform on its legs over the waves: the pictogram's platform wells. |
| 377 | `Icons.TANKER` | `"M2 15l2 4h16l2-4z M5 15v-3h9v3 M16 15V8h3v7"` | A tanker, low in the water: imported crude. |
| 380 | `Icons.VESSEL` | `"M8 7a4 4 0 0 1 8 0v10a4 4 0 0 1 -8 0z M8 9h8 M8 15h8"` | A process vessel, a column with its trays: a conversion unit's box. |
| 383 | `Icons.TANK` | `"M4 9l8-4 8 4 M4 9v11h16V9 M4 14h16"` | A storage tank, its roof a shallow cone: what goes into the refiners' tanks, and a Tank Farm's crude. |
| 386 | `Icons.FLAME` | `"M12 3c3 4 5 6.5 5 10a5 5 0 0 1 -10 0c0-2 1-3.5 2.5-5.5 2 1.5 3 2.5 3 -1-3 -.5-5.5 0-8z"` | A flame: the furnaces, where residue no diesel can cut is burned. |
| 389 | `Icons.EXCHANGE` | `"M8 3L4 7l4 4 M4 7h16 M16 21l4-4-4-4 M20 17H4"` | Two arrows passing, one each way (Lucide's arrow-left-right): money changed from one currency to the other - the Trade tab's exchange (0.7.35). |
| 514 | `Icons.DICE` | `"M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2z" + " M8 8h...` | A die showing five (Lucide "dice-5"): the founding screen's roll of a new world (0.7.56). |
| 518 | `Icons.MAP` | `"M3 6l6-3 6 3 6-3v15l-6 3-6-3-6 3z M9 3v15 M15 6v15"` | A folded map (Lucide "map", its earlier three-panel form): the land office's map, expanded over the window, and Build's shortcut, "Buy the best: North 3 · ..." (0.7.61; "Buy the best land" until 0.... |
| 521 | `Icons.ZONING` | `"M12 8l6-3-6-3v10" + " M8 11.99l-5.5 3.14a1 1 0 0 0 0 1.74l8.5 4.86a2 2 0 0 0 2 0l8.5-4...` | A flag on a plot (Lucide "land-plot", its relative moves written absolute): Policy's Zoning area, its chip, its card and a zoning decision's flag (0.7.109, batch Z2). |
| 526 | `Icons.TREES` | `"M10 10v.2A3 3 0 0 1 8.9 16H5a3 3 0 0 1-1-5.8V10a3 3 0 0 1 6 0Z M7 16v6 M13 19v3" + " M...` | Two trees (Lucide "trees"): the forest law - its tab, its card and its page (0.7.109, batch Z2). |
| 530 | `Icons.EXPAND` | `"M15 3h6v6 M9 21H3v-6 M21 3l-7 7 M3 21l7-7"` | Two arrows out to the corners (Lucide "maximize-2"): the land office's Expand (0.7.61). |
| 533 | `Icons.SETTINGS` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2.34 0 0 1 ...` | A gear. |

### InfrastructureScreen.java ([map](map/InfrastructureScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 111 | `InfrastructureScreen.INFRA_PAGES` | `{ "Roads", "Transit", "Freight", "Ports" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know (FR4: "The railway" and "Freight" are one page, and the Ports a new one;... |
| 115 | `InfrastructureScreen.INFRA_ICONS` | `{ Icons.ROADS, Icons.BUS, Icons.LORRY, Icons.TANKER }` | Each page's icon on its chip (0.7.29; FR4): the road, the bus, the lorry and the ship. |
| 118 | `InfrastructureScreen.DIVISIONS` | `{ "All", "Haulage", "Rail", "Ports" }` | The freight business's divisions, in the strip's order (FR4; spec-freight-and-infrastructure 6): All is today's Freight page, Haulage the trucks, Rail the railway, Ports the Ports page. |
| 132 | `InfrastructureScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). |
| 135 | `InfrastructureScreen.DIVISION_CHROME` | `38` | ...and what the division strip adds on the Freight and Ports pages (FR4): its chips and the gap above them. |
| 138 | `InfrastructureScreen.CURVE_MAX` | `2.0` | The flow curve's x scale: what the road serves, from nothing to this (0.7.41; its use, to 260%, until then), fixed so the dot's motion reads month to month; the floor ends at MIN_THROUGHPUT / FREE_... |
| 348 | `InfrastructureScreen.UNKNOWN_YET` | `"known after a month: the save predates 0.7.29"` | What a dash means, where one is shown. |
| 490 | `InfrastructureScreen.FLOW_INFO` | `String.format("Every business in the city multiplies its output by its " + "flow, and s...` | The flow's (i): the old page's paragraph under its big figure, said in served since 0.7.41. |
| 581 | `InfrastructureScreen.WALK_INFO` | `"Every building makes trips - its staff to and from work, its goods and its " + "bulk i...` | The walk's (i). |
| 588 | `InfrastructureScreen.CARS_INFO` | `String.format("A fully motorised city asks %.1f times the commuter road a city where " ...` | The car row's (i): the old page's car paragraph (P4). |
| 596 | `InfrastructureScreen.NO_CARS_INFO` | `"Nobody in this city owns one yet, so a commuter costs the road exactly " + "one trip a...` | ...and with nobody driving (P3). |
| 601 | `InfrastructureScreen.COSTS_INFO` | `"\"Costs\" is what one trip of this kind asks of the street once it is on " + "it: a lo...` | The stream cards' "costs" (P2, rewritten: a commuter is not always one). |
| 720 | `InfrastructureScreen.FlowCurve.W` | `270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5` | The plot's size, and the room for the axes' figures at its left and under it. |
| 912 | `InfrastructureScreen.NO_TRANSIT_INFO` | `"This city has built no transit at all.A Bus Network is the cheap rung " + "and a Metro...` | The no-transit card's (i) (P6). |
| 918 | `InfrastructureScreen.CEILINGS_INFO` | `"A city that builds a metro and no streets gets a metro nobody can reach, " + "and no c...` | The ceilings' (i) (P7). |
| 1009 | `InfrastructureScreen.RIDERS_INFO` | `"A household holds one car at most, so a couple's second earner and four of five " + "f...` | The riders' caption's (i) (0.7.49). |
| 1108 | `InfrastructureScreen.FARE_LADDER` | `520` | The fare's ladder, and its effects under it: the card is one of two across the page. |
| 1145 | `InfrastructureScreen.FARE_INFO` | `"A FARE IS A PRICE TO CAR OWNERS AND A CHARGE TO EVERYONE ELSE.Commuters with no car " ...` | The fare's (i) (P9). |
| 1151 | `InfrastructureScreen.PREVIEW_INFO` | `"The riders line is this month's ceilings at the new fare, which is the " + "honest hal...` | The preview's (i) (P10). |
| 1255 | `InfrastructureScreen.QUOTE_INFO` | `"Of what a lorry would charge for the same tonne.The railway is a " + "private business...` | The quote's (i) (P12). |
| 1262 | `InfrastructureScreen.NO_TRACK_INFO` | `"Nobody has laid a line in this city, so every tonne that leaves " + "it leaves by lorr...` | No track (P11). |
| 1267 | `InfrastructureScreen.LINE_RULE_INFO` | `String.format("It will not lay a line it cannot fill to %.0f%%, which is why a town doe...` | The line rule (P17). |
| 1273 | `InfrastructureScreen.BILL_INFO` | `"The month's lorry bill is what the tonnes that crossed the city's boundary " + "would ...` | The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). |
| 1370 | `InfrastructureScreen.RELIEF_INFO` | `String.format("A tonne that leaves by train does not drive across the city to leave by ...` | The relief's (i) (P15). |
| 1405 | `InfrastructureScreen.BIGGER_INFO` | `"It bills under three quarters of what the rule allows.It is allowed to " + "charge for...` | The too-big railway (P16). |
| 1497 | `InfrastructureScreen.BAND_INFO` | `"Every traded good's price has a cost of MOVING it inside the gap between " + "what the...` | The bars' (i) (P18 and P20, for bars rather than a grid). |
| 1636 | `InfrastructureScreen.LORRY_INFO` | `String.format("A vehicle moves about %,.0f tonnes a month and lasts %.0f years.The " + ...` | The lorries' (i) (P23). |
| 1727 | `InfrastructureScreen.HAULAGE_QUOTE_INFO` | `String.format("What it charges for a tonne's city leg, from the depot or the siding to ...` | Haulage's quote (i) (FR4). |
| 1736 | `InfrastructureScreen.TRUCKS_INFO` | `String.format("A truck carries about %,.0f tonnes a month.Haulage owns the fleet and bu...` | The trucks' (i) (FR4). |
| 1744 | `InfrastructureScreen.DEPOTS_INFO` | `String.format("A Truck Depot keeps trucks, and its posts are the drivers and the mechan...` | The depots' (i) (FR4). |
| 1751 | `InfrastructureScreen.PORTS_INFO` | `"The sea terminals are the city's: only the player builds them.Freight works them." + "...` | The ports' (i) (FR4). |
| 1758 | `InfrastructureScreen.BERTHS_INFO` | `"A berth handles one kind of cargo: liquid bulk, dry bulk, containers or " + "general c...` | The berths' (i) (FR4). |

### Kept.java ([map](map/Kept.md))

| line | constant | value | says |
|---:|---|---|---|
| 577 | `Kept.TOOLTIP` | `"javafx.scene.control.Tooltip"` | The key Tooltip.install() keeps a node's tooltip under in its properties: what tipOf() reads. |
| 579 | `Kept.ANIMATION` | `"kept.animation"` | The key play() keeps a node's last animation under in its properties, to stop it before the next. |

### KeptBench.java ([map](map/KeptBench.md))

| line | constant | value | says |
|---:|---|---|---|
| 55 | `KeptBench.WIDTH` | `1456` | The width the page is laid out at: the menu column's in a 1600-pixel window (the study's renders). |
| 63 | `KeptBench.HEADER_WIDTH` | `1600` | The window's width in KeptCheck's pictures: the header is laid out across it. |
| 66 | `KeptBench.RAIL_HEIGHT` | `900` | The window's height: the rail is laid out down it. |
| 73 | `KeptBench.PANEL_HEIGHT` | `900` | The three panels' scenes (UP2, slice 2b): the city panel's 290 wide, the construction panel's 280, the inbox list's. |
| 627 | `KeptBench.PRECISE` | `Boolean.getBoolean("keptbench.precise")` | Whether a signature writes a node's place and size to four decimals instead of one (-Dkeptbench.precise=true). |

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
| 289 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + "plot they...` | The page's (i): who pays for the ground, and who does not. |
| 298 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 515 | `LandScreen.BEST_HEAD` | `"THE BEST OFFERS · cheapest dry ground first, from every side"` | The section's head over THE GROUND: the best N, from every side. |
| 656 | `LandScreen.EMPTY_PLACE` | `"no room on this edge yet: it lists when the city grows here"` | A place waiting for room, as its row says it (spec-grid 2.5): one muted line where its offer would stand. |
| 659 | `LandScreen.ROW_DEPOSITS` | `2` | Deposits a row shows by name before "+N": 2... |
| 662 | `LandScreen.ROW_DEPOSIT_CHARS` | `20` | ...while their words run to no more than this many characters together, else one: 20 - "8 · 136 Mt" and "1 · 179 kt" and "+1" measured 152.8 px in the column's 158 at 9 px Plex Mono. |
| 795 | `LandScreen.WHOLE_FIELDS` | `" in the ground: every field of it centred in this offer, whole, paid for in its price."` | What a row's tooltip says after each resource's sites and tonnes (0.7.64, batch L): an offer holds every field centred on its ground whole, all its sites and tonnes, wherever its sites lie (CityLan... |
| 901 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expensive land ...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 947 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, a large one on twenty of one field, and each draws on th...` | The Ore card's (i). |
| 967 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " now shows up...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 980 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square metre of ground, as each month recorded" + " it - in ...` | The chart's (i). |
| 1320 | `LandScreen.MAP_W` | `MapView.SMALL_W, HERO_H = MapView.SMALL_H` | The map's width and the hero row's height (spec-land 2.8). |
| 1323 | `LandScreen.HERO_GAP` | `16` | The gap between the map and THE CITY... |
| 1326 | `LandScreen.CITY_PANEL` | `657` | ...and THE CITY's width: what the content area's 1,273 px leave at 1,389 x 868 (spec-land 2.8's 657). |
| 1329 | `LandScreen.ROW_H` | `28` | A row's height: 28 px (spec-land 2.8), six of them, their heads and THE CITY's inside HERO_H. |
| 1332 | `LandScreen.ROW_COLUMNS` | `{ 24, 74, 64, 158, 82, 78, 76, 58 }` | The rows' columns, in pixels: the place (#), size, ground and water, deposits, price, a dry km2, tag, button - with the gaps, CITY_PANEL. |
| 1335 | `LandScreen.ROW_GAP` | `4` | The gap between a row's columns. |
| 1338 | `LandScreen.ROW_HEADS` | `{ "#", "size", "dry · fresh · sea", "deposits", "price", "a dry km²", "", "" }` | The rows' column names. |
| 1578 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + " -fx-backg...` | A worth card's style, its edge's colour last. |
| 1830 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the land.On the...` | The dollar offers' (i), the 0.7.13 note word for word. |
| 2082 | `LandScreen.MAP_CARD_W` | `320` | The map's card, its width: 320 px, the pay toggle's wrap (payToggle()), so the toggle lies on one line in it. |
| 2085 | `LandScreen.MAP_CARD_PAD` | `12` | The map's card's padding, a side: the hover card's 9 px, and 3 more for the buttons' edges. |

### Levers.java ([map](map/Levers.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `Levers.EFFECTS` | `420` | How wide the effects column is held beside the ladder. |

### MapView.java ([map](map/MapView.md))

| line | constant | value | says |
|---:|---|---|---|
| 240 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 243 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 246 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 249 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 252 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron... |
| 255 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 258 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 261 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 267 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step wi... |
| 270 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 273 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 279 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map"); t.setDae...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground, the district plans (0.7.88) and their pictures (0.7.128) - and since 0.7.134 the m... |
| 444 | `MapView.MODE_PICTURE` | `0, MODE_GROUND = 1, MODE_FADE = 2` | How the globe's picture was drawn: the planet's picture, the GROUND tier, or the plane's nodes alone for the crossfade. |
| 497 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 500 | `MapView.EXPAND_TIP` | `"The map under the window's header: drag along the city's edge to pick the exact ground...` | The Expand button's tooltip (since MS1 it says the drag picks; since BL1 the ground it picks is the player's own: a drag along the city's edge picks the exact ground, one from elsewhere pans). |
| 504 | `MapView.HINT` | `"drag along the city's edge to pick the exact ground · drag elsewhere to pan"` | The expanded map's hint, at the right of its head: since 0.7.137 how to buy (spec-earth 3.1, "its hint becomes"), since MS1 by a click or a drag, since BL1 of the exact ground - blocks of the city'... |
| 507 | `MapView.KEYS_TIP` | `"Click an offer to open its card beside it, or a free block along the city's edge to pi...` | ...the hint's tooltip: every key and gesture the expanded map takes - the panning and zooming the hint said until 0.7.137, and the buying since. |
| 517 | `MapView.SELECT_EVERY` | `"Select every offer"` | The head's chip that picks every offer listed (B7). |
| 1448 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 1451 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 1454 | `MapView.EDGE_ALPHA` | `MapTiles.EDGE_ALPHA` | The city's edge's white, its alpha: 0.55, as since 0.7.61 - the far nodes' too (MapTiles.EDGE_ALPHA, 0.7.128). |
| 1457 | `MapView.WORLD_LINE_ALPHA` | `LandMap.WORLD_ALPHA` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"): the model's dial, LandMap.WORLD_ALPHA, so the line and its legend swatch are one. |
| 1765 | `MapView.DASH_ON` | `5, DASH_PERIOD = 9` | A run's dashes: 5 px on and 4 off, as the offers' edge has been drawn since 0.7.61. |
| 1826 | `MapView.PICKED_ALPHA` | `0.22` | The tint of a picked offer's ground and of picked blocks: the building pink at 0.22 (the picked offer's since 0.7.137). |
| 1883 | `MapView.NEW_TAG` | `"NEW"` | The words under an offer's number on the expanded map while it is NEW (0.7.137, board 6): the office's tag (LandScreen.isNew()), in the building pink. |
| 1950 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 1989 | `MapView.DIMMED` | `GlobePicture.colours(true)` | The ground's colours on the globe, by code - a class and its relief (ET1 slice D): the world off the city's land dimmed, as the far nodes dim it (GlobePicture.colours()). |
| 1992 | `MapView.PIECE_PX` | `MapFrame.NODE_PX` | A GROUND-tier piece past the plane, in pixels a side: 128, a far node's (MapFrame.NODE_PX) - 9 to 50 ms on the worker from 0.48 to 7.3 km a pixel (E9's probe on the full file). |
| 1995 | `MapView.RING_PX` | `6` | The city's ring on the globe, its radius in pixels: 6, the founding globe's site mark's arms (FoundingScreen, 6 px each way). |
| 1998 | `MapView.NODE_SAMPLE_PX` | `16` | The screen is sampled every this many pixels for the part of it the plane draws (globeNodes()): 16, an eighth of a node - the part's edge found to 16 px, and padded by as much. |
| 2138 | `MapView.CHART_NODE_OFFSET` | `1L<<19` | A chart node's indexes run negative west and north of the chart's anchor: this many are added to each to key it. |
| 2497 | `MapView.DRAWING_EARTH` | `"Drawing the Earth…"` | The note while the planet's picture is made, the first time an Earth map is expanded. |
| 2685 | `MapView.BLOCK_LINE_SAG_PX` | `0.25` | The most the camera may bend a block line from its chord before the line is split in two, in pixels: a quarter (the line is drawn a pixel wide, so a quarter is not seen). |
| 2688 | `MapView.BLOCK_LINE_SPLITS` | `6` | ...and how many times a line is split at most: 6 (64 pieces - far more than the 64 px a line bends by at the widest near scale 12,000 km from the city needs). |
| 2798 | `MapView.CLICK_TO_DROP` | `"Shift-click to drop it · Enter buys"` | The hover card's last line over a block that is picked already (BL1), in blue as the offers' is: how to drop it. |
| 3224 | `MapView.CLICK_TO_BUY` | `"Click to buy it here"` | The hover card's last line over an offer on the expanded map (spec-earth 3.1, board 4), in blue. |
| 3257 | `MapView.CARD_FIELDS` | `LandMap.CARD_FIELDS` | The most fields a hover card lists: 3 (LandMap.CARD_FIELDS since 0.7.135). |
| 3301 | `MapView.CARD_GAP` | `14` | The card stands off its offer's box by this many pixels: 14, as the hover card stands off the pointer (showCard()). |
| 3304 | `MapView.CARD_MARGIN` | `10` | ...and at least this far inside the view: 10, the hover card's (showCard()). |
| 3307 | `MapView.BOX_FILL_ALPHA` | `0.12` | The box's fill (B7), the offers' pink at this alpha: 0.12 (star M6) - under the lit offer's 0.34 and the picked one's 0.22 (overlays()), so the offers under the box read through it. |
| 3730 | `MapView.NATIONS_TIP` | `"A dot at each nation's largest port or city, by its output; its name, output and peopl...` | The Nations chip's tooltip (WN2). |
| 3734 | `MapView.BORDERS_TIP` | `"The nations' borders, dashed white, at the far views; up close only where one crosses ...` | ...and the Borders chip's. |
| 3737 | `MapView.TRADE_TIP` | `"This month's trade as routes from the city: violet what we sell, sand what we buy, the...` | ...and the Trade routes chip's (WN3). |
| 3741 | `MapView.TRADE_SELL_WORDS` | `"What we sell, a month", TRADE_BUY_WORDS = "What we buy"` | The legend's words for the two kinds of route (the board's, less its "it"). |
| 3744 | `MapView.TRADE_ALPHA` | `0.78` | A route's colours: the business violet for what we sell and the ore's sand for what we buy (Palette's), at the board's stroke opacity, .78. |
| 3745 | `MapView.TRADE_SELL` | `Color.web(Palette.BUSINESS, TRADE_ALPHA), TRADE_BUY = Color.web(Palette.ORE, TRADE_ALPHA)` |  |
| 3748 | `MapView.ARROW_LEN_PX` | `34` | A generated world's arrow is this many pixels long from its tip inward... |
| 3751 | `MapView.ARROW_HEAD_PX` | `9, ARROW_HEAD_WIDE_PX = 3` | ...with a head this many pixels long, and this much wider than the shaft either side. |
| 3754 | `MapView.NO_FIGURES` | `new double [ 0 ]` | A stand-in for a feed that gave no figures. |
| 3757 | `MapView.LABEL_NAME_PX` | `11` | A dot's label, the name: sans semibold, 11 px (the board's .lbl)... |
| 3760 | `MapView.LABEL_FIGURES_PX` | `9.5` | ...and the figures under it, mono, 9.5 px (the board's .lbl2). |
| 3763 | `MapView.DOT_EDGE_PX` | `1.2` | A dot's edge, in pixels: 1.2, in NationsMap.DOT_EDGE_ARGB (the board's). |
| 3766 | `MapView.HALO_PX` | `3, HALO_ALPHA = 0.75` | A label's halo, drawn under its words: 3 px wide in NationsMap.DOT_EDGE_ARGB at 0.75 (the board's .lbl stroke). |
| 3769 | `MapView.WIDTHS_MOST` | `600` | The widths of label words kept, by string (the names never change and the figures change once a year): the 600 most recently used. |
| 3772 | `MapView.NATION_CARD_W` | `330` | A nation's card is this wide, in pixels: 330 (the board's .card)... |
| 3775 | `MapView.NATION_CARD_GAP` | `4` | ...and its head, its distance line and its rows stand this many pixels apart: 4. |
| 4710 | `MapView.LEGEND_ROWS` | `17` | Rows a column of the legend holds: 17, its 52 entries in three columns and the first row of a fourth, and the zones set after them (since 0.8.2 the train, 51 in three columns before; since 0.8.1 th... |
| 4713 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 4716 | `MapView.TRUCK_WORDS` | `"Trucks: one per %s"` | The legend's line for the trucks (0.8.8, batch FR5; spec-freight 4.7): "Trucks: one per 12 loads", the unit's words filled in by trucksWords(). |
| 4724 | `MapView.TRACK_ENTRY` | `LandMap.TRACK_ENTRY` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for - the model's words since 0.7.135. |
| 4742 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |
| 4797 | `MapView.LEGEND_FOLD_WORDS` | `"· click to fold"` | The head line's words when the legend is open, after its chevron and "Legend" (the board's .lh). |

### MiniMap.java ([map](map/MiniMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `MiniMap.FRAME_MS` | `MapView.FRAME_MS` | The most a frame spends drawing the stamp and the preview, in ms: MapView.FRAME_MS - a node at least, so each finishes. |
| 61 | `MiniMap.STAMP_EVERY_MS` | `250` | The least a drawing waits between beginning one picture and the next, in ms: the stamp drawn at most four times a second, against ten at 50x (a month every 100 ms), so a highway laid shows within a... |
| 64 | `MiniMap.PREVIEW_GAP` | `6` | The gap between the rail's edge and the preview: 6 px (the mockup's). |
| 67 | `MiniMap.PREVIEW_WORDS` | `"Click for the full map"` | The preview's line under the picture (the mockup's). |
| 70 | `MiniMap.STAMP_WORD` | `"Map"` | The stamp's word, under it as each rail button's is. |

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
| 111 | `PeopleScreen.OUT_OF_WORK_SHORT` | `.03` | Under this share out of work, nobody is spare: amber, "jobs going unfilled". |
| 114 | `PeopleScreen.OUT_OF_WORK_HIGH` | `.15` | Over this, high: amber. |
| 117 | `PeopleScreen.OUT_OF_WORK_FAR` | `.25` | Over this, far too many adults with nothing to do: red. |
| 127 | `PeopleScreen.GOING_SHORT_WARN` | `.01` | Over this share of the city eating less than a basket, GOING SHORT is amber; the dashboard's HUNGRY reads it too (0.7.27). |
| 130 | `PeopleScreen.GOING_SHORT_BAD` | `.10` | ...and over this, red. |
| 241 | `PeopleScreen.COUNT_MILLIS` | `600` | How long the headcount counts and the month's bars grow when a month lands (Jerus's "fun": the number moves). |
| 265 | `PeopleScreen.STILL_FAKED` | `"What this model does not do yet:\n" + "· Nobody has an age.Each band holds a mean, so ...` | What the model does not do yet: the four lines of the old page's eight that are still true (0.7.27). |
| 504 | `PeopleScreen.BORN` | `Palette.PEOPLE_LIGHT, MOVED_IN = Palette.PEOPLE` | The born and the arrived: the people teal's light step and the teal. |
| 507 | `PeopleScreen.DIED_OF_AGE` | `Palette.PEOPLE_DARK, DIED_OF_ILLNESS = Palette.BUSINESS, DIED_KILLED = Palette.BUILDING...` | The dead by cause: of age the dark teal, of illness the violet, the killed the pink, the aged out a light blue. |
| 511 | `PeopleScreen.LEFT_WORK` | `Palette.PEOPLE_DARK, LEFT_BROKE = Palette.MONEY, LEFT_CRIME = Palette.BUILDING` | The leavers by why: for want of work the dark teal, broke the blue, driven out by crime the pink. |
| 899 | `PeopleScreen.CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + " -fx-backg...` | The Build card ground every card on both pages stands on. |
| 1037 | `PeopleScreen.PYRAMID_HALF` | `200` | How wide half of the widest band is drawn, at most. |
| 1370 | `PeopleScreen.WHY_INFO` | `"The city draws people the way Migration strikes it each month: every post " + "support...` | WHY PEOPLE COME's (i): what the bridge is. |
| 1747 | `PeopleScreen.TILE_MIN` | `56` | The narrowest a tile is drawn; anything smaller folds into "+n more". |
| 1842 | `PeopleScreen.Strip.UNUSED` | `" "` | What the "+n more" label says while nothing folds: a space, never nothing (PT1-M4). |
| 1913 | `PeopleScreen.Strip.TILE_TALL` | `62` | How tall a strip is. |
| 2097 | `PeopleScreen.OUTSIDE_INFO` | `"Families are built from the adults who work, and their children." + "Everybody else is...` | OUTSIDE THE FAMILIES' (i): who they are, the old page's words. |
| 2347 | `PeopleScreen.LADDER_INFO` | `"open = posts this band can be put into(licensed posts are on the " + "chips under the ...` | The ladder's (i): the old legend. |
| 2834 | `PeopleScreen.IN_DOLLARS` | `"Every figure on this page is in dollars, not the thousands the rest of the " + "game c...` | The page's (i): what its money is counted in. |
| 2839 | `PeopleScreen.PAID_IN_ORDER` | `"The order is fixed: the payslip first, then rent, then the fees, and " + "the shop tak...` | The order a household pays in, and what happens when it comes up short: the verdict's (i). |
| 3043 | `PeopleScreen.BESIDE` | `1200` | The narrowest page that sets the open cell's books beside the grid; under it they stack. |
| 3364 | `PeopleScreen.CARS_INFO` | `"Bought out of savings past the same cushion a share is, so income " + "decides who can...` | The cars' (i): the old note. |
| 3434 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 3435 | `PeopleScreen.SHAPE_COL` | `168` |  |
| 3566 | `PeopleScreen.BELOW_THE_RULE` | `"Below the rule: the retired, on a pension and no wage; the out of work, " + "on EI for...` | The section captions' (i): who is below the rule, and what their one cell says. |

### Pieces.java ([map](map/Pieces.md))

| line | constant | value | says |
|---:|---|---|---|
| 122 | `Pieces.LIMIT_CELL` | `190` | How wide a limit cell is, its padding included; its note wraps inside it. |
| 776 | `Pieces.POPOVER_WIDTH` | `360` | How wide a popover's text wraps. |
| 779 | `Pieces.MORE_IN_THE_MANUAL` | `"More in the manual"` | The line a popover may end with: the third layer's door, with no link until the manual has one. |
| 823 | `Pieces.INFO_SIZE` | `16` | How big the (i) is drawn: its 24-unit grid at this many pixels. |
| 929 | `Pieces.TILE_GAP` | `10` | The gap between cards in a row or a grid: Build's cards in their flow, the land office's shelf. |
| 1097 | `Pieces.PAGE_WIDE` | `1480` | The widest a redrawn page is laid out - Build's Overview and categories since 0.7.24, the land office since 0.7.26 - so a 1,920 window does not stretch a row of cards across the glass. |
| 1853 | `Pieces.Waterfall.FIGURE_ROOM` | `16, NAME_ROOM = 12, ICON = 13` | The figures' line over the plot, the least the names under it take, and an icon's size. |
| 2149 | `Pieces.CAUSE_LABEL_ROOM` | `70` | How wide a part must be drawn to carry its name and figure under it; a narrower one is keyed. |
| 2175 | `Pieces.CauseBar.GAP` | `2, UNDER = 3, KEY_GAP = 12, KEY_ROW = 3` | Between the parts, under the bar, and between the key's entries and rows. |
| 2453 | `Pieces.EffectScale.TALL` | `44, Y = 22, DOT = 10` | Its height, how far down it the line runs, and the mark's size. |
| 2529 | `Pieces.CohortBars.AXIS` | `15` | Room under the bars for the near and far names. |
| 2790 | `Pieces.ScaleRows.ROW` | `26, GAP = 10, RULE_NAMES = 16, TAG_GAP = 6, ICON = 12` | A row's least height, the gap either side of the bar, the room the rules' names take over the rows, the gap before a tag, and an icon's size. |
| 3279 | `Pieces.EVERYTHING_ELSE` | `"Everything else"` | What topSlices() calls the slices past its ramp, folded into one. |
| 3426 | `Pieces.RANK_BAND` | `12, RANK_ROW = 36` | How tall a ranked bar's band is, and the least a row takes. |
| 3429 | `Pieces.RANK_KEY` | `"rankBars.key"` | The property a ranked bar's row carries its line's key under, so a screen can find the row to scroll to. |
| 3590 | `Pieces.BRIDGE_TILE` | `200, BRIDGE_BAR = 56, BRIDGE_FIGURE = 64` | A bridge's tiles' width, its step bars' and its figures' (0.7.31). |
| 3593 | `Pieces.BRIDGE_NOTHING` | `.5` | A step under this, in the model's thousands, is nothing: half a thousand, below which signedTight() writes "$0". |
| 3812 | `Pieces.Columns.TOP` | `34, FOOT = 30, GAP = 8` | The room over the plot for a tag and a figure, under it for a label and its second line, and the gap between columns. |
| 4048 | `Pieces.BAND_SCALE` | `1.6` | How far past the top of its band a ratio's bar runs, as a multiple of the top: the band sits in the left of it, so a ratio well over its band reads as full (BankScreen's capital band's since 0.7.9). |
| 4113 | `Pieces.RUNG_ROW` | `30, RUNG_BAND = 12` | A rung's least height, and its bar's band. |
| 4302 | `Pieces.ACTION_TALL` | `40` | An action button's height on a card, where it is the thing the card is for. |
| 4305 | `Pieces.ACTION_INLINE` | `32` | ...beside a heading, where it shares a row with words: Build's "Build all three", the land office's "Buy the next 5". |
| 4308 | `Pieces.DOOR_TALL` | `30` | A door pill's height. |
| 4474 | `Pieces.DOOR_ACCENT` | `"pieces.doorAccent"` | The key under which a door pill carries its accent, for the listeners added once to read. |
| 4770 | `Pieces.MirrorRows.ROW` | `28, BAR = 12, ICON = 18, GAP = 8` | A row's least height, its bar's thickness, an icon's size and the gaps between the columns. |
| 4995 | `Pieces.DivergingBars.ROW` | `30, BAR = 12` | A row's least height and its bar's thickness. |
| 5365 | `Pieces.RangeBar.TRACK` | `6, DOT = 10, TICK_W = 5, TICK_H = 14, PAD = 6` | The track's height, the dot's size, the tick's width and height, and the room either end so the dot is never cut. |
| 5517 | `Pieces.HUNGER_INFO` | `"The measure is struck at the top of the month on the last sale: what each " + "househo...` | The (i): when the measure is struck, and what each half means. |

### PolicyScreen.java ([map](map/PolicyScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `PolicyScreen.TAXES` | `"Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises", ZONING = "Zoning"` | The four areas, by what kind of lever each is - Jerus picked the grouping - and since 0.7.109 (batch Z2) the fifth, the city's ground: Zoning (ZoningScreen draws it). |
| 115 | `PolicyScreen.AREAS` | `{ TAXES, WAGES, MONEY, PROMISES, ZONING }` | ...as the head's chips list them. |
| 118 | `PolicyScreen.AREA_ICONS` | `{ Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION, Icons.ZONING }` | ...and each one's icon: the coin, the staff, the bank, the people, the flag on a plot. |
| 123 | `PolicyScreen.POLICY_HOME` | `"Everything"` | The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. |
| 140 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property", "Fuel", "Port dues" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 142 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 144 | `PolicyScreen.CURRENCY_PAGE` | `"The currency"` | Money's middle page (XR2): how the D$ is held - its regime, the peg or the band, who pays at the strong edge, what the dial aims at. |
| 145 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", CURRENCY_PAGE, "Currency reform" }` |  |
| 146 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Food", "Health", "Schools", "Subsidies" }` |  |
| 149 | `PolicyScreen.TAX_ICONS` | `{ Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES, Icons.DROP, Icon...` | Each page's icon on its tab, in the pages' order (the crane for the port dues, FR4). |
| 151 | `PolicyScreen.WAGE_ICONS` | `{ Icons.STAFF }` | ...Wages' one, the staff. |
| 153 | `PolicyScreen.MONEY_ICONS` | `{ Icons.BANK, Icons.EXCHANGE, Icons.BANKNOTE }` | ...Money's: the bank for the policy rate, the two arrows for the currency (XR2), the banknote for the currency reform. |
| 155 | `PolicyScreen.PROMISE_ICONS` | `{ Icons.CANE, Icons.STAFF, Icons.FOOD, Icons.HEALTH, Icons.EDUCATION, Icons.SECTOR }` | ...and the promises': the cane, the staff, food, health, education and the sector icon for the subsidies. |
| 167 | `PolicyScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. |
| 170 | `PolicyScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). |
| 173 | `PolicyScreen.TRAY_CHROME` | `90` | How tall the staged tray is taken to be until it is laid out. |
| 315 | `PolicyScreen.LEAD_INFO` | `"Every number the city sets for itself, and what each one is doing this month." + "Ever...` | The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). |
| 510 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 513 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 516 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 681 | `PolicyScreen.BUDGET_INFO` | `"Last month's surplus or deficit, and what it would be with this change: every " + "lin...` | THE BUDGET's (i). |
| 690 | `PolicyScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press.THE BUDGET is " + "struc...` | P20, the tray's (i). |
| 1062 | `PolicyScreen.NOTHING_BINDING_INFO` | `"Nothing is binding.Every lever has room to move, no sector is shut " + "out of credit,...` | P2: what the hub says when nothing is binding. |
| 1108 | `PolicyScreen.TAX_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST }` | The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. |
| 1117 | `PolicyScreen.TAX_NAMES` | `{ "Profit", "Sales", "Wage", "Property" }` | The four taxes by name, profit to property: their cards, the bar's parts and their pages. |
| 1452 | `PolicyScreen.CAVEAT` | `"Nothing here knows that the new rate changes what anybody does next month - a " + "bus...` | What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. |
| 1456 | `PolicyScreen.CAVEAT_LINE` | `"Struck on this month's books; behaviour is not projected."` | The caveat's line on a card. |
| 1467 | `PolicyScreen.TAKE_INFO` | `"Last month, by tax.Each has a rate of its own, and three of the four a " + "move off i...` | P3, THE TAX TAKE's (i). |
| 1563 | `PolicyScreen.EVERY_TAX_INFO` | `"Moves all three rates to one number.A sector or wage band you've set apart " + "keeps ...` | P5: every tax at once, its (i). |
| 1571 | `PolicyScreen.FUEL_DUTY` | `"fuelDuty"` | The staged key of the fuel duty dial (0.7.103, batch P4; Jerus's A7). |
| 1574 | `PolicyScreen.FUEL_DUTY_STEP` | `.00001` | One step of the fuel duty's ladder: a cent a litre at founding prices, in thousands. |
| 1577 | `PolicyScreen.FUEL_INFO` | `"A duty a litre on the petrol the drivers buy at the pump, paid to the treasury as a li...` | The fuel duty's (i). |
| 1584 | `PolicyScreen.FUEL_CAVEAT` | `"Struck on last month's litres at the pump: the owners who would take the bus at a" + "...` | The fuel duty's caveat, whole. |
| 1628 | `PolicyScreen.PORT_DUES_PAGE` | `"Port dues"` | The Taxes area's page of the port dues dial (FR4; the dial and its collection are FR3's, TaxPolicy.getPortDues() and sectors.PortDivision). |
| 1631 | `PolicyScreen.PORT_DUES` | `"portDues"` | The staged key of the port dues dial (FR4). |
| 1634 | `PolicyScreen.PORT_DUES_STEP` | `TaxPolicy.MAX_PORT_DUES / 40` | One step of the port dues' ladder: a fortieth of the dial's top, so the default (1) and the top are both on a step. |
| 1648 | `PolicyScreen.PORT_DUES_CAVEAT` | `"Struck on last month's tonnes across the berths.The handling charge carries a dearer d...` | The port dues' caveat, whole. |
| 1702 | `PolicyScreen.PROPERTY_NOT` | `"Property is not on this dial: it is charged on value rather than on income, " + "and t...` | P6. |
| 1751 | `PolicyScreen.PROFIT_INFO` | `"Charged on what each sector earned before tax.A sector that lost money " + "pays nothi...` | P8, the profit tax's head. |
| 1756 | `PolicyScreen.LOSS_INFO` | `"A sector that lost money paid no profit tax at all, which is why the " + "first line f...` | P4. |
| 1760 | `PolicyScreen.SALES_INFO` | `"Charged on VALUE ADDED - what a sector sells, less the tax it already " + "paid on wha...` | P10, the sales tax's head. |
| 1766 | `PolicyScreen.WAGE_INFO` | `"Taken off every payroll in the city before the household sees it.The " + "jobs are gro...` | P12, the wage tax's head. |
| 1771 | `PolicyScreen.PROPERTY_INFO` | `"Charged on what land and buildings are assessed at, whether or not the " + "owner earn...` | P14, the property tax's head. |
| 1776 | `PolicyScreen.PROPERTY_STEP_INFO` | `"A twentieth of a point a step rather than the quarter the other three " + "get, becaus...` | P15. |
| 1781 | `PolicyScreen.REFUND_INFO` | `"\"In refund\" means a sector's credits on what it bought exceed the tax " + "on what i...` | P11. |
| 1787 | `PolicyScreen.ROLL_INFO` | `"The power and water plants are the city's own and exempt.Raising what " + "land sells ...` | P16. |
| 1792 | `PolicyScreen.SALES_SCALED` | `"Scaled, not recomputed: each sector's net remittance moves by the ratio of its " + "ne...` | The sales tax's preview, scaled (the spec's D11, B6). |
| 1829 | `PolicyScreen.OFFSET_INFO` | `"A move is in POINTS off the tax's own rate, so a row left at zero is taxed " + "at exa...` | P9, the offsets' note. |
| 1899 | `PolicyScreen.BANK_INFO` | `"The commercial bank's profit is taxed at Retail's rate - a Commercial Bank is a " + "c...` | The bank's line (B5, D12). |
| 2141 | `PolicyScreen.PAYSLIP_INFO` | `"Only the first is a tax and only the first is set here - the other three " + "are prom...` | P13. |
| 2172 | `PolicyScreen.FARMLAND_INFO` | `"A field is worth what a developer would pay for it and grows what a " + "farmer can gr...` | P17. |
| 2179 | `PolicyScreen.FARMLAND_CAVEAT` | `"Exact against today's land price.What it actually decides is whether " + "the next fie...` | P19. |
| 2326 | `PolicyScreen.FLOOR_INFO` | `"Every wage in the city is a multiple of this number, so moving it moves " + "all of th...` | P22, the floor's (i). |
| 2332 | `PolicyScreen.PINNED_WORDS` | `"At least one skill level is oversupplied AND cannot get any cheaper, so " + "those wor...` | P23, both forms. |
| 2337 | `PolicyScreen.FREE_WORDS` | `"No skill level is pinned against the floor, so the labour market is " + "clearing on p...` | ...the other: no band pinned. |
| 2342 | `PolicyScreen.FLOOR_CAVEAT` | `"Once wages have walked there, which takes about a year.Nothing about " + "this preview...` | P24. |
| 2490 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2493 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2496 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2499 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2502 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2505 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2531 | `PolicyScreen.OVER_INFO` | `"Hot money follows this difference in, and leaves the day it closes." + "It is also wha...` | P28. |
| 2537 | `PolicyScreen.REAL_INFO` | `"The currency follows this one: a dial under the inflation people expect is a " + "real...` | P28's second half: the real differential - against the inflation people expect since 0.7.42 (the UI spec's B9). |
| 2543 | `PolicyScreen.REAL_DIAL_INFO` | `"The dial less the inflation people expect: what saving at the dial earns " + "in what ...` | The dial's own real rate (0.7.45): REAL_INFO's first half, for the dial alone. |
| 2697 | `PolicyScreen.HAND_INFO` | `"Jerus's autopilot: the rule can hold the dial.Every month, before anything is " + "pri...` | P32. |
| 2701 | `PolicyScreen.REPRICE_INFO` | `"This does not reprice a single bond the city has already sold - every " + "coupon on t...` | P34: the dial card's caveat. |
| 2958 | `PolicyScreen.QE_LINE` | `"Buys term paper only: it reaches prices through what people own and through the " + "c...` | How QE reaches prices, in one plain line on the holdings card (0.7.45; the UI spec's D20). |
| 2991 | `PolicyScreen.CEILING_INFO` | `String.format("When the treasury runs dry the central bank advances the gap in money it...` | P36. |
| 3038 | `PolicyScreen.ANCHOR_INFO` | `"What people expect prices to do decides what they do next.Wages are asked " + "for hal...` | THE ANCHOR's (i): Expectations, in a player's words. |
| 3049 | `PolicyScreen.STRUCK_INFO` | `"Fees, build costs and upkeep, land, the shelf's floor, the pension's wage base, " + "t...` | The struck level's (i). |
| 3054 | `PolicyScreen.GAP_INFO` | `"A real rate over the world's brings money here and holds the currency dearer than " + ...` | THE CURRENCY'S LEVEL's (i). |
| 3192 | `PolicyScreen.BASKET_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST, Palette.TE...` | The basket's five parts' colours, groceries to services: the money ramp, then the rest - areas, not verdicts. |
| 3238 | `PolicyScreen.SWING_INFO` | `"Prices here have more than doubled and come back at some point." + "Wages, rents and e...` | P30. |
| 3243 | `PolicyScreen.REFORMED_INFO` | `"The index is measured against the city's first basket in founding money, " + "chained ...` | P31, once the money has been reformed. |
| 3331 | `PolicyScreen.XR_REGIME` | `"xr:regime", XR_PAYER = "xr:payer", XR_AIM = "xr:aim", XR_PEG = "xr:peg", XR_CENTRE = "...` | The staged keys of the currency page: the regime, who pays at the strong edge and what the dial aims at by ordinal; the peg; the band's centre, slope and width; the spread over the US's rate under ... |
| 3335 | `PolicyScreen.PEG_STEP` | `.01` | One step of the peg's chips: a hundredth of the peg, up to devalue and down to revalue. |
| 3338 | `PolicyScreen.CENTRE_STEP` | `.01` | One step of the band's centre: a hundredth of it. |
| 3341 | `PolicyScreen.SLOPE_STEP` | `.005, WIDTH_STEP =.005` | One step of the band's slope and of its width: half a point. |
| 3344 | `PolicyScreen.SPREAD_STEP` | `.0025` | One step of the spread over the US's rate: a quarter of a point. |
| 3347 | `PolicyScreen.CURRENCY_CHART_MONTHS` | `60` | The months the currency page's charts show: five years. |
| 3350 | `PolicyScreen.COVER_CHART_TOP` | `12` | The most months of import cover the vault's chart draws: a year. |
| 3353 | `PolicyScreen.DEAL_SHARE` | `.01` | The size of a deal the BUY OR SELL card weighs, a share of a year's output. |
| 4221 | `PolicyScreen.COVER_INFO` | `"The rest is general revenue - the same pot the schools and the hospitals " + "come out...` | P41 and P47. |
| 4390 | `PolicyScreen.EI_INFO` | `"EI only pays the first twelve months, so a long bust costs less in EI than " + "a shor...` | P57. |
| 4833 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 4836 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 4876 | `PolicyScreen.SHARE_INFO` | `"The city's share of every course fee.Households pay the rest out of a " + "month's wag...` | P48. |
| 4883 | `PolicyScreen.BURDEN_INFO` | `String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is a " + "co...` | P49. |
| 4889 | `PolicyScreen.PRICE_INFO` | `"The founding tuition table times this, before the city's share comes off." + "The tabl...` | P50. |
| 4897 | `PolicyScreen.SCHOOL_CAVEAT` | `"Against the courses being taken now, each kind's students at its own " + "price - and ...` | P51/P52: the schools' preview caveat. |
| 5248 | `PolicyScreen.LOAN_INFO` | `"Charged on a graduate's balance while they repay it, and on nothing while " + "they st...` | P55. |
| 5254 | `PolicyScreen.LOAN_CAVEAT` | `"Against the balances the graduates owe today.The instalment itself does " + "not chang...` | P56. |
| 5299 | `PolicyScreen.SUBSIDY_INFO` | `"A protected sector is topped up to break-even every month it loses money, " + "so it n...` | P69. |

### SectorScreen.java ([map](map/SectorScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 91 | `SectorScreen.SECTOR_HOME` | `"Operations"` | The page a business opens on, and falls back to for a page it does not know. |
| 98 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` | The five pages, in the strip's order: the names other screens open a business's books by. |
| 102 | `SectorScreen.PAGE_ICONS` | `{ Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR }` | Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. |
| 114 | `SectorScreen.FRAME_CHROME` | `276` | How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own... |
| 117 | `SectorScreen.LIST_CHROME` | `190` | ...and the list's: its head and its four figures (0.7.30). |
| 120 | `SectorScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page ... |
| 123 | `SectorScreen.COUNT_MILLIS` | `400` | How long a figure takes to count from last month's to this month's (0.7.30). |
| 126 | `SectorScreen.SWEEP_MILLIS` | `700` | ...and the plant's ring to sweep up from nothing (0.7.30). |
| 196 | `SectorScreen.LIST_INFO` | `"What the city's businesses kept this month, after tax - each card one business, " + "w...` | What the list's (i) holds: the old caption, and what a card does. |
| 203 | `SectorScreen.NOTHING_YET_LIST` | `"The sector books are written when a month closes.This city has not " + "closed one sin...` | The empty books' sentence (the old list's alert). |
| 207 | `SectorScreen.NOTHING_YET_SECTOR` | `"This city has not closed a month since it was loaded, so there is no " + "statement to...` | ...and a business's (the old page's alert). |
| 211 | `SectorScreen.NO_WORD_YET_LIST` | `"Nothing recorded since the city was loaded or founded: a month on, each " + "card says...` | The list's one line while no business has a word for the month (0.7.34), in place of each card's own. |
| 383 | `SectorScreen.RUNNING_INFO` | `"How much of what its plants could make each business made this month: its " + "posts f...` | RUNNING AT's (i). |
| 507 | `SectorScreen.SPARK_WIDTH` | `120` | The width the sparkline takes on a card (0.7.30; 90 on the old list). |
| 510 | `SectorScreen.SPARK_HEIGHT` | `28` | ...and its height (0.7.30; 22 on the old list). |
| 513 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |
| 1377 | `SectorScreen.SHIPPER_COLUMN` | `80` | The width of one figure column of the shippers table. |
| 1380 | `SectorScreen.SHIPPER_HEADS` | `{ ham.citybuildersim.sectors.Haulage.BILLED_AS, FreightView.RAIL_BILLED_AS, ham.citybui...` | The shippers table's column heads: the lines the bills are raised under, then the sum. |
| 1695 | `SectorScreen.OPERATING_INFO` | `"What the business made from trading, after its sales tax and the ground it " + "stands...` | The operating line's (i): what it is, and the model's own operating income, which is before the ground and the sales tax (D4, D5). |
| 1699 | `SectorScreen.REFUND_INFO` | `"The sales tax line is a REFUND this month: the credit on what this sector " + "bought ...` | The sales tax refund's (i). |
| 1704 | `SectorScreen.FALL_INFO` | `"Revenue at the left, less the sales tax it remitted and what it bought, to its " + "gr...` | The waterfall's (i). |
| 1994 | `SectorScreen.OWNERS_DOOR` | `"its owners, its share's price and what it pays: Investors"` | The Balance sheet's door to its owners' card (D7). |
| 2759 | `SectorScreen.INCOME` | `"income", SHEET = "sheet", EQUITY = "equity", CASH = "cash"` | Each statement's key in a business's open set: an open note on one is "income:note:5" in linesOpen(). |
| 2762 | `SectorScreen.OUTSIDE_INFO` | `"Money that reached its books outside its trading: the city's subsidy and the " + "arre...` | Note 6, and the equity statement's outside line: what the outside lines are (F1). |
| 2771 | `SectorScreen.DERIVED_INFO` | `"Derived when the city was loaded: its save was made before share capital was " + "kept...` | Share capital's line and note, when a save from before 0.7.75 was loaded (R3). |
| 2777 | `SectorScreen.CAPITAL_INFO` | `"What its owners put in: the book its founders' shares were issued against, and " + "ev...` | Share capital's line and note (R3). |
| 2782 | `SectorScreen.STOCK_WORDS` | `"Stock is held at what it would fetch today, so a price collapse shrinks this " + "busi...` | Note 7: the stock. |
| 2786 | `SectorScreen.LAND_WORDS` | `"Its land at today's prices, by the ground it stands on: a share of the city's flat " +...` | Note 11 (ET3, spec-earth2-terrain 5.3): its land by the ground it stands on. |
| 2804 | `SectorScreen.ABROAD_WORDS` | `"What it has sent abroad for the world's rate, in the city's money at the rate it " + "...` | Note 9: what it holds abroad. |
| 2809 | `SectorScreen.DUE_WORDS` | `"What its loans, bonds and mortgages ask at the twelve settles ahead: a loan whole in "...` | What falls due within a year (R2), behind its line's and its card's (i). |
| 2814 | `SectorScreen.NOT_SPLIT_WORDS` | `"When it falls due is counted at the next month's books: a load keeps the debt, " + "no...` | The month after a load, R2's line. |
| 2946 | `SectorScreen.EQUITY_HEADS` | `{ "share capital", "kept, revalued", "total" }` | The equity statement's columns (R3). |
| 2949 | `SectorScreen.REVALUED_INFO` | `"Whatever else moved its equity, worked out as what is left: a building bought " + "for...` | The equity statement's remainder's (i). |
| 3014 | `SectorScreen.SCHEDULE_RATE` | `70, SCHEDULE_RUNS = 80` | The debt schedule's columns (R7): the kind (DEBT_KIND), five figures (DEBT_FIGURE), the rate and when the last of it falls due. |
| 3017 | `SectorScreen.SCHEDULE_INFO` | `"What it owed of each kind at last month's sheet, what it borrowed, repaid and had " + ...` | The schedule's (i). |
| 3129 | `SectorScreen.DEBT_KIND` | `130, DEBT_FIGURE = 100` | The debt card's columns: the kind, then owed, within a year, in one to five, after five, and the month's interest. |
| 3218 | `SectorScreen.EVERY_GATE_INFO` | `"Each building it can put up at every gate investors ask - ore, a licence, " + "staff, ...` | EVERY BUILDING AT EVERY GATE's (i): since 0.7.122 the last gate the interest on its whole cost, consider()'s brake (batch C1). |
| 3229 | `SectorScreen.GATE_NAME` | `200, GATE_MARK = 44, GATE_FIGURE = 96, GATE_PAYBACK = 84, GATE_PAYS = 210` | The grid's columns: the building, a gate's mark, a figure, the payback, how it would pay. |
| 3232 | `SectorScreen.GATE_HEADS` | `{ "ore", "licence", "staff", "land", "pays", "interest", "lender" }` | The gates' heads, in BuildCard.GateKind's order: the sixth, since 0.7.122, the interest on its whole cost, and the seventh (I13-L) the mortgage lender's test - a column only where a building of the... |
| 3405 | `SectorScreen.SHARE_ROWS` | `{ { "Shares", "n" }, { "Earnings a share, the month", "$" }, { "Dividend a share, the m...` | The report's rows: {label, kind} - "n" a count, "$" money a share, "%" a share, "x" times, "M" money. |
| 3449 | `SectorScreen.SHARE_LABEL` | `220, SHARE_FIGURE = 120` | The report's columns. |
| 3563 | `SectorScreen.CONTROL_INFO` | `"None of this is yours to set.These are private companies deciding for " + "themselves ...` | WHAT YOU CONTROL's (i): the old page's last paragraph. |
| 3568 | `SectorScreen.WAITING_INFO` | `"This business wants to build and there is nowhere to put it.It is the one " + "refusal...` | The waiting-on-ground alert's (i). |
| 3920 | `SectorScreen.FLOW_INFO` | `"Each row's money is the month the books closed on - the Income page's - and its " + "u...` | The flow's money and units: which month each is (SectorFlow's two months). |
| 3993 | `SectorScreen.TONNES_INFO` | `"How this business's freight went in the month billed, good by good.Tonnes it " + "boug...` | What the strip's (i) says. |
| 4003 | `SectorScreen.PELLET_INFO` | `"The Pelletising Plant's city legs at nameplate - every tonne of the bulk goods it uses...` | The siding line's (i) is its own sentence; the plant card's. |
| 4010 | `SectorScreen.MOVE_NAME` | `150` | The width of a line's name column. |
| 4282 | `SectorScreen.REFINERY_HEAD` | `"THE REFINERY THIS MONTH"` | The card's heading, its line, and what its (i) says. |
| 4284 | `SectorScreen.REFINERY_SUB` | `"where the crude went · ribbons to scale"` | ...the line beside the heading... |
| 4286 | `SectorScreen.REFINERY_INFO` | `"The month's crude comes in on the left, from the wells, the reserve or the world." + "...` | ...and the (i)'s words: the picture read from left to right. |
| 4492 | `SectorScreen.HATCHES` | `new java.util.HashMap<>()` | The hatchings made so far, by colour and opacity (hatch()'s cache). |
| 4589 | `SectorScreen.INDUSTRY_INFO` | `"On the left is what the month worked: a mine's fields, each a vessel filled to " + "wh...` | What the materials card's (i) says: the picture read from left to right. |
| 4773 | `SectorScreen.OIL_WELLS_INFO` | `"The city's wells by kind.A land well lifts the ground pool - the oil under the city's ...` | What the oil industry's (i)s say. |
| 4779 | `SectorScreen.OIL_UNITS_INFO` | `"Every kind of refinery unit.Its spread is what it makes of a litre of its feed, less" ...` | ...THE REFINERY's, its units' table... |
| 4784 | `SectorScreen.OIL_PRODUCTS_INFO` | `"Every product of the refinery, and crude: its price here and the world's, the" + " wor...` | ...PRODUCTS', crude's and the nine products' table... |
| 4788 | `SectorScreen.OIL_RESERVE_INFO` | `"The city's own crude, in its Strategic Reserves' tanks.Fill orders crude for the next"...` | ...and THE STRATEGIC RESERVE's, which the Fill and Release cards' (i)s say too. |
| 4794 | `SectorScreen.RESERVE_CAVEAT` | `"bought at the next clearing at what crude then costs; what the room cannot take lapses"` | The Fill card's caveat under its effects... |
| 4796 | `SectorScreen.RESERVE_CAVEAT_INFO` | `"A fill is an order for the next clearing: the wells' crude pro rata with the" + " refi...` | ...and what its (i) says: an order for the next clearing, paid at the next strike. |
| 4800 | `SectorScreen.RELEASE_CAVEAT` | `"offered to the refiners; what they do not take stays in the tanks"` | The Release card's caveat; its (i) is OIL_RESERVE_INFO. |
| 4803 | `SectorScreen.RESERVE_LADDER` | `380` | The lever's ladder width on its dial card. |
| 5213 | `SectorScreen.FREIGHT_STRIP_INFO` | `"Freight is three businesses in one set of books: Haulage's trucks, the " + "railway an...` | What the division strip's (i) says. |
| 5219 | `SectorScreen.FREIGHT_LEGS_INFO` | `"Every tonne that crossed the city's boundary went the long haul by the " + "world's lo...` | The legs card's (i). |
| 5227 | `SectorScreen.FREIGHT_SHIPPERS_INFO` | `"The sectors billed by this division this month, largest bill first: the " + "line each...` | The shippers card's (i). |
| 5405 | `SectorScreen.LEG_NAME` | `120` | The width of a leg's name column. |
| 5430 | `SectorScreen.LegBar.BAND` | `26` | The bar's height, in pixels: 26. |
| 5541 | `SectorScreen.FREIGHT_TRUCKS_INFO` | `"The trucks Haulage owns against the trucks the plant standing asks for to " + "carry t...` | Haulage's fleet card's (i). |
| 5546 | `SectorScreen.FREIGHT_HAULAGE_QUOTE_INFO` | `"What a tonne's city leg costs the shipper.The rule: Haulage's running " + "cost plus a...` | Haulage's price card's (i). |
| 5683 | `SectorScreen.FREIGHT_RAIL_INFO` | `"The wagon sets the railway owns against the sets its track standing needs." + "The tra...` | The railway's fleet card's (i). |
| 5690 | `SectorScreen.FREIGHT_RAIL_QUOTE_INFO` | `"What the railway charges a tonne, as a share of what a lorry charges the " + "same ton...` | The railway's price card's (i). |
| 5717 | `SectorScreen.FREIGHT_PORTS_INFO` | `"The terminals standing by kind, the tonnes each kind handled this month " + "against w...` | The berths card's (i). |
| 5851 | `SectorScreen.SHELF_INFO` | `"A basket is one person's groceries for a month.The shelf moves a sixth of the way " + ...` | THE SHELF's (i). |
| 6007 | `SectorScreen.FOLD_PAST` | `6` | The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). |

### ServicesScreen.java ([map](map/ServicesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `ServicesScreen.OVERVIEW` | `"Overview", BOOKS = "Books"` | The page every system opens on (0.7.28), and the last chip of each, its books. |
| 107 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 110 | `ServicesScreen.SERVICE_HOME` | `OVERVIEW` | ...and every system opens on its Overview (0.7.28; General care until then). |
| 136 | `ServicesScreen.FRAME_CHROME` | `268` | How much of the stage the fixed frame takes above the page's scroller: the head, the systems, the four figures with their sparklines and changes, the pages, and their gaps. |
| 809 | `ServicesScreen.SICK_INFO` | `String.format("The bar is today's sick rate split into what makes it, in points of the ...` | The sick rate's (i): what the bar is, its floor and its cap, and why it is a cost of output and not of wages. |
| 1069 | `ServicesScreen.BURIAL_INFO` | `"A household that can save a plot's price over ten years chooses burial; " + "the ones ...` | The burial choice (the death care page's note). |
| 1175 | `ServicesScreen.LONG_SICK_INFO` | `String.format("Everybody sick this month, by how long they have been ill: the left bar ...` | The long sick's (i): who can die of it, at what chance by age (the elders' too, which the old note left out). |
| 1184 | `ServicesScreen.RECOVERY_INFO` | `String.format("The share of the sick who get better in a month: %.0f%% with no general ...` | What care cures, in words. |
| 1432 | `ServicesScreen.PLOTS_INFO` | `"Plots are consumed permanently — the land never comes back, and a cemetery " + "cannot...` | Plots are permanent (the ground's note). |
| 1436 | `ServicesScreen.CREMATORIA_INFO` | `"A rate rather than a stock, and it needs almost no land — which makes it " + "the answ...` | The crematoria's note. |
| 1773 | `ServicesScreen.DIPLOMA_INFO` | `"Teens age out at a steady rate and the ones who were in school leave with " + "a diplo...` | The diplomas' (i): the teens' note, and what the figure is (and is not). |
| 1781 | `ServicesScreen.LADDER_INFO` | `String.format("The ladder serves the minimum of its three stages, not the average — the...` | The basic ladder's notes: the minimum of three, and the four-and-three split. |
| 1789 | `ServicesScreen.PROFESSIONS_INFO` | `"A band row on the People screen can say the city has eight hundred " + "graduates and ...` | The professions page's sentence. |
| 2006 | `ServicesScreen.GATES_INFO` | `String.format("The funnel is why that many and not more.Who could enrol holds the level...` | The gates' (i): why that many and not more, as the old page said it under its five lines. |
| 2018 | `ServicesScreen.COULD_ENROL` | `"Who holds the level this course takes, in the workforce - and for a " + "professional ...` | Who could enrol, in words (the old page's note). |
| 2022 | `ServicesScreen.RETURN_INFO_PREFIX` | `"How much better off somebody is for doing it - 0 means not worth it.\n\n"` | The wage return's (i) opens on this, then says what the return is measured against (returnNote()). |
| 2379 | `ServicesScreen.POWER_INFO` | `"Power is counted in kilowatts, a rate - the screens wrote watts until " + "0.7.28, a t...` | The power row's (i): the unit, the staff discount, one workforce, who is billed. |
| 2389 | `ServicesScreen.BROWNOUT_INFO` | `"Every industrial and commercial building's output is cut in proportion " + "— a browno...` | A brownout's (i), as the grid's old alert said it. |
| 2394 | `ServicesScreen.WATER_INFO` | `"Water is counted in units of 10,000 gallons a month.The people draw " + "their own(res...` | The water row's (i). |
| 2590 | `ServicesScreen.CAUSES_INFO` | `"Every adult at liberty is counted once, at the heaviest reason they have.The" + " last...` | The causes' (i). |
| 2596 | `ServicesScreen.STOLEN_INFO` | `"What is stolen goes to the offenders' households.The killings are next" + " month's de...` | What is stolen, and the injured (the old "what it did" note). |
| 2600 | `ServicesScreen.CAUGHT_INFO` | `"Anybody caught with no staffed cell free stays on the street and keeps" + " offending....` | Anybody caught with no cell (the prisons' note). |
| 2605 | `ServicesScreen.OFFICERS_INFO` | `crime -> String.format("%s officers per 100,000 people.Canada has %s; full coverage is ...` | The officers against Canada, and the founding constabulary. |

### Statement.java ([map](map/Statement.md))

| line | constant | value | says |
|---:|---|---|---|
| 34 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 150 | `Statement.BOOK_NOW` | `116` |  |
| 152 | `Statement.BOOK_THEN` | `104` |  |
| 443 | `Statement.CLOSED` | `"\u25b8"` |  |
| 445 | `Statement.OPENED` | `"\u25be"` |  |

### StatementView.java ([map](map/StatementView.md))

| line | constant | value | says |
|---:|---|---|---|
| 42 | `StatementView.SUMMARY` | `"Summary", STATEMENT = "Statement"` | The switch's two words: the picture, or the statement (D1: one choice for every page, kept for the session). |
| 45 | `StatementView.TABLE` | `760` | A formal statement's width: its five columns and a label of some fifty characters at the body size. |
| 48 | `StatementView.NOTE_COLUMN` | `28` | The note column. |
| 51 | `StatementView.FIGURE_COLUMN` | `100` | This month's column and last month's: "(9,999,999)" at the body size, and room. |
| 54 | `StatementView.CHANGE_COLUMN` | `96` | The change column. |
| 57 | `StatementView.SHARE_COLUMN` | `64` | The common-size column: "(100.0%)". |
| 60 | `StatementView.INDENT` | `12` | How far a line sits in under its head. |
| 63 | `StatementView.SHARE_MOST` | `10` | A share of the base past this many times it reads "n/m", not meaningful: an outside line against a month with almost no revenue. |
| 66 | `StatementView.GAP` | `Palette.GAP` | The gap between columns. |
| 535 | `StatementView.SHORT_LABEL` | `150, SHORT_FIGURE = 78` | IN SHORT's columns: a label, then this month, last month and the change. |

### SummaryScreen.java ([map](map/SummaryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 161 | `SummaryScreen.PANEL_LABEL` | `Palette.TEXT_LABEL` |  |
| 162 | `SummaryScreen.PANEL_VALUE` | `Palette.TEXT_HEAD` |  |
| 163 | `SummaryScreen.PANEL_GOOD` | `Palette.GOOD` |  |
| 164 | `SummaryScreen.PANEL_WARN` | `Palette.WARN` |  |
| 165 | `SummaryScreen.PANEL_BAD` | `Palette.BAD` |  |
| 172 | `SummaryScreen.DEBT_GDP_LABEL` | `"Debt/GDP, bonds and bills"` | ECONOMY's debt to GDP, named for what it counts (0.7.129): the city's bonds and bills, DebtManager.getAllPrincipal(). |
| 429 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "safety", "re...` | Every section key, so open-all does not have to be kept in step by hand. |
| 525 | `SummaryScreen.WORDS` | `new CityNeeds.Words() { @ Override public String people(double count) { return Money.pe...` | The interface's own words for a figure, which the needs are read in (CityNeeds.Words). |

### TimeChart.java ([map](map/TimeChart.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `TimeChart.AXIS_W` | `64` | How wide a y-axis's labels are held. |
| 68 | `TimeChart.NO_AXIS_W` | `14` | The margin on a side with no axis. |
| 71 | `TimeChart.BAND_ROW` | `20` | The row above the plot that recessions' names sit in. |
| 74 | `TimeChart.TIME_ROW` | `20` | The row under the plot that the years sit in. |
| 77 | `TimeChart.EPISODE_ROW` | `18` | One row of the episode lane. |
| 80 | `TimeChart.FLAG_ROW` | `24` | The decision lane. |
| 83 | `TimeChart.SMALL_FLAG_ROW` | `18` | ...on a small chart handed decisions (0.7.38): the same lane, smaller. |
| 86 | `TimeChart.OVERVIEW` | `52` | The overview strip under the lanes. |
| 89 | `TimeChart.HANDLE` | `7` | How close, in pixels, the pointer must be to the overview window's edge to take it. |
| 92 | `TimeChart.FLAG_GAP` | `16` | A flag closer than this many pixels to the first of the group before it is drawn in that group, as one flag with the count. |
| 95 | `TimeChart.RECESSION_SHADE` | `.10` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 98 | `TimeChart.DRAG_SLOP` | `3` | How far a press may wander and still be a click rather than a drag. |
| 101 | `TimeChart.SETTLE_MS` | `280` | How long the window must rest before the page under the chart is redrawn for it, in milliseconds. |
| 104 | `TimeChart.CARD_W` | `300` | The card's widest. |
| 107 | `TimeChart.NOTCH` | `40` | A wheel notch, in the pixels JavaFX reports it as. |
| 110 | `TimeChart.WHEEL_OWNER` | `"TimeChart.wheel"` | Which node owns the wheel: the window's page-scroll filter leaves a wheel over this alone (UserInterface). |
| 113 | `TimeChart.OVERHANG` | `2000` | How far above and below its own box the chart's clip reaches (0.7.40): it cuts the sides only. |
| 825 | `TimeChart.MARK_REACH` | `5` | How near a mark the pointer must be, in pixels, for the card to say it (0.7.45). |
| 1407 | `TimeChart.CARD_DECISIONS` | `10` | At most this many decisions are listed on a flag's card; the rest are counted. |
| 1579 | `TimeChart.MEASURE` | `new javafx.scene.text.Text()` | One Text node, reused to measure a string's width in a font (textWidth()). |

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
| 347 | `TradeScreen.LEAD_INFO` | `"What the city sells, what it buys, and what it owes in somebody else's money." + "The ...` | The tab's (i): the landing's lead (the spec's L2), and what the Overview shows. |
| 352 | `TradeScreen.PAGE_INFO` | `{ "Every dollar that crossed the city's edge this month, and which way: a walk from wha...` | Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). |
| 396 | `TradeScreen.NOT_COUNTED` | `"not counted yet"` | What a figure of the month says until a month has turned since the city was loaded or founded (ForeignAccounts.isMonthCounted()). |
| 399 | `TradeScreen.NOT_COUNTED_NOTE` | `"since the city was founded, or loaded from a save before 0.7.46: a month on, it is"` | ...and its note. |
| 754 | `TradeScreen.GAUGES_INFO` | `"Always here, deliberately understated, and the same three every month - so a " + "play...` | The section's (i): the banner's reason (the spec's L8). |
| 914 | `TradeScreen.TRADE_INFO` | `"Every good the city's businesses sold abroad and bought abroad this month, from " + "t...` | WHAT WE TRADE's (i). |
| 922 | `TradeScreen.NOTHING_CROSSED` | `"Nothing crossed the city's edge this month.No exports, no imports, nothing " + "borrow...` | The empty month's whole (the spec's M2). |
| 992 | `TradeScreen.PUMP_WORDS` | `String.format("The grocers' filling stations buy the drivers' petrol at wholesale - the...` | Petrol's popover, under the pump price (0.7.83): where it comes from - the fuel duty on the sign since 0.7.103. |
| 998 | `TradeScreen.FREIGHT_AFTER_LOAD` | `"Not counted yet: the railway's freight on each good is struck when the month " + "turn...` | The prices before a month is counted - a city just founded, or loaded from a save before 0.7.46 (the spec's B14; a newer save carries the month's trade the freight is struck on, A1). |
| 1057 | `TradeScreen.RATE_INFO` | `"How many of the city's dollars one US dollar costs.Higher is a weaker currency: " + "i...` | THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). |
| 1119 | `TradeScreen.COVER_SCALE` | `ForeignAccounts.COMFORTABLE_COVER * 2` | The cover gauge's scale: twice the comfortable line, a year of imports. |
| 1146 | `TradeScreen.PARITY_SCALE` | `ForeignAccounts.PARITY_FAR * 1.5` | The parity gauge's scale either side: half as far again as PARITY_FAR, so the red band shows. |
| 1180 | `TradeScreen.COVER_INFO` | `String.format("Import cover is the oldest test there is: if every dollar of " + "earnin...` | The gauges' (i)s: what each one measures (the cover sentence, the spec's C1; the others the landing's notes). |
| 1186 | `TradeScreen.BACKING_INFO` | `String.format("The vault against the foreign money parked in the city's bank, " + "whic...` | ...the backing gauge's: the vault against the money that can leave, and where a run becomes likely. |
| 1190 | `TradeScreen.PARITY_INFO` | `String.format("How far the rate sits from parity - where a basket costs the same " + "h...` | ...and the parity gauge's: the one parity rule's two lines, and where else they are read. |
| 1223 | `TradeScreen.NOTHING` | `.0005` | Under half a dollar is nothing: a step that small is named, not drawn. |
| 1295 | `TradeScreen.CURRENT_INFO` | `"What the city earned from the world by selling it things, less what it spent buying " ...` | The current account's note (the spec's A1). |
| 1300 | `TradeScreen.FINANCIAL_INFO` | `"Borrowing abroad and foreign money parking here are both inflows, and neither is " + "...` | The financial account's note (A2). |
| 1305 | `TradeScreen.INCOME_INFO` | `"Interest and dividends: what the businesses' and the households' paper abroad paid " +...` | The income line's note (B3). |
| 1310 | `TradeScreen.SURPLUS_INFO` | `"A surplus month: the world owes the city a little more than it did, and that is what "...` | The month's closing words (the spec's D17). |
| 1314 | `TradeScreen.DEFICIT_INFO` | `"A deficit month has to be settled in somebody else's money: out of the vault, or by " ...` | ...and a deficit month's. |
| 1378 | `TradeScreen.MONTH_INFO` | `"Every dollar that crossed the city's edge this month, and which way.Steps: each " + "c...` | The hero's (i). |
| 1384 | `TradeScreen.NOT_SAVED_INFO` | `"The month's flows across the edge - what was sold and bought abroad, the income, " + "...` | Why a city reads nothing yet (the spec's B1): founded, or loaded from a save before 0.7.46, which did not carry the month's flows (D4, built as A2). |
| 1390 | `TradeScreen.HAND_INFO` | `"Below the line, and deliberately: an intervention does not earn or spend anything " + ...` | The treasury's hand (the spec's A6). |
| 1395 | `TradeScreen.VALUATION_INFO` | `"%s of foreign claims were written off this month.It improves what the city owes " + "t...` | The valuation change (M5, A4; D15: a chip, not an alert). |
| 1531 | `TradeScreen.RIVER_INFO` | `"Band width is money.The two sides balance because they must: what came in and what " +...` | The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). |
| 1656 | `TradeScreen.HOLDINGS_INFO` | `"The stocks the flows add up to - the rough shape of an international investment " + "p...` | The holdings' (i) (the spec's A5). |
| 1891 | `TradeScreen.RECORD_INFO` | `"One month says whether a mill was staffed.The run says whether the city earns its " + ...` | The record's sentence (the spec's H1). |
| 1952 | `TradeScreen.WORLD_INFO` | `"Every world price is quoted in the world's money and converted at the rate.So a " + "w...` | The world prices' note (the spec's G3). |
| 2008 | `TradeScreen.BAND_LEGS_INFO` | `"Between what the world pays for a good and what it charges to bring one in " + "there ...` | The card's (i): the legs, who each is paid to, and the two readings. |
| 2019 | `TradeScreen.NO_BAND` | `"No good the city trades has both a world buyer and a world seller yet, so there is no ...` | The card's line while no good has a band: the world will not both buy and sell anything the city makes. |
| 2023 | `TradeScreen.NO_BAND_TRADE` | `"Nothing has crossed the edge in the month the books last struck, so there is no " + "t...` | ...and while the books have no trade to weigh it by (a city just founded, or a month with nothing across the edge). |
| 2152 | `TradeScreen.BandBar.BAND` | `26` | The bar's height, in pixels: 26, the Freight page's LegBar's. |
| 2412 | `TradeScreen.FAIR_INFO` | `String.format("The fair level is where the fundamentals alone put the currency: parity'...` | The fair-level gauge's (i): what the level is and why its gauge has no verdict. |
| 2635 | `TradeScreen.HOLD_INFO` | `"The part of the month's move the vault's hold made: under a peg the rate stays at the ...` | THE HOLD's (i): what it is, why the six do not stand on their own under it, and what it is outside a band and a peg. |
| 2653 | `TradeScreen.FORCES_INFO` | `"Six parts add to the month's move, in log points of the rate.Parity's trend: the " + "...` | The forces card's (i): the six parts and what each is, and under a band or a peg the seventh. |
| 2662 | `TradeScreen.TREND_INFO` | `"The city's price level against the world's, on the basket with rent held to a third " ...` | ...and each force's own (i): what it is, and the dials behind it. |
| 2665 | `TradeScreen.REAL_RATE_INFO` | `String.format("A real rate over the world's brings money here and holds the " + "curren...` |  |
| 2670 | `TradeScreen.BALANCE_INFO` | `String.format("A year of the current and the financial account together, against a " + ...` |  |
| 2674 | `TradeScreen.COVER_FORCE_INFO` | `String.format("An empty vault costs the currency a %.0f%% discount, falling to " + "not...` |  |
| 2677 | `TradeScreen.INTERVENTION_INFO` | `String.format("Dollars the treasury buys cheapen the currency and dollars it " + "sells...` |  |
| 2681 | `TradeScreen.MARKET_INFO` | `String.format("The currency's own swing, drawn from the city's seed so the same seed " ...` |  |
| 2686 | `TradeScreen.GATE_INFO` | `String.format("How much of the market's swing and of the fundamentals - the real rate, ...` | The gate's (i): what it lets through, and what it does not gate. |
| 2839 | `TradeScreen.FUNDAMENTALS_INFO` | `"The rate built up from parity, one step at a time, each row the rate after " + "its st...` | The steps card's (i): how to read the table. |
| 2847 | `TradeScreen.RULE` | `String.format("The rate is parity's trend, times where the fundamentals put the currenc...` | THE RULE, folded at the page's foot: the board's paragraph in the model's words and dials. |
| 2915 | `TradeScreen.VAULT_MOVED_INFO` | `"The vault is kept in dollars, so its dollar figure stays put and its local figure " + ...` | What the currency does to the vault (the spec's E2 note, both ways). |
| 2989 | `TradeScreen.ONE_POT_INFO` | `"The vault is one pot — the game does not tag a dollar as borrowed or earned, and it " ...` | WHOSE IT IS's (i) (the spec's R4, and B11: the method's name is out of it). |

### UiProbe.java ([map](map/UiProbe.md))

| line | constant | value | says |
|---:|---|---|---|
| 139 | `UiProbe.report` | `System.out` | Where the probe's own lines go: the console, as it was when the class loaded. |
| 151 | `UiProbe.FRAME_P95_MS` | `34` | The 95th-percentile frame on People at 50x at month 4,000 the clock must keep under, ms (UP2: 49 in the brief's figures and 42 in this probe's before it; the redraw paced, the months budgeted and t... |
| 160 | `UiProbe.FRAME_P95` | `Map.ofEntries(Map.entry("Build", 99.0), Map.entry("Land", 57.0), Map.entry("People", FR...` | Every rail screen's 95th-percentile frame at 50x at month 4,000, ms: its figure before UP2, as this probe measured it on the PC on main f29b9938 (0.8.27) with nothing else running, the run the batc... |
| 167 | `UiProbe.FRAME_P95_FROM` | `4000` | The month from which FRAME_P95 is asserted: the city it was measured on. |
| 593 | `UiProbe.KEYS` | `{ "ham.citybuildersim.ui.UserInterface$", "ham.citybuildersim.Game.nextMonth", "ham.cit...` | The prefixes of "type.method" an FX-thread sample is counted under, if any frame of its stack starts with one: inclusive shares, so a sample counts once for each key it is under. |
| 630 | `UiProbe.FX_THREAD` | `"JavaFX Application Thread"` | The JavaFX thread's name, as the recording names it: the only thread whose samples analyse() reads. |

### UserInterface.java ([map](map/UserInterface.md))

| line | constant | value | says |
|---:|---|---|---|
| 281 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 462 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 465 | `UserInterface.SPEED_TOP` | `50` | The clock's top speed, the ladder's last rung: 50x, a month in a fiftieth of SECONDS_PER_MONTH (SL1, 2026-10-28: what a month's new routes must be found within, MapCheck's ROUTES_MONTH_MS). |
| 477 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, SPEED_TOP }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 478 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 528 | `UserInterface.PRESS_HELD` | `"UserInterface.pressHeld"` | The scene property that says a button is down (0.7.40), for what waits on a timer outside this class (TimeChart's settle). |
| 1420 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1845 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1920 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1923 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1926 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1938 | `UserInterface.DATE_WIDTH` | `164` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five... |
| 1941 | `UserInterface.DATE_SIZE` | `17` | The date's size in the clock (0.7.24; it was 19). |
| 1944 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1947 | `UserInterface.SPARK_WIDTH` | `72` | A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). |
| 1949 | `UserInterface.SPARK_HEIGHT` | `16` | ...and its height, the label's row (0.7.24; it was 30). |
| 1952 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1955 | `UserInterface.TILE_FIGURE` | `15` | The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). |
| 1957 | `UserInterface.TILE_LABEL` | `10.5` | The size of a tile's label, and of the money block's. |
| 1959 | `UserInterface.TILE_CHANGE` | `10.5` | The size of a tile's change line (0.7.24; it was 11). |
| 2420 | `UserInterface.MONEY_FIGURE` | `28` | The cash in the money block: the mockups' 28 px. |
| 3153 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 3154 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 3155 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 3158 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 3161 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 3504 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 3507 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 3582 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3631 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 4096 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 4296 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4977 | `UserInterface.CONSTRUCTION_TAB` | `44` | How wide the construction panel's tab is. |
| 5274 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 5431 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 5434 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 5435 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 5438 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 5921 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 6268 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 6271 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 6274 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

### ZoningScreen.java ([map](map/ZoningScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `ZoningScreen.THE_GROUND` | `"The ground", THE_FOREST = "The forest law"` | The area's two fixed pages: the ground and its zones (the hub), and the forest law. |
| 87 | `ZoningScreen.PAGES` | `{ THE_GROUND, THE_FOREST }` | ...as the tabs list them, before each zone's own. |
| 90 | `ZoningScreen.PAGE_ICONS` | `{ Icons.ZONING, Icons.TREES }` | ...and their icons: the plot, the trees. |
| 93 | `ZoningScreen.ZONE_PAGE` | `"zone:"` | A sector's zone's page is this and its key: "zone:Agriculture". |
| 173 | `ZoningScreen.AREA_INFO` | `"Ground held for one industry: a share of the city's dry ground or a size in km², " + "...` | The area's (i). |
| 229 | `ZoningScreen.BUILT_COLOUR` | `Palette.RAMP_REST` | Built ground: the rest's grey - ground nobody can have. |
| 232 | `ZoningScreen.FREE_COLOUR` | `Palette.BUILDING` | Bare ground free for anyone: the land office's free ground, solid pink. |
| 235 | `ZoningScreen.FOREST_OPEN` | `"#9cc48a", FOREST_WORKING = "#6f9a5e", FOREST_PROTECTED = "#527c45"` | The forest's three, open to protected: two lighter steps of the map's forest green, and the map's own. |
| 305 | `ZoningScreen.GROUND_INFO` | `"The city's dry ground, all of it: what is built on; each zone's ground - built on " + ...` | THE CITY'S GROUND's (i). |
| 312 | `ZoningScreen.ZONES_INFO` | `"A zone holds its size, less what its sector has built in it, out of the free " + "grou...` | THE ZONES' (i). |
| 318 | `ZoningScreen.LAW_INFO` | `"Of the city's forest ground, a protected share is never cut nor built on and a kept " ...` | THE FOREST LAW's (i) on the hub. |
| 363 | `ZoningScreen.PICK` | `"zoning:pick"` | The open line of the sector picker. |
| 535 | `ZoningScreen.PROTECTED_KEY` | `"forest:protected", KEPT_KEY = "forest:kept"` | The forest law's two staged keys: its protected share and its kept share. |
| 538 | `ZoningScreen.SHARE_STEP` | `.005` | A share of the dry ground's step: half a point. |
| 541 | `ZoningScreen.PRICE_STEP` | `.05` | A price share's step: five points of the going price, a twentieth (PolicyScreen's TUITION_STEP's). |
| 544 | `ZoningScreen.LAW_STEP` | `.01` | The forest law's step: a point of the forest. |
| 547 | `ZoningScreen.KM2_STEPS` | `200` | How many steps a km² dial is cut into across the city's dry ground, before its step is rounded to 1, 2 or 5 of a power of ten. |
| 662 | `ZoningScreen.BUDGET_INFO` | `PolicyScreen.BUDGET_INFO + " On a zone's page it adds the property tax its staged " + "...` | THE BUDGET's (i) on these pages. |
| 666 | `ZoningScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press: the zone's size and " +...` | The tray's (i). |
| 702 | `ZoningScreen.SIZE_INFO` | `"A share of the city's dry ground - re-read as the city buys land, so 2% stays 2% - or ...` | HOW MUCH GROUND's (i). |
| 708 | `ZoningScreen.PRICE_INFO` | `"What the city sells the zone's ground at, as a share of the going business price - " +...` | ITS PRICE's (i). |
| 713 | `ZoningScreen.TAXES_INFO` | `"A zone's taxes are its sector's own rates: the city's rate and the sector's move off "...` | ITS TAXES' (i). |
| 718 | `ZoningScreen.GROUND_CAVEAT` | `"Ground as it stands: what the city buys next widens a share."` | The caveat a zone's ground owes: amounts, not places. |
| 974 | `ZoningScreen.FOREST_INFO` | `LAW_INFO + " What it would do is the long run: the protected share of the forest's " + ...` | The law's (i) on its page. |

## harnesses (1140 constants)

### AgricultureCheck.java ([map](map/AgricultureCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `AgricultureCheck.MIXED` | `"Mixed Farm"` |  |
| 57 | `AgricultureCheck.GRAIN` | `"Grain Farm"` |  |
| 58 | `AgricultureCheck.GLASS` | `"Greenhouse Complex"` |  |

### AllChecks.java ([map](map/AllChecks.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "BrandingCheck", "NewGameCheck", "WorldCheck", "GridCheck", "Con...` | In the order they are cheapest to fail. |
| 104 | `AllChecks.FAIL_LINES` | `40` | The most lines -f prints for one failed harness; the rest stays in its log. |
| 107 | `AllChecks.PARALLEL` | `3` | How many harnesses a soft, logged run runs at once (SP1); memory, not the cores, bounds it. |
| 110 | `AllChecks.PARALLEL_PROPERTY` | `"cbs.parallel"` | The property that sets it for one run: -Dcbs.parallel=1 runs them one at a time (given on the java command line or in JDK_JAVA_OPTIONS). |
| 113 | `AllChecks.HEAP_MB` | `2048` | The most heap a harness JVM may take, its -Xmx (SP1): measured 2026-10-27 under a 4 GB ceiling, no harness committed more than 1.5 GB (SaveSlotCheck) or kept more than 0.6 GB after a collection (Ma... |
| 116 | `AllChecks.HEAP_START_MB` | `64` | The heap a harness JVM starts with, its -Xms (SP1): the JVM's default is a 64th of the RAM, 508 MB committed at once whether used or not (measured 2026-10-27: BooksCheck 616 MB of commit by default... |
| 119 | `AllChecks.COMMIT_PCT` | `75` | No harness is started beside another of this run while the machine's commit charge is over this percent (SP1): the first three, started at 79.8%, took the PC to 85.9% within a minute as they grew (... |
| 122 | `AllChecks.LONGEST_FIRST` | `List.of("MapCheck", "GlobeCheck", "LongPlaytest", "EarthTerrainCheck", "BridgeMapCheck"...` | The harnesses started first when more than one runs at once, the longest first (measured 2026-10-27, earth-2.bin named: MapCheck 192 s, GlobeCheck 72, LongPlaytest 55, EarthTerrainCheck 52, the res... |
| 125 | `AllChecks.LOGS` | `Paths.get("target", "checks")` | Where -q and -f keep each harness's whole output. |
| 128 | `AllChecks.SECTION` | `Pattern.compile("^\\s*---\\s.*\\s---\\s*$")` | A harness's section title, "--- what it proves ---". |
| 131 | `AllChecks.SUMMARY` | `Pattern.compile("^\\s*\\d+ FAILED\\s*$")` | A harness's closing count, "N FAILED": printed, but not under the last section's title. |
| 134 | `AllChecks.FAILING` | `Pattern.compile("\\bFAIL(ED)?\\b\|Exception\|^\\S*Error\\b\|^Caused by")` | A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. |
| 137 | `AllChecks.IN_ALL` | `Pattern.compile("^\\.\\.\\.[0-9]+ failing lines in all: ")` | The line failingLines prints when it left failing lines out: "... |
| 140 | `AllChecks.GATE` | `Paths.get("docs", "notes", "gate.txt")` | Main's recorded reds, one line each after the "#" lines: what ship.sh writes at every gate. |
| 143 | `AllChecks.KNOWN_PROPERTY` | `"cbs.knownReds"` | The system property that names the record when it is not GATE; batch-check.sh gives it the record it judges against. |
| 146 | `AllChecks.NO_FAIL_LINE` | `"(no FAIL line; its last lines:)"` | The line a log says before the last lines of a harness that died with no failing line of its own. |
| 149 | `AllChecks.KNOWN_TAG` | `"[known] "` | The tag -f puts before a failing line that is in the record; the same width as NEW_TAG. |
| 152 | `AllChecks.NEW_TAG` | `"[NEW] "` | The tag -f puts before a failing line that is not in the record. |

### AuctionCheck.java ([map](map/AuctionCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 138 | `AuctionCheck.FEE` | `12_000` | The fixed cost of a sale: the study's AUCTION_FEE, the one Game charges. |
| 141 | `AuctionCheck.FAIR` | `.03602` | The 10-year's fair yield in the calm city (spec-city-debt.md, section 4.3's table: 3.602%). |
| 144 | `AuctionCheck.FACE` | `3e8` | D$300M of face, the table's issue. |
| 725 | `AuctionCheck.QUIET` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | A stream that prints nothing: System.out and LongPlaytest's out while a fixture city runs. |

### AutoBuildCheck.java ([map](map/AutoBuildCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 301 | `AutoBuildCheck.heldBy` | `new LinkedHashMap<>()` | The passes that held a kept service short of its target with nothing on site, by the cut that held it. |
| 303 | `AutoBuildCheck.builtBy` | `new LinkedHashMap<>()` | The buildings it ordered over the run, by measure. |
| 1055 | `AutoBuildCheck.YOUNG_MONTH` | `60` | The month the young city is played to before the refusal: the 0.7.89 city that stalled was refused at month 60. |
| 1058 | `AutoBuildCheck.SHORT` | `280` | What the refused order is short of, in thousands, the game's money: $0.28M, the 0.7.89 city's ($24.11M of treasury against $24.39M of order). |
| 1061 | `AutoBuildCheck.AFTER_MONTHS` | `24` | The months the city plays on after the refusal, its debt limit left where the fixture pinned it. |
| 1064 | `AutoBuildCheck.LOOK_MONTHS` | `6` | The months between the test player's looks, as section 3's loop plays: it plays on, then moves. |
| 1067 | `AutoBuildCheck.YOUNG_PEOPLE` | `100_000` | A young city has fewer people than this at the refusal: a town, not the city it grows into. |
| 1329 | `AutoBuildCheck.ROOM` | `50_000` | A month's property tax withRoom() adds, in thousands: $50M, far past what any pass in the town orders runs to (0.7.101; the payback's since 0.7.104). |
| 1431 | `AutoBuildCheck.OVER_SQ_FT` | `1_000_000` | The over-full fixture's deficit, in square feet: a million, a sixth of Jerus's 5.66M at month 416 (runs/fixN5-notes.md) - any deficit makes the point. |
| 1472 | `AutoBuildCheck.ORE_TONNES` | `1` | A tonne of iron under each offer of the ore fixture: any ore makes an offer not bare ground (LandMarket.bareGround()). |

### BondCheck.java ([map](map/BondCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 117 | `BondCheck.ISSUER` | `Sectors.CONSTRUCTION` | The sector every played fixture's bond is issued by: sound, owing nothing, with plant to borrow against. |

### BridgeCheck.java ([map](map/BridgeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `BridgeCheck.out` | `System.out` |  |
| 62 | `BridgeCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 65 | `BridgeCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 73 | `BridgeCheck.SURVEY_BUDGET_MS` | `10_000` | How long a survey may take on the Earth, milliseconds: 10,000. |
| 76 | `BridgeCheck.BAY_BUDGET_MS` | `10_000` | The worst case of a synthetic bay a bridge can be as long as, milliseconds: the survey of 1.7 million plots of open shallow water. |
| 102 | `BridgeCheck.DEEP_M` | `100` | The depth the grid gives a plot nobody set: deep sea. |
| 200 | `BridgeCheck.ISLAND` | `2, HOME = 1` | The far island's landmass id and home's, in every grid here. |
| 911 | `BridgeCheck.SHORE_SQUARE` | `128` | Plots a side of the squares the full-file crossings buy at each shore. |

### BridgeMapCheck.java ([map](map/BridgeMapCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `BridgeMapCheck.SITE` | `IslandNetCheck.SITE` | The founding site of the runs' ground: a highway line crossing, so that the hub is the site itself. |
| 88 | `BridgeMapCheck.ROAD` | `CityMap.BridgeRun.ROAD, HIGHWAY = CityMap.BridgeRun.HIGHWAY, RAIL = CityMap.BridgeRun.RAIL` | The kinds, short. |
| 174 | `BridgeMapCheck.W0` | `SITE - 700, W1 = SITE + 150, E0 = SITE + 221, E1 = SITE + 721, Y0 = SITE - 450, Y1 = SI...` | The runs' ground: the west island (to x = SITE + 150), a channel of 70 plots unowned between, the east island from SITE + 221. |
| 177 | `BridgeMapCheck.HW` | `{ W1, SITE + 40, SITE + 170, SITE + 40, SITE + 182, SITE + 52, E0, SITE + 52 }` | The highway bridge: out of the west island's shore plot, east, a 45-degree stretch, east into the east island's first column. |
| 179 | `BridgeMapCheck.RL` | `{ W1, SITE + 40, SITE + 160, SITE + 30, E0 + 1, SITE + 30 }` | The rail bridge: out of the same landing, north-east, then east. |
| 181 | `BridgeMapCheck.RD` | `{ W1, SITE - 60, E0, SITE - 60 }` | The road bridge: straight across, a row above. |
| 380 | `BridgeMapCheck.MOST_WIDE` | `140` | The most plots wide a channel may be for the fixture: the bridges are paths of a hundred plots, not thousands. |
| 383 | `BridgeMapCheck.BAND` | `24` | Rows of the band either side of its middle row. |
| 579 | `BridgeMapCheck.MapFixture.APPROACH` | `14` | How far inland of the shore a bridge's landing may be chosen, in plots: the city builds on a bank's first plots in a few rows only. |

### BuildCardCheck.java ([map](map/BuildCardCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 961 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind.BUILDING }...` | The examples: a word shaped as the model files it, and the kind it must read as. |

### BuildersFirstCheck.java ([map](map/BuildersFirstCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 115 | `BuildersFirstCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key: whose depots the rule serves first, and whose plan the fixture asks. |
| 118 | `BuildersFirstCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the template that adds most to their capacity. |
| 121 | `BuildersFirstCheck.YARD` | `Game.FOUNDING_YARD` | ...and the Builders' Yard the city founds with (PT1-M4), the other that adds to it: 160 points. |
| 124 | `BuildersFirstCheck.RUN_MONTHS` | `24` | The months the fixture's run is played: two years, a span the trace's city held through ten times over. |
| 439 | `BuildersFirstCheck.NEW_TOWN_MONTHS` | `12` | The most months the new town is played once it has its people, and its first depot opened early or not: the window ends the month the twin without the test opens one, or here (PT1-M4: it opens in t... |
| 448 | `BuildersFirstCheck.DEPOTS_STANDING` | `3` | The depots that stand beside the founding Builders' Yard (PT1-M4): the works department that built for a town nobody lived in is retired, and the yard alone, at the town's per-type fill (about a fi... |
| 456 | `BuildersFirstCheck.FOUNDING_MONTHS` | `3` | The months the town is played before the twins are compared (PT1-M4): the town is founded with nobody in it, so the builders' fill after month 1 reads 0, and in months 2 and 3 the first homes fill ... |

### BusinessServicesCheck.java ([map](map/BusinessServicesCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BusinessServicesCheck.CONTACT` | `"Contact Centre"` | The three rungs, and the band each is meant to employ. |
| 58 | `BusinessServicesCheck.SHARED` | `"Shared Services Centre"` |  |
| 59 | `BusinessServicesCheck.OFFICE` | `"Engineering Services Office"` |  |

### BuybackCheck.java ([map](map/BuybackCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 102 | `BuybackCheck.PRINCIPAL` | `1_000` |  |
| 103 | `BuybackCheck.COUPON` | `.04` |  |
| 104 | `BuybackCheck.MARKET_YIELD` | `.05` |  |

### CallCheck.java ([map](map/CallCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 127 | `CallCheck.ROW_MONTHS` | `{ 12, 24, 60, 120, 240, 360, 600 }` | The study's calm city (spec section 3.3): the 1-year zero rate and the zero rate at 2, 5, 10, 20, 30 and 50 years. |
| 128 | `CallCheck.ROW_ZERO` | `{.0312,.0318,.0335,.0362,.0402,.0427,.0462 }` |  |

### CarCheck.java ([map](map/CarCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 115 | `CarCheck.CARS_WAS_IN` | `44, CARS_WAS_OUT = 36, VANS_WAS_IN = 72, ROLLING_WAS_IN = 2400, FABRICATED_WAS = 2.21, ...` | The prices to 0.7.119, written down: cars in and out, vans and wagon sets in; fabricated steel out; machinery in and out. |

### CementConcreteCheck.java ([map](map/CementConcreteCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `CementConcreteCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 66 | `CementConcreteCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 119 | `CementConcreteCheck.ROWS` | `{ CementConcrete.CEMENT_PLANT, CementConcrete.LARGE_CEMENT_PLANT, CementConcrete.LIME_K...` | Cement & Concrete's rows, by their permanent ids, in the study's 6.4 order. |
| 122 | `CementConcreteCheck.STUDY_COSTS` | `{ 120_000, 500_000, 75_000 }` | The study's 6.4: each one's cost, D$ thousands all in. |
| 125 | `CementConcreteCheck.STUDY_POSTS` | `{ 60, 150, 45 }` | ...its posts. |
| 128 | `CementConcreteCheck.STUDY_RUNS` | `{ 501, 1_730, 298 }` | ...its running cost a month, D$ thousands. |
| 131 | `CementConcreteCheck.STUDY_MAKES` | `{ 25_000, 125_000, 18_000 }` | ...what it makes a month, t: cement, cement, lime. |
| 134 | `CementConcreteCheck.STUDY_USES` | `{ { 30_000, 3_750, 3_350 }, { 150_000, 18_750, 16_750 }, { 32_400, 0, 3_600 } }` | ...and what it takes, t a month: limestone, aggregate, thermal coal. |
| 137 | `CementConcreteCheck.CEMENT_KWH` | `91` | The research's cement, kWh a tonne: the study's 6.2, 91 [S31]. |
| 140 | `CementConcreteCheck.HOURS` | `QuarryingCheck.HOURS` | The hours of a month the study reads its kW at (its 2.2): QuarryingCheck's. |
| 143 | `CementConcreteCheck.SINCE` | `{ 1900, 1902, 1900 }` | The year each first existed: 1900 - standing before the start - but the Large Cement Plant, Edison's long kilns at New Village in 1902 (research-1900.md 1.2). |
| 146 | `CementConcreteCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |

### CentralBankCheck.java ([map](map/CentralBankCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 207 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the window's i...` |  |
| 210 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1191 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1607 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 1610 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |
| 1620 | `CentralBankCheck.STRICT_WORLD_FALL` | `.20` | How far the world's prices fall, in the first month of the fourth game's twenty years (worldPricesFall): the cause of the very loose twin's months under its target, which the probe city's own path ... |
| 1623 | `CentralBankCheck.STRICT_FALL_WINDOW` | `24` | The months after the fall in which the fixture needs the very loose twin under its target: the fall's own, before the currency has answered. |

### ChartFitCheck.java ([map](map/ChartFitCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `ChartFitCheck.SIZES` | `{ { 1280, 720 }, { 1600, 900 }, { 1920, 1080 }, { 2560, 1440 } }` | The window sizes the checks run at: a laptop, a desk monitor, full HD, and a big one. |
| 43 | `ChartFitCheck.MOST_LINES` | `16` | The most lines tried when looking for the counts that wrap the legend. |

### ChildcareCheck.java ([map](map/ChildcareCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ChildcareCheck.ONTARIO_INFANTS_PER_ADULT` | `10.0 / 3` | Children to an adult in Ontario's infant groups, 3 adults to 10 (O. |
| 86 | `ChildcareCheck.ONTARIO_PRESCHOOL_PER_ADULT` | `8` | ...and in its preschool groups, 1 to 8: the fewest. |
| 89 | `ChildcareCheck.SMALL` | `15, CENTRE = 16, LARGE = 17` | The three centres' ids in buildings.json: the Small Childcare Centre, the Childcare Centre and the Large. |
| 92 | `ChildcareCheck.CHILDCARE` | `BuildAdvice.Measure.care(CareType.CHILDCARE)` | The advice's measure for childcare, which the sections here read the need, the site and the card through. |

### ClockCheck.java ([map](map/ClockCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 46 | `ClockCheck.SPM` | `UserInterface.SECONDS_PER_MONTH` | Real seconds a month takes at 1x, as the window's clock has it. |
| 49 | `ClockCheck.TOP` | `50` | The top rung. |
| 50 | `ClockCheck.TOP_LABEL` | `"50×"` |  |
| 52 | `ClockCheck.FRAME` | `1.0 / 60` |  |
| 363 | `ClockCheck.KEYS` | `{ "population", "gdp", "priceIndex", "unemployment", "policyRate" }` | The header's five keys, and the whole-history read each one stands in for. |

### CoalMiningCheck.java ([map](map/CoalMiningCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `CoalMiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 92 | `CoalMiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 127 | `CoalMiningCheck.STUDY_RAW` | `500_000, STUDY_COKING = 275_000, STUDY_THERMAL = 100_000, STUDY_SPOIL = 125_000` | The study's 3.3: a plant's 500,000 t of raw coal, and what comes of it. |
| 204 | `CoalMiningCheck.ROWS` | `{ CoalMining.SURFACE_MINE, CoalMining.LARGE_SURFACE_MINE, CoalMining.UNDERGROUND_MINE, ...` | Coal Mining's rows, by their permanent ids, in the study's 3.3 order. |
| 208 | `CoalMiningCheck.STUDY_COSTS` | `{ 120_000, 950_000, 800_000, 150_000 }` | The study's 3.3: each one's cost, D$ thousands all in. |
| 211 | `CoalMiningCheck.STUDY_POSTS` | `{ 80, 350, 400, 50 }` | ...its posts. |
| 214 | `CoalMiningCheck.STUDY_KW` | `{ 800, 6_000, 29_000, 4_000 }` | ...its draw, kW. |
| 217 | `CoalMiningCheck.STUDY_RUNS` | `{ 1_070, 5_570, 5_770, 774 }` | ...its running cost a month, D$ thousands (the surface mine's "with 230,000 L diesel" in it, at the study's D$0.60). |
| 220 | `CoalMiningCheck.STUDY_MAKES` | `{ 100_000, 800_000, 250_000, 500_000 }` | ...the coal each mine makes a month, t (the longwall's raw), and the plant's wash. |
| 223 | `CoalMiningCheck.STUDY_SITES` | `{ 1, 4, 1, 0 }` | ...the sites each stands on, and of which type (the study's 1.3 and 3.3; the plant none). |
| 225 | `CoalMiningCheck.STUDY_TYPES` | `{ Deposit.FieldType.THERMAL, Deposit.FieldType.THERMAL, Deposit.FieldType.COKING, null }` | ...and the type of field each stands on (null for the plant, which stands on none). |
| 228 | `CoalMiningCheck.SURFACE_DIG` | `1 /.95, UNDERGROUND_DIG = 1 /.80` | ...the tonnes of seam a tonne of coal takes: 95% of the seam recovered at the surface, 80% by a longwall (the study's 3.1; spec 2.4). |
| 231 | `CoalMiningCheck.STUDY_DIESEL_LITRES` | `230_000, STUDY_DIESEL =.0006` | ...the surface mine's diesel, litres a month (the study's 3.3), and its price in the study's runs, D$ thousands a litre (its 1.5). |
| 234 | `CoalMiningCheck.STUDY_PLANT_HECTARES` | `19` | ...the plant's ground, hectares (the study's 3.3). |
| 237 | `CoalMiningCheck.STUDY_ROUNDING` | `.05, STUDY_ROUNDING_POINT =.5` | The study's rounding: its returns to a tenth of a point, the plant's 19% to the point. |
| 240 | `CoalMiningCheck.STUDY_SURFACE` | `3.3, STUDY_LARGE = 3.6, STUDY_ALONE =.8, STUDY_PAIR = 2.4, STUDY_PLANT = 19` | The returns the study's 3.4 states, % of the cost a month: a surface mine and a large one exporting, a longwall alone, the pair, the plant alone. |
| 379 | `CoalMiningCheck.TYPE_WITHIN` | `.02` | How far coal's thermal share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points, WorldCheck 8's). |

### ConstructionControlCheck.java ([map](map/ConstructionControlCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 111 | `ConstructionControlCheck.RE` | `Sectors.REAL_ESTATE` |  |

### ConsumptionCheck.java ([map](map/ConsumptionCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 38 | `ConsumptionCheck.LADDER` | `{ 1, 2, 5, 10, 20, 27, 50, 90, 200, 500, 2_000 }` |  |

### ConversionCheck.java ([map](map/ConversionCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ConversionCheck.FIXTURE` | `"conversion-saves.json"` | The fixture: the five saves' land, copied key for key (src/main/resources). |
| 86 | `ConversionCheck.WHOLE_CITY_PURCHASES` | `133` | Purchases the whole-field city of section 4 is bought to: 133, as many as Jerus's live city had made. |

### CopperMiningCheck.java ([map](map/CopperMiningCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 74 | `CopperMiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 76 | `CopperMiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 108 | `CopperMiningCheck.CU` | `Resource.COPPER` | Copper, the resource its mines and leach plants dig. |
| 110 | `CopperMiningCheck.SULPHIDE` | `Deposit.FieldType.SULPHIDE, OXIDE = Deposit.FieldType.OXIDE` | Copper's two types of field (Resource.typesOf()): sulphide, floated at the mine, and oxide, leached. |
| 115 | `CopperMiningCheck.GOODS` | `{ Good.COPPER_CONC, Good.ANODE, Good.COPPER, Good.COPPER_SCRAP, Good.COPPER_ROD, Good.A...` | Copper's goods, the study's 4.2: import and export price, D$ thousands a tonne. |
| 117 | `CopperMiningCheck.STUDY_IMPORT` | `{ 2.800, 10.150, 10.500, 8.700, 10.750,.160, 17.000 }` | ...each one's import price, D$ thousands a tonne (the study's 4.2)... |
| 119 | `CopperMiningCheck.STUDY_EXPORT` | `{ 2.575, 9.650, 10.000, 7.300, 10.250,.070, 13.000 }` | ...and its export price. |
| 122 | `CopperMiningCheck.FREIGHT_SHARE` | `.375` | Good.java's rule: freight three eighths of the wedge. |
| 193 | `CopperMiningCheck.ROWS` | `{ CopperMining.MINE_AND_MILL, CopperMining.LARGE_MINE, CopperMining.LEACH_PLANT }` | Copper Mining's rows, by their permanent ids, in the study's 4.3 order. |
| 196 | `CopperMiningCheck.STUDY_COSTS` | `{ 160_000, 2_000_000, 250_000 }` | The study's 4.3: each one's cost, D$ thousands all in. |
| 199 | `CopperMiningCheck.STUDY_POSTS` | `{ 120, 450, 150 }` | ...its posts. |
| 202 | `CopperMiningCheck.STUDY_KW` | `{ 5_500, 60_000, 6_000 }` | ...its draw, kW. |
| 205 | `CopperMiningCheck.STUDY_MAKES` | `{ 2_200, 26_400, 1_000 }` | ...what it makes a month, t: concentrate, concentrate, cathode. |
| 208 | `CopperMiningCheck.STUDY_ACID` | `3_500` | ...the acid the leach plant takes, t a month. |
| 211 | `CopperMiningCheck.STUDY_SITES` | `{ 1, 8, 1 }` | ...the sites each stands on, and of which type (its 4.3 and 1.3: "an oxide copper field takes a leach plant"). |
| 213 | `CopperMiningCheck.STUDY_TYPES` | `{ SULPHIDE, SULPHIDE, OXIDE }` | ...and the type of field each stands on. |
| 216 | `CopperMiningCheck.STUDY_TABLE` | `{ { 2.6, 2.6, 2.9, 2.9 }, { 2.8, 2.8, 3.1, 3.1 }, { 3.1, 3.2, 3.4, 3.3 } }` | The study's 4.4 table, % of the cost a month, cases A to D. |
| 219 | `CopperMiningCheck.STUDY_RUNS` | `{ 1_512.5, 11_950, 1_722.5 }` | Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table. |
| 222 | `CopperMiningCheck.SINCE` | `{ 1900, 1906, 1968 }` | The year each first existed: 1900 - standing before the start - but the Large Copper Mine, the open-pit porphyry mine at Bingham Canyon in 1906 (research-1900.md 1.2, PE8), and the leach plant, SX-... |
| 225 | `CopperMiningCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 289 | `CopperMiningCheck.TYPE_WITHIN` | `.02` | How far copper's sulphide share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points, WorldCheck 8's). |

### CopperRefiningCheck.java ([map](map/CopperRefiningCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 78 | `CopperRefiningCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 80 | `CopperRefiningCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 167 | `CopperRefiningCheck.ROWS` | `{ CopperRefining.SMELTER, CopperRefining.ELECTROLYTIC_REFINERY, CopperRefining.SCRAP_RE...` | The rows, by their permanent ids, in the study's 4.3 order: Copper Refining's four and Manufacturing's wire works. |
| 171 | `CopperRefiningCheck.STUDY_COSTS` | `{ 500_000, 180_000, 280_000, 45_000, 110_000 }` | The study's 4.3: each one's cost, D$ thousands all in. |
| 174 | `CopperRefiningCheck.STUDY_POSTS` | `{ 700, 200, 200, 80, 400 }` | ...its posts. |
| 177 | `CopperRefiningCheck.STUDY_KW` | `{ 13_400, 6_000, 5_500, 1_100, 2_500 }` | ...its draw, kW. |
| 180 | `CopperRefiningCheck.STUDY_MADE` | `{ { Good.ANODE, Good.ACID }, { Good.COPPER }, { Good.COPPER }, { Good.COPPER_ROD }, { G...` | ...what it makes a month, t. |
| 182 | `CopperRefiningCheck.STUDY_MAKES` | `{ { 9_750, 29_700 }, { 9_700 }, { 4_000 }, { 8_000 }, { 2_000 } }` | ...and how many tonnes of each, a month. |
| 185 | `CopperRefiningCheck.STUDY_FEED` | `{ Good.COPPER_CONC, Good.ANODE, Good.COPPER_SCRAP, Good.COPPER, Good.COPPER_ROD }` | ...and what it takes, t a month. |
| 187 | `CopperRefiningCheck.STUDY_USES` | `{ 33_000, 9_750, 4_400, 8_016, 2_000 }` | ...and how many tonnes of it, a month. |
| 190 | `CopperRefiningCheck.STUDY_TABLE` | `{ { - 0.3, 1.2, 2.7, 1.2 }, { - 1.9, 0.8, 3.5, 0.8 }, { 0.0, 2.2, 2.9, 0.7 }, { - 6.7, ...` | The study's 4.4 table, % of the cost a month, cases A to D. |
| 194 | `CopperRefiningCheck.STUDY_RUNS` | `{ 5_253, 1_460, 1_776, 846, 4_820 }` | Each row's running cost a month, D$ thousands, backed out of its 4.4 table (its 4.3 gives none): the middle of the range on which all four of its returns round to the table - but the scrap refinery... |
| 197 | `CopperRefiningCheck.STUDY_SCRAP_A` | `- 0.02, STUDY_SCRAP_A_ROUNDING =.005` | The scrap refinery's case A as the study states it (its 4.4: "imported scrap just breaks even (case A, -0.02%)"), and that figure's rounding. |
| 200 | `CopperRefiningCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 282 | `CopperRefiningCheck.ACID_A_TONNE` | `.9` | The acid a smelter makes a tonne of concentrate: 0.9 t (the study's 4.1, [K27]). |

### CreditCheck.java ([map](map/CreditCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 20 | `CreditCheck.IND` | `Sectors.INDUSTRY` |  |

### CrossingCheck.java ([map](map/CrossingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 161 | `CrossingCheck.TOP` | `160` | The plan's frame row (or column) at which a band of water starts across it (PlanCheck 10 (e)'s: south of the hub, with district to spare either side). |
| 164 | `CrossingCheck.ARTERIAL_LINES` | `(DistrictPlan.FRAME - 1) / DistrictPlan.CELL + 1` | How many lines of the plan's frame are an arterial's: the multiples of CELL (a district's lines), the frame's first and last among them. |

### CurrencyCheck.java ([map](map/CurrencyCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 105 | `CurrencyCheck.OLD_DIAL_STOP` | `.25` | The dial's stop until 0.7.2, for the assertion that it is gone. |
| 108 | `CurrencyCheck.OLD_SPREAD_STOP` | `.06` | The hot money's old stop, likewise: six points. |
| 111 | `CurrencyCheck.OLD_MAX_RATE` | `100` | The currency's guard above until 0.7.3, a hundred local dollars to one of theirs - for the assertions that a rate goes past it. |
| 114 | `CurrencyCheck.OLD_MIN_RATE` | `.01` | ...and below, a hundredth. |
| 127 | `CurrencyCheck.TRADING_MONTHS` | `24 + 12` | The months a fixture trades before its test month: 24, the currency's settling months until XR1, and a year more for the trailing figures to settle. |
| 698 | `CurrencyCheck.Before.broken` | `new int [ 5 ]` |  |

### CurrencyRegimeCheck.java ([map](map/CurrencyRegimeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 159 | `CurrencyRegimeCheck.FLOAT` | `ForeignAccounts.Regime.FLOAT, LEAN = ForeignAccounts.Regime.LEAN, BAND = ForeignAccount...` | The four regimes, by their short names. |
| 163 | `CurrencyRegimeCheck.TRADE` | `3_000, GDP = 6_000` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one (ExchangeRateCheck's). |
| 166 | `CurrencyRegimeCheck.SEED` | `20_261_022L` | The market's seed in the sections that draw one. |
| 169 | `CurrencyRegimeCheck.DEEP` | `1e9` | A vault no fixture of these can empty or come near the floor of, in dollars. |
| 172 | `CurrencyRegimeCheck.DOLLAR` | `1e-6` | What the harness's own sums are held to where they are exact arithmetic on dollars. |
| 854 | `CurrencyRegimeCheck.SLOT_DOUBT_LIFETIME` | `15, SLOT_ARRIVED = 16, SLOT_DEPARTED = 17, SLOT_DOUBT_DEPARTED = 18, SAVE_SLOTS = 19` | Where CapitalFlows.toSaveArray() puts the doubt's lifetime (XR2) and the month's three flows (FX8), and how long the array is. |
| 1110 | `CurrencyRegimeCheck.EDGE_BILL` | `"The band's or the peg's dollars"` | The label the treasury's journal gives the edge's bill (Game.settleTheEdge()). |

### CurveCheck.java ([map](map/CurveCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 148 | `CurveCheck.TARGET` | `.02` | The target the study's cities aim at: the game's default, the figure the rule's neutral rate is struck on. |
| 151 | `CurveCheck.GDP_YEAR` | `5.4e9, TAX_YEAR = 1.1e9, DEBT = 3.4e9` | The study's economy and debt (spec section 3.3, debt-model.py): a year's output and a year's tax, in local money, and the term paper outstanding. |
| 154 | `CurveCheck.MONTHS` | `{ 3, 12, 24, 60, 120, 240, 360, 600 }` | The maturities section 1 asserts at, in months. |
| 157 | `CurveCheck.ROW` | `{ 1, 12, 24, 60, 120, 240, 360, 600 }` | Section 3.3's columns: 1 month, 1, 2, 5, 10, 20, 30 and 50 years. |
| 163 | `CurveCheck.CALM` | `new Study("calm",.03,.02,.80, 0, new double[] { 3.12, 3.12, 3.18, 3.35, 3.62, 4.02, 4.2...` | The calm city: the dial at 3%, inflation at 2%, credibility .80, no holdings. |
| 166 | `CurveCheck.HIKING` | `new Study("hiking",.07,.06,.65, 0, new double[] { 7.12, 6.85, 6.50, 6.04, 6.02, 6.27, 6...` | ...the hiking city: the dial at 7% against inflation at 6%, credibility .65. |
| 169 | `CurveCheck.BEHIND` | `new Study("behind",.01,.05,.60, 0, new double[] { 1.12, 3.65, 4.46, 5.00, 5.35, 5.79, 6...` | ...the city behind: the dial at 1% against inflation at 5%, credibility .60. |
| 172 | `CurveCheck.QE` | `new Study("QE",.03,.02,.80,.25, new double[] { 3.12, 3.12, 3.15, 3.24, 3.37, 3.57, 3.70...` | ...and QE: the calm city with the central bank holding a quarter of the term paper. |
| 175 | `CurveCheck.CITIES` | `{ CALM, HIKING, BEHIND, QE }` | The four, in the study's order. |
| 483 | `CurveCheck.ASK` | `20_000` | What the issue sections borrow: small against what the founding city earns (its output is about 3,300 a month). |
| 552 | `CurveCheck.USD_ASK` | `1_000` | What the dollar legs borrow, in dollars. |

### DeathCareFirstCheck.java ([map](map/DeathCareFirstCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `DeathCareFirstCheck.RE` | `Sectors.REAL_ESTATE` | Real Estate's key: the landlords, whose Houses the fixture's town is given. |
| 101 | `DeathCareFirstCheck.CEMETERY` | `"Memorial Cemetery"` | The death care the fixture puts on site: the smaller cemetery, which serves the dead by its care type. |
| 104 | `DeathCareFirstCheck.ROAD` | `"Paved Road"` | The road whose repairs fill the builders' month: it has its own repairs share (0.7.103), and carries traffic, nothing else. |
| 112 | `DeathCareFirstCheck.TRAP_MONTHS` | `12` | The months the cemetery's points take at the trap's city share, in the fixture: the trace's measure A opened Jerus's in about twelve (why-the-builders-stall.md), so the repairs leave the builders' ... |

### EarthCheck.java ([map](map/EarthCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 110 | `EarthCheck.ROUND_TRIP_M` | `1e-6` | Inverse then forward comes back within this, metres (spec-earth 8, E1; 2.4e-7 measured). |
| 112 | `EarthCheck.ROUND_TRIP_REACH_M` | `1_050_000` | ...for every plane point within this of the spot, metres (spec-earth 8: 1,050 km; a 10B city's half-diagonal is 938). |
| 114 | `EarthCheck.AREA_TOLERANCE` | `1e-9` | A cell's area measured on the sphere is World.CELL_KM2 within this share of it (spec-earth 8: 3,774.87 km2 to 1e-9). |
| 116 | `EarthCheck.AREA_SAMPLES` | `4096` | Points a side the sphere's measure of a cell takes: 15 m apart, so the great circles between them stay within a millionth of a square metre of the edge's curve. |
| 118 | `EarthCheck.BLEND_M` | `0.1` | A blended point lies within this of the exact one, metres (spec-earth 8: 0.1 m; 0.054 measured 10,000 km out). |
| 120 | `EarthCheck.BLEND_REACH_M` | `10_000_000` | How far out the blend is measured, metres of the plane (spec-earth 8: 10,000 km). |
| 122 | `EarthCheck.INFLATE_MS` | `1` | A 15" tile inflates within this, milliseconds (spec-earth 8; 0.13 measured). |
| 124 | `EarthCheck.OFF_PLANET_CELLS` | `12_108` | Rotterdam's square: the cells whose 4 x 4 samples are all off the planet (spec-earth 2: 12,108 of 135,424 measured; the square is the same round any spot). |
| 126 | `EarthCheck.FIXTURE` | `"earth/earth-2-fixture.bin", EXPECT = "earth/earth-2-fixture.expect"` | The fixture and its expected figures, beside the classes. |
| 128 | `EarthCheck.EARTH1_PROPERTY` | `"cbs.earth1"` | The system property that names earth-1.bin for section 11, when it is not beside the named file. |
| 130 | `EarthCheck.ROOMY` | `Long.MAX_VALUE / 4` | An LRU budget no harness fills: a reader with room for every tile it is asked for, to weigh them. |
| 132 | `EarthCheck.CITY_TILES` | `6` | A city's 50 km reads this many 1-degree tiles at most (the study's 2.9: 4 to 6). |
| 134 | `EarthCheck.MIN_FULL_TILES` | `4 * CITY_TILES` | The default LRU has to hold at least this many tiles with every layer inflated (section 12's loose bound): four cities' tiles. |
| 136 | `EarthCheck.EARTH1_TILE_LAYERS` | `{ "sea", "lake", "landcover", "lakes.small", "rivers" }, EARTH2_TILE_LAYERS = { "slope"...` | The 1-degree tile layers of earth-1 and of earth-2 section 12 inflates: the five a tile of earth-1 has, the four earth-2 adds. |
| 139 | `EarthCheck.EARTH1_LAYERS` | `{ "sea", "lake", "lakes.small", "rivers", "landcover", "far.land", "far.lake", "geology...` | The ten layers earth-2 holds of earth-1's byte for byte (meta is earth-2's own). |
| 143 | `EarthCheck.out` | `System.out` | Where the harness prints: the console. |
| 771 | `EarthCheck.RELIEF_KINDS` | `Set.of("slopeb", "shallowb", "depthb", "depthcode", "slopeest", "landmasstile", "landma...` | The expected figures' kinds relief() reads (agree() leaves them to it). |

### EarthDepositsCheck.java ([map](map/EarthDepositsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `EarthDepositsCheck.out` | `System.out` | Where the harness prints: the console. |
| 102 | `EarthDepositsCheck.SIGMAS` | `3` | A sample's mean is the model's within this many of its own standard errors (the sample's, sd over the root of the fields): the same three standard deviations EarthTerrainCheck holds its draws to. |
| 105 | `EarthDepositsCheck.AREA_REL` | `1e-9` | A province or code area is the brute force's within this share, relative: a billionth (both sum the same cells, only in another order). |
| 108 | `EarthDepositsCheck.MULT_REL` | `1e-12` | A multiplier is the formula's within this share, relative: a million millionth (the same sums of a few terms). |
| 111 | `EarthDepositsCheck.MIN_POOLS` | `200` | The pools whose field counts and lists 6 compares, at least: 200 (spec 8.5's "fewer and bigger" holds on a world, not on a pool). |
| 114 | `EarthDepositsCheck.REACH_CELLS` | `6` | The cells round the founding site that 5 and 7 draw the fields of, each way: 6, 368 km, past 300 km and the cell the site stands in. |
| 117 | `EarthDepositsCheck.CONTROL_CELLS` | `2` | ...and the controls', each way: 2, 123 km, past their 50 km. |
| 120 | `EarthDepositsCheck.CONTROL_SEED` | `1` | The seed the controls' worlds are drawn with (spec-earth2-terrain 8.3's controls ran on several; one is enough to count). |
| 123 | `EarthDepositsCheck.TENTH` | `0.1` | A tenth: how much of what the study found a finding of 5 may leave (spec 8.5: "oil and iron all round them" to a handful). |
| 126 | `EarthDepositsCheck.KM` | `{ 50, 150, 300 }` | The distances 5 counts the fields within, in km (the study's: spec 8.3). |
| 129 | `EarthDepositsCheck.FIELD_RESOURCES` | `Arrays.stream(Resource.values()).filter(Resource : : inFields).toArray(Resource[] : : new)` | Every resource drawn in fields. |
| 135 | `EarthDepositsCheck.HALIFAX` | `new Spot("Halifax", 44.65, - 63.60, 758_809_812L)` | Jerus's two saves (spec-earth2-terrain 8.1): slot 1 and slot 2 as the study read them, by their seeds. |
| 136 | `EarthDepositsCheck.EASTERN_SHORE` | `new Spot("Eastern Shore", 45.17, - 61.72, 284_719_415L)` |  |
| 139 | `EarthDepositsCheck.HALIFAX_IRON` | `{ 45, 417, 941 }, HALIFAX_OIL = { 14, 155, 661 }, HALIFAX_COAL = { 2, 11, 20 }` | The study's counts within 50, 150 and 300 km (spec-earth2-terrain 8.3), by resource: iron, oil (land and sea together), coal. |
| 140 | `EarthDepositsCheck.SHORE_IRON` | `{ 72, 320, 699 }, SHORE_OIL = { 8, 182, 646 }, SHORE_COAL = { 0, 3, 15 }` |  |
| 143 | `EarthDepositsCheck.HALIFAX_OIL_LAND_150` | `53` | ...and, of Halifax's 155 oil fields within 150 km, the 53 on land (spec 8.3: "53 + 102"). |

### EarthFoundingCheck.java ([map](map/EarthFoundingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 91 | `EarthFoundingCheck.out` | `System.out` | Where the harness prints: the console. |
| 93 | `EarthFoundingCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | Where the game's own narration goes while a city is founded or played here (quietly()): nowhere. |
| 109 | `EarthFoundingCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 112 | `EarthFoundingCheck.STUDY` | `Map.of("Rotterdam", new double[] { 88, 9.2, Double.NaN, 0.6, 3.0 }, "Perth", new double...` | The study's card for each window (spec-earth 2): dry %, river and lake %, sea %, fresh water km, sea km - NaN where it gave none. |

### EarthTerrainCheck.java ([map](map/EarthTerrainCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 193 | `EarthTerrainCheck.SHARE_POINTS` | `2` | Each whole 15" cell's sea share of the plots drawn is the data's within this, in points, in the mean over a window (spec-earth 8: 2; the study measured 0.39 to 1.49). |
| 196 | `EarthTerrainCheck.SEED_CHANGES` | `0.10` | A second seed changes at least this share of a window's plots (spec-earth 8: 10%; the study measured 15.7 to 28.6%). |
| 199 | `EarthTerrainCheck.SEED_WATER_POINTS` | `0.5` | ...while a window's water, sea and fresh, moves no more than this, in points (spec-earth 8: 0.5; the study measured 0.1). |
| 202 | `EarthTerrainCheck.TILE_MS` | `0.3` | A tile's ground in no more than this, ms (spec-earth 8: 0.3; the study measured 0.09 to 0.20). |
| 205 | `EarthTerrainCheck.LANDMASS_TILE_MS` | `0.3` | A tile's landmass (ET5) in no more than this, ms, its cover kept: the data's cell at each of its 1,024 plots' blend points, the nearest land cell's for a dry plot on a cell with none (the median of... |
| 208 | `EarthTerrainCheck.CELLS_PASS_MS` | `1000` | The cells pass in no more than this, ms of the main thread's CPU since batch EC1 (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s on the wall clock). |
| 211 | `EarthTerrainCheck.WINDOW` | `World.CELL` | The window about each spot, in plots a side: a world cell, 61.44 km (spec-earth 3, "61 x 61 km about each spot"). |
| 214 | `EarthTerrainCheck.TIMED_TILES` | `32` | The tiles a side of each window's middle that are timed: 32, 30.7 km. |
| 217 | `EarthTerrainCheck.TILE_ROUNDS` | `3` | Rounds of the timed tiles after one to warm: 3, the median taken (the study's 3 repeats). |
| 220 | `EarthTerrainCheck.SEED` | `Founding.DEFAULT_WORLD_SEED, SECOND_SEED = Founding.DEFAULT_WORLD_SEED + 1` | The seed the windows are drawn with, and the second one section 4 draws them with. |
| 223 | `EarthTerrainCheck.SEAM_DEG` | `0.05` | How close to the 180th meridian Fiji's cells are held, in degrees: 0.05, twelve 15" cells (5.5 km) each side. |
| 226 | `EarthTerrainCheck.LAKE_AREA` | `0.10` | The small lakes clear of other water draw their own area within this share of it, over the windows: 10% (★E2: each blob covers its lake's area in expectation, and its fray varies it a few percent; ... |
| 229 | `EarthTerrainCheck.LAND_WITHIN` | `0.005` | The world's land on the full file - its cells' shares added - is the land the 4' layers put inside the world's square within this share: half a percent (★E2: sixteen points a 61 km cell, each its 7... |
| 233 | `EarthTerrainCheck.out` | `System.out` | Where the harness prints: the console. |
| 944 | `EarthTerrainCheck.STUDY_CLASSES` | `{ 55, 38, 151, 20, 66, 474, 219 }` | The study's count of the provinces by geology.py's rules (spec-earth 4): 474 basin, 151 fold belt, 66 shelf, 55 ocean, 38 shield, 20 rift and 219 other, in Geology.Kind's order. |
| 947 | `EarthTerrainCheck.STUDY_SHARES` | `{ 0.7, 17.8, 18.9, 2.2, 2.8, 35.1, 22.5 }` | The study's land in each class by the rules, Antarctica left out, in points (spec-earth 4), in Geology.Kind's order: 0.7 ocean, 17.8 shield, 18.9 fold belt, 2.2 rift, 2.8 shelf, 35.1 basin, 22.5 ot... |
| 950 | `EarthTerrainCheck.SHARE_WITHIN` | `0.5` | ...reproduced within this, in points: half a point (★E3: the study's are rounded to a tenth, and its land was its prototype's cells). |
| 953 | `EarthTerrainCheck.STUDY_Z` | `{ 0.888, 0.701, 0.751, 0.959, 0.846, 1.049 }` | The study's Z on the rules' classes (spec-earth 4), in Resource's order for the six in fields: iron 0.888, oil 0.701, stone 0.751, coal 0.959, copper 0.846, uranium 1.049. |
| 956 | `EarthTerrainCheck.STUDY_LIMESTONE_BASIN` | `0.886, STUDY_LIMESTONE_SHIELD = 0.493` | The study's limestone in basins and on shields on the rules' classes (spec-materials 2.4, out/earth-types.txt): .886 and .493. |
| 959 | `EarthTerrainCheck.LIMESTONE_WITHIN` | `0.005` | ...reproduced within this: half a point (★E3: the study's are rounded to three places). |
| 962 | `EarthTerrainCheck.SIGMAS` | `3` | A sampled figure is held to its expectation within this many standard deviations: 3 (★E3). |
| 965 | `EarthTerrainCheck.Z_ROUNDING` | `0.00005` | A Z on the full file is Geology's constant to its rounding, four decimals: half the last place. |
| 968 | `EarthTerrainCheck.E3_Z` | `{ 0.7219, 0.6033, 0.6915, 0.7884, 0.7322, 0.7518 }` | E3's Z on the hand table (0.7.117, before EG1), in Resource's order for the six in fields: iron 0.7219, oil 0.6033, stone 0.6915, coal 0.7884, copper 0.7322, uranium 0.7518. |
| 971 | `EarthTerrainCheck.E3_Z_WITHIN` | `0.0001` | ...replayed on today's measure within this: a place and a half of the last (the Sydney patch, EG1, moves a little land, and copper's 0.73226 is E3's 0.7322 and a bit). |
| 974 | `EarthTerrainCheck.ODDS_ROUNDING` | `0.000005` | An odds on the full file is Geology's constant to its rounding, six figures: half the last place, relative. |
| 977 | `EarthTerrainCheck.YILGARN_KM` | `150` | How far east of Perth the study's shield spot lies, in km: 150 (spec-earth 4, "150 km east of Perth (Yilgarn Block)"). |
| 980 | `EarthTerrainCheck.TYPE_DRAWS` | `200_000` | Synthetic fields drawn for each class to hold the types' odds: 200,000 (one standard deviation about 0.1 point). |
| 983 | `EarthTerrainCheck.SEA_OIL_CELLS` | `8` | How far about Rotterdam's site the North Sea's cells are looked at for oil at sea, in cells: 8 (490 km), within the fixture's bands. |
| 1434 | `EarthTerrainCheck.COVER_POINTS` | `SHARE_POINTS` | Each kind's share of a 1-degree cell's ground drawn is the data's within this, in points: SHARE_POINTS, the bound the sea's share of a 15" cell is held to (spec-earth 8); E7 measured 0.02 to 0.34. |
| 1437 | `EarthTerrainCheck.ICE_CELLS` | `{ { 64, - 17 }, { 61, - 46 } }, DESERT_CELLS = { { 16, 0 }, { 44, 56 } }` | The 1-degree cells each kind is held on, {lat0, lon0}, on the full file: ice at Vatnajokull and on south Greenland's margin, the desert at the Sahel's edge and on the Ustyurt by the Aral Sea - each... |
| 1440 | `EarthTerrainCheck.SHEET` | `{ 72, - 40 }` | Greenland's sheet's middle, EarthFoundingCheck 3's spot on ice. |
| 1443 | `EarthTerrainCheck.MARGIN` | `{ 61.48, - 45.68 }` | A spot on south Greenland's ice margin whose snapped site lies on grass in a 2' cell with the ice bit - the bit's rule (E4's) refused it, the plot's does not (found by E7's probe, the default seed;... |
| 1446 | `EarthTerrainCheck.OFF_THE_ICE` | `{ 61.42, - 45.62 }` | A spot on the ice there whose site the snap finds off it, on dry ground over a kilometre away (E7's probe). |
| 1449 | `EarthTerrainCheck.SAHARA` | `{ 23, 10 }` | The Sahara's spot, EarthFoundingCheck 3's desert. |
| 1598 | `EarthTerrainCheck.SHALLOW_POINTS` | `COVER_POINTS` | The shallow share of a window's sea drawn is the data's within this, in points: COVER_POINTS, the bound ice's and the desert's share of a cell is held to (the sea-weighted share of cells whose coas... |
| 1601 | `EarthTerrainCheck.TWO_COPIES_POINTS` | `0.5` | The tile rule (draw()) and the point rule (classAt()) split the sea alike on all but this share of the plots both call sea, in points: half a point (the lattice blends the shares between points 120... |
| 1604 | `EarthTerrainCheck.SPOT_REACH` | `5` | How far round a spot the Narrows' and the Basin's plots are all held to their class, in plots: 5 (150 m, a Narrows is a few hundred metres across). |
| 1607 | `EarthTerrainCheck.NARROWS` | `{ 44.67, - 63.59 }, BASIN = { 44.70, - 63.64 }` | The Halifax Narrows between Bedford Basin and the harbour, and Bedford Basin's middle (the fixture holds the Narrows' tiles: tools/earth/fixture.py NARROWS). |
| 1781 | `EarthTerrainCheck.RELIEF_POINTS` | `SHARE_POINTS` | Each relief class's share of a window's dry ground drawn is the data's within this, in points, for a window of RELIEF_REF_PLOTS dry plots: SHARE_POINTS, section 15's bound - and more for a window w... |
| 1784 | `EarthTerrainCheck.RELIEF_REF_PLOTS` | `(long) WINDOW * WINDOW` | The dry plots a 61 km window of land holds: 2,048 squared, 4,194,304 - the size RELIEF_POINTS is the bound for. |
| 1787 | `EarthTerrainCheck.RELIEF_COPIES_POINTS` | `1.0` | The relief's two copies (the tile's drawRelief() and the point's reliefOf()) give a dry plot the same class on all but this share of the plots both draw dry, in points: 1 (the lattice blends the sh... |
| 1790 | `EarthTerrainCheck.FAR_POINTS` | `2 * RELIEF_POINTS` | A far node's relief shares are the data's cells' at its dry pixels within this, in points: twice RELIEF_POINTS (a node's 16,384 pixels are a sample, a pixel a hash's draw against the share), where ... |
| 1791 | `EarthTerrainCheck.FAR_DRY_LEAST` | `2000` |  |
| 1794 | `EarthTerrainCheck.FLOOR` | `{ 6.25, - 75.57 }, VALLEY = { 6.25, - 75.58 }` | Medellin's valley floor, the centre of the city (the study's 2.3: 98% flat in a 3 x 3 of 15" cells, 1,482 m), and the middle of the fixture's window about it (tools/earth/fixture.py MEDELLIN, one 1... |
| 1797 | `EarthTerrainCheck.SUMMIT` | `{ 27.99, 86.93 }` | Everest's summit area: snow and ice on all its high ground (the study's 12, 7), the rock between its glaciers the only dry land. |
| 1800 | `EarthTerrainCheck.HILLS` | `{ VALLEY, SUMMIT, { 46.7, 10.3 }, { 22.3, 114.2 }, { - 16.5, - 68.1 }, { 27.7, 85.3 } }` | The full file's hilly spots, {lat, lon}: Medellin, Everest's, the Upper Engadin, Hong Kong (a steep coast), La Paz and Kathmandu - their classes in the study's table of 2.3. |
| 1801 | `EarthTerrainCheck.HILL_NAMES` | `{ "Medellin", "Everest", "the Upper Engadin", "Hong Kong", "La Paz", "Kathmandu" }` |  |
| 1804 | `EarthTerrainCheck.FLOOR_REACH` | `5, FLOOR_WIDE = 17` | How far round the floor's spot the plots are all held FLAT, in plots: 5 (150 m); and the share of the plots within FLOOR_WIDE (17: 510 m) that are flat, at least. |
| 1805 | `EarthTerrainCheck.FLOOR_FLAT` | `0.95` |  |
| 2350 | `EarthTerrainCheck.SHELVES` | `{ { 52.0, 3.0 }, { - 32.0, 115.4 }, { 28.5, - 94.0 }, { 27.0, 50.5 } }` | Four coasts of the full file, {lat, lon}, each a 61 km window with sea of both depths: the North Sea at the Flemish banks, the shelf off Perth, the Texas shelf off Galveston and the Gulf off Ras Ta... |

### EducationCheck.java ([map](map/EducationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `EducationCheck.OUT` | `System.out` |  |
| 64 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### EnsembleCheck.java ([map](map/EnsembleCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 213 | `EnsembleCheck.POLICY_ENUM` | `Pattern.compile("\\benum\\s+Policy\\s*\\{([^}]*)\\}")` |  |
| 214 | `EnsembleCheck.POLICIES_LIST` | `Pattern.compile("(?m)^POLICIES\\s*=\\s*\\[([^\\]]*)\\]")` |  |
| 216 | `EnsembleCheck.QUOTED` | `Pattern.compile("\"([^\"]*)\"\|'([^']*)'")` | A name in double or single quotes, as either half lists its policies. |
| 306 | `EnsembleCheck.BASELINE_NAME` | `Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.md")` |  |
| 308 | `EnsembleCheck.CRITERIA` | `Pattern.compile("(?m)^criteria passed:\\s*(\\d+)\\s*/\\s*(\\d+)\\b")` | A stored score's verdict line: "criteria passed: N / M". |
| 309 | `EnsembleCheck.JSON_BLOCK` | `Pattern.compile("(?s)'''json\\s*\\R(.*?)\\R\\s*'''")` |  |
| 396 | `EnsembleCheck.VERDICT` | `"criteria passed: 5/6(vacuous, not counted: 0); missing runs: none; short runs: none"` | A canned page's verdict line, for the reader's fixtures. |
| 398 | `EnsembleCheck.JSON` | `""" { "seeds": 4, "policies": { "ALPHA": {"final_pop_median": 1000.5, "runs_with_findin...` | ...and its JSON block, sound as it stands; a fixture edits it into a page the reader should refuse. |
| 485 | `EnsembleCheck.BROKEN` | `""" package ham.citybuildersim; final class EnsembleCheckBroken { static void call() { ...` | Calls a method that no class has: the change the guard exists for, made by hand. |
| 494 | `EnsembleCheck.SOUND` | `""" package ham.citybuildersim; final class EnsembleCheckSound { static Class<?> call()...` | Calls a class that is there. |

### ExchangeCheck.java ([map](map/ExchangeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 86 | `ExchangeCheck.W` | `DebtManager.WORLD_BASE_RATE` |  |
| 87 | `ExchangeCheck.N` | `Equity.COMPANIES.length` |  |
| 88 | `ExchangeCheck.HURDLE` | `W + Equity.FOREIGN_PREMIUM` |  |

### ExchangeRateCheck.java ([map](map/ExchangeRateCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 121 | `ExchangeRateCheck.TRADE` | `3_000, GDP = 6_000` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one. |
| 124 | `ExchangeRateCheck.SEED` | `20_261_021L` | The market's seed in the sections that draw one; the others are quiet. |
| 127 | `ExchangeRateCheck.OLD_PULL` | `.004` | The old pull toward parity, REVERSION, retired by XR1 (WHAT XR1 RETIRED): a month's share of the distance it closed. |
| 170 | `ExchangeRateCheck.FEES` | `{ 1, 1, 1, 1 }` | The four fees' prices a fixture's basket is fed: all at 1. |
| 172 | `ExchangeRateCheck.RENT_HEAVY` | `{.15,.60,.10,.05, 0 }, RENT_HEAVY_FEES = {.04,.03,.02,.01 }` | Spending that puts rent at 60% of the basket (groceries 15, meals 10, luxury 5, services 10 - the fee lines add to it), and spending that puts it at 30%. |
| 173 | `ExchangeRateCheck.RENT_LIGHT` | `{.30,.30,.20,.05, 0 }, RENT_LIGHT_FEES = {.06,.04,.03,.02 }` |  |

### ExpectationsCheck.java ([map](map/ExpectationsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 109 | `ExpectationsCheck.TARGET` | `.03` | The target the bare fixtures aim at: not the default, so nothing reads the default by accident. |

### FloatLinkCheck.java ([map](map/FloatLinkCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 189 | `FloatLinkCheck.audits` | `new Tally()` |  |
| 192 | `FloatLinkCheck.ran` | `new boolean [ 10 ]` | Which of sections 3-5 have run, for section 6 to know what it can ask of the months they pressed. |
| 207 | `FloatLinkCheck.X_HOLDERS` | `new Worst()` | Each holder was paid its share of every coupon: households, businesses, the world, the central bank. |
| 209 | `FloatLinkCheck.X_TREASURY` | `new Worst()` | The cash coupons the pieces paid are what the treasury paid on its interest line, the uplift paid beside it. |
| 211 | `FloatLinkCheck.X_BOOKS` | `new Worst()` | The debt manager's month and the statements' lines are the sums of the pieces' months. |
| 213 | `FloatLinkCheck.X_NOTCASH` | `new Worst()` | The bridge's not-cash row is the expense the cash never paid. |
| 215 | `FloatLinkCheck.X_FOOTS` | `new Worst()` | The bridge foots through its journal and its residual. |
| 217 | `FloatLinkCheck.X_BRIDGE` | `new Worst()` | The bridge's residual is the bank's coupon timing and nothing else: the uplift is not in it. |
| 219 | `FloatLinkCheck.X_EARNED` | `new Worst()` | The walk from EARNED to the budget leaves what EARNED never counted - the coupons paid beyond the bank - and nothing of the uplift. |
| 718 | `FloatLinkCheck.floaters` | `new ArrayList<>()` |  |
| 719 | `FloatLinkCheck.linkers` | `new ArrayList<>()` |  |
| 726 | `FloatLinkCheck.CAP_TAIL` | `.0025` | A tail the harness caps the sale at, to cause one: a sale far past the bids stops at the cap. |
| 728 | `FloatLinkCheck.SALE_MARGIN` | `new Worst(), SALE_INDEX = new Worst(), SALE_CASH = new Worst(), SALE_BOOKS = new Worst(...` |  |
| 1146 | `FloatLinkCheck.M_SHARES` | `new Worst()` |  |
| 1147 | `FloatLinkCheck.FX_ZERO` | `new Worst()` |  |

### FoodProcessingCheck.java ([map](map/FoodProcessingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `FoodProcessingCheck.MEAT_WORKS` | `"Meat Works"` |  |
| 64 | `FoodProcessingCheck.SNACKS` | `"Snack & Oils Plant"` |  |
| 65 | `FoodProcessingCheck.BOTTLING` | `"Bottling Plant"` |  |

### ForeignCheck.java ([map](map/ForeignCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 62 | `ForeignCheck.TRADED_BEFORE` | `24` | The months a fixture trades before its test month: 24, the currency's settling months until XR1, kept so the fixtures are the cities they always were. |

### ForestryCheck.java ([map](map/ForestryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `ForestryCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 87 | `ForestryCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it. |
| 123 | `ForestryCheck.F` | `Resource.FOREST` |  |
| 126 | `ForestryCheck.M3_A_KM2` | `World.FOREST_M3_PER_KM2` | Timber a square kilometre of forest holds: the world's (World.FOREST_M3_PER_KM2, Canada's 137 m3 a hectare). |
| 136 | `ForestryCheck.GOODS` | `{ Good.SAWLOGS, Good.PULPWOOD }` | Wood's two goods, the study's 5.3: import and export price, D$ thousands a cubic metre. |
| 138 | `ForestryCheck.STUDY_IMPORT` | `{.110,.090 }` | ...each one's import price (pulpwood's widened from 70, the study's 1.2)... |
| 140 | `ForestryCheck.STUDY_EXPORT` | `{.065,.035 }` | ...and its export price. |
| 142 | `ForestryCheck.FREIGHT_SHARE` | `.375` | Good.java's rule: freight three eighths of the wedge. |
| 144 | `ForestryCheck.GREEN_WOOD` | `.9` | Green roundwood, tonnes a cubic metre (spec 2.3, est.). |
| 146 | `ForestryCheck.IN_GROUND` | `.02` | The share of forest's pulpwood price its standing timber sells for in the ground (the study's 8). |
| 212 | `ForestryCheck.ROWS` | `{ Forestry.LOGGING_CAMP, Forestry.HARVESTING_OPERATION, Forestry.TREE_PLANTATION, Fores...` | Forestry's rows, by their permanent ids, in the study's 5.4 order and the woodlot after. |
| 215 | `ForestryCheck.STUDY_COSTS` | `{ 3_000, 15_000, 2_000 }` | The study's 5.4 for the first three: cost, D$ thousands all in. |
| 217 | `ForestryCheck.STUDY_POSTS` | `{ 11, 18, 5 }` | ...posts. |
| 219 | `ForestryCheck.STUDY_SAWLOGS` | `{ 1_000, 6_250, 0 }` | ...sawlogs a month, m3 (and as much pulpwood). |
| 221 | `ForestryCheck.STUDY_RUNS` | `{ 61, 217, 24 }` | ...its running cost a month, D$ thousands ("Runs"). |
| 223 | `ForestryCheck.STUDY_TABLE` | `{ { 1.3, 1.3, 4.6, 4.6 }, { 2.7, 2.7, 6.9, 6.9 }, { - 1.2, - 1.2, - 1.2, - 1.2 } }` | The study's 5.5 table, % of the cost a month, cases A to D. |
| 225 | `ForestryCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 227 | `ForestryCheck.PLANTATION_HA` | `1_000, WOODLOT_HA = 100` | A plantation's hectares (the study's 5.4) and a woodlot's (A12). |
| 229 | `ForestryCheck.SINCE` | `{ 1900, 1973, 1900, 1900 }` | The year each first existed: 1900 - standing before the start - but the Harvesting Operation, 1973 (research-design.md E14, PIKA 75). |
| 404 | `ForestryCheck.TOWN_KM2` | `1_000` | The ground a forest town is restated to hold past its founding, km2: enough for a Harvesting Operation's 109 km2 of forest at the default world's share. |
| 624 | `ForestryCheck.MOCKUP_FULL` | `25_400_000, MOCKUP_CUT = 75_000` | The study's mockup 4 (its 5.2): the natural woods' full stock, m3, and the month's cut. |
| 626 | `ForestryCheck.MOCKUP_SETTLES` | `.291, MOCKUP_PROTECTED =.40` | ...where they settle: 29.1% standing, and at 40% with 40% protected (spec-zoning 3.3; spec 2.11). |

### FoundingYardCheck.java ([map](map/FoundingYardCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 108 | `FoundingYardCheck.YARD` | `Game.FOUNDING_YARD` | The builders' yard: what every city is founded with. |
| 111 | `FoundingYardCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the village's own order, and the second builders' building of sections 5, 6 and 7. |
| 114 | `FoundingYardCheck.BANK` | `"Commercial Bank"` | The founding bank, whose ground the yard's is added to. |
| 117 | `FoundingYardCheck.NO_YARD_MONTHS` | `3` | The months a city with no yard is played: long enough for a month's work to show it is not done. |
| 135 | `FoundingYardCheck.FIRST_SHOP_MONTHS` | `36` | The months a hands-off founded village is given to open its first shop: three years. |

### FreightViewCheck.java ([map](map/FreightViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 110 | `FreightViewCheck.CENT` | `0.01 / 1000` | A cent, in the game's money: D$0.01, and the money is in D$ thousands. |
| 117 | `FreightViewCheck.K` | `Ports.Cargo.values().length` | The kinds of cargo a berth handles: Ports.Cargo's count. |

### FundLedgerCheck.java ([map](map/FundLedgerCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 314 | `FundLedgerCheck.ISSUER` | `"Mining"` | The issuer of section 3's one bond: a sector's name, which BondMarket.writeDown() finds an issuer's bonds by. |

### GatewayCheck.java ([map](map/GatewayCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 138 | `GatewayCheck.COAST_TRACK_TIMES` | `10` | The coast fixture's railway: each track type's count times this, 10 - enough track that the railway's gate arm reaches the coast and waits at the sea (section 16). |
| 141 | `GatewayCheck.COAST_SAVED_AT` | `100` | The month section 21 saves the coast city at: past the month its main line first runs on beyond an arm that waits at the sea, between 55 and 60 on this world (CC1-M's loads at every fifth month: th... |
| 144 | `GatewayCheck.COAST_HELD` | `10` | The months section 21 plays past MapCheck.MONTHS with the counts held. |
| 147 | `GatewayCheck.HALIFAX_LAT` | `44.656780322078724, HALIFAX_LON = - 63.613890005401984` | Jerus's founding site, Halifax (section 17), in degrees. |
| 150 | `GatewayCheck.MOST_HIGHWAYS` | `20` | The most Elevated Highways section 19 tries when it looks for the count that reaches a street. |
| 153 | `GatewayCheck.EPS` | `1e-9` | The tolerance of a position in plots or pixels, well below anything either measures. |
| 194 | `GatewayCheck.GROUND_BEFORE_SL1` | `{ 430_556, 2_368_060, 2_152_782, 107_639 }` | The four sea terminals' ground in square feet, ids 94-97, as it was before SL1 (2026-10-28, main at 0.8.66): a Tanker Terminal, a Bulk Terminal, a Container Terminal and a General Cargo Terminal. |
| 843 | `GatewayCheck.VIEW_W` | `1000, VIEW_H = 600, LABEL_W = 110, LABEL_H = 13` | The view and the label it is tried with: 1,000 x 600 pixels, a label 110 wide and 13 high above its baseline. |
| 905 | `GatewayCheck.LABEL_BOTH_W` | `LABEL_W * LandMap.WORLD_BOTH_WORDS.length() /(double) LandMap.WORLD_WORDS.length()` | The shared label's width in the harness: WORLD_BOTH_WORDS in the font that makes WORLD_WORDS LABEL_W wide (monospaced: by the characters). |

### GlobeCheck.java ([map](map/GlobeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 204 | `GlobeCheck.POINTER_PX` | `1e-3` | A zoom keeps the point under the pointer under it to within this, in pixels: a thousandth (★E8) - the solver's rounding, nothing a player sees. |
| 207 | `GlobeCheck.ROUND_TRIP_M` | `1e-6` | The orthographic round trip holds a point to within this, in metres: a micrometre (★E8) - at the limb the inverse loses digits, so the planet's points are held within LIMB_DEG of the centre. |
| 210 | `GlobeCheck.LIMB_DEG` | `89` | ...a point on the planet within this many degrees of the view's centre: 89, the limb's last degree left out, where the disc squeezes 111 km of ground into 17 m. |
| 213 | `GlobeCheck.ROUNDS` | `100_000` | Random views and points the projection is tried on: 100,000. |
| 216 | `GlobeCheck.REACH_PARTING_PX` | `1.25` | At REACH_KM with the roll eased, the plane and the globe part by under this, in pixels: 1.25 (★E8) - the study's 1.16 px, measured with the globe scaled to the plane's mean scale there (globe-proto... |
| 219 | `GlobeCheck.BEARINGS` | `{ 0, 45, 90, 135, 180 }` | The bearings a view away from the city is looked at, degrees: the study's five. |
| 222 | `GlobeCheck.TABLE_KM` | `{ 100, 300, 500, 1000 }` | The distances the study's table measured the swap at, km (spec-globe 3.3). |
| 225 | `GlobeCheck.CITIES` | `{ { 44.65, - 63.57 }, { 14.60, 120.98 } }` | The study's cities, its boards' (spec-globe 1): Halifax and Manila. |
| 228 | `GlobeCheck.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding globe's canvas, pixels: 560 x 270 (FoundingScreen's GLOBE_W and GLOBE_H; spec-globe 3.2's second column). |
| 231 | `GlobeCheck.TURN_PX` | `0.1` | A turn of the globe of this many pixels flips the forest on no more than this share of the dry pixels: a tenth (★E8) - the dither's cells are at least a pixel each way, so a point moving a tenth of... |
| 234 | `GlobeCheck.FRAME_MS` | `100` | A frame of the expanded map from the planet's picture in no more than this of the main thread's CPU, ms: 100 (spec-globe 3.6 measured 96 to 102 ms on one cloud thread, 50 on two, and called that li... |
| 237 | `GlobeCheck.FRAME_ROUNDS` | `10` | Frames timed, the least taken, after as many to warm. |
| 245 | `GlobeCheck.FIXTURE_PRINTS` | `Map.of("Rotterdam", 0xed2ff708L, "Perth", 0x0b1d7f68L, "Paris", 0xc3979824L, "Fiji", 0x...` | The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera (probe E8Fingerprint on its build, 19f1cad), but for wha... |
| 248 | `GlobeCheck.FULL_SPOTS` | `{ { 51.90625, 4.48125 }, { 44.65, - 63.57 }, { 14.60, 120.98 }, { 72, - 40 }, { - 77.85...` | The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. |
| 255 | `GlobeCheck.FULL_PRINTS` | `{ 0xf291ea65L, 0x208e9728L, 0xd85a163cL, 0x734fdb04L, 0xf5f461adL, 0xbdc3b5b7L, 0x864aa...` | ...and their nodes' CRC32, as 0.7.131 drew them but for the shallow sea (ET1, 2026-10-24: World.SHALLOW is now a class the far nodes hold, so the figures moved). |
| 259 | `GlobeCheck.out` | `System.out` | Where the harness prints: the console. |
| 267 | `GlobeCheck.R` | `EarthProj.RADIUS_M` | The planet's radius in metres (EarthProj.RADIUS_M), which the sections measure arcs and the disc by. |
| 534 | `GlobeCheck.NEAR_KM` | `GlobeFrame.HANDOFF_KM / 4` | A near scale, km a pixel: a quarter of the handoff's, four plots a pixel (MS1's views closer than the handoff, on the plane and past its reach). |
| 537 | `GlobeCheck.STEP_PX` | `150` | The pixels a drag outward takes at a time in section 2b: 150 - at NEAR_KM, 18 km, so the reach is crossed in about seventeen. |
| 540 | `GlobeCheck.STEPS_MOST` | `100` | The most drags outward section 2b takes: 100, far past twice the reach at NEAR_KM. |
| 543 | `GlobeCheck.FAR_SPOTS` | `{ { 14.60, 120.98 }, { - 31.95, 115.86 }, { - 17.0, 179.9 }, { - 17.0, - 179.9 }, { 14....` | The spots section 2b zooms in on far from either city, {lat, lon}: Manila, Perth, and a spot each side of the antimeridian (the date line) at 17 degrees south and Manila's latitude. |
| 552 | `GlobeCheck.CROSSING_HANDOFF_SHARE` | `0.01` | The share of a patch's pixels whose class the plane and the GROUND tier's far rule may draw differently at the handoff, over a patch at the reach's edge: 1 percent (measured 0.15 to 0.65 percent ov... |
| 558 | `GlobeCheck.CROSSING_NEAR_SHARE` | `0.06` | ...and at NEAR_KM, where the plane draws a far node of four plots a pixel and the camera the far rule at its least stride, DITHER_STRIDE (16 plots), so a coast or a river parts by up to a pixel and... |
| 890 | `GlobeCheck.JERUS_KM` | `2.0 / 120` | Jerus's near scale, km a pixel: 2 km in the scale bar's 120 px (ui/MapView.SCALE_BAR_PX), 1.8 px a plot - the view of his two screenshots. |
| 897 | `GlobeCheck.NEAR_PAST_SAME` | `1 - 1e-5` | At one plot a pixel (his scale and the closest) the classes the plane's nodes give through the camera are the plane's own, plot for plot: the share of a view's pixels, either side of the reach, who... |
| 905 | `GlobeCheck.NEAR_PAST_FOLDED` | `0.98` | At 2, 4 and 8 plots a pixel - where the plane draws a far node a pixel of which is one sample of its 2, 4 or 8 plots, and the camera reads that node - the share of pixels whose class is the class o... |
| 908 | `GlobeCheck.NEAR_PAST_CLASSES` | `0.90` | ...and with the forest and the grass told apart: 90 percent (measured 91.6 to 98.3% on the fixture, 96.7 to 97.6% on the full file). |
| 911 | `GlobeCheck.NEAR_PAST_SPECKLE` | `0.001` | The share of pixels whose class differs from both horizontal neighbours the reprojected nodes may draw over the plane's own: a tenth of a percent - the camera reads the same node pixels the plane d... |
| 914 | `GlobeCheck.FAR_SPECKLE_AT_LEAST` | `0.05` | The far rule's speckle on a forested window the fixture must cause, for the comparison to mean anything: 5 percent of the pixels (measured 15.2% over Rotterdam and 11.9% over Paris at Jerus's scale... |
| 917 | `GlobeCheck.LAKE_IS_A_DISC` | `0.02` | A small lake the far rule draws as a disc is one whose pixels part from the equal-area disc about their centroid by no more than this share of their number: 2 percent (the far rule's discs measured... |
| 920 | `GlobeCheck.LAKE_IS_FRAYED` | `0.10` | ...and the plane's frayed one parts by at least this: 10 percent (its fray is BLOB_FRAY of its own noise: measured 0.10 to 0.23 over the lakes picked). |
| 923 | `GlobeCheck.LAKE_SAME_IOU` | `1 - 1e-3` | The reprojected lake is the plane's: their pixels' intersection over their union at one plot a pixel, within a thousandth of 1 (the share of section 2c's first figure). |
| 926 | `GlobeCheck.LAKE_DISC_IOU` | `0.9` | ...and the far rule's disc is not the plane's lake: at most this (measured 0.55 to 0.85 over the lakes picked). |
| 929 | `GlobeCheck.LAKE_LEAST_PLOTS` | `12` | Small lakes at least this many plots across the radius are tried (smaller are a few pixels at his scale), the least of them picked in each source: 12 plots (0.4 km2), four of them. |
| 930 | `GlobeCheck.LAKES_PICKED` | `4` |  |
| 933 | `GlobeCheck.LAKE_VIEW_PX` | `256` | A lake view's side, pixels: 256, the lake's radius a quarter of it. |
| 936 | `GlobeCheck.NEAR_SCALES` | `{ JERUS_KM, NEAR_KM / 2, NEAR_KM, NEAR_KM * 2, GlobeFrame.leastKmPx() }` | The scales the views straddling the reach are drawn at, km a pixel: Jerus's (1.8 px a plot), then 2, 4 and 8 plots a pixel - NEAR_KM among them - and the closest, 16 px a plot. |
| 946 | `GlobeCheck.NO_NODE` | `- 1` | What the camera draws of the plane's nodes (GlobeFrame.ground()) over a w x h view, a class a pixel (MapTiles.nodeTerrain()), NO_NODE where it drew none, OFF where it drew the far rule's place (bey... |
| 1257 | `GlobeCheck.STREAK_LAG_PX` | `4` | The offset the ground's change rates are read at, in pixels: 4 - short enough that the share of pixel pairs that differ grows with the offset's length across a patch of the 2 km view's 20 to 60 px. |
| 1260 | `GlobeCheck.STREAK_VIEW_PX` | `512` | The side of the views the ground's streak is read over, in pixels: 512 (the fixture's windows hold the 2c view of 1,345 x 806 at his scale, and this is a fraction of it). |
| 1263 | `GlobeCheck.STREAK_KM` | `{ GlobeFrame.REACH_KM *(1 + 1e-6), 2000, 4000, 6000, 8000, 10000, 12000 }` | The distances the city is placed from the spot, in km: just past the reach (the spot's reference, where Lambert's plane is true to a thousandth) and 2,000 to 12,000 km, where it squashes the ground... |
| 1266 | `GlobeCheck.STREAK_READ_TOL` | `0.10` | The measure reads a field of no stretch as 1 within this, and a stretched one at most this above its stretch: 0.10 - set before the first run. |
| 1269 | `GlobeCheck.STREAK_READ_FLOOR` | `0.65` | ...and a stretched field at least this share of its stretch: 0.65 - the measure's own reading, set from the calibration and not to pass a case: it reads 4 to 1 at 0.683 of it on the equal-area fiel... |
| 1272 | `GlobeCheck.STREAK_PHI_DEG` | `5` | ...and the field's direction within this many degrees of the stretch's, for a stretch of at least 2: 5. |
| 1275 | `GlobeCheck.STREAK_SEEDS` | `8` | The synthetic fields a stretch's response is averaged over, and the fields of the plane's noise a placement's: 8 (one field's E varies by a tenth). |
| 1278 | `GlobeCheck.STREAK_TRACKS` | `0.15` | The plane's noise under the pixels is stretched as the measure reads Lambert's projection stretch it: its E over its E at the reach is within this share of response(sigma) / response(1), at every p... |
| 1281 | `GlobeCheck.STREAK_BEARING_DEG` | `10` | Where the distortion is at least STREAK_FROM_SIGMA the drawn ground's streak runs along the line from the spot to the city to within this many degrees: 10. |
| 1284 | `GlobeCheck.STREAK_FROM_SIGMA` | `1.5` | ...the shape distortion from which the streak's direction and its rise are asserted: 1.5 (the patches half again as long one way as the other). |
| 1287 | `GlobeCheck.STREAK_SHOWN` | `1.5` | A ground read at this E / E_ref or more is visibly streaked: 1.5 - the measure reads Jerus's screenshot, Goose Bay from Singapore, at 2.4, and a window at 12,000 km at 1.6 to 2.0. |
| 1290 | `GlobeCheck.STREAK_STRETCHES` | `{ 1, 2, 4 }` | The stretches the measure is calibrated on: 1, 2 and 4 to 1. |
| 1293 | `GlobeCheck.STREAK_TURNED_S` | `3, STREAK_TURN_DEG = 30` | ...and once a stretch of STREAK_TURNED_S to 1 turned STREAK_TURN_DEG degrees on the screen: 3 to 1, 30 degrees. |
| 1296 | `GlobeCheck.STREAK_SEED` | `0x5EED_2DL` | The synthetic fields' noise seed. |
| 1299 | `GlobeCheck.SINGAPORE` | `{ 1.2903, 103.8519 }, HAPPY_VALLEY = { 53.3017, - 60.3261 }` | Singapore (Jerus's city, the brief's) and Happy Valley-Goose Bay, Labrador, whose place Goose Bay (GOOSE_BAY_NORTH_KM due north of it) is. |
| 1302 | `GlobeCheck.GOOSE_BAY_NORTH_KM` | `42` | ...the distance due north of Happy Valley-Goose Bay Jerus's hover named, in km: 42. |
| 1305 | `GlobeCheck.TROMSO` | `{ 69.65, 18.96 }` | A city where the plane's up is some degrees from north a little way out, for the roll's sign: Tromso (the view is put the reach's edge due east of it). |
| 1390 | `GlobeCheck.RESPONSES` | `new java.util.HashMap<>()` | What response() has read, by stretch and turn. |
| 1421 | `GlobeCheck.SPHERE_SEEDS` | `4` | The synthetic fields of the sphere's noise a placement's E is averaged over: 4 of the STREAK_SEEDS (its noise costs twice the plane's a pixel). |
| 1618 | `GlobeCheck.SEAM_TOL` | `0.15` | The change rate of pixel pairs across the hand-over's line (or the band's far edge), over the rate on one side, may exceed the same figure of the same pixels drawn by the chart alone by this share:... |
| 1621 | `GlobeCheck.SHARE_TOL` | `0.05` | The class shares of the ground either side of the hand-over's line (water, forest, grass, the rest), pooled over the views, differ by at most this: 0.05. |
| 1624 | `GlobeCheck.SEAM_BEARINGS` | `{ 0, 90, 180, 270 }` | The bearings the city is put at from the view's centre, degrees, to pool a seam over: the four screen directions - a pixel is on the plane's side in half of them and the chart's in the other, so th... |
| 1738 | `GlobeCheck.SEAM_NORTH_KM` | `60` | How far north of Jerus's hover (GOOSE_BAY_NORTH_KM from Happy Valley) the seam views near Goose Bay are centred, km: 60 - his own spot lies at the edge of Hamilton Inlet's 40 km wide "river" run of... |
| 1741 | `GlobeCheck.SEAM_SHIFTS` | `{ - 0.5, 0.5 }` | The shifts along the line the views are taken at, as shares of the view's width: either side of the centre, so the ground the views hold is more than the one window's centre. |
| 1744 | `GlobeCheck.SEAM_SHOWN` | `1.5` | A seam made on purpose (the sphere's noise switched on at the hand-over with no band) reads at least this: 1.5 - so the measure can see one. |
| 1782 | `GlobeCheck.SPREAD_POINTS` | `1<<16` | The points the sphere's noise spread is read over: 2^16. |
| 1785 | `GlobeCheck.SPREAD_TOL` | `0.02` | The sphere's noise's standard deviation is within this share of the plane's: 0.02. |
| 1788 | `GlobeCheck.RANK_TOL` | `0.01` | ...and the share of its points under the ranking table's value for p (EarthTerrain.below()) is within this of p, and of the plane's: 0.01. |
| 1791 | `GlobeCheck.RANK_P` | `{ 0.05, 0.25, 0.5, 0.75, 0.95 }` | The ranks the shares are read at. |
| 2318 | `GlobeCheck.RELIEF_SIGMAS` | `4, RELIEF_ROUNDING = 0.03` | The picture's relief shares are the data's within this many binomial spreads of the dry texels drawn (a texel is a hash's draw against the 4' block's share, as a far node's pixel is) and a rounding... |
| 2456 | `GlobeCheck.VERTICES_MOST` | `45_000` | The most vertices the whole planet's borders may draw: 45,000 - measured 14,814 on the 1,389 x 868 view facing Halifax at the 4 km level, 2026-10-28, against 56,914 at the file's own; three times t... |
| 2459 | `GlobeCheck.NAME_PX` | `6.5, FIGURES_PX = 5.7` | The widths the harness gives the labels, pixels a character: the board's 11 px sans name (6.5) and its 9.5 px mono figures (5.7) - the screen measures its own fonts. |
| 2460 | `GlobeCheck.NAME_W` | `s -> s.length() * NAME_PX, FIGURES_W = s -> s.length() * FIGURES_PX` |  |
| 2463 | `GlobeCheck.YEAR` | `2020` | The year the nations' figures are read for: 2020, a published one (the marks are read at 2000, 2025 and 2030 too). |
| 2466 | `GlobeCheck.D_PER_USD` | `1.16` | The game dollar's price in US$ the cards are given: D$1.16 a US$ (a figure to carry through, not a dial). |
| 2469 | `GlobeCheck.VIEWS` | `{ { 0, 0, 0 }, { 50, - 30, 10 }, { 50, 10, 3 }, { CITIES [ 0 ] [ 0 ], CITIES [ 0 ] [ 1 ...` | The views of 9 and 10, {lat, lon, km a pixel} (0: the whole planet facing the city), and their names: the planet, the North Atlantic, Europe and the GROUND tier at the city. |
| 2470 | `GlobeCheck.VIEW_NAMES` | `{ "the whole planet", "the North Atlantic at 10 km a pixel", "Europe at 3 km a pixel", ...` |  |
| 2840 | `GlobeCheck.RANK` | `Pattern.compile("(\\d+)(st\|nd\|rd\|th) of(\\d+)")` |  |
| 2946 | `GlobeCheck.EUROPE` | `{ { 54.0, - 2.0 }, { 53.4, - 8.0 }, { 64.9, - 18.6 }, { 60.5, 8.5 }, { 46.6, 2.3 }, { 4...` | Twelve partners of a Halifax city, {lat, lon}: the ten nodes of Europe that are partners at 1,000 sold and 500 bought, Poland at 100 and 100 (under MIN_LANE) and Sweden at 200 and 100 (at it). |
| 2950 | `GlobeCheck.TRADE_ROUNDS` | `31, TRADE_WARM = 300` | The rounds a trade timing takes the median of (31), after as many as it takes to warm the JIT (300). |

### GridCheck.java ([map](map/GridCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `GridCheck.FIXTURES` | `200` | Fixtures of fills checked against a raster: 200 (spec-grid 3, M1). |
| 61 | `GridCheck.WINDOW` | `128` | Plots across a fixture's window: 128, 2^7, so its fills merge up to level 7 and unaligned windows straddle every block line under it. |
| 64 | `GridCheck.CITY_PURCHASES` | `600` | Purchases the city of section 5 is bought to, evenly: 600 (spec-grid 3, M1) - past the 344 that bring the default world's city to Jerus's old city's size. |
| 67 | `GridCheck.ALL_LISTED_BY` | `12` | The purchase by which a new city's every place stands, bought evenly: the 12th, the latest spec-grid 2.2 measured on three worlds and two ways of buying (4 to 12). |
| 70 | `GridCheck.OWNER_NS` | `100` | The most owner() may take a plot, in nanoseconds: 100 (spec-grid 3, M1; measured 18 to 49 on two shared cores). |
| 73 | `GridCheck.TIMING_RUNS` | `7` | Runs owner()'s timing takes the median of: seven, as the spec's benchmarks did. |
| 76 | `GridCheck.TIMED_PLOTS` | `2_000_000` | Random plots owner() is timed on in each run: 2,000,000, as the spec's benchmark. |

### HaulageCheck.java ([map](map/HaulageCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 113 | `HaulageCheck.GROSS_TONNES` | `40` | A truck's gross weight, FR2.md section 2 (an EU 40 t articulated truck): the model states its tare, 40 t less a load. |
| 116 | `HaulageCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The relief a siding gives a railed tonne's city leg (InfrastructureManager). |
| 118 | `HaulageCheck.T` | `Haulage.TONNES_A_TRUCK_MONTH` |  |
| 124 | `HaulageCheck.SLOT` | `11` | Freight's place in Sectors.KEYS: FR2.md calls it "position 12" and "KEYS[12]", counting from one - the twelfth sector, Rail's old place, index 11 (retail is the first). |

### HistoryCheck.java ([map](map/HistoryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 44 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### HoldersCheck.java ([map](map/HoldersCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 1152 | `HoldersCheck.CENT` | `.0051` | The audit's tolerance for a figure read to the cent. |

### HomeRailCheck.java ([map](map/HomeRailCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `HomeRailCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The siding's part of a home tonne, and of a railed boundary tonne (InfrastructureManager). |
| 91 | `HomeRailCheck.YARD` | `Freight.PORT_YARD_SHARE` | The most of the tonnes that crossed a berth the port yard takes to rail (FR6, D7). |
| 94 | `HomeRailCheck.CITY_LITRES` | `Freight.CITY_LITRES_A_TONNE` | The diesel a railed city tonne burns, in litres. |
| 97 | `HomeRailCheck.BASE_MONTHS` | `14` | Months a fixture city is played before a railway is handed to it: HaulageCheck's, long enough for the trucks to be seeded and the books settled. |
| 100 | `HomeRailCheck.REAL_RUN_MONTHS` | `1250` | Months of a real run section 9 plays: the advisor lays its first station at about month 990 in this city (the run's own count prints it) and the run takes about half a minute. |

### HouseholdMemoryCheck.java ([map](map/HouseholdMemoryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 42 | `HouseholdMemoryCheck.WORKING` | `java.util.Arrays.stream(FamilyStructure.values()).filter(s -> ! s.isRetired() & & s ! =...` |  |

### HousingCheck.java ([map](map/HousingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `HousingCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### HydroCheck.java ([map](map/HydroCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `HydroCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 145 | `HydroCheck.Y0` | `60` | The plot row of the water of every river here: the river is two plots (60 m) wide, rows Y0 and Y0 + 1, its centre the line y = Y0 + 1. |
| 148 | `HydroCheck.W` | `2 * World.PLOT_M` | The width of the fixtures' rivers, metres: two plots. |
| 166 | `HydroCheck.ALL` | `(cx, cy) -> true` |  |
| 481 | `HydroCheck.PLACES` | `{ new Place("Shawinigan QC", 46.556, - 72.748, true), new Place("Niagara Falls ON", 43....` |  |
| 491 | `HydroCheck.HALF` | `333` | The half-side of the square a place is scanned in, in plots: 333 (10 km), a square of world 20 km a side. |

### InboxCheck.java ([map](map/InboxCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `InboxCheck.OUT` | `System.out` |  |
| 50 | `InboxCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### IndustryViewCheck.java ([map](map/IndustryViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 123 | `IndustryViewCheck.SETTLE_MONTHS` | `2` | Months a town plays before the month it is read on. |
| 126 | `IndustryViewCheck.OLD_STEEL` | `14_000` | Old metal arisen before the month read, tonnes before recovery: enough for one Scrap Yard's 10,500 t and some to landfill. |
| 128 | `IndustryViewCheck.OLD_COPPER` | `4_000` | ...and old copper, for one Scrap Copper Refinery's 4,400 t. |
| 133 | `IndustryViewCheck.FILES` | `new ArrayList<>()` |  |
| 956 | `IndustryViewCheck.FRAME_WIDTH` | `1_200` | The picture's width at the 1,389 x 868 window: the Operations card's inside (RefineryViewCheck's 1,200). |
| 959 | `IndustryViewCheck.PX` | `1e-6` | The slack a laid-out pixel is held to. |

### IslandNetCheck.java ([map](map/IslandNetCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 100 | `IslandNetCheck.SITE` | `128L * CityRuns.CELL + CityRuns.HIGHWAY_AT` | The founding site: a highway line crossing, so that the hub is the site itself. |

### JunctionCheck.java ([map](map/JunctionCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 139 | `JunctionCheck.T` | `TilePainter.TILE` |  |
| 168 | `JunctionCheck.ARTERIAL` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_ARTERIAL)` |  |
| 169 | `JunctionCheck.BOULEVARD` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_BOULEVARD)` |  |
| 170 | `JunctionCheck.STREET` | `streetCode(DistrictPlan.PAVED, 1, 0)` |  |
| 683 | `JunctionCheck.SITE` | `112` | The founding site the fixture cities lie round: a line crossing of the highways' (16 plots into a cell), so the hub is here. |
| 913 | `JunctionCheck.YARD` | `{ 163, 131, 20, 20 }, PORT = { 40, 100, 12, 10 }` | The yard, sea terminal and depot the access fixtures use: a Rail Terminal's box east of the hub's cell row, a terminal's west of it. |
| 1152 | `JunctionCheck.BIG` | `51, SMALL = 12` | The two bulk works the siding fixtures place: Machine Works-sized 11 x 11 (id 51) and the smaller 6 x 7 (id 12) - ids in buildings.json are permanent. |
| 1155 | `JunctionCheck.LINE_ROW` | `125` | The main line's row in the siding fixtures: 125, 16 plots below the first works' box, with a street between. |

### KeptCheck.java ([map](map/KeptCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 101 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 104 | `KeptCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 107 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 176 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's... |
| 179 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 222 | `KeptCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 225 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu", "showHist...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 447 | `KeptCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |

### KnownRedsCheck.java ([map](map/KnownRedsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `KnownRedsCheck.NAME` | `"KnownRedsFixture"` | What the fixture is called in the red lines: not its class name, which has a $ in it. |
| 60 | `KnownRedsCheck.KNOWN_LABEL` | `"the fixture's known assertion holds"` | The fixture's first assertion, always false: the red a record holds. |
| 63 | `KnownRedsCheck.SECOND_LABEL` | `"the fixture's second assertion holds"` | The fixture's second assertion, false only when asked: the NEW red. |
| 66 | `KnownRedsCheck.SECOND_PROPERTY` | `"knownreds.second"` | The system property that makes the fixture fail its second assertion as well. |
| 69 | `KnownRedsCheck.FIXTURE_HARNESS` | `"KnownRedsCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 72 | `KnownRedsCheck.WAIT_SECONDS` | `120` | The longest a child JVM or the shell may take. |

### LabourCheck.java ([map](map/LabourCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 35 | `LabourCheck.OUT` | `System.out` |  |
| 36 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 770 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 773 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |

### LandCheck.java ([map](map/LandCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 1730 | `LandCheck.IRON_IN_THE_GROUND` | `.0004` | Iron ore in the ground, a tonne in thousands: US$0.40, the land office's charge since 0.7.57, kept when ore fell to its world price (Jerus's D, 2026-10-08; 0.7.120). |
| 2175 | `LandCheck.RESEARCH` | `{ new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450.60153519, ...` | The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. |
| 2277 | `LandCheck.DEAL` | `7` | Read by nothing since 0.7.67: the width of the bands section 19 tiled the plane with until then, 7 plots, which no offer was - the pieces are the holdings and the offers standing now. |

### LandClassCheck.java ([map](map/LandClassCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `LandClassCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 103 | `LandClassCheck.CASH` | `1e8` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 106 | `LandClassCheck.BUSINESS_CASH` | `1e6` | ...and the cash a sector is given to pay a business's order from by fiat. |
| 109 | `LandClassCheck.GROUND` | `{ "flat", "foothill", "steep", "very steep" }` | The relief classes in World's order (FLAT to VERY_STEEP). |
| 111 | `LandClassCheck.HOUSE` | `"House"` |  |
| 114 | `LandClassCheck.ROUNDING` | `2.5 + 1e-6` | An offer's price is rounded to US$5k; a figure worked out from the ground's price can be that far off. |
| 272 | `LandClassCheck.DRY` | `0.5` | The dry ground the offers of 2 are of, square kilometres. |
| 782 | `LandClassCheck.HOUSES` | `100, DINERS = 30, MINES = 2` | The orders the hilly city of 6 is built from: houses and diners on the buildings' ground, iron mines on the works'. |

### LandlordPortCheck.java ([map](map/LandlordPortCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 81 | `LandlordPortCheck.CENT` | `0.01 / 1000` | A cent, in the game's money: D$0.01, and the money is in D$ thousands. |
| 343 | `LandlordPortCheck.K` | `Ports.Cargo.values().length` |  |

### LandmassCheck.java ([map](map/LandmassCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `LandmassCheck.out` | `System.out` |  |
| 113 | `LandmassCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 116 | `LandmassCheck.SEED` | `Founding.DEFAULT_WORLD_SEED` | The seed every world here is drawn with: the default world's. |
| 119 | `LandmassCheck.FREE_SLACK` | `1.0` | Two square-foot figures of one ground are the same within a square foot (the two landmass counts and the land's sum differ in the last bits of a double). |
| 122 | `LandmassCheck.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The area of a plot, square feet, as the books reckon it. |
| 125 | `LandmassCheck.SQUARE` | `64` | The side of an island square, in plots. |
| 128 | `LandmassCheck.RLAT` | `51.90625, RLON = 4.48125` | Rotterdam, the fixture's window: where the city is founded. |
| 131 | `LandmassCheck.ON_THE_ISLAND` | `{ { 51.77, 4.15 }, { 51.75, 4.20 }, { 51.80, 4.10 } }` | Where a city on Goeree-Overflakkee is founded (the mainland is then across the water): candidate spots, the first that serves. |
| 1006 | `LandmassCheck.FOOTHILL_X` | `376_640, FOOTHILL_Y = 375_616` | A square of home's ground in the Ardennes' foothills, 38 tiles south-west of Rotterdam (found by a scan of the fixture; its foothill count is a premise below). |
| 1513 | `LandmassCheck.TERMINAL_MONTHS` | `400` | Months a General Cargo Terminal is given to stand on a city's builders: 400. |
| 1519 | `LandmassCheck.HOME_COAST_SHORE` | `90` | Shore plots the square of home's open coast must hold in section 10: 90. |
| 1522 | `LandmassCheck.GROWN_MONTHS` | `480` | Months section 12 runs after its two terminals are ordered: 480 (it was 96, for terminals of 35,000 points; see TERMINAL_MONTHS). |
| 1553 | `LandmassCheck.GSON` | `new com.google.gson.Gson()` |  |
| 3173 | `LandmassCheck.RUN_MONTHS` | `1200` | The months the Rotterdam run plays. |

### LongPlaytest.java ([map](map/LongPlaytest.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `LongPlaytest.TARGET_MONTHS` | `Integer.getInteger("playtest.months", 4000)` | The run's length: 4,000 months, or -Dplaytest.months=N for a short look at one mechanic (a trace of its own, not the baseline). |
| 59 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 165 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 167 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 232 | `LongPlaytest.portBilledMonths` | `new int [ Ports.Cargo.values().length ]` | THE PORTS DIVISION OVER THE RUN (FR3, docs/queue/FR3.md 6 item 10): by kind of cargo, the months the division billed handling, the tonnes it handled and the last quote it struck; the handling bille... |
| 233 | `LongPlaytest.portTonnesRun` | `new double [ Ports.Cargo.values().length ]` |  |
| 234 | `LongPlaytest.portLastQuote` | `new double [ Ports.Cargo.values().length ]` |  |
| 258 | `LongPlaytest.BY_YEAR` | `{ "Gravel Road", "Paved Road", "Elevated Highway", "Rail Depot", "Rail Terminal", "Rail...` | THE ROADS AND THE RAILS BY YEAR (FR7, 2026-10-18; its brief's 5: "count in a real run how many highways, paved and gravel roads a long playtest builds, and in which years; the same for the rail run... |
| 261 | `LongPlaytest.byYearFirst` | `new int [ BY_YEAR.length ]` | ...the first month each stood, 0 for never. |
| 263 | `LongPlaytest.byYearAt` | `new int [ BY_YEAR.length ] [ 3 ]` | ...and how many stood at months 1,000, 2,000 and 3,000. |
| 268 | `LongPlaytest.FAR_FROM_PARITY` | `2.0` | How far from parity, as a multiple of it, a weak currency has to be to count as far from it. |
| 284 | `LongPlaytest.dialPath` | `new java.util.ArrayList<>()` | Every month's policy rate, for the dial's min, median and max over the run. |
| 286 | `LongPlaytest.inflationPath` | `new java.util.ArrayList<>()` | Every month's inflation reading, once the index has a year to read, for its median. |
| 295 | `LongPlaytest.spendPath` | `new java.util.ArrayList<>()` | Every month's spend factor (0.7.3): the share of their spending above subsistence the households planned at, on the month's real deposit rate - HouseholdBalance.getSpendFactor() after the month. |
| 313 | `LongPlaytest.bankNii` | `new double [ 12 ], bankFees = new double [ 12 ], bankOther = new double [ 12 ], bankOpe...` | THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for the checkpoint line that prints its price build-up beside its margin, its cost ratio, its fee share and its return on equity - ... |
| 330 | `LongPlaytest.bankYears` | `new java.util.ArrayList<>()` | THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own target averaged over each year, its return on the equity it opened the year with, its provisions over the loans it made (the ... |
| 353 | `LongPlaytest.watchSpell` | `new java.util.HashMap<>()` |  |
| 366 | `LongPlaytest.refusedOnPrice` | `new java.util.TreeMap<>()` |  |
| 376 | `LongPlaytest.houseReasons` | `new java.util.TreeMap<>()` |  |
| 420 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 575 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 587 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 591 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 626 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 706 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 717 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 718 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 719 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 720 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 721 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 722 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 723 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 726 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 729 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 730 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 731 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 732 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 735 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 736 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 737 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 738 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 801 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 802 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 803 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 804 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 806 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 810 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they... |
| 812 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 813 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1103 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1937 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasional"))` | True when this run is played by somebody paying attention. |
| 1944 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1947 | `LongPlaytest.CHILDCARE` | `Boolean.getBoolean("playtest.childcare")` | -Dplaytest.childcare=true (0.7.71, batch N2): the city builds childcare by the build advice's own card, which the advisor never does. |
| 1986 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(System.getPr...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1990 | `LongPlaytest.HELD_AFTER` | `24` | The month the held runs start after: the currency's settling months until XR1, kept at 24 so the held runs and the ensemble start where they always did. |
| 2017 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").trim().toUp...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 2038 | `LongPlaytest.PATH` | `System.getProperty("playtest.world") = = null ? Nations.DEFAULT_PATH : Nations.Path.val...` | The path under -Dplaytest.world, Nations.DEFAULT_PATH when unset. |
| 2042 | `LongPlaytest.PATH_SEED` | `Long.parseLong(System.getProperty("playtest.pathSeed", String.valueOf(Nations.DEFAULT_S...` | The path's seed under -Dplaytest.pathSeed, Nations.DEFAULT_SEED when unset. |
| 2192 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 2195 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 2527 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 2535 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE").trim()....` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2562 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").trim())` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 2565 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT").trim())` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 2568 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 2598 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2617 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2633 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(System.getPrope...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2648 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueOf(System.g...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2673 | `LongPlaytest.MONEY_GATE` | `System.getProperty("playtest.moneyGate", "").trim()` | THE MONEY GATE, FOR THE COUNTERFACTUAL (0.7.82, batch O5; turned round at 0.7.112): since 0.7.112 the refiners' spread planner tests every order on its whole cost, whoever pays (SpreadPlanner.ON_IT... |
| 2676 | `LongPlaytest.REFINERY_TRACE` | `Boolean.getBoolean("playtest.refinery")` | -Dplaytest.refinery=true (0.7.82): the refiners' planner yearly - its outlook and its best candidates under both money gates (refineryLine()). |
| 2679 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.valueOf(System....` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2707 | `LongPlaytest.FLOAT_FROM_MONTH` | `60` | THE D$ OVER THE RUN (XR1, 2026-10-21): how the float behaved in this city, counted a month at a time (countTheFloat()) and printed as one line beside the currency's (docs/studies/spec-exchange-rate... |
| 2902 | `LongPlaytest.CHILDCARE_SEEN` | `{ 1_000, 2_000, 3_000, 4_000 }` | CHILDCARE ON THE MAP (0.7.139, batch CC1). |
| 2905 | `LongPlaytest.childcareSeen` | `new java.util.ArrayList<>()` | ...what it said at each, in order. |
| 2927 | `LongPlaytest.BUDGET_EVERY` | `Integer.getInteger("playtest.budget", 0)` | -Dplaytest.budget=N (0.7.139, batch CC1): every N months the treasury's month, line by line - what came in and what went out, care by kind and childcare's own running (its centres' posts and upkeep... |
| 2952 | `LongPlaytest.childcareBuilt` | `new java.util.TreeMap<>()` | ...the buildings, by name. |
| 2955 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 3574 | `LongPlaytest.OIL_FUEL_IMPORTS_SHARE` | `.01` | The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. |
| 3604 | `LongPlaytest.refinedRun` | `new java.util.EnumMap<>(Good.class)` | ...and the refinery's products over the run (0.7.76, batch O1): made, by product, and what of them its tanks could not hold (spec-oil 6's joint-products risk). |
| 3709 | `LongPlaytest.terminalsOrdered` | `new java.util.TreeMap<>()` | Terminals the test player ordered, by name; the looks that found a berth's worth with no coast; the first terminal's month. |
| 3718 | `LongPlaytest.PORTS_TRACE` | `Boolean.getBoolean("playtest.ports")` | -Dplaytest.ports=true (0.7.86): the ports yearly - each kind's tonnes across the boundary, its berths, its share at sea and the month's calls (portsLine()). |
| 3807 | `LongPlaytest.refinersOpened` | `new java.util.TreeMap<>(), refinersSold = new java.util.TreeMap<>()` | THE SPREAD PLANNER'S BUILDINGS OVER THE RUN (0.7.82, batch O5): each of the refiners' buildings opened and sold back, and the crude units and the conversion units standing at most. |
| 3871 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 3878 | `LongPlaytest.GROUND_AHEAD_CASH_SHARE` | `.10` | The share of the treasury's cash one look spends keeping ground ahead (0.7.58, J1d): a tenth, the share the war chest tops the reserves up from and the every-13th-stop purchase is held under. |
| 4076 | `LongPlaytest.LAYOUT_WINDOW` | `13` | How many months a window of the layout's watch is. |
| 4078 | `LongPlaytest.LAYOUT_STARTS` | `layoutStarts(TARGET_MONTHS)` | The months each window follows: TARGET_MONTHS / 4, / 2, 3 / 4 and TARGET_MONTHS - LAYOUT_WINDOW, those that fit in the run and clear the window before. |
| 4108 | `LongPlaytest.layoutSpillByMonth` | `new java.util.TreeMap<>()` | The spill's moves by the month they were found in (the month pair ending there): how many pairs had any, and when. |
| 4112 | `LongPlaytest.layoutOldBoxes` | `new java.util.HashMap<>()` | ...each district's industry boxes, by its key... |
| 4114 | `LongPlaytest.layoutOldCounts` | `new java.util.HashMap<>()` | ...and each district's count of boxes by type. |
| 4116 | `LongPlaytest.layoutOldModel` | `new java.util.HashMap<>()` | ...and each district's model count of every type (District.count()) then and the lowest it has been in any month since: a finding's detail (an old works that left in a dip of its type's count did n... |
| 4123 | `LongPlaytest.layoutOldSpill` | `new java.util.HashSet<>()` | The districts a spill reached when the oldest works were kept (a carry in), and the old works in a district a spill reached then or at the end, counted apart: a district joining the map's order ahe... |
| 4320 | `LongPlaytest.layoutBandsAt` | `new ArrayList<>()` | How many boxes each window's first month holds in the homes' band, in industry and in the outer kinds, one phrase a window: what the oldest works are read against. |
| 4910 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 4916 | `LongPlaytest.ABROAD_SHARE` | `.8` | CC1-S: the share of the city's own quote for a sale that the dollar quote for it must come under for the build to borrow abroad. |
| 4919 | `LongPlaytest.QUOTED_BORROWING` | `Boolean.getBoolean("playtest.quotedBorrowing")` | -Dplaytest.quotedBorrowing=true (CC1-S): the build borrows abroad by the quotes for the sale (borrowsAbroad()); unset, by the rates before it (abroadByTheRatesBefore()), and the run is the run it a... |
| 4975 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 5556 | `LongPlaytest.ADDED_BACK_TERMS` | `{ "food", "materials", "luxuries", "held goods" }` | The inventory terms in NationalAccounts.getWrittenOffAddedBack()'s order. |
| 5558 | `LongPlaytest.addedBackMonths` | `new int [ ADDED_BACK_TERMS.length ], addedBackFirst = { - 1, - 1, - 1, - 1 }, addedBack...` | Months each term added a write-off back, the first, and the largest with its month. |
| 5560 | `LongPlaytest.addedBackWorst` | `new double [ ADDED_BACK_TERMS.length ]` |  |
| 5562 | `LongPlaytest.EVENTS_PRINTED` | `12` | The events printed as they happen, each term's first few: enough to say where a trace moved. |
| 5615 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 5623 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |
| 7507 | `LongPlaytest.DISTINCT_ROUNDS` | `64` | The most letters distinctCuts() adds to a name: past the longest key there is nothing left to add. |
| 7656 | `LongPlaytest.PROBE_QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } @ Override...` |  |
| 7662 | `LongPlaytest.PROBE_MONTH_NANOS` | `new long [ 20_000 ]` | Per-month wall time of the probe's run(g, 1), nanoseconds, by month index. |

### MainLineCheck.java ([map](map/MainLineCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 705 | `MainLineCheck.YARD_SIDE` | `24` | Plots: a yard's box a side in the fixture, his Rail Terminals' (CityRuns.spotsIn() lays one at either end of its cell: at 1, or CELL - side). |

### ManufacturingCheck.java ([map](map/ManufacturingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 68 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 69 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 70 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |
| 79 | `ManufacturingCheck.STEEL_WAS` | `1.284, FABRICATED_WAS = 2.21, MACHINERY_WAS = 9.0` | The prices the three plants were costed on, written down (2026-09-13 to 0.7.119): steel landed at the US band, and the two goods made of it. |
| 82 | `ManufacturingCheck.STEEL_FALL` | `STEEL_WAS -.650` | Steel's fall a tonne at the import price on 0.7.120, the figure Q6b moved each good by: 1.284 to .650. |

### MapCheck.java ([map](map/MapCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 390 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 393 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 405 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114, 1, 2, 0,...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 sin... |
| 410 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 413 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 416 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 419 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 437 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 440 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 443 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 446 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 449 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 452 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). |
| 455 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 458 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 461 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 464 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 511 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } @ Override...` |  |
| 926 | `MapCheck.SPILL_FIRST_CELLS` | `2, SPILL_SECOND_CELLS = 6` | The cells across the window of whole cells a spill's first district owns and its second: 2 and 6 of a district's 8 - the first draws little, the second all it is carried. |
| 1085 | `MapCheck.BOUGHT_PLOTS` | `64` | The side of a bought block, at least: 64 plots (rounded up to whole blocks of the land's level), a few cells of a district's 8 across. |
| 1352 | `MapCheck.MIX` | `{ "Low-Rise Apartments", "Small Grocery Store", "Bakery", "Elementary School", "Industr...` | What the founding district is given month by month besides houses, in turn (an apartment block, shops, a school, works): buildings of several sizes for its plan to place. |
| 1355 | `MapCheck.USED_SHARE` | `0.5` | How much of its room the founding district is brought to before a run is laid across it: half of the cells its plan may fill (CityMap.PLAN_FILL of its owned plots). |
| 1358 | `MapCheck.BUILDINGS_A_MONTH` | `3` | The buildings of the mix the founding district is given each month, beside its houses. |
| 1361 | `MapCheck.MONTHS_BETWEEN` | `4` | Months of arrivals after each change of the runs, before the next plan is drawn: the buildings that must go on the ground as the run left it. |
| 1364 | `MapCheck.HIGHWAY_MONTHS` | `60, HIGHWAY_STEP = 8` | The most months the highways are given to reach the district, and the Elevated Highways added a month while they do. |
| 1367 | `MapCheck.HIGHWAY_CLEAR` | `1` | A building keeps this far from a highway plot (batch H5: no building touches one, corners included): the reach, in plots, within which a highway laid takes a box's place. |
| 1481 | `MapCheck.RUN_KINDS` | `{ DistrictPlan.FIXED_YARD, DistrictPlan.FIXED_RAIL, DistrictPlan.FIXED_HIGHWAY, Distric...` | The kinds of fixed plot the runs lay, in the order a line prints them. |
| 1483 | `MapCheck.RUN_NAMES` | `{ "yard", "rail", "highway", "reserve" }` | ...and their names, in the same order. |
| 1968 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 2502 | `MapCheck.ACCESS_HIGHWAYS_ADDED` | `200, ACCESS_DEPOTS_ADDED = 3` | The access fixture's added highways and Truck Depots: enough highways to cross, three depots to place. |
| 2670 | `MapCheck.SPURS_ADDED` | `12, BULK_WORKS_ADDED = 90` | The sidings fixture's added Rail Spurs and bulk works (JunctionCheck's two bulk types, two in three the larger). |
| 2974 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated ... |
| 3129 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 3393 | `MapCheck.SIDECAR_BYTES` | `500_000` | The most a sidecar may be at 5 and 10 billion people, log and all, in deflated bytes (CL3): the 10B copy's measured 333,776 (294,101 without the log, in FORMAT 8; its 1,457,418 records the canonica... |
| 3396 | `MapCheck.SIDECAR_MS` | `500` | The most the 5B and 10B copies' sidecar may take to write, and to read, in ms (CL3): measured 271 and 267 at 10B (240 and 161 at 5B), and near twice that. |
| 3443 | `MapCheck.THREADS` | `java.lang.management.ManagementFactory.getThreadMXBean()` | The JVM's threads, read for the main thread's CPU time. |
| 3446 | `MapCheck.CPU_STEP_MS` | `0.1` | The coarsest step of the thread's CPU clock a bound is read on, in ms: 0.1, a twentieth of the quickest stamp timed; coarser (Windows: 15.6) and the bounds are read on the wall clock. |
| 3449 | `MapCheck.CPU_TIMED` | `cpuTimed()` | Whether a month is timed on the main thread's CPU: this JVM measures it, it is on (turned on here if it is off), and its clock steps no coarser than CPU_STEP_MS. |
| 3590 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 3593 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 4043 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, whi... |
| 4150 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 4280 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district we... |
| 4297 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 4300 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 4303 | `MapCheck.ROUTES_ALL_MS` | `ham.citybuildersim.ui.UserInterface.SECONDS_PER_MONTH * 1000` | A month at normal speed, in ms: ui.UserInterface.SECONDS_PER_MONTH x 1000, 5,000 - what every terminal's route found from scratch at 10 billion must be found within (SL1, 2026-10-28). |
| 4306 | `MapCheck.ROUTES_MONTH_MS` | `ham.citybuildersim.ui.UserInterface.SECONDS_PER_MONTH * 1000 / ham.citybuildersim.ui.Us...` | A month at the clock's top speed, in ms: SECONDS_PER_MONTH x 1000 over UserInterface.SPEED_TOP, 100 - what a month's new terminals' routes, laid onto those kept, must be found within at 10 billion ... |
| 4309 | `MapCheck.ROUTES_MONTH_SHARE` | `0.01` | The share of the 10 billion lanes' terminals a month lays: 1%, 200 of the 20,000 (SL1). |
| 4312 | `MapCheck.ROUTES_ROUNDS` | `3` | Rounds of the 10 billion routes' timings, the least kept (a find from scratch is the slow one, so fewer than the frames'): 3 after one warm round. |
| 4331 | `MapCheck.SHORE_FEW` | `10` | General Cargo Terminals standing on the small coast of the shore's cost: 10. |
| 4334 | `MapCheck.SHORE_MANY` | `300` | ...and on the big coast: 300, where a late city stands thousands. |
| 4337 | `MapCheck.SHORE_GROWTH` | `2` | How many times the cost of a month's terminal may grow from SHORE_FEW standing to SHORE_MANY: twice (it was 21 times, 74 ms and 1,557 ms: every plot the search read asked every work, and every sear... |
| 4340 | `MapCheck.SHORE_WARM` | `3, SHORE_TIMED = 15` | Months of one more terminal before the timing and timed, a terminal each: SHORE_WARM that are not, then the median of SHORE_TIMED kept (a month is about a ms, which a pause moves). |
| 4343 | `MapCheck.SHORE_MARGIN` | `2` | Plots asked about each work beyond its box, verge and quay: 2, past the cell the index keeps a work in. |
| 4346 | `MapCheck.SHORE_FILL` | `20` | Terminals asked of the coast that fills, one at a time: 20, more than its 12. |
| 4349 | `MapCheck.SHORE_LAGOON` | `40` | Terminals asked of each lagoon, one at a time: 40. |
| 5139 | `MapCheck.TRAIN_FRAME_MS` | `BOAT_FRAME_MS` | The design's bound on a frame's trains at 10 billion, in ms: the boats' (spec-freight 4.4, "the same structure is well inside budget"). |
| 5142 | `MapCheck.TRAIN_YARDS_A_SIDE` | `40` | Yards a side of the synthetic city the frame is timed on at 10 billion: 40, 1,600 yards, 12,800 routes on one main line. |
| 5612 | `MapCheck.MOST_TURN_DEG` | `30` | The most two neighbouring vehicles of a train may differ in heading, in degrees: 30 (FX5; on 0.8.14 a corner turned them 90 and a ladder 60). |
| 5615 | `MapCheck.LADDER_TURN_DEG` | `45` | ...and on a yard's ladder, the tightest curve the railway has: 45 degrees (FX5: an S of up to a few plots across over YARD_LADDER, 3 plots, turns a depot's neighbours 40; the long yards slice Y lay... |
| 5759 | `MapCheck.TRUCK_FRAME_MS` | `TRAIN_FRAME_MS` | The design's bound on a frame's trucks at 10 billion, in ms: the trains' (FR9: the trips run the trains' pace, and a frame is the same structure). |
| 5762 | `MapCheck.TRUCK_ROADS` | `20` | Roads a district of the synthetic city has at 10 billion: 20, each with a world trip and a home trip, so a district's schedule holds 40 routes. |
| 5853 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 5856 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 5859 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 5861 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` | ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). |
| 6021 | `MapCheck.WINDOW_STRIDE` | `EarthTerrainCheck.WINDOW / MapFrame.NODE_PX` | The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers ... |
| 6024 | `MapCheck.CLOSE_STRIDES` | `{ 2, 4, 8 }` | The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. |
| 6027 | `MapCheck.SEAM_SHIFT_PX` | `MapFrame.NODE_PX / 2` | How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. |
| 6030 | `MapCheck.CHUKOTKA_LAT` | `66, CHUKOTKA_LON = 180` | The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. |
| 6033 | `MapCheck.CHUKOTKA_STRIDE` | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. |
| 6036 | `MapCheck.LAKES_LAT` | `62, LAKES_LON = 26` | The full file's small lakes: Finland's lake district, 62 N 26 E. |
| 6039 | `MapCheck.LAKES_NODES` | `3` | ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. |
| 6437 | `MapCheck.PICTURE_MONTHS` | `MONTHS` | Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. |
| 6445 | `MapCheck.PICTURE_CASH_MONTHS` | `3` | FR8, 2026-10-28 (the path moved): the months of its GDP the played city is handed in cash at each look of the advisor in 11's run when it holds less. |
| 6454 | `MapCheck.PICTURE_SIGMAS` | `4` | How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. |
| 6457 | `MapCheck.NODE_ROUNDS` | `TIMED_ROUNDS` | Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. |
| 6910 | `MapCheck.FAR_DISTRICTS` | `4` | How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. |
| 6913 | `MapCheck.STAMP_WARM` | `20, STAMP_TIMED = 10` | Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (abou... |
| 7155 | `MapCheck.KEY_FRAMES` | `60` | Frames of the keys' second: 60, the pump's (a sixtieth each). |
| 7158 | `MapCheck.KEY_SLOP_PX` | `1e-3` | How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeChe... |
| 7161 | `MapCheck.MATRIX_PLOTS` | `1e-6` | The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). |
| 7670 | `MapCheck.BOXES` | `200` | Boxes drawn at random on the played city's opening view, each tried against every plot under it: 200. |
| 7673 | `MapCheck.GLOBE_BOXES` | `60` | ...and through the globe's camera, each against a point at every free plot's middle: 60. |
| 7676 | `MapCheck.FIELD_BUYS_MOST` | `200` | The most offers the city buys toward an iron field for one to be on offer: 200, MiningCheck.makeRoom()'s own bound. |
| 7679 | `MapCheck.BOX_PLOTS_MOST` | `200_000` | The most plots a box is tried on, one by one: 200,000 - a box larger is drawn again smaller. |
| 7682 | `MapCheck.GLOBE_LAT` | `51.90625, GLOBE_LON = 4.48125` | Where the globe's check looks from: EarthCheck's Rotterdam window's centre, the played city's site put there. |
| 8048 | `MapCheck.PICKS_MONTHS` | `3` | The months section 14b holds the picks over, each with a place relisted: 3. |
| 8051 | `MapCheck.PICKS_HELD` | `3` | The offers section 14b picks and holds over the months: 3. |
| 8206 | `MapCheck.FAR_PLOTS` | `World.SIDE / 4.0` | How far from the city section 14c sets the plane: a quarter of the world's side, in plots. |
| 8209 | `MapCheck.FAR_DRAGS` | `50` | The drags section 14c carries the plane on, further out, of the pixels each. |
| 8255 | `MapCheck.PRESS_PLOTS` | `400` | The plots of an offer's rectangle section 14d tries for the owned-or-free rule: at most about this many, a stride over the rectangle. |
| 8356 | `MapCheck.STEPS` | `{ { 1, 0 }, { - 1, 0 }, { 0, 1 }, { 0, - 1 } }` | The four steps from a block to its neighbours: east, west, south, north. |
| 9304 | `MapCheck.MATERIALS_SIDE` | `10` | The materials fixture's square, in districts a side: 10 (77 km), wide enough to hold fields of every typed slot on the default world. |
| 9307 | `MapCheck.MATERIALS_SECTORS` | `{ Sectors.MINING, Sectors.HEAVY_INDUSTRY, Sectors.COAL_MINING, Sectors.QUARRYING, Secto...` | The nine materials industries, Iron Mining to Wood Products (spec-materials 2.1): every building of theirs drawn with a look of its own. |
| 9644 | `MapCheck.MATERIALS_RINGS` | `24` | How far out materialsPlace() looks, in cells: 24. |
| 9685 | `MapCheck.AV_SAMPLES` | `4` | How many samples a step of an avenue's line is sampled at: four, a quarter step apart. |
| 9688 | `MapCheck.AV_STEPS` | `400` | The farthest an arm is followed from the origin, in steps: 400, past a district and a half. |
| 9696 | `MapCheck.AV_TIMES` | `9, AV_SIDE = 6, AV_SHIFT_X = 104, AV_SHIFT_Y = 56` | The city the avenues are drawn on: Jerus's city x 9 on a square of 6 districts a side (his density, as the copies', every district full) whose founding site is the dry place's moved AV_SHIFT_X plot... |
| 9716 | `MapCheck.AvTiles.OWNED` | `1, BENEATH = 2, BRIDGE = 4` | Flag bits of a plot: it is owned, a street passes beneath a highway on it, it is a bridge's. |
| 10041 | `MapCheck.CV_FROM` | `3, CV_TO = 8` | The districts the curves are looked for in, in districts east and south of the founding site's: 3 to 8 - past CRESCENT_FROM (16 cells, two districts) wherever the cell lies. |
| 10044 | `MapCheck.CV_CRESCENTS` | `8, CV_SHORES = 16` | The most crescents and shore curves the audit follows: 8 and 16 (the shore curves that cross a tile's edge, and those that bend, first). |
| 10047 | `MapCheck.CV_TURN_SPAN` | `4` | How far apart, in plots, the turn audit's samples are: 4 - at a crescent's radius (22.6 plots) a turn of 0.18 radian between neighbours. |
| 10050 | `MapCheck.CV_CENTRE_PX` | `1.0` | How far the band's middle, measured across it from the pixels, may lie from the line: a pixel - the least a raster can place a band, and less than the 2.8 pixels at 4 px a plot a staircase's bars l... |
| 10053 | `MapCheck.CV_NARROW_PX` | `1.5` | How much narrower than its street the band, measured across it, may measure: 1.5 pixels (a pixel's rounding at either end and a half). |
| 10056 | `MapCheck.CV_GENTLE` | `0.2` | How much the line may turn within a plot and a quarter either side of a sample for the band to be measured across it there: 0.2 radian - a crescent turns 0.06 over that; a shore street's corner (ro... |
| 10059 | `MapCheck.CV_MEASURED` | `{ 0.5, 0.35 }` | The share of a kind's samples the band must have been measured across (crescents, shore curves): a half, and 35% - a shore's line turns a corner every few plots, and the band is not measured within... |
| 10659 | `MapCheck.HIS_TIMES` | `80` | The copy at Jerus's own district count: x 80, 125 districts, 11 a side (CL3's notes cite 115 for his cities), between x 1's 4 districts and 5B's 14,661. |
| 10662 | `MapCheck.REDRAW_MONTHS` | `24, REDRAW_MONTHS_5B = 12` | The months grown at x 1 and at x 80, and at 5B (its months are slower): 24 and 12. |
| 10665 | `MapCheck.REDRAW_WARM` | `3` | The first months of each fixture are drawn, compared and not counted in its figures: 3, the JIT's compiling done before a month is read. |
| 10668 | `MapCheck.GROW_PER_MILLE` | `1000, GROW_PER_TEN_THOUSAND = 10_000` | A month's growth as a share of each type's count: 1/1,000 (+0.1%) at x 1 and x 80, 1/10,000 (+0.01%) at 5B, as section 5's, at least one building each. |
| 10671 | `MapCheck.RUNS_EVERY` | `4` | The runs' types (a highway's, a track's) are grown only in every 4th month, so most months lay no run, as in play. |
| 10674 | `MapCheck.CHAIN_ROUNDS` | `4` | The times a month's screens are drawn: the month's, then again after each round of the chain's jobs (handed out, run, adopted) until none is left, at most 4. |
| 10677 | `MapCheck.PLAYED_BEFORE` | `3, PLAYED_AFTER = 3` | The months the played city is grown before its land purchase and after it: 3 and 3. |
| 10680 | `MapCheck.PURCHASE_SIDE` | `32` | The side of the block the played city buys, in plots: 32, a hole in its built-up ground's box (the played city's ground is irregular). |
| 10683 | `MapCheck.MAP_MONTH_MS` | `58` | The most a month's FX-side map work may take at 5B, in ms, its median month - the stamp, the far view's screen, an L0 screen re-stamped and the chain's handing out and adopting: 58, nine tenths of ... |
| 10686 | `MapCheck.GROW` | `0, ONE = 1, SETTLE = 2` | The kinds of month: the grown one, one built type raised by one, and the first, which only draws. |
| 11662 | `MapCheck.NEAR_BOX_KM` | `8` | The side of the box of the city's ground the borders are crossed against, km: 8 - a few kilometres, and wide enough for the fixture's Belgium-Netherlands line, which Natural Earth runs 2.7 km from ... |

### MaterialsCheck.java ([map](map/MaterialsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 172 | `MaterialsCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 174 | `MaterialsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 721 | `MaterialsCheck.IRON_ROWS` | `{ Mining.IRON_MINE, HeavyIndustry.ARC_FURNACE, HeavyIndustry.MINI_MILL }` | The iron rows of batch I3 (0.7.120), by their permanent ids: the Iron Mine, the Electric Arc Furnace, the Steel Mini-Mill. |
| 724 | `MaterialsCheck.STUDY_CASES` | `{ { 2.7, 2.7, 4.1, 4.1 }, { - 0.5, - 0.1, 0.7, 0.2 }, { - 0.5, 0.1, 1.0, 0.4 } }` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 727 | `MaterialsCheck.STUDY_YEARS` | `{ 2.2, 4.4, 3.5 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 730 | `MaterialsCheck.STUDY_RUNS` | `{ 469, 187, 708 }` | ...and their running cost a month, D$ thousands, the study's 2.4 "Runs" (the mine's with its diesel in it). |
| 733 | `MaterialsCheck.STUDY_DIESEL` | `.0006` | The study's price of a litre of diesel in its runs, D$ thousands: the oil mockups' D$0.60 (its 1.5). |
| 736 | `MaterialsCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its cases to a tenth of a point, its years to a tenth. |
| 853 | `MaterialsCheck.ROUTE_ROWS` | `{ HeavyIndustry.SCRAP_YARD, HeavyIndustry.PELLETISING_PLANT, HeavyIndustry.COKE_OVENS, ...` | The integrated route's rows of batch I4 (0.7.123), by their permanent ids, in the study's 2.4 order. |
| 857 | `MaterialsCheck.ROUTE_CASES` | `{ { 1.5, 1.5, 4.7, 4.7 }, { 0.1, 1.8, 3.7, 2.0 }, { - 0.2, 0.7, 2.2, 1.3 }, { - 1.1, 1....` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 861 | `MaterialsCheck.ROUTE_YEARS` | `{ 1.0, 1.2, 1.7, 0.8, 1.1, 1.1, 1.7 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 864 | `MaterialsCheck.ROUTE_RUNS` | `{ 255, 1000, 1250, 868, 11700, 25600, 5100 }` | ...their running cost a month, D$ thousands, the study's 2.4 "Runs" (only the power each buys from the grid in it, the study's 1.6). |
| 867 | `MaterialsCheck.ROUTE_COSTS` | `{ 35_000, 270_000, 264_000, 54_000, 1_350_000, 1_800_000, 1_200_000 }` | ...their all-in cost, D$ thousands (the study's 2.4). |
| 870 | `MaterialsCheck.ROUTE_POSTS` | `{ 40, 150, 200, 150, 1200, 1800, 400 }` | ...their posts (the study's 2.4). |
| 873 | `MaterialsCheck.ROUTE_KW` | `{ 550, 5750, 2200, 2050, 51400, 51600, 77000 }` | ...their draw, kW (the study's 2.4 and 1.6). |
| 876 | `MaterialsCheck.ROUTE_GEN` | `{ 0, 0, 8000, 0, 60000, 25000, 0 }` | ...and what they make, kW: the Coke Ovens', the Blast Furnace's and the Oxygen Steel Shop's power from their gas (the study's 2.4 and 1.6). |
| 879 | `MaterialsCheck.ROUTE_HECTARES` | `{ 1.9, 15, 9, 3.7, 60, 84, 46 }` | ...and their ground, hectares (the study's 2.4). |
| 1240 | `MaterialsCheck.MINE_ROWS` | `{ Mining.LARGE_OPEN_PIT, Mining.MAGNETITE_MINE, Mining.LARGE_MAGNETITE }` | Iron Mining's rows of batch I5 (0.7.124), by their permanent ids, in the study's 2.4 order: the Large Open-Pit Mine, the Magnetite Mine & Concentrator, the Large Magnetite Complex. |
| 1243 | `MaterialsCheck.MINE_CASES` | `{ { 3.7, 3.7, 5.2, 5.2 }, { 1.2, 1.2, 2.2, 2.2 }, { 2.2, 2.2, 3.3, 3.3 } }` | The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). |
| 1246 | `MaterialsCheck.MINE_YEARS` | `{ 2.0, 2.9, 2.7 }` | ...their cost over a year of what each would sell abroad, its last column. |
| 1249 | `MaterialsCheck.MINE_RUNS` | `{ 5_420, 951, 9_580 }` | ...their running cost a month, D$ thousands, the study's 2.4 "Runs". |
| 1252 | `MaterialsCheck.MINE_COSTS` | `{ 1_000_000, 60_000, 1_100_000 }` | ...their all-in cost, D$ thousands (the study's 2.4). |
| 1255 | `MaterialsCheck.MINE_POSTS` | `{ 400, 120, 500 }` | ...their posts (the study's 2.4). |
| 1258 | `MaterialsCheck.MINE_KW` | `{ 3_000, 3_000, 52_000 }, MINE_WATER = { 300, 400, 7_900 }` | ...their draw, kW, and water, units a month (the study's 2.4). |
| 1261 | `MaterialsCheck.MINE_MAKES` | `{ 500_000, 20_000, 400_000 }, MINE_ROCK = { 500_000, 60_000, 1_200_000 }` | ...what each makes a month, t of ore or concentrate, and the t of rock it digs for it (the study's 2.4: 60,000 t for the small magnetite works' 20,000, 1.2 Mt for the large one's 400,000; the hemat... |
| 1264 | `MaterialsCheck.MINE_TYPES` | `{ Deposit.FieldType.HEMATITE, Deposit.FieldType.MAGNETITE, Deposit.FieldType.MAGNETITE }` | ...the type of field each stands on, and the sites it takes (the study's 1.3, 2.4: twenty sites for a large mine, one for a small). |
| 1266 | `MaterialsCheck.MINE_SITES` | `{ 20, 1, 20 }` | ...and the sites each takes. |

### MiningCheck.java ([map](map/MiningCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 44 | `MiningCheck.IRON_SEED` | `35` | A world whose founding site has an iron field near it, as the default world's had until 0.7.98 (35 sites, 449 Mt, 0.87 km out): 35, whose nearest is 36 sites, 471 Mt, 0.97 km out on dry ground, non... |
| 154 | `MiningCheck.IMPORTED_SCRAP_LOW` | `.05, IMPORTED_SCRAP_HIGH =.15` | The share of its sales an arc furnace keeps on imported scrap, selling its steel at home, at least and at most (docs/studies/spec-materials.md 4, from the study's 2.5: the furnace about 7%, the Min... |
| 157 | `MiningCheck.LOCAL_SCRAP_LOW` | `.20, LOCAL_SCRAP_HIGH =.35` | ...and on the city's own scrap, at its export price (the study's 25% and 31%). |

### MonetaryCheck.java ([map](map/MonetaryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 691 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 694 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 697 | `MonetaryCheck.HELD_FROM` | `LongPlaytest.HELD_AFTER + 1` | The month the dial is held from: the one after LongPlaytest.HELD_AFTER, the months the held runs start after. |
| 710 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 713 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |

### MortgageCheck.java ([map](map/MortgageCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 113 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |

### NationsCheck.java ([map](map/NationsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `NationsCheck.out` | `System.out` | Where the harness prints: the console. |
| 67 | `NationsCheck.BASE_SEED` | `7_001` | The seed the single-world sections use, and the one the others start from. |
| 70 | `NationsCheck.SEEDS` | `20` | The seeds the statistics are counted over, and the years each (2001-2100). |
| 73 | `NationsCheck.GOOD_SEEDS` | `30` | The goods' statistics are counted over this many seeds of 100 years (a lag-1 autocorrelation's error is about 1 over the root of the count). |
| 76 | `NationsCheck.GOOD_BURN_IN` | `30` | The years a good's price needs to settle from the file's 2000 ratio to its mean before it is counted: 0.7 to the 30th is 2e-5. |
| 79 | `NationsCheck.SD_TOL` | `.10, FRONTIER_SD_TOL =.12, GOOD_SD_TOL =.12, SHARE_TOL =.15` | A swing's standard deviation is within this share of its dial (the world's, a region's, the frontier's, a good's). |
| 82 | `NationsCheck.LAG_TOL` | `.06` | An autocorrelation is within this of its dial (error of a lag-1 estimate over 2,000 years is about .02). |
| 85 | `NationsCheck.RATE_TOL` | `.004` | The crisis rate among the years a crisis could start is within this of CRISIS_RATE. |
| 88 | `NationsCheck.LONG_RUN_TOL` | `.003` | The Historical frontier's growth a head, 2160-2200 and over twelve seeds, is within this of LONG_RUN_GROWTH: tight, because FRONTIER_GROWTH is only .005 above the long run, so an ease that did not ... |
| 91 | `NationsCheck.HELD_TOL` | `1e-12` | A rate held, or a ratio kept, is the same within this share. |

### NewGameCheck.java ([map](map/NewGameCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 263 | `NewGameCheck.USED_HOUSES` | `120` | The Houses the used city is founded with (0.7.113; 200 until then): fewer than its people need, so its landlords build and borrow. |
| 1239 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 1240 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |
| 1266 | `NewGameCheck.AFTER_REFORM_MONTHS` | `24` |  |
| 1268 | `NewGameCheck.AFTER_REFORM_BAND` | `0.05` | How far apart, as a ratio, the two cities' parity and rate may sit: the runaway put them a hundredfold apart. |

### OilCheck.java ([map](map/OilCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 236 | `OilCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder, for the reloads (section 9). |
| 668 | `OilCheck.DUTY` | `.000215` | The duty a litre the fixture sets, in thousands: the research's Canada-like default for petrol, 21.5 cents a litre (runs/research-pump.md 7). |
| 964 | `OilCheck.PRODUCTS` | `{ Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS, Good.FUE...` | The nine products of 0.7.76 (spec-oil 2.1), in the spec's table's order. |
| 968 | `OilCheck.LITRES_A_TONNE` | `{ 1850, 1351, 1320, 1260, 1180, 1127, 1010, Double.NaN, Double.NaN }` | The spec's table (2.1): litres a tonne for the litre goods ([P35]; naphtha and lubricants est., JODI), NaN for the two in tonnes. |
| 971 | `OilCheck.LADDER` | `{.46, 1.0, 1.20, 1.28, 1.35, 1.89,.98, 1.08,.155 }` | ...and each one's ratio to crude's world middle (the research's ladder, the prototype's RATIO): a litre's, or a tonne's for bitumen and coke. |
| 974 | `OilCheck.PUMP_LITRE` | `Motoring.CAR_FUEL_PER_JOURNEY / Motoring.LITRES_PER_JOURNEY` | FUEL's import price, 0.7.62 to 0.7.75 (petrol's and diesel's at 0.7.76 and 0.7.77): a journey's pump price over its litres. |
| 977 | `OilCheck.PETROL_CUT_PCT` | `63, DIESEL_CUT_PCT = 59` | What the ladder takes off the pump price, as the spec gives it (spec-oil 1, item 4): petrol 63%, diesel 59%, to the percent. |
| 980 | `OilCheck.FOUR_FIGURES` | `5e-4` | The spec's four figures: a price within half a unit in its fourth significant figure. |
| 1979 | `OilCheck.SEA_OIL_SEED` | `518` | The world whose founding site's nearest oil field in the sea is heavy and shallow enough for a jacket: 518, its field 3.5 km out - heavy, 12 sites (a jacket's slots), 58 m deep (fixW1-notes.md, the... |
| 1982 | `OilCheck.FIAT_TONNES` | `1_000` | Tonnes of oil a fixture's centre is handed by fiat ahead of the field, so a lift crosses from it into the field: 1,000. |

### OilViewCheck.java ([map](map/OilViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 105 | `OilViewCheck.GROUND` | `2_000_000` | The oil handed to the sea town's two dry sites by fiat: the ground pool, plenty for the sections' months. |
| 108 | `OilViewCheck.FILL` | `40_000` | The fill the sea town orders for its reserve. |
| 111 | `OilViewCheck.SEA_MONTHS` | `14` | Months the sea town plays before it is read: past a year, so its platform's wells are in their plateau's second year. |

### OrderSearchCheck.java ([map](map/OrderSearchCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `OrderSearchCheck.LONG_MONTHS` | `1200` | Months the playtest's city plays with every order watched... |
| 60 | `OrderSearchCheck.DEAR_MONTHS` | `120` | ...then this many more with the policy rate held at DEAR_RATE, so the mortgage lender trims and refuses. |
| 62 | `OrderSearchCheck.DEAR_RATE` | `.15` | ...and the policy rate those months are held at. |
| 94 | `OrderSearchCheck.COPY_MONTH` | `400` | The copy of that city: the month it is copied at, how many times over, and how many months it plays. |
| 96 | `OrderSearchCheck.COPY_TIMES` | `1000` | ...how many times over it is copied... |
| 98 | `OrderSearchCheck.COPY_MONTHS` | `24` | ...and the months the copy plays. |
| 107 | `OrderSearchCheck.COPY_RATE` | `DEAR_RATE` | The policy rate the copy's months are held at (FR8, 2026-10-28): the long run's dear stretch's, DEAR_RATE. |
| 123 | `OrderSearchCheck.TWIN_RATE` | `.40` | The policy rate the twin's months are held at (FW9, 2026-10-25). |
| 260 | `OrderSearchCheck.TWIN_TILLS` | `{.25,.5 }` | The shares of its whole cost the twin's till is put at under its largest order (tillsAsTheLine()). |

### PassThroughCheck.java ([map](map/PassThroughCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 123 | `PassThroughCheck.K` | `EconomyManager.PASS_THROUGH` | The speed the goods follow the D$ at, as the model has it. |
| 129 | `PassThroughCheck.MONTHS` | `240` | The months section 2 plays the real city, and the one it is saved at the end of. |
| 132 | `PassThroughCheck.STEP` | `1.10` | The size of the step in sections 1 and 3: the D$ 10% weaker. |
| 201 | `PassThroughCheck.BASE_SLOT` | `1` | The slot, in this harness's own folder (TempDirs), the base city is saved to and its twins are loaded from. |
| 246 | `PassThroughCheck.seen` | `new double [ MONTHS ], struck = new double [ MONTHS ]` | The D$ the strike saw, and the goods' D$ it struck, for each of section 2's months: section 6 reads them. |
| 356 | `PassThroughCheck.SWING` | `.05` | The size of the treasury's swing: dollars worth this share of a year's GDP. |
| 359 | `PassThroughCheck.HELD` | `12, FOLLOWED = 120` | The months the swing is bought, held and sold in, and the months it is followed for after: a year held, ten years in all. |
| 362 | `PassThroughCheck.HORIZON` | `HELD +(int) Math.ceil(Math.log(.5) / Math.log(1 - K))` | The swing's own horizon, from the model's constants: the round trip (HELD) and the months the goods take to feel half of it (their half-life, rounded up). |

### PlanCheck.java ([map](map/PlanCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 182 | `PlanCheck.TIMED` | `5` | Repeats a district's plan is timed over, after one untimed: its median is its time. |
| 193 | `PlanCheck.PLAN_MS` | `MapCheck.SCREEN_MS / 2` | The most a district's plan may take, the slowest of the dense screen's districts (each its median of TIMED): half MapCheck's screen, 40 ms (star) - one plan and a screen painted from plans within S... |
| 196 | `PlanCheck.TABLE` | `{ { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 }, { 48, 13, 6052, 3137, 2915, 0, 0, ...` | The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junction... |
| 202 | `PlanCheck.TABLE_BUILDINGS` | `3617, TABLE_BUILDING_PLOTS = 32217` | ...its buildings and their plots... |
| 204 | `PlanCheck.TABLE_BUDGET` | `{ 9388, 4484, 2242 }` | ...and the road of each budget, whole plots. |
| 207 | `PlanCheck.TEST_HUB_X` | `66, TEST_HUB_Y = 86` | The prototype's hub on its test district, in the district's plots: (70, 90) in its frame, 4 plots in. |
| 210 | `PlanCheck.TEST_UNOWNED_ROWS` | `40, TEST_UNOWNED_FROM = 196` | The part of the test district the prototype did not own: its first 40 rows east of plot 196 (O[:44, 200:] in its frame). |
| 213 | `PlanCheck.FILLS` | `{ 0.75, 0.80, 0.85, 0.92 }` | The fills of 2.10's table: the model's buildings and roads at these shares of a flat district's ground. |
| 216 | `PlanCheck.PROTO_FRAME` | `264` | The ground 2.10's fills are shares of: the prototype's frame, the district and 4 plots about it, 264 x 264 (its n = OFF + 32 x 8 + 4) - so its 75% is 79.8% of the district's own 256 x 256. |
| 219 | `PlanCheck.GRAVEL_TRIPS` | `900, PAVED_TRIPS = 1200` | Gravel's and paved road's trips a road (buildings.json's capacities, as the prototype read them): the same city paved holds his roads' trips on Paved Roads. |
| 229 | `PlanCheck.TEST_GROUND` | `"g36f14g13f1g14f29g89u61/g36f15g11f2g14f28g90u61/g35f17g10f2g13f29g90u61/g35f17g24f30g9...` | THE PROTOTYPE'S TEST DISTRICT (claude/roads-prototype/proto2.py and roads_proto.py, written out by batch RD1): district_ground(264, 7) - grass, forest, a river two to four plots wide, a bay of sea ... |
| 272 | `PlanCheck.TEST_NUDGE` | `{ 0.3727494347187473, 0.03906684873305479, 0.22912251263578634, 0.5245030267418446, 0.8...` | Each cell's nudge, h32(4127, 'cell', c) as a fraction, cell ci + cj x CELLS_A_SIDE. |
| 290 | `PlanCheck.TEST_ACROSS` | `2483539012620168834L` | Each cell's direction, a bit a cell: set where h32(4127, 'dir', c) is under a half (east-west), read as 0.25, else 0.75. |
| 292 | `PlanCheck.TEST_TYPES` | `{ 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31, 32, 33, 3...` | The types its buildings are, by id (BuildingVisual.table())... |
| 294 | `PlanCheck.TEST_COUNTS` | `{ 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22, 22, 19, ...` | ...how many of each, scaled_buildings at 70% of its dry ground... |
| 296 | `PlanCheck.TEST_DEAL` | `{ 0.8623860022826665, 0.4688361493186886, 0.49244847032280087, 0.9101025582510129, 0.04...` | ...and each type's start, h32(4127, 'deal', t) as a fraction. |
| 310 | `PlanCheck.TEST_GRAVEL` | `8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.870967741936` | Its road plots (Input.gravel and Input.paved): his Gravel and Paved Roads, and the same trips all paved (the third budget half of it). |
| 312 | `PlanCheck.TEST_PLACED_JERUS` | `- 1980402857778823524L, TEST_STREETS_JERUS = - 7143981648789130548L` | Each budget's plan as the prototype made it, its buildings' and its streets' FNV-1a digests (placedDigest(), streetDigest()): his roads... |
| 314 | `PlanCheck.TEST_PLACED_PAVED` | `9000504916410474359L, TEST_STREETS_PAVED = - 4283127041626870812L` | ...the same trips paved... |
| 316 | `PlanCheck.TEST_PLACED_THIN` | `9000504916410474359L, TEST_STREETS_THIN = 4092048347058802282L` | ...and half that. |
| 565 | `PlanCheck.ARM_DX` | `{ 1, - 1, - 1, 1 }, ARM_DY = { 1, 1, - 1, - 1 }` | The four avenues' headings (dx, dy) by arm, CityRuns.diagCode()'s numbering: south-east 0, south-west 1, north-west 2, north-east 3. |
| 828 | `PlanCheck.BUDGETS` | `{ "his roads", "the same trips paved", "half that" }` | The three budgets' names, as the sections print them. |
| 1239 | `PlanCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } @ Override...` | A stream that drops what is written to it: the game's and the playtest's prose while a section plays or loads a city. |
| 1300 | `PlanCheck.HOMES_ENOUGH` | `10` | The homes cells a played city's samples must hold before they are asked to show a layout other than the comb (CL2): ten cells all keeping the comb by the hash alone has a chance of 1 in 59,049. |
| 1307 | `PlanCheck.ESTATE_FILL` | `0.4` | The share of a flat district's plots each estate-band type is set at, alone: 0.4 - room to spare, so what differs is the cells they take. |
| 1401 | `PlanCheck.SPARE_FILL` | `FILLS [ 0 ], FULL_FILL = FILLS [ FILLS.length - 1 ]` | Section 4's fill with cells to spare (2.10's first, 75%) and its fullest (92%: every cell open, and outer kinds left without a place when the bands keep apart). |
| 1488 | `PlanCheck.LONG_ROADS` | `0.5` | LONG's roads, a factor of his (section 4's 75% fill): 0.5 - too little road for the ladder to square a homes cell, so all of them keep long blocks. |
| 1491 | `PlanCheck.SQUARED_ROADS` | `0.7` | SQUARED's: 0.7 - road enough to square most of the homes cells and not to make boulevards of them. |
| 1494 | `PlanCheck.BOULEVARD_ROADS` | `1` | BOULEVARD's: 1, his own - road enough for boulevards, squared cells among them. |
| 1497 | `PlanCheck.LAYOUT_FIXTURES` | `{ "LONG", "SQUARED", "BOULEVARD", "CUT0", "CUT1" }` | Section 10's fixtures: flat ground at his fill with his roads times a factor (the first three), and the test district's ground, a river and the sea cutting cells, on its first two budgets. |
| 1500 | `PlanCheck.COMB_DIGESTS` | `{ { - 8123742265030361694L, - 580478996347052330L }, { 6065673935051394403L, 4482261714...` | Each fixture's plan with Input.combOnly, its buildings' and its streets' digests (placedDigest(), streetDigest()) as 0.8.11 drew them: the switch off is that plan, box for box and street for street. |
| 1508 | `PlanCheck.SHARE_SIDE` | `200` | The cells on a side of the square of the lattice layoutOf() is asked about to measure its shares: 200, 40,000 cells. |
| 1511 | `PlanCheck.SHARE_TOLERANCE` | `0.01` | How far a layout's share of those cells may stand from its dial: 0.01. |
| 1514 | `PlanCheck.MORE_HOMES` | `0.1` | The part more of the homes band's largest type LONG is given, to see a cell keep its layout as its district grows: a tenth. |
| 1517 | `PlanCheck.OTHER_SEED` | `Founding.DEFAULT_WORLD_SEED + 1` | Another world's seed, to see the layouts read it. |
| 2042 | `PlanCheck.BATCHES` | `40` | The batches the dense screen's counts are dealt in (h): forty months. |
| 2045 | `PlanCheck.COVER_LEAST` | `0.95` | What the order of arrival must cover of the ground today's plans cover on the dense screen: 95%. |
| 2468 | `PlanCheck.HOUSES_A_MONTH` | `1000` | The houses each month from the first to the fifth brings: with the shops, works and farms beside them, enough to fill the old ground. |
| 2937 | `PlanCheck.FAR_CELLS` | `(int) DistrictPlan.CRESCENT_FROM + 4` | How many cells west of the frame section 14's far fixture puts the founding site: CRESCENT_FROM and four more, so every cell of the frame lies beyond CRESCENT_FROM. |
| 2940 | `PlanCheck.CROWDED_FILL` | `0.90, CROWDED_ROADS = 0.7` | Section 14's crowded fixture: 90% of the ground, with 0.7 of his roads - the ladder takes several builds (the later ones from its turns), squares a few homes cells and leaves a few buildings out, a... |
| 2943 | `PlanCheck.ROAD_RICH` | `BOULEVARD_ROADS` | The roads section 14's road-rich fixture is given, a factor of his: BOULEVARD_ROADS, 1 - road enough for the ladder to square cells and make boulevards. |
| 2946 | `PlanCheck.MID_WEST` | `1.25` | Section 14 (b)'s site, in cells west of the frame's edge short of CRESCENT_FROM: 1.25 - the frame's first column of cells lies within CRESCENT_FROM of it and its second beyond. |
| 2949 | `PlanCheck.ARM_SITE` | `21 * DistrictPlan.CELL` | Section 14 (c)'s site for an avenue across the frame: (-ARM_SITE, -ARM_SITE), 21 cells out along the diagonal, so its south-east arm runs through the frame's diagonal cells - the ones nearest it, w... |
| 3437 | `PlanCheck.HOUSES_A_MONTH_FAR` | `3000` | The houses each month brings in (f): enough to open a homes cell or three a month beyond CRESCENT_FROM. |
| 3554 | `PlanCheck.RIM_FROM` | `3` | The rim of the dense city (rimCurves()): districts this many or more from the one the founding site lies in, by the larger of their columns and rows. |
| 3557 | `PlanCheck.RIM_EVERY` | `37` | ...and of those, one in this many is planned: 37, a stride that takes districts all round it. |
| 3742 | `PlanCheck.RUN_HOUSES` | `250` | The houses each month of the runs' fixtures brings: with the shops, works and farms beside them, a district that grows by some hundreds of boxes a month. |

### PlaytestFixCheck.java ([map](map/PlaytestFixCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 52 | `PlaytestFixCheck.OUT` | `System.out` |  |
| 53 | `PlaytestFixCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### PolicyPreviewCheck.java ([map](map/PolicyPreviewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 87 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 159 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "800" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders (the houses 800 since PT1-M4: see city()). |

### PopulationCheck.java ([map](map/PopulationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `PopulationCheck.ADULT_MIX` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share these fixtures run at. |

### PortCheck.java ([map](map/PortCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 118 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 123 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 126 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 129 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... |
| 131 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` | ...dry Capesize, Panamax... |
| 133 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` | ...deep-sea boxes... |
| 135 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` | ...and general cargo. |
| 436 | `PortCheck.MAIN_GENERAL_BERTH_TONNES_A_YEAR` | `450_000` | Tonnes a year of a General Cargo Terminal on main at 0.8.66, before SL1: the berth the default playtest stood full 35 of at m3996 (544,000 people). |
| 438 | `PortCheck.MAIN_GENERAL_BERTHS_AT_M3996` | `35` | ...and how many it stood, all full, at m3996. |
| 444 | `PortCheck.SPLIT_ROUNDING` | `2` | How far a terminal's cash, points or materials may sit from its ratio's rounding: 2. |

### QuarryingCheck.java ([map](map/QuarryingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `QuarryingCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 85 | `QuarryingCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 117 | `QuarryingCheck.STONE` | `Resource.STONE` | Stone, the resource its quarries dig. |
| 119 | `QuarryingCheck.LIME` | `Deposit.FieldType.LIMESTONE, HARD = Deposit.FieldType.HARD_ROCK` | Stone's two types of field (Resource.typesOf()): limestone, and hard rock. |
| 295 | `QuarryingCheck.ROWS` | `{ Quarrying.QUARRY, Quarrying.LARGE_QUARRY, Quarrying.DIMENSION_STONE_QUARRY }` | Quarrying's rows, by their permanent ids, in the study's 6.4 order. |
| 298 | `QuarryingCheck.STUDY_COSTS` | `{ 15_000, 90_000, 6_000 }` | The study's 6.4: each one's cost, D$ thousands all in. |
| 301 | `QuarryingCheck.STUDY_POSTS` | `{ 22, 125, 17 }` | ...its posts. |
| 304 | `QuarryingCheck.STUDY_RUNS` | `{ 163, 981, 104 }` | ...its running cost a month, D$ thousands. |
| 307 | `QuarryingCheck.STUDY_MAKES` | `{ 40_000, 250_000, 1_000 }` | ...what it makes a month, t: stone, stone, blocks. |
| 310 | `QuarryingCheck.STUDY_SITES` | `{ 1, 3, 1 }` | ...the sites it takes, and of which type (null for either; the study's 6.4). |
| 312 | `QuarryingCheck.STUDY_TYPES` | `{ null, null, HARD }` | ...and the type of field each stands on (null for either). |
| 315 | `QuarryingCheck.BLOCKS_DIG` | `3` | ...and the tonnes of rock a tonne of blocks takes (spec 2.4: "dimension stone 3"); stone is a tonne a tonne. |
| 318 | `QuarryingCheck.CRUSH_KWH` | `1.7` | The research's crushing, kWh a tonne of stone dug: 1.7, the middle of the study's 6.2 "1.6-1.8" [S18]. |
| 321 | `QuarryingCheck.HOURS` | `730` | The hours of a month the study reads its kW at: its 2.2, "3,700 kW for 6,000 t a month is 450 kWh a tonne" - 450 x 6,000 / 3,700, 730 to the hour. |
| 324 | `QuarryingCheck.STUDY_ROUNDING` | `.05, STUDY_ROUNDING_POINT =.5` | The study's rounding: its returns to a tenth of a point, "about 10%" to the point. |
| 327 | `QuarryingCheck.STUDY_EXPORTING_LOW` | `1.6, STUDY_EXPORTING_HIGH = 1.7, STUDY_REPLACING = 10` | The returns the study's 6.5 states, % of the cost a month: the Quarry's and the Large Quarry's exporting at D$10, and replacing imports at D$40. |
| 634 | `QuarryingCheck.FOUND_KM2` | `.3` | The dry ground a fixture town is founded on round its field, km2: the default city's rings (CityLand.found()). |
| 637 | `QuarryingCheck.MAX_RING` | `6` | How many cells round the founding site's the fixture looks for its field in. |

### RailDepotCheck.java ([map](map/RailDepotCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `RailDepotCheck.LORRY_A_TONNE` | `.319` | What a lorry charges for a tonne in the model's money: BuildingManager's RAIL banner measures $319 a tonne in the played city, and the fixtures' freight is the planner's to value, not this harness's. |

### RailDrawCheck.java ([map](map/RailDrawCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 131 | `RailDrawCheck.T` | `TilePainter.TILE` |  |
| 134 | `RailDrawCheck.TYPES` | `BuildingVisual.table(new BuildingCatalog().load())` | The game's building templates, as the map draws them. |

### RefineryCheck.java ([map](map/RefineryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 155 | `RefineryCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 157 | `RefineryCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 453 | `RefineryCheck.UNITS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkylation Unit...` | The units the town stands beside its Oil Refinery: one small one of every kind but the asphalt unit, which medium crude cannot feed. |
| 570 | `RefineryCheck.PROTOTYPE_WAGE` | `4.84` | The prototype's frame (spread.py): wages a post-month, world $k (m4000's Refining, 736k over 200 posts at 760). |
| 573 | `RefineryCheck.PROTOTYPE_RATE` | `.005` | ...and its money test, a month on the whole cost (its RATE). |
| 576 | `RefineryCheck.PROTOTYPE_REFINERY_CAPITAL` | `78_000` | ...and its Oil Refinery's capital: D$60M and its 1,000 materials at 18 (spread.py's TOPPING); every other building its cash. |
| 579 | `RefineryCheck.PROTOTYPE_BUILD_MONTHS` | `6` | ...and the months an order takes to open. |
| 582 | `RefineryCheck.PROTOTYPE_MONTHS` | `240` | ...and the months it runs. |
| 585 | `RefineryCheck.M4000_PETROL` | `7.63e6, M4000_DIESEL = 16.13e6` | The prototype's demands, litres a month of petrol and diesel: the playtest's city at m4000 (spread.py's cities). |
| 588 | `RefineryCheck.FORTY_WELLS` | `40 * 415` | ...and its wells in the 40-well rows: 40 land wells' 415 t a month. |
| 931 | `RefineryCheck.PLANNER_FILES` | `new java.util.IdentityHashMap<>()` | Each planner town's save folder (plannerTown()), for the sections that save one and load its twin. |
| 1175 | `RefineryCheck.TOWN_WELLS` | `30` | Wells the town of section 14 stands under its Oil Refinery (OilCheck 4's thirty): more crude than it runs, its barrel paying at their price. |
| 1178 | `RefineryCheck.STUDY_LORRY` | `-.100, STUDY_RAIL = -.047, STUDY_FLOOR = -.007, STUDY_TANKER =.023, STUDY_CONVERTED =.093` | The study's margins (the project's why-the-refinery-loses.md, on 0.7.99), a share of crude's world middle: a topping barrel of medium crude on imported crude, all exported, by lorry, by rail at .60... |
| 1181 | `RefineryCheck.STUDY_HOME_FLOOR` | `.014, STUDY_HOME_RAIL = -.017` | ...and the barrels the city's own petrol and diesel take, at the net import price: at the floor, and at .60. |
| 1184 | `RefineryCheck.STUDY_QUOTE` | `.60` | The study's railway above its floor: a quote of .60 of a lorry's rate. |
| 1187 | `RefineryCheck.STUDY_ROUNDING` | `.0006` | The study's figures are to a tenth of a point, and its tanker's +2.3% is +2.25% here: within six hundredths. |
| 1190 | `RefineryCheck.HIS_CRUDE` | `5 * 415_000 + 8_300` | Jerus's campus: five Crude Units and an Oil Refinery - the study's 500,000 b/d topping plant. |
| 1193 | `RefineryCheck.NINE` | `{ Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRI...` | The nine products, as Refining lists them. |

### RefineryViewCheck.java ([map](map/RefineryViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 95 | `RefineryViewCheck.PX` | `1e-6` | ...and a picture's pixels: within a millionth of one. |
| 98 | `RefineryViewCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each town's scratch files, so a twin can load its save and the end can clean every folder up. |
| 101 | `RefineryViewCheck.CAMPUS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkylation Unit...` | The units of the campus town: one small of each kind but the asphalt unit (heavy crude's), so the cracking unit and the coker feed alkylation and the hydrocracker and the coker the reformer. |
| 105 | `RefineryViewCheck.WELLS` | `2` | Land wells on fiat sites in the campus town - fewer than its refinery runs on, so it imports the rest - and the oil each site holds. |
| 107 | `RefineryViewCheck.SITE_TONNES` | `5_000_000` | ...the tonnes each of those sites holds. |
| 110 | `RefineryViewCheck.MONTHS` | `24` | Months the towns play before they are read: two years, so the households own cars and drive (OilCheck 4's). |

### ReloadCheck.java ([map](map/ReloadCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 80 | `ReloadCheck.CHECKPOINTS` | `{ 30, 120, 360, 1200 }` | The months the live city is saved at; LongPlaytest.probePlay overshoots by a few, so a point reads g.getMonth(). |
| 83 | `ReloadCheck.EARTH_CHECKPOINT` | `120` | The month the Earth city is saved at (section 4). |
| 86 | `ReloadCheck.EARTH_SPOT` | `"rotterdam"` | Where section 4's city is founded: the real Earth round Rotterdam, as LongPlaytest --earth rotterdam plays it. |
| 89 | `ReloadCheck.SKIP_MONTHS` | `2` | A time skip of section 3f: two months, so it is a skip (the game autosaves before one), and two are run on each city. |
| 92 | `ReloadCheck.CENT` | `0.005` | Two numbers are the same when they differ by no more than a cent... |
| 95 | `ReloadCheck.RELATIVE` | `1e-9` | ...and by no more than a billionth of the larger (or of 1, for the tiny). |
| 103 | `ReloadCheck.EXEMPT` | `Set.of("slotName", "savedAt")` | The keys no two saves of one city share, whatever it did: the slot's name and the time it was written. |
| 106 | `ReloadCheck.SHOWN` | `12` | How many differing paths a failing line lists, each with both values; the rest is counted. |
| 108 | `ReloadCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |

### RestaurantsCheck.java ([map](map/RestaurantsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `RestaurantsCheck.DINER` | `"Diner"` |  |
| 71 | `RestaurantsCheck.RESTAURANT` | `"Restaurant"` |  |

### RestructureCheck.java ([map](map/RestructureCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 531 | `RestructureCheck.DOLLAR_ASK` | `5_000` | What the dollar round trip borrows: small against what its city earns, so the world's premium stays low. |

### RoadCheck.java ([map](map/RoadCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 156 | `RoadCheck.GRAVEL` | `ConstructionControl.PAVE_FROM, PAVED = ConstructionControl.PAVE_TO, HIGHWAY = "Elevated...` | The three roads by name: the paving's from and to, and the highway. |
| 160 | `RoadCheck.ROADS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS)` | The advice's measure for roads, which the sections here read the site, the units and the cards through. |

### RunWorksCheck.java ([map](map/RunWorksCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 114 | `RunWorksCheck.CASH` | `1e10` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 117 | `RunWorksCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 120 | `RunWorksCheck.OWNED` | `24_000_000` | The ground a fixture city owns, square feet: a Freight Line (2.4 million) and the buildings beside it fit in it. |
| 122 | `RunWorksCheck.HOUSE` | `"House", HIGHWAY = "Elevated Highway", SPUR = "Rail Spur", LINE = "Freight Line"` |  |
| 645 | `RunWorksCheck.BUSINESS_CASH` | `4e6` | The cash a business is given to pay a run's order from by fiat, thousands: the Freight Line's price and more, in a figure small enough for the sectors' statements to be exact to the cent. |
| 887 | `RunWorksCheck.HIGHWAY_DEPOTS` | `20, FREIGHT_DEPOTS = 100` | The depots a fixture's builders are given for 10: enough that a few highways (a Freight Line) take them some years, not centuries. |
| 1104 | `RunWorksCheck.T` | `TilePainter.TILE` |  |
| 1107 | `RunWorksCheck.HY` | `6, HX0 = 8, TX = 4, BARE = 20` | The fixture's rows and columns: the highway along row HY from column HX0, the track down column TX, and row BARE with no run. |

### SaveFileCheck.java ([map](map/SaveFileCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 3365 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

### SaveSlotCheck.java ([map](map/SaveSlotCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 348 | `SaveSlotCheck.GROWN_MONTHS` | `180` | How many months 7c grows its city: long enough that the lists and books a city reaches by itself hold something - the inbox is empty at month 124 of this fixture and has a notice by 183, the bonds'... |
| 670 | `SaveSlotCheck.SILENT` | `new PrintStream(OutputStream.nullOutputStream())` | The stream the model's per-month printing goes to while 7c steps months: each month prints some forty lines, and nobody reads them here. |

### ScaleCheck.java ([map](map/ScaleCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 108 | `ScaleCheck.TARGET_PEOPLE` | `5e9` | Jerus's smaller plan, in people: what the copy is scaled to reach. |
| 111 | `ScaleCheck.MONTHS_AT_SIZE` | `3` | Months the copy plays before it is saved. |
| 114 | `ScaleCheck.FIXTURE_MONTHS` | `120` | The founded city's age, in months, when it is saved and copied. |
| 117 | `ScaleCheck.FREE_PEOPLE` | `{ 5e9, 1e10 }` | Jerus's two plans, in people: the copies whose sectors are free (section 6). |
| 268 | `ScaleCheck.FREE_FIXTURE_MONTHS` | `807` | The month the playtest's city is copied at for section 6. |
| 271 | `ScaleCheck.FREE_MONTHS` | `6` | Months each free copy plays, every one timed. |
| 274 | `ScaleCheck.RELATIVE_RESIDUAL` | `1e-10` | The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. |
| 284 | `ScaleCheck.MONTH_MEDIAN_MS` | `1500` | The longest a free copy's median month may take, in milliseconds. |
| 287 | `ScaleCheck.TRACK_TIMES` | `1000` | The copy section 9 reads against its city: its factor, K (FW9, 2026-10-25). |
| 295 | `ScaleCheck.TRACK_PEOPLE` | `.015` | How far the copy's people may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 298 | `ScaleCheck.TRACK_CASH` | `.0125` | How far the copy's treasury cash may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 301 | `ScaleCheck.TRACK_SAVINGS` | `.016` | How far the copy's household savings may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 309 | `ScaleCheck.TRACK_GDP` | `.14` | How far the copy's month GDP may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 312 | `ScaleCheck.TRACK_DEPOSITS` | `.026` | How far the copy's bank deposits may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 315 | `ScaleCheck.TRACK_PRICES` | `.004` | How far the copy's price level (PriceIndex.getIndex()) may miss the city's own, as a share of it, in any of FREE_MONTHS. |
| 322 | `ScaleCheck.TRACK_UNEMPLOYMENT` | `.033` | How far the copy's unemployment rate may stand from the city's, in rate points (.01 is a point), in any of FREE_MONTHS. |
| 325 | `ScaleCheck.TRACK_POLICY` | `.0005` | How far the copy's policy rate may stand from the city's, in rate points, in any of FREE_MONTHS. |
| 328 | `ScaleCheck.REGISTER_WITHIN` | `1e-9` | The most a register may stand from what its holders hold of it, as a share of the larger: the shares, the corporate bonds and the city's paper against the households' own (section 9). |
| 860 | `ScaleCheck.Figures.NAMES` | `{ "people", "cash", "savings", "gdp", "deposits", "prices", "unemployment", "policy" }` |  |
| 862 | `ScaleCheck.Figures.EXTENSIVE` | `{ true, true, true, true, true, false, false, false }` | Whether each figure is a quantity (K times the city's) or a level (the city's own). |
| 863 | `ScaleCheck.Figures.WITHIN` | `{ TRACK_PEOPLE, TRACK_CASH, TRACK_SAVINGS, TRACK_GDP, TRACK_DEPOSITS, TRACK_PRICES, TRA...` |  |
| 924 | `ScaleCheck.AFFORDABILITY_WITHIN` | `.01` | How far a free copy's affordability pull may stand from the city's in any month (0.7.55): the rents' multiplier on the migrants' target (Migration.affordabilityPull()). |
| 1266 | `ScaleCheck.INTENSIVE` | `Pattern.compile("(price\|Price\|rate\|Rate\|month\|Month\|Months\|share\|Share\|ratio\|Ratio\|targ...` | A name the scaler leaves alone wherever it meets one, unless EXTENSIVE names it: a price, a rate, a month, an id, a share, a ratio or a target, as scale_save.py's SKIP read them, and since 0.7.54 t... |
| 1290 | `ScaleCheck.EXTENSIVE` | `java.util.Set.of("recognisedThisMonth", "escalationThisMonth", "repairsThisMonth", "las...` | THE NAMES THE PATTERN READ WRONG (0.7.54). |
| 1297 | `ScaleCheck.TOP` | `{ "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxCharged", "...` | The save's top-level keys scaled: scale_save.py's list, less the works yard. |
| 1312 | `ScaleCheck.NAMED` | `java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks", "sectorBooksBefo...` | The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. |

### SectorBooksCheck.java ([map](map/SectorBooksCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `SectorBooksCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### SectorFlowCheck.java ([map](map/SectorFlowCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `SectorFlowCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 100 | `SectorFlowCheck.ORDERS` | `{ { "House", "400" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders, and whether each went on site. |
| 106 | `SectorFlowCheck.built` | `new java.util.LinkedHashMap<>()` | ...whether each went on site, as city() found it. |

### SectorStatementCheck.java ([map](map/SectorStatementCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 93 | `SectorStatementCheck.CENT` | `.00001` | A cent, in the model's thousands. |
| 131 | `SectorStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of one city's run, by identity. |
| 240 | `SectorStatementCheck.bondsSoonBefore` | `new LinkedHashMap<>()` | Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. |
| 765 | `SectorStatementCheck.NEW_FIELDS` | `{ "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts", "bondsWrit...` | The S2 fields a save from before 0.7.75 does not carry in its books' months. |

### SlopeCheck.java ([map](map/SlopeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 87 | `SlopeCheck.CASH` | `1e8` | A fixture city's treasury: more than any order here costs, so a refusal is never for cash. |
| 90 | `SlopeCheck.BUSINESS_CASH` | `1e6` | ...and the cash the Real Estate sector is given to pay a business's order from by fiat: an order's worth and more, in a figure small enough for the sectors' statements to be exact to the cent (a fi... |
| 93 | `SlopeCheck.TOL` | `1e-9` | What a fixture of figures near one another can be told apart by: a rounding, not ground. |
| 96 | `SlopeCheck.GROUND` | `{ "flat", "foothill", "steep", "very steep" }` | The relief classes' names, in World's order (FLAT to VERY_STEEP). |
| 98 | `SlopeCheck.HOUSE` | `"House"` |  |
| 101 | `SlopeCheck.HOME_CARE` | `"Home Care Service"` | A building of the city's own, which the city may demolish (Game.demolish(): a House is a landlord's) and the builders take a few months to put up. |
| 170 | `SlopeCheck.ORDER` | `5` | How many Houses the fixtures of 1 order at once. |
| 271 | `SlopeCheck.FLAT_SHARE` | `0.4, FOOTHILL_SHARE = 0.3, STEEP_SHARE = 0.3` | The mixed ground's shares: 40% flat, 30% foothill, 30% steep. |
| 417 | `SlopeCheck.PATIENCE` | `120` | The most months a fixture waits for the builders before it says they did not come. |
| 554 | `SlopeCheck.HOUSES` | `100` | Houses ordered in 6, enough that the pad alone (their land's points) is more than a month of the builders' work. |
| 624 | `SlopeCheck.FOOT_COLUMNS` | `DistrictPlan.CELL, STEEP_FROM = 3 * DistrictPlan.CELL + DistrictPlan.CELL / 8, STEEP_RO...` | Where the bands of 7's district lie, in plots: foothill the first cell's columns; steep, then very steep, then ice rows below. |
| 629 | `SlopeCheck.VERY_FROM` | `STEEP_FROM + STEEP_ROWS, ICE_FROM = VERY_FROM + VERY_ROWS, ICE_TO = ICE_FROM + ICE_ROWS` | The bands' rows: steep [STEEP_FROM, VERY_FROM), very steep [VERY_FROM, ICE_FROM), ice [ICE_FROM, ICE_TO). |
| 757 | `SlopeCheck.MEDELLIN` | `{ 6.25, - 75.57 }` |  |
| 760 | `SlopeCheck.SUMMIT` | `EarthTerrainCheck.SUMMIT, SLOPES = { 27.60, 86.70 }` | Everest's summit: ice (EarthTerrainCheck's SUMMIT) - and a spot on its steep southern slopes, no ice about it, no buildable ground within 5 km. |
| 834 | `SlopeCheck.RUN_MONTHS` | `2000` | The months the Medellin run plays: enough for the land office's offers to have brought the city steep ground (at 1,000 it owns none; at 2,000, about a tenth of it) - the section about twenty seconds. |

### StaleCheck.java ([map](map/StaleCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 213 | `StaleCheck.PLANTED` | `""" package ham.citybuildersim; /** * A fixture with three defects planted in it. * * I...` | Three planted lies: a stranded javadoc, a file that is not there, and a header out by one. |
| 240 | `StaleCheck.ALPHA` | `""" package ham.citybuildersim; /** A fixture class for tools.Names.*/ public class Alp...` | Section 6's tree: members declared the ways a regex over source misses. |
| 259 | `StaleCheck.BETA` | `""" package ham.citybuildersim; /** A fixture subclass for tools.Names.*/ public class ...` | A subclass: what it inherits resolves on it, and what it declares does not resolve on Alpha. |
| 270 | `StaleCheck.GAMMA` | `""" package ham.citybuildersim; /** Added on the branch; this comment names madeUpInJav...` | A file the branch adds, read as the branch has it. |
| 281 | `StaleCheck.NAMES_DIFF` | `""" diff --git a/fixture/names.md b/fixture/names.md --- a/fixture/names.md +++ b/fixtu...` | The diff: a document's six lines (2 to 7), the Java file whole, and a generated page. |
| 312 | `StaleCheck.SOUND` | `""" package ham.citybuildersim; /** * A fixture with nothing wrong with it. * * The one...` | The same shapes, all of them true. |

### SupplierCreditCheck.java ([map](map/SupplierCreditCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 85 | `SupplierCreditCheck.FARM` | `Sectors.AGRICULTURE, LORRIES = Sectors.AUTOMOTIVE` | The two local suppliers the rule's fixtures name: the farms, which fill the shelf's meat, and the car makers, which fill the fleet's vans. |

### TimingCheck.java ([map](map/TimingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `TimingCheck.FIXTURE_BUDGET_MS` | `0` | The fixture's own budget, in milliseconds: zero, so its measured figure always misses; never a real dial's budget. |
| 91 | `TimingCheck.SOURCES` | `Paths.get("src", "main", "java", "ham", "citybuildersim")` | Where the sources lie, seen from the repository root, which is where the suite runs. |
| 94 | `TimingCheck.COMPARES_DIAL` | `Pattern.compile("(<=?\|>=?)\\s*\\(?([A-Za-z]+\\.)?[A-Z][A-Z_]*_(MS\|NS)\\b" + "\|\\b[A-Z][...` | A line that compares a figure against a *_MS or *_NS dial, either way round: what a timing assertion looked like before it went through Timing. |
| 99 | `TimingCheck.ALLOWED_FILE` | `"MapCheck.java"` | The file of the one allowance: the line below is a test of the clock's own resolution, which asserts no figure, so it stays a bare comparison. |
| 102 | `TimingCheck.ALLOWED_TEXT` | `"cpuStepMs()<= CPU_STEP_MS"` | The one line the dial rule allows outside Timing: MapCheck deciding whether the thread-CPU clock is fine enough to time with at all (a test of the clock's resolution, not an assertion). |
| 105 | `TimingCheck.FIXTURE_HARNESS` | `"TimingCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 108 | `TimingCheck.ALL_SOURCES` | `Paths.get("src", "main", "java")` | Where the walk of every source starts. |
| 617 | `TimingCheck.PAIR_AWK` | `Paths.get("tools", "timing-pair.awk")` |  |

### TradeCostCheck.java ([map](map/TradeCostCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 70 | `TradeCostCheck.DELIVERED` | `{ { Good.CROPS,.44,.28 }, { Good.GRAINS,.00080,.00050 }, { Good.BREAD,.00250,.00160 }, ...` | The delivered prices, as they were on 2026-09-16 before a line of this was written, hard-coded on purpose. |

### TreasuryCheck.java ([map](map/TreasuryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 113 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### TruckRouteCheck.java ([map](map/TruckRouteCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 107 | `TruckRouteCheck.STEP` | `CityMap.WORLD_STEP` | How finely a leg is read, in plots: a quarter, as the world lines are (CityMap.WORLD_STEP). |
| 110 | `TruckRouteCheck.EPS` | `1e-9` | The tolerance of a position in plots, well below anything the map measures. |
| 117 | `TruckRouteCheck.TRIPS_MORE_DEPOTS` | `12, TRIPS_HIGHWAYS = 60` | Section 7's trips city: MapCheck's access city with 12 Truck Depots more and 60 Elevated Highways in place of its 200. |

### WellCheck.java ([map](map/WellCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 129 | `WellCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder (section 6). |
| 132 | `WellCheck.WELL_TONNES` | `415` | A well's nameplate a month, the template's: 415 t, a hundred barrels a day. |
| 135 | `WellCheck.FILL` | `50_000` | The Strategic Reserve's fill a month in the sea sections (OilCheck 16's order): more than the wells lift, so the city buys every tonne of theirs at home. |
| 138 | `WellCheck.PLENTY` | `50_000_000` | The oil a town is handed: far more than its wells lift in the months a section runs, so the ground never limits them. |
| 930 | `WellCheck.GROUND` | `2 * WELL_TONNES` | Section 13's ground pool: two months of a land well's nameplate, so it is worked out inside the months the section runs. |
| 933 | `WellCheck.LAST_AT_SEA` | `100` | Section 13's offshore pool left for its other half: less than a month of the platform wells' lift, so it is worked out in one. |

### WoodProductsCheck.java ([map](map/WoodProductsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 81 | `WoodProductsCheck.out` | `System.out` | Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. |
| 83 | `WoodProductsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` | A stream that drops what is written to it: the game's prose while a fixture town is founded and played. |
| 119 | `WoodProductsCheck.GOODS` | `{ Good.LUMBER, Good.PANELS, Good.RESIDUES, Good.PULP, Good.PAPER, Good.WOOD_PELLETS }` | Wood's six goods, in Good's order (the spec's 2.3). |
| 121 | `WoodProductsCheck.STUDY_IMPORT` | `{.340,.420,.045,.760, 1.050,.220 }` | The study's 5.3: each one's import price, D$ thousands a cubic metre or a tonne. |
| 123 | `WoodProductsCheck.STUDY_EXPORT` | `{.250,.260,.015,.600,.880,.150 }` | ...and its export price. |
| 125 | `WoodProductsCheck.STUDY_UNIT` | `{ "cubic metre", "cubic metre", "cubic metre", "tonne", "tonne", "tonne" }` | ...its unit. |
| 127 | `WoodProductsCheck.TONNES_A_M3` | `{.5,.62,.9, Double.NaN, Double.NaN, Double.NaN }` | ...the tonnes a cubic metre of it weighs (spec 2.3, est.: sawn lumber 0.5, OSB 0.62 [W35], chips green as the logs, 0.9); NaN for one by the tonne. |
| 129 | `WoodProductsCheck.STOCKABLE` | `{ true, true, false, true, true, true }` | ...and whether it keeps in a yard (spec 2.3: all but the residues). |
| 131 | `WoodProductsCheck.FREIGHT_SHARE` | `.375` | Good.java's rule: freight three eighths of the wedge. |
| 217 | `WoodProductsCheck.ROWS` | `{ WoodProducts.SAWMILL, WoodProducts.LARGE_SAWMILL, WoodProducts.PANEL_MILL, WoodProduc...` | The rows, by their permanent ids, in the study's 5.4 order. |
| 221 | `WoodProductsCheck.STUDY_COSTS` | `{ 15_000, 150_000, 140_000, 260_000, 150_000, 30_000 }` | The study's 5.4: each one's cost, D$ thousands all in. |
| 224 | `WoodProductsCheck.STUDY_POSTS` | `{ 35, 130, 100, 200, 120, 30 }` | ...its posts. |
| 227 | `WoodProductsCheck.STUDY_MADE` | `{ { Good.LUMBER, Good.RESIDUES }, { Good.LUMBER, Good.RESIDUES }, { Good.PANELS }, { Go...` | ...what it makes a month. |
| 230 | `WoodProductsCheck.STUDY_MAKES` | `{ { 2_080, 1_640 }, { 20_020, 15_785 }, { 15_000 }, { 11_000 }, { 5_500 }, { 6_000 } }` | ...and how much of each, a month (cubic metres or tonnes). |
| 233 | `WoodProductsCheck.STUDY_FEED` | `{ { Good.SAWLOGS }, { Good.SAWLOGS }, { Good.PULPWOOD }, { Good.PULPWOOD, Good.RESIDUES...` | ...what it takes a month. |
| 236 | `WoodProductsCheck.STUDY_USES` | `{ { 4_000 }, { 38_500 }, { 27_750 }, { 28_000, 21_500 }, { 5_775, 850_000 }, { 13_200 } }` | ...and how much of each (the paper mill's fuel in litres, the study's 5.6). |
| 239 | `WoodProductsCheck.STUDY_RUNS` | `{ 177, 844, 1_360, 2_170, 1_380, 183 }` | ...its running cost a month, D$ thousands, the study's 5.4 "Runs" - power bought from the grid only (its 2.4), the paper mill's fuel in it. |
| 242 | `WoodProductsCheck.STUDY_TABLE` | `{ { - 0.5, 0.7, 2.3, 1.1 }, { 0.1, 1.3, 2.8, 1.6 }, { 0.0, 1.1, 2.8, 1.7 }, { 0.4, 1.3,...` | The study's 5.5 table, % of the cost a month, cases A to D. |
| 246 | `WoodProductsCheck.STUDY_ROUNDING` | `.05` | The study's rounding: its returns to a tenth of a point. |
| 249 | `WoodProductsCheck.PULP_GEN` | `13_500` | The pulp mill's power past its own draw, kW (the study's 1.6: 11,000 t at 0.9 MWh a tonne [W42]). |
| 252 | `WoodProductsCheck.PAPER_KW` | `4_900, PAPER_WATER = 1_450, PAPER_HA = 19` | The paper mill's draw, kW, its water units and its ground, ha (the study's 5.6). |
| 255 | `WoodProductsCheck.PULP_WATER` | `11_600` | The pulp mill's water units (the study's 5.4). |
| 258 | `WoodProductsCheck.PAPER_POSTS` | `{ 50, 45, 20, 5 }` | The paper mill's posts by tier (the study's 5.4: 50/45/20/5). |
| 261 | `WoodProductsCheck.SAWMILL_KWH` | `104` | The sawmills' power, kWh a cubic metre of lumber: the middle of the research's 99-109 [W27]. |
| 263 | `WoodProductsCheck.OSB_KWH` | `207` | ...the panel mill's, a cubic metre of OSB [W34]. |
| 265 | `WoodProductsCheck.HOURS` | `730` | The study's month in hours (its 2.2: the Steel Mini-Mill's 3,700 kW for 6,000 t is 450 kWh a tonne). |
| 268 | `WoodProductsCheck.HEAVY` | `{ 54, 35, 9, 2 }, PULP_AND_PAPER = { 42, 37, 17, 4 }` | The posts' families (spec 2.2), no diploma / diploma / college / university, %: the heavy plants' and pulp and paper's. |
| 271 | `WoodProductsCheck.SQ_FT_A_HA` | `107_639.104` | Square feet in a hectare. |
| 632 | `WoodProductsCheck.RELOAD_RESIDUAL_K` | `.01` | A reloaded city's known residual a month on in money, D$ thousands (QUEUE.md's FW6): ten dollars, past the 2.8 measured. |
| 635 | `WoodProductsCheck.RELOAD_RESIDUAL` | `1e-6` | ...and in what a yard holds, a share of it: a millionth. |

### WorldCheck.java ([map](map/WorldCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `WorldCheck.SEEDS` | `{ Founding.DEFAULT_WORLD_SEED, 77, 2026 }` | The default world, and two more: 77, and 2026, whose search hands on to a second cell. |
| 93 | `WorldCheck.SAMPLE` | `200_000` | Points in each world's independent sample: 200,000 (a lake share's error about 0.1 point). |
| 96 | `WorldCheck.TOTALS_MS` | `200` | The spec's bound on the totals pass, in ms (measured 30). |
| 99 | `WorldCheck.TILE_MS` | `0.5` | The spec's bound on a tile, in ms (measured 0.07). |
| 102 | `WorldCheck.TILE_AGREES` | `0.995` | The share of a tile's plots its terrain must agree with the point function on. |
| 105 | `WorldCheck.COASTAL_CELLS` | `300` | Cells, neither all sea nor all land, whose fields are drawn to see where ore and oil lie: 300 (about 100,000 iron fields). |
| 108 | `WorldCheck.GRADE_CELLS` | `95` | The square of cells round the site whose oil fields are graded (section 7): 95 a side, 9,025 cells - some 20,000 fields, a grade's share then within a third of a point (one standard error); 30 a si... |
| 111 | `WorldCheck.NO_IRON_SEED` | `14` | A world whose first plot passing a founding's three tests has none of the old world's iron within World.SITE_IRON_KM (section 8): 14, the first such seed from 1 (fixW1-notes.md; 4127, 77 and 2026 f... |
| 114 | `WorldCheck.CLUSTER_CELLS` | `6` | The square of cells round the site whose iron fields' nearest neighbours are measured (section 8): 6 a side, 36 cells. |
| 117 | `WorldCheck.SHELF_WITHIN` | `.005` | How far the shelf's share of an independent sample of the sea may sit from SHELF_SHARE (spec-oil 4: half a point). |
| 120 | `WorldCheck.GRADE_WITHIN` | `.02` | How far each grade's share of the fields may sit from GRADE_SHARES (spec-oil 4: two points). |
| 620 | `WorldCheck.FIELD_COUNT_WITHIN` | `.01` | How far the world's count of fields over its new count may sit from FIELD_SCALE: 1% (each pool's fraction is drawn; the sparse resources' at-least-one adds a few, measured 0.3%). |
| 623 | `WorldCheck.CLUSTER_NN_OF_OLD` | `1.25` | How much farther than the old world's a field's nearest neighbour may lie on the mean, clustered: 1.25 times (est.: inside a cluster the fields lie at the old world's density, and a disc of ten los... |
| 662 | `WorldCheck.TYPE_WITHIN` | `.02` | How far each type's share of the fields may sit from Deposit.TYPE_SHARES (spec-materials 4: two points). |
| 665 | `WorldCheck.TYPE_IRON_CELLS` | `15` | The cells round the site whose iron pools are typed (section 9): 15 each way, 256 pools of two cells - some 25,000 fields on the default world. |
| 668 | `WorldCheck.TYPE_STONE_CELLS` | `45` | ...and stone's: 45 each way, 256 pools of six cells - some 10,000 fields; coal's and copper's are the whole world's, some 30,000 and 7,400. |
| 671 | `WorldCheck.TYPE_OLD_IRON_CELLS` | `10, TYPE_OLD_CELLS = 30` | The cells round the site whose old world's fields are typed cell by cell: 10 each way for iron, 30 for the rest (the old world's ten times the fields). |
| 674 | `WorldCheck.NEAR_SITE_KM` | `50` | How far round the founding site the fields of each type are counted for the notes, in km: 50, the spec's (spec-materials 2.4). |

### WorldDataCheck.java ([map](map/WorldDataCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 76 | `WorldDataCheck.EXPECT` | `"world/world-1-fixture.expect"` | The fixture's expected figures, beside the classes (the fixture itself is WorldData.FIXTURE). |
| 79 | `WorldDataCheck.FIXTURE_NAME` | `"world-1-fixture", FULL_NAME = "world-1"` | A fixture's meta name row, and the full file's. |
| 82 | `WorldDataCheck.SUM_TOL` | `1e-9` | A sum, a make or a use, agrees with the flows it is the sum of within this share of it (FORMAT.txt, trade: 1e-9). |
| 85 | `WorldDataCheck.TRIANGLE_SLACK` | `1e-9` | The triangle rule holds within this share of the dearer side (floating point adds the same route in a different order). |
| 88 | `WorldDataCheck.TABLE_TOL` | `1e-9` | The freight table is symmetric, and is its edges' closure, within this share of the figure. |
| 91 | `WorldDataCheck.MARGIN_TOL` | `1e-12` | The solve's margins meet when no make or use is off by more than this share of it (FORMAT.txt, THE KERNEL: 1e-12). |
| 94 | `WorldDataCheck.FLOW_TOL` | `1e-9` | The solve gives the file's flow within this share of it (FORMAT.txt: 1e-9), plus MARGIN_ULPS of its margin over the flow (see there). |
| 101 | `WorldDataCheck.MARGIN_ULPS` | `1e-15` | The room a flow is given beyond FLOW_TOL, as this share of the smaller of its row's make and its column's use, over the flow: 4 ulps of the margin. |
| 104 | `WorldDataCheck.COST_TOL` | `1e-9` | A pair's cost is elasticity x ln(1 + freight / unit value) within this share of it. |
| 107 | `WorldDataCheck.BACI_TOL` | `1e-6` | BACI's own sum for a pair is the file's flow within this share of it (the file's flow is BACI's, as the CSV is summed in a second pass). |
| 110 | `WorldDataCheck.SWEEP_CAP` | `100_000` | The most passes (every nation's a and b solved together, the others held) the solve may take for one good before it is called not to converge. |
| 113 | `WorldDataCheck.CHANGE_TOL` | `1e-9` | The solve may stop when no a or b moved by more than this share in a pass, and every margin is within MARGIN_TOL / TARGET_DIVISOR (econ_trade.py's rule). |
| 116 | `WorldDataCheck.TARGET_DIVISOR` | `100` | A margin must be within MARGIN_TOL over this before the solve may stop: the margin floor of double precision is far below MARGIN_TOL. |
| 119 | `WorldDataCheck.CHANGE_FLOOR` | `1e-15` | The solve stops at once when no a or b moved by more than this share in a pass: they have stopped moving, at the floor of double precision. |
| 122 | `WorldDataCheck.STALE_PASSES` | `8` | ...or when this many passes after it may stop have moved them no less than the least so far: a nation whose own pair is nearly all of its make and use keeps a rounding wobble. |
| 125 | `WorldDataCheck.TWELVE` | `List.of("AUS", "BEL", "CAN", "CHE", "CHN", "COL", "DEU", "FRA", "GBR", "JPN", "NLD", "U...` | The nations of the fixture, in iso3 order (FORMAT.txt, THE FIXTURE). |
| 128 | `WorldDataCheck.THE_THREE` | `List.of("TWN", "HKG", "MAC")` | The three the 193 UN members do not hold, that the full file's N = 196 adds: Taiwan, Hong Kong and Macau. |
| 131 | `WorldDataCheck.UN_MEMBER_COUNT` | `193` | How many member states the UN has (since South Sudan joined in 2011). |
| 134 | `WorldDataCheck.WORLD_BANK_REGIONS` | `7` | How many regions the World Bank's countries list sorts the nations in (FORMAT.txt, nations). |
| 137 | `WorldDataCheck.FULL_NATIONS` | `UN_MEMBER_COUNT + THE_THREE.size()` | How many nations the full file holds. |
| 144 | `WorldDataCheck.UN_MEMBERS` | `"AFG AGO ALB AND ARE ARG ARM ATG AUS AUT AZE " + "BDI BEL BEN BFA BGD BGR BHR BHS BIH B...` | The United Nations' 193 member states by ISO 3166 alpha-3 code, A to Z: the list on the UN's member states page (https://www.un.org/en/about-us/member-states), written here and not read from the fi... |
| 172 | `WorldDataCheck.LAST_MONTH` | `WorldData.monthNumber(2025, 12)` | The last month the rates and the figures run to: 2025-12. |
| 175 | `WorldDataCheck.PEOPLE_LAST` | `2100, FIGURES_LAST = 2025` | The people run to this year; the figures (output, the exchange rate) to FIGURES_LAST. |
| 178 | `WorldDataCheck.FIGURE_SERIES` | `List.of("gdp_2015usd", "gdp_usd", "gdp_ppp", "rate")` | The series that have a figure for every nation to FIGURES_LAST. |
| 181 | `WorldDataCheck.FREIGHT_DIALS` | `List.of("SEA_USD_TKM", "LAND_USD_TKM", "HANDLING_USD_T", "LAND_PER_SEA")` | The dials the freight cost and the kernel read (FORMAT.txt, trade and freight). |
| 184 | `WorldDataCheck.DAY` | `Pattern.compile("\\d{4}-\\d{2}-\\d{2}")` | A day as the meta writes it. |
| 188 | `WorldDataCheck.out` | `System.out` | Where the harness prints: the console. |

### WorldMarketCheck.java ([map](map/WorldMarketCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 79 | `WorldMarketCheck.out` | `System.out` | Where the harness prints: the console. |
| 85 | `WorldMarketCheck.BASE_SEED` | `11` | The seed the single-world sections use. |
| 88 | `WorldMarketCheck.SHARES` | `{ 0,.001,.01,.1,.5 }` | The city's yearly exports and imports in sections 3 and 4, as shares of the good's world use. |
| 91 | `WorldMarketCheck.STEPS` | `{.001,.01,.05,.10 }` | The volumes of section 5, shares of the world's use. |
| 94 | `WorldMarketCheck.STEP_TOL` | `.03` | The month's step is within this share of the price (the served buyers' landed price, tonne-weighted) of the re-solve's figure: a freight-heavy good's netback is a small remainder of its price, so a... |
| 97 | `WorldMarketCheck.SMALL_MOVE` | `.01, BIG_MOVE =.05` | An ordinary city (0.01% of the world's use) moves its netback by less than this share of the price it sells at, a big one (10%) by at least BIG_MOVE of it. |
| 100 | `WorldMarketCheck.ORDINARY_SHARE` | `.0001, LARGE_SHARE =.001, BIG_SHARE =.1` | The shares of a good's world use section 4 reads: an ordinary city's, a large mill town's (printed only: the host's market is small), and a big city's. |
| 103 | `WorldMarketCheck.ONE_PERCENT` | `{ Good.LUMBER, Good.CRUDE, Good.IRON, Good.GRAINS }` | The goods whose step is asserted within STEP_TOL at 1% of world use, beside the median: the study's and the brief's four. |
| 106 | `WorldMarketCheck.SAME` | `1e-9` | Two figures that should be one are the same within this share. |
| 109 | `WorldMarketCheck.STAND_IN_SEEDS` | `2_000` | Seeds drawn for the stand-in. |
| 112 | `WorldMarketCheck.SIGMAS` | `4.5` | A frequency is judged within this many standard deviations of a binomial. |
| 494 | `WorldMarketCheck.NAMED` | `{ Good.LUMBER, Good.IRON, Good.CRUDE, Good.GRAINS, Good.MACHINERY, Good.CARS }` | The goods the study and the brief name, always printed. |

### YearBookCheck.java ([map](map/YearBookCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |
| 935 | `YearBookCheck.COMMA_CO` | `"Acme, Inc."` | Two companies on the fixture's register, named the way a CSV has to quote. |
| 937 | `YearBookCheck.QUOTE_CO` | `"The \"Good\" Co"` | ...and the second, with a double quote in its name, which the CSV doubles. |

### ZoningCheck.java ([map](map/ZoningCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `ZoningCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 85 | `ZoningCheck.FARM_HANDS` | `100` | Spare unskilled hands section 8 hands the city past what its posts are short of, so the fields' planner gets past the staff to the zone (0.7.113): more than any farm's posts. |
| 88 | `ZoningCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders: homes, shops, a diner, builders, a bakery buying crops, power, water and roads. |

### ZoningViewCheck.java ([map](map/ZoningViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ZoningViewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 99 | `ZoningViewCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | ZoningCheck's orders: a small city with homes, shops, diners and works. |
| 105 | `ZoningViewCheck.CITY_KM2` | `80` | The ground the city is drawn to round the default world's founding site: ZoningCheck's land office's 80 km², forest and bare ground both. |

## tools (54 constants)

### HarnessMap.java ([map](map/HarnessMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 36 | `HarnessMap.HELPERS` | `Set.of("assertTrue", "check", "close", "report", "assertEquals", "same", "within", "sam...` |  |
| 38 | `HarnessMap.SECTION` | `Pattern.compile("^\\s*(?:\\\\n)?\\s*(?:---\|===)+\\s*(.*?)\\s*(?:---\|===)+\\s*(?:\\\\n)?...` |  |

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

### Names.java ([map](map/Names.md))

| line | constant | value | says |
|---:|---|---|---|
| 166 | `Names.HUNK` | `Pattern.compile("^@@ -\\d+(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@")` |  |
| 204 | `Names.GENERATED` | `List.of("docs/map/", "docs/dials.md", "docs/month-order.md", "docs/harnesses.md", "docs...` | The documents Maps and ManualToMarkdown write: what they name is the tree's own, or the manual's. |
| 228 | `Names.TICKS` | `Pattern.compile("'([^'\\n]+)'")` |  |
| 230 | `Names.WHOLE` | `Pattern.compile("([A-Za-z_][A-Za-z0-9_]*(?:\\.[A-Za-z_][A-Za-z0-9_]*)*)(\\([^()]*\\))?")` | A whole span that is one name: dotted identifiers, then perhaps one argument list. |
| 232 | `Names.GIVEN` | `Pattern.compile("([A-Za-z_][A-Za-z0-9_]*)\\s*=[^=].*")` | A span that gives a name a value is the text proposing it: `DRIFT_MONTHS = 6`. |
| 233 | `Names.FILE` | `Pattern.compile(".*\\.(java\|md\|json\|bin\|bat\|cmd\|sh\|txt\|html\|jar\|csv\|png\|xml\|properties\|...` |  |
| 234 | `Names.QCALL` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*(?:\\.[A-Z][A-Za-z0-9_]*)*)\\.([a-zA-Z_][A-Za-z0...` |  |
| 235 | `Names.CONST` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*(?:\\.[A-Z][A-Za-z0-9_]*)*)\\.([A-Z][A-Z0-9]*_[A...` |  |
| 236 | `Names.CALL` | `Pattern.compile("\\b([a-z_][A-Za-z0-9_]*)\\(\\)")` |  |
| 238 | `Names.NEW_BEFORE` | `Pattern.compile("(?i)\\b(?:new\|proposed\|for example)\\s+['*_\"(]*$")` | "new `CurveCheck`", "for example `HistoricalEconomy`": the text says the name is one it proposes. |
| 277 | `Names.LANGUAGE` | `Set.of("abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const...` | Words a backtick holds that are Java's, the codebase's conventions' or the shell's, not the tree's. |
| 286 | `Names.OBJECT` | `Set.of("equals", "hashCode", "toString", "getClass", "clone")` | What every object has, and every enum. |
| 287 | `Names.ENUM` | `Set.of("values", "valueOf", "ordinal", "name", "compareTo")` |  |

### ReliefRenders.java ([map](map/ReliefRenders.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `ReliefRenders.SEED` | `1` | The world's seed the plot-zoom renders are drawn on: any (the cover and relief noise are the world's); EarthTerrainCheck's. |
| 48 | `ReliefRenders.W` | `1000, H = 750` | The renders' size in pixels: 1,000 x 750, inside the 800 x 600 to 1,200 x 900 the review asked. |

### RunRenders.java ([map](map/RunRenders.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `RunRenders.PX` | `8, ACROSS = 4, DOWN = 3` | Pixels a plot, and the window in tiles: 8 x 32 x 4 by 8 x 32 x 3, 1,024 x 768. |

### SlopeRenders.java ([map](map/SlopeRenders.md))

| line | constant | value | says |
|---:|---|---|---|
| 65 | `SlopeRenders.PX` | `4, ACROSS = 8, DOWN = 6` | Pixels a plot is drawn at, and the window in tiles: 4 x 32 x 8 by 4 x 32 x 6, 1,024 x 768 - inside the review's 800 x 600 to 1,200 x 900. |
| 68 | `SlopeRenders.SPOTS` | `{ { 6.25, - 75.57 }, { 6.12, - 75.57 }, { - 13.2, - 72.5 }, { 27.60, 86.70 }, { 27.9881...` | Where the founding cards are measured: Medellin's spot (SlopeCheck's), a spot in the hills south of it that the snap moves 1.4 km, a spot in the Andes slice C probed, Everest's slopes (SlopeCheck's... |
| 69 | `SlopeRenders.SPOT_NAMES` | `{ "Medellin", "the hills south of Medellin, a spot the snap moves", "the Andes at -13.2...` |  |
| 198 | `SlopeRenders.CEILING` | `2_500` | The buildings after which a window is not the better for more. |

### SourceTree.java ([map](map/SourceTree.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `SourceTree.AREAS` | `{ "model", "sectors", "interface", "harnesses", "tools" }` |  |

### Stale.java ([map](map/Stale.md))

| line | constant | value | says |
|---:|---|---|---|
| 254 | `Stale.JAVA_REF` | `Pattern.compile("\\b([A-Z][A-Za-z0-9]*)\\.java\\b")` |  |
| 262 | `Stale.DOC_REF` | `Pattern.compile("\\b(docs(?:/[A-Za-z0-9_-]+)*/[a-z0-9][a-z0-9-]*\\.md)\\b")` | A document is only reported when it is written as a path under docs/ with a lower-case name. |
| 268 | `Stale.SHAPE_NAMES` | `Set.of("Name", "NAME", "Something", "Foo", "Bar", "Baz", "ClassName", "Class", "Screen"...` | Names that are written as a shape rather than as a file: "&lt;Name&gt;Screen.java", "*Check.java", "NAME.md". |
| 333 | `Stale.NUMBERS` | `numbers()` |  |
| 334 | `Stale.NUM` | `alternation()` |  |
| 337 | `Stale.SAID` | `"(?<![A-Za-z-])(" + NUM + ")"` | A number is only a number when a letter or a hyphen does not run into it: "fifty-six" is 56, never six. |
| 339 | `Stale.BANNERS` | `Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+(?:top-level\\s+\|class-level\\s+)?b...` |  |
| 340 | `Stale.SECTIONS` | `Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+sections?\\b")` |  |
| 341 | `Stale.HARNESSES` | `Pattern.compile("(?i)\\b" + SAID + "\\s+harness(?:es)?\\b")` |  |
| 342 | `Stale.UI_FILES` | `Pattern.compile("(?i)\\b" + SAID + "\\s+files\\b")` |  |
| 343 | `Stale.SECTORS` | `Pattern.compile("(?i)\\b" + SAID + "\\s+(?:private\\s+)?sectors?(?:\\s+classes)?\\b")` |  |
| 539 | `Stale.LIBRARY` | `Set.of("println", "runLater", "equals", "hashCode", "toString", "compareTo", "format", ...` | Names that appear in prose here and belong to the JDK or JavaFX, not to the tree. |
| 547 | `Stale.PLACEHOLDERS` | `Set.of("foo", "bar", "baz", "doSomething")` | Names written as an example of a member rather than as one: "see foo()" is the shape, not a claim. |
| 555 | `Stale.CALL` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\.([a-zA-Z][A-Za-z0-9_]*)\\(\\)\|\\b([a-z][A-Za...` | A member is named in prose as name() - the empty parentheses, with nothing between them and no space before them, are what makes it a member and not an English word with a parenthesis after it. |


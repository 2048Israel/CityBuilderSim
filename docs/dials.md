# The dials

Generated 2026-10-10 by `ham.citybuildersim.tools.Dials` - every `static final` constant in the tree, with the comment that explains it. Do not edit; regenerate with `Regenerate maps.bat`.

**2,566 constants in 304 files.**

## model (1390 constants)

### AgeBand.java ([map](map/AgeBand.md))

| line | constant | value | says |
|---:|---|---|---|
| 180 | `AgeBand.MAX_MONTHLY_MORTALITY` | `.05` | No band may lose more than this in a single month, whatever modifies it. |

### AutoBuilder.java ([map](map/AutoBuilder.md))

| line | constant | value | says |
|---:|---|---|---|
| 104 | `AutoBuilder.DEFAULT_SLACK` | `.15` | The spare margin a new city is given (Jerus, 2026-10-08: "maintain say 15% surplus service of everything"). |
| 113 | `AutoBuilder.DEFAULT_DEBT_LIMIT` | `.60` | The debt limit a new city is given, the city's debt over a year of its GDP (0.7.81, star N6-2): the Maastricht Treaty's reference value for government debt, 60% of GDP (Treaty on the Functioning of... |
| 116 | `AutoBuilder.SLACK_MOST` | `.50` | The spare margin's slider runs from none - just enough - to this: half again what the city uses of every service (star N4-3). |
| 124 | `AutoBuilder.DEBT_LIMIT_MOST` | `3.0` | The debt limit's slider runs from none - with any debt it builds nothing - to three years of GDP (star N6-2): past the most any large government has owed in peace or war, about two and a half years... |
| 127 | `AutoBuilder.STEP` | `.01` | The spare margin's step: a whole per cent (the debt limit's until 0.7.81). |
| 130 | `AutoBuilder.DEBT_STEP` | `.05` | The debt limit's step (0.7.81, star N6-4): five per cent of a year's GDP - sixty steps to DEBT_LIMIT_MOST, where whole per cents were three hundred on a slider a third of the Build page wide; DEFAU... |
| 133 | `AutoBuilder.LOG_MOST` | `40` | The most of its orders the log keeps, newest kept (BuildLog's cap). |
| 337 | `AutoBuilder.FIRST_SHARE` | `CityNeeds.FIRST_SCHOOL_SHARE` | A first building's share of need before it is built: the firms' first-plant share, as NEEDS YOU's first school (CityNeeds.FIRST_SCHOOL_SHARE). |
| 479 | `AutoBuilder.REVENUE_MONTHS` | `12` | Months of monthRevenue() the year's figure averages: a year, as a lender reads a city's accounts (star N4-2). |
| 908 | `AutoBuilder.Ground.NONE` | `new Ground(List.of(), 0, true)` | No ground to buy: the order fits what is free. |

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
| 3072 | `Bank.ACCOUNT_FEE` | `.012` | A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). |
| 3075 | `Bank.LOAN_FEE` | `.01` | The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. |
| 3464 | `Bank.DEPOSIT_SHARE_FLUSH` | `.35` | The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - s... |
| 3467 | `Bank.DEPOSIT_SHARE_AT_WINDOW` | `.90` | ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of... |
| 3470 | `Bank.DEPOSIT_RATE_SPEED` | `1.0 / 6` | How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. |
| 4025 | `Bank.SECTOR_WATCH_LEVERAGE` | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not... |
| 4028 | `Bank.HOUSEHOLD_WATCH_MONTHS` | `HouseholdBalance.CREDIT_LIMIT_MONTHS / 2` | Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. |
| 4031 | `Bank.HOUSEHOLD_BOOK` | `"Households"` | The key the families' book is saved and shown under, beside the sectors' names. |
| 4327 | `Bank.CONSERVATION_BUFFER` | `.025` | The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). |
| 4330 | `Bank.MANAGEMENT_CUSHION` | `.025` | How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). |
| 4333 | `Bank.YEAR_MONTHS` | `12` | A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. |
| 4424 | `Bank.MAX_BUFFER` | `.085` | The most buffer a bank holds over the minimum, however bad a year it has seen: 8.5 points, the whole Basel III stack - the 2.5-point conservation buffer, a countercyclical buffer at its 2.5-point c... |
| 4518 | `Bank.PAYOUT_IN_BAND` | `.45` | The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. |
| 4521 | `Bank.EXCESS_PAYOUT_MONTHS` | `YEAR_MONTHS` | How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. |
| 4724 | `Bank.OWN_ISSUE_DEAD_BAND` | `1e-9` | How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into... |
| 4805 | `Bank.RATIONED_GROWTH` | `.01` | A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit... |
| 5179 | `Bank.CUSTOMERS_PER_BRANCH` | `16_000` | The customers one branch serves: 16,000, TD's clients per branch - "approximately 16 million clients in Canadian Personal and Business banking" through "more than 1,000 branches" (TD, corporate inf... |
| 6002 | `Bank.SHEET_ASSETS` | `{ Sheet.RESERVES, Sheet.BUSINESS_LOANS, Sheet.INTERIM, Sheet.MORTGAGES, Sheet.FAMILIES,...` | The asset lines, in the page's order: they sum to totalAssets(). |
| 6006 | `Bank.SHEET_LIABILITIES` | `{ Sheet.DEPOSIT_FUNDING, Sheet.WINDOW, Sheet.FOREIGN_DEPOSITS, Sheet.DESK_SHORT, Sheet....` | ...and the liability lines: they sum to totalLiabilities(). |

### BoatSchedule.java ([map](map/BoatSchedule.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BoatSchedule.MONTH_SECONDS` | `60` | Seconds of boats a game month: 60 (the research's Q8: about one boat every 4 s at district zoom in the big city). |
| 60 | `BoatSchedule.LEG_SECONDS` | `4` | Seconds a boat takes to run its lane, in or out: 4 (spec-oil 2.10, the UI's). |
| 63 | `BoatSchedule.BERTH_SECONDS` | `24 / 730.5 * MONTH_SECONDS` | Seconds a boat lies at the quay: about 24 hours of a 730.5-hour month [P33], about 2 s. |
| 66 | `BoatSchedule.LANE_PLOTS` | `1536` | A lane's length, quay to the map's sea, in plots: 1,536 (six districts, spec-oil 2.10). |
| 69 | `BoatSchedule.SALT` | `0xB0A7_5417L` | The salt the calls' hashes start from. |
| 72 | `BoatSchedule.GROUP_BITS` | `7, ARRIVAL_BITS = 36, ROUTE_SHIFT = GROUP_BITS + ARRIVAL_BITS` | A call's key: its route + 1 (0 for none) in the top ROUTE_BITS, its arrival in ARRIVAL_BITS ticks of a month, its group in the low GROUP_BITS. |
| 73 | `BoatSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ARRIVAL_MASK =(1L<<ARRIVAL_BITS) - 1` |  |
| 74 | `BoatSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` |  |

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
| 65 | `BuildAdvice.OVERVIEW` | `"Overview"` | The page Build opens on: the city's job, what would help most, and what the market builds. |
| 80 | `BuildAdvice.UTILITIES` | `"Utilities", ROADS = "Roads & transit", HEALTHCARE = "Healthcare", EDUCATION = "Educati...` | The fourteen categories' names, as the strip, the Overview and NEEDS YOU's doors say them. |
| 897 | `BuildAdvice.MAX_SUGGESTIONS` | `3` | At most this many suggestions, one per need. |
| 900 | `BuildAdvice.MOST` | `1<<22` | The most of one building a search will count to. |
| 903 | `BuildAdvice.SLACK` | `BusinessInvestment.TARGET_HEADROOM` | The slack an order is sized with past its projection: the businesses' own headroom (BusinessInvestment.TARGET_HEADROOM). |
| 906 | `BuildAdvice.HORIZON` | `BusinessInvestment.PLANNING_HORIZON` | Months past an order's opening it is sized for: the businesses' (BusinessInvestment.PLANNING_HORIZON); the opening's wait is held to their BusinessInvestment.MAX_ORDER_MONTHS. |
| 919 | `BuildAdvice.NOW` | `new Ahead(0, 1, 0)` | Today's demand, no slack: what NEEDS YOU reads. |
| 1317 | `BuildAdvice.LIFE_MONTHS` | `Game.BUILD_BOND_YEARS * 12` | Months a road is weighed over (0.7.70): the funding page's bond term, Game.BUILD_BOND_YEARS - a long-lived asset "paid for over the years the city uses it". |

### BuildCard.java ([map](map/BuildCard.md))

| line | constant | value | says |
|---:|---|---|---|
| 141 | `BuildCard.KILOWATTS` | `" kW"` | The words after a power plant's figure (0.7.28): kilowatts, the model's unit and a rate - "units a month" until then, which was neither. |
| 220 | `BuildCard.GROUP_ORDER` | `{ "Homes", "Groceries", "Filling stations", "The bank's branches", "Food mills", "Food ...` | The market groups, in the order a page lays them out. |
| 410 | `BuildCard.ROAD_PER` | `"trip over " + BuildAdvice.LIFE_MONTHS / 12 + " years, with its land"` | What a road card's first bar is per (0.7.70): a trip it takes off the road, over its life, its ground in it - BuildAdvice.lifetime() over BuildAdvice.unit(); the bar's (i) says the rest. |
| 1047 | `BuildCard.LAND_WORDS` | `{ "no land", "short of homes" }` | The phrases each kind is read by, in lower case; a word starting "Could not build" is LAND as well, and one starting "Declined" is MONEY unless CREDIT took it. |
| 1049 | `BuildCard.STAFF_WORDS` | `{ "could staff" }` | ...STAFF's. |
| 1051 | `BuildCard.LICENCE_WORDS` | `{ "licence", "licences" }` | ...LICENCE's: whole words, so the plural as well. |
| 1053 | `BuildCard.ORE_WORDS` | `{ "deposit", "ore" }` | ...ORE's. |
| 1055 | `BuildCard.SUPPLY_WORDS` | `{ "fabricates", "will not sell" }` | ...SUPPLY's. |
| 1057 | `BuildCard.CREDIT_WORDS` | `{ "borrowing ban", "bank", "default point", "down payment", "lender", "mortgage" }` | ...CREDIT's. |
| 1059 | `BuildCard.MONEY_WORDS` | `{ "below cost", "no margin", "worth sinking", "worth building", "cost more than", "noth...` | ...MONEY's. |
| 1063 | `BuildCard.ENOUGH_WORDS` | `{ "ahead of", "already", "covers all", "months of work queued", "months of work on site...` | ...ENOUGH's. |

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
| 3753 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 4985 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 5008 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 6055 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BuildingVisual.java ([map](map/BuildingVisual.md))

| line | constant | value | says |
|---:|---|---|---|
| 50 | `BuildingVisual.HOME` | `0` | Homes: houses, and the flats drawn darker. |
| 52 | `BuildingVisual.SHOP` | `1` | Shops: convenience and grocery stores, the bank's branches, boutiques, diners. |
| 54 | `BuildingVisual.OFFICE` | `2` | Offices: business services. |
| 56 | `BuildingVisual.INDUSTRY` | `3` | Industry: bakeries to steel, construction, the railway and the car plants. |
| 58 | `BuildingVisual.FARM` | `4` | Farms: on open grass only. |
| 60 | `BuildingVisual.UTILITY` | `5` | Utilities: power, water and the transit depots. |
| 62 | `BuildingVisual.SCHOOL` | `6` | Schools, colleges and universities. |
| 64 | `BuildingVisual.HEALTH` | `7` | Health: clinics, hospitals, care and the cemeteries. |
| 66 | `BuildingVisual.SAFETY` | `8` | Safety: police and the jails. |
| 68 | `BuildingVisual.MINE` | `9` | Mines and wells: on their resource's sites. |
| 71 | `BuildingVisual.CLASSES` | `10` | How many classes: ten - what the pyramid sums a node's buildings by (spec-land 2.5). |
| 74 | `BuildingVisual.CLASS_NAMES` | `{ "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities", "Schools", "Health", "...` | The classes' names, as the legend writes them (the mockup's). |
| 78 | `BuildingVisual.FILL` | `{ 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e, 0xffd0587a, 0...` | Each class's fill, 0xAARRGGBB - the mockup's TYPES. |
| 82 | `BuildingVisual.EDGE` | `{ 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d, 0xff86304a, 0...` | ...and its edge, drawn from 6 px a plot (the mockup's). |
| 86 | `BuildingVisual.FLATS_FILL` | `0xffd07a44` | Flats' fill: the mockup's FLATS, a darker orange than a house. |
| 89 | `BuildingVisual.FLATS_EDGE` | `0xff7e4119` | ...and their edge. |
| 92 | `BuildingVisual.CAMPUS_FILL` | `0xff4a3f5c` | The refinery's units (0.7.97, campus()): mockup 3's refinery, #4a3f5c, a darker violet than industry's, so the campus reads as one... |
| 95 | `BuildingVisual.CAMPUS_EDGE` | `0xff7a6a90` | ...edged in its #7a6a90. |
| 98 | `BuildingVisual.TERMINAL_FILL` | `0xff2e3640` | A sea terminal's apron (0.7.97, berth()): mockup 3's quays' grey, #2e3640... |
| 101 | `BuildingVisual.TERMINAL_EDGE` | `0xff4a5866` | ...edged in its #4a5866. |
| 104 | `BuildingVisual.TANKS_FILL` | `0xff2a3540` | A tank farm's tanks (0.7.97, the refiners' Tank Farm and the city's Strategic Reserve): mockup 3's tanks, #2a3540... |
| 107 | `BuildingVisual.TANKS_EDGE` | `0xff7f8ca6` | ...ringed in its #7f8ca6. |
| 112 | `BuildingVisual.NOT_A_ROAD` | `0` | Not a road. |
| 114 | `BuildingVisual.GRAVEL` | `1` | A gravel road: no freight grade. |
| 116 | `BuildingVisual.PAVED` | `2` | A paved road: a freight grade under one. |
| 118 | `BuildingVisual.HIGHWAY` | `3` | An elevated highway: a freight grade of one. |
| 121 | `BuildingVisual.DRAWN_AS_HOMES` | `{ 22 }` | The permanent ids of the care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Care Service (22) - still drawn, one for one, on its own land (0.7.64). |
| 124 | `BuildingVisual.RAIL_TERMINALS` | `{ 64 }` | The permanent ids of the rail types drawn as a yard beside the track, not as track (0.7.72): the Rail Terminal (64). |
| 127 | `BuildingVisual.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. |
| 301 | `BuildingVisual.ORDERS` | `java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>())` |  |

### BuildingsTemplate.java ([map](map/BuildingsTemplate.md))

| line | constant | value | says |
|---:|---|---|---|
| 706 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 709 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 1105 | `BusinessDebtManager.DEBT_KINDS` | `{ "Bank loans", "Bonds", "Mortgages", "Interim financing" }` | The four kinds of debt, in the order the statements print them. |
| 1123 | `BusinessDebtManager.DUE_HORIZONS` | `{ 12, 60 }` | The two horizons R2 reads, in settles: a year and five. |
| 1126 | `BusinessDebtManager.OWED` | `0, WITHIN_YEAR = 1, WITHIN_FIVE = 2, RATE = 3, RUNS_TO = 4` | debtByKind()'s rows: what is owed, what falls due within a year and within five, the rate it pays a year weighted by what is owed, and the month the last of it falls due (0 with none) - the last tw... |
| 1197 | `BusinessDebtManager.BORROWED` | `0, REPAID = 1, WRITTEN_OFF = 2` | debtMovedByKind()'s rows: borrowed (the principal written, a bond's face), repaid, written off. |
| 2017 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 2019 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 2021 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 2023 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 2025 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 2027 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 2520 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 3108 | `BusinessDebtManager.INTERIM_PAST_LINE` | `"past the default point after the write-down"` | Why the interim lender would not lend: the sector still past the default point after the write-down. |
| 3110 | `BusinessDebtManager.INTERIM_BANK_SHUT` | `"the bank is shut"` | ...or the bank that would lend it has failed or is frozen in resolution. |
| 3531 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BusinessInvestment.java ([map](map/BusinessInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 52 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 55 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 58 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 61 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site - every sector but the landlords, who hold work by the month (0.7.17; withinMonthsOfWork()). |
| 108 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, in months of the builders' work. |
| 111 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 402 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 405 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 408 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 419 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |
| 1044 | `BusinessInvestment.REAL_HURDLE_FLOOR` | `.25` | The least of the rate a project is tested against, however much inflation its owners expect: a quarter of it. |

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
| 61 | `CityLand.SIDES` | `LegacyLand.SIDES` | Sides of the city, seen from the founding site: north, east, south and west, in that order - LegacyLand.SIDES (spec-land star 1). |
| 64 | `CityLand.SIDE_NAMES` | `LegacyLand.SIDE_NAMES` | The sides' names, in their order: LegacyLand's. |
| 67 | `CityLand.TOTAL` | `0` | Where a record keeps its whole area, in square kilometres. |
| 70 | `CityLand.DRY` | `1` | ...its dry ground: what buildings stand on, and what the city's square feet count. |
| 73 | `CityLand.FRESH` | `2` | ...its fresh water: lakes and the founding river. |
| 76 | `CityLand.SEA` | `3` | ...its sea. |
| 79 | `CityLand.FOREST` | `4` | ...and its forest, which is dry ground too. |
| 82 | `CityLand.AREAS` | `5` | How many areas a record keeps: total, dry, fresh, sea and forest. |
| 85 | `CityLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 88 | `CityLand.CENTRE` | `0` | The centre's holding on the grid: 0; the k-th purchase is holding k. |
| 355 | `CityLand.NEW_FIELDS_FORMAT` | `36` | The first save format whose land says which holdings hold the old world's fields (oldWorldHoldings()): 36. |
| 564 | `CityLand.CELLS_KEPT` | `256` | How many cells' fields are kept: 256 - the nine round a site for every resource, and the cells a large holding reaches. |
| 566 | `CityLand.CELLS` | `new LinkedHashMap<>(64, 0.75f, true) { @ Override protected boolean removeEldestEntry(M...` |  |
| 631 | `CityLand.TILE_COUNTS_KEPT` | `65_536` | Tiles' counts kept, by world and tile: 65,536 (a few megabytes) - the whole tiles an offer's count reads, which the next city on the same world's ground, or the same city drawn again, reads again. |
| 634 | `CityLand.PARALLEL_TILES` | `2_048` | A rectangle of this many tiles or more is counted over the machine's cores: 2,048 (about an eighth of a second's reading on one core). |
| 638 | `CityLand.TILE_COUNTS` | `new LinkedHashMap<>(1024, 0.75f, true) { @ Override protected boolean removeEldestEntry...` |  |
| 747 | `CityLand.CENTRE_FIELDS` | `AREAS + 2 * KINDS + 3 + 2` | How wide the centre's record is: 24 (spec-grid 3, M3; 25 with a half-side before, format 31). |
| 750 | `CityLand.RECT_FIELDS` | `4` | How wide a centre rectangle's record is: x0, y0, x1, y1 - its plots, half-open. |

### CityMap.java ([map](map/CityMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 113 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 116 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 119 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 122 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 125 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 128 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5). |
| 131 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 134 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 137 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 140 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 143 | `CityMap.FORMAT` | `6` | The sidecar's format: 6 since 0.7.97, the city's works on its shore after the runs (CityShore: each terminal's and tank farm's box, never moved) - a FORMAT 5 sidecar has them laid from its counts a... |
| 146 | `CityMap.OLDEST_READ` | `4` | The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. |
| 149 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 455 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1369 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.dy).thenCom...` | Nearest the founding site first, then north to south, west to east. |
| 1596 | `CityMap.PLANS_KEPT` | `64` | How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan wa... |
| 1599 | `CityMap.PACKED_BIT` | `1<<20` | A packed box's flag (Drawn.boxes): drawn without a street, R7's packing at the city's edge. |
| 1602 | `CityMap.BOX_TYPE_SHIFT` | `21` | Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). |
| 1786 | `CityMap.M_WEST` | `0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4` | A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, ... |
| 1807 | `CityMap.AROUND` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 }, { - 1, - 1 }, { 1, - 1 }, { 1, 1 }, { - 1...` | The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). |
| 1810 | `CityMap.CORNER_DIRS` | `{ { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } }` | Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. |
| 1937 | `CityMap.CHAIN_BAND` | `128` | THE CHAIN'S BANDS (0.7.89, batch RD3). |
| 1940 | `CityMap.BANDS_KEPT` | `16` | The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. |
| 2573 | `CityMap.INTERIOR_MOST` | `World.TILE - 1` | A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. |
| 2643 | `CityMap.RUN_LINES_KEPT` | `4096` | Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. |
| 2646 | `CityMap.RUN_TILES_KEPT` | `256` | ...whole tiles, for a 45-degree way: 256. |
| 3097 | `CityMap.AtSea.NONE` | `new AtSea(List.of(), List.of())` | None. |
| 3660 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned dry plots. |
| 3663 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 3666 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned dry plots. |

### CityNeeds.java ([map](map/CityNeeds.md))

| line | constant | value | says |
|---:|---|---|---|
| 146 | `CityNeeds.PLAIN` | `new Words() { @ Override public String people(double count) { return Formats.INSTANCE.c...` | The same four shapes without the toolkit (Formats), for a harness and a log. |
| 168 | `CityNeeds.NETWORK_YELLOW` | `.75, NETWORK_RED = 1` | A network is listed from three-quarters of its capacity, red once it is over. |
| 171 | `CityNeeds.GENERAL_YELLOW` | `.80, GENERAL_RED =.50` | General care is watched hardest: it moves the sick rate. |
| 174 | `CityNeeds.OTHER_CARE_YELLOW` | `.70, OTHER_CARE_RED =.40` | Childcare and senior care kill at the ends of life; a young city legitimately has neither for a while. |
| 177 | `CityNeeds.PLOTS_WATCHED` | `120` | Burial plots are watched once fewer than this many months are left. |
| 179 | `CityNeeds.PLOTS_YELLOW` | `24, PLOTS_RED = 6` | ...listed under two years of plots, red under six months. |
| 182 | `CityNeeds.SCHOOLS_YELLOW` | `.90, SCHOOLS_RED =.60` | The basic ladder's bottleneck, its coverage (read as served since 0.7.41; "taught" until then). |
| 185 | `CityNeeds.SEATS_YELLOW` | `1.05, SEATS_RED = 2` | A school above the ladder: who would come and be hired (wanted(), 0.7.51; who would come until then) over its seats. |
| 188 | `CityNeeds.SEATS_FLOOR` | `25` | A class's worth: fewer students than this the city would get and hire (would-be students until 0.7.51) and a school is not a row (measured: 3 would-be law students in a city of 1,650). |
| 191 | `CityNeeds.CRIME_YELLOW` | `1.2, CRIME_RED = 1.5` | Crime against Canada's rate. |
| 194 | `CityNeeds.CELLS_YELLOW` | `1, CELLS_RED = 25` | The caught, not held. |
| 202 | `CityNeeds.SICK_YELLOW` | `.06, SICK_RED =.12` | The sick rate, the share of the workforce off sick (0.7.28): the left panel's OFF SICK lines (SummaryScreen's literals until then), here so the Services screen colours the same figure by the same l... |
| 211 | `CityNeeds.FALLS_DUE_MONTHS` | `3` | FALLS DUE (0.7.24): paper due within this many months is red, as the bottom strip drew its maturity chip until it left the frame (its maturity chip, FinancesScreen's until 0.7.32 and its urgency() ... |
| 219 | `CityNeeds.FALLS_DUE_SOON_MONTHS` | `12` | ...and paper due within this many months is amber: the strip's other rule (gap <= 12), named in 0.7.32 so the Finances tab's NEXT DUE, its book and its strip colour a maturity by one line. |
| 229 | `CityNeeds.SERVICE_FELT` | `.12, SERVICE_CONSTRAINED =.25` | DEBT SERVICE, A SHARE OF THE TAKE (0.7.32): what the city pays its lenders against what it collects is comfortable under SERVICE_FELT, felt from there, and constrained past SERVICE_CONSTRAINED - th... |
| 239 | `CityNeeds.YEAR_WALL` | `.5` | A WALL ON THE LADDER (0.7.32): a calendar year whose payments - coupons and principal - pass this share of a year of revenue is one a city meets by refinancing before it arrives. |
| 273 | `CityNeeds.SERVED` | `"served", SHORT = "short", TIGHT = "tight", ENOUGH = "enough"` | The gauges' words (0.7.41), the same on every screen: what the figure is, and its verdict's three words. |
| 569 | `CityNeeds.GROUND_YELLOW` | `LandManager.BLOCK_SQ_FT` | Free ground under which NEEDS YOU lists the GROUND row: a block, 100,000 sq ft. |
| 572 | `CityNeeds.TRUST_RED` | `.5` | Trust in the central bank under which a fall is red in the PRICES row: half - under it, what people expect is more recent prices than the bank's target. |
| 858 | `CityNeeds.FIRST_SCHOOL_SHARE` | `.5` | A first school above the ladder is listed once the students it would get and hire fill this share of the smallest that teaches it - the firms' first-plant share (Materials.FIRST_PLANT_UTILISATION, ... |

### CityRuns.java ([map](map/CityRuns.md))

| line | constant | value | says |
|---:|---|---|---|
| 80 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 83 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 86 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 89 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 92 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 95 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 98 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 101 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 104 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 107 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 110 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7). |
| 113 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 116 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 119 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 122 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 125 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 127 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 129 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 131 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 134 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 136 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 137 | `CityRuns.M_DIAG_SHIFT` | `2` |  |
| 139 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 357 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 440 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |

### CityShore.java ([map](map/CityShore.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `CityShore.QUAY_PLOTS` | `{ 10, 10, 14, 6 }` | A terminal's quay out over the sea, in plots, by Ports.Cargo's ordinal: the research's quay a berth (4.1) at its middle, in whole plots of 30 m - a tanker berth's 270 to 345 m [P1] 10, a bulk berth... |
| 60 | `CityShore.CLEAR` | `1` | Plots kept clear between two works, and between a work and a highway (H5's verge): one. |
| 63 | `CityShore.SEARCH_TILES_MOST` | `4096` | The most tiles one search looks at before it counts the work short: 4,096 - 64 districts' worth, about 0.3 s, so a city of billions with no free shore never pays more. |
| 66 | `CityShore.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west: the sea's side of a work, its quay's heading. |

### ConstructionControl.java ([map](map/ConstructionControl.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `ConstructionControl.STANDARD_HOURS` | `40` | The normal working week the report measures against, in hours. |
| 115 | `ConstructionControl.OVERTIME_HOURS` | `50` | The week on overtime: the Business Roundtable's five tens (Report C-2, Figure 4). |
| 118 | `ConstructionControl.OVERTIME_RATE` | `1.5` | What an hour over the standard week is paid at: time and a half (Canada Labour Code, section 174). |
| 121 | `ConstructionControl.WEEKS_A_MONTH` | `52.0 / 12.0` | Weeks in a month for averaging the report's table: 52 / 12, the 4.33 the brief reads it at. |
| 124 | `ConstructionControl.OVERTIME_WEEKS_ENDING` | `{ 2, 4, 6, 8, 10 }` | Where each step of the report's 50-hour curve ends, in weeks on the schedule (Report C-2, Figure 4); the last step runs on. |
| 127 | `ConstructionControl.OVERTIME_PRODUCTIVITY` | `{ 0.926, 0.90, 0.87, 0.80, 0.752, 0.750 }` | Productivity on a 50-hour week against a 40-hour one, for each step above and beyond the last (Report C-2, Figure 4). |
| 130 | `ConstructionControl.OVERTIME_WAGE_BILL` | `(STANDARD_HOURS +(OVERTIME_HOURS - STANDARD_HOURS) * OVERTIME_RATE) / STANDARD_HOURS` | The crews' wage bill on overtime over their normal bill: (40 + 10 x 1.5) / 40 = 1.375. |
| 235 | `ConstructionControl.DEMOLITION_SHARE` | `0.05` | The share of a building's construction points its demolition is: Detroit's average demolition of July 2015, $14,855 (SIGTARP, 26 April 2017), over the average new single-family home of 2015, $289,4... |
| 334 | `ConstructionControl.PAVE_FROM` | `"Gravel Road"` | The road that can be paved (0.7.70): a Gravel Road, and nothing else. |
| 337 | `ConstructionControl.PAVE_TO` | `"Paved Road"` | ...and what it is paved to: a Paved Road. |

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
| 141 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way, at the default 2% target: a one-point real rate (ruleRate() adds the target's distance from the default since 0.7.42 - its aim's, at a s... |
| 144 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 147 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 150 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 196 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target, at Standard - since 0.7.52 the dial beside the target sets another, never as little as one (HOW STRICT, below). |
| 247 | `DebtManager.STRICTEST_AIM` | `Expectations.TOLERANCE` | How far under the target the very strict bank aims, a fraction a year: Expectations.TOLERANCE, the furthest under it inflation can sit and still count as on target - so hitting the aim costs no trust. |
| 250 | `DebtManager.LOOSEST_BAND` | `2 * Expectations.TOLERANCE` | The band over the target the very loose bank lets be, a fraction a year (either side of it until 0.7.81; under it, it cuts as Standard does): twice Expectations.TOLERANCE, so its top is a miss trus... |
| 253 | `DebtManager.STRICTEST_WEIGHT` | `2.0` | The very strict bank's weight on the gap from its aim: TAYLOR_WEIGHT's margin over one, doubled. |
| 256 | `DebtManager.LOOSEST_WEIGHT` | `1.25` | The very loose bank's weight on the gap past its band: TAYLOR_WEIGHT's margin over one, halved - still over one (the Taylor principle). |
| 522 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 557 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 560 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 563 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 700 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 703 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 706 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 713 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 716 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 719 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 722 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 725 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 728 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1255 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1258 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 1513 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1516 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1519 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1522 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1525 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1528 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TERM_PREMIUM_...` | The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. |
| 2013 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 80 | `DecisionLog.RESERVE` | `"reserve"` | The city's strategic reserve (0.7.85): a fill ordered, a release set or stopped. |

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
| 76 | `Deposit.SITE_PLACES` | `places()` | The grid points a field's sites stand on, nearest the centre first, in site widths east and south: every point within PLACES_BOX of the centre each way sorted by its squared distance, then from nor... |
| 79 | `Deposit.PLACES_BOX` | `(int) Math.ceil(Math.sqrt(World.MAX_SITES / Math.PI)) + 1` | The box the site table is sorted in, in site widths each way: sqrt(World.MAX_SITES / pi) rounded up, and one more - 14 for the old world's 512 sites, 42 for 5,120. |
| 82 | `Deposit.PLACES_REACH` | `reaches()` | How far, in site widths each way (L-infinity), the first k + 1 sites reach from the centre. |
| 173 | `Deposit.GRADE_SHARES` | `{ 1.0 / 3, 1.0 / 3, 1.0 / 3 }` | The share of fields of each grade, in Grade's order: a third each (est., spec-oil 2.2 and 6 - to confirm; the research gives a grade a field, not the world's mix). |
| 176 | `Deposit.GRADE_SALT` | `0x6A7DE5L` | What makes a field's grade a draw of its own, apart from its turn()'s. |

### DistrictPlan.java ([map](map/DistrictPlan.md))

| line | constant | value | says |
|---:|---|---|---|
| 174 | `DistrictPlan.SIDE` | `CityMap.DISTRICT` | Plots on a district's side: CityMap.DISTRICT, 256. |
| 177 | `DistrictPlan.FRAME` | `SIDE + 1` | Plots on the plan's side: the district and the first column (row) of its east (south) neighbour, where its edge cells' last arterial runs - 257. |
| 180 | `DistrictPlan.AREA` | `FRAME * FRAME` | Plots in the frame. |
| 183 | `DistrictPlan.CELL` | `World.TILE` | A cell's side, arterial to arterial: a tile, World.TILE, 32 plots (960 m) - its west column and north row its arterials (spec 2.3). |
| 186 | `DistrictPlan.INTERIOR` | `CELL - 1` | A cell's interior: 31 plots a side, between its arterials. |
| 189 | `DistrictPlan.CELLS_A_SIDE` | `SIDE / CELL` | Cells on a district's side: 8. |
| 192 | `DistrictPlan.CELLS` | `CELLS_A_SIDE * CELLS_A_SIDE` | Cells in a district: 64. |
| 195 | `DistrictPlan.LATTICE` | `TilePainter.JUNCTION_APART` | The street lattice: lines every TilePainter.JUNCTION_APART (8) plots, the + junction floor - a cell's west arterial on one, so the lattice runs on from cell to cell. |
| 198 | `DistrictPlan.REACH` | `TilePainter.REACH` | A building is within reach of a street when one lies within this many plots of it, across corners: TilePainter.REACH, 4. |
| 201 | `DistrictPlan.STREETS_AT` | `{ LATTICE - 1, 2 * LATTICE - 1, 3 * LATTICE - 1 }` | Where a cell's streets run, in plots into its interior: 7, 15 and 23 - the lattice's lines inside it (spec 2.3's long blocks of 15 x 7, square blocks of 7 x 7). |
| 204 | `DistrictPlan.MIDDLE` | `2 * LATTICE - 1` | ...and a homes cell's cross street and an estate cell's spine: the middle line, 15 - long blocks 15 plots long either side of it. |
| 207 | `DistrictPlan.LINE_FIRST` | `LATTICE - 1` | An estate cell's streets lie on interior lines from this one (0.7.90, ESTATE LINES): 7, LATTICE - 1 - so where one meets a neighbour's street across an arterial, the + it makes is LATTICE or more f... |
| 210 | `DistrictPlan.LINE_LAST` | `INTERIOR - LATTICE` | ...to this one: 23, INTERIOR - LATTICE, the same from the far corner. |
| 213 | `DistrictPlan.STRIP_LEAST` | `LINE_FIRST` | A strip's least depth between an estate cell's streets: 7, LINE_FIRST - so its streets are LATTICE or more apart, and so are the + junctions two of them could make on one arterial. |
| 216 | `DistrictPlan.STREET_BRIDGE` | `TilePainter.STREET_BRIDGE` | A street's longest crossing of fresh water: TilePainter.STREET_BRIDGE, 6 plots (spec 2.4, today's street bridge). |
| 219 | `DistrictPlan.ARTERIAL_BRIDGE` | `TilePainter.MAX_BRIDGE [ BuildingVisual.HIGHWAY ]` | An arterial's: a highway's, TilePainter.MAX_BRIDGE[HIGHWAY], 14 plots (spec 2.4) - wider water is a landmass's edge (spec 3). |
| 222 | `DistrictPlan.CELL_ROOM_LEAST` | `120` | A cell is opened only with at least this many plots of dry owned ground off the highways in its interior: 120 of 961, the prototype's. |
| 225 | `DistrictPlan.NUDGE` | `0.6` | The hashed nudge on a cell's distance from the hub, in cells: up to 0.6, the prototype's - the open cells' edge ragged, not a disc. |
| 228 | `DistrictPlan.MIXED_SLOT` | `640` | The first band's buildings are dealt round one slot for every this many plots of their footprints: 640, the prototype's - about a homes cell's room after its streets, so the slots are about its cel... |
| 231 | `DistrictPlan.SQUARE_PLOTS` | `56` | The surface the ladder's square blocks take a homes cell: 56 plots, the prototype's - a square cell's streets (3 rows and 3 columns, 177 plots) less a long-block cell's (121). |
| 234 | `DistrictPlan.BOULEVARD_PLOTS` | `100` | ...and a boulevard a cell: 100 plots, the prototype's - its arterials' second row, less what neighbouring boulevards share. |
| 237 | `DistrictPlan.MERGE_GROUPS` | `{ { 15, 15 }, { 15, 23 }, { 15, 31 }, { 31, 15 }, { 31, 31 } }` | The groups of long blocks a homes cell merges for a building wider than a block, {across the blocks, along them} in plots: two blocks, three, four, two across, the whole cell (the prototype's; spec... |
| 240 | `DistrictPlan.MERGE_DRY` | `0.95` | A group of blocks is merged only when more than this share of it is dry ground or street: 0.95, the prototype's. |
| 243 | `DistrictPlan.BLOCK_DEPTH` | `LATTICE - 1` | A building wider than this, in a homes cell, may merge blocks: 7, a block's depth. |
| 246 | `DistrictPlan.JOIN_ROUNDS` | `40` | The most pieces the join joins, one at a time: 40, the prototype's. |
| 249 | `DistrictPlan.JOIN_DX` | `{ 0, 0, 1, - 1 }, JOIN_DY = { 1, - 1, 0, 0 }` | The join's steps in the prototype's order: south, north, east, west - so of two ways as short it takes the prototype's. |
| 252 | `DistrictPlan.HALF` | `0.5` | A street's least surface, in plots of its right of way: a half, 15 m (spec 2.5). |
| 255 | `DistrictPlan.FULL` | `1.0` | ...and its full width, a whole plot, 30 m. |
| 260 | `DistrictPlan.FIXED_HIGHWAY` | `BuildingVisual.HIGHWAY` | A fixed plot: an Elevated Highway's (BuildingVisual.HIGHWAY). |
| 263 | `DistrictPlan.FIXED_RAIL` | `TilePainter.RAIL` | ...a railway's track (TilePainter.RAIL). |
| 266 | `DistrictPlan.FIXED_YARD` | `CityRuns.F_YARD` | ...a railway yard's ground, a Rail Terminal the city's runs drew on its track (0.7.89, CityRuns.F_YARD): no street crosses it, no building stands on it. |
| 269 | `DistrictPlan.SITE_FIELD` | `1` | A site with nothing on it: a field, built on last. |
| 272 | `DistrictPlan.SITE_MINED` | `2` | A site a mine or well stands on: its own. |
| 275 | `DistrictPlan.CLOSED` | `0` | A cell's layout: not opened. |
| 277 | `DistrictPlan.HOMES` | `1` | ...homes: long blocks (or square, the ladder's), for what follows people. |
| 279 | `DistrictPlan.ESTATE` | `2` | ...an estate: one spine, two strips, for industry and the outer kinds. |
| 284 | `DistrictPlan.NONE` | `0` | A plot's street, its low three bits: none. |
| 286 | `DistrictPlan.TRACK` | `1` | ...a track: a street the city has bought no road for (spec 2.5, R4). |
| 288 | `DistrictPlan.GRAVEL` | `2` | ...gravel. |
| 290 | `DistrictPlan.PAVED` | `3` | ...paved. |
| 292 | `DistrictPlan.HIGHWAY` | `4` | ...an Elevated Highway's plots given to the plan as a street's surface (Input.highway: CityMap's deal did so from 0.7.72, its plans to 0.7.88; since 0.7.89 the runs lay every one and the map gives ... |
| 294 | `DistrictPlan.SEAM` | `5` | ...a seam: a street on the district's edge whose surface is the neighbour's (SEAMS). |
| 296 | `DistrictPlan.UNDER` | `6` | ...a street passing beneath a highway (spec 2.7: elevated). |
| 298 | `DistrictPlan.KIND_MASK` | `7` | The kind's bits. |
| 300 | `DistrictPlan.WIDTH_SHIFT` | `3` | Its width, bits 3 and 4: 1 half, 2 full; 0 for none (a track, a seam, beneath a highway). |
| 302 | `DistrictPlan.ROLE_SHIFT` | `5` | Its role, bits 5 to 7. |
| 304 | `DistrictPlan.ROLE_STREET` | `1` | ...a cell's street. |
| 306 | `DistrictPlan.ROLE_ARTERIAL` | `2` | ...an arterial: a cell's ring. |
| 308 | `DistrictPlan.ROLE_BOULEVARD` | `3` | ...an arterial of a boulevard cell, either row (spec 2.5). |
| 310 | `DistrictPlan.ROLE_SHORE` | `4` | ...a street along a cut, joining a dead end to its neighbour (H2). |
| 312 | `DistrictPlan.ROLE_JOIN` | `5` | ...a street the join laid along the lattice. |
| 314 | `DistrictPlan.BRIDGE` | `1<<8` | A bridge over fresh water, bit 8. |
| 316 | `DistrictPlan.CROSSING` | `1<<9` | A level crossing of a railway, bit 9. |
| 533 | `DistrictPlan.BUILDERS` | `new ThreadLocal<>()` | KEPT (0.7.94, batch RD7): each thread's builder, its arrays - about 4 MB - and the ladder's turns - about 0.4 MB each, one a step of the longest chain it has climbed - made once and used plan after... |
| 718 | `DistrictPlan.Builder.WORDS` | `(FRAME + 63) / 64` | The streets row by row as bits, WORDS longs a row: what a cell's reach is read from. |
| 792 | `DistrictPlan.Builder.WORN` | `1<<30` | The stamps' ceiling: a builder is replaced past it, half the int's range, far beyond what a plan stamps (a dense plan some tens of thousands). |
| 1510 | `DistrictPlan.Builder.FLIP` | `1<<31` | estateLines()'s flag: the cell's streets run the other way from its own direction (a cell the ground cuts). |
| 1526 | `DistrictPlan.Builder.FULL_LINES` | `new java.util.concurrent.ConcurrentHashMap<>()` | Each layout a full cell takes for a building's shape, its ring whole: {across, down, boulevard, the cell's direction} to estateLines()'s answer - the same whatever lies about it (no street beyond i... |
| 1635 | `DistrictPlan.Builder.H_STREET` | `1, H_ARTERIAL = 2, V_STREET = 4, V_ARTERIAL = 8, LAY_REFUSED = 15, LAY_UNDER = 16` | LAY CODES (0.7.94, batch RD7): what layPlot() makes of each plot, read from the input once a plan - the plot refused to a street running east-west (H_STREET), to an arterial so (H_ARTERIAL), and no... |
| 2093 | `DistrictPlan.Builder.LAST_WORD` | `(1L<<(FRAME - 64 *(WORDS - 1))) - 1` | A row's last word's plots in the frame: bit 0 to FRAME - 1 - 64 x (WORDS - 1). |
| 2152 | `DistrictPlan.Builder.WINDOW` | `(1L<<(INTERIOR + 2 * REACH)) - 1` |  |
| 2539 | `DistrictPlan.Builder.SPLIT_NEAR` | `CELL` | splits()'s first walk keeps within this many plots of the box: a cell (NEAR FIRST). |

### EconomyManager.java ([map](map/EconomyManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 601 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, BuildingTy...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 615 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 779 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 781 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 783 | `EconomyManager.NRRP_CAP` | `6.3` | ...and its most a unit, in founding thousands: $6,300. |
| 785 | `EconomyManager.NRRP_FULL_BELOW` | `350` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 787 | `EconomyManager.NRRP_NONE_FROM` | `450` | ...and none for a unit worth this or more: $450,000. |
| 2166 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 1352 | `Exchange.BY_DESK` | `0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3, BY_FUND = 4` | Who sells, by class: the desk, the world, the leavers, a household cell, and since 0.7.14 the city's fund - for the fill rate by seller. |
| 1680 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. |
| 1682 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). |

### Expectations.java ([map](map/Expectations.md))

| line | constant | value | says |
|---:|---|---|---|
| 72 | `Expectations.ADAPT_MONTHS` | `12` | Months recent inflation is smoothed over, as an exponential average: a twelfth of the gap a month. |
| 75 | `Expectations.TOLERANCE` | `.01` | How far smoothed inflation may sit from the target, a fraction a year, and still count as on target: one point. |
| 78 | `Expectations.MISS_SCALE` | `.04` | The miss past TOLERANCE that costs credibility at the full LOSS_MONTHS speed: four points more, five off target in all. |
| 81 | `Expectations.LOSS_MONTHS` | `24` | Months over which credibility falls toward KMIN on a full miss nobody leans against: a twenty-fourth of the distance a month, half of it gone in about seventeen. |
| 84 | `Expectations.GAIN_MONTHS` | `60` | Months over which credibility climbs toward KMAX on target: a sixtieth of the distance a month, about five years to rebuild. |
| 87 | `Expectations.KMIN` | `.25` | The least anyone believes the bank: a quarter of expected inflation stays the target however long it misses (.1 was measured to lock the founding at 10%). |
| 90 | `Expectations.KMAX` | `.95` | The most anyone believes it: a twentieth of expected inflation is always recent experience. |
| 93 | `Expectations.KSEED` | `.80` | Where a new city, and a save from before the anchor, starts: a bank believed more than not, with something to earn. |
| 96 | `Expectations.EXPECTED_FLOOR` | `-.01` | The least inflation anybody expects, a fraction a year: the floor under the deflation attractor (minus one per cent). |
| 234 | `Expectations.SAVE_SLOTS` | `8` | The slots a save carries: credibility, smoothed, expected, level, seeded - then the lean, the level the month's constants are struck at, and (0.7.45) the month's move in credibility. |

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
| 500 | `ForeignAccounts.RATE_PULL` | `4.0` | How far the city's own rate is above the world's - in real terms since 0.7.2 - and what that is worth to the currency. |
| 503 | `ForeignAccounts.MAX_RATE_PRESSURE` | `4.0` | A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 p... |
| 539 | `ForeignAccounts.EXPECTED_REVERSION` | `.15` | How fast investors expect a currency away from parity to come back, a fraction of the log gap a year: the reversion that sets the UIP level at e^(-gap / this). |
| 1009 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |
| 1565 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the sp... |
| 1568 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 1583 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb. |

### Formats.java ([map](map/Formats.md))

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |
| 35 | `Formats.WHOLE_DOLLARS_MOST` | `0x1p53` | The most dollars cash() prints whole (0.7.54): 2^53, about nine quadrillion, the last whole number a double holds exactly. |

### Founding.java ([map](map/Founding.md))

| line | constant | value | says |
|---:|---|---|---|
| 75 | `Founding.INSANE_LAND_COUPON` | `.03` | Insane's coupon on the land it owes for, a year: 3%, Jerus's number. |
| 78 | `Founding.INSANE_LAND_YEARS` | `20` | Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the five term loans (LongTermBond.MATURITIES). |
| 86 | `Founding.LEAN_CASH` | `25_000` | Lean's treasury, in thousands: D$25M - two-thirds of the founding village at a new city's invoices (three-quarters until the builders' sales tax went into them, 0.7.19), so the city borrows from it... |
| 89 | `Founding.LEAN_RESERVE_USD` | `10_000` | Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. |
| 92 | `Founding.WEALTHY_CASH` | `2_500_000` | Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. |
| 95 | `Founding.WEALTHY_RESERVE_USD` | `1_000_000` | Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). |
| 169 | `Founding.MIN_CASH` | `6_000` | The least a city may be founded with in its treasury, in thousands: D$6M, ten houses and a shop at a new city's invoices - D$5.37M since the builders' sales tax went into them (0.7.19; it was D$5M,... |
| 172 | `Founding.MAX_CASH` | `10_000_000` | The most, in thousands: D$10B, four times the Wealthy start. |
| 175 | `Founding.MIN_RESERVE_USD` | `0` | The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. |
| 178 | `Founding.MAX_RESERVE_USD` | `4_000_000` | The most, in thousands of US dollars: US$4B, four times the Wealthy start. |
| 183 | `Founding.DEFAULT_CITY_NAME` | `"Danzik"` | The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. |
| 186 | `Founding.MAX_CITY_NAME_LENGTH` | `24` | The longest city name: room for the window's title and the slot list's line, not a policy. |
| 191 | `Founding.DEFAULT_WORLD_SEED` | `4127` | The world a founding stands on when nobody rolls another: 4127, the map mockup's default seed (city-map.html), so every harness and the playtest found on the same ground. |
| 194 | `Founding.ROLLED_SEED_MAX` | `999_999_999` | The largest seed the founding screen's dice rolls: 999,999,999, nine digits a player can read back and type. |
| 388 | `Founding.VILLAGE` | `{ { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "Constructi...` | The founding village: the playtest's hand-built settlement, name and count. |
| 392 | `Founding.FIRST_WORKS` | `{ "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" }` | The first big works, in the order a young city tends to need them. |

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
| 782 | `FundView.TRADES` | `"Trades", INCOME = "Income", MONEY = "Money in and out", EVENTS = "Events"` | The activity page's chips: what each kind of row is filed under. |

### Game.java ([map](map/Game.md))

| line | constant | value | says |
|---:|---|---|---|
| 635 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 943 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 946 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town... |
| 949 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2019 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 4587 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 4590 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 5952 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 6970 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 8043 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 8216 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 8338 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 9896 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 9908 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 9911 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 9926 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 9935 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, W... |
| 9943 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 11892 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 4593 | `GameVersion.VERSION` | `"0.7.99"` | Bump on release. |
| 5211 | `GameVersion.SAVE_FORMAT` | `36` | The save shape. |
| 5214 | `GameVersion.FIRST_SECTOR_FORMAT` | `21` | The first format a sector can be read out of. |
| 5216 | `GameVersion.NAME` | `"CityBuilderSim"` |  |

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
| 816 | `Healthcare.STRAINED` | `.90` | Past this share of the ovens' throughput, say so. |
| 819 | `Healthcare.PLOT_WARNING_MONTHS` | `60` | Warn once the ground will not last this long at the current rate. |
| 877 | `Healthcare.STATE_BEFORE_FULL_BILL` | `12` | How many figures the state carried before the full-service bill was appended (2026-09-19). |
| 880 | `Healthcare.STATE_BEFORE_COVERAGE` | `13` | ...and before the three coverages were, the same day. |
| 883 | `Healthcare.STATE_BEFORE_SERVED` | `16` | ...and before the three kinds' served counts were (A3, 2026-10). |

### HistorySave.java ([map](map/HistorySave.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 920 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "deaths", "a...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 942 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

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
| 2161 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2243 | `HouseholdBalance.FOOD_ASSISTANCE_MEANS_SHARE` | `.5` | A household is eligible for food assistance when a basket a head would take more than this share of what it has after its fixed bills and its investment income over the year: half. |
| 2257 | `HouseholdBalance.MEANS_INCOME_MONTHS` | `12` | Months the means test's investment income is smoothed over, as an exponential average - a twelfth of the gap a month, the year Expectations smooths inflation over (0.7.45; the UI spec's B16). |
| 2942 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 3139 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point - so two and a half points of spread would take half an... |
| 3142 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and never more than this share of one issue: half, because a bond market with no bank in it is no longer the city's bank's market. |
| 3936 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 3939 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 4213 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 4216 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 4219 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 4222 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 4225 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 4235 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 4245 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 4254 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 4266 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim ... |
| 4280 | `HouseholdBalance.CELL_SLOTS_BEFORE_GROCERIES` | `CELL_SLOTS_BEFORE_BONDS + 1` | ...and the last sale's groceries, appended 0.7.43: the baskets each household got, the baskets it asked for at the price, the baskets it needed, and the food assistance paid on them - written at th... |
| 4288 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEANS_INCOME` | `CELL_SLOTS_BEFORE_GROCERIES + 4` | ...and the investment income the food assistance means test reads, smoothed over MEANS_INCOME_MONTHS, appended 0.7.45. |
| 4298 | `HouseholdBalance.CELL_SLOTS_BEFORE_AFTER_FIXED` | `CELL_SLOTS_BEFORE_MEANS_INCOME + 1` | ...and before each cell's income after its fixed bills was carried (A4). |
| 4301 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_AFTER_FIXED + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month'... |

### Inbox.java ([map](map/Inbox.md))

| line | constant | value | says |
|---:|---|---|---|
| 39 | `Inbox.KEEP_MONTHS` | `24` | How long a resolved notice stays readable. |
| 216 | `Inbox.BODY_COLUMNS` | `62` | A body line's most characters (0.7.73): the measure the bodies written by hand keep, inside the list's width in its mono face. |

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
| 68 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 71 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 74 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 85 | `LandManager.SQ_FT_PER_KM2` | `SQ_M_PER_KM2 / SQ_M_PER_SQ_FT` | Square feet in a square kilometre: a million square metres over a square foot's, 10,763,910.4 (0.7.57). |
| 104 | `LandManager.M2_WORDS_BELOW` | `10_000` | Areas under this many square metres read in square metres (0.7.68): a hundredth of a square kilometre, so a building's plot reads "743 m\u00b2" (a House's 8,000 square feet) and not "0.000743 km\u0... |
| 166 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres; since 0.7.67 the figure a new city's centre of whole blocks is drawn to hold, and it owns the dry plots drawn, a little more (CityLand.fou... |
| 207 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 276 | `LandManager.FOREST_REGROWTH` | `1.0 / 240` | Forest's stored depletion falls by this share a month: 1/240, a twenty-year time constant, so 95% of what is cut grows back within a sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). |
| 777 | `LandManager.TWO_POOLS_FORMAT` | `35` | The first save format that carries the two pools: its depletion's oil the ground pool's, and the offshore pool's E beside it. |
| 1245 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### LandMap.java ([map](map/LandMap.md))

| line | constant | value | says |
|---:|---|---|---|
| 36 | `LandMap.OUTSIDE` | `0, CENTRE = 1, BOUGHT = 2, OFFER = 3` | Whose a plot is: nobody's, the centre's, a purchase's, or an offer's. |
| 85 | `LandMap.NORTH_EDGE` | `0, EAST_EDGE = 1, SOUTH_EDGE = 2, WEST_EDGE = 3` | The four edges a run of the outline lies on, by the side of the city's ground it bounds: its north edge, east, south and west. |
| 469 | `LandMap.CLASS_ONE` | `{ "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School", "Health", "Safety...` | One building of each class, as the hover card names a type it has no name for. |
| 526 | `LandMap.QUAY_WORDS` | `"Quay: a terminal's berth, where its ships come alongside"` | A terminal's quay in the hover (0.7.97): where its ships come alongside. |
| 529 | `LandMap.JACKET_WORDS` | `"Offshore platform: its wells drilled from the jacket"` | An offshore platform's jacket in the hover (0.7.97). |
| 532 | `LandMap.WELL_WORDS` | `"Platform well, on its sea site"` | ...a platform well, on its sea site. |
| 535 | `LandMap.RING_WORDS` | `"A platform's 500 m safety zone"` | ...the ring of a platform's safety zone (the research's 3.3 [W32]). |
| 538 | `LandMap.PIPE_WORDS` | `"Crude pipeline, buried: the platforms' crude ashore without tankers"` | ...a crude pipeline, buried (spec-roads-and-ports.md 2.8). |
| 541 | `LandMap.RAMP_WORDS` | `" · a ramp"` | A highway's ramp in the hover (0.7.89; spec 2.7). |
| 544 | `LandMap.RAIL_OVER_WORDS` | `" · the railway over it"` | ...and the railway bridging it (0.7.89; spec 2.8). |
| 547 | `LandMap.TRACK_WORDS` | `"Track: the city has bought no road here"` | A track's words in the hover (0.7.88; spec 5). |
| 550 | `LandMap.HALF_WORDS` | `"15 m", FULL_WORDS = "30 m"` | A street's widths in the hover: half (15 m) and full (30 m), a plot's 30 m right of way (spec 2.1). |
| 553 | `LandMap.PACKED_WORDS` | `" · packed without a street"` | A building packed without a street, in the hover (R7). |

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
| 66 | `LandParcel.MOSTLY_SEA` | `0.70` | The share of an offer's area that, when sea, tags it "mostly sea": 70% - listed, because sea is cheap, but never the best value (spec-land 2.2). |
| 248 | `LandParcel.RECT_FIELDS` | `7` | Where a record keeps the rectangle: side, place, level, x0, y0, x1, y1 - seven. |
| 251 | `LandParcel.OFFER_FIELDS` | `1 + RECT_FIELDS + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide an offer's record is: its id, its rectangle (side, place, level, x0, y0, x1, y1), its five areas, its sites, its amounts, its price and the month it was listed - 29. |
| 254 | `LandParcel.PURCHASE_FIELDS` | `RECT_FIELDS + 3 + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide a holding's record is: its rectangle, the month bought, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 31 (spec-grid 3, M3). |

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
| 24 | `LongTermBond.MATURITIES` | `{ 10, 20, 30, 40, 50 }` | The only terms a term loan is issued at, in years: five benchmark maturities, so the curve is five points a player can read. |
| 27 | `LongTermBond.REFUSAL` | `"Term loans are issued at 10, 20, 30, 40 or 50 years."` | What the treasury is told when it asks for any other term. |

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

### MapTiles.java ([map](map/MapTiles.md))

| line | constant | value | says |
|---:|---|---|---|
| 31 | `MapTiles.TERRAIN_KEPT` | `2048` | Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. |
| 34 | `MapTiles.PAINTED_KEPT` | `1` | Painted tiles kept, with their inputs: 1, the hover's (star RD2-5; 64 until 0.7.87) - about 31 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512). |
| 37 | `MapTiles.BUDGET_MB` | `48` | What every cache of the view together may hold, in MB: 48 (spec-land 2.6). |
| 40 | `MapTiles.DESIGN_W` | `1345, DESIGN_H = 806` | The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.... |

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
| 309 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank, and since 0.7.14 the city's fund (on the end, so every other pool keeps its place). |
| 430 | `MoneyAudit.CENT` | `.01` | A harness's cent: 0.01 of a unit of a thousand dollars, the least a money comparison has allowed. |
| 433 | `MoneyAudit.RELATIVE_TOLERANCE` | `1e-12` | ...and the share of the figures compared that their own rounding is allowed past it: a part in a trillion. |
| 446 | `MoneyAudit.ULP_STEPS` | `64` | ...and the least a floor allows, in a double's steps at the size of the figures compared (0.7.64; 8 in 0.7.63): the most an identity held to a floor under a cent was measured to miss by, in steps o... |

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
| 346 | `Mortgage.Decision.DOWN_PAYMENT` | `"down payment"` | Trimmed, or refused, because its own funds could not put the down payment on it. |
| 348 | `Mortgage.Decision.LENDERS_TEST` | `"lender's test"` | ...because its rent would not cover the payment MORTGAGE_DEBT_COVERAGE times. |

### Motoring.java ([map](map/Motoring.md))

| line | constant | value | says |
|---:|---|---|---|
| 97 | `Motoring.CAR_FUEL_PER_JOURNEY` | `.002` | What a journey to work by car burns, in world money: $2.00, fifteen kilometres at eight litres a hundred and about $1.65 a litre. |
| 111 | `Motoring.LITRES_PER_JOURNEY` | `1.2` | ...and the litres in it (0.7.62): fifteen kilometres at eight litres a hundred, the same journey. |

### NationalAccounts.java ([map](map/NationalAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 201 | `NationalAccounts.HELD` | `{ Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.PETROL, Good.DIESEL, Good.LPG, Good.N...` | The goods the fifth term measures, in the order the save keeps their units (slots 15 on, EconomyManager.getNationalAccountsState()): a new one goes on the end. |
| 205 | `NationalAccounts.HELD_BEFORE_FUEL` | `3` | ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. |
| 208 | `NationalAccounts.HELD_WITH_FUEL` | `4` | ...and how many a save from 0.7.62 to 0.7.75 carries: the three and FUEL, which FuelSplit makes PETROL and DIESEL. |
| 211 | `NationalAccounts.NOT_HELD` | `Good.CARS` | ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. |
| 833 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

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
| 681 | `OilView.PLATFORM_COLOURS` | `{ "#4fa3c7", "#2c6f8f", "#86c6e2" }` |  |
| 682 | `OilView.OTHER_PLATFORMS` | `"#1d4b62"` |  |
| 685 | `OilView.GRID` | `"#1d2b3c", ACCENT = "#5aa9ff"` | A gridline, and the now line (mockup 2's). |
| 752 | `OilView.NOTHING_LIFTED` | `"no well stands, and none would lift"` | What the chart says with nothing to draw. |
| 776 | `OilView.AHEAD_WORDS` | `"if nothing new is built ›"` | ...and under them. |
| 826 | `OilView.NOT_COUNTED` | `"not counted until a month runs"` | What the page says while the month is not counted. |
| 842 | `OilView.TEXT` | `RefineryView.TEXT, GOOD = "#3fb950", BAD = "#f85149", WARN = RefineryView.HOT` | The colour of a figure that is the answer, of good news and bad, and of a warning. |
| 897 | `OilView.FACT` | `RefineryView.TEXT_2` | The figure's colour on a card: the secondary grey (mockup 2's .kv b). |
| 1019 | `OilView.UNIT_HEADS` | `{ "Unit · what it turns into what", "b/d", "running", "spread", "state" }` | The table's heads, in its columns' order (mockup 2's). |
| 1022 | `OilView.UNIT_WIDTHS` | `{ 0, 64, 62, 76, 210 }` | The columns' widths (b/d, running, spread, the state; the first takes the rest - 238 px in the units' box's 650 at the 1,389 window). |
| 1025 | `OilView.PILL` | `RefineryView.TEXT_2, PILL_BUILDING = ACCENT, PILL_GOOD = GOOD, PILL_GATE = WARN, PILL_B...` | The pills' colours: neutral, under way, good, a gate the player can clear, a loss (mockup 2's .pill, .b, .w, .r). |
| 1080 | `OilView.NO_BREAK` | `'\u00a0'` | The space inside a table's item, where a line does not break: "fuel oil 7" stays whole. |
| 1169 | `OilView.PRODUCT_HEADS` | `{ "Product", "price here", "world", "× crude", "made here", "used here", "imported", "e...` | The table's heads, in its columns' order (mockup 2's). |
| 1173 | `OilView.PRODUCT_WIDTHS` | `{ 150, 76, 76, 58, 72, 72, 72, 72, 0 }` | The columns' widths (the product, the seven figures; where it went takes the rest - 560 px in the box's 1,208 at the 1,389 window). |
| 1176 | `OilView.PRODUCTS_WORDS` | `"this month · litres(M a million, k a thousand); crude, bitumen and coke in" + " tonnes...` | The products table's line over it. |
| 1180 | `OilView.PRODUCTS_RIGHT` | `"world: halfway between the import and the export price · × crude: the" + " world price...` | ...and at its right. |
| 1184 | `OilView.CRUDE` | `RefineryView.CRUDE` | Crude's colour in the table (mockup 1's). |
| 1207 | `OilView.HALF` | `.5` | Under half a unit, a figure is written as nothing (RefineryView.figure() would write "0"). |
| 1260 | `OilView.LADDER_STEPS` | `100` | The steps a lever's ladder takes from nothing to the most it may be set to: a hundredth of its reach a step (★ O12-8). |
| 1279 | `OilView.NO_RESERVE` | `"No Strategic Reserve stands.Build one under Build › Industry › Oil storage: the city" ...` | What the reserve's section says when none stands and none is held. |

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

### Ports.java ([map](map/Ports.md))

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Ports.SEA_FREIGHT_SHARE_LIQUID` | `.08` | Liquid bulk by sea against a lorry, at 5,000 km: .08, the mean of the VLCC, Suezmax, Aframax-LR and MR rows of the rule. |
| 76 | `Ports.SEA_FREIGHT_SHARE_DRY_BULK` | `.07` | Dry bulk by sea against a lorry, at 5,000 km: .07, the mean of the Capesize and Panamax rows. |
| 79 | `Ports.SEA_FREIGHT_SHARE_CONTAINER` | `.16` | Containers by sea against a lorry, at 5,000 km: .16, the deep-sea box row. |
| 82 | `Ports.SEA_FREIGHT_SHARE_GENERAL` | `.20` | General cargo by sea against a lorry, at 5,000 km: .20, the 5,500 t ship's row. |
| 110 | `Ports.TONNES_A_TEU` | `9` | A loaded TEU's cargo, in tonnes: 9 (the research's 8-10 [P43][P44][P45]; spec-oil 2.9). |
| 113 | `Ports.FEEDER_TEU` | `3_000` | A feeder's boxes: 3,000 TEU, the top of [P43]'s feeder class ("under 3,000"), est. |
| 158 | `Ports.WORTH_A_BERTH` | `ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION` | The share of a berth's month the uncovered tonnes of its kind must reach before the test player orders one (spec-oil 5's O9 row): the railway's own line, Rail.MIN_LINE_UTILISATION. |
| 160 | `Ports.KINDS` | `Cargo.values().length` |  |
| 161 | `Ports.STREAMS` | `Traffic.values().length` |  |

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
| 500 | `PriceIndex.SLOTS_BEFORE_CHAIN` | `5 + WINDOW + 5` | The slots a save carried before the chained basket (0.7.43): the two-component basket, the ring, the marks and the settling count. |
| 503 | `PriceIndex.CHAIN_MARKER` | `743` | What follows them in a save from 0.7.43 on, so an older array - which ends there - is told apart from one that goes on. |
| 506 | `PriceIndex.CHAIN_TAIL_0743` | `4 + COMPONENTS * 3 + FEE_LINES * 3 + 1 + WEIGHT_MONTHS *(COMPONENTS + FEE_LINES)` | The chained basket's slots behind the marker as 0.7.43 wrote them: the link, its month, the pending flag, the bases, weights and last prices, the fee lines', and the trailing year. |

### RefineryView.java ([map](map/RefineryView.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `RefineryView.TANK_ORDER` | `{ Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS, Good.FUE...` | The products in the tank's order, top to bottom: light to heavy, as the column's cuts boil (mockup 1), and the coker's coke last. |
| 70 | `RefineryView.COLUMN` | `{ Stream.GAS, Stream.LIGHT_NAPHTHA, Stream.HEAVY_NAPHTHA, Stream.KEROSENE, Stream.DIESE...` | The straight-run cuts the column holds, top to bottom: the slate's CUT_ order; the heavy crude's residue is drawn as the residue's. |
| 74 | `RefineryView.LITRES_A_TONNE_DRAWN` | `RefineryFlow.RESIDUE_LITRES_PER_TONNE` | Litres a tonne of bitumen and coke are drawn at: the residue's, RefineryFlow.RESIDUE_LITRES_PER_TONNE - the weight the flow strikes them at, so a unit's ribbons balance to the litre. |
| 173 | `RefineryView.End.FURNACES` | `new End(null, null, null)` |  |
| 538 | `RefineryView.HEIGHT` | `404` | The picture's height on the page (mockup 1's 404 px at 1,389 x 868). |
| 541 | `RefineryView.LEAST_WIDTH` | `1000` | The least width it is laid out at: a second rank's line of words fits before the tank (the 1,280 window's card is about 1,094). |
| 544 | `RefineryView.TOP` | `64` | Room above the columns for their headings: three lines over the column. |
| 547 | `RefineryView.PAD` | `4` | ...and the margin under and over what the columns hold. |
| 550 | `RefineryView.SOURCE_ZONE` | `182` | The sources' room at the left: an icon, three lines of words and the bar at its right edge. |
| 553 | `RefineryView.SOURCE_W` | `10` | The sources' bar. |
| 556 | `RefineryView.BUYER_ZONE` | `236` | The takers' room at the right: the bar, an icon, a name and its figure, a line of words. |
| 559 | `RefineryView.BUYER_W` | `12` | The takers' bar. |
| 562 | `RefineryView.COLUMN_W` | `82` | The column's width (mockup 1's 82). |
| 565 | `RefineryView.UNIT_W` | `56` | A unit's box (mockup 1's 66, narrower for the second rank beside the first). |
| 568 | `RefineryView.TANK_W` | `140` | The tank's width: a product's name and its figure inside its band, and a block's line under it. |
| 571 | `RefineryView.COLUMN_AT` | `.10, FIRST_RANK_AT =.29, SECOND_RANK_AT =.42, TANK_AT =.69` | Where the column, each rank of units and the tank stand, as a share of the room between the sources' bar and the takers': a rank's line of words fits between its box and the tank. |
| 574 | `RefineryView.UNIT_GAP` | `18` | The gap over a unit's box: its line of words (mockup 1's 18). |
| 577 | `RefineryView.PASS_GAP` | `3` | The gap between bands that run through the units' columns, and between a column's bands of different ends. |
| 580 | `RefineryView.SOURCE_GAP` | `26, BUYER_GAP = 12, BLOCK_GAP = 22` | The gap between the sources, between the takers, and between the tank's blocks (made, from the tanks, imported). |
| 583 | `RefineryView.LABEL_SPACING` | `33` | The least a taker's label centre stands from the next (mockup 1's 33). |
| 586 | `RefineryView.WORDS_IN_BAND` | `11` | The least band a word is written in: a cut's name in the column, a product's in the tank. |
| 589 | `RefineryView.LEAST_NODE` | `1.5` | The least a node is drawn, so a trickle shows. |
| 594 | `RefineryView.GROUND` | `"#152130"` | The ground the picture sits on: the card's (ui/Palette.RAISED), and a word's halo. |
| 596 | `RefineryView.CAP` | `"#101924"` | A roof, a cap, a dome. |
| 598 | `RefineryView.FRAME` | `"#3a4f6a"` | Their edges, and the column's and the tank's frame. |
| 600 | `RefineryView.CRUDE` | `"#a07d4a"` | Crude, in a ribbon and a source's bar. |
| 602 | `RefineryView.UNIT_FILL` | `"#1b2a3d", UNIT_EDGE = "#4a6283"` | A unit's box and its edge. |
| 604 | `RefineryView.FURNACE_FILL` | `"#2a1f1a", FURNACE_EDGE = "#6b4a3a", FLARE = "#ffb454"` | The furnaces' box and its edge, and the flare. |
| 606 | `RefineryView.TEXT` | `"#e6edf3", TEXT_2 = "#a9b8c9", TEXT_3 = "#8496ab"` | The words: the head's, the body's, the faint. |
| 608 | `RefineryView.HOT` | `"#e3b341"` | A word that something is imported. |
| 610 | `RefineryView.ON_BAND` | `"#0b1118"` | The words on a pale band. |
| 1202 | `RefineryView.AFTER_UNIT_GAP` | `6` | The gap under a unit's box before a band runs on. |
| 1205 | `RefineryView.BLOCK_WORDS` | `16` | The room under each of the tank's blocks for its line ("IMPORTED" and its litres): its baseline 13 px under, and the line's descent. |
| 1208 | `RefineryView.IMPORT_EDGE` | `"#5a6d84"` | The edge of the tank's imported block (mockup 1's). |
| 1211 | `RefineryView.FURNACE_WORDS` | `"burned %s · no diesel to cut it"` | The furnaces' line under their name. |
| 1214 | `RefineryView.NO_UNITS` | `"NO CONVERSION UNIT · EACH CUT SOLD AS IT IS"` | The units' heading when none stands. |
| 1217 | `RefineryView.FIGURE_IN_BAND` | `46` | The room a product's figure has at the right of its band in the tank. |
| 1220 | `RefineryView.BLOCK_FIGURE` | `52` | ...and a block's litres at the right of its line under it. |
| 1223 | `RefineryView.FIGURE_ROOM` | `58` | The room a figure has at the right of a band or a taker's name. |
| 1501 | `RefineryView.LIST_MOST` | `3` | The most products a taker's line names before "and more". |
| 1553 | `RefineryView.STRIP_HEAD` | `"THIS MONTH, BY PRODUCT"` | The strip's heading words. |
| 1556 | `RefineryView.STRIP_WORDS` | `"litres(M a million, k a thousand), bitumen and coke in tonnes · bar: taken here," + " ...` | ...and its line. |
| 1574 | `RefineryView.CELL_LINES` | `{ "made here", "imported", "exported", "tanks", "price here" }` | The strip's line names, in a cell's order. |

### Rollover.java ([map](map/Rollover.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `Rollover.NETTING_MONTHS` | `12` | How far back the surplus that nets a maturity is read, in months: Jerus's "net of last year's surplus" - a year. |
| 99 | `Rollover.BILL_MONTHS` | `12` | The note TWELVE_MONTH_BILL rolls into, in months: twelve, the longest note the treasury sells (the Finances tab's notes run 3 to 12 months). |

### SalesTaxLedger.java ([map](map/SalesTaxLedger.md))

| line | constant | value | says |
|---:|---|---|---|
| 49 | `SalesTaxLedger.TAXABLE_SALES` | `0, IMPORT_TAX = 1, ZERO_RATED = 2, CREDITED_INPUT = 3, PAYABLE = 4, CREDIT = 5, SLOTS = 6` | slots in a row |

### SeaRoutes.java ([map](map/SeaRoutes.md))

| line | constant | value | says |
|---:|---|---|---|
| 62 | `SeaRoutes.CELL` | `16` | The sea grid's cell, in plots: 16, 480 m (spec 4.1; the prototype's CG). |
| 65 | `SeaRoutes.SAMPLE` | `4` | A cell's ground is read at a sample every this many plots: 4, so 4 x 4 samples a cell. |
| 68 | `SeaRoutes.SALT_LEAST` | `12` | A cell is sea when at least this many of its 16 samples are salt: 12, three quarters (star O13-5; the prototype's 95% shut a 300 m narrows). |
| 71 | `SeaRoutes.SHORE_COST` | `3` | A shore cell's cost against an open one: 3 (spec 4.1: "three times the cost one cell from the shore"). |
| 74 | `SeaRoutes.OFFING_M` | `10_000` | How far past the city's radius the offing lies: 10 km (spec 4.1). |
| 77 | `SeaRoutes.BEARING_STEP` | `5` | The bearings tried for the offing, every this many degrees: 5 (spec 4.1). |
| 80 | `SeaRoutes.RAY_CELLS` | `400` | The longest run of sea a bearing is measured to, in cells: 400, 192 km (the prototype's). |
| 83 | `SeaRoutes.NEAR_CELLS` | `6` | How far round a berth its first sea cell is looked for, in cells: 6 (the prototype's nearest_sea()). |
| 86 | `SeaRoutes.ABYSS_M` | `3_000` | How far past the offing a boat sails into the abyss, fading: 3 km - "fading over a few km" (spec 4.1); less where land comes sooner (abyssRun()). |
| 89 | `SeaRoutes.SPREAD_DEG` | `3` | The most a call's last leg is turned either way, in degrees: 3 - "a few degrees" (spec 4.1). |
| 92 | `SeaRoutes.EXPANSIONS_MOST` | `250_000` | The most cells a route's A* looks at before it takes the straight lane: 250,000 - a way of about 1,000 km through open sea. |
| 95 | `SeaRoutes.BLOCK` | `64` | Cells a side of a block of the grid read at once: 64, 1,024 plots (a block a World.regionTerrain() call). |
| 302 | `SeaRoutes.OPEN_M` | `OFFING_M` | How far out a berth's water must reach for a terminal to stand on it, in metres: OFFING_M, the offing's own margin past the city (star O13-5). |

### Sector.java ([map](map/Sector.md))

| line | constant | value | says |
|---:|---|---|---|
| 241 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 331 | `Sector.Staffing.ANY` | `new Staffing(1, new double [ 0 ], new double [ 0 ], new double [ 0 ], new boolean [ 0 ])` | Always staffable: a building with no posts, or nobody to ask. |
| 655 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 661 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 690 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 704 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 718 | `Sector.LOADS_A_VAN_MONTH` | `TONNES_PER_VAN / Good.VANS.tonnesPerUnit()` | The loads a van-month carries: TONNES_PER_VAN at a van's own weight, three tonnes (Good.VANS.tonnesPerUnit()) - forty. |
| 721 | `Sector.KM_A_LOAD` | `50` | How far a load goes, there and back: fifty kilometres (runs/spec-oil.md 2.5, est.; to confirm). |
| 724 | `Sector.DIESEL_LITRES_PER_100_KM` | `12` | What a laden van or truck burns: twelve litres of diesel a hundred kilometres (runs/spec-oil.md 2.5, est.; to confirm). |
| 727 | `Sector.DIESEL_LITRES_A_VAN_MONTH` | `LOADS_A_VAN_MONTH * KM_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | ...so a van-month burns LOADS_A_VAN_MONTH x KM_A_LOAD x DIESEL_LITRES_PER_100_KM / 100 litres: 240. |
| 1829 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 1832 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

### SectorBooks.java ([map](map/SectorBooks.md))

| line | constant | value | says |
|---:|---|---|---|
| 402 | `SectorBooks.Debt.KINDS` | `BusinessDebtManager.DEBT_KINDS` | The four kinds, as the statements name them: BusinessDebtManager.DEBT_KINDS. |

### SectorFlow.java ([map](map/SectorFlow.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `SectorFlow.THROTTLES` | `{ "staffed", "power", "water", "roads", "well", "vans" }` | The six throttles in getOperatingRate()'s order, as the screen names them. |

### SectorStatements.java ([map](map/SectorStatements.md))

| line | constant | value | says |
|---:|---|---|---|
| 94 | `SectorStatements.NOTICE` | `.001` | A residual line - NOT ACCOUNTED FOR - is shown from a dollar (the cash page's rule since 0.7.30): under that it is the floating point. |
| 185 | `SectorStatements.THOUSANDS_UNTIL` | `10_000_000` | D2: a statement is in $ thousands - the model's own unit - until its largest figure passes seven digits, and then in $ millions, so the landlords' $1.2B a month does not run to ten. |
| 325 | `SectorStatements.REVENUE` | `"revenue", SALES_TAX = "salesTax", NET_REVENUE = "netRevenue", INPUTS = "inputs", GROSS...` | The statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by (Table.row()). |
| 331 | `SectorStatements.SUBSIDY` | `"subsidy", ARREARS = "arrears", DEPOSIT_INTEREST = "depositInterest", COUPONS = "coupon...` | The outside-the-trading-result lines' ids, in their order (F1; since 0.7.75 the bonds written off and F2's three). |
| 337 | `SectorStatements.FINANCE_NOTE` | `5` | The finance costs' note. |
| 339 | `SectorStatements.OUTSIDE_NOTE` | `6` | The outside-the-trading-result note. |
| 365 | `SectorStatements.OUTSIDE_IDS` | `{ SUBSIDY, ARREARS, DEPOSIT_INTEREST, COUPONS, FOREIGN_INTEREST, FORGIVEN, WRITTEN_OFF,...` | The outside lines' row ids, in outside()'s order. |
| 369 | `SectorStatements.OUTSIDE_LABELS` | `{ "Subsidy from the city", "Arrears the city paid", "Interest on its bank balance", "Co...` | ...and their labels, in the same order. |
| 447 | `SectorStatements.ASSETS_HEAD` | `"assetsHead", CURRENT_HEAD = "currentHead", CASH = "cash", STOCK = "stock", RECEIVABLES...` | The balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. |
| 457 | `SectorStatements.STOCK_NOTE` | `7, BUILDINGS_NOTE = 8, ABROAD_NOTE = 9, CAPITAL_NOTE = 10` | The stock's note, the buildings' and what it holds abroad - and since 0.7.75 its share capital's (R3). |
| 526 | `SectorStatements.OPERATING_HEAD` | `"operatingHead", NET_INCOME = "netIncome", PAID_EARLIER = "paidEarlier", TRADE_CREDIT =...` | The cash flow statement's row ids, which the screens and SectorStatementCheck look a row up by. |
| 620 | `SectorStatements.EQ_START` | `"eq.start", EQ_PROFIT = "eq.profit", EQ_OUTSIDE = "eq.outside", EQ_FOUNDED = "eq.founde...` | The statement of changes in equity's row ids, which the screens and SectorStatementCheck look a row up by. |
| 626 | `SectorStatements.CAPITAL` | `0, KEPT = 1` | The equity statement's two columns, each row's parts: share capital, and what it kept and revalued. |
| 629 | `SectorStatements.REVALUED_IDS` | `{ EQ_STOCK, EQ_LAND, EQ_BUILDINGS, EQ_ABROAD }` | R6's four parts, ids: what the prices did to what it began the month with. |
| 826 | `SectorStatements.B_INCOME_HEAD` | `"b.incomeHead", B_OTHER_INTEREST = "b.otherInterest", B_INTEREST = "b.interest", B_EXPE...` | The bank's statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by. |
| 834 | `SectorStatements.BANK_INTEREST` | `{ { "From the businesses", Bank.Line.FROM_BUSINESSES }, { "On the businesses' bonds it ...` | The interest income lines: {label, line}. |
| 844 | `SectorStatements.BANK_FEES` | `{ { "On the families' accounts", Bank.Line.ACCOUNT_FEES }, { "On the businesses' new lo...` | ...and the fees. |
| 909 | `SectorStatements.BS_ASSETS_HEAD` | `"bs.assetsHead", BS_LOANS_HEAD = "bs.loansHead", BS_GROSS = "bs.gross", BS_NET = "bs.ne...` | The bank's balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. |

### Sectors.java ([map](map/Sectors.md))

| line | constant | value | says |
|---:|---|---|---|
| 60 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = "Construct...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a... |
| 78 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERIALS, BUSIN...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |
| 261 | `Sectors.HOUSEHOLDS` | `"Households"` | The name the households' own imports are kept under among a good's buyers: the cars they buy from the world (Game.getHouseholdCarImports()). |
| 264 | `Sectors.CITY` | `"City"` | ...and the city's, among crude's buyers and sellers: its strategic reserve (0.7.85; StrategicReserve). |

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
| 863 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 866 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | The dial's cap, $50 a ride, twenty times the default, in founding money: past it no car owner rides while a journey's fuel costs under half of it, and the commuters with no car of their own still d... |
| 907 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 1351 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 1354 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 1357 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 1360 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 1363 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 1366 | `TaxPolicy.STATE_BEFORE_REAL_GRANT` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | ...and one from before the real FIXED grant (0.7.19): a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |
| 1369 | `TaxPolicy.STATE_BEFORE_REAL_FARE` | `STATE_BEFORE_REAL_GRANT + 1` | ...and one from before the real fare (0.7.42): one slot on top saying the FIXED amount is real, in founding money (0.7.19) - see realiseFixedGrant(). |
| 1372 | `TaxPolicy.STATE_BEFORE_FOOD_ASSISTANCE` | `STATE_BEFORE_REAL_FARE + 1` | ...and one from before food assistance (0.7.43): one slot on top saying the fare is real, in founding money (0.7.42) - see realiseFare(). |
| 1375 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_FOOD_ASSISTANCE + 1` | This build's array: the food assistance dial on top (0.7.43). |

### TilePainter.java ([map](map/TilePainter.md))

| line | constant | value | says |
|---:|---|---|---|
| 62 | `TilePainter.TILE` | `World.TILE` | Plots on a tile's side: World.TILE, 32. |
| 65 | `TilePainter.PLOTS` | `TILE * TILE` | Plots on a tile: 1,024. |
| 70 | `TilePainter.EMPTY` | `0` | Nothing on it. |
| 72 | `TilePainter.ROAD` | `1` | A road: a street of the plan, or a highway. |
| 74 | `TilePainter.BUILDING` | `2` | A building. |
| 76 | `TilePainter.FIELD` | `4` | A resource's site with nothing on it. |
| 78 | `TilePainter.RAIL` | `5` | A railway's track (0.7.72): its Painted.road is 0, or the kind of the street that crosses the track there. |
| 81 | `TilePainter.RAIL_OVER` | `CityRuns.F_RAIL_OVER` | A plot of the runs where the railway bridges a highway (0.7.89, CityRuns.F_RAIL_OVER; spec 2.8): Input.fixed's code, the highway's plot with the track over it. |
| 84 | `TilePainter.TRACK` | `4` | A road plot's kind past BuildingVisual's GRAVEL, PAVED and HIGHWAY (Painted.road, 0.7.88): a TRACK, a street the city has bought no road for (spec 2.5, R4). |
| 89 | `TilePainter.QUAY` | `1` | A plot's work at sea (Input.sea, Painted.sea's low two bits): a terminal's quay, out over the water from its box (CityShore)... |
| 91 | `TilePainter.JACKET` | `2` | ...an offshore platform's jacket... |
| 93 | `TilePainter.WELL` | `3` | ...a platform well, on the middle of its sea site. |
| 95 | `TilePainter.SEA_WORK` | `3` | The low two bits of Painted.sea. |
| 97 | `TilePainter.SEA_RING` | `4` | Painted.sea's bit for a plot a platform's safety ring crosses... |
| 99 | `TilePainter.SEA_PIPE` | `8` | ...and for one a crude pipeline crosses (buried: it takes no plot, and is drawn over what stands there). |
| 102 | `TilePainter.PLATFORM_ZONE_M` | `500` | A platform's safety zone, drawn as a faint ring about its jacket: 500 m (the research's 3.3 [W32]). |
| 105 | `TilePainter.RING_PLOTS` | `PLATFORM_ZONE_M / World.PLOT_M` | ...its radius in plots: 16.7. |
| 108 | `TilePainter.JACKET_PLOTS` | `2` | A jacket's side, in plots: 2, 60 m - mockup 3's platform, 12 px at 6 m a pixel (72 m), in whole plots (star O13-6). |
| 113 | `TilePainter.UNOWNED` | `0` | A site on ground the city does not own. |
| 115 | `TilePainter.UNWORKED` | `1` | ...on the city's ground, its holding not yet worked. |
| 117 | `TilePainter.WORKING` | `2` | ...in the holding being worked: drawn half grey. |
| 119 | `TilePainter.WORKED_OUT` | `3` | ...in a holding worked out: drawn grey (the mockup's star 7). |
| 124 | `TilePainter.S_KIND` | `7` | A plot's street as its district's plan gives it the painter (CityMap.Drawn, Input.street), a byte: its kind in the low three bits - DistrictPlan's NONE to UNDER... |
| 126 | `TilePainter.S_WIDTH_SHIFT` | `3` | ...its surface's width in bits 3 and 4: 1 half (15 m), 2 full (30 m), 0 none... |
| 128 | `TilePainter.S_ROLE_SHIFT` | `5` | ...its role in bits 5 and 6: 0 a cell's street (or one along a cut, or the join's), S_ARTERIAL, S_BOULEVARD... |
| 129 | `TilePainter.S_ARTERIAL` | `1, S_BOULEVARD = 2` |  |
| 131 | `TilePainter.S_BRIDGE` | `0x80` | ...and a bridge over fresh water in bit 7. |
| 134 | `TilePainter.STREET` | `1` | A painted street plot's role (Painted.role): a cell's street... |
| 136 | `TilePainter.ARTERIAL` | `2` | ...an arterial, on a cell's ring (every 32 plots, 960 m)... |
| 138 | `TilePainter.BOULEVARD` | `3` | ...a boulevard's, either row of a boulevard cell's arterials. |
| 152 | `TilePainter.JUNCTION_APART` | `8` | The + junction floor: 8 plots - eight House footprints (a House's 8,000 sq ft is drawn on one whole plot, BuildingVisual.footprint()), Jerus's "about 8 houses' length" from one + junction to the ne... |
| 155 | `TilePainter.REACH` | `JUNCTION_APART / 2` | A building is near a road when one lies within this many plots of it, across corners: 4, half the junction floor - a block between streets at the floor is 7 plots across, and its middle plot 4 from... |
| 163 | `TilePainter.STREET_BRIDGE` | `6` | A street's longest crossing of fresh water, gravel or paved: 6 plots (180 m), the mockup's paved bridge (its star 1); gravel's too since 0.7.77 (batch N5; Jerus, 2026-10-08: "gravel road bridge riv... |
| 166 | `TilePainter.MAX_BRIDGE` | `{ 0, STREET_BRIDGE, STREET_BRIDGE, 14 }` | The longest crossing of fresh water, in plots, by road kind: gravel and paved STREET_BRIDGE, a highway 14 (the mockup's star 1) - an arterial's since 0.7.87 (DistrictPlan.ARTERIAL_BRIDGE). |
| 169 | `TilePainter.RAIL_BRIDGE` | `14` | The longest crossing of fresh water a railway's track makes: 14, as a highway's (star N3-6). |
| 172 | `TilePainter.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west. |

### TileRaster.java ([map](map/TileRaster.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `TileRaster.GROUND` | `{ 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa }` | Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend. |
| 67 | `TileRaster.ROAD` | `{ 0, 0xffcdb07c, 0xffa4aab0, 0xff3b4048 }` | Each road kind's colour (gravel, paved, highway): the mockup's MAP. |
| 70 | `TileRaster.RAIL_LINE` | `0xff707070` | The railway's line, and its casing from 4 px a plot (0.7.72): openstreetmap-carto's rail grey, #707070 (the mockup drew no railway; star N3-9)... |
| 73 | `TileRaster.RAIL_DASH` | `0xffffffff` | ...and its dashes, white on every other plot along it: carto's rail dash. |
| 76 | `TileRaster.DECK` | `0xff5d4c3c` | A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. |
| 79 | `TileRaster.UNOWNED_DIM` | `0.4` | Ground no one in the city owns is drawn this much of the way to the map's background: 40%. |
| 82 | `TileRaster.VOID` | `0xff16222c` | The map's background, which unowned ground is dimmed toward: the mockup's --map-void. |
| 85 | `TileRaster.SITE_TINT` | `0.36` | A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). |
| 88 | `TileRaster.SITE_SPECKLE` | `0.75` | ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). |
| 91 | `TileRaster.SPECKLE_SHARE` | `0.15` | The share of a site's plots speckled: 15%. |
| 94 | `TileRaster.WORKED_GREY` | `0xff929496` | A worked-out site's grey: the mockup's mined grey. |
| 97 | `TileRaster.RING` | `0xff2b2d31` | A mine's ring and centre mark: the mockup's ring. |
| 100 | `TileRaster.INSET_FROM` | `3` | Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). |
| 103 | `TileRaster.EDGE_FROM` | `6` | ...and from which it is edged: 6 (the mockup's outlines). |
| 106 | `TileRaster.ROAD_WIDTH` | `{ 0, 0.30, 0.52, 0.86 }` | A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its ... |
| 109 | `TileRaster.ROAD_LEAST_PX` | `{ 0, 1, 2, 3 }` | ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). |
| 112 | `TileRaster.HIGHWAY_CASE` | `0xff1d2126` | A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... |
| 115 | `TileRaster.CENTRE_LINE` | `0xfff0cf5a` | ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). |
| 118 | `TileRaster.LINES_FROM` | `4` | Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. |
| 121 | `TileRaster.SMALL_MOST` | `0.92` | A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before)... |
| 124 | `TileRaster.SET_BACK` | `0.07` | ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). |
| 128 | `TileRaster.VERGE` | `0xffbfd39a` | A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). |
| 131 | `TileRaster.TRACK_DASH` | `0xff8a6a3c` | A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). |
| 134 | `TileRaster.SURFACE` | `{ 0, DistrictPlan.HALF, DistrictPlan.FULL }` | A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_... |
| 137 | `TileRaster.PACKED_DIM` | `0.35` | A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, ... |
| 223 | `TileRaster.QUAY_DECK` | `0xff3a4655` | A quay's deck: mockup 3's jetty, #3a4655... |
| 226 | `TileRaster.QUAY_EDGE` | `0xff5a6676` | ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. |
| 229 | `TileRaster.JACKET_FILL` | `0xff2a3540` | A platform's jacket: mockup 3's #2a3540... |
| 232 | `TileRaster.OIL_TAN` | `0xffc9b68f` | ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). |
| 235 | `TileRaster.RING_ALPHA` | `0.25` | A platform's 500 m ring over the water: the tan at the mockup's 25%... |
| 238 | `TileRaster.RING_DASH_PX` | `3` | ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). |
| 241 | `TileRaster.PIPE_ALPHA` | `0.5` | A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). |
| 242 | `TileRaster.PIPE_ON_PX` | `2, PIPE_OFF_PX = 4` |  |
| 357 | `TileRaster.BLOCK_ALPHA` | `{ 0.4, 0.6, 0.8, 0.96 }` | A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... |
| 360 | `TileRaster.BLOCK_FILLS` | `{ 0.12, 0.28, 0.5 }` | ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). |
| 363 | `TileRaster.BLOCK_MIDDLE` | `4` | Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... |
| 366 | `TileRaster.BLOCK_FAR` | `8` | ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. |
| 449 | `TileRaster.HIGHWAY_LEAST_PX` | `2` | The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). |
| 663 | `TileRaster.RAMP_WIDTH` | `ROAD_WIDTH [ BuildingVisual.GRAVEL ]` | A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). |
| 804 | `TileRaster.ROAD_SMALL` | `{ 0, 0xffcdb07c, 0xff6a7077, 0xff3b4048 }` | A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it ... |

### Trade.java ([map](map/Trade.md))

| line | constant | value | says |
|---:|---|---|---|
| 25 | `Trade.WORLD` | `"world"` | The other side of every export and every import. |
| 28 | `Trade.HOUSEHOLDS` | `"households"` | The households, as a buyer of the basket. |
| 31 | `Trade.CITY` | `"city"` | The treasury, as a buyer of its own buildings' materials and repairs. |

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
| 92 | `UtilitiesHandler.FRESH_UNITS_PER_KM2` | `121_600` | Units of fresh water a month one square kilometre of owned lake or river yields: the world's renewable river runoff (about 42,700 km3 a year, Shiklomanov) over the world's river and stream area (77... |
| 635 | `UtilitiesHandler.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` | miscelanous |

### World.java ([map](map/World.md))

| line | constant | value | says |
|---:|---|---|---|
| 59 | `World.PLOT_M` | `30` | A plot's side, in metres: 30, the map's smallest square and the mockup's cell. |
| 62 | `World.KM2_PER_PLOT` | `PLOT_M * PLOT_M / 1e6` | A plot's area in square kilometres: 30 m squared, 0.0009. |
| 65 | `World.TILE` | `32` | Plots on a tile's side: 32, 0.96 km - the unit the map paints (spec-land 2.6). |
| 68 | `World.DISTRICT` | `256` | Plots on a district's side: 256, 7.68 km - the unit the map stores (spec-land 2.5). |
| 71 | `World.CELL` | `2048` | Plots on a world cell's side: 2,048, 61.44 km - the unit the world's totals and far view are counted in, so plot, tile, district and cell nest by powers of two (spec-land star 4). |
| 74 | `World.CELLS` | `368` | World cells on the world's side: 368, so the world is 511.2 million km2, within 0.2% of the Earth's 510.1 million. |
| 77 | `World.SIDE` | `(long) CELL * CELLS` | Plots on the world's side: 2,048 x 368, 753,664 (22,610 km). |
| 80 | `World.CELL_KM2` | `(CELL * PLOT_M / 1000) *(CELL * PLOT_M / 1000)` | A world cell's area in square kilometres: 61.44 km squared, 3,774.87. |
| 85 | `World.GRASS` | `0` | Open ground: dry and buildable. |
| 88 | `World.FOREST` | `1` | Forest: dry and buildable, and standing timber (Resource.FOREST). |
| 91 | `World.FRESH` | `2` | Fresh water: a lake, or the founding river. |
| 94 | `World.SALT` | `3` | Salt water: the sea, and everything beyond the world's edge. |
| 97 | `World.SAND` | `4` | Beach: dry land within BEACH_BAND of the sea's level. |
| 111 | `World.OCTAVE_FALLOFF` | `0.55` | Each octave's amplitude against the one above it: 0.55, a little rougher than classic fractal noise's half (the mockup's terrain, scaled to plots). |
| 114 | `World.ELEV_COARSE_HI` | `16` | Elevation's coarse octaves run from 2^16 plots (1,966 km): continents and oceans. |
| 117 | `World.ELEV_COARSE_LO` | `9` | ...down to 2^9 plots (15 km): coasts and bays. |
| 120 | `World.ELEV_FINE_HI` | `8` | Elevation's fine octaves run from 2^8 plots (7.7 km)... |
| 123 | `World.ELEV_FINE_LO` | `4` | ...down to 2^4 plots (480 m): headlands, coves and islets. |
| 126 | `World.ELEV_COARSE_WEIGHT` | `0.85` | The coarse octaves' share of the elevation: 0.85, so the coast is the continents' and the fine octaves only fray it. |
| 129 | `World.ELEV_FINE_WEIGHT` | `0.15` | The fine octaves' share: the rest, 0.15. |
| 132 | `World.LAKE_HI` | `10` | The lake field's octaves run from 2^10 plots (30.7 km)... |
| 135 | `World.LAKE_LO` | `5` | ...down to 2^5 plots (960 m). |
| 138 | `World.FOREST_HI` | `8` | The forest field's octaves run from 2^8 plots (7.7 km)... |
| 141 | `World.FOREST_LO` | `5` | ...down to 2^5 plots (960 m). |
| 144 | `World.SEA_SHARE` | `0.71` | The share of the world that is sea: 71%, the Earth's. |
| 156 | `World.SEA_SAMPLES` | `4` | Elevation samples a world cell the sea's level is found from: 4, one at a hashed point in each quarter of the cell (541,696 in all). |
| 159 | `World.LAKE_SHARE` | `0.037` | Lakes' share of land: 3.7%, of the Earth's non-glaciated land (Verpoorter 2014). |
| 169 | `World.LAKE_THETA` | `0.7092` | The lake field's level from which ground is a lake: 0.7092, the 96.3rd percentile of the field over land (LAKE_SHARE) - measured 0.7088 to 0.7095 over five seeds, a million points each, off the lat... |
| 172 | `World.FOREST_SHARE` | `0.31` | Forest's share of land: 31% (FAO 2020). |
| 175 | `World.FOREST_THETA` | `0.5676` | The forest field's level from which land is forest: 0.5676, the 69th percentile of the field over land (FOREST_SHARE), measured 0.5672 to 0.5679 as LAKE_THETA was (the design's 0.595 makes 24% of l... |
| 178 | `World.BEACH_BAND` | `0.0015` | How far above the sea's level, in the elevation field, land is beach: 0.0015. |
| 192 | `World.SHELF_SHARE` | `0.0886` | The continental shelf's share of the sea: 8.86% (est., the shelf's share of the ocean's area, Harris et al. |
| 195 | `World.SHELF_BREAK_M` | `140` | The depth of the shelf's edge, in metres: 140 (est., the mean shelf break - spec-oil 2.7 and 6, to confirm); depthAt() is this at shelfTheta(). |
| 213 | `World.SITE_CELL_LAND_MIN` | `1` | The least land, in quarters of a cell's SEA_SAMPLES, a cell needs to be searched for a site: 1, 25%. |
| 216 | `World.SITE_CELL_LAND_MAX` | `3` | The most: 3, 75% - a coast, not open sea or an interior. |
| 219 | `World.SITE_STEP` | `17` | The spiral's step: 17 plots (510 m) between rings, and the ring's points about that far apart (six a ring per ring). |
| 222 | `World.SITE_RINGS` | `70` | Rings in a cell's spiral: 70, out to 1,173 plots (35 km), past the cell's own half-width of 1,024. |
| 225 | `World.SITE_DRY_PLOTS` | `20` | Test 1: the site and eight points this far round it are dry ground, in plots: 20 (600 m), room for a new city's centre - since 0.7.67 rings of 120 m blocks round the site's own, on the default worl... |
| 228 | `World.SITE_LAND_SHARE` | `0.6` | Test 2: of SITE_LAND_SAMPLES points within 5 km, at least this share are land: 60%. |
| 231 | `World.SITE_LAND_SAMPLES` | `48` | ...over 48 points, at 1.5, 3 and 5 km in turn round the compass. |
| 234 | `World.SITE_LAND_RADII` | `{ 50, 100, 167 }` | ...at these radii, in plots: 50, 100 and 167 (1.5, 3 and 5 km). |
| 237 | `World.SITE_SEA_NOT_WITHIN` | `45` | Test 3: no sea within this many plots in 16 directions: 45 (1.35 km). |
| 240 | `World.SITE_SEA_WITHIN` | `70` | ...but sea within this many in at least one: 70 (2.1 km) - a coast with room for a town. |
| 251 | `World.SITE_IRON_KM` | `2` | Test 4 (spec-land star), the conversion's alone since 0.7.99: an iron field's centre within this many km of the old world's fields (legacyFieldsInCell()) - the mockup put deposits near the site "so... |
| 262 | `World.RIVER_SEA_DIRECTIONS` | `32` | Directions the sea is looked for in, round the site: 32. |
| 265 | `World.RIVER_SEA_REACH` | `100` | ...out to this many plots, from SITE_SEA_NOT_WITHIN: 100 (3 km). |
| 268 | `World.RIVER_SEA_STEP` | `5` | ...in steps of this many plots: 5. |
| 271 | `World.RIVER_LAKE_TRIES` | `32` | Tries at a lake whose shore is dry: 32. |
| 274 | `World.RIVER_LAKE_FAN` | `1.7` | The lake's bearing from the site: opposite the sea, give or take half of this many radians: 1.7. |
| 277 | `World.RIVER_LAKE_NEAR` | `64` | The lake's centre this many plots from the site, at the least: 64 (1.9 km)... |
| 280 | `World.RIVER_LAKE_SPAN` | `26` | ...plus up to this many: 26, so 64 to 90 plots (1.9 to 2.7 km). |
| 283 | `World.LAKE_R_MIN` | `11` | The lake's radius in plots, at the least: 11 (330 m)... |
| 286 | `World.LAKE_R_SPAN` | `6` | ...plus up to this many: 6, so 11 to 17 plots. |
| 289 | `World.LAKE_SHORE` | `1.6` | The lake's shore is tested this many radii out: 1.6... |
| 292 | `World.LAKE_SHORE_RISE` | `0.003` | ...for ground this far above the sea's level: 0.003, two beaches. |
| 295 | `World.RIVER_PASS_MIN` | `8` | The river passes the site at least this many plots to one side: 8 (240 m)... |
| 298 | `World.RIVER_PASS_SPAN` | `12` | ...plus up to this many: 12, so 8 to 20 plots. |
| 301 | `World.RIVER_PASS_REACHED` | `10` | Within this many plots of its passing point the river turns for the sea: 10. |
| 304 | `World.RIVER_STEP` | `3` | Plots the river moves a step: 3 (90 m). |
| 307 | `World.RIVER_WANDER` | `0.75` | Its heading wanders by up to half this many radians a step: 0.75... |
| 310 | `World.RIVER_STEER` | `0.25` | ...and turns this share of the way to its target a step: 0.25. |
| 313 | `World.RIVER_MAX_STEPS` | `500` | The most steps it takes: 500 (45 km). |
| 316 | `World.RIVER_MOUTH` | `0.002` | It ends where the elevation is this far under the sea's level: 0.002. |
| 319 | `World.RIVER_HALF_WIDTH` | `1.0` | Its half-width at the lake, in plots: 1 (a 60 m river)... |
| 322 | `World.RIVER_WIDENS` | `0.95` | ...growing by this many plots to the sea: 0.95, so 1.95 at the mouth (117 m). |
| 347 | `World.FIELD_TAIL` | `1.5` | How heavy the tail of a field's size is: 1.5, so P(sites >= k) = k^-1.5 - most small, a few huge. |
| 350 | `World.LEGACY_MAX_SITES` | `512` | The most sites one of the old world's fields holds: 512 (the cap of the tail's draw, sites()). |
| 353 | `World.MEAN_SITES` | `meanSites()` | The mean sites one of the old world's fields: the sum of k^-1.5 for k = 1 to LEGACY_MAX_SITES, 2.524 - exact for the capped tail, since P(sites >= k) = k^-1.5. |
| 356 | `World.RICHNESS_MIN` | `0.6` | A cell's richness, its total against its count's mean, at the least: 0.6... |
| 359 | `World.RICHNESS_SPAN` | `0.8` | ...plus up to this: 0.8, so 0.6 to 1.4, with a mean of one. |
| 362 | `World.SEA_REDRAWS` | `8` | Times a field centred in the sea is drawn again: 8, since ore lies under land. |
| 365 | `World.POISSON_NORMAL_ABOVE` | `40` | Above this mean a cell's count is drawn as a rounded normal rather than by inversion: 40. |
| 368 | `World.FOREST_M3_PER_KM2` | `13_700` | Standing timber a square kilometre of forest, in cubic metres: 13,700 (FAO 2020: 557 billion m3 on 4.06 billion hectares). |
| 416 | `World.FIELD_SCALE` | `10` | How many of the old world's fields one field stands for: 10 (Jerus, 2026-10-08: "a tenth as many fields, each ten times bigger", the world's totals as they were). |
| 419 | `World.MAX_SITES` | `FIELD_SCALE * LEGACY_MAX_SITES` | The most sites one field holds: FIELD_SCALE of the old world's largest, 5,120. |
| 422 | `World.CLUSTER_FIELDS` | `10` | The fields a cluster holds on the mean: 10 (★W1-2, est.: Jerus's "very big clusters" - ten fields a cluster, as a field is ten of the old). |
| 425 | `World.POOL_CLUSTERS` | `10` | The clusters a pool of land holds on the mean, at the least: 10 (★W1-2, est.): enough that a pool's clusters lie where the draws put them, not one a pool on a lattice - a pool a quarter land still ... |
| 428 | `World.FIELD_INDEX_FROM` | `1<<16` | The first number a field is listed under in its cell: 65,536, past any old world's field's (a cell held at most 868 of them on the default world, iron's). |
| 431 | `World.POOLS_KEPT` | `256` | Pools kept, by resource and place: 256 (an iron pool is about 300 fields, some 15 KB). |
| 434 | `World.POOL_SALT` | `0x5EB0C1A5L` | The stream a pool is drawn from. |
| 441 | `World.WORLDS_KEPT` | `4` | How many worlds World.of() keeps: 4, the city's and a few the founding screen looked at (about 0.3 MB each). |
| 443 | `World.KEPT` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeEldestEntry(Ma...` |  |
| 520 | `World.F_ELEV` | `0x1111, F_ELEV_FINE = F_ELEV ^ 0x55, F_LAKE = 0x2222, F_FOREST = 0x3333` | The fields' keys: what makes one field's octaves another's. |
| 523 | `World.AMP` | `new double [ ELEV_COARSE_HI + 1 ]` | OCTAVE_FALLOFF to the power n, by repeated multiplication - the order fbm() has always summed in. |
| 539 | `World.NORM_COARSE` | `norm(ELEV_COARSE_HI, ELEV_COARSE_LO), NORM_FINE = norm(ELEV_FINE_HI, ELEV_FINE_LO), NOR...` |  |
| 929 | `World.SMOOTH` | `new double [ ELEV_COARSE_HI + 1 ][]` | The smoothstep at a plot's centre within an octave's lattice cell, for every wavelength 2^k: SMOOTH[k][f] for f = 0 .. |
| 948 | `World.SCRATCH` | `ThreadLocal.withInitial(TileScratch : : new)` |  |
| 1078 | `World.LineScratch.CORNERS` | `64` |  |
| 1094 | `World.LINE_SCRATCH` | `ThreadLocal.withInitial(LineScratch : : new)` |  |
| 1371 | `World.POOL_SIDE` | `new int [ Resource.values().length ]` | Each resource's pool side in cells, in Resource's order (poolCells()). |
| 1423 | `World.NO_POOL` | `new Pool(new long [ 0 ], new long [ 0 ], new int [ 0 ], new double [ 0 ], 0, 0)` |  |

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
| 69 | `YearBook.MONTHS_A_YEAR` | `12` |  |
| 70 | `YearBook.MONTHS_A_DECADE` | `120` |  |
| 101 | `YearBook.MONEY` | `"{the city's money}"` | Where a note names the city's money. |
| 279 | `YearBook.RULES` | `rules()` |  |
| 280 | `YearBook.PREFIXES` | `prefixRules()` |  |
| 529 | `YearBook.GDP_PARTS` | `{ "consumption", "investment", "government", "netExports" }` | GDP's four parts, as HistorySave names them (0.7.6): C, I, G and NX, in the order they stack. |
| 1117 | `YearBook.EPISODE_MIN_MONTHS` | `3` | A run shorter than this many months is noise, and is not named - or shaded on the chart. |
| 1120 | `YearBook.EPISODE_JOIN_MONTHS` | `6` | Two runs with fewer months of relief than this between them are one episode. |
| 1123 | `YearBook.DEPRESSION_MONTHS` | `24` | A recession this many months long, or longer, is called a depression. |
| 1135 | `YearBook.FINANCIAL_EQUITY` | `0` | The bank's equity under this, in thousands, is a financial crisis: the bank has failed. |
| 1138 | `YearBook.RECESSION_GROWTH` | `0` | Real output over a rolling year against the year before, as a fraction, under this is a recession: under zero, a fall. |
| 1141 | `YearBook.CURRENCY_MOVE` | `2` | The exchange rate past this multiple of itself a year before is a currency crisis: the currency halved. |
| 1144 | `YearBook.INFLATION_EPISODE` | `.25` | Prices rising faster than this a year is an inflation. |
| 1147 | `YearBook.DEFLATION_EPISODE` | `-.10` | Prices falling faster than this a year (a negative rate) is a deflation. |
| 1150 | `YearBook.EPIDEMIC_OUTBREAK` | `Health.OUTBREAK_FLOOR` | An epidemic is named while an outbreak runs - more of the workforce off sick than its care explains - not while a city short of care is as sick as it always is (A8). |
| 1153 | `YearBook.TREASURY_CASH` | `0` | The treasury's cash under this, in thousands, is a treasury crisis: it is overdrawn. |
| 1156 | `YearBook.SLUMP_UNEMPLOYMENT` | `.20` | More of the labour force out of work than this is a slump. |
| 1333 | `YearBook.CHRONIC_MONTHS` | `120` | An episode still running after this many months is chronic: City History lists it after the others that are running, and leads with it only when it runs alone (0.7.37). |

## sectors (93 constants)

### Agriculture.java ([map](map/Agriculture.md))

| line | constant | value | says |
|---:|---|---|---|
| 128 | `Agriculture.BAKED_KG_PER_TONNE` | `525` | Kilograms of bread and bakery goods a tonne of crops becomes. |
| 131 | `Agriculture.BAKED_KG_A_HEAD` | `5.0` | What one person eats of the city's own baking a month: 3.5kg of bread, 1.5kg of the rest. |
| 301 | `Agriculture.FIRST_FARM_UTILISATION` | `.5` | Half a farm's nameplate, a month, before the first one is sunk. |

### Automotive.java ([map](map/Automotive.md))

| line | constant | value | says |
|---:|---|---|---|
| 109 | `Automotive.MAX_SHARE_OF_LOCAL_SUPPLY` | `.25` | The most of the city's WHOLE fabrication output one new plant may want. |
| 119 | `Automotive.LUBRICANT_LITRES_A_VEHICLE` | `8` | Lubricants a vehicle built takes (0.7.83, batch O6; runs/spec-oil.md 2.5, est., to confirm): eight litres - its first fill of engine, gearbox and axle oils and the line's own - on the car plants' a... |

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
| 115 | `LuxuryRetail.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a shop's charged margin moves toward the fixed point its buyers strike in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 250 | `LuxuryRetail.MARGIN_STEPS` | `60` | Bisection steps for the margin's fixed point: 2.75 / 2^60 is far below a cent's grain on any landed cost. |

### Manufacturing.java ([map](map/Manufacturing.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `Manufacturing.LUBRICANT_LITRES_A_TONNE_OF_MACHINERY` | `10` | Lubricants a tonne of machinery made takes: ten litres (spec-oil 2.5, est.). |
| 87 | `Manufacturing.LUBRICANT_LITRES_A_TONNE_FABRICATED` | `2` | ...and a tonne of fabricated steel: two litres (spec-oil 2.5, est.). |

### Materials.java ([map](map/Materials.md))

| line | constant | value | says |
|---:|---|---|---|
| 33 | `Materials.FIRST_PLANT_UTILISATION` | `.5` | Half a plant's nameplate, a month, before the first one is sunk. |

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
| 185 | `Oil.LAND_LIFE` | `firstWornOut(WellKind.LAND), PLATFORM_LIFE = firstWornOut(WellKind.PLATFORM)` |  |
| 675 | `Oil.PLATFORM_MAX_DEPTH_M` | `150` | The deepest sea a platform's jacket stands in, in metres: 150, the fixed platform's limit (the research's 3.2; spec-oil 2.7). |
| 678 | `Oil.PIPE_LIFE_MONTHS` | `480` | A pipe's working life, in months: 480, 40 years (the research's 4.5, est.) - what the pipeline rule counts the field's months to at most. |
| 681 | `Oil.PIPE_PAYBACK` | `1.25` | The pipeline rule's margin: the freight a pipe saves over the field's life must repay its cost 1.25 times (the research's 4.5; spec-oil 2.11). |
| 684 | `Oil.SHUTTLE_TANKERS` | `"Shuttle tankers"` | The shuttle tankers' name on the Oil sector's input line. |
| 687 | `Oil.PLATFORM_KEY` | `"platforms."` | The extras' key a platform is saved under: platforms.<place>.<cell>.<index>.<month opened>, the wells in its slots the value. |
| 690 | `Oil.PIPELINE_KEY` | `"pipelines."` | ...and a pipeline: pipelines.<place>.<cell>.<index>.<month ordered>, its kilometres the value. |

### Rail.java ([map](map/Rail.md))

| line | constant | value | says |
|---:|---|---|---|
| 134 | `Rail.RAIL_FLOOR` | `.30` | The cheapest the railway will ever quote, as a share of the lorry rate. |
| 137 | `Rail.OPENING_QUOTE` | `.60` | What a city with no railway assumes the first line could charge. |
| 158 | `Rail.MIN_LINE_UTILISATION` | `.60` | How much of a line's nameplate has to be freight nobody is carrying before it is worth laying. |
| 177 | `Rail.TONNES_PER_SET` | `2500` | Tonnes a month one wagon set can haul. |
| 180 | `Rail.SET_LIFE_MONTHS` | `240` | How long a wagon set lasts before it is scrap. |
| 211 | `Rail.TARGET_RETURN` | `.012` | What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity. |
| 214 | `Rail.MAX_SCARCITY_MULTIPLE` | `1.6` | How far a network that cannot keep up can push the quote above cost-plus. |
| 217 | `Rail.REPRICE_SPEED` | `.25` | How fast the quote walks to where it should be. |
| 253 | `Rail.WORLD_FUEL_PER_TONNE` | `.03` | What a tonne of haulage burns, IN THE WORLD'S MONEY. |
| 265 | `Rail.FUEL_LITRES_PER_TONNE` | `18` | Litres of fuel a tonne hauled burns: eighteen. |

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

### RefineryFlow.java ([map](map/RefineryFlow.md))

| line | constant | value | says |
|---:|---|---|---|
| 76 | `RefineryFlow.HYDROGEN_MADE` | `1400` | A barrel's hydrogen a reformer makes, in standard cubic feet, the middle of the research's 1.3 range [R6]. |
| 79 | `RefineryFlow.HYDROGEN_USED` | `1850` | ...and a barrel of gas oil a hydrocracker uses [R7]. |
| 82 | `RefineryFlow.RESIDUE_LITRES_PER_TONNE` | `1010` | Litres in a tonne of residue: fuel oil's [P35] (Good.FUEL_OIL), what the coke's and the bitumen's weights are struck at. |
| 85 | `RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY` | `30.44 * 158.987` | A litre a month of a barrel a day: 30.44 days of 158.987 L - how the units' sizes in barrels a day become their feed in litres (spec-oil 2.3). |
| 190 | `RefineryFlow.FLOW_ORDER` | `{ Stream.GAS_OIL, Stream.RESIDUE, Stream.HEAVY_NAPHTHA, Stream.CRACKED_GAS }` | The streams in the order they are solved (3 in the header). |

### Refining.java ([map](map/Refining.md))

| line | constant | value | says |
|---:|---|---|---|
| 107 | `Refining.CRUDE_LITRES_PER_TONNE` | `1165` | Litres in a tonne of crude [P35]: the slate is struck in litres, the crude bought in tonnes. |
| 114 | `Refining.MEDIUM_VOL_PCT` | `{ 1.5, 6.0, 12.0, 12.0, 22.0, 24.0, 22.5 }` | The straight-run cuts of a MEDIUM crude, vol% (the research's 1.1, the blend): gas, light naphtha, heavy naphtha, kerosene, diesel, gas oil and residue, in that order (the CUT_ indexes); normalised... |
| 117 | `Refining.MEDIUM_CUTS` | `normalised(MEDIUM_VOL_PCT)` | ...each a share of the barrel, MEDIUM_VOL_PCT over its sum (the column normalised, as the prototype does). |
| 120 | `Refining.CUT_GAS` | `0, CUT_LIGHT_NAPHTHA = 1, CUT_HEAVY_NAPHTHA = 2, CUT_KEROSENE = 3, CUT_DIESEL = 4, CUT_...` | The cuts' places in MEDIUM_CUTS. |
| 124 | `Refining.LIGHT_VOL_PCT` | `{ 4.1, 8.4, 15.9, 13.9, 25.6, 21.3, 11.4 }` | The straight-run cuts of a LIGHT crude, vol%, in the CUT_ order: Brent, 38 degrees API [R1] (the research's 1.1; they sum to 100.6). |
| 127 | `Refining.HEAVY_VOL_PCT` | `{ 0.3, 5.1, 10.2, 13.8, 9.4, 24.3, 36.9 }` | ...of a HEAVY crude: Maya, 21.5 degrees API [R2], its 15.3 of naphtha split one to two light to heavy as the blend's is (spec-oil 2.2). |
| 130 | `Refining.CUTS` | `{ normalised(LIGHT_VOL_PCT), MEDIUM_CUTS, normalised(HEAVY_VOL_PCT) }` | Each grade's cuts, in Deposit.Grade's order, each column normalised to one (0.7.79): LIGHT_VOL_PCT's, MEDIUM_CUTS itself, HEAVY_VOL_PCT's. |
| 133 | `Refining.MEDIUM_MIX` | `mixOf(Deposit.Grade.MEDIUM)` | A run all of MEDIUM crude, the research's blend: the mix of imports, of a refinery that took no crude, and of every crude unit until 0.7.79. |
| 143 | `Refining.RESIDUE_PER_DIESEL` | `3` | Litres of residue a litre of diesel cuts into fuel oil (Q5): three to one, so four litres of fuel oil. |
| 146 | `Refining.PRODUCTS` | `{ Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, Good.LUBRI...` | The products a crude unit makes, in the order a screen lists them: the two the city burns first. |
| 150 | `Refining.BOUGHT_HERE` | `{ Good.PETROL, Good.DIESEL }` | The products something in the city buys (spec-oil 2.5, O1): the drivers' petrol and the railway's diesel - what the gate is struck on. |
| 688 | `Refining.CRUDE_COVER_MONTHS` | `1` | Months of the crude units' run kept on hand in a Tank Farm's room (★ spec-oil 2.8): one, a month's run. |
| 904 | `Refining.PACKAGE_MOST_UNITS` | `12` | The most conversion units a crude unit is weighed with: the prototype's twelve rounds (spread.py, its package) - more than one of each of the seven kinds. |
| 907 | `Refining.KIND_NAMES` |  | Each kind's name, in Kind's order: what the idle months are saved by. |

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
| 853 | `Retail.PUMP_MARGIN` | `.12` | What a station adds to the wholesale price it paid a litre, before the sales tax: twelve per cent (runs/research-pump.md 7: Canada's 10.4 c/L in 2025, about 10% of the wholesale ex tax [6]; the US ... |
| 861 | `Retail.QUEUE_MARGIN` | `.18` | ...and on the litres past what the stations can sell: eighteen per cent, the top of the research's range - a rural or post-spike forecourt's (runs/research-pump.md 2, 7). |
| 926 | `Retail.FuelSale.NONE` | `new FuelSale(0, 0, 0, 0, 0)` |  |
| 1219 | `Retail.SUPPLIER_CREDIT_KEY` | `"supplierCredit."` | The prefix the suppliers' credit is saved under among the extras. |

### SpreadPlanner.java ([map](map/SpreadPlanner.md))

| line | constant | value | says |
|---:|---|---|---|
| 68 | `SpreadPlanner.FEED_GATE` | `Rail.MIN_LINE_UTILISATION` | The least share of one building's feed the spare stream must cover for it to be weighed at all: the railway's MIN_LINE_UTILISATION, nothing built to stand idle (spec-oil 2.4). |
| 71 | `SpreadPlanner.IDLE_MONTHS` | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | Months a kind must stand idle running before its sector may sell it back (spec-oil 2.4): six, RETIREMENT_LOSS_MONTHS - as long as the spare-capacity rule waits on losses. |
| 74 | `SpreadPlanner.IDLE_KEY` | `"idleMonths."` | The prefix of each kind's idle months in a client's saved extras. |
| 232 | `SpreadPlanner.ON_ITS_BORROWING` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash) { return ...` | THE RULE IN FORCE: on what the order would borrow, its cost less the sector's cash - Game.consider()'s own test, which asks nothing of an order the till pays. |
| 239 | `SpreadPlanner.ON_ITS_WHOLE_COST` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash) { return ...` | THE STRICTER RULE, NOT IN FORCE (spec-materials.md, Jerus's item A): on the whole cost, whoever pays - for the counterfactual (LongPlaytest's -Dplaytest.moneyGate=whole, the probes). |

## interface (743 constants)

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
| 1056 | `BankScreen.LADDER_INFO` | `"From the price of money to what each borrower pays.Every step up is a cost " + "the ba...` | THE LADDER's (i): the landing's note (the spec's L10), with what the parts are. |
| 1443 | `BankScreen.NOTHING` | `.0005` | A step under half a dollar is nothing: it is left off the walk and named under it. |
| 1551 | `BankScreen.PROVISIONS_INFO` | `"A provision is money set aside for loans expected to go bad: a year's " + "expected lo...` | The provisions' note (the spec's T, Profit 790), behind the step's tooltip and the statement's line. |
| 1588 | `BankScreen.RATIOS_INFO` | `"The margin is what it charges less what it pays for its money, on everything lent." + ...` | The ratios' note (Profit 874), behind MARGIN's and COSTS' (i). |
| 2054 | `BankScreen.OWNS` | `List.of(new SheetPart(Bank.Sheet.BUSINESS_LOANS, "Loans to the businesses", Palette.BUS...` | What it owns, in the bars' order (the spec's section 3): the businesses' loans first, its reserves last; the allowance is the net tick. |
| 2066 | `BankScreen.OWES` | `List.of(new SheetPart(Bank.Sheet.DEPOSIT_FUNDING, "Lent past its own cash, on its depos...` | ...and what it owes, then its owners' (equity last). |
| 2319 | `BankScreen.BESIDE_INFO` | `"Counted for what it can lend and not held on its sheet in full: the families' " + "sav...` | BESIDE THE SHEET's (i) (Balance sheet 1161). |
| 2613 | `BankScreen.BOOK_INFO` | `"Everything it has lent, by who owes it: the businesses' loans and the bonds of theirs ...` | Who-owes-it note (Lending 1225), behind THE BOOK's (i). |
| 3253 | `BankScreen.LEND_AGAINST_INFO` | `"Savings reach the bank wherever its branches are - online - so it lends " + "against e...` | What it can lend against (Funding 1681), behind the banked bar's (i). |
| 3707 | `BankScreen.STAKE_INFO` | `"A resolution makes every share the city's, in its fund's rescue book, which its rule "...` | The city's stake's note (Capital 2002). |
| 3802 | `BankScreen.MOVED_INFO` | `"Equity moves by what it earned, what it was given and what it paid out, and by " + "no...` | The equity's note (Capital 1990): Jerus's rule on a plug. |
| 3856 | `BankScreen.RESCUES_INFO` | `"When a bank loses more than it owns, the city resolves it: the old owners lose " + "ev...` | The rescues' note (Capital 2027). |
| 4077 | `BankScreen.RESCUE_INFO` | `"Its owners lose everything: the households' shares and the world's pass to the city " ...` | The rescue's note (Rescue 2119). |

### BuildScreen.java ([map](map/BuildScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 187 | `BuildScreen.BUILD_HOME` | `BuildAdvice.OVERVIEW` | Where Build opens (BUILD_HOME), and which category the player was last looking at (buildCategory). |
| 322 | `BuildScreen.CITY_DOT` | `Palette.MONEY` | The colour of "only the city builds these": the money blue - the city's own account. |
| 325 | `BuildScreen.INVESTOR_DOT` | `Palette.BUSINESS` | The colour of "investors build these too": the business violet. |
| 548 | `BuildScreen.LAND_FREE_CELL` | `236` | LAND FREE's width with its shortcut under it: the cell's own 190 and room for the shortcut's words on one line. |
| 800 | `BuildScreen.PAVE_HEAD` | `"PAVE TO A PAVED ROAD"` | The paving's heading on the Gravel Road card. |
| 803 | `BuildScreen.PAVE_TAG` | `"paving beats a new Paved Road"` | ...its tag, when paving beats a new Paved Road over its life a trip. |
| 806 | `BuildScreen.PAVE_INFO` | `"Paving lays a Paved Road on a gravel road's ground: a Paved Road's price, less the" + ...` | ...and the (i) on its two figures: what it costs and why it may win. |
| 916 | `BuildScreen.ROAD_TAG` | `"cheapest per trip over its life"` | A road card's first tag (0.7.70): the cheapest a trip off the road over its life, its ground in it - BuildCard.ROAD_PER, shortened. |
| 919 | `BuildScreen.ROAD_OFF` | `"trip off the road"` | ...and what its land bar is per: a trip it takes off the road (BuildAdvice.unit()). |
| 1766 | `BuildScreen.JOB_RING` | `58` | A ring's size on the Overview's tiles (its stroke is 6 px). |
| 2387 | `BuildScreen.AUTO_CHIP` | `"Automatic building on ›"` | Build's heading chip on every page but the Overview while it is on: the way back to its cards. |
| 2390 | `BuildScreen.AUTO_INFO` | `"Turned on, it orders every month what this page advises for the city's works - " + "po...` | The section's (i). |
| 2412 | `BuildScreen.AUTO_DIAL` | `(1389 - Palette.RAIL - 36 - 20) / 3.0 - 24` | A dial's width in its card at the 1,389 window: a third of the page less the card's padding. |
| 2652 | `BuildScreen.NEED_CARD` | `300` | A card's width, on every Build page since 0.7.25 (a city category's only, in 0.7.24). |
| 3910 | `BuildScreen.RECEIPTS` | `5` | How many purchases the receipt keeps. |

### ConstructionScreen.java ([map](map/ConstructionScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 50 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 202 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 216 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 552 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 557 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |
| 899 | `ConstructionScreen.PAVING_WORDS` | `"%,d paving · no stop"` | The Paved Road site's words while it paves gravel roads (0.7.70), where Cancel would be: how many, and that it runs on. |
| 902 | `ConstructionScreen.PAVING_TIP` | `"Some of these Paved Roads pave gravel roads, which carry their traffic until each" + "...` | ...and why. |

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
| 239 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 242 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height... |
| 245 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentSc... |
| 382 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "The ladder i...` | The tab's (i): what the hub is, in a breath. |
| 388 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balance and the...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 652 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " + "everythi...` | What resolving a failed bank does (the spec's T41). |
| 893 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, " + "becaus...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 903 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " + "changes."` | The ladder with nothing on it (the spec's T11). |
| 907 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "refinancing i...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 912 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 915 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1152 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month notes" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1155 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't cover the...` | Each setting's one line (the spec's T35). |
| 1163 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and what the c...` | ...and each in full, on its chip (the old block's sentences). |
| 1174 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's " + "cash,...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1292 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1295 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays frozen until yo...` | Each setting's one line (the spec's T40). |
| 1301 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose " + "every...` | The rescue's (i): both settings in full (the old block's T40). |
| 1518 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to " + "build...` | THE BALANCE's (i): the spec's T7. |
| 1685 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 1688 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is " + "repa...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 1734 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the coupon is an expense; the principal is a balance-sheet movement." + "Both lea...` | LAST MONTH's (i): the spec's T14. |
| 1766 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city " + "earns...` | FOREIGN's (i): the spec's T16. |
| 1797 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none of " + "it ...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 1845 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the " + "exchan...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 1867 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports " + "they w...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 1985 | `FinancesScreen.CURVE_MONTHS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The curve's maturities, in months. |
| 1987 | `FinancesScreen.CURVE_NAMES` | `{ "3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "40y", "50y" }` | ...and how they are written under it. |
| 1990 | `FinancesScreen.CURVE_INFO` | `"The note is the floor - the dial, or what the bank's money costs it, whichever " + "is...` | THE CURVE's (i): the spec's T24, and the world's curve. |
| 2025 | `FinancesScreen.CurveChart.W` | `720, H = 190, LEFT = 44, RIGHT = 16, TOP = 26, FOOT = 22` | Its size and its margins, in pixels: a card's picture, at a fixed size. |
| 2157 | `FinancesScreen.NOTE_INFO` | `"No coupon at all - the lender's return was the discount, taken out of the proceeds " +...` | A note's (i): the spec's T28. |
| 2161 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, " + "the c...` | A price against face, in words (the spec's T33, D16). |
| 2321 | `FinancesScreen.TERMS_INFO` | `"Each column is the rate this paper would cost at that term today - for the " + "amount...` | The terms' (i): the spec's T55. |
| 2328 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " + "smalles...` | The ask's (i): the spec's T56. |
| 2332 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here, with the ladder it would build.Nothing "...` | The quote's empty state: the spec's T57. |
| 2336 | `FinancesScreen.PROCEEDS_INFO` | `"Paper is sold in lots and the face is grossed up for the discount, so the " + "proceed...` | Why the proceeds are not the ask: the spec's T58. |
| 2341 | `FinancesScreen.BUYERS_INFO` | `"The households first, when it pays them more than the bank does: up to %s of " + "an i...` | Who buys it: the spec's T61, its first half. |
| 2348 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "devaluation...` | Where the dollars go: the spec's T62. |
| 2453 | `FinancesScreen.ASK_TYPED_INFO` | `"Type an amount in dollars: digits, with or without commas, a decimal " + "point if you...` | HOW MUCH's (i): what the box takes - the case rules - and what the buttons do. |
| 2461 | `FinancesScreen.ASK_WORDS` | `java.util.regex.Pattern.compile("(?:[A-Za-z]{0,3}\\$)?\\s*((?:\\d{1,3}(?:,\\d{3})+\|\\d+...` | A typed amount: an optional mark, digits (grouped by commas or not), an optional fraction, an optional unit. |
| 2793 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate and " + "eno...` | The default page's (i). |
| 2934 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's - plus " ...` | M2's (i): the spec's T69. |
| 3024 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no more " + "...` | No bond outstanding: the spec's T70. |
| 3029 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has not " + "t...` | The prices' (i): the spec's T71. |
| 3034 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when it " + "me...` | The order books' (i): the spec's T74. |
| 3042 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 3044 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY_DARK }` | ...and their colours on it. |
| 3201 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since " + "whate...` | A bond's book's (i): the spec's T75, T77 and T78. |

### FoundingScreen.java ([map](map/FoundingScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 76 | `FoundingScreen.SCREEN` | `"showFoundingScreen"` | This screen's name for clearMenu(), the key filter and isGameMenu(). |
| 123 | `FoundingScreen.PANEL` | `760` | The panel's width, as the mockups draw it. |
| 126 | `FoundingScreen.PANEL_PAD` | `40` | The panel's padding, left and right: what the cards and fields share is the rest. |
| 129 | `FoundingScreen.SEED_FIELD` | `200` | The World's field: room for a seed of nineteen digits and a sign in the figures' face at 14 px (0.7.56). |

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
| 414 | `FundScreen.SHARES` | `"COMPANY SHARES", RESCUE = "THE RESCUE BOOK", BONDS = "BONDS"` | The holdings' groups, in the page's order. |
| 968 | `FundScreen.HERO_INFO` | `"Everything the fund holds at the marks every other holder uses, and its cash.The " + "...` | THE CITY'S FUND's (i). |
| 1081 | `FundScreen.COL_NAME` | `250, COL_UNITS = 120, COL_PRICE = 110, COL_VALUE = 110, COL_AVG = 110, COL_PNL = 160` | HOLDINGS' columns: the name, units, price, worth, average cost, unrealized, and the share of the fund's bar. |
| 1084 | `FundScreen.HOLDINGS_INFO` | `"Every lot the fund holds, at its mark, with what it cost by the average-cost " + "meth...` | HOLDINGS' (i). |
| 1277 | `FundScreen.POP_MILLIS` | `220` | How long a payment's pop takes each way. |
| 1304 | `FundScreen.KINDS_INFO` | `"Each kind of holding since the fund began: what the city put into it(bought on " + "th...` | SINCE IT BEGAN, BY KIND's (i). |
| 1399 | `FundScreen.FILTERS` | `{ "All", "Shares", "Bonds", "Held" }` | Search's chips. |
| 1402 | `FundScreen.SEARCH_INFO` | `"Every company listed on the exchange, every business's bond still outstanding, and " +...` | Search's (i): the spec's D1. |
| 1810 | `FundScreen.TICKET_INFO` | `"An order goes on the book at the next month's step, after every other participant " + ...` | THE ORDER TICKET's (i). |
| 2056 | `FundScreen.GROUPS` | `{ "All", FundView.TRADES, FundView.INCOME, FundView.MONEY, FundView.EVENTS }` | Activity's chips: every row, or one group of them (FundView.groupOf()). |
| 2200 | `FundScreen.WITHDRAWAL_LADDER` | `520` | The withdrawal's ladder: the card runs the page's width, the dial at the left and what it would do beside it. |
| 2217 | `FundScreen.WITHDRAWAL_CAVEAT` | `"next month's on the fund as it stands - its prices and what the book takes will move it"` | The withdrawal's caveat, under what it would do. |
| 2219 | `FundScreen.WITHDRAWAL_CAVEAT_INFO` | `"Next month's withdrawal is struck on what the fund is worth at the top of " + "next mo...` | ...and the sentence behind it, its (i). |
| 2324 | `FundScreen.RESCUE_BOOK_INFO` | `"What the city holds from rescuing its bank: the shares it took when it " + "resolved i...` | The rescue book's (i). |

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
| 730 | `GovernmentScreen.SURPLUS_INFO` | `"It took in more than it spent.A surplus pays down debt or buys the next " + "thing wit...` | P1's rest: what a surplus is for. |
| 734 | `GovernmentScreen.DEFICIT_INFO` | `"It spent more than it took in.That gap is borrowed, and next month's " + "interest is ...` | ...and what a deficit costs. |
| 842 | `GovernmentScreen.BRIDGE_INFO` | `"EARNED is the header's figure: taxes and fees less the running programmes " + "- inter...` | The bridge card's (i): P2, rewritten, and P3. |
| 855 | `GovernmentScreen.DIALS_INFO` | `"EARNED is read at today's tax rates; the budget was struck at the month's." + "A dial ...` | The dials' step's tooltip (the spec's B7). |
| 860 | `GovernmentScreen.RESIDUAL_INFO` | `"What the named steps do not explain: timing between the books and the money, " + "and ...` | The "not accounted for" step's tooltip. |
| 1038 | `GovernmentScreen.ARREARS_INFO` | `"What the ceiling cut: owed, with no interest on it, and paid down out of the " + "firs...` | P6: the arrears. |
| 1079 | `GovernmentScreen.ECONOMY_INFO` | `"A year of revenue against a year of output is the only honest way to " + "compare a bu...` | P8: why a month against a year. |
| 1086 | `GovernmentScreen.NO_YEAR` | `"There is no output recorded yet, so nothing here can be put in " + "proportion.A month...` | P7: no month of output yet. |
| 1244 | `GovernmentScreen.LIST_COLUMNS` | `{ 100, 60, 70 }` | The column heads' widths: a month, of the budget, of GDP. |
| 1247 | `GovernmentScreen.LIST_DOOR` | `96` | ...and the door's at the end of a row: "set it ›", "Land office ›". |
| 1509 | `GovernmentScreen.FUND_INFO` | `String.format("The withdrawal dial's share of everything the city's fund holds, a " + "...` | P20: the fund's transfer. |
| 1828 | `GovernmentScreen.PRINCIPAL_INFO` | `"Not on the list above, and deliberately: repaying principal is not " + "spending.The m...` | P27: principal is not spending. |
| 1833 | `GovernmentScreen.TERM_INFO` | `"A term loan pays its coupon every month and the whole face at the end, so the " + "mon...` | P28: term loans. |
| 1838 | `GovernmentScreen.SERIAL_INFO` | `"Serial bonds amortise - a slice of principal falls due every anniversary, so " + "both...` | P29: serial bonds. |
| 1842 | `GovernmentScreen.NOTES_INFO` | `"Notes carry no interest at all - the lender's return was the discount, taken " + "out ...` | P31: notes. |
| 1847 | `GovernmentScreen.COUPON_INFO` | `"The interest on the budget is struck from what the city actually paid over a " + "comp...` | P30: the coupon and the budget disagreeing. |
| 1943 | `GovernmentScreen.BOTH_DEFICIT_INFO` | `"Both run at a deficit on purpose.What the deficit buys is on the " + "Services tab, in...` | P32, as it is true: both cost the city more than they charge. |
| 1947 | `GovernmentScreen.NOT_BOTH_INFO` | `"Care and the schools are meant to run at a deficit: what it buys is on the " + "Servic...` | ...and when one does not (B16: fees with no bill). |
| 1986 | `GovernmentScreen.PENSIONS_INFO` | `"It is pay-as-you-go: this month's workers pay this month's pensioners, so " + "the gap...` | P33: pay-as-you-go. |
| 2069 | `GovernmentScreen.LAYERS_INFO` | `"What GDP is made of over the years, as City History draws it in layers: " + "consumpti...` | The layers' caption's (i). |
| 2146 | `GovernmentScreen.STOCK_INFO` | `"Stock built up is output that has been made and not yet sold, so it counts " + "the mo...` | P34: stock built up. |
| 2156 | `GovernmentScreen.GOVERNMENT_INFO` | `"Services with no market price, valued at cost: the utilities' staff, " + "care, the sc...` | P35, rewritten (B5): what G is, as the model counts it. |
| 2194 | `GovernmentScreen.MOM_INFO` | `"Month-on-month annualised is one month multiplied up - compounded, so a city " + "grow...` | P37, on the strip's month on month. |
| 2199 | `GovernmentScreen.YOY_INFO` | `"This month's output against the same month a year ago, in today's money - " + "nominal...` | P36's point, on the strip's year on year: nominal, where the header's tile is real. |

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
| 2251 | `HistoryScreen.MOVE_MILLIS` | `600` | How long a figure takes to slide to a month's new reading, in milliseconds - the range bar's dot; the figure counts on over SectorScreen's own time. |
| 2430 | `HistoryScreen.AXES_INFO` | `"Lines measured in the same thing are drawn against each other on a real axis; " + "two...` | PICK WHAT TO DRAW's (i): how the lines share axes. |
| 2632 | `HistoryScreen.HARD_TIMES_SHOWN` | `5` | At most this many hard times are listed in view; the rest are counted, and in the details. |
| 2635 | `HistoryScreen.DECISIONS_SHOWN` | `8` | At most this many decisions are listed in view. |
| 2638 | `HistoryScreen.KINDS` | `{ "recession", "depression", "slump", "epidemic", "financial", "currency", "inflation",...` | The kinds of episode, in the order the details list them. |
| 2641 | `HistoryScreen.KIND_NAMES` | `{ "Recessions", "Depressions", "Slumps", "Epidemics", "Financial crises", "Currency cri...` | ...and what the details call each, in that order. |
| 2951 | `HistoryScreen.AT_END` | `1e-6` | How near an end of its band a price must be to be AT it - a rounding hair of the band (the market strikes an end exactly). |
| 2954 | `HistoryScreen.BAND_ROOM` | `1.25` | The scale a good's row is drawn on: the band from 0 to 1, and room past its ceiling for the month's trade. |
| 2957 | `HistoryScreen.GOODS_INFO` | `"What a unit costs here this month, against what the world pays for one and " + "what i...` | The section's (i). |

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
| 370 | `Icons.WELL` | `"M3 21h18 M8 21l3-9 3 9 M2 9l14-4 3 3-14 4z M16 5v3 M5 12v4"` | A land well, its beam nodding over the pad: the pictogram's land wells. |
| 373 | `Icons.PLATFORM` | `"M2 20c2 0 2-1.5 4-1.5S8 20 10 20s2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5" + " M6 18V10h12v...` | A platform on its legs over the waves: the pictogram's platform wells. |
| 377 | `Icons.TANKER` | `"M2 15l2 4h16l2-4z M5 15v-3h9v3 M16 15V8h3v7"` | A tanker, low in the water: imported crude. |
| 380 | `Icons.VESSEL` | `"M8 7a4 4 0 0 1 8 0v10a4 4 0 0 1 -8 0z M8 9h8 M8 15h8"` | A process vessel, a column with its trays: a conversion unit's box. |
| 383 | `Icons.TANK` | `"M4 9l8-4 8 4 M4 9v11h16V9 M4 14h16"` | A storage tank, its roof a shallow cone: what goes into the refiners' tanks, and a Tank Farm's crude. |
| 386 | `Icons.FLAME` | `"M12 3c3 4 5 6.5 5 10a5 5 0 0 1 -10 0c0-2 1-3.5 2.5-5.5 2 1.5 3 2.5 3 -1-3 -.5-5.5 0-8z"` | A flame: the furnaces, where residue no diesel can cut is burned. |
| 389 | `Icons.EXCHANGE` | `"M8 3L4 7l4 4 M4 7h16 M16 21l4-4-4-4 M20 17H4"` | Two arrows passing, one each way (Lucide's arrow-left-right): money changed from one currency to the other - the Trade tab's exchange (0.7.35). |
| 511 | `Icons.DICE` | `"M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2z" + " M8 8h...` | A die showing five (Lucide "dice-5"): the founding screen's roll of a new world (0.7.56). |
| 515 | `Icons.MAP` | `"M3 6l6-3 6 3 6-3v15l-6 3-6-3-6 3z M9 3v15 M15 6v15"` | A folded map (Lucide "map", its earlier three-panel form): the land office's map, expanded over the window, and Build's shortcut, "Buy the best: North 3 · ..." (0.7.61; "Buy the best land" until 0.... |
| 518 | `Icons.EXPAND` | `"M15 3h6v6 M9 21H3v-6 M21 3l-7 7 M3 21l7-7"` | Two arrows out to the corners (Lucide "maximize-2"): the land office's Expand (0.7.61). |
| 521 | `Icons.SETTINGS` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2.34 0 0 1 ...` | A gear. |

### InfrastructureScreen.java ([map](map/InfrastructureScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 100 | `InfrastructureScreen.INFRA_PAGES` | `{ "Roads", "Transit", "The railway", "Freight" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 104 | `InfrastructureScreen.INFRA_ICONS` | `{ Icons.ROADS, Icons.BUS, Icons.RAIL, Icons.LORRY }` | Each page's icon on its chip (0.7.29): the road, the bus, the train and the lorry. |
| 115 | `InfrastructureScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). |
| 118 | `InfrastructureScreen.CURVE_MAX` | `2.0` | The flow curve's x scale: what the road serves, from nothing to this (0.7.41; its use, to 260%, until then), fixed so the dot's motion reads month to month; the floor ends at MIN_THROUGHPUT / FREE_... |
| 278 | `InfrastructureScreen.UNKNOWN_YET` | `"known after a month: the save predates 0.7.29"` | What a dash means, where one is shown. |
| 420 | `InfrastructureScreen.FLOW_INFO` | `String.format("Every business in the city multiplies its output by its " + "flow, and s...` | The flow's (i): the old page's paragraph under its big figure, said in served since 0.7.41. |
| 511 | `InfrastructureScreen.WALK_INFO` | `"Every building makes trips - its staff to and from work, its goods and its " + "bulk i...` | The walk's (i). |
| 518 | `InfrastructureScreen.CARS_INFO` | `String.format("A fully motorised city asks %.1f times the commuter road a city where " ...` | The car row's (i): the old page's car paragraph (P4). |
| 526 | `InfrastructureScreen.NO_CARS_INFO` | `"Nobody in this city owns one yet, so a commuter costs the road exactly " + "one trip a...` | ...and with nobody driving (P3). |
| 531 | `InfrastructureScreen.COSTS_INFO` | `"\"Costs\" is what one trip of this kind asks of the street once it is on " + "it: a lo...` | The stream cards' "costs" (P2, rewritten: a commuter is not always one). |
| 650 | `InfrastructureScreen.FlowCurve.W` | `270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5` | The plot's size, and the room for the axes' figures at its left and under it. |
| 842 | `InfrastructureScreen.NO_TRANSIT_INFO` | `"This city has built no transit at all.A Bus Network is the cheap rung " + "and a Metro...` | The no-transit card's (i) (P6). |
| 848 | `InfrastructureScreen.CEILINGS_INFO` | `"A city that builds a metro and no streets gets a metro nobody can reach, " + "and no c...` | The ceilings' (i) (P7). |
| 939 | `InfrastructureScreen.RIDERS_INFO` | `"A household holds one car at most, so a couple's second earner and four of five " + "f...` | The riders' caption's (i) (0.7.49). |
| 1038 | `InfrastructureScreen.FARE_LADDER` | `520` | The fare's ladder, and its effects under it: the card is one of two across the page. |
| 1075 | `InfrastructureScreen.FARE_INFO` | `"A FARE IS A PRICE TO CAR OWNERS AND A CHARGE TO EVERYONE ELSE.Commuters with no car " ...` | The fare's (i) (P9). |
| 1081 | `InfrastructureScreen.PREVIEW_INFO` | `"The riders line is this month's ceilings at the new fare, which is the " + "honest hal...` | The preview's (i) (P10). |
| 1185 | `InfrastructureScreen.QUOTE_INFO` | `"Of what a lorry would charge for the same tonne.The railway is a " + "private business...` | The quote's (i) (P12). |
| 1192 | `InfrastructureScreen.NO_TRACK_INFO` | `"Nobody has laid a line in this city, so every tonne that leaves " + "it leaves by lorr...` | No track (P11). |
| 1197 | `InfrastructureScreen.LINE_RULE_INFO` | `String.format("It will not lay a line it cannot fill to %.0f%%, which is why a town doe...` | The line rule (P17). |
| 1203 | `InfrastructureScreen.BILL_INFO` | `"The month's lorry bill is what the tonnes that crossed the city's boundary " + "would ...` | The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). |
| 1300 | `InfrastructureScreen.RELIEF_INFO` | `String.format("A tonne that leaves by train does not drive across the city to leave by ...` | The relief's (i) (P15). |
| 1335 | `InfrastructureScreen.BIGGER_INFO` | `"It bills under three quarters of what the rule allows.It is allowed to " + "charge for...` | The too-big railway (P16). |
| 1427 | `InfrastructureScreen.BAND_INFO` | `"Every traded good's price has a cost of MOVING it inside the gap between " + "what the...` | The bars' (i) (P18 and P20, for bars rather than a grid). |
| 1566 | `InfrastructureScreen.LORRY_INFO` | `String.format("A vehicle moves about %,.0f tonnes a month and lasts %.0f years.The " + ...` | The lorries' (i) (P23). |

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
| 280 | `LandScreen.WHO_PAYS` | `"Investors build only on ground the city owns, and pay the city for each " + "plot they...` | The page's (i): who pays for the ground, and who does not. |
| 289 | `LandScreen.PAY_TIPS` | `{ "Pay by converting cash", "Pay from the vault" }` | The toggle's tooltips: its 0.7.6 names, which the chips shortened. |
| 469 | `LandScreen.BEST_HEAD` | `"THE BEST OFFERS · cheapest dry ground first, from every side"` | The section's head over THE GROUND: the best N, from every side. |
| 608 | `LandScreen.EMPTY_PLACE` | `"no room on this edge yet: it lists when the city grows here"` | A place waiting for room, as its row says it (spec-grid 2.5): one muted line where its offer would stand. |
| 611 | `LandScreen.ROW_DEPOSITS` | `2` | Deposits a row shows by name before "+N": 2... |
| 614 | `LandScreen.ROW_DEPOSIT_CHARS` | `20` | ...while their words run to no more than this many characters together, else one: 20 - "8 · 136 Mt" and "1 · 179 kt" and "+1" measured 152.8 px in the column's 158 at 9 px Plex Mono. |
| 699 | `LandScreen.WHOLE_FIELDS` | `" in the ground: every field of it centred in this offer, whole, paid for in its price."` | What a row's tooltip says after each resource's sites and tonnes (0.7.64, batch L): an offer holds every field centred on its ground whole, all its sites and tonnes, wherever its sites lie (CityLan... |
| 804 | `LandScreen.ON_TOP_INFO` | `"The ground is charged on top of the build, so a cheap building on" + " expensive land ...` | The second card's (i), the 0.7.6 note word for word, and what the bars are. |
| 828 | `LandScreen.ORE_INFO` | `"A mine stands on one deposit, and every mine draws on the city's tonnes" + " together....` | The Ore card's (i). |
| 845 | `LandScreen.WAITING_INFO` | `"As of last month — the sectors decide once a month, so ground bought" + " now shows up...` | The Waiting card's (i), the 0.7.6 note word for word. |
| 858 | `LandScreen.DETAILS_INFO` | `"What the world asks for a square metre of ground, as each month recorded" + " it - in ...` | The chart's (i). |
| 1130 | `LandScreen.MAP_W` | `MapView.SMALL_W, HERO_H = MapView.SMALL_H` | The map's width and the hero row's height (spec-land 2.8). |
| 1133 | `LandScreen.HERO_GAP` | `16` | The gap between the map and THE CITY... |
| 1136 | `LandScreen.CITY_PANEL` | `657` | ...and THE CITY's width: what the content area's 1,273 px leave at 1,389 x 868 (spec-land 2.8's 657). |
| 1139 | `LandScreen.ROW_H` | `28` | A row's height: 28 px (spec-land 2.8), six of them, their heads and THE CITY's inside HERO_H. |
| 1142 | `LandScreen.ROW_COLUMNS` | `{ 24, 74, 64, 158, 82, 78, 76, 58 }` | The rows' columns, in pixels: the place (#), size, ground and water, deposits, price, a dry km2, tag, button - with the gaps, CITY_PANEL. |
| 1145 | `LandScreen.ROW_GAP` | `4` | The gap between a row's columns. |
| 1148 | `LandScreen.ROW_HEADS` | `{ "#", "size", "dry · fresh · sea", "deposits", "price", "a dry km²", "", "" }` | The rows' column names. |
| 1383 | `LandScreen.WORTH_CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + " -fx-backg...` | A worth card's style, its edge's colour last. |
| 1602 | `LandScreen.ABROAD_INFO` | `"Issued abroad; the dollars are in reserve, and the " + "vault pays for the land.On the...` | The dollar offers' (i), the 0.7.13 note word for word. |

### Levers.java ([map](map/Levers.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `Levers.EFFECTS` | `420` | How wide the effects column is held beside the ladder. |

### MapView.java ([map](map/MapView.md))

| line | constant | value | says |
|---:|---|---|---|
| 103 | `MapView.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 106 | `MapView.FRAME_MS` | `8` | The most a frame spends painting tiles, in ms (spec-land 2.6): about 40 tiles at the design's 0.19 ms each. |
| 109 | `MapView.CLICK_SLOP` | `5` | A press that moves less than this many pixels is a click, not a drag: 5 (the mockup's). |
| 112 | `MapView.FIELDS_MOST` | `4000` | The most deposits marked in the far views: 4,000 - far more than a city's land and offers hold (Jerus's: a few dozen fields)... |
| 115 | `MapView.FIELD_CELLS_MOST` | `64` | ...and none once its land and offers span more than this many world cells (star): 64, a box about 490 km across - a city of billions spans a continent, where a dot a field would be the world's iron... |
| 118 | `MapView.PANE_TOP` | `14, PANE_SIDE = 22, PANE_BOTTOM = 12` | The expanded pane's padding, as City History's full screen lays it (top, right, bottom, left)... |
| 121 | `MapView.PANE_HEAD` | `36` | ...and its head line's height with the gap under it. |
| 124 | `MapView.DRAFTS_MOST` | `3` | Drafts a city may fail to keep before its map is drawn on the FX thread instead: 3. |
| 127 | `MapView.BOAT_GAME_MONTHS` | `BoatSchedule.MONTH_SECONDS / UserInterface.SECONDS_PER_MONTH` | Game months a boat-month spans (0.7.97): BoatSchedule.MONTH_SECONDS over UserInterface.SECONDS_PER_MONTH, 12 - so at 1x a month of ships plays as the research's 60 s (Q8), at every speed in step wi... |
| 130 | `MapView.LANE_BAND_ALPHA` | `0.10, LANE_BAND_PX = 8` | A lane's band: the import blue at 10%, 8 px wide (mockup 3's trade lanes, quieter: the playtest's 23 lanes overlap)... |
| 133 | `MapView.LANE_DASH_ALPHA` | `0.45` | ...and its dashes, the blue at 45%, 5 px on and 6 off (mockup 3's). |
| 139 | `MapView.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "city-map"); t.setDae...` | The one thread the view's arithmetic runs on away from the screen: the first draw of a map, the far nodes' ground. |
| 264 | `MapView.DRAWING` | `"Drawing the city's map…"` | The note while the map is drawn away from the screen. |
| 267 | `MapView.EXPAND_TIP` | `"The map over the whole window: drag to pan, scroll to zoom, Esc to come back."` | The Expand button's tooltip. |
| 270 | `MapView.HINT` | `"drag to pan · scroll to zoom · 0 fits · Esc closes"` | The expanded map's hint, at the right of its head. |
| 768 | `MapView.OFFER_EDGE` | `"#f6a6c9"` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 771 | `MapView.BLOCK_LINE_ALPHA` | `0.08` | The block lines' white, its alpha: 0.08 (0.7.69, star) - faint, so the ground under them reads first; at 0.08 a line shows on the dimmed world and on the city's own ground alike (the M5 renders). |
| 774 | `MapView.EDGE_ALPHA` | `0.55` | The city's edge's white, its alpha: 0.55, as since 0.7.61. |
| 974 | `MapView.SCALE_BAR_PX` | `120` | The scale bar's longest, in pixels: 120 (the mockup's). |
| 1124 | `MapView.CARD_FIELDS` | `3` | The most fields a hover card lists: 3. |
| 1268 | `MapView.LEGEND_ROWS` | `12` | Rows a column of the legend holds: 12, its 33 entries in three columns (since 0.7.97, nine for the shore and the sea; 24 in two before; 11 until 0.7.72 added the railway to its 22; 0.7.88 the tracks). |
| 1271 | `MapView.SHIP_SIZE_WORDS` | `"Ships are drawn 1.6\u00d7 their size until 6 px a plot"` | The legend's line under the boats' entries (0.7.97; the research's 4.4, mockup 3's legend). |
| 1274 | `MapView.TRACK_ENTRY` | `"Track"` | The legend's tracks (0.7.88; spec 5): a street the city has bought no road for. |
| 1292 | `MapView.LEGEND_NOTE_WIDTH` | `300` | The legend's note's widest, in pixels: the legend's own width at most, so it wraps under the entries. |

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
| 484 | `PeopleScreen.BORN` | `Palette.PEOPLE_LIGHT, MOVED_IN = Palette.PEOPLE` | The born and the arrived: the people teal's light step and the teal. |
| 487 | `PeopleScreen.DIED_OF_AGE` | `Palette.PEOPLE_DARK, DIED_OF_ILLNESS = Palette.BUSINESS, DIED_KILLED = Palette.BUILDING...` | The dead by cause: of age the dark teal, of illness the violet, the killed the pink, the aged out a light blue. |
| 491 | `PeopleScreen.LEFT_WORK` | `Palette.PEOPLE_DARK, LEFT_BROKE = Palette.MONEY, LEFT_CRIME = Palette.BUILDING` | The leavers by why: for want of work the dark teal, broke the blue, driven out by crime the pink. |
| 879 | `PeopleScreen.CARD` | `"-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";" + " -fx-backg...` | The Build card ground every card on both pages stands on. |
| 1016 | `PeopleScreen.PYRAMID_HALF` | `200` | How wide half of the widest band is drawn, at most. |
| 1329 | `PeopleScreen.WHY_INFO` | `"The city draws people the way Migration strikes it each month: every post " + "support...` | WHY PEOPLE COME's (i): what the bridge is. |
| 1693 | `PeopleScreen.TILE_MIN` | `56` | The narrowest a tile is drawn; anything smaller folds into "+n more". |
| 1806 | `PeopleScreen.Strip.TILE_TALL` | `62` | How tall a strip is. |
| 1938 | `PeopleScreen.OUTSIDE_INFO` | `"Families are built from the adults who work, and their children." + "Everybody else is...` | OUTSIDE THE FAMILIES' (i): who they are, the old page's words. |
| 2168 | `PeopleScreen.LADDER_INFO` | `"open = posts this band can be put into(licensed posts are on the " + "chips under the ...` | The ladder's (i): the old legend. |
| 2633 | `PeopleScreen.IN_DOLLARS` | `"Every figure on this page is in dollars, not the thousands the rest of the " + "game c...` | The page's (i): what its money is counted in. |
| 2638 | `PeopleScreen.PAID_IN_ORDER` | `"The order is fixed: the payslip first, then rent, then the fees, and " + "the shop tak...` | The order a household pays in, and what happens when it comes up short: the verdict's (i). |
| 2842 | `PeopleScreen.BESIDE` | `1200` | The narrowest page that sets the open cell's books beside the grid; under it they stack. |
| 3163 | `PeopleScreen.CARS_INFO` | `"Bought out of savings past the same cushion a share is, so income " + "decides who can...` | The cars' (i): the old note. |
| 3233 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 3234 | `PeopleScreen.SHAPE_COL` | `168` |  |
| 3365 | `PeopleScreen.BELOW_THE_RULE` | `"Below the rule: the retired, on a pension and no wage; the out of work, " + "on EI for...` | The section captions' (i): who is below the rule, and what their one cell says. |

### Pieces.java ([map](map/Pieces.md))

| line | constant | value | says |
|---:|---|---|---|
| 119 | `Pieces.LIMIT_CELL` | `190` | How wide a limit cell is, its padding included; its note wraps inside it. |
| 769 | `Pieces.POPOVER_WIDTH` | `360` | How wide a popover's text wraps. |
| 772 | `Pieces.MORE_IN_THE_MANUAL` | `"More in the manual"` | The line a popover may end with: the third layer's door, with no link until the manual has one. |
| 814 | `Pieces.INFO_SIZE` | `16` | How big the (i) is drawn: its 24-unit grid at this many pixels. |
| 920 | `Pieces.TILE_GAP` | `10` | The gap between cards in a row or a grid: Build's cards in their flow, the land office's shelf. |
| 1059 | `Pieces.PAGE_WIDE` | `1480` | The widest a redrawn page is laid out - Build's Overview and categories since 0.7.24, the land office since 0.7.26 - so a 1,920 window does not stretch a row of cards across the glass. |
| 1693 | `Pieces.Waterfall.FIGURE_ROOM` | `16, NAME_ROOM = 12, ICON = 13` | The figures' line over the plot, the least the names under it take, and an icon's size. |
| 1911 | `Pieces.CAUSE_LABEL_ROOM` | `70` | How wide a part must be drawn to carry its name and figure under it; a narrower one is keyed. |
| 1937 | `Pieces.CauseBar.GAP` | `2, UNDER = 3, KEY_GAP = 12, KEY_ROW = 3` | Between the parts, under the bar, and between the key's entries and rows. |
| 2215 | `Pieces.EffectScale.TALL` | `44, Y = 22, DOT = 10` | Its height, how far down it the line runs, and the mark's size. |
| 2291 | `Pieces.CohortBars.AXIS` | `15` | Room under the bars for the near and far names. |
| 2552 | `Pieces.ScaleRows.ROW` | `26, GAP = 10, RULE_NAMES = 16, TAG_GAP = 6, ICON = 12` | A row's least height, the gap either side of the bar, the room the rules' names take over the rows, the gap before a tag, and an icon's size. |
| 3041 | `Pieces.EVERYTHING_ELSE` | `"Everything else"` | What topSlices() calls the slices past its ramp, folded into one. |
| 3188 | `Pieces.RANK_BAND` | `12, RANK_ROW = 36` | How tall a ranked bar's band is, and the least a row takes. |
| 3191 | `Pieces.RANK_KEY` | `"rankBars.key"` | The property a ranked bar's row carries its line's key under, so a screen can find the row to scroll to. |
| 3352 | `Pieces.BRIDGE_TILE` | `200, BRIDGE_BAR = 56, BRIDGE_FIGURE = 64` | A bridge's tiles' width, its step bars' and its figures' (0.7.31). |
| 3355 | `Pieces.BRIDGE_NOTHING` | `.5` | A step under this, in the model's thousands, is nothing: half a thousand, below which signedTight() writes "$0". |
| 3574 | `Pieces.Columns.TOP` | `34, FOOT = 30, GAP = 8` | The room over the plot for a tag and a figure, under it for a label and its second line, and the gap between columns. |
| 3810 | `Pieces.BAND_SCALE` | `1.6` | How far past the top of its band a ratio's bar runs, as a multiple of the top: the band sits in the left of it, so a ratio well over its band reads as full (BankScreen's capital band's since 0.7.9). |
| 3875 | `Pieces.RUNG_ROW` | `30, RUNG_BAND = 12` | A rung's least height, and its bar's band. |
| 4064 | `Pieces.ACTION_TALL` | `40` | An action button's height on a card, where it is the thing the card is for. |
| 4067 | `Pieces.ACTION_INLINE` | `32` | ...beside a heading, where it shares a row with words: Build's "Build all three", the land office's "Buy the next 5". |
| 4070 | `Pieces.DOOR_TALL` | `30` | A door pill's height. |
| 4519 | `Pieces.MirrorRows.ROW` | `28, BAR = 12, ICON = 18, GAP = 8` | A row's least height, its bar's thickness, an icon's size and the gaps between the columns. |
| 4744 | `Pieces.DivergingBars.ROW` | `30, BAR = 12` | A row's least height and its bar's thickness. |
| 5114 | `Pieces.RangeBar.TRACK` | `6, DOT = 10, TICK_W = 5, TICK_H = 14, PAD = 6` | The track's height, the dot's size, the tick's width and height, and the room either end so the dot is never cut. |
| 5266 | `Pieces.HUNGER_INFO` | `"The measure is struck at the top of the month on the last sale: what each " + "househo...` | The (i): when the measure is struck, and what each half means. |

### PolicyScreen.java ([map](map/PolicyScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 100 | `PolicyScreen.TAXES` | `"Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises"` | The four areas, by what kind of lever each is - Jerus picked the grouping. |
| 103 | `PolicyScreen.AREAS` | `{ TAXES, WAGES, MONEY, PROMISES }` | ...as the head's chips list them. |
| 106 | `PolicyScreen.AREA_ICONS` | `{ Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION }` | ...and each one's icon: the coin, the staff, the bank, the people. |
| 111 | `PolicyScreen.POLICY_HOME` | `"Everything"` | The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. |
| 128 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 130 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 131 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 132 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Food", "Health", "Schools", "Subsidies" }` |  |
| 135 | `PolicyScreen.TAX_ICONS` | `{ Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES }` | Each page's icon on its tab, in the pages' order. |
| 137 | `PolicyScreen.WAGE_ICONS` | `{ Icons.STAFF }` | ...Wages' one, the staff. |
| 139 | `PolicyScreen.MONEY_ICONS` | `{ Icons.BANK, Icons.BANKNOTE }` | ...Money's: the bank for the policy rate, the banknote for the currency reform. |
| 141 | `PolicyScreen.PROMISE_ICONS` | `{ Icons.CANE, Icons.STAFF, Icons.FOOD, Icons.HEALTH, Icons.EDUCATION, Icons.SECTOR }` | ...and the promises': the cane, the staff, food, health, education and the sector icon for the subsidies. |
| 153 | `PolicyScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. |
| 156 | `PolicyScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). |
| 159 | `PolicyScreen.TRAY_CHROME` | `90` | How tall the staged tray is taken to be until it is laid out. |
| 291 | `PolicyScreen.LEAD_INFO` | `"Every number the city sets for itself, and what each one is doing this month." + "Ever...` | The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). |
| 482 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 485 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 488 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 651 | `PolicyScreen.BUDGET_INFO` | `"Last month's surplus or deficit, and what it would be with this change: every " + "lin...` | THE BUDGET's (i). |
| 660 | `PolicyScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press.THE BUDGET is " + "struc...` | P20, the tray's (i). |
| 1031 | `PolicyScreen.NOTHING_BINDING_INFO` | `"Nothing is binding.Every lever has room to move, no sector is shut " + "out of credit,...` | P2: what the hub says when nothing is binding. |
| 1076 | `PolicyScreen.TAX_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST }` | The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. |
| 1085 | `PolicyScreen.TAX_NAMES` | `{ "Profit", "Sales", "Wage", "Property" }` | The four taxes by name, profit to property: their cards, the bar's parts and their pages. |
| 1388 | `PolicyScreen.CAVEAT` | `"Nothing here knows that the new rate changes what anybody does next month - a " + "bus...` | What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. |
| 1392 | `PolicyScreen.CAVEAT_LINE` | `"Struck on this month's books; behaviour is not projected."` | The caveat's line on a card. |
| 1403 | `PolicyScreen.TAKE_INFO` | `"Last month, by tax.Each has a rate of its own, and three of the four a " + "move off i...` | P3, THE TAX TAKE's (i). |
| 1499 | `PolicyScreen.EVERY_TAX_INFO` | `"Moves all three rates to one number.A sector or wage band you've set apart " + "keeps ...` | P5: every tax at once, its (i). |
| 1505 | `PolicyScreen.PROPERTY_NOT` | `"Property is not on this dial: it is charged on value rather than on income, " + "and t...` | P6. |
| 1554 | `PolicyScreen.PROFIT_INFO` | `"Charged on what each sector earned before tax.A sector that lost money " + "pays nothi...` | P8, the profit tax's head. |
| 1559 | `PolicyScreen.LOSS_INFO` | `"A sector that lost money paid no profit tax at all, which is why the " + "first line f...` | P4. |
| 1563 | `PolicyScreen.SALES_INFO` | `"Charged on VALUE ADDED - what a sector sells, less the tax it already " + "paid on wha...` | P10, the sales tax's head. |
| 1569 | `PolicyScreen.WAGE_INFO` | `"Taken off every payroll in the city before the household sees it.The " + "jobs are gro...` | P12, the wage tax's head. |
| 1574 | `PolicyScreen.PROPERTY_INFO` | `"Charged on what land and buildings are assessed at, whether or not the " + "owner earn...` | P14, the property tax's head. |
| 1579 | `PolicyScreen.PROPERTY_STEP_INFO` | `"A twentieth of a point a step rather than the quarter the other three " + "get, becaus...` | P15. |
| 1584 | `PolicyScreen.REFUND_INFO` | `"\"In refund\" means a sector's credits on what it bought exceed the tax " + "on what i...` | P11. |
| 1590 | `PolicyScreen.ROLL_INFO` | `"The power and water plants are the city's own and exempt.Raising what " + "land sells ...` | P16. |
| 1595 | `PolicyScreen.SALES_SCALED` | `"Scaled, not recomputed: each sector's net remittance moves by the ratio of its " + "ne...` | The sales tax's preview, scaled (the spec's D11, B6). |
| 1632 | `PolicyScreen.OFFSET_INFO` | `"A move is in POINTS off the tax's own rate, so a row left at zero is taxed " + "at exa...` | P9, the offsets' note. |
| 1702 | `PolicyScreen.BANK_INFO` | `"The commercial bank's profit is taxed at Retail's rate - a Commercial Bank is a " + "c...` | The bank's line (B5, D12). |
| 1944 | `PolicyScreen.PAYSLIP_INFO` | `"Only the first is a tax and only the first is set here - the other three " + "are prom...` | P13. |
| 1975 | `PolicyScreen.FARMLAND_INFO` | `"A field is worth what a developer would pay for it and grows what a " + "farmer can gr...` | P17. |
| 1982 | `PolicyScreen.FARMLAND_CAVEAT` | `"Exact against today's land price.What it actually decides is whether " + "the next fie...` | P19. |
| 2129 | `PolicyScreen.FLOOR_INFO` | `"Every wage in the city is a multiple of this number, so moving it moves " + "all of th...` | P22, the floor's (i). |
| 2135 | `PolicyScreen.PINNED_WORDS` | `"At least one skill level is oversupplied AND cannot get any cheaper, so " + "those wor...` | P23, both forms. |
| 2140 | `PolicyScreen.FREE_WORDS` | `"No skill level is pinned against the floor, so the labour market is " + "clearing on p...` | ...the other: no band pinned. |
| 2145 | `PolicyScreen.FLOOR_CAVEAT` | `"Once wages have walked there, which takes about a year.Nothing about " + "this preview...` | P24. |
| 2293 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2296 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2299 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2302 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2305 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2308 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2334 | `PolicyScreen.OVER_INFO` | `"Hot money follows this difference in, and leaves the day it closes." + "It is also wha...` | P28. |
| 2340 | `PolicyScreen.REAL_INFO` | `"The currency follows this one: a dial under the inflation people expect is a " + "real...` | P28's second half: the real differential - against the inflation people expect since 0.7.42 (the UI spec's B9). |
| 2346 | `PolicyScreen.REAL_DIAL_INFO` | `"The dial less the inflation people expect: what saving at the dial earns " + "in what ...` | The dial's own real rate (0.7.45): REAL_INFO's first half, for the dial alone. |
| 2500 | `PolicyScreen.HAND_INFO` | `"Jerus's autopilot: the rule can hold the dial.Every month, before anything is " + "pri...` | P32. |
| 2504 | `PolicyScreen.REPRICE_INFO` | `"This does not reprice a single bond the city has already sold - every " + "coupon on t...` | P34: the dial card's caveat. |
| 2733 | `PolicyScreen.QE_LINE` | `"Buys term paper only: it reaches prices through what people own and through the " + "c...` | How QE reaches prices, in one plain line on the holdings card (0.7.45; the UI spec's D20). |
| 2766 | `PolicyScreen.CEILING_INFO` | `String.format("When the treasury runs dry the central bank advances the gap in money it...` | P36. |
| 2812 | `PolicyScreen.ANCHOR_INFO` | `"What people expect prices to do decides what they do next.Wages are asked " + "for hal...` | THE ANCHOR's (i): Expectations, in a player's words. |
| 2823 | `PolicyScreen.STRUCK_INFO` | `"Fees, build costs and upkeep, land, the shelf's floor, the pension's wage base, " + "t...` | The struck level's (i). |
| 2828 | `PolicyScreen.DRIFT_INFO` | `"The currency slides every month by the credible part of the " + "inflation people expe...` | THE CURRENCY'S DRIFT's (i). |
| 2969 | `PolicyScreen.BASKET_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST, Palette.TE...` | The basket's five parts' colours, groceries to services: the money ramp, then the rest - areas, not verdicts. |
| 3015 | `PolicyScreen.SWING_INFO` | `"Prices here have more than doubled and come back at some point." + "Wages, rents and e...` | P30. |
| 3020 | `PolicyScreen.REFORMED_INFO` | `"The index is measured against the city's first basket in founding money, " + "chained ...` | P31, once the money has been reformed. |
| 3229 | `PolicyScreen.COVER_INFO` | `"The rest is general revenue - the same pot the schools and the hospitals " + "come out...` | P41 and P47. |
| 3398 | `PolicyScreen.EI_INFO` | `"EI only pays the first twelve months, so a long bust costs less in EI than " + "a shor...` | P57. |
| 3841 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 3844 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 3884 | `PolicyScreen.SHARE_INFO` | `"The city's share of every course fee.Households pay the rest out of a " + "month's wag...` | P48. |
| 3891 | `PolicyScreen.BURDEN_INFO` | `String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is a " + "co...` | P49. |
| 3897 | `PolicyScreen.PRICE_INFO` | `"The founding tuition table times this, before the city's share comes off." + "The tabl...` | P50. |
| 3905 | `PolicyScreen.SCHOOL_CAVEAT` | `"Against the courses being taken now, each kind's students at its own " + "price - and ...` | P51/P52: the schools' preview caveat. |
| 4256 | `PolicyScreen.LOAN_INFO` | `"Charged on a graduate's balance while they repay it, and on nothing while " + "they st...` | P55. |
| 4262 | `PolicyScreen.LOAN_CAVEAT` | `"Against the balances the graduates owe today.The instalment itself does " + "not chang...` | P56. |
| 4307 | `PolicyScreen.SUBSIDY_INFO` | `"A protected sector is topped up to break-even every month it loses money, " + "so it n...` | P69. |

### SectorScreen.java ([map](map/SectorScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `SectorScreen.SECTOR_HOME` | `"Operations"` | The page a business opens on, and falls back to for a page it does not know. |
| 96 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` | The five pages, in the strip's order: the names other screens open a business's books by. |
| 100 | `SectorScreen.PAGE_ICONS` | `{ Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR }` | Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. |
| 112 | `SectorScreen.FRAME_CHROME` | `276` | How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own... |
| 115 | `SectorScreen.LIST_CHROME` | `190` | ...and the list's: its head and its four figures (0.7.30). |
| 118 | `SectorScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page ... |
| 121 | `SectorScreen.COUNT_MILLIS` | `400` | How long a figure takes to count from last month's to this month's (0.7.30). |
| 124 | `SectorScreen.SWEEP_MILLIS` | `700` | ...and the plant's ring to sweep up from nothing (0.7.30). |
| 194 | `SectorScreen.LIST_INFO` | `"What the city's businesses kept this month, after tax - each card one business, " + "w...` | What the list's (i) holds: the old caption, and what a card does. |
| 201 | `SectorScreen.NOTHING_YET_LIST` | `"The sector books are written when a month closes.This city has not " + "closed one sin...` | The empty books' sentence (the old list's alert). |
| 205 | `SectorScreen.NOTHING_YET_SECTOR` | `"This city has not closed a month since it was loaded, so there is no " + "statement to...` | ...and a business's (the old page's alert). |
| 209 | `SectorScreen.NO_WORD_YET_LIST` | `"Nothing recorded since the city was loaded or founded: a month on, each " + "card says...` | The list's one line while no business has a word for the month (0.7.34), in place of each card's own. |
| 381 | `SectorScreen.RUNNING_INFO` | `"How much of what its plants could make each business made this month: its " + "posts f...` | RUNNING AT's (i). |
| 505 | `SectorScreen.SPARK_WIDTH` | `120` | The width the sparkline takes on a card (0.7.30; 90 on the old list). |
| 508 | `SectorScreen.SPARK_HEIGHT` | `28` | ...and its height (0.7.30; 22 on the old list). |
| 511 | `SectorScreen.SPARK_MONTHS` | `24` | How many months of net income the sparkline draws (0.7.4): two years. |
| 1565 | `SectorScreen.OPERATING_INFO` | `"What the business made from trading, after its sales tax and the ground it " + "stands...` | The operating line's (i): what it is, and the model's own operating income, which is before the ground and the sales tax (D4, D5). |
| 1569 | `SectorScreen.REFUND_INFO` | `"The sales tax line is a REFUND this month: the credit on what this sector " + "bought ...` | The sales tax refund's (i). |
| 1574 | `SectorScreen.FALL_INFO` | `"Revenue at the left, less the sales tax it remitted and what it bought, to its " + "gr...` | The waterfall's (i). |
| 1860 | `SectorScreen.OWNERS_DOOR` | `"its owners, its share's price and what it pays: Investors"` | The Balance sheet's door to its owners' card (D7). |
| 2625 | `SectorScreen.INCOME` | `"income", SHEET = "sheet", EQUITY = "equity", CASH = "cash"` | Each statement's key in a business's open set: an open note on one is "income:note:5" in linesOpen(). |
| 2628 | `SectorScreen.OUTSIDE_INFO` | `"Money that reached its books outside its trading: the city's subsidy and the " + "arre...` | Note 6, and the equity statement's outside line: what the outside lines are (F1). |
| 2636 | `SectorScreen.DERIVED_INFO` | `"Derived when the city was loaded: its save was made before share capital was " + "kept...` | Share capital's line and note, when a save from before 0.7.75 was loaded (R3). |
| 2642 | `SectorScreen.CAPITAL_INFO` | `"What its owners put in: the book its founders' shares were issued against, and " + "ev...` | Share capital's line and note (R3). |
| 2647 | `SectorScreen.STOCK_WORDS` | `"Stock is held at what it would fetch today, so a price collapse shrinks this " + "busi...` | Note 7: the stock. |
| 2651 | `SectorScreen.ABROAD_WORDS` | `"What it has sent abroad for the world's rate, in the city's money at the rate it " + "...` | Note 9: what it holds abroad. |
| 2656 | `SectorScreen.DUE_WORDS` | `"What its loans, bonds and mortgages ask at the twelve settles ahead: a loan whole in "...` | What falls due within a year (R2), behind its line's and its card's (i). |
| 2661 | `SectorScreen.NOT_SPLIT_WORDS` | `"When it falls due is counted at the next month's books: a load keeps the debt, " + "no...` | The month after a load, R2's line. |
| 2792 | `SectorScreen.EQUITY_HEADS` | `{ "share capital", "kept, revalued", "total" }` | The equity statement's columns (R3). |
| 2795 | `SectorScreen.REVALUED_INFO` | `"Whatever else moved its equity, worked out as what is left: a building bought " + "for...` | The equity statement's remainder's (i). |
| 2860 | `SectorScreen.SCHEDULE_RATE` | `70, SCHEDULE_RUNS = 80` | The debt schedule's columns (R7): the kind (DEBT_KIND), five figures (DEBT_FIGURE), the rate and when the last of it falls due. |
| 2863 | `SectorScreen.SCHEDULE_INFO` | `"What it owed of each kind at last month's sheet, what it borrowed, repaid and had " + ...` | The schedule's (i). |
| 2973 | `SectorScreen.DEBT_KIND` | `130, DEBT_FIGURE = 100` | The debt card's columns: the kind, then owed, within a year, in one to five, after five, and the month's interest. |
| 3062 | `SectorScreen.EVERY_GATE_INFO` | `"Each building it can put up at every gate investors ask - ore, a licence, " + "staff, ...` | EVERY BUILDING AT EVERY GATE's (i). |
| 3070 | `SectorScreen.GATE_NAME` | `200, GATE_MARK = 44, GATE_FIGURE = 96, GATE_PAYBACK = 84, GATE_PAYS = 210` | The grid's columns: the building, a gate's mark, a figure, the payback, how it would pay. |
| 3073 | `SectorScreen.GATE_HEADS` | `{ "ore", "licence", "staff", "land", "pays" }` | The gates' heads, in BuildCard.GateKind's order. |
| 3220 | `SectorScreen.SHARE_ROWS` | `{ { "Shares", "n" }, { "Earnings a share, the month", "$" }, { "Dividend a share, the m...` | The report's rows: {label, kind} - "n" a count, "$" money a share, "%" a share, "x" times, "M" money. |
| 3264 | `SectorScreen.SHARE_LABEL` | `220, SHARE_FIGURE = 120` | The report's columns. |
| 3378 | `SectorScreen.CONTROL_INFO` | `"None of this is yours to set.These are private companies deciding for " + "themselves ...` | WHAT YOU CONTROL's (i): the old page's last paragraph. |
| 3383 | `SectorScreen.WAITING_INFO` | `"This business wants to build and there is nowhere to put it.It is the one " + "refusal...` | The waiting-on-ground alert's (i). |
| 3732 | `SectorScreen.FLOW_INFO` | `"Each row's money is the month the books closed on - the Income page's - and its " + "u...` | The flow's money and units: which month each is (SectorFlow's two months). |
| 3776 | `SectorScreen.REFINERY_HEAD` | `"THE REFINERY THIS MONTH"` | The card's heading, its line, and what its (i) says. |
| 3777 | `SectorScreen.REFINERY_SUB` | `"where the crude went · ribbons to scale"` |  |
| 3778 | `SectorScreen.REFINERY_INFO` | `"The month's crude comes in on the left, from the wells, the reserve or the world." + "...` |  |
| 3976 | `SectorScreen.HATCHES` | `new java.util.HashMap<>()` |  |
| 4073 | `SectorScreen.OIL_WELLS_INFO` | `"The city's wells by kind.A land well lifts the ground pool - the oil under the city's ...` | What the oil industry's (i)s say. |
| 4078 | `SectorScreen.OIL_UNITS_INFO` | `"Every kind of refinery unit.Its spread is what it makes of a litre of its feed, less" ...` |  |
| 4082 | `SectorScreen.OIL_PRODUCTS_INFO` | `"Every product of the refinery, and crude: its price here and the world's, the" + " wor...` |  |
| 4085 | `SectorScreen.OIL_RESERVE_INFO` | `"The city's own crude, in its Strategic Reserves' tanks.Fill orders crude for the next"...` |  |
| 4089 | `SectorScreen.RESERVE_CAVEAT` | `"bought at the next clearing at what crude then costs; what the room cannot take lapses"` |  |
| 4090 | `SectorScreen.RESERVE_CAVEAT_INFO` | `"A fill is an order for the next clearing: the wells' crude pro rata with the" + " refi...` |  |
| 4093 | `SectorScreen.RELEASE_CAVEAT` | `"offered to the refiners first; what they do not take ships at the export price"` |  |
| 4096 | `SectorScreen.RESERVE_LADDER` | `380` | The lever's ladder width on its dial card. |
| 4569 | `SectorScreen.SHELF_INFO` | `"A basket is one person's groceries for a month.The shelf moves a sixth of the way " + ...` | THE SHELF's (i). |
| 4725 | `SectorScreen.FOLD_PAST` | `6` | The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). |

### ServicesScreen.java ([map](map/ServicesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `ServicesScreen.OVERVIEW` | `"Overview", BOOKS = "Books"` | The page every system opens on (0.7.28), and the last chip of each, its books. |
| 107 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 110 | `ServicesScreen.SERVICE_HOME` | `OVERVIEW` | ...and every system opens on its Overview (0.7.28; General care until then). |
| 136 | `ServicesScreen.FRAME_CHROME` | `268` | How much of the stage the fixed frame takes above the page's scroller: the head, the systems, the four figures with their sparklines and changes, the pages, and their gaps. |
| 809 | `ServicesScreen.SICK_INFO` | `String.format("The bar is today's sick rate split into what makes it, in points of the ...` | The sick rate's (i): what the bar is, its floor and its cap, and why it is a cost of output and not of wages. |
| 1053 | `ServicesScreen.BURIAL_INFO` | `"A household that can save a plot's price over ten years chooses burial; " + "the ones ...` | The burial choice (the death care page's note). |
| 1159 | `ServicesScreen.LONG_SICK_INFO` | `String.format("Everybody sick this month, by how long they have been ill: the left bar ...` | The long sick's (i): who can die of it, at what chance by age (the elders' too, which the old note left out). |
| 1168 | `ServicesScreen.RECOVERY_INFO` | `String.format("The share of the sick who get better in a month: %.0f%% with no general ...` | What care cures, in words. |
| 1416 | `ServicesScreen.PLOTS_INFO` | `"Plots are consumed permanently — the land never comes back, and a cemetery " + "cannot...` | Plots are permanent (the ground's note). |
| 1420 | `ServicesScreen.CREMATORIA_INFO` | `"A rate rather than a stock, and it needs almost no land — which makes it " + "the answ...` | The crematoria's note. |
| 1757 | `ServicesScreen.DIPLOMA_INFO` | `"Teens age out at a steady rate and the ones who were in school leave with " + "a diplo...` | The diplomas' (i): the teens' note, and what the figure is (and is not). |
| 1765 | `ServicesScreen.LADDER_INFO` | `String.format("The ladder serves the minimum of its three stages, not the average — the...` | The basic ladder's notes: the minimum of three, and the four-and-three split. |
| 1773 | `ServicesScreen.PROFESSIONS_INFO` | `"A band row on the People screen can say the city has eight hundred " + "graduates and ...` | The professions page's sentence. |
| 1990 | `ServicesScreen.GATES_INFO` | `String.format("The funnel is why that many and not more.Who could enrol holds the level...` | The gates' (i): why that many and not more, as the old page said it under its five lines. |
| 2002 | `ServicesScreen.COULD_ENROL` | `"Who holds the level this course takes, in the workforce - and for a " + "professional ...` | Who could enrol, in words (the old page's note). |
| 2006 | `ServicesScreen.RETURN_INFO_PREFIX` | `"How much better off somebody is for doing it - 0 means not worth it.\n\n"` | The wage return's (i) opens on this, then says what the return is measured against (returnNote()). |
| 2363 | `ServicesScreen.POWER_INFO` | `"Power is counted in kilowatts, a rate - the screens wrote watts until " + "0.7.28, a t...` | The power row's (i): the unit, the staff discount, one workforce, who is billed. |
| 2373 | `ServicesScreen.BROWNOUT_INFO` | `"Every industrial and commercial building's output is cut in proportion " + "— a browno...` | A brownout's (i), as the grid's old alert said it. |
| 2378 | `ServicesScreen.WATER_INFO` | `"Water is counted in units of 10,000 gallons a month.The people draw " + "their own(res...` | The water row's (i). |
| 2573 | `ServicesScreen.CAUSES_INFO` | `"Every adult at liberty is counted once, at the heaviest reason they have.The" + " last...` | The causes' (i). |
| 2579 | `ServicesScreen.STOLEN_INFO` | `"What is stolen goes to the offenders' households.The killings are next" + " month's de...` | What is stolen, and the injured (the old "what it did" note). |
| 2583 | `ServicesScreen.CAUGHT_INFO` | `"Anybody caught with no staffed cell free stays on the street and keeps" + " offending....` | Anybody caught with no cell (the prisons' note). |
| 2588 | `ServicesScreen.OFFICERS_INFO` | `crime -> String.format("%s officers per 100,000 people.Canada has %s; full coverage is ...` | The officers against Canada, and the founding constabulary. |

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
| 408 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "safety", "re...` | Every section key, so open-all does not have to be kept in step by hand. |
| 502 | `SummaryScreen.WORDS` | `new CityNeeds.Words() { @ Override public String people(double count) { return Money.pe...` | The interface's own words for a figure, which the needs are read in (CityNeeds.Words). |

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
| 782 | `TimeChart.MARK_REACH` | `5` | How near a mark the pointer must be, in pixels, for the card to say it (0.7.45). |
| 1358 | `TimeChart.CARD_DECISIONS` | `10` | At most this many decisions are listed on a flag's card; the rest are counted. |
| 1530 | `TimeChart.MEASURE` | `new javafx.scene.text.Text()` | One Text node, reused to measure a string's width in a font (textWidth()). |

### TradeScreen.java ([map](map/TradeScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 98 | `TradeScreen.OVERVIEW` | `"Overview", MONTH = "The month", GOODS = "What we trade", CURRENCY = "The currency", RE...` | The five pages, in the strip's order. |
| 102 | `TradeScreen.PAGES` | `{ OVERVIEW, MONTH, GOODS, CURRENCY, RESERVES }` | ...as the strip lists them. |
| 105 | `TradeScreen.PAGE_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.LORRY, Icons.EXCHANGE, Icons.SAFE }` | ...and each one's icon: the tiles, the coin, the lorry, the two arrows, the safe. |
| 108 | `TradeScreen.TRADE_HOME` | `OVERVIEW` | The page the tab opens on, and the one the rail's trade icon resets to. |
| 123 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` | The currency's two pages until 0.7.35: a door that names the first lands on The currency. |
| 126 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` | The reserves' three pages until 0.7.35: a door that names the first lands on The reserves. |
| 161 | `TradeScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and the five figures - until the frame is laid out and its own height is read. |
| 164 | `TradeScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - BankScreen's... |
| 167 | `TradeScreen.GROW_MILLIS` | `500` | How long the bars take to grow out of their axis when a month lands. |
| 170 | `TradeScreen.OVERVIEW_GOODS` | `8, PAGE_GOODS = 12` | The Overview shows this many goods; What we trade this many before "+N more" (the spec's D6). |
| 173 | `TradeScreen.EXCHANGE_CARD` | `"#exchange", HOLDINGS = "#holdings", FORCES = "#forces", DEBT = "#debt", LEAVE = "#leav...` | The scroll targets a door can land on. |
| 356 | `TradeScreen.LEAD_INFO` | `"What the city sells, what it buys, and what it owes in somebody else's money." + "The ...` | The tab's (i): the landing's lead (the spec's L2), and what the Overview shows. |
| 361 | `TradeScreen.PAGE_INFO` | `{ "Every dollar that crossed the city's edge this month, and which way: a walk from wha...` | Each page's (i), in PAGES' order after the Overview: what it holds (the landing's row blurbs until 0.7.35). |
| 405 | `TradeScreen.NOT_COUNTED` | `"not counted yet"` | What a figure of the month says until a month has turned since the city was loaded or founded (ForeignAccounts.isMonthCounted()). |
| 408 | `TradeScreen.NOT_COUNTED_NOTE` | `"since the city was founded, or loaded from a save before 0.7.46: a month on, it is"` | ...and its note. |
| 763 | `TradeScreen.GAUGES_INFO` | `"Always here, deliberately understated, and the same three every month - so a " + "play...` | The section's (i): the banner's reason (the spec's L8). |
| 923 | `TradeScreen.TRADE_INFO` | `"Every good the city's businesses sold abroad and bought abroad this month, from " + "t...` | WHAT WE TRADE's (i). |
| 931 | `TradeScreen.NOTHING_CROSSED` | `"Nothing crossed the city's edge this month.No exports, no imports, nothing " + "borrow...` | The empty month's whole (the spec's M2). |
| 1001 | `TradeScreen.PUMP_WORDS` | `String.format("The grocers' filling stations buy the drivers' petrol at wholesale - the...` | Petrol's popover, under the pump price (0.7.83): where it comes from. |
| 1006 | `TradeScreen.FREIGHT_AFTER_LOAD` | `"Not counted yet: the railway's freight on each good is struck when the month " + "turn...` | The prices before a month is counted - a city just founded, or loaded from a save before 0.7.46 (the spec's B14; a newer save carries the month's trade the freight is struck on, A1). |
| 1040 | `TradeScreen.RATE_INFO` | `"How many of the city's dollars one US dollar costs.Higher is a weaker currency: " + "i...` | THE CURRENCY's (i): the unit, and parity (the spec's Q1, Q3). |
| 1094 | `TradeScreen.COVER_SCALE` | `ForeignAccounts.COMFORTABLE_COVER * 2` | The cover gauge's scale: twice the comfortable line, a year of imports. |
| 1121 | `TradeScreen.PARITY_SCALE` | `ForeignAccounts.PARITY_FAR * 1.5` | The parity gauge's scale either side: half as far again as PARITY_FAR, so the red band shows. |
| 1155 | `TradeScreen.COVER_INFO` | `String.format("Import cover is the oldest test there is: if every dollar of " + "earnin...` | The gauges' (i)s: what each one measures (the cover sentence, the spec's C1; the others the landing's notes). |
| 1161 | `TradeScreen.BACKING_INFO` | `String.format("The vault against the foreign money parked in the city's bank, " + "whic...` | ...the backing gauge's: the vault against the money that can leave, and where a run becomes likely. |
| 1165 | `TradeScreen.PARITY_INFO` | `String.format("How far the rate sits from parity - where a basket costs the same " + "h...` | ...and the parity gauge's: the one parity rule's two lines, and where else they are read. |
| 1197 | `TradeScreen.NOTHING` | `.0005` | Under half a dollar is nothing: a step that small is named, not drawn. |
| 1269 | `TradeScreen.CURRENT_INFO` | `"What the city earned from the world by selling it things, less what it spent buying " ...` | The current account's note (the spec's A1). |
| 1274 | `TradeScreen.FINANCIAL_INFO` | `"Borrowing abroad and foreign money parking here are both inflows, and neither is " + "...` | The financial account's note (A2). |
| 1279 | `TradeScreen.INCOME_INFO` | `"Interest and dividends: what the businesses' and the households' paper abroad paid " +...` | The income line's note (B3). |
| 1284 | `TradeScreen.SURPLUS_INFO` | `"A surplus month: the world owes the city a little more than it did, and that is what "...` | The month's closing words (the spec's D17). |
| 1288 | `TradeScreen.DEFICIT_INFO` | `"A deficit month has to be settled in somebody else's money: out of the vault, or by " ...` | ...and a deficit month's. |
| 1352 | `TradeScreen.MONTH_INFO` | `"Every dollar that crossed the city's edge this month, and which way.Steps: each " + "c...` | The hero's (i). |
| 1358 | `TradeScreen.NOT_SAVED_INFO` | `"The month's flows across the edge - what was sold and bought abroad, the income, " + "...` | Why a city reads nothing yet (the spec's B1): founded, or loaded from a save before 0.7.46, which did not carry the month's flows (D4, built as A2). |
| 1364 | `TradeScreen.HAND_INFO` | `"Below the line, and deliberately: an intervention does not earn or spend anything " + ...` | The treasury's hand (the spec's A6). |
| 1369 | `TradeScreen.VALUATION_INFO` | `"%s of foreign claims were written off this month.It improves what the city owes " + "t...` | The valuation change (M5, A4; D15: a chip, not an alert). |
| 1505 | `TradeScreen.RIVER_INFO` | `"Band width is money.The two sides balance because they must: what came in and what " +...` | The river's foot (the spec's section 4: one line, the rest in the (i), no colour named). |
| 1630 | `TradeScreen.HOLDINGS_INFO` | `"The stocks the flows add up to - the rough shape of an international investment " + "p...` | The holdings' (i) (the spec's A5). |
| 1862 | `TradeScreen.RECORD_INFO` | `"One month says whether a mill was staffed.The run says whether the city earns its " + ...` | The record's sentence (the spec's H1). |
| 1923 | `TradeScreen.WORLD_INFO` | `"Every world price is quoted in the world's money and converted at the rate.So a " + "w...` | The world prices' note (the spec's G3). |
| 2207 | `TradeScreen.FORCES_INFO` | `"One reading: what the next month does to the rate, on the accounts as they stand " + "...` | The forces card's (i) (the spec's F1). |
| 2215 | `TradeScreen.COMES_TO_INFO` | `"The push is what the month is doing to the currency; the drift is the slide " + "the i...` | WHICH COMES TO's (i) (F3's two notes). |
| 2331 | `TradeScreen.VAULT_MOVED_INFO` | `"The vault is kept in dollars, so its dollar figure stays put and its local figure " + ...` | What the currency does to the vault (the spec's E2 note, both ways). |
| 2405 | `TradeScreen.ONE_POT_INFO` | `"The vault is one pot — the game does not tag a dollar as borrowed or earned, and it " ...` | WHOSE IT IS's (i) (the spec's R4, and B11: the method's name is out of it). |

### UserInterface.java ([map](map/UserInterface.md))

| line | constant | value | says |
|---:|---|---|---|
| 279 | `UserInterface.STAGE` | `Palette.STAGE` | The middle of the window: the blackish blue everything else sits on (Palette.STAGE since 0.7.21). |
| 429 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 441 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed steps along: a rung a click on the clock's two arrows since 0.7.21, and the stops the speed slider stuck to before. |
| 442 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 463 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 489 | `UserInterface.PRESS_HELD` | `"UserInterface.pressHeld"` | The scene property that says a button is down (0.7.40), for what waits on a timer outside this class (TimeChart's settle). |
| 1335 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1736 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1811 | `UserInterface.STRIP_INFLATION_QUIET` | `.03` | Inflation within this many points of the player's target (DebtManager.getInflationTarget()), either side, reads as on target. |
| 1814 | `UserInterface.STRIP_INFLATION_OVER_TARGET` | `.05` | Inflation more than this many points over the player's target reads red: prices running away from what the player asked for. |
| 1817 | `UserInterface.STRIP_DEFLATION_ALARM` | `.10` | Deflation past this reads red, whatever the target: prices collapsing. |
| 1829 | `UserInterface.DATE_WIDTH` | `164` | How wide the clock's date is held, so the tiles do not move as the day's name changes width: "28 September 2151" at the date's size, and a little over (0.7.24: at 17 px, so the money block and five... |
| 1832 | `UserInterface.DATE_SIZE` | `17` | The date's size in the clock (0.7.24; it was 19). |
| 1835 | `UserInterface.SPARK_MONTHS` | `120` | How many months a tile's sparkline draws: ten years, or everything recorded if less. |
| 1838 | `UserInterface.SPARK_WIDTH` | `72` | A tile's sparkline at full size: on the label's row since 0.7.24, as the mockups draw it (it was 84 by 30, beside the words). |
| 1840 | `UserInterface.SPARK_HEIGHT` | `16` | ...and its height, the label's row (0.7.24; it was 30). |
| 1843 | `UserInterface.SPARK_MIN` | `30` | Narrower than this and a tile draws no sparkline: a line the width of a word says nothing. |
| 1846 | `UserInterface.TILE_FIGURE` | `15` | The size of a tile's figure (0.7.24: 15, so the money block and five tiles fit a 1,280 window whole; it was 17). |
| 1848 | `UserInterface.TILE_LABEL` | `10.5` | The size of a tile's label, and of the money block's. |
| 1850 | `UserInterface.TILE_CHANGE` | `10.5` | The size of a tile's change line (0.7.24; it was 11). |
| 2199 | `UserInterface.MONEY_FIGURE` | `28` | The cash in the money block: the mockups' 28 px. |
| 2851 | `UserInterface.BACKDROP_BLOCK` | `"#121c28"` | The building blocks' fill and edge, and an unlit window, on the backdrop: the skyline's own darks, under the panels' ground. |
| 2852 | `UserInterface.BACKDROP_EDGE` | `"#1d2b3c"` |  |
| 2853 | `UserInterface.BACKDROP_WINDOW` | `"#22344a"` |  |
| 2856 | `UserInterface.FOUNDING_DIM` | `.72` | How dark the founding screen dims the backdrop under its panel: the mockups' 0.72. |
| 2859 | `UserInterface.MENU_CITIES` | `3` | Up to this many of the cities saved last, as cards at the menu's bottom right. |
| 3202 | `UserInterface.MENU_LEFT` | `96` | How far in from the window's left the menu's column and version sit. |
| 3205 | `UserInterface.MENU_BUTTON` | `360` | The menu's buttons' width, as the mockups draw them. |
| 3280 | `UserInterface.CITY_CARD` | `250` | A city card's width, as the mockups draw it. |
| 3329 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 3774 | `UserInterface.TIP_WIDTH` | `420` | The width a tooltip's text wraps at, unless it asked for its own. |
| 3961 | `UserInterface.PAGE_FOOT` | `24` | Room left under the end of every scrolled page: a margin, since nothing floats over the stage's foot (0.7.21; it was 90, the dome's 74 and a margin). |
| 4637 | `UserInterface.CONSTRUCTION_TAB` | `44` | How wide the construction panel's tab is. |
| 4895 | `UserInterface.PANEL_TEXT` | `256` | How wide the construction panel's lines wrap: the panel less its padding. |
| 5038 | `UserInterface.RAIL_WIDTH` | `Palette.RAIL` | The rail's width: an icon over its name, as the mockups draw it (0.7.21; it was 46). |
| 5041 | `UserInterface.RAIL_BUTTON` | `54` | A rail button's height, and the least it may shrink to on a short window. |
| 5042 | `UserInterface.RAIL_BUTTON_MIN` | `40` |  |
| 5045 | `UserInterface.RAIL_ICON` | `20` | How big a rail icon is drawn: its 24-unit grid at 20 pixels. |
| 5486 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |
| 5783 | `UserInterface.TOAST_SECONDS` | `8` | How long a toast stays before it fades, in seconds. |
| 5786 | `UserInterface.TOAST_MAX` | `3` | How many toasts at once. |
| 5789 | `UserInterface.TOAST_WIDTH` | `340` | How wide a toast's text wraps. |

## harnesses (306 constants)

### AgricultureCheck.java ([map](map/AgricultureCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 56 | `AgricultureCheck.MIXED` | `"Mixed Farm"` |  |
| 57 | `AgricultureCheck.GRAIN` | `"Grain Farm"` |  |
| 58 | `AgricultureCheck.GLASS` | `"Greenhouse Complex"` |  |

### AllChecks.java ([map](map/AllChecks.md))

| line | constant | value | says |
|---:|---|---|---|
| 27 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "NewGameCheck", "WorldCheck", "GridCheck", "ConversionCheck", "C...` | In the order they are cheapest to fail. |

### AutoBuildCheck.java ([map](map/AutoBuildCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 241 | `AutoBuildCheck.heldBy` | `new LinkedHashMap<>()` | The passes that held a kept service short of its target with nothing on site, by the cut that held it. |
| 243 | `AutoBuildCheck.builtBy` | `new LinkedHashMap<>()` | The buildings it ordered over the run, by measure. |
| 709 | `AutoBuildCheck.ORE_TONNES` | `1` | A tonne of iron under each offer of the ore fixture: any ore makes an offer not bare ground (LandMarket.bareGround()). |

### BondCheck.java ([map](map/BondCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 117 | `BondCheck.ISSUER` | `Sectors.CONSTRUCTION` | The sector every played fixture's bond is issued by: sound, owing nothing, with plant to borrow against. |

### BuildCardCheck.java ([map](map/BuildCardCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 731 | `BuildCardCheck.KIND_EXAMPLES` | `{ { "Built 2 Industrial Bakery - output short of demand", BuildCard.WordKind.BUILDING }...` | The examples: a word shaped as the model files it, and the kind it must read as. |

### BusinessServicesCheck.java ([map](map/BusinessServicesCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `BusinessServicesCheck.CONTACT` | `"Contact Centre"` | The three rungs, and the band each is meant to employ. |
| 59 | `BusinessServicesCheck.SHARED` | `"Shared Services Centre"` |  |
| 60 | `BusinessServicesCheck.OFFICE` | `"Engineering Services Office"` |  |

### CentralBankCheck.java ([map](map/CentralBankCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 205 | `CentralBankCheck.KINDS` | `{ "interest on reserves", "lent at the window", "repaid at the window", "the window's i...` |  |
| 208 | `CentralBankCheck.seen` | `new boolean [ KINDS.length ]` |  |
| 1136 | `CentralBankCheck.ROLL_SLOT` | `10` | The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. |
| 1530 | `CentralBankCheck.STRICT_BRANCH` | `600` | The month the probe city leaves Standard at: a mature city, the inflation ensemble's month for its policies. |
| 1533 | `CentralBankCheck.STRICT_HORIZON` | `240` | How long each of its twins plays on from there: twenty years, the inflation ensemble's window after its month 600. |

### ChildcareCheck.java ([map](map/ChildcareCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `ChildcareCheck.ONTARIO_INFANTS_PER_ADULT` | `10.0 / 3` | Children to an adult in Ontario's infant groups, 3 adults to 10 (O. |
| 67 | `ChildcareCheck.ONTARIO_PRESCHOOL_PER_ADULT` | `8` | ...and in its preschool groups, 1 to 8: the fewest. |
| 70 | `ChildcareCheck.SMALL` | `15, CENTRE = 16, LARGE = 17` | The three centres' ids in buildings.json: the Small Childcare Centre, the Childcare Centre and the Large. |
| 73 | `ChildcareCheck.CHILDCARE` | `BuildAdvice.Measure.care(CareType.CHILDCARE)` | The advice's measure for childcare, which the sections here read the need, the site and the card through. |

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
| 81 | `ConversionCheck.FIXTURE` | `"conversion-saves.json"` | The fixture: the five saves' land, copied key for key (src/main/resources). |
| 84 | `ConversionCheck.WHOLE_CITY_PURCHASES` | `133` | Purchases the whole-field city of section 4 is bought to: 133, as many as Jerus's live city had made. |

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
| 550 | `CurrencyCheck.Before.broken` | `new int [ 5 ]` |  |

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

### ExpectationsCheck.java ([map](map/ExpectationsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 107 | `ExpectationsCheck.TARGET` | `.03` | The target the bare fixtures aim at: not the default, so nothing reads the default by accident. |

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

### HistoryCheck.java ([map](map/HistoryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 44 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

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
| 768 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 771 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |

### LandCheck.java ([map](map/LandCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 2079 | `LandCheck.RESEARCH` | `{ new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450.60153519, ...` | The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. |
| 2222 | `LandCheck.DEAL` | `7` | Read by nothing since 0.7.67: the width of the bands section 19 tiled the plane with until then, 7 plots, which no offer was - the pieces are the holdings and the offers standing now. |

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
| 1027 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1743 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasional"))` | True when this run is played by somebody paying attention. |
| 1750 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1753 | `LongPlaytest.CHILDCARE` | `Boolean.getBoolean("playtest.childcare")` | -Dplaytest.childcare=true (0.7.71, batch N2): the city builds childcare by the build advice's own card, which the advisor never does. |
| 1792 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(System.getPr...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1820 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").trim().toUp...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 1887 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 1890 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 2222 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 2230 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE").trim()....` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2257 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").trim())` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 2260 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT").trim())` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 2263 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 2293 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2312 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2328 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(System.getPrope...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2343 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueOf(System.g...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2365 | `LongPlaytest.MONEY_GATE_WHOLE` | `"whole".equalsIgnoreCase(System.getProperty("playtest.moneyGate", "").trim())` | THE STRICTER MONEY GATE, FOR THE COUNTERFACTUAL (0.7.82, batch O5): -Dplaytest.moneyGate=whole has the refiners' spread planner test every order on its whole cost, whoever pays (SpreadPlanner.ON_IT... |
| 2368 | `LongPlaytest.REFINERY_TRACE` | `Boolean.getBoolean("playtest.refinery")` | -Dplaytest.refinery=true (0.7.82): the refiners' planner yearly - its outlook and its best candidates under both money gates (refineryLine()). |
| 2371 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.valueOf(System....` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2516 | `LongPlaytest.childcareBuilt` | `new java.util.TreeMap<>()` | ...the buildings, by name. |
| 2519 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 3174 | `LongPlaytest.OIL_FUEL_IMPORTS_SHARE` | `.01` | The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. |
| 3204 | `LongPlaytest.refinedRun` | `new java.util.EnumMap<>(Good.class)` | ...and the refinery's products over the run (0.7.76, batch O1): made, by product, and what of them its tanks could not hold (spec-oil 6's joint-products risk). |
| 3288 | `LongPlaytest.terminalsOrdered` | `new java.util.TreeMap<>()` | Terminals the test player ordered, by name; the looks that found a berth's worth with no coast; the first terminal's month. |
| 3297 | `LongPlaytest.PORTS_TRACE` | `Boolean.getBoolean("playtest.ports")` | -Dplaytest.ports=true (0.7.86): the ports yearly - each kind's tonnes across the boundary, its berths, its share at sea and the month's calls (portsLine()). |
| 3371 | `LongPlaytest.refinersOpened` | `new java.util.TreeMap<>(), refinersSold = new java.util.TreeMap<>()` | THE SPREAD PLANNER'S BUILDINGS OVER THE RUN (0.7.82, batch O5): each of the refiners' buildings opened and sold back, and the crude units and the conversion units standing at most. |
| 3435 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 3442 | `LongPlaytest.GROUND_AHEAD_CASH_SHARE` | `.10` | The share of the treasury's cash one look spends keeping ground ahead (0.7.58, J1d): a tenth, the share the war chest tops the reserves up from and the every-13th-stop purchase is held under. |
| 3970 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 4002 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 4548 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 4556 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |

### ManufacturingCheck.java ([map](map/ManufacturingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 63 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 64 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 65 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |

### MapCheck.java ([map](map/MapCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 160 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 163 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 175 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114, 1, 2, 0,...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 sin... |
| 180 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 183 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 186 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 189 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 207 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 210 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 213 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 216 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 219 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 222 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 10B, in ms (measured 0.76). |
| 225 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 228 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 231 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 234 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 261 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } @ Override...` |  |
| 755 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 1483 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated ... |
| 1638 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 1908 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 1911 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 2361 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, whi... |
| 2468 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 2598 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district we... |
| 2615 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 2618 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 2890 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 2893 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 2896 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 2897 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` |  |

### MiningCheck.java ([map](map/MiningCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 36 | `MiningCheck.IRON_SEED` | `35` | A world whose founding site has an iron field near it, as the default world's had until 0.7.98 (35 sites, 449 Mt, 0.87 km out): 35, whose nearest is 36 sites, 471 Mt, 0.97 km out on dry ground, non... |

### MonetaryCheck.java ([map](map/MonetaryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 673 | `MonetaryCheck.HELD_RATES` | `{.03,.10,.20,.40 }` | The policy rates the one founding is held at, from month 25: the three of the baseline, and since 0.7.2 a fourth at 40% - past the old stop of the dial (25% until 0.7.2), the uncapped case. |
| 676 | `MonetaryCheck.MEASURED_MONTHS` | `60` | How long each run is: five years, the last three of them at the held rate. |
| 679 | `MonetaryCheck.HELD_FROM` | `ForeignAccounts.SETTLING_MONTHS + 1` | The month the dial is held from: the first in which the currency may move. |
| 692 | `MonetaryCheck.MEASUREMENT_NOISE` | `.0010` | How far apart two runs of the one founding may read, in inflation a year, when only the dial's timing moves: 0.10 points. |
| 695 | `MonetaryCheck.TRANSMISSION_FLOOR` | `.01` | How much lower inflation must run at a dial of 40% than at 3%, a year: one point - the channel has to be worth a point across the range or it is not a channel. |

### MortgageCheck.java ([map](map/MortgageCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 112 | `MortgageCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 113 | `MortgageCheck.FEE` | `Bank.LOAN_FEE` |  |

### NewGameCheck.java ([map](map/NewGameCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 1100 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 1101 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |

### OilCheck.java ([map](map/OilCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 200 | `OilCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder, for the reloads (section 9). |
| 780 | `OilCheck.PRODUCTS` | `{ Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS, Good.FUE...` | The nine products of 0.7.76 (spec-oil 2.1), in the spec's table's order. |
| 784 | `OilCheck.LITRES_A_TONNE` | `{ 1850, 1351, 1320, 1260, 1180, 1127, 1010, Double.NaN, Double.NaN }` | The spec's table (2.1): litres a tonne for the litre goods ([P35]; naphtha and lubricants est., JODI), NaN for the two in tonnes. |
| 787 | `OilCheck.LADDER` | `{.46, 1.0, 1.20, 1.28, 1.35, 1.89,.98, 1.08,.155 }` | ...and each one's ratio to crude's world middle (the research's ladder, the prototype's RATIO): a litre's, or a tonne's for bitumen and coke. |
| 790 | `OilCheck.PUMP_LITRE` | `Motoring.CAR_FUEL_PER_JOURNEY / Motoring.LITRES_PER_JOURNEY` | FUEL's import price, 0.7.62 to 0.7.75 (petrol's and diesel's at 0.7.76 and 0.7.77): a journey's pump price over its litres. |
| 793 | `OilCheck.PETROL_CUT_PCT` | `63, DIESEL_CUT_PCT = 59` | What the ladder takes off the pump price, as the spec gives it (spec-oil 1, item 4): petrol 63%, diesel 59%, to the percent. |
| 796 | `OilCheck.FOUR_FIGURES` | `5e-4` | The spec's four figures: a price within half a unit in its fourth significant figure. |
| 1729 | `OilCheck.SEA_OIL_SEED` | `518` | The world whose founding site's nearest oil field in the sea is heavy and shallow enough for a jacket: 518, its field 3.5 km out - heavy, 12 sites (a jacket's slots), 58 m deep (fixW1-notes.md, the... |
| 1732 | `OilCheck.FIAT_TONNES` | `1_000` | Tonnes of oil a fixture's centre is handed by fiat ahead of the field, so a lift crosses from it into the field: 1,000. |

### OilViewCheck.java ([map](map/OilViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 105 | `OilViewCheck.GROUND` | `2_000_000` | The oil handed to the sea town's two dry sites by fiat: the ground pool, plenty for the sections' months. |
| 108 | `OilViewCheck.FILL` | `40_000` | The fill the sea town orders for its reserve. |
| 111 | `OilViewCheck.SEA_MONTHS` | `14` | Months the sea town plays before it is read: past a year, so its platform's wells are in their plateau's second year. |

### OrderSearchCheck.java ([map](map/OrderSearchCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 54 | `OrderSearchCheck.LONG_MONTHS` | `1200` | Months the playtest's city plays with every order watched... |
| 57 | `OrderSearchCheck.DEAR_MONTHS` | `120` | ...then this many more with the policy rate held at DEAR_RATE, so the mortgage lender trims and refuses. |
| 59 | `OrderSearchCheck.DEAR_RATE` | `.15` | ...and the policy rate those months are held at. |
| 67 | `OrderSearchCheck.COPY_MONTH` | `400` | The copy of that city: the month it is copied at, how many times over, and how many months it plays. |
| 69 | `OrderSearchCheck.COPY_TIMES` | `1000` | ...how many times over it is copied... |
| 71 | `OrderSearchCheck.COPY_MONTHS` | `24` | ...and the months the copy plays. |

### PlanCheck.java ([map](map/PlanCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 89 | `PlanCheck.TIMED` | `5` | Repeats a district's plan is timed over, after one untimed: its median is its time. |
| 99 | `PlanCheck.PLAN_MS` | `MapCheck.SCREEN_MS / 2` | The most a district's plan may take, the slowest of the dense screen's districts (each its median of TIMED): half MapCheck's screen, 40 ms (star) - one plan and a screen painted from plans within S... |
| 102 | `PlanCheck.TABLE` | `{ { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 }, { 48, 13, 6052, 3137, 2915, 0, 0, ...` | The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junction... |
| 108 | `PlanCheck.TABLE_BUILDINGS` | `3617, TABLE_BUILDING_PLOTS = 32217` | ...its buildings, their plots, and the road of each budget, whole plots. |
| 109 | `PlanCheck.TABLE_BUDGET` | `{ 9388, 4484, 2242 }` |  |
| 112 | `PlanCheck.TEST_HUB_X` | `66, TEST_HUB_Y = 86` | The prototype's hub on its test district, in the district's plots: (70, 90) in its frame, 4 plots in. |
| 115 | `PlanCheck.TEST_UNOWNED_ROWS` | `40, TEST_UNOWNED_FROM = 196` | The part of the test district the prototype did not own: its first 40 rows east of plot 196 (O[:44, 200:] in its frame). |
| 118 | `PlanCheck.FILLS` | `{ 0.75, 0.80, 0.85, 0.92 }` | The fills of 2.10's table: the model's buildings and roads at these shares of a flat district's ground. |
| 121 | `PlanCheck.PROTO_FRAME` | `264` | The ground 2.10's fills are shares of: the prototype's frame, the district and 4 plots about it, 264 x 264 (its n = OFF + 32 x 8 + 4) - so its 75% is 79.8% of the district's own 256 x 256. |
| 124 | `PlanCheck.GRAVEL_TRIPS` | `900, PAVED_TRIPS = 1200` | Gravel's and paved road's trips a road (buildings.json's capacities, as the prototype read them): the same city paved holds his roads' trips on Paved Roads. |
| 134 | `PlanCheck.TEST_GROUND` | `"g36f14g13f1g14f29g89u61/g36f15g11f2g14f28g90u61/g35f17g10f2g13f29g90u61/g35f17g24f30g9...` | THE PROTOTYPE'S TEST DISTRICT (claude/roads-prototype/proto2.py and roads_proto.py, written out by batch RD1): district_ground(264, 7) - grass, forest, a river two to four plots wide, a bay of sea ... |
| 176 | `PlanCheck.TEST_NUDGE` | `{ 0.3727494347187473, 0.03906684873305479, 0.22912251263578634, 0.5245030267418446, 0.8...` |  |
| 193 | `PlanCheck.TEST_ACROSS` | `2483539012620168834L` |  |
| 194 | `PlanCheck.TEST_TYPES` | `{ 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31, 32, 33, 3...` |  |
| 195 | `PlanCheck.TEST_COUNTS` | `{ 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22, 22, 19, ...` |  |
| 196 | `PlanCheck.TEST_DEAL` | `{ 0.8623860022826665, 0.4688361493186886, 0.49244847032280087, 0.9101025582510129, 0.04...` |  |
| 209 | `PlanCheck.TEST_GRAVEL` | `8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.870967741936` |  |
| 210 | `PlanCheck.TEST_PLACED_JERUS` | `- 1980402857778823524L, TEST_STREETS_JERUS = - 7143981648789130548L` |  |
| 211 | `PlanCheck.TEST_PLACED_PAVED` | `9000504916410474359L, TEST_STREETS_PAVED = - 4283127041626870812L` |  |
| 212 | `PlanCheck.TEST_PLACED_THIN` | `9000504916410474359L, TEST_STREETS_THIN = 4092048347058802282L` |  |
| 582 | `PlanCheck.BUDGETS` | `{ "his roads", "the same trips paved", "half that" }` |  |
| 932 | `PlanCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } @ Override...` |  |
| 987 | `PlanCheck.ESTATE_FILL` | `0.4` | The share of a flat district's plots each estate-band type is set at, alone: 0.4 - room to spare, so what differs is the cells they take. |
| 1079 | `PlanCheck.SPARE_FILL` | `FILLS [ 0 ], FULL_FILL = FILLS [ FILLS.length - 1 ]` | Section 4's fill with cells to spare (2.10's first, 75%) and its fullest (92%: every cell open, and outer kinds left without a place when the bands keep apart). |

### PolicyPreviewCheck.java ([map](map/PolicyPreviewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `PolicyPreviewCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 113 | `PolicyPreviewCheck.ORDERS` | `{ { "House", "580" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders. |

### PopulationCheck.java ([map](map/PopulationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `PopulationCheck.ADULT_MIX` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share these fixtures run at. |

### PortCheck.java ([map](map/PortCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 104 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 109 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 112 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 115 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR; dry Capesize, Panamax; deep-sea boxes; general cargo. |
| 116 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` |  |
| 117 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` |  |
| 118 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` |  |

### RefineryCheck.java ([map](map/RefineryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 102 | `RefineryCheck.out` | `System.out` |  |
| 103 | `RefineryCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 392 | `RefineryCheck.UNITS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkylation Unit...` | The units the town stands beside its Oil Refinery: one small one of every kind but the asphalt unit, which medium crude cannot feed. |
| 509 | `RefineryCheck.PROTOTYPE_WAGE` | `4.84` | The prototype's frame (spread.py): wages a post-month, world $k (m4000's Refining, 736k over 200 posts at 760). |
| 512 | `RefineryCheck.PROTOTYPE_RATE` | `.005` | ...and its money test, a month on the whole cost (its RATE). |
| 515 | `RefineryCheck.PROTOTYPE_REFINERY_CAPITAL` | `78_000` | ...and its Oil Refinery's capital: D$60M and its 1,000 materials at 18 (spread.py's TOPPING); every other building its cash. |
| 518 | `RefineryCheck.PROTOTYPE_BUILD_MONTHS` | `6` | ...and the months an order takes to open. |
| 521 | `RefineryCheck.PROTOTYPE_MONTHS` | `240` | ...and the months it runs. |
| 524 | `RefineryCheck.M4000_PETROL` | `7.63e6, M4000_DIESEL = 16.13e6` | The prototype's demands, litres a month of petrol and diesel: the playtest's city at m4000 (spread.py's cities). |
| 527 | `RefineryCheck.FORTY_WELLS` | `40 * 415` | ...and its wells in the 40-well rows: 40 land wells' 415 t a month. |
| 841 | `RefineryCheck.PLANNER_FILES` | `new java.util.IdentityHashMap<>()` |  |

### RefineryViewCheck.java ([map](map/RefineryViewCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 95 | `RefineryViewCheck.PX` | `1e-6` | ...and a picture's pixels: within a millionth of one. |
| 97 | `RefineryViewCheck.FILES` | `new java.util.IdentityHashMap<>()` |  |
| 100 | `RefineryViewCheck.CAMPUS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkylation Unit...` | The units of the campus town: one small of each kind but the asphalt unit (heavy crude's), so the cracking unit and the coker feed alkylation and the hydrocracker and the coker the reformer. |
| 104 | `RefineryViewCheck.WELLS` | `2` | Land wells on fiat sites in the campus town - fewer than its refinery runs on, so it imports the rest - and the oil each site holds. |
| 105 | `RefineryViewCheck.SITE_TONNES` | `5_000_000` |  |
| 108 | `RefineryViewCheck.MONTHS` | `24` | Months the towns play before they are read: two years, so the households own cars and drive (OilCheck 4's). |

### RestaurantsCheck.java ([map](map/RestaurantsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `RestaurantsCheck.DINER` | `"Diner"` |  |
| 72 | `RestaurantsCheck.RESTAURANT` | `"Restaurant"` |  |

### RestructureCheck.java ([map](map/RestructureCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 514 | `RestructureCheck.DOLLAR_ASK` | `5_000` | What the dollar round trip borrows: small against what its city earns, so the world's premium stays low. |

### RoadCheck.java ([map](map/RoadCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 118 | `RoadCheck.GRAVEL` | `ConstructionControl.PAVE_FROM, PAVED = ConstructionControl.PAVE_TO, HIGHWAY = "Elevated...` | The three roads by name: the paving's from and to, and the highway. |
| 122 | `RoadCheck.ROADS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS)` | The advice's measure for roads, which the sections here read the site, the units and the cards through. |

### ScaleCheck.java ([map](map/ScaleCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 92 | `ScaleCheck.TARGET_PEOPLE` | `5e9` | Jerus's smaller plan, in people: what the copy is scaled to reach. |
| 95 | `ScaleCheck.MONTHS_AT_SIZE` | `3` | Months the copy plays before it is saved. |
| 98 | `ScaleCheck.FIXTURE_MONTHS` | `120` | The founded city's age, in months, when it is saved and copied. |
| 101 | `ScaleCheck.FREE_PEOPLE` | `{ 5e9, 1e10 }` | Jerus's two plans, in people: the copies whose sectors are free (section 6). |
| 175 | `ScaleCheck.FREE_FIXTURE_MONTHS` | `545` | The month the playtest's city is copied at for section 6. |
| 178 | `ScaleCheck.FREE_MONTHS` | `6` | Months each free copy plays, every one timed. |
| 181 | `ScaleCheck.RELATIVE_RESIDUAL` | `1e-10` | The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. |
| 191 | `ScaleCheck.MONTH_MEDIAN_MS` | `1500` | The longest a free copy's median month may take, in milliseconds. |
| 625 | `ScaleCheck.AFFORDABILITY_WITHIN` | `.01` | How far a free copy's affordability pull may stand from the city's in any month (0.7.55): the rents' multiplier on the migrants' target (Migration.affordabilityPull()). |
| 921 | `ScaleCheck.INTENSIVE` | `Pattern.compile("(price\|Price\|rate\|Rate\|month\|Month\|Months\|share\|Share\|ratio\|Ratio\|targ...` | A name the scaler leaves alone wherever it meets one, unless EXTENSIVE names it: a price, a rate, a month, an id, a share, a ratio or a target, as scale_save.py's SKIP read them, and since 0.7.54 t... |
| 939 | `ScaleCheck.EXTENSIVE` | `java.util.Set.of("recognisedThisMonth", "escalationThisMonth", "repairsThisMonth", "las...` | THE NAMES THE PATTERN READ WRONG (0.7.54). |
| 943 | `ScaleCheck.TOP` | `{ "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxCharged", "...` | The save's top-level keys scaled: scale_save.py's list, less the works yard. |
| 958 | `ScaleCheck.NAMED` | `java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks", "sectorBooksBefo...` | The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. |

### SectorBooksCheck.java ([map](map/SectorBooksCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `SectorBooksCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### SectorFlowCheck.java ([map](map/SectorFlowCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 57 | `SectorFlowCheck.CENT` | `1e-5` | Money is in thousands, so this is a cent. |
| 99 | `SectorFlowCheck.ORDERS` | `{ { "House", "400" }, { "Convenience Store", "12" }, { "Diner", "2" }, { "Construction ...` | The fixture's orders, and whether each went on site. |
| 105 | `SectorFlowCheck.built` | `new java.util.LinkedHashMap<>()` | ...whether each went on site, as city() found it. |

### SectorStatementCheck.java ([map](map/SectorStatementCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 87 | `SectorStatementCheck.CENT` | `.00001` | A cent, in the model's thousands. |
| 125 | `SectorStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of one city's run, by identity. |
| 231 | `SectorStatementCheck.bondsSoonBefore` | `new LinkedHashMap<>()` | Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. |
| 520 | `SectorStatementCheck.SPLIT_ROWS` | `Set.of(SectorStatements.SOON_HEAD, SectorStatements.DEBT_SOON, SectorStatements.SOON, S...` | The rows R2 splits the debt into, which a load does not keep. |
| 672 | `SectorStatementCheck.NEW_FIELDS` | `{ "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts", "bondsWrit...` | The S2 fields a save from before 0.7.75 does not carry in its books' months. |

### StaleCheck.java ([map](map/StaleCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 158 | `StaleCheck.PLANTED` | `""" package ham.citybuildersim; /** * A fixture with three defects planted in it. * * I...` | Three planted lies: a stranded javadoc, a file that is not there, and a header out by one. |
| 185 | `StaleCheck.SOUND` | `""" package ham.citybuildersim; /** * A fixture with nothing wrong with it. * * The one...` | The same shapes, all of them true. |

### SupplierCreditCheck.java ([map](map/SupplierCreditCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 86 | `SupplierCreditCheck.FARM` | `Sectors.AGRICULTURE, LORRIES = Sectors.AUTOMOTIVE` | The two local suppliers the rule's fixtures name: the farms, which fill the shelf's meat, and the car makers, which fill the fleet's vans. |

### TradeCostCheck.java ([map](map/TradeCostCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `TradeCostCheck.DELIVERED` | `{ { Good.CROPS,.44,.28 }, { Good.GRAINS,.00080,.00050 }, { Good.BREAD,.00250,.00160 }, ...` | The delivered prices, as they were on 2026-09-16 before a line of this was written, hard-coded on purpose. |

### TreasuryCheck.java ([map](map/TreasuryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 99 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### WellCheck.java ([map](map/WellCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 126 | `WellCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder (section 6). |
| 129 | `WellCheck.WELL_TONNES` | `415` | A well's nameplate a month, the template's: 415 t, a hundred barrels a day. |
| 132 | `WellCheck.FILL` | `50_000` | The Strategic Reserve's fill a month in the sea sections (OilCheck 16's order): more than the wells lift, so the city buys every tonne of theirs at home. |
| 135 | `WellCheck.PLENTY` | `50_000_000` | The oil a town is handed: far more than its wells lift in the months a section runs, so the ground never limits them. |
| 907 | `WellCheck.GROUND` | `2 * WELL_TONNES` | Section 13's ground pool: two months of a land well's nameplate, so it is worked out inside the months the section runs. |
| 910 | `WellCheck.LAST_AT_SEA` | `100` | Section 13's offshore pool left for its other half: less than a month of the platform wells' lift, so it is worked out in one. |

### WorldCheck.java ([map](map/WorldCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 80 | `WorldCheck.SEEDS` | `{ Founding.DEFAULT_WORLD_SEED, 77, 2026 }` | The default world, and two more: 77, and 2026, whose search hands on to a second cell. |
| 83 | `WorldCheck.SAMPLE` | `200_000` | Points in each world's independent sample: 200,000 (a lake share's error about 0.1 point). |
| 86 | `WorldCheck.TOTALS_MS` | `200` | The spec's bound on the totals pass, in ms (measured 30). |
| 89 | `WorldCheck.TILE_MS` | `0.5` | The spec's bound on a tile, in ms (measured 0.07). |
| 92 | `WorldCheck.TILE_AGREES` | `0.995` | The share of a tile's plots its terrain must agree with the point function on. |
| 95 | `WorldCheck.COASTAL_CELLS` | `300` | Cells, neither all sea nor all land, whose fields are drawn to see where ore and oil lie: 300 (about 100,000 iron fields). |
| 98 | `WorldCheck.GRADE_CELLS` | `95` | The square of cells round the site whose oil fields are graded (section 7): 95 a side, 9,025 cells - some 20,000 fields, a grade's share then within a third of a point (one standard error); 30 a si... |
| 101 | `WorldCheck.NO_IRON_SEED` | `14` | A world whose first plot passing a founding's three tests has none of the old world's iron within World.SITE_IRON_KM (section 8): 14, the first such seed from 1 (fixW1-notes.md; 4127, 77 and 2026 f... |
| 104 | `WorldCheck.CLUSTER_CELLS` | `6` | The square of cells round the site whose iron fields' nearest neighbours are measured (section 8): 6 a side, 36 cells. |
| 107 | `WorldCheck.SHELF_WITHIN` | `.005` | How far the shelf's share of an independent sample of the sea may sit from SHELF_SHARE (spec-oil 4: half a point). |
| 110 | `WorldCheck.GRADE_WITHIN` | `.02` | How far each grade's share of the fields may sit from GRADE_SHARES (spec-oil 4: two points). |
| 609 | `WorldCheck.FIELD_COUNT_WITHIN` | `.01` | How far the world's count of fields over its new count may sit from FIELD_SCALE: 1% (each pool's fraction is drawn; the sparse resources' at-least-one adds a few, measured 0.3%). |
| 612 | `WorldCheck.CLUSTER_NN_OF_OLD` | `1.25` | How much farther than the old world's a field's nearest neighbour may lie on the mean, clustered: 1.25 times (est.: inside a cluster the fields lie at the old world's density, and a disc of ten los... |

### YearBookCheck.java ([map](map/YearBookCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |
| 935 | `YearBookCheck.COMMA_CO` | `"Acme, Inc."` | Two companies on the fixture's register, named the way a CSV has to quote. |
| 937 | `YearBookCheck.QUOTE_CO` | `"The \"Good\" Co"` | ...and the second, with a double quote in its name, which the CSV doubles. |

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


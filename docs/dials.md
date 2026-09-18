# The dials

Generated 2026-09-18 by `ham.citybuildersim.tools.Dials` - every `static final` constant in the tree, with the comment that explains it. Do not edit; regenerate with `Regenerate maps.bat`.

**682 constants in 203 files.**

## model (456 constants)

### AgeBand.java ([map](map/AgeBand.md))

| line | constant | value | says |
|---:|---|---|---|
| 180 | `AgeBand.MAX_MONTHLY_MORTALITY` | `.05` | No band may lose more than this in a single month, whatever modifies it. |

### Bank.java ([map](map/Bank.md))

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Bank.LEVERAGE` | `6` | How many times its deposits the bank will lend. |
| 101 | `Bank.CAPITAL_RATIO` | `.08` | Equity a bank must hold against its risk-weighted book. |
| 114 | `Bank.DEPOSITS_PER_BRANCH` | `250_000` | How much of a city's savings one branch can gather. |
| 137 | `Bank.PAID_IN_PER_BRANCH` | `32_000` | What the shareholders put up when a branch opens. |
| 165 | `Bank.RISK_CITY` | `.20` | The city cannot default on its own paper. |
| 168 | `Bank.RISK_BUSINESS` | `1.00` | A business can be restructured, and in this game regularly is. |
| 171 | `Bank.RISK_HOUSEHOLD` | `1.00` | ...and a family can be discharged. |
| 187 | `Bank.RISK_CARRY` | `1.00` | A foreign carry borrower, against the risk weights above. |
| 190 | `Bank.SHORTEST_WEIGHT` | `.40` | What a loan repaying tomorrow weighs against one repaying never. |
| 193 | `Bank.LONG_TERM_MONTHS` | `60` | Months of remaining term at which a loan weighs its full amount. |
| 208 | `Bank.EASY_STRAIN` | `.80` | Where the premium starts biting, as a share of capacity lent out. |
| 224 | `Bank.BUILD_AT_STRAIN` | `.70` | Where the private sector starts building, which is BEFORE it bites. |
| 227 | `Bank.HARD_STRAIN` | `1.50` | ...and where it is fully bitten. |
| 237 | `Bank.MAX_STRAIN_PREMIUM` | `.18` | The most the strain can add to any borrower's annual rate. |
| 294 | `Bank.PLACEMENT_RATE` | `DebtManager.WORLD_BASE_RATE` |  |
| 425 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 568 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 808 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 945 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 1331 | `Bank.DEPOSIT_PASS_THROUGH` | `.45` | What share of its INTEREST INCOME the bank passes on to its depositors. |
| 1365 | `Bank.FUNDING_SPREAD` | `.02` | Over the risk-free rate, for being a bank rather than a treasury. |
| 1368 | `Bank.FUNDING_STRETCH` | `.06` | ...and more, the further past its deposits it has reached. |
| 1565 | `Bank.RATE_STEPS` | `12` | How many candidate rates the bank considers between nothing and its ceiling. |

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
| 3292 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 3315 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 4258 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BuildingsTemplate.java ([map](map/BuildingsTemplate.md))

| line | constant | value | says |
|---:|---|---|---|
| 543 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 546 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

### BusinessDebtManager.java ([map](map/BusinessDebtManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 78 | `BusinessDebtManager.MIN_SPREAD` | `.01` | Floor over the government rate. |
| 81 | `BusinessDebtManager.MAX_SPREAD` | `.08` | Ceiling over the government rate - the "even worst case, not too bad" cap. |
| 99 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | Debt above this multiple of assets is not getting repaid, and both sides know it. |
| 132 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 135 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What a restructured borrower is left owing, as a multiple of its assets. |
| 147 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 181 | `BusinessDebtManager.SPREAD_PER_DEBT_TO_ASSETS` | `.06` | Extra annual interest per 1.0 of debt-to-assets. |
| 194 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the leverage spread and outside its cap, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 195 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` |  |
| 197 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` |  |
| 207 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 993 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### BusinessInvestment.java ([map](map/BusinessInvestment.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 46 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 49 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 52 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 55 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site. |
| 95 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, expressed as months of the city's whole construction output. |
| 98 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 252 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 255 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 258 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 266 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |

### CapitalFlows.java ([map](map/CapitalFlows.md))

| line | constant | value | says |
|---:|---|---|---|
| 51 | `CapitalFlows.APPETITE` | `3.0` | Foreign money held, per point of excess return, as a multiple of a year's output. |
| 54 | `CapitalFlows.MAX_SPREAD` | `.06` | Excess return above which appetite stops growing. |
| 57 | `CapitalFlows.ARRIVAL_SPEED` | `.08` | How much of the gap to its target the stock closes in a month, coming in. |
| 60 | `CapitalFlows.DEPARTURE_SPEED` | `.20` | ...and going out, which is faster, because leaving is always faster. |
| 74 | `CapitalFlows.MIN_STOCK` | `1` | Below this much foreign money, the flow is not worth modelling. |
| 110 | `CapitalFlows.MATERIAL_MONTHS` | `.5` | Months of output below which the hot money is too small to break anything. |
| 143 | `CapitalFlows.PANIC_BACKING` | `.25` | Reserves needed to back the hot money, as a share of it. |
| 146 | `CapitalFlows.PANIC_DEPRECIATION` | `.12` | A twelve-month fall in the currency past this reads as a run. |
| 149 | `CapitalFlows.PANIC_MONTHS` | `18` | How long a break lasts before money will look at the city again. |
| 161 | `CapitalFlows.PANIC_EXIT` | `.33` | The share that leaves each month while confidence is broken. |
| 214 | `CapitalFlows.CARRY_FULL_SPREAD` | `.02` | At this spread or better, the world wants all the spare book there is. |
| 217 | `CapitalFlows.CARRY_MAX_SHARE` | `.90` | ...and never quite all of it, because a bank at its limit lends to nobody. |
| 220 | `CapitalFlows.CARRY_BORROW_SPEED` | `.06` | How fast the book fills, and empties. |
| 221 | `CapitalFlows.CARRY_REPAY_SPEED` | `.20` |  |

### CareType.java ([map](map/CareType.md))

| line | constant | value | says |
|---:|---|---|---|
| 114 | `CareType.SENIOR_NEED_AGAINST_AN_ELDER` | `.19` | 5.5% of 70-84 in care against 29.6% of the over-85s - see placesPerHead. |

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
| 30 | `Currency.NAME` | `"Danzik dollar"` | The city's own money. |
| 31 | `Currency.PLURAL` | `"Danzik dollars"` |  |
| 32 | `Currency.CODE` | `"DZD"` |  |
| 35 | `Currency.SYMBOL` | `"$"` | Written alone, where nothing foreign is in sight. |
| 38 | `Currency.QUALIFIED` | `"D$"` | ...and written where a foreign figure is on the same screen. |
| 41 | `Currency.FOREIGN_NAME` | `"US dollar"` | The world's money, which the game holds exactly one of. |
| 42 | `Currency.FOREIGN_CODE` | `"USD"` |  |
| 43 | `Currency.FOREIGN_SYMBOL` | `"US$"` |  |

### DebtManager.java ([map](map/DebtManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 43 | `DebtManager.MIN_POLICY_RATE` | `.0` |  |
| 44 | `DebtManager.MAX_POLICY_RATE` | `.25` |  |
| 75 | `DebtManager.CITY_DISCOUNT` | `.02` | WHAT THE CITY'S OWN PAPER IS QUOTED UNDER THE POLICY RATE - and it is the one number in this file that does not describe anything real. |
| 78 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 81 | `DebtManager.INFLATION_TARGET` | `.02` | What the city is trying to hold inflation at. |
| 91 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 169 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 203 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 206 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 209 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 345 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 348 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 351 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 358 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 361 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 364 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 367 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 370 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 373 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 999 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 94 | `Denomination.UNLOCK_AT` | `10.0` | How far prices have to have risen before the button appears. |
| 97 | `Denomination.FACTORS` | `{ 10, 100, 1000 }` | The factors the player may choose between. |
| 109 | `Denomination.MAX_UNIT` | `1e12` | The ceiling on the unit, and it is a numeric guard rather than a policy. |

### EconomyManager.java ([map](map/EconomyManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 473 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, BuildingTy...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 485 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1283 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### Education.java ([map](map/Education.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 155 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 168 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 171 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 189 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 198 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 816 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

### Equity.java ([map](map/Equity.md))

| line | constant | value | says |
|---:|---|---|---|
| 96 | `Equity.COMPANIES` |  | The companies, in register order: every sector in the registry's order, then the bank. |
| 97 | `Equity.BANK` |  |  |
| 114 | `Equity.PAYOUT` | `.40` | The share of a positive month's net income paid to the owners. |
| 117 | `Equity.FOUNDING_PRICE` | `1.0` | A founding share: a thousand dollars, in the game's thousands. |
| 149 | `Equity.RECORD_MONTHS` | `12` | Months on the books before a company has a record to be judged on. |
| 152 | `Equity.GOOD_MONTHS` | `9` | Profitable months of the last twelve that make a good year. |
| 155 | `Equity.BAD_MONTHS` | `6` | ...and the most a bad year has. |
| 158 | `Equity.BASE_EQUITY_SHARE` | `.30` | What a steady business keeps as equity: the rest is leverage. |
| 161 | `Equity.RISK_SLOPE` | `.20` | How much the target rises per unit of income swing (std dev over \|mean\|). |
| 163 | `Equity.MAX_EQUITY_SHARE` | `.70` |  |
| 166 | `Equity.NEW_EQUITY_SHARE` | `.50` | A new company's plans are this much equity, whatever its assets say. |
| 169 | `Equity.HORIZON_YEARS` | `3` | In good times, the years of expansion a company raises for ahead. |
| 172 | `Equity.UNDER_TARGET` | `.10` | Under target by this much before a normal year raises instead of borrows. |
| 175 | `Equity.FOREIGN_PREMIUM` | `.03` | What the world wants over its own rate to buy a share here, annual. |
| 715 | `Equity.SLOTS_BEFORE_DESK` | `RECORD_MONTHS * 2 + 10` | Slots a company before the desk (2026-09-10, night). |
| 717 | `Equity.SLOTS` | `SLOTS_BEFORE_DESK + 2` |  |

### Exchange.java ([map](map/Exchange.md))

| line | constant | value | says |
|---:|---|---|---|
| 113 | `Exchange.SPREAD` | `.02` | Ask over bid, as a share of the mid. |
| 116 | `Exchange.PRESSURE` | `.25` | How far a full position moves the quote from fair value. |
| 119 | `Exchange.POSITION_LIMIT` | `.25` | The desk's position in one company, as a share of the bank's equity at fair value. |
| 131 | `Exchange.BOOK_LIMIT` | `.50` | ...and its whole book, all companies together, as a share of the bank's equity at fair value. |
| 134 | `Exchange.FLOOR` | `.50, CEILING = 5.0` | The quote never leaves this band round fair value, whatever the book. |
| 143 | `Exchange.MIN_LIMIT_OF_FLOAT` | `.02` | The quote reads the desk's position against at least this share of the float, however small the bank. |
| 146 | `Exchange.CAPACITY` | `2.0` | The most the desk will hold, as a multiple of its position limit. |
| 149 | `Exchange.DEMAND_DECAY` | `.50` | What is left of a month's unfilled demand the next month, in the quote. |
| 152 | `Exchange.FOREIGN_SPEED` | `.10` | What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. |
| 155 | `Exchange.FOREIGN_TOLERANCE` | `.10` | ...and it holds still while the yield is within this of the hurdle. |
| 158 | `Exchange.MONTHLY_SHARE_OF_EXCESS` | `.05` | A household's monthly buying: this share of what is past its cushion. |
| 161 | `Exchange.HOUSEHOLD_PREMIUM` | `.01` | ...into a yield at least this far over the deposit rate, annual. |
| 164 | `Exchange.BUYBACK_CUSHION_MONTHS` | `6` | A company keeps this many months of operating cost before it buys back. |
| 167 | `Exchange.OVER_TARGET` | `.10` | Equity this far past target before a company buys back, as a share of assets. |
| 187 | `Exchange.MIN_EXCESS` | `1e-9` |  |
| 229 | `Exchange.TIED_YIELD` | `1e-9` |  |
| 232 | `Exchange.BUYBACK_PACE` | `.10` | The most a company retires in a year, as a share of what it is worth. |
| 235 | `Exchange.BUYBACK_TOLERANCE` | `.10` | A company buys back only while the ask is within this of fair value; past it, the money is a special dividend. |
| 238 | `Exchange.SPLIT_AT` | `100` | A share quoted at this many times its founding price is split; at one over it, consolidated. |
| 264 | `Exchange.MIN_FAIR` | `1e-9` | The least a share may be worth before the desk treats the company as worthless. |
| 300 | `Exchange.MIN_DEALER_EQUITY` | `1.0` |  |
| 898 | `Exchange.SLOTS_BEFORE_SPLITS` | `3` | Slots per company before the split factor joined (the exchange's first night). |
| 905 | `Exchange.SLOTS` | `SLOTS_BEFORE_SPLITS + 1` | The quote, the demand it carries and the split factor are STOCKS: the next month trades at the first two before anything re-quotes, and the price history is read through the third. |

### FamilyModel.java ([map](map/FamilyModel.md))

| line | constant | value | says |
|---:|---|---|---|
| 90 | `FamilyModel.REFORMING_EACH_MONTH` | `.01` | The share of households that re-form on their own each month. |
| 155 | `FamilyModel.SEEKERS` | `Seeker.values().length` |  |
| 1001 | `FamilyModel.STUDIO_MAX_SIZE` | `2` | The largest unit that counts as a studio. |
| 1482 | `FamilyModel.MAX_SHARING` | `.85` | Not everybody doubles up, however dear the rent. |
| 1543 | `FamilyModel.COUPLED_SENIORS` | `.55` | What share of a retired band lives as a couple rather than alone. |
| 1544 | `FamilyModel.COUPLED_ELDERS` | `.25` |  |
| 1670 | `FamilyModel.LEGACY_SHAPES` | `{ "SENIOR_ALONE", "SENIOR_COUPLE", "SINGLE_ADULT", "COUPLE", "SINGLE_PARENT", "COUPLE_B...` | The shapes a save written before the names travelled must be read with. |
| 1692 | `FamilyModel.OUTSIDE_SLOTS` | `outsideSlots(AgeBand.values().length)` | What the people outside the families add to the save: see toSaveArray(). |
| 1737 | `FamilyModel.KIN_SLOTS` | `kinSlots(AgeBand.values().length)` |  |
| 1754 | `FamilyModel.MEMORY_SLOTS` | `FamilyStructure.values().length * PayTier.values().length + 1 + 4` | ...and what the households remember: the formed matrix, whether there is one, the month's four counts. |

### ForeignAccounts.java ([map](map/ForeignAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 72 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 94 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 97 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 100 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 110 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 130 | `ForeignAccounts.MIN_RATE` | `.01` | UNCAPPED, and these are now a numeric guard rather than a policy. |
| 131 | `ForeignAccounts.MAX_RATE` | `100.0` |  |
| 209 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 264 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 326 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 329 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 384 | `ForeignAccounts.RATE_PULL` | `6.0` | How far the city's own rate is above the world's, and what that is worth to the currency. |
| 387 | `ForeignAccounts.MAX_RATE_PRESSURE` | `.8` | Most of the pressure a rate differential alone can produce. |
| 603 | `ForeignAccounts.COVER_WINDOW` | `12` | Months of imports the reserve would cover. |

### Formats.java ([map](map/Formats.md))

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |

### Game.java ([map](map/Game.md))

| line | constant | value | says |
|---:|---|---|---|
| 353 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 2656 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 3247 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4261 | `Game.EMERGENCY_NOTE_MONTHS` | `6` | Term of the note the city is forced into when it cannot pay its bills. |
| 4276 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 4285 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 4293 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 5641 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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

### GameVersion.java ([map](map/GameVersion.md))

| line | constant | value | says |
|---:|---|---|---|
| 110 | `GameVersion.VERSION` | `"0.6.7"` | 0.6.7 - THE FIFTEENTH SECTOR, AND A MEAL OUT IS FOOD. |
| 576 | `GameVersion.SAVE_FORMAT` | `27` |  |
| 579 | `GameVersion.FIRST_SECTOR_FORMAT` | `21` | The first format a sector can be read out of. |
| 581 | `GameVersion.NAME` | `"CityBuilderSim"` |  |

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
| 63 | `Healthcare.FOUNDING_CITY` | `1200` | How many residents the founding endowment was meant to serve. |
| 66 | `Healthcare.FOUNDING_PLOTS` | `2500` | Graves in the old churchyard. |
| 111 | `Healthcare.GENERAL_FEE` | `.010` |  |
| 112 | `Healthcare.CHILDCARE_FEE` | `.150` |  |
| 113 | `Healthcare.SENIOR_FEE` | `.300` |  |
| 135 | `Healthcare.BURIAL_FEE` | `3.000` |  |
| 136 | `Healthcare.CREMATION_FEE` | `.900` |  |
| 197 | `Healthcare.BURIAL_SAVING_MONTHS` | `120` | How long a household is assumed to be putting money aside for a funeral. |
| 216 | `Healthcare.MAX_BACKLOG_MONTHS` | `24` | How far behind a city can get before it starts improvising. |
| 256 | `Healthcare.CHILDCARE_SWING` | `40` | Children, and it is enormous - per Jerus, "really really really". |
| 277 | `Healthcare.SENIOR_SWING` | `1.35` | Seniors, and it stays gentle. |
| 293 | `Healthcare.ELDER_SWING` | `1.80` | And what it is worth to the over-85s, which is more. |
| 304 | `Healthcare.CHILDCARE_BIRTH_BONUS` | `1.0` | How much more a city with childcare gives birth. |
| 617 | `Healthcare.STRAINED` | `.90` | Past this share of the ovens' throughput, say so. |
| 620 | `Healthcare.PLOT_WARNING_MONTHS` | `60` | Warn once the ground will not last this long at the current rate. |

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
| 93 | `Household.STUDENT_LOAN_MONTHS` | `114` | How long a graduate takes to repay a student loan, in months: nine and a half years, the Canada Student Loan standard term (Alberta Student Aid's repayment page; the six-month grace is not modelled). |

### HouseholdAccounts.java ([map](map/HouseholdAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 367 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 370 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 371 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 372 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 374 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 375 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 378 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 801 | `HouseholdAccounts.STATE_SCALARS` | `16, STATE_ROWS = 15` | Scalars and row arrays in the statement's state since 2026-09-16. |
| 804 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

### HouseholdBalance.java ([map](map/HouseholdBalance.md))

| line | constant | value | says |
|---:|---|---|---|
| 86 | `HouseholdBalance.ROWS` | `Household.ROWS` | One row per pay tier, the retired, and since 2026-09-11 the out of work, the students and the orphans - the same shape as HouseholdAccounts. |
| 89 | `HouseholdBalance.ROWS_BEFORE_OUTSIDE` | `Household.RETIRED_ROW + 1` | The rows before the people outside the families had books: the six tiers and the retired. |
| 103 | `HouseholdBalance.CREDIT_LIMIT_MONTHS` | `6` | How many months of take-home a household can owe before the lender stops. |
| 112 | `HouseholdBalance.BASE_SPREAD` | `.03` | What a household pays over the risk-free rate, before any risk premium. |
| 123 | `HouseholdBalance.RISK_SLOPE` | `.015` | Extra spread per month of income owed. |
| 126 | `HouseholdBalance.MAX_RATE` | `.36` | Nobody is charged more than this, however deep they are. |
| 145 | `HouseholdBalance.MARGINAL_PROPENSITY` | `.80` | How much of the money above subsistence a household spends. |
| 190 | `HouseholdBalance.WEALTH_SPENT_A_MONTH` | `.0033` | What share of its net worth a household spends in a month, over and above what it spends out of income. |
| 210 | `HouseholdBalance.LUXURY_SHARE_OF_SURPLUS` | `.5` | What share of the income a household does NOT spend on food goes over a luxury counter instead of into the bank. |
| 225 | `HouseholdBalance.MEAL_SHARE_OF_SURPLUS` | `.25` |  |
| 236 | `HouseholdBalance.MEAL_SHARE_OF_WEALTH` | `.5` | ...and the share of a FORTUNE'S monthly spend that goes on a table. |
| 252 | `HouseholdBalance.MOST_MEALS_EATEN_OUT` | `1 / 3.0` |  |
| 290 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 324 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 327 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 330 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 333 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1167 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 1179 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 1204 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 1263 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 1289 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 1348 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 1357 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 1599 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 2220 | `HouseholdBalance.MIN_MOVE` | `1e-12` |  |
| 2615 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 2618 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 2822 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 2825 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 2828 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 2831 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 2834 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 2845 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 2847 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_MEALS + 1` |  |

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
| 293 | `LabourMarket.COST_OF_LIVING_PASS_THROUGH` | `1.0` | How much of a rise in prices wages eventually chase. |
| 309 | `LabourMarket.DRIFT_PER_MONTH` | `1.0 / 24` | How fast they chase it. |

### LandManager.java ([map](map/LandManager.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 53 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres. |
| 86 | `LandManager.COST_GROWTH_PER_BLOCK` | `.02` | Each block bought makes the next this much dearer - annexing outward. |
| 98 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 417 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

### LandMarket.java ([map](map/LandMarket.md))

| line | constant | value | says |
|---:|---|---|---|
| 54 | `LandMarket.LISTING_SIZE` | `9` | Plots on offer at any one time. |
| 64 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands. |
| 122 | `LandMarket.PREMIUM_PER_BLOCK_OWNED` | `.008` | Each block already owned makes the next offer this much dearer. |
| 125 | `LandMarket.PREMIUM_PER_1000_PEOPLE` | `.05` | ...and so does each thousand residents. |
| 137 | `LandMarket.IRON_PRICE_PER_TONNE` | `.0004` | What the seller charges for the ore, per tonne in the ground, in thousands. |
| 149 | `LandMarket.SQ_FT_PER_DEPOSIT` | `400_000` | Land a single mine occupies, and therefore the room one deposit needs. |
| 152 | `LandMarket.EXTRA_DEPOSIT_CHANCE` | `.28` | Chance that a parcel with ore has one MORE site, each time it is asked. |
| 155 | `LandMarket.MAX_DEPOSITS` | `4` | However big the tract, this many sites is the most it will ever carry. |
| 192 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 195 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 207 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 221 | `LandMarket.MIN_BLOCKS` | `1` | The smallest thing the land office will sell, ever: one city block. |
| 236 | `LandMarket.BLOCKS_PER_FLOOR_STEP` | `40` | Blocks the city must already own before the floor rises another block. |
| 243 | `LandMarket.MAX_MIN_BLOCKS` | `15` | A ceiling on the floor. |
| 256 | `LandMarket.SEED` | `705_398_211_733L` | Fixed seed. |
| 549 | `LandMarket.FIELDS_PER_PARCEL` | `5` | Fields written per parcel. |
| 564 | `LandMarket.LISTING_FORMAT_MARKER` | `- FIELDS_PER_PARCEL` | Marks a listing written with deposit counts, and says how wide it is. |

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
| 44 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 266 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank. |

### NationalAccounts.java ([map](map/NationalAccounts.md))

| line | constant | value | says |
|---:|---|---|---|
| 82 | `NationalAccounts.HISTORY_MONTHS` | `120` |  |

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
| 230 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 409 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 415 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 444 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 458 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 1074 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 1077 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

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
| 26 | `StudentHousehold.UNLIMITED` | `1e15` | A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. |

### TaxPolicy.java ([map](map/TaxPolicy.md))

| line | constant | value | says |
|---:|---|---|---|
| 50 | `TaxPolicy.DEFAULT_INCOME_TAX` | `.15` | Where income tax started before it was a dial. |
| 60 | `TaxPolicy.DEFAULT_PROPERTY_TAX` | `.015` | 1.5% a year, near the real-world average. |
| 63 | `TaxPolicy.MAX_PROPERTY_TAX` | `.10` | Nobody has ever paid 100% property tax and the game should not model it. |
| 66 | `TaxPolicy.MAX_INCOME_TAX` | `.60` | Above this, income tax stops being a policy and starts being confiscation. |
| 75 | `TaxPolicy.MAX_OFFSET` | `.30` | How far a band or sector may be moved from the city rate, either way. |
| 117 | `TaxPolicy.DEFAULT_FARMLAND_RELIEF` | `1.0` |  |
| 120 | `TaxPolicy.FARM_SECTOR` | `Sectors.AGRICULTURE` | The sector whose ground the relief applies to. |
| 159 | `TaxPolicy.MAX_CONTRIBUTION` | `.20` | Nobody hands over more than a fifth of a wage, whatever the deficit. |
| 162 | `TaxPolicy.MAX_REPLACEMENT` | `1.00` | A pension of more than one unskilled wage is a wage, not a pension. |
| 195 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currenc... |
| 199 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 202 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 205 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage. |
| 270 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 273 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | Past this nobody rides at all, as a multiple of the default. |
| 314 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |

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
| 504 | `Unemployment.STATE_LENGTH` | `EI_MONTHS * 2 + 6 + PayTier.values().length * 2 + 12` |  |

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
| 112 | `WorldEconomy.PERSISTENCE` | `.90` | How much of last month's rate survives into this one. |
| 168 | `WorldEconomy.LOW_BIAS` | `2.0` | RETIRED 2026-09-13, kept because the reasoning is still true of the band it was written for. |
| 170 | `WorldEconomy.SEED` | `0x5F3A91C7L` |  |
| 214 | `WorldEconomy.TREND_INFLATION` | `.0` | The world's long-run trend, against which the wandering rate is a cycle. |
| 217 | `WorldEconomy.TREND_PULL` | `.006` | How hard the level is pulled back to trend. |
| 319 | `WorldEconomy.LEVEL_RING` | `13` |  |
| 384 | `WorldEconomy.LEGACY_MEAN_INFLATION` | `.0333` | The mean every city founded before 2026-09-13 grew up in. |

### YearBook.java ([map](map/YearBook.md))

| line | constant | value | says |
|---:|---|---|---|
| 59 | `YearBook.MONTHS_A_YEAR` | `12` |  |
| 60 | `YearBook.MONTHS_A_DECADE` | `120` |  |
| 207 | `YearBook.RULES` | `rules()` |  |
| 208 | `YearBook.PREFIXES` | `prefixRules()` |  |

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
| 49 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The smallest share of payroll construction pays when it has no work. |

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

## interface (138 constants)

### BankScreen.java ([map](map/BankScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `BankScreen.BANK_HOME` | `"The gauge"` | null is the landing |
| 70 | `BankScreen.BANK_LEND_PAGES` | `{ "The gauge", "The two limits", "Another branch" }` |  |
| 72 | `BankScreen.BANK_OWED_PAGES` | `{ "By borrower", "In trouble" }` |  |
| 73 | `BankScreen.BANK_MONEY_PAGES` | `{ "Deposits", "Funding" }` |  |
| 74 | `BankScreen.BANK_BOOKS_PAGES` | `{ "Income", "Balance sheet" }` |  |
| 75 | `BankScreen.BANK_PAST_PAGES` | `{ "Lending", "Strain", "Capital" }` |  |
| 346 | `BankScreen.GAUGE_MAX` | `2.0` | The top of the gauge's scale, as a multiple of capacity. |

### BuildScreen.java ([map](map/BuildScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 188 | `BuildScreen.BUILD_HOME` | `"Residential"` | Which category the player was last looking at. |

### FinancesScreen.java ([map](map/FinancesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 74 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 76 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 77 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 91 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1000, "months", "No coupon at all.The ...` |  |
| 479 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |

### GovernmentScreen.java ([map](map/GovernmentScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 66 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` |  |

### HistoryScreen.java ([map](map/HistoryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 80 | `HistoryScreen.MAX_PLOT_POINTS` | `400` | Above this many points a line is bucket-averaged; see decimate(). |
| 92 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheMarket(new Trace[] { new Trace("gdp", "GDP", "MON...` |  |
| 268 | `HistoryScreen.TRACE_COLOURS` | `{ "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454", "#ce93d8", "#4dd0e1", "#d4e157", "#c8b0a5" }` | Eight, then it wraps - and the legend swatch uses the same list. |
| 308 | `HistoryScreen.GRAPH` | `760` | How wide this one screen runs. |
| 313 | `HistoryScreen.PRESETS` | `{ new Preset("How it is going", "output, people, and what money costs", new String[] { ...` |  |
| 1384 | `HistoryScreen.TABLE_WIDTH` | `660` | How wide the paragraph above the buyback table wraps. |

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
| 232 | `Palette.RAMP_REST` | `"#5c6b75"` | Everything too small to have its own step. |
| 235 | `Palette.RING` | `26` | How thick a donut's ring is drawn, and how wide the hole is. |
| 249 | `Palette.MONO` | `"'Courier New'"` | Figures. |
| 253 | `Palette.GLYPH` | `"'Segoe UI Symbol', 'Segoe UI', sans-serif"` | Glyphs - the rail, the envelope, the round buttons. |
| 256 | `Palette.SIZE_HEADLINE` | `28` | The date and the cash: the two figures readable from across the room. |
| 259 | `Palette.SIZE_TITLE` | `20` | A screen's title. |
| 262 | `Palette.SIZE_LEAD` | `17` | A figure that is the point of its panel. |
| 265 | `Palette.SIZE_SECTION` | `14` | A section heading inside a screen. |
| 268 | `Palette.SIZE_HEADING` | `12` | A panel's own heading. |
| 271 | `Palette.SIZE_BODY` | `11` | Body text, and the figure in a row. |
| 274 | `Palette.SIZE_LABEL` | `10` | The label in a row, and a button in a dense list. |
| 277 | `Palette.SIZE_CAPTION` | `9` | A caption under something, and a unit after something. |
| 285 | `Palette.GAP_TIGHT` | `4` |  |
| 286 | `Palette.GAP` | `8` |  |
| 287 | `Palette.GAP_LOOSE` | `12` |  |
| 288 | `Palette.GAP_SECTION` | `20` |  |
| 291 | `Palette.RADIUS` | `4` | Corner of a block, a chip, a control. |
| 294 | `Palette.RADIUS_TIGHT` | `3` | Corner of something small - a row, a badge. |
| 304 | `Palette.RAIL` | `46` | The navigation rail, on the city panel's ground. |
| 307 | `Palette.CITY_PANEL` | `290` | The city panel, not counting the rail. |
| 310 | `Palette.BUILD_PANEL` | `280` | The construction panel down the right. |
| 313 | `Palette.STRIP` | `72` | The strip under the stage holding the dome and the time controls. |
| 316 | `Palette.BUILD_ROW` | `400` | A building row, so the price column lines up down the list. |
| 319 | `Palette.INBOX` | `470` | The inbox, sized to the 62-character lines the notices are written at. |

### PeopleScreen.java ([map](map/PeopleScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 737 | `PeopleScreen.PYRAMID_BAR` | `200` | How wide the age bars are drawn. |
| 1513 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 1514 | `PeopleScreen.SHAPE_COL` | `168` |  |

### Pieces.java ([map](map/Pieces.md))

| line | constant | value | says |
|---:|---|---|---|
| 614 | `Pieces.TILE_WIDTH` | `250` | A tile's width. |
| 616 | `Pieces.TILE_HEIGHT` | `232` | A tile's height, the same for a card and a plot. |
| 618 | `Pieces.TILE_GAP` | `10` | The gap between tiles. |

### PolicyScreen.java ([map](map/PolicyScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 74 | `PolicyScreen.POLICY_HOME` | `"Everything"` | null is the landing |
| 91 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 93 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 94 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 95 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Tuition", "Subsidies" }` |  |
| 177 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 180 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 182 | `PolicyScreen.LADDER_READ` | `118` |  |

### SectorScreen.java ([map](map/SectorScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 64 | `SectorScreen.SECTOR_HOME` | `"Operations"` |  |
| 67 | `SectorScreen.SECTOR_PAGES` | `{ "Operations", "Income", "Balance sheet", "Cash & debt", "Investors" }` |  |

### ServicesScreen.java ([map](map/ServicesScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 85 | `ServicesScreen.SERVICE_HOME` | `"General care"` |  |
| 125 | `ServicesScreen.INFRA_PAGES` | `{ "Roads", "Transit", "The railway", "Freight" }` |  |

### Statement.java ([map](map/Statement.md))

| line | constant | value | says |
|---:|---|---|---|
| 32 | `Statement.STATEMENT` | `560` | How wide a statement is. |
| 130 | `Statement.BOOK_NOW` | `116` |  |
| 132 | `Statement.BOOK_THEN` | `104` |  |
| 340 | `Statement.CLOSED` | `"\u25b8"` |  |
| 342 | `Statement.OPENED` | `"\u25be"` |  |

### SummaryScreen.java ([map](map/SummaryScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 148 | `SummaryScreen.PANEL_LABEL` | `"#78909c"` |  |
| 149 | `SummaryScreen.PANEL_VALUE` | `"#eceff1"` |  |
| 150 | `SummaryScreen.PANEL_GOOD` | `"#5fd68a"` |  |
| 151 | `SummaryScreen.PANEL_WARN` | `"#ffb454"` |  |
| 152 | `SummaryScreen.PANEL_BAD` | `"#ff6b6b"` |  |
| 383 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "safety", "re...` | Every section key, so open-all does not have to be kept in step by hand. |

### TradeScreen.java ([map](map/TradeScreen.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `TradeScreen.TRADE_HOME` | `"The picture"` | null is the landing |
| 70 | `TradeScreen.TRADE_MONTH_PAGES` | `{ "The picture", "The two accounts" }` |  |
| 71 | `TradeScreen.TRADE_VAULT_PAGES` | `{ "What is yours", "Cover", "Exchange" }` |  |
| 72 | `TradeScreen.TRADE_MONEY_PAGES` | `{ "The rate", "What is moving it" }` |  |
| 73 | `TradeScreen.TRADE_GOODS_PAGES` | `{ "In and out", "Since founding" }` |  |

### UserInterface.java ([map](map/UserInterface.md))

| line | constant | value | says |
|---:|---|---|---|
| 189 | `UserInterface.STAGE` | `"#111a24"` | The middle of the window: the blackish blue everything else sits on. |
| 311 | `UserInterface.SECONDS_PER_MONTH` | `5.0` | Real seconds a month takes at 1x. |
| 321 | `UserInterface.SPEEDS` | `{ 0.1, 0.25, 0.5, 1, 2, 5, 10, 20, 50 }` | The ladder the speed slider sticks to. |
| 322 | `UserInterface.NORMAL_SPEED` | `3` | 1x |
| 343 | `UserInterface.REDRAW_EVERY` | `.12` | HOW OFTEN THE SCREEN MAY BE REBUILT WHILE TIME RUNS. |
| 977 | `UserInterface.WHEEL_STEP` | `48` | The least the first wheel event of a gesture may move the page, in pixels (since 2026-09-18 the first only; see scrollPageBy). |
| 1375 | `UserInterface.WHEEL_GESTURE_GAP_NANOS` | `150_000_000L` | A wheel event this long after the last one starts a new gesture; a burst is closer than this. |
| 1808 | `UserInterface.SAVED_AT` | `java.time.format.DateTimeFormatter.ofPattern("d MMM HH:mm")` |  |
| 2951 | `UserInterface.RAIL_WIDTH` | `46` | Wide enough for a glyph and its highlight, narrow enough to be an edge. |
| 2954 | `UserInterface.STRIP_HEIGHT` | `72` | The strip under the stage that holds the dome and the time controls. |
| 3324 | `UserInterface.INBOX_WIDTH` | `530` | See refreshInbox: sized to the notice bodies, not to the corner. |

## harnesses (43 constants)

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

### BusinessServicesCheck.java ([map](map/BusinessServicesCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `BusinessServicesCheck.CONTACT` | `"Contact Centre"` | The three rungs, and the band each is meant to employ. |
| 59 | `BusinessServicesCheck.SHARED` | `"Shared Services Centre"` |  |
| 60 | `BusinessServicesCheck.OFFICE` | `"Engineering Services Office"` |  |

### ConsumptionCheck.java ([map](map/ConsumptionCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 38 | `ConsumptionCheck.LADDER` | `{ 1, 2, 5, 10, 20, 27, 50, 90, 200, 500, 2_000 }` |  |

### CreditCheck.java ([map](map/CreditCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 20 | `CreditCheck.IND` | `Sectors.INDUSTRY` |  |

### EducationCheck.java ([map](map/EducationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 36 | `EducationCheck.OUT` | `System.out` |  |
| 37 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

### ExchangeCheck.java ([map](map/ExchangeCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 42 | `ExchangeCheck.W` | `DebtManager.WORLD_BASE_RATE` |  |
| 43 | `ExchangeCheck.N` | `Equity.COMPANIES.length` |  |

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

### LongPlaytest.java ([map](map/LongPlaytest.md))

| line | constant | value | says |
|---:|---|---|---|
| 45 | `LongPlaytest.TARGET_MONTHS` | `4000` |  |
| 55 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 161 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 163 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 807 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasional"))` | True when this run is played by somebody paying attention. |
| 1189 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 1399 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 1431 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |

### ManufacturingCheck.java ([map](map/ManufacturingCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 61 | `ManufacturingCheck.SHOP` | `"Fabrication Shop"` |  |
| 62 | `ManufacturingCheck.WORKS` | `"Fabrication Works"` |  |
| 63 | `ManufacturingCheck.MACH` | `"Machine Works"` |  |

### PopulationCheck.java ([map](map/PopulationCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `PopulationCheck.ADULT_MIX` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share these fixtures run at. |

### RestaurantsCheck.java ([map](map/RestaurantsCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 71 | `RestaurantsCheck.DINER` | `"Diner"` |  |
| 72 | `RestaurantsCheck.RESTAURANT` | `"Restaurant"` |  |

### SectorBooksCheck.java ([map](map/SectorBooksCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 88 | `SectorBooksCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### TradeCostCheck.java ([map](map/TradeCostCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 67 | `TradeCostCheck.DELIVERED` | `{ { Good.CROPS,.44,.28 }, { Good.GRAINS,.00080,.00050 }, { Good.BREAD,.00250,.00160 }, ...` | The delivered prices, as they were on 2026-09-16 before a line of this was written, hard-coded on purpose. |

### TreasuryCheck.java ([map](map/TreasuryCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 58 | `TreasuryCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

### YearBookCheck.java ([map](map/YearBookCheck.md))

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |

## tools (6 constants)

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

### MonthOrder.java ([map](map/MonthOrder.md))

| line | constant | value | says |
|---:|---|---|---|
| 30 | `MonthOrder.DEFAULT` | `"Game.nextMonth,Game.startOfMonthUpdate,SimulationEngine.simulateMonth," + "SimulationE...` |  |

### SourceTree.java ([map](map/SourceTree.md))

| line | constant | value | says |
|---:|---|---|---|
| 84 | `SourceTree.AREAS` | `{ "model", "sectors", "interface", "harnesses", "tools" }` |  |


# TaxPolicy.java - 2,023 lines · 124 methods · 48 constants · model

`ham/citybuildersim/TaxPolicy.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The city's tax rates - the revenue half of what the player actually decides.
> 
> Both rates used to be one hardcoded field on EconomyManager. They live here
> together because they are the same kind of thing, they are both set by the
> player, they both have to survive a save, and because the two of them are
> only interesting relative to each other: an income tax takes a share of what
> a business earned, a property tax takes a share of what it owns whether it
> earned anything or not. Which one the city leans on decides who actually pays
> for it.
> 
> ANNUAL IN, MONTHLY OUT
> 
> Property tax is quoted annually, because that is how anyone who has ever paid
> one thinks about it, and charged monthly, because that is the game's tick.
> The conversion lives here and nowhere else - getting a 1.5%/year rate charged
> as 1.5%/month would be an eighteen-fold error that still looks like a
> plausible number on screen, so there is exactly one place it can be got wrong.
> 
> Income tax is not converted: it is a share of a month's income, so the rate
> applies to the month directly.
> 
> ======================================================================
> CITY RATES, AND OFFSETS FROM THEM
> ======================================================================
> 
> There are four base rates - one per tax: profit, sales, wage and property -
> and then every band and every sector carries an OFFSET in rate points from
> its own tax's base. Industry at -0.03 on profit pays three points under
> whatever the profit rate is.
> 
> Jerus's call, and it is the right one for a game where a base is a lever the
> player pulls often: raise it and every sector you have customised keeps its
> relative treatment instead of being silently left behind. The cost is that a
> rate on screen is arithmetic rather than a number, so every screen shows the
> offset AND what it resolves to.
> 
> THREE BASES WHERE THERE WAS ONE (0.7.4). Until 0.7.4 profit, sales and wage
> all moved off ONE city income rate, so raising sales tax for every sector
> meant fifteen offsets or the city rate, and the city rate dragged profit and
> wage with it. Jerus: "what about just increasing sale tax for all at the same
> time? currently that's a hassle, i want to be able to do that for every type
> of tax, even wage tax". Each of the three has its own base now, and
> setIncomeTaxRate() is what his old city rate became: every income tax at
> once, all three bases set to one number.
> 
> RESOLUTION HAPPENS HERE, ONCE. effective*() clamps to [0, max]. No caller
> adds an offset itself: an offset that escapes clamping is a negative tax rate,
> which is the city paying businesses to trade, and it would show up as revenue
> appearing from nowhere three layers away from the line that caused it.
> 
> DEFAULTS ARE ALL ZERO, and the three bases open equal, which makes every
> effective rate the one income rate and reproduces the single-rate behaviour
> this replaced, exactly.

**Uses:** [DecisionLog](DecisionLog.md) (68), [PolicyDates](PolicyDates.md) (62), [EducationType](EducationType.md) (17), [WageBand](WageBand.md) (10), [Sector](Sector.md) (10), [Unemployment](Unemployment.md) (6), [PayTier](PayTier.md) (6), [SocialSecurity](SocialSecurity.md) (4), [PriceLevel](PriceLevel.md) (3), [JobType](JobType.md) (3), [WageLadder](WageLadder.md) (2), [Sectors](Sectors.md) (1)

**Used by (64):** [AgricultureCheck](AgricultureCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [CarCheck](CarCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [DataSave](DataSave.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DenominationCheck](DenominationCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [EconomyManager](EconomyManager.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [Inbox](Inbox.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureManager](InfrastructureManager.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyCheck](PolicyCheck.md), [PolicyDates](PolicyDates.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [PriceLevelCheck](PriceLevelCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReplayCheck](ReplayCheck.md), [Retail](Retail.md), [SalesTaxLedger](SalesTaxLedger.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [WageLadderCheck](WageLadderCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 60 | THE DECISION LOG (0.7.23) |
| 81 | THE LEVERS ON HISTORY'S DATES (R0, 2026-11-04; research-1900.md 7) |
| 311 | FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) |
| 375 | THE PENSION PROMISE, AS TWO DIALS |
| 420 | EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS |
| 470 | · FOOD ASSISTANCE (0.7.43) |
| 540 | THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE |
| 904 | HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) |
| 1072 | WHAT A RIDE COSTS (2026-09-16) |
| 1101 | A RIDE IS NOT A MONTH (2026-09-17) |
| 1156 | · THE FARE IS REAL (0.7.42, the anchor; star 9) |
| 1236 | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7, 2026-10-10) |
| 1286 | PORT DUES (FR3, 2026-10-19; docs/queue/FR3.md 2, D9) |
| 1341 | THE CITY RATES |
| 1450 | OFFSETS |
| 1501 | EFFECTIVE RATES - the only numbers anything is ever charged at |
| 1556 | WAGES |
| 1631 | A COPY TO PREVIEW ON (0.7.36) |
| 1683 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 624 | `TaxPolicy.GrantBasis.WAGE_SHARE` | A share of the unskilled wage, per student per month - the founding rule until 0.7.19, and what an older save that chose nothing keeps. |
| 626 | `TaxPolicy.GrantBasis.FIXED` | A fixed REAL amount per student per month, in founding thousands, kept up with prices - by the expected price level since 0.7.42, the price index from 0.7.19 (a nominal amount until then) - the default since 0.7.19. |
| 628 | `TaxPolicy.GrantBasis.SURPLUS_SHARE` | A share of the cash part of last month's budget surplus (SG1: not the paper gains in it) as ONE POOL, split evenly over this month's students; a deficit month pays nothing. |
| 630 | `TaxPolicy.GrantBasis.TUITION_SHARE` | A share of each student's own course tuition at today's price - after the tuition scale, before the subsidy. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 119 | `TaxPolicy.GATED` | `{ PolicyDates.Lever.PROFIT_TAX, PolicyDates.Lever.WAGE_TAX, PolicyDates.Lever...` | The levers this class holds, in the order they open. |
| 273 | `TaxPolicy.DEFAULT_INCOME_TAX` | `.15` | Where income tax started before it was a dial. |
| 283 | `TaxPolicy.DEFAULT_PROPERTY_TAX` | `.015` | 1.5% a year, near the real-world average. |
| 286 | `TaxPolicy.MAX_PROPERTY_TAX` | `.10` | Nobody has ever paid 100% property tax and the game should not model it. |
| 289 | `TaxPolicy.MAX_INCOME_TAX` | `.60` | Above this, income tax stops being a policy and starts being confiscation. |
| 299 | `TaxPolicy.MAX_OFFSET` | `.30` | How far a band or sector may be moved from its tax's base rate, either way. |
| 348 | `TaxPolicy.DEFAULT_FARMLAND_RELIEF` | `1.0` |  |
| 351 | `TaxPolicy.FARM_SECTOR` | `Sectors.AGRICULTURE` | The sector whose ground the relief applies to. |
| 394 | `TaxPolicy.MAX_CONTRIBUTION` | `.20` | Nobody hands over more than a fifth of a wage, whatever the deficit. |
| 397 | `TaxPolicy.MAX_REPLACEMENT` | `1.00` | A pension of more than one unskilled wage is a wage, not a pension. |
| 441 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currency reform. |
| 456 | `TaxPolicy.DEFAULT_FIXED_GRANT` | `DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage()` | THE GRANT FOLLOWS PRICES (0.7.19): the default grant as a FIXED amount, in founding thousands - $525 a month, the Canada Student Grant (2026-27) this basis's share was read off, so at founding, when the price index is... |
| 459 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 462 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 465 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 485 | `TaxPolicy.DEFAULT_FOOD_ASSISTANCE` | `0` | No voucher: the city it was. |
| 488 | `TaxPolicy.MAX_FOOD_ASSISTANCE` | `1.00` | A voucher for every basket an eligible household eats is the most there is. |
| 668 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.FIXED` | Where the grant starts: a fixed real amount, kept up with prices - the expected price level since 0.7.42, the price index from 0.7.19 (the founding rule, a share of the unskilled wage, until then). |
| 671 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 674 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 677 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 680 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 683 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 686 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 689 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so x5 leaves room past ... |
| 721 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 939 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 942 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 945 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 948 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 1096 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025 * PriceLevel.FACTOR` | What a single journey costs a rider, in thousands. |
| 1099 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05 * PriceLevel.FACTOR` | The dial's cap, $50 a ride, twenty times the default, in founding money: past it no car owner rides while a journey's fuel costs under half of it, and the commuters with no car of their own still do (0.7.49, Infrastru... |
| 1140 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 1261 | `TaxPolicy.DEFAULT_FUEL_DUTY` | `0` | No duty: the city it was (Jerus's A7: default 0). |
| 1264 | `TaxPolicy.MAX_FUEL_DUTY` | `.001 * PriceLevel.FACTOR` | The dial's top, a dollar a litre at founding prices, in thousands: past the dearest duty the research found, the Netherlands' EUR 0.789 a litre of petrol (2024; runs/research-pump.md 6 [30]) - a ceiling, not a default. |
| 1303 | `TaxPolicy.DEFAULT_PORT_DUES` | `1.0` | The default due: the berths' cost recovered over their life at the city's rate (D9). |
| 1306 | `TaxPolicy.MAX_PORT_DUES` | `2.0` | The dial's top: twice the default. |
| 1688 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 1691 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 1694 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 1697 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 1700 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 1703 | `TaxPolicy.STATE_BEFORE_REAL_GRANT` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | ...and one from before the real FIXED grant (0.7.19): a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |
| 1706 | `TaxPolicy.STATE_BEFORE_REAL_FARE` | `STATE_BEFORE_REAL_GRANT + 1` | ...and one from before the real fare (0.7.42): one slot on top saying the FIXED amount is real, in founding money (0.7.19) - see realiseFixedGrant(). |
| 1709 | `TaxPolicy.STATE_BEFORE_FOOD_ASSISTANCE` | `STATE_BEFORE_REAL_FARE + 1` | ...and one from before food assistance (0.7.43): one slot on top saying the fare is real, in founding money (0.7.42) - see realiseFare(). |
| 1712 | `TaxPolicy.STATE_BEFORE_FUEL_DUTY` | `STATE_BEFORE_FOOD_ASSISTANCE + 1` | ...and one from before the fuel duty (0.7.103): the food assistance dial on top (0.7.43). |
| 1715 | `TaxPolicy.STATE_BEFORE_PORT_DUES` | `STATE_BEFORE_FUEL_DUTY + 1` | ...and one from before the port dues dial (FR3): the fuel duty on top (0.7.103). |
| 1718 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_PORT_DUES + 1` | This build's array: the port dues dial on top (FR3). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private transient DecisionLog decisions` | Where a change to a dial here is written; null for a policy no city holds. |
| 125 | `private int month` | The city's game month as Game pushes it (setMonth()); 0 is a policy no city holds, whose every lever is open. |
| 128 | `private final java.util.EnumMap<PolicyDates.Lever, Integer> opening` | Each lever's opening month: history's (PolicyDates.openingMonth()) until a find moves it or openAll() opens it. |
| 131 | `private boolean restoring` | True while a save is being read back: what a city saved is put back as it was, whatever month the policy last heard. |
| 138 | `private final java.util.EnumSet<PolicyDates.Lever> held` | The gated levers this policy held shut at the last month it heard (setMonth()), so that the month one opens is known: the income taxes then open at the founding rate (call 16). |
| 306 | `private double profitTaxRate` | THE THREE INCOME BASES (0.7.4), one per tax, where there was one income rate - see CITY RATES, AND OFFSETS FROM THEM above. |
| 307 | `private double salesTaxRate` |  |
| 308 | `private double wageTaxRate` |  |
| 309 | `private double propertyTaxRate` |  |
| 353 | `private double farmlandRelief` |  |
| 390 | `private double contributionRate` |  |
| 391 | `private double pensionReplacement` |  |
| 432 | `private double eiPremiumRate` |  |
| 433 | `private double eiBenefitRate` |  |
| 490 | `private double foodAssistance` |  |
| 695 | `private GrantBasis grantBasis` | What the grant is a share of, or is. |
| 703 | `private double grantAmount` | The grant's one number, in the basis's own unit: a share of the wage, a fixed real amount in founding thousands, a share of the surplus, or a share of the tuition. |
| 706 | `private boolean fixedGrantNominal` | True while a FIXED amount read from a save before 0.7.19 is still nominal, until realiseFixedGrant() reads it at the load month's level (the expected price level since 0.7.42; the price index before). |
| 709 | `private double studentLoanRate` | Annual interest on the treasury's student loans. |
| 717 | `private final double[] tuitionScales` | The multiplier on the founding tuition table, one per school kind by EducationType ordinal (0.7.6). |
| 950 | `private double healthFeeScale` |  |
| 951 | `private double healthPremiumRate` |  |
| 1014 | `private double pensionWageBase` | The wage a pension is a share of, carried in today's money - struck at the expected price level since 0.7.42 (seedConstants()). |
| 1021 | `private transient double wageAnchor` | The unskilled wage the city was founded on, the figure the pension's wage base and the default grant are struck from (R0d): PayTier's unskilled rung in every city but one founded the 1900 way, which is founded on 1901... |
| 1154 | `private double transitFare` |  |
| 1179 | `private double expectedLevel` | The expected price level the fare is charged at; 1.0 until Game tells it. |
| 1182 | `private boolean fareNominal` | True while a fare read from a save before 0.7.42 is still nominal, until realiseFare() reads it at the load month's level. |
| 1206 | `private double moneyUnit` | The currency's unit (Denomination.getUnit(), B6, 0.7.47): the dial is founding money in today's unit and MAX_TRANSIT_FARE is founding money, so the dial stops at the cap over the unit (maxTransitFare()). |
| 1266 | `private double fuelDuty` |  |
| 1308 | `private double portDues` |  |
| 1326 | `private final double[] wageOffset` |  |
| 1337 | `private final java.util.Map<String, Double> profitOffset` | THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector template). |
| 1338 | `private final java.util.Map<String, Double> salesOffset` |  |
| 1339 | `private final java.util.Map<String, Double> propertyOffset` |  |
| 1923 | `public String sector` |  |
| 1924 | `public double profit, sales, property` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1966 | **type** `public class TaxPolicy` | The city's tax rates - the revenue half of what the player actually decides. |

### THE DECISION LOG (0.7.23) (lines 60-80)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1 | `public void recordTo(DecisionLog log)` | Wires this policy to its city's decision log (Game.buildWorld()). |
| 77 | 3 | `private void decided(String kind, String label)` |  |

### THE LEVERS ON HISTORY'S DATES (R0, 2026-11-04; research-1900.md 7) (lines 81-310)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 5 | `private static java.util.EnumMap<PolicyDates.Lever, Integer> historyOpenings()` |  |
| 147 | 3 | `public boolean isOpen(PolicyDates.Lever lever)` | Whether a lever is open: always for a policy no city holds (month 0), else from its opening month on. |
| 152 | 3 | `public int openingMonth(PolicyDates.Lever lever)` | The game month a lever opens in on this city: history's, or earlier where a find brought it forward, or 1 where openAll() opened it. |
| 157 | 3 | `private boolean closed(PolicyDates.Lever lever)` | A setter's question: is this lever shut to the player? |
| 168 | 38 | `public void setMonth(int gameMonth)` | The city's month, told by Game at founding, after a load and at the top of every month before anything is taxed or paid. |
| 208 | 5 | `public void openAll()` | Every lever's opening month to 1: a save from before R0, whose taxes were set before there was a gate, and a fixture whose premise is a lever's mechanics. |
| 215 | 3 | `public void openNow(PolicyDates.Lever lever)` | One lever's opening month to 1, the rest and what was held untouched: a fixture whose premise is one lever's mechanics (a tax so opened still opens at the founding rate the next month it is told). |
| 227 | 8 | `public boolean bringForward(PolicyDates.Lever lever, int month)` | A law discovery found here brings its lever forward (R3): the lever opens in `month` or at the earliest the discovery allows (PolicyDates.earliestOpening(): MAX_EARLY_MONTHS before its date), whichever is later, and n... |
| 237 | 3 | `private void resetOpenings()` | Every lever back to history's date: a new city. |
| 242 | 7 | `public java.util.Map<String, Integer> openingsForSave()` | The dated levers' opening months for the save, keyed by the lever's name and never its ordinal. |
| 254 | 17 | `public void restoreOpenings(java.util.Map<String, Integer> saved)` | Puts a saved city's openings back. |

### FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) (lines 311-374)

| line | len | member | says |
|---:|---:|---|---|
| 356 | 1 | `public double getFarmlandRelief()` | 0 assesses a field like a building lot; 1 assesses only what stands on it. |
| 358 | 7 | `public void setFarmlandRelief(double share)` |  |
| 371 | 3 | `public double assessedLandShare(String sector)` | The share of a sector's LAND that is on the assessment roll. |

### THE PENSION PROMISE, AS TWO DIALS (lines 375-419)

| line | len | member | says |
|---:|---:|---|---|
| 399 | 1 | `public double getContributionRate()` |  |
| 400 | 1 | `public double getPensionReplacement()` |  |
| 402 | 8 | `public void setContributionRate(double rate)` |  |
| 411 | 8 | `public void setPensionReplacement(double share)` |  |

### EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS (lines 420-469)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 1 | `public double getEiPremiumRate()` |  |
| 468 | 1 | `public double getEiBenefitRate()` |  |

### FOOD ASSISTANCE (0.7.43) (lines 470-539)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 1 | `public double getFoodAssistance()` | The share of an eligible household's baskets, at the shelf price, the treasury's voucher covers. |
| 495 | 7 | `public void setFoodAssistance(double share)` |  |
| 508 | 3 | `public double getStudentGrantShare()` | The share of the unskilled wage a student is granted a month: the amount under the WAGE_SHARE basis, and nothing under any other, because under any other there is no such share. |
| 512 | 8 | `public void setEiPremiumRate(double rate)` |  |
| 521 | 8 | `public void setEiBenefitRate(double share)` |  |
| 531 | 8 | `public void setStudentGrantShare(double share)` | The grant as a share of the unskilled wage - the founding basis until 0.7.19, at this share. |

### THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE (lines 540-903)

| line | len | member | says |
|---:|---:|---|---|
| 622 | 10 | **type** `public enum GrantBasis` | How the student grant is struck: what the one amount is a share of, or is. |
| 718 | 1 | `{ ... }` |  |
| 723 | 7 | `private static EducationType[] schoolKinds()` |  |
| 731 | 1 | `public GrantBasis getGrantBasis()` |  |
| 734 | 1 | `public double getGrantAmount()` | The amount that goes with getGrantBasis(), in that basis's unit. |
| 737 | 1 | `public double getStudentLoanRate()` | The annual rate a graduate is charged on the student loan while repaying it. |
| 749 | 1 | `public double getTuitionScale()` | The multiplier on the founding tuition table; 1 is the founding price, 0 is free at the point of use. |
| 752 | 4 | `public double tuitionScaleOf(EducationType type)` | One school kind's own multiplier (0.7.6); NONE, or null, reads the every-school one. |
| 758 | 7 | `public boolean tuitionScalesSplit()` | Whether the nine kinds' scales have parted - no longer one number (0.7.6). |
| 774 | 9 | `public double maxGrantAmount(GrantBasis basis)` | The ceiling on the amount under a basis: a share of a wage up to one wage, a fixed amount up to an unskilled wage (the founding wage in today's unit - a real amount against a real one since 0.7.19), the whole surplus,... |
| 785 | 8 | `public void setGrantBasis(GrantBasis basis)` | Picks the basis; the amount is clamped to the new basis's ceiling and otherwise left where it was. |
| 795 | 6 | `public void setGrantAmount(double amount)` | Sets the amount, in the current basis's unit, clamped to its ceiling. |
| 803 | 8 | `public void setGrant(GrantBasis basis, double amount)` | Both at once, so a screen can stage them together - and one line in the log for the two. |
| 813 | 10 | `private void grantDecided(GrantBasis wasBasis, double wasAmount)` | The grant's line in the decision log, when its basis or its amount moved (0.7.23). |
| 824 | 9 | `public void setStudentLoanRate(double annual)` |  |
| 840 | 9 | `public void setTuitionScale(double scale)` | Every school at once: all nine kinds set to this scale (0.7.6), which is what the one scale did. |
| 851 | 9 | `public void setTuitionScaleOf(EducationType type, double scale)` | One school kind's own scale, held to MAX_TUITION_SCALE (0.7.6); NONE, or null, is ignored. |
| 892 | 11 | `public static double grantBill(GrantBasis basis, double amount, double students, double unskilledWage, double priceIndex, doubl...` | The month's grant bill under any basis and amount - the one place the four rules are written, so the treasury's bill, the save's re-strike, the students' income and the Schools page's preview cannot disagree. |

### HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) (lines 904-1071)

| line | len | member | says |
|---:|---:|---|---|
| 954 | 1 | `public double getHealthFeeScale()` | The multiplier on the three care fees; 1 is the founding fee, 0 is free at the point of use. |
| 957 | 1 | `public double getHealthPremiumRate()` | The share of every wage the health premium takes, employee side. |
| 959 | 7 | `public void setHealthFeeScale(double scale)` |  |
| 967 | 8 | `public void setHealthPremiumRate(double rate)` |  |
| 994 | 3 | `public double pensionPerSenior()` | A pension, in TODAY's money. |
| 1006 | 3 | `public double pensionPerSeniorAt(double replacement)` | ...at another replacement rate, in today's money (0.7.36): what each senior would be paid if the dial read `replacement`, on the same wage base - the Policy tab's pension preview, which scaled today's bill by the rati... |
| 1011 | 1 | `public double foundingPensionWage()` | The founding unskilled wage the pension is a share of, in today's unit at founding prices: the wage base over the level it is struck at (0.7.45, for the Policy tab's (i)). |
| 1024 | 3 | `public void setLadder(WageLadder l)` | Takes the founding wage the pension and the default grant are struck from from a ladder (R0d); the grant a load restored is left where it was. |
| 1034 | 5 | `public void foundOn(WageLadder l)` | A city founded on a ladder (R0d): its anchor, the pension's wage base on it, and the default grant it opens on - DEFAULT_STUDENT_GRANT_SHARE of the ladder's unskilled wage, as DEFAULT_FIXED_GRANT is of PayTier's - whe... |
| 1041 | 1 | `private double defaultFixedGrant()` | The default grant on this policy's founding wage: DEFAULT_FIXED_GRANT where that is PayTier's, the same share of the 1900 city's own. |
| 1043 | 28 | `public void redenominate(double scale)` |  |

### WHAT A RIDE COSTS (2026-09-16) (lines 1072-1100)

### A RIDE IS NOT A MONTH (2026-09-17) (lines 1101-1155)

| line | len | member | says |
|---:|---:|---|---|
| 1149 | 1 | `public double monthlyFare()` | What a month of riding costs one commuter, in thousands. |
| 1152 | 1 | `public static double monthlyFareAt(double fare)` | ...at a fare the city has not set (0.7.38): the fare dial's preview, the same one multiplication. |

### THE FARE IS REAL (0.7.42, the anchor; star 9) (lines 1156-1235)

| line | len | member | says |
|---:|---:|---|---|
| 1185 | 1 | `public double getTransitFare()` | The fare dial: a ride's price in founding money, in today's unit. |
| 1188 | 1 | `public double chargedFare()` | What a ride is charged today: the dial at the expected price level (THE FARE IS REAL). |
| 1191 | 3 | `public void setExpectedLevel(double level)` | Told by Game with every re-strike of the money constants; anything not positive is ignored. |
| 1196 | 1 | `public double getExpectedLevel()` | The level the fare is charged at. |
| 1209 | 3 | `public void setMoneyUnit(double unit)` | Told by Game.restrikeMoneyConstants() and Game.reformCurrency(); anything not positive is ignored. |
| 1214 | 1 | `public double maxTransitFare()` | The most the fare dial takes in today's unit: MAX_TRANSIT_FARE over the unit, where no car owner rides. |
| 1216 | 7 | `public void setTransitFare(double fare)` |  |
| 1230 | 5 | `public void realiseFare(double level)` | An older save's fare, read as the real fare that charges the same at the load month's expected level (0.7.42): the nominal fare over the level. |

### THE FUEL DUTY (0.7.103, batch P4; Jerus's A7, 2026-10-10) (lines 1236-1285)

| line | len | member | says |
|---:|---:|---|---|
| 1269 | 1 | `public double getFuelDuty()` | The fuel duty dial: founding money a litre of petrol at the pump, in today's unit. |
| 1272 | 1 | `public double chargedFuelDuty()` | What a litre is charged today: the dial at the expected price level (THE FUEL DUTY). |
| 1275 | 1 | `public double maxFuelDuty()` | The most the duty dial takes in today's unit: MAX_FUEL_DUTY over the unit. |
| 1277 | 8 | `public void setFuelDuty(double dutyALitre)` |  |

### PORT DUES (FR3, 2026-10-19; docs/queue/FR3.md 2, D9) (lines 1286-1340)

| line | len | member | says |
|---:|---:|---|---|
| 1311 | 1 | `public double getPortDues()` | The port dues dial: the multiple of the default due Freight pays the city a tonne across a berth, 0 to MAX_PORT_DUES (FR3). |
| 1313 | 7 | `public void setPortDues(double multiple)` |  |
| 1322 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### THE CITY RATES (lines 1341-1449)

| line | len | member | says |
|---:|---:|---|---|
| 1359 | 3 | `public double getIncomeTaxRate()` | The three income taxes TOGETHER - what "the city rate" was until 0.7.4. |
| 1364 | 4 | `public boolean incomeRatesSplit()` | Whether the three income bases have parted - profit, sales and wage no longer one number. |
| 1370 | 1 | `public double getProfitTaxRate()` | What every sector's profit tax moves off (0.7.4). |
| 1373 | 1 | `public double getSalesTaxRate()` | What every sector's sales tax moves off (0.7.4). |
| 1376 | 1 | `public double getWageTaxRate()` | What every band's wage tax moves off (0.7.4). |
| 1378 | 8 | `public void setProfitTaxRate(double rate)` |  |
| 1387 | 8 | `public void setSalesTaxRate(double rate)` |  |
| 1396 | 8 | `public void setWageTaxRate(double rate)` |  |
| 1406 | 3 | `public double getPropertyTaxRate()` | The annual rate - what the player sets and what the screens show. |
| 1411 | 3 | `public double getMonthlyPropertyTaxRate()` | The annual rate divided by twelve. |
| 1422 | 18 | `public void setIncomeTaxRate(double rate)` | Every income tax at once: the profit, sales and wage bases all set to this rate (0.7.4), which is what the one city rate did. |
| 1442 | 7 | `public void setPropertyTaxRate(double annualRate)` | Takes the ANNUAL rate. |

### OFFSETS (lines 1450-1500)

| line | len | member | says |
|---:|---:|---|---|
| 1454 | 1 | `public double getWageOffset(WageBand band)` |  |
| 1455 | 1 | `public double getProfitOffset(String sector)` |  |
| 1456 | 1 | `public double getSalesOffset(String sector)` |  |
| 1457 | 1 | `public double getPropertyOffset(String sector)` |  |
| 1459 | 1 | `public double getProfitOffset(Sector s)` |  |
| 1460 | 1 | `public double getSalesOffset(Sector s)` |  |
| 1461 | 1 | `public double getPropertyOffset(Sector s)` |  |
| 1463 | 6 | `public void setWageOffset(WageBand band, double points)` |  |
| 1470 | 6 | `public void setProfitOffset(String sector, double points)` |  |
| 1477 | 6 | `public void setSalesOffset(String sector, double points)` |  |
| 1485 | 6 | `public void setPropertyOffset(String sector, double points)` | In ANNUAL points, matching the rate it offsets; held at MAX_PROPERTY_TAX either way (clampPropertyOffset()), on a load too. |
| 1493 | 3 | `private void offsetDecided(String tax, String who, double was, double now)` | An offset's line in the decision log, when it moved (0.7.23): "Profit tax, Retail, to -2 pts". |
| 1497 | 1 | `public void setProfitOffset(Sector s, double points)` |  |
| 1498 | 1 | `public void setSalesOffset(Sector s, double points)` |  |
| 1499 | 1 | `public void setPropertyOffset(Sector s, double points)` |  |

### EFFECTIVE RATES - the only numbers anything is ever charged at (lines 1501-1555)

| line | len | member | says |
|---:|---:|---|---|
| 1506 | 3 | `public double effectiveWageRate(WageBand band)` | What wages in this band are taxed at: the wage base and the band's offset. |
| 1511 | 3 | `public double effectiveProfitRate(String sector)` | What this sector's profit is taxed at: the profit base and the sector's offset. |
| 1516 | 3 | `public double effectiveSalesRate(String sector)` | What this sector charges on the value it adds: the sales base and its offset. |
| 1521 | 3 | `public double effectivePropertyRate(String sector)` | ANNUAL property tax rate for this sector. |
| 1526 | 3 | `public double effectiveMonthlyPropertyRate(String sector)` | ...and the monthly one, which is what is actually billed. |
| 1530 | 1 | `public double effectiveProfitRate(Sector s)` |  |
| 1531 | 1 | `public double effectiveSalesRate(Sector s)` |  |
| 1532 | 1 | `public double effectivePropertyRate(Sector s)` |  |
| 1533 | 1 | `public double effectiveMonthlyPropertyRate(Sector s)` |  |
| 1541 | 6 | `public double propertyTaxOn(double assessedValue)` | What one month's property tax comes to on a given assessed value. |
| 1549 | 6 | `public double propertyTaxOn(double assessedValue, String sector)` | One month's property tax at this sector's own rate. |

### WAGES (lines 1556-1630)

| line | len | member | says |
|---:|---:|---|---|
| 1568 | 4 | `public double payslipShare(WageBand band)` | What a payslip loses in this band (0.7.36): its wage tax at its own rate, the pension contribution and the EI and health premiums - the three promises are charged on the same payroll, flat across every band. |
| 1584 | 5 | `public double wageTaxOn(double[] wagePerType, double[] fillRate)` | The month's wage tax, summed job type by job type at its band's rate. |
| 1611 | 19 | `public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate)` | The same wage tax, split across the six pay tiers. |

### A COPY TO PREVIEW ON (0.7.36) (lines 1631-1682)

| line | len | member | says |
|---:|---:|---|---|
| 1646 | 36 | `public TaxPolicy copy()` | A detached copy of every dial (0.7.36): setters on it clamp as here and record nothing. |

### SAVE AND RESTORE (lines 1683-2023)

| line | len | member | says |
|---:|---:|---|---|
| 1727 | 85 | `public double[] getPolicyState()` | The city rates and the wage-band offsets as one array, city rates first - bar the three income bases of 0.7.4, which ride the end like every field added since. |
| 1821 | 15 | `public boolean restorePolicyState(double[] state)` | (STATE_BEFORE_EI) or longer than this build's (STATE_SLOTS); nothing is changed. |
| 1837 | 67 | `private void readPolicyState(double[] state)` |  |
| 1915 | 5 | `public void realiseFixedGrant(double priceIndex)` | An older save's FIXED grant, read as the real amount that pays the same at the load month's level (0.7.19): its nominal amount over the level, so the bill the month after the load is the bill the save was paying, and ... |
| 1922 | 4 | **type** `public static final class SectorOffsets` | One sector's three offsets, as the save carries them. |
| 1927 | 16 | `public java.util.List<SectorOffsets> getSectorOffsets()` |  |
| 1945 | 18 | `public void restoreSectorOffsets(java.util.List<SectorOffsets> saved)` | A sector the build does not have keeps its row - harmless, and it comes back if the sector does. |
| 1969 | 30 | `public void reset()` | Back to a new city's policy: the defaults, history's dates for every lever, and a month no city holds (0), so that every default is set whatever the last city's month was. |
| 2000 | 6 | `private double clamp(double rate, double max)` |  |
| 2007 | 4 | `private double clampOffset(double points)` |  |
| 2019 | 4 | `private double clampPropertyOffset(double points)` | A property offset is held at MAX_PROPERTY_TAX either way (B5, 0.7.47), not MAX_OFFSET: the rate is the base plus the offset held within 0 and MAX_PROPERTY_TAX, and the base itself is within them, so no offset past the... |


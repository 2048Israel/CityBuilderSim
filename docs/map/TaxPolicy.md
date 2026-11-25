# TaxPolicy.java - 1,846 lines · 117 methods · 49 constants · model

`ham/citybuildersim/TaxPolicy.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [DecisionLog](DecisionLog.md) (68), [EducationType](EducationType.md) (17), [WageBand](WageBand.md) (10), [Sector](Sector.md) (10), [Unemployment](Unemployment.md) (6), [PayTier](PayTier.md) (6), [SocialSecurity](SocialSecurity.md) (4), [PolicyDates](PolicyDates.md) (3), [PriceLevel](PriceLevel.md) (3), [JobType](JobType.md) (3), [Sectors](Sectors.md) (2), [WageLadder](WageLadder.md) (2), [Equity](Equity.md) (2), [Currency](Currency.md) (1)

**Used by (70):** [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CarCheck](CarCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [DataSave](DataSave.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [Game](Game.md), [GovernmentPlayer](GovernmentPlayer.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentView](GovernmentView.md), [GovernmentViewCheck](GovernmentViewCheck.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureManager](InfrastructureManager.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyWordsCheck](MoneyWordsCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyCheck](PolicyCheck.md), [PolicyOpenCheck](PolicyOpenCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [PriceLevelCheck](PriceLevelCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReplayCheck](ReplayCheck.md), [Retail](Retail.md), [SalesTaxLedger](SalesTaxLedger.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [WageLadderCheck](WageLadderCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 60 | THE DECISION LOG (0.7.23) |
| 81 | EVERY LEVER OPEN FROM THE FOUNDING (OP1, 2026-11-19) |
| 159 | FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) |
| 223 | THE PENSION PROMISE, AS TWO DIALS |
| 266 | EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS |
| 316 | · FOOD ASSISTANCE (0.7.43) |
| 383 | THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE |
| 742 | HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) |
| 907 | WHAT A RIDE COSTS (2026-09-16) |
| 936 | A RIDE IS NOT A MONTH (2026-09-17) |
| 991 | · THE FARE IS REAL (0.7.42, the anchor; star 9) |
| 1071 | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7, 2026-10-10) |
| 1121 | PORT DUES (FR3, 2026-10-19; docs/queue/FR3.md 2, D9) |
| 1194 | THE CITY RATES |
| 1290 | OFFSETS |
| 1340 | EFFECTIVE RATES - the only numbers anything is ever charged at |
| 1395 | WAGES |
| 1470 | A COPY TO PREVIEW ON (0.7.36) |
| 1520 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 467 | `TaxPolicy.GrantBasis.WAGE_SHARE` | A share of the unskilled wage, per student per month - the founding rule until 0.7.19, and what an older save that chose nothing keeps. |
| 469 | `TaxPolicy.GrantBasis.FIXED` | A fixed REAL amount per student per month, in founding thousands, kept up with prices - by the expected price level since 0.7.42, the price index from 0.7.19 (a nominal amount until then) - the default since 0.7.19. |
| 471 | `TaxPolicy.GrantBasis.SURPLUS_SHARE` | A share of the cash part of last month's budget surplus (SG1: not the paper gains in it) as ONE POOL, split evenly over this month's students; a deficit month pays nothing. |
| 473 | `TaxPolicy.GrantBasis.TUITION_SHARE` | A share of each student's own course tuition at today's price - after the tuition scale, before the subsidy. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 121 | `TaxPolicy.DEFAULT_INCOME_TAX` | `.15` | Where income tax started before it was a dial. |
| 131 | `TaxPolicy.DEFAULT_PROPERTY_TAX` | `.015` | 1.5% a year, near the real-world average. |
| 134 | `TaxPolicy.MAX_PROPERTY_TAX` | `.10` | Nobody has ever paid 100% property tax and the game should not model it. |
| 137 | `TaxPolicy.MAX_INCOME_TAX` | `.60` | Above this, income tax stops being a policy and starts being confiscation. |
| 147 | `TaxPolicy.MAX_OFFSET` | `.30` | How far a band or sector may be moved from its tax's base rate, either way. |
| 196 | `TaxPolicy.DEFAULT_FARMLAND_RELIEF` | `1.0` |  |
| 199 | `TaxPolicy.FARM_SECTOR` | `Sectors.AGRICULTURE` | The sector whose ground the relief applies to. |
| 242 | `TaxPolicy.MAX_CONTRIBUTION` | `.20` | Nobody hands over more than a fifth of a wage, whatever the deficit. |
| 245 | `TaxPolicy.MAX_REPLACEMENT` | `1.00` | A pension of more than one unskilled wage is a wage, not a pension. |
| 287 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currency reform. |
| 302 | `TaxPolicy.DEFAULT_FIXED_GRANT` | `DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage()` | THE GRANT FOLLOWS PRICES (0.7.19): the default grant as a FIXED amount, in founding thousands - $525 a month, the Canada Student Grant (2026-27) this basis's share was read off, so at founding, when the price index is... |
| 305 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 308 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 311 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 331 | `TaxPolicy.DEFAULT_FOOD_ASSISTANCE` | `0` | No voucher: the city it was. |
| 334 | `TaxPolicy.MAX_FOOD_ASSISTANCE` | `1.00` | A voucher for every basket an eligible household eats is the most there is. |
| 511 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.FIXED` | Where the grant starts: a fixed real amount, kept up with prices - the expected price level since 0.7.42, the price index from 0.7.19 (the founding rule, a share of the unskilled wage, until then). |
| 514 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 517 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 520 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 523 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 526 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 529 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 532 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so x5 leaves room past ... |
| 564 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 777 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 780 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 783 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 786 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 931 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025 * PriceLevel.FACTOR` | What a single journey costs a rider, in thousands. |
| 934 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05 * PriceLevel.FACTOR` | The dial's cap, $50 a ride, twenty times the default, in founding money: past it no car owner rides while a journey's fuel costs under half of it, and the commuters with no car of their own still do (0.7.49, Infrastru... |
| 975 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 1096 | `TaxPolicy.DEFAULT_FUEL_DUTY` | `0` | No duty: the city it was (Jerus's A7: default 0). |
| 1099 | `TaxPolicy.MAX_FUEL_DUTY` | `.001 * PriceLevel.FACTOR` | The dial's top, a dollar a litre at founding prices, in thousands: past the dearest duty the research found, the Netherlands' EUR 0.789 a litre of petrol (2024; runs/research-pump.md 6 [30]) - a ceiling, not a default. |
| 1138 | `TaxPolicy.DEFAULT_PORT_DUES` | `1.0` | The default due: the berths' cost recovered over their life at the city's rate (D9). |
| 1141 | `TaxPolicy.MAX_PORT_DUES` | `2.0` | The dial's top: twice the default. |
| 1144 | `TaxPolicy.DEFAULT_CAPITAL_GAINS_INCLUSION` | `.5` | The share of a net realised gain taxed at the holder's wage rate (HT1, accepted call 4). |
| 1525 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 1528 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 1531 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 1534 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 1537 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 1540 | `TaxPolicy.STATE_BEFORE_REAL_GRANT` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | ...and one from before the real FIXED grant (0.7.19): a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |
| 1543 | `TaxPolicy.STATE_BEFORE_REAL_FARE` | `STATE_BEFORE_REAL_GRANT + 1` | ...and one from before the real fare (0.7.42): one slot on top saying the FIXED amount is real, in founding money (0.7.19) - see realiseFixedGrant(). |
| 1546 | `TaxPolicy.STATE_BEFORE_FOOD_ASSISTANCE` | `STATE_BEFORE_REAL_FARE + 1` | ...and one from before food assistance (0.7.43): one slot on top saying the fare is real, in founding money (0.7.42) - see realiseFare(). |
| 1549 | `TaxPolicy.STATE_BEFORE_FUEL_DUTY` | `STATE_BEFORE_FOOD_ASSISTANCE + 1` | ...and one from before the fuel duty (0.7.103): the food assistance dial on top (0.7.43). |
| 1552 | `TaxPolicy.STATE_BEFORE_PORT_DUES` | `STATE_BEFORE_FUEL_DUTY + 1` | ...and one from before the port dues dial (FR3): the fuel duty on top (0.7.103). |
| 1555 | `TaxPolicy.STATE_BEFORE_CAPITAL_GAINS` | `STATE_BEFORE_PORT_DUES + 1` | This build's array: the port dues dial on top (FR3). |
| 1557 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_CAPITAL_GAINS + 1` | The inclusion dial appended by HT1. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private transient DecisionLog decisions` | Where a change to a dial here is written; null for a policy no city holds. |
| 154 | `private double profitTaxRate` | THE THREE INCOME BASES (0.7.4), one per tax, where there was one income rate - see CITY RATES, AND OFFSETS FROM THEM above. |
| 155 | `private double salesTaxRate` |  |
| 156 | `private double wageTaxRate` |  |
| 157 | `private double propertyTaxRate` |  |
| 201 | `private double farmlandRelief` |  |
| 238 | `private double contributionRate` |  |
| 239 | `private double pensionReplacement` |  |
| 278 | `private double eiPremiumRate` |  |
| 279 | `private double eiBenefitRate` |  |
| 336 | `private double foodAssistance` |  |
| 538 | `private GrantBasis grantBasis` | What the grant is a share of, or is. |
| 546 | `private double grantAmount` | The grant's one number, in the basis's own unit: a share of the wage, a fixed real amount in founding thousands, a share of the surplus, or a share of the tuition. |
| 549 | `private boolean fixedGrantNominal` | True while a FIXED amount read from a save before 0.7.19 is still nominal, until realiseFixedGrant() reads it at the load month's level (the expected price level since 0.7.42; the price index before). |
| 552 | `private double studentLoanRate` | Annual interest on the treasury's student loans. |
| 560 | `private final double[] tuitionScales` | The multiplier on the founding tuition table, one per school kind by EducationType ordinal (0.7.6). |
| 788 | `private double healthFeeScale` |  |
| 789 | `private double healthPremiumRate` |  |
| 851 | `private double pensionWageBase` | The wage a pension is a share of, carried in today's money - struck at the expected price level since 0.7.42 (seedConstants()). |
| 858 | `private transient double wageAnchor` | The unskilled wage the city was founded on, the figure the pension's wage base and the default grant are struck from (R0d): PayTier's unskilled rung in every city but one founded the 1900 way, which is founded on 1901... |
| 989 | `private double transitFare` |  |
| 1014 | `private double expectedLevel` | The expected price level the fare is charged at; 1.0 until Game tells it. |
| 1017 | `private boolean fareNominal` | True while a fare read from a save before 0.7.42 is still nominal, until realiseFare() reads it at the load month's level. |
| 1041 | `private double moneyUnit` | The currency's unit (Denomination.getUnit(), B6, 0.7.47): the dial is founding money in today's unit and MAX_TRANSIT_FARE is founding money, so the dial stops at the cap over the unit (maxTransitFare()). |
| 1101 | `private double fuelDuty` |  |
| 1145 | `private double capitalGainsInclusion` |  |
| 1161 | `private double portDues` |  |
| 1179 | `private final double[] wageOffset` |  |
| 1190 | `private final java.util.Map<String, Double> profitOffset` | THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector template). |
| 1191 | `private final java.util.Map<String, Double> salesOffset` |  |
| 1192 | `private final java.util.Map<String, Double> propertyOffset` |  |
| 1755 | `public String sector` |  |
| 1756 | `public double profit, sales, property` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1789 | **type** `public class TaxPolicy` | The city's tax rates - the revenue half of what the player actually decides. |

### THE DECISION LOG (0.7.23) (lines 60-80)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1 | `public void recordTo(DecisionLog log)` | Wires this policy to its city's decision log (Game.buildWorld()). |
| 77 | 3 | `private void decided(String kind, String label)` |  |

### EVERY LEVER OPEN FROM THE FOUNDING (OP1, 2026-11-19) (lines 81-158)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 16 | `public void openAtFounding()` | The founding's opening (OP1): every lever PolicyDates dates opens as R0 opened one on its date (PolicyDates.opensAtFoundingRate()): the profit, wage and sales taxes at DEFAULT_INCOME_TAX with no offsets, and every pro... |

### FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) (lines 159-222)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 1 | `public double getFarmlandRelief()` | 0 assesses a field like a building lot; 1 assesses only what stands on it. |
| 206 | 7 | `public void setFarmlandRelief(double share)` |  |
| 219 | 3 | `public double assessedLandShare(String sector)` | The share of a sector's LAND that is on the assessment roll. |

### THE PENSION PROMISE, AS TWO DIALS (lines 223-265)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 1 | `public double getContributionRate()` |  |
| 248 | 1 | `public double getPensionReplacement()` |  |
| 250 | 7 | `public void setContributionRate(double rate)` |  |
| 258 | 7 | `public void setPensionReplacement(double share)` |  |

### EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS (lines 266-315)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 1 | `public double getEiPremiumRate()` |  |
| 314 | 1 | `public double getEiBenefitRate()` |  |

### FOOD ASSISTANCE (0.7.43) (lines 316-382)

| line | len | member | says |
|---:|---:|---|---|
| 339 | 1 | `public double getFoodAssistance()` | The share of an eligible household's baskets, at the shelf price, the treasury's voucher covers. |
| 341 | 7 | `public void setFoodAssistance(double share)` |  |
| 354 | 3 | `public double getStudentGrantShare()` | The share of the unskilled wage a student is granted a month: the amount under the WAGE_SHARE basis, and nothing under any other, because under any other there is no such share. |
| 358 | 7 | `public void setEiPremiumRate(double rate)` |  |
| 366 | 7 | `public void setEiBenefitRate(double share)` |  |
| 375 | 7 | `public void setStudentGrantShare(double share)` | The grant as a share of the unskilled wage - the founding basis until 0.7.19, at this share. |

### THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE (lines 383-741)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 10 | **type** `public enum GrantBasis` | How the student grant is struck: what the one amount is a share of, or is. |
| 561 | 1 | `{ ... }` |  |
| 566 | 7 | `private static EducationType[] schoolKinds()` |  |
| 574 | 1 | `public GrantBasis getGrantBasis()` |  |
| 577 | 1 | `public double getGrantAmount()` | The amount that goes with getGrantBasis(), in that basis's unit. |
| 580 | 1 | `public double getStudentLoanRate()` | The annual rate a graduate is charged on the student loan while repaying it. |
| 592 | 1 | `public double getTuitionScale()` | The multiplier on the founding tuition table; 1 is the founding price, 0 is free at the point of use. |
| 595 | 4 | `public double tuitionScaleOf(EducationType type)` | One school kind's own multiplier (0.7.6); NONE, or null, reads the every-school one. |
| 601 | 7 | `public boolean tuitionScalesSplit()` | Whether the nine kinds' scales have parted - no longer one number (0.7.6). |
| 617 | 9 | `public double maxGrantAmount(GrantBasis basis)` | The ceiling on the amount under a basis: a share of a wage up to one wage, a fixed amount up to an unskilled wage (the founding wage in today's unit - a real amount against a real one since 0.7.19), the whole surplus,... |
| 628 | 7 | `public void setGrantBasis(GrantBasis basis)` | Picks the basis; the amount is clamped to the new basis's ceiling and otherwise left where it was. |
| 637 | 5 | `public void setGrantAmount(double amount)` | Sets the amount, in the current basis's unit, clamped to its ceiling. |
| 644 | 7 | `public void setGrant(GrantBasis basis, double amount)` | Both at once, so a screen can stage them together - and one line in the log for the two. |
| 653 | 10 | `private void grantDecided(GrantBasis wasBasis, double wasAmount)` | The grant's line in the decision log, when its basis or its amount moved (0.7.23). |
| 664 | 7 | `public void setStudentLoanRate(double annual)` |  |
| 678 | 9 | `public void setTuitionScale(double scale)` | Every school at once: all nine kinds set to this scale (0.7.6), which is what the one scale did. |
| 689 | 9 | `public void setTuitionScaleOf(EducationType type, double scale)` | One school kind's own scale, held to MAX_TUITION_SCALE (0.7.6); NONE, or null, is ignored. |
| 730 | 11 | `public static double grantBill(GrantBasis basis, double amount, double students, double unskilledWage, double priceIndex, doubl...` | The month's grant bill under any basis and amount - the one place the four rules are written, so the treasury's bill, the save's re-strike, the students' income and the Schools page's preview cannot disagree. |

### HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) (lines 742-906)

| line | len | member | says |
|---:|---:|---|---|
| 792 | 1 | `public double getHealthFeeScale()` | The multiplier on the three care fees; 1 is the founding fee, 0 is free at the point of use. |
| 795 | 1 | `public double getHealthPremiumRate()` | The share of every wage the health premium takes, employee side. |
| 797 | 7 | `public void setHealthFeeScale(double scale)` |  |
| 805 | 7 | `public void setHealthPremiumRate(double rate)` |  |
| 831 | 3 | `public double pensionPerSenior()` | A pension, in TODAY's money. |
| 843 | 3 | `public double pensionPerSeniorAt(double replacement)` | ...at another replacement rate, in today's money (0.7.36): what each senior would be paid if the dial read `replacement`, on the same wage base - the Policy tab's pension preview, which scaled today's bill by the rati... |
| 848 | 1 | `public double foundingPensionWage()` | The founding unskilled wage the pension is a share of, in today's unit at founding prices: the wage base over the level it is struck at (0.7.45, for the Policy tab's (i)). |
| 861 | 3 | `public void setLadder(WageLadder l)` | Takes the founding wage the pension and the default grant are struck from from a ladder (R0d); the grant a load restored is left where it was. |
| 870 | 4 | `public void foundOn(WageLadder l)` | A city founded on a ladder (R0d): its anchor and the pension's wage base on it. |
| 876 | 1 | `private double defaultFixedGrant()` | The default grant on this policy's founding wage: DEFAULT_FIXED_GRANT where that is PayTier's, the same share of the 1900 city's own. |
| 878 | 28 | `public void redenominate(double scale)` |  |

### WHAT A RIDE COSTS (2026-09-16) (lines 907-935)

### A RIDE IS NOT A MONTH (2026-09-17) (lines 936-990)

| line | len | member | says |
|---:|---:|---|---|
| 984 | 1 | `public double monthlyFare()` | What a month of riding costs one commuter, in thousands. |
| 987 | 1 | `public static double monthlyFareAt(double fare)` | ...at a fare the city has not set (0.7.38): the fare dial's preview, the same one multiplication. |

### THE FARE IS REAL (0.7.42, the anchor; star 9) (lines 991-1070)

| line | len | member | says |
|---:|---:|---|---|
| 1020 | 1 | `public double getTransitFare()` | The fare dial: a ride's price in founding money, in today's unit. |
| 1023 | 1 | `public double chargedFare()` | What a ride is charged today: the dial at the expected price level (THE FARE IS REAL). |
| 1026 | 3 | `public void setExpectedLevel(double level)` | Told by Game with every re-strike of the money constants; anything not positive is ignored. |
| 1031 | 1 | `public double getExpectedLevel()` | The level the fare is charged at. |
| 1044 | 3 | `public void setMoneyUnit(double unit)` | Told by Game.restrikeMoneyConstants() and Game.reformCurrency(); anything not positive is ignored. |
| 1049 | 1 | `public double maxTransitFare()` | The most the fare dial takes in today's unit: MAX_TRANSIT_FARE over the unit, where no car owner rides. |
| 1051 | 7 | `public void setTransitFare(double fare)` |  |
| 1065 | 5 | `public void realiseFare(double level)` | An older save's fare, read as the real fare that charges the same at the load month's expected level (0.7.42): the nominal fare over the level. |

### THE FUEL DUTY (0.7.103, batch P4; Jerus's A7, 2026-10-10) (lines 1071-1120)

| line | len | member | says |
|---:|---:|---|---|
| 1104 | 1 | `public double getFuelDuty()` | The fuel duty dial: founding money a litre of petrol at the pump, in today's unit. |
| 1107 | 1 | `public double chargedFuelDuty()` | What a litre is charged today: the dial at the expected price level (THE FUEL DUTY). |
| 1110 | 1 | `public double maxFuelDuty()` | The most the duty dial takes in today's unit: MAX_FUEL_DUTY over the unit. |
| 1112 | 8 | `public void setFuelDuty(double dutyALitre)` |  |

### PORT DUES (FR3, 2026-10-19; docs/queue/FR3.md 2, D9) (lines 1121-1193)

| line | len | member | says |
|---:|---:|---|---|
| 1147 | 1 | `public double getCapitalGainsInclusion()` |  |
| 1149 | 5 | `public void setCapitalGainsInclusion(double share)` |  |
| 1156 | 4 | `public double dividendTaxRate(double wageRate, String company)` | Integration credits the company profit tax already borne by a dividend (HT1, accepted call 2). |
| 1164 | 1 | `public double getPortDues()` | The port dues dial: the multiple of the default due Freight pays the city a tonne across a berth, 0 to MAX_PORT_DUES (FR3). |
| 1166 | 7 | `public void setPortDues(double multiple)` |  |
| 1175 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### THE CITY RATES (lines 1194-1289)

| line | len | member | says |
|---:|---:|---|---|
| 1212 | 3 | `public double getIncomeTaxRate()` | The three income taxes TOGETHER - what "the city rate" was until 0.7.4. |
| 1217 | 4 | `public boolean incomeRatesSplit()` | Whether the three income bases have parted - profit, sales and wage no longer one number. |
| 1223 | 1 | `public double getProfitTaxRate()` | What every sector's profit tax moves off (0.7.4). |
| 1226 | 1 | `public double getSalesTaxRate()` | What every sector's sales tax moves off (0.7.4). |
| 1229 | 1 | `public double getWageTaxRate()` | What every band's wage tax moves off (0.7.4). |
| 1231 | 7 | `public void setProfitTaxRate(double rate)` |  |
| 1239 | 7 | `public void setSalesTaxRate(double rate)` |  |
| 1247 | 7 | `public void setWageTaxRate(double rate)` |  |
| 1256 | 3 | `public double getPropertyTaxRate()` | The annual rate - what the player sets and what the screens show. |
| 1261 | 3 | `public double getMonthlyPropertyTaxRate()` | The annual rate divided by twelve. |
| 1272 | 8 | `public void setIncomeTaxRate(double rate)` | Every income tax at once: the profit, sales and wage bases all set to this rate (0.7.4), which is what the one city rate did. |
| 1282 | 7 | `public void setPropertyTaxRate(double annualRate)` | Takes the ANNUAL rate. |

### OFFSETS (lines 1290-1339)

| line | len | member | says |
|---:|---:|---|---|
| 1294 | 1 | `public double getWageOffset(WageBand band)` |  |
| 1295 | 1 | `public double getProfitOffset(String sector)` |  |
| 1296 | 1 | `public double getSalesOffset(String sector)` |  |
| 1297 | 1 | `public double getPropertyOffset(String sector)` |  |
| 1299 | 1 | `public double getProfitOffset(Sector s)` |  |
| 1300 | 1 | `public double getSalesOffset(Sector s)` |  |
| 1301 | 1 | `public double getPropertyOffset(Sector s)` |  |
| 1303 | 5 | `public void setWageOffset(WageBand band, double points)` |  |
| 1309 | 6 | `public void setProfitOffset(String sector, double points)` |  |
| 1316 | 6 | `public void setSalesOffset(String sector, double points)` |  |
| 1324 | 6 | `public void setPropertyOffset(String sector, double points)` | In ANNUAL points, matching the rate it offsets; held at MAX_PROPERTY_TAX either way (clampPropertyOffset()), on a load too. |
| 1332 | 3 | `private void offsetDecided(String tax, String who, double was, double now)` | An offset's line in the decision log, when it moved (0.7.23): "Profit tax, Retail, to -2 pts". |
| 1336 | 1 | `public void setProfitOffset(Sector s, double points)` |  |
| 1337 | 1 | `public void setSalesOffset(Sector s, double points)` |  |
| 1338 | 1 | `public void setPropertyOffset(Sector s, double points)` |  |

### EFFECTIVE RATES - the only numbers anything is ever charged at (lines 1340-1394)

| line | len | member | says |
|---:|---:|---|---|
| 1345 | 3 | `public double effectiveWageRate(WageBand band)` | What wages in this band are taxed at: the wage base and the band's offset. |
| 1350 | 3 | `public double effectiveProfitRate(String sector)` | What this sector's profit is taxed at: the profit base and the sector's offset. |
| 1355 | 3 | `public double effectiveSalesRate(String sector)` | What this sector charges on the value it adds: the sales base and its offset. |
| 1360 | 3 | `public double effectivePropertyRate(String sector)` | ANNUAL property tax rate for this sector. |
| 1365 | 3 | `public double effectiveMonthlyPropertyRate(String sector)` | ...and the monthly one, which is what is actually billed. |
| 1369 | 1 | `public double effectiveProfitRate(Sector s)` |  |
| 1370 | 1 | `public double effectiveSalesRate(Sector s)` |  |
| 1371 | 1 | `public double effectivePropertyRate(Sector s)` |  |
| 1372 | 1 | `public double effectiveMonthlyPropertyRate(Sector s)` |  |
| 1380 | 6 | `public double propertyTaxOn(double assessedValue)` | What one month's property tax comes to on a given assessed value. |
| 1388 | 6 | `public double propertyTaxOn(double assessedValue, String sector)` | One month's property tax at this sector's own rate. |

### WAGES (lines 1395-1469)

| line | len | member | says |
|---:|---:|---|---|
| 1407 | 4 | `public double payslipShare(WageBand band)` | What a payslip loses in this band (0.7.36): its wage tax at its own rate, the pension contribution and the EI and health premiums - the three promises are charged on the same payroll, flat across every band. |
| 1423 | 5 | `public double wageTaxOn(double[] wagePerType, double[] fillRate)` | The month's wage tax, summed job type by job type at its band's rate. |
| 1450 | 19 | `public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate)` | The same wage tax, split across the six pay tiers. |

### A COPY TO PREVIEW ON (0.7.36) (lines 1470-1519)

| line | len | member | says |
|---:|---:|---|---|
| 1485 | 34 | `public TaxPolicy copy()` | A detached copy of every dial (0.7.36): setters on it clamp as here and record nothing. |

### SAVE AND RESTORE (lines 1520-1846)

| line | len | member | says |
|---:|---:|---|---|
| 1566 | 86 | `public double[] getPolicyState()` | The city rates and the wage-band offsets as one array, city rates first - bar the three income bases of 0.7.4, which ride the end like every field added since. |
| 1661 | 6 | `public boolean restorePolicyState(double[] state)` | (STATE_BEFORE_EI) or longer than this build's (STATE_SLOTS); nothing is changed. |
| 1668 | 68 | `private void readPolicyState(double[] state)` |  |
| 1747 | 5 | `public void realiseFixedGrant(double priceIndex)` | An older save's FIXED grant, read as the real amount that pays the same at the load month's level (0.7.19): its nominal amount over the level, so the bill the month after the load is the bill the save was paying, and ... |
| 1754 | 4 | **type** `public static final class SectorOffsets` | One sector's three offsets, as the save carries them. |
| 1759 | 16 | `public java.util.List<SectorOffsets> getSectorOffsets()` |  |
| 1777 | 12 | `public void restoreSectorOffsets(java.util.List<SectorOffsets> saved)` | A sector the build does not have keeps its row - harmless, and it comes back if the sector does. |
| 1794 | 28 | `public void reset()` | Back to a policy's defaults, every dial at its default whatever the last city set. |
| 1823 | 6 | `private double clamp(double rate, double max)` |  |
| 1830 | 4 | `private double clampOffset(double points)` |  |
| 1842 | 4 | `private double clampPropertyOffset(double points)` | A property offset is held at MAX_PROPERTY_TAX either way (B5, 0.7.47), not MAX_OFFSET: the rate is the base plus the offset held within 0 and MAX_PROPERTY_TAX, and the base itself is within them, so no offset past the... |


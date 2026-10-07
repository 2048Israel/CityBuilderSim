# TaxPolicy.java - 1,641 lines · 103 methods · 41 constants · model

`ham/citybuildersim/TaxPolicy.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [DecisionLog](DecisionLog.md) (64), [EducationType](EducationType.md) (17), [WageBand](WageBand.md) (10), [Sector](Sector.md) (10), [Unemployment](Unemployment.md) (6), [PayTier](PayTier.md) (5), [SocialSecurity](SocialSecurity.md) (4), [JobType](JobType.md) (3), [Sectors](Sectors.md) (1)

**Used by (43):** [AgricultureCheck](AgricultureCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [CarCheck](CarCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [DataSave](DataSave.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureManager](InfrastructureManager.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SalesTaxLedger](SalesTaxLedger.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 60 | THE DECISION LOG (0.7.23) |
| 120 | FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) |
| 184 | THE PENSION PROMISE, AS TWO DIALS |
| 227 | EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS |
| 277 | · FOOD ASSISTANCE (0.7.43) |
| 344 | THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE |
| 702 | HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) |
| 839 | WHAT A RIDE COSTS (2026-09-16) |
| 868 | A RIDE IS NOT A MONTH (2026-09-17) |
| 923 | · THE FARE IS REAL (0.7.42, the anchor; star 9) |
| 1023 | THE CITY RATES |
| 1120 | OFFSETS |
| 1170 | EFFECTIVE RATES - the only numbers anything is ever charged at |
| 1225 | WAGES |
| 1300 | A COPY TO PREVIEW ON (0.7.36) |
| 1346 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 428 | `TaxPolicy.GrantBasis.WAGE_SHARE` | A share of the unskilled wage, per student per month - the founding rule until 0.7.19, and what an older save that chose nothing keeps. |
| 430 | `TaxPolicy.GrantBasis.FIXED` | A fixed REAL amount per student per month, in founding thousands, kept up with prices - by the expected price level since 0.7.42, the price index from 0.7.19 (a nominal amount until then) - the default since 0.7.19. |
| 432 | `TaxPolicy.GrantBasis.SURPLUS_SHARE` | A share of last month's budget surplus as ONE POOL, split evenly over this month's students; a deficit month pays nothing. |
| 434 | `TaxPolicy.GrantBasis.TUITION_SHARE` | A share of each student's own course tuition at today's price - after the tuition scale, before the subsidy. |

## Constants

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
| 248 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currency reform. |
| 263 | `TaxPolicy.DEFAULT_FIXED_GRANT` | `DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage()` | THE GRANT FOLLOWS PRICES (0.7.19): the default grant as a FIXED amount, in founding thousands - $525 a month, the Canada Student Grant (2026-27) this basis's share was read off, so at founding, when the price index is... |
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
| 493 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so x5 leaves room past ... |
| 525 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 737 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 740 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 743 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 746 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 863 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 866 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | The dial's cap, $50 a ride, twenty times the default, in founding money: past it no car owner rides while a journey's fuel costs under half of it, and the commuters with no car of their own still do (0.7.49, Infrastru... |
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

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private transient DecisionLog decisions` | Where a change to a dial here is written; null for a policy no city holds. |
| 115 | `private double profitTaxRate` | THE THREE INCOME BASES (0.7.4), one per tax, where there was one income rate - see CITY RATES, AND OFFSETS FROM THEM above. |
| 116 | `private double salesTaxRate` |  |
| 117 | `private double wageTaxRate` |  |
| 118 | `private double propertyTaxRate` |  |
| 162 | `private double farmlandRelief` |  |
| 199 | `private double contributionRate` |  |
| 200 | `private double pensionReplacement` |  |
| 239 | `private double eiPremiumRate` |  |
| 240 | `private double eiBenefitRate` |  |
| 297 | `private double foodAssistance` |  |
| 499 | `private GrantBasis grantBasis` | What the grant is a share of, or is. |
| 507 | `private double grantAmount` | The grant's one number, in the basis's own unit: a share of the wage, a fixed real amount in founding thousands, a share of the surplus, or a share of the tuition. |
| 510 | `private boolean fixedGrantNominal` | True while a FIXED amount read from a save before 0.7.19 is still nominal, until realiseFixedGrant() reads it at the load month's level (the expected price level since 0.7.42; the price index before). |
| 513 | `private double studentLoanRate` | Annual interest on the treasury's student loans. |
| 521 | `private final double[] tuitionScales` | The multiplier on the founding tuition table, one per school kind by EducationType ordinal (0.7.6). |
| 748 | `private double healthFeeScale` |  |
| 749 | `private double healthPremiumRate` |  |
| 811 | `private double pensionWageBase` | The wage a pension is a share of, carried in today's money - struck at the expected price level since 0.7.42 (seedConstants()). |
| 921 | `private double transitFare` |  |
| 946 | `private double expectedLevel` | The expected price level the fare is charged at; 1.0 until Game tells it. |
| 949 | `private boolean fareNominal` | True while a fare read from a save before 0.7.42 is still nominal, until realiseFare() reads it at the load month's level. |
| 973 | `private double moneyUnit` | The currency's unit (Denomination.getUnit(), B6, 0.7.47): the dial is founding money in today's unit and MAX_TRANSIT_FARE is founding money, so the dial stops at the cap over the unit (maxTransitFare()). |
| 1008 | `private final double[] wageOffset` |  |
| 1019 | `private final java.util.Map<String, Double> profitOffset` | THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector template). |
| 1020 | `private final java.util.Map<String, Double> salesOffset` |  |
| 1021 | `private final java.util.Map<String, Double> propertyOffset` |  |
| 1557 | `public String sector` |  |
| 1558 | `public double profit, sales, property` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1584 | **type** `public class TaxPolicy` | The city's tax rates - the revenue half of what the player actually decides. |

### THE DECISION LOG (0.7.23) (lines 60-119)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1 | `public void recordTo(DecisionLog log)` | Wires this policy to its city's decision log (Game.buildWorld()). |
| 77 | 3 | `private void decided(String kind, String label)` |  |

### FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) (lines 120-183)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 1 | `public double getFarmlandRelief()` | 0 assesses a field like a building lot; 1 assesses only what stands on it. |
| 167 | 7 | `public void setFarmlandRelief(double share)` |  |
| 180 | 3 | `public double assessedLandShare(String sector)` | The share of a sector's LAND that is on the assessment roll. |

### THE PENSION PROMISE, AS TWO DIALS (lines 184-226)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 1 | `public double getContributionRate()` |  |
| 209 | 1 | `public double getPensionReplacement()` |  |
| 211 | 7 | `public void setContributionRate(double rate)` |  |
| 219 | 7 | `public void setPensionReplacement(double share)` |  |

### EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS (lines 227-276)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 1 | `public double getEiPremiumRate()` |  |
| 275 | 1 | `public double getEiBenefitRate()` |  |

### FOOD ASSISTANCE (0.7.43) (lines 277-343)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 1 | `public double getFoodAssistance()` | The share of an eligible household's baskets, at the shelf price, the treasury's voucher covers. |
| 302 | 7 | `public void setFoodAssistance(double share)` |  |
| 315 | 3 | `public double getStudentGrantShare()` | The share of the unskilled wage a student is granted a month: the amount under the WAGE_SHARE basis, and nothing under any other, because under any other there is no such share. |
| 319 | 7 | `public void setEiPremiumRate(double rate)` |  |
| 327 | 7 | `public void setEiBenefitRate(double share)` |  |
| 336 | 7 | `public void setStudentGrantShare(double share)` | The grant as a share of the unskilled wage - the founding basis until 0.7.19, at this share. |

### THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE (lines 344-701)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 10 | **type** `public enum GrantBasis` | How the student grant is struck: what the one amount is a share of, or is. |
| 522 | 1 | `{ ... }` |  |
| 527 | 7 | `private static EducationType[] schoolKinds()` |  |
| 535 | 1 | `public GrantBasis getGrantBasis()` |  |
| 538 | 1 | `public double getGrantAmount()` | The amount that goes with getGrantBasis(), in that basis's unit. |
| 541 | 1 | `public double getStudentLoanRate()` | The annual rate a graduate is charged on the student loan while repaying it. |
| 553 | 1 | `public double getTuitionScale()` | The multiplier on the founding tuition table; 1 is the founding price, 0 is free at the point of use. |
| 556 | 4 | `public double tuitionScaleOf(EducationType type)` | One school kind's own multiplier (0.7.6); NONE, or null, reads the every-school one. |
| 562 | 7 | `public boolean tuitionScalesSplit()` | Whether the nine kinds' scales have parted - no longer one number (0.7.6). |
| 578 | 9 | `public double maxGrantAmount(GrantBasis basis)` | The ceiling on the amount under a basis: a share of a wage up to one wage, a fixed amount up to an unskilled wage (the founding wage in today's unit - a real amount against a real one since 0.7.19), the whole surplus,... |
| 589 | 7 | `public void setGrantBasis(GrantBasis basis)` | Picks the basis; the amount is clamped to the new basis's ceiling and otherwise left where it was. |
| 598 | 5 | `public void setGrantAmount(double amount)` | Sets the amount, in the current basis's unit, clamped to its ceiling. |
| 605 | 7 | `public void setGrant(GrantBasis basis, double amount)` | Both at once, so a screen can stage them together - and one line in the log for the two. |
| 614 | 10 | `private void grantDecided(GrantBasis wasBasis, double wasAmount)` | The grant's line in the decision log, when its basis or its amount moved (0.7.23). |
| 625 | 7 | `public void setStudentLoanRate(double annual)` |  |
| 639 | 9 | `public void setTuitionScale(double scale)` | Every school at once: all nine kinds set to this scale (0.7.6), which is what the one scale did. |
| 650 | 9 | `public void setTuitionScaleOf(EducationType type, double scale)` | One school kind's own scale, held to MAX_TUITION_SCALE (0.7.6); NONE, or null, is ignored. |
| 690 | 11 | `public static double grantBill(GrantBasis basis, double amount, double students, double unskilledWage, double priceIndex, doubl...` | The month's grant bill under any basis and amount - the one place the four rules are written, so the treasury's bill, the save's re-strike, the students' income and the Schools page's preview cannot disagree. |

### HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) (lines 702-838)

| line | len | member | says |
|---:|---:|---|---|
| 752 | 1 | `public double getHealthFeeScale()` | The multiplier on the three care fees; 1 is the founding fee, 0 is free at the point of use. |
| 755 | 1 | `public double getHealthPremiumRate()` | The share of every wage the health premium takes, employee side. |
| 757 | 7 | `public void setHealthFeeScale(double scale)` |  |
| 765 | 7 | `public void setHealthPremiumRate(double rate)` |  |
| 791 | 3 | `public double pensionPerSenior()` | A pension, in TODAY's money. |
| 803 | 3 | `public double pensionPerSeniorAt(double replacement)` | ...at another replacement rate, in today's money (0.7.36): what each senior would be paid if the dial read `replacement`, on the same wage base - the Policy tab's pension preview, which scaled today's bill by the rati... |
| 808 | 1 | `public double foundingPensionWage()` | The founding unskilled wage the pension is a share of, in today's unit at founding prices: the wage base over the level it is struck at (0.7.45, for the Policy tab's (i)). |
| 813 | 25 | `public void redenominate(double scale)` |  |

### WHAT A RIDE COSTS (2026-09-16) (lines 839-867)

### A RIDE IS NOT A MONTH (2026-09-17) (lines 868-922)

| line | len | member | says |
|---:|---:|---|---|
| 916 | 1 | `public double monthlyFare()` | What a month of riding costs one commuter, in thousands. |
| 919 | 1 | `public static double monthlyFareAt(double fare)` | ...at a fare the city has not set (0.7.38): the fare dial's preview, the same one multiplication. |

### THE FARE IS REAL (0.7.42, the anchor; star 9) (lines 923-1022)

| line | len | member | says |
|---:|---:|---|---|
| 952 | 1 | `public double getTransitFare()` | The fare dial: a ride's price in founding money, in today's unit. |
| 955 | 1 | `public double chargedFare()` | What a ride is charged today: the dial at the expected price level (THE FARE IS REAL). |
| 958 | 3 | `public void setExpectedLevel(double level)` | Told by Game with every re-strike of the money constants; anything not positive is ignored. |
| 963 | 1 | `public double getExpectedLevel()` | The level the fare is charged at. |
| 976 | 3 | `public void setMoneyUnit(double unit)` | Told by Game.restrikeMoneyConstants() and Game.reformCurrency(); anything not positive is ignored. |
| 981 | 1 | `public double maxTransitFare()` | The most the fare dial takes in today's unit: MAX_TRANSIT_FARE over the unit, where no car owner rides. |
| 983 | 7 | `public void setTransitFare(double fare)` |  |
| 997 | 5 | `public void realiseFare(double level)` | An older save's fare, read as the real fare that charges the same at the load month's expected level (0.7.42): the nominal fare over the level. |
| 1004 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### THE CITY RATES (lines 1023-1119)

| line | len | member | says |
|---:|---:|---|---|
| 1041 | 3 | `public double getIncomeTaxRate()` | The three income taxes TOGETHER - what "the city rate" was until 0.7.4. |
| 1046 | 4 | `public boolean incomeRatesSplit()` | Whether the three income bases have parted - profit, sales and wage no longer one number. |
| 1052 | 1 | `public double getProfitTaxRate()` | What every sector's profit tax moves off (0.7.4). |
| 1055 | 1 | `public double getSalesTaxRate()` | What every sector's sales tax moves off (0.7.4). |
| 1058 | 1 | `public double getWageTaxRate()` | What every band's wage tax moves off (0.7.4). |
| 1060 | 7 | `public void setProfitTaxRate(double rate)` |  |
| 1068 | 7 | `public void setSalesTaxRate(double rate)` |  |
| 1076 | 7 | `public void setWageTaxRate(double rate)` |  |
| 1085 | 3 | `public double getPropertyTaxRate()` | The annual rate - what the player sets and what the screens show. |
| 1090 | 3 | `public double getMonthlyPropertyTaxRate()` | The annual rate divided by twelve. |
| 1101 | 9 | `public void setIncomeTaxRate(double rate)` | Every income tax at once: the profit, sales and wage bases all set to this rate (0.7.4), which is what the one city rate did. |
| 1112 | 7 | `public void setPropertyTaxRate(double annualRate)` | Takes the ANNUAL rate. |

### OFFSETS (lines 1120-1169)

| line | len | member | says |
|---:|---:|---|---|
| 1124 | 1 | `public double getWageOffset(WageBand band)` |  |
| 1125 | 1 | `public double getProfitOffset(String sector)` |  |
| 1126 | 1 | `public double getSalesOffset(String sector)` |  |
| 1127 | 1 | `public double getPropertyOffset(String sector)` |  |
| 1129 | 1 | `public double getProfitOffset(Sector s)` |  |
| 1130 | 1 | `public double getSalesOffset(Sector s)` |  |
| 1131 | 1 | `public double getPropertyOffset(Sector s)` |  |
| 1133 | 5 | `public void setWageOffset(WageBand band, double points)` |  |
| 1139 | 6 | `public void setProfitOffset(String sector, double points)` |  |
| 1146 | 6 | `public void setSalesOffset(String sector, double points)` |  |
| 1154 | 6 | `public void setPropertyOffset(String sector, double points)` | In ANNUAL points, matching the rate it offsets; held at MAX_PROPERTY_TAX either way (clampPropertyOffset()), on a load too. |
| 1162 | 3 | `private void offsetDecided(String tax, String who, double was, double now)` | An offset's line in the decision log, when it moved (0.7.23): "Profit tax, Retail, to -2 pts". |
| 1166 | 1 | `public void setProfitOffset(Sector s, double points)` |  |
| 1167 | 1 | `public void setSalesOffset(Sector s, double points)` |  |
| 1168 | 1 | `public void setPropertyOffset(Sector s, double points)` |  |

### EFFECTIVE RATES - the only numbers anything is ever charged at (lines 1170-1224)

| line | len | member | says |
|---:|---:|---|---|
| 1175 | 3 | `public double effectiveWageRate(WageBand band)` | What wages in this band are taxed at: the wage base and the band's offset. |
| 1180 | 3 | `public double effectiveProfitRate(String sector)` | What this sector's profit is taxed at: the profit base and the sector's offset. |
| 1185 | 3 | `public double effectiveSalesRate(String sector)` | What this sector charges on the value it adds: the sales base and its offset. |
| 1190 | 3 | `public double effectivePropertyRate(String sector)` | ANNUAL property tax rate for this sector. |
| 1195 | 3 | `public double effectiveMonthlyPropertyRate(String sector)` | ...and the monthly one, which is what is actually billed. |
| 1199 | 1 | `public double effectiveProfitRate(Sector s)` |  |
| 1200 | 1 | `public double effectiveSalesRate(Sector s)` |  |
| 1201 | 1 | `public double effectivePropertyRate(Sector s)` |  |
| 1202 | 1 | `public double effectiveMonthlyPropertyRate(Sector s)` |  |
| 1210 | 6 | `public double propertyTaxOn(double assessedValue)` | What one month's property tax comes to on a given assessed value. |
| 1218 | 6 | `public double propertyTaxOn(double assessedValue, String sector)` | One month's property tax at this sector's own rate. |

### WAGES (lines 1225-1299)

| line | len | member | says |
|---:|---:|---|---|
| 1237 | 4 | `public double payslipShare(WageBand band)` | What a payslip loses in this band (0.7.36): its wage tax at its own rate, the pension contribution and the EI and health premiums - the three promises are charged on the same payroll, flat across every band. |
| 1253 | 5 | `public double wageTaxOn(double[] wagePerType, double[] fillRate)` | The month's wage tax, summed job type by job type at its band's rate. |
| 1280 | 19 | `public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate)` | The same wage tax, split across the six pay tiers. |

### A COPY TO PREVIEW ON (0.7.36) (lines 1300-1345)

| line | len | member | says |
|---:|---:|---|---|
| 1315 | 30 | `public TaxPolicy copy()` | A detached copy of every dial (0.7.36): setters on it clamp as here and record nothing. |

### SAVE AND RESTORE (lines 1346-1641)

| line | len | member | says |
|---:|---:|---|---|
| 1384 | 79 | `public double[] getPolicyState()` | The city rates and the wage-band offsets as one array, city rates first - bar the three income bases of 0.7.4, which ride the end like every field added since. |
| 1472 | 66 | `public boolean restorePolicyState(double[] state)` | (STATE_BEFORE_EI) or longer than this build's (STATE_SLOTS); nothing is changed. |
| 1549 | 5 | `public void realiseFixedGrant(double priceIndex)` | An older save's FIXED grant, read as the real amount that pays the same at the load month's level (0.7.19): its nominal amount over the level, so the bill the month after the load is the bill the save was paying, and ... |
| 1556 | 4 | **type** `public static final class SectorOffsets` | One sector's three offsets, as the save carries them. |
| 1561 | 16 | `public java.util.List<SectorOffsets> getSectorOffsets()` |  |
| 1579 | 12 | `public void restoreSectorOffsets(java.util.List<SectorOffsets> saved)` | A sector the build does not have keeps its row - harmless, and it comes back if the sector does. |
| 1592 | 25 | `public void reset()` |  |
| 1618 | 6 | `private double clamp(double rate, double max)` |  |
| 1625 | 4 | `private double clampOffset(double points)` |  |
| 1637 | 4 | `private double clampPropertyOffset(double points)` | A property offset is held at MAX_PROPERTY_TAX either way (B5, 0.7.47), not MAX_OFFSET: the rate is the base plus the offset held within 0 and MAX_PROPERTY_TAX, and the base itself is within them, so no offset past the... |


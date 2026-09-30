# TaxPolicy.java - 1,261 lines · 85 methods · 37 constants · model

`ham/citybuildersim/TaxPolicy.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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

**Uses:** [EducationType](EducationType.md) (17), [Sector](Sector.md) (10), [WageBand](WageBand.md) (9), [Unemployment](Unemployment.md) (6), [PayTier](PayTier.md) (5), [SocialSecurity](SocialSecurity.md) (4), [JobType](JobType.md) (3), [Sectors](Sectors.md) (1)

**Used by (30):** [AgricultureCheck](AgricultureCheck.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureManager](InfrastructureManager.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SalesTaxLedger](SalesTaxLedger.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 98 | FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) |
| 158 | THE PENSION PROMISE, AS TWO DIALS |
| 193 | EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS |
| 260 | THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE |
| 577 | HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) |
| 682 | WHAT A RIDE COSTS (2026-09-16) |
| 706 | A RIDE IS NOT A MONTH (2026-09-17) |
| 784 | THE CITY RATES |
| 854 | OFFSETS |
| 888 | EFFECTIVE RATES - the only numbers anything is ever charged at |
| 943 | WAGES |
| 1005 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 344 | `TaxPolicy.GrantBasis.WAGE_SHARE` | A share of the unskilled wage, per student per month - the founding rule until 0.7.19, and what an older save that chose nothing keeps. |
| 346 | `TaxPolicy.GrantBasis.FIXED` | A fixed REAL amount per student per month, in founding thousands, kept up with the price index (0.7.19; a nominal amount until then) - the default since 0.7.19. |
| 348 | `TaxPolicy.GrantBasis.SURPLUS_SHARE` | A share of last month's budget surplus as ONE POOL, split evenly over this month's students; a deficit month pays nothing. |
| 350 | `TaxPolicy.GrantBasis.TUITION_SHARE` | A share of each student's own course tuition at today's price - after the tuition scale, before the subsidy. |

## Constants

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
| 214 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currency reform. |
| 228 | `TaxPolicy.DEFAULT_FIXED_GRANT` | `DEFAULT_STUDENT_GRANT_SHARE * PayTier.UNSKILLED.getMonthlyWage()` | THE GRANT FOLLOWS PRICES (0.7.19): the default grant as a FIXED amount, in founding thousands - $525 a month, the Canada Student Grant (2026-27) this basis's share was read off, so at founding, when the price index is... |
| 231 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 234 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 237 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 387 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.FIXED` | Where the grant starts: a fixed real amount, kept up with the price index (0.7.19; the founding rule, a share of the unskilled wage, until then). |
| 390 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 393 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 396 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 399 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 402 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 405 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 408 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so x5 leaves room past ... |
| 440 | `TaxPolicy.SCHOOL_KINDS` | `schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). |
| 612 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 615 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 618 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 621 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 701 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 704 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | Past this nobody rides at all, as a multiple of the default. |
| 745 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 1010 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 1013 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 1016 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 1019 | `TaxPolicy.STATE_BEFORE_SPLIT` | `STATE_BEFORE_EDUCATION + 4` | ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. |
| 1022 | `TaxPolicy.STATE_BEFORE_SCHOOLS` | `STATE_BEFORE_SPLIT + 3` | ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. |
| 1025 | `TaxPolicy.STATE_BEFORE_REAL_GRANT` | `STATE_BEFORE_SCHOOLS + EducationType.values().length - 1` | ...and one from before the real FIXED grant (0.7.19): a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. |
| 1028 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_REAL_GRANT + 1` | This build's array: one slot on top saying the FIXED amount is real, in founding money (0.7.19) - see realiseFixedGrant(). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `private double profitTaxRate` | THE THREE INCOME BASES (0.7.4), one per tax, where there was one income rate - see CITY RATES, AND OFFSETS FROM THEM above. |
| 94 | `private double salesTaxRate` |  |
| 95 | `private double wageTaxRate` |  |
| 96 | `private double propertyTaxRate` |  |
| 140 | `private double farmlandRelief` |  |
| 173 | `private double contributionRate` |  |
| 174 | `private double pensionReplacement` |  |
| 205 | `private double eiPremiumRate` |  |
| 206 | `private double eiBenefitRate` |  |
| 414 | `private GrantBasis grantBasis` | What the grant is a share of, or is. |
| 422 | `private double grantAmount` | The grant's one number, in the basis's own unit: a share of the wage, a fixed real amount in founding thousands, a share of the surplus, or a share of the tuition. |
| 425 | `private boolean fixedGrantNominal` | True while a FIXED amount read from a save before 0.7.19 is still nominal, until realiseFixedGrant() reads it at the load month's price index. |
| 428 | `private double studentLoanRate` | Annual interest on the treasury's student loans. |
| 436 | `private final double[] tuitionScales` | The multiplier on the founding tuition table, one per school kind by EducationType ordinal (0.7.6). |
| 623 | `private double healthFeeScale` |  |
| 624 | `private double healthPremiumRate` |  |
| 655 | `private double pensionWageBase` | The wage a pension is a share of, carried in today's money. |
| 756 | `private double transitFare` |  |
| 769 | `private final double[] wageOffset` |  |
| 780 | `private final java.util.Map<String, Double> profitOffset` | THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector template). |
| 781 | `private final java.util.Map<String, Double> salesOffset` |  |
| 782 | `private final java.util.Map<String, Double> propertyOffset` |  |
| 1193 | `public String sector` |  |
| 1194 | `public double profit, sales, property` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1204 | **type** `public class TaxPolicy` | The city's tax rates - the revenue half of what the player actually decides. |

### FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) (lines 98-157)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 1 | `public double getFarmlandRelief()` | 0 assesses a field like a building lot; 1 assesses only what stands on it. |
| 145 | 3 | `public void setFarmlandRelief(double share)` |  |
| 154 | 3 | `public double assessedLandShare(String sector)` | The share of a sector's LAND that is on the assessment roll. |

### THE PENSION PROMISE, AS TWO DIALS (lines 158-192)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 1 | `public double getContributionRate()` |  |
| 183 | 1 | `public double getPensionReplacement()` |  |
| 185 | 3 | `public void setContributionRate(double rate)` |  |
| 189 | 3 | `public void setPensionReplacement(double share)` |  |

### EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS (lines 193-259)

| line | len | member | says |
|---:|---:|---|---|
| 239 | 1 | `public double getEiPremiumRate()` |  |
| 240 | 1 | `public double getEiBenefitRate()` |  |
| 247 | 3 | `public double getStudentGrantShare()` | The share of the unskilled wage a student is granted a month: the amount under the WAGE_SHARE basis, and nothing under any other, because under any other there is no such share. |
| 251 | 1 | `public void setEiPremiumRate(double rate)` |  |
| 252 | 1 | `public void setEiBenefitRate(double share)` |  |
| 255 | 4 | `public void setStudentGrantShare(double share)` | The grant as a share of the unskilled wage - the founding basis until 0.7.19, at this share. |

### THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE (lines 260-576)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 10 | **type** `public enum GrantBasis` | How the student grant is struck: what the one amount is a share of, or is. |
| 437 | 1 | `{ ... }` |  |
| 442 | 7 | `private static EducationType[] schoolKinds()` |  |
| 450 | 1 | `public GrantBasis getGrantBasis()` |  |
| 453 | 1 | `public double getGrantAmount()` | The amount that goes with getGrantBasis(), in that basis's unit. |
| 456 | 1 | `public double getStudentLoanRate()` | The annual rate a graduate is charged on the student loan while repaying it. |
| 468 | 1 | `public double getTuitionScale()` | The multiplier on the founding tuition table; 1 is the founding price, 0 is free at the point of use. |
| 471 | 4 | `public double tuitionScaleOf(EducationType type)` | One school kind's own multiplier (0.7.6); NONE, or null, reads the every-school one. |
| 477 | 7 | `public boolean tuitionScalesSplit()` | Whether the nine kinds' scales have parted - no longer one number (0.7.6). |
| 491 | 9 | `public double maxGrantAmount(GrantBasis basis)` | The ceiling on the amount under a basis: a share of a wage up to one wage, a fixed amount up to an unskilled wage (the founding wage in today's unit, which pensionWageBase carries - a real amount against a real one si... |
| 502 | 4 | `public void setGrantBasis(GrantBasis basis)` | Picks the basis; the amount is clamped to the new basis's ceiling and otherwise left where it was. |
| 508 | 3 | `public void setGrantAmount(double amount)` | Sets the amount, in the current basis's unit, clamped to its ceiling. |
| 513 | 4 | `public void setGrant(GrantBasis basis, double amount)` | Both at once, so a screen can stage them together. |
| 518 | 1 | `public void setStudentLoanRate(double annual)` |  |
| 526 | 4 | `public void setTuitionScale(double scale)` | Every school at once: all nine kinds set to this scale (0.7.6), which is what the one scale did. |
| 532 | 4 | `public void setTuitionScaleOf(EducationType type, double scale)` | One school kind's own scale, held to MAX_TUITION_SCALE (0.7.6); NONE, or null, is ignored. |
| 565 | 11 | `public static double grantBill(GrantBasis basis, double amount, double students, double unskilledWage, double priceIndex, doubl...` | The month's grant bill under any basis and amount - the one place the four rules are written, so the treasury's bill, the save's re-strike, the students' income and the Schools page's preview cannot disagree. |

### HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) (lines 577-681)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 1 | `public double getHealthFeeScale()` | The multiplier on the three care fees; 1 is the founding fee, 0 is free at the point of use. |
| 630 | 1 | `public double getHealthPremiumRate()` | The share of every wage the health premium takes, employee side. |
| 632 | 1 | `public void setHealthFeeScale(double scale)` |  |
| 633 | 1 | `public void setHealthPremiumRate(double rate)` |  |
| 650 | 3 | `public double pensionPerSenior()` | A pension, in TODAY's money. |
| 657 | 24 | `public void redenominate(double scale)` |  |

### WHAT A RIDE COSTS (2026-09-16) (lines 682-705)

### A RIDE IS NOT A MONTH (2026-09-17) (lines 706-783)

| line | len | member | says |
|---:|---:|---|---|
| 754 | 1 | `public double monthlyFare()` | What a month of riding costs one commuter, in thousands. |
| 758 | 1 | `public double getTransitFare()` |  |
| 760 | 3 | `public void setTransitFare(double fare)` |  |
| 765 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### THE CITY RATES (lines 784-853)

| line | len | member | says |
|---:|---:|---|---|
| 802 | 3 | `public double getIncomeTaxRate()` | The three income taxes TOGETHER - what "the city rate" was until 0.7.4. |
| 807 | 4 | `public boolean incomeRatesSplit()` | Whether the three income bases have parted - profit, sales and wage no longer one number. |
| 813 | 1 | `public double getProfitTaxRate()` | What every sector's profit tax moves off (0.7.4). |
| 816 | 1 | `public double getSalesTaxRate()` | What every sector's sales tax moves off (0.7.4). |
| 819 | 1 | `public double getWageTaxRate()` | What every band's wage tax moves off (0.7.4). |
| 821 | 1 | `public void setProfitTaxRate(double rate)` |  |
| 822 | 1 | `public void setSalesTaxRate(double rate)` |  |
| 823 | 1 | `public void setWageTaxRate(double rate)` |  |
| 826 | 3 | `public double getPropertyTaxRate()` | The annual rate - what the player sets and what the screens show. |
| 831 | 3 | `public double getMonthlyPropertyTaxRate()` | The annual rate divided by twelve. |
| 842 | 6 | `public void setIncomeTaxRate(double rate)` | Every income tax at once: the profit, sales and wage bases all set to this rate (0.7.4), which is what the one city rate did. |
| 850 | 3 | `public void setPropertyTaxRate(double annualRate)` | Takes the ANNUAL rate. |

### OFFSETS (lines 854-887)

| line | len | member | says |
|---:|---:|---|---|
| 858 | 1 | `public double getWageOffset(WageBand band)` |  |
| 859 | 1 | `public double getProfitOffset(String sector)` |  |
| 860 | 1 | `public double getSalesOffset(String sector)` |  |
| 861 | 1 | `public double getPropertyOffset(String sector)` |  |
| 863 | 1 | `public double getProfitOffset(Sector s)` |  |
| 864 | 1 | `public double getSalesOffset(Sector s)` |  |
| 865 | 1 | `public double getPropertyOffset(Sector s)` |  |
| 867 | 3 | `public void setWageOffset(WageBand band, double points)` |  |
| 871 | 3 | `public void setProfitOffset(String sector, double points)` |  |
| 875 | 3 | `public void setSalesOffset(String sector, double points)` |  |
| 880 | 3 | `public void setPropertyOffset(String sector, double points)` | In ANNUAL points, matching the rate it offsets. |
| 884 | 1 | `public void setProfitOffset(Sector s, double points)` |  |
| 885 | 1 | `public void setSalesOffset(Sector s, double points)` |  |
| 886 | 1 | `public void setPropertyOffset(Sector s, double points)` |  |

### EFFECTIVE RATES - the only numbers anything is ever charged at (lines 888-942)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 3 | `public double effectiveWageRate(WageBand band)` | What wages in this band are taxed at: the wage base and the band's offset. |
| 898 | 3 | `public double effectiveProfitRate(String sector)` | What this sector's profit is taxed at: the profit base and the sector's offset. |
| 903 | 3 | `public double effectiveSalesRate(String sector)` | What this sector charges on the value it adds: the sales base and its offset. |
| 908 | 3 | `public double effectivePropertyRate(String sector)` | ANNUAL property tax rate for this sector. |
| 913 | 3 | `public double effectiveMonthlyPropertyRate(String sector)` | ...and the monthly one, which is what is actually billed. |
| 917 | 1 | `public double effectiveProfitRate(Sector s)` |  |
| 918 | 1 | `public double effectiveSalesRate(Sector s)` |  |
| 919 | 1 | `public double effectivePropertyRate(Sector s)` |  |
| 920 | 1 | `public double effectiveMonthlyPropertyRate(Sector s)` |  |
| 928 | 6 | `public double propertyTaxOn(double assessedValue)` | What one month's property tax comes to on a given assessed value. |
| 936 | 6 | `public double propertyTaxOn(double assessedValue, String sector)` | One month's property tax at this sector's own rate. |

### WAGES (lines 943-1004)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 5 | `public double wageTaxOn(double[] wagePerType, double[] fillRate)` | The month's wage tax, summed job type by job type at its band's rate. |
| 985 | 19 | `public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate)` | The same wage tax, split across the six pay tiers. |

### SAVE AND RESTORE (lines 1005-1261)

| line | len | member | says |
|---:|---:|---|---|
| 1037 | 72 | `public double[] getPolicyState()` | The city rates and the wage-band offsets as one array, city rates first - bar the three income bases of 0.7.4, which ride the end like every field added since. |
| 1118 | 58 | `public boolean restorePolicyState(double[] state)` | (STATE_BEFORE_EI) or longer than this build's (STATE_SLOTS); nothing is changed. |
| 1185 | 5 | `public void realiseFixedGrant(double priceIndex)` | An older save's FIXED grant, read as the real amount that pays the same at the load month's price index (0.7.19): its nominal amount over the index, so the bill the month after the load is the bill the save was paying... |
| 1192 | 4 | **type** `public static final class SectorOffsets` | One sector's three offsets, as the save carries them. |
| 1197 | 16 | `public java.util.List<SectorOffsets> getSectorOffsets()` |  |
| 1215 | 12 | `public void restoreSectorOffsets(java.util.List<SectorOffsets> saved)` | A sector the build does not have keeps its row - harmless, and it comes back if the sector does. |
| 1228 | 21 | `public void reset()` |  |
| 1250 | 6 | `private double clamp(double rate, double max)` |  |
| 1257 | 4 | `private double clampOffset(double points)` |  |


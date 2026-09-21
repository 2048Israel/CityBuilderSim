# TaxPolicy.java - 1,008 lines · 73 methods · 32 constants · model

`ham/citybuildersim/TaxPolicy.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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
> There are two city-wide rates, and then every band and every sector carries an
> OFFSET in rate points from one of them. Industry at -0.03 pays three points
> under whatever the city rate is.
> 
> Jerus's call, and it is the right one for a game where the city rate is a
> lever the player pulls often: raise the city rate and every sector you have
> customised keeps its relative treatment instead of being silently left behind.
> The cost is that a rate on screen is arithmetic rather than a number, so every
> screen shows the offset AND what it resolves to.
> 
> RESOLUTION HAPPENS HERE, ONCE. effective*() clamps to [0, max]. No caller
> adds an offset itself: an offset that escapes clamping is a negative tax rate,
> which is the city paying businesses to trade, and it would show up as revenue
> appearing from nowhere three layers away from the line that caused it.
> 
> DEFAULTS ARE ALL ZERO, which makes every effective rate the city rate and
> reproduces the single-rate behaviour this replaced, exactly.

**Uses:** [Sector](Sector.md) (10), [WageBand](WageBand.md) (9), [Unemployment](Unemployment.md) (6), [SocialSecurity](SocialSecurity.md) (4), [PayTier](PayTier.md) (4), [JobType](JobType.md) (3), [Sectors](Sectors.md) (1)

**Used by (24):** [AgricultureCheck](AgricultureCheck.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureManager](InfrastructureManager.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SalesTaxLedger](SalesTaxLedger.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 80 | FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) |
| 140 | THE PENSION PROMISE, AS TWO DIALS |
| 175 | EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS |
| 227 | THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE |
| 435 | HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) |
| 537 | WHAT A RIDE COSTS (2026-09-16) |
| 561 | A RIDE IS NOT A MONTH (2026-09-17) |
| 639 | THE CITY RATES |
| 666 | OFFSETS |
| 700 | EFFECTIVE RATES - the only numbers anything is ever charged at |
| 755 | WAGES |
| 817 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 298 | `TaxPolicy.GrantBasis.WAGE_SHARE` | A share of the unskilled wage, per student per month - the founding rule, and the default. |
| 300 | `TaxPolicy.GrantBasis.FIXED` | A fixed amount per student per month, in thousands: money, so a currency reform scales it. |
| 302 | `TaxPolicy.GrantBasis.SURPLUS_SHARE` | A share of last month's budget surplus as ONE POOL, split evenly over this month's students; a deficit month pays nothing. |
| 304 | `TaxPolicy.GrantBasis.TUITION_SHARE` | A share of each student's own course tuition at today's price - after the tuition scale, before the subsidy. |

## Constants

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
| 195 | `TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE` | `525.0 / 3_460` | The Canada Student Grant, $525 a month of study (2026-27), as a share of the $3,460 unskilled median the ladder is anchored on - so it moves with the wage it was measured against and with a currency reform. |
| 198 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 201 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 204 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. |
| 308 | `TaxPolicy.DEFAULT_GRANT_BASIS` | `GrantBasis.WAGE_SHARE` | Where the grant starts: the founding rule, a share of the unskilled wage. |
| 311 | `TaxPolicy.MAX_FIXED_GRANT_WAGES` | `1.00` | A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. |
| 314 | `TaxPolicy.MAX_SURPLUS_GRANT_SHARE` | `1.00` | The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. |
| 317 | `TaxPolicy.MAX_TUITION_GRANT_SHARE` | `2.00` | Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. |
| 320 | `TaxPolicy.DEFAULT_STUDENT_LOAN_RATE` | `0` | Where the loan rate starts: no interest, as Canada's loans have been since April 2023. |
| 323 | `TaxPolicy.MAX_STUDENT_LOAN_RATE` | `.15` | Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. |
| 326 | `TaxPolicy.DEFAULT_TUITION_SCALE` | `1.0` | Where the tuition scale starts: the founding table, unscaled. |
| 329 | `TaxPolicy.MAX_TUITION_SCALE` | `5.0` | Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so x5 leaves room past ... |
| 470 | `TaxPolicy.DEFAULT_HEALTH_FEE_SCALE` | `1.0` | Where the fee scale starts: the founding fees, unscaled. |
| 473 | `TaxPolicy.MAX_HEALTH_FEE_SCALE` | `15.0` | Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. |
| 476 | `TaxPolicy.DEFAULT_HEALTH_PREMIUM` | `0` | Where the health premium starts: nobody pays one until the player says so. |
| 479 | `TaxPolicy.MAX_HEALTH_PREMIUM` | `.10` | A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. |
| 556 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 559 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | Past this nobody rides at all, as a multiple of the default. |
| 600 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |
| 822 | `TaxPolicy.STATE_BEFORE_EI` | `4 + WageBand.values().length` | The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. |
| 825 | `TaxPolicy.STATE_BEFORE_HEALTH` | `STATE_BEFORE_EI + 3 + 1 + 1` | ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. |
| 828 | `TaxPolicy.STATE_BEFORE_EDUCATION` | `STATE_BEFORE_HEALTH + 2` | ...and one from before the education dials of 2026-09-21: the two health dials on top. |
| 831 | `TaxPolicy.STATE_SLOTS` | `STATE_BEFORE_EDUCATION + 4` | This build's array: the grant's basis and amount, the loan rate and the tuition scale on top. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `private double incomeTaxRate` |  |
| 78 | `private double propertyTaxRate` |  |
| 122 | `private double farmlandRelief` |  |
| 155 | `private double contributionRate` |  |
| 156 | `private double pensionReplacement` |  |
| 187 | `private double eiPremiumRate` |  |
| 188 | `private double eiBenefitRate` |  |
| 335 | `private GrantBasis grantBasis` | What the grant is a share of, or is. |
| 343 | `private double grantAmount` | The grant's one number, in the basis's own unit: a share of the wage, a fixed amount in thousands, a share of the surplus, or a share of the tuition. |
| 346 | `private double studentLoanRate` | Annual interest on the treasury's student loans. |
| 349 | `private double tuitionScale` | The multiplier on the founding tuition table. |
| 481 | `private double healthFeeScale` |  |
| 482 | `private double healthPremiumRate` |  |
| 513 | `private double pensionWageBase` | The wage a pension is a share of, carried in today's money. |
| 611 | `private double transitFare` |  |
| 624 | `private final double[] wageOffset` |  |
| 635 | `private final java.util.Map<String, Double> profitOffset` | THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector template). |
| 636 | `private final java.util.Map<String, Double> salesOffset` |  |
| 637 | `private final java.util.Map<String, Double> propertyOffset` |  |
| 941 | `public String sector` |  |
| 942 | `public double profit, sales, property` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 962 | **type** `public class TaxPolicy` | The city's tax rates - the revenue half of what the player actually decides. |

### FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) (lines 80-139)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 1 | `public double getFarmlandRelief()` | 0 assesses a field like a building lot; 1 assesses only what stands on it. |
| 127 | 3 | `public void setFarmlandRelief(double share)` |  |
| 136 | 3 | `public double assessedLandShare(String sector)` | The share of a sector's LAND that is on the assessment roll. |

### THE PENSION PROMISE, AS TWO DIALS (lines 140-174)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 1 | `public double getContributionRate()` |  |
| 165 | 1 | `public double getPensionReplacement()` |  |
| 167 | 3 | `public void setContributionRate(double rate)` |  |
| 171 | 3 | `public void setPensionReplacement(double share)` |  |

### EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS (lines 175-226)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 1 | `public double getEiPremiumRate()` |  |
| 207 | 1 | `public double getEiBenefitRate()` |  |
| 214 | 3 | `public double getStudentGrantShare()` | The share of the unskilled wage a student is granted a month: the amount under the WAGE_SHARE basis, and nothing under any other, because under any other there is no such share. |
| 218 | 1 | `public void setEiPremiumRate(double rate)` |  |
| 219 | 1 | `public void setEiBenefitRate(double share)` |  |
| 222 | 4 | `public void setStudentGrantShare(double share)` | The grant as a share of the unskilled wage - the founding basis, at this share. |

### THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE (lines 227-434)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 10 | **type** `public enum GrantBasis` | How the student grant is struck: what the one amount is a share of, or is. |
| 351 | 1 | `public GrantBasis getGrantBasis()` |  |
| 354 | 1 | `public double getGrantAmount()` | The amount that goes with getGrantBasis(), in that basis's unit. |
| 357 | 1 | `public double getStudentLoanRate()` | The annual rate a graduate is charged on the student loan while repaying it. |
| 360 | 1 | `public double getTuitionScale()` | The multiplier on the founding tuition table; 1 is the founding price, 0 is free at the point of use. |
| 368 | 9 | `public double maxGrantAmount(GrantBasis basis)` | The ceiling on the amount under a basis: a share of a wage up to one wage, a fixed amount up to an unskilled wage (the founding wage in today's money, which pensionWageBase carries), the whole surplus, twice the tuition. |
| 379 | 4 | `public void setGrantBasis(GrantBasis basis)` | Picks the basis; the amount is clamped to the new basis's ceiling and otherwise left where it was. |
| 385 | 3 | `public void setGrantAmount(double amount)` | Sets the amount, in the current basis's unit, clamped to its ceiling. |
| 390 | 4 | `public void setGrant(GrantBasis basis, double amount)` | Both at once, so a screen can stage them together. |
| 395 | 1 | `public void setStudentLoanRate(double annual)` |  |
| 396 | 1 | `public void setTuitionScale(double scale)` |  |
| 423 | 11 | `public static double grantBill(GrantBasis basis, double amount, double students, double unskilledWage, double lastSurplus, doub...` | The month's grant bill under any basis and amount - the one place the four rules are written, so the treasury's bill, the save's re-strike, the students' income and the Schools page's preview cannot disagree. |

### HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19) (lines 435-536)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 1 | `public double getHealthFeeScale()` | The multiplier on the three care fees; 1 is the founding fee, 0 is free at the point of use. |
| 488 | 1 | `public double getHealthPremiumRate()` | The share of every wage the health premium takes, employee side. |
| 490 | 1 | `public void setHealthFeeScale(double scale)` |  |
| 491 | 1 | `public void setHealthPremiumRate(double rate)` |  |
| 508 | 3 | `public double pensionPerSenior()` | A pension, in TODAY's money. |
| 515 | 21 | `public void redenominate(double scale)` |  |

### WHAT A RIDE COSTS (2026-09-16) (lines 537-560)

### A RIDE IS NOT A MONTH (2026-09-17) (lines 561-638)

| line | len | member | says |
|---:|---:|---|---|
| 609 | 1 | `public double monthlyFare()` | What a month of riding costs one commuter, in thousands. |
| 613 | 1 | `public double getTransitFare()` |  |
| 615 | 3 | `public void setTransitFare(double fare)` |  |
| 620 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### THE CITY RATES (lines 639-665)

| line | len | member | says |
|---:|---:|---|---|
| 643 | 3 | `public double getIncomeTaxRate()` |  |
| 648 | 3 | `public double getPropertyTaxRate()` | The annual rate - what the player sets and what the screens show. |
| 653 | 3 | `public double getMonthlyPropertyTaxRate()` | The annual rate divided by twelve. |
| 657 | 3 | `public void setIncomeTaxRate(double rate)` |  |
| 662 | 3 | `public void setPropertyTaxRate(double annualRate)` | Takes the ANNUAL rate. |

### OFFSETS (lines 666-699)

| line | len | member | says |
|---:|---:|---|---|
| 670 | 1 | `public double getWageOffset(WageBand band)` |  |
| 671 | 1 | `public double getProfitOffset(String sector)` |  |
| 672 | 1 | `public double getSalesOffset(String sector)` |  |
| 673 | 1 | `public double getPropertyOffset(String sector)` |  |
| 675 | 1 | `public double getProfitOffset(Sector s)` |  |
| 676 | 1 | `public double getSalesOffset(Sector s)` |  |
| 677 | 1 | `public double getPropertyOffset(Sector s)` |  |
| 679 | 3 | `public void setWageOffset(WageBand band, double points)` |  |
| 683 | 3 | `public void setProfitOffset(String sector, double points)` |  |
| 687 | 3 | `public void setSalesOffset(String sector, double points)` |  |
| 692 | 3 | `public void setPropertyOffset(String sector, double points)` | In ANNUAL points, matching the rate it offsets. |
| 696 | 1 | `public void setProfitOffset(Sector s, double points)` |  |
| 697 | 1 | `public void setSalesOffset(Sector s, double points)` |  |
| 698 | 1 | `public void setPropertyOffset(Sector s, double points)` |  |

### EFFECTIVE RATES - the only numbers anything is ever charged at (lines 700-754)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 3 | `public double effectiveWageRate(WageBand band)` | What wages in this band are taxed at. |
| 710 | 3 | `public double effectiveProfitRate(String sector)` | What this sector's profit is taxed at. |
| 715 | 3 | `public double effectiveSalesRate(String sector)` | What this sector charges on the value it adds. |
| 720 | 3 | `public double effectivePropertyRate(String sector)` | ANNUAL property tax rate for this sector. |
| 725 | 3 | `public double effectiveMonthlyPropertyRate(String sector)` | ...and the monthly one, which is what is actually billed. |
| 729 | 1 | `public double effectiveProfitRate(Sector s)` |  |
| 730 | 1 | `public double effectiveSalesRate(Sector s)` |  |
| 731 | 1 | `public double effectivePropertyRate(Sector s)` |  |
| 732 | 1 | `public double effectiveMonthlyPropertyRate(Sector s)` |  |
| 740 | 6 | `public double propertyTaxOn(double assessedValue)` | What one month's property tax comes to on a given assessed value. |
| 748 | 6 | `public double propertyTaxOn(double assessedValue, String sector)` | One month's property tax at this sector's own rate. |

### WAGES (lines 755-816)

| line | len | member | says |
|---:|---:|---|---|
| 770 | 5 | `public double wageTaxOn(double[] wagePerType, double[] fillRate)` | The month's wage tax, summed job type by job type at its band's rate. |
| 797 | 19 | `public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate)` | The same wage tax, split across the six pay tiers. |

### SAVE AND RESTORE (lines 817-1008)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 50 | `public double[] getPolicyState()` | The city rates and the wage-band offsets as one array, city rates first. |
| 898 | 40 | `public boolean restorePolicyState(double[] state)` | (STATE_BEFORE_EI) or longer than this build's (STATE_SLOTS); nothing is changed. |
| 940 | 4 | **type** `public static final class SectorOffsets` | One sector's three offsets, as the save carries them. |
| 945 | 16 | `public java.util.List<SectorOffsets> getSectorOffsets()` |  |
| 963 | 12 | `public void restoreSectorOffsets(java.util.List<SectorOffsets> saved)` | A sector the build does not have keeps its row - harmless, and it comes back if the sector does. |
| 976 | 20 | `public void reset()` |  |
| 997 | 6 | `private double clamp(double rate, double max)` |  |
| 1004 | 4 | `private double clampOffset(double points)` |  |


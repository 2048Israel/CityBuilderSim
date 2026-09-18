# TaxPolicy.java - 668 lines · 58 methods · 16 constants · model

`ham/citybuildersim/TaxPolicy.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Used by (18):** [AgricultureCheck](AgricultureCheck.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HouseholdCheck](HouseholdCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureManager](InfrastructureManager.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [SalesTaxLedger](SalesTaxLedger.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 80 | FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13) |
| 140 | THE PENSION PROMISE, AS TWO DIALS |
| 175 | EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS |
| 251 | WHAT A RIDE COSTS (2026-09-16) |
| 275 | A RIDE IS NOT A MONTH (2026-09-17) |
| 353 | THE CITY RATES |
| 380 | OFFSETS |
| 414 | EFFECTIVE RATES - the only numbers anything is ever charged at |
| 469 | WAGES |
| 531 | SAVE AND RESTORE |

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
| 199 | `TaxPolicy.MAX_EI_PREMIUM` | `.10` | A premium past a tenth of a wage is a second income tax. |
| 202 | `TaxPolicy.MAX_EI_BENEFIT` | `1.00` | EI that replaces more than the wage pays people to stay out of work. |
| 205 | `TaxPolicy.MAX_STUDENT_GRANT` | `1.00` | A grant of more than an unskilled wage is a wage. |
| 270 | `TaxPolicy.DEFAULT_TRANSIT_FARE` | `.0025` | What a single journey costs a rider, in thousands. |
| 273 | `TaxPolicy.MAX_TRANSIT_FARE` | `.05` | Past this nobody rides at all, as a multiple of the default. |
| 314 | `TaxPolicy.JOURNEYS_A_MONTH` | `40` | Journeys one commuter makes in a month: out and back, twenty days. |

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
| 196 | `private double studentGrantShare` |  |
| 236 | `private double pensionWageBase` | The wage a pension is a share of, carried in today's money. |
| 325 | `private double transitFare` |  |
| 338 | `private final double[] wageOffset` |  |
| 349 | `private final java.util.Map<String, Double> profitOffset` | THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector template). |
| 350 | `private final java.util.Map<String, Double> salesOffset` |  |
| 351 | `private final java.util.Map<String, Double> propertyOffset` |  |
| 606 | `public String sector` |  |
| 607 | `public double profit, sales, property` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 622 | **type** `public class TaxPolicy` | The city's tax rates - the revenue half of what the player actually decides. |

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

### EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS (lines 175-250)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 1 | `public double getEiPremiumRate()` |  |
| 208 | 1 | `public double getEiBenefitRate()` |  |
| 209 | 1 | `public double getStudentGrantShare()` |  |
| 211 | 1 | `public void setEiPremiumRate(double rate)` |  |
| 212 | 1 | `public void setEiBenefitRate(double share)` |  |
| 213 | 1 | `public void setStudentGrantShare(double share)` |  |
| 231 | 3 | `public double pensionPerSenior()` | A pension, in TODAY's money. |
| 238 | 12 | `public void redenominate(double scale)` |  |

### WHAT A RIDE COSTS (2026-09-16) (lines 251-274)

### A RIDE IS NOT A MONTH (2026-09-17) (lines 275-352)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 1 | `public double monthlyFare()` | What a month of riding costs one commuter, in thousands. |
| 327 | 1 | `public double getTransitFare()` |  |
| 329 | 3 | `public void setTransitFare(double fare)` |  |
| 334 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

### THE CITY RATES (lines 353-379)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 3 | `public double getIncomeTaxRate()` |  |
| 362 | 3 | `public double getPropertyTaxRate()` | The annual rate - what the player sets and what the screens show. |
| 367 | 3 | `public double getMonthlyPropertyTaxRate()` | The annual rate divided by twelve. |
| 371 | 3 | `public void setIncomeTaxRate(double rate)` |  |
| 376 | 3 | `public void setPropertyTaxRate(double annualRate)` | Takes the ANNUAL rate. |

### OFFSETS (lines 380-413)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 1 | `public double getWageOffset(WageBand band)` |  |
| 385 | 1 | `public double getProfitOffset(String sector)` |  |
| 386 | 1 | `public double getSalesOffset(String sector)` |  |
| 387 | 1 | `public double getPropertyOffset(String sector)` |  |
| 389 | 1 | `public double getProfitOffset(Sector s)` |  |
| 390 | 1 | `public double getSalesOffset(Sector s)` |  |
| 391 | 1 | `public double getPropertyOffset(Sector s)` |  |
| 393 | 3 | `public void setWageOffset(WageBand band, double points)` |  |
| 397 | 3 | `public void setProfitOffset(String sector, double points)` |  |
| 401 | 3 | `public void setSalesOffset(String sector, double points)` |  |
| 406 | 3 | `public void setPropertyOffset(String sector, double points)` | In ANNUAL points, matching the rate it offsets. |
| 410 | 1 | `public void setProfitOffset(Sector s, double points)` |  |
| 411 | 1 | `public void setSalesOffset(Sector s, double points)` |  |
| 412 | 1 | `public void setPropertyOffset(Sector s, double points)` |  |

### EFFECTIVE RATES - the only numbers anything is ever charged at (lines 414-468)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 3 | `public double effectiveWageRate(WageBand band)` | What wages in this band are taxed at. |
| 424 | 3 | `public double effectiveProfitRate(String sector)` | What this sector's profit is taxed at. |
| 429 | 3 | `public double effectiveSalesRate(String sector)` | What this sector charges on the value it adds. |
| 434 | 3 | `public double effectivePropertyRate(String sector)` | ANNUAL property tax rate for this sector. |
| 439 | 3 | `public double effectiveMonthlyPropertyRate(String sector)` | ...and the monthly one, which is what is actually billed. |
| 443 | 1 | `public double effectiveProfitRate(Sector s)` |  |
| 444 | 1 | `public double effectiveSalesRate(Sector s)` |  |
| 445 | 1 | `public double effectivePropertyRate(Sector s)` |  |
| 446 | 1 | `public double effectiveMonthlyPropertyRate(Sector s)` |  |
| 454 | 6 | `public double propertyTaxOn(double assessedValue)` | What one month's property tax comes to on a given assessed value. |
| 462 | 6 | `public double propertyTaxOn(double assessedValue, String sector)` | One month's property tax at this sector's own rate. |

### WAGES (lines 469-530)

| line | len | member | says |
|---:|---:|---|---|
| 484 | 5 | `public double wageTaxOn(double[] wagePerType, double[] fillRate)` | The month's wage tax, summed job type by job type at its band's rate. |
| 511 | 19 | `public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate)` | The same wage tax, split across the six pay tiers. |

### SAVE AND RESTORE (lines 531-668)

| line | len | member | says |
|---:|---:|---|---|
| 541 | 30 | `public double[] getPolicyState()` | The city rates and the wage-band offsets as one array, city rates first. |
| 573 | 30 | `public boolean restorePolicyState(double[] state)` |  |
| 605 | 4 | **type** `public static final class SectorOffsets` | One sector's three offsets, as the save carries them. |
| 610 | 16 | `public java.util.List<SectorOffsets> getSectorOffsets()` |  |
| 628 | 12 | `public void restoreSectorOffsets(java.util.List<SectorOffsets> saved)` | A sector the build does not have keeps its row - harmless, and it comes back if the sector does. |
| 641 | 15 | `public void reset()` |  |
| 657 | 6 | `private double clamp(double rate, double max)` |  |
| 664 | 4 | `private double clampOffset(double points)` |  |


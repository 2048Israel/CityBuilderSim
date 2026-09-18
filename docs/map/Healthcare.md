# Healthcare.java - 703 lines · 41 methods · 15 constants · model

`ham/citybuildersim/Healthcare.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The city's healthcare service: what it costs, what it collects, and what it
> does with the dead.
> 
> TWO CLASSES, AND THE DIFFERENCE MATTERS. {@link Health} is the EFFECT - how
> much of the workforce is off sick this month, which is a multiplier on output.
> This is the SERVICE - an employer with a payroll, a landlord with upkeep, a
> till that takes fees, and a cemetery with a finite number of plots. Health
> reads coverage; Healthcare pays for it.
> 
> WHY THIS EXISTS AT ALL
> 
> Because it did not, and the hole was the largest conservation break in the
> game. Every category of building with jobs is paid by some handler - shops by
> CommercialHandler, mills by IndustrialHandler, the grid and the water and the
> crews by ServicesManager. HEALTHCARE was paid by nobody, and it carries 2,128
> jobs across its fourteen templates, more than any other category. Its wages
> were counted in the city's wage bill, the city collected wage tax on them, the
> households received and spent them, and nothing debited the treasury. A
> General Hospital created $1.15M a month out of nothing; a Regional Medical
> Centre $3.25M.
> 
> `upkeep` had a matching hole of its own, and a wider one: getUpkeep() was read
> by a debug println and by BuildingDataCheck, and by nothing else, for ANY
> building in the game. It has always been a wish. Healthcare is the first thing
> that actually charges it, because healthcare is the first building category
> whose running cost is the whole point of it.
> 
> A NET DEFICIT BUSINESS, per Jerus - "patients still pay, just not much". The
> fees below recover roughly a fifth to a quarter of what the service costs, and
> they are charged on people SERVED rather than on capacity built, so an empty
> ward is paid for and collects nothing. Overbuilding is meant to hurt.

**Uses:** [CareType](CareType.md) (18), [AgeBand](AgeBand.md) (12), [PopulationCohorts](PopulationCohorts.md) (4)

**Used by (16):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SafetyType](SafetyType.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 38 | WHAT THE CITY WAS FOUNDED WITH |
| 93 | WHAT PATIENTS PAY |
| 120 | THE TWO WAYS TO BURY SOMEBODY |
| 218 | WHAT CARE DOES TO MORTALITY |
| 366 | · the month |
| 384 | · carried forward |
| 494 | · reading it |
| 546 | DEATH CARE AS A UTILITY |
| 641 | · saving |

## Constants

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

## Fields (state)

| line | field | says |
|---:|---|---|
| 116 | `private double generalFee` | The same three, in today's money - reformed with every other price. |
| 117 | `private double childcareFee` |  |
| 118 | `private double seniorFee` |  |
| 139 | `private double burialFee` | The same two, in today's money. |
| 140 | `private double cremationFee` |  |
| 368 | `private double payroll` |  |
| 369 | `private double upkeep` |  |
| 370 | `private double fees` |  |
| 371 | `private double treatmentFees` |  |
| 372 | `private double funeralFees` |  |
| 374 | `private double deaths` |  |
| 375 | `private double burials` |  |
| 376 | `private double cremations` |  |
| 381 | `private double plotsBuilt` | What the city had to work with, kept so the screen can say how tight it was. |
| 382 | `private double cremationCapacity` |  |
| 394 | `private double plotsUsed` | Plots used, ever. |
| 403 | `private double unburied` | The dead nobody could deal with, accumulated. |
| 497 | `private final double[] served` | People treated this month, by kind of care. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 668 | **type** `public class Healthcare` | The city's healthcare service: what it costs, what it collects, and what it does with the dead. |

### WHAT THE CITY WAS FOUNDED WITH (lines 38-92)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 17 | `public static double foundingCapacity(CareType care)` | Capacity the city has before it builds anything. |

### WHAT PATIENTS PAY (lines 93-119)

### THE TWO WAYS TO BURY SOMEBODY (lines 120-217)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 11 | `public static double feeFor(CareType care)` | The fee a care type charges, per head or per body. |
| 174 | 11 | `public double feeNow(CareType care)` | The same five fees, at what this city charges TODAY. |

### WHAT CARE DOES TO MORTALITY (lines 218-365)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 10 | `public static double mortalityFactor(AgeBand band, double childcareCoverage, double generalCoverage, double seniorCoverage)` | What this band's death rate is multiplied by, given the city's coverage. |
| 327 | 4 | `public static double mortalityFactor(AgeBand band, double childcareCoverage, double seniorCoverage)` | The two-argument form, for callers that only care about the extremes. |
| 333 | 4 | `private static double swing(double swing, double coverage)` | swing at no coverage, 1 at half, 1/swing at full. |
| 339 | 11 | `public static double[] mortalityFactors(double childcareCoverage, double generalCoverage, double seniorCoverage)` | The whole array, in band order, for PopulationCohorts.advanceMonth(). |
| 358 | 3 | `public static double birthFactor(double childcareCoverage)` | What the birth rate is multiplied by. |
| 362 | 3 | `private static double clamp(double v)` |  |

### the month (lines 366-383)

### carried forward (lines 384-493)

| line | len | member | says |
|---:|---:|---|---|
| 416 | 36 | `public void advanceMonth(double staffedPayroll, double upkeep, double[] served, double deaths, double burialShare, double plots...` | Settles the month. |
| 453 | 4 | `private static double feeOn(double[] served, CareType care, double fee)` |  |
| 468 | 25 | `private void settleDeaths(double burialShare, double plotsBuilt, double cremationCapacity)` | Who gets buried, who gets burned, and who gets neither. |

### reading it (lines 494-545)

| line | len | member | says |
|---:|---:|---|---|
| 500 | 3 | `public double getServed(CareType care)` | How many this kind of care actually saw this month. |
| 505 | 3 | `public double feesFrom(CareType care)` | ...and what they were charged for it. |
| 509 | 1 | `public double getPayroll()` |  |
| 510 | 1 | `public double getUpkeep()` |  |
| 513 | 1 | `public double getGrossCost()` | Everything the service costs the city before a penny comes back. |
| 515 | 1 | `public double getFees()` |  |
| 516 | 1 | `public double getTreatmentFees()` |  |
| 517 | 1 | `public double getFuneralFees()` |  |
| 526 | 1 | `public double getNetCost()` | The line that belongs on the city's expenditure list. |
| 529 | 4 | `public double getCostRecovery()` | Share of the gross cost the fees cover. |
| 534 | 1 | `public double getDeaths()` |  |
| 535 | 1 | `public double getBurials()` |  |
| 536 | 1 | `public double getCremations()` |  |
| 537 | 1 | `public double getPlotsUsed()` |  |
| 540 | 1 | `public double getUnburied()` | The dead the city has nowhere to put. |
| 542 | 3 | `public static double plotsRemaining(double plotsBuilt, double plotsUsed)` |  |

### DEATH CARE AS A UTILITY (lines 546-640)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 5 | `public double getDeathCareRatio()` | How much of this month's demand the city actually dealt with. |
| 572 | 3 | `public double getCremationUtilisation()` | Share of the crematoria's monthly throughput that was used. |
| 577 | 3 | `public double getPlotUtilisation()` | Share of every plot ever built that is now occupied. |
| 581 | 1 | `public double getPlotsBuilt()` |  |
| 582 | 1 | `public double getCremationCapacity()` |  |
| 601 | 1 | `public boolean isOverwhelmed()` | True when the city dealt with fewer people than died. |
| 610 | 5 | `public boolean isStrained()` | True while there is still room and not much of it. |
| 623 | 6 | `public String getStatus()` | One line for the panel, in the grid's own vocabulary. |
| 636 | 4 | `public double monthsOfPlotsLeft(double plotsBuilt)` | Months of burials the city's remaining plots will take at this rate. |

### saving (lines 641-703)

| line | len | member | says |
|---:|---:|---|---|
| 653 | 8 | `public double[] getState()` | The state, in order. |
| 663 | 18 | `public boolean restore(double[] state)` | Refused whole on a length mismatch, per the standing rule. |
| 683 | 9 | `public void redenominate(double scale)` | The healthcare fees and this month's bill, in the new unit. |
| 695 | 7 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |


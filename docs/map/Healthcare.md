# Healthcare.java - 963 lines · 55 methods · 18 constants · model

`ham/citybuildersim/Healthcare.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> A NET DEFICIT BUSINESS AT THE FOUNDING FEES, per Jerus - "patients still pay,
> just not much". The fees below recover roughly a fifth to a quarter of what
> the service costs, and they are charged on people SERVED rather than on
> capacity built, so an empty ward is paid for and collects nothing.
> Overbuilding is meant to hurt. Since 2026-09-19 the player can scale those
> three fees from nothing to fifteen times them, so the deficit is the DEFAULT
> rather than the design - see the banner WHAT PATIENTS PAY.

**Uses:** [CareType](CareType.md) (48), [AgeBand](AgeBand.md) (14), [PopulationCohorts](PopulationCohorts.md) (4), [TaxPolicy](TaxPolicy.md) (2)

**Used by (24):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyPreview](PolicyPreview.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SafetyType](SafetyType.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 41 | WHAT THE CITY WAS FOUNDED WITH |
| 109 | WHAT PATIENTS PAY |
| 165 | THE TWO WAYS TO BURY SOMEBODY |
| 280 | WHAT CARE DOES TO MORTALITY |
| 428 | · the month |
| 446 | · carried forward |
| 602 | · reading it |
| 758 | DEATH CARE AS A UTILITY |
| 853 | · saving |

## Constants

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

## Fields (state)

| line | field | says |
|---:|---|---|
| 146 | `private double generalFee` | The same three, in today's money - reformed with every other price, and struck at the expected price level since 0.7.42 (seedConstants()). |
| 147 | `private double childcareFee` |  |
| 148 | `private double seniorFee` |  |
| 155 | `private double careFeeScale` | The player's multiplier on the three care fees, handed in by Game every month from TaxPolicy and after a load - a policy, so it is saved there and not here. |
| 184 | `private double burialFee` | The same two, in today's money. |
| 185 | `private double cremationFee` |  |
| 430 | `private double payroll` |  |
| 431 | `private double upkeep` |  |
| 432 | `private double fees` |  |
| 433 | `private double treatmentFees` |  |
| 434 | `private double funeralFees` |  |
| 436 | `private double deaths` |  |
| 437 | `private double burials` |  |
| 438 | `private double cremations` |  |
| 443 | `private double plotsBuilt` | What the city had to work with, kept so the screen can say how tight it was. |
| 444 | `private double cremationCapacity` |  |
| 456 | `private double plotsUsed` | Plots used, ever. |
| 465 | `private double unburied` | The dead nobody could deal with, accumulated. |
| 605 | `private final double[] served` | People treated this month, by kind of care. |
| 608 | `private final double[] offered` | People the staffed beds could have treated this month, before the fee turned anybody away. |
| 611 | `private final double[] affordable` | Of the people each kind of care would serve, the share whose household could pay its fee. |
| 637 | `private final double[] coverage` | The coverage the month's population step READ for each kind of care, by CareType.ordinal(): the staffed beds over the people, clamped, less the share of those people the fee turned away - the figure the mortality swin... |
| 691 | `private double treatmentFeesAtOne` | What the three care fees would raise at 1x on everybody the beds could take, in today's money. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 925 | **type** `public class Healthcare` | The city's healthcare service: what it costs, what it collects, and what it does with the dead. |

### WHAT THE CITY WAS FOUNDED WITH (lines 41-108)

| line | len | member | says |
|---:|---:|---|---|
| 88 | 20 | `public static double foundingCapacity(CareType care)` | Capacity the city has before it builds anything. |

### WHAT PATIENTS PAY (lines 109-164)

| line | len | member | says |
|---:|---:|---|---|
| 158 | 3 | `public void setFeeScale(double scale)` | Sets the multiplier every care fee is charged at this month. |
| 163 | 1 | `public double getFeeScale()` | The multiplier the three care fees are charged at. |

### THE TWO WAYS TO BURY SOMEBODY (lines 165-279)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 11 | `public static double feeFor(CareType care)` | The fee a care type charges, per head or per body. |
| 220 | 11 | `public double feeNow(CareType care)` | The same five fees, at what this city charges TODAY. |
| 238 | 9 | `public double feeAtOne(CareType care)` | The same five, in today's money and at 1x: what a staged scale previews against, so the screen multiplies a fee the service owns rather than deriving one. |

### WHAT CARE DOES TO MORTALITY (lines 280-427)

| line | len | member | says |
|---:|---:|---|---|
| 377 | 10 | `public static double mortalityFactor(AgeBand band, double childcareCoverage, double generalCoverage, double seniorCoverage)` | What this band's death rate is multiplied by, given the city's coverage. |
| 389 | 4 | `public static double mortalityFactor(AgeBand band, double childcareCoverage, double seniorCoverage)` | The two-argument form, for callers that only care about the extremes. |
| 395 | 4 | `private static double swing(double swing, double coverage)` | swing at no coverage, 1 at half, 1/swing at full. |
| 401 | 11 | `public static double[] mortalityFactors(double childcareCoverage, double generalCoverage, double seniorCoverage)` | The whole array, in band order, for PopulationCohorts.advanceMonth(). |
| 420 | 3 | `public static double birthFactor(double childcareCoverage)` | What the birth rate is multiplied by. |
| 424 | 3 | `private static double clamp(double v)` |  |

### the month (lines 428-445)

### carried forward (lines 446-601)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 10 | `public void advanceMonth(double staffedPayroll, double upkeep, double[] served, double deaths, double burialShare, double plots...` | Settles the month. |
| 500 | 60 | `public void advanceMonth(double staffedPayroll, double upkeep, double[] offered, double[] affordable, double deaths, double bur...` | The same month, with the price at the door (2026-09-19). |
| 561 | 4 | `private static double feeOn(double[] served, CareType care, double fee)` |  |
| 576 | 25 | `private void settleDeaths(double burialShare, double plotsBuilt, double cremationCapacity)` | Who gets buried, who gets burned, and who gets neither. |

### reading it (lines 602-757)

| line | len | member | says |
|---:|---:|---|---|
| 612 | 1 | `{ ... }` |  |
| 619 | 3 | `public double getServed(CareType care)` | How many this kind of care actually TREATED this month: the people the beds could take, less the ones the fee turned away (2026-09-19). |
| 624 | 3 | `public double feesFrom(CareType care)` | ...and what they were charged for it - the people who paid, at today's scaled fee. |
| 638 | 1 | `{ ... }` |  |
| 641 | 5 | `public void noteCoverage(double childcare, double general, double senior)` | Game tells the service what coverage the month read, once it has struck it. |
| 653 | 3 | `public double getCoverage(CareType care)` | The coverage the month read for this kind of care - see noteCoverage(): what the beds could do for the people, less whoever the fee turned away. |
| 658 | 3 | `public double getOffered(CareType care)` | How many this kind of care could have treated this month, had everybody been able to pay. |
| 663 | 3 | `public double getAffordability(CareType care)` | Of the people this kind of care would serve, the share whose household could pay the fee. |
| 668 | 3 | `public double getPricedOut(CareType care)` | The people this kind of care had a bed for and turned away at the door for want of the fee. |
| 673 | 7 | `public double getPricedOutTotal()` | ...and all of them, across the three kinds of care. |
| 699 | 1 | `public double fullTreatmentFees()` | What the treatment fees would have come to had everybody the beds could take been able to pay: the bill the households are measured against when they decide whether they can afford care. |
| 702 | 1 | `public double treatmentFeesAtOne()` | The same bill at 1x, whatever the dial says: what a staged scale previews against. |
| 705 | 1 | `public double getFullFees()` | ...and with the funerals, which is the whole bill the households see at full service. |
| 717 | 3 | `public double breakEvenScale()` | The fee scale at which this city's fees would meet its gross cost, with everybody the beds could take paying: gross cost over what the three fees raise at 1x on the people offered care. |
| 721 | 1 | `public double getPayroll()` |  |
| 722 | 1 | `public double getUpkeep()` |  |
| 725 | 1 | `public double getGrossCost()` | Everything the service costs the city before a penny comes back. |
| 727 | 1 | `public double getFees()` |  |
| 728 | 1 | `public double getTreatmentFees()` |  |
| 729 | 1 | `public double getFuneralFees()` |  |
| 738 | 1 | `public double getNetCost()` | The line that belongs on the city's expenditure list. |
| 741 | 4 | `public double getCostRecovery()` | Share of the gross cost the fees cover. |
| 746 | 1 | `public double getDeaths()` |  |
| 747 | 1 | `public double getBurials()` |  |
| 748 | 1 | `public double getCremations()` |  |
| 749 | 1 | `public double getPlotsUsed()` |  |
| 752 | 1 | `public double getUnburied()` | The dead the city has nowhere to put. |
| 754 | 3 | `public static double plotsRemaining(double plotsBuilt, double plotsUsed)` |  |

### DEATH CARE AS A UTILITY (lines 758-852)

| line | len | member | says |
|---:|---:|---|---|
| 777 | 5 | `public double getDeathCareRatio()` | How much of this month's demand the city actually dealt with. |
| 784 | 3 | `public double getCremationUtilisation()` | Share of the crematoria's monthly throughput that was used. |
| 789 | 3 | `public double getPlotUtilisation()` | Share of every plot ever built that is now occupied. |
| 793 | 1 | `public double getPlotsBuilt()` |  |
| 794 | 1 | `public double getCremationCapacity()` |  |
| 813 | 1 | `public boolean isOverwhelmed()` | True when the city dealt with fewer people than died. |
| 822 | 5 | `public boolean isStrained()` | True while there is still room and not much of it. |
| 835 | 6 | `public String getStatus()` | One line for the panel, in the grid's own vocabulary. |
| 848 | 4 | `public double monthsOfPlotsLeft(double plotsBuilt)` | Months of burials the city's remaining plots will take at this rate. |

### saving (lines 853-963)

| line | len | member | says |
|---:|---:|---|---|
| 865 | 23 | `public double[] getState()` | The state, in order. |
| 906 | 34 | `public boolean restore(double[] state)` | Refused whole on a length mismatch, per the standing rule - the shapes from before the full-service bill and before the coverages (both 2026-09-19) and before the served counts (0.7.46) are the exceptions, and each re... |
| 942 | 10 | `public void redenominate(double scale)` | The healthcare fees and this month's bill, in the new unit. |
| 955 | 7 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |


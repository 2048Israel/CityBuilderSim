# Healthcare.java - 932 lines · 55 methods · 17 constants · model

`ham/citybuildersim/Healthcare.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [CareType](CareType.md) (40), [AgeBand](AgeBand.md) (12), [PopulationCohorts](PopulationCohorts.md) (4), [TaxPolicy](TaxPolicy.md) (2)

**Used by (16):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SafetyType](SafetyType.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 41 | WHAT THE CITY WAS FOUNDED WITH |
| 96 | WHAT PATIENTS PAY |
| 152 | THE TWO WAYS TO BURY SOMEBODY |
| 267 | WHAT CARE DOES TO MORTALITY |
| 415 | · the month |
| 433 | · carried forward |
| 588 | · reading it |
| 744 | DEATH CARE AS A UTILITY |
| 839 | · saving |

## Constants

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
| 815 | `Healthcare.STRAINED` | `.90` | Past this share of the ovens' throughput, say so. |
| 818 | `Healthcare.PLOT_WARNING_MONTHS` | `60` | Warn once the ground will not last this long at the current rate. |
| 870 | `Healthcare.STATE_BEFORE_FULL_BILL` | `12` | How many figures the state carried before the full-service bill was appended (2026-09-19). |
| 873 | `Healthcare.STATE_BEFORE_COVERAGE` | `13` | ...and before the three coverages were, the same day. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 133 | `private double generalFee` | The same three, in today's money - reformed with every other price. |
| 134 | `private double childcareFee` |  |
| 135 | `private double seniorFee` |  |
| 142 | `private double careFeeScale` | The player's multiplier on the three care fees, handed in by Game every month from TaxPolicy and after a load - a policy, so it is saved there and not here. |
| 171 | `private double burialFee` | The same two, in today's money. |
| 172 | `private double cremationFee` |  |
| 417 | `private double payroll` |  |
| 418 | `private double upkeep` |  |
| 419 | `private double fees` |  |
| 420 | `private double treatmentFees` |  |
| 421 | `private double funeralFees` |  |
| 423 | `private double deaths` |  |
| 424 | `private double burials` |  |
| 425 | `private double cremations` |  |
| 430 | `private double plotsBuilt` | What the city had to work with, kept so the screen can say how tight it was. |
| 431 | `private double cremationCapacity` |  |
| 443 | `private double plotsUsed` | Plots used, ever. |
| 452 | `private double unburied` | The dead nobody could deal with, accumulated. |
| 591 | `private final double[] served` | People treated this month, by kind of care. |
| 594 | `private final double[] offered` | People the staffed beds could have treated this month, before the fee turned anybody away. |
| 597 | `private final double[] affordable` | Of the people each kind of care would serve, the share whose household could pay its fee. |
| 623 | `private final double[] coverage` | The coverage the month's population step READ for each kind of care, by CareType.ordinal(): the staffed beds over the people, clamped, less the share of those people the fee turned away - the figure the mortality swin... |
| 677 | `private double treatmentFeesAtOne` | What the three care fees would raise at 1x on everybody the beds could take, in today's money. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 894 | **type** `public class Healthcare` | The city's healthcare service: what it costs, what it collects, and what it does with the dead. |

### WHAT THE CITY WAS FOUNDED WITH (lines 41-95)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 17 | `public static double foundingCapacity(CareType care)` | Capacity the city has before it builds anything. |

### WHAT PATIENTS PAY (lines 96-151)

| line | len | member | says |
|---:|---:|---|---|
| 145 | 3 | `public void setFeeScale(double scale)` | Sets the multiplier every care fee is charged at this month. |
| 150 | 1 | `public double getFeeScale()` | The multiplier the three care fees are charged at. |

### THE TWO WAYS TO BURY SOMEBODY (lines 152-266)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 11 | `public static double feeFor(CareType care)` | The fee a care type charges, per head or per body. |
| 207 | 11 | `public double feeNow(CareType care)` | The same five fees, at what this city charges TODAY. |
| 225 | 9 | `public double feeAtOne(CareType care)` | The same five, in today's money and at 1x: what a staged scale previews against, so the screen multiplies a fee the service owns rather than deriving one. |

### WHAT CARE DOES TO MORTALITY (lines 267-414)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 10 | `public static double mortalityFactor(AgeBand band, double childcareCoverage, double generalCoverage, double seniorCoverage)` | What this band's death rate is multiplied by, given the city's coverage. |
| 376 | 4 | `public static double mortalityFactor(AgeBand band, double childcareCoverage, double seniorCoverage)` | The two-argument form, for callers that only care about the extremes. |
| 382 | 4 | `private static double swing(double swing, double coverage)` | swing at no coverage, 1 at half, 1/swing at full. |
| 388 | 11 | `public static double[] mortalityFactors(double childcareCoverage, double generalCoverage, double seniorCoverage)` | The whole array, in band order, for PopulationCohorts.advanceMonth(). |
| 407 | 3 | `public static double birthFactor(double childcareCoverage)` | What the birth rate is multiplied by. |
| 411 | 3 | `private static double clamp(double v)` |  |

### the month (lines 415-432)

### carried forward (lines 433-587)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 10 | `public void advanceMonth(double staffedPayroll, double upkeep, double[] served, double deaths, double burialShare, double plots...` | Settles the month. |
| 487 | 59 | `public void advanceMonth(double staffedPayroll, double upkeep, double[] offered, double[] affordable, double deaths, double bur...` | The same month, with the price at the door (2026-09-19). |
| 547 | 4 | `private static double feeOn(double[] served, CareType care, double fee)` |  |
| 562 | 25 | `private void settleDeaths(double burialShare, double plotsBuilt, double cremationCapacity)` | Who gets buried, who gets burned, and who gets neither. |

### reading it (lines 588-743)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 1 | `{ ... }` |  |
| 605 | 3 | `public double getServed(CareType care)` | How many this kind of care actually TREATED this month: the people the beds could take, less the ones the fee turned away (2026-09-19). |
| 610 | 3 | `public double feesFrom(CareType care)` | ...and what they were charged for it - the people who paid, at today's scaled fee. |
| 624 | 1 | `{ ... }` |  |
| 627 | 5 | `public void noteCoverage(double childcare, double general, double senior)` | Game tells the service what coverage the month read, once it has struck it. |
| 639 | 3 | `public double getCoverage(CareType care)` | The coverage the month read for this kind of care - see noteCoverage(): what the beds could do for the people, less whoever the fee turned away. |
| 644 | 3 | `public double getOffered(CareType care)` | How many this kind of care could have treated this month, had everybody been able to pay. |
| 649 | 3 | `public double getAffordability(CareType care)` | Of the people this kind of care would serve, the share whose household could pay the fee. |
| 654 | 3 | `public double getPricedOut(CareType care)` | The people this kind of care had a bed for and turned away at the door for want of the fee. |
| 659 | 7 | `public double getPricedOutTotal()` | ...and all of them, across the three kinds of care. |
| 685 | 1 | `public double fullTreatmentFees()` | What the treatment fees would have come to had everybody the beds could take been able to pay: the bill the households are measured against when they decide whether they can afford care. |
| 688 | 1 | `public double treatmentFeesAtOne()` | The same bill at 1x, whatever the dial says: what a staged scale previews against. |
| 691 | 1 | `public double getFullFees()` | ...and with the funerals, which is the whole bill the households see at full service. |
| 703 | 3 | `public double breakEvenScale()` | The fee scale at which this city's fees would meet its gross cost, with everybody the beds could take paying: gross cost over what the three fees raise at 1x on the people offered care. |
| 707 | 1 | `public double getPayroll()` |  |
| 708 | 1 | `public double getUpkeep()` |  |
| 711 | 1 | `public double getGrossCost()` | Everything the service costs the city before a penny comes back. |
| 713 | 1 | `public double getFees()` |  |
| 714 | 1 | `public double getTreatmentFees()` |  |
| 715 | 1 | `public double getFuneralFees()` |  |
| 724 | 1 | `public double getNetCost()` | The line that belongs on the city's expenditure list. |
| 727 | 4 | `public double getCostRecovery()` | Share of the gross cost the fees cover. |
| 732 | 1 | `public double getDeaths()` |  |
| 733 | 1 | `public double getBurials()` |  |
| 734 | 1 | `public double getCremations()` |  |
| 735 | 1 | `public double getPlotsUsed()` |  |
| 738 | 1 | `public double getUnburied()` | The dead the city has nowhere to put. |
| 740 | 3 | `public static double plotsRemaining(double plotsBuilt, double plotsUsed)` |  |

### DEATH CARE AS A UTILITY (lines 744-838)

| line | len | member | says |
|---:|---:|---|---|
| 763 | 5 | `public double getDeathCareRatio()` | How much of this month's demand the city actually dealt with. |
| 770 | 3 | `public double getCremationUtilisation()` | Share of the crematoria's monthly throughput that was used. |
| 775 | 3 | `public double getPlotUtilisation()` | Share of every plot ever built that is now occupied. |
| 779 | 1 | `public double getPlotsBuilt()` |  |
| 780 | 1 | `public double getCremationCapacity()` |  |
| 799 | 1 | `public boolean isOverwhelmed()` | True when the city dealt with fewer people than died. |
| 808 | 5 | `public boolean isStrained()` | True while there is still room and not much of it. |
| 821 | 6 | `public String getStatus()` | One line for the panel, in the grid's own vocabulary. |
| 834 | 4 | `public double monthsOfPlotsLeft(double plotsBuilt)` | Months of burials the city's remaining plots will take at this rate. |

### saving (lines 839-932)

| line | len | member | says |
|---:|---:|---|---|
| 851 | 17 | `public double[] getState()` | The state, in order. |
| 882 | 27 | `public boolean restore(double[] state)` | Refused whole on a length mismatch, per the standing rule - the shapes from before the full-service bill and before the coverages (both 2026-09-19) are the exceptions, and each restores what it carries: that city's fu... |
| 911 | 10 | `public void redenominate(double scale)` | The healthcare fees and this month's bill, in the new unit. |
| 924 | 7 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |


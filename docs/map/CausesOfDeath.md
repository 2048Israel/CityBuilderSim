# CausesOfDeath.java - 606 lines · 30 methods · 25 constants · model

`ham/citybuildersim/CausesOfDeath.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The causes of death of 1900, and how history and the city take them away.
> 
> WHY. The game's deaths had no causes. A city founded in 1900 lost its
> adults at AgeBand's 0.225% a year, roughly a third of what Canada lost in
> 1901, and its babies and children died only by childcare's swing
> (Healthcare.CHILDCARE_SWING); typhoid, the summer diarrhoea, the childhood
> outbreaks, TB, mothers at home births, pneumonia and the 1918 influenza were
> nowhere. So the catalogue of discoveries (R2: chlorination, the toxoid,
> sulfa, streptomycin, penicillin) had nothing to act on. Seven causes now
> each carry a death rate by band, and history and the city move them. See
> docs/queue/R0b-notes.md and docs/studies/research-1900.md, 2.7.
> 
> NOBODY DIES HERE EITHER. Like Sickness, this class says how likely each band
> is to die of each cause this month and remembers the dead it is told were
> booked; PopulationCohorts does the dying, off the same opening balance as the
> band's own rate, illness and violence (a fourth competing risk).
> 
> THE CAUSES ADD, EXCEPT WHERE THE SWING ALREADY HOLDS THEM. For teenagers,
> adults, seniors and elders the rate in AgeBand is today's, so the causes of
> 1900 are on top of it. For babies and children, whose unserved rate
> childcare's swing already sets at 1900's level, the causes SPLIT the swing's
> excess deaths (the rate above today's): each takes its 1900 share, history
> and the city move its part, and the factor the band keeps (keptFactor) is
> what the causes do not name. At 1900 conditions a baby's total is unchanged.
> 
> HISTORY IS THE PATH, THE CITY ONLY RAISES IT (R0b call 3). Each cause follows
> Canada's observed rate on its dates, each step named by the catalogue row
> that will later be the city's find; water short, hunger and no general care
> lift a cause above that path and never below it. Every cause runs to zero by
> 1980 (est.), so a city past it dies as AgeBand says it does today.

**Uses:** [AgeBand](AgeBand.md) (13), [Health](Health.md) (7), [CityCalendar](CityCalendar.md) (4), [CareType](CareType.md) (1), [Healthcare](Healthcare.md) (1), [Sickness](Sickness.md) (1)

**Used by (8):** [CausesOfDeathCheck](CausesOfDeathCheck.md), [Game](Game.md), [Health](Health.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md)

## Sections

| line | section |
|---:|---|
| 57 | 1900's NUMBERS - all-ages rates, and who they fall on |
| 126 | SEASONS - each averages exactly 1 over a year |
| 142 | OUTBREAKS - Health's, the same ones |
| 176 | THE CITY'S CONDITIONS - est., each multiplies a cause above history's path |
| 203 | HISTORY - Canada's observed rates, as a factor of the 1900 rate |
| 296 | RATES AND SPLITS - what a cause was in 1900, band by band |
| 394 | · state |
| 427 | · the month |
| 536 | · reading it |
| 562 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 39 | `CausesOfDeath.Cause.TYPHOID` |  |
| 40 | `CausesOfDeath.Cause.DIARRHOEA` |  |
| 41 | `CausesOfDeath.Cause.OUTBREAK` |  |
| 42 | `CausesOfDeath.Cause.TB` |  |
| 43 | `CausesOfDeath.Cause.PNEUMONIA` |  |
| 44 | `CausesOfDeath.Cause.CHILDBIRTH` |  |
| 45 | `CausesOfDeath.Cause.INFLUENZA` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `CausesOfDeath.CAUSES` | `Cause.values()` |  |
| 55 | `CausesOfDeath.BANDS` | `AgeBand.values()` |  |
| 65 | `CausesOfDeath.PEOPLE_1901` | `{.143,.154,.102,.566,.032,.003 }` | Canada's 1901 people by band, in band order (est., from the census's five-year groups); it turns an all-ages rate into a band's. |
| 75 | `CausesOfDeath.RATE_1900` | `{ 40.8, 155, 82, 180, 202, 0, 0 }` | The 1900 death rate of each cause, per 100,000 people a year, all ages. |
| 82 | `CausesOfDeath.DEATH_SHARE` | `{ {.03,.12,.12,.68,.05, 0 }, {.85,.02, 0,.05,.06,.02 }, {.65,.32,.03, 0, 0, 0...` | The share of each cause's deaths that falls on each band, in band order (est.; each row sums to 1): who died of it in 1900, judged from each disease's age pattern. |
| 96 | `CausesOfDeath.MOTHERS_1900` | `4.7` | Mothers dying in childbirth per 1,000 live births in 1900: Canada 1921's 4.7, the first figure there is, held back to 1900 [SC-B51]. |
| 99 | `CausesOfDeath.INFLUENZA_DEATHS` | `.006` | The 1918 influenza killed .6% of the people: about 50,000 Canadians [FLU-W]. |
| 102 | `CausesOfDeath.INFLUENZA_SICK` | `.25` | A quarter of the people were ill in the 1918 influenza (est.). |
| 105 | `CausesOfDeath.INFLUENZA_FIRST` | `monthOf(1918, 10)` | The first month of the influenza: October 1918. |
| 108 | `CausesOfDeath.INFLUENZA_LAST` | `monthOf(1920, 3)` | The last month of the influenza: March 1920, its third wave. |
| 117 | `CausesOfDeath.FLU_MONTHS` | `{ monthOf(1918, 10), monthOf(1918, 11), monthOf(1918, 12), monthOf(1919, 1), ...` | The three waves: October-December 1918 (.35, .35, .10 of the dead and the sick), January-April 1919 (.03 each), January-March 1920 (.08 between them). |
| 122 | `CausesOfDeath.FLU_SHARES` | `{.35,.35,.10,.03,.03,.03,.03,.08 / 3,.08 / 3,.08 / 3 }` |  |
| 131 | `CausesOfDeath.DIARRHOEA_SUMMER` | `2.5` | Summer diarrhoea in July, August and September is two and a half times a mean month's. |
| 134 | `CausesOfDeath.DIARRHOEA_REST` | `(12 - 3 * DIARRHOEA_SUMMER) / 9` | ...and in the other nine months, what makes the year average 1. |
| 137 | `CausesOfDeath.PNEUMONIA_WINTER` | `1.6` | Pneumonia in December, January, February and March is one and six-tenths a mean month's. |
| 140 | `CausesOfDeath.PNEUMONIA_REST` | `(12 - 4 * PNEUMONIA_WINTER) / 8` | ...and in the other eight months, what makes the year average 1. |
| 157 | `CausesOfDeath.OUTBREAK_MEAN_SEVERITY` | `meanOutbreakSeverity()` | The long-run mean of Health's unmitigated outbreakSeverity, so that the childhood outbreaks' rate, which is 1900's on average, rises and falls with the outbreaks the city actually has. |
| 181 | `CausesOfDeath.TB_HUNGER` | `2.0` | TB is 1 + 2 x the hungry share higher: hunger is what lets it take hold (est.). |
| 184 | `CausesOfDeath.DIARRHOEA_HUNGER` | `1.0` | Summer diarrhoea is 1 + 1 x the hungry share higher (est.). |
| 191 | `CausesOfDeath.UNCARED` | `.5` | TB, pneumonia and childbirth are 1 + .5 x the share with no general care higher (est.), for teenagers and over: babies' and children's care is childcare's, "one care type per band" (Healthcare). |
| 194 | `CausesOfDeath.CONDITION_CAP` | `3.0` | All the multipliers on one cause together never come to more than this (est.). |
| 201 | `CausesOfDeath.MAX_SPLIT` | `.9` | The splitting bands' causes never name more than this share of the swing's excess (est.): the children's shares sum to about 1.05 at the game's own rates, and a cause cannot take more than the swing holds. |
| 209 | `CausesOfDeath.KNOTS` | `{ { { 1900, 1 }, { 1910, 1 }, { 1915, 1 / 40.8 }, { 1950,.5 / 40.8 }, { 1980,...` | Each cause's knots: {decimal year, factor of the 1900 rate}. |
| 321 | `CausesOfDeath.SPLIT` | `new double [ CAUSES.length ] [ BANDS.length ]` |  |
| 396 | `CausesOfDeath.MONTHS` | `12` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `private final String label` |  |
| 399 | `private final double[] lastDeaths` | The dead of each cause booked last month. |
| 402 | `private final double[] totals` | The dead of each cause since the city began. |
| 405 | `private final double[][] ring` | The last twelve months' dead of each cause, one slot to a month, at month % 12. |
| 408 | `private double lastTbSick` | The people sick of TB for a year or more: the long sick of TB, from the month's rates. |
| 421 | `private boolean heldOff` | Held off: no cause kills, and the babies' and children's swing stays whole - the deaths of today, as before R0b. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 572 | **type** `public class CausesOfDeath` | The causes of death of 1900, and how history and the city take them away. |
| 38 | 15 | **type** `public enum Cause` | The seven causes, in the order every array here is indexed. |
| 49 | 1 | `Cause(String label)` _(in CausesOfDeath.Cause)_ |  |
| 51 | 1 | `public String label()` _(in CausesOfDeath.Cause)_ |  |

### 1900's NUMBERS - all-ages rates, and who they fall on (lines 57-125)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 3 | `private static int monthOf(int year, int monthOfYear)` | the game month of a calendar month: game month 1 is January 1900 |

### SEASONS - each averages exactly 1 over a year (lines 126-141)

### OUTBREAKS - Health's, the same ones (lines 142-175)

| line | len | member | says |
|---:|---:|---|---|
| 159 | 16 | `private static double meanOutbreakSeverity()` |  |

### THE CITY'S CONDITIONS - est., each multiplies a cause above history's path (lines 176-202)

### HISTORY - Canada's observed rates, as a factor of the 1900 rate (lines 203-295)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 15 | `public static double history(Cause cause, int month)` | A cause's factor of its 1900 rate in a game month: the knots joined in a straight line over the decimal year, 1 before the first, and 0 from 1980 on. |
| 278 | 4 | `public static double influenzaShare(int month)` | The influenza's share of its dead and its sick that fell in this game month: 0 outside its ten. |
| 284 | 3 | `public static double influenzaSick(int month)` | The share of the people sick of the influenza this month: slice 2 puts it on Health's rate. |
| 289 | 6 | `public static double season(Cause cause, int month)` | A cause's season in a game month: its mean is 1 over a year, and it is 1 for every cause without one. |

### RATES AND SPLITS - what a cause was in 1900, band by band (lines 296-393)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 5 | `public static double rate1900(Cause cause, AgeBand band)` | A cause's annual chance of killing a person of a band at 1900's rate: the all-ages rate times the band's share of the dead, over the band's share of the people. |
| 312 | 3 | `public static boolean isSwung(AgeBand band)` | True for the bands childcare's swing sets (babies and children): the causes split their excess. |
| 317 | 3 | `private static boolean splits(Cause cause)` | the causes that split the swing's excess: childbirth and the influenza never do |
| 323 | 13 | `static { ... }` |  |
| 344 | 4 | `public static double share(Cause cause, AgeBand band)` | The share of a swung band's swing excess (its mortality above today's) that this cause takes at 1900: the cause's 1900 rate over the excess at the top of the swing, the band's shares scaled down together so they sum t... |
| 350 | 5 | `public static double split(AgeBand band)` | The share of a swung band's excess the causes name together; 0 for any other band. |
| 361 | 4 | `public static double keptFactor(AgeBand band, double f)` | The mortality factor the band keeps once the causes have taken their part of the swing: f less the causes' share of the excess above 1. |
| 373 | 9 | `public static double conditions(Cause cause, AgeBand band, double hungry, double generalCoverage)` | What the city's conditions make of a cause: hunger for TB and summer diarrhoea, and no general care for TB, pneumonia and childbirth in the bands whose care is general care (not the swung ones); the product never more... |
| 384 | 9 | `public static double[] bandTotals(double[][] rates)` | Each band's chance of death, the sum over causes of a [cause][band] array. |

### state (lines 394-426)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 1 | `public void holdOff()` |  |
| 425 | 1 | `public boolean isHeldOff()` |  |

### the month (lines 427-535)

| line | len | member | says |
|---:|---:|---|---|
| 450 | 55 | `public double[][] rates(int month, double[] people, double[] mortalityFactor, double pipedWater, double hungry, double generalC...` | Each cause's MONTHLY chance of death in each band this month, [cause][band]: a rate to hand PopulationCohorts as a competing risk. |
| 513 | 22 | `public void book(int month, double[][] rates, double[] causeDeathsByBand)` | Books the month's dead by cause. |

### reading it (lines 536-561)

| line | len | member | says |
|---:|---:|---|---|
| 539 | 1 | `public double getLastDeaths(Cause cause)` | The dead of one cause booked last month. |
| 542 | 5 | `public double getLastDeaths()` | The dead of every cause booked last month. |
| 549 | 6 | `public double getYearDeaths(Cause cause)` | The dead of one cause in the last twelve months. |
| 557 | 1 | `public double getTotalDeaths(Cause cause)` | The dead of one cause since the city began. |
| 560 | 1 | `public double getTbSick()` | The people sick of TB for a year or more, as of the last rates() call. |

### saving (lines 562-606)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 10 | `public double[] getState()` | The state, in order: last month's dead, the totals, the ring (cause by cause), the TB sick. |
| 581 | 10 | `public boolean restore(double[] saved)` | Puts a saved state back. |
| 592 | 6 | `public void reset()` |  |
| 599 | 3 | `private static double clean(double v)` |  |
| 603 | 3 | `private static double clamp01(double v)` |  |


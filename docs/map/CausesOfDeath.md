# CausesOfDeath.java - 728 lines · 32 methods · 42 constants · model

`ham/citybuildersim/CausesOfDeath.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (13), [Health](Health.md) (7), [CityCalendar](CityCalendar.md) (6), [MedicalRows](MedicalRows.md) (2), [CareType](CareType.md) (1), [Healthcare](Healthcare.md) (1), [Sickness](Sickness.md) (1)

**Used by (10):** [CausesOfDeathCheck](CausesOfDeathCheck.md), [Game](Game.md), [Health](Health.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MedicineCheck](MedicineCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md)

## Sections

| line | section |
|---:|---|
| 59 | 1900's NUMBERS - all-ages rates, and who they fall on |
| 128 | SEASONS - each averages exactly 1 over a year |
| 144 | OUTBREAKS - Health's, the same ones |
| 178 | THE CITY'S CONDITIONS - est., each multiplies a cause above history's path |
| 205 | HISTORY - Canada's observed rates, as a factor of the 1900 rate |
| 298 | THE MEDICAL ROWS' STEPS (R2f) - history's, brought forward by a find |
| 404 | RATES AND SPLITS - what a cause was in 1900, band by band |
| 502 | · state |
| 535 | · the month |
| 658 | · reading it |
| 684 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 41 | `CausesOfDeath.Cause.TYPHOID` |  |
| 42 | `CausesOfDeath.Cause.DIARRHOEA` |  |
| 43 | `CausesOfDeath.Cause.OUTBREAK` |  |
| 44 | `CausesOfDeath.Cause.TB` |  |
| 45 | `CausesOfDeath.Cause.PNEUMONIA` |  |
| 46 | `CausesOfDeath.Cause.CHILDBIRTH` |  |
| 47 | `CausesOfDeath.Cause.INFLUENZA` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `CausesOfDeath.CAUSES` | `Cause.values()` |  |
| 57 | `CausesOfDeath.BANDS` | `AgeBand.values()` |  |
| 67 | `CausesOfDeath.PEOPLE_1901` | `{.143,.154,.102,.566,.032,.003 }` | Canada's 1901 people by band, in band order (est., from the census's five-year groups); it turns an all-ages rate into a band's. |
| 77 | `CausesOfDeath.RATE_1900` | `{ 40.8, 155, 82, 180, 202, 0, 0 }` | The 1900 death rate of each cause, per 100,000 people a year, all ages. |
| 84 | `CausesOfDeath.DEATH_SHARE` | `{ {.03,.12,.12,.68,.05, 0 }, {.85,.02, 0,.05,.06,.02 }, {.65,.32,.03, 0, 0, 0...` | The share of each cause's deaths that falls on each band, in band order (est.; each row sums to 1): who died of it in 1900, judged from each disease's age pattern. |
| 98 | `CausesOfDeath.MOTHERS_1900` | `4.7` | Mothers dying in childbirth per 1,000 live births in 1900: Canada 1921's 4.7, the first figure there is, held back to 1900 [SC-B51]. |
| 101 | `CausesOfDeath.INFLUENZA_DEATHS` | `.006` | The 1918 influenza killed .6% of the people: about 50,000 Canadians [FLU-W]. |
| 104 | `CausesOfDeath.INFLUENZA_SICK` | `.25` | A quarter of the people were ill in the 1918 influenza (est.). |
| 107 | `CausesOfDeath.INFLUENZA_FIRST` | `monthOf(1918, 10)` | The first month of the influenza: October 1918. |
| 110 | `CausesOfDeath.INFLUENZA_LAST` | `monthOf(1920, 3)` | The last month of the influenza: March 1920, its third wave. |
| 119 | `CausesOfDeath.FLU_MONTHS` | `{ monthOf(1918, 10), monthOf(1918, 11), monthOf(1918, 12), monthOf(1919, 1), ...` | The three waves: October-December 1918 (.35, .35, .10 of the dead and the sick), January-April 1919 (.03 each), January-March 1920 (.08 between them). |
| 124 | `CausesOfDeath.FLU_SHARES` | `{.35,.35,.10,.03,.03,.03,.03,.08 / 3,.08 / 3,.08 / 3 }` |  |
| 133 | `CausesOfDeath.DIARRHOEA_SUMMER` | `2.5` | Summer diarrhoea in July, August and September is two and a half times a mean month's. |
| 136 | `CausesOfDeath.DIARRHOEA_REST` | `(12 - 3 * DIARRHOEA_SUMMER) / 9` | ...and in the other nine months, what makes the year average 1. |
| 139 | `CausesOfDeath.PNEUMONIA_WINTER` | `1.6` | Pneumonia in December, January, February and March is one and six-tenths a mean month's. |
| 142 | `CausesOfDeath.PNEUMONIA_REST` | `(12 - 4 * PNEUMONIA_WINTER) / 8` | ...and in the other eight months, what makes the year average 1. |
| 159 | `CausesOfDeath.OUTBREAK_MEAN_SEVERITY` | `meanOutbreakSeverity()` | The long-run mean of Health's unmitigated outbreakSeverity, so that the childhood outbreaks' rate, which is 1900's on average, rises and falls with the outbreaks the city actually has. |
| 183 | `CausesOfDeath.TB_HUNGER` | `2.0` | TB is 1 + 2 x the hungry share higher: hunger is what lets it take hold (est.). |
| 186 | `CausesOfDeath.DIARRHOEA_HUNGER` | `1.0` | Summer diarrhoea is 1 + 1 x the hungry share higher (est.). |
| 193 | `CausesOfDeath.UNCARED` | `.5` | TB, pneumonia and childbirth are 1 + .5 x the share with no general care higher (est.), for teenagers and over: babies' and children's care is childcare's, "one care type per band" (Healthcare). |
| 196 | `CausesOfDeath.CONDITION_CAP` | `3.0` | All the multipliers on one cause together never come to more than this (est.). |
| 203 | `CausesOfDeath.MAX_SPLIT` | `.9` | The splitting bands' causes never name more than this share of the swing's excess (est.): the children's shares sum to about 1.05 at the game's own rates, and a cause cannot take more than the swing holds. |
| 211 | `CausesOfDeath.KNOTS` | `{ { { 1900, 1 }, { 1910, 1 }, { 1915, 1 / 40.8 }, { 1950,.5 / 40.8 }, { 1980,...` | Each cause's knots: {decimal year, factor of the 1900 rate}. |
| 317 | `CausesOfDeath.MILK_TB` | `.85` | Pasteurised milk (PM1) leaves TB at this factor: about 15% of TB cases came from dairy, c.1900 [MOH-MILK]. |
| 320 | `CausesOfDeath.MILK_FROM` | `1914 + 5 / 12.0` | The milk's step starts in history at Toronto's by-law, June 1914 [TOR-MILK]... |
| 323 | `CausesOfDeath.MILK_YEARS` | `1` | ...and takes this many years, a city's milk being pasteurised within a year of its by-law (est.). |
| 331 | `CausesOfDeath.TOXOID_OUTBREAK` | `1 -.9 * 40.3 / RATE_1900 [ Cause.OUTBREAK.ordinal() ]` | The diphtheria toxoid (PM3) leaves the childhood outbreaks at this factor: diphtheria killed 40.3 per 100,000 in the US in 1900 [MMWR99], of the outbreaks' RATE_1900, and three doses cut its cases at least 90% (Toront... |
| 334 | `CausesOfDeath.TOXOID_FROM` | `1925 + 8 / 12.0` | The toxoid's step starts in history with Connaught's trials, September 1925 [CON-D]... |
| 337 | `CausesOfDeath.TOXOID_YEARS` | `1931 - TOXOID_FROM` | ...and ends with Hamilton's 5 cases and no deaths in 1931 [CON-D]. |
| 340 | `CausesOfDeath.SULFA_PNEUMONIA` | `.75` | Sulfa (PM4) leaves pneumonia at this factor: deaths -17% to -32%, US 1937-43, -25% taken [JLS]; the PNEUMONIA knots' 1937 to 1940. |
| 343 | `CausesOfDeath.SULFA_CHILDBIRTH` | `.7` | Sulfa leaves mothers' deaths at this factor: -24% to -36%, -30% taken [JLS]; the CHILDBIRTH knots' 1937 to 1940. |
| 346 | `CausesOfDeath.SULFA_FROM` | `1937` | Sulfa's step starts in history in 1937, when Canada had it [SULF]... |
| 349 | `CausesOfDeath.SULFA_YEARS` | `3` | ...and takes the knots' three years. |
| 352 | `CausesOfDeath.PENICILLIN_CHILDBIRTH` | `2.8 /(5.8 *.7)` | Penicillin (PM5) leaves mothers' deaths at this factor: 2.8 per 1,000 births in 1944 against sulfa's 1940 level [SC-B51]; the CHILDBIRTH knots' 1940 to 1944. |
| 355 | `CausesOfDeath.PENICILLIN_FROM` | `1940` | Penicillin's step starts in history in 1940, the knot before 1944's... |
| 358 | `CausesOfDeath.PENICILLIN_YEARS` | `4` | ...and takes the knots' four years. |
| 361 | `CausesOfDeath.STREPTOMYCIN_TB` | `.26` | Streptomycin (PM6) leaves TB at this factor: deaths within six months 27% to 7% (MRC trial, 1946-48), .26 taken [STREP]; the TB knots' 1948 to 1952. |
| 364 | `CausesOfDeath.STREPTOMYCIN_FROM` | `1948` | Streptomycin's step starts in history in 1948, when Canada had it [R4]... |
| 367 | `CausesOfDeath.STREPTOMYCIN_YEARS` | `4` | ...and takes the knots' four years. |
| 373 | `CausesOfDeath.STEPS` | `List.of(new Step("PM1", Cause.TB, MILK_TB, MILK_FROM, MILK_YEARS), new Step("...` | Every step, in the catalogue's order. |
| 429 | `CausesOfDeath.SPLIT` | `new double [ CAUSES.length ] [ BANDS.length ]` |  |
| 504 | `CausesOfDeath.MONTHS` | `12` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `private final String label` |  |
| 507 | `private final double[] lastDeaths` | The dead of each cause booked last month. |
| 510 | `private final double[] totals` | The dead of each cause since the city began. |
| 513 | `private final double[][] ring` | The last twelve months' dead of each cause, one slot to a month, at month % 12. |
| 516 | `private double lastTbSick` | The people sick of TB for a year or more: the long sick of TB, from the month's rates. |
| 529 | `private boolean heldOff` | Held off: no cause kills, and the babies' and children's swing stays whole - the deaths of today, as before R0b. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 692 | **type** `public class CausesOfDeath` | The causes of death of 1900, and how history and the city take them away. |
| 40 | 15 | **type** `public enum Cause` | The seven causes, in the order every array here is indexed. |
| 51 | 1 | `Cause(String label)` _(in CausesOfDeath.Cause)_ |  |
| 53 | 1 | `public String label()` _(in CausesOfDeath.Cause)_ |  |

### 1900's NUMBERS - all-ages rates, and who they fall on (lines 59-127)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 3 | `private static int monthOf(int year, int monthOfYear)` | the game month of a calendar month: game month 1 is January 1900 |

### SEASONS - each averages exactly 1 over a year (lines 128-143)

### OUTBREAKS - Health's, the same ones (lines 144-177)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 16 | `private static double meanOutbreakSeverity()` |  |

### THE CITY'S CONDITIONS - est., each multiplies a cause above history's path (lines 178-204)

### HISTORY - Canada's observed rates, as a factor of the 1900 rate (lines 205-297)

| line | len | member | says |
|---:|---:|---|---|
| 263 | 15 | `public static double history(Cause cause, int month)` | A cause's factor of its 1900 rate in a game month: the knots joined in a straight line over the decimal year, 1 before the first, and 0 from 1980 on. |
| 280 | 4 | `public static double influenzaShare(int month)` | The influenza's share of its dead and its sick that fell in this game month: 0 outside its ten. |
| 286 | 3 | `public static double influenzaSick(int month)` | The share of the people sick of the influenza this month: slice 2 puts it on Health's rate. |
| 291 | 6 | `public static double season(Cause cause, int month)` | A cause's season in a game month: its mean is 1 over a year, and it is 1 for every cause without one. |

### THE MEDICAL ROWS' STEPS (R2f) - history's, brought forward by a find (lines 298-403)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 1 | **type** `public record Step(String code, Cause cause, double factor, double from, double years)` | One row's step on one cause: the row's code, the cause, the factor it leaves, and history's ramp (its start, a decimal year, and its length in years). |
| 389 | 14 | `public static double ahead(Cause cause, int month, MedicalRows rows)` | What a city's finds ahead of the world make of a cause's history in a game month: the product over the cause's steps of (1 - (1 - factor) x q) over (1 - (1 - factor) x p), p history's ramp and q the city's, which is h... |

### RATES AND SPLITS - what a cause was in 1900, band by band (lines 404-501)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 5 | `public static double rate1900(Cause cause, AgeBand band)` | A cause's annual chance of killing a person of a band at 1900's rate: the all-ages rate times the band's share of the dead, over the band's share of the people. |
| 420 | 3 | `public static boolean isSwung(AgeBand band)` | True for the bands childcare's swing sets (babies and children): the causes split their excess. |
| 425 | 3 | `private static boolean splits(Cause cause)` | the causes that split the swing's excess: childbirth and the influenza never do |
| 431 | 13 | `static { ... }` |  |
| 452 | 4 | `public static double share(Cause cause, AgeBand band)` | The share of a swung band's swing excess (its mortality above today's) that this cause takes at 1900: the cause's 1900 rate over the excess at the top of the swing, the band's shares scaled down together so they sum t... |
| 458 | 5 | `public static double split(AgeBand band)` | The share of a swung band's excess the causes name together; 0 for any other band. |
| 469 | 4 | `public static double keptFactor(AgeBand band, double f)` | The mortality factor the band keeps once the causes have taken their part of the swing: f less the causes' share of the excess above 1. |
| 481 | 9 | `public static double conditions(Cause cause, AgeBand band, double hungry, double generalCoverage)` | What the city's conditions make of a cause: hunger for TB and summer diarrhoea, and no general care for TB, pneumonia and childbirth in the bands whose care is general care (not the swung ones); the product never more... |
| 492 | 9 | `public static double[] bandTotals(double[][] rates)` | Each band's chance of death, the sum over causes of a [cause][band] array. |

### state (lines 502-534)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 1 | `public void holdOff()` |  |
| 533 | 1 | `public boolean isHeldOff()` |  |

### the month (lines 535-657)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 6 | `public double[][] rates(int month, double[] people, double[] mortalityFactor, double pipedWater, double hungry, double generalC...` | Each cause's MONTHLY chance of death in each band this month, [cause][band]: a rate to hand PopulationCohorts as a competing risk. |
| 570 | 57 | `public double[][] rates(int month, double[] people, double[] mortalityFactor, double pipedWater, double hungry, double generalC...` | The same, with the medical rows the city found ahead of the world (R2f): each cause's history times ahead(), which is 1 exactly, and not applied, where the city found none of its rows ahead. |
| 635 | 22 | `public void book(int month, double[][] rates, double[] causeDeathsByBand)` | Books the month's dead by cause. |

### reading it (lines 658-683)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 1 | `public double getLastDeaths(Cause cause)` | The dead of one cause booked last month. |
| 664 | 5 | `public double getLastDeaths()` | The dead of every cause booked last month. |
| 671 | 6 | `public double getYearDeaths(Cause cause)` | The dead of one cause in the last twelve months. |
| 679 | 1 | `public double getTotalDeaths(Cause cause)` | The dead of one cause since the city began. |
| 682 | 1 | `public double getTbSick()` | The people sick of TB for a year or more, as of the last rates() call. |

### saving (lines 684-728)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 10 | `public double[] getState()` | The state, in order: last month's dead, the totals, the ring (cause by cause), the TB sick. |
| 703 | 10 | `public boolean restore(double[] saved)` | Puts a saved state back. |
| 714 | 6 | `public void reset()` |  |
| 721 | 3 | `private static double clean(double v)` |  |
| 725 | 3 | `private static double clamp01(double v)` |  |


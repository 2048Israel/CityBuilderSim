# CausesOfDeathCheck.java - 914 lines · 25 methods · 7 constants · harnesses

`ham/citybuildersim/CausesOfDeathCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> Proves the causes of death of 1900 (R0b, slice 1): the model on its own,
> before the month hands its rates to the cohorts. Every claim below sets its
> own cause and is asserted against the model's constants and methods.
> 
>   1. the constants hold together (shares, seasons, the influenza's months, history's ends)
>   2. a 1900 city dies of each cause at its 1900 rate
>   3. the split leaves a 1900 baby's death rate where it was
>   4. seasons and outbreaks (and Health's own mean severity, measured)
>   5. history on its dates
>   6. the city's conditions raise a cause, never lower it
>   7. the 1918 influenza
>   8. booking the dead, the TB sick and the save
>   9. a real city's month books the causes (slice 2: the cohorts' fourth competing risk)
>  10. the 1918 influenza in a real city: its dead, its sick on Health's rate, its notice
>      (a) a reload mid-influenza, played on: January to March 1919 beside the unbroken city, to the bit; on the playtest's founding,
>          for the section's stocked city drifts after any reload (queued as SR1)
>      (b) the fixtures' hold kept across a load: a held twin loaded from September 1918 stays held to March 1920
> 
> The 1900 city of sections 2, 4, 5 and 6 is Canada's 1901 pyramid at 100,000
> people with no childcare (the swing at its top), general care for everybody,
> piped water, nobody hungry, and the mean outbreak running. Sections 9 and 10
> play one real city, founded in 1900, a month at a time.

**Uses:** [CausesOfDeath](CausesOfDeath.md) (121), [AgeBand](AgeBand.md) (42), [Game](Game.md) (18), [Health](Health.md) (6), [MoneyAudit](MoneyAudit.md) (3), [Healthcare](Healthcare.md) (2), [GameFiles](GameFiles.md) (2), [Sickness](Sickness.md) (2), [DeterminismCheck](DeterminismCheck.md) (2), [CityCalendar](CityCalendar.md) (1), [BuildingManager](BuildingManager.md) (1), [Founding](Founding.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Notice](Notice.md) (1), [LongPlaytest](LongPlaytest.md) (1)

## Sections

| line | section |
|---:|---|
| 185 | · 1. the constants hold together |
| 231 | · 2. a 1900 city dies of each cause at its 1900 rate |
| 250 | · 3. the split leaves a 1900 baby's rate where it was |
| 311 | · 4. seasons and outbreaks |
| 360 | · 5. history on its dates |
| 395 | · 6. the city's conditions raise a cause, never lower it |
| 449 | · 7. the 1918 influenza |
| 481 | · 8. booking the dead, the TB sick and the save |
| 618 | 9. A REAL CITY'S MONTH BOOKS THE CAUSES |
| 674 | 10. THE 1918 INFLUENZA IN A REAL CITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `CausesOfDeathCheck.CAUSES` | `Cause.values()` |  |
| 54 | `CausesOfDeathCheck.BANDS` | `AgeBand.values()` |  |
| 55 | `CausesOfDeathCheck.BABY` | `AgeBand.BABY.ordinal()` |  |
| 56 | `CausesOfDeathCheck.CHILD` | `AgeBand.CHILD.ordinal()` |  |
| 57 | `CausesOfDeathCheck.TEEN` | `AgeBand.TEEN.ordinal()` |  |
| 58 | `CausesOfDeathCheck.ADULT` | `AgeBand.ADULT.ordinal()` |  |
| 61 | `CausesOfDeathCheck.CITY` | `100_000` | The people in the 1900 city. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 883 | **type** `public class CausesOfDeathCheck` | Proves the causes of death of 1900 (R0b, slice 1): the model on its own, before the month hands its rates to the cohorts. |
| 36 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 44 | 3 | `static void checkRel(String label, double actual, double expected, double rel)` | Within a relative tolerance of the expected value. |
| 48 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 3 | `static int month(int year, int monthOfYear)` | The game month of a calendar month. |
| 68 | 5 | `static double sum(double[] v)` |  |
| 74 | 5 | `static double[] people()` |  |
| 81 | 3 | `static double births()` | Thirty births per 1,000 people a year, a month's worth. |
| 86 | 3 | `static double[] topOfSwing()` | Healthcare's factors with no childcare and everybody covered by general and senior care. |
| 91 | 5 | `static double[] swungAt(double f)` | The factors with the swung bands set to f. |
| 98 | 4 | `static double[][] city(CausesOfDeath m, int month, double piped, double hungry, double general, double severity, double[] factor)` | The 1900 city's month, with its conditions as given. |
| 104 | 3 | `static double[][] city(CausesOfDeath m, int month)` | The 1900 city's month at the mean outbreak, in the best of conditions. |
| 109 | 8 | `static double[] dead(double[][] rates)` | Each cause's dead in the month, for the city's people. |
| 119 | 8 | `static double[] year(CausesOfDeath m, int year)` | Each cause's dead in a calendar year, per 100,000 people. |
| 129 | 5 | `static double average(CausesOfDeath m, Cause c, int from, int to)` | One cause's yearly dead per 100,000, averaged over the years from..to inclusive. |
| 136 | 3 | `static String one(AgeBand band)` | A band's member, in the singular. |
| 141 | 5 | `static double meanHistory(Cause c, int year)` | The mean of a cause's history over a calendar year's twelve months. |
| 147 | 5 | `static void quietly(Runnable work)` |  |
| 158 | 10 | `static void stock(Game g)` | A city three times the size its founding doctor was meant for, SicknessCheck's fixture: without hospitals its general care is thin, and the dying has people to fall on. |
| 170 | 10 | `static Game founded()` | The real city of sections 9 and 10: founded in 1900, stocked, and played a month at a time. |
| 181 | 436 | `public static void main(String[] args)` |  |

### 9. A REAL CITY'S MONTH BOOKS THE CAUSES (lines 618-673)

| line | len | member | says |
|---:|---:|---|---|
| 622 | 51 | `static void realCityBooksTheCauses(Game g)` |  |

### 10. THE 1918 INFLUENZA IN A REAL CITY (lines 674-914)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 86 | `static void theInfluenzaInARealCity(Game g)` |  |
| 770 | 42 | `static String firstDifference(Game a, Game b)` | The first field in which two cities differ, with both values, or null when they are the same to the bit: the fingerprint DeterminismCheck takes, every pool the money audit reads, the cohorts' people in each band and t... |
| 823 | 55 | `static void reloadedMidInfluenzaAndPlayedOn()` | (a) A reload mid-influenza, played on. |
| 884 | 30 | `static void theHoldKeptAcrossALoad(Game g, double unheldFluRate)` | (b) A fixture's order: hold, then load. |


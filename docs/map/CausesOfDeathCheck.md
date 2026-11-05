# CausesOfDeathCheck.java - 755 lines · 22 methods · 7 constants · harnesses

`ham/citybuildersim/CausesOfDeathCheck.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

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
> 
> The 1900 city of sections 2, 4, 5 and 6 is Canada's 1901 pyramid at 100,000
> people with no childcare (the swing at its top), general care for everybody,
> piped water, nobody hungry, and the mean outbreak running. Sections 9 and 10
> play one real city, founded in 1900, a month at a time.

**Uses:** [CausesOfDeath](CausesOfDeath.md) (114), [AgeBand](AgeBand.md) (41), [Game](Game.md) (9), [Health](Health.md) (6), [Healthcare](Healthcare.md) (2), [Sickness](Sickness.md) (2), [CityCalendar](CityCalendar.md) (1), [BuildingManager](BuildingManager.md) (1), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Notice](Notice.md) (1)

## Sections

| line | section |
|---:|---|
| 181 | · 1. the constants hold together |
| 227 | · 2. a 1900 city dies of each cause at its 1900 rate |
| 246 | · 3. the split leaves a 1900 baby's rate where it was |
| 307 | · 4. seasons and outbreaks |
| 356 | · 5. history on its dates |
| 391 | · 6. the city's conditions raise a cause, never lower it |
| 445 | · 7. the 1918 influenza |
| 477 | · 8. booking the dead, the TB sick and the save |
| 614 | 9. A REAL CITY'S MONTH BOOKS THE CAUSES |
| 670 | 10. THE 1918 INFLUENZA IN A REAL CITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `CausesOfDeathCheck.CAUSES` | `Cause.values()` |  |
| 50 | `CausesOfDeathCheck.BANDS` | `AgeBand.values()` |  |
| 51 | `CausesOfDeathCheck.BABY` | `AgeBand.BABY.ordinal()` |  |
| 52 | `CausesOfDeathCheck.CHILD` | `AgeBand.CHILD.ordinal()` |  |
| 53 | `CausesOfDeathCheck.TEEN` | `AgeBand.TEEN.ordinal()` |  |
| 54 | `CausesOfDeathCheck.ADULT` | `AgeBand.ADULT.ordinal()` |  |
| 57 | `CausesOfDeathCheck.CITY` | `100_000` | The people in the 1900 city. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 728 | **type** `public class CausesOfDeathCheck` | Proves the causes of death of 1900 (R0b, slice 1): the model on its own, before the month hands its rates to the cohorts. |
| 32 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 40 | 3 | `static void checkRel(String label, double actual, double expected, double rel)` | Within a relative tolerance of the expected value. |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 60 | 3 | `static int month(int year, int monthOfYear)` | The game month of a calendar month. |
| 64 | 5 | `static double sum(double[] v)` |  |
| 70 | 5 | `static double[] people()` |  |
| 77 | 3 | `static double births()` | Thirty births per 1,000 people a year, a month's worth. |
| 82 | 3 | `static double[] topOfSwing()` | Healthcare's factors with no childcare and everybody covered by general and senior care. |
| 87 | 5 | `static double[] swungAt(double f)` | The factors with the swung bands set to f. |
| 94 | 4 | `static double[][] city(CausesOfDeath m, int month, double piped, double hungry, double general, double severity, double[] factor)` | The 1900 city's month, with its conditions as given. |
| 100 | 3 | `static double[][] city(CausesOfDeath m, int month)` | The 1900 city's month at the mean outbreak, in the best of conditions. |
| 105 | 8 | `static double[] dead(double[][] rates)` | Each cause's dead in the month, for the city's people. |
| 115 | 8 | `static double[] year(CausesOfDeath m, int year)` | Each cause's dead in a calendar year, per 100,000 people. |
| 125 | 5 | `static double average(CausesOfDeath m, Cause c, int from, int to)` | One cause's yearly dead per 100,000, averaged over the years from..to inclusive. |
| 132 | 3 | `static String one(AgeBand band)` | A band's member, in the singular. |
| 137 | 5 | `static double meanHistory(Cause c, int year)` | The mean of a cause's history over a calendar year's twelve months. |
| 143 | 5 | `static void quietly(Runnable work)` |  |
| 154 | 10 | `static void stock(Game g)` | A city three times the size its founding doctor was meant for, SicknessCheck's fixture: without hospitals its general care is thin, and the dying has people to fall on. |
| 166 | 10 | `static Game founded()` | The real city of sections 9 and 10: founded in 1900, stocked, and played a month at a time. |
| 177 | 436 | `public static void main(String[] args)` |  |

### 9. A REAL CITY'S MONTH BOOKS THE CAUSES (lines 614-669)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 51 | `static void realCityBooksTheCauses(Game g)` |  |

### 10. THE 1918 INFLUENZA IN A REAL CITY (lines 670-755)

| line | len | member | says |
|---:|---:|---|---|
| 674 | 81 | `static void theInfluenzaInARealCity(Game g)` |  |


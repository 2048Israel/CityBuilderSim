# CausesOfDeathCheck.java - 1,023 lines · 30 methods · 7 constants · harnesses

`ham/citybuildersim/CausesOfDeathCheck.java` - generated 2026-11-16 by CodeMap; line numbers are as of that run.

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
>      (a) reloads played on, to the bit: the bulk-stocked city at months 3, 12 and 60, and January to March 1919
>          on both that city and the playtest's founding (SR1)
>      (b) the fixtures' hold kept across a load: a held twin loaded from September 1918 stays held to March 1920
>      (c) September 1918 twins with only Health's influenza held off: no delayed ring deaths beyond their economic drift (R0h)
> 
> The 1900 city of sections 2, 4, 5 and 6 is Canada's 1901 pyramid at 100,000
> people with no childcare (the swing at its top), general care for everybody,
> piped water, nobody hungry, and the mean outbreak running. Sections 9 and 10
> play one real city, founded in 1900, a month at a time.

**Uses:** [CausesOfDeath](CausesOfDeath.md) (123), [AgeBand](AgeBand.md) (44), [Game](Game.md) (27), [Sickness](Sickness.md) (7), [Health](Health.md) (6), [MoneyAudit](MoneyAudit.md) (5), [PopulationCohorts](PopulationCohorts.md) (4), [Healthcare](Healthcare.md) (3), [CareType](CareType.md) (3), [GameFiles](GameFiles.md) (2), [DeterminismCheck](DeterminismCheck.md) (2), [CityCalendar](CityCalendar.md) (1), [BuildingManager](BuildingManager.md) (1), [Founding](Founding.md) (1), [Notice](Notice.md) (1), [LongPlaytest](LongPlaytest.md) (1)

## Sections

| line | section |
|---:|---|
| 193 | · 1. the constants hold together |
| 239 | · 2. a 1900 city dies of each cause at its 1900 rate |
| 258 | · 3. the split leaves a 1900 baby's rate where it was |
| 319 | · 4. seasons and outbreaks |
| 368 | · 5. history on its dates |
| 403 | · 6. the city's conditions raise a cause, never lower it |
| 457 | · 7. the 1918 influenza |
| 489 | · 8. booking the dead, the TB sick and the save |
| 626 | 9. A REAL CITY'S MONTH BOOKS THE CAUSES |
| 682 | 10. THE 1918 INFLUENZA IN A REAL CITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `CausesOfDeathCheck.CAUSES` | `Cause.values()` |  |
| 55 | `CausesOfDeathCheck.BANDS` | `AgeBand.values()` |  |
| 56 | `CausesOfDeathCheck.BABY` | `AgeBand.BABY.ordinal()` |  |
| 57 | `CausesOfDeathCheck.CHILD` | `AgeBand.CHILD.ordinal()` |  |
| 58 | `CausesOfDeathCheck.TEEN` | `AgeBand.TEEN.ordinal()` |  |
| 59 | `CausesOfDeathCheck.ADULT` | `AgeBand.ADULT.ordinal()` |  |
| 62 | `CausesOfDeathCheck.CITY` | `100_000` | The people in the 1900 city. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 991 | **type** `public class CausesOfDeathCheck` | Proves the causes of death of 1900 (R0b, slice 1): the model on its own, before the month hands its rates to the cohorts. |
| 37 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 45 | 3 | `static void checkRel(String label, double actual, double expected, double rel)` | Within a relative tolerance of the expected value. |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 65 | 3 | `static int month(int year, int monthOfYear)` | The game month of a calendar month. |
| 69 | 5 | `static double sum(double[] v)` |  |
| 75 | 5 | `static double[] people()` |  |
| 82 | 3 | `static double births()` | Thirty births per 1,000 people a year, a month's worth. |
| 87 | 3 | `static double[] topOfSwing()` | Healthcare's factors with no childcare and everybody covered by general and senior care. |
| 92 | 5 | `static double[] swungAt(double f)` | The factors with the swung bands set to f. |
| 99 | 4 | `static double[][] city(CausesOfDeath m, int month, double piped, double hungry, double general, double severity, double[] factor)` | The 1900 city's month, with its conditions as given. |
| 105 | 3 | `static double[][] city(CausesOfDeath m, int month)` | The 1900 city's month at the mean outbreak, in the best of conditions. |
| 110 | 8 | `static double[] dead(double[][] rates)` | Each cause's dead in the month, for the city's people. |
| 120 | 8 | `static double[] year(CausesOfDeath m, int year)` | Each cause's dead in a calendar year, per 100,000 people. |
| 130 | 5 | `static double average(CausesOfDeath m, Cause c, int from, int to)` | One cause's yearly dead per 100,000, averaged over the years from..to inclusive. |
| 137 | 3 | `static String one(AgeBand band)` | A band's member, in the singular. |
| 142 | 5 | `static double meanHistory(Cause c, int year)` | The mean of a cause's history over a calendar year's twelve months. |
| 148 | 5 | `static void quietly(Runnable work)` |  |
| 159 | 10 | `static void stock(Game g)` | A city three times the size its founding doctor was meant for, SicknessCheck's fixture: without hospitals its general care is thin, and the dying has people to fall on. |
| 178 | 10 | `static Game founded()` | The real city of sections 9 and 10: founded in 1900, stocked, and played a month at a time. |
| 189 | 436 | `public static void main(String[] args)` |  |

### 9. A REAL CITY'S MONTH BOOKS THE CAUSES (lines 626-681)

| line | len | member | says |
|---:|---:|---|---|
| 630 | 51 | `static void realCityBooksTheCauses(Game g)` |  |

### 10. THE 1918 INFLUENZA IN A REAL CITY (lines 682-1023)

| line | len | member | says |
|---:|---:|---|---|
| 686 | 87 | `static void theInfluenzaInARealCity(Game g)` |  |
| 779 | 43 | `static String firstDifference(Game a, Game b)` | The first field in which two cities differ, with both values, or null when they are the same to the bit: the fingerprint DeterminismCheck takes, every pool the money audit reads, the cohorts' people in each band and t... |
| 824 | 7 | `static void reloadedMidInfluenzaAndPlayedOn()` | (a) Keep the original complete bulk stock, and the playtest founding, beside their reloads (SR1). |
| 833 | 34 | `static void stockedReloadsPlayedOn()` | R0g's earliest three failing saves, played on without changing founded()'s premise. |
| 869 | 55 | `static void reloadedMidInfluenzaAndPlayedOn(Game city, String fixture)` | Both founding fixtures play to December 1918, reload, then match through March 1919, to the bit. |
| 926 | 46 | `static void theInfluenzaCountedOnce(Game saved)` | R0g's September 1918 twins: only Health's influenza sick are held off, with causes on in both. |
| 974 | 6 | `static double ringDeaths(Sickness ring, PopulationCohorts people)` | Ring deaths on opening cohorts; the fixture checks that competing-risk caps do not reduce them. |
| 982 | 5 | `static void advanceChronic(Sickness ring, Game city)` | Replay only each twin's observed chronic inputs, retaining its own economic and care drift. |
| 993 | 30 | `static void theHoldKeptAcrossALoad(Game g, double unheldFluRate)` | (b) A fixture's order: hold, then load. |


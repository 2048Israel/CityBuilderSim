# MedicineCheck.java - 605 lines · 34 methods · 3 constants · harnesses

`ham/citybuildersim/MedicineCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Proves the research catalogue's medical rows (R2f) move the constants they map to: a medical row a 1900 city finds ahead of the world brings
> its step on a cause of death forward, and a row it lacks leaves its constant undone until the month history had it or the month the city found
> it - and a city that found nothing, or was founded the default way, is the city it was before R2f, to the bit.
> 
> WHY. Before R2f a found medical row changed nothing: the catalogue (R2) dated the sixteen rows and the city's schools could find them (R3),
> but no constant read the finds, so a 1900 city that found streptomycin ten years early died of TB on history's dates all the same, and its
> general care cured the sick at today's rate in 1900. Research-design Q17 (Jerus, "rec all", 2026-10-18) maps each medical discovery to the
> constants it moves. A row mapped to the wrong constant, or to none, or a step that does not come home to history's own, would move the
> dying silently, and the default city (which finds nothing) must not move at all. So the map is held to the catalogue, the steps to history,
> the undone constants to the row's month, and a real city to the model's own readings.
> 
> What this has to prove:
> 
>   1. every medical row is mapped: the catalogue's medicine rows are MedicalRows.CODES, each has a group (a step, a stays row, the TB drugs'
>      long slot, the breathing machine, the building, or nothing), and the rows that move nothing are in no other group
>   2. the steps are history's: each step's factor is what history's knots do over its dates
>   3. a city that found nothing is history's path to the bit (TODAY, and a 1900 city with an empty research): ahead() is 1 for every cause
>      and month, rates() equals the rates of no rows cell for cell, and Sickness's static factors are today's
>   4. a row found ahead of the world brings its step forward, and history then has it: the product of the step's factors before history's
>      ramp, 1 after it, one cell of the fixture's rates times the factor, and the formula written out for a find only a year ahead
>   5. the undone constants switch at the row's month, and at a find: the long slot's recovery (M1), the served long sick's deaths (M2) and
>      general care's stays (PM5 and the later rows)
>   6. a ring reads them: two Sickness twins seeded and turned a month (the last slot's tail before M1, the deaths before M2, the recovery
>      before the stays rows, and TODAY the ring of before R2f)
>   7. a real city reads them: a 1900 village and a default village played, M1 found here, a twin with PM1 found at month 1, and M8's building
> 
> The 1900 city of sections 3 and 4 is CausesOfDeathCheck's: Canada's 1901 pyramid at 100,000 people with no childcare, general care for
> everybody, piped water, nobody hungry, and the mean outbreak running.

**Uses:** [Sickness](Sickness.md) (79), [CausesOfDeath](CausesOfDeath.md) (31), [MedicalRows](MedicalRows.md) (28), [Research](Research.md) (12), [AgeBand](AgeBand.md) (8), [Game](Game.md) (8), [CausesOfDeathCheck](CausesOfDeathCheck.md) (4), [ResearchCatalog](ResearchCatalog.md) (3), [CityCalendar](CityCalendar.md) (3), [FittingCheck](FittingCheck.md) (2), [Founding](Founding.md) (2), [PolicyDates](PolicyDates.md) (1), [EquipmentCheck](EquipmentCheck.md) (1), [BuildingManager](BuildingManager.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [CareType](CareType.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 83 | · the check's own readings |
| 200 | 1. every medical row is mapped |
| 236 | 2. the steps are history's |
| 259 | 3. a city that found nothing is history's path to the bit |
| 311 | 4. a row found ahead of the world brings its step forward, and history then has it |
| 365 | 5. the undone constants switch at the row's month, and at a find |
| 413 | 6. a ring reads them |
| 514 | 7. a real city reads them |

## Enum constants

| line | constant | says |
|---:|---|---|
| 143 | `MedicineCheck.Group.STEP` |  |
| 143 | `MedicineCheck.Group.STAYS` |  |
| 143 | `MedicineCheck.Group.LONG` |  |
| 143 | `MedicineCheck.Group.SERVED` |  |
| 143 | `MedicineCheck.Group.BUILDING` |  |
| 143 | `MedicineCheck.Group.NOTHING` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `MedicineCheck.out` | `System.out` | The real output: quietly() swaps System.out, and the log is this. |
| 146 | `MedicineCheck.TABLE` | `new LinkedHashMap<>()` | Every medical row's group or groups, typed from research-design Q17 and R2f's notes; section 1 holds the model to it. |
| 431 | `MedicineCheck.RATE` | `.05` | The sick rate the twins are seeded at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |
| 54 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 559 | **type** `public class MedicineCheck` | Proves the research catalogue's medical rows (R2f) move the constants they map to: a medical row a 1900 city finds ahead of the world brings its step on a cause of death forward, and a row it lacks leaves its constant... |
| 56 | 5 | `static void quietly(Runnable r)` |  |
| 62 | 3 | `static void check(String label, boolean ok)` |  |
| 67 | 5 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |
| 73 | 4 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 79 | 3 | `static void nearRel(String label, double actual, double expected, double rel)` | Within a relative tolerance of the expected value. |

### the check's own readings (lines 83-199)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 3 | `static int month(int year, int monthOfYear)` |  |
| 89 | 3 | `static int world(String code)` |  |
| 94 | 3 | `static double decimalYear(int month)` | A game month's decimal year, as history's knots and the steps read it. |
| 99 | 3 | `static int monthAtOrAfter(double decimalYear)` | The first game month whose decimal year is at or after a decimal year. |
| 103 | 3 | `static double clamp01(double v)` |  |
| 108 | 5 | `static Research finds(Map<String, Integer> found)` | A research that found each code at the given month, and nothing else. |
| 115 | 3 | `static Research foundAhead(String code, int monthsAhead)` | A research that found one code this many months before the world. |
| 120 | 3 | `static MedicalRows nothingFound()` | What a 1900-way city that found nothing knows. |
| 125 | 5 | `static double stepsProduct(String code, Cause cause)` | The product of the factors of one row's steps on one cause: 1 where it has none. |
| 132 | 4 | `static double[][] cityWith(CausesOfDeath m, int month, MedicalRows rows)` | The 1900 city's month with the rows given: CausesOfDeathCheck's fixture, at the mean outbreak, in the best of conditions. |
| 138 | 3 | `static double[][] cityWithout(CausesOfDeath m, int month)` | The same with no rows: the call that was before R2f. |
| 143 | 1 | **type** `enum Group` | Where a row's group sits in the check's table. |
| 148 | 3 | `static void row(String code, Group first, Group...more)` |  |
| 152 | 18 | `static { ... }` |  |
| 172 | 6 | `static List<String> staysRows()` | The rows that shorten general care's stays: PM5 first, then the later four. |
| 179 | 3 | `static double staysShare(String code)` |  |
| 183 | 16 | `public static void main(String[] args)` |  |

### 1. every medical row is mapped (lines 200-235)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 33 | `static void sectionOne()` |  |

### 2. the steps are history's (lines 236-258)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 20 | `static void sectionTwo()` |  |

### 3. a city that found nothing is history's path to the bit (lines 259-310)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 49 | `static void sectionThree()` |  |

### 4. a row found ahead of the world brings its step forward, and history then has it (lines 311-364)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 51 | `static void sectionFour()` |  |

### 5. the undone constants switch at the row's month, and at a find (lines 365-412)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 45 | `static void sectionFive()` |  |

### 6. a ring reads them (lines 413-513)

| line | len | member | says |
|---:|---:|---|---|
| 416 | 5 | `static MedicalRows knowing(List<String> codes)` | Every code in the list found at month 1: those rows are known in any month. |
| 423 | 6 | `static Sickness twin(MedicalRows rows, double coverage)` | A ring seeded at the rate with the month-1 medicine of the rows, general care at the coverage. |
| 433 | 5 | `static double ringSum(Sickness s, AgeBand band)` |  |
| 440 | 12 | `static void keepsItsShare(String name, Sickness s, double coverage)` | One advanceMonth on a twin keeps every slot finite and every ring at its band's share. |
| 453 | 60 | `static void sectionSix()` |  |

### 7. a real city reads them (lines 514-605)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 9 | `static Game hospitalVillage(String label, boolean the1900Way)` | A village (FittingCheck.village) with a General Hospital stack standing and the staff handed; founded the 1900 way or the default way. |
| 527 | 3 | `static void play(Game g, int months)` |  |
| 531 | 4 | `static boolean stands(Game g, String name)` |  |
| 536 | 69 | `static void sectionSeven()` |  |


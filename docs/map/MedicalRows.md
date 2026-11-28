# MedicalRows.java - 64 lines · 5 methods · 2 constants · model

`ham/citybuildersim/MedicalRows.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Which of the research catalogue's medical rows (M, PM) a city knows in a
> month, and how far ahead of the world it found each (R2f).
> 
> WHY. The catalogue (R2) dated the sixteen medical rows and the city's
> schools could find them (R3), but nothing read them: a 1900 city that found
> streptomycin ten years early died of TB on history's dates all the same, and
> general care cured its sick at today's rate in 1900. Research-design Q17
> (Jerus, "rec all", 2026-10-18): map each medical discovery to the constants
> it moves. The constants live where their mechanisms do (CausesOfDeath's
> steps, Sickness's recovery and deaths); this class is only the question
> they ask, so that a harness can hand them a city's knowledge without a Game.
> 
> ONLY A CITY FOUNDED THE 1900 WAY READS IT (R2 call 3). A city founded the
> default way knows every row from its first month and finds nothing here
> (Research.idleMonth()), so it is TODAY: every constant is today's and every
> cause of death is on history's path, as before R2f.

**Uses:** [Research](Research.md) (3), [ResearchCatalog](ResearchCatalog.md) (1)

**Used by (5):** [CausesOfDeath](CausesOfDeath.md), [Game](Game.md), [MedicineCheck](MedicineCheck.md), [ModernRowsCheck](ModernRowsCheck.md), [Sickness](Sickness.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `MedicalRows.TODAY` | `new MedicalRows(null)` | A city founded the default way: every row known from its first month, none found ahead of the world. |
| 29 | `MedicalRows.CODES` | `List.of("M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "PM1", "...` | The medical rows in the catalogue's order, as research.json has them; MedicineCheck holds the file to this list. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final Research research` | The city's research, or null for TODAY. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 42 | **type** `public final class MedicalRows` | Which of the research catalogue's medical rows (M, PM) a city knows in a month, and how far ahead of the world it found each (R2f). |
| 36 | 3 | `private MedicalRows(Research research)` |  |
| 41 | 3 | `public static MedicalRows of(Research research, boolean foundedUnfitted)` | What a city knows: its research in a city founded the 1900 way, TODAY in any other (or with no research). |
| 46 | 1 | `public boolean isToday()` | Whether this is a default city's reading, every row known and none ahead. |
| 49 | 3 | `public boolean known(String code, int month)` | Whether a row is known in this city in a game month: always in TODAY; else from history's month or the month the city found it. |
| 57 | 7 | `public int monthsAhead(String code)` | How many months before the world the city found a row: 0 where it did not find it here, for a code the catalogue lacks, and always in TODAY. |


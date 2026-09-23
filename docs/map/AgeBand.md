# AgeBand.java - 181 lines · 12 methods · 1 constants · model

`ham/citybuildersim/AgeBand.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The six ages of a resident.
> 
> Each band knows the span it covers, and therefore what share of it moves up
> every month. Ages are inclusive of the first year and exclusive of the next
> band's, so a resident is a BABY from 0 up to their sixth birthday.

**Used by (31):** [CareType](CareType.md), [Crime](Crime.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Education](Education.md), [EducationType](EducationType.md), [FamilyModel](FamilyModel.md), [FamilyStructure](FamilyStructure.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HistorySave](HistorySave.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [Inbox](Inbox.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [Offending](Offending.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [PopulationCohorts](PopulationCohorts.md), [RetiredHousehold](RetiredHousehold.md), [ServicesScreen](ServicesScreen.md), [Sickness](Sickness.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [Unemployment](Unemployment.md)

## Sections

| line | section |
|---:|---|
| 88 | MORTALITY |

## Enum constants

| line | constant | says |
|---:|---|---|
| 12 | `AgeBand.BABY` |  |
| 13 | `AgeBand.CHILD` |  |
| 14 | `AgeBand.TEEN` |  |
| 15 | `AgeBand.ADULT` |  |
| 16 | `AgeBand.SENIOR` |  |
| 17 | `AgeBand.ELDER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 180 | `AgeBand.MAX_MONTHLY_MORTALITY` | `.05` | No band may lose more than this in a single month, whatever modifies it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `private final String label` |  |
| 20 | `private final int fromAge` |  |
| 21 | `private final int toAge` |  |
| 22 | `private final double annualMortality` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 10 | 172 | **type** `public enum AgeBand` | The six ages of a resident. |
| 24 | 6 | `AgeBand(String label, int fromAge, int toAge, double annualMortality)` |  |
| 31 | 1 | `public String getLabel()` |  |
| 32 | 1 | `public int getFromAge()` |  |
| 33 | 1 | `public int getToAge()` |  |
| 36 | 3 | `public int spanMonths()` | How many months a resident spends here, on average. |
| 60 | 3 | `public double monthlyOutflowRate()` | The fraction that moves up each month. |
| 65 | 4 | `public AgeBand next()` | The band after this one, or null for the last. |
| 71 | 3 | `public boolean isWorkingAge()` | True for the bands that can hold a job. |
| 84 | 3 | `public boolean isRetirementAge()` | True for the bands past working age. |

### MORTALITY (lines 88-181)

| line | len | member | says |
|---:|---:|---|---|
| 144 | 3 | `public double getAnnualMortality()` |  |
| 167 | 3 | `public double monthlyMortality()` | Monthly chance of dying, COMPOUNDED rather than divided. |
| 172 | 6 | `public static double monthlyFromAnnual(double annualRate)` | The same conversion, exposed so a modifier can be applied honestly. |


# WageBand.java - 152 lines · 7 methods · 0 constants · model

`ham/citybuildersim/WageBand.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The four education bands the wage tax is set by.
> 
> There are eleven JobTypes and eleven separate wage rates behind them, but a
> tax screen with eleven dials is not a decision - it is data entry, and ten of
> the eleven would move together anyway. These are the bands the job types
> already fall into by the education they require, so setting them is the
> progressive-tax choice in the form a player actually thinks about it: what do
> I charge the people I most want to attract?
> 
> The arithmetic is still exact. PopulationManager tracks the wage bill per job
> type, so the tax is summed per type at its band's rate - the banding decides
> what the player sets, never what is charged.

**Uses:** [JobType](JobType.md) (1)

**Used by (19):** [Education](Education.md), [EducationCheck](EducationCheck.md), [EducationType](EducationType.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [LabourCheck](LabourCheck.md), [LabourMarket](LabourMarket.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TaxPolicy](TaxPolicy.md)

## Sections

| line | section |
|---:|---|
| 34 | THE SECOND JOB THIS ENUM DOES |

## Enum constants

| line | constant | says |
|---:|---|---|
| 19 | `WageBand.NONE` |  |
| 20 | `WageBand.DIPLOMA` |  |
| 21 | `WageBand.COLLEGE` |  |
| 22 | `WageBand.UNIVERSITY` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `private final String label` |  |
| 25 | `private final double arrivalCeiling` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 17 | 136 | **type** `public enum WageBand` | The four education bands the wage tax is set by. |
| 27 | 4 | `WageBand(String label, double arrivalCeiling)` |  |
| 32 | 1 | `public String label()` |  |

### THE SECOND JOB THIS ENUM DOES (lines 34-152)

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1 | `public int rank()` | 0 for unskilled, 3 for university. |
| 94 | 1 | `public double arrivalCeiling()` | The most of a month's arrivals this band can ever be, relative to the diploma band's 1.00 - and it is reached only at the wage ceiling. |
| 121 | 9 | `public double mobility()` | How readily somebody at this level will move away for work. |
| 132 | 1 | `public static WageBand[] ladder()` | The band a worker of this level can also work down into. |
| 144 | 8 | `public static WageBand of(JobType job)` | Which band a job sits in. |


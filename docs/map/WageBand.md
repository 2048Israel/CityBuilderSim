# WageBand.java - 170 lines · 7 methods · 0 constants · model

`ham/citybuildersim/WageBand.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

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

**Used by (26):** [BankCheck](BankCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [CityNeeds](CityNeeds.md), [EconomyManager](EconomyManager.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [EducationType](EducationType.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LabourMarket](LabourMarket.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TaxPolicy](TaxPolicy.md)

## Sections

| line | section |
|---:|---|
| 45 | THE SECOND JOB THIS ENUM DOES |

## Enum constants

| line | constant | says |
|---:|---|---|
| 30 | `WageBand.NONE` | NONE's ceiling (0.7.18) is the share of working-age migrants with no certificate, diploma or degree, read the way the graduate ceilings are read: at every band's ceiling it is that share of a month's arrivals. |
| 31 | `WageBand.DIPLOMA` |  |
| 32 | `WageBand.COLLEGE` |  |
| 33 | `WageBand.UNIVERSITY` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `private final String label` |  |
| 36 | `private final double arrivalCeiling` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 17 | 154 | **type** `public enum WageBand` | The four education bands the wage tax is set by. |
| 38 | 4 | `WageBand(String label, double arrivalCeiling)` |  |
| 43 | 1 | `public String label()` |  |

### THE SECOND JOB THIS ENUM DOES (lines 45-170)

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1 | `public int rank()` | 0 for unskilled, 3 for university. |
| 112 | 1 | `public double arrivalCeiling()` | The most of a month's arrivals this band can ever be, relative to the diploma band's 1.00 - and it is reached only at the wage ceiling. |
| 139 | 9 | `public double mobility()` | How readily somebody at this level will move away for work. |
| 150 | 1 | `public static WageBand[] ladder()` | The band a worker of this level can also work down into. |
| 162 | 8 | `public static WageBand of(JobType job)` | Which band a job sits in. |


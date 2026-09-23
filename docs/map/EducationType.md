# EducationType.java - 191 lines · 11 methods · 0 constants · model

`ham/citybuildersim/EducationType.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> What a school actually teaches.
> 
> The same shape as CareType and for the same reason: nine education buildings
> would otherwise know their capacity and not what that capacity was FOR. A
> High School's 900 places and a Medical School's 90 are both "capacity", and
> without this field the only thing telling them apart is the name on the
> button - which is to say, nothing a line of code can read.
> 
> TWO KINDS OF SCHOOL, AND THE DIFFERENCE IS THE WHOLE DESIGN
> 
> The first six move a person UP THE LADDER: elementary, middle and high school
> turn a child into somebody with a diploma; a college and a university turn a
> diploma into a qualification. What comes out is a WageBand, and within a band
> workers are interchangeable.
> 
> The last four do something else entirely - they do not raise anybody's level,
> they make a PROFESSION possible. A city without a medical school can have all
> the university graduates it likes and not one of them can be a doctor; the
> posts sit empty and the only doctors it will ever have are the ones who moved
> there. Build the school and its graduates can hold that job.
> 
> Jerus's design: "they gate the specialisations". It breaks the
> within-a-band fungibility exactly where that simplification is least
> believable, and it turns each graduate school into a distinct thing a city
> decides to become rather than another building that makes the same number
> bigger.
> 
> WHY NOTHING REFUSES TO BE BUILT
> 
> A graduate school in a town of forty thousand is not forbidden, it is
> ruinous. Jerus: "basically there would be one student in the whole grad
> school, so cost ineffective basically." The cost is fixed and enormous; the
> throughput is however many people are eligible and willing, which in a small
> city is nearly nobody. So the eight-hundred-thousand-population gate everyone
> talks about is not a rule anywhere in this file - it is where the arithmetic
> stops being stupid, and the player can work it out by reading the build menu.

**Uses:** [WageBand](WageBand.md) (10), [JobType](JobType.md) (7), [AgeBand](AgeBand.md) (3)

**Used by (17):** [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [GovernmentScreen](GovernmentScreen.md), [LabourMarket](LabourMarket.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationManager](PopulationManager.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 44 | `EducationType.NONE` | Every building in the game that is not a school. |
| 55 | `EducationType.ELEMENTARY` | Ages 6-10, the first half of CHILD. |
| 58 | `EducationType.MIDDLE` | Ages 10-13, the second half. |
| 61 | `EducationType.HIGH` | The TEEN band, and what actually hands out the diploma. |
| 65 | `EducationType.COLLEGE` |  |
| 66 | `EducationType.UNIVERSITY` |  |
| 74 | `EducationType.MEDICAL` |  |
| 75 | `EducationType.LAW` |  |
| 76 | `EducationType.BUSINESS` |  |
| 77 | `EducationType.ENGINEERING` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `private final String label` |  |
| 80 | `private final WageBand produces` |  |
| 81 | `private final JobType licenses` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 151 | **type** `public enum EducationType` | What a school actually teaches. |
| 83 | 5 | `EducationType(String label, WageBand produces, JobType licenses)` |  |
| 89 | 1 | `public String getLabel()` |  |
| 92 | 1 | `public WageBand produces()` | The band somebody leaves with, or null for a stage that only feeds one. |
| 95 | 1 | `public boolean isAdult()` | A course adults enrol in and leave the labour supply for; the basic stages are not. |
| 98 | 1 | `public JobType licenses()` | The job this school makes possible, or null if it teaches a level. |
| 101 | 3 | `public boolean isBasic()` | True for the three stages that a child has to pass through in order. |
| 106 | 1 | `public boolean isProfessional()` | True for the four that gate a profession rather than raising a level. |
| 116 | 8 | `public AgeBand servesAges()` | Which age band this school's places are measured against. |
| 133 | 11 | `public WageBand requires()` | What somebody must already have to enrol. |
| 159 | 9 | `public double worldLicenceShare()` | What share of the outside world's graduates already hold this licence. |
| 177 | 14 | `public int months()` | How long the course runs, in months. |


# Education.java - 874 lines · 43 methods · 7 constants · model

`ham/citybuildersim/Education.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Who the city teaches, what it costs, and why anybody bothers.
> 
> THE PROBLEM THIS SOLVES
> 
> Before schools existed, every skilled worker in the game had arrived from
> somewhere else. The labour market could make a doctor EXPENSIVE and it could
> not make a doctor, so a city's whole skill mix was a function of how many
> strangers it had persuaded to move in - and WageBand.arrivalCeiling() means
> a graduate arrives only where the city is paying over the going rate, and
> even then out of a world with few to spare. A city of eighty thousand cannot
> import two hundred doctors at any price. At some size it has to make its own
> or go without.
> 
> WHAT MAKES A PERSON GET EDUCATED
> 
> Three things, and they answer three different questions. Jerus picked all
> three, and the interesting part is that each one fails differently:
> 
>   CAN THEY GO?     Places. A school seats so many, and a course lasts so
>                    long, so a university of 2,000 seats and a four-year
>                    degree graduates forty a month. This is the cap.
> 
>   WOULD THEY?      The return. Nobody spends four years to earn what they
>                    already earn, so enrolment follows the gap between what
>                    the next band pays and what this one does - which the
>                    labour market is already computing every month for its
>                    own reasons.
> 
>   CAN THEY AFFORD IT?  Tuition against a wage. This is the one that bites,
>                    and it is the reason the subsidy dial exists: at no
>                    subsidy a university education costs a diploma-holder half
>                    their monthly income and almost nobody goes, so a city of
>                    poor unskilled workers CANNOT EDUCATE ITS WAY OUT. That is
>                    a real trap with three different fixes - build schools,
>                    raise wages, or pay the tuition - and it is the most
>                    interesting thing on this page.
> 
> TWO KINDS OF OUTPUT
> 
> The basic ladder and the colleges raise a person's BAND, which is a level.
> The four professional schools raise nobody's level; they license a job. See
> EducationType - a city with no medical school has graduates and no doctors.
> 
> THE PIPELINE IS ONLY AS WIDE AS ITS NARROWEST STAGE
> 
> Elementary and middle school serve CHILD, high school serves TEEN, and a
> child who never went to middle school does not turn up at high school. So the
> diploma rate is the MINIMUM of the three coverages and not their average: a
> city with elementary places for everybody and one high school produces as
> many diplomas as the high school can seat, and the People screen can say
> which stage is the bottleneck.

**Uses:** [EducationType](EducationType.md) (59), [WageBand](WageBand.md) (16), [LabourMarket](LabourMarket.md) (11), [JobType](JobType.md) (5), [PopulationManager](PopulationManager.md) (5), [AgeBand](AgeBand.md) (3), [PopulationCohorts](PopulationCohorts.md) (2), [PayTier](PayTier.md) (1)

**Used by (8):** [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [MoneyAudit](MoneyAudit.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 58 | THE DIAL |
| 75 | WHAT A COURSE COSTS, IN TODAY'S MONEY |
| 158 | THE CURVE |
| 201 | STATE |
| 265 | THE MONTH |
| 333 | · · the basic ladder |
| 402 | · · adult study |
| 627 | READING |
| 699 | WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL |
| 770 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 156 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 169 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 172 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 190 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 199 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 817 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `private double tuitionSubsidy` |  |
| 95 | `private final double[] tuition` |  |
| 205 | `private final double[] graduates` |  |
| 206 | `private final double[] licences` |  |
| 207 | `private final double[] coverage` |  |
| 208 | `private final double[] enrolled` |  |
| 210 | `private double tuitionCollected` |  |
| 211 | `private double citySubsidyPaid` |  |
| 212 | `private double payroll` |  |
| 213 | `private double upkeep` |  |
| 223 | `private double finished` | Adults who came out of a course this month, every course counted once: the gross flow out of the student body, where graduates[] is the net movement between bands. |
| 234 | `private final double[] everGraduated` | Everyone the city has ever put through school, by band. |
| 254 | `private final double[][] inFlight` | THE PIPELINE (2026-09-06). |
| 257 | `private final double[] studying` | Adults currently studying full time, by the band they came FROM. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 819 | **type** `public class Education` | Who the city teaches, what it costs, and why anybody bothers. |

### THE DIAL (lines 58-74)

### WHAT A COURSE COSTS, IN TODAY'S MONEY (lines 75-157)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 1 | `{ ... }` |  |
| 99 | 5 | `public void seedConstants(double unit)` |  |
| 106 | 3 | `public double feeFor(EducationType type)` | What this city charges for the course today. |
| 126 | 14 | `public static double foundingTuition(EducationType type)` | Tuition per student per month, before any subsidy. |

### THE CURVE (lines 158-200)

### STATE (lines 201-264)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 5 | `{ ... }` |  |

### THE MONTH (lines 265-626)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 5 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | Teaches everybody who can, will, and can afford to go. |
| 289 | 131 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | month, so the students in flight thin at the same rate as the workforce they came from |
| 422 | 7 | `private void refreshStudying()` | Recounts who is in a lecture theatre, by the band they came from. |
| 431 | 5 | `public double studentBody(EducationType type)` | Everybody part way through this course. |
| 445 | 1 | `public double[] getStudying()` | Adults out of the labour supply this month because they are studying, by the band they hold NOW (the one they enrolled from). |
| 455 | 56 | `private void study(EducationType type, double[] places, double[] workforceByBand, LabourMarket market, PopulationManager people)` | One course, for adults who already have what it asks for. |
| 520 | 5 | `private double participation(EducationType type, LabourMarket market, WageBand from)` | What share of the eligible actually enrol: the return, times the money. |
| 535 | 28 | `private double returnOn(EducationType type, LabourMarket market, WageBand from)` | How much better off somebody is for having done it. |
| 565 | 8 | `private double bandWage(LabourMarket market, WageBand band)` | The best-paid job in a band, which is what a student is aiming at. |
| 575 | 14 | `private double ungatedWage(LabourMarket market, WageBand band)` | The best a graduate can earn WITHOUT a professional licence. |
| 599 | 11 | `private double affordability(EducationType type, LabourMarket market, WageBand from)` | What share of people could pay the un-subsidised part out of a month's pay. |
| 612 | 5 | `private void charge(EducationType type, double students)` | Bills the month's tuition, split between the household and the treasury. |
| 618 | 4 | `private static double cover(double have, double need)` |  |
| 623 | 3 | `private static double clamp(double v)` |  |

### READING (lines 627-698)

| line | len | member | says |
|---:|---:|---|---|
| 635 | 1 | `public double[] getGraduates()` | The month's movement between bands: positive where people arrived, negative where they left. |
| 638 | 1 | `public double[] getLicences()` | People who became able to hold a gated job this month. |
| 640 | 1 | `public double getCoverage(EducationType type)` |  |
| 641 | 1 | `public double getEnrolled(EducationType type)` |  |
| 642 | 1 | `public double getTuitionSubsidy()` |  |
| 644 | 3 | `public void setTuitionSubsidy(double value)` |  |
| 678 | 1 | `public double getGrossCost()` | What leaves the treasury: staff and buildings. |
| 679 | 1 | `public double getPayroll()` |  |
| 680 | 1 | `public double getUpkeep()` |  |
| 683 | 1 | `public double getSubsidy()` | Fees the city waived. |
| 686 | 1 | `public double getFees()` | ...and what comes back from the households. |
| 687 | 1 | `public double getNetCost()` |  |
| 689 | 4 | `public double getCostRecovery()` |  |
| 694 | 1 | `public double[] getEverGraduated()` |  |
| 697 | 1 | `public double getFinished()` | Adults who finished a course this month - the gross flow out of getStudying(). |

### WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL (lines 699-769)

| line | len | member | says |
|---:|---:|---|---|
| 711 | 3 | `public double[] cohortsInFlight(EducationType type)` | Everybody part way through this course, cohort by cohort, nearest first. |
| 716 | 3 | `public double outOfPocket(EducationType type)` | What a household actually pays for a seat, after the subsidy. |
| 721 | 3 | `public double studyReturn(EducationType type, LabourMarket market)` | How much better off somebody is for doing it - 0 means not worth it. |
| 726 | 3 | `public double studyAffordability(EducationType type, LabourMarket market)` | What share of the eligible could pay the un-subsidised part. |
| 731 | 3 | `public double willingShare(EducationType type, LabourMarket market)` | The two above, multiplied and capped: who actually enrols. |
| 741 | 9 | `public double eligibleFor(EducationType type, PopulationManager people)` | The pool this course draws on, before anything else is applied. |
| 757 | 8 | `public EducationType basicBottleneck()` | Which stage of the basic ladder is holding the rest up. |
| 766 | 3 | `public double basicCoverage()` |  |

### SAVE AND RESTORE (lines 770-874)

| line | len | member | says |
|---:|---:|---|---|
| 779 | 36 | `public double[] getState()` |  |
| 825 | 38 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded. |
| 865 | 8 | `public void redenominate(double scale)` | Tuition and this month's bill, in the new unit. |


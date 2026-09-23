# Education.java - 934 lines · 47 methods · 7 constants · model

`ham/citybuildersim/Education.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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
>                    That is the founding calibration, struck against the
>                    wages before the rebalance; against today's the
>                    founding price mostly lets them through. Since
>                    2026-09-21 the price is the player's own dial
>                    (TaxPolicy.getTuitionScale(), default 1, the founding
>                    table), and around three times it the trap is back as
>                    described - see foundingTuition().
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

**Uses:** [EducationType](EducationType.md) (62), [WageBand](WageBand.md) (16), [LabourMarket](LabourMarket.md) (11), [JobType](JobType.md) (5), [PopulationManager](PopulationManager.md) (5), [AgeBand](AgeBand.md) (3), [TaxPolicy](TaxPolicy.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [PayTier](PayTier.md) (1)

**Used by (9):** [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 66 | THE DIAL |
| 83 | WHAT A COURSE COSTS, IN TODAY'S MONEY |
| 218 | THE CURVE |
| 261 | STATE |
| 325 | THE MONTH |
| 393 | · · the basic ladder |
| 462 | · · adult study |
| 687 | READING |
| 759 | WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL |
| 830 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 79 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 216 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 229 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 232 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 250 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 259 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 877 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `private double tuitionSubsidy` |  |
| 113 | `private final double[] tuition` |  |
| 129 | `private double tuitionScale` | The player's multiplier on the tuition table, told to the schools by Game every month from TaxPolicy and after a load - a policy, so it is saved there and not here. |
| 265 | `private final double[] graduates` |  |
| 266 | `private final double[] licences` |  |
| 267 | `private final double[] coverage` |  |
| 268 | `private final double[] enrolled` |  |
| 270 | `private double tuitionCollected` |  |
| 271 | `private double citySubsidyPaid` |  |
| 272 | `private double payroll` |  |
| 273 | `private double upkeep` |  |
| 283 | `private double finished` | Adults who came out of a course this month, every course counted once: the gross flow out of the student body, where graduates[] is the net movement between bands. |
| 294 | `private final double[] everGraduated` | Everyone the city has ever put through school, by band. |
| 314 | `private final double[][] inFlight` | THE PIPELINE (2026-09-06). |
| 317 | `private final double[] studying` | Adults currently studying full time, by the band they came FROM. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 871 | **type** `public class Education` | Who the city teaches, what it costs, and why anybody bothers. |

### THE DIAL (lines 66-82)

### WHAT A COURSE COSTS, IN TODAY'S MONEY (lines 83-217)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 1 | `{ ... }` |  |
| 117 | 5 | `public void seedConstants(double unit)` |  |
| 132 | 3 | `public void setTuitionScale(double scale)` | Sets the multiplier every course fee is charged at this month. |
| 137 | 1 | `public double getTuitionScale()` | The multiplier the tuition table is charged at. |
| 140 | 3 | `public double feeFor(EducationType type)` | What this city charges for the course today: the founding fee in today's money, at the player's scale. |
| 145 | 3 | `public double feeAtOne(EducationType type)` | The same fee at a scale of 1 - the founding table in today's money - so a screen can re-strike it at a staged scale. |
| 154 | 7 | `public double studentBodyTuition()` | What the whole adult student body is charged a month at today's price, before the subsidy: each course's students at its own fee. |
| 186 | 14 | `public static double foundingTuition(EducationType type)` | Tuition per student per month, before any subsidy. |

### THE CURVE (lines 218-260)

### STATE (lines 261-324)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 5 | `{ ... }` |  |

### THE MONTH (lines 325-686)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 5 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | Teaches everybody who can, will, and can afford to go. |
| 349 | 131 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | month, so the students in flight thin at the same rate as the workforce they came from |
| 482 | 7 | `private void refreshStudying()` | Recounts who is in a lecture theatre, by the band they came from. |
| 491 | 5 | `public double studentBody(EducationType type)` | Everybody part way through this course. |
| 505 | 1 | `public double[] getStudying()` | Adults out of the labour supply this month because they are studying, by the band they hold NOW (the one they enrolled from). |
| 515 | 56 | `private void study(EducationType type, double[] places, double[] workforceByBand, LabourMarket market, PopulationManager people)` | One course, for adults who already have what it asks for. |
| 580 | 5 | `private double participation(EducationType type, LabourMarket market, WageBand from)` | What share of the eligible actually enrol: the return, times the money. |
| 595 | 28 | `private double returnOn(EducationType type, LabourMarket market, WageBand from)` | How much better off somebody is for having done it. |
| 625 | 8 | `private double bandWage(LabourMarket market, WageBand band)` | The best-paid job in a band, which is what a student is aiming at. |
| 635 | 14 | `private double ungatedWage(LabourMarket market, WageBand band)` | The best a graduate can earn WITHOUT a professional licence. |
| 659 | 11 | `private double affordability(EducationType type, LabourMarket market, WageBand from)` | What share of people could pay the un-subsidised part out of a month's pay. |
| 672 | 5 | `private void charge(EducationType type, double students)` | Bills the month's tuition, split between the household and the treasury. |
| 678 | 4 | `private static double cover(double have, double need)` |  |
| 683 | 3 | `private static double clamp(double v)` |  |

### READING (lines 687-758)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 1 | `public double[] getGraduates()` | The month's movement between bands: positive where people arrived, negative where they left. |
| 698 | 1 | `public double[] getLicences()` | People who became able to hold a gated job this month. |
| 700 | 1 | `public double getCoverage(EducationType type)` |  |
| 701 | 1 | `public double getEnrolled(EducationType type)` |  |
| 702 | 1 | `public double getTuitionSubsidy()` |  |
| 704 | 3 | `public void setTuitionSubsidy(double value)` |  |
| 738 | 1 | `public double getGrossCost()` | What leaves the treasury: staff and buildings. |
| 739 | 1 | `public double getPayroll()` |  |
| 740 | 1 | `public double getUpkeep()` |  |
| 743 | 1 | `public double getSubsidy()` | Fees the city waived. |
| 746 | 1 | `public double getFees()` | ...and what comes back from the households. |
| 747 | 1 | `public double getNetCost()` |  |
| 749 | 4 | `public double getCostRecovery()` |  |
| 754 | 1 | `public double[] getEverGraduated()` |  |
| 757 | 1 | `public double getFinished()` | Adults who finished a course this month - the gross flow out of getStudying(). |

### WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL (lines 759-829)

| line | len | member | says |
|---:|---:|---|---|
| 771 | 3 | `public double[] cohortsInFlight(EducationType type)` | Everybody part way through this course, cohort by cohort, nearest first. |
| 776 | 3 | `public double outOfPocket(EducationType type)` | What a household actually pays for a seat, after the subsidy. |
| 781 | 3 | `public double studyReturn(EducationType type, LabourMarket market)` | How much better off somebody is for doing it - 0 means not worth it. |
| 786 | 3 | `public double studyAffordability(EducationType type, LabourMarket market)` | What share of the eligible could pay the un-subsidised part. |
| 791 | 3 | `public double willingShare(EducationType type, LabourMarket market)` | The two above, multiplied and capped: who actually enrols. |
| 801 | 9 | `public double eligibleFor(EducationType type, PopulationManager people)` | The pool this course draws on, before anything else is applied. |
| 817 | 8 | `public EducationType basicBottleneck()` | Which stage of the basic ladder is holding the rest up. |
| 826 | 3 | `public double basicCoverage()` |  |

### SAVE AND RESTORE (lines 830-934)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 36 | `public double[] getState()` |  |
| 885 | 38 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded. |
| 925 | 8 | `public void redenominate(double scale)` | Tuition and this month's bill, in the new unit. |


# Education.java - 1,093 lines · 59 methods · 7 constants · model

`ham/citybuildersim/Education.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [EducationType](EducationType.md) (76), [WageBand](WageBand.md) (17), [LabourMarket](LabourMarket.md) (12), [DecisionLog](DecisionLog.md) (5), [JobType](JobType.md) (5), [PopulationManager](PopulationManager.md) (5), [AgeBand](AgeBand.md) (4), [TaxPolicy](TaxPolicy.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [PayTier](PayTier.md) (1)

**Used by (12):** [BuildAdvice](BuildAdvice.md), [CityNeeds](CityNeeds.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 76 | THE DIAL |
| 93 | WHAT A COURSE COSTS, IN TODAY'S MONEY |
| 253 | THE CURVE |
| 296 | STATE |
| 385 | THE MONTH |
| 456 | · · the basic ladder |
| 524 | · · adult study |
| 768 | READING |
| 900 | WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL |
| 971 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 251 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 264 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 267 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 285 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 294 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 1023 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private transient DecisionLog decisions` | Where the player's change to the city's share of tuition is written (DecisionLog, 0.7.23); null for one no city holds. |
| 91 | `private double tuitionSubsidy` |  |
| 136 | `private final double[] tuition` |  |
| 152 | `private final double[] tuitionScales` | The player's multiplier on the tuition table, one per school kind by EducationType ordinal (0.7.6), told to the schools by Game every month from TaxPolicy and after a load - a policy, so it is saved there and not here. |
| 300 | `private final double[] graduates` |  |
| 301 | `private final double[] licences` |  |
| 302 | `private final double[] coverage` |  |
| 303 | `private final double[] enrolled` |  |
| 305 | `private double tuitionCollected` |  |
| 306 | `private double citySubsidyPaid` |  |
| 307 | `private double payroll` |  |
| 308 | `private double upkeep` |  |
| 320 | `private final double[] feesOf` | THE MONTH BY SCHOOL KIND (0.7.6), for the Schools page's row per kind: the tuition collected from each kind's students - tuitionCollected, split by what was taught, added to by the same line in charge() - and what eac... |
| 321 | `private final double[] costOf` |  |
| 331 | `private double finished` | Adults who came out of a course this month, every course counted once: the gross flow out of the student body, where graduates[] is the net movement between bands. |
| 343 | `private double diplomasThisMonth` | The diplomas the school leavers took this month, gross (0.7.28): the first half of the DIPLOMA movement in graduates[], before the diploma-holders who finished college or university are taken off it (both take diploma... |
| 354 | `private final double[] everGraduated` | Everyone the city has ever put through school, by band. |
| 374 | `private final double[][] inFlight` | THE PIPELINE (2026-09-06). |
| 377 | `private final double[] studying` | Adults currently studying full time, by the band they came FROM. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 1030 | **type** `public class Education` | Who the city teaches, what it costs, and why anybody bothers. |
| 70 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 72 | 3 | `private void decided(String kind, String label)` |  |

### THE DIAL (lines 76-92)

### WHAT A COURSE COSTS, IN TODAY'S MONEY (lines 93-252)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 1 | `{ ... }` |  |
| 140 | 5 | `public void seedConstants(double unit)` |  |
| 153 | 1 | `{ ... }` |  |
| 156 | 3 | `public void setTuitionScale(double scale)` | Sets the multiplier every course fee is charged at this month, every kind at once. |
| 161 | 4 | `public void setTuitionScaleOf(EducationType type, double scale)` | Sets one kind's multiplier (0.7.6). |
| 167 | 1 | `public double getTuitionScale()` | The multiplier the tuition table is charged at: the first kind's, which is every kind's until they part (0.7.6). |
| 170 | 3 | `public double getTuitionScaleOf(EducationType type)` | One kind's multiplier (0.7.6). |
| 175 | 3 | `public double feeFor(EducationType type)` | What this city charges for the course today: the founding fee in today's money, at the player's scale for that kind. |
| 180 | 3 | `public double feeAtOne(EducationType type)` | The same fee at a scale of 1 - the founding table in today's money - so a screen can re-strike it at a staged scale. |
| 189 | 7 | `public double studentBodyTuition()` | What the whole adult student body is charged a month at today's price, before the subsidy: each course's students at its own fee. |
| 221 | 14 | `public static double foundingTuition(EducationType type)` | Tuition per student per month, before any subsidy. |

### THE CURVE (lines 253-295)

### STATE (lines 296-384)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 5 | `{ ... }` |  |

### THE MONTH (lines 385-767)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 5 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | Teaches everybody who can, will, and can afford to go. |
| 409 | 133 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | month, so the students in flight thin at the same rate as the workforce they came from |
| 544 | 4 | `static double diplomas(double teens, double basic, double afford)` | The school leavers' diplomas a month: the teens ageing out, at the ladder's coverage, who could afford the high school's fees. |
| 556 | 4 | `public double schoolLeavers(PopulationCohorts pyramid, LabourMarket market)` | The diplomas a month the high schools hand out at the ladder's saved coverage (0.7.51): advanceMonth()'s arithmetic (diplomas()) on these teens and these wages - on saved state, where getNewDiplomas() is this month's ... |
| 562 | 7 | `private void refreshStudying()` | Recounts who is in a lecture theatre, by the band they came from. |
| 571 | 5 | `public double studentBody(EducationType type)` | Everybody part way through this course. |
| 585 | 1 | `public double[] getStudying()` | Adults out of the labour supply this month because they are studying, by the band they hold NOW (the one they enrolled from). |
| 595 | 56 | `private void study(EducationType type, double[] places, double[] workforceByBand, LabourMarket market, PopulationManager people)` | One course, for adults who already have what it asks for. |
| 660 | 5 | `private double participation(EducationType type, LabourMarket market, WageBand from)` | What share of the eligible actually enrol: the return, times the money. |
| 675 | 28 | `private double returnOn(EducationType type, LabourMarket market, WageBand from)` | How much better off somebody is for having done it. |
| 705 | 8 | `private double bandWage(LabourMarket market, WageBand band)` | The best-paid job in a band, which is what a student is aiming at. |
| 715 | 14 | `private double ungatedWage(LabourMarket market, WageBand band)` | The best a graduate can earn WITHOUT a professional licence. |
| 739 | 11 | `private double affordability(EducationType type, LabourMarket market, WageBand from)` | What share of people could pay the un-subsidised part out of a month's pay. |
| 752 | 6 | `private void charge(EducationType type, double students)` | Bills the month's tuition, split between the household and the treasury. |
| 759 | 4 | `private static double cover(double have, double need)` |  |
| 764 | 3 | `private static double clamp(double v)` |  |

### READING (lines 768-899)

| line | len | member | says |
|---:|---:|---|---|
| 776 | 1 | `public double[] getGraduates()` | The month's movement between bands: positive where people arrived, negative where they left. |
| 785 | 5 | `public double gainedThisMonth()` | People who gained a qualification this month, at every level (A6, 0.7.46): the positive half of getGraduates(), the sum everGraduated accrues. |
| 792 | 1 | `public double[] getLicences()` | People who became able to hold a gated job this month. |
| 795 | 1 | `public double getNewDiplomas()` | The school leavers' diplomas this month, gross (0.7.28) - see diplomasThisMonth; NaN until a month has run since the founding or a load. |
| 797 | 1 | `public double getCoverage(EducationType type)` |  |
| 798 | 1 | `public double getEnrolled(EducationType type)` |  |
| 799 | 1 | `public double getTuitionSubsidy()` |  |
| 801 | 7 | `public void setTuitionSubsidy(double value)` |  |
| 839 | 1 | `public double getGrossCost()` | What leaves the treasury: staff and buildings. |
| 840 | 1 | `public double getPayroll()` |  |
| 841 | 1 | `public double getUpkeep()` |  |
| 844 | 1 | `public double getSubsidy()` | Fees the city waived. |
| 847 | 1 | `public double getFees()` | ...and what comes back from the households. |
| 848 | 1 | `public double getNetCost()` |  |
| 850 | 4 | `public double getCostRecovery()` |  |
| 861 | 1 | `public double getCostOf(EducationType type)` | What one kind's standing buildings cost the treasury this month: staffed payroll plus upkeep (0.7.6). |
| 864 | 1 | `public double getFeesOf(EducationType type)` | The tuition households paid for one kind this month (0.7.6); across the kinds, getFees(). |
| 873 | 4 | `public void setCostOf(EducationType type, double payroll, double upkeep)` | Hands in one kind's cost for the month: its staffed payroll plus its upkeep, off BuildingManager.getSchoolPayroll() and getSchoolUpkeep(). |
| 886 | 8 | `public double billedAt(java.util.function.ToDoubleFunction<EducationType> scaleOf)` | What this month's students would be billed at another set of scales, before the subsidy (0.7.6): each kind's enrolled at the founding fee in today's money times the scale asked for it. |
| 895 | 1 | `public double[] getEverGraduated()` |  |
| 898 | 1 | `public double getFinished()` | Adults who finished a course this month - the gross flow out of getStudying(). |

### WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL (lines 900-970)

| line | len | member | says |
|---:|---:|---|---|
| 912 | 3 | `public double[] cohortsInFlight(EducationType type)` | Everybody part way through this course, cohort by cohort, nearest first. |
| 917 | 3 | `public double outOfPocket(EducationType type)` | What a household actually pays for a seat, after the subsidy. |
| 922 | 3 | `public double studyReturn(EducationType type, LabourMarket market)` | How much better off somebody is for doing it - 0 means not worth it. |
| 927 | 3 | `public double studyAffordability(EducationType type, LabourMarket market)` | What share of the eligible could pay the un-subsidised part. |
| 932 | 3 | `public double willingShare(EducationType type, LabourMarket market)` | The two above, multiplied and capped: who actually enrols. |
| 942 | 9 | `public double eligibleFor(EducationType type, PopulationManager people)` | The pool this course draws on, before anything else is applied. |
| 958 | 8 | `public EducationType basicBottleneck()` | Which stage of the basic ladder is holding the rest up. |
| 967 | 3 | `public double basicCoverage()` |  |

### SAVE AND RESTORE (lines 971-1093)

| line | len | member | says |
|---:|---:|---|---|
| 980 | 41 | `public double[] getState()` |  |
| 1031 | 49 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded. |
| 1082 | 10 | `public void redenominate(double scale)` | Tuition and this month's bill, in the new unit. |


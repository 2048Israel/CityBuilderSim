# Education.java - 1,058 lines · 56 methods · 7 constants · model

`ham/citybuildersim/Education.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [EducationType](EducationType.md) (75), [WageBand](WageBand.md) (16), [LabourMarket](LabourMarket.md) (11), [DecisionLog](DecisionLog.md) (5), [JobType](JobType.md) (5), [PopulationManager](PopulationManager.md) (5), [TaxPolicy](TaxPolicy.md) (3), [AgeBand](AgeBand.md) (3), [PopulationCohorts](PopulationCohorts.md) (2), [PayTier](PayTier.md) (1)

**Used by (11):** [BuildAdvice](BuildAdvice.md), [CityNeeds](CityNeeds.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 76 | THE DIAL |
| 93 | WHAT A COURSE COSTS, IN TODAY'S MONEY |
| 247 | THE CURVE |
| 290 | STATE |
| 379 | THE MONTH |
| 450 | · · the basic ladder |
| 520 | · · adult study |
| 746 | READING |
| 865 | WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL |
| 936 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 245 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 258 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 261 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 279 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 288 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 988 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private transient DecisionLog decisions` | Where the player's change to the city's share of tuition is written (DecisionLog, 0.7.23); null for one no city holds. |
| 91 | `private double tuitionSubsidy` |  |
| 130 | `private final double[] tuition` |  |
| 146 | `private final double[] tuitionScales` | The player's multiplier on the tuition table, one per school kind by EducationType ordinal (0.7.6), told to the schools by Game every month from TaxPolicy and after a load - a policy, so it is saved there and not here. |
| 294 | `private final double[] graduates` |  |
| 295 | `private final double[] licences` |  |
| 296 | `private final double[] coverage` |  |
| 297 | `private final double[] enrolled` |  |
| 299 | `private double tuitionCollected` |  |
| 300 | `private double citySubsidyPaid` |  |
| 301 | `private double payroll` |  |
| 302 | `private double upkeep` |  |
| 314 | `private final double[] feesOf` | THE MONTH BY SCHOOL KIND (0.7.6), for the Schools page's row per kind: the tuition collected from each kind's students - tuitionCollected, split by what was taught, added to by the same line in charge() - and what eac... |
| 315 | `private final double[] costOf` |  |
| 325 | `private double finished` | Adults who came out of a course this month, every course counted once: the gross flow out of the student body, where graduates[] is the net movement between bands. |
| 337 | `private double diplomasThisMonth` | The diplomas the school leavers took this month, gross (0.7.28): the first half of the DIPLOMA movement in graduates[], before the diploma-holders who finished college or university are taken off it (both take diploma... |
| 348 | `private final double[] everGraduated` | Everyone the city has ever put through school, by band. |
| 368 | `private final double[][] inFlight` | THE PIPELINE (2026-09-06). |
| 371 | `private final double[] studying` | Adults currently studying full time, by the band they came FROM. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 995 | **type** `public class Education` | Who the city teaches, what it costs, and why anybody bothers. |
| 70 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 72 | 3 | `private void decided(String kind, String label)` |  |

### THE DIAL (lines 76-92)

### WHAT A COURSE COSTS, IN TODAY'S MONEY (lines 93-246)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 1 | `{ ... }` |  |
| 134 | 5 | `public void seedConstants(double unit)` |  |
| 147 | 1 | `{ ... }` |  |
| 150 | 3 | `public void setTuitionScale(double scale)` | Sets the multiplier every course fee is charged at this month, every kind at once. |
| 155 | 4 | `public void setTuitionScaleOf(EducationType type, double scale)` | Sets one kind's multiplier (0.7.6). |
| 161 | 1 | `public double getTuitionScale()` | The multiplier the tuition table is charged at: the first kind's, which is every kind's until they part (0.7.6). |
| 164 | 3 | `public double getTuitionScaleOf(EducationType type)` | One kind's multiplier (0.7.6). |
| 169 | 3 | `public double feeFor(EducationType type)` | What this city charges for the course today: the founding fee in today's money, at the player's scale for that kind. |
| 174 | 3 | `public double feeAtOne(EducationType type)` | The same fee at a scale of 1 - the founding table in today's money - so a screen can re-strike it at a staged scale. |
| 183 | 7 | `public double studentBodyTuition()` | What the whole adult student body is charged a month at today's price, before the subsidy: each course's students at its own fee. |
| 215 | 14 | `public static double foundingTuition(EducationType type)` | Tuition per student per month, before any subsidy. |

### THE CURVE (lines 247-289)

### STATE (lines 290-378)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 5 | `{ ... }` |  |

### THE MONTH (lines 379-745)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 5 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | Teaches everybody who can, will, and can afford to go. |
| 403 | 135 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | month, so the students in flight thin at the same rate as the workforce they came from |
| 540 | 7 | `private void refreshStudying()` | Recounts who is in a lecture theatre, by the band they came from. |
| 549 | 5 | `public double studentBody(EducationType type)` | Everybody part way through this course. |
| 563 | 1 | `public double[] getStudying()` | Adults out of the labour supply this month because they are studying, by the band they hold NOW (the one they enrolled from). |
| 573 | 56 | `private void study(EducationType type, double[] places, double[] workforceByBand, LabourMarket market, PopulationManager people)` | One course, for adults who already have what it asks for. |
| 638 | 5 | `private double participation(EducationType type, LabourMarket market, WageBand from)` | What share of the eligible actually enrol: the return, times the money. |
| 653 | 28 | `private double returnOn(EducationType type, LabourMarket market, WageBand from)` | How much better off somebody is for having done it. |
| 683 | 8 | `private double bandWage(LabourMarket market, WageBand band)` | The best-paid job in a band, which is what a student is aiming at. |
| 693 | 14 | `private double ungatedWage(LabourMarket market, WageBand band)` | The best a graduate can earn WITHOUT a professional licence. |
| 717 | 11 | `private double affordability(EducationType type, LabourMarket market, WageBand from)` | What share of people could pay the un-subsidised part out of a month's pay. |
| 730 | 6 | `private void charge(EducationType type, double students)` | Bills the month's tuition, split between the household and the treasury. |
| 737 | 4 | `private static double cover(double have, double need)` |  |
| 742 | 3 | `private static double clamp(double v)` |  |

### READING (lines 746-864)

| line | len | member | says |
|---:|---:|---|---|
| 754 | 1 | `public double[] getGraduates()` | The month's movement between bands: positive where people arrived, negative where they left. |
| 757 | 1 | `public double[] getLicences()` | People who became able to hold a gated job this month. |
| 760 | 1 | `public double getNewDiplomas()` | The school leavers' diplomas this month, gross (0.7.28) - see diplomasThisMonth; NaN until a month has run since the founding or a load. |
| 762 | 1 | `public double getCoverage(EducationType type)` |  |
| 763 | 1 | `public double getEnrolled(EducationType type)` |  |
| 764 | 1 | `public double getTuitionSubsidy()` |  |
| 766 | 7 | `public void setTuitionSubsidy(double value)` |  |
| 804 | 1 | `public double getGrossCost()` | What leaves the treasury: staff and buildings. |
| 805 | 1 | `public double getPayroll()` |  |
| 806 | 1 | `public double getUpkeep()` |  |
| 809 | 1 | `public double getSubsidy()` | Fees the city waived. |
| 812 | 1 | `public double getFees()` | ...and what comes back from the households. |
| 813 | 1 | `public double getNetCost()` |  |
| 815 | 4 | `public double getCostRecovery()` |  |
| 826 | 1 | `public double getCostOf(EducationType type)` | What one kind's standing buildings cost the treasury this month: staffed payroll plus upkeep (0.7.6). |
| 829 | 1 | `public double getFeesOf(EducationType type)` | The tuition households paid for one kind this month (0.7.6); across the kinds, getFees(). |
| 838 | 4 | `public void setCostOf(EducationType type, double payroll, double upkeep)` | Hands in one kind's cost for the month: its staffed payroll plus its upkeep, off BuildingManager.getSchoolPayroll() and getSchoolUpkeep(). |
| 851 | 8 | `public double billedAt(java.util.function.ToDoubleFunction<EducationType> scaleOf)` | What this month's students would be billed at another set of scales, before the subsidy (0.7.6): each kind's enrolled at the founding fee in today's money times the scale asked for it. |
| 860 | 1 | `public double[] getEverGraduated()` |  |
| 863 | 1 | `public double getFinished()` | Adults who finished a course this month - the gross flow out of getStudying(). |

### WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL (lines 865-935)

| line | len | member | says |
|---:|---:|---|---|
| 877 | 3 | `public double[] cohortsInFlight(EducationType type)` | Everybody part way through this course, cohort by cohort, nearest first. |
| 882 | 3 | `public double outOfPocket(EducationType type)` | What a household actually pays for a seat, after the subsidy. |
| 887 | 3 | `public double studyReturn(EducationType type, LabourMarket market)` | How much better off somebody is for doing it - 0 means not worth it. |
| 892 | 3 | `public double studyAffordability(EducationType type, LabourMarket market)` | What share of the eligible could pay the un-subsidised part. |
| 897 | 3 | `public double willingShare(EducationType type, LabourMarket market)` | The two above, multiplied and capped: who actually enrols. |
| 907 | 9 | `public double eligibleFor(EducationType type, PopulationManager people)` | The pool this course draws on, before anything else is applied. |
| 923 | 8 | `public EducationType basicBottleneck()` | Which stage of the basic ladder is holding the rest up. |
| 932 | 3 | `public double basicCoverage()` |  |

### SAVE AND RESTORE (lines 936-1058)

| line | len | member | says |
|---:|---:|---|---|
| 945 | 41 | `public double[] getState()` |  |
| 996 | 49 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded. |
| 1047 | 10 | `public void redenominate(double scale)` | Tuition and this month's bill, in the new unit. |


# Education.java - 1,098 lines · 60 methods · 7 constants · model

`ham/citybuildersim/Education.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [EducationType](EducationType.md) (77), [WageBand](WageBand.md) (17), [LabourMarket](LabourMarket.md) (12), [DecisionLog](DecisionLog.md) (5), [JobType](JobType.md) (5), [PopulationManager](PopulationManager.md) (5), [AgeBand](AgeBand.md) (4), [TaxPolicy](TaxPolicy.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [PriceLevel](PriceLevel.md) (1), [PayTier](PayTier.md) (1)

**Used by (13):** [BuildAdvice](BuildAdvice.md), [CityNeeds](CityNeeds.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [PolicyScreen](PolicyScreen.md), [PriceLevelCheck](PriceLevelCheck.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 76 | THE DIAL |
| 93 | WHAT A COURSE COSTS, IN TODAY'S MONEY |
| 258 | THE CURVE |
| 301 | STATE |
| 390 | THE MONTH |
| 461 | · · the basic ladder |
| 529 | · · adult study |
| 773 | READING |
| 905 | WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL |
| 976 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `Education.DEFAULT_SUBSIDY` | `.60` | What share of tuition the city pays. |
| 256 | `Education.MAX_BURDEN` | `.60` | The share of a month's wage above which nobody enrols. |
| 269 | `Education.RETURN_ELASTICITY` | `1.3` | How hard the pay gap pulls people into a classroom. |
| 272 | `Education.MAX_PARTICIPATION` | `.90` | However good the return, this share of the eligible is the most that go. |
| 290 | `Education.ENROLMENT_RATE` | `1 / 60.0` | What fraction of the willing eligible pool starts a course in any month. |
| 299 | `Education.ELEMENTARY_SHARE` | `4 / 7.0` | Ages 6-10 out of the CHILD band's 6-13. |
| 1028 | `Education.MONTH_FIELDS` | `4` | Scalars appended to the state array on 2026-09-09. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private transient DecisionLog decisions` | Where the player's change to the city's share of tuition is written (DecisionLog, 0.7.23); null for one no city holds. |
| 91 | `private double tuitionSubsidy` |  |
| 136 | `private final double[] tuition` |  |
| 152 | `private final double[] tuitionScales` | The player's multiplier on the tuition table, one per school kind by EducationType ordinal (0.7.6), told to the schools by Game every month from TaxPolicy and after a load - a policy, so it is saved there and not here. |
| 305 | `private final double[] graduates` |  |
| 306 | `private final double[] licences` |  |
| 307 | `private final double[] coverage` |  |
| 308 | `private final double[] enrolled` |  |
| 310 | `private double tuitionCollected` |  |
| 311 | `private double citySubsidyPaid` |  |
| 312 | `private double payroll` |  |
| 313 | `private double upkeep` |  |
| 325 | `private final double[] feesOf` | THE MONTH BY SCHOOL KIND (0.7.6), for the Schools page's row per kind: the tuition collected from each kind's students - tuitionCollected, split by what was taught, added to by the same line in charge() - and what eac... |
| 326 | `private final double[] costOf` |  |
| 336 | `private double finished` | Adults who came out of a course this month, every course counted once: the gross flow out of the student body, where graduates[] is the net movement between bands. |
| 348 | `private double diplomasThisMonth` | The diplomas the school leavers took this month, gross (0.7.28): the first half of the DIPLOMA movement in graduates[], before the diploma-holders who finished college or university are taken off it (both take diploma... |
| 359 | `private final double[] everGraduated` | Everyone the city has ever put through school, by band. |
| 379 | `private final double[][] inFlight` | THE PIPELINE (2026-09-06). |
| 382 | `private final double[] studying` | Adults currently studying full time, by the band they came FROM. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 1035 | **type** `public class Education` | Who the city teaches, what it costs, and why anybody bothers. |
| 70 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 72 | 3 | `private void decided(String kind, String label)` |  |

### THE DIAL (lines 76-92)

### WHAT A COURSE COSTS, IN TODAY'S MONEY (lines 93-257)

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
| 221 | 3 | `public static double foundingTuition(EducationType type)` | Tuition per student per month, before any subsidy. |
| 226 | 14 | `static double todaysTuition(EducationType type)` | The table as it is written, founding thousands a month at today's (2025's) price level; foundingTuition() prices it at the founding's (times PriceLevel.FACTOR, EP1). |

### THE CURVE (lines 258-300)

### STATE (lines 301-389)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 5 | `{ ... }` |  |

### THE MONTH (lines 390-772)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 5 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | Teaches everybody who can, will, and can afford to go. |
| 414 | 133 | `public void advanceMonth(double[] places, PopulationCohorts pyramid, PopulationManager people, LabourMarket market, double staf...` | month, so the students in flight thin at the same rate as the workforce they came from |
| 549 | 4 | `static double diplomas(double teens, double basic, double afford)` | The school leavers' diplomas a month: the teens ageing out, at the ladder's coverage, who could afford the high school's fees. |
| 561 | 4 | `public double schoolLeavers(PopulationCohorts pyramid, LabourMarket market)` | The diplomas a month the high schools hand out at the ladder's saved coverage (0.7.51): advanceMonth()'s arithmetic (diplomas()) on these teens and these wages - on saved state, where getNewDiplomas() is this month's ... |
| 567 | 7 | `private void refreshStudying()` | Recounts who is in a lecture theatre, by the band they came from. |
| 576 | 5 | `public double studentBody(EducationType type)` | Everybody part way through this course. |
| 590 | 1 | `public double[] getStudying()` | Adults out of the labour supply this month because they are studying, by the band they hold NOW (the one they enrolled from). |
| 600 | 56 | `private void study(EducationType type, double[] places, double[] workforceByBand, LabourMarket market, PopulationManager people)` | One course, for adults who already have what it asks for. |
| 665 | 5 | `private double participation(EducationType type, LabourMarket market, WageBand from)` | What share of the eligible actually enrol: the return, times the money. |
| 680 | 28 | `private double returnOn(EducationType type, LabourMarket market, WageBand from)` | How much better off somebody is for having done it. |
| 710 | 8 | `private double bandWage(LabourMarket market, WageBand band)` | The best-paid job in a band, which is what a student is aiming at. |
| 720 | 14 | `private double ungatedWage(LabourMarket market, WageBand band)` | The best a graduate can earn WITHOUT a professional licence. |
| 744 | 11 | `private double affordability(EducationType type, LabourMarket market, WageBand from)` | What share of people could pay the un-subsidised part out of a month's pay. |
| 757 | 6 | `private void charge(EducationType type, double students)` | Bills the month's tuition, split between the household and the treasury. |
| 764 | 4 | `private static double cover(double have, double need)` |  |
| 769 | 3 | `private static double clamp(double v)` |  |

### READING (lines 773-904)

| line | len | member | says |
|---:|---:|---|---|
| 781 | 1 | `public double[] getGraduates()` | The month's movement between bands: positive where people arrived, negative where they left. |
| 790 | 5 | `public double gainedThisMonth()` | People who gained a qualification this month, at every level (A6, 0.7.46): the positive half of getGraduates(), the sum everGraduated accrues. |
| 797 | 1 | `public double[] getLicences()` | People who became able to hold a gated job this month. |
| 800 | 1 | `public double getNewDiplomas()` | The school leavers' diplomas this month, gross (0.7.28) - see diplomasThisMonth; NaN until a month has run since the founding or a load. |
| 802 | 1 | `public double getCoverage(EducationType type)` |  |
| 803 | 1 | `public double getEnrolled(EducationType type)` |  |
| 804 | 1 | `public double getTuitionSubsidy()` |  |
| 806 | 7 | `public void setTuitionSubsidy(double value)` |  |
| 844 | 1 | `public double getGrossCost()` | What leaves the treasury: staff and buildings. |
| 845 | 1 | `public double getPayroll()` |  |
| 846 | 1 | `public double getUpkeep()` |  |
| 849 | 1 | `public double getSubsidy()` | Fees the city waived. |
| 852 | 1 | `public double getFees()` | ...and what comes back from the households. |
| 853 | 1 | `public double getNetCost()` |  |
| 855 | 4 | `public double getCostRecovery()` |  |
| 866 | 1 | `public double getCostOf(EducationType type)` | What one kind's standing buildings cost the treasury this month: staffed payroll plus upkeep (0.7.6). |
| 869 | 1 | `public double getFeesOf(EducationType type)` | The tuition households paid for one kind this month (0.7.6); across the kinds, getFees(). |
| 878 | 4 | `public void setCostOf(EducationType type, double payroll, double upkeep)` | Hands in one kind's cost for the month: its staffed payroll plus its upkeep, off BuildingManager.getSchoolPayroll() and getSchoolUpkeep(). |
| 891 | 8 | `public double billedAt(java.util.function.ToDoubleFunction<EducationType> scaleOf)` | What this month's students would be billed at another set of scales, before the subsidy (0.7.6): each kind's enrolled at the founding fee in today's money times the scale asked for it. |
| 900 | 1 | `public double[] getEverGraduated()` |  |
| 903 | 1 | `public double getFinished()` | Adults who finished a course this month - the gross flow out of getStudying(). |

### WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL (lines 905-975)

| line | len | member | says |
|---:|---:|---|---|
| 917 | 3 | `public double[] cohortsInFlight(EducationType type)` | Everybody part way through this course, cohort by cohort, nearest first. |
| 922 | 3 | `public double outOfPocket(EducationType type)` | What a household actually pays for a seat, after the subsidy. |
| 927 | 3 | `public double studyReturn(EducationType type, LabourMarket market)` | How much better off somebody is for doing it - 0 means not worth it. |
| 932 | 3 | `public double studyAffordability(EducationType type, LabourMarket market)` | What share of the eligible could pay the un-subsidised part. |
| 937 | 3 | `public double willingShare(EducationType type, LabourMarket market)` | The two above, multiplied and capped: who actually enrols. |
| 947 | 9 | `public double eligibleFor(EducationType type, PopulationManager people)` | The pool this course draws on, before anything else is applied. |
| 963 | 8 | `public EducationType basicBottleneck()` | Which stage of the basic ladder is holding the rest up. |
| 972 | 3 | `public double basicCoverage()` |  |

### SAVE AND RESTORE (lines 976-1098)

| line | len | member | says |
|---:|---:|---|---|
| 985 | 41 | `public double[] getState()` |  |
| 1036 | 49 | `public void restore(double[] saved)` | Refused whole on a length mismatch, never padded. |
| 1087 | 10 | `public void redenominate(double scale)` | Tuition and this month's bill, in the new unit. |


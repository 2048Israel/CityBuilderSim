# Unemployment.java - 587 lines · 38 methods · 8 constants · model

`ham/citybuildersim/Unemployment.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The people out of work: how many, who they were, what Employment Insurance
> pays them, and who has lost their home.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Jerus, 2026-09-11: "we are to add a new household structure called
> unemployed, these are different, these will just sum up by age the
> unemployed, or unhoused, cause i think we are missing those categories...
> and they will have their own cashflow and stuff." Until now unemployment
> was one number, workforce less the filled posts, and the people behind it
> lived in family households at the tier mix of the filled jobs, sharing a
> wage they did not earn.
> 
> The families are built from the adults who work now (FamilyModel), and
> the rest have books of their own (UnemployedHousehold). This class is what
> those books need that nothing else knows: who is on EI and at what wage,
> who has run out of it, and who has been evicted.
> 
> ==================== THE POOL, AND THE FLOWS THROUGH IT ====================
> 
> The SIZE of the pool is not decided here. It is the labour market's:
> everyone in the labour force less the posts filled (PopulationManager
> .getUnemployed()). What this class decides is who is IN it, from the flows
> between one month and the next - Jerus's answers, each one:
> 
>   jobs lost      only posts that DISAPPEARED - "net losses only", no churn.
>                  The fall in filled posts, but no more than the fall in
>                  posts: somebody who left the city left a post empty, and
>                  nobody lost a job.
>   openings       the rise in filled posts, filled PRO RATA between the pool
>                  and last month's arrivals, who joined the workforce this
>                  month. Jerus: "pro rata".
>   arrivals       an arrival who does not get a post goes on EI "same as
>   not hired      locals", at the unskilled rate.
>   exits          hires, deaths, turning seventy, and those who left the city
>                  broke - drawn from every group in proportion.
>   the rest       whatever the stock says the pool is that the flows above
>                  do not explain - a graduate with no post, a teenager
>                  turning eighteen - joins it WITHOUT EI: they never lost a
>                  job. Or, the other way, leaves it.
> 
> ==================== EI, ON THE INFLOW ====================
> 
> Jerus: "for ei, use inflow of unemployed to get the rolling avg EI." A ring
> of twelve monthly cohorts, each the month's inflow and the insured wage it
> came from. Each month every cohort shrinks by the pool's exits, the oldest
> drops off EI, and the month's inflow starts a new one. What EI pays is the
> ring: every claimant, 55% of their insured wage, capped. The individual is
> never tracked; the cohort is.
> 
> The numbers are the real 2026 ones (ESDC; Canada.ca): a 1.63% premium off
> every wage, 55% of insurable earnings, insurable earnings capped at $68,900
> a year - 1.66 times the unskilled wage the game's ladder is anchored on,
> held as that multiple so it moves with wages and with a currency reform.
> Twelve months, where the real thing runs 14-45 weeks: Jerus's call.
> 
> ==================== EVICTION ====================
> 
> Jerus: when EI is over and savings and credit are gone, "a share leaves"
> and the rest are unhoused. HouseholdBalance strikes who could not pay; this
> ... (3 more lines in the source)

**Uses:** [PayTier](PayTier.md) (4), [Crime](Crime.md) (3), [AgeBand](AgeBand.md) (1)

**Used by (9):** [DeathRecordCheck](DeathRecordCheck.md), [Game](Game.md), [Health](Health.md), [HistorySave](HistorySave.md), [LongPlaytest](LongPlaytest.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [TaxPolicy](TaxPolicy.md)

## Sections

| line | section |
|---:|---|
| 70 | · the dials |
| 103 | · the stocks |
| 129 | · the month |
| 137 | · reading |
| 199 | · the month |
| 425 | HEALTH: the unhoused, as a share of the adult band |
| 519 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Unemployment.EI_MONTHS` | `12` | Months a claim lasts. |
| 76 | `Unemployment.DEFAULT_PREMIUM_RATE` | `.0163` | The employee premium, 2026: $1.63 per $100 of insurable earnings. |
| 79 | `Unemployment.DEFAULT_BENEFIT_RATE` | `.55` | What EI replaces: 55% of insurable earnings. |
| 82 | `Unemployment.MAX_INSURABLE_MULTIPLE` | `68_900.0 / 12 / 3_460` | Maximum insurable earnings, 2026: $68,900 a year, over the $3,460 unskilled median the ladder is anchored on. |
| 89 | `Unemployment.LEAVE_WHEN_BROKE` | `.25` | The share of the evicted who leave the city rather than stay on the street. |
| 98 | `Unemployment.UNHOUSED_MORTALITY` | `3.7` | How much faster the unhoused die: 3.7x. |
| 101 | `Unemployment.UNHOUSED_SICKNESS` | `3.7` | ...and how much faster they get sick. |
| 521 | `Unemployment.STATE_LENGTH` | `EI_MONTHS * 2 + 6 + PayTier.values().length * 2 + 12` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 106 | `private final double[] cohortPeople` | Claimants by the month their claim began, newest first. |
| 109 | `private final double[] cohortWage` | The monthly wage each cohort was insured at, in today's money, before the cap. |
| 112 | `private double offEi` | Out of work, housed, and past EI or never on it. |
| 115 | `private double unhoused` | Out of work and evicted: no home, no EI. |
| 118 | `private double lastPool` | Last month's pool, and what the flows are struck against. |
| 119 | `private double[] lastFilled` |  |
| 120 | `private double[] lastPosts` |  |
| 121 | `private boolean started` |  |
| 124 | `private double arrivalsLooking` | Adults who arrived last month and joined the workforce this month. |
| 127 | `private double pendingEvicted` | Evicted at the last settle, waiting to be moved. |
| 131 | `private double jobsLost, openings, localHires, arrivalHires, arrivalsUnhired` |  |
| 132 | `private double entrants, otherExits, dropped, leftWhenBroke, evictedMoved` |  |
| 133 | `private double deaths, agedOut` |  |
| 134 | `private double benefitsPaid` |  |
| 135 | `private double insuredCap` |  |
| 274 | `private double lastImprisoned` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 520 | **type** `public class Unemployment` | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. |

### the dials (lines 70-102)

### the stocks (lines 103-128)

### the month (lines 129-136)

### reading (lines 137-198)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 1 | `public double getPool()` | Everyone out of work. |
| 143 | 5 | `public double onEi()` | On EI this month. |
| 149 | 1 | `public double getOffEi()` |  |
| 150 | 1 | `public double getUnhoused()` |  |
| 153 | 1 | `public double getHoused()` | Out of work with a home to keep - who the housing match is told about. |
| 156 | 1 | `public double getCohort(int monthsAgo)` | Claimants in a cohort, 0 the newest. |
| 158 | 1 | `public double getJobsLost()` |  |
| 159 | 1 | `public double getOpenings()` |  |
| 160 | 1 | `public double getLocalHires()` |  |
| 161 | 1 | `public double getArrivalHires()` |  |
| 162 | 1 | `public double getArrivalsUnhired()` |  |
| 165 | 1 | `public double getEntrants()` | Joined the pool with no claim: a graduate with no post, somebody turning eighteen. |
| 168 | 1 | `public double getOtherExits()` | Left the pool by some door the named flows do not name. |
| 171 | 1 | `public double getDroppedOffEi()` | Claims that reached their twelfth month this month. |
| 174 | 1 | `public double getLeftWhenBroke()` | The evicted who left the city this month. |
| 177 | 1 | `public double getNewlyUnhoused()` | The evicted who stayed, moved to the unhoused this month. |
| 179 | 1 | `public double getDeaths()` |  |
| 180 | 1 | `public double getAgedOut()` |  |
| 188 | 1 | `public double getBenefitsPaid()` | The EI bill on the pool as it stands, in total: struck at the end of each month's step, and struck again at the top of the next one, where the treasury pays it and the out of work are credited it (0.7.3) - see restrik... |
| 191 | 4 | `public double getBenefitPerClaimant()` | What one claimant draws on average this month. |
| 197 | 1 | `public double getInsuredCap()` | The insured wage this month's cap sits at. |

### the month (lines 199-424)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 3 | `public void noteEvicted(double households)` | Who the last settle evicted. |
| 213 | 9 | `public double takeEvicted()` | Moves last month's evicted: a share leave, the rest lose their home. |
| 239 | 34 | `public double imprison(double adults)` | Out of the pool and into prison, before the month's flows are struck (2026-09-11). |
| 277 | 1 | `public double getImprisoned()` | Taken from the pool into prison this month. |
| 280 | 3 | `public void noteArrivals(double adults)` | Adults who arrived this month; they look for work next month. |
| 296 | 86 | `public void advanceMonth(double pool, double[] filledByTier, double[] postsByTier, double[] wageByTier, double unskilledWage, d...` | The month's flows, the ring, and the bill. |
| 384 | 11 | `private void reconcile(double pool)` | The groups sum to the pool, whatever rounding or a clamped exit did. |
| 396 | 6 | `private void remember(double pool, double[] filled, double[] posts)` |  |
| 410 | 4 | `public double restrikeBenefits(double benefitRate)` | The EI bill struck again on the pool as it stands, at this benefit rate, and returned (0.7.3): Game pays it at the top of the month, where the out of work are credited it - on the pool the month opens with, at the dia... |
| 415 | 9 | `private void strikeBenefits(double benefitRate)` |  |

### HEALTH: the unhoused, as a share of the adult band (lines 425-518)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 17 | `public static double[] blendMortality(double[] factors, double[] uncared, double[] inBand, double[] unhoused, double[] orphans)` | Every band's mortality factor with its unhoused and its orphans blended in: the unhoused at UNHOUSED_MORTALITY times the band's factor, the orphans at the factor the band has with no care at all (or the city's, if tha... |
| 482 | 25 | `public static double[] attributeDeaths(double[] factors, double[] uncared, double[] inBand, double[] unhoused, double[] orphans...` | WHO AMONG THE MONTH'S DEAD WERE ORPHANS, AND WHO HAD NO HOME. |
| 514 | 4 | `public static double withUnhoused(double factor, double unhousedShare)` | A band's mortality factor with a share of it unhoused. |

### saving (lines 519-587)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 21 | `public double[] toSaveArray()` |  |
| 546 | 22 | `public boolean restore(double[] saved)` |  |
| 569 | 11 | `public void reset()` |  |
| 582 | 5 | `public void redenominate(double scale)` | Wages and the bill in the new unit. |


# Crime.java - 522 lines · 61 methods · 17 constants · model

`ham/citybuildersim/Crime.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Crime, the police who deter and catch it, and the prisons that hold who
> they catch.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Jerus, 2026-09-11: "crime is a function of unemployment, and tight or under
> households, we need police, and also prison, and yes that means another
> population category, only adults can go to jail, only adults commit crime,
> if there is crime, alot of police drastically reduces it but never
> eliminates it, but does reduce it alot. just if there is a reason for crime
> there is no way to actually remove it without changing the underlying
> reason."
> 
> That last sentence is the design. The police are a multiplier on crime,
> never a subtraction from it: full coverage takes 90% off whatever the city's
> reasons make, and the reasons are the out of work, the unhoused, the crowded
> and the households short of money - the things the rest of the game already
> measures. A city that wants less crime and has built all the police it can
> has to build homes and jobs.
> 
> ==================== THE MONTH ====================
> 
> 1. WHO IS AT RISK. Adults at liberty, sorted without double counting (Game
>    sorts them - see Causes): no home (5), out of work past EI (4), short of
>    money (3), on EI (2), crowded (2), everybody else (0.1). Where a group
>    overlaps another, the higher weight counts.
> 
> 2. HOW MANY CRIMES.
> 
>        pressure = sum of adults x weight + 2 x (1 - coverage) x adults
>        crimes   = K x pressure x (1 - 0.9 x coverage^1.5)
>        coverage = staffed officers / (360 per 100,000 people), at most 1
> 
>    The second term is Jerus's "low police will increase crime incentive
>    regardless of other reasons": every adult at liberty is two points more
>    tempted with no police at all, fading to nothing at full coverage. K is
>    struck so a Canada-like city - 3% of adults on EI, 1.5% past it, 0.15%
>    with no home, 5% short of money, 8% crowded - at Canada's half coverage
>    makes Canada's 5,585 crimes per 100,000 people a year (StatCan, 2025).
> 
> 3. WHAT IT DOES. A quarter is violent: half a month off work for each,
>    and 0.115% of them kill (Canada's 1.61 homicides per 100,000 against its
>    violent crime). The rest is theft, a quarter of a month's unskilled wage
>    each, half from the households and half from the businesses, handed to
>    the offenders' households. And a city with more crime than Canada's draws
>    fewer people and loses some - see Migration.
> 
> 4. WHO IS CAUGHT, AND WHO IS HELD. 9% of crimes times coverage end in a
>    six-month sentence - Canada's half coverage sends 4.5% of its crimes to
>    prison, 251 admissions a year per 100,000 people, which at six months
>    each is Canada's 127 inside on an average day. With no police nobody is
>    caught. The caught go in if there is a staffed cell free; the rest are
>    caught but not held. Six monthly cohorts; the seventh month, out, into
>    the pool of the out of work.
> 
> WHAT IS NOT HERE. Who a crime happens to, apart from the dead (adults - the
> pyramid has no other way to say who). Sentences by offence. Parole,
> reoffending, a record that follows a released prisoner into the labour
> market. Each would be a finer version of something this does coarsely;
> none is needed for the thing Jerus asked for, which is that crime has
> ... (1 more lines in the source)

**Uses:** [PopulationCohorts](PopulationCohorts.md) (1), [AgeBand](AgeBand.md) (1)

**Used by (12):** [BuildScreen](BuildScreen.md), [CrimeCheck](CrimeCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [Offending](Offending.md), [SafetyType](SafetyType.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [Unemployment](Unemployment.md)

## Sections

| line | section |
|---:|---|
| 68 | · the dials |
| 118 | · the causes |
| 253 | · the stock |
| 258 | · the month |
| 267 | · lifetime |
| 271 | · the month |
| 364 | · reading |
| 457 | · saving |

## Enum constants

| line | constant | says |
|---:|---|---|
| 128 | `Crime.Cause.NO_HOME` |  |
| 129 | `Crime.Cause.PAST_EI` |  |
| 130 | `Crime.Cause.SHORT_OF_MONEY` |  |
| 131 | `Crime.Cause.ON_EI` |  |
| 132 | `Crime.Cause.CROWDED` |  |
| 133 | `Crime.Cause.NO_CAUSE` |  |
| 134 | `Crime.Cause.FEW_POLICE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `Crime.CANADA_OFFICERS_PER_100K` | `180` | Police officers per 100,000 people, Canada, 2025 (StatCan: 75,107 officers). |
| 74 | `Crime.FULL_OFFICERS_PER_100K` | `2 * CANADA_OFFICERS_PER_100K` | Full coverage: twice Canada's level. |
| 77 | `Crime.CANADA_CRIMES_PER_100K` | `5_585` | Police-reported crime per 100,000 people a year, Canada, 2025. |
| 80 | `Crime.CANADA_HOMICIDES_PER_100K` | `1.61` | Homicides per 100,000 people a year, Canada, 2025. |
| 83 | `Crime.MAX_DETERRENCE` | `.90` | What full coverage takes off. |
| 91 | `Crime.DETERRENCE_POWER` | `1.5` | The curve between none and full. |
| 94 | `Crime.NO_POLICE_WEIGHT` | `2` | Every adult at liberty, tempted this much more with no police at all. |
| 97 | `Crime.VIOLENT_SHARE` | `.25` | A quarter of crime is violent. |
| 100 | `Crime.MONTHS_OFF_PER_VIOLENT` | `.5` | Months off work per violent crime, on the sick rate. |
| 103 | `Crime.KILLED_PER_VIOLENT` | `CANADA_HOMICIDES_PER_100K /(CANADA_CRIMES_PER_100K * VIOLENT_SHARE)` | Of violent crimes, the share that kill: Canada's homicides over its violent crime, 0.115%. |
| 107 | `Crime.THEFT_WAGE_SHARE` | `.25` | What one property crime takes, as a share of a month's unskilled wage. |
| 110 | `Crime.FROM_HOUSEHOLDS` | `.5` | ...of which this much from households, the rest from the businesses. |
| 113 | `Crime.CAUGHT_AT_FULL` | `.09` | The share of crimes that end in a sentence at full coverage; straight down to none with no police. |
| 116 | `Crime.SENTENCE_MONTHS` | `6` | Months a sentence lasts. |
| 153 | `Crime.CAUSES` | `Cause.values().length` |  |
| 224 | `Crime.K` | `strikeK()` | Crimes a month per point of pressure. |
| 459 | `Crime.STATE_LENGTH` | `SENTENCE_MONTHS + 5 + CAUSES + 13 + 2 + 5` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 136 | `private final String label` |  |
| 137 | `private final double weight` |  |
| 161 | `private final double[] adults` |  |
| 164 | `private double poolWeighted` | Of the weighted pressure, the part that is the out of work's. |
| 256 | `private final double[] ring` | Prisoners by months served, 0 the month they went in. |
| 260 | `private double population, officers, cells, coverage` |  |
| 261 | `private final double[] pressureBy` |  |
| 262 | `private double pressure, crimes, violent, killed, injuredShare, theftWanted` |  |
| 263 | `private double caught, admitted, notHeld, released, releasedEarly` |  |
| 264 | `private double stolenFromHouseholds, stolenFromBusinesses` |  |
| 265 | `private double payroll, upkeep` |  |
| 269 | `private double everCrimes, everKilled, everStolen, everAdmitted, everNotHeld` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 66 | 457 | **type** `public class Crime` | Crime, the police who deter and catch it, and the prisons that hold who they catch. |

### the dials (lines 68-117)

### the causes (lines 118-252)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 25 | **type** `public enum Cause` | Why an adult at liberty offends, and how much. |
| 139 | 4 | `Cause(String label, double weight)` _(in Crime.Cause)_ |  |
| 144 | 1 | `public String label()` _(in Crime.Cause)_ |  |
| 147 | 1 | `public double weight()` _(in Crime.Cause)_ | Per adult; FEW_POLICE's is before the coverage it is missing. |
| 150 | 1 | `public boolean isGroup()` _(in Crime.Cause)_ | The six that are groups of people. |
| 160 | 58 | **type** `public static final class Causes` | The adults at liberty, sorted into the groups above - each adult once. |
| 166 | 4 | `public Causes add(Cause cause, double people)` _(in Crime.Causes)_ |  |
| 172 | 6 | `public Causes add(Causes other)` _(in Crime.Causes)_ | Another set of adults, added group by group - a cell's, into the city's. |
| 179 | 4 | `public Causes addPool(double weighted)` _(in Crime.Causes)_ |  |
| 184 | 1 | `public double adults(Cause cause)` _(in Crime.Causes)_ |  |
| 187 | 5 | `public double adults()` _(in Crime.Causes)_ | Every adult at liberty. |
| 194 | 5 | `public double weighted()` _(in Crime.Causes)_ | Adults times weight, the six groups. |
| 200 | 1 | `public double poolWeighted()` _(in Crime.Causes)_ |  |
| 207 | 10 | `public static Causes canadaLike(double adults)` _(in Crime.Causes)_ | A city of this many adults at liberty sorted the way Canada's are: 3% on EI, 1.5% past it, 0.15% with no home, 5% short of money, 8% crowded. |
| 226 | 8 | `private static double strikeK()` |  |
| 236 | 4 | `public static double deterrence(double coverage)` | What the police leave of the crime the reasons make: 1 with none, 0.1 at full. |
| 242 | 5 | `public static double coverageOf(double officers, double population)` | Staffed officers against full coverage for this many people, 0-1. |
| 249 | 3 | `public static double caughtShare(double coverage)` | The share of crimes that end in a sentence at this coverage. |

### the stock (lines 253-257)

### the month (lines 258-266)

### lifetime (lines 267-270)

### the month (lines 271-363)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 63 | `public void advanceMonth(Causes causes, double population, double staffedOfficers, double staffedCells, double adultExitRate, d...` | Strikes the month's crime and turns the prison ring. |
| 352 | 5 | `public void recordStolen(double fromHouseholds, double fromBusinesses)` | What the thieves actually got - which is less than getTheftWanted() when the households or the businesses had less than that to take. |
| 359 | 4 | `public void setCosts(double payroll, double upkeep)` | What the police and the prisons cost this month. |

### reading (lines 364-456)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 5 | `public double prisoners()` | Everyone serving a sentence. |
| 374 | 1 | `public double cohort(int monthsServed)` | Prisoners in their nth month, 0 the newest. |
| 376 | 1 | `public double getPopulation()` |  |
| 377 | 1 | `public double getOfficers()` |  |
| 378 | 1 | `public double getCells()` |  |
| 379 | 1 | `public double getCoverage()` |  |
| 380 | 1 | `public double getAdultsAtLiberty()` |  |
| 382 | 1 | `public double getPressure()` |  |
| 383 | 1 | `public double getPressure(Cause c)` |  |
| 386 | 1 | `public double getCrimes()` | This month's crimes. |
| 389 | 3 | `public double getCrimes(Cause c)` | ...of which this many from one cause, by its share of the pressure. |
| 398 | 6 | `public double crimesAt(double otherCoverage)` | What this month's reasons would have made at another coverage: the groups the same, the part the police are missing moved, and what the police leave of it. |
| 406 | 1 | `public double crimesWithoutPolice()` | ...and with no police at all, for how much the police are taking off. |
| 408 | 1 | `public double getViolent()` |  |
| 409 | 1 | `public double getProperty()` |  |
| 410 | 1 | `public double getKilled()` |  |
| 413 | 1 | `public double getInjuredShare()` | The share of the city off work this month with an injury. |
| 416 | 1 | `public double getTheftWanted()` | What the month's thefts would take if there were money to take. |
| 417 | 1 | `public double getStolenFromHouseholds()` |  |
| 418 | 1 | `public double getStolenFromBusinesses()` |  |
| 419 | 1 | `public double getStolen()` |  |
| 421 | 1 | `public double getCaught()` |  |
| 422 | 1 | `public double getAdmitted()` |  |
| 423 | 1 | `public double getNotHeld()` |  |
| 424 | 1 | `public double getReleased()` |  |
| 425 | 1 | `public double getReleasedEarly()` |  |
| 428 | 3 | `public double getRatePer100k()` | Crimes a year per 100,000 people, at this month's pace. |
| 433 | 3 | `public double getRateVsCanada()` | The rate against Canada's: 1 is Canada. |
| 438 | 3 | `public double getOfficersPer100k()` | Officers per 100,000 people. |
| 443 | 3 | `public double getPrisonersPer100k()` | Prisoners per 100,000 people. |
| 447 | 1 | `public double getPayroll()` |  |
| 448 | 1 | `public double getUpkeep()` |  |
| 449 | 1 | `public double getGrossCost()` |  |
| 451 | 1 | `public double getEverCrimes()` |  |
| 452 | 1 | `public double getEverKilled()` |  |
| 453 | 1 | `public double getEverStolen()` |  |
| 454 | 1 | `public double getEverAdmitted()` |  |
| 455 | 1 | `public double getEverNotHeld()` |  |

### saving (lines 457-522)

| line | len | member | says |
|---:|---:|---|---|
| 466 | 17 | `public double[] getState()` | The ring, and the month as it was struck - the migration pull, the killings and the injuries next month read are this month's figures, and a flow cannot be rebuilt from the state a month ended in. |
| 485 | 17 | `public boolean restore(double[] saved)` |  |
| 503 | 9 | `public void reset()` |  |
| 514 | 8 | `public void redenominate(double scale)` | Money in the new unit. |


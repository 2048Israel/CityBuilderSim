# Health.java - 428 lines · 23 methods · 14 constants · model

`ham/citybuildersim/Health.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> How much of the workforce is off sick this month.
> 
> WHAT THIS IS NOT: a second population model. Nobody dies here, nobody leaves,
> and getWorkforce() never moves. The city has exactly as many adults on the
> first of the month as it did on the last of the previous one, and their names
> are still on the payroll. What changes is how much work gets done.
> 
> (Since 2026-09-11 people who STAY sick die - but of Sickness, which reads
> this rate and remembers how long each band's sick have been ill. This class
> still only says how many.)
> 
> SO IT IS A FOURTH UTILISATION RATIO, alongside energy, water and roads, and
> that is the whole design. Those three already exist, already multiply into
> output, already get carried across a save as a basis, and are already
> understood by every sector - so sickness costs the codebase one more
> multiplier rather than a new mechanism. A city short of power, short of water,
> gridlocked and mid-epidemic is worse off than any one of those alone, which is
> what multiplying gets you for free.
> 
> THE EMPLOYER CARRIES IT, per Jerus. Payroll is charged on who is STAFFED, not
> on who turned up: a mill with fifty people on the books pays fifty wages in a
> bad flu month and sells four weeks of output in three. That is what makes
> sickness bite - it is a margin squeeze, not a headcount cut - and it is the
> reason this multiplies revenue and not rPayroll. It also means the households
> are untouched: a sick worker still gets paid, still shops, still pays rent.
> 
> TWO THINGS SET THE RATE
> 
>   1. COVERAGE, which is the standing state. General-care beds divided by the
>      people who might need them. A city with none runs at the untreated rate
>      permanently; a city with enough runs near the floor. Nobody reaches zero
>      - people get sick in the best-served city on earth.
> 
>   2. OUTBREAKS, which are the events. Rare, sharp, and they decay. Hospitals
>      blunt an outbreak rather than preventing it, so a well-covered city still
>      gets them and still notices - it just survives them.
> 
> WHY THE OUTBREAK ROLL IS A FUNCTION OF THE MONTH NUMBER. Seeding a fresh
> Random from the month index means the sequence of outbreaks is fixed for a
> city and reproducible across a save, WITHOUT the generator's internal state
> having to be written to the save file and read back. A save carries the
> severity that is currently decaying, which is a fact about the city, and
> nothing else. Reload in month 340 and month 340's roll comes out the same way
> it did the first time - so an outbreak cannot be save-scummed away, which is
> the same property that made the consecutive-loss streaks worth carrying.

**Uses:** [Unemployment](Unemployment.md) (1), [CausesOfDeath](CausesOfDeath.md) (1)

**Used by (24):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [CausesOfDeath](CausesOfDeath.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [ScaleCheck](ScaleCheck.md), [ServicesScreen](ServicesScreen.md), [Sickness](Sickness.md), [SicknessCheck](SicknessCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WorksValue](WorksValue.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 54 | THE BASELINE |
| 77 | THE DEAD NOBODY BURIED |
| 102 | OUTBREAKS |
| 136 | · state |
| 178 | · the month |
| 317 | · reading it |
| 353 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 69 | `Health.WELL_SERVED_RATE` | `.03` | Absence when general care covers everybody. |
| 72 | `Health.UNTREATED_RATE` | `.18` | Absence with no general care at all. |
| 75 | `Health.MAX_SICK_RATE` | `.45` | No month loses more of the workforce than this, outbreak included. |
| 97 | `Health.UNBURIED_WEIGHT` | `20` | Multiplier on the unburied share of the population. |
| 100 | `Health.MAX_UNBURIED_SICKNESS` | `.15` | However many are lying about, this is the most it can cost. |
| 111 | `Health.OUTBREAK_CHANCE` | `1 / 60.0` | Chance per month that an outbreak begins, when none is running. |
| 114 | `Health.OUTBREAK_MIN_PEAK` | `.08` | Extra absence at the peak of an outbreak, before coverage blunts it. |
| 115 | `Health.OUTBREAK_MAX_PEAK` | `.25` |  |
| 118 | `Health.OUTBREAK_DECAY` | `.55` | What is left of the peak each following month. |
| 121 | `Health.OUTBREAK_FLOOR` | `.005` | Below this the outbreak is over. |
| 131 | `Health.OUTBREAK_MITIGATION` | `.60` | How much of an outbreak's peak good coverage can take off. |
| 134 | `Health.SEED` | `411_902_537_009L` | Fixed, like LandMarket's. |
| 175 | `Health.HUNGER_WEIGHT` | `.15` | How much sickness a city that cannot feed itself carries. |
| 176 | `Health.MAX_HUNGER_SICKNESS` | `.08` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 139 | `private double outbreakSeverity` | What is left of the current outbreak. |
| 142 | `private int outbreakStarted` | The month the current outbreak began, for the screen. |
| 145 | `private double sickRate` | Last month's figures, so the UI and the harness can read them. |
| 146 | `private double coverage` |  |
| 147 | `private double baselineRate` |  |
| 150 | `private double unburiedRate` | What the unburied dead are adding this month. |
| 153 | `private double hungerRate` | ...and what hunger is adding. |
| 285 | `private double influenzaRate` | What the 1918 influenza is adding on top: its sick, in its months only (R0b). |
| 292 | `private boolean influenzaHeldOff` | The influenza held off, for a harness fixture sized on today's deaths: see CausesOfDeath.holdOff(). |
| 296 | `private double unhousedRate` |  |
| 299 | `private double injuryRate` | What violent crime is adding on top: the injured, off work. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 377 | **type** `public class Health` | How much of the workforce is off sick this month. |

### THE BASELINE (lines 54-76)

### THE DEAD NOBODY BURIED (lines 77-101)

### OUTBREAKS (lines 102-135)

### state (lines 136-177)

### the month (lines 178-316)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 3 | `public void advanceMonth(double generalCareCapacity, double population, int month)` | The old three-argument form: a city with nothing left lying about. |
| 186 | 4 | `public void advanceMonth(double generalCareCapacity, double population, int month, double unburied)` | ...and the four-argument form: a city that can feed itself. |
| 201 | 4 | `public void advanceMonth(double generalCareCapacity, double population, int month, double unburied, double hungry)` | Works out this month's sick rate. |
| 211 | 4 | `public void advanceMonth(double generalCareCapacity, double population, int month, double unburied, double hungry, double unhou...` | the orphans - who get sick UNHOUSED_SICKNESS times as often. |
| 222 | 61 | `public void advanceMonth(double generalCareCapacity, double population, int month, double unburied, double hungry, double unhou...` | violent crime - Crime.getInjuredShare(). |
| 286 | 1 | `public double getInfluenzaRate()` |  |
| 293 | 1 | `public void holdInfluenzaOff()` |  |
| 294 | 1 | `public boolean isInfluenzaHeldOff()` |  |
| 300 | 1 | `public double getInjuryRate()` |  |
| 303 | 1 | `public double getUnhousedRate()` | What the unhoused and the orphans are adding on top. |
| 312 | 4 | `public static double coverageOf(double capacity, double population)` | Share of the people general care has to serve that it has room for. |

### reading it (lines 317-352)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 1 | `public double getSickRate()` | The share of the workforce that is off sick. |
| 329 | 1 | `public double getWorkRatio()` | What sickness leaves of the month's output: 1 when nobody is ill. |
| 336 | 1 | `public double getCoverage()` | People treated per person, capped at 1 - which since 2026-09-19 is the staffed beds LESS whoever the clinic's fee turned away, because that is what Game passes in. |
| 339 | 1 | `public double getBaselineRate()` | What the rate would be with no outbreak running. |
| 342 | 1 | `public double getOutbreakSeverity()` | What the current outbreak is adding on top. |
| 345 | 1 | `public double getUnburiedRate()` | What the unburied dead are adding on top. |
| 346 | 1 | `public double getHungerRate()` |  |
| 348 | 1 | `public boolean isOutbreak()` |  |
| 351 | 1 | `public int getOutbreakStarted()` | The month the running outbreak began, or 0. |

### saving (lines 353-428)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 21 | `public double[] getState()` | The state, in order. |
| 394 | 26 | `public boolean restore(double[] state)` | Puts a saved month's health back. |
| 422 | 6 | `private static long scramble(long value)` | SplitMix64, same as LandMarket's, so adjacent months look unrelated. |


# Sickness.java - 393 lines · 28 methods · 6 constants · model

`ham/citybuildersim/Sickness.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> Who has been sick, and for how long - and the ones it kills.
> 
> Jerus, 2026-09-11: people who stay sick should start dying. Health sets how
> many people are sick this month, as it always has;
> this class remembers how long they have been, with a ring of monthly
> cohorts the way Unemployment remembers who is on EI, and whoever has been
> sick for more than two months dies at their age's monthly chance.
> 
> ONE RING PER BAND, AND IT HOLDS SHARES. Each slot is a share of the band -
> sick for under a month, one to two months, ... eleven to twelve, and a year
> or more - not a count of people. The pyramid ages, gives birth, dies and
> migrates in proportion, so a share carried from one month to the next is
> still the same share of the same band, and no sick person ever has to be
> walked from one band to the next. Nobody is tracked; the ring only knows
> how the band's sick are spread over time.
> 
> ACUTE INFLUENZA STAYS BESIDE THE RING (R0h). Its cases end within weeks and
> CausesOfDeath books its dead. Putting them in the ring killed them again
> three months later. The acute share joins the visible sick and the youngest
> bar, while only the rate without influenza enters the mortality ring.
> 
> THE RATE DECIDES HOW MANY, THE RING HOW LONG. Each month the ring turns - the
> dead come out, the month's recovery gets better, everyone else moves a slot
> along - and then it meets the band's share: the shortfall fell ill this month
> and enters the first slot; a surplus means the sickness is lifting, and every
> slot is scaled down alike. The same shape as Unemployment.reconcile(): the
> pool is the labour market's, the ring only says who is in it.
> 
> WHAT CLINICS DO NOW. General care still sets the city's rate (Health), and
> now it also sets how fast the sick get better - half of them a month with
> none, nine in ten with everybody covered - and that is the only way care
> keeps a teenager or an adult alive: its old three-fold swing on their death
> rate is gone (Healthcare.mortalityFactor). Jerus: sickness replaces it.
> Childcare and senior care keep their swings, and also take away the extra
> sickness the youngest and the oldest carry.

**Uses:** [AgeBand](AgeBand.md) (31), [PopulationCohorts](PopulationCohorts.md) (5), [Health](Health.md) (1)

**Used by (13):** [BuildScreen](BuildScreen.md), [CausesOfDeath](CausesOfDeath.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [ScaleCheck](ScaleCheck.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md)

## Sections

| line | section |
|---:|---|
| 42 | THE NUMBERS - every one of them Jerus's, from a table of what each |
| 122 | · state |
| 133 | · the month |
| 234 | · reading it |
| 305 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `Sickness.RING` | `13` | Slots in each band's ring: 0 is under a month, 12 is a year or more. |
| 56 | `Sickness.DEADLY_FROM` | `2` | The first slot that can die: sick for more than two months. |
| 66 | `Sickness.EXTRA_SICKNESS` | `1.0` | How much sicker babies and seniors are than the city, with none of their own care: twice. |
| 69 | `Sickness.RECOVERY_UNTREATED` | `.5` | The share of the sick who get better in a month, with no general care... |
| 72 | `Sickness.RECOVERY_SERVED` | `.9` | ...and with general care for everybody. |
| 75 | `Sickness.MAX_SHARE` | `Health.MAX_SICK_RATE` | No band has more of itself sick than the city rate may. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 124 | `private final double[][] ring` |  |
| 126 | `private final double[] acute` | This month's acute influenza shares: visible, but never aged into the mortality ring (R0h). |
| 127 | `private boolean seeded` |  |
| 128 | `private double lastRecovery` |  |
| 131 | `private final double[] lastDeaths` | The dead the pyramid booked to sickness last month, by band - set by Game. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 354 | **type** `public class Sickness` | Who has been sick, and for how long - and the ones it kills. |

### THE NUMBERS - every one of them Jerus's, from a table of what each (lines 42-121)

| line | len | member | says |
|---:|---:|---|---|
| 94 | 12 | `public static double deathChance(AgeBand band)` | The monthly chance of dying once sick for more than two months, by age: babies 4%, children and teenagers 1%, adults 2%, seniors 4%, elders 8%. |
| 108 | 8 | `public static double shareFor(AgeBand band, double cityRate, double childcareCoverage, double seniorCoverage)` | How much of a band is sick at a city rate, given the care that serves it. |
| 118 | 3 | `public static double recovery(double generalCoverage)` | The share of the sick who get better this month. |

### state (lines 122-132)

### the month (lines 133-233)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 7 | `public double[] deathRates()` | Each band's extra monthly chance of dying, from the ring as it stands: the share sick past two months times their age's chance. |
| 157 | 4 | `public void advanceMonth(double cityRate, double generalCoverage, double childcareCoverage, double seniorCoverage)` | Turns every band's ring a month and meets this month's rate. |
| 163 | 31 | `public void advanceMonth(double cityRate, double withoutInfluenza, double generalCoverage, double childcareCoverage, double sen...` | The full rate remains visible; only the rate without influenza enters the ring. |
| 205 | 4 | `public void seed(double cityRate, double generalCoverage, double childcareCoverage, double seniorCoverage)` | Puts every band's ring at the steady state for a rate: a city that has been this sick, and this well served, for ever. |
| 211 | 22 | `public void seed(double cityRate, double withoutInfluenza, double generalCoverage, double childcareCoverage, double seniorCover...` | Seeds the chronic steady state, carrying the month's acute shares beside it. |

### reading it (lines 234-304)

| line | len | member | says |
|---:|---:|---|---|
| 237 | 5 | `public double share(AgeBand b)` | The share of a band that is sick, including this month's acute influenza. |
| 244 | 6 | `public double pastTwoMonths(AgeBand b)` | The share of a band sick for more than two months - the ones who can die of it. |
| 252 | 1 | `public double aYearOrMore(AgeBand b)` | The share of a band sick for a year or more. |
| 255 | 3 | `public double slot(AgeBand b, int k)` | One slot of one band's ring, 0 to RING - 1. |
| 259 | 1 | `public boolean isSeeded()` |  |
| 260 | 1 | `public double getLastRecovery()` |  |
| 263 | 6 | `public double peoplePastTwoMonths(PopulationCohorts cohorts)` | People sick past two months, across the city. |
| 275 | 6 | `public double peopleInSlot(PopulationCohorts cohorts, int k)` | People sick k months, across the city (0.7.28): one slot of every band's ring in people, with acute cases in slot 0 - the Services screen's long-sick bars. |
| 283 | 6 | `public double peopleSick(PopulationCohorts cohorts)` | People sick this month, across the city. |
| 291 | 5 | `public void setLastDeaths(double[] byBand)` | Recorded by Game from what the pyramid actually booked. |
| 297 | 1 | `public double getLastDeaths(AgeBand b)` |  |
| 299 | 5 | `public double getLastDeaths()` |  |

### saving (lines 305-393)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 1 | `public double[] getAcuteShares()` | A copy of this month's acute shares, saved separately from the chronic ring. |
| 311 | 9 | `public void restoreAcute(String[] bands, double[] saved)` | Missing on an older save means none; names follow the same saved bands as the ring. |
| 322 | 10 | `public double[] getState()` | Every ring, the month's dead by band, the recovery, and whether it has been seeded. |
| 334 | 3 | `public boolean restore(double[] saved)` | A ring saved before the band names travelled with it. |
| 351 | 23 | `public boolean restore(String[] bands, double[] saved)` | Refused whole on a length mismatch. |
| 376 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 382 | 7 | `public void reset()` |  |
| 390 | 3 | `private static double clamp(double v)` |  |


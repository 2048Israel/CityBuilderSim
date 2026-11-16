# Sickness.java - 519 lines · 39 methods · 11 constants · model

`ham/citybuildersim/Sickness.java` - generated 2026-11-16 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (32), [PopulationCohorts](PopulationCohorts.md) (5), [MedicalRows](MedicalRows.md) (4), [Health](Health.md) (1)

**Used by (14):** [BuildScreen](BuildScreen.md), [CausesOfDeath](CausesOfDeath.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MedicineCheck](MedicineCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [ScaleCheck](ScaleCheck.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md)

## Sections

| line | section |
|---:|---|
| 42 | THE NUMBERS - every one of them Jerus's, from a table of what each |
| 122 | THE MEDICAL ROWS (R2f) - what a 1900 city's sick lack until each is known |
| 185 | · state |
| 237 | · the month |
| 360 | · reading it |
| 431 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `Sickness.RING` | `13` | Slots in each band's ring: 0 is under a month, 12 is a year or more. |
| 56 | `Sickness.DEADLY_FROM` | `2` | The first slot that can die: sick for more than two months. |
| 66 | `Sickness.EXTRA_SICKNESS` | `1.0` | How much sicker babies and seniors are than the city, with none of their own care: twice. |
| 69 | `Sickness.RECOVERY_UNTREATED` | `.5` | The share of the sick who get better in a month, with no general care... |
| 72 | `Sickness.RECOVERY_SERVED` | `.9` | ...and with general care for everybody. |
| 75 | `Sickness.MAX_SHARE` | `Health.MAX_SICK_RATE` | No band has more of itself sick than the city rate may. |
| 137 | `Sickness.LONG_RECOVERY_BEFORE_M1` | `224 / 362.0` | Before the TB drugs (M1) the sick of a year or more get better at this share of the month's recovery: Canadian TB stays fell from 362 to 224 days with them [M-S1]. |
| 145 | `Sickness.SERVED_DEATHS_BEFORE_M2` | `55 / 900.0 *(87 / 22.0 - 1)` | Before the breathing machine (M2) the long sick general care serves die this much more often: it took respiratory polio's deaths from 87% to 22% [M-M5], on the respiratory share of deaths, pneumonia's 55 of about 900 ... |
| 148 | `Sickness.STAYS_BEFORE_PM5` | `10 / 11.0` | General care's part of the recovery before penicillin (PM5) is this share of its part after: Canada's general-hospital stay fell from 11.0 to 10.0 days, 1945-50 (observed, not proven causal) [SC-B285]. |
| 155 | `Sickness.STAYS_BEFORE_LATER_ROW` | `Math.pow(7.3 / 10.0, 1 / 4.0)` | ...and before each of the four later rows that shorten a stay (M5, M6, M9, M10), this share: they split the fall from 1950's 10.0 days to 2022-23's 7.3 [SC-B285], [CIHI-LOS] evenly (est.). |
| 158 | `Sickness.LATER_STAYS_ROWS` | `java.util.List.of("M5", "M6", "M9", "M10")` | The later rows that shorten general care's stays, each taking STAYS_BEFORE_LATER_ROW off until it is known. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 187 | `private final double[][] ring` |  |
| 189 | `private final double[] acute` | This month's acute influenza shares: visible, but never aged into the mortality ring (R0h). |
| 190 | `private boolean seeded` |  |
| 191 | `private double lastRecovery` |  |
| 200 | `private double staysFactor` | This month's medical rows (R2f), set by Game before the month reads the ring and derived from the city's research, so never saved: general care's part of the recovery, the year-or-more slot's share of it, and how much... |
| 201 | `private double longRecovery` |  |
| 202 | `private double deathLift` |  |
| 235 | `private final double[] lastDeaths` | The dead the pyramid booked to sickness last month, by band - set by Game. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 480 | **type** `public class Sickness` | Who has been sick, and for how long - and the ones it kills. |

### THE NUMBERS - every one of them Jerus's, from a table of what each (lines 42-121)

| line | len | member | says |
|---:|---:|---|---|
| 94 | 12 | `public static double deathChance(AgeBand band)` | The monthly chance of dying once sick for more than two months, by age: babies 4%, children and teenagers 1%, adults 2%, seniors 4%, elders 8%. |
| 108 | 8 | `public static double shareFor(AgeBand band, double cityRate, double childcareCoverage, double seniorCoverage)` | How much of a band is sick at a city rate, given the care that serves it. |
| 118 | 3 | `public static double recovery(double generalCoverage)` | The share of the sick who get better this month. |

### THE MEDICAL ROWS (R2f) - what a 1900 city's sick lack until each is known (lines 122-184)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 7 | `public static double staysFactor(MedicalRows rows, int month)` | General care's part of the recovery in a month, as a share of today's: 1 once PM5 and the later stays rows are known. |
| 170 | 3 | `public static double longRecoveryFactor(MedicalRows rows, int month)` | The year-or-more slot's share of the month's recovery: 1 once M1 is known. |
| 175 | 3 | `public static double servedDeathsBefore(MedicalRows rows, int month)` | How much more often the served long sick die, per unit of general coverage: 0 once M2 is known. |
| 180 | 4 | `public static double recovery(double generalCoverage, double staysFactor)` | The share of the sick who get better this month, general care's part times the stays factor: recovery(generalCoverage) itself where it is 1. |

### state (lines 185-236)

| line | len | member | says |
|---:|---:|---|---|
| 210 | 5 | `public void setMedicine(MedicalRows rows, int month, double generalCoverage)` | The month's medical rows (R2f): what the city knows this month, and general care's coverage, the share of the long sick the breathing machine would serve. |
| 217 | 1 | `public double getStaysFactor()` | General care's part of the recovery this month, as a share of today's (R2f). |
| 219 | 1 | `public double getLongRecovery()` | The year-or-more slot's share of the month's recovery (R2f). |
| 221 | 1 | `public double getDeathLift()` | How much more often the long sick die this month than deathChance() says (R2f). |
| 224 | 1 | `public double recoveryAt(double generalCoverage)` | The share of the sick who get better this month at a coverage, with the month's stays factor: what a notice or a screen quotes (R2f). |
| 227 | 1 | `public double servedRecovery()` | The share of the sick who get better this month with everybody served: RECOVERY_SERVED once the stays rows are known (R2f). |
| 230 | 3 | `public double chanceThisMonth(AgeBand band)` | A band's monthly chance of dying once sick past two months, this month: deathChance() raised by the lift before M2. |

### the month (lines 237-359)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 7 | `public double[] deathRates()` | Each band's extra monthly chance of dying, from the ring as it stands: the share sick past two months times their age's chance. |
| 261 | 4 | `public void advanceMonth(double cityRate, double generalCoverage, double childcareCoverage, double seniorCoverage)` | Turns every band's ring a month and meets this month's rate. |
| 267 | 33 | `public void advanceMonth(double cityRate, double withoutInfluenza, double generalCoverage, double childcareCoverage, double sen...` | The full rate remains visible; only the rate without influenza enters the ring. |
| 311 | 4 | `public void seed(double cityRate, double generalCoverage, double childcareCoverage, double seniorCoverage)` | Puts every band's ring at the steady state for a rate: a city that has been this sick, and this well served, for ever. |
| 317 | 42 | `public void seed(double cityRate, double withoutInfluenza, double generalCoverage, double childcareCoverage, double seniorCover...` | Seeds the chronic steady state, carrying the month's acute shares beside it. |

### reading it (lines 360-430)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 5 | `public double share(AgeBand b)` | The share of a band that is sick, including this month's acute influenza. |
| 370 | 6 | `public double pastTwoMonths(AgeBand b)` | The share of a band sick for more than two months - the ones who can die of it. |
| 378 | 1 | `public double aYearOrMore(AgeBand b)` | The share of a band sick for a year or more. |
| 381 | 3 | `public double slot(AgeBand b, int k)` | One slot of one band's ring, 0 to RING - 1. |
| 385 | 1 | `public boolean isSeeded()` |  |
| 386 | 1 | `public double getLastRecovery()` |  |
| 389 | 6 | `public double peoplePastTwoMonths(PopulationCohorts cohorts)` | People sick past two months, across the city. |
| 401 | 6 | `public double peopleInSlot(PopulationCohorts cohorts, int k)` | People sick k months, across the city (0.7.28): one slot of every band's ring in people, with acute cases in slot 0 - the Services screen's long-sick bars. |
| 409 | 6 | `public double peopleSick(PopulationCohorts cohorts)` | People sick this month, across the city. |
| 417 | 5 | `public void setLastDeaths(double[] byBand)` | Recorded by Game from what the pyramid actually booked. |
| 423 | 1 | `public double getLastDeaths(AgeBand b)` |  |
| 425 | 5 | `public double getLastDeaths()` |  |

### saving (lines 431-519)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 1 | `public double[] getAcuteShares()` | A copy of this month's acute shares, saved separately from the chronic ring. |
| 437 | 9 | `public void restoreAcute(String[] bands, double[] saved)` | Missing on an older save means none; names follow the same saved bands as the ring. |
| 448 | 10 | `public double[] getState()` | Every ring, the month's dead by band, the recovery, and whether it has been seeded. |
| 460 | 3 | `public boolean restore(double[] saved)` | A ring saved before the band names travelled with it. |
| 477 | 23 | `public boolean restore(String[] bands, double[] saved)` | Refused whole on a length mismatch. |
| 502 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 508 | 7 | `public void reset()` |  |
| 516 | 3 | `private static double clamp(double v)` |  |


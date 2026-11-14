# ArrivalMixCheck.java - 375 lines · 21 methods · 7 constants · harnesses

`ham/citybuildersim/ArrivalMixCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The skill mix of the arrivals by year (R0d slice 2): the year's mix in a city founded the 1900 way, and WageBand's ceilings in every
> other city. Not part of the game.
> 
> WHAT THIS HAS TO PROVE (R0d, 2026-11-06; docs/studies/research-1900.md 5.4 item 6 and 12.1, "Skill mix by year"). A 1901 arrival was a
> labourer or a farm hand far more often than a diploma-holder, and a graduate was rare; the mix reaches today's by 2021, and only a city
> founded the 1900 way is drawn from it. What is checkable:
> 
>   1. THE KNOTS: in year order, each one's shares non-negative and summing to one, a source key or the estimate flag on each, the last
>      one today's model, and the mix from its January on today's constants themselves
>   2. THE LOOK-UP: a knot's own point at its January, the mean of two knots' shares half-way, the first knot's before the first, the
>      university figure never falling and the no-diploma one never rising from month to month, the same month the same point
>   3. AGAINST THE STUDY: a 1901 arrival at the going rate most likely holds no diploma, and 1953's university share is a good
>      multiple of 1901's
>   4. MIGRATION ON THE YEAR'S MIX: today's mix is today's model (no mix and TODAY draw the same arrivals, the same licences and the
>      same volume, bit for bit or not, said below); at the going rate in 1913 no graduate arrives and the arrivals without a diploma
>      outnumber the diploma-holders; with the graduate bands bid up, 1953 brings a larger graduate share than 1913
>   5. PLAYED: a city founded the 1900 way plays 24 months drawing on the year's mix each month, its arrivals by band summing to the
>      month's and more of them without a diploma than with one; a city founded the default way holds no mix in any of them
> 
> Every fixture CAUSES its condition: the markets are priced by the labour market's own months (a market at base, and one whose posts
> outrun its supply for 200 months, with every licence in short supply too), the draw is a fresh Migration into a city with room, jobs and
> homes and no workforce (so every band's chance of work is one), and the cities are founded through newGame(Founding.withUnfitted(true))
> as the player's door founds them. Figures are asserted against the model's own constants and methods.

**Uses:** [ArrivalMix](ArrivalMix.md) (66), [WageBand](WageBand.md) (15), [Migration](Migration.md) (14), [LabourMarket](LabourMarket.md) (9), [Game](Game.md) (7), [Founding](Founding.md) (6), [CityCalendar](CityCalendar.md) (4), [FittingCheck](FittingCheck.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [EquipmentCheck](EquipmentCheck.md) (2), [FamilyModel](FamilyModel.md) (2), [GameFiles](GameFiles.md) (1), [AgeBand](AgeBand.md) (1), [JobType](JobType.md) (1), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 109 | 1. the knots |
| 148 | 2. the look-up |
| 199 | 3. against the study |
| 214 | 4. Migration on the year's mix |
| 329 | 5. played |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `ArrivalMixCheck.DEFAULT` | `Founding.defaults()` |  |
| 76 | `ArrivalMixCheck.THE_1900_WAY` | `Founding.defaults().withUnfitted(true)` |  |
| 78 | `ArrivalMixCheck.NONE` | `WageBand.NONE.ordinal()` |  |
| 79 | `ArrivalMixCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal()` |  |
| 80 | `ArrivalMixCheck.COLLEGE` | `WageBand.COLLEGE.ordinal()` |  |
| 81 | `ArrivalMixCheck.UNIVERSITY` | `WageBand.UNIVERSITY.ordinal()` |  |
| 217 | `ArrivalMixCheck.ADULT_SHARE` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share the fixtures run at (PopulationCheck's). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |
| 36 | `static PrintStream out` |  |
| 37 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 343 | **type** `public class ArrivalMixCheck` | The skill mix of the arrivals by year (R0d slice 2): the year's mix in a city founded the 1900 way, and WageBand's ceilings in every other city. |
| 39 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 44 | 1 | `static void check(String label, boolean ok)` |  |
| 47 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the harness's own, to a relative tolerance. |
| 52 | 5 | `static void quietly(Runnable r)` |  |
| 59 | 5 | `static Game found(String label, Founding founding)` | A new city founded as the player's door founds it (newGame(founding)), on a scratch folder, quietly. |
| 66 | 3 | `static void step(Game g, int months)` | One press of the clock, as the player's button makes it, quietly. |
| 71 | 3 | `static Game village(String label, Founding founding)` | A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()). |
| 83 | 15 | `public static void main(String[] args) throws Exception` |  |
| 100 | 4 | `static ArrivalMix.Knot knot(int year)` | The knot of a year. |
| 105 | 1 | `static ArrivalMix.Knot first()` |  |
| 107 | 1 | `static ArrivalMix.Knot last()` |  |

### 1. the knots (lines 109-147)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 36 | `static void sectionOne()` |  |

### 2. the look-up (lines 148-198)

| line | len | member | says |
|---:|---:|---|---|
| 150 | 48 | `static void sectionTwo()` |  |

### 3. against the study (lines 199-213)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 12 | `static void sectionThree()` |  |

### 4. Migration on the year's mix (lines 214-328)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 3 | `static LabourMarket atBase()` | A labour market at base: every wage the going rate, so every band's premium is one and nobody is bought. |
| 228 | 11 | `static LabourMarket bidUp()` | A labour market whose posts outrun its supply for 200 months, every band's and every licence's: the wages walk to the top of their range, so the premium on the graduate and the no-diploma bands and the doctors' over t... |
| 244 | 10 | `static Migration draw(ArrivalMix.Point mix, LabourMarket market)` | One month's draw by a fresh Migration with this mix (null for none), priced by this market, into a city of 4,000 with room, jobs and homes for more and no workforce behind it (so every band's chance of work is one): t... |
| 255 | 5 | `static double sum(double[] values)` |  |
| 262 | 4 | `static double graduateShare(Migration mig)` | The college and university arrivals (the licensed professions included, which are counted in the university band) as a share of the month's. |
| 267 | 61 | `static void sectionFour()` |  |

### 5. played (lines 329-375)

| line | len | member | says |
|---:|---:|---|---|
| 331 | 44 | `static void sectionFive()` |  |


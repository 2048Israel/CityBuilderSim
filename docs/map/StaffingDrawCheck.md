# StaffingDrawCheck.java - 1,124 lines · 52 methods · 16 constants · harnesses

`ham/citybuildersim/StaffingDrawCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff
> Sector.MIN_STAFFABLE_TO_ORDER of its posts, and since GW1 that count is the spare workers and the workers migration would bring for
> the order's own posts within Sector.GATE_DRAW_MONTHS (Migration.drawFor()). Not part of the game.
> 
> WHY. GW0 (docs/studies/spec-staffing-gate.md) found about 15 refused orders a month in a young city while a fifth of its workers
> were jobless: the gate asked what the city could staff today, and the workers a new works would draw were not there until it opened.
> The gate now counts a year's draw. Each fixture below causes its case: a village with room whose spare workers are few, a city
> whose crowding reads 0, a month struck on a pull, a town without its graduate bands, a refusal worded, a saved city and its twin, and
> every order of a village counted ask by ask. Nothing here moves a premise to get green.
> 
> WHAT THE DRAW COUNTS (Migration.drawFor(), after the gate's review): only the part of migration's gap that the order makes positive
> (max(0, gap + the order's addition to the target) - max(0, gap), the gap being the target less the population), closed over the
> window, and no more than the doors last month left free; at the going wage, so no band premium is read (the premium is priced on the
> posts the gate refused, Game.repriceLabour(), and a gate that read it would let a refused order's own posts raise the wage that
> draws the workers that pass it). It first counted the order's whole addition whatever the gap and the room, so it drew where
> migration brings nobody (a target at or below the people; YearBookCheck's city had 7,738 posts against a labour force of 6,326 at month 60).
> 
> What this has to prove:
>   1. In a village with room (the gap positive, the doors many) the draw passes a works the spare workers alone refuse, and the drawn
>      workers are the formula's.
>   2. In a city whose crowding reads 0 the draw is nothing, the share is the spare's, and what the spare refuses is refused.
>   3. The pulls turn the draw down, by the ratio of their products (the jobs' pulls: the rent's and the crime's; WG2's brake is section 10).
>   4. The Shared Services Centre in a town without its graduate bands is refused by the share, not the band test; a band held closed
>      draws nobody and is the band nobody can fill, as before; and a college wage bid up draws the gate nobody more, to the bit.
>   5. The hold's words name both counts.
>   6. A loaded city's gate is its twin's, to the bit, in a city founded the default way and one founded the 1900 way, and the last
>      month's room it reads rides the save.
>   7. How often the draw opens an order, in a village seated by hand (seat()) and asked every order in a month struck with room and
>      in one struck with none (roomMonth(), noRoomMonth()): it opens some with room, and never does in a month of no room.
>   8. The gap's positive part: where the population stands above migration's target even with the order, the draw is nothing, to the
>      bit; where the order takes the gap from negative to positive, it is that positive part's, and less than the whole addition's.
>      The population is set over the target by hand (setPopulation()), by the mean of two orders' additions, so both sides of the zero gap are asked.
>   9. Within the room: where the doors last month left free are fewer than the draw would be, the draw is the room left.
>  10. The brake and the pull rule of WG2 turn the draw down as they turn arrivals: full senior care draws the same workers as none, to
>      the bit; the draw is the unbraked draw x Migration.arrivalBrake() of the village's out-of-work share, the brake acting from
>      Migration.BRAKE_FROM_MONTH (the villages are that old); and past Migration.ARRIVALS_STOP_OUT_OF_WORK it is nothing, and the
>      order the village passed is refused. A village younger than BRAKE_FROM_MONTH draws the unbraked draw whole, past the stop too.

**Uses:** [Sector](Sector.md) (75), [Migration](Migration.md) (62), [Game](Game.md) (37), [BuildingsTemplate](BuildingsTemplate.md) (26), [WageBand](WageBand.md) (24), [PopulationManager](PopulationManager.md) (17), [JobType](JobType.md) (15), [BuildCard](BuildCard.md) (11), [Founding](Founding.md) (6), [GameFiles](GameFiles.md) (5), [PayTier](PayTier.md) (4), [FamilyModel](FamilyModel.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [LabourMarket](LabourMarket.md) (3), [NewUnit](NewUnit.md) (2), [RealEstate](RealEstate.md) (2), [EducationType](EducationType.md) (2), [BuildingManager](BuildingManager.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [TempDirs](TempDirs.md) (1), [ArrivalMix](ArrivalMix.md) (1)

**Used by (1):** [StaffingTieCheck](StaffingTieCheck.md)

## Sections

| line | section |
|---:|---|
| 100 | · the helpers |
| 415 | 1. a village with room: the draw passes what the spare does not |
| 460 | 2. a city with no room: the draw refuses what the spare refuses |
| 499 | 3. the pulls turn the draw down |
| 568 | 4. the Shared Services Centre in a town without its bands is still refused |
| 680 | 5. the hold's words name both counts |
| 713 | 6. a loaded city's gate is its twin's |
| 829 | 7. how often the draw opens an order |
| 887 | 8. the gap's positive part: only what the order makes positive is drawn |
| 966 | 9. within the room: no more than the doors last month left free |
| 1019 | 10. the brake and the pull rule turn the draw down as they turn arrivals |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `StaffingDrawCheck.SPARE_SHARE` | `.40` | The share of the works the village's spare workers alone fill: well under MIN_STAFFABLE_TO_ORDER, so only the draw can take it over the line. |
| 59 | `StaffingDrawCheck.WORKFORCE_SHARE` | `.5` | The workforce as a share of the people: what Migration.drawFor() turns a draw of residents into workers by (workforce over population). |
| 62 | `StaffingDrawCheck.MOST_WORKERS` | `5_000` | The most workers a fixture searches for a share. |
| 65 | `StaffingDrawCheck.TOWN_WORKERS` | `100` | The workers of the town whose graduate bands are empty, half of them with a diploma: few enough that the draw, not the spare, fills the centre's diploma posts. |
| 68 | `StaffingDrawCheck.NO_CENSUS_SPARE` | `4` | The spare workers of the city that has no census (section 9): few, so that with the workers it employs its people are fewer than the centre's addition. |
| 71 | `StaffingDrawCheck.HOUSEHOLD_PEOPLE` | `4_000, MONTH_JOBS = 6_000, MONTH_CAPACITY = 12_000, ROOMY_DOORS = 9_000` | The people Migration's month is struck on in the fixtures that put its crowding at 1 or at 0 (ArrivalMixCheck.draw()'s figures). |
| 74 | `StaffingDrawCheck.CENSUS_SIZES` | `6` | The household sizes a month's door census holds in the fixtures that give it one (ROOMY_DOORS split evenly over 1 to this). |
| 77 | `StaffingDrawCheck.EQUILIBRIUM_ADULTS` | `0` | The adult share Migration's month is struck on: 0 reads the equilibrium share, the one the fresh city's residents a job already use. |
| 80 | `StaffingDrawCheck.DEAR_RENT` | `2, DEAR_CRIME = 3` | Rent as a multiple of the target burden in section 3's dear month, and crime as a multiple of Canada's: affordabilityPull() 1/2, crimePull() 1/1.2. |
| 83 | `StaffingDrawCheck.RUINOUS_RENT` | `10` | Rent at ten times the target: the affordability pull is a tenth, and the draw with it. |
| 86 | `StaffingDrawCheck.MONTHS_BEFORE_SAVE` | `24` | The months the twin cities are played before they are saved. |
| 88 | `StaffingDrawCheck.NONE` | `WageBand.NONE.ordinal()` |  |
| 89 | `StaffingDrawCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal()` |  |
| 90 | `StaffingDrawCheck.COLLEGE` | `WageBand.COLLEGE.ordinal()` |  |
| 91 | `StaffingDrawCheck.UNIVERSITY` | `WageBand.UNIVERSITY.ordinal()` |  |
| 890 | `StaffingDrawCheck.MOST_PLAYED_MONTHS` | `60` | The most months a played city is run, one press of the clock at a time, to reach the month a section asks for. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 53 | `static PrintStream out, quiet` |  |
| 94 | `static boolean villagePassed` | Carried from section 1 to section 2: whether the village's order passed. |
| 97 | `static Sector.Staffing noRoomRefusal` | Carried from section 2 to section 5: the order the city with no room refused, and the works it was. |
| 98 | `static String noRoomName` |  |
| 340 | `final Sector sector` |  |
| 341 | `final BuildingsTemplate template` |  |
| 342 | `final double posts` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1075 | **type** `public class StaffingDrawCheck` | The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff Sector.MIN_STAFFABLE_TO_ORDER of its posts, and since GW1 that count is the spare workers and the w... |

### the helpers (lines 100-414)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 108 | 6 | `static void near(String label, double actual, double expected)` | Equal within a part in a billion of the expected (exactly, where it is nothing). |
| 115 | 4 | `static void quietly(Runnable r)` |  |
| 120 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 127 | 10 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat; no month played. |
| 139 | 20 | `static Game town(Path root, String name, Founding founding)` | A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()), in a folder of the harness's own. |
| 161 | 3 | `static void month(Game g)` | One month, quietly. |
| 166 | 3 | `static void step(Game g)` | One press of the clock, as the player's button makes it, quietly. |
| 176 | 6 | `static double posts(Game g, BuildingsTemplate t)` | The posts of one new building of `t` in this city, all bands: what Sector.staffing() asks the draw for, NewUnit.of()'s jobs. |
| 184 | 5 | `static double posts(BuildingsTemplate t)` | The posts of a building, all bands. |
| 191 | 5 | `static double posts(BuildingsTemplate t, WageBand band)` | The posts of a building in one wage band. |
| 197 | 5 | `static double sum(double[] a)` |  |
| 203 | 4 | `static boolean allZero(double[] a)` |  |
| 208 | 3 | `static String pct(double share)` |  |
| 212 | 3 | `static String bands(double[] a)` |  |
| 222 | 3 | `static long employedBeside(long spare)` | The workers the village already employs, in unskilled posts, beside {@code spare} workers it has no post for: so few of its labour force are out of work that the share stays under Migration.HEALTHY_OUT_OF_WORK, where ... |
| 227 | 3 | `static void seatOutOfWork(PopulationManager people, long workers, double share)` | The village as seat() has it, but employing as many beside its spare workers as leave this share of its labour force out of work (to a person). |
| 237 | 3 | `static void seat(PopulationManager people, long workers)` | The village's people and its workforce: this many spare workers, half of them with a diploma, beside the workers it employs in posts of its own (employedBeside()), the labour force WORKFORCE_SHARE of the people. |
| 242 | 8 | `static void seatWith(PopulationManager people, long workers, long employed)` | As seat(), employing this many beside the spare workers: the village's out-of-work share is the spare over the two. |
| 252 | 7 | `static long seatFor(PopulationManager people, Sector owner, BuildingsTemplate t, Predicate<Sector.Staffing> wanted)` | The fewest workers, seated, at which the works' staffing is as wanted; -1 where none up to MOST_WORKERS is (the village is left at MOST_WORKERS). |
| 261 | 4 | `static double addition(Game g, double posts)` | The order's addition to migration's target, in people: JOB_WEIGHT x posts x last month's residents a job x the pulls on the jobs' draw. |
| 267 | 3 | `static double gapNow(Game g)` | The gap migration's target stands over the population now, in people (below 0 where the population is the larger). |
| 272 | 4 | `static double closed(Migration m)` | The share of a gap that GATE_DRAW_MONTHS months close, at last month's crowding. |
| 278 | 5 | `static double uncappedPeople(Game g, double posts)` | The people an order of this many posts would draw over the window before the room: the part of the gap the order makes positive, closed. |
| 285 | 3 | `static double roomLeft(Migration m)` | The people the doors last month left free after its own arrivals; unbounded where the city has no census to place them against (lastRoom NaN). |
| 290 | 3 | `static double expectedPeople(Game g, double posts)` | The people Migration.drawFor() is to count for an order of this many posts: the uncapped draw, within the room, cut by the brake (WG2). |
| 295 | 4 | `static double expectedDraw(Game g, BuildingsTemplate t)` | The workers Migration.drawFor() is to bring for one of this works: the formula in the model's own constants and last-month scalars. |
| 301 | 3 | `static double workersOf(Game g, double people)` | The workers a number of drawn people come to in this city: the people over the population, times the workforce. |
| 306 | 3 | `static double pulls(Migration m)` | The pulls on the jobs' draw: the rent's and the crime's, never above 1; senior care lifts the homes' draw only (WG2). |
| 311 | 3 | `static double brake(Game g)` | The share of the arrivals migration's brake lets through in this city now: 1 at and under the healthy out-of-work rate, 0 at and past the stop (WG2). |
| 316 | 5 | `static void makeOld(Migration mig)` | Makes this Migration an old city's: Migration.BRAKE_FROM_MONTH months of the same steady wage recorded, so its brake acts and no tier declines (WG2). |
| 326 | 5 | `static void drawIsClear(String when, Game g, BuildingsTemplate t)` | A fixture of the sections that read the draw as the order's whole addition: the gap is positive without the order, the room does not bind, and the village is out of work under the healthy rate so the brake is whole (W... |
| 333 | 4 | `static boolean zeroToTheBit(double[] a)` | Whether every drawn figure is positive zero, to the bit. |
| 339 | 13 | **type** `static final class Ask` | One order a city could ask about: its sector, the building and its posts. |
| 344 | 5 | `Ask(Sector sector, BuildingsTemplate template, double posts)` _(in StaffingDrawCheck.Ask)_ |  |
| 350 | 1 | `Sector.Staffing staffing()` _(in StaffingDrawCheck.Ask)_ |  |
| 354 | 10 | `static java.util.List<Ask> asksOf(Game g)` | Every building of the city that has posts, with its sector: the orders the gate is asked about. |
| 370 | 3 | `static void roomMonth(Migration mig)` | Migration's month struck on households with doors enough: its crowding reads 1, and the pulls are the ones set. |
| 375 | 10 | `static void roomMonth(Migration mig, double seniorCoverage)` | ...with this share of the seniors who need a place in senior care placed (1 is full coverage: the senior care pull at its most). |
| 387 | 7 | `static void noRoomMonth(Migration mig)` | Migration's month struck on the same households and no door at all: the crowding floor, and its crowding reads 0. |
| 395 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1. a village with room: the draw passes what the spare does not (lines 415-459)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 42 | `static long sectionOne(Path root)` |  |

### 2. a city with no room: the draw refuses what the spare refuses (lines 460-498)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 36 | `static void sectionTwo(Path root)` |  |

### 3. the pulls turn the draw down (lines 499-567)

| line | len | member | says |
|---:|---:|---|---|
| 506 | 61 | `static void sectionThree(Path root, long workers)` | The pulls on the jobs' draw are the rent's and the crime's; senior care lifts the homes' draw only, since WG2 (section 10). |

### 4. the Shared Services Centre in a town without its bands is still refused (lines 568-679)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 109 | `static void sectionFour(Path root)` |  |

### 5. the hold's words name both counts (lines 680-712)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 30 | `static void sectionFive(Path root)` |  |

### 6. a loaded city's gate is its twin's (lines 713-828)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 113 | `static void sectionSix(Path root)` |  |

### 7. how often the draw opens an order (lines 829-886)

| line | len | member | says |
|---:|---:|---|---|
| 832 | 15 | `static int[] drawCounts(Game g)` | What the gate says of every order of a city: {asks, opened by the draw, passed on the spare alone, refused, refused by the spare alone}. |
| 848 | 38 | `static void sectionSeven(Path root)` |  |

### 8. the gap's positive part: only what the order makes positive is drawn (lines 887-965)

| line | len | member | says |
|---:|---:|---|---|
| 892 | 73 | `static void sectionEight(Path root)` |  |

### 9. within the room: no more than the doors last month left free (lines 966-1018)

| line | len | member | says |
|---:|---:|---|---|
| 968 | 50 | `static void sectionNine(Path root)` |  |

### 10. the brake and the pull rule turn the draw down as they turn arrivals (lines 1019-1124)

| line | len | member | says |
|---:|---:|---|---|
| 1022 | 3 | `static double unbraked(Game g, BuildingsTemplate t)` | The workers the village's draw would bring before the brake: the order's uncapped people, within the room, in this village's workers. |
| 1026 | 98 | `static void sectionTen(Path root, long workers)` |  |


# StaffingDrawCheck.java - 914 lines · 42 methods · 15 constants · harnesses

`ham/citybuildersim/StaffingDrawCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff
> Sector.MIN_STAFFABLE_TO_ORDER of its posts, and since GW1 that count is the spare workers and the workers migration would bring for
> the order's own posts within Sector.GATE_DRAW_MONTHS (Migration.drawFor()). Not part of the game.
> 
> WHY. GW0 (docs/studies/spec-staffing-gate.md) found about 15 refused orders a month in a young city while a fifth of its workers
> were jobless: the gate asked what the city could staff today, and the workers a new works would draw were not there until it opened.
> The gate now counts a year's draw. Each fixture below causes its case: a village with room whose spare workers are few, a city
> whose crowding reads 0, a month struck on a pull, a town without its graduate bands, a refusal worded, a saved city and its twin, and
> a played city counted ask by ask. Nothing here moves a premise to get green.
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
>   3. The pulls turn the draw down, by the ratio of their products (WG2, queued next, adds a migration brake and will add its section here).
>   4. The Shared Services Centre in a town without its graduate bands is refused by the share, not the band test; a band held closed
>      draws nobody and is the band nobody can fill, as before; and a college wage bid up draws the gate nobody more, to the bit.
>   5. The hold's words name both counts.
>   6. A loaded city's gate is its twin's, to the bit, in a city founded the default way and one founded the 1900 way, and the last
>      month's room it reads rides the save.
>   7. How often the draw opens an order in a played city, and that it never does in a month of no room.
>   8. The gap's positive part: where the population stands above migration's target even with the order, the draw is nothing, to the
>      bit; where the order takes the gap from negative to positive, it is that positive part's, and less than the whole addition's.
>   9. Within the room: where the doors last month left free are fewer than the draw would be, the draw is the room left.

**Uses:** [Sector](Sector.md) (63), [Migration](Migration.md) (34), [Game](Game.md) (31), [WageBand](WageBand.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (22), [PopulationManager](PopulationManager.md) (12), [JobType](JobType.md) (11), [BuildCard](BuildCard.md) (8), [Founding](Founding.md) (7), [GameFiles](GameFiles.md) (5), [FamilyModel](FamilyModel.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [LabourMarket](LabourMarket.md) (3), [PayTier](PayTier.md) (2), [RealEstate](RealEstate.md) (2), [EducationType](EducationType.md) (2), [BuildingManager](BuildingManager.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [TempDirs](TempDirs.md) (1), [ArrivalMix](ArrivalMix.md) (1)

## Sections

| line | section |
|---:|---|
| 91 | · the helpers |
| 342 | 1. a village with room: the draw passes what the spare does not |
| 387 | 2. a city with no room: the draw refuses what the spare refuses |
| 426 | 3. the pulls turn the draw down |
| 494 | 4. the Shared Services Centre in a town without its bands is still refused |
| 603 | 5. the hold's words name both counts |
| 635 | 6. a loaded city's gate is its twin's |
| 742 | 7. how often the draw opens an order in a played city |
| 790 | 8. the gap's positive part: only what the order makes positive is drawn |
| 862 | 9. within the room: no more than the doors last month left free |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `StaffingDrawCheck.SPARE_SHARE` | `.40` | The share of the works the village's spare workers alone fill: well under MIN_STAFFABLE_TO_ORDER, so only the draw can take it over the line. |
| 53 | `StaffingDrawCheck.WORKFORCE_SHARE` | `.5` | The workforce as a share of the people: what Migration.drawFor() turns a draw of residents into workers by (workforce over population). |
| 56 | `StaffingDrawCheck.MOST_WORKERS` | `5_000` | The most workers a fixture searches for a share. |
| 59 | `StaffingDrawCheck.TOWN_WORKERS` | `100` | The workers of the town whose graduate bands are empty, half of them with a diploma: few enough that the draw, not the spare, fills the centre's diploma posts. |
| 62 | `StaffingDrawCheck.HOUSEHOLD_PEOPLE` | `4_000, MONTH_JOBS = 6_000, MONTH_CAPACITY = 12_000, ROOMY_DOORS = 9_000` | The people Migration's month is struck on in the fixtures that put its crowding at 1 or at 0 (ArrivalMixCheck.draw()'s figures). |
| 65 | `StaffingDrawCheck.CENSUS_SIZES` | `6` | The household sizes a month's door census holds in the fixtures that give it one (ROOMY_DOORS split evenly over 1 to this). |
| 68 | `StaffingDrawCheck.EQUILIBRIUM_ADULTS` | `0` | The adult share Migration's month is struck on: 0 reads the equilibrium share, the one the fresh city's residents a job already use. |
| 71 | `StaffingDrawCheck.DEAR_RENT` | `2, DEAR_CRIME = 3` | Rent as a multiple of the target burden in section 3's dear month, and crime as a multiple of Canada's: affordabilityPull() 1/2, crimePull() 1/1.2. |
| 74 | `StaffingDrawCheck.RUINOUS_RENT` | `10` | Rent at ten times the target: the affordability pull is a tenth, and the draw with it. |
| 77 | `StaffingDrawCheck.MONTHS_BEFORE_SAVE` | `24, MONTHS_PLAYED = 120` | The months the twin cities are played before they are saved, and the months the played city is asked about. |
| 79 | `StaffingDrawCheck.NONE` | `WageBand.NONE.ordinal()` |  |
| 80 | `StaffingDrawCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal()` |  |
| 81 | `StaffingDrawCheck.COLLEGE` | `WageBand.COLLEGE.ordinal()` |  |
| 82 | `StaffingDrawCheck.UNIVERSITY` | `WageBand.UNIVERSITY.ordinal()` |  |
| 793 | `StaffingDrawCheck.MOST_PLAYED_MONTHS` | `60` | The most months a played city is run, one press of the clock at a time, to reach the month a section asks for. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 47 | `static PrintStream out, quiet` |  |
| 85 | `static boolean villagePassed` | Carried from section 1 to section 2: whether the village's order passed. |
| 88 | `static Sector.Staffing noRoomRefusal` | Carried from section 2 to section 5: the order the city with no room refused, and the works it was. |
| 89 | `static String noRoomName` |  |
| 273 | `final Sector sector` |  |
| 274 | `final BuildingsTemplate template` |  |
| 275 | `final double posts` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 871 | **type** `public class StaffingDrawCheck` | The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff Sector.MIN_STAFFABLE_TO_ORDER of its posts, and since GW1 that count is the spare workers and the w... |

### the helpers (lines 91-341)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 99 | 6 | `static void near(String label, double actual, double expected)` | Equal within a part in a billion of the expected (exactly, where it is nothing). |
| 106 | 4 | `static void quietly(Runnable r)` |  |
| 111 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 118 | 10 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat; no month played. |
| 130 | 20 | `static Game town(Path root, String name, Founding founding)` | A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()), in a folder of the harness's own. |
| 152 | 3 | `static void month(Game g)` | One month, quietly. |
| 157 | 3 | `static void step(Game g)` | One press of the clock, as the player's button makes it, quietly. |
| 162 | 5 | `static double posts(BuildingsTemplate t)` | The posts of a building, all bands. |
| 169 | 5 | `static double posts(BuildingsTemplate t, WageBand band)` | The posts of a building in one wage band. |
| 175 | 5 | `static double sum(double[] a)` |  |
| 181 | 4 | `static boolean allZero(double[] a)` |  |
| 186 | 3 | `static String pct(double share)` |  |
| 190 | 3 | `static String bands(double[] a)` |  |
| 195 | 5 | `static void seat(PopulationManager people, long workers)` | The village's people and its workforce: this many workers, half of them with a diploma, WORKFORCE_SHARE of the people. |
| 202 | 7 | `static long seatFor(PopulationManager people, Sector owner, BuildingsTemplate t, Predicate<Sector.Staffing> wanted)` | The fewest workers, seated, at which the works' staffing is as wanted; -1 where none up to MOST_WORKERS is (the village is left at MOST_WORKERS). |
| 211 | 4 | `static double addition(Game g, double posts)` | The order's addition to migration's target, in people: JOB_WEIGHT x posts x last month's residents a job x the pulls on the jobs' draw. |
| 217 | 3 | `static double gapNow(Game g)` | The gap migration's target stands over the population now, in people (below 0 where the population is the larger). |
| 222 | 4 | `static double closed(Migration m)` | The share of a gap that GATE_DRAW_MONTHS months close, at last month's crowding. |
| 228 | 5 | `static double uncappedPeople(Game g, double posts)` | The people an order of this many posts would draw over the window before the room: the part of the gap the order makes positive, closed. |
| 235 | 3 | `static double roomLeft(Migration m)` | The people the doors last month left free after its own arrivals; unbounded where the city has no census to place them against (lastRoom NaN). |
| 240 | 3 | `static double expectedPeople(Game g, double posts)` | The people Migration.drawFor() is to count for an order of this many posts: the uncapped draw, within the room. |
| 245 | 4 | `static double expectedDraw(Game g, BuildingsTemplate t)` | The workers Migration.drawFor() is to bring for one of this works: the formula in the model's own constants and last-month scalars. |
| 251 | 3 | `static double workersOf(Game g, double people)` | The workers a number of drawn people come to in this city: the people over the population, times the workforce. |
| 255 | 3 | `static double pulls(Migration m)` |  |
| 260 | 4 | `static void drawIsClear(String when, Game g, BuildingsTemplate t)` | A fixture of the sections that read the draw as the order's whole addition: the gap is positive without the order and the room does not bind. |
| 266 | 4 | `static boolean zeroToTheBit(double[] a)` | Whether every drawn figure is positive zero, to the bit. |
| 272 | 13 | **type** `static final class Ask` | One order a city could ask about: its sector, the building and its posts. |
| 277 | 5 | `Ask(Sector sector, BuildingsTemplate template, double posts)` _(in StaffingDrawCheck.Ask)_ |  |
| 283 | 1 | `Sector.Staffing staffing()` _(in StaffingDrawCheck.Ask)_ |  |
| 287 | 10 | `static java.util.List<Ask> asksOf(Game g)` | Every building of the city that has posts, with its sector: the orders the gate is asked about. |
| 303 | 10 | `static void roomMonth(Migration mig)` | Migration's month struck on households with doors enough: its crowding reads 1, and the pulls are the ones set. |
| 315 | 7 | `static void noRoomMonth(Migration mig)` | Migration's month struck on the same households and no door at all: the crowding floor, and its crowding reads 0. |
| 323 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. a village with room: the draw passes what the spare does not (lines 342-386)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 42 | `static long sectionOne(Path root)` |  |

### 2. a city with no room: the draw refuses what the spare refuses (lines 387-425)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 36 | `static void sectionTwo(Path root)` |  |

### 3. the pulls turn the draw down (lines 426-493)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 61 | `static void sectionThree(Path root, long workers)` | Main has no migration brake yet (a pull is the only thing that turns arrivals down): WG2, queued next, adds one, cutting the draw as it cuts arrivals, and will add its section here beside the rent and crime of this one. |

### 4. the Shared Services Centre in a town without its bands is still refused (lines 494-602)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 106 | `static void sectionFour(Path root)` |  |

### 5. the hold's words name both counts (lines 603-634)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 29 | `static void sectionFive(Path root)` |  |

### 6. a loaded city's gate is its twin's (lines 635-741)

| line | len | member | says |
|---:|---:|---|---|
| 637 | 104 | `static void sectionSix(Path root)` |  |

### 7. how often the draw opens an order in a played city (lines 742-789)

| line | len | member | says |
|---:|---:|---|---|
| 744 | 45 | `static void sectionSeven(Path root)` |  |

### 8. the gap's positive part: only what the order makes positive is drawn (lines 790-861)

| line | len | member | says |
|---:|---:|---|---|
| 795 | 66 | `static void sectionEight(Path root)` |  |

### 9. within the room: no more than the doors last month left free (lines 862-914)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 50 | `static void sectionNine(Path root)` |  |


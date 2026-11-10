# StaffingDrawCheck.java - 589 lines · 28 methods · 13 constants · harnesses

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
> What this has to prove:
>   1. In a village with room the draw passes a works the spare workers alone refuse, and the drawn workers are the formula's.
>   2. In a city whose crowding reads 0 the draw is nothing, the share is the spare's, and what the spare refuses is refused.
>   3. The pulls turn the draw down, by the ratio of their products (WG2, queued next, adds a migration brake and will add its section here).
>   4. The Shared Services Centre in a town without its graduate bands is refused by the share, not the band test; a band held closed
>      draws nobody and is the band nobody can fill, as before.
>   5. The hold's words name both counts.
>   6. A loaded city's gate is its twin's, to the bit, in a city founded the default way and one founded the 1900 way.
>   7. How often the draw opens an order in a played city, and that it never does in a month of no room.

**Uses:** [Sector](Sector.md) (51), [Game](Game.md) (20), [WageBand](WageBand.md) (18), [BuildingsTemplate](BuildingsTemplate.md) (17), [Migration](Migration.md) (17), [PopulationManager](PopulationManager.md) (9), [BuildCard](BuildCard.md) (7), [GameFiles](GameFiles.md) (5), [Founding](Founding.md) (5), [JobType](JobType.md) (5), [FamilyModel](FamilyModel.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [PayTier](PayTier.md) (2), [RealEstate](RealEstate.md) (2), [LabourMarket](LabourMarket.md) (2), [BuildingManager](BuildingManager.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [TempDirs](TempDirs.md) (1), [ArrivalMix](ArrivalMix.md) (1)

## Sections

| line | section |
|---:|---|
| 76 | · the helpers |
| 244 | 1. a village with room: the draw passes what the spare does not |
| 279 | 2. a city with no room: the draw refuses what the spare refuses |
| 318 | 3. the pulls turn the draw down |
| 382 | 4. the Shared Services Centre in a town without its bands is still refused |
| 445 | 5. the hold's words name both counts |
| 477 | 6. a loaded city's gate is its twin's |
| 542 | 7. how often the draw opens an order in a played city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `StaffingDrawCheck.SPARE_SHARE` | `.40` | The share of the works the village's spare workers alone fill: well under MIN_STAFFABLE_TO_ORDER, so only the draw can take it over the line. |
| 41 | `StaffingDrawCheck.WORKFORCE_SHARE` | `.5` | The workforce as a share of the people: what Migration.drawFor() turns a draw of residents into workers by (workforce over population). |
| 44 | `StaffingDrawCheck.MOST_WORKERS` | `5_000` | The most workers a fixture searches for a share. |
| 47 | `StaffingDrawCheck.TOWN_WORKERS` | `100` | The workers of the town whose graduate bands are empty, half of them with a diploma: few enough that the draw, not the spare, fills the centre's diploma posts. |
| 50 | `StaffingDrawCheck.HOUSEHOLD_PEOPLE` | `4_000, MONTH_JOBS = 6_000, MONTH_CAPACITY = 12_000, ROOMY_DOORS = 9_000` | The people Migration's month is struck on in the fixtures that put its crowding at 1 or at 0 (ArrivalMixCheck.draw()'s figures). |
| 53 | `StaffingDrawCheck.EQUILIBRIUM_ADULTS` | `0` | The adult share Migration's month is struck on: 0 reads the equilibrium share, the one the fresh city's residents a job already use. |
| 56 | `StaffingDrawCheck.DEAR_RENT` | `2, DEAR_CRIME = 3` | Rent as a multiple of the target burden in section 3's dear month, and crime as a multiple of Canada's: affordabilityPull() 1/2, crimePull() 1/1.2. |
| 59 | `StaffingDrawCheck.RUINOUS_RENT` | `10` | Rent at ten times the target: the affordability pull is a tenth, and the draw with it. |
| 62 | `StaffingDrawCheck.MONTHS_BEFORE_SAVE` | `24, MONTHS_PLAYED = 120` | The months the twin cities are played before they are saved, and the months the played city is asked about. |
| 64 | `StaffingDrawCheck.NONE` | `WageBand.NONE.ordinal()` |  |
| 65 | `StaffingDrawCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal()` |  |
| 66 | `StaffingDrawCheck.COLLEGE` | `WageBand.COLLEGE.ordinal()` |  |
| 67 | `StaffingDrawCheck.UNIVERSITY` | `WageBand.UNIVERSITY.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `static int fails` |  |
| 35 | `static PrintStream out, quiet` |  |
| 70 | `static boolean villagePassed` | Carried from section 1 to section 2: whether the village's order passed. |
| 73 | `static Sector.Staffing noRoomRefusal` | Carried from section 2 to section 5: the order the city with no room refused, and the works it was. |
| 74 | `static String noRoomName` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 558 | **type** `public class StaffingDrawCheck` | The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff Sector.MIN_STAFFABLE_TO_ORDER of its posts, and since GW1 that count is the spare workers and the w... |

### the helpers (lines 76-243)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 84 | 6 | `static void near(String label, double actual, double expected)` | Equal within a part in a billion of the expected (exactly, where it is nothing). |
| 91 | 4 | `static void quietly(Runnable r)` |  |
| 96 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 103 | 10 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat; no month played. |
| 115 | 20 | `static Game town(Path root, String name, Founding founding)` | A village founded as given: roads, homes, shops, farms, bakeries, power and water (FittingCheck.village()), in a folder of the harness's own. |
| 137 | 3 | `static void month(Game g)` | One month, quietly. |
| 142 | 3 | `static void step(Game g)` | One press of the clock, as the player's button makes it, quietly. |
| 147 | 5 | `static double posts(BuildingsTemplate t)` | The posts of a building, all bands. |
| 154 | 5 | `static double posts(BuildingsTemplate t, WageBand band)` | The posts of a building in one wage band. |
| 160 | 5 | `static double sum(double[] a)` |  |
| 166 | 4 | `static boolean allZero(double[] a)` |  |
| 171 | 3 | `static String pct(double share)` |  |
| 175 | 3 | `static String bands(double[] a)` |  |
| 180 | 5 | `static void seat(PopulationManager people, long workers)` | The village's people and its workforce: this many workers, half of them with a diploma, WORKFORCE_SHARE of the people. |
| 187 | 7 | `static long seatFor(PopulationManager people, Sector owner, BuildingsTemplate t, Predicate<Sector.Staffing> wanted)` | The fewest workers, seated, at which the works' staffing is as wanted; -1 where none up to MOST_WORKERS is (the village is left at MOST_WORKERS). |
| 196 | 8 | `static double expectedDraw(Game g, BuildingsTemplate t)` | The workers Migration.drawFor() is to bring for one of this works: the formula in the model's own constants and last-month scalars. |
| 205 | 3 | `static double pulls(Migration m)` |  |
| 210 | 7 | `static void roomMonth(Migration mig)` | Migration's month struck on households with doors enough: its crowding reads 1, and the pulls are the ones set. |
| 219 | 7 | `static void noRoomMonth(Migration mig)` | Migration's month struck on the same households and no door at all: the crowding floor, and its crowding reads 0. |
| 227 | 16 | `public static void main(String[] args) throws Exception` |  |

### 1. a village with room: the draw passes what the spare does not (lines 244-278)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 32 | `static long sectionOne(Path root)` |  |

### 2. a city with no room: the draw refuses what the spare refuses (lines 279-317)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 36 | `static void sectionTwo(Path root)` |  |

### 3. the pulls turn the draw down (lines 318-381)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 57 | `static void sectionThree(Path root, long workers)` | Main has no migration brake yet (a pull is the only thing that turns arrivals down): WG2, queued next, adds one, cutting the draw as it cuts arrivals, and will add its section here beside the rent and crime of this one. |

### 4. the Shared Services Centre in a town without its bands is still refused (lines 382-444)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 60 | `static void sectionFour(Path root)` |  |

### 5. the hold's words name both counts (lines 445-476)

| line | len | member | says |
|---:|---:|---|---|
| 447 | 29 | `static void sectionFive(Path root)` |  |

### 6. a loaded city's gate is its twin's (lines 477-541)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 62 | `static void sectionSix(Path root)` |  |

### 7. how often the draw opens an order in a played city (lines 542-589)

| line | len | member | says |
|---:|---:|---|---|
| 544 | 45 | `static void sectionSeven(Path root)` |  |


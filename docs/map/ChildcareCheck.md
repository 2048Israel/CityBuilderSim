# ChildcareCheck.java - 641 lines · 22 methods · 7 constants · harnesses

`ham/citybuildersim/ChildcareCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> Childcare resized (0.7.71, batch N2): the three childcare centres, the
> build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS
> THE NEED), a city's old daycares kept as their ids' new centres, and the
> test player's childcare rule.
> 
> WHY. Jerus: "one city had 5k daycares and 2k residential buildings,
> hilarious, the numbers children and housing wise make sense ... i think we
> need to resize those, daycares are childcares and childcares are even
> bigger." A Home Daycare held 8 children and was the cheapest a place to
> put up, so the advice ordered them by the hundred - 629 in his city of
> 24,000, 2,951 in the playtest's at m4,000 with a childcare rule on
> (runs/fixN2-notes.md). Since 0.7.71 ids 15, 16 and 17 are centres of 80,
> 220 and 360 places, a place cheaper the bigger, and the advice ranks a
> living care building by its whole order over the places the need lacks.
> 
> What this has to prove:
>   1. THREE CENTRES, A PLACE CHEAPER THE BIGGER: ids 15, 16 and 17 are
>      childcare, named centres (no "Daycare" left), in rising size; a place
>      in a bigger one costs less to build all in, to keep, to stand on and to
>      staff, and costs less to run in a played city; every one keeps between
>      Ontario's infant and preschool ratios of children to an adult.
>   2. THE SIZE THAT FITS THE NEED: in a town short of thousands of places
>      and one short of hundreds the advice's childcare card is the order
>      whose cost over its life - quote, ground and running (0.7.101, Jerus's
>      A4; its quote and ground until then) - over the places it will fill
>      over that life (the places lacking at its projection until then) is
>      the least of the three, to the bit - short of thousands, not the
>      Small Childcare Centre; with the gap brought under one small centre's
>      places, the least count of its centre that closes it (until 0.7.101
>      the Small Childcare Centres, where a place in one costs more than in
>      the Large; since then the centre the town fills over its life); and
>      the card's figure is the advice's own.
>   3. A CITY'S BUILDINGS KEEP THEIR TYPE: a save holds its childcare by id,
>      so a city of the old daycares loads as the same count of the ids'
>      centres, with their places, posts and ground; overbuilt so, the advice
>      says nothing for childcare and NEEDS YOU does not list it.
>   4. THE TEST PLAYER'S RULE (-Dplaytest.childcare): where NEEDS YOU lists
>      childcare it orders the advice's card, building and count; overbuilt,
>      nothing.
>   5. THE MAP DRAWS A CENTRE AS CARE: id 15 is no longer drawn as a home;
>      home care (22) still is.
>   6. THE PLACES A CITY WANTS AT REAL DEMAND (0.7.139, batch CC1): a baby
>      wants CareType.CHILDCARE_UNDER_SIX of a place and a child
>      CHILDCARE_SIX_TO_TWELVE, nobody else any; a pyramid of known bands
>      wants exactly those places, under a third of the place a head the
>      rule wanted until then; the founding's nursery is twelve hundred
>      people's at those weights; the Large Childcare Centres that serve a
>      known city whole are its places over one centre's, one fewer short;
>      and NEEDS YOU's childcare row and the model's month read those places
>      as the demand.
> 
> WHY 6. Jerus, 2026-10-10, on the map: "real life doesnt have that many
> like even with the expanded facilities i still check the map and its quite
> the bit of childcares". Until 0.7.139 childcare wanted a whole place for
> every child 0 to 12; in Canada 32% of the under-6s are in centre-based care
> and 18.2% of the 6-to-12s in a before- or after-school programme
> (CareType.placesPerHead()).
> 
> Every fixture causes its condition. The fixtures (FS15): founding(), a founded city with its depots and homes stood and
> no month played, its lack placed by hand (the people's pyramid, their workforce and a year's history), for sections 2
> ... (3 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (36), [CareType](CareType.md) (29), [Game](Game.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (23), [AgeBand](AgeBand.md) (17), [CityNeeds](CityNeeds.md) (15), [GameFiles](GameFiles.md) (9), [PopulationCohorts](PopulationCohorts.md) (9), [JobType](JobType.md) (6), [BuildingManager](BuildingManager.md) (6), [BuildingVisual](BuildingVisual.md) (6), [Founding](Founding.md) (5), [LongPlaytest](LongPlaytest.md) (5), [Healthcare](Healthcare.md) (4), [BuildingType](BuildingType.md) (4), [Health](Health.md) (4), [BuildCard](BuildCard.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [WorksValue](WorksValue.md) (2), [Sector](Sector.md) (1), [PopulationManager](PopulationManager.md) (1), [HistorySave](HistorySave.md) (1), [TempDirs](TempDirs.md) (1)

**Used by (6):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [HydroCheck](HydroCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 313 | · 1 |
| 362 | · 2 |
| 462 | · 3 |
| 513 | · 4 |
| 555 | · 5 |
| 572 | · 6 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `ChildcareCheck.ONTARIO_INFANTS_PER_ADULT` | `10.0 / 3` | Children to an adult in Ontario's infant groups, 3 adults to 10 (O. |
| 92 | `ChildcareCheck.ONTARIO_PRESCHOOL_PER_ADULT` | `8` | ...and in its preschool groups, 1 to 8: the fewest. |
| 95 | `ChildcareCheck.SMALL` | `15, CENTRE = 16, LARGE = 17` | The three centres' ids in buildings.json: the Small Childcare Centre, the Childcare Centre and the Large. |
| 98 | `ChildcareCheck.THOUSANDS_LARGES` | `8` | FS15: the places past the founding's a city short of thousands lacks, in Large Childcare Centres: eight, past the five the section asks. |
| 101 | `ChildcareCheck.HUNDREDS_LARGES` | `2.5` | ...and one short of hundreds: two and a half, past the one it asks. |
| 104 | `ChildcareCheck.GROWING` | `1.3` | ...whose population grew by this much over the year before (founding()'s history), so its projection grows. |
| 107 | `ChildcareCheck.CHILDCARE` | `BuildAdvice.Measure.care(CareType.CHILDCARE)` | The advice's measure for childcare, which the sections here read the need, the site and the card through. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `static int fails` |  |
| 85 | `static PrintStream out` |  |
| 86 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 560 | **type** `public class ChildcareCheck` | Childcare resized (0.7.71, batch N2): the three childcare centres, the build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS THE NEED), a city's old daycares kept as their ids' new centres, and the t... |
| 109 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 114 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 120 | 5 | `static void quietly(Runnable r)` |  |
| 126 | 4 | `static BuildingsTemplate byId(Game g, int id)` |  |
| 131 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 137 | 5 | `static int staff(BuildingsTemplate t)` |  |
| 152 | 3 | `static Game town(Path root, String name)` | The fixture: BuildAdviceCheck's short city with no daycare of its own - nine hundred houses, its schools, college, university and four medical schools for the posts that bring the people, every sector held - played th... |
| 167 | 38 | `static Game town(Path root, String name, int k)` | ...with every building `k` times over: at k 4 a city of thousands of children (calibrated: scratch-n2/h/cc-3.txt). |
| 216 | 34 | `static Game founding(Path root, String name, double larges, double growth)` | FS15: sections 2's and 4's city, its lack caused. |
| 252 | 5 | `static long peopleWanting(double places)` | The people of a settled pyramid (equilibriumShare()) who want `places` of childcare at CareType's weights. |
| 258 | 3 | `static CityNeeds.Need need(Game g)` |  |
| 262 | 5 | `static BuildAdvice.Suggestion advice(Game g)` |  |
| 269 | 4 | `static double lacks(Game g, BuildAdvice.Ahead p)` | The places the need lacks at a suggestion's own projection: its demand less what is on site. |
| 285 | 1 | **type** `record Weighed(BuildingsTemplate t, long count, boolean closes, double per, double net)` | One centre weighed as suggestFor() weighs it: the advice's own card with the other two centres left out (its skip), so its count at the demand its own order opens to, whether it closes the need, and its plan's figure. |
| 287 | 8 | `static Weighed weigh(Game g, BuildingsTemplate t)` |  |
| 296 | 16 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 313-361)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 46 | `static void threeCentres(Path root)` |  |

### 2 (lines 362-461)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 70 | `static void sizeThatFits(Path root)` |  |
| 440 | 21 | `static void theLeast(Game g, BuildAdvice.Suggestion s, String when)` | The suggestion is the centre whose order over its life over the places it will fill is the least (0.7.101; its order over the places lacking until then), one that closes the need before one that cannot (the advice's t... |

### 3 (lines 462-512)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 48 | `static void keepTheirType(Path root) throws Exception` |  |

### 4 (lines 513-554)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 39 | `static void thePlayer(Path root)` |  |

### 5 (lines 555-571)

| line | len | member | says |
|---:|---:|---|---|
| 557 | 14 | `static void theMap(Path root)` |  |

### 6 (lines 572-641)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 67 | `static void realDemand(Path root)` |  |


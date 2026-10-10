# ChildcareCheck.java - 448 lines · 19 methods · 4 constants · harnesses

`ham/citybuildersim/ChildcareCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> 
> Every fixture causes its condition.

**Uses:** [BuildAdvice](BuildAdvice.md) (53), [BuildingsTemplate](BuildingsTemplate.md) (21), [Game](Game.md) (20), [CityNeeds](CityNeeds.md) (14), [CareType](CareType.md) (11), [JobType](JobType.md) (6), [BuildingVisual](BuildingVisual.md) (6), [GameFiles](GameFiles.md) (5), [LongPlaytest](LongPlaytest.md) (5), [BuildingType](BuildingType.md) (4), [BuildingManager](BuildingManager.md) (4), [BuildCard](BuildCard.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [Founding](Founding.md) (2), [Sector](Sector.md) (1), [Healthcare](Healthcare.md) (1)

**Used by (1):** [AutoBuildCheck](AutoBuildCheck.md)

## Sections

| line | section |
|---:|---|
| 210 | · 1 |
| 259 | · 2 |
| 343 | · 3 |
| 392 | · 4 |
| 432 | · 5 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `ChildcareCheck.ONTARIO_INFANTS_PER_ADULT` | `10.0 / 3` | Children to an adult in Ontario's infant groups, 3 adults to 10 (O. |
| 70 | `ChildcareCheck.ONTARIO_PRESCHOOL_PER_ADULT` | `8` | ...and in its preschool groups, 1 to 8: the fewest. |
| 73 | `ChildcareCheck.SMALL` | `15, CENTRE = 16, LARGE = 17` | The three centres' ids in buildings.json: the Small Childcare Centre, the Childcare Centre and the Large. |
| 76 | `ChildcareCheck.CHILDCARE` | `BuildAdvice.Measure.care(CareType.CHILDCARE)` | The advice's measure for childcare, which the sections here read the need, the site and the card through. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 63 | `static PrintStream out` |  |
| 64 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 389 | **type** `public class ChildcareCheck` | Childcare resized (0.7.71, batch N2): the three childcare centres, the build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS THE NEED), a city's old daycares kept as their ids' new centres, and the t... |
| 78 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 83 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 89 | 5 | `static void quietly(Runnable r)` |  |
| 95 | 4 | `static BuildingsTemplate byId(Game g, int id)` |  |
| 100 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 106 | 5 | `static int staff(BuildingsTemplate t)` |  |
| 119 | 3 | `static Game town(Path root, String name)` | The fixture: BuildAdviceCheck's short city with no daycare of its own - nine hundred houses, its schools, college, university and four medical schools for the posts that bring the people, every sector held - played th... |
| 124 | 22 | `static Game town(Path root, String name, int k)` | ...with every building `k` times over: at k 4 a city of thousands of children (calibrated: scratch-n2/h/cc-3.txt). |
| 147 | 3 | `static CityNeeds.Need need(Game g)` |  |
| 151 | 5 | `static BuildAdvice.Suggestion advice(Game g)` |  |
| 158 | 4 | `static double lacks(Game g, BuildAdvice.Ahead p)` | The places the need lacks at a suggestion's own projection: its demand less what is on site. |
| 164 | 1 | **type** `record Weighed(BuildingsTemplate t, int count, BuildAdvice.Ahead p, double per)` | One centre weighed as suggestFor() weighs it: its count at the demand it opens to, and its figure. |
| 166 | 27 | `static Weighed weigh(Game g, BuildingsTemplate t)` |  |
| 194 | 15 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 210-258)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 46 | `static void threeCentres(Path root)` |  |

### 2 (lines 259-342)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 65 | `static void sizeThatFits(Path root)` |  |
| 328 | 14 | `static void theLeast(Game g, BuildAdvice.Suggestion s, String when)` | The suggestion is the centre whose order over its life over the places it will fill is the least (0.7.101; its order over the places lacking until then), each recomputed here, to the bit. |

### 3 (lines 343-391)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 46 | `static void keepTheirType(Path root) throws Exception` |  |

### 4 (lines 392-431)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 37 | `static void thePlayer(Path root)` |  |

### 5 (lines 432-448)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 14 | `static void theMap(Path root)` |  |


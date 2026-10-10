# ChildcareCheck.java - 428 lines · 19 methods · 4 constants · harnesses

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
>      whose quote and ground over the places lacking are the least of the
>      three, to the bit - short of thousands, not the Small Childcare
>      Centre; with the gap brought under one small centre's places, the
>      least count of Small Childcare Centres that closes it - where a place
>      in one costs more than in the Large; and the card's figure is the
>      advice's own.
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

**Uses:** [BuildAdvice](BuildAdvice.md) (43), [BuildingsTemplate](BuildingsTemplate.md) (20), [Game](Game.md) (20), [CityNeeds](CityNeeds.md) (14), [CareType](CareType.md) (11), [JobType](JobType.md) (6), [BuildingVisual](BuildingVisual.md) (6), [GameFiles](GameFiles.md) (5), [LongPlaytest](LongPlaytest.md) (5), [BuildingType](BuildingType.md) (4), [BuildingManager](BuildingManager.md) (4), [BuildCard](BuildCard.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [Founding](Founding.md) (2), [Sector](Sector.md) (1), [Healthcare](Healthcare.md) (1)

**Used by (1):** [AutoBuildCheck](AutoBuildCheck.md)

## Sections

| line | section |
|---:|---|
| 192 | · 1 |
| 241 | · 2 |
| 323 | · 3 |
| 372 | · 4 |
| 412 | · 5 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `ChildcareCheck.ONTARIO_INFANTS_PER_ADULT` | `10.0 / 3` | Children to an adult in Ontario's infant groups, 3 adults to 10 (O. |
| 67 | `ChildcareCheck.ONTARIO_PRESCHOOL_PER_ADULT` | `8` | ...and in its preschool groups, 1 to 8: the fewest. |
| 70 | `ChildcareCheck.SMALL` | `15, CENTRE = 16, LARGE = 17` | The three centres' ids in buildings.json: the Small Childcare Centre, the Childcare Centre and the Large. |
| 73 | `ChildcareCheck.CHILDCARE` | `BuildAdvice.Measure.care(CareType.CHILDCARE)` | The advice's measure for childcare, which the sections here read the need, the site and the card through. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 59 | `static int fails` |  |
| 60 | `static PrintStream out` |  |
| 61 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 372 | **type** `public class ChildcareCheck` | Childcare resized (0.7.71, batch N2): the three childcare centres, the build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS THE NEED), a city's old daycares kept as their ids' new centres, and the t... |
| 75 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 80 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 86 | 5 | `static void quietly(Runnable r)` |  |
| 92 | 4 | `static BuildingsTemplate byId(Game g, int id)` |  |
| 97 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 103 | 5 | `static int staff(BuildingsTemplate t)` |  |
| 116 | 3 | `static Game town(Path root, String name)` | The fixture: BuildAdviceCheck's short city with no daycare of its own - nine hundred houses, its schools, college, university and four medical schools for the posts that bring the people, every sector held - played th... |
| 121 | 22 | `static Game town(Path root, String name, int k)` | ...with every building `k` times over: at k 4 a city of thousands of children (calibrated: scratch-n2/h/cc-3.txt). |
| 144 | 3 | `static CityNeeds.Need need(Game g)` |  |
| 148 | 5 | `static BuildAdvice.Suggestion advice(Game g)` |  |
| 155 | 4 | `static double lacks(Game g, BuildAdvice.Ahead p)` | The places the need lacks at a suggestion's own projection: its demand less what is on site. |
| 161 | 1 | **type** `record Weighed(BuildingsTemplate t, int count, BuildAdvice.Ahead p, double per)` | One centre weighed as suggestFor() weighs it: its count at the demand it opens to, and its figure. |
| 163 | 12 | `static Weighed weigh(Game g, BuildingsTemplate t)` |  |
| 176 | 15 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 192-240)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 46 | `static void threeCentres(Path root)` |  |

### 2 (lines 241-322)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 64 | `static void sizeThatFits(Path root)` |  |
| 309 | 13 | `static void theLeast(Game g, BuildAdvice.Suggestion s, String when)` | The suggestion is the centre with the least order over the places lacking, each recomputed here, to the bit. |

### 3 (lines 323-371)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 46 | `static void keepTheirType(Path root) throws Exception` |  |

### 4 (lines 372-411)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 37 | `static void thePlayer(Path root)` |  |

### 5 (lines 412-428)

| line | len | member | says |
|---:|---:|---|---|
| 414 | 14 | `static void theMap(Path root)` |  |


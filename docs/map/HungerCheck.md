# HungerCheck.java - 620 lines · 30 methods · 15 constants · harnesses

`ham/citybuildersim/HungerCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> A town with no shop goes hungry, the screens say so, and the price index does not wait for the shop (HG1).
> 
> Jerus's 1900 city (0.9.219) had no shop in 79 months and went 97.5% hungry: its posts outran its people from the founding,
> the 1900 arrival mix holds few diplomas, and the diploma band was over-posted every month, so the spare-worker test refused
> every shop format and nothing in the hunger moved it. The header said "Nothing needs you", and the price index never based
> (no month had both rent and groceries), so the policy rule read no inflation for ever.
> 
> WHAT THIS HAS TO PROVE, in the order of the sections; each fixture CAUSES its condition (a city founded the 1900 way through
> newGame, a bare PriceIndex fed by hand) and asserts against the model's own constants:
> 
>   1. THE TRAP, CAUSED. A city founded the 1900 way has no Retail building and an over-posted diploma band, so the ordinary
>      gate (another sector's staffing() of the same template) refuses a Convenience Store; while the town goes short of baskets
>      Retail's gate passes it on the share the city's fill would staff (Sector.shareAtTheFill()) and its planner orders one.
>      BOTH SIDES: a Filling Station (coverage 0) is not lifted, and once the shops cover the households' need Retail's
>      reading of the Convenience Store is the ordinary one again.
> 
>   2. THE TRAP PLAYED. From such a city, within PLAYED_BOUND months a shop stands, its shelf holds stock, baskets are sold,
>      and the households short of stock fall under CityNeeds.HUNGER_YELLOW.
> 
>   3. THE SCREENS. At red hunger from an empty shelf CityNeeds.food() is level 2, FOOD, door SHOPS, and NEEDS YOU lists it
>      (so the header cannot say "Nothing needs you"); with the people priced out the door is FOOD_PROMISE; a fed city reads
>      level 0 and lists no FOOD row; and the row follows the thresholds in every month any city here played.
> 
>   4. THE INDEX. A basket with rent and a good bought beside it (meals, luxury, cars, fuels) bases at the SETTLING_MONTHS-th
>      month with no groceries in it; rent alone, the city's own fees and a half that is priced or bought but not both never
>      base it (BOTH HALVES); groceries join a year after they appear, with no jump in the level; the rule then reads a
>      real rate; and a basket with groceries from the start bases and never relinks, as before.
> 
>   5. THE MONEY AUDIT is exact to the cent in every month section 2's city played (and in the months of the others).
> 
> Every city lives in GameFiles.scratch(); none touches a save slot.

**Uses:** [PriceIndex](PriceIndex.md) (88), [CityNeeds](CityNeeds.md) (43), [Game](Game.md) (13), [Sector](Sector.md) (11), [Retail](Retail.md) (6), [HouseholdBalance](HouseholdBalance.md) (5), [Founding](Founding.md) (4), [MoneyAudit](MoneyAudit.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [WageBand](WageBand.md) (1), [GameFiles](GameFiles.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingManager](BuildingManager.md) (1), [PopulationManager](PopulationManager.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 68 | · the fixtures' own figures |
| 199 | 1. the trap, caused |
| 269 | 2. the trap played |
| 310 | 3. the screens |
| 380 | 4. the index |
| 608 | 5. the money audit |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `HungerCheck.THE_1900_WAY` | `Founding.defaults().withUnfitted(true)` |  |
| 71 | `HungerCheck.CONVENIENCE` | `"Convenience Store", STATION = "Filling Station"` |  |
| 72 | `HungerCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal()` |  |
| 75 | `HungerCheck.HUNT_BOUND` | `36` | The most months a city founded the 1900 way is played for the first month the city's fill would staff a shop. |
| 77 | `HungerCheck.PLAYED_BOUND` | `12` | The months section 2 gives the town to stand a shop, stock it, sell baskets and be fed. |
| 79 | `HungerCheck.COVER_BOUND` | `50` | The most Convenience Stores section 1 stands to cover the households' need. |
| 81 | `HungerCheck.FED_BOUND` | `40` | The most months a default city is played for a month with a shop selling and nobody hungry. |
| 83 | `HungerCheck.PRICE_UP` | `20` | The shelf price section 3 multiplies, and the months it plays for the people to be priced out. |
| 84 | `HungerCheck.PRICED_MONTHS` | `2` |  |
| 180 | `HungerCheck.playedAudit` | `new Audit(), otherAudit = new Audit()` |  |
| 181 | `HungerCheck.watch` | `new FoodWatch()` |  |
| 382 | `HungerCheck.LEAD` | `5` |  |
| 383 | `HungerCheck.RENT_PRICE` | `100, RENT_SPEND = 1000, MEAL_PRICE = 20, MEAL_SPEND = 1000, SHELF_PRICE = 2, ...` |  |
| 384 | `HungerCheck.GOOD_PRICE` | `50, GOOD_SPEND = 1000, FEE_PRICE = 10, FEE_SPEND = 100` |  |
| 385 | `HungerCheck.RENT_DRIFT` | `.005, MEAL_DRIFT =.003, MEAL_RISE =.01, SHELF_STEP =.10` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 47 | `static PrintStream out` |  |
| 48 | `static PrintStream quiet` |  |
| 95 | `int months, missing, broken` |  |
| 96 | `double worst` |  |
| 97 | `String first` |  |
| 115 | `final int[] levels` |  |
| 116 | `int shops, promises, months` |  |
| 117 | `boolean follows` |  |
| 118 | `String first` |  |
| 164 | `final Game game` |  |
| 165 | `final Retail retail` |  |
| 166 | `final BuildingsTemplate convenience` |  |
| 167 | `int months` |  |
| 183 | `static Trap played` | Section 2's city, once fed: section 3 prices its people out. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 577 | **type** `public class HungerCheck` | A town with no shop goes hungry, the screens say so, and the price index does not wait for the shop (HG1). |
| 50 | 1 | `static void check(String label, boolean ok)` |  |
| 52 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 58 | 3 | `static void near(String label, double actual, double expected, double tol)` | A figure against the harness's own, to an absolute tolerance. |
| 62 | 5 | `static void quietly(Runnable r)` |  |

### the fixtures' own figures (lines 68-198)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 5 | `static Game found(String label, Founding founding)` | A new city founded as the player's door founds it (newGame(founding)), on a scratch folder, quietly. |
| 94 | 18 | **type** `static final class Audit` | What the money audit said of every month played through play(): the worst residual over its tolerance, and the months that broke it. |
| 99 | 12 | `void take(Game g)` _(in HungerCheck.Audit)_ |  |
| 114 | 24 | **type** `static final class FoodWatch` | Every month played through play(): CityNeeds.food() against the thresholds and the doors, and which levels and doors were met. |
| 120 | 17 | `void look(Game g)` _(in HungerCheck.FoodWatch)_ |  |
| 139 | 4 | `static boolean hasFood(List<CityNeeds.Need> needs)` |  |
| 145 | 5 | `static void play(Game g, Audit audit, FoodWatch watch)` | One month of the clock, as the player's button makes it, and what is watched of it. |
| 152 | 5 | `static long retailBuildings(Game g)` | The buildings Retail owns, standing and on site. |
| 163 | 16 | **type** `static final class Trap` | A city founded the 1900 way and played, month by month, up to the first month Retail's gate would pass a Convenience Store: the founding has Retail no building and its posts outrun its people, so nothing but the clock... |
| 169 | 9 | `Trap(String label, Audit audit, FoodWatch watch)` _(in HungerCheck.Trap)_ |  |
| 185 | 13 | `public static void main(String[] args) throws Exception` |  |

### 1. the trap, caused (lines 199-268)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 67 | `static void sectionOne()` |  |

### 2. the trap played (lines 269-309)

| line | len | member | says |
|---:|---:|---|---|
| 271 | 38 | `static void sectionTwo()` |  |

### 3. the screens (lines 310-379)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 62 | `static void sectionThree()` |  |
| 375 | 4 | `static CityNeeds.Need rowOf(List<CityNeeds.Need> all)` |  |

### 4. the index (lines 380-607)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 7 | `static double[] prices(double groceries, double rent, double meals)` |  |
| 395 | 1 | `static double[] spends(double groceries, double rent, double meals)` |  |
| 397 | 3 | `static void take(PriceIndex px, int month, double[] prices, double[] spends)` |  |
| 401 | 5 | `static double[] filled(int n, double value)` |  |
| 407 | 8 | `static void sectionFour()` |  |
| 417 | 39 | `static void basesWithoutGroceries()` | (a) rent and a good bought beside it base the basket at the SETTLING_MONTHS-th such month, groceries absent. |
| 458 | 8 | `static boolean neverBased(double[] prices, double[] spends, double[] fees, double[] feeSpends)` | Whether the index never bases in three settlings of the same month fed to it. |
| 468 | 18 | `static void neverBasesOnHalf()` | (b) BOTH HALVES: rent beside nothing from outside the loop never bases the basket. |
| 487 | 1 | `static double driftRent(int month)` |  |
| 489 | 1 | `static double driftMeal(int month)` |  |
| 492 | 60 | `static void groceriesJoin()` | (c) groceries appearing after a basing with none join it once a whole year of them is in the weights, with the level running on. |
| 554 | 28 | `static void theRateIsReal()` | (d) with no groceries the rule still reads a real rate: the one the meals' price makes of the level, a year on. |
| 584 | 23 | `static void groceriesFromTheStart()` | (e) a basket with groceries from the start is as it was: both halves at SETTLING_MONTHS, groceries in it from the basing, no relink. |

### 5. the money audit (lines 608-620)

| line | len | member | says |
|---:|---:|---|---|
| 610 | 10 | `static void sectionFive()` |  |


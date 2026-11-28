# HungerCheck.java - 638 lines · 30 methods · 16 constants · harnesses

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
| 202 | 1. the trap, caused |
| 287 | 2. the trap played |
| 328 | 3. the screens |
| 398 | 4. the index |
| 626 | 5. the money audit |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `HungerCheck.THE_1900_WAY` | `Founding.defaults().withUnfitted(true)` |  |
| 71 | `HungerCheck.CONVENIENCE` | `"Convenience Store", STATION = "Filling Station"` |  |
| 72 | `HungerCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal()` |  |
| 75 | `HungerCheck.HUNT_BOUND` | `36` | The most months a city founded the 1900 way is played for the first month the city's fill would staff a shop. |
| 77 | `HungerCheck.PLAYED_BOUND` | `12` | The months section 2 gives the town to stand a shop, stock it, sell baskets and be fed. |
| 79 | `HungerCheck.COVER_BOUND` | `50` | The most Convenience Stores section 1 stands to cover the households' need. |
| 82 | `HungerCheck.THIN_FILL` | `.05` | The fill the BOTH SIDES fixture thins Retail's staff to, so its shops serve under the need at their operating rate. |
| 84 | `HungerCheck.FED_BOUND` | `40` | The most months a default city is played for a month with a shop selling and nobody hungry. |
| 86 | `HungerCheck.PRICE_UP` | `20` | The shelf price section 3 multiplies, and the months it plays for the people to be priced out. |
| 87 | `HungerCheck.PRICED_MONTHS` | `2` |  |
| 183 | `HungerCheck.playedAudit` | `new Audit(), otherAudit = new Audit()` |  |
| 184 | `HungerCheck.watch` | `new FoodWatch()` |  |
| 400 | `HungerCheck.LEAD` | `5` |  |
| 401 | `HungerCheck.RENT_PRICE` | `100, RENT_SPEND = 1000, MEAL_PRICE = 20, MEAL_SPEND = 1000, SHELF_PRICE = 2, ...` |  |
| 402 | `HungerCheck.GOOD_PRICE` | `50, GOOD_SPEND = 1000, FEE_PRICE = 10, FEE_SPEND = 100` |  |
| 403 | `HungerCheck.RENT_DRIFT` | `.005, MEAL_DRIFT =.003, MEAL_RISE =.01, SHELF_STEP =.10` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 47 | `static PrintStream out` |  |
| 48 | `static PrintStream quiet` |  |
| 98 | `int months, missing, broken` |  |
| 99 | `double worst` |  |
| 100 | `String first` |  |
| 118 | `final int[] levels` |  |
| 119 | `int shops, promises, months` |  |
| 120 | `boolean follows` |  |
| 121 | `String first` |  |
| 167 | `final Game game` |  |
| 168 | `final Retail retail` |  |
| 169 | `final BuildingsTemplate convenience` |  |
| 170 | `int months` |  |
| 186 | `static Trap played` | Section 2's city, once fed: section 3 prices its people out. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 595 | **type** `public class HungerCheck` | A town with no shop goes hungry, the screens say so, and the price index does not wait for the shop (HG1). |
| 50 | 1 | `static void check(String label, boolean ok)` |  |
| 52 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 58 | 3 | `static void near(String label, double actual, double expected, double tol)` | A figure against the harness's own, to an absolute tolerance. |
| 62 | 5 | `static void quietly(Runnable r)` |  |

### the fixtures' own figures (lines 68-201)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 5 | `static Game found(String label, Founding founding)` | A new city founded as the player's door founds it (newGame(founding)), on a scratch folder, quietly. |
| 97 | 18 | **type** `static final class Audit` | What the money audit said of every month played through play(): the worst residual over its tolerance, and the months that broke it. |
| 102 | 12 | `void take(Game g)` _(in HungerCheck.Audit)_ |  |
| 117 | 24 | **type** `static final class FoodWatch` | Every month played through play(): CityNeeds.food() against the thresholds and the doors, and which levels and doors were met. |
| 123 | 17 | `void look(Game g)` _(in HungerCheck.FoodWatch)_ |  |
| 142 | 4 | `static boolean hasFood(List<CityNeeds.Need> needs)` |  |
| 148 | 5 | `static void play(Game g, Audit audit, FoodWatch watch)` | One month of the clock, as the player's button makes it, and what is watched of it. |
| 155 | 5 | `static long retailBuildings(Game g)` | The buildings Retail owns, standing and on site. |
| 166 | 16 | **type** `static final class Trap` | A city founded the 1900 way and played, month by month, up to the first month Retail's gate would pass a Convenience Store: the founding has Retail no building and its posts outrun its people, so nothing but the clock... |
| 172 | 9 | `Trap(String label, Audit audit, FoodWatch watch)` _(in HungerCheck.Trap)_ |  |
| 188 | 13 | `public static void main(String[] args) throws Exception` |  |

### 1. the trap, caused (lines 202-286)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 82 | `static void sectionOne()` |  |

### 2. the trap played (lines 287-327)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 38 | `static void sectionTwo()` |  |

### 3. the screens (lines 328-397)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 62 | `static void sectionThree()` |  |
| 393 | 4 | `static CityNeeds.Need rowOf(List<CityNeeds.Need> all)` |  |

### 4. the index (lines 398-625)

| line | len | member | says |
|---:|---:|---|---|
| 405 | 7 | `static double[] prices(double groceries, double rent, double meals)` |  |
| 413 | 1 | `static double[] spends(double groceries, double rent, double meals)` |  |
| 415 | 3 | `static void take(PriceIndex px, int month, double[] prices, double[] spends)` |  |
| 419 | 5 | `static double[] filled(int n, double value)` |  |
| 425 | 8 | `static void sectionFour()` |  |
| 435 | 39 | `static void basesWithoutGroceries()` | (a) rent and a good bought beside it base the basket at the SETTLING_MONTHS-th such month, groceries absent. |
| 476 | 8 | `static boolean neverBased(double[] prices, double[] spends, double[] fees, double[] feeSpends)` | Whether the index never bases in three settlings of the same month fed to it. |
| 486 | 18 | `static void neverBasesOnHalf()` | (b) BOTH HALVES: rent beside nothing from outside the loop never bases the basket. |
| 505 | 1 | `static double driftRent(int month)` |  |
| 507 | 1 | `static double driftMeal(int month)` |  |
| 510 | 60 | `static void groceriesJoin()` | (c) groceries appearing after a basing with none join it once a whole year of them is in the weights, with the level running on. |
| 572 | 28 | `static void theRateIsReal()` | (d) with no groceries the rule still reads a real rate: the one the meals' price makes of the level, a year on. |
| 602 | 23 | `static void groceriesFromTheStart()` | (e) a basket with groceries from the start is as it was: both halves at SETTLING_MONTHS, groceries in it from the basing, no relink. |

### 5. the money audit (lines 626-638)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 10 | `static void sectionFive()` |  |


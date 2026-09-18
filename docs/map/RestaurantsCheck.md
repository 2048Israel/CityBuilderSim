# RestaurantsCheck.java - 513 lines · 6 methods · 2 constants · harnesses

`ham/citybuildersim/RestaurantsCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> A meal out is food, and it is the same food.
> 
> WHAT THIS HAS TO PROVE, and none of it is "the sector makes money" - whether
> it does depends on the city, which is the point of it:
> 
>   1. A MEAL IS A NINETIETH OF A PERSON-MONTH, in both directions, and the
>      kitchens buy the reference basket in exactly that proportion. If the
>      two halves of that conversion ever disagree, a Diner either feeds
>      thirty times the people it can or starves them, and nothing else in the
>      game would notice.
> 
>   2. THE MARGIN IS STRUCK AGAINST THE TABLES, on GoodsMarket.strike()'s own
>      rule: empty tables at the floor, a queue at the ceiling, monotone
>      between them.
> 
>   3. A MEAL EATEN COUNTS AS SUBSISTENCE AT THE GROCER'S PRICE, not at the
>      restaurant's. Two and a half to five times the food cost is wages and
>      washing up, and none of it is nourishment. Counting the ticket would
>      let a city feed itself by putting its prices up, which is the most
>      embarrassing bug this sector could have.
> 
>   4. THE KITCHENS TAKE FOOD OFF THE SAME SHELF. They add nothing to the
>      city's supply: what they add is a second door to it. A harness that did
>      not check this would let the sector quietly conjure dinners.
> 
>   5. AND THE BOOTSTRAP. Every new sector in this game has deadlocked on one:
>      a kitchen with no service has used nothing, so it orders nothing, so it
>      has nothing to cook. LuxuryRetail lost a whole run to that one.
> 
> Every fixture CAUSES its condition rather than waiting for it.

**Uses:** [HouseholdBalance](HouseholdBalance.md) (20), [Restaurants](Restaurants.md) (19), [Household](Household.md) (14), [LongPlaytest](LongPlaytest.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (9), [PayTier](PayTier.md) (8), [FamilyStructure](FamilyStructure.md) (7), [Retail](Retail.md) (6), [Game](Game.md) (5), [Good](Good.md) (5), [GameFiles](GameFiles.md) (3), [JobType](JobType.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Sectors](Sectors.md) (1), [Equity](Equity.md) (1)

## Sections

| line | section |
|---:|---|
| 86 | · 1. a meal is a ninetieth of a person-month |
| 128 | · 2. the margin, struck against the tables |
| 239 | · 3. a meal eaten is food, at the grocer's price |
| 331 | · 3b. appetite, not money, is the ceiling |
| 366 | · 4. the kitchens eat the city's own food |
| 398 | · 5. the bootstrap |
| 455 | · and the save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `RestaurantsCheck.DINER` | `"Diner"` |  |
| 72 | `RestaurantsCheck.RESTAURANT` | `"Restaurant"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `static int fails` |  |
| 46 | `static PrintStream out` |  |
| 47 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 43 | 471 | **type** `public class RestaurantsCheck` | A meal out is food, and it is the same food. |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 54 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 59 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 65 | 5 | `static void quietly(Runnable r)` |  |
| 74 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 81 | 432 | `public static void main(String[] args) throws Exception` |  |


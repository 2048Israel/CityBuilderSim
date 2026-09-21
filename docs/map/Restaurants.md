# Restaurants.java - 445 lines · 18 methods · 5 constants · sectors

`ham/citybuildersim/sectors/Restaurants.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The kitchens. THE FIFTEENTH SECTOR (2026-09-18, Jerus's call).
> 
> Jerus: "lets add restuarants as well as luxury stores, these two will absorb
> some spending as well" - and then the rule that makes this one different
> from a second boutique: *a meal out REPLACES groceries*.
> 
> =========================================================================
> SO IT IS GROCERIES' SIBLING, NOT LUXURIES'
> =========================================================================
> 
> A watch is imported. The world has as many as the city will pay for, so
> LuxuryRetail is short of nothing but counters and the money it absorbs
> leaves the country. A dinner cannot be imported. The food in it is the
> thirteen things already on the shop shelf, bought in the same market at the
> same prices, so a restaurant brings in no kilogram that was not there.
> 
> THAT IS NOT A LIMITATION, IT IS THE MECHANIC. A city whose shops already
> cannot reach everybody now has a second buyer in the same food market, and
> the price says so. Jerus, on the supply wall: *"let the basket get dearer,
> not bigger"*, and *"supply v demand, thats the most important thing"*. This
> is where that bites.
> 
> =========================================================================
> WHAT A RESTAURANT ACTUALLY ADDS
> =========================================================================
> 
> Two things, and neither is food.
> 
> ONE: A SECOND DOOR. Retail's binding constraint is not stock, it is
> COVERAGE times the operating rate - people the shops can physically serve.
> A city at the wall can feed some of its people through a kitchen instead,
> and the hunger measure sees it, because a meal is a meal wherever it was
> cooked. See HouseholdBalance.advanceMonth(), where meals eaten are added to
> what a household ate before it is compared against subsistence.
> 
> TWO: SOMEWHERE FOR THE MONEY TO GO. A meal out costs a multiple of what the
> same food costs at home, and the multiple is the sector's whole revenue.
> That is the absorption Jerus asked for, and unlike the luxury shops' it
> circulates: the margin is wages and rent HERE.
> 
> =========================================================================
> A MEAL IS ONE NINETIETH OF A PERSON-MONTH
> =========================================================================
> 
> Three a day, thirty days. The number is not tuned to anything - it is what
> a month of eating is - and it is the conversion every arithmetic in this
> sector and in the household ledger runs through. Coverage is in MEALS,
> groceries are in person-months, and reading one as the other is how a Diner
> would look ninety times the business it is.

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (11), [Markets](Markets.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [Retail](Retail.md) (2), [Trade](Trade.md) (2), [Game](Game.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (5):** [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [RestaurantsCheck](RestaurantsCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 69 | WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING |
| 89 | THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S |
| 174 | THE SALE |
| 317 | PLANNING - the queue at a door that is not there |
| 401 | THE SCREEN |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `Restaurants.MEALS_A_PERSON_MONTH` | `90` | Meals one person eats in a month: three a day, thirty days. |
| 87 | `Restaurants.PERSON_MONTHS_PER_MEAL` | `1 / MEALS_A_PERSON_MONTH` | ...and the same fact the other way up, which is what the arithmetic wants. |
| 118 | `Restaurants.MARGIN_FLOOR` | `3.0` | What a kitchen with empty tables charges over what the food cost it. |
| 121 | `Restaurants.MARGIN_CEILING` | `8.0` | ...and what a kitchen with a queue at the door charges. |
| 156 | `Restaurants.KITCHEN_COVER_MONTHS` | `1` | Months of food a kitchen keeps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 124 | `private double sellPrice` | What one meal sells for this month. |
| 127 | `private double rMargin` | The month's reading, for the screen and the harness. |
| 136 | `private final Map<Good, Double> basket` | Kilograms of each good in one person-month, handed in by Game. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 379 | **type** `public class Restaurants extends Sector` | The kitchens. |

### WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING (lines 69-88)

### THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S (lines 89-173)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 16 | `public Restaurants()` |  |
| 159 | 9 | `public void setBasket(Map<Good, Double> kgPerHead)` | What one person-month costs in kilograms, by good. |
| 170 | 3 | `public double kgPerMeal(Good g)` | Kilograms of one good in one MEAL. |

### THE SALE (lines 174-316)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 4 | `public int seats()` | Meals the kitchens can serve a month, off their buildings. |
| 190 | 10 | `public double mealsInTheLarder()` | Meals the larder can actually put out: the ingredient that runs out first, exactly as Retail.basketsOnShelf() asks the same question of a shop's shelf. |
| 202 | 9 | `public double foodCostOfAMeal(Markets markets)` | What the food in one meal cost the kitchen, at the market's prices today. |
| 212 | 1 | `public double getMargin()` |  |
| 213 | 1 | `public double getWanted()` |  |
| 214 | 1 | `public double getServed()` |  |
| 215 | 1 | `public double getSeats()` |  |
| 216 | 1 | `public double getFoodCost()` |  |
| 217 | 1 | `public double getSellPrice()` |  |
| 232 | 21 | `public double strikeMargin(Markets markets, double wanted)` | Strikes the margin against the queue and returns what a meal costs this month. |
| 263 | 22 | `public double serve(Markets markets, double meals)` | ...and serves what the tables and the larder can actually get through. |
| 296 | 20 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 317-400)

| line | len | member | says |
|---:|---:|---|---|
| 331 | 45 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the diners who CAME, not against a sales record. |
| 389 | 11 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more kitchen would earn a month. |

### THE SCREEN (lines 401-445)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 39 | `public List<Line> operations(Game game)` |  |


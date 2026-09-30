# LuxuryCounter.java - 155 lines · 10 methods · 0 constants · model

`ham/citybuildersim/LuxuryCounter.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> The households' discretionary spending: the boutiques and the restaurants,
> each striking its price against the queue.
> 
> ==================== THE LUXURY COUNTER (2026-09-17) ====================
> 
> Jerus: "lets add restuarants as well as luxury stores, these two will
> absorb some spending as well", and on the pricing, "supply v demand,
> thats the most important thing."
> 
> THE SHAPE IS Motoring.month()'s, and deliberately: ask the seller a price, ask
> the households what they will take at it, hand over what the seller
> actually had, take the money. What is different is that the seller here
> strikes its price against the QUEUE rather than reading it off a market
> band - the world has no shortage of watches, so the scarce thing is the
> shop, and the shop is what a player builds. See LuxuryRetail. Since 0.7.19
> the boutiques' queue is the households who would buy at the price they
> charge, struck as one fixed point (LuxuryRetail.strikeMargin()); the
> kitchens still ask at the floor and then at the price (dine()).
> 
> ==================== WHERE IT CAME FROM ====================
> 
> This was Game's "THE LUXURY COUNTER" section until 2026-09-18, when it
> moved out with its text intact: the eight month flows and their getters,
> luxuryShopping() as shop() and diningOut() as dine(). Game keeps the eight
> getters as delegations so that no caller changed, and calls the two in the
> same order and the same place in the month it always did.
> 
> WHY IT LEFT. Game.java was 8,200 lines, and a session reading the luxury
> counter had to carry the month, the save and the treasury with it. A
> mechanic that has the shape of a class - its own month, its own figures,
> one place it is called from - is its own file since 2026-09-18, and Game
> keeps what every reader goes through: the getters, and the order of the
> month. The interface went the same way the same day. See the project's
> splitting-game.md.

**Uses:** [Game](Game.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Restaurants](Restaurants.md) (2), [LuxuryRetail](LuxuryRetail.md) (1)

**Used by (1):** [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `private double luxuriesSold, luxurySpend, luxuryPrice, luxuryWanted` |  |
| 42 | `private double mealsServed, mealSpend, mealPrice, mealsWanted` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 39 | 117 | **type** `public final class LuxuryCounter` | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. |
| 45 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 48 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 51 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 54 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 72 | 31 | `void dine(Game game)` | The month's dining out. |
| 106 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 109 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 112 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 115 | 1 | `public double getLuxuryWanted()` | ...and how many would buy at the price the shops struck (0.7.19; at the margin's floor until then), which in a city short of shops is more than were served. |
| 118 | 37 | `void shop(Game game)` | The month's shopping. |


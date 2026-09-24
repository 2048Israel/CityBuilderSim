# CityBasket.java - 120 lines · 2 methods · 0 constants · model

`ham/citybuildersim/CityBasket.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> What the city eats: the basket per head, struck from the households' own
> statements, and the file's reference basket for a city that has none yet.
> 
> ==================== WHAT THE CITY EATS ====================
> 
> Loaded once. Consumption owns the thirteen goods, Engel's curve and
> Bennett's, and knows nothing about this city; what makes the basket
> THIS city's basket is the incomes handed to it below, cell by cell.
> 
> LOADED, NOT REQUIRED. A city whose consumption.json is missing or
> unreadable gets an empty model, and cityBasketPerHead() then hands the
> shops an empty basket - which they read as "no shelf yet" rather than
> as "nobody eats". A data file must not be able to stop a game.
> 
> ==================== WHERE IT CAME FROM ====================
> 
> This was Game's "WHAT THE CITY EATS" section until 2026-09-18, when it
> moved out with its text intact: cityBasketPerHead() as perHead(), and
> referenceBasket() under its own name. The Consumption model and its lazy
> loader, Game.getConsumption(), stayed behind, because the field is Game's
> and the loader is what every reader of it goes through. It holds no month
> flows of its own - the basket is struck fresh each time the shops and the
> kitchens ask for it - so Game keeps cityBasketPerHead() as a delegation
> and nothing else changed.
> 
> WHY IT LEFT. Game.java was 8,200 lines, and a session reading what the
> city eats had to carry the month, the save and the treasury with it. A
> mechanic that has the shape of a class - its own month, its own figures,
> one place it is called from - is its own file since 2026-09-18, and Game
> keeps what every reader goes through: the getters, and the order of the
> month. The interface went the same way the same day. See the project's
> splitting-game.md.

**Uses:** [Good](Good.md) (11), [Consumption](Consumption.md) (3), [Game](Game.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Household](Household.md) (1)

**Used by (1):** [Game](Game.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 84 | **type** `public final class CityBasket` | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. |
| 59 | 50 | `java.util.Map<Good, Double> perHead(Game game)` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |
| 111 | 9 | `private static java.util.Map<Good, Double> referenceBasket(Consumption consumption)` | What the file's reference household eats, for a city with no statements yet. |


# Restaurants.java - 521 lines · 24 methods · 6 constants · sectors

`ham/citybuildersim/sectors/Restaurants.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> what a household ate before it is compared against the baskets it needs
> (against subsistence, in money, until 0.7.43).
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

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (11), [Retail](Retail.md) (3), [Markets](Markets.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [Trade](Trade.md) (2), [Game](Game.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (7):** [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [RestaurantsCheck](RestaurantsCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 70 | WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING |
| 90 | THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S |
| 187 | THE SALE |
| 345 | PLANNING - the queue at a door that is not there |
| 445 | THE SCREEN |
| 496 | SAVE, RESET (0.7.43) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `Restaurants.MEALS_A_PERSON_MONTH` | `90` | Meals one person eats in a month: three a day, thirty days. |
| 88 | `Restaurants.PERSON_MONTHS_PER_MEAL` | `1 / MEALS_A_PERSON_MONTH` | ...and the same fact the other way up, which is what the arithmetic wants. |
| 119 | `Restaurants.MARGIN_FLOOR` | `3.0` | What a kitchen with empty tables charges over what the food cost it. |
| 122 | `Restaurants.MARGIN_CEILING` | `8.0` | ...and what a kitchen with a queue at the door charges. |
| 131 | `Restaurants.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a kitchen's charged margin moves toward the one its queue strikes in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 169 | `Restaurants.KITCHEN_COVER_MONTHS` | `1` | Months of food a kitchen keeps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 134 | `private double sellPrice` | What one meal sells for this month. |
| 137 | `private double rMargin` | The month's reading, for the screen and the harness: the margin CHARGED, and the one the queue struck (the target). |
| 140 | `private double chargedMargin` | The margin the kitchens charge, carried month to month (0.7.43); NaN until the first strike, and on a save from before, which opens at the target. |
| 149 | `private final Map<Good, Double> basket` | Kilograms of each good in one person-month, handed in by Game. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 454 | **type** `public class Restaurants extends Sector` | The kitchens. |

### WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING (lines 70-89)

### THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S (lines 90-186)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 16 | `public Restaurants()` |  |
| 172 | 9 | `public void setBasket(Map<Good, Double> kgPerHead)` | What one person-month costs in kilograms, by good. |
| 183 | 3 | `public double kgPerMeal(Good g)` | Kilograms of one good in one MEAL. |

### THE SALE (lines 187-344)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 4 | `public long seats()` | Meals the kitchens can serve a month, off their buildings. |
| 203 | 10 | `public double mealsInTheLarder()` | Meals the larder can actually put out: the ingredient that runs out first, exactly as Retail.basketsOnShelf() asks the same question of a shop's shelf. |
| 215 | 9 | `public double foodCostOfAMeal(Markets markets)` | What the food in one meal cost the kitchen, at the market's prices today. |
| 226 | 1 | `public double getMargin()` | The margin charged this month: a sixth of the way from last month's to the target, in logs. |
| 228 | 1 | `public double getTargetMargin()` | The margin the queue struck this month, which the charged one chases. |
| 229 | 1 | `public double getWanted()` |  |
| 230 | 1 | `public double getServed()` |  |
| 231 | 1 | `public double getSeats()` |  |
| 232 | 1 | `public double getFoodCost()` |  |
| 233 | 1 | `public double getSellPrice()` |  |
| 249 | 31 | `public double strikeMargin(Markets markets, double wanted)` | Strikes the margin against the queue and returns what a meal costs this month - since 0.7.43 the queue strikes the TARGET, and the margin charged moves MARGIN_SPEED of the way to it. |
| 290 | 23 | `public double serve(Markets markets, double meals)` | ...and serves what the tables and the larder can actually get through. |
| 324 | 20 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 345-444)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 61 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the diners who CAME, not against a sales record. |
| 433 | 11 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more kitchen would earn a month. |

### THE SCREEN (lines 445-495)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a merchant, whose middle line is its gross margin. |
| 454 | 1 | `public boolean hasPlantBlock()` |  |
| 457 | 39 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET (0.7.43) (lines 496-521)

| line | len | member | says |
|---:|---:|---|---|
| 502 | 4 | `protected void saveExtras(Map<String, Double> extras)` | The charged margin, which next month's strike moves from; not written while it is NaN (a fresh sector), since a save carries no NaN. |
| 509 | 6 | `protected void restoreExtras(Map<String, Double> extras)` | A save from before 0.7.43 has none, and the first strike opens at its target. |
| 517 | 4 | `protected void resetExtras()` |  |


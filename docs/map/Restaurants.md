# Restaurants.java - 532 lines · 25 methods · 6 constants · sectors

`ham/citybuildersim/sectors/Restaurants.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (10), [Retail](Retail.md) (3), [Markets](Markets.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [Trade](Trade.md) (2), [Game](Game.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [NewUnit](NewUnit.md) (1)

**Used by (8):** [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [RestaurantsCheck](RestaurantsCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 71 | WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING |
| 91 | THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S |
| 188 | THE SALE |
| 358 | PLANNING - the queue at a door that is not there |
| 456 | THE SCREEN |
| 507 | SAVE, RESET (0.7.43) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `Restaurants.MEALS_A_PERSON_MONTH` | `90` | Meals one person eats in a month: three a day, thirty days. |
| 89 | `Restaurants.PERSON_MONTHS_PER_MEAL` | `1 / MEALS_A_PERSON_MONTH` | ...and the same fact the other way up, which is what the arithmetic wants. |
| 120 | `Restaurants.MARGIN_FLOOR` | `3.0` | What a kitchen with empty tables charges over what the food cost it. |
| 123 | `Restaurants.MARGIN_CEILING` | `8.0` | ...and what a kitchen with a queue at the door charges. |
| 132 | `Restaurants.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a kitchen's charged margin moves toward the one its queue strikes in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 170 | `Restaurants.KITCHEN_COVER_MONTHS` | `1` | Months of food a kitchen keeps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `private double sellPrice` | What one meal sells for this month. |
| 138 | `private double rMargin` | The month's reading, for the screen and the harness: the margin CHARGED, and the one the queue struck (the target). |
| 141 | `private double chargedMargin` | The margin the kitchens charge, carried month to month (0.7.43); NaN until the first strike, and on a save from before, which opens at the target. |
| 150 | `private final Map<Good, Double> basket` | Kilograms of each good in one person-month, handed in by Game. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 464 | **type** `public class Restaurants extends Sector` | The kitchens. |

### WHAT A MEAL IS, AS A SHARE OF A MONTH OF EATING (lines 71-90)

### THE MARGIN, AND WHY ITS FLOOR IS SO MUCH HIGHER THAN A BOUTIQUE'S (lines 91-187)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 16 | `public Restaurants()` |  |
| 173 | 9 | `public void setBasket(Map<Good, Double> kgPerHead)` | What one person-month costs in kilograms, by good. |
| 184 | 3 | `public double kgPerMeal(Good g)` | Kilograms of one good in one MEAL. |

### THE SALE (lines 188-357)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 4 | `public long seats()` | Meals the kitchens can serve a month, off their buildings. |
| 205 | 4 | `public long seatsOnSite()` | ...and the meals the kitchens on site will serve once they open (BA1, 2026-11-02): plan() counted the tables standing only while a sector could have one order on site; with no cap on orders it would order the same que... |
| 216 | 10 | `public double mealsInTheLarder()` | Meals the larder can actually put out: the ingredient that runs out first, exactly as Retail.basketsOnShelf() asks the same question of a shop's shelf. |
| 228 | 9 | `public double foodCostOfAMeal(Markets markets)` | What the food in one meal cost the kitchen, at the market's prices today. |
| 239 | 1 | `public double getMargin()` | The margin charged this month: a sixth of the way from last month's to the target, in logs. |
| 241 | 1 | `public double getTargetMargin()` | The margin the queue struck this month, which the charged one chases. |
| 242 | 1 | `public double getWanted()` |  |
| 243 | 1 | `public double getServed()` |  |
| 244 | 1 | `public double getSeats()` |  |
| 245 | 1 | `public double getFoodCost()` |  |
| 246 | 1 | `public double getSellPrice()` |  |
| 262 | 31 | `public double strikeMargin(Markets markets, double wanted)` | Strikes the margin against the queue and returns what a meal costs this month - since 0.7.43 the queue strikes the TARGET, and the margin charged moves MARGIN_SPEED of the way to it. |
| 303 | 23 | `public double serve(Markets markets, double meals)` | ...and serves what the tables and the larder can actually get through. |
| 337 | 20 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 358-455)

| line | len | member | says |
|---:|---:|---|---|
| 372 | 56 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the diners who CAME, not against a sales record. |
| 443 | 12 | `public double estimatedMonthlyProfit(NewUnit u, BusinessInvestment plans)` | What one more kitchen would earn a month. |

### THE SCREEN (lines 456-506)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a merchant, whose middle line is its gross margin. |
| 465 | 1 | `public boolean hasPlantBlock()` |  |
| 468 | 39 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET (0.7.43) (lines 507-532)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 4 | `protected void saveExtras(Map<String, Double> extras)` | The charged margin, which next month's strike moves from; not written while it is NaN (a fresh sector), since a save carries no NaN. |
| 520 | 6 | `protected void restoreExtras(Map<String, Double> extras)` | A save from before 0.7.43 has none, and the first strike opens at its target. |
| 528 | 4 | `protected void resetExtras()` |  |


# Retail.java - 621 lines · 46 methods · 7 constants · sectors

`ham/citybuildersim/sectors/Retail.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The shops. Buy food on the food market, keep it on a shelf, sell it to the
> households as groceries at a price they strike themselves.
> 
> THE ONE CONVERTER IN THE CITY: a unit of FOOD in is a unit of GROCERIES
> out, and the difference between what it paid and what it charges is the
> whole of retail. The shelf is a PANTRY in the template's terms - an input
> it keeps STORE_COVER_MONTHS of recent sales of, bidding for the difference
> each month - and GROCERIES is a seller-priced good, so this class owns
> two things the template does not: what it charges, and the sale to the
> households at the bottom of the month.
> 
> WHAT THE SHOPS CHARGE. Cost-plus with a lag, and scarcity lifts it: the
> shelf price moves a quarter of the way each month toward what the stock
> cost plus RETAIL_MARKUP, times a mark-up for the share of what people
> came for that the shops could not hand over. That is where prices learned
> to ration - see the old CommercialHandler's note, which is preserved in
> repriceShelf() below because the reasoning is the mechanic.
> 
> WHO CAN BUY. Demand is min(coverage, population) - the shops' own capacity
> to serve people - capped by what the households can pay at the shelf
> price, which HouseholdBalance works out after savings and credit have
> been drawn on and Game hands in each month. See the budget constraint in
> sellOwnPriced().
> 
> The bank's branches are COMMERCIAL buildings and used to be inside this
> sector's payroll; they belong to no sector now and their tellers are the
> bank's own bill (see EconomyManager.getBankPayroll()).

**Uses:** [Good](Good.md) (34), [BusinessInvestment](BusinessInvestment.md) (10), [Game](Game.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [GoodsMarket](GoodsMarket.md) (2), [Trade](Trade.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Markets](Markets.md) (1)

**Used by (17):** [EconomyManager](EconomyManager.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 92 | THE THIRTEEN THINGS ON THE SHELF |
| 164 | INPUTS FROM THE CITY |
| 260 | THE SALE, at the bottom of the month |
| 451 | PLANNING - customers against coverage |
| 510 | THE SCREEN |
| 569 | SAVE, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `Retail.STORE_COVER_MONTHS` | `2.5` | Months of recent sales a store tries to keep on the shelf. |
| 52 | `Retail.RETAIL_MARKUP` | `1.50` | What the shops add to what their stock cost them. |
| 55 | `Retail.REPRICE_SPEED` | `.25` | How fast the shelf catches up with the invoice. |
| 58 | `Retail.OPENING_SELL_PRICE` | `.3` | The price the game opened at, and the floor it will not go below. |
| 66 | `Retail.MAX_SCARCITY_MULTIPLE` | `1.6` | How far above cost-plus a total shortage can push the shelf price. |
| 69 | `Retail.COMFORTABLE_DELIVERY` | `.95` | Delivery share at which scarcity stops adding anything. |
| 109 | `Retail.SHELF` | `{ Good.GRAINS, Good.BREAD, Good.DAIRY_EGGS, Good.VEGETABLES, Good.FRUIT, Good...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private double openingSellPrice` | The same floor, in TODAY's money - seeded from the founding value on a reform. |
| 74 | `private double storeSellPrice` |  |
| 76 | `private double lastScarcityMultiple` |  |
| 77 | `private double lastDeliveredShare` |  |
| 80 | `private int lastMonthSales` | Units the shops sold last month. |
| 83 | `private int population` | Who could shop, and what they could pay - set each month by Game from the households' ledger. |
| 84 | `private double spendingCapacity` |  |
| 85 | `private double wantedSpend` |  |
| 88 | `private int rWantedDemand` | the month's sale, for the screens |
| 89 | `private int rDemand` |  |
| 90 | `private int rProductsSold` |  |
| 122 | `private final Map<Good, Double> basket` | Kilograms of each good in one person-month, set by Game from Consumption. |
| 198 | `private double rHouseholdWant` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 576 | **type** `public final class Retail extends Sector` | The shops. |

### THE THIRTEEN THINGS ON THE SHELF (lines 92-163)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 8 | `public Retail()` |  |
| 134 | 9 | `public void setBasket(Map<Good, Double> kgPerHead)` | What one person-month costs in kilograms, by good. |
| 145 | 1 | `public double kgPerHead(Good g)` | Kilograms of one good in one person-month; 0 for anything not on the shelf. |
| 147 | 1 | `public Map<Good, Double> getBasket()` |  |
| 155 | 8 | `private double basketsOnShelf()` | Person-months the shelf can cover: the good that runs out first. |

### INPUTS FROM THE CITY (lines 164-259)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 1 | `public void setPopulation(int population)` |  |
| 169 | 1 | `public void setSpendingCapacity(double money)` |  |
| 170 | 1 | `public void setWantedSpend(double money)` |  |
| 172 | 1 | `public int getPopulation()` |  |
| 173 | 1 | `public double getSpendingCapacity()` |  |
| 174 | 1 | `public double getWantedSpend()` |  |
| 177 | 3 | `public int getStoreCoverage()` | People the shops can serve a month, off their buildings. |
| 182 | 3 | `public int getStoreCapacity()` | Shelf room, off their buildings. |
| 187 | 1 | `public int getStoreInventory()` | What is on the shelf now, counted in person-months rather than kilograms. |
| 189 | 1 | `public double getStoreSellPrice()` |  |
| 190 | 1 | `public double getOpeningSellPrice()` |  |
| 191 | 1 | `public double getScarcityMultiple()` |  |
| 192 | 1 | `public double getDeliveredShare()` |  |
| 193 | 1 | `public int getLastMonthSales()` |  |
| 194 | 1 | `public int getWantedDemand()` |  |
| 195 | 1 | `public int getDemand()` |  |
| 196 | 1 | `public int getUnaffordableDemand()` |  |
| 197 | 1 | `public int getProductsSold()` |  |
| 201 | 8 | `public double getFoodPrice()` | What the shops paid for one person-month of food this month: the basket, at the market's prices. |
| 211 | 9 | `public double getImportPrice()` | ...and what the world charges for one. |
| 232 | 3 | `public double getSupplyRatio()` | Sold over demand - what left the shelf against what people came for and could afford. |
| 237 | 1 | `public double getHouseholdWant()` | What the households asked for this month, in baskets, before any cap. |
| 247 | 4 | `public double getHouseholdShare()` | ...and the share of it they actually got. |
| 252 | 1 | `public void setStoreSellPrice(double price)` |  |
| 253 | 1 | `public void setLastMonthSales(int units)` |  |
| 255 | 4 | `public void setStoreInventory(int units)` | Puts N person-months on the shelf, in the kilograms that makes - the save's way back in. |

### THE SALE, at the bottom of the month (lines 260-450)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 89 | `public void sellOwnPriced(Markets markets, Game game)` |  |
| 361 | 6 | `protected double recentUse(Good g)` | What the shops sell, the month-one fallback included: with no sales to go on, they stock for every customer they could serve, and after that for what they actually sold - see the old handler's restockTarget(). |
| 379 | 15 | `public void endOfMonth(Game game)` | ...and the shelf follows THIRTEEN invoices now. |
| 412 | 10 | `public void repriceShelf(double localUnits, double localPrice, double importUnits, double importPrice, double plannedUnits, dou...` | What the shops charge, and this is where prices learned to ration. |
| 433 | 17 | `public void repriceShelf(double blendedCost, double plannedUnits, double deliveredUnits)` | The same rule, told what one unit cost instead of working it out. |

### PLANNING - customers against coverage (lines 451-509)

| line | len | member | says |
|---:|---:|---|---|
| 456 | 42 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` |  |
| 501 | 3 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | Gross margin on a full store: every covered customer buys a unit a month. |
| 506 | 3 | `public double[] retirementDemandAndCapacity(Game game)` |  |

### THE SCREEN (lines 510-568)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 1 | `public String inputLabel()` |  |
| 518 | 50 | `public List<Line> operations(Game game)` |  |

### SAVE, RESET, THE REFORM (lines 569-621)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 11 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 587 | 11 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 600 | 8 | `protected void resetExtras()` |  |
| 610 | 6 | `protected void redenominateExtras(double scale)` |  |
| 618 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |


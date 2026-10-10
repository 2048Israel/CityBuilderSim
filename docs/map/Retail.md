# Retail.java - 1,291 lines · 98 methods · 16 constants · sectors

`ham/citybuildersim/sectors/Retail.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> WHAT THE SHOPS CHARGE (0.7.43). A floor - what the stock cost plus
> RETAIL_MARKUP, and never under the opening price struck at the expected
> price level - and above it the price at which what the households want
> equals what the shops can hand over, capped at CLEARING_CAP over the
> floor. The shelf moves a sixth of the way there a month, drifting with
> expected inflation as it goes: sticky, as a shop's price is. See
> repriceShelf(), and the old CommercialHandler's note preserved there.
> 
> WHO CAN BUY. Each household asks for baskets at a price - a basket a head
> at most, a little fewer above the satiation price, never more than its
> money buys (HouseholdBalance.groceryDemandOf()) - and the shops hand over
> what coverage, the operating rate and the shelf allow. A shortage is
> shared out at the clearing price, so the poorest are priced out first.
> See sellOwnPriced().
> 
> WHO PAYS FOR THE STOCK (0.7.44). The till and the lender, as for every
> firm - and, for what they cannot cover, the shops' suppliers, who wait a
> month for a month of the sale the shops expect and are repaid at the next
> strike out of that sale. See supplierCreditLimit() and SupplierCredit.
> 
> The bank's branches are COMMERCIAL buildings and used to be inside this
> sector's payroll; they belong to no sector now and their tellers are the
> bank's own bill (see EconomyManager.getBankPayroll()).
> 
> AND THE GROCERS' FORECOURTS (0.7.83, batch O6): the drivers' petrol is
> sold here, drawn at wholesale and sold at the pump, and the Filling
> Stations are what the forecourts can sell. See THE FORECOURTS.

**Uses:** [Good](Good.md) (40), [BusinessInvestment](BusinessInvestment.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (11), [Game](Game.md) (9), [Trade](Trade.md) (8), [SupplierCredit](SupplierCredit.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [Markets](Markets.md) (2), [GoodsMarket](GoodsMarket.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [TaxPolicy](TaxPolicy.md) (1)

**Used by (37):** [BondCheck](BondCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [CarCheck](CarCheck.md), [EconomyManager](EconomyManager.md), [ExpectationsCheck](ExpectationsCheck.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [MonetaryCheck](MonetaryCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeScreen](TradeScreen.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 69 | · the price that clears (0.7.43) |
| 107 | · its suppliers' credit (0.7.44) |
| 162 | THE THIRTEEN THINGS ON THE SHELF |
| 234 | INPUTS FROM THE CITY |
| 280 | · the sale, read for the screens (0.7.45) |
| 447 | THE SALE, at the bottom of the month |
| 675 | PLANNING - baskets wanted against baskets the shops can hand over |
| 800 | THE FORECOURTS (0.7.83, batch O6; runs/research-pump.md; the brief's |
| 1056 | THE SCREEN |
| 1170 | SAVE, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `Retail.STORE_COVER_MONTHS` | `2.5` | Months of recent sales a store tries to keep on the shelf. |
| 64 | `Retail.RETAIL_MARKUP` | `1.50` | What the shops add to what their stock cost them. |
| 67 | `Retail.OPENING_SELL_PRICE` | `.3` | The price the game opened at, and the floor it will not go below - struck at the expected price level since 0.7.42 (seedConstants()). |
| 82 | `Retail.SATIATION_MULTIPLE` | `1.5` | How far over the opening floor a full basket is still wanted: 1.5x. |
| 90 | `Retail.GROCERY_ELASTICITY` | `.4` | How far a household's baskets fall with their price above satiation, an elasticity: .4, the middle of the measured food-at-home range (USDA ERS -0.3 to -0.6; Andreyeva et al. |
| 93 | `Retail.CLEARING_CAP` | `1.5` | The most the shelf's target goes over its floor however short the shops are: half again. |
| 96 | `Retail.CLEAR_SPEED` | `1.0 / 6` | The share of the way to its target, in logs, the shelf moves in a month: a sixth - about six months to clear, slower than the old quarter. |
| 99 | `Retail.FLOOR_CATCH_UP` | `.5` | How much of the gap to its floor a shelf under the floor closes in a month: half. |
| 102 | `Retail.CLEARING_BAND` | `50` | The clearing price is looked for between the shelf price over this and the shelf price times it. |
| 105 | `Retail.CLEARING_STEPS` | `50` | Halvings (in logs) of that band the search takes: fifty, far finer than a cent's grain. |
| 121 | `Retail.SUPPLIER_CREDIT_MONTHS` | `1` | How much the shops' suppliers will wait for, in months of the stock for the sale the shops expect, at what it costs to bring in: one - a month's terms, repaid at the next strike out of the sale the stock went to. |
| 179 | `Retail.SHELF` | `{ Good.GRAINS, Good.BREAD, Good.DAIRY_EGGS, Good.VEGETABLES, Good.FRUIT, Good...` |  |
| 853 | `Retail.PUMP_MARGIN` | `.12` | What a station adds to the wholesale price it paid a litre, before the sales tax: twelve per cent (runs/research-pump.md 7: Canada's 10.4 c/L in 2025, about 10% of the wholesale ex tax [6]; the US 35-43 c/gal, 16-19% ... |
| 861 | `Retail.QUEUE_MARGIN` | `.18` | ...and on the litres past what the stations can sell: eighteen per cent, the top of the research's range - a rural or post-spike forecourt's (runs/research-pump.md 2, 7). |
| 926 | `Retail.FuelSale.NONE` | `new FuelSale(0, 0, 0, 0, 0)` |  |
| 1219 | `Retail.SUPPLIER_CREDIT_KEY` | `"supplierCredit."` | The prefix the suppliers' credit is saved under among the extras. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 124 | `private final SupplierCredit supplierCredit` | What the shops owe their suppliers for the shelf's stock, struck and repaid a month at a time (0.7.44). |
| 127 | `private double openingSellPrice` | OPENING_SELL_PRICE, the opening floor, in TODAY's money - seeded from the founding value on a reform, and struck at the expected price level every month since 0.7.42 (seedConstants()). |
| 129 | `private double storeSellPrice` |  |
| 131 | `private double lastScarcityMultiple` |  |
| 132 | `private double lastDeliveredShare` |  |
| 135 | `private double clearingPrice, demandAtPrice, supplyBaskets, floorPrice, rNeeded` | The month's sale at a price (0.7.43): the clearing price, the baskets demanded at the shelf price, what the shops could hand over, and the floor the last reprice stood on. |
| 144 | `private double handOver` | ...and, recorded at the sale for the screens (0.7.45), what the shops' buildings could hand over at the operating rate - coverage times the rate, before the shelf - and the price a basket was charged. |
| 147 | `private double expectedLevel` | The expected price level Game hands on after the anchor's month - the level the next month's constants are struck at - and expected inflation a month: what the shelf drifts at. |
| 150 | `private long lastMonthSales` | Units the shops sold last month. |
| 153 | `private long population` | Who could shop, and what they could pay - set each month by Game from the households' ledger. |
| 154 | `private double spendingCapacity` |  |
| 155 | `private double wantedSpend` |  |
| 158 | `private long rWantedDemand` | the month's sale, for the screens |
| 159 | `private long rDemand` |  |
| 160 | `private long rProductsSold` |  |
| 192 | `private final Map<Good, Double> basket` | Kilograms of each good in one person-month, set by Game from Consumption. |
| 383 | `private double rHouseholdWant` |  |
| 864 | `private double rPumpLitres, rQueueLitres, rPumpCapacity, rPumpPrice` | The month's forecourts, struck at the sale (sellFuel()) and saved for the screens and the planner: the litres sold at the pump and past the stations, what the stations could sell, the prices, the wholesale a litre and... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1234 | **type** `public final class Retail extends Sector` | The shops. |

### the price that clears (0.7.43) (lines 69-106)

### its suppliers' credit (0.7.44) (lines 107-161)

### THE THIRTEEN THINGS ON THE SHELF (lines 162-233)

| line | len | member | says |
|---:|---:|---|---|
| 194 | 8 | `public Retail()` |  |
| 204 | 9 | `public void setBasket(Map<Good, Double> kgPerHead)` | What one person-month costs in kilograms, by good. |
| 215 | 1 | `public double kgPerHead(Good g)` | Kilograms of one good in one person-month; 0 for anything not on the shelf. |
| 217 | 1 | `public Map<Good, Double> getBasket()` |  |
| 225 | 8 | `private double basketsOnShelf()` | Person-months the shelf can cover: the good that runs out first. |

### INPUTS FROM THE CITY (lines 234-279)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 1 | `public void setPopulation(long population)` |  |
| 245 | 1 | `public void setSpendingCapacity(double money)` | The households' plan, in money: what they could spend and what they would like to. |
| 246 | 1 | `public void setWantedSpend(double money)` |  |
| 257 | 4 | `public void setExpected(double level, double monthly)` | The expected price level (Expectations.getExpectedLevel(), handed on after the anchor's month: the level the next month's money constants are struck at, and inside that month the one they are struck at) and expected i... |
| 262 | 1 | `public double getExpectedLevel()` |  |
| 263 | 1 | `public double getExpectedMonthly()` |  |
| 266 | 1 | `public double getSatiationPrice()` | The price a full basket is still wanted at: SATIATION_MULTIPLE over the opening floor, in today's money at the expected level. |
| 269 | 1 | `public double getClearingPrice()` | The price at which what the households want met what the shops could hand over, at the last sale - looked for within CLEARING_BAND of the shelf price. |
| 272 | 1 | `public double getDemandAtPrice()` | The baskets the households asked for at the shelf price, at the last sale. |
| 275 | 1 | `public double getSupplyBaskets()` | The baskets the shops could hand over at the last sale: coverage times the operating rate, or the shelf if it ran out first. |
| 278 | 1 | `public double getFloorPrice()` | The floor the shelf stood on at the last reprice: the opening price at the expected level, or the stock's cost plus RETAIL_MARKUP if more. |

### the sale, read for the screens (0.7.45) (lines 280-446)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 3 | `public double getHandOver()` | The baskets the shops' buildings could hand over at the last sale: coverage times the operating rate, before the shelf - recorded at the sale; on a save from before 0.7.45, today's coverage at today's rate. |
| 290 | 4 | `public boolean isShelfBound()` | True when the shelf ran out before the buildings did at the last sale: what limited it was the stock its cash and credit bought. |
| 296 | 1 | `public double shelfOverFloor()` | The shelf price over its floor: 1 on the floor. |
| 299 | 1 | `public double getCapPrice()` | The most the shelf's target goes to: CLEARING_CAP over the floor. |
| 302 | 1 | `public boolean clearsPastTheCap()` | True when even the cap left more asked for than the shops could hand over at the last sale: a price cannot clear it. |
| 305 | 1 | `public boolean isSlack()` | True when the shops could hand over more than is asked for at the floor at the last sale: the clearing price is under the floor, and not a price anybody would charge (the bisection's band bottom when the slack is wide). |
| 308 | 1 | `public boolean isSaleCounted()` | True once a sale has been counted at a price: a save from before 0.7.43 has none until its first month. |
| 311 | 1 | `public double getChargedPrice()` | The price a basket was charged at the last sale: the shelf price before the bottom of the month repriced it - today's shelf price on a save from before 0.7.45. |
| 319 | 3 | `public double expectedBaskets()` | The baskets the shops expect to sell next month, as the last sale reads it: what the households asked for at the shelf price, up to what the shops can hand over - coverage times the operating rate, the shelf not count... |
| 334 | 4 | `public double supplierCreditLimit()` | WHAT THE SHOPS' SUPPLIERS WILL WAIT FOR (0.7.44; SupplierCredit): the stock for the sale the shops expect - the expected baskets, at what a basket costs to bring in - for SUPPLIER_CREDIT_MONTHS. |
| 340 | 9 | `public double basketLandedCost()` | What one basket costs the shops to bring in now, at the price their orders are budgeted at (GoodsMarket.landedPrice()). |
| 351 | 1 | `public SupplierCredit supplierCredit()` |  |
| 353 | 1 | `public long getPopulation()` |  |
| 354 | 1 | `public double getSpendingCapacity()` |  |
| 355 | 1 | `public double getWantedSpend()` |  |
| 358 | 3 | `public long getStoreCoverage()` | People the shops can serve a month, off their buildings. |
| 363 | 3 | `public long getStoreCapacity()` | Shelf room, off their buildings. |
| 368 | 1 | `public long getStoreInventory()` | What is on the shelf now, counted in person-months rather than kilograms. |
| 370 | 1 | `public double getStoreSellPrice()` |  |
| 371 | 1 | `public double getOpeningSellPrice()` |  |
| 373 | 1 | `public double getScarcityMultiple()` | The shelf's target over its floor at the last reprice: the clearing price held between 1 and CLEARING_CAP (0.7.43; the scarcity mark-up until then). |
| 374 | 1 | `public double getDeliveredShare()` |  |
| 375 | 1 | `public long getLastMonthSales()` |  |
| 376 | 1 | `public long getWantedDemand()` |  |
| 377 | 1 | `public long getDemand()` |  |
| 379 | 1 | `public long getUnaffordableDemand()` | Baskets the households needed and did not ask for at the shelf price - priced out, by their money or by the price (0.7.43; the want past what they could afford until then). |
| 381 | 1 | `public double getBasketsNeeded()` | The baskets the households needed at the last sale, one a head. |
| 382 | 1 | `public long getProductsSold()` |  |
| 386 | 8 | `public double getFoodPrice()` | What the shops paid for one person-month of food this month: the basket, at the market's prices. |
| 396 | 9 | `public double getImportPrice()` | ...and what the world charges for one. |
| 418 | 3 | `public double getSupplyRatio()` | Sold over demand - what left the shelf against what people came for and could afford. |
| 423 | 1 | `public double getHouseholdWant()` | What the households asked for this month, in baskets, before any cap. |
| 434 | 4 | `public double getHouseholdShare()` | ...and the share of it they actually got. |
| 439 | 1 | `public void setStoreSellPrice(double price)` |  |
| 440 | 1 | `public void setLastMonthSales(long units)` |  |
| 442 | 4 | `public void setStoreInventory(int units)` | Puts N person-months on the shelf, in the kilograms that makes - the save's way back in. |

### THE SALE, at the bottom of the month (lines 447-674)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 77 | `public void sellOwnPriced(Markets markets, Game game)` |  |
| 536 | 4 | `private double demandAt(ham.citybuildersim.HouseholdBalance households, double price)` | The baskets the city asks for at a price: the households' own curve, or - with no households to ask, a harness's bare market - a basket for every person the shops could serve, at any price, which is what this sector s... |
| 547 | 3 | `private double clearingPriceFor(ham.citybuildersim.HouseholdBalance households, double price, double supply)` | The price at which the city's demand meets a supply, by bisection in logs on [price / CLEARING_BAND, price x CLEARING_BAND]: the top of the band when even it leaves more wanted than there is (or there is nothing), the... |
| 556 | 11 | `public static double clearingPriceOf(java.util.function.DoubleUnaryOperator demandAt, double price, double supply)` | ...the rule alone, on any demand curve that falls in the price: the smallest price in the band, to CLEARING_STEPS halvings in logs, at which no more is wanted than the supply. |
| 574 | 6 | `protected double recentUse(Good g)` | What the shops sell, the month-one fallback included: with no sales to go on, they stock for every customer they could serve, and after that for what they actually sold - see the old handler's restockTarget(). |
| 592 | 15 | `public void endOfMonth(Game game)` | ...and the shelf follows THIRTEEN invoices now. |
| 638 | 3 | `public void repriceShelf(double blendedCost)` | What the shops charge, and this is where prices learned to ration. |
| 646 | 14 | `public void repriceShelf(double blendedCost, double clearing)` | ...told the clearing price, rather than reading the last sale's: the rule alone, for a harness that causes a clearing price without a city. |
| 669 | 5 | `public static double stickyPrice(double price, double target, double expectedMonthly, double speed)` | ONE FORM FOR EVERY SELLER (0.7.43, spec-inflation.md 2.7): a price that keeps last month's level grown at expected inflation, and moves a share `speed` of the way to its target in logs. |

### PLANNING - baskets wanted against baskets the shops can hand over (lines 675-799)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 63 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | THE SUPPLY ANSWER TO A SHORTAGE (0.7.43; spec-inflation.md 4.3). |
| 761 | 4 | `private double handOverRate()` | The operating rate a new shop is planned at: the sector's own, or full with none yet (see the note above plan()). |
| 768 | 4 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | Gross margin on a store: the baskets it can hand over at the operating rate, at the shelf price over the food (0.7.43; every covered customer until then) - and a Filling Station's on the litres it can sell (0.7.83; st... |
| 775 | 3 | `public boolean mayRetire(BuildingsTemplate t)` | A Filling Station is not sold by the shops' rules (0.7.83, star O6): it serves the drivers' litres, not the baskets they measure. |
| 789 | 4 | `public double[] retirementDemandAndCapacity(Game game)` | ...AND SELLS SHOPS BY THE SAME MEASURE (0.7.43): the baskets wanted at the floor against the baskets the shops can hand over, each shop its coverage times the operating rate - the planner's own. |
| 796 | 3 | `public double unitsOf(BuildingsTemplate t)` | A shop's worth of what retirement counts: the baskets it can hand over (0.7.43; its coverage until then). |

### THE FORECOURTS (0.7.83, batch O6; runs/research-pump.md; the brief's (lines 800-1055)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 3 | `public static boolean isStation(BuildingsTemplate t)` | Whether a building is a Filling Station: it sells litres at the pump. |
| 873 | 3 | `public int stationsStanding()` | Filling Stations standing. |
| 878 | 3 | `public int stationsOnSite()` | ...and on site. |
| 883 | 3 | `public double stationLitres()` | Litres a month the standing stations sell at their typical throughput, at nameplate. |
| 888 | 4 | `public double stationCapacity()` | ...and at the operating rate: what the forecourts can sell this month. |
| 894 | 3 | `public static double pumpPrice(double wholesale, double salesRate)` | The pump price of a litre bought at `wholesale`, with the sales tax at `salesRate` passed on: wholesale x (1 + PUMP_MARGIN) / (1 - rate). |
| 899 | 3 | `public static double queuePrice(double wholesale, double salesRate)` | ...and past the stations' capacity: QUEUE_MARGIN in place of PUMP_MARGIN. |
| 903 | 3 | `private static double clampRate(double r)` |  |
| 908 | 3 | `private double salesRate(Game game)` | Retail's own sales tax rate in a city, nothing without one. |
| 919 | 4 | `public double pumpPriceToday(Game game)` | What a litre costs the drivers at the pump today, before any is drawn (Motoring.journeyFuel(): what the owners weigh a ride against): what a litre of petrol costs to bring in (GoodsMarket.landedPrice() - the refiners'... |
| 925 | 3 | **type** `public record FuelSale(double litres, double queued, double wholesale, double imported, double bill)` | A month's sale at the pump (sellFuel()): the litres, those sold past the stations, the wholesale bill and its imported part, and what the households paid. |
| 937 | 28 | `public FuelSale sellFuel(double litres, Game game)` | The drivers' month at the pump (0.7.83): `litres` drawn at wholesale on Retail's books, the refiners' tanks first (Markets.draw()), and sold to the households - what the stations can sell at the pump price, the rest a... |
| 967 | 1 | `public double getPumpLitres()` | The month's litres sold at the pump price, and past the stations at the queue's. |
| 968 | 1 | `public double getQueueLitres()` |  |
| 971 | 1 | `public double getPumpCapacity()` | ...what the stations could sell this month, at the operating rate. |
| 974 | 1 | `public double getPumpPrice()` | ...the two prices a litre, and the wholesale Retail paid a litre: NaN before a month's sale. |
| 975 | 1 | `public double getQueuePrice()` |  |
| 976 | 1 | `public double getWholesaleLitre()` |  |
| 979 | 1 | `public double getFuelBill()` | ...what the households paid for their petrol, and what of the wholesale bill Retail paid the world. |
| 980 | 1 | `public double getFuelImported()` |  |
| 992 | 8 | `public double stationEarns(BuildingsTemplate t, BusinessInvestment plans)` | A station's earnings a month, for the interest test (0.7.83): the litres it would sell - its typical throughput at the operating rate (handOverRate(), full with no rate yet), or the forecast's litres the stations stan... |
| 1007 | 8 | `public double litresForecast(BuildingsTemplate station, BusinessInvestment plans)` | The drivers' litres a month the planner sees ahead (0.7.83): last month's at the pump and past it, grown as the shops' baskets are - as the people are, over a station's lead time and the planning horizon, to what the ... |
| 1026 | 29 | `public BusinessInvestment.Decision planStations(BusinessInvestment plans, Game game)` | Retail's other question each month (0.7.83): a Filling Station when the drivers' litres outrun what its stations can sell. |

### THE SCREEN (lines 1056-1169)

| line | len | member | says |
|---:|---:|---|---|
| 1062 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a merchant, whose middle line is its gross margin on the stock it sells. |
| 1065 | 1 | `public String inputLabel()` |  |
| 1068 | 1 | `public boolean hasPlantBlock()` |  |
| 1071 | 98 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET, THE REFORM (lines 1170-1291)

| line | len | member | says |
|---:|---:|---|---|
| 1175 | 42 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 1222 | 29 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 1253 | 16 | `protected void resetExtras()` |  |
| 1271 | 15 | `protected void redenominateExtras(double scale)` |  |
| 1288 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |


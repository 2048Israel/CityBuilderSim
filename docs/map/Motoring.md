# Motoring.java - 487 lines · 33 methods · 2 constants · model

`ham/citybuildersim/Motoring.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The households' car market: the second-hand pass, then the showroom, with
> the road told what is parked on it - and since 0.7.49 the drivers' fuel and
> a buyer who weighs a car's full cost against the fare.
> 
> ==================== THE HOUSEHOLDS BUY CARS (2026-09-16) ====================
> 
> The fifth link, and the first time in this game that a household has
> bought anything except food, a roof and a share.
> 
> WHY IT IS HERE AND NOT IN THE MARKET PASS. Cars clear in the band like
> everything else, but the households are not a sector and cannot bid in
> a strike. They take what they want off the makers' shelf at the price
> the market struck, and import the rest - which is exactly what the CITY
> does for building material, through the same Markets.draw(). The units
> count as demand for next month's strike (see Markets.noteDrawn), so the
> price answers with a month's lag, as materials' does.
> 
> WHY THE TOP OF THE MONTH. Savings are fresh - Game.updateHouseholdAccounts()
> settled them, the call before this one in startOfMonthUpdate() - and the
> makers' shelf holds what last month's production put there. A household
> buying before the export market opens is domestic demand getting first
> refusal on a domestic car, which is the right way round and is what the
> city's own materials draw already does.
> 
> THE PRICE IS QUOTED BEFORE IT IS CHARGED, and that is not a nicety. The
> shelf is finite: past it every car is an import at the ceiling, so a
> month that buys more than the city made costs more per car than the
> market price says. Sizing the demand at the local price and settling it
> at the blended one would have taken money the households had not agreed
> to spend - and the clamp that stops savings going negative would have
> swallowed the difference silently, which is money from nowhere wearing a
> safety guard. So: quote at what they want, re-ask at what that would
> cost, buy that, and settle at the blend of what was actually taken -
> which is never dearer than the figure they were re-asked at, because
> fewer cars means a smaller imported share.
> 
> ==================== WHERE IT CAME FROM ====================
> 
> This was Game's "THE HOUSEHOLDS BUY CARS" section until 2026-09-18, when
> it moved out with its text intact: the nine month flows and their getters,
> and motoring() as month(). The ownership rate carried across a load
> (Game.carriedCarOwnership) stayed behind, because the load path reads it.
> Game keeps the nine getters as delegations so that no caller changed.
> 
> WHY IT LEFT. Game.java was 8,200 lines, and a session reading the car
> market had to carry the month, the save and the treasury with it. A
> mechanic that has the shape of a class - its own month, its own figures,
> one place it is called from - is its own file since 2026-09-18, and Game
> keeps what every reader goes through: the getters, and the order of the
> month. The interface went the same way the same day. See the project's
> splitting-game.md.

**Uses:** [Good](Good.md) (4), [Markets](Markets.md) (3), [Game](Game.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [Retail](Retail.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [PriceLevel](PriceLevel.md) (1), [Bank](Bank.md) (1), [GoodsMarket](GoodsMarket.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Consumption](Consumption.md) (1), [Trade](Trade.md) (1)

**Used by (11):** [CarCheck](CarCheck.md), [CpiCoverageFinding](CpiCoverageFinding.md), [CpiReferenceFinding](CpiReferenceFinding.md), [DataSave](DataSave.md), [DenominationCheck](DenominationCheck.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [ScaleCheck](ScaleCheck.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 155 | THE FUEL, AND THE BUYER WEIGHS THE FARE (0.7.49) |
| 207 | THE FUEL IS DRAWN (0.7.62, batch K) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 188 | `Motoring.CAR_FUEL_PER_JOURNEY` | `.002 * PriceLevel.FACTOR` | What a journey to work by car burns, in world money: $2.00, fifteen kilometres at eight litres a hundred and about $1.65 a litre. |
| 202 | `Motoring.LITRES_PER_JOURNEY` | `1.2` | ...and the litres in it (0.7.62): fifteen kilometres at eight litres a hundred, the same journey. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `private double householdCarsBought` |  |
| 59 | `private double householdCarSpend` |  |
| 60 | `private double householdCarImports` |  |
| 61 | `private double householdCarCredit` |  |
| 64 | `private double newCarPrice` | Last actual seller bill per new car, local money/car; zero before the first purchase. |
| 86 | `private PetrolPrices petrolPrices` |  |
| 130 | `private double usedCarsTraded, usedCarsOffered, usedCarSpend, usedCarCredit, usedCarPrice` | The second-hand market's month: offered, traded, what it cost and what a lender found. |
| 205 | `private double fuelBill` | The month's fuel: the drivers' journeys at a journey's price, today's money. |
| 227 | `private double fuelImports, fuelLitres` |  |
| 230 | `private double prefersTransit` | The households' share who would rather ride than buy: 1 until a month is struck, and 1 at the default fare in the research cities. |
| 233 | `private double ownershipCeiling` | ...and the ceiling on ownership the month's buyers met, cars per household (1 until a month is struck). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 432 | **type** `public final class Motoring` | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it - and since 0.7.49 the drivers' fuel and a buyer who weighs a car's full cost against the fare. |
| 67 | 1 | **type** `public record NewTrade(double units, double spend, double imports, double credit, double price)` | Closed-month new-car flow: physical cars, local money paid/imported/advanced, local money/car. |
| 69 | 3 | `public NewTrade newTradeForSave()` |  |
| 73 | 10 | `public void restoreNewTrade(NewTrade saved)` |  |
| 85 | 1 | **type** `public record PetrolPrices(double pumpLitres, double queueLitres, double pumpPrice, double queuePrice, doub...` | Last observed channel quantities in litres, tax-inclusive prices and comparable level in local money/litre. |
| 88 | 1 | `public PetrolPrices petrolPricesForSave()` |  |
| 90 | 6 | `public void restorePetrolPrices(PetrolPrices saved)` |  |
| 98 | 1 | `public double getNewCarPrice()` | Actual paid new-car price, carried through months with no sale. |
| 101 | 1 | `public double getPetrolPrice()` | Comparable petrol price, carried through months with no sale. |
| 109 | 10 | `void notePetrolPrices(double pumpLitres, double queueLitres, double pumpPrice, double queuePrice, double bill)` | Record once at the existing fuel sale. |
| 121 | 7 | `public void redenominateCpi(double scale)` | All money fields scale; physical cars/litres and the resulting price relatives do not. |
| 133 | 1 | **type** `public record UsedTrade(double traded, double offered, double spend, double credit, double price)` | The closed month's trades and asset mark, which cannot be rebuilt from its closing fleet. |
| 135 | 3 | `public UsedTrade usedTradeForSave()` |  |
| 139 | 10 | `public void restoreUsedTrade(UsedTrade saved)` |  |
| 150 | 4 | `public void redenominateUsedTrade(double scale)` |  |

### THE FUEL, AND THE BUYER WEIGHS THE FARE (0.7.49) (lines 155-206)

### THE FUEL IS DRAWN (0.7.62, batch K) (lines 207-487)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 5 | `public void setFuelBill(double v)` | Sets the month's fuel bill with every litre of it imported: what the fuel was before a refinery could sell any, and a save from before 0.7.62. |
| 241 | 1 | `public double getFuelBill()` |  |
| 244 | 1 | `public double getFuelImports()` | ...the part of it bought from the world (0.7.62): all of it with no refinery. |
| 247 | 1 | `public double getFuelLitres()` | ...and the litres the drivers burned (0.7.62); 0 for a month struck by setFuelBill(). |
| 250 | 5 | `public void restoreFuel(double bill, double imports, double litres)` | The month's fuel as 6d struck it, put back by a load (0.7.62): the bill, its imported part and the litres. |
| 264 | 4 | `public static double journeyFuel(Markets markets)` | What a journey's fuel costs today, in today's money (0.7.62): its litres at what a litre costs to bring in (GoodsMarket.landedPrice()) - the refiners' price while the city has fuel on offer, the import price while it ... |
| 277 | 4 | `public static double journeyFuel(Game game)` | ...at the pump (0.7.83, batch O6): a journey's litres at the forecourts' pump price today (Retail.pumpPriceToday()) - the price on the sign, the wholesale with the stations' margin and the sales tax on it, and since 0... |
| 289 | 9 | `void drawFuel(Game game, double journeys)` | The drivers' month of fuel (6d, 0.7.62): `journeys` at LITRES_PER_JOURNEY, bought at the pump since 0.7.83 - the grocers' forecourts draw them off the refiners' shelf and the world at wholesale and sell them on (Retai... |
| 298 | 1 | `public double getPrefersTransit()` |  |
| 299 | 1 | `public double getOwnershipCeiling()` |  |
| 306 | 6 | `public static double carPayment(double price, double annualRate)` | A car's monthly payment over its life (CAR_LIFE_MONTHS) at an annual rate: the annuity P i / (1 - (1 + i)^-n), i the rate over twelve; the price over the months at no rate. |
| 314 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 317 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 318 | 1 | `public double getHouseholdCarImports()` |  |
| 321 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 324 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 327 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 330 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 333 | 1 | `void restoreUsedCarPrice(double price)` | ...restored from a save (PW2; DataSave.usedCarPrice): the month's figure the households' cars are marked at until the next month clears its own. |
| 336 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 339 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |
| 342 | 145 | `void month(Game game)` | The car market's month. |


# Motoring.java - 363 lines · 21 methods · 2 constants · model

`ham/citybuildersim/Motoring.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (4), [Markets](Markets.md) (3), [Game](Game.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [InfrastructureManager](InfrastructureManager.md) (2), [Retail](Retail.md) (1), [Bank](Bank.md) (1), [GoodsMarket](GoodsMarket.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Trade](Trade.md) (1)

**Used by (6):** [CarCheck](CarCheck.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [TradeCostCheck](TradeCostCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | THE FUEL, AND THE BUYER WEIGHS THE FARE (0.7.49) |
| 116 | THE FUEL IS DRAWN (0.7.62, batch K) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `Motoring.CAR_FUEL_PER_JOURNEY` | `.002` | What a journey to work by car burns, in world money: $2.00, fifteen kilometres at eight litres a hundred and about $1.65 a litre. |
| 111 | `Motoring.LITRES_PER_JOURNEY` | `1.2` | ...and the litres in it (0.7.62): fifteen kilometres at eight litres a hundred, the same journey. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `private double householdCarsBought` |  |
| 59 | `private double householdCarSpend` |  |
| 60 | `private double householdCarImports` |  |
| 61 | `private double householdCarCredit` |  |
| 64 | `private double usedCarsTraded, usedCarsOffered, usedCarSpend, usedCarCredit, usedCarPrice` | The second-hand market's month: offered, traded, what it cost and what a lender found. |
| 114 | `private double fuelBill` | The month's fuel: the drivers' journeys at a journey's price, today's money. |
| 136 | `private double fuelImports, fuelLitres` |  |
| 139 | `private double prefersTransit` | The households' share who would rather ride than buy: 1 until a month is struck, and 1 at the default fare in the research cities. |
| 142 | `private double ownershipCeiling` | ...and the ceiling on ownership the month's buyers met, cars per household (1 until a month is struck). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 308 | **type** `public final class Motoring` | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it - and since 0.7.49 the drivers' fuel and a buyer who weighs a car's full cost against the fare. |

### THE FUEL, AND THE BUYER WEIGHS THE FARE (0.7.49) (lines 66-115)

### THE FUEL IS DRAWN (0.7.62, batch K) (lines 116-363)

| line | len | member | says |
|---:|---:|---|---|
| 145 | 5 | `public void setFuelBill(double v)` | Sets the month's fuel bill with every litre of it imported: what the fuel was before a refinery could sell any, and a save from before 0.7.62. |
| 150 | 1 | `public double getFuelBill()` |  |
| 153 | 1 | `public double getFuelImports()` | ...the part of it bought from the world (0.7.62): all of it with no refinery. |
| 156 | 1 | `public double getFuelLitres()` | ...and the litres the drivers burned (0.7.62); 0 for a month struck by setFuelBill(). |
| 159 | 5 | `public void restoreFuel(double bill, double imports, double litres)` | The month's fuel as 6d struck it, put back by a load (0.7.62): the bill, its imported part and the litres. |
| 173 | 4 | `public static double journeyFuel(Markets markets)` | What a journey's fuel costs today, in today's money (0.7.62): its litres at what a litre costs to bring in (GoodsMarket.landedPrice()) - the refiners' price while the city has fuel on offer, the import price while it ... |
| 185 | 4 | `public static double journeyFuel(Game game)` | ...at the pump (0.7.83, batch O6): a journey's litres at the forecourts' pump price today (Retail.pumpPriceToday()) - the price on the sign, the wholesale with the stations' margin and the sales tax on it. |
| 197 | 7 | `void drawFuel(Game game, double journeys)` | The drivers' month of fuel (6d, 0.7.62): `journeys` at LITRES_PER_JOURNEY, bought at the pump since 0.7.83 - the grocers' forecourts draw them off the refiners' shelf and the world at wholesale and sell them on (Retai... |
| 204 | 1 | `public double getPrefersTransit()` |  |
| 205 | 1 | `public double getOwnershipCeiling()` |  |
| 212 | 6 | `public static double carPayment(double price, double annualRate)` | A car's monthly payment over its life (CAR_LIFE_MONTHS) at an annual rate: the annuity P i / (1 - (1 + i)^-n), i the rate over twelve; the price over the months at no rate. |
| 220 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 223 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 224 | 1 | `public double getHouseholdCarImports()` |  |
| 227 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 230 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 233 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 236 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 239 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 242 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |
| 245 | 118 | `void month(Game game)` | The car market's month. |


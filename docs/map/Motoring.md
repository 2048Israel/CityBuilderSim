# Motoring.java - 193 lines · 10 methods · 0 constants · model

`ham/citybuildersim/Motoring.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The households' car market: the second-hand pass, then the showroom, with
> the road told what is parked on it.
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

**Uses:** [Good](Good.md) (3), [HouseholdBalance](HouseholdBalance.md) (2), [Markets](Markets.md) (2), [Game](Game.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [Bank](Bank.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Trade](Trade.md) (1)

**Used by (1):** [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private double householdCarsBought` |  |
| 58 | `private double householdCarSpend` |  |
| 59 | `private double householdCarImports` |  |
| 60 | `private double householdCarCredit` |  |
| 63 | `private double usedCarsTraded, usedCarsOffered, usedCarSpend, usedCarCredit, usedCarPrice` | The second-hand market's month: offered, traded, what it cost and what a lender found. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 55 | 139 | **type** `public final class Motoring` | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it. |
| 66 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 69 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 70 | 1 | `public double getHouseholdCarImports()` |  |
| 73 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 76 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 79 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 82 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 85 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 88 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |
| 91 | 102 | `void month(Game game)` | The car market's month. |


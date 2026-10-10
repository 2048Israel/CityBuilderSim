# StrategicReserve.java - 233 lines · 32 methods · 0 constants · model

`ham/citybuildersim/StrategicReserve.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city's strategic reserve of crude oil: what it holds, what it paid for
> it, and what the player has told it to fill and release (0.7.85, batch O8;
> runs/spec-oil.md 2.8, the research's 4.5 and Q14).
> 
> WHY. Crude was a flow until the oil run: the wells shipped what they
> lifted and the refiners ran what they bought, the month it landed. The
> research's Tank Farm makes it storable, and "a city-owned one is a
> strategic reserve: it buys when oil is cheap and releases in a price
> shock, a lever in the spirit of the game". The refiners' farm is theirs
> (sectors.Refining, THE TANK FARM); this is the city's.
> 
> THE ROOM is the Strategic Reserves standing (buildings.json: the Tank
> Farm's 500,000 m3 of tanks, owned by the city, no sector) - their `stock`
> litres at Refining.CRUDE_LITRES_PER_TONNE to the tonne, 429,185 t each.
> 
> FILL (Game.fillReserve(t)): an order for t tonnes, bought in the month's
> crude market as a buyer (Trade.CITY; Markets.CityTrader) - the wells'
> crude pro rata with the refiners, the world's for the rest, as a refiner
> buys - cut to the room; what the room could not take lapses with the
> month. RELEASE (Game.releaseReserve(t)): t a month offered into the same
> market as a seller, pro rata with the wells' offers, as long as there is
> crude to offer; what the buyers here do not take ships at the export
> price, as the wells' unsold crude does. A fill order stops the release,
> and a release cancels a fill, so the city never trades with itself.
> 
> ITS MONEY MOVES AT THE NEXT STRIKE, beside the businesses it traded with
> (Game.settleReserve()): what it bought from the wells and sold to the
> refiners is a pool paying a pool in the same window, which the money
> audit never lists; what it bought from the world and shipped to it
> crosses the edge, as "- city ReserveFill" and "+ city ReserveSales"
> (TRADE). The month's trades are carried to the strike in the save.
> 
> ITS BOOK is at average cost: a fill adds what was paid, a sale takes the
> share of the book the tonnes sold were. Pure bookkeeping; nothing here
> moves money - Game pays and journals it.

**Uses:** [Good](Good.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (2), [Trade](Trade.md) (2), [Markets](Markets.md) (1), [BuildingType](BuildingType.md) (1), [BuildingManager](BuildingManager.md) (1), [Refining](Refining.md) (1)

**Used by (11):** [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingVisual](BuildingVisual.md), [DataSave](DataSave.md), [Game](Game.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 58 | · the state, saved (DataSave.reserve) |
| 102 | · the player's two levers, applied by Game |
| 119 | · the city in crude's clearing (Markets.CityTrader) |
| 198 | · the save |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private double tonnes, cost` | Tonnes of crude held, and what they cost the city (its book, at average cost). |
| 64 | `private double release, fill` | Tonnes a month to release, standing until changed; and tonnes ordered to fill, for the next clearing. |
| 67 | `private double boughtHome, boughtHomeTonnes, boughtAbroad, boughtAbroadTonnes` | The month's trades, carried to the next strike: money and tonnes bought at home and abroad, sold at home and shipped. |
| 68 | `private double soldHome, soldHomeTonnes, soldAbroad, soldAbroadTonnes` |  |
| 71 | `private double settledImports, settledExports, settledBought, settledSold` | What the last strike settled: paid for crude bought abroad, and taken in for crude shipped - the audit's two lines - and the whole of each side. |
| 74 | `private double roomNow` | The room the clearing may fill to, told by the city before the month's markets (Game.finalUpdateEconomy()). |
| 202 | `public double tonnes, cost, release, fill` |  |
| 203 | `public double boughtHome, boughtHomeTonnes, boughtAbroad, boughtAbroadTonnes` |  |
| 204 | `public double soldHome, soldHomeTonnes, soldAbroad, soldAbroadTonnes` |  |
| 205 | `public double settledImports, settledExports, settledBought, settledSold` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 194 | **type** `public final class StrategicReserve implements Markets.CityTrader` | The city's strategic reserve of crude oil: what it holds, what it paid for it, and what the player has told it to fill and release (0.7.85, batch O8; runs/spec-oil.md 2.8, the research's 4.5 and Q14). |
| 43 | 4 | `public static boolean isReserve(BuildingsTemplate t)` | Whether a building is a Strategic Reserve (0.7.85): the city's (no sector), with tanks - crude oil's room. |
| 49 | 8 | `public static double room(BuildingManager buildings)` | Tonnes of crude the city's Strategic Reserves standing hold: their tanks' litres at a tonne of crude's (sectors.Refining.CRUDE_LITRES_PER_TONNE). |

### the state, saved (DataSave.reserve) (lines 58-101)

| line | len | member | says |
|---:|---:|---|---|
| 76 | 1 | `public double getTonnes()` |  |
| 77 | 1 | `public double getCost()` |  |
| 78 | 1 | `public double getRelease()` |  |
| 79 | 1 | `public double getFill()` |  |
| 82 | 1 | `public double costPerTonne()` | The book a tonne: what the crude held cost, over the tonnes; 0 when empty. |
| 85 | 1 | `public double getBoughtHome()` | The month's trades not yet settled: bought at home, bought abroad, sold at home, shipped - money. |
| 86 | 1 | `public double getBoughtAbroad()` |  |
| 87 | 1 | `public double getSoldHome()` |  |
| 88 | 1 | `public double getSoldAbroad()` |  |
| 90 | 1 | `public double getBoughtHomeTonnes()` | ...and in tonnes. |
| 91 | 1 | `public double getBoughtAbroadTonnes()` |  |
| 92 | 1 | `public double getSoldHomeTonnes()` |  |
| 93 | 1 | `public double getSoldAbroadTonnes()` |  |
| 96 | 1 | `public double getSettledImports()` | What the last strike settled abroad: paid for the crude bought from the world (the audit's "- city ReserveFill"), taken in for what shipped ("+ city ReserveSales"). |
| 97 | 1 | `public double getSettledExports()` |  |
| 99 | 1 | `public double getSettledBought()` | ...and each side whole, home and abroad: what the treasury paid, and took in. |
| 100 | 1 | `public double getSettledSold()` |  |

### the player's two levers, applied by Game (lines 102-118)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 4 | `void orderFill(double tonnes)` | An order for `tonnes`, for the next clearing; a release standing is stopped. |
| 111 | 4 | `void setRelease(double tonnesAMonth)` | `tonnesAMonth` released from the next clearing on; a fill not yet bought is cancelled. |
| 117 | 1 | `void setRoom(double room)` | The room the next clearing may fill to (Game, before the markets clear). |

### the city in crude's clearing (Markets.CityTrader) (lines 119-197)

| line | len | member | says |
|---:|---:|---|---|
| 123 | 4 | `public double cityBid(Good g)` | Its fill, as far as the room left holds; nothing for any other good. |
| 130 | 4 | `public double cityOffer(Good g)` | Its release, as far as it holds crude; nothing while a fill stands, nothing for any other good. |
| 136 | 13 | `public void cityBought(Trade t)` |  |
| 151 | 17 | `public void citySold(Trade t)` |  |
| 171 | 3 | `public void cityCleared(Good g)` | What the month's clearing did not fill of the order lapses: the room held no more. |
| 181 | 9 | `double[] settle()` | The strike settles the month's trades (Game.settleReserve()): what was bought, and what was sold, each whole and its part abroad, struck for the audit and the treasury, and the month cleared. |
| 192 | 5 | `void redenominate(double scale)` | A currency reform: the book and the month's money, scaled; the tonnes are tonnes. |

### the save (lines 198-233)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 6 | **type** `public static final class State` | The reserve as a save carries it (DataSave.reserve): spec-oil 2.8's {tonnes, cost, release}, the fill ordered, and the month's trades and what the last strike settled. |
| 208 | 11 | `public State toState()` |  |
| 221 | 10 | `public void restore(State s)` | A save's reserve; null - a save from before 0.7.85 - an empty one, which is what that city had. |
| 232 | 1 | `private static double finite(double v)` |  |


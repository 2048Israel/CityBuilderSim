# Markets.java - 514 lines · 26 methods · 0 constants · model

`ham/citybuildersim/Markets.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Every goods market in the city, and the month they clear in.
> 
> ONE OBJECT PER GOOD, and one pass a month over all of them. The pass is
> the whole of what used to be EconomyManager.procedureUpdate(),
> priceFoodMarket(), priceIronMarket(), mineIron(), produceFood(),
> updateFinalIndustrialHandler() and CommercialHandler.buyInventory(),
> written once for any good:
> 
>   1. the flow goods are made - a mine lifts what the ground allows
>   2. the seller-priced goods are sold - the shops to the households, the
>      landlords' doors - so a shelf that emptied is restocked below
>   2b. each buyer is told what it can pay for (0.7.12 round 6; Sector, BUY
>      ONLY WHAT IT CAN PAY FOR), so an order for stock it cannot pay for
>      is not placed; a maker's inputs are bought whole
>   3. each traded good, in turn: priced off what the makers will bring and
>      what the buyers intend; offered by the makers at that price; bid for
>      by the users; allocated pro rata both ways; the buyers' shortfall
>      imported if the world sells it; the makers' unsold flow exported if
>      the world buys it
>   4. the stockable goods are made into the warehouses, for next month
>   5. each sector finishes its month
> 
> THE CITY TRADES IN ONE MARKET (0.7.85, batch O8; runs/spec-oil.md 2.8):
> its strategic reserve (StrategicReserve, a CityTrader) bids for crude to
> fill it and offers crude to release it, in crude's clearing beside the
> sectors - its fill pro rata with theirs from the wells and the world for
> the rest, its release pro rata with the wells' offers and shipped, as
> their unsold crude is, past what the buyers take. A city with no order
> standing trades nothing, and every figure of the clearing is what it was.
> 
> PRO RATA BOTH WAYS. With one mill and ten shops, or three mines and one
> mill, somebody has to decide who gets what. Every buyer gets the same
> share of its bid and every seller sells the same share of its offer, and
> each pair is recorded as its own trade so the input-tax credit can be
> struck at the supplier's rate - see SalesTaxLedger.
> 
> DRAWS. Building material is not bid for monthly; an order takes it the
> moment it is placed, out of the city's yard first and then from whoever
> makes it, and imports the rest. draw() is that, against the same market
> at the same price, and the units drawn count as demand for next month's
> strike so a city that builds hard makes materials dear.
> 
> NOTHING HERE MOVES MONEY. Trades land in the sectors' ledgers and are
> banked at the strike; see Sector.

**Uses:** [Good](Good.md) (24), [Sector](Sector.md) (23), [GoodsMarket](GoodsMarket.md) (16), [Trade](Trade.md) (16), [Sectors](Sectors.md) (5), [Game](Game.md) (1), [SupplierCredit](SupplierCredit.md) (1)

**Used by (23):** [BooksCheck](BooksCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BusinessInvestment](BusinessInvestment.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FoodProcessingCheck](FoodProcessingCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LuxuryRetail](LuxuryRetail.md), [MiningCheck](MiningCheck.md), [Motoring](Motoring.md), [OilCheck](OilCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [SectorFlow](SectorFlow.md), [Sectors](Sectors.md), [StrategicReserve](StrategicReserve.md)

## Sections

| line | section |
|---:|---|
| 112 | THE MONTH |
| 374 | DRAWS - taken on demand, at the price of the month |
| 460 | READERS |
| 468 | SAVE, RESET, THE REFORM |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `private final Map<Good, GoodsMarket> markets` |  |
| 80 | `private CityTrader city` | The city's trader, or null - a harness's bare market (0.7.85). |
| 474 | `public String good` |  |
| 475 | `public double price, flow, stock, demand` |  |
| 476 | `public double[] taken` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 461 | **type** `public final class Markets` | Every goods market in the city, and the month they clear in. |
| 66 | 12 | **type** `public interface CityTrader` | The city as a trader in a market (0.7.85): what it bids for and offers in the month's clearing of a good, and what it bought and sold there - its strategic reserve's crude (StrategicReserve). |
| 68 | 1 | `double cityBid(Good g)` _(in Markets.CityTrader)_ | Units of a good the city bids for in this month's clearing. |
| 70 | 1 | `double cityOffer(Good g)` _(in Markets.CityTrader)_ | ...and offers in it. |
| 72 | 1 | `void cityBought(Trade t)` _(in Markets.CityTrader)_ | A fill it bought: from a seller here, or the world's. |
| 74 | 1 | `void citySold(Trade t)` _(in Markets.CityTrader)_ | A sale it made: to a buyer here, or shipped abroad. |
| 76 | 1 | `void cityCleared(Good g)` _(in Markets.CityTrader)_ | The good's clearing is done: whatever of its bid the month did not fill lapses. |
| 83 | 1 | `public void setCity(CityTrader trader)` | The city's trader (Game.buildWorld()): its strategic reserve. |
| 85 | 3 | `public Markets()` |  |
| 89 | 1 | `public GoodsMarket get(Good g)` |  |
| 91 | 1 | `public Iterable<GoodsMarket> all()` |  |
| 94 | 3 | `public void setExchangeRate(double rate)` | City money per dollar, times the world's price level. |
| 107 | 4 | `public double getExchangeRate()` | The one rate every market here was told, read off one of them. |

### THE MONTH (lines 112-373)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 56 | `public void clearMonth(Sectors sectors, Game game)` |  |
| 174 | 12 | `private double orderValue(Sector s, boolean stock)` | What a sector's orders this month would come to, each at what a unit costs to bring in (GoodsMarket.landedPrice()): its orders for stock, or its inputs. |
| 188 | 14 | `private double coveredOrderValue(Sector s)` | ...and what its orders for the stock its suppliers' credit covers would come to (0.7.44; SupplierCredit): nothing without one. |
| 204 | 160 | `private void clear(Good g, Sectors sectors)` | Prices one good, then everybody trades it. |
| 366 | 7 | `private void trade(GoodsMarket m, Sector seller, Sector buyer, double units, double price)` | One local fill between two sectors, booked both sides. |

### DRAWS - taken on demand, at the price of the month (lines 374-459)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 3 | **type** `public record Draw(double units, double local, double imported, double localCost, double importCost)` | What a draw came to. |
| 380 | 1 | `public double cost()` _(in Markets.Draw)_ |  |
| 388 | 3 | `public Draw quote(Good g, double units, Sectors sectors)` | What a draw WOULD come to, at today's prices, without taking anything - the quote the build screen shows and the affordability check uses. |
| 398 | 12 | `public Draw quote(Good g, double units, Sectors sectors, double takenAhead)` | ...with `takenAhead` units of the makers' stock drawn first by orders placed before this one (0.7.83: the refiners' bitumen a run's earlier roads take as they are placed - Game.buildRunInvoice()). |
| 418 | 41 | `public Draw draw(Good g, Sector buyer, String buyerKey, double units, Sectors sectors)` | Takes units of a good now, from whoever makes and holds it, and imports the rest. |

### READERS (lines 460-467)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 1 | `public double importedUnits(Good g)` | Units the world sold the city this month, of one good, from the markets' own record. |
| 466 | 1 | `public double exportedUnits(Good g)` |  |

### SAVE, RESET, THE REFORM (lines 468-514)

| line | len | member | says |
|---:|---:|---|---|
| 473 | 5 | **type** `public static final class State` | One market as a save carries it: the price it traded at and the strike behind it. |
| 479 | 14 | `public List<State> toState()` |  |
| 494 | 12 | `public void restore(List<State> saved)` |  |
| 507 | 3 | `public void reset()` |  |
| 511 | 3 | `public void redenominate(double scale)` |  |


# Markets.java - 411 lines · 19 methods · 0 constants · model

`ham/citybuildersim/Markets.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (22), [Good](Good.md) (20), [GoodsMarket](GoodsMarket.md) (16), [Trade](Trade.md) (6), [Sectors](Sectors.md) (4), [Game](Game.md) (1), [SupplierCredit](SupplierCredit.md) (1)

**Used by (21):** [BooksCheck](BooksCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BusinessInvestment](BusinessInvestment.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LuxuryRetail](LuxuryRetail.md), [MiningCheck](MiningCheck.md), [Motoring](Motoring.md), [OilCheck](OilCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [SectorFlow](SectorFlow.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 77 | THE MONTH |
| 282 | DRAWS - taken on demand, at the price of the month |
| 357 | READERS |
| 365 | SAVE, RESET, THE REFORM |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private final Map<Good, GoodsMarket> markets` |  |
| 371 | `public String good` |  |
| 372 | `public double price, flow, stock, demand` |  |
| 373 | `public double[] taken` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 366 | **type** `public final class Markets` | Every goods market in the city, and the month they clear in. |
| 50 | 3 | `public Markets()` |  |
| 54 | 1 | `public GoodsMarket get(Good g)` |  |
| 56 | 1 | `public Iterable<GoodsMarket> all()` |  |
| 59 | 3 | `public void setExchangeRate(double rate)` | City money per dollar, times the world's price level. |
| 72 | 4 | `public double getExchangeRate()` | The one rate every market here was told, read off one of them. |

### THE MONTH (lines 77-281)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 54 | `public void clearMonth(Sectors sectors, Game game)` |  |
| 137 | 12 | `private double orderValue(Sector s, boolean stock)` | What a sector's orders this month would come to, each at what a unit costs to bring in (GoodsMarket.landedPrice()): its orders for stock, or its inputs. |
| 151 | 14 | `private double coveredOrderValue(Sector s)` | ...and what its orders for the stock its suppliers' credit covers would come to (0.7.44; SupplierCredit): nothing without one. |
| 167 | 105 | `private void clear(Good g, Sectors sectors)` | Prices one good, then everybody trades it. |
| 274 | 7 | `private void trade(GoodsMarket m, Sector seller, Sector buyer, double units, double price)` | One local fill between two sectors, booked both sides. |

### DRAWS - taken on demand, at the price of the month (lines 282-356)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 3 | **type** `public record Draw(double units, double local, double imported, double localCost, double importCost)` | What a draw came to. |
| 288 | 1 | `public double cost()` _(in Markets.Draw)_ |  |
| 296 | 11 | `public Draw quote(Good g, double units, Sectors sectors)` | What a draw WOULD come to, at today's prices, without taking anything - the quote the build screen shows and the affordability check uses. |
| 315 | 41 | `public Draw draw(Good g, Sector buyer, String buyerKey, double units, Sectors sectors)` | Takes units of a good now, from whoever makes and holds it, and imports the rest. |

### READERS (lines 357-364)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 1 | `public double importedUnits(Good g)` | Units the world sold the city this month, of one good, from the markets' own record. |
| 363 | 1 | `public double exportedUnits(Good g)` |  |

### SAVE, RESET, THE REFORM (lines 365-411)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 5 | **type** `public static final class State` | One market as a save carries it: the price it traded at and the strike behind it. |
| 376 | 14 | `public List<State> toState()` |  |
| 391 | 12 | `public void restore(List<State> saved)` |  |
| 404 | 3 | `public void reset()` |  |
| 408 | 3 | `public void redenominate(double scale)` |  |


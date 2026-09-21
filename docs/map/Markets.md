# Markets.java - 338 lines · 17 methods · 0 constants · model

`ham/citybuildersim/Markets.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (17), [Good](Good.md) (16), [GoodsMarket](GoodsMarket.md) (14), [Trade](Trade.md) (6), [Sectors](Sectors.md) (4), [Game](Game.md) (1)

**Used by (16):** [BooksCheck](BooksCheck.md), [BusinessInvestment](BusinessInvestment.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [InfrastructureCheck](InfrastructureCheck.md), [LuxuryRetail](LuxuryRetail.md), [MiningCheck](MiningCheck.md), [Motoring](Motoring.md), [RailCheck](RailCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 74 | THE MONTH |
| 209 | DRAWS - taken on demand, at the price of the month |
| 284 | READERS |
| 292 | SAVE, RESET, THE REFORM |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private final Map<Good, GoodsMarket> markets` |  |
| 298 | `public String good` |  |
| 299 | `public double price, flow, stock, demand` |  |
| 300 | `public double[] taken` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 296 | **type** `public final class Markets` | Every goods market in the city, and the month they clear in. |
| 47 | 3 | `public Markets()` |  |
| 51 | 1 | `public GoodsMarket get(Good g)` |  |
| 53 | 1 | `public Iterable<GoodsMarket> all()` |  |
| 56 | 3 | `public void setExchangeRate(double rate)` | City money per dollar, times the world's price level. |
| 69 | 4 | `public double getExchangeRate()` | The one rate every market here was told, read off one of them. |

### THE MONTH (lines 74-208)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 40 | `public void clearMonth(Sectors sectors, Game game)` |  |
| 120 | 79 | `private void clear(Good g, Sectors sectors)` | Prices one good, then everybody trades it. |
| 201 | 7 | `private void trade(GoodsMarket m, Sector seller, Sector buyer, double units, double price)` | One local fill between two sectors, booked both sides. |

### DRAWS - taken on demand, at the price of the month (lines 209-283)

| line | len | member | says |
|---:|---:|---|---|
| 214 | 3 | **type** `public record Draw(double units, double local, double imported, double localCost, double importCost)` | What a draw came to. |
| 215 | 1 | `public double cost()` _(in Markets.Draw)_ |  |
| 223 | 11 | `public Draw quote(Good g, double units, Sectors sectors)` | What a draw WOULD come to, at today's prices, without taking anything - the quote the build screen shows and the affordability check uses. |
| 242 | 41 | `public Draw draw(Good g, Sector buyer, String buyerKey, double units, Sectors sectors)` | Takes units of a good now, from whoever makes and holds it, and imports the rest. |

### READERS (lines 284-291)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 1 | `public double importedUnits(Good g)` | Units the world sold the city this month, of one good, from the markets' own record. |
| 290 | 1 | `public double exportedUnits(Good g)` |  |

### SAVE, RESET, THE REFORM (lines 292-338)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 5 | **type** `public static final class State` | One market as a save carries it: the price it traded at and the strike behind it. |
| 303 | 14 | `public List<State> toState()` |  |
| 318 | 12 | `public void restore(List<State> saved)` |  |
| 331 | 3 | `public void reset()` |  |
| 335 | 3 | `public void redenominate(double scale)` |  |


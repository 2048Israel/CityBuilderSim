# Trade.java - 40 lines · 4 methods · 3 constants · model

`ham/citybuildersim/Trade.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> One fill: somebody sold somebody some units of a good at a price.
> 
> The month's trades are the ledger everything downstream reads. A sector's
> revenue is the trades it sold, its input cost the trades it bought; the
> sales tax is struck from who sold to whom (a local sale at the seller's
> rate, an export zero-rated, an import charged to the buyer); the balance
> of payments and the national accounts read the trades with the world.
> Nothing is computed twice, so nothing can disagree about a month - which
> is the rule this codebase kept being caught breaking while every sector
> booked its own version of the same sale.
> 
> SELLER AND BUYER ARE KEYS, not objects: a sector's saved name, or one of
> the three parties that are not sectors - WORLD, HOUSEHOLDS, CITY. Money is
> in thousands, as everywhere.

**Uses:** [Good](Good.md) (1)

**Used by (10):** [BooksCheck](BooksCheck.md), [CreditCheck](CreditCheck.md), [GoodsMarket](GoodsMarket.md), [LuxuryRetail](LuxuryRetail.md), [Markets](Markets.md), [Motoring](Motoring.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 25 | `Trade.WORLD` | `"world"` | The other side of every export and every import. |
| 28 | `Trade.HOUSEHOLDS` | `"households"` | The households, as a buyer of the basket. |
| 31 | `Trade.CITY` | `"city"` | The treasury, as a buyer of its own buildings' materials and repairs. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 19 | **type** `public record Trade(Good good, String seller, String buyer, double units, double price)` | One fill: somebody sold somebody some units of a good at a price. |
| 33 | 1 | `public double value()` |  |
| 35 | 1 | `public boolean isExport()` |  |
| 36 | 1 | `public boolean isImport()` |  |
| 39 | 1 | `public boolean isLocalSale()` | Sold to a local buyer - a sector, the households or the city. |


# RailCheck.java - 499 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/RailCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The railway: what it charges, who pays it, and what it does to the band.
> 
> WHY THIS HARNESS EXISTS. TradeCostCheck proved the decomposition - that every
> delivered price is still the literal it was, and that three quarters of the
> wedge is freight. This one is about the business that now charges for that
> freight, and it has four claims to hold that the earlier one cannot:
> 
>   1. A CITY WITH NO RAILWAY IS THE CITY IT WAS, to the bit, on all four
>      prices - the band and the net pair both. Everything below only matters
>      if this holds, because it is the promise that a mechanic this large did
>      not quietly move every existing save.
> 
>   2. THE TWO HALVES ADD UP. What leaves the band and what the railway bills
>      come to exactly the blended rate the shipper should pay, for any share
>      and any quote. This is the one piece of arithmetic in the whole design
>      that has to be right, and it is two lines of algebra that are easy to
>      get backwards - the first draft of this model had the exporter paying
>      the freight twice.
> 
>   3. THE MONEY IS CONSERVED. Every dollar of haulage on the railway's
>      revenue is a dollar on some shipper's input line, in the same month,
>      because the invoice and the charge are one call.
> 
>   4. THE PRICE IS A PRICE. An over-built network quotes near its floor and a
>      starved one quotes near the lorries, and in between it covers what it
>      costs to run plus a return on the track. A haulage rate that did not
>      respond to either would make this a subsidy rather than a sector.
> 
> See claude/transport-and-the-freight-band.md and sectors.Rail.

**Uses:** [Traffic](Traffic.md) (15), [Good](Good.md) (13), [Markets](Markets.md) (8), [Rail](Rail.md) (8), [Game](Game.md) (6), [Sector](Sector.md) (5), [GoodsMarket](GoodsMarket.md) (4), [BuildingManager](BuildingManager.md) (3), [GameFiles](GameFiles.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [Sectors](Sectors.md) (1), [Statement](Statement.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | · 1. A CITY WITH NO RAILWAY |
| 108 | · 2. THE TWO HALVES ADD UP |
| 174 | · 3. THE LAND, THE ROAD AND THE CATALOGUE |
| 244 | · 4. THE SECTOR, IN A CITY |
| 429 | · 5. AND IT SURVIVES A RELOAD |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 464 | **type** `public class RailCheck` | The railway: what it charges, who pays it, and what it does to the band. |
| 40 | 7 | `static void quietly(Runnable r)` |  |
| 48 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 53 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 59 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 63 | 3 | `static boolean near(double a, double b, double tol)` |  |
| 67 | 396 | `public static void main(String[] args)` |  |
| 473 | 4 | `static void lay(Game game, String name, int count)` | Track, handed to the city rather than waited for. |
| 483 | 16 | `static boolean allInputsAddUp(Game game)` | The goods bought, plus the services named, come to the input line - for every sector, every month. |


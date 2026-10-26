# RailCheck.java - 742 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/RailCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

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
> See claude/transport-and-the-freight-band.md and sectors.Freight.

**Uses:** [Good](Good.md) (21), [Traffic](Traffic.md) (15), [Freight](Freight.md) (13), [Ports](Ports.md) (11), [Sectors](Sectors.md) (9), [Markets](Markets.md) (8), [Sector](Sector.md) (7), [Game](Game.md) (6), [BuildingManager](BuildingManager.md) (5), [GoodsMarket](GoodsMarket.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [SectorState](SectorState.md) (2), [CarCheck](CarCheck.md) (2), [Founding](Founding.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [Haulage](Haulage.md) (1), [TrainSchedule](TrainSchedule.md) (1), [Statement](Statement.md) (1)

**Used by (1):** [GdpCheck](GdpCheck.md)

## Sections

| line | section |
|---:|---|
| 70 | · 1. A CITY WITH NO RAILWAY |
| 109 | · 2. THE TWO HALVES ADD UP |
| 175 | · 3. THE LAND, THE ROAD AND THE CATALOGUE |
| 246 | · 4. THE SECTOR, IN A CITY |
| 524 | · 5. AND IT SURVIVES A RELOAD |
| 614 | · 6. THE MONTH'S TRADE ACROSS A SAVE (A1, 0.7.46) |
| 656 | · 7. ITS TONNES BY KIND, THE MAP'S TRAINS' (0.8.2, batch FR1) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 706 | **type** `public class RailCheck` | The railway: what it charges, who pays it, and what it does to the band. |
| 41 | 7 | `static void quietly(Runnable r)` |  |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 54 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 60 | 3 | `static boolean same(double a, double b)` | Bitwise, not near. |
| 64 | 3 | `static boolean near(double a, double b, double tol)` |  |
| 68 | 627 | `public static void main(String[] args)` |  |
| 705 | 15 | `static void lay(Game game, String name, int count)` | Track, handed to the city rather than waited for. |
| 726 | 16 | `static boolean allInputsAddUp(Game game)` | The goods bought, plus the services named, come to the input line - for every sector, every month. |


# SectorFlow.java - 260 lines · 17 methods · 1 constants · model

`ham/citybuildersim/SectorFlow.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> One business's month as a flow (0.7.30): what went in, what its plant made
> of it and what held the plant back, and what came out - each good's units
> off the month's production rows, its money off the statement, the plant's
> six throttles and the rate they multiply to.
> 
> WHY. The Sectors screen's Operations page was the factory's block of
> label-and-figure lines (Sector.plantLines()) - "Could make", "Made",
> "Idled", "Sold at home", "Exported", "Wanted", "From the city", "Imported
> instead" a good at a time - and a note that said output was cut by the
> thinnest of five ratios when it is the product of six. Jerus: "the others
> are still full of text and the design could be more intuitive and fun".
> Redrawn as inputs → the plant → outputs (the project's
> spec-sectors-0730.md, D1), and this is that picture's figures, held by a
> harness (SectorFlowCheck) rather than worked out in the screen: the money
> a row carries adds up to the income statement's lines to the cent, and the
> cascade's last step is getOperatingRate() itself.
> 
> TWO MONTHS, AND WHICH IS WHICH. The units are the production rows
> (Sector.Output, Sector.Input): the month the plants just ran, the figures
> the operations page has always shown. The money is the statement the books
> closed on (Sector.statement(), SectorBooks), the Income page's. In a city
> that is not changing fast the two agree; a row's money is not its units
> times a price. The rows are not saved, so after a load the units are NOT
> COUNTED (NaN) until a month runs - the rule BuildCard.counted() uses for
> the same flows - and the money, which is saved, is shown.
> 
> Pure: it reads the sector and the city and changes nothing; the
> production rows are read without creating any (Sector.outputRow()).

**Uses:** [Good](Good.md) (10), [Sector](Sector.md) (8), [BuildCard](BuildCard.md) (4), [GoodsMarket](GoodsMarket.md) (2), [Game](Game.md) (1), [Statement](Statement.md) (1), [Markets](Markets.md) (1), [Construction](Construction.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (3):** [ReadPathCheck](ReadPathCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 157 | · · what went in |
| 184 | · · what came out |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `SectorFlow.THROTTLES` | `{ "staffed", "power", "water", "roads", "well", "vans" }` | The six throttles in getOperatingRate()'s order, as the screen names them. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 39 | 222 | **type** `public final class SectorFlow` | One business's month as a flow (0.7.30): what went in, what its plant made of it and what held the plant back, and what came out - each good's units off the month's production rows, its money off the statement, the pl... |
| 55 | 9 | **type** `public record In(Good good, String name, double wanted, double fromCity, double imported, double onHand, do...` | One good or service that went in. |
| 57 | 1 | `public boolean service()` _(in SectorFlow.In)_ |  |
| 59 | 1 | `public double units()` _(in SectorFlow.In)_ | Units bought, home and abroad. |
| 60 | 1 | `public double money()` _(in SectorFlow.In)_ |  |
| 62 | 1 | `public double cityShare()` _(in SectorFlow.In)_ | The city's share of the units bought; NaN with none bought or not counted. |
| 84 | 12 | **type** `public record Out(Good good, String name, double capacity, double made, double idled, double soldHome, doub...` | One good that came out, or a part of its revenue that is not a good. |
| 87 | 1 | `public boolean work()` _(in SectorFlow.Out)_ |  |
| 88 | 1 | `public double money()` _(in SectorFlow.Out)_ |  |
| 90 | 1 | `public double intoStock()` _(in SectorFlow.Out)_ | Made and not sold or shipped this month: into the warehouse (0 when the month sold more than it made). |
| 92 | 1 | `public double fromStock()` _(in SectorFlow.Out)_ | Sold or shipped beyond what was made: out of the warehouse (0 when it made more than it sold). |
| 94 | 1 | `public boolean underwater()` _(in SectorFlow.Out)_ | Whether a unit costs more to make than the city pays for it. |
| 112 | 11 | **type** `public record Plant(int standing, int onSite, long posts, double workers, double[] ratios, double[] cascade...` | The plant. |
| 114 | 1 | `public boolean none()` _(in SectorFlow.Plant)_ |  |
| 116 | 6 | `public int lowest()` _(in SectorFlow.Plant)_ | The throttle that cuts most: the lowest ratio, the first of equals; -1 with nothing standing. |
| 133 | 15 | **type** `public record Flow(Sector sector, List<In> inputs, List<Out> outputs, Plant plant, boolean counted, BuildCa...` | The flow. |
| 136 | 5 | `public double inputsMoney()` _(in SectorFlow.Flow)_ | The money in, goods and services: the statement's inputs line. |
| 142 | 5 | `public double revenueMoney()` _(in SectorFlow.Flow)_ | The money out, goods and work: the statement's revenue line. |
| 149 | 1 | `private SectorFlow()` |  |
| 151 | 71 | `public static Flow of(Game game, Sector sector)` |  |
| 224 | 20 | `public static Plant plant(Sector sector)` | The plant alone (the Sectors list's cards and every page's RUNNING AT). |
| 251 | 9 | `static double capacityNote(BuildCard.Note note, Good g)` | What a good the seller prices can serve, off the Build group's note: the shops' coverage for groceries, the counters' for a counter's trade, the kitchens' seats for meals; NaN for any other (homes are counted in doors... |


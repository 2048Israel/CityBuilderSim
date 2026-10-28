# TradeBand.java - 252 lines · 21 methods · 0 constants · model

`ham/citybuildersim/TradeBand.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The freight band of what the city trades, split into its legs, as the Trade
> tab draws it (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6,
> last bullet): the gap between what the world pays for a good and what it
> charges to bring one in, as the world's own margin, the long haul by mode
> (the world's lorries, the city's railway, the ships and the quay that works
> them) and the city leg from the gateway to the door.
> 
> WHY. Until FR2 the band was two figures and a bar on Infrastructure's Freight
> page - the world's margin, the freight still paid abroad, the railway's
> charge - and the Trade tab, where the city meets the world, named the prices
> (the markets' own, "the railway's freight in them") and nothing of what is in
> them. Since FR2 and FR3 a tonne that crosses the boundary moves in legs, each
> paid once and to somebody (spec 2): the long haul by lorry or ship abroad, by
> rail at home; at the quay, handling at home; and the city leg at home, to
> Freight's trucks or its siding. The study asks the band to say so on Trade.
> These are those figures, held by a harness (TradeBandCheck) to the getters
> that keep them rather than worked out in the screen, which only paints them.
> 
> TWO READINGS OF ONE BAND. A row is a good's gap A UNIT, in the city's money,
> at the prices and shares in force now: every piece the model's own getter
> (GoodsMarket.worldMargin(), freightInBand(), railInWedge(), the quote the
> quay and Haulage.legPrice() state), the first three of them summing to the
> band the price grid shows (importPrice() less exportPrice()), and with the
> railway's, the delivered gap (netImportPrice() less netExportPrice()). The
> whole is the same pieces over the month's trade: each good's crossings - what
> the books say it sold and bought abroad, divided by the price it cleared at
> - times half its two-way piece, a crossing paying one way. It is money at
> today's prices on last month's trade, because the model keeps the month's
> trade in money by good and not in units (the units are cleared at the
> strike), and says so; the exact bills of the month are Freight's own
> (getHaulageBilled(), getPaidAbroad(), Haulage's bill), which a screen on the
> Freight page prints.
> 
> WHICH GOODS. The ones the city can both buy and sell abroad with a price to
> measure against - Infrastructure's bars' set - so the band exists. Money that
> crossed in a good with no band (a service, a good only one way) is not in the
> whole, and its row is not here.
> 
> Every figure is finite whatever the city: a getter's NaN or infinity reads 0,
> a ratio with nothing under it is 0, and a city that has never traded has rows
> of the prices and a whole of zeros.
> 
> Pure: it reads the city and changes nothing.

**Uses:** [Good](Good.md) (3), [Ports](Ports.md) (3), [Sectors](Sectors.md) (2), [Game](Game.md) (1), [Markets](Markets.md) (1), [Freight](Freight.md) (1), [Haulage](Haulage.md) (1), [PortDivision](PortDivision.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (2):** [TradeBandCheck](TradeBandCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 60 | THE VOCABULARY |
| 101 | THE RECORDS |
| 177 | THE BAND |

## Enum constants

| line | constant | says |
|---:|---|---|
| 72 | `TradeBand.Part.MARGIN` |  |
| 73 | `TradeBand.Part.WORLD_LORRY` |  |
| 74 | `TradeBand.Part.RAIL` |  |
| 75 | `TradeBand.Part.SEA` |  |
| 76 | `TradeBand.Part.PORT` |  |
| 77 | `TradeBand.Part.CITY_LEG` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `private final String label, leg, paidTo` |  |
| 80 | `private final boolean abroad` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 197 | **type** `public final class TradeBand` | The freight band of what the city trades, split into its legs, as the Trade tab draws it (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6, last bullet): the gap between what the world pays for a good and ... |
| 58 | 1 | `private TradeBand()` |  |

### THE VOCABULARY (lines 60-100)

| line | len | member | says |
|---:|---:|---|---|
| 71 | 29 | **type** `public enum Part` | The pieces of the band, in the order the bar lays them: the world's own margin, the long haul by mode, and the city leg. |
| 82 | 6 | `Part(String label, String leg, boolean abroad, String paidTo)` _(in TradeBand.Part)_ |  |
| 89 | 1 | `public String label()` _(in TradeBand.Part)_ |  |
| 92 | 1 | `public String leg()` _(in TradeBand.Part)_ | The leg it belongs to, in words: "the world's margin", "the long haul" or "the city leg". |
| 95 | 1 | `public boolean abroad()` _(in TradeBand.Part)_ | Whether it is paid abroad (the Freight page's hatching): the world's margin, its lorries and its ships. |
| 98 | 1 | `public String paidTo()` _(in TradeBand.Part)_ | Who is paid, in words. |

### THE RECORDS (lines 101-176)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 1 | **type** `public record Piece(Part part, double amount)` | One piece of the band: which, and how much, in the city's money. |
| 113 | 26 | **type** `public record Row(Good good, double importPrice, double exportPrice, double crossed, List<Piece> pieces)` | One good's gap a unit at the prices in force: what the world pays and charges, the money that crossed in the good in the month the books last struck, and the pieces, one for every Part in Part order. |
| 115 | 1 | `public double amount(Part part)` _(in TradeBand.Row)_ | The piece of one part. |
| 118 | 1 | `public double band()` _(in TradeBand.Row)_ | The band the price grid shows: the world's margin, the lorries' freight and the ships' - importPrice() less exportPrice(). |
| 121 | 1 | `public double delivered()` _(in TradeBand.Row)_ | ...and the railway's charge on it: the delivered gap, netImportPrice() less netExportPrice(). |
| 124 | 1 | `public double toTheDoor()` _(in TradeBand.Row)_ | ...and the quay's handling and the city leg: the gap to the door. |
| 127 | 5 | `public double abroad()` _(in TradeBand.Row)_ | The part of the money paid abroad, in the city's money. |
| 134 | 1 | `public double share(Part part)` _(in TradeBand.Row)_ | A part as a share of the good's import price (the bars' scale); 0 where the good has no price. |
| 137 | 1 | `public double shareToTheDoor()` _(in TradeBand.Row)_ | The whole gap to the door as a share of the import price. |
| 146 | 27 | **type** `public record Whole(double crossed, List<Piece> pieces)` | The month's trade in the same pieces: the money that crossed in the goods the band measures (what they sold abroad and bought abroad), and each part's money on it at today's prices, one piece for every Part in Part or... |
| 148 | 1 | `public double amount(Part part)` _(in TradeBand.Whole)_ | The piece of one part. |
| 151 | 5 | `public double total()` _(in TradeBand.Whole)_ | All the pieces together: what the trade's gap to the door came to. |
| 158 | 5 | `public double abroad()` _(in TradeBand.Whole)_ | The part paid abroad, in money. |
| 165 | 4 | `public double shareOfGap(Part part)` _(in TradeBand.Whole)_ | A part's share of the gap (0 with no gap). |
| 171 | 1 | `public double shareOfTrade()` _(in TradeBand.Whole)_ | The gap to the door as a share of the money that crossed (0 when nothing did). |
| 175 | 1 | **type** `public record Band(Whole whole, List<Row> rows)` | The whole, and the goods' rows: those that crossed first (largest first), then the rest by the widest gap. |

### THE BAND (lines 177-252)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 60 | `public static Band of(Game game)` | The band of the city's trade, from its markets, its Freight sector and the books of the month last struck. |
| 244 | 3 | `private static double fin(double v)` | A finite figure: NaN and infinity read 0, so no screen ever draws or divides by one. |
| 249 | 3 | `private static double unit(double x)` | A share clamped to 0..1, and 0 for a number that is not one. |


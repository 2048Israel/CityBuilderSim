# WorldChain.java - 151 lines · 4 methods · 1 constants · model

`ham/citybuildersim/WorldChain.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> The world market's chain: for each good made of other goods, which goods and in what shares of its price, and the order the January solves
> the goods in (inputs first), so that a made good's price in a nation is what its inputs cost that nation plus its own part (CU2;
> docs/studies/spec-world-prices.md 3).
> 
> WHY THIS EXISTS. CU1 found rod priced apart from the cathode it is rolled from: WorldMarket solved each good alone, on its own price path, so by
> 2100 on the Historical path a cathode stood at 0.38 of the literals' level and the rod beside it at 2.4 times its inputs' cost, and a city
> rolling imported cathode grew rich on a pricing artifact (spec-copper-margin.md). Here a made good follows what it is made of, as the works
> that makes it does: the table is the works' recipe in buildings.json at the literals' mid-band prices, and a check holds each row to it.
> 
> Built once for a Base, from the goods the file models: a row whose made good is not modelled is skipped, an input that is not modelled is
> dropped (its share leaves the sum, so the good's own part grows). Pure data and a sort; nothing is saved.

**Uses:** [Good](Good.md) (7), [WorldMarket](WorldMarket.md) (1)

**Used by (2):** [WorldMarket](WorldMarket.md), [WorldMarketCheck](WorldMarketCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `WorldChain.RECIPE` | `List.of(row(COPPER_CONC, null, in(COPPER, 1)), row(ANODE, null, in(COPPER, 1)...` | The forty rows: each good a works makes, on that works' inputs at the shares buildings.json gives at the literals' mid-band prices (the rest of its price is its conversion, on the manufactures index), and each good th... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `final int[][] parent` | Per modelled good (the Base's traded order): its inputs' indices; empty for a root, a good that follows nothing. |
| 93 | `final double[][] share` | Per modelled good: its inputs' shares of its price, in parent's order. |
| 95 | `final double[] own` | Per modelled good: its own part of its price, 1 - sum(shares) for a made good (0 if below 1e-9), 1 for a root. |
| 97 | `final int[] order` | Every modelled good, roots first (in index order), then each made good once its inputs are placed. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 131 | **type** `final class WorldChain` | The world market's chain: for each good made of other goods, which goods and in what shares of its price, and the order the January solves the goods in (inputs first), so that a made good's price in a nation is what i... |
| 24 | 1 | **type** `record Row(Good made, String works, Good[] inputs, double[] shares)` | One made good: the works whose recipe gives its shares (its template's name in buildings.json; null for a good the price file prices wholly off another with no works between them), its inputs and their shares of its p... |
| 27 | 1 | **type** `private record In(Good good, double share)` | One input of a row and its share of the made good's price. |
| 29 | 1 | `private static In in(Good good, double share)` |  |
| 31 | 9 | `private static Row row(Good made, String works, In...ins)` |  |
| 100 | 48 | `WorldChain(WorldMarket.Traded[] traded, int[] indexOf)` | The chain on the goods a file models: traded in the Base's order, indexOf each Good's number among them (-1 for a good not modelled). |
| 150 | 1 | `boolean made(int gi)` | Whether a modelled good is made of others (it has inputs); a root has none. |


# GoldMining.java - 158 lines · 15 methods · 2 constants · sectors

`ham/citybuildersim/sectors/GoldMining.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Gold mines and their mills recover fine ounces at a geology-dependent grade.
> 
> WHY (GD4, spec-gold-money 4.4). Bullion can be made at home. The same
> ore throughput costs the same as its grade falls. Under Gold the mint
> buys the output with new money and keeps the metal in the vault.

**Uses:** [Good](Good.md) (17), [ForeignAccounts](ForeignAccounts.md) (5), [Sectors](Sectors.md) (2), [Resource](Resource.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [GoodsMarket](GoodsMarket.md) (2), [ExtractionSector](ExtractionSector.md) (1), [BuildingType](BuildingType.md) (1), [Game](Game.md) (1), [BuildCard](BuildCard.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (3):** [BuildingDataCheck](BuildingDataCheck.md), [GoldMiningCheck](GoldMiningCheck.md), [Sectors](Sectors.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 15 | `GoldMining.MINE_AND_MILL` | `152` | Permanent catalogue id, after R4's research labs at 148 through 151. |
| 18 | `GoldMining.ORE_PER_MONTH` | `30_000` | Ore milled a month at nameplate, tonnes (GD4, estimate). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private double mintOunces, mintPayment` | The accepted mint offer waiting for the next strike, ounces and thousands of D$. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 147 | **type** `public final class GoldMining extends ExtractionSector` | Gold mines and their mills recover fine ounces at a geology-dependent grade. |
| 23 | 9 | `public GoldMining()` |  |
| 35 | 10 | `public java.util.List<Line> ownLines(Game game)` | The deposits and the buyer of the mill's fine ounces. |
| 48 | 4 | `protected double madeAtRate(Good g)` | Fixed ore throughput, recovered at the fields' declining grades. |
| 55 | 3 | `public double getPlannedOutput(Good g)` | Bullion is offered straight from the mill to its buyer. |
| 61 | 5 | `public double getExportBoundOutput(Good g)` | The mint takes the offer at M. |
| 69 | 4 | `public double getCostPerUnit(Good g)` | Ore-based costs divided by the ounces that ore actually recovers. |
| 76 | 4 | `public double getMarginalCostPerUnit(Good g)` | The grade also decides whether another tonne of ore is worth milling. |
| 83 | 9 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | The plan's revenue follows the next field's grade, with ore costs held. |
| 95 | 14 | `protected void sellDirectOutput(Good g, GoodsMarket market, double units)` | A mint purchase is a domestic receipt funded by new base money. |
| 112 | 8 | `protected void beforeBank()` | Settles the accepted offer in the same audit window as its sale reaches cash. |
| 123 | 5 | `protected void saveExtras(java.util.Map<String, Double> extras)` | An accepted offer survives the gap between production and the next strike. |
| 130 | 5 | `protected void restoreExtras(java.util.Map<String, Double> extras)` |  |
| 138 | 4 | `protected void redenominateExtras(double scale)` | A change of currency unit changes the accepted payment and keeps its fine ounces. |
| 144 | 4 | `protected void resetExtras()` |  |
| 151 | 7 | `public void produceStock(Good g, GoodsMarket market)` | Deplete precisely what was made, once, after the input-order machinery ran. |


# CpiReferenceFinding.java - 195 lines · 11 methods · 1 constants · model

`ham/citybuildersim/CpiReferenceFinding.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Actual reference expenditure and purchase coverage, using the pre-correction API.
> WHY: a component-count check cannot show the omitted household money or the
> unsupported luxury truncation. This finding can run unchanged on old production.

**Uses:** [PriceIndex](PriceIndex.md) (21), [Good](Good.md) (13), [Game](Game.md) (7), [HouseholdBalance](HouseholdBalance.md) (5), [Founding](Founding.md) (4), [GameFiles](GameFiles.md) (3), [Household](Household.md) (2), [Trade](Trade.md) (2), [BuildingManager](BuildingManager.md) (1), [GoodsMarket](GoodsMarket.md) (1), [OilCheck](OilCheck.md) (1), [Motoring](Motoring.md) (1)

**Used by (1):** [CpiCoverageFinding](CpiCoverageFinding.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 14 | `CpiReferenceFinding.OUT` | `System.out` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 184 | **type** `public final class CpiReferenceFinding` | Actual reference expenditure and purchase coverage, using the pre-correction API. |
| 15 | 1 | **type** `record Town(Game game, GameFiles files)` |  |
| 17 | 4 | `static void check(String label, boolean ok)` |  |
| 22 | 5 | `static void close(String label, double actual, double expected)` |  |
| 28 | 7 | `static void quietly(Runnable action)` |  |
| 36 | 18 | `static void referenceWeights()` |  |
| 56 | 1 | `static Town actualTown()` | CarCheck's caused buyer premise, on real scratch files, with operating filling stations. |
| 58 | 24 | `static Town actualTown(Founding founding, String label)` |  |
| 83 | 5 | `static double covered(Game g, String name)` |  |
| 89 | 5 | `static double coveredPrice(Game g, String name)` |  |
| 95 | 60 | `static void actualPurchases(Town town)` |  |
| 156 | 31 | `static void actualHomeFuel(Town town)` |  |
| 188 | 7 | `public static void main(String[] args)` |  |


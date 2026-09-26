# Materials.java - 88 lines · 4 methods · 1 constants · sectors

`ham/citybuildersim/sectors/Materials.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The materials plant. THE SEVENTH SECTOR (2026-09-11, Jerus's call).
> 
> Until the sector template the Construction Materials Plant's 160 units a
> month went into the city's yard for free, and the builders imported the
> rest at a fixed world price - a building that was never paid for what it
> made. It sells on the materials market now: every build order and every
> repair draws on the yard first, then on this plant's stock at the local
> price, then on the world. A city that builds hard makes materials dear,
> and a plant near the builders is the first thing in the game that gets
> cheaper for the buyer AND richer for the maker at once.
> 
> Stockable, importable, exportable - the same three as food, so the same
> template rules: it makes for the demand it can see, holds three months of
> output, and ships spare nameplate abroad at the export wedge.

**Uses:** [Good](Good.md) (3), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (1):** [Sectors](Sectors.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `Materials.FIRST_PLANT_UTILISATION` | `.5` | Half a plant's nameplate, a month, before the first one is sunk. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 66 | **type** `public final class Materials extends Sector` | The materials plant. |
| 35 | 7 | `public Materials()` |  |
| 44 | 1 | `public double firstPlantUtilisation()` |  |
| 63 | 6 | `public double visibleDemandOver(double months, ham.citybuildersim.EconomyManager economy)` | ITS CUSTOMERS' ORDER BOOK IS PUBLIC. |
| 82 | 6 | `public double getPlannedOutput(Good g)` | THE SHED IS THE BUSINESS. |


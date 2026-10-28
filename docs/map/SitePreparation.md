# SitePreparation.java - 70 lines · 3 methods · 7 constants · model

`ham/citybuildersim/SitePreparation.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> What it takes to prepare a foothill site for a building, as builders' work: the earth a pad is cut and filled in
> and the wall where it meets the slope above, priced from published unit rates and turned into construction
> points at the builders' own price for a House. Pure arithmetic: LandManager measures how much foothill an order
> has to prepare (foothillToPrepareSqFt()), Game.quoteBuild() prices it into the quote, and the points are owed by
> the order's site like any work (BuildingsStacks.addPreparation()).
> 
> WHY. spec-earth2-terrain 4.1 sets the shape - earth moved a g x W / 4 a m2 of pad, one wall the height of the
> pad's drop - and leaves the rates and the class's grade to this batch (ET2). The rule it gave is that no
> coefficient is laid on top: a foothill pad costs what its earthworks cost. Written as dollars, though, the figure
> would have drifted from the game's own prices, which are founding-dollar figures in buildings.json struck at the
> expected price level and with the builders' wages (BuildingManager.nonMaterialCost()); so the work is turned into
> points once, against the House's founding price a point, and the money for those points is the builders' price
> for house work today. It keeps up with wages as every building's labour does, and drifts with nothing else.

**Uses:** [LandManager](LandManager.md) (1)

**Used by (7):** [Game](Game.md), [LandClassCheck](LandClassCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [RunWorks](RunWorks.md), [RunWorksCheck](RunWorksCheck.md), [SlopeCheck](SlopeCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 28 | `SitePreparation.MEAN_FOOTHILL_GRADE` | `0.19` | The foothill class's mean grade (10% to 30%), 19%: fitted here from the study's measured class shares by an exponential fall of land with grade between the class edges - 17.9% to 19.7% at every one of nine sites (Bogo... |
| 31 | `SitePreparation.PAD_WIDTH_M` | `30` | The width of a pad across the contours, metres: one plot (30 m). |
| 34 | `SitePreparation.EARTH_M3_PER_M2` | `MEAN_FOOTHILL_GRADE * PAD_WIDTH_M / 4` | Earth moved a m2 of pad, m3: balanced cut and fill on a pad W wide on a grade g moves g x W / 4 (spec 4.1), 1.425. |
| 37 | `SitePreparation.WALL_M2_PER_M2` | `MEAN_FOOTHILL_GRADE` | Wall face a m2 of pad, m2: one wall the height of the pad's drop, g m2 of face per m2 of pad (spec 4.1). |
| 44 | `SitePreparation.EARTH_USD_PER_M3` | `20` | US dollars a m3 of earth, balanced cut and fill with compaction and no haul off site: US$15-30 a cubic yard (about $20-39 a m3) is the commonest 2026 benchmark (snaptrude.com/blog/cut-fill-earthwork-calculator-site-gr... |
| 51 | `SitePreparation.WALL_USD_PER_M2` | `430` | US dollars a m2 of retaining wall, face area: a segmental block wall is US$15-35 a sq ft in national guides (fixr.com/costs/retaining-wall-building) and $70-140 for an engineered wall with geogrid in San Diego (instal... |
| 54 | `SitePreparation.USD_PER_M2` | `EARTH_M3_PER_M2 * EARTH_USD_PER_M3 + WALL_M2_PER_M2 * WALL_USD_PER_M2` | US dollars to prepare a m2 of foothill: its earth and its wall at their rates (about $110). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 53 | **type** `public final class SitePreparation` | What it takes to prepare a foothill site for a building, as builders' work: the earth a pad is cut and filled in and the wall where it meets the slope above, priced from published unit rates and turned into constructi... |
| 20 | 1 | `private SitePreparation()` |  |
| 61 | 4 | `public static double pointsPerM2(double houseFoundingCashPerPoint)` | Construction points to prepare a m2 of foothill: its dollars, in the thousands buildings.json counts in, over what the builders charge a point of house work at founding - the House's founding cash cost over its points... |
| 67 | 3 | `public static double points(double sqFt, double houseFoundingCashPerPoint)` | ...and for `sqFt` square feet of foothill; 0 for none. |


# BridgeGround.java - 72 lines · 7 methods · 0 constants · model

`ham/citybuildersim/BridgeGround.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The ground a bridge is surveyed over, read from the World and the city's land: the Earth's terrain, relief and landmass a
> tile at a time, the city's ownership from the land's grid, and the linked set from the books by landmass.
> 
> WHY THIS EXISTS. BridgeSurvey is a function of a small interface so that the harness can hand it a grid of its own
> making; this is the interface's one reader of the real thing (ET6, spec-earth2-terrain 6.3: the model reads World and
> CityLand, never the map). It reads nothing it does not need: a tile's relief and landmass only where the city owns dry
> ground in it, and the landmass of a plot the data names none for is home's, as Landmasses counts it.

**Uses:** [World](World.md) (11), [BridgeSurvey](BridgeSurvey.md) (3), [CityLand](CityLand.md) (2), [Landmasses](Landmasses.md) (2), [LandGrid](LandGrid.md) (2)

**Used by (1):** [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 16 | `private final World world` |  |
| 17 | `private final CityLand land` |  |
| 18 | `private final Landmasses landmasses` |  |
| 19 | `private final int[] ids` |  |
| 20 | `private final int[] held` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 59 | **type** `final class BridgeGround implements BridgeSurvey.Ground` | The ground a bridge is surveyed over, read from the World and the city's land: the Earth's terrain, relief and landmass a tile at a time, the city's ownership from the land's grid, and the linked set from the books by... |
| 22 | 5 | `BridgeGround(World world, CityLand land, Landmasses landmasses)` |  |
| 28 | 1 | `public int home()` |  |
| 30 | 1 | `public int[] linkedIds()` |  |
| 32 | 13 | `public long[] ownedTiles()` |  |
| 47 | 5 | `public boolean mayHold(long tx, long ty, int landmass)` | A tile can hold dry ground of a landmass off home only where the landmass layer has a cell of it within the reach of the tile (World.idsNear(): a window of cells, no plot read); home's ground can lie anywhere. |
| 53 | 17 | `public void tile(long tx, long ty, BridgeSurvey.Tile out)` |  |
| 71 | 1 | `public double depthM(long x, long y)` |  |


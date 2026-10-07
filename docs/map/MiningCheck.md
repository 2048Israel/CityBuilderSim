# MiningCheck.java - 659 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/MiningCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Ore, from the band it clears in to whether it makes steel worth building.
> 
> The point of this feature is one number: a Steel Foundry earns about $9,650 a
> month on a $3.6M asset, which is a quarter of a percent and the reason nobody
> builds one. If local ore does not move that number substantially, everything
> else here is decoration.
> 
> So the last section measures it directly - the same foundry, the same city,
> with and without a mine - rather than asserting that the parts are wired
> together and hoping.

**Uses:** [Good](Good.md) (20), [Game](Game.md) (12), [World](World.md) (6), [CityLand](CityLand.md) (5), [GameFiles](GameFiles.md) (5), [GoodsMarket](GoodsMarket.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (3), [BuildingManager](BuildingManager.md) (3), [Sector](Sector.md) (3), [LandParcel](LandParcel.md) (2), [Resource](Resource.md) (2), [Deposit](Deposit.md) (2), [Markets](Markets.md) (2), [Sectors](Sectors.md) (2), [BuildingType](BuildingType.md) (1), [Mining](Mining.md) (1), [Statement](Statement.md) (1), [LandManager](LandManager.md) (1)

**Used by (1):** [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 114 | · 1. the band |
| 185 | · 2. a mine needs ground with ore in it |
| 218 | · THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT |
| 284 | · 3. does it actually pay? |
| 309 | · "BARELY BREAKS EVEN" WAS A CONSEQUENCE OF A FAKE STEEL PRICE. |
| 352 | · 4. and is the mine worth sinking? |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |
| 23 | `static PrintStream out` |  |
| 24 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 640 | **type** `public class MiningCheck` | Ore, from the band it clears in to whether it makes steel worth building. |
| 26 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 31 | 9 | `static void close(String label, double actual, double expected)` |  |
| 41 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 59 | 26 | `static void makeRoom(Game game, double sqFt, boolean wantDeposit)` | Buys land until the city can fit what is coming, deposits included. |
| 87 | 21 | `static LandParcel towardIron(Game game)` | The offer standing in the lane of the iron field nearest the city's site that it does not own: pushing that lane out is the shortest way to a deposit. |
| 109 | 334 | `public static void main(String[] args) throws Exception` |  |
| 464 | 187 | `static double[] foundryIncome(GameFiles files, Path root, boolean withMine) throws Exception` | The measurement: the same foundry, in the same city, with and without a mine feeding it. |
| 652 | 7 | `static void cleanUp(Path root)` |  |


# MiningCheck.java - 681 lines · 9 methods · 0 constants · harnesses

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

**Uses:** [Good](Good.md) (20), [Game](Game.md) (12), [World](World.md) (6), [LandParcel](LandParcel.md) (5), [GameFiles](GameFiles.md) (5), [GoodsMarket](GoodsMarket.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (3), [BuildingManager](BuildingManager.md) (3), [Sector](Sector.md) (3), [Resource](Resource.md) (2), [Deposit](Deposit.md) (2), [Markets](Markets.md) (2), [Sectors](Sectors.md) (2), [CityLand](CityLand.md) (1), [LegacyLand](LegacyLand.md) (1), [LandMarket](LandMarket.md) (1), [BuildingType](BuildingType.md) (1), [Mining](Mining.md) (1), [Statement](Statement.md) (1), [LandManager](LandManager.md) (1)

**Used by (4):** [LandCheck](LandCheck.md), [MapCheck](MapCheck.md), [OilCheck](OilCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 136 | · 1. the band |
| 207 | · 2. a mine needs ground with ore in it |
| 240 | · THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT |
| 306 | · 3. does it actually pay? |
| 331 | · "BARELY BREAKS EVEN" WAS A CONSEQUENCE OF A FAKE STEEL PRICE. |
| 374 | · 4. and is the mine worth sinking? |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |
| 23 | `static PrintStream out` |  |
| 24 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 662 | **type** `public class MiningCheck` | Ore, from the band it clears in to whether it makes steel worth building. |
| 26 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 31 | 9 | `static void close(String label, double actual, double expected)` |  |
| 41 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 60 | 26 | `static void makeRoom(Game game, double sqFt, boolean wantDeposit)` | Buys land until the city can fit what is coming, deposits included. |
| 88 | 20 | `static LandParcel towardIron(Game game)` | The offer standing nearest the iron field nearest the city's site that it does not own: buying toward it is the shortest way to a deposit (0.7.67; that field's lane pushed out until 0.7.66). |
| 116 | 14 | `static LandParcel nearestOffer(LandMarket market, long x, long y)` | The offer standing nearest plot (x, y), L-infinity from its rectangle, the lower id on a tie; null with nothing listed. |
| 131 | 334 | `public static void main(String[] args) throws Exception` |  |
| 486 | 187 | `static double[] foundryIncome(GameFiles files, Path root, boolean withMine) throws Exception` | The measurement: the same foundry, in the same city, with and without a mine feeding it. |
| 674 | 7 | `static void cleanUp(Path root)` |  |


# MiningCheck.java - 696 lines · 9 methods · 1 constants · harnesses

`ham/citybuildersim/MiningCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (20), [Game](Game.md) (12), [World](World.md) (6), [LandParcel](LandParcel.md) (5), [GameFiles](GameFiles.md) (5), [GoodsMarket](GoodsMarket.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (3), [BuildingManager](BuildingManager.md) (3), [Sector](Sector.md) (3), [Resource](Resource.md) (2), [Deposit](Deposit.md) (2), [Markets](Markets.md) (2), [Sectors](Sectors.md) (2), [CityLand](CityLand.md) (1), [LegacyLand](LegacyLand.md) (1), [LandMarket](LandMarket.md) (1), [Founding](Founding.md) (1), [BuildingType](BuildingType.md) (1), [Mining](Mining.md) (1), [Statement](Statement.md) (1), [LandManager](LandManager.md) (1)

**Used by (7):** [LandCheck](LandCheck.md), [MapCheck](MapCheck.md), [OilCheck](OilCheck.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 150 | · 1. the band |
| 221 | · 2. a mine needs ground with ore in it |
| 255 | · THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT |
| 321 | · 3. does it actually pay? |
| 346 | · "BARELY BREAKS EVEN" WAS A CONSEQUENCE OF A FAKE STEEL PRICE. |
| 389 | · 4. and is the mine worth sinking? |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `MiningCheck.IRON_SEED` | `35` | A world whose founding site has an iron field near it, as the default world's had until 0.7.98 (35 sites, 449 Mt, 0.87 km out): 35, whose nearest is 36 sites, 471 Mt, 0.97 km out on dry ground, none in a new city's ce... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |
| 23 | `static PrintStream out` |  |
| 24 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 677 | **type** `public class MiningCheck` | Ore, from the band it clears in to whether it makes steel worth building. |
| 38 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 43 | 9 | `static void close(String label, double actual, double expected)` |  |
| 53 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 73 | 27 | `static void makeRoom(Game game, double sqFt, boolean wantDeposit)` | Buys land until the city can fit what is coming, deposits included. |
| 102 | 20 | `static LandParcel towardIron(Game game)` | The offer standing nearest the iron field nearest the city's site that it does not own: buying toward it is the shortest way to a deposit (0.7.67; that field's lane pushed out until 0.7.66). |
| 130 | 14 | `static LandParcel nearestOffer(LandMarket market, long x, long y)` | The offer standing nearest plot (x, y), L-infinity from its rectangle, the lower id on a tie; null with nothing listed. |
| 145 | 335 | `public static void main(String[] args) throws Exception` |  |
| 501 | 187 | `static double[] foundryIncome(GameFiles files, Path root, boolean withMine) throws Exception` | The measurement: the same foundry, in the same city, with and without a mine feeding it. |
| 689 | 7 | `static void cleanUp(Path root)` |  |


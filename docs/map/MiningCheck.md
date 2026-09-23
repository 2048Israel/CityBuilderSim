# MiningCheck.java - 609 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/MiningCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (20), [Game](Game.md) (13), [GameFiles](GameFiles.md) (5), [GoodsMarket](GoodsMarket.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (3), [BuildingManager](BuildingManager.md) (3), [Sector](Sector.md) (3), [Markets](Markets.md) (2), [Sectors](Sectors.md) (2), [LandParcel](LandParcel.md) (1), [BuildingType](BuildingType.md) (1), [Mining](Mining.md) (1), [Statement](Statement.md) (1), [LandManager](LandManager.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | · 1. the band |
| 140 | · 2. a mine needs ground with ore in it |
| 173 | · THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT |
| 234 | · 3. does it actually pay? |
| 259 | · "BARELY BREAKS EVEN" WAS A CONSEQUENCE OF A FAKE STEEL PRICE. |
| 302 | · 4. and is the mine worth sinking? |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |
| 23 | `static PrintStream out` |  |
| 24 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 590 | **type** `public class MiningCheck` | Ore, from the band it clears in to whether it makes steel worth building. |
| 26 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 31 | 9 | `static void close(String label, double actual, double expected)` |  |
| 41 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 49 | 14 | `static void makeRoom(Game game, double sqFt, boolean wantDeposit)` | Buys land until the city can fit what is coming, deposits included. |
| 64 | 329 | `public static void main(String[] args) throws Exception` |  |
| 414 | 187 | `static double[] foundryIncome(GameFiles files, Path root, boolean withMine) throws Exception` | The measurement: the same foundry, in the same city, with and without a mine feeding it. |
| 602 | 7 | `static void cleanUp(Path root)` |  |


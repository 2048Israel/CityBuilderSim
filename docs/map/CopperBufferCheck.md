# CopperBufferCheck.java - 182 lines · 9 methods · 2 constants · harnesses

`ham/citybuildersim/CopperBufferCheck.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> A stock buffer must not buy feed worth more than its undemanded output (CPB1).
> 
> WHY. WN3 covered a refinery with a full shed; an empty shed still bought
> imported anode at a loss solely to reach Sector.DUMP_THRESHOLD. Exercise
> the real market, strike and accounts, keeping the recipe and GDP laws.

**Uses:** [Good](Good.md) (45), [CopperRefining](CopperRefining.md) (12), [Game](Game.md) (11), [Sector](Sector.md) (11), [GoodsMarket](GoodsMarket.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (3), [BuildingManager](BuildingManager.md) (2), [EconomyManager](EconomyManager.md) (2), [NationalAccounts](NationalAccounts.md) (2), [Trade](Trade.md) (2), [GameFiles](GameFiles.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `CopperBufferCheck.out` | `System.out` |  |
| 17 | `CopperBufferCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 15 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 169 | **type** `public class CopperBufferCheck` | A stock buffer must not buy feed worth more than its undemanded output (CPB1). |
| 19 | 4 | `static void check(String label, boolean ok)` |  |
| 24 | 3 | `static boolean near(double a, double b)` |  |
| 28 | 19 | `static Game fixture(String label, double feed, double sale)` |  |
| 48 | 5 | `static void clear(Game g)` |  |
| 54 | 8 | `static void accounts(Game g)` |  |
| 63 | 38 | `static void loss()` |  |
| 102 | 43 | `static void demandAndProfit()` |  |
| 146 | 28 | `static void existingStock()` |  |
| 175 | 7 | `public static void main(String[] args)` |  |


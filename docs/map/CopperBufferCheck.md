# CopperBufferCheck.java - 250 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/CopperBufferCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> A stock buffer must not buy feed worth more than its undemanded output (CPB1).
> 
> WHY. WN3 covered a refinery with a full shed; an empty shed still bought
> imported anode at a loss solely to reach Sector.DUMP_THRESHOLD. Exercise
> the real market, strike and accounts, keeping the recipe and GDP laws.

**Uses:** [Good](Good.md) (77), [CopperRefining](CopperRefining.md) (18), [Sector](Sector.md) (16), [Game](Game.md) (13), [GoodsMarket](GoodsMarket.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (5), [BuildingManager](BuildingManager.md) (4), [OldMetal](OldMetal.md) (4), [EconomyManager](EconomyManager.md) (2), [NationalAccounts](NationalAccounts.md) (2), [Trade](Trade.md) (2), [GameFiles](GameFiles.md) (1)

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
| 14 | 237 | **type** `public class CopperBufferCheck` | A stock buffer must not buy feed worth more than its undemanded output (CPB1). |
| 19 | 4 | `static void check(String label, boolean ok)` |  |
| 24 | 3 | `static boolean near(double a, double b)` |  |
| 28 | 19 | `static Game fixture(String label, double feed, double sale)` |  |
| 48 | 5 | `static void clear(Game g)` |  |
| 54 | 8 | `static void accounts(Game g)` |  |
| 63 | 38 | `static void loss()` |  |
| 102 | 54 | `static void demandAndProfit()` |  |
| 157 | 51 | `static void committedFeed()` |  |
| 209 | 32 | `static void existingStock()` |  |
| 242 | 8 | `public static void main(String[] args)` |  |


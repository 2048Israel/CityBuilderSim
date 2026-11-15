# ER1Check.java - 112 lines · 4 methods · 1 constants · harnesses

`ham/citybuildersim/ER1Check.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> The growth hold when a full city's treasury cannot buy a whole land offer.
> 
> WHY: ER1's Rotterdam replay has willing private investors, no free plots,
> and a builder with no orders. This fixture consumes the free ground and
> removes the city's purchasing cash, then tests the actual private build
> path each month. Funding the land buyer must release the same order.
> The long replay's measurements belong in docs/queue/ER1-notes.md.

**Uses:** [LongPlaytest](LongPlaytest.md) (6), [Game](Game.md) (3), [LandManager](LandManager.md) (3), [BuildingManager](BuildingManager.md) (2), [JobType](JobType.md) (2), [LandMarket](LandMarket.md) (2), [GameFiles](GameFiles.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Investor](Investor.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [LandParcel](LandParcel.md) (1), [BuildAdvice](BuildAdvice.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 19 | `ER1Check.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Suppresses the game's month narration while the fixture runs. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 16 | `static int fails` |  |
| 17 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 98 | **type** `public class ER1Check` | The growth hold when a full city's treasury cannot buy a whole land offer. |
| 21 | 4 | `static void check(String label, boolean ok)` |  |
| 26 | 5 | `static void quietly(Runnable action)` |  |
| 32 | 4 | `static long posts(BuildingManager buildings)` |  |
| 37 | 75 | `public static void main(String[] args) throws Exception` |  |


# ConservationCheck.java - 415 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/ConservationCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Nothing is created and nothing is destroyed.
> 
> ==================== WHY THIS FILE EXISTS ====================
> 
> A deliberate audit in September 2026 turned up three bugs that all twenty-two
> existing harnesses and four thousand months of `LongPlaytest` had missed:
> 
>   1. Loading a save into a running game DOUBLED every building. loadGameSave()
>      called only initialize(), which is guarded and therefore a no-op once a
>      game is running, and the reset inside loadGame() was commented out.
>      Measured: house capacity 2,720 -> 2,720 -> 5,340.
> 
>   2. The food warehouse never drained. updateFinalIndustrialHandler()
>      subtracted two fields that only a dead method ever wrote, so stock sat at
>      capacity while the mills booked revenue on it every month and the shops
>      added inventory the mills never lost.
> 
>   3. 73% of the city's electricity was billed to nobody. The utility collected
>      on every building's draw; only four sector categories were ever charged.
> 
> Every one of them is the same KIND of bug and none of them is a broken
> mechanism. The existing harnesses assert a great deal about mechanisms - that
> a bond amortises, that a streak survives a save, that households fit their
> homes - and almost nothing about whether the books balance. A mechanism can be
> perfectly correct while quietly manufacturing food.
> 
> So this file asserts conservation laws and nothing else. One per commodity,
> one per money flow, plus the one that says a city is the same city after you
> load it. They are cheap, they are unglamorous, and they are the only thing
> that would have caught any of the three.
> 
> THE BLIND QUADRANT. Bug 1 was invisible BY CONSTRUCTION: every other harness
> builds a fresh Game per case, so the same Game object is never loaded into
> twice. Section 4 deliberately reuses one. Any future bug that only appears
> when state survives across a load or a new game lives in that quadrant too.
> 
> ==============================================================

**Uses:** [Game](Game.md) (13), [GameFiles](GameFiles.md) (7), [Good](Good.md) (5), [Sector](Sector.md) (4), [BuildingManager](BuildingManager.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Sectors](Sectors.md) (1), [Founding](Founding.md) (1), [EconomyManager](EconomyManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1)

## Sections

| line | section |
|---:|---|
| 123 | · 1. FOOD |
| 214 | · 2. ELECTRICITY |
| 237 | · 3. WATER |
| 246 | · 4. A CITY IS THE SAME CITY AFTER YOU LOAD IT |
| 315 | · 5. WHAT A RELOAD MUST NOT FORGET |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |
| 50 | `static PrintStream out` |  |
| 51 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 47 | 369 | **type** `public class ConservationCheck` | Nothing is created and nothing is destroyed. |
| 56 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 63 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 69 | 43 | `static Game city(Path root, String name, int months) throws Exception` | A city with something of everything in it, so every law has something to test. |
| 113 | 281 | `public static void main(String[] args) throws Exception` |  |
| 396 | 11 | `static double[] snapshot(Game g)` | The figures a load must not move. |
| 408 | 7 | `static void cleanUp(Path root)` |  |


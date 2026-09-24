# NewGameCheck.java - 363 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/NewGameCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Does "Start New Game" actually start a new game?
> 
> Plays a city hard, calls newGame(), and compares the result field by field
> against a Game that has never been played at all. Anything that differs is the
> previous city's fingerprints on a fresh one.
> 
> WHY THIS IS A LIST OF FIELDS AND NOT A LIST OF ASSERTIONS
> 
> The bug this exists for was not that someone reset the wrong thing. It was
> that resetGame() cleared the fields somebody had remembered to add to it, and
> the list had fallen twenty-three fields behind - a new city inherited $81,777k
> of construction cash, $15,402k of business debt, 1,868 units of the previous
> city's food, and 122 months of someone else's graph history.
> 
> A test that checks the same handful of things the reset already handled would
> have passed throughout. So this sweeps everything it can reach and fails on
> ANY difference, including fields that do not exist yet.

**Uses:** [Game](Game.md) (18), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [Sector](Sector.md) (2), [Good](Good.md) (2), [GameFiles](GameFiles.md) (2), [EconomyManager](EconomyManager.md) (1), [PopulationManager](PopulationManager.md) (1), [Retail](Retail.md) (1), [RealEstate](RealEstate.md) (1), [ServicesManager](ServicesManager.md) (1), [Construction](Construction.md) (1), [BuildingManager](BuildingManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Statement](Statement.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 191 | · 1. what a city that never existed looks like |
| 208 | · 2. live in one, hard |
| 258 | · 3. start a new one |
| 279 | · 4. and it is actually playable |
| 305 | · 5. a new game after a LOAD, too |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 337 | **type** `public class NewGameCheck` | Does "Start New Game" actually start a new game? |
| 31 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 36 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 44 | 141 | `static Map<String, Double> snapshot(Game g)` | Everything a previous city could possibly leave behind. |
| 186 | 169 | `public static void main(String[] args) throws Exception` |  |
| 356 | 7 | `static void cleanUp(Path root)` |  |


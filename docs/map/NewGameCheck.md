# NewGameCheck.java - 858 lines · 12 methods · 2 constants · harnesses

`ham/citybuildersim/NewGameCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
> 
> AND WHAT A CITY IS FOUNDED WITH (0.7.10), sections 6-11: since Start New
> Game founds a city from a record - its name, its money, a treasury, a vault
> and a world (Founding) - the same question is asked of every door into a
> city, and of the endowment's job against the catalogue's own costs. The
> list is in the section's banner, FOUNDING A CITY.

**Uses:** [Game](Game.md) (100), [Founding](Founding.md) (81), [Currency](Currency.md) (39), [WorldEconomy](WorldEconomy.md) (20), [ForeignAccounts](ForeignAccounts.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [GameFiles](GameFiles.md) (3), [LongTermBond](LongTermBond.md) (3), [DebtQuote](DebtQuote.md) (3), [Sector](Sector.md) (2), [Good](Good.md) (2), [EconomyManager](EconomyManager.md) (1), [PopulationManager](PopulationManager.md) (1), [Retail](Retail.md) (1), [RealEstate](RealEstate.md) (1), [ServicesManager](ServicesManager.md) (1), [Construction](Construction.md) (1), [BuildingManager](BuildingManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Statement](Statement.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Debt](Debt.md) (1), [MoneyAudit](MoneyAudit.md) (1)

**Used by (1):** [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 215 | · 1. what a city that never existed looks like |
| 232 | · 2. live in one, hard |
| 291 | · 3. start a new one |
| 312 | · 4. and it is actually playable |
| 338 | · 5. a new game after a LOAD, too |
| 383 | · 6-11. FOUNDING A CITY (0.7.10) |
| 392 | 6-11. FOUNDING A CITY (0.7.10) |
| 421 | · · 6 |
| 527 | · · 7 |
| 580 | · · 8 |
| 635 | · · 9 |
| 652 | · · 10 |
| 678 | · · 11 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 837 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 838 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 826 | **type** `public class NewGameCheck` | Does "Start New Game" actually start a new game? |
| 37 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 42 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 50 | 159 | `static Map<String, Double> snapshot(Game g)` | Everything a previous city could possibly leave behind. |
| 210 | 181 | `public static void main(String[] args) throws Exception` |  |

### 6-11. FOUNDING A CITY (0.7.10) (lines 392-858)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 387 | `static void founding(GameFiles files) throws Exception` |  |
| 808 | 4 | `static double cost(Founding.Buys buys, String name)` | A first work's invoice, by name. |
| 814 | 8 | `static boolean isDefault(Game g)` | Everything a founding on the defaults is, and nothing a previous city was. |
| 824 | 5 | `static boolean audited(Game g)` | The month's money audit closed, and nothing moved after it struck - LongPlaytest's two tests. |
| 830 | 6 | `static void close(String label, double actual, double expected, double tol)` |  |
| 841 | 4 | `static<T> T quietly(java.util.function.Supplier<T> work)` | Runs a piece of the city with the game's own narration off. |
| 846 | 4 | `static void quietly(Runnable work)` |  |
| 851 | 7 | `static void cleanUp(Path root)` |  |


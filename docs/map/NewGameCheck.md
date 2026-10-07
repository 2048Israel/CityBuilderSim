# NewGameCheck.java - 1,116 lines · 14 methods · 2 constants · harnesses

`ham/citybuildersim/NewGameCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> 
> AND WHAT A PLAYER'S CITY FOUNDS WITH (0.7.13), section 12: the dial on the
> autopilot and the treasury rolling what falls due - by both of the
> founding screen's doors - while a city built bare keeps the hand on the
> dial and rolls nothing, and a save keeps whatever it saved.
> 
> AND THE GROUND IT STANDS ON (0.7.56), section 13: the world's seed is part
> of the founding record - the defaults' and every preset's the default
> world, a chosen one carried into the city and its World, kept by a save,
> derived the same way every time for a save from before it, and left behind
> by Start New Game; the founding screen's dice and typed seed.

**Uses:** [Game](Game.md) (137), [Founding](Founding.md) (119), [Currency](Currency.md) (40), [WorldEconomy](WorldEconomy.md) (25), [Rollover](Rollover.md) (6), [ForeignAccounts](ForeignAccounts.md) (5), [GameFiles](GameFiles.md) (5), [World](World.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [LongTermBond](LongTermBond.md) (3), [DebtQuote](DebtQuote.md) (3), [Sector](Sector.md) (2), [Good](Good.md) (2), [EconomyManager](EconomyManager.md) (1), [PopulationManager](PopulationManager.md) (1), [Retail](Retail.md) (1), [RealEstate](RealEstate.md) (1), [ServicesManager](ServicesManager.md) (1), [Construction](Construction.md) (1), [BuildingManager](BuildingManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Statement](Statement.md) (1), [GoodsMarket](GoodsMarket.md) (1), [CityLand](CityLand.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Debt](Debt.md) (1), [MoneyAudit](MoneyAudit.md) (1)

**Used by (1):** [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 242 | · 1. what a city that never existed looks like |
| 259 | · 2. live in one, hard |
| 332 | · 3. start a new one |
| 353 | · 4. and it is actually playable |
| 379 | · 5. a new game after a LOAD, too |
| 424 | · 6-11. FOUNDING A CITY (0.7.10) |
| 427 | · 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) |
| 430 | · 13. THE GROUND IT STANDS ON (0.7.56) |
| 439 | 6-11. FOUNDING A CITY (0.7.10) |
| 474 | · · 6 |
| 617 | · · 7 |
| 670 | · · 8 |
| 725 | · · 9 |
| 742 | · · 10 |
| 768 | · · 11 |
| 920 | 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) |
| 988 | 13. THE GROUND IT STANDS ON (0.7.56, batch J1a) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1095 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 1096 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 1073 | **type** `public class NewGameCheck` | Does "Start New Game" actually start a new game? |
| 48 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 53 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 61 | 175 | `static Map<String, Double> snapshot(Game g)` | Everything a previous city could possibly leave behind. |
| 237 | 201 | `public static void main(String[] args) throws Exception` |  |

### 6-11. FOUNDING A CITY (0.7.10) (lines 439-919)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 424 | `static void founding(GameFiles files) throws Exception` |  |
| 898 | 4 | `static double cost(Founding.Buys buys, String name)` | A first work's invoice, by name. |
| 904 | 8 | `static boolean isDefault(Game g)` | Everything a founding on the defaults is, and nothing a previous city was. |
| 914 | 5 | `static boolean audited(Game g)` | The month's money audit closed, and nothing moved after it struck - LongPlaytest's two tests. |

### 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) (lines 920-987)

| line | len | member | says |
|---:|---:|---|---|
| 937 | 50 | `static void theDialAndTheRollover(GameFiles files) throws Exception` |  |

### 13. THE GROUND IT STANDS ON (0.7.56, batch J1a) (lines 988-1116)

| line | len | member | says |
|---:|---:|---|---|
| 1005 | 82 | `static void theWorldsSeed(GameFiles files) throws Exception` |  |
| 1088 | 6 | `static void close(String label, double actual, double expected, double tol)` |  |
| 1099 | 4 | `static<T> T quietly(java.util.function.Supplier<T> work)` | Runs a piece of the city with the game's own narration off. |
| 1104 | 4 | `static void quietly(Runnable work)` |  |
| 1109 | 7 | `static void cleanUp(Path root)` |  |


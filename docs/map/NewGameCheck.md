# NewGameCheck.java - 1,225 lines · 15 methods · 3 constants · harnesses

`ham/citybuildersim/NewGameCheck.java` - generated 2026-10-14 by CodeMap; line numbers are as of that run.

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
> 
> AND THE REAL EARTH (0.7.121), section 14: a founding's world may be the
> Earth round a spot - its kind, latitude, longitude and data on the record,
> carried into the city's World and land, kept by a save; a spot the founding
> card refuses is a problem the game will not found; Start New Game after an
> Earth city founds on the default generated world.

**Uses:** [Game](Game.md) (143), [Founding](Founding.md) (134), [Currency](Currency.md) (40), [WorldEconomy](WorldEconomy.md) (27), [World](World.md) (13), [GameFiles](GameFiles.md) (6), [Rollover](Rollover.md) (6), [ForeignAccounts](ForeignAccounts.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [LongTermBond](LongTermBond.md) (3), [DebtQuote](DebtQuote.md) (3), [Sector](Sector.md) (2), [Good](Good.md) (2), [EarthData](EarthData.md) (2), [EarthCheck](EarthCheck.md) (2), [EconomyManager](EconomyManager.md) (1), [PopulationManager](PopulationManager.md) (1), [Retail](Retail.md) (1), [RealEstate](RealEstate.md) (1), [ServicesManager](ServicesManager.md) (1), [Construction](Construction.md) (1), [BuildingManager](BuildingManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Statement](Statement.md) (1), [GoodsMarket](GoodsMarket.md) (1), [CityLand](CityLand.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Debt](Debt.md) (1), [MoneyAudit](MoneyAudit.md) (1), [EarthTerrain](EarthTerrain.md) (1)... and 1 more

**Used by (1):** [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 258 | · 1. what a city that never existed looks like |
| 275 | · 2. live in one, hard |
| 359 | · 3. start a new one |
| 380 | · 4. and it is actually playable |
| 406 | · 5. a new game after a LOAD, too |
| 451 | · 6-11. FOUNDING A CITY (0.7.10) |
| 454 | · 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) |
| 457 | · 13. THE GROUND IT STANDS ON (0.7.56) |
| 460 | · 14. THE REAL EARTH (0.7.121) |
| 469 | 6-11. FOUNDING A CITY (0.7.10) |
| 504 | · · 6 |
| 647 | · · 7 |
| 700 | · · 8 |
| 755 | · · 9 |
| 772 | · · 10 |
| 798 | · · 11 |
| 950 | 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) |
| 1018 | 13. THE GROUND IT STANDS ON (0.7.56, batch J1a) |
| 1118 | 14. THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 251 | `NewGameCheck.USED_HOUSES` | `120` | The Houses the used city is founded with (0.7.113; 200 until then): fewer than its people need, so its landlords build and borrow. |
| 1204 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 1205 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1176 | **type** `public class NewGameCheck` | Does "Start New Game" actually start a new game? |
| 54 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 67 | 182 | `static Map<String, Double> snapshot(Game g)` | Everything a previous city could possibly leave behind. |
| 253 | 215 | `public static void main(String[] args) throws Exception` |  |

### 6-11. FOUNDING A CITY (0.7.10) (lines 469-949)

| line | len | member | says |
|---:|---:|---|---|
| 502 | 424 | `static void founding(GameFiles files) throws Exception` |  |
| 928 | 4 | `static double cost(Founding.Buys buys, String name)` | A first work's invoice, by name. |
| 934 | 8 | `static boolean isDefault(Game g)` | Everything a founding on the defaults is, and nothing a previous city was. |
| 944 | 5 | `static boolean audited(Game g)` | The month's money audit closed, and nothing moved after it struck - LongPlaytest's two tests. |

### 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) (lines 950-1017)

| line | len | member | says |
|---:|---:|---|---|
| 967 | 50 | `static void theDialAndTheRollover(GameFiles files) throws Exception` |  |

### 13. THE GROUND IT STANDS ON (0.7.56, batch J1a) (lines 1018-1117)

| line | len | member | says |
|---:|---:|---|---|
| 1035 | 82 | `static void theWorldsSeed(GameFiles files) throws Exception` |  |

### 14. THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2 (lines 1118-1225)

| line | len | member | says |
|---:|---:|---|---|
| 1137 | 59 | `static void theRealEarth(GameFiles files) throws Exception` |  |
| 1197 | 6 | `static void close(String label, double actual, double expected, double tol)` |  |
| 1208 | 4 | `static<T> T quietly(java.util.function.Supplier<T> work)` | Runs a piece of the city with the game's own narration off. |
| 1213 | 4 | `static void quietly(Runnable work)` |  |
| 1218 | 7 | `static void cleanUp(Path root)` |  |


# NewGameCheck.java - 1,383 lines · 17 methods · 5 constants · harnesses

`ham/citybuildersim/NewGameCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> 
> AND A NEW CITY AFTER A REFORMED ONE (0.8.10, batch FX4), section 15: New
> Game in the window of a city whose currency was reformed founds at unit 1,
> and over two years reads the parity and the rate a city never reformed
> reads.
> 
> AND THE NAME ON THE FOUND-A-CITY PAGE (FN1, 2026-10-30), section 16: the
> name field opens blank, a place searched fills it unless the player has
> typed a name, and a blank name is the model's refusal.

**Uses:** [Game](Game.md) (150), [Founding](Founding.md) (143), [Currency](Currency.md) (41), [WorldEconomy](WorldEconomy.md) (27), [World](World.md) (13), [FoundingName](FoundingName.md) (10), [GameFiles](GameFiles.md) (7), [Rollover](Rollover.md) (6), [EarthData](EarthData.md) (6), [ForeignAccounts](ForeignAccounts.md) (5), [DebtQuote](DebtQuote.md) (4), [EarthCheck](EarthCheck.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [LongTermBond](LongTermBond.md) (3), [EarthSite](EarthSite.md) (3), [Sector](Sector.md) (2), [Good](Good.md) (2), [Denomination](Denomination.md) (2), [EconomyManager](EconomyManager.md) (1), [PopulationManager](PopulationManager.md) (1), [Retail](Retail.md) (1), [RealEstate](RealEstate.md) (1), [ServicesManager](ServicesManager.md) (1), [Construction](Construction.md) (1), [BuildingManager](BuildingManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Statement](Statement.md) (1), [GoodsMarket](GoodsMarket.md) (1), [CityLand](CityLand.md) (1), [Landmasses](Landmasses.md) (1)... and 6 more

**Used by (1):** [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 274 | · 1. what a city that never existed looks like |
| 291 | · 2. live in one, hard |
| 375 | · 3. start a new one |
| 396 | · 4. and it is actually playable |
| 422 | · 5. a new game after a LOAD, too |
| 467 | · 6-11. FOUNDING A CITY (0.7.10) |
| 470 | · 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) |
| 473 | · 13. THE GROUND IT STANDS ON (0.7.56) |
| 476 | · 14. THE REAL EARTH (0.7.121) |
| 479 | · 15. A NEW CITY AFTER A REFORMED ONE (FX4) |
| 482 | · 16. THE NAME ON THE FOUND-A-CITY PAGE (FN1) |
| 491 | 6-11. FOUNDING A CITY (0.7.10) |
| 526 | · · 6 |
| 669 | · · 7 |
| 722 | · · 8 |
| 777 | · · 9 |
| 794 | · · 10 |
| 820 | · · 11 |
| 992 | 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) |
| 1060 | 13. THE GROUND IT STANDS ON (0.7.56, batch J1a) |
| 1160 | 14. THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2 |
| 1260 | 15. A NEW CITY AFTER A REFORMED ONE (FX4, 2026-10-19) |
| 1298 | 16. THE NAME ON THE FOUND-A-CITY PAGE (FN1, 2026-10-30). Jerus: "remove |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 267 | `NewGameCheck.USED_HOUSES` | `120` | The Houses the used city is founded with (0.7.113; 200 until then): fewer than its people need, so its landlords build and borrow. |
| 1246 | `NewGameCheck.REAL_OUT` | `System.out` |  |
| 1247 | `NewGameCheck.QUIET` | `new java.io.PrintStream(java.io.OutputStream.nullOutputStream())` |  |
| 1273 | `NewGameCheck.AFTER_REFORM_MONTHS` | `24` |  |
| 1275 | `NewGameCheck.AFTER_REFORM_BAND` | `0.05` | How far apart, as a ratio, the two cities' parity and rate may sit: the runaway put them a hundredfold apart. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 1325 | **type** `public class NewGameCheck` | Does "Start New Game" actually start a new game? |
| 63 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 68 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 76 | 189 | `static Map<String, Double> snapshot(Game g)` | Everything a previous city could possibly leave behind. |
| 269 | 221 | `public static void main(String[] args) throws Exception` |  |

### 6-11. FOUNDING A CITY (0.7.10) (lines 491-991)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 444 | `static void founding(GameFiles files) throws Exception` |  |
| 970 | 4 | `static double cost(Founding.Buys buys, String name)` | A first work's invoice, by name. |
| 976 | 8 | `static boolean isDefault(Game g)` | Everything a founding on the defaults is, and nothing a previous city was. |
| 986 | 5 | `static boolean audited(Game g)` | The month's money audit closed, and nothing moved after it struck - LongPlaytest's two tests. |

### 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) (lines 992-1059)

| line | len | member | says |
|---:|---:|---|---|
| 1009 | 50 | `static void theDialAndTheRollover(GameFiles files) throws Exception` |  |

### 13. THE GROUND IT STANDS ON (0.7.56, batch J1a) (lines 1060-1159)

| line | len | member | says |
|---:|---:|---|---|
| 1077 | 82 | `static void theWorldsSeed(GameFiles files) throws Exception` |  |

### 14. THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 2 (lines 1160-1259)

| line | len | member | says |
|---:|---:|---|---|
| 1179 | 59 | `static void theRealEarth(GameFiles files) throws Exception` |  |
| 1239 | 6 | `static void close(String label, double actual, double expected, double tol)` |  |
| 1250 | 4 | `static<T> T quietly(java.util.function.Supplier<T> work)` | Runs a piece of the city with the game's own narration off. |
| 1255 | 4 | `static void quietly(Runnable work)` |  |

### 15. A NEW CITY AFTER A REFORMED ONE (FX4, 2026-10-19) (lines 1260-1297)

| line | len | member | says |
|---:|---:|---|---|
| 1277 | 20 | `static void aNewCityAfterAReform(GameFiles files)` |  |

### 16. THE NAME ON THE FOUND-A-CITY PAGE (FN1, 2026-10-30). Jerus: "remove (lines 1298-1383)

| line | len | member | says |
|---:|---:|---|---|
| 1306 | 69 | `static void theNameOnThePage() throws Exception` |  |
| 1376 | 7 | `static void cleanUp(Path root)` |  |


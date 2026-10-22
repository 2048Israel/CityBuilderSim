# SaveFileCheck.java - 3,036 lines · 14 methods · 1 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Verifies where saves go and how they are written.
> 
> This is the only part of the game where a bug destroys something the player
> cannot get back. Everything else can be re-simulated; a save written over
> badly is a city that no longer exists. So the tests here are deliberately
> mean: they fill the target with junk, point the writer at a path it cannot
> write to, and check that in every case the file that was already on disk is
> still exactly what it was.
> 
> Path resolution is tested through the pure resolveDirectory() rather than the
> real environment, because a chooser that reads System.getenv can only be
> tested on the machine it is running on - which is precisely the machine where
> a mistake in the other two branches would never show up.

**Uses:** [Game](Game.md) (101), [GameFiles](GameFiles.md) (54), [Sectors](Sectors.md) (26), [Founding](Founding.md) (25), [GameVersion](GameVersion.md) (24), [DataSave](DataSave.md) (21), [Bank](Bank.md) (13), [OldMetal](OldMetal.md) (12), [CareType](CareType.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [Oil](Oil.md) (10), [OrderBook](OrderBook.md) (10), [TreasuryFund](TreasuryFund.md) (10), [Equity](Equity.md) (9), [Sector](Sector.md) (8), [Household](Household.md) (8), [MoneyAudit](MoneyAudit.md) (8), [World](World.md) (8), [Ports](Ports.md) (7), [Trade](Trade.md) (6), [Migration](Migration.md) (6), [LandManager](LandManager.md) (5), [Resource](Resource.md) (5), [BuildingManager](BuildingManager.md) (5), [EconomyManager](EconomyManager.md) (5), [FundLedger](FundLedger.md) (5), [SaveHeader](SaveHeader.md) (4), [HistorySave](HistorySave.md) (4), [LongPlaytest](LongPlaytest.md) (4), [WageBand](WageBand.md) (4)... and 40 more

**Used by (8):** [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConversionCheck](ConversionCheck.md), [LandCheck](LandCheck.md), [OilCheck](OilCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | · 1. where it goes |
| 112 | · 2. writing safely |
| 145 | · 3. a write that cannot possibly work |
| 172 | · 4. the old folder |
| 205 | · 5. it never overwrites a newer save |
| 240 | · 6. a real round trip |
| 408 | · 7. construction survives a save |
| 505 | · 7b. the warning survives a save |
| 583 | · 8. a save with nothing else built |
| 607 | · 8b. a stack emptied keeps its place (0.7.103) |
| 637 | · 9. the headline income does not move |
| 677 | · 10. the whole figure, to the cent |
| 856 | · 11. a city that is still MOVING |
| 960 | · 12. a city that OWES money |
| 1048 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1112 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 2034 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2357 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 2652 | · the oldest format this build loads (0.7.108) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 2978 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 3011 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 30 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 35 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 47 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 58 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 62 | 2701 | `public static void main(String[] args) throws Exception` |  |
| 2764 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 2781 | 36 | `static void everyIdCrossesASave(Path root)` | EVERY ID CROSSES A SAVE, THE HIGHEST PAST A GAP TOO (FR3, 2026-10-19). |
| 2839 | 125 | `static void onEarth(Path root) throws IOException` | A CITY ON THE REAL EARTH (0.7.121, batch E4; spec-earth 6). |
| 2966 | 10 | `static java.util.List<String> jsonDiff(com.google.gson.JsonObject a, com.google.gson.JsonObject b, java.util.Set<String> except)` | The keys two saves differ on, by name and value, but those left out. |
| 2981 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 2986 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 3006 | 10 | `static boolean refusedAtTheFloor(GameFiles files, int slot, Game loaded, byte[] before)` | Whether a load of `slot` from `files` into `loaded` was refused at the floor (0.7.120, batch I3): the save's format below GameVersion.OLDEST_LOADABLE_FORMAT, the game told in P7's words naming the build that saved it ... |
| 3018 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 3029 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |


# SaveFileCheck.java - 3,414 lines · 15 methods · 1 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (120), [GameFiles](GameFiles.md) (61), [CityLand](CityLand.md) (40), [Founding](Founding.md) (32), [GameVersion](GameVersion.md) (30), [DataSave](DataSave.md) (29), [Sectors](Sectors.md) (26), [Nations](Nations.md) (13), [Bank](Bank.md) (13), [World](World.md) (13), [OldMetal](OldMetal.md) (12), [CareType](CareType.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [Ports](Ports.md) (11), [Oil](Oil.md) (10), [OrderBook](OrderBook.md) (10), [TreasuryFund](TreasuryFund.md) (10), [Equity](Equity.md) (9), [Sector](Sector.md) (8), [Household](Household.md) (8), [MoneyAudit](MoneyAudit.md) (8), [EarthSite](EarthSite.md) (8), [LandParcel](LandParcel.md) (7), [LandmassCheck](LandmassCheck.md) (7), [LandManager](LandManager.md) (6), [BuildingManager](BuildingManager.md) (6), [EconomyManager](EconomyManager.md) (6), [Trade](Trade.md) (6), [Migration](Migration.md) (6), [Resource](Resource.md) (5)... and 51 more

**Used by (10):** [BridgeCheck](BridgeCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConversionCheck](ConversionCheck.md), [LandCheck](LandCheck.md), [LandmassCheck](LandmassCheck.md), [OilCheck](OilCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | · 1. where it goes |
| 112 | · 2. writing safely |
| 145 | · 3. a write that cannot possibly work |
| 172 | · 4. the old folder |
| 205 | · 5. it never overwrites a newer save |
| 240 | · 6. a real round trip |
| 433 | · 7. construction survives a save |
| 530 | · 7b. the warning survives a save |
| 608 | · 8. a save with nothing else built |
| 632 | · 8b. a stack emptied keeps its place (0.7.103) |
| 662 | · 9. the headline income does not move |
| 702 | · 10. the whole figure, to the cent |
| 881 | · 11. a city that is still MOVING |
| 985 | · 12. a city that OWES money |
| 1090 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1154 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 2102 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2425 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 2720 | · the oldest format this build loads (0.7.108) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 3356 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 3389 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 30 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 35 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 47 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 58 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 62 | 2771 | `public static void main(String[] args) throws Exception` |  |
| 2834 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 2851 | 41 | `static void everyIdCrossesASave(Path root)` | EVERY ID CROSSES A SAVE, THE HIGHEST PAST A GAP TOO (FR3, 2026-10-19). |
| 2920 | 364 | `static void onEarth(Path root) throws IOException` | A CITY ON THE REAL EARTH (0.7.121, batch E4; spec-earth 6). |
| 3296 | 46 | `static void refusedIntoARunningCity(GameFiles files, Game running, String olderBuild, com.google.gson.JsonObject newerSave) thr...` | A REFUSED LOAD LEAVES THE CITY IN PLAY ALONE (FW1, 0.8.37). |
| 3344 | 10 | `static java.util.List<String> jsonDiff(com.google.gson.JsonObject a, com.google.gson.JsonObject b, java.util.Set<String> except)` | The keys two saves differ on, by name and value, but those left out. |
| 3359 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 3364 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 3384 | 10 | `static boolean refusedAtTheFloor(GameFiles files, int slot, Game loaded, byte[] before)` | Whether a load of `slot` from `files` into `loaded` was refused at the floor (0.7.120, batch I3): the save's format below GameVersion.OLDEST_LOADABLE_FORMAT, the game told in P7's words naming the build that saved it ... |
| 3396 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 3407 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |


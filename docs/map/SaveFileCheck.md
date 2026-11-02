# SaveFileCheck.java - 3,964 lines · 18 methods · 2 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-11-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (141), [GameFiles](GameFiles.md) (66), [CityLand](CityLand.md) (40), [Founding](Founding.md) (36), [GameVersion](GameVersion.md) (36), [DataSave](DataSave.md) (33), [LandParcel](LandParcel.md) (26), [Sectors](Sectors.md) (26), [Nations](Nations.md) (17), [OrderBook](OrderBook.md) (14), [Equity](Equity.md) (13), [Bank](Bank.md) (13), [World](World.md) (13), [LandManager](LandManager.md) (12), [OldMetal](OldMetal.md) (12), [CareType](CareType.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [Good](Good.md) (11), [PriceLevel](PriceLevel.md) (11), [Ports](Ports.md) (11), [Exchange](Exchange.md) (11), [Oil](Oil.md) (10), [Household](Household.md) (10), [TreasuryFund](TreasuryFund.md) (10), [EarthSite](EarthSite.md) (10), [HistorySave](HistorySave.md) (9), [Sector](Sector.md) (8), [MoneyAudit](MoneyAudit.md) (8), [Trade](Trade.md) (7), [ForeignAccounts](ForeignAccounts.md) (7)... and 64 more

**Used by (10):** [BridgeCheck](BridgeCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConversionCheck](ConversionCheck.md), [LandCheck](LandCheck.md), [LandmassCheck](LandmassCheck.md), [OilCheck](OilCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 67 | · 1. where it goes |
| 115 | · 2. writing safely |
| 148 | · 3. a write that cannot possibly work |
| 175 | · 4. the old folder |
| 208 | · 5. it never overwrites a newer save |
| 243 | · 6. a real round trip |
| 484 | · 7. construction survives a save |
| 585 | · 7b. the warning survives a save |
| 663 | · 8. a save with nothing else built |
| 687 | · 8b. a stack emptied keeps its place (0.7.103) |
| 717 | · 9. the headline income does not move |
| 757 | · 10. the whole figure, to the cent |
| 936 | · 11. a city that is still MOVING |
| 1040 | · 12. a city that OWES money |
| 1145 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1231 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 2265 | · 13b. a bond in the saved history (HD1, 2026-10-29) |
| 2304 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2627 | · a recording's switch and id cross a save (TR1, 2026-10-31) |
| 2665 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 2972 | · the oldest format this build loads (0.7.108) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `SaveFileCheck.OTHER_PATH` | `Nations.DEFAULT_PATH = = Nations.Path.RANDOM ? Nations.Path.HISTORICAL : Nati...` | The path a fixture founds on to be off the default, whichever Nations.DEFAULT_PATH is (WN3: the default became Random, Jerus 2026-10-29). |
| 3632 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 3939 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 50 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 61 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 65 | 3021 | `public static void main(String[] args) throws Exception` |  |
| 3087 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 3104 | 41 | `static void everyIdCrossesASave(Path root)` | EVERY ID CROSSES A SAVE, THE HIGHEST PAST A GAP TOO (FR3, 2026-10-19). |
| 3173 | 387 | `static void onEarth(Path root) throws IOException` | A CITY ON THE REAL EARTH (0.7.121, batch E4; spec-earth 6). |
| 3572 | 46 | `static void refusedIntoARunningCity(GameFiles files, Game running, String olderBuild, com.google.gson.JsonObject newerSave) thr...` | A REFUSED LOAD LEAVES THE CITY IN PLAY ALONE (FW1, 0.8.37). |
| 3620 | 10 | `static java.util.List<String> jsonDiff(com.google.gson.JsonObject a, com.google.gson.JsonObject b, java.util.Set<String> except)` | The keys two saves differ on, by name and value, but those left out. |
| 3635 | 8 | `static boolean restoredWithoutSellers(LandMarket from)` | Whether a market's offers, restored with no sellers (a save from before WN5, or without its key), come back as many offers all the host's whole. |
| 3645 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 3650 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 3670 | 10 | `static boolean refusedAtTheFloor(GameFiles files, int slot, Game loaded, byte[] before)` | Whether a load of `slot` from `files` into `loaded` was refused at the floor (0.7.120, batch I3): the save's format below GameVersion.OLDEST_LOADABLE_FORMAT, the game told in P7's words naming the build that saved it ... |
| 3682 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 3699 | 248 | `static void worldMarketCrossesASave(Path root) throws IOException` | THE WORLD MARKET CROSSES A SAVE (WN3, format 48; Game, THE WORLD MARKET). |
| 3949 | 6 | `static String printedMonths(Game city, int months)` | The narration of some months of a city, as text. |
| 3957 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |


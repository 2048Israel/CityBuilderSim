# SaveFileCheck.java - 4,324 lines · 21 methods · 2 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (163), [GameFiles](GameFiles.md) (70), [CityLand](CityLand.md) (40), [Founding](Founding.md) (38), [GameVersion](GameVersion.md) (36), [DataSave](DataSave.md) (34), [Sectors](Sectors.md) (28), [LandParcel](LandParcel.md) (26), [ForeignAccounts](ForeignAccounts.md) (23), [Nations](Nations.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (14), [Equity](Equity.md) (14), [OrderBook](OrderBook.md) (14), [LandManager](LandManager.md) (13), [Bank](Bank.md) (13), [World](World.md) (13), [OldMetal](OldMetal.md) (12), [Household](Household.md) (12), [CareType](CareType.md) (12), [Good](Good.md) (11), [PriceLevel](PriceLevel.md) (11), [Ports](Ports.md) (11), [Oil](Oil.md) (10), [Exchange](Exchange.md) (10), [TreasuryFund](TreasuryFund.md) (10), [EarthSite](EarthSite.md) (10), [HistorySave](HistorySave.md) (9), [FundLedger](FundLedger.md) (9), [Migration](Migration.md) (9), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (9)... and 69 more

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
| 494 | · 7. construction survives a save |
| 610 | · 7b. the warning survives a save |
| 694 | · 8. a save with nothing else built |
| 718 | · 8b. a stack emptied keeps its place (0.7.103) |
| 748 | · 9. the headline income does not move |
| 788 | · 10. the whole figure, to the cent |
| 967 | · 11. a city that is still MOVING |
| 1071 | · 12. a city that OWES money |
| 1176 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1275 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 2420 | · 13b. a bond in the saved history (HD1, 2026-10-29) |
| 2459 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2789 | · a recording's switch and id cross a save (TR1, 2026-10-31) |
| 2827 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 3141 | · the oldest format this build loads (0.7.108) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `SaveFileCheck.OTHER_PATH` | `Nations.DEFAULT_PATH = = Nations.Path.RANDOM ? Nations.Path.HISTORICAL : Nati...` | The path a fixture founds on to be off the default, whichever Nations.DEFAULT_PATH is (WN3: the default became Random, Jerus 2026-10-29). |
| 3959 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 4299 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 50 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 61 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 65 | 3193 | `public static void main(String[] args) throws Exception` |  |
| 3259 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 3267 | 59 | `static void allocatedLandCrossesASave() throws IOException` | AW2: the ordinary loader keeps the live ledger even when raw fixture stacks exceed its ground. |
| 3328 | 31 | `static void landPlotsCrossASave()` | AW2: catalogue edits change the occupied ground, while the fixture's unallocated gap stays the same. |
| 3361 | 59 | `static void constructionContinuesAfterALoad(Path root)` | AW2: cause automatic land buying and private orders, then play live and ordinary-loaded twins together. |
| 3431 | 41 | `static void everyIdCrossesASave(Path root)` | EVERY ID CROSSES A SAVE, THE HIGHEST PAST A GAP TOO (FR3, 2026-10-19). |
| 3500 | 387 | `static void onEarth(Path root) throws IOException` | A CITY ON THE REAL EARTH (0.7.121, batch E4; spec-earth 6). |
| 3899 | 46 | `static void refusedIntoARunningCity(GameFiles files, Game running, String olderBuild, com.google.gson.JsonObject newerSave) thr...` | A REFUSED LOAD LEAVES THE CITY IN PLAY ALONE (FW1, 0.8.37). |
| 3947 | 10 | `static java.util.List<String> jsonDiff(com.google.gson.JsonObject a, com.google.gson.JsonObject b, java.util.Set<String> except)` | The keys two saves differ on, by name and value, but those left out. |
| 3962 | 8 | `static boolean restoredWithoutSellers(LandMarket from)` | Whether a market's offers, restored with no sellers (a save from before WN5, or without its key), come back as many offers all the host's whole. |
| 3972 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 3977 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 3997 | 10 | `static boolean refusedAtTheFloor(GameFiles files, int slot, Game loaded, byte[] before)` | Whether a load of `slot` from `files` into `loaded` was refused at the floor (0.7.120, batch I3): the save's format below GameVersion.OLDEST_LOADABLE_FORMAT, the game told in P7's words naming the build that saved it ... |
| 4009 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 4026 | 281 | `static void worldMarketCrossesASave(Path root) throws IOException` | THE WORLD MARKET CROSSES A SAVE (WN3, format 48; Game, THE WORLD MARKET). |
| 4309 | 6 | `static String printedMonths(Game city, int months)` | The narration of some months of a city, as text. |
| 4317 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |


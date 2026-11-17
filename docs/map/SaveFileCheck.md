# SaveFileCheck.java - 4,816 lines · 33 methods · 2 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (192), [GameFiles](GameFiles.md) (81), [Founding](Founding.md) (41), [CityLand](CityLand.md) (40), [GameVersion](GameVersion.md) (36), [DataSave](DataSave.md) (34), [Sectors](Sectors.md) (30), [LandParcel](LandParcel.md) (26), [ForeignAccounts](ForeignAccounts.md) (24), [Migration](Migration.md) (22), [Nations](Nations.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (17), [LandManager](LandManager.md) (14), [Equity](Equity.md) (14), [OrderBook](OrderBook.md) (14), [World](World.md) (14), [Bank](Bank.md) (13), [OldMetal](OldMetal.md) (12), [Household](Household.md) (12), [CareType](CareType.md) (12), [Good](Good.md) (11), [PriceLevel](PriceLevel.md) (11), [Ports](Ports.md) (11), [TreasuryFund](TreasuryFund.md) (11), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (10), [Oil](Oil.md) (10), [Exchange](Exchange.md) (10), [EarthSite](EarthSite.md) (10), [HistorySave](HistorySave.md) (9), [FundLedger](FundLedger.md) (9)... and 76 more

**Used by (9):** [BridgeCheck](BridgeCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [LandCheck](LandCheck.md), [LandmassCheck](LandmassCheck.md), [OilCheck](OilCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 79 | · 1. where it goes |
| 127 | · 2. writing safely |
| 160 | · 3. a write that cannot possibly work |
| 187 | · 4. the old folder |
| 220 | · 5. it never overwrites a newer save |
| 255 | · 6. a real round trip |
| 506 | · 7. construction survives a save |
| 618 | · 7b. the warning survives a save |
| 702 | · 8. a save with nothing else built |
| 726 | · 8b. a stack emptied keeps its place (0.7.103) |
| 756 | · 9. the headline income does not move |
| 796 | · 10. the whole figure, to the cent |
| 975 | · 11. a city that is still MOVING |
| 1081 | · 12. a city that OWES money |
| 1186 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1285 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 2421 | · 13b. a bond in the saved history (HD1, 2026-10-29) |
| 2460 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2792 | · a recording's switch and id cross a save (TR1, 2026-10-31) |
| 2830 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 3148 | · the oldest format this build loads (0.7.108) |
| 4639 | · 4. A CITY IS THE SAME CITY AFTER YOU LOAD IT |
| 4708 | · 5. WHAT A RELOAD MUST NOT FORGET |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `SaveFileCheck.OTHER_PATH` | `Nations.DEFAULT_PATH = = Nations.Path.RANDOM ? Nations.Path.HISTORICAL : Nati...` | The path a fixture founds on to be off the default, whichever Nations.DEFAULT_PATH is (WN3: the default became Random, Jerus 2026-10-29). |
| 4106 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `static int fails` |  |
| 4570 | `static int fails` |  |
| 4571 | `static PrintStream out` |  |
| 4572 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 4789 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 35 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 40 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 52 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 63 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 67 | 3203 | `public static void main(String[] args) throws Exception` |  |
| 3272 | 9 | `static void bits(String label, double actual, double expected)` | Two readings that must be the same double, to the bit. |
| 3295 | 46 | `static void brakeThresholdCrossesASave(Path root) throws IOException` | THE BRAKE'S THRESHOLD THROUGH A SAVE (WG2-R). |
| 3343 | 8 | `private static void sameBrake(String at, Game twin, Game city, int monthsSlot)` | The reading of the brake a twin must share with its original: whether it applies, the jobs' pull, the brake, the arrivals and the months recorded. |
| 3353 | 34 | `static void acuteSicknessCrossesASave() throws IOException` | R0h: nonzero acute shares cross JSON and the actual load, and a missing old field clears them. |
| 3388 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 3396 | 59 | `static void allocatedLandCrossesASave() throws IOException` | AW2: the ordinary loader keeps the live ledger even when raw fixture stacks exceed its ground. |
| 3457 | 31 | `static void landPlotsCrossASave()` | AW2: catalogue edits change the occupied ground, while the fixture's unallocated gap stays the same. |
| 3490 | 72 | `static void constructionContinuesAfterALoad(Path root)` | AW2: cause automatic land buying and private orders, then play live and ordinary-loaded twins together. |
| 3563 | 4 | `private static long standingAndOnSite(Game game, BuildingsTemplate template)` |  |
| 3578 | 41 | `static void everyIdCrossesASave(Path root)` | EVERY ID CROSSES A SAVE, THE HIGHEST PAST A GAP TOO (FR3, 2026-10-19). |
| 3647 | 387 | `static void onEarth(Path root) throws IOException` | A CITY ON THE REAL EARTH (0.7.121, batch E4; spec-earth 6). |
| 4046 | 46 | `static void refusedIntoARunningCity(GameFiles files, Game running, String olderBuild, com.google.gson.JsonObject newerSave) thr...` | A REFUSED LOAD LEAVES THE CITY IN PLAY ALONE (FW1, 0.8.37). |
| 4094 | 10 | `static java.util.List<String> jsonDiff(com.google.gson.JsonObject a, com.google.gson.JsonObject b, java.util.Set<String> except)` | The keys two saves differ on, by name and value, but those left out. |
| 4109 | 8 | `static boolean restoredWithoutSellers(LandMarket from)` | Whether a market's offers, restored with no sellers (a save from before WN5, or without its key), come back as many offers all the host's whole. |
| 4119 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 4124 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 4144 | 10 | `static boolean refusedAtTheFloor(GameFiles files, int slot, Game loaded, byte[] before)` | Whether a load of `slot` from `files` into `loaded` was refused at the floor (0.7.120, batch I3): the save's format below GameVersion.OLDEST_LOADABLE_FORMAT, the game told in P7's words naming the build that saved it ... |
| 4156 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 4173 | 281 | `static void worldMarketCrossesASave(Path root) throws IOException` | THE WORLD MARKET CROSSES A SAVE (WN3, format 48; Game, THE WORLD MARKET). |
| 4456 | 6 | `static String printedMonths(Game city, int months)` | The narration of some months of a city, as text. |
| 4464 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |
| 4473 | 46 | `static void landLoadFallbacks() throws Exception` | HA1: current-format land is repaired, or refused before a live city is rebuilt. |
| 4568 | 248 | **type** `static final class LoadConservation` | Nothing is created and nothing is destroyed. |
| 4577 | 6 | `static void check(String label, double actual, double expected, double tol)` _(in SaveFileCheck.LoadConservation)_ |  |
| 4584 | 4 | `static void assertTrue(String label, boolean ok)` _(in SaveFileCheck.LoadConservation)_ |  |
| 4590 | 43 | `static Game city(Path root, String name, int months) throws Exception` _(in SaveFileCheck.LoadConservation)_ | A city with something of everything in it, so every law has something to test. |
| 4634 | 160 | `static void run(String[] args) throws Exception` _(in SaveFileCheck.LoadConservation)_ |  |
| 4796 | 11 | `static double[] snapshot(Game g)` _(in SaveFileCheck.LoadConservation)_ | The figures a load must not move. |
| 4808 | 7 | `static void cleanUp(Path root)` _(in SaveFileCheck.LoadConservation)_ |  |


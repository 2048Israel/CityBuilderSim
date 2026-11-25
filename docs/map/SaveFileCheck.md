# SaveFileCheck.java - 5,254 lines · 46 methods · 2 constants · harnesses

`ham/citybuildersim/SaveFileCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (204), [GameFiles](GameFiles.md) (83), [Founding](Founding.md) (41), [ForeignAccounts](ForeignAccounts.md) (41), [CityLand](CityLand.md) (40), [GameVersion](GameVersion.md) (38), [DataSave](DataSave.md) (34), [Sectors](Sectors.md) (30), [LandParcel](LandParcel.md) (26), [OrderBook](OrderBook.md) (23), [Migration](Migration.md) (22), [BuildingsTemplate](BuildingsTemplate.md) (19), [Household](Household.md) (19), [Nations](Nations.md) (18), [Exchange](Exchange.md) (18), [Equity](Equity.md) (16), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (14), [LandManager](LandManager.md) (14), [World](World.md) (14), [Good](Good.md) (13), [Bank](Bank.md) (13), [BuildingManager](BuildingManager.md) (12), [OldMetal](OldMetal.md) (12), [CareType](CareType.md) (12), [PriceLevel](PriceLevel.md) (11), [Ports](Ports.md) (11), [FundLedger](FundLedger.md) (11), [TreasuryFund](TreasuryFund.md) (11), [Oil](Oil.md) (10), [EarthSite](EarthSite.md) (10)... and 89 more

**Used by (13):** [BridgeCheck](BridgeCheck.md), [CardCheck](CardCheck.md), [ChartCheck](ChartCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [LandCheck](LandCheck.md), [LandmassCheck](LandmassCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [SubstationCheck](SubstationCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 92 | · 1. where it goes |
| 140 | · 2. writing safely |
| 173 | · 3. a write that cannot possibly work |
| 200 | · 4. the old folder |
| 233 | · 5. it never overwrites a newer save |
| 268 | · 6. a real round trip |
| 519 | · 7. construction survives a save |
| 647 | · 7b. the warning survives a save |
| 731 | · 8. a save with nothing else built |
| 755 | · 8b. a stack emptied keeps its place (0.7.103) |
| 785 | · 9. the headline income does not move |
| 825 | · 10. the whole figure, to the cent |
| 1004 | · 11. a city that is still MOVING |
| 1115 | · 12. a city that OWES money |
| 1220 | · 12b. ...AND ONE THAT OWES ABROAD (2026-09-21) |
| 1324 | · 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY |
| 2532 | · 13b. a bond in the saved history (HD1, 2026-10-29) |
| 2571 | · 14. a reloaded city PLAYS ON as the one it was saved from |
| 2922 | · a recording's switch and id cross a save (TR1, 2026-10-31) |
| 2960 | · the city's fund, its rescue setting and the bank's preferred (0.7.14) |
| 3318 | · the oldest format this build loads (0.7.108) |
| 5077 | · 4. A CITY IS THE SAME CITY AFTER YOU LOAD IT |
| 5146 | · 5. WHAT A RELOAD MUST NOT FORGET |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `SaveFileCheck.OTHER_PATH` | `Nations.DEFAULT_PATH = = Nations.Path.RANDOM ? Nations.Path.HISTORICAL : Nati...` | The path a fixture founds on to be off the default, whichever Nations.DEFAULT_PATH is (WN3: the default became Random, Jerus 2026-10-29). |
| 4482 | `SaveFileCheck.REAL_OUT` | `System.out` | The console, as the harness found it: where quietly() puts the narration back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `static int fails` |  |
| 5008 | `static int fails` |  |
| 5009 | `static PrintStream out` |  |
| 5010 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 5227 | **type** `public class SaveFileCheck` | Verifies where saves go and how they are written. |
| 35 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 40 | 10 | `static void assertEquals(String label, Object actual, Object expected)` |  |
| 52 | 9 | `static void same(String label, double actual, double expected)` | Two readings of one month, which must not differ at all. |
| 63 | 3 | `static String flat(Path p)` | Forward slashes, so the assertions read the same on any host. |
| 67 | 3403 | `public static void main(String[] args) throws Exception` |  |
| 3472 | 3 | `static byte[] native60Bytes(String name) throws IOException` | Exact native .188 bytes, shared with HC1; no current JSON is converted. |
| 3476 | 3 | `static byte[] nativeCardBytes(int format, String name) throws IOException` |  |
| 3480 | 6 | `private static byte[] nativeFixtureBytes(String directory, String name) throws IOException` |  |
| 3488 | 1 | `static boolean nativeCard60Refusal()` | A native .188 record, with the actual 128-slot old cell layout and old history. |
| 3490 | 5 | `static boolean nativeCard60Refusal(boolean readPaths)` |  |
| 3497 | 5 | `static boolean nativeGrid61Refusal(boolean readPaths)` | Native RES2 saves carry housing inventory but no PW5 closing grid costs. |
| 3504 | 5 | `static boolean nativeCard61Refusal(boolean readPaths)` | Keep HC1's independently captured RES2 pair and its refusal assertion. |
| 3510 | 3 | `static byte[] nativeHousingBoundaryBytes(String name) throws IOException` |  |
| 3514 | 17 | `static boolean nativeHousingBoundaryRefusal(boolean readPaths)` |  |
| 3532 | 25 | `private static boolean nativeHousingFixtureRefusal(int format, String version, String saveHash, String historyHash, boolean rea...` |  |
| 3559 | 5 | `static boolean nativeGrid62Refusal(boolean readPaths)` | HC1's published native card layout still has no PW5 closing grid costs. |
| 3565 | 22 | `private static boolean nativeOldFormatRefusal(boolean readPaths, int format, String version, String directory, String saveHash,...` |  |
| 3589 | 39 | `private static boolean nativePairRefusal(int format, String version, byte[] save, byte[] history, boolean readPaths)` | The same byte, manager, city and history guards for each native previous-layout artifact. |
| 3630 | 9 | `static void bits(String label, double actual, double expected)` | Two readings that must be the same double, to the bit. |
| 3653 | 57 | `static void brakeThresholdCrossesASave(Path root) throws IOException` | THE BRAKE'S THRESHOLD THROUGH A SAVE (WG2-R). |
| 3712 | 8 | `private static void sameBrake(String at, Game twin, Game city, int monthsSlot)` | The reading of the brake a twin must share with its original: whether it applies, the jobs' pull, the brake, the arrivals and the months recorded. |
| 3722 | 34 | `static void acuteSicknessCrossesASave() throws IOException` | R0h: nonzero acute shares cross JSON and the actual load, and a missing old field clears them. |
| 3757 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 3765 | 59 | `static void allocatedLandCrossesASave() throws IOException` | AW2: the ordinary loader keeps the live ledger even when raw fixture stacks exceed its ground. |
| 3826 | 31 | `static void landPlotsCrossASave()` | AW2: catalogue edits change the occupied ground, while the fixture's unallocated gap stays the same. |
| 3859 | 79 | `static void constructionContinuesAfterALoad(Path root)` | AW2: cause automatic land buying and private orders, then play live and ordinary-loaded twins together. |
| 3939 | 4 | `private static long standingAndOnSite(Game game, BuildingsTemplate template)` |  |
| 3954 | 41 | `static void everyIdCrossesASave(Path root)` | EVERY ID CROSSES A SAVE, THE HIGHEST PAST A GAP TOO (FR3, 2026-10-19). |
| 4023 | 387 | `static void onEarth(Path root) throws IOException` | A CITY ON THE REAL EARTH (0.7.121, batch E4; spec-earth 6). |
| 4422 | 46 | `static void refusedIntoARunningCity(GameFiles files, Game running, String olderBuild, com.google.gson.JsonObject newerSave) thr...` | A REFUSED LOAD LEAVES THE CITY IN PLAY ALONE (FW1, 0.8.37). |
| 4470 | 10 | `static java.util.List<String> jsonDiff(com.google.gson.JsonObject a, com.google.gson.JsonObject b, java.util.Set<String> except)` | The keys two saves differ on, by name and value, but those left out. |
| 4485 | 8 | `static boolean restoredWithoutSellers(LandMarket from)` | Whether a market's offers, restored with no sellers (a save from before WN5, or without its key), come back as many offers all the host's whole. |
| 4495 | 4 | `static void quietly(Runnable r)` | Runs a piece of the city with the game's narration off. |
| 4500 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 4520 | 10 | `static boolean refusedAtTheFloor(GameFiles files, int slot, Game loaded, byte[] before)` | Whether a load of `slot` from `files` into `loaded` was refused at the floor (0.7.120, batch I3): the save's format below GameVersion.OLDEST_LOADABLE_FORMAT, the game told in P7's words naming the build that saved it ... |
| 4532 | 9 | `static java.util.Map<String, byte[]> folderBytes(Path dir) throws IOException` | Every file under a folder and its bytes, by its path within it - to say nothing in it moved. |
| 4549 | 343 | `static void worldMarketCrossesASave(Path root) throws IOException` | THE WORLD MARKET CROSSES A SAVE (WN3, format 48; Game, THE WORLD MARKET). |
| 4894 | 6 | `static String printedMonths(Game city, int months)` | The narration of some months of a city, as text. |
| 4902 | 7 | `static void cleanUp(Path root)` | Temp directory only - nothing here ever points at a real save folder. |
| 4911 | 46 | `static void landLoadFallbacks() throws Exception` | HA1: current-format land is repaired, or refused before a live city is rebuilt. |
| 5006 | 248 | **type** `static final class LoadConservation` | Nothing is created and nothing is destroyed. |
| 5015 | 6 | `static void check(String label, double actual, double expected, double tol)` _(in SaveFileCheck.LoadConservation)_ |  |
| 5022 | 4 | `static void assertTrue(String label, boolean ok)` _(in SaveFileCheck.LoadConservation)_ |  |
| 5028 | 43 | `static Game city(Path root, String name, int months) throws Exception` _(in SaveFileCheck.LoadConservation)_ | A city with something of everything in it, so every law has something to test. |
| 5072 | 160 | `static void run(String[] args) throws Exception` _(in SaveFileCheck.LoadConservation)_ |  |
| 5234 | 11 | `static double[] snapshot(Game g)` _(in SaveFileCheck.LoadConservation)_ | The figures a load must not move. |
| 5246 | 7 | `static void cleanUp(Path root)` _(in SaveFileCheck.LoadConservation)_ |  |


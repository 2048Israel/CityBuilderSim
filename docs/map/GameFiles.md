# GameFiles.java - 516 lines · 42 methods · 10 constants · model

`ham/citybuildersim/GameFiles.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> Where the game keeps its files, and how it writes them.
> 
> WHY THIS EXISTS AT ALL
> 
> The save path used to be a literal - Path.of(userHome, "YourGame", "save.json")
> - written out FOUR separate times: in DataSave, in HistorySave, in
> Game.loadGame() and in Game.loadHistory(). Four copies of one fact is three
> chances to change it in three places and forget the fourth, and the failure
> mode is the nastiest kind: the game saves happily to one folder and loads an
> older file from another, so a player loses hours and nothing anywhere reports
> an error.
> 
> WHY NOT user.home/YourGame
> 
> "YourGame" is a tutorial placeholder, and dropping a folder straight into
> someone's home directory is the kind of thing people notice and resent. More
> practically, Steam Cloud syncs from a fixed set of known roots - %APPDATA%
> among them - and a bare folder in the home root is not one of them, so saves
> kept there could never be synced.
> 
> So: %APPDATA%\CityBuilderSim on Windows, and the equivalent convention on the
> other two platforms, because getting this right once is cheaper than
> discovering it after players have saves worth keeping.
> 
> THE OLD FOLDER IS NOT DELETED
> 
> migrateLegacy() COPIES the old files across and leaves the originals exactly
> where they were. A migration that moves files is a migration that can destroy
> a save if it half-runs, and the disk cost of leaving a few kilobytes behind is
> nothing against the cost of being wrong.
> 
> WRITING
> 
> write() never writes over a good save. It writes a .tmp beside the target,
> copies the existing save to .bak, and only then swaps the .tmp into place -
> atomically where the filesystem supports it. If the disk is full or the
> process dies mid-write, the worst case is a stray .tmp file and a save that is
> one autosave old, instead of a truncated file that loads as a corrupt city.

**Uses:** [SaveHeader](SaveHeader.md) (3), [TempDirs](TempDirs.md) (1), [GameLog](GameLog.md) (1)

**Used by (145):** [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BridgeCheck](BridgeCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBuilderSim](CityBuilderSim.md), [ClockCheck](ClockCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [ForestryCheck](ForestryCheck.md), [FoundingYardCheck](FoundingYardCheck.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GameLog](GameLog.md), [GamePrefs](GamePrefs.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [HydroCheck](HydroCheck.md), [InboxCheck](InboxCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [ReloadCheck](ReloadCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunRenders](RunRenders.md), [RunWorksCheck](RunWorksCheck.md), [SaveDump](SaveDump.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [SicknessCheck](SicknessCheck.md), [SkipReportCheck](SkipReportCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeBandCheck](TradeBandCheck.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 61 | · slots |
| 113 | · locations |
| 224 | · migration |
| 282 | · reading |
| 339 | · writing |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `GameFiles.APP_NAME` | `"CityBuilderSim"` |  |
| 58 | `GameFiles.SAVE_FILE` | `"save.json"` |  |
| 59 | `GameFiles.HISTORY_FILE` | `"history.json"` |  |
| 73 | `GameFiles.SLOT_COUNT` | `10` |  |
| 74 | `GameFiles.AUTOSAVE_SLOT` | `0` |  |
| 75 | `GameFiles.SAVES_FOLDER` | `"saves"` |  |
| 78 | `GameFiles.LEGACY_FOLDER` | `"YourGame"` | The folder the game used before this class existed. |
| 440 | `GameFiles.SWAP_RETRIES` | `10` | Times a swap is tried again when Windows refuses it because another handle has the file open: a reader, an antivirus scan, the indexer (LB3's gate review). |
| 443 | `GameFiles.SWAP_RETRY_MS` | `50` | The wait before each of those tries, in milliseconds: the ten cover half a second, long enough for a reader or a scan to let go, short enough that a write that cannot land says so. |
| 446 | `GameFiles.WAITED` | `new java.util.concurrent.atomic.AtomicInteger()` | Swaps refused at first that landed on a later try, since the program started; each is in the log too (SaveSlotCheck 7d counts them). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 80 | `private final Path directory` |  |
| 81 | `private final Path legacyDirectory` |  |
| 349 | `public final boolean ok` |  |
| 350 | `public final Path file` |  |
| 351 | `public final String error` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 463 | **type** `public final class GameFiles` | Where the game keeps its files, and how it writes them. |

### slots (lines 61-112)

| line | len | member | says |
|---:|---:|---|---|
| 83 | 3 | `public GameFiles()` |  |
| 88 | 4 | `GameFiles(Path directory, Path legacyDirectory)` | For tests, which must never touch the real user's save folder. |
| 104 | 8 | `public static GameFiles scratch(String label)` | A throwaway folder for a harness city. |

### locations (lines 113-223)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 1 | `public Path getDirectory()` |  |
| 118 | 1 | `public Path legacyFlatSave()` | The pre-slot single save. |
| 119 | 1 | `public Path legacyFlatHistory()` |  |
| 121 | 1 | `public Path savesDirectory()` |  |
| 123 | 1 | `public Path saveFile(int slot)` |  |
| 124 | 1 | `public Path historyFile(int slot)` |  |
| 132 | 1 | `public Path mapFile(int slot)` | The slot's city map, beside its save (0.7.60, batch J3): slot-NN-map.bin, the districts' counts deflated (CityMap's sidecar). |
| 144 | 1 | `public Path yearBookFile()` | THE YEAR BOOK AND THE DECADE BOOK |
| 145 | 1 | `public Path decadeBookFile()` |  |
| 153 | 1 | `public Path yearBookCsv()` | ...AND THEIR TABLES AS CSV (0.7.16), beside them under the same stem: each book's main table, and its WITHIN table of the [~] columns' worst and best months. |
| 154 | 1 | `public Path yearBookWithinCsv()` |  |
| 155 | 1 | `public Path decadeBookCsv()` |  |
| 156 | 1 | `public Path decadeBookWithinCsv()` |  |
| 162 | 3 | `private static String stem(int slot)` | Zero-padded, so the folder sorts the way a person reads it and slot 10 does not land between 1 and 2. |
| 166 | 3 | `public static boolean isValidSlot(int slot)` |  |
| 171 | 3 | `public static String slotLabel(int slot)` | "Autosave", "Slot 1"... |
| 175 | 7 | `public static Path defaultDirectory()` |  |
| 183 | 3 | `public static Path defaultLegacyDirectory()` |  |
| 195 | 24 | `static Path resolveDirectory(String osName, String appData, String xdgDataHome, String userHome)` | The convention for each platform, as a pure function of its inputs. |
| 220 | 3 | `private static boolean notBlank(String s)` |  |

### migration (lines 224-281)

| line | len | member | says |
|---:|---:|---|---|
| 234 | 30 | `public List<String> migrateLegacy()` | Brings saves over from the old folder. |
| 265 | 16 | `private void copyIfAbsent(Path from, Path to, String description, List<String> copied)` |  |

### reading (lines 282-338)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 18 | `public SaveHeader readHeader(int slot)` | Reads just enough of a slot to label it, or null if the slot is empty. |
| 311 | 3 | `public boolean slotIsEmpty(int slot)` | No file at all. |
| 329 | 3 | `public boolean slotIsUnreadable(int slot)` | True when a slot holds a file the game cannot make sense of. |
| 334 | 4 | `public boolean slotIsLoadable(int slot)` | Loadable: something is there, and it can be read. |

### writing (lines 339-516)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 32 | **type** `public static final class Result` | What a write attempt did. |
| 353 | 5 | `private Result(boolean ok, Path file, String error)` _(in GameFiles.Result)_ |  |
| 359 | 1 | `static Result succeeded(Path file)` _(in GameFiles.Result)_ |  |
| 362 | 3 | `static Result failed(Path file, String reason)` _(in GameFiles.Result)_ | For a failure that is not an exception - see DataSave.saveGame(). |
| 366 | 6 | `static Result failed(Path file, Throwable cause)` _(in GameFiles.Result)_ |  |
| 374 | 4 | `public String message()` _(in GameFiles.Result)_ | One line, fit to show a player. |
| 380 | 3 | `public Result write(Path file, String contents)` |  |
| 385 | 18 | `public Result write(Path file, byte[] contents)` | The same, for bytes (0.7.60: the city map's sidecar): the .tmp, the .bak, then the swap. |
| 406 | 3 | **type** `public interface Body` | What a streamed write puts in the file, written to {@code out} (which the caller closes). |
| 407 | 1 | `void writeTo(java.io.Writer out) throws IOException` _(in GameFiles.Body)_ |  |
| 419 | 19 | `public Result write(Path file, Body body)` | The same, streamed (UP1, 2026-10-22): the body writes straight into the .tmp through a buffered UTF-8 writer instead of being built whole as a String first - a 13 MB history was a String of 13 MB on the FX thread, and... |
| 449 | 3 | `static int swapsThatWaited()` | How many swaps had to wait for a file another handle held open, and landed. |
| 465 | 29 | `private static Result swapIn(Path file, Path tmp) throws IOException` | The tail both writes share: the existing file copied to .bak, then the finished .tmp moved over it. |
| 496 | 12 | `private static void moveOver(Path tmp, Path file) throws IOException` | The finished .tmp moved over the file, atomically where the filesystem can. |
| 509 | 7 | `private static void deleteQuietly(Path path)` |  |


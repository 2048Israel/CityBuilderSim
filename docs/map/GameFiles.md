# GameFiles.java - 401 lines · 31 methods · 7 constants · model

`ham/citybuildersim/GameFiles.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [SaveHeader](SaveHeader.md) (3), [GameLog](GameLog.md) (1)

**Used by (67):** [AgricultureCheck](AgricultureCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBuilderSim](CityBuilderSim.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DataSave](DataSave.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [GameLog](GameLog.md), [GamePrefs](GamePrefs.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PopulationCheck](PopulationCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveDump](SaveDump.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [ShadowBasket](ShadowBasket.md), [SicknessCheck](SicknessCheck.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 59 | · slots |
| 111 | · locations |
| 203 | · migration |
| 261 | · reading |
| 318 | · writing |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `GameFiles.APP_NAME` | `"CityBuilderSim"` |  |
| 56 | `GameFiles.SAVE_FILE` | `"save.json"` |  |
| 57 | `GameFiles.HISTORY_FILE` | `"history.json"` |  |
| 71 | `GameFiles.SLOT_COUNT` | `10` |  |
| 72 | `GameFiles.AUTOSAVE_SLOT` | `0` |  |
| 73 | `GameFiles.SAVES_FOLDER` | `"saves"` |  |
| 76 | `GameFiles.LEGACY_FOLDER` | `"YourGame"` | The folder the game used before this class existed. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `private final Path directory` |  |
| 79 | `private final Path legacyDirectory` |  |
| 328 | `public final boolean ok` |  |
| 329 | `public final Path file` |  |
| 330 | `public final String error` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 350 | **type** `public final class GameFiles` | Where the game keeps its files, and how it writes them. |

### slots (lines 59-110)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 3 | `public GameFiles()` |  |
| 86 | 4 | `GameFiles(Path directory, Path legacyDirectory)` | For tests, which must never touch the real user's save folder. |
| 102 | 8 | `public static GameFiles scratch(String label)` | A throwaway folder for a harness city. |

### locations (lines 111-202)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 1 | `public Path getDirectory()` |  |
| 116 | 1 | `public Path legacyFlatSave()` | The pre-slot single save. |
| 117 | 1 | `public Path legacyFlatHistory()` |  |
| 119 | 1 | `public Path savesDirectory()` |  |
| 121 | 1 | `public Path saveFile(int slot)` |  |
| 122 | 1 | `public Path historyFile(int slot)` |  |
| 134 | 1 | `public Path yearBookFile()` | THE YEAR BOOK AND THE DECADE BOOK |
| 135 | 1 | `public Path decadeBookFile()` |  |
| 141 | 3 | `private static String stem(int slot)` | Zero-padded, so the folder sorts the way a person reads it and slot 10 does not land between 1 and 2. |
| 145 | 3 | `public static boolean isValidSlot(int slot)` |  |
| 150 | 3 | `public static String slotLabel(int slot)` | "Autosave", "Slot 1"... |
| 154 | 7 | `public static Path defaultDirectory()` |  |
| 162 | 3 | `public static Path defaultLegacyDirectory()` |  |
| 174 | 24 | `static Path resolveDirectory(String osName, String appData, String xdgDataHome, String userHome)` | The convention for each platform, as a pure function of its inputs. |
| 199 | 3 | `private static boolean notBlank(String s)` |  |

### migration (lines 203-260)

| line | len | member | says |
|---:|---:|---|---|
| 213 | 30 | `public List<String> migrateLegacy()` | Brings saves over from the old folder. |
| 244 | 16 | `private void copyIfAbsent(Path from, Path to, String description, List<String> copied)` |  |

### reading (lines 261-317)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 18 | `public SaveHeader readHeader(int slot)` | Reads just enough of a slot to label it, or null if the slot is empty. |
| 290 | 3 | `public boolean slotIsEmpty(int slot)` | No file at all. |
| 308 | 3 | `public boolean slotIsUnreadable(int slot)` | True when a slot holds a file the game cannot make sense of. |
| 313 | 4 | `public boolean slotIsLoadable(int slot)` | Loadable: something is there, and it can be read. |

### writing (lines 318-401)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 32 | **type** `public static final class Result` | What a write attempt did. |
| 332 | 5 | `private Result(boolean ok, Path file, String error)` _(in GameFiles.Result)_ |  |
| 338 | 1 | `static Result succeeded(Path file)` _(in GameFiles.Result)_ |  |
| 341 | 3 | `static Result failed(Path file, String reason)` _(in GameFiles.Result)_ | For a failure that is not an exception - see DataSave.saveGame(). |
| 345 | 6 | `static Result failed(Path file, Throwable cause)` _(in GameFiles.Result)_ |  |
| 353 | 4 | `public String message()` _(in GameFiles.Result)_ | One line, fit to show a player. |
| 359 | 34 | `public Result write(Path file, String contents)` |  |
| 394 | 7 | `private static void deleteQuietly(Path path)` |  |


# TempDirs.java - 141 lines · 7 methods · 1 constants · model

`ham/citybuildersim/TempDirs.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The one place a harness, or a scratch city, gets a temporary folder from:
> one folder per JVM under the system temp folder, deleted when that JVM exits.
> 
> <p>WHY (batch TD1, 2026-10-21): every harness made its temp folders with
> {@code Files.createTempDirectory(prefix)} straight into the system temp
> folder and never removed them. A suite run makes about 95, so a week of
> suite runs on Jerus's PC left some 37 GB of {@code autobuildcheck...},
> {@code educheck...} and {@code mapcheck...} folders in his %TEMP%, nearly
> filling the disk. The call is now {@link #create}: the same call with the
> same checked exception, but into {@code <java.io.tmpdir>/citybuildersim-tmp/<pid>},
> a folder made on the first call, with one shutdown hook that deletes it.
> 
> <p>A JVM that is killed hard (a stopped batch, a closed console) skips its
> shutdown hooks and leaves its folder. So the first call of the next JVM
> sweeps the folders beside its own: one named for a process that is gone is
> deleted, one named for a live process (another batch's suite, Jerus's running
> game) is left alone, and a name that is not a number is none of this class's
> business. Deleting is best effort and never throws, never follows a link, and
> never reaches outside {@code citybuildersim-tmp}. Nothing is printed: in this
> project every {@code println} is the log.
> 
> <p>What this does not do: it does not clean the folders the old calls left
> directly in the temp folder, and it is no home for a file somebody opens
> after the JVM is gone.

**Used by (78):** [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [ConservationCheck](ConservationCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurveCheck](CurveCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EducationCheck](EducationCheck.md), [EnsembleCheck](EnsembleCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FoundingYardCheck](FoundingYardCheck.md), [GameFiles](GameFiles.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HydroCheck](HydroCheck.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [KnownRedsCheck](KnownRedsCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [SicknessCheck](SicknessCheck.md), [StaleCheck](StaleCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TimingCheck](TimingCheck.md), [UiProbe](UiProbe.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `TempDirs.BASE_NAME` | `"citybuildersim-tmp"` | The folder, under the system temp folder, that holds one folder per JVM. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private static Path runRoot` | This JVM's folder, made on the first call; only read or written inside {@link #runRoot()}. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 105 | **type** `public final class TempDirs` | The one place a harness, or a scratch city, gets a temporary folder from: one folder per JVM under the system temp folder, deleted when that JVM exits. |
| 39 | 1 | `private TempDirs()` |  |
| 51 | 3 | `public static Path create(String prefix) throws IOException` | A new empty folder named from {@code prefix}, inside this JVM's folder; {@code Files.createTempDirectory(prefix)} with the clean-up added. |
| 56 | 11 | `private static synchronized Path runRoot() throws IOException` | This JVM's folder: made, hooked for deletion and the stale folders beside it swept, once per JVM. |
| 68 | 3 | `private static Path base()` |  |
| 73 | 15 | `private static void sweep(Path base)` | Deletes the folders beside this JVM's whose name is the number of a process that no longer exists. |
| 96 | 29 | `private static void deleteTree(Path target, Path base)` | Deletes {@code target} and everything under it, files first and each folder after its contents. |
| 127 | 14 | `private static void tryDelete(Path p)` | One entry deleted if it can be; a read-only one is made writable and tried once more. |


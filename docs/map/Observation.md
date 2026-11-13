# Observation.java - 67 lines · 5 methods · 3 constants · model

`ham/citybuildersim/Observation.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The played city's path premises, reported without counting a failure.
> 
> WHY (PO1): a model change moved 465 path lines in 132 harnesses. A missed
> premise must skip its dependent laws, while every law that runs stays hard.
> Nightly records both reached and missed premises to retain each last hold.

**Uses:** [Timing](Timing.md) (1)

**Used by (31):** [AllChecks](AllChecks.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CentralBankCheck](CentralBankCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DeskCheck](DeskCheck.md), [FittingCheck](FittingCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignCheck](ForeignCheck.md), [FundCheck](FundCheck.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HousingCheck](HousingCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [MapCheck](MapCheck.md), [MonetaryCheck](MonetaryCheck.md), [ObservationCheck](ObservationCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Observation.PROPERTY` | `"cbs.observations"` | Nightly's mode records every reading, including holds, for merge attribution. |
| 20 | `Observation.TSV_PROPERTY` | `"cbs.observations.tsv"` | The shared nightly record, harness, label and REACHED or MISSED, tab-separated. |
| 23 | `Observation.HARNESSES` | `List.of("ScaleCheck", "HoldersCheck", "SaveFileCheck", "CurrencyRegimeCheck",...` | Every harness with path observations, also run nightly alongside HA1's tier. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 13 | 55 | **type** `public final class Observation` | The played city's path premises, reported without counting a failure. |
| 14 | 1 | `private Observation()` |  |
| 32 | 3 | `static boolean reached(String label, boolean reached)` | A path reading with no dependent laws, such as a sample share. |
| 37 | 3 | `static boolean reached(String label, boolean reached, String laws)` | Returns the actual premise, so callers run only laws whose premise held. |
| 42 | 17 | `static boolean reached(String identity, String label, boolean reached, String laws)` | A stable line identity when the printed label contains that run's measured figures. |
| 61 | 6 | `static void forwardTo(List<String> command)` | Forwards the mode and record to each harness JVM. |


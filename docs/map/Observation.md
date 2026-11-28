# Observation.java - 71 lines · 5 methods · 4 constants · model

`ham/citybuildersim/Observation.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The played city's path premises, reported without counting a failure.
> 
> WHY (PO1): a model change moved 465 path lines in 132 harnesses. A missed
> premise must skip its dependent laws, while every law that runs stays hard.
> Nightly records both reached and missed premises to retain each last hold.

**Uses:** [Timing](Timing.md) (1)

**Used by (11):** [AllChecks](AllChecks.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [FittingCheck](FittingCheck.md), [HealthCheck](HealthCheck.md), [HousingCheck](HousingCheck.md), [ObservationCheck](ObservationCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [ScaleCheck](ScaleCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Observation.PROPERTY` | `"cbs.observations"` | Nightly's mode records every reading, including holds, for merge attribution. |
| 20 | `Observation.TSV_PROPERTY` | `"cbs.observations.tsv"` | The shared nightly record, harness, label and REACHED or MISSED, tab-separated. |
| 23 | `Observation.HARNESS_PROPERTY` | `"cbs.observations.harness"` | The running harness, set on its JVM even when another harness supplies a fixture. |
| 26 | `Observation.HARNESSES` | `List.of("ScaleCheck", "AutoBuildCheck", "PriceLevelCheck", "FittingCheck", "B...` | Every harness with path observations, also run nightly alongside HA1's tier. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 13 | 59 | **type** `public final class Observation` | The played city's path premises, reported without counting a failure. |
| 14 | 1 | `private Observation()` |  |
| 35 | 3 | `static boolean reached(String label, boolean reached)` | A path reading with no dependent laws, such as a sample share. |
| 40 | 3 | `static boolean reached(String label, boolean reached, String laws)` | Returns the actual premise, so callers run only laws whose premise held. |
| 45 | 18 | `static boolean reached(String identity, String label, boolean reached, String laws)` | A stable line identity when the printed label contains that run's measured figures. |
| 65 | 6 | `static void forwardTo(List<String> command)` | Forwards the mode and record to each harness JVM. |


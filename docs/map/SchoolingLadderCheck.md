# SchoolingLadderCheck.java - 171 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/SchoolingLadderCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Hand-summed monthly cash ledgers for every after-cost qualification (WG3).
> WHY. A closed-form annuity repeated in a check could agree with the same
> indexing error in the model; these sums book each month's wage and fee.
> No city is played and no save folder is opened.

**Uses:** [EducationType](EducationType.md) (47), [SchoolingLadder](SchoolingLadder.md) (36), [AdultStage](AdultStage.md) (20), [JobType](JobType.md) (14), [PopulationCohorts](PopulationCohorts.md) (5), [LabourMarket](LabourMarket.md) (4), [Education](Education.md) (2), [TaxPolicy](TaxPolicy.md) (2), [WageBand](WageBand.md) (2), [PopulationManager](PopulationManager.md) (1), [LabourCheck](LabourCheck.md) (1)

**Used by (2):** [EducationCheck](EducationCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 12 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 161 | **type** `public class SchoolingLadderCheck` | Hand-summed monthly cash ledgers for every after-cost qualification (WG3). |
| 14 | 4 | `static void check(String label, boolean ok)` |  |
| 19 | 4 | `static void close(String label, double actual, double expected)` |  |
| 24 | 6 | `static LabourMarket wages()` |  |
| 32 | 16 | `static double handSum(int life, int start, int duration, boolean teen, double wage, double before, double monthlyFee, double an...` | The independent ledger: a payment in month m is worth payment/(1+r)^m today. |
| 49 | 9 | `static PopulationCohorts stages(double young, double established)` |  |
| 59 | 112 | `public static void main(String[] args)` |  |


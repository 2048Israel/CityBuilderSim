# SchoolingLadderCheck.java - 229 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/SchoolingLadderCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Hand-summed monthly cash ledgers for every after-cost qualification (WG3).
> WHY. A closed-form annuity repeated in a check could agree with the same
> indexing error in the model; these sums book each month's wage and fee.
> No city is played and no save folder is opened.

**Uses:** [EducationType](EducationType.md) (65), [SchoolingLadder](SchoolingLadder.md) (46), [AdultStage](AdultStage.md) (27), [JobType](JobType.md) (22), [PopulationCohorts](PopulationCohorts.md) (5), [LabourMarket](LabourMarket.md) (4), [Education](Education.md) (2), [TaxPolicy](TaxPolicy.md) (2), [WageBand](WageBand.md) (2), [PopulationManager](PopulationManager.md) (1), [LabourCheck](LabourCheck.md) (1)

**Used by (2):** [EducationCheck](EducationCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 12 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 219 | **type** `public class SchoolingLadderCheck` | Hand-summed monthly cash ledgers for every after-cost qualification (WG3). |
| 14 | 4 | `static void check(String label, boolean ok)` |  |
| 19 | 4 | `static void close(String label, double actual, double expected)` |  |
| 24 | 6 | `static LabourMarket wages()` |  |
| 37 | 12 | `static double ledger(int life, double annual, double wage, int earnFrom, double[]...courses)` | The independent ledger of one path's cash: a payment in month m is worth payment/(1+r)^m at 18, a teen's month (m <= 0) carried forward. |
| 51 | 3 | `static double handSum(int life, int duration, double wage, double monthlyFee, double annual)` | An adult course taken at 18 from a free diploma, the shape EducationCheck borrows. |
| 55 | 9 | `static PopulationCohorts stages(double young, double established)` |  |
| 65 | 164 | `public static void main(String[] args)` |  |


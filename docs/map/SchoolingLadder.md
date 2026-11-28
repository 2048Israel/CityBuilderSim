# SchoolingLadder.java - 173 lines · 15 methods · 2 constants · model

`ham/citybuildersim/SchoolingLadder.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> What each qualification pays after its course's cost, as a level monthly wage.
> 
> WHY (WG3/WS2). Gross wages promised a doctor's salary to an unlicensed degree
> holder and made four years out of work free. This is spec-wages 3.5's ladder.py,
> with HS2's remaining adult stages in place of its single adult lifetime.
> WageLadder remains the historical founding wage table, with its APIs intact.
> Prices and wages are held at today's level: this is a comparison, not a forecast.
> 
> A row is its whole path's value: every fee along it (a degree's high-school bill, a
> licence's university bill) and its earnings once its courses end. The wages a student
> gives up are what the step below earns meanwhile, so they are in the gap between two
> rows already; WG3's first cut also took them off the row, as ladder.py did, which
> counted them twice (Becker 1964: the cost of schooling is its fees and the earnings
> forgone, once) and left a degree paying 1.2 diplomas worth less than the diploma.

**Uses:** [EducationType](EducationType.md) (31), [JobType](JobType.md) (9), [WageBand](WageBand.md) (7), [AdultStage](AdultStage.md) (5), [PopulationManager](PopulationManager.md) (4), [LabourMarket](LabourMarket.md) (3), [Education](Education.md) (2), [TaxPolicy](TaxPolicy.md) (1)

**Used by (7):** [BuildAdviceCheck](BuildAdviceCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [PolicyScreen](PolicyScreen.md), [SchoolingLadderCheck](SchoolingLadderCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md), [SchoolingProbe](SchoolingProbe.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 32 | `SchoolingLadder.Verdict.BASE` |  |
| 33 | `SchoolingLadder.Verdict.NO_POSTS` |  |
| 34 | `SchoolingLadder.Verdict.UNDER` |  |
| 35 | `SchoolingLadder.Verdict.FAR` |  |
| 36 | `SchoolingLadder.Verdict.ABOVE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `SchoolingLadder.FLOOR_RATE` | `.03` | A family's annual price of waiting when borrowing is cheaper, an estimate (spec-wages 3.5). |
| 29 | `SchoolingLadder.FAR_RETURN` | `1` | A return reaching full willingness counts as far above the prerequisite (Education's curve). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `private final String words` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 152 | **type** `public final class SchoolingLadder` | What each qualification pays after its course's cost, as a level monthly wage. |
| 23 | 1 | `private SchoolingLadder()` |  |
| 31 | 11 | **type** `public enum Verdict` |  |
| 39 | 1 | `Verdict(String words)` _(in SchoolingLadder.Verdict)_ |  |
| 40 | 1 | `public String words()` _(in SchoolingLadder.Verdict)_ |  |
| 47 | 4 | **type** `public record Row(EducationType qualification, String label, JobType job, double paid, double target, doubl...` | One qualification; NONE is no diploma, HIGH is a diploma. |
| 53 | 30 | **type** `public record View(AdultStage stage, double annualRate, int workingMonths, List<Row> rows)` | An immutable snapshot, so every bar and verdict reads the same month's figures. |
| 54 | 1 | `{ ... }` _(in SchoolingLadder.View)_ |  |
| 56 | 3 | `public Row row(EducationType type)` _(in SchoolingLadder.View)_ |  |
| 60 | 3 | `public Row before(EducationType type)` _(in SchoolingLadder.View)_ |  |
| 70 | 5 | `public double gain(EducationType type)` _(in SchoolingLadder.View)_ | The enrollment curve's input, before its exponent and cap: the step's value over the one it is taken from, both net of the fees already paid on the way to that one. |
| 76 | 6 | `public Verdict verdict(EducationType type, long posts)` _(in SchoolingLadder.View)_ |  |
| 85 | 4 | `static EducationType prior(EducationType type)` | The step a course is taken from: a licence from a degree, a degree or college from a diploma. |
| 91 | 5 | `public static int workingMonths(AdultStage stage)` | Expected work left in HS2's compartments: a young stage still has the established stage ahead. |
| 98 | 8 | `public static JobType bestJob(LabourMarket market, WageBand band)` | A degree earns the best ungated wage in its band; a licence earns its own job's wage. |
| 107 | 4 | `public static View of(LabourMarket market, Education schools, TaxPolicy tax, double policyRate, AdultStage stage)` |  |
| 112 | 39 | `static View atRates(LabourMarket market, Education schools, double loanRate, double policyRate, AdultStage stage)` |  |
| 152 | 3 | `private static double annuity(int months, double rate)` |  |
| 157 | 10 | `public static long posts(Row row, PopulationManager people)` | Posts for this qualification, excluding licensed jobs from a degree's prospects. |
| 169 | 4 | `public static double holders(Row row, PopulationManager people)` | Band supply or recorded licence holders, matching the population model's existing inventories. |


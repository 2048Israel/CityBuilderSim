# SchoolingLadder.java - 148 lines · 14 methods · 2 constants · model

`ham/citybuildersim/SchoolingLadder.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> What each qualification pays after its course's cost, as a level monthly wage.
> 
> WHY (WG3/WS2). Gross wages promised a doctor's salary to an unlicensed degree
> holder and made four years out of work free. This is spec-wages 3.5's ladder.py,
> with HS2's remaining adult stages in place of its single adult lifetime.
> WageLadder remains the historical founding wage table, with its APIs intact.
> Prices and wages are held at today's level: this is a comparison, not a forecast.

**Uses:** [EducationType](EducationType.md) (27), [JobType](JobType.md) (9), [WageBand](WageBand.md) (7), [AdultStage](AdultStage.md) (5), [PopulationManager](PopulationManager.md) (4), [LabourMarket](LabourMarket.md) (3), [Education](Education.md) (2), [TaxPolicy](TaxPolicy.md) (1)

**Used by (6):** [Education](Education.md), [EducationCheck](EducationCheck.md), [PolicyScreen](PolicyScreen.md), [SchoolingLadderCheck](SchoolingLadderCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md), [SchoolingProbe](SchoolingProbe.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 25 | `SchoolingLadder.Verdict.BASE` |  |
| 26 | `SchoolingLadder.Verdict.NO_POSTS` |  |
| 27 | `SchoolingLadder.Verdict.UNDER` |  |
| 28 | `SchoolingLadder.Verdict.FAR` |  |
| 29 | `SchoolingLadder.Verdict.ABOVE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 19 | `SchoolingLadder.FLOOR_RATE` | `.03` | A family's annual price of waiting when borrowing is cheaper, an estimate (spec-wages 3.5). |
| 22 | `SchoolingLadder.FAR_RETURN` | `1` | A return reaching full willingness counts as far above the prerequisite (Education's curve). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 31 | `private final String words` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 134 | **type** `public final class SchoolingLadder` | What each qualification pays after its course's cost, as a level monthly wage. |
| 16 | 1 | `private SchoolingLadder()` |  |
| 24 | 11 | **type** `public enum Verdict` |  |
| 32 | 1 | `Verdict(String words)` _(in SchoolingLadder.Verdict)_ |  |
| 33 | 1 | `public String words()` _(in SchoolingLadder.Verdict)_ |  |
| 37 | 3 | **type** `public record Row(EducationType qualification, String label, JobType job, double paid, double target, doubl...` | One qualification; NONE is no diploma, HIGH is a diploma. |
| 42 | 26 | **type** `public record View(AdultStage stage, double annualRate, int workingMonths, List<Row> rows)` | An immutable snapshot, so every bar and verdict reads the same month's figures. |
| 43 | 1 | `{ ... }` _(in SchoolingLadder.View)_ |  |
| 45 | 3 | `public Row row(EducationType type)` _(in SchoolingLadder.View)_ |  |
| 49 | 4 | `public Row before(EducationType type)` _(in SchoolingLadder.View)_ |  |
| 55 | 5 | `public double gain(EducationType type)` _(in SchoolingLadder.View)_ | The enrollment curve's input, before its exponent and cap. |
| 61 | 6 | `public Verdict verdict(EducationType type, long posts)` _(in SchoolingLadder.View)_ |  |
| 70 | 5 | `public static int workingMonths(AdultStage stage)` | Expected work left in HS2's compartments: a young stage still has the established stage ahead. |
| 77 | 8 | `public static JobType bestJob(LabourMarket market, WageBand band)` | A degree earns the best ungated wage in its band; a licence earns its own job's wage. |
| 86 | 4 | `public static View of(LabourMarket market, Education schools, TaxPolicy tax, double policyRate, AdultStage stage)` |  |
| 91 | 35 | `static View atRates(LabourMarket market, Education schools, double loanRate, double policyRate, AdultStage stage)` |  |
| 127 | 3 | `private static double annuity(int months, double rate)` |  |
| 132 | 10 | `public static long posts(Row row, PopulationManager people)` | Posts for this qualification, excluding licensed jobs from a degree's prospects. |
| 144 | 4 | `public static double holders(Row row, PopulationManager people)` | Band supply or recorded licence holders, matching the population model's existing inventories. |


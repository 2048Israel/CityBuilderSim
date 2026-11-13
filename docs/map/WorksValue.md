# WorksValue.java - 131 lines · 8 methods · 0 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (28), [BuildingsTemplate](BuildingsTemplate.md) (9), [Health](Health.md) (9), [DecisionLog](DecisionLog.md) (7), [Game](Game.md) (5), [Sector](Sector.md) (5), [Crime](Crime.md) (4), [Traffic](Traffic.md) (3), [JobType](JobType.md) (2), [PopulationManager](PopulationManager.md) (1), [WageBand](WageBand.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [Statement](Statement.md) (1), [CareType](CareType.md) (1)

**Used by (5):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [EssentialWorksCheck](EssentialWorksCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 121 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
| 12 | 1 | `private WorksValue()` |  |
| 15 | 12 | **type** `public record Value(double capital, double land, double running, double avoided, double payroll, double pay...` | Present values in local thousands; jobs and payroll are shown separately from their tax return. |
| 17 | 1 | `public double cost()` _(in WorksValue.Value)_ |  |
| 18 | 1 | `public double net()` _(in WorksValue.Value)_ |  |
| 19 | 7 | `public String words()` _(in WorksValue.Value)_ |  |
| 28 | 22 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, int count, dou...` |  |
| 52 | 20 | `public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Intege...` | Twenty yearly midpoints, the same real rate and long city trend as the service comparison. |
| 73 | 4 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added, BuildAdvice.Ahead at)` |  |
| 79 | 52 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> before, Map<BuildingsTemplate, Inte...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


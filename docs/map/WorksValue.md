# WorksValue.java - 181 lines · 12 methods · 0 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (40), [BuildingsTemplate](BuildingsTemplate.md) (20), [Game](Game.md) (9), [DecisionLog](DecisionLog.md) (7), [Sector](Sector.md) (5), [Health](Health.md) (5), [Crime](Crime.md) (4), [Traffic](Traffic.md) (3), [JobType](JobType.md) (2), [ConstructionControl](ConstructionControl.md) (2), [PopulationManager](PopulationManager.md) (1), [WageBand](WageBand.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [Statement](Statement.md) (1), [CareType](CareType.md) (1)

**Used by (7):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 171 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
| 12 | 1 | `private WorksValue()` |  |
| 15 | 14 | **type** `public record Value(double capital, double land, double running, double avoided, double payroll, double pay...` | Present values in local thousands; jobs and payroll are shown separately from their tax return. |
| 17 | 1 | `public double cost()` _(in WorksValue.Value)_ |  |
| 18 | 1 | `public double net()` _(in WorksValue.Value)_ |  |
| 20 | 1 | `public double perUsed()` _(in WorksValue.Value)_ | Informational cost per physical unit relieved; reserve-only work has no such denominator. |
| 21 | 7 | `public String words()` _(in WorksValue.Value)_ |  |
| 30 | 22 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, int count, dou...` |  |
| 54 | 11 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, int count, double landLeft)` | A paving is the whole replacement plan, including the ground freed and running-cost difference. |
| 66 | 1 | **type** `private record Relief(double used, double avoided)` |  |
| 69 | 4 | `public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Intege...` | Twenty yearly midpoints, the same real rate and long city trend as the service comparison. |
| 75 | 24 | `private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Intege...` | Demand is physical: the reserve dial sizes orders but cannot create lost output. |
| 101 | 18 | `private static Map<BuildingsTemplate, Integer> powerOnSiteAt(Game g, Map<BuildingsTemplate, Integer> site, double months)` | Pending power contributes only after its construction output can finish it, including preparation. |
| 120 | 4 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added, BuildAdvice.Ahead at)` |  |
| 126 | 55 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> before, Map<BuildingsTemplate, Inte...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


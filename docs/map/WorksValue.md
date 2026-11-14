# WorksValue.java - 373 lines · 23 methods · 0 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (58), [BuildingsTemplate](BuildingsTemplate.md) (39), [Game](Game.md) (21), [Sector](Sector.md) (10), [DecisionLog](DecisionLog.md) (9), [Health](Health.md) (5), [Crime](Crime.md) (4), [Traffic](Traffic.md) (3), [JobType](JobType.md) (2), [ConstructionControl](ConstructionControl.md) (2), [Statement](Statement.md) (2), [PopulationManager](PopulationManager.md) (1), [WageBand](WageBand.md) (1), [DebtQuote](DebtQuote.md) (1), [AutoBuilder](AutoBuilder.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingType](BuildingType.md) (1), [BuildingManager](BuildingManager.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (7):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 363 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
| 12 | 1 | `private WorksValue()` |  |
| 15 | 19 | **type** `public record Value(double capital, double land, double futureCapital, int futureUnits, double financing, d...` | Present values in local thousands; jobs and payroll are shown separately from their tax return. |
| 18 | 1 | `public double cost()` _(in WorksValue.Value)_ |  |
| 19 | 1 | `public double net()` _(in WorksValue.Value)_ |  |
| 21 | 1 | `public double perUsed()` _(in WorksValue.Value)_ | Informational cost per physical unit relieved; reserve-only work has no such denominator. |
| 22 | 11 | `public String words()` _(in WorksValue.Value)_ |  |
| 35 | 4 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, int count, dou...` |  |
| 41 | 27 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, BuildingsTemplate t, int count, dou...` | The same comparison after earlier cards have used some of the treasury. |
| 70 | 3 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, int count, double landLeft)` | A paving is the whole replacement plan, including the ground freed and running-cost difference. |
| 74 | 12 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, int count, double landLeft, dou...` |  |
| 88 | 2 | **type** `private record Maintenance(BuildingsTemplate template, int count, double landLeft, double cashLeft, double ...` | Later works are priced in the comparison and ordered by later monthly passes. |
| 91 | 2 | **type** `private record Relief(double used, double avoided, double futureCapital, int futureUnits, double futureMont...` |  |
| 94 | 2 | **type** `private record Financing(double monthlyCoupon, double issueCost, double couponPerCash, double issuePerCash)` |  |
| 97 | 9 | `private static Financing financing(Game g, double gap)` |  |
| 107 | 4 | `private static double annuity(double monthlyRate, double months)` |  |
| 113 | 4 | `public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Intege...` | Power at monthly resolution and other services at yearly midpoints, with the same real rate and long city trend. |
| 119 | 96 | `private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTemplate, Intege...` | Demand is physical: the reserve dial sizes orders but cannot create lost output. |
| 217 | 3 | `public static double pendingPowerLoad(Game g, double months)` | Electrical draw of already ordered non-power works that their present queue opens by this date. |
| 221 | 9 | `private static Map<BuildingsTemplate, Integer> pendingLoads(Game g)` |  |
| 231 | 6 | `private static double pendingPowerLoad(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` |  |
| 238 | 1 | **type** `private record PendingWork(int units, double first, double interval, boolean rushed)` |  |
| 241 | 14 | `private static Map<BuildingsTemplate, PendingWork> completionSchedule(Game g, Map<BuildingsTemplate, Integer> site)` | The same queue-aware unit quotes the automatic completion deadline reads. |
| 257 | 27 | `private static Map<BuildingsTemplate, Integer> completedAt(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` | Quote-completed units at this date. |
| 285 | 3 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added, BuildAdvice.Ahead at)` |  |
| 289 | 3 | `private static double cover(double[] sd)` |  |
| 293 | 10 | `private static Map<Sector, double[]> traffic(Game g, BuildAdvice.Measure m)` |  |
| 305 | 11 | `static double shortageLoss(Game g, BuildAdvice.Measure m)` | The current monthly output loss that a fully serving available work could relieve. |
| 318 | 55 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> before, Map<BuildingsTemplate, Inte...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


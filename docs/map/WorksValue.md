# WorksValue.java - 419 lines · 25 methods · 0 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (60), [BuildingsTemplate](BuildingsTemplate.md) (40), [Game](Game.md) (22), [Sector](Sector.md) (10), [DecisionLog](DecisionLog.md) (9), [Health](Health.md) (5), [Crime](Crime.md) (4), [AutoBuilder](AutoBuilder.md) (3), [Traffic](Traffic.md) (3), [JobType](JobType.md) (2), [ConstructionControl](ConstructionControl.md) (2), [Statement](Statement.md) (2), [PopulationManager](PopulationManager.md) (1), [WageBand](WageBand.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [LandManager](LandManager.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingType](BuildingType.md) (1), [BuildingManager](BuildingManager.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (7):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 115 | `private final Game game` |  |
| 116 | `private final java.util.Set<Integer> bought` |  |
| 117 | `private double free, paid` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 409 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
| 12 | 1 | `private WorksValue()` |  |
| 15 | 19 | **type** `public record Value(double capital, double land, double futureCapital, long futureUnits, double financing, ...` | Present values in local thousands; jobs and payroll are shown separately from their tax return. |
| 18 | 1 | `public double cost()` _(in WorksValue.Value)_ |  |
| 19 | 1 | `public double net()` _(in WorksValue.Value)_ |  |
| 21 | 1 | `public double perUsed()` _(in WorksValue.Value)_ | Informational cost per physical unit relieved; reserve-only work has no such denominator. |
| 22 | 11 | `public String words()` _(in WorksValue.Value)_ |  |
| 35 | 4 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, long count, doubl...` |  |
| 41 | 30 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, long count, doubl...` | The same comparison after earlier cards have used some of the treasury. |
| 73 | 3 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, long count, double landLeft)` | A paving is the whole replacement plan, including the ground freed and running-cost difference. |
| 77 | 14 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, long count, double landLeft, doubl...` |  |
| 93 | 2 | **type** `private record Maintenance(BuildingsTemplate template, long count, double landLeft, double cashLeft, double...` | Later works are priced in the comparison and ordered by later monthly passes. |
| 96 | 2 | **type** `private record Relief(double used, double avoided, double futureCapital, long futureUnits, double futureMon...` |  |
| 99 | 1 | **type** `private record Financing(double monthlyCoupon, double issueCost, int months)` |  |
| 101 | 11 | `private static Financing financing(Game g, double gap)` |  |
| 114 | 33 | **type** `private static final class GroundPlan` | Whole parcels retain their unused ground, and the foreign vault pays only once across the sequence. |
| 119 | 4 | `GroundPlan(Game game, BuildingsTemplate template, double available)` _(in WorksValue.GroundPlan)_ |  |
| 124 | 22 | `double take(double area)` _(in WorksValue.GroundPlan)_ |  |
| 148 | 4 | `private static double annuity(double monthlyRate, double months)` |  |
| 154 | 4 | `public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> ord...` | Power at monthly resolution and other services at yearly midpoints, with the same real rate and long city trend. |
| 160 | 98 | `private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> ord...` | Compare every plan to the same date, LIFE_MONTHS from now. |
| 260 | 3 | `public static double pendingPowerLoad(Game g, double months)` | Electrical draw of already ordered non-power works that their present queue opens by this date. |
| 264 | 9 | `private static Map<BuildingsTemplate, Long> pendingLoads(Game g)` |  |
| 274 | 6 | `private static double pendingPowerLoad(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` |  |
| 281 | 1 | **type** `private record PendingWork(long units, double first, double interval, boolean rushed)` |  |
| 284 | 14 | `private static Map<BuildingsTemplate, PendingWork> completionSchedule(Game g, Map<BuildingsTemplate, Long> site)` | The same queue-aware unit quotes the automatic completion deadline reads. |
| 300 | 27 | `private static Map<BuildingsTemplate, Long> completedAt(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` | Quote-completed units at this date. |
| 328 | 3 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> added, BuildAdvice.Ahead at)` |  |
| 332 | 3 | `private static double cover(double[] sd)` |  |
| 336 | 10 | `private static Map<Sector, double[]> traffic(Game g, BuildAdvice.Measure m)` |  |
| 348 | 11 | `static double shortageLoss(Game g, BuildAdvice.Measure m)` | The current monthly output loss that a fully serving available work could relieve. |
| 361 | 58 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> a...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


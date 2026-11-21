# WorksValue.java - 476 lines · 28 methods · 0 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (62), [BuildingsTemplate](BuildingsTemplate.md) (43), [Game](Game.md) (25), [Sector](Sector.md) (10), [DecisionLog](DecisionLog.md) (9), [LandManager](LandManager.md) (9), [NewUnit](NewUnit.md) (6), [CityLand](CityLand.md) (5), [Health](Health.md) (5), [Crime](Crime.md) (4), [AutoBuilder](AutoBuilder.md) (3), [Traffic](Traffic.md) (3), [JobType](JobType.md) (2), [ConstructionControl](ConstructionControl.md) (2), [Zoning](Zoning.md) (2), [Statement](Statement.md) (2), [PopulationManager](PopulationManager.md) (1), [WageBand](WageBand.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingType](BuildingType.md) (1), [BuildingManager](BuildingManager.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (8):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [ChildcareCheck](ChildcareCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [NewUnitCheck](NewUnitCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `private final Game game` |  |
| 130 | `private final java.util.Set<Integer> bought` |  |
| 131 | `private final LandManager.Use use` |  |
| 132 | `private double owned, slope, forest, allocated, works, runs` |  |
| 133 | `private double free, paid` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 466 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
| 12 | 1 | `private WorksValue()` |  |
| 15 | 19 | **type** `public record Value(double capital, double land, double futureCapital, long futureUnits, double financing, ...` | Present values in local thousands; jobs and payroll are shown separately from their tax return. |
| 18 | 1 | `public double cost()` _(in WorksValue.Value)_ |  |
| 19 | 1 | `public double net()` _(in WorksValue.Value)_ |  |
| 21 | 1 | `public double perUsed()` _(in WorksValue.Value)_ | Informational cost per physical unit relieved; reserve-only work has no such denominator. |
| 22 | 11 | `public String words()` _(in WorksValue.Value)_ |  |
| 35 | 4 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, long count, doubl...` |  |
| 41 | 4 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, NewUnit u, long count, double landLeft)` | ...for one new building already resolved (QN1): its own posts; the template it is of is what is ordered. |
| 47 | 4 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, BuildingsTemplate t, long count, doubl...` | The same comparison after earlier cards have used some of the treasury. |
| 53 | 31 | `public static Value of(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, NewUnit u, long count, double landLeft...` | ...for one new building already resolved (QN1). |
| 86 | 3 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, long count, double landLeft)` | A paving is the whole replacement plan, including the ground freed and running-cost difference. |
| 90 | 14 | `public static Value paving(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, long count, double landLeft, doubl...` |  |
| 106 | 2 | **type** `private record Maintenance(BuildingsTemplate template, long count, double landLeft, double cashLeft, double...` | Later works are priced in the comparison and ordered by later monthly passes. |
| 109 | 2 | **type** `private record Relief(double used, double avoided, double futureCapital, long futureUnits, double futureMon...` |  |
| 112 | 1 | **type** `private record Financing(double monthlyCoupon, double issueCost, int months, double proceeds)` |  |
| 114 | 12 | `private static Financing financing(Game g, double gap)` |  |
| 128 | 76 | **type** `private static final class GroundPlan` | Whole parcels retain their unused ground, and the foreign vault pays only once across the sequence. |
| 135 | 13 | `GroundPlan(Game game, BuildingsTemplate template, double available)` _(in WorksValue.GroundPlan)_ |  |
| 150 | 17 | `private double room()` _(in WorksValue.GroundPlan)_ | The office's class and zone constraints on this projected pool, before reservations. |
| 168 | 35 | `double take(double area)` _(in WorksValue.GroundPlan)_ |  |
| 205 | 4 | `private static double annuity(double monthlyRate, double months)` |  |
| 211 | 4 | `public static double avoided(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> ord...` | Power at monthly resolution and other services at yearly midpoints, with the same real rate and long city trend. |
| 217 | 98 | `private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> ord...` | Compare every plan to the same date, LIFE_MONTHS from now. |
| 317 | 3 | `public static double pendingPowerLoad(Game g, double months)` | Electrical draw of already ordered non-power works that their present queue opens by this date. |
| 321 | 9 | `private static Map<BuildingsTemplate, Long> pendingLoads(Game g)` |  |
| 331 | 6 | `private static double pendingPowerLoad(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` |  |
| 338 | 1 | **type** `private record PendingWork(long units, double first, double interval, boolean rushed)` |  |
| 341 | 14 | `private static Map<BuildingsTemplate, PendingWork> completionSchedule(Game g, Map<BuildingsTemplate, Long> site)` | The same queue-aware unit quotes the automatic completion deadline reads. |
| 357 | 27 | `private static Map<BuildingsTemplate, Long> completedAt(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` | Quote-completed units at this date. |
| 385 | 3 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> added, BuildAdvice.Ahead at)` |  |
| 389 | 3 | `private static double cover(double[] sd)` |  |
| 393 | 10 | `private static Map<Sector, double[]> traffic(Game g, BuildAdvice.Measure m)` |  |
| 405 | 11 | `static double shortageLoss(Game g, BuildAdvice.Measure m)` | The current monthly output loss that a fully serving available work could relieve. |
| 418 | 58 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> a...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


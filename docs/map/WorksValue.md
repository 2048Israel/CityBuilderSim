# WorksValue.java - 698 lines · 47 methods · 4 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (65), [BuildingsTemplate](BuildingsTemplate.md) (57), [Game](Game.md) (34), [Sector](Sector.md) (10), [DecisionLog](DecisionLog.md) (9), [LandManager](LandManager.md) (9), [NewUnit](NewUnit.md) (7), [CityLand](CityLand.md) (5), [Health](Health.md) (5), [BuildingManager](BuildingManager.md) (4), [Crime](Crime.md) (4), [AutoBuilder](AutoBuilder.md) (3), [Traffic](Traffic.md) (3), [PopulationManager](PopulationManager.md) (2), [JobType](JobType.md) (2), [ConstructionControl](ConstructionControl.md) (2), [Zoning](Zoning.md) (2), [Statement](Statement.md) (2), [PowerDispatch](PowerDispatch.md) (2), [CityCalendar](CityCalendar.md) (2), [WageBand](WageBand.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingType](BuildingType.md) (1), [LicenceFees](LicenceFees.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (14):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ChildcareCheck](ChildcareCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [LabourCheck](LabourCheck.md), [NewUnitCheck](NewUnitCheck.md), [PopulationManager](PopulationManager.md), [PowerPlanCheck](PowerPlanCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 358 | · HELD FOR ONE PASS (AP1, 2026-11-20) |
| 421 | · THE LABOUR FIGURES HELD FOR ONE PASS (AP5, 2026-11-26) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 394 | `WorksValue.HELD` | `new ThreadLocal<>()` |  |
| 395 | `WorksValue.BARE` | `new ThreadLocal<>()` |  |
| 401 | `WorksValue.SCHEDULES` | `new java.util.concurrent.atomic.AtomicLong()` | Every schedule of the works on site built, held or bare: NewUnitCheck 13's count, never a time. |
| 450 | `WorksValue.LABOUR_COMPARED` | `new java.util.concurrent.atomic.AtomicLong(), LABOUR_DIFFERED = new java.util...` | Held labour figures read and compared while labourChecked was set, and those that differed from the bare figure. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `private final Game game` |  |
| 130 | `private final java.util.Set<Integer> bought` |  |
| 131 | `private final LandManager.Use use` |  |
| 132 | `private double owned, slope, forest, allocated, works, runs` |  |
| 133 | `private double free, paid` |  |
| 371 | `private final Game game` |  |
| 372 | `private int depth` |  |
| 373 | `private java.util.List<Object> stamp` |  |
| 374 | `private Loads loads` |  |
| 376 | `long built` | Schedules built while it was open (NewUnitCheck 13: one with no change on site). |
| 378 | `long[] vacancy, vacancyAt` | The labour figures held while it is open (AP5): the vacancies with every input they were read from, and the stacks' posts. |
| 379 | `BuildingManager.HeldPosts posts` |  |
| 381 | `long vacanciesRead, postsRead` | Vacancies and posts read afresh while it was open (LabourCheck's and AutoBuildCheck's counts, never a time). |
| 448 | `static boolean labourChecked` | Set by a harness (AP5): every held labour figure is also read afresh and compared to the bit, and LABOUR_DIFFERED counts those that were not the same. |
| 548 | `private final Game game` |  |
| 549 | `private final Map<BuildingsTemplate, PendingWork> generation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 11 | 688 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
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
| 217 | 103 | `private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> ord...` | Compare every plan to the same date, LIFE_MONTHS from now. |
| 322 | 6 | `public static double pendingPowerLoad(Game g, double months)` | Electrical draw of already ordered non-power works that their present queue opens by this date. |
| 329 | 3 | `private static Map<BuildingsTemplate, Long> pendingLoads(Game g)` |  |
| 334 | 13 | `private static Map<BuildingsTemplate, Long> pendingLoads(Game g, Map<BuildingsTemplate, Double> draws)` | ...each one's new unit's draw put in `draws` as it is read, when given (the held scope's, AP1). |
| 349 | 8 | `private static double pendingPowerLoad(Game g, Map<BuildingsTemplate, PendingWork> schedule, Map<BuildingsTemplate, Double> dra...` | The draw ahead on a schedule: each template's new unit's draw from `draws` when held (AP1), else read afresh. |

### HELD FOR ONE PASS (AP1, 2026-11-20) (lines 358-420)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 20 | **type** `public static final class Scope implements AutoCloseable` | One open scope: the city it is for, how deep it is opened, the stamp its loads were built at, and the loads. |
| 383 | 1 | `private Scope(Game game)` _(in WorksValue.Scope)_ |  |
| 386 | 3 | `public void close()` _(in WorksValue.Scope)_ | Closes the outermost valuation scope: its loads and labour readings are no longer used. |
| 392 | 1 | **type** `private record Loads(Map<BuildingsTemplate, PendingWork> schedule, Map<BuildingsTemplate, Double> draws)` | The schedule of the non-power works on site and each one's new unit's draw, built together. |
| 398 | 1 | `static boolean bareReading()` | A harness's bare reading also bypasses sector groupings held outside a pass (AP6). |
| 407 | 10 | `public static Scope hold(Game g)` | Opens a scope for this city on this thread, closed by try-with-resources. |
| 419 | 1 | `static Scope open()` | The open scope on this thread, for tests; null when none is. |

### THE LABOUR FIGURES HELD FOR ONE PASS (AP5, 2026-11-26) (lines 421-698)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 4 | `static Scope heldFor(PopulationManager people)` | The scope open on this thread for the city these people are, or null (AP5). |
| 439 | 4 | `static Scope heldFor(BuildingManager buildings)` | The scope open on this thread for the city these buildings are, or null (AP5). |
| 454 | 7 | `static long[] compared(long[] held, java.util.function.Supplier<long[]> bare)` | A held figure against the bare one, when a harness has asked (labourChecked); returns the held one. |
| 463 | 7 | `static long compared(long held, java.util.function.LongSupplier bare)` | ...one long. |
| 472 | 8 | `static double compared(double held, java.util.function.DoubleSupplier bare)` | ...one double, to the bit. |
| 482 | 12 | `static<T> T bareWhile(java.util.function.Supplier<T> reading)` | An original walk without held figures or groups (AP5/AP6); restores the scope and bare mode even on failure. |
| 499 | 16 | `private static Loads held(Game g)` | The held loads for this city, rebuilt when the site's stamp has moved; null when no scope for it is open, or inside a pricing that reads every template as it stands (NewUnit.of() reads the template there, not the new ... |
| 517 | 4 | `private static Map<BuildingsTemplate, PendingWork> loadSchedule(Game g)` | The schedule of the non-power works on site, as it stands: built afresh, outside a scope. |
| 522 | 1 | **type** `private record PendingWork(long units, double first, double interval, boolean rushed)` |  |
| 525 | 14 | `private static Map<BuildingsTemplate, PendingWork> completionSchedule(Game g, Map<BuildingsTemplate, Long> site)` | The same queue-aware unit quotes the automatic completion deadline reads. |
| 541 | 4 | `public static Map<BuildingsTemplate, Long> openBy(Game g, Map<BuildingsTemplate, Long> site, double months)` | The units of a site its queue opens by `months` from now (PW3, PW1-P's second gap: the power planner's reserve test counts a plant from its opening, not from its order). |
| 547 | 30 | **type** `static final class PowerDates` | Read-only generation schedule and native monthly planning dates; pending loads retain their existing held reader. |
| 550 | 4 | `PowerDates(Game game, Map<BuildingsTemplate, Long> site)` _(in WorksValue.PowerDates)_ |  |
| 555 | 3 | `Map<BuildingsTemplate, Long> openBy(double months)` _(in WorksValue.PowerDates)_ |  |
| 560 | 8 | `int nextPeak(double from)` _(in WorksValue.PowerDates)_ | The next native highest-load calendar month at or after this monthly commissioning. |
| 570 | 6 | `java.util.SortedSet<Double> frontier(double from)` _(in WorksValue.PowerDates)_ | Construction runs before dispatch: both generation and new load use the same completed whole month. |
| 579 | 27 | `private static Map<BuildingsTemplate, Long> completedAt(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` | Quote-completed units at this date. |
| 607 | 3 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> added, BuildAdvice.Ahead at)` |  |
| 611 | 3 | `private static double cover(double[] sd)` |  |
| 615 | 10 | `private static Map<Sector, double[]> traffic(Game g, BuildAdvice.Measure m)` |  |
| 627 | 11 | `static double shortageLoss(Game g, BuildAdvice.Measure m)` | The current monthly output loss that a fully serving available work could relieve. |
| 640 | 58 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> a...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


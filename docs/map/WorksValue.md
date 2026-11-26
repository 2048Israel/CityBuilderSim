# WorksValue.java - 690 lines · 46 methods · 3 constants · model

`ham/citybuildersim/WorksValue.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> A work's discounted cost and the output its service prevents losing (HW1).
> WHY. A plant's cheap used kW hid its land and a shortage's lost work. These
> are comparisons at today's prices, never entries in the city's money books.

**Uses:** [BuildAdvice](BuildAdvice.md) (63), [BuildingsTemplate](BuildingsTemplate.md) (57), [Game](Game.md) (34), [Sector](Sector.md) (10), [DecisionLog](DecisionLog.md) (9), [LandManager](LandManager.md) (9), [NewUnit](NewUnit.md) (7), [CityLand](CityLand.md) (5), [Health](Health.md) (5), [BuildingManager](BuildingManager.md) (4), [Crime](Crime.md) (4), [AutoBuilder](AutoBuilder.md) (3), [Traffic](Traffic.md) (3), [PopulationManager](PopulationManager.md) (2), [JobType](JobType.md) (2), [ConstructionControl](ConstructionControl.md) (2), [Zoning](Zoning.md) (2), [Statement](Statement.md) (2), [PowerDispatch](PowerDispatch.md) (2), [CityCalendar](CityCalendar.md) (2), [WageBand](WageBand.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingType](BuildingType.md) (1), [LicenceFees](LicenceFees.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (11):** [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ChildcareCheck](ChildcareCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [NewUnitCheck](NewUnitCheck.md), [PopulationManager](PopulationManager.md), [PowerPlanCheck](PowerPlanCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 356 | · HELD FOR ONE PASS (AP1, 2026-11-20) |
| 415 | · THE LABOUR FIGURES HELD FOR ONE PASS (AP5, 2026-11-26) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 392 | `WorksValue.HELD` | `new ThreadLocal<>()` |  |
| 395 | `WorksValue.SCHEDULES` | `new java.util.concurrent.atomic.AtomicLong()` | Every schedule of the works on site built, held or bare: NewUnitCheck 13's count, never a time. |
| 444 | `WorksValue.LABOUR_COMPARED` | `new java.util.concurrent.atomic.AtomicLong(), LABOUR_DIFFERED = new java.util...` | Held labour figures read and compared while labourChecked was set, and those that differed from the bare figure. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `private final Game game` |  |
| 130 | `private final java.util.Set<Integer> bought` |  |
| 131 | `private final LandManager.Use use` |  |
| 132 | `private double owned, slope, forest, allocated, works, runs` |  |
| 133 | `private double free, paid` |  |
| 369 | `private final Game game` |  |
| 370 | `private int depth` |  |
| 371 | `private java.util.List<Object> stamp` |  |
| 372 | `private Loads loads` |  |
| 374 | `long built` | Schedules built while it was open (NewUnitCheck 13: one with no change on site). |
| 376 | `long[] vacancy, vacancyAt` | The labour figures held while it is open (AP5): the vacancies with every input they were read from, and the stacks' posts. |
| 377 | `BuildingManager.HeldPosts posts` |  |
| 379 | `long vacanciesRead, postsRead` | Vacancies and posts read afresh while it was open (LabourCheck's and AutoBuildCheck's counts, never a time). |
| 442 | `static boolean labourChecked` | Set by a harness (AP5): every held labour figure is also read afresh and compared to the bit, and LABOUR_DIFFERED counts those that were not the same. |
| 540 | `private final Game game` |  |
| 541 | `private final Map<BuildingsTemplate, PendingWork> generation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 11 | 680 | **type** `public final class WorksValue` | A work's discounted cost and the output its service prevents losing (HW1). |
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
| 217 | 101 | `private static Relief relief(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate, Long> ord...` | Compare every plan to the same date, LIFE_MONTHS from now. |
| 320 | 6 | `public static double pendingPowerLoad(Game g, double months)` | Electrical draw of already ordered non-power works that their present queue opens by this date. |
| 327 | 3 | `private static Map<BuildingsTemplate, Long> pendingLoads(Game g)` |  |
| 332 | 13 | `private static Map<BuildingsTemplate, Long> pendingLoads(Game g, Map<BuildingsTemplate, Double> draws)` | ...each one's new unit's draw put in `draws` as it is read, when given (the held scope's, AP1). |
| 347 | 8 | `private static double pendingPowerLoad(Game g, Map<BuildingsTemplate, PendingWork> schedule, Map<BuildingsTemplate, Double> dra...` | The draw ahead on a schedule: each template's new unit's draw from `draws` when held (AP1), else read afresh. |

### HELD FOR ONE PASS (AP1, 2026-11-20) (lines 356-414)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 20 | **type** `public static final class Scope implements AutoCloseable` | One open scope: the city it is for, how deep it is opened, the stamp its loads were built at, and the loads. |
| 381 | 1 | `private Scope(Game game)` _(in WorksValue.Scope)_ |  |
| 384 | 3 | `public void close()` _(in WorksValue.Scope)_ | Closes it at its outermost close: nothing is held after. |
| 390 | 1 | **type** `private record Loads(Map<BuildingsTemplate, PendingWork> schedule, Map<BuildingsTemplate, Double> draws)` | The schedule of the non-power works on site and each one's new unit's draw, built together. |
| 401 | 10 | `public static Scope hold(Game g)` | Opens a scope for this city on this thread, closed by try-with-resources. |
| 413 | 1 | `static Scope open()` | The open scope on this thread, for tests; null when none is. |

### THE LABOUR FIGURES HELD FOR ONE PASS (AP5, 2026-11-26) (lines 415-690)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 4 | `static Scope heldFor(PopulationManager people)` | The scope open on this thread for the city these people are, or null (AP5). |
| 433 | 4 | `static Scope heldFor(BuildingManager buildings)` | The scope open on this thread for the city these buildings are, or null (AP5). |
| 448 | 7 | `static long[] compared(long[] held, java.util.function.Supplier<long[]> bare)` | A held figure against the bare one, when a harness has asked (labourChecked); returns the held one. |
| 457 | 7 | `static long compared(long held, java.util.function.LongSupplier bare)` | ...one long. |
| 466 | 8 | `static double compared(double held, java.util.function.DoubleSupplier bare)` | ...one double, to the bit. |
| 476 | 10 | `static<T> T bareWhile(java.util.function.Supplier<T> reading)` | A reading taken as no scope were open on this thread (AP5: the bare figure a held one is compared with); the scope is open again after. |
| 491 | 16 | `private static Loads held(Game g)` | The held loads for this city, rebuilt when the site's stamp has moved; null when no scope for it is open, or inside a pricing that reads every template as it stands (NewUnit.of() reads the template there, not the new ... |
| 509 | 4 | `private static Map<BuildingsTemplate, PendingWork> loadSchedule(Game g)` | The schedule of the non-power works on site, as it stands: built afresh, outside a scope. |
| 514 | 1 | **type** `private record PendingWork(long units, double first, double interval, boolean rushed)` |  |
| 517 | 14 | `private static Map<BuildingsTemplate, PendingWork> completionSchedule(Game g, Map<BuildingsTemplate, Long> site)` | The same queue-aware unit quotes the automatic completion deadline reads. |
| 533 | 4 | `public static Map<BuildingsTemplate, Long> openBy(Game g, Map<BuildingsTemplate, Long> site, double months)` | The units of a site its queue opens by `months` from now (PW3, PW1-P's second gap: the power planner's reserve test counts a plant from its opening, not from its order). |
| 539 | 30 | **type** `static final class PowerDates` | Read-only generation schedule and native monthly planning dates; pending loads retain their existing held reader. |
| 542 | 4 | `PowerDates(Game game, Map<BuildingsTemplate, Long> site)` _(in WorksValue.PowerDates)_ |  |
| 547 | 3 | `Map<BuildingsTemplate, Long> openBy(double months)` _(in WorksValue.PowerDates)_ |  |
| 552 | 8 | `int nextPeak(double from)` _(in WorksValue.PowerDates)_ | The next native highest-load calendar month at or after this monthly commissioning. |
| 562 | 6 | `java.util.SortedSet<Double> frontier(double from)` _(in WorksValue.PowerDates)_ | Construction runs before dispatch: both generation and new load use the same completed whole month. |
| 571 | 27 | `private static Map<BuildingsTemplate, Long> completedAt(Game g, Map<BuildingsTemplate, PendingWork> schedule, double months)` | Quote-completed units at this date. |
| 599 | 3 | `static double cover(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> added, BuildAdvice.Ahead at)` |  |
| 603 | 3 | `private static double cover(double[] sd)` |  |
| 607 | 10 | `private static Map<Sector, double[]> traffic(Game g, BuildAdvice.Measure m)` |  |
| 619 | 11 | `static double shortageLoss(Game g, BuildAdvice.Measure m)` | The current monthly output loss that a fully serving available work could relieve. |
| 632 | 58 | `static double outputRestored(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> a...` | Only effects that the simulation actually charges: utility rationing, traffic and absence. |


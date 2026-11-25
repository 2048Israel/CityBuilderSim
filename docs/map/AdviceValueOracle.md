# AdviceValueOracle.java - 368 lines · 14 methods · 0 constants · model

`ham/citybuildersim/AdviceValueOracle.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Independent discounted arithmetic for BuildAdviceCheck's complete-plan law.
> WHY. Reading the value object back through its own factory could never find
> an omitted cost. This fixture helper reads quotes, stocks and physical model
> inputs, and computes the cash flows without calling WorksValue.

**Uses:** [BuildAdvice](BuildAdvice.md) (47), [BuildingsTemplate](BuildingsTemplate.md) (34), [Game](Game.md) (18), [LandManager](LandManager.md) (9), [BuildingType](BuildingType.md) (7), [CityLand](CityLand.md) (5), [Health](Health.md) (5), [JobType](JobType.md) (4), [Sector](Sector.md) (4), [Crime](Crime.md) (4), [Traffic](Traffic.md) (3), [Statement](Statement.md) (2), [Zoning](Zoning.md) (2), [AutoBuilder](AutoBuilder.md) (2), [ConstructionControl](ConstructionControl.md) (2), [WageBand](WageBand.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [BuildCard](BuildCard.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (2):** [BuildAdviceCheck](BuildAdviceCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 201 | `final Game game` |  |
| 202 | `final Set<Integer> parcels` |  |
| 203 | `final LandManager.Use use` |  |
| 204 | `double dry, steep, forest, taken, works, runs` |  |
| 205 | `double free, paid` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 353 | **type** `final class AdviceValueOracle` | Independent discounted arithmetic for BuildAdviceCheck's complete-plan law. |
| 17 | 1 | `private AdviceValueOracle()` |  |
| 19 | 99 | `static double[] value(Game g, BuildAdvice.Suggestion s, double land, double treasury)` |  |
| 119 | 12 | `private static boolean maintained(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> stock, BuildAdvice.Ahead at, Map<...` |  |
| 132 | 16 | `private static long powerCount(Game g, Map<BuildingsTemplate, Long> stock, BuildingsTemplate t, BuildAdvice.Ahead at, long limi...` |  |
| 150 | 29 | `private static double[] powerService(Game g, Map<BuildingsTemplate, Long> stock, BuildAdvice.Ahead at, Map<BuildingsTemplate, L...` | Independent utility pooling and pending electrical draw, including the demand used by sizing. |
| 181 | 18 | `private static double[] credit(Game g, double gap, double discount, double months)` | Present cost and cash actually raised, including the quoted granule's excess. |
| 200 | 70 | **type** `private static final class Ground` |  |
| 206 | 14 | `Ground(Game g, BuildingsTemplate t, double free)` _(in AdviceValueOracle.Ground)_ |  |
| 220 | 16 | `private double available()` _(in AdviceValueOracle.Ground)_ |  |
| 236 | 33 | `double use(double area)` _(in AdviceValueOracle.Ground)_ |  |
| 271 | 12 | `private static Map<BuildingsTemplate, Long> ready(Game g, Map<BuildingsTemplate, Long> orders, double month)` |  |
| 284 | 9 | `private static double gross(Game g, BuildingsTemplate t, double use)` |  |
| 294 | 16 | `private static double running(Game g, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> after, BuildAdvice.Ahea...` |  |
| 311 | 3 | `private static double fraction(double[] service)` |  |
| 315 | 53 | `private static double restored(Game g, BuildAdvice.Measure measure, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate,...` |  |


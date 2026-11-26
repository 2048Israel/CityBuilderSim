# AdviceValueOracle.java - 388 lines · 15 methods · 0 constants · model

`ham/citybuildersim/AdviceValueOracle.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> Independent discounted arithmetic for BuildAdviceCheck's complete-plan law.
> WHY. Reading the value object back through its own factory could never find
> an omitted cost. This fixture helper reads quotes, stocks and physical model
> inputs, and computes the cash flows without calling WorksValue.

**Uses:** [BuildAdvice](BuildAdvice.md) (47), [BuildingsTemplate](BuildingsTemplate.md) (39), [Game](Game.md) (18), [LandManager](LandManager.md) (9), [NewUnit](NewUnit.md) (7), [CityLand](CityLand.md) (5), [Health](Health.md) (5), [JobType](JobType.md) (4), [Sector](Sector.md) (4), [BuildingType](BuildingType.md) (4), [PowerDispatch](PowerDispatch.md) (4), [Crime](Crime.md) (4), [BuildingsStacks](BuildingsStacks.md) (3), [Traffic](Traffic.md) (3), [Statement](Statement.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [Zoning](Zoning.md) (2), [AutoBuilder](AutoBuilder.md) (2), [ConstructionControl](ConstructionControl.md) (2), [WageBand](WageBand.md) (1), [BuildingManager](BuildingManager.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [BuildCard](BuildCard.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (3):** [BuildAdviceCheck](BuildAdviceCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 221 | `final Game game` |  |
| 222 | `final Set<Integer> parcels` |  |
| 223 | `final LandManager.Use use` |  |
| 224 | `double dry, steep, forest, taken, works, runs` |  |
| 225 | `double free, paid` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 373 | **type** `final class AdviceValueOracle` | Independent discounted arithmetic for BuildAdviceCheck's complete-plan law. |
| 17 | 1 | `private AdviceValueOracle()` |  |
| 19 | 101 | `static double[] value(Game g, BuildAdvice.Suggestion s, double land, double treasury)` |  |
| 121 | 12 | `private static boolean maintained(Game g, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> stock, BuildAdvice.Ahead at, Map<...` |  |
| 134 | 16 | `private static long powerCount(Game g, Map<BuildingsTemplate, Long> stock, BuildingsTemplate t, BuildAdvice.Ahead at, long limi...` |  |
| 152 | 34 | `static double[] powerService(Game g, Map<BuildingsTemplate, Long> stock, BuildAdvice.Ahead at, Map<BuildingsTemplate, Long> loads)` | Independent firm supply and pending draw: the founding grid has no posts; each plant has its own (REV-PW9). |
| 187 | 12 | `private static double firm(UtilitiesHandler utility, BuildingsTemplate t, NewUnit unit, long count, double[] fill, double share)` |  |
| 201 | 18 | `private static double[] credit(Game g, double gap, double discount, double months)` | Present cost and cash actually raised, including the quoted granule's excess. |
| 220 | 70 | **type** `private static final class Ground` |  |
| 226 | 14 | `Ground(Game g, BuildingsTemplate t, double free)` _(in AdviceValueOracle.Ground)_ |  |
| 240 | 16 | `private double available()` _(in AdviceValueOracle.Ground)_ |  |
| 256 | 33 | `double use(double area)` _(in AdviceValueOracle.Ground)_ |  |
| 291 | 12 | `private static Map<BuildingsTemplate, Long> ready(Game g, Map<BuildingsTemplate, Long> orders, double month)` |  |
| 304 | 9 | `private static double gross(Game g, BuildingsTemplate t, double use)` |  |
| 314 | 16 | `private static double running(Game g, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate, Long> after, BuildAdvice.Ahea...` |  |
| 331 | 3 | `private static double fraction(double[] service)` |  |
| 335 | 53 | `private static double restored(Game g, BuildAdvice.Measure measure, Map<BuildingsTemplate, Long> before, Map<BuildingsTemplate,...` |  |


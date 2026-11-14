# AdviceValueOracle.java - 257 lines · 10 methods · 0 constants · model

`ham/citybuildersim/AdviceValueOracle.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Independent discounted arithmetic for BuildAdviceCheck's complete-plan law.
> WHY. Reading the value object back through its own factory could never find
> an omitted cost. This fixture helper reads quotes, stocks and physical model
> inputs, and computes the cash flows without calling WorksValue.

**Uses:** [BuildAdvice](BuildAdvice.md) (39), [BuildingsTemplate](BuildingsTemplate.md) (22), [Game](Game.md) (14), [Health](Health.md) (5), [Sector](Sector.md) (4), [Crime](Crime.md) (4), [Traffic](Traffic.md) (3), [JobType](JobType.md) (2), [Statement](Statement.md) (2), [AutoBuilder](AutoBuilder.md) (2), [ConstructionControl](ConstructionControl.md) (2), [WageBand](WageBand.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [BuildingType](BuildingType.md) (1), [DebtQuote](DebtQuote.md) (1), [LandParcel](LandParcel.md) (1), [LandManager](LandManager.md) (1), [BuildCard](BuildCard.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [CareType](CareType.md) (1)

**Used by (2):** [BuildAdviceCheck](BuildAdviceCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 130 | `final Game game` |  |
| 131 | `final Set<Integer> parcels` |  |
| 132 | `double free, paid` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 242 | **type** `final class AdviceValueOracle` | Independent discounted arithmetic for BuildAdviceCheck's complete-plan law. |
| 17 | 1 | `private AdviceValueOracle()` |  |
| 19 | 91 | `static double[] value(Game g, BuildAdvice.Suggestion s, double land, double treasury)` |  |
| 111 | 17 | `private static double credit(Game g, double gap, double discount, double months)` |  |
| 129 | 30 | **type** `private static final class Ground` |  |
| 133 | 4 | `Ground(Game g, BuildingsTemplate t, double free)` _(in AdviceValueOracle.Ground)_ |  |
| 137 | 21 | `double use(double area)` _(in AdviceValueOracle.Ground)_ |  |
| 160 | 12 | `private static Map<BuildingsTemplate, Integer> ready(Game g, Map<BuildingsTemplate, Integer> orders, double month)` |  |
| 173 | 9 | `private static double gross(Game g, BuildingsTemplate t, double use)` |  |
| 183 | 16 | `private static double running(Game g, Map<BuildingsTemplate, Integer> before, Map<BuildingsTemplate, Integer> after, BuildAdvic...` |  |
| 200 | 3 | `private static double fraction(double[] service)` |  |
| 204 | 53 | `private static double restored(Game g, BuildAdvice.Measure measure, Map<BuildingsTemplate, Integer> before, Map<BuildingsTempla...` |  |


# EssentialWorksCheck.java - 685 lines · 21 methods · 3 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Essential works ordered for their unmet service, including their ground (HW1).
> WHY. Highways waited behind street sites at the twelve-month cap; schools
> and police waited for a payback that the service itself must never face.
> Fixtures cause short highways with and without ground, a street on site,
> a partial power order from scarce cash, and schools that cannot pay their upkeep.

**Uses:** [BuildAdvice](BuildAdvice.md) (135), [AutoBuilder](AutoBuilder.md) (34), [BuildingsTemplate](BuildingsTemplate.md) (27), [Game](Game.md) (23), [WorksValue](WorksValue.md) (15), [LandManager](LandManager.md) (9), [CityNeeds](CityNeeds.md) (9), [UtilitiesHandler](UtilitiesHandler.md) (9), [Health](Health.md) (8), [BuildingManager](BuildingManager.md) (7), [Sector](Sector.md) (5), [CareType](CareType.md) (5), [ConstructionControl](ConstructionControl.md) (5), [ChildcareCheck](ChildcareCheck.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [EducationType](EducationType.md) (3), [JobType](JobType.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [BuildCard](BuildCard.md) (2), [PopulationManager](PopulationManager.md) (2), [WageBand](WageBand.md) (2), [Traffic](Traffic.md) (2), [GameFiles](GameFiles.md) (2), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1), [Education](Education.md) (1), [Healthcare](Healthcare.md) (1), [Statement](Statement.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [TempDirs](TempDirs.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 18 | `EssentialWorksCheck.out` | `System.out` |  |
| 19 | `EssentialWorksCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 20 | `EssentialWorksCheck.HIGHWAYS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 670 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
| 22 | 4 | `static void check(String label, boolean ok)` |  |
| 27 | 5 | `static void quietly(Runnable run)` |  |
| 33 | 9 | `static Game town(Path root, String name)` |  |
| 43 | 3 | `static AutoBuilder.Step step(Game g, BuildAdvice.Measure m)` |  |
| 47 | 25 | `static void highways(Path root, boolean ground)` |  |
| 73 | 42 | `static void anticipateHighway(Path root)` |  |
| 116 | 33 | `static void schoolsAndPolice(Path root)` |  |
| 150 | 31 | `static void lostOutput(Path root)` |  |
| 182 | 28 | `static void scarceCash(Path root)` |  |
| 211 | 40 | `static void professionalDemand(Path root)` |  |
| 252 | 24 | `static void filledPostCosts(Path root)` |  |
| 277 | 44 | `static void cappedCare(Path root)` |  |
| 322 | 35 | `static void adviceParity(Path root)` |  |
| 358 | 52 | `static void interimPower(Path root)` |  |
| 411 | 35 | `static void waterAtCap(Path root)` |  |
| 447 | 38 | `static void queuedPower(Path root)` |  |
| 486 | 34 | `static void shortPowerBenefit(Path root)` |  |
| 521 | 40 | `static void pavingLead(Path root)` |  |
| 562 | 82 | `static void overduePending(Path root) throws Exception` |  |
| 645 | 18 | `static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first, java.util.List<String> orders, bool...` |  |
| 664 | 21 | `public static void main(String[] args) throws Exception` |  |


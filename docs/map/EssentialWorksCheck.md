# EssentialWorksCheck.java - 770 lines · 23 methods · 3 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Essential works ordered for their unmet service, including their ground (HW1).
> WHY. Highways waited behind street sites at the twelve-month cap; schools
> and police waited for a payback that the service itself must never face.
> Fixtures cause short highways with and without ground, a street on site,
> a partial power order from scarce cash, and schools that cannot pay their upkeep.

**Uses:** [BuildAdvice](BuildAdvice.md) (161), [AutoBuilder](AutoBuilder.md) (36), [BuildingsTemplate](BuildingsTemplate.md) (32), [Game](Game.md) (25), [WorksValue](WorksValue.md) (16), [LandManager](LandManager.md) (11), [UtilitiesHandler](UtilitiesHandler.md) (10), [CityNeeds](CityNeeds.md) (9), [BuildingManager](BuildingManager.md) (9), [Health](Health.md) (8), [CareType](CareType.md) (6), [Sector](Sector.md) (5), [ConstructionControl](ConstructionControl.md) (5), [BuildingsStacks](BuildingsStacks.md) (4), [ChildcareCheck](ChildcareCheck.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [EducationType](EducationType.md) (3), [JobType](JobType.md) (3), [BuildCard](BuildCard.md) (2), [PopulationManager](PopulationManager.md) (2), [WageBand](WageBand.md) (2), [Traffic](Traffic.md) (2), [GameFiles](GameFiles.md) (2), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1), [Education](Education.md) (1), [Healthcare](Healthcare.md) (1), [Statement](Statement.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [TempDirs](TempDirs.md) (1)

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
| 16 | 755 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
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
| 252 | 38 | `static void pendingIndustrialLoad(Path root)` |  |
| 291 | 39 | `static void interimCare(Path root)` |  |
| 331 | 24 | `static void filledPostCosts(Path root)` |  |
| 356 | 44 | `static void cappedCare(Path root)` |  |
| 401 | 37 | `static void adviceParity(Path root)` |  |
| 439 | 52 | `static void interimPower(Path root)` |  |
| 492 | 35 | `static void waterAtCap(Path root)` |  |
| 528 | 38 | `static void queuedPower(Path root)` |  |
| 567 | 34 | `static void shortPowerBenefit(Path root)` |  |
| 602 | 40 | `static void pavingLead(Path root)` |  |
| 643 | 82 | `static void overduePending(Path root) throws Exception` |  |
| 726 | 20 | `static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first, java.util.List<String> orders, bool...` |  |
| 747 | 23 | `public static void main(String[] args) throws Exception` |  |


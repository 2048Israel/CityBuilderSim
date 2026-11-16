# EssentialWorksCheck.java - 1,042 lines · 30 methods · 3 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-16 by CodeMap; line numbers are as of that run.

> Essential works ordered for their unmet service, including their ground (HW1).
> WHY. Highways waited behind street sites at the twelve-month cap; schools
> and police waited for a payback that the service itself must never face.
> Fixtures cause short highways with and without ground, a street on site,
> a partial power order from scarce cash, and schools that cannot pay their upkeep.

**Uses:** [BuildAdvice](BuildAdvice.md) (231), [AutoBuilder](AutoBuilder.md) (52), [BuildingsTemplate](BuildingsTemplate.md) (44), [Game](Game.md) (35), [WorksValue](WorksValue.md) (23), [LandManager](LandManager.md) (16), [CityNeeds](CityNeeds.md) (15), [UtilitiesHandler](UtilitiesHandler.md) (12), [BuildingManager](BuildingManager.md) (11), [CareType](CareType.md) (8), [Health](Health.md) (8), [InfrastructureManager](InfrastructureManager.md) (5), [Sector](Sector.md) (5), [Traffic](Traffic.md) (5), [ConstructionControl](ConstructionControl.md) (5), [EducationType](EducationType.md) (4), [BuildingsStacks](BuildingsStacks.md) (4), [ChildcareCheck](ChildcareCheck.md) (3), [JobType](JobType.md) (3), [BuildCard](BuildCard.md) (2), [PopulationManager](PopulationManager.md) (2), [WageBand](WageBand.md) (2), [GameFiles](GameFiles.md) (2), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1), [Education](Education.md) (1), [Healthcare](Healthcare.md) (1), [Statement](Statement.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [TempDirs](TempDirs.md) (1)

**Used by (1):** [WorksFinanceCheck](WorksFinanceCheck.md)

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
| 16 | 1027 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
| 22 | 4 | `static void check(String label, boolean ok)` |  |
| 27 | 5 | `static void quietly(Runnable run)` |  |
| 33 | 9 | `static Game town(Path root, String name)` |  |
| 43 | 3 | `static AutoBuilder.Step step(Game g, BuildAdvice.Measure m)` |  |
| 47 | 25 | `static void highways(Path root, boolean ground)` |  |
| 74 | 21 | `static boolean serveOtherEssentials(Game g, BuildAdvice.Measure except)` | Give unrelated essential services their operating capacity before isolating an ahead-of-need order. |
| 96 | 50 | `static void anticipateHighway(Path root)` |  |
| 147 | 33 | `static void schoolsAndPolice(Path root)` |  |
| 181 | 34 | `static void lostOutput(Path root)` |  |
| 216 | 13 | `static void serveStreets(Game g)` |  |
| 230 | 29 | `static void scarceCash(Path root)` |  |
| 260 | 40 | `static void professionalDemand(Path root)` |  |
| 301 | 40 | `static void pendingIndustrialLoad(Path root)` |  |
| 342 | 39 | `static void interimCare(Path root)` |  |
| 382 | 33 | `static void stagedCosts(Path root)` |  |
| 416 | 42 | `static void completeRunning(Path root)` |  |
| 459 | 10 | `static double existingWear(Game g, Map<BuildingsTemplate, Integer> order)` |  |
| 470 | 27 | `static void currentLossPriority(Path root)` |  |
| 498 | 88 | `static void fundingPriority(Path root, boolean spareCare) throws Exception` |  |
| 587 | 27 | `static void filledPostCosts(Path root)` |  |
| 615 | 44 | `static void cappedCare(Path root)` |  |
| 660 | 38 | `static void adviceParity(Path root)` |  |
| 699 | 56 | `static void interimPower(Path root)` |  |
| 756 | 35 | `static void waterAtCap(Path root)` |  |
| 792 | 38 | `static void queuedPower(Path root)` |  |
| 831 | 37 | `static void shortPowerBenefit(Path root)` |  |
| 869 | 40 | `static void pavingLead(Path root)` |  |
| 910 | 82 | `static void overduePending(Path root) throws Exception` |  |
| 993 | 20 | `static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first, java.util.List<String> orders, bool...` |  |
| 1014 | 28 | `public static void main(String[] args) throws Exception` |  |


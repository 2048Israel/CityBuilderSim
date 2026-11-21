# EssentialWorksCheck.java - 1,124 lines · 33 methods · 4 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> Essential works ordered for their unmet service, including their ground (HW1).
> WHY. Highways waited behind street sites at the twelve-month cap; schools
> and police waited for a payback that the service itself must never face.
> Fixtures cause short highways with and without ground, a street on site,
> a partial power order from scarce cash, and schools that cannot pay their upkeep.
> The town is ChildcareCheck.town() at x1 (played 36 months) with a bond and no debt
> room, town() here; what a section reads off it is set there by hand. Since FS15
> the premises that sat on the played town's own figures are caused: the highway
> forecast's traffic scaled between its two edges (mostCovered()), the streets'
> share set (SERVED_STREETS), spare care by the people scaled to it (scalePeople()),
> and the staff-short plant by the workforce set to half the posts.

**Uses:** [BuildAdvice](BuildAdvice.md) (237), [AutoBuilder](AutoBuilder.md) (52), [BuildingsTemplate](BuildingsTemplate.md) (46), [Game](Game.md) (38), [WorksValue](WorksValue.md) (23), [LandManager](LandManager.md) (16), [CityNeeds](CityNeeds.md) (15), [UtilitiesHandler](UtilitiesHandler.md) (12), [BuildingManager](BuildingManager.md) (11), [Traffic](Traffic.md) (11), [CareType](CareType.md) (8), [Health](Health.md) (8), [InfrastructureManager](InfrastructureManager.md) (7), [Sector](Sector.md) (5), [ConstructionControl](ConstructionControl.md) (5), [EducationType](EducationType.md) (4), [BuildingsStacks](BuildingsStacks.md) (4), [ChildcareCheck](ChildcareCheck.md) (3), [PopulationManager](PopulationManager.md) (3), [JobType](JobType.md) (3), [BuildCard](BuildCard.md) (2), [WageBand](WageBand.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [GameFiles](GameFiles.md) (2), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1), [Education](Education.md) (1), [AgeBand](AgeBand.md) (1), [Healthcare](Healthcare.md) (1), [Statement](Statement.md) (1)... and 2 more

**Used by (1):** [WorksFinanceCheck](WorksFinanceCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `EssentialWorksCheck.out` | `System.out` |  |
| 25 | `EssentialWorksCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 26 | `EssentialWorksCheck.HIGHWAYS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS)` |  |
| 508 | `EssentialWorksCheck.SERVED_STREETS` | `.5` | The share of their traffic currentLossPriority()'s streets serve: under full, over power's quarter (FS15). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 1103 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
| 28 | 4 | `static void check(String label, boolean ok)` |  |
| 33 | 5 | `static void quietly(Runnable run)` |  |
| 39 | 9 | `static Game town(Path root, String name)` |  |
| 49 | 3 | `static AutoBuilder.Step step(Game g, BuildAdvice.Measure m)` |  |
| 53 | 25 | `static void highways(Path root, boolean ground)` |  |
| 80 | 21 | `static boolean serveOtherEssentials(Game g, BuildAdvice.Measure except)` | Give unrelated essential services their operating capacity before isolating an ahead-of-need order. |
| 105 | 9 | `static void carry(Game g, BuildingsTemplate highway, double load, double[] streams, double f)` | The town carrying its traffic as it stood times `f`, every stream alike, and streets built past it: the forecast order has no unrelated unserved street whose construction promise would take precedence. |
| 117 | 11 | `static double mostCovered(Game g, BuildingsTemplate highway, double load, double[] streams, BuildAdvice.Ahead p)` | The most of its traffic, as a multiple, at which the town's highways are maintained under `p`, by bisection (FS15). |
| 129 | 54 | `static void anticipateHighway(Path root)` |  |
| 184 | 33 | `static void schoolsAndPolice(Path root)` |  |
| 218 | 34 | `static void lostOutput(Path root)` |  |
| 253 | 13 | `static void serveStreets(Game g)` |  |
| 267 | 29 | `static void scarceCash(Path root)` |  |
| 297 | 40 | `static void professionalDemand(Path root)` |  |
| 338 | 40 | `static void pendingIndustrialLoad(Path root)` |  |
| 379 | 39 | `static void interimCare(Path root)` |  |
| 419 | 33 | `static void stagedCosts(Path root)` |  |
| 453 | 42 | `static void completeRunning(Path root)` |  |
| 496 | 10 | `static double existingWear(Game g, Map<BuildingsTemplate, Long> order)` |  |
| 510 | 39 | `static void currentLossPriority(Path root)` |  |
| 552 | 13 | `static void scalePeople(Game g, double f)` | Every band of the town's people, and its two adult stages, times `f`, its workforce held (FS15): a demand moved without moving the staffing of the works that serve it. |
| 566 | 100 | `static void fundingPriority(Path root, boolean spareCare) throws Exception` |  |
| 667 | 29 | `static void filledPostCosts(Path root)` |  |
| 697 | 44 | `static void cappedCare(Path root)` |  |
| 742 | 38 | `static void adviceParity(Path root)` |  |
| 781 | 56 | `static void interimPower(Path root)` |  |
| 838 | 35 | `static void waterAtCap(Path root)` |  |
| 874 | 38 | `static void queuedPower(Path root)` |  |
| 913 | 37 | `static void shortPowerBenefit(Path root)` |  |
| 951 | 40 | `static void pavingLead(Path root)` |  |
| 992 | 82 | `static void overduePending(Path root) throws Exception` |  |
| 1075 | 20 | `static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first, java.util.List<String> orders, bool...` |  |
| 1096 | 28 | `public static void main(String[] args) throws Exception` |  |


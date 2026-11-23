# EssentialWorksCheck.java - 1,172 lines · 35 methods · 4 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildAdvice](BuildAdvice.md) (250), [AutoBuilder](AutoBuilder.md) (52), [BuildingsTemplate](BuildingsTemplate.md) (46), [Game](Game.md) (40), [WorksValue](WorksValue.md) (23), [LandManager](LandManager.md) (16), [CityNeeds](CityNeeds.md) (15), [UtilitiesHandler](UtilitiesHandler.md) (13), [BuildingManager](BuildingManager.md) (12), [Traffic](Traffic.md) (11), [CareType](CareType.md) (8), [Health](Health.md) (8), [InfrastructureManager](InfrastructureManager.md) (7), [Sector](Sector.md) (5), [ConstructionControl](ConstructionControl.md) (5), [EducationType](EducationType.md) (4), [BuildingsStacks](BuildingsStacks.md) (4), [ChildcareCheck](ChildcareCheck.md) (3), [PopulationManager](PopulationManager.md) (3), [JobType](JobType.md) (3), [PowerDispatch](PowerDispatch.md) (3), [BuildCard](BuildCard.md) (2), [WageBand](WageBand.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [GameFiles](GameFiles.md) (2), [SubstationCheck](SubstationCheck.md) (1), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1), [Education](Education.md) (1), [AgeBand](AgeBand.md) (1)... and 4 more

**Used by (1):** [WorksFinanceCheck](WorksFinanceCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `EssentialWorksCheck.out` | `System.out` |  |
| 25 | `EssentialWorksCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 26 | `EssentialWorksCheck.HIGHWAYS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS)` |  |
| 546 | `EssentialWorksCheck.SERVED_STREETS` | `.5` | The share of their traffic currentLossPriority()'s streets serve: under full, over power's quarter (FS15). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 1151 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
| 28 | 4 | `static void check(String label, boolean ok)` |  |
| 33 | 5 | `static void quietly(Runnable run)` |  |
| 39 | 9 | `static Game town(Path root, String name)` |  |
| 49 | 3 | `static AutoBuilder.Step step(Game g, BuildAdvice.Measure m)` |  |
| 53 | 25 | `static void highways(Path root, boolean ground)` |  |
| 84 | 11 | `static void powerDrawAt(Game g, double share)` | Sets the city's power draw so the advice's demand is `share` of its supply (BuildAdvice.supplyDemand(), read at two draws and solved, the works pending in it): served with a margin under 1, short over it. |
| 97 | 14 | `static void serveGrid(Game g)` | These cases isolate another essential order; physical wires cover even its longest forecast. |
| 113 | 21 | `static boolean serveOtherEssentials(Game g, BuildAdvice.Measure except)` | Give unrelated essential services their operating capacity before isolating an ahead-of-need order. |
| 138 | 9 | `static void carry(Game g, BuildingsTemplate highway, double load, double[] streams, double f)` | The town carrying its traffic as it stood times `f`, every stream alike, and streets built past it: the forecast order has no unrelated unserved street whose construction promise would take precedence. |
| 150 | 11 | `static double mostCovered(Game g, BuildingsTemplate highway, double load, double[] streams, BuildAdvice.Ahead p)` | The most of its traffic, as a multiple, at which the town's highways are maintained under `p`, by bisection (FS15). |
| 162 | 54 | `static void anticipateHighway(Path root)` |  |
| 217 | 33 | `static void schoolsAndPolice(Path root)` |  |
| 251 | 35 | `static void lostOutput(Path root)` |  |
| 287 | 13 | `static void serveStreets(Game g)` |  |
| 301 | 30 | `static void scarceCash(Path root)` |  |
| 332 | 40 | `static void professionalDemand(Path root)` |  |
| 373 | 43 | `static void pendingIndustrialLoad(Path root)` |  |
| 417 | 39 | `static void interimCare(Path root)` |  |
| 457 | 33 | `static void stagedCosts(Path root)` |  |
| 491 | 42 | `static void completeRunning(Path root)` |  |
| 534 | 10 | `static double existingWear(Game g, Map<BuildingsTemplate, Long> order)` |  |
| 548 | 39 | `static void currentLossPriority(Path root)` |  |
| 590 | 13 | `static void scalePeople(Game g, double f)` | Every band of the town's people, and its two adult stages, times `f`, its workforce held (FS15): a demand moved without moving the staffing of the works that serve it. |
| 604 | 101 | `static void fundingPriority(Path root, boolean spareCare) throws Exception` |  |
| 706 | 29 | `static void filledPostCosts(Path root)` |  |
| 736 | 44 | `static void cappedCare(Path root)` |  |
| 781 | 39 | `static void adviceParity(Path root)` |  |
| 821 | 63 | `static void interimPower(Path root)` |  |
| 885 | 35 | `static void waterAtCap(Path root)` |  |
| 921 | 38 | `static void queuedPower(Path root)` |  |
| 960 | 37 | `static void shortPowerBenefit(Path root)` |  |
| 998 | 40 | `static void pavingLead(Path root)` |  |
| 1039 | 83 | `static void overduePending(Path root) throws Exception` |  |
| 1123 | 20 | `static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first, java.util.List<String> orders, bool...` |  |
| 1144 | 28 | `public static void main(String[] args) throws Exception` |  |


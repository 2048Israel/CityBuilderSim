# EssentialWorksCheck.java - 1,525 lines · 40 methods · 9 constants · harnesses

`ham/citybuildersim/EssentialWorksCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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
> the staff-short plant by the workforce set to half the posts, and (RES3) the school's
> demand and the growth cap by the people scaled to them (capGrowth()).

**Uses:** [BuildAdvice](BuildAdvice.md) (301), [AutoBuilder](AutoBuilder.md) (63), [BuildingsTemplate](BuildingsTemplate.md) (63), [Game](Game.md) (45), [WorksValue](WorksValue.md) (36), [LandManager](LandManager.md) (17), [UtilitiesHandler](UtilitiesHandler.md) (15), [CityNeeds](CityNeeds.md) (15), [BuildingManager](BuildingManager.md) (14), [Traffic](Traffic.md) (11), [ConstructionControl](ConstructionControl.md) (10), [CityCalendar](CityCalendar.md) (9), [PowerDispatch](PowerDispatch.md) (8), [CareType](CareType.md) (8), [Health](Health.md) (8), [InfrastructureManager](InfrastructureManager.md) (7), [Sector](Sector.md) (5), [BuildingsStacks](BuildingsStacks.md) (5), [EducationType](EducationType.md) (4), [ChildcareCheck](ChildcareCheck.md) (3), [PopulationManager](PopulationManager.md) (3), [JobType](JobType.md) (3), [BuildCard](BuildCard.md) (2), [WageBand](WageBand.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [PowerPlanCheck](PowerPlanCheck.md) (2), [GameFiles](GameFiles.md) (2), [SubstationCheck](SubstationCheck.md) (1), [SafetyType](SafetyType.md) (1), [Crime](Crime.md) (1)... and 7 more

**Used by (9):** [AskProceedsCheck](AskProceedsCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [FarmlandReserveCheck](FarmlandReserveCheck.md), [GapOrderCheck](GapOrderCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerWiresCheck](PowerWiresCheck.md), [ScaleCheck](ScaleCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 25 | `EssentialWorksCheck.out` | `System.out` |  |
| 26 | `EssentialWorksCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 27 | `EssentialWorksCheck.HIGHWAYS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS)` |  |
| 360 | `EssentialWorksCheck.PROFESSIONAL_TOWN_HOUSES` | `2_000` | The Houses each copy of the professional-demand town stands with (RES3): a door holds one household, so the 900 of a copy hold 2,900 people and 1,100 workers for about 3,000 posts, and the builders' fill falls from 0.... |
| 576 | `EssentialWorksCheck.SERVED_STREETS` | `.5` | The share of their traffic currentLossPriority()'s streets serve: under full, over power's quarter (FS15). |
| 646 | `EssentialWorksCheck.INTERIM_GROWTH_CAP` | `1.198` | The growth cap interimPower()'s town is scaled to (RES3): main's 4,590 people under 5,500 beds, where one household to a door left 3,021 under them (1.82). |
| 649 | `EssentialWorksCheck.EARLY_WIND_GROWTH_CAP` | `1.094` | The growth cap earlyWind()'s town is scaled to (RES3): main's town after its years to the Steam Station's retirement, where RES3's (1.60 at the wind's opening) let five Diesel Stations close the gap and five Wind Farm... |
| 661 | `EssentialWorksCheck.SCHOOL_DEMAND_OVER_FIRST_WORK` | `1.36` | The elementary school's unmet demand over its first-work threshold (BuildAdvice.FIRST_WORK_SHARE of an Elementary School's places) that fundingPriority()'s ranked town is scaled to (RES3): main's town asked 245 places... |
| 1279 | `EssentialWorksCheck.PENDING_DEPOTS` | `28` | The Construction Depots overduePending() stands (RES3): one household to a door left the town's builders 494 to 468 points a month where main's made 723 to 747, and once the repairs of the roads on site jumped (328 to... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 1503 | **type** `public final class EssentialWorksCheck` | Essential works ordered for their unmet service, including their ground (HW1). |
| 29 | 4 | `static void check(String label, boolean ok)` |  |
| 34 | 5 | `static void quietly(Runnable run)` |  |
| 40 | 12 | `static Game town(Path root, String name)` |  |
| 54 | 3 | `static void knowWind(Game g)` | A city's research finds the Wind Farm's code (its since) at month 1, so a 1900 city knows it: the fixture's lever, since PW2 dates every plant. |
| 58 | 3 | `static AutoBuilder.Step step(Game g, BuildAdvice.Measure m)` |  |
| 62 | 25 | `static void highways(Path root, boolean ground)` |  |
| 93 | 11 | `static void powerDrawAt(Game g, double share)` | Sets the city's power draw so the advice's demand is `share` of its supply (BuildAdvice.supplyDemand(), read at two draws and solved, the works pending in it): served with a margin under 1, short over it. |
| 106 | 14 | `static void serveGrid(Game g)` | These cases isolate another essential order; physical wires cover even its longest forecast. |
| 122 | 21 | `static boolean serveOtherEssentials(Game g, BuildAdvice.Measure except)` | Give unrelated essential services their operating capacity before isolating an ahead-of-need order. |
| 147 | 9 | `static void carry(Game g, BuildingsTemplate highway, double load, double[] streams, double f)` | The town carrying its traffic as it stood times `f`, every stream alike, and streets built past it: the forecast order has no unrelated unserved street whose construction promise would take precedence. |
| 159 | 11 | `static double mostCovered(Game g, BuildingsTemplate highway, double load, double[] streams, BuildAdvice.Ahead p)` | The most of its traffic, as a multiple, at which the town's highways are maintained under `p`, by bisection (FS15). |
| 171 | 54 | `static void anticipateHighway(Path root)` |  |
| 226 | 33 | `static void schoolsAndPolice(Path root)` |  |
| 260 | 35 | `static void lostOutput(Path root)` |  |
| 296 | 13 | `static void serveStreets(Game g)` |  |
| 310 | 43 | `static void scarceCash(Path root)` |  |
| 362 | 40 | `static void professionalDemand(Path root)` |  |
| 403 | 43 | `static void pendingIndustrialLoad(Path root)` |  |
| 447 | 39 | `static void interimCare(Path root)` |  |
| 487 | 33 | `static void stagedCosts(Path root)` |  |
| 521 | 42 | `static void completeRunning(Path root)` |  |
| 564 | 10 | `static double existingWear(Game g, Map<BuildingsTemplate, Long> order)` |  |
| 578 | 39 | `static void currentLossPriority(Path root)` |  |
| 620 | 13 | `static void scalePeople(Game g, double f)` | Every band of the town's people, and its two adult stages, times `f`, its workforce held (FS15): a demand moved without moving the staffing of the works that serve it. |
| 639 | 5 | `static void capGrowth(Game g, double cap)` | The town's people scaled, its workforce held, to where its beds cap its growth at `cap` (BusinessInvestment.growthFactor(): reachablePopulation() over the people, which the written population history reads as the last... |
| 652 | 3 | `static double growthCap(Game g, java.util.List<Long> history)` | The cap the model reads off a written population history (BusinessInvestment.growthFactor()): the beds over the history's last, the figure a section's fixture states. |
| 663 | 114 | `static void fundingPriority(Path root, boolean spareCare) throws Exception` |  |
| 778 | 29 | `static void filledPostCosts(Path root)` |  |
| 808 | 44 | `static void cappedCare(Path root)` |  |
| 853 | 49 | `static void adviceParity(Path root)` |  |
| 904 | 26 | `static double nativeUnserved(Game g, Map<BuildingsTemplate, Long> added, int months)` | Native unserved kWh at a forecast calendar month, then restore the town's current dispatch. |
| 931 | 102 | `static void interimPower(Path root)` |  |
| 1034 | 85 | `static void earlyWind(Path root)` |  |
| 1120 | 35 | `static void waterAtCap(Path root)` |  |
| 1156 | 38 | `static void queuedPower(Path root)` |  |
| 1195 | 37 | `static void shortPowerBenefit(Path root)` |  |
| 1233 | 40 | `static void pavingLead(Path root)` |  |
| 1281 | 143 | `static void overduePending(Path root) throws Exception` |  |
| 1425 | 60 | `static void installPendingProbe(Game g, BuildAdvice.Measure power, int began, double first, int oldCheckpoint, boolean postNeed...` |  |
| 1486 | 39 | `public static void main(String[] args) throws Exception` |  |


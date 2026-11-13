# ServicesManager.java - 401 lines · 28 methods · 0 constants · model

`ham/citybuildersim/ServicesManager.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (17), [BuildingType](BuildingType.md) (13), [Traffic](Traffic.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [BuildingManager](BuildingManager.md) (2)

**Used by (7):** [BuildAdviceCheck](BuildAdviceCheck.md), [Game](Game.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [WaterCheck](WaterCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 10 | `private final BuildingManager buildingManager` |  |
| 12 | `private final UtilitiesHandler utilitiesHandler` |  |
| 13 | `private final InfrastructureManager infrastructureManager` |  |
| 16 | `private final double[] fillRate` | universal |
| 20 | `private final double[] utilityWages` | utilities labor - kept split so the utilities report can attribute payroll to electricity or water rather than showing one lump |
| 21 | `private final long[] electricityJobs` |  |
| 22 | `private final long[] waterJobs` |  |
| 38 | `private DoubleSupplier freshCapSource` | Where the fresh water limit comes from (0.7.59, batch J2): the city's land and rights, read each time the services update - Game hands in Game.getFreshCap() - so every path that updates them (the month, the load, the ... |
| 49 | `private java.util.function.ToDoubleFunction<String> steamShareSource` | The steam share of a sector, by its key (R0c, the steam form; Sector.setSteamShare()): Game hands in the sectors' own. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 8 | 394 | **type** `public class ServicesManager` |  |
| 24 | 5 | `public ServicesManager(BuildingManager buildingManager)` |  |
| 40 | 3 | `public void setFreshCapSource(DoubleSupplier source)` |  |
| 51 | 3 | `public void setSteamShareSource(java.util.function.ToDoubleFunction<String> source)` |  |
| 56 | 6 | `private double gridDraw(BuildingsTemplate t)` | What a building draws from the grid (BuildingsTemplate.powerBought()), less the part its sector turns with steam (R0c): the building's own draw while no share is set. |
| 67 | 7 | `public void updateServices()` | =============================== MAIN UPDATE =============================== |
| 79 | 79 | `private void updateProduction()` | =============================== UPDATE PHASES =============================== |
| 187 | 54 | `public void updateInfrastructure()` | Recomputes the road network from what is standing right now. |
| 243 | 3 | `public void updateTransitFare(double fare)` | The fare the player set, which decides how many of them ride. |
| 247 | 5 | `private void updateLabor()` |  |
| 253 | 3 | `private void updateHandlers()` |  |
| 266 | 17 | `public void updateServiceWages(double[] wages)` | NOTE: this used to be named updateIndustrialWages() - a copy-paste of EconomyManager's method name that had nothing to do with what this does. |
| 284 | 3 | `public void updateJobFillRate(double[] fillRate)` |  |
| 292 | 3 | `public UtilitiesHandler getUtilitiesHandler()` | =============================== GETTERS =============================== |
| 296 | 3 | `public InfrastructureManager getInfrastructureManager()` |  |
| 300 | 3 | `public double getEnergyRatio()` |  |
| 304 | 3 | `public double getWaterRatio()` |  |
| 315 | 3 | `public double getRoadRatio()` | What congestion lets the city actually get done, 0.35 to 1. |
| 328 | 3 | `public double getServiceNetIncome()` | What the CITY earns from the services it owns - now utilities only. |
| 332 | 3 | `public double getPricePerWatt()` |  |
| 336 | 3 | `public double getPricePerWaterUnit()` |  |
| 341 | 3 | `public void setPopulation(long population)` | The people's water draw. |
| 350 | 3 | `public void printUtilityInfo()` | =============================== PRINTERS =============================== |
| 358 | 9 | `public void updateByCategoryHandlerDouble(BuildingType category, DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> get...` | =============================== GENERIC BUILDING AGGREGATION =============================== |
| 368 | 8 | `public void updateHandlerDouble(DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 381 | 3 | `public void updateFromGame(DoubleConsumer setter, double value)` | =============================== GAME → HANDLER HELPERS =============================== |
| 385 | 3 | `public void updateFromGameInt(IntConsumer setter, int value)` |  |
| 393 | 3 | `private void copyArray(double[] source, double[] target)` | =============================== ARRAY HELPERS =============================== |
| 397 | 3 | `private void copyArray(long[] source, long[] target)` |  |


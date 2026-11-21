# ServicesManager.java - 418 lines · 31 methods · 0 constants · model

`ham/citybuildersim/ServicesManager.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
| 69 | `private double electrolyserSaving` | The grid units a month the Hydrogen Steel Works fitted with FE10's electrolysers no longer draw, at nameplate and net of their sector's steam share (R6d, REV-R6d P1): EquipmentForms sets it from the works' counts at e... |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 8 | 411 | **type** `public class ServicesManager` |  |
| 24 | 5 | `public ServicesManager(BuildingManager buildingManager)` |  |
| 40 | 3 | `public void setFreshCapSource(DoubleSupplier source)` |  |
| 51 | 3 | `public void setSteamShareSource(java.util.function.ToDoubleFunction<String> source)` |  |
| 56 | 6 | `private double gridDraw(BuildingsTemplate t)` | What a building draws from the grid (BuildingsTemplate.powerBought()), less the part its sector turns with steam (R0c): the building's own draw while no share is set. |
| 71 | 3 | `public void setElectrolyserSaving(double units)` |  |
| 75 | 1 | `public double getElectrolyserSaving()` |  |
| 78 | 3 | `private double lessElectrolysers(double draw)` | A draw the grid is asked for, less the electrolysers' saving, kept at 0 or more; the draw itself, to the bit, while none is saved. |
| 86 | 7 | `public void updateServices()` | =============================== MAIN UPDATE =============================== |
| 98 | 77 | `private void updateProduction()` | =============================== UPDATE PHASES =============================== |
| 204 | 54 | `public void updateInfrastructure()` | Recomputes the road network from what is standing right now. |
| 260 | 3 | `public void updateTransitFare(double fare)` | The fare the player set, which decides how many of them ride. |
| 264 | 5 | `private void updateLabor()` |  |
| 270 | 3 | `private void updateHandlers()` |  |
| 283 | 17 | `public void updateServiceWages(double[] wages)` | NOTE: this used to be named updateIndustrialWages() - a copy-paste of EconomyManager's method name that had nothing to do with what this does. |
| 301 | 3 | `public void updateJobFillRate(double[] fillRate)` |  |
| 309 | 3 | `public UtilitiesHandler getUtilitiesHandler()` | =============================== GETTERS =============================== |
| 313 | 3 | `public InfrastructureManager getInfrastructureManager()` |  |
| 317 | 3 | `public double getEnergyRatio()` |  |
| 321 | 3 | `public double getWaterRatio()` |  |
| 332 | 3 | `public double getRoadRatio()` | What congestion lets the city actually get done, 0.35 to 1. |
| 345 | 3 | `public double getServiceNetIncome()` | What the CITY earns from the services it owns - now utilities only. |
| 349 | 3 | `public double getPricePerWatt()` |  |
| 353 | 3 | `public double getPricePerWaterUnit()` |  |
| 358 | 3 | `public void setPopulation(long population)` | The people's water draw. |
| 367 | 3 | `public void printUtilityInfo()` | =============================== PRINTERS =============================== |
| 375 | 9 | `public void updateByCategoryHandlerDouble(BuildingType category, DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> get...` | =============================== GENERIC BUILDING AGGREGATION =============================== |
| 385 | 8 | `public void updateHandlerDouble(DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 398 | 3 | `public void updateFromGame(DoubleConsumer setter, double value)` | =============================== GAME → HANDLER HELPERS =============================== |
| 402 | 3 | `public void updateFromGameInt(IntConsumer setter, int value)` |  |
| 410 | 3 | `private void copyArray(double[] source, double[] target)` | =============================== ARRAY HELPERS =============================== |
| 414 | 3 | `private void copyArray(long[] source, long[] target)` |  |


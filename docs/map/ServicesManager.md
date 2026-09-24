# ServicesManager.java - 320 lines · 25 methods · 0 constants · model

`ham/citybuildersim/ServicesManager.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (11), [BuildingType](BuildingType.md) (6), [Traffic](Traffic.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [BuildingManager](BuildingManager.md) (2)

**Used by (6):** [Game](Game.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [WaterCheck](WaterCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 9 | `private final BuildingManager buildingManager` |  |
| 11 | `private final UtilitiesHandler utilitiesHandler` |  |
| 12 | `private final InfrastructureManager infrastructureManager` |  |
| 15 | `private final double[] fillRate` | universal |
| 19 | `private final double[] utilityWages` | utilities labor - kept split so the utilities report can attribute payroll to electricity or water rather than showing one lump |
| 20 | `private final int[] electricityJobs` |  |
| 21 | `private final int[] waterJobs` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 7 | 314 | **type** `public class ServicesManager` |  |
| 23 | 5 | `public ServicesManager(BuildingManager buildingManager)` |  |
| 33 | 7 | `public void updateServices()` | =============================== MAIN UPDATE =============================== |
| 45 | 48 | `private void updateProduction()` | =============================== UPDATE PHASES =============================== |
| 122 | 38 | `public void updateInfrastructure()` | Recomputes the road network from what is standing right now. |
| 162 | 3 | `public void updateTransitFare(double fare)` | The fare the player set, which decides how many of them ride. |
| 166 | 5 | `private void updateLabor()` |  |
| 172 | 3 | `private void updateHandlers()` |  |
| 185 | 17 | `public void updateServiceWages(double[] wages)` | NOTE: this used to be named updateIndustrialWages() - a copy-paste of EconomyManager's method name that had nothing to do with what this does. |
| 203 | 3 | `public void updateJobFillRate(double[] fillRate)` |  |
| 211 | 3 | `public UtilitiesHandler getUtilitiesHandler()` | =============================== GETTERS =============================== |
| 215 | 3 | `public InfrastructureManager getInfrastructureManager()` |  |
| 219 | 3 | `public double getEnergyRatio()` |  |
| 223 | 3 | `public double getWaterRatio()` |  |
| 234 | 3 | `public double getRoadRatio()` | What congestion lets the city actually get done, 0.35 to 1. |
| 247 | 3 | `public double getServiceNetIncome()` | What the CITY earns from the services it owns - now utilities only. |
| 251 | 3 | `public double getPricePerWatt()` |  |
| 255 | 3 | `public double getPricePerWaterUnit()` |  |
| 260 | 3 | `public void setPopulation(int population)` | The people's water draw. |
| 269 | 3 | `public void printUtilityInfo()` | =============================== PRINTERS =============================== |
| 277 | 9 | `public void updateByCategoryHandlerDouble(BuildingType category, DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> get...` | =============================== GENERIC BUILDING AGGREGATION =============================== |
| 287 | 8 | `public void updateHandlerDouble(DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 300 | 3 | `public void updateFromGame(DoubleConsumer setter, double value)` | =============================== GAME → HANDLER HELPERS =============================== |
| 304 | 3 | `public void updateFromGameInt(IntConsumer setter, int value)` |  |
| 312 | 3 | `private void copyArray(double[] source, double[] target)` | =============================== ARRAY HELPERS =============================== |
| 316 | 3 | `private void copyArray(int[] source, int[] target)` |  |


# ServicesManager.java - 328 lines · 25 methods · 0 constants · model

`ham/citybuildersim/ServicesManager.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (11), [BuildingType](BuildingType.md) (8), [Traffic](Traffic.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [BuildingManager](BuildingManager.md) (2)

**Used by (7):** [BuildAdviceCheck](BuildAdviceCheck.md), [Game](Game.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [WaterCheck](WaterCheck.md)

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
| 7 | 322 | **type** `public class ServicesManager` |  |
| 23 | 5 | `public ServicesManager(BuildingManager buildingManager)` |  |
| 33 | 7 | `public void updateServices()` | =============================== MAIN UPDATE =============================== |
| 45 | 56 | `private void updateProduction()` | =============================== UPDATE PHASES =============================== |
| 130 | 38 | `public void updateInfrastructure()` | Recomputes the road network from what is standing right now. |
| 170 | 3 | `public void updateTransitFare(double fare)` | The fare the player set, which decides how many of them ride. |
| 174 | 5 | `private void updateLabor()` |  |
| 180 | 3 | `private void updateHandlers()` |  |
| 193 | 17 | `public void updateServiceWages(double[] wages)` | NOTE: this used to be named updateIndustrialWages() - a copy-paste of EconomyManager's method name that had nothing to do with what this does. |
| 211 | 3 | `public void updateJobFillRate(double[] fillRate)` |  |
| 219 | 3 | `public UtilitiesHandler getUtilitiesHandler()` | =============================== GETTERS =============================== |
| 223 | 3 | `public InfrastructureManager getInfrastructureManager()` |  |
| 227 | 3 | `public double getEnergyRatio()` |  |
| 231 | 3 | `public double getWaterRatio()` |  |
| 242 | 3 | `public double getRoadRatio()` | What congestion lets the city actually get done, 0.35 to 1. |
| 255 | 3 | `public double getServiceNetIncome()` | What the CITY earns from the services it owns - now utilities only. |
| 259 | 3 | `public double getPricePerWatt()` |  |
| 263 | 3 | `public double getPricePerWaterUnit()` |  |
| 268 | 3 | `public void setPopulation(int population)` | The people's water draw. |
| 277 | 3 | `public void printUtilityInfo()` | =============================== PRINTERS =============================== |
| 285 | 9 | `public void updateByCategoryHandlerDouble(BuildingType category, DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> get...` | =============================== GENERIC BUILDING AGGREGATION =============================== |
| 295 | 8 | `public void updateHandlerDouble(DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 308 | 3 | `public void updateFromGame(DoubleConsumer setter, double value)` | =============================== GAME → HANDLER HELPERS =============================== |
| 312 | 3 | `public void updateFromGameInt(IntConsumer setter, int value)` |  |
| 320 | 3 | `private void copyArray(double[] source, double[] target)` | =============================== ARRAY HELPERS =============================== |
| 324 | 3 | `private void copyArray(int[] source, int[] target)` |  |


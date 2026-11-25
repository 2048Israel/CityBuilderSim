# ServicesManager.java - 448 lines · 34 methods · 0 constants · model

`ham/citybuildersim/ServicesManager.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (19), [BuildingType](BuildingType.md) (16), [Traffic](Traffic.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [BuildingManager](BuildingManager.md) (2), [Substations](Substations.md) (1), [Good](Good.md) (1)

**Used by (7):** [BuildAdviceCheck](BuildAdviceCheck.md), [Game](Game.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [WaterCheck](WaterCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `private final BuildingManager buildingManager` |  |
| 15 | `private final UtilitiesHandler utilitiesHandler` |  |
| 16 | `private final InfrastructureManager infrastructureManager` |  |
| 19 | `private final double[] fillRate` | universal |
| 23 | `private final double[] utilityWages` | utilities labor - kept split so the utilities report can attribute payroll to electricity or water rather than showing one lump |
| 24 | `private final long[] electricityJobs` |  |
| 25 | `private final long[] waterJobs` |  |
| 41 | `private DoubleSupplier freshCapSource` | Where the fresh water limit comes from (0.7.59, batch J2): the city's land and rights, read each time the services update - Game hands in Game.getFreshCap() - so every path that updates them (the month, the load, the ... |
| 52 | `private java.util.function.ToDoubleFunction<String> steamShareSource` | The steam share of a sector, by its key (R0c, the steam form; Sector.setSteamShare()): Game hands in the sectors' own. |
| 72 | `private double electrolyserSaving` | The grid units a month the Hydrogen Steel Works fitted with FE10's electrolysers no longer draw, at nameplate and net of their sector's steam share (R6d, REV-R6d P1): EquipmentForms sets it from the works' counts at e... |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 438 | **type** `public class ServicesManager` |  |
| 27 | 5 | `public ServicesManager(BuildingManager buildingManager)` |  |
| 43 | 3 | `public void setFreshCapSource(DoubleSupplier source)` |  |
| 54 | 3 | `public void setSteamShareSource(java.util.function.ToDoubleFunction<String> source)` |  |
| 59 | 6 | `private double gridDraw(BuildingsTemplate t)` | What a building draws from the grid (BuildingsTemplate.powerBought()), less the part its sector turns with steam (R0c): the building's own draw while no share is set. |
| 74 | 3 | `public void setElectrolyserSaving(double units)` |  |
| 78 | 1 | `public double getElectrolyserSaving()` |  |
| 81 | 3 | `private double lessElectrolysers(double draw)` | A draw the grid is asked for, less the electrolysers' saving, kept at 0 or more; the draw itself, to the bit, while none is saved. |
| 89 | 7 | `public void updateServices()` | =============================== MAIN UPDATE =============================== |
| 101 | 89 | `private void updateProduction()` | =============================== UPDATE PHASES =============================== |
| 219 | 54 | `public void updateInfrastructure()` | Recomputes the road network from what is standing right now. |
| 275 | 3 | `public void updateTransitFare(double fare)` | The fare the player set, which decides how many of them ride. |
| 279 | 5 | `private void updateLabor()` |  |
| 285 | 3 | `private void updateHandlers()` |  |
| 298 | 17 | `public void updateServiceWages(double[] wages)` | NOTE: this used to be named updateIndustrialWages() - a copy-paste of EconomyManager's method name that had nothing to do with what this does. |
| 316 | 3 | `public void updateJobFillRate(double[] fillRate)` |  |
| 324 | 3 | `public UtilitiesHandler getUtilitiesHandler()` | =============================== GETTERS =============================== |
| 328 | 3 | `public InfrastructureManager getInfrastructureManager()` |  |
| 332 | 3 | `public double getEnergyRatio()` |  |
| 336 | 3 | `public double getWaterRatio()` |  |
| 347 | 3 | `public double getRoadRatio()` | What congestion lets the city actually get done, 0.35 to 1. |
| 360 | 3 | `public double getServiceNetIncome()` | What the CITY earns from the services it owns - now utilities only. |
| 364 | 3 | `public double getPricePerWatt()` |  |
| 369 | 3 | `public void setFuelPrice(ToDoubleFunction<Good> cityPrice)` | The city's price of a fuel the plants burn (PW1); null: the world's. |
| 374 | 3 | `public void setExchangeRate(DoubleSupplier rate)` | City money per world thousand, for the plants' upkeep (PW1); null: 1, at par. |
| 379 | 3 | `public void setCalendarMonth(IntSupplier month)` | The calendar month, 1 to 12, for the season of the month's load (PW1); null: none. |
| 383 | 3 | `public double getPricePerWaterUnit()` |  |
| 388 | 3 | `public void setPopulation(long population)` | The people's water draw. |
| 397 | 3 | `public void printUtilityInfo()` | =============================== PRINTERS =============================== |
| 405 | 9 | `public void updateByCategoryHandlerDouble(BuildingType category, DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> get...` | =============================== GENERIC BUILDING AGGREGATION =============================== |
| 415 | 8 | `public void updateHandlerDouble(DoubleConsumer setter, ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 428 | 3 | `public void updateFromGame(DoubleConsumer setter, double value)` | =============================== GAME → HANDLER HELPERS =============================== |
| 432 | 3 | `public void updateFromGameInt(IntConsumer setter, int value)` |  |
| 440 | 3 | `private void copyArray(double[] source, double[] target)` | =============================== ARRAY HELPERS =============================== |
| 444 | 3 | `private void copyArray(long[] source, long[] target)` |  |


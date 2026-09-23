# SimulationEngine.java - 191 lines · 5 methods · 0 constants · model

`ham/citybuildersim/SimulationEngine.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Game](Game.md) (4), [EconomyManager](EconomyManager.md) (2), [PopulationManager](PopulationManager.md) (2), [ServicesManager](ServicesManager.md) (2), [BuildingManager](BuildingManager.md) (2), [DebtManager](DebtManager.md) (2), [AgeBand](AgeBand.md) (2)

**Used by (1):** [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 10 | `private EconomyManager economyManager` |  |
| 11 | `private PopulationManager populationManager` |  |
| 12 | `private ServicesManager servicesManager` |  |
| 13 | `private BuildingManager buildingManager` |  |
| 14 | `private DebtManager debtManager` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 8 | 184 | **type** `public class SimulationEngine` |  |
| 16 | 13 | `public SimulationEngine(EconomyManager economyManager, PopulationManager populationManager, ServicesManager servicesManager, Bu...` |  |
| 30 | 78 | `public void simulateMonth(Game game)` |  |
| 109 | 8 | `public void updatePopulation(Game game)` |  |
| 118 | 63 | `public void updateEconomy(Game game)` |  |
| 182 | 8 | `public void updateServices(Game game)` |  |


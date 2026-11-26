# SimulationEngine.java - 264 lines · 5 methods · 0 constants · model

`ham/citybuildersim/SimulationEngine.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The order the month runs in: the sites advance, the roads, the posts and
> the wages, the people, then the economy and the services.

**Uses:** [Game](Game.md) (5), [BuildingManager](BuildingManager.md) (3), [EconomyManager](EconomyManager.md) (2), [PopulationManager](PopulationManager.md) (2), [ServicesManager](ServicesManager.md) (2), [DebtManager](DebtManager.md) (2), [AgeBand](AgeBand.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (1)

**Used by (1):** [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 12 | `private EconomyManager economyManager` |  |
| 13 | `private PopulationManager populationManager` |  |
| 14 | `private ServicesManager servicesManager` |  |
| 15 | `private BuildingManager buildingManager` |  |
| 16 | `private DebtManager debtManager` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 10 | 255 | **type** `public class SimulationEngine` | The order the month runs in: the sites advance, the roads, the posts and the wages, the people, then the economy and the services. |
| 18 | 13 | `public SimulationEngine(EconomyManager economyManager, PopulationManager populationManager, ServicesManager servicesManager, Bu...` |  |
| 32 | 135 | `public void simulateMonth(Game game)` |  |
| 168 | 13 | `public void updatePopulation(Game game)` |  |
| 182 | 72 | `public void updateEconomy(Game game)` |  |
| 255 | 8 | `public void updateServices(Game game)` |  |


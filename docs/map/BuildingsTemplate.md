# BuildingsTemplate.java - 792 lines · 79 methods · 2 constants · model

`ham/citybuildersim/BuildingsTemplate.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> One kind of building, and what it costs to put up.
> 
> MONEY HERE IS IN THOUSANDS OF DOLLARS, and this is the file where that
> matters most, because it is the file somebody opens to balance the game.
> Money.toDollars() multiplies by a thousand on the way to the screen,
> so:
> 
>     cashCost 30           is  $30,000
>     cashCost 125,000      is  $125,000,000   (a Coal Power Plant)
>     upkeep 190            is  $190,000 a month
> 
> constructionPoints, capacity, dwellings and landSqFt are NOT money and do not
> convert - ten points is ten points, 8,000 sq ft is 8,000 sq ft.
> 
> The scale exists to delay floating-point error: four thousand months of
> arithmetic in thousands stays in a range where a double has digits to spare.
> 
> claude/reading-the-numbers.md carries every building's price in real dollars
> next to what the real thing costs, which is the table to balance against.

**Uses:** [Good](Good.md) (14), [JobType](JobType.md) (8), [BuildingType](BuildingType.md) (7), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [SafetyType](SafetyType.md) (5), [Traffic](Traffic.md) (4)

**Used by (99):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingInstance](BuildingInstance.md), [BuildingManager](BuildingManager.md), [BuildingVisual](BuildingVisual.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [Construction](Construction.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 102 | WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) |
| 121 | A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) |
| 145 | WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). |
| 482 | WHAT A MODE IS GOOD AT (2026-09-16) |
| 546 | WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 117 | `BuildingsTemplate.Source.FRESH` |  |
| 117 | `BuildingsTemplate.Source.SEA` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 587 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 590 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `String name` |  |
| 31 | `double cashCost` | What the buyer pays in cash, in THOUSANDS. |
| 32 | `int constructionPoints` |  |
| 33 | `int capacity` |  |
| 45 | `int dwellings` | How many separate HOMES this building contains, each holding one household. |
| 46 | `double upkeep` |  |
| 47 | `int constructionMaterials` |  |
| 48 | `int electricityConsumption` |  |
| 51 | `double waterConsumption` | double, not int: a single House draws a fraction of a unit. |
| 57 | `double landSqFt` | Lot footprint in square feet. |
| 67 | `double roadLoad` | Trips this building puts on the road network every month. |
| 68 | `int coverage` |  |
| 69 | `double production1` |  |
| 70 | `double production2` |  |
| 71 | `double productionModifier1` |  |
| 72 | `double productionModifier2` |  |
| 73 | `boolean nationalized` |  |
| 82 | `private CareType care` | What a healthcare building is for; NONE for everything else. |
| 93 | `private EducationType teaches` | What a school teaches; NONE for everything else. |
| 100 | `private SafetyType safety` | What a safety building is for - officers or cells - and NONE for everything else. |
| 119 | `private Source source` |  |
| 141 | `private JobType requiresLicence` |  |
| 143 | `private int id` |  |
| 165 | `private String sector` |  |
| 166 | `private final java.util.Map<Good, Double> makes` |  |
| 167 | `private final java.util.Map<Good, Double> uses` |  |
| 168 | `private double stock` |  |
| 224 | `int[] jobsByEducation` | enums |
| 225 | `private BuildingType category` |  |
| 491 | `private double freightGrade` |  |
| 492 | `private double transitCapacity` |  |
| 529 | `private double railCapacity` |  |
| 749 | `private double foundingCashCost` | What this building cost when the catalogue was read, before any currency reform - so a reformed city that reloads can divide it again. |
| 750 | `private double foundingUpkeep` |  |
| 766 | `private double struckAt` | What the founding figures were last divided by to give today's: the unit over the expected price level at the last re-seed, over any reform since. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 766 | **type** `public class BuildingsTemplate` | One kind of building, and what it costs to put up. |

### WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) (lines 102-120)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 1 | **type** `public enum Source` | What a water works draws: FRESH water, held to the city's limit, or the SEA, which is not. |

### A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) (lines 121-144)

### WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). (lines 145-481)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 4 | `public BuildingsTemplate setSector(String sector)` |  |
| 175 | 4 | `public BuildingsTemplate makes(Good good, double unitsAMonth)` |  |
| 180 | 4 | `public BuildingsTemplate uses(Good good, double unitsAMonth)` |  |
| 185 | 4 | `public BuildingsTemplate setStock(double units)` |  |
| 191 | 1 | `public String getSector()` | The owning sector's key, or "" for a building nobody in the private sector owns. |
| 193 | 1 | `public boolean isOwnedBySector()` |  |
| 202 | 3 | `public static boolean isBilledForUtilities(BuildingsTemplate t)` | Whether a building's power and water are invoiced to anybody: the business buildings a sector owns. |
| 207 | 1 | `public double makes(Good good)` | Units of a good this building makes a month at nameplate. |
| 210 | 1 | `public double uses(Good good)` | Units of a good this building uses a month at nameplate. |
| 212 | 1 | `public java.util.Map<Good, Double> goodsMade()` |  |
| 213 | 1 | `public java.util.Map<Good, Double> goodsUsed()` |  |
| 216 | 1 | `public double getStock()` | Room for what it holds, in units. |
| 219 | 1 | `public double stocks(Good good)` | The same, asked per good: a building has one warehouse and it holds whatever the building holds. |
| 228 | 4 | `public BuildingsTemplate(String name, BuildingType category)` | barebones constructor |
| 276 | 4 | `public BuildingsTemplate setJobs(JobType type, int number)` | setters (method chaining) |
| 281 | 7 | `public BuildingsTemplate setCashCost(double cashCost)` |  |
| 289 | 4 | `public BuildingsTemplate setConstructionPoints(int constructionPoints)` |  |
| 294 | 4 | `public BuildingsTemplate setCapacity(int capacity)` |  |
| 299 | 6 | `public BuildingsTemplate setUpkeep(double upkeep)` |  |
| 306 | 4 | `public BuildingsTemplate setConstructionMaterials(int constructionMaterials)` |  |
| 311 | 4 | `public BuildingsTemplate setElectricityConsumption(int electricityConsumption)` |  |
| 316 | 4 | `public BuildingsTemplate setWaterConsumption(double waterConsumption)` |  |
| 321 | 4 | `public BuildingsTemplate setLandSqFt(double landSqFt)` |  |
| 326 | 4 | `public BuildingsTemplate setRoadLoad(double roadLoad)` |  |
| 331 | 4 | `public BuildingsTemplate setCoverage(int coverage)` |  |
| 336 | 4 | `public BuildingsTemplate setProduction1(double production1)` |  |
| 341 | 4 | `public BuildingsTemplate setProduction2(double production2)` |  |
| 346 | 4 | `public BuildingsTemplate setProductionModifier1(double productionModifier1)` |  |
| 351 | 4 | `public BuildingsTemplate setProductionModifier2(double productionModifier2)` |  |
| 356 | 4 | `public BuildingsTemplate setNationalized(boolean nationalized)` |  |
| 361 | 4 | `public BuildingsTemplate setId(int id)` |  |
| 366 | 4 | `public BuildingsTemplate setCare(CareType care)` |  |
| 371 | 4 | `public BuildingsTemplate setSafety(SafetyType safety)` |  |
| 376 | 4 | `public BuildingsTemplate setTeaches(EducationType teaches)` |  |
| 381 | 4 | `public BuildingsTemplate setSource(Source source)` |  |
| 387 | 3 | `public Source getSource()` | What it draws from: FRESH for every building but a desalination plant (see the field's note). |
| 392 | 3 | `public boolean isSeaWater()` | A water works that draws the sea: a Desalination Plant (0.7.59). |
| 397 | 3 | `public boolean isFreshWater()` | A water works that draws fresh water, held to the city's limit: a Water Treatment Plant. |
| 402 | 3 | `public double getCashCost()` | getters |
| 406 | 3 | `public int getConstructionPoints()` |  |
| 410 | 3 | `public int getDwellings()` |  |
| 414 | 4 | `public BuildingsTemplate setDwellings(int dwellings)` |  |
| 435 | 5 | `public int homeSize()` | How big a household one of these units takes, in people. |
| 449 | 4 | `public boolean adultsOnly()` | True for a flat too small to put a child in. |
| 454 | 3 | `public int getCapacity()` |  |
| 458 | 3 | `public double getUpkeep()` |  |
| 462 | 3 | `public int getConstructionMaterials()` |  |
| 466 | 3 | `public int getElectricityConsumption()` |  |
| 470 | 3 | `public double getWaterConsumption()` |  |
| 474 | 3 | `public double getLandSqFt()` |  |
| 478 | 3 | `public double getRoadLoad()` |  |

### WHAT A MODE IS GOOD AT (2026-09-16) (lines 482-545)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 4 | `public BuildingsTemplate setFreightGrade(double grade)` | How much of this road is built for lorries, 0 to 1. |
| 509 | 1 | `public double getFreightGrade()` |  |
| 519 | 4 | `public BuildingsTemplate setTransitCapacity(double riders)` | Commuter journeys a month this carries OFF the road, if it is transit. |
| 524 | 1 | `public double getTransitCapacity()` |  |
| 527 | 1 | `public boolean isTransit()` | True for a building whose whole purpose is carrying people. |
| 539 | 4 | `public BuildingsTemplate setRailCapacity(double tonnes)` | Tonnes a month this can haul across the city boundary, if it is rail. |
| 544 | 1 | `public double getRailCapacity()` |  |

### WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) (lines 546-792)

| line | len | member | says |
|---:|---:|---|---|
| 600 | 42 | `public double loadOf(Traffic stream)` | This building's road load, split by what is actually moving. |
| 643 | 3 | `public int getCoverage()` |  |
| 647 | 3 | `public double getProduction1()` |  |
| 651 | 3 | `public double getProduction2()` |  |
| 655 | 3 | `public double getProductionModifier1()` |  |
| 659 | 3 | `public double getProductionModifier2()` |  |
| 663 | 3 | `public boolean getNationalized()` |  |
| 666 | 3 | `public int getJobs(JobType type)` |  |
| 670 | 3 | `public String getName()` |  |
| 674 | 3 | `public BuildingType getCategory()` |  |
| 678 | 3 | `public int getId()` |  |
| 682 | 3 | `public CareType getCare()` |  |
| 687 | 4 | `public BuildingsTemplate setRequiresLicence(JobType licence)` | The licence this building's practice needs, or null. |
| 693 | 1 | `public JobType getRequiresLicence()` | The licence this building's practice needs, or null if it needs none. |
| 696 | 3 | `public int getLicensedPosts()` | How many posts here need that licence - what the gate is measured against. |
| 700 | 3 | `public SafetyType getSafety()` |  |
| 704 | 3 | `public EducationType getTeaches()` |  |
| 712 | 5 | `public int getTotalJobs()` | sum of all jobs |
| 731 | 6 | `public void redenominate(double scale)` | What a building costs and what it costs to run, in the new unit. |
| 769 | 9 | `public void seedConstants(double unit)` | Re-seeds this template's price at a given unit (since 0.7.42, the unit over the expected price level). |
| 785 | 6 | `private void rememberFounding()` | ...and a reform moves the founding figure with it, so that a LATER re-seed at the new unit lands in the same place. |


# BuildingsTemplate.java - 730 lines · 75 methods · 2 constants · model

`ham/citybuildersim/BuildingsTemplate.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (14), [JobType](JobType.md) (8), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [SafetyType](SafetyType.md) (5), [BuildingType](BuildingType.md) (5), [Traffic](Traffic.md) (4)

**Used by (74):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingInstance](BuildingInstance.md), [BuildingManager](BuildingManager.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CentralBankCheck](CentralBankCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 102 | A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) |
| 126 | WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). |
| 438 | WHAT A MODE IS GOOD AT (2026-09-16) |
| 502 | WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 543 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 546 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 122 | `private JobType requiresLicence` |  |
| 124 | `private int id` |  |
| 146 | `private String sector` |  |
| 147 | `private final java.util.Map<Good, Double> makes` |  |
| 148 | `private final java.util.Map<Good, Double> uses` |  |
| 149 | `private double stock` |  |
| 205 | `int[] jobsByEducation` | enums |
| 206 | `private BuildingType category` |  |
| 447 | `private double freightGrade` |  |
| 448 | `private double transitCapacity` |  |
| 485 | `private double railCapacity` |  |
| 704 | `private double foundingCashCost` | What this building cost when the catalogue was read, before any currency reform - so a reformed city that reloads can divide it again. |
| 705 | `private double foundingUpkeep` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 704 | **type** `public class BuildingsTemplate` | One kind of building, and what it costs to put up. |

### A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) (lines 102-125)

### WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). (lines 126-437)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 4 | `public BuildingsTemplate setSector(String sector)` |  |
| 156 | 4 | `public BuildingsTemplate makes(Good good, double unitsAMonth)` |  |
| 161 | 4 | `public BuildingsTemplate uses(Good good, double unitsAMonth)` |  |
| 166 | 4 | `public BuildingsTemplate setStock(double units)` |  |
| 172 | 1 | `public String getSector()` | The owning sector's key, or "" for a building nobody in the private sector owns. |
| 174 | 1 | `public boolean isOwnedBySector()` |  |
| 183 | 3 | `public static boolean isBilledForUtilities(BuildingsTemplate t)` | Whether a building's power and water are invoiced to anybody: the business buildings a sector owns. |
| 188 | 1 | `public double makes(Good good)` | Units of a good this building makes a month at nameplate. |
| 191 | 1 | `public double uses(Good good)` | Units of a good this building uses a month at nameplate. |
| 193 | 1 | `public java.util.Map<Good, Double> goodsMade()` |  |
| 194 | 1 | `public java.util.Map<Good, Double> goodsUsed()` |  |
| 197 | 1 | `public double getStock()` | Room for what it holds, in units. |
| 200 | 1 | `public double stocks(Good good)` | The same, asked per good: a building has one warehouse and it holds whatever the building holds. |
| 209 | 4 | `public BuildingsTemplate(String name, BuildingType category)` | barebones constructor |
| 257 | 4 | `public BuildingsTemplate setJobs(JobType type, int number)` | setters (method chaining) |
| 262 | 4 | `public BuildingsTemplate setCashCost(double cashCost)` |  |
| 267 | 4 | `public BuildingsTemplate setConstructionPoints(int constructionPoints)` |  |
| 272 | 4 | `public BuildingsTemplate setCapacity(int capacity)` |  |
| 277 | 4 | `public BuildingsTemplate setUpkeep(double upkeep)` |  |
| 282 | 4 | `public BuildingsTemplate setConstructionMaterials(int constructionMaterials)` |  |
| 287 | 4 | `public BuildingsTemplate setElectricityConsumption(int electricityConsumption)` |  |
| 292 | 4 | `public BuildingsTemplate setWaterConsumption(double waterConsumption)` |  |
| 297 | 4 | `public BuildingsTemplate setLandSqFt(double landSqFt)` |  |
| 302 | 4 | `public BuildingsTemplate setRoadLoad(double roadLoad)` |  |
| 307 | 4 | `public BuildingsTemplate setCoverage(int coverage)` |  |
| 312 | 4 | `public BuildingsTemplate setProduction1(double production1)` |  |
| 317 | 4 | `public BuildingsTemplate setProduction2(double production2)` |  |
| 322 | 4 | `public BuildingsTemplate setProductionModifier1(double productionModifier1)` |  |
| 327 | 4 | `public BuildingsTemplate setProductionModifier2(double productionModifier2)` |  |
| 332 | 4 | `public BuildingsTemplate setNationalized(boolean nationalized)` |  |
| 337 | 4 | `public BuildingsTemplate setId(int id)` |  |
| 342 | 4 | `public BuildingsTemplate setCare(CareType care)` |  |
| 347 | 4 | `public BuildingsTemplate setSafety(SafetyType safety)` |  |
| 352 | 4 | `public BuildingsTemplate setTeaches(EducationType teaches)` |  |
| 358 | 3 | `public double getCashCost()` | getters |
| 362 | 3 | `public int getConstructionPoints()` |  |
| 366 | 3 | `public int getDwellings()` |  |
| 370 | 4 | `public BuildingsTemplate setDwellings(int dwellings)` |  |
| 391 | 5 | `public int homeSize()` | How big a household one of these units takes, in people. |
| 405 | 4 | `public boolean adultsOnly()` | True for a flat too small to put a child in. |
| 410 | 3 | `public int getCapacity()` |  |
| 414 | 3 | `public double getUpkeep()` |  |
| 418 | 3 | `public int getConstructionMaterials()` |  |
| 422 | 3 | `public int getElectricityConsumption()` |  |
| 426 | 3 | `public double getWaterConsumption()` |  |
| 430 | 3 | `public double getLandSqFt()` |  |
| 434 | 3 | `public double getRoadLoad()` |  |

### WHAT A MODE IS GOOD AT (2026-09-16) (lines 438-501)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 4 | `public BuildingsTemplate setFreightGrade(double grade)` | How much of this road is built for lorries, 0 to 1. |
| 465 | 1 | `public double getFreightGrade()` |  |
| 475 | 4 | `public BuildingsTemplate setTransitCapacity(double riders)` | Commuter journeys a month this carries OFF the road, if it is transit. |
| 480 | 1 | `public double getTransitCapacity()` |  |
| 483 | 1 | `public boolean isTransit()` | True for a building whose whole purpose is carrying people. |
| 495 | 4 | `public BuildingsTemplate setRailCapacity(double tonnes)` | Tonnes a month this can haul across the city boundary, if it is rail. |
| 500 | 1 | `public double getRailCapacity()` |  |

### WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) (lines 502-730)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 42 | `public double loadOf(Traffic stream)` | This building's road load, split by what is actually moving. |
| 599 | 3 | `public int getCoverage()` |  |
| 603 | 3 | `public double getProduction1()` |  |
| 607 | 3 | `public double getProduction2()` |  |
| 611 | 3 | `public double getProductionModifier1()` |  |
| 615 | 3 | `public double getProductionModifier2()` |  |
| 619 | 3 | `public boolean getNationalized()` |  |
| 622 | 3 | `public int getJobs(JobType type)` |  |
| 626 | 3 | `public String getName()` |  |
| 630 | 3 | `public BuildingType getCategory()` |  |
| 634 | 3 | `public int getId()` |  |
| 638 | 3 | `public CareType getCare()` |  |
| 643 | 4 | `public BuildingsTemplate setRequiresLicence(JobType licence)` | The licence this building's practice needs, or null. |
| 649 | 1 | `public JobType getRequiresLicence()` | The licence this building's practice needs, or null if it needs none. |
| 652 | 3 | `public int getLicensedPosts()` | How many posts here need that licence - what the gate is measured against. |
| 656 | 3 | `public SafetyType getSafety()` |  |
| 660 | 3 | `public EducationType getTeaches()` |  |
| 668 | 5 | `public int getTotalJobs()` | sum of all jobs |
| 687 | 5 | `public void redenominate(double scale)` | What a building costs and what it costs to run, in the new unit. |
| 708 | 8 | `public void seedConstants(double unit)` | Re-seeds this template's price at a given unit. |
| 723 | 6 | `private void rememberFounding()` | ...and a reform moves the founding figure with it, so that a LATER re-seed at the new unit lands in the same place. |


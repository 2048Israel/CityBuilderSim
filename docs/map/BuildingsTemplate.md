# BuildingsTemplate.java - 911 lines · 97 methods · 2 constants · model

`ham/citybuildersim/BuildingsTemplate.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (14), [JobType](JobType.md) (8), [BuildingType](BuildingType.md) (8), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [SafetyType](SafetyType.md) (5), [Traffic](Traffic.md) (4), [RefineryFlow](RefineryFlow.md) (3), [Ports](Ports.md) (3)

**Used by (116):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingInstance](BuildingInstance.md), [BuildingManager](BuildingManager.md), [BuildingVisual](BuildingVisual.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [Construction](Construction.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PlanCheck](PlanCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SpreadPlanner](SpreadPlanner.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 104 | WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) |
| 123 | A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) |
| 147 | WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). |
| 223 | · A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; runs/spec-oil.md 2.3) |
| 248 | · A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) |
| 265 | · A SEA TERMINAL'S BERTH (0.7.86, batch O9; runs/spec-oil.md 2.9) |
| 293 | · THE OIL AT SEA (0.7.91, batch O10; runs/spec-oil.md 2.7, 2.11) |
| 601 | WHAT A MODE IS GOOD AT (2026-09-16) |
| 665 | WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 119 | `BuildingsTemplate.Source.FRESH` |  |
| 119 | `BuildingsTemplate.Source.SEA` |  |
| 305 | `BuildingsTemplate.Offshore.PLATFORM` |  |
| 305 | `BuildingsTemplate.Offshore.WELL` |  |
| 305 | `BuildingsTemplate.Offshore.PIPELINE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 706 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 709 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 84 | `private CareType care` | What a healthcare building is for; NONE for everything else. |
| 95 | `private EducationType teaches` | What a school teaches; NONE for everything else. |
| 102 | `private SafetyType safety` | What a safety building is for - officers or cells - and NONE for everything else. |
| 121 | `private Source source` |  |
| 143 | `private JobType requiresLicence` |  |
| 145 | `private int id` |  |
| 167 | `private String sector` |  |
| 168 | `private final java.util.Map<Good, Double> makes` |  |
| 169 | `private final java.util.Map<Good, Double> uses` |  |
| 170 | `private double stock` |  |
| 232 | `private ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit` |  |
| 233 | `private double feedPerMonth` |  |
| 254 | `private double pumpLitres` |  |
| 271 | `private Ports.Cargo berthCargo` |  |
| 272 | `private double berthTonnesAYear` |  |
| 307 | `private Offshore offshore` |  |
| 308 | `private int platformSlots` |  |
| 309 | `private double onshoreCashPerKm` |  |
| 343 | `int[] jobsByEducation` | enums |
| 344 | `private BuildingType category` |  |
| 610 | `private double freightGrade` |  |
| 611 | `private double transitCapacity` |  |
| 648 | `private double railCapacity` |  |
| 868 | `private double foundingCashCost` | What this building cost when the catalogue was read, before any currency reform - so a reformed city that reloads can divide it again. |
| 869 | `private double foundingUpkeep` |  |
| 885 | `private double struckAt` | What the founding figures were last divided by to give today's: the unit over the expected price level at the last re-seed, over any reform since. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 885 | **type** `public class BuildingsTemplate` | One kind of building, and what it costs to put up. |

### WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) (lines 104-122)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 1 | **type** `public enum Source` | What a water works draws: FRESH water, held to the city's limit, or the SEA, which is not. |

### A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) (lines 123-146)

### WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). (lines 147-222)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 4 | `public BuildingsTemplate setSector(String sector)` |  |
| 177 | 4 | `public BuildingsTemplate makes(Good good, double unitsAMonth)` |  |
| 182 | 4 | `public BuildingsTemplate uses(Good good, double unitsAMonth)` |  |
| 187 | 4 | `public BuildingsTemplate setStock(double units)` |  |
| 193 | 1 | `public String getSector()` | The owning sector's key, or "" for a building nobody in the private sector owns. |
| 195 | 1 | `public boolean isOwnedBySector()` |  |
| 204 | 3 | `public static boolean isBilledForUtilities(BuildingsTemplate t)` | Whether a building's power and water are invoiced to anybody: the business buildings a sector owns. |
| 209 | 1 | `public double makes(Good good)` | Units of a good this building makes a month at nameplate. |
| 212 | 1 | `public double uses(Good good)` | Units of a good this building uses a month at nameplate. |
| 214 | 1 | `public java.util.Map<Good, Double> goodsMade()` |  |
| 215 | 1 | `public java.util.Map<Good, Double> goodsUsed()` |  |
| 218 | 1 | `public double getStock()` | Room for what it holds, in units. |
| 221 | 1 | `public double stocks(Good good)` | The same, asked per good: a building has one warehouse and it holds whatever the building holds. |

### A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; runs/spec-oil.md 2.3) (lines 223-247)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 5 | `public BuildingsTemplate setRefineryUnit(ham.citybuildersim.sectors.RefineryFlow.Kind unit, double litresAMonth)` | Makes this a refinery's conversion unit of a kind, taking `litresAMonth` of its feed stream at nameplate. |
| 243 | 1 | `public ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit()` | The kind of conversion unit this is, or null for every building that is not one. |
| 246 | 1 | `public double feedPerMonth()` | Litres a month of its feed stream a conversion unit takes at nameplate; 0 for every other building. |

### A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) (lines 248-264)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 4 | `public BuildingsTemplate setPumpLitres(double litresAMonth)` | Makes this a filling station that sells `litresAMonth` at its typical throughput. |
| 263 | 1 | `public double pumpLitres()` | Litres a month a filling station sells at its typical throughput; 0 for every other building. |

### A SEA TERMINAL'S BERTH (0.7.86, batch O9; runs/spec-oil.md 2.9) (lines 265-292)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 5 | `public BuildingsTemplate setBerth(Ports.Cargo cargo, double tonnesAYear)` | Makes this a berth of a kind of cargo, handling `tonnesAYear` of it. |
| 282 | 1 | `public Ports.Cargo berthCargo()` | The kind of cargo a terminal's berth handles, or null for every building that is not a berth. |
| 285 | 1 | `public double berthTonnesAYear()` | Tonnes a year a terminal's berth handles; 0 for every other building. |
| 288 | 1 | `public boolean isPort()` | Whether this is a sea terminal (0.7.86): a PORTS building with a berth. |
| 291 | 1 | `public boolean needsCoast()` | Whether an order for this has to stand on owned sea: a desalination plant (0.7.59) and a sea terminal (0.7.86). |

### THE OIL AT SEA (0.7.91, batch O10; runs/spec-oil.md 2.7, 2.11) (lines 293-600)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 1 | **type** `public enum Offshore` | What a building at sea is: a platform's jacket, a well in one of its slots, or a kilometre of crude pipeline. |
| 312 | 6 | `public BuildingsTemplate setOffshore(Offshore kind, int slots, double onshoreCash)` | Makes this one of the oil buildings at sea: a PLATFORM with `slots` for wells, a WELL, or a PIPELINE whose kilometre on land costs `onshoreCash` (thousands). |
| 320 | 1 | `public Offshore offshore()` | Which of the buildings at sea this is, or null for every building on the ground. |
| 323 | 1 | `public boolean standsAtSea()` | Whether this stands at sea (an Offshore Platform, a Platform Well, a Crude Pipeline): not on the city's dry ground, nor drawn on its land. |
| 326 | 1 | `public boolean isPlatform()` | Whether this is an offshore platform's jacket. |
| 329 | 1 | `public boolean isPlatformWell()` | Whether this is a well in a platform's slot. |
| 332 | 1 | `public boolean isPipeline()` | Whether this is a kilometre of crude pipeline. |
| 335 | 1 | `public int platformSlots()` | The wells a platform's jacket holds: 12 for the Offshore Platform; 0 for every other building. |
| 338 | 1 | `public double onshoreCashPerKm()` | A pipeline's kilometre on land, in thousands (its own cash is a kilometre at sea); 0 for every other building. |
| 347 | 4 | `public BuildingsTemplate(String name, BuildingType category)` | barebones constructor |
| 395 | 4 | `public BuildingsTemplate setJobs(JobType type, int number)` | setters (method chaining) |
| 400 | 7 | `public BuildingsTemplate setCashCost(double cashCost)` |  |
| 408 | 4 | `public BuildingsTemplate setConstructionPoints(int constructionPoints)` |  |
| 413 | 4 | `public BuildingsTemplate setCapacity(int capacity)` |  |
| 418 | 6 | `public BuildingsTemplate setUpkeep(double upkeep)` |  |
| 425 | 4 | `public BuildingsTemplate setConstructionMaterials(int constructionMaterials)` |  |
| 430 | 4 | `public BuildingsTemplate setElectricityConsumption(int electricityConsumption)` |  |
| 435 | 4 | `public BuildingsTemplate setWaterConsumption(double waterConsumption)` |  |
| 440 | 4 | `public BuildingsTemplate setLandSqFt(double landSqFt)` |  |
| 445 | 4 | `public BuildingsTemplate setRoadLoad(double roadLoad)` |  |
| 450 | 4 | `public BuildingsTemplate setCoverage(int coverage)` |  |
| 455 | 4 | `public BuildingsTemplate setProduction1(double production1)` |  |
| 460 | 4 | `public BuildingsTemplate setProduction2(double production2)` |  |
| 465 | 4 | `public BuildingsTemplate setProductionModifier1(double productionModifier1)` |  |
| 470 | 4 | `public BuildingsTemplate setProductionModifier2(double productionModifier2)` |  |
| 475 | 4 | `public BuildingsTemplate setNationalized(boolean nationalized)` |  |
| 480 | 4 | `public BuildingsTemplate setId(int id)` |  |
| 485 | 4 | `public BuildingsTemplate setCare(CareType care)` |  |
| 490 | 4 | `public BuildingsTemplate setSafety(SafetyType safety)` |  |
| 495 | 4 | `public BuildingsTemplate setTeaches(EducationType teaches)` |  |
| 500 | 4 | `public BuildingsTemplate setSource(Source source)` |  |
| 506 | 3 | `public Source getSource()` | What it draws from: FRESH for every building but a desalination plant (see the field's note). |
| 511 | 3 | `public boolean isSeaWater()` | A water works that draws the sea: a Desalination Plant (0.7.59). |
| 516 | 3 | `public boolean isFreshWater()` | A water works that draws fresh water, held to the city's limit: a Water Treatment Plant. |
| 521 | 3 | `public double getCashCost()` | getters |
| 525 | 3 | `public int getConstructionPoints()` |  |
| 529 | 3 | `public int getDwellings()` |  |
| 533 | 4 | `public BuildingsTemplate setDwellings(int dwellings)` |  |
| 554 | 5 | `public int homeSize()` | How big a household one of these units takes, in people. |
| 568 | 4 | `public boolean adultsOnly()` | True for a flat too small to put a child in. |
| 573 | 3 | `public int getCapacity()` |  |
| 577 | 3 | `public double getUpkeep()` |  |
| 581 | 3 | `public int getConstructionMaterials()` |  |
| 585 | 3 | `public int getElectricityConsumption()` |  |
| 589 | 3 | `public double getWaterConsumption()` |  |
| 593 | 3 | `public double getLandSqFt()` |  |
| 597 | 3 | `public double getRoadLoad()` |  |

### WHAT A MODE IS GOOD AT (2026-09-16) (lines 601-664)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 4 | `public BuildingsTemplate setFreightGrade(double grade)` | How much of this road is built for lorries, 0 to 1. |
| 628 | 1 | `public double getFreightGrade()` |  |
| 638 | 4 | `public BuildingsTemplate setTransitCapacity(double riders)` | Commuter journeys a month this carries OFF the road, if it is transit. |
| 643 | 1 | `public double getTransitCapacity()` |  |
| 646 | 1 | `public boolean isTransit()` | True for a building whose whole purpose is carrying people. |
| 658 | 4 | `public BuildingsTemplate setRailCapacity(double tonnes)` | Tonnes a month this can haul across the city boundary, if it is rail. |
| 663 | 1 | `public double getRailCapacity()` |  |

### WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) (lines 665-911)

| line | len | member | says |
|---:|---:|---|---|
| 719 | 42 | `public double loadOf(Traffic stream)` | This building's road load, split by what is actually moving. |
| 762 | 3 | `public int getCoverage()` |  |
| 766 | 3 | `public double getProduction1()` |  |
| 770 | 3 | `public double getProduction2()` |  |
| 774 | 3 | `public double getProductionModifier1()` |  |
| 778 | 3 | `public double getProductionModifier2()` |  |
| 782 | 3 | `public boolean getNationalized()` |  |
| 785 | 3 | `public int getJobs(JobType type)` |  |
| 789 | 3 | `public String getName()` |  |
| 793 | 3 | `public BuildingType getCategory()` |  |
| 797 | 3 | `public int getId()` |  |
| 801 | 3 | `public CareType getCare()` |  |
| 806 | 4 | `public BuildingsTemplate setRequiresLicence(JobType licence)` | The licence this building's practice needs, or null. |
| 812 | 1 | `public JobType getRequiresLicence()` | The licence this building's practice needs, or null if it needs none. |
| 815 | 3 | `public int getLicensedPosts()` | How many posts here need that licence - what the gate is measured against. |
| 819 | 3 | `public SafetyType getSafety()` |  |
| 823 | 3 | `public EducationType getTeaches()` |  |
| 831 | 5 | `public int getTotalJobs()` | sum of all jobs |
| 850 | 6 | `public void redenominate(double scale)` | What a building costs and what it costs to run, in the new unit. |
| 888 | 9 | `public void seedConstants(double unit)` | Re-seeds this template's price at a given unit (since 0.7.42, the unit over the expected price level). |
| 904 | 6 | `private void rememberFounding()` | ...and a reform moves the founding figure with it, so that a LATER re-seed at the new unit lands in the same place. |


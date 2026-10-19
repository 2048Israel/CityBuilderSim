# BuildingsTemplate.java - 1,150 lines · 121 methods · 2 constants · model

`ham/citybuildersim/BuildingsTemplate.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (19), [BuildingType](BuildingType.md) (9), [JobType](JobType.md) (8), [Ports](Ports.md) (6), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [SafetyType](SafetyType.md) (5), [Traffic](Traffic.md) (4), [RefineryFlow](RefineryFlow.md) (3), [Deposit](Deposit.md) (3), [Resource](Resource.md) (2), [Sectors](Sectors.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (144):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingInstance](BuildingInstance.md), [BuildingManager](BuildingManager.md), [BuildingVisual](BuildingVisual.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [Construction](Construction.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [Freight](Freight.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OldMetal](OldMetal.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PlanCheck](PlanCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SpreadPlanner](SpreadPlanner.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 104 | WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) |
| 123 | A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) |
| 148 | WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). |
| 241 | · A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; docs/studies/spec-oil.md 2.3) |
| 266 | · A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) |
| 283 | · A ROAD'S OWN REPAIRS (0.7.103, batch P4; Jerus's A11, 2026-10-10) |
| 314 | · A SEA TERMINAL'S BERTH (0.7.86, batch O9; docs/studies/spec-oil.md 2.9) |
| 360 | · THE OIL AT SEA (0.7.91, batch O10; docs/studies/spec-oil.md 2.7, 2.11) |
| 407 | · THE SITE IT STANDS ON (0.7.110, batch I1; docs/studies/spec-materials.md 2.4, 2.5) |
| 442 | · THE POWER A WORKS MAKES ITSELF (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8) |
| 476 | · WHAT A WORKS BUYS ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5) |
| 498 | · THE OLD METAL A YARD TAKES (0.7.123, batch I4; docs/studies/spec-materials.md 2.5, 2.9) |
| 522 | · THE YEAR IT FIRST EXISTED (0.7.123, batch I4; research-design.md 2.1) |
| 544 | · THE RAW COAL A PREPARATION PLANT WASHES (0.7.126, batch I6; docs/studies/spec-materials.md 2.5, 2.10) |
| 827 | WHAT A MODE IS GOOD AT (2026-09-16) |
| 904 | WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 119 | `BuildingsTemplate.Source.FRESH` |  |
| 119 | `BuildingsTemplate.Source.SEA` |  |
| 372 | `BuildingsTemplate.Offshore.PLATFORM` |  |
| 372 | `BuildingsTemplate.Offshore.WELL` |  |
| 372 | `BuildingsTemplate.Offshore.PIPELINE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 945 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 948 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 144 | `private JobType requiresLicence` |  |
| 146 | `private int id` |  |
| 168 | `private String sector` |  |
| 169 | `private final java.util.Map<Good, Double> makes` |  |
| 170 | `private final java.util.Map<Good, Double> uses` |  |
| 171 | `private double stock` |  |
| 250 | `private ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit` |  |
| 251 | `private double feedPerMonth` |  |
| 272 | `private double pumpLitres` |  |
| 298 | `private double repairsPerYear` |  |
| 320 | `private Ports.Cargo berthCargo` |  |
| 321 | `private double berthTonnesAYear` |  |
| 343 | `private java.util.List<Ports.Ship> berthShips` | Which classes call (FR3 2026-10-19, docs/queue/FR3.md 2 and call 6): a berth may take only some of the classes of its kind ("port": {... |
| 374 | `private Offshore offshore` |  |
| 375 | `private int platformSlots` |  |
| 376 | `private double onshoreCashPerKm` |  |
| 426 | `private Site site` |  |
| 455 | `private double gen` |  |
| 487 | `private double consumables` |  |
| 506 | `private final java.util.Map<Good, Double> oldMetal` |  |
| 533 | `private int since` |  |
| 555 | `private double wash` |  |
| 569 | `int[] jobsByEducation` | enums |
| 570 | `private BuildingType category` |  |
| 836 | `private double freightGrade` |  |
| 837 | `private double transitCapacity` |  |
| 874 | `private double railCapacity` |  |
| 891 | `private double truckRoom` |  |
| 1107 | `private double foundingCashCost` | What this building cost when the catalogue was read, before any currency reform - so a reformed city that reloads can divide it again. |
| 1108 | `private double foundingUpkeep` |  |
| 1124 | `private double struckAt` | What the founding figures were last divided by to give today's: the unit over the expected price level at the last re-seed, over any reform since. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 1124 | **type** `public class BuildingsTemplate` | One kind of building, and what it costs to put up. |

### WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) (lines 104-122)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 1 | **type** `public enum Source` | What a water works draws: FRESH water, held to the city's limit, or the SEA, which is not. |

### A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) (lines 123-147)

### WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). (lines 148-240)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 4 | `public BuildingsTemplate setSector(String sector)` |  |
| 178 | 4 | `public BuildingsTemplate makes(Good good, double unitsAMonth)` |  |
| 183 | 4 | `public BuildingsTemplate uses(Good good, double unitsAMonth)` |  |
| 188 | 4 | `public BuildingsTemplate setStock(double units)` |  |
| 194 | 1 | `public String getSector()` | The owning sector's key, or "" for a building nobody in the private sector owns. |
| 196 | 1 | `public boolean isOwnedBySector()` |  |
| 211 | 3 | `public String getOperator()` | The key of the sector that WORKS the building and pays its posts: Freight for a sea terminal (BuildingType.PORTS), getSector() for every other building. |
| 222 | 3 | `public static boolean isBilledForUtilities(BuildingsTemplate t)` | Whether a building's power and water are invoiced to anybody: the business buildings a sector owns. |
| 227 | 1 | `public double makes(Good good)` | Units of a good this building makes a month at nameplate. |
| 230 | 1 | `public double uses(Good good)` | Units of a good this building uses a month at nameplate. |
| 232 | 1 | `public java.util.Map<Good, Double> goodsMade()` |  |
| 233 | 1 | `public java.util.Map<Good, Double> goodsUsed()` |  |
| 236 | 1 | `public double getStock()` | Room for what it holds, in units. |
| 239 | 1 | `public double stocks(Good good)` | The same, asked per good: a building has one warehouse and it holds whatever the building holds. |

### A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; docs/studies/spec-oil.md 2.3) (lines 241-265)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 5 | `public BuildingsTemplate setRefineryUnit(ham.citybuildersim.sectors.RefineryFlow.Kind unit, double litresAMonth)` | Makes this a refinery's conversion unit of a kind, taking `litresAMonth` of its feed stream at nameplate. |
| 261 | 1 | `public ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit()` | The kind of conversion unit this is, or null for every building that is not one. |
| 264 | 1 | `public double feedPerMonth()` | Litres a month of its feed stream a conversion unit takes at nameplate; 0 for every other building. |

### A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) (lines 266-282)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 4 | `public BuildingsTemplate setPumpLitres(double litresAMonth)` | Makes this a filling station that sells `litresAMonth` at its typical throughput. |
| 281 | 1 | `public double pumpLitres()` | Litres a month a filling station sells at its typical throughput; 0 for every other building. |

### A ROAD'S OWN REPAIRS (0.7.103, batch P4; Jerus's A11, 2026-10-10) (lines 283-313)

| line | len | member | says |
|---:|---:|---|---|
| 301 | 4 | `public BuildingsTemplate setRepairsPerYear(double shareAYear)` | Gives this building its own repairs a year, a share of what it cost to put up; 0 or less is none (the house rate). |
| 307 | 1 | `public boolean hasOwnRepairs()` | Whether this building has a repairs share of its own (0.7.103: the three road kinds). |
| 310 | 3 | `public double repairsPerYear()` | Its repairs a year, a share of what it cost to put up: its own, or RealEstate.MAINTENANCE_PER_YEAR. |

### A SEA TERMINAL'S BERTH (0.7.86, batch O9; docs/studies/spec-oil.md 2.9) (lines 314-359)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 5 | `public BuildingsTemplate setBerth(Ports.Cargo cargo, double tonnesAYear)` | Makes this a berth of a kind of cargo, handling `tonnesAYear` of it. |
| 331 | 1 | `public Ports.Cargo berthCargo()` | The kind of cargo a terminal's berth handles, or null for every building that is not a berth. |
| 334 | 1 | `public double berthTonnesAYear()` | Tonnes a year a terminal's berth handles; 0 for every other building. |
| 346 | 4 | `public BuildingsTemplate setBerthShips(java.util.List<Ports.Ship> ships)` | Limits this berth to these classes of its cargo; an empty list lifts the limit. |
| 352 | 1 | `public java.util.List<Ports.Ship> berthShips()` | The classes this berth takes, smallest first as the list gives them; empty when it takes every class of its kind (Ports.shipsOf() answers either way). |
| 355 | 1 | `public boolean isPort()` | Whether this is a sea terminal (0.7.86): a PORTS building with a berth. |
| 358 | 1 | `public boolean needsCoast()` | Whether an order for this has to stand on owned sea: a desalination plant (0.7.59) and a sea terminal (0.7.86). |

### THE OIL AT SEA (0.7.91, batch O10; docs/studies/spec-oil.md 2.7, 2.11) (lines 360-406)

| line | len | member | says |
|---:|---:|---|---|
| 372 | 1 | **type** `public enum Offshore` | What a building at sea is: a platform's jacket, a well in one of its slots, or a kilometre of crude pipeline. |
| 379 | 6 | `public BuildingsTemplate setOffshore(Offshore kind, int slots, double onshoreCash)` | Makes this one of the oil buildings at sea: a PLATFORM with `slots` for wells, a WELL, or a PIPELINE whose kilometre on land costs `onshoreCash` (thousands). |
| 387 | 1 | `public Offshore offshore()` | Which of the buildings at sea this is, or null for every building on the ground. |
| 390 | 1 | `public boolean standsAtSea()` | Whether this stands at sea (an Offshore Platform, a Platform Well, a Crude Pipeline): not on the city's dry ground, nor drawn on its land. |
| 393 | 1 | `public boolean isPlatform()` | Whether this is an offshore platform's jacket. |
| 396 | 1 | `public boolean isPlatformWell()` | Whether this is a well in a platform's slot. |
| 399 | 1 | `public boolean isPipeline()` | Whether this is a kilometre of crude pipeline. |
| 402 | 1 | `public int platformSlots()` | The wells a platform's jacket holds: 12 for the Offshore Platform; 0 for every other building. |
| 405 | 1 | `public double onshoreCashPerKm()` | A pipeline's kilometre on land, in thousands (its own cash is a kilometre at sea); 0 for every other building. |

### THE SITE IT STANDS ON (0.7.110, batch I1; docs/studies/spec-materials.md 2.4, 2.5) (lines 407-441)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 4 | **type** `public record Site(Resource resource, Deposit.FieldType type, int sites, double dig)` | The site a building stands on: its resource, the type of field it needs (null for ANY), the sites it takes and the tonnes of field a unit it makes takes out. |
| 423 | 1 | `public boolean any()` _(in BuildingsTemplate.Site)_ | Whether it stands on a field of any type. |
| 429 | 9 | `public BuildingsTemplate setSite(Resource resource, Deposit.FieldType type, int sites, double dig)` | Makes this stand on `sites` of a resource's sites of a type (null for ANY), taking `dig` of the field a unit it makes; a type not the resource's reads as ANY. |
| 440 | 1 | `public Site site()` | The site it stands on, or null for a building that stands on no resource's sites. |

### THE POWER A WORKS MAKES ITSELF (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8) (lines 442-475)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 4 | `public BuildingsTemplate setGen(double kw)` | Makes this building make `kw` of power a month itself; 0 or less is none. |
| 464 | 1 | `public double getGen()` | kW of power it makes itself; 0 for every building but the Coke Ovens, the Blast Furnace and the Oxygen Steel Shop (0.7.123). |
| 467 | 3 | `public double powerBought()` | kW it buys from the grid: its draw past what it makes - its whole draw, exactly, when it makes none. |
| 472 | 3 | `public double powerSold()` | kW it sells the grid: what it makes past its draw; nothing when it makes none. |

### WHAT A WORKS BUYS ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5) (lines 476-497)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 4 | `public BuildingsTemplate setConsumables(double worldThousands)` | Gives this building `worldThousands` a month of consumables at nameplate, in the world's money; 0 or less is none. |
| 496 | 1 | `public double getConsumables()` | Its consumables a month at nameplate, thousands of the world's money; 0 for every building but the materials chains' rows (the iron rows since 0.7.120, the integrated route's since 0.7.123). |

### THE OLD METAL A YARD TAKES (0.7.123, batch I4; docs/studies/spec-materials.md 2.5, 2.9) (lines 498-521)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 6 | `public BuildingsTemplate setOldMetal(Good metal, double tonnesAMonth)` | Makes this building take `tonnesAMonth` of the city's old metal of a kind at nameplate; 0 or less is none. |
| 517 | 1 | `public double oldMetal(Good metal)` | Tonnes of the city's old metal of this kind it takes a month at nameplate; 0 for every building but a yard. |
| 520 | 1 | `public java.util.Map<Good, Double> oldMetalTaken()` | Every kind of old metal it takes, tonnes a month at nameplate; empty for every building but a yard. |

### THE YEAR IT FIRST EXISTED (0.7.123, batch I4; research-design.md 2.1) (lines 522-543)

| line | len | member | says |
|---:|---:|---|---|
| 536 | 4 | `public BuildingsTemplate setSince(int year)` | Dates this building: the year it first existed, the start year (1900) for one that stood before it; 0 or less is undated. |
| 542 | 1 | `public int getSince()` | The year it first existed, or 0 for a row not yet dated. |

### THE RAW COAL A PREPARATION PLANT WASHES (0.7.126, batch I6; docs/studies/spec-materials.md 2.5, 2.10) (lines 544-826)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 4 | `public BuildingsTemplate setWash(double tonnesAMonth)` | Makes this a preparation plant washing `tonnesAMonth` of raw coal a month at nameplate; 0 or less is none. |
| 564 | 1 | `public double getWash()` | Tonnes of raw coal it washes a month at nameplate; 0 for every building but the Coal Preparation Plant (0.7.126). |
| 573 | 4 | `public BuildingsTemplate(String name, BuildingType category)` | barebones constructor |
| 621 | 4 | `public BuildingsTemplate setJobs(JobType type, int number)` | setters (method chaining) |
| 626 | 7 | `public BuildingsTemplate setCashCost(double cashCost)` |  |
| 634 | 4 | `public BuildingsTemplate setConstructionPoints(int constructionPoints)` |  |
| 639 | 4 | `public BuildingsTemplate setCapacity(int capacity)` |  |
| 644 | 6 | `public BuildingsTemplate setUpkeep(double upkeep)` |  |
| 651 | 4 | `public BuildingsTemplate setConstructionMaterials(int constructionMaterials)` |  |
| 656 | 4 | `public BuildingsTemplate setElectricityConsumption(int electricityConsumption)` |  |
| 661 | 4 | `public BuildingsTemplate setWaterConsumption(double waterConsumption)` |  |
| 666 | 4 | `public BuildingsTemplate setLandSqFt(double landSqFt)` |  |
| 671 | 4 | `public BuildingsTemplate setRoadLoad(double roadLoad)` |  |
| 676 | 4 | `public BuildingsTemplate setCoverage(int coverage)` |  |
| 681 | 4 | `public BuildingsTemplate setProduction1(double production1)` |  |
| 686 | 4 | `public BuildingsTemplate setProduction2(double production2)` |  |
| 691 | 4 | `public BuildingsTemplate setProductionModifier1(double productionModifier1)` |  |
| 696 | 4 | `public BuildingsTemplate setProductionModifier2(double productionModifier2)` |  |
| 701 | 4 | `public BuildingsTemplate setNationalized(boolean nationalized)` |  |
| 706 | 4 | `public BuildingsTemplate setId(int id)` |  |
| 711 | 4 | `public BuildingsTemplate setCare(CareType care)` |  |
| 716 | 4 | `public BuildingsTemplate setSafety(SafetyType safety)` |  |
| 721 | 4 | `public BuildingsTemplate setTeaches(EducationType teaches)` |  |
| 726 | 4 | `public BuildingsTemplate setSource(Source source)` |  |
| 732 | 3 | `public Source getSource()` | What it draws from: FRESH for every building but a desalination plant (see the field's note). |
| 737 | 3 | `public boolean isSeaWater()` | A water works that draws the sea: a Desalination Plant (0.7.59). |
| 742 | 3 | `public boolean isFreshWater()` | A water works that draws fresh water, held to the city's limit: a Water Treatment Plant. |
| 747 | 3 | `public double getCashCost()` | getters |
| 751 | 3 | `public int getConstructionPoints()` |  |
| 755 | 3 | `public int getDwellings()` |  |
| 759 | 4 | `public BuildingsTemplate setDwellings(int dwellings)` |  |
| 780 | 5 | `public int homeSize()` | How big a household one of these units takes, in people. |
| 794 | 4 | `public boolean adultsOnly()` | True for a flat too small to put a child in. |
| 799 | 3 | `public int getCapacity()` |  |
| 803 | 3 | `public double getUpkeep()` |  |
| 807 | 3 | `public int getConstructionMaterials()` |  |
| 811 | 3 | `public int getElectricityConsumption()` |  |
| 815 | 3 | `public double getWaterConsumption()` |  |
| 819 | 3 | `public double getLandSqFt()` |  |
| 823 | 3 | `public double getRoadLoad()` |  |

### WHAT A MODE IS GOOD AT (2026-09-16) (lines 827-903)

| line | len | member | says |
|---:|---:|---|---|
| 849 | 4 | `public BuildingsTemplate setFreightGrade(double grade)` | How much of this road is built for lorries, 0 to 1. |
| 854 | 1 | `public double getFreightGrade()` |  |
| 864 | 4 | `public BuildingsTemplate setTransitCapacity(double riders)` | Commuter journeys a month this carries OFF the road, if it is transit. |
| 869 | 1 | `public double getTransitCapacity()` |  |
| 872 | 1 | `public boolean isTransit()` | True for a building whose whole purpose is carrying people. |
| 884 | 4 | `public BuildingsTemplate setRailCapacity(double tonnes)` | Tonnes a month this can haul across the city boundary, if it is rail. |
| 889 | 1 | `public double getRailCapacity()` |  |
| 897 | 4 | `public BuildingsTemplate setTruckRoom(double trucks)` | Trucks a Truck Depot keeps (FR2): Freight's Haulage division's room; none for every other building. |
| 902 | 1 | `public double getTruckRoom()` |  |

### WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) (lines 904-1150)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 42 | `public double loadOf(Traffic stream)` | This building's road load, split by what is actually moving. |
| 1001 | 3 | `public int getCoverage()` |  |
| 1005 | 3 | `public double getProduction1()` |  |
| 1009 | 3 | `public double getProduction2()` |  |
| 1013 | 3 | `public double getProductionModifier1()` |  |
| 1017 | 3 | `public double getProductionModifier2()` |  |
| 1021 | 3 | `public boolean getNationalized()` |  |
| 1024 | 3 | `public int getJobs(JobType type)` |  |
| 1028 | 3 | `public String getName()` |  |
| 1032 | 3 | `public BuildingType getCategory()` |  |
| 1036 | 3 | `public int getId()` |  |
| 1040 | 3 | `public CareType getCare()` |  |
| 1045 | 4 | `public BuildingsTemplate setRequiresLicence(JobType licence)` | The licence this building's practice needs, or null. |
| 1051 | 1 | `public JobType getRequiresLicence()` | The licence this building's practice needs, or null if it needs none. |
| 1054 | 3 | `public int getLicensedPosts()` | How many posts here need that licence - what the gate is measured against. |
| 1058 | 3 | `public SafetyType getSafety()` |  |
| 1062 | 3 | `public EducationType getTeaches()` |  |
| 1070 | 5 | `public int getTotalJobs()` | sum of all jobs |
| 1089 | 6 | `public void redenominate(double scale)` | What a building costs and what it costs to run, in the new unit. |
| 1127 | 9 | `public void seedConstants(double unit)` | Re-seeds this template's price at a given unit (since 0.7.42, the unit over the expected price level). |
| 1143 | 6 | `private void rememberFounding()` | ...and a reform moves the founding figure with it, so that a LATER re-seed at the new unit lands in the same place. |


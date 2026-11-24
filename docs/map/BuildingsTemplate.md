# BuildingsTemplate.java - 1,693 lines · 186 methods · 2 constants · model

`ham/citybuildersim/BuildingsTemplate.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (29), [Recipe](Recipe.md) (20), [BuildingType](BuildingType.md) (13), [JobType](JobType.md) (12), [Equipment](Equipment.md) (7), [ResearchCatalog](ResearchCatalog.md) (6), [Ports](Ports.md) (6), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [SafetyType](SafetyType.md) (5), [PriceLevel](PriceLevel.md) (5), [RecipeChange](RecipeChange.md) (4), [HousingRecipe](HousingRecipe.md) (4), [PowerDispatch](PowerDispatch.md) (4), [Traffic](Traffic.md) (4), [RefineryFlow](RefineryFlow.md) (3), [Deposit](Deposit.md) (3), [BridgeWorks](BridgeWorks.md) (3), [Resource](Resource.md) (2), [Sectors](Sectors.md) (1), [RealEstate](RealEstate.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [ConstructionControl](ConstructionControl.md) (1), [RecipeChanges](RecipeChanges.md) (1), [Research](Research.md) (1)

**Used by (243):** [AdviceValueOracle](AdviceValueOracle.md), [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingInstance](BuildingInstance.md), [BuildingManager](BuildingManager.md), [BuildingStackCheck](BuildingStackCheck.md), [BuildingVisual](BuildingVisual.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [Construction](Construction.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [CopperBufferCheck](CopperBufferCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [CrimeCheck](CrimeCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [DistressBleedCheck](DistressBleedCheck.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [ER1Check](ER1Check.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FB13Check](FB13Check.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [Fitout](Fitout.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [FoodIndustry](FoodIndustry.md), [FoodIndustryCheck](FoodIndustryCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GapOrderCheck](GapOrderCheck.md), [GatewayCheck](GatewayCheck.md), [GdpCheck](GdpCheck.md), [GoldMining](GoldMining.md), [GoldMiningCheck](GoldMiningCheck.md), [GroundInputCheck](GroundInputCheck.md), [HarbourRenders](HarbourRenders.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [HousingInventory](HousingInventory.md), [HousingInventoryCheck](HousingInventoryCheck.md), [HousingRecipe](HousingRecipe.md), [HydroCheck](HydroCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [IslandRailCheck](IslandRailCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [MedicineCheck](MedicineCheck.md), [MiningCheck](MiningCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [ModernRowsCheck](ModernRowsCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [NewUnit](NewUnit.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OldMetal](OldMetal.md), [OrderDoorCheck](OrderDoorCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PowerDispatchCheck](PowerDispatchCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PowerView](PowerView.md), [PowerViewCheck](PowerViewCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [ProcessingSector](ProcessingSector.md), [PublicLicenceCheck](PublicLicenceCheck.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Recipe](Recipe.md), [RecipeChange](RecipeChange.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [ResearchCheck](ResearchCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [ShortSellingCheck](ShortSellingCheck.md), [SlopeCheck](SlopeCheck.md), [SpreadPlanner](SpreadPlanner.md), [StaffingDrawCheck](StaffingDrawCheck.md), [StaffingTieCheck](StaffingTieCheck.md), [StrategicReserve](StrategicReserve.md), [SubstationCheck](SubstationCheck.md), [Substations](Substations.md), [SulphideLeachCheck](SulphideLeachCheck.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UndemandedFeedCheck](UndemandedFeedCheck.md), [UserInterface](UserInterface.md), [UtilitiesHandler](UtilitiesHandler.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WaterPowerCheck](WaterPowerCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [WorldMarketCheck](WorldMarketCheck.md), [YearBookCheck](YearBookCheck.md), [YoungBankCheck](YoungBankCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 111 | WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) |
| 130 | A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) |
| 155 | WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). |
| 258 | · A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; docs/studies/spec-oil.md 2.3) |
| 283 | · A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) |
| 300 | · A ROAD'S OWN REPAIRS (0.7.103, batch P4; Jerus's A11, 2026-10-10) |
| 356 | · A SEA TERMINAL'S BERTH (0.7.86, batch O9; docs/studies/spec-oil.md 2.9) |
| 402 | · THE OIL AT SEA (0.7.91, batch O10; docs/studies/spec-oil.md 2.7, 2.11) |
| 449 | · THE SITE IT STANDS ON (0.7.110, batch I1; docs/studies/spec-materials.md 2.4, 2.5) |
| 484 | · THE POWER A WORKS MAKES ITSELF (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8) |
| 518 | · WHAT A WORKS BUYS ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5) |
| 540 | · THE OLD METAL A YARD TAKES (0.7.123, batch I4; docs/studies/spec-materials.md 2.5, 2.9) |
| 564 | · THE EQUIPMENT IT CAN BE FITTED WITH (R1, 2026-11-05; research-design.md 2.1-2.2) |
| 588 | · THE WAYS IT RUNS (R1c, 2026-11-05; research-design.md 2.1) |
| 790 | · WHEN IT FIRST EXISTED (0.7.123, batch I4; R2, 2026-11-05; research-design.md 2.1, 7) |
| 852 | · THE RAW COAL A PREPARATION PLANT WASHES (0.7.126, batch I6; docs/studies/spec-materials.md 2.5, 2.10) |
| 1189 | WHAT A MODE IS GOOD AT (2026-09-16) |
| 1316 | THE POWER ROWS (PW1; spec-electricity 2.3, 2.5) |
| 1371 | · THE COOLING WATER AND THE BUILD-TIME FLOOR (PW2, 2026-11-23; spec-electricity 2.3) |
| 1439 | WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 126 | `BuildingsTemplate.Source.FRESH` |  |
| 126 | `BuildingsTemplate.Source.SEA` |  |
| 414 | `BuildingsTemplate.Offshore.PLATFORM` |  |
| 414 | `BuildingsTemplate.Offshore.WELL` |  |
| 414 | `BuildingsTemplate.Offshore.PIPELINE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1480 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 1483 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 109 | `private ResearchCatalog.Field researches` | The field a Research Lab researches (R4, buildings.json's "researches"); null for everything else. |
| 128 | `private Source source` |  |
| 151 | `private JobType requiresLicence` |  |
| 153 | `private int id` |  |
| 175 | `private String sector` |  |
| 178 | `private volatile java.util.Map<Good, Double> makes` | Not final since R1c: runRecipes() swaps in whole new maps (a screen's thread iterating goodsMade() keeps the map it was given), where a clear-and-fill would break its iteration. |
| 179 | `private volatile java.util.Map<Good, Double> uses` |  |
| 180 | `private double stock` |  |
| 267 | `private ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit` |  |
| 268 | `private double feedPerMonth` |  |
| 289 | `private double pumpLitres` |  |
| 315 | `private double repairsPerYear` |  |
| 362 | `private Ports.Cargo berthCargo` |  |
| 363 | `private double berthTonnesAYear` |  |
| 385 | `private java.util.List<Ports.Ship> berthShips` | Which classes call (FR3 2026-10-19, docs/queue/FR3.md 2 and call 6): a berth may take only some of the classes of its kind ("port": {... |
| 416 | `private Offshore offshore` |  |
| 417 | `private int platformSlots` |  |
| 418 | `private double onshoreCashPerKm` |  |
| 468 | `private Site site` |  |
| 497 | `private double gen` |  |
| 529 | `private double consumables` |  |
| 548 | `private final java.util.Map<Good, Double> oldMetal` |  |
| 571 | `private java.util.List<Equipment> equipment` |  |
| 605 | `private java.util.List<Recipe> recipes` |  |
| 606 | `private String recipePieceId` |  |
| 607 | `private java.util.List<RecipeChange> recipeChanges` |  |
| 608 | `private double recipeProduction, recipeLand` |  |
| 609 | `private Site recipeSite` |  |
| 610 | `private double steamRailShare` |  |
| 611 | `private double crackerPetrol` |  |
| 655 | `private volatile String runsFitted, runsUnfitted` |  |
| 807 | `private int since` |  |
| 808 | `private String sinceText` |  |
| 863 | `private double wash` |  |
| 877 | `int[] jobsByEducation` | enums |
| 878 | `private BuildingType category` |  |
| 1101 | `private HousingRecipe housingRecipe` |  |
| 1198 | `private double freightGrade` |  |
| 1199 | `private double transitCapacity` |  |
| 1245 | `private double railCapacity` |  |
| 1262 | `private double stationTonnes` |  |
| 1286 | `private boolean siding` |  |
| 1301 | `private boolean hydro` |  |
| 1319 | `private double nameplate` | A power station's nameplate, kW: what it can put out in an hour, past its average (production1). |
| 1322 | `private String fuel` | The good a fuelled station burns, by its Good name ("THERMAL_COAL"); blank for one that burns none (buildings.json "fuel"). |
| 1325 | `private double heatRate` | A fuelled station's heat rate, Btu of fuel a kWh, as the sources state it (buildings.json "heatRate"). |
| 1328 | `private double variableUpkeep` | A station's variable upkeep, US$ a MWh at today's level (buildings.json "variableUpkeep"). |
| 1331 | `private String variable` | What kind of weather-bound output a station makes: "wind" for the Wind Farm, blank for the rest (buildings.json "variable"). |
| 1384 | `private boolean cooling` | Whether it is cooled by water the city owns (buildings.json "cooling": true). |
| 1387 | `private int buildMonths` | The fewest months it is built in, whatever the crews; 0 for no floor (buildings.json "buildMonths"). |
| 1406 | `private BridgeWorks.Kind bridge` |  |
| 1426 | `private double truckRoom` |  |
| 1650 | `private double foundingCashCost` | What this building cost when the catalogue was read, before any currency reform - so a reformed city that reloads can divide it again. |
| 1651 | `private double foundingUpkeep` |  |
| 1667 | `private double struckAt` | What the founding figures were last divided by to give today's: the unit over the expected price level at the last re-seed, over any reform since. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 1667 | **type** `public class BuildingsTemplate` | One kind of building, and what it costs to put up. |

### WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) (lines 111-129)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 1 | **type** `public enum Source` | What a water works draws: FRESH water, held to the city's limit, or the SEA, which is not. |

### A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) (lines 130-154)

### WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). (lines 155-257)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 4 | `public BuildingsTemplate setSector(String sector)` |  |
| 187 | 4 | `public BuildingsTemplate makes(Good good, double unitsAMonth)` |  |
| 192 | 4 | `public BuildingsTemplate uses(Good good, double unitsAMonth)` |  |
| 197 | 4 | `public BuildingsTemplate setStock(double units)` |  |
| 203 | 1 | `public String getSector()` | The owning sector's key, or "" for a building nobody in the private sector owns. |
| 205 | 1 | `public boolean isOwnedBySector()` |  |
| 220 | 3 | `public String getOperator()` | The key of the sector that WORKS the building and pays its posts: Freight for a sea terminal (BuildingType.PORTS), getSector() for every other building. |
| 231 | 3 | `public static boolean isBilledForUtilities(BuildingsTemplate t)` | Whether a building's power and water are invoiced to anybody: the business buildings a sector owns. |
| 239 | 3 | `public static boolean isHighVoltage(BuildingsTemplate t)` | Whether a building takes its power at high voltage (spec-electricity 2.6): Heavy Industry and Mining are the two classes that pay the energy and the long wires alone, not the street's, so the utility bills them at the... |
| 244 | 1 | `public double makes(Good good)` | Units of a good this building makes a month at nameplate. |
| 247 | 1 | `public double uses(Good good)` | Units of a good this building uses a month at nameplate. |
| 249 | 1 | `public java.util.Map<Good, Double> goodsMade()` |  |
| 250 | 1 | `public java.util.Map<Good, Double> goodsUsed()` |  |
| 253 | 1 | `public double getStock()` | Room for what it holds, in units. |
| 256 | 1 | `public double stocks(Good good)` | The same, asked per good: a building has one warehouse and it holds whatever the building holds. |

### A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; docs/studies/spec-oil.md 2.3) (lines 258-282)

| line | len | member | says |
|---:|---:|---|---|
| 271 | 5 | `public BuildingsTemplate setRefineryUnit(ham.citybuildersim.sectors.RefineryFlow.Kind unit, double litresAMonth)` | Makes this a refinery's conversion unit of a kind, taking `litresAMonth` of its feed stream at nameplate. |
| 278 | 1 | `public ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit()` | The kind of conversion unit this is, or null for every building that is not one. |
| 281 | 1 | `public double feedPerMonth()` | Litres a month of its feed stream a conversion unit takes at nameplate; 0 for every other building. |

### A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) (lines 283-299)

| line | len | member | says |
|---:|---:|---|---|
| 292 | 4 | `public BuildingsTemplate setPumpLitres(double litresAMonth)` | Makes this a filling station that sells `litresAMonth` at its typical throughput. |
| 298 | 1 | `public double pumpLitres()` | Litres a month a filling station sells at its typical throughput; 0 for every other building. |

### A ROAD'S OWN REPAIRS (0.7.103, batch P4; Jerus's A11, 2026-10-10) (lines 300-355)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 4 | `public BuildingsTemplate setRepairsPerYear(double shareAYear)` | Gives this building its own repairs a year, a share of what it cost to put up; 0 or less is none (the house rate). |
| 324 | 1 | `public boolean hasOwnRepairs()` | Whether this building has a repairs share of its own (0.7.103: the three road kinds). |
| 327 | 3 | `public double repairsPerYear()` | Its repairs a year, a share of what it cost to put up: its own, or RealEstate.MAINTENANCE_PER_YEAR. |
| 342 | 4 | `public double gravelRepairsAt(double streetUtilisation)` | A GRAVEL ROAD'S REPAIRS AT THE TRAFFIC ON ITS STREET (FR7 2.2, Jerus's star 5, 2026-10-18): its share a year, repairsPerYear(), up to the load of InfrastructureManager.GRAVEL_QUIET_VEHICLES_A_DAY, and past it that sha... |
| 352 | 3 | `public double repairsPerYearAt(double streetUtilisation)` | ...and any building's repairs a year at the traffic on the streets: the Gravel Road's rise with it (gravelRepairsAt()), the Paved Road's, the highway's and every other building's are flat (repairsPerYear()). |

### A SEA TERMINAL'S BERTH (0.7.86, batch O9; docs/studies/spec-oil.md 2.9) (lines 356-401)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 5 | `public BuildingsTemplate setBerth(Ports.Cargo cargo, double tonnesAYear)` | Makes this a berth of a kind of cargo, handling `tonnesAYear` of it. |
| 373 | 1 | `public Ports.Cargo berthCargo()` | The kind of cargo a terminal's berth handles, or null for every building that is not a berth. |
| 376 | 1 | `public double berthTonnesAYear()` | Tonnes a year a terminal's berth handles; 0 for every other building. |
| 388 | 4 | `public BuildingsTemplate setBerthShips(java.util.List<Ports.Ship> ships)` | Limits this berth to these classes of its cargo; an empty list lifts the limit. |
| 394 | 1 | `public java.util.List<Ports.Ship> berthShips()` | The classes this berth takes, smallest first as the list gives them; empty when it takes every class of its kind (Ports.shipsOf() answers either way). |
| 397 | 1 | `public boolean isPort()` | Whether this is a sea terminal (0.7.86): a PORTS building with a berth. |
| 400 | 1 | `public boolean needsCoast()` | Whether an order for this has to stand on owned sea: a desalination plant (0.7.59) and a sea terminal (0.7.86). |

### THE OIL AT SEA (0.7.91, batch O10; docs/studies/spec-oil.md 2.7, 2.11) (lines 402-448)

| line | len | member | says |
|---:|---:|---|---|
| 414 | 1 | **type** `public enum Offshore` | What a building at sea is: a platform's jacket, a well in one of its slots, or a kilometre of crude pipeline. |
| 421 | 6 | `public BuildingsTemplate setOffshore(Offshore kind, int slots, double onshoreCash)` | Makes this one of the oil buildings at sea: a PLATFORM with `slots` for wells, a WELL, or a PIPELINE whose kilometre on land costs `onshoreCash` (thousands). |
| 429 | 1 | `public Offshore offshore()` | Which of the buildings at sea this is, or null for every building on the ground. |
| 432 | 1 | `public boolean standsAtSea()` | Whether this stands at sea (an Offshore Platform, a Platform Well, a Crude Pipeline): not on the city's dry ground, nor drawn on its land. |
| 435 | 1 | `public boolean isPlatform()` | Whether this is an offshore platform's jacket. |
| 438 | 1 | `public boolean isPlatformWell()` | Whether this is a well in a platform's slot. |
| 441 | 1 | `public boolean isPipeline()` | Whether this is a kilometre of crude pipeline. |
| 444 | 1 | `public int platformSlots()` | The wells a platform's jacket holds: 12 for the Offshore Platform; 0 for every other building. |
| 447 | 1 | `public double onshoreCashPerKm()` | A pipeline's kilometre on land, in thousands (its own cash is a kilometre at sea); 0 for every other building. |

### THE SITE IT STANDS ON (0.7.110, batch I1; docs/studies/spec-materials.md 2.4, 2.5) (lines 449-483)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 4 | **type** `public record Site(Resource resource, Deposit.FieldType type, int sites, double dig)` | The site a building stands on: its resource, the type of field it needs (null for ANY), the sites it takes and the tonnes of field a unit it makes takes out. |
| 465 | 1 | `public boolean any()` _(in BuildingsTemplate.Site)_ | Whether it stands on a field of any type. |
| 471 | 9 | `public BuildingsTemplate setSite(Resource resource, Deposit.FieldType type, int sites, double dig)` | Makes this stand on `sites` of a resource's sites of a type (null for ANY), taking `dig` of the field a unit it makes; a type not the resource's reads as ANY. |
| 482 | 1 | `public Site site()` | The site it stands on, or null for a building that stands on no resource's sites. |

### THE POWER A WORKS MAKES ITSELF (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8) (lines 484-517)

| line | len | member | says |
|---:|---:|---|---|
| 500 | 4 | `public BuildingsTemplate setGen(double kw)` | Makes this building make `kw` of power a month itself; 0 or less is none. |
| 506 | 1 | `public double getGen()` | kW of power it makes itself; 0 for every building but the Coke Ovens, the Blast Furnace and the Oxygen Steel Shop (0.7.123). |
| 509 | 3 | `public double powerBought()` | kW it buys from the grid: its draw past what it makes - its whole draw, exactly, when it makes none. |
| 514 | 3 | `public double powerSold()` | kW it sells the grid: what it makes past its draw; nothing when it makes none. |

### WHAT A WORKS BUYS ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5) (lines 518-539)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 4 | `public BuildingsTemplate setConsumables(double worldThousands)` | Gives this building `worldThousands` a month of consumables at nameplate, in the world's money; 0 or less is none. |
| 538 | 1 | `public double getConsumables()` | Its consumables a month at nameplate, thousands of the world's money; 0 for every building but the materials chains' rows (the iron rows since 0.7.120, the integrated route's since 0.7.123). |

### THE OLD METAL A YARD TAKES (0.7.123, batch I4; docs/studies/spec-materials.md 2.5, 2.9) (lines 540-563)

| line | len | member | says |
|---:|---:|---|---|
| 551 | 6 | `public BuildingsTemplate setOldMetal(Good metal, double tonnesAMonth)` | Makes this building take `tonnesAMonth` of the city's old metal of a kind at nameplate; 0 or less is none. |
| 559 | 1 | `public double oldMetal(Good metal)` | Tonnes of the city's old metal of this kind it takes a month at nameplate; 0 for every building but a yard. |
| 562 | 1 | `public java.util.Map<Good, Double> oldMetalTaken()` | Every kind of old metal it takes, tonnes a month at nameplate; empty for every building but a yard. |

### THE EQUIPMENT IT CAN BE FITTED WITH (R1, 2026-11-05; research-design.md 2.1-2.2) (lines 564-587)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 4 | `public BuildingsTemplate setEquipment(java.util.List<Equipment> pieces)` | Gives this building its equipment, in the order given; a copy, and null or empty is none. |
| 580 | 1 | `public java.util.List<Equipment> getEquipment()` | The pieces of equipment it can be fitted with, in the file's order; unmodifiable, empty for most buildings. |
| 583 | 4 | `public Equipment getEquipment(String id)` | The piece of this id, or null if the building carries none of it. |

### THE WAYS IT RUNS (R1c, 2026-11-05; research-design.md 2.1) (lines 588-789)

| line | len | member | says |
|---:|---:|---|---|
| 614 | 1 | `public java.util.List<RecipeChange> getRecipeChanges()` | Independent discovery undos, in code order, anchored to the immutable default. |
| 617 | 8 | `public BuildingsTemplate setRecipeChanges(java.util.List<RecipeChange> changes)` | Installs independent changes before the template first runs. |
| 627 | 3 | `public boolean changeNeeds(String piece)` | Whether an independent recipe change needs this piece. |
| 632 | 1 | `double recipeProduction()` | Utility output before any undo. |
| 635 | 1 | `double recipeLand()` | Ground before any undo. |
| 638 | 1 | `Site recipeSite()` | The immutable modern extraction coefficient and site identity. |
| 641 | 1 | `public double steamRailShare()` | Share of rail engines on steam, for the actual fuel invoice. |
| 644 | 1 | `public double crackerPetrol()` | Petrol per litre fed to this cracking unit after the chosen process. |
| 647 | 7 | `void runRecipeExtras(double production, double land, double steamRail, double petrol, double dig)` | Sets the non-goods figures of the independent changes. |
| 665 | 26 | `public BuildingsTemplate setRecipes(java.util.List<Recipe> list)` | Gives this building its recipes, in the order given; a copy, and null or empty is none (the one implicit default, its top level). |
| 693 | 3 | `public java.util.List<Recipe> getRecipes()` | Its recipes in the file's order; unmodifiable. |
| 698 | 6 | `public Recipe getRecipe(String id)` | The recipe of this id, or null if the building knows none of it. |
| 706 | 4 | `public Recipe defaultRecipe()` | The default recipe: its top level, today's way (the one entry of a list that states no figure). |
| 712 | 1 | `public boolean hasRecipeChoice()` | Whether it has a choice of ways to run: a list with more than the default in it. |
| 715 | 1 | `public String recipePieceId()` | The id of the piece of equipment its recipes need (at most one per template), or null where none does. |
| 718 | 1 | `public Equipment recipePiece()` | The piece of equipment its recipes need, the template's "recipe piece": the buildings that have it run the recipes that need it, those that have not the ones that need nothing; null where no recipe needs one. |
| 727 | 33 | `public void runRecipes(Recipe fitted, long fittedBuildings, Recipe unfitted, long unfittedBuildings)` | Sets this building's live figures to the recipes its two parts run, weighted by their buildings: `fitted` for `fittedBuildings` and `unfitted` for `unfittedBuildings` (a part with no building weighs nothing; with none... |
| 762 | 12 | `private void setFigures(Recipe r)` | Sets the live figures to one recipe's, to the bit. |
| 776 | 4 | `void setRunningRecipeIds(String[] ids)` | Retains the saved process decisions when independent equipment changes live figures. |
| 782 | 4 | `public String[] runningRecipeIds()` | The ids of the recipes the fitted part and the unfitted part last ran (runRecipes()), in that order; the default's where it has run none. |
| 788 | 1 | `boolean hasRunningRecipes()` | Whether this recipe list has reached production since it was installed. |

### WHEN IT FIRST EXISTED (0.7.123, batch I4; R2, 2026-11-05; research-design.md 2.1, 7) (lines 790-851)

| line | len | member | says |
|---:|---:|---|---|
| 811 | 3 | `public BuildingsTemplate setSince(int year)` | Dates this building by a year: the year it first existed, the start year (1900) for one that stood before it; 0 or less is undated. |
| 819 | 13 | `public BuildingsTemplate setSince(String text)` | Dates this building by a year ("1902") or a research catalogue code ("PE4"): blank, null or a year of 0 or less is undated. |
| 834 | 1 | `public int getSince()` | The year it first existed (a code's entry's year; 0 for a code that is not in the catalogue), or 0 for a row not yet dated. |
| 837 | 1 | `public String getSinceText()` | What the row says, as written: "" undated, "1902" a year, "PE4" a catalogue code. |
| 840 | 3 | `public boolean knownAt(int gameMonth)` | Whether it is known at a game month (month 1 = January 1900): an undated row always; a dated one from the month its year or its code's entry gives (ResearchCatalog.knownFrom()). |
| 848 | 3 | `public boolean knownAt(int gameMonth, Research found)` | Whether it is known at a game month in a city whose research may have found its code first (R3): knownAt(int), or from the month the city found the code (Research.knownFrom()). |

### THE RAW COAL A PREPARATION PLANT WASHES (0.7.126, batch I6; docs/studies/spec-materials.md 2.5, 2.10) (lines 852-1188)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 4 | `public BuildingsTemplate setWash(double tonnesAMonth)` | Makes this a preparation plant washing `tonnesAMonth` of raw coal a month at nameplate; 0 or less is none. |
| 872 | 1 | `public double getWash()` | Tonnes of raw coal it washes a month at nameplate; 0 for every building but the Coal Preparation Plant (0.7.126). |
| 881 | 4 | `public BuildingsTemplate(String name, BuildingType category)` | barebones constructor |
| 929 | 4 | `public BuildingsTemplate setJobs(JobType type, int number)` | setters (method chaining) |
| 934 | 7 | `public BuildingsTemplate setCashCost(double cashCost)` |  |
| 950 | 13 | `BuildingsTemplate pricedAtFoundingLevel()` | THE FUNNEL FOR THE BUILDINGS' MONEY (EP1, Jerus 2026-10-30: "1900 price level"). |
| 964 | 4 | `public BuildingsTemplate setConstructionPoints(int constructionPoints)` |  |
| 969 | 4 | `public BuildingsTemplate setCapacity(int capacity)` |  |
| 974 | 6 | `public BuildingsTemplate setUpkeep(double upkeep)` |  |
| 981 | 4 | `public BuildingsTemplate setConstructionMaterials(int constructionMaterials)` |  |
| 986 | 4 | `public BuildingsTemplate setElectricityConsumption(int electricityConsumption)` |  |
| 991 | 4 | `public BuildingsTemplate setWaterConsumption(double waterConsumption)` |  |
| 996 | 4 | `public BuildingsTemplate setLandSqFt(double landSqFt)` |  |
| 1001 | 4 | `public BuildingsTemplate setRoadLoad(double roadLoad)` |  |
| 1006 | 4 | `public BuildingsTemplate setCoverage(int coverage)` |  |
| 1011 | 4 | `public BuildingsTemplate setProduction1(double production1)` |  |
| 1016 | 4 | `public BuildingsTemplate setProduction2(double production2)` |  |
| 1021 | 4 | `public BuildingsTemplate setProductionModifier1(double productionModifier1)` |  |
| 1026 | 4 | `public BuildingsTemplate setProductionModifier2(double productionModifier2)` |  |
| 1031 | 4 | `public BuildingsTemplate setNationalized(boolean nationalized)` |  |
| 1036 | 4 | `public BuildingsTemplate setId(int id)` |  |
| 1041 | 4 | `public BuildingsTemplate setCare(CareType care)` |  |
| 1046 | 4 | `public BuildingsTemplate setSafety(SafetyType safety)` |  |
| 1051 | 4 | `public BuildingsTemplate setTeaches(EducationType teaches)` |  |
| 1057 | 4 | `public BuildingsTemplate setResearches(ResearchCatalog.Field field)` | The field this Research Lab researches (R4); null clears it. |
| 1062 | 4 | `public BuildingsTemplate setSource(Source source)` |  |
| 1068 | 3 | `public Source getSource()` | What it draws from: FRESH for every building but a desalination plant (see the field's note). |
| 1073 | 3 | `public boolean isSeaWater()` | A water works that draws the sea: a Desalination Plant (0.7.59). |
| 1078 | 3 | `public boolean isFreshWater()` | A water works that draws fresh water, held to the city's limit: a Water Treatment Plant. |
| 1083 | 3 | `public double getCashCost()` | getters |
| 1093 | 3 | `public double getFoundingCashCost()` | The cash cost as the catalogue states it (ET2): buildings.json's figure, in thousands of founding dollars, before the unit and the expected price level it is struck at (seedConstants()) - which the price level and a c... |
| 1097 | 3 | `public int getConstructionPoints()` |  |
| 1104 | 4 | `public HousingRecipe getHousingRecipe()` | Fixed dwelling/rights metadata, separate from the structure's physical recipe. |
| 1109 | 7 | `public BuildingsTemplate setHousingRecipe(HousingRecipe recipe)` |  |
| 1117 | 3 | `public int getDwellings()` |  |
| 1121 | 4 | `public BuildingsTemplate setDwellings(int dwellings)` |  |
| 1142 | 5 | `public int homeSize()` | How big a household one of these units takes, in people. |
| 1156 | 4 | `public boolean adultsOnly()` | True for a flat too small to put a child in. |
| 1161 | 3 | `public int getCapacity()` |  |
| 1165 | 3 | `public double getUpkeep()` |  |
| 1169 | 3 | `public int getConstructionMaterials()` |  |
| 1173 | 3 | `public int getElectricityConsumption()` |  |
| 1177 | 3 | `public double getWaterConsumption()` |  |
| 1181 | 3 | `public double getLandSqFt()` |  |
| 1185 | 3 | `public double getRoadLoad()` |  |

### WHAT A MODE IS GOOD AT (2026-09-16) (lines 1189-1315)

| line | len | member | says |
|---:|---:|---|---|
| 1211 | 4 | `public BuildingsTemplate setFreightGrade(double grade)` | How much of this road is built for lorries, 0 to 1. |
| 1216 | 1 | `public double getFreightGrade()` |  |
| 1225 | 1 | `public boolean isHighway()` | Whether this is a highway (FR7, 2026-10-18): a road that is fully grade-separated, a freight grade of 1. |
| 1235 | 4 | `public BuildingsTemplate setTransitCapacity(double riders)` | Commuter journeys a month this carries OFF the road, if it is transit. |
| 1240 | 1 | `public double getTransitCapacity()` |  |
| 1243 | 1 | `public boolean isTransit()` | True for a building whose whole purpose is carrying people. |
| 1255 | 4 | `public BuildingsTemplate setRailCapacity(double tonnes)` | Tonnes a month this can haul across the city boundary, if it is rail. |
| 1260 | 1 | `public double getRailCapacity()` |  |
| 1276 | 4 | `public BuildingsTemplate setStationTonnes(double tonnes)` | Tonnes a month a station sorts (FR7, 2026-10-18): the Rail Depot's 50,000 and the Rail Terminal's 500,000; none for every other building. |
| 1281 | 1 | `public double getStationTonnes()` |  |
| 1284 | 1 | `public boolean isStation()` | True for a building that is a station: it sorts tonnes (getStationTonnes() &gt; 0). |
| 1294 | 4 | `public BuildingsTemplate setSiding(boolean siding)` | Whether this track is a siding into one works (FR7): the Rail Spur, and no other. |
| 1299 | 1 | `public boolean isSiding()` |  |
| 1309 | 4 | `public BuildingsTemplate setHydro(boolean hydro)` | Whether this power station needs a river with a drop on the ground the city owns (HY1): the Hydro Station, and no other. |
| 1314 | 1 | `public boolean isHydro()` |  |

### THE POWER ROWS (PW1; spec-electricity 2.3, 2.5) (lines 1316-1370)

| line | len | member | says |
|---:|---:|---|---|
| 1333 | 1 | `public BuildingsTemplate setNameplate(double kw)` |  |
| 1334 | 1 | `public BuildingsTemplate setFuel(String good)` |  |
| 1335 | 1 | `public BuildingsTemplate setHeatRate(double btuPerKwh)` |  |
| 1336 | 1 | `public BuildingsTemplate setVariableUpkeep(double usdPerMwh)` |  |
| 1337 | 1 | `public BuildingsTemplate setVariable(String kind)` |  |
| 1340 | 1 | `public double getNameplate()` | The station's nameplate, kW: the row's, or with none its production1. |
| 1343 | 8 | `public Good getFuel()` | The good the station burns; null when it burns none or names one the game does not have. |
| 1352 | 1 | `public double getHeatRate()` |  |
| 1354 | 1 | `public double getVariableUpkeep()` |  |
| 1357 | 1 | `public boolean isWind()` | Whether this station's output is the wind's, shaped by the weather (PowerDispatch.windAt()). |
| 1360 | 1 | `public boolean isSolar()` | Whether this station's output is the sun's, shaped by the day and the weather (PowerDispatch.solarAt(); PW2): buildings.json "variable": "solar". |
| 1367 | 3 | `public double credit()` | The share of its average a station counts as firm, what the build advice plans power by (PW1's calm peak; PW2): a wind farm's PowerDispatch.WIND_FIRM, a solar farm's SOLAR_FIRM (its darkest half-block, 0, until PW3 cr... |

### THE COOLING WATER AND THE BUILD-TIME FLOOR (PW2, 2026-11-23; spec-electricity 2.3) (lines 1371-1438)

| line | len | member | says |
|---:|---:|---|---|
| 1389 | 1 | `public BuildingsTemplate setCooling(boolean cooling)` |  |
| 1391 | 1 | `public BuildingsTemplate setBuildMonths(int months)` |  |
| 1394 | 1 | `public boolean cooling()` | Whether it needs owned sea, lake or river to cool it (PW2): the steam rows. |
| 1397 | 1 | `public int buildMonths()` | The fewest months it is built in, whatever the crews; 0 for none (PW2). |
| 1400 | 5 | `public double fuelPerKwh()` | Units of fuel the station burns a kWh: its heat rate over the fuel's heat content; 0 with no fuel, no heat rate or no heat content. |
| 1415 | 4 | `public BuildingsTemplate setBridge(BridgeWorks.Kind kind)` | Which long bridge this is (ET6): the Highway Bridge, the Rail Bridge and the Road Bridge, and no other building. |
| 1421 | 1 | `public BridgeWorks.Kind bridgeKind()` | The kind of long bridge this is, or null for every other building. |
| 1424 | 1 | `public boolean isBridge()` | Whether this is a long bridge (ET6). |
| 1432 | 4 | `public BuildingsTemplate setTruckRoom(double trucks)` | Trucks a Truck Depot keeps (FR2): Freight's Haulage division's room; none for every other building. |
| 1437 | 1 | `public double getTruckRoom()` |  |

### WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) (lines 1439-1693)

| line | len | member | says |
|---:|---:|---|---|
| 1493 | 45 | `public double loadOf(Traffic stream)` | This building's road load, split by what is actually moving. |
| 1539 | 3 | `public int getCoverage()` |  |
| 1543 | 3 | `public double getProduction1()` |  |
| 1547 | 3 | `public double getProduction2()` |  |
| 1551 | 3 | `public double getProductionModifier1()` |  |
| 1555 | 3 | `public double getProductionModifier2()` |  |
| 1559 | 3 | `public boolean getNationalized()` |  |
| 1562 | 3 | `public int getJobs(JobType type)` |  |
| 1566 | 3 | `public String getName()` |  |
| 1570 | 3 | `public BuildingType getCategory()` |  |
| 1574 | 3 | `public int getId()` |  |
| 1578 | 3 | `public CareType getCare()` |  |
| 1583 | 4 | `public BuildingsTemplate setRequiresLicence(JobType licence)` | The licence this building's practice needs, or null. |
| 1589 | 1 | `public JobType getRequiresLicence()` | The licence this building's practice needs, or null if it needs none. |
| 1592 | 3 | `public int getLicensedPosts()` | How many posts here need that licence - what the gate is measured against. |
| 1596 | 3 | `public SafetyType getSafety()` |  |
| 1600 | 3 | `public EducationType getTeaches()` |  |
| 1605 | 3 | `public ResearchCatalog.Field getResearches()` | The field this building is a Research Lab of (R4), or null for every other building. |
| 1613 | 5 | `public int getTotalJobs()` | sum of all jobs |
| 1632 | 6 | `public void redenominate(double scale)` | What a building costs and what it costs to run, in the new unit. |
| 1670 | 9 | `public void seedConstants(double unit)` | Re-seeds this template's price at a given unit (since 0.7.42, the unit over the expected price level). |
| 1686 | 6 | `private void rememberFounding()` | ...and a reform moves the founding figure with it, so that a LATER re-seed at the new unit lands in the same place. |


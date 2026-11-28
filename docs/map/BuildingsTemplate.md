# BuildingsTemplate.java - 1,727 lines · 191 methods · 2 constants · model

`ham/citybuildersim/BuildingsTemplate.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (29), [Recipe](Recipe.md) (20), [JobType](JobType.md) (12), [BuildingType](BuildingType.md) (12), [Equipment](Equipment.md) (7), [ResearchCatalog](ResearchCatalog.md) (6), [Ports](Ports.md) (6), [CareType](CareType.md) (5), [EducationType](EducationType.md) (5), [SafetyType](SafetyType.md) (5), [PriceLevel](PriceLevel.md) (5), [BuildingManager](BuildingManager.md) (4), [RecipeChange](RecipeChange.md) (4), [HousingRecipe](HousingRecipe.md) (4), [PowerDispatch](PowerDispatch.md) (4), [Traffic](Traffic.md) (4), [RefineryFlow](RefineryFlow.md) (3), [Deposit](Deposit.md) (3), [BridgeWorks](BridgeWorks.md) (3), [Resource](Resource.md) (2), [Sectors](Sectors.md) (1), [RealEstate](RealEstate.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [ConstructionControl](ConstructionControl.md) (1), [RecipeChanges](RecipeChanges.md) (1), [CityCalendar](CityCalendar.md) (1), [Research](Research.md) (1)

**Used by (254):** [AdviceValueOracle](AdviceValueOracle.md), [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingInstance](BuildingInstance.md), [BuildingManager](BuildingManager.md), [BuildingStackCheck](BuildingStackCheck.md), [BuildingVisual](BuildingVisual.md), [BuildingsStacks](BuildingsStacks.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [Construction](Construction.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [CopperBufferCheck](CopperBufferCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [CrimeCheck](CrimeCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DenominationCheck](DenominationCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [DistressBleedCheck](DistressBleedCheck.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [ER1Check](ER1Check.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FB13Check](FB13Check.md), [FarmlandReserveCheck](FarmlandReserveCheck.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [Fitout](Fitout.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [FoodIndustry](FoodIndustry.md), [FoodIndustryCheck](FoodIndustryCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedgerCheck](FundLedgerCheck.md), [Game](Game.md), [GapOrderCheck](GapOrderCheck.md), [GatewayCheck](GatewayCheck.md), [GdpCheck](GdpCheck.md), [GoldMining](GoldMining.md), [GoldMiningCheck](GoldMiningCheck.md), [GroundInputCheck](GroundInputCheck.md), [HarbourRenders](HarbourRenders.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [HousingForms](HousingForms.md), [HousingInventory](HousingInventory.md), [HousingInventoryCheck](HousingInventoryCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [HousingRecipe](HousingRecipe.md), [HydroCheck](HydroCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [IslandRailCheck](IslandRailCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandManager](LandManager.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [MedicineCheck](MedicineCheck.md), [MiningCheck](MiningCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [ModernRowsCheck](ModernRowsCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [NewUnit](NewUnit.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OldMetal](OldMetal.md), [OrderDoorCheck](OrderDoorCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PowerDispatchCheck](PowerDispatchCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PowerView](PowerView.md), [PowerViewCheck](PowerViewCheck.md), [PowerWiresCheck](PowerWiresCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [ProcessingSector](ProcessingSector.md), [PublicLicenceCheck](PublicLicenceCheck.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Recipe](Recipe.md), [RecipeChange](RecipeChange.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [ResearchCheck](ResearchCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [ShortSellingCheck](ShortSellingCheck.md), [SlopeCheck](SlopeCheck.md), [SpreadPlanner](SpreadPlanner.md), [StaffingDrawCheck](StaffingDrawCheck.md), [StaffingTieCheck](StaffingTieCheck.md), [StrategicReserve](StrategicReserve.md), [SubstationCheck](SubstationCheck.md), [Substations](Substations.md), [SulphideLeachCheck](SulphideLeachCheck.md), [SummaryScreen](SummaryScreen.md), [TownGridCheck](TownGridCheck.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UndemandedFeedCheck](UndemandedFeedCheck.md), [UserInterface](UserInterface.md), [UtilitiesHandler](UtilitiesHandler.md), [Village1900Check](Village1900Check.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WaterPowerCheck](WaterPowerCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [WorldMarketCheck](WorldMarketCheck.md), [YearBookCheck](YearBookCheck.md), [YoungBankCheck](YoungBankCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 111 | WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) |
| 130 | A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) |
| 155 | WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). |
| 259 | · A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; docs/studies/spec-oil.md 2.3) |
| 284 | · A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) |
| 301 | · A ROAD'S OWN REPAIRS (0.7.103, batch P4; Jerus's A11, 2026-10-10) |
| 357 | · A SEA TERMINAL'S BERTH (0.7.86, batch O9; docs/studies/spec-oil.md 2.9) |
| 403 | · THE OIL AT SEA (0.7.91, batch O10; docs/studies/spec-oil.md 2.7, 2.11) |
| 450 | · THE SITE IT STANDS ON (0.7.110, batch I1; docs/studies/spec-materials.md 2.4, 2.5) |
| 485 | · THE POWER A WORKS MAKES ITSELF (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8) |
| 519 | · WHAT A WORKS BUYS ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5) |
| 541 | · THE OLD METAL A YARD TAKES (0.7.123, batch I4; docs/studies/spec-materials.md 2.5, 2.9) |
| 565 | · THE EQUIPMENT IT CAN BE FITTED WITH (R1, 2026-11-05; research-design.md 2.1-2.2) |
| 589 | · THE WAYS IT RUNS (R1c, 2026-11-05; research-design.md 2.1) |
| 793 | · WHEN IT FIRST EXISTED (0.7.123, batch I4; R2, 2026-11-05; research-design.md 2.1, 7) |
| 866 | · THE RAW COAL A PREPARATION PLANT WASHES (0.7.126, batch I6; docs/studies/spec-materials.md 2.5, 2.10) |
| 1223 | WHAT A MODE IS GOOD AT (2026-09-16) |
| 1350 | THE POWER ROWS (PW1; spec-electricity 2.3, 2.5) |
| 1405 | · THE COOLING WATER AND THE BUILD-TIME FLOOR (PW2, 2026-11-23; spec-electricity 2.3) |
| 1473 | WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 126 | `BuildingsTemplate.Source.FRESH` |  |
| 126 | `BuildingsTemplate.Source.SEA` |  |
| 415 | `BuildingsTemplate.Offshore.PLATFORM` |  |
| 415 | `BuildingsTemplate.Offshore.WELL` |  |
| 415 | `BuildingsTemplate.Offshore.PIPELINE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1514 | `BuildingsTemplate.TONNES_PER_WORKER` | `20` | One worker's monthly travel, as tonnes of freight. |
| 1517 | `BuildingsTemplate.WORKERS_PER_HOME` | `1.2` | A home's monthly travel, as workers. |

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
| 268 | `private ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit` |  |
| 269 | `private double feedPerMonth` |  |
| 290 | `private double pumpLitres` |  |
| 316 | `private double repairsPerYear` |  |
| 363 | `private Ports.Cargo berthCargo` |  |
| 364 | `private double berthTonnesAYear` |  |
| 386 | `private java.util.List<Ports.Ship> berthShips` | Which classes call (FR3 2026-10-19, docs/queue/FR3.md 2 and call 6): a berth may take only some of the classes of its kind ("port": {... |
| 417 | `private Offshore offshore` |  |
| 418 | `private int platformSlots` |  |
| 419 | `private double onshoreCashPerKm` |  |
| 469 | `private Site site` |  |
| 498 | `private double gen` |  |
| 530 | `private double consumables` |  |
| 549 | `private final java.util.Map<Good, Double> oldMetal` |  |
| 572 | `private java.util.List<Equipment> equipment` |  |
| 606 | `private java.util.List<Recipe> recipes` |  |
| 607 | `private String recipePieceId` |  |
| 608 | `private java.util.List<RecipeChange> recipeChanges` |  |
| 609 | `private double recipeProduction, recipeLand` |  |
| 610 | `private Site recipeSite` |  |
| 611 | `private double steamRailShare` |  |
| 612 | `private double crackerPetrol` |  |
| 656 | `private volatile String runsFitted, runsUnfitted` |  |
| 810 | `private int since` |  |
| 811 | `private String sinceText` |  |
| 813 | `private int until` | PW9, REV-PW9 P1-b: an obsolete station remains in service, but the catalogue stops taking new orders. |
| 877 | `private double wash` |  |
| 891 | `int[] jobsByEducation` | enums |
| 892 | `private BuildingType category` |  |
| 1135 | `private HousingRecipe housingRecipe` |  |
| 1232 | `private double freightGrade` |  |
| 1233 | `private double transitCapacity` |  |
| 1279 | `private double railCapacity` |  |
| 1296 | `private double stationTonnes` |  |
| 1320 | `private boolean siding` |  |
| 1335 | `private boolean hydro` |  |
| 1353 | `private double nameplate` | A power station's nameplate, kW: what it can put out in an hour, past its average (production1). |
| 1356 | `private String fuel` | The good a fuelled station burns, by its Good name ("THERMAL_COAL"); blank for one that burns none (buildings.json "fuel"). |
| 1359 | `private double heatRate` | A fuelled station's heat rate, Btu of fuel a kWh, as the sources state it (buildings.json "heatRate"). |
| 1362 | `private double variableUpkeep` | A station's variable upkeep, US$ a MWh at today's level (buildings.json "variableUpkeep"). |
| 1365 | `private String variable` | What kind of weather-bound output a station makes: "wind" for the Wind Farm, blank for the rest (buildings.json "variable"). |
| 1418 | `private boolean cooling` | Whether it is cooled by water the city owns (buildings.json "cooling": true). |
| 1421 | `private int buildMonths` | The fewest months it is built in, whatever the crews; 0 for no floor (buildings.json "buildMonths"). |
| 1440 | `private BridgeWorks.Kind bridge` |  |
| 1460 | `private double truckRoom` |  |
| 1684 | `private double foundingCashCost` | What this building cost when the catalogue was read, before any currency reform - so a reformed city that reloads can divide it again. |
| 1685 | `private double foundingUpkeep` |  |
| 1701 | `private double struckAt` | What the founding figures were last divided by to give today's: the unit over the expected price level at the last re-seed, over any reform since. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 1701 | **type** `public class BuildingsTemplate` | One kind of building, and what it costs to put up. |

### WHERE A WATER WORKS DRAWS FROM (0.7.59, batch J2; spec-land 2.3) (lines 111-129)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 1 | **type** `public enum Source` | What a water works draws: FRESH water, held to the city's limit, or the SEA, which is not. |

### A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12) (lines 130-154)

### WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template). (lines 155-258)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 5 | `public BuildingsTemplate setSector(String sector)` |  |
| 188 | 4 | `public BuildingsTemplate makes(Good good, double unitsAMonth)` |  |
| 193 | 4 | `public BuildingsTemplate uses(Good good, double unitsAMonth)` |  |
| 198 | 4 | `public BuildingsTemplate setStock(double units)` |  |
| 204 | 1 | `public String getSector()` | The owning sector's key, or "" for a building nobody in the private sector owns. |
| 206 | 1 | `public boolean isOwnedBySector()` |  |
| 221 | 3 | `public String getOperator()` | The key of the sector that WORKS the building and pays its posts: Freight for a sea terminal (BuildingType.PORTS), getSector() for every other building. |
| 232 | 3 | `public static boolean isBilledForUtilities(BuildingsTemplate t)` | Whether a building's power and water are invoiced to anybody: the buildings a sector owns, including rental homes billed to Real Estate. |
| 240 | 3 | `public static boolean isHighVoltage(BuildingsTemplate t)` | Whether a building takes its power at high voltage (spec-electricity 2.6): Heavy Industry and Mining are the two classes that pay the energy and the long wires alone, not the street's, so the utility bills them at the... |
| 245 | 1 | `public double makes(Good good)` | Units of a good this building makes a month at nameplate. |
| 248 | 1 | `public double uses(Good good)` | Units of a good this building uses a month at nameplate. |
| 250 | 1 | `public java.util.Map<Good, Double> goodsMade()` |  |
| 251 | 1 | `public java.util.Map<Good, Double> goodsUsed()` |  |
| 254 | 1 | `public double getStock()` | Room for what it holds, in units. |
| 257 | 1 | `public double stocks(Good good)` | The same, asked per good: a building has one warehouse and it holds whatever the building holds. |

### A REFINERY'S CONVERSION UNIT (0.7.80, batch O4; docs/studies/spec-oil.md 2.3) (lines 259-283)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 5 | `public BuildingsTemplate setRefineryUnit(ham.citybuildersim.sectors.RefineryFlow.Kind unit, double litresAMonth)` | Makes this a refinery's conversion unit of a kind, taking `litresAMonth` of its feed stream at nameplate. |
| 279 | 1 | `public ham.citybuildersim.sectors.RefineryFlow.Kind refineryUnit()` | The kind of conversion unit this is, or null for every building that is not one. |
| 282 | 1 | `public double feedPerMonth()` | Litres a month of its feed stream a conversion unit takes at nameplate; 0 for every other building. |

### A FILLING STATION'S PUMPS (0.7.83, batch O6; runs/research-pump.md 7) (lines 284-300)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 4 | `public BuildingsTemplate setPumpLitres(double litresAMonth)` | Makes this a filling station that sells `litresAMonth` at its typical throughput. |
| 299 | 1 | `public double pumpLitres()` | Litres a month a filling station sells at its typical throughput; 0 for every other building. |

### A ROAD'S OWN REPAIRS (0.7.103, batch P4; Jerus's A11, 2026-10-10) (lines 301-356)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 4 | `public BuildingsTemplate setRepairsPerYear(double shareAYear)` | Gives this building its own repairs a year, a share of what it cost to put up; 0 or less is none (the house rate). |
| 325 | 1 | `public boolean hasOwnRepairs()` | Whether this building has a repairs share of its own (0.7.103: the three road kinds). |
| 328 | 3 | `public double repairsPerYear()` | Its repairs a year, a share of what it cost to put up: its own, or RealEstate.MAINTENANCE_PER_YEAR. |
| 343 | 4 | `public double gravelRepairsAt(double streetUtilisation)` | A GRAVEL ROAD'S REPAIRS AT THE TRAFFIC ON ITS STREET (FR7 2.2, Jerus's star 5, 2026-10-18): its share a year, repairsPerYear(), up to the load of InfrastructureManager.GRAVEL_QUIET_VEHICLES_A_DAY, and past it that sha... |
| 353 | 3 | `public double repairsPerYearAt(double streetUtilisation)` | ...and any building's repairs a year at the traffic on the streets: the Gravel Road's rise with it (gravelRepairsAt()), the Paved Road's, the highway's and every other building's are flat (repairsPerYear()). |

### A SEA TERMINAL'S BERTH (0.7.86, batch O9; docs/studies/spec-oil.md 2.9) (lines 357-402)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 5 | `public BuildingsTemplate setBerth(Ports.Cargo cargo, double tonnesAYear)` | Makes this a berth of a kind of cargo, handling `tonnesAYear` of it. |
| 374 | 1 | `public Ports.Cargo berthCargo()` | The kind of cargo a terminal's berth handles, or null for every building that is not a berth. |
| 377 | 1 | `public double berthTonnesAYear()` | Tonnes a year a terminal's berth handles; 0 for every other building. |
| 389 | 4 | `public BuildingsTemplate setBerthShips(java.util.List<Ports.Ship> ships)` | Limits this berth to these classes of its cargo; an empty list lifts the limit. |
| 395 | 1 | `public java.util.List<Ports.Ship> berthShips()` | The classes this berth takes, smallest first as the list gives them; empty when it takes every class of its kind (Ports.shipsOf() answers either way). |
| 398 | 1 | `public boolean isPort()` | Whether this is a sea terminal (0.7.86): a PORTS building with a berth. |
| 401 | 1 | `public boolean needsCoast()` | Whether an order for this has to stand on owned sea: a desalination plant (0.7.59) and a sea terminal (0.7.86). |

### THE OIL AT SEA (0.7.91, batch O10; docs/studies/spec-oil.md 2.7, 2.11) (lines 403-449)

| line | len | member | says |
|---:|---:|---|---|
| 415 | 1 | **type** `public enum Offshore` | What a building at sea is: a platform's jacket, a well in one of its slots, or a kilometre of crude pipeline. |
| 422 | 6 | `public BuildingsTemplate setOffshore(Offshore kind, int slots, double onshoreCash)` | Makes this one of the oil buildings at sea: a PLATFORM with `slots` for wells, a WELL, or a PIPELINE whose kilometre on land costs `onshoreCash` (thousands). |
| 430 | 1 | `public Offshore offshore()` | Which of the buildings at sea this is, or null for every building on the ground. |
| 433 | 1 | `public boolean standsAtSea()` | Whether this stands at sea (an Offshore Platform, a Platform Well, a Crude Pipeline): not on the city's dry ground, nor drawn on its land. |
| 436 | 1 | `public boolean isPlatform()` | Whether this is an offshore platform's jacket. |
| 439 | 1 | `public boolean isPlatformWell()` | Whether this is a well in a platform's slot. |
| 442 | 1 | `public boolean isPipeline()` | Whether this is a kilometre of crude pipeline. |
| 445 | 1 | `public int platformSlots()` | The wells a platform's jacket holds: 12 for the Offshore Platform; 0 for every other building. |
| 448 | 1 | `public double onshoreCashPerKm()` | A pipeline's kilometre on land, in thousands (its own cash is a kilometre at sea); 0 for every other building. |

### THE SITE IT STANDS ON (0.7.110, batch I1; docs/studies/spec-materials.md 2.4, 2.5) (lines 450-484)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 4 | **type** `public record Site(Resource resource, Deposit.FieldType type, int sites, double dig)` | The site a building stands on: its resource, the type of field it needs (null for ANY), the sites it takes and the tonnes of field a unit it makes takes out. |
| 466 | 1 | `public boolean any()` _(in BuildingsTemplate.Site)_ | Whether it stands on a field of any type. |
| 472 | 9 | `public BuildingsTemplate setSite(Resource resource, Deposit.FieldType type, int sites, double dig)` | Makes this stand on `sites` of a resource's sites of a type (null for ANY), taking `dig` of the field a unit it makes; a type not the resource's reads as ANY. |
| 483 | 1 | `public Site site()` | The site it stands on, or null for a building that stands on no resource's sites. |

### THE POWER A WORKS MAKES ITSELF (0.7.112, batch I2; docs/studies/spec-materials.md 2.5, 2.8) (lines 485-518)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 4 | `public BuildingsTemplate setGen(double kw)` | Makes this building make `kw` of power a month itself; 0 or less is none. |
| 507 | 1 | `public double getGen()` | kW of power it makes itself; 0 for every building but the Coke Ovens, the Blast Furnace and the Oxygen Steel Shop (0.7.123). |
| 510 | 3 | `public double powerBought()` | kW it buys from the grid: its draw past what it makes - its whole draw, exactly, when it makes none. |
| 515 | 3 | `public double powerSold()` | kW it sells the grid: what it makes past its draw; nothing when it makes none. |

### WHAT A WORKS BUYS ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; docs/studies/spec-materials.md 2.5) (lines 519-540)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 4 | `public BuildingsTemplate setConsumables(double worldThousands)` | Gives this building `worldThousands` a month of consumables at nameplate, in the world's money; 0 or less is none. |
| 539 | 1 | `public double getConsumables()` | Its consumables a month at nameplate, thousands of the world's money; 0 for every building but the materials chains' rows (the iron rows since 0.7.120, the integrated route's since 0.7.123). |

### THE OLD METAL A YARD TAKES (0.7.123, batch I4; docs/studies/spec-materials.md 2.5, 2.9) (lines 541-564)

| line | len | member | says |
|---:|---:|---|---|
| 552 | 6 | `public BuildingsTemplate setOldMetal(Good metal, double tonnesAMonth)` | Makes this building take `tonnesAMonth` of the city's old metal of a kind at nameplate; 0 or less is none. |
| 560 | 1 | `public double oldMetal(Good metal)` | Tonnes of the city's old metal of this kind it takes a month at nameplate; 0 for every building but a yard. |
| 563 | 1 | `public java.util.Map<Good, Double> oldMetalTaken()` | Every kind of old metal it takes, tonnes a month at nameplate; empty for every building but a yard. |

### THE EQUIPMENT IT CAN BE FITTED WITH (R1, 2026-11-05; research-design.md 2.1-2.2) (lines 565-588)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 4 | `public BuildingsTemplate setEquipment(java.util.List<Equipment> pieces)` | Gives this building its equipment, in the order given; a copy, and null or empty is none. |
| 581 | 1 | `public java.util.List<Equipment> getEquipment()` | The pieces of equipment it can be fitted with, in the file's order; unmodifiable, empty for most buildings. |
| 584 | 4 | `public Equipment getEquipment(String id)` | The piece of this id, or null if the building carries none of it. |

### THE WAYS IT RUNS (R1c, 2026-11-05; research-design.md 2.1) (lines 589-792)

| line | len | member | says |
|---:|---:|---|---|
| 615 | 1 | `public java.util.List<RecipeChange> getRecipeChanges()` | Independent discovery undos, in code order, anchored to the immutable default. |
| 618 | 8 | `public BuildingsTemplate setRecipeChanges(java.util.List<RecipeChange> changes)` | Installs independent changes before the template first runs. |
| 628 | 3 | `public boolean changeNeeds(String piece)` | Whether an independent recipe change needs this piece. |
| 633 | 1 | `double recipeProduction()` | Utility output before any undo. |
| 636 | 1 | `double recipeLand()` | Ground before any undo. |
| 639 | 1 | `Site recipeSite()` | The immutable modern extraction coefficient and site identity. |
| 642 | 1 | `public double steamRailShare()` | Share of rail engines on steam, for the actual fuel invoice. |
| 645 | 1 | `public double crackerPetrol()` | Petrol per litre fed to this cracking unit after the chosen process. |
| 648 | 7 | `void runRecipeExtras(double production, double land, double steamRail, double petrol, double dig)` | Sets the non-goods figures of the independent changes. |
| 666 | 26 | `public BuildingsTemplate setRecipes(java.util.List<Recipe> list)` | Gives this building its recipes, in the order given; a copy, and null or empty is none (the one implicit default, its top level). |
| 694 | 3 | `public java.util.List<Recipe> getRecipes()` | Its recipes in the file's order; unmodifiable. |
| 699 | 6 | `public Recipe getRecipe(String id)` | The recipe of this id, or null if the building knows none of it. |
| 707 | 4 | `public Recipe defaultRecipe()` | The default recipe: its top level, today's way (the one entry of a list that states no figure). |
| 713 | 1 | `public boolean hasRecipeChoice()` | Whether it has a choice of ways to run: a list with more than the default in it. |
| 716 | 1 | `public String recipePieceId()` | The id of the piece of equipment its recipes need (at most one per template), or null where none does. |
| 719 | 1 | `public Equipment recipePiece()` | The piece of equipment its recipes need, the template's "recipe piece": the buildings that have it run the recipes that need it, those that have not the ones that need nothing; null where no recipe needs one. |
| 728 | 34 | `public void runRecipes(Recipe fitted, long fittedBuildings, Recipe unfitted, long unfittedBuildings)` | Sets this building's live figures to the recipes its two parts run, weighted by their buildings: `fitted` for `fittedBuildings` and `unfitted` for `unfittedBuildings` (a part with no building weighs nothing; with none... |
| 764 | 13 | `private void setFigures(Recipe r)` | Sets the live figures to one recipe's, to the bit. |
| 779 | 4 | `void setRunningRecipeIds(String[] ids)` | Retains the saved process decisions when independent equipment changes live figures. |
| 785 | 4 | `public String[] runningRecipeIds()` | The ids of the recipes the fitted part and the unfitted part last ran (runRecipes()), in that order; the default's where it has run none. |
| 791 | 1 | `boolean hasRunningRecipes()` | Whether this recipe list has reached production since it was installed. |

### WHEN IT FIRST EXISTED (0.7.123, batch I4; R2, 2026-11-05; research-design.md 2.1, 7) (lines 793-865)

| line | len | member | says |
|---:|---:|---|---|
| 816 | 1 | `public BuildingsTemplate setUntil(int year)` | Last calendar year for new orders, inclusive; zero means no end. |
| 818 | 1 | `public int getUntil()` |  |
| 821 | 1 | `public boolean offeredAt(int gameMonth)` | Whether new orders remain available at this date, independent of when the technology was discovered. |
| 824 | 3 | `public BuildingsTemplate setSince(int year)` | Dates this building by a year: the year it first existed, the start year (1900) for one that stood before it; 0 or less is undated. |
| 832 | 13 | `public BuildingsTemplate setSince(String text)` | Dates this building by a year ("1902") or a research catalogue code ("PE4"): blank, null or a year of 0 or less is undated. |
| 847 | 1 | `public int getSince()` | The year it first existed (a code's entry's year; 0 for a code that is not in the catalogue), or 0 for a row not yet dated. |
| 850 | 1 | `public String getSinceText()` | What the row says, as written: "" undated, "1902" a year, "PE4" a catalogue code. |
| 853 | 3 | `public boolean knownAt(int gameMonth)` | Whether the catalogue offers it at a game month (month 1 = January 1900): from its year/code's month through its last order year, if any. |
| 861 | 4 | `public boolean knownAt(int gameMonth, Research found)` | Whether it is known at a game month in a city whose research may have found its code first (R3): knownAt(int), or from the month the city found the code (Research.knownFrom()), while it remains offered. |

### THE RAW COAL A PREPARATION PLANT WASHES (0.7.126, batch I6; docs/studies/spec-materials.md 2.5, 2.10) (lines 866-1222)

| line | len | member | says |
|---:|---:|---|---|
| 880 | 4 | `public BuildingsTemplate setWash(double tonnesAMonth)` | Makes this a preparation plant washing `tonnesAMonth` of raw coal a month at nameplate; 0 or less is none. |
| 886 | 1 | `public double getWash()` | Tonnes of raw coal it washes a month at nameplate; 0 for every building but the Coal Preparation Plant (0.7.126). |
| 895 | 4 | `public BuildingsTemplate(String name, BuildingType category)` | barebones constructor |
| 943 | 5 | `public BuildingsTemplate setJobs(JobType type, int number)` | setters (method chaining) |
| 949 | 7 | `public BuildingsTemplate setCashCost(double cashCost)` |  |
| 965 | 13 | `BuildingsTemplate pricedAtFoundingLevel()` | THE FUNNEL FOR THE BUILDINGS' MONEY (EP1, Jerus 2026-10-30: "1900 price level"). |
| 979 | 4 | `public BuildingsTemplate setConstructionPoints(int constructionPoints)` |  |
| 984 | 4 | `public BuildingsTemplate setCapacity(int capacity)` |  |
| 989 | 6 | `public BuildingsTemplate setUpkeep(double upkeep)` |  |
| 1001 | 8 | `public BuildingsTemplate setFoundingCosts(double cash, double upkeep)` | Sets the cash cost and the upkeep from figures in founding money (R1g's dated home, Recipes.dateHomes()): the founding figures exactly these, today's struck from them at the unit the template was last struck at, as se... |
| 1010 | 4 | `public BuildingsTemplate setConstructionMaterials(int constructionMaterials)` |  |
| 1015 | 4 | `public BuildingsTemplate setElectricityConsumption(int electricityConsumption)` |  |
| 1020 | 4 | `public BuildingsTemplate setWaterConsumption(double waterConsumption)` |  |
| 1025 | 4 | `public BuildingsTemplate setLandSqFt(double landSqFt)` |  |
| 1030 | 4 | `public BuildingsTemplate setRoadLoad(double roadLoad)` |  |
| 1035 | 4 | `public BuildingsTemplate setCoverage(int coverage)` |  |
| 1040 | 4 | `public BuildingsTemplate setProduction1(double production1)` |  |
| 1045 | 4 | `public BuildingsTemplate setProduction2(double production2)` |  |
| 1050 | 4 | `public BuildingsTemplate setProductionModifier1(double productionModifier1)` |  |
| 1055 | 4 | `public BuildingsTemplate setProductionModifier2(double productionModifier2)` |  |
| 1060 | 4 | `public BuildingsTemplate setNationalized(boolean nationalized)` |  |
| 1065 | 4 | `public BuildingsTemplate setId(int id)` |  |
| 1070 | 4 | `public BuildingsTemplate setCare(CareType care)` |  |
| 1075 | 4 | `public BuildingsTemplate setSafety(SafetyType safety)` |  |
| 1080 | 4 | `public BuildingsTemplate setTeaches(EducationType teaches)` |  |
| 1086 | 4 | `public BuildingsTemplate setResearches(ResearchCatalog.Field field)` | The field this Research Lab researches (R4); null clears it. |
| 1091 | 4 | `public BuildingsTemplate setSource(Source source)` |  |
| 1097 | 3 | `public Source getSource()` | What it draws from: FRESH for every building but a desalination plant (see the field's note). |
| 1102 | 3 | `public boolean isSeaWater()` | A water works that draws the sea: a Desalination Plant (0.7.59). |
| 1107 | 3 | `public boolean isFreshWater()` | A water works that draws fresh water, held to the city's limit: a Water Treatment Plant. |
| 1112 | 3 | `public double getCashCost()` | getters |
| 1122 | 3 | `public double getFoundingCashCost()` | The cash cost as the catalogue states it (ET2): buildings.json's figure, in thousands of founding dollars, before the unit and the expected price level it is struck at (seedConstants()) - which the price level and a c... |
| 1127 | 3 | `public double getFoundingUpkeep()` | The upkeep as the catalogue states it, in founding money, as getFoundingCashCost() states the cash cost; the upkeep itself where none was struck. |
| 1131 | 3 | `public int getConstructionPoints()` |  |
| 1138 | 4 | `public HousingRecipe getHousingRecipe()` | Fixed dwelling/rights metadata, separate from the structure's physical recipe. |
| 1143 | 7 | `public BuildingsTemplate setHousingRecipe(HousingRecipe recipe)` |  |
| 1151 | 3 | `public int getDwellings()` |  |
| 1155 | 4 | `public BuildingsTemplate setDwellings(int dwellings)` |  |
| 1176 | 5 | `public int homeSize()` | How big a household one of these units takes, in people. |
| 1190 | 4 | `public boolean adultsOnly()` | True for a flat too small to put a child in. |
| 1195 | 3 | `public int getCapacity()` |  |
| 1199 | 3 | `public double getUpkeep()` |  |
| 1203 | 3 | `public int getConstructionMaterials()` |  |
| 1207 | 3 | `public int getElectricityConsumption()` |  |
| 1211 | 3 | `public double getWaterConsumption()` |  |
| 1215 | 3 | `public double getLandSqFt()` |  |
| 1219 | 3 | `public double getRoadLoad()` |  |

### WHAT A MODE IS GOOD AT (2026-09-16) (lines 1223-1349)

| line | len | member | says |
|---:|---:|---|---|
| 1245 | 4 | `public BuildingsTemplate setFreightGrade(double grade)` | How much of this road is built for lorries, 0 to 1. |
| 1250 | 1 | `public double getFreightGrade()` |  |
| 1259 | 1 | `public boolean isHighway()` | Whether this is a highway (FR7, 2026-10-18): a road that is fully grade-separated, a freight grade of 1. |
| 1269 | 4 | `public BuildingsTemplate setTransitCapacity(double riders)` | Commuter journeys a month this carries OFF the road, if it is transit. |
| 1274 | 1 | `public double getTransitCapacity()` |  |
| 1277 | 1 | `public boolean isTransit()` | True for a building whose whole purpose is carrying people. |
| 1289 | 4 | `public BuildingsTemplate setRailCapacity(double tonnes)` | Tonnes a month this can haul across the city boundary, if it is rail. |
| 1294 | 1 | `public double getRailCapacity()` |  |
| 1310 | 4 | `public BuildingsTemplate setStationTonnes(double tonnes)` | Tonnes a month a station sorts (FR7, 2026-10-18): the Rail Depot's 50,000 and the Rail Terminal's 500,000; none for every other building. |
| 1315 | 1 | `public double getStationTonnes()` |  |
| 1318 | 1 | `public boolean isStation()` | True for a building that is a station: it sorts tonnes (getStationTonnes() &gt; 0). |
| 1328 | 4 | `public BuildingsTemplate setSiding(boolean siding)` | Whether this track is a siding into one works (FR7): the Rail Spur, and no other. |
| 1333 | 1 | `public boolean isSiding()` |  |
| 1343 | 4 | `public BuildingsTemplate setHydro(boolean hydro)` | Whether this power station needs a river with a drop on the ground the city owns (HY1): the Hydro Station, and no other. |
| 1348 | 1 | `public boolean isHydro()` |  |

### THE POWER ROWS (PW1; spec-electricity 2.3, 2.5) (lines 1350-1404)

| line | len | member | says |
|---:|---:|---|---|
| 1367 | 1 | `public BuildingsTemplate setNameplate(double kw)` |  |
| 1368 | 1 | `public BuildingsTemplate setFuel(String good)` |  |
| 1369 | 1 | `public BuildingsTemplate setHeatRate(double btuPerKwh)` |  |
| 1370 | 1 | `public BuildingsTemplate setVariableUpkeep(double usdPerMwh)` |  |
| 1371 | 1 | `public BuildingsTemplate setVariable(String kind)` |  |
| 1374 | 1 | `public double getNameplate()` | The station's nameplate, kW: the row's, or with none its production1. |
| 1377 | 8 | `public Good getFuel()` | The good the station burns; null when it burns none or names one the game does not have. |
| 1386 | 1 | `public double getHeatRate()` |  |
| 1388 | 1 | `public double getVariableUpkeep()` |  |
| 1391 | 1 | `public boolean isWind()` | Whether this station's output is the wind's, shaped by the weather (PowerDispatch.windAt()). |
| 1394 | 1 | `public boolean isSolar()` | Whether this station's output is the sun's, shaped by the day and the weather (PowerDispatch.solarAt(); PW2): buildings.json "variable": "solar". |
| 1401 | 3 | `public double credit()` | The share of its average a station counts as firm alone (PW3): a wind farm's and a solar farm's output in the year's peak hours over its average with no other variable row standing, at the US fleet's place on a tracke... |

### THE COOLING WATER AND THE BUILD-TIME FLOOR (PW2, 2026-11-23; spec-electricity 2.3) (lines 1405-1472)

| line | len | member | says |
|---:|---:|---|---|
| 1423 | 1 | `public BuildingsTemplate setCooling(boolean cooling)` |  |
| 1425 | 1 | `public BuildingsTemplate setBuildMonths(int months)` |  |
| 1428 | 1 | `public boolean cooling()` | Whether it needs owned sea, lake or river to cool it (PW2): the steam rows. |
| 1431 | 1 | `public int buildMonths()` | The fewest months it is built in, whatever the crews; 0 for none (PW2). |
| 1434 | 5 | `public double fuelPerKwh()` | Units of fuel the station burns a kWh: its heat rate over the fuel's heat content; 0 with no fuel, no heat rate or no heat content. |
| 1449 | 4 | `public BuildingsTemplate setBridge(BridgeWorks.Kind kind)` | Which long bridge this is (ET6): the Highway Bridge, the Rail Bridge and the Road Bridge, and no other building. |
| 1455 | 1 | `public BridgeWorks.Kind bridgeKind()` | The kind of long bridge this is, or null for every other building. |
| 1458 | 1 | `public boolean isBridge()` | Whether this is a long bridge (ET6). |
| 1466 | 4 | `public BuildingsTemplate setTruckRoom(double trucks)` | Trucks a Truck Depot keeps (FR2): Freight's Haulage division's room; none for every other building. |
| 1471 | 1 | `public double getTruckRoom()` |  |

### WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16) (lines 1473-1727)

| line | len | member | says |
|---:|---:|---|---|
| 1527 | 45 | `public double loadOf(Traffic stream)` | This building's road load, split by what is actually moving. |
| 1573 | 3 | `public int getCoverage()` |  |
| 1577 | 3 | `public double getProduction1()` |  |
| 1581 | 3 | `public double getProduction2()` |  |
| 1585 | 3 | `public double getProductionModifier1()` |  |
| 1589 | 3 | `public double getProductionModifier2()` |  |
| 1593 | 3 | `public boolean getNationalized()` |  |
| 1596 | 3 | `public int getJobs(JobType type)` |  |
| 1600 | 3 | `public String getName()` |  |
| 1604 | 3 | `public BuildingType getCategory()` |  |
| 1608 | 3 | `public int getId()` |  |
| 1612 | 3 | `public CareType getCare()` |  |
| 1617 | 4 | `public BuildingsTemplate setRequiresLicence(JobType licence)` | The licence this building's practice needs, or null. |
| 1623 | 1 | `public JobType getRequiresLicence()` | The licence this building's practice needs, or null if it needs none. |
| 1626 | 3 | `public int getLicensedPosts()` | How many posts here need that licence - what the gate is measured against. |
| 1630 | 3 | `public SafetyType getSafety()` |  |
| 1634 | 3 | `public EducationType getTeaches()` |  |
| 1639 | 3 | `public ResearchCatalog.Field getResearches()` | The field this building is a Research Lab of (R4), or null for every other building. |
| 1647 | 5 | `public int getTotalJobs()` | sum of all jobs |
| 1666 | 6 | `public void redenominate(double scale)` | What a building costs and what it costs to run, in the new unit. |
| 1704 | 9 | `public void seedConstants(double unit)` | Re-seeds this template's price at a given unit (since 0.7.42, the unit over the expected price level). |
| 1720 | 6 | `private void rememberFounding()` | ...and a reform moves the founding figure with it, so that a LATER re-seed at the new unit lands in the same place. |


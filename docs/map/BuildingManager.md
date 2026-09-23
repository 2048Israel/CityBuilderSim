# BuildingManager.java - 4,286 lines · 106 methods · 4 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [JobType](JobType.md) (251), [BuildingsTemplate](BuildingsTemplate.md) (203), [Good](Good.md) (118), [BuildingType](BuildingType.md) (93), [BuildingsStacks](BuildingsStacks.md) (65), [CareType](CareType.md) (22), [EducationType](EducationType.md) (18), [SafetyType](SafetyType.md) (12), [BuildingInstance](BuildingInstance.md) (4), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (45):** [AgricultureCheck](AgricultureCheck.md), [BooksCheck](BooksCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | · WATER DRAW, per building, in units of 10,000 gallons/month. |
| 258 | · HEALTHCARE |
| 620 | · WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH |
| 744 | · A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of |
| 779 | · THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10). |
| 854 | · · WIND |
| 951 | · · STEEL |
| 1003 | · · INFRASTRUCTURE |
| 1135 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1227 | · · RAIL |
| 1328 | · · AUTOMOTIVE |
| 1379 | · THE LUXURY SHOPS (2026-09-17) |
| 1440 | · THE KITCHENS (2026-09-18) |
| 1630 | · · MINING |
| 1695 | · · EDUCATION |
| 1908 | · · SAFETY |
| 2019 | · THE BANK |
| 2045 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 2103 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 2189 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 2221 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 2381 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 2419 | · · And the same trade at scale. Two and a half times the shop's output |
| 2447 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 2548 | · · And the same thing at three times the size on two and a half times as |
| 2567 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 2603 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 2640 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 3355 | BY SECTOR (2026-09-11, the sector template) |
| 3756 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 4043 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3292 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 3315 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 4253 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `private List<BuildingsTemplate> templates` |  |
| 18 | `private List<BuildingsStacks> stacks` |  |
| 19 | `private List<BuildingInstance> instances` |  |
| 20 | `private JobType[] jobTypes` |  |
| 21 | `private int constructionMaterials` |  |
| 54 | `private double materialsCost` |  |
| 2934 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 2943 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 2954 | `public final String building` |  |
| 2955 | `public final int quantity` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 15 | 4272 | **type** `public class BuildingManager` |  |
| 56 | 3 | `public void setExchangeRate(double rate)` |  |
| 60 | 5 | `public BuildingManager()` |  |

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 66-3354)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 109 | 2690 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 2800 | 6 | `public void finalUpdateBuildings()` |  |
| 2808 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 2813 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 2848 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 2852 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 2864 | 3 | `public void addInstance(BuildingsTemplate template)` |  |
| 2873 | 4 | `public double outputPerSite(int constructionOutput)` | What each site gets of the month's output. |
| 2883 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 2903 | 24 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 2936 | 5 | `public double takeMaterialsDue()` |  |
| 2945 | 5 | `public double takeRevenueDue()` |  |
| 2952 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 2957 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 2963 | 7 | `public void displayAllBuildings()` |  |
| 2973 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 2991 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 2999 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 3008 | 13 | `public int[] getTotalJobs()` |  |
| 3028 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 3044 | 12 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 3072 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 3089 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 3114 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 3134 | 7 | `public int getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 3143 | 9 | `public int getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 3153 | 10 | `public int getUnderConstruction()` |  |
| 3163 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 3167 | 10 | `public int getTotalJobs(JobType type)` |  |
| 3193 | 13 | `public int getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 3214 | 21 | `public int[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 3237 | 13 | `public int getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 3251 | 5 | `public int getTotalHouseCapacity()` |  |
| 3264 | 3 | `public int getTotalStoreCoverage()` | Commercial Methods |
| 3269 | 3 | `public int getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 3317 | 7 | `public int getTotalConstructionCapacity()` |  |
| 3331 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 3347 | 3 | `public int getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 3351 | 3 | `public int getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 3355-3755)

| line | len | member | says |
|---:|---:|---|---|
| 3365 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 3376 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 3387 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 3396 | 9 | `public int[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier. |
| 3407 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 3418 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 3430 | 9 | `public int getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 3441 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 3448 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 3465 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 3479 | 13 | `public int getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 3493 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 3519 | 9 | `public int getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 3536 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 3561 | 14 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 3593 | 25 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 3631 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 3667 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 3689 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included. |
| 3701 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 3713 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 3724 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 3743 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 3756-4042)

| line | len | member | says |
|---:|---:|---|---|
| 3771 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 3783 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 3794 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 3807 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 3818 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 3832 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 3848 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 3855 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 3879 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 3891 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 3903 | 10 | `public int[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 3914 | 16 | `public int[] getJobArrayPerCategory(BuildingType category)` |  |
| 3934 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 3938 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 3943 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 3953 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 3962 | 3 | `public int getTemplateCount()` |  |
| 3966 | 9 | `public double[] getConstructionProgress()` |  |
| 3985 | 8 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 4009 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 4029 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 4033 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 4043-4286)

| line | len | member | says |
|---:|---:|---|---|
| 4067 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 4075 | 10 | `public double[] getConstructionProgressById()` |  |
| 4086 | 10 | `public int[] getUnderConstructionById()` |  |
| 4098 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 4110 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 4116 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 4122 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 4134 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 4146 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 4159 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 4173 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 4183 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 4193 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 4197 | 3 | `public void clearStacks()` |  |
| 4226 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 4237 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 4243 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 4255 | 4 | `static { ... }` |  |
| 4260 | 7 | `public void resetBuildingManager()` |  |
| 4270 | 7 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 4280 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit. |


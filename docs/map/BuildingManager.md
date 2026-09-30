# BuildingManager.java - 4,854 lines · 135 methods · 5 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (255), [BuildingsTemplate](BuildingsTemplate.md) (217), [Good](Good.md) (118), [BuildingType](BuildingType.md) (93), [BuildingsStacks](BuildingsStacks.md) (81), [CareType](CareType.md) (22), [EducationType](EducationType.md) (18), [SafetyType](SafetyType.md) (12), [BuildingInstance](BuildingInstance.md) (4), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (50):** [AgricultureCheck](AgricultureCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 60 | THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) |
| 203 | · WATER DRAW, per building, in units of 10,000 gallons/month. |
| 395 | · HEALTHCARE |
| 757 | · WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH |
| 881 | · A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of |
| 916 | · THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10). |
| 991 | · · WIND |
| 1088 | · · STEEL |
| 1140 | · · INFRASTRUCTURE |
| 1272 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1364 | · · RAIL |
| 1465 | · · AUTOMOTIVE |
| 1516 | · THE LUXURY SHOPS (2026-09-17) |
| 1577 | · THE KITCHENS (2026-09-18) |
| 1767 | · · MINING |
| 1832 | · · EDUCATION |
| 2045 | · · SAFETY |
| 2156 | · THE BANK |
| 2185 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 2246 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 2332 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 2364 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 2524 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 2562 | · · And the same trade at scale. Two and a half times the shop's output |
| 2590 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 2691 | · · And the same thing at three times the size on two and a half times as |
| 2710 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 2746 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 2783 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 3011 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 3335 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 3851 | BY SECTOR (2026-09-11, the sector template) |
| 4252 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 4539 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3099 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 3788 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 3811 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 4821 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private List<BuildingsTemplate> templates` |  |
| 22 | `private List<BuildingsStacks> stacks` |  |
| 23 | `private List<BuildingInstance> instances` |  |
| 24 | `private JobType[] jobTypes` |  |
| 25 | `private int constructionMaterials` |  |
| 58 | `private double materialsCost` |  |
| 119 | `private BuildersWages buildersWages` |  |
| 3315 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 3324 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 3331 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 3348 | `private java.util.function.ToDoubleFunction<String> offeredShare` | The share of a sector's posts it offers this month; null offers every post. |
| 3403 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 3414 | `public final String building` |  |
| 3415 | `public final int quantity` |  |
| 4626 | `public int templateId` |  |
| 4627 | `public String payer` |  |
| 4628 | `public boolean creditable` |  |
| 4630 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 4631 | `public double value, units, allowance` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 4836 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

### THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) (lines 60-202)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 6 | **type** `public interface BuildersWages` | What the builders' crews cost a point of output, today and at the founding ladder, both in today's money. |
| 114 | 1 | `double perPointToday()` _(in BuildingManager.BuildersWages)_ | A depot's posts at today's wages, over its points. |
| 116 | 1 | `double perPointAtFounding()` _(in BuildingManager.BuildersWages)_ | ...and at the founding ladder, PayTier, in today's unit. |
| 121 | 1 | `public void setBuildersWages(BuildersWages wages)` |  |
| 124 | 8 | `public double buildersWageIndex()` | The builders' labour index: their wage bill a point today over at founding. |
| 140 | 8 | `public double nonMaterialCost(BuildingsTemplate t)` | The non-material part of what one of these costs to build today (0.7.19): its cash cost, with its labour - its points at the builders' founding wage bill a point, never more than the whole cash cost - at today's build... |
| 150 | 7 | `public double labourCost(BuildingsTemplate t)` | ...its labour part alone, at today's wages. |
| 159 | 3 | `public double structureCost(BuildingsTemplate t)` | ...and with its materials at the world's price today: what it would cost to put up now, before the sales tax. |
| 172 | 20 | `public double nonMaterialPricePerPoint()` | What the builders are paid for a point of work, beyond its material and its sales tax (0.7.19): the non-material price of the work on site, averaged over the points still owed; with nothing on site, of the repairs - t... |
| 193 | 3 | `public void setExchangeRate(double rate)` |  |
| 197 | 5 | `public BuildingManager()` |  |

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 203-3010)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 246 | 2696 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 2943 | 6 | `public void finalUpdateBuildings()` |  |
| 2951 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 2956 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 2991 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 2995 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 3007 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 3011-3334)

| line | len | member | says |
|---:|---:|---|---|
| 3114 | 24 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 3140 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 3151 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 3178 | 3 | `static double weightOf(BuildingsTemplate t, int buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 3194 | 20 | `public double waitFor(BuildingsTemplate t, int quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 3216 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 3231 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 3242 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 3257 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 3277 | 31 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 3317 | 5 | `public double takeMaterialsDue()` |  |
| 3333 | 1 | `public double getPointsBuilt()` |  |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 3335-3850)

| line | len | member | says |
|---:|---:|---|---|
| 3350 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` |  |
| 3355 | 4 | `public static int postsOffered(int posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 3361 | 5 | `public double offeredShareOf(String sector)` | A sector's share of its posts on offer: 1 without the hook. |
| 3368 | 6 | `public int[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - its buildings' posts at its share. |
| 3390 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 3396 | 5 | `public double takeRevenueDue()` |  |
| 3405 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 3412 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 3417 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 3423 | 7 | `public void displayAllBuildings()` |  |
| 3433 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 3451 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 3459 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 3468 | 13 | `public int[] getTotalJobs()` |  |
| 3488 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 3504 | 12 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 3532 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 3549 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 3574 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 3594 | 7 | `public int getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 3603 | 9 | `public int getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 3613 | 10 | `public int getUnderConstruction()` |  |
| 3623 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 3627 | 5 | `public int getTotalJobs(JobType type)` |  |
| 3634 | 6 | `public int getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 3653 | 13 | `public int getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 3668 | 5 | `public int getPostsWithheld()` | getPostsWithheld() over every job type. |
| 3689 | 13 | `public int getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 3710 | 21 | `public int[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 3733 | 13 | `public int getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 3747 | 5 | `public int getTotalHouseCapacity()` |  |
| 3760 | 3 | `public int getTotalStoreCoverage()` | Commercial Methods |
| 3765 | 3 | `public int getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 3813 | 7 | `public int getTotalConstructionCapacity()` |  |
| 3827 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 3843 | 3 | `public int getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 3847 | 3 | `public int getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 3851-4251)

| line | len | member | says |
|---:|---:|---|---|
| 3861 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 3872 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 3883 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 3892 | 9 | `public int[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier. |
| 3903 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 3914 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 3926 | 9 | `public int getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 3937 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 3944 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 3961 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 3975 | 13 | `public int getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 3989 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 4015 | 9 | `public int getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 4032 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 4057 | 14 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 4089 | 25 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 4127 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 4163 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 4185 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included. |
| 4197 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 4209 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 4220 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 4239 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 4252-4538)

| line | len | member | says |
|---:|---:|---|---|
| 4267 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 4279 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 4290 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 4303 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 4314 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 4328 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 4344 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 4351 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 4375 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 4387 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 4399 | 10 | `public int[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 4410 | 16 | `public int[] getJobArrayPerCategory(BuildingType category)` |  |
| 4430 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 4434 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 4439 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 4449 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 4458 | 3 | `public int getTemplateCount()` |  |
| 4462 | 9 | `public double[] getConstructionProgress()` |  |
| 4481 | 8 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 4505 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 4525 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 4529 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 4539-4854)

| line | len | member | says |
|---:|---:|---|---|
| 4563 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 4571 | 10 | `public double[] getConstructionProgressById()` |  |
| 4582 | 10 | `public int[] getUnderConstructionById()` |  |
| 4594 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 4606 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 4612 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 4618 | 5 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 4625 | 8 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 4635 | 17 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 4654 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 4673 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 4690 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 4702 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 4714 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 4727 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 4741 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 4751 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 4761 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 4765 | 3 | `public void clearStacks()` |  |
| 4794 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 4805 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 4811 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 4823 | 4 | `static { ... }` |  |
| 4828 | 7 | `public void resetBuildingManager()` |  |
| 4838 | 7 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 4848 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit. |


# BuildingManager.java - 5,533 lines · 174 methods · 5 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (265), [BuildingsTemplate](BuildingsTemplate.md) (235), [Good](Good.md) (121), [BuildingsStacks](BuildingsStacks.md) (96), [BuildingType](BuildingType.md) (96), [ConstructionControl](ConstructionControl.md) (51), [CareType](CareType.md) (22), [EducationType](EducationType.md) (18), [SafetyType](SafetyType.md) (12), [BuildingInstance](BuildingInstance.md) (4), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (63):** [AgricultureCheck](AgricultureCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Founding](Founding.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 60 | THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) |
| 238 | · WATER DRAW, per building, in units of 10,000 gallons/month. |
| 430 | · HEALTHCARE |
| 792 | · WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH |
| 916 | · A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of |
| 951 | · THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10). |
| 1026 | · · WIND |
| 1153 | · · STEEL |
| 1205 | · · INFRASTRUCTURE |
| 1339 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1431 | · · RAIL |
| 1532 | · · AUTOMOTIVE |
| 1583 | · THE LUXURY SHOPS (2026-09-17) |
| 1644 | · THE KITCHENS (2026-09-18) |
| 1834 | · · MINING |
| 1899 | · · FUEL (0.7.62) |
| 1948 | · · EDUCATION |
| 2161 | · · SAFETY |
| 2272 | · THE BANK |
| 2301 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 2362 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 2448 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 2480 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 2640 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 2678 | · · And the same trade at scale. Two and a half times the shop's output |
| 2706 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 2807 | · · And the same thing at three times the size on two and a half times as |
| 2826 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 2862 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 2899 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 3151 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 3560 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 3802 | · ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that |
| 3997 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 4515 | BY SECTOR (2026-09-11, the sector template) |
| 4916 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 5215 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3246 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 4452 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 4475 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 5499 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private List<BuildingsTemplate> templates` |  |
| 22 | `private List<BuildingsStacks> stacks` |  |
| 23 | `private List<BuildingInstance> instances` |  |
| 24 | `private JobType[] jobTypes` |  |
| 25 | `private int constructionMaterials` |  |
| 58 | `private double materialsCost` |  |
| 121 | `private BuildersWages buildersWages` |  |
| 183 | `private double expectedLevel` | The expected price level the templates' cash was last struck at (Expectations.getStruckLevel(), the level the last month ended on); 1.0 until Game re-strikes. |
| 3540 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 3549 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 3556 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 3575 | `private final ConstructionControl control` | The player's hand on the queue: the order, the rushes, the shells, the demolitions and the buy-outs. |
| 3580 | `private ConstructionControl.Events controlEvents` | What this month's advance left for Game to settle: the overtime worked, the orders stopped, the demolitions done. |
| 3598 | `public final java.util.List<String> keys` |  |
| 3600 | `public final double output` | The site output it was struck on. |
| 3602 | `public final double[] owed, rule, share, work` | Points owed; the rule's share (siteShares()); the share after the city's order; the work, overtime in. |
| 3603 | `public final java.util.List<ConstructionControl.Overtime> overtime` |  |
| 3604 | `public final double overtimePoints` |  |
| 3676 | `private Plan lastPlan` | The plan the last month's advance applied, or null when the player's hand was not on the queue: a month's reading, not saved. |
| 4010 | `private java.util.function.ToDoubleFunction<String> offeredShare` | The share of a sector's posts it offers this month; null offers every post. |
| 4065 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 4076 | `public final String building` |  |
| 4077 | `public final int quantity` |  |
| 5304 | `public int templateId` |  |
| 5305 | `public String payer` |  |
| 5306 | `public boolean creditable` |  |
| 5308 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 5309 | `public double value, units, allowance` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 5515 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

### THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) (lines 60-237)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 6 | **type** `public interface BuildersWages` | What the builders' crews cost a point of output, today and at the founding ladder, both in today's money. |
| 116 | 1 | `double perPointToday()` _(in BuildingManager.BuildersWages)_ | A depot's posts at today's wages, over its points. |
| 118 | 1 | `double perPointAtFounding()` _(in BuildingManager.BuildersWages)_ | ...and at the founding ladder, PayTier, in today's unit. |
| 123 | 1 | `public void setBuildersWages(BuildersWages wages)` |  |
| 126 | 3 | `public double buildersWagePerPoint()` | What the builders' crews cost a point today (a depot's posts at today's wages, over its points): the bill a rushed site's overtime premium is struck on (0.7.22). |
| 131 | 8 | `public double buildersWageIndex()` | The builders' labour index: their wage bill a point today over at founding. |
| 147 | 12 | `public double nonMaterialCost(BuildingsTemplate t)` | The non-material part of what one of these costs to build today (0.7.19): its cash cost, with its labour - its points at the builders' founding wage bill a point, never more than the whole cash cost - at today's build... |
| 161 | 7 | `public double labourCost(BuildingsTemplate t)` | ...its labour part alone, at today's wages. |
| 186 | 3 | `public void setExpectedLevel(double level)` | Told by Game with every re-strike of the templates; anything not positive is ignored. |
| 191 | 1 | `public double getExpectedLevel()` | The level the cash costs are struck at. |
| 194 | 3 | `public double structureCost(BuildingsTemplate t)` | ...and with its materials at the world's price today: what it would cost to put up now, before the sales tax. |
| 207 | 20 | `public double nonMaterialPricePerPoint()` | What the builders are paid for a point of work, beyond its material and its sales tax (0.7.19): the non-material price of the work on site, averaged over the points still owed; with nothing on site, of the repairs - t... |
| 228 | 3 | `public void setExchangeRate(double rate)` |  |
| 232 | 5 | `public BuildingManager()` |  |

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 238-3150)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 281 | 2777 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 3059 | 6 | `public void finalUpdateBuildings()` |  |
| 3067 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 3073 | 5 | `public int[] stackOrder()` | The template ids of the stacks, in the order they stand: the order each was first bought (0.7.43; DataSave.getStackOrder()). |
| 3085 | 10 | `public void orderStacks(int[] ids)` | Stands the stacks in a saved order (0.7.43): each in the place its id holds in `ids`, any the order does not name after them in the order they stand. |
| 3096 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 3131 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 3135 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 3147 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 3151-3559)

| line | len | member | says |
|---:|---:|---|---|
| 3262 | 35 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 3303 | 4 | `static double demolitionWeight(ConstructionControl.Demolition site)` | A demolition's crew by the rule above (0.7.22): its buildings times the crew one of them can use, by the points its own demolition is - the whole of them, as a building's crew is struck on the whole building. |
| 3309 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 3320 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 3347 | 3 | `static double weightOf(BuildingsTemplate t, long buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 3363 | 4 | `public double waitFor(BuildingsTemplate t, int quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 3376 | 4 | `public double waitOnSite(BuildingsTemplate t, double siteOutput)` | Months what is already on site of one building would wait at this month's shares (0.7.20): waitFor()'s rule with no order added - the stack's own points still owed over its weight's share of everything on site. |
| 3382 | 27 | `private double waitWith(BuildingsTemplate t, int quantity, double siteOutput)` | waitFor()'s arithmetic, for an order of `quantity` - none, for what is on site alone. |
| 3411 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 3426 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 3437 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 3454 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 3474 | 59 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 3542 | 5 | `public double takeMaterialsDue()` |  |
| 3558 | 1 | `public double getPointsBuilt()` |  |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 3560-3801)

| line | len | member | says |
|---:|---:|---|---|
| 3577 | 1 | `public ConstructionControl getControl()` |  |
| 3582 | 5 | `public ConstructionControl.Events takeControlEvents()` |  |
| 3589 | 1 | `public double getOvertimePoints()` | The overtime's points this month past the sites' shares - negative from a rush's third month on. |
| 3597 | 27 | **type** `public static final class Plan` | One month's crews, site by site, as the advance applies them: the rule's share, the city's in the city's order, and a rushed site's month on overtime. |
| 3605 | 5 | `Plan(java.util.List<String> keys, double output, double[] owed, double[] rule, double[] share, double[] work, java.util.List<Co...` _(in BuildingManager.Plan)_ |  |
| 3611 | 1 | `public double shareOf(String key)` _(in BuildingManager.Plan)_ | This month's share of the crews, before any overtime. |
| 3613 | 1 | `public double workOf(String key)` _(in BuildingManager.Plan)_ | ...and the work the site does on it, overtime included. |
| 3615 | 1 | `public double owedOf(String key)` _(in BuildingManager.Plan)_ | Points still owed. |
| 3617 | 1 | `public double ruleOf(String key)` _(in BuildingManager.Plan)_ | The crews' rule's share, before the city's order. |
| 3619 | 4 | `public ConstructionControl.Overtime overtimeOf(String key)` _(in BuildingManager.Plan)_ | The month's overtime on one site, or null. |
| 3632 | 42 | `public Plan plan(double siteOutput)` | The month's crews at this site output, as the advance would apply them: siteShares(), then the city's share in the city's order (applyPriority()), then each rushed site's month on overtime. |
| 3678 | 1 | `public Plan getLastPlan()` |  |
| 3686 | 14 | `static void applyPriority(double[] share, double[] owed, java.util.List<String> keys, java.util.List<String> order)` | A. |
| 3702 | 6 | `public java.util.List<String> citySiteKeys()` | The city's own sites on site, in the stacks' order and then the demolitions': the ones its order is set over. |
| 3710 | 3 | `public java.util.List<String> cityOrder()` | ...in the order the city's crews serve them: the player's, or the stacks' order with none set. |
| 3715 | 6 | `public java.util.List<String> siteKeysOnSite()` | Every site with work on it: the stacks with buildings on site, and the demolitions. |
| 3723 | 5 | `public boolean isCitySite(String key)` | Whether a site is the city's own: a stack whose every order on site is the city's, or a demolition. |
| 3730 | 5 | `public BuildingsStacks stackOfKey(String key)` | The stack a site's key names, or null. |
| 3737 | 6 | `public String nameOfSite(String key)` | A site's name: its building's, "Demolishing" before a demolition's. |
| 3745 | 20 | `private void advanceDemolitions(Plan plan)` | D. |
| 3773 | 15 | `private void stopCancelled()` | C. |
| 3790 | 11 | `public void resumeShell(ConstructionControl.Shell shell)` | A shell put back on site (Game.restartShell()): its buildings, the work in them and what they still owe, onto its stack. |

### ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that (lines 3802-3996)

| line | len | member | says |
|---:|---:|---|---|
| 3825 | 3 | `private boolean handOn(String key)` | Whether the player's hand times this site: a city site while an order is set, or a rushed one. |
| 3835 | 9 | `public double siteMonths(String key, double siteOutput)` | Months a site on site would take at today's queue: its points over its weight's share of everything on site, by the rule held steady - or as the player's hand times it (handWait()). |
| 3852 | 7 | `public double cityOrderWait(BuildingsTemplate t, int quantity, double siteOutput)` | Months n buildings of t would take if the city ordered them now: with no order set and no rush on t's site, waitFor() exactly - the quote's wait since 0.7.17; otherwise as the hand times it, the order joining t's site... |
| 3866 | 7 | `public double restartWait(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months a shell of n buildings owing `owedPoints` would take if it were restarted now (0.7.22), beside whatever of it is on site already: by the rule as waitFor() times an order, or as the hand times it, as cityOrderWa... |
| 3874 | 4 | `private boolean isOnSite(BuildingsTemplate t)` |  |
| 3885 | 11 | `private double[] joined(BuildingsTemplate t, int n, double owed)` | What t's site would be with n more of it owing `owed` more points on it - {its crew's weight, its points owed} - when the player's hand times it: a city site (or none, which the city's order would make one) while an o... |
| 3898 | 1 | `public double pointsOwedOnSite()` | Points owed on every site, demolitions included: everything owed. |
| 3907 | 8 | `public double waitForDemolition(int n, double points, double siteOutput)` | Months a demolition of `points` over n buildings would take if ordered now, at today's queue (0.7.22): its own crew (demolitionWeight()) by the rule held steady - and with the city's order set, behind everything the c... |
| 3922 | 19 | `public double siteMonthsWith(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months n buildings of t owing `owedPoints` would take if put on site now - a shell restarted (0.7.22): beside whatever of it is on site already, as waitFor() times an order. |
| 3951 | 45 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput)` | THE HAND'S ARITHMETIC: every site on site, its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, and added at the bottom of the city's order when it is a new site of the city's - then th... |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 3997-4514)

| line | len | member | says |
|---:|---:|---|---|
| 4012 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` |  |
| 4017 | 4 | `public static long postsOffered(long posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 4023 | 5 | `public double offeredShareOf(String sector)` | A sector's share of its posts on offer: 1 without the hook. |
| 4030 | 6 | `public long[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - its buildings' posts at its share. |
| 4052 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 4058 | 5 | `public double takeRevenueDue()` |  |
| 4067 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 4074 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 4079 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 4085 | 7 | `public void displayAllBuildings()` |  |
| 4095 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 4113 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 4121 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 4130 | 13 | `public long[] getTotalJobs()` |  |
| 4150 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 4166 | 14 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 4196 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 4213 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 4238 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 4258 | 7 | `public long getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 4267 | 9 | `public long getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 4277 | 10 | `public int getUnderConstruction()` |  |
| 4287 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 4291 | 5 | `public long getTotalJobs(JobType type)` |  |
| 4298 | 6 | `public long getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 4317 | 13 | `public long getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 4332 | 5 | `public long getPostsWithheld()` | getPostsWithheld() over every job type. |
| 4353 | 13 | `public long getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 4374 | 21 | `public long[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 4397 | 13 | `public long getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 4411 | 5 | `public long getTotalHouseCapacity()` |  |
| 4424 | 3 | `public long getTotalStoreCoverage()` | Commercial Methods |
| 4429 | 3 | `public long getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 4477 | 7 | `public long getTotalConstructionCapacity()` |  |
| 4491 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 4507 | 3 | `public long getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 4511 | 3 | `public long getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 4515-4915)

| line | len | member | says |
|---:|---:|---|---|
| 4525 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 4536 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 4547 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 4556 | 9 | `public long[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier. |
| 4567 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 4578 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 4590 | 9 | `public long getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 4601 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 4608 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 4625 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 4639 | 13 | `public long getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 4653 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 4679 | 9 | `public long getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 4696 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 4721 | 14 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 4753 | 25 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 4791 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 4827 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 4849 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included. |
| 4861 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 4873 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 4884 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 4903 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 4916-5214)

| line | len | member | says |
|---:|---:|---|---|
| 4931 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 4943 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 4954 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 4967 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 4978 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 4992 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 5008 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 5015 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 5039 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 5051 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 5063 | 10 | `public long[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 5074 | 16 | `public long[] getJobArrayPerCategory(BuildingType category)` |  |
| 5094 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 5098 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 5103 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 5113 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 5122 | 3 | `public int getTemplateCount()` |  |
| 5126 | 9 | `public double[] getConstructionProgress()` |  |
| 5145 | 20 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 5181 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 5201 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 5205 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 5215-5533)

| line | len | member | says |
|---:|---:|---|---|
| 5239 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 5247 | 10 | `public double[] getConstructionProgressById()` |  |
| 5258 | 10 | `public int[] getUnderConstructionById()` |  |
| 5270 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 5282 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 5288 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 5294 | 7 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 5303 | 8 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 5313 | 17 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 5332 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 5351 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 5368 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 5380 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 5392 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 5405 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 5419 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 5429 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 5439 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 5443 | 3 | `public void clearStacks()` |  |
| 5472 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 5483 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 5489 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 5501 | 4 | `static { ... }` |  |
| 5506 | 7 | `public void resetBuildingManager()` |  |
| 5516 | 8 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 5527 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit - since 0.7.42 the unit over the expected price level, every month (Game.restrikeMoneyConstants()). |


# BuildingManager.java - 6,099 lines · 176 methods · 5 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (337), [BuildingsTemplate](BuildingsTemplate.md) (284), [Good](Good.md) (128), [BuildingType](BuildingType.md) (121), [BuildingsStacks](BuildingsStacks.md) (98), [ConstructionControl](ConstructionControl.md) (56), [CareType](CareType.md) (22), [EducationType](EducationType.md) (18), [RefineryFlow](RefineryFlow.md) (14), [SafetyType](SafetyType.md) (12), [BuildingInstance](BuildingInstance.md) (4), [Resource](Resource.md) (4), [Ports](Ports.md) (4), [Automotive](Automotive.md) (3), [Manufacturing](Manufacturing.md) (3), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (78):** [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Founding](Founding.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [ZoningView](ZoningView.md)

## Sections

| line | section |
|---:|---|
| 60 | THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) |
| 238 | · WATER DRAW, per building, in units of 10,000 gallons/month. |
| 430 | · HEALTHCARE |
| 470 | · · childcare, resized (0.7.71, batch N2) |
| 832 | · WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH |
| 956 | · A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of |
| 991 | · THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10). |
| 1066 | · · WIND |
| 1193 | · · STEEL |
| 1245 | · · INFRASTRUCTURE |
| 1382 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1474 | · · RAIL |
| 1575 | · · AUTOMOTIVE |
| 1627 | · THE LUXURY SHOPS (2026-09-17) |
| 1688 | · THE KITCHENS (2026-09-18) |
| 1880 | · · MINING |
| 1946 | · · FUEL (0.7.62) |
| 2002 | · · THE REFINERY'S UNITS (0.7.80, batch O4) |
| 2264 | · · THE FILLING STATION (0.7.83) |
| 2293 | · · THE TANK FARM AND THE STRATEGIC RESERVE (0.7.85) |
| 2340 | · · THE PORTS (0.7.86) |
| 2402 | · · THE OIL AT SEA (0.7.91) |
| 2459 | · · EDUCATION |
| 2672 | · · SAFETY |
| 2783 | · THE BANK |
| 2812 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 2873 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 2959 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 2991 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 3152 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 3191 | · · And the same trade at scale. Two and a half times the shop's output |
| 3220 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 3321 | · · And the same thing at three times the size on two and a half times as |
| 3340 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 3376 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 3413 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 3665 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 4076 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 4342 | · ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that |
| 4537 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 5055 | BY SECTOR (2026-09-11, the sector template) |
| 5472 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 5781 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3760 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 4992 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 5015 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 6065 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 4056 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 4065 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 4072 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 4091 | `private final ConstructionControl control` | The player's hand on the queue: the order, the rushes, the shells, the demolitions and the buy-outs. |
| 4096 | `private ConstructionControl.Events controlEvents` | What this month's advance left for Game to settle: the overtime worked, the orders stopped, the demolitions done. |
| 4114 | `public final java.util.List<String> keys` |  |
| 4116 | `public final double output` | The site output it was struck on. |
| 4118 | `public final double[] owed, rule, share, work` | Points owed; the rule's share (siteShares()); the share after the city's order; the work, overtime in. |
| 4119 | `public final java.util.List<ConstructionControl.Overtime> overtime` |  |
| 4120 | `public final double overtimePoints` |  |
| 4192 | `private Plan lastPlan` | The plan the last month's advance applied, or null when the player's hand was not on the queue: a month's reading, not saved. |
| 4550 | `private java.util.function.ToDoubleFunction<String> offeredShare` | The share of a sector's posts it offers this month; null offers every post. |
| 4605 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 4616 | `public final String building` |  |
| 4617 | `public final int quantity` |  |
| 5870 | `public int templateId` |  |
| 5871 | `public String payer` |  |
| 5872 | `public boolean creditable` |  |
| 5874 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 5875 | `public double value, units, allowance` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 6081 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

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

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 238-3664)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 281 | 3291 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 3573 | 6 | `public void finalUpdateBuildings()` |  |
| 3581 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 3587 | 5 | `public int[] stackOrder()` | The template ids of the stacks, in the order they stand: the order each was first bought (0.7.43; DataSave.getStackOrder()). |
| 3599 | 10 | `public void orderStacks(int[] ids)` | Stands the stacks in a saved order (0.7.43): each in the place its id holds in `ids`, any the order does not name after them in the order they stand. |
| 3610 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 3645 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 3649 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 3661 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 3665-4075)

| line | len | member | says |
|---:|---:|---|---|
| 3776 | 35 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 3817 | 4 | `static double demolitionWeight(ConstructionControl.Demolition site)` | A demolition's crew by the rule above (0.7.22): its buildings times the crew one of them can use, by the points its own demolition is - the whole of them, as a building's crew is struck on the whole building. |
| 3823 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 3834 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 3861 | 3 | `static double weightOf(BuildingsTemplate t, long buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 3877 | 4 | `public double waitFor(BuildingsTemplate t, int quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 3890 | 4 | `public double waitOnSite(BuildingsTemplate t, double siteOutput)` | Months what is already on site of one building would wait at this month's shares (0.7.20): waitFor()'s rule with no order added - the stack's own points still owed over its weight's share of everything on site. |
| 3896 | 27 | `private double waitWith(BuildingsTemplate t, int quantity, double siteOutput)` | waitFor()'s arithmetic, for an order of `quantity` - none, for what is on site alone. |
| 3925 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 3940 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 3951 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 3968 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 3988 | 61 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 4058 | 5 | `public double takeMaterialsDue()` |  |
| 4074 | 1 | `public double getPointsBuilt()` |  |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 4076-4341)

| line | len | member | says |
|---:|---:|---|---|
| 4093 | 1 | `public ConstructionControl getControl()` |  |
| 4098 | 5 | `public ConstructionControl.Events takeControlEvents()` |  |
| 4105 | 1 | `public double getOvertimePoints()` | The overtime's points this month past the sites' shares - negative from a rush's third month on. |
| 4113 | 27 | **type** `public static final class Plan` | One month's crews, site by site, as the advance applies them: the rule's share, the city's in the city's order, and a rushed site's month on overtime. |
| 4121 | 5 | `Plan(java.util.List<String> keys, double output, double[] owed, double[] rule, double[] share, double[] work, java.util.List<Co...` _(in BuildingManager.Plan)_ |  |
| 4127 | 1 | `public double shareOf(String key)` _(in BuildingManager.Plan)_ | This month's share of the crews, before any overtime. |
| 4129 | 1 | `public double workOf(String key)` _(in BuildingManager.Plan)_ | ...and the work the site does on it, overtime included. |
| 4131 | 1 | `public double owedOf(String key)` _(in BuildingManager.Plan)_ | Points still owed. |
| 4133 | 1 | `public double ruleOf(String key)` _(in BuildingManager.Plan)_ | The crews' rule's share, before the city's order. |
| 4135 | 4 | `public ConstructionControl.Overtime overtimeOf(String key)` _(in BuildingManager.Plan)_ | The month's overtime on one site, or null. |
| 4148 | 42 | `public Plan plan(double siteOutput)` | The month's crews at this site output, as the advance would apply them: siteShares(), then the city's share in the city's order (applyPriority()), then each rushed site's month on overtime. |
| 4194 | 1 | `public Plan getLastPlan()` |  |
| 4202 | 14 | `static void applyPriority(double[] share, double[] owed, java.util.List<String> keys, java.util.List<String> order)` | A. |
| 4218 | 6 | `public java.util.List<String> citySiteKeys()` | The city's own sites on site, in the stacks' order and then the demolitions': the ones its order is set over. |
| 4226 | 3 | `public java.util.List<String> cityOrder()` | ...in the order the city's crews serve them: the player's, or the stacks' order with none set. |
| 4231 | 6 | `public java.util.List<String> siteKeysOnSite()` | Every site with work on it: the stacks with buildings on site, and the demolitions. |
| 4239 | 5 | `public boolean isCitySite(String key)` | Whether a site is the city's own: a stack whose every order on site is the city's, or a demolition. |
| 4246 | 5 | `public BuildingsStacks stackOfKey(String key)` | The stack a site's key names, or null. |
| 4253 | 6 | `public String nameOfSite(String key)` | A site's name: its building's, "Demolishing" before a demolition's. |
| 4261 | 20 | `private void advanceDemolitions(Plan plan)` | D. |
| 4289 | 17 | `private void stopCancelled()` | C. |
| 4317 | 11 | `private void retirePaved(BuildingsStacks stack)` | F. |
| 4330 | 11 | `public void resumeShell(ConstructionControl.Shell shell)` | A shell put back on site (Game.restartShell()): its buildings, the work in them and what they still owe, onto its stack. |

### ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that (lines 4342-4536)

| line | len | member | says |
|---:|---:|---|---|
| 4365 | 3 | `private boolean handOn(String key)` | Whether the player's hand times this site: a city site while an order is set, or a rushed one. |
| 4375 | 9 | `public double siteMonths(String key, double siteOutput)` | Months a site on site would take at today's queue: its points over its weight's share of everything on site, by the rule held steady - or as the player's hand times it (handWait()). |
| 4392 | 7 | `public double cityOrderWait(BuildingsTemplate t, int quantity, double siteOutput)` | Months n buildings of t would take if the city ordered them now: with no order set and no rush on t's site, waitFor() exactly - the quote's wait since 0.7.17; otherwise as the hand times it, the order joining t's site... |
| 4406 | 7 | `public double restartWait(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months a shell of n buildings owing `owedPoints` would take if it were restarted now (0.7.22), beside whatever of it is on site already: by the rule as waitFor() times an order, or as the hand times it, as cityOrderWa... |
| 4414 | 4 | `private boolean isOnSite(BuildingsTemplate t)` |  |
| 4425 | 11 | `private double[] joined(BuildingsTemplate t, int n, double owed)` | What t's site would be with n more of it owing `owed` more points on it - {its crew's weight, its points owed} - when the player's hand times it: a city site (or none, which the city's order would make one) while an o... |
| 4438 | 1 | `public double pointsOwedOnSite()` | Points owed on every site, demolitions included: everything owed. |
| 4447 | 8 | `public double waitForDemolition(int n, double points, double siteOutput)` | Months a demolition of `points` over n buildings would take if ordered now, at today's queue (0.7.22): its own crew (demolitionWeight()) by the rule held steady - and with the city's order set, behind everything the c... |
| 4462 | 19 | `public double siteMonthsWith(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months n buildings of t owing `owedPoints` would take if put on site now - a shell restarted (0.7.22): beside whatever of it is on site already, as waitFor() times an order. |
| 4491 | 45 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput)` | THE HAND'S ARITHMETIC: every site on site, its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, and added at the bottom of the city's order when it is a new site of the city's - then th... |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 4537-5054)

| line | len | member | says |
|---:|---:|---|---|
| 4552 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` |  |
| 4557 | 4 | `public static long postsOffered(long posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 4563 | 5 | `public double offeredShareOf(String sector)` | A sector's share of its posts on offer: 1 without the hook. |
| 4570 | 6 | `public long[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - its buildings' posts at its share. |
| 4592 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 4598 | 5 | `public double takeRevenueDue()` |  |
| 4607 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 4614 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 4619 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 4625 | 7 | `public void displayAllBuildings()` |  |
| 4635 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 4653 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 4661 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 4670 | 13 | `public long[] getTotalJobs()` |  |
| 4690 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 4706 | 14 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 4736 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 4753 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 4778 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 4798 | 7 | `public long getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 4807 | 9 | `public long getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 4817 | 10 | `public int getUnderConstruction()` |  |
| 4827 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 4831 | 5 | `public long getTotalJobs(JobType type)` |  |
| 4838 | 6 | `public long getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 4857 | 13 | `public long getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 4872 | 5 | `public long getPostsWithheld()` | getPostsWithheld() over every job type. |
| 4893 | 13 | `public long getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 4914 | 21 | `public long[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 4937 | 13 | `public long getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 4951 | 5 | `public long getTotalHouseCapacity()` |  |
| 4964 | 3 | `public long getTotalStoreCoverage()` | Commercial Methods |
| 4969 | 3 | `public long getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 5017 | 7 | `public long getTotalConstructionCapacity()` |  |
| 5031 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 5047 | 3 | `public long getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 5051 | 3 | `public long getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 5055-5471)

| line | len | member | says |
|---:|---:|---|---|
| 5065 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 5076 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 5087 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 5096 | 9 | `public long[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier. |
| 5107 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 5118 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 5135 | 9 | `public double getBuildingMaterialsBySector(String sector)` | The construction materials in a sector's buildings, finished and on site, in units (0.7.75, the sector statements' R6): what getBuildingsValueBySector() prices at today's materials, so a move in that price moves the s... |
| 5146 | 9 | `public long getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 5157 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 5164 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 5181 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 5195 | 13 | `public long getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 5209 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 5235 | 9 | `public long getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 5252 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 5277 | 14 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 5309 | 25 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 5347 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 5383 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 5405 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included. |
| 5417 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 5429 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 5440 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 5459 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 5472-5780)

| line | len | member | says |
|---:|---:|---|---|
| 5487 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 5499 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 5510 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 5523 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 5534 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 5548 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 5564 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 5571 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 5595 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 5607 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 5619 | 10 | `public long[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 5630 | 16 | `public long[] getJobArrayPerCategory(BuildingType category)` |  |
| 5650 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 5654 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 5659 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 5669 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 5678 | 3 | `public int getTemplateCount()` |  |
| 5682 | 9 | `public double[] getConstructionProgress()` |  |
| 5701 | 27 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 5747 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 5767 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 5771 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 5781-6099)

| line | len | member | says |
|---:|---:|---|---|
| 5805 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 5813 | 10 | `public double[] getConstructionProgressById()` |  |
| 5824 | 10 | `public int[] getUnderConstructionById()` |  |
| 5836 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 5848 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 5854 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 5860 | 7 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 5869 | 8 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 5879 | 17 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 5898 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 5917 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 5934 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 5946 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 5958 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 5971 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 5985 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 5995 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 6005 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 6009 | 3 | `public void clearStacks()` |  |
| 6038 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 6049 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 6055 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 6067 | 4 | `static { ... }` |  |
| 6072 | 7 | `public void resetBuildingManager()` |  |
| 6082 | 8 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 6093 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit - since 0.7.42 the unit over the expected price level, every month (Game.restrikeMoneyConstants()). |


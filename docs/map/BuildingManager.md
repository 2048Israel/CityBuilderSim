# BuildingManager.java - 6,089 lines · 176 methods · 5 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (337), [BuildingsTemplate](BuildingsTemplate.md) (284), [Good](Good.md) (128), [BuildingType](BuildingType.md) (121), [BuildingsStacks](BuildingsStacks.md) (98), [ConstructionControl](ConstructionControl.md) (56), [CareType](CareType.md) (22), [EducationType](EducationType.md) (18), [RefineryFlow](RefineryFlow.md) (14), [SafetyType](SafetyType.md) (12), [BuildingInstance](BuildingInstance.md) (4), [Ports](Ports.md) (4), [Automotive](Automotive.md) (3), [Manufacturing](Manufacturing.md) (3), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (76):** [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Founding](Founding.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

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
| 1379 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1471 | · · RAIL |
| 1572 | · · AUTOMOTIVE |
| 1624 | · THE LUXURY SHOPS (2026-09-17) |
| 1685 | · THE KITCHENS (2026-09-18) |
| 1877 | · · MINING |
| 1942 | · · FUEL (0.7.62) |
| 1997 | · · THE REFINERY'S UNITS (0.7.80, batch O4) |
| 2259 | · · THE FILLING STATION (0.7.83) |
| 2288 | · · THE TANK FARM AND THE STRATEGIC RESERVE (0.7.85) |
| 2335 | · · THE PORTS (0.7.86) |
| 2397 | · · THE OIL AT SEA (0.7.91) |
| 2452 | · · EDUCATION |
| 2665 | · · SAFETY |
| 2776 | · THE BANK |
| 2805 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 2866 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 2952 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 2984 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 3145 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 3184 | · · And the same trade at scale. Two and a half times the shop's output |
| 3213 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 3314 | · · And the same thing at three times the size on two and a half times as |
| 3333 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 3369 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 3406 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 3658 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 4069 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 4335 | · ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that |
| 4530 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 5048 | BY SECTOR (2026-09-11, the sector template) |
| 5465 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 5771 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 3753 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 4985 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 5008 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 6055 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 4049 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 4058 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 4065 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 4084 | `private final ConstructionControl control` | The player's hand on the queue: the order, the rushes, the shells, the demolitions and the buy-outs. |
| 4089 | `private ConstructionControl.Events controlEvents` | What this month's advance left for Game to settle: the overtime worked, the orders stopped, the demolitions done. |
| 4107 | `public final java.util.List<String> keys` |  |
| 4109 | `public final double output` | The site output it was struck on. |
| 4111 | `public final double[] owed, rule, share, work` | Points owed; the rule's share (siteShares()); the share after the city's order; the work, overtime in. |
| 4112 | `public final java.util.List<ConstructionControl.Overtime> overtime` |  |
| 4113 | `public final double overtimePoints` |  |
| 4185 | `private Plan lastPlan` | The plan the last month's advance applied, or null when the player's hand was not on the queue: a month's reading, not saved. |
| 4543 | `private java.util.function.ToDoubleFunction<String> offeredShare` | The share of a sector's posts it offers this month; null offers every post. |
| 4598 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 4609 | `public final String building` |  |
| 4610 | `public final int quantity` |  |
| 5860 | `public int templateId` |  |
| 5861 | `public String payer` |  |
| 5862 | `public boolean creditable` |  |
| 5864 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 5865 | `public double value, units, allowance` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 6071 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

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

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 238-3657)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 281 | 3284 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 3566 | 6 | `public void finalUpdateBuildings()` |  |
| 3574 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 3580 | 5 | `public int[] stackOrder()` | The template ids of the stacks, in the order they stand: the order each was first bought (0.7.43; DataSave.getStackOrder()). |
| 3592 | 10 | `public void orderStacks(int[] ids)` | Stands the stacks in a saved order (0.7.43): each in the place its id holds in `ids`, any the order does not name after them in the order they stand. |
| 3603 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 3638 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 3642 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 3654 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 3658-4068)

| line | len | member | says |
|---:|---:|---|---|
| 3769 | 35 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 3810 | 4 | `static double demolitionWeight(ConstructionControl.Demolition site)` | A demolition's crew by the rule above (0.7.22): its buildings times the crew one of them can use, by the points its own demolition is - the whole of them, as a building's crew is struck on the whole building. |
| 3816 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 3827 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 3854 | 3 | `static double weightOf(BuildingsTemplate t, long buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 3870 | 4 | `public double waitFor(BuildingsTemplate t, int quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 3883 | 4 | `public double waitOnSite(BuildingsTemplate t, double siteOutput)` | Months what is already on site of one building would wait at this month's shares (0.7.20): waitFor()'s rule with no order added - the stack's own points still owed over its weight's share of everything on site. |
| 3889 | 27 | `private double waitWith(BuildingsTemplate t, int quantity, double siteOutput)` | waitFor()'s arithmetic, for an order of `quantity` - none, for what is on site alone. |
| 3918 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 3933 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 3944 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 3961 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 3981 | 61 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 4051 | 5 | `public double takeMaterialsDue()` |  |
| 4067 | 1 | `public double getPointsBuilt()` |  |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 4069-4334)

| line | len | member | says |
|---:|---:|---|---|
| 4086 | 1 | `public ConstructionControl getControl()` |  |
| 4091 | 5 | `public ConstructionControl.Events takeControlEvents()` |  |
| 4098 | 1 | `public double getOvertimePoints()` | The overtime's points this month past the sites' shares - negative from a rush's third month on. |
| 4106 | 27 | **type** `public static final class Plan` | One month's crews, site by site, as the advance applies them: the rule's share, the city's in the city's order, and a rushed site's month on overtime. |
| 4114 | 5 | `Plan(java.util.List<String> keys, double output, double[] owed, double[] rule, double[] share, double[] work, java.util.List<Co...` _(in BuildingManager.Plan)_ |  |
| 4120 | 1 | `public double shareOf(String key)` _(in BuildingManager.Plan)_ | This month's share of the crews, before any overtime. |
| 4122 | 1 | `public double workOf(String key)` _(in BuildingManager.Plan)_ | ...and the work the site does on it, overtime included. |
| 4124 | 1 | `public double owedOf(String key)` _(in BuildingManager.Plan)_ | Points still owed. |
| 4126 | 1 | `public double ruleOf(String key)` _(in BuildingManager.Plan)_ | The crews' rule's share, before the city's order. |
| 4128 | 4 | `public ConstructionControl.Overtime overtimeOf(String key)` _(in BuildingManager.Plan)_ | The month's overtime on one site, or null. |
| 4141 | 42 | `public Plan plan(double siteOutput)` | The month's crews at this site output, as the advance would apply them: siteShares(), then the city's share in the city's order (applyPriority()), then each rushed site's month on overtime. |
| 4187 | 1 | `public Plan getLastPlan()` |  |
| 4195 | 14 | `static void applyPriority(double[] share, double[] owed, java.util.List<String> keys, java.util.List<String> order)` | A. |
| 4211 | 6 | `public java.util.List<String> citySiteKeys()` | The city's own sites on site, in the stacks' order and then the demolitions': the ones its order is set over. |
| 4219 | 3 | `public java.util.List<String> cityOrder()` | ...in the order the city's crews serve them: the player's, or the stacks' order with none set. |
| 4224 | 6 | `public java.util.List<String> siteKeysOnSite()` | Every site with work on it: the stacks with buildings on site, and the demolitions. |
| 4232 | 5 | `public boolean isCitySite(String key)` | Whether a site is the city's own: a stack whose every order on site is the city's, or a demolition. |
| 4239 | 5 | `public BuildingsStacks stackOfKey(String key)` | The stack a site's key names, or null. |
| 4246 | 6 | `public String nameOfSite(String key)` | A site's name: its building's, "Demolishing" before a demolition's. |
| 4254 | 20 | `private void advanceDemolitions(Plan plan)` | D. |
| 4282 | 17 | `private void stopCancelled()` | C. |
| 4310 | 11 | `private void retirePaved(BuildingsStacks stack)` | F. |
| 4323 | 11 | `public void resumeShell(ConstructionControl.Shell shell)` | A shell put back on site (Game.restartShell()): its buildings, the work in them and what they still owe, onto its stack. |

### ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that (lines 4335-4529)

| line | len | member | says |
|---:|---:|---|---|
| 4358 | 3 | `private boolean handOn(String key)` | Whether the player's hand times this site: a city site while an order is set, or a rushed one. |
| 4368 | 9 | `public double siteMonths(String key, double siteOutput)` | Months a site on site would take at today's queue: its points over its weight's share of everything on site, by the rule held steady - or as the player's hand times it (handWait()). |
| 4385 | 7 | `public double cityOrderWait(BuildingsTemplate t, int quantity, double siteOutput)` | Months n buildings of t would take if the city ordered them now: with no order set and no rush on t's site, waitFor() exactly - the quote's wait since 0.7.17; otherwise as the hand times it, the order joining t's site... |
| 4399 | 7 | `public double restartWait(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months a shell of n buildings owing `owedPoints` would take if it were restarted now (0.7.22), beside whatever of it is on site already: by the rule as waitFor() times an order, or as the hand times it, as cityOrderWa... |
| 4407 | 4 | `private boolean isOnSite(BuildingsTemplate t)` |  |
| 4418 | 11 | `private double[] joined(BuildingsTemplate t, int n, double owed)` | What t's site would be with n more of it owing `owed` more points on it - {its crew's weight, its points owed} - when the player's hand times it: a city site (or none, which the city's order would make one) while an o... |
| 4431 | 1 | `public double pointsOwedOnSite()` | Points owed on every site, demolitions included: everything owed. |
| 4440 | 8 | `public double waitForDemolition(int n, double points, double siteOutput)` | Months a demolition of `points` over n buildings would take if ordered now, at today's queue (0.7.22): its own crew (demolitionWeight()) by the rule held steady - and with the city's order set, behind everything the c... |
| 4455 | 19 | `public double siteMonthsWith(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months n buildings of t owing `owedPoints` would take if put on site now - a shell restarted (0.7.22): beside whatever of it is on site already, as waitFor() times an order. |
| 4484 | 45 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput)` | THE HAND'S ARITHMETIC: every site on site, its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, and added at the bottom of the city's order when it is a new site of the city's - then th... |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 4530-5047)

| line | len | member | says |
|---:|---:|---|---|
| 4545 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` |  |
| 4550 | 4 | `public static long postsOffered(long posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 4556 | 5 | `public double offeredShareOf(String sector)` | A sector's share of its posts on offer: 1 without the hook. |
| 4563 | 6 | `public long[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - its buildings' posts at its share. |
| 4585 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 4591 | 5 | `public double takeRevenueDue()` |  |
| 4600 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 4607 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 4612 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 4618 | 7 | `public void displayAllBuildings()` |  |
| 4628 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 4646 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 4654 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 4663 | 13 | `public long[] getTotalJobs()` |  |
| 4683 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 4699 | 14 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 4729 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 4746 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 4771 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 4791 | 7 | `public long getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 4800 | 9 | `public long getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 4810 | 10 | `public int getUnderConstruction()` |  |
| 4820 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 4824 | 5 | `public long getTotalJobs(JobType type)` |  |
| 4831 | 6 | `public long getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 4850 | 13 | `public long getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 4865 | 5 | `public long getPostsWithheld()` | getPostsWithheld() over every job type. |
| 4886 | 13 | `public long getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 4907 | 21 | `public long[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 4930 | 13 | `public long getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 4944 | 5 | `public long getTotalHouseCapacity()` |  |
| 4957 | 3 | `public long getTotalStoreCoverage()` | Commercial Methods |
| 4962 | 3 | `public long getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 5010 | 7 | `public long getTotalConstructionCapacity()` |  |
| 5024 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 5040 | 3 | `public long getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 5044 | 3 | `public long getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 5048-5464)

| line | len | member | says |
|---:|---:|---|---|
| 5058 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 5069 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 5080 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 5089 | 9 | `public long[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier. |
| 5100 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 5111 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 5128 | 9 | `public double getBuildingMaterialsBySector(String sector)` | The construction materials in a sector's buildings, finished and on site, in units (0.7.75, the sector statements' R6): what getBuildingsValueBySector() prices at today's materials, so a move in that price moves the s... |
| 5139 | 9 | `public long getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 5150 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 5157 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 5174 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 5188 | 13 | `public long getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 5202 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 5228 | 9 | `public long getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 5245 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 5270 | 14 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 5302 | 25 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 5340 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 5376 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 5398 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included. |
| 5410 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 5422 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 5433 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 5452 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 5465-5770)

| line | len | member | says |
|---:|---:|---|---|
| 5480 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 5492 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 5503 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 5516 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 5527 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 5541 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 5557 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 5564 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 5588 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 5600 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 5612 | 10 | `public long[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 5623 | 16 | `public long[] getJobArrayPerCategory(BuildingType category)` |  |
| 5643 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 5647 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 5652 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 5662 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 5671 | 3 | `public int getTemplateCount()` |  |
| 5675 | 9 | `public double[] getConstructionProgress()` |  |
| 5694 | 27 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 5737 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 5757 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 5761 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 5771-6089)

| line | len | member | says |
|---:|---:|---|---|
| 5795 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 5803 | 10 | `public double[] getConstructionProgressById()` |  |
| 5814 | 10 | `public int[] getUnderConstructionById()` |  |
| 5826 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 5838 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 5844 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 5850 | 7 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 5859 | 8 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 5869 | 17 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 5888 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 5907 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 5924 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 5936 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 5948 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 5961 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 5975 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 5985 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 5995 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 5999 | 3 | `public void clearStacks()` |  |
| 6028 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 6039 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 6045 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 6057 | 4 | `static { ... }` |  |
| 6062 | 7 | `public void resetBuildingManager()` |  |
| 6072 | 8 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 6083 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit - since 0.7.42 the unit over the expected price level, every month (Game.restrikeMoneyConstants()). |


# BuildingManager.java - 8,018 lines · 215 methods · 6 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (506), [BuildingsTemplate](BuildingsTemplate.md) (352), [Good](Good.md) (216), [BuildingType](BuildingType.md) (165), [BuildingsStacks](BuildingsStacks.md) (113), [ConstructionControl](ConstructionControl.md) (68), [CareType](CareType.md) (24), [EducationType](EducationType.md) (18), [Resource](Resource.md) (16), [RefineryFlow](RefineryFlow.md) (14), [SafetyType](SafetyType.md) (12), [Deposit](Deposit.md) (11), [Ports](Ports.md) (10), [BuildingInstance](BuildingInstance.md) (4), [Automotive](Automotive.md) (4), [Manufacturing](Manufacturing.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (105):** [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [Ports](Ports.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningView](ZoningView.md)

## Sections

| line | section |
|---:|---|
| 73 | THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) |
| 251 | · WATER DRAW, per building, in units of 10,000 gallons/month. |
| 443 | · HEALTHCARE |
| 483 | · · childcare, resized (0.7.71, batch N2) |
| 845 | · WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH |
| 969 | · A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of |
| 1004 | · THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10). |
| 1096 | · · WIND |
| 1223 | · · STEEL |
| 1287 | · · INFRASTRUCTURE |
| 1424 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1516 | · · RAIL |
| 1627 | · · AUTOMOTIVE |
| 1679 | · THE LUXURY SHOPS (2026-09-17) |
| 1740 | · THE KITCHENS (2026-09-18) |
| 1932 | · · MINING |
| 2016 | · · FUEL (0.7.62) |
| 2072 | · · THE REFINERY'S UNITS (0.7.80, batch O4) |
| 2334 | · · THE FILLING STATION (0.7.83) |
| 2363 | · · THE TANK FARM AND THE STRATEGIC RESERVE (0.7.85) |
| 2410 | · · THE PORTS (0.7.86) |
| 2492 | · · THE OIL AT SEA (0.7.91) |
| 2549 | · · THE INTEGRATED ROUTE (0.7.123, batch I4) |
| 2718 | · · IRON MINING BY TYPE AND SIZE (0.7.124, batch I5) |
| 2806 | · · COAL MINING (0.7.126, batch I6) |
| 2920 | · · QUARRYING AND CEMENT & CONCRETE (0.7.131, batch I7) |
| 3075 | · · COPPER MINING AND COPPER REFINING (0.7.133, batch I8) |
| 3268 | · · FORESTRY (0.7.138, batch I9) |
| 3354 | · · WOOD PRODUCTS (0.7.140, batch I10) |
| 3512 | · · HAULAGE (FR2, 2026-10-18) |
| 3572 | · · THE RAIL DEPOT (FR7, 2026-10-18) |
| 3611 | · · EDUCATION |
| 3824 | · · SAFETY |
| 3935 | · THE BANK |
| 3964 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 4025 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 4111 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 4143 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 4304 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 4343 | · · And the same trade at scale. Two and a half times the shop's output |
| 4372 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 4473 | · · And the same thing at three times the size on two and a half times as |
| 4492 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 4528 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 4565 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 4828 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 5270 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 5523 | · DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129, batch T1-B) |
| 5660 | · THE BUILDERS' OWN DEPOTS FIRST (0.7.130, batch T1-C) |
| 5956 | · ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that |
| 6297 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 6848 | BY SECTOR (2026-09-11, the sector template) |
| 7351 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 7700 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 4923 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 5568 | `BuildingManager.DEATH_CARE_FIRST` | `"Death care first while the dead wait"` | The page's words for the rule, which Plan.line() opens with: what it does and when. |
| 5749 | `BuildingManager.BUILDERS_FIRST` | `"The builders' own depots first"` | The page's words for the rule, which Plan.line() opens with when a depot is on site. |
| 6797 | `BuildingManager.BASE_MATERIALS` | `36` | The public works yard: it produces this many units of materials a month on its own. |
| 7984 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private List<BuildingsTemplate> templates` |  |
| 22 | `private List<BuildingsStacks> stacks` |  |
| 23 | `private List<BuildingInstance> instances` |  |
| 24 | `private JobType[] jobTypes` |  |
| 37 | `private long foundingEndowments` | How many founding endowments the city holds (FW9, 2026-10-25): the hundred household places, the doctor, the nursery, the almshouse, the churchyard and the constabulary a city is founded with (getTotalHouseCapacity(),... |
| 38 | `private int constructionMaterials` |  |
| 71 | `private double materialsCost` |  |
| 134 | `private BuildersWages buildersWages` |  |
| 196 | `private double expectedLevel` | The expected price level the templates' cash was last struck at (Expectations.getStruckLevel(), the level the last month ended on); 1.0 until Game re-strikes. |
| 5250 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 5259 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 5266 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 5290 | `private final ConstructionControl control` | The player's hand on the queue: the order, the rushes, the shells, the demolitions and the buy-outs. |
| 5295 | `private ConstructionControl.Events controlEvents` | What this month's advance left for Game to settle: the overtime worked, the orders stopped, the demolitions done. |
| 5317 | `public final java.util.List<String> keys` |  |
| 5319 | `public final double output` | The site output it was struck on. |
| 5321 | `public final double[] owed, rule, share, work` | Points owed; the rule's share (siteShares()); the share after the first group and the city's order; the work, overtime in. |
| 5322 | `public final java.util.List<ConstructionControl.Overtime> overtime` |  |
| 5323 | `public final double overtimePoints` |  |
| 5325 | `public final java.util.List<String> deathCareFirst` | The city's death-care sites served first because the dead wait, in the order served (0.7.129); empty when the rule is off. |
| 5327 | `public final double unburied` | ...and the dead lying unburied that put them there, as Healthcare counted them last month; 0 when the rule is off. |
| 5329 | `public final java.util.List<String> buildersFirst` | The builders' own sites served off the top of the month (0.7.130), in the stacks' order; empty when none is on site. |
| 5331 | `public final int buildersOnSite` | ...and the buildings on them: the depots the line counts. |
| 5467 | `private Plan lastPlan` | The plan the last month's advance applied, or null when the player's hand was not on the queue and nothing was served first - no death care (0.7.129), no depot of the builders' (0.7.130): a month's reading, not saved. |
| 5571 | `private java.util.function.DoubleSupplier unburied` | The dead lying unburied, as Game hands them (Healthcare.getUnburied()); null - a harness's bare manager - reads none. |
| 5743 | `private boolean buildersFirstOn` | Whether the rule is on: always, in the game; a harness's twin turns it off to measure the city without it. |
| 5763 | `private java.util.function.BiPredicate<BuildingsTemplate, Integer> buildersCouldStaff` | Whether the city could staff n of the builders' depots, by their own plan's test (Sector.staffing(), MIN_STAFFABLE_TO_ORDER), as Game hands it; null - a bare manager - says yes. |
| 5771 | `private java.util.function.DoubleSupplier sitesAtEveryPost` | The builders' month for the sites with every post offered, as Game hands it (Game.getBuildingOutputAtEveryPost(), their planner's figure); null - a bare manager - reads none, and every depot is starved. |
| 6316 | `private java.util.function.ToDoubleBiFunction<String, JobType> offeredShare` | The share of a sector's posts of a job type it offers this month; null offers every post. |
| 6385 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 6396 | `public final String building` |  |
| 6397 | `public final int quantity` |  |
| 7789 | `public int templateId` |  |
| 7790 | `public String payer` |  |
| 7791 | `public boolean creditable` |  |
| 7793 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 7794 | `public double value, units, allowance` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 8000 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

### THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) (lines 73-250)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 6 | **type** `public interface BuildersWages` | What the builders' crews cost a point of output, today and at the founding ladder, both in today's money. |
| 129 | 1 | `double perPointToday()` _(in BuildingManager.BuildersWages)_ | A depot's posts at today's wages, over its points. |
| 131 | 1 | `double perPointAtFounding()` _(in BuildingManager.BuildersWages)_ | ...and at the founding ladder, PayTier, in today's unit. |
| 136 | 1 | `public void setBuildersWages(BuildersWages wages)` |  |
| 139 | 3 | `public double buildersWagePerPoint()` | What the builders' crews cost a point today (a depot's posts at today's wages, over its points): the bill a rushed site's overtime premium is struck on (0.7.22). |
| 144 | 8 | `public double buildersWageIndex()` | The builders' labour index: their wage bill a point today over at founding. |
| 160 | 12 | `public double nonMaterialCost(BuildingsTemplate t)` | The non-material part of what one of these costs to build today (0.7.19): its cash cost, with its labour - its points at the builders' founding wage bill a point, never more than the whole cash cost - at today's build... |
| 174 | 7 | `public double labourCost(BuildingsTemplate t)` | ...its labour part alone, at today's wages. |
| 199 | 3 | `public void setExpectedLevel(double level)` | Told by Game with every re-strike of the templates; anything not positive is ignored. |
| 204 | 1 | `public double getExpectedLevel()` | The level the cash costs are struck at. |
| 207 | 3 | `public double structureCost(BuildingsTemplate t)` | ...and with its materials at the world's price today: what it would cost to put up now, before the sales tax. |
| 220 | 20 | `public double nonMaterialPricePerPoint()` | What the builders are paid for a point of work, beyond its material and its sales tax (0.7.19): the non-material price of the work on site, averaged over the points still owed; with nothing on site, of the repairs - t... |
| 241 | 3 | `public void setExchangeRate(double rate)` |  |
| 245 | 5 | `public BuildingManager()` |  |

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 251-4827)

| line | len | member | says |
|---:|---:|---|---|
| 277 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 294 | 4430 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 4725 | 6 | `public void finalUpdateBuildings()` |  |
| 4733 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 4739 | 5 | `public int[] stackOrder()` | The template ids of the stacks, in the order they stand: the order each was first bought (0.7.43; DataSave.getStackOrder()). |
| 4751 | 10 | `public void orderStacks(int[] ids)` | Stands the stacks in a saved order (0.7.43): each in the place its id holds in `ids`, any the order does not name after them in the order they stand. |
| 4762 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 4801 | 5 | `public void addPreparation(BuildingsTemplate template, double points)` | Puts a foothill site's preparation on the template's stack as owed work (ET2; SitePreparation): called by Game once the order's stack exists (addStack()), with the points its quote carried. |
| 4808 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 4812 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 4824 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 4828-5269)

| line | len | member | says |
|---:|---:|---|---|
| 4939 | 35 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 4980 | 4 | `static double demolitionWeight(ConstructionControl.Demolition site)` | A demolition's crew by the rule above (0.7.22): its buildings times the crew one of them can use, by the points its own demolition is - the whole of them, as a building's crew is struck on the whole building. |
| 4986 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 4997 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 5024 | 3 | `static double weightOf(BuildingsTemplate t, long buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 5040 | 3 | `public double waitFor(BuildingsTemplate t, int quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 5045 | 4 | `public double waitFor(BuildingsTemplate t, int quantity, double extraPoints, double siteOutput)` | ...with `extraPoints` more owed on the order's site than its buildings' own (ET2: a foothill pad's preparation, SitePreparation); waitFor() exactly for none. |
| 5058 | 4 | `public double waitOnSite(BuildingsTemplate t, double siteOutput)` | Months what is already on site of one building would wait at this month's shares (0.7.20): waitFor()'s rule with no order added - the stack's own points still owed over its weight's share of everything on site. |
| 5064 | 12 | `private double waitWith(BuildingsTemplate t, int quantity, double extraPoints, double siteOutput)` | waitFor()'s arithmetic, for an order of `quantity` - none, for what is on site alone - and `extraPoints` more owed on its site (ET2). |
| 5078 | 4 | `private static double orderPoints(BuildingsTemplate t, int quantity, double extraPoints)` | The points an order of `quantity` owes: its buildings' own, and `extraPoints` more on its site (ET2) - the buildings' alone, to the bit, for none. |
| 5084 | 27 | `private double ruleWait(BuildingsTemplate t, int quantity, double extraPoints, double siteOutput)` | The crews' rule's own wait, held steady, for an order of `quantity` - none, for what is on site alone: waitWith() before 0.7.130, and still, with no depot served first - and `extraPoints` more owed on its site (ET2). |
| 5113 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 5128 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 5139 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 5156 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 5176 | 67 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 5252 | 5 | `public double takeMaterialsDue()` |  |
| 5268 | 1 | `public double getPointsBuilt()` |  |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 5270-5522)

| line | len | member | says |
|---:|---:|---|---|
| 5292 | 1 | `public ConstructionControl getControl()` |  |
| 5297 | 5 | `public ConstructionControl.Events takeControlEvents()` |  |
| 5304 | 1 | `public double getOvertimePoints()` | The overtime's points this month past the sites' shares - negative from a rush's third month on. |
| 5316 | 64 | **type** `public static final class Plan` | One month's crews, site by site, as the advance applies them: the rule's share, the city's in the city's order, and a rushed site's month on overtime. |
| 5332 | 11 | `Plan(java.util.List<String> keys, double output, double[] owed, double[] rule, double[] share, double[] work, java.util.List<Co...` _(in BuildingManager.Plan)_ |  |
| 5349 | 17 | `public String line()` _(in BuildingManager.Plan)_ | The construction page's line where the order shows: DEATH_CARE_FIRST and how many lie unburied (0.7.129); BUILDERS_FIRST and their depots on site, with the death care beside them while the dead wait (0.7.130); null wh... |
| 5367 | 1 | `public double shareOf(String key)` _(in BuildingManager.Plan)_ | This month's share of the crews, before any overtime. |
| 5369 | 1 | `public double workOf(String key)` _(in BuildingManager.Plan)_ | ...and the work the site does on it, overtime included. |
| 5371 | 1 | `public double owedOf(String key)` _(in BuildingManager.Plan)_ | Points still owed. |
| 5373 | 1 | `public double ruleOf(String key)` _(in BuildingManager.Plan)_ | The crews' rule's share, before the city's order. |
| 5375 | 4 | `public ConstructionControl.Overtime overtimeOf(String key)` _(in BuildingManager.Plan)_ | The month's overtime on one site, or null. |
| 5388 | 77 | `public Plan plan(double siteOutput)` | The month's crews at this site output, as the advance would apply them: siteShares(), then the city's share in the city's order (applyPriority()), then each rushed site's month on overtime. |
| 5469 | 1 | `public Plan getLastPlan()` |  |
| 5477 | 14 | `static void applyPriority(double[] share, double[] owed, java.util.List<String> keys, java.util.List<String> order)` | A. |
| 5493 | 6 | `public java.util.List<String> citySiteKeys()` | The city's own sites on site, in the stacks' order and then the demolitions': the ones its order is set over. |
| 5507 | 5 | `public java.util.List<String> cityOrder()` | ...in the order the city's crews serve them: the player's, or the stacks' order with none set - and while the dead wait, the city's death care at its top (0.7.129, DEATH CARE FIRST WHILE THE DEAD WAIT); and a depot of... |
| 5514 | 8 | `public int cityOrderFirst()` | How many of the city's order head it because they are served first (0.7.130): its death care while the dead wait, and its depots - what the page's arrows keep within. |

### DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129, batch T1-B) (lines 5523-5659)

| line | len | member | says |
|---:|---:|---|---|
| 5574 | 1 | `public void setUnburied(java.util.function.DoubleSupplier unburied)` | Told by Game: where the dead lying unburied are read from (0.7.129). |
| 5577 | 5 | `public double unburiedNow()` | The dead lying unburied, as last month settled them; 0 without the hook. |
| 5584 | 3 | `public static boolean servesTheDead(BuildingsTemplate t)` | Whether a template serves the dead: its care type is burial or cremation. |
| 5589 | 4 | `public boolean servesTheDead(String key)` | ...and a site's key: a stack of such a template. |
| 5598 | 6 | `java.util.List<String> deathCareAmong(java.util.List<String> sites)` | Of these sites, the ones served first while the dead wait - those that serve the dead, in the order given - or none when nobody lies unburied. |
| 5610 | 7 | `public java.util.List<String> deathCareFirst()` | The city's death-care sites served first this month, in the order served: empty when nobody lies unburied or none is on site - the rule is then off, and nothing it touches moves. |
| 5619 | 7 | `static java.util.List<String> firstTheDead(java.util.List<String> order, java.util.List<String> dead)` | An order with `dead` taken to its top, each group in the order it had; the order itself with none. |
| 5635 | 24 | `static void applyDeathCareFirst(double[] share, double[] owed, double[] weight, java.util.List<String> keys, java.util.List<Str...` | The city's share with no order of the player's, while the dead wait: what the rule gave the city's sites, together, handed first to the death care top down - each what it still owes, at most, of what is left - and wha... |

### THE BUILDERS' OWN DEPOTS FIRST (0.7.130, batch T1-C) (lines 5660-5955)

| line | len | member | says |
|---:|---:|---|---|
| 5746 | 1 | `void setBuildersFirstForTest(boolean on)` | Harnesses only (BuildersFirstCheck): the twin without the rule, its depots crewed by the crews' rule alone - the counterfactual. |
| 5752 | 3 | `public static boolean addsToTheBuilders(BuildingsTemplate t)` | Whether a template adds to the builders' capacity: the Construction sector's, and it makes building work - a depot. |
| 5757 | 4 | `public boolean addsToTheBuilders(String key)` | ...and a site's key: a stack of such a template. |
| 5766 | 3 | `public void setBuildersCouldStaff(java.util.function.BiPredicate<BuildingsTemplate, Integer> couldStaff)` | Told by Game: how the builders' planner tests a depot's posts against the city's workers (0.7.130). |
| 5774 | 1 | `public void setSitesAtEveryPost(java.util.function.DoubleSupplier sites)` | Told by Game: where the builders' month for the sites, every post offered, is read from (0.7.130). |
| 5784 | 10 | `public boolean goesFirst(BuildingsTemplate t, int extra)` | Whether t's site, with `extra` more of it ordered, goes first: the rule on, t a template that adds to the builders, the city able to staff what would be on site (buildersCouldStaff()), and that site starved - the crew... |
| 5796 | 10 | `public java.util.List<String> buildersOwnOnSite()` | The builders' own sites served first, in the stacks' order: every stack that adds to their capacity with buildings on site, whoever paid for them, while the city could staff them (goesFirst()). |
| 5813 | 8 | `public java.util.List<String> firstGroup()` | The first group this month, in the order the sites stand: the builders' own sites on site, and while the dead wait the city's death care beside them - or none, when no depot is on site (death care first is then the ci... |
| 5829 | 14 | `static double[] firstOffTheTop(double output, double[] weight, double[] owed, java.util.List<String> keys, java.util.List<Strin...` | The month with the first group served off its top: of the whole output, the group takes what it owes, or all of it if less, shared among its sites by the crews' rule (waterFill(), each by its crew's weight and no more... |
| 5845 | 6 | `public java.util.List<String> siteKeysOnSite()` | Every site with work on it: the stacks with buildings on site, and the demolitions. |
| 5853 | 5 | `public boolean isCitySite(String key)` | Whether a site is the city's own: a stack whose every order on site is the city's, or a demolition. |
| 5860 | 5 | `public BuildingsStacks stackOfKey(String key)` | The stack a site's key names, or null. |
| 5867 | 6 | `public String nameOfSite(String key)` | A site's name: its building's, "Demolishing" before a demolition's. |
| 5875 | 20 | `private void advanceDemolitions(Plan plan)` | D. |
| 5903 | 17 | `private void stopCancelled()` | C. |
| 5931 | 11 | `private void retirePaved(BuildingsStacks stack)` | F. |
| 5944 | 11 | `public void resumeShell(ConstructionControl.Shell shell)` | A shell put back on site (Game.restartShell()): its buildings, the work in them and what they still owe, onto its stack. |

### ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that (lines 5956-6296)

| line | len | member | says |
|---:|---:|---|---|
| 5989 | 4 | `private boolean handOn(String key)` | Whether the player's hand times this site: a city site while an order is set, or a rushed one - or, since 0.7.129, any city site while the city's death care is served first (DEATH CARE FIRST WHILE THE DEAD WAIT). |
| 6000 | 11 | `public double siteMonths(String key, double siteOutput)` | Months a site on site would take at today's queue: its points over its weight's share of everything on site, by the rule held steady - or as the player's hand times it (handWait()). |
| 6019 | 3 | `public double cityOrderWait(BuildingsTemplate t, int quantity, double siteOutput)` | Months n buildings of t would take if the city ordered them now: with no order set and no rush on t's site, waitFor() exactly - the quote's wait since 0.7.17; otherwise as the hand times it, the order joining t's site... |
| 6024 | 14 | `public double cityOrderWait(BuildingsTemplate t, int quantity, double extraPoints, double siteOutput)` | ...with `extraPoints` more owed on its site than its buildings' own (ET2: a foothill pad's preparation, SitePreparation); cityOrderWait() exactly for none. |
| 6045 | 8 | `public double restartWait(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months a shell of n buildings owing `owedPoints` would take if it were restarted now (0.7.22), beside whatever of it is on site already: by the rule as waitFor() times an order, or as the hand times it, as cityOrderWa... |
| 6054 | 4 | `private boolean isOnSite(BuildingsTemplate t)` |  |
| 6067 | 13 | `private double[] joined(BuildingsTemplate t, int n, double owed)` | What t's site would be with n more of it owing `owed` more points on it - {its crew's weight, its points owed} - when the player's hand times it: a city site (or none, which the city's order would make one) while an o... |
| 6082 | 1 | `public double pointsOwedOnSite()` | Points owed on every site, demolitions included: everything owed. |
| 6091 | 10 | `public double waitForDemolition(int n, double points, double siteOutput)` | Months a demolition of `points` over n buildings would take if ordered now, at today's queue (0.7.22): its own crew (demolitionWeight()) by the rule held steady - and with the city's order set, behind everything the c... |
| 6108 | 20 | `public double siteMonthsWith(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months n buildings of t owing `owedPoints` would take if put on site now - a shell restarted (0.7.22): beside whatever of it is on site already, as waitFor() times an order. |
| 6142 | 48 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput)` | THE HAND'S ARITHMETIC: every site on site, its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, and added at the bottom of the city's order when it is a new site of the city's - then th... |
| 6192 | 11 | `private double onOvertime(String key, double owed, double months, boolean isNew, java.util.List<String> city)` | A wait worked on overtime when the site is a rushed city site on site: the same pace, at each coming month's factor; the wait itself otherwise. |
| 6205 | 5 | `private boolean firstOn(BuildingsTemplate t, int quantity)` | Whether a wait is the first group's (0.7.130): a depot of the builders' on site served first, or `quantity` more of `t` that would be. |
| 6212 | 6 | `private double[] withMore(BuildingsTemplate t, int n, double owed)` | t's site with n more of it owing `owed` more points on it: {its crew's weight, its points owed}. |
| 6233 | 63 | `private double firstWait(String key, double[] mine, boolean isNew, boolean cityOrder, double siteOutput)` | THE FIRST GROUP'S ARITHMETIC (0.7.130): every site on site with its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, a new one added - and the first group: the builders' own sites, and ... |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 6297-6847)

| line | len | member | says |
|---:|---:|---|---|
| 6319 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` | A share for every job type of a sector alike (the builders'). |
| 6324 | 3 | `public void setOfferedShareByType(java.util.function.ToDoubleBiFunction<String, JobType> offeredShare)` | ...and one a job type at a time (FR2: Freight's drivers). |
| 6329 | 4 | `public static long postsOffered(long posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 6335 | 5 | `public double offeredShareOf(String sector, JobType type)` | A sector's share of its posts of a job type on offer: 1 without the hook. |
| 6347 | 9 | `public long[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - the posts of the buildings it owns at its share of each, and the crews it works for the city (the ports', FR3) whole: a lay-off is struck on a sector's own building... |
| 6372 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 6378 | 5 | `public double takeRevenueDue()` |  |
| 6387 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 6394 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 6399 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 6405 | 7 | `public void displayAllBuildings()` |  |
| 6415 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 6433 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 6441 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 6450 | 13 | `public long[] getTotalJobs()` |  |
| 6470 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 6486 | 14 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 6516 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 6533 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 6558 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 6578 | 7 | `public long getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 6587 | 9 | `public long getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 6597 | 10 | `public int getUnderConstruction()` |  |
| 6607 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 6611 | 5 | `public long getTotalJobs(JobType type)` |  |
| 6618 | 6 | `public long getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 6644 | 13 | `public long getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 6659 | 5 | `public long getPostsWithheld()` | getPostsWithheld() over every job type. |
| 6680 | 13 | `public long getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 6701 | 21 | `public long[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 6724 | 13 | `public long getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 6739 | 1 | `public long getFoundingEndowments()` | How many founding endowments the city holds: 1 for a founded city, K for a harness's copy K times over (FW9; see the field). |
| 6742 | 1 | `public void setFoundingEndowments(long endowments)` | The load's (and ScaleCheck's) way to hold K endowments: the city's hundred places and its founding care and police, K times over. |
| 6745 | 5 | `public long getTotalHouseCapacity()` | The household places: the founding hundred (per endowment, FW9) and every residential building's. |
| 6758 | 3 | `public long getTotalStoreCoverage()` | Commercial Methods |
| 6763 | 3 | `public long getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 6811 | 6 | `public long getTotalConstructionCapacity()` | The nameplate of the builders' yards and depots, and nothing else since PT1-M4 (2026-10-23): the municipal works department's free 400 points are retired, and the city founds with a Builders' Yard instead (Game.foundi... |
| 6824 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 6840 | 3 | `public long getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 6844 | 3 | `public long getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 6848-7350)

| line | len | member | says |
|---:|---:|---|---|
| 6858 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 6872 | 7 | `public int standingWhere(java.util.function.Predicate<BuildingsTemplate> which)` | How many finished buildings, in every sector, have a template this accepts (FR7): Freight counts the bulk works a spur could be laid into. |
| 6881 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 6892 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 6907 | 9 | `public long[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier: the buildings it OPERATES (BuildingsTemplate.getOperator()), which are its own and, for Freight, the city's sea terminals (FR3, the landlord port, docs/queue /F... |
| 6918 | 9 | `public long[] getOwnedJobArrayBySector(String sector)` | ...the posts of the buildings the sector OWNS (the old reading, before the ports' crews moved): its depots' and its track's, not the berths' crews it works for the city (FR3). |
| 6929 | 9 | `public long[] getCrewArrayBySector(String sector)` | ...and the posts it works but does not own, per tier: the crews of the city's berths for Freight (FR3), none for any other sector. |
| 6940 | 9 | `public long[] getCrewArrayByCargo(Ports.Cargo kind)` | The posts of the city's sea terminals that handle one kind of cargo, per tier (FR3): the crews of that kind's berths, for the Ports division's per-kind figures. |
| 6951 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 6965 | 9 | `public double getWorksLandSqFtBySector(String sector, java.util.function.Predicate<BuildingsTemplate> works)` | ...the part of it that works stand on (ET3: `works` is Game.standsAnywhere()), which the books attribute to the steep ground first (EconomyManager.landByReliefOf()); getLandSqFtBySector() less this is its buildings'. |
| 6976 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 6993 | 9 | `public double getBuildingMaterialsBySector(String sector)` | The construction materials in a sector's buildings, finished and on site, in units (0.7.75, the sector statements' R6): what getBuildingsValueBySector() prices at today's materials, so a move in that price moves the s... |
| 7004 | 9 | `public long getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 7015 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 7022 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 7039 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 7053 | 13 | `public long getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 7067 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 7093 | 9 | `public long getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 7110 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 7122 | 9 | `public double getWorksLandSqFtByCategory(BuildingType category, java.util.function.Predicate<BuildingsTemplate> works)` | ...the part of a category's land that works stand on (ET3: `works` is Game.standsAnywhere()); getLandSqFtByCategory() less this is its buildings'. |
| 7149 | 16 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 7186 | 26 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 7225 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 7262 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 7284 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included (one for each founding endowment, FW9). |
| 7296 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 7308 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 7319 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 7338 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 7351-7699)

| line | len | member | says |
|---:|---:|---|---|
| 7366 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 7378 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 7389 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 7402 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 7413 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 7427 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 7443 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 7450 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 7462 | 9 | `public double getPortBookValueByCargo(Ports.Cargo kind)` | What the terminals of one kind of cargo standing are worth on the books (FR3, docs/queue/FR3.md 2, port dues): each PORTS building with a berth of this kind at its cash cost plus its materials at market, as getBookVal... |
| 7491 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 7503 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 7515 | 10 | `public long[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 7526 | 16 | `public long[] getJobArrayPerCategory(BuildingType category)` |  |
| 7546 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 7550 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 7555 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 7565 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 7574 | 3 | `public int getTemplateCount()` |  |
| 7578 | 9 | `public double[] getConstructionProgress()` |  |
| 7597 | 27 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 7631 | 16 | `public double getWorksLandFootprint(java.util.function.Predicate<BuildingsTemplate> works)` | The part of getTotalLandFootprint() that the buildings `works` picks stand on (ET2): the same sum, over the standing and on site, the stopped shells and the demolitions on site, of those templates alone - the ground L... |
| 7666 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 7686 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 7690 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 7700-8018)

| line | len | member | says |
|---:|---:|---|---|
| 7724 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 7732 | 10 | `public double[] getConstructionProgressById()` |  |
| 7743 | 10 | `public int[] getUnderConstructionById()` |  |
| 7755 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 7767 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 7773 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 7779 | 7 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 7788 | 8 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 7798 | 17 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 7817 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 7836 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 7853 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 7865 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 7877 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 7890 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 7904 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 7914 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 7924 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 7928 | 3 | `public void clearStacks()` |  |
| 7957 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 7968 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 7974 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 7986 | 4 | `static { ... }` |  |
| 7991 | 7 | `public void resetBuildingManager()` |  |
| 8001 | 8 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 8012 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit - since 0.7.42 the unit over the expected price level, every month (Game.restrikeMoneyConstants()). |


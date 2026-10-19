# BuildingManager.java - 7,805 lines · 202 methods · 7 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (492), [BuildingsTemplate](BuildingsTemplate.md) (335), [Good](Good.md) (215), [BuildingType](BuildingType.md) (162), [BuildingsStacks](BuildingsStacks.md) (105), [ConstructionControl](ConstructionControl.md) (66), [CareType](CareType.md) (24), [EducationType](EducationType.md) (18), [Resource](Resource.md) (16), [RefineryFlow](RefineryFlow.md) (14), [SafetyType](SafetyType.md) (12), [Deposit](Deposit.md) (11), [BuildingInstance](BuildingInstance.md) (4), [Automotive](Automotive.md) (4), [Ports](Ports.md) (4), [Manufacturing](Manufacturing.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1)

**Used by (95):** [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [ExpectationsCheck](ExpectationsCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [Freight](Freight.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningView](ZoningView.md)

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
| 1257 | · · INFRASTRUCTURE |
| 1394 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1486 | · · RAIL |
| 1597 | · · AUTOMOTIVE |
| 1649 | · THE LUXURY SHOPS (2026-09-17) |
| 1710 | · THE KITCHENS (2026-09-18) |
| 1902 | · · MINING |
| 1986 | · · FUEL (0.7.62) |
| 2042 | · · THE REFINERY'S UNITS (0.7.80, batch O4) |
| 2304 | · · THE FILLING STATION (0.7.83) |
| 2333 | · · THE TANK FARM AND THE STRATEGIC RESERVE (0.7.85) |
| 2380 | · · THE PORTS (0.7.86) |
| 2442 | · · THE OIL AT SEA (0.7.91) |
| 2499 | · · THE INTEGRATED ROUTE (0.7.123, batch I4) |
| 2668 | · · IRON MINING BY TYPE AND SIZE (0.7.124, batch I5) |
| 2756 | · · COAL MINING (0.7.126, batch I6) |
| 2870 | · · QUARRYING AND CEMENT & CONCRETE (0.7.131, batch I7) |
| 3025 | · · COPPER MINING AND COPPER REFINING (0.7.133, batch I8) |
| 3218 | · · FORESTRY (0.7.138, batch I9) |
| 3304 | · · WOOD PRODUCTS (0.7.140, batch I10) |
| 3462 | · · HAULAGE (FR2, 2026-10-18) |
| 3522 | · · THE RAIL DEPOT (FR7, 2026-10-18) |
| 3561 | · · EDUCATION |
| 3774 | · · SAFETY |
| 3885 | · THE BANK |
| 3914 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 3975 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 4061 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 4093 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 4254 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 4293 | · · And the same trade at scale. Two and a half times the shop's output |
| 4322 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 4423 | · · And the same thing at three times the size on two and a half times as |
| 4442 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 4478 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 4515 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 4767 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 5198 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 5451 | · DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129, batch T1-B) |
| 5588 | · THE BUILDERS' OWN DEPOTS FIRST (0.7.130, batch T1-C) |
| 5884 | · ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that |
| 6219 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 6749 | BY SECTOR (2026-09-11, the sector template) |
| 7178 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 7487 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BuildingManager.MATERIALS_WORLD_PRICE` | `18` | What a unit of construction material costs, in the city's money. |
| 4862 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 5496 | `BuildingManager.DEATH_CARE_FIRST` | `"Death care first while the dead wait"` | The page's words for the rule, which Plan.line() opens with: what it does and when. |
| 5677 | `BuildingManager.BUILDERS_FIRST` | `"The builders' own depots first"` | The page's words for the rule, which Plan.line() opens with when a depot is on site. |
| 6686 | `BuildingManager.BASE_CONSTRUCTION` | `400` | The city's own crews, plus whatever the depots add. |
| 6709 | `BuildingManager.BASE_MATERIALS` | `36` | Same idea for materials: a yard that produces this many a month on its own. |
| 7771 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 5178 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 5187 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 5194 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 5218 | `private final ConstructionControl control` | The player's hand on the queue: the order, the rushes, the shells, the demolitions and the buy-outs. |
| 5223 | `private ConstructionControl.Events controlEvents` | What this month's advance left for Game to settle: the overtime worked, the orders stopped, the demolitions done. |
| 5245 | `public final java.util.List<String> keys` |  |
| 5247 | `public final double output` | The site output it was struck on. |
| 5249 | `public final double[] owed, rule, share, work` | Points owed; the rule's share (siteShares()); the share after the first group and the city's order; the work, overtime in. |
| 5250 | `public final java.util.List<ConstructionControl.Overtime> overtime` |  |
| 5251 | `public final double overtimePoints` |  |
| 5253 | `public final java.util.List<String> deathCareFirst` | The city's death-care sites served first because the dead wait, in the order served (0.7.129); empty when the rule is off. |
| 5255 | `public final double unburied` | ...and the dead lying unburied that put them there, as Healthcare counted them last month; 0 when the rule is off. |
| 5257 | `public final java.util.List<String> buildersFirst` | The builders' own sites served off the top of the month (0.7.130), in the stacks' order; empty when none is on site. |
| 5259 | `public final int buildersOnSite` | ...and the buildings on them: the depots the line counts. |
| 5395 | `private Plan lastPlan` | The plan the last month's advance applied, or null when the player's hand was not on the queue and nothing was served first - no death care (0.7.129), no depot of the builders' (0.7.130): a month's reading, not saved. |
| 5499 | `private java.util.function.DoubleSupplier unburied` | The dead lying unburied, as Game hands them (Healthcare.getUnburied()); null - a harness's bare manager - reads none. |
| 5671 | `private boolean buildersFirstOn` | Whether the rule is on: always, in the game; a harness's twin turns it off to measure the city without it. |
| 5691 | `private java.util.function.BiPredicate<BuildingsTemplate, Integer> buildersCouldStaff` | Whether the city could staff n of the builders' depots, by their own plan's test (Sector.staffing(), MIN_STAFFABLE_TO_ORDER), as Game hands it; null - a bare manager - says yes. |
| 5699 | `private java.util.function.DoubleSupplier sitesAtEveryPost` | The builders' month for the sites with every post offered, as Game hands it (Game.getBuildingOutputAtEveryPost(), their planner's figure); null - a bare manager - reads none, and every depot is starved. |
| 6238 | `private java.util.function.ToDoubleBiFunction<String, JobType> offeredShare` | The share of a sector's posts of a job type it offers this month; null offers every post. |
| 6299 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 6310 | `public final String building` |  |
| 6311 | `public final int quantity` |  |
| 7576 | `public int templateId` |  |
| 7577 | `public String payer` |  |
| 7578 | `public boolean creditable` |  |
| 7580 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 7581 | `public double value, units, allowance` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 7787 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

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

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 238-4766)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 281 | 4393 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 4675 | 6 | `public void finalUpdateBuildings()` |  |
| 4683 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 4689 | 5 | `public int[] stackOrder()` | The template ids of the stacks, in the order they stand: the order each was first bought (0.7.43; DataSave.getStackOrder()). |
| 4701 | 10 | `public void orderStacks(int[] ids)` | Stands the stacks in a saved order (0.7.43): each in the place its id holds in `ids`, any the order does not name after them in the order they stand. |
| 4712 | 33 | `public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 4747 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 4751 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 4763 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 4767-5197)

| line | len | member | says |
|---:|---:|---|---|
| 4878 | 35 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 4919 | 4 | `static double demolitionWeight(ConstructionControl.Demolition site)` | A demolition's crew by the rule above (0.7.22): its buildings times the crew one of them can use, by the points its own demolition is - the whole of them, as a building's crew is struck on the whole building. |
| 4925 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 4936 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 4963 | 3 | `static double weightOf(BuildingsTemplate t, long buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 4979 | 4 | `public double waitFor(BuildingsTemplate t, int quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 4992 | 4 | `public double waitOnSite(BuildingsTemplate t, double siteOutput)` | Months what is already on site of one building would wait at this month's shares (0.7.20): waitFor()'s rule with no order added - the stack's own points still owed over its weight's share of everything on site. |
| 4998 | 12 | `private double waitWith(BuildingsTemplate t, int quantity, double siteOutput)` | waitFor()'s arithmetic, for an order of `quantity` - none, for what is on site alone. |
| 5012 | 27 | `private double ruleWait(BuildingsTemplate t, int quantity, double siteOutput)` | The crews' rule's own wait, held steady, for an order of `quantity` - none, for what is on site alone: waitWith() before 0.7.130, and still, with no depot served first. |
| 5041 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 5056 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 5067 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 5084 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 5104 | 67 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 5180 | 5 | `public double takeMaterialsDue()` |  |
| 5196 | 1 | `public double getPointsBuilt()` |  |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 5198-5450)

| line | len | member | says |
|---:|---:|---|---|
| 5220 | 1 | `public ConstructionControl getControl()` |  |
| 5225 | 5 | `public ConstructionControl.Events takeControlEvents()` |  |
| 5232 | 1 | `public double getOvertimePoints()` | The overtime's points this month past the sites' shares - negative from a rush's third month on. |
| 5244 | 64 | **type** `public static final class Plan` | One month's crews, site by site, as the advance applies them: the rule's share, the city's in the city's order, and a rushed site's month on overtime. |
| 5260 | 11 | `Plan(java.util.List<String> keys, double output, double[] owed, double[] rule, double[] share, double[] work, java.util.List<Co...` _(in BuildingManager.Plan)_ |  |
| 5277 | 17 | `public String line()` _(in BuildingManager.Plan)_ | The construction page's line where the order shows: DEATH_CARE_FIRST and how many lie unburied (0.7.129); BUILDERS_FIRST and their depots on site, with the death care beside them while the dead wait (0.7.130); null wh... |
| 5295 | 1 | `public double shareOf(String key)` _(in BuildingManager.Plan)_ | This month's share of the crews, before any overtime. |
| 5297 | 1 | `public double workOf(String key)` _(in BuildingManager.Plan)_ | ...and the work the site does on it, overtime included. |
| 5299 | 1 | `public double owedOf(String key)` _(in BuildingManager.Plan)_ | Points still owed. |
| 5301 | 1 | `public double ruleOf(String key)` _(in BuildingManager.Plan)_ | The crews' rule's share, before the city's order. |
| 5303 | 4 | `public ConstructionControl.Overtime overtimeOf(String key)` _(in BuildingManager.Plan)_ | The month's overtime on one site, or null. |
| 5316 | 77 | `public Plan plan(double siteOutput)` | The month's crews at this site output, as the advance would apply them: siteShares(), then the city's share in the city's order (applyPriority()), then each rushed site's month on overtime. |
| 5397 | 1 | `public Plan getLastPlan()` |  |
| 5405 | 14 | `static void applyPriority(double[] share, double[] owed, java.util.List<String> keys, java.util.List<String> order)` | A. |
| 5421 | 6 | `public java.util.List<String> citySiteKeys()` | The city's own sites on site, in the stacks' order and then the demolitions': the ones its order is set over. |
| 5435 | 5 | `public java.util.List<String> cityOrder()` | ...in the order the city's crews serve them: the player's, or the stacks' order with none set - and while the dead wait, the city's death care at its top (0.7.129, DEATH CARE FIRST WHILE THE DEAD WAIT); and a depot of... |
| 5442 | 8 | `public int cityOrderFirst()` | How many of the city's order head it because they are served first (0.7.130): its death care while the dead wait, and its depots - what the page's arrows keep within. |

### DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129, batch T1-B) (lines 5451-5587)

| line | len | member | says |
|---:|---:|---|---|
| 5502 | 1 | `public void setUnburied(java.util.function.DoubleSupplier unburied)` | Told by Game: where the dead lying unburied are read from (0.7.129). |
| 5505 | 5 | `public double unburiedNow()` | The dead lying unburied, as last month settled them; 0 without the hook. |
| 5512 | 3 | `public static boolean servesTheDead(BuildingsTemplate t)` | Whether a template serves the dead: its care type is burial or cremation. |
| 5517 | 4 | `public boolean servesTheDead(String key)` | ...and a site's key: a stack of such a template. |
| 5526 | 6 | `java.util.List<String> deathCareAmong(java.util.List<String> sites)` | Of these sites, the ones served first while the dead wait - those that serve the dead, in the order given - or none when nobody lies unburied. |
| 5538 | 7 | `public java.util.List<String> deathCareFirst()` | The city's death-care sites served first this month, in the order served: empty when nobody lies unburied or none is on site - the rule is then off, and nothing it touches moves. |
| 5547 | 7 | `static java.util.List<String> firstTheDead(java.util.List<String> order, java.util.List<String> dead)` | An order with `dead` taken to its top, each group in the order it had; the order itself with none. |
| 5563 | 24 | `static void applyDeathCareFirst(double[] share, double[] owed, double[] weight, java.util.List<String> keys, java.util.List<Str...` | The city's share with no order of the player's, while the dead wait: what the rule gave the city's sites, together, handed first to the death care top down - each what it still owes, at most, of what is left - and wha... |

### THE BUILDERS' OWN DEPOTS FIRST (0.7.130, batch T1-C) (lines 5588-5883)

| line | len | member | says |
|---:|---:|---|---|
| 5674 | 1 | `void setBuildersFirstForTest(boolean on)` | Harnesses only (BuildersFirstCheck): the twin without the rule, its depots crewed by the crews' rule alone - the counterfactual. |
| 5680 | 3 | `public static boolean addsToTheBuilders(BuildingsTemplate t)` | Whether a template adds to the builders' capacity: the Construction sector's, and it makes building work - a depot. |
| 5685 | 4 | `public boolean addsToTheBuilders(String key)` | ...and a site's key: a stack of such a template. |
| 5694 | 3 | `public void setBuildersCouldStaff(java.util.function.BiPredicate<BuildingsTemplate, Integer> couldStaff)` | Told by Game: how the builders' planner tests a depot's posts against the city's workers (0.7.130). |
| 5702 | 1 | `public void setSitesAtEveryPost(java.util.function.DoubleSupplier sites)` | Told by Game: where the builders' month for the sites, every post offered, is read from (0.7.130). |
| 5712 | 10 | `public boolean goesFirst(BuildingsTemplate t, int extra)` | Whether t's site, with `extra` more of it ordered, goes first: the rule on, t a template that adds to the builders, the city able to staff what would be on site (buildersCouldStaff()), and that site starved - the crew... |
| 5724 | 10 | `public java.util.List<String> buildersOwnOnSite()` | The builders' own sites served first, in the stacks' order: every stack that adds to their capacity with buildings on site, whoever paid for them, while the city could staff them (goesFirst()). |
| 5741 | 8 | `public java.util.List<String> firstGroup()` | The first group this month, in the order the sites stand: the builders' own sites on site, and while the dead wait the city's death care beside them - or none, when no depot is on site (death care first is then the ci... |
| 5757 | 14 | `static double[] firstOffTheTop(double output, double[] weight, double[] owed, java.util.List<String> keys, java.util.List<Strin...` | The month with the first group served off its top: of the whole output, the group takes what it owes, or all of it if less, shared among its sites by the crews' rule (waterFill(), each by its crew's weight and no more... |
| 5773 | 6 | `public java.util.List<String> siteKeysOnSite()` | Every site with work on it: the stacks with buildings on site, and the demolitions. |
| 5781 | 5 | `public boolean isCitySite(String key)` | Whether a site is the city's own: a stack whose every order on site is the city's, or a demolition. |
| 5788 | 5 | `public BuildingsStacks stackOfKey(String key)` | The stack a site's key names, or null. |
| 5795 | 6 | `public String nameOfSite(String key)` | A site's name: its building's, "Demolishing" before a demolition's. |
| 5803 | 20 | `private void advanceDemolitions(Plan plan)` | D. |
| 5831 | 17 | `private void stopCancelled()` | C. |
| 5859 | 11 | `private void retirePaved(BuildingsStacks stack)` | F. |
| 5872 | 11 | `public void resumeShell(ConstructionControl.Shell shell)` | A shell put back on site (Game.restartShell()): its buildings, the work in them and what they still owe, onto its stack. |

### ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that (lines 5884-6218)

| line | len | member | says |
|---:|---:|---|---|
| 5917 | 4 | `private boolean handOn(String key)` | Whether the player's hand times this site: a city site while an order is set, or a rushed one - or, since 0.7.129, any city site while the city's death care is served first (DEATH CARE FIRST WHILE THE DEAD WAIT). |
| 5928 | 11 | `public double siteMonths(String key, double siteOutput)` | Months a site on site would take at today's queue: its points over its weight's share of everything on site, by the rule held steady - or as the player's hand times it (handWait()). |
| 5947 | 13 | `public double cityOrderWait(BuildingsTemplate t, int quantity, double siteOutput)` | Months n buildings of t would take if the city ordered them now: with no order set and no rush on t's site, waitFor() exactly - the quote's wait since 0.7.17; otherwise as the hand times it, the order joining t's site... |
| 5967 | 8 | `public double restartWait(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months a shell of n buildings owing `owedPoints` would take if it were restarted now (0.7.22), beside whatever of it is on site already: by the rule as waitFor() times an order, or as the hand times it, as cityOrderWa... |
| 5976 | 4 | `private boolean isOnSite(BuildingsTemplate t)` |  |
| 5989 | 13 | `private double[] joined(BuildingsTemplate t, int n, double owed)` | What t's site would be with n more of it owing `owed` more points on it - {its crew's weight, its points owed} - when the player's hand times it: a city site (or none, which the city's order would make one) while an o... |
| 6004 | 1 | `public double pointsOwedOnSite()` | Points owed on every site, demolitions included: everything owed. |
| 6013 | 10 | `public double waitForDemolition(int n, double points, double siteOutput)` | Months a demolition of `points` over n buildings would take if ordered now, at today's queue (0.7.22): its own crew (demolitionWeight()) by the rule held steady - and with the city's order set, behind everything the c... |
| 6030 | 20 | `public double siteMonthsWith(BuildingsTemplate t, int n, double owedPoints, double siteOutput)` | Months n buildings of t owing `owedPoints` would take if put on site now - a shell restarted (0.7.22): beside whatever of it is on site already, as waitFor() times an order. |
| 6064 | 48 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput)` | THE HAND'S ARITHMETIC: every site on site, its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, and added at the bottom of the city's order when it is a new site of the city's - then th... |
| 6114 | 11 | `private double onOvertime(String key, double owed, double months, boolean isNew, java.util.List<String> city)` | A wait worked on overtime when the site is a rushed city site on site: the same pace, at each coming month's factor; the wait itself otherwise. |
| 6127 | 5 | `private boolean firstOn(BuildingsTemplate t, int quantity)` | Whether a wait is the first group's (0.7.130): a depot of the builders' on site served first, or `quantity` more of `t` that would be. |
| 6134 | 6 | `private double[] withMore(BuildingsTemplate t, int n, double owed)` | t's site with n more of it owing `owed` more points on it: {its crew's weight, its points owed}. |
| 6155 | 63 | `private double firstWait(String key, double[] mine, boolean isNew, boolean cityOrder, double siteOutput)` | THE FIRST GROUP'S ARITHMETIC (0.7.130): every site on site with its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, a new one added - and the first group: the builders' own sites, and ... |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 6219-6748)

| line | len | member | says |
|---:|---:|---|---|
| 6241 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` | A share for every job type of a sector alike (the builders'). |
| 6246 | 3 | `public void setOfferedShareByType(java.util.function.ToDoubleBiFunction<String, JobType> offeredShare)` | ...and one a job type at a time (FR2: Freight's drivers). |
| 6251 | 4 | `public static long postsOffered(long posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 6257 | 5 | `public double offeredShareOf(String sector, JobType type)` | A sector's share of its posts of a job type on offer: 1 without the hook. |
| 6264 | 6 | `public long[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - its buildings' posts at its share of each. |
| 6286 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 6292 | 5 | `public double takeRevenueDue()` |  |
| 6301 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 6308 | 10 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 6313 | 4 | `Completion(String building, int quantity)` _(in BuildingManager.Completion)_ |  |
| 6319 | 7 | `public void displayAllBuildings()` |  |
| 6329 | 7 | `public int countByName(String name)` | How many finished buildings of this name the city has. |
| 6347 | 7 | `public int underConstructionByName(String name)` | How many of one named building are on site right now. |
| 6355 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 6364 | 13 | `public long[] getTotalJobs()` |  |
| 6384 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 6400 | 14 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 6430 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 6447 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 6472 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 6492 | 7 | `public long getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 6501 | 9 | `public long getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 6511 | 10 | `public int getUnderConstruction()` |  |
| 6521 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 6525 | 5 | `public long getTotalJobs(JobType type)` |  |
| 6532 | 6 | `public long getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 6551 | 13 | `public long getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 6566 | 5 | `public long getPostsWithheld()` | getPostsWithheld() over every job type. |
| 6587 | 13 | `public long getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 6608 | 21 | `public long[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 6631 | 13 | `public long getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 6645 | 5 | `public long getTotalHouseCapacity()` |  |
| 6658 | 3 | `public long getTotalStoreCoverage()` | Commercial Methods |
| 6663 | 3 | `public long getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 6711 | 7 | `public long getTotalConstructionCapacity()` |  |
| 6725 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 6741 | 3 | `public long getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 6745 | 3 | `public long getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 6749-7177)

| line | len | member | says |
|---:|---:|---|---|
| 6759 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 6773 | 7 | `public int standingWhere(java.util.function.Predicate<BuildingsTemplate> which)` | How many finished buildings, in every sector, have a template this accepts (FR7): Freight counts the bulk works a spur could be laid into. |
| 6782 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 6793 | 7 | `public int getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 6802 | 9 | `public long[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier. |
| 6813 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 6824 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 6841 | 9 | `public double getBuildingMaterialsBySector(String sector)` | The construction materials in a sector's buildings, finished and on site, in units (0.7.75, the sector statements' R6): what getBuildingsValueBySector() prices at today's materials, so a move in that price moves the s... |
| 6852 | 9 | `public long getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 6863 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build. |
| 6870 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 6887 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 6901 | 13 | `public long getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 6915 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 6941 | 9 | `public long getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 6958 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 6983 | 14 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 7015 | 25 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 7053 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 7089 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 7111 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included. |
| 7123 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 7135 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 7146 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 7165 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 7178-7486)

| line | len | member | says |
|---:|---:|---|---|
| 7193 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 7205 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 7216 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 7229 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 7240 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 7254 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 7270 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 7277 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 7301 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 7313 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 7325 | 10 | `public long[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 7336 | 16 | `public long[] getJobArrayPerCategory(BuildingType category)` |  |
| 7356 | 3 | `public int getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 7360 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 7365 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 7375 | 8 | `public int getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 7384 | 3 | `public int getTemplateCount()` |  |
| 7388 | 9 | `public double[] getConstructionProgress()` |  |
| 7407 | 27 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 7453 | 18 | `public int retire(BuildingsTemplate template, int quantity)` | Scraps finished buildings. |
| 7473 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 7477 | 9 | `public int[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 7487-7805)

| line | len | member | says |
|---:|---:|---|---|
| 7511 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 7519 | 10 | `public double[] getConstructionProgressById()` |  |
| 7530 | 10 | `public int[] getUnderConstructionById()` |  |
| 7542 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 7554 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 7560 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 7566 | 7 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 7575 | 8 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 7585 | 17 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 7604 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 7623 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 7640 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 7652 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 7664 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 7677 | 13 | `public boolean restoreConstruction(int templateId, int underConstruction, double progress, double materialsOwed, double contrac...` | Puts a template's in-progress work back onto its stack. |
| 7691 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 7701 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 7711 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 7715 | 3 | `public void clearStacks()` |  |
| 7744 | 3 | `public void handleConstructionMaterials(int required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 7755 | 5 | `public int takeFromYard(int required)` | Takes what the yard has, up to what was asked. |
| 7761 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 7773 | 4 | `static { ... }` |  |
| 7778 | 7 | `public void resetBuildingManager()` |  |
| 7788 | 8 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 7799 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit - since 0.7.42 the unit over the expected price level, every month (Game.restrikeMoneyConstants()). |


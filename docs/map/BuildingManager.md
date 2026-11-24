# BuildingManager.java - 9,503 lines · 308 methods · 6 constants · model

`ham/citybuildersim/BuildingManager.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The city's buildings: the catalogue of templates, the stacks standing and
> on site with the contracts the builders are working to and who placed
> them, the yard's material, and what the work costs today
> (nonMaterialCost(), since 0.7.19).

**Uses:** [JobType](JobType.md) (552), [BuildingsTemplate](BuildingsTemplate.md) (413), [Good](Good.md) (230), [BuildingType](BuildingType.md) (177), [BuildingsStacks](BuildingsStacks.md) (147), [ConstructionControl](ConstructionControl.md) (89), [CareType](CareType.md) (24), [BuildersShort](BuildersShort.md) (23), [Resource](Resource.md) (18), [EducationType](EducationType.md) (18), [RefineryFlow](RefineryFlow.md) (14), [Deposit](Deposit.md) (13), [SafetyType](SafetyType.md) (12), [Ports](Ports.md) (10), [NewUnit](NewUnit.md) (6), [BuildingInstance](BuildingInstance.md) (4), [Automotive](Automotive.md) (4), [ResearchCatalog](ResearchCatalog.md) (4), [HousingInventory](HousingInventory.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Manufacturing](Manufacturing.md) (3), [BridgeWorks](BridgeWorks.md) (3), [Fitout](Fitout.md) (2), [Substations](Substations.md) (2), [HousingRecipe](HousingRecipe.md) (2), [Healthcare](Healthcare.md) (2), [BuildingCatalog](BuildingCatalog.md) (1), [GoldPrice](GoldPrice.md) (1), [CopperMining](CopperMining.md) (1), [Sectors](Sectors.md) (1)

**Used by (162):** [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankFailureCheck](BankFailureCheck.md), [BasketYearCheck](BasketYearCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingStackCheck](BuildingStackCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CarCheck](CarCheck.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [CopperBufferCheck](CopperBufferCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [DistressBleedCheck](DistressBleedCheck.md), [ER1Check](ER1Check.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [FoodIndustryCheck](FoodIndustryCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [GapOrderCheck](GapOrderCheck.md), [GdpCheck](GdpCheck.md), [GoldMiningCheck](GoldMiningCheck.md), [GovernmentScreen](GovernmentScreen.md), [GroundInputCheck](GroundInputCheck.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [HousingInventoryCheck](HousingInventoryCheck.md), [HydroCheck](HydroCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [Inbox](Inbox.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MedicineCheck](MedicineCheck.md), [MiningCheck](MiningCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [NewUnitCheck](NewUnitCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [Ports](Ports.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [ResearchCheck](ResearchCheck.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [RestructureCheck](RestructureCheck.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ServicesManager](ServicesManager.md), [ServicesScreen](ServicesScreen.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [StaffingDrawCheck](StaffingDrawCheck.md), [StrategicReserve](StrategicReserve.md), [SubstationCheck](SubstationCheck.md), [Substations](Substations.md), [SulphideLeachCheck](SulphideLeachCheck.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [UndemandedFeedCheck](UndemandedFeedCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [YoungBankCheck](YoungBankCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 78 | THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) |
| 316 | · WATER DRAW, per building, in units of 10,000 gallons/month. |
| 518 | · HEALTHCARE |
| 558 | · · childcare, resized (0.7.71, batch N2) |
| 922 | · WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH |
| 1046 | · A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of |
| 1081 | · THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10). |
| 1156 | · · THE COAL POWER PLANT, refigured (PW2; spec-electricity 2.3). A 325 MW |
| 1195 | · · WIND |
| 1331 | · · STEEL |
| 1397 | · · INFRASTRUCTURE |
| 1534 | · THE THINGS THAT CARRY PEOPLE (2026-09-16) |
| 1626 | · · RAIL |
| 1737 | · · AUTOMOTIVE |
| 1789 | · THE LUXURY SHOPS (2026-09-17) |
| 1861 | · THE KITCHENS (2026-09-18) |
| 2054 | · · MINING |
| 2138 | · · FUEL (0.7.62) |
| 2194 | · · THE REFINERY'S UNITS (0.7.80, batch O4) |
| 2466 | · · THE FILLING STATION (0.7.83) |
| 2496 | · · THE TANK FARM AND THE STRATEGIC RESERVE (0.7.85) |
| 2543 | · · THE PORTS (0.7.86) |
| 2661 | · · THE OIL AT SEA (0.7.91) |
| 2718 | · · THE INTEGRATED ROUTE (0.7.123, batch I4) |
| 2887 | · · IRON MINING BY TYPE AND SIZE (0.7.124, batch I5) |
| 2975 | · · COAL MINING (0.7.126, batch I6) |
| 3089 | · · QUARRYING AND CEMENT & CONCRETE (0.7.131, batch I7) |
| 3244 | · · COPPER MINING AND COPPER REFINING (0.7.133, batch I8) |
| 3437 | · · FORESTRY (0.7.138, batch I9) |
| 3523 | · · WOOD PRODUCTS (0.7.140, batch I10) |
| 3681 | · · HAULAGE (FR2, 2026-10-18) |
| 3741 | · · THE RAIL DEPOT (FR7, 2026-10-18) |
| 3780 | · · EDUCATION |
| 3993 | · · SAFETY |
| 4104 | · THE BANK |
| 4133 | · TWENTY-NINE STAFF - 278, then 69, now the figure the world has |
| 4194 | · BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR |
| 4282 | · · And the one with a gate on it. Seventy-eight licensed engineers, at |
| 4314 | · MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL |
| 4475 | · · The machine plant, and the one that does NOT want a yard. Eight |
| 4514 | · · And the same trade at scale. Two and a half times the shop's output |
| 4543 | · AGRICULTURE - THE GROUND UNDER THE LOAF |
| 4646 | · · And the same thing at three times the size on two and a half times as |
| 4666 | · · And the one that escapes the clock. Twenty times a field's yield off |
| 4702 | · THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE |
| 4740 | · THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16) |
| 4898 | · · HYDRO STATION (HY1, id 144). Canada's power was mostly hydro from |
| 4948 | · · THE LONG BRIDGES (ET6, ids 145 to 147). A Highway Bridge, a Rail |
| 4994 | · · THE RESEARCH LABS (R4, ids 148 to 151; docs/studies/research-design.md 5.3). A Business Services building of Research... |
| 5066 | · · GOLD MINE AND MILL (GD4, next permanent id after R4) |
| 5086 | · · THE NEAR FUTURE (R6b, 2026-11-16; docs/studies/research-near-future.md 3, docs/queue/R6b-notes.md) |
| 5143 | · · THE POWER PLANTS AND THEIR FUELS (PW2, ids 167 to 171; RES3 takes |
| 5344 | EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) |
| 5589 | · the build-time floor (PW2) |
| 5936 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 6204 | · DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129, batch T1-B) |
| 6341 | · THE BUILDERS' OWN DEPOTS FIRST (0.7.130, batch T1-C) |
| 7158 | · ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that |
| 7633 | · THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the |
| 8193 | BY SECTOR (2026-09-11, the sector template) |
| 8735 | THE SAME TWO SUMS, NARROWED TO ONE SERVICE. |
| 9152 | · Construction state for the save, keyed by template id. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `BuildingManager.MATERIALS_WORLD_PRICE` | `Good.MATERIALS.worldImportPrice()` | What a unit of construction material costs, in the city's money. |
| 5439 | `BuildingManager.CREW_SCALE_EXPONENT` | `0.70` | The power of a building's construction points its crew grows by: 1 - Bromilow's B. |
| 6249 | `BuildingManager.DEATH_CARE_FIRST` | `"Death care first while the dead wait"` | The page's words for the rule, which Plan.line() opens with: what it does and when. |
| 6431 | `BuildingManager.BUILDERS_FIRST` | `"The builders' own depots first"` | The page's words for the rule, which Plan.line() opens with when a depot is on site. |
| 8142 | `BuildingManager.BASE_MATERIALS` | `36` | The public works yard: it produces this many units of materials a month on its own. |
| 9465 | `BuildingManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private List<BuildingsTemplate> templates` |  |
| 22 | `private List<BuildingsStacks> stacks` |  |
| 23 | `private List<BuildingInstance> instances` |  |
| 24 | `private JobType[] jobTypes` |  |
| 37 | `private long foundingEndowments` | How many founding endowments the city holds (FW9, 2026-10-25): the hundred household places, the doctor, the nursery, the almshouse, the churchyard and the constabulary a city is founded with (getTotalHouseCapacity(),... |
| 42 | `private long constructionMaterials` | The yard's stock, in units: a long since SQ1b, as every figure a draw reads or reduces is (takeFromYard()), so that a month's draw past 2^31 units takes its share of it without a narrowing on the way. |
| 76 | `private double materialsCost` |  |
| 139 | `private BuildersWages buildersWages` |  |
| 212 | `private double expectedLevel` | The expected price level the templates' cash was last struck at (Expectations.getStruckLevel(), the level the last month ended on); 1.0 until Game re-strikes. |
| 308 | `private double exchangeRate` | The rate the goods are struck at (FC2): 1 until EconomyManager hands one. |
| 5237 | `private java.util.function.Consumer<BuildingsStacks> onScrap` |  |
| 5238 | `private final HousingInventory housing` |  |
| 5311 | `private java.util.function.Predicate<BuildingsTemplate> templateKnown` | The city's live knowledge (R2b); a standalone catalogue keeps every template available. |
| 5903 | `private double materialsDue` | Units of material this month's building work drew on, summed over the sites by advanceConstruction() - the builders' purchase for the month, which Game.drawSiteMaterials() takes from the yard, the plant and the world. |
| 5912 | `private double revenueDue` | ...and what the same work earned of the builders' contracts. |
| 5919 | `private double pointsBuilt` | The points this month's advance actually put into buildings (0.7.17): the site output less what no site could use. |
| 5928 | `private double abroadPointsBuilt, abroadBill` | The foreign crews' work on the sites handed them this month, in points, and what the builders owe them for it (FC2b; ConstructionControl, G. |
| 5956 | `private final ConstructionControl control` | The player's hand on the queue: the order, the rushes, the shells, the demolitions and the buy-outs. |
| 5961 | `private ConstructionControl.Events controlEvents` | What this month's advance left for Game to settle: the overtime worked, the orders stopped, the demolitions done. |
| 5983 | `public final java.util.List<String> keys` |  |
| 5985 | `public final double output` | The site output it was struck on. |
| 5987 | `public final double[] owed, rule, share, work` | Points owed; the rule's share (siteShares()); the share after the first group and the city's order; the work, overtime in. |
| 5988 | `public final java.util.List<ConstructionControl.Overtime> overtime` |  |
| 5989 | `public final double overtimePoints` |  |
| 5991 | `public final java.util.List<String> deathCareFirst` | The city's death-care sites served first because the dead wait, in the order served (0.7.129); empty when the rule is off. |
| 5993 | `public final double unburied` | ...and the dead lying unburied that put them there, as Healthcare counted them last month; 0 when the rule is off. |
| 5995 | `public final java.util.List<String> buildersFirst` | The builders' own sites served off the top of the month (0.7.130), in the stacks' order; empty when none is on site. |
| 5997 | `public final long buildersOnSite` | ...and the buildings on them: the depots the line counts. |
| 6148 | `private Plan lastPlan` | The plan the last month's advance applied, or null when the player's hand was not on the queue and nothing was served first - no death care (0.7.129), no depot of the builders' (0.7.130): a month's reading, not saved. |
| 6252 | `private java.util.function.DoubleSupplier unburied` | The dead lying unburied, as Game hands them (Healthcare.getUnburied()); null - a harness's bare manager - reads none. |
| 6425 | `private boolean buildersFirstOn` | Whether the rule is on: always, in the game; a harness's twin turns it off to measure the city without it. |
| 6445 | `private java.util.function.BiPredicate<BuildingsTemplate, Long> buildersCouldStaff` | Whether the city could staff n of the builders' depots, by their own plan's test (Sector.staffing(), MIN_STAFFABLE_TO_ORDER), as Game hands it; null - a bare manager - says yes. |
| 6453 | `private java.util.function.DoubleSupplier sitesAtEveryPost` | The builders' month for the sites with every post offered, as Game hands it (Game.getBuildingOutputAtEveryPost(), their planner's figure); null - a bare manager - reads none, and every depot is starved. |
| 6459 | `private java.util.function.DoubleSupplier priorityWork` | Finite work done ahead of sites, rather than a permanent reduction in throughput (R1d review). |
| 6477 | `private java.util.function.DoubleSupplier outputAtEveryPost` | The builders' whole month with every post offered, before the repairs, as Game hands it (Game.getConstructionOutputAtEveryPost(), FC1); null - a bare manager - reads none, and the premium and the foreign contractor ar... |
| 6494 | `static boolean buildersShortOn` | Whether the builders' scarcity price and the foreign contractor are on (FC1): always, in the game. |
| 6497 | `private java.util.function.DoubleSupplier foreignBook` | Where the foreign crews' book is read from (FC2; sectors.Construction.getForeignBook()), as Game hands it; null - a bare manager - reads none. |
| 7652 | `private java.util.function.ToDoubleBiFunction<String, JobType> offeredShare` | The share of a sector's posts of a job type it offers this month; null offers every post. |
| 7721 | `private final java.util.List<BuildingsStacks.Due> contractsDue` | ...and payer by payer (0.7.19): each order's share of the month's work, units and allowance. |
| 7732 | `public final String building` |  |
| 7733 | `public final long quantity` |  |
| 7735 | `public final HousingRecipe housingRecipe` | This one completion delivers quantity times this mix; null for non-housing. |
| 7737 | `public final long loose` | Of those, the ones no order fitted out (loose on site, BuildingsStacks): what they come with is decided at the finish (R1e). |
| 7739 | `public final java.util.Map<String, Long> without` | ...and of the rest, how many came without each piece, by id, as their orders froze them: unmodifiable (R1e). |
| 7977 | `private java.util.function.Supplier<long[]> postsPast` | The posts per job type the sectors run past their buildings' (R0c, the horse form; sectors.Agriculture): Game hands in Sectors.postsPastBuildings(), null for none. |
| 8243 | `private java.util.function.Function<BuildingsTemplate, NewUnit> newUnits` | WHAT IS ON SITE, AS IT WILL OPEN (QN1, 2026-11-19). |
| 9250 | `public int templateId` |  |
| 9251 | `public String payer` |  |
| 9252 | `public boolean creditable` |  |
| 9254 | `public Double recovered` | The share of the tax on it the payer gets back; absent from a save written before the rebates, which read it off creditable. |
| 9255 | `public double value, units, allowance` |  |
| 9257 | `public double points` | The points of work its orders put on site still owed (FC2b); absent from an older save, which reads none. |
| 9259 | `public boolean abroad` | Whether it is a contract handed to the foreign crews (FC2b; BuildingsStacks.getAbroadContracts()), not a home one. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 9485 | **type** `public class BuildingManager` | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed them, the yard's material, and what the work costs today (nonMaterialCost... |

### THE LABOUR IN A PRICE KEEPS UP WITH WAGES (0.7.19) (lines 78-315)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 6 | **type** `public interface BuildersWages` | What the builders' crews cost a point of output, today and at the founding ladder, both in today's money. |
| 134 | 1 | `double perPointToday()` _(in BuildingManager.BuildersWages)_ | A depot's posts at today's wages, over its points. |
| 136 | 1 | `double perPointAtFounding()` _(in BuildingManager.BuildersWages)_ | ...and at the founding ladder, PayTier, in today's unit. |
| 141 | 1 | `public void setBuildersWages(BuildersWages wages)` |  |
| 144 | 3 | `public double buildersWagePerPoint()` | What the builders' crews cost a point today (a depot's posts at today's wages, over its points): the bill a rushed site's overtime premium is struck on (0.7.22). |
| 149 | 8 | `public double buildersWageIndex()` | The builders' labour index: their wage bill a point today over at founding. |
| 165 | 3 | `public double nonMaterialCost(BuildingsTemplate t)` | The non-material part of what one of these costs to build today (0.7.19): its cash cost, with its labour - its points at the builders' founding wage bill a point, never more than the whole cash cost - at today's build... |
| 173 | 3 | `public double nonMaterialCost(Fitout fit)` | ...for one building of an order's fitout (R1e): the same arithmetic on its own cash cost and points, the template's shell and the pieces it comes with (Fitout); the template's own figures, to the bit, for the whole te... |
| 177 | 11 | `private double nonMaterialCost(double cash, double points)` |  |
| 190 | 7 | `public double labourCost(BuildingsTemplate t)` | ...its labour part alone, at today's wages. |
| 215 | 3 | `public void setExpectedLevel(double level)` | Told by Game with every re-strike of the templates; anything not positive is ignored. |
| 220 | 1 | `public double getExpectedLevel()` | The level the cash costs are struck at. |
| 223 | 3 | `public double structureCost(BuildingsTemplate t)` | ...and with its materials at the world's price today: what it would cost to put up now, before the sales tax. |
| 237 | 3 | `public double basePricePerPoint()` | What the builders are paid for a point of work, beyond its material and its sales tax (0.7.19): the non-material price of the work on site, averaged over the points still owed; with nothing on site, of the repairs - t... |
| 250 | 3 | `public double basePricePerPointAtFounding()` | ...the same work at the founding's wages and level (FC2): each template's cash cost as the catalogue states it, in founding dollars (BuildingsTemplate.getFoundingCashCost(), which the price level and a currency reform... |
| 255 | 20 | `private double pricePerPointOf(java.util.function.ToDoubleFunction<BuildingsTemplate> costOf)` | basePricePerPoint()'s average, each template's non-material cost read by `costOf`: over the points owed on site, or the standing city's mix with nothing on site. |
| 283 | 3 | `public double worldPricePerPoint()` | The world's base price for a point of the same work, in the city's money (FC2): the founding's price for it (basePricePerPointAtFounding(); the D$ opened at par, ForeignAccounts.OPENING_PARITY) carried at the world's ... |
| 288 | 1 | `public double getExchangeRate()` | The rate the goods are struck at, as EconomyManager last handed it (setExchangeRate()): the D$ per US$ times the world's level. |
| 297 | 3 | `public double nonMaterialPricePerPoint()` | What a depot earns a point of work (FC1): basePricePerPoint() with the scarcity premium the orders pay while the builders are short (scarcityPremium()) - what Construction.estimatedMonthlyProfit() reads, so the premiu... |
| 301 | 5 | `public void setExchangeRate(double rate)` |  |
| 310 | 5 | `public BuildingManager()` |  |

### WATER DRAW, per building, in units of 10,000 gallons/month. (lines 316-5343)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 12 | `public void initializeTemplates()` | Loads every building the game knows about. |
| 362 | 4837 | `public void initializeBuiltInTemplates()` | The definitions as code. |
| 5200 | 6 | `public void finalUpdateBuildings()` |  |
| 5208 | 3 | `public double getConstructionMaterialPrice()` | getters |
| 5214 | 5 | `public int[] stackOrder()` | The template ids of the stacks, in the order they stand: the order each was first bought (0.7.43; DataSave.getStackOrder()). |
| 5226 | 10 | `public void orderStacks(int[] ids)` | Stands the stacks in a saved order (0.7.43): each in the place its id holds in `ids`, any the order does not name after them in the order they stand. |
| 5241 | 1 | `public HousingInventory getHousingInventory()` | Physical recipe cohorts and their aggregate titles/occupants (RES2). |
| 5244 | 4 | `public void setOnScrap(java.util.function.Consumer<BuildingsStacks> listener)` | The fitting queue follows every stack's removals, told the stack after its counts fell; installed again by Game when its world is rebuilt (R1d). |
| 5249 | 3 | `public void addStack(BuildingsTemplate template, long quantity, boolean noConstruction)` |  |
| 5258 | 35 | `public void addStack(BuildingsTemplate template, long quantity, boolean noConstruction, Fitout fit)` | ...with the order's fitout (R1e; Fitout): a fitout the order chose goes on site as the stack's newest lot (BuildingsStacks, THE ORDERS' EQUIPMENT ON SITE), which keeps its set and owes its points until it finishes. |
| 5299 | 5 | `public void addPreparation(BuildingsTemplate template, double points)` | Puts a foothill site's preparation on the template's stack as owed work (ET2; SitePreparation): called by Game once the order's stack exists (addStack()), with the points its quote carried. |
| 5306 | 3 | `public List<BuildingsTemplate> getTemplates()` | Every template, in load order. |
| 5314 | 3 | `public void setTemplateKnown(java.util.function.Predicate<BuildingsTemplate> known)` | Supplied by Game once, reading the current month and local discoveries on every query. |
| 5319 | 1 | `public boolean isTemplateKnown(BuildingsTemplate t)` | One rule for candidate lists and orders; the complete catalogue remains available for saves and accounting. |
| 5322 | 5 | `public List<BuildingsTemplate> getKnownTemplates()` | Every building the city knows how to build now, in catalogue order (R2b). |
| 5328 | 11 | `public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories)` |  |
| 5340 | 3 | `public void addInstance(BuildingsTemplate template)` |  |

### EVERY BUILDING GETS THE CREW IT CAN USE (0.7.17) (lines 5344-5588)

| line | len | member | says |
|---:|---:|---|---|
| 5455 | 39 | `public double[] siteShares(double siteOutput, BuildingsTemplate extra, int extraCount)` | Each stack's share of the month's site output, in the order of the stacks, by the rule above. |
| 5500 | 4 | `static double demolitionWeight(ConstructionControl.Demolition site)` | A demolition's crew by the rule above (0.7.22): its buildings times the crew one of them can use, by the points its own demolition is - the whole of them, as a building's crew is struck on the whole building. |
| 5506 | 4 | `public double[] siteShares(double siteOutput)` | The month's site output by the rule above, one share per stack. |
| 5517 | 25 | `static double[] waterFill(double output, double[] weight, double[] owed)` | One rate per unit of weight, each claimant capped at what it is owed, and what a capped claimant leaves shared among the rest by the same rule - the order a claimant runs out in is the order of owed over weight, so th... |
| 5544 | 3 | `static double weightOf(BuildingsTemplate t, long buildingsOnSite)` | A stack's crew by the rule above: its buildings on site times the crew one of them can use, its points to CREW_SCALE_EXPONENT. |
| 5560 | 3 | `public double waitFor(BuildingsTemplate t, long quantity, double siteOutput)` | Months an order would wait at this month's shares (0.7.17): what its stack would owe with it - the order's points, and whatever of the same building is on site ahead of it - over the share that stack would get of the ... |
| 5565 | 4 | `public double waitFor(BuildingsTemplate t, long quantity, double extraPoints, double siteOutput)` | ...with `extraPoints` more owed on the order's site than its buildings' own (ET2: a foothill pad's preparation, SitePreparation); waitFor() exactly for none. |
| 5578 | 4 | `public double waitOnSite(BuildingsTemplate t, double siteOutput)` | Months what is already on site of one building would wait at this month's shares (0.7.20): waitFor()'s rule with no order added - the stack's own points still owed over its weight's share of everything on site. |
| 5584 | 4 | `private double waitWith(BuildingsTemplate t, long quantity, double extraPoints, double siteOutput)` | waitFor()'s arithmetic, for an order of `quantity` - none, for what is on site alone - and `extraPoints` more owed on its site (ET2); never sooner than its build time allows (PW2, floorWait()). |

### the build-time floor (PW2) (lines 5589-5935)

| line | len | member | says |
|---:|---:|---|---|
| 5609 | 4 | `static double monthCap(BuildingsTemplate t, double pointsOnSite)` | The most a site of this building may be worked in a month: its points on site over its build time; unbounded for a building with none (PW2). |
| 5615 | 3 | `static double siteCap(BuildingsStacks s, double extraPoints)` | A site's cap (PW2): its buildings' points on site, and `extraPoints` more of them, over their build time, and all the preparation it still owes. |
| 5623 | 5 | `private double homeOwedOf(BuildingsStacks s)` | The points a stack owes that the city's crews' rule shares (FC2b): all it owes, less what is handed to the foreign crews on it (ConstructionControl, G. |
| 5634 | 5 | `private double homeCapOf(BuildingsStacks s, double extraPoints)` | siteCap() less the foreign crews' month on the site (FC2b): a site's build time is the site's, both crews together (PW2), so what they build of it in a month - the least of what they owe and their pace - is not the ci... |
| 5641 | 3 | `static double capped(double owed, double cap)` | What a site may take this month: all it owes when that is within its cap (a hair over is rounding, and taken, so the last month finishes it rather than holding it a month for the hair), else the cap. |
| 5646 | 9 | `double floorWait(BuildingsTemplate t, long quantity, double extraPoints)` | The months its build time holds an order's site to at the least (PW2): its months x the buildings' own points the site would owe with the order over their points with it, its preparation (and the order's, `extraPoints... |
| 5657 | 12 | `private double crewWait(BuildingsTemplate t, long quantity, double extraPoints, double siteOutput)` | waitWith() before PW2: the crews' wait alone, no build time counted. |
| 5674 | 5 | `private static double orderPoints(BuildingsTemplate t, long quantity, double extraPoints)` | The points an order of `quantity` owes: its buildings' own, and `extraPoints` more on its site (ET2) - the buildings' alone, to the bit, for none. |
| 5681 | 47 | `private double ruleWait(BuildingsTemplate t, long quantity, double extraPoints, double siteOutput)` | The crews' rule's own wait, held steady, for an order of `quantity` - none, for what is on site alone: waitWith() before 0.7.130, and still, with no depot served first - and `extraPoints` more owed on its site (ET2). |
| 5730 | 4 | `public double shareOf(BuildingsStacks site, double siteOutput)` | One stack's share of the month's site output, by the rule above. |
| 5747 | 9 | `public double siteShareOfSector(String sector, double siteOutput, BuildingsTemplate extra, int extraCount)` | What one sector's sites would get of the month's site output, with an order counted as though it were already on site (0.7.17). |
| 5758 | 9 | `public double pointsOwedBySector(String sector)` | Points one sector's sites still owe (0.7.17). |
| 5775 | 6 | `public double monthsLeft(BuildingsStacks site, double perSiteOutput)` | Months until a site's last building finishes at a given per-site output: the points still owed over the pace. |
| 5795 | 3 | `public java.util.List<Completion> advanceConstruction(int constructionOutput)` | Runs the month's construction and reports what actually opened. |
| 5804 | 92 | `public java.util.List<Completion> advanceConstruction(int constructionOutput, int foreignPoints)` | ...with `foreignPoints` of the month the foreign contractor's (FC1b; SimulationEngine passes his offer): the sites take the whole month as before, and a rushed site's overtime is struck on the city's own crews' part o... |
| 5905 | 5 | `public double takeMaterialsDue()` |  |
| 5921 | 1 | `public double getPointsBuilt()` |  |
| 5931 | 1 | `public double getAbroadPointsBuilt()` | The foreign crews' work on the sites handed them this month, in points (FC2b). |
| 5934 | 1 | `public double getAbroadBill()` | What the builders owe the foreign crews for it, in the city's money (FC2b): the points built at each site's price. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 5936-6203)

| line | len | member | says |
|---:|---:|---|---|
| 5958 | 1 | `public ConstructionControl getControl()` |  |
| 5963 | 5 | `public ConstructionControl.Events takeControlEvents()` |  |
| 5970 | 1 | `public double getOvertimePoints()` | The overtime's points this month past the sites' shares - negative from a rush's third month on. |
| 5982 | 64 | **type** `public static final class Plan` | One month's crews, site by site, as the advance applies them: the rule's share, the city's in the city's order, and a rushed site's month on overtime. |
| 5998 | 11 | `Plan(java.util.List<String> keys, double output, double[] owed, double[] rule, double[] share, double[] work, java.util.List<Co...` _(in BuildingManager.Plan)_ |  |
| 6015 | 17 | `public String line()` _(in BuildingManager.Plan)_ | The construction page's line where the order shows: DEATH_CARE_FIRST and how many lie unburied (0.7.129); BUILDERS_FIRST and their depots on site, with the death care beside them while the dead wait (0.7.130); null wh... |
| 6033 | 1 | `public double shareOf(String key)` _(in BuildingManager.Plan)_ | This month's share of the crews, before any overtime. |
| 6035 | 1 | `public double workOf(String key)` _(in BuildingManager.Plan)_ | ...and the work the site does on it, overtime included. |
| 6037 | 1 | `public double owedOf(String key)` _(in BuildingManager.Plan)_ | Points still owed. |
| 6039 | 1 | `public double ruleOf(String key)` _(in BuildingManager.Plan)_ | The crews' rule's share, before the city's order. |
| 6041 | 4 | `public ConstructionControl.Overtime overtimeOf(String key)` _(in BuildingManager.Plan)_ | The month's overtime on one site, or null. |
| 6054 | 1 | `public Plan plan(double siteOutput)` | The month's crews at this site output, as the advance would apply them: siteShares(), then the city's share in the city's order (applyPriority()), then each rushed site's month on overtime. |
| 6061 | 85 | `public Plan plan(double siteOutput, double foreignPoints)` | ...with `foreignPoints` of `siteOutput` the foreign contractor's (FC1b): the shares are the whole month's, and a rushed site's overtime is struck on the city's own crews' part of its share alone - the month's own poin... |
| 6150 | 1 | `public Plan getLastPlan()` |  |
| 6158 | 14 | `static void applyPriority(double[] share, double[] owed, java.util.List<String> keys, java.util.List<String> order)` | A. |
| 6174 | 6 | `public java.util.List<String> citySiteKeys()` | The city's own sites on site, in the stacks' order and then the demolitions': the ones its order is set over. |
| 6188 | 5 | `public java.util.List<String> cityOrder()` | ...in the order the city's crews serve them: the player's, or the stacks' order with none set - and while the dead wait, the city's death care at its top (0.7.129, DEATH CARE FIRST WHILE THE DEAD WAIT); and a depot of... |
| 6195 | 8 | `public int cityOrderFirst()` | How many of the city's order head it because they are served first (0.7.130): its death care while the dead wait, and its depots - what the page's arrows keep within. |

### DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129, batch T1-B) (lines 6204-6340)

| line | len | member | says |
|---:|---:|---|---|
| 6255 | 1 | `public void setUnburied(java.util.function.DoubleSupplier unburied)` | Told by Game: where the dead lying unburied are read from (0.7.129). |
| 6258 | 5 | `public double unburiedNow()` | The dead lying unburied, as last month settled them; 0 without the hook. |
| 6265 | 3 | `public static boolean servesTheDead(BuildingsTemplate t)` | Whether a template serves the dead: its care type is burial or cremation. |
| 6270 | 4 | `public boolean servesTheDead(String key)` | ...and a site's key: a stack of such a template. |
| 6279 | 6 | `java.util.List<String> deathCareAmong(java.util.List<String> sites)` | Of these sites, the ones served first while the dead wait - those that serve the dead, in the order given - or none when nobody lies unburied. |
| 6291 | 7 | `public java.util.List<String> deathCareFirst()` | The city's death-care sites served first this month, in the order served: empty when nobody lies unburied or none is on site - the rule is then off, and nothing it touches moves. |
| 6300 | 7 | `static java.util.List<String> firstTheDead(java.util.List<String> order, java.util.List<String> dead)` | An order with `dead` taken to its top, each group in the order it had; the order itself with none. |
| 6316 | 24 | `static void applyDeathCareFirst(double[] share, double[] owed, double[] weight, java.util.List<String> keys, java.util.List<Str...` | The city's share with no order of the player's, while the dead wait: what the rule gave the city's sites, together, handed first to the death care top down - each what it still owes, at most, of what is left - and wha... |

### THE BUILDERS' OWN DEPOTS FIRST (0.7.130, batch T1-C) (lines 6341-7157)

| line | len | member | says |
|---:|---:|---|---|
| 6428 | 1 | `void setBuildersFirstForTest(boolean on)` | Harnesses only (BuildersFirstCheck): the twin without the rule, its depots crewed by the crews' rule alone - the counterfactual. |
| 6434 | 3 | `public static boolean addsToTheBuilders(BuildingsTemplate t)` | Whether a template adds to the builders' capacity: the Construction sector's, and it makes building work - a depot. |
| 6439 | 4 | `public boolean addsToTheBuilders(String key)` | ...and a site's key: a stack of such a template. |
| 6448 | 3 | `public void setBuildersCouldStaff(java.util.function.BiPredicate<BuildingsTemplate, Long> couldStaff)` | Told by Game: how the builders' planner tests a depot's posts against the city's workers (0.7.130). |
| 6456 | 1 | `public void setSitesAtEveryPost(java.util.function.DoubleSupplier sites)` | Told by Game: where the builders' month for the sites, every post offered, is read from (0.7.130). |
| 6461 | 1 | `public void setPriorityWork(java.util.function.DoubleSupplier points)` |  |
| 6463 | 1 | `private double priorityPoints()` |  |
| 6466 | 9 | `private double priorityWait(double wait, double siteOutput)` | The local crews alone fit equipment; a foreign contractor cannot work this delay off. |
| 6480 | 1 | `public void setOutputAtEveryPost(java.util.function.DoubleSupplier output)` | Told by Game: where the builders' whole month, every post offered, is read from (FC1). |
| 6483 | 1 | `private boolean buildersMonthKnown()` | Whether both months are known (FC1): with either unset - a bare manager in a harness - there is no premium and no foreign contractor, and every price reads what it read before. |
| 6500 | 1 | `public void setForeignBook(java.util.function.DoubleSupplier book)` | Told by Game: where the points the foreign crews have taken and not yet built are read from (FC2). |
| 6503 | 4 | `public double foreignBookPoints()` | The points the foreign crews have taken and not yet built (FC2): nothing with no book to read. |
| 6513 | 3 | `public double queueAtHome()` | The queue the city's own builders hold (FC2): everything owed on site and the work ahead of the sites, less the foreign crews' book - their points are theirs to build, at their own month - and the work handed to them ... |
| 6522 | 4 | `public double monthsQueued()` | The months of work queued at the pace the builders leave the sites (BuildersShort.monthsQueued(): the queue at home as it stands over the sites' month, every post offered); 0 with no month to read. |
| 6532 | 4 | `public double scarcityPremium()` | The premium a point's work carries over its price for the queue as it stands (BuildersShort.premium()), as a share of the price; 0 with no month to read, and 0 while the builders have no month at all, every post offer... |
| 6542 | 1 | `public double homePriceMultiplier()` | What work built at home is multiplied by (REV-FC2, the second review): one and the builders' own premium on the queue at home, never the foreign crews' share - a restart's shell, a demolition and a paving, which no qu... |
| 6550 | 13 | `public void noteAbroad(BuildingsTemplate t, double billed, double abroad)` | An order just billed on t's site (REV-FC2, the second review): {@code billed} points of it on site, {@code abroad} of them handed to the foreign crews' book (Game.processBuildOrder(), buildFor(), after Construction.ha... |
| 6565 | 5 | `private double liveClaims()` | The claims on the foreign crews' book of every site with points owed (noteAbroad()), in their own unit. |
| 6572 | 4 | `private double claimedPoints(BuildingsStacks s, double claims)` | A site's work on the foreign crews' book, in points (REV-FC2, the second review): his book in proportion to its claim; nothing for a site with no points owed. |
| 6582 | 4 | `public double abroadOwedOf(BuildingsTemplate t)` | The points of t's site on the foreign crews' book (REV-FC2, the second review): his book in proportion to the site's claim, never more than the points the site owes - what a cancel of it takes off his book once his mo... |
| 6588 | 9 | `public java.util.Map<Integer, Double> getAbroadOwedById()` | Each site's work on the foreign crews' book in points, by template id, for the save: only the sites with some. |
| 6602 | 6 | `public void restoreAbroadOwed(java.util.Map<Integer, Double> byId)` | ...and restored from it, the points the claims. |
| 6614 | 3 | `public boolean foreignTradeOn()` | Whether the builders' price and the foreign crews are in force (FC2): both months known (buildersMonthKnown()) and a month of the builders' at all, every post offered. |
| 6626 | 5 | **type** `public record ForeignTerms(double sites, double output, double queueAtHome, double base, double world, doub...` | The foreign crews' terms as the city stands (FC2), each month read once - a read of the sites' month is the city's repairs worked out again, and a quote is struck often: the sites' and the builders' month at every pos... |
| 6629 | 1 | `public double monthsQueued()` _(in BuildingManager.ForeignTerms)_ | The months queued at home (BuildersShort.monthsQueued()). |
| 6644 | 2 | **type** `public record OrderAbroad(double fromMonths, double stepMonths, double thresholdMonths, long buildings, dou...` | An order's work abroad (REV-FC2's P1): its work past the threshold, point for point (BuildersShort.homeUnits(): the building the threshold falls in split, its points past it abroad), the buildings with any of it, and ... |
| 6647 | 3 | `public OrderAbroad abroadOfOrder(double pointsAhead, double abroadAhead, double pointsEach, long n, BuildingsTemplate of)` |  |
| 6658 | 27 | `public OrderAbroad abroadOfOrder(double pointsAhead, double abroadAhead, double pointsEach, long n, BuildingsTemplate of, java....` | ...with {@code siteAhead}, the points the orders before it in a run put on site, by template (REV-FC2, the second review; Game.buildRunInvoice()): each on its own template's stack, as placing them would, so the thresh... |
| 6687 | 4 | `public double basePerPointOf(BuildingsTemplate t)` | The base a point of t's work is priced on, before any premium: its non-material cost over its points, as basePricePerPoint() weighs each stack on site; NaN for no template or no points. |
| 6701 | 27 | `private double pricePerPointWith(java.util.function.ToDoubleFunction<BuildingsTemplate> costOf, java.util.Map<BuildingsTemplate...` | pricePerPointOf() with {@code added} more owed on site, points by template (REV-FC2): what it reads once the orders are placed - the base a point (basePricePerPoint()) and, at the founding's, the world's (worldPricePe... |
| 6730 | 15 | `public ForeignTerms foreignTerms()` | The terms now; null with no trade on. |
| 6755 | 3 | **type** `public record AbroadTerms(String key, int templateId, String building, String payer, double share, double p...` | What a payer's share of one site's work would cost and take abroad now (FC2b; ConstructionControl, G. |
| 6765 | 28 | `public AbroadTerms abroadTermsOf(BuildingsTemplate t, String payer, double waitOutput)` | The terms a payer's share of template t's site would be handed to the foreign crews on, now (FC2b), its months at home read at {@code waitOutput} (siteMonths()). |
| 6801 | 27 | `public ConstructionControl.Abroad handSiteAbroad(AbroadTerms a, int month, double paid, double interestSaved, double earningsFo...` | The books' half of a handover (FC2b): the payer's home contract on the site moves to the foreign crews' part (BuildingsStacks.handAbroad()), what it paid over is booked to it (bookAbroad()), and the site's record is m... |
| 6830 | 1 | `private static double finiteOr0(double x)` | A figure for the saved record (REV-FC2b-1): itself when finite, else nothing - the save's Gson refuses an infinity or a NaN. |
| 6833 | 3 | `public double foreignExcessShare()` | The foreign crews' excess share at the curve's first point (BuildersShort.excessShare(): E x / p, capped). |
| 6838 | 3 | `public double thresholdMonths()` | The months queued at home past which an order's work goes abroad (BuildersShort.thresholdMonths() at foreignExcessShare()). |
| 6843 | 4 | `public double overflowAtHome()` | The queue at home past the threshold, in points (BuildersShort.overflow()): work its payers were quoted at home, which the city's builders keep (REV-FC2); nothing with no trade on. |
| 6855 | 5 | `public double foreignMonthNow()` | The foreign crews' month as the next offer would strike it now (BuildersShort.foreignMonth()): their book, in the threshold's months, never under the builders' largest depot; nothing with no trade on. |
| 6862 | 4 | `public double foreignExcessNow()` | Their excess share at that month (BuildersShort.excessAt(), the supply curve): what the work an order sends abroad now pays over the base price; the first point's with no trade on. |
| 6868 | 4 | `public double foreignPricePerPoint()` | What the foreign crews ask a point of work now, in the city's money (FC2): the base price and their excess at their month; nothing with no trade on. |
| 6878 | 4 | `public double exportPricePerPoint()` | What a point the city's builders sell abroad fetches now, in the city's money (FC2): the world's base for the work and the city's own excess on it (BuildersShort.exportShare()); nothing with no trade on, or while the ... |
| 6884 | 4 | `public static double exportPriceOf(ForeignTerms t)` | ...on these terms: nothing past the knee. |
| 6890 | 5 | `public boolean ordersGoAbroad()` | Whether an order placed now sends work abroad (FC2): the queue at home is at the threshold or past it; never with no trade on. |
| 6897 | 1 | `public double orderPriceMultiplier()` | What an order's non-material price (the work, the foothill's preparation, a run's works, a span, deep water) is multiplied by, struck once per quote on the queue as it stands: one and the premium at home, or the forei... |
| 6900 | 1 | `public double orderPriceMultiplier(double pointsAhead)` | ...with `pointsAhead` more on the queue than it holds: what the orders before this one in a run put there, which a run's invoice prices it on (Game.buildRunInvoice()). |
| 6903 | 3 | `public double orderPriceMultiplier(double pointsAhead, double pointsEach, long n)` | ...for an order of `n` buildings of `pointsEach` points on the base as it stands (the founding's village, Founding.whatItBuys()): the four-figure form with no base a point to read. |
| 6915 | 3 | `public double orderPriceMultiplier(double pointsAhead, double pointsEach, long n, BuildingsTemplate of)` | ...for an order of `n` buildings of `pointsEach` points (BA1): each building priced on the queue at home the ones before it in the order leave, at the premium while it stands within the threshold and at the foreign cr... |
| 6920 | 3 | `public double orderPriceMultiplier(double pointsAhead, double abroadAhead, double pointsEach, long n, BuildingsTemplate of)` | ...with {@code abroadAhead} of the points ahead handed abroad by the orders before it in a run (abroadOfOrder(); Game.buildRunInvoice()): what placing it after them asks. |
| 6925 | 6 | `public double orderPriceMultiplier(double pointsAhead, double abroadAhead, double pointsEach, long n, BuildingsTemplate of, jav...` | ...with the points the orders before it in a run put on site, by template (abroadOfOrder()'s six-argument form). |
| 6933 | 10 | `public double buildersOwnOwed()` | Points still owed on the builders' own sites served first (buildersOwnOnSite()): buildings on site over their points, less progress, as basePricePerPoint() counts each stack. |
| 6945 | 7 | `public double largestDepotPoints()` | The points to build the builders' largest depot: the most construction points among the templates that add to their capacity (addsToTheBuilders()); 0 with none. |
| 6961 | 3 | `public boolean goesFirst(BuildingsTemplate t, long extra)` | Whether t's site, with `extra` more of it ordered, goes first: the rule on, t a template that adds to the builders, the city able to staff what would be on site (buildersCouldStaff()), and that site starved - the crew... |
| 6966 | 18 | `private boolean goesFirst(BuildingsTemplate t, long extra, java.util.Map<String, double[]> projected)` | The same eligibility and starvation test on a quoted queue, before first-unit progress replaces full-site points. |
| 6986 | 10 | `public java.util.List<String> buildersOwnOnSite()` | The builders' own sites served first, in the stacks' order: every stack that adds to their capacity with buildings on site, whoever paid for them, while the city could staff them (goesFirst()). |
| 7003 | 8 | `public java.util.List<String> firstGroup()` | The first group this month, in the order the sites stand: the builders' own sites on site, and while the dead wait the city's death care beside them - or none, when no depot is on site (death care first is then the ci... |
| 7019 | 14 | `static double[] firstOffTheTop(double output, double[] weight, double[] owed, java.util.List<String> keys, java.util.List<Strin...` | The month with the first group served off its top: of the whole output, the group takes what it owes, or all of it if less, shared among its sites by the crews' rule (waterFill(), each by its crew's weight and no more... |
| 7035 | 6 | `public java.util.List<String> siteKeysOnSite()` | Every site with work on it: the stacks with buildings on site, and the demolitions. |
| 7043 | 5 | `public boolean isCitySite(String key)` | Whether a site is the city's own: a stack whose every order on site is the city's, or a demolition. |
| 7050 | 5 | `public BuildingsStacks stackOfKey(String key)` | The stack a site's key names, or null. |
| 7057 | 6 | `public String nameOfSite(String key)` | A site's name: its building's, "Demolishing" before a demolition's. |
| 7065 | 20 | `private void advanceDemolitions(Plan plan)` | D. |
| 7093 | 28 | `private void stopCancelled()` | C. |
| 7132 | 11 | `private void retirePaved(BuildingsStacks stack)` | F. |
| 7145 | 12 | `public void resumeShell(ConstructionControl.Shell shell)` | A shell put back on site (Game.restartShell()): its buildings, the work in them and what they still owe, onto its stack. |

### ONE WAIT FOR A SITE (0.7.22, after the docs pass). Everything that (lines 7158-7632)

| line | len | member | says |
|---:|---:|---|---|
| 7191 | 4 | `private boolean handOn(String key)` | Whether the player's hand times this site: a city site while an order is set, or a rushed one - or, since 0.7.129, any city site while the city's death care is served first (DEATH CARE FIRST WHILE THE DEAD WAIT). |
| 7202 | 12 | `public double siteMonths(String key, double siteOutput)` | Months a site on site would take at today's queue: its points over its weight's share of everything on site, by the rule held steady - or as the player's hand times it (handWait()). |
| 7216 | 3 | `private static double floored(double wait, double floor)` | A wait no sooner than its build time's floor (PW2); NaN and MAX_VALUE pass as they are. |
| 7226 | 17 | `public double siteUnitMonths(BuildingsTemplate t, long units, double siteOutput)` | The quoted opening of the nth unit already on a site. |
| 7252 | 28 | `java.util.List<Object> siteStamp()` | What siteUnitMonths() reads that can be read cheaply, as values a later stamp equals only if none of it has moved (AP1: WorksValue holds the site's completion schedule while the stamp holds): each stack on site, its b... |
| 7282 | 27 | `public double siteUnitMonthsAfter(BuildingsTemplate t, long units, double siteOutput, BuildingsTemplate additional, long quanti...` | Completion of currently pending units after one additional city order, with its full preparation points. |
| 7317 | 3 | `public double cityOrderWait(BuildingsTemplate t, long quantity, double siteOutput)` | Months n buildings of t would take if the city ordered them now: with no order set and no rush on t's site, waitFor() exactly - the quote's wait since 0.7.17; otherwise as the hand times it, the order joining t's site... |
| 7322 | 14 | `public double cityOrderWait(BuildingsTemplate t, long quantity, double extraPoints, double siteOutput)` | ...with `extraPoints` more owed on its site than its buildings' own (ET2: a foothill pad's preparation, SitePreparation); cityOrderWait() exactly for none. |
| 7343 | 8 | `public double restartWait(BuildingsTemplate t, long n, double owedPoints, double siteOutput)` | Months a shell of n buildings owing `owedPoints` would take if it were restarted now (0.7.22), beside whatever of it is on site already: by the rule as waitFor() times an order, or as the hand times it, as cityOrderWa... |
| 7352 | 4 | `private boolean isOnSite(BuildingsTemplate t)` |  |
| 7365 | 13 | `private double[] joined(BuildingsTemplate t, long n, double owed)` | What t's site would be with n more of it owing `owed` more points on it - {its crew's weight, its points owed} - when the player's hand times it: a city site (or none, which the city's order would make one) while an o... |
| 7380 | 1 | `public double pointsOwedOnSite()` | Points owed on every site, demolitions included: everything owed. |
| 7389 | 10 | `public double waitForDemolition(long n, double points, double siteOutput)` | Months a demolition of `points` over n buildings would take if ordered now, at today's queue (0.7.22): its own crew (demolitionWeight()) by the rule held steady - and with the city's order set, behind everything the c... |
| 7406 | 3 | `public double siteMonthsWith(BuildingsTemplate t, long n, double owedPoints, double siteOutput)` | Months n buildings of t owing `owedPoints` would take if put on site now - a shell restarted (0.7.22): beside whatever of it is on site already, as waitFor() times an order. |
| 7410 | 20 | `private double siteMonthsWithRaw(BuildingsTemplate t, long n, double owedPoints, double siteOutput)` |  |
| 7432 | 20 | `private java.util.Map<String, double[]> quotedSites(BuildingsTemplate additional, long quantity, double points)` | Full pending crew weights and points after one quoted additional city order. |
| 7466 | 3 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput)` | THE HAND'S ARITHMETIC: every site on site, its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, and added at the bottom of the city's order when it is a new site of the city's - then th... |
| 7470 | 42 | `private double handWait(String key, double[] mine, boolean isNew, double siteOutput, BuildingsTemplate additional, long quantit...` |  |
| 7514 | 11 | `private double onOvertime(String key, double owed, double months, boolean isNew, java.util.List<String> city)` | A wait worked on overtime when the site is a rushed city site on site: the same pace, at each coming month's factor; the wait itself otherwise. |
| 7527 | 5 | `private boolean firstOn(BuildingsTemplate t, long quantity)` | Whether a wait is the first group's (0.7.130): a depot of the builders' on site served first, or `quantity` more of `t` that would be. |
| 7534 | 10 | `private java.util.Set<String> buildersFirstAfter(java.util.Map<String, double[]> projected, BuildingsTemplate additional, long ...` | Builders promoted by the complete prospective queue, including depots starved only by the additional reserve. |
| 7546 | 6 | `private double[] withMore(BuildingsTemplate t, long n, double owed)` | t's site with n more of it owing `owed` more points on it: {its crew's weight, its points owed}. |
| 7567 | 3 | `private double firstWait(String key, double[] mine, boolean isNew, boolean cityOrder, double siteOutput)` | THE FIRST GROUP'S ARITHMETIC (0.7.130): every site on site with its crew's weight and its points owed - `key`'s replaced by `mine` when it is given, a new one added - and the first group: the builders' own sites, and ... |
| 7571 | 61 | `private double firstWait(String key, double[] mine, boolean isNew, boolean cityOrder, double siteOutput, BuildingsTemplate addi...` |  |

### THE POSTS A SECTOR OFFERS (0.7.17, revised to Jerus's answer): the (lines 7633-8192)

| line | len | member | says |
|---:|---:|---|---|
| 7655 | 3 | `public void setOfferedShare(java.util.function.ToDoubleFunction<String> offeredShare)` | A share for every job type of a sector alike (the builders'). |
| 7660 | 3 | `public void setOfferedShareByType(java.util.function.ToDoubleBiFunction<String, JobType> offeredShare)` | ...and one a job type at a time (FR2: Freight's drivers). |
| 7665 | 4 | `public static long postsOffered(long posts, double share)` | The whole posts offered of a job type's posts at a share: rounded once, the same way everywhere. |
| 7671 | 5 | `public double offeredShareOf(String sector, JobType type)` | A sector's share of its posts of a job type on offer: 1 without the hook. |
| 7683 | 9 | `public long[] getPostsOfferedBySector(String sector)` | The posts a sector offers this month, per job type - the posts of the buildings it owns at its share of each, and the crews it works for the city (the ports', FR3) whole: a lay-off is struck on a sector's own building... |
| 7708 | 5 | `public double clearBankedProgress()` | Clears progress a stack carries past what it owes - all of it on a stack with nothing on site - and says how many points that was (0.7.17). |
| 7714 | 5 | `public double takeRevenueDue()` |  |
| 7723 | 5 | `public java.util.List<BuildingsStacks.Due> takeContractsDue()` |  |
| 7730 | 29 | **type** `public static final class Completion` | One building type and how many of it opened this month. |
| 7742 | 3 | `Completion(String building, long quantity)` _(in BuildingManager.Completion)_ | `quantity` opened, every one loose: a finish decided at the finish, as before R1e. |
| 7746 | 3 | `Completion(String building, long quantity, long loose, java.util.Map<String, Long> without)` _(in BuildingManager.Completion)_ |  |
| 7750 | 8 | `Completion(String building, long quantity, long loose, java.util.Map<String, Long> without, HousingRecipe housingRecipe)` _(in BuildingManager.Completion)_ |  |
| 7760 | 7 | `public void displayAllBuildings()` |  |
| 7770 | 7 | `public long countByName(String name)` | How many finished buildings of this name the city has. |
| 7788 | 7 | `public long underConstructionByName(String name)` | How many of one named building are on site right now. |
| 7796 | 8 | `public BuildingsTemplate getTemplateByName(String name)` |  |
| 7805 | 13 | `public long[] getTotalJobs()` |  |
| 7825 | 9 | `public List<BuildingsStacks> getStacksUnderConstruction()` | The stacks that currently have at least one building in progress, for the construction panel in the UI. |
| 7841 | 13 | `public double getRemainingConstructionPoints()` | Construction points still owed on everything on site. |
| 7870 | 8 | `public double productionUnderConstruction(BuildingType category)` | Production capacity of one category that is ON SITE but not finished. |
| 7887 | 10 | `public int getUnderConstructionByCategory(BuildingType category)` | Stacks of one category with work still on site. |
| 7912 | 8 | `public double getMaterialsInProgress()` | Materials already bought for buildings that are not finished yet. |
| 7932 | 7 | `public long getJobsUnderConstruction()` | Jobs that will exist once everything on site is finished. |
| 7941 | 9 | `public long getHouseCapacityUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 7951 | 10 | `public int getUnderConstruction()` |  |
| 7961 | 3 | `public String getName(BuildingsTemplate selected)` |  |
| 7965 | 5 | `public long getTotalJobs(JobType type)` |  |
| 7979 | 3 | `public void setPostsPast(java.util.function.Supplier<long[]> hook)` |  |
| 7984 | 5 | `public long getPostsPast(JobType type)` | The posts of a job type the sectors run past their buildings' (setPostsPast()): 0 with none. |
| 7991 | 6 | `public long getTotalJobsAtEveryPost(JobType type)` | Every post of a job type the city's buildings have, offered this month or not (0.7.17). |
| 8019 | 13 | `public long getPostsWithheld(JobType type)` | The posts of a job type the sectors are not offering this month (0.7.17): the builders' laid-off crews. |
| 8034 | 8 | `public long getPostsWithheld(String sector, JobType type)` | The posts of a job type one sector does not offer this month: getPostsWithheld(type) for that sector alone (IC1). |
| 8044 | 5 | `public long getPostsWithheld()` | getPostsWithheld() over every job type. |
| 8065 | 3 | `public long getTotalHomes()` | How many separate HOMES the city has, each holding one household. |
| 8076 | 3 | `public long[] homesBySize()` | The city's finished homes, counted by how big a household each one takes. |
| 8081 | 3 | `public long getHomesUnderConstruction()` | Homes that will exist once everything on site is finished. |
| 8086 | 1 | `public long getFoundingEndowments()` | How many founding endowments the city holds: 1 for a founded city, K for a harness's copy K times over (FW9; see the field). |
| 8089 | 1 | `public void setFoundingEndowments(long endowments)` | The load's (and ScaleCheck's) way to hold K endowments: the city's hundred places and its founding care and police, K times over. |
| 8092 | 3 | `public long getTotalHouseCapacity()` | The household places: the founding hundred (per endowment, FW9) and every residential building's. |
| 8103 | 3 | `public long getTotalStoreCoverage()` | Commercial Methods |
| 8108 | 3 | `public long getTotalStoreCapacity()` | Shelf room across the shops, in units. |
| 8156 | 6 | `public long getTotalConstructionCapacity()` | The nameplate of the builders' yards and depots, and nothing else since PT1-M4 (2026-10-23): the municipal works department's free 400 points are retired, and the city founds with a Builders' Yard instead (Game.foundi... |
| 8169 | 3 | `public int getConstructionMaterialsProduction()` | THE YARD'S OWN OUTPUT, and only that, since the sector template. |
| 8185 | 3 | `public long getFoodProduction()` | Kilograms of bakery goods the city's own ovens turn out a month. |
| 8189 | 3 | `public long getFoodCapacity()` |  |

### BY SECTOR (2026-09-11, the sector template) (lines 8193-8734)

| line | len | member | says |
|---:|---:|---|---|
| 8203 | 9 | `public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Finished buildings only: quantity times the getter, over the sector's stacks. |
| 8217 | 7 | `public long standingWhere(java.util.function.Predicate<BuildingsTemplate> which)` | How many finished buildings, in every sector, have a template this accepts (FR7): Freight counts the bulk works a spur could be laid into. |
| 8226 | 9 | `public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter)` | Buildings on site only - what is coming. |
| 8246 | 3 | `public void setNewUnits(java.util.function.Function<BuildingsTemplate, NewUnit> resolve)` | Game hands in how a new building of a template is resolved (NewUnit.of()); null puts back the template as it stands. |
| 8251 | 1 | `public NewUnit newUnit(BuildingsTemplate t)` | One new building of `t` as it would open now (NewUnit.of()); null for none. |
| 8254 | 9 | `public double underConstructionOfNew(String sector, java.util.function.ToDoubleFunction<NewUnit> getter)` | Buildings on site only, each read as the new building it will open as (QN1): a sector's posts, output and input to come. |
| 8265 | 7 | `public long getUnderConstructionBySector(String sector)` | Orders on site for a sector, in buildings. |
| 8278 | 3 | `public double postsOnSite(String sector, JobType job)` | The posts of one tier that a sector's buildings under construction will offer when they open: buildings on site times the jobs of that tier each will open with (BA1; QN1, newUnit()). |
| 8289 | 9 | `public long[] getJobArrayBySector(String sector)` | The posts a sector's finished buildings offer, per tier: the buildings it OPERATES (BuildingsTemplate.getOperator()), which are its own and, for Freight, the city's sea terminals (FR3, the landlord port, docs/queue /F... |
| 8300 | 9 | `public long[] getOwnedJobArrayBySector(String sector)` | ...the posts of the buildings the sector OWNS (the old reading, before the ports' crews moved): its depots' and its track's, not the berths' crews it works for the city (FR3). |
| 8311 | 9 | `public long[] getCrewArrayBySector(String sector)` | ...and the posts it works but does not own, per tier: the crews of the city's berths for Freight (FR3), none for any other sector. |
| 8322 | 9 | `public long[] getCrewArrayByCargo(Ports.Cargo kind)` | The posts of the city's sea terminals that handle one kind of cargo, per tier (FR3): the crews of that kind's berths, for the Ports division's per-kind figures. |
| 8333 | 9 | `public double getLandSqFtBySector(String sector)` | Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. |
| 8347 | 9 | `public double getWorksLandSqFtBySector(String sector, java.util.function.Predicate<BuildingsTemplate> works)` | ...the part of it that works stand on (ET3: `works` is Game.standsAnywhere()), which the books attribute to the steep ground first (EconomyManager.landByReliefOf()); getLandSqFtBySector() less this is its buildings'. |
| 8358 | 10 | `public double getBuildingsValueBySector(String sector)` | Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. |
| 8375 | 9 | `public double getBuildingMaterialsBySector(String sector)` | The construction materials in a sector's buildings, finished and on site, in units (0.7.75, the sector statements' R6): what getBuildingsValueBySector() prices at today's materials, so a move in that price moves the s... |
| 8386 | 11 | `public long getCapacityInPortfolioBySector(String sector)` | People a sector's buildings hold, sites included. |
| 8399 | 5 | `public List<BuildingsTemplate> getTemplatesBySector(String sector)` | Every template a sector may build now: unknown buildings never reach any sector's planner (R2b). |
| 8406 | 5 | `public java.util.Set<String> sectorsNamed()` | Every sector name any template answers to, for the catalogue check. |
| 8423 | 13 | `public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter)` | Universal methods |
| 8437 | 13 | `public long getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter)` |  |
| 8451 | 12 | `public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter)` |  |
| 8477 | 9 | `public long getCapacityInPortfolio(BuildingType category)` | People a category's buildings hold, plus those its SITES will hold. |
| 8494 | 10 | `public double getLandSqFtByCategory(BuildingType category)` | Square feet of lot held by one category, standing and under construction. |
| 8506 | 9 | `public double getWorksLandSqFtByCategory(BuildingType category, java.util.function.Predicate<BuildingsTemplate> works)` | ...the part of a category's land that works stand on (ET3: `works` is Game.standsAnywhere()); getLandSqFtByCategory() less this is its buildings'. |
| 8533 | 16 | `public double getCareCapacity(CareType care)` | How many people the city's finished buildings of one care type have room for. |
| 8570 | 26 | `public double getStaffedCareCapacity(CareType care, double[] jobFillRate)` | The same capacity, discounted by how much of it is actually staffed. |
| 8609 | 24 | `public double[] getStaffedEducationPlaces(double[] jobFillRate)` | School places, discounted by how much of the teaching staff turned up. |
| 8646 | 20 | `public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate)` | Officers or cells, discounted by how much of the staff turned up. |
| 8668 | 10 | `public double getSafetyCapacity(SafetyType safety)` | ...and without the discount: what the buildings would hold, the founding constabulary included (one for each founding endowment, FW9). |
| 8680 | 10 | `public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the police, or just the prisons. |
| 8692 | 9 | `public double getSafetyUpkeep(SafetyType safety)` | ...and their upkeep. |
| 8703 | 10 | `public double[] getBuiltEducationPlaces()` | Places without the staffing discount - what the buildings would seat. |
| 8722 | 12 | `public double getCategoryPayroll(BuildingType category, double[] wagePerType, double[] jobFillRate)` | The healthcare service's wage bill, with the fill rate applied. |

### THE SAME TWO SUMS, NARROWED TO ONE SERVICE. (lines 8735-9151)

| line | len | member | says |
|---:|---:|---|---|
| 8750 | 10 | `public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the buildings providing one kind of care. |
| 8762 | 9 | `public double getCareUpkeep(CareType care)` | ...and what those same buildings cost to keep standing. |
| 8773 | 11 | `public double getSchoolPayroll(EducationType teaches, double[] wagePerType, double[] jobFillRate)` | The wage bill of just the schools teaching one course. |
| 8786 | 9 | `public double getSchoolUpkeep(EducationType teaches)` | ...and their upkeep. |
| 8797 | 12 | `private static double jobBill(BuildingsTemplate t, double[] wagePerType, double[] jobFillRate)` | One building's monthly wage bill, with the fill rate applied per post. |
| 8811 | 3 | `public double getUpkeepByCategory(BuildingType category)` | What the standing buildings of one category cost to run each month. |
| 8827 | 5 | `public double getBookValueByCategory(BuildingType category)` | Gross book value of everything standing in a category: cash paid plus the materials it consumed, valued at market. |
| 8834 | 4 | `public double getBookValueBySector(String sector)` | The same, for the buildings one sector owns - finished ones only. |
| 8846 | 9 | `public double getPortBookValueByCargo(Ports.Cargo kind)` | What the terminals of one kind of cargo standing are worth on the books (FR3, docs/queue/FR3.md 2, port dues): each PORTS building with a berth of this kind at its cash cost plus its materials at market, as getBookVal... |
| 8875 | 10 | `public double getWorkInProgressByCategory(BuildingType category)` | What a category has PAID FOR and not yet got: buildings on site, at the same valuation the finished ones carry. |
| 8887 | 3 | `public double getBuildingsValueByCategory(BuildingType category)` | Finished and unfinished together - what the sector's buildings are worth. |
| 8899 | 10 | `public long[] getJobArrayByName(String name)` | The same array, for one named building rather than a whole category. |
| 8910 | 16 | `public long[] getJobArrayPerCategory(BuildingType category)` |  |
| 8930 | 3 | `public long getConstructionMaterials()` | --------------------------------------------------------------------------- |
| 8934 | 4 | `public int getStackIndex(BuildingsTemplate template)` |  |
| 8939 | 8 | `public BuildingsTemplate getTemplate(int i)` |  |
| 8949 | 8 | `public long getQuantity(int i)` | How many of template id {@code i} are finished and standing. |
| 8958 | 3 | `public int getTemplateCount()` |  |
| 8962 | 9 | `public double[] getConstructionProgress()` |  |
| 8980 | 27 | `public double getTotalLandFootprint()` | Every square foot the city has committed to buildings - standing and on site both, since a half-built plant is occupying its plot. |
| 9014 | 16 | `public double getWorksLandFootprint(java.util.function.Predicate<BuildingsTemplate> works)` | The part of getTotalLandFootprint() that the buildings `works` picks stand on (ET2): the same sum, over the standing and on site, the stopped shells and the demolitions on site, of those templates alone - the ground L... |
| 9049 | 18 | `public long retire(BuildingsTemplate template, long quantity)` | Scraps finished buildings. |
| 9069 | 3 | `public int getStackCount()` | How many stacks exist, for callers checking a save's arrays line up. |
| 9077 | 3 | `public List<BuildingsStacks> getStacksView()` | Every stack, in the order the month shares the crews out over them (R1; EquipmentForms): a read-only view of the live list, so a caller that adds a stack while walking it is walking a changed list. |
| 9086 | 7 | `public java.util.Map<Integer, java.util.Map<String, Long>> getUnfittedById()` | The buildings each stack lacks a piece of equipment on, by template id and then by the piece's id, for the save (R1; BuildingsStacks' unfitted counts): only the templates with any, so a city with none unfitted writes ... |
| 9099 | 13 | `public void restoreUnfitted(java.util.Map<Integer, java.util.Map<String, Long>> byId)` | ...and back onto the stacks the load path has put up (R1): each template's counts, clamped to what stands. |
| 9118 | 8 | `public java.util.Map<Integer, List<BuildingsStacks.Lot>> getLotsById()` | The equipment each site's orders froze, by template id (R1e; BuildingsStacks' lots), for the save: only the sites with a lot, so a city founded the default way writes none and an old save, which has none, reads every ... |
| 9132 | 9 | `public void restoreLots(java.util.Map<Integer, List<BuildingsStacks.Lot>> byId)` | ...and back onto the sites the load path has put up (R1e), after their counts (restoreConstruction()): each lot clamped to what is on site, a piece the template no longer carries dropped (Fitout.without()); a template... |
| 9142 | 9 | `public long[] getUnderConstructionArray()` |  |

### Construction state for the save, keyed by template id. (lines 9152-9503)

| line | len | member | says |
|---:|---:|---|---|
| 9176 | 7 | `public int getMaxTemplateId()` | The highest id in the catalogue, not the number of templates. |
| 9184 | 10 | `public double[] getConstructionProgressById()` |  |
| 9195 | 10 | `public long[] getUnderConstructionById()` |  |
| 9207 | 10 | `public double[] getMaterialsOwedById()` | Material the sites still have to draw, by template id. |
| 9219 | 4 | `public void deliverToSites(BuildingsTemplate template, double units)` | Material delivered to a template's sites from the yard: off what they owe. |
| 9225 | 4 | `public void bookContract(BuildingsTemplate template, double amount)` | The builders' price for an order, on the stack it was placed on. |
| 9231 | 7 | `public void bookContract(BuildingsTemplate template, String payer, double recovered, double amount, double units, double allowa...` | ...and who placed it, with the material units it will draw beyond the yard and the allowance priced in for them (0.7.19), and the share of the tax on it the payer gets back (EconomyManager.taxRecoveredShare()). |
| 9243 | 4 | `public void noteContractPoints(BuildingsTemplate t, String payer, double points)` | The points an order of {@code payer}'s put on site (FC2b), booked after its contract (bookContract()): what a payer's share of a shared site is read by (BuildingsStacks.homePointsShare()). |
| 9249 | 12 | **type** `public static final class ContractRecord` | One payer's contract on one template's sites, as the save carries it (0.7.19). |
| 9263 | 32 | `public java.util.List<ContractRecord> getContractRecords()` | Every payer's contract on site, for the save. |
| 9297 | 10 | `public void restoreContractRecords(java.util.List<ContractRecord> records)` | ...and back onto the stacks the load path has put up. |
| 9316 | 15 | `public void inferContracts(java.util.function.Function<BuildingsTemplate, String> payerOf, java.util.function.ToDoubleBiFunctio...` | Every stack's contract read as one payer's, for a save from before the payers were kept (0.7.19; Game, OLD CONTRACTS): the whole contract left, the material its sites still owe, and an allowance of the contract less t... |
| 9333 | 10 | `public void spreadContracts(double unearned)` | One order book, spread over the sites by the points they still owe. |
| 9345 | 10 | `public double[] getContractValueById()` | The builders' contracts still on site, by template id. |
| 9357 | 5 | `public double getMaterialsOwed()` | ...and in total, for the screens. |
| 9370 | 13 | `public boolean restoreConstruction(int templateId, long underConstruction, double progress, double materialsOwed, double contra...` | Puts a template's in-progress work back onto its stack. |
| 9384 | 9 | `public void setConstructionProgress(double[] progress)` |  |
| 9394 | 9 | `public void setUnderConstructionArray(int[] progress)` |  |
| 9404 | 3 | `public void setConstructionMaterials(long constructionMaterials)` |  |
| 9408 | 4 | `public void clearStacks()` |  |
| 9438 | 3 | `public void handleConstructionMaterials(long required)` | Takes the order's materials out of the yard, importing whatever is short. |
| 9449 | 5 | `public long takeFromYard(long required)` | Takes what the yard has, up to what was asked. |
| 9455 | 9 | `public BuildingsStacks getStack(BuildingsTemplate template)` |  |
| 9467 | 4 | `static { ... }` |  |
| 9472 | 7 | `public void resetBuildingManager()` |  |
| 9482 | 12 | `public void redenominate(double scale)` | Every template's price, and the materials the city is holding, in the new unit. |
| 9497 | 5 | `public void seedConstants(double unit)` | Re-seeds every template's price at a given unit - since 0.7.42 the unit over the expected price level, every month (Game.restrikeMoneyConstants()). |


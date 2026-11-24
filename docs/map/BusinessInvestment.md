# BusinessInvestment.java - 1,840 lines · 108 methods · 13 constants · model

`ham/citybuildersim/BusinessInvestment.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> Capacity planning for the private sector.
> 
> Each month every business looks at the demand it can see, forecasts where
> that demand will be by the time a new building could actually open, and
> expands if the extra capacity would pay for itself. This is the ordinary
> operations question - how much capacity do I need, and when do I have to
> start building it - rather than anything clever.
> 
> THREE PIECES
> 
>   1. DEMAND. Each sector measures a different thing, and getting this right
>      matters more than the forecast does. Real estate looks at JOBS, not
>      population - population is min(housing, jobs x 2.25), so a landlord
>      that watched population would conclude demand had stopped exactly
>      when it was the one causing the shortage. Retail looks at customers
>      against store coverage. A maker looks at what its market wants
>      against what it can make.
> 
>   2. LEAD TIME. A building takes as long as its share of the builders'
>      site output needs to finish it (leadTime(), BuildingManager.waitFor();
>      constructionPoints / cityOutput until 0.7.17), so demand is projected
>      to completion plus a planning horizon.
> 
>   3. THE BRAKE. Businesses here borrow freely, so something has to stop a
>      loss-making expansion spiral: a project must service its own debt -
>      since 0.7.113 the interest on its whole cost, whoever pays (Jerus's
>      decisions A and A13; Game.consider(), ON ITS WHOLE COST), where until
>      then a project the till paid for was asked nothing.
>      That is a business test rather than a credit limit, which is the
>      honest place for it - the lender is willing, the business shouldn't be.
>      Since 0.7.11 a landlord's home is asked the mortgage lender's test
>      instead (Mortgage.decide()); every other order still asks this one.
>      Both read the rate less the inflation the owners expect since 0.7.42
>      and 0.7.44 (THE HURDLE IS REAL, realTestRate()).
> 
> SINCE THE SECTOR TEMPLATE (2026-09-11) this class is the shared
> arithmetic and the two generic rules - the maker's expansion and the two
> ways to shrink. Each sector's own decision is Sector.plan(); the landlords,
> the shops, the builders, the mills and the mines override it, and a
> sector that is a factory does not. The bank's branch has its own planner
> here because the bank is not a sector.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (47), [Sector](Sector.md) (22), [NewUnit](NewUnit.md) (18), [Fitout](Fitout.md) (9), [Good](Good.md) (8), [Game](Game.md) (5), [JobType](JobType.md) (4), [Equipment](Equipment.md) (3), [LandManager](LandManager.md) (3), [GoodsMarket](GoodsMarket.md) (3), [Formats](Formats.md) (3), [FamilyModel](FamilyModel.md) (3), [BuildingManager](BuildingManager.md) (2), [EconomyManager](EconomyManager.md) (2), [Sectors](Sectors.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [ProcessingSector](ProcessingSector.md) (2), [Bank](Bank.md) (2), [Mortgage](Mortgage.md) (1), [LandmassView](LandmassView.md) (1), [Statement](Statement.md) (1), [OldMetal](OldMetal.md) (1), [Currency](Currency.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1), [Oil](Oil.md) (1)

**Used by (102):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildersShort](BuildersShort.md), [BuildingManager](BuildingManager.md), [BuildingStackCheck](BuildingStackCheck.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [Construction](Construction.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [DistressBleedCheck](DistressBleedCheck.md), [ER1Check](ER1Check.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ExtractionSector](ExtractionSector.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [FoodIndustryCheck](FoodIndustryCheck.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [Game](Game.md), [GapOrderCheck](GapOrderCheck.md), [GoldMining](GoldMining.md), [HaulageCheck](HaulageCheck.md), [HousingCheck](HousingCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InvestCheck](InvestCheck.md), [LandmassCheck](LandmassCheck.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MortgageCheck](MortgageCheck.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OrderDoorCheck](OrderDoorCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PolicyCheck](PolicyCheck.md), [PortCheck](PortCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PrizeCheck](PrizeCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [Refining](Refining.md), [Research](Research.md), [ResearchCheck](ResearchCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [SpreadPlanner](SpreadPlanner.md), [StaffingTieCheck](StaffingTieCheck.md), [SulphideLeachCheck](SulphideLeachCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [YoungBankCheck](YoungBankCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 88 | SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) |
| 565 | EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) |
| 687 | RETIREMENT |
| 1003 | THE MAKER'S RULE - the default Sector.plan() |
| 1334 | THE BANK'S BRANCH - not a sector, so its planner lives here |
| 1391 | THE BRAKE |
| 1469 | THE INTEREST ON THE WAIT (BA1, 2026-11-02) |
| 1537 | DEAR CONTRACTS (BA1, 2026-11-04) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `BusinessInvestment.PLANNING_HORIZON` | `6` | The least months of demand growth to build ahead of, on top of the lead time: what a building that opens inside it plans for (horizonFor() grows it with a lead time past it, since BA1). |
| 55 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 58 | `BusinessInvestment.HORIZON_MOST` | `TREND_WINDOW` | The furthest past an order's opening that a sector plans its demand growth: a trend read over TREND_WINDOW months is not projected further than that past the opening (BA1). |
| 83 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 86 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 144 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 463 | `BusinessInvestment.MIN_MONTHS_OF_SITE_WORK` | `TREND_WINDOW / 2` | The months of site work kept before the builders size an order to their median (OG1): half the window. |
| 701 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 704 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 707 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 718 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |
| 727 | `BusinessInvestment.BLEED_MONTHS` | `DISTRESS_LOSS_MONTHS` | The months a price taker takes to sell its biggest holding once its plant has run at an operating loss DISTRESS_LOSS_MONTHS running (its own saved streak, getOperatingLossMonths(), the old metal it sold left out, not ... |
| 1456 | `BusinessInvestment.REAL_HURDLE_FLOOR` | `.25` | The least of the rate a project is tested against, however much inflation its owners expect: a quarter of it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 114 | `private final java.util.Set<String> held` |  |
| 135 | `private boolean branchesHeld` | THE BANK'S BRANCHES, HELD (ST3, 2026-11-09). |
| 146 | `private final BuildingManager buildingManager` |  |
| 147 | `private final EconomyManager economyManager` |  |
| 149 | `private final List<Long> populationHistory` |  |
| 152 | `private final java.util.Map<String, Integer> lossMonths` | Consecutive months each sector has lost money. |
| 155 | `private final java.util.Map<String, Integer> operatingLossMonths` | Consecutive months each sector's plant has run at an operating loss, the old metal it sold left out (AQ1): the streak the bleed reads, apart from lossMonths, which interest alone can keep climbing. |
| 158 | `private double landAvailable` | What the city has left to sell, and what it is charging for it. |
| 159 | `private double landPricePerSqFt` |  |
| 173 | `private double landAvailableForWorks` | ...and what a works that may stand anywhere has (ET2, Game.standsAnywhere(); LandManager.freeForAnyone(true)): the steep ground no building may have too. |
| 185 | `private double landAvailableForRuns` | ...and what a run has (ET4, Game.isRun(); LandManager.freeForAnyone(Use.RUN)): a railway line is laid across every class of ground in the city's proportions, as much of it as the room the buildings leave it. |
| 209 | `private java.util.Map<String, double[]> zoneLand` | THE INVESTORS' GROUND BY ZONE (0.7.107, batch Z1; Jerus's Z3 and Z4). |
| 234 | `public final String sector` |  |
| 235 | `public final BuildingsTemplate template` |  |
| 236 | `public final long quantity` |  |
| 237 | `public final String reason` |  |
| 238 | `public final boolean build` |  |
| 244 | `public final boolean landBlocked` | True when the ONLY thing stopping this was nowhere to put it - the one refusal the player can personally clear, by annexing. |
| 252 | `public final Sector.Staffing heldForStaff` | The staffing test that refused this, when it was a hold for want of workers (Sector.Staffing), with the building and the sector it was asked of; null for every other decision. |
| 268 | `public final Equipment piece` | The piece of equipment this decision FITS to a standing building of `template` instead of building one (R1b; EquipmentFit.choose()); null for every decision that builds or holds. |
| 466 | `private final List<Double> siteWorkOrdered` | The points each recent month ordered of the builders, their own depots left out, oldest first (OG1). |
| 468 | `private double othersOwedAfterAdvance` | What everyone else's sites owed when the last advance ended; NaN before the first (OG1). |
| 560 | `private OrderWatch orderWatch` |  |
| 673 | `private double unlinkedFree` | THE GROUND ACROSS THE WATER (ET5, 2026-10-25). |
| 674 | `private String unlinkedNames` |  |
| 1459 | `private double expectedInflation` | Expected inflation, a fraction a year, as Game last handed it; 0 for the nominal test. |
| 1503 | `private java.util.function.DoubleSupplier orderWaitOutput` | The site output an order's wait is read at (Game.orderWaitOutput()); null in a bare fixture, where no wait is known and every carry is 1. |
| 1584 | `private FamilyModel families` | The household mix, so a residential building can be priced on who would actually live in it. |
| 1589 | `private Bank bank` | The bank, so the advisor can see when credit has got dear. |
| 1601 | `private java.util.function.Function<BuildingsTemplate, NewUnit> newUnits` | A NEW BUILDING'S FIGURES (QN1, 2026-11-19): every estimate, gate and future total here reads one new building of a template as a NewUnit - the recipe, posts, draw, output and input the building would open with - resol... |
| 1669 | `private java.util.function.ToDoubleFunction<NewUnit> licenceCost` |  |
| 1670 | `private java.util.function.ToDoubleFunction<BuildingsTemplate> standingLicenceCost` |  |
| 1751 | `private java.util.function.Function<BuildingsTemplate, Fitout> fitout` | The equipment a new building of a template would come with if ordered now (R1e; Fitout.of(), wired by Game): what the quote prices and the site keeps, so what a business reckons is what it is charged. |
| 1768 | `private java.util.function.ToDoubleBiFunction<BuildingsTemplate, Long> runWorksCost` | What the cuts, viaducts and tunnels of an order of a run would cost the builders' customer before the builders' tax (ET4; Game.runWorksCost(), wired by Game): the Freight sector's investor sees what a rail line costs ... |
| 1803 | `private DeepWaterPrice deepWaterPrice` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 1792 | **type** `public class BusinessInvestment` | Capacity planning for the private sector. |
| 66 | 4 | `public static double horizonFor(double lead)` | The months of demand growth a sector plans past an order's opening, for a building that takes {@code lead} months (BA1): a horizon that grows with how long its buildings take. |
| 76 | 5 | `public static double leadFor(double months)` | The lead whose demand growth is planned over {@code months} (BA1): the inverse of {@code lead + horizonFor(lead)}, which rises strictly with the lead, so a figure that only carries the sum (BuildAdvice.Ahead.months())... |

### SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) (lines 88-564)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 1 | `public void holdSector(String key)` | Harnesses only: this sector will not ask to build for the rest of the run. |
| 120 | 1 | `public void releaseSector(String key)` | Harnesses only (0.7.91, WellCheck): this sector asks to build again. |
| 123 | 1 | `public boolean isHeld(String key)` | Whether a sector has been held out by a fixture. |
| 138 | 1 | `public void holdBranches()` | Harnesses only (ST3): the bank's branches neither open nor close for the rest of the run. |
| 141 | 1 | `public boolean areBranchesHeld()` | Whether a fixture has held the bank's branches (holdBranches()). |
| 161 | 6 | `public void setLandAvailable(double sqFt, double pricePerSqFt)` |  |
| 176 | 3 | `public void setLandAvailableForWorks(double sqFt)` | The ground free for anyone for a works that may stand anywhere, square feet - handed in after setLandAvailable(). |
| 188 | 3 | `public void setLandAvailableForRuns(double sqFt)` | The ground free for anyone for a run, square feet - handed in after setLandAvailable(). |
| 193 | 5 | `private double landAvailableFor(BuildingsTemplate template)` | The ground free for anyone that this building may take: a works's (ET2) or a run's (ET4) where one is handed in, the buildings' otherwise. |
| 212 | 3 | `public void setZoneLand(java.util.Map<String, double[]> zones)` | Each zoned sector's {held square feet, price share} (LandManager.zoneLand()); empty or null for none. |
| 217 | 4 | `public double zoneHeldFor(String sector)` | What a sector's zone holds for it, square feet; 0 with none. |
| 223 | 7 | `private double zonedForOthers(String sector)` | ...and what the other sectors' zones hold: ground this one cannot have. |
| 232 | 74 | **type** `public static class Decision` | What the engine decided, and why - surfaced on the sector screens. |
| 254 | 4 | `public Decision(String sector, BuildingsTemplate template, long quantity, String reason, boolean build)` _(in BusinessInvestment.Decision)_ |  |
| 259 | 4 | `public Decision(String sector, BuildingsTemplate template, long quantity, String reason, boolean build, boolean landBlocked)` _(in BusinessInvestment.Decision)_ |  |
| 270 | 4 | `private Decision(String sector, BuildingsTemplate template, long quantity, String reason, boolean build, boolean landBlocked, S...` _(in BusinessInvestment.Decision)_ |  |
| 275 | 11 | `private Decision(String sector, BuildingsTemplate template, long quantity, String reason, boolean build, boolean landBlocked, S...` _(in BusinessInvestment.Decision)_ |  |
| 288 | 3 | `public static Decision fit(String sector, BuildingsTemplate t, Equipment piece, String reason)` _(in BusinessInvestment.Decision)_ | The month's decision is to fit one building of `t` with `piece` (R1b; quantity 1, and `build` is true: it spends); the reason in the planner's words. |
| 292 | 3 | `public static Decision no(String sector, String reason)` _(in BusinessInvestment.Decision)_ |  |
| 297 | 3 | `public static Decision unstaffed(String sector, Sector.Staffing held, String reason)` _(in BusinessInvestment.Decision)_ | Held back by the staffing test; the reason is the test's own words, and the test rides along (LD1). |
| 302 | 3 | `public static Decision noLand(String sector, String reason)` _(in BusinessInvestment.Decision)_ | Wanted to build, had nowhere to put it. |
| 307 | 4 | `public BusinessInvestment(BuildingManager buildingManager, EconomyManager economyManager)` |  |
| 313 | 6 | `public void recordMonth(long population)` | Call once a month, before the sectors are asked what they want to build. |
| 325 | 6 | `public double getPopulationGrowth()` | Average monthly population change over the window. |
| 341 | 4 | `public double reachablePopulation()` | The most people this city could physically hold once everything on site is finished. |
| 362 | 7 | `public double leadTime(BuildingsTemplate template, long quantity, double siteOutput)` | Months before an order of this building would actually open: the wait it would have at this month's shares of the site output (BuildingManager.waitFor() - everything on site plus the order, by the crew each can use, u... |
| 386 | 4 | `public int orderSize(double shortfall, double capacityPerUnit, BuildingsTemplate template)` | How many of a building to order: enough to close the gap, floored at one and never more plots than the city has land to sell. |
| 422 | 8 | `public static long largestThatPasses(long most, java.util.function.LongPredicate passes)` | The largest n of up to {@code most} for which {@code passes} holds, a test that holds of an order of n only if it holds of every smaller one (the n-th building passes with the n - 1 before it counted); at least one, w... |
| 436 | 4 | `public long gapOrder(Sector sector, BuildingsTemplate t, long wanted)` | An order of {@code wanted} of {@code t} for {@code sector}, the gap its plan measured (OG1, THE ORDER IS THE GAP): no more than the plots the city has for it nor than it could staff together (Sector.staffableCount()),... |
| 471 | 4 | `public double othersOwedOnSite()` | The points owed on every site but the builders' own depots and yards (OG1): the queue less pointsOwedBySector(CONSTRUCTION). |
| 477 | 5 | `public void beforeSitesAdvance()` | Call once a month as the sites' advance begins (SimulationEngine): the month's orders join the record. |
| 484 | 3 | `public void afterSitesAdvance()` | ...and once as it ends: what the sites still owe is where the next month's orders are counted from. |
| 492 | 6 | `public double typicalMonthOfSiteWork()` | The work a typical month orders of the builders (OG1): the median of the months kept, so one month's spike - a playtest move of 18 million points - is worked off rather than built for; NaN under MIN_MONTHS_OF_SITE_WORK. |
| 499 | 1 | `public List<Double> getSiteWorkOrdered()` |  |
| 502 | 1 | `public Double getOthersOwedAfterAdvance()` | The point the next month's orders count from, null before the first advance (a save's form: a JSON number is never NaN). |
| 505 | 6 | `public void restoreSiteWork(List<Double> ordered, Double owedAfterAdvance)` | A save's record (DataSave.buildersWork); a save from before OG1 has none and starts it again. |
| 552 | 7 | **type** `public interface OrderWatch` | HARNESSES ONLY (0.7.54): told every order the searches decide - Game.consider() and Mortgage.decide() through Game.watchOrders() (and orderSize() here, until BA1 took the wait out of it) - with what each was asked, so... |
| 554 | 1 | `default void invested(Decision decision, double cash, double perUnitProfit, Game.Afford found)` _(in BusinessInvestment.OrderWatch)_ | Game.consider(): the order, the till it was judged on, one building's profit, and what was found. |
| 556 | 2 | `default void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash, double noiPerUnit, double annua...` _(in BusinessInvestment.OrderWatch)_ | Game.considerOnMortgage(): what Mortgage.decide() was asked, and its answer. |
| 563 | 1 | `public void watchOrders(OrderWatch watch)` | Harnesses only: see OrderWatch; Game.watchOrders() sets it (and nothing here reads it since BA1 took the wait out of orderSize(): Game tells its own). |

### EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) (lines 565-686)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 4 | `public double monthsOfWorkOnSite(String sector, double siteOutput)` | Months of the builders' site output a sector's sites owe (0.7.17): the points owed over the output. |
| 622 | 7 | `public int ordersOnSite(String sector)` | How many orders a sector holds on site (BA1): its stacks with buildings under construction. |
| 637 | 10 | `public double monthsAheadOf(String sector, double siteOutput)` | How far ahead a sector is building (BA1): the longest wait any of its stacks on site has at this month's shares of the site output (BuildingManager.waitOnSite(), the build card's "N on site"), in months. |
| 649 | 7 | `public int plotsAvailableFor(BuildingsTemplate template)` | How many of these the city currently has room for. |
| 658 | 8 | `public String landReason(BuildingsTemplate template)` | Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one: "no land - needs 743 m\u00b2, 301 m\u00b2 free" (in square feet until 0.7.68; LandManager.areaWords()... |
| 677 | 4 | `public void setUnlinkedLand(double sqFt, String names)` | The unbuilt dry ground the city owns on landmasses it has not linked, square feet, and their names for a sentence (LandManager.getUnlinkedFreeSqFt(), getUnlinkedNames()); 0 and empty for none. |
| 683 | 3 | `public static String zonedForOthersWords(double sqFt)` | What the other sectors' zones hold, as a land refusal names it (0.7.107's words, shared since 0.7.109 with the build card's own land gate and the "Could not build" word): "55.9 km² is zoned for others". |

### RETIREMENT (lines 687-1002)

| line | len | member | says |
|---:|---:|---|---|
| 730 | 7 | `public void recordSectorResult(String sector, double netIncome)` | Call once a month with each sector's net income. |
| 741 | 10 | `public void recordOperatingResult(Sector sector)` | Call once a month with each sector, after its statement is closed: adds a month to its operating-loss streak when its running result (operating income less the old metal it sold to the Scrap Yards, the sale of a scrap... |
| 764 | 6 | `public void noteOpened(String sector)` | A sector that has just opened something starts its count again. |
| 778 | 3 | `public java.util.Map<String, Integer> getLossMonthsState()` | THE TWO HISTORIES, CARRIED. |
| 782 | 4 | `public void restoreLossMonths(java.util.Map<String, Integer> saved)` |  |
| 788 | 3 | `public java.util.Map<String, Integer> getOperatingLossMonthsState()` | The operating-loss streaks as saved, keyed by sector name (AQ1). |
| 793 | 4 | `public void restoreOperatingLossMonths(java.util.Map<String, Integer> saved)` | Puts the saved operating-loss streaks back; a save written before AQ1 has none and the streak starts again at 0 (AQ1). |
| 798 | 3 | `public java.util.List<Long> getPopulationHistory()` |  |
| 802 | 6 | `public void restorePopulationHistory(java.util.List<Long> saved)` |  |
| 809 | 3 | `public int getLossMonths(String sector)` |  |
| 814 | 3 | `public int getOperatingLossMonths(String sector)` | The months in a row a sector's plant has run at an operating loss, the old metal it sold left out (AQ1). |
| 827 | 67 | `public Decision planRetirement(Sector sector, double demand, double capacity, long ordersInFlight)` | Whether a sector should sell capacity, and how much. |
| 914 | 4 | `private static boolean makesWhatItSells(Sector sector, BuildingsTemplate template)` | WHAT A SECTOR IN DISTRESS MAY SELL IS ANYTHING IT MAKES WITH (0.7.12 round 7). |
| 935 | 67 | `public Decision planDistressRetirement(Sector sector, double cash, long ordersInFlight)` | Whether a sector that cannot pay its way and cannot borrow should shed capacity anyway. |

### THE MAKER'S RULE - the default Sector.plan() (lines 1003-1333)

| line | len | member | says |
|---:|---:|---|---|
| 1011 | 171 | `public Decision planMaker(Sector sector, Game game)` |  |
| 1194 | 5 | `public double forecast(Sector sector, GoodsMarket market)` | The demand a maker plans against: the smaller of the trend and this month (high AND been high - see planMaker), and no more than the sector can see on its customers' books over the months the trend looks back (Good.pl... |
| 1208 | 5 | `public double growthFactor(double months)` | THE CITY'S FUTURE, READ ONE WAY (0.7.51): how much bigger demand will be in `months`, by the population trend over the window - never below today, and never past where the people could live (the cap above planMaker()'... |
| 1215 | 4 | `private double growthShare()` | The population trend as a share a month, for projecting a good's demand forward. |
| 1226 | 3 | `private static double knownCost(double raw)` | A break-even that a sector with nothing running cannot state. |
| 1249 | 3 | `public double estimatedMakerProfit(Sector sector, BuildingsTemplate t)` | What one of a maker's templates would clear a month: every good it makes, at the price it would actually get for it, less the inputs it uses at theirs, at the rate the sector's plants actually run, less what the build... |
| 1254 | 3 | `public double estimatedMakerProfit(Sector sector, NewUnit t)` | ...for one new building already resolved (QN1): what it makes, uses and costs to run. |
| 1264 | 3 | `public double estimatedMakerProfit(Sector sector, BuildingsTemplate t, java.util.Map<Good, Double> made)` | ...with what the building makes given apart from its template (0.7.76): a crude unit's slate (sectors.Refining.madeBy()), whose template says only the crude it takes. |
| 1269 | 34 | `public double estimatedMakerProfit(Sector sector, NewUnit t, java.util.Map<Good, Double> made)` | ...for one new building already resolved (QN1). |
| 1311 | 22 | `public double standingCostOf(Sector sector, BuildingsTemplate t)` | What a building costs its owner just for standing: the repairs and the property tax, at today's prices and the sector's own rate. |

### THE BANK'S BRANCH - not a sector, so its planner lives here (lines 1334-1390)

| line | len | member | says |
|---:|---:|---|---|
| 1358 | 32 | `public Decision planBank()` | Whether to open another bank branch. |

### THE BRAKE (lines 1391-1468)

| line | len | member | says |
|---:|---:|---|---|
| 1410 | 4 | `public boolean servicesItsOwnDebt(double estimatedMonthlyProfit, double amountBorrowed, double annualRate)` | Whether a project can carry the debt it needs: if the new capacity cannot out-earn the interest on the money that built it, by a margin, the business declines the project even though the lender would fund it. |
| 1423 | 5 | `public double interestHurdle(double amountBorrowed, double annualRate)` | The monthly profit servicesItsOwnDebt() asks for on {@code amountBorrowed} at {@code annualRate}: the interest at the real rate, times PROFIT_OVER_INTEREST; minus infinity when nothing is borrowed (anything carries it). |
| 1451 | 3 | `public double realTestRate(double annualRate)` | The rate a project is tested at: this one less the inflation its owners expect, never under REAL_HURDLE_FLOOR of it. |
| 1462 | 3 | `public void setExpectedInflation(double expected)` | Told each month by Game (Expectations.getExpectedInflation() once the basket is based, 0 before). |
| 1467 | 1 | `public double getExpectedInflation()` | What servicesItsOwnDebt() takes off the rate, a fraction a year. |

### THE INTEREST ON THE WAIT (BA1, 2026-11-02) (lines 1469-1536)

| line | len | member | says |
|---:|---:|---|---|
| 1506 | 1 | `public void setOrderWaitOutput(java.util.function.DoubleSupplier output)` | Hands in the site output an order's wait is read at (Game.orderWaitOutput()); null clears it, and every carry is exactly 1 again. |
| 1514 | 7 | `public double waitCarry(double annualRate, double months)` | What a cost grows to over a wait of `months` at the real test rate of `annualRate`: (1 + i)^months for i the monthly real rate (realTestRate()). |
| 1531 | 5 | `public double carryOf(BuildingsTemplate t, long n, double annualRate)` | The carry of an order of `n` of `t` (waitCarry() over leadTime(): the whole wait, the months until its last building opens, at this month's site output as Game hands it in), times dearOf() while contracts are dear (DE... |

### DEAR CONTRACTS (BA1, 2026-11-04) (lines 1537-1840)

| line | len | member | says |
|---:|---:|---|---|
| 1577 | 5 | `public double dearOf(BuildingsTemplate t, long n)` | What a money test grows an order's cost by while contracts are dear (DEAR CONTRACTS): the order's own multiple on the builders' price (BuildingManager.orderPriceMultiplier() for `n` of `t`'s points), counted once more. |
| 1585 | 1 | `public void setFamilies(FamilyModel families)` |  |
| 1586 | 1 | `public FamilyModel families()` |  |
| 1590 | 5 | `public void setBank(Bank bank)` |  |
| 1604 | 3 | `public void setNewUnits(java.util.function.Function<BuildingsTemplate, NewUnit> resolve)` | Game hands in how one new building of a template is resolved (NewUnit.of()); null puts back the template as it stands. |
| 1609 | 1 | `public NewUnit newUnit(BuildingsTemplate t)` | One new building of `t` as it would open now (NewUnit.of()); null for none. |
| 1616 | 4 | `double newBranchExtra(NewUnit branch)` | What a new branch's own crew costs over a later branch's cached cost (QN1; Bank.newBranchCost()): the proposed branch's wage bill less the bill of the posts the standing branches have, 0 where the proposal is the temp... |
| 1622 | 3 | `public double wageBillFor(BuildingsTemplate t)` | What one of these would cost to staff, at what the city pays today. |
| 1627 | 9 | `public double wageBillFor(NewUnit t)` | ...for one new building already resolved (QN1). |
| 1644 | 15 | `public double estimatedMonthlyProfit(String sector, BuildingsTemplate t)` | Rough monthly profit a finished building would add - the screening number the interest test is struck on. |
| 1665 | 3 | `public static double operatingRateOf(double rate)` | A sector's operating rate as a planning figure: what it is, unless the sector has nothing running yet, in which case a plant that does not exist runs at nameplate on paper. |
| 1673 | 3 | `public void setLicenceCost(java.util.function.ToDoubleFunction<NewUnit> cost)` | Game supplies patent rent; a bare planner reads zero. |
| 1678 | 3 | `public void setStandingLicenceCost(java.util.function.ToDoubleFunction<BuildingsTemplate> cost)` | The corresponding royalty for an installed building's standing valuation. |
| 1687 | 3 | `public double runningCostOf(BuildingsTemplate t)` | Wages, power, water and patent rent for a building that does not exist yet, read off the template and the current schedule rather than off a sector's income statement, because the first mine in a city has no sector to... |
| 1692 | 3 | `public double runningCostOf(NewUnit t)` | ...for one new building already resolved (QN1): its own posts, draw and water, and the rent LicenceFees.forNew() strikes on it. |
| 1697 | 3 | `public double standingRunningCostOf(BuildingsTemplate t)` | The same operating costs with the installed entry's standing royalty (R2c). |
| 1701 | 12 | `private double runningCostOf(NewUnit t, double fee)` |  |
| 1727 | 19 | `private double totalCostOf(BuildingsTemplate t, long quantity, double deepFactor)` | Cash price of a building, matching what Game charges: the builders' price for the work - its labour at today's wages (0.7.19) - plus any materials that have to be bought beyond the city's yard, at the market price, wi... |
| 1754 | 3 | `public void setFitout(java.util.function.Function<BuildingsTemplate, Fitout> fit)` | Hands in the fitout of a new building (Game: Fitout.of()); null clears it. |
| 1758 | 4 | `private Fitout fitoutOf(BuildingsTemplate t)` |  |
| 1771 | 3 | `public void setRunWorksCost(java.util.function.ToDoubleBiFunction<BuildingsTemplate, Long> price)` | Hands in the price of a run's works on the city's slopes (Game.runWorksCost()); null clears it. |
| 1781 | 7 | `private double groundCostOf(BuildingsTemplate t, long quantity)` | The ground under an order, as Game.buildFor() charges it: the part its sector's zone holds at the zone's price, the rest at the going price (0.7.107, LandManager.priceFor(sqFt, sector)); with no zone, the going price. |
| 1789 | 3 | `public double getCostOf(BuildingsTemplate t, long quantity)` |  |
| 1799 | 3 | **type** `public interface DeepWaterPrice` | What the deep-water works of an order of `quantity` of a platform or its well cost the builders' customer before the builders' tax at `factor` times a jacket's price (DW1; Game.deepWater(), wired by Game): the quote's... |
| 1800 | 1 | `double moneyBeforeTax(BuildingsTemplate t, long quantity, double factor)` _(in BusinessInvestment.DeepWaterPrice)_ |  |
| 1806 | 3 | `public void setDeepWaterPrice(DeepWaterPrice price)` | Hands in the price of a floating platform's deep-water works (Game.deepWater()); null clears it. |
| 1811 | 6 | `private double deepWaterFactorOf(BuildingsTemplate t)` | How many times a jacket's price the next unit of an order of this costs at sea (DW1; sectors.Oil.deepWaterFactor()): 1 for every template but an Offshore Platform and its well, and for those in a sea a jacket stands in. |
| 1823 | 3 | `public double getCostOfAt(BuildingsTemplate t, long quantity, double deepFactor)` | getCostOf() with the deep-water works priced at `deepFactor` times a jacket's price rather than at the depth of the next unit (DW1): the Oil planner judges a platform and its wells together, on the field it would stan... |
| 1834 | 6 | `public int yardCovers(BuildingsTemplate t)` | How many of this building the city's yard of construction materials covers before getCostOf() buys the rest at the market - where its cost turns steeper; Integer.MAX_VALUE for a building that needs none. |


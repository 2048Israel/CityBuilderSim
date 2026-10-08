# BusinessInvestment.java - 1,145 lines · 53 methods · 12 constants · model

`ham/citybuildersim/BusinessInvestment.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
>      loss-making expansion spiral: a project must service its own debt.
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

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (25), [Sector](Sector.md) (11), [Good](Good.md) (6), [JobType](JobType.md) (4), [GoodsMarket](GoodsMarket.md) (3), [Formats](Formats.md) (3), [FamilyModel](FamilyModel.md) (3), [BuildingManager](BuildingManager.md) (2), [EconomyManager](EconomyManager.md) (2), [Game](Game.md) (2), [LandManager](LandManager.md) (2), [Bank](Bank.md) (2), [Mortgage](Mortgage.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (37):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionScreen](ConstructionScreen.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Mining](Mining.md), [MortgageCheck](MortgageCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PolicyCheck](PolicyCheck.md), [Rail](Rail.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 63 | SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) |
| 385 | RETIREMENT |
| 615 | THE MAKER'S RULE - the default Sector.plan() |
| 925 | THE BANK'S BRANCH - not a sector, so its planner lives here |
| 981 | THE BRAKE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 52 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 55 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 58 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 61 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site - every sector but the landlords, who hold work by the month (0.7.17; withinMonthsOfWork()). |
| 105 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, in months of the builders' work. |
| 108 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 399 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 402 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 405 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 416 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |
| 1031 | `BusinessInvestment.REAL_HURDLE_FLOOR` | `.25` | The least of the rate a project is tested against, however much inflation its owners expect: a quarter of it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `private final java.util.Set<String> held` |  |
| 110 | `private final BuildingManager buildingManager` |  |
| 111 | `private final EconomyManager economyManager` |  |
| 113 | `private final List<Long> populationHistory` |  |
| 116 | `private final java.util.Map<String, Integer> lossMonths` | Consecutive months each sector has lost money. |
| 119 | `private double landAvailable` | What the city has left to sell, and what it is charging for it. |
| 120 | `private double landPricePerSqFt` |  |
| 130 | `public final String sector` |  |
| 131 | `public final BuildingsTemplate template` |  |
| 132 | `public final int quantity` |  |
| 133 | `public final String reason` |  |
| 134 | `public final boolean build` |  |
| 140 | `public final boolean landBlocked` | True when the ONLY thing stopping this was nowhere to put it - the one refusal the player can personally clear, by annexing. |
| 317 | `private OrderWatch orderWatch` |  |
| 1034 | `private double expectedInflation` | Expected inflation, a fraction a year, as Game last handed it; 0 for the nominal test. |
| 1045 | `private FamilyModel families` | The household mix, so a residential building can be priced on who would actually live in it. |
| 1050 | `private Bank bank` | The bank, so the advisor can see when credit has got dear. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 1100 | **type** `public class BusinessInvestment` | Capacity planning for the private sector. |

### SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) (lines 63-384)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 1 | `public void holdSector(String key)` | Harnesses only: this sector will not ask to build for the rest of the run. |
| 93 | 1 | `public boolean isHeld(String key)` | Whether a sector has been held out by a fixture. |
| 122 | 4 | `public void setLandAvailable(double sqFt, double pricePerSqFt)` |  |
| 128 | 38 | **type** `public static class Decision` | What the engine decided, and why - surfaced on the sector screens. |
| 142 | 4 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build)` _(in BusinessInvestment.Decision)_ |  |
| 147 | 9 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build, boolean landBlocked)` _(in BusinessInvestment.Decision)_ |  |
| 157 | 3 | `public static Decision no(String sector, String reason)` _(in BusinessInvestment.Decision)_ |  |
| 162 | 3 | `public static Decision noLand(String sector, String reason)` _(in BusinessInvestment.Decision)_ | Wanted to build, had nowhere to put it. |
| 167 | 4 | `public BusinessInvestment(BuildingManager buildingManager, EconomyManager economyManager)` |  |
| 173 | 6 | `public void recordMonth(long population)` | Call once a month, before the sectors are asked what they want to build. |
| 185 | 6 | `public double getPopulationGrowth()` | Average monthly population change over the window. |
| 201 | 4 | `public double reachablePopulation()` | The most people this city could physically hold once everything on site is finished. |
| 222 | 4 | `public double leadTime(BuildingsTemplate template, int quantity, double siteOutput)` | Months before an order of this building would actually open: the wait it would have at this month's shares of the site output (BuildingManager.waitFor() - everything on site plus the order, by the crew each can use, u... |
| 241 | 30 | `public int orderSize(double shortfall, double capacityPerUnit, BuildingsTemplate template, double siteOutput)` | How many of a building to order: enough to close the gap, but no more than would open inside MAX_ORDER_MONTHS at the wait leadTime() reads (0.7.17: it was twelve months of the builders' whole output, as though the ord... |
| 307 | 9 | **type** `public interface OrderWatch` | HARNESSES ONLY (0.7.54): told every order the three searches decide - orderSize() here, and Game.consider() and Mortgage.decide() through Game.watchOrders() - with what each was asked, so a harness can ask the countdo... |
| 309 | 1 | `default void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead)` _(in BusinessInvestment.OrderWatch)_ | orderSize(): what it needed, at what site output, the order inside MAX_ORDER_MONTHS (0 for none), and the waits it read. |
| 311 | 1 | `default void invested(Decision decision, double cash, double perUnitProfit, Game.Afford found)` _(in BusinessInvestment.OrderWatch)_ | Game.consider(): the order, the till it was judged on, one building's profit, and what was found. |
| 313 | 2 | `default void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualR...` _(in BusinessInvestment.OrderWatch)_ | Game.considerOnMortgage(): what Mortgage.decide() was asked, and its answer. |
| 320 | 1 | `public void watchOrders(OrderWatch watch)` | Harnesses only: see OrderWatch; Game.watchOrders() sets it. |
| 355 | 7 | `public int withinMonthsOfWork(String sector, BuildingsTemplate t, int wanted, double siteOutput)` | The largest order of up to {@code wanted} buildings a sector may add to its sites and still owe no more than MAX_ORDER_MONTHS of the builders' site output, the order counted; 0 when not even one fits. |
| 367 | 4 | `public double monthsOfWorkOnSite(String sector, double siteOutput)` | Months of the builders' site output a sector's sites owe (0.7.17): the points owed over the output. |
| 373 | 5 | `public int plotsAvailableFor(BuildingsTemplate template)` | How many of these the city currently has room for. |
| 380 | 4 | `public String landReason(BuildingsTemplate template)` | Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one: "no land - needs 743 m\u00b2, 301 m\u00b2 free" (in square feet until 0.7.68; LandManager.areaWords()). |

### RETIREMENT (lines 385-614)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 7 | `public void recordSectorResult(String sector, double netIncome)` | Call once a month with each sector's net income. |
| 439 | 3 | `public void noteOpened(String sector)` | A sector that has just opened something starts its count again. |
| 450 | 3 | `public java.util.Map<String, Integer> getLossMonthsState()` | THE TWO HISTORIES, CARRIED. |
| 454 | 4 | `public void restoreLossMonths(java.util.Map<String, Integer> saved)` |  |
| 459 | 3 | `public java.util.List<Long> getPopulationHistory()` |  |
| 463 | 6 | `public void restorePopulationHistory(java.util.List<Long> saved)` |  |
| 470 | 3 | `public int getLossMonths(String sector)` |  |
| 483 | 59 | `public Decision planRetirement(Sector sector, double demand, double capacity, int ordersInFlight)` | Whether a sector should sell capacity, and how much. |
| 562 | 4 | `private static boolean makesWhatItSells(Sector sector, BuildingsTemplate template)` | WHAT A SECTOR IN DISTRESS MAY SELL IS ANYTHING IT MAKES WITH (0.7.12 round 7). |
| 579 | 35 | `public Decision planDistressRetirement(Sector sector, double cash, int ordersInFlight)` | Whether a sector that cannot pay its way and cannot borrow should shed capacity anyway. |

### THE MAKER'S RULE - the default Sector.plan() (lines 615-924)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 171 | `public Decision planMaker(Sector sector, Game game)` |  |
| 806 | 5 | `public double forecast(Sector sector, GoodsMarket market)` | The demand a maker plans against: the smaller of the trend and this month (high AND been high - see planMaker), and no more than the sector can see on its customers' books over the months the trend looks back (Good.pl... |
| 820 | 5 | `public double growthFactor(double months)` | THE CITY'S FUTURE, READ ONE WAY (0.7.51): how much bigger demand will be in `months`, by the population trend over the window - never below today, and never past where the people could live (the cap above planMaker()'... |
| 827 | 4 | `private double growthShare()` | The population trend as a share a month, for projecting a good's demand forward. |
| 838 | 3 | `private static double knownCost(double raw)` | A break-even that a sector with nothing running cannot state. |
| 861 | 34 | `public double estimatedMakerProfit(Sector sector, BuildingsTemplate t)` | What one of a maker's templates would clear a month: every good it makes, at the price it would actually get for it, less the inputs it uses at theirs, at the rate the sector's plants actually run, less what the build... |
| 903 | 21 | `public double standingCostOf(Sector sector, BuildingsTemplate t)` | What a building costs its owner just for standing: the repairs and the property tax, at today's prices and the sector's own rate. |

### THE BANK'S BRANCH - not a sector, so its planner lives here (lines 925-980)

| line | len | member | says |
|---:|---:|---|---|
| 949 | 31 | `public Decision planBank()` | Whether to open another bank branch. |

### THE BRAKE (lines 981-1145)

| line | len | member | says |
|---:|---:|---|---|
| 995 | 8 | `public boolean servicesItsOwnDebt(double estimatedMonthlyProfit, double amountBorrowed, double annualRate)` | Whether a project can carry the debt it needs: if the new capacity cannot out-earn the interest on the money that built it, by a margin, the business declines the project even though the lender would fund it. |
| 1026 | 3 | `public double realTestRate(double annualRate)` | The rate a project is tested at: this one less the inflation its owners expect, never under REAL_HURDLE_FLOOR of it. |
| 1037 | 3 | `public void setExpectedInflation(double expected)` | Told each month by Game (Expectations.getExpectedInflation() once the basket is based, 0 before). |
| 1042 | 1 | `public double getExpectedInflation()` | What servicesItsOwnDebt() takes off the rate, a fraction a year. |
| 1046 | 1 | `public void setFamilies(FamilyModel families)` |  |
| 1047 | 1 | `public FamilyModel families()` |  |
| 1051 | 1 | `public void setBank(Bank bank)` |  |
| 1054 | 9 | `public double wageBillFor(BuildingsTemplate t)` | What one of these would cost to staff, at what the city pays today. |
| 1071 | 13 | `public double estimatedMonthlyProfit(String sector, BuildingsTemplate t)` | Rough monthly profit a finished building would add - the screening number the interest test is struck on. |
| 1090 | 3 | `public static double operatingRateOf(double rate)` | A sector's operating rate as a planning figure: what it is, unless the sector has nothing running yet, in which case a plant that does not exist runs at nameplate on paper. |
| 1099 | 11 | `public double runningCostOf(BuildingsTemplate t)` | Wages, power and water for a building that does not exist yet, read off the template and the current schedule rather than off a sector's income statement, because the first mine in a city has no sector to read. |
| 1119 | 8 | `private double totalCostOf(BuildingsTemplate t, int quantity)` | Cash price of a building, matching what Game charges: the builders' price for the work - its labour at today's wages (0.7.19) - plus any materials that have to be bought beyond the city's yard, at the market price, wi... |
| 1128 | 3 | `public double getCostOf(BuildingsTemplate t, int quantity)` |  |
| 1139 | 6 | `public int yardCovers(BuildingsTemplate t)` | How many of this building the city's yard of construction materials covers before getCostOf() buys the rest at the market - where its cost turns steeper; Integer.MAX_VALUE for a building that needs none. |


# BusinessInvestment.java - 1,008 lines · 44 methods · 11 constants · model

`ham/citybuildersim/BusinessInvestment.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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
> 
> SINCE THE SECTOR TEMPLATE (2026-09-11) this class is the shared
> arithmetic and the two generic rules - the maker's expansion and the two
> ways to shrink. Each sector's own decision is Sector.plan(); the landlords,
> the shops, the builders, the mills and the mines override it, and a
> sector that is a factory does not. The bank's branch has its own planner
> here because the bank is not a sector.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (23), [Sector](Sector.md) (11), [Good](Good.md) (6), [JobType](JobType.md) (4), [GoodsMarket](GoodsMarket.md) (3), [Formats](Formats.md) (3), [FamilyModel](FamilyModel.md) (3), [BuildingManager](BuildingManager.md) (2), [EconomyManager](EconomyManager.md) (2), [Bank](Bank.md) (2), [Game](Game.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (27):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionScreen](ConstructionScreen.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [InvestCheck](InvestCheck.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Mining](Mining.md), [MortgageCheck](MortgageCheck.md), [PolicyCheck](PolicyCheck.md), [Rail](Rail.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 61 | SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) |
| 318 | RETIREMENT |
| 548 | THE MAKER'S RULE - the default Sector.plan() |
| 846 | THE BANK'S BRANCH - not a sector, so its planner lives here |
| 902 | THE BRAKE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 50 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 53 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 56 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 59 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site - every sector but the landlords, who hold work by the month (0.7.17; withinMonthsOfWork()). |
| 103 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, in months of the builders' work. |
| 106 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 332 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 335 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 338 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 349 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `private final java.util.Set<String> held` |  |
| 108 | `private final BuildingManager buildingManager` |  |
| 109 | `private final EconomyManager economyManager` |  |
| 111 | `private final List<Integer> populationHistory` |  |
| 114 | `private final java.util.Map<String, Integer> lossMonths` | Consecutive months each sector has lost money. |
| 117 | `private double landAvailable` | What the city has left to sell, and what it is charging for it. |
| 118 | `private double landPricePerSqFt` |  |
| 128 | `public final String sector` |  |
| 129 | `public final BuildingsTemplate template` |  |
| 130 | `public final int quantity` |  |
| 131 | `public final String reason` |  |
| 132 | `public final boolean build` |  |
| 138 | `public final boolean landBlocked` | True when the ONLY thing stopping this was nowhere to put it - the one refusal the player can personally clear, by annexing. |
| 922 | `private FamilyModel families` | The household mix, so a residential building can be priced on who would actually live in it. |
| 927 | `private Bank bank` | The bank, so the advisor can see when credit has got dear. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 965 | **type** `public class BusinessInvestment` | Capacity planning for the private sector. |

### SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) (lines 61-317)

| line | len | member | says |
|---:|---:|---|---|
| 88 | 1 | `public void holdSector(String key)` | Harnesses only: this sector will not ask to build for the rest of the run. |
| 91 | 1 | `public boolean isHeld(String key)` | Whether a sector has been held out by a fixture. |
| 120 | 4 | `public void setLandAvailable(double sqFt, double pricePerSqFt)` |  |
| 126 | 38 | **type** `public static class Decision` | What the engine decided, and why - surfaced on the sector screens. |
| 140 | 4 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build)` _(in BusinessInvestment.Decision)_ |  |
| 145 | 9 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build, boolean landBlocked)` _(in BusinessInvestment.Decision)_ |  |
| 155 | 3 | `public static Decision no(String sector, String reason)` _(in BusinessInvestment.Decision)_ |  |
| 160 | 3 | `public static Decision noLand(String sector, String reason)` _(in BusinessInvestment.Decision)_ | Wanted to build, had nowhere to put it. |
| 165 | 4 | `public BusinessInvestment(BuildingManager buildingManager, EconomyManager economyManager)` |  |
| 171 | 6 | `public void recordMonth(int population)` | Call once a month, before the sectors are asked what they want to build. |
| 183 | 6 | `public double getPopulationGrowth()` | Average monthly population change over the window. |
| 199 | 4 | `public double reachablePopulation()` | The most people this city could physically hold once everything on site is finished. |
| 220 | 4 | `public double leadTime(BuildingsTemplate template, int quantity, double siteOutput)` | Months before an order of this building would actually open: the wait it would have at this month's shares of the site output (BuildingManager.waitFor() - everything on site plus the order, by the crew each can use, u... |
| 234 | 20 | `public int orderSize(double shortfall, double capacityPerUnit, BuildingsTemplate template, double siteOutput)` | How many of a building to order: enough to close the gap, but no more than would open inside MAX_ORDER_MONTHS at the wait leadTime() reads (0.7.17: it was twelve months of the builders' whole output, as though the ord... |
| 288 | 7 | `public int withinMonthsOfWork(String sector, BuildingsTemplate t, int wanted, double siteOutput)` | The largest order of up to {@code wanted} buildings a sector may add to its sites and still owe no more than MAX_ORDER_MONTHS of the builders' site output, the order counted; 0 when not even one fits. |
| 300 | 4 | `public double monthsOfWorkOnSite(String sector, double siteOutput)` | Months of the builders' site output a sector's sites owe (0.7.17): the points owed over the output. |
| 306 | 5 | `public int plotsAvailableFor(BuildingsTemplate template)` | How many of these the city currently has room for. |
| 313 | 4 | `public String landReason(BuildingsTemplate template)` | Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one. |

### RETIREMENT (lines 318-547)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 7 | `public void recordSectorResult(String sector, double netIncome)` | Call once a month with each sector's net income. |
| 372 | 3 | `public void noteOpened(String sector)` | A sector that has just opened something starts its count again. |
| 383 | 3 | `public java.util.Map<String, Integer> getLossMonthsState()` | THE TWO HISTORIES, CARRIED. |
| 387 | 4 | `public void restoreLossMonths(java.util.Map<String, Integer> saved)` |  |
| 392 | 3 | `public java.util.List<Integer> getPopulationHistory()` |  |
| 396 | 6 | `public void restorePopulationHistory(java.util.List<Integer> saved)` |  |
| 403 | 3 | `public int getLossMonths(String sector)` |  |
| 416 | 59 | `public Decision planRetirement(Sector sector, double demand, double capacity, int ordersInFlight)` | Whether a sector should sell capacity, and how much. |
| 495 | 4 | `private static boolean makesWhatItSells(Sector sector, BuildingsTemplate template)` | WHAT A SECTOR IN DISTRESS MAY SELL IS ANYTHING IT MAKES WITH (0.7.12 round 7). |
| 512 | 35 | `public Decision planDistressRetirement(Sector sector, double cash, int ordersInFlight)` | Whether a sector that cannot pay its way and cannot borrow should shed capacity anyway. |

### THE MAKER'S RULE - the default Sector.plan() (lines 548-845)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 173 | `public Decision planMaker(Sector sector, Game game)` |  |
| 741 | 5 | `public double forecast(Sector sector, GoodsMarket market)` | The demand a maker plans against: the smaller of the trend and this month (high AND been high - see planMaker), and no more than the sector can see on its customers' books over the months the trend looks back (Good.pl... |
| 748 | 4 | `private double growthShare()` | The population trend as a share a month, for projecting a good's demand forward. |
| 759 | 3 | `private static double knownCost(double raw)` | A break-even that a sector with nothing running cannot state. |
| 782 | 34 | `public double estimatedMakerProfit(Sector sector, BuildingsTemplate t)` | What one of a maker's templates would clear a month: every good it makes, at the price it would actually get for it, less the inputs it uses at theirs, at the rate the sector's plants actually run, less what the build... |
| 824 | 21 | `public double standingCostOf(Sector sector, BuildingsTemplate t)` | What a building costs its owner just for standing: the repairs and the property tax, at today's prices and the sector's own rate. |

### THE BANK'S BRANCH - not a sector, so its planner lives here (lines 846-901)

| line | len | member | says |
|---:|---:|---|---|
| 870 | 31 | `public Decision planBank()` | Whether to open another bank branch. |

### THE BRAKE (lines 902-1008)

| line | len | member | says |
|---:|---:|---|---|
| 914 | 6 | `public boolean servicesItsOwnDebt(double estimatedMonthlyProfit, double amountBorrowed, double annualRate)` | Whether a project can carry the debt it needs: if the new capacity cannot out-earn the interest on the money that built it, by a margin, the business declines the project even though the lender would fund it. |
| 923 | 1 | `public void setFamilies(FamilyModel families)` |  |
| 924 | 1 | `public FamilyModel families()` |  |
| 928 | 1 | `public void setBank(Bank bank)` |  |
| 931 | 9 | `public double wageBillFor(BuildingsTemplate t)` | What one of these would cost to staff, at what the city pays today. |
| 948 | 13 | `public double estimatedMonthlyProfit(String sector, BuildingsTemplate t)` | Rough monthly profit a finished building would add - the screening number the interest test is struck on. |
| 967 | 3 | `public static double operatingRateOf(double rate)` | A sector's operating rate as a planning figure: what it is, unless the sector has nothing running yet, in which case a plant that does not exist runs at nameplate on paper. |
| 976 | 11 | `public double runningCostOf(BuildingsTemplate t)` | Wages, power and water for a building that does not exist yet, read off the template and the current schedule rather than off a sector's income statement, because the first mine in a city has no sector to read. |
| 996 | 8 | `private double totalCostOf(BuildingsTemplate t, int quantity)` | Cash price of a building, matching what Game charges: the builders' price for the work - its labour at today's wages (0.7.19) - plus any materials that have to be bought beyond the city's yard, at the market price, wi... |
| 1005 | 3 | `public double getCostOf(BuildingsTemplate t, int quantity)` |  |


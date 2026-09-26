# BusinessInvestment.java - 889 lines · 42 methods · 11 constants · model

`ham/citybuildersim/BusinessInvestment.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
>   2. LEAD TIME. A building takes constructionPoints / cityOutput months to
>      finish, so demand is projected to completion plus a planning horizon.
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

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (22), [Sector](Sector.md) (8), [Good](Good.md) (6), [JobType](JobType.md) (4), [GoodsMarket](GoodsMarket.md) (3), [FamilyModel](FamilyModel.md) (3), [BuildingManager](BuildingManager.md) (2), [EconomyManager](EconomyManager.md) (2), [Bank](Bank.md) (2), [Game](Game.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (27):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [InvestCheck](InvestCheck.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Mining](Mining.md), [MortgageCheck](MortgageCheck.md), [PolicyCheck](PolicyCheck.md), [Rail](Rail.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 59 | SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) |
| 240 | RETIREMENT |
| 468 | THE MAKER'S RULE - the default Sector.plan() |
| 739 | THE BANK'S BRANCH - not a sector, so its planner lives here |
| 784 | THE BRAKE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 48 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 51 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 54 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 57 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site. |
| 97 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, expressed as months of the city's whole construction output. |
| 100 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 254 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 257 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 260 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 269 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `private final java.util.Set<String> held` |  |
| 102 | `private final BuildingManager buildingManager` |  |
| 103 | `private final EconomyManager economyManager` |  |
| 105 | `private final List<Integer> populationHistory` |  |
| 108 | `private final java.util.Map<String, Integer> lossMonths` | Consecutive months each sector has lost money. |
| 111 | `private double landAvailable` | What the city has left to sell, and what it is charging for it. |
| 112 | `private double landPricePerSqFt` |  |
| 122 | `public final String sector` |  |
| 123 | `public final BuildingsTemplate template` |  |
| 124 | `public final int quantity` |  |
| 125 | `public final String reason` |  |
| 126 | `public final boolean build` |  |
| 132 | `public final boolean landBlocked` | True when the ONLY thing stopping this was nowhere to put it - the one refusal the player can personally clear, by annexing. |
| 804 | `private FamilyModel families` | The household mix, so a residential building can be priced on who would actually live in it. |
| 809 | `private Bank bank` | The bank, so the advisor can see when credit has got dear. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 848 | **type** `public class BusinessInvestment` | Capacity planning for the private sector. |

### SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) (lines 59-239)

| line | len | member | says |
|---:|---:|---|---|
| 86 | 1 | `public void holdSector(String key)` | Harnesses only: this sector will not ask to build for the rest of the run. |
| 89 | 1 | `public boolean isHeld(String key)` | Whether a sector has been held out by a fixture. |
| 114 | 4 | `public void setLandAvailable(double sqFt, double pricePerSqFt)` |  |
| 120 | 38 | **type** `public static class Decision` | What the engine decided, and why - surfaced on the sector screens. |
| 134 | 4 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build)` _(in BusinessInvestment.Decision)_ |  |
| 139 | 9 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build, boolean landBlocked)` _(in BusinessInvestment.Decision)_ |  |
| 149 | 3 | `public static Decision no(String sector, String reason)` _(in BusinessInvestment.Decision)_ |  |
| 154 | 3 | `public static Decision noLand(String sector, String reason)` _(in BusinessInvestment.Decision)_ | Wanted to build, had nowhere to put it. |
| 159 | 4 | `public BusinessInvestment(BuildingManager buildingManager, EconomyManager economyManager)` |  |
| 165 | 6 | `public void recordMonth(int population)` | Call once a month, before the sectors are asked what they want to build. |
| 177 | 6 | `public double getPopulationGrowth()` | Average monthly population change over the window. |
| 193 | 4 | `public double reachablePopulation()` | The most people this city could physically hold once everything on site is finished. |
| 199 | 4 | `public double leadTime(BuildingsTemplate template, int quantity, double cityConstructionOutput)` | Months before a building of this size would actually open. |
| 212 | 14 | `public int orderSize(double shortfall, double capacityPerUnit, BuildingsTemplate template, double cityConstructionOutput)` | How many of a building to order: enough to close the gap, but no more than the city's builders could deliver in MAX_ORDER_MONTHS, and never more plots than the city has land to sell. |
| 228 | 5 | `public int plotsAvailableFor(BuildingsTemplate template)` | How many of these the city currently has room for. |
| 235 | 4 | `public String landReason(BuildingsTemplate template)` | Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one. |

### RETIREMENT (lines 240-467)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 7 | `public void recordSectorResult(String sector, double netIncome)` | Call once a month with each sector's net income. |
| 292 | 3 | `public void noteOpened(String sector)` | A sector that has just opened something starts its count again. |
| 303 | 3 | `public java.util.Map<String, Integer> getLossMonthsState()` | THE TWO HISTORIES, CARRIED. |
| 307 | 4 | `public void restoreLossMonths(java.util.Map<String, Integer> saved)` |  |
| 312 | 3 | `public java.util.List<Integer> getPopulationHistory()` |  |
| 316 | 6 | `public void restorePopulationHistory(java.util.List<Integer> saved)` |  |
| 323 | 3 | `public int getLossMonths(String sector)` |  |
| 336 | 59 | `public Decision planRetirement(Sector sector, double demand, double capacity, int ordersInFlight)` | Whether a sector should sell capacity, and how much. |
| 415 | 4 | `private static boolean makesWhatItSells(Sector sector, BuildingsTemplate template)` | WHAT A SECTOR IN DISTRESS MAY SELL IS ANYTHING IT MAKES WITH (0.7.12 round 7). |
| 432 | 35 | `public Decision planDistressRetirement(Sector sector, double cash, int ordersInFlight)` | Whether a sector that cannot pay its way and cannot borrow should shed capacity anyway. |

### THE MAKER'S RULE - the default Sector.plan() (lines 468-738)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 153 | `public Decision planMaker(Sector sector, Game game)` |  |
| 641 | 5 | `public double forecast(Sector sector, GoodsMarket market)` | The demand a maker plans against: the smaller of the trend and this month (high AND been high - see planMaker), and no more than the sector can see on its customers' books over the months the trend looks back (Good.pl... |
| 648 | 4 | `private double growthShare()` | The population trend as a share a month, for projecting a good's demand forward. |
| 659 | 3 | `private static double knownCost(double raw)` | A break-even that a sector with nothing running cannot state. |
| 682 | 34 | `public double estimatedMakerProfit(Sector sector, BuildingsTemplate t)` | What one of a maker's templates would clear a month: every good it makes, at the price it would actually get for it, less the inputs it uses at theirs, at the rate the sector's plants actually run, less what the build... |
| 724 | 14 | `public double standingCostOf(Sector sector, BuildingsTemplate t)` | What a building costs its owner just for standing: the repairs and the property tax, at today's prices and the sector's own rate. |

### THE BANK'S BRANCH - not a sector, so its planner lives here (lines 739-783)

| line | len | member | says |
|---:|---:|---|---|
| 756 | 27 | `public Decision planBank()` | Whether to open another bank branch. |

### THE BRAKE (lines 784-889)

| line | len | member | says |
|---:|---:|---|---|
| 796 | 6 | `public boolean servicesItsOwnDebt(double estimatedMonthlyProfit, double amountBorrowed, double annualRate)` | Whether a project can carry the debt it needs: if the new capacity cannot out-earn the interest on the money that built it, by a margin, the business declines the project even though the lender would fund it. |
| 805 | 1 | `public void setFamilies(FamilyModel families)` |  |
| 806 | 1 | `public FamilyModel families()` |  |
| 810 | 1 | `public void setBank(Bank bank)` |  |
| 813 | 9 | `public double wageBillFor(BuildingsTemplate t)` | What one of these would cost to staff, at what the city pays today. |
| 829 | 16 | `public double estimatedMonthlyProfit(String sector, BuildingsTemplate t)` | Rough monthly profit a finished building would add - the screening number the interest test is struck on. |
| 851 | 3 | `public static double operatingRateOf(double rate)` | A sector's operating rate as a planning figure: what it is, unless the sector has nothing running yet, in which case a plant that does not exist runs at nameplate on paper. |
| 860 | 11 | `public double runningCostOf(BuildingsTemplate t)` | Wages, power and water for a building that does not exist yet, read off the template and the current schedule rather than off a sector's income statement, because the first mine in a city has no sector to read. |
| 877 | 8 | `private double totalCostOf(BuildingsTemplate t, int quantity)` | Cash price of a building, matching what Game charges: the cash cost plus any materials that have to be bought beyond the city's yard, at the market price, plus the land. |
| 886 | 3 | `public double getCostOf(BuildingsTemplate t, int quantity)` |  |


# BusinessInvestment.java - 849 lines · 41 methods · 11 constants · model

`ham/citybuildersim/BusinessInvestment.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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
> 
> SINCE THE SECTOR TEMPLATE (2026-09-11) this class is the shared
> arithmetic and the two generic rules - the maker's expansion and the two
> ways to shrink. Each sector's own decision is Sector.plan(); the landlords,
> the shops, the builders, the mills and the mines override it, and a
> sector that is a factory does not. The bank's branch has its own planner
> here because the bank is not a sector.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (21), [Sector](Sector.md) (7), [Good](Good.md) (5), [JobType](JobType.md) (4), [GoodsMarket](GoodsMarket.md) (3), [FamilyModel](FamilyModel.md) (3), [BuildingManager](BuildingManager.md) (2), [EconomyManager](EconomyManager.md) (2), [Bank](Bank.md) (2), [Game](Game.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)

**Used by (25):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [InvestCheck](InvestCheck.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Mining](Mining.md), [PolicyCheck](PolicyCheck.md), [Rail](Rail.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 57 | SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) |
| 238 | RETIREMENT |
| 440 | THE MAKER'S RULE - the default Sector.plan() |
| 711 | THE BANK'S BRANCH - not a sector, so its planner lives here |
| 747 | THE BRAKE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `BusinessInvestment.PLANNING_HORIZON` | `6` | Months of demand growth to build ahead of, on top of the lead time. |
| 46 | `BusinessInvestment.TREND_WINDOW` | `12` | How many months of population history to measure the trend over. |
| 49 | `BusinessInvestment.TARGET_HEADROOM` | `.05` | Below this much spare capacity (as a fraction of demand), start building. |
| 52 | `BusinessInvestment.PROFIT_OVER_INTEREST` | `1.25` | A project must clear its interest by this much to be worth doing. |
| 55 | `BusinessInvestment.MAX_CONCURRENT_ORDERS` | `1` | Never start a second order for a sector while one is still on site. |
| 95 | `BusinessInvestment.MAX_ORDER_MONTHS` | `12` | The largest order a sector will place, expressed as months of the city's whole construction output. |
| 98 | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | `9` | Months of construction backlog above which the builders build themselves more capacity. |
| 252 | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | `6` | Consecutive loss-making months before a sector starts selling capacity. |
| 255 | `BusinessInvestment.RETIREMENT_SLACK` | `.25` | Capacity has to exceed demand by this much before any of it is spare. |
| 258 | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | `.25` | Most of its excess a sector will scrap in one month. |
| 266 | `BusinessInvestment.DISTRESS_LOSS_MONTHS` | `24` | Months of losses before a sector that is overdrawn and refused credit starts liquidating plant it is actually using. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `private final java.util.Set<String> held` |  |
| 100 | `private final BuildingManager buildingManager` |  |
| 101 | `private final EconomyManager economyManager` |  |
| 103 | `private final List<Integer> populationHistory` |  |
| 106 | `private final java.util.Map<String, Integer> lossMonths` | Consecutive months each sector has lost money. |
| 109 | `private double landAvailable` | What the city has left to sell, and what it is charging for it. |
| 110 | `private double landPricePerSqFt` |  |
| 120 | `public final String sector` |  |
| 121 | `public final BuildingsTemplate template` |  |
| 122 | `public final int quantity` |  |
| 123 | `public final String reason` |  |
| 124 | `public final boolean build` |  |
| 130 | `public final boolean landBlocked` | True when the ONLY thing stopping this was nowhere to put it - the one refusal the player can personally clear, by annexing. |
| 764 | `private FamilyModel families` | The household mix, so a residential building can be priced on who would actually live in it. |
| 769 | `private Bank bank` | The bank, so the advisor can see when credit has got dear. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 810 | **type** `public class BusinessInvestment` | Capacity planning for the private sector. |

### SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13) (lines 57-237)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 1 | `public void holdSector(String key)` | Harnesses only: this sector will not ask to build for the rest of the run. |
| 87 | 1 | `public boolean isHeld(String key)` | Whether a sector has been held out by a fixture. |
| 112 | 4 | `public void setLandAvailable(double sqFt, double pricePerSqFt)` |  |
| 118 | 38 | **type** `public static class Decision` | What the engine decided, and why - surfaced on the sector screens. |
| 132 | 4 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build)` _(in BusinessInvestment.Decision)_ |  |
| 137 | 9 | `public Decision(String sector, BuildingsTemplate template, int quantity, String reason, boolean build, boolean landBlocked)` _(in BusinessInvestment.Decision)_ |  |
| 147 | 3 | `public static Decision no(String sector, String reason)` _(in BusinessInvestment.Decision)_ |  |
| 152 | 3 | `public static Decision noLand(String sector, String reason)` _(in BusinessInvestment.Decision)_ | Wanted to build, had nowhere to put it. |
| 157 | 4 | `public BusinessInvestment(BuildingManager buildingManager, EconomyManager economyManager)` |  |
| 163 | 6 | `public void recordMonth(int population)` | Call once a month, before the sectors are asked what they want to build. |
| 175 | 6 | `public double getPopulationGrowth()` | Average monthly population change over the window. |
| 191 | 4 | `public double reachablePopulation()` | The most people this city could physically hold once everything on site is finished. |
| 197 | 4 | `public double leadTime(BuildingsTemplate template, int quantity, double cityConstructionOutput)` | Months before a building of this size would actually open. |
| 210 | 14 | `public int orderSize(double shortfall, double capacityPerUnit, BuildingsTemplate template, double cityConstructionOutput)` | How many of a building to order: enough to close the gap, but no more than the city's builders could deliver in MAX_ORDER_MONTHS, and never more plots than the city has land to sell. |
| 226 | 5 | `public int plotsAvailableFor(BuildingsTemplate template)` | How many of these the city currently has room for. |
| 233 | 4 | `public String landReason(BuildingsTemplate template)` | Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one. |

### RETIREMENT (lines 238-439)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 7 | `public void recordSectorResult(String sector, double netIncome)` | Call once a month with each sector's net income. |
| 289 | 3 | `public void noteOpened(String sector)` | A sector that has just opened something starts its count again. |
| 300 | 3 | `public java.util.Map<String, Integer> getLossMonthsState()` | THE TWO HISTORIES, CARRIED. |
| 304 | 4 | `public void restoreLossMonths(java.util.Map<String, Integer> saved)` |  |
| 309 | 3 | `public java.util.List<Integer> getPopulationHistory()` |  |
| 313 | 6 | `public void restorePopulationHistory(java.util.List<Integer> saved)` |  |
| 320 | 3 | `public int getLossMonths(String sector)` |  |
| 333 | 59 | `public Decision planRetirement(Sector sector, double demand, double capacity, int ordersInFlight)` | Whether a sector should sell capacity, and how much. |
| 405 | 34 | `public Decision planDistressRetirement(Sector sector, double cash, int ordersInFlight)` | Whether a sector that cannot pay its way and cannot borrow should shed capacity anyway. |

### THE MAKER'S RULE - the default Sector.plan() (lines 440-710)

| line | len | member | says |
|---:|---:|---|---|
| 448 | 153 | `public Decision planMaker(Sector sector, Game game)` |  |
| 613 | 5 | `public double forecast(Sector sector, GoodsMarket market)` | The demand a maker plans against: the smaller of the trend and this month (high AND been high - see planMaker), and no more than the sector can see on its customers' books over the months the trend looks back (Good.pl... |
| 620 | 4 | `private double growthShare()` | The population trend as a share a month, for projecting a good's demand forward. |
| 631 | 3 | `private static double knownCost(double raw)` | A break-even that a sector with nothing running cannot state. |
| 654 | 34 | `public double estimatedMakerProfit(Sector sector, BuildingsTemplate t)` | What one of a maker's templates would clear a month: every good it makes, at the price it would actually get for it, less the inputs it uses at theirs, at the rate the sector's plants actually run, less what the build... |
| 696 | 14 | `public double standingCostOf(Sector sector, BuildingsTemplate t)` | What a building costs its owner just for standing: the repairs and the property tax, at today's prices and the sector's own rate. |

### THE BANK'S BRANCH - not a sector, so its planner lives here (lines 711-746)

| line | len | member | says |
|---:|---:|---|---|
| 723 | 23 | `public Decision planBank()` | Whether to open another bank branch. |

### THE BRAKE (lines 747-849)

| line | len | member | says |
|---:|---:|---|---|
| 756 | 6 | `public boolean servicesItsOwnDebt(double estimatedMonthlyProfit, double amountBorrowed, double annualRate)` | Whether a project can carry the debt it needs: if the new capacity cannot out-earn the interest on the money that built it, by a margin, the business declines the project even though the lender would fund it. |
| 765 | 1 | `public void setFamilies(FamilyModel families)` |  |
| 766 | 1 | `public FamilyModel families()` |  |
| 770 | 1 | `public void setBank(Bank bank)` |  |
| 773 | 9 | `public double wageBillFor(BuildingsTemplate t)` | What one of these would cost to staff, at what the city pays today. |
| 789 | 16 | `public double estimatedMonthlyProfit(String sector, BuildingsTemplate t)` | Rough monthly profit a finished building would add - the screening number the interest test is struck on. |
| 811 | 3 | `public static double operatingRateOf(double rate)` | A sector's operating rate as a planning figure: what it is, unless the sector has nothing running yet, in which case a plant that does not exist runs at nameplate on paper. |
| 820 | 11 | `public double runningCostOf(BuildingsTemplate t)` | Wages, power and water for a building that does not exist yet, read off the template and the current schedule rather than off a sector's income statement, because the first mine in a city has no sector to read. |
| 837 | 8 | `private double totalCostOf(BuildingsTemplate t, int quantity)` | Cash price of a building, matching what Game charges: the cash cost plus any materials that have to be bought beyond the city's yard, at the market price, plus the land. |
| 846 | 3 | `public double getCostOf(BuildingsTemplate t, int quantity)` |  |


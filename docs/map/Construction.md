# Construction.java - 466 lines · 30 methods · 1 constants · sectors

`ham/citybuildersim/sectors/Construction.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The builders. Every build order in the city is theirs, and they bill it.
> 
> WHAT MAKES IT UNLIKE A FACTORY, and therefore what it overrides:
> 
>   revenue     an order pays the whole price up front, but the work happens
>               over months. Booking it all on the order month made the
>               builders look enormously profitable in a boom and ruinous
>               for the year after, doing work they had been paid for. So
>               the price goes into an ORDER BOOK and is earned as the points
>               are delivered - except the material that had to be bought
>               in, which is delivered to site the month it is bought and
>               earned at the next strike (see bill()).
>   inputs      building material, DRAWN when an order is placed rather than
>               bid for monthly: the city's yard first, then the builders'
>               own stock of scrapped plant's material (0.7.8), the materials
>               plant next, the world last. See Markets.draw().
>   payroll     a firm with no work keeps a core crew and its yard and pays
>               a quarter of its wages, not all of them and not none.
>   planning    off the order book, not off population: it expands when the
>               queue is deeper than BACKLOG_MONTHS_BEFORE_EXPANDING.
> 
> Everything else - the books, the credit, the listing, the property tax on
> its depots and the repairs it pays itself for - is the template's. The
> city's own works department (BuildingManager.BASE_CONSTRUCTION) builds
> alongside the depots and has no payroll; it is capacity, not a company.

**Uses:** [Good](Good.md) (13), [BusinessInvestment](BusinessInvestment.md) (10), [Game](Game.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (8):** [BankCheck](BankCheck.md), [Game](Game.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [NewGameCheck](NewGameCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 87 | THE ORDER BOOK |
| 180 | WHAT IT COSTS TO STAND |
| 208 | MATERIALS ARE DRAWN, NOT BID FOR |
| 276 | PLANNING - off the order book |
| 351 | THE SCREEN |
| 427 | SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The smallest share of payroll construction pays when it has no work. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private double unearnedRevenue` | Billed but not yet earned, and the work it is owed against. |
| 62 | `private double backlogPoints` |  |
| 65 | `private double utilisation` | How much of the crew had something to do this month. |
| 68 | `private double recognisedThisMonth` | What the month last struck recognised, for the national accounts' investment line. |
| 76 | `private double repairsThisMonth` | Repairs billed in the month last struck, apart from the building work - not investment. |
| 239 | `private double salvageCost` | What the builders paid for the salvage on hand, in money: its cost basis. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 425 | **type** `public final class Construction extends Sector` | The builders. |
| 78 | 8 | `public Construction()` |  |

### THE ORDER BOOK (lines 87-179)

| line | len | member | says |
|---:|---:|---|---|
| 98 | 4 | `public void bill(double amount, double points)` | Takes an order: the whole price into the order book, to be earned as the work is delivered, material and all. |
| 113 | 22 | `public void recogniseWork(double earned, double pointsDelivered)` | Recognise the month's work. |
| 144 | 7 | `public void receiveMaintenance(double amount)` | The repair order for the month, from every owner of a standing building. |
| 153 | 1 | `public double getRecognisedThisMonth()` | Building work recognised this month - the national accounts' investment. |
| 156 | 1 | `public double getRepairsThisMonth()` | Repairs billed this month. |
| 158 | 1 | `public double getUnearnedRevenue()` |  |
| 159 | 1 | `public double getBacklogPoints()` |  |
| 170 | 1 | `public double getOrderBookForAudit()` | The order book as the money audit counts it: what is still owed to the sites AND what this month's work has earned but not yet banked. |
| 171 | 1 | `public double getUtilisation()` |  |
| 174 | 5 | `public void restoreOrderBook(double cash, double unearned, double backlog)` | The order book, put back on load. |

### WHAT IT COSTS TO STAND (lines 180-207)

| line | len | member | says |
|---:|---:|---|---|
| 186 | 3 | `protected double payrollScale()` | ...and by how much work there was. |
| 196 | 6 | `public double getStandingCostPerCapacity(double capacity)` | What it costs to keep one point of capacity standing for a month. |
| 204 | 3 | `public double getConstructionCapacity()` | Points a month the depots and the city's own works could deliver, before staffing and roads. |

### MATERIALS ARE DRAWN, NOT BID FOR (lines 208-275)

| line | len | member | says |
|---:|---:|---|---|
| 214 | 1 | `public double bid(Good g)` | Nothing bid: the crews draw what the month's work needs, as they build. |
| 242 | 1 | `public double getSalvage()` | Units of scrapped plant's material on hand. |
| 245 | 1 | `public double getSalvageCost()` | ...and what they paid for it. |
| 248 | 5 | `public void addSalvage(double units, double paid)` | Takes material bought from plant being scrapped into the stock, at what was paid for it (nothing for the builders' own). |
| 260 | 11 | `public double takeSalvage(double units)` | Draws from the stock, up to what it holds, and books what the units drawn cost - their share of the cost basis - as this month's input. |
| 274 | 1 | `public double getInventoryValue()` | Its stock of scrapped plant's material at today's price; the yard is the city's and the plant is the plant's. |

### PLANNING - off the order book (lines 276-350)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 41 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Everyone else's lead times are its output, so when the queue gets long it is the constraint on the whole city. |
| 334 | 3 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | A depot's extra output is billable work. |
| 344 | 6 | `public double[] retirementDemandAndCapacity(Game game)` | Its demand is the queue: work ordered and not yet done, capped at what its plant could deliver in a month, and never less than what the city has undertaken to keep alive - a subsidised depot has a customer. |

### THE SCREEN (lines 351-426)

| line | len | member | says |
|---:|---:|---|---|
| 356 | 1 | `public String inputLabel()` |  |
| 372 | 6 | `protected java.util.Map<String, Double> nameOtherRevenue()` | THE BUILDERS' REVENUE IS TWO BUSINESSES and the statement showed one figure. |
| 380 | 46 | `public List<Line> operations(Game game)` |  |

### SAVE (lines 427-466)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 8 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 442 | 8 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 452 | 5 | `protected void resetExtras()` |  |
| 460 | 6 | `protected void redenominateExtras(double scale)` | Points and the utilisation are work, not money; the book is money. |


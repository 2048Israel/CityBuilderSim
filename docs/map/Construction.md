# Construction.java - 856 lines · 44 methods · 1 constants · sectors

`ham/citybuildersim/sectors/Construction.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The builders. Every build order in the city is theirs, and they bill it.
> 
> WHAT MAKES IT UNLIKE A FACTORY, and therefore what it overrides:
> 
>   revenue     an order pays the whole price up front, but the work happens
>               over months. Booking it all on the order month made the
>               builders look enormously profitable in a boom and ruinous
>               for the year after, doing work they had been paid for. So
>               the price goes into an ORDER BOOK and is earned as the points
>               are delivered, its material with them (see bill(); until
>               2026-09-11 the material bought in was earned at the next
>               strike). Since 0.7.19 the price carries the sales tax they
>               remit, and the owners' material escalation is earned beside
>               the work (recogniseEscalation()).
>   inputs      building material, DRAWN as the work is done (since
>               2026-09-11; when an order was placed until then) rather than
>               bid for monthly: the city's yard, delivered to the sites the
>               day the order is placed, then the builders' own stock of
>               scrapped plant's material (0.7.8), the materials plant next,
>               the world last. See Markets.draw().
>   payroll     a firm with less work than crews lays the rest off and keeps
>               a core crew - a quarter of its posts - and its yard; the
>               posts it keeps it pays in full (THE CREWS THE WORK NEEDS).
>   planning    off the order book, not off population: it expands when the
>               queue is deeper than BACKLOG_MONTHS_BEFORE_EXPANDING.
> 
> Everything else - the books, the credit, the listing, the property tax on
> its depots and the repairs it pays itself for - is the template's. The
> city's own works department (BuildingManager.BASE_CONSTRUCTION) builds
> alongside the depots and has no payroll; it is capacity, not a company.

**Uses:** [Good](Good.md) (13), [BusinessInvestment](BusinessInvestment.md) (11), [Game](Game.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [BuildingManager](BuildingManager.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [JobType](JobType.md) (1)

**Used by (12):** [BankCheck](BankCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [Game](Game.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sectors](Sectors.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 222 | THE ORDER BOOK |
| 310 | · THE OVERTIME AND THE CANCELS (0.7.22; ConstructionControl, B and C) |
| 417 | WHAT IT COSTS TO STAND |
| 446 | MATERIALS ARE DRAWN, NOT BID FOR |
| 514 | PLANNING - off the order book |
| 680 | THE SCREEN |
| 784 | SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The core crew: the smallest share of its posts construction keeps on when it has no work, and lays the rest off. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `private double unearnedRevenue` | Billed but not yet earned, and the work it is owed against. |
| 70 | `private double backlogPoints` |  |
| 73 | `private double utilisation` | How much of the crew had something to do this month. |
| 140 | `private double postsOfferedShare` | The share of the depots' posts offered this month: the work's need over the fill, floored at the core crew. |
| 143 | `private double crewsNeeded` | ...and the need it was struck from, unfloored: the work over the depots' output at full staffing. |
| 173 | `private double fillStruckOn` | The fill the share was struck on: the sector's own, as last month's wages left it (0.7.17). |
| 195 | `private double recognisedThisMonth` | What the month last struck recognised, for the national accounts' investment line. |
| 203 | `private double escalationThisMonth` | ...of which the owners' material escalation (0.7.19): what they paid for the material the month's work drew, at the price it was drawn at, less what their quotes allowed for it - negative for a refund. |
| 211 | `private double repairsThisMonth` | Repairs billed in the month last struck, apart from the building work - not investment. |
| 320 | `private double overtimeThisMonth` | What the city paid this month for overtime on its rushed sites, with the builders' tax in it (Game.settleConstructionControl()): earned beside the work, as the escalation is - the price of the hours the work was done ... |
| 343 | `private double[] overtimeWages` | The overtime's wages, by job type, that the crews on the rushed sites were paid this month on top of their posts' (0.7.22): the premium, the whole of it, in a depot's mix of posts at today's wages. |
| 477 | `private double salvageCost` | What the builders paid for the salvage on hand, in money: its cost basis. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 810 | **type** `public final class Construction extends Sector` | The builders. |
| 152 | 13 | `public void strikeCrews(double work, double cityWorks, double depots)` | Strikes the month's crews. |
| 167 | 1 | `public double getPostsOfferedShare()` | The share of the depots' posts on offer this month. |
| 170 | 1 | `public double getCrewsNeeded()` | The work over the depots' full-staffing output, as struck - above 1 when the work is more than they can do. |
| 176 | 1 | `public double getFillStruckOn()` | The fill the month's share was struck on - see strikeCrews(). |
| 179 | 5 | `public int getPostsStanding()` | The posts its depots have, offered or not. |
| 187 | 6 | `public void updateWages(double[] wagePerType, int[] posts)` | The wage bill on the posts it offers: its depots' posts at the struck share, rounded as the city counts them. |
| 213 | 8 | `public Construction()` |  |

### THE ORDER BOOK (lines 222-309)

| line | len | member | says |
|---:|---:|---|---|
| 233 | 4 | `public void bill(double amount, double points)` | Takes an order: the whole price into the order book, to be earned as the work is delivered, material and all. |
| 249 | 4 | `public void recogniseWork(double earned, double pointsDelivered)` | Recognise the month's work. |
| 267 | 24 | `public void recogniseWork(double earned, double pointsBuilt, double pointsAvailable)` | The same, with the points the sites actually took beside the points they were offered (0.7.17). |
| 299 | 7 | `public void recogniseEscalation(double amount)` | The owners' material escalation on the month's work (0.7.19): earned beside it, in the same month and on the same line of the accounts - it is the price of the material the work was built with - or, for a refund, give... |
| 308 | 1 | `public double getEscalationThisMonth()` | ...this month's, for the screens. |

### THE OVERTIME AND THE CANCELS (0.7.22; ConstructionControl, B and C) (lines 310-416)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 6 | `public void recogniseOvertime(double amount)` | ...earned now. |
| 330 | 1 | `public double getOvertimeThisMonth()` |  |
| 345 | 3 | `public void setOvertimeWages(double[] byType)` |  |
| 350 | 1 | `public double[] getOvertimeWages()` | The month's overtime wages by job type, or null. |
| 354 | 7 | `public double[] getStaffedPayrollPerType()` | The posts' staffed payroll by tier, and the month's overtime on it. |
| 368 | 4 | `public void cancelOrder(double refund, double points)` | A cancelled order, stopped (0.7.22; ConstructionControl, C. |
| 381 | 7 | `public void receiveMaintenance(double amount)` | The repair order for the month, from every owner of a standing building. |
| 390 | 1 | `public double getRecognisedThisMonth()` | Building work recognised this month - the national accounts' investment. |
| 393 | 1 | `public double getRepairsThisMonth()` | Repairs billed this month. |
| 395 | 1 | `public double getUnearnedRevenue()` |  |
| 396 | 1 | `public double getBacklogPoints()` |  |
| 407 | 1 | `public double getOrderBookForAudit()` | The order book as the money audit counts it: what is still owed to the sites AND what this month's work has earned but not yet banked. |
| 408 | 1 | `public double getUtilisation()` |  |
| 411 | 5 | `public void restoreOrderBook(double cash, double unearned, double backlog)` | The order book, put back on load. |

### WHAT IT COSTS TO STAND (lines 417-445)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 7 | `public double getStandingCostPerCapacity(double capacity)` | What it costs to keep one point of capacity standing for a month. |
| 442 | 3 | `public double getConstructionCapacity()` | Points a month the depots and the city's own works could deliver, before staffing and roads. |

### MATERIALS ARE DRAWN, NOT BID FOR (lines 446-513)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 1 | `public double bid(Good g)` | Nothing bid: the crews draw what the month's work needs, as they build. |
| 480 | 1 | `public double getSalvage()` | Units of scrapped plant's material on hand. |
| 483 | 1 | `public double getSalvageCost()` | ...and what they paid for it. |
| 486 | 5 | `public void addSalvage(double units, double paid)` | Takes material bought from plant being scrapped into the stock, at what was paid for it (nothing for the builders' own). |
| 498 | 11 | `public double takeSalvage(double units)` | Draws from the stock, up to what it holds, and books what the units drawn cost - their share of the cost basis - as this month's input. |
| 512 | 1 | `public double getInventoryValue()` | Its stock of scrapped plant's material at today's price; the yard is the city's and the plant is the plant's. |

### PLANNING - off the order book (lines 514-679)

| line | len | member | says |
|---:|---:|---|---|
| 541 | 70 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Everyone else's lead times are its output, so when the queue gets long it is the constraint on the whole city. |
| 630 | 9 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | WHAT A DEPOT WOULD EARN, LESS WHAT IT WOULD COST (0.7.19). |
| 666 | 13 | `public double[] retirementDemandAndCapacity(Game game)` | Its demand is the repairs and the queue: the city's repair order and the work ordered and not yet done, the queue capped at what the sites could take this month, and never less than what the city has undertaken to kee... |

### THE SCREEN (lines 680-783)

| line | len | member | says |
|---:|---:|---|---|
| 685 | 1 | `public String inputLabel()` |  |
| 701 | 9 | `protected java.util.Map<String, Double> nameOtherRevenue()` | THE BUILDERS' REVENUE IS TWO BUSINESSES and the statement showed one figure. |
| 712 | 71 | `public List<Line> operations(Game game)` |  |

### SAVE (lines 784-856)

| line | len | member | says |
|---:|---:|---|---|
| 789 | 20 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 811 | 23 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 836 | 8 | `protected void resetExtras()` |  |
| 847 | 9 | `protected void redenominateExtras(double scale)` | Points and the utilisation are work, not money; the book is money. |


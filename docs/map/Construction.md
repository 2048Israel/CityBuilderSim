# Construction.java - 1,153 lines · 65 methods · 3 constants · sectors

`ham/citybuildersim/sectors/Construction.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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
> its depots and the repairs it pays itself for - is the template's. Every
> point of building work is a yard's or a depot's, worked by its crews: the
> municipal works department's free points were retired at PT1-M4, and the
> city founds with a Builders' Yard standing instead (Game.foundingYard()).

**Uses:** [Good](Good.md) (19), [BusinessInvestment](BusinessInvestment.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (6), [BuildingManager](BuildingManager.md) (3), [Game](Game.md) (3), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [JobType](JobType.md) (1)

**Used by (24):** [BankCheck](BankCheck.md), [BuildersFirstCheck](BuildersFirstCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [FittingCheck](FittingCheck.md), [FoundingYardCheck](FoundingYardCheck.md), [Game](Game.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [ReloadCheck](ReloadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorFlow](SectorFlow.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [SlopeCheck](SlopeCheck.md), [UserInterface](UserInterface.md), [WantedPostsCheck](WantedPostsCheck.md)

## Sections

| line | section |
|---:|---|
| 259 | THE ORDER BOOK |
| 291 | THE FOREIGN CONTRACTOR'S BOOK (FC1) |
| 428 | · THE OVERTIME AND THE CANCELS (0.7.22; ConstructionControl, B and C) |
| 611 | WHAT IT COSTS TO STAND |
| 640 | MATERIALS ARE DRAWN, NOT BID FOR |
| 708 | PLANNING - off the order book |
| 933 | THE SCREEN |
| 1053 | SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `Construction.IDLE_PAYROLL_FLOOR` | `.25` | The core crew: the smallest share of its posts construction keeps on when it has no work, and lays the rest off. |
| 304 | `Construction.FOREIGN_CREWS` | `"Foreign crews"` | What the contractor's price is booked under, on the builders' input line and the money audit's "- construction Imports". |
| 713 | `Construction.ONE_DEPOT_ON_SITE` | `"a depot on site already"` | The builders' word while a depot of theirs is on site (ONE DEPOT AT A TIME, below); "already building" until BA1. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `private double unearnedRevenue` | Billed but not yet earned, and the work it is owed against. |
| 71 | `private double backlogPoints` |  |
| 74 | `private double billedPoints` | The construction points ever billed (ET2: what the foothill's preparation is a share of, in SlopeCheck 9 and LongPlaytest's summary): every order's, restarts and pavings too. |
| 77 | `private double utilisation` | How much of the crew had something to do this month. |
| 145 | `private double postsOfferedShare` | The share of the depots' posts offered this month: the work's need over the fill, floored at the core crew. |
| 148 | `private double crewsNeeded` | ...and the need it was struck from, unfloored: the work over the depots' output at full staffing. |
| 184 | `private double fillStruckOn` | The fill the share was struck on: the sector's own, as last month's wages left it (0.7.17). |
| 206 | `private double recognisedThisMonth` | What the month last struck recognised, for the national accounts' investment line. |
| 214 | `private double escalationThisMonth` | ...of which the owners' material escalation (0.7.19): what they paid for the material the month's work drew, at the price it was drawn at, less what their quotes allowed for it - negative for a refund. |
| 222 | `private double repairsThisMonth` | Repairs billed in the month last struck, apart from the building work - not investment. |
| 236 | `private double rDrawnLocal` | Materials bought from the plant in the month last struck, in units: the row as it stood at the strike. |
| 239 | `private double rDrawnImported` | ...and imported. |
| 248 | `private boolean drawnKnown` | Whether the two above are this builder's own figures: FALSE AFTER LOADING A SAVE FROM BEFORE THEY WERE KEPT, until its first strike - the row they come from was never saved. |
| 307 | `private double foreignBook` | Points the foreign contractor has taken and not yet built (FC1). |
| 310 | `private double foreignPointsThisMonth` | Points the contractor built in the month last settled (FC1). |
| 313 | `private double foreignBillThisMonth` | What it was paid for them, the builders' import of services in that month (FC1). |
| 438 | `private double overtimeThisMonth` | What the city paid this month for overtime on its rushed sites, with the builders' tax in it (Game.settleConstructionControl()): earned beside the work, as the escalation is - the price of the hours the work was done ... |
| 461 | `private double[] overtimeWages` | The overtime's wages, by job type, that the crews on the rushed sites were paid this month on top of their posts' (0.7.22): the premium, the whole of it, in a depot's mix of posts at today's wages. |
| 528 | `private double salvageCutThisMonth` | ...this month's: booked at the month's market after recogniseWork() cleared it, named at the next strike (nameOtherRevenue()). |
| 551 | `private double fittingThisMonth` | ...booked since the last strike banked its ledger, named at the next strike (nameOtherRevenue()) and read once for the national accounts. |
| 671 | `private double salvageCost` | What the builders paid for the salvage on hand, in money: its cost basis. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 1106 | **type** `public final class Construction extends Sector` | The builders. |
| 156 | 13 | `public void strikeCrews(double work, double depots)` | Strikes the month's crews. |
| 171 | 1 | `public double getPostsOfferedShare()` | The share of the depots' posts on offer this month. |
| 178 | 1 | `public boolean isIdleAtCoreCrew()` | Whether the builders stand idle at the core crew: the work this month's strike saw needs no more than the core crew, so they offer IDLE_PAYROLL_FLOOR of their posts and lay the rest off (strikeCrews()). |
| 181 | 1 | `public double getCrewsNeeded()` | The work over the depots' full-staffing output, as struck - above 1 when the work is more than they can do. |
| 187 | 1 | `public double getFillStruckOn()` | The fill the month's share was struck on - see strikeCrews(). |
| 190 | 5 | `public long getPostsStanding()` | The posts its depots have, offered or not. |
| 198 | 6 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill on the posts it offers: its depots' posts at the struck share, rounded as the city counts them. |
| 250 | 8 | `public Construction()` |  |

### THE ORDER BOOK (lines 259-290)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 7 | `public void bill(double amount, double points)` | Takes an order: the whole price into the order book, to be earned as the work is delivered, material and all. |
| 283 | 7 | `public void bill(double amount, double points, BuildingsTemplate t)` | ...an order of `t` (FC1): as bill(), but a depot the builders serve first - one the contractor builds as theirs while it takes orders (Game.foreignCrewsOffer(), BuildingManager.buildersOwnOwed()) - is not put on its b... |

### THE FOREIGN CONTRACTOR'S BOOK (FC1) (lines 291-427)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 5 | `public void payForeignCrews(double points, double bill)` | The contractor's month settled: the points it built and what the builders pay it for them, at its price. |
| 329 | 3 | `public void takeFromForeignBook(double points)` | These points off the contractor's book, never below nothing: points he built, a depot he builds as the builders' own (bill()), or an order cancelled (cancelOrder(), FC1b). |
| 339 | 3 | `public void capForeignBook(double owedOnSite)` | The book at most what is owed on site, before each month's offer (Game.foreignCrewsOffer()). |
| 344 | 1 | `public double getForeignBook()` | Points the foreign contractor has taken and not yet built. |
| 347 | 1 | `public double getForeignPointsThisMonth()` | Points the foreign contractor built in the month last settled. |
| 350 | 1 | `public double getForeignBillThisMonth()` | What the builders paid it for them: the import of services in the month last settled. |
| 353 | 1 | `public double getBilledPoints()` | The construction points ever billed, from this game's start (ET2): not saved. |
| 366 | 4 | `public void recogniseWork(double earned, double pointsDelivered)` | Recognise the month's work. |
| 384 | 25 | `public void recogniseWork(double earned, double pointsBuilt, double pointsAvailable)` | The same, with the points the sites actually took beside the points they were offered (0.7.17). |
| 417 | 7 | `public void recogniseEscalation(double amount)` | The owners' material escalation on the month's work (0.7.19): earned beside it, in the same month and on the same line of the accounts - it is the price of the material the work was built with - or, for a refund, give... |
| 426 | 1 | `public double getEscalationThisMonth()` | ...this month's, for the screens. |

### THE OVERTIME AND THE CANCELS (0.7.22; ConstructionControl, B and C) (lines 428-610)

| line | len | member | says |
|---:|---:|---|---|
| 441 | 6 | `public void recogniseOvertime(double amount)` | ...earned now. |
| 448 | 1 | `public double getOvertimeThisMonth()` |  |
| 463 | 3 | `public void setOvertimeWages(double[] byType)` |  |
| 468 | 1 | `public double[] getOvertimeWages()` | The month's overtime wages by job type, or null. |
| 472 | 7 | `public double[] getStaffedPayrollPerType()` | The posts' staffed payroll by tier, and the month's overtime on it. |
| 490 | 5 | `public void cancelOrder(double refund, double points, double offTheBook)` | A cancelled order, stopped (0.7.22; ConstructionControl, C. |
| 504 | 7 | `public void receiveMaintenance(double amount)` | The repair order for the month, from every owner of a standing building. |
| 521 | 5 | `public void earnSalvageCut(double amount)` | THE SALVAGE CUT'S CLEARING (0.7.138, batch I9; docs/studies/spec-materials.md 2.11, ★): the builders who cleared standing forest under a building are paid by Forestry, whose wood it is to sell, for felling and hauling... |
| 531 | 1 | `public double getSalvageCutThisMonth()` | ...this month's, for the screens and the strike. |
| 544 | 5 | `public void earnFitting(double amount)` | THE FITTINGS (R1b, 2026-11-05; Game.fit(), Fittings): the builders are paid for the goods they bought and the work they will do to fit a piece of equipment to a standing building, at the order. |
| 554 | 1 | `public double getFittingThisMonth()` | ...for the accounts' investment line and the screens. |
| 557 | 1 | `public double getRecognisedThisMonth()` | Building work recognised this month - the national accounts' investment. |
| 560 | 1 | `public double getRepairsThisMonth()` | Repairs billed this month. |
| 562 | 1 | `public double getUnearnedRevenue()` |  |
| 563 | 1 | `public double getBacklogPoints()` |  |
| 577 | 1 | `public double getOrderBookForAudit()` | The order book as the money audit counts it: what is still owed to the sites AND what this month's work has earned but not yet banked. |
| 578 | 1 | `public double getUtilisation()` |  |
| 581 | 1 | `public double getDrawnLocal()` | Materials bought from the plant in the month last struck, in units - the statement's month; NaN until a loaded older save's first strike. |
| 584 | 1 | `public double getDrawnImported()` | ...and imported in it. |
| 587 | 1 | `public boolean isDrawnKnown()` | Whether the month last struck's materials are known: false after loading a save from before 0.7.46, until its first strike. |
| 596 | 7 | `protected void beforeBank()` | The materials as they stand at the strike, kept before bank() clears them (A5). |
| 605 | 5 | `public void restoreOrderBook(double cash, double unearned, double backlog)` | The order book, put back on load. |

### WHAT IT COSTS TO STAND (lines 611-639)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 7 | `public double getStandingCostPerCapacity(double capacity)` | What it costs to keep one point of capacity standing for a month. |
| 636 | 3 | `public double getConstructionCapacity()` | Points a month the depots could deliver, before staffing and roads. |

### MATERIALS ARE DRAWN, NOT BID FOR (lines 640-707)

| line | len | member | says |
|---:|---:|---|---|
| 646 | 1 | `public double bid(Good g)` | Nothing bid: the crews draw what the month's work needs, as they build. |
| 674 | 1 | `public double getSalvage()` | Units of scrapped plant's material on hand. |
| 677 | 1 | `public double getSalvageCost()` | ...and what they paid for it. |
| 680 | 5 | `public void addSalvage(double units, double paid)` | Takes material bought from plant being scrapped into the stock, at what was paid for it (nothing for the builders' own). |
| 692 | 11 | `public double takeSalvage(double units)` | Draws from the stock, up to what it holds, and books what the units drawn cost - their share of the cost basis - as this month's input. |
| 706 | 1 | `public double getInventoryValue()` | Its stock of scrapped plant's material at today's price; the yard is the city's and the plant is the plant's. |

### PLANNING - off the order book (lines 708-932)

| line | len | member | says |
|---:|---:|---|---|
| 763 | 71 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Everyone else's lead times are its output, so when the queue gets long it is the constraint on the whole city. |
| 853 | 9 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | WHAT A DEPOT WOULD EARN, LESS WHAT IT WOULD COST (0.7.19). |
| 889 | 13 | `public double[] retirementDemandAndCapacity(Game game)` | Its demand is the repairs and the queue: the city's repair order and the work ordered and not yet done, the queue capped at what the sites could take this month, and never less than what the city has undertaken to kee... |
| 913 | 12 | `public boolean mayRetire(BuildingsTemplate t)` | THE LAST DEPOT IS NOT FOR SALE (PT1-M4). |
| 928 | 4 | `public String noRetirementReason(boolean distress)` | Why nothing could be sold, when mayRetire() refused everything: the builders have the one depot. |

### THE SCREEN (lines 933-1052)

| line | len | member | says |
|---:|---:|---|---|
| 939 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a builder, whose middle line is its gross profit on contracts. |
| 942 | 1 | `public String inputLabel()` |  |
| 958 | 13 | `protected java.util.Map<String, Double> nameOtherRevenue()` | THE BUILDERS' REVENUE IS TWO BUSINESSES and the statement showed one figure. |
| 973 | 1 | `public boolean hasPlantBlock()` |  |
| 976 | 76 | `public List<Line> ownLines(Game game)` |  |

### SAVE (lines 1053-1153)

| line | len | member | says |
|---:|---:|---|---|
| 1058 | 34 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 1094 | 31 | `protected void restoreExtras(Map<String, Double> extras)` |  |
| 1127 | 12 | `protected void resetExtras()` |  |
| 1142 | 11 | `protected void redenominateExtras(double scale)` | Points, the utilisation and the materials struck are work, not money; the book is money. |


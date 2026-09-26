# Sector.java - 1,950 lines · 164 methods · 7 constants · model

`ham/citybuildersim/Sector.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> One business in the city, and the template every sector extends.
> 
> WHY (2026-09-11). Jerus: "we should make a single template, and every
> single sector just extends it, and adds or overrides, right? cause in the
> future there will be over a thousand or near a thousand sectors." Until
> this class the six sectors were five handlers in five shapes, named by hand
> in a hundred places; a seventh would have been a week of finding them. See
> claude/the-sector-template.md for the design and the decisions.
> 
> WHAT EVERY SECTOR HAS, written once here:
> 
>   identity      a key (its saved name), a label, the build-menu group its
>                 buildings sit under
>   buildings     every template in buildings.json names its owner; the
>                 sector reads capacity, jobs, land, book value, power, water
>                 and road load off its own stacks
>   production    the goods it makes and the goods it uses, in units per
>                 building per month from the templates; the operating rate
>                 (staffing x power x water x roads x sickness) that scales
>                 output, and payroll that scales with staffing alone
>   stock         a warehouse per stockable output, a pantry per input it
>                 buys ahead of using
>   books         one income statement in one order for everyone; the cash
>                 it keeps; the balance-sheet inputs the city pushes in
>   the ledger    every sale and every purchase of the month, by counterparty,
>                 which is what the statement, the VAT and the accounts read
>   credit, listing, savings abroad - all keyed by the sector's name in
>                 BusinessDebtManager, Equity, Exchange and OutwardInvestment,
>                 none of which know or care what the sector makes
>   planning      the generic expansion decision, and the shrinking rules
> 
> A SECTOR CLASS IS A DECLARATION. Twenty lines, in ham.citybuildersim.sectors,
> one file each: what it makes, what it uses, what it stocks, and any hook it
> overrides. The quantities come from the buildings and the prices from the
> goods, so a sector that is nothing but a factory needs no code of its own
> at all - see sectors.Mining for the smallest one and sectors.RealEstate for
> the largest.
> 
> THE MONTH, in the order Sectors runs it (the same order the handlers ran
> in, so nothing about when money moves has changed):
> 
>   top of the month   bill(): interest, property tax and maintenance are
>                      set for the month; strike(): the statement is struck
>                      from LAST month's trades and THIS month's bills, the
>                      VAT is settled from the same figures, and the month
>                      is banked to cash. That is what the handlers did -
>                      they struck at the top, off the month before.
>   middle             the simulation refreshes wages, ratios, capacities.
>   bottom             Markets.clearMonth(): the seller-priced goods are sold
>                      (the shops to the households, the landlords' doors),
>                      then every traded good is priced, offered, bid for,
>                      allocated, imported and exported, and the makers
>                      produce for next month. Every fill lands in a ledger.
> 
> MONEY MOVES AT THE STRIKE AND NOWHERE ELSE in this class. A trade is a
> fact about units and a price; the cash follows when the statement is
> banked, buyer and seller in the same pass, so a local sale is always a
> pool-to-pool move inside one MoneyAudit window. The "cheque in the post"
> pool the food trade needed is gone with the lag that needed it.

**Uses:** [Good](Good.md) (107), [Statement](Statement.md) (9), [Game](Game.md) (7), [GoodsMarket](GoodsMarket.md) (7), [Trade](Trade.md) (7), [SectorState](SectorState.md) (7), [Markets](Markets.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [BuildingType](BuildingType.md) (3), [BuildingManager](BuildingManager.md) (3), [BalanceSheet](BalanceSheet.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [WageBand](WageBand.md) (2), [JobType](JobType.md) (2), [Formats](Formats.md) (2), [PopulationManager](PopulationManager.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (54):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HousingCheck](HousingCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [Materials](Materials.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [NewGameCheck](NewGameCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [ServicesScreen](ServicesScreen.md), [ShadowBasket](ShadowBasket.md), [SummaryScreen](SummaryScreen.md), [TaxPolicy](TaxPolicy.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 75 | IDENTITY AND DECLARATION |
| 136 | WIRING - set once by Sectors, through attach() |
| 156 | LABOUR |
| 199 | A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved |
| 337 | UTILISATION |
| 379 | THE FLEET (2026-09-17) |
| 439 | · AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL |
| 557 | MONEY AND THE BILLS |
| 630 | STOCK |
| 651 | CAPACITY, off the buildings |
| 670 | THE MONTH'S PRODUCTION FIGURES, per output good |
| 716 | THE LEDGER |
| 892 | BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) |
| 1091 | THE STATEMENT |
| 1248 | THE MONTH AT THE BOTTOM - HOOKS Markets CALLS |
| 1319 | WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS |
| 1569 | PLANNING - the decision to grow, and to shrink |
| 1659 | THE SCREENS |
| 1816 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1669 | `Sector.Line.Kind.HEAD` |  |
| 1669 | `Sector.Line.Kind.LINE` |  |
| 1669 | `Sector.Line.Kind.NOTE` |  |
| 1670 | `Sector.Line.Tone.NONE` |  |
| 1670 | `Sector.Line.Tone.GOOD` |  |
| 1670 | `Sector.Line.Tone.WARN` |  |
| 1670 | `Sector.Line.Tone.BAD` |  |
| 1670 | `Sector.Line.Tone.MUTED` |  |
| 1670 | `Sector.Line.Tone.HEAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 231 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 431 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 437 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 466 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 480 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 1261 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 1264 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `private final String key` |  |
| 80 | `private final String label` |  |
| 81 | `private final BuildingType group` |  |
| 82 | `private String blurb` |  |
| 84 | `private final Set<Good> makes` |  |
| 85 | `private final Set<Good> uses` |  |
| 88 | `private final Map<Good, Double> pantryMonths` | Inputs it buys ahead of using, and how many months of use it keeps. |
| 140 | `protected BuildingManager buildings` |  |
| 141 | `protected Markets markets` |  |
| 144 | `protected Game game` | The city, for the few hooks that need more than the buildings and the markets. |
| 161 | `protected final double[] wages` | Payroll per tier at full staffing: wage x posts. |
| 162 | `protected final int[] jobs` |  |
| 163 | `protected final double[] fill` |  |
| 166 | `protected double averageFill` | Share of its posts that are filled. |
| 350 | `protected double energyRatio` |  |
| 351 | `protected double electricity, water` |  |
| 352 | `protected double pricePerWatt, pricePerWaterUnit` |  |
| 495 | `protected boolean vansKnown` | Whether this sector's fleet is a fact about the sector rather than a fact about the version it was saved from. |
| 561 | `private double cash` |  |
| 564 | `private double interestExpense` | Set for the month at the top of it, before the statement runs. |
| 565 | `private double propertyTaxExpense` |  |
| 566 | `private double maintenanceExpense` |  |
| 569 | `private double taxRate` | The profit rate in force this month. |
| 571 | `private double landValue, buildingsValue, bondsPayable` |  |
| 635 | `protected final Map<Good, Double> stock` | Output on hand, per stockable good it makes. |
| 638 | `protected final Map<Good, Double> pantry` | Input on hand, per good it keeps a pantry of. |
| 676 | `public double capacity` | nameplate a month |
| 677 | `public double planned` | nameplate a month |
| 678 | `public double produced` | what it decided to make for home |
| 679 | `public double idled` | what went into stock or to market |
| 680 | `public double exportBound` | nameplate neither the city nor the world would take |
| 681 | `public double offered` | made straight for the ship |
| 682 | `public double withheld` | released to the market at the price |
| 683 | `public double soldLocal` | kept back below cost |
| 684 | `public double exported` | units taken by local buyers |
| 685 | `public double writtenOff` | units shipped, from the line or the shed |
| 686 | `public double costPerUnit` | stock lost to a demolished warehouse |
| 691 | `public double needed` | at this month's operating rate |
| 692 | `public double bid` | at this month's operating rate |
| 693 | `public double boughtLocal` | what it asked the market for |
| 694 | `public double imported` | filled at home |
| 697 | `protected final Map<Good, Output> outputs` |  |
| 698 | `protected final Map<Good, Input> inputs` |  |
| 744 | `public double atHome` |  |
| 745 | `public double abroad` |  |
| 751 | `public double localSales` | Sold to local buyers - sectors, households, the city - in money. |
| 753 | `public double exports` | Sold to the world. |
| 755 | `public double otherRevenue` | Revenue that is not a sale of a good: recognised building work, repairs billed. |
| 757 | `public final Map<String, Double> purchasesBySupplier` | Bought from each local supplier, by the supplier's key: the input tax credit is at THEIR rate. |
| 759 | `public double imports` | Bought from the world. |
| 761 | `public final Map<Good, Double> unitsSold` | Units sold and bought, per good, for the accounts and the screens. |
| 762 | `public final Map<Good, Double> unitsBought` |  |
| 770 | `public final Map<Good, Split> sold` | And the same two in MONEY, split home and abroad. |
| 771 | `public final Map<Good, Split> bought` |  |
| 790 | `public final Map<String, Double> otherInputs` | ...and the part of the input line that is NOT a good, by name. |
| 802 | `public double paidEarlier` | ...AND STOCK IT PAID FOR EARLIER (0.7.8): an input drawn this month out of stock the sector bought in an earlier month and paid cash for then - the builders' material from scrapped plant, booked at what they paid for ... |
| 807 | `public double salesToHouseholds` | Sold to households in particular - consumption, in the national accounts. |
| 855 | `private Ledger pending` |  |
| 947 | `private double purchasesLeft` | What the clearing lets it spend, what is left of it, and the share of each order it funds. |
| 948 | `private double purchaseShare` |  |
| 950 | `private double rPurchaseBudget` | The clearing's reading, for the screens and the playtest. |
| 951 | `private final Map<Good, Double> rForgoneUnits` |  |
| 952 | `private final Map<Good, Double> rForgoneValue` |  |
| 1011 | `private double rShelfShort, rShelfShortValue` | What the shelf could not sell this month (0.7.12 round 6): customers the counter and the staff could have served and the stock could not, in what the sector sells, and their value at its price. |
| 1100 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 1101 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 1103 | `public double localSales, exports, otherRevenue, salesToHouseholds` | The two halves of revenue, for the accounts. |
| 1105 | `public double localPurchases, imports` | The two halves of inputs, for the accounts and the audit. |
| 1106 | `public Map<String, Double> purchasesBySupplier` |  |
| 1113 | `public Map<Good, Split> sold` | Revenue and the cost of sales BY GOOD, each split home and abroad - what the income statement's two biggest lines open into. |
| 1114 | `public Map<Good, Split> bought` |  |
| 1121 | `public Map<String, Double> otherParts` | And the parts of otherRevenue that have names - the builders' work recognised and repairs billed. |
| 1127 | `public Map<String, Double> otherInputs` | ...and the parts of the INPUT line that are not a good: haulage on the shippers' books, fuel on the railway's. |
| 1129 | `public double paidEarlier` | The part of inputs drawn from stock paid for in an earlier month - see Ledger.paidEarlier. |
| 1146 | `private final Statement statement` |  |
| 1433 | `protected final Map<Good, Double> pantryUsedLastMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 1878 | **type** `public abstract class Sector` | One business in the city, and the template every sector extends. |

### IDENTITY AND DECLARATION (lines 75-135)

| line | len | member | says |
|---:|---:|---|---|
| 99 | 14 | `protected Sector(String key, String label, BuildingType group)` | "Construction", "Heavy Industry", "Mining" are what every save already calls the six; a new sector picks a name and keeps it for ever. |
| 114 | 1 | `protected final void makes(Good good)` |  |
| 115 | 1 | `protected final void uses(Good good)` |  |
| 118 | 4 | `protected final void pantry(Good good, double coverMonths)` | It keeps this many months of use of an input on hand, buying the difference each month. |
| 123 | 1 | `protected final void blurb(String text)` |  |
| 125 | 1 | `public final String key()` |  |
| 126 | 1 | `public final String label()` |  |
| 127 | 1 | `public final BuildingType group()` |  |
| 128 | 1 | `public final String blurb()` |  |
| 129 | 1 | `public final Set<Good> goodsMade()` |  |
| 130 | 1 | `public final Set<Good> goodsUsed()` |  |
| 131 | 1 | `public final boolean isMaker(Good g)` |  |
| 132 | 1 | `public final boolean isUser(Good g)` |  |
| 134 | 1 | `public final boolean hasPantry(Good g)` | True for a good it keeps on hand and restocks - a shelf, a larder, a fleet - and so, since 0.7.12 round 6, what a budget can cut (BUY ONLY WHAT IT CAN PAY FOR); false for a maker's input, consumed as it makes. |

### WIRING - set once by Sectors, through attach() (lines 136-155)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 4 | `void attach(BuildingManager buildings, Markets markets)` |  |
| 151 | 1 | `void attachGame(Game game)` |  |
| 153 | 1 | `public BuildingManager buildings()` |  |
| 154 | 1 | `public Markets markets()` |  |

### LABOUR (lines 156-198)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 4 | `public void updateJobFillRate(double[] fillRate)` | The fill rate per tier, as PopulationManager last set it. |
| 180 | 13 | `public void updateWages(double[] wagePerType, int[] posts)` | The wage bill, from the schedule and this sector's own posts. |
| 195 | 3 | `public int[] postsPerTier()` | Posts per tier, off its own buildings. |

### A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved (lines 199-336)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 25 | `public double staffableShare(BuildingsTemplate t)` | The share of a building's posts this city could actually fill today. |
| 293 | 5 | `public double getPayroll()` | What the staffed posts cost this month. |
| 304 | 1 | `protected double payrollScale()` | A sector that pays less than its full staffed payroll in a slack month says so here. |
| 307 | 6 | `public double[] getStaffedPayrollPerType()` | The staffed payroll by tier, for the banded wage tax. |
| 314 | 1 | `public double getAverageFill()` |  |
| 321 | 5 | `public int getPostsOffered()` | The posts its buildings offer, as the month's wage pass last counted them (0.7.4, for the sector list): the total the "Staffed" share on the operations page is a share of. |
| 333 | 3 | `public double getWorkers()` | The posts filled - its workers (0.7.4, for the sector list): the posts at the operations page's "Staffed" share, getAverageFill(), which is the filled posts over the posts, so the two cannot disagree. |

### UTILISATION (lines 337-378)

| line | len | member | says |
|---:|---:|---|---|
| 354 | 1 | `public void setEnergyRatio(double r)` |  |
| 355 | 1 | `public void setWaterRatio(double r)` |  |
| 356 | 1 | `public void setRoadRatio(double r)` |  |
| 357 | 1 | `public void setHealthRatio(double r)` |  |
| 358 | 1 | `public void setPricePerWatt(double p)` |  |
| 359 | 1 | `public void setPricePerWaterUnit(double p)` |  |
| 361 | 1 | `public void setElectricityConsumption(double kw)` | Draw at nameplate, off its own buildings. |
| 362 | 1 | `public void setWaterConsumption(double units)` |  |
| 364 | 1 | `public double getEnergyRatio()` |  |
| 365 | 1 | `public double getWaterRatio()` |  |
| 366 | 1 | `public double getRoadRatio()` |  |
| 367 | 1 | `public double getHealthRatio()` |  |
| 370 | 3 | `public double getOperatingRate()` | How much of nameplate actually runs: staffing times the five ratios. |
| 375 | 1 | `public double getElectricityCost()` | Charged for what was DELIVERED, not asked for - the utility books the same slice. |
| 376 | 1 | `public double getWaterCost()` |  |

### THE FLEET (2026-09-17) (lines 379-438)

### AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL (lines 439-556)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 1 | `public boolean isFleetKnown()` |  |
| 507 | 7 | `public double tonnesMoved()` | Tonnes a month this sector's standing plant moves, in and out. |
| 515 | 1 | `public double vansNeeded()` |  |
| 518 | 1 | `public double vanFleet()` | The vehicles it owns. |
| 531 | 8 | `public double getVanRatio()` | The fifth ratio. |
| 547 | 9 | `public void runFleet()` | A month of wear, and the seeding. |

### MONEY AND THE BILLS (lines 557-629)

| line | len | member | says |
|---:|---:|---|---|
| 573 | 1 | `public final double getCash()` |  |
| 574 | 1 | `public final void setCash(double cash)` |  |
| 575 | 1 | `public final void addCash(double amount)` |  |
| 577 | 1 | `public void setInterestExpense(double v)` |  |
| 578 | 1 | `public void setPropertyTaxExpense(double v)` |  |
| 579 | 1 | `public void setMaintenanceExpense(double v)` |  |
| 580 | 1 | `public void setTaxRate(double rate)` |  |
| 582 | 1 | `public double getInterestExpense()` |  |
| 583 | 1 | `public double getPropertyTaxExpense()` |  |
| 584 | 1 | `public double getMaintenanceExpense()` |  |
| 585 | 1 | `public double getTaxRate()` |  |
| 587 | 5 | `public void setBalanceSheetInputs(double land, double buildingsWorth, double bonds)` |  |
| 593 | 1 | `public double getLandValue()` |  |
| 594 | 1 | `public double getBuildingsValue()` |  |
| 595 | 1 | `public double getBondsPayable()` |  |
| 598 | 10 | `public double getInventoryValue()` | Stock on hand at the price it would fetch today, every good together. |
| 610 | 5 | `protected double priceOf(Good g)` | Today's price of a good, for valuing stock: the market's, or nothing for a seller-priced good. |
| 620 | 9 | `public BalanceSheet getBalanceSheet()` | Position as of right now - a balance sheet is an instant, an income statement a period. |

### STOCK (lines 630-650)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 1 | `public double getStock(Good g)` |  |
| 641 | 1 | `public double getPantry(Good g)` |  |
| 643 | 1 | `public void setStock(Good g, double units)` |  |
| 644 | 1 | `public void setPantry(Good g, double units)` |  |
| 647 | 3 | `public double getStockCapacity(Good g)` | Warehouse room for a good, off its buildings' `stock` field. |

### CAPACITY, off the buildings (lines 651-669)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 3 | `public double getCapacity(Good g)` | Nameplate output of a good a month, finished buildings only. |
| 661 | 3 | `public double getPipeline(Good g)` | ...and what is on site, which counts as supply for the planner. |
| 666 | 3 | `public double getInputAtCapacity(Good g)` | Input a good needs a month at nameplate, across its buildings. |

### THE MONTH'S PRODUCTION FIGURES, per output good (lines 670-715)

| line | len | member | says |
|---:|---:|---|---|
| 675 | 13 | **type** `public static final class Output` | What the month did with each good it makes. |
| 690 | 6 | **type** `public static final class Input` | What the month did with each good it uses. |
| 700 | 1 | `public Output output(Good g)` |  |
| 701 | 1 | `public Input input(Good g)` |  |
| 713 | 1 | `public double unitsExported(Good g)` | What CROSSED THE CITY BOUNDARY this month, in units, by good - read-only, so asking does not create a row. |
| 714 | 1 | `public double unitsImported(Good g)` |  |

### THE LEDGER (lines 716-891)

| line | len | member | says |
|---:|---:|---|---|
| 743 | 5 | **type** `public static final class Split` | One good's side of the month, in money, split by which side of the border it cleared on. |
| 746 | 1 | `public double total()` _(in Sector.Split)_ |  |
| 749 | 93 | **type** `public static final class Ledger` |  |
| 804 | 1 | `public Split soldOf(Good g)` _(in Sector.Ledger)_ |  |
| 805 | 1 | `public Split boughtOf(Good g)` _(in Sector.Ledger)_ |  |
| 809 | 5 | `public double purchases()` _(in Sector.Ledger)_ |  |
| 815 | 1 | `public double revenue()` _(in Sector.Ledger)_ |  |
| 817 | 9 | `void clear()` _(in Sector.Ledger)_ |  |
| 833 | 8 | `void scale(double s)` _(in Sector.Ledger)_ | AND THE PER-GOOD MONEY SCALES WITH EVERYTHING ELSE. |
| 844 | 10 | `public static Map<Good, Split> copyOf(Map<Good, Split> from)` | A deep copy, because a Split is mutable and the ledger is cleared under it. |
| 857 | 1 | `public Ledger pending()` |  |
| 860 | 14 | `protected void bookSale(Trade t)` | A sale of this sector's, booked. |
| 876 | 15 | `protected void bookPurchase(Trade t)` | A purchase of this sector's, booked. |

### BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) (lines 892-1090)

| line | len | member | says |
|---:|---:|---|---|
| 960 | 9 | `public void openPurchases(double budget, double orderValue)` | Opens the clearing's purchases: what it can pay for, against what its orders would come to at what a unit costs to bring in (GoodsMarket.landedPrice()). |
| 971 | 4 | `public void closePurchases()` | ...and closes it: a purchase outside the clearing is not read against it. |
| 977 | 1 | `public double purchaseShare()` | The share of every order for stock the budget funds this clearing: 1 when they all fit. |
| 979 | 1 | `public double purchasesLeft()` | What is left of what it can pay for, in money. |
| 982 | 8 | `void noteForgone(Good g, double units, double value)` | Units of an order it did not place because it could not pay for them, and what they would have cost. |
| 992 | 1 | `public double getPurchaseBudget()` | What the last clearing said it could pay for (infinite with nobody asking). |
| 994 | 1 | `public double getOrderValue()` | ...what its orders came to, before the budget. |
| 996 | 1 | `public double getPurchasesForgone()` | ...and what it did not buy for want of cash and credit, in money. |
| 998 | 1 | `public double getUnitsForgone(Good g)` | ...of one good, in units. |
| 1000 | 1 | `public double getPurchasesForgone(Good g)` | ...and in money. |
| 1002 | 1 | `public boolean wasPurchaseLimited()` | True when the last clearing's budget cut an order. |
| 1013 | 4 | `protected final void noteShelfShort(double units, double price)` |  |
| 1018 | 1 | `public double getShelfShort()` |  |
| 1019 | 1 | `public double getShelfShortValue()` |  |
| 1022 | 3 | `protected final void bookOtherRevenue(double amount)` | Revenue that is not a sale of a good, booked into the month. |
| 1050 | 5 | `public final void billForService(String supplier, String what, double amount)` | A SERVICE BOUGHT FROM ANOTHER BUSINESS IN THE CITY, billed by the business that performed it. |
| 1061 | 5 | `protected final void drawPaidStock(String what, double cost)` | An input drawn from stock the sector paid for in an earlier month, at what it paid (0.7.8): a cost this month, named, and no cash - see Ledger.paidEarlier. |
| 1085 | 5 | `protected final void bookImportedService(String what, double amount)` | ...and one bought from the WORLD: an import with no good behind it. |

### THE STATEMENT (lines 1091-1247)

| line | len | member | says |
|---:|---:|---|---|
| 1099 | 46 | **type** `public static final class Statement` | The month's figures, as the statement was struck. |
| 1131 | 13 | `void scale(double s)` _(in Sector.Statement)_ |  |
| 1148 | 1 | `public Statement statement()` |  |
| 1151 | 1 | `public double getNetIncome()` | Pre-tax, before the profit tax. |
| 1154 | 1 | `public double getProfitTax()` | What the city collects from it this month. |
| 1161 | 29 | `public void strike()` | Strikes the month WITHOUT the sales tax and without banking: the ledger is read into the statement, and the VAT is struck from these figures next, by SalesTaxLedger, and cannot be known while they are written. |
| 1199 | 19 | `public void bank(double salesTaxRemitted)` | ...and banks it, once the sales tax is known. |
| 1220 | 1 | `protected void afterBank()` | A sector with something to clear when its month is banked says so here. |
| 1226 | 21 | `public void restoreStatement(Statement saved)` | The struck month, put back on load, so the first month back reads the same as the one before it and the loss counter sees what it saw. |

### THE MONTH AT THE BOTTOM - HOOKS Markets CALLS (lines 1248-1318)

| line | len | member | says |
|---:|---:|---|---|
| 1275 | 4 | `protected double plannedDemand(Good g)` | What the city will want of a good this month, for planning output. |
| 1286 | 9 | `public double getPlannedOutput(Good g)` | What it will make of a stockable good this month for the home market: nameplate at today's rate, or what brings the stock to STOCK_MONTHS of demand, whichever is less. |
| 1301 | 6 | `public double getExportBoundOutput(Good g)` | Nameplate the city cannot eat, made for export instead - if the export price clears the marginal cost of running the line, which is the energy and water and nothing else: the staff are paid either way. |
| 1312 | 6 | `public double getCostPerUnit(Good g)` | Break-even per unit this month: everything the line costs over what it makes. |

### WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS (lines 1319-1568)

| line | len | member | says |
|---:|---:|---|---|
| 1352 | 19 | `protected double costShareOf(Good g)` |  |
| 1379 | 6 | `public double getMarginalCostPerUnit(Good g)` | What it costs to SELL a unit already made: the energy and water, and the inputs, and not the payroll, which is paid whether or not a unit leaves the shed. |
| 1387 | 8 | `protected double inputCostAtRate()` | What the month's inputs cost at the operating rate, at today's prices. |
| 1403 | 19 | `public double bid(Good g)` | What it wants of an input this month. |
| 1424 | 5 | `protected double pantryTarget(Good g)` | Units of a pantry good the sector aims to hold: months of recent use. |
| 1431 | 1 | `protected double recentUse(Good g)` | What it used of a pantry good last month. |
| 1443 | 19 | `public double offer(Good g, double price)` | Units it will release to the market at the price. |
| 1468 | 10 | `public void produceFlow(Good g)` | Lifts, brews or smelts a flow good for the month - what the makers bring to a market that has no stock behind it. |
| 1480 | 1 | `protected double groundLimit(Good g, double asked)` | A sector whose output is limited by what is in the ground says so here. |
| 1486 | 12 | `public void shipUnsoldFlow(Good g, GoodsMarket market)` | A flow good's unsold units, once the market has taken what it wants: shipped abroad at the export price, or lost. |
| 1513 | 23 | `public void produceStock(Good g, GoodsMarket market)` | Runs the month's production of a stockable good into the warehouse, after the market has taken what it wanted from last month's stock. |
| 1538 | 4 | `void takeFromStock(Good g, double units)` | Units of a good taken out of stock by a local sale or an export from the shed. |
| 1544 | 5 | `void receiveInput(Good g, double units)` | Units of an input received into the pantry, or consumed on the spot. |
| 1551 | 6 | `protected final void usePantry(Good g, double units)` | A pantry good used up this month, recorded for next month's cover. |
| 1564 | 1 | `public void sellOwnPriced(Markets markets, Game game)` | The seller-priced goods, sold: the shops to the households, the landlords' doors to the families. |
| 1567 | 1 | `public void endOfMonth(Game game)` | After every market has cleared and every good is made: anything the month still needs. |

### PLANNING - the decision to grow, and to shrink (lines 1569-1658)

| line | len | member | says |
|---:|---:|---|---|
| 1586 | 3 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | What this sector would like to build this month, or why not. |
| 1594 | 5 | `public Good planningGood()` | The good the generic planner sizes the sector by: its first stockable output, else its first output. |
| 1607 | 1 | `public double firstPlantUtilisation()` | The share of a plant's nameplate the city has to be taking, a month, before this sector sinks its FIRST plant. |
| 1616 | 1 | `public double visibleDemandOver(double months, EconomyManager economy)` | What the sector can SEE it will sell a month over the next `months`, when its customers' orders are on a book - a ceiling on the demand the maker's rule reads off the trend, since a trend read off a boom runs on after... |
| 1619 | 3 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one of its templates would clear a month, for the interest test. |
| 1637 | 7 | `public double[] retirementDemandAndCapacity(Game game)` | The demand the spare-capacity rule measures the sector against, and the capacity it measures. |
| 1646 | 4 | `public double unitsOf(BuildingsTemplate t)` | What one of these contributes to the measure the sector is judged on. |
| 1652 | 1 | `public boolean mayRetire(BuildingsTemplate t)` | Whether a holding of this template may be sold back this month. |
| 1655 | 3 | `public String noRetirementReason(boolean distress)` | Why nothing could be sold, when mayRetire() refused everything. |

### THE SCREENS (lines 1659-1815)

| line | len | member | says |
|---:|---:|---|---|
| 1668 | 9 | **type** `public record Line(Kind kind, String label, String value, Tone tone)` | One line of the operations page. |
| 1669 | 1 | **type** `public enum Kind` _(in Sector.Line)_ |  |
| 1670 | 1 | **type** `public enum Tone` _(in Sector.Line)_ |  |
| 1672 | 1 | `public static Line head(String text)` _(in Sector.Line)_ |  |
| 1673 | 1 | `public static Line note(String text)` _(in Sector.Line)_ |  |
| 1674 | 1 | `public static Line of(String label, String value)` _(in Sector.Line)_ |  |
| 1675 | 1 | `public static Line of(String label, String value, Tone tone)` _(in Sector.Line)_ |  |
| 1679 | 9 | `public String inputLabel()` | What the sector's direct-cost line is called on its income statement. |
| 1705 | 1 | `public Map<String, Double> otherRevenueParts()` | The parts of this sector's revenue that are not the sale of a good, named, for the lines inside an opened Revenue. |
| 1715 | 1 | `public Map<String, Double> otherInputParts()` | ...and the same for the cost line: what is in Inputs that is not a good. |
| 1735 | 5 | `protected Map<String, Double> nameOtherRevenue()` | ...and where those names come from, read off the sector's LIVE fields at the moment the month is struck. |
| 1741 | 74 | `public List<Line> operations(Game game)` |  |

### SAVE AND RESTORE (lines 1816-1950)

| line | len | member | says |
|---:|---:|---|---|
| 1821 | 18 | `public SectorState toState()` | Everything about this sector that a save has to carry. |
| 1840 | 47 | `public void restore(SectorState s)` |  |
| 1889 | 7 | `public void restoreBills(SectorState s)` | The bills of the month back, over whatever a rebuild re-derived. |
| 1898 | 1 | `protected void saveExtras(Map<String, Double> extras)` | A sector with state of its own - a price it walks, an order book - writes it here by name. |
| 1901 | 1 | `protected void restoreExtras(Map<String, Double> extras)` | ...and reads it back. |
| 1904 | 18 | `public void reset()` | Everything back to a founding sector. |
| 1923 | 1 | `protected void resetExtras()` |  |
| 1929 | 16 | `public void redenominate(double scale)` | Its money in the new unit. |
| 1946 | 1 | `protected void redenominateExtras(double scale)` |  |
| 1949 | 1 | `public String toString()` |  |


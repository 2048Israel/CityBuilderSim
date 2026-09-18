# Sector.java - 1,763 lines · 147 methods · 7 constants · model

`ham/citybuildersim/Sector.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (100), [Statement](Statement.md) (9), [Game](Game.md) (7), [GoodsMarket](GoodsMarket.md) (7), [Trade](Trade.md) (7), [SectorState](SectorState.md) (7), [Markets](Markets.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [BuildingType](BuildingType.md) (3), [BuildingManager](BuildingManager.md) (3), [BalanceSheet](BalanceSheet.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [WageBand](WageBand.md) (2), [JobType](JobType.md) (2), [Formats](Formats.md) (2), [PopulationManager](PopulationManager.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (50):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BooksCheck](BooksCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HeavyIndustry](HeavyIndustry.md), [HousingCheck](HousingCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [Materials](Materials.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [NewGameCheck](NewGameCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [ServicesScreen](ServicesScreen.md), [ShadowBasket](ShadowBasket.md), [SummaryScreen](SummaryScreen.md), [TaxPolicy](TaxPolicy.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 75 | IDENTITY AND DECLARATION |
| 135 | WIRING - set once by Sectors.attach() |
| 155 | LABOUR |
| 198 | A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved |
| 315 | UTILISATION |
| 357 | THE FLEET (2026-09-17) |
| 417 | · AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL |
| 535 | MONEY AND THE BILLS |
| 608 | STOCK |
| 629 | CAPACITY, off the buildings |
| 648 | THE MONTH'S PRODUCTION FIGURES, per output good |
| 694 | THE LEDGER |
| 912 | THE STATEMENT |
| 1061 | THE MONTH AT THE BOTTOM - HOOKS Markets CALLS |
| 1132 | WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS |
| 1382 | PLANNING - the decision to grow, and to shrink |
| 1472 | THE SCREENS |
| 1629 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1482 | `Sector.Line.Kind.HEAD` |  |
| 1482 | `Sector.Line.Kind.LINE` |  |
| 1482 | `Sector.Line.Kind.NOTE` |  |
| 1483 | `Sector.Line.Tone.NONE` |  |
| 1483 | `Sector.Line.Tone.GOOD` |  |
| 1483 | `Sector.Line.Tone.WARN` |  |
| 1483 | `Sector.Line.Tone.BAD` |  |
| 1483 | `Sector.Line.Tone.MUTED` |  |
| 1483 | `Sector.Line.Tone.HEAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 230 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 409 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 415 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 444 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 458 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 1074 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 1077 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

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
| 139 | `protected BuildingManager buildings` |  |
| 140 | `protected Markets markets` |  |
| 143 | `protected Game game` | The city, for the few hooks that need more than the buildings and the markets. |
| 160 | `protected final double[] wages` | Payroll per tier at full staffing: wage x posts. |
| 161 | `protected final int[] jobs` |  |
| 162 | `protected final double[] fill` |  |
| 165 | `protected double averageFill` | Share of its posts that are filled. |
| 328 | `protected double energyRatio` |  |
| 329 | `protected double electricity, water` |  |
| 330 | `protected double pricePerWatt, pricePerWaterUnit` |  |
| 473 | `protected boolean vansKnown` | Whether this sector's fleet is a fact about the sector rather than a fact about the version it was saved from. |
| 539 | `private double cash` |  |
| 542 | `private double interestExpense` | Set for the month at the top of it, before the statement runs. |
| 543 | `private double propertyTaxExpense` |  |
| 544 | `private double maintenanceExpense` |  |
| 547 | `private double taxRate` | The profit rate in force this month. |
| 549 | `private double landValue, buildingsValue, bondsPayable` |  |
| 613 | `protected final Map<Good, Double> stock` | Output on hand, per stockable good it makes. |
| 616 | `protected final Map<Good, Double> pantry` | Input on hand, per good it keeps a pantry of. |
| 654 | `public double capacity` | nameplate a month |
| 655 | `public double planned` | nameplate a month |
| 656 | `public double produced` | what it decided to make for home |
| 657 | `public double idled` | what went into stock or to market |
| 658 | `public double exportBound` | nameplate neither the city nor the world would take |
| 659 | `public double offered` | made straight for the ship |
| 660 | `public double withheld` | released to the market at the price |
| 661 | `public double soldLocal` | kept back below cost |
| 662 | `public double exported` | units taken by local buyers |
| 663 | `public double writtenOff` | units shipped, from the line or the shed |
| 664 | `public double costPerUnit` | stock lost to a demolished warehouse |
| 669 | `public double needed` | at this month's operating rate |
| 670 | `public double bid` | at this month's operating rate |
| 671 | `public double boughtLocal` | what it asked the market for |
| 672 | `public double imported` | filled at home |
| 675 | `protected final Map<Good, Output> outputs` |  |
| 676 | `protected final Map<Good, Input> inputs` |  |
| 722 | `public double atHome` |  |
| 723 | `public double abroad` |  |
| 729 | `public double localSales` | Sold to local buyers - sectors, households, the city - in money. |
| 731 | `public double exports` | Sold to the world. |
| 733 | `public double otherRevenue` | Revenue that is not a sale of a good: recognised building work, repairs billed. |
| 735 | `public final Map<String, Double> purchasesBySupplier` | Bought from each local supplier, by the supplier's key: the input tax credit is at THEIR rate. |
| 737 | `public double imports` | Bought from the world. |
| 739 | `public final Map<Good, Double> unitsSold` | Units sold and bought, per good, for the accounts and the screens. |
| 740 | `public final Map<Good, Double> unitsBought` |  |
| 748 | `public final Map<Good, Split> sold` | And the same two in MONEY, split home and abroad. |
| 749 | `public final Map<Good, Split> bought` |  |
| 768 | `public final Map<String, Double> otherInputs` | ...and the part of the input line that is NOT a good, by name. |
| 773 | `public double salesToHouseholds` | Sold to households in particular - consumption, in the national accounts. |
| 820 | `private Ledger pending` |  |
| 921 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 922 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 924 | `public double localSales, exports, otherRevenue, salesToHouseholds` | The two halves of revenue, for the accounts. |
| 926 | `public double localPurchases, imports` | The two halves of inputs, for the accounts and the audit. |
| 927 | `public Map<String, Double> purchasesBySupplier` |  |
| 934 | `public Map<Good, Split> sold` | Revenue and the cost of sales BY GOOD, each split home and abroad - what the income statement's two biggest lines open into. |
| 935 | `public Map<Good, Split> bought` |  |
| 942 | `public Map<String, Double> otherParts` | And the parts of otherRevenue that have names - the builders' work recognised and repairs billed. |
| 948 | `public Map<String, Double> otherInputs` | ...and the parts of the INPUT line that are not a good: haulage on the shippers' books, fuel on the railway's. |
| 964 | `private final Statement statement` |  |
| 1246 | `protected final Map<Good, Double> pantryUsedLastMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 1691 | **type** `public abstract class Sector` | One business in the city, and the template every sector extends. |

### IDENTITY AND DECLARATION (lines 75-134)

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
| 133 | 1 | `public final boolean hasPantry(Good g)` |  |

### WIRING - set once by Sectors.attach() (lines 135-154)

| line | len | member | says |
|---:|---:|---|---|
| 145 | 4 | `void attach(BuildingManager buildings, Markets markets)` |  |
| 150 | 1 | `void attachGame(Game game)` |  |
| 152 | 1 | `public BuildingManager buildings()` |  |
| 153 | 1 | `public Markets markets()` |  |

### LABOUR (lines 155-197)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 4 | `public void updateJobFillRate(double[] fillRate)` | The fill rate per tier, as PopulationManager last set it. |
| 179 | 13 | `public void updateWages(double[] wagePerType, int[] posts)` | The wage bill, from the schedule and this sector's own posts. |
| 194 | 3 | `public int[] postsPerTier()` | Posts per tier, off its own buildings. |

### A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved (lines 198-314)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 25 | `public double staffableShare(BuildingsTemplate t)` | The share of a building's posts this city could actually fill today. |
| 292 | 5 | `public double getPayroll()` | What the staffed posts cost this month. |
| 303 | 1 | `protected double payrollScale()` | A sector that pays less than its full staffed payroll in a slack month says so here. |
| 306 | 6 | `public double[] getStaffedPayrollPerType()` | The staffed payroll by tier, for the banded wage tax. |
| 313 | 1 | `public double getAverageFill()` |  |

### UTILISATION (lines 315-356)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 1 | `public void setEnergyRatio(double r)` |  |
| 333 | 1 | `public void setWaterRatio(double r)` |  |
| 334 | 1 | `public void setRoadRatio(double r)` |  |
| 335 | 1 | `public void setHealthRatio(double r)` |  |
| 336 | 1 | `public void setPricePerWatt(double p)` |  |
| 337 | 1 | `public void setPricePerWaterUnit(double p)` |  |
| 339 | 1 | `public void setElectricityConsumption(double kw)` | Draw at nameplate, off its own buildings. |
| 340 | 1 | `public void setWaterConsumption(double units)` |  |
| 342 | 1 | `public double getEnergyRatio()` |  |
| 343 | 1 | `public double getWaterRatio()` |  |
| 344 | 1 | `public double getRoadRatio()` |  |
| 345 | 1 | `public double getHealthRatio()` |  |
| 348 | 3 | `public double getOperatingRate()` | How much of nameplate actually runs: staffing times the five ratios. |
| 353 | 1 | `public double getElectricityCost()` | Charged for what was DELIVERED, not asked for - the utility books the same slice. |
| 354 | 1 | `public double getWaterCost()` |  |

### THE FLEET (2026-09-17) (lines 357-416)

### AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL (lines 417-534)

| line | len | member | says |
|---:|---:|---|---|
| 475 | 1 | `public boolean isFleetKnown()` |  |
| 485 | 7 | `public double tonnesMoved()` | Tonnes a month this sector's standing plant moves, in and out. |
| 493 | 1 | `public double vansNeeded()` |  |
| 496 | 1 | `public double vanFleet()` | The vehicles it owns. |
| 509 | 8 | `public double getVanRatio()` | The fifth ratio. |
| 525 | 9 | `public void runFleet()` | A month of wear, and the seeding. |

### MONEY AND THE BILLS (lines 535-607)

| line | len | member | says |
|---:|---:|---|---|
| 551 | 1 | `public final double getCash()` |  |
| 552 | 1 | `public final void setCash(double cash)` |  |
| 553 | 1 | `public final void addCash(double amount)` |  |
| 555 | 1 | `public void setInterestExpense(double v)` |  |
| 556 | 1 | `public void setPropertyTaxExpense(double v)` |  |
| 557 | 1 | `public void setMaintenanceExpense(double v)` |  |
| 558 | 1 | `public void setTaxRate(double rate)` |  |
| 560 | 1 | `public double getInterestExpense()` |  |
| 561 | 1 | `public double getPropertyTaxExpense()` |  |
| 562 | 1 | `public double getMaintenanceExpense()` |  |
| 563 | 1 | `public double getTaxRate()` |  |
| 565 | 5 | `public void setBalanceSheetInputs(double land, double buildingsWorth, double bonds)` |  |
| 571 | 1 | `public double getLandValue()` |  |
| 572 | 1 | `public double getBuildingsValue()` |  |
| 573 | 1 | `public double getBondsPayable()` |  |
| 576 | 10 | `public double getInventoryValue()` | Stock on hand at the price it would fetch today, every good together. |
| 588 | 5 | `protected double priceOf(Good g)` | Today's price of a good, for valuing stock: the market's, or nothing for a seller-priced good. |
| 598 | 9 | `public BalanceSheet getBalanceSheet()` | Position as of right now - a balance sheet is an instant, an income statement a period. |

### STOCK (lines 608-628)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 1 | `public double getStock(Good g)` |  |
| 619 | 1 | `public double getPantry(Good g)` |  |
| 621 | 1 | `public void setStock(Good g, double units)` |  |
| 622 | 1 | `public void setPantry(Good g, double units)` |  |
| 625 | 3 | `public double getStockCapacity(Good g)` | Warehouse room for a good, off its buildings' `stock` field. |

### CAPACITY, off the buildings (lines 629-647)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 3 | `public double getCapacity(Good g)` | Nameplate output of a good a month, finished buildings only. |
| 639 | 3 | `public double getPipeline(Good g)` | ...and what is on site, which counts as supply for the planner. |
| 644 | 3 | `public double getInputAtCapacity(Good g)` | Input a good needs a month at nameplate, across its buildings. |

### THE MONTH'S PRODUCTION FIGURES, per output good (lines 648-693)

| line | len | member | says |
|---:|---:|---|---|
| 653 | 13 | **type** `public static final class Output` | What the month did with each good it makes. |
| 668 | 6 | **type** `public static final class Input` | What the month did with each good it uses. |
| 678 | 1 | `public Output output(Good g)` |  |
| 679 | 1 | `public Input input(Good g)` |  |
| 691 | 1 | `public double unitsExported(Good g)` | What CROSSED THE CITY BOUNDARY this month, in units, by good - read-only, so asking does not create a row. |
| 692 | 1 | `public double unitsImported(Good g)` |  |

### THE LEDGER (lines 694-911)

| line | len | member | says |
|---:|---:|---|---|
| 721 | 5 | **type** `public static final class Split` | One good's side of the month, in money, split by which side of the border it cleared on. |
| 724 | 1 | `public double total()` _(in Sector.Split)_ |  |
| 727 | 80 | **type** `public static final class Ledger` |  |
| 770 | 1 | `public Split soldOf(Good g)` _(in Sector.Ledger)_ |  |
| 771 | 1 | `public Split boughtOf(Good g)` _(in Sector.Ledger)_ |  |
| 775 | 5 | `public double purchases()` _(in Sector.Ledger)_ |  |
| 781 | 1 | `public double revenue()` _(in Sector.Ledger)_ |  |
| 783 | 9 | `void clear()` _(in Sector.Ledger)_ |  |
| 799 | 7 | `void scale(double s)` _(in Sector.Ledger)_ | AND THE PER-GOOD MONEY SCALES WITH EVERYTHING ELSE. |
| 809 | 10 | `public static Map<Good, Split> copyOf(Map<Good, Split> from)` | A deep copy, because a Split is mutable and the ledger is cleared under it. |
| 822 | 1 | `public Ledger pending()` |  |
| 825 | 14 | `protected void bookSale(Trade t)` | A sale of this sector's, booked. |
| 841 | 11 | `protected void bookPurchase(Trade t)` | A purchase of this sector's, booked. |
| 854 | 3 | `protected final void bookOtherRevenue(double amount)` | Revenue that is not a sale of a good, booked into the month. |
| 882 | 5 | `public final void billForService(String supplier, String what, double amount)` | A SERVICE BOUGHT FROM ANOTHER BUSINESS IN THE CITY, billed by the business that performed it. |
| 906 | 5 | `protected final void bookImportedService(String what, double amount)` | ...and one bought from the WORLD: an import with no good behind it. |

### THE STATEMENT (lines 912-1060)

| line | len | member | says |
|---:|---:|---|---|
| 920 | 43 | **type** `public static final class Statement` | The month's figures, as the statement was struck. |
| 950 | 12 | `void scale(double s)` _(in Sector.Statement)_ |  |
| 966 | 1 | `public Statement statement()` |  |
| 969 | 1 | `public double getNetIncome()` | Pre-tax, before the profit tax. |
| 972 | 1 | `public double getProfitTax()` | What the city collects from it this month. |
| 979 | 28 | `public void strike()` | Strikes the month WITHOUT the sales tax and without banking: the ledger is read into the statement, and the VAT is struck from these figures next, by SalesTaxLedger, and cannot be known while they are written. |
| 1016 | 16 | `public void bank(double salesTaxRemitted)` | ...and banks it, once the sales tax is known. |
| 1034 | 1 | `protected void afterBank()` | A sector with something to clear when its month is banked says so here. |
| 1040 | 20 | `public void restoreStatement(Statement saved)` | The struck month, put back on load, so the first month back reads the same as the one before it and the loss counter sees what it saw. |

### THE MONTH AT THE BOTTOM - HOOKS Markets CALLS (lines 1061-1131)

| line | len | member | says |
|---:|---:|---|---|
| 1088 | 4 | `protected double plannedDemand(Good g)` | What the city will want of a good this month, for planning output. |
| 1099 | 9 | `public double getPlannedOutput(Good g)` | What it will make of a stockable good this month for the home market: nameplate at today's rate, or what brings the stock to STOCK_MONTHS of demand, whichever is less. |
| 1114 | 6 | `public double getExportBoundOutput(Good g)` | Nameplate the city cannot eat, made for export instead - if the export price clears the marginal cost of running the line, which is the energy and water and nothing else: the staff are paid either way. |
| 1125 | 6 | `public double getCostPerUnit(Good g)` | Break-even per unit this month: everything the line costs over what it makes. |

### WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS (lines 1132-1381)

| line | len | member | says |
|---:|---:|---|---|
| 1165 | 19 | `protected double costShareOf(Good g)` |  |
| 1192 | 6 | `public double getMarginalCostPerUnit(Good g)` | What it costs to SELL a unit already made: the energy and water, and the inputs, and not the payroll, which is paid whether or not a unit leaves the shed. |
| 1200 | 8 | `protected double inputCostAtRate()` | What the month's inputs cost at the operating rate, at today's prices. |
| 1216 | 19 | `public double bid(Good g)` | What it wants of an input this month. |
| 1237 | 5 | `protected double pantryTarget(Good g)` | Units of a pantry good the sector aims to hold: months of recent use. |
| 1244 | 1 | `protected double recentUse(Good g)` | What it used of a pantry good last month. |
| 1256 | 19 | `public double offer(Good g, double price)` | Units it will release to the market at the price. |
| 1281 | 10 | `public void produceFlow(Good g)` | Lifts, brews or smelts a flow good for the month - what the makers bring to a market that has no stock behind it. |
| 1293 | 1 | `protected double groundLimit(Good g, double asked)` | A sector whose output is limited by what is in the ground says so here. |
| 1299 | 12 | `public void shipUnsoldFlow(Good g, GoodsMarket market)` | A flow good's unsold units, once the market has taken what it wants: shipped abroad at the export price, or lost. |
| 1326 | 23 | `public void produceStock(Good g, GoodsMarket market)` | Runs the month's production of a stockable good into the warehouse, after the market has taken what it wanted from last month's stock. |
| 1351 | 4 | `void takeFromStock(Good g, double units)` | Units of a good taken out of stock by a local sale or an export from the shed. |
| 1357 | 5 | `void receiveInput(Good g, double units)` | Units of an input received into the pantry, or consumed on the spot. |
| 1364 | 6 | `protected final void usePantry(Good g, double units)` | A pantry good used up this month, recorded for next month's cover. |
| 1377 | 1 | `public void sellOwnPriced(Markets markets, Game game)` | The seller-priced goods, sold: the shops to the households, the landlords' doors to the families. |
| 1380 | 1 | `public void endOfMonth(Game game)` | After every market has cleared and every good is made: anything the month still needs. |

### PLANNING - the decision to grow, and to shrink (lines 1382-1471)

| line | len | member | says |
|---:|---:|---|---|
| 1399 | 3 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | What this sector would like to build this month, or why not. |
| 1407 | 5 | `public Good planningGood()` | The good the generic planner sizes the sector by: its first stockable output, else its first output. |
| 1420 | 1 | `public double firstPlantUtilisation()` | The share of a plant's nameplate the city has to be taking, a month, before this sector sinks its FIRST plant. |
| 1429 | 1 | `public double visibleDemandOver(double months, EconomyManager economy)` | What the sector can SEE it will sell a month over the next `months`, when its customers' orders are on a book - a ceiling on the demand the maker's rule reads off the trend, since a trend read off a boom runs on after... |
| 1432 | 3 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one of its templates would clear a month, for the interest test. |
| 1450 | 7 | `public double[] retirementDemandAndCapacity(Game game)` | The demand the spare-capacity rule measures the sector against, and the capacity it measures. |
| 1459 | 4 | `public double unitsOf(BuildingsTemplate t)` | What one of these contributes to the measure the sector is judged on. |
| 1465 | 1 | `public boolean mayRetire(BuildingsTemplate t)` | Whether a holding of this template may be sold back this month. |
| 1468 | 3 | `public String noRetirementReason(boolean distress)` | Why nothing could be sold, when mayRetire() refused everything. |

### THE SCREENS (lines 1472-1628)

| line | len | member | says |
|---:|---:|---|---|
| 1481 | 9 | **type** `public record Line(Kind kind, String label, String value, Tone tone)` | One line of the operations page. |
| 1482 | 1 | **type** `public enum Kind` _(in Sector.Line)_ |  |
| 1483 | 1 | **type** `public enum Tone` _(in Sector.Line)_ |  |
| 1485 | 1 | `public static Line head(String text)` _(in Sector.Line)_ |  |
| 1486 | 1 | `public static Line note(String text)` _(in Sector.Line)_ |  |
| 1487 | 1 | `public static Line of(String label, String value)` _(in Sector.Line)_ |  |
| 1488 | 1 | `public static Line of(String label, String value, Tone tone)` _(in Sector.Line)_ |  |
| 1492 | 9 | `public String inputLabel()` | What the sector's direct-cost line is called on its income statement. |
| 1518 | 1 | `public Map<String, Double> otherRevenueParts()` | The parts of this sector's revenue that are not the sale of a good, named, for the lines inside an opened Revenue. |
| 1528 | 1 | `public Map<String, Double> otherInputParts()` | ...and the same for the cost line: what is in Inputs that is not a good. |
| 1548 | 5 | `protected Map<String, Double> nameOtherRevenue()` | ...and where those names come from, read off the sector's LIVE fields at the moment the month is struck. |
| 1554 | 74 | `public List<Line> operations(Game game)` |  |

### SAVE AND RESTORE (lines 1629-1763)

| line | len | member | says |
|---:|---:|---|---|
| 1634 | 18 | `public SectorState toState()` | Everything about this sector that a save has to carry. |
| 1653 | 47 | `public void restore(SectorState s)` |  |
| 1702 | 7 | `public void restoreBills(SectorState s)` | The bills of the month back, over whatever a rebuild re-derived. |
| 1711 | 1 | `protected void saveExtras(Map<String, Double> extras)` | A sector with state of its own - a price it walks, an order book - writes it here by name. |
| 1714 | 1 | `protected void restoreExtras(Map<String, Double> extras)` | ...and reads it back. |
| 1717 | 18 | `public void reset()` | Everything back to a founding sector. |
| 1736 | 1 | `protected void resetExtras()` |  |
| 1742 | 16 | `public void redenominate(double scale)` | Its money in the new unit. |
| 1759 | 1 | `protected void redenominateExtras(double scale)` |  |
| 1762 | 1 | `public String toString()` |  |


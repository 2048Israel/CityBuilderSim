# Sector.java - 2,906 lines · 217 methods · 14 constants · model

`ham/citybuildersim/Sector.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (134), [WageBand](WageBand.md) (10), [SupplierCredit](SupplierCredit.md) (10), [Game](Game.md) (9), [GoodsMarket](GoodsMarket.md) (9), [Statement](Statement.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [Trade](Trade.md) (8), [SectorState](SectorState.md) (7), [Markets](Markets.md) (5), [BuildingManager](BuildingManager.md) (4), [JobType](JobType.md) (4), [BuildingType](BuildingType.md) (3), [BalanceSheet](BalanceSheet.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [PopulationManager](PopulationManager.md) (1), [Migration](Migration.md) (1), [Sectors](Sectors.md) (1), [Mortgage](Mortgage.md) (1), [OldMetal](OldMetal.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (101):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HousingCheck](HousingCheck.md), [Icons](Icons.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [Materials](Materials.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [Pieces](Pieces.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [ProcessingSector](ProcessingSector.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TaxPolicy](TaxPolicy.md), [TradeScreen](TradeScreen.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 75 | IDENTITY AND DECLARATION |
| 146 | WIRING - set once by Sectors, through attach() |
| 166 | LABOUR |
| 209 | A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved |
| 280 | EVERY PLANNER THAT BUILDS POSTS ASKS, AND THE FIFTH IT LEAVES IS FRICTION |
| 551 | UTILISATION |
| 602 | · THE POWER ITS WORKS MAKE THEMSELVES (0.7.112, batch I2; runs/spec- |
| 630 | · WHAT ITS WORKS BUY ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; |
| 669 | THE FLEET (2026-09-17) |
| 729 | · AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL |
| 772 | · AND THE DIESEL THEY BURN (0.7.83, batch O6; runs/spec-oil.md 2.5) |
| 914 | MONEY AND THE BILLS |
| 954 | · WHAT ITS BORROWING COST IT UP FRONT, EXPENSED AND DEDUCTED (0.7.102) |
| 1086 | STOCK |
| 1107 | CAPACITY, off the buildings |
| 1126 | THE MONTH'S PRODUCTION FIGURES, per output good |
| 1193 | THE MONTH'S TRADE ACROSS A SAVE (A1, 2026-10-05) |
| 1263 | THE LEDGER |
| 1461 | BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) |
| 1573 | ITS SUPPLIERS' CREDIT (0.7.44) |
| 1791 | THE STATEMENT |
| 2022 | THE MONTH AT THE BOTTOM - HOOKS Markets CALLS |
| 2097 | WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS |
| 2272 | CAPTIVE USE (0.7.112, batch I2; runs/spec-materials.md 2.7) |
| 2415 | PLANNING - the decision to grow, and to shrink |
| 2505 | THE SCREENS |
| 2723 | SAVE AND RESTORE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2515 | `Sector.Line.Kind.HEAD` |  |
| 2515 | `Sector.Line.Kind.LINE` |  |
| 2515 | `Sector.Line.Kind.NOTE` |  |
| 2516 | `Sector.Line.Tone.NONE` |  |
| 2516 | `Sector.Line.Tone.GOOD` |  |
| 2516 | `Sector.Line.Tone.WARN` |  |
| 2516 | `Sector.Line.Tone.BAD` |  |
| 2516 | `Sector.Line.Tone.MUTED` |  |
| 2516 | `Sector.Line.Tone.HEAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 241 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 331 | `Sector.Staffing.ANY` | `new Staffing(1, new double [ 0 ], new double [ 0 ], new double [ 0 ], new boo...` | Always staffable: a building with no posts, or nobody to ask. |
| 647 | `Sector.CONSUMABLES` | `"Consumables"` | The consumables' name on a sector's input line. |
| 721 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 727 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 756 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 770 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 784 | `Sector.LOADS_A_VAN_MONTH` | `TONNES_PER_VAN / Good.VANS.tonnesPerUnit()` | The loads a van-month carries: TONNES_PER_VAN at a van's own weight, three tonnes (Good.VANS.tonnesPerUnit()) - forty. |
| 787 | `Sector.KM_A_LOAD` | `50` | How far a load goes, there and back: fifty kilometres (runs/spec-oil.md 2.5, est.; to confirm). |
| 790 | `Sector.DIESEL_LITRES_PER_100_KM` | `12` | What a laden van or truck burns: twelve litres of diesel a hundred kilometres (runs/spec-oil.md 2.5, est.; to confirm). |
| 793 | `Sector.DIESEL_LITRES_A_VAN_MONTH` | `LOADS_A_VAN_MONTH * KM_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | ...so a van-month burns LOADS_A_VAN_MONTH x KM_A_LOAD x DIESEL_LITRES_PER_100_KM / 100 litres: 240. |
| 997 | `Sector.BORROWING_COST_MOST_MONTHS` | `Mortgage.MORTGAGE_AMORTIZATION_MONTHS` | The longest life a cost of borrowing is expensed over, in months: a mortgage's amortization, forty years (0.7.102, A16) - the clock's length. |
| 2035 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 2038 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |

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
| 150 | `protected BuildingManager buildings` |  |
| 151 | `protected Markets markets` |  |
| 154 | `protected Game game` | The city, for the few hooks that need more than the buildings and the markets. |
| 171 | `protected final double[] wages` | Payroll per tier at full staffing: wage x posts. |
| 172 | `protected final long[] jobs` |  |
| 173 | `protected final double[] fill` |  |
| 176 | `protected double averageFill` | Share of its posts that are filled. |
| 326 | `public final double share` | The share of its posts the city's spare workers could fill. |
| 328 | `public final WageBand unfillable` | A band it has posts in that nobody can fill (see passes()), or null. |
| 564 | `protected double energyRatio` |  |
| 565 | `protected double electricity, water` |  |
| 566 | `protected double pricePerWatt, pricePerWaterUnit` |  |
| 620 | `protected double powerSurplus` | kW its buildings make past their own draws, at nameplate - what it sells the grid. |
| 796 | `private double fleetDieselLitres, fleetDieselCost, fleetDieselImported` | The month's diesel for the fleet (0.7.83): the litres drawn, what they cost, and the part bought abroad. |
| 815 | `protected boolean vansKnown` | Whether this sector's fleet is a fact about the sector rather than a fact about the version it was saved from. |
| 918 | `private double cash` |  |
| 921 | `private double interestExpense` | Set for the month at the top of it, before the statement runs. |
| 922 | `private double propertyTaxExpense` |  |
| 923 | `private double maintenanceExpense` |  |
| 926 | `private double taxRate` | The profit rate in force this month. |
| 928 | `private double landValue, buildingsValue, bondsPayable` |  |
| 1000 | `private final double[] borrowingChargesLeft` | The month's charge by how many months it has still to run: index k, k months; nothing at 0. |
| 1091 | `protected final Map<Good, Double> stock` | Output on hand, per stockable good it makes. |
| 1094 | `protected final Map<Good, Double> pantry` | Input on hand, per good it keeps a pantry of. |
| 1132 | `public double capacity` | nameplate a month |
| 1133 | `public double planned` | nameplate a month |
| 1134 | `public double produced` | what it decided to make for home |
| 1135 | `public double idled` | what went into stock or to market |
| 1136 | `public double exportBound` | nameplate neither the city nor the world would take |
| 1137 | `public double offered` | made straight for the ship |
| 1138 | `public double withheld` | released to the market at the price |
| 1139 | `public double soldLocal` | kept back below cost |
| 1140 | `public double exported` | units taken by local buyers |
| 1141 | `public double writtenOff` | units shipped, from the line or the shed |
| 1142 | `public double costPerUnit` | stock lost to a demolished warehouse |
| 1143 | `public double captive` | the month's break-even, for the screen |
| 1148 | `public double needed` | at this month's operating rate |
| 1149 | `public double bid` | at this month's operating rate |
| 1150 | `public double boughtLocal` | what it asked the market for |
| 1151 | `public double imported` | filled at home |
| 1154 | `protected final Map<Good, Output> outputs` |  |
| 1155 | `protected final Map<Good, Input> inputs` |  |
| 1212 | `private final Map<Good, Double> carriedExports` | Units shipped in the month a save was taken, by good: read by unitsExported() until the first strike after the load. |
| 1215 | `private final Map<Good, Double> carriedImports` | ...and units landed, read by unitsImported(). |
| 1228 | `private boolean tradeToDerive` | True from the load of a save that carried the month's trade in money alone until deriveCarriedTrade() has read it. |
| 1291 | `public double atHome` |  |
| 1292 | `public double abroad` |  |
| 1298 | `public double localSales` | Sold to local buyers - sectors, households, the city - in money. |
| 1300 | `public double exports` | Sold to the world. |
| 1302 | `public double otherRevenue` | Revenue that is not a sale of a good: recognised building work, repairs billed. |
| 1304 | `public final Map<String, Double> purchasesBySupplier` | Bought from each local supplier, by the supplier's key: the input tax credit is at THEIR rate. |
| 1306 | `public double imports` | Bought from the world. |
| 1308 | `public final Map<Good, Double> unitsSold` | Units sold and bought, per good, for the accounts and the screens. |
| 1309 | `public final Map<Good, Double> unitsBought` |  |
| 1317 | `public final Map<Good, Split> sold` | And the same two in MONEY, split home and abroad. |
| 1318 | `public final Map<Good, Split> bought` |  |
| 1337 | `public final Map<String, Double> otherInputs` | ...and the part of the input line that is NOT a good, by name. |
| 1349 | `public double paidEarlier` | ...AND STOCK IT PAID FOR EARLIER (0.7.8): an input drawn this month out of stock the sector bought in an earlier month and paid cash for then - the builders' material from scrapped plant, booked at what they paid for ... |
| 1362 | `public final Map<String, Double> capitalBySupplier` | ...AND BUILDINGS IT BOUGHT FROM ANOTHER BUSINESS, by the supplier's key (0.7.19): the builders' contract work on its own premises and the material escalation on it, as the builders bill them. |
| 1367 | `public double salesToHouseholds` | Sold to households in particular - consumption, in the national accounts. |
| 1369 | `public double oldMetalSold` | The part of otherRevenue that is its old steel sold to the Scrap Yards (0.7.123, batch I4; sellOldMetal()), named apart at the strike. |
| 1421 | `private Ledger pending` |  |
| 1523 | `private double purchasesLeft` | What the clearing lets it spend, what is left of it, and the share of each order it funds - and of each order its suppliers' credit covers (0.7.44). |
| 1524 | `private double purchaseShare` |  |
| 1526 | `private double rPurchaseBudget` | The clearing's reading, for the screens and the playtest. |
| 1527 | `private final Map<Good, Double> rForgoneUnits` |  |
| 1528 | `private final Map<Good, Double> rForgoneValue` |  |
| 1673 | `private double rShelfShort, rShelfShortValue` | What the shelf could not sell this month (0.7.12 round 6): customers the counter and the staff could have served and the stock could not, in what the sector sells, and their value at its price. |
| 1800 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 1801 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 1803 | `public double localSales, exports, otherRevenue, salesToHouseholds` | The two halves of revenue, for the accounts. |
| 1805 | `public double localPurchases, imports` | The two halves of inputs, for the accounts and the audit. |
| 1806 | `public Map<String, Double> purchasesBySupplier` |  |
| 1813 | `public Map<Good, Split> sold` | Revenue and the cost of sales BY GOOD, each split home and abroad - what the income statement's two biggest lines open into. |
| 1814 | `public Map<Good, Split> bought` |  |
| 1821 | `public Map<String, Double> otherParts` | And the parts of otherRevenue that have names - the builders' work recognised and repairs billed. |
| 1827 | `public Map<String, Double> otherInputs` | ...and the parts of the INPUT line that are not a good: haulage on the shippers' books, fuel on the railway's. |
| 1829 | `public double paidEarlier` | The part of inputs drawn from stock paid for in an earlier month - see Ledger.paidEarlier. |
| 1831 | `public double borrowingCosts` | What its borrowing cost it up front, the month's share of each over its debt's life (0.7.102, A16): a cost before the profit tax, paid when it borrowed, so added back to the cash at bank(). |
| 1833 | `public Map<String, Double> capitalBySupplier` | Buildings bought from other businesses this month, by supplier (0.7.19) - see Ledger.capitalBySupplier. |
| 1841 | `public double capitalTaxCredit` | The sales tax credited back on them (0.7.19): inside the net the ledger settled, and NOT in salesTax - it was capitalised with the building, not expensed, so crediting it through the tax line would have made it profit... |
| 1851 | `public double powerSold` | What it sold the grid of the power its works make past their own draws (0.7.112, batch I2; spec-materials 2.8): the surplus at the tariff, IN REVENUE - so every line the books strike from revenue adds up with it - and... |
| 1871 | `private final Statement statement` |  |
| 2218 | `protected final Map<Good, Double> pantryUsedLastMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 2834 | **type** `public abstract class Sector` | One business in the city, and the template every sector extends. |

### IDENTITY AND DECLARATION (lines 75-145)

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
| 144 | 1 | `public boolean buysAhead(Good g)` | Whether its order of a good is for stock it keeps - cut, with the rest of its stock, to what it can pay for (BUY ONLY WHAT IT CAN PAY FOR) - rather than a maker's input bought whole (0.7.85): its pantry goods (hasPant... |

### WIRING - set once by Sectors, through attach() (lines 146-165)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 4 | `void attach(BuildingManager buildings, Markets markets)` |  |
| 161 | 1 | `void attachGame(Game game)` |  |
| 163 | 1 | `public BuildingManager buildings()` |  |
| 164 | 1 | `public Markets markets()` |  |

### LABOUR (lines 166-208)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 4 | `public void updateJobFillRate(double[] fillRate)` | The fill rate per tier, as PopulationManager last set it. |
| 190 | 13 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill, from the schedule and this sector's own posts. |
| 205 | 3 | `public long[] postsPerTier()` | Posts per tier, off its own buildings. |

### A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved (lines 209-279)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 3 | `public double staffableShare(BuildingsTemplate t)` | The share of a building's posts this city could actually fill today. |

### EVERY PLANNER THAT BUILDS POSTS ASKS, AND THE FIFTH IT LEAVES IS FRICTION (lines 280-550)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 56 | **type** `public static final class Staffing` | Whether the city could staff one of a building, and if not, why not (0.7.18). |
| 339 | 8 | `Staffing(double share, double[] wanted, double[] spare, double[] above, boolean[] comeFor)` _(in Sector.Staffing)_ |  |
| 357 | 3 | `static boolean nobodyCanFill(double spare, double workersAbove, boolean migrantsComeForIt)` _(in Sector.Staffing)_ | THE ONE DEFINITION OF "NOBODY CAN FILL" (Jerus, "Truly unfillable only"): "A band counts as 'nobody can fill' only when it has no spare workers, nobody above who can step down into it, and no migrants who come for it.... |
| 366 | 3 | `public boolean passes()` _(in Sector.Staffing)_ | Staffable enough to order: MIN_STAFFABLE_TO_ORDER of its posts fillable from spare workers, and none of the rest in a band nobody can fill. |
| 371 | 8 | `public String why(String building)` _(in Sector.Staffing)_ | Why not, as the advisor's hold reason, for a building of this name. |
| 382 | 8 | `static String postsWord(WageBand band)` | What the advisor calls a band's posts. |
| 392 | 3 | `public Staffing staffing(BuildingsTemplate t)` | The staffing test every planner that builds posts asks before ordering one (0.7.18); see above. |
| 405 | 9 | `public int staffableCount(BuildingsTemplate t, int wanted)` | The largest order of up to {@code wanted} of these that still passes, counted as one building of that many times the posts; 0 when not even one does (0.7.18). |
| 416 | 44 | `public Staffing staffing(BuildingsTemplate t, int count)` | The same test for {@code count} of these at once. |
| 480 | 5 | `public double getPayroll()` | What the staffed posts cost this month: each job type's wage for the posts of that type actually filled. |
| 492 | 5 | `public double getPayrollAtFullStaffing()` | The same bill with every post it offers filled - what the unfilled posts would cost as well, for the operations page's note (0.7.17); the builders' laid-off posts are not offered and not in it. |
| 514 | 5 | `public double[] getStaffedPayrollPerType()` | The staffed payroll by tier, for the banded wage tax: each tier's wage bill at that tier's own fill (0.7.17 - it was the average fill, see getPayroll()). |
| 520 | 1 | `public double getAverageFill()` |  |
| 527 | 5 | `public long getPostsOffered()` | The posts its buildings offer, as the month's wage pass last counted them (0.7.4, for the sector list): the total the "Staffed" share on the operations page is a share of. |
| 539 | 1 | `public long[] postsOfferedPerTier()` | ...per job type: its buildings' posts (postsPerTier()) less any it has laid off this month - only the builders do (0.7.17; sectors .Construction, THE CREWS THE WORK NEEDS). |
| 547 | 3 | `public double getWorkers()` | The posts filled - its workers (0.7.4, for the sector list): the posts at the operations page's "Staffed" share, getAverageFill(), which is the filled posts over the posts, so the two cannot disagree. |

### UTILISATION (lines 551-601)

| line | len | member | says |
|---:|---:|---|---|
| 568 | 1 | `public void setEnergyRatio(double r)` |  |
| 569 | 1 | `public void setWaterRatio(double r)` |  |
| 570 | 1 | `public void setRoadRatio(double r)` |  |
| 571 | 1 | `public void setHealthRatio(double r)` |  |
| 572 | 1 | `public void setPricePerWatt(double p)` |  |
| 573 | 1 | `public void setPricePerWaterUnit(double p)` |  |
| 575 | 1 | `public void setElectricityConsumption(double kw)` | Draw at nameplate, off its own buildings. |
| 576 | 1 | `public void setWaterConsumption(double units)` |  |
| 578 | 1 | `public double getEnergyRatio()` |  |
| 579 | 1 | `public double getWaterRatio()` |  |
| 580 | 1 | `public double getRoadRatio()` |  |
| 581 | 1 | `public double getHealthRatio()` |  |
| 584 | 3 | `public int buildingsStanding()` | How many of its buildings stand, finished (0.7.30): none is "no plant standing" on its pages, not a rate. |
| 589 | 3 | `public int buildingsOnSite()` | ...and how many are on site, for anyone's order (0.7.30). |
| 594 | 3 | `public double getOperatingRate()` | How much of nameplate actually runs: staffing times the five ratios. |
| 599 | 1 | `public double getElectricityCost()` | Charged for what was DELIVERED, not asked for - the utility books the same slice. |
| 600 | 1 | `public double getWaterCost()` |  |

### THE POWER ITS WORKS MAKE THEMSELVES (0.7.112, batch I2; runs/spec- (lines 602-629)

| line | len | member | says |
|---:|---:|---|---|
| 622 | 1 | `public void setPowerSurplus(double kw)` |  |
| 625 | 1 | `public double getPowerSurplus()` | kW its buildings make past their own draws (0 for every sector until a works makes power). |
| 628 | 1 | `public double getPowerSoldValue()` | What the surplus sells for a month, at the tariff: nothing with none. |

### WHAT ITS WORKS BUY ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; (lines 630-668)

| line | len | member | says |
|---:|---:|---|---|
| 650 | 3 | `public double getConsumablesAtNameplate()` | Its buildings' consumables a month at nameplate, thousands of the world's money: nothing with none standing. |
| 655 | 6 | `public double consumablesBill()` | What the month's consumables cost it, in the city's money: their nameplate at the operating rate and the exchange factor; nothing with none, or no markets to read the factor off. |
| 663 | 4 | `public void buyConsumables()` | The month's consumables, bought from the world (above): an import named CONSUMABLES on the input line. |

### THE FLEET (2026-09-17) (lines 669-728)

### AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL (lines 729-771)

### AND THE DIESEL THEY BURN (0.7.83, batch O6; runs/spec-oil.md 2.5) (lines 772-913)

| line | len | member | says |
|---:|---:|---|---|
| 798 | 1 | `public double getFleetDieselLitres()` |  |
| 799 | 1 | `public double getFleetDieselCost()` |  |
| 800 | 1 | `public double getFleetDieselImported()` |  |
| 817 | 1 | `public boolean isFleetKnown()` |  |
| 827 | 20 | `public double tonnesMoved()` | Tonnes a month this sector's standing plant moves, in and out. |
| 848 | 1 | `public double vansNeeded()` |  |
| 851 | 1 | `public double vanFleet()` | The vehicles it owns. |
| 864 | 8 | `public double getVanRatio()` | The fifth ratio. |
| 882 | 3 | `public void runFleet()` | A month of wear, and the seeding. |
| 895 | 18 | `public void runFleet(Sectors sectors)` | ...and the month's diesel (0.7.83, batch O6; spec-oil 2.5): the van-months the sector works this month - what its standing plant moves at the operating rate, over TONNES_PER_VAN, the same measure its fleet is sized by... |

### MONEY AND THE BILLS (lines 914-953)

| line | len | member | says |
|---:|---:|---|---|
| 930 | 1 | `public final double getCash()` |  |
| 931 | 1 | `public final void setCash(double cash)` |  |
| 932 | 1 | `public final void addCash(double amount)` |  |
| 934 | 1 | `public void setInterestExpense(double v)` |  |
| 935 | 1 | `public void setPropertyTaxExpense(double v)` |  |
| 936 | 1 | `public void setMaintenanceExpense(double v)` |  |
| 937 | 1 | `public void setTaxRate(double rate)` |  |
| 939 | 1 | `public double getInterestExpense()` |  |
| 940 | 1 | `public double getPropertyTaxExpense()` |  |
| 941 | 1 | `public double getMaintenanceExpense()` |  |
| 942 | 1 | `public double getTaxRate()` |  |
| 944 | 5 | `public void setBalanceSheetInputs(double land, double buildingsWorth, double bonds)` |  |
| 950 | 1 | `public double getLandValue()` |  |
| 951 | 1 | `public double getBuildingsValue()` |  |
| 952 | 1 | `public double getBondsPayable()` |  |

### WHAT ITS BORROWING COST IT UP FRONT, EXPENSED AND DEDUCTED (0.7.102) (lines 954-1085)

| line | len | member | says |
|---:|---:|---|---|
| 1003 | 5 | `public void deferBorrowingCosts(double cost, int lifeMonths)` | What its borrowing cost it up front this month on debts of this life, in months: expensed in equal months from the next strike. |
| 1010 | 7 | `public double getBorrowingCostsToExpense()` | What its borrowing cost it up front and is still to expense: the sheet's asset (SectorBooks), as of now. |
| 1019 | 10 | `private double expenseBorrowingCosts()` | The strike's charge: every charge with a month to run, then each a month nearer its end. |
| 1031 | 10 | `public double getInventoryValue()` | Stock on hand at the price it would fetch today, every good together. |
| 1048 | 10 | `public Map<Good, double[]> stockAtPrices()` | Its stock and its pantry by good: the units, and the price each is valued at today - the two getInventoryValue() multiplies (0.7.75, the sector statements' R6, where the books price last month's stock at this month's ... |
| 1060 | 1 | `public double stockPrice(Good g)` | The price a good of its stock is valued at today (priceOf()): for last month's stock at this month's prices (R6). |
| 1063 | 5 | `protected double priceOf(Good g)` | Today's price of a good, for valuing stock: the market's, or nothing for a seller-priced good. |
| 1073 | 12 | `public BalanceSheet getBalanceSheet()` | Position as of right now - a balance sheet is an instant, an income statement a period. |

### STOCK (lines 1086-1106)

| line | len | member | says |
|---:|---:|---|---|
| 1096 | 1 | `public double getStock(Good g)` |  |
| 1097 | 1 | `public double getPantry(Good g)` |  |
| 1099 | 1 | `public void setStock(Good g, double units)` |  |
| 1100 | 1 | `public void setPantry(Good g, double units)` |  |
| 1103 | 3 | `public double getStockCapacity(Good g)` | Warehouse room for a good, off its buildings' `stock` field. |

### CAPACITY, off the buildings (lines 1107-1125)

| line | len | member | says |
|---:|---:|---|---|
| 1112 | 3 | `public double getCapacity(Good g)` | Nameplate output of a good a month, finished buildings only. |
| 1117 | 3 | `public double getPipeline(Good g)` | ...and what is on site, which counts as supply for the planner. |
| 1122 | 3 | `public double getInputAtCapacity(Good g)` | Input a good needs a month at nameplate, across its buildings. |

### THE MONTH'S PRODUCTION FIGURES, per output good (lines 1126-1192)

| line | len | member | says |
|---:|---:|---|---|
| 1131 | 14 | **type** `public static final class Output` | What the month did with each good it makes. |
| 1147 | 6 | **type** `public static final class Input` | What the month did with each good it uses. |
| 1157 | 1 | `public Output output(Good g)` |  |
| 1158 | 1 | `public Input input(Good g)` |  |
| 1161 | 1 | `public Output outputRow(Good g)` | The same rows read-only (0.7.30, for SectorFlow): null when the month has none for the good, and asking makes none. |
| 1162 | 1 | `public Input inputRow(Good g)` |  |
| 1179 | 6 | `public double unitsExported(Good g)` | What CROSSED THE CITY BOUNDARY this month, in units, by good - read-only, so asking does not create a row. |
| 1186 | 6 | `public double unitsImported(Good g)` |  |

### THE MONTH'S TRADE ACROSS A SAVE (A1, 2026-10-05) (lines 1193-1262)

| line | len | member | says |
|---:|---:|---|---|
| 1218 | 8 | `private static void carry(Map<String, Double> saved, Map<Good, Double> into)` | A save's units by good name into a carried map; a good this build does not know, or nothing, is left out. |
| 1247 | 15 | `public void deriveCarriedTrade()` | ...AND FROM A SAVE BEFORE THE UNITS (0.7.63): the units out of the money. |

### THE LEDGER (lines 1263-1460)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 5 | **type** `public static final class Split` | One good's side of the month, in money, split by which side of the border it cleared on. |
| 1293 | 1 | `public double total()` _(in Sector.Split)_ |  |
| 1296 | 112 | **type** `public static final class Ledger` |  |
| 1364 | 1 | `public Split soldOf(Good g)` _(in Sector.Ledger)_ |  |
| 1365 | 1 | `public Split boughtOf(Good g)` _(in Sector.Ledger)_ |  |
| 1371 | 5 | `public double purchases()` _(in Sector.Ledger)_ |  |
| 1377 | 1 | `public double revenue()` _(in Sector.Ledger)_ |  |
| 1379 | 11 | `void clear()` _(in Sector.Ledger)_ |  |
| 1397 | 10 | `void scale(double s)` _(in Sector.Ledger)_ | AND THE PER-GOOD MONEY SCALES WITH EVERYTHING ELSE. |
| 1410 | 10 | `public static Map<Good, Split> copyOf(Map<Good, Split> from)` | A deep copy, because a Split is mutable and the ledger is cleared under it. |
| 1423 | 1 | `public Ledger pending()` |  |
| 1426 | 14 | `protected void bookSale(Trade t)` | A sale of this sector's, booked. |
| 1442 | 18 | `protected void bookPurchase(Trade t)` | A purchase of this sector's, booked. |

### BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) (lines 1461-1572)

| line | len | member | says |
|---:|---:|---|---|
| 1536 | 3 | `public void openPurchases(double budget, double orderValue)` | Opens the clearing's purchases: what it can pay for, against what its orders would come to at what a unit costs to bring in (GoodsMarket.landedPrice()). |
| 1548 | 16 | `public void openPurchases(double budget, double orderValue, double coveredValue)` | ...and with what the orders its suppliers' credit covers would come to (0.7.44; SupplierCredit), when it has one. |
| 1566 | 6 | `public void closePurchases()` | ...and closes it: a purchase outside the clearing is not read against it. |

### ITS SUPPLIERS' CREDIT (0.7.44) (lines 1573-1790)

| line | len | member | says |
|---:|---:|---|---|
| 1586 | 1 | `public SupplierCredit supplierCredit()` | The ledger of what this sector owes its suppliers for stock they let it have on credit, or null for a sector whose suppliers are paid at the strike. |
| 1589 | 4 | `public double getTradePayable()` | What it owes its suppliers now: the next strike repays it. |
| 1595 | 9 | `public double getTradeReceivable()` | What the buyers it supplied owe it now, for stock it let them have on credit: the next strike collects it. |
| 1606 | 4 | `public double getTradeCreditTaken()` | What its suppliers' credit added to its till at the last strike: the stock it took on credit (what it repaid there is getTradeCreditRepaid()). |
| 1612 | 4 | `public double getTradeCreditRepaid()` | ...what it repaid there. |
| 1623 | 9 | `public double getTradeCreditCash()` | What trade credit moved its till by at the last strike, net, in: the credit it took less what it repaid, as a buyer; what it was repaid less what it let its buyers have, as a supplier. |
| 1634 | 1 | `public double purchaseShare()` | The share of every order for stock the budget funds this clearing: 1 when they all fit. |
| 1636 | 4 | `public double purchaseShare(Good g)` | ...of an order for this good: more, for one its suppliers' credit covers (0.7.44). |
| 1641 | 1 | `public double purchasesLeft()` | What is left of what it can pay for, in money. |
| 1644 | 8 | `void noteForgone(Good g, double units, double value)` | Units of an order it did not place because it could not pay for them, and what they would have cost. |
| 1654 | 1 | `public double getPurchaseBudget()` | What the last clearing said it could pay for (infinite with nobody asking). |
| 1656 | 1 | `public double getOrderValue()` | ...what its orders came to, before the budget. |
| 1658 | 1 | `public double getPurchasesForgone()` | ...and what it did not buy for want of cash and credit, in money. |
| 1660 | 1 | `public double getUnitsForgone(Good g)` | ...of one good, in units. |
| 1662 | 1 | `public double getPurchasesForgone(Good g)` | ...and in money. |
| 1664 | 1 | `public boolean wasPurchaseLimited()` | True when the last clearing's budget cut an order. |
| 1675 | 4 | `protected final void noteShelfShort(double units, double price)` |  |
| 1680 | 1 | `public double getShelfShort()` |  |
| 1681 | 1 | `public double getShelfShortValue()` |  |
| 1684 | 3 | `protected final void bookOtherRevenue(double amount)` | Revenue that is not a sale of a good, booked into the month. |
| 1696 | 5 | `public final void sellOldMetal(double amount)` | ...and its old steel, sold to the Scrap Yards (0.7.123, batch I4; OldMetal): other revenue like any, the yards' input from it at the same strike (they bill it to themselves - billForService() - so the sales tax nets a... |
| 1708 | 3 | `protected final void bookRevenueRefund(double amount)` | ...and a price given back (0.7.19): the builders' refund of material escalation to an owner whose material cost less when it was drawn than its quote allowed. |
| 1736 | 5 | `public final void billForService(String supplier, String what, double amount)` | A SERVICE BOUGHT FROM ANOTHER BUSINESS IN THE CITY, billed by the business that performed it. |
| 1749 | 4 | `public final void recordCapitalPurchase(String supplier, double amount)` | A building bought from another business in the city (0.7.19): the builders' work on this sector's own premises, billed as they do it, and any escalation on it - negative for a refund. |
| 1759 | 5 | `protected final void drawPaidStock(String what, double cost)` | An input drawn from stock the sector paid for in an earlier month, at what it paid (0.7.8): a cost this month, named, and no cash - see Ledger.paidEarlier. |
| 1785 | 5 | `protected final void bookImportedService(String what, double amount)` | ...AND ONE AGAIN SINCE 0.7.91 (batch O10; spec-oil 2.7): the shuttle tankers that take a platform's crude ashore, a service the world's ships sell with no good behind it. |

### THE STATEMENT (lines 1791-2021)

| line | len | member | says |
|---:|---:|---|---|
| 1799 | 71 | **type** `public static final class Statement` | The month's figures, as the statement was struck. |
| 1853 | 16 | `void scale(double s)` _(in Sector.Statement)_ |  |
| 1873 | 1 | `public Statement statement()` |  |
| 1876 | 1 | `public double getNetIncome()` | Pre-tax, before the profit tax. |
| 1879 | 1 | `public double getProfitTax()` | What the city collects from it this month. |
| 1886 | 45 | `public void strike()` | Strikes the month WITHOUT the sales tax and without banking: the ledger is read into the statement, and the VAT is struck from these figures next, by SalesTaxLedger, and cannot be known while they are written. |
| 1940 | 3 | `public void bank(double salesTaxRemitted)` | ...and banks it, once the sales tax is known. |
| 1950 | 30 | `public void bank(double salesTaxRemitted, double capitalTaxCredit)` | ...with the part of that net which is the tax credited back on buildings it bought (0.7.19): remitted net of it, but charged on the statement without it, and handed to the cash apart - see Statement.capitalTaxCredit. |
| 1982 | 1 | `protected void afterBank()` | A sector with something to clear when its month is banked says so here. |
| 1989 | 1 | `protected void beforeBank()` | ...and one with something to keep of the month's rows before they are cleared (A5, 0.7.46): called first in bank(), while every row is still the month being struck. |
| 1995 | 26 | `public void restoreStatement(Statement saved)` | The struck month, put back on load, so the first month back reads the same as the one before it and the loss counter sees what it saw. |

### THE MONTH AT THE BOTTOM - HOOKS Markets CALLS (lines 2022-2096)

| line | len | member | says |
|---:|---:|---|---|
| 2049 | 4 | `protected double plannedDemand(Good g)` | What the city will want of a good this month, for planning output. |
| 2060 | 11 | `public double getPlannedOutput(Good g)` | What it will make of a stockable good this month for the home market: nameplate at today's rate, or what brings the stock to STOCK_MONTHS of demand, whichever is less. |
| 2077 | 8 | `public double getExportBoundOutput(Good g)` | Nameplate the city cannot eat, made for export instead - if the export price clears the marginal cost of running the line, which is the energy and water and nothing else: the staff are paid either way. |
| 2090 | 6 | `public double getCostPerUnit(Good g)` | Break-even per unit this month: everything the line costs over what it makes. |

### WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS (lines 2097-2271)

| line | len | member | says |
|---:|---:|---|---|
| 2130 | 19 | `protected double costShareOf(Good g)` |  |
| 2157 | 6 | `public double getMarginalCostPerUnit(Good g)` | What it costs to SELL a unit already made: the energy and water, and the inputs, and not the payroll, which is paid whether or not a unit leaves the shed. |
| 2165 | 13 | `protected double inputCostAtRate()` | What the month's inputs cost at the operating rate, at today's prices. |
| 2186 | 21 | `public double bid(Good g)` | What it wants of an input this month. |
| 2209 | 5 | `protected double pantryTarget(Good g)` | Units of a pantry good the sector aims to hold: months of recent use. |
| 2216 | 1 | `protected double recentUse(Good g)` | What it used of a pantry good last month. |
| 2228 | 19 | `public double offer(Good g, double price)` | Units it will release to the market at the price. |
| 2253 | 15 | `public void produceFlow(Good g)` | Lifts, brews or smelts a flow good for the month - what the makers bring to a market that has no stock behind it. |
| 2270 | 1 | `protected double groundLimit(Good g, double asked)` | A sector whose output is limited by what is in the ground says so here. |

### CAPTIVE USE (0.7.112, batch I2; runs/spec-materials.md 2.7) (lines 2272-2414)

| line | len | member | says |
|---:|---:|---|---|
| 2296 | 1 | `public boolean captive(Good g)` | Whether it uses what it makes of a good first, itself (CAPTIVE USE): false here; sectors.ProcessingSector says yes for every good it both makes and uses. |
| 2304 | 9 | `public double captiveUnits(Good g)` | The units of a captive good it makes and uses itself this month: its need at the operating rate, as far as what it makes at the rate covers it - for a flow good what it lifted, after the ground (produceFlow() strikes ... |
| 2318 | 12 | `public void shipUnsoldFlow(Good g, GoodsMarket market)` | A flow good's unsold units, once the market has taken what it wants: shipped abroad at the export price, or lost. |
| 2345 | 28 | `public void produceStock(Good g, GoodsMarket market)` | Runs the month's production of a stockable good into the warehouse, after the market has taken what it wanted from last month's stock. |
| 2375 | 4 | `void takeFromStock(Good g, double units)` | Units of a good taken out of stock by a local sale or an export from the shed. |
| 2381 | 5 | `void receiveInput(Good g, double units)` | Units of an input received into the pantry, or consumed on the spot. |
| 2388 | 6 | `protected final void usePantry(Good g, double units)` | A pantry good used up this month, recorded for next month's cover. |
| 2401 | 1 | `public void sellOwnPriced(Markets markets, Game game)` | The seller-priced goods, sold: the shops to the households, the landlords' doors to the families. |
| 2404 | 1 | `public void endOfMonth(Game game)` | After every market has cleared and every good is made: anything the month still needs. |
| 2413 | 1 | `protected void afterClearing(Good g)` | One good's market has cleared this month and its fills are booked (0.7.85; Markets.clear()), told to each of its buyers: a sector whose month reads what it bought says so here - the refiners' run, held to their crude ... |

### PLANNING - the decision to grow, and to shrink (lines 2415-2504)

| line | len | member | says |
|---:|---:|---|---|
| 2432 | 3 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | What this sector would like to build this month, or why not. |
| 2440 | 5 | `public Good planningGood()` | The good the generic planner sizes the sector by: its first stockable output, else its first output. |
| 2453 | 1 | `public double firstPlantUtilisation()` | The share of a plant's nameplate the city has to be taking, a month, before this sector sinks its FIRST plant. |
| 2462 | 1 | `public double visibleDemandOver(double months, EconomyManager economy)` | What the sector can SEE it will sell a month over the next `months`, when its customers' orders are on a book - a ceiling on the demand the maker's rule reads off the trend, since a trend read off a boom runs on after... |
| 2465 | 3 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one of its templates would clear a month, for the interest test. |
| 2483 | 7 | `public double[] retirementDemandAndCapacity(Game game)` | The demand the spare-capacity rule measures the sector against, and the capacity it measures. |
| 2492 | 4 | `public double unitsOf(BuildingsTemplate t)` | What one of these contributes to the measure the sector is judged on. |
| 2498 | 1 | `public boolean mayRetire(BuildingsTemplate t)` | Whether a holding of this template may be sold back this month. |
| 2501 | 3 | `public String noRetirementReason(boolean distress)` | Why nothing could be sold, when mayRetire() refused everything. |

### THE SCREENS (lines 2505-2722)

| line | len | member | says |
|---:|---:|---|---|
| 2514 | 11 | **type** `public record Line(Kind kind, String label, String value, Tone tone)` | One line of the operations page. |
| 2515 | 1 | **type** `public enum Kind` _(in Sector.Line)_ |  |
| 2516 | 1 | **type** `public enum Tone` _(in Sector.Line)_ |  |
| 2518 | 1 | `public static Line head(String text)` _(in Sector.Line)_ |  |
| 2519 | 1 | `public static Line note(String text)` _(in Sector.Line)_ |  |
| 2521 | 1 | `public static Line note(String shown, String whole)` _(in Sector.Line)_ | A note in two layers (0.7.21): `shown` on the page, and `whole` one click away, behind an (i). |
| 2522 | 1 | `public static Line of(String label, String value)` _(in Sector.Line)_ |  |
| 2523 | 1 | `public static Line of(String label, String value, Tone tone)` _(in Sector.Line)_ |  |
| 2532 | 1 | `public SectorStatements.Format statementFormat()` | What kind of business its formal statements read as (0.7.74; the project's spec-sector-statements.md, 4.6): the words of its revenue, its cost of sales and its middle line - the bottom three are the model's in every f... |
| 2535 | 9 | `public String inputLabel()` | What the sector's direct-cost line is called on its income statement. |
| 2561 | 1 | `public Map<String, Double> otherRevenueParts()` | The parts of this sector's revenue that are not the sale of a good, named, for the lines inside an opened Revenue. |
| 2571 | 1 | `public Map<String, Double> otherInputParts()` | ...and the same for the cost line: what is in Inputs that is not a good. |
| 2591 | 5 | `protected Map<String, Double> nameOtherRevenue()` | ...and where those names come from, read off the sector's LIVE fields at the moment the month is struck. |
| 2612 | 5 | `public List<Line> operations(Game game)` | The operations page, as data: the factory's block (plantLines()) when the sector keeps it, then the sector's own lines (ownLines()). |
| 2619 | 1 | `public boolean hasPlantBlock()` | Whether its operations page opens on the factory's block (0.7.30): false for the seven sectors whose page is all their own. |
| 2622 | 1 | `public List<Line> ownLines(Game game)` | The sector's own lines, after the factory's block (0.7.30): none unless it says more. |
| 2630 | 92 | `public List<Line> plantLines(Game game)` | The factory's block (0.7.30; operations() itself until then): the plant - staffed, running at, and the five ratios - then a block per good it makes and per good it buys. |

### SAVE AND RESTORE (lines 2723-2906)

| line | len | member | says |
|---:|---:|---|---|
| 2728 | 37 | `public SectorState toState()` | Everything about this sector that a save has to carry. |
| 2766 | 62 | `public void restore(SectorState s)` |  |
| 2830 | 14 | `public void restoreBills(SectorState s)` | The bills of the month back, over whatever a rebuild re-derived. |
| 2846 | 1 | `private static Double ratioToSave(double r)` | A ratio for the save: itself, or null when it is not a number - Gson writes no NaN, and the load then derives it. |
| 2849 | 1 | `protected void saveExtras(Map<String, Double> extras)` | A sector with state of its own - a price it walks, an order book - writes it here by name. |
| 2852 | 1 | `protected void restoreExtras(Map<String, Double> extras)` | ...and reads it back. |
| 2855 | 22 | `public void reset()` | Everything back to a founding sector. |
| 2878 | 1 | `protected void resetExtras()` |  |
| 2884 | 17 | `public void redenominate(double scale)` | Its money in the new unit. |
| 2902 | 1 | `protected void redenominateExtras(double scale)` |  |
| 2905 | 1 | `public String toString()` |  |


# DataSave.java - 2,145 lines · 343 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (6), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Ports](Ports.md) (3), [Zoning](Zoning.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [DebtManager](DebtManager.md) (2), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (5):** [FuelSplit](FuelSplit.md), [Game](Game.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 219 | · land and ore |
| 306 | · the shedding warning |
| 464 | · the header |
| 772 | · construction, by id |
| 864 | · charged, not derived |
| 946 | · policy |

## Fields (state)

| line | field | says |
|---:|---|---|
| 27 | `private java.util.List<Notice> notices` | See setNotices: null on every save written before the inbox existed. |
| 29 | `private String slotName` |  |
| 30 | `private String gameVersion` |  |
| 31 | `private int saveFormat` |  |
| 32 | `private long savedAt` |  |
| 43 | `private String cityName` | THE FOUNDING RECORD (0.7.10): the city's name, its money's five names and the treasury and vault it was founded with - see Founding. |
| 44 | `private String currencyName` |  |
| 45 | `private String currencyPlural` |  |
| 46 | `private String currencyCode` |  |
| 47 | `private String currencySymbol` |  |
| 48 | `private String currencyQualified` |  |
| 49 | `private Double foundingCash` |  |
| 50 | `private Double foundingReserveUsd` |  |
| 59 | `private Long worldSeed` | ...AND THE WORLD IT STANDS ON (0.7.56): the seed of the city's World. |
| 62 | `private double cash` | save variables |
| 63 | `private int[] buildings` |  |
| 64 | `private int month` |  |
| 65 | `private JsonArray debts` |  |
| 73 | `private JsonArray businessDebts` | Private-sector loans. |
| 80 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 81 | `private int[] underConstruction` |  |
| 92 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 93 | `private int[] underConstructionById` |  |
| 95 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 97 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 99 | `private java.util.List<BuildingManager.ContractRecord> contractRecords` | ...and who placed each part of it, with its material still to draw and the allowance priced in for it (0.7.19). |
| 109 | `private ConstructionControl.State constructionControl` | The player's hand on the queue (0.7.22; ConstructionControl): the city's order of its own sites, the rushes and their months on overtime, the cancels waiting for the month's end, the stopped shells, the demolitions on... |
| 117 | `private java.util.List<DecisionLog.Entry> decisionLog` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, a month, a kind and a line each. |
| 125 | `private AutoBuilder.State autoBuild` | Automatic building (0.7.73; AutoBuilder): the switch, the spare margin and the debt limit, its log of orders and what held it back at its last pass. |
| 135 | `private StrategicReserve.State reserve` | The city's strategic reserve (0.7.85, batch O8; StrategicReserve): the crude its tanks hold and what it cost, the release standing and the fill ordered, and the month's trades the next strike settles (runs/ spec-oil.m... |
| 146 | `private Ports.State portMonth` | The city's ports (0.7.86, batch O9; Ports): each kind of cargo's share of its tonnes at sea in force, whether it goes before the railway, the class crude goes in, the road's two shares, and the month's tonnes by kind ... |
| 157 | `private Zoning.State zoning` | The city's zones, its forest law and the forest cleared for building (0.7.107, batch Z1; Zoning, LandManager's ZONES AND THE FOREST LAW): each zone's sector, size and its kind, price share and the ground used in it; t... |
| 178 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 193 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 204 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 205 | `private java.util.List<Markets.State> markets` |  |
| 217 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 249 | `private Double worldSeaTheta` |  |
| 250 | `private double[] worldTotals` |  |
| 251 | `private double[] landCentre` |  |
| 252 | `private double[][] landCentreRects` |  |
| 253 | `private double[][] landHoldings` |  |
| 254 | `private double[][] landPartFields` |  |
| 255 | `private double[][] landConverted` |  |
| 256 | `private double[] landLanes` |  |
| 257 | `private double[][] landPurchases` |  |
| 258 | `private double[][] landOffers` |  |
| 259 | `private Integer nextOfferId` |  |
| 260 | `private double[] depletion` |  |
| 268 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 278 | `private double[][] depletionByType` | What has been taken out of each typed resource by type (0.7.110, batch I1; LandManager's THE GROUND BY TYPE): a row per resource in Resource's order, its types in Resource.typesOf()'s order, null for a resource whose ... |
| 286 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 287 | `private double[] landListing` |  |
| 289 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 290 | `private Integer ironDeposits` |  |
| 291 | `private Double ironReserveTonnes` |  |
| 298 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 304 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 322 | `private int constructionShedMonth` |  |
| 323 | `private double constructionShedPoints` |  |
| 330 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 341 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 351 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 358 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 359 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 367 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 368 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 376 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 388 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 389 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 390 | `private double insurancePremiums` |  |
| 393 | `private int constructionMaterials` | The city's own yard, in units. |
| 394 | `private long population` |  |
| 405 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 406 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 425 | `private boolean reports` | settings |
| 426 | `private boolean graphs` |  |
| 437 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 447 | `private double landOwned` | Land. |
| 448 | `private int landBlocksPurchased` |  |
| 449 | `private double landPricePerSqFt` |  |
| 458 | `private double incomeTaxRate` | Tax rates. |
| 459 | `private double propertyTaxRate` |  |
| 522 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 540 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 541 | `private double[] treasuryJournalAmounts` |  |
| 542 | `private String[] treasuryJournalPendingLabels` |  |
| 543 | `private double[] treasuryJournalPendingAmounts` |  |
| 551 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 562 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 563 | `private double cityDiscountUnsettled` |  |
| 595 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 783 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 956 | `private double[] taxPolicyState` |  |
| 969 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 981 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 982 | `private double[] householdCells` |  |
| 994 | `private double bankCash` | The bank's cash. |
| 1004 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 1007 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 1008 | `private SalesTaxLedger.State salesTax` |  |
| 1010 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1044 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1056 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1062 | `private double bankDepositRate` | What savers were paid. |
| 1078 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 1091 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1102 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1103 | `private double[] treasuryArrearsAmounts` |  |
| 1120 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1132 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1143 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1154 | `private String[] equityKeys` | The share register, company by company, named. |
| 1155 | `private double[] equity` |  |
| 1162 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1167 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1185 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1204 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1221 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1233 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1245 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1260 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1272 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1283 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1295 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1308 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1320 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1321 | `private Double bankRetainedOpening` |  |
| 1334 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1335 | `private double[] bankPreferredRecord` |  |
| 1351 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1357 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1369 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1383 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1396 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1412 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1427 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1442 | `private Double policyRate` | The policy rate. |
| 1453 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1465 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1466 | `private double[] rolloverLedger` |  |
| 1467 | `private double[] rolloverRecord` |  |
| 1487 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1500 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1512 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1525 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1539 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1552 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1571 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1572 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1573 | `private double householdBondRatio` |  |
| 1574 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1591 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1592 | `private double buybackAbroadUnsettled` |  |
| 1615 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1621 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1635 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 1648 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 1662 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 1675 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 1692 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1706 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1732 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1765 | `private double[] cohorts` | The demographics. |
| 1789 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1791 | `private double[] families` |  |
| 1792 | `private double[] migration` |  |
| 1802 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1811 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1821 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1834 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1847 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1863 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1875 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1892 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1893 | `private double[] education` |  |
| 1922 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1955 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1956 | `private java.util.List<Long> populationTrend` |  |
| 1957 | `private double cityCapitalSpending` |  |
| 1958 | `private double monthlyMaterialImports` |  |
| 1965 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1966 | `private int materialsConsumed` |  |
| 1989 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 2006 | `private java.util.Map<String, String> lastInvestment` | THE INVESTORS' LAST WORD ON EACH SECTOR (0.7.102, Jerus's A22): what the month's investment pass decided for it and why - "Built 2 ...", "Holding: ...", a refusal - as Game.getLastInvestment() reads it, the Sectors sc... |
| 2023 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 2034 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2134 | **type** `public class DataSave` |  |
| 207 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 208 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 209 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 210 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 219-305)

### the shedding warning (lines 306-463)

| line | len | member | says |
|---:|---:|---|---|
| 408 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 412 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 416 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 420 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 464-771)

| line | len | member | says |
|---:|---:|---|---|
| 466 | 1 | `public void setSlotName(String name)` |  |
| 469 | 12 | `public void setFounding(Founding f)` | The founding record, whole. |
| 492 | 16 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 510 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 513 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 524 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 525 | 1 | `public double[] getTreasuryMonth()` |  |
| 565 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 569 | 1 | `public double getCityPaperUnsettled()` |  |
| 570 | 1 | `public double getCityDiscountUnsettled()` |  |
| 572 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 579 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 580 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 581 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 582 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 583 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 584 | 1 | `public double getTreasuryRaisedPending()` |  |
| 597 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 598 | 1 | `public double[] getGovernmentMonth()` |  |
| 599 | 1 | `public String getSlotName()` |  |
| 602 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 608 | 1 | `public String getGameVersion()` |  |
| 609 | 1 | `public int getSaveFormat()` |  |
| 610 | 1 | `public long getSavedAt()` |  |
| 612 | 1 | `public void setHouseholdSavings(double value)` |  |
| 613 | 1 | `public double getHouseholdSavings()` |  |
| 615 | 1 | `public void setLandOwned(double sqFt)` |  |
| 616 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 617 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 619 | 1 | `public double getLandOwned()` |  |
| 620 | 1 | `public int getLandBlocksPurchased()` |  |
| 621 | 1 | `public double getLandPricePerSqFt()` |  |
| 623 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 624 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 626 | 1 | `public double getIncomeTaxRate()` |  |
| 627 | 1 | `public double getPropertyTaxRate()` |  |
| 629 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 634 | 3 | `public void setCash(double money)` | setters |
| 638 | 3 | `public void setMonth(int month)` |  |
| 642 | 3 | `public void setBuildingNum(int i)` |  |
| 646 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 653 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 658 | 3 | `public void setProgress(double[] progress)` |  |
| 662 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 666 | 3 | `public void setPopulation(long population)` |  |
| 669 | 3 | `public void setReports(boolean reports)` |  |
| 682 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 685 | 3 | `public void setGraphs(boolean graphs)` |  |
| 714 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 739 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 765 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 772-863)

| line | len | member | says |
|---:|---:|---|---|
| 785 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 786 | 1 | `public int[] getStackOrder()` |  |
| 788 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 796 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 798 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 800 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 802 | 1 | `public void setConstructionControl(ConstructionControl.State state)` |  |
| 804 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 806 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 808 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 810 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 812 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 814 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 816 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 818 | 1 | `public void setZoning(Zoning.State state)` |  |
| 820 | 1 | `public Zoning.State getZoning()` | Null on a save from before 0.7.107, or with nothing zoned, no law and no forest cleared. |
| 822 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 824 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 827 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 831 | 3 | `public int getConstructionByIdLength()` |  |
| 835 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 842 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 849 | 6 | `public double getContractValueById(int templateId)` |  |
| 857 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 864-945)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 867 | 1 | `public double getPropertyTaxCharged()` |  |
| 869 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 870 | 1 | `public double getCityInterestAccrued()` |  |
| 872 | 1 | `public void setWorkforce(long workforce)` |  |
| 875 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 878 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 896 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 898 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 900 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 902 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 904 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 906 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 908 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 909 | 1 | `public double[][] getLandOffers()` |  |
| 911 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 912 | 1 | `public double[] getDepletion()` |  |
| 914 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 915 | 1 | `public void setDepletionByType(double[][] byType)` |  |
| 917 | 1 | `public double[][] getDepletionByType()` | E by type as saved (0.7.110), a row per resource or null for the walk's reading; null on a save that carries none. |
| 919 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 921 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 923 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 924 | 1 | `public double[] getWorldTotals()` |  |
| 926 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 928 | 1 | `public void setFreshRights(double units)` |  |
| 930 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 932 | 1 | `public void setMapStamp(Long stamp)` |  |
| 934 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 937 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 938 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 939 | 1 | `public double[] getLandMarketPrices()` |  |
| 941 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 943 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 946-2145)

| line | len | member | says |
|---:|---:|---|---|
| 1012 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 1013 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 1014 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 1015 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 1016 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 1017 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 1019 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 1020 | 1 | `public double[] getTaxPolicyState()` |  |
| 1022 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 1023 | 1 | `public double[] getHouseholdBalance()` |  |
| 1025 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1029 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1030 | 1 | `public double[] getHouseholdCells()` |  |
| 1032 | 1 | `public void setBankCash(double cash)` |  |
| 1033 | 1 | `public double getBankCash()` |  |
| 1046 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1047 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1058 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1059 | 1 | `public double getBankProfitLastMonth()` |  |
| 1063 | 1 | `public void setBankDepositRate(double v)` |  |
| 1064 | 1 | `public double getBankDepositRate()` |  |
| 1080 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1081 | 1 | `public double[] getForeignAccounts()` |  |
| 1093 | 1 | `public void setCentralBank(double[] state)` |  |
| 1094 | 1 | `public double[] getCentralBank()` |  |
| 1105 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1109 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1110 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1122 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1123 | 1 | `public double[] getForeignStanding()` |  |
| 1134 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1135 | 1 | `public double[] getCapitalFlows()` |  |
| 1145 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1146 | 1 | `public double[] getOutwardInvestment()` |  |
| 1157 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1158 | 1 | `public String[] getEquityKeys()` |  |
| 1159 | 1 | `public double[] getEquity()` |  |
| 1163 | 1 | `public void setExchange(double[] state)` |  |
| 1164 | 1 | `public double[] getExchange()` |  |
| 1168 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 1169 | 1 | `public Exchange.State getExchangeState()` |  |
| 1187 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1188 | 1 | `public double[] getBankSolvency()` |  |
| 1206 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1207 | 1 | `public double[] getHousingOccupancy()` |  |
| 1223 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1224 | 1 | `public double[] getBankLastMonth()` |  |
| 1235 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1236 | 1 | `public double[] getBankPricingHistory()` |  |
| 1247 | 1 | `public void setBankLateProfit(double value)` |  |
| 1248 | 1 | `public double getBankLateProfit()` |  |
| 1262 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1263 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1274 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1275 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1285 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1286 | 1 | `public double[] getBankMonthLines()` |  |
| 1297 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1298 | 1 | `public double[] getBankStatementYear()` |  |
| 1310 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1311 | 1 | `public double[] getBankSheetYear()` |  |
| 1323 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1324 | 1 | `public Double getBankPaidInOpening()` |  |
| 1325 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1326 | 1 | `public Double getBankRetainedOpening()` |  |
| 1337 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1341 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1342 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1353 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1354 | 1 | `public TreasuryFund.State getFund()` |  |
| 1359 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1360 | 1 | `public double[] getDebtMarket()` |  |
| 1371 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1372 | 1 | `public double[] getPriceIndex()` |  |
| 1385 | 1 | `public void setExpectations(double[] state)` |  |
| 1386 | 1 | `public double[] getExpectations()` |  |
| 1398 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1399 | 1 | `public double[] getWorldEconomy()` |  |
| 1414 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1415 | 1 | `public double getTradedExchangeRate()` |  |
| 1429 | 1 | `public void setDenomination(double[] state)` |  |
| 1430 | 1 | `public double[] getDenomination()` |  |
| 1444 | 1 | `public void setPolicyRate(double rate)` |  |
| 1446 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1455 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1456 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1469 | 1 | `public void setRolloverMode(String mode)` |  |
| 1471 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1476 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1477 | 1 | `public double[] getRolloverLedger()` |  |
| 1478 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1479 | 1 | `public double[] getRolloverRecord()` |  |
| 1489 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1490 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1502 | 1 | `public void setInflationTarget(double target)` |  |
| 1504 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1514 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1516 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 1527 | 1 | `public void setQeTargetShare(double share)` |  |
| 1528 | 1 | `public double getQeTargetShare()` |  |
| 1541 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1543 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1554 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1555 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1576 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1577 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1578 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1579 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1580 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1581 | 1 | `public double getHouseholdBondRatio()` |  |
| 1582 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1583 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1594 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1598 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1599 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1623 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1624 | 1 | `public double getCarsPerHousehold()` |  |
| 1637 | 1 | `public void setTransitBill(double v)` |  |
| 1638 | 1 | `public double getTransitBill()` |  |
| 1650 | 1 | `public void setCaptiveShare(double v)` |  |
| 1651 | 1 | `public double getCaptiveShare()` |  |
| 1652 | 1 | `public void setFuelPerJourney(double v)` |  |
| 1653 | 1 | `public double getFuelPerJourney()` |  |
| 1664 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 1665 | 1 | `public double[] getHouseholdFuel()` |  |
| 1677 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 1678 | 1 | `public double[] getHouseholdGraduates()` |  |
| 1680 | 1 | `public void setRememberedCommute(double v)` |  |
| 1681 | 3 | `public double getRememberedCommute()` |  |
| 1694 | 1 | `public void setCostOfLiving(double v)` |  |
| 1695 | 1 | `public double getCostOfLiving()` |  |
| 1708 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1709 | 1 | `public double getBankTaxCharged()` |  |
| 1713 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1714 | 1 | `public double getRentWeight()` |  |
| 1734 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1735 | 1 | `public double getRentWeightStudio()` |  |
| 1737 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1741 | 1 | `public int getConstructionShedMonth()` |  |
| 1742 | 1 | `public double getConstructionShedPoints()` |  |
| 1744 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1747 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1895 | 1 | `public void setLicences(double[] a)` |  |
| 1896 | 1 | `public double[] getLicences()` |  |
| 1897 | 1 | `public void setEducation(double[] a)` |  |
| 1898 | 1 | `public double[] getEducation()` |  |
| 1900 | 1 | `public void setLabour(double[] a)` |  |
| 1901 | 1 | `public double[] getLabour()` |  |
| 1902 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1903 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1905 | 1 | `public void setCohorts(double[] a)` |  |
| 1906 | 1 | `public double[] getCohorts()` |  |
| 1909 | 1 | `public void setBandNames(String[] names)` |  |
| 1911 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1925 | 1 | `public void setShapeNames(String[] names)` |  |
| 1926 | 1 | `public String[] getShapeNames()` |  |
| 1927 | 1 | `public void setFamilies(double[] a)` |  |
| 1928 | 1 | `public double[] getFamilies()` |  |
| 1929 | 1 | `public void setMigration(double[] a)` |  |
| 1930 | 1 | `public double[] getMigration()` |  |
| 1931 | 1 | `public void setUnemployment(double[] a)` |  |
| 1932 | 1 | `public double[] getUnemployment()` |  |
| 1933 | 1 | `public void setSickness(double[] a)` |  |
| 1934 | 1 | `public double[] getSickness()` |  |
| 1935 | 1 | `public void setCrime(double[] a)` |  |
| 1936 | 1 | `public double[] getCrime()` |  |
| 1937 | 1 | `public void setHealth(double[] a)` |  |
| 1938 | 1 | `public double[] getHealth()` |  |
| 1939 | 1 | `public void setHealthcare(double[] a)` |  |
| 1940 | 1 | `public double[] getHealthcare()` |  |
| 1968 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1969 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1970 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 1971 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 1972 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1973 | 1 | `public double getCityCapitalSpending()` |  |
| 1991 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1992 | 1 | `public double getCityMaintenancePaid()` |  |
| 2008 | 1 | `public void setLastInvestment(java.util.Map<String, String> m)` |  |
| 2009 | 1 | `public java.util.Map<String, String> getLastInvestment()` |  |
| 2010 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 2011 | 1 | `public double getMonthlyMaterialImports()` |  |
| 2012 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 2013 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 2014 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 2015 | 1 | `public int getMaterialsConsumed()` |  |
| 2024 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 2025 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 2035 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 2036 | 1 | `public double[] getHouseholdStatement()` |  |
| 2038 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 2041 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 2043 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2046 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2048 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2051 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2053 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2056 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2058 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2061 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2063 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2064 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2066 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2067 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2069 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2070 | 1 | `public double getInsurancePremiums()` |  |
| 2072 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2073 | 1 | `public double[] getNationalAccounts()` |  |
| 2075 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2076 | 1 | `public List<Double> getGdpRolling()` |  |
| 2086 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2089 | 3 | `public int getUnderConstruction(int index)` |  |
| 2092 | 3 | `public double getCash()` |  |
| 2096 | 3 | `public int getMonth()` |  |
| 2100 | 3 | `public int getBuildingQuantity(int index)` |  |
| 2104 | 3 | `public int getBuildingsLength()` |  |
| 2108 | 3 | `public JsonArray getDebt()` |  |
| 2112 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 2117 | 3 | `public JsonArray getBusinessDebt()` |  |
| 2121 | 3 | `public int getProgressLength()` |  |
| 2125 | 3 | `public double getProgress(int index)` |  |
| 2129 | 3 | `public int getConstructionMaterials()` |  |
| 2133 | 3 | `public long getPopulation()` |  |
| 2136 | 3 | `public boolean getReports()` |  |
| 2139 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2142 | 3 | `public boolean getGraphs()` |  |


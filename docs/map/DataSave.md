# DataSave.java - 2,100 lines · 337 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (6), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Ports](Ports.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [DebtManager](DebtManager.md) (2), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (5):** [FuelSplit](FuelSplit.md), [Game](Game.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 208 | · land and ore |
| 285 | · the shedding warning |
| 443 | · the header |
| 751 | · construction, by id |
| 839 | · charged, not derived |
| 918 | · policy |

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
| 167 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 182 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 193 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 194 | `private java.util.List<Markets.State> markets` |  |
| 206 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 238 | `private Double worldSeaTheta` |  |
| 239 | `private double[] worldTotals` |  |
| 240 | `private double[] landCentre` |  |
| 241 | `private double[][] landCentreRects` |  |
| 242 | `private double[][] landHoldings` |  |
| 243 | `private double[][] landPartFields` |  |
| 244 | `private double[][] landConverted` |  |
| 245 | `private double[] landLanes` |  |
| 246 | `private double[][] landPurchases` |  |
| 247 | `private double[][] landOffers` |  |
| 248 | `private Integer nextOfferId` |  |
| 249 | `private double[] depletion` |  |
| 257 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 265 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 266 | `private double[] landListing` |  |
| 268 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 269 | `private Integer ironDeposits` |  |
| 270 | `private Double ironReserveTonnes` |  |
| 277 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 283 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 301 | `private int constructionShedMonth` |  |
| 302 | `private double constructionShedPoints` |  |
| 309 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 320 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 330 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 337 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 338 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 346 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 347 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 355 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 367 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 368 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 369 | `private double insurancePremiums` |  |
| 372 | `private int constructionMaterials` | The city's own yard, in units. |
| 373 | `private long population` |  |
| 384 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 385 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 404 | `private boolean reports` | settings |
| 405 | `private boolean graphs` |  |
| 416 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 426 | `private double landOwned` | Land. |
| 427 | `private int landBlocksPurchased` |  |
| 428 | `private double landPricePerSqFt` |  |
| 437 | `private double incomeTaxRate` | Tax rates. |
| 438 | `private double propertyTaxRate` |  |
| 501 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 519 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 520 | `private double[] treasuryJournalAmounts` |  |
| 521 | `private String[] treasuryJournalPendingLabels` |  |
| 522 | `private double[] treasuryJournalPendingAmounts` |  |
| 530 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 541 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 542 | `private double cityDiscountUnsettled` |  |
| 574 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 762 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 928 | `private double[] taxPolicyState` |  |
| 941 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 953 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 954 | `private double[] householdCells` |  |
| 966 | `private double bankCash` | The bank's cash. |
| 976 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 979 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 980 | `private SalesTaxLedger.State salesTax` |  |
| 982 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1016 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1028 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1034 | `private double bankDepositRate` | What savers were paid. |
| 1050 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 1063 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1074 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1075 | `private double[] treasuryArrearsAmounts` |  |
| 1092 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1104 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1115 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1126 | `private String[] equityKeys` | The share register, company by company, named. |
| 1127 | `private double[] equity` |  |
| 1134 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1139 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1157 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1176 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1193 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1205 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1217 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1232 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1244 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1255 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1267 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1280 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1292 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1293 | `private Double bankRetainedOpening` |  |
| 1306 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1307 | `private double[] bankPreferredRecord` |  |
| 1323 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1329 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1341 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1355 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1368 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1384 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1399 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1414 | `private Double policyRate` | The policy rate. |
| 1425 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1437 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1438 | `private double[] rolloverLedger` |  |
| 1439 | `private double[] rolloverRecord` |  |
| 1459 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1472 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1484 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1497 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1511 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1524 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1543 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1544 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1545 | `private double householdBondRatio` |  |
| 1546 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1563 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1564 | `private double buybackAbroadUnsettled` |  |
| 1587 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1593 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1607 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 1620 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 1634 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 1647 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 1664 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1678 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1704 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1737 | `private double[] cohorts` | The demographics. |
| 1761 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1763 | `private double[] families` |  |
| 1764 | `private double[] migration` |  |
| 1774 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1783 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1793 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1806 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1819 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1835 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1847 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1864 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1865 | `private double[] education` |  |
| 1894 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1927 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1928 | `private java.util.List<Long> populationTrend` |  |
| 1929 | `private double cityCapitalSpending` |  |
| 1930 | `private double monthlyMaterialImports` |  |
| 1937 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1938 | `private int materialsConsumed` |  |
| 1961 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1978 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1989 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2089 | **type** `public class DataSave` |  |
| 196 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 197 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 198 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 199 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 208-284)

### the shedding warning (lines 285-442)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 391 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 395 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 399 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 443-750)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 1 | `public void setSlotName(String name)` |  |
| 448 | 12 | `public void setFounding(Founding f)` | The founding record, whole. |
| 471 | 16 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 489 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 492 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 503 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 504 | 1 | `public double[] getTreasuryMonth()` |  |
| 544 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 548 | 1 | `public double getCityPaperUnsettled()` |  |
| 549 | 1 | `public double getCityDiscountUnsettled()` |  |
| 551 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 558 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 559 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 560 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 561 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 562 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 563 | 1 | `public double getTreasuryRaisedPending()` |  |
| 576 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 577 | 1 | `public double[] getGovernmentMonth()` |  |
| 578 | 1 | `public String getSlotName()` |  |
| 581 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 587 | 1 | `public String getGameVersion()` |  |
| 588 | 1 | `public int getSaveFormat()` |  |
| 589 | 1 | `public long getSavedAt()` |  |
| 591 | 1 | `public void setHouseholdSavings(double value)` |  |
| 592 | 1 | `public double getHouseholdSavings()` |  |
| 594 | 1 | `public void setLandOwned(double sqFt)` |  |
| 595 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 596 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 598 | 1 | `public double getLandOwned()` |  |
| 599 | 1 | `public int getLandBlocksPurchased()` |  |
| 600 | 1 | `public double getLandPricePerSqFt()` |  |
| 602 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 603 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 605 | 1 | `public double getIncomeTaxRate()` |  |
| 606 | 1 | `public double getPropertyTaxRate()` |  |
| 608 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 613 | 3 | `public void setCash(double money)` | setters |
| 617 | 3 | `public void setMonth(int month)` |  |
| 621 | 3 | `public void setBuildingNum(int i)` |  |
| 625 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 632 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 637 | 3 | `public void setProgress(double[] progress)` |  |
| 641 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 645 | 3 | `public void setPopulation(long population)` |  |
| 648 | 3 | `public void setReports(boolean reports)` |  |
| 661 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 664 | 3 | `public void setGraphs(boolean graphs)` |  |
| 693 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 718 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 744 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 751-838)

| line | len | member | says |
|---:|---:|---|---|
| 764 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 765 | 1 | `public int[] getStackOrder()` |  |
| 767 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 775 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 777 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 779 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 781 | 1 | `public void setConstructionControl(ConstructionControl.State state)` |  |
| 783 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 785 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 787 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 789 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 791 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 793 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 795 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 797 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 799 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 802 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 806 | 3 | `public int getConstructionByIdLength()` |  |
| 810 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 817 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 824 | 6 | `public double getContractValueById(int templateId)` |  |
| 832 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 839-917)

| line | len | member | says |
|---:|---:|---|---|
| 841 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 842 | 1 | `public double getPropertyTaxCharged()` |  |
| 844 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 845 | 1 | `public double getCityInterestAccrued()` |  |
| 847 | 1 | `public void setWorkforce(long workforce)` |  |
| 850 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 853 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 871 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 873 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 875 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 877 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 879 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 881 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 883 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 884 | 1 | `public double[][] getLandOffers()` |  |
| 886 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 887 | 1 | `public double[] getDepletion()` |  |
| 889 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 891 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 893 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 895 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 896 | 1 | `public double[] getWorldTotals()` |  |
| 898 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 900 | 1 | `public void setFreshRights(double units)` |  |
| 902 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 904 | 1 | `public void setMapStamp(Long stamp)` |  |
| 906 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 909 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 910 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 911 | 1 | `public double[] getLandMarketPrices()` |  |
| 913 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 915 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 918-2100)

| line | len | member | says |
|---:|---:|---|---|
| 984 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 985 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 986 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 987 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 988 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 989 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 991 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 992 | 1 | `public double[] getTaxPolicyState()` |  |
| 994 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 995 | 1 | `public double[] getHouseholdBalance()` |  |
| 997 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1001 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1002 | 1 | `public double[] getHouseholdCells()` |  |
| 1004 | 1 | `public void setBankCash(double cash)` |  |
| 1005 | 1 | `public double getBankCash()` |  |
| 1018 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1019 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1030 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1031 | 1 | `public double getBankProfitLastMonth()` |  |
| 1035 | 1 | `public void setBankDepositRate(double v)` |  |
| 1036 | 1 | `public double getBankDepositRate()` |  |
| 1052 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1053 | 1 | `public double[] getForeignAccounts()` |  |
| 1065 | 1 | `public void setCentralBank(double[] state)` |  |
| 1066 | 1 | `public double[] getCentralBank()` |  |
| 1077 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1081 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1082 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1094 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1095 | 1 | `public double[] getForeignStanding()` |  |
| 1106 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1107 | 1 | `public double[] getCapitalFlows()` |  |
| 1117 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1118 | 1 | `public double[] getOutwardInvestment()` |  |
| 1129 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1130 | 1 | `public String[] getEquityKeys()` |  |
| 1131 | 1 | `public double[] getEquity()` |  |
| 1135 | 1 | `public void setExchange(double[] state)` |  |
| 1136 | 1 | `public double[] getExchange()` |  |
| 1140 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 1141 | 1 | `public Exchange.State getExchangeState()` |  |
| 1159 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1160 | 1 | `public double[] getBankSolvency()` |  |
| 1178 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1179 | 1 | `public double[] getHousingOccupancy()` |  |
| 1195 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1196 | 1 | `public double[] getBankLastMonth()` |  |
| 1207 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1208 | 1 | `public double[] getBankPricingHistory()` |  |
| 1219 | 1 | `public void setBankLateProfit(double value)` |  |
| 1220 | 1 | `public double getBankLateProfit()` |  |
| 1234 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1235 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1246 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1247 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1257 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1258 | 1 | `public double[] getBankMonthLines()` |  |
| 1269 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1270 | 1 | `public double[] getBankStatementYear()` |  |
| 1282 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1283 | 1 | `public double[] getBankSheetYear()` |  |
| 1295 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1296 | 1 | `public Double getBankPaidInOpening()` |  |
| 1297 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1298 | 1 | `public Double getBankRetainedOpening()` |  |
| 1309 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1313 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1314 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1325 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1326 | 1 | `public TreasuryFund.State getFund()` |  |
| 1331 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1332 | 1 | `public double[] getDebtMarket()` |  |
| 1343 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1344 | 1 | `public double[] getPriceIndex()` |  |
| 1357 | 1 | `public void setExpectations(double[] state)` |  |
| 1358 | 1 | `public double[] getExpectations()` |  |
| 1370 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1371 | 1 | `public double[] getWorldEconomy()` |  |
| 1386 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1387 | 1 | `public double getTradedExchangeRate()` |  |
| 1401 | 1 | `public void setDenomination(double[] state)` |  |
| 1402 | 1 | `public double[] getDenomination()` |  |
| 1416 | 1 | `public void setPolicyRate(double rate)` |  |
| 1418 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1427 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1428 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1441 | 1 | `public void setRolloverMode(String mode)` |  |
| 1443 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1448 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1449 | 1 | `public double[] getRolloverLedger()` |  |
| 1450 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1451 | 1 | `public double[] getRolloverRecord()` |  |
| 1461 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1462 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1474 | 1 | `public void setInflationTarget(double target)` |  |
| 1476 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1486 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1488 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 1499 | 1 | `public void setQeTargetShare(double share)` |  |
| 1500 | 1 | `public double getQeTargetShare()` |  |
| 1513 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1515 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1526 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1527 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1548 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1549 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1550 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1551 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1552 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1553 | 1 | `public double getHouseholdBondRatio()` |  |
| 1554 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1555 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1566 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1570 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1571 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1595 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1596 | 1 | `public double getCarsPerHousehold()` |  |
| 1609 | 1 | `public void setTransitBill(double v)` |  |
| 1610 | 1 | `public double getTransitBill()` |  |
| 1622 | 1 | `public void setCaptiveShare(double v)` |  |
| 1623 | 1 | `public double getCaptiveShare()` |  |
| 1624 | 1 | `public void setFuelPerJourney(double v)` |  |
| 1625 | 1 | `public double getFuelPerJourney()` |  |
| 1636 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 1637 | 1 | `public double[] getHouseholdFuel()` |  |
| 1649 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 1650 | 1 | `public double[] getHouseholdGraduates()` |  |
| 1652 | 1 | `public void setRememberedCommute(double v)` |  |
| 1653 | 3 | `public double getRememberedCommute()` |  |
| 1666 | 1 | `public void setCostOfLiving(double v)` |  |
| 1667 | 1 | `public double getCostOfLiving()` |  |
| 1680 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1681 | 1 | `public double getBankTaxCharged()` |  |
| 1685 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1686 | 1 | `public double getRentWeight()` |  |
| 1706 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1707 | 1 | `public double getRentWeightStudio()` |  |
| 1709 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1713 | 1 | `public int getConstructionShedMonth()` |  |
| 1714 | 1 | `public double getConstructionShedPoints()` |  |
| 1716 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1719 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1867 | 1 | `public void setLicences(double[] a)` |  |
| 1868 | 1 | `public double[] getLicences()` |  |
| 1869 | 1 | `public void setEducation(double[] a)` |  |
| 1870 | 1 | `public double[] getEducation()` |  |
| 1872 | 1 | `public void setLabour(double[] a)` |  |
| 1873 | 1 | `public double[] getLabour()` |  |
| 1874 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1875 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1877 | 1 | `public void setCohorts(double[] a)` |  |
| 1878 | 1 | `public double[] getCohorts()` |  |
| 1881 | 1 | `public void setBandNames(String[] names)` |  |
| 1883 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1897 | 1 | `public void setShapeNames(String[] names)` |  |
| 1898 | 1 | `public String[] getShapeNames()` |  |
| 1899 | 1 | `public void setFamilies(double[] a)` |  |
| 1900 | 1 | `public double[] getFamilies()` |  |
| 1901 | 1 | `public void setMigration(double[] a)` |  |
| 1902 | 1 | `public double[] getMigration()` |  |
| 1903 | 1 | `public void setUnemployment(double[] a)` |  |
| 1904 | 1 | `public double[] getUnemployment()` |  |
| 1905 | 1 | `public void setSickness(double[] a)` |  |
| 1906 | 1 | `public double[] getSickness()` |  |
| 1907 | 1 | `public void setCrime(double[] a)` |  |
| 1908 | 1 | `public double[] getCrime()` |  |
| 1909 | 1 | `public void setHealth(double[] a)` |  |
| 1910 | 1 | `public double[] getHealth()` |  |
| 1911 | 1 | `public void setHealthcare(double[] a)` |  |
| 1912 | 1 | `public double[] getHealthcare()` |  |
| 1940 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1941 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1942 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 1943 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 1944 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1945 | 1 | `public double getCityCapitalSpending()` |  |
| 1963 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1964 | 1 | `public double getCityMaintenancePaid()` |  |
| 1965 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1966 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1967 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1968 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1969 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1970 | 1 | `public int getMaterialsConsumed()` |  |
| 1979 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1980 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1990 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1991 | 1 | `public double[] getHouseholdStatement()` |  |
| 1993 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1996 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1998 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2001 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2003 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2006 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2008 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2011 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2013 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2016 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2018 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2019 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2021 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2022 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2024 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2025 | 1 | `public double getInsurancePremiums()` |  |
| 2027 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2028 | 1 | `public double[] getNationalAccounts()` |  |
| 2030 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2031 | 1 | `public List<Double> getGdpRolling()` |  |
| 2041 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2044 | 3 | `public int getUnderConstruction(int index)` |  |
| 2047 | 3 | `public double getCash()` |  |
| 2051 | 3 | `public int getMonth()` |  |
| 2055 | 3 | `public int getBuildingQuantity(int index)` |  |
| 2059 | 3 | `public int getBuildingsLength()` |  |
| 2063 | 3 | `public JsonArray getDebt()` |  |
| 2067 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 2072 | 3 | `public JsonArray getBusinessDebt()` |  |
| 2076 | 3 | `public int getProgressLength()` |  |
| 2080 | 3 | `public double getProgress(int index)` |  |
| 2084 | 3 | `public int getConstructionMaterials()` |  |
| 2088 | 3 | `public long getPopulation()` |  |
| 2091 | 3 | `public boolean getReports()` |  |
| 2094 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2097 | 3 | `public boolean getGraphs()` |  |


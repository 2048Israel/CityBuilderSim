# DataSave.java - 2,213 lines · 351 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Founding](Founding.md) (7), [SectorBooks](SectorBooks.md) (6), [World](World.md) (5), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Ports](Ports.md) (3), [Zoning](Zoning.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [OldMetal](OldMetal.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [DebtManager](DebtManager.md) (2), [EarthSite](EarthSite.md) (1), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (5):** [FuelSplit](FuelSplit.md), [Game](Game.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 247 | · land and ore |
| 334 | · the shedding warning |
| 492 | · the header |
| 840 | · construction, by id |
| 932 | · charged, not derived |
| 1014 | · policy |

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
| 72 | `private String worldKind` | ...AND WHETHER IT IS THE REAL EARTH (0.7.121, batch E4; spec-earth 6), kept together: worldKind "EARTH" on a city founded on the Earth, absent on a generated world's - every save before 0.7.121 is one; the spot, earth... |
| 73 | `private Double earthLat` |  |
| 74 | `private Double earthLon` |  |
| 75 | `private String earthData` |  |
| 78 | `private double cash` | save variables |
| 79 | `private int[] buildings` |  |
| 80 | `private int month` |  |
| 81 | `private JsonArray debts` |  |
| 89 | `private JsonArray businessDebts` | Private-sector loans. |
| 96 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 97 | `private int[] underConstruction` |  |
| 108 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 109 | `private int[] underConstructionById` |  |
| 111 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 113 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 115 | `private java.util.List<BuildingManager.ContractRecord> contractRecords` | ...and who placed each part of it, with its material still to draw and the allowance priced in for it (0.7.19). |
| 125 | `private ConstructionControl.State constructionControl` | The player's hand on the queue (0.7.22; ConstructionControl): the city's order of its own sites, the rushes and their months on overtime, the cancels waiting for the month's end, the stopped shells, the demolitions on... |
| 133 | `private java.util.List<DecisionLog.Entry> decisionLog` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, a month, a kind and a line each. |
| 141 | `private AutoBuilder.State autoBuild` | Automatic building (0.7.73; AutoBuilder): the switch, the spare margin and the debt limit, its log of orders and what held it back at its last pass. |
| 151 | `private StrategicReserve.State reserve` | The city's strategic reserve (0.7.85, batch O8; StrategicReserve): the crude its tanks hold and what it cost, the release standing and the fill ordered, and the month's trades the next strike settles (runs/ spec-oil.m... |
| 162 | `private Ports.State portMonth` | The city's ports (0.7.86, batch O9; Ports): each kind of cargo's share of its tonnes at sea in force, whether it goes before the railway, the class crude goes in, the road's two shares, and the month's tonnes by kind ... |
| 173 | `private Zoning.State zoning` | The city's zones, its forest law and the forest cleared for building (0.7.107, batch Z1; Zoning, LandManager's ZONES AND THE FOREST LAW): each zone's sector, size and its kind, price share and the ground used in it; t... |
| 194 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 209 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 220 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 221 | `private java.util.List<Markets.State> markets` |  |
| 234 | `private OldMetal.State oldMetal` | The city's old steel (0.7.123, batch I4; OldMetal): what it has thrown out since its Scrap Yards last bought, by seller and by source, and the yards' last month. |
| 245 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 277 | `private Double worldSeaTheta` |  |
| 278 | `private double[] worldTotals` |  |
| 279 | `private double[] landCentre` |  |
| 280 | `private double[][] landCentreRects` |  |
| 281 | `private double[][] landHoldings` |  |
| 282 | `private double[][] landPartFields` |  |
| 283 | `private double[][] landConverted` |  |
| 284 | `private double[] landLanes` |  |
| 285 | `private double[][] landPurchases` |  |
| 286 | `private double[][] landOffers` |  |
| 287 | `private Integer nextOfferId` |  |
| 288 | `private double[] depletion` |  |
| 296 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 306 | `private double[][] depletionByType` | What has been taken out of each typed resource by type (0.7.110, batch I1; LandManager's THE GROUND BY TYPE): a row per resource in Resource's order, its types in Resource.typesOf()'s order, null for a resource whose ... |
| 314 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 315 | `private double[] landListing` |  |
| 317 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 318 | `private Integer ironDeposits` |  |
| 319 | `private Double ironReserveTonnes` |  |
| 326 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 332 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 350 | `private int constructionShedMonth` |  |
| 351 | `private double constructionShedPoints` |  |
| 358 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 369 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 379 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 386 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 387 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 395 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 396 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 404 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 416 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 417 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 418 | `private double insurancePremiums` |  |
| 421 | `private int constructionMaterials` | The city's own yard, in units. |
| 422 | `private long population` |  |
| 433 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 434 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 453 | `private boolean reports` | settings |
| 454 | `private boolean graphs` |  |
| 465 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 475 | `private double landOwned` | Land. |
| 476 | `private int landBlocksPurchased` |  |
| 477 | `private double landPricePerSqFt` |  |
| 486 | `private double incomeTaxRate` | Tax rates. |
| 487 | `private double propertyTaxRate` |  |
| 590 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 608 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 609 | `private double[] treasuryJournalAmounts` |  |
| 610 | `private String[] treasuryJournalPendingLabels` |  |
| 611 | `private double[] treasuryJournalPendingAmounts` |  |
| 619 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 630 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 631 | `private double cityDiscountUnsettled` |  |
| 663 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 851 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 1024 | `private double[] taxPolicyState` |  |
| 1037 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 1049 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 1050 | `private double[] householdCells` |  |
| 1062 | `private double bankCash` | The bank's cash. |
| 1072 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 1075 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 1076 | `private SalesTaxLedger.State salesTax` |  |
| 1078 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1112 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1124 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1130 | `private double bankDepositRate` | What savers were paid. |
| 1146 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 1159 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1170 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1171 | `private double[] treasuryArrearsAmounts` |  |
| 1188 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1200 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1211 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1222 | `private String[] equityKeys` | The share register, company by company, named. |
| 1223 | `private double[] equity` |  |
| 1230 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1235 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1253 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1272 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1289 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1301 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1313 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1328 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1340 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1351 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1363 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1376 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1388 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1389 | `private Double bankRetainedOpening` |  |
| 1402 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1403 | `private double[] bankPreferredRecord` |  |
| 1419 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1425 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1437 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1451 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1464 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1480 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1495 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1510 | `private Double policyRate` | The policy rate. |
| 1521 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1533 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1534 | `private double[] rolloverLedger` |  |
| 1535 | `private double[] rolloverRecord` |  |
| 1555 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1568 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1580 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1593 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1607 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1620 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1639 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1640 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1641 | `private double householdBondRatio` |  |
| 1642 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1659 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1660 | `private double buybackAbroadUnsettled` |  |
| 1683 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1689 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1703 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 1716 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 1730 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 1743 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 1760 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1774 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1800 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1833 | `private double[] cohorts` | The demographics. |
| 1857 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1859 | `private double[] families` |  |
| 1860 | `private double[] migration` |  |
| 1870 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1879 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1889 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1902 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1915 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1931 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1943 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1960 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1961 | `private double[] education` |  |
| 1990 | `private String[] shapeNames` | The household shapes this save was written with. |
| 2023 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 2024 | `private java.util.List<Long> populationTrend` |  |
| 2025 | `private double cityCapitalSpending` |  |
| 2026 | `private double monthlyMaterialImports` |  |
| 2033 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 2034 | `private int materialsConsumed` |  |
| 2057 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 2074 | `private java.util.Map<String, String> lastInvestment` | THE INVESTORS' LAST WORD ON EACH SECTOR (0.7.102, Jerus's A22): what the month's investment pass decided for it and why - "Built 2 ...", "Holding: ...", a refusal - as Game.getLastInvestment() reads it, the Sectors sc... |
| 2091 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 2102 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2202 | **type** `public class DataSave` |  |
| 223 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 224 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 225 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 226 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |
| 236 | 1 | `public void setOldMetal(OldMetal.State s)` |  |
| 238 | 1 | `public OldMetal.State getOldMetal()` | The old steel as saved (0.7.123), or null on a save from before. |

### land and ore (lines 247-333)

### the shedding warning (lines 334-491)

| line | len | member | says |
|---:|---:|---|---|
| 436 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 440 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 444 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 448 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 492-839)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 1 | `public void setSlotName(String name)` |  |
| 497 | 18 | `public void setFounding(Founding f)` | The founding record, whole. |
| 526 | 23 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 551 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 554 | 1 | `public String getWorldKind()` | The world's kind as saved (0.7.121): "EARTH" on a city founded on the real Earth, null on a generated world's and on every save before 0.7.121. |
| 557 | 1 | `public boolean isOnEarth()` | Whether the save is a city on the real Earth (0.7.121): worldKind EARTH. |
| 560 | 1 | `public Double getEarthLat()` | The Earth's spot as saved, latitude in degrees (0.7.121); null on a generated world. |
| 563 | 1 | `public Double getEarthLon()` | ...its longitude. |
| 566 | 1 | `public String getEarthData()` | The Earth's data the city was drawn from, by identity ("earth-1:<hash>", 0.7.121); null on a generated world. |
| 574 | 5 | `public String earthProblem()` | Why this save's world cannot be read here, or null (0.7.121): a kind this build does not know, or an Earth city whose spot is missing or whose data is not this build's or not here (EarthSite.loadProblem()). |
| 581 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 592 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 593 | 1 | `public double[] getTreasuryMonth()` |  |
| 633 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 637 | 1 | `public double getCityPaperUnsettled()` |  |
| 638 | 1 | `public double getCityDiscountUnsettled()` |  |
| 640 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 647 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 648 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 649 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 650 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 651 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 652 | 1 | `public double getTreasuryRaisedPending()` |  |
| 665 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 666 | 1 | `public double[] getGovernmentMonth()` |  |
| 667 | 1 | `public String getSlotName()` |  |
| 670 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 676 | 1 | `public String getGameVersion()` |  |
| 677 | 1 | `public int getSaveFormat()` |  |
| 678 | 1 | `public long getSavedAt()` |  |
| 680 | 1 | `public void setHouseholdSavings(double value)` |  |
| 681 | 1 | `public double getHouseholdSavings()` |  |
| 683 | 1 | `public void setLandOwned(double sqFt)` |  |
| 684 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 685 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 687 | 1 | `public double getLandOwned()` |  |
| 688 | 1 | `public int getLandBlocksPurchased()` |  |
| 689 | 1 | `public double getLandPricePerSqFt()` |  |
| 691 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 692 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 694 | 1 | `public double getIncomeTaxRate()` |  |
| 695 | 1 | `public double getPropertyTaxRate()` |  |
| 697 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 702 | 3 | `public void setCash(double money)` | setters |
| 706 | 3 | `public void setMonth(int month)` |  |
| 710 | 3 | `public void setBuildingNum(int i)` |  |
| 714 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 721 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 726 | 3 | `public void setProgress(double[] progress)` |  |
| 730 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 734 | 3 | `public void setPopulation(long population)` |  |
| 737 | 3 | `public void setReports(boolean reports)` |  |
| 750 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 753 | 3 | `public void setGraphs(boolean graphs)` |  |
| 782 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 807 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 833 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 840-931)

| line | len | member | says |
|---:|---:|---|---|
| 853 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 854 | 1 | `public int[] getStackOrder()` |  |
| 856 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 864 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 866 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 868 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 870 | 1 | `public void setConstructionControl(ConstructionControl.State state)` |  |
| 872 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 874 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 876 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 878 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 880 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 882 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 884 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 886 | 1 | `public void setZoning(Zoning.State state)` |  |
| 888 | 1 | `public Zoning.State getZoning()` | Null on a save from before 0.7.107, or with nothing zoned, no law and no forest cleared. |
| 890 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 892 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 895 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 899 | 3 | `public int getConstructionByIdLength()` |  |
| 903 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 910 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 917 | 6 | `public double getContractValueById(int templateId)` |  |
| 925 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 932-1013)

| line | len | member | says |
|---:|---:|---|---|
| 934 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 935 | 1 | `public double getPropertyTaxCharged()` |  |
| 937 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 938 | 1 | `public double getCityInterestAccrued()` |  |
| 940 | 1 | `public void setWorkforce(long workforce)` |  |
| 943 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 946 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 964 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 966 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 968 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 970 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 972 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 974 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 976 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 977 | 1 | `public double[][] getLandOffers()` |  |
| 979 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 980 | 1 | `public double[] getDepletion()` |  |
| 982 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 983 | 1 | `public void setDepletionByType(double[][] byType)` |  |
| 985 | 1 | `public double[][] getDepletionByType()` | E by type as saved (0.7.110), a row per resource or null for the walk's reading; null on a save that carries none. |
| 987 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 989 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 991 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 992 | 1 | `public double[] getWorldTotals()` |  |
| 994 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 996 | 1 | `public void setFreshRights(double units)` |  |
| 998 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 1000 | 1 | `public void setMapStamp(Long stamp)` |  |
| 1002 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 1005 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 1006 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 1007 | 1 | `public double[] getLandMarketPrices()` |  |
| 1009 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 1011 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 1014-2213)

| line | len | member | says |
|---:|---:|---|---|
| 1080 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 1081 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 1082 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 1083 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 1084 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 1085 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 1087 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 1088 | 1 | `public double[] getTaxPolicyState()` |  |
| 1090 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 1091 | 1 | `public double[] getHouseholdBalance()` |  |
| 1093 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1097 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1098 | 1 | `public double[] getHouseholdCells()` |  |
| 1100 | 1 | `public void setBankCash(double cash)` |  |
| 1101 | 1 | `public double getBankCash()` |  |
| 1114 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1115 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1126 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1127 | 1 | `public double getBankProfitLastMonth()` |  |
| 1131 | 1 | `public void setBankDepositRate(double v)` |  |
| 1132 | 1 | `public double getBankDepositRate()` |  |
| 1148 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1149 | 1 | `public double[] getForeignAccounts()` |  |
| 1161 | 1 | `public void setCentralBank(double[] state)` |  |
| 1162 | 1 | `public double[] getCentralBank()` |  |
| 1173 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1177 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1178 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1190 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1191 | 1 | `public double[] getForeignStanding()` |  |
| 1202 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1203 | 1 | `public double[] getCapitalFlows()` |  |
| 1213 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1214 | 1 | `public double[] getOutwardInvestment()` |  |
| 1225 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1226 | 1 | `public String[] getEquityKeys()` |  |
| 1227 | 1 | `public double[] getEquity()` |  |
| 1231 | 1 | `public void setExchange(double[] state)` |  |
| 1232 | 1 | `public double[] getExchange()` |  |
| 1236 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 1237 | 1 | `public Exchange.State getExchangeState()` |  |
| 1255 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1256 | 1 | `public double[] getBankSolvency()` |  |
| 1274 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1275 | 1 | `public double[] getHousingOccupancy()` |  |
| 1291 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1292 | 1 | `public double[] getBankLastMonth()` |  |
| 1303 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1304 | 1 | `public double[] getBankPricingHistory()` |  |
| 1315 | 1 | `public void setBankLateProfit(double value)` |  |
| 1316 | 1 | `public double getBankLateProfit()` |  |
| 1330 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1331 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1342 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1343 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1353 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1354 | 1 | `public double[] getBankMonthLines()` |  |
| 1365 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1366 | 1 | `public double[] getBankStatementYear()` |  |
| 1378 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1379 | 1 | `public double[] getBankSheetYear()` |  |
| 1391 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1392 | 1 | `public Double getBankPaidInOpening()` |  |
| 1393 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1394 | 1 | `public Double getBankRetainedOpening()` |  |
| 1405 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1409 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1410 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1421 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1422 | 1 | `public TreasuryFund.State getFund()` |  |
| 1427 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1428 | 1 | `public double[] getDebtMarket()` |  |
| 1439 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1440 | 1 | `public double[] getPriceIndex()` |  |
| 1453 | 1 | `public void setExpectations(double[] state)` |  |
| 1454 | 1 | `public double[] getExpectations()` |  |
| 1466 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1467 | 1 | `public double[] getWorldEconomy()` |  |
| 1482 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1483 | 1 | `public double getTradedExchangeRate()` |  |
| 1497 | 1 | `public void setDenomination(double[] state)` |  |
| 1498 | 1 | `public double[] getDenomination()` |  |
| 1512 | 1 | `public void setPolicyRate(double rate)` |  |
| 1514 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1523 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1524 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1537 | 1 | `public void setRolloverMode(String mode)` |  |
| 1539 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1544 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1545 | 1 | `public double[] getRolloverLedger()` |  |
| 1546 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1547 | 1 | `public double[] getRolloverRecord()` |  |
| 1557 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1558 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1570 | 1 | `public void setInflationTarget(double target)` |  |
| 1572 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1582 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1584 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 1595 | 1 | `public void setQeTargetShare(double share)` |  |
| 1596 | 1 | `public double getQeTargetShare()` |  |
| 1609 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1611 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1622 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1623 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1644 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1645 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1646 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1647 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1648 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1649 | 1 | `public double getHouseholdBondRatio()` |  |
| 1650 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1651 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1662 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1666 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1667 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1691 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1692 | 1 | `public double getCarsPerHousehold()` |  |
| 1705 | 1 | `public void setTransitBill(double v)` |  |
| 1706 | 1 | `public double getTransitBill()` |  |
| 1718 | 1 | `public void setCaptiveShare(double v)` |  |
| 1719 | 1 | `public double getCaptiveShare()` |  |
| 1720 | 1 | `public void setFuelPerJourney(double v)` |  |
| 1721 | 1 | `public double getFuelPerJourney()` |  |
| 1732 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 1733 | 1 | `public double[] getHouseholdFuel()` |  |
| 1745 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 1746 | 1 | `public double[] getHouseholdGraduates()` |  |
| 1748 | 1 | `public void setRememberedCommute(double v)` |  |
| 1749 | 3 | `public double getRememberedCommute()` |  |
| 1762 | 1 | `public void setCostOfLiving(double v)` |  |
| 1763 | 1 | `public double getCostOfLiving()` |  |
| 1776 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1777 | 1 | `public double getBankTaxCharged()` |  |
| 1781 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1782 | 1 | `public double getRentWeight()` |  |
| 1802 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1803 | 1 | `public double getRentWeightStudio()` |  |
| 1805 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1809 | 1 | `public int getConstructionShedMonth()` |  |
| 1810 | 1 | `public double getConstructionShedPoints()` |  |
| 1812 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1815 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1963 | 1 | `public void setLicences(double[] a)` |  |
| 1964 | 1 | `public double[] getLicences()` |  |
| 1965 | 1 | `public void setEducation(double[] a)` |  |
| 1966 | 1 | `public double[] getEducation()` |  |
| 1968 | 1 | `public void setLabour(double[] a)` |  |
| 1969 | 1 | `public double[] getLabour()` |  |
| 1970 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1971 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1973 | 1 | `public void setCohorts(double[] a)` |  |
| 1974 | 1 | `public double[] getCohorts()` |  |
| 1977 | 1 | `public void setBandNames(String[] names)` |  |
| 1979 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1993 | 1 | `public void setShapeNames(String[] names)` |  |
| 1994 | 1 | `public String[] getShapeNames()` |  |
| 1995 | 1 | `public void setFamilies(double[] a)` |  |
| 1996 | 1 | `public double[] getFamilies()` |  |
| 1997 | 1 | `public void setMigration(double[] a)` |  |
| 1998 | 1 | `public double[] getMigration()` |  |
| 1999 | 1 | `public void setUnemployment(double[] a)` |  |
| 2000 | 1 | `public double[] getUnemployment()` |  |
| 2001 | 1 | `public void setSickness(double[] a)` |  |
| 2002 | 1 | `public double[] getSickness()` |  |
| 2003 | 1 | `public void setCrime(double[] a)` |  |
| 2004 | 1 | `public double[] getCrime()` |  |
| 2005 | 1 | `public void setHealth(double[] a)` |  |
| 2006 | 1 | `public double[] getHealth()` |  |
| 2007 | 1 | `public void setHealthcare(double[] a)` |  |
| 2008 | 1 | `public double[] getHealthcare()` |  |
| 2036 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 2037 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 2038 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 2039 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 2040 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 2041 | 1 | `public double getCityCapitalSpending()` |  |
| 2059 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 2060 | 1 | `public double getCityMaintenancePaid()` |  |
| 2076 | 1 | `public void setLastInvestment(java.util.Map<String, String> m)` |  |
| 2077 | 1 | `public java.util.Map<String, String> getLastInvestment()` |  |
| 2078 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 2079 | 1 | `public double getMonthlyMaterialImports()` |  |
| 2080 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 2081 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 2082 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 2083 | 1 | `public int getMaterialsConsumed()` |  |
| 2092 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 2093 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 2103 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 2104 | 1 | `public double[] getHouseholdStatement()` |  |
| 2106 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 2109 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 2111 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2114 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2116 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2119 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2121 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2124 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2126 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2129 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2131 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2132 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2134 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2135 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2137 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2138 | 1 | `public double getInsurancePremiums()` |  |
| 2140 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2141 | 1 | `public double[] getNationalAccounts()` |  |
| 2143 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2144 | 1 | `public List<Double> getGdpRolling()` |  |
| 2154 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2157 | 3 | `public int getUnderConstruction(int index)` |  |
| 2160 | 3 | `public double getCash()` |  |
| 2164 | 3 | `public int getMonth()` |  |
| 2168 | 3 | `public int getBuildingQuantity(int index)` |  |
| 2172 | 3 | `public int getBuildingsLength()` |  |
| 2176 | 3 | `public JsonArray getDebt()` |  |
| 2180 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 2185 | 3 | `public JsonArray getBusinessDebt()` |  |
| 2189 | 3 | `public int getProgressLength()` |  |
| 2193 | 3 | `public double getProgress(int index)` |  |
| 2197 | 3 | `public int getConstructionMaterials()` |  |
| 2201 | 3 | `public long getPopulation()` |  |
| 2204 | 3 | `public boolean getReports()` |  |
| 2207 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2210 | 3 | `public boolean getGraphs()` |  |


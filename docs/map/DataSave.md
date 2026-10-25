# DataSave.java - 2,486 lines · 392 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Founding](Founding.md) (7), [SectorBooks](SectorBooks.md) (6), [World](World.md) (5), [DebtManager](DebtManager.md) (5), [Rollover](Rollover.md) (5), [Notice](Notice.md) (4), [BuildLog](BuildLog.md) (4), [Currency](Currency.md) (4), [GameFiles](GameFiles.md) (4), [Bank](Bank.md) (4), [CapSetting](CapSetting.md) (4), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Ports](Ports.md) (3), [Zoning](Zoning.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [OldMetal](OldMetal.md) (3), [DemolitionLog](DemolitionLog.md) (3), [SaveCopy](SaveCopy.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [Auction](Auction.md) (2), [EarthSite](EarthSite.md) (1)... and 2 more

**Used by (8):** [EarthTerrainCheck](EarthTerrainCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md)

## Sections

| line | section |
|---:|---|
| 247 | · land and ore |
| 344 | · the shedding warning |
| 560 | · the header |
| 1001 | · construction, by id |
| 1094 | · charged, not derived |
| 1193 | · policy |

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
| 284 | `private Double worldSeaTheta` |  |
| 285 | `private double[] worldTotals` |  |
| 286 | `private double[] landCentre` |  |
| 287 | `private double[][] landCentreRects` |  |
| 288 | `private double[][] landHoldings` |  |
| 289 | `private double[][] landPartFields` |  |
| 290 | `private double[][] landConverted` |  |
| 291 | `private double[] landLanes` |  |
| 292 | `private double[][] landPurchases` |  |
| 293 | `private double[][] landOffers` |  |
| 294 | `private double[] landCentreRelief` |  |
| 295 | `private double[][] landHoldingsRelief` |  |
| 296 | `private double[][] landOffersRelief` |  |
| 297 | `private Integer nextOfferId` |  |
| 298 | `private double[] depletion` |  |
| 306 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 316 | `private double[][] depletionByType` | What has been taken out of each typed resource by type (0.7.110, batch I1; LandManager's THE GROUND BY TYPE): a row per resource in Resource's order, its types in Resource.typesOf()'s order, null for a resource whose ... |
| 324 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 325 | `private double[] landListing` |  |
| 327 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 328 | `private Integer ironDeposits` |  |
| 329 | `private Double ironReserveTonnes` |  |
| 336 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 342 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 360 | `private int constructionShedMonth` |  |
| 361 | `private double constructionShedPoints` |  |
| 368 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 379 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 389 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 396 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 397 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 405 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 406 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 414 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 426 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 427 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 428 | `private double insurancePremiums` |  |
| 431 | `private int constructionMaterials` | The city's own yard, in units. |
| 432 | `private long population` |  |
| 443 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 444 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 474 | `private java.util.Map<String, double[][]> sectorDebt, sectorDebtBefore` | ...AND THE DEBT THEIR SHEETS SPLIT (FW2, 2026-10-24): R1's interest by instrument and R2's debt by kind and when it falls due, this month's and last month's, by sector (SectorBooks.Debt) - and what had moved each sect... |
| 475 | `private java.util.Map<String, double[][]> creditMoved` |  |
| 491 | `private java.util.Map<String, java.util.Map<String, double[]>> sectorStock` | The stock each sector's books revalue next month (FW6, 2026-10-25): every business's stock by good as the month closed, {units, price} under the good's name (SectorBooks.stockToSave()). |
| 501 | `private java.util.Map<String, double[]> sectorShares, sectorSharesBefore` | Each listed company's share as this month closed and as last month did (FW6, 2026-10-25), by sector, in SectorBooks.SHARES_SLOTS slots (SectorBooks.sharesToSave()). |
| 511 | `private boolean reports` | settings |
| 512 | `private boolean graphs` |  |
| 523 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 533 | `private double landOwned` | Land. |
| 534 | `private int landBlocksPurchased` |  |
| 535 | `private double landPricePerSqFt` |  |
| 544 | `private Double landWorksSqFt` | ET2 (spec-earth2-terrain 3.2, 4.2, 4.4): the part of what is built on that works stand on (LandManager.getWorksSqFt()), and the foothill prepared so far (getPreparedSqFt(), which never falls, so it is history and not ... |
| 545 | `private Double landPreparedSqFt` |  |
| 554 | `private double incomeTaxRate` | Tax rates. |
| 555 | `private double propertyTaxRate` |  |
| 661 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 679 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 680 | `private double[] treasuryJournalAmounts` |  |
| 681 | `private String[] treasuryJournalPendingLabels` |  |
| 682 | `private double[] treasuryJournalPendingAmounts` |  |
| 690 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 699 | `private double retiredGainPending` | The gain on paper the treasury has retired since the last strike - Game.retiredGainSoFar (CD3), the carrying value less the price paid - carried for the same reason: a city saved between two presses, after a buyback, ... |
| 708 | `private double[] interestBookedByKind` | The month's interest expense on each kind of paper, in DebtManager.BOOK_KINDS' order - DebtManager.interestByKind (CD6), the rows of the budget's interest line the Government tab's grid reads after the month. |
| 717 | `private double[][] curveBook` | The curve as the months behind it stood (CD6): one row a month, the newest DebtManager.CURVE_KEPT - [month, the dial, the city's zero rates at DebtManager.CURVE_TENORS, the world's at the same tenors] - DebtManager.cu... |
| 728 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 729 | `private double cityDiscountUnsettled` |  |
| 767 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 1012 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 1203 | `private double[] taxPolicyState` |  |
| 1216 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 1228 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 1229 | `private double[] householdCells` |  |
| 1241 | `private double bankCash` | The bank's cash. |
| 1251 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 1254 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 1255 | `private SalesTaxLedger.State salesTax` |  |
| 1257 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1290 | `private Double bankHouseholdDeposits, bankSectorDeposits` | The bank's deposit book as the month last set it (FW2, 2026-10-24; F-S1-1): what the families and the businesses had banked when Game.refreshBank() last ran in the month, which the load's own refreshBank() would re-re... |
| 1308 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1320 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1326 | `private double bankDepositRate` | What savers were paid. |
| 1342 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 1348 | `private double[] rateHistory` | Game's ring of the last twelve exchange rates and how full it is (XR1): the year's depreciation the hot money reads. |
| 1361 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1372 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1373 | `private double[] treasuryArrearsAmounts` |  |
| 1390 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1402 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1408 | `private String capitalFlowsStopReason` | The words the sudden stop gave when it began (FW6, 2026-10-25): a String the capital flows' array cannot hold, for the Trade screen's card. |
| 1419 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1430 | `private String[] equityKeys` | The share register, company by company, named. |
| 1431 | `private double[] equity` |  |
| 1438 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1443 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1462 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1481 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1498 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1510 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1522 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1537 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1549 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1560 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1572 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1585 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1597 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1598 | `private Double bankRetainedOpening` |  |
| 1611 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1612 | `private double[] bankPreferredRecord` |  |
| 1629 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1636 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1642 | `private double[] auctionTally` | The city's auctions counted (CD2, D33): AuctionTally.toSave(), fourteen slots. |
| 1654 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1668 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1681 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1697 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1708 | `private double struckRate` | The D$ per US$ the city's goods were struck at (AN1, EconomyManager's THE D$ THE GOODS ARE STRUCK AT): the state the next month's strike moves from, which neither the D$ nor the traded rate above can give back. |
| 1723 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1738 | `private Double policyRate` | The policy rate. |
| 1749 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1758 | `private String policyAim` | What the autopilot aims at (XR2; DebtManager.Aim), by name. |
| 1772 | `private Double pegSpread` | The spread the dial stands at over the US's rate under a peg (XR2), a fraction a year. |
| 1784 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1785 | `private double[] rolloverLedger` |  |
| 1786 | `private double[] rolloverRecord` |  |
| 1806 | `private String rolloverCapMode` | ...and the cap its auctions take (CD2, D28), the mode by name and the tail or yield beside it, two plain fields because a cap's Fill it carries no number and Gson writes no NaN (CapSetting). |
| 1807 | `private double rolloverCapValue` |  |
| 1824 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1837 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1849 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1862 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1876 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1889 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1908 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1909 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1910 | `private double householdBondRatio` |  |
| 1911 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1929 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1930 | `private double buybackAbroadUnsettled` |  |
| 1953 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1959 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1973 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 1986 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 2000 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 2013 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 2030 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 2044 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 2070 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 2103 | `private double[] cohorts` | The demographics. |
| 2127 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 2129 | `private double[] families` |  |
| 2130 | `private double[] migration` |  |
| 2140 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 2149 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 2159 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 2172 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 2185 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 2201 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 2213 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 2230 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 2231 | `private double[] education` |  |
| 2262 | `private String[] shapeNames` | The household shapes this save was written with. |
| 2295 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 2296 | `private java.util.List<Long> populationTrend` |  |
| 2297 | `private double cityCapitalSpending` |  |
| 2298 | `private double monthlyMaterialImports` |  |
| 2305 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 2306 | `private int materialsConsumed` |  |
| 2329 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 2346 | `private java.util.Map<String, String> lastInvestment` | THE INVESTORS' LAST WORD ON EACH SECTOR (0.7.102, Jerus's A22): what the month's investment pass decided for it and why - "Built 2 ...", "Holding: ...", a refusal - as Game.getLastInvestment() reads it, the Sectors sc... |
| 2363 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 2374 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2475 | **type** `public class DataSave implements Cloneable` |  |
| 223 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 224 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 225 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 226 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |
| 236 | 1 | `public void setOldMetal(OldMetal.State s)` |  |
| 238 | 1 | `public OldMetal.State getOldMetal()` | The old steel as saved (0.7.123), or null on a save from before. |

### land and ore (lines 247-343)

### the shedding warning (lines 344-559)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 450 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 454 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 458 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |
| 477 | 4 | `public void setSectorDebt(java.util.Map<String, double[][]> now, java.util.Map<String, double[][]> before)` |  |
| 481 | 1 | `public java.util.Map<String, double[][]> getSectorDebt()` |  |
| 482 | 1 | `public java.util.Map<String, double[][]> getSectorDebtBefore()` |  |
| 483 | 1 | `public void setCreditMoved(java.util.Map<String, double[][]> moved)` |  |
| 484 | 1 | `public java.util.Map<String, double[][]> getCreditMoved()` |  |
| 493 | 1 | `public void setSectorStock(java.util.Map<String, java.util.Map<String, double[]>> stock)` |  |
| 494 | 1 | `public java.util.Map<String, java.util.Map<String, double[]>> getSectorStock()` |  |
| 503 | 4 | `public void setSectorShares(java.util.Map<String, double[]> now, java.util.Map<String, double[]> before)` |  |
| 507 | 1 | `public java.util.Map<String, double[]> getSectorShares()` |  |
| 508 | 1 | `public java.util.Map<String, double[]> getSectorSharesBefore()` |  |

### the header (lines 560-1000)

| line | len | member | says |
|---:|---:|---|---|
| 562 | 1 | `public void setSlotName(String name)` |  |
| 565 | 18 | `public void setFounding(Founding f)` | The founding record, whole. |
| 594 | 23 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 619 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 622 | 1 | `public String getWorldKind()` | The world's kind as saved (0.7.121): "EARTH" on a city founded on the real Earth, null on a generated world's and on every save before 0.7.121. |
| 625 | 1 | `public boolean isOnEarth()` | Whether the save is a city on the real Earth (0.7.121): worldKind EARTH. |
| 628 | 1 | `public Double getEarthLat()` | The Earth's spot as saved, latitude in degrees (0.7.121); null on a generated world. |
| 631 | 1 | `public Double getEarthLon()` | ...its longitude. |
| 634 | 1 | `public String getEarthData()` | The Earth's data the city was drawn from, by identity ("earth-2:<hash>", 0.7.121); null on a generated world. |
| 645 | 5 | `public String earthProblem()` | Why this save's world cannot be read here, or null (0.7.121): a kind this build does not know, or an Earth city whose spot is missing or whose data is not this build's or not here, or (0.7.127) that was saved before i... |
| 652 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 663 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 664 | 1 | `public double[] getTreasuryMonth()` |  |
| 731 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 735 | 1 | `public double getCityPaperUnsettled()` |  |
| 736 | 1 | `public double getCityDiscountUnsettled()` |  |
| 738 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 745 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 746 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 747 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 748 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 749 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 750 | 1 | `public double getTreasuryRaisedPending()` |  |
| 751 | 1 | `public void setRetiredGainPending(double v)` |  |
| 752 | 1 | `public double getRetiredGainPending()` |  |
| 753 | 1 | `public void setInterestBookedByKind(double[] v)` |  |
| 754 | 1 | `public double[] getInterestBookedByKind()` |  |
| 755 | 1 | `public void setCurveBook(double[][] v)` |  |
| 756 | 1 | `public double[][] getCurveBook()` |  |
| 769 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 770 | 1 | `public double[] getGovernmentMonth()` |  |
| 771 | 1 | `public String getSlotName()` |  |
| 774 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 780 | 1 | `public String getGameVersion()` |  |
| 781 | 1 | `public int getSaveFormat()` |  |
| 782 | 1 | `public long getSavedAt()` |  |
| 784 | 1 | `public void setHouseholdSavings(double value)` |  |
| 785 | 1 | `public double getHouseholdSavings()` |  |
| 787 | 1 | `public void setLandOwned(double sqFt)` |  |
| 788 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 789 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 791 | 4 | `public void setLandWorks(double worksSqFt, double preparedSqFt)` | The works' ground and the foothill prepared (ET2): LandManager.getWorksSqFt() and getPreparedSqFt(). |
| 796 | 1 | `public double getLandOwned()` |  |
| 797 | 1 | `public int getLandBlocksPurchased()` |  |
| 798 | 1 | `public double getLandPricePerSqFt()` |  |
| 800 | 1 | `public Double getLandWorksSqFt()` | The works' ground as saved (ET2), or null on a save without it: read from the standing buildings. |
| 802 | 1 | `public Double getLandPreparedSqFt()` | The foothill prepared as saved (ET2), or null on a save without it. |
| 804 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 805 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 807 | 1 | `public double getIncomeTaxRate()` |  |
| 808 | 1 | `public double getPropertyTaxRate()` |  |
| 810 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 815 | 3 | `public void setCash(double money)` | setters |
| 819 | 3 | `public void setMonth(int month)` |  |
| 823 | 3 | `public void setBuildingNum(int i)` |  |
| 827 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 834 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 839 | 3 | `public void setProgress(double[] progress)` |  |
| 843 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 847 | 3 | `public void setPopulation(long population)` |  |
| 850 | 3 | `public void setReports(boolean reports)` |  |
| 863 | 4 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 867 | 3 | `public void setGraphs(boolean graphs)` |  |
| 903 | 28 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 950 | 9 | `public DataSave snapshot()` | A copy of this save for the writer thread (UP1, 2026-10-22): the object itself, shallow, plus a copy of every array or list DataSave mutates in place. |
| 968 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 994 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 1001-1093)

| line | len | member | says |
|---:|---:|---|---|
| 1014 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 1015 | 1 | `public int[] getStackOrder()` |  |
| 1017 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 1025 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 1027 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 1029 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 1032 | 1 | `public void setConstructionControl(ConstructionControl.State state)` | UP1: a copy - toState() is the control's own live state. |
| 1034 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 1036 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 1038 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 1040 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 1042 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 1044 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 1046 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 1048 | 1 | `public void setZoning(Zoning.State state)` |  |
| 1050 | 1 | `public Zoning.State getZoning()` | Null on a save from before 0.7.107, or with nothing zoned, no law and no forest cleared. |
| 1052 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 1054 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 1057 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 1061 | 3 | `public int getConstructionByIdLength()` |  |
| 1065 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 1072 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 1079 | 6 | `public double getContractValueById(int templateId)` |  |
| 1087 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 1094-1192)

| line | len | member | says |
|---:|---:|---|---|
| 1096 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 1097 | 1 | `public double getPropertyTaxCharged()` |  |
| 1099 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 1100 | 1 | `public double getCityInterestAccrued()` |  |
| 1102 | 1 | `public void setWorkforce(long workforce)` |  |
| 1105 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 1108 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 1130 | 5 | `public void setCityLandRelief(double[] centre, double[][] holdings, double[][] offers)` | The city's ground by relief and shallow sea (ET1): the centre's, a row for each purchase and a row for each offer standing, each CityLand.RELIEF_AREAS figures in km2 (CityLand.centreReliefState(), holdingsReliefState(... |
| 1137 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 1139 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 1141 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 1143 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 1145 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 1147 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 1149 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 1150 | 1 | `public double[][] getLandOffers()` |  |
| 1152 | 1 | `public double[] getLandCentreRelief()` | The centre's dry ground by relief and shallow sea as saved (ET1), or null on a save from before: all flat. |
| 1154 | 1 | `public double[][] getLandHoldingsRelief()` | The purchases' relief, a row each in landHoldings' order, or null: all flat. |
| 1156 | 1 | `public double[][] getLandOffersRelief()` | The offers' relief, a row each in landOffers' order, or null: all flat. |
| 1158 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 1159 | 1 | `public double[] getDepletion()` |  |
| 1161 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 1162 | 1 | `public void setDepletionByType(double[][] byType)` |  |
| 1164 | 1 | `public double[][] getDepletionByType()` | E by type as saved (0.7.110), a row per resource or null for the walk's reading; null on a save that carries none. |
| 1166 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 1168 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 1170 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 1171 | 1 | `public double[] getWorldTotals()` |  |
| 1173 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 1175 | 1 | `public void setFreshRights(double units)` |  |
| 1177 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 1179 | 1 | `public void setMapStamp(Long stamp)` |  |
| 1181 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 1184 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 1185 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 1186 | 1 | `public double[] getLandMarketPrices()` |  |
| 1188 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 1190 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 1193-2486)

| line | len | member | says |
|---:|---:|---|---|
| 1259 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 1260 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 1261 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 1262 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 1263 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 1264 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 1266 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 1267 | 1 | `public double[] getTaxPolicyState()` |  |
| 1269 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 1270 | 1 | `public double[] getHouseholdBalance()` |  |
| 1272 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1276 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1277 | 1 | `public double[] getHouseholdCells()` |  |
| 1279 | 1 | `public void setBankCash(double cash)` |  |
| 1280 | 1 | `public double getBankCash()` |  |
| 1292 | 4 | `public void setBankDeposits(double household, double sector)` |  |
| 1296 | 1 | `public Double getBankHouseholdDeposits()` |  |
| 1297 | 1 | `public Double getBankSectorDeposits()` |  |
| 1310 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1311 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1322 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1323 | 1 | `public double getBankProfitLastMonth()` |  |
| 1327 | 1 | `public void setBankDepositRate(double v)` |  |
| 1328 | 1 | `public double getBankDepositRate()` |  |
| 1344 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1345 | 1 | `public double[] getForeignAccounts()` |  |
| 1350 | 1 | `public void setRateHistory(double[] ring)` |  |
| 1351 | 1 | `public double[] getRateHistory()` |  |
| 1363 | 1 | `public void setCentralBank(double[] state)` |  |
| 1364 | 1 | `public double[] getCentralBank()` |  |
| 1375 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1379 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1380 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1392 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1393 | 1 | `public double[] getForeignStanding()` |  |
| 1404 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1405 | 1 | `public double[] getCapitalFlows()` |  |
| 1410 | 1 | `public void setCapitalFlowsStopReason(String reason)` |  |
| 1411 | 1 | `public String getCapitalFlowsStopReason()` |  |
| 1421 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1422 | 1 | `public double[] getOutwardInvestment()` |  |
| 1433 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1434 | 1 | `public String[] getEquityKeys()` |  |
| 1435 | 1 | `public double[] getEquity()` |  |
| 1439 | 1 | `public void setExchange(double[] state)` |  |
| 1440 | 1 | `public double[] getExchange()` |  |
| 1445 | 1 | `public void setExchangeState(Exchange.State state)` | UP1: a copy - toState() lists the exchange's own books. |
| 1446 | 1 | `public Exchange.State getExchangeState()` |  |
| 1464 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1465 | 1 | `public double[] getBankSolvency()` |  |
| 1483 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1484 | 1 | `public double[] getHousingOccupancy()` |  |
| 1500 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1501 | 1 | `public double[] getBankLastMonth()` |  |
| 1512 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1513 | 1 | `public double[] getBankPricingHistory()` |  |
| 1524 | 1 | `public void setBankLateProfit(double value)` |  |
| 1525 | 1 | `public double getBankLateProfit()` |  |
| 1539 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1540 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1551 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1552 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1562 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1563 | 1 | `public double[] getBankMonthLines()` |  |
| 1574 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1575 | 1 | `public double[] getBankStatementYear()` |  |
| 1587 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1588 | 1 | `public double[] getBankSheetYear()` |  |
| 1600 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1601 | 1 | `public Double getBankPaidInOpening()` |  |
| 1602 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1603 | 1 | `public Double getBankRetainedOpening()` |  |
| 1614 | 5 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1619 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1620 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1632 | 1 | `public void setFund(TreasuryFund.State state)` | UP1: a copy - toState() lists the fund's own resolutions and orders. |
| 1633 | 1 | `public TreasuryFund.State getFund()` |  |
| 1638 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1639 | 1 | `public double[] getDebtMarket()` |  |
| 1644 | 1 | `public void setAuctionTally(double[] tally)` |  |
| 1645 | 1 | `public double[] getAuctionTally()` |  |
| 1656 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1657 | 1 | `public double[] getPriceIndex()` |  |
| 1670 | 1 | `public void setExpectations(double[] state)` |  |
| 1671 | 1 | `public double[] getExpectations()` |  |
| 1683 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1684 | 1 | `public double[] getWorldEconomy()` |  |
| 1699 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1700 | 1 | `public double getTradedExchangeRate()` |  |
| 1710 | 1 | `public void setStruckRate(double rate)` |  |
| 1711 | 1 | `public double getStruckRate()` |  |
| 1725 | 1 | `public void setDenomination(double[] state)` |  |
| 1726 | 1 | `public double[] getDenomination()` |  |
| 1740 | 1 | `public void setPolicyRate(double rate)` |  |
| 1742 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1751 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1752 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1760 | 1 | `public void setPolicyAim(String name)` |  |
| 1761 | 6 | `public DebtManager.Aim getPolicyAim()` |  |
| 1774 | 1 | `public void setPegSpread(double spread)` |  |
| 1775 | 1 | `public double getPegSpread()` |  |
| 1788 | 1 | `public void setRolloverMode(String mode)` |  |
| 1790 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1795 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1796 | 1 | `public double[] getRolloverLedger()` |  |
| 1797 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1798 | 1 | `public double[] getRolloverRecord()` |  |
| 1809 | 5 | `public void setRolloverCap(Auction.Cap cap)` |  |
| 1814 | 3 | `public Auction.Cap getRolloverCap()` |  |
| 1826 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1827 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1839 | 1 | `public void setInflationTarget(double target)` |  |
| 1841 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1851 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1853 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 1864 | 1 | `public void setQeTargetShare(double share)` |  |
| 1865 | 1 | `public double getQeTargetShare()` |  |
| 1878 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1880 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1891 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1892 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1914 | 1 | `public void setBondMarket(BondMarket.State state)` | UP1: a copy - toState() lists the market's own bonds and books. |
| 1915 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1916 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1917 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1918 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1919 | 1 | `public double getHouseholdBondRatio()` |  |
| 1920 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1921 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1932 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1936 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1937 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1961 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1962 | 1 | `public double getCarsPerHousehold()` |  |
| 1975 | 1 | `public void setTransitBill(double v)` |  |
| 1976 | 1 | `public double getTransitBill()` |  |
| 1988 | 1 | `public void setCaptiveShare(double v)` |  |
| 1989 | 1 | `public double getCaptiveShare()` |  |
| 1990 | 1 | `public void setFuelPerJourney(double v)` |  |
| 1991 | 1 | `public double getFuelPerJourney()` |  |
| 2002 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 2003 | 1 | `public double[] getHouseholdFuel()` |  |
| 2015 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 2016 | 1 | `public double[] getHouseholdGraduates()` |  |
| 2018 | 1 | `public void setRememberedCommute(double v)` |  |
| 2019 | 3 | `public double getRememberedCommute()` |  |
| 2032 | 1 | `public void setCostOfLiving(double v)` |  |
| 2033 | 1 | `public double getCostOfLiving()` |  |
| 2046 | 1 | `public void setBankTaxCharged(double v)` |  |
| 2047 | 1 | `public double getBankTaxCharged()` |  |
| 2051 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 2052 | 1 | `public double getRentWeight()` |  |
| 2072 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 2073 | 1 | `public double getRentWeightStudio()` |  |
| 2075 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 2079 | 1 | `public int getConstructionShedMonth()` |  |
| 2080 | 1 | `public double getConstructionShedPoints()` |  |
| 2082 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 2085 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 2234 | 1 | `public void setLicences(double[] a)` | UP1: a copy, not the live head counts - PopulationManager hands out its own arrays. |
| 2235 | 1 | `public double[] getLicences()` |  |
| 2236 | 1 | `public void setEducation(double[] a)` |  |
| 2237 | 1 | `public double[] getEducation()` |  |
| 2239 | 1 | `public void setLabour(double[] a)` |  |
| 2240 | 1 | `public double[] getLabour()` |  |
| 2242 | 1 | `public void setSkilledWorkforce(double[] a)` | UP1: a copy, not the live head counts. |
| 2243 | 1 | `public double[] getSkilledWorkforce()` |  |
| 2245 | 1 | `public void setCohorts(double[] a)` |  |
| 2246 | 1 | `public double[] getCohorts()` |  |
| 2249 | 1 | `public void setBandNames(String[] names)` |  |
| 2251 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 2265 | 1 | `public void setShapeNames(String[] names)` |  |
| 2266 | 1 | `public String[] getShapeNames()` |  |
| 2267 | 1 | `public void setFamilies(double[] a)` |  |
| 2268 | 1 | `public double[] getFamilies()` |  |
| 2269 | 1 | `public void setMigration(double[] a)` |  |
| 2270 | 1 | `public double[] getMigration()` |  |
| 2271 | 1 | `public void setUnemployment(double[] a)` |  |
| 2272 | 1 | `public double[] getUnemployment()` |  |
| 2273 | 1 | `public void setSickness(double[] a)` |  |
| 2274 | 1 | `public double[] getSickness()` |  |
| 2275 | 1 | `public void setCrime(double[] a)` |  |
| 2276 | 1 | `public double[] getCrime()` |  |
| 2277 | 1 | `public void setHealth(double[] a)` |  |
| 2278 | 1 | `public double[] getHealth()` |  |
| 2279 | 1 | `public void setHealthcare(double[] a)` |  |
| 2280 | 1 | `public double[] getHealthcare()` |  |
| 2308 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 2309 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 2310 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 2311 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 2312 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 2313 | 1 | `public double getCityCapitalSpending()` |  |
| 2331 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 2332 | 1 | `public double getCityMaintenancePaid()` |  |
| 2348 | 1 | `public void setLastInvestment(java.util.Map<String, String> m)` |  |
| 2349 | 1 | `public java.util.Map<String, String> getLastInvestment()` |  |
| 2350 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 2351 | 1 | `public double getMonthlyMaterialImports()` |  |
| 2352 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 2353 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 2354 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 2355 | 1 | `public int getMaterialsConsumed()` |  |
| 2364 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 2365 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 2375 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 2376 | 1 | `public double[] getHouseholdStatement()` |  |
| 2378 | 4 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 2382 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 2384 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2387 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2389 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2392 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2394 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2397 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2399 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2402 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2404 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2405 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2407 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2408 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2410 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2411 | 1 | `public double getInsurancePremiums()` |  |
| 2413 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2414 | 1 | `public double[] getNationalAccounts()` |  |
| 2416 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2417 | 1 | `public List<Double> getGdpRolling()` |  |
| 2427 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2430 | 3 | `public int getUnderConstruction(int index)` |  |
| 2433 | 3 | `public double getCash()` |  |
| 2437 | 3 | `public int getMonth()` |  |
| 2441 | 3 | `public int getBuildingQuantity(int index)` |  |
| 2445 | 3 | `public int getBuildingsLength()` |  |
| 2449 | 3 | `public JsonArray getDebt()` |  |
| 2453 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 2458 | 3 | `public JsonArray getBusinessDebt()` |  |
| 2462 | 3 | `public int getProgressLength()` |  |
| 2466 | 3 | `public double getProgress(int index)` |  |
| 2470 | 3 | `public int getConstructionMaterials()` |  |
| 2474 | 3 | `public long getPopulation()` |  |
| 2477 | 3 | `public boolean getReports()` |  |
| 2480 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2483 | 3 | `public boolean getGraphs()` |  |


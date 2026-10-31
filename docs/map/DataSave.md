# DataSave.java - 2,622 lines · 412 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Founding](Founding.md) (7), [SectorBooks](SectorBooks.md) (6), [World](World.md) (5), [Nations](Nations.md) (5), [DebtManager](DebtManager.md) (5), [Rollover](Rollover.md) (5), [Notice](Notice.md) (4), [BuildLog](BuildLog.md) (4), [Currency](Currency.md) (4), [GameFiles](GameFiles.md) (4), [Bank](Bank.md) (4), [CapSetting](CapSetting.md) (4), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Ports](Ports.md) (3), [Zoning](Zoning.md) (3), [Landmasses](Landmasses.md) (3), [Bridges](Bridges.md) (3), [PartnerTrade](PartnerTrade.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [OldMetal](OldMetal.md) (3), [DemolitionLog](DemolitionLog.md) (3), [SaveCopy](SaveCopy.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3)... and 6 more

**Used by (9):** [EarthTerrainCheck](EarthTerrainCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [LandmassCheck](LandmassCheck.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md)

## Sections

| line | section |
|---:|---|
| 300 | · land and ore |
| 397 | · the shedding warning |
| 620 | · the header |
| 1084 | · construction, by id |
| 1219 | · charged, not derived |
| 1318 | · policy |

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
| 84 | `private String worldPath` | ...AND THE PATH OF THE WORLD BEYOND IT (WN1; spec-nations 7.3): which path the nations take, the enum's name() ("HISTORICAL" or "RANDOM"), and the seed that draws it - not the ground's worldSeed above. |
| 85 | `private Long pathSeed` |  |
| 88 | `private double cash` | save variables |
| 89 | `private int[] buildings` |  |
| 90 | `private int month` |  |
| 91 | `private JsonArray debts` |  |
| 99 | `private JsonArray businessDebts` | Private-sector loans. |
| 106 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 107 | `private int[] underConstruction` |  |
| 118 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 119 | `private int[] underConstructionById` |  |
| 121 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 123 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 125 | `private java.util.List<BuildingManager.ContractRecord> contractRecords` | ...and who placed each part of it, with its material still to draw and the allowance priced in for it (0.7.19). |
| 130 | `private Long foundingEndowments` | How many founding endowments the city holds (FW9; BuildingManager.getFoundingEndowments()): 1 for every city a player founds, K for a harness's copy of one K times over. |
| 140 | `private ConstructionControl.State constructionControl` | The player's hand on the queue (0.7.22; ConstructionControl): the city's order of its own sites, the rushes and their months on overtime, the cancels waiting for the month's end, the stopped shells, the demolitions on... |
| 148 | `private java.util.List<DecisionLog.Entry> decisionLog` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, a month, a kind and a line each. |
| 156 | `private AutoBuilder.State autoBuild` | Automatic building (0.7.73; AutoBuilder): the switch, the spare margin and the debt limit, its log of orders and what held it back at its last pass. |
| 166 | `private StrategicReserve.State reserve` | The city's strategic reserve (0.7.85, batch O8; StrategicReserve): the crude its tanks hold and what it cost, the release standing and the fill ordered, and the month's trades the next strike settles (runs/ spec-oil.m... |
| 177 | `private Ports.State portMonth` | The city's ports (0.7.86, batch O9; Ports): each kind of cargo's share of its tonnes at sea in force, whether it goes before the railway, the class crude goes in, the road's two shares, and the month's tonnes by kind ... |
| 188 | `private Zoning.State zoning` | The city's zones, its forest law and the forest cleared for building (0.7.107, batch Z1; Zoning, LandManager's ZONES AND THE FOREST LAW): each zone's sector, size and its kind, price share and the ground used in it; t... |
| 197 | `private Landmasses.State landmasses` | Where the city's sea terminals stand (ET5, 2026-10-25; Landmasses): per terminal template the landmass ids of its standing, on-site and stopped units, oldest first, and of the units a demolition on site is taking down. |
| 205 | `private Bridges.State bridges` | The city's long bridges (ET6, 2026-10-26; Bridges): each one built or building - its template, kind, the two landings and the landmass at each end, its length, the deepest and mean depth of the water under it, the cor... |
| 215 | `private Integer worldYear` | The world market (WN3, 2026-10-28; WorldMarket, Game's THE WORLD MARKET), when the city has one - a world file was there: the year its January last solved (worldYear), and the solved state, every modelled good's mill ... |
| 216 | `private double[] worldPrices` |  |
| 217 | `private double[] worldScales` |  |
| 218 | `private PartnerTrade.State partnerTrade` |  |
| 226 | `private double[] worldMoneys` | The world's moneys (WN4, 2026-10-30; WorldMoneys, Game's THE WORLD'S MONEYS IN THE MONTH), when the city has a world market: the month they stand in, the dollar cycle, the risk-off and each money's rate, swing, terms ... |
| 247 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 262 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 273 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 274 | `private java.util.List<Markets.State> markets` |  |
| 287 | `private OldMetal.State oldMetal` | The city's old steel (0.7.123, batch I4; OldMetal): what it has thrown out since its Scrap Yards last bought, by seller and by source, and the yards' last month. |
| 298 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 337 | `private Double worldSeaTheta` |  |
| 338 | `private double[] worldTotals` |  |
| 339 | `private double[] landCentre` |  |
| 340 | `private double[][] landCentreRects` |  |
| 341 | `private double[][] landHoldings` |  |
| 342 | `private double[][] landPartFields` |  |
| 343 | `private double[][] landConverted` |  |
| 344 | `private double[] landLanes` |  |
| 345 | `private double[][] landPurchases` |  |
| 346 | `private double[][] landOffers` |  |
| 347 | `private double[] landCentreRelief` |  |
| 348 | `private double[][] landHoldingsRelief` |  |
| 349 | `private double[][] landOffersRelief` |  |
| 350 | `private Integer nextOfferId` |  |
| 351 | `private double[] depletion` |  |
| 359 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 369 | `private double[][] depletionByType` | What has been taken out of each typed resource by type (0.7.110, batch I1; LandManager's THE GROUND BY TYPE): a row per resource in Resource's order, its types in Resource.typesOf()'s order, null for a resource whose ... |
| 377 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 378 | `private double[] landListing` |  |
| 380 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 381 | `private Integer ironDeposits` |  |
| 382 | `private Double ironReserveTonnes` |  |
| 389 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 395 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 413 | `private int constructionShedMonth` |  |
| 414 | `private double constructionShedPoints` |  |
| 421 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 432 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 442 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 449 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 450 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 458 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 459 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 467 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 479 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 480 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 481 | `private double insurancePremiums` |  |
| 484 | `private int constructionMaterials` | The city's own yard, in units. |
| 485 | `private long population` |  |
| 496 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 497 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 527 | `private java.util.Map<String, double[][]> sectorDebt, sectorDebtBefore` | ...AND THE DEBT THEIR SHEETS SPLIT (FW2, 2026-10-24): R1's interest by instrument and R2's debt by kind and when it falls due, this month's and last month's, by sector (SectorBooks.Debt) - and what had moved each sect... |
| 528 | `private java.util.Map<String, double[][]> creditMoved` |  |
| 544 | `private java.util.Map<String, java.util.Map<String, double[]>> sectorStock` | The stock each sector's books revalue next month (FW6, 2026-10-25): every business's stock by good as the month closed, {units, price} under the good's name (SectorBooks.stockToSave()). |
| 554 | `private java.util.Map<String, double[]> sectorShares, sectorSharesBefore` | Each listed company's share as this month closed and as last month did (FW6, 2026-10-25), by sector, in SectorBooks.SHARES_SLOTS slots (SectorBooks.sharesToSave()). |
| 564 | `private boolean reports` | settings |
| 565 | `private boolean graphs` |  |
| 576 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 586 | `private double landOwned` | Land. |
| 587 | `private int landBlocksPurchased` |  |
| 588 | `private double landPricePerSqFt` |  |
| 597 | `private Double landWorksSqFt` | ET2 (spec-earth2-terrain 3.2, 4.2, 4.4): the part of what is built on that works stand on (LandManager.getWorksSqFt()), and the foothill prepared so far (getPreparedSqFt(), which never falls, so it is history and not ... |
| 598 | `private Double landPreparedSqFt` |  |
| 605 | `private Double landRunsSqFt` | ET4 (spec-earth2-terrain 4.3): the part of what is built on that highways and railway lines stand on (LandManager.getRunsSqFt()). |
| 614 | `private double incomeTaxRate` | Tax rates. |
| 615 | `private double propertyTaxRate` |  |
| 740 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 758 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 759 | `private double[] treasuryJournalAmounts` |  |
| 760 | `private String[] treasuryJournalPendingLabels` |  |
| 761 | `private double[] treasuryJournalPendingAmounts` |  |
| 769 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 778 | `private double retiredGainPending` | The gain on paper the treasury has retired since the last strike - Game.retiredGainSoFar (CD3), the carrying value less the price paid - carried for the same reason: a city saved between two presses, after a buyback, ... |
| 787 | `private double[] interestBookedByKind` | The month's interest expense on each kind of paper, in DebtManager.BOOK_KINDS' order - DebtManager.interestByKind (CD6), the rows of the budget's interest line the Government tab's grid reads after the month. |
| 796 | `private double[][] curveBook` | The curve as the months behind it stood (CD6): one row a month, the newest DebtManager.CURVE_KEPT - [month, the dial, the city's zero rates at DebtManager.CURVE_TENORS, the world's at the same tenors] - DebtManager.cu... |
| 807 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 808 | `private double cityDiscountUnsettled` |  |
| 846 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 1095 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 1328 | `private double[] taxPolicyState` |  |
| 1341 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 1353 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 1354 | `private double[] householdCells` |  |
| 1366 | `private double bankCash` | The bank's cash. |
| 1374 | `private Double bankSecurities` | The desk's inventory as the bank carries it (Bank.getSecurities(), FL1): the mark of its last re-mark, the exchange's step. |
| 1384 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 1387 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 1388 | `private SalesTaxLedger.State salesTax` |  |
| 1390 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1426 | `private Double bankHouseholdDeposits, bankSectorDeposits` | The bank's deposit book as the month last set it (FW2, 2026-10-24; F-S1-1): what the families and the businesses had banked when Game.refreshBank() last ran in the month, which the load's own refreshBank() would re-re... |
| 1444 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1456 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1462 | `private double bankDepositRate` | What savers were paid. |
| 1478 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 1484 | `private double[] rateHistory` | Game's ring of the last twelve exchange rates and how full it is (XR1): the year's depreciation the hot money reads. |
| 1497 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1508 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1509 | `private double[] treasuryArrearsAmounts` |  |
| 1526 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1538 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1544 | `private String capitalFlowsStopReason` | The words the sudden stop gave when it began (FW6, 2026-10-25): a String the capital flows' array cannot hold, for the Trade screen's card. |
| 1555 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1566 | `private String[] equityKeys` | The share register, company by company, named. |
| 1567 | `private double[] equity` |  |
| 1574 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1579 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1598 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1617 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1634 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1646 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1658 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1673 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1685 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1696 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1708 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1721 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1733 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1734 | `private Double bankRetainedOpening` |  |
| 1747 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1748 | `private double[] bankPreferredRecord` |  |
| 1765 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1772 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1778 | `private double[] auctionTally` | The city's auctions counted (CD2, D33): AuctionTally.toSave(), fourteen slots. |
| 1790 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1804 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1817 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1833 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1844 | `private double struckRate` | The D$ per US$ the city's goods were struck at (AN1, EconomyManager's THE D$ THE GOODS ARE STRUCK AT): the state the next month's strike moves from, which neither the D$ nor the traded rate above can give back. |
| 1859 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1874 | `private Double policyRate` | The policy rate. |
| 1885 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1894 | `private String policyAim` | What the autopilot aims at (XR2; DebtManager.Aim), by name. |
| 1908 | `private Double pegSpread` | The spread the dial stands at over the US's rate under a peg (XR2), a fraction a year. |
| 1920 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1921 | `private double[] rolloverLedger` |  |
| 1922 | `private double[] rolloverRecord` |  |
| 1942 | `private String rolloverCapMode` | ...and the cap its auctions take (CD2, D28), the mode by name and the tail or yield beside it, two plain fields because a cap's Fill it carries no number and Gson writes no NaN (CapSetting). |
| 1943 | `private double rolloverCapValue` |  |
| 1960 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1973 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1985 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1998 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 2012 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 2025 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 2044 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 2045 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 2046 | `private double householdBondRatio` |  |
| 2047 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 2065 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 2066 | `private double buybackAbroadUnsettled` |  |
| 2089 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 2095 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 2109 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 2122 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 2136 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 2149 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 2166 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 2180 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 2206 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 2239 | `private double[] cohorts` | The demographics. |
| 2263 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 2265 | `private double[] families` |  |
| 2266 | `private double[] migration` |  |
| 2276 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 2285 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 2295 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 2308 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 2321 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 2337 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 2349 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 2366 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 2367 | `private double[] education` |  |
| 2398 | `private String[] shapeNames` | The household shapes this save was written with. |
| 2431 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 2432 | `private java.util.List<Long> populationTrend` |  |
| 2433 | `private double cityCapitalSpending` |  |
| 2434 | `private double monthlyMaterialImports` |  |
| 2441 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 2442 | `private int materialsConsumed` |  |
| 2465 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 2482 | `private java.util.Map<String, String> lastInvestment` | THE INVESTORS' LAST WORD ON EACH SECTOR (0.7.102, Jerus's A22): what the month's investment pass decided for it and why - "Built 2 ...", "Holding: ...", a refusal - as Game.getLastInvestment() reads it, the Sectors sc... |
| 2499 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 2510 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2611 | **type** `public class DataSave implements Cloneable` |  |
| 276 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 277 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 278 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 279 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |
| 289 | 1 | `public void setOldMetal(OldMetal.State s)` |  |
| 291 | 1 | `public OldMetal.State getOldMetal()` | The old steel as saved (0.7.123), or null on a save from before. |

### land and ore (lines 300-396)

### the shedding warning (lines 397-619)

| line | len | member | says |
|---:|---:|---|---|
| 499 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 503 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 507 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 511 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |
| 530 | 4 | `public void setSectorDebt(java.util.Map<String, double[][]> now, java.util.Map<String, double[][]> before)` |  |
| 534 | 1 | `public java.util.Map<String, double[][]> getSectorDebt()` |  |
| 535 | 1 | `public java.util.Map<String, double[][]> getSectorDebtBefore()` |  |
| 536 | 1 | `public void setCreditMoved(java.util.Map<String, double[][]> moved)` |  |
| 537 | 1 | `public java.util.Map<String, double[][]> getCreditMoved()` |  |
| 546 | 1 | `public void setSectorStock(java.util.Map<String, java.util.Map<String, double[]>> stock)` |  |
| 547 | 1 | `public java.util.Map<String, java.util.Map<String, double[]>> getSectorStock()` |  |
| 556 | 4 | `public void setSectorShares(java.util.Map<String, double[]> now, java.util.Map<String, double[]> before)` |  |
| 560 | 1 | `public java.util.Map<String, double[]> getSectorShares()` |  |
| 561 | 1 | `public java.util.Map<String, double[]> getSectorSharesBefore()` |  |

### the header (lines 620-1083)

| line | len | member | says |
|---:|---:|---|---|
| 622 | 1 | `public void setSlotName(String name)` |  |
| 625 | 21 | `public void setFounding(Founding f)` | The founding record, whole. |
| 657 | 33 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 692 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 695 | 1 | `public String getWorldKind()` | The world's kind as saved (0.7.121): "EARTH" on a city founded on the real Earth, null on a generated world's and on every save before 0.7.121. |
| 698 | 1 | `public boolean isOnEarth()` | Whether the save is a city on the real Earth (0.7.121): worldKind EARTH. |
| 701 | 1 | `public Double getEarthLat()` | The Earth's spot as saved, latitude in degrees (0.7.121); null on a generated world. |
| 704 | 1 | `public Double getEarthLon()` | ...its longitude. |
| 707 | 1 | `public String getEarthData()` | The Earth's data the city was drawn from, by identity ("earth-2:<hash>", 0.7.121); null on a generated world. |
| 710 | 1 | `public String getWorldPath()` | The path of the world beyond the city as saved (WN1): "HISTORICAL" or "RANDOM", null on a save from before it. |
| 713 | 1 | `public Long getPathSeed()` | The path's seed as saved (WN1); null on a save from before it. |
| 724 | 5 | `public String earthProblem()` | Why this save's world cannot be read here, or null (0.7.121): a kind this build does not know, or an Earth city whose spot is missing or whose data is not this build's or not here, or (0.7.127) that was saved before i... |
| 731 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 742 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 743 | 1 | `public double[] getTreasuryMonth()` |  |
| 810 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 814 | 1 | `public double getCityPaperUnsettled()` |  |
| 815 | 1 | `public double getCityDiscountUnsettled()` |  |
| 817 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 824 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 825 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 826 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 827 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 828 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 829 | 1 | `public double getTreasuryRaisedPending()` |  |
| 830 | 1 | `public void setRetiredGainPending(double v)` |  |
| 831 | 1 | `public double getRetiredGainPending()` |  |
| 832 | 1 | `public void setInterestBookedByKind(double[] v)` |  |
| 833 | 1 | `public double[] getInterestBookedByKind()` |  |
| 834 | 1 | `public void setCurveBook(double[][] v)` |  |
| 835 | 1 | `public double[][] getCurveBook()` |  |
| 848 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 849 | 1 | `public double[] getGovernmentMonth()` |  |
| 850 | 1 | `public String getSlotName()` |  |
| 853 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 859 | 1 | `public String getGameVersion()` |  |
| 860 | 1 | `public int getSaveFormat()` |  |
| 861 | 1 | `public long getSavedAt()` |  |
| 863 | 1 | `public void setHouseholdSavings(double value)` |  |
| 864 | 1 | `public double getHouseholdSavings()` |  |
| 866 | 1 | `public void setLandOwned(double sqFt)` |  |
| 867 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 868 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 870 | 4 | `public void setLandWorks(double worksSqFt, double preparedSqFt)` | The works' ground and the foothill prepared (ET2): LandManager.getWorksSqFt() and getPreparedSqFt(). |
| 875 | 1 | `public void setLandRuns(double runsSqFt)` | The runs' ground (ET4): LandManager.getRunsSqFt(). |
| 877 | 1 | `public double getLandOwned()` |  |
| 878 | 1 | `public int getLandBlocksPurchased()` |  |
| 879 | 1 | `public double getLandPricePerSqFt()` |  |
| 881 | 1 | `public Double getLandWorksSqFt()` | The works' ground as saved (ET2), or null on a save without it: read from the standing buildings. |
| 883 | 1 | `public Double getLandPreparedSqFt()` | The foothill prepared as saved (ET2), or null on a save without it. |
| 885 | 1 | `public Double getLandRunsSqFt()` | The runs' ground as saved (ET4), or null on a save without it: read from the standing highways and railway lines. |
| 887 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 888 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 890 | 1 | `public double getIncomeTaxRate()` |  |
| 891 | 1 | `public double getPropertyTaxRate()` |  |
| 893 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 898 | 3 | `public void setCash(double money)` | setters |
| 902 | 3 | `public void setMonth(int month)` |  |
| 906 | 3 | `public void setBuildingNum(int i)` |  |
| 910 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 917 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 922 | 3 | `public void setProgress(double[] progress)` |  |
| 926 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 930 | 3 | `public void setPopulation(long population)` |  |
| 933 | 3 | `public void setReports(boolean reports)` |  |
| 946 | 4 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 950 | 3 | `public void setGraphs(boolean graphs)` |  |
| 986 | 28 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 1033 | 9 | `public DataSave snapshot()` | A copy of this save for the writer thread (UP1, 2026-10-22): the object itself, shallow, plus a copy of every array or list DataSave mutates in place. |
| 1051 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 1077 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 1084-1218)

| line | len | member | says |
|---:|---:|---|---|
| 1097 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 1098 | 1 | `public int[] getStackOrder()` |  |
| 1100 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 1108 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 1110 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 1112 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 1114 | 1 | `public void setFoundingEndowments(long endowments)` |  |
| 1116 | 1 | `public long getFoundingEndowments()` | The founding endowments as saved, or 1 on a save without the figure (FW9). |
| 1119 | 1 | `public void setConstructionControl(ConstructionControl.State state)` | UP1: a copy - toState() is the control's own live state. |
| 1121 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 1123 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 1125 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 1127 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 1129 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 1131 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 1133 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 1135 | 1 | `public void setZoning(Zoning.State state)` |  |
| 1137 | 1 | `public Zoning.State getZoning()` | Null on a save from before 0.7.107, or with nothing zoned, no law and no forest cleared. |
| 1139 | 1 | `public void setLandmasses(Landmasses.State state)` |  |
| 1141 | 1 | `public Landmasses.State getLandmasses()` |  |
| 1143 | 1 | `public void setBridges(Bridges.State state)` |  |
| 1146 | 1 | `public Bridges.State getBridges()` | Null on a save from before ET6, and for a city with no bridge. |
| 1149 | 6 | `public void setWorldMarket(Integer year, double[] prices, double[] scales)` | The world market's solved year and state (WN3), copied: all three or none. |
| 1157 | 1 | `public Integer getWorldYear()` | The year the saved world last solved, or null on a save with no world market (older, or no world file). |
| 1160 | 1 | `public double[] getWorldPrices()` | The saved mill prices, good-major; null with no market. |
| 1163 | 1 | `public double[] getWorldScales()` | The saved supply scales; null with no market. |
| 1166 | 1 | `public void setWorldMoneys(double[] state)` | The world's moneys' state (WN4), copied; null for none. |
| 1169 | 1 | `public double[] getWorldMoneys()` | The saved world moneys (WorldMoneys.toSaveArray()); null on a save with none (older, or no world file). |
| 1172 | 1 | `public void setPartnerTrade(PartnerTrade.State state)` | The city's trade by partner (WN3), the producer's fresh state. |
| 1175 | 1 | `public PartnerTrade.State getPartnerTrade()` | Null on a save with no world market. |
| 1177 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 1179 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 1182 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 1186 | 3 | `public int getConstructionByIdLength()` |  |
| 1190 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 1197 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 1204 | 6 | `public double getContractValueById(int templateId)` |  |
| 1212 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 1219-1317)

| line | len | member | says |
|---:|---:|---|---|
| 1221 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 1222 | 1 | `public double getPropertyTaxCharged()` |  |
| 1224 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 1225 | 1 | `public double getCityInterestAccrued()` |  |
| 1227 | 1 | `public void setWorkforce(long workforce)` |  |
| 1230 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 1233 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 1255 | 5 | `public void setCityLandRelief(double[] centre, double[][] holdings, double[][] offers)` | The city's ground by relief and shallow sea (ET1): the centre's, a row for each purchase and a row for each offer standing, each CityLand.RELIEF_AREAS figures in km2 (CityLand.centreReliefState(), holdingsReliefState(... |
| 1262 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 1264 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 1266 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 1268 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 1270 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 1272 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 1274 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 1275 | 1 | `public double[][] getLandOffers()` |  |
| 1277 | 1 | `public double[] getLandCentreRelief()` | The centre's dry ground by relief and shallow sea as saved (ET1), or null on a save from before: all flat. |
| 1279 | 1 | `public double[][] getLandHoldingsRelief()` | The purchases' relief, a row each in landHoldings' order, or null: all flat. |
| 1281 | 1 | `public double[][] getLandOffersRelief()` | The offers' relief, a row each in landOffers' order, or null: all flat. |
| 1283 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 1284 | 1 | `public double[] getDepletion()` |  |
| 1286 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 1287 | 1 | `public void setDepletionByType(double[][] byType)` |  |
| 1289 | 1 | `public double[][] getDepletionByType()` | E by type as saved (0.7.110), a row per resource or null for the walk's reading; null on a save that carries none. |
| 1291 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 1293 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 1295 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 1296 | 1 | `public double[] getWorldTotals()` |  |
| 1298 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 1300 | 1 | `public void setFreshRights(double units)` |  |
| 1302 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 1304 | 1 | `public void setMapStamp(Long stamp)` |  |
| 1306 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 1309 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 1310 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 1311 | 1 | `public double[] getLandMarketPrices()` |  |
| 1313 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 1315 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 1318-2622)

| line | len | member | says |
|---:|---:|---|---|
| 1392 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 1393 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 1394 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 1395 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 1396 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 1397 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 1399 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 1400 | 1 | `public double[] getTaxPolicyState()` |  |
| 1402 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 1403 | 1 | `public double[] getHouseholdBalance()` |  |
| 1405 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1409 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1410 | 1 | `public double[] getHouseholdCells()` |  |
| 1412 | 1 | `public void setBankCash(double cash)` |  |
| 1413 | 1 | `public double getBankCash()` |  |
| 1415 | 1 | `public void setBankSecurities(Double value)` |  |
| 1416 | 1 | `public Double getBankSecurities()` |  |
| 1428 | 4 | `public void setBankDeposits(double household, double sector)` |  |
| 1432 | 1 | `public Double getBankHouseholdDeposits()` |  |
| 1433 | 1 | `public Double getBankSectorDeposits()` |  |
| 1446 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1447 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1458 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1459 | 1 | `public double getBankProfitLastMonth()` |  |
| 1463 | 1 | `public void setBankDepositRate(double v)` |  |
| 1464 | 1 | `public double getBankDepositRate()` |  |
| 1480 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1481 | 1 | `public double[] getForeignAccounts()` |  |
| 1486 | 1 | `public void setRateHistory(double[] ring)` |  |
| 1487 | 1 | `public double[] getRateHistory()` |  |
| 1499 | 1 | `public void setCentralBank(double[] state)` |  |
| 1500 | 1 | `public double[] getCentralBank()` |  |
| 1511 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1515 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1516 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1528 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1529 | 1 | `public double[] getForeignStanding()` |  |
| 1540 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1541 | 1 | `public double[] getCapitalFlows()` |  |
| 1546 | 1 | `public void setCapitalFlowsStopReason(String reason)` |  |
| 1547 | 1 | `public String getCapitalFlowsStopReason()` |  |
| 1557 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1558 | 1 | `public double[] getOutwardInvestment()` |  |
| 1569 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1570 | 1 | `public String[] getEquityKeys()` |  |
| 1571 | 1 | `public double[] getEquity()` |  |
| 1575 | 1 | `public void setExchange(double[] state)` |  |
| 1576 | 1 | `public double[] getExchange()` |  |
| 1581 | 1 | `public void setExchangeState(Exchange.State state)` | UP1: a copy - toState() lists the exchange's own books. |
| 1582 | 1 | `public Exchange.State getExchangeState()` |  |
| 1600 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1601 | 1 | `public double[] getBankSolvency()` |  |
| 1619 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1620 | 1 | `public double[] getHousingOccupancy()` |  |
| 1636 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1637 | 1 | `public double[] getBankLastMonth()` |  |
| 1648 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1649 | 1 | `public double[] getBankPricingHistory()` |  |
| 1660 | 1 | `public void setBankLateProfit(double value)` |  |
| 1661 | 1 | `public double getBankLateProfit()` |  |
| 1675 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1676 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1687 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1688 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1698 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1699 | 1 | `public double[] getBankMonthLines()` |  |
| 1710 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1711 | 1 | `public double[] getBankStatementYear()` |  |
| 1723 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1724 | 1 | `public double[] getBankSheetYear()` |  |
| 1736 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1737 | 1 | `public Double getBankPaidInOpening()` |  |
| 1738 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1739 | 1 | `public Double getBankRetainedOpening()` |  |
| 1750 | 5 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1755 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1756 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1768 | 1 | `public void setFund(TreasuryFund.State state)` | UP1: a copy - toState() lists the fund's own resolutions and orders. |
| 1769 | 1 | `public TreasuryFund.State getFund()` |  |
| 1774 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1775 | 1 | `public double[] getDebtMarket()` |  |
| 1780 | 1 | `public void setAuctionTally(double[] tally)` |  |
| 1781 | 1 | `public double[] getAuctionTally()` |  |
| 1792 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1793 | 1 | `public double[] getPriceIndex()` |  |
| 1806 | 1 | `public void setExpectations(double[] state)` |  |
| 1807 | 1 | `public double[] getExpectations()` |  |
| 1819 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1820 | 1 | `public double[] getWorldEconomy()` |  |
| 1835 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1836 | 1 | `public double getTradedExchangeRate()` |  |
| 1846 | 1 | `public void setStruckRate(double rate)` |  |
| 1847 | 1 | `public double getStruckRate()` |  |
| 1861 | 1 | `public void setDenomination(double[] state)` |  |
| 1862 | 1 | `public double[] getDenomination()` |  |
| 1876 | 1 | `public void setPolicyRate(double rate)` |  |
| 1878 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1887 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1888 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1896 | 1 | `public void setPolicyAim(String name)` |  |
| 1897 | 6 | `public DebtManager.Aim getPolicyAim()` |  |
| 1910 | 1 | `public void setPegSpread(double spread)` |  |
| 1911 | 1 | `public double getPegSpread()` |  |
| 1924 | 1 | `public void setRolloverMode(String mode)` |  |
| 1926 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1931 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1932 | 1 | `public double[] getRolloverLedger()` |  |
| 1933 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1934 | 1 | `public double[] getRolloverRecord()` |  |
| 1945 | 5 | `public void setRolloverCap(Auction.Cap cap)` |  |
| 1950 | 3 | `public Auction.Cap getRolloverCap()` |  |
| 1962 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1963 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1975 | 1 | `public void setInflationTarget(double target)` |  |
| 1977 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1987 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1989 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 2000 | 1 | `public void setQeTargetShare(double share)` |  |
| 2001 | 1 | `public double getQeTargetShare()` |  |
| 2014 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 2016 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 2027 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 2028 | 1 | `public double getHouseholdPaperRatio()` |  |
| 2050 | 1 | `public void setBondMarket(BondMarket.State state)` | UP1: a copy - toState() lists the market's own bonds and books. |
| 2051 | 1 | `public BondMarket.State getBondMarket()` |  |
| 2052 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 2053 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 2054 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 2055 | 1 | `public double getHouseholdBondRatio()` |  |
| 2056 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 2057 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 2068 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 2072 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 2073 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 2097 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 2098 | 1 | `public double getCarsPerHousehold()` |  |
| 2111 | 1 | `public void setTransitBill(double v)` |  |
| 2112 | 1 | `public double getTransitBill()` |  |
| 2124 | 1 | `public void setCaptiveShare(double v)` |  |
| 2125 | 1 | `public double getCaptiveShare()` |  |
| 2126 | 1 | `public void setFuelPerJourney(double v)` |  |
| 2127 | 1 | `public double getFuelPerJourney()` |  |
| 2138 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 2139 | 1 | `public double[] getHouseholdFuel()` |  |
| 2151 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 2152 | 1 | `public double[] getHouseholdGraduates()` |  |
| 2154 | 1 | `public void setRememberedCommute(double v)` |  |
| 2155 | 3 | `public double getRememberedCommute()` |  |
| 2168 | 1 | `public void setCostOfLiving(double v)` |  |
| 2169 | 1 | `public double getCostOfLiving()` |  |
| 2182 | 1 | `public void setBankTaxCharged(double v)` |  |
| 2183 | 1 | `public double getBankTaxCharged()` |  |
| 2187 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 2188 | 1 | `public double getRentWeight()` |  |
| 2208 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 2209 | 1 | `public double getRentWeightStudio()` |  |
| 2211 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 2215 | 1 | `public int getConstructionShedMonth()` |  |
| 2216 | 1 | `public double getConstructionShedPoints()` |  |
| 2218 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 2221 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 2370 | 1 | `public void setLicences(double[] a)` | UP1: a copy, not the live head counts - PopulationManager hands out its own arrays. |
| 2371 | 1 | `public double[] getLicences()` |  |
| 2372 | 1 | `public void setEducation(double[] a)` |  |
| 2373 | 1 | `public double[] getEducation()` |  |
| 2375 | 1 | `public void setLabour(double[] a)` |  |
| 2376 | 1 | `public double[] getLabour()` |  |
| 2378 | 1 | `public void setSkilledWorkforce(double[] a)` | UP1: a copy, not the live head counts. |
| 2379 | 1 | `public double[] getSkilledWorkforce()` |  |
| 2381 | 1 | `public void setCohorts(double[] a)` |  |
| 2382 | 1 | `public double[] getCohorts()` |  |
| 2385 | 1 | `public void setBandNames(String[] names)` |  |
| 2387 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 2401 | 1 | `public void setShapeNames(String[] names)` |  |
| 2402 | 1 | `public String[] getShapeNames()` |  |
| 2403 | 1 | `public void setFamilies(double[] a)` |  |
| 2404 | 1 | `public double[] getFamilies()` |  |
| 2405 | 1 | `public void setMigration(double[] a)` |  |
| 2406 | 1 | `public double[] getMigration()` |  |
| 2407 | 1 | `public void setUnemployment(double[] a)` |  |
| 2408 | 1 | `public double[] getUnemployment()` |  |
| 2409 | 1 | `public void setSickness(double[] a)` |  |
| 2410 | 1 | `public double[] getSickness()` |  |
| 2411 | 1 | `public void setCrime(double[] a)` |  |
| 2412 | 1 | `public double[] getCrime()` |  |
| 2413 | 1 | `public void setHealth(double[] a)` |  |
| 2414 | 1 | `public double[] getHealth()` |  |
| 2415 | 1 | `public void setHealthcare(double[] a)` |  |
| 2416 | 1 | `public double[] getHealthcare()` |  |
| 2444 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 2445 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 2446 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 2447 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 2448 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 2449 | 1 | `public double getCityCapitalSpending()` |  |
| 2467 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 2468 | 1 | `public double getCityMaintenancePaid()` |  |
| 2484 | 1 | `public void setLastInvestment(java.util.Map<String, String> m)` |  |
| 2485 | 1 | `public java.util.Map<String, String> getLastInvestment()` |  |
| 2486 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 2487 | 1 | `public double getMonthlyMaterialImports()` |  |
| 2488 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 2489 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 2490 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 2491 | 1 | `public int getMaterialsConsumed()` |  |
| 2500 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 2501 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 2511 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 2512 | 1 | `public double[] getHouseholdStatement()` |  |
| 2514 | 4 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 2518 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 2520 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2523 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2525 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2528 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2530 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2533 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2535 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2538 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2540 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2541 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2543 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2544 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2546 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2547 | 1 | `public double getInsurancePremiums()` |  |
| 2549 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2550 | 1 | `public double[] getNationalAccounts()` |  |
| 2552 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2553 | 1 | `public List<Double> getGdpRolling()` |  |
| 2563 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2566 | 3 | `public int getUnderConstruction(int index)` |  |
| 2569 | 3 | `public double getCash()` |  |
| 2573 | 3 | `public int getMonth()` |  |
| 2577 | 3 | `public int getBuildingQuantity(int index)` |  |
| 2581 | 3 | `public int getBuildingsLength()` |  |
| 2585 | 3 | `public JsonArray getDebt()` |  |
| 2589 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 2594 | 3 | `public JsonArray getBusinessDebt()` |  |
| 2598 | 3 | `public int getProgressLength()` |  |
| 2602 | 3 | `public double getProgress(int index)` |  |
| 2606 | 3 | `public int getConstructionMaterials()` |  |
| 2610 | 3 | `public long getPopulation()` |  |
| 2613 | 3 | `public boolean getReports()` |  |
| 2616 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2619 | 3 | `public boolean getGraphs()` |  |


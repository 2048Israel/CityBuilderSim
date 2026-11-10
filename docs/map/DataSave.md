# DataSave.java - 2,887 lines · 441 methods · 1 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Founding](Founding.md) (7), [SectorBooks](SectorBooks.md) (6), [World](World.md) (5), [Nations](Nations.md) (5), [DebtManager](DebtManager.md) (5), [Rollover](Rollover.md) (5), [Notice](Notice.md) (4), [BuildLog](BuildLog.md) (4), [Currency](Currency.md) (4), [GameFiles](GameFiles.md) (4), [Bank](Bank.md) (4), [CapSetting](CapSetting.md) (4), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [Ports](Ports.md) (3), [Zoning](Zoning.md) (3), [Landmasses](Landmasses.md) (3), [Bridges](Bridges.md) (3), [PartnerTrade](PartnerTrade.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [OldMetal](OldMetal.md) (3), [DemolitionLog](DemolitionLog.md) (3), [SaveCopy](SaveCopy.md) (3), [Fittings](Fittings.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3)... and 8 more

**Used by (12):** [EarthTerrainCheck](EarthTerrainCheck.md), [FittingCheck](FittingCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [LandmassCheck](LandmassCheck.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [Recording](Recording.md), [ReplayCheck](ReplayCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md)

## Sections

| line | section |
|---:|---|
| 326 | · land and ore |
| 433 | · the shedding warning |
| 669 | · the header |
| 1148 | · construction, by id |
| 1409 | · charged, not derived |
| 1528 | · policy |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 975 | `DataSave.LOANS_GSON` | `new Gson()` | The one Gson the two loan lists below go through (GT1, 2026-11-02): a new Gson per fill had to build its reflective adapters for Debt and BusinessDebt again each time, about half a millisecond of the month that autosa... |

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
| 92 | `private Boolean foundedUnfitted` | ...AND WHETHER IT WAS FOUNDED THE 1900 WAY (R1b; Founding.isUnfitted()): written only for a city that was, so a city founded the default way writes no key and a save without one reads false (getFounding()). |
| 95 | `private double cash` | save variables |
| 96 | `private int[] buildings` |  |
| 97 | `private int month` |  |
| 98 | `private JsonArray debts` |  |
| 106 | `private JsonArray businessDebts` | Private-sector loans. |
| 113 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 114 | `private int[] underConstruction` |  |
| 125 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 126 | `private int[] underConstructionById` |  |
| 128 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 130 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 132 | `private java.util.List<BuildingManager.ContractRecord> contractRecords` | ...and who placed each part of it, with its material still to draw and the allowance priced in for it (0.7.19). |
| 137 | `private Long foundingEndowments` | How many founding endowments the city holds (FW9; BuildingManager.getFoundingEndowments()): 1 for every city a player founds, K for a harness's copy of one K times over. |
| 147 | `private ConstructionControl.State constructionControl` | The player's hand on the queue (0.7.22; ConstructionControl): the city's order of its own sites, the rushes and their months on overtime, the cancels waiting for the month's end, the stopped shells, the demolitions on... |
| 155 | `private java.util.List<DecisionLog.Entry> decisionLog` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, a month, a kind and a line each. |
| 163 | `private AutoBuilder.State autoBuild` | Automatic building (0.7.73; AutoBuilder): the switch, the spare margin and the debt limit, its log of orders and what held it back at its last pass. |
| 173 | `private StrategicReserve.State reserve` | The city's strategic reserve (0.7.85, batch O8; StrategicReserve): the crude its tanks hold and what it cost, the release standing and the fill ordered, and the month's trades the next strike settles (runs/ spec-oil.m... |
| 184 | `private Ports.State portMonth` | The city's ports (0.7.86, batch O9; Ports): each kind of cargo's share of its tonnes at sea in force, whether it goes before the railway, the class crude goes in, the road's two shares, and the month's tonnes by kind ... |
| 195 | `private Zoning.State zoning` | The city's zones, its forest law and the forest cleared for building (0.7.107, batch Z1; Zoning, LandManager's ZONES AND THE FOREST LAW): each zone's sector, size and its kind, price share and the ground used in it; t... |
| 204 | `private Landmasses.State landmasses` | Where the city's sea terminals stand (ET5, 2026-10-25; Landmasses): per terminal template the landmass ids of its standing, on-site and stopped units, oldest first, and of the units a demolition on site is taking down. |
| 212 | `private Bridges.State bridges` | The city's long bridges (ET6, 2026-10-26; Bridges): each one built or building - its template, kind, the two landings and the landmass at each end, its length, the deepest and mean depth of the water under it, the cor... |
| 222 | `private Integer worldYear` | The world market (WN3, 2026-10-28; WorldMarket, Game's THE WORLD MARKET), when the city has one - a world file was there: the year its January last solved (worldYear), and the solved state, every modelled good's mill ... |
| 223 | `private double[] worldPrices` |  |
| 224 | `private double[] worldScales` |  |
| 225 | `private PartnerTrade.State partnerTrade` |  |
| 233 | `private double[] worldMoneys` | The world's moneys (WN4, 2026-10-30; WorldMoneys, Game's THE WORLD'S MONEYS IN THE MONTH), when the city has a world market: the month they stand in, the dollar cycle, the risk-off and each money's rate, swing, terms ... |
| 241 | `private double[] policyRates` | The eight central banks' policy rates (XR4, 2026-10-31; PolicyRates, Game's THE WORLD'S MONEYS IN THE MONTH), when the city has a world market: the month they stand in and each bank's rate, shadow, target and inflatio... |
| 262 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 277 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 288 | `private double cityForeignInterest` | ...AND THE MONTH'S COUPONS ABROAD (CD9, CD7-E, 2026-11-08): the foreign paper's interest, every money's, in D$, that EARNED's expenses carry beside the accrued interest (EconomyManager.getForeignInterest()). |
| 299 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 300 | `private java.util.List<Markets.State> markets` |  |
| 313 | `private OldMetal.State oldMetal` | The city's old steel (0.7.123, batch I4; OldMetal): what it has thrown out since its Scrap Yards last bought, by seller and by source, and the yards' last month. |
| 324 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 363 | `private Double worldSeaTheta` |  |
| 364 | `private double[] worldTotals` |  |
| 365 | `private double[] landCentre` |  |
| 366 | `private double[][] landCentreRects` |  |
| 367 | `private double[][] landHoldings` |  |
| 368 | `private double[][] landPartFields` |  |
| 369 | `private double[][] landConverted` |  |
| 370 | `private double[] landLanes` |  |
| 371 | `private double[][] landPurchases` |  |
| 372 | `private double[][] landOffers` |  |
| 373 | `private double[] landCentreRelief` |  |
| 374 | `private double[][] landHoldingsRelief` |  |
| 375 | `private double[][] landOffersRelief` |  |
| 383 | `private double[][] landOffersSellers` | WN5 (2026-11-01; spec-nations 6.2-6.4): who sells each offer standing - a row for each of landOffers, in its order, of (nation, km2, US$ thousands) for each nation whose ground it holds (LandParcel.sellersRow()), an e... |
| 384 | `private double[] landBoughtKm2` |  |
| 385 | `private double[] landBoughtUsd` |  |
| 386 | `private Integer nextOfferId` |  |
| 387 | `private double[] depletion` |  |
| 395 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 405 | `private double[][] depletionByType` | What has been taken out of each typed resource by type (0.7.110, batch I1; LandManager's THE GROUND BY TYPE): a row per resource in Resource's order, its types in Resource.typesOf()'s order, null for a resource whose ... |
| 413 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 414 | `private double[] landListing` |  |
| 416 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 417 | `private Integer ironDeposits` |  |
| 418 | `private Double ironReserveTonnes` |  |
| 425 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 431 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 449 | `private int constructionShedMonth` |  |
| 450 | `private double constructionShedPoints` |  |
| 457 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 468 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 478 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 485 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 486 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 494 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 495 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 503 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 515 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 516 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 517 | `private double insurancePremiums` |  |
| 520 | `private int constructionMaterials` | The city's own yard, in units. |
| 521 | `private long population` |  |
| 532 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 533 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 563 | `private java.util.Map<String, double[][]> sectorDebt, sectorDebtBefore` | ...AND THE DEBT THEIR SHEETS SPLIT (FW2, 2026-10-24): R1's interest by instrument and R2's debt by kind and when it falls due, this month's and last month's, by sector (SectorBooks.Debt) - and what had moved each sect... |
| 564 | `private java.util.Map<String, double[][]> creditMoved` |  |
| 580 | `private java.util.Map<String, java.util.Map<String, double[]>> sectorStock` | The stock each sector's books revalue next month (FW6, 2026-10-25): every business's stock by good as the month closed, {units, price} under the good's name (SectorBooks.stockToSave()). |
| 590 | `private java.util.Map<String, double[]> sectorShares, sectorSharesBefore` | Each listed company's share as this month closed and as last month did (FW6, 2026-10-25), by sector, in SectorBooks.SHARES_SLOTS slots (SectorBooks.sharesToSave()). |
| 600 | `private boolean reports` | settings |
| 601 | `private boolean graphs` |  |
| 613 | `private String recordingId` | THE RECORDING'S SWITCH AND ID (TR1, 2026-10-31). |
| 614 | `private boolean recording` |  |
| 625 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 635 | `private double landOwned` | Land. |
| 636 | `private int landBlocksPurchased` |  |
| 637 | `private double landPricePerSqFt` |  |
| 646 | `private Double landWorksSqFt` | ET2 (spec-earth2-terrain 3.2, 4.2, 4.4): the part of what is built on that works stand on (LandManager.getWorksSqFt()), and the foothill prepared so far (getPreparedSqFt(), which never falls, so it is history and not ... |
| 647 | `private Double landPreparedSqFt` |  |
| 654 | `private Double landRunsSqFt` | ET4 (spec-earth2-terrain 4.3): the part of what is built on that highways and railway lines stand on (LandManager.getRunsSqFt()). |
| 663 | `private double incomeTaxRate` | Tax rates. |
| 664 | `private double propertyTaxRate` |  |
| 793 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 811 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 812 | `private double[] treasuryJournalAmounts` |  |
| 813 | `private String[] treasuryJournalPendingLabels` |  |
| 814 | `private double[] treasuryJournalPendingAmounts` |  |
| 822 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 831 | `private double retiredGainPending` | The gain on paper the treasury has retired since the last strike - Game.retiredGainSoFar (CD3), the carrying value less the price paid - carried for the same reason: a city saved between two presses, after a buyback, ... |
| 840 | `private double[] interestBookedByKind` | The month's interest expense on each kind of paper, in DebtManager.BOOK_KINDS' order - DebtManager.interestByKind (CD6), the rows of the budget's interest line the Government tab's grid reads after the month. |
| 849 | `private double[][] curveBook` | The curve as the months behind it stood (CD6): one row a month, the newest DebtManager.CURVE_KEPT - [month, the dial, the city's zero rates at DebtManager.CURVE_TENORS, the world's at the same tenors] - DebtManager.cu... |
| 860 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 861 | `private double cityDiscountUnsettled` |  |
| 899 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 1159 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 1176 | `private java.util.Map<Integer, java.util.Map<String, Integer>> unfittedById` | The buildings still WITHOUT a piece of equipment, by template id and then by the piece's id (R1; BuildingsStacks' unfitted counts), written only for the stacks that have any: null, or empty, is every building fitted. |
| 1193 | `private java.util.Map<Integer, java.util.List<String>> recipeChoices` | The recipes the two parts of a stack run (R1c; Recipes), by template id: [the fitted part's recipe id, the unfitted part's], written only for a template where a part runs a recipe other than the default, so only a cit... |
| 1209 | `private java.util.List<Fittings.Job> fittings` | The fittings on site (R1b; Fittings), in the order placed, each with the template, the piece, who ordered it and the points still owed: written only while there is a job, so a city with none writes no key and a save w... |
| 1228 | `public java.util.Map<String, Double> grants` |  |
| 1229 | `public java.util.Map<String, Integer> posts` |  |
| 1230 | `public java.util.Map<String, Double> piles` |  |
| 1231 | `public java.util.Map<String, Integer> found` |  |
| 1232 | `public double payroll, costs` |  |
| 1235 | `public java.util.Map<String, Double> prizes` | R4: the prize dials, the pots, the labs' part of the piles, the city's own researchers on each problem last month, the months in a row a field has had a lab too many, and the prize the treasury paid the labs that mont... |
| 1236 | `public java.util.Map<String, Double> pots` |  |
| 1237 | `public java.util.Map<String, Double> labPiles` |  |
| 1238 | `public java.util.Map<String, Double> cityShares` |  |
| 1239 | `public java.util.Map<String, Integer> labIdleMonths` |  |
| 1240 | `public double prizePaid` |  |
| 1243 | `public java.util.Map<String, Double> researchers` | R3b: the month's researchers, idle researchers, open problems, lab researchers and idle lab researchers by field name (only the fields above nothing), which Research.month() struck and a load would otherwise read as 0... |
| 1244 | `public java.util.Map<String, Double> idle` |  |
| 1245 | `public java.util.Map<String, Integer> open` |  |
| 1246 | `public java.util.Map<String, Double> labResearchers` |  |
| 1247 | `public java.util.Map<String, Double> labsIdle` |  |
| 1250 | `private ResearchRecord research` |  |
| 1538 | `private double[] taxPolicyState` |  |
| 1551 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 1563 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 1564 | `private double[] householdCells` |  |
| 1576 | `private double bankCash` | The bank's cash. |
| 1584 | `private Double bankSecurities` | The desk's inventory as the bank carries it (Bank.getSecurities(), FL1): the mark of its last re-mark, the exchange's step. |
| 1594 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 1597 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 1598 | `private SalesTaxLedger.State salesTax` |  |
| 1600 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1616 | `private java.util.Map<String, Integer> leverOpenings` | The month each Policy lever opens in this city, by the lever's NAME (R0; TaxPolicy.openingsForSave()). |
| 1645 | `private Double bankHouseholdDeposits, bankSectorDeposits` | The bank's deposit book as the month last set it (FW2, 2026-10-24; F-S1-1): what the families and the businesses had banked when Game.refreshBank() last ran in the month, which the load's own refreshBank() would re-re... |
| 1663 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1675 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1681 | `private double bankDepositRate` | What savers were paid. |
| 1697 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 1703 | `private double[] rateHistory` | Game's ring of the last twelve exchange rates and how full it is (XR1): the year's depreciation the hot money reads. |
| 1716 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1727 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1728 | `private double[] treasuryArrearsAmounts` |  |
| 1745 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1757 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1763 | `private String capitalFlowsStopReason` | The words the sudden stop gave when it began (FW6, 2026-10-25): a String the capital flows' array cannot hold, for the Trade screen's card. |
| 1774 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1785 | `private String[] equityKeys` | The share register, company by company, named. |
| 1786 | `private double[] equity` |  |
| 1793 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1798 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1817 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1836 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1853 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1865 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1877 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1892 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1904 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1915 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1927 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1940 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1952 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1953 | `private Double bankRetainedOpening` |  |
| 1966 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1967 | `private double[] bankPreferredRecord` |  |
| 1984 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1991 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1997 | `private double[] auctionTally` | The city's auctions counted (CD2, D33): AuctionTally.toSave(), fourteen slots. |
| 2009 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 2023 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 2036 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 2052 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 2063 | `private double struckRate` | The D$ per US$ the city's goods were struck at (AN1, EconomyManager's THE D$ THE GOODS ARE STRUCK AT): the state the next month's strike moves from, which neither the D$ nor the traded rate above can give back. |
| 2078 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 2093 | `private Double policyRate` | The policy rate. |
| 2104 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 2113 | `private String policyAim` | What the autopilot aims at (XR2; DebtManager.Aim), by name. |
| 2127 | `private Double pegSpread` | The spread the dial stands at over the US's rate under a peg (XR2), a fraction a year. |
| 2139 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 2140 | `private double[] rolloverLedger` |  |
| 2141 | `private double[] rolloverRecord` |  |
| 2161 | `private String rolloverCapMode` | ...and the cap its auctions take (CD2, D28), the mode by name and the tail or yield beside it, two plain fields because a cap's Fill it carries no number and Gson writes no NaN (CapSetting). |
| 2162 | `private double rolloverCapValue` |  |
| 2179 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 2192 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 2204 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 2217 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 2231 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 2244 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 2263 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 2264 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 2265 | `private double householdBondRatio` |  |
| 2266 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 2284 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 2285 | `private double buybackAbroadUnsettled` |  |
| 2308 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 2314 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 2328 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 2341 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 2355 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 2365 | `private double[] homeFuelSale` | ...and the homes' lamp oil and coal as 6d struck them for the households' next bill (R0e): {homes, litres, tonnes, bill, cost, imported} (HomeFuel.heldSale()) - a sale off the refiners' and mines' shelves is a month p... |
| 2378 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 2395 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 2409 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 2435 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 2468 | `private double[] cohorts` | The demographics. |
| 2492 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 2494 | `private double[] families` |  |
| 2495 | `private double[] migration` |  |
| 2505 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 2514 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 2524 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 2537 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 2548 | `private double[] causesOfDeath` | The causes of death of 1900 (R0b; CausesOfDeath.getState()): last month's dead of each of the seven, the dead of each since the city began, the last twelve months' dead of each, one slot to a month, and the people sic... |
| 2561 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 2577 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 2591 | `private double[] wantedPosts` | The posts per wage band the staffing gate refused this month that the labour market prices as if they were offered (LD1; WantedPosts). |
| 2603 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 2620 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 2621 | `private double[] education` |  |
| 2654 | `private String[] shapeNames` | The household shapes this save was written with. |
| 2689 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 2690 | `private java.util.List<Long> populationTrend` |  |
| 2691 | `private double cityCapitalSpending` |  |
| 2692 | `private double monthlyMaterialImports` |  |
| 2699 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 2700 | `private int materialsConsumed` |  |
| 2723 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 2740 | `private java.util.Map<String, String> lastInvestment` | THE INVESTORS' LAST WORD ON EACH SECTOR (0.7.102, Jerus's A22): what the month's investment pass decided for it and why - "Built 2 ...", "Holding: ...", a refusal - as Game.getLastInvestment() reads it, the Sectors sc... |
| 2757 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 2768 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2876 | **type** `public class DataSave implements Cloneable` |  |
| 302 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 303 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 304 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 305 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |
| 315 | 1 | `public void setOldMetal(OldMetal.State s)` |  |
| 317 | 1 | `public OldMetal.State getOldMetal()` | The old steel as saved (0.7.123), or null on a save from before. |

### land and ore (lines 326-432)

### the shedding warning (lines 433-668)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 539 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 543 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 547 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |
| 566 | 4 | `public void setSectorDebt(java.util.Map<String, double[][]> now, java.util.Map<String, double[][]> before)` |  |
| 570 | 1 | `public java.util.Map<String, double[][]> getSectorDebt()` |  |
| 571 | 1 | `public java.util.Map<String, double[][]> getSectorDebtBefore()` |  |
| 572 | 1 | `public void setCreditMoved(java.util.Map<String, double[][]> moved)` |  |
| 573 | 1 | `public java.util.Map<String, double[][]> getCreditMoved()` |  |
| 582 | 1 | `public void setSectorStock(java.util.Map<String, java.util.Map<String, double[]>> stock)` |  |
| 583 | 1 | `public java.util.Map<String, java.util.Map<String, double[]>> getSectorStock()` |  |
| 592 | 4 | `public void setSectorShares(java.util.Map<String, double[]> now, java.util.Map<String, double[]> before)` |  |
| 596 | 1 | `public java.util.Map<String, double[]> getSectorShares()` |  |
| 597 | 1 | `public java.util.Map<String, double[]> getSectorSharesBefore()` |  |

### the header (lines 669-1147)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 1 | `public void setSlotName(String name)` |  |
| 674 | 23 | `public void setFounding(Founding f)` | The founding record, whole. |
| 708 | 35 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 745 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 748 | 1 | `public String getWorldKind()` | The world's kind as saved (0.7.121): "EARTH" on a city founded on the real Earth, null on a generated world's and on every save before 0.7.121. |
| 751 | 1 | `public boolean isOnEarth()` | Whether the save is a city on the real Earth (0.7.121): worldKind EARTH. |
| 754 | 1 | `public Double getEarthLat()` | The Earth's spot as saved, latitude in degrees (0.7.121); null on a generated world. |
| 757 | 1 | `public Double getEarthLon()` | ...its longitude. |
| 760 | 1 | `public String getEarthData()` | The Earth's data the city was drawn from, by identity ("earth-2:<hash>", 0.7.121); null on a generated world. |
| 763 | 1 | `public String getWorldPath()` | The path of the world beyond the city as saved (WN1): "HISTORICAL" or "RANDOM", null on a save from before it. |
| 766 | 1 | `public Long getPathSeed()` | The path's seed as saved (WN1); null on a save from before it. |
| 777 | 5 | `public String earthProblem()` | Why this save's world cannot be read here, or null (0.7.121): a kind this build does not know, or an Earth city whose spot is missing or whose data is not this build's or not here, or (0.7.127) that was saved before i... |
| 784 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 795 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 796 | 1 | `public double[] getTreasuryMonth()` |  |
| 863 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 867 | 1 | `public double getCityPaperUnsettled()` |  |
| 868 | 1 | `public double getCityDiscountUnsettled()` |  |
| 870 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 877 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 878 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 879 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 880 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 881 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 882 | 1 | `public double getTreasuryRaisedPending()` |  |
| 883 | 1 | `public void setRetiredGainPending(double v)` |  |
| 884 | 1 | `public double getRetiredGainPending()` |  |
| 885 | 1 | `public void setInterestBookedByKind(double[] v)` |  |
| 886 | 1 | `public double[] getInterestBookedByKind()` |  |
| 887 | 1 | `public void setCurveBook(double[][] v)` |  |
| 888 | 1 | `public double[][] getCurveBook()` |  |
| 901 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 902 | 1 | `public double[] getGovernmentMonth()` |  |
| 903 | 1 | `public String getSlotName()` |  |
| 906 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 912 | 1 | `public String getGameVersion()` |  |
| 913 | 1 | `public int getSaveFormat()` |  |
| 914 | 1 | `public long getSavedAt()` |  |
| 916 | 1 | `public void setHouseholdSavings(double value)` |  |
| 917 | 1 | `public double getHouseholdSavings()` |  |
| 919 | 1 | `public void setLandOwned(double sqFt)` |  |
| 920 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 921 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 923 | 4 | `public void setLandWorks(double worksSqFt, double preparedSqFt)` | The works' ground and the foothill prepared (ET2): LandManager.getWorksSqFt() and getPreparedSqFt(). |
| 928 | 1 | `public void setLandRuns(double runsSqFt)` | The runs' ground (ET4): LandManager.getRunsSqFt(). |
| 930 | 1 | `public double getLandOwned()` |  |
| 931 | 1 | `public int getLandBlocksPurchased()` |  |
| 932 | 1 | `public double getLandPricePerSqFt()` |  |
| 934 | 1 | `public Double getLandWorksSqFt()` | The works' ground as saved (ET2), or null on a save without it: read from the standing buildings. |
| 936 | 1 | `public Double getLandPreparedSqFt()` | The foothill prepared as saved (ET2), or null on a save without it. |
| 938 | 1 | `public Double getLandRunsSqFt()` | The runs' ground as saved (ET4), or null on a save without it: read from the standing highways and railway lines. |
| 940 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 941 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 943 | 1 | `public double getIncomeTaxRate()` |  |
| 944 | 1 | `public double getPropertyTaxRate()` |  |
| 946 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 951 | 3 | `public void setCash(double money)` | setters |
| 955 | 3 | `public void setMonth(int month)` |  |
| 959 | 3 | `public void setBuildingNum(int i)` |  |
| 963 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 977 | 3 | `public void setDebt(List<Debt> debts)` |  |
| 981 | 3 | `public void setProgress(double[] progress)` |  |
| 985 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 989 | 3 | `public void setPopulation(long population)` |  |
| 992 | 3 | `public void setReports(boolean reports)` |  |
| 1005 | 4 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 1009 | 3 | `public void setGraphs(boolean graphs)` |  |
| 1013 | 4 | `public void setRecording(String id, boolean on)` | The recording's id (null: never recorded) and whether "Record this city" is on (TR1; see the field). |
| 1050 | 28 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 1097 | 9 | `public DataSave snapshot()` | A copy of this save for the writer thread (UP1, 2026-10-22): the object itself, shallow, plus a copy of every array or list DataSave mutates in place. |
| 1115 | 25 | `String describeUnwritable()` | Names the field that broke, if it can find it. |
| 1141 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 1148-1408)

| line | len | member | says |
|---:|---:|---|---|
| 1161 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 1162 | 1 | `public int[] getStackOrder()` |  |
| 1164 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 1178 | 3 | `public void setUnfittedById(java.util.Map<Integer, java.util.Map<String, Integer>> byId)` |  |
| 1183 | 3 | `public java.util.Map<Integer, java.util.Map<String, Integer>> getUnfittedById()` | The unfitted counts the save carries by template id, or an empty map for a save with none (every building fitted). |
| 1195 | 3 | `public void setRecipeChoices(java.util.Map<Integer, java.util.List<String>> byId)` |  |
| 1200 | 3 | `public java.util.Map<Integer, java.util.List<String>> getRecipeChoices()` | The recipe choices the save carries by template id, or an empty map for a save with none (every building on its default). |
| 1212 | 3 | `public void setFittings(java.util.List<Fittings.Job> jobs)` | The jobs to write: a list of the caller's own copies (Fittings.view()), or none. |
| 1217 | 1 | `public java.util.List<Fittings.Job> getFittings()` | The fittings the save carries, or null for a save with none. |
| 1227 | 22 | **type** `public static final class ResearchRecord` | The city's research (R3; Research): the grants by field name, the posts the month struck by field name, the work piled by code and the discoveries found here by code with the month each was found, in the order found. |
| 1257 | 3 | `public void setResearch(Research r, double payroll, double costs)` | The research to write with the month's bill (EconomyManager.getResearchPayroll(), getResearchCosts()), or none: a city that has no grant, post, pile or find and no researcher this month writes no key. |
| 1262 | 25 | `public void setResearch(Research r, double payroll, double costs, double prizePaid)` | ...and with the prize the treasury paid the labs that month (R4), after the prize dials, the pots, the labs' piles, the city's shares and the idle counts, which Research gives. |
| 1289 | 1 | `public ResearchRecord getResearch()` | The research the save carries, or null for a save with none. |
| 1292 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 1294 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 1296 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 1298 | 1 | `public void setFoundingEndowments(long endowments)` |  |
| 1300 | 1 | `public long getFoundingEndowments()` | The founding endowments as saved, or 1 on a save without the figure (FW9). |
| 1303 | 1 | `public void setConstructionControl(ConstructionControl.State state)` | UP1: a copy - toState() is the control's own live state. |
| 1305 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 1307 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 1309 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 1311 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 1313 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 1315 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 1317 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 1319 | 1 | `public void setZoning(Zoning.State state)` |  |
| 1321 | 1 | `public Zoning.State getZoning()` | Null on a save from before 0.7.107, or with nothing zoned, no law and no forest cleared. |
| 1323 | 1 | `public void setLandmasses(Landmasses.State state)` |  |
| 1325 | 1 | `public Landmasses.State getLandmasses()` |  |
| 1327 | 1 | `public void setBridges(Bridges.State state)` |  |
| 1330 | 1 | `public Bridges.State getBridges()` | Null on a save from before ET6, and for a city with no bridge. |
| 1333 | 6 | `public void setWorldMarket(Integer year, double[] prices, double[] scales)` | The world market's solved year and state (WN3), copied: all three or none. |
| 1341 | 1 | `public Integer getWorldYear()` | The year the saved world last solved, or null on a save with no world market (older, or no world file). |
| 1344 | 1 | `public double[] getWorldPrices()` | The saved mill prices, good-major; null with no market. |
| 1347 | 1 | `public double[] getWorldScales()` | The saved supply scales; null with no market. |
| 1350 | 1 | `public void setWorldMoneys(double[] state)` | The world's moneys' state (WN4), copied; null for none. |
| 1353 | 1 | `public double[] getWorldMoneys()` | The saved world moneys (WorldMoneys.toSaveArray()); null on a save with none (older, or no world file). |
| 1356 | 1 | `public void setPolicyRates(double[] state)` | The eight central banks' policy rates' state (XR4), copied; null for none. |
| 1359 | 1 | `public double[] getPolicyRates()` | The saved policy rates (PolicyRates.toSaveArray()); null on a save with none (older, or no world file). |
| 1362 | 1 | `public void setPartnerTrade(PartnerTrade.State state)` | The city's trade by partner (WN3), the producer's fresh state. |
| 1365 | 1 | `public PartnerTrade.State getPartnerTrade()` | Null on a save with no world market. |
| 1367 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 1369 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 1372 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 1376 | 3 | `public int getConstructionByIdLength()` |  |
| 1380 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 1387 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 1394 | 6 | `public double getContractValueById(int templateId)` |  |
| 1402 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 1409-1527)

| line | len | member | says |
|---:|---:|---|---|
| 1411 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 1412 | 1 | `public double getPropertyTaxCharged()` |  |
| 1414 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 1415 | 1 | `public double getCityInterestAccrued()` |  |
| 1417 | 1 | `public void setCityForeignInterest(double value)` |  |
| 1418 | 1 | `public double getCityForeignInterest()` |  |
| 1420 | 1 | `public void setWorkforce(long workforce)` |  |
| 1423 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 1426 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 1448 | 5 | `public void setCityLandRelief(double[] centre, double[][] holdings, double[][] offers)` | The city's ground by relief and shallow sea (ET1): the centre's, a row for each purchase and a row for each offer standing, each CityLand.RELIEF_AREAS figures in km2 (CityLand.centreReliefState(), holdingsReliefState(... |
| 1455 | 3 | `public void setLandOffersSellers(double[][] offers)` | Who sells each offer standing (WN5; LandMarket.getOffersSellersState()), a row for each of landOffers; null for none. |
| 1460 | 4 | `public void setLandBought(double[] km2, double[] usd)` | What the city has bought from each nation, in km2 and in thousands of US dollars (WN5; Game.getLandBoughtKm2()): a figure for each nation of the world file and index 0 for the purchases with no seller named; null for ... |
| 1466 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 1468 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 1470 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 1472 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 1474 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 1476 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 1478 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 1479 | 1 | `public double[][] getLandOffers()` |  |
| 1481 | 1 | `public double[] getLandCentreRelief()` | The centre's dry ground by relief and shallow sea as saved (ET1), or null on a save from before: all flat. |
| 1483 | 1 | `public double[][] getLandHoldingsRelief()` | The purchases' relief, a row each in landHoldings' order, or null: all flat. |
| 1485 | 1 | `public double[][] getLandOffersRelief()` | The offers' relief, a row each in landOffers' order, or null: all flat. |
| 1487 | 1 | `public double[][] getLandOffersSellers()` | The offers' sellers, a row each in landOffers' order, or null: every offer the host's whole (WN5). |
| 1489 | 1 | `public double[] getLandBoughtKm2()` | The km2 bought from each nation (index 0: no seller named), or null for nothing bought (WN5). |
| 1491 | 1 | `public double[] getLandBoughtUsd()` | ...and the thousands of US dollars paid. |
| 1493 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 1494 | 1 | `public double[] getDepletion()` |  |
| 1496 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 1497 | 1 | `public void setDepletionByType(double[][] byType)` |  |
| 1499 | 1 | `public double[][] getDepletionByType()` | E by type as saved (0.7.110), a row per resource or null for the walk's reading; null on a save that carries none. |
| 1501 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 1503 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 1505 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 1506 | 1 | `public double[] getWorldTotals()` |  |
| 1508 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 1510 | 1 | `public void setFreshRights(double units)` |  |
| 1512 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 1514 | 1 | `public void setMapStamp(Long stamp)` |  |
| 1516 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 1519 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 1520 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 1521 | 1 | `public double[] getLandMarketPrices()` |  |
| 1523 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 1525 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 1528-2887)

| line | len | member | says |
|---:|---:|---|---|
| 1602 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 1603 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 1604 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 1605 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 1606 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 1607 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 1609 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 1610 | 1 | `public double[] getTaxPolicyState()` |  |
| 1618 | 1 | `public void setLeverOpenings(java.util.Map<String, Integer> openings)` |  |
| 1619 | 1 | `public java.util.Map<String, Integer> getLeverOpenings()` |  |
| 1621 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 1622 | 1 | `public double[] getHouseholdBalance()` |  |
| 1624 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1628 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1629 | 1 | `public double[] getHouseholdCells()` |  |
| 1631 | 1 | `public void setBankCash(double cash)` |  |
| 1632 | 1 | `public double getBankCash()` |  |
| 1634 | 1 | `public void setBankSecurities(Double value)` |  |
| 1635 | 1 | `public Double getBankSecurities()` |  |
| 1647 | 4 | `public void setBankDeposits(double household, double sector)` |  |
| 1651 | 1 | `public Double getBankHouseholdDeposits()` |  |
| 1652 | 1 | `public Double getBankSectorDeposits()` |  |
| 1665 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1666 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1677 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1678 | 1 | `public double getBankProfitLastMonth()` |  |
| 1682 | 1 | `public void setBankDepositRate(double v)` |  |
| 1683 | 1 | `public double getBankDepositRate()` |  |
| 1699 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1700 | 1 | `public double[] getForeignAccounts()` |  |
| 1705 | 1 | `public void setRateHistory(double[] ring)` |  |
| 1706 | 1 | `public double[] getRateHistory()` |  |
| 1718 | 1 | `public void setCentralBank(double[] state)` |  |
| 1719 | 1 | `public double[] getCentralBank()` |  |
| 1730 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1734 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1735 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1747 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1748 | 1 | `public double[] getForeignStanding()` |  |
| 1759 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1760 | 1 | `public double[] getCapitalFlows()` |  |
| 1765 | 1 | `public void setCapitalFlowsStopReason(String reason)` |  |
| 1766 | 1 | `public String getCapitalFlowsStopReason()` |  |
| 1776 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1777 | 1 | `public double[] getOutwardInvestment()` |  |
| 1788 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1789 | 1 | `public String[] getEquityKeys()` |  |
| 1790 | 1 | `public double[] getEquity()` |  |
| 1794 | 1 | `public void setExchange(double[] state)` |  |
| 1795 | 1 | `public double[] getExchange()` |  |
| 1800 | 1 | `public void setExchangeState(Exchange.State state)` | UP1: a copy - toState() lists the exchange's own books. |
| 1801 | 1 | `public Exchange.State getExchangeState()` |  |
| 1819 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1820 | 1 | `public double[] getBankSolvency()` |  |
| 1838 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1839 | 1 | `public double[] getHousingOccupancy()` |  |
| 1855 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1856 | 1 | `public double[] getBankLastMonth()` |  |
| 1867 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1868 | 1 | `public double[] getBankPricingHistory()` |  |
| 1879 | 1 | `public void setBankLateProfit(double value)` |  |
| 1880 | 1 | `public double getBankLateProfit()` |  |
| 1894 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1895 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1906 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1907 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1917 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1918 | 1 | `public double[] getBankMonthLines()` |  |
| 1929 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1930 | 1 | `public double[] getBankStatementYear()` |  |
| 1942 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1943 | 1 | `public double[] getBankSheetYear()` |  |
| 1955 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1956 | 1 | `public Double getBankPaidInOpening()` |  |
| 1957 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1958 | 1 | `public Double getBankRetainedOpening()` |  |
| 1969 | 5 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1974 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1975 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1987 | 1 | `public void setFund(TreasuryFund.State state)` | UP1: a copy - toState() lists the fund's own resolutions and orders. |
| 1988 | 1 | `public TreasuryFund.State getFund()` |  |
| 1993 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1994 | 1 | `public double[] getDebtMarket()` |  |
| 1999 | 1 | `public void setAuctionTally(double[] tally)` |  |
| 2000 | 1 | `public double[] getAuctionTally()` |  |
| 2011 | 1 | `public void setPriceIndex(double[] state)` |  |
| 2012 | 1 | `public double[] getPriceIndex()` |  |
| 2025 | 1 | `public void setExpectations(double[] state)` |  |
| 2026 | 1 | `public double[] getExpectations()` |  |
| 2038 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 2039 | 1 | `public double[] getWorldEconomy()` |  |
| 2054 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 2055 | 1 | `public double getTradedExchangeRate()` |  |
| 2065 | 1 | `public void setStruckRate(double rate)` |  |
| 2066 | 1 | `public double getStruckRate()` |  |
| 2080 | 1 | `public void setDenomination(double[] state)` |  |
| 2081 | 1 | `public double[] getDenomination()` |  |
| 2095 | 1 | `public void setPolicyRate(double rate)` |  |
| 2097 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 2106 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 2107 | 1 | `public boolean getPolicyAutopilot()` |  |
| 2115 | 1 | `public void setPolicyAim(String name)` |  |
| 2116 | 6 | `public DebtManager.Aim getPolicyAim()` |  |
| 2129 | 1 | `public void setPegSpread(double spread)` |  |
| 2130 | 1 | `public double getPegSpread()` |  |
| 2143 | 1 | `public void setRolloverMode(String mode)` |  |
| 2145 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 2150 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 2151 | 1 | `public double[] getRolloverLedger()` |  |
| 2152 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 2153 | 1 | `public double[] getRolloverRecord()` |  |
| 2164 | 5 | `public void setRolloverCap(Auction.Cap cap)` |  |
| 2169 | 3 | `public Auction.Cap getRolloverCap()` |  |
| 2181 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 2182 | 1 | `public boolean getLandPaidFromVault()` |  |
| 2194 | 1 | `public void setInflationTarget(double target)` |  |
| 2196 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 2206 | 1 | `public void setPolicyStrictness(String name)` |  |
| 2208 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 2219 | 1 | `public void setQeTargetShare(double share)` |  |
| 2220 | 1 | `public double getQeTargetShare()` |  |
| 2233 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 2235 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 2246 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 2247 | 1 | `public double getHouseholdPaperRatio()` |  |
| 2269 | 1 | `public void setBondMarket(BondMarket.State state)` | UP1: a copy - toState() lists the market's own bonds and books. |
| 2270 | 1 | `public BondMarket.State getBondMarket()` |  |
| 2271 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 2272 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 2273 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 2274 | 1 | `public double getHouseholdBondRatio()` |  |
| 2275 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 2276 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 2287 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 2291 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 2292 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 2316 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 2317 | 1 | `public double getCarsPerHousehold()` |  |
| 2330 | 1 | `public void setTransitBill(double v)` |  |
| 2331 | 1 | `public double getTransitBill()` |  |
| 2343 | 1 | `public void setCaptiveShare(double v)` |  |
| 2344 | 1 | `public double getCaptiveShare()` |  |
| 2345 | 1 | `public void setFuelPerJourney(double v)` |  |
| 2346 | 1 | `public double getFuelPerJourney()` |  |
| 2357 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 2358 | 1 | `public double[] getHouseholdFuel()` |  |
| 2367 | 1 | `public void setHomeFuelSale(double[] v)` |  |
| 2368 | 1 | `public double[] getHomeFuelSale()` |  |
| 2380 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 2381 | 1 | `public double[] getHouseholdGraduates()` |  |
| 2383 | 1 | `public void setRememberedCommute(double v)` |  |
| 2384 | 3 | `public double getRememberedCommute()` |  |
| 2397 | 1 | `public void setCostOfLiving(double v)` |  |
| 2398 | 1 | `public double getCostOfLiving()` |  |
| 2411 | 1 | `public void setBankTaxCharged(double v)` |  |
| 2412 | 1 | `public double getBankTaxCharged()` |  |
| 2416 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 2417 | 1 | `public double getRentWeight()` |  |
| 2437 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 2438 | 1 | `public double getRentWeightStudio()` |  |
| 2440 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 2444 | 1 | `public int getConstructionShedMonth()` |  |
| 2445 | 1 | `public double getConstructionShedPoints()` |  |
| 2447 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 2450 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 2624 | 1 | `public void setLicences(double[] a)` | UP1: a copy, not the live head counts - PopulationManager hands out its own arrays. |
| 2625 | 1 | `public double[] getLicences()` |  |
| 2626 | 1 | `public void setEducation(double[] a)` |  |
| 2627 | 1 | `public double[] getEducation()` |  |
| 2629 | 1 | `public void setLabour(double[] a)` |  |
| 2630 | 1 | `public double[] getLabour()` |  |
| 2631 | 1 | `public void setWantedPosts(double[] a)` |  |
| 2632 | 1 | `public double[] getWantedPosts()` |  |
| 2634 | 1 | `public void setSkilledWorkforce(double[] a)` | UP1: a copy, not the live head counts. |
| 2635 | 1 | `public double[] getSkilledWorkforce()` |  |
| 2637 | 1 | `public void setCohorts(double[] a)` |  |
| 2638 | 1 | `public double[] getCohorts()` |  |
| 2641 | 1 | `public void setBandNames(String[] names)` |  |
| 2643 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 2657 | 1 | `public void setShapeNames(String[] names)` |  |
| 2658 | 1 | `public String[] getShapeNames()` |  |
| 2659 | 1 | `public void setFamilies(double[] a)` |  |
| 2660 | 1 | `public double[] getFamilies()` |  |
| 2661 | 1 | `public void setMigration(double[] a)` |  |
| 2662 | 1 | `public double[] getMigration()` |  |
| 2663 | 1 | `public void setUnemployment(double[] a)` |  |
| 2664 | 1 | `public double[] getUnemployment()` |  |
| 2665 | 1 | `public void setSickness(double[] a)` |  |
| 2666 | 1 | `public double[] getSickness()` |  |
| 2667 | 1 | `public void setCrime(double[] a)` |  |
| 2668 | 1 | `public double[] getCrime()` |  |
| 2669 | 1 | `public void setHealth(double[] a)` |  |
| 2670 | 1 | `public double[] getHealth()` |  |
| 2671 | 1 | `public void setCausesOfDeath(double[] a)` |  |
| 2672 | 1 | `public double[] getCausesOfDeath()` |  |
| 2673 | 1 | `public void setHealthcare(double[] a)` |  |
| 2674 | 1 | `public double[] getHealthcare()` |  |
| 2702 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 2703 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 2704 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 2705 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 2706 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 2707 | 1 | `public double getCityCapitalSpending()` |  |
| 2725 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 2726 | 1 | `public double getCityMaintenancePaid()` |  |
| 2742 | 1 | `public void setLastInvestment(java.util.Map<String, String> m)` |  |
| 2743 | 1 | `public java.util.Map<String, String> getLastInvestment()` |  |
| 2744 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 2745 | 1 | `public double getMonthlyMaterialImports()` |  |
| 2746 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 2747 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 2748 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 2749 | 1 | `public int getMaterialsConsumed()` |  |
| 2758 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 2759 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 2769 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 2770 | 1 | `public double[] getHouseholdStatement()` |  |
| 2772 | 4 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 2776 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 2778 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2781 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2783 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2786 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2788 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2791 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2793 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2796 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2798 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2799 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2801 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2802 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2804 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2805 | 1 | `public double getInsurancePremiums()` |  |
| 2807 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2808 | 1 | `public double[] getNationalAccounts()` |  |
| 2810 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2811 | 1 | `public List<Double> getGdpRolling()` |  |
| 2821 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2824 | 3 | `public int getUnderConstruction(int index)` |  |
| 2827 | 3 | `public double getCash()` |  |
| 2831 | 3 | `public int getMonth()` |  |
| 2835 | 3 | `public int getBuildingQuantity(int index)` |  |
| 2839 | 3 | `public int getBuildingsLength()` |  |
| 2843 | 3 | `public JsonArray getDebt()` |  |
| 2847 | 3 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 2851 | 3 | `public JsonArray getBusinessDebt()` |  |
| 2855 | 3 | `public int getProgressLength()` |  |
| 2859 | 3 | `public double getProgress(int index)` |  |
| 2863 | 3 | `public int getConstructionMaterials()` |  |
| 2867 | 3 | `public long getPopulation()` |  |
| 2870 | 3 | `public boolean getReports()` |  |
| 2873 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2876 | 3 | `public boolean getGraphs()` |  |
| 2880 | 3 | `public String getRecordingId()` | The recording's id, or null in a city never recorded and in a save from before TR1. |
| 2884 | 3 | `public boolean isRecording()` | Whether the city was recording when it was saved; false in a save from before TR1. |


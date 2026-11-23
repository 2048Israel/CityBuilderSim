# DataSave.java - 3,041 lines · 488 methods · 1 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Founding](Founding.md) (7), [SectorBooks](SectorBooks.md) (6), [World](World.md) (5), [Nations](Nations.md) (5), [DebtManager](DebtManager.md) (5), [Rollover](Rollover.md) (5), [Notice](Notice.md) (4), [BuildLog](BuildLog.md) (4), [Currency](Currency.md) (4), [GameFiles](GameFiles.md) (4), [Bank](Bank.md) (4), [CapSetting](CapSetting.md) (4), [HousingInventory](HousingInventory.md) (3), [HousingMarket](HousingMarket.md) (3), [Motoring](Motoring.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [AutoBuilder](AutoBuilder.md) (3), [StrategicReserve](StrategicReserve.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [Ports](Ports.md) (3), [Zoning](Zoning.md) (3), [Landmasses](Landmasses.md) (3), [Bridges](Bridges.md) (3), [PartnerTrade](PartnerTrade.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [OldMetal](OldMetal.md) (3), [DemolitionLog](DemolitionLog.md) (3)... and 14 more

**Used by (24):** [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FittingCheck](FittingCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GoldMiningCheck](GoldMiningCheck.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HousingInventoryCheck](HousingInventoryCheck.md), [LandmassCheck](LandmassCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [MapCheck](MapCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [MoneyDatesCheck](MoneyDatesCheck.md), [MoneySignCheck](MoneySignCheck.md), [PolicyOpenCheck](PolicyOpenCheck.md), [PopulationCheck](PopulationCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [Recording](Recording.md), [ReplayCheck](ReplayCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md)

## Sections

| line | section |
|---:|---|
| 350 | · land and ore |
| 454 | · the shedding warning |
| 694 | · the header |
| 1180 | · construction, by id |
| 1486 | · charged, not derived |
| 1605 | · policy |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1006 | `DataSave.LOANS_GSON` | `new Gson()` | The one Gson the two loan lists below go through (GT1, 2026-11-02): a new Gson per fill had to build its reflective adapters for Debt and BusinessDebt again each time, about half a millisecond of the month that autosa... |

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
| 50 | `private Boolean currencySignAfter` | Whether the money's sign follows the figure (FM1); a save without the key reads false, the sign before. |
| 51 | `private Double foundingCash` |  |
| 52 | `private Double foundingReserveUsd` |  |
| 61 | `private Long worldSeed` | ...AND THE WORLD IT STANDS ON (0.7.56): the seed of the city's World. |
| 74 | `private String worldKind` | ...AND WHETHER IT IS THE REAL EARTH (0.7.121, batch E4; spec-earth 6), kept together: worldKind "EARTH" on a city founded on the Earth, absent on a generated world's - every save before 0.7.121 is one; the spot, earth... |
| 75 | `private Double earthLat` |  |
| 76 | `private Double earthLon` |  |
| 77 | `private String earthData` |  |
| 86 | `private String worldPath` | ...AND THE PATH OF THE WORLD BEYOND IT (WN1; spec-nations 7.3): which path the nations take, the enum's name() ("HISTORICAL" or "RANDOM"), and the seed that draws it - not the ground's worldSeed above. |
| 87 | `private Long pathSeed` |  |
| 94 | `private Boolean foundedUnfitted` | ...AND WHETHER IT WAS FOUNDED THE 1900 WAY (R1b; Founding.isUnfitted()): written only for a city that was, so a city founded the default way writes no key and a save without one reads false (getFounding()). |
| 97 | `private double cash` | save variables |
| 98 | `private long[] buildings` |  |
| 101 | `private java.util.List<HousingInventory.Holding> housingInventory` | RES2 bounded title/occupancy/listing cohorts; physical stock stays in buildings[]. |
| 102 | `private double[] homePayIndices` |  |
| 103 | `private int homePayMonth` |  |
| 107 | `private HousingMarket.State housingMarket` |  |
| 110 | `private Motoring.UsedTrade usedCarTrade` |  |
| 115 | `private int month` |  |
| 116 | `private JsonArray debts` |  |
| 124 | `private JsonArray businessDebts` | Private-sector loans. |
| 131 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 132 | `private int[] underConstruction` |  |
| 143 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 144 | `private long[] underConstructionById` |  |
| 146 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 148 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 150 | `private java.util.List<BuildingManager.ContractRecord> contractRecords` | ...and who placed each part of it, with its material still to draw and the allowance priced in for it (0.7.19). |
| 155 | `private Long foundingEndowments` | How many founding endowments the city holds (FW9; BuildingManager.getFoundingEndowments()): 1 for every city a player founds, K for a harness's copy of one K times over. |
| 165 | `private ConstructionControl.State constructionControl` | The player's hand on the queue (0.7.22; ConstructionControl): the city's order of its own sites, the rushes and their months on overtime, the cancels waiting for the month's end, the stopped shells, the demolitions on... |
| 173 | `private java.util.List<DecisionLog.Entry> decisionLog` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, a month, a kind and a line each. |
| 181 | `private AutoBuilder.State autoBuild` | Automatic building (0.7.73; AutoBuilder): the switch, the spare margin and the debt limit, its log of orders and what held it back at its last pass. |
| 191 | `private StrategicReserve.State reserve` | The city's strategic reserve (0.7.85, batch O8; StrategicReserve): the crude its tanks hold and what it cost, the release standing and the fill ordered, and the month's trades the next strike settles (runs/ spec-oil.m... |
| 197 | `private UtilitiesHandler.PowerState powerBooks` | The utility's struck book (PW1; UtilitiesHandler, THE MERIT ORDER): the prices this month's power is billed at, the ratio it served and the fuel bought at the next strike, and the month's fuel paid. |
| 208 | `private Ports.State portMonth` | The city's ports (0.7.86, batch O9; Ports): each kind of cargo's share of its tonnes at sea in force, whether it goes before the railway, the class crude goes in, the road's two shares, and the month's tonnes by kind ... |
| 219 | `private Zoning.State zoning` | The city's zones, its forest law and the forest cleared for building (0.7.107, batch Z1; Zoning, LandManager's ZONES AND THE FOREST LAW): each zone's sector, size and its kind, price share and the ground used in it; t... |
| 228 | `private Landmasses.State landmasses` | Where the city's sea terminals stand (ET5, 2026-10-25; Landmasses): per terminal template the landmass ids of its standing, on-site and stopped units, oldest first, and of the units a demolition on site is taking down. |
| 236 | `private Bridges.State bridges` | The city's long bridges (ET6, 2026-10-26; Bridges): each one built or building - its template, kind, the two landings and the landmass at each end, its length, the deepest and mean depth of the water under it, the cor... |
| 246 | `private Integer worldYear` | The world market (WN3, 2026-10-28; WorldMarket, Game's THE WORLD MARKET), when the city has one - a world file was there: the year its January last solved (worldYear), and the solved state, every modelled good's mill ... |
| 247 | `private double[] worldPrices` |  |
| 248 | `private double[] worldScales` |  |
| 249 | `private PartnerTrade.State partnerTrade` |  |
| 257 | `private double[] worldMoneys` | The world's moneys (WN4, 2026-10-30; WorldMoneys, Game's THE WORLD'S MONEYS IN THE MONTH), when the city has a world market: the month they stand in, the dollar cycle, the risk-off and each money's rate, swing, terms ... |
| 265 | `private double[] policyRates` | The eight central banks' policy rates (XR4, 2026-10-31; PolicyRates, Game's THE WORLD'S MONEYS IN THE MONTH), when the city has a world market: the month they stand in and each bank's rate, shadow, target and inflatio... |
| 286 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 301 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 312 | `private double cityForeignInterest` | ...AND THE MONTH'S COUPONS ABROAD (CD9, CD7-E, 2026-11-08): the foreign paper's interest, every money's, in D$, that EARNED's expenses carry beside the accrued interest (EconomyManager.getForeignInterest()). |
| 323 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 324 | `private java.util.List<Markets.State> markets` |  |
| 337 | `private OldMetal.State oldMetal` | The city's old steel (0.7.123, batch I4; OldMetal): what it has thrown out since its Scrap Yards last bought, by seller and by source, and the yards' last month. |
| 348 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 384 | `private Double worldSeaTheta` |  |
| 385 | `private double[] worldTotals` |  |
| 386 | `private double[] landCentre` |  |
| 387 | `private double[][] landCentreRects` |  |
| 388 | `private double[][] landHoldings` |  |
| 389 | `private double[][] landPartFields` |  |
| 390 | `private double[][] landConverted` |  |
| 391 | `private double[] landLanes` |  |
| 392 | `private double[][] landPurchases` |  |
| 393 | `private double[][] landOffers` |  |
| 394 | `private double[] landCentreRelief` |  |
| 395 | `private double[][] landHoldingsRelief` |  |
| 396 | `private double[][] landOffersRelief` |  |
| 404 | `private double[][] landOffersSellers` | WN5 (2026-11-01; spec-nations 6.2-6.4): who sells each offer standing - a row for each of landOffers, in its order, of (nation, km2, US$ thousands) for each nation whose ground it holds (LandParcel.sellersRow()), an e... |
| 405 | `private double[] landBoughtKm2` |  |
| 406 | `private double[] landBoughtUsd` |  |
| 407 | `private Integer nextOfferId` |  |
| 408 | `private double[] depletion` |  |
| 416 | `private Double oilDepletionAtSea` | What the platform wells have lifted from the city's offshore pool of crude, in tonnes (0.7.93, SAVE_FORMAT 35; LandManager's THE TWO OIL POOLS): since then the depletion's oil is the ground pool's alone. |
| 426 | `private double[][] depletionByType` | What has been taken out of each typed resource by type (0.7.110, batch I1; LandManager's THE GROUND BY TYPE): a row per resource in Resource's order, its types in Resource.typesOf()'s order, null for a resource whose ... |
| 434 | `private Integer landOldWorldHoldings` | How many of the city's holdings - the centre, then its purchases in the order made - hold the old world's fields (0.7.99, SAVE_FORMAT 36; CityLand.oldWorldHoldings()): 0 for a city founded on 0.7.99. |
| 435 | `private double[] landListing` |  |
| 437 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 438 | `private Integer ironDeposits` |  |
| 439 | `private Double ironReserveTonnes` |  |
| 446 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 452 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 470 | `private int constructionShedMonth` |  |
| 471 | `private double constructionShedPoints` |  |
| 478 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 489 | `private List<Double> gdpRolling` | The rolling year of GDP, exactly (0.7.81, batch N6): NationalAccounts' history, the last HISTORY_MONTHS of the month's GDP. |
| 499 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 506 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 507 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 515 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 516 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 524 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 536 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 537 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 538 | `private double insurancePremiums` |  |
| 541 | `private long constructionMaterials` | The city's own yard, in units. |
| 542 | `private long population` |  |
| 553 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 554 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 584 | `private java.util.Map<String, double[][]> sectorDebt, sectorDebtBefore` | ...AND THE DEBT THEIR SHEETS SPLIT (FW2, 2026-10-24): R1's interest by instrument and R2's debt by kind and when it falls due, this month's and last month's, by sector (SectorBooks.Debt) - and what had moved each sect... |
| 585 | `private java.util.Map<String, double[][]> creditMoved` |  |
| 601 | `private java.util.Map<String, java.util.Map<String, double[]>> sectorStock` | The stock each sector's books revalue next month (FW6, 2026-10-25): every business's stock by good as the month closed, {units, price} under the good's name (SectorBooks.stockToSave()). |
| 611 | `private java.util.Map<String, double[]> sectorShares, sectorSharesBefore` | Each listed company's share as this month closed and as last month did (FW6, 2026-10-25), by sector, in SectorBooks.SHARES_SLOTS slots (SectorBooks.sharesToSave()). |
| 621 | `private boolean reports` | settings |
| 622 | `private boolean graphs` |  |
| 634 | `private String recordingId` | THE RECORDING'S SWITCH AND ID (TR1, 2026-10-31). |
| 635 | `private boolean recording` |  |
| 646 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 656 | `private double landOwned` | Land. |
| 658 | `private Double landAllocatedSqFt` | AW2: the live land ledger, including an explicit zero; absent saves retain footprint reconstruction. |
| 660 | `private Double landFootprintSqFt` | AW2: the catalogue footprint at save time, so a later plot-size change adjusts the ledger by its delta. |
| 661 | `private int landBlocksPurchased` |  |
| 662 | `private double landPricePerSqFt` |  |
| 671 | `private Double landWorksSqFt` | ET2 (spec-earth2-terrain 3.2, 4.2, 4.4): the part of what is built on that works stand on (LandManager.getWorksSqFt()), and the foothill prepared so far (getPreparedSqFt(), which never falls, so it is history and not ... |
| 672 | `private Double landPreparedSqFt` |  |
| 679 | `private Double landRunsSqFt` | ET4 (spec-earth2-terrain 4.3): the part of what is built on that highways and railway lines stand on (LandManager.getRunsSqFt()). |
| 688 | `private double incomeTaxRate` | Tax rates. |
| 689 | `private double propertyTaxRate` |  |
| 820 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 838 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 839 | `private double[] treasuryJournalAmounts` |  |
| 840 | `private String[] treasuryJournalPendingLabels` |  |
| 841 | `private double[] treasuryJournalPendingAmounts` |  |
| 849 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 858 | `private double retiredGainPending` | The gain on paper the treasury has retired since the last strike - Game.retiredGainSoFar (CD3), the carrying value less the price paid - carried for the same reason: a city saved between two presses, after a buyback, ... |
| 867 | `private double[] interestBookedByKind` | The month's interest expense on each kind of paper, in DebtManager.BOOK_KINDS' order - DebtManager.interestByKind (CD6), the rows of the budget's interest line the Government tab's grid reads after the month. |
| 876 | `private double[][] curveBook` | The curve as the months behind it stood (CD6): one row a month, the newest DebtManager.CURVE_KEPT - [month, the dial, the city's zero rates at DebtManager.CURVE_TENORS, the world's at the same tenors] - DebtManager.cu... |
| 887 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 888 | `private double cityDiscountUnsettled` |  |
| 926 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 1191 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 1208 | `private java.util.Map<Integer, java.util.Map<String, Long>> unfittedById` | The buildings still WITHOUT a piece of equipment, by template id and then by the piece's id (R1; BuildingsStacks' unfitted counts), written only for the stacks that have any: null, or empty, is every building fitted. |
| 1225 | `private java.util.Map<Integer, java.util.List<BuildingsStacks.Lot>> lotsById` | The equipment each site's orders froze, by template id (R1e; BuildingsStacks' lots: so many buildings, the pieces they come without), written only for the sites that have a lot, so only a city founded the 1900 way eve... |
| 1242 | `private java.util.Map<Integer, java.util.List<String>> recipeChoices` | The recipes the two parts of a stack run (R1c; Recipes), by template id: [the fitted part's recipe id, the unfitted part's], written only for a template where a part runs a recipe other than the default, so only a cit... |
| 1245 | `private java.util.Map<Integer, Double> licenceFees` | The last captured royalty per installed building, by template id; a saved flow for standing readers (R2c). |
| 1259 | `private Double publicIpPending, publicIpPaid` | The bill on the city's own patented works (R2c-P; LicenceFees.capturePublic()): what production accrued, to be paid at the next strike, and what the last strike paid. |
| 1282 | `private java.util.List<Fittings.Job> fittings` | The fittings on site (R1b; Fittings), in the order placed, each with the template, the piece, who ordered it and the points still owed: written only while there is a job, so a city with none writes no key and a save w... |
| 1301 | `public java.util.Map<String, Double> grants` |  |
| 1302 | `public java.util.Map<String, Integer> posts` |  |
| 1303 | `public java.util.Map<String, Double> piles` |  |
| 1304 | `public java.util.Map<String, Integer> found` |  |
| 1305 | `public double payroll, costs` |  |
| 1308 | `public java.util.Map<String, Double> prizes` | R4: the prize dials, the pots, the labs' part of the piles, the city's own researchers on each problem last month, the months in a row a field has had a lab too many, and the prize the treasury paid the labs that mont... |
| 1309 | `public java.util.Map<String, Double> pots` |  |
| 1310 | `public java.util.Map<String, Double> labPiles` |  |
| 1311 | `public java.util.Map<String, Double> cityShares` |  |
| 1312 | `public java.util.Map<String, Integer> labIdleMonths` |  |
| 1313 | `public double prizePaid` |  |
| 1316 | `public java.util.Map<String, Double> researchers` | R3b: the month's researchers, idle researchers, open problems, lab researchers and idle lab researchers by field name (only the fields above nothing), which Research.month() struck and a load would otherwise read as 0... |
| 1317 | `public java.util.Map<String, Double> idle` |  |
| 1318 | `public java.util.Map<String, Integer> open` |  |
| 1319 | `public java.util.Map<String, Double> labResearchers` |  |
| 1320 | `public java.util.Map<String, Double> labsIdle` |  |
| 1323 | `private ResearchRecord research` |  |
| 1615 | `private double[] taxPolicyState` |  |
| 1628 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 1640 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 1641 | `private double[] householdCells` |  |
| 1652 | `private java.util.Map<String, Integer> ownerFamilies` | The owner line (BO1): the companies that have an owner family, each with its months at the exchange's floor, keyed by Equity.COMPANIES name. |
| 1661 | `private Double ownerAdults` | The owner families' adults as the month's census counted them (BO1): FamilyModel's, out of the labour force. |
| 1673 | `private double bankCash` | The bank's cash. |
| 1681 | `private Double bankSecurities` | The desk's inventory as the bank carries it (Bank.getSecurities(), FL1): the mark of its last re-mark, the exchange's step. |
| 1691 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 1694 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 1695 | `private SalesTaxLedger.State salesTax` |  |
| 1697 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 1738 | `private Double bankHouseholdDeposits, bankSectorDeposits` | The bank's deposit book as the month last set it (FW2, 2026-10-24; F-S1-1): what the families and the businesses had banked when Game.refreshBank() last ran in the month, which the load's own refreshBank() would re-re... |
| 1756 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 1768 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 1774 | `private double bankDepositRate` | What savers were paid. |
| 1793 | `private double[] foreignAccounts` | R0f: includes the Canadian gold episode and its exhausted flag (ForeignAccounts slots 156-157). |
| 1799 | `private double[] rateHistory` | Game's ring of the last twelve exchange rates and how full it is (XR1): the year's depreciation the hot money reads. |
| 1812 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 1823 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 1824 | `private double[] treasuryArrearsAmounts` |  |
| 1841 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1853 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1859 | `private String capitalFlowsStopReason` | The words the sudden stop gave when it began (FW6, 2026-10-25): a String the capital flows' array cannot hold, for the Trade screen's card. |
| 1870 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1881 | `private String[] equityKeys` | The share register, company by company, named. |
| 1882 | `private double[] equity` |  |
| 1889 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1894 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1913 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1932 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1949 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1961 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1973 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1988 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 2000 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 2011 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 2023 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 2036 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 2048 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 2049 | `private Double bankRetainedOpening` |  |
| 2062 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 2063 | `private double[] bankPreferredRecord` |  |
| 2080 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 2087 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 2093 | `private double[] auctionTally` | The city's auctions counted (CD2, D33): AuctionTally.toSave(), fourteen slots. |
| 2105 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 2119 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 2132 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 2148 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 2159 | `private double struckRate` | The D$ per US$ the city's goods were struck at (AN1, EconomyManager's THE D$ THE GOODS ARE STRUCK AT): the state the next month's strike moves from, which neither the D$ nor the traded rate above can give back. |
| 2174 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 2189 | `private Double policyRate` | The policy rate. |
| 2200 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 2209 | `private String policyAim` | What the autopilot aims at (XR2; DebtManager.Aim), by name. |
| 2223 | `private Double pegSpread` | The spread the dial stands at over the US's rate under a peg (XR2), a fraction a year. |
| 2235 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 2236 | `private double[] rolloverLedger` |  |
| 2237 | `private double[] rolloverRecord` |  |
| 2257 | `private String rolloverCapMode` | ...and the cap its auctions take (CD2, D28), the mode by name and the tail or yield beside it, two plain fields because a cap's Fill it carries no number and Gson writes no NaN (CapSetting). |
| 2258 | `private double rolloverCapValue` |  |
| 2275 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 2288 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 2300 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 2313 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 2327 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 2340 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 2359 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 2360 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 2361 | `private double householdBondRatio` |  |
| 2362 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 2380 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 2381 | `private double buybackAbroadUnsettled` |  |
| 2404 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 2410 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 2424 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 2437 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 2451 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 2461 | `private double[] homeFuelSale` | ...and the homes' lamp oil and coal as 6d struck them for the households' next bill (R0e): {homes, litres, tonnes, bill, cost, imported} (HomeFuel.heldSale()) - a sale off the refiners' and mines' shelves is a month p... |
| 2474 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 2487 | `private double[] householdFlows` | ...and the month's flows waiting for the next census (HS1, HouseholdBalance.flowsToSave()): the shares of each band who retired, aged on, died and left, which carry their own money at the census before the pool. |
| 2489 | `private double[] householdInvestmentTaxes` | HT1's struck investment-tax transfers and actual assessment counts. |
| 2490 | `private double[] securedHouseholdTaxes, securedForeignTaxes` |  |
| 2491 | `private EstateCustody.State estateCustody` |  |
| 2492 | `private double unbackedCustodyPaper` |  |
| 2520 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 2534 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 2560 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 2593 | `private double[] cohorts` | The demographics. |
| 2617 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 2619 | `private double[] families` |  |
| 2620 | `private double[] migration` |  |
| 2630 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 2639 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 2642 | `private double[] sicknessAcute` | Acute influenza shares by saved age band, beside the mortality ring (R0h); null means none. |
| 2652 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 2665 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 2676 | `private double[] causesOfDeath` | The causes of death of 1900 (R0b; CausesOfDeath.getState()): last month's dead of each of the seven, the dead of each since the city began, the last twelve months' dead of each, one slot to a month, and the people sic... |
| 2689 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 2692 | `private double[] healthcareAccess` | SZ1: the month's offered care and shares who could pay it; null when the save did not carry access. |
| 2708 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 2722 | `private double[] wantedPosts` | The posts per wage band the staffing gate refused this month that the labour market prices as if they were offered (LD1; WantedPosts). |
| 2734 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 2751 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 2752 | `private double[] education` |  |
| 2785 | `private String[] shapeNames` | The household shapes this save was written with. |
| 2825 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 2826 | `private java.util.Map<String, Integer> sectorOperatingLossMonths` |  |
| 2827 | `private java.util.List<Long> populationTrend` |  |
| 2828 | `private double cityCapitalSpending` |  |
| 2829 | `private double monthlyMaterialImports` |  |
| 2836 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 2837 | `private long materialsConsumed` |  |
| 2852 | `private java.util.List<Double> buildersWork` | The work each recent month ordered of the builders, their own depots left out, and what everyone else's sites owed when the last advance ended (OG1; BusinessInvestment, THE WORK THE CITY SENDS THE BUILDERS): what the ... |
| 2853 | `private Double buildersOwedAfterAdvance` |  |
| 2876 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 2893 | `private java.util.Map<String, String> lastInvestment` | THE INVESTORS' LAST WORD ON EACH SECTOR (0.7.102, Jerus's A22): what the month's investment pass decided for it and why - "Built 2 ...", "Holding: ...", a refusal - as Game.getLastInvestment() reads it, the Sectors sc... |
| 2910 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 2921 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 3030 | **type** `public class DataSave implements Cloneable` |  |
| 104 | 1 | `public void setHomePayIndices(double[] indices, int month)` |  |
| 105 | 1 | `public double[] getHomePayIndices()` |  |
| 106 | 1 | `public int getHomePayMonth()` |  |
| 108 | 1 | `public void setHousingMarket(HousingMarket.State state)` |  |
| 109 | 1 | `public HousingMarket.State getHousingMarket()` |  |
| 111 | 1 | `public void setUsedCarTrade(Motoring.UsedTrade state)` |  |
| 112 | 1 | `public Motoring.UsedTrade getUsedCarTrade()` |  |
| 113 | 1 | `public void setHousingInventory(java.util.List<HousingInventory.Holding> holdings)` |  |
| 114 | 1 | `public java.util.List<HousingInventory.Holding> getHousingInventory()` |  |
| 326 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 327 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 328 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 329 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |
| 339 | 1 | `public void setOldMetal(OldMetal.State s)` |  |
| 341 | 1 | `public OldMetal.State getOldMetal()` | The old steel as saved (0.7.123), or null on a save from before. |

### land and ore (lines 350-453)

### the shedding warning (lines 454-693)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 560 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 564 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 568 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |
| 587 | 4 | `public void setSectorDebt(java.util.Map<String, double[][]> now, java.util.Map<String, double[][]> before)` |  |
| 591 | 1 | `public java.util.Map<String, double[][]> getSectorDebt()` |  |
| 592 | 1 | `public java.util.Map<String, double[][]> getSectorDebtBefore()` |  |
| 593 | 1 | `public void setCreditMoved(java.util.Map<String, double[][]> moved)` |  |
| 594 | 1 | `public java.util.Map<String, double[][]> getCreditMoved()` |  |
| 603 | 1 | `public void setSectorStock(java.util.Map<String, java.util.Map<String, double[]>> stock)` |  |
| 604 | 1 | `public java.util.Map<String, java.util.Map<String, double[]>> getSectorStock()` |  |
| 613 | 4 | `public void setSectorShares(java.util.Map<String, double[]> now, java.util.Map<String, double[]> before)` |  |
| 617 | 1 | `public java.util.Map<String, double[]> getSectorShares()` |  |
| 618 | 1 | `public java.util.Map<String, double[]> getSectorSharesBefore()` |  |

### the header (lines 694-1179)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 1 | `public void setSlotName(String name)` |  |
| 699 | 24 | `public void setFounding(Founding f)` | The founding record, whole. |
| 734 | 36 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 772 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 775 | 1 | `public String getWorldKind()` | The world's kind as saved (0.7.121): "EARTH" on a city founded on the real Earth, null on a generated world's and on every save before 0.7.121. |
| 778 | 1 | `public boolean isOnEarth()` | Whether the save is a city on the real Earth (0.7.121): worldKind EARTH. |
| 781 | 1 | `public Double getEarthLat()` | The Earth's spot as saved, latitude in degrees (0.7.121); null on a generated world. |
| 784 | 1 | `public Double getEarthLon()` | ...its longitude. |
| 787 | 1 | `public String getEarthData()` | The Earth's data the city was drawn from, by identity ("earth-2:<hash>", 0.7.121); null on a generated world. |
| 790 | 1 | `public String getWorldPath()` | The path of the world beyond the city as saved (WN1): "HISTORICAL" or "RANDOM", null on a save from before it. |
| 793 | 1 | `public Long getPathSeed()` | The path's seed as saved (WN1); null on a save from before it. |
| 804 | 5 | `public String earthProblem()` | Why this save's world cannot be read here, or null (0.7.121): a kind this build does not know, or an Earth city whose spot is missing or whose data is not this build's or not here, or (0.7.127) that was saved before i... |
| 811 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 822 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 823 | 1 | `public double[] getTreasuryMonth()` |  |
| 890 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 894 | 1 | `public double getCityPaperUnsettled()` |  |
| 895 | 1 | `public double getCityDiscountUnsettled()` |  |
| 897 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 904 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 905 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 906 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 907 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 908 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 909 | 1 | `public double getTreasuryRaisedPending()` |  |
| 910 | 1 | `public void setRetiredGainPending(double v)` |  |
| 911 | 1 | `public double getRetiredGainPending()` |  |
| 912 | 1 | `public void setInterestBookedByKind(double[] v)` |  |
| 913 | 1 | `public double[] getInterestBookedByKind()` |  |
| 914 | 1 | `public void setCurveBook(double[][] v)` |  |
| 915 | 1 | `public double[][] getCurveBook()` |  |
| 928 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 929 | 1 | `public double[] getGovernmentMonth()` |  |
| 930 | 1 | `public String getSlotName()` |  |
| 933 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 939 | 1 | `public String getGameVersion()` |  |
| 940 | 1 | `public int getSaveFormat()` |  |
| 941 | 1 | `public long getSavedAt()` |  |
| 943 | 1 | `public void setHouseholdSavings(double value)` |  |
| 944 | 1 | `public double getHouseholdSavings()` |  |
| 946 | 1 | `public void setLandOwned(double sqFt)` |  |
| 947 | 1 | `public void setLandAllocatedSqFt(double sqFt)` |  |
| 948 | 1 | `public void setLandFootprintSqFt(double sqFt)` |  |
| 949 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 950 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 952 | 4 | `public void setLandWorks(double worksSqFt, double preparedSqFt)` | The works' ground and the foothill prepared (ET2): LandManager.getWorksSqFt() and getPreparedSqFt(). |
| 957 | 1 | `public void setLandRuns(double runsSqFt)` | The runs' ground (ET4): LandManager.getRunsSqFt(). |
| 959 | 1 | `public double getLandOwned()` |  |
| 960 | 1 | `public Double getLandAllocatedSqFt()` |  |
| 961 | 1 | `public Double getLandFootprintSqFt()` |  |
| 962 | 1 | `public int getLandBlocksPurchased()` |  |
| 963 | 1 | `public double getLandPricePerSqFt()` |  |
| 965 | 1 | `public Double getLandWorksSqFt()` | The works' ground as saved (ET2), or null on a save without it: read from the standing buildings. |
| 967 | 1 | `public Double getLandPreparedSqFt()` | The foothill prepared as saved (ET2), or null on a save without it. |
| 969 | 1 | `public Double getLandRunsSqFt()` | The runs' ground as saved (ET4), or null on a save without it: read from the standing highways and railway lines. |
| 971 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 972 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 974 | 1 | `public double getIncomeTaxRate()` |  |
| 975 | 1 | `public double getPropertyTaxRate()` |  |
| 977 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 982 | 3 | `public void setCash(double money)` | setters |
| 986 | 3 | `public void setMonth(int month)` |  |
| 990 | 3 | `public void setBuildingNum(int i)` |  |
| 994 | 6 | `public void setBuildingQuantity(int index, long quantity)` |  |
| 1008 | 3 | `public void setDebt(List<Debt> debts)` |  |
| 1012 | 3 | `public void setProgress(double[] progress)` |  |
| 1016 | 3 | `public void setConstructionMaterials(long constructionMaterials)` |  |
| 1020 | 3 | `public void setPopulation(long population)` |  |
| 1023 | 3 | `public void setReports(boolean reports)` |  |
| 1036 | 4 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 1040 | 3 | `public void setGraphs(boolean graphs)` |  |
| 1044 | 4 | `public void setRecording(String id, boolean on)` | The recording's id (null: never recorded) and whether "Record this city" is on (TR1; see the field). |
| 1081 | 28 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 1128 | 10 | `public DataSave snapshot()` | A copy of this save for the writer thread (UP1, 2026-10-22): the object itself, shallow, plus a copy of every array or list DataSave mutates in place. |
| 1147 | 25 | `String describeUnwritable()` | Names the field that broke, if it can find it. |
| 1173 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 1180-1485)

| line | len | member | says |
|---:|---:|---|---|
| 1193 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 1194 | 1 | `public int[] getStackOrder()` |  |
| 1196 | 6 | `public void setConstructionById(long[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 1210 | 3 | `public void setUnfittedById(java.util.Map<Integer, java.util.Map<String, Long>> byId)` |  |
| 1215 | 3 | `public java.util.Map<Integer, java.util.Map<String, Long>> getUnfittedById()` | The unfitted counts the save carries by template id, or an empty map for a save with none (every building fitted). |
| 1227 | 3 | `public void setLotsById(java.util.Map<Integer, java.util.List<BuildingsStacks.Lot>> byId)` |  |
| 1232 | 3 | `public java.util.Map<Integer, java.util.List<BuildingsStacks.Lot>> getLotsById()` | The lots the save carries by template id, or an empty map for a save with none (every building on site loose). |
| 1247 | 3 | `public void setLicenceFees(java.util.Map<Integer, Double> byId)` |  |
| 1251 | 3 | `public java.util.Map<Integer, Double> getLicenceFees()` |  |
| 1261 | 1 | `public double getPublicIpPending()` |  |
| 1263 | 1 | `public void setPublicIpPending(double amount)` | Stores the amount owed, null (no key in the file) for 0, a figure not finite or below 0 (R2c-P). |
| 1264 | 1 | `public double getPublicIpPaid()` |  |
| 1266 | 1 | `public void setPublicIpPaid(double amount)` | Stores the amount last paid, null (no key in the file) for 0, a figure not finite or below 0 (R2c-P). |
| 1268 | 3 | `public void setRecipeChoices(java.util.Map<Integer, java.util.List<String>> byId)` |  |
| 1273 | 3 | `public java.util.Map<Integer, java.util.List<String>> getRecipeChoices()` | The recipe choices the save carries by template id, or an empty map for a save with none (every building on its default). |
| 1285 | 3 | `public void setFittings(java.util.List<Fittings.Job> jobs)` | The jobs to write: a list of the caller's own copies (Fittings.view()), or none. |
| 1290 | 1 | `public java.util.List<Fittings.Job> getFittings()` | The fittings the save carries, or null for a save with none. |
| 1300 | 22 | **type** `public static final class ResearchRecord` | The city's research (R3; Research): the grants by field name, the posts the month struck by field name, the work piled by code and the discoveries found here by code with the month each was found, in the order found. |
| 1330 | 3 | `public void setResearch(Research r, double payroll, double costs)` | The research to write with the month's bill (EconomyManager.getResearchPayroll(), getResearchCosts()), or none: a city that has no grant, post, pile or find and no researcher this month writes no key. |
| 1335 | 25 | `public void setResearch(Research r, double payroll, double costs, double prizePaid)` | ...and with the prize the treasury paid the labs that month (R4), after the prize dials, the pots, the labs' piles, the city's shares and the idle counts, which Research gives. |
| 1362 | 1 | `public ResearchRecord getResearch()` | The research the save carries, or null for a save with none. |
| 1365 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 1367 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 1369 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 1371 | 1 | `public void setFoundingEndowments(long endowments)` |  |
| 1373 | 1 | `public long getFoundingEndowments()` | The founding endowments as saved, or 1 on a save without the figure (FW9). |
| 1376 | 1 | `public void setConstructionControl(ConstructionControl.State state)` | UP1: a copy - toState() is the control's own live state. |
| 1378 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 1380 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 1382 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 1384 | 1 | `public void setAutoBuild(AutoBuilder.State state)` |  |
| 1386 | 1 | `public AutoBuilder.State getAutoBuild()` | Null on a save from before 0.7.73. |
| 1388 | 1 | `public void setReserve(StrategicReserve.State state)` |  |
| 1390 | 1 | `public StrategicReserve.State getReserve()` | Null on a save from before 0.7.85. |
| 1392 | 1 | `public void setPowerBooks(UtilitiesHandler.PowerState state)` |  |
| 1394 | 1 | `public UtilitiesHandler.PowerState getPowerBooks()` | Null on a save from before PW1: the founding book. |
| 1396 | 1 | `public void setZoning(Zoning.State state)` |  |
| 1398 | 1 | `public Zoning.State getZoning()` | Null on a save from before 0.7.107, or with nothing zoned, no law and no forest cleared. |
| 1400 | 1 | `public void setLandmasses(Landmasses.State state)` |  |
| 1402 | 1 | `public Landmasses.State getLandmasses()` |  |
| 1404 | 1 | `public void setBridges(Bridges.State state)` |  |
| 1407 | 1 | `public Bridges.State getBridges()` | Null on a save from before ET6, and for a city with no bridge. |
| 1410 | 6 | `public void setWorldMarket(Integer year, double[] prices, double[] scales)` | The world market's solved year and state (WN3), copied: all three or none. |
| 1418 | 1 | `public Integer getWorldYear()` | The year the saved world last solved, or null on a save with no world market (older, or no world file). |
| 1421 | 1 | `public double[] getWorldPrices()` | The saved mill prices, good-major; null with no market. |
| 1424 | 1 | `public double[] getWorldScales()` | The saved supply scales; null with no market. |
| 1427 | 1 | `public void setWorldMoneys(double[] state)` | The world's moneys' state (WN4), copied; null for none. |
| 1430 | 1 | `public double[] getWorldMoneys()` | The saved world moneys (WorldMoneys.toSaveArray()); null on a save with none (older, or no world file). |
| 1433 | 1 | `public void setPolicyRates(double[] state)` | The eight central banks' policy rates' state (XR4), copied; null for none. |
| 1436 | 1 | `public double[] getPolicyRates()` | The saved policy rates (PolicyRates.toSaveArray()); null on a save with none (older, or no world file). |
| 1439 | 1 | `public void setPartnerTrade(PartnerTrade.State state)` | The city's trade by partner (WN3), the producer's fresh state. |
| 1442 | 1 | `public PartnerTrade.State getPartnerTrade()` | Null on a save with no world market. |
| 1444 | 1 | `public void setPortMonth(Ports.State state)` |  |
| 1446 | 1 | `public Ports.State getPortMonth()` | Null on a save from before 0.7.86. |
| 1449 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 1453 | 3 | `public int getConstructionByIdLength()` |  |
| 1457 | 6 | `public long getUnderConstructionById(int templateId)` |  |
| 1464 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 1471 | 6 | `public double getContractValueById(int templateId)` |  |
| 1479 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 1486-1604)

| line | len | member | says |
|---:|---:|---|---|
| 1488 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 1489 | 1 | `public double getPropertyTaxCharged()` |  |
| 1491 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 1492 | 1 | `public double getCityInterestAccrued()` |  |
| 1494 | 1 | `public void setCityForeignInterest(double value)` |  |
| 1495 | 1 | `public double getCityForeignInterest()` |  |
| 1497 | 1 | `public void setWorkforce(long workforce)` |  |
| 1500 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 1503 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 1525 | 5 | `public void setCityLandRelief(double[] centre, double[][] holdings, double[][] offers)` | The city's ground by relief and shallow sea (ET1): the centre's, a row for each purchase and a row for each offer standing, each CityLand.RELIEF_AREAS figures in km2 (CityLand.centreReliefState(), holdingsReliefState(... |
| 1532 | 3 | `public void setLandOffersSellers(double[][] offers)` | Who sells each offer standing (WN5; LandMarket.getOffersSellersState()), a row for each of landOffers; null for none. |
| 1537 | 4 | `public void setLandBought(double[] km2, double[] usd)` | What the city has bought from each nation, in km2 and in thousands of US dollars (WN5; Game.getLandBoughtKm2()): a figure for each nation of the world file and index 0 for the purchases with no seller named; null for ... |
| 1543 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 1545 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 1547 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 1549 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 1551 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 1553 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 1555 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 1556 | 1 | `public double[][] getLandOffers()` |  |
| 1558 | 1 | `public double[] getLandCentreRelief()` | The centre's dry ground by relief and shallow sea as saved (ET1), or null on a save from before: all flat. |
| 1560 | 1 | `public double[][] getLandHoldingsRelief()` | The purchases' relief, a row each in landHoldings' order, or null: all flat. |
| 1562 | 1 | `public double[][] getLandOffersRelief()` | The offers' relief, a row each in landOffers' order, or null: all flat. |
| 1564 | 1 | `public double[][] getLandOffersSellers()` | The offers' sellers, a row each in landOffers' order, or null: every offer the host's whole (WN5). |
| 1566 | 1 | `public double[] getLandBoughtKm2()` | The km2 bought from each nation (index 0: no seller named), or null for nothing bought (WN5). |
| 1568 | 1 | `public double[] getLandBoughtUsd()` | ...and the thousands of US dollars paid. |
| 1570 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 1571 | 1 | `public double[] getDepletion()` |  |
| 1573 | 1 | `public void setOilDepletionAtSea(double tonnes)` |  |
| 1574 | 1 | `public void setDepletionByType(double[][] byType)` |  |
| 1576 | 1 | `public double[][] getDepletionByType()` | E by type as saved (0.7.110), a row per resource or null for the walk's reading; null on a save that carries none. |
| 1578 | 1 | `public Double getOilDepletionAtSea()` | The offshore pool's E as saved (0.7.93), or null on a save from before the two pools. |
| 1580 | 1 | `public void setLandOldWorldHoldings(int holdings)` |  |
| 1582 | 1 | `public Integer getLandOldWorldHoldings()` | How many holdings hold the old world's fields, as saved (0.7.99), or null on a save from before. |
| 1583 | 1 | `public double[] getWorldTotals()` |  |
| 1585 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 1587 | 1 | `public void setFreshRights(double units)` |  |
| 1589 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 1591 | 1 | `public void setMapStamp(Long stamp)` |  |
| 1593 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 1596 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 1597 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 1598 | 1 | `public double[] getLandMarketPrices()` |  |
| 1600 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: kept only as an obsolete save key. |
| 1602 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 1605-3041)

| line | len | member | says |
|---:|---:|---|---|
| 1699 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 1700 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 1701 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 1702 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 1703 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 1704 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 1706 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 1707 | 1 | `public double[] getTaxPolicyState()` |  |
| 1709 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 1710 | 1 | `public double[] getHouseholdBalance()` |  |
| 1712 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 1716 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 1717 | 1 | `public double[] getHouseholdCells()` |  |
| 1719 | 1 | `public void setOwnerFamilies(java.util.Map<String, Integer> line)` |  |
| 1720 | 1 | `public java.util.Map<String, Integer> getOwnerFamilies()` |  |
| 1721 | 1 | `public void setOwnerAdults(double adults)` |  |
| 1722 | 1 | `public double getOwnerAdults()` |  |
| 1724 | 1 | `public void setBankCash(double cash)` |  |
| 1725 | 1 | `public double getBankCash()` |  |
| 1727 | 1 | `public void setBankSecurities(Double value)` |  |
| 1728 | 1 | `public Double getBankSecurities()` |  |
| 1740 | 4 | `public void setBankDeposits(double household, double sector)` |  |
| 1744 | 1 | `public Double getBankHouseholdDeposits()` |  |
| 1745 | 1 | `public Double getBankSectorDeposits()` |  |
| 1758 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 1759 | 1 | `public double getBankBranchesCapitalised()` |  |
| 1770 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 1771 | 1 | `public double getBankProfitLastMonth()` |  |
| 1775 | 1 | `public void setBankDepositRate(double v)` |  |
| 1776 | 1 | `public double getBankDepositRate()` |  |
| 1795 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 1796 | 1 | `public double[] getForeignAccounts()` |  |
| 1801 | 1 | `public void setRateHistory(double[] ring)` |  |
| 1802 | 1 | `public double[] getRateHistory()` |  |
| 1814 | 1 | `public void setCentralBank(double[] state)` |  |
| 1815 | 1 | `public double[] getCentralBank()` |  |
| 1826 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1830 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1831 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1843 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1844 | 1 | `public double[] getForeignStanding()` |  |
| 1855 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1856 | 1 | `public double[] getCapitalFlows()` |  |
| 1861 | 1 | `public void setCapitalFlowsStopReason(String reason)` |  |
| 1862 | 1 | `public String getCapitalFlowsStopReason()` |  |
| 1872 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1873 | 1 | `public double[] getOutwardInvestment()` |  |
| 1884 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1885 | 1 | `public String[] getEquityKeys()` |  |
| 1886 | 1 | `public double[] getEquity()` |  |
| 1890 | 1 | `public void setExchange(double[] state)` |  |
| 1891 | 1 | `public double[] getExchange()` |  |
| 1896 | 1 | `public void setExchangeState(Exchange.State state)` | UP1: a copy - toState() lists the exchange's own books. |
| 1897 | 1 | `public Exchange.State getExchangeState()` |  |
| 1915 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1916 | 1 | `public double[] getBankSolvency()` |  |
| 1934 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1935 | 1 | `public double[] getHousingOccupancy()` |  |
| 1951 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1952 | 1 | `public double[] getBankLastMonth()` |  |
| 1963 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1964 | 1 | `public double[] getBankPricingHistory()` |  |
| 1975 | 1 | `public void setBankLateProfit(double value)` |  |
| 1976 | 1 | `public double getBankLateProfit()` |  |
| 1990 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1991 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 2002 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 2003 | 1 | `public double[] getBankCapitalRecord()` |  |
| 2013 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 2014 | 1 | `public double[] getBankMonthLines()` |  |
| 2025 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 2026 | 1 | `public double[] getBankStatementYear()` |  |
| 2038 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 2039 | 1 | `public double[] getBankSheetYear()` |  |
| 2051 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 2052 | 1 | `public Double getBankPaidInOpening()` |  |
| 2053 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 2054 | 1 | `public Double getBankRetainedOpening()` |  |
| 2065 | 5 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 2070 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 2071 | 1 | `public double[] getBankPreferredRecord()` |  |
| 2083 | 1 | `public void setFund(TreasuryFund.State state)` | UP1: a copy - toState() lists the fund's own resolutions and orders. |
| 2084 | 1 | `public TreasuryFund.State getFund()` |  |
| 2089 | 1 | `public void setDebtMarket(double[] market)` |  |
| 2090 | 1 | `public double[] getDebtMarket()` |  |
| 2095 | 1 | `public void setAuctionTally(double[] tally)` |  |
| 2096 | 1 | `public double[] getAuctionTally()` |  |
| 2107 | 1 | `public void setPriceIndex(double[] state)` |  |
| 2108 | 1 | `public double[] getPriceIndex()` |  |
| 2121 | 1 | `public void setExpectations(double[] state)` |  |
| 2122 | 1 | `public double[] getExpectations()` |  |
| 2134 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 2135 | 1 | `public double[] getWorldEconomy()` |  |
| 2150 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 2151 | 1 | `public double getTradedExchangeRate()` |  |
| 2161 | 1 | `public void setStruckRate(double rate)` |  |
| 2162 | 1 | `public double getStruckRate()` |  |
| 2176 | 1 | `public void setDenomination(double[] state)` |  |
| 2177 | 1 | `public double[] getDenomination()` |  |
| 2191 | 1 | `public void setPolicyRate(double rate)` |  |
| 2193 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 2202 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 2203 | 1 | `public boolean getPolicyAutopilot()` |  |
| 2211 | 1 | `public void setPolicyAim(String name)` |  |
| 2212 | 6 | `public DebtManager.Aim getPolicyAim()` |  |
| 2225 | 1 | `public void setPegSpread(double spread)` |  |
| 2226 | 1 | `public double getPegSpread()` |  |
| 2239 | 1 | `public void setRolloverMode(String mode)` |  |
| 2241 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 2246 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 2247 | 1 | `public double[] getRolloverLedger()` |  |
| 2248 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 2249 | 1 | `public double[] getRolloverRecord()` |  |
| 2260 | 5 | `public void setRolloverCap(Auction.Cap cap)` |  |
| 2265 | 3 | `public Auction.Cap getRolloverCap()` |  |
| 2277 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 2278 | 1 | `public boolean getLandPaidFromVault()` |  |
| 2290 | 1 | `public void setInflationTarget(double target)` |  |
| 2292 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 2302 | 1 | `public void setPolicyStrictness(String name)` |  |
| 2304 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 2315 | 1 | `public void setQeTargetShare(double share)` |  |
| 2316 | 1 | `public double getQeTargetShare()` |  |
| 2329 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 2331 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 2342 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 2343 | 1 | `public double getHouseholdPaperRatio()` |  |
| 2365 | 1 | `public void setBondMarket(BondMarket.State state)` | UP1: a copy - toState() lists the market's own bonds and books. |
| 2366 | 1 | `public BondMarket.State getBondMarket()` |  |
| 2367 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 2368 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 2369 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 2370 | 1 | `public double getHouseholdBondRatio()` |  |
| 2371 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 2372 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 2383 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 2387 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 2388 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 2412 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 2413 | 1 | `public double getCarsPerHousehold()` |  |
| 2426 | 1 | `public void setTransitBill(double v)` |  |
| 2427 | 1 | `public double getTransitBill()` |  |
| 2439 | 1 | `public void setCaptiveShare(double v)` |  |
| 2440 | 1 | `public double getCaptiveShare()` |  |
| 2441 | 1 | `public void setFuelPerJourney(double v)` |  |
| 2442 | 1 | `public double getFuelPerJourney()` |  |
| 2453 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 2454 | 1 | `public double[] getHouseholdFuel()` |  |
| 2463 | 1 | `public void setHomeFuelSale(double[] v)` |  |
| 2464 | 1 | `public double[] getHomeFuelSale()` |  |
| 2476 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 2477 | 1 | `public double[] getHouseholdGraduates()` |  |
| 2493 | 1 | `public void setEstateCustody(EstateCustody.State state)` |  |
| 2494 | 1 | `public EstateCustody.State getEstateCustody()` |  |
| 2495 | 1 | `public void setUnbackedCustodyPaper(double face)` |  |
| 2496 | 1 | `public double getUnbackedCustodyPaper()` |  |
| 2497 | 1 | `public void setSecuredHouseholdTaxes(double[] state)` |  |
| 2498 | 1 | `public double[] getSecuredHouseholdTaxes()` |  |
| 2499 | 1 | `public void setSecuredForeignTaxes(double[] state)` |  |
| 2500 | 1 | `public double[] getSecuredForeignTaxes()` |  |
| 2501 | 1 | `public void setHouseholdInvestmentTaxes(double[] state)` |  |
| 2502 | 1 | `public double[] getHouseholdInvestmentTaxes()` |  |
| 2505 | 1 | `public void setHouseholdFlows(double[] v)` |  |
| 2506 | 1 | `public double[] getHouseholdFlows()` |  |
| 2508 | 1 | `public void setRememberedCommute(double v)` |  |
| 2509 | 3 | `public double getRememberedCommute()` |  |
| 2522 | 1 | `public void setCostOfLiving(double v)` |  |
| 2523 | 1 | `public double getCostOfLiving()` |  |
| 2536 | 1 | `public void setBankTaxCharged(double v)` |  |
| 2537 | 1 | `public double getBankTaxCharged()` |  |
| 2541 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 2542 | 1 | `public double getRentWeight()` |  |
| 2562 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 2563 | 1 | `public double getRentWeightStudio()` |  |
| 2565 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 2569 | 1 | `public int getConstructionShedMonth()` |  |
| 2570 | 1 | `public double getConstructionShedPoints()` |  |
| 2572 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 2575 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 2755 | 1 | `public void setLicences(double[] a)` | UP1: a copy, not the live head counts - PopulationManager hands out its own arrays. |
| 2756 | 1 | `public double[] getLicences()` |  |
| 2757 | 1 | `public void setEducation(double[] a)` |  |
| 2758 | 1 | `public double[] getEducation()` |  |
| 2760 | 1 | `public void setLabour(double[] a)` |  |
| 2761 | 1 | `public double[] getLabour()` |  |
| 2762 | 1 | `public void setWantedPosts(double[] a)` |  |
| 2763 | 1 | `public double[] getWantedPosts()` |  |
| 2765 | 1 | `public void setSkilledWorkforce(double[] a)` | UP1: a copy, not the live head counts. |
| 2766 | 1 | `public double[] getSkilledWorkforce()` |  |
| 2768 | 1 | `public void setCohorts(double[] a)` |  |
| 2769 | 1 | `public double[] getCohorts()` |  |
| 2772 | 1 | `public void setBandNames(String[] names)` |  |
| 2774 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 2788 | 1 | `public void setShapeNames(String[] names)` |  |
| 2789 | 1 | `public String[] getShapeNames()` |  |
| 2790 | 1 | `public void setFamilies(double[] a)` |  |
| 2791 | 1 | `public double[] getFamilies()` |  |
| 2792 | 1 | `public void setMigration(double[] a)` |  |
| 2793 | 1 | `public double[] getMigration()` |  |
| 2794 | 1 | `public void setUnemployment(double[] a)` |  |
| 2795 | 1 | `public double[] getUnemployment()` |  |
| 2796 | 1 | `public void setSickness(double[] a)` |  |
| 2797 | 1 | `public double[] getSickness()` |  |
| 2798 | 1 | `public void setSicknessAcute(double[] a)` |  |
| 2799 | 1 | `public double[] getSicknessAcute()` |  |
| 2800 | 1 | `public void setCrime(double[] a)` |  |
| 2801 | 1 | `public double[] getCrime()` |  |
| 2802 | 1 | `public void setHealth(double[] a)` |  |
| 2803 | 1 | `public double[] getHealth()` |  |
| 2804 | 1 | `public void setCausesOfDeath(double[] a)` |  |
| 2805 | 1 | `public double[] getCausesOfDeath()` |  |
| 2806 | 1 | `public void setHealthcare(double[] a)` |  |
| 2807 | 1 | `public double[] getHealthcare()` |  |
| 2808 | 1 | `public void setHealthcareAccess(double[] a)` |  |
| 2809 | 1 | `public double[] getHealthcareAccess()` |  |
| 2839 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 2840 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 2841 | 1 | `public void setSectorOperatingLossMonths(java.util.Map<String, Integer> m)` |  |
| 2842 | 1 | `public java.util.Map<String, Integer> getSectorOperatingLossMonths()` |  |
| 2843 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 2844 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 2855 | 1 | `public void setBuildersWork(java.util.List<Double> l)` |  |
| 2856 | 1 | `public java.util.List<Double> getBuildersWork()` |  |
| 2857 | 1 | `public void setBuildersOwedAfterAdvance(Double v)` |  |
| 2858 | 1 | `public Double getBuildersOwedAfterAdvance()` |  |
| 2859 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 2860 | 1 | `public double getCityCapitalSpending()` |  |
| 2878 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 2879 | 1 | `public double getCityMaintenancePaid()` |  |
| 2895 | 1 | `public void setLastInvestment(java.util.Map<String, String> m)` |  |
| 2896 | 1 | `public java.util.Map<String, String> getLastInvestment()` |  |
| 2897 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 2898 | 1 | `public double getMonthlyMaterialImports()` |  |
| 2899 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 2900 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 2901 | 1 | `public void setMaterialsConsumed(long v)` |  |
| 2902 | 1 | `public long getMaterialsConsumed()` |  |
| 2911 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 2912 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 2922 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 2923 | 1 | `public double[] getHouseholdStatement()` |  |
| 2925 | 4 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 2929 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 2931 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 2934 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 2936 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 2939 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 2941 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 2944 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 2946 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 2949 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 2951 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 2952 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 2954 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 2955 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 2957 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 2958 | 1 | `public double getInsurancePremiums()` |  |
| 2960 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 2961 | 1 | `public double[] getNationalAccounts()` |  |
| 2963 | 1 | `public void setGdpRolling(List<Double> months)` |  |
| 2964 | 1 | `public List<Double> getGdpRolling()` |  |
| 2974 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 2977 | 3 | `public int getUnderConstruction(int index)` |  |
| 2980 | 3 | `public double getCash()` |  |
| 2984 | 3 | `public int getMonth()` |  |
| 2988 | 3 | `public long getBuildingQuantity(int index)` |  |
| 2992 | 3 | `public int getBuildingsLength()` |  |
| 2996 | 3 | `public JsonArray getDebt()` |  |
| 3001 | 3 | `public void setBusinessDebt(List<BusinessDebt> loans)` | All instrument fields, including BF1 retirementClaim, go out and back through the loan JSON. |
| 3005 | 3 | `public JsonArray getBusinessDebt()` |  |
| 3009 | 3 | `public int getProgressLength()` |  |
| 3013 | 3 | `public double getProgress(int index)` |  |
| 3017 | 3 | `public long getConstructionMaterials()` |  |
| 3021 | 3 | `public long getPopulation()` |  |
| 3024 | 3 | `public boolean getReports()` |  |
| 3027 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 3030 | 3 | `public boolean getGraphs()` |  |
| 3034 | 3 | `public String getRecordingId()` | The recording's id, or null in a city never recorded and in a save from before TR1. |
| 3038 | 3 | `public boolean isRecording()` | Whether the city was recording when it was saved; false in a save from before TR1. |


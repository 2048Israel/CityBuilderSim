# DataSave.java - 2,021 lines · 325 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (6), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [DebtManager](DebtManager.md) (2), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (4):** [Game](Game.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 179 | · land and ore |
| 240 | · the shedding warning |
| 387 | · the header |
| 695 | · construction, by id |
| 771 | · charged, not derived |
| 842 | · policy |

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
| 138 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 153 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 164 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 165 | `private java.util.List<Markets.State> markets` |  |
| 177 | `private long workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 209 | `private Double worldSeaTheta` |  |
| 210 | `private double[] worldTotals` |  |
| 211 | `private double[] landCentre` |  |
| 212 | `private double[][] landCentreRects` |  |
| 213 | `private double[][] landHoldings` |  |
| 214 | `private double[][] landPartFields` |  |
| 215 | `private double[][] landConverted` |  |
| 216 | `private double[] landLanes` |  |
| 217 | `private double[][] landPurchases` |  |
| 218 | `private double[][] landOffers` |  |
| 219 | `private Integer nextOfferId` |  |
| 220 | `private double[] depletion` |  |
| 221 | `private double[] landListing` |  |
| 223 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 224 | `private Integer ironDeposits` |  |
| 225 | `private Double ironReserveTonnes` |  |
| 232 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 238 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 256 | `private int constructionShedMonth` |  |
| 257 | `private double constructionShedPoints` |  |
| 264 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 274 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 281 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 282 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 290 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 291 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 299 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 311 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 312 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 313 | `private double insurancePremiums` |  |
| 316 | `private int constructionMaterials` | The city's own yard, in units. |
| 317 | `private long population` |  |
| 328 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 329 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 348 | `private boolean reports` | settings |
| 349 | `private boolean graphs` |  |
| 360 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 370 | `private double landOwned` | Land. |
| 371 | `private int landBlocksPurchased` |  |
| 372 | `private double landPricePerSqFt` |  |
| 381 | `private double incomeTaxRate` | Tax rates. |
| 382 | `private double propertyTaxRate` |  |
| 445 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 463 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 464 | `private double[] treasuryJournalAmounts` |  |
| 465 | `private String[] treasuryJournalPendingLabels` |  |
| 466 | `private double[] treasuryJournalPendingAmounts` |  |
| 474 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 485 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 486 | `private double cityDiscountUnsettled` |  |
| 518 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 706 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 852 | `private double[] taxPolicyState` |  |
| 865 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 877 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 878 | `private double[] householdCells` |  |
| 890 | `private double bankCash` | The bank's cash. |
| 900 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 903 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 904 | `private SalesTaxLedger.State salesTax` |  |
| 906 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 940 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 952 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 958 | `private double bankDepositRate` | What savers were paid. |
| 974 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 987 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 998 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 999 | `private double[] treasuryArrearsAmounts` |  |
| 1016 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 1028 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1039 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1050 | `private String[] equityKeys` | The share register, company by company, named. |
| 1051 | `private double[] equity` |  |
| 1058 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1063 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1081 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1100 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1117 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1129 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1141 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1156 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1168 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1179 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1191 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1204 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1216 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1217 | `private Double bankRetainedOpening` |  |
| 1230 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1231 | `private double[] bankPreferredRecord` |  |
| 1247 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1253 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1265 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1279 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1292 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1308 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1323 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1338 | `private Double policyRate` | The policy rate. |
| 1349 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1361 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1362 | `private double[] rolloverLedger` |  |
| 1363 | `private double[] rolloverRecord` |  |
| 1383 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1396 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1408 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1421 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1435 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1448 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1467 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1468 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1469 | `private double householdBondRatio` |  |
| 1470 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1487 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1488 | `private double buybackAbroadUnsettled` |  |
| 1511 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1517 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1531 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 1544 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 1558 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 1571 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 1588 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1602 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1628 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1661 | `private double[] cohorts` | The demographics. |
| 1685 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1687 | `private double[] families` |  |
| 1688 | `private double[] migration` |  |
| 1698 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1707 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1717 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1730 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1743 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1759 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1771 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1788 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1789 | `private double[] education` |  |
| 1818 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1851 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1852 | `private java.util.List<Long> populationTrend` |  |
| 1853 | `private double cityCapitalSpending` |  |
| 1854 | `private double monthlyMaterialImports` |  |
| 1861 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1862 | `private int materialsConsumed` |  |
| 1885 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1902 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1913 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 2010 | **type** `public class DataSave` |  |
| 167 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 168 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 169 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 170 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 179-239)

### the shedding warning (lines 240-386)

| line | len | member | says |
|---:|---:|---|---|
| 331 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 335 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 339 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 343 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 387-694)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 1 | `public void setSlotName(String name)` |  |
| 392 | 12 | `public void setFounding(Founding f)` | The founding record, whole. |
| 415 | 16 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 433 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 436 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 447 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 448 | 1 | `public double[] getTreasuryMonth()` |  |
| 488 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 492 | 1 | `public double getCityPaperUnsettled()` |  |
| 493 | 1 | `public double getCityDiscountUnsettled()` |  |
| 495 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 502 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 503 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 504 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 505 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 506 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 507 | 1 | `public double getTreasuryRaisedPending()` |  |
| 520 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 521 | 1 | `public double[] getGovernmentMonth()` |  |
| 522 | 1 | `public String getSlotName()` |  |
| 525 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 531 | 1 | `public String getGameVersion()` |  |
| 532 | 1 | `public int getSaveFormat()` |  |
| 533 | 1 | `public long getSavedAt()` |  |
| 535 | 1 | `public void setHouseholdSavings(double value)` |  |
| 536 | 1 | `public double getHouseholdSavings()` |  |
| 538 | 1 | `public void setLandOwned(double sqFt)` |  |
| 539 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 540 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 542 | 1 | `public double getLandOwned()` |  |
| 543 | 1 | `public int getLandBlocksPurchased()` |  |
| 544 | 1 | `public double getLandPricePerSqFt()` |  |
| 546 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 547 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 549 | 1 | `public double getIncomeTaxRate()` |  |
| 550 | 1 | `public double getPropertyTaxRate()` |  |
| 552 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 557 | 3 | `public void setCash(double money)` | setters |
| 561 | 3 | `public void setMonth(int month)` |  |
| 565 | 3 | `public void setBuildingNum(int i)` |  |
| 569 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 576 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 581 | 3 | `public void setProgress(double[] progress)` |  |
| 585 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 589 | 3 | `public void setPopulation(long population)` |  |
| 592 | 3 | `public void setReports(boolean reports)` |  |
| 605 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 608 | 3 | `public void setGraphs(boolean graphs)` |  |
| 637 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 662 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 688 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 695-770)

| line | len | member | says |
|---:|---:|---|---|
| 708 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 709 | 1 | `public int[] getStackOrder()` |  |
| 711 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 719 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 721 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 723 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 725 | 1 | `public void setConstructionControl(ConstructionControl.State state)` |  |
| 727 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 729 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 731 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 734 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 738 | 3 | `public int getConstructionByIdLength()` |  |
| 742 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 749 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 756 | 6 | `public double getContractValueById(int templateId)` |  |
| 764 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 771-841)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 774 | 1 | `public double getPropertyTaxCharged()` |  |
| 776 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 777 | 1 | `public double getCityInterestAccrued()` |  |
| 779 | 1 | `public void setWorkforce(long workforce)` |  |
| 782 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 785 | 16 | `public void setCityLand(double[] centre, double[][] centreRects, double[][] holdings, double[][] partFields, double[][] convert...` | The city's land on the world, whole (0.7.57; on the block grid since 0.7.67, format 32): see land and ore above. |
| 803 | 1 | `public double[] getLandCentre()` | The centre's record: 24 wide in format 32, 25 (with a half-side) in format 31. |
| 805 | 1 | `public double[][] getLandCentreRects()` | The centre's blocks (format 32). |
| 807 | 1 | `public double[][] getLandHoldings()` | The purchases since, 31 wide (format 32). |
| 809 | 1 | `public double[][] getLandPartFields()` | The fields held in part (format 32), or null. |
| 811 | 1 | `public double[][] getLandConverted()` | A converted city's purchase history (format 32), or null. |
| 813 | 1 | `public double[] getLandLanes()` | A format-31 save's lanes: read only to convert. |
| 815 | 1 | `public double[][] getLandPurchases()` | A format-31 save's purchases, 28 wide: read only to convert. |
| 816 | 1 | `public double[][] getLandOffers()` |  |
| 818 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 819 | 1 | `public double[] getDepletion()` |  |
| 820 | 1 | `public double[] getWorldTotals()` |  |
| 822 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 824 | 1 | `public void setFreshRights(double units)` |  |
| 826 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 828 | 1 | `public void setMapStamp(Long stamp)` |  |
| 830 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 833 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 834 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 835 | 1 | `public double[] getLandMarketPrices()` |  |
| 837 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 839 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 842-2021)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 909 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 910 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 911 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 912 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 913 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 915 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 916 | 1 | `public double[] getTaxPolicyState()` |  |
| 918 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 919 | 1 | `public double[] getHouseholdBalance()` |  |
| 921 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 925 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 926 | 1 | `public double[] getHouseholdCells()` |  |
| 928 | 1 | `public void setBankCash(double cash)` |  |
| 929 | 1 | `public double getBankCash()` |  |
| 942 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 943 | 1 | `public double getBankBranchesCapitalised()` |  |
| 954 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 955 | 1 | `public double getBankProfitLastMonth()` |  |
| 959 | 1 | `public void setBankDepositRate(double v)` |  |
| 960 | 1 | `public double getBankDepositRate()` |  |
| 976 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 977 | 1 | `public double[] getForeignAccounts()` |  |
| 989 | 1 | `public void setCentralBank(double[] state)` |  |
| 990 | 1 | `public double[] getCentralBank()` |  |
| 1001 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 1005 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 1006 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 1018 | 1 | `public void setForeignStanding(double[] state)` |  |
| 1019 | 1 | `public double[] getForeignStanding()` |  |
| 1030 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1031 | 1 | `public double[] getCapitalFlows()` |  |
| 1041 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1042 | 1 | `public double[] getOutwardInvestment()` |  |
| 1053 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1054 | 1 | `public String[] getEquityKeys()` |  |
| 1055 | 1 | `public double[] getEquity()` |  |
| 1059 | 1 | `public void setExchange(double[] state)` |  |
| 1060 | 1 | `public double[] getExchange()` |  |
| 1064 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 1065 | 1 | `public Exchange.State getExchangeState()` |  |
| 1083 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1084 | 1 | `public double[] getBankSolvency()` |  |
| 1102 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1103 | 1 | `public double[] getHousingOccupancy()` |  |
| 1119 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1120 | 1 | `public double[] getBankLastMonth()` |  |
| 1131 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1132 | 1 | `public double[] getBankPricingHistory()` |  |
| 1143 | 1 | `public void setBankLateProfit(double value)` |  |
| 1144 | 1 | `public double getBankLateProfit()` |  |
| 1158 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1159 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1170 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1171 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1181 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1182 | 1 | `public double[] getBankMonthLines()` |  |
| 1193 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1194 | 1 | `public double[] getBankStatementYear()` |  |
| 1206 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1207 | 1 | `public double[] getBankSheetYear()` |  |
| 1219 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1220 | 1 | `public Double getBankPaidInOpening()` |  |
| 1221 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1222 | 1 | `public Double getBankRetainedOpening()` |  |
| 1233 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1237 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1238 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1249 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1250 | 1 | `public TreasuryFund.State getFund()` |  |
| 1255 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1256 | 1 | `public double[] getDebtMarket()` |  |
| 1267 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1268 | 1 | `public double[] getPriceIndex()` |  |
| 1281 | 1 | `public void setExpectations(double[] state)` |  |
| 1282 | 1 | `public double[] getExpectations()` |  |
| 1294 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1295 | 1 | `public double[] getWorldEconomy()` |  |
| 1310 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1311 | 1 | `public double getTradedExchangeRate()` |  |
| 1325 | 1 | `public void setDenomination(double[] state)` |  |
| 1326 | 1 | `public double[] getDenomination()` |  |
| 1340 | 1 | `public void setPolicyRate(double rate)` |  |
| 1342 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1351 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1352 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1365 | 1 | `public void setRolloverMode(String mode)` |  |
| 1367 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1372 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1373 | 1 | `public double[] getRolloverLedger()` |  |
| 1374 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1375 | 1 | `public double[] getRolloverRecord()` |  |
| 1385 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1386 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1398 | 1 | `public void setInflationTarget(double target)` |  |
| 1400 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1410 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1412 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 1423 | 1 | `public void setQeTargetShare(double share)` |  |
| 1424 | 1 | `public double getQeTargetShare()` |  |
| 1437 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1439 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1450 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1451 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1472 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1473 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1474 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1475 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1476 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1477 | 1 | `public double getHouseholdBondRatio()` |  |
| 1478 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1479 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1490 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1494 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1495 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1519 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1520 | 1 | `public double getCarsPerHousehold()` |  |
| 1533 | 1 | `public void setTransitBill(double v)` |  |
| 1534 | 1 | `public double getTransitBill()` |  |
| 1546 | 1 | `public void setCaptiveShare(double v)` |  |
| 1547 | 1 | `public double getCaptiveShare()` |  |
| 1548 | 1 | `public void setFuelPerJourney(double v)` |  |
| 1549 | 1 | `public double getFuelPerJourney()` |  |
| 1560 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 1561 | 1 | `public double[] getHouseholdFuel()` |  |
| 1573 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 1574 | 1 | `public double[] getHouseholdGraduates()` |  |
| 1576 | 1 | `public void setRememberedCommute(double v)` |  |
| 1577 | 3 | `public double getRememberedCommute()` |  |
| 1590 | 1 | `public void setCostOfLiving(double v)` |  |
| 1591 | 1 | `public double getCostOfLiving()` |  |
| 1604 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1605 | 1 | `public double getBankTaxCharged()` |  |
| 1609 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1610 | 1 | `public double getRentWeight()` |  |
| 1630 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1631 | 1 | `public double getRentWeightStudio()` |  |
| 1633 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1637 | 1 | `public int getConstructionShedMonth()` |  |
| 1638 | 1 | `public double getConstructionShedPoints()` |  |
| 1640 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1643 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1791 | 1 | `public void setLicences(double[] a)` |  |
| 1792 | 1 | `public double[] getLicences()` |  |
| 1793 | 1 | `public void setEducation(double[] a)` |  |
| 1794 | 1 | `public double[] getEducation()` |  |
| 1796 | 1 | `public void setLabour(double[] a)` |  |
| 1797 | 1 | `public double[] getLabour()` |  |
| 1798 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1799 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1801 | 1 | `public void setCohorts(double[] a)` |  |
| 1802 | 1 | `public double[] getCohorts()` |  |
| 1805 | 1 | `public void setBandNames(String[] names)` |  |
| 1807 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1821 | 1 | `public void setShapeNames(String[] names)` |  |
| 1822 | 1 | `public String[] getShapeNames()` |  |
| 1823 | 1 | `public void setFamilies(double[] a)` |  |
| 1824 | 1 | `public double[] getFamilies()` |  |
| 1825 | 1 | `public void setMigration(double[] a)` |  |
| 1826 | 1 | `public double[] getMigration()` |  |
| 1827 | 1 | `public void setUnemployment(double[] a)` |  |
| 1828 | 1 | `public double[] getUnemployment()` |  |
| 1829 | 1 | `public void setSickness(double[] a)` |  |
| 1830 | 1 | `public double[] getSickness()` |  |
| 1831 | 1 | `public void setCrime(double[] a)` |  |
| 1832 | 1 | `public double[] getCrime()` |  |
| 1833 | 1 | `public void setHealth(double[] a)` |  |
| 1834 | 1 | `public double[] getHealth()` |  |
| 1835 | 1 | `public void setHealthcare(double[] a)` |  |
| 1836 | 1 | `public double[] getHealthcare()` |  |
| 1864 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1865 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1866 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 1867 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 1868 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1869 | 1 | `public double getCityCapitalSpending()` |  |
| 1887 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1888 | 1 | `public double getCityMaintenancePaid()` |  |
| 1889 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1890 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1891 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1892 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1893 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1894 | 1 | `public int getMaterialsConsumed()` |  |
| 1903 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1904 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1914 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1915 | 1 | `public double[] getHouseholdStatement()` |  |
| 1917 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1920 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1922 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1925 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1927 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1930 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1932 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1935 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1937 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1940 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1942 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 1943 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 1945 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 1946 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 1948 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 1949 | 1 | `public double getInsurancePremiums()` |  |
| 1951 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1952 | 1 | `public double[] getNationalAccounts()` |  |
| 1962 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1965 | 3 | `public int getUnderConstruction(int index)` |  |
| 1968 | 3 | `public double getCash()` |  |
| 1972 | 3 | `public int getMonth()` |  |
| 1976 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1980 | 3 | `public int getBuildingsLength()` |  |
| 1984 | 3 | `public JsonArray getDebt()` |  |
| 1988 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1993 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1997 | 3 | `public int getProgressLength()` |  |
| 2001 | 3 | `public double getProgress(int index)` |  |
| 2005 | 3 | `public int getConstructionMaterials()` |  |
| 2009 | 3 | `public long getPopulation()` |  |
| 2012 | 3 | `public boolean getReports()` |  |
| 2015 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 2018 | 3 | `public boolean getGraphs()` |  |


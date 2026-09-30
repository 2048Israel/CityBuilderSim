# DataSave.java - 1,784 lines · 288 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (5), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 152 | · land and ore |
| 166 | · the shedding warning |
| 313 | · the header |
| 612 | · construction, by id |
| 666 | · charged, not derived |
| 692 | · policy |

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
| 53 | `private double cash` | save variables |
| 54 | `private int[] buildings` |  |
| 55 | `private int month` |  |
| 56 | `private JsonArray debts` |  |
| 64 | `private JsonArray businessDebts` | Private-sector loans. |
| 71 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 72 | `private int[] underConstruction` |  |
| 83 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 84 | `private int[] underConstructionById` |  |
| 86 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 88 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 90 | `private java.util.List<BuildingManager.ContractRecord> contractRecords` | ...and who placed each part of it, with its material still to draw and the allowance priced in for it (0.7.19). |
| 111 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 126 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 137 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 138 | `private java.util.List<Markets.State> markets` |  |
| 150 | `private int workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 160 | `private double[] landListing` |  |
| 162 | `private double[] landMarketPrices` | The office's struck prices and minimum lot - see LandMarket.getPriceState(). |
| 163 | `private int ironDeposits` |  |
| 164 | `private double ironReserveTonnes` |  |
| 182 | `private int constructionShedMonth` |  |
| 183 | `private double constructionShedPoints` |  |
| 190 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 200 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 207 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 208 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 216 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 217 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 225 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 237 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 238 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 239 | `private double insurancePremiums` |  |
| 242 | `private int constructionMaterials` | The city's own yard, in units. |
| 243 | `private int population` |  |
| 254 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 255 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 274 | `private boolean reports` | settings |
| 275 | `private boolean graphs` |  |
| 286 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 296 | `private double landOwned` | Land. |
| 297 | `private int landBlocksPurchased` |  |
| 298 | `private double landPricePerSqFt` |  |
| 307 | `private double incomeTaxRate` | Tax rates. |
| 308 | `private double propertyTaxRate` |  |
| 362 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 380 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 381 | `private double[] treasuryJournalAmounts` |  |
| 382 | `private String[] treasuryJournalPendingLabels` |  |
| 383 | `private double[] treasuryJournalPendingAmounts` |  |
| 391 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 402 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 403 | `private double cityDiscountUnsettled` |  |
| 435 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 702 | `private double[] taxPolicyState` |  |
| 712 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 724 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 725 | `private double[] householdCells` |  |
| 737 | `private double bankCash` | The bank's cash. |
| 747 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 750 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 751 | `private SalesTaxLedger.State salesTax` |  |
| 753 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 787 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 799 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 805 | `private double bankDepositRate` | What savers were paid. |
| 821 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 834 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 845 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 846 | `private double[] treasuryArrearsAmounts` |  |
| 863 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 875 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 886 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 897 | `private String[] equityKeys` | The share register, company by company, named. |
| 898 | `private double[] equity` |  |
| 905 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 910 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 928 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 947 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 964 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 976 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 988 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1003 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1015 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1026 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1038 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1051 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1063 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1064 | `private Double bankRetainedOpening` |  |
| 1077 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1078 | `private double[] bankPreferredRecord` |  |
| 1094 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1100 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1112 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1125 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1141 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1156 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1171 | `private Double policyRate` | The policy rate. |
| 1182 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1194 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1195 | `private double[] rolloverLedger` |  |
| 1196 | `private double[] rolloverRecord` |  |
| 1216 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1229 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1242 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1256 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1269 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1288 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1289 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1290 | `private double householdBondRatio` |  |
| 1291 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1308 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1309 | `private double buybackAbroadUnsettled` |  |
| 1332 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1338 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1355 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1369 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1395 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1424 | `private double[] cohorts` | The demographics. |
| 1448 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1450 | `private double[] families` |  |
| 1451 | `private double[] migration` |  |
| 1461 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1470 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1480 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1493 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1506 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1522 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1534 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1551 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1552 | `private double[] education` |  |
| 1581 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1614 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1615 | `private java.util.List<Integer> populationTrend` |  |
| 1616 | `private double cityCapitalSpending` |  |
| 1617 | `private double monthlyMaterialImports` |  |
| 1624 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1625 | `private int materialsConsumed` |  |
| 1648 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1665 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1676 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1773 | **type** `public class DataSave` |  |
| 140 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 141 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 142 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 143 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 152-165)

### the shedding warning (lines 166-312)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 261 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 265 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 269 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 313-611)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 1 | `public void setSlotName(String name)` |  |
| 318 | 11 | `public void setFounding(Founding f)` | The founding record, whole. |
| 337 | 14 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 353 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 364 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 365 | 1 | `public double[] getTreasuryMonth()` |  |
| 405 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 409 | 1 | `public double getCityPaperUnsettled()` |  |
| 410 | 1 | `public double getCityDiscountUnsettled()` |  |
| 412 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 419 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 420 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 421 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 422 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 423 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 424 | 1 | `public double getTreasuryRaisedPending()` |  |
| 437 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 438 | 1 | `public double[] getGovernmentMonth()` |  |
| 439 | 1 | `public String getSlotName()` |  |
| 442 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 448 | 1 | `public String getGameVersion()` |  |
| 449 | 1 | `public int getSaveFormat()` |  |
| 450 | 1 | `public long getSavedAt()` |  |
| 452 | 1 | `public void setHouseholdSavings(double value)` |  |
| 453 | 1 | `public double getHouseholdSavings()` |  |
| 455 | 1 | `public void setLandOwned(double sqFt)` |  |
| 456 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 457 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 459 | 1 | `public double getLandOwned()` |  |
| 460 | 1 | `public int getLandBlocksPurchased()` |  |
| 461 | 1 | `public double getLandPricePerSqFt()` |  |
| 463 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 464 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 466 | 1 | `public double getIncomeTaxRate()` |  |
| 467 | 1 | `public double getPropertyTaxRate()` |  |
| 469 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 474 | 3 | `public void setCash(double money)` | setters |
| 478 | 3 | `public void setMonth(int month)` |  |
| 482 | 3 | `public void setBuildingNum(int i)` |  |
| 486 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 493 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 498 | 3 | `public void setProgress(double[] progress)` |  |
| 502 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 506 | 3 | `public void setPopulation(int population)` |  |
| 509 | 3 | `public void setReports(boolean reports)` |  |
| 522 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 525 | 3 | `public void setGraphs(boolean graphs)` |  |
| 554 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 579 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 605 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 612-665)

| line | len | member | says |
|---:|---:|---|---|
| 614 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 622 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 624 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 626 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 629 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 633 | 3 | `public int getConstructionByIdLength()` |  |
| 637 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 644 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 651 | 6 | `public double getContractValueById(int templateId)` |  |
| 659 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 666-691)

| line | len | member | says |
|---:|---:|---|---|
| 668 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 669 | 1 | `public double getPropertyTaxCharged()` |  |
| 671 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 672 | 1 | `public double getCityInterestAccrued()` |  |
| 674 | 1 | `public void setWorkforce(int workforce)` |  |
| 677 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 679 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 685 | 1 | `public double[] getLandListing()` |  |
| 686 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 687 | 1 | `public double[] getLandMarketPrices()` |  |
| 688 | 1 | `public int getIronDeposits()` |  |
| 689 | 1 | `public double getIronReserveTonnes()` |  |

### policy (lines 692-1784)

| line | len | member | says |
|---:|---:|---|---|
| 755 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 756 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 757 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 758 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 759 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 760 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 762 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 763 | 1 | `public double[] getTaxPolicyState()` |  |
| 765 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 766 | 1 | `public double[] getHouseholdBalance()` |  |
| 768 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 772 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 773 | 1 | `public double[] getHouseholdCells()` |  |
| 775 | 1 | `public void setBankCash(double cash)` |  |
| 776 | 1 | `public double getBankCash()` |  |
| 789 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 790 | 1 | `public double getBankBranchesCapitalised()` |  |
| 801 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 802 | 1 | `public double getBankProfitLastMonth()` |  |
| 806 | 1 | `public void setBankDepositRate(double v)` |  |
| 807 | 1 | `public double getBankDepositRate()` |  |
| 823 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 824 | 1 | `public double[] getForeignAccounts()` |  |
| 836 | 1 | `public void setCentralBank(double[] state)` |  |
| 837 | 1 | `public double[] getCentralBank()` |  |
| 848 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 852 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 853 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 865 | 1 | `public void setForeignStanding(double[] state)` |  |
| 866 | 1 | `public double[] getForeignStanding()` |  |
| 877 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 878 | 1 | `public double[] getCapitalFlows()` |  |
| 888 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 889 | 1 | `public double[] getOutwardInvestment()` |  |
| 900 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 901 | 1 | `public String[] getEquityKeys()` |  |
| 902 | 1 | `public double[] getEquity()` |  |
| 906 | 1 | `public void setExchange(double[] state)` |  |
| 907 | 1 | `public double[] getExchange()` |  |
| 911 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 912 | 1 | `public Exchange.State getExchangeState()` |  |
| 930 | 1 | `public void setBankSolvency(double[] state)` |  |
| 931 | 1 | `public double[] getBankSolvency()` |  |
| 949 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 950 | 1 | `public double[] getHousingOccupancy()` |  |
| 966 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 967 | 1 | `public double[] getBankLastMonth()` |  |
| 978 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 979 | 1 | `public double[] getBankPricingHistory()` |  |
| 990 | 1 | `public void setBankLateProfit(double value)` |  |
| 991 | 1 | `public double getBankLateProfit()` |  |
| 1005 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1006 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1017 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1018 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1028 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1029 | 1 | `public double[] getBankMonthLines()` |  |
| 1040 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1041 | 1 | `public double[] getBankStatementYear()` |  |
| 1053 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1054 | 1 | `public double[] getBankSheetYear()` |  |
| 1066 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1067 | 1 | `public Double getBankPaidInOpening()` |  |
| 1068 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1069 | 1 | `public Double getBankRetainedOpening()` |  |
| 1080 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1084 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1085 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1096 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1097 | 1 | `public TreasuryFund.State getFund()` |  |
| 1102 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1103 | 1 | `public double[] getDebtMarket()` |  |
| 1114 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1115 | 1 | `public double[] getPriceIndex()` |  |
| 1127 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1128 | 1 | `public double[] getWorldEconomy()` |  |
| 1143 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1144 | 1 | `public double getTradedExchangeRate()` |  |
| 1158 | 1 | `public void setDenomination(double[] state)` |  |
| 1159 | 1 | `public double[] getDenomination()` |  |
| 1173 | 1 | `public void setPolicyRate(double rate)` |  |
| 1175 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1184 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1185 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1198 | 1 | `public void setRolloverMode(String mode)` |  |
| 1200 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1205 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1206 | 1 | `public double[] getRolloverLedger()` |  |
| 1207 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1208 | 1 | `public double[] getRolloverRecord()` |  |
| 1218 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1219 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1231 | 1 | `public void setInflationTarget(double target)` |  |
| 1233 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1244 | 1 | `public void setQeTargetShare(double share)` |  |
| 1245 | 1 | `public double getQeTargetShare()` |  |
| 1258 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1260 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1271 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1272 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1293 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1294 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1295 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1296 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1297 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1298 | 1 | `public double getHouseholdBondRatio()` |  |
| 1299 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1300 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1311 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1315 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1316 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1340 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1341 | 1 | `public double getCarsPerHousehold()` |  |
| 1343 | 1 | `public void setRememberedCommute(double v)` |  |
| 1344 | 3 | `public double getRememberedCommute()` |  |
| 1357 | 1 | `public void setCostOfLiving(double v)` |  |
| 1358 | 1 | `public double getCostOfLiving()` |  |
| 1371 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1372 | 1 | `public double getBankTaxCharged()` |  |
| 1376 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1377 | 1 | `public double getRentWeight()` |  |
| 1397 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1398 | 1 | `public double getRentWeightStudio()` |  |
| 1400 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1404 | 1 | `public int getConstructionShedMonth()` |  |
| 1405 | 1 | `public double getConstructionShedPoints()` |  |
| 1407 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1410 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1554 | 1 | `public void setLicences(double[] a)` |  |
| 1555 | 1 | `public double[] getLicences()` |  |
| 1556 | 1 | `public void setEducation(double[] a)` |  |
| 1557 | 1 | `public double[] getEducation()` |  |
| 1559 | 1 | `public void setLabour(double[] a)` |  |
| 1560 | 1 | `public double[] getLabour()` |  |
| 1561 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1562 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1564 | 1 | `public void setCohorts(double[] a)` |  |
| 1565 | 1 | `public double[] getCohorts()` |  |
| 1568 | 1 | `public void setBandNames(String[] names)` |  |
| 1570 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1584 | 1 | `public void setShapeNames(String[] names)` |  |
| 1585 | 1 | `public String[] getShapeNames()` |  |
| 1586 | 1 | `public void setFamilies(double[] a)` |  |
| 1587 | 1 | `public double[] getFamilies()` |  |
| 1588 | 1 | `public void setMigration(double[] a)` |  |
| 1589 | 1 | `public double[] getMigration()` |  |
| 1590 | 1 | `public void setUnemployment(double[] a)` |  |
| 1591 | 1 | `public double[] getUnemployment()` |  |
| 1592 | 1 | `public void setSickness(double[] a)` |  |
| 1593 | 1 | `public double[] getSickness()` |  |
| 1594 | 1 | `public void setCrime(double[] a)` |  |
| 1595 | 1 | `public double[] getCrime()` |  |
| 1596 | 1 | `public void setHealth(double[] a)` |  |
| 1597 | 1 | `public double[] getHealth()` |  |
| 1598 | 1 | `public void setHealthcare(double[] a)` |  |
| 1599 | 1 | `public double[] getHealthcare()` |  |
| 1627 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1628 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1629 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1630 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1631 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1632 | 1 | `public double getCityCapitalSpending()` |  |
| 1650 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1651 | 1 | `public double getCityMaintenancePaid()` |  |
| 1652 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1653 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1654 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1655 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1656 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1657 | 1 | `public int getMaterialsConsumed()` |  |
| 1666 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1667 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1677 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1678 | 1 | `public double[] getHouseholdStatement()` |  |
| 1680 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1683 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1685 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1688 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1690 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1693 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1695 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1698 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1700 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1703 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1705 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 1706 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 1708 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 1709 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 1711 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 1712 | 1 | `public double getInsurancePremiums()` |  |
| 1714 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1715 | 1 | `public double[] getNationalAccounts()` |  |
| 1725 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1728 | 3 | `public int getUnderConstruction(int index)` |  |
| 1731 | 3 | `public double getCash()` |  |
| 1735 | 3 | `public int getMonth()` |  |
| 1739 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1743 | 3 | `public int getBuildingsLength()` |  |
| 1747 | 3 | `public JsonArray getDebt()` |  |
| 1751 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1756 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1760 | 3 | `public int getProgressLength()` |  |
| 1764 | 3 | `public double getProgress(int index)` |  |
| 1768 | 3 | `public int getConstructionMaterials()` |  |
| 1772 | 3 | `public int getPopulation()` |  |
| 1775 | 3 | `public boolean getReports()` |  |
| 1778 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1781 | 3 | `public boolean getGraphs()` |  |


# DataSave.java - 1,775 lines · 286 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-28 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (5), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 150 | · land and ore |
| 164 | · the shedding warning |
| 311 | · the header |
| 610 | · construction, by id |
| 660 | · charged, not derived |
| 686 | · policy |

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
| 109 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 124 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 135 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 136 | `private java.util.List<Markets.State> markets` |  |
| 148 | `private int workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 158 | `private double[] landListing` |  |
| 160 | `private double[] landMarketPrices` | The office's struck prices and minimum lot - see LandMarket.getPriceState(). |
| 161 | `private int ironDeposits` |  |
| 162 | `private double ironReserveTonnes` |  |
| 180 | `private int constructionShedMonth` |  |
| 181 | `private double constructionShedPoints` |  |
| 188 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 198 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 205 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 206 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 214 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 215 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 223 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 235 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 236 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 237 | `private double insurancePremiums` |  |
| 240 | `private int constructionMaterials` | The city's own yard, in units. |
| 241 | `private int population` |  |
| 252 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 253 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 272 | `private boolean reports` | settings |
| 273 | `private boolean graphs` |  |
| 284 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 294 | `private double landOwned` | Land. |
| 295 | `private int landBlocksPurchased` |  |
| 296 | `private double landPricePerSqFt` |  |
| 305 | `private double incomeTaxRate` | Tax rates. |
| 306 | `private double propertyTaxRate` |  |
| 360 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 378 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 379 | `private double[] treasuryJournalAmounts` |  |
| 380 | `private String[] treasuryJournalPendingLabels` |  |
| 381 | `private double[] treasuryJournalPendingAmounts` |  |
| 389 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 400 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 401 | `private double cityDiscountUnsettled` |  |
| 433 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 696 | `private double[] taxPolicyState` |  |
| 706 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 718 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 719 | `private double[] householdCells` |  |
| 731 | `private double bankCash` | The bank's cash. |
| 741 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 744 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 745 | `private SalesTaxLedger.State salesTax` |  |
| 747 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 781 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 793 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 799 | `private double bankDepositRate` | What savers were paid. |
| 815 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 828 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 839 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 840 | `private double[] treasuryArrearsAmounts` |  |
| 857 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 869 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 880 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 891 | `private String[] equityKeys` | The share register, company by company, named. |
| 892 | `private double[] equity` |  |
| 899 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 904 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 922 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 941 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 955 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 967 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 979 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 994 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1006 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1017 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1029 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1042 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1054 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1055 | `private Double bankRetainedOpening` |  |
| 1068 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1069 | `private double[] bankPreferredRecord` |  |
| 1085 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1091 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1103 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1116 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1132 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1147 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1162 | `private Double policyRate` | The policy rate. |
| 1173 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1185 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1186 | `private double[] rolloverLedger` |  |
| 1187 | `private double[] rolloverRecord` |  |
| 1207 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1220 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1233 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1247 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1260 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1279 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1280 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1281 | `private double householdBondRatio` |  |
| 1282 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1299 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1300 | `private double buybackAbroadUnsettled` |  |
| 1323 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1329 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1346 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1360 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1386 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1415 | `private double[] cohorts` | The demographics. |
| 1439 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1441 | `private double[] families` |  |
| 1442 | `private double[] migration` |  |
| 1452 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1461 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1471 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1484 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1497 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1513 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1525 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1542 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1543 | `private double[] education` |  |
| 1572 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1605 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1606 | `private java.util.List<Integer> populationTrend` |  |
| 1607 | `private double cityCapitalSpending` |  |
| 1608 | `private double monthlyMaterialImports` |  |
| 1615 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1616 | `private int materialsConsumed` |  |
| 1639 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1656 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1667 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1764 | **type** `public class DataSave` |  |
| 138 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 139 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 140 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 141 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 150-163)

### the shedding warning (lines 164-310)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 259 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 263 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 267 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 311-609)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 1 | `public void setSlotName(String name)` |  |
| 316 | 11 | `public void setFounding(Founding f)` | The founding record, whole. |
| 335 | 14 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 351 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 362 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 363 | 1 | `public double[] getTreasuryMonth()` |  |
| 403 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 407 | 1 | `public double getCityPaperUnsettled()` |  |
| 408 | 1 | `public double getCityDiscountUnsettled()` |  |
| 410 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 417 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 418 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 419 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 420 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 421 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 422 | 1 | `public double getTreasuryRaisedPending()` |  |
| 435 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 436 | 1 | `public double[] getGovernmentMonth()` |  |
| 437 | 1 | `public String getSlotName()` |  |
| 440 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 446 | 1 | `public String getGameVersion()` |  |
| 447 | 1 | `public int getSaveFormat()` |  |
| 448 | 1 | `public long getSavedAt()` |  |
| 450 | 1 | `public void setHouseholdSavings(double value)` |  |
| 451 | 1 | `public double getHouseholdSavings()` |  |
| 453 | 1 | `public void setLandOwned(double sqFt)` |  |
| 454 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 455 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 457 | 1 | `public double getLandOwned()` |  |
| 458 | 1 | `public int getLandBlocksPurchased()` |  |
| 459 | 1 | `public double getLandPricePerSqFt()` |  |
| 461 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 462 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 464 | 1 | `public double getIncomeTaxRate()` |  |
| 465 | 1 | `public double getPropertyTaxRate()` |  |
| 467 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 472 | 3 | `public void setCash(double money)` | setters |
| 476 | 3 | `public void setMonth(int month)` |  |
| 480 | 3 | `public void setBuildingNum(int i)` |  |
| 484 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 491 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 496 | 3 | `public void setProgress(double[] progress)` |  |
| 500 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 504 | 3 | `public void setPopulation(int population)` |  |
| 507 | 3 | `public void setReports(boolean reports)` |  |
| 520 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 523 | 3 | `public void setGraphs(boolean graphs)` |  |
| 552 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 577 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 603 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 610-659)

| line | len | member | says |
|---:|---:|---|---|
| 612 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 620 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 623 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 627 | 3 | `public int getConstructionByIdLength()` |  |
| 631 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 638 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 645 | 6 | `public double getContractValueById(int templateId)` |  |
| 653 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 660-685)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 663 | 1 | `public double getPropertyTaxCharged()` |  |
| 665 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 666 | 1 | `public double getCityInterestAccrued()` |  |
| 668 | 1 | `public void setWorkforce(int workforce)` |  |
| 671 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 673 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 679 | 1 | `public double[] getLandListing()` |  |
| 680 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 681 | 1 | `public double[] getLandMarketPrices()` |  |
| 682 | 1 | `public int getIronDeposits()` |  |
| 683 | 1 | `public double getIronReserveTonnes()` |  |

### policy (lines 686-1775)

| line | len | member | says |
|---:|---:|---|---|
| 749 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 750 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 751 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 752 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 753 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 754 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 756 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 757 | 1 | `public double[] getTaxPolicyState()` |  |
| 759 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 760 | 1 | `public double[] getHouseholdBalance()` |  |
| 762 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 766 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 767 | 1 | `public double[] getHouseholdCells()` |  |
| 769 | 1 | `public void setBankCash(double cash)` |  |
| 770 | 1 | `public double getBankCash()` |  |
| 783 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 784 | 1 | `public double getBankBranchesCapitalised()` |  |
| 795 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 796 | 1 | `public double getBankProfitLastMonth()` |  |
| 800 | 1 | `public void setBankDepositRate(double v)` |  |
| 801 | 1 | `public double getBankDepositRate()` |  |
| 817 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 818 | 1 | `public double[] getForeignAccounts()` |  |
| 830 | 1 | `public void setCentralBank(double[] state)` |  |
| 831 | 1 | `public double[] getCentralBank()` |  |
| 842 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 846 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 847 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 859 | 1 | `public void setForeignStanding(double[] state)` |  |
| 860 | 1 | `public double[] getForeignStanding()` |  |
| 871 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 872 | 1 | `public double[] getCapitalFlows()` |  |
| 882 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 883 | 1 | `public double[] getOutwardInvestment()` |  |
| 894 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 895 | 1 | `public String[] getEquityKeys()` |  |
| 896 | 1 | `public double[] getEquity()` |  |
| 900 | 1 | `public void setExchange(double[] state)` |  |
| 901 | 1 | `public double[] getExchange()` |  |
| 905 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 906 | 1 | `public Exchange.State getExchangeState()` |  |
| 924 | 1 | `public void setBankSolvency(double[] state)` |  |
| 925 | 1 | `public double[] getBankSolvency()` |  |
| 943 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 944 | 1 | `public double[] getHousingOccupancy()` |  |
| 957 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 958 | 1 | `public double[] getBankLastMonth()` |  |
| 969 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 970 | 1 | `public double[] getBankPricingHistory()` |  |
| 981 | 1 | `public void setBankLateProfit(double value)` |  |
| 982 | 1 | `public double getBankLateProfit()` |  |
| 996 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 997 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1008 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1009 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1019 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1020 | 1 | `public double[] getBankMonthLines()` |  |
| 1031 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1032 | 1 | `public double[] getBankStatementYear()` |  |
| 1044 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1045 | 1 | `public double[] getBankSheetYear()` |  |
| 1057 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1058 | 1 | `public Double getBankPaidInOpening()` |  |
| 1059 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1060 | 1 | `public Double getBankRetainedOpening()` |  |
| 1071 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1075 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1076 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1087 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1088 | 1 | `public TreasuryFund.State getFund()` |  |
| 1093 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1094 | 1 | `public double[] getDebtMarket()` |  |
| 1105 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1106 | 1 | `public double[] getPriceIndex()` |  |
| 1118 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1119 | 1 | `public double[] getWorldEconomy()` |  |
| 1134 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1135 | 1 | `public double getTradedExchangeRate()` |  |
| 1149 | 1 | `public void setDenomination(double[] state)` |  |
| 1150 | 1 | `public double[] getDenomination()` |  |
| 1164 | 1 | `public void setPolicyRate(double rate)` |  |
| 1166 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1175 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1176 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1189 | 1 | `public void setRolloverMode(String mode)` |  |
| 1191 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1196 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1197 | 1 | `public double[] getRolloverLedger()` |  |
| 1198 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1199 | 1 | `public double[] getRolloverRecord()` |  |
| 1209 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1210 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1222 | 1 | `public void setInflationTarget(double target)` |  |
| 1224 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1235 | 1 | `public void setQeTargetShare(double share)` |  |
| 1236 | 1 | `public double getQeTargetShare()` |  |
| 1249 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1251 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1262 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1263 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1284 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1285 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1286 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1287 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1288 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1289 | 1 | `public double getHouseholdBondRatio()` |  |
| 1290 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1291 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1302 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1306 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1307 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1331 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1332 | 1 | `public double getCarsPerHousehold()` |  |
| 1334 | 1 | `public void setRememberedCommute(double v)` |  |
| 1335 | 3 | `public double getRememberedCommute()` |  |
| 1348 | 1 | `public void setCostOfLiving(double v)` |  |
| 1349 | 1 | `public double getCostOfLiving()` |  |
| 1362 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1363 | 1 | `public double getBankTaxCharged()` |  |
| 1367 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1368 | 1 | `public double getRentWeight()` |  |
| 1388 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1389 | 1 | `public double getRentWeightStudio()` |  |
| 1391 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1395 | 1 | `public int getConstructionShedMonth()` |  |
| 1396 | 1 | `public double getConstructionShedPoints()` |  |
| 1398 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1401 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1545 | 1 | `public void setLicences(double[] a)` |  |
| 1546 | 1 | `public double[] getLicences()` |  |
| 1547 | 1 | `public void setEducation(double[] a)` |  |
| 1548 | 1 | `public double[] getEducation()` |  |
| 1550 | 1 | `public void setLabour(double[] a)` |  |
| 1551 | 1 | `public double[] getLabour()` |  |
| 1552 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1553 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1555 | 1 | `public void setCohorts(double[] a)` |  |
| 1556 | 1 | `public double[] getCohorts()` |  |
| 1559 | 1 | `public void setBandNames(String[] names)` |  |
| 1561 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1575 | 1 | `public void setShapeNames(String[] names)` |  |
| 1576 | 1 | `public String[] getShapeNames()` |  |
| 1577 | 1 | `public void setFamilies(double[] a)` |  |
| 1578 | 1 | `public double[] getFamilies()` |  |
| 1579 | 1 | `public void setMigration(double[] a)` |  |
| 1580 | 1 | `public double[] getMigration()` |  |
| 1581 | 1 | `public void setUnemployment(double[] a)` |  |
| 1582 | 1 | `public double[] getUnemployment()` |  |
| 1583 | 1 | `public void setSickness(double[] a)` |  |
| 1584 | 1 | `public double[] getSickness()` |  |
| 1585 | 1 | `public void setCrime(double[] a)` |  |
| 1586 | 1 | `public double[] getCrime()` |  |
| 1587 | 1 | `public void setHealth(double[] a)` |  |
| 1588 | 1 | `public double[] getHealth()` |  |
| 1589 | 1 | `public void setHealthcare(double[] a)` |  |
| 1590 | 1 | `public double[] getHealthcare()` |  |
| 1618 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1619 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1620 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1621 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1622 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1623 | 1 | `public double getCityCapitalSpending()` |  |
| 1641 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1642 | 1 | `public double getCityMaintenancePaid()` |  |
| 1643 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1644 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1645 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1646 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1647 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1648 | 1 | `public int getMaterialsConsumed()` |  |
| 1657 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1658 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1668 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1669 | 1 | `public double[] getHouseholdStatement()` |  |
| 1671 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1674 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1676 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1679 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1681 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1684 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1686 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1689 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1691 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1694 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1696 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 1697 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 1699 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 1700 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 1702 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 1703 | 1 | `public double getInsurancePremiums()` |  |
| 1705 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1706 | 1 | `public double[] getNationalAccounts()` |  |
| 1716 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1719 | 3 | `public int getUnderConstruction(int index)` |  |
| 1722 | 3 | `public double getCash()` |  |
| 1726 | 3 | `public int getMonth()` |  |
| 1730 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1734 | 3 | `public int getBuildingsLength()` |  |
| 1738 | 3 | `public JsonArray getDebt()` |  |
| 1742 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1747 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1751 | 3 | `public int getProgressLength()` |  |
| 1755 | 3 | `public double getProgress(int index)` |  |
| 1759 | 3 | `public int getConstructionMaterials()` |  |
| 1763 | 3 | `public int getPopulation()` |  |
| 1766 | 3 | `public boolean getReports()` |  |
| 1769 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1772 | 3 | `public boolean getGraphs()` |  |


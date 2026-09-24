# DataSave.java - 1,576 lines · 248 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Notice](Notice.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | · land and ore |
| 146 | · the shedding warning |
| 279 | · the header |
| 540 | · construction, by id |
| 590 | · charged, not derived |
| 616 | · policy |

## Fields (state)

| line | field | says |
|---:|---|---|
| 27 | `private java.util.List<Notice> notices` | See setNotices: null on every save written before the inbox existed. |
| 29 | `private String slotName` |  |
| 30 | `private String gameVersion` |  |
| 31 | `private int saveFormat` |  |
| 32 | `private long savedAt` |  |
| 35 | `private double cash` | save variables |
| 36 | `private int[] buildings` |  |
| 37 | `private int month` |  |
| 38 | `private JsonArray debts` |  |
| 46 | `private JsonArray businessDebts` | Private-sector loans. |
| 53 | `private double[] progress` | LEGACY construction state: one entry per stack, in build order. |
| 54 | `private int[] underConstruction` |  |
| 65 | `private double[] constructionProgressById` | Construction, keyed by template id - the same key buildings[] uses. |
| 66 | `private int[] underConstructionById` |  |
| 68 | `private double[] materialsOwedById` | Material the sites still have to draw, by id. |
| 70 | `private double[] contractValueById` | The builders' contract still on each template's sites, by id. |
| 91 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 106 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 117 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 118 | `private java.util.List<Markets.State> markets` |  |
| 130 | `private int workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 140 | `private double[] landListing` |  |
| 142 | `private double[] landMarketPrices` | The office's struck prices and minimum lot - see LandMarket.getPriceState(). |
| 143 | `private int ironDeposits` |  |
| 144 | `private double ironReserveTonnes` |  |
| 162 | `private int constructionShedMonth` |  |
| 163 | `private double constructionShedPoints` |  |
| 170 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 180 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 187 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 188 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 196 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 197 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 205 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 208 | `private int constructionMaterials` | The city's own yard, in units. |
| 209 | `private int population` |  |
| 220 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 221 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 240 | `private boolean reports` | settings |
| 241 | `private boolean graphs` |  |
| 252 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 262 | `private double landOwned` | Land. |
| 263 | `private int landBlocksPurchased` |  |
| 264 | `private double landPricePerSqFt` |  |
| 273 | `private double incomeTaxRate` | Tax rates. |
| 274 | `private double propertyTaxRate` |  |
| 290 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 308 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 309 | `private double[] treasuryJournalAmounts` |  |
| 310 | `private String[] treasuryJournalPendingLabels` |  |
| 311 | `private double[] treasuryJournalPendingAmounts` |  |
| 319 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 330 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 331 | `private double cityDiscountUnsettled` |  |
| 363 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 626 | `private double[] taxPolicyState` |  |
| 636 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 648 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 649 | `private double[] householdCells` |  |
| 661 | `private double bankCash` | The bank's cash. |
| 671 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 674 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 675 | `private SalesTaxLedger.State salesTax` |  |
| 677 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 711 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 723 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 729 | `private double bankDepositRate` | What savers were paid. |
| 745 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 758 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 769 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 770 | `private double[] treasuryArrearsAmounts` |  |
| 787 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 799 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 810 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 821 | `private String[] equityKeys` | The share register, company by company, named. |
| 822 | `private double[] equity` |  |
| 829 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 845 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate, whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over its life, which a shorter record counts from the load. |
| 864 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 878 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 890 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 902 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 917 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 929 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 940 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 952 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 964 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 977 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 993 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1008 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1023 | `private Double policyRate` | The policy rate. |
| 1034 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1045 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1058 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1071 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1085 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1098 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1109 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1110 | `private double buybackAbroadUnsettled` |  |
| 1133 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1139 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1156 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1170 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1196 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1225 | `private double[] cohorts` | The demographics. |
| 1249 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1251 | `private double[] families` |  |
| 1252 | `private double[] migration` |  |
| 1262 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1271 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1281 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1294 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1307 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1323 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1335 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1352 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1353 | `private double[] education` |  |
| 1382 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1415 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1416 | `private java.util.List<Integer> populationTrend` |  |
| 1417 | `private double cityCapitalSpending` |  |
| 1418 | `private double monthlyMaterialImports` |  |
| 1425 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1426 | `private int materialsConsumed` |  |
| 1449 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1466 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1477 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1565 | **type** `public class DataSave` |  |
| 120 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 121 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 122 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 123 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 132-145)

### the shedding warning (lines 146-278)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 227 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 231 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 235 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 279-539)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 1 | `public void setSlotName(String name)` |  |
| 292 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 293 | 1 | `public double[] getTreasuryMonth()` |  |
| 333 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 337 | 1 | `public double getCityPaperUnsettled()` |  |
| 338 | 1 | `public double getCityDiscountUnsettled()` |  |
| 340 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 347 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 348 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 349 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 350 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 351 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 352 | 1 | `public double getTreasuryRaisedPending()` |  |
| 365 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 366 | 1 | `public double[] getGovernmentMonth()` |  |
| 367 | 1 | `public String getSlotName()` |  |
| 370 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 376 | 1 | `public String getGameVersion()` |  |
| 377 | 1 | `public int getSaveFormat()` |  |
| 378 | 1 | `public long getSavedAt()` |  |
| 380 | 1 | `public void setHouseholdSavings(double value)` |  |
| 381 | 1 | `public double getHouseholdSavings()` |  |
| 383 | 1 | `public void setLandOwned(double sqFt)` |  |
| 384 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 385 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 387 | 1 | `public double getLandOwned()` |  |
| 388 | 1 | `public int getLandBlocksPurchased()` |  |
| 389 | 1 | `public double getLandPricePerSqFt()` |  |
| 391 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 392 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 394 | 1 | `public double getIncomeTaxRate()` |  |
| 395 | 1 | `public double getPropertyTaxRate()` |  |
| 397 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 402 | 3 | `public void setCash(double money)` | setters |
| 406 | 3 | `public void setMonth(int month)` |  |
| 410 | 3 | `public void setBuildingNum(int i)` |  |
| 414 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 421 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 426 | 3 | `public void setProgress(double[] progress)` |  |
| 430 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 434 | 3 | `public void setPopulation(int population)` |  |
| 437 | 3 | `public void setReports(boolean reports)` |  |
| 450 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 453 | 3 | `public void setGraphs(boolean graphs)` |  |
| 482 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 507 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 533 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 540-589)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 550 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 553 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 557 | 3 | `public int getConstructionByIdLength()` |  |
| 561 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 568 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 575 | 6 | `public double getContractValueById(int templateId)` |  |
| 583 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 590-615)

| line | len | member | says |
|---:|---:|---|---|
| 592 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 593 | 1 | `public double getPropertyTaxCharged()` |  |
| 595 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 596 | 1 | `public double getCityInterestAccrued()` |  |
| 598 | 1 | `public void setWorkforce(int workforce)` |  |
| 601 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 603 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 609 | 1 | `public double[] getLandListing()` |  |
| 610 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 611 | 1 | `public double[] getLandMarketPrices()` |  |
| 612 | 1 | `public int getIronDeposits()` |  |
| 613 | 1 | `public double getIronReserveTonnes()` |  |

### policy (lines 616-1576)

| line | len | member | says |
|---:|---:|---|---|
| 679 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 680 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 681 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 682 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 683 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 684 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 686 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 687 | 1 | `public double[] getTaxPolicyState()` |  |
| 689 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 690 | 1 | `public double[] getHouseholdBalance()` |  |
| 692 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 696 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 697 | 1 | `public double[] getHouseholdCells()` |  |
| 699 | 1 | `public void setBankCash(double cash)` |  |
| 700 | 1 | `public double getBankCash()` |  |
| 713 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 714 | 1 | `public double getBankBranchesCapitalised()` |  |
| 725 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 726 | 1 | `public double getBankProfitLastMonth()` |  |
| 730 | 1 | `public void setBankDepositRate(double v)` |  |
| 731 | 1 | `public double getBankDepositRate()` |  |
| 747 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 748 | 1 | `public double[] getForeignAccounts()` |  |
| 760 | 1 | `public void setCentralBank(double[] state)` |  |
| 761 | 1 | `public double[] getCentralBank()` |  |
| 772 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 776 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 777 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 789 | 1 | `public void setForeignStanding(double[] state)` |  |
| 790 | 1 | `public double[] getForeignStanding()` |  |
| 801 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 802 | 1 | `public double[] getCapitalFlows()` |  |
| 812 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 813 | 1 | `public double[] getOutwardInvestment()` |  |
| 824 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 825 | 1 | `public String[] getEquityKeys()` |  |
| 826 | 1 | `public double[] getEquity()` |  |
| 830 | 1 | `public void setExchange(double[] state)` |  |
| 831 | 1 | `public double[] getExchange()` |  |
| 847 | 1 | `public void setBankSolvency(double[] state)` |  |
| 848 | 1 | `public double[] getBankSolvency()` |  |
| 866 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 867 | 1 | `public double[] getHousingOccupancy()` |  |
| 880 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 881 | 1 | `public double[] getBankLastMonth()` |  |
| 892 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 893 | 1 | `public double[] getBankPricingHistory()` |  |
| 904 | 1 | `public void setBankLateProfit(double value)` |  |
| 905 | 1 | `public double getBankLateProfit()` |  |
| 919 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 920 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 931 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 932 | 1 | `public double[] getBankCapitalRecord()` |  |
| 942 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 943 | 1 | `public double[] getBankMonthLines()` |  |
| 954 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 955 | 1 | `public double[] getBankStatementYear()` |  |
| 966 | 1 | `public void setPriceIndex(double[] state)` |  |
| 967 | 1 | `public double[] getPriceIndex()` |  |
| 979 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 980 | 1 | `public double[] getWorldEconomy()` |  |
| 995 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 996 | 1 | `public double getTradedExchangeRate()` |  |
| 1010 | 1 | `public void setDenomination(double[] state)` |  |
| 1011 | 1 | `public double[] getDenomination()` |  |
| 1025 | 1 | `public void setPolicyRate(double rate)` |  |
| 1027 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1036 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1037 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1047 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1048 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1060 | 1 | `public void setInflationTarget(double target)` |  |
| 1062 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1073 | 1 | `public void setQeTargetShare(double share)` |  |
| 1074 | 1 | `public double getQeTargetShare()` |  |
| 1087 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1089 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1100 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1101 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1112 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1116 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1117 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1141 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1142 | 1 | `public double getCarsPerHousehold()` |  |
| 1144 | 1 | `public void setRememberedCommute(double v)` |  |
| 1145 | 3 | `public double getRememberedCommute()` |  |
| 1158 | 1 | `public void setCostOfLiving(double v)` |  |
| 1159 | 1 | `public double getCostOfLiving()` |  |
| 1172 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1173 | 1 | `public double getBankTaxCharged()` |  |
| 1177 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1178 | 1 | `public double getRentWeight()` |  |
| 1198 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1199 | 1 | `public double getRentWeightStudio()` |  |
| 1201 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1205 | 1 | `public int getConstructionShedMonth()` |  |
| 1206 | 1 | `public double getConstructionShedPoints()` |  |
| 1208 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1211 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1355 | 1 | `public void setLicences(double[] a)` |  |
| 1356 | 1 | `public double[] getLicences()` |  |
| 1357 | 1 | `public void setEducation(double[] a)` |  |
| 1358 | 1 | `public double[] getEducation()` |  |
| 1360 | 1 | `public void setLabour(double[] a)` |  |
| 1361 | 1 | `public double[] getLabour()` |  |
| 1362 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1363 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1365 | 1 | `public void setCohorts(double[] a)` |  |
| 1366 | 1 | `public double[] getCohorts()` |  |
| 1369 | 1 | `public void setBandNames(String[] names)` |  |
| 1371 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1385 | 1 | `public void setShapeNames(String[] names)` |  |
| 1386 | 1 | `public String[] getShapeNames()` |  |
| 1387 | 1 | `public void setFamilies(double[] a)` |  |
| 1388 | 1 | `public double[] getFamilies()` |  |
| 1389 | 1 | `public void setMigration(double[] a)` |  |
| 1390 | 1 | `public double[] getMigration()` |  |
| 1391 | 1 | `public void setUnemployment(double[] a)` |  |
| 1392 | 1 | `public double[] getUnemployment()` |  |
| 1393 | 1 | `public void setSickness(double[] a)` |  |
| 1394 | 1 | `public double[] getSickness()` |  |
| 1395 | 1 | `public void setCrime(double[] a)` |  |
| 1396 | 1 | `public double[] getCrime()` |  |
| 1397 | 1 | `public void setHealth(double[] a)` |  |
| 1398 | 1 | `public double[] getHealth()` |  |
| 1399 | 1 | `public void setHealthcare(double[] a)` |  |
| 1400 | 1 | `public double[] getHealthcare()` |  |
| 1428 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1429 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1430 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1431 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1432 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1433 | 1 | `public double getCityCapitalSpending()` |  |
| 1451 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1452 | 1 | `public double getCityMaintenancePaid()` |  |
| 1453 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1454 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1455 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1456 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1457 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1458 | 1 | `public int getMaterialsConsumed()` |  |
| 1467 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1468 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1478 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1479 | 1 | `public double[] getHouseholdStatement()` |  |
| 1481 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1484 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1486 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1489 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1491 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1494 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1496 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1499 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1501 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1504 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1506 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1507 | 1 | `public double[] getNationalAccounts()` |  |
| 1517 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1520 | 3 | `public int getUnderConstruction(int index)` |  |
| 1523 | 3 | `public double getCash()` |  |
| 1527 | 3 | `public int getMonth()` |  |
| 1531 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1535 | 3 | `public int getBuildingsLength()` |  |
| 1539 | 3 | `public JsonArray getDebt()` |  |
| 1543 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1548 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1552 | 3 | `public int getProgressLength()` |  |
| 1556 | 3 | `public double getProgress(int index)` |  |
| 1560 | 3 | `public int getConstructionMaterials()` |  |
| 1564 | 3 | `public int getPopulation()` |  |
| 1567 | 3 | `public boolean getReports()` |  |
| 1570 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1573 | 3 | `public boolean getGraphs()` |  |


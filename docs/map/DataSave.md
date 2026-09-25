# DataSave.java - 1,654 lines · 257 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 150 | · land and ore |
| 164 | · the shedding warning |
| 310 | · the header |
| 609 | · construction, by id |
| 659 | · charged, not derived |
| 685 | · policy |

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
| 234 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 235 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 236 | `private double insurancePremiums` |  |
| 239 | `private int constructionMaterials` | The city's own yard, in units. |
| 240 | `private int population` |  |
| 251 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 252 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 271 | `private boolean reports` | settings |
| 272 | `private boolean graphs` |  |
| 283 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 293 | `private double landOwned` | Land. |
| 294 | `private int landBlocksPurchased` |  |
| 295 | `private double landPricePerSqFt` |  |
| 304 | `private double incomeTaxRate` | Tax rates. |
| 305 | `private double propertyTaxRate` |  |
| 359 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 377 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 378 | `private double[] treasuryJournalAmounts` |  |
| 379 | `private String[] treasuryJournalPendingLabels` |  |
| 380 | `private double[] treasuryJournalPendingAmounts` |  |
| 388 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 399 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 400 | `private double cityDiscountUnsettled` |  |
| 432 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 695 | `private double[] taxPolicyState` |  |
| 705 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 717 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 718 | `private double[] householdCells` |  |
| 730 | `private double bankCash` | The bank's cash. |
| 740 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 743 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 744 | `private SalesTaxLedger.State salesTax` |  |
| 746 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 780 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 792 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 798 | `private double bankDepositRate` | What savers were paid. |
| 814 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 827 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 838 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 839 | `private double[] treasuryArrearsAmounts` |  |
| 856 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 868 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 879 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 890 | `private String[] equityKeys` | The share register, company by company, named. |
| 891 | `private double[] equity` |  |
| 898 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 914 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate, whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over its life, which a shorter record counts from the load. |
| 933 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 947 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 959 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 971 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 986 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 998 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1009 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1021 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1033 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1046 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1062 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1077 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1092 | `private Double policyRate` | The policy rate. |
| 1103 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1114 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1127 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1140 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1154 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1167 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1178 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1179 | `private double buybackAbroadUnsettled` |  |
| 1202 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1208 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1225 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1239 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1265 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1294 | `private double[] cohorts` | The demographics. |
| 1318 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1320 | `private double[] families` |  |
| 1321 | `private double[] migration` |  |
| 1331 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1340 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1350 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1363 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1376 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1392 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1404 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1421 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1422 | `private double[] education` |  |
| 1451 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1484 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1485 | `private java.util.List<Integer> populationTrend` |  |
| 1486 | `private double cityCapitalSpending` |  |
| 1487 | `private double monthlyMaterialImports` |  |
| 1494 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1495 | `private int materialsConsumed` |  |
| 1518 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1535 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1546 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1643 | **type** `public class DataSave` |  |
| 138 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 139 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 140 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 141 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 150-163)

### the shedding warning (lines 164-309)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 258 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 262 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 266 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 310-608)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 1 | `public void setSlotName(String name)` |  |
| 315 | 11 | `public void setFounding(Founding f)` | The founding record, whole. |
| 334 | 14 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 350 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 361 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 362 | 1 | `public double[] getTreasuryMonth()` |  |
| 402 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 406 | 1 | `public double getCityPaperUnsettled()` |  |
| 407 | 1 | `public double getCityDiscountUnsettled()` |  |
| 409 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 416 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 417 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 418 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 419 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 420 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 421 | 1 | `public double getTreasuryRaisedPending()` |  |
| 434 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 435 | 1 | `public double[] getGovernmentMonth()` |  |
| 436 | 1 | `public String getSlotName()` |  |
| 439 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 445 | 1 | `public String getGameVersion()` |  |
| 446 | 1 | `public int getSaveFormat()` |  |
| 447 | 1 | `public long getSavedAt()` |  |
| 449 | 1 | `public void setHouseholdSavings(double value)` |  |
| 450 | 1 | `public double getHouseholdSavings()` |  |
| 452 | 1 | `public void setLandOwned(double sqFt)` |  |
| 453 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 454 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 456 | 1 | `public double getLandOwned()` |  |
| 457 | 1 | `public int getLandBlocksPurchased()` |  |
| 458 | 1 | `public double getLandPricePerSqFt()` |  |
| 460 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 461 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 463 | 1 | `public double getIncomeTaxRate()` |  |
| 464 | 1 | `public double getPropertyTaxRate()` |  |
| 466 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 471 | 3 | `public void setCash(double money)` | setters |
| 475 | 3 | `public void setMonth(int month)` |  |
| 479 | 3 | `public void setBuildingNum(int i)` |  |
| 483 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 490 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 495 | 3 | `public void setProgress(double[] progress)` |  |
| 499 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 503 | 3 | `public void setPopulation(int population)` |  |
| 506 | 3 | `public void setReports(boolean reports)` |  |
| 519 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 522 | 3 | `public void setGraphs(boolean graphs)` |  |
| 551 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 576 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 602 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 609-658)

| line | len | member | says |
|---:|---:|---|---|
| 611 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 619 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 622 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 626 | 3 | `public int getConstructionByIdLength()` |  |
| 630 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 637 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 644 | 6 | `public double getContractValueById(int templateId)` |  |
| 652 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 659-684)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 662 | 1 | `public double getPropertyTaxCharged()` |  |
| 664 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 665 | 1 | `public double getCityInterestAccrued()` |  |
| 667 | 1 | `public void setWorkforce(int workforce)` |  |
| 670 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 672 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 678 | 1 | `public double[] getLandListing()` |  |
| 679 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 680 | 1 | `public double[] getLandMarketPrices()` |  |
| 681 | 1 | `public int getIronDeposits()` |  |
| 682 | 1 | `public double getIronReserveTonnes()` |  |

### policy (lines 685-1654)

| line | len | member | says |
|---:|---:|---|---|
| 748 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 749 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 750 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 751 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 752 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 753 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 755 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 756 | 1 | `public double[] getTaxPolicyState()` |  |
| 758 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 759 | 1 | `public double[] getHouseholdBalance()` |  |
| 761 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 765 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 766 | 1 | `public double[] getHouseholdCells()` |  |
| 768 | 1 | `public void setBankCash(double cash)` |  |
| 769 | 1 | `public double getBankCash()` |  |
| 782 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 783 | 1 | `public double getBankBranchesCapitalised()` |  |
| 794 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 795 | 1 | `public double getBankProfitLastMonth()` |  |
| 799 | 1 | `public void setBankDepositRate(double v)` |  |
| 800 | 1 | `public double getBankDepositRate()` |  |
| 816 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 817 | 1 | `public double[] getForeignAccounts()` |  |
| 829 | 1 | `public void setCentralBank(double[] state)` |  |
| 830 | 1 | `public double[] getCentralBank()` |  |
| 841 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 845 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 846 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 858 | 1 | `public void setForeignStanding(double[] state)` |  |
| 859 | 1 | `public double[] getForeignStanding()` |  |
| 870 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 871 | 1 | `public double[] getCapitalFlows()` |  |
| 881 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 882 | 1 | `public double[] getOutwardInvestment()` |  |
| 893 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 894 | 1 | `public String[] getEquityKeys()` |  |
| 895 | 1 | `public double[] getEquity()` |  |
| 899 | 1 | `public void setExchange(double[] state)` |  |
| 900 | 1 | `public double[] getExchange()` |  |
| 916 | 1 | `public void setBankSolvency(double[] state)` |  |
| 917 | 1 | `public double[] getBankSolvency()` |  |
| 935 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 936 | 1 | `public double[] getHousingOccupancy()` |  |
| 949 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 950 | 1 | `public double[] getBankLastMonth()` |  |
| 961 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 962 | 1 | `public double[] getBankPricingHistory()` |  |
| 973 | 1 | `public void setBankLateProfit(double value)` |  |
| 974 | 1 | `public double getBankLateProfit()` |  |
| 988 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 989 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1000 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1001 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1011 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1012 | 1 | `public double[] getBankMonthLines()` |  |
| 1023 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1024 | 1 | `public double[] getBankStatementYear()` |  |
| 1035 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1036 | 1 | `public double[] getPriceIndex()` |  |
| 1048 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1049 | 1 | `public double[] getWorldEconomy()` |  |
| 1064 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1065 | 1 | `public double getTradedExchangeRate()` |  |
| 1079 | 1 | `public void setDenomination(double[] state)` |  |
| 1080 | 1 | `public double[] getDenomination()` |  |
| 1094 | 1 | `public void setPolicyRate(double rate)` |  |
| 1096 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1105 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1106 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1116 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1117 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1129 | 1 | `public void setInflationTarget(double target)` |  |
| 1131 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1142 | 1 | `public void setQeTargetShare(double share)` |  |
| 1143 | 1 | `public double getQeTargetShare()` |  |
| 1156 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1158 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1169 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1170 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1181 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1185 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1186 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1210 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1211 | 1 | `public double getCarsPerHousehold()` |  |
| 1213 | 1 | `public void setRememberedCommute(double v)` |  |
| 1214 | 3 | `public double getRememberedCommute()` |  |
| 1227 | 1 | `public void setCostOfLiving(double v)` |  |
| 1228 | 1 | `public double getCostOfLiving()` |  |
| 1241 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1242 | 1 | `public double getBankTaxCharged()` |  |
| 1246 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1247 | 1 | `public double getRentWeight()` |  |
| 1267 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1268 | 1 | `public double getRentWeightStudio()` |  |
| 1270 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1274 | 1 | `public int getConstructionShedMonth()` |  |
| 1275 | 1 | `public double getConstructionShedPoints()` |  |
| 1277 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1280 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1424 | 1 | `public void setLicences(double[] a)` |  |
| 1425 | 1 | `public double[] getLicences()` |  |
| 1426 | 1 | `public void setEducation(double[] a)` |  |
| 1427 | 1 | `public double[] getEducation()` |  |
| 1429 | 1 | `public void setLabour(double[] a)` |  |
| 1430 | 1 | `public double[] getLabour()` |  |
| 1431 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1432 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1434 | 1 | `public void setCohorts(double[] a)` |  |
| 1435 | 1 | `public double[] getCohorts()` |  |
| 1438 | 1 | `public void setBandNames(String[] names)` |  |
| 1440 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1454 | 1 | `public void setShapeNames(String[] names)` |  |
| 1455 | 1 | `public String[] getShapeNames()` |  |
| 1456 | 1 | `public void setFamilies(double[] a)` |  |
| 1457 | 1 | `public double[] getFamilies()` |  |
| 1458 | 1 | `public void setMigration(double[] a)` |  |
| 1459 | 1 | `public double[] getMigration()` |  |
| 1460 | 1 | `public void setUnemployment(double[] a)` |  |
| 1461 | 1 | `public double[] getUnemployment()` |  |
| 1462 | 1 | `public void setSickness(double[] a)` |  |
| 1463 | 1 | `public double[] getSickness()` |  |
| 1464 | 1 | `public void setCrime(double[] a)` |  |
| 1465 | 1 | `public double[] getCrime()` |  |
| 1466 | 1 | `public void setHealth(double[] a)` |  |
| 1467 | 1 | `public double[] getHealth()` |  |
| 1468 | 1 | `public void setHealthcare(double[] a)` |  |
| 1469 | 1 | `public double[] getHealthcare()` |  |
| 1497 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1498 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1499 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1500 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1501 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1502 | 1 | `public double getCityCapitalSpending()` |  |
| 1520 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1521 | 1 | `public double getCityMaintenancePaid()` |  |
| 1522 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1523 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1524 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1525 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1526 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1527 | 1 | `public int getMaterialsConsumed()` |  |
| 1536 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1537 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1547 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1548 | 1 | `public double[] getHouseholdStatement()` |  |
| 1550 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1553 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1555 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1558 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1560 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1563 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1565 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1568 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1570 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1573 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1575 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 1576 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 1578 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 1579 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 1581 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 1582 | 1 | `public double getInsurancePremiums()` |  |
| 1584 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1585 | 1 | `public double[] getNationalAccounts()` |  |
| 1595 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1598 | 3 | `public int getUnderConstruction(int index)` |  |
| 1601 | 3 | `public double getCash()` |  |
| 1605 | 3 | `public int getMonth()` |  |
| 1609 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1613 | 3 | `public int getBuildingsLength()` |  |
| 1617 | 3 | `public JsonArray getDebt()` |  |
| 1621 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1626 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1630 | 3 | `public int getProgressLength()` |  |
| 1634 | 3 | `public double getProgress(int index)` |  |
| 1638 | 3 | `public int getConstructionMaterials()` |  |
| 1642 | 3 | `public int getPopulation()` |  |
| 1645 | 3 | `public boolean getReports()` |  |
| 1648 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1651 | 3 | `public boolean getGraphs()` |  |


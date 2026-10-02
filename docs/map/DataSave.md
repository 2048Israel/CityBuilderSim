# DataSave.java - 1,817 lines · 292 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (5), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 170 | · land and ore |
| 184 | · the shedding warning |
| 331 | · the header |
| 630 | · construction, by id |
| 692 | · charged, not derived |
| 718 | · policy |

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
| 100 | `private ConstructionControl.State constructionControl` | The player's hand on the queue (0.7.22; ConstructionControl): the city's order of its own sites, the rushes and their months on overtime, the cancels waiting for the month's end, the stopped shells, the demolitions on... |
| 108 | `private java.util.List<DecisionLog.Entry> decisionLog` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, a month, a kind and a line each. |
| 129 | `private double propertyTaxCharged` | The property tax the city CHARGED this month, rather than a figure derived from its state. |
| 144 | `private double cityInterestAccrued` | Interest the city's own bonds accrued this month, not yet charged. |
| 155 | `private java.util.List<SectorState> sectors` | EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11, the sector template). |
| 156 | `private java.util.List<Markets.State> markets` |  |
| 168 | `private int workforce` | The workforce the month was worked by - see PopulationManager.restoreWorkforce(). |
| 178 | `private double[] landListing` |  |
| 180 | `private double[] landMarketPrices` | The office's struck prices and minimum lot - see LandMarket.getPriceState(). |
| 181 | `private int ironDeposits` |  |
| 182 | `private double ironReserveTonnes` |  |
| 200 | `private int constructionShedMonth` |  |
| 201 | `private double constructionShedPoints` |  |
| 208 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 218 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 225 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 226 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 234 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 235 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 243 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 255 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 256 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 257 | `private double insurancePremiums` |  |
| 260 | `private int constructionMaterials` | The city's own yard, in units. |
| 261 | `private int population` |  |
| 272 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 273 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 292 | `private boolean reports` | settings |
| 293 | `private boolean graphs` |  |
| 304 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 314 | `private double landOwned` | Land. |
| 315 | `private int landBlocksPurchased` |  |
| 316 | `private double landPricePerSqFt` |  |
| 325 | `private double incomeTaxRate` | Tax rates. |
| 326 | `private double propertyTaxRate` |  |
| 380 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 398 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 399 | `private double[] treasuryJournalAmounts` |  |
| 400 | `private String[] treasuryJournalPendingLabels` |  |
| 401 | `private double[] treasuryJournalPendingAmounts` |  |
| 409 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 420 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 421 | `private double cityDiscountUnsettled` |  |
| 453 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 728 | `private double[] taxPolicyState` |  |
| 741 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 753 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 754 | `private double[] householdCells` |  |
| 766 | `private double bankCash` | The bank's cash. |
| 776 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 779 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 780 | `private SalesTaxLedger.State salesTax` |  |
| 782 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 816 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 828 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 834 | `private double bankDepositRate` | What savers were paid. |
| 850 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 863 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 874 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 875 | `private double[] treasuryArrearsAmounts` |  |
| 892 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 904 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 915 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 926 | `private String[] equityKeys` | The share register, company by company, named. |
| 927 | `private double[] equity` |  |
| 934 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 939 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 957 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 976 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 993 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1005 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1017 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1032 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1044 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1055 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1067 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1080 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1092 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1093 | `private Double bankRetainedOpening` |  |
| 1106 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1107 | `private double[] bankPreferredRecord` |  |
| 1123 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1129 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1141 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1154 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1170 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1185 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1200 | `private Double policyRate` | The policy rate. |
| 1211 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1223 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1224 | `private double[] rolloverLedger` |  |
| 1225 | `private double[] rolloverRecord` |  |
| 1245 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1258 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1271 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1285 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1298 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1317 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1318 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1319 | `private double householdBondRatio` |  |
| 1320 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1337 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1338 | `private double buybackAbroadUnsettled` |  |
| 1361 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1367 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1384 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1398 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1424 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1457 | `private double[] cohorts` | The demographics. |
| 1481 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1483 | `private double[] families` |  |
| 1484 | `private double[] migration` |  |
| 1494 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1503 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1513 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1526 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1539 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1555 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1567 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1584 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1585 | `private double[] education` |  |
| 1614 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1647 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1648 | `private java.util.List<Integer> populationTrend` |  |
| 1649 | `private double cityCapitalSpending` |  |
| 1650 | `private double monthlyMaterialImports` |  |
| 1657 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1658 | `private int materialsConsumed` |  |
| 1681 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1698 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1709 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1806 | **type** `public class DataSave` |  |
| 158 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 159 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 160 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 161 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 170-183)

### the shedding warning (lines 184-330)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 279 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 283 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 287 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 331-629)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 1 | `public void setSlotName(String name)` |  |
| 336 | 11 | `public void setFounding(Founding f)` | The founding record, whole. |
| 355 | 14 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 371 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 382 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 383 | 1 | `public double[] getTreasuryMonth()` |  |
| 423 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 427 | 1 | `public double getCityPaperUnsettled()` |  |
| 428 | 1 | `public double getCityDiscountUnsettled()` |  |
| 430 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 437 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 438 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 439 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 440 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 441 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 442 | 1 | `public double getTreasuryRaisedPending()` |  |
| 455 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 456 | 1 | `public double[] getGovernmentMonth()` |  |
| 457 | 1 | `public String getSlotName()` |  |
| 460 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 466 | 1 | `public String getGameVersion()` |  |
| 467 | 1 | `public int getSaveFormat()` |  |
| 468 | 1 | `public long getSavedAt()` |  |
| 470 | 1 | `public void setHouseholdSavings(double value)` |  |
| 471 | 1 | `public double getHouseholdSavings()` |  |
| 473 | 1 | `public void setLandOwned(double sqFt)` |  |
| 474 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 475 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 477 | 1 | `public double getLandOwned()` |  |
| 478 | 1 | `public int getLandBlocksPurchased()` |  |
| 479 | 1 | `public double getLandPricePerSqFt()` |  |
| 481 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 482 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 484 | 1 | `public double getIncomeTaxRate()` |  |
| 485 | 1 | `public double getPropertyTaxRate()` |  |
| 487 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 492 | 3 | `public void setCash(double money)` | setters |
| 496 | 3 | `public void setMonth(int month)` |  |
| 500 | 3 | `public void setBuildingNum(int i)` |  |
| 504 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 511 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 516 | 3 | `public void setProgress(double[] progress)` |  |
| 520 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 524 | 3 | `public void setPopulation(int population)` |  |
| 527 | 3 | `public void setReports(boolean reports)` |  |
| 540 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 543 | 3 | `public void setGraphs(boolean graphs)` |  |
| 572 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 597 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 623 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 630-691)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 640 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 642 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 644 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 646 | 1 | `public void setConstructionControl(ConstructionControl.State state)` |  |
| 648 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 650 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 652 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 655 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 659 | 3 | `public int getConstructionByIdLength()` |  |
| 663 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 670 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 677 | 6 | `public double getContractValueById(int templateId)` |  |
| 685 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 692-717)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 695 | 1 | `public double getPropertyTaxCharged()` |  |
| 697 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 698 | 1 | `public double getCityInterestAccrued()` |  |
| 700 | 1 | `public void setWorkforce(int workforce)` |  |
| 703 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 705 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 711 | 1 | `public double[] getLandListing()` |  |
| 712 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 713 | 1 | `public double[] getLandMarketPrices()` |  |
| 714 | 1 | `public int getIronDeposits()` |  |
| 715 | 1 | `public double getIronReserveTonnes()` |  |

### policy (lines 718-1817)

| line | len | member | says |
|---:|---:|---|---|
| 784 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 785 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 786 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 787 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 788 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 789 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 791 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 792 | 1 | `public double[] getTaxPolicyState()` |  |
| 794 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 795 | 1 | `public double[] getHouseholdBalance()` |  |
| 797 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 801 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 802 | 1 | `public double[] getHouseholdCells()` |  |
| 804 | 1 | `public void setBankCash(double cash)` |  |
| 805 | 1 | `public double getBankCash()` |  |
| 818 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 819 | 1 | `public double getBankBranchesCapitalised()` |  |
| 830 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 831 | 1 | `public double getBankProfitLastMonth()` |  |
| 835 | 1 | `public void setBankDepositRate(double v)` |  |
| 836 | 1 | `public double getBankDepositRate()` |  |
| 852 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 853 | 1 | `public double[] getForeignAccounts()` |  |
| 865 | 1 | `public void setCentralBank(double[] state)` |  |
| 866 | 1 | `public double[] getCentralBank()` |  |
| 877 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 881 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 882 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 894 | 1 | `public void setForeignStanding(double[] state)` |  |
| 895 | 1 | `public double[] getForeignStanding()` |  |
| 906 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 907 | 1 | `public double[] getCapitalFlows()` |  |
| 917 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 918 | 1 | `public double[] getOutwardInvestment()` |  |
| 929 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 930 | 1 | `public String[] getEquityKeys()` |  |
| 931 | 1 | `public double[] getEquity()` |  |
| 935 | 1 | `public void setExchange(double[] state)` |  |
| 936 | 1 | `public double[] getExchange()` |  |
| 940 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 941 | 1 | `public Exchange.State getExchangeState()` |  |
| 959 | 1 | `public void setBankSolvency(double[] state)` |  |
| 960 | 1 | `public double[] getBankSolvency()` |  |
| 978 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 979 | 1 | `public double[] getHousingOccupancy()` |  |
| 995 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 996 | 1 | `public double[] getBankLastMonth()` |  |
| 1007 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1008 | 1 | `public double[] getBankPricingHistory()` |  |
| 1019 | 1 | `public void setBankLateProfit(double value)` |  |
| 1020 | 1 | `public double getBankLateProfit()` |  |
| 1034 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1035 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1046 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1047 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1057 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1058 | 1 | `public double[] getBankMonthLines()` |  |
| 1069 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1070 | 1 | `public double[] getBankStatementYear()` |  |
| 1082 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1083 | 1 | `public double[] getBankSheetYear()` |  |
| 1095 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1096 | 1 | `public Double getBankPaidInOpening()` |  |
| 1097 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1098 | 1 | `public Double getBankRetainedOpening()` |  |
| 1109 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1113 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1114 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1125 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1126 | 1 | `public TreasuryFund.State getFund()` |  |
| 1131 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1132 | 1 | `public double[] getDebtMarket()` |  |
| 1143 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1144 | 1 | `public double[] getPriceIndex()` |  |
| 1156 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1157 | 1 | `public double[] getWorldEconomy()` |  |
| 1172 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1173 | 1 | `public double getTradedExchangeRate()` |  |
| 1187 | 1 | `public void setDenomination(double[] state)` |  |
| 1188 | 1 | `public double[] getDenomination()` |  |
| 1202 | 1 | `public void setPolicyRate(double rate)` |  |
| 1204 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1213 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1214 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1227 | 1 | `public void setRolloverMode(String mode)` |  |
| 1229 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1234 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1235 | 1 | `public double[] getRolloverLedger()` |  |
| 1236 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1237 | 1 | `public double[] getRolloverRecord()` |  |
| 1247 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1248 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1260 | 1 | `public void setInflationTarget(double target)` |  |
| 1262 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1273 | 1 | `public void setQeTargetShare(double share)` |  |
| 1274 | 1 | `public double getQeTargetShare()` |  |
| 1287 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1289 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1300 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1301 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1322 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1323 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1324 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1325 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1326 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1327 | 1 | `public double getHouseholdBondRatio()` |  |
| 1328 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1329 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1340 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1344 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1345 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1369 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1370 | 1 | `public double getCarsPerHousehold()` |  |
| 1372 | 1 | `public void setRememberedCommute(double v)` |  |
| 1373 | 3 | `public double getRememberedCommute()` |  |
| 1386 | 1 | `public void setCostOfLiving(double v)` |  |
| 1387 | 1 | `public double getCostOfLiving()` |  |
| 1400 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1401 | 1 | `public double getBankTaxCharged()` |  |
| 1405 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1406 | 1 | `public double getRentWeight()` |  |
| 1426 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1427 | 1 | `public double getRentWeightStudio()` |  |
| 1429 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1433 | 1 | `public int getConstructionShedMonth()` |  |
| 1434 | 1 | `public double getConstructionShedPoints()` |  |
| 1436 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1439 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1587 | 1 | `public void setLicences(double[] a)` |  |
| 1588 | 1 | `public double[] getLicences()` |  |
| 1589 | 1 | `public void setEducation(double[] a)` |  |
| 1590 | 1 | `public double[] getEducation()` |  |
| 1592 | 1 | `public void setLabour(double[] a)` |  |
| 1593 | 1 | `public double[] getLabour()` |  |
| 1594 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1595 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1597 | 1 | `public void setCohorts(double[] a)` |  |
| 1598 | 1 | `public double[] getCohorts()` |  |
| 1601 | 1 | `public void setBandNames(String[] names)` |  |
| 1603 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1617 | 1 | `public void setShapeNames(String[] names)` |  |
| 1618 | 1 | `public String[] getShapeNames()` |  |
| 1619 | 1 | `public void setFamilies(double[] a)` |  |
| 1620 | 1 | `public double[] getFamilies()` |  |
| 1621 | 1 | `public void setMigration(double[] a)` |  |
| 1622 | 1 | `public double[] getMigration()` |  |
| 1623 | 1 | `public void setUnemployment(double[] a)` |  |
| 1624 | 1 | `public double[] getUnemployment()` |  |
| 1625 | 1 | `public void setSickness(double[] a)` |  |
| 1626 | 1 | `public double[] getSickness()` |  |
| 1627 | 1 | `public void setCrime(double[] a)` |  |
| 1628 | 1 | `public double[] getCrime()` |  |
| 1629 | 1 | `public void setHealth(double[] a)` |  |
| 1630 | 1 | `public double[] getHealth()` |  |
| 1631 | 1 | `public void setHealthcare(double[] a)` |  |
| 1632 | 1 | `public double[] getHealthcare()` |  |
| 1660 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1661 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1662 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1663 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1664 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1665 | 1 | `public double getCityCapitalSpending()` |  |
| 1683 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1684 | 1 | `public double getCityMaintenancePaid()` |  |
| 1685 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1686 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1687 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1688 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1689 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1690 | 1 | `public int getMaterialsConsumed()` |  |
| 1699 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1700 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1710 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1711 | 1 | `public double[] getHouseholdStatement()` |  |
| 1713 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1716 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1718 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1721 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1723 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1726 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1728 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1731 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1733 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1736 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1738 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 1739 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 1741 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 1742 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 1744 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 1745 | 1 | `public double getInsurancePremiums()` |  |
| 1747 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1748 | 1 | `public double[] getNationalAccounts()` |  |
| 1758 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1761 | 3 | `public int getUnderConstruction(int index)` |  |
| 1764 | 3 | `public double getCash()` |  |
| 1768 | 3 | `public int getMonth()` |  |
| 1772 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1776 | 3 | `public int getBuildingsLength()` |  |
| 1780 | 3 | `public JsonArray getDebt()` |  |
| 1784 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1789 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1793 | 3 | `public int getProgressLength()` |  |
| 1797 | 3 | `public double getProgress(int index)` |  |
| 1801 | 3 | `public int getConstructionMaterials()` |  |
| 1805 | 3 | `public int getPopulation()` |  |
| 1808 | 3 | `public boolean getReports()` |  |
| 1811 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1814 | 3 | `public boolean getGraphs()` |  |


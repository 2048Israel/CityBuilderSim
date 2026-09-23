# DataSave.java - 1,462 lines · 230 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Notice](Notice.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | · land and ore |
| 146 | · the shedding warning |
| 271 | · the header |
| 532 | · construction, by id |
| 582 | · charged, not derived |
| 608 | · policy |

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
| 200 | `private int constructionMaterials` | The city's own yard, in units. |
| 201 | `private int population` |  |
| 212 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 213 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 232 | `private boolean reports` | settings |
| 233 | `private boolean graphs` |  |
| 244 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 254 | `private double landOwned` | Land. |
| 255 | `private int landBlocksPurchased` |  |
| 256 | `private double landPricePerSqFt` |  |
| 265 | `private double incomeTaxRate` | Tax rates. |
| 266 | `private double propertyTaxRate` |  |
| 282 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 300 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 301 | `private double[] treasuryJournalAmounts` |  |
| 302 | `private String[] treasuryJournalPendingLabels` |  |
| 303 | `private double[] treasuryJournalPendingAmounts` |  |
| 311 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 322 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 323 | `private double cityDiscountUnsettled` |  |
| 355 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 618 | `private double[] taxPolicyState` |  |
| 628 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 640 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 641 | `private double[] householdCells` |  |
| 653 | `private double bankCash` | The bank's cash. |
| 663 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 666 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 667 | `private SalesTaxLedger.State salesTax` |  |
| 669 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 703 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 715 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 721 | `private double bankDepositRate` | What savers were paid. |
| 737 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 750 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 761 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 762 | `private double[] treasuryArrearsAmounts` |  |
| 779 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 791 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 802 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 813 | `private String[] equityKeys` | The share register, company by company, named. |
| 814 | `private double[] equity` |  |
| 821 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 835 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate, and whether it is frozen right now. |
| 854 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 868 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 880 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 893 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 909 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 924 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 939 | `private Double policyRate` | The policy rate. |
| 950 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 962 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 976 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 989 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1000 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1001 | `private double buybackAbroadUnsettled` |  |
| 1024 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1030 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1047 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1061 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1087 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1116 | `private double[] cohorts` | The demographics. |
| 1140 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1142 | `private double[] families` |  |
| 1143 | `private double[] migration` |  |
| 1153 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1162 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1172 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1185 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1198 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1214 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1226 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1243 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1244 | `private double[] education` |  |
| 1273 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1306 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1307 | `private java.util.List<Integer> populationTrend` |  |
| 1308 | `private double cityCapitalSpending` |  |
| 1309 | `private double monthlyMaterialImports` |  |
| 1316 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1317 | `private int materialsConsumed` |  |
| 1340 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1357 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1368 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1451 | **type** `public class DataSave` |  |
| 120 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 121 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 122 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 123 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 132-145)

### the shedding warning (lines 146-270)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 219 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 223 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 227 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 271-531)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 1 | `public void setSlotName(String name)` |  |
| 284 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 285 | 1 | `public double[] getTreasuryMonth()` |  |
| 325 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 329 | 1 | `public double getCityPaperUnsettled()` |  |
| 330 | 1 | `public double getCityDiscountUnsettled()` |  |
| 332 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 339 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 340 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 341 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 342 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 343 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 344 | 1 | `public double getTreasuryRaisedPending()` |  |
| 357 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 358 | 1 | `public double[] getGovernmentMonth()` |  |
| 359 | 1 | `public String getSlotName()` |  |
| 362 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 368 | 1 | `public String getGameVersion()` |  |
| 369 | 1 | `public int getSaveFormat()` |  |
| 370 | 1 | `public long getSavedAt()` |  |
| 372 | 1 | `public void setHouseholdSavings(double value)` |  |
| 373 | 1 | `public double getHouseholdSavings()` |  |
| 375 | 1 | `public void setLandOwned(double sqFt)` |  |
| 376 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 377 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 379 | 1 | `public double getLandOwned()` |  |
| 380 | 1 | `public int getLandBlocksPurchased()` |  |
| 381 | 1 | `public double getLandPricePerSqFt()` |  |
| 383 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 384 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 386 | 1 | `public double getIncomeTaxRate()` |  |
| 387 | 1 | `public double getPropertyTaxRate()` |  |
| 389 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 394 | 3 | `public void setCash(double money)` | setters |
| 398 | 3 | `public void setMonth(int month)` |  |
| 402 | 3 | `public void setBuildingNum(int i)` |  |
| 406 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 413 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 418 | 3 | `public void setProgress(double[] progress)` |  |
| 422 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 426 | 3 | `public void setPopulation(int population)` |  |
| 429 | 3 | `public void setReports(boolean reports)` |  |
| 442 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 445 | 3 | `public void setGraphs(boolean graphs)` |  |
| 474 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 499 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 525 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 532-581)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 542 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 545 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 549 | 3 | `public int getConstructionByIdLength()` |  |
| 553 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 560 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 567 | 6 | `public double getContractValueById(int templateId)` |  |
| 575 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 582-607)

| line | len | member | says |
|---:|---:|---|---|
| 584 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 585 | 1 | `public double getPropertyTaxCharged()` |  |
| 587 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 588 | 1 | `public double getCityInterestAccrued()` |  |
| 590 | 1 | `public void setWorkforce(int workforce)` |  |
| 593 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 595 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 601 | 1 | `public double[] getLandListing()` |  |
| 602 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 603 | 1 | `public double[] getLandMarketPrices()` |  |
| 604 | 1 | `public int getIronDeposits()` |  |
| 605 | 1 | `public double getIronReserveTonnes()` |  |

### policy (lines 608-1462)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 672 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 673 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 674 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 675 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 676 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 678 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 679 | 1 | `public double[] getTaxPolicyState()` |  |
| 681 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 682 | 1 | `public double[] getHouseholdBalance()` |  |
| 684 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 688 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 689 | 1 | `public double[] getHouseholdCells()` |  |
| 691 | 1 | `public void setBankCash(double cash)` |  |
| 692 | 1 | `public double getBankCash()` |  |
| 705 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 706 | 1 | `public double getBankBranchesCapitalised()` |  |
| 717 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 718 | 1 | `public double getBankProfitLastMonth()` |  |
| 722 | 1 | `public void setBankDepositRate(double v)` |  |
| 723 | 1 | `public double getBankDepositRate()` |  |
| 739 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 740 | 1 | `public double[] getForeignAccounts()` |  |
| 752 | 1 | `public void setCentralBank(double[] state)` |  |
| 753 | 1 | `public double[] getCentralBank()` |  |
| 764 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 768 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 769 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 781 | 1 | `public void setForeignStanding(double[] state)` |  |
| 782 | 1 | `public double[] getForeignStanding()` |  |
| 793 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 794 | 1 | `public double[] getCapitalFlows()` |  |
| 804 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 805 | 1 | `public double[] getOutwardInvestment()` |  |
| 816 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 817 | 1 | `public String[] getEquityKeys()` |  |
| 818 | 1 | `public double[] getEquity()` |  |
| 822 | 1 | `public void setExchange(double[] state)` |  |
| 823 | 1 | `public double[] getExchange()` |  |
| 837 | 1 | `public void setBankSolvency(double[] state)` |  |
| 838 | 1 | `public double[] getBankSolvency()` |  |
| 856 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 857 | 1 | `public double[] getHousingOccupancy()` |  |
| 870 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 871 | 1 | `public double[] getBankLastMonth()` |  |
| 882 | 1 | `public void setPriceIndex(double[] state)` |  |
| 883 | 1 | `public double[] getPriceIndex()` |  |
| 895 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 896 | 1 | `public double[] getWorldEconomy()` |  |
| 911 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 912 | 1 | `public double getTradedExchangeRate()` |  |
| 926 | 1 | `public void setDenomination(double[] state)` |  |
| 927 | 1 | `public double[] getDenomination()` |  |
| 941 | 1 | `public void setPolicyRate(double rate)` |  |
| 943 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 952 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 953 | 1 | `public boolean getPolicyAutopilot()` |  |
| 964 | 1 | `public void setQeTargetShare(double share)` |  |
| 965 | 1 | `public double getQeTargetShare()` |  |
| 978 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 980 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 991 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 992 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1003 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1007 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1008 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1032 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1033 | 1 | `public double getCarsPerHousehold()` |  |
| 1035 | 1 | `public void setRememberedCommute(double v)` |  |
| 1036 | 3 | `public double getRememberedCommute()` |  |
| 1049 | 1 | `public void setCostOfLiving(double v)` |  |
| 1050 | 1 | `public double getCostOfLiving()` |  |
| 1063 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1064 | 1 | `public double getBankTaxCharged()` |  |
| 1068 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1069 | 1 | `public double getRentWeight()` |  |
| 1089 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1090 | 1 | `public double getRentWeightStudio()` |  |
| 1092 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1096 | 1 | `public int getConstructionShedMonth()` |  |
| 1097 | 1 | `public double getConstructionShedPoints()` |  |
| 1099 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1102 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1246 | 1 | `public void setLicences(double[] a)` |  |
| 1247 | 1 | `public double[] getLicences()` |  |
| 1248 | 1 | `public void setEducation(double[] a)` |  |
| 1249 | 1 | `public double[] getEducation()` |  |
| 1251 | 1 | `public void setLabour(double[] a)` |  |
| 1252 | 1 | `public double[] getLabour()` |  |
| 1253 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1254 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1256 | 1 | `public void setCohorts(double[] a)` |  |
| 1257 | 1 | `public double[] getCohorts()` |  |
| 1260 | 1 | `public void setBandNames(String[] names)` |  |
| 1262 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1276 | 1 | `public void setShapeNames(String[] names)` |  |
| 1277 | 1 | `public String[] getShapeNames()` |  |
| 1278 | 1 | `public void setFamilies(double[] a)` |  |
| 1279 | 1 | `public double[] getFamilies()` |  |
| 1280 | 1 | `public void setMigration(double[] a)` |  |
| 1281 | 1 | `public double[] getMigration()` |  |
| 1282 | 1 | `public void setUnemployment(double[] a)` |  |
| 1283 | 1 | `public double[] getUnemployment()` |  |
| 1284 | 1 | `public void setSickness(double[] a)` |  |
| 1285 | 1 | `public double[] getSickness()` |  |
| 1286 | 1 | `public void setCrime(double[] a)` |  |
| 1287 | 1 | `public double[] getCrime()` |  |
| 1288 | 1 | `public void setHealth(double[] a)` |  |
| 1289 | 1 | `public double[] getHealth()` |  |
| 1290 | 1 | `public void setHealthcare(double[] a)` |  |
| 1291 | 1 | `public double[] getHealthcare()` |  |
| 1319 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1320 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1321 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1322 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1323 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1324 | 1 | `public double getCityCapitalSpending()` |  |
| 1342 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1343 | 1 | `public double getCityMaintenancePaid()` |  |
| 1344 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1345 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1346 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1347 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1348 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1349 | 1 | `public int getMaterialsConsumed()` |  |
| 1358 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1359 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1369 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1370 | 1 | `public double[] getHouseholdStatement()` |  |
| 1372 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1375 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1377 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1380 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1382 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1385 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1387 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1390 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1392 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1393 | 1 | `public double[] getNationalAccounts()` |  |
| 1403 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1406 | 3 | `public int getUnderConstruction(int index)` |  |
| 1409 | 3 | `public double getCash()` |  |
| 1413 | 3 | `public int getMonth()` |  |
| 1417 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1421 | 3 | `public int getBuildingsLength()` |  |
| 1425 | 3 | `public JsonArray getDebt()` |  |
| 1429 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1434 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1438 | 3 | `public int getProgressLength()` |  |
| 1442 | 3 | `public double getProgress(int index)` |  |
| 1446 | 3 | `public int getConstructionMaterials()` |  |
| 1450 | 3 | `public int getPopulation()` |  |
| 1453 | 3 | `public boolean getReports()` |  |
| 1456 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1459 | 3 | `public boolean getGraphs()` |  |


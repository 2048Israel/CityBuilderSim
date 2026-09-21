# DataSave.java - 1,343 lines · 213 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Notice](Notice.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | · land, ore and the retainer |
| 148 | · the shedding warning |
| 273 | · the header |
| 515 | · construction, by id |
| 565 | · charged, not derived |
| 593 | · policy |

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
| 146 | `private double constructionSubsidy` |  |
| 164 | `private int constructionShedMonth` |  |
| 165 | `private double constructionShedPoints` |  |
| 172 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 182 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 189 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 190 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 198 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 199 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 202 | `private int constructionMaterials` | The city's own yard, in units. |
| 203 | `private int population` |  |
| 214 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 215 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 234 | `private boolean reports` | settings |
| 235 | `private boolean graphs` |  |
| 246 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 256 | `private double landOwned` | Land. |
| 257 | `private int landBlocksPurchased` |  |
| 258 | `private double landPricePerSqFt` |  |
| 267 | `private double incomeTaxRate` | Tax rates. |
| 268 | `private double propertyTaxRate` |  |
| 284 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 302 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 303 | `private double[] treasuryJournalAmounts` |  |
| 304 | `private String[] treasuryJournalPendingLabels` |  |
| 305 | `private double[] treasuryJournalPendingAmounts` |  |
| 313 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 338 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 603 | `private double[] taxPolicyState` |  |
| 613 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 625 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 626 | `private double[] householdCells` |  |
| 638 | `private double bankCash` | The bank's cash. |
| 648 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 651 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 652 | `private SalesTaxLedger.State salesTax` |  |
| 654 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 688 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 700 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 706 | `private double bankDepositRate` | What savers were paid. |
| 722 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 735 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 747 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 758 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 769 | `private String[] equityKeys` | The share register, company by company, named. |
| 770 | `private double[] equity` |  |
| 777 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 791 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate, and whether it is frozen right now. |
| 810 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 824 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 836 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 849 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 865 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 880 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 886 | `private double policyRate` | The policy rate. |
| 905 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 911 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 928 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 942 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 968 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 997 | `private double[] cohorts` | The demographics. |
| 1021 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1023 | `private double[] families` |  |
| 1024 | `private double[] migration` |  |
| 1034 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1043 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1053 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1066 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1079 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1095 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1107 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1124 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1125 | `private double[] education` |  |
| 1154 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1187 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1188 | `private java.util.List<Integer> populationTrend` |  |
| 1189 | `private double cityCapitalSpending` |  |
| 1190 | `private double monthlyMaterialImports` |  |
| 1197 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1198 | `private int materialsConsumed` |  |
| 1221 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1238 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1249 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1332 | **type** `public class DataSave` |  |
| 120 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 121 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 122 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 123 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land, ore and the retainer (lines 132-147)

### the shedding warning (lines 148-272)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 221 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 225 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 229 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 273-514)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 1 | `public void setSlotName(String name)` |  |
| 286 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 287 | 1 | `public double[] getTreasuryMonth()` |  |
| 315 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 322 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 323 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 324 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 325 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 326 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 327 | 1 | `public double getTreasuryRaisedPending()` |  |
| 340 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 341 | 1 | `public double[] getGovernmentMonth()` |  |
| 342 | 1 | `public String getSlotName()` |  |
| 345 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 351 | 1 | `public String getGameVersion()` |  |
| 352 | 1 | `public int getSaveFormat()` |  |
| 353 | 1 | `public long getSavedAt()` |  |
| 355 | 1 | `public void setHouseholdSavings(double value)` |  |
| 356 | 1 | `public double getHouseholdSavings()` |  |
| 358 | 1 | `public void setLandOwned(double sqFt)` |  |
| 359 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 360 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 362 | 1 | `public double getLandOwned()` |  |
| 363 | 1 | `public int getLandBlocksPurchased()` |  |
| 364 | 1 | `public double getLandPricePerSqFt()` |  |
| 366 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 367 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 369 | 1 | `public double getIncomeTaxRate()` |  |
| 370 | 1 | `public double getPropertyTaxRate()` |  |
| 372 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 377 | 3 | `public void setCash(double money)` | setters |
| 381 | 3 | `public void setMonth(int month)` |  |
| 385 | 3 | `public void setBuildingNum(int i)` |  |
| 389 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 396 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 401 | 3 | `public void setProgress(double[] progress)` |  |
| 405 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 409 | 3 | `public void setPopulation(int population)` |  |
| 412 | 3 | `public void setReports(boolean reports)` |  |
| 425 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 428 | 3 | `public void setGraphs(boolean graphs)` |  |
| 457 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 482 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 508 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 515-564)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 525 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 528 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 532 | 3 | `public int getConstructionByIdLength()` |  |
| 536 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 543 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 550 | 6 | `public double getContractValueById(int templateId)` |  |
| 558 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 565-592)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 568 | 1 | `public double getPropertyTaxCharged()` |  |
| 570 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 571 | 1 | `public double getCityInterestAccrued()` |  |
| 573 | 1 | `public void setWorkforce(int workforce)` |  |
| 576 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 578 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 584 | 1 | `public double[] getLandListing()` |  |
| 585 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 586 | 1 | `public double[] getLandMarketPrices()` |  |
| 587 | 1 | `public int getIronDeposits()` |  |
| 588 | 1 | `public double getIronReserveTonnes()` |  |
| 590 | 1 | `public void setConstructionSubsidy(double amount)` |  |
| 591 | 1 | `public double getConstructionSubsidy()` |  |

### policy (lines 593-1343)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 657 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 658 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 659 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 660 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 661 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 663 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 664 | 1 | `public double[] getTaxPolicyState()` |  |
| 666 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 667 | 1 | `public double[] getHouseholdBalance()` |  |
| 669 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 673 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 674 | 1 | `public double[] getHouseholdCells()` |  |
| 676 | 1 | `public void setBankCash(double cash)` |  |
| 677 | 1 | `public double getBankCash()` |  |
| 690 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 691 | 1 | `public double getBankBranchesCapitalised()` |  |
| 702 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 703 | 1 | `public double getBankProfitLastMonth()` |  |
| 707 | 1 | `public void setBankDepositRate(double v)` |  |
| 708 | 1 | `public double getBankDepositRate()` |  |
| 724 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 725 | 1 | `public double[] getForeignAccounts()` |  |
| 737 | 1 | `public void setForeignStanding(double[] state)` |  |
| 738 | 1 | `public double[] getForeignStanding()` |  |
| 749 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 750 | 1 | `public double[] getCapitalFlows()` |  |
| 760 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 761 | 1 | `public double[] getOutwardInvestment()` |  |
| 772 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 773 | 1 | `public String[] getEquityKeys()` |  |
| 774 | 1 | `public double[] getEquity()` |  |
| 778 | 1 | `public void setExchange(double[] state)` |  |
| 779 | 1 | `public double[] getExchange()` |  |
| 793 | 1 | `public void setBankSolvency(double[] state)` |  |
| 794 | 1 | `public double[] getBankSolvency()` |  |
| 812 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 813 | 1 | `public double[] getHousingOccupancy()` |  |
| 826 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 827 | 1 | `public double[] getBankLastMonth()` |  |
| 838 | 1 | `public void setPriceIndex(double[] state)` |  |
| 839 | 1 | `public double[] getPriceIndex()` |  |
| 851 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 852 | 1 | `public double[] getWorldEconomy()` |  |
| 867 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 868 | 1 | `public double getTradedExchangeRate()` |  |
| 882 | 1 | `public void setDenomination(double[] state)` |  |
| 883 | 1 | `public double[] getDenomination()` |  |
| 888 | 1 | `public void setPolicyRate(double rate)` |  |
| 889 | 1 | `public double getPolicyRate()` |  |
| 913 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 914 | 1 | `public double getCarsPerHousehold()` |  |
| 916 | 1 | `public void setRememberedCommute(double v)` |  |
| 917 | 3 | `public double getRememberedCommute()` |  |
| 930 | 1 | `public void setCostOfLiving(double v)` |  |
| 931 | 1 | `public double getCostOfLiving()` |  |
| 944 | 1 | `public void setBankTaxCharged(double v)` |  |
| 945 | 1 | `public double getBankTaxCharged()` |  |
| 949 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 950 | 1 | `public double getRentWeight()` |  |
| 970 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 971 | 1 | `public double getRentWeightStudio()` |  |
| 973 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 977 | 1 | `public int getConstructionShedMonth()` |  |
| 978 | 1 | `public double getConstructionShedPoints()` |  |
| 980 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 983 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1127 | 1 | `public void setLicences(double[] a)` |  |
| 1128 | 1 | `public double[] getLicences()` |  |
| 1129 | 1 | `public void setEducation(double[] a)` |  |
| 1130 | 1 | `public double[] getEducation()` |  |
| 1132 | 1 | `public void setLabour(double[] a)` |  |
| 1133 | 1 | `public double[] getLabour()` |  |
| 1134 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1135 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1137 | 1 | `public void setCohorts(double[] a)` |  |
| 1138 | 1 | `public double[] getCohorts()` |  |
| 1141 | 1 | `public void setBandNames(String[] names)` |  |
| 1143 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1157 | 1 | `public void setShapeNames(String[] names)` |  |
| 1158 | 1 | `public String[] getShapeNames()` |  |
| 1159 | 1 | `public void setFamilies(double[] a)` |  |
| 1160 | 1 | `public double[] getFamilies()` |  |
| 1161 | 1 | `public void setMigration(double[] a)` |  |
| 1162 | 1 | `public double[] getMigration()` |  |
| 1163 | 1 | `public void setUnemployment(double[] a)` |  |
| 1164 | 1 | `public double[] getUnemployment()` |  |
| 1165 | 1 | `public void setSickness(double[] a)` |  |
| 1166 | 1 | `public double[] getSickness()` |  |
| 1167 | 1 | `public void setCrime(double[] a)` |  |
| 1168 | 1 | `public double[] getCrime()` |  |
| 1169 | 1 | `public void setHealth(double[] a)` |  |
| 1170 | 1 | `public double[] getHealth()` |  |
| 1171 | 1 | `public void setHealthcare(double[] a)` |  |
| 1172 | 1 | `public double[] getHealthcare()` |  |
| 1200 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1201 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1202 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1203 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1204 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1205 | 1 | `public double getCityCapitalSpending()` |  |
| 1223 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1224 | 1 | `public double getCityMaintenancePaid()` |  |
| 1225 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1226 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1227 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1228 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1229 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1230 | 1 | `public int getMaterialsConsumed()` |  |
| 1239 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1240 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1250 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1251 | 1 | `public double[] getHouseholdStatement()` |  |
| 1253 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1256 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1258 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1261 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1263 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1266 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1268 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1271 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1273 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1274 | 1 | `public double[] getNationalAccounts()` |  |
| 1284 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1287 | 3 | `public int getUnderConstruction(int index)` |  |
| 1290 | 3 | `public double getCash()` |  |
| 1294 | 3 | `public int getMonth()` |  |
| 1298 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1302 | 3 | `public int getBuildingsLength()` |  |
| 1306 | 3 | `public JsonArray getDebt()` |  |
| 1310 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1315 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1319 | 3 | `public int getProgressLength()` |  |
| 1323 | 3 | `public double getProgress(int index)` |  |
| 1327 | 3 | `public int getConstructionMaterials()` |  |
| 1331 | 3 | `public int getPopulation()` |  |
| 1334 | 3 | `public boolean getReports()` |  |
| 1337 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1340 | 3 | `public boolean getGraphs()` |  |


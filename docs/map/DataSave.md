# DataSave.java - 1,301 lines · 206 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Notice](Notice.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [Game](Game.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | · land, ore and the retainer |
| 148 | · the shedding warning |
| 273 | · the header |
| 475 | · construction, by id |
| 525 | · charged, not derived |
| 553 | · policy |

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
| 298 | `private double[] governmentMonth` | The government's own month: seventeen revenue and spending lines, saved and restored as one. |
| 563 | `private double[] taxPolicyState` |  |
| 573 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 585 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 586 | `private double[] householdCells` |  |
| 598 | `private double bankCash` | The bank's cash. |
| 608 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 611 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 612 | `private SalesTaxLedger.State salesTax` |  |
| 614 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 648 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 660 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 666 | `private double bankDepositRate` | What savers were paid. |
| 680 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 693 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 705 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 716 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 727 | `private String[] equityKeys` | The share register, company by company, named. |
| 728 | `private double[] equity` |  |
| 735 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 749 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate, and whether it is frozen right now. |
| 768 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 782 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 794 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 807 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 823 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 838 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 844 | `private double policyRate` | The policy rate. |
| 863 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 869 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 886 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 900 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 926 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 955 | `private double[] cohorts` | The demographics. |
| 979 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 981 | `private double[] families` |  |
| 982 | `private double[] migration` |  |
| 992 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1001 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1011 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1024 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1037 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1053 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1065 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1082 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1083 | `private double[] education` |  |
| 1112 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1145 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1146 | `private java.util.List<Integer> populationTrend` |  |
| 1147 | `private double cityCapitalSpending` |  |
| 1148 | `private double monthlyMaterialImports` |  |
| 1155 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1156 | `private int materialsConsumed` |  |
| 1179 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1196 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1207 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1290 | **type** `public class DataSave` |  |
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

### the header (lines 273-474)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 1 | `public void setSlotName(String name)` |  |
| 286 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 287 | 1 | `public double[] getTreasuryMonth()` |  |
| 300 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 301 | 1 | `public double[] getGovernmentMonth()` |  |
| 302 | 1 | `public String getSlotName()` |  |
| 305 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 311 | 1 | `public String getGameVersion()` |  |
| 312 | 1 | `public int getSaveFormat()` |  |
| 313 | 1 | `public long getSavedAt()` |  |
| 315 | 1 | `public void setHouseholdSavings(double value)` |  |
| 316 | 1 | `public double getHouseholdSavings()` |  |
| 318 | 1 | `public void setLandOwned(double sqFt)` |  |
| 319 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 320 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 322 | 1 | `public double getLandOwned()` |  |
| 323 | 1 | `public int getLandBlocksPurchased()` |  |
| 324 | 1 | `public double getLandPricePerSqFt()` |  |
| 326 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 327 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 329 | 1 | `public double getIncomeTaxRate()` |  |
| 330 | 1 | `public double getPropertyTaxRate()` |  |
| 332 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 337 | 3 | `public void setCash(double money)` | setters |
| 341 | 3 | `public void setMonth(int month)` |  |
| 345 | 3 | `public void setBuildingNum(int i)` |  |
| 349 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 356 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 361 | 3 | `public void setProgress(double[] progress)` |  |
| 365 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 369 | 3 | `public void setPopulation(int population)` |  |
| 372 | 3 | `public void setReports(boolean reports)` |  |
| 385 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 388 | 3 | `public void setGraphs(boolean graphs)` |  |
| 417 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 442 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 468 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 475-524)

| line | len | member | says |
|---:|---:|---|---|
| 477 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 485 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 488 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 492 | 3 | `public int getConstructionByIdLength()` |  |
| 496 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 503 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 510 | 6 | `public double getContractValueById(int templateId)` |  |
| 518 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 525-552)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 528 | 1 | `public double getPropertyTaxCharged()` |  |
| 530 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 531 | 1 | `public double getCityInterestAccrued()` |  |
| 533 | 1 | `public void setWorkforce(int workforce)` |  |
| 536 | 1 | `public int getWorkforce()` | -1 when the save predates this field. |
| 538 | 5 | `public void setLandState(double[] listing, int deposits, double reserveTonnes)` |  |
| 544 | 1 | `public double[] getLandListing()` |  |
| 545 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 546 | 1 | `public double[] getLandMarketPrices()` |  |
| 547 | 1 | `public int getIronDeposits()` |  |
| 548 | 1 | `public double getIronReserveTonnes()` |  |
| 550 | 1 | `public void setConstructionSubsidy(double amount)` |  |
| 551 | 1 | `public double getConstructionSubsidy()` |  |

### policy (lines 553-1301)

| line | len | member | says |
|---:|---:|---|---|
| 616 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 617 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 618 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 619 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 620 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 621 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 623 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 624 | 1 | `public double[] getTaxPolicyState()` |  |
| 626 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 627 | 1 | `public double[] getHouseholdBalance()` |  |
| 629 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 633 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 634 | 1 | `public double[] getHouseholdCells()` |  |
| 636 | 1 | `public void setBankCash(double cash)` |  |
| 637 | 1 | `public double getBankCash()` |  |
| 650 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 651 | 1 | `public double getBankBranchesCapitalised()` |  |
| 662 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 663 | 1 | `public double getBankProfitLastMonth()` |  |
| 667 | 1 | `public void setBankDepositRate(double v)` |  |
| 668 | 1 | `public double getBankDepositRate()` |  |
| 682 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 683 | 1 | `public double[] getForeignAccounts()` |  |
| 695 | 1 | `public void setForeignStanding(double[] state)` |  |
| 696 | 1 | `public double[] getForeignStanding()` |  |
| 707 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 708 | 1 | `public double[] getCapitalFlows()` |  |
| 718 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 719 | 1 | `public double[] getOutwardInvestment()` |  |
| 730 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 731 | 1 | `public String[] getEquityKeys()` |  |
| 732 | 1 | `public double[] getEquity()` |  |
| 736 | 1 | `public void setExchange(double[] state)` |  |
| 737 | 1 | `public double[] getExchange()` |  |
| 751 | 1 | `public void setBankSolvency(double[] state)` |  |
| 752 | 1 | `public double[] getBankSolvency()` |  |
| 770 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 771 | 1 | `public double[] getHousingOccupancy()` |  |
| 784 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 785 | 1 | `public double[] getBankLastMonth()` |  |
| 796 | 1 | `public void setPriceIndex(double[] state)` |  |
| 797 | 1 | `public double[] getPriceIndex()` |  |
| 809 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 810 | 1 | `public double[] getWorldEconomy()` |  |
| 825 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 826 | 1 | `public double getTradedExchangeRate()` |  |
| 840 | 1 | `public void setDenomination(double[] state)` |  |
| 841 | 1 | `public double[] getDenomination()` |  |
| 846 | 1 | `public void setPolicyRate(double rate)` |  |
| 847 | 1 | `public double getPolicyRate()` |  |
| 871 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 872 | 1 | `public double getCarsPerHousehold()` |  |
| 874 | 1 | `public void setRememberedCommute(double v)` |  |
| 875 | 3 | `public double getRememberedCommute()` |  |
| 888 | 1 | `public void setCostOfLiving(double v)` |  |
| 889 | 1 | `public double getCostOfLiving()` |  |
| 902 | 1 | `public void setBankTaxCharged(double v)` |  |
| 903 | 1 | `public double getBankTaxCharged()` |  |
| 907 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 908 | 1 | `public double getRentWeight()` |  |
| 928 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 929 | 1 | `public double getRentWeightStudio()` |  |
| 931 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 935 | 1 | `public int getConstructionShedMonth()` |  |
| 936 | 1 | `public double getConstructionShedPoints()` |  |
| 938 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 941 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1085 | 1 | `public void setLicences(double[] a)` |  |
| 1086 | 1 | `public double[] getLicences()` |  |
| 1087 | 1 | `public void setEducation(double[] a)` |  |
| 1088 | 1 | `public double[] getEducation()` |  |
| 1090 | 1 | `public void setLabour(double[] a)` |  |
| 1091 | 1 | `public double[] getLabour()` |  |
| 1092 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1093 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1095 | 1 | `public void setCohorts(double[] a)` |  |
| 1096 | 1 | `public double[] getCohorts()` |  |
| 1099 | 1 | `public void setBandNames(String[] names)` |  |
| 1101 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1115 | 1 | `public void setShapeNames(String[] names)` |  |
| 1116 | 1 | `public String[] getShapeNames()` |  |
| 1117 | 1 | `public void setFamilies(double[] a)` |  |
| 1118 | 1 | `public double[] getFamilies()` |  |
| 1119 | 1 | `public void setMigration(double[] a)` |  |
| 1120 | 1 | `public double[] getMigration()` |  |
| 1121 | 1 | `public void setUnemployment(double[] a)` |  |
| 1122 | 1 | `public double[] getUnemployment()` |  |
| 1123 | 1 | `public void setSickness(double[] a)` |  |
| 1124 | 1 | `public double[] getSickness()` |  |
| 1125 | 1 | `public void setCrime(double[] a)` |  |
| 1126 | 1 | `public double[] getCrime()` |  |
| 1127 | 1 | `public void setHealth(double[] a)` |  |
| 1128 | 1 | `public double[] getHealth()` |  |
| 1129 | 1 | `public void setHealthcare(double[] a)` |  |
| 1130 | 1 | `public double[] getHealthcare()` |  |
| 1158 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1159 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1160 | 1 | `public void setPopulationTrend(java.util.List<Integer> l)` |  |
| 1161 | 1 | `public java.util.List<Integer> getPopulationTrend()` |  |
| 1162 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1163 | 1 | `public double getCityCapitalSpending()` |  |
| 1181 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1182 | 1 | `public double getCityMaintenancePaid()` |  |
| 1183 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1184 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1185 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1186 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1187 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1188 | 1 | `public int getMaterialsConsumed()` |  |
| 1197 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1198 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1208 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1209 | 1 | `public double[] getHouseholdStatement()` |  |
| 1211 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1214 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1216 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1219 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1221 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1224 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1226 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1229 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1231 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1232 | 1 | `public double[] getNationalAccounts()` |  |
| 1242 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1245 | 3 | `public int getUnderConstruction(int index)` |  |
| 1248 | 3 | `public double getCash()` |  |
| 1252 | 3 | `public int getMonth()` |  |
| 1256 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1260 | 3 | `public int getBuildingsLength()` |  |
| 1264 | 3 | `public JsonArray getDebt()` |  |
| 1268 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1273 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1277 | 3 | `public int getProgressLength()` |  |
| 1281 | 3 | `public double getProgress(int index)` |  |
| 1285 | 3 | `public int getConstructionMaterials()` |  |
| 1289 | 3 | `public int getPopulation()` |  |
| 1292 | 3 | `public boolean getReports()` |  |
| 1295 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1298 | 3 | `public boolean getGraphs()` |  |


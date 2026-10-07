# DataSave.java - 1,992 lines · 321 methods · 0 constants · model

`ham/citybuildersim/DataSave.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [SectorBooks](SectorBooks.md) (6), [Founding](Founding.md) (6), [Rollover](Rollover.md) (5), [Currency](Currency.md) (4), [Notice](Notice.md) (3), [BuildingManager](BuildingManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [DecisionLog](DecisionLog.md) (3), [SectorState](SectorState.md) (3), [Markets](Markets.md) (3), [DemolitionLog](DemolitionLog.md) (3), [BuildLog](BuildLog.md) (3), [GameFiles](GameFiles.md) (3), [SalesTaxLedger](SalesTaxLedger.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Exchange](Exchange.md) (3), [Bank](Bank.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BondMarket](BondMarket.md) (3), [DebtManager](DebtManager.md) (2), [Debt](Debt.md) (1), [BusinessDebt](BusinessDebt.md) (1)

**Used by (4):** [Game](Game.md), [MapCheck](MapCheck.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 179 | · land and ore |
| 227 | · the shedding warning |
| 374 | · the header |
| 682 | · construction, by id |
| 758 | · charged, not derived |
| 813 | · policy |

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
| 200 | `private Double worldSeaTheta` |  |
| 201 | `private double[] worldTotals` |  |
| 202 | `private double[] landCentre` |  |
| 203 | `private double[] landLanes` |  |
| 204 | `private double[][] landPurchases` |  |
| 205 | `private double[][] landOffers` |  |
| 206 | `private Integer nextOfferId` |  |
| 207 | `private double[] depletion` |  |
| 208 | `private double[] landListing` |  |
| 210 | `private double[] landMarketPrices` | The office's struck prices and the unit it lists at - see LandMarket.getPriceState(). |
| 211 | `private Integer ironDeposits` |  |
| 212 | `private Double ironReserveTonnes` |  |
| 219 | `private Double freshRights` | The city's water rights, units a month of fresh water it may treat past what its lakes and river yield (0.7.59; Game, THE FRESH WATER LIMIT AND THE COAST). |
| 225 | `private Long mapStamp` | The stamp of the city map's sidecar written with this save (0.7.60, batch J3; CityMap.writeSidecar()): a load reads slot-NN-map.bin only when its stamp is this one. |
| 243 | `private int constructionShedMonth` |  |
| 244 | `private double constructionShedPoints` |  |
| 251 | `private double[] nationalAccounts` | The month's GDP, and the inventory level it was measured against. |
| 261 | `private java.util.List<DemolitionLog.Entry> demolitions` | Two records of things that HAPPENED, rather than things the city has. |
| 268 | `private java.util.List<BuildLog.Entry> builds` | The other half of that history: what the city GAINED. |
| 269 | `private java.util.Map<String, Double> writeOffTotals` |  |
| 277 | `private java.util.Map<String, Integer> restructureCounts` | The rest of the borrower's record: how many times each sector has been written down, and how many months of borrowing ban it has left. |
| 278 | `private java.util.Map<String, Integer> blockedMonths` |  |
| 286 | `private java.util.Map<String, double[]> creditStatements` | The last quarter of month-end readings the bank rates each sector on (0.7.8, round 3: BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST QUARTER), by sector name: {owed, owned, ...}, oldest first. |
| 298 | `private java.util.Map<String, Double> mortgageRepaid` | THE LANDLORDS' MORTGAGES (0.7.11): the principal their payments took in the month saved, by sector - a flow the Bank tab and the landlords' screen read the month after - and the city's insurance book: the premiums it ... |
| 299 | `private java.util.Map<String, Double> insuranceClaims` |  |
| 300 | `private double insurancePremiums` |  |
| 303 | `private int constructionMaterials` | The city's own yard, in units. |
| 304 | `private long population` |  |
| 315 | `private java.util.List<SectorBooks.SectorMonth> sectorBooks` | THE SECTOR STATEMENTS, this month and last. |
| 316 | `private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore` |  |
| 335 | `private boolean reports` | settings |
| 336 | `private boolean graphs` |  |
| 347 | `private double householdSavings` | What the residents have not spent, since the city was founded. |
| 357 | `private double landOwned` | Land. |
| 358 | `private int landBlocksPurchased` |  |
| 359 | `private double landPricePerSqFt` |  |
| 368 | `private double incomeTaxRate` | Tax rates. |
| 369 | `private double propertyTaxRate` |  |
| 432 | `private double[] treasuryMonth` | The last month the treasury closed: opening, closing, raised, repaid, surplus, and whether it happened at all. |
| 450 | `private String[] treasuryJournalLabels` | The treasury's journal: the non-budget movements by name, last month (what the bridge shows) and the month in progress, as two pairs of parallel arrays - a label and its amount in thousands, signed as the treasury see... |
| 451 | `private double[] treasuryJournalAmounts` |  |
| 452 | `private String[] treasuryJournalPendingLabels` |  |
| 453 | `private double[] treasuryJournalPendingAmounts` |  |
| 461 | `private double treasuryRaisedPending` | What the treasury has raised by issuing paper since the last strike - Game.treasuryRaisedSoFar - carried for the reason cityCapitalSpending is: a city saved between two presses has already raised what the next strike ... |
| 472 | `private double cityPaperUnsettled` | The city's own paper its bank has taken and not yet paid for, with the discount on it - Game.getCityPaperUnsettled() - carried since 2026-09-21 because the bank now PAYS for the paper at the settle after the issue, an... |
| 473 | `private double cityDiscountUnsettled` |  |
| 505 | `private double[] governmentMonth` | The government's own month: twenty-three revenue and spending lines, saved and restored as one. |
| 693 | `private int[] stackOrder` | THE ORDER THE CITY'S BUILDINGS STAND IN (0.7.43): their template ids in BuildingManager's list, which is the order each was first bought. |
| 823 | `private double[] taxPolicyState` |  |
| 836 | `private double[] householdBalance` | What the households have saved and what they owe, per pay tier. |
| 848 | `private String[] householdCellKeys` | The same stock, per CELL of the family matrix - one shape at one tier - since 2026-09-10, when the households became objects (Household, HouseholdBalance). |
| 849 | `private double[] householdCells` |  |
| 861 | `private double bankCash` | The bank's cash. |
| 871 | `private double rentWeight` | The housing match the month's rent was struck on. |
| 874 | `private java.util.List<String> subsidisedSectors` | The protected sectors, by name, and the month's VAT ledger by name. |
| 875 | `private SalesTaxLedger.State salesTax` |  |
| 877 | `private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets` | Every sector's three tax offsets, by name. |
| 911 | `private double bankBranchesCapitalised` | Branches the shareholders have already paid capital for. |
| 923 | `private double bankProfitLastMonth` | The bank's profit for the month this save was taken in. |
| 929 | `private double bankDepositRate` | What savers were paid. |
| 945 | `private double[] foreignAccounts` | The city's foreign position: reserves, the trailing import bill the cover is measured against, how many months have been counted into it, and the exchange rate. |
| 958 | `private double[] centralBank` | The central bank's balance sheet (0.7.0), under its own key: the two advances, the paper it holds, reserves and currency, the loss it carries, the remittance it owes, the lifetime totals and the trailing revenue its c... |
| 969 | `private String[] treasuryArrearsKeys` | What the treasury owes and has not paid (0.7.0): its arrears, as two parallel arrays - the ledger's key ("LINE" or "LINE:sector", see TreasuryLine) and the amount in thousands. |
| 970 | `private double[] treasuryArrearsAmounts` |  |
| 987 | `private double[] foreignStanding` | The city's standing with foreign lenders: the default scar and how long ago it was earned. |
| 999 | `private double[] capitalFlows` | Hot money: how much is here, and how long the city has left to sweat. |
| 1010 | `private double[] outwardInvestment` | The city's own savings abroad, per sector. |
| 1021 | `private String[] equityKeys` | The share register, company by company, named. |
| 1022 | `private double[] equity` |  |
| 1029 | `private double[] exchange` | The exchange's quotes, company by company, named. |
| 1034 | `private Exchange.State exchangeState` | The exchange on its order books (0.7.12 round 2): every company's book with its resting orders, fair value, the split factors and the record. |
| 1052 | `private double[] bankSolvency` | How often the bank has failed, what its creditors ate (before 0.7.14, when nothing absorbs a hole any more), whether it is frozen right now, and - on the end since 0.7.9 - what the city has put into it in rescues over... |
| 1071 | `private double[] housingOccupancy` | Doors that were lived in when the month's housing pass ran. |
| 1088 | `private double[] bankLastMonth` | The bank's last CLOSED month: payroll, upkeep, interest earned, book. |
| 1100 | `private double[] bankPricingHistory` | The record the bank's loan prices are struck from (0.7.7): a year of payroll and upkeep beside the book they served - flows, which no month's end state can give back. |
| 1112 | `private double bankLateProfit` | What the bank earned after its month's close (0.7.7) - the desk's re-mark, its dividends, the paper it bought from the households - which the next month's taxed profit carries. |
| 1127 | `private java.util.Map<String, double[]> bankAllowance` | What the bank has set aside against its books (0.7.8), book by book - each sector's name and Bank.HOUSEHOLD_BOOK to {the allowance, what it held when the month opened, what the month wrote off, whether it is in troubl... |
| 1139 | `private double[] bankCapitalRecord` | The record the bank's capital target is struck from (0.7.8): its worst year of provisions and the rings of the last year's provisions and weighted book, and the owners' year of dividends and buybacks. |
| 1150 | `private double[] bankMonthLines` | The bank's month, line by line (0.7.8): every flow its income statement, its equity's movement and its funding page read. |
| 1162 | `private double[] bankStatementYear` | The bank's year of statements (0.7.9): the months before the one saved, each filed whole at the top of the month after - what the Bank tab's last-month column and its last twelve months are read from. |
| 1175 | `private double[] bankSheetYear` | The bank's balance sheet at the top of each of the last twelve months (0.7.13): every line of it and its loans by sector, what the Balance sheet page's year-ago column is read from. |
| 1187 | `private Double bankPaidInOpening` | The bank's equity in two parts (0.7.13, round 2): its paid-in capital and its retained earnings at the top of the saved month, by name - the month's own causes are its statement lines (bankMonthLines). |
| 1188 | `private Double bankRetainedOpening` |  |
| 1201 | `private java.util.List<Bank.Preferred> bankPreferred` | The city's preferred in its bank (0.7.14), block by block - its par, the month it was issued, the dividend cap a share and its warrants - and the arrears with their record (Bank.preferredRecordToSave()). |
| 1202 | `private double[] bankPreferredRecord` |  |
| 1218 | `private TreasuryFund.State fund` | The city's fund (0.7.14), by name (TreasuryFund.State): its cash, its dial, the rescue setting, the bank's pending offer and its clocks, the rescues and the player's orders. |
| 1224 | `private double[] debtMarket` | The city's debt market as it last struck its rate (0.7.14): DebtManager.marketToSave(). |
| 1236 | `private double[] priceIndex` | The price basket, its weights, and a year of readings. |
| 1250 | `private double[] expectations` | The anchor (0.7.42): credibility, smoothed inflation, expected inflation, the expected price level and whether it was seeded, then the month's lean, the level its money constants are struck at and (0.7.45) the month's... |
| 1263 | `private double[] worldEconomy` | The world's price level and what it is rising at. |
| 1279 | `private double tradedExchangeRate` | THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign rate times the world's price level, struck at the top of the month and fanned out to the food market and the building manager. |
| 1294 | `private double[] denomination` | The currency's unit and how many reforms it has been through. |
| 1309 | `private Double policyRate` | The policy rate. |
| 1320 | `private Boolean policyAutopilot` | Whether the rule held the dial when this was saved (0.7.0) - see DebtManager's autopilot. |
| 1332 | `private String rolloverMode` | The treasury's rollover (0.7.13), by name: its setting, the ledger of what it netted from the year's surplus - {month, netted} pairs, so two months cannot net the same surplus twice across a reload - and its record (R... |
| 1333 | `private double[] rolloverLedger` |  |
| 1334 | `private double[] rolloverRecord` |  |
| 1354 | `private Boolean landPaidFromVault` | How the land office pays (0.7.6) - Game.isLandPaidFromVault(): true out of the vault's dollars, false converting cash. |
| 1367 | `private Double inflationTarget` | The inflation target the rule aims at (0.7.4) - DebtManager .getInflationTarget(). |
| 1379 | `private String policyStrictness` | How strictly the rule holds the target (0.7.52) - DebtManager .getStrictness(), by name, beside the target for the same reason. |
| 1392 | `private double qeTargetShare` | The central bank's holdings dial (0.7.1): the share of the city's term paper it aims to hold - CentralBank.getTargetShare(). |
| 1406 | `private Double advancesCeilingMonths` | The advances ceiling dial (0.7.2): the most the treasury may owe its central bank, in months of its revenue - CentralBank .getAdvancesCeilingMonths(). |
| 1419 | `private double householdPaperRatio` | The month's ratio for the households' city paper (0.7.1): their book at the curve over its face, which their plan reads - so it is carried, and the load path re-strikes the plan on the figure the live path used. |
| 1438 | `private BondMarket.State bondMarket` | THE BUSINESSES' BONDS (0.7.12): every bond and who holds it, every order book's resting orders, the market's month and its record over the city's life (BondMarket.State); what defaults have taken off the bonds, by sec... |
| 1439 | `private java.util.Map<String, Double> bondWrittenOff` |  |
| 1440 | `private double householdBondRatio` |  |
| 1441 | `private java.util.Map<String, java.util.Map<String, Double>> householdBondsByCell` |  |
| 1458 | `private double buybackToHouseholdsUnsettled` | What a buyback between two presses paid the households and a dollar bond's holders and the next month has not yet declared (0.7.1) - Game.getBuybackUnsettled() less the central bank's share, which rides in its own array. |
| 1459 | `private double buybackAbroadUnsettled` |  |
| 1482 | `private double rememberedCommute` | The commute the city REMEMBERS, which decides how many of its car owners get on a tram - see InfrastructureManager.noteCongestion(). |
| 1488 | `private double carsPerHousehold` | Cars per household, as the road read it. |
| 1502 | `private double transitBill` | The month's transit bill as advanceDemographics() 6d struck it (0.7.49): the buses' and trams' wages and upkeep. |
| 1515 | `private double captiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, and a journey's fuel in the month's money. |
| 1529 | `private double[] householdFuel` | ...and the drivers' fuel as 6d drew it (0.7.62, batch K): the bill, the part of it imported and the litres - a draw off the refiners' shelf is a month passing and cannot be run again by a load. |
| 1542 | `private double[] householdGraduates` | ...and the students who finished a course and wait for the next census to carry their loans to the working families, beside the ones who carried theirs at this month's (0.7.63; HouseholdBalance.graduatesToSave()). |
| 1559 | `private double costOfLiving` | How far wages have chased the cost of living. |
| 1573 | `private double bankTaxCharged` | The tax the city actually took off the bank this month. |
| 1599 | `private double rentWeightStudio` | The studio half of that weight, added 2026-09-09 with the segment split. |
| 1632 | `private double[] cohorts` | The demographics. |
| 1656 | `private String[] bandNames` | THE AGE BANDS THIS WHOLE SAVE WAS WRITTEN WITH (2026-09-15). |
| 1658 | `private double[] families` |  |
| 1659 | `private double[] migration` |  |
| 1669 | `private double[] unemployment` | The people out of work: the EI claims by the month they began, who is past EI, who has been evicted, and the month the flows were struck against (2026-09-11). |
| 1678 | `private double[] sickness` | Who has been sick how long: five rings of thirteen monthly shares, the last month's deaths from sickness by band, the recovery and whether the ring has been seeded (2026-09-11). |
| 1688 | `private double[] crime` | Crime and the prisons (2026-09-11): six monthly cohorts of prisoners, the month as it was struck - the rate next month's migration reads, the killings next month's pyramid reads - and the running totals. |
| 1701 | `private double[] health` | The month's sickness: the outbreak still decaying, and the rate the sectors were throttled by. |
| 1714 | `private double[] healthcare` | The health service: plots used, the unburied backlog, and the month's bill. |
| 1730 | `private double[] labour` | The minimum wage, then the eleven wages the city is actually paying. |
| 1742 | `private double[] skilledWorkforce` | How many skilled workers the city has, by band. |
| 1759 | `private double[] licences` | Who is licensed to practise what, and the schools' running total. |
| 1760 | `private double[] education` |  |
| 1789 | `private String[] shapeNames` | The household shapes this save was written with. |
| 1822 | `private java.util.Map<String, Integer> sectorLossMonths` | The private sector's memory, and the player's own turn. |
| 1823 | `private java.util.List<Long> populationTrend` |  |
| 1824 | `private double cityCapitalSpending` |  |
| 1825 | `private double monthlyMaterialImports` |  |
| 1832 | `private double monthlyMaterialImportBill` | The same imports in money, at the price each was charged at - the builders' materials expense and the accounts' import line. |
| 1833 | `private int materialsConsumed` |  |
| 1856 | `private double cityMaintenancePaid` | What the treasury paid the builders to keep the city's own buildings up. |
| 1873 | `private java.util.Map<String, Double> subsidyPaid` | What each protected sector was paid last month. |
| 1884 | `private double[] householdStatement` | The residents' month: twelve scalars and eleven per-tier arrays. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1981 | **type** `public class DataSave` |  |
| 167 | 1 | `public void setSectors(java.util.List<SectorState> s)` |  |
| 168 | 1 | `public java.util.List<SectorState> getSectors()` |  |
| 169 | 1 | `public void setMarkets(java.util.List<Markets.State> m)` |  |
| 170 | 1 | `public java.util.List<Markets.State> getMarkets()` |  |

### land and ore (lines 179-226)

### the shedding warning (lines 227-373)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 3 | `public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 322 | 3 | `public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books)` |  |
| 326 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooks()` |  |
| 330 | 3 | `public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore()` |  |

### the header (lines 374-681)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 1 | `public void setSlotName(String name)` |  |
| 379 | 12 | `public void setFounding(Founding f)` | The founding record, whole. |
| 402 | 16 | `public Founding getFounding(double meanInflation)` | The founding record this save carries, handed the world's mean the load has just restored. |
| 420 | 1 | `public Long getWorldSeed()` | The world seed as saved, or null on a save from before 0.7.56 - which getFounding() derives one for. |
| 423 | 1 | `public String getCityName()` | The city's name as saved, or null on a save from before 0.7.10. |
| 434 | 1 | `public void setTreasuryMonth(double[] state)` |  |
| 435 | 1 | `public double[] getTreasuryMonth()` |  |
| 475 | 4 | `public void setCityPaperUnsettled(double cash, double discount)` |  |
| 479 | 1 | `public double getCityPaperUnsettled()` |  |
| 480 | 1 | `public double getCityDiscountUnsettled()` |  |
| 482 | 7 | `public void setTreasuryJournal(String[] labels, double[] amounts, String[] pendingLabels, double[] pendingAmounts)` |  |
| 489 | 1 | `public String[] getTreasuryJournalLabels()` |  |
| 490 | 1 | `public double[] getTreasuryJournalAmounts()` |  |
| 491 | 1 | `public String[] getTreasuryJournalPendingLabels()` |  |
| 492 | 1 | `public double[] getTreasuryJournalPendingAmounts()` |  |
| 493 | 1 | `public void setTreasuryRaisedPending(double v)` |  |
| 494 | 1 | `public double getTreasuryRaisedPending()` |  |
| 507 | 1 | `public void setGovernmentMonth(double[] state)` |  |
| 508 | 1 | `public double[] getGovernmentMonth()` |  |
| 509 | 1 | `public String getSlotName()` |  |
| 512 | 5 | `public void stamp(String gameVersion, int saveFormat, long savedAt)` | Stamped at save time so a save always says which build wrote it. |
| 518 | 1 | `public String getGameVersion()` |  |
| 519 | 1 | `public int getSaveFormat()` |  |
| 520 | 1 | `public long getSavedAt()` |  |
| 522 | 1 | `public void setHouseholdSavings(double value)` |  |
| 523 | 1 | `public double getHouseholdSavings()` |  |
| 525 | 1 | `public void setLandOwned(double sqFt)` |  |
| 526 | 1 | `public void setLandBlocksPurchased(int blocks)` |  |
| 527 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 529 | 1 | `public double getLandOwned()` |  |
| 530 | 1 | `public int getLandBlocksPurchased()` |  |
| 531 | 1 | `public double getLandPricePerSqFt()` |  |
| 533 | 1 | `public void setIncomeTaxRate(double rate)` |  |
| 534 | 1 | `public void setPropertyTaxRate(double rate)` |  |
| 536 | 1 | `public double getIncomeTaxRate()` |  |
| 537 | 1 | `public double getPropertyTaxRate()` |  |
| 539 | 3 | `public void setUnderConstruction(int[] underConstruction)` |  |
| 544 | 3 | `public void setCash(double money)` | setters |
| 548 | 3 | `public void setMonth(int month)` |  |
| 552 | 3 | `public void setBuildingNum(int i)` |  |
| 556 | 6 | `public void setBuildingQuantity(int index, int quantity)` |  |
| 563 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 568 | 3 | `public void setProgress(double[] progress)` |  |
| 572 | 3 | `public void setConstructionMaterials(int constructionMaterials)` |  |
| 576 | 3 | `public void setPopulation(long population)` |  |
| 579 | 3 | `public void setReports(boolean reports)` |  |
| 592 | 3 | `public void setNotices(java.util.List<Notice> notices)` | The city's inbox. |
| 595 | 3 | `public void setGraphs(boolean graphs)` |  |
| 624 | 16 | `public GameFiles.Result saveGame(GameFiles files, int slot)` | Writes the save, and refuses to take the game down with it if it cannot. |
| 649 | 25 | `private String describeUnwritable()` | Names the field that broke, if it can find it. |
| 675 | 4 | `private void append(StringBuilder sb, String name, double value)` |  |

### construction, by id (lines 682-757)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 1 | `public void setStackOrder(int[] templateIds)` |  |
| 696 | 1 | `public int[] getStackOrder()` |  |
| 698 | 6 | `public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue)` |  |
| 706 | 1 | `public boolean hasContractsById()` | False on a save that kept one order book for the whole city. |
| 708 | 1 | `public void setContractRecords(java.util.List<BuildingManager.ContractRecord> records)` |  |
| 710 | 1 | `public java.util.List<BuildingManager.ContractRecord> getContractRecords()` | Null on a save from before 0.7.19. |
| 712 | 1 | `public void setConstructionControl(ConstructionControl.State state)` |  |
| 714 | 1 | `public ConstructionControl.State getConstructionControl()` | Null on a save from before 0.7.22 (format 27 and older). |
| 716 | 1 | `public void setDecisionLog(java.util.List<DecisionLog.Entry> log)` |  |
| 718 | 1 | `public java.util.List<DecisionLog.Entry> getDecisionLog()` | Null on a save from before 0.7.23 (format 28 and older). |
| 721 | 3 | `public boolean hasConstructionById()` | False for a save written before the format changed. |
| 725 | 3 | `public int getConstructionByIdLength()` |  |
| 729 | 6 | `public int getUnderConstructionById(int templateId)` |  |
| 736 | 6 | `public double getConstructionProgressById(int templateId)` |  |
| 743 | 6 | `public double getContractValueById(int templateId)` |  |
| 751 | 6 | `public double getMaterialsOwedById(int templateId)` | Zero on a save that has no record: its orders drew their material the day they were placed. |

### charged, not derived (lines 758-812)

| line | len | member | says |
|---:|---:|---|---|
| 760 | 1 | `public void setPropertyTaxCharged(double value)` |  |
| 761 | 1 | `public double getPropertyTaxCharged()` |  |
| 763 | 1 | `public void setCityInterestAccrued(double value)` |  |
| 764 | 1 | `public double getCityInterestAccrued()` |  |
| 766 | 1 | `public void setWorkforce(long workforce)` |  |
| 769 | 1 | `public long getWorkforce()` | -1 when the save predates this field. |
| 772 | 11 | `public void setCityLand(double[] centre, double[] lanes, double[][] purchases, double[][] offers, int nextOfferId, double[] dep...` | The city's land on the world, whole (0.7.57): see land and ore above. |
| 784 | 1 | `public double[] getLandCentre()` |  |
| 785 | 1 | `public double[] getLandLanes()` |  |
| 786 | 1 | `public double[][] getLandPurchases()` |  |
| 787 | 1 | `public double[][] getLandOffers()` |  |
| 789 | 1 | `public int getNextOfferId()` | The next offer's id, 1 on a save that has none. |
| 790 | 1 | `public double[] getDepletion()` |  |
| 791 | 1 | `public double[] getWorldTotals()` |  |
| 793 | 1 | `public Double getWorldSeaTheta()` | The world's sea level as stored, or null on a save from before 0.7.57. |
| 795 | 1 | `public void setFreshRights(double units)` |  |
| 797 | 1 | `public Double getFreshRights()` | The water rights as saved, or null on a save from before 0.7.59. |
| 799 | 1 | `public void setMapStamp(Long stamp)` |  |
| 801 | 1 | `public Long getMapStamp()` | The city map's sidecar stamp, or null when the city had no map (or the save is from before 0.7.60). |
| 804 | 1 | `public double[] getLandListing()` | An older save's nine parcels, read by nothing since 0.7.57 but kept to say what the save carried. |
| 805 | 1 | `public void setLandMarketPrices(double[] state)` |  |
| 806 | 1 | `public double[] getLandMarketPrices()` |  |
| 808 | 1 | `public int getIronDeposits()` | An older save's pool of iron sites, 0 when it carries none: what LandConversion reads. |
| 810 | 1 | `public double getIronReserveTonnes()` | ...and its tonnes. |

### policy (lines 813-1992)

| line | len | member | says |
|---:|---:|---|---|
| 879 | 1 | `public void setSubsidisedSectors(java.util.List<String> keys)` |  |
| 880 | 1 | `public java.util.List<String> getSubsidisedSectors()` |  |
| 881 | 1 | `public void setSalesTax(SalesTaxLedger.State state)` |  |
| 882 | 1 | `public SalesTaxLedger.State getSalesTax()` |  |
| 883 | 1 | `public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o)` |  |
| 884 | 1 | `public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()` |  |
| 886 | 1 | `public void setTaxPolicyState(double[] state)` |  |
| 887 | 1 | `public double[] getTaxPolicyState()` |  |
| 889 | 1 | `public void setHouseholdBalance(double[] state)` |  |
| 890 | 1 | `public double[] getHouseholdBalance()` |  |
| 892 | 4 | `public void setHouseholdCells(String[] keys, double[] state)` |  |
| 896 | 1 | `public String[] getHouseholdCellKeys()` |  |
| 897 | 1 | `public double[] getHouseholdCells()` |  |
| 899 | 1 | `public void setBankCash(double cash)` |  |
| 900 | 1 | `public double getBankCash()` |  |
| 913 | 1 | `public void setBankBranchesCapitalised(double n)` |  |
| 914 | 1 | `public double getBankBranchesCapitalised()` |  |
| 925 | 1 | `public void setBankProfitLastMonth(double v)` |  |
| 926 | 1 | `public double getBankProfitLastMonth()` |  |
| 930 | 1 | `public void setBankDepositRate(double v)` |  |
| 931 | 1 | `public double getBankDepositRate()` |  |
| 947 | 1 | `public void setForeignAccounts(double[] state)` |  |
| 948 | 1 | `public double[] getForeignAccounts()` |  |
| 960 | 1 | `public void setCentralBank(double[] state)` |  |
| 961 | 1 | `public double[] getCentralBank()` |  |
| 972 | 4 | `public void setTreasuryArrears(String[] keys, double[] amounts)` |  |
| 976 | 1 | `public String[] getTreasuryArrearsKeys()` |  |
| 977 | 1 | `public double[] getTreasuryArrearsAmounts()` |  |
| 989 | 1 | `public void setForeignStanding(double[] state)` |  |
| 990 | 1 | `public double[] getForeignStanding()` |  |
| 1001 | 1 | `public void setCapitalFlows(double[] state)` |  |
| 1002 | 1 | `public double[] getCapitalFlows()` |  |
| 1012 | 1 | `public void setOutwardInvestment(double[] state)` |  |
| 1013 | 1 | `public double[] getOutwardInvestment()` |  |
| 1024 | 1 | `public void setEquity(String[] keys, double[] state)` |  |
| 1025 | 1 | `public String[] getEquityKeys()` |  |
| 1026 | 1 | `public double[] getEquity()` |  |
| 1030 | 1 | `public void setExchange(double[] state)` |  |
| 1031 | 1 | `public double[] getExchange()` |  |
| 1035 | 1 | `public void setExchangeState(Exchange.State state)` |  |
| 1036 | 1 | `public Exchange.State getExchangeState()` |  |
| 1054 | 1 | `public void setBankSolvency(double[] state)` |  |
| 1055 | 1 | `public double[] getBankSolvency()` |  |
| 1073 | 1 | `public void setHousingOccupancy(double[] state)` |  |
| 1074 | 1 | `public double[] getHousingOccupancy()` |  |
| 1090 | 1 | `public void setBankLastMonth(double[] state)` |  |
| 1091 | 1 | `public double[] getBankLastMonth()` |  |
| 1102 | 1 | `public void setBankPricingHistory(double[] state)` |  |
| 1103 | 1 | `public double[] getBankPricingHistory()` |  |
| 1114 | 1 | `public void setBankLateProfit(double value)` |  |
| 1115 | 1 | `public double getBankLateProfit()` |  |
| 1129 | 1 | `public void setBankAllowance(java.util.Map<String, double[]> state)` |  |
| 1130 | 1 | `public java.util.Map<String, double[]> getBankAllowance()` |  |
| 1141 | 1 | `public void setBankCapitalRecord(double[] state)` |  |
| 1142 | 1 | `public double[] getBankCapitalRecord()` |  |
| 1152 | 1 | `public void setBankMonthLines(double[] state)` |  |
| 1153 | 1 | `public double[] getBankMonthLines()` |  |
| 1164 | 1 | `public void setBankStatementYear(double[] state)` |  |
| 1165 | 1 | `public double[] getBankStatementYear()` |  |
| 1177 | 1 | `public void setBankSheetYear(double[] state)` |  |
| 1178 | 1 | `public double[] getBankSheetYear()` |  |
| 1190 | 1 | `public void setBankPaidInOpening(Double value)` |  |
| 1191 | 1 | `public Double getBankPaidInOpening()` |  |
| 1192 | 1 | `public void setBankRetainedOpening(Double value)` |  |
| 1193 | 1 | `public Double getBankRetainedOpening()` |  |
| 1204 | 4 | `public void setBankPreferred(java.util.List<Bank.Preferred> blocks, double[] record)` |  |
| 1208 | 1 | `public java.util.List<Bank.Preferred> getBankPreferred()` |  |
| 1209 | 1 | `public double[] getBankPreferredRecord()` |  |
| 1220 | 1 | `public void setFund(TreasuryFund.State state)` |  |
| 1221 | 1 | `public TreasuryFund.State getFund()` |  |
| 1226 | 1 | `public void setDebtMarket(double[] market)` |  |
| 1227 | 1 | `public double[] getDebtMarket()` |  |
| 1238 | 1 | `public void setPriceIndex(double[] state)` |  |
| 1239 | 1 | `public double[] getPriceIndex()` |  |
| 1252 | 1 | `public void setExpectations(double[] state)` |  |
| 1253 | 1 | `public double[] getExpectations()` |  |
| 1265 | 1 | `public void setWorldEconomy(double[] state)` |  |
| 1266 | 1 | `public double[] getWorldEconomy()` |  |
| 1281 | 1 | `public void setTradedExchangeRate(double rate)` |  |
| 1282 | 1 | `public double getTradedExchangeRate()` |  |
| 1296 | 1 | `public void setDenomination(double[] state)` |  |
| 1297 | 1 | `public double[] getDenomination()` |  |
| 1311 | 1 | `public void setPolicyRate(double rate)` |  |
| 1313 | 1 | `public Double getPolicyRate()` | The rate as saved, or null on a save that did not carry one. |
| 1322 | 1 | `public void setPolicyAutopilot(boolean on)` |  |
| 1323 | 1 | `public boolean getPolicyAutopilot()` |  |
| 1336 | 1 | `public void setRolloverMode(String mode)` |  |
| 1338 | 5 | `public Rollover.Mode getRolloverMode()` | The setting as saved; MANUAL for an older save or a name this build does not know. |
| 1343 | 1 | `public void setRolloverLedger(double[] ledger)` |  |
| 1344 | 1 | `public double[] getRolloverLedger()` |  |
| 1345 | 1 | `public void setRolloverRecord(double[] record)` |  |
| 1346 | 1 | `public double[] getRolloverRecord()` |  |
| 1356 | 1 | `public void setLandPaidFromVault(boolean fromVault)` |  |
| 1357 | 1 | `public boolean getLandPaidFromVault()` |  |
| 1369 | 1 | `public void setInflationTarget(double target)` |  |
| 1371 | 1 | `public Double getInflationTarget()` | The target as saved, or null on a save from before the dial. |
| 1381 | 1 | `public void setPolicyStrictness(String name)` |  |
| 1383 | 1 | `public DebtManager.Strictness getPolicyStrictness()` | The strictness as saved; STANDARD for an older save or a name this build does not know. |
| 1394 | 1 | `public void setQeTargetShare(double share)` |  |
| 1395 | 1 | `public double getQeTargetShare()` |  |
| 1408 | 1 | `public void setAdvancesCeilingMonths(double months)` |  |
| 1410 | 1 | `public Double getAdvancesCeilingMonths()` | The ceiling as saved, or null on a save from before the dial. |
| 1421 | 1 | `public void setHouseholdPaperRatio(double ratio)` |  |
| 1422 | 1 | `public double getHouseholdPaperRatio()` |  |
| 1443 | 1 | `public void setBondMarket(BondMarket.State state)` |  |
| 1444 | 1 | `public BondMarket.State getBondMarket()` |  |
| 1445 | 1 | `public void setBondWrittenOff(java.util.Map<String, Double> totals)` |  |
| 1446 | 1 | `public java.util.Map<String, Double> getBondWrittenOff()` |  |
| 1447 | 1 | `public void setHouseholdBondRatio(double ratio)` |  |
| 1448 | 1 | `public double getHouseholdBondRatio()` |  |
| 1449 | 1 | `public void setHouseholdBondsByCell(java.util.Map<String, java.util.Map<String, Double>> byCell)` |  |
| 1450 | 1 | `public java.util.Map<String, java.util.Map<String, Double>> getHouseholdBondsByCell()` |  |
| 1461 | 4 | `public void setBuybackUnsettled(double households, double abroad)` |  |
| 1465 | 1 | `public double getBuybackToHouseholdsUnsettled()` |  |
| 1466 | 1 | `public double getBuybackAbroadUnsettled()` |  |
| 1490 | 1 | `public void setCarsPerHousehold(double v)` |  |
| 1491 | 1 | `public double getCarsPerHousehold()` |  |
| 1504 | 1 | `public void setTransitBill(double v)` |  |
| 1505 | 1 | `public double getTransitBill()` |  |
| 1517 | 1 | `public void setCaptiveShare(double v)` |  |
| 1518 | 1 | `public double getCaptiveShare()` |  |
| 1519 | 1 | `public void setFuelPerJourney(double v)` |  |
| 1520 | 1 | `public double getFuelPerJourney()` |  |
| 1531 | 1 | `public void setHouseholdFuel(double[] v)` |  |
| 1532 | 1 | `public double[] getHouseholdFuel()` |  |
| 1544 | 1 | `public void setHouseholdGraduates(double[] v)` |  |
| 1545 | 1 | `public double[] getHouseholdGraduates()` |  |
| 1547 | 1 | `public void setRememberedCommute(double v)` |  |
| 1548 | 3 | `public double getRememberedCommute()` |  |
| 1561 | 1 | `public void setCostOfLiving(double v)` |  |
| 1562 | 1 | `public double getCostOfLiving()` |  |
| 1575 | 1 | `public void setBankTaxCharged(double v)` |  |
| 1576 | 1 | `public double getBankTaxCharged()` |  |
| 1580 | 1 | `public void setRentWeight(double weight)` | What the landlords billed this month - see FamilyModel.setRentWeight(). |
| 1581 | 1 | `public double getRentWeight()` |  |
| 1601 | 1 | `public void setRentWeightStudio(double weight)` |  |
| 1602 | 1 | `public double getRentWeightStudio()` |  |
| 1604 | 4 | `public void setConstructionShedding(int month, double points)` |  |
| 1608 | 1 | `public int getConstructionShedMonth()` |  |
| 1609 | 1 | `public double getConstructionShedPoints()` |  |
| 1611 | 3 | `public void setDemolitions(java.util.List<DemolitionLog.Entry> entries)` |  |
| 1614 | 1 | `public java.util.List<DemolitionLog.Entry> getDemolitions()` |  |
| 1762 | 1 | `public void setLicences(double[] a)` |  |
| 1763 | 1 | `public double[] getLicences()` |  |
| 1764 | 1 | `public void setEducation(double[] a)` |  |
| 1765 | 1 | `public double[] getEducation()` |  |
| 1767 | 1 | `public void setLabour(double[] a)` |  |
| 1768 | 1 | `public double[] getLabour()` |  |
| 1769 | 1 | `public void setSkilledWorkforce(double[] a)` |  |
| 1770 | 1 | `public double[] getSkilledWorkforce()` |  |
| 1772 | 1 | `public void setCohorts(double[] a)` |  |
| 1773 | 1 | `public double[] getCohorts()` |  |
| 1776 | 1 | `public void setBandNames(String[] names)` |  |
| 1778 | 1 | `public String[] getBandNames()` | Null on a save from before the names travelled: read it as LEGACY_BANDS. |
| 1792 | 1 | `public void setShapeNames(String[] names)` |  |
| 1793 | 1 | `public String[] getShapeNames()` |  |
| 1794 | 1 | `public void setFamilies(double[] a)` |  |
| 1795 | 1 | `public double[] getFamilies()` |  |
| 1796 | 1 | `public void setMigration(double[] a)` |  |
| 1797 | 1 | `public double[] getMigration()` |  |
| 1798 | 1 | `public void setUnemployment(double[] a)` |  |
| 1799 | 1 | `public double[] getUnemployment()` |  |
| 1800 | 1 | `public void setSickness(double[] a)` |  |
| 1801 | 1 | `public double[] getSickness()` |  |
| 1802 | 1 | `public void setCrime(double[] a)` |  |
| 1803 | 1 | `public double[] getCrime()` |  |
| 1804 | 1 | `public void setHealth(double[] a)` |  |
| 1805 | 1 | `public double[] getHealth()` |  |
| 1806 | 1 | `public void setHealthcare(double[] a)` |  |
| 1807 | 1 | `public double[] getHealthcare()` |  |
| 1835 | 1 | `public void setSectorLossMonths(java.util.Map<String, Integer> m)` |  |
| 1836 | 1 | `public java.util.Map<String, Integer> getSectorLossMonths()` |  |
| 1837 | 1 | `public void setPopulationTrend(java.util.List<Long> l)` |  |
| 1838 | 1 | `public java.util.List<Long> getPopulationTrend()` |  |
| 1839 | 1 | `public void setCityCapitalSpending(double v)` |  |
| 1840 | 1 | `public double getCityCapitalSpending()` |  |
| 1858 | 1 | `public void setCityMaintenancePaid(double v)` |  |
| 1859 | 1 | `public double getCityMaintenancePaid()` |  |
| 1860 | 1 | `public void setMonthlyMaterialImports(double v)` |  |
| 1861 | 1 | `public double getMonthlyMaterialImports()` |  |
| 1862 | 1 | `public void setMonthlyMaterialImportBill(double v)` |  |
| 1863 | 1 | `public double getMonthlyMaterialImportBill()` |  |
| 1864 | 1 | `public void setMaterialsConsumed(int v)` |  |
| 1865 | 1 | `public int getMaterialsConsumed()` |  |
| 1874 | 1 | `public void setSubsidyPaid(java.util.Map<String, Double> v)` |  |
| 1875 | 1 | `public java.util.Map<String, Double> getSubsidyPaid()` |  |
| 1885 | 1 | `public void setHouseholdStatement(double[] v)` |  |
| 1886 | 1 | `public double[] getHouseholdStatement()` |  |
| 1888 | 3 | `public void setBuilds(java.util.List<BuildLog.Entry> entries)` |  |
| 1891 | 1 | `public java.util.List<BuildLog.Entry> getBuilds()` |  |
| 1893 | 3 | `public void setWriteOffTotals(java.util.Map<String, Double> totals)` |  |
| 1896 | 1 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1898 | 3 | `public void setRestructureCounts(java.util.Map<String, Integer> counts)` |  |
| 1901 | 1 | `public java.util.Map<String, Integer> getRestructureCounts()` |  |
| 1903 | 3 | `public void setBlockedMonths(java.util.Map<String, Integer> months)` |  |
| 1906 | 1 | `public java.util.Map<String, Integer> getBlockedMonths()` |  |
| 1908 | 3 | `public void setCreditStatements(java.util.Map<String, double[]> statements)` |  |
| 1911 | 1 | `public java.util.Map<String, double[]> getCreditStatements()` |  |
| 1913 | 1 | `public void setMortgageRepaid(java.util.Map<String, Double> repaid)` |  |
| 1914 | 1 | `public java.util.Map<String, Double> getMortgageRepaid()` |  |
| 1916 | 1 | `public void setInsuranceClaims(java.util.Map<String, Double> claims)` |  |
| 1917 | 1 | `public java.util.Map<String, Double> getInsuranceClaims()` |  |
| 1919 | 1 | `public void setInsurancePremiums(double premiums)` |  |
| 1920 | 1 | `public double getInsurancePremiums()` |  |
| 1922 | 1 | `public void setNationalAccounts(double[] state)` |  |
| 1923 | 1 | `public double[] getNationalAccounts()` |  |
| 1933 | 3 | `public int getUnderConstructionLength()` | Null-safe, because new saves no longer write the legacy arrays at all. |
| 1936 | 3 | `public int getUnderConstruction(int index)` |  |
| 1939 | 3 | `public double getCash()` |  |
| 1943 | 3 | `public int getMonth()` |  |
| 1947 | 3 | `public int getBuildingQuantity(int index)` |  |
| 1951 | 3 | `public int getBuildingsLength()` |  |
| 1955 | 3 | `public JsonArray getDebt()` |  |
| 1959 | 4 | `public void setBusinessDebt(List<BusinessDebt> loans)` |  |
| 1964 | 3 | `public JsonArray getBusinessDebt()` |  |
| 1968 | 3 | `public int getProgressLength()` |  |
| 1972 | 3 | `public double getProgress(int index)` |  |
| 1976 | 3 | `public int getConstructionMaterials()` |  |
| 1980 | 3 | `public long getPopulation()` |  |
| 1983 | 3 | `public boolean getReports()` |  |
| 1986 | 3 | `public java.util.List<Notice> getNotices()` |  |
| 1989 | 3 | `public boolean getGraphs()` |  |


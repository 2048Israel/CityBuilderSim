# BusinessDebtManager.java - 1,675 lines · 99 methods · 13 constants · model

`ham/citybuildersim/BusinessDebtManager.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Private-sector credit. The counterpart to DebtManager, which handles the
> city's own borrowing.
> 
> One manager holds every business loan in the city, tagged by sector, rather
> than one manager per sector. That way there is a single list to save, a single
> place to see total private credit, and adding a fourth sector is a constant in
> this file instead of another object to wire up. Each sector still gets its own
> rate - the rate is per sector, the bookkeeping is shared.
> 
> PRICING (0.7.7; the borrower's part from the curve since 0.7.8)
> 
>     rate = the bank's prime + this borrower's own expected loss over the
>            book's + its record
> 
> PRIME is the bank's: what money, running the bank, the expected loss and the
> capital a loan ties up cost it, for a sound business (Bank.prime(), WHAT A
> LOAN COSTS). Until 0.7.7 the base was the city's own rate - with the bank's
> strain premium in it - floored on the bank's cost of funds plus a point.
> The spread is THIS borrower's: since 0.7.8 its own expected loss off the
> curve its firms default on, over the through-the-cycle loss prime already
> carries, at the leverage the loan leaves it at - see PRICING FROM THE
> CURVE. A loan keeps the rate it was written at for its LOAN_TERM_MONTHS.
> 
> ORIGINATION
> 
> Loans are underwritten automatically when a sector cannot cover its month
> (the shortfall desk, coverShortfall()), and for a building a sector's
> investor plans and cannot pay for out of its own cash (the investment desk,
> canFundProject() and issueProjectLoan(), the building counted as the
> collateral). Before this existed, a sector's cash simply went negative with
> no lender, no interest and no liability on its balance sheet - the food
> industry was $48,011.82 overdrawn at month 170 and paying nothing for the
> privilege.

**Uses:** [BusinessDebt](BusinessDebt.md) (17), [BusinessLoan](BusinessLoan.md) (5), [Bank](Bank.md) (4), [Sectors](Sectors.md) (1)

**Used by (15):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CreditCheck](CreditCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 72 | PRICING FROM THE CURVE (0.7.8) |
| 128 | INSOLVENCY |
| 194 | A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 851 | · ...AND NOBODY LENDS PAST THE CEILING |
| 1002 | ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) |
| 1062 | THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) |
| 1242 | · insolvency |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 156 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | The default point: a firm owing more than this multiple of its assets is not getting repaid, and both sides know it. |
| 189 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 192 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What a restructured borrower is left owing, as a multiple of its assets. |
| 270 | `BusinessDebtManager.ASSET_VOLATILITY` | `.25` | The one-year volatility of a firm's assets: sigma in PD(L), the spread of fortunes among the firms inside a sector. |
| 273 | `BusinessDebtManager.DEFAULT_HORIZON_MONTHS` | `12` | The horizon a default probability is quoted over, in months: one year, the convention of KMV's EDF and of every rating agency's default rate. |
| 281 | `BusinessDebtManager.LOSS_GIVEN_DEFAULT` | `1 - RESTRUCTURE_TARGET / INSOLVENCY_TRIGGER` | What the bank loses on a dollar that defaults: a defaulting firm sits at the default point, owing INSOLVENCY_TRIGGER times its assets, and the restructure rule leaves it owing RESTRUCTURE_TARGET of them - so 1 - 0.6 /... |
| 372 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 417 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the borrower's expected loss, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 419 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` | The most write-downs DEFAULT_SURCHARGE is charged for: a record adds three points at the most. |
| 422 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` | How long a business loan runs, interest only, before its principal is due: three years, and it keeps the rate it was written at for all of them. |
| 432 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 1111 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 1644 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private String[] SECTORS` | Every set of books that can borrow, by name, in the registry's order. |
| 434 | `private List<BusinessDebt> loans` |  |
| 437 | `private double primeRate` | Prime: the bank's rate for a sound business this month, which every sector's own spread sits on (see PRICING). |
| 440 | `private final Map<String, Double> writtenOffThisMonth` | Written off this month, and over the whole game, per sector. |
| 441 | `private final Map<String, Double> writtenOffTotal` |  |
| 442 | `private final Map<String, Integer> restructures` |  |
| 445 | `private final Map<String, Integer> blockedMonths` | Months of borrowing ban remaining, per sector. |
| 462 | `private boolean lendingOpen` | Whether the lender is open for business at all. |
| 464 | `private final Map<String, Double> assets` |  |
| 465 | `private final Map<String, Double> rates` |  |
| 472 | `private final Map<String, Double> cashBalance` | Each sector's cash balance, refreshed with the assets. |
| 475 | `private final Map<String, Double> overdraftForgiven` | What this month's restructures forgave, per sector, until Game collects it. |
| 478 | `private final Map<String, Double> maturedPrincipal` | Principal that fell due this month, per sector, waiting to be settled. |
| 485 | `private double lentThisMonth` | The lender's side of the month, for MoneyAudit: what it advanced and what it took back. |
| 486 | `private double repaidThisMonth` |  |
| 496 | `private double feesThisMonth` | THE LOAN FEE (0.7.7): Bank.LOAN_FEE of every loan written, paid out of its proceeds - the borrower is handed the principal less this, and the bank's cash takes it at the month's settle beside the lending (Game.nextMon... |
| 497 | `private final Map<String, Double> feesBySector` |  |
| 517 | `private final Map<String, Double> lentBySector` | THE SAME TWO FIGURES, PER SECTOR. |
| 518 | `private final Map<String, Double> repaidBySector` |  |
| 1021 | `private double capitalGrowth` |  |
| 1022 | `private boolean keepGoingOnly` |  |
| 1023 | `private final Map<String, Double> principalAtRule` |  |
| 1025 | `private final java.util.Set<String> refusedForCapital` | Sectors whose project the capital rule refused this month, so the investor can say so. |
| 1114 | `private final Map<String, double[]> statements` | Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. |
| 1215 | `private final List<Written> writtenThisMonth` |  |
| 1526 | `private final Map<String, Double> defaultedThisMonth` | The month's defaults, per sector, for the notice and the playtest - struck by restructureInsolventSectors() and read in the same month (Inbox.takeMonth()), so not saved: the bank's own record of the month's write-off ... |
| 1527 | `private final Map<String, Double> defaultShareThisMonth` |  |
| 1528 | `private final java.util.Set<String> restructuredThisMonth` |  |
| 1574 | `private final Map<String, Double> principalJudged` | What each sector owed when the month's insolvency check judged it, against the assets it judged it on (getAssets(), struck at the same check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 1630 | **type** `public class BusinessDebtManager` | Private-sector credit. |
| 60 | 9 | `public void setSectors(String[] keys)` | The registry's names, in its order. |
| 70 | 1 | `public String[] sectors()` |  |

### PRICING FROM THE CURVE (0.7.8) (lines 72-127)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 3 | `public static double expectedLossSpread(double leverage)` | A borrower's own expected loss over the book's, at this leverage: LOSS_GIVEN_DEFAULT x PD(L) less Bank.BASE_LOSS_RATE, never below nothing - the part of its rate that is its risk. |
| 123 | 4 | `static double pricingLeverage(double principal, double assets)` | The leverage a price is read at: what is owed over the assets, and the whole curve against no assets at all, owing or not - a business with nothing is not a good credit, it is an empty one. |

### INSOLVENCY (lines 128-193)

### A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 194-1001)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 35 | `public static double normalCdf(double x)` | The standard normal cumulative distribution, N(x): Hart's (1968) double-precision rational approximation as Graeme West gives it ("Better approximations to cumulative normal functions", Wilmott, 2005), with a continue... |
| 337 | 5 | `public static double defaultProbability(double leverage)` | PD(L): the share of a sector's firms - weighted by what they owe - that default within a year at this leverage, N(ln(L / INSOLVENCY_TRIGGER) / ASSET_VOLATILITY). |
| 344 | 6 | `public static double defaultProbability(double leverage, double months)` | ...over this many months instead of a year: 1 - (1 - PD)^(months / DEFAULT_HORIZON_MONTHS), for a borrower held at this leverage. |
| 352 | 3 | `public static double monthlyDefaultShare(double leverage)` | h: the share of a sector's debt that defaults in one month at this leverage - the monthly hazard of PD(L). |
| 357 | 4 | `static double leverageOf(double principal, double assets)` | A sector's leverage for the curve: what it owes over its assets, infinite when it owes anything against nothing. |
| 401 | 3 | `static int exclusionFor(int defaultsSoFar)` | How long a sector is shut out, given how many times it has done this. |
| 500 | 3 | `public static double feeOn(double principal)` | What a loan of this principal pays up front: Bank.LOAN_FEE of it. |
| 504 | 1 | `public double getFeesThisMonth()` |  |
| 505 | 1 | `public double getFeesThisMonth(String sector)` |  |
| 520 | 1 | `public double getLentThisMonth()` |  |
| 521 | 1 | `public double getRepaidThisMonth()` |  |
| 523 | 3 | `public double getLentThisMonth(String sector)` |  |
| 527 | 3 | `public double getRepaidThisMonth(String sector)` |  |
| 531 | 9 | `public void startAuditMonth()` |  |
| 541 | 7 | `public BusinessDebtManager()` |  |
| 557 | 3 | `public void setPrimeRate(double rate)` | Prime, pushed in by Game each month from Bank.prime() before anything is priced (0.7.7; the city's rate before it). |
| 562 | 3 | `public void setAssets(String sector, double totalAssets)` | Total assets from that sector's balance sheet - the denominator of leverage. |
| 567 | 5 | `public void updateRates()` | pricing |
| 574 | 3 | `private double priceSector(String sector)` | The quote: the curve at the leverage the sector's last quarter of statements reads (getQuarterLeverage()), and the whole curve against no assets (pricingLeverage()). |
| 598 | 31 | `private double priceSector(String sector, double extraPrincipal, double extraAssets)` | ratio. |
| 632 | 3 | `public double getRate(String sector)` | What NEW borrowing costs this sector today. |
| 637 | 3 | `public double getSpread(String sector)` | What this sector pays over prime: its own expected loss and record. |
| 642 | 3 | `public double getRiskSpread(String sector)` | ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()). |
| 647 | 3 | `public double getRecordSurcharge(String sector)` | ...and the second: DEFAULT_SURCHARGE a write-down on its record, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 651 | 3 | `private double recordSurcharge(String sector)` |  |
| 662 | 3 | `public double projectRate(String sector, double amount)` | WHAT A PROJECT LOAN OF THIS SIZE WOULD BE WRITTEN AT (0.7.8): the curve at the leverage it leaves the sector at, the building counted at the loan's value - the rate issueProjectLoan() writes, and the one Game.consider... |
| 671 | 4 | `public double leverageAfterProject(String sector, double amount)` | ...and the leverage that loan is priced at: the last quarter's statements with the deal on top, the building counted. |
| 677 | 3 | `public double getPrimeRate()` | Prime, as the bank set it this month. |
| 681 | 4 | `public double getLeverage(String sector)` |  |
| 686 | 3 | `public void setCash(String sector, double cash)` |  |
| 690 | 3 | `public double getCash(String sector)` |  |
| 694 | 3 | `private double getOverdraftForgivenPending(String sector)` |  |
| 699 | 5 | `public double takeOverdraftForgiven(String sector)` | The overdraft a restructure forgave this month, handed over once. |
| 705 | 3 | `public double getAssets(String sector)` |  |
| 710 | 5 | `public double getAllPrincipal()` | Everything every sector owes. |
| 716 | 9 | `public double getPrincipal(String sector)` |  |
| 726 | 7 | `public double getTotalPrincipal()` |  |
| 735 | 9 | `public double getMonthlyInterest(String sector)` | This month's interest cost for a sector - the income statement's expense line. |
| 745 | 7 | `public double getTotalMonthlyInterest()` |  |
| 758 | 4 | `public double getEffectiveRate(String sector)` | Blended annual rate actually being paid on existing debt, as opposed to getRate() which is what the next loan would cost. |
| 763 | 9 | `public int getLoanCount(String sector)` |  |
| 773 | 3 | `public List<BusinessDebt> getLoans()` |  |
| 777 | 9 | `public List<BusinessDebt> getLoans(String sector)` |  |
| 795 | 16 | `public void processMonth()` | Advances every loan and retires the ones that mature. |
| 813 | 7 | `public double takeMaturedPrincipal(String sector)` | Reads and clears the principal that fell due this month for one sector. |
| 829 | 96 | `public double coverShortfall(String sector, double cash, double monthlyLoss, int month)` | Underwrites a loan if the sector is short, and returns the proceeds. |
| 943 | 3 | `public double borrowingRoom(String sector)` | How much more this sector may borrow today, from either desk. |
| 974 | 14 | `public boolean canFundProject(String sector, double amount)` | Whether the INVESTMENT desk will fund a project of this size. |
| 990 | 3 | `private double roomUnder(String sector, double multiple)` | The ceiling at this multiple of assets, and the bank's capital rule on top of it. |
| 995 | 6 | `private double ceilingRoom(String sector, double multiple)` | The ceiling alone: nothing while the lender is shut or the sector barred. |

### ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) (lines 1002-1061)

| line | len | member | says |
|---:|---:|---|---|
| 1032 | 7 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | The bank's capital rule for the month, from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(). |
| 1046 | 6 | `public double capitalRoom(String sector)` | What the bank's capital lets this sector borrow this month, over what it owes now: its debt when the rule was set, grown by the month's limit (none under the minimum), less what it owes - so what matured is room to re... |
| 1054 | 1 | `public double getCapitalGrowth()` | The month's limit on a borrower's growth, a share a month: infinite with none. |
| 1057 | 1 | `public boolean isKeepGoingOnly()` | True when the bank lends only to keep its borrowers going this month. |
| 1060 | 1 | `public boolean wasRefusedForCapital(String sector)` | True when the capital rule refused this sector a project this month. |

### THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) (lines 1062-1241)

| line | len | member | says |
|---:|---:|---|---|
| 1117 | 10 | `public void recordStatement(String sector, double owed, double owned)` | One month-end reading of a sector: what it owed and what it owned, as the bank read them (Game.sectorPositions()). |
| 1129 | 7 | `public double quarterPrincipal(String sector)` | What the sector owed, averaged over its last quarter of readings - what it owes now, with none. |
| 1138 | 7 | `public double quarterAssets(String sector)` | ...and what it owned. |
| 1147 | 4 | `public double getQuarterLeverage(String sector)` | The leverage the bank reads the sector at: its quarter's average debt over its average assets, 0 with no assets. |
| 1153 | 3 | `public double getQuarterDefaultRate(String sector)` | The sector's default rate a year at the leverage its last quarter reads (getQuarterLeverage()) - the reading its price is struck on (getRiskSpread()), which the screens print beside that price. |
| 1158 | 4 | `public int getStatementCount(String sector)` | How many readings a sector has, up to STATEMENT_MONTHS. |
| 1164 | 5 | `public Map<String, double[]> getStatementsToSave()` | The readings, for the save: a copy, by sector name. |
| 1171 | 10 | `public void restoreStatements(Map<String, double[]> saved)` | ...and back on load. |
| 1183 | 3 | `public void setLendingOpen(boolean open)` | Game tells the lender each month whether the bank behind it is standing. |
| 1187 | 3 | `public boolean isLendingOpen()` |  |
| 1197 | 3 | `public BusinessLoan issueLoan(String sector, double faceValue, int month)` | Writes a loan of this principal - a shortfall loan, priced at what the sector will owe over the assets it has. |
| 1202 | 3 | `public BusinessLoan issueProjectLoan(String sector, double faceValue, int month)` | ...a project's: priced with the building it buys counted in the assets, at the loan's value (projectRate()). |
| 1213 | 1 | **type** `public record Written(String sector, double amount, double leverage, double rate, boolean project)` | One loan written this month: to whom, how much, the leverage it left the borrower at, the rate it was written at, and whether it bought a building (0.7.8, for the playtest's count of loans written past the watch line ... |
| 1218 | 1 | `public List<Written> getWrittenThisMonth()` | Every loan written this month, in the order written. |
| 1220 | 21 | `private BusinessLoan write(String sector, double faceValue, int month, double extraAssets, boolean project)` |  |

### insolvency (lines 1242-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 3 | `public boolean isBorrowingBlocked(String sector)` |  |
| 1248 | 3 | `public int getBlockedMonths(String sector)` |  |
| 1252 | 3 | `public double getWrittenOffThisMonth(String sector)` |  |
| 1256 | 3 | `public double getWrittenOffTotal(String sector)` |  |
| 1260 | 3 | `public int getRestructureCount(String sector)` |  |
| 1272 | 9 | `public void restoreWriteOffs(java.util.Map<String, Double> totals)` | Puts the write-off history back on load. |
| 1282 | 3 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1309 | 3 | `public java.util.Map<String, Integer> getRestructureCounts()` | THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED. |
| 1313 | 3 | `public java.util.Map<String, Integer> getBlockedMonthsAll()` |  |
| 1317 | 19 | `public void restoreCreditRecord(java.util.Map<String, Integer> counts, java.util.Map<String, Integer> blocked)` |  |
| 1337 | 7 | `public double getTotalWrittenOff()` |  |
| 1361 | 4 | `public boolean isInsolvent(String sector)` | Is every firm in this sector under water at once - the backstop's case? |
| 1380 | 77 | `public double restructure(String sector)` | THE BACKSTOP: writes a sector with nothing left down to what its assets can support - RESTRUCTURE_TARGET of nothing - forgives its overdraft, counts the default on its record and shuts it out for exclusionFor(). |
| 1464 | 19 | `public double restructureInsolventSectors()` | THE MONTH'S DEFAULTS, every sector: the backstop for a sector with nothing left (restructure()), and for every other the slice of its debt whose firms fell through the default point (defaultSlice()). |
| 1498 | 21 | `public double defaultSlice(String sector)` | THE SLICE: the share of this sector's debt whose firms fell through the default point this month, monthlyDefaultShare() of it at the leverage the month's check reads (principal over getAssets(), struck after the balan... |
| 1531 | 1 | `public double getDefaultedThisMonth(String sector)` | The debt whose firms defaulted this month in the slice, before what the bank recovers - the write-off is LOSS_GIVEN_DEFAULT of it. |
| 1534 | 1 | `public double getDefaultShareThisMonth(String sector)` | The share of the sector's debt that defaulted this month in the slice, h. |
| 1537 | 1 | `public boolean wasRestructuredThisMonth(String sector)` | True when the backstop wrote this sector down whole this month. |
| 1540 | 3 | `public double getDefaultRate(String sector)` | The sector's default rate a year at its leverage now, PD(L) - what the Bank tab shows beside its leverage. |
| 1560 | 4 | `public boolean defaultsAreNews(String sector)` | WHETHER THIS MONTH'S DEFAULTS ARE NEWS: the backstop, or a slice at least the share that defaults a month at the default point itself - monthlyDefaultShare(INSOLVENCY_TRIGGER), 5.6% of its debt, where half the sector'... |
| 1576 | 4 | `public double getPrincipalJudged(String sector)` |  |
| 1582 | 8 | `public void advanceBlocks()` | Counts down the borrowing bans. |
| 1592 | 3 | `public void setLoans(List<BusinessDebt> loans)` | save / load |
| 1596 | 17 | `public void clearLoans()` |  |
| 1615 | 28 | `public void printBusinessDebtInfo(int currentMonth)` | printers |
| 1646 | 4 | `static { ... }` |  |
| 1652 | 22 | `public void redenominate(double scale)` | Every business loan and this month's lending, in the new unit. |


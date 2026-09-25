# BusinessDebtManager.java - 2,142 lines · 143 methods · 19 constants · model

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
> 
> AND THE LANDLORDS' BUILDINGS ON INSURED MORTGAGES (0.7.11): a residential
> order is funded by a Mortgage, not a loan - issueMortgage(), tested by
> canFundMortgage(), priced at the insured rate Game pushes in with prime,
> paid down every month and renewed at each term's end in processMonth().
> See THE LANDLORDS' MORTGAGES below.

**Uses:** [Mortgage](Mortgage.md) (28), [BusinessDebt](BusinessDebt.md) (18), [BusinessLoan](BusinessLoan.md) (5), [Bank](Bank.md) (4), [Sectors](Sectors.md) (1)

**Used by (17):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CreditCheck](CreditCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 78 | PRICING FROM THE CURVE (0.7.8) |
| 134 | INSOLVENCY |
| 200 | A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 919 | · ...AND NOBODY LENDS PAST THE CEILING |
| 1070 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 1432 | ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) |
| 1517 | THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) |
| 1697 | · insolvency |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 162 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | The default point: a firm owing more than this multiple of its assets is not getting repaid, and both sides know it. |
| 195 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 198 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What a restructured borrower is left owing, as a multiple of its assets. |
| 276 | `BusinessDebtManager.ASSET_VOLATILITY` | `.25` | The one-year volatility of a firm's assets: sigma in PD(L), the spread of fortunes among the firms inside a sector. |
| 279 | `BusinessDebtManager.DEFAULT_HORIZON_MONTHS` | `12` | The horizon a default probability is quoted over, in months: one year, the convention of KMV's EDF and of every rating agency's default rate. |
| 287 | `BusinessDebtManager.LOSS_GIVEN_DEFAULT` | `1 - RESTRUCTURE_TARGET / INSOLVENCY_TRIGGER` | What the bank loses on a dollar that defaults: a defaulting firm sits at the default point, owing INSOLVENCY_TRIGGER times its assets, and the restructure rule leaves it owing RESTRUCTURE_TARGET of them - so 1 - 0.6 /... |
| 378 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 423 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the borrower's expected loss, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 425 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` | The most write-downs DEFAULT_SURCHARGE is charged for: a record adds three points at the most. |
| 428 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` | How long a business loan runs, interest only, before its principal is due: three years, and it keeps the rate it was written at for all of them. |
| 438 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 1152 | `BusinessDebtManager.MORTGAGE_BANK_SHUT` | `"the bank is shut"` | canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). |
| 1154 | `BusinessDebtManager.MORTGAGE_BANNED` | `"borrowing ban"` | ...when the sector is serving a borrowing ban. |
| 1156 | `BusinessDebtManager.MORTGAGE_DOWN_PAYMENT` | `Mortgage.Decision.DOWN_PAYMENT` | ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. |
| 1158 | `BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT` | `"past the default point"` | ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. |
| 1160 | `BusinessDebtManager.MORTGAGE_CAPITAL` | `"the bank's capital"` | ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). |
| 1162 | `BusinessDebtManager.MORTGAGE_NOTHING` | `"nothing to borrow"` | ...when there is nothing to borrow. |
| 1566 | `BusinessDebtManager.STATEMENT_MONTHS` | `3` | How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. |
| 2104 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `private String[] SECTORS` | Every set of books that can borrow, by name, in the registry's order. |
| 440 | `private List<BusinessDebt> loans` |  |
| 443 | `private double primeRate` | Prime: the bank's rate for a sound business this month, which every sector's own spread sits on (see PRICING). |
| 446 | `private double insuredMortgageRate` | The insured mortgage's rate this month (Bank.insuredMortgageRate()), pushed in by Game beside prime: what a new mortgage is written at and a term renews at. |
| 449 | `private final Map<String, Double> writtenOffThisMonth` | Written off this month, and over the whole game, per sector. |
| 450 | `private final Map<String, Double> writtenOffTotal` |  |
| 451 | `private final Map<String, Integer> restructures` |  |
| 454 | `private final Map<String, Integer> blockedMonths` | Months of borrowing ban remaining, per sector. |
| 471 | `private boolean lendingOpen` | Whether the lender is open for business at all. |
| 473 | `private final Map<String, Double> assets` |  |
| 474 | `private final Map<String, Double> rates` |  |
| 481 | `private final Map<String, Double> cashBalance` | Each sector's cash balance, refreshed with the assets. |
| 484 | `private final Map<String, Double> overdraftForgiven` | What this month's restructures forgave, per sector, until Game collects it. |
| 487 | `private final Map<String, Double> maturedPrincipal` | Principal that fell due this month, per sector, waiting to be settled. |
| 494 | `private double lentThisMonth` | The lender's side of the month, for MoneyAudit: what it advanced and what it took back. |
| 495 | `private double repaidThisMonth` |  |
| 505 | `private double feesThisMonth` | THE LOAN FEE (0.7.7): Bank.LOAN_FEE of every loan written, paid out of its proceeds - the borrower is handed the principal less this, and the bank's cash takes it at the month's settle beside the lending (Game.nextMon... |
| 506 | `private final Map<String, Double> feesBySector` |  |
| 526 | `private final Map<String, Double> lentBySector` | THE SAME TWO FIGURES, PER SECTOR. |
| 527 | `private final Map<String, Double> repaidBySector` |  |
| 1118 | `private double premiumsThisMonth` | The premiums added to the mortgages written this month, and by sector: the treasury's revenue line. |
| 1119 | `private final Map<String, Double> premiumsBySector` |  |
| 1127 | `private final Map<String, Double> mortgageRepaidBySector` | THE PRINCIPAL THE MORTGAGES' PAYMENTS TOOK THIS MONTH, by sector - part of getRepaidThisMonth(), and the figure the Bank tab and the landlords' screen show beside the payment. |
| 1130 | `private int mortgagesWrittenThisMonth, renewedThisMonth, fallenDueThisMonth` | The month's mortgages written, renewed, and fallen due because the lender could not renew them - and the same over the run, for the playtest (not saved, a count for the run). |
| 1131 | `private int renewedLifetime, fallenDueLifetime` |  |
| 1142 | `private final Map<String, Double> insuredWrittenOffThisMonth` | WHAT THE INSURANCE PAID THIS MONTH, by sector: what the month's write-downs took off insured mortgages. |
| 1143 | `private final Map<String, Double> insuredWrittenOffTotal` |  |
| 1146 | `private double premiumsTotal` | The premiums written over the city's life. |
| 1149 | `private String mortgageRefusal` | The reason canFundMortgage() last refused, or null. |
| 1451 | `private double capitalGrowth` |  |
| 1452 | `private boolean keepGoingOnly` |  |
| 1453 | `private final Map<String, Double> principalAtRule` |  |
| 1455 | `private final java.util.Set<String> refusedForCapital` | Sectors whose project the capital rule refused this month, so the investor can say so. |
| 1486 | `private boolean insuredRationed` | True this month when the capital rule rations the insured mortgages too - the bank's leverage requirement binding. |
| 1569 | `private final Map<String, double[]> statements` | Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. |
| 1670 | `private final List<Written> writtenThisMonth` |  |
| 1976 | `private final Map<String, Double> defaultedThisMonth` | The month's defaults, per sector, for the notice and the playtest - struck by restructureInsolventSectors() and read in the same month (Inbox.takeMonth()), so not saved: the bank's own record of the month's write-off ... |
| 1977 | `private final Map<String, Double> defaultShareThisMonth` |  |
| 1978 | `private final java.util.Set<String> restructuredThisMonth` |  |
| 2024 | `private final Map<String, Double> principalJudged` | What each sector owed when the month's insolvency check judged it, against the assets it judged it on (getAssets(), struck at the same check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 2091 | **type** `public class BusinessDebtManager` | Private-sector credit. |
| 66 | 9 | `public void setSectors(String[] keys)` | The registry's names, in its order. |
| 76 | 1 | `public String[] sectors()` |  |

### PRICING FROM THE CURVE (0.7.8) (lines 78-133)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 3 | `public static double expectedLossSpread(double leverage)` | A borrower's own expected loss over the book's, at this leverage: LOSS_GIVEN_DEFAULT x PD(L) less Bank.BASE_LOSS_RATE, never below nothing - the part of its rate that is its risk. |
| 129 | 4 | `static double pricingLeverage(double principal, double assets)` | The leverage a price is read at: what is owed over the assets, and the whole curve against no assets at all, owing or not - a business with nothing is not a good credit, it is an empty one. |

### INSOLVENCY (lines 134-199)

### A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 200-1069)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 35 | `public static double normalCdf(double x)` | The standard normal cumulative distribution, N(x): Hart's (1968) double-precision rational approximation as Graeme West gives it ("Better approximations to cumulative normal functions", Wilmott, 2005), with a continue... |
| 343 | 5 | `public static double defaultProbability(double leverage)` | PD(L): the share of a sector's firms - weighted by what they owe - that default within a year at this leverage, N(ln(L / INSOLVENCY_TRIGGER) / ASSET_VOLATILITY). |
| 350 | 6 | `public static double defaultProbability(double leverage, double months)` | ...over this many months instead of a year: 1 - (1 - PD)^(months / DEFAULT_HORIZON_MONTHS), for a borrower held at this leverage. |
| 358 | 3 | `public static double monthlyDefaultShare(double leverage)` | h: the share of a sector's debt that defaults in one month at this leverage - the monthly hazard of PD(L). |
| 363 | 4 | `static double leverageOf(double principal, double assets)` | A sector's leverage for the curve: what it owes over its assets, infinite when it owes anything against nothing. |
| 407 | 3 | `static int exclusionFor(int defaultsSoFar)` | How long a sector is shut out, given how many times it has done this. |
| 509 | 3 | `public static double feeOn(double principal)` | What a loan of this principal pays up front: Bank.LOAN_FEE of it. |
| 513 | 1 | `public double getFeesThisMonth()` |  |
| 514 | 1 | `public double getFeesThisMonth(String sector)` |  |
| 529 | 1 | `public double getLentThisMonth()` |  |
| 530 | 1 | `public double getRepaidThisMonth()` |  |
| 532 | 3 | `public double getLentThisMonth(String sector)` |  |
| 536 | 3 | `public double getRepaidThisMonth(String sector)` |  |
| 540 | 17 | `public void startAuditMonth()` |  |
| 558 | 7 | `public BusinessDebtManager()` |  |
| 574 | 3 | `public void setPrimeRate(double rate)` | Prime, pushed in by Game each month from Bank.prime() before anything is priced (0.7.7; the city's rate before it). |
| 579 | 3 | `public void setInsuredMortgageRate(double rate)` | The insured mortgage's rate, pushed in by Game with prime (Bank.insuredMortgageRate()). |
| 584 | 3 | `public double getInsuredMortgageRate()` | What a new insured mortgage is written at this month, and what a term that ends renews at. |
| 589 | 3 | `public void setAssets(String sector, double totalAssets)` | Total assets from that sector's balance sheet - the denominator of leverage. |
| 594 | 5 | `public void updateRates()` | pricing |
| 601 | 3 | `private double priceSector(String sector)` | The quote: the curve at the leverage the sector's last quarter of statements reads (getQuarterLeverage()), and the whole curve against no assets (pricingLeverage()). |
| 625 | 31 | `private double priceSector(String sector, double extraPrincipal, double extraAssets)` | ratio. |
| 659 | 3 | `public double getRate(String sector)` | What NEW borrowing costs this sector today. |
| 664 | 3 | `public double getSpread(String sector)` | What this sector pays over prime: its own expected loss and record. |
| 669 | 3 | `public double getRiskSpread(String sector)` | ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()). |
| 674 | 3 | `public double getRecordSurcharge(String sector)` | ...and the second: DEFAULT_SURCHARGE a write-down on its record, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 678 | 3 | `private double recordSurcharge(String sector)` |  |
| 689 | 3 | `public double projectRate(String sector, double amount)` | WHAT A PROJECT LOAN OF THIS SIZE WOULD BE WRITTEN AT (0.7.8): the curve at the leverage it leaves the sector at, the building counted at the loan's value - the rate issueProjectLoan() writes, and the one Game.consider... |
| 698 | 4 | `public double leverageAfterProject(String sector, double amount)` | ...and the leverage that loan is priced at: the last quarter's statements with the deal on top, the building counted. |
| 704 | 3 | `public double getPrimeRate()` | Prime, as the bank set it this month. |
| 708 | 4 | `public double getLeverage(String sector)` |  |
| 713 | 3 | `public void setCash(String sector, double cash)` |  |
| 717 | 3 | `public double getCash(String sector)` |  |
| 721 | 3 | `private double getOverdraftForgivenPending(String sector)` |  |
| 726 | 5 | `public double takeOverdraftForgiven(String sector)` | The overdraft a restructure forgave this month, handed over once. |
| 732 | 3 | `public double getAssets(String sector)` |  |
| 737 | 5 | `public double getAllPrincipal()` | Everything every sector owes. |
| 743 | 9 | `public double getPrincipal(String sector)` |  |
| 753 | 7 | `public double getTotalPrincipal()` |  |
| 762 | 9 | `public double getMonthlyInterest(String sector)` | This month's interest cost for a sector - the income statement's expense line. |
| 772 | 7 | `public double getTotalMonthlyInterest()` |  |
| 785 | 4 | `public double getEffectiveRate(String sector)` | Blended annual rate actually being paid on existing debt, as opposed to getRate() which is what the next loan would cost. |
| 790 | 9 | `public int getLoanCount(String sector)` |  |
| 800 | 3 | `public List<BusinessDebt> getLoans()` |  |
| 804 | 9 | `public List<BusinessDebt> getLoans(String sector)` |  |
| 822 | 57 | `public void processMonth()` | Advances every loan and retires the ones that mature. |
| 881 | 7 | `public double takeMaturedPrincipal(String sector)` | Reads and clears the principal that fell due this month for one sector. |
| 897 | 96 | `public double coverShortfall(String sector, double cash, double monthlyLoss, int month)` | Underwrites a loan if the sector is short, and returns the proceeds. |
| 1011 | 3 | `public double borrowingRoom(String sector)` | How much more this sector may borrow today, from either desk. |
| 1042 | 14 | `public boolean canFundProject(String sector, double amount)` | Whether the INVESTMENT desk will fund a project of this size. |
| 1058 | 3 | `private double roomUnder(String sector, double multiple)` | The ceiling at this multiple of assets, and the bank's capital rule on top of it. |
| 1063 | 6 | `private double ceilingRoom(String sector, double multiple)` | The ceiling alone: nothing while the lender is shut or the sector barred. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 1070-1431)

| line | len | member | says |
|---:|---:|---|---|
| 1176 | 23 | `public boolean canFundMortgage(String sector, double shortfall, double cost)` | Whether the lender will write a mortgage for this shortfall on a building of this cost: open, the sector not barred, the loan no more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost, and the borrower not past the ... |
| 1201 | 1 | `public String getMortgageRefusal()` | Why canFundMortgage() last said no, or null if it said yes. |
| 1210 | 20 | `public Mortgage issueMortgage(String sector, double shortfall, int month)` | WRITES A MORTGAGE for this shortfall: the loan that covers it once the fee is paid (Mortgage.loanFor()), the premium added, at the insured rate. |
| 1232 | 1 | `public double getPremiumsThisMonth()` | The premiums on the mortgages written this month: the treasury's revenue line. |
| 1234 | 1 | `public double getPremiumsThisMonth(String sector)` | ...by sector, which its cash flow statement takes off what it was handed. |
| 1236 | 1 | `public double getPremiumsTotal()` | The premiums written over the city's life. |
| 1239 | 1 | `public int getMortgagesWrittenThisMonth()` | The mortgages written this month, renewed this month, and fallen due this month because the lender could not renew them. |
| 1240 | 1 | `public int getRenewedThisMonth()` |  |
| 1241 | 1 | `public int getFallenDueThisMonth()` |  |
| 1243 | 1 | `public int getRenewedLifetime()` | ...over the run, for the playtest; not saved. |
| 1244 | 1 | `public int getFallenDueLifetime()` |  |
| 1247 | 7 | `public List<Mortgage> getMortgages(String sector)` | Every mortgage one sector owes, in the order written. |
| 1256 | 5 | `public List<Mortgage> getMortgages()` | Every mortgage in the city. |
| 1263 | 1 | `public int getMortgageCount(String sector)` | How many mortgages one sector owes. |
| 1266 | 5 | `public double getMortgagePrincipal(String sector)` | What one sector owes on its mortgages. |
| 1273 | 5 | `public double getMortgagePrincipal()` | What every sector owes on mortgages: the bank's mortgage book. |
| 1280 | 5 | `public double getInsuredPrincipal(String sector)` | What one sector owes on its insured mortgages - the part of its debt the city insures. |
| 1287 | 5 | `public double getInsuredPrincipal()` | ...every sector's: what the bank's book holds at Bank.RISK_INSURED_MORTGAGE. |
| 1294 | 3 | `public double getUninsuredPrincipal(String sector)` | What one sector owes that nobody insures: its debt less its insured mortgages - what the bank's allowance reads, and its capital rule while the leverage ratio does not bind (rationedPrincipal()). |
| 1299 | 5 | `public double getMortgagePayment(String sector)` | The level payments one sector's mortgages ask next month: interest and principal together. |
| 1306 | 5 | `public double getMortgagePayment()` | ...every sector's. |
| 1313 | 3 | `public double getMortgageRate(String sector)` | The rate one sector's mortgages carry, weighted by what is owed on each; 0 with none. |
| 1318 | 3 | `public double getMortgageRate()` | ...every mortgage's. |
| 1322 | 8 | `private static double weightedRate(List<Mortgage> ms)` |  |
| 1332 | 8 | `public int getNextRenewalMonth(String sector)` | The first month one of this sector's mortgages renews, or -1 with none. |
| 1342 | 5 | `public int getMortgagesRenewingWithin(int months)` | How many mortgages renew within this many months of this one. |
| 1349 | 4 | `public boolean allMortgagesInsured()` | True when every mortgage in the city is insured - which every one this build writes is. |
| 1355 | 1 | `public double getMortgageRepaidThisMonth(String sector)` | The principal one sector's mortgage payments took this month. |
| 1358 | 5 | `public double getMortgageRepaidThisMonth()` | ...every sector's. |
| 1365 | 1 | `public Map<String, Double> getMortgageRepaidToSave()` | The month's principal repaid on mortgages, for the save: a copy, by sector name. |
| 1368 | 7 | `public void restoreMortgageRepaid(Map<String, Double> saved)` | ...and back on load. |
| 1377 | 1 | `public double getInsuredWrittenOffThisMonth(String sector)` | What this month's write-downs took off one sector's insured mortgages: the claim the treasury pays the bank. |
| 1380 | 5 | `public double getInsuredWrittenOffThisMonth()` | ...every sector's: the month's claims. |
| 1387 | 1 | `public double getInsuredWrittenOffTotal(String sector)` | What write-downs have taken off one sector's insured mortgages over the city's life. |
| 1390 | 5 | `public double getInsuredWrittenOffTotal()` | ...every sector's: the claims over the city's life. |
| 1397 | 1 | `public double getPremiumsTotalToSave()` | The insurance book's record, for the save: the premiums over the city's life. |
| 1400 | 1 | `public Map<String, Double> getInsuredWrittenOffTotals()` | The claims over the city's life, by sector, for the save. |
| 1403 | 8 | `public void restoreInsuranceRecord(double premiums, Map<String, Double> claims)` | ...and both back on load. |
| 1417 | 14 | `private double writeDownSector(String sector, double scale)` | Writes every instrument of one sector down to this share, pro rata, and says what came off its insured mortgages - the claim. |

### ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8) (lines 1432-1516)

| line | len | member | says |
|---:|---:|---|---|
| 1463 | 3 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | The bank's capital rule for the month, from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(). |
| 1473 | 11 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly, boolean insuredToo)` | ...and whether it rations the insured mortgages too: true when the bank's leverage requirement is the larger (Bank.leverageBinds(), 0.7.11 round 2), because a mortgage then uses the capital the bank is short of. |
| 1488 | 1 | `public boolean isInsuredRationed()` |  |
| 1491 | 3 | `private double rationedPrincipal(String sector)` | The debt the capital rule reads: the uninsured, or all of it while insuredRationed. |
| 1501 | 6 | `public double capitalRoom(String sector)` | What the bank's capital lets this sector borrow this month, over what it owes now: its debt when the rule was set, grown by the month's limit (none under the minimum), less what it owes - so what matured is room to re... |
| 1509 | 1 | `public double getCapitalGrowth()` | The month's limit on a borrower's growth, a share a month: infinite with none. |
| 1512 | 1 | `public boolean isKeepGoingOnly()` | True when the bank lends only to keep its borrowers going this month. |
| 1515 | 1 | `public boolean wasRefusedForCapital(String sector)` | True when the capital rule refused this sector a project this month. |

### THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3) (lines 1517-1696)

| line | len | member | says |
|---:|---:|---|---|
| 1572 | 10 | `public void recordStatement(String sector, double owed, double owned)` | One month-end reading of a sector: what it owed and what it owned, as the bank read them (Game.sectorPositions()). |
| 1584 | 7 | `public double quarterPrincipal(String sector)` | What the sector owed, averaged over its last quarter of readings - what it owes now, with none. |
| 1593 | 7 | `public double quarterAssets(String sector)` | ...and what it owned. |
| 1602 | 4 | `public double getQuarterLeverage(String sector)` | The leverage the bank reads the sector at: its quarter's average debt over its average assets, 0 with no assets. |
| 1608 | 3 | `public double getQuarterDefaultRate(String sector)` | The sector's default rate a year at the leverage its last quarter reads (getQuarterLeverage()) - the reading its price is struck on (getRiskSpread()), which the screens print beside that price. |
| 1613 | 4 | `public int getStatementCount(String sector)` | How many readings a sector has, up to STATEMENT_MONTHS. |
| 1619 | 5 | `public Map<String, double[]> getStatementsToSave()` | The readings, for the save: a copy, by sector name. |
| 1626 | 10 | `public void restoreStatements(Map<String, double[]> saved)` | ...and back on load. |
| 1638 | 3 | `public void setLendingOpen(boolean open)` | Game tells the lender each month whether the bank behind it is standing. |
| 1642 | 3 | `public boolean isLendingOpen()` |  |
| 1652 | 3 | `public BusinessLoan issueLoan(String sector, double faceValue, int month)` | Writes a loan of this principal - a shortfall loan, priced at what the sector will owe over the assets it has. |
| 1657 | 3 | `public BusinessLoan issueProjectLoan(String sector, double faceValue, int month)` | ...a project's: priced with the building it buys counted in the assets, at the loan's value (projectRate()). |
| 1668 | 1 | **type** `public record Written(String sector, double amount, double leverage, double rate, boolean project)` | One loan written this month: to whom, how much, the leverage it left the borrower at, the rate it was written at, and whether it bought a building (0.7.8, for the playtest's count of loans written past the watch line ... |
| 1673 | 1 | `public List<Written> getWrittenThisMonth()` | Every loan written this month, in the order written. |
| 1675 | 21 | `private BusinessLoan write(String sector, double faceValue, int month, double extraAssets, boolean project)` |  |

### insolvency (lines 1697-2142)

| line | len | member | says |
|---:|---:|---|---|
| 1699 | 3 | `public boolean isBorrowingBlocked(String sector)` |  |
| 1703 | 3 | `public int getBlockedMonths(String sector)` |  |
| 1707 | 3 | `public double getWrittenOffThisMonth(String sector)` |  |
| 1711 | 3 | `public double getWrittenOffTotal(String sector)` |  |
| 1715 | 3 | `public int getRestructureCount(String sector)` |  |
| 1727 | 9 | `public void restoreWriteOffs(java.util.Map<String, Double> totals)` | Puts the write-off history back on load. |
| 1737 | 3 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 1764 | 3 | `public java.util.Map<String, Integer> getRestructureCounts()` | THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED. |
| 1768 | 3 | `public java.util.Map<String, Integer> getBlockedMonthsAll()` |  |
| 1772 | 19 | `public void restoreCreditRecord(java.util.Map<String, Integer> counts, java.util.Map<String, Integer> blocked)` |  |
| 1792 | 7 | `public double getTotalWrittenOff()` |  |
| 1816 | 4 | `public boolean isInsolvent(String sector)` | Is every firm in this sector under water at once - the backstop's case? |
| 1835 | 74 | `public double restructure(String sector)` | THE BACKSTOP: writes a sector with nothing left down to what its assets can support - RESTRUCTURE_TARGET of nothing - forgives its overdraft, counts the default on its record and shuts it out for exclusionFor(). |
| 1916 | 20 | `public double restructureInsolventSectors()` | THE MONTH'S DEFAULTS, every sector: the backstop for a sector with nothing left (restructure()), and for every other the slice of its debt whose firms fell through the default point (defaultSlice()). |
| 1951 | 18 | `public double defaultSlice(String sector)` | THE SLICE: the share of this sector's debt whose firms fell through the default point this month, monthlyDefaultShare() of it at the leverage the month's check reads (principal over getAssets(), struck after the balan... |
| 1981 | 1 | `public double getDefaultedThisMonth(String sector)` | The debt whose firms defaulted this month in the slice, before what the bank recovers - the write-off is LOSS_GIVEN_DEFAULT of it. |
| 1984 | 1 | `public double getDefaultShareThisMonth(String sector)` | The share of the sector's debt that defaulted this month in the slice, h. |
| 1987 | 1 | `public boolean wasRestructuredThisMonth(String sector)` | True when the backstop wrote this sector down whole this month. |
| 1990 | 3 | `public double getDefaultRate(String sector)` | The sector's default rate a year at its leverage now, PD(L) - what the Bank tab shows beside its leverage. |
| 2010 | 4 | `public boolean defaultsAreNews(String sector)` | WHETHER THIS MONTH'S DEFAULTS ARE NEWS: the backstop, or a slice at least the share that defaults a month at the default point itself - monthlyDefaultShare(INSOLVENCY_TRIGGER), 5.6% of its debt, where half the sector'... |
| 2026 | 4 | `public double getPrincipalJudged(String sector)` |  |
| 2032 | 8 | `public void advanceBlocks()` | Counts down the borrowing bans. |
| 2042 | 3 | `public void setLoans(List<BusinessDebt> loans)` | save / load |
| 2046 | 26 | `public void clearLoans()` |  |
| 2074 | 29 | `public void printBusinessDebtInfo(int currentMonth)` | printers |
| 2106 | 4 | `static { ... }` |  |
| 2112 | 29 | `public void redenominate(double scale)` | Every business loan and this month's lending, in the new unit. |


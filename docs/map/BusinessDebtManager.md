# BusinessDebtManager.java - 1,015 lines · 62 methods · 12 constants · model

`ham/citybuildersim/BusinessDebtManager.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Private-sector credit. The counterpart to DebtManager, which handles the
> city's own borrowing.
> 
> One manager holds every business loan in the city, tagged by sector, rather
> than one manager per sector. That way there is a single list to save, a single
> place to see total private credit, and adding a fourth sector is a constant in
> this file instead of another object to wire up. Each sector still gets its own
> rate - the rate is per sector, the bookkeeping is shared.
> 
> PRICING
> 
>     rate = government rate + credit spread
> 
> The government rate is the risk-free floor: no private borrower is safer than
> the city that can tax them. The spread is driven by leverage - debt to total
> assets - which mirrors how DebtManager prices the city off debt-to-GDP. Both
> ask the same question of the same shape: how much is owed against how much
> there is to pay with.
> 
>     spread = MIN_SPREAD + SPREAD_PER_DEBT_TO_ASSETS * (debt / assets)
> 
> clamped to [MIN_SPREAD, MAX_SPREAD]. A debt-free business still pays
> MIN_SPREAD over the city, because it is still not the city. A business whose
> debts exceed its assets pays the ceiling and no more - the cap is what stops
> a bad month from compounding into an unrecoverable one.
> 
> Capping the SPREAD rather than the total rate keeps the two systems coupled:
> if city borrowing drives the risk-free rate up, business credit follows it up
> rather than compressing to nothing against a fixed ceiling.
> 
> ORIGINATION
> 
> Loans are underwritten automatically when a sector cannot cover its month.
> Buildings are paid for out of city cash, so a business never borrows to
> expand; the only thing it needs credit for is a shortfall. Before this
> existed, a sector's cash simply went negative with no lender, no interest and
> no liability on its balance sheet - the food industry was $48,011.82 overdrawn
> at month 170 and paying nothing for the privilege.

**Uses:** [BusinessDebt](BusinessDebt.md) (16), [BusinessLoan](BusinessLoan.md) (3), [Sectors](Sectors.md) (1), [Bank](Bank.md) (1)

**Used by (10):** [BankScreen](BankScreen.md), [CreditCheck](CreditCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [SectorBooks](SectorBooks.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 83 | INSOLVENCY |
| 567 | · ...AND NOBODY LENDS PAST THE CEILING |
| 721 | · insolvency |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `BusinessDebtManager.MIN_SPREAD` | `.01` | Floor over the government rate. |
| 81 | `BusinessDebtManager.MAX_SPREAD` | `.08` | Ceiling over the government rate - the "even worst case, not too bad" cap. |
| 99 | `BusinessDebtManager.INSOLVENCY_TRIGGER` | `1.5` | Debt above this multiple of assets is not getting repaid, and both sides know it. |
| 132 | `BusinessDebtManager.MAX_LOAN_TO_ASSETS` | `0.9` | The most a lender will advance against a borrower's assets. |
| 135 | `BusinessDebtManager.RESTRUCTURE_TARGET` | `.6` | What a restructured borrower is left owing, as a multiple of its assets. |
| 147 | `BusinessDebtManager.BORROWING_BLOCKED_MONTHS` | `12` | Months a sector cannot borrow after being restructured. |
| 181 | `BusinessDebtManager.SPREAD_PER_DEBT_TO_ASSETS` | `.06` | Extra annual interest per 1.0 of debt-to-assets. |
| 194 | `BusinessDebtManager.DEFAULT_SURCHARGE` | `.01` | Extra annual interest per prior write-down, on top of the leverage spread and outside its cap, up to DEFAULT_SURCHARGE_MAX_COUNT of them. |
| 195 | `BusinessDebtManager.DEFAULT_SURCHARGE_MAX_COUNT` | `3` |  |
| 197 | `BusinessDebtManager.LOAN_TERM_MONTHS` | `36` |  |
| 207 | `BusinessDebtManager.BUFFER_MONTHS` | `3` | Borrow enough to cover the hole plus this many months of the current loss. |
| 993 | `BusinessDebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private String[] SECTORS` | Every set of books that can borrow, by name, in the registry's order. |
| 209 | `private List<BusinessDebt> loans` |  |
| 211 | `private double riskFreeRate` |  |
| 214 | `private final Map<String, Double> writtenOffThisMonth` | Written off this month, and over the whole game, per sector. |
| 215 | `private final Map<String, Double> writtenOffTotal` |  |
| 216 | `private final Map<String, Integer> restructures` |  |
| 219 | `private final Map<String, Integer> blockedMonths` | Months of borrowing ban remaining, per sector. |
| 236 | `private boolean lendingOpen` | Whether the lender is open for business at all. |
| 238 | `private final Map<String, Double> assets` |  |
| 239 | `private final Map<String, Double> rates` |  |
| 246 | `private final Map<String, Double> cashBalance` | Each sector's cash balance, refreshed with the assets. |
| 249 | `private final Map<String, Double> overdraftForgiven` | What this month's restructures forgave, per sector, until Game collects it. |
| 252 | `private final Map<String, Double> maturedPrincipal` | Principal that fell due this month, per sector, waiting to be settled. |
| 259 | `private double lentThisMonth` | The lender's side of the month, for MoneyAudit: what it advanced and what it took back. |
| 260 | `private double repaidThisMonth` |  |
| 272 | `private final Map<String, Double> lentBySector` | THE SAME TWO FIGURES, PER SECTOR. |
| 273 | `private final Map<String, Double> repaidBySector` |  |
| 314 | `private double costOfFunds` | What the bank pays for the money it is about to lend. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 965 | **type** `public class BusinessDebtManager` | Private-sector credit. |
| 65 | 9 | `public void setSectors(String[] keys)` | The registry's names, in its order. |
| 75 | 1 | `public String[] sectors()` |  |

### INSOLVENCY (lines 83-720)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 3 | `static int exclusionFor(int defaultsSoFar)` | How long a sector is shut out, given how many times it has done this. |
| 275 | 1 | `public double getLentThisMonth()` |  |
| 276 | 1 | `public double getRepaidThisMonth()` |  |
| 278 | 3 | `public double getLentThisMonth(String sector)` |  |
| 282 | 3 | `public double getRepaidThisMonth(String sector)` |  |
| 286 | 6 | `public void startAuditMonth()` |  |
| 293 | 7 | `public BusinessDebtManager()` |  |
| 302 | 3 | `public void setRiskFreeRate(double rate)` | setters |
| 315 | 1 | `public void setCostOfFunds(double rate)` |  |
| 316 | 1 | `public double getCostOfFunds()` |  |
| 319 | 3 | `public void setAssets(String sector, double totalAssets)` | Total assets from that sector's balance sheet - the denominator of leverage. |
| 324 | 5 | `public void updateRates()` | pricing |
| 330 | 3 | `private double priceSector(String sector)` |  |
| 342 | 47 | `private double priceSector(String sector, double extraPrincipal)` | ratio. |
| 392 | 3 | `public double getRate(String sector)` | What NEW borrowing costs this sector today. |
| 396 | 3 | `public double getSpread(String sector)` |  |
| 400 | 3 | `public double getRiskFreeRate()` |  |
| 404 | 4 | `public double getLeverage(String sector)` |  |
| 409 | 3 | `public void setCash(String sector, double cash)` |  |
| 413 | 3 | `public double getCash(String sector)` |  |
| 417 | 3 | `private double getOverdraftForgivenPending(String sector)` |  |
| 422 | 5 | `public double takeOverdraftForgiven(String sector)` | The overdraft a restructure forgave this month, handed over once. |
| 428 | 3 | `public double getAssets(String sector)` |  |
| 433 | 5 | `public double getAllPrincipal()` | Everything every sector owes. |
| 439 | 9 | `public double getPrincipal(String sector)` |  |
| 449 | 7 | `public double getTotalPrincipal()` |  |
| 458 | 9 | `public double getMonthlyInterest(String sector)` | This month's interest cost for a sector - the income statement's expense line. |
| 468 | 7 | `public double getTotalMonthlyInterest()` |  |
| 481 | 4 | `public double getEffectiveRate(String sector)` | Blended annual rate actually being paid on existing debt, as opposed to getRate() which is what the next loan would cost. |
| 486 | 9 | `public int getLoanCount(String sector)` |  |
| 496 | 3 | `public List<BusinessDebt> getLoans()` |  |
| 500 | 9 | `public List<BusinessDebt> getLoans(String sector)` |  |
| 518 | 16 | `public void processMonth()` | Advances every loan and retires the ones that mature. |
| 536 | 7 | `public double takeMaturedPrincipal(String sector)` | Reads and clears the principal that fell due this month for one sector. |
| 552 | 84 | `public double coverShortfall(String sector, double cash, double monthlyLoss, int month)` | Underwrites a loan if the sector is short, and returns the proceeds. |
| 651 | 3 | `public double borrowingRoom(String sector)` | How much more this sector may borrow today, from either desk. |
| 682 | 7 | `public boolean canFundProject(String sector, double amount)` | Whether the INVESTMENT desk will fund a project of this size. |
| 690 | 6 | `private double roomUnder(String sector, double multiple)` |  |
| 698 | 3 | `public void setLendingOpen(boolean open)` | Game tells the lender each month whether the bank behind it is standing. |
| 702 | 3 | `public boolean isLendingOpen()` |  |
| 706 | 14 | `public BusinessLoan issueLoan(String sector, double faceValue, int month)` |  |

### insolvency (lines 721-1015)

| line | len | member | says |
|---:|---:|---|---|
| 723 | 3 | `public boolean isBorrowingBlocked(String sector)` |  |
| 727 | 3 | `public int getBlockedMonths(String sector)` |  |
| 731 | 3 | `public double getWrittenOffThisMonth(String sector)` |  |
| 735 | 3 | `public double getWrittenOffTotal(String sector)` |  |
| 739 | 3 | `public int getRestructureCount(String sector)` |  |
| 751 | 9 | `public void restoreWriteOffs(java.util.Map<String, Double> totals)` | Puts the write-off history back on load. |
| 761 | 3 | `public java.util.Map<String, Double> getWriteOffTotals()` |  |
| 788 | 3 | `public java.util.Map<String, Integer> getRestructureCounts()` | THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED. |
| 792 | 3 | `public java.util.Map<String, Integer> getBlockedMonthsAll()` |  |
| 796 | 19 | `public void restoreCreditRecord(java.util.Map<String, Integer> counts, java.util.Map<String, Integer> blocked)` |  |
| 816 | 7 | `public double getTotalWrittenOff()` |  |
| 836 | 8 | `public boolean isInsolvent(String sector)` | Is this sector's debt beyond what its assets could ever cover? |
| 854 | 66 | `public double restructure(String sector)` | Writes a sector's debt down to what its assets can support. |
| 922 | 12 | `public double restructureInsolventSectors()` | Restructures whoever needs it. |
| 936 | 8 | `public void advanceBlocks()` | Counts down the borrowing bans. |
| 946 | 3 | `public void setLoans(List<BusinessDebt> loans)` | save / load |
| 950 | 12 | `public void clearLoans()` |  |
| 964 | 28 | `public void printBusinessDebtInfo(int currentMonth)` | printers |
| 995 | 4 | `static { ... }` |  |
| 1001 | 13 | `public void redenominate(double scale)` | Every business loan and this month's lending, in the new unit. |


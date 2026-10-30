# SectorBooks.java - 1,029 lines · 60 methods · 3 constants · model

`ham/citybuildersim/SectorBooks.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> A month of books for every business in the city, and last month's too.
> 
> WHY THIS EXISTS AT ALL. Six sectors used to keep their own figures in five
> different handlers, in five different shapes, and none of them could show
> LAST month at all. So: one shape, read once a month, kept for two months.
> What the sector screens draw is this. Since the sector template
> (2026-09-11) every sector strikes its statement in this shape itself - see
> Sector.Statement - and this class adds the flows the city recorded
> against its name (credit, subsidy, the owners, the money abroad) and keeps
> the comparative column.
> 
> NOTHING HERE FEEDS BACK. This class reads the model and is read by screens.
> No handler asks it a question, no decision depends on it, and deleting it
> would change nothing about how the city runs. That is the whole design
> constraint: a reporting layer that can affect the thing it reports is not a
> reporting layer, it is a bug waiting for a save/load to expose it.
> 
> IT IS SAVED, and that is not decoration. A statement whose comparative column
> is blank until you have played a month is a statement that is blank exactly
> when a returning player opens it - which is the same trap the schools fell
> into. Two months of six small records is a few hundred bytes.

**Uses:** [Debt](Debt.md) (18), [Good](Good.md) (11), [Equity](Equity.md) (8), [BusinessDebtManager](BusinessDebtManager.md) (7), [Sector](Sector.md) (5), [Game](Game.md) (4), [EconomyManager](EconomyManager.md) (2), [Exchange](Exchange.md) (1), [Statement](Statement.md) (1), [BalanceSheet](BalanceSheet.md) (1)

**Used by (27):** [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HoldersCheck](HoldersCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MortgageCheck](MortgageCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReloadCheck](ReloadCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 547 | THE TWO MONTHS |
| 593 | THE MONTH |
| 787 | SAVE AND RESTORE |
| 975 | A REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 496 | `SectorBooks.Debt.KINDS` | `BusinessDebtManager.DEBT_KINDS` | The four kinds, as the statements name them: BusinessDebtManager.DEBT_KINDS. |
| 828 | `SectorBooks.DEBT_ROWS` | `12` | The rows a saved Debt is written as: six arrays, then moved's three and movedAtClose's three. |
| 873 | `SectorBooks.SHARES_SLOTS` | `7` | The slots a saved Shares is written as: shares, outstanding, price, fair value, traded (1 or 0), dividend, dividend a year. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 551 | `private final Map<String, SectorMonth> now` |  |
| 552 | `private final Map<String, SectorMonth> before` |  |
| 555 | `private final Map<String, Double> lastCash` | Closing cash from the month just recorded, which is next month's opening. |
| 558 | `private final Map<String, Debt> debtNow` | R1 and R2 for this month and last, and each company's share (0.7.74): saved since FW2 and FW6 - see Debt and Shares. |
| 559 | `private final Map<String, Shares> sharesNow` |  |
| 562 | `private final Map<String, Map<Good, double[]>> stockAtClose` | Each business's stock by good as the month closed, {units, price} (0.7.75, R6): next month's revaluation is last month's units at its prices. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 999 | **type** `public final class SectorBooks` | A month of books for every business in the city, and last month's too. |
| 40 | 427 | **type** `public record SectorMonth(String sector, int month, double revenue, double inputs, double payroll, double e...` | One sector's month. |
| 316 | 3 | `public double equity()` _(in SectorBooks.SectorMonth)_ | What the sheet says the owners have. |
| 321 | 3 | `public double totalLiabilities()` _(in SectorBooks.SectorMonth)_ | What it owes: its loans and bonds, and since 0.7.44 its suppliers. |
| 325 | 5 | `public double totalAssets()` _(in SectorBooks.SectorMonth)_ |  |
| 332 | 3 | `public double operatingCost()` _(in SectorBooks.SectorMonth)_ | Everything above operating income: goods bought, payroll, utilities, repairs. |
| 345 | 17 | `public double unexplained()` _(in SectorBooks.SectorMonth)_ | What the cash flow statement adds up to, against what the cash actually is. |
| 364 | 9 | `public double unexplainedScale()` _(in SectorBooks.SectorMonth)_ | The size of the figures unexplained() is made of, every one of them as a magnitude: what MoneyAudit.tolerance() reads it against (0.7.54). |
| 381 | 6 | `public double businessNetIncome()` _(in SectorBooks.SectorMonth)_ | Its net income without the coupon on its dollars abroad, after the tax that coupon carried: what the business itself earned, the figure the share register values it on (EM1), its money counted at face beside it (HD2). |
| 389 | 3 | `public double foreignInterestAfterTax()` _(in SectorBooks.SectorMonth)_ | What the coupon on its dollars abroad left it after its tax: netIncome() less businessNetIncome(). |
| 393 | 3 | `public double margin()` _(in SectorBooks.SectorMonth)_ |  |
| 398 | 18 | `public static SectorMonth none(String sector)` _(in SectorBooks.SectorMonth)_ | An empty month, for a sector that has not been recorded yet. |
| 418 | 3 | `public double retained()` _(in SectorBooks.SectorMonth)_ | Its equity less its share capital: what it kept and what the model revalued (R3). |
| 423 | 3 | `public boolean hasLandByRelief()` _(in SectorBooks.SectorMonth)_ | Whether the sheet valued its land by the ground it stands on (landByRelief is read and has all six figures). |
| 428 | 3 | `public double landSqFtOf(int c)` _(in SectorBooks.SectorMonth)_ | Its land on one class of ground, square feet: c is 0 flat, 1 foothill, 2 steep and very steep (the slope class, which only works stand on). |
| 433 | 3 | `public double landPriceOf(int c)` _(in SectorBooks.SectorMonth)_ | ...and the price a square foot of that class was valued at. |
| 438 | 6 | `public double landByClassValue()` _(in SectorBooks.SectorMonth)_ | The sheet's land by class, each at its price: landSqFtOf(c) x landPriceOf(c) summed; the one-price figure (landSqFt() x landPrice()) where the month did not read it. |
| 446 | 3 | `public boolean landOnHills()` _(in SectorBooks.SectorMonth)_ | Whether any of its land stands on foothill or steep ground. |
| 451 | 11 | `SectorMonth withPaidIn(double capital, boolean derived)` _(in SectorBooks.SectorMonth)_ | The same month with its share capital put: a save from before 0.7.75, derived at the load (derivePaidIn()). |
| 463 | 3 | `public boolean isEmpty()` _(in SectorBooks.SectorMonth)_ |  |
| 492 | 37 | **type** `public record Debt(double[] interest, double[] owed, double[] withinYear, double[] withinFive, double[] rat...` | R1 AND R2 (0.7.74, the sector statements): one business's interest by instrument as its statement was struck, and what it owes by kind with what of it falls due within a year and within five, as its sheet was read - e... |
| 498 | 1 | `public double interestTotal()` _(in SectorBooks.Debt)_ |  |
| 499 | 1 | `public double owedTotal()` _(in SectorBooks.Debt)_ |  |
| 500 | 1 | `public double withinYearTotal()` _(in SectorBooks.Debt)_ |  |
| 501 | 1 | `public double withinFiveTotal()` _(in SectorBooks.Debt)_ |  |
| 503 | 6 | `private static double sum(double[] a)` _(in SectorBooks.Debt)_ |  |
| 510 | 4 | `Debt scaled(double s)` _(in SectorBooks.Debt)_ |  |
| 515 | 6 | `private static double[][] times(double[][] a, double s)` _(in SectorBooks.Debt)_ |  |
| 522 | 6 | `private static double[] times(double[] a, double s)` _(in SectorBooks.Debt)_ |  |
| 539 | 7 | **type** `public record Shares(double shares, double outstanding, double price, double fair, boolean traded, double d...` | One company's share as its month closed (0.7.74, the investor report): its shares, those outside the bank's desk, the last trade or fair value with whether it has traded, the register's fair value, the month's dividen... |
| 542 | 3 | `Shares scaled(double s)` _(in SectorBooks.Shares)_ |  |

### THE TWO MONTHS (lines 547-592)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 1 | `public Debt debt(String key)` | This month's R1 and R2 for one business, or null while not counted (a load, a founding). |
| 567 | 1 | `public Debt debtBefore(String key)` | ...and last month's. |
| 569 | 1 | `public Shares shares(String key)` | One company's share as this month closed, or null while not counted. |
| 571 | 1 | `public Shares sharesBefore(String key)` | ...and as last month closed. |
| 573 | 1 | `public SectorMonth get(Sector sector)` |  |
| 574 | 1 | `public SectorMonth previous(Sector sector)` |  |
| 576 | 3 | `public SectorMonth get(String key)` |  |
| 580 | 3 | `public SectorMonth previous(String key)` |  |
| 584 | 4 | `public boolean hasComparatives()` |  |
| 589 | 3 | `public boolean isEmpty()` |  |

### THE MONTH (lines 593-786)

| line | len | member | says |
|---:|---:|---|---|
| 602 | 24 | `public void takeMonth(Game game)` |  |
| 628 | 15 | `private static Debt readDebt(Game game, String key)` | R1 and R2 as the month's books read them (0.7.74): EconomyManager's splits, copied - null when it kept neither. |
| 644 | 5 | `private static double[][] copy(double[][] a)` |  |
| 651 | 8 | `private static Shares readShares(Game game, String key)` | A company's share as the month closes (0.7.74): null for one that is not listed or has no shares. |
| 670 | 91 | `private SectorMonth read(Game game, Sector sector)` | One sector's figures, off the statement it struck this month and the flows the city recorded against its name. |
| 771 | 15 | `public void derivePaidIn(Equity register)` | A SAVE FROM BEFORE 0.7.75 KEPT NO SHARE CAPITAL (R3): its two months read nothing there. |

### SAVE AND RESTORE (lines 787-974)

| line | len | member | says |
|---:|---:|---|---|
| 800 | 1 | `public java.util.List<SectorMonth> thisMonth()` |  |
| 801 | 1 | `public java.util.List<SectorMonth> lastMonth()` |  |
| 803 | 3 | `private static java.util.List<SectorMonth> list(Map<String, SectorMonth> from)` |  |
| 818 | 8 | `public Map<String, double[][]> debtToSave(boolean thisMonth)` | R1 and R2 as the books took them, this month's or last month's (FW2, 2026-10-24), for the save: each sector's record as DEBT_ROWS rows of fresh arrays - interest, owed, within a year, within five, rate, runs to, then ... |
| 830 | 12 | `private static double[][] rowsOf(Debt d)` |  |
| 843 | 10 | `private static Debt debtOf(double[][] rows)` |  |
| 854 | 4 | `private static boolean finite(double[][] rows)` |  |
| 865 | 6 | `public void restoreDebt(Map<String, double[][]> saved, Map<String, double[][]> savedBefore)` | ...and put back by the load path after restoreFrom() (FW2): a loaded city's sheets split their debt, and its income statements their interest, as the saved city's did. |
| 880 | 12 | `public Map<String, double[]> sharesToSave(boolean thisMonth)` | Each company's share as the books took it, this month's or last month's (FW6, 2026-10-25), for the save: the record's figures in SHARES_SLOTS slots, as fresh arrays. |
| 897 | 6 | `public void restoreShares(Map<String, double[]> saved, Map<String, double[]> savedBefore)` | ...and put back by the load path after restoreFrom() (FW6): the Sectors screen's share report reads this month and last as the saved city's did, not "not counted" for two months. |
| 904 | 4 | `private static Shares sharesOf(double[] a)` |  |
| 915 | 12 | `public Map<String, Map<String, double[]>> stockToSave()` | Each business's stock by good as the month closed, for the save (FW6, 2026-10-25): {units, price} by the good's name, as fresh arrays, a business with no stock as an empty map (it was counted: its next revaluation is ... |
| 933 | 14 | `public void restoreStock(Map<String, Map<String, double[]>> saved)` | ...and put back by the load path after restoreFrom() (FW6): the month after a load counts its revaluation, last month's units at this month's prices, as the city that kept running does. |
| 948 | 26 | `public void restoreFrom(java.util.List<SectorMonth> saved, java.util.List<SectorMonth> savedBefore)` |  |

### A REFORM (lines 975-1029)

| line | len | member | says |
|---:|---:|---|---|
| 986 | 10 | `public void redenominate(double scale)` |  |
| 997 | 24 | `private static SectorMonth scaled(SectorMonth m, double s)` |  |
| 1023 | 6 | `private static double[] scaledByRelief(double[] v, double s)` | The land by class with its three prices rescaled and its three areas left (ET3); null stays null. |


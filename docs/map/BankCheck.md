# BankCheck.java - 3,750 lines · 20 methods · 0 constants · harnesses

`ham/citybuildersim/BankCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The commercial bank, and the families it discharges.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
> Before this went in there were three lenders in the game and none of them was
> anybody: a sector borrowed from BusinessDebtManager, the city sold bonds to a
> market, and a family short of the shop borrowed from nothing at all. Money
> arrived from outside the city and interest disappeared out of it.
> 
> So the questions here are not "does the class compile". They are:
> 
>   1. Does the price of credit actually depend on the bank - since 0.7.7 on
>      what lending costs it, not on how lent out it is? A price that no
>      borrower pays is a number on a screen.
>   2. Does the money still add up, now that four flows that used to run to and
>      from nowhere run between two pools inside the city?
>   3. Does a family that cannot carry its debt get discharged - and does the
>      bank, not thin air, eat the loss?
>   4. Does the trading desk's statement add up? Jerus: "the bank, just
>      explain to me the trading desk, cause a bunch of times it's losing
>      billions of dollars due to the trading desk." The opened lines have to
>      sum to the figure above them, and the term that makes them - the
>      re-mark of what the desk holds - is measured on a fixture that trades
>      and counted on a played city.
>   5. Does the bank PAY for the city's paper? It held every bond the city
>      sold and, until 2026-09-21, had never handed over a dollar for one -
>      section 10. Since 0.7.1 it pays for what the households did not take,
>      and earns the discount as it accretes rather than the month it settles.
>   6. Does what the Bank tab prints add up (0.7.9, section 13)? The ladder's
>      parts are its prime, the weight table foots to the weighted book, and
>      the equity's movement leaves nothing unexplained - three figures the
>      screen used to work out for itself, two of them wrongly. Since
>      0.7.13 the Balance sheet page too: its lines are what totalAssets()
>      and totalLiabilities() sum, it foots this month and a year ago, and
>      the year ago survives a save; and its equity's two parts, paid in
>      and retained, add up to it to the cent every month and move by
>      exactly their own causes, and an older save does not invent them.
>   7. Does the desk keep to the bank's capital (0.7.8, section 17)? It buys
>      the bank's own shares back only with what the bank holds over its
>      target, and other companies' only while the bank would still hold its
>      target with them on its books, at the weight the weighted book gives
>      the desk; what it will not buy stays with the seller.
>   8. Does the bank keep its capital like a business (0.7.8, sections 7-12)?
>      It sets aside for the loans that will not come back and draws a
>      write-off against that first, chooses its target from the worst year
>      it has lived through, keeps its profit and lends slower under it,
>      pays a share inside its band and returns the excess over it - and a
>      reload reads the same month.
>   9. Does a sector default a slice at a time (0.7.8, sections 14-16)? The
>      month's slice is the curve's and the allowance reads the same curve,
>      firm by firm; the backstop takes only a sector with nothing left; a
>      failing sector's plant is sold to the builders for its material; and
>      the bank reads a borrower from its last quarter.
> 
> Each of those is measured by CAUSING the condition, never by finding a city
> that happens to be in it.

**Uses:** [Bank](Bank.md) (311), [BusinessDebtManager](BusinessDebtManager.md) (107), [HouseholdBalance](HouseholdBalance.md) (46), [Game](Game.md) (34), [Sectors](Sectors.md) (31), [Equity](Equity.md) (21), [FamilyStructure](FamilyStructure.md) (21), [Exchange](Exchange.md) (19), [ExchangeCheck](ExchangeCheck.md) (17), [PayTier](PayTier.md) (16), [GameFiles](GameFiles.md) (13), [DebtManager](DebtManager.md) (12), [OrderBook](OrderBook.md) (10), [CentralBank](CentralBank.md) (9), [MoneyAudit](MoneyAudit.md) (9), [Founding](Founding.md) (8), [Household](Household.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [BusinessInvestment](BusinessInvestment.md) (3), [SectorBooks](SectorBooks.md) (3), [Debt](Debt.md) (3), [HouseholdAccounts](HouseholdAccounts.md) (2), [Good](Good.md) (2), [Ladder](Ladder.md) (2), [BusinessLoan](BusinessLoan.md) (1), [BusinessDebt](BusinessDebt.md) (1), [Notice](Notice.md) (1), [EconomyManager](EconomyManager.md) (1), [Construction](Construction.md) (1), [Sector](Sector.md) (1)... and 4 more

**Used by (1):** [BondCheck](BondCheck.md)

## Sections

| line | section |
|---:|---|
| 139 | · 1. the two limits |
| 170 | · 1b. WHAT TO PAY SAVERS IS A DECISION |
| 275 | · · ...and it does not open counters either |
| 331 | · 2. STRAIN IS NOT A PRICE (0.7.7) |
| 507 | · 3. WHAT IT PAYS, WHAT IT TAKES, WHAT IT KEEPS (0.7.7) |
| 719 | · 4. a real city, and its money |
| 822 | · 5. the save carries the bank's cash |
| 883 | · 6. a family that cannot carry it |
| 998 | · 7. and the city opens its own |
| 1278 | · 8. the accounting identities, on a played city |
| 1570 | · 8b. the trading desk's statement foots |
| 1647 | · 12. a reload reads the same month (0.7.8) |
| 1851 | · 9. capital is the constraint, and it can run out |
| 1938 | 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 2339 | 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) |
| 2522 | 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) |
| 2607 | 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) |
| 2760 | 13. WHAT THE BANK TAB READS (0.7.9) |
| 3148 | 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 3518 | 10. the bank pays for the city's paper (2026-09-21) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 68 | `static int fails` |  |
| 69 | `static PrintStream out` |  |
| 70 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 66 | 3685 | **type** `public class BankCheck` | The commercial bank, and the families it discharges. |
| 72 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 5 | `static double[] sheetOf(Bank bank)` | The bank's balance sheet as it stands, every line of it (0.7.13). |
| 84 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 94 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 107 | 9 | `static double deskParts(Exchange exchange, Equity register, Bank bank)` | The trading desk's statement as the bank screen opens it, less the re-mark: sold to households and abroad, bought from both, dividends on the inventory, tendered into buybacks - the same getters BankScreen reads, with... |
| 123 | 6 | `static double capitalMoved(Bank b)` | Everything that moves the bank's equity that is not its net income: capital put in (both halves), the treasury's, the founding settlement, less its dividend - and since 0.7.8 its own shares, issued or bought back, whi... |
| 130 | 3 | `static double deskParts(Game game)` |  |
| 134 | 1803 | `public static void main(String[] args) throws Exception` |  |

### 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 1938-2338)

| line | len | member | says |
|---:|---:|---|---|
| 1961 | 9 | `static double staged(double principal, double leverage)` | The allowance the round-2 brief writes down, staged firm by firm, computed here rather than by the model: principal x ((1 - s2) x EL12 + s2 x ELlife), s2 = N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY), EL12 = m... |
| 1972 | 5 | `static double hazard(double leverage)` | The monthly hazard the brief writes down, 1 - (1 - PD)^(1/12), computed here rather than by the model. |
| 1979 | 9 | `static BusinessDebtManager lender(String sector, double assets, double...loans)` | A lender with one sector owing these loans against these assets. |
| 1989 | 349 | `static void theSectorDefaultsASliceAtATime() throws Exception` |  |

### 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) (lines 2339-2521)

| line | len | member | says |
|---:|---:|---|---|
| 2353 | 168 | `static void aFailingSectorsPlantIsSoldToTheBuilders() throws Exception` |  |

### 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) (lines 2522-2606)

| line | len | member | says |
|---:|---:|---|---|
| 2537 | 69 | `static void theBankReadsTheQuarter()` |  |

### 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) (lines 2607-2759)

| line | len | member | says |
|---:|---:|---|---|
| 2626 | 133 | `static void theDeskIsHeldToTheBanksCapital()` |  |

### 13. WHAT THE BANK TAB READS (0.7.9) (lines 2760-3147)

| line | len | member | says |
|---:|---:|---|---|
| 2774 | 373 | `static void whatTheBankTabReads() throws Exception` |  |

### 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 3148-3517)

| line | len | member | says |
|---:|---:|---|---|
| 3161 | 8 | `static Bank lentOut(double capital, double book)` | A bank with one branch, this much capital and this much lent - equity is the capital, its cash what it lent past it. |
| 3171 | 5 | `static java.util.Map<String, double[]> owing(String sector, double principal, double assets)` | The sectors' positions as the bank's provide() reads them: one sector, owing this against these assets. |
| 3177 | 340 | `static void theBankAsABusinessWithItsCapital()` |  |

### 10. the bank pays for the city's paper (2026-09-21) (lines 3518-3750)

| line | len | member | says |
|---:|---:|---|---|
| 3572 | 178 | `static void theBankPaysForTheCitysPaper() throws Exception` |  |


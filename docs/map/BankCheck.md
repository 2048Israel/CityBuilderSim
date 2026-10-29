# BankCheck.java - 4,432 lines · 23 methods · 0 constants · harnesses

`ham/citybuildersim/BankCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
>      and retained - and since 0.7.14 the city's preferred, a third - add
>      up to it to the cent every month and move by exactly their own
>      causes, and an older save does not invent them. Since 0.7.33
>      (section 13b) every rate the ladder draws in its parts is those parts
>      - a sector's quote to the bit, an insured mortgage to 1e-12 - a city
>      just loaded quotes its record and its concentration, and the branch
>      verdict is the investors' planner's, which is a read. And the interest
>      margin, month and year, is read against what earns the bank interest -
>      its loans, the paper and bonds it holds, and its reserves - and not its
>      loan book alone, which made it 9,972% in a city whose reserves were 700
>      times its loans (BK1, GitHub issue #5).
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
> ... (11 more lines in the source)

**Uses:** [Bank](Bank.md) (338), [BusinessDebtManager](BusinessDebtManager.md) (114), [HouseholdBalance](HouseholdBalance.md) (46), [Game](Game.md) (43), [Sectors](Sectors.md) (33), [Equity](Equity.md) (24), [MoneyAudit](MoneyAudit.md) (23), [FamilyStructure](FamilyStructure.md) (21), [Exchange](Exchange.md) (19), [ExchangeCheck](ExchangeCheck.md) (17), [PayTier](PayTier.md) (16), [GameFiles](GameFiles.md) (16), [DebtManager](DebtManager.md) (12), [Founding](Founding.md) (10), [OrderBook](OrderBook.md) (10), [DecisionLog](DecisionLog.md) (10), [CentralBank](CentralBank.md) (9), [TempDirs](TempDirs.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [Household](Household.md) (4), [WageBand](WageBand.md) (3), [SectorBooks](SectorBooks.md) (3), [JobType](JobType.md) (3), [Ladder](Ladder.md) (3), [Debt](Debt.md) (3), [ChartModel](ChartModel.md) (3), [HouseholdAccounts](HouseholdAccounts.md) (2), [Good](Good.md) (2), [Sector](Sector.md) (2)... and 12 more

**Used by (1):** [BondCheck](BondCheck.md)

## Sections

| line | section |
|---:|---|
| 177 | · 1. the two limits |
| 223 | · 1b. WHAT TO PAY SAVERS IS A DECISION |
| 322 | · · ...and it does not open counters either |
| 372 | · 2. STRAIN IS NOT A PRICE (0.7.7) |
| 550 | · 3. WHAT IT PAYS, WHAT IT TAKES, WHAT IT KEEPS (0.7.7) |
| 769 | · 4. a real city, and its money |
| 888 | · 5. the save carries the bank's cash |
| 949 | · 6. a family that cannot carry it |
| 1064 | · 7. and the city opens its own |
| 1386 | · 8. the accounting identities, on a played city |
| 1689 | · 8b. the trading desk's statement foots |
| 1766 | · 12. a reload reads the same month (0.7.8) |
| 1971 | · 9. capital is the constraint, and it can run out |
| 2060 | 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 2479 | 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) |
| 2713 | 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) |
| 2798 | 19. THE BRANCHES, BY THEIR CUSTOMERS (0.7.19) |
| 3005 | 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) |
| 3158 | 13. WHAT THE BANK TAB READS (0.7.9) |
| 3648 | 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 4018 | 10. the bank pays for the city's paper (2026-09-21) |
| 4257 | 13b. THE TAB DRAWS EACH RATE IN ITS PARTS (0.7.33) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `static int fails` |  |
| 84 | `static PrintStream out` |  |
| 85 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 4352 | **type** `public class BankCheck` | The commercial bank, and the families it discharges. |
| 87 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 93 | 5 | `static double[] sheetOf(Bank bank)` | The bank's balance sheet as it stands, every line of it (0.7.13). |
| 99 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 114 | 11 | `static void playRecordingEarningAssets(Game city, int months, java.util.List<Double> closed)` | Plays a city this many months, one at a time and quietly, and adds to the list the bank's earning assets as each month closed - the figure the sheet ring files at the next startMonth() (BK1). |
| 126 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 139 | 9 | `static double deskParts(Exchange exchange, Equity register, Bank bank)` | The trading desk's statement as the bank screen opens it, less the re-mark: sold to households and abroad, bought from both, dividends on the inventory, tendered into buybacks - the same getters BankScreen reads, with... |
| 157 | 10 | `static double capitalMoved(Bank b)` | Everything that moves the bank's equity that is not its net income: capital put in (both halves), the treasury's, the founding settlement, less its dividend - and since 0.7.8 its own shares, issued or bought back, whi... |
| 168 | 3 | `static double deskParts(Game game)` |  |
| 172 | 1887 | `public static void main(String[] args) throws Exception` |  |

### 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 2060-2478)

| line | len | member | says |
|---:|---:|---|---|
| 2083 | 9 | `static double staged(double principal, double leverage)` | The allowance the round-2 brief writes down, staged firm by firm, computed here rather than by the model: principal x ((1 - s2) x EL12 + s2 x ELlife), s2 = N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY), EL12 = m... |
| 2094 | 5 | `static double hazard(double leverage)` | The monthly hazard the brief writes down, 1 - (1 - PD)^(1/12), computed here rather than by the model. |
| 2101 | 9 | `static BusinessDebtManager lender(String sector, double assets, double...loans)` | A lender with one sector owing these loans against these assets. |
| 2111 | 367 | `static void theSectorDefaultsASliceAtATime() throws Exception` |  |

### 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) (lines 2479-2712)

| line | len | member | says |
|---:|---:|---|---|
| 2493 | 219 | `static void aFailingSectorsPlantIsSoldToTheBuilders() throws Exception` |  |

### 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) (lines 2713-2797)

| line | len | member | says |
|---:|---:|---|---|
| 2728 | 69 | `static void theBankReadsTheQuarter()` |  |

### 19. THE BRANCHES, BY THEIR CUSTOMERS (0.7.19) (lines 2798-3004)

| line | len | member | says |
|---:|---:|---|---|
| 2841 | 163 | `static void theBranchesByTheirCustomers() throws Exception` |  |

### 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) (lines 3005-3157)

| line | len | member | says |
|---:|---:|---|---|
| 3024 | 133 | `static void theDeskIsHeldToTheBanksCapital()` |  |

### 13. WHAT THE BANK TAB READS (0.7.9) (lines 3158-3647)

| line | len | member | says |
|---:|---:|---|---|
| 3174 | 473 | `static void whatTheBankTabReads() throws Exception` |  |

### 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 3648-4017)

| line | len | member | says |
|---:|---:|---|---|
| 3661 | 8 | `static Bank lentOut(double capital, double book)` | A bank with one branch, this much capital and this much lent - equity is the capital, its cash what it lent past it. |
| 3671 | 5 | `static java.util.Map<String, double[]> owing(String sector, double principal, double assets)` | The sectors' positions as the bank's provide() reads them: one sector, owing this against these assets. |
| 3677 | 340 | `static void theBankAsABusinessWithItsCapital()` |  |

### 10. the bank pays for the city's paper (2026-09-21) (lines 4018-4256)

| line | len | member | says |
|---:|---:|---|---|
| 4072 | 184 | `static void theBankPaysForTheCitysPaper() throws Exception` |  |

### 13b. THE TAB DRAWS EACH RATE IN ITS PARTS (0.7.33) (lines 4257-4432)

| line | len | member | says |
|---:|---:|---|---|
| 4275 | 157 | `static void theTabDrawsEachRateInItsParts() throws Exception` |  |


# CentralBankStatementCheck.java - 537 lines · 20 methods · 8 constants · harnesses

`ham/citybuildersim/CentralBankStatementCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The central bank's own statements (CBK1): CentralBankStatements' income statement, balance sheet and money made and
> destroyed held to the central bank's own figures, every month of two played cities, and across a save.
> 
> <p>WHY. Jerus, 2026-11-26, on the Bank tab's two banks: "give both the banks proper statements ... make sure to really
> include alot of stuff into the central bank area". A statement whose subtotals are its own sums has to be shown to land
> on the bank's figures, or it is a second set of books that can drift from the first - the page would print a profit, a
> remittance and a change in M0 nobody had checked (SectorStatementCheck's reason, for the sectors and the commercial
> bank). And the batch may not move a month: its new figures are written beside the bank's and read by nothing in the
> month, which section 5 holds.
> 
> <p>What this has to prove:
> <ol>
>   <li>THE INCOME STATEMENT sums to the profit: its lines are the month's interest and gains less what reserves cost and
>       add to the profit closeMonth() struck with nothing unaccounted; what the strike did with it leaves nothing kept;
>       and the remittance, to the cent: what one month's strike owes the treasury is what the next month pays it, and
>       the loss carried moves by exactly what was made good and what was added.</li>
>   <li>THE BALANCE SHEET balances: its assets are totalAssets(), its liabilities M0, and the assets are the liabilities
>       and equity in both columns; the year-ago column is the sheet as the harness read it twelve months before.</li>
>   <li>THE MONEY MADE AND DESTROYED: each group is getIssued() and getRetired() with nothing unaccounted, it closes on
>       M0, and in every played month made less destroyed is the change in M0 the harness measured from the month
>       before, to the cent.</li>
>   <li>LAST MONTH: the income statement's and the money statement's comparative columns are what they printed a month
>       before.</li>
>   <li>THE PAGE'S FIGURES ARE THE GETTERS': every line is the CentralBank figure it names, the holdings' ladder adds to
>       what the bank holds at face and to its book at market; and reading all of it every month leaves the month's
>       arithmetic where it was - a city read every month ends to the bit where its twin, never read, does.</li>
>   <li>A SAVE AND A LOAD keep every column, and the month after the load prints the saved month as its last; an older
>       save's bank (its array cut where CBK1's slots begin) knows no last month and no year ago, and every other figure
>       is what it was.</li>
> </ol>
> 
> <p>The cities cause what is checked: the test player's city with the holdings dial moved up and back down (paper bought
> and sold, coupons, principal), a month of profit and a month of loss, a remittance, the window and the treasury's
> advances; each is counted, and a count of none fails.

**Uses:** [CentralBankStatements](CentralBankStatements.md) (77), [CentralBank](CentralBank.md) (33), [SectorStatements](SectorStatements.md) (28), [LongPlaytest](LongPlaytest.md) (20), [Game](Game.md) (15), [DebtManager](DebtManager.md) (5), [GameFiles](GameFiles.md) (2), [PriceLevel](PriceLevel.md) (2), [TempDirs](TempDirs.md) (1), [TreasuryFund](TreasuryFund.md) (1)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 127 | · the cities |
| 222 | · one month |
| 416 | · 5. reading changes nothing |
| 453 | · 6. a save and a load |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `CentralBankStatementCheck.CENT` | `CentralBankStatements.CENT` |  |
| 92 | `CentralBankStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of the run, by identity, each under its section's number. |
| 97 | `CentralBankStatementCheck.caused` | `new LinkedHashMap<>()` | What the cities caused: months with each condition, by name. |
| 130 | `CentralBankStatementCheck.QE_FROM` | `100, QE_BACK = 220, MONTHS = 300` | The month the holdings dial goes up in the test player's city, the month it comes down, and the settings. |
| 131 | `CentralBankStatementCheck.QE_UP` | `.30, QE_DOWN =.05` |  |
| 133 | `CentralBankStatementCheck.DRY_AT` | `60, REFILLED_AT = 84` | ...and the months its treasury is emptied, and filled again. |
| 431 | `CentralBankStatementCheck.PLAIN_MONTHS` | `120` |  |
| 511 | `CentralBankStatementCheck.BEFORE_CBK1` | `13 + CentralBank.REVENUE_MONTHS + 14` | The slots of the central bank's array before CBK1's: 13, the revenue ring, and the fourteen 0.7.1 to FB14 appended. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 51 | `static PrintStream out` |  |
| 52 | `static PrintStream quiet` |  |
| 69 | `int checked, missed` |  |
| 70 | `double worst` |  |
| 71 | `String where` |  |
| 105 | `SectorStatements.Table income, money` |  |
| 107 | `final Map<Integer, SectorStatements.Table> openings` | The sheet as each month opened, by the month it opened: read before the month ran, after whatever the player did. |
| 108 | `double m0` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 490 | **type** `public class CentralBankStatementCheck` | The central bank's own statements (CBK1): CentralBankStatements' income statement, balance sheet and money made and destroyed held to the central bank's own figures, every month of two played cities, and across a save. |
| 56 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 61 | 5 | `static void quietly(Runnable r)` |  |
| 68 | 22 | **type** `static final class Tally` | One identity's tally over a run: how many times it was checked, how many missed by more than a cent, and the worst miss. |
| 73 | 11 | `void near(double actual, double expected, String at)` _(in CentralBankStatementCheck.Tally)_ |  |
| 85 | 4 | `String words()` _(in CentralBankStatementCheck.Tally)_ |  |
| 94 | 1 | `static Tally t(String name)` |  |
| 99 | 3 | `static void count(String what, boolean when)` |  |
| 104 | 6 | **type** `static final class Memory` | One city's memory between its months: last month's statements, the sheet as each month opened, M0 and what the strike owed. |
| 111 | 15 | `public static void main(String[] args) throws Exception` |  |

### the cities (lines 127-221)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 68 | `static Game playersCity(Path root)` | The test player's city (LongPlaytest's founding and rhythm, SectorStatementCheck's), the holdings dial moved up and back. |
| 205 | 1 | `static String pct(double v)` |  |
| 208 | 1 | `static String thousands(double v)` | Money in the model's thousands, as a line of the log says it. |
| 210 | 11 | `static void months(Game g, int n, Memory mem)` |  |

### one month (lines 222-415)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 149 | `static void audit(Game g, Memory mem)` | Sections 1 to 5 on the month just played. |
| 376 | 6 | `static void lastMonthIs(SectorStatements.Table now, SectorStatements.Table before, String label, String at)` | Every row of `now` whose last-month column is known is the same row of the statement a month before. |
| 384 | 3 | `static void lineIs(SectorStatements.Table table, String id, double getter, String at)` | A line of a statement is the getter it names. |
| 389 | 26 | `static void everyMonth()` | Sections 1 to 5's verdicts: each identity's tally, then what the city caused. |

### 5. reading changes nothing (lines 416-452)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 11 | `static void readChangesNothing(Path root)` | A plain founding read every month as the pages read it, against its twin never read: the same city to the bit. |
| 433 | 19 | `static Game plain(Path dir, boolean readEveryMonth)` |  |

### 6. a save and a load (lines 453-537)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 46 | `static void savedAndLoaded(Game g)` |  |
| 502 | 7 | `static void sameTable(Tally x, SectorStatements.Table a, SectorStatements.Table b, String which)` |  |
| 513 | 24 | `static void anOlderSave(Game g)` |  |


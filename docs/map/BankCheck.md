# BankCheck.java - 5,097 lines · 35 methods · 24 constants · harnesses

`ham/citybuildersim/BankCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

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
>      causes, and an older save does not invent them. Those are read on a
>      played city whose founding, loan, owners' capital, dividend, buyback,
>      loss, bond, treasury buyback and rescue are each set in a month of
>      its own (TAB_* dials; FS9: a played city's own path moved under them). Since 0.7.33
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
> ... (75 more lines in the source)

**Uses:** [Bank](Bank.md) (394), [BusinessDebtManager](BusinessDebtManager.md) (124), [HouseholdBalance](HouseholdBalance.md) (47), [Sectors](Sectors.md) (46), [Game](Game.md) (44), [MoneyAudit](MoneyAudit.md) (44), [ExchangeCheck](ExchangeCheck.md) (33), [Equity](Equity.md) (25), [PriceLevel](PriceLevel.md) (20), [Exchange](Exchange.md) (19), [FamilyStructure](FamilyStructure.md) (19), [PayTier](PayTier.md) (16), [GameFiles](GameFiles.md) (16), [DebtManager](DebtManager.md) (15), [OrderBook](OrderBook.md) (12), [Founding](Founding.md) (10), [DecisionLog](DecisionLog.md) (10), [CentralBank](CentralBank.md) (9), [BusinessInvestment](BusinessInvestment.md) (9), [TempDirs](TempDirs.md) (8), [Household](Household.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [Sector](Sector.md) (4), [BusinessLoan](BusinessLoan.md) (3), [WageBand](WageBand.md) (3), [SectorBooks](SectorBooks.md) (3), [JobType](JobType.md) (3), [Ladder](Ladder.md) (3), [Debt](Debt.md) (3), [ChartModel](ChartModel.md) (3)... and 16 more

**Used by (3):** [BondCheck](BondCheck.md), [ModernRowsCheck](ModernRowsCheck.md), [YoungBankCheck](YoungBankCheck.md)

## Sections

| line | section |
|---:|---|
| 315 | · 1. the two limits |
| 361 | · 1b. WHAT TO PAY SAVERS IS A DECISION |
| 460 | · · ...and it does not open counters either |
| 510 | · 2. STRAIN IS NOT A PRICE (0.7.7) |
| 688 | · 3. WHAT IT PAYS, WHAT IT TAKES, WHAT IT KEEPS (0.7.7) |
| 908 | · 4. a real city, and its money |
| 1036 | · 5. the save carries the bank's cash |
| 1097 | · 6. a family that cannot carry it |
| 1228 | · 7. and the city opens its own |
| 1483 | · 8. the accounting identities, on a played city |
| 1822 | · 8b. the trading desk's statement foots |
| 1931 | · 12. a reload reads the same month (0.7.8) |
| 2152 | · 9. capital is the constraint, and it can run out |
| 2244 | 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) |
| 2671 | 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) |
| 2922 | 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) |
| 3007 | 19. THE BRANCHES, BY THEIR CUSTOMERS (0.7.19) |
| 3230 | 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) |
| 3383 | 13. WHAT THE BANK TAB READS (0.7.9) |
| 3987 | 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) |
| 4365 | 10. the bank pays for the city's paper (2026-09-21) |
| 4613 | 13b. THE TAB DRAWS EACH RATE IN ITS PARTS (0.7.33) |
| 4792 | THE PAGE'S TWO INTEGRITY CARDS READ THE BOOK'S SIZE (BI1, 2026-11-20) |
| 4959 | THE EQUITY-MOVEMENT ALERT READS THE MOVEMENT'S SIZE (BI2, 2026-11-20) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 152 | `BankCheck.BOOKING_MONTHS` | `1` | The months a town is played after a loan is written by hand, for the bank to read its books at the close of one: a month, which is when refreshBank() reads them. |
| 155 | `BankCheck.LOAN_BY_HAND` | `50` | What each sector is lent by hand, $k, in the towns that read a bank's books: more than the books' rounding and less than a sector's assets, so nobody defaults in the month. |
| 158 | `BankCheck.CARRY_HEADROOM_BY_HAND` | `3_000` | The bank's spare book the carry's own rule may lend from when foreigners borrow by hand, $k: wide enough that the rule's target, a share of it, is more than the stock it moves in a month. |
| 161 | `BankCheck.ALLOWANCE_BY_HAND` | `10` | What the families' allowance is raised by, by hand, before a reload between two months, $k: more than a reload could round away. |
| 164 | `BankCheck.SLICE_TOWN_MONTHS` | `1` | The months the town of the slice section is played before the claim is first taken on: the first, which gives each sector its books and the lender its assets to read. |
| 167 | `BankCheck.SLICE_LEVERAGE` | `1.2` | What retail owes against what it owns, taken on again between each month of the slice section: past the watch line and under the default point, so a slice defaults each month and no month is news. |
| 170 | `BankCheck.PAST_THE_LINE_LEVERAGE` | `2` | What retail owes against what it owns when its month is to be news, after the slice section's quiet months: twice what it owns, past the default point (BusinessDebtManager.INSOLVENCY_TRIGGER). |
| 173 | `BankCheck.FAMILIES_WITHIN_MONTHS` | `12` | The most months a new town is waited for its families to arrive and want groceries, before retail is put in distress: the capacity rule reads no demand until they have, and past this the wait fails as a fixture. |
| 176 | `BankCheck.CHARTER_COVER_UNDER` | `.1` | How far the town's fees leave the charter under its cost, as a share of it, for the law that it never closes "however far": under a tenth. |
| 179 | `BankCheck.BUILDERS_CASH_FOR_A_SALE` | `100_000` | What the builders hold, at least, before a sale of plant, $k: more than the material of the shop retail holds at the day's price, so they can pay for all of it. |
| 182 | `BankCheck.PARTS_TOWN_MONTHS` | `1` | The months the town of the rate-parts section is played before its lender is handed a record and a concentration: the first, which prices every sector. |
| 185 | `BankCheck.CONCENTRATION_BY_HAND` | `.004` | The concentration charge the rate-parts section hands one sector, a year: more than the rates' own rounding and less than a spread. |
| 188 | `BankCheck.TAB_LEND_MONTH` | `2` | The month of the tab city, counted from the first played, in which a claim on retail is written by hand (BusinessDebtManager.issueLoan) and paid out by the bank: after the founding month, with retail's books standing. |
| 191 | `BankCheck.TAB_LEND_SHARE` | `.5` | The share of the room retail has under the lender's ceiling (MAX_LOAN_TO_ASSETS of its assets, less what it owes) that the tab city's by-hand claim takes: half, so retail is lent to and stays far from every line. |
| 194 | `BankCheck.TAB_CAPITAL_MONTH` | `5` | The month of the tab city in which its owners put the bank one top of its band's equity in by hand (Bank.injectCapital), between two presses, so that the next month it holds that over the top and its rule pays a twelf... |
| 197 | `BankCheck.TAB_BUYBACK_MONTH` | `9` | The month of the tab city in which the bank's desk buys its own shares back by hand (Bank.buyBackOwnShares), between two presses and after the capital above, so there is spare capital over its target to spend. |
| 200 | `BankCheck.TAB_BUYBACK_SHARE` | `.5` | The share of its spare capital over its target that the tab city's by-hand buyback spends: half, so the bank ends the month over its target, as the desk's own rule would leave it. |
| 203 | `BankCheck.TAB_LOSS_MONTH` | `20` | The month of the tab city in which a claim past the default point is written on retail by hand (the section-14 dial PAST_THE_LINE_LEVERAGE), so its firms default in the month after, whole: a year and a half after the ... |
| 206 | `BankCheck.TAB_BOND_MONTH` | `12` | The month of the tab city in which the city borrows on a bond that the bank buys at the next settle. |
| 209 | `BankCheck.TAB_TREASURY_BUYBACK_MONTH` | `30` | The month of the tab city in which the treasury buys that bond back between two presses, which is in no income statement. |
| 212 | `BankCheck.TAB_RESCUE_MONTH` | `45` | The month of the tab city in which the city puts capital into the bank that stands, between two presses: its preferred, since 0.7.14. |
| 215 | `BankCheck.TAB_PLAYED` | `TAB_RESCUE_MONTH + 1 + Bank.YEAR_MONTHS` | The months the tab city is played: through its last caused event, the rescue, and the year after it that a year ago on the Balance sheet page needs to read a sheet that has the rescue in it. |
| 223 | `BankCheck.DESK_MONTHS` | `6` | The months the trading desk is stepped after the leavers sell it their shares, so it goes on to hold, re-mark and sell what it bought. |
| 5010 | `BankCheck.OLD_ABSOLUTE` | `.005` | What the Bank page's three sites tested before BI2: an absolute $5 (.005 of a thousand). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 147 | `static int fails` |  |
| 148 | `static PrintStream out` |  |
| 149 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 145 | 4953 | **type** `public class BankCheck` | The commercial bank, and the families it discharges. |
| 218 | 3 | `static boolean parted(double drawn, double priced)` | Whether a rate drawn in its parts is the rate priced, to 1e-12 of it: the parts' arithmetic can part from the pricing's by an ulp on another path. |
| 225 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 231 | 5 | `static double[] sheetOf(Bank bank)` | The bank's balance sheet as it stands, every line of it (0.7.13). |
| 237 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 252 | 11 | `static void playRecordingEarningAssets(Game city, int months, java.util.List<Double> closed)` | Plays a city this many months, one at a time and quietly, and adds to the list the bank's earning assets as each month closed - the figure the sheet ring files at the next startMonth() (BK1). |
| 264 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 277 | 9 | `static double deskParts(Exchange exchange, Equity register, Bank bank)` | The trading desk's statement as the bank screen opens it, less the re-mark: sold to households and abroad, bought from both, dividends on the inventory, tendered into buybacks - the same getters BankScreen reads, with... |
| 295 | 10 | `static double capitalMoved(Bank b)` | Everything that moves the bank's equity that is not its net income: capital put in (both halves), the treasury's, the founding settlement, less its dividend - and since 0.7.8 its own shares, issued or bought back, whi... |
| 306 | 3 | `static double deskParts(Game game)` |  |
| 310 | 1933 | `public static void main(String[] args) throws Exception` |  |

### 14. A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8) (lines 2244-2670)

| line | len | member | says |
|---:|---:|---|---|
| 2267 | 9 | `static double staged(double principal, double leverage)` | The allowance the round-2 brief writes down, staged firm by firm, computed here rather than by the model: principal x ((1 - s2) x EL12 + s2 x ELlife), s2 = N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY), EL12 = m... |
| 2278 | 5 | `static double hazard(double leverage)` | The monthly hazard the brief writes down, 1 - (1 - PD)^(1/12), computed here rather than by the model. |
| 2285 | 9 | `static BusinessDebtManager lender(String sector, double assets, double...loans)` | A lender with one sector owing these loans against these assets. |
| 2295 | 375 | `static void theSectorDefaultsASliceAtATime() throws Exception` |  |

### 15. A FAILING SECTOR'S PLANT, SOLD TO THE BUILDERS (0.7.8) (lines 2671-2921)

| line | len | member | says |
|---:|---:|---|---|
| 2693 | 10 | `static void holdFromOrdering(Game city, String sector, double overdraft)` | Retail held from ordering a shop, as the first sale's loop holds it (BA1, 2026-11-05): credit shut for twelve months, the register's twelve losing months so the owners raise nothing, and the till overdrawn by `overdra... |
| 2705 | 5 | `static void restoreStreak(BusinessInvestment plans, String sector, int months)` | Sets a sector's loss streak, the months the distress rule reads, and leaves every other sector's as it stands. |
| 2711 | 210 | `static void aFailingSectorsPlantIsSoldToTheBuilders() throws Exception` |  |

### 16. THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8) (lines 2922-3006)

| line | len | member | says |
|---:|---:|---|---|
| 2937 | 69 | `static void theBankReadsTheQuarter()` |  |

### 19. THE BRANCHES, BY THEIR CUSTOMERS (0.7.19) (lines 3007-3229)

| line | len | member | says |
|---:|---:|---|---|
| 3050 | 179 | `static void theBranchesByTheirCustomers() throws Exception` |  |

### 17. THE DESK HELD TO THE BANK'S CAPITAL (0.7.8) (lines 3230-3382)

| line | len | member | says |
|---:|---:|---|---|
| 3249 | 133 | `static void theDeskIsHeldToTheBanksCapital()` |  |

### 13. WHAT THE BANK TAB READS (0.7.9) (lines 3383-3986)

| line | len | member | says |
|---:|---:|---|---|
| 3399 | 587 | `static void whatTheBankTabReads() throws Exception` |  |

### 7-11. THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8) (lines 3987-4364)

| line | len | member | says |
|---:|---:|---|---|
| 4000 | 8 | `static Bank lentOut(double capital, double book)` | A bank with one branch, this much capital and this much lent - equity is the capital, its cash what it lent past it. |
| 4010 | 5 | `static java.util.Map<String, double[]> owing(String sector, double principal, double assets)` | The sectors' positions as the bank's provide() reads them: one sector, owing this against these assets. |
| 4016 | 348 | `static void theBankAsABusinessWithItsCapital()` |  |

### 10. the bank pays for the city's paper (2026-09-21) (lines 4365-4612)

| line | len | member | says |
|---:|---:|---|---|
| 4419 | 193 | `static void theBankPaysForTheCitysPaper() throws Exception` |  |

### 13b. THE TAB DRAWS EACH RATE IN ITS PARTS (0.7.33) (lines 4613-4791)

| line | len | member | says |
|---:|---:|---|---|
| 4631 | 160 | `static void theTabDrawsEachRateInItsParts() throws Exception` |  |

### THE PAGE'S TWO INTEGRITY CARDS READ THE BOOK'S SIZE (BI1, 2026-11-20) (lines 4792-4958)

| line | len | member | says |
|---:|---:|---|---|
| 4812 | 12 | `static Bank bookOf(double scale, int k)` | A bank built by hand whose books are of this scale, k giving each figure its fraction so that their sums round (BI1): no city, no save. |
| 4826 | 9 | `static void fileYearAgo(Bank bank, double off)` | Files the bank's own sheet as the one a year ago, its lines `off` more than its equity line (a residual of `off`): the save's own path (restoreSheetYear). |
| 4837 | 4 | `static void splitEquity(Bank bank, double off)` | Splits the bank's equity into paid in and retained, `off` more than equity makes: what a save carries (restoreEquitySplit), the preferred being nothing here. |
| 4843 | 7 | `static boolean aBankOfNoSplitOrYear()` | A bank that has kept no split (a save from before the bank kept it) and no year of sheets reads both as adding up. |
| 4851 | 107 | `static void theIntegrityCardsReadTheBooksSize()` |  |

### THE EQUITY-MOVEMENT ALERT READS THE MOVEMENT'S SIZE (BI2, 2026-11-20) (lines 4959-5097)

| line | len | member | says |
|---:|---:|---|---|
| 4980 | 22 | `static double[] movementFigures(double scale, int k, double out)` | The seventeen figures of a movement by hand, in the record's order (opening ... |
| 5003 | 3 | `static Bank.EquityMovement movementFrom(double[] f)` |  |
| 5007 | 1 | `static Bank.EquityMovement movementOf(double scale, int k, double out)` |  |
| 5012 | 85 | `static void theEquityMovementAlertReadsTheMovementsSize()` |  |


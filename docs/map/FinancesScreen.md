# FinancesScreen.java - 2,238 lines · 35 methods · 8 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The Finances tab: the position, the ladder of what the city owes, debt
> service, home and abroad, your rate taken apart, the book, buying back,
> and borrowing - at home or in somebody else's money - and, since 0.7.0,
> the money itself: the central bank's books, M0 and M2; and since 0.7.12
> the businesses' bond market beside the city's own.
> 
> Split out of UserInterface on 2026-09-18: the eleven banners from FINANCES
> to BORROW exactly as they were, the shell's members reached through ui. The
> shell still reads which page and area are open (financePage, financeArea)
> for the rail and the scroll memory. THE DEBT RESULT - what a debt decision
> did, shown after it - had sat at the tail of the build cards and came here
> the same day, because it is this tab's.

**Uses:** [Palette](Palette.md) (301), [DebtManager](DebtManager.md) (16), [Debt](Debt.md) (12), [CityCalendar](CityCalendar.md) (10), [CorporateBond](CorporateBond.md) (10), [Currency](Currency.md) (6), [OrderBook](OrderBook.md) (6), [Bank](Bank.md) (5), [BondMarket](BondMarket.md) (3), [NationalAccounts](NationalAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [UserInterface](UserInterface.md) (2), [CentralBank](CentralBank.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [HistorySave](HistorySave.md) (1), [BankScreen](BankScreen.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 42 | FINANCES |
| 140 | THE LANDING |
| 215 | · · the standing warnings |
| 344 | ONE SUBJECT, ITS OWN STRIP |
| 407 | THE POSITION |
| 441 | · · the credit |
| 459 | · · against the economy |
| 503 | THE LADDER |
| 542 | · · and what it adds up to |
| 705 | · · the key |
| 729 | DEBT SERVICE |
| 763 | · · against the take |
| 786 | · · what the world sees |
| 856 | HOME AND ABROAD |
| 895 | · · the foreign half |
| 939 | YOUR RATE, TAKEN APART |
| 1025 | · · how far each measure has run |
| 1104 | THE BOOK |
| 1171 | · · who holds it (0.7.1) |
| 1202 | BUY BACK |
| 1356 | BORROW |
| 1394 | · · what to sell |
| 1415 | · · the curve (0.7.1) |
| 1445 | · · for how long |
| 1469 | · · how much |
| 1521 | · · the quote |
| 1581 | · · the ladder, with this bond in it |
| 1589 | · · who is buying it |
| 1592 | · · and where the dollars go |
| 1609 | · · the button |
| 1804 | MONEY (0.7.0) |
| 1821 | · · in two sentences |
| 1855 | · · the balance sheet |
| 1903 | · · this month |
| 1933 | · · M0 and M2 |
| 1973 | THE BOND MARKET (0.7.12) |
| 2070 | · · the month |
| 2102 | · · the order books |
| 2199 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 80 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 82 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 83 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 85 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 87 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 106 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupo...` |  |
| 520 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 76 | `String financeArea` | Which subject is open. |
| 78 | `String financePage` |  |
| 135 | `String borrowType` | what the borrow page is currently asking for |
| 136 | `int borrowTerm` |  |
| 137 | `double borrowAsk` |  |
| 138 | `boolean borrowHold` |  |
| 2121 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 2204 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money - and, since 0.7.0, th... |
| 40 | 1 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 42-139)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 121 | 4 | `static Instrument instrument(String key)` |  |
| 126 | 7 | `static String instrumentColour(String type)` |  |

### THE LANDING (lines 140-343)

| line | len | member | says |
|---:|---:|---|---|
| 144 | 96 | `void showFinanceMenu()` |  |
| 248 | 42 | `HBox financeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the landing page. |
| 292 | 44 | `HBox financeVitals()` | The four figures that are true of the whole tab. |
| 338 | 5 | `double couponNow(DebtManager owed)` | What the city is charged in coupon each month, across every instrument. |

### ONE SUBJECT, ITS OWN STRIP (lines 344-406)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 58 | `void drawFinanceScreen()` |  |

### THE POSITION (lines 407-502)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 81 | `void positionPage(VBox column)` |  |
| 493 | 9 | `int ratioRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double annual, String tone)` |  |

### THE LADDER (lines 503-728)

| line | len | member | says |
|---:|---:|---|---|
| 522 | 58 | `void ladderPage(VBox column)` |  |
| 587 | 23 | `double[][] ladderYears(java.util.List<Debt> paper, double[] extra)` | What falls due in each of the next LADDER_YEARS years, split by instrument. |
| 617 | 111 | `VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel)` | The ladder itself: a bar per year, stacked by instrument. |

### DEBT SERVICE (lines 729-855)

| line | len | member | says |
|---:|---:|---|---|
| 739 | 64 | `void debtServicePage(VBox column)` |  |
| 810 | 45 | `VBox serviceBands(double share)` | The three bands a debt-service ratio falls in, with the city's own mark. |

### HOME AND ABROAD (lines 856-938)

| line | len | member | says |
|---:|---:|---|---|
| 860 | 78 | `void homeAndAbroadPage(VBox column)` |  |

### YOUR RATE, TAKEN APART (lines 939-1103)

| line | len | member | says |
|---:|---:|---|---|
| 943 | 106 | `void yourRatePage(VBox column)` |  |
| 1050 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1062 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1104-1201)

| line | len | member | says |
|---:|---:|---|---|
| 1108 | 93 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1202-1355)

| line | len | member | says |
|---:|---:|---|---|
| 1225 | 45 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1272 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### BORROW (lines 1356-1803)

| line | len | member | says |
|---:|---:|---|---|
| 1373 | 261 | `void borrowPage(VBox column, boolean foreign)` |  |
| 1642 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 1697 | 41 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 1740 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 1759 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### MONEY (0.7.0) (lines 1804-1972)

| line | len | member | says |
|---:|---:|---|---|
| 1816 | 150 | `void moneyPage(VBox column)` |  |
| 1968 | 4 | `static double[] lastYear(double[] series)` | The last twelve months of a series, or all of it if the city is younger. |

### THE BOND MARKET (0.7.12) (lines 1973-2198)

| line | len | member | says |
|---:|---:|---|---|
| 1986 | 6 | `double businessDebt()` | What every business owes, bank loans and bonds together. |
| 1994 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 1998 | 121 | `void bondMarketPage(VBox column)` |  |
| 2123 | 75 | `void bondBookPage(VBox column)` |  |

### THE DEBT RESULT (lines 2199-2238)

| line | len | member | says |
|---:|---:|---|---|
| 2210 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 2220 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


# FinancesScreen.java - 2,334 lines · 36 methods · 9 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The Finances tab: the position, the ladder of what the city owes, debt
> service, home and abroad, your rate taken apart, the book, buying back,
> and borrowing - at home or in somebody else's money - and, since 0.7.0,
> the money itself: the central bank's books, M0 and M2; since 0.7.12
> the businesses' bond market beside the city's own; and since 0.7.13, at
> the top of both borrow pages, the treasury's rollover of what falls due.
> 
> Split out of UserInterface on 2026-09-18: the eleven banners from FINANCES
> to BORROW exactly as they were, the shell's members reached through ui. The
> shell still reads which page and area are open (financePage, financeArea)
> for the rail and the scroll memory. THE DEBT RESULT - what a debt decision
> did, shown after it - had sat at the tail of the build cards and came here
> the same day, because it is this tab's.

**Uses:** [Palette](Palette.md) (309), [DebtManager](DebtManager.md) (16), [Debt](Debt.md) (12), [CityCalendar](CityCalendar.md) (11), [CorporateBond](CorporateBond.md) (10), [Rollover](Rollover.md) (7), [Currency](Currency.md) (6), [OrderBook](OrderBook.md) (6), [Bank](Bank.md) (5), [BondMarket](BondMarket.md) (3), [NationalAccounts](NationalAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [UserInterface](UserInterface.md) (2), [CentralBank](CentralBank.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1), [Game](Game.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [HistorySave](HistorySave.md) (1), [BankScreen](BankScreen.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 43 | FINANCES |
| 141 | THE LANDING |
| 216 | · · the standing warnings |
| 345 | ONE SUBJECT, ITS OWN STRIP |
| 408 | THE POSITION |
| 442 | · · the credit |
| 460 | · · against the economy |
| 504 | THE LADDER |
| 543 | · · and what it adds up to |
| 706 | · · the key |
| 730 | DEBT SERVICE |
| 764 | · · against the take |
| 787 | · · what the world sees |
| 857 | HOME AND ABROAD |
| 896 | · · the foreign half |
| 940 | YOUR RATE, TAKEN APART |
| 1026 | · · how far each measure has run |
| 1105 | THE BOOK |
| 1172 | · · who holds it (0.7.1) |
| 1203 | BUY BACK |
| 1357 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1450 | BORROW |
| 1490 | · · what to sell |
| 1511 | · · the curve (0.7.1) |
| 1541 | · · for how long |
| 1565 | · · how much |
| 1617 | · · the quote |
| 1677 | · · the ladder, with this bond in it |
| 1685 | · · who is buying it |
| 1688 | · · and where the dollars go |
| 1705 | · · the button |
| 1900 | MONEY (0.7.0) |
| 1917 | · · in two sentences |
| 1951 | · · the balance sheet |
| 1999 | · · this month |
| 2029 | · · M0 and M2 |
| 2069 | THE BOND MARKET (0.7.12) |
| 2166 | · · the month |
| 2198 | · · the order books |
| 2295 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 81 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 83 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 84 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 86 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 88 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 107 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupo...` |  |
| 521 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |
| 1369 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", "12-month notes" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 77 | `String financeArea` | Which subject is open. |
| 79 | `String financePage` |  |
| 136 | `String borrowType` | what the borrow page is currently asking for |
| 137 | `int borrowTerm` |  |
| 138 | `double borrowAsk` |  |
| 139 | `boolean borrowHold` |  |
| 2217 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 2299 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money - and, since 0.7.0, th... |
| 41 | 1 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 43-140)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 122 | 4 | `static Instrument instrument(String key)` |  |
| 127 | 7 | `static String instrumentColour(String type)` |  |

### THE LANDING (lines 141-344)

| line | len | member | says |
|---:|---:|---|---|
| 145 | 96 | `void showFinanceMenu()` |  |
| 249 | 42 | `HBox financeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the landing page. |
| 293 | 44 | `HBox financeVitals()` | The four figures that are true of the whole tab. |
| 339 | 5 | `double couponNow(DebtManager owed)` | What the city is charged in coupon each month, across every instrument. |

### ONE SUBJECT, ITS OWN STRIP (lines 345-407)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 58 | `void drawFinanceScreen()` |  |

### THE POSITION (lines 408-503)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 81 | `void positionPage(VBox column)` |  |
| 494 | 9 | `int ratioRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double annual, String tone)` |  |

### THE LADDER (lines 504-729)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 58 | `void ladderPage(VBox column)` |  |
| 588 | 23 | `double[][] ladderYears(java.util.List<Debt> paper, double[] extra)` | What falls due in each of the next LADDER_YEARS years, split by instrument. |
| 618 | 111 | `VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel)` | The ladder itself: a bar per year, stacked by instrument. |

### DEBT SERVICE (lines 730-856)

| line | len | member | says |
|---:|---:|---|---|
| 740 | 64 | `void debtServicePage(VBox column)` |  |
| 811 | 45 | `VBox serviceBands(double share)` | The three bands a debt-service ratio falls in, with the city's own mark. |

### HOME AND ABROAD (lines 857-939)

| line | len | member | says |
|---:|---:|---|---|
| 861 | 78 | `void homeAndAbroadPage(VBox column)` |  |

### YOUR RATE, TAKEN APART (lines 940-1104)

| line | len | member | says |
|---:|---:|---|---|
| 944 | 106 | `void yourRatePage(VBox column)` |  |
| 1051 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1063 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1105-1202)

| line | len | member | says |
|---:|---:|---|---|
| 1109 | 93 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1203-1356)

| line | len | member | says |
|---:|---:|---|---|
| 1226 | 45 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1273 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1357-1449)

| line | len | member | says |
|---:|---:|---|---|
| 1371 | 78 | `void rolloverBlock(VBox column)` |  |

### BORROW (lines 1450-1899)

| line | len | member | says |
|---:|---:|---|---|
| 1467 | 263 | `void borrowPage(VBox column, boolean foreign)` |  |
| 1738 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 1793 | 41 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 1836 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 1855 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### MONEY (0.7.0) (lines 1900-2068)

| line | len | member | says |
|---:|---:|---|---|
| 1912 | 150 | `void moneyPage(VBox column)` |  |
| 2064 | 4 | `static double[] lastYear(double[] series)` | The last twelve months of a series, or all of it if the city is younger. |

### THE BOND MARKET (0.7.12) (lines 2069-2294)

| line | len | member | says |
|---:|---:|---|---|
| 2082 | 6 | `double businessDebt()` | What every business owes, bank loans and bonds together. |
| 2090 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 2094 | 121 | `void bondMarketPage(VBox column)` |  |
| 2219 | 75 | `void bondBookPage(VBox column)` |  |

### THE DEBT RESULT (lines 2295-2334)

| line | len | member | says |
|---:|---:|---|---|
| 2306 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 2316 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


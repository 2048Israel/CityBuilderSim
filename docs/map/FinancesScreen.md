# FinancesScreen.java - 2,002 lines · 31 methods · 7 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The Finances tab: the position, the ladder of what the city owes, debt
> service, home and abroad, your rate taken apart, the book, buying back,
> and borrowing - at home or in somebody else's money - and, since 0.7.0,
> the money itself: the central bank's books, M0 and M2.
> 
> Split out of UserInterface on 2026-09-18: the eleven banners from FINANCES
> to BORROW exactly as they were, the shell's members reached through ui. The
> shell still reads which page and area are open (financePage, financeArea)
> for the rail and the scroll memory. THE DEBT RESULT - what a debt decision
> did, shown after it - had sat at the tail of the build cards and came here
> the same day, because it is this tab's.

**Uses:** [Palette](Palette.md) (260), [DebtManager](DebtManager.md) (16), [Debt](Debt.md) (12), [Currency](Currency.md) (6), [CityCalendar](CityCalendar.md) (4), [Bank](Bank.md) (4), [NationalAccounts](NationalAccounts.md) (3), [UserInterface](UserInterface.md) (2), [CentralBank](CentralBank.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [HistorySave](HistorySave.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | FINANCES |
| 135 | THE LANDING |
| 201 | · · the standing warnings |
| 330 | ONE SUBJECT, ITS OWN STRIP |
| 388 | THE POSITION |
| 422 | · · the credit |
| 440 | · · against the economy |
| 484 | THE LADDER |
| 523 | · · and what it adds up to |
| 686 | · · the key |
| 710 | DEBT SERVICE |
| 744 | · · against the take |
| 767 | · · what the world sees |
| 837 | HOME AND ABROAD |
| 876 | · · the foreign half |
| 920 | YOUR RATE, TAKEN APART |
| 1009 | · · how far each measure has run |
| 1094 | THE BOOK |
| 1161 | · · who holds it (0.7.1) |
| 1192 | BUY BACK |
| 1346 | BORROW |
| 1384 | · · what to sell |
| 1405 | · · the curve (0.7.1) |
| 1435 | · · for how long |
| 1459 | · · how much |
| 1511 | · · the quote |
| 1571 | · · the ladder, with this bond in it |
| 1579 | · · who is buying it |
| 1582 | · · and where the dollars go |
| 1599 | · · the button |
| 1796 | MONEY (0.7.0) |
| 1813 | · · in two sentences |
| 1847 | · · the balance sheet |
| 1895 | · · this month |
| 1925 | · · M0 and M2 |
| 1965 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 77 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 79 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 80 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 82 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 101 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupo...` |  |
| 501 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 73 | `String financeArea` | Which subject is open. |
| 75 | `String financePage` |  |
| 130 | `String borrowType` | what the borrow page is currently asking for |
| 131 | `int borrowTerm` |  |
| 132 | `double borrowAsk` |  |
| 133 | `boolean borrowHold` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 1969 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money - and, since 0.7.0, th... |
| 39 | 1 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 41-134)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 116 | 4 | `static Instrument instrument(String key)` |  |
| 121 | 7 | `static String instrumentColour(String type)` |  |

### THE LANDING (lines 135-329)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 87 | `void showFinanceMenu()` |  |
| 234 | 42 | `HBox financeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the landing page. |
| 278 | 44 | `HBox financeVitals()` | The four figures that are true of the whole tab. |
| 324 | 5 | `double couponNow(DebtManager owed)` | What the city is charged in coupon each month, across every instrument. |

### ONE SUBJECT, ITS OWN STRIP (lines 330-387)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 53 | `void drawFinanceScreen()` |  |

### THE POSITION (lines 388-483)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 81 | `void positionPage(VBox column)` |  |
| 474 | 9 | `int ratioRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double annual, String tone)` |  |

### THE LADDER (lines 484-709)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 58 | `void ladderPage(VBox column)` |  |
| 568 | 23 | `double[][] ladderYears(java.util.List<Debt> paper, double[] extra)` | What falls due in each of the next LADDER_YEARS years, split by instrument. |
| 598 | 111 | `VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel)` | The ladder itself: a bar per year, stacked by instrument. |

### DEBT SERVICE (lines 710-836)

| line | len | member | says |
|---:|---:|---|---|
| 720 | 64 | `void debtServicePage(VBox column)` |  |
| 791 | 45 | `VBox serviceBands(double share)` | The three bands a debt-service ratio falls in, with the city's own mark. |

### HOME AND ABROAD (lines 837-919)

| line | len | member | says |
|---:|---:|---|---|
| 841 | 78 | `void homeAndAbroadPage(VBox column)` |  |

### YOUR RATE, TAKEN APART (lines 920-1093)

| line | len | member | says |
|---:|---:|---|---|
| 924 | 115 | `void yourRatePage(VBox column)` |  |
| 1040 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1052 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1094-1191)

| line | len | member | says |
|---:|---:|---|---|
| 1098 | 93 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1192-1345)

| line | len | member | says |
|---:|---:|---|---|
| 1215 | 45 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1262 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### BORROW (lines 1346-1795)

| line | len | member | says |
|---:|---:|---|---|
| 1363 | 261 | `void borrowPage(VBox column, boolean foreign)` |  |
| 1632 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 1686 | 44 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 1732 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 1751 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### MONEY (0.7.0) (lines 1796-1964)

| line | len | member | says |
|---:|---:|---|---|
| 1808 | 150 | `void moneyPage(VBox column)` |  |
| 1960 | 4 | `static double[] lastYear(double[] series)` | The last twelve months of a series, or all of it if the city is younger. |

### THE DEBT RESULT (lines 1965-2002)

| line | len | member | says |
|---:|---:|---|---|
| 1974 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 1984 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


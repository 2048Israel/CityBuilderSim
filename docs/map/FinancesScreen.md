# FinancesScreen.java - 1,713 lines · 29 methods · 6 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The Finances tab: the position, the ladder of what the city owes, debt
> service, home and abroad, your rate taken apart, the book, buying back,
> and borrowing - at home or in somebody else's money.
> 
> Split out of UserInterface on 2026-09-18: the eleven banners from FINANCES
> to BORROW exactly as they were, the shell's members reached through ui. The
> shell still reads which page and area are open (financePage, financeArea)
> for the rail and the scroll memory. THE DEBT RESULT - what a debt decision
> did, shown after it - had sat at the tail of the build cards and came here
> the same day, because it is this tab's.

**Uses:** [Palette](Palette.md) (213), [DebtManager](DebtManager.md) (16), [Debt](Debt.md) (12), [Currency](Currency.md) (6), [CityCalendar](CityCalendar.md) (4), [NationalAccounts](NationalAccounts.md) (3), [Bank](Bank.md) (3), [UserInterface](UserInterface.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 40 | FINANCES |
| 125 | THE LANDING |
| 183 | · · the standing warnings |
| 310 | ONE SUBJECT, ITS OWN STRIP |
| 366 | THE POSITION |
| 400 | · · the credit |
| 418 | · · against the economy |
| 462 | THE LADDER |
| 501 | · · and what it adds up to |
| 664 | · · the key |
| 688 | DEBT SERVICE |
| 722 | · · against the take |
| 745 | · · what the world sees |
| 815 | HOME AND ABROAD |
| 854 | · · the foreign half |
| 898 | YOUR RATE, TAKEN APART |
| 958 | · · how far each measure has run |
| 1043 | THE BOOK |
| 1115 | BUY BACK |
| 1265 | BORROW |
| 1301 | · · what to sell |
| 1322 | · · for how long |
| 1346 | · · how much |
| 1398 | · · the quote |
| 1458 | · · the ladder, with this bond in it |
| 1466 | · · who is buying it |
| 1469 | · · and where the dollars go |
| 1486 | · · the button |
| 1676 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 74 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 76 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 77 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 91 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1000, "months", "No coupon a...` |  |
| 479 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 70 | `String financeArea` | Which subject is open. |
| 72 | `String financePage` |  |
| 120 | `String borrowType` | what the borrow page is currently asking for |
| 121 | `int borrowTerm` |  |
| 122 | `double borrowAsk` |  |
| 123 | `boolean borrowHold` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 1681 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money. |
| 38 | 1 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 40-124)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, double rounding, String unit, S...` | One kind of paper the city can sell. |
| 106 | 4 | `static Instrument instrument(String key)` |  |
| 111 | 7 | `static String instrumentColour(String type)` |  |

### THE LANDING (lines 125-309)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 77 | `void showFinanceMenu()` |  |
| 214 | 42 | `HBox financeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the landing page. |
| 258 | 44 | `HBox financeVitals()` | The four figures that are true of the whole tab. |
| 304 | 5 | `double couponNow(DebtManager owed)` | What the city is charged in coupon each month, across every instrument. |

### ONE SUBJECT, ITS OWN STRIP (lines 310-365)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 51 | `void drawFinanceScreen()` |  |

### THE POSITION (lines 366-461)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 81 | `void positionPage(VBox column)` |  |
| 452 | 9 | `int ratioRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double annual, String tone)` |  |

### THE LADDER (lines 462-687)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 58 | `void ladderPage(VBox column)` |  |
| 546 | 23 | `double[][] ladderYears(java.util.List<Debt> paper, double[] extra)` | What falls due in each of the next LADDER_YEARS years, split by instrument. |
| 576 | 111 | `VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel)` | The ladder itself: a bar per year, stacked by instrument. |

### DEBT SERVICE (lines 688-814)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 64 | `void debtServicePage(VBox column)` |  |
| 769 | 45 | `VBox serviceBands(double share)` | The three bands a debt-service ratio falls in, with the city's own mark. |

### HOME AND ABROAD (lines 815-897)

| line | len | member | says |
|---:|---:|---|---|
| 819 | 78 | `void homeAndAbroadPage(VBox column)` |  |

### YOUR RATE, TAKEN APART (lines 898-1042)

| line | len | member | says |
|---:|---:|---|---|
| 902 | 86 | `void yourRatePage(VBox column)` |  |
| 989 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1001 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1043-1114)

| line | len | member | says |
|---:|---:|---|---|
| 1047 | 67 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1115-1264)

| line | len | member | says |
|---:|---:|---|---|
| 1138 | 41 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1181 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### BORROW (lines 1265-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1282 | 229 | `void borrowPage(VBox column, boolean foreign)` |  |
| 1519 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 1573 | 37 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 1612 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 1631 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### THE DEBT RESULT (lines 1676-1713)

| line | len | member | says |
|---:|---:|---|---|
| 1685 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 1695 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


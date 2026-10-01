# FinancesScreen.java - 2,765 lines · 41 methods · 11 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The Finances tab: the position, the ladder of what the city owes, debt
> service, home and abroad, your rate taken apart, the book, buying back,
> and borrowing - at home or in somebody else's money - and, since 0.7.0,
> the money itself: the central bank's books, M0 and M2; since 0.7.12
> the businesses' bond market beside the city's own; since 0.7.13, at
> the top of both borrow pages, the treasury's rollover of what falls due;
> and since 0.7.14 the city's fund, with the treasury's setting for a failed
> bank beside the rollover.
> 
> Split out of UserInterface on 2026-09-18: what are now the twelve banners
> from FINANCES to BORROW - all but THE CITY'S FUND (0.7.14) exactly as they
> were then - the shell's members reached through ui. The
> shell still reads which page and area are open (financePage, financeArea)
> for the rail and the scroll memory. THE DEBT RESULT - what a debt decision
> did, shown after it - had sat at the tail of the build cards and came here
> the same day, because it is this tab's.

**Uses:** [Palette](Palette.md) (341), [DebtManager](DebtManager.md) (16), [TreasuryFund](TreasuryFund.md) (16), [CityCalendar](CityCalendar.md) (14), [Debt](Debt.md) (12), [CorporateBond](CorporateBond.md) (11), [Rollover](Rollover.md) (9), [Equity](Equity.md) (9), [Currency](Currency.md) (6), [Bank](Bank.md) (6), [OrderBook](OrderBook.md) (6), [BondMarket](BondMarket.md) (4), [Game](Game.md) (4), [NationalAccounts](NationalAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [UserInterface](UserInterface.md) (2), [CentralBank](CentralBank.md) (2), [Sectors](Sectors.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1), [Exchange](Exchange.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [HistorySave](HistorySave.md) (1), [BankScreen](BankScreen.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 46 | FINANCES |
| 148 | THE LANDING |
| 232 | · · the standing warnings |
| 361 | ONE SUBJECT, ITS OWN STRIP |
| 427 | THE POSITION |
| 461 | · · the credit |
| 479 | · · against the economy |
| 523 | THE LADDER |
| 562 | · · and what it adds up to |
| 725 | · · the key |
| 749 | DEBT SERVICE |
| 783 | · · against the take |
| 806 | · · what the world sees |
| 876 | HOME AND ABROAD |
| 915 | · · the foreign half |
| 959 | YOUR RATE, TAKEN APART |
| 1067 | · · how far each measure has run |
| 1146 | THE BOOK |
| 1213 | · · who holds it (0.7.1) |
| 1247 | BUY BACK |
| 1401 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1538 | · WHEN THE BANK FAILS (0.7.14) |
| 1597 | THE CITY'S FUND (0.7.14) |
| 1620 | · · what it holds |
| 1678 | · · the dial |
| 1710 | · · the transfer |
| 1749 | · · how much |
| 1769 | · · pay in, draw out |
| 1788 | · · shares |
| 1822 | · · bonds |
| 1858 | · · waiting |
| 1872 | BORROW |
| 1913 | · · what to sell |
| 1934 | · · the curve (0.7.1) |
| 1964 | · · for how long |
| 1988 | · · how much |
| 2040 | · · the quote |
| 2100 | · · the ladder, with this bond in it |
| 2108 | · · who is buying it |
| 2111 | · · and where the dollars go |
| 2128 | · · the button |
| 2323 | MONEY (0.7.0) |
| 2340 | · · in two sentences |
| 2374 | · · the balance sheet |
| 2422 | · · this month |
| 2452 | · · M0 and M2 |
| 2500 | THE BOND MARKET (0.7.12) |
| 2597 | · · the month |
| 2629 | · · the order books |
| 2726 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `FinancesScreen.FINANCE_HOME` | `"Overview"` |  |
| 86 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "The ladder", "Debt service", "Home & abroad", "Your rate" }` |  |
| 88 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece", "Buy back" }` |  |
| 89 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` |  |
| 91 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 93 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 95 | `FinancesScreen.FUND_PAGES` | `{ "Holdings", "By hand" }` | The city's fund: what it holds and its rules, and the player's own orders (0.7.14). |
| 114 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupo...` |  |
| 540 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |
| 1413 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", "12-month notes" }` |  |
| 1545 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 82 | `String financeArea` | Which subject is open. |
| 84 | `String financePage` |  |
| 143 | `String borrowType` | what the borrow page is currently asking for |
| 144 | `int borrowTerm` |  |
| 145 | `double borrowAsk` |  |
| 146 | `boolean borrowHold` |  |
| 1607 | `double fundAsk` | What the player's hand is asking for on the fund's page: money to move or spend, and the company or bond picked. |
| 1608 | `int fundCompany` |  |
| 1609 | `String fundIssuer` |  |
| 2648 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 2727 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money - and, since 0.7.0, th... |
| 44 | 1 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 46-147)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 129 | 4 | `static Instrument instrument(String key)` |  |
| 134 | 7 | `static String instrumentColour(String type)` |  |

### THE LANDING (lines 148-360)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 105 | `void showFinanceMenu()` |  |
| 265 | 42 | `HBox financeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the landing page. |
| 309 | 44 | `HBox financeVitals()` | The four figures that are true of the whole tab. |
| 355 | 5 | `double couponNow(DebtManager owed)` | What the city is charged in coupon each month, across every instrument. |

### ONE SUBJECT, ITS OWN STRIP (lines 361-426)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 61 | `void drawFinanceScreen()` |  |

### THE POSITION (lines 427-522)

| line | len | member | says |
|---:|---:|---|---|
| 431 | 81 | `void positionPage(VBox column)` |  |
| 513 | 9 | `int ratioRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double annual, String tone)` |  |

### THE LADDER (lines 523-748)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 58 | `void ladderPage(VBox column)` |  |
| 607 | 23 | `double[][] ladderYears(java.util.List<Debt> paper, double[] extra)` | What falls due in each of the next LADDER_YEARS years, split by instrument. |
| 637 | 111 | `VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel)` | The ladder itself: a bar per year, stacked by instrument. |

### DEBT SERVICE (lines 749-875)

| line | len | member | says |
|---:|---:|---|---|
| 759 | 64 | `void debtServicePage(VBox column)` |  |
| 830 | 45 | `VBox serviceBands(double share)` | The three bands a debt-service ratio falls in, with the city's own mark. |

### HOME AND ABROAD (lines 876-958)

| line | len | member | says |
|---:|---:|---|---|
| 880 | 78 | `void homeAndAbroadPage(VBox column)` |  |

### YOUR RATE, TAKEN APART (lines 959-1145)

| line | len | member | says |
|---:|---:|---|---|
| 963 | 128 | `void yourRatePage(VBox column)` |  |
| 1092 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1104 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1146-1246)

| line | len | member | says |
|---:|---:|---|---|
| 1150 | 96 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1247-1400)

| line | len | member | says |
|---:|---:|---|---|
| 1270 | 45 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1317 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1401-1537)

| line | len | member | says |
|---:|---:|---|---|
| 1415 | 122 | `void rolloverBlock(VBox column)` |  |

### WHEN THE BANK FAILS (0.7.14) (lines 1538-1596)

| line | len | member | says |
|---:|---:|---|---|
| 1547 | 49 | `void rescueBlock(VBox column)` |  |

### THE CITY'S FUND (0.7.14) (lines 1597-1871)

| line | len | member | says |
|---:|---:|---|---|
| 1612 | 1 | `static String percentOf(double s)` | A share, 0-1, as "x.x%". |
| 1614 | 120 | `void fundPage(VBox column)` |  |
| 1741 | 130 | `void fundHandPage(VBox column)` | THE HAND (0.7.14): pay in and draw out, and orders on the two books. |

### BORROW (lines 1872-2322)

| line | len | member | says |
|---:|---:|---|---|
| 1889 | 264 | `void borrowPage(VBox column, boolean foreign)` |  |
| 2161 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 2216 | 41 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 2259 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 2278 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### MONEY (0.7.0) (lines 2323-2499)

| line | len | member | says |
|---:|---:|---|---|
| 2335 | 153 | `void moneyPage(VBox column)` |  |
| 2490 | 4 | `static double[] lastYear(double[] series)` | The last twelve months of a series, or all of it if the city is younger. |
| 2496 | 3 | `static List<Integer> lastYear(List<Integer> months)` | ...and the same twelve months of the history's axis, for the chart's years (0.7.23). |

### THE BOND MARKET (0.7.12) (lines 2500-2725)

| line | len | member | says |
|---:|---:|---|---|
| 2513 | 6 | `double businessDebt()` | What every business owes, bank loans and bonds together. |
| 2521 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 2525 | 121 | `void bondMarketPage(VBox column)` |  |
| 2650 | 75 | `void bondBookPage(VBox column)` |  |

### THE DEBT RESULT (lines 2726-2765)

| line | len | member | says |
|---:|---:|---|---|
| 2737 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 2747 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


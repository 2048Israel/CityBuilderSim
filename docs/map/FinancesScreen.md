# FinancesScreen.java - 2,756 lines · 40 methods · 11 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (340), [DebtManager](DebtManager.md) (16), [TreasuryFund](TreasuryFund.md) (16), [CityCalendar](CityCalendar.md) (14), [Debt](Debt.md) (12), [CorporateBond](CorporateBond.md) (11), [Equity](Equity.md) (9), [Rollover](Rollover.md) (8), [Currency](Currency.md) (6), [Bank](Bank.md) (6), [OrderBook](OrderBook.md) (6), [BondMarket](BondMarket.md) (4), [Game](Game.md) (4), [NationalAccounts](NationalAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [UserInterface](UserInterface.md) (2), [CentralBank](CentralBank.md) (2), [Sectors](Sectors.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1), [Exchange](Exchange.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [HistorySave](HistorySave.md) (1), [BankScreen](BankScreen.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 46 | FINANCES |
| 148 | THE LANDING |
| 233 | · · the standing warnings |
| 362 | ONE SUBJECT, ITS OWN STRIP |
| 430 | THE POSITION |
| 464 | · · the credit |
| 482 | · · against the economy |
| 526 | THE LADDER |
| 565 | · · and what it adds up to |
| 728 | · · the key |
| 752 | DEBT SERVICE |
| 786 | · · against the take |
| 809 | · · what the world sees |
| 879 | HOME AND ABROAD |
| 918 | · · the foreign half |
| 962 | YOUR RATE, TAKEN APART |
| 1070 | · · how far each measure has run |
| 1149 | THE BOOK |
| 1216 | · · who holds it (0.7.1) |
| 1250 | BUY BACK |
| 1404 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1537 | · WHEN THE BANK FAILS (0.7.14) |
| 1596 | THE CITY'S FUND (0.7.14) |
| 1619 | · · what it holds |
| 1677 | · · the dial |
| 1709 | · · the transfer |
| 1748 | · · how much |
| 1768 | · · pay in, draw out |
| 1787 | · · shares |
| 1821 | · · bonds |
| 1857 | · · waiting |
| 1871 | BORROW |
| 1912 | · · what to sell |
| 1933 | · · the curve (0.7.1) |
| 1963 | · · for how long |
| 1987 | · · how much |
| 2039 | · · the quote |
| 2099 | · · the ladder, with this bond in it |
| 2107 | · · who is buying it |
| 2110 | · · and where the dollars go |
| 2127 | · · the button |
| 2322 | MONEY (0.7.0) |
| 2339 | · · in two sentences |
| 2373 | · · the balance sheet |
| 2421 | · · this month |
| 2451 | · · M0 and M2 |
| 2491 | THE BOND MARKET (0.7.12) |
| 2588 | · · the month |
| 2620 | · · the order books |
| 2717 | THE DEBT RESULT |

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
| 543 | `FinancesScreen.LADDER_YEARS` | `12` | How many years out the ladder is drawn before it gives up and totals. |
| 1416 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", "12-month notes" }` |  |
| 1544 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` |  |

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
| 1606 | `double fundAsk` | What the player's hand is asking for on the fund's page: money to move or spend, and the company or bond picked. |
| 1607 | `int fundCompany` |  |
| 1608 | `String fundIssuer` |  |
| 2639 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 2718 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money - and, since 0.7.0, th... |
| 44 | 1 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 46-147)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 129 | 4 | `static Instrument instrument(String key)` |  |
| 134 | 7 | `static String instrumentColour(String type)` |  |

### THE LANDING (lines 148-361)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 106 | `void showFinanceMenu()` |  |
| 266 | 42 | `HBox financeRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the landing page. |
| 310 | 44 | `HBox financeVitals()` | The four figures that are true of the whole tab. |
| 356 | 5 | `double couponNow(DebtManager owed)` | What the city is charged in coupon each month, across every instrument. |

### ONE SUBJECT, ITS OWN STRIP (lines 362-429)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 63 | `void drawFinanceScreen()` |  |

### THE POSITION (lines 430-525)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 81 | `void positionPage(VBox column)` |  |
| 516 | 9 | `int ratioRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double annual, String tone)` |  |

### THE LADDER (lines 526-751)

| line | len | member | says |
|---:|---:|---|---|
| 545 | 58 | `void ladderPage(VBox column)` |  |
| 610 | 23 | `double[][] ladderYears(java.util.List<Debt> paper, double[] extra)` | What falls due in each of the next LADDER_YEARS years, split by instrument. |
| 640 | 111 | `VBox ladderChart(java.util.List<Debt> paper, double[] extra, String extraLabel)` | The ladder itself: a bar per year, stacked by instrument. |

### DEBT SERVICE (lines 752-878)

| line | len | member | says |
|---:|---:|---|---|
| 762 | 64 | `void debtServicePage(VBox column)` |  |
| 833 | 45 | `VBox serviceBands(double share)` | The three bands a debt-service ratio falls in, with the city's own mark. |

### HOME AND ABROAD (lines 879-961)

| line | len | member | says |
|---:|---:|---|---|
| 883 | 78 | `void homeAndAbroadPage(VBox column)` |  |

### YOUR RATE, TAKEN APART (lines 962-1148)

| line | len | member | says |
|---:|---:|---|---|
| 966 | 128 | `void yourRatePage(VBox column)` |  |
| 1095 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1107 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1149-1249)

| line | len | member | says |
|---:|---:|---|---|
| 1153 | 96 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1250-1403)

| line | len | member | says |
|---:|---:|---|---|
| 1273 | 45 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1320 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1404-1536)

| line | len | member | says |
|---:|---:|---|---|
| 1418 | 118 | `void rolloverBlock(VBox column)` |  |

### WHEN THE BANK FAILS (0.7.14) (lines 1537-1595)

| line | len | member | says |
|---:|---:|---|---|
| 1546 | 49 | `void rescueBlock(VBox column)` |  |

### THE CITY'S FUND (0.7.14) (lines 1596-1870)

| line | len | member | says |
|---:|---:|---|---|
| 1611 | 1 | `static String percentOf(double s)` | A share, 0-1, as "x.x%". |
| 1613 | 120 | `void fundPage(VBox column)` |  |
| 1740 | 130 | `void fundHandPage(VBox column)` | THE HAND (0.7.14): pay in and draw out, and orders on the two books. |

### BORROW (lines 1871-2321)

| line | len | member | says |
|---:|---:|---|---|
| 1888 | 264 | `void borrowPage(VBox column, boolean foreign)` |  |
| 2160 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 2215 | 41 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 2258 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 2277 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### MONEY (0.7.0) (lines 2322-2490)

| line | len | member | says |
|---:|---:|---|---|
| 2334 | 150 | `void moneyPage(VBox column)` |  |
| 2486 | 4 | `static double[] lastYear(double[] series)` | The last twelve months of a series, or all of it if the city is younger. |

### THE BOND MARKET (0.7.12) (lines 2491-2716)

| line | len | member | says |
|---:|---:|---|---|
| 2504 | 6 | `double businessDebt()` | What every business owes, bank loans and bonds together. |
| 2512 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 2516 | 121 | `void bondMarketPage(VBox column)` |  |
| 2641 | 75 | `void bondBookPage(VBox column)` |  |

### THE DEBT RESULT (lines 2717-2756)

| line | len | member | says |
|---:|---:|---|---|
| 2728 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 2738 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


# FinancesScreen.java - 2,695 lines · 40 methods · 11 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-09-27 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (336), [DebtManager](DebtManager.md) (16), [TreasuryFund](TreasuryFund.md) (16), [CityCalendar](CityCalendar.md) (14), [Debt](Debt.md) (12), [CorporateBond](CorporateBond.md) (11), [Equity](Equity.md) (9), [Rollover](Rollover.md) (7), [Currency](Currency.md) (6), [Bank](Bank.md) (6), [OrderBook](OrderBook.md) (6), [BondMarket](BondMarket.md) (4), [Game](Game.md) (4), [NationalAccounts](NationalAccounts.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [UserInterface](UserInterface.md) (2), [CentralBank](CentralBank.md) (2), [Sectors](Sectors.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignAccounts](ForeignAccounts.md) (1), [Exchange](Exchange.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [HistorySave](HistorySave.md) (1), [BankScreen](BankScreen.md) (1)

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
| 1048 | · · how far each measure has run |
| 1127 | THE BOOK |
| 1194 | · · who holds it (0.7.1) |
| 1225 | BUY BACK |
| 1379 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1476 | · WHEN THE BANK FAILS (0.7.14) |
| 1535 | THE CITY'S FUND (0.7.14) |
| 1558 | · · what it holds |
| 1616 | · · the dial |
| 1648 | · · the transfer |
| 1687 | · · how much |
| 1707 | · · pay in, draw out |
| 1726 | · · shares |
| 1760 | · · bonds |
| 1796 | · · waiting |
| 1810 | BORROW |
| 1851 | · · what to sell |
| 1872 | · · the curve (0.7.1) |
| 1902 | · · for how long |
| 1926 | · · how much |
| 1978 | · · the quote |
| 2038 | · · the ladder, with this bond in it |
| 2046 | · · who is buying it |
| 2049 | · · and where the dollars go |
| 2066 | · · the button |
| 2261 | MONEY (0.7.0) |
| 2278 | · · in two sentences |
| 2312 | · · the balance sheet |
| 2360 | · · this month |
| 2390 | · · M0 and M2 |
| 2430 | THE BOND MARKET (0.7.12) |
| 2527 | · · the month |
| 2559 | · · the order books |
| 2656 | THE DEBT RESULT |

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
| 1391 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", "12-month notes" }` |  |
| 1483 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` |  |

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
| 1545 | `double fundAsk` | What the player's hand is asking for on the fund's page: money to move or spend, and the company or bond picked. |
| 1546 | `int fundCompany` |  |
| 1547 | `String fundIssuer` |  |
| 2578 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 2657 | **type** `final class FinancesScreen` | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and borrowing - at home or in somebody else's money - and, since 0.7.0, th... |
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

### YOUR RATE, TAKEN APART (lines 962-1126)

| line | len | member | says |
|---:|---:|---|---|
| 966 | 106 | `void yourRatePage(VBox column)` |  |
| 1073 | 10 | `int rateRow(javafx.scene.layout.GridPane table, int line, String label, double rate, String lever)` |  |
| 1085 | 41 | `VBox stressRow(String label, double stress)` | How much of one measure's worst case the city has used up. |

### THE BOOK (lines 1127-1224)

| line | len | member | says |
|---:|---:|---|---|
| 1131 | 93 | `void theBookPage(VBox column)` |  |

### BUY BACK (lines 1225-1378)

| line | len | member | says |
|---:|---:|---|---|
| 1248 | 45 | `void buyBackPage(VBox column)` | Buying the city's own debt back, one bond at a time. |
| 1295 | 83 | `VBox buyBackRow(Debt debt, double rate, int month)` | One bond, with what it would cost to clear and what that saves. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1379-1475)

| line | len | member | says |
|---:|---:|---|---|
| 1393 | 82 | `void rolloverBlock(VBox column)` |  |

### WHEN THE BANK FAILS (0.7.14) (lines 1476-1534)

| line | len | member | says |
|---:|---:|---|---|
| 1485 | 49 | `void rescueBlock(VBox column)` |  |

### THE CITY'S FUND (0.7.14) (lines 1535-1809)

| line | len | member | says |
|---:|---:|---|---|
| 1550 | 1 | `static String percentOf(double s)` | A share, 0-1, as "x.x%". |
| 1552 | 120 | `void fundPage(VBox column)` |  |
| 1679 | 130 | `void fundHandPage(VBox column)` | THE HAND (0.7.14): pay in and draw out, and orders on the two books. |

### BORROW (lines 1810-2260)

| line | len | member | says |
|---:|---:|---|---|
| 1827 | 264 | `void borrowPage(VBox column, boolean foreign)` |  |
| 2099 | 43 | `double[] proposedSchedule(String type, DebtQuote quote, int term, boolean foreign)` | The payment schedule the proposed bond would add, month by month. |
| 2154 | 41 | `VBox bankAppetite()` | WHO IS BUYING THIS, AND WHAT IT DOES TO EVERYONE ELSE. |
| 2197 | 11 | `VBox foreignDoor()` | The door marked do not open. |
| 2216 | 44 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out. |

### MONEY (0.7.0) (lines 2261-2429)

| line | len | member | says |
|---:|---:|---|---|
| 2273 | 150 | `void moneyPage(VBox column)` |  |
| 2425 | 4 | `static double[] lastYear(double[] series)` | The last twelve months of a series, or all of it if the city is younger. |

### THE BOND MARKET (0.7.12) (lines 2430-2655)

| line | len | member | says |
|---:|---:|---|---|
| 2443 | 6 | `double businessDebt()` | What every business owes, bank loans and bonds together. |
| 2451 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 2455 | 121 | `void bondMarketPage(VBox column)` |  |
| 2580 | 75 | `void bondBookPage(VBox column)` |  |

### THE DEBT RESULT (lines 2656-2695)

| line | len | member | says |
|---:|---:|---|---|
| 2667 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 2677 | 18 | `void showDebtResultMenu(String summary)` | Shows the terms the player just agreed to. |


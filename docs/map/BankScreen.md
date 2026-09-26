# BankScreen.java - 2,189 lines · 37 methods · 4 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The bank tab: whether the city's bank is healthy and why, on one landing -
> a sentence, a scorecard and the ladder of its rates - with its profit, its
> balance sheet (0.7.13), its lending, its funding, its capital and owners,
> and its history behind it.
> 
> WHY THIS SHAPE (0.7.9). Jerus: "a redesign of the bank UI info, cause when
> you click on bank you dont even see all the relevant stuff, lets make
> banks realistic." The tab it replaced was split out of UserInterface on
> 2026-09-18 and still opened on the strain premium's questions - a gauge,
> the two limits, another branch - after 0.7.7 took the premium away. A
> player could not find the bank's rates side by side, its return on its
> capital, its capital against a target, its losses as a rate, what it did
> with its profit, its account at the central bank, or its owners in one
> place; and a dozen of the figures it did print were worked out on the
> screen, several of them wrong (the project's the-bank-tab.md has the
> list). Every figure is a model getter now - Bank's WHAT THE BANK TAB
> READS has the ones this tab asked for.
> 
> ONE WAY TO WRITE EACH KIND OF NUMBER: a rate as "x.xx% a year" (rate()),
> a spread between two rates in points to two decimals, as the rates are
> (points()), a share or a ratio as "x.x%" (share()), and money through
> Money - a flow says "this month", a stock does not.
> 
> The shell reads which page is open (bankArea, bankPage) for the rail and
> the scroll memory; the panel is rebuilt on the clock, so the page, the
> scroll position (UserInterface.scrolled()) and the lines the player has
> opened (openLines) all survive a redraw.

**Uses:** [Palette](Palette.md) (354), [Bank](Bank.md) (169), [BusinessDebtManager](BusinessDebtManager.md) (19), [Equity](Equity.md) (13), [Sectors](Sectors.md) (9), [Mortgage](Mortgage.md) (9), [Exchange](Exchange.md) (7), [Ladder](Ladder.md) (4), [CorporateBond](CorporateBond.md) (4), [HistorySave](HistorySave.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [CentralBank](CentralBank.md) (3), [UserInterface](UserInterface.md) (2), [BondMarket](BondMarket.md) (2), [CityCalendar](CityCalendar.md) (2), [OrderBook](OrderBook.md) (2), [DebtManager](DebtManager.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (3):** [FinancesScreen](FinancesScreen.md), [SectorScreen](SectorScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 61 | THE BANK AT A GLANCE |
| 89 | · one way to write each number |
| 144 | · · there is no bank |
| 163 | · · behind it |
| 227 | · the scorecard |
| 376 | · the rate ladder |
| 452 | · · the borrowers |
| 666 | THE PAGES BEHIND IT |
| 728 | PROFIT |
| 824 | · · and what it did with it |
| 833 | · · the last twelve months |
| 854 | · · as ratios |
| 1000 | BALANCE SHEET (0.7.13) |
| 1024 | · · what it owns |
| 1072 | · · what it owes |
| 1093 | · · what is left |
| 1142 | · · beside the sheet |
| 1173 | LENDING |
| 1189 | · · who owes it |
| 1216 | · · the businesses |
| 1284 | · · the landlords' mortgages |
| 1330 | · · the families |
| 1355 | · · what it set aside |
| 1374 | · · who has stopped paying |
| 1419 | · · what it lent this month |
| 1441 | · · how the next loan is priced |
| 1513 | · · what the book weighs |
| 1546 | · · the businesses' bonds it holds (0.7.12) |
| 1575 | · · what concentration costs (0.7.12) |
| 1623 | FUNDING |
| 1641 | · · what is banked |
| 1658 | · · what it can reach |
| 1687 | · · what it pays savers |
| 1710 | · · its account at the central bank |
| 1727 | · · how it is funded |
| 1742 | · · what it can carry |
| 1840 | · · ...and whether one should close (0.7.11, round 2) |
| 1865 | CAPITAL & OWNERS |
| 1882 | · · its capital |
| 1900 | · · ...and against everything it has lent (0.7.11, round 2) |
| 1927 | · · what it does with its profit |
| 1955 | · · how its equity moved |
| 1992 | · · its owners |
| 1999 | · · its rescues |
| 2028 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 2092 | HISTORY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the landing; BANK_PAGES, the pages behind it |
| 79 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the landing that is lit until the player picks another; the rail's bank icon resets to it. |
| 83 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "Histo...` | The six pages behind the landing, in the chip strip's order: the balance sheet beside the income statement since 0.7.13. |
| 379 | `BankScreen.LADDER_BAR` | `190` | How wide the ladder's bars run at the highest rate on it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 76 | `String bankArea` | null is the landing; BANK_PAGES, the pages behind it |
| 80 | `String bankPage` |  |
| 87 | `final Set<String> openLines` | The lines the player has opened, by label, so a redraw on the clock leaves them open (Statement.opens()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 2136 | **type** `final class BankScreen` | The bank tab: whether the city's bank is healthy and why, on one landing - a sentence, a scorecard and the ladder of its rates - with its profit, its balance sheet (0.7.13), its lending, its funding, its capital and o... |
| 59 | 1 | `BankScreen(UserInterface ui)` |  |

### THE BANK AT A GLANCE (lines 61-88)

### one way to write each number (lines 89-226)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1 | `static String rate(double r)` | A rate: "x.xx% a year". |
| 95 | 3 | `static String points(double p)` | A spread between two rates, signed, to the rates' own two decimals - a quarter point must not print as 0.3. |
| 100 | 1 | `static String share(double s)` | A share or a ratio that is not a yearly rate: "x.x%". |
| 103 | 6 | `static String defaultShare(double pd)` | A default rate, which runs from the curve's far tail to all of it: "under 0.1%" rather than a "0.0%" that reads as none, "x.x%" to 99.9%, then "all". |
| 111 | 4 | `static String yearWords(Bank bank)` | "the last 12 months", or as many as the bank has lived. |
| 121 | 85 | `void showBankMenu()` | The tab's entry point: the landing, or the page behind it the player was on. |
| 208 | 8 | `Label statusLine(Bank bank)` | The bank's state in one sentence, in the colour of the news. |
| 218 | 8 | `static String stanceTone(Bank bank)` | Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. |

### the scorecard (lines 227-375)

| line | len | member | says |
|---:|---:|---|---|
| 230 | 16 | `HBox scorecardTop(Bank bank)` | What it earned, what that returns its owners, its capital, and its losses. |
| 248 | 14 | `HBox scorecardBottom(Bank bank)` | Its margin, its costs, and the two sides of its balance sheet a player knows by name. |
| 268 | 23 | `VBox capitalCell(Bank bank)` | The capital ratio with its band drawn under it: the minimum, the bank's own target and the top of its band, and where it stands against them. |
| 303 | 72 | `Pane capitalBand(Bank bank, double width, boolean labelled)` | The capital ratio on a bar, with the minimum, the target and the top of the band marked on it. |

### the rate ladder (lines 376-665)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 155 | `void ladder(VBox column, Bank bank)` | THE LADDER OF ITS RATES, the landing's centrepiece: the policy rate; what savers get, a share of it; what a prime loan's money costs the bank; prime, with its four parts; and what each borrower pays - every rung with ... |
| 545 | 12 | `static String saversWhy(Bank.Ladder l)` | Why savers get what they get: the share its funding asks for, and whether its margin held them under it. |
| 563 | 56 | `VBox rung(String name, double value, double top, String colour, String step, String explain, Runnable go)` | One rung: its name (a link where the rate is set somewhere else), a bar on the ladder's one scale, the rate, and under them its step from the rung it is built on and what the step is for. |
| 621 | 6 | `void openPage(String page)` | Opens one page behind the landing, at its top - the landing's rows, and the inbox's "defaults" notice (0.7.8). |
| 629 | 36 | `HBox bankRow(String name, String blurb, String figure, String sub, String tone, String page)` | One page on the landing: what it holds, and its headline. |

### THE PAGES BEHIND IT (lines 666-727)

| line | len | member | says |
|---:|---:|---|---|
| 675 | 37 | `void drawBankScreen()` |  |
| 714 | 13 | `HBox pageVitals()` | The four figures across the top of every page. |

### PROFIT (lines 728-999)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 139 | `void profitPage(VBox column)` |  |
| 879 | 4 | `HBox line(Bank bank, String label, Bank.Line which, boolean known, boolean out)` | One statement line, this month and last - negated for money going out. |
| 885 | 4 | `VBox total(Bank bank, String label, Bank.Line which, boolean known)` | One total, this month and last. |
| 891 | 4 | `void year(VBox column, Bank bank, String label, Bank.Line which, boolean out)` | One line of the year, negated for money going out. |
| 904 | 95 | `VBox deskDetail(Bank bank)` | THE TRADING DESK, opened (2026-09-18, and so it foots). |

### BALANCE SHEET (0.7.13) (lines 1000-1172)

| line | len | member | says |
|---:|---:|---|---|
| 1016 | 142 | `void sheetPage(VBox column)` |  |
| 1160 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail)` | One line of the balance sheet, this month and a year ago, opening into its detail. |
| 1164 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail, String word)` |  |
| 1169 | 3 | `static VBox said(String text)` | A line's detail that is a sentence. |

### LENDING (lines 1173-1622)

| line | len | member | says |
|---:|---:|---|---|
| 1183 | 425 | `void lendingPage(VBox column)` |  |
| 1610 | 12 | `static String bookName(Bank.Book book)` | What the weight table calls each book. |

### FUNDING (lines 1623-1864)

| line | len | member | says |
|---:|---:|---|---|
| 1635 | 131 | `void fundingPage(VBox column)` |  |
| 1775 | 89 | `void branches(VBox column, Bank bank)` | ITS BRANCHES, and whether another would pay - the model's own verdict, both halves of Bank.wantsBranch(): does it relieve anything (the book spilling past what the bank comfortably carries), and would it earn its keep... |

### CAPITAL & OWNERS (lines 1865-2027)

| line | len | member | says |
|---:|---:|---|---|
| 1877 | 144 | `void capitalPage(VBox column)` |  |
| 2023 | 4 | `void moved(VBox column, String label, double amount, String tone)` | One cause of the equity's movement, printed only when it moved it. |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 2028-2091)

| line | len | member | says |
|---:|---:|---|---|
| 2042 | 49 | `VBox bankRescue()` |  |

### HISTORY (lines 2092-2189)

| line | len | member | says |
|---:|---:|---|---|
| 2108 | 81 | `void historyPage(VBox column)` |  |


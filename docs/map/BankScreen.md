# BankScreen.java - 2,333 lines · 38 methods · 4 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (348), [Bank](Bank.md) (172), [BusinessDebtManager](BusinessDebtManager.md) (19), [Equity](Equity.md) (13), [Game](Game.md) (13), [Sectors](Sectors.md) (9), [Mortgage](Mortgage.md) (9), [Exchange](Exchange.md) (7), [Ladder](Ladder.md) (4), [CorporateBond](CorporateBond.md) (4), [HistorySave](HistorySave.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [CentralBank](CentralBank.md) (3), [CityCalendar](CityCalendar.md) (3), [TreasuryFund](TreasuryFund.md) (3), [UserInterface](UserInterface.md) (2), [BondMarket](BondMarket.md) (2), [OrderBook](OrderBook.md) (2), [DebtQuote](DebtQuote.md) (2), [DebtManager](DebtManager.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [BuildingType](BuildingType.md) (1), [Rollover](Rollover.md) (1)

**Used by (3):** [FinancesScreen](FinancesScreen.md), [SectorScreen](SectorScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 61 | THE BANK AT A GLANCE |
| 92 | · one way to write each number |
| 145 | · · there is no bank |
| 168 | · · behind it |
| 232 | · the scorecard |
| 380 | · the rate ladder |
| 456 | · · the borrowers |
| 670 | THE PAGES BEHIND IT |
| 730 | PROFIT |
| 826 | · · and what it did with it |
| 835 | · · the last twelve months |
| 856 | · · as ratios |
| 1002 | BALANCE SHEET (0.7.13) |
| 1026 | · · what it owns |
| 1074 | · · what it owes |
| 1095 | · · what is left |
| 1155 | · · beside the sheet |
| 1186 | LENDING |
| 1202 | · · who owes it |
| 1229 | · · the businesses |
| 1300 | · · the landlords' mortgages |
| 1346 | · · the families |
| 1371 | · · what it set aside |
| 1390 | · · who has stopped paying |
| 1435 | · · what it lent this month |
| 1457 | · · how the next loan is priced |
| 1529 | · · what the book weighs |
| 1562 | · · the businesses' bonds it holds (0.7.12) |
| 1591 | · · what concentration costs (0.7.12) |
| 1639 | FUNDING |
| 1658 | · · what is banked |
| 1675 | · · what it can lend against (0.7.19) |
| 1696 | · · what it pays savers |
| 1719 | · · its account at the central bank |
| 1736 | · · how it is funded |
| 1751 | · · what it can carry |
| 1841 | · · ...and whether one should close (0.7.19) |
| 1867 | CAPITAL & OWNERS |
| 1884 | · · its capital |
| 1902 | · · ...and against everything it has lent (0.7.11, round 2) |
| 1929 | · · what it does with its profit |
| 1957 | · · how its equity moved |
| 1998 | · · its owners |
| 2009 | · · its rescues |
| 2034 | · · the city's preferred |
| 2069 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 2130 | THE BANK ASKS FOR PREFERRED (0.7.14) |
| 2230 | HISTORY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 80 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the landing; BANK_PAGES, the pages behind it |
| 82 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the landing that is lit until the player picks another; the rail's bank icon resets to it. |
| 86 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "Histo...` | The six pages behind the landing, in the chip strip's order: the balance sheet beside the income statement since 0.7.13. |
| 383 | `BankScreen.LADDER_BAR` | `190` | How wide the ladder's bars run at the highest rate on it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 79 | `String bankArea` | null is the landing; BANK_PAGES, the pages behind it |
| 83 | `String bankPage` |  |
| 90 | `final Set<String> openLines` | The lines the player has opened, by label, so a redraw on the clock leaves them open (Statement.opens()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 2280 | **type** `final class BankScreen` | The bank tab: whether the city's bank is healthy and why, on one landing - a sentence, a scorecard and the ladder of its rates - with its profit, its balance sheet (0.7.13), its lending, its funding, its capital and o... |
| 59 | 1 | `BankScreen(UserInterface ui)` |  |

### THE BANK AT A GLANCE (lines 61-91)

### one way to write each number (lines 92-231)

| line | len | member | says |
|---:|---:|---|---|
| 95 | 1 | `static String rate(double r)` | A rate: "x.xx% a year". |
| 98 | 3 | `static String points(double p)` | A spread between two rates, signed, to the rates' own two decimals - a quarter point must not print as 0.3. |
| 103 | 1 | `static String share(double s)` | A share or a ratio that is not a yearly rate: "x.x%". |
| 106 | 6 | `static String defaultShare(double pd)` | A default rate, which runs from the curve's far tail to all of it: "under 0.1%" rather than a "0.0%" that reads as none, "x.x%" to 99.9%, then "all". |
| 114 | 4 | `static String yearWords(Bank bank)` | "the last 12 months", or as many as the bank has lived. |
| 124 | 87 | `void showBankMenu()` | The tab's entry point: the landing, or the page behind it the player was on. |
| 213 | 8 | `Label statusLine(Bank bank)` | The bank's state in one sentence, in the colour of the news. |
| 223 | 8 | `static String stanceTone(Bank bank)` | Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. |

### the scorecard (lines 232-379)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 16 | `HBox scorecardTop(Bank bank)` | What it earned, what that returns its owners, its capital, and its losses. |
| 253 | 14 | `HBox scorecardBottom(Bank bank)` | Its margin, its costs, and the two sides of its balance sheet a player knows by name. |
| 273 | 23 | `VBox capitalCell(Bank bank)` | The capital ratio with its band drawn under it: the minimum, the bank's own target and the top of its band, and where it stands against them. |
| 308 | 71 | `Pane capitalBand(Bank bank, double width, boolean labelled)` | The capital ratio on a bar, with the minimum, the target and the top of the band marked on it. |

### the rate ladder (lines 380-669)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 155 | `void ladder(VBox column, Bank bank)` | THE LADDER OF ITS RATES, the landing's centrepiece: the policy rate; what savers get, a share of it; what a prime loan's money costs the bank; prime, with its four parts; and what each borrower pays - every rung with ... |
| 549 | 12 | `static String saversWhy(Bank.Ladder l)` | Why savers get what they get: the share its funding asks for, and whether its margin held them under it. |
| 567 | 56 | `VBox rung(String name, double value, double top, String colour, String step, String explain, Runnable go)` | One rung: its name (a link where the rate is set somewhere else), a bar on the ladder's one scale, the rate, and under them its step from the rung it is built on and what the step is for. |
| 625 | 6 | `void openPage(String page)` | Opens one page behind the landing, at its top - the landing's rows, and the inbox's "defaults" notice (0.7.8). |
| 633 | 36 | `HBox bankRow(String name, String blurb, String figure, String sub, String tone, String page)` | One page on the landing: what it holds, and its headline. |

### THE PAGES BEHIND IT (lines 670-729)

| line | len | member | says |
|---:|---:|---|---|
| 679 | 35 | `void drawBankScreen()` |  |
| 716 | 13 | `HBox pageVitals()` | The four figures across the top of every page. |

### PROFIT (lines 730-1001)

| line | len | member | says |
|---:|---:|---|---|
| 740 | 139 | `void profitPage(VBox column)` |  |
| 881 | 4 | `HBox line(Bank bank, String label, Bank.Line which, boolean known, boolean out)` | One statement line, this month and last - negated for money going out. |
| 887 | 4 | `VBox total(Bank bank, String label, Bank.Line which, boolean known)` | One total, this month and last. |
| 893 | 4 | `void year(VBox column, Bank bank, String label, Bank.Line which, boolean out)` | One line of the year, negated for money going out. |
| 906 | 95 | `VBox deskDetail(Bank bank)` | THE TRADING DESK, opened (2026-09-18, and so it foots). |

### BALANCE SHEET (0.7.13) (lines 1002-1185)

| line | len | member | says |
|---:|---:|---|---|
| 1018 | 153 | `void sheetPage(VBox column)` |  |
| 1173 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail)` | One line of the balance sheet, this month and a year ago, opening into its detail. |
| 1177 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail, String word)` |  |
| 1182 | 3 | `static VBox said(String text)` | A line's detail that is a sentence. |

### LENDING (lines 1186-1638)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 428 | `void lendingPage(VBox column)` |  |
| 1626 | 12 | `static String bookName(Bank.Book book)` | What the weight table calls each book. |

### FUNDING (lines 1639-1866)

| line | len | member | says |
|---:|---:|---|---|
| 1652 | 123 | `void fundingPage(VBox column)` |  |
| 1786 | 80 | `void branches(VBox column, Bank bank)` | ITS BRANCHES, BY THEIR CUSTOMERS (0.7.19) - the model's own verdict: Bank.wantsBranch() and Bank.branchesToClose(), on the month's fees against what a branch cost last month. |

### CAPITAL & OWNERS (lines 1867-2068)

| line | len | member | says |
|---:|---:|---|---|
| 1879 | 183 | `void capitalPage(VBox column)` |  |
| 2064 | 4 | `void moved(VBox column, String label, double amount, String tone)` | One cause of the equity's movement, printed only when it moved it. |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 2069-2129)

| line | len | member | says |
|---:|---:|---|---|
| 2086 | 43 | `VBox bankRescue()` |  |

### THE BANK ASKS FOR PREFERRED (0.7.14) (lines 2130-2229)

| line | len | member | says |
|---:|---:|---|---|
| 2142 | 87 | `VBox preferredOffer()` |  |

### HISTORY (lines 2230-2333)

| line | len | member | says |
|---:|---:|---|---|
| 2246 | 87 | `void historyPage(VBox column)` |  |


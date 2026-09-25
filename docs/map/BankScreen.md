# BankScreen.java - 1,841 lines · 33 methods · 4 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The bank tab: whether the city's bank is healthy and why, on one landing -
> a sentence, a scorecard and the ladder of its rates - with its profit, its
> lending, its funding, its capital and owners, and its history behind it.
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

**Uses:** [Palette](Palette.md) (332), [Bank](Bank.md) (125), [BusinessDebtManager](BusinessDebtManager.md) (15), [Mortgage](Mortgage.md) (9), [Ladder](Ladder.md) (4), [Sectors](Sectors.md) (4), [Equity](Equity.md) (4), [HistorySave](HistorySave.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [CentralBank](CentralBank.md) (3), [UserInterface](UserInterface.md) (2), [DebtManager](DebtManager.md) (1), [CityCalendar](CityCalendar.md) (1), [Exchange](Exchange.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (2):** [SectorScreen](SectorScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 60 | THE BANK AT A GLANCE |
| 88 | · one way to write each number |
| 143 | · · there is no bank |
| 162 | · · behind it |
| 220 | · the scorecard |
| 369 | · the rate ladder |
| 438 | · · the borrowers |
| 627 | THE PAGES BEHIND IT |
| 687 | PROFIT |
| 776 | · · and what it did with it |
| 785 | · · the last twelve months |
| 805 | · · as ratios |
| 903 | LENDING |
| 919 | · · who owes it |
| 940 | · · the businesses |
| 1006 | · · the landlords' mortgages |
| 1052 | · · the families |
| 1077 | · · what it set aside |
| 1096 | · · who has stopped paying |
| 1141 | · · what it lent this month |
| 1161 | · · how the next loan is priced |
| 1233 | · · what the book weighs |
| 1275 | FUNDING |
| 1293 | · · what is banked |
| 1310 | · · what it can reach |
| 1339 | · · what it pays savers |
| 1362 | · · its account at the central bank |
| 1379 | · · how it is funded |
| 1394 | · · what it can carry |
| 1492 | · · ...and whether one should close (0.7.11, round 2) |
| 1517 | CAPITAL & OWNERS |
| 1534 | · · its capital |
| 1552 | · · ...and against everything it has lent (0.7.11, round 2) |
| 1579 | · · what it does with its profit |
| 1607 | · · how its equity moved |
| 1644 | · · its owners |
| 1651 | · · its rescues |
| 1680 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 1744 | HISTORY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the landing; BANK_PAGES, the pages behind it |
| 78 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the landing that is lit until the player picks another; the rail's bank icon resets to it. |
| 82 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Lending", "Funding", "Capital & owners", "History" }` | The five pages behind the landing, in the chip strip's order. |
| 372 | `BankScreen.LADDER_BAR` | `190` | How wide the ladder's bars run at the highest rate on it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 75 | `String bankArea` | null is the landing; BANK_PAGES, the pages behind it |
| 79 | `String bankPage` |  |
| 86 | `final Set<String> openLines` | The lines the player has opened, by label, so a redraw on the clock leaves them open (Statement.opens()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 1789 | **type** `final class BankScreen` | The bank tab: whether the city's bank is healthy and why, on one landing - a sentence, a scorecard and the ladder of its rates - with its profit, its lending, its funding, its capital and owners, and its history behin... |
| 58 | 1 | `BankScreen(UserInterface ui)` |  |

### THE BANK AT A GLANCE (lines 60-87)

### one way to write each number (lines 88-219)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 1 | `static String rate(double r)` | A rate: "x.xx% a year". |
| 94 | 3 | `static String points(double p)` | A spread between two rates, signed, to the rates' own two decimals - a quarter point must not print as 0.3. |
| 99 | 1 | `static String share(double s)` | A share or a ratio that is not a yearly rate: "x.x%". |
| 102 | 6 | `static String defaultShare(double pd)` | A default rate, which runs from the curve's far tail to all of it: "under 0.1%" rather than a "0.0%" that reads as none, "x.x%" to 99.9%, then "all". |
| 110 | 4 | `static String yearWords(Bank bank)` | "the last 12 months", or as many as the bank has lived. |
| 120 | 79 | `void showBankMenu()` | The tab's entry point: the landing, or the page behind it the player was on. |
| 201 | 8 | `Label statusLine(Bank bank)` | The bank's state in one sentence, in the colour of the news. |
| 211 | 8 | `static String stanceTone(Bank bank)` | Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. |

### the scorecard (lines 220-368)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 16 | `HBox scorecardTop(Bank bank)` | What it earned, what that returns its owners, its capital, and its losses. |
| 241 | 14 | `HBox scorecardBottom(Bank bank)` | Its margin, its costs, and the two sides of its balance sheet a player knows by name. |
| 261 | 23 | `VBox capitalCell(Bank bank)` | The capital ratio with its band drawn under it: the minimum, the bank's own target and the top of its band, and where it stands against them. |
| 296 | 72 | `Pane capitalBand(Bank bank, double width, boolean labelled)` | The capital ratio on a bar, with the minimum, the target and the top of the band marked on it. |

### the rate ladder (lines 369-626)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 123 | `void ladder(VBox column, Bank bank)` | THE LADDER OF ITS RATES, the landing's centrepiece: the policy rate; what savers get, a share of it; what a prime loan's money costs the bank; prime, with its four parts; and what each borrower pays - every rung with ... |
| 506 | 12 | `static String saversWhy(Bank.Ladder l)` | Why savers get what they get: the share its funding asks for, and whether its margin held them under it. |
| 524 | 56 | `VBox rung(String name, double value, double top, String colour, String step, String explain, Runnable go)` | One rung: its name (a link where the rate is set somewhere else), a bar on the ladder's one scale, the rate, and under them its step from the rung it is built on and what the step is for. |
| 582 | 6 | `void openPage(String page)` | Opens one page behind the landing, at its top - the landing's rows, and the inbox's "defaults" notice (0.7.8). |
| 590 | 36 | `HBox bankRow(String name, String blurb, String figure, String sub, String tone, String page)` | One page on the landing: what it holds, and its headline. |

### THE PAGES BEHIND IT (lines 627-686)

| line | len | member | says |
|---:|---:|---|---|
| 635 | 36 | `void drawBankScreen()` |  |
| 673 | 13 | `HBox pageVitals()` | The four figures across the top of every page. |

### PROFIT (lines 687-902)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 131 | `void profitPage(VBox column)` |  |
| 830 | 4 | `HBox line(Bank bank, String label, Bank.Line which, boolean known, boolean out)` | One statement line, this month and last - negated for money going out. |
| 836 | 4 | `VBox total(Bank bank, String label, Bank.Line which, boolean known)` | One total, this month and last. |
| 842 | 4 | `void year(VBox column, Bank bank, String label, Bank.Line which, boolean out)` | One line of the year, negated for money going out. |
| 855 | 47 | `VBox deskDetail(Bank bank)` | THE TRADING DESK, opened (2026-09-18, and so it foots). |

### LENDING (lines 903-1274)

| line | len | member | says |
|---:|---:|---|---|
| 913 | 349 | `void lendingPage(VBox column)` |  |
| 1264 | 10 | `static String bookName(Bank.Book book)` | What the weight table calls each book. |

### FUNDING (lines 1275-1516)

| line | len | member | says |
|---:|---:|---|---|
| 1287 | 131 | `void fundingPage(VBox column)` |  |
| 1427 | 89 | `void branches(VBox column, Bank bank)` | ITS BRANCHES, and whether another would pay - the model's own verdict, both halves of Bank.wantsBranch(): does it relieve anything (the book spilling past what the bank comfortably carries), and would it earn its keep... |

### CAPITAL & OWNERS (lines 1517-1679)

| line | len | member | says |
|---:|---:|---|---|
| 1529 | 144 | `void capitalPage(VBox column)` |  |
| 1675 | 4 | `void moved(VBox column, String label, double amount, String tone)` | One cause of the equity's movement, printed only when it moved it. |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 1680-1743)

| line | len | member | says |
|---:|---:|---|---|
| 1694 | 49 | `VBox bankRescue()` |  |

### HISTORY (lines 1744-1841)

| line | len | member | says |
|---:|---:|---|---|
| 1760 | 81 | `void historyPage(VBox column)` |  |


# CentralBankScreen.java - 416 lines · 24 methods · 4 constants · interface

`ham/citybuildersim/ui/CentralBankScreen.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the
> pages behind them, the Overview, its income statement, its balance sheet and the money it made and destroyed in this
> slice, the other five to follow. BankScreen owns it: it is drawn inside BankScreen's frame and kept pool, reached through
> the tab's choice of the two banks (BankScreen.choicePage()) or the rail's bank icon.
> 
> <p>WHY. Jerus, 2026-11-26: "when you click bank you have two options, the commercial bank, and the central bank ... give
> both the banks proper statements, aka ability to not only view summary but also the statements same way the other
> sectors, but make sure to really include alot of stuff into the central bank area". The central bank was a card on
> Finances > Money and another on Government, each a figure or two read once; it had no page of its own, no last month, no
> year ago and no statement that added up. The statements here are CentralBankStatements' tables drawn through
> StatementView as the sectors' and the commercial bank's are (BankScreen.profitStatementView()), beside ratios whose parts
> are the bank's own getters; the screen sums and works out nothing a statement already holds.

**Uses:** [Palette](Palette.md) (44), [CentralBankStatements](CentralBankStatements.md) (30), [SectorScreen](SectorScreen.md) (28), [CentralBank](CentralBank.md) (17), [Icons](Icons.md) (14), [Kept](Kept.md) (14), [StatementView](StatementView.md) (7), [SectorStatements](SectorStatements.md) (6), [DebtManager](DebtManager.md) (4), [BankScreen](BankScreen.md) (3), [CityCalendar](CityCalendar.md) (3), [UserInterface](UserInterface.md) (2), [PriceIndex](PriceIndex.md) (2)

**Used by (3):** [BankScreen](BankScreen.md), [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 50 | THE FRAME (CBK1, 2026-11-26) |
| 126 | THE OVERVIEW (CBK1, 2026-11-26) |
| 232 | ITS STATEMENTS (CBK1, 2026-11-26) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `CentralBankScreen.OVERVIEW` | `"Overview", INCOME = "Income statement", SHEET = "Balance sheet", MADE_AND_DE...` | The Overview, and the eight pages behind it. |
| 64 | `CentralBankScreen.PAGES` | `{ OVERVIEW, INCOME, SHEET, MADE_AND_DESTROYED, HOLDINGS, INFLATION, MONEY, HI...` | The nine chips, in the strip's order: the Overview first. |
| 67 | `CentralBankScreen.ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.BANKNOTE, Icons.SAFE, Ico...` | ...and each chip's icon. |
| 71 | `CentralBankScreen.PAGE_INFO` | `{ "The central bank at a glance: the money it has made, what it holds, its mo...` | Each page's (i), in PAGES' order. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `private final UserInterface ui` | The window this screen draws into: its game, its redraw. |
| 43 | `private final BankScreen bank` | The Bank tab this screen is a side of: the way back to the choice, the frame, and the formatters. |
| 87 | `String page` | Which page is open; the rail's bank icon resets it to the Overview. |
| 90 | `final Set<String> openLines` | What the player has opened on a statement - a line's note - by key, so a redraw on the clock leaves it open (BankScreen.openLines'). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 380 | **type** `final class CentralBankScreen` | The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the pages behind them, the Overview, its income statement, its balance sheet and the money it made an... |
| 45 | 4 | `CentralBankScreen(UserInterface ui, BankScreen bank)` |  |

### THE FRAME (CBK1, 2026-11-26) (lines 50-125)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 4 | `static String pageInfo(String page)` | A page's (i) by its name. |
| 99 | 5 | `HBox head()` | The head: "Bank › Central bank", at its right the door to the policy rate. |
| 106 | 3 | `HBox strip()` | The nine chips, the page open lit; the strip has no Summary \| Statement switch, since each page is what it is. |
| 111 | 9 | `void page(VBox p)` | The page open, drawn into the scroller's page. |
| 122 | 3 | `private void laterPage(VBox p)` | A page the next slice draws: a muted line, in a scope of its own. |

### THE OVERVIEW (CBK1, 2026-11-26) (lines 126-231)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 23 | `private void overviewPage(VBox p)` | The Overview: its month in a sentence, four cards of figures, the doors. |
| 160 | 6 | `private static String lead(CentralBank cb)` | The lead: what it has made and holds, what it earned or lost this month and what it owes the treasury. |
| 168 | 9 | `private VBox madeCard(CentralBank cb)` | THE MONEY IT HAS MADE: M0 and its two parts; a click opens the money made and destroyed. |
| 179 | 10 | `private VBox holdsCard(CentralBank cb)` | WHAT IT HOLDS: its advances against the treasury's ceiling, its paper and its vault; a click opens the balance sheet. |
| 191 | 12 | `private VBox monthCard(CentralBank cb)` | ITS MONTH: its profit and what the strike did with it, the loss it carries and what M0 moved by; a click opens the income statement. |
| 205 | 14 | `private VBox rateCard(CentralBank cb, DebtManager ledger, PriceIndex prices)` | THE RATE AND PRICES: the policy rate, inflation against its target, its share of the city's term paper; a click opens Inflation & credibility. |
| 221 | 10 | `private VBox pagesBehind()` | THE PAGES BEHIND IT: a door pill to each of the other eight. |

### ITS STATEMENTS (CBK1, 2026-11-26) (lines 232-416)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 12 | `private void incomePage(VBox p)` | The income statement, formal, this month against last, its ratios beside it. |
| 258 | 13 | `private void sheetPage(VBox p)` | The balance sheet, formal, this month against a year ago, its ratios beside it. |
| 273 | 15 | `private void madePage(VBox p)` | The money made and destroyed, this month against last, with what M0 did under it. |
| 290 | 12 | `private Node statement(SectorStatements.Table t, String key, String nowWord, String thenWord, String shareWord, double base, do...` | A statement as BankScreen draws its own: the title block, the toolbar and the table in a card, the ratios beside it. |
| 304 | 3 | `private static String of(double part, double whole)` | A part over its whole as a share, "—" with no whole. |
| 312 | 25 | `private VBox incomeRatios(CentralBank cb, SectorStatements.Table t)` | The income statement's ratios: its return on what it holds, each source's share of what it earned, what its reserves cost, how much of the profit goes to the treasury, and the loss it still carries. |
| 339 | 20 | `private VBox sheetRatios(CentralBank cb, DebtManager ledger)` | The balance sheet's ratios: its equity and its paper over its assets, its draw on the treasury's ceiling, the parts of M0, its vault, and the market's mark on its paper. |
| 361 | 10 | `private VBox moneyRatios(CentralBank cb)` | The money statement's ratios: what was made and what was destroyed against the M0 the month opened with, and what M0 moved by. |
| 373 | 19 | `private static String incomeSaid(String id)` | An (i) for a row of the income statement, by its id; null for a row that needs none. |
| 394 | 12 | `private static String sheetSaid(String id)` | An (i) for a row of the balance sheet, by its id; null for a row that needs none. |
| 408 | 8 | `private static String moneySaid(String id)` | An (i) for a row of the money statement, by its id; null for a row that needs none. |


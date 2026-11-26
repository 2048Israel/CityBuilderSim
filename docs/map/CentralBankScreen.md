# CentralBankScreen.java - 923 lines · 51 methods · 11 constants · interface

`ham/citybuildersim/ui/CentralBankScreen.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the
> nine pages behind them: the Overview; its income statement, its balance sheet and the money it made and destroyed; its
> Holdings (the city's paper it holds by when each piece matures, at face and at market, what it bought and sold, and what
> the holdings take off the curve); Inflation &amp; credibility (prices against the target, how far people trust it, the real
> rate and what the rule would set); Money (M0 in its parts against M2, the vault, the gold, what it has made since
> founding); its History (the charts of the series City History keeps for it); and its Policy (the dials that apply at once,
> Policy's own cards drawn here, beside the policy rate read and a door to where it is set). BankScreen owns it: it is drawn
> inside BankScreen's frame and kept pool, reached through the tab's choice of the two banks (BankScreen.choicePage()) or the
> rail's bank icon.
> 
> <p>WHY. Jerus, 2026-11-26: "when you click bank you have two options, the commercial bank, and the central bank ... give
> both the banks proper statements, aka ability to not only view summary but also the statements same way the other
> sectors, but make sure to really include alot of stuff into the central bank area". The central bank was a card on
> Finances > Money and another on Government, each a figure or two read once; it had no page of its own, no last month, no
> year ago and no statement that added up. The statements here are CentralBankStatements' tables drawn through
> StatementView as the sectors' and the commercial bank's are (BankScreen.profitStatementView()), beside ratios whose parts
> are the bank's own getters; the screen sums and works out nothing a statement already holds. The five pages after them
> read the same getters as cards (the holdings' ladder is CentralBankStatements.ladder()), and work out no more than a
> share of one getter over another.

**Uses:** [Palette](Palette.md) (114), [Kept](Kept.md) (54), [CentralBankStatements](CentralBankStatements.md) (37), [CentralBank](CentralBank.md) (31), [Icons](Icons.md) (30), [SectorScreen](SectorScreen.md) (27), [DebtManager](DebtManager.md) (22), [StatementView](StatementView.md) (7), [PriceIndex](PriceIndex.md) (7), [PolicyScreen](PolicyScreen.md) (7), [FinancesScreen](FinancesScreen.md) (7), [SectorStatements](SectorStatements.md) (6), [Expectations](Expectations.md) (4), [BankScreen](BankScreen.md) (3), [CityCalendar](CityCalendar.md) (3), [UserInterface](UserInterface.md) (2), [HistorySave](HistorySave.md) (1)

**Used by (5):** [BankScreen](BankScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 62 | THE FRAME (CBK1, 2026-11-26) |
| 142 | THE OVERVIEW (CBK1, 2026-11-26) |
| 248 | ITS STATEMENTS (CBK1, 2026-11-26) |
| 433 | THE FIVE PAGES' PIECES (CBK1, 2026-11-26) |
| 468 | HOLDINGS (CBK1, 2026-11-26) |
| 608 | INFLATION & CREDIBILITY (CBK1, 2026-11-26) |
| 703 | MONEY (CBK1, 2026-11-26) |
| 819 | HISTORY (CBK1, 2026-11-26) |
| 863 | POLICY (CBK1, 2026-11-26) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `CentralBankScreen.OVERVIEW` | `"Overview", INCOME = "Income statement", SHEET = "Balance sheet", MADE_AND_DE...` | The Overview, and the eight pages behind it. |
| 76 | `CentralBankScreen.PAGES` | `{ OVERVIEW, INCOME, SHEET, MADE_AND_DESTROYED, HOLDINGS, INFLATION, MONEY, HI...` | The nine chips, in the strip's order: the Overview first. |
| 79 | `CentralBankScreen.ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.BANKNOTE, Icons.SAFE, Ico...` | ...and each chip's icon. |
| 83 | `CentralBankScreen.PAGE_INFO` | `{ "The central bank at a glance: the money it has made, what it holds, its mo...` | Each page's (i), in PAGES' order. |
| 443 | `CentralBankScreen.LEAD_WIDE` | `900` | How wide a page's lead sentence runs before it wraps: Finances' Money page's. |
| 481 | `CentralBankScreen.HOLDINGS_INFO` | `"The city's own paper the central bank holds - bought with money it made, whi...` | The Holdings page's (i). |
| 488 | `CentralBankScreen.CURVE_TERMS` | `{ 12, 60, 120, 360 }` | The terms whose premium and compression the Holdings page lists, in months: a year, five, ten and thirty. |
| 620 | `CentralBankScreen.INFLATION_INFO` | `"Prices against the target the city holds inflation to: this year's inflation...` | The Inflation & credibility page's (i). |
| 715 | `CentralBankScreen.MONEY_INFO` | `"M0, every dollar the central bank has made and not taken back, in its two pa...` | The Money page's (i). |
| 831 | `CentralBankScreen.HISTORY_INFO` | `"Its figures as City History keeps them, a point a month: M2 and M0, the rese...` | The History page's (i). |
| 877 | `CentralBankScreen.POLICY_INFO` | `"The central bank's own dials, the same ones Policy sets: the inflation targe...` | The Policy page's (i). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `private final UserInterface ui` | The window this screen draws into: its game, its redraw. |
| 55 | `private final BankScreen bank` | The Bank tab this screen is a side of: the way back to the choice, the frame, and the formatters. |
| 104 | `String page` | Which page is open; the rail's bank icon resets it to the Overview. |
| 107 | `final Set<String> openLines` | What the player has opened on a statement - a line's note - by key, so a redraw on the clock leaves it open (BankScreen.openLines'). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 875 | **type** `final class CentralBankScreen` | The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the nine pages behind them: the Overview; its income statement, its balance sheet and the money it ma... |
| 57 | 4 | `CentralBankScreen(UserInterface ui, BankScreen bank)` |  |

### THE FRAME (CBK1, 2026-11-26) (lines 62-141)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 4 | `static String pageInfo(String page)` | A page's (i) by its name. |
| 116 | 5 | `HBox head()` | The head: "Bank › Central bank", at its right the door to the policy rate. |
| 123 | 3 | `HBox strip()` | The nine chips, the page open lit; the strip has no Summary \| Statement switch, since each page is what it is. |
| 128 | 13 | `void page(VBox p)` | The page open, drawn into the scroller's page. |

### THE OVERVIEW (CBK1, 2026-11-26) (lines 142-247)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 23 | `private void overviewPage(VBox p)` | The Overview: its month in a sentence, four cards of figures, the doors. |
| 176 | 6 | `private static String lead(CentralBank cb)` | The lead: what it has made and holds, what it earned or lost this month and what it owes the treasury. |
| 184 | 9 | `private VBox madeCard(CentralBank cb)` | THE MONEY IT HAS MADE: M0 and its two parts; a click opens the money made and destroyed. |
| 195 | 10 | `private VBox holdsCard(CentralBank cb)` | WHAT IT HOLDS: its advances against the treasury's ceiling, its paper and its vault; a click opens the balance sheet. |
| 207 | 12 | `private VBox monthCard(CentralBank cb)` | ITS MONTH: its profit and what the strike did with it, the loss it carries and what M0 moved by; a click opens the income statement. |
| 221 | 14 | `private VBox rateCard(CentralBank cb, DebtManager ledger, PriceIndex prices)` | THE RATE AND PRICES: the policy rate, inflation against its target, its share of the city's term paper; a click opens Inflation & credibility. |
| 237 | 10 | `private VBox pagesBehind()` | THE PAGES BEHIND IT: a door pill to each of the other eight. |

### ITS STATEMENTS (CBK1, 2026-11-26) (lines 248-432)

| line | len | member | says |
|---:|---:|---|---|
| 260 | 12 | `private void incomePage(VBox p)` | The income statement, formal, this month against last, its ratios beside it. |
| 274 | 13 | `private void sheetPage(VBox p)` | The balance sheet, formal, this month against a year ago, its ratios beside it. |
| 289 | 15 | `private void madePage(VBox p)` | The money made and destroyed, this month against last, with what M0 did under it. |
| 306 | 12 | `private Node statement(SectorStatements.Table t, String key, String nowWord, String thenWord, String shareWord, double base, do...` | A statement as BankScreen draws its own: the title block, the toolbar and the table in a card, the ratios beside it. |
| 320 | 3 | `private static String of(double part, double whole)` | A part over its whole as a share, "—" with no whole. |
| 328 | 25 | `private VBox incomeRatios(CentralBank cb, SectorStatements.Table t)` | The income statement's ratios: its return on what it holds, each source's share of what it earned, what its reserves cost, how much of the profit goes to the treasury, and the loss it still carries. |
| 355 | 20 | `private VBox sheetRatios(CentralBank cb, DebtManager ledger)` | The balance sheet's ratios: its equity and its paper over its assets, its draw on the treasury's ceiling, the parts of M0, its vault, and the market's mark on its paper. |
| 377 | 10 | `private VBox moneyRatios(CentralBank cb)` | The money statement's ratios: what was made and what was destroyed against the M0 the month opened with, and what M0 moved by. |
| 389 | 19 | `private static String incomeSaid(String id)` | An (i) for a row of the income statement, by its id; null for a row that needs none. |
| 410 | 12 | `private static String sheetSaid(String id)` | An (i) for a row of the balance sheet, by its id; null for a row that needs none. |
| 424 | 8 | `private static String moneySaid(String id)` | An (i) for a row of the money statement, by its id; null for a row that needs none. |

### THE FIVE PAGES' PIECES (CBK1, 2026-11-26) (lines 433-467)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 3 | `private static HBox leadLine(String sentence, String info)` | A page's lead: a sentence in the model's figures, and behind its (i) what the page holds. |
| 451 | 10 | `private static GridPane twoAcross(List<VBox> cards)` | Cards two across, each as tall as its row, as the Overview's four are. |
| 463 | 4 | `private static String monthsLeft(double months)` | Months left in words: "7.5 months", "4.2 years"; "—" with none. |

### HOLDINGS (CBK1, 2026-11-26) (lines 468-607)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 12 | `private void holdingsPage(VBox p)` | The Holdings page: its lead, the ladder, then four cards. |
| 505 | 6 | `private static String holdingsLead(CentralBank cb, DebtManager ledger)` | The lead: what it holds at face, its share of the term paper and of all the paper, and what its dial aims at. |
| 513 | 12 | `private VBox ladderCard(DebtManager ledger)` | ITS PAPER, BY WHEN IT MATURES: the ladder as a table, or a line saying it holds nothing. |
| 527 | 17 | `private static GridPane ladderTable(List<CentralBankStatements.Rung> rungs)` | The ladder: a row to a rung, the last "Every piece", in the table pieces the commercial bank's lending tables use. |
| 546 | 11 | `private VBox bookCard(CentralBank cb, DebtManager ledger)` | ITS BOOK AT MARKET: its paper at face and at market, the mark between, the months left on average. |
| 559 | 17 | `private VBox boughtThisMonthCard(CentralBank cb)` | THIS MONTH: what it paid for paper and from whom, what it sold, what the paper paid it, and the gains. |
| 578 | 10 | `private VBox boughtSinceFoundingCard(CentralBank cb)` | SINCE FOUNDING: what it paid for paper and was paid for it, and of the buying from the families and the treasury at issue. |
| 590 | 17 | `private VBox curveCard(DebtManager ledger)` | WHAT IT TAKES OFF THE CURVE: for a year, five, ten and thirty, the term premium and what its holdings take off it. |

### INFLATION & CREDIBILITY (CBK1, 2026-11-26) (lines 608-702)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 11 | `private void inflationPage(VBox p)` | The Inflation & credibility page: its lead, then four cards. |
| 639 | 5 | `private String inflationLead(DebtManager ledger, PriceIndex px)` | The lead: inflation against its target ("—" before a year of prices), and how far people trust it. |
| 646 | 14 | `private VBox inflationCard(DebtManager ledger, PriceIndex px)` | INFLATION: this year's, the one the rule read, the target, the gap in points, and what people expect. |
| 662 | 16 | `private VBox credibilityCard()` | CREDIBILITY: how far people trust it, a gauge between the least and the most it can be, and PolicyScreen's words for its month. |
| 680 | 7 | `private VBox realRateCard(DebtManager ledger)` | THE REAL RATE: the dial less what people expect, and the real rate against the world's. |
| 689 | 13 | `private VBox ruleSaysCard(DebtManager ledger, PriceIndex px)` | THE RULE: what it would set the dial to, why, and the dial today; with no year of prices, that it has nothing to say. |

### MONEY (CBK1, 2026-11-26) (lines 703-818)

| line | len | member | says |
|---:|---:|---|---|
| 720 | 16 | `private void moneyPage(VBox p)` | The Money page: its lead, then the cards, the vault's and the gold's only once they have something. |
| 738 | 5 | `private String moneyLead(CentralBank cb)` | The lead: M0 against M2, and the multiple. |
| 745 | 3 | `private static String timesWords(double times)` | M2 over M0 as "3.4 times", to two places under one ("0.02 times"); "—" with no M0. |
| 750 | 16 | `private VBox partsCard(CentralBank cb)` | M0 IN ITS PARTS: reserves and currency as a bar and as lines, each of M0. |
| 768 | 8 | `private VBox againstM2Card(CentralBank cb)` | M2 AGAINST M0: what the public holds, what the central bank made, and how many times as much. |
| 778 | 15 | `private VBox vaultCard(CentralBank cb, DebtManager ledger)` | THE VAULT: the foreign reserves at today's rate, how much of it is money this bank made, and what defending the currency has spent. |
| 795 | 11 | `private VBox goldCard(CentralBank cb)` | GOLD: what the mint paid out, what the gold it paid retired, and what is still to retire. |
| 808 | 10 | `private VBox madeSinceFoundingCard(CentralBank cb)` | SINCE FOUNDING: the money it made and destroyed, what it printed for the treasury, what it remitted, and M0 now. |

### HISTORY (CBK1, 2026-11-26) (lines 819-862)

| line | len | member | says |
|---:|---:|---|---|
| 836 | 26 | `private void historyPage(VBox p)` | The History page: its lead, then the charts - or a line saying the history is too young. |

### POLICY (CBK1, 2026-11-26) (lines 863-923)

| line | len | member | says |
|---:|---:|---|---|
| 882 | 27 | `private void policyPage(VBox p)` | The Policy page: its lead, the policy rate read beside the rule, and the holdings and the ceiling. |
| 911 | 12 | `private VBox policyRateCard(DebtManager market)` | THE POLICY RATE, read: the dial today and its real rate, with the door to Policy where it is set. |


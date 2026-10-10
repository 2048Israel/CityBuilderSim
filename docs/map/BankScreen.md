# BankScreen.java - 4,404 lines · 166 methods · 24 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The bank tab: whether the city's bank is healthy and what it charges, on
> an Overview - its state in a sentence, its capital in its band, eight
> figures and the ladder of its rates drawn in their parts - with its profit,
> its balance sheet, its lending, its funding, its capital and owners, and
> its history behind it, a picture a page.
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
> ...AND WHY IT LOOKS LIKE THIS (0.7.33). It was still a 560 px statement
> column in a 1,270 px centre: a landing of a sentence, two rows of four
> figures and the ladder as bars with a paragraph under every rung, then six
> pages of statement lines and some fifty notes. Jerus, on the screens not
> yet redone: "the others are still full of text and the design could be
> more intuitive and fun". Redrawn in Build's style (the project's
> spec-bank-0733.md): the Overview leads with the capital gauge beside the
> scorecard and the ladder in its parts - prime as its four, each borrower as
> prime and its own risk, record and concentration (BusinessDebtManager
> .quoteParts()) - and every page with the one picture that answers its
> question: a waterfall from interest to what it kept, the balance sheet as
> two bars on one scale, the book by borrower and a card a borrower, the
> deposits and what funds the book, both capital ratios on their bands, and
> the rates over the city's life with the decisions as flags. Every
> paragraph is behind an (i); every statement and table the pages printed is
> behind "details", verbatim. One verdict each: the stance (Bank
> .payoutStance()) in the banner, the strip and the capital gauge; losses
> past Bank.LOSS_WATCH; HOW FULL as NEEDS YOU's THE BANK row; defaults,
> shut-outs and the window when it is used; the residuals. The colours of the
> series are the areas' (businesses violet, families teal, landlords pink,
> the bank's own blue), never a verdict's.
> 
> ONE WAY TO WRITE EACH KIND OF NUMBER: a rate as "x.xx% a year"
> (Money.ratePerYear(), three places under a tenth of a per cent), a spread
> between two rates in points to two decimals (Money.points(), "pts" on a
> chip), a share or a ratio as "x.x%" (Money.share1()), money through Money
> - a flow says "this month", a stock does not - and a true minus wherever
> the tab writes a figure itself. The statements under "details" and
> History's money charts (City History's formatter) still write a negative
> as Money.tightMoney() does, with a hyphen (the spec's B16, left for a
> batch of its own).
> 
> The shell reads which page is open (bankArea, bankPage) for the rail and
> the scroll memory; the panel is rebuilt on the clock, so the page, the
> scroll position (UserInterface.scrolled()) and what the player has opened -
> a borrower's line, a fold, the waterfall's year (openLines, profitYear) -
> all survive a redraw.

**Uses:** [Palette](Palette.md) (598), [Bank](Bank.md) (397), [Icons](Icons.md) (72), [BusinessDebtManager](BusinessDebtManager.md) (27), [Equity](Equity.md) (21), [Exchange](Exchange.md) (20), [TimeChart](TimeChart.md) (19), [SectorScreen](SectorScreen.md) (16), [StatementView](StatementView.md) (13), [Ladder](Ladder.md) (13), [Game](Game.md) (13), [SectorStatements](SectorStatements.md) (10), [HistorySave](HistorySave.md) (10), [Sectors](Sectors.md) (10), [CityNeeds](CityNeeds.md) (9), [Mortgage](Mortgage.md) (9), [CityCalendar](CityCalendar.md) (8), [ChartModel](ChartModel.md) (6), [TreasuryFund](TreasuryFund.md) (6), [UserInterface](UserInterface.md) (4), [CentralBank](CentralBank.md) (4), [HouseholdBalance](HouseholdBalance.md) (4), [BondMarket](BondMarket.md) (4), [OrderBook](OrderBook.md) (4), [DecisionLog](DecisionLog.md) (4), [HistoryScreen](HistoryScreen.md) (4), [CorporateBond](CorporateBond.md) (3), [Pieces](Pieces.md) (3), [YearBook](YearBook.md) (3), [BuildAdvice](BuildAdvice.md) (2)... and 10 more

**Used by (3):** [FinancesScreen](FinancesScreen.md), [SectorScreen](SectorScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 94 | THE BANK (0.7.33): THE FRAME |
| 166 | · one way to write each number |
| 200 | · the frame |
| 262 | · its statements, formal (0.7.74) |
| 494 | · the stance |
| 594 | · the action cards |
| 675 | · the screen's own pieces |
| 870 | THE OVERVIEW (0.7.33; the landing, "THE BANK AT A GLANCE", until then) |
| 901 | · the capital gauge |
| 983 | · the scorecard |
| 1053 | · the rate ladder |
| 1345 | · the pages behind it |
| 1410 | PROFIT |
| 1437 | · the waterfall |
| 1585 | · the four cards |
| 1637 | · the trading desk |
| 1747 | · the statement, under details |
| 1839 | · · and what it did with it |
| 1847 | · · the last twelve months |
| 1868 | · · as ratios |
| 2013 | BALANCE SHEET (0.7.13) |
| 2316 | · beside the sheet |
| 2354 | · its equity in parts |
| 2411 | · the statement, under details |
| 2422 | · · what it owns |
| 2448 | · · what it owes |
| 2463 | · · what is left |
| 2495 | · · beside the sheet |
| 2527 | LENDING |
| 2588 | · the book |
| 2633 | · the borrowers |
| 2844 | · set aside, written off |
| 2875 | · how the next loan is priced |
| 2926 | · the tables, under details |
| 2937 | · · the businesses |
| 2990 | · · who has stopped paying |
| 3034 | · · what it lent this month |
| 3050 | · · how the next loan is priced |
| 3103 | · · what the book weighs |
| 3133 | · · what concentration costs (0.7.12) |
| 3220 | FUNDING |
| 3330 | · the three cards |
| 3426 | · the branches |
| 3542 | · the lines, under details |
| 3548 | · · what is banked |
| 3555 | · · what it can lend against (0.7.19) |
| 3563 | · · what it pays savers |
| 3581 | · · its account at the central bank |
| 3596 | · · how it is funded |
| 3605 | · · what it can carry |
| 3627 | · · its branches |
| 3664 | CAPITAL & OWNERS |
| 3770 | · payout, and how its equity moved |
| 3853 | · rescues, and the city's preferred |
| 3917 | · the lines, under details |
| 3924 | · · its capital |
| 3938 | · · ...and against everything it has lent (0.7.11, round 2) |
| 3949 | · · what it does with its profit |
| 3970 | · · how its equity moved |
| 4008 | · · its owners |
| 4014 | · · its rescues |
| 4035 | · · the city's preferred |
| 4058 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 4120 | THE BANK ASKS FOR PREFERRED (0.7.14) |
| 4221 | HISTORY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 116 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the Overview; BANK_PAGES, the pages behind it |
| 118 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the Overview that is lit until the player picks another; the rail's bank icon resets to it. |
| 122 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "Histo...` | The six pages behind the Overview, in the chip strip's order: the balance sheet beside the income statement since 0.7.13. |
| 126 | `BankScreen.OVERVIEW` | `"Overview"` | The strip (0.7.33): the Overview first - it was the landing, with a button back to it at the foot of every page - then the six. |
| 128 | `BankScreen.CHIPS` | `{ OVERVIEW, "Profit", "Balance sheet", "Lending", "Funding", "Capital & owner...` | ...the strip's seven chips: the Overview, then BANK_PAGE_NAMES in their order. |
| 131 | `BankScreen.CHIP_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.SECTOR, Icons.BANK, Icons...` | ...and each chip's icon. |
| 156 | `BankScreen.FRAME_CHROME` | `190` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and, on a page, the status strip - until the frame is laid out and its own height is read (0.7.33). |
| 159 | `BankScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same... |
| 162 | `BankScreen.SET_ASIDE` | `"#setAside", PRICING = "#pricing", LANDLORDS = "#landlords", FAMILIES = "#fam...` | The scroll targets a door can land on: Lending's cards and its pricing, Funding's carry card, the details folds. |
| 438 | `BankScreen.LEAD_INFO` | `"Every loan in the city is its money, priced from what it costs the bank to m...` | The tab's (i): the Overview's lead (the spec's L2), with what the Overview shows. |
| 445 | `BankScreen.PAGE_INFO` | `{ "Its income statement, and what it did with the profit: a walk from the int...` | Each page's (i), in BANK_PAGE_NAMES' order: what it holds (the landing's row blurbs until 0.7.33). |
| 1056 | `BankScreen.LADDER_INFO` | `"From the price of money to what each borrower pays.Every step up is a cost "...` | THE LADDER's (i): the landing's note (the spec's L10), with what the parts are. |
| 1443 | `BankScreen.NOTHING` | `.0005` | A step under half a dollar is nothing: it is left off the walk and named under it. |
| 1551 | `BankScreen.PROVISIONS_INFO` | `"A provision is money set aside for loans expected to go bad: a year's " + "e...` | The provisions' note (the spec's T, Profit 790), behind the step's tooltip and the statement's line. |
| 1588 | `BankScreen.RATIOS_INFO` | `"The margin is what it charges less what it pays for its money, on everything...` | The ratios' note (Profit 874), behind MARGIN's and COSTS' (i). |
| 2054 | `BankScreen.OWNS` | `List.of(new SheetPart(Bank.Sheet.BUSINESS_LOANS, "Loans to the businesses", P...` | What it owns, in the bars' order (the spec's section 3): the businesses' loans first, its reserves last; the allowance is the net tick. |
| 2066 | `BankScreen.OWES` | `List.of(new SheetPart(Bank.Sheet.DEPOSIT_FUNDING, "Lent past its own cash, on...` | ...and what it owes, then its owners' (equity last). |
| 2319 | `BankScreen.BESIDE_INFO` | `"Counted for what it can lend and not held on its sheet in full: the families...` | BESIDE THE SHEET's (i) (Balance sheet 1161). |
| 2613 | `BankScreen.BOOK_INFO` | `"Everything it has lent, by who owes it: the businesses' loans and the bonds ...` | Who-owes-it note (Lending 1225), behind THE BOOK's (i). |
| 3253 | `BankScreen.LEND_AGAINST_INFO` | `"Savings reach the bank wherever its branches are - online - so it lends " + ...` | What it can lend against (Funding 1681), behind the banked bar's (i). |
| 3707 | `BankScreen.STAKE_INFO` | `"A resolution makes every share the city's, in its fund's rescue book, which ...` | The city's stake's note (Capital 2002). |
| 3802 | `BankScreen.MOVED_INFO` | `"Equity moves by what it earned, what it was given and what it paid out, and ...` | The equity's note (Capital 1990): Jerus's rule on a plug. |
| 3856 | `BankScreen.RESCUES_INFO` | `"When a bank loses more than it owns, the city resolves it: the old owners lo...` | The rescues' note (Capital 2027). |
| 4077 | `BankScreen.RESCUE_INFO` | `"Its owners lose everything: the households' shares and the world's pass to t...` | The rescue's note (Rescue 2119). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 90 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 115 | `String bankArea` | null is the Overview; BANK_PAGES, the pages behind it |
| 119 | `String bankPage` |  |
| 135 | `final Set<String> openLines` | What the player has opened - a statement's line, a borrower's sectors, a "details" fold - by key, so a redraw on the clock leaves it open (Statement.opens()). |
| 138 | `boolean profitYear` | The Profit page's waterfall: the last twelve months rather than this month (0.7.33), kept while the tab is. |
| 141 | `private String scrollTarget` | Where the page is to be scrolled to once it is drawn - a card's key - or null for its top. |
| 144 | `private final java.util.Map<String, Node> targets` | The nodes a door on this tab can scroll to, by key, as the page draws them. |
| 147 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 150 | `private final ChartModel historyWindow` | History's window, shared by its charts and kept across the month's redraw, for a chart dragged back to stay where it was put - though no chart here takes a drag (a small TimeChart does not pan; 0.7.38 took "Drag to lo... |
| 153 | `private int drawnMonth` | The month the tab last drew: a new month landing is when its figures pop and its gauge grows. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 4318 | **type** `final class BankScreen` | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figures and the ladder of its rates drawn in their parts - with its profit... |
| 92 | 1 | `BankScreen(UserInterface ui)` |  |

### THE BANK (0.7.33): THE FRAME (lines 94-165)

### one way to write each number (lines 166-199)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 6 | `static String defaultShare(double pd)` | A default rate, which runs from the curve's far tail to all of it: "under 0.1%" rather than a "0.0%" that reads as none, "x.x%" to 99.9%, then "all". |
| 177 | 4 | `static String yearWords(Bank bank)` | "the last 12 months", or as many as the bank has lived. |
| 183 | 1 | `static String pts(double spread)` | A spread for a chip: "+2.06 pts", "−0.22 pts" - Money.points() shortened. |
| 186 | 1 | `static String mv(double thousands)` | Money signed for a movement, with a true minus: "+$851,963", "−$81,238", "$0" (Money.signed(), never negated). |
| 189 | 4 | `static String m(double thousands)` | Money with a true minus when below nothing, abbreviated: "−$2.1M". |
| 195 | 4 | `static String mFull(double thousands)` | ...with every digit. |

### the frame (lines 200-261)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 47 | `void showBankMenu()` | The tab's entry point: the Overview, or the page behind it the player was on. |
| 256 | 5 | `void pickView(boolean statement)` | The switch picked (0.7.74): this page in the other view, at its top - one choice with the sector pages' (D1). |

### its statements, formal (0.7.74) (lines 262-493)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 18 | `void profitStatementView(VBox page)` | The bank's income statement, formal, its ratios beside it. |
| 296 | 20 | `void sheetStatementView(VBox page)` | The bank's balance sheet, formal, against a year ago, its ratios beside it. |
| 318 | 4 | `static Bank.Sheet sheetLineOf(String id)` | The sheet line a statement row is, by its id ("bs.RESERVES"), or null for a subtotal. |
| 330 | 48 | `VBox bankRatios(Bank bank, boolean sheet)` | Its ratios (spec 4.5), on the income statement: net interest margin, cost to income, the fees' share, provisions over what it has lent, return on equity and its payout; on the sheet: its capital over its weighted book... |
| 380 | 4 | `void pick(String name)` | A chip picked: the Overview, or a page at its top. |
| 386 | 3 | `void openPage(String page)` | Opens one page behind the Overview, at its top - the Overview's cards, and the inbox's "defaults" notice (0.7.8). |
| 391 | 7 | `void open(String page, String target)` | ...scrolled to a card on it (0.7.33: the doors land where the line is decided). |
| 400 | 5 | `void openOverview()` | The Overview, at its top. |
| 407 | 16 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (FinancesScreen's, 0.7.32). |
| 425 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 460 | 4 | `static String pageInfo(String page)` | A page's (i) by its name. |
| 471 | 7 | `HBox head()` | The head: "Bank" with the money blue's swatch, or the breadcrumb "Bank › Profit", the first word a way back to the Overview; at its right the two doors - the policy rate (Policy › Money › The policy rate) and the trea... |
| 480 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (FinancesScreen.openPolicy()'s). |
| 489 | 4 | `void openSector(String key, String page)` | One sector's books, on one of its pages (Sectors › it › Cash & debt). |

### the stance (lines 494-593)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 8 | `static String stanceTone(Bank bank)` | Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. |
| 507 | 9 | `static String stanceWord(Bank bank)` | The stance in a word or three, on its chip (the spec's D18). |
| 523 | 27 | `HBox statusStrip(boolean fresh)` | THE STATUS STRIP, on every page behind the Overview: the stance as a chip and the four figures across the top of every page until 0.7.33 - profit, the binding capital ratio, prime with its move on last month, and what... |
| 552 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of a strip or the scorecard, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 555 | 16 | `List<Cell> stripCells()` | The status strip's four (pure: the probe reads them as the strip shows them). |
| 573 | 5 | `static String capitalFigure(Bank bank)` | The binding ratio as the gauge and the strip write it: "failed", "—" with nothing lent, else "14.1%". |
| 580 | 13 | `String primeChange()` | PRIME's move on last month, from History's last two points: "▲ 0.25 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). |

### the action cards (lines 594-674)

| line | len | member | says |
|---:|---:|---|---|
| 603 | 49 | `List<Node> actionCards()` | What the player must act on or know of first, at the top of every page: no bank; a failed bank and its rescue; a bank under its minimum; the bank asking the city for preferred; money from abroad (on Funding); a sheet ... |
| 654 | 13 | `static VBox actionCard(String svg, String tone, String title, String line, String whole, Node act)` | One action card: its icon square, its title, one line with the whole behind an (i), and what to press (null: nothing), on the raised ground with a 3 px top edge in `tone`. |
| 669 | 5 | `static String actionStyle(String tone)` | An action card's ground: the raised ground, its top edge 3 px in the verdict's colour. |

### the screen's own pieces (lines 675-869)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 687 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 695 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - LandScreen.worthCard()'s head, the spec's info card. |
| 714 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 723 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 729 | 9 | `static VBox doorCard(VBox c, Runnable go)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 740 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 751 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 758 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 763 | 1 | `static Label line(String text)` | A plain line of words in a card, wrapping. |
| 766 | 1 | `static Label muted(String text)` | ...muted. |
| 769 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut (Pieces.hint() at its own width). |
| 776 | 1 | `static Label big(String text, String tone)` | A big figure on a card, in a colour. |
| 779 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width (the spec's D15). |
| 787 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 792 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 806 | 7 | `static HBox swatch(String colour, String name, boolean ghost)` | A key's entry: a swatch and a name, the swatch outlined when `ghost`. |
| 815 | 5 | `static javafx.scene.layout.FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 822 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 827 | 3 | `static Segment seg(double amount, String colour, String label, String tip)` | ...with a label in it, drawn where it fits. |
| 832 | 3 | `static Segment ghost(double amount, String colour, String tip)` | ...hollow: what a rate is built on, or a quote nobody pays. |
| 837 | 3 | `static Tick tick(double at, String colour, String name, String tip)` | A named mark across a bar. |
| 842 | 11 | `static Label chipTip(String text, String colour, String tip)` | A chip with a tooltip. |
| 855 | 9 | `Region spark(String series, int n, String colour, double width, double height)` | The last `n` months of a History series, for a sparkline. |
| 866 | 3 | `static String partTip(String name, double amount, double whole)` | A segment bar's tooltip line: "name  $X  (12.3%)". |

### THE OVERVIEW (0.7.33; the landing, "THE BANK AT A GLANCE", until then) (lines 870-900)

| line | len | member | says |
|---:|---:|---|---|
| 884 | 16 | `void overviewPage(VBox page, boolean fresh)` |  |

### the capital gauge (lines 901-982)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 17 | `static String capitalTip(Bank bank)` | The capital gauge's tooltip: both measures, the one that binds named (the old capital band's, since 0.7.11 round 2). |
| 923 | 2 | **type** `record Gauge(String caption, String figure, String tone, String line, String key, String other, double valu...` | The gauge's words, worked out without drawing it (pure: the probe reads them). |
| 927 | 23 | `Gauge gauge()` | The Overview's gauge: the measure that binds, as status() reads it (the spec's D4). |
| 959 | 23 | `VBox capitalCard(Bank bank, boolean fresh)` | THE CAPITAL CARD: the ratio that binds at 28 px in its stance's colour, what it is of and its target, the bar in its band with the minimum, the target and the top marked (Pieces.bandBar()), the other measure in one mu... |

### the scorecard (lines 983-1052)

| line | len | member | says |
|---:|---:|---|---|
| 993 | 37 | `List<Cell> scorecardCells(List<CityNeeds.Need> all)` | The eight figures (pure: the probe reads them as the scorecard shows them): what it earned, what that returns its owners, its losses and HOW FULL it is (0.7.33: Bank.strain(), worded and coloured as NEEDS YOU's THE BA... |
| 1032 | 20 | `VBox scorecard(List<CityNeeds.Need> all, boolean fresh)` | The scorecard: the eight as two rows of four, each a door. |

### the rate ladder (lines 1053-1344)

| line | len | member | says |
|---:|---:|---|---|
| 1064 | 21 | `VBox ladderCard()` | The ladder's card: its head, its key, the rungs. |
| 1087 | 12 | `static String saversWhy(Bank.Ladder l)` | Why savers get what they get: the share its funding asks for, and whether its margin held them under it. |
| 1101 | 8 | `List<String> owing()` | The sectors that owe the bank or are shut out of it, in the registry's order. |
| 1111 | 1 | `String sectorIcon(String key)` | A sector's icon, by its key (Icons.ofSector()). |
| 1128 | 60 | `List<Rung> rungs()` | THE LADDER'S RUNGS (pure: the probe reads them), each drawn in its parts - the spec's D2 and D3. |
| 1196 | 83 | `List<Rung> borrowerRungs(Bank.Ladder l, boolean all)` | What each borrower pays (pure): the businesses that owe it or are shut out, each with its bonds under it when `all`; the families; the carry trade; an insured mortgage; and, when `all`, the city's paper. |
| 1281 | 38 | `Rung sectorRung(String name, Bank.Ladder l, BusinessDebtManager credit)` | A sector's rung: prime outlined, then its own risk, its record and the book's concentration (the spec's D2; B4 named the concentration). |
| 1321 | 23 | `Rung bondRung(String name, double y, Bank bank, BusinessDebtManager credit, BondMarket market)` | A sector's bond: what the bank would hold one at, over its loan's rate, a tick at what its bonds pay (BondMarket.bankYield(), 0.7.12). |

### the pages behind it (lines 1345-1409)

| line | len | member | says |
|---:|---:|---|---|
| 1348 | 1 | **type** `record PageCard(String name, String icon, String blurb, String figure, String sub, String tone, String series)` | One page as a card on the Overview, worked out without drawing it (pure: the probe reads them). |
| 1351 | 32 | `List<PageCard> pageCards()` | The six (the landing's rows until 0.7.33, the spec's L16): what each holds and its headline. |
| 1385 | 24 | `VBox pagesBehind()` | THE PAGES BEHIND IT: the six as cards, three to a row, each with a year of its line. |

### PROFIT (lines 1410-1436)

| line | len | member | says |
|---:|---:|---|---|
| 1425 | 11 | `void profitPage(VBox page)` |  |

### the waterfall (lines 1437-1584)

| line | len | member | says |
|---:|---:|---|---|
| 1440 | 1 | **type** `record Walk(List<Step> steps, List<String> nothing)` | The waterfall worked out without drawing it (pure: the probe reads it): its steps, and the lines at nothing named in a line under it. |
| 1454 | 64 | `Walk walk(boolean year)` | FROM WHAT IT EARNED TO WHAT IT KEPT (pure): the income statement's lines as steps, this month or over the year (Bank.thisMonth(), overYear()); the four totals are the model's own (NET_INTEREST, PRE_TAX, NET, RETAINED)... |
| 1520 | 4 | `void step(List<Step> steps, List<String> nothing, String name, double amount, String icon, List<Slice> parts, String tip)` | A plain step of the walk: up in the money blue, down in its dark step; at nothing, named instead. |
| 1526 | 11 | `void step(List<Step> steps, List<String> nothing, String name, double amount, String icon, List<Slice> parts, String tip, Runna...` | ...with a door of its own (null: the statement under "details"). |
| 1539 | 3 | `void total(List<Step> steps, String name, double amount)` | A total of the walk, the model's own figure, drawn from zero: neutral, red under nothing. |
| 1544 | 5 | `void openStatement()` | The statement under "details", opened and scrolled to. |
| 1555 | 29 | `VBox waterfallCard(Bank bank)` | The waterfall's card: its head with the toggle, the walk, and what was nothing. |

### the four cards (lines 1585-1636)

| line | len | member | says |
|---:|---:|---|---|
| 1591 | 7 | `VBox marginCard(Bank bank)` |  |
| 1599 | 12 | `VBox costsCard(Bank bank)` |  |
| 1612 | 13 | `VBox roeCard(Bank bank)` |  |
| 1626 | 10 | `VBox yearCard(Bank bank)` |  |

### the trading desk (lines 1637-1746)

| line | len | member | says |
|---:|---:|---|---|
| 1640 | 6 | `boolean deskShows(Bank bank)` | Whether the desk card shows: it holds something, or traded or was paid this month. |
| 1655 | 62 | `VBox deskCard(Bank bank)` | THE TRADING DESK as a card (0.7.33): what it sold and bought and from whom, what it was paid, its re-mark and what it holds; its book and its largest holding against their caps (Exchange.BOOK_LIMIT and POSITION_LIMIT ... |
| 1719 | 27 | `String deskInfo(Bank bank)` | The desk card's (i): its orders on the book and, when it lost on the re-mark, why (Profit 968-997). |

### the statement, under details (lines 1747-2012)

| line | len | member | says |
|---:|---:|---|---|
| 1756 | 133 | `VBox profitStatement(Bank bank)` | The income statement as the page printed it until 0.7.33, verbatim (the spec's R1-R12, D15): this month against last, every line opening into what it is made of, then what it did with it, the last twelve months, and t... |
| 1891 | 4 | `HBox line(Bank bank, String label, Bank.Line which, boolean known, boolean out)` | One statement line, this month and last - negated for money going out. |
| 1897 | 4 | `VBox total(Bank bank, String label, Bank.Line which, boolean known)` | One total, this month and last. |
| 1903 | 4 | `void year(VBox column, Bank bank, String label, Bank.Line which, boolean out)` | One line of the year, negated for money going out. |
| 1917 | 95 | `VBox deskDetail(Bank bank)` | THE TRADING DESK, opened (2026-09-18, and so it foots). |

### BALANCE SHEET (0.7.13) (lines 2013-2315)

| line | len | member | says |
|---:|---:|---|---|
| 2040 | 9 | `void sheetPage(VBox page)` |  |
| 2051 | 1 | **type** `record SheetPart(Bank.Sheet line, String name, String colour, boolean ghost)` | One line of the sheet as the bars and the key draw it: the line, its name, its colour, whether it is outlined. |
| 2075 | 2 | **type** `record SheetBars(List<Segment> owns, List<Tick> ownsTicks, List<Segment> owes, List<Tick> owesTicks, double...` | The two bars worked out without drawing them (pure: the probe reads them): their segments and ticks, their words, and the one scale. |
| 2079 | 60 | `SheetBars sheetSegments()` | WHAT IT OWNS · WHAT IT OWES, AND WHAT IS ITS OWNERS' (pure; the spec's D6). |
| 2141 | 8 | `String sheetInfo(Bank bank)` | The hero's (i): what the two bars are, and the year ago's note when it is not on file (the spec's B7). |
| 2151 | 25 | `VBox sheetHero(Bank bank)` | The hero card: the two bars and, under them, every line in two columns. |
| 2178 | 6 | `static String moved(double now, double then, boolean known)` | A line's move on a year ago: "▲ $1.2M", "▼ $3.4M", nothing when none is on file or it did not move. |
| 2186 | 37 | `Node legendRow(Bank bank, SheetPart p, boolean known)` | One line of the key: its swatch, its name with its sentence behind an (i), its figure now and its move on a year ago; the loans and the interim financing open by sector in place. |
| 2225 | 13 | `Node legendTotal(String label, double now, double then, boolean known)` | A total under a column of the key. |
| 2240 | 18 | `VBox bySector(Bank bank, Bank.Sheet line, boolean known)` | The loans or the interim financing by sector, this month and a year ago - null for a line that does not open. |
| 2260 | 55 | `String sheetSaid(Bank bank, Bank.Sheet line)` | Each line's sentence (the statement's said()s, Balance sheet 1029-1130): the key's (i)s and the statement's openings. |

### beside the sheet (lines 2316-2353)

| line | len | member | says |
|---:|---:|---|---|
| 2325 | 28 | `VBox besideTheSheet(Bank bank)` | BESIDE THE SHEET: the city's own deposits as one bar, what it has lent on them, and how big they are against the sheet. |

### its equity in parts (lines 2354-2410)

| line | len | member | says |
|---:|---:|---|---|
| 2357 | 53 | `VBox equityParts(Bank bank)` | ITS EQUITY IN PARTS: the city's preferred, paid in and retained, now and a year ago, on one scale; a part below nothing drawn leftward in the spent grey. |

### the statement, under details (lines 2411-2526)

| line | len | member | says |
|---:|---:|---|---|
| 2414 | 98 | `VBox sheetStatement(Bank bank)` | The balance sheet as the page printed it until 0.7.33, verbatim (the spec's B1-B7, D15). |
| 2514 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail)` | One line of the balance sheet, this month and a year ago, opening into its detail. |
| 2518 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail, String word)` |  |
| 2523 | 3 | `static VBox said(String text)` | A line's detail that is a sentence. |

### LENDING (lines 2527-2587)

| line | len | member | says |
|---:|---:|---|---|
| 2545 | 19 | `void lendingPage(VBox page)` |  |
| 2566 | 7 | `String stanceInfo(Bank bank)` | The capital-lets-it-lend note (Lending 1449), behind the stance chip's (i). |
| 2575 | 12 | `HBox stanceRow(Bank bank)` | What its capital lets it lend: Bank.lendingStance() as a chip and its line, and the growth it allows when it is finite. |

### the book (lines 2588-2632)

| line | len | member | says |
|---:|---:|---|---|
| 2591 | 1 | **type** `record BookPart(String name, double amount, String colour)` | One borrower's part of the book (pure). |
| 2594 | 17 | `List<BookPart> bookParts()` | THE BOOK by borrower (pure): the sheet's loan lines, the bonds among them, so they foot to Bank.getBook() (B3). |
| 2619 | 13 | `VBox bookCard(Bank bank)` |  |

### the borrowers (lines 2633-2843)

| line | len | member | says |
|---:|---:|---|---|
| 2636 | 3 | **type** `record Borrower(String key, boolean shut, int blockedMonths, double rate, double spread, double owesBank, d...` | One business as its card shows it (pure: the probe reads them). |
| 2641 | 21 | `List<Borrower> borrowers()` | The businesses the old table listed (Lending 1243): owing, shut out, set aside against, or written off this month. |
| 2664 | 20 | `static String borrowersInfo()` | The businesses' note (Lending 1280): its short line on the section's head, and the whole behind its (i). |
| 2686 | 30 | `VBox borrowersSection(Bank bank)` | THE BORROWERS: a card a business, then the landlords, the families and the carry trade, three to a row. |
| 2718 | 53 | `VBox borrowerCard(Borrower b)` | One business as a card: its rate over prime, what it owes the bank and of its bonds, its leverage on its scale, its default rate, its chips; a click opens its Cash & debt page. |
| 2773 | 35 | `VBox landlordsCard(Bank bank)` | THE LANDLORDS: their insured mortgages (Lending 1309-1344), part of what the businesses owe. |
| 2810 | 19 | `VBox familiesCard(Bank bank)` | THE FAMILIES: their credit lines (Lending 1347-1369), shown while they owe or something is set aside. |
| 2831 | 12 | `VBox carryCard(Bank bank)` | THE CARRY TRADE: what foreigners have borrowed here to hold abroad (the Overview's rung, Balance sheet's line). |

### set aside, written off (lines 2844-2874)

| line | len | member | says |
|---:|---:|---|---|
| 2846 | 16 | `VBox setAsideCard(Bank bank)` |  |
| 2863 | 11 | `VBox writtenOffCard(Bank bank)` |  |

### how the next loan is priced (lines 2875-2925)

| line | len | member | says |
|---:|---:|---|---|
| 2878 | 20 | `String pricingInfo(Bank bank)` | Prime's note and the quotes' note (Lending 1470, 1515), behind HOW THE NEXT LOAN IS PRICED's (i). |
| 2900 | 25 | `VBox pricingCard(Bank bank)` | HOW THE NEXT LOAN IS PRICED: prime as its four parts, each named, then a row a borrower. |

### the tables, under details (lines 2926-3219)

| line | len | member | says |
|---:|---:|---|---|
| 2933 | 235 | `VBox lendingTables(Bank bank)` | The old page's tables and lines, verbatim (the spec's N2, N6, N7, N9, N11; D15), with B9's fix - the businesses' first column is what each owes THE BANK - and B2's: no green or amber on what is not a verdict. |
| 3170 | 12 | `static String bookName(Bank.Book book)` | What the weight table calls each book. |
| 3184 | 35 | `VBox bondsHeld(Bank bank)` | THE BUSINESSES' BONDS IT HOLDS (0.7.12), verbatim, under their own fold - the Overview's bond rungs open it. |

### FUNDING (lines 3220-3329)

| line | len | member | says |
|---:|---:|---|---|
| 3238 | 13 | `void fundingPage(VBox page)` |  |
| 3258 | 2 | **type** `record FundingBars(List<Segment> banked, List<Tick> bankedTicks, List<Segment> funds, String bankedWords, S...` | The two bars worked out without drawing them (pure: the probe reads them). |
| 3262 | 33 | `FundingBars fundingBars()` | WHAT THE CITY HAS BANKED and WHAT FUNDS ITS BOOK (pure), each on its own scale. |
| 3297 | 20 | `VBox fundingHero(Bank bank, Bank.Ladder l)` | The hero: the two bars, each with its words at the left and its key under it. |
| 3319 | 10 | `static HBox fundingRow(String words, Node bar)` | One of the hero's rows: its words in a column at the left, the bar taking the rest. |

### the three cards (lines 3330-3425)

| line | len | member | says |
|---:|---:|---|---|
| 3333 | 6 | `static String saversShareInfo()` | What savers are paid's note (Funding 1712). |
| 3341 | 20 | `VBox saversCard(Bank bank, Bank.Ladder l)` | SAVERS: the policy rate, the share its funding asks it to pass on, the rate it chose and what it paid - a ladder of four - and what that paid this month. |
| 3363 | 5 | `static VBox saversStep(String what, String figure, String under)` | One step of the savers' ladder: its words over its figure. |
| 3370 | 14 | `VBox centralBankCard(Bank bank, Bank.Ladder l)` | THE CENTRAL BANK: its reserves there and what they earned, and what it borrowed overnight and what that cost - red while it borrows. |
| 3386 | 30 | `VBox canCarryCard(Bank bank)` | WHAT IT CAN CARRY: what its capital carries and what its deposits carry on one scale, the tighter marked BINDS, its weighed book against them, and how full that is. |
| 3418 | 7 | `static VBox carryLine(String words, double amount, double scale, boolean binds, List<Tick> ticks)` | One of what it can carry's two bars: its words, the bar on the card's scale with the book's weight ticked, and BINDS on the tighter. |

### the branches (lines 3426-3541)

| line | len | member | says |
|---:|---:|---|---|
| 3429 | 1 | **type** `record Verdict(String words, String tone, boolean build)` | The branch verdict worked out without drawing it (pure: the probe reads it, BankCheck asserts the planner it reads). |
| 3438 | 8 | `Verdict branchVerdict()` | WOULD ANOTHER ONE PAY? |
| 3448 | 14 | `String branchesInfo()` | The branches' notes (Funding 1801, 1848): a branch's cost, and the test run the other way. |
| 3470 | 61 | `VBox branchesCard(Bank bank)` | ITS BRANCHES, BY THEIR CUSTOMERS (0.7.19) as a card: the customers a branch against what one serves as a ring; a branch's fees against what one past the first costs; the two questions, the investors' verdict, whether ... |
| 3533 | 8 | `static HBox question(String words, boolean yes)` | One of the branch questions: a tick or a cross, and the words. |

### the lines, under details (lines 3542-3663)

| line | len | member | says |
|---:|---:|---|---|
| 3545 | 118 | `VBox fundingLines(Bank bank, Bank.Ladder l)` | The Funding page's lines as it printed them until 0.7.33, verbatim (the spec's F1-F7), with B2's colours and B6's verdict. |

### CAPITAL & OWNERS (lines 3664-3769)

| line | len | member | says |
|---:|---:|---|---|
| 3680 | 25 | `void capitalPage(VBox page)` |  |
| 3713 | 4 | `static String bandInfo()` | The band's note (Capital 1896). |
| 3719 | 8 | `static String leverageInfo(Bank bank)` | The leverage measure's note (Capital 1910). |
| 3729 | 25 | `Gauge measureGauge(boolean leverage)` | One of the two gauges, worked out without drawing it (pure: the probe reads them): the risk-weighted ratio, or the leverage ratio when `leverage`. |
| 3756 | 13 | `VBox gaugeCard(Bank bank, boolean leverage)` | A gauge's card: its caption with BINDS on the larger requirement, the ratio, the bar in its band, the key, what it is of, and why the target is where it is. |

### payout, and how its equity moved (lines 3770-3852)

| line | len | member | says |
|---:|---:|---|---|
| 3773 | 7 | `static String payoutInfo()` | The payout rule (Capital 1948). |
| 3782 | 18 | `VBox payoutCard(Bank bank)` | PAYOUT: its decision; last month's profit after tax with what it paid and bought back on it; what it held over its target and the top; the year's. |
| 3806 | 29 | `List<Step> equitySteps()` | HOW ITS EQUITY MOVED, worked out without drawing it (pure): each cause as a step, at nothing left off, "Not accounted for" always. |
| 3836 | 16 | `VBox equityMovedCard(Bank bank)` |  |

### rescues, and the city's preferred (lines 3853-3916)

| line | len | member | says |
|---:|---:|---|---|
| 3861 | 18 | `VBox rescuesCard(Bank bank)` |  |
| 3881 | 6 | `Label bankDecisionChip()` | The bank's first decision this month, in the order they were made - a rescue, the preferred offer - as a chip (the spec's section 5), or null. |
| 3889 | 5 | `static boolean preferredHasHistory(Bank bank)` | Whether the city's preferred has any history to show. |
| 3896 | 7 | `static String preferredInfo()` | The preferred's note (Capital 2046). |
| 3904 | 12 | `VBox preferredCard(Bank bank)` |  |

### the lines, under details (lines 3917-4057)

| line | len | member | says |
|---:|---:|---|---|
| 3920 | 131 | `VBox capitalLines(Bank bank)` | The Capital & owners page's lines as it printed them until 0.7.33, verbatim (the spec's C1-C8), with B2's colours. |
| 4053 | 4 | `void moved(VBox column, String label, double amount)` | One cause of the equity's movement, printed only when it moved it - plain since 0.7.33 (B2: it was green in and amber out). |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 4058-4119)

| line | len | member | says |
|---:|---:|---|---|
| 4083 | 31 | `VBox bankRescue()` |  |
| 4116 | 3 | `static VBox rescueFigure(String caption, String value, String tone)` | One of the rescue's four figures: its caption over it. |

### THE BANK ASKS FOR PREFERRED (0.7.14) (lines 4120-4220)

| line | len | member | says |
|---:|---:|---|---|
| 4134 | 86 | `VBox preferredOffer()` |  |

### HISTORY (lines 4221-4404)

| line | len | member | says |
|---:|---:|---|---|
| 4251 | 106 | `void historyPage(VBox page)` |  |
| 4359 | 4 | `TimeChart.Line percentLine(HistorySave h, String key, String name, String colour)` | A line of a History series in per cent, read out as a rate. |
| 4365 | 4 | `TimeChart.Line moneyLine(HistorySave h, String key, String name, String colour)` | ...in money. |
| 4371 | 6 | `static TimeChart.Line flatLine(HistorySave h, String key, String name, double level, String colour, String unit)` | A reference line at one level for every month: what its owners want, the 100% line. |
| 4379 | 9 | `TimeChart chart(HistorySave h, List<TimeChart.Line> lines, String unit, List<YearBook.Band> bands, List<ChartModel.Flag> flags,...` | One of the charts, on the tab's window (it follows `leader` when there is one), without controls. |
| 4390 | 13 | `VBox smallCard(String title, String info, String unit, TimeChart leader, HistorySave h, List<YearBook.Band> bands, List<TimeCha...` | A small chart's card: its title and (i), the chart following the rates' window, its key, and its statistics as chips. |


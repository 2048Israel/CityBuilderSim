# BankScreen.java - 4,656 lines · 179 methods · 25 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (598), [Bank](Bank.md) (401), [Kept](Kept.md) (218), [Icons](Icons.md) (73), [BusinessDebtManager](BusinessDebtManager.md) (27), [Equity](Equity.md) (21), [Exchange](Exchange.md) (20), [TimeChart](TimeChart.md) (20), [SectorScreen](SectorScreen.md) (16), [StatementView](StatementView.md) (13), [Ladder](Ladder.md) (13), [SectorStatements](SectorStatements.md) (10), [HistorySave](HistorySave.md) (10), [Sectors](Sectors.md) (10), [CityNeeds](CityNeeds.md) (9), [Mortgage](Mortgage.md) (9), [Game](Game.md) (9), [CityCalendar](CityCalendar.md) (8), [Pieces](Pieces.md) (7), [ChartModel](ChartModel.md) (6), [TreasuryFund](TreasuryFund.md) (6), [UserInterface](UserInterface.md) (4), [CentralBank](CentralBank.md) (4), [HouseholdBalance](HouseholdBalance.md) (4), [BondMarket](BondMarket.md) (4), [OrderBook](OrderBook.md) (4), [DecisionLog](DecisionLog.md) (4), [HistoryScreen](HistoryScreen.md) (4), [Money](Money.md) (3), [CorporateBond](CorporateBond.md) (3)... and 13 more

**Used by (6):** [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [KeptBench](KeptBench.md), [ResearchScreen](ResearchScreen.md), [SectorScreen](SectorScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 102 | THE BANK (0.7.33): THE FRAME |
| 174 | · one way to write each number |
| 208 | · the frame |
| 293 | · its statements, formal (0.7.74) |
| 548 | · the stance |
| 681 | · the action cards |
| 776 | · the screen's own pieces |
| 990 | THE OVERVIEW (0.7.33; the landing, "THE BANK AT A GLANCE", until then) |
| 1023 | · the capital gauge |
| 1106 | · the scorecard |
| 1180 | · the rate ladder |
| 1472 | · the pages behind it |
| 1543 | PROFIT |
| 1572 | · the waterfall |
| 1723 | · the four cards |
| 1775 | · the trading desk |
| 1889 | · the statement, under details |
| 1981 | · · and what it did with it |
| 1989 | · · the last twelve months |
| 2010 | · · as ratios |
| 2155 | BALANCE SHEET (0.7.13) |
| 2480 | · beside the sheet |
| 2519 | · its equity in parts |
| 2587 | · the statement, under details |
| 2598 | · · what it owns |
| 2624 | · · what it owes |
| 2639 | · · what is left |
| 2671 | · · beside the sheet |
| 2703 | LENDING |
| 2768 | · the book |
| 2818 | · the borrowers |
| 3043 | · set aside, written off |
| 3074 | · how the next loan is priced |
| 3125 | · the tables, under details |
| 3136 | · · the businesses |
| 3189 | · · who has stopped paying |
| 3233 | · · what it lent this month |
| 3249 | · · how the next loan is priced |
| 3302 | · · what the book weighs |
| 3332 | · · what concentration costs (0.7.12) |
| 3419 | FUNDING |
| 3537 | · the three cards |
| 3636 | · the branches |
| 3759 | · the lines, under details |
| 3765 | · · what is banked |
| 3772 | · · what it can lend against (0.7.19) |
| 3780 | · · what it pays savers |
| 3798 | · · its account at the central bank |
| 3813 | · · how it is funded |
| 3822 | · · what it can carry |
| 3844 | · · its branches |
| 3881 | CAPITAL & OWNERS |
| 3994 | · payout, and how its equity moved |
| 4079 | · rescues, and the city's preferred |
| 4147 | · the lines, under details |
| 4154 | · · its capital |
| 4168 | · · ...and against everything it has lent (0.7.11, round 2) |
| 4179 | · · what it does with its profit |
| 4200 | · · how its equity moved |
| 4239 | · · its owners |
| 4245 | · · its rescues |
| 4266 | · · the city's preferred |
| 4289 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 4351 | THE BANK ASKS FOR PREFERRED (0.7.14) |
| 4459 | HISTORY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 124 | `BankScreen.BANK_PAGES` | `"The bank's pages"` | null is the Overview; BANK_PAGES, the pages behind it |
| 126 | `BankScreen.BANK_HOME` | `"Profit"` | The page behind the Overview that is lit until the player picks another; the rail's bank icon resets to it. |
| 130 | `BankScreen.BANK_PAGE_NAMES` | `{ "Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "Histo...` | The six pages behind the Overview, in the chip strip's order: the balance sheet beside the income statement since 0.7.13. |
| 134 | `BankScreen.OVERVIEW` | `"Overview"` | The strip (0.7.33): the Overview first - it was the landing, with a button back to it at the foot of every page - then the six. |
| 136 | `BankScreen.CHIPS` | `{ OVERVIEW, "Profit", "Balance sheet", "Lending", "Funding", "Capital & owner...` | ...the strip's seven chips: the Overview, then BANK_PAGE_NAMES in their order. |
| 139 | `BankScreen.CHIP_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.SECTOR, Icons.BANK, Icons...` | ...and each chip's icon. |
| 164 | `BankScreen.FRAME_CHROME` | `190` | How much of the stage the fixed frame takes above the page's scroller - the head, the chips and, on a page, the status strip - until the frame is laid out and its own height is read (0.7.33). |
| 167 | `BankScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same... |
| 170 | `BankScreen.SET_ASIDE` | `"#setAside", PRICING = "#pricing", LANDLORDS = "#landlords", FAMILIES = "#fam...` | The scroll targets a door can land on: Lending's cards and its pricing, Funding's carry card, the details folds. |
| 476 | `BankScreen.BOUND_TO_FRAME` | `"bank.frame", BOUND_TO_SCROLLER = "bank.scroller"` | The key in a kept scroller's properties for the frame its height is bound to, and in a kept page's for the scroller its width is bound to (frameOver()). |
| 492 | `BankScreen.LEAD_INFO` | `"Every loan in the city is its money, priced from what it costs the bank to m...` | The tab's (i): the Overview's lead (the spec's L2), with what the Overview shows. |
| 499 | `BankScreen.PAGE_INFO` | `{ "Its income statement, and what it did with the profit: a walk from the int...` | Each page's (i), in BANK_PAGE_NAMES' order: what it holds (the landing's row blurbs until 0.7.33). |
| 1183 | `BankScreen.LADDER_INFO` | `"From the price of money to what each borrower pays.Every step up is a cost "...` | THE LADDER's (i): the landing's note (the spec's L10), with what the parts are. |
| 1578 | `BankScreen.NOTHING` | `.0005` | A step under half a dollar is nothing: it is left off the walk and named under it. |
| 1686 | `BankScreen.PROVISIONS_INFO` | `"A provision is money set aside for loans expected to go bad: a year's " + "e...` | The provisions' note (the spec's T, Profit 790), behind the step's tooltip and the statement's line. |
| 1726 | `BankScreen.RATIOS_INFO` | `"The margin is what it charges less what it pays for its money, on everything...` | The ratios' note (Profit 874), behind MARGIN's and COSTS' (i). |
| 2198 | `BankScreen.OWNS` | `List.of(new SheetPart(Bank.Sheet.BUSINESS_LOANS, "Loans to the businesses", P...` | What it owns, in the bars' order (the spec's section 3): the businesses' loans first, its reserves last; the allowance is the net tick. |
| 2210 | `BankScreen.OWES` | `List.of(new SheetPart(Bank.Sheet.DEPOSIT_FUNDING, "Lent past its own cash, on...` | ...and what it owes, then its owners' (equity last). |
| 2483 | `BankScreen.BESIDE_INFO` | `"Counted for what it can lend and not held on its sheet in full: the families...` | BESIDE THE SHEET's (i) (Balance sheet 1161). |
| 2793 | `BankScreen.BOOK_INFO` | `"Everything it has lent, by who owes it: the businesses' loans and the bonds ...` | Who-owes-it note (Lending 1225), behind THE BOOK's (i). |
| 3454 | `BankScreen.LEND_AGAINST_INFO` | `"Savings reach the bank wherever its branches are - online - so it lends " + ...` | What it can lend against (Funding 1681), behind the banked bar's (i). |
| 3931 | `BankScreen.STAKE_INFO` | `"A resolution makes every share the city's, in its fund's rescue book, which ...` | The city's stake's note (Capital 2002). |
| 4026 | `BankScreen.MOVED_INFO` | `"Equity moves by what it earned, what it was given and what it paid out, and ...` | The equity's note (Capital 1990): Jerus's rule on a plug. |
| 4082 | `BankScreen.RESCUES_INFO` | `"When a bank loses more than it owns, the city resolves it: the old owners lo...` | The rescues' note (Capital 2027). |
| 4308 | `BankScreen.RESCUE_INFO` | `"Its owners lose everything: the households' shares and the world's pass to t...` | The rescue's note (Rescue 2119). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 90 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 95 | `final Kept.Pool pool` | The nodes this screen made at its last draw, handed back to it at the next (KP5; study UP4, docs/studies/spec-screens-in-place.md). |
| 123 | `String bankArea` | null is the Overview; BANK_PAGES, the pages behind it |
| 127 | `String bankPage` |  |
| 143 | `final Set<String> openLines` | What the player has opened - a statement's line, a borrower's sectors, a "details" fold - by key, so a redraw on the clock leaves it open (Statement.opens()). |
| 146 | `boolean profitYear` | The Profit page's waterfall: the last twelve months rather than this month (0.7.33), kept while the tab is. |
| 149 | `private String scrollTarget` | Where the page is to be scrolled to once it is drawn - a card's key - or null for its top. |
| 152 | `private final java.util.Map<String, Node> targets` | The nodes a door on this tab can scroll to, by key, as the page draws them. |
| 155 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 158 | `private final ChartModel historyWindow` | History's window, shared by its charts and kept across the month's redraw, for a chart dragged back to stay where it was put - though no chart here takes a drag (a small TimeChart does not pan; 0.7.38 took "Drag to lo... |
| 161 | `private int drawnMonth` | The month the tab last drew: a new month landing is when its figures pop and its gauge grows. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 4570 | **type** `final class BankScreen` | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figures and the ladder of its rates drawn in their parts - with its profit... |
| 92 | 1 | `BankScreen(UserInterface ui)` |  |
| 98 | 3 | `void forget()` | Every node is made new at the next draw - the History charts with them: another city opened (UserInterface.anotherCity()). |

### THE BANK (0.7.33): THE FRAME (lines 102-173)

### one way to write each number (lines 174-207)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 6 | `static String defaultShare(double pd)` | A default rate, which runs from the curve's far tail to all of it: "under 0.1%" rather than a "0.0%" that reads as none, "x.x%" to 99.9%, then "all". |
| 185 | 4 | `static String yearWords(Bank bank)` | "the last 12 months", or as many as the bank has lived. |
| 191 | 1 | `static String pts(double spread)` | A spread for a chip: "+2.06 pts", "−0.22 pts" - Money.points() shortened. |
| 194 | 1 | `static String mv(double thousands)` | Money signed for a movement, with a true minus: "+$851,963", "−$81,238", "$0" (Money.signed(), never negated). |
| 197 | 4 | `static String m(double thousands)` | Money with a true minus when below nothing, abbreviated: "−$2.1M". |
| 203 | 4 | `static String mFull(double thousands)` | ...with every digit. |

### the frame (lines 208-292)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 16 | `void showBankMenu()` | The tab's entry point: the Overview, or the page behind it the player was on. |
| 239 | 45 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck - the Overview, or the page `bankPage` names, in the view... |
| 286 | 6 | `void pickView(boolean statement)` | The switch picked (0.7.74): this page in the other view, at its top - one choice with the sector pages' (D1). |

### its statements, formal (0.7.74) (lines 293-547)

| line | len | member | says |
|---:|---:|---|---|
| 307 | 18 | `void profitStatementView(VBox page)` | The bank's income statement, formal, its ratios beside it. |
| 327 | 20 | `void sheetStatementView(VBox page)` | The bank's balance sheet, formal, against a year ago, its ratios beside it. |
| 349 | 4 | `static Bank.Sheet sheetLineOf(String id)` | The sheet line a statement row is, by its id ("bs.RESERVES"), or null for a subtotal. |
| 361 | 48 | `VBox bankRatios(Bank bank, boolean sheet)` | Its ratios (spec 4.5), on the income statement: net interest margin, cost to income, the fees' share, provisions over what it has lent, return on equity and its payout; on the sheet: its capital over its weighted book... |
| 411 | 4 | `void pick(String name)` | A chip picked: the Overview, or a page at its top. |
| 417 | 3 | `void openPage(String page)` | Opens one page behind the Overview, at its top - the Overview's cards, and the inbox's "defaults" notice (0.7.8). |
| 422 | 8 | `void open(String page, String target)` | ...scrolled to a card on it (0.7.33: the doors land where the line is decided). |
| 432 | 6 | `void openOverview()` | The Overview, at its top. |
| 444 | 3 | `private void toTop()` | The page on show scrolled to its top (KP5): a chip, the switch or a door picked draws its page from the top, and another page is a scope of its own with a scroller of its own, which opens there; but the page the pick ... |
| 449 | 25 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (FinancesScreen's, 0.7.32). |
| 479 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 514 | 4 | `static String pageInfo(String page)` | A page's (i) by its name. |
| 525 | 7 | `HBox head()` | The head: "Bank" with the money blue's swatch, or the breadcrumb "Bank › Profit", the first word a way back to the Overview; at its right the two doors - the policy rate (Policy › Money › The policy rate) and the trea... |
| 534 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (FinancesScreen.openPolicy()'s). |
| 543 | 4 | `void openSector(String key, String page)` | One sector's books, on one of its pages (Sectors › it › Cash & debt). |

### the stance (lines 548-680)

| line | len | member | says |
|---:|---:|---|---|
| 551 | 8 | `static String stanceTone(Bank bank)` | Good at or over its target, a warning while it rebuilds, bad under the minimum or failed. |
| 561 | 3 | `boolean bankNotOpenYet(Bank bank)` | A Commercial Bank stands that the first month has not opened yet (PT1): Bank itself still counts no branch. |
| 566 | 3 | `String stanceNow(Bank bank)` | The stance's chip, "NOT OPEN YET" while a standing bank waits for the first month, else stanceWord(). |
| 571 | 6 | `String statusNow(Bank bank)` | The overview's sentence under the stance, the same swap. |
| 579 | 9 | `static String stanceWord(Bank bank)` | The stance in a word or three, on its chip (the spec's D18). |
| 595 | 31 | `HBox statusStrip(boolean fresh)` | THE STATUS STRIP, on every page behind the Overview: the stance as a chip and the four figures across the top of every page until 0.7.33 - profit, the binding capital ratio, prime with its move on last month, and what... |
| 631 | 6 | `static void restPip(Region pip)` | A figure that does not pop at this draw (KP5): the pop the last draw gave it stopped, and the figure at rest - a pop stopped half way leaves it half grown. |
| 639 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of a strip or the scorecard, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 642 | 16 | `List<Cell> stripCells()` | The status strip's four (pure: the probe reads them as the strip shows them). |
| 660 | 5 | `static String capitalFigure(Bank bank)` | The binding ratio as the gauge and the strip write it: "failed", "—" with nothing lent, else "14.1%". |
| 667 | 13 | `String primeChange()` | PRIME's move on last month, from History's last two points: "▲ 0.25 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). |

### the action cards (lines 681-775)

| line | len | member | says |
|---:|---:|---|---|
| 690 | 63 | `List<Node> actionCards()` | What the player must act on or know of first, at the top of every page: no bank, or one that has not opened yet; a failed bank and its rescue; a bank under its minimum; the bank asking the city for preferred; money fr... |
| 755 | 13 | `static VBox actionCard(String svg, String tone, String title, String line, String whole, Node act)` | One action card: its icon square, its title, one line with the whole behind an (i), and what to press (null: nothing), on the raised ground with a 3 px top edge in `tone`. |
| 770 | 5 | `static String actionStyle(String tone)` | An action card's ground: the raised ground, its top edge 3 px in the verdict's colour. |

### the screen's own pieces (lines 776-989)

| line | len | member | says |
|---:|---:|---|---|
| 779 | 3 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 784 | 7 | `static Label words(String text, double size, String tone, boolean hand)` | ...with the hand when it is a door (KP2b: made so, not made without it and set again). |
| 793 | 7 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 802 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - LandScreen.worthCard()'s head, the spec's info card. |
| 821 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 830 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 840 | 12 | `static VBox doorCard(Runnable go, Node...rows)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 854 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 865 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 872 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 877 | 3 | `static Label statementNote(String text)` | A statement's note in a scope of its own (KP5): a note drawn only sometimes, or a table's rows in a number that changes, shifts no label after it. |
| 882 | 3 | `static Label sentence(String text, String tone)` | A paragraph in the statement column, in a scope of its own for the same reason. |
| 887 | 1 | `static Label line(String text)` | A plain line of words in a card, wrapping. |
| 890 | 1 | `static Label muted(String text)` | ...muted. |
| 893 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut (Pieces.hint() at its own width). |
| 900 | 1 | `static Label big(String text, String tone)` | A big figure on a card, in a colour. |
| 903 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width (the spec's D15). |
| 911 | 4 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 917 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 931 | 3 | `static HBox swatch(String colour, String name, boolean ghost)` | A key's entry: a swatch and a name, the swatch outlined when `ghost`. |
| 936 | 5 | `static javafx.scene.layout.FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 943 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 948 | 3 | `static Segment seg(double amount, String colour, String label, String tip)` | ...with a label in it, drawn where it fits. |
| 953 | 3 | `static Segment ghost(double amount, String colour, String tip)` | ...hollow: what a rate is built on, or a quote nobody pays. |
| 958 | 3 | `static Tick tick(double at, String colour, String name, String tip)` | A named mark across a bar. |
| 963 | 10 | `static Label chipTip(String text, String colour, String tip)` | A chip with a tooltip. |
| 975 | 9 | `Region spark(String series, int n, String colour, double width, double height)` | The last `n` months of a History series, for a sparkline. |
| 986 | 3 | `static String partTip(String name, double amount, double whole)` | A segment bar's tooltip line: "name  $X  (12.3%)". |

### THE OVERVIEW (0.7.33; the landing, "THE BANK AT A GLANCE", until then) (lines 990-1022)

| line | len | member | says |
|---:|---:|---|---|
| 1004 | 18 | `void overviewPage(VBox page, boolean fresh)` |  |

### the capital gauge (lines 1023-1105)

| line | len | member | says |
|---:|---:|---|---|
| 1026 | 17 | `static String capitalTip(Bank bank)` | The capital gauge's tooltip: both measures, the one that binds named (the old capital band's, since 0.7.11 round 2). |
| 1045 | 2 | **type** `record Gauge(String caption, String figure, String tone, String line, String key, String other, double valu...` | The gauge's words, worked out without drawing it (pure: the probe reads them). |
| 1049 | 23 | `Gauge gauge()` | The Overview's gauge: the measure that binds, as status() reads it (the spec's D4). |
| 1081 | 24 | `VBox capitalCard(Bank bank, boolean fresh)` | THE CAPITAL CARD: the ratio that binds at 28 px in its stance's colour, what it is of and its target, the bar in its band with the minimum, the target and the top marked (Pieces.bandBar()), the other measure in one mu... |

### the scorecard (lines 1106-1179)

| line | len | member | says |
|---:|---:|---|---|
| 1116 | 38 | `List<Cell> scorecardCells(List<CityNeeds.Need> all)` | The eight figures (pure: the probe reads them as the scorecard shows them): what it earned, what that returns its owners, its losses and HOW FULL it is (0.7.33: Bank.strain(), worded and coloured as NEEDS YOU's THE BA... |
| 1156 | 23 | `VBox scorecard(List<CityNeeds.Need> all, boolean fresh)` | The scorecard: the eight as two rows of four, each a door. |

### the rate ladder (lines 1180-1471)

| line | len | member | says |
|---:|---:|---|---|
| 1191 | 21 | `VBox ladderCard()` | The ladder's card: its head, its key, the rungs. |
| 1214 | 12 | `static String saversWhy(Bank.Ladder l)` | Why savers get what they get: the share its funding asks for, and whether its margin held them under it. |
| 1228 | 8 | `List<String> owing()` | The sectors that owe the bank or are shut out of it, in the registry's order. |
| 1238 | 1 | `String sectorIcon(String key)` | A sector's icon, by its key (Icons.ofSector()). |
| 1255 | 60 | `List<Rung> rungs()` | THE LADDER'S RUNGS (pure: the probe reads them), each drawn in its parts - the spec's D2 and D3. |
| 1323 | 83 | `List<Rung> borrowerRungs(Bank.Ladder l, boolean all)` | What each borrower pays (pure): the businesses that owe it or are shut out, each with its bonds under it when `all`; the families; the carry trade; an insured mortgage; and, when `all`, the city's paper. |
| 1408 | 38 | `Rung sectorRung(String name, Bank.Ladder l, BusinessDebtManager credit)` | A sector's rung: prime outlined, then its own risk, its record and the book's concentration (the spec's D2; B4 named the concentration). |
| 1448 | 23 | `Rung bondRung(String name, double y, Bank bank, BusinessDebtManager credit, BondMarket market)` | A sector's bond: what the bank would hold one at, over its loan's rate, a tick at what its bonds pay (BondMarket.bankYield(), 0.7.12). |

### the pages behind it (lines 1472-1542)

| line | len | member | says |
|---:|---:|---|---|
| 1475 | 1 | **type** `record PageCard(String name, String icon, String blurb, String figure, String sub, String tone, String series)` | One page as a card on the Overview, worked out without drawing it (pure: the probe reads them). |
| 1478 | 32 | `List<PageCard> pageCards()` | The six (the landing's rows until 0.7.33, the spec's L16): what each holds and its headline. |
| 1512 | 13 | `VBox pagesBehind()` | THE PAGES BEHIND IT: the six as cards, three to a row, each with a year of its line. |
| 1527 | 15 | `private VBox pageCard(PageCard p)` | One of the six, as a card on the Overview: its name, what it holds, its headline and a year of its line; a click opens it. |

### PROFIT (lines 1543-1571)

| line | len | member | says |
|---:|---:|---|---|
| 1558 | 13 | `void profitPage(VBox page)` |  |

### the waterfall (lines 1572-1722)

| line | len | member | says |
|---:|---:|---|---|
| 1575 | 1 | **type** `record Walk(List<Step> steps, List<String> nothing)` | The waterfall worked out without drawing it (pure: the probe reads it): its steps, and the lines at nothing named in a line under it. |
| 1589 | 64 | `Walk walk(boolean year)` | FROM WHAT IT EARNED TO WHAT IT KEPT (pure): the income statement's lines as steps, this month or over the year (Bank.thisMonth(), overYear()); the four totals are the model's own (NET_INTEREST, PRE_TAX, NET, RETAINED)... |
| 1655 | 4 | `void step(List<Step> steps, List<String> nothing, String name, double amount, String icon, List<Slice> parts, String tip)` | A plain step of the walk: up in the money blue, down in its dark step; at nothing, named instead. |
| 1661 | 11 | `void step(List<Step> steps, List<String> nothing, String name, double amount, String icon, List<Slice> parts, String tip, Runna...` | ...with a door of its own (null: the statement under "details"). |
| 1674 | 3 | `void total(List<Step> steps, String name, double amount)` | A total of the walk, the model's own figure, drawn from zero: neutral, red under nothing. |
| 1679 | 5 | `void openStatement()` | The statement under "details", opened and scrolled to. |
| 1690 | 32 | `VBox waterfallCard(Bank bank)` | The waterfall's card: its head with the toggle, the walk, and what was nothing. |

### the four cards (lines 1723-1774)

| line | len | member | says |
|---:|---:|---|---|
| 1729 | 7 | `VBox marginCard(Bank bank)` |  |
| 1737 | 12 | `VBox costsCard(Bank bank)` |  |
| 1750 | 13 | `VBox roeCard(Bank bank)` |  |
| 1764 | 10 | `VBox yearCard(Bank bank)` |  |

### the trading desk (lines 1775-1888)

| line | len | member | says |
|---:|---:|---|---|
| 1778 | 6 | `boolean deskShows(Bank bank)` | Whether the desk card shows: it holds something, or traded or was paid this month. |
| 1793 | 66 | `VBox deskCard(Bank bank)` | THE TRADING DESK as a card (0.7.33): what it sold and bought and from whom, what it was paid, its re-mark and what it holds; its book and its largest holding against their caps (Exchange.BOOK_LIMIT and POSITION_LIMIT ... |
| 1861 | 27 | `String deskInfo(Bank bank)` | The desk card's (i): its orders on the book and, when it lost on the re-mark, why (Profit 968-997). |

### the statement, under details (lines 1889-2154)

| line | len | member | says |
|---:|---:|---|---|
| 1898 | 133 | `VBox profitStatement(Bank bank)` | The income statement as the page printed it until 0.7.33, verbatim (the spec's R1-R12, D15): this month against last, every line opening into what it is made of, then what it did with it, the last twelve months, and t... |
| 2033 | 4 | `HBox line(Bank bank, String label, Bank.Line which, boolean known, boolean out)` | One statement line, this month and last - negated for money going out. |
| 2039 | 4 | `VBox total(Bank bank, String label, Bank.Line which, boolean known)` | One total, this month and last. |
| 2045 | 4 | `void year(VBox column, Bank bank, String label, Bank.Line which, boolean out)` | One line of the year, negated for money going out. |
| 2059 | 95 | `VBox deskDetail(Bank bank)` | THE TRADING DESK, opened (2026-09-18, and so it foots). |

### BALANCE SHEET (0.7.13) (lines 2155-2479)

| line | len | member | says |
|---:|---:|---|---|
| 2182 | 11 | `void sheetPage(VBox page)` |  |
| 2195 | 1 | **type** `record SheetPart(Bank.Sheet line, String name, String colour, boolean ghost)` | One line of the sheet as the bars and the key draw it: the line, its name, its colour, whether it is outlined. |
| 2219 | 2 | **type** `record SheetBars(List<Segment> owns, List<Tick> ownsTicks, List<Segment> owes, List<Tick> owesTicks, double...` | The two bars worked out without drawing them (pure: the probe reads them): their segments and ticks, their words, and the one scale. |
| 2223 | 60 | `SheetBars sheetSegments()` | WHAT IT OWNS · WHAT IT OWES, AND WHAT IS ITS OWNERS' (pure; the spec's D6). |
| 2285 | 8 | `String sheetInfo(Bank bank)` | The hero's (i): what the two bars are, and the year ago's note when it is not on file (the spec's B7). |
| 2295 | 28 | `VBox sheetHero(Bank bank)` | The hero card: the two bars and, under them, every line in two columns. |
| 2329 | 8 | `static Label moveFigure(String words)` | A line's move as the key writes it, at the right of its figure. |
| 2339 | 6 | `static String moved(double now, double then, boolean known)` | A line's move on a year ago: "▲ $1.2M", "▼ $3.4M", nothing when none is on file or it did not move. |
| 2347 | 39 | `Node legendRow(Bank bank, SheetPart p, boolean known)` | One line of the key: its swatch, its name with its sentence behind an (i), its figure now and its move on a year ago; the loans and the interim financing open by sector in place. |
| 2388 | 11 | `Node legendTotal(String label, double now, double then, boolean known)` | A total under a column of the key. |
| 2401 | 21 | `VBox bySector(Bank bank, Bank.Sheet line, boolean known)` | The loans or the interim financing by sector, this month and a year ago - null for a line that does not open. |
| 2424 | 55 | `String sheetSaid(Bank bank, Bank.Sheet line)` | Each line's sentence (the statement's said()s, Balance sheet 1029-1130): the key's (i)s and the statement's openings. |

### beside the sheet (lines 2480-2518)

| line | len | member | says |
|---:|---:|---|---|
| 2489 | 29 | `VBox besideTheSheet(Bank bank)` | BESIDE THE SHEET: the city's own deposits as one bar, what it has lent on them, and how big they are against the sheet. |

### its equity in parts (lines 2519-2586)

| line | len | member | says |
|---:|---:|---|---|
| 2522 | 57 | `VBox equityParts(Bank bank)` | ITS EQUITY IN PARTS: the city's preferred, paid in and retained, now and a year ago, on one scale; a part below nothing drawn leftward in the spent grey. |
| 2581 | 5 | `private static Pieces.RankBar rank(double amount, double pos, double neg, String colour)` | A bar of the equity's parts on the card's one scale, kept: told its figures. |

### the statement, under details (lines 2587-2702)

| line | len | member | says |
|---:|---:|---|---|
| 2590 | 98 | `VBox sheetStatement(Bank bank)` | The balance sheet as the page printed it until 0.7.33, verbatim (the spec's B1-B7, D15). |
| 2690 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail)` | One line of the balance sheet, this month and a year ago, opening into its detail. |
| 2694 | 3 | `VBox sheetLine(Bank bank, String label, Bank.Sheet line, boolean known, VBox detail, String word)` |  |
| 2699 | 3 | `static VBox said(String text)` | A line's detail that is a sentence. |

### LENDING (lines 2703-2767)

| line | len | member | says |
|---:|---:|---|---|
| 2721 | 20 | `void lendingPage(VBox page)` |  |
| 2743 | 7 | `String stanceInfo(Bank bank)` | The capital-lets-it-lend note (Lending 1449), behind the stance chip's (i). |
| 2752 | 15 | `HBox stanceRow(Bank bank)` | What its capital lets it lend: Bank.lendingStance() as a chip and its line, and the growth it allows when it is finite. |

### the book (lines 2768-2817)

| line | len | member | says |
|---:|---:|---|---|
| 2771 | 1 | **type** `record BookPart(String name, double amount, String colour)` | One borrower's part of the book (pure). |
| 2774 | 17 | `List<BookPart> bookParts()` | THE BOOK by borrower (pure): the sheet's loan lines, the bonds among them, so they foot to Bank.getBook() (B3). |
| 2799 | 18 | `VBox bookCard(Bank bank)` |  |

### the borrowers (lines 2818-3042)

| line | len | member | says |
|---:|---:|---|---|
| 2821 | 3 | **type** `record Borrower(String key, boolean shut, int blockedMonths, double rate, double spread, double owesBank, d...` | One business as its card shows it (pure: the probe reads them). |
| 2826 | 21 | `List<Borrower> borrowers()` | The businesses the old table listed (Lending 1243): owing, shut out, set aside against, or written off this month. |
| 2849 | 20 | `static String borrowersInfo()` | The businesses' note (Lending 1280): its short line on the section's head, and the whole behind its (i). |
| 2871 | 32 | `VBox borrowersSection(Bank bank)` | THE BORROWERS: a card a business, then the landlords, the families and the carry trade, three to a row. |
| 2905 | 63 | `VBox borrowerCard(Borrower b)` | One business as a card: its rate over prime, what it owes the bank and of its bonds, its leverage on its scale, its default rate, its chips; a click opens its Cash & debt page. |
| 2970 | 36 | `VBox landlordsCard(Bank bank)` | THE LANDLORDS: their insured mortgages (Lending 1309-1344), part of what the businesses owe. |
| 3008 | 20 | `VBox familiesCard(Bank bank)` | THE FAMILIES: their credit lines (Lending 1347-1369), shown while they owe or something is set aside. |
| 3030 | 12 | `VBox carryCard(Bank bank)` | THE CARRY TRADE: what foreigners have borrowed here to hold abroad (the Overview's rung, Balance sheet's line). |

### set aside, written off (lines 3043-3073)

| line | len | member | says |
|---:|---:|---|---|
| 3045 | 16 | `VBox setAsideCard(Bank bank)` |  |
| 3062 | 11 | `VBox writtenOffCard(Bank bank)` |  |

### how the next loan is priced (lines 3074-3124)

| line | len | member | says |
|---:|---:|---|---|
| 3077 | 20 | `String pricingInfo(Bank bank)` | Prime's note and the quotes' note (Lending 1470, 1515), behind HOW THE NEXT LOAN IS PRICED's (i). |
| 3099 | 25 | `VBox pricingCard(Bank bank)` | HOW THE NEXT LOAN IS PRICED: prime as its four parts, each named, then a row a borrower. |

### the tables, under details (lines 3125-3418)

| line | len | member | says |
|---:|---:|---|---|
| 3132 | 235 | `VBox lendingTables(Bank bank)` | The old page's tables and lines, verbatim (the spec's N2, N6, N7, N9, N11; D15), with B9's fix - the businesses' first column is what each owes THE BANK - and B2's: no green or amber on what is not a verdict. |
| 3369 | 12 | `static String bookName(Bank.Book book)` | What the weight table calls each book. |
| 3383 | 35 | `VBox bondsHeld(Bank bank)` | THE BUSINESSES' BONDS IT HOLDS (0.7.12), verbatim, under their own fold - the Overview's bond rungs open it. |

### FUNDING (lines 3419-3536)

| line | len | member | says |
|---:|---:|---|---|
| 3437 | 15 | `void fundingPage(VBox page)` |  |
| 3459 | 2 | **type** `record FundingBars(List<Segment> banked, List<Tick> bankedTicks, List<Segment> funds, String bankedWords, S...` | The two bars worked out without drawing them (pure: the probe reads them). |
| 3463 | 33 | `FundingBars fundingBars()` | WHAT THE CITY HAS BANKED and WHAT FUNDS ITS BOOK (pure), each on its own scale. |
| 3498 | 26 | `VBox fundingHero(Bank bank, Bank.Ladder l)` | The hero: the two bars, each with its words at the left and its key under it. |
| 3526 | 10 | `static HBox fundingRow(String words, Node bar)` | One of the hero's rows: its words in a column at the left, the bar taking the rest. |

### the three cards (lines 3537-3635)

| line | len | member | says |
|---:|---:|---|---|
| 3540 | 6 | `static String saversShareInfo()` | What savers are paid's note (Funding 1712). |
| 3548 | 21 | `VBox saversCard(Bank bank, Bank.Ladder l)` | SAVERS: the policy rate, the share its funding asks it to pass on, the rate it chose and what it paid - a ladder of four - and what that paid this month. |
| 3571 | 5 | `static VBox saversStep(String what, String figure, String under)` | One step of the savers' ladder: its words over its figure. |
| 3578 | 14 | `VBox centralBankCard(Bank bank, Bank.Ladder l)` | THE CENTRAL BANK: its reserves there and what they earned, and what it borrowed overnight and what that cost - red while it borrows. |
| 3594 | 32 | `VBox canCarryCard(Bank bank)` | WHAT IT CAN CARRY: what its capital carries and what its deposits carry on one scale, the tighter marked BINDS, its weighed book against them, and how full that is. |
| 3628 | 7 | `static VBox carryLine(String words, double amount, double scale, boolean binds, List<Tick> ticks)` | One of what it can carry's two bars: its words, the bar on the card's scale with the book's weight ticked, and BINDS on the tighter. |

### the branches (lines 3636-3758)

| line | len | member | says |
|---:|---:|---|---|
| 3639 | 1 | **type** `record Verdict(String words, String tone, boolean build)` | The branch verdict worked out without drawing it (pure: the probe reads it, BankCheck asserts the planner it reads). |
| 3648 | 8 | `Verdict branchVerdict()` | WOULD ANOTHER ONE PAY? |
| 3658 | 14 | `String branchesInfo()` | The branches' notes (Funding 1801, 1848): a branch's cost, and the test run the other way. |
| 3680 | 68 | `VBox branchesCard(Bank bank)` | ITS BRANCHES, BY THEIR CUSTOMERS (0.7.19) as a card: the customers a branch against what one serves as a ring; a branch's fees against what one past the first costs; the two questions, the investors' verdict, whether ... |
| 3750 | 8 | `static HBox question(String words, boolean yes)` | One of the branch questions: a tick or a cross, and the words. |

### the lines, under details (lines 3759-3880)

| line | len | member | says |
|---:|---:|---|---|
| 3762 | 118 | `VBox fundingLines(Bank bank, Bank.Ladder l)` | The Funding page's lines as it printed them until 0.7.33, verbatim (the spec's F1-F7), with B2's colours and B6's verdict. |

### CAPITAL & OWNERS (lines 3881-3993)

| line | len | member | says |
|---:|---:|---|---|
| 3897 | 32 | `void capitalPage(VBox page)` |  |
| 3937 | 4 | `static String bandInfo()` | The band's note (Capital 1896). |
| 3943 | 8 | `static String leverageInfo(Bank bank)` | The leverage measure's note (Capital 1910). |
| 3953 | 25 | `Gauge measureGauge(boolean leverage)` | One of the two gauges, worked out without drawing it (pure: the probe reads them): the risk-weighted ratio, or the leverage ratio when `leverage`. |
| 3980 | 13 | `VBox gaugeCard(Bank bank, boolean leverage)` | A gauge's card: its caption with BINDS on the larger requirement, the ratio, the bar in its band, the key, what it is of, and why the target is where it is. |

### payout, and how its equity moved (lines 3994-4078)

| line | len | member | says |
|---:|---:|---|---|
| 3997 | 7 | `static String payoutInfo()` | The payout rule (Capital 1948). |
| 4006 | 18 | `VBox payoutCard(Bank bank)` | PAYOUT: its decision; last month's profit after tax with what it paid and bought back on it; what it held over its target and the top; the year's. |
| 4030 | 30 | `List<Step> equitySteps()` | HOW ITS EQUITY MOVED, worked out without drawing it (pure): each cause as a step, at nothing left off, "Not accounted for" always. |
| 4061 | 17 | `VBox equityMovedCard(Bank bank)` |  |

### rescues, and the city's preferred (lines 4079-4146)

| line | len | member | says |
|---:|---:|---|---|
| 4087 | 22 | `VBox rescuesCard(Bank bank)` |  |
| 4111 | 6 | `Label bankDecisionChip()` | The bank's first decision this month, in the order they were made - a rescue, the preferred offer - as a chip (the spec's section 5), or null. |
| 4119 | 5 | `static boolean preferredHasHistory(Bank bank)` | Whether the city's preferred has any history to show. |
| 4126 | 7 | `static String preferredInfo()` | The preferred's note (Capital 2046). |
| 4134 | 12 | `VBox preferredCard(Bank bank)` |  |

### the lines, under details (lines 4147-4288)

| line | len | member | says |
|---:|---:|---|---|
| 4150 | 132 | `VBox capitalLines(Bank bank)` | The Capital & owners page's lines as it printed them until 0.7.33, verbatim (the spec's C1-C8), with B2's colours. |
| 4284 | 4 | `void moved(VBox column, String label, double amount)` | One cause of the equity's movement, printed only when it moved it - plain since 0.7.33 (B2: it was green in and amber out). |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 4289-4350)

| line | len | member | says |
|---:|---:|---|---|
| 4314 | 31 | `VBox bankRescue()` |  |
| 4347 | 3 | `static VBox rescueFigure(String caption, String value, String tone)` | One of the rescue's four figures: its caption over it. |

### THE BANK ASKS FOR PREFERRED (0.7.14) (lines 4351-4458)

| line | len | member | says |
|---:|---:|---|---|
| 4365 | 93 | `VBox preferredOffer()` |  |

### HISTORY (lines 4459-4656)

| line | len | member | says |
|---:|---:|---|---|
| 4489 | 111 | `void historyPage(VBox page)` |  |
| 4602 | 4 | `TimeChart.Line percentLine(HistorySave h, String key, String name, String colour)` | A line of a History series in per cent, read out as a rate. |
| 4608 | 4 | `TimeChart.Line moneyLine(HistorySave h, String key, String name, String colour)` | ...in money. |
| 4614 | 6 | `static TimeChart.Line flatLine(HistorySave h, String key, String name, double level, String colour, String unit)` | A reference line at one level for every month: what its owners want, the 100% line. |
| 4626 | 9 | `TimeChart chart(HistorySave h, List<TimeChart.Line> lines, String unit, List<YearBook.Band> bands, List<ChartModel.Flag> flags,...` | One of the charts, on the tab's window (it follows `leader` when there is one), without controls. |
| 4637 | 18 | `VBox smallCard(String title, String info, String unit, TimeChart leader, HistorySave h, List<YearBook.Band> bands, List<TimeCha...` | A small chart's card: its title and (i), the chart following the rates' window, its key, and its statistics as chips - made in the card's own scope, which is keyed by its title. |


# GovernmentScreen.java - 2,128 lines · 99 methods · 30 constants · interface

`ham/citybuildersim/ui/GovernmentScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The government tab: where the city's money came from and went, the road
> from what it EARNED through the budget's SURPLUS to what the cash BANKED,
> who pays every line and where each is decided, what the city owes and has
> promised, and what its economy makes - four pages under five figures.
> 
> WHY, and why it looks like this (0.7.31). Split out of UserInterface on
> 2026-09-18 with its banners verbatim, it was a 560 px statement column
> of rows and two dozen paragraphs; Jerus, on the screens not yet redone:
> "the others are still full of text and the design could be more intuitive
> and fun", and on the header: "the money one has is barely visible to see
> as well as ones income" - where "+$1.5B a month" stood by the cash while
> the cash grew $2.3B and no screen said why. Redrawn in Build's style (the
> project's spec-government-0731.md): the two rings - the walkthrough's
> keeper - with the balance between them; the three figures named the same
> way everywhere, EARNED, SURPLUS and BANKED, and the steps between them as
> one card the model reads end to end (Game.getEarnedToBudget() and the
> treasury's bridge); Revenue and Spending as ranked bars that open into who
> pays, each with a door to where the line is decided; the debt, the
> services that charge and the pensions as cards; Output led by History's
> GDP layers. The old statements are behind "details" and the paragraphs
> behind an (i). The rail opens showGovernmentMenu(); open() is every other
> door in.

**Uses:** [Palette](Palette.md) (183), [Icons](Icons.md) (45), [NationalAccounts](NationalAccounts.md) (25), [CityNeeds](CityNeeds.md) (24), [CareType](CareType.md) (16), [EconomyManager](EconomyManager.md) (11), [HistoryScreen](HistoryScreen.md) (8), [HouseholdAccounts](HouseholdAccounts.md) (6), [EducationType](EducationType.md) (6), [BuildScreen](BuildScreen.md) (4), [TimeChart](TimeChart.md) (4), [ServicesScreen](ServicesScreen.md) (3), [TreasuryJournal](TreasuryJournal.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Sector](Sector.md) (3), [UserInterface](UserInterface.md) (2), [PolicyScreen](PolicyScreen.md) (2), [HistorySave](HistorySave.md) (2), [CentralBank](CentralBank.md) (2), [TreasuryLine](TreasuryLine.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingManager](BuildingManager.md) (2), [SummaryScreen](SummaryScreen.md) (1), [FinancesScreen](FinancesScreen.md) (1), [Game](Game.md) (1), [CityCalendar](CityCalendar.md) (1), [SectorBooks](SectorBooks.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1), [Mortgage](Mortgage.md) (1)... and 5 more

**Used by (2):** [FinancesScreen](FinancesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 52 | THE GOVERNMENT. |
| 235 | · THE FIVE FIGURES (0.7.31; four until then) |
| 357 | · the screen's own pieces |
| 475 | · the doors (D15) |
| 561 | THE OVERVIEW (0.7.31) |
| 592 | · the rings and the balance |
| 780 | · from EARNED to BANKED |
| 970 | · the central bank |
| 1020 | · against the economy |
| 1073 | THE TWO LISTS (0.7.31: ranked bars). |
| 1283 | · who pays what |
| 1475 | · what it spends |
| 1515 | WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON |
| 1612 | · · and the money that is not on this statement |
| 1657 | REVENUE AND SPENDING (0.7.31) |
| 1722 | · the three cards |
| 1914 | THE OUTPUT (0.7.31) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 93 | `GovernmentScreen.GOV_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.REPORTS }` | Each page's icon on its chip (0.7.31). |
| 116 | `GovernmentScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with SURPLUS's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.31). |
| 119 | `GovernmentScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - SectorScreen's, for the same fra... |
| 122 | `GovernmentScreen.BRIDGE` | `"#bridge", BALANCE = "#balance"` | The scroll targets on the Overview: the bridge card and the balance. |
| 218 | `GovernmentScreen.HEAD_INFO` | `"What the city took in and paid out last month, and what its money did." + "E...` | The tab's (i): what the five figures are, in a breath. |
| 676 | `GovernmentScreen.SURPLUS_INFO` | `"It took in more than it spent.A surplus pays down debt or buys the next " + ...` | P1's rest: what a surplus is for. |
| 680 | `GovernmentScreen.DEFICIT_INFO` | `"It spent more than it took in.That gap is borrowed, and next month's " + "in...` | ...and what a deficit costs. |
| 783 | `GovernmentScreen.BRIDGE_INFO` | `"EARNED is the header's figure: taxes and fees less the running programmes " ...` | The bridge card's (i): P2, rewritten, and P3. |
| 796 | `GovernmentScreen.DIALS_INFO` | `"EARNED is read at today's tax rates; the budget was struck at the month's." ...` | The dials' step's tooltip (the spec's B7). |
| 801 | `GovernmentScreen.RESIDUAL_INFO` | `"What the named steps do not explain: timing between the books and the money,...` | The "not accounted for" step's tooltip. |
| 982 | `GovernmentScreen.ARREARS_INFO` | `"What the ceiling cut: owed, with no interest on it, and paid down out of the...` | P6: the arrears. |
| 1023 | `GovernmentScreen.ECONOMY_INFO` | `"A month of revenue against a year of output is the only honest way to " + "c...` | P8: why a month against a year. |
| 1028 | `GovernmentScreen.NO_YEAR` | `"There is no output recorded yet, so nothing here can be put in " + "proporti...` | P7: no month of output yet. |
| 1168 | `GovernmentScreen.LIST_COLUMNS` | `{ 100, 60, 70 }` | The column heads' widths: a month, of the budget, of GDP. |
| 1171 | `GovernmentScreen.LIST_DOOR` | `96` | ...and the door's at the end of a row: "set it ›", "Land office ›". |
| 1429 | `GovernmentScreen.FUND_INFO` | `String.format("A twelfth of %.0f%% of everything the city's fund holds, " + "...` | P20: the fund's transfer. |
| 1725 | `GovernmentScreen.PRINCIPAL_INFO` | `"Not on the list above, and deliberately: repaying principal is not " + "spen...` | P27: principal is not spending. |
| 1730 | `GovernmentScreen.TERM_INFO` | `"A term loan pays its coupon every month and the whole face at the end, so th...` | P28: term loans. |
| 1735 | `GovernmentScreen.SERIAL_INFO` | `"Serial bonds amortise - a slice of principal falls due every anniversary, so...` | P29: serial bonds. |
| 1739 | `GovernmentScreen.NOTES_INFO` | `"Notes carry no interest at all - the lender's return was the discount, taken...` | P31: notes. |
| 1744 | `GovernmentScreen.COUPON_INFO` | `"The interest on the budget is struck from what the city actually paid over a...` | P30: the coupon and the budget disagreeing. |
| 1840 | `GovernmentScreen.BOTH_DEFICIT_INFO` | `"Both run at a deficit on purpose.What the deficit buys is on the " + "Servic...` | P32, as it is true: both cost the city more than they charge. |
| 1844 | `GovernmentScreen.NOT_BOTH_INFO` | `"Care and the schools are meant to run at a deficit: what it buys is on the "...` | ...and when one does not (B16: fees with no bill). |
| 1883 | `GovernmentScreen.PENSIONS_INFO` | `"It is pay-as-you-go: this month's workers pay this month's pensioners, so " ...` | P33: pay-as-you-go. |
| 1966 | `GovernmentScreen.LAYERS_INFO` | `"What GDP is made of over the years, as City History draws it in layers: " + ...` | The layers' caption's (i). |
| 2043 | `GovernmentScreen.STOCK_INFO` | `"Stock built up is output that has been made and not yet sold, so it counts "...` | P34: stock built up. |
| 2053 | `GovernmentScreen.GOVERNMENT_INFO` | `"Services with no market price, valued at cost: the utilities' staff, " + "ca...` | P35, rewritten (B5): what G is, as the model counts it. |
| 2090 | `GovernmentScreen.MOM_INFO` | `"Month-on-month annualised is one month multiplied up - compounded, so a city...` | P37, on the strip's month on month. |
| 2095 | `GovernmentScreen.YOY_INFO` | `"This month's output against the same month a year ago, in today's money - " ...` | P36's point, on the strip's year on year: nominal, where the header's tile is real. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 95 | `String govPage` |  |
| 104 | `private final java.util.Set<String> open` | What is standing open - a line's payers, a "details" fold, a bridge column's "N more" - by key. |
| 107 | `private String scrollTarget` | Where the page is to be scrolled to once it is drawn: a line's key, BRIDGE or BALANCE; null for the top. |
| 110 | `private final java.util.Map<String, Node> targets` | The nodes a door on this tab can scroll to, by the same keys, as the page draws them. |
| 113 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 2084 | **type** `final class GovernmentScreen` | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, who pays every line and where each is decided, what the city owes and h... |
| 50 | 1 | `GovernmentScreen(UserInterface ui)` |  |

### THE GOVERNMENT. (lines 52-234)

| line | len | member | says |
|---:|---:|---|---|
| 131 | 7 | `void open(String page, String line)` | The tab opened on one of its pages (0.7.31): at its top, or with `line` - a budget line's name on Revenue or Spending, which opens it and scrolls to it, or BRIDGE or BALANCE on the Overview - in view. |
| 140 | 1 | `static String lineKey(String page, String line)` | A line's key on its page: what its open state and its scroll target are kept under. |
| 142 | 37 | `void showGovernmentMenu()` |  |
| 181 | 16 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (SectorScreen's, 0.7.30). |
| 199 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 212 | 4 | `void showOnOverview(String target)` | The Overview's bridge or balance in view: scrolled to where it is, or the Overview opened on it. |
| 229 | 5 | `HBox head()` | The head: "Government" with the money blue's swatch, and at its right the two doors every page has - the taxes, on Policy, and the revenue and the surplus over the years, in City History. |

### THE FIVE FIGURES (0.7.31; four until then) (lines 235-356)

| line | len | member | says |
|---:|---:|---|---|
| 248 | 1 | **type** `record Kpi(String label, String value, String note, String tone, String where)` | One of the five figures, worked out without drawing it: its label, its figure, its note, its colour and where its click goes. |
| 251 | 30 | `List<Kpi> kpis(NationalAccounts na, List<CityNeeds.Need> all)` | The five figures (pure: the probe reads them as the strip shows them). |
| 282 | 16 | `HBox vitals(EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` |  |
| 300 | 23 | `void withSpark(VBox cell, String series)` | A limit cell with a year of a History series as a sparkline at the right of its figure, and its change on last month under the note (ServicesScreen.kpiCell()'s shape). |
| 325 | 13 | `String change(String series)` | A money series' change on last month, from its last two points in History: "▲ $1.4M on last month"; null with fewer than two. |
| 340 | 4 | `static CityNeeds.Need need(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | NEEDS YOU's row of a kind - the one judge of it - or null when it is not measured. |
| 346 | 4 | `static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | ...its level: 0 green, 1 amber, 2 red, -1 not measured. |
| 352 | 4 | `static String needTone(List<CityNeeds.Need> all, CityNeeds.Kind kind, String none)` | ...and its colour, or `none` when it is not measured. |

### the screen's own pieces (lines 357-474)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 4 | `static String m(double thousands)` | Money with a true minus (B15: tightMoney() writes a hyphen, signedTight() a minus, and both stood on one page). |
| 366 | 1 | `static String s(double thousands)` | Money signed, as a movement: "+$1.2M", "−$3.5M", "$0". |
| 369 | 3 | `static String ofGdp(double monthly, double annual)` | A share of a year's output, as every "of GDP" on this tab writes it, or a dash with no year to compare. |
| 374 | 1 | `static String minus(String figure)` | A formatted figure's hyphen as a minus (B15). |
| 384 | 5 | `static List<Double> drawn(List<Double> amounts)` | The amounts a ring and a list draw: a line under half a thousand ($500) as nothing - no slice, no bar, and it joins the list's "nothing this month". |
| 391 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 400 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 408 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 419 | 14 | `static HBox cardHead(String svg, String colour, String name, String info, Node door)` | A card's head: its icon in a square tinted in its colour, its name, its (i), and a door at its right (each of the last two may be null). |
| 435 | 8 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. |
| 445 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 456 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 463 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 468 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold or an opened line: the old rows at their old width (D14). |

### the doors (D15) (lines 475-560)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (SectorScreen.openPolicy()'s). |
| 487 | 6 | `void openFinances(String area, String page)` | Finances, on one of its subjects and pages. |
| 499 | 18 | `static String[] policyPlace(String line)` | Where a budget line is decided (0.7.31, the spec's D15), as Policy's {area, page} - each a page in PolicyScreen's own lists - or null for a line no dial on Policy sets. |
| 519 | 1 | **type** `record Door(String words, Runnable go)` | A budget line's door: its words and where it goes - a dial on Policy, or the screen the line is decided on; null for none. |
| 521 | 15 | `Door doorOf(String line)` |  |
| 538 | 22 | `static String iconOf(String line)` | Each budget line's icon (the spec's section 6): every one an icon the rail and Build already draw. |

### THE OVERVIEW (0.7.31) (lines 561-591)

| line | len | member | says |
|---:|---:|---|---|
| 571 | 20 | `void overviewPage(VBox page, EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` |  |

### the rings and the balance (lines 592-779)

| line | len | member | says |
|---:|---:|---|---|
| 600 | 38 | `HBox budgetCard(EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` | WHERE IT COMES FROM · THE BALANCE · WHERE IT GOES: the two rings - five slices and a grey rest each, the walkthrough's keeper - with their keys, and between them the balance. |
| 640 | 18 | `VBox ringBlock(String title, List<Slice> slices, String top, String total, List<Node> foot, String page)` | One ring's block: its caption, the ring and its key side by side, and the netted and outside lines under them. |
| 664 | 10 | `static List<Node> netted(List<String> names, List<Double> amounts)` | A ring's netted lines: every line below nothing, which the ring cannot draw as an arc and the total under the ring's caption nets - "less utility income −$471k" (the spec's B14). |
| 692 | 39 | `VBox balanceBlock(NationalAccounts na, List<CityNeeds.Need> all)` | THE BALANCE: the verdict word in NEEDS YOU's colour, the figure, what share of the take it is, and the two bars on one scale - taken in in the money blue, paid out in its darker step (the spec's D6; the paid out bar w... |
| 737 | 34 | `static VBox balanceBar(String name, double value, double other, double scale, String colour, String tone, boolean first)` | One balance bar: its name and figure over a bar of 230 x 12 on the scale both share, filled in its colour to the shorter of the two figures and - when it is the longer - outlined in `tone` past that. |
| 773 | 6 | `static void place(Region r, double x, double w, double h)` | A region held at a size, at x along a plain Pane (which sizes its children to their preferred size). |

### from EARNED to BANKED (lines 780-969)

| line | len | member | says |
|---:|---:|---|---|
| 811 | 43 | `VBox bridgeCard(NationalAccounts na)` | FROM EARNED TO BANKED: three tiles - the header's figure, the budget, the cash - and the steps between each pair, every one a model figure by name (Game.getEarnedToBudget() and its residual; the bridge's raised and re... |
| 856 | 15 | `List<BridgeStep> earnedSteps()` | The steps from EARNED to the budget: Game.getEarnedToBudget() a line each, and what they leave - today's dials. |
| 873 | 15 | `List<BridgeStep> cashSteps()` | The steps from the budget to the cash: paper raised and repaid, the journal's lines in the player's words, and what is not accounted for. |
| 890 | 9 | `BridgeStep mostly(double balance)` | The biggest step from the budget to the cash, when the cash moved more by those steps than by the budget (P4's alert, as a chip) - or null. |
| 901 | 12 | `static String journalIcon(String label)` | A journal line's icon, by the words TreasuryJournal records it in. |
| 915 | 12 | `Runnable journalDoor(String label)` | ...and where it is decided (the spec's section 4): reserves on Trade, the fund on Finances, the bank's shares on Bank, the central bank on Policy, repairs and salvage on Build's construction page, fares on Infrastruct... |
| 935 | 24 | `VBox treasuryStatement()` | The treasury's month as the old page said it, in the fold: opened with, closed with, grew or fell by; the budget, the paper and every journal line with what was not accounted for; and the change. |
| 961 | 8 | `static int moved(javafx.scene.layout.GridPane grid, int row, String label, double amount, boolean under)` | One row of the fold's grid: a movement, signed, and a zero written as one. |

### the central bank (lines 970-1019)

| line | len | member | says |
|---:|---:|---|---|
| 973 | 7 | `String advancesInfo()` | P5: the advances. |
| 992 | 27 | `VBox centralBankCard(CentralBank cb)` | CENTRAL BANK (the old page's "What it owes its central bank"), shown when the city owes it, has ever been advanced anything, or has arrears: a ring of the advances against the ceiling - red once the ceiling binds, a v... |

### against the economy (lines 1020-1072)

| line | len | member | says |
|---:|---:|---|---|
| 1039 | 33 | `VBox economyCard(NationalAccounts na)` | AGAINST THE ECONOMY: what was taken in, paid out and kept or short - the surplus or deficit - and the care and schools staff's wages, as bars on one scale of per cent of a year's GDP - a month and a year in their tool... |

### THE TWO LISTS (0.7.31: ranked bars). (lines 1073-1282)

| line | len | member | says |
|---:|---:|---|---|
| 1088 | 7 | `static List<String> revenueNames()` |  |
| 1096 | 28 | `List<Double> revenueAmounts(NationalAccounts na)` |  |
| 1137 | 5 | `static List<String> spendingNames()` | REPAIRS LEFT THIS LIST IN 0.7.31, and stayed on the page. |
| 1143 | 23 | `List<Double> spendingAmounts(NationalAccounts na)` |  |
| 1174 | 4 | `String scaledYearInfo(NationalAccounts na)` | P19, on the "of GDP" head while the year is scaled up. |
| 1186 | 60 | `void listPage(VBox page, String pageName, String verb, List<String> names, List<Double> amounts, double total, NationalAccounts...` | A list page: its head line ("$162.5M taken in · 34.4% of annual GDP · the month"), the column heads, a ranked bar a line - every line with a door, the ones that open with their payers - the lines at nothing folded int... |
| 1248 | 34 | `HBox listHead(NationalAccounts na)` | The column heads over a list, in the rows' own columns; the "of GDP" head carries P19 while the year is scaled. |

### who pays what (lines 1283-1474)

| line | len | member | says |
|---:|---:|---|---|
| 1286 | 1 | **type** `record Payer(String who, double amount, String rate)` | One payer inside an opened line. |
| 1293 | 14 | `VBox payers(String caption, String note, List<Payer> who, double total, String colour)` | A line opened (0.7.31): its payers as one bar - the row's colour at two strengths, alternating - then a payer row each, and the old note behind the (i) on the panel's first line. |
| 1309 | 4 | `String colourOf(String line, List<String> names, List<Double> amounts)` | A line's colour as the list draws it: its ring slice's, or the grey rest. |
| 1315 | 17 | `VBox businessTaxDetail(double total, String colour)` | Business tax, by the companies that pay it - every sector, and the bank. |
| 1334 | 13 | `VBox salesTaxDetail(double total, String colour)` | Sales tax, by the sector that remitted it. |
| 1349 | 13 | `VBox wageTaxDetail(double total, String colour)` | Wage tax, by the pay tier that earned the wages. |
| 1364 | 11 | `VBox contributionsDetail(double total, String colour)` | Pension contributions, by the tier that paid them. |
| 1377 | 14 | `VBox propertyTaxDetail(double total, String colour)` | Property tax, by the sector it is assessed on. |
| 1393 | 17 | `VBox healthFeeDetail(double total, String colour)` | Healthcare fees, by the kind of care that charged them - and the burials and cremations, which charge with no care bill of their own. |
| 1412 | 15 | `VBox schoolFeeDetail(double total, String colour)` | School fees, by the course. |
| 1433 | 9 | `VBox fundTransferDetail()` | THE TRANSFER FROM THE CITY'S FUND (0.7.14), opened from its line: due, paid and short this month, and the fund it is struck on. |
| 1444 | 7 | `static String insuranceInfo()` | P21: the mortgage insurance. |
| 1460 | 14 | `VBox mortgageInsuranceDetail()` | THE CITY'S MORTGAGE INSURANCE (0.7.11), opened from either of its two lines: the premiums taken this month and what they were on, what the insurance paid the bank, and the book over the city's life - does the city mak... |

### what it spends (lines 1475-1514)

| line | len | member | says |
|---:|---:|---|---|
| 1478 | 17 | `VBox healthSpendDetail(double total, String colour)` | Healthcare spending, by the kind of care it is spent on. |
| 1497 | 17 | `VBox educationSpendDetail(double total, String colour)` | Education spending, by the school it is spent on. |

### WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON (lines 1515-1656)

| line | len | member | says |
|---:|---:|---|---|
| 1546 | 2 | **type** `record PaperKind(String name, int count, double principal, double coupon, String note, double discount, int...` | One kind of paper, totalled: its pieces, principal, coupons a month, the discounts it was sold at, and the soonest of its maturities. |
| 1549 | 23 | `java.util.List<PaperKind> paperKinds()` |  |
| 1573 | 3 | `static PaperKind kind(String name, double[] t, String note)` |  |
| 1578 | 6 | `PaperKind paperKind(String name)` | One kind's row, or an empty one. |
| 1586 | 10 | `String interestInfo(double repaid)` | P24: interest is the only part of debt service that is an expense. |
| 1603 | 15 | `VBox debtServiceDetail(double total, String colour)` | The interest line, opened into the paper it is charged on. |
| 1620 | 11 | `VBox pensionDetail(double total, String colour)` | Pensions, and who they go to. |
| 1640 | 16 | `VBox landSpendDetail(double total, String colour)` | The land line, opened (0.7.6): the land office is paid in US dollars, and the budget carries it at what it cost in local money on the day - paid by converting cash, or out of the vault with no cash moving at all (the ... |

### REVENUE AND SPENDING (0.7.31) (lines 1657-1721)

| line | len | member | says |
|---:|---:|---|---|
| 1661 | 27 | `void revenuePage(VBox page, EconomyManager em, NationalAccounts na)` |  |
| 1689 | 32 | `void spendingPage(VBox page, EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` |  |

### the three cards (lines 1722-1913)

| line | len | member | says |
|---:|---:|---|---|
| 1757 | 38 | `VBox debtCard(NationalAccounts na, double spending)` | THE DEBT: the principal by paper as one bar in the maturity ladder's three steps, what is owed and what it costs a month, a line a kind - the notes' "no coupon: the discount was the price" - the principal repaid ("not... |
| 1797 | 22 | `List<String[]> debtLines(NationalAccounts na)` | THE DEBT card's lines, {words, (i)} each, the first the total with no (i) (pure: the probe reads them). |
| 1821 | 17 | `Node debtGrid(double spending)` | S6's grid: pieces, owed, interest a month, of spending - plain figures. |
| 1857 | 9 | `VBox servicesCard(EconomyManager em)` | THE SERVICES THAT CHARGE: care and the schools, each a bar of its fees against its bill and one line - what the fees cover and the net cost, or what they ran over by, or that there was no bill. |
| 1868 | 6 | `static String serviceWords(String name, double bill, double fees, double net)` | One service's words: what its fees cover and its net cost, what they ran over by, or that there was no bill (B3, B16). |
| 1876 | 5 | `static VBox serviceLine(String name, double bill, double fees, double net)` | One service: its fees against its bill as a bar, and its words. |
| 1895 | 18 | `VBox pensionsCard(EconomyManager em, List<CityNeeds.Need> all)` | PENSIONS: contributions against what the pensions cost as a bar, the seniors and what each is paid, and what came out of general revenue - with a chip in NEEDS YOU's PENSIONS colour only while NEEDS YOU lists it (B11:... |

### THE OUTPUT (0.7.31) (lines 1914-2128)

| line | len | member | says |
|---:|---:|---|---|
| 1925 | 39 | `void outputPage(VBox page, NationalAccounts na)` |  |
| 1972 | 41 | `HBox outputHero(NationalAccounts na)` | WHAT THE CITY MAKES: History's layers chart at the left, this month's GDP, the year, a head and the real growth at the right. |
| 2015 | 6 | `static String realGrowthWords(int recorded, double grew)` | The header GDP tile's real growth, in its words (UserInterface's tile), without its verdict colour - that is the tile's. |
| 2023 | 12 | `static VBox partCard(String name, String colour, String amount, String info, Node...lines)` | One of the four parts' cards: its name, its figure, and its lines. |
| 2036 | 5 | `VBox consumptionCard(NationalAccounts na)` |  |
| 2046 | 5 | `VBox investmentCard(NationalAccounts na)` |  |
| 2056 | 4 | `VBox governmentCard(NationalAccounts na)` |  |
| 2068 | 20 | `VBox tradeCard(NationalAccounts na)` | NET EXPORTS: what was sold abroad against what was bought, as two bars on one scale - exports in the business violet, imports in its darker step in three parts - with the parts named for what they are (B5: "Steel expo... |
| 2100 | 19 | `HBox growthStrip(NationalAccounts na)` | The growth strip: five readings, no verdict tone (B4: MoM and YoY were coloured on thresholds of the screen's own). |
| 2121 | 7 | `static void withInfo(VBox cell, String info)` | A limit cell's note with an (i) after it. |


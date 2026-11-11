# GovernmentScreen.java - 2,779 lines · 120 methods · 42 constants · interface

`ham/citybuildersim/ui/GovernmentScreen.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (222), [Kept](Kept.md) (143), [Icons](Icons.md) (56), [Money](Money.md) (37), [NationalAccounts](NationalAccounts.md) (33), [CityNeeds](CityNeeds.md) (24), [CareType](CareType.md) (16), [EconomyManager](EconomyManager.md) (12), [HistoryScreen](HistoryScreen.md) (8), [HouseholdAccounts](HouseholdAccounts.md) (7), [EducationType](EducationType.md) (6), [TimeChart](TimeChart.md) (5), [HistorySave](HistorySave.md) (4), [BuildScreen](BuildScreen.md) (4), [GovernmentView](GovernmentView.md) (4), [FinancesScreen](FinancesScreen.md) (3), [ServicesScreen](ServicesScreen.md) (3), [TreasuryJournal](TreasuryJournal.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Sector](Sector.md) (3), [DebtManager](DebtManager.md) (3), [UserInterface](UserInterface.md) (2), [PolicyScreen](PolicyScreen.md) (2), [CentralBank](CentralBank.md) (2), [TreasuryLine](TreasuryLine.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingManager](BuildingManager.md) (2), [SummaryScreen](SummaryScreen.md) (1), [PolicyPreview](PolicyPreview.md) (1), [CityCalendar](CityCalendar.md) (1)... and 11 more

**Used by (3):** [FinancesScreen](FinancesScreen.md), [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 60 | THE GOVERNMENT. |
| 281 | · THE FIVE FIGURES (0.7.31; four until then) |
| 412 | · the screen's own pieces |
| 604 | · the doors (D15) |
| 719 | THE OVERVIEW (0.7.31) |
| 755 | · government wealth (GV1) |
| 830 | · the rings and the balance |
| 988 | · the books (CD3) |
| 1093 | · from EARNED to BANKED |
| 1120 | · the books (CD3) |
| 1386 | · the central bank |
| 1442 | · against the economy |
| 1506 | THE TWO LISTS (0.7.31: ranked bars). |
| 1761 | · who pays what |
| 1960 | · what it spends |
| 2000 | WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON |
| 2139 | · · and the money that is not on this statement |
| 2221 | REVENUE AND SPENDING (0.7.31) |
| 2286 | · the three cards |
| 2553 | THE OUTPUT (0.7.31) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 101 | `GovernmentScreen.GOV_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.FINANCES, Icons.REPORTS }` | Each page's icon on its chip (0.7.31). |
| 124 | `GovernmentScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with SURPLUS's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.31). |
| 127 | `GovernmentScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - SectorScreen's, for the same fra... |
| 130 | `GovernmentScreen.BRIDGE` | `"#bridge", BALANCE = "#balance"` | The scroll targets on the Overview: the bridge card and the balance. |
| 215 | `GovernmentScreen.BOUND_TO_FRAME` | `"gov.frame", BOUND_TO_SCROLLER = "gov.scroller"` | The key in a kept scroller's properties for the frame its height is bound to, and in a kept page's for the scroller its width is bound to (frameOver()). |
| 264 | `GovernmentScreen.HEAD_INFO` | `"What the city took in and paid out last month, and what its money did." + "E...` | The tab's (i): what the five figures are, in a breath. |
| 430 | `GovernmentScreen.TRAILING_MONTHS` | `12` | Every "of GDP" on this tab reads the trailing year of its line where History records it (B7, 0.7.47): the last TRAILING_MONTHS months, once the line has that many. |
| 433 | `GovernmentScreen.LINE_KEYS` | `java.util.Map.ofEntries(java.util.Map.entry("Business tax", List.of("taxBusin...` | The History series each budget line is, where it records one - the line's own figure each month (a series of something else, like the care's and the schools' net cost against their gross lines, is not one). |
| 450 | `GovernmentScreen.REVENUE_KEYS` | `List.of("revenue")` | What was taken in: History's revenue. |
| 452 | `GovernmentScreen.SURPLUS_KEYS` | `List.of("surplus")` | ...the balance: History's surplus, negative for a deficit. |
| 472 | `GovernmentScreen.RETIRED_GAIN_KEY` | `"retiredGain"` | ...the gain (loss) on debt retired, which the balance carries and spending does not (CD4, CD3's ★5). |
| 475 | `GovernmentScreen.DEBT_EXCHANGE_KEY` | `"debtExchange"` | ...and the exchange gain (loss) on the city's foreign-currency paper, which the balance carries and spending does not (CD7). |
| 914 | `GovernmentScreen.SURPLUS_INFO` | `"It took in more than it spent.A surplus pays down debt or buys the next " + ...` | P1's rest: what a surplus is for. |
| 918 | `GovernmentScreen.DEFICIT_INFO` | `"It spent more than it took in.That gap is borrowed, and next month's " + "in...` | ...and what a deficit costs. |
| 1112 | `GovernmentScreen.DIALS_INFO` | `"EARNED is read at today's tax rates; the budget was struck at the month's." ...` | The dials' step's tooltip (the spec's B7). |
| 1117 | `GovernmentScreen.RESIDUAL_INFO` | `"What the named steps do not explain: timing between the books and the money,...` | The "not accounted for" step's tooltip. |
| 1123 | `GovernmentScreen.NOT_CASH_INTEREST_INFO` | `"The budget charges the discount wearing off the city's paper as " + "interes...` | The not-cash interest step's tooltip: the discount wearing off the city's paper, charged to the budget and paid to nobody yet. |
| 1129 | `GovernmentScreen.NOT_CASH_GAIN_INFO` | `"Buying paper back for less than it is carried at books a gain, and for " + "...` | The not-cash gain step's tooltip: the gain on paper bought back, booked against what it was carried at, paid by nobody. |
| 1134 | `GovernmentScreen.NOT_CASH_UPLIFT_INFO` | `"A linked bond's principal follows the price index, and the budget " + "charg...` | The not-cash uplift step's tooltip (CD5): the indexation of the linked bonds, charged to the budget and paid in the bond's last month. |
| 1141 | `GovernmentScreen.RETIRED_GAIN_INFO` | `"Paper bought back before it falls due is booked at what it was carried " + "...` | The gain (loss) on debt retired: the line under the budget's operating lines, and what is in the balance. |
| 1398 | `GovernmentScreen.ARREARS_INFO` | `"What the ceiling cut: owed, with no interest on it, and paid down out of the...` | P6: the arrears. |
| 1445 | `GovernmentScreen.ECONOMY_INFO` | `"A year of revenue against a year of output is the only honest way to " + "co...` | P8: why a month against a year. |
| 1452 | `GovernmentScreen.NO_YEAR` | `"There is no output recorded yet, so nothing here can be put in " + "proporti...` | P7: no month of output yet. |
| 1632 | `GovernmentScreen.LIST_COLUMNS` | `{ 100, 60, 70 }` | The column heads' widths: a month, of the budget, of GDP. |
| 1635 | `GovernmentScreen.LIST_DOOR` | `96` | ...and the door's at the end of a row: "set it ›", "Land office ›". |
| 1910 | `GovernmentScreen.FUND_INFO` | `String.format("The withdrawal dial's share of everything the city's fund hold...` | P20: the fund's transfer. |
| 2289 | `GovernmentScreen.PRINCIPAL_INFO` | `"Not on the list above, and deliberately: repaying principal is not " + "spen...` | P27: principal is not spending. |
| 2294 | `GovernmentScreen.TERM_INFO` | `"A term loan pays its coupon every month and the whole face at the end, so th...` | P28: term loans. |
| 2299 | `GovernmentScreen.SERIAL_INFO` | `"Serial bonds amortise - a slice of principal falls due every anniversary, so...` | P29: serial bonds. |
| 2303 | `GovernmentScreen.FLOATING_INFO` | `"A floating-rate note pays the policy rate of the month plus a margin fixed a...` | Floating-rate notes (CD5, spec-city-debt.md 2.4). |
| 2309 | `GovernmentScreen.LINKED_INFO` | `"An inflation-linked bond pays a real coupon on a principal that follows the ...` | Inflation-linked bonds (CD5, spec-city-debt.md 2.5). |
| 2315 | `GovernmentScreen.NOTES_INFO` | `"A note pays no coupon.The lender's return is the discount, taken out of the ...` | P31: notes. |
| 2321 | `GovernmentScreen.COUPON_INFO` | `"The coupons on the budget are struck from what the city actually paid over a...` | P30: the coupon and the budget disagreeing. |
| 2475 | `GovernmentScreen.BOTH_DEFICIT_INFO` | `"Both run at a deficit on purpose.What the deficit buys is on the " + "Servic...` | P32, as it is true: both cost the city more than they charge. |
| 2479 | `GovernmentScreen.NOT_BOTH_INFO` | `"Care and the schools are meant to run at a deficit: what it buys is on the "...` | ...and when one does not (B16: fees with no bill). |
| 2522 | `GovernmentScreen.PENSIONS_INFO` | `"It is pay-as-you-go: this month's workers pay this month's pensioners, so " ...` | P33: pay-as-you-go. |
| 2608 | `GovernmentScreen.LAYERS_INFO` | `"What GDP is made of over the years, as City History draws it in layers: " + ...` | The layers' caption's (i). |
| 2686 | `GovernmentScreen.STOCK_INFO` | `"Stock built up is output that has been made and not yet sold, so it counts "...` | P34: stock built up. |
| 2696 | `GovernmentScreen.GOVERNMENT_INFO` | `"Services with no market price, valued at cost: the utilities' staff, " + "ca...` | P35, rewritten (B5): what G is, as the model counts it. |
| 2734 | `GovernmentScreen.MOM_INFO` | `"Month-on-month annualised is one month multiplied up - compounded, so a city...` | P37, on the strip's month on month. |
| 2739 | `GovernmentScreen.YOY_INFO` | `"This month's output against the same month a year ago, in today's money - " ...` | P36's point, on the strip's year on year: nominal, where the header's tile is real. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 53 | `final Kept.Pool pool` | The nodes this screen made at its last draw, handed back to it at the next (KP6; study UP4, docs/studies/spec-screens-in-place.md). |
| 103 | `String govPage` |  |
| 112 | `final java.util.Set<String> open` | What is standing open - a line's payers, a "details" fold, a bridge column's "N more" - by key. |
| 115 | `private String scrollTarget` | Where the page is to be scrolled to once it is drawn: a line's key, BRIDGE or BALANCE; null for the top. |
| 118 | `private final java.util.Map<String, Node> targets` | The nodes a door on this tab can scroll to, by the same keys, as the page draws them. |
| 121 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 2735 | **type** `final class GovernmentScreen` | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, who pays every line and where each is decided, what the city owes and h... |
| 50 | 1 | `GovernmentScreen(UserInterface ui)` |  |
| 56 | 3 | `void forget()` | Every node is made new at the next draw: another city opened (UserInterface.anotherCity()). |

### THE GOVERNMENT. (lines 60-280)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 10 | `void open(String page, String line)` | The tab opened on one of its pages (0.7.31): at its top, or with `line` - a budget line's name on Revenue or Spending, which opens it and scrolls to it, or BRIDGE or BALANCE on the Overview - in view. |
| 151 | 1 | `static String lineKey(String page, String line)` | A line's key on its page: what its open state and its scroll target are kept under. |
| 153 | 16 | `void showGovernmentMenu()` |  |
| 176 | 32 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck - the page `govPage` names under its frame. |
| 210 | 3 | `private void toTop()` | The page on show scrolled to its top: a page opened is drawn from the top, and another page is a scope of its own with a scroller of its own, which opens there; but the page opened may be the one already on show, whos... |
| 218 | 25 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (SectorScreen's, 0.7.30). |
| 245 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 258 | 4 | `void showOnOverview(String target)` | The Overview's bridge or balance in view: scrolled to where it is, or the Overview opened on it. |
| 275 | 5 | `HBox head()` | The head: "Government" with the money blue's swatch, and at its right the two doors every page has - the taxes, on Policy, and the revenue and the surplus over the years, in City History. |

### THE FIVE FIGURES (0.7.31; four until then) (lines 281-411)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 1 | **type** `record Kpi(String label, String value, String note, String tone, String where)` | One of the five figures, worked out without drawing it: its label, its figure, its note, its colour and where its click goes. |
| 297 | 31 | `List<Kpi> kpis(NationalAccounts na, List<CityNeeds.Need> all)` | The five figures (pure: the probe reads them as the strip shows them). |
| 329 | 21 | `HBox vitals(EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` |  |
| 352 | 26 | `void withSpark(VBox cell, String series)` | A limit cell with a year of a History series as a sparkline at the right of its figure, and its change on last month under the note (ServicesScreen.kpiCell()'s shape). |
| 380 | 13 | `String change(String series)` | A money series' change on last month, from its last two points in History: "▲ $1.4M on last month"; null with fewer than two. |
| 395 | 4 | `static CityNeeds.Need need(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | NEEDS YOU's row of a kind - the one judge of it - or null when it is not measured. |
| 401 | 4 | `static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | ...its level: 0 green, 1 amber, 2 red, -1 not measured. |
| 407 | 4 | `static String needTone(List<CityNeeds.Need> all, CityNeeds.Kind kind, String none)` | ...and its colour, or `none` when it is not measured. |

### the screen's own pieces (lines 412-603)

| line | len | member | says |
|---:|---:|---|---|
| 415 | 4 | `static String m(double thousands)` | Money with a true minus (B15: tightMoney() writes a hyphen, signedTight() a minus, and both stood on one page). |
| 421 | 1 | `static String s(double thousands)` | Money signed, as a movement: "+$1.2M", "−$3.5M", "$0". |
| 455 | 6 | `boolean trailing(List<String> keys)` | Whether History has TRAILING_MONTHS months of every one of these series (and there is one). |
| 463 | 7 | `double yearOf(double monthly, List<String> keys)` | A line's year: the sum of its series' last TRAILING_MONTHS months where trailing(), the month × 12 where not. |
| 486 | 7 | `double spendingYear(double monthly)` | ...what was paid out: what came in less the balance, plus the gain on debt retired that the balance carries (NationalAccounts.getBalance() is revenue less expenses plus that gain) - their trailing years, or the month ... |
| 495 | 3 | `String ofGdpYear(List<String> keys, double monthly, double annual)` | A line's share of a year's output, as every "of GDP" on this tab writes it - its trailing year where History records it - or a dash with no year to compare. |
| 500 | 1 | `static String minus(String figure)` | A formatted figure's hyphen as a minus (B15). |
| 510 | 5 | `static List<Double> drawn(List<Double> amounts)` | The amounts a ring and a list draw: a line under half a thousand ($500) as nothing - no slice, no bar, and it joins the list's "nothing this month". |
| 517 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 526 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 534 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 545 | 14 | `static HBox cardHead(String svg, String colour, String name, String info, Node door)` | A card's head: its icon in a square tinted in its colour, its name, its (i), and a door at its right (each of the last two may be null). |
| 561 | 8 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. |
| 571 | 12 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 585 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 592 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 597 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold or an opened line: the old rows at their old width (D14). |

### the doors (D15) (lines 604-718)

| line | len | member | says |
|---:|---:|---|---|
| 607 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (SectorScreen.openPolicy()'s). |
| 616 | 6 | `void openFinances(String area, String page)` | Finances, on one of its subjects and pages. |
| 628 | 27 | `static String[] policyPlace(String line)` | Where a budget line is decided (0.7.31, the spec's D15), as Policy's {area, page} - each a page in PolicyScreen's own lists - or null for a line no dial on Policy sets. |
| 657 | 1 | **type** `record Door(String words, Runnable go)` | A budget line's door: its words and where it goes - a dial on Policy, or the screen the line is decided on; null for none. |
| 659 | 26 | `Door doorOf(String line)` |  |
| 687 | 31 | `static String iconOf(String line)` | Each budget line's icon (the spec's section 6): every one an icon the rail and Build already draw. |

### THE OVERVIEW (0.7.31) (lines 719-754)

| line | len | member | says |
|---:|---:|---|---|
| 729 | 25 | `void overviewPage(VBox page, EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` |  |

### government wealth (GV1) (lines 755-829)

| line | len | member | says |
|---:|---:|---|---|
| 758 | 39 | `VBox wealthCard()` | The government's net financial wealth and three existing doors for returning it; every amount comes from the pure view. |
| 799 | 6 | `static String wealthWords(GovernmentView view)` | A full year's comparable wealth, or the reason the ratio cannot yet be measured. |
| 807 | 8 | `static String wealthColour(GovernmentView.Band band)` | Both too little and too much wealth call for attention; no band is drawn before a comparable year exists. |
| 817 | 12 | `HBox wealthAdvice(String key, String text, String monthly, String action, Runnable go, String info)` | One kept advice row, with an illustrative monthly amount and a door only while that action is available. |

### the rings and the balance (lines 830-987)

| line | len | member | says |
|---:|---:|---|---|
| 838 | 29 | `HBox budgetCard(EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` | WHERE IT COMES FROM · THE BALANCE · WHERE IT GOES: the two rings - five slices and a grey rest each, the walkthrough's keeper - with their keys, and between them the balance. |
| 869 | 24 | `VBox ringBlock(String title, List<Slice> slices, String top, String total, List<Node> foot, String page)` | One ring's block: its caption, the ring and its key side by side, and the netted and outside lines under them. |
| 899 | 13 | `static List<Node> netted(List<String> names, List<Double> amounts)` | A ring's netted lines: every line below nothing, which the ring cannot draw as an arc and the total under the ring's caption nets - "less utility income −$471k" (the spec's B14). |
| 930 | 57 | `VBox balanceBlock(NationalAccounts na, List<CityNeeds.Need> all)` | THE BALANCE: the verdict word in NEEDS YOU's colour, the figure, what share of the take it is, and the two bars on one scale - taken in in the money blue, paid out in its darker step (the spec's D6; the paid out bar w... |

### the books (CD3) (lines 988-1092)

| line | len | member | says |
|---:|---:|---|---|
| 995 | 6 | `static Node retiredGainLine(NationalAccounts na, double wide)` | The gain (below nothing, a loss) on debt retired as a muted line with its (i), or null at nothing: the balance takes it and the lines above it do not (NationalAccounts.getRetiredGain()), so a month it is not zero the ... |
| 1007 | 6 | `static Node debtExchangeLine(NationalAccounts na, double wide)` | CD7: the exchange gain (loss) on the city's foreign-currency paper as a muted line with its (i), or null at nothing: the balance takes it (NationalAccounts.getDebtExchangeGain()) and the lines above it do not, so a mo... |
| 1015 | 12 | `static Node retiredGainRow(NationalAccounts na)` | ...and as a row under the Spending page's total, its own line below the operating lines: the words, an (i), the figure on the right. |
| 1029 | 12 | `static Node debtExchangeRow(NationalAccounts na)` | CD7: ...and the same exchange gain (loss) as a row under the Spending page's total, beside the retired gain's. |
| 1047 | 37 | `static VBox balanceBar(String name, double value, double other, double scale, String colour, String tone, boolean first)` | One balance bar: its name and figure over a bar of 230 x 12 on the scale both share, filled in its colour to the shorter of the two figures and - when it is the longer - outlined in `tone` past that. |
| 1086 | 6 | `static void place(Region r, double x, double w, double h)` | A region held at a size, at x along a plain Pane (which sizes its children to their preferred size). |

### from EARNED to BANKED (lines 1093-1119)

| line | len | member | says |
|---:|---:|---|---|
| 1096 | 14 | `static String bridgeInfo()` | The bridge card's (i): P2, rewritten, and P3. |

### the books (CD3) (lines 1120-1385)

| line | len | member | says |
|---:|---:|---|---|
| 1147 | 6 | `static String debtExchangeInfo()` | CD7: the exchange gain (loss) on the city's foreign-currency paper: the line under the budget's operating lines, and what is in the balance. |
| 1155 | 4 | `static String notCashExchangeInfo()` | CD7: the exchange step's tooltip: the D$'s move on the dollar paper, booked in the budget, paid by nobody. |
| 1167 | 47 | `VBox bridgeCard(NationalAccounts na)` | FROM EARNED TO BANKED: three tiles - the header's figure, the budget, the cash - and the steps between each pair, every one a model figure by name (Game.getEarnedToBudget() and its residual; the bridge's raised and re... |
| 1216 | 12 | `List<BridgeStep> earnedSteps()` | The steps from EARNED to the budget: Game.getEarnedToBudget() a line each, and what they leave - today's dials. |
| 1230 | 32 | `List<BridgeStep> cashSteps()` | The steps from the budget to the cash: paper raised and repaid, the journal's lines in the player's words, and what is not accounted for. |
| 1264 | 9 | `BridgeStep mostly(double balance)` | The biggest step from the budget to the cash, when the cash moved more by those steps than by the budget (P4's alert, as a chip) - or null. |
| 1275 | 12 | `static String journalIcon(String label)` | A journal line's icon, by the words TreasuryJournal records it in. |
| 1289 | 12 | `Runnable journalDoor(String label)` | ...and where it is decided (the spec's section 4): reserves on Trade, the fund on Finances, the bank's shares on Bank, the central bank on Policy, repairs and salvage on Build's construction page, fares on Infrastruct... |
| 1309 | 42 | `VBox treasuryStatement()` | The treasury's month as the old page said it, in the fold: opened with, closed with, grew or fell by; the budget, the paper and every journal line with what was not accounted for; and the change. |
| 1353 | 7 | `static Node outsideRows(NationalAccounts na)` | CD7: the rows under the Spending page's total that the balance carries and the total does not: the retired gain's and the exchange gain's, one or both, or null at neither. |
| 1365 | 6 | `VBox subsidiesSpendDetail(double total, String colour)` | The Subsidies line, opened (R3b): what the city paid to keep its sectors alive (Game.getTotalSubsidyPaid()) and the labs' share of a find's purse (Game.getPrizePaid(), TreasuryLine.PRIZES), which the books strike in t... |
| 1373 | 12 | `static int moved(javafx.scene.layout.GridPane grid, int row, String label, double amount, boolean under)` | One row of the fold's grid: a movement, signed, and a zero written as one. |

### the central bank (lines 1386-1441)

| line | len | member | says |
|---:|---:|---|---|
| 1389 | 7 | `String advancesInfo()` | P5: the advances. |
| 1408 | 33 | `VBox centralBankCard(CentralBank cb)` | CENTRAL BANK (the old page's "What it owes its central bank"), shown when the city owes it, has ever been advanced anything, or has arrears: a ring of the advances against the ceiling - red once the ceiling binds, a v... |

### against the economy (lines 1442-1505)

| line | len | member | says |
|---:|---:|---|---|
| 1465 | 40 | `VBox economyCard(NationalAccounts na)` | AGAINST THE ECONOMY: what was taken in, paid out and kept or short - the surplus or deficit - and the care and schools staff's wages, as bars on one scale of per cent of a year's GDP - a month and a year in their tool... |

### THE TWO LISTS (0.7.31: ranked bars). (lines 1506-1760)

| line | len | member | says |
|---:|---:|---|---|
| 1521 | 7 | `static List<String> revenueNames()` |  |
| 1529 | 37 | `List<Double> revenueAmounts(NationalAccounts na)` |  |
| 1581 | 10 | `static List<String> spendingNames()` | REPAIRS LEFT THIS LIST IN 0.7.31, AND CAME BACK IN 0.7.102. |
| 1592 | 38 | `List<Double> spendingAmounts(NationalAccounts na)` |  |
| 1638 | 4 | `String scaledYearInfo(NationalAccounts na)` | P19, on the "of GDP" head while the year is scaled up. |
| 1650 | 68 | `void listPage(VBox page, String pageName, String verb, List<String> names, List<Double> amounts, double total, NationalAccounts...` | A list page: its head line ("$162.5M taken in · 34.4% of annual GDP · the month"), the column heads, a ranked bar a line - every line with a door, the ones that open with their payers - the lines at nothing folded int... |
| 1720 | 40 | `HBox listHead(NationalAccounts na)` | The column heads over a list, in the rows' own columns; the "of GDP" head carries P19 while the year is scaled. |

### who pays what (lines 1761-1959)

| line | len | member | says |
|---:|---:|---|---|
| 1764 | 1 | **type** `record Payer(String who, double amount, String rate)` | One payer inside an opened line. |
| 1771 | 17 | `VBox payers(String caption, String note, List<Payer> who, double total, String colour)` | A line opened (0.7.31): its payers as one bar - the row's colour at two strengths, alternating - then a payer row each, and the old note behind the (i) on the panel's first line. |
| 1790 | 4 | `String colourOf(String line, List<String> names, List<Double> amounts)` | A line's colour as the list draws it: its ring slice's, or the grey rest. |
| 1796 | 17 | `VBox businessTaxDetail(double total, String colour)` | Business tax, by the companies that pay it - every sector, and the bank. |
| 1815 | 13 | `VBox salesTaxDetail(double total, String colour)` | Sales tax, by the sector that remitted it. |
| 1830 | 13 | `VBox wageTaxDetail(double total, String colour)` | Wage tax, by the pay tier that earned the wages. |
| 1845 | 11 | `VBox contributionsDetail(double total, String colour)` | Pension contributions, by the tier that paid them. |
| 1858 | 14 | `VBox propertyTaxDetail(double total, String colour)` | Property tax, by the sector it is assessed on. |
| 1874 | 17 | `VBox healthFeeDetail(double total, String colour)` | Healthcare fees, by the kind of care that charged them - and the burials and cremations, which charge with no care bill of their own. |
| 1893 | 15 | `VBox schoolFeeDetail(double total, String colour)` | School fees, by the course. |
| 1915 | 12 | `VBox fundTransferDetail()` | THE TRANSFER FROM THE CITY'S FUND (0.7.14), opened from its line: due, paid and short this month, and the fund it is struck on. |
| 1929 | 7 | `static String insuranceInfo()` | P21: the mortgage insurance. |
| 1945 | 14 | `VBox mortgageInsuranceDetail()` | THE CITY'S MORTGAGE INSURANCE (0.7.11), opened from either of its two lines: the premiums taken this month and what they were on, what the insurance paid the bank, and the book over the city's life - does the city mak... |

### what it spends (lines 1960-1999)

| line | len | member | says |
|---:|---:|---|---|
| 1963 | 17 | `VBox healthSpendDetail(double total, String colour)` | Healthcare spending, by the kind of care it is spent on. |
| 1982 | 17 | `VBox educationSpendDetail(double total, String colour)` | Education spending, by the school it is spent on. |

### WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON (lines 2000-2220)

| line | len | member | says |
|---:|---:|---|---|
| 2041 | 3 | **type** `record PaperKind(String name, int count, double principal, double coupon, String note, double discount, int...` | One kind of paper, totalled: its pieces, principal (face), the coupons it pays next month in cash (a linker's uplift not among them: DebtManager.getUpliftDueNextMonth()), the discounts it was sold at, and the soonest ... |
| 2045 | 31 | `java.util.List<PaperKind> paperKinds()` |  |
| 2077 | 3 | `static PaperKind kind(String name, double[] t, String note)` |  |
| 2082 | 6 | `PaperKind paperKind(String name)` | One kind's row, or an empty one. |
| 2090 | 13 | `String interestInfo(double repaid)` | P24: interest is the only part of debt service that is an expense. |
| 2110 | 37 | `VBox debtServiceDetail(double total, String colour)` | The interest line, opened into the paper it is charged on. |
| 2154 | 15 | `VBox foodAssistanceDetail(double total, String colour)` | Food assistance, by who it was paid to (0.7.45): each kind of household's vouchers at the last sale (HouseholdBalance.foodAssistanceByRow()), which add to the treasury's line - not the households' own books, which car... |
| 2174 | 8 | `VBox researchSpendDetail(double total, String colour)` | The research line, opened (R3b): the bill the accounts book is the pay of the posts the grants bought (already in the households' wages, which count a post as the job it is) and the laboratories' other costs (EconomyM... |
| 2184 | 11 | `VBox pensionDetail(double total, String colour)` | Pensions, and who they go to. |
| 2204 | 16 | `VBox landSpendDetail(double total, String colour)` | The land line, opened (0.7.6): the land office is paid in US dollars, and the budget carries it at what it cost in local money on the day - paid by converting cash, or out of the vault with no cash moving at all (the ... |

### REVENUE AND SPENDING (0.7.31) (lines 2221-2285)

| line | len | member | says |
|---:|---:|---|---|
| 2225 | 23 | `void revenuePage(VBox page, EconomyManager em, NationalAccounts na)` |  |
| 2249 | 36 | `void spendingPage(VBox page, EconomyManager em, NationalAccounts na, List<CityNeeds.Need> all)` |  |

### the three cards (lines 2286-2552)

| line | len | member | says |
|---:|---:|---|---|
| 2336 | 51 | `VBox debtCard(NationalAccounts na, double spending)` | THE DEBT: the principal by paper as one bar in the maturity ladder's three steps, what is owed and what it costs a month (since CD3 the interest on the city's paper: the coupons and the discount wearing off), a line a... |
| 2389 | 42 | `List<String[]> debtLines(NationalAccounts na)` | THE DEBT card's lines, {words, (i)} each, the first the total with no (i) (pure: the probe reads them). |
| 2437 | 36 | `Node debtGrid(double spending)` | S6's grid: pieces, owed (face), carried (CD3), interest a month (the effective interest), of spending - plain figures. |
| 2492 | 11 | `VBox servicesCard(EconomyManager em)` | THE SERVICES THAT CHARGE: care and the schools, each a bar of its fees against its bill and one line - what the fees cover and the net cost, or what they ran over by, or that there was no bill. |
| 2505 | 6 | `static String serviceWords(String name, double bill, double fees, double net)` | One service's words: what its fees cover and its net cost, what they ran over by, or that there was no bill (B3, B16). |
| 2513 | 7 | `static VBox serviceLine(String name, double bill, double fees, double net)` | One service: its fees against its bill as a bar, and its words. |
| 2534 | 18 | `VBox pensionsCard(EconomyManager em, List<CityNeeds.Need> all)` | PENSIONS: contributions against what the pensions cost as a bar, the seniors and what each is paid, and what came out of general revenue - with a chip in NEEDS YOU's PENSIONS colour only while NEEDS YOU lists it (B11:... |

### THE OUTPUT (0.7.31) (lines 2553-2779)

| line | len | member | says |
|---:|---:|---|---|
| 2564 | 42 | `void outputPage(VBox page, NationalAccounts na)` |  |
| 2614 | 42 | `HBox outputHero(NationalAccounts na)` | WHAT THE CITY MAKES: History's layers chart at the left, this month's GDP, the year, a head and the real growth at the right. |
| 2658 | 6 | `static String realGrowthWords(int recorded, double grew)` | The header GDP tile's real growth, in its words (UserInterface's tile), without its verdict colour - that is the tile's. |
| 2666 | 12 | `static VBox partCard(String name, String colour, String amount, String info, Node...lines)` | One of the four parts' cards: its name, its figure, and its lines. |
| 2679 | 5 | `VBox consumptionCard(NationalAccounts na)` |  |
| 2689 | 5 | `VBox investmentCard(NationalAccounts na)` |  |
| 2700 | 4 | `VBox governmentCard(NationalAccounts na)` |  |
| 2712 | 20 | `VBox tradeCard(NationalAccounts na)` | NET EXPORTS: what was sold abroad against what was bought, as two bars on one scale - exports in the business violet, imports in its darker step in three parts - with the parts named for what they are (B5: "Steel expo... |
| 2744 | 26 | `HBox growthStrip(NationalAccounts na)` | The growth strip: five readings, no verdict tone (B4: MoM and YoY were coloured on thresholds of the screen's own). |
| 2772 | 7 | `static void withInfo(VBox cell, String info)` | A limit cell's note with an (i) after it. |


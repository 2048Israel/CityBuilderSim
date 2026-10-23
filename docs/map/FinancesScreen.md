# FinancesScreen.java - 4,154 lines · 166 methods · 74 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The Finances tab: what the city owes and when it falls due, what its paper
> costs and who holds it, why its money costs what it does, every piece and
> what it would cost to retire, borrowing at home and abroad, the money
> itself, the businesses' bond market and the city's fund - a hub and six
> areas under five figures.
> 
> WHY, and why it looks like this (0.7.32). Split out of UserInterface on
> 2026-09-18 with its banners verbatim, it was a 560 px statement column:
> six rows on a landing, thirteen pages of lines and some seventy
> paragraphs, two settings repeated on three pages, and a ladder that
> called its "later" bin "year 13" and labelled its bars a year early.
> Jerus, on the screens not yet redone: "the others are still full of text
> and the design could be more intuitive and fun". Redrawn in Build's style
> (the project's spec-finances-0732.md): the hub is the debt's dashboard -
> the ladder by calendar year with NEXT DUE beside it (the 0.7.24 card,
> folded in), the rollover and the bank's rescue said once each, and the six
> areas as cards; every page leads with the one picture that answers its
> question; the paragraphs are behind an (i) and the old tables behind
> "details". The figures are the model's: the ladder, the next twelve
> months, the coupon and each kind's principal are DebtManager's (0.7.32),
> the rollover's cash Rollover.Plan's, the bond market's sums BondMarket's.
> One verdict each: TREASURY in NEEDS YOU's colour, OWED red only when the
> market has priced the city out, NEXT DUE by FALLS DUE's line, the service
> bands CityNeeds' constants. Local money is written D$ throughout, because
> the tab shows dollars too.
> 
> The shell sets the area and page back to the hub (financeArea,
> financePage; its resetSection()) before the rail and the header's money
> block open showFinanceMenu(), and Government's doors set them and call it
> too (GovernmentScreen.openFinances()); open() is every other door in.
> THE DEBT RESULT - what an issue booked, shown after it - came here from
> the build cards on 2026-09-18, because it is this tab's.

**Uses:** [Palette](Palette.md) (523), [DebtManager](DebtManager.md) (54), [Auction](Auction.md) (35), [CityCalendar](CityCalendar.md) (31), [Game](Game.md) (28), [CityNeeds](CityNeeds.md) (26), [Rollover](Rollover.md) (19), [Icons](Icons.md) (17), [Debt](Debt.md) (17), [CorporateBond](CorporateBond.md) (14), [OrderBook](OrderBook.md) (14), [HistoryScreen](HistoryScreen.md) (10), [BondMarket](BondMarket.md) (9), [Bank](Bank.md) (9), [Ladder](Ladder.md) (8), [TimeChart](TimeChart.md) (8), [DebtQuote](DebtQuote.md) (8), [FundScreen](FundScreen.md) (7), [CentralBank](CentralBank.md) (7), [TreasuryFund](TreasuryFund.md) (6), [Currency](Currency.md) (5), [ChartModel](ChartModel.md) (5), [ForeignAccounts](ForeignAccounts.md) (5), [CapChips](CapChips.md) (4), [NationalAccounts](NationalAccounts.md) (4), [HistorySave](HistorySave.md) (4), [LongTermBond](LongTermBond.md) (3), [BuildScreen](BuildScreen.md) (3), [Pieces](Pieces.md) (3), [Money](Money.md) (3)... and 12 more

**Used by (7):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapChips](CapChips.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 74 | FINANCES |
| 493 | · THE FIVE FIGURES (0.7.32; four until then) |
| 662 | · THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line |
| 731 | · the screen's own pieces |
| 926 | THE HUB (0.7.32) |
| 952 | · when it falls due |
| 1120 | · next due |
| 1198 | · the two settings |
| 1200 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1378 | · WHEN THE BANK FAILS (0.7.14) |
| 1465 | · the six areas |
| 1590 | THE POSITION (0.7.32) |
| 1775 | DEBT SERVICE (0.7.32) |
| 1907 | HOME AND ABROAD (0.7.32) |
| 2016 | YOUR RATE, TAKEN APART (0.7.32) |
| 2264 | THE BOOK (0.7.32) |
| 2300 | · the books (CD3) |
| 2491 | BORROW (0.7.32) |
| 2726 | · the highest yield (CD2) |
| 2737 | · the ask, typed (0.7.40) |
| 3169 | · the auction, shown (CD2) |
| 3564 | MONEY (0.7.0; redrawn 0.7.32) |
| 3734 | THE BOND MARKET (0.7.12; redrawn 0.7.32) |
| 4060 | THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) |
| 4074 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 105 | `FinancesScreen.FINANCE_HOME` | `"Overview"` | The page an area opens on when none is named: the first of any area's pages that is called this. |
| 109 | `FinancesScreen.AREAS` | `{ "The position", "The book", "Borrow", "Money", "The bond market", "The city...` | The six areas, in the hub's order. |
| 111 | `FinancesScreen.AREA_ICONS` | `{ Icons.FINANCES, Icons.PAPER, Icons.COIN, Icons.BANKNOTE, Icons.REPORTS, Ico...` | ...and each one's icon, on its card. |
| 114 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "Debt service", "Home & abroad", "Your rate" }` | The position's four pages (0.7.32: "The ladder" became the hub's hero). |
| 116 | `FinancesScreen.POSITION_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.TRADE, Icons.POLICY }` | ...and their icons on the chips. |
| 118 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece" }` | The book is one page since 0.7.32: Buy back is on each piece's card. |
| 120 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` | Borrow's two pages: the city's own paper at home, and dollars from the world. |
| 122 | `FinancesScreen.BORROW_ICONS` | `{ Icons.HOMES, Icons.TRADE }` | ...and their icons on the chips. |
| 124 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 126 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 128 | `FinancesScreen.BOND_ICONS` | `{ Icons.REPORTS, Icons.PAPER }` | ...and their icons on the chips. |
| 130 | `FinancesScreen.FUND_PAGES` | `FundScreen.PAGES` | The city's fund as a brokerage (0.7.39; "Holdings" and "By hand" from 0.7.14): FundScreen's four pages. |
| 132 | `FinancesScreen.FUND_ICONS` | `FundScreen.ICONS` | ...and their icons on the chips. |
| 189 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Bills", "BILL", ShortTermTBill.TERMS, 1000, "months...` | The three kinds of paper CD2 sells, in the board's tile order: bills, bonds, the amortizing bond (floating and linked bonds are CD5's, so no tile). |
| 241 | `FinancesScreen.KIND_NAMES` | `{ "Bills", "Amortizing bonds", "Bonds" }` | The ladder's three kinds, in DebtManager.LADDER_KINDS' order, as the key and a tooltip say them. |
| 293 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 296 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.32; Go... |
| 299 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same... |
| 438 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "Th...` | The tab's (i): what the hub is, in a breath. |
| 444 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balan...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 708 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " +...` | What resolving a failed bank does (the spec's T41). |
| 955 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, "...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 965 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " ...` | The ladder with nothing on it (the spec's T11). |
| 969 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "ref...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 974 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 977 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1218 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month bills" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1221 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't...` | Each setting's one line (the spec's T35). |
| 1229 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and ...` | ...and each in full, on its chip (the old block's sentences). |
| 1240 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's ...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1260 | `FinancesScreen.ROLLOVER_CAP_TITLE` | `"THE HIGHEST YIELD IT WILL TAKE"` | The rollover's cap block: its caption, and its (i), which says what the cap is and what a short sale does (D28, D29). |
| 1261 | `FinancesScreen.ROLLOVER_CAP_INFO` | `"The most the rollover will pay to sell what it refinances: fair plus a " + "...` |  |
| 1387 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1390 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays froze...` | Each setting's one line (the spec's T40). |
| 1396 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose ...` | The rescue's (i): both settings in full (the old block's T40). |
| 1613 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to ...` | THE BALANCE's (i): the spec's T7. |
| 1622 | `FinancesScreen.PAPER_VALUES_INFO` | `"The same paper three ways.Its face is what falls due.What the books " + "car...` | The line under THE BALANCE's bars: three readings of the same paper. |
| 1796 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 1799 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 1845 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the interest is an expense; the principal is a balance-sheet movement."...` | LAST MONTH's (i): the spec's T14. |
| 1886 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city ...` | FOREIGN's (i): the spec's T16. |
| 1917 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none o...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 1971 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the "...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 1993 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports "...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 2117 | `FinancesScreen.CURVE_MONTHS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The curve's maturities, in months. |
| 2119 | `FinancesScreen.CURVE_NAMES` | `{ "3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "40y", "50y" }` | ...and how they are written under it. |
| 2122 | `FinancesScreen.CURVE_INFO` | `"The short end is the floor - the dial, or what the bank's money costs it, " ...` | THE CURVE's (i): the spec's T24, and the world's curve - CD1 priced it on the dial the market expects. |
| 2164 | `FinancesScreen.CurveChart.W` | `720, H = 190, LEFT = 44, RIGHT = 16, TOP = 26, FOOT = 22` | Its size and its margins, in pixels: a card's picture, at a fixed size. |
| 2296 | `FinancesScreen.NOTE_INFO` | `"No coupon.The lender's return was the discount, taken out of the proceeds at...` | A bill's (i): the spec's T28. |
| 2303 | `FinancesScreen.CARRIED_INFO` | `"What the books hold the piece at, and the rate they charge it at.It starts "...` | The "carried · effective" line on a piece's card: its (i). |
| 2310 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, ...` | A price against face, in words (the spec's T33, D16). |
| 2530 | `FinancesScreen.TERMS_INFO` | `"Each column is today's fair yield for this paper at that term: what the curv...` | The terms' (i): the spec's T55, on the auction's fair yield (CD2). |
| 2539 | `FinancesScreen.CAP_INFO` | `"The most the city will pay to sell this: fair plus a point, a yield you name...` | THE HIGHEST YIELD's (i): the spec's 4.1 and 4.3 in two sentences. |
| 2547 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " ...` | The ask's (i): the spec's T56. |
| 2551 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here - at home the auction itself, r...` | The quote's empty state: the spec's T57. |
| 2556 | `FinancesScreen.PROCEEDS_INFO` | `"A bill's ask is the cash it should raise, and its face is the lot that bring...` | Why a bill's proceeds are not the ask: the spec's T58 (a bill's ask is the cash; a bond's is the face). |
| 2562 | `FinancesScreen.BUYERS_INFO` | `"Each bidder bids by the rule it follows elsewhere in the game.The households...` | Who bids: the spec's T61 and 4.2 (CD2). |
| 2572 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "d...` | Where the dollars go: the spec's T62. |
| 2740 | `FinancesScreen.ASK_TYPED_INFO` | `"Type an amount in dollars: digits, with or without commas, a decimal " + "po...` | HOW MUCH's (i): what the box takes - the case rules - and what the buttons do. |
| 2748 | `FinancesScreen.ASK_WORDS` | `java.util.regex.Pattern.compile("(?:[A-Za-z]{0,3}\\$)?\\s*((?:\\d{1,3}(?:,\\d...` | A typed amount: an optional mark, digits (grouped by commas or not), an optional fraction, an optional unit. |
| 3172 | `FinancesScreen.STOP_INFO` | `"The one yield at which the bids first cover the issue - never under fair, ne...` | STOPS AT's (i): the stop and the tail (the spec's 4.3). |
| 3177 | `FinancesScreen.COVER_INFO` | `"What was bid up to your highest yield, each bidder counted at most the issue...` | BIDS' (i): the cover. |
| 3329 | `FinancesScreen.AuctionChart.WHO_COLOURS` | `{ Palette.PEOPLE, Palette.BUSINESS, Palette.ORE, Palette.MONEY }` | The bidders' colours in Auction.Fill's order, as the holders' bar has them. |
| 3331 | `FinancesScreen.AuctionChart.WHO` | `{ "households", "the firms", "the world", "the bank" }` | ...and their names, in the same order. |
| 3333 | `FinancesScreen.AuctionChart.H` | `240, LEFT = 62, RIGHT = 16, TOP = 16, FOOT = 26, FIRST_W = 720, LEAST_W = 420` | Its height and its margins, in pixels; the width it is first drawn at, and the least it is drawn at. |
| 3335 | `FinancesScreen.AuctionChart.SAMPLES` | `60` | How many yields the bids are read at across the axis. |
| 3523 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate an...` | The default page's (i). |
| 3664 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's...` | M2's (i): the spec's T69. |
| 3754 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no ...` | No bond outstanding: the spec's T70. |
| 3759 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has ...` | The prices' (i): the spec's T71. |
| 3764 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when ...` | The order books' (i): the spec's T74. |
| 3772 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 3774 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY...` | ...and their colours on it. |
| 3931 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since ...` | A bond's book's (i): the spec's T75, T77 and T78. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 72 | `final FundScreen fundScreen` | The city's fund's pages and a security's (0.7.39): the area is drawn by its own class. |
| 103 | `String financeArea` | Which area is open; null is the hub. |
| 106 | `String financePage` |  |
| 253 | `String borrowType` | what the borrow page is currently asking for |
| 254 | `int borrowTerm` |  |
| 255 | `double borrowAsk` |  |
| 256 | `boolean borrowHold` |  |
| 262 | `Auction.Cap borrowCap` | The highest yield the sale will take (CD2, spec-city-debt.md 4.1): fair plus a point by default, a yield the player names, or Fill it. |
| 264 | `private final CapChips borrowChips` | The Borrow page's cap chips and their yield box (CapChips), and the rollover card's own: each keeps its typing through a month's redraw. |
| 265 | `private final CapChips rolloverChips` |  |
| 267 | `private String askTyped` | What is typed in the ask's box and not yet set (0.7.40), kept through a month's redraw; null while the box is empty. |
| 269 | `private double askTypedOver` | ...the ask it was typed over: a step, a preset or an issue that moves the ask drops it. |
| 271 | `private String askRefused` | What the last entry that could not be read says, under the box until the ask is next set; null for none. |
| 273 | `private javafx.scene.control.TextField askField` | The ask's box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing (FundScreen's search box). |
| 274 | `private boolean askTyping` |  |
| 281 | `private final java.util.Set<String> open` | What is standing open - a "details" fold, by key. |
| 284 | `private Object scrollTarget` | Where the page is to be scrolled to once it is drawn: LADDER or RESCUE on the hub, a piece of paper or a calendar year on The book; null for the top. |
| 287 | `private final java.util.Map<Object, Node> targets` | The nodes a door on this tab can scroll to, by the same keys, as the page draws them. |
| 290 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 3337 | `private final Auction.Result r` |  |
| 3338 | `private final Auction.Bidders bidders` |  |
| 3339 | `private final java.util.function.DoubleFunction<String> money` |  |
| 3341 | `private double drawnAt` | The width it is drawn at now; 0 before the first. |
| 3928 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |
| 4072 | `double fundAsk` | What the player's hand is asking to pay into the fund or draw out of it, on Rules & cash (FundScreen.moveCard()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 4097 | **type** `final class FinancesScreen` | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece and what it would cost to retire, borrowing at home and abroad, the mone... |
| 63 | 7 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 74-492)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 11 | `static String[] pagesOf(String area)` | An area's pages, by its name; the position's for a name it does not know. |
| 148 | 10 | `static String[] iconsOf(String area)` | ...and their icons on the chips. |
| 180 | 3 | **type** `record Instrument(String key, String name, String short_, int[] terms, double rounding, String unit, String...` | One kind of paper the city can sell. |
| 209 | 5 | `static int nearestTerm(Instrument kit, int term)` | The one of an instrument's terms nearest this. |
| 216 | 4 | `static Instrument instrument(String key)` | An instrument by its key; the bond for a key it does not know. |
| 222 | 7 | `static Auction.Kind auctionKind(Instrument kit)` | The auction's name for an instrument's kind of paper. |
| 231 | 3 | `static int termMonths(Instrument kit, int term)` | An instrument's term as the months the auction counts it in: a bill's months, a bond's years times twelve. |
| 236 | 3 | `static String instrumentColour(String type)` | A kind's colour on the ladder, by Debt.getType(): DebtManager.LADDER_KINDS in Palette.LADDER's three. |
| 244 | 7 | `static String kindTag(String type)` | A piece's kind as its tag says it, by Debt.getType(): the keys are the model's, the words are the board's. |
| 310 | 8 | `void open(String area, String page, Object anchor)` | The tab opened on an area and a page (0.7.32, the spec's D17): the hub when `area` is null, an area's first page when `page` is not one of its own, and with `anchor` - LADDER or RESCUE on the hub, a piece of paper (a ... |
| 319 | 80 | `void showFinanceMenu()` |  |
| 401 | 16 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (GovernmentScreen's, 0.7.31). |
| 419 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 432 | 4 | `void showOnHub(String target)` | On the hub, scroll to `target` where it is; anywhere else, the hub opened on it. |
| 457 | 4 | `static String areaInfo(String area)` | An area's (i) by its name. |
| 468 | 9 | `HBox head()` | The head: "Finances" with the money blue's swatch; on an area the breadcrumb "Finances › The book", the first word a way back. |
| 479 | 13 | `static Label backDoor(String text, Runnable go)` | A door back: "‹ Finances", in the accent, underlined under the pointer (Pieces.door()'s, pointing the other way). |

### THE FIVE FIGURES (0.7.32; four until then) (lines 493-661)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 1 | **type** `record Kpi(String label, String value, String note, String tone, String where, String alarm)` | One of the five figures, worked out without drawing it: its label, its figure, its note, its colour, where its click goes, and the red line under it (null: none). |
| 512 | 63 | `List<Kpi> kpis(List<CityNeeds.Need> all)` | The five figures (pure: the probe reads them as the strip shows them). |
| 576 | 25 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 603 | 22 | `void withSpark(VBox cell, String series, String change)` | A limit cell with a year of a History series as a sparkline at the right of its figure, and a line under the note when `change` is not null (GovernmentScreen.withSpark()'s shape). |
| 627 | 13 | `String rateChange()` | THE RATE's move on last month, from History's last two points: "▲ 0.01 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). |
| 642 | 7 | `Debt soonest()` | The piece that falls due soonest, or null with nothing owed. |
| 651 | 4 | `static String urgency(int monthsOff)` | A maturity's colour, by how many months off it is: red inside FALLS DUE's line (NEEDS YOU's), amber inside FALLS_DUE_SOON_MONTHS, the headings' ink after. |
| 657 | 4 | `static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | NEEDS YOU's level for a kind: 0 green, 1 amber, 2 red, -1 not measured. |

### THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line (lines 662-730)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 37 | `List<Node> alertBands()` |  |
| 713 | 17 | `static HBox alertBand(String line, String whole, Node door)` | One alert band: a red ground, the alert icon, one line, its (i), and a door at the right (null: none). |

### the screen's own pieces (lines 731-925)

| line | len | member | says |
|---:|---:|---|---|
| 734 | 1 | `String sym()` | The city's money's mark where a foreign figure is on the tab: "D$" (the spec's D9). |
| 737 | 4 | `String d(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k" (the spec's D9, B9). |
| 743 | 4 | `String dFull(double thousands)` | ...with every digit. |
| 749 | 5 | `String signedD(double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 756 | 1 | `String perUsd(double rate)` | An exchange rate with its unit, as the land office writes it: "D$0.7035 per US$" (the spec's B10). |
| 759 | 1 | `static String pc(double share)` | A share as a whole per cent: "58%". |
| 762 | 1 | `static String pc1(double share)` | ...to one place: "1.5%". |
| 765 | 1 | `static String pts(double rate)` | Points of a rate, signed: "+0.02 pts". |
| 768 | 1 | `static String pct3(double rate)` | A yield to three places, as an auction stops: "3.602%". |
| 771 | 1 | `static String bp(double rate)` | A tail in basis points: "28 bp". |
| 774 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 783 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 791 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 802 | 9 | `static HBox caption(String text, String info, Node right)` | ...with words at its right. |
| 813 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. |
| 822 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour - red when the card is in alarm. |
| 828 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 839 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 846 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 851 | 1 | `static Label line(String text)` | A plain line of words in a card, wrapping. |
| 854 | 1 | `static Label muted(String text)` | ...muted. |
| 857 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width (the spec's D10). |
| 865 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 870 | 8 | `static GridPane pair(Node left, Node right)` | Two nodes side by side in equal columns, each as tall as the taller. |
| 880 | 7 | `static HBox swatch(String colour, String name, boolean ghost)` | A key's entry: a swatch and a name, the swatch outlined when `ghost`. |
| 889 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 894 | 3 | `static Segment ghost(double amount, String colour, String label, String tip)` | ...hollow: what would be there, or what is not. |
| 899 | 5 | `static javafx.scene.layout.FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 906 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (GovernmentScreen.openPolicy()'s). |
| 919 | 6 | `void openTrade(String area, String page)` | Trade, on the page an area names: "The currency" and "The reserves" were Trade's areas until 0.7.35 and are its pages now - TradeScreen reads tradeArea as the page, so `page` is overwritten. |

### THE HUB (0.7.32) (lines 926-951)

| line | len | member | says |
|---:|---:|---|---|
| 941 | 10 | `void hubPage(VBox page, List<CityNeeds.Need> all)` |  |

### when it falls due (lines 952-1119)

| line | len | member | says |
|---:|---:|---|---|
| 979 | 17 | `VBox ladderCard()` |  |
| 1016 | 27 | `Pieces.Columns ladderChart(DebtManager.Ladder ladder, String extraColour, String extraName, double height)` | The ladder as columns (Pieces.columns()): a column a calendar year, each kind's payments stacked from the foot - notes, serial bonds, term loans, Palette.LADDER's three - the part owed in dollars striped down its edge... |
| 1045 | 5 | `static double tallestYear(DebtManager.Ladder ladder)` | The tallest of the twelve years' columns, a proposed issue's ghost included. |
| 1052 | 4 | `static boolean laterBroken(DebtManager.Ladder ladder)` | Whether "later" is drawn broken (0.7.34): more than LATER_BREAK times the tallest year. |
| 1058 | 5 | `static double ladderScale(DebtManager.Ladder ladder)` | The ladder's scale: the tallest column - or, "later" broken, LATER_CAP tallest years, so the years fill the height. |
| 1065 | 11 | `List<Segment> rungParts(DebtManager.Rung r, String extraColour)` | A rung's segments, foot first: each kind at home, then its dollar part striped; then a proposed issue's, as a ghost. |
| 1078 | 16 | `String rungTip(DebtManager.Rung r, double revenueYear, String extraName)` | A column's tooltip: "2210 · D$579.0M: bonds D$579.0M, D$563.3M of it in dollars · 30% of a year's take" (the spec's section 3). |
| 1096 | 23 | `HBox ladderKey(DebtManager.Ladder ladder, String extraColour, String extraName)` | The ladder's key: only the kinds drawn, "in dollars" when any is, the proposed issue's ghost; at the right, everything still to pay. |

### next due (lines 1120-1197)

| line | len | member | says |
|---:|---:|---|---|
| 1137 | 26 | `VBox nextDueCard()` | NEXT DUE: the next five pieces to fall due, beside the ladder (0.7.32; a card at the top of the hub from 0.7.24, the window's foot until then). |
| 1165 | 19 | `HBox nextDueRow(Debt debt, int month)` | One piece on NEXT DUE: its amount in its urgency, its kind's tag and its date; a click opens it on The book. |
| 1186 | 11 | `String fellDueWords()` | What fell due at the last press, for the month after it (the spec's section 5): "D$272.0M fell due · rolled", off the rollover's record or, by hand, the principal the treasury repaid; null when nothing did. |

### the two settings (lines 1198-1199)

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1200-1377)

| line | len | member | says |
|---:|---:|---|---|
| 1271 | 7 | `static String rolloverCapWords(Rollover.Mode mode)` | The line under the rollover's chips: what the cap is for, and what a short sale does (D29); by hand, that nothing is sold for you. |
| 1280 | 3 | `double rolloverFair()` | What the rollover's "at …" chip starts from: the 10-year bond's fair yield today, since the rollover sells many papers and has no one fair. |
| 1284 | 36 | `VBox rolloverCard()` |  |
| 1322 | 25 | `String planWords(Rollover.Plan plan)` | NEXT MONTH in one line (the spec's T37): what falls due and where each part of it goes - the bar's parts in words, so the bar needs no key. |
| 1349 | 14 | `String surplusWords(Rollover.Plan plan)` | NEXT MONTH's (i): the year's surplus and what is used of it, and the central bank's part (the old block's lines). |
| 1369 | 8 | `String lastRollWords()` | The last rollover, in a muted line (the spec's T38), or null when none has run: dated by the month what it rolled fell due in - the month after the press it ran at, Rollover.getLastMonth(). |

### WHEN THE BANK FAILS (0.7.14) (lines 1378-1464)

| line | len | member | says |
|---:|---:|---|---|
| 1403 | 40 | `VBox rescueCard()` |  |
| 1445 | 11 | `String settingsWords()` | The rollover and the rescue, as Borrow and the fund's Rules & cash (its Holdings until 0.7.39) point at them (the spec's D3): "What falls due rolls as the same structure; a failed bank is resolved automatically." |
| 1458 | 6 | `HBox settingsPointer()` | ...as a line with its door to the hub's two cards. |

### the six areas (lines 1465-1589)

| line | len | member | says |
|---:|---:|---|---|
| 1468 | 1 | **type** `record AreaCard(String area, String figure, String line, String lineTone, String chip)` | One area's card, worked out without drawing it: its figure, its line and the line's colour, and an amber chip (null: none). |
| 1471 | 41 | `List<AreaCard> areaWords()` | The six areas' figures (pure: the probe reads them as the cards show them). |
| 1513 | 10 | `GridPane areaCards()` |  |
| 1525 | 31 | `Node areaBar(int i)` | Each area's thin bar: the credit band, the principal by kind, the bank's room, M2's parts, the bonds' holders, the fund's shares against its aim. |
| 1558 | 31 | `VBox areaCard(int i, AreaCard w, Node bar)` | One area as a card (the spec's section 3 A.3): its icon, name and (i), its figure, its line, its chip, its bar; hover lights its edge, a click opens it. |

### THE POSITION (0.7.32) (lines 1590-1774)

| line | len | member | says |
|---:|---:|---|---|
| 1606 | 5 | `void positionPage(VBox page, List<CityNeeds.Need> all)` |  |
| 1628 | 37 | `VBox balanceCard()` | THE BALANCE: the cash against what is owed, two bars on one scale, and the net position (the spec's section 3 B). |
| 1667 | 25 | `VBox creditBandCard()` | THE CREDIT BAND: the rate on the band from what a spotless city pays to what a hopeless one does, its parts the floor and the two measures (the spec's T8 cut: the band labels its ends). |
| 1694 | 9 | `String economyInfo(double ratio)` | AGAINST THE ECONOMY's (i): the spec's T9, rewritten from the market's own measure (B4). |
| 1705 | 38 | `VBox economyCard()` | AGAINST THE ECONOMY: the debt, a year of its coupon and a year of revenue, each a share of a year's output, on one scale. |
| 1745 | 29 | `VBox owedChartCard()` | OWED AND THE RATE: City History's public debt and borrowing rate on two axes, the borrowing decisions as flags (the spec's section 5). |

### DEBT SERVICE (0.7.32) (lines 1775-1906)

| line | len | member | says |
|---:|---:|---|---|
| 1790 | 4 | `void debtServicePage(VBox page)` |  |
| 1806 | 37 | `Node serviceCard()` |  |
| 1853 | 31 | `VBox lastMonthCard()` | LAST MONTH: the interest on the city's paper (the coupon paid and the discount wearing off), and the principal that fell due at the last press split as the rollover booked it. |
| 1891 | 15 | `VBox foreignCard()` | FOREIGN: a year of the dollar paper's service on a bar of a year of exports, red only when the world has shut its window. |

### HOME AND ABROAD (0.7.32) (lines 1907-2015)

| line | len | member | says |
|---:|---:|---|---|
| 1931 | 19 | `void homeAndAbroadPage(VBox page)` |  |
| 1952 | 17 | `VBox holdersCard(double all)` | WHO HOLDS IT: the households, the bank, the central bank, the businesses (CD2, slice 4) and the world, one bar, each with its amount and share. |
| 1977 | 14 | `VBox dollarDebtCard()` | THE DOLLAR DEBT: dollars times the rate is the local figure; what the currency did to it last month as a chip. |
| 2000 | 15 | `VBox behindCard()` | WHAT IS BEHIND IT: the reserves, and the import cover on a year's bar with the model's comfortable line. |

### YOUR RATE, TAKEN APART (0.7.32) (lines 2016-2263)

| line | len | member | says |
|---:|---:|---|---|
| 2037 | 19 | `void yourRatePage(VBox page)` |  |
| 2058 | 15 | `String floorInfo()` | The floor's (i): the spec's T23, rewritten (B13), and the central bank's share (0.7.15). |
| 2075 | 29 | `VBox builtUpCard()` | THE RATE, BUILT UP: the floor and the two measures on the band from nothing to the ceiling, the dial and the city's rate marked, then each part with what moves it. |
| 2106 | 9 | `static VBox rateCell(String name, String value, String moves, Node door, String colour)` | One part of the rate: its swatch and name, its figure, what moves it, and its door. |
| 2131 | 24 | `VBox curveCard()` | THE CURVE: the city's rate by maturity at home and, while the window is open, abroad, with the dial dashed and the thirty-year point taken apart. |
| 2162 | 64 | **type** `static final class CurveChart extends javafx.scene.layout.Pane` | The curve, drawn: a point a maturity at equal steps, the city's in the money blue, the world's in violet, the dial dashed across, and the point at `marked` annotated with `note` - a fixed size, as a card's picture is;... |
| 2166 | 41 | `CurveChart(double[] home, double[] abroad, double dial, int marked, String note)` _(in FinancesScreen.CurveChart)_ |  |
| 2208 | 17 | `private void draw(double[] values, java.util.function.IntToDoubleFunction x, java.util.function.DoubleUnaryOperator y, String c...` _(in FinancesScreen.CurveChart)_ |  |
| 2228 | 6 | `static String measuresInfo()` | WHAT EACH MEASURE HAS USED's (i): the spec's T25. |
| 2236 | 15 | `VBox measuresCard()` | WHAT EACH MEASURE HAS USED: how much of its worst case each of the two measures has used, and a default's scar abroad. |
| 2253 | 10 | `static VBox stressRow(String label, double stress)` | One measure: its name, a bar of how much of its worst case it has used, and the share. |

### THE BOOK (0.7.32) (lines 2264-2299)

| line | len | member | says |
|---:|---:|---|---|
| 2282 | 12 | `String bookInfo()` | The book's (i): the spec's T32 and T34. |

### the books (CD3) (lines 2300-2490)

| line | len | member | says |
|---:|---:|---|---|
| 2316 | 27 | `void bookPage(VBox page)` |  |
| 2349 | 9 | `static String couponWords(double coupon)` | A coupon in eighths of a point, the way a bond is quoted: 0.035 is "3½%", 0.04125 "4⅛%", 0.00125 "⅛%". |
| 2366 | 14 | `static String pieceName(Debt debt)` | A piece's name in words (CD2): a bond at home is its line, "3½% Jan 2211" - its coupon in eighths and the month and year it falls due, as a treasury names a line -; a bill "6-month bill"; an amortizing bond "20-year a... |
| 2382 | 80 | `VBox pieceCard(Debt debt)` | One piece as a card (the spec's section 3 C). |
| 2464 | 26 | `Node bookGrid(List<Debt> paper)` | The book as the old page's table, in the fold: kind, owed (face), carried and effective rate (CD3), coupon a month, when it matures, how far off. |

### BORROW (0.7.32) (lines 2491-2725)

| line | len | member | says |
|---:|---:|---|---|
| 2576 | 49 | `void borrowPage(VBox page, boolean foreign)` |  |
| 2632 | 67 | `VBox askCard(Instrument kit, boolean foreign, DebtQuote quote, double fair)` | THE ASK: the kinds as tiles, the terms as columns of today's fair yield (chips abroad), how much, and at home the highest yield the city will take. |
| 2706 | 19 | `String lineNote(Instrument kit)` | What the term's line is, in a sentence, under the columns (the board's bw-line): a bond sold into the line it reopens, or opening a new one; a bill or an amortizing bond is always its own. |

### the highest yield (CD2) (lines 2726-2736)

| line | len | member | says |
|---:|---:|---|---|
| 2733 | 3 | `List<Node> capControls(double fair)` | THE HIGHEST YIELD: fair + a point (the default), a yield the player types in per cent, or Fill it - the cap chips (CapChips), bound to the Borrow page's own cap. |

### the ask, typed (0.7.40) (lines 2737-3168)

| line | len | member | says |
|---:|---:|---|---|
| 2752 | 14 | `static double askFromWords(String typed)` | The ask a typed amount sets, in the model's thousands (Money.toDollars()'s unit), by ASK_TYPED_INFO's rules; NaN when it is not an amount. |
| 2768 | 4 | `static String askRefusal(String typed)` | What the box says under itself when an entry cannot be read. |
| 2774 | 6 | `String[] askShown(double ask, boolean foreign)` | The ask written in full and short: {"D$2,500,000,000,000", "D$2.5T"} - the second null when it would say the same; nothing asked, {"nothing asked for yet", null}. |
| 2782 | 6 | `static double[] askSteps(double ask, double lot)` | The ± steps for an ask: one unit of its leading digit and a tenth of that - D$1B and D$100M at D$3.4B - never under a lot; at nothing, one lot. |
| 2790 | 4 | `double askBase()` | What a step or a scale starts from: what is typed and not yet set, when it reads as an amount, else the ask. |
| 2796 | 7 | `void setAsk(double ask)` | The ask set by a step, a preset or clear: what was typed and the refusal go, and the page is drawn on it. |
| 2805 | 1 | **type** `record AskPreset(String name, double ask)` | One preset of the ask: what it is called, and the model's figure it sets. |
| 2819 | 16 | `List<AskPreset> askPresets(Instrument kit, boolean foreign)` | The ask's presets, every figure a model getter's, each offered only when it is something: at home, the minimum issue (at least a lot, as "the minimum" always set it), what falls due in the next twelve months (DebtMana... |
| 2843 | 27 | `Node askBox(boolean foreign)` | The box the ask is typed in (0.7.40), the house's search box: what is typed and not yet set lives through a month's redraw, and the focus with it. |
| 2878 | 25 | `private void commitAsk(javafx.scene.control.TextField box, boolean enter)` | What is in the box, set as the ask - or, when it cannot be read, said under the box with the ask left as it was. |
| 2905 | 3 | `static String abroadLine(Instrument kit)` | A kind's tile line abroad: dollar paper is the world's, at its price, not an auction's (spec-city-debt.md 2.7). |
| 2910 | 7 | `static String abroadBlurb(Instrument kit)` | ...and its (i): what dollar paper is. |
| 2919 | 25 | `VBox instrumentTile(Instrument kit, boolean on, boolean foreign)` | One instrument as a tile: its swatch-tinted icon, its name and (i), the range it is issued over, its line; picked, its ground and edge lit. |
| 2954 | 30 | `Node termColumns(Instrument kit, DebtQuote quote)` | The terms at home as columns of today's fair yield (DebtManager. |
| 2986 | 21 | `Node termChips(Instrument kit)` | The terms abroad as chips, each with the world's rate for it in its tooltip. |
| 3009 | 8 | `String quoteName(Instrument kit, boolean foreign)` | What the quoted paper is called on its card: "10-year bond", "6-month bill", "20-year amortizing bond", "... |
| 3023 | 42 | `VBox quoteCard(Instrument kit, DebtQuote quote, boolean foreign)` | THE QUOTE: the offer card on exactly the terms the button books. |
| 3075 | 77 | `VBox auctionQuote(Instrument kit, DebtQuote quote, Auction.Result sale)` | THE QUOTE at home (CD2): the offer card of a sale at auction, in the land office's shape - the paper's name and (i), what the sale settles at, the face, the cash, the monthly cost and the cost all in, when it falls du... |
| 3154 | 8 | `String settlesAt(Instrument kit, Auction.Result r)` | What a sale settles at, in a line: a bill's yield and its price for 100; a bond's coupon, line and yield; the amortizing bond's one coupon. |
| 3164 | 4 | `static String tailTone(Auction.Result r)` | The tail's colour: none is good, some is amber, the highest yield reached is red. |

### the auction, shown (CD2) (lines 3169-3563)

| line | len | member | says |
|---:|---:|---|---|
| 3186 | 54 | `VBox auctionCard(Instrument kit, Auction.Result r)` | THE AUCTION: the four bids against yield with the issue, fair and the highest yield drawn (AuctionChart), then the result line by line, and the bank's room. |
| 3242 | 3 | `HBox fillLine(String who, double face, double sold)` | One bidder's fill: its face, and its share of what was sold. |
| 3252 | 24 | `Node shortCard(Auction.Result r)` | THE RED CARD (the spec's section 7, board 4): a sale the bids would not cover. |
| 3283 | 30 | `List<Node> bankRoomRows(Auction.Result r)` | THE BANK'S ROOM, under the auction (what WHO BUYS IT's bar was, before CD2): its book, risk-weighted, against its room, with what the bank takes of this sale as a ghost; or why it bids nothing - failed, or no room. |
| 3327 | 170 | **type** `static final class AuctionChart extends javafx.scene.layout.Pane` | The four bids against yield, drawn (board 4): the households, the firms, the world and the bank stacked from the foot, so the top edge is the four summed, over a yield axis from fair less a fifth of a point to the hig... |
| 3343 | 9 | `AuctionChart(Auction.Result r, Auction.Bidders bidders, java.util.function.DoubleFunction<String> money)` _(in FinancesScreen.AuctionChart)_ |  |
| 3353 | 5 | `protected void layoutChildren()` _(in FinancesScreen.AuctionChart)_ |  |
| 3360 | 109 | `private void draw(double w)` _(in FinancesScreen.AuctionChart)_ | The chart at a width: everything in it, drawn again from the sale. |
| 3471 | 7 | `private void vertical(double x, String colour, double from, double to)` _(in FinancesScreen.AuctionChart)_ | A dashed vertical line at x, in a colour, between two heights. |
| 3484 | 12 | `private void label(String text, double x, double y, javafx.geometry.Pos align, double wide, String colour)` _(in FinancesScreen.AuctionChart)_ | A label of the chart's: `x` is where the text starts, ends or centres by `align`, in a box `wide` pixels across (a Pane gives its children their preferred size, so the box is the alignment's room). |
| 3499 | 10 | `VBox dollarsCard()` | AND THE DOLLARS: convert and spend, or hold as reserves (the spec's W8). |
| 3511 | 10 | `HBox foreignDoor()` | The door marked do not open, outlined in red. |
| 3533 | 30 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out: "Finances › Default abroad" (0.7.32, the spec's D14), two equal cards in the same type, "Keep paying" first. |

### MONEY (0.7.0; redrawn 0.7.32) (lines 3564-3733)

| line | len | member | says |
|---:|---:|---|---|
| 3579 | 16 | `String moneyInfo()` | The page's (i): the old page's two sentences, the spec's T64 to T66. |
| 3596 | 15 | `void moneyPage(VBox page)` |  |
| 3613 | 15 | `String centralBookInfo(CentralBank cb)` | The book's (i): the spec's T67 and T68. |
| 3630 | 32 | `VBox centralBookCard(CentralBank cb)` | THE CENTRAL BANK'S BOOK: what it holds against what it owes, one scale; its equity on the owing side, or a shortfall red on the holding side. |
| 3669 | 17 | `VBox publicCard(CentralBank cb)` | WHAT THE PUBLIC HOLDS: M2 as a bar of its parts, and M0 beside it. |
| 3688 | 28 | `VBox thisMonthCard(CentralBank cb)` | THIS MONTH: the money made and the money destroyed, each a bar of its parts on one scale, and what M0 moved by. |
| 3718 | 15 | `VBox smallChart(String title, HistorySave h, String[] keys, String[] names, String[] colours)` | History's money series as a small chart without the controls, on the last ten years (ChartModel.DEFAULT_RANGE) or the whole history while it is younger - TimeChart, as Government's Output draws it. |

### THE BOND MARKET (0.7.12; redrawn 0.7.32) (lines 3734-4059)

| line | len | member | says |
|---:|---:|---|---|
| 3749 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 3777 | 14 | `Node holdersBar(double[] held, double band, boolean key)` | A holders bar over five amounts, each keyed when `key` is set. |
| 3792 | 32 | `void bondMarketPage(VBox page)` |  |
| 3826 | 24 | `VBox issuerCard(BondMarket.Issuer is)` | One issuer as a card: its sector's icon, its face, its bonds and their coupons, its nearest maturity, its holders; a click opens its largest bond's book. |
| 3852 | 25 | `VBox bondMonthCard(BondMarket market)` | THIS MONTH on the bond market: sold, coupons, repaid, written off, and the last issue. |
| 3879 | 14 | `VBox orderBooksCard(BondMarket market)` | THE ORDER BOOKS, last month: offered for sale, how much of it sold, and how many sellers waited. |
| 3895 | 31 | `Node bondGrids(BondMarket market)` | The old page's two tables, in the fold: every bond with its price and yield, and who holds each. |
| 3936 | 46 | `void bondBookPage(VBox page)` |  |
| 3984 | 17 | `Node depthChart(OrderBook book, CorporateBond b, int month)` | The book's depth: a row a price level, the asks over the bids, best nearest the middle; bids drawn leftward, asks rightward, face as length. |
| 4003 | 28 | `Node depthRow(OrderBook.Level l, boolean bid, double most, CorporateBond b, int month)` | One price level of the depth chart. |
| 4033 | 26 | `Node levelGrids(OrderBook book, CorporateBond b, int month)` | The old page's bid and ask tables, in the fold. |

### THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) (lines 4060-4073)

### THE DEBT RESULT (lines 4074-4154)

| line | len | member | says |
|---:|---:|---|---|
| 4093 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding, Auction.Cap cap)` | Books the sale the page quoted, at the highest yield it was quoted at (CD2): the handler struck on the same city at the same cap. |
| 4103 | 51 | `void showDebtResultMenu(DebtQuote quote, boolean foreign, String summary)` | Shows the terms the player just agreed to: the quote booked as a receipt, and the ladder with it on the books; or what the booking said when nothing was booked. |


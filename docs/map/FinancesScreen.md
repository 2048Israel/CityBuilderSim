# FinancesScreen.java - 3,220 lines · 133 methods · 61 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (439), [DebtManager](DebtManager.md) (53), [CityCalendar](CityCalendar.md) (29), [CityNeeds](CityNeeds.md) (26), [Game](Game.md) (26), [Icons](Icons.md) (17), [Rollover](Rollover.md) (17), [Debt](Debt.md) (15), [CorporateBond](CorporateBond.md) (14), [OrderBook](OrderBook.md) (14), [HistoryScreen](HistoryScreen.md) (10), [BondMarket](BondMarket.md) (9), [Ladder](Ladder.md) (8), [Bank](Bank.md) (8), [TimeChart](TimeChart.md) (8), [FundScreen](FundScreen.md) (7), [CentralBank](CentralBank.md) (7), [TreasuryFund](TreasuryFund.md) (6), [ChartModel](ChartModel.md) (5), [ForeignAccounts](ForeignAccounts.md) (5), [DebtQuote](DebtQuote.md) (5), [HistorySave](HistorySave.md) (4), [Currency](Currency.md) (4), [NationalAccounts](NationalAccounts.md) (3), [BuildScreen](BuildScreen.md) (3), [Pieces](Pieces.md) (3), [UserInterface](UserInterface.md) (2), [TradeScreen](TradeScreen.md) (2), [Money](Money.md) (2), [HouseholdBalance](HouseholdBalance.md) (2)... and 7 more

**Used by (5):** [BankScreen](BankScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 71 | FINANCES |
| 426 | · THE FIVE FIGURES (0.7.32; four until then) |
| 595 | · THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line |
| 664 | · the screen's own pieces |
| 853 | THE HUB (0.7.32) |
| 879 | · when it falls due |
| 1047 | · next due |
| 1125 | · the two settings |
| 1127 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1272 | · WHEN THE BANK FAILS (0.7.14) |
| 1359 | · the six areas |
| 1483 | THE POSITION (0.7.32) |
| 1652 | DEBT SERVICE (0.7.32) |
| 1775 | HOME AND ABROAD (0.7.32) |
| 1878 | YOUR RATE, TAKEN APART (0.7.32) |
| 2113 | THE BOOK (0.7.32) |
| 2289 | BORROW (0.7.32) |
| 2639 | MONEY (0.7.0; redrawn 0.7.32) |
| 2809 | THE BOND MARKET (0.7.12; redrawn 0.7.32) |
| 3135 | THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) |
| 3149 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 102 | `FinancesScreen.FINANCE_HOME` | `"Overview"` | The page an area opens on when none is named: the first of any area's pages that is called this. |
| 106 | `FinancesScreen.AREAS` | `{ "The position", "The book", "Borrow", "Money", "The bond market", "The city...` | The six areas, in the hub's order. |
| 108 | `FinancesScreen.AREA_ICONS` | `{ Icons.FINANCES, Icons.PAPER, Icons.COIN, Icons.BANKNOTE, Icons.REPORTS, Ico...` | ...and each one's icon, on its card. |
| 111 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "Debt service", "Home & abroad", "Your rate" }` | The position's four pages (0.7.32: "The ladder" became the hub's hero). |
| 113 | `FinancesScreen.POSITION_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.TRADE, Icons.POLICY }` | ...and their icons on the chips. |
| 115 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece" }` | The book is one page since 0.7.32: Buy back is on each piece's card. |
| 117 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` | Borrow's two pages: the city's own paper at home, and dollars from the world. |
| 119 | `FinancesScreen.BORROW_ICONS` | `{ Icons.HOMES, Icons.TRADE }` | ...and their icons on the chips. |
| 121 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 123 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 125 | `FinancesScreen.BOND_ICONS` | `{ Icons.REPORTS, Icons.PAPER }` | ...and their icons on the chips. |
| 127 | `FinancesScreen.FUND_PAGES` | `FundScreen.PAGES` | The city's fund as a brokerage (0.7.39; "Holdings" and "By hand" from 0.7.14): FundScreen's four pages. |
| 129 | `FinancesScreen.FUND_ICONS` | `FundScreen.ICONS` | ...and their icons on the chips. |
| 178 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Notes", "NOTE", 3, 12, 1, 1000, "months", "No coupo...` | The three kinds of paper, short to long, in the ladder's order: the note, the serial bond and the term loan. |
| 205 | `FinancesScreen.KIND_NAMES` | `{ "Notes", "Serial bonds", "Term loans" }` | The ladder's three kinds, as the key and a tooltip say them. |
| 230 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 233 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.32; Go... |
| 236 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same... |
| 371 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "Th...` | The tab's (i): what the hub is, in a breath. |
| 377 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balan...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 641 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " +...` | What resolving a failed bank does (the spec's T41). |
| 882 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, "...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 892 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " ...` | The ladder with nothing on it (the spec's T11). |
| 896 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "ref...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 901 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 904 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1141 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month notes" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1144 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't...` | Each setting's one line (the spec's T35). |
| 1152 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and ...` | ...and each in full, on its chip (the old block's sentences). |
| 1163 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's ...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1281 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1284 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays froze...` | Each setting's one line (the spec's T40). |
| 1290 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose ...` | The rescue's (i): both settings in full (the old block's T40). |
| 1506 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to ...` | THE BALANCE's (i): the spec's T7. |
| 1673 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 1676 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 1722 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the coupon is an expense; the principal is a balance-sheet movement." +...` | LAST MONTH's (i): the spec's T14. |
| 1754 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city ...` | FOREIGN's (i): the spec's T16. |
| 1785 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none o...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 1833 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the "...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 1855 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports "...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 1973 | `FinancesScreen.CURVE_MONTHS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The curve's maturities, in months. |
| 1975 | `FinancesScreen.CURVE_NAMES` | `{ "3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "40y", "50y" }` | ...and how they are written under it. |
| 1978 | `FinancesScreen.CURVE_INFO` | `"The note is the floor - the dial, or what the bank's money costs it, whichev...` | THE CURVE's (i): the spec's T24, and the world's curve. |
| 2013 | `FinancesScreen.CurveChart.W` | `720, H = 190, LEFT = 44, RIGHT = 16, TOP = 26, FOOT = 22` | Its size and its margins, in pixels: a card's picture, at a fixed size. |
| 2145 | `FinancesScreen.NOTE_INFO` | `"No coupon at all - the lender's return was the discount, taken out of the pr...` | A note's (i): the spec's T28. |
| 2149 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, ...` | A price against face, in words (the spec's T33, D16). |
| 2309 | `FinancesScreen.TERMS_INFO` | `"Each column is the rate this paper would cost at that term today - for the "...` | The terms' (i): the spec's T55. |
| 2316 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " ...` | The ask's (i): the spec's T56. |
| 2320 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here, with the ladder it would build...` | The quote's empty state: the spec's T57. |
| 2324 | `FinancesScreen.PROCEEDS_INFO` | `"Paper is sold in lots and the face is grossed up for the discount, so the " ...` | Why the proceeds are not the ask: the spec's T58. |
| 2329 | `FinancesScreen.BUYERS_INFO` | `"The households first, when it pays them more than the bank does: up to %s of...` | Who buys it: the spec's T61, its first half. |
| 2336 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "d...` | Where the dollars go: the spec's T62. |
| 2598 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate an...` | The default page's (i). |
| 2739 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's...` | M2's (i): the spec's T69. |
| 2829 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no ...` | No bond outstanding: the spec's T70. |
| 2834 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has ...` | The prices' (i): the spec's T71. |
| 2839 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when ...` | The order books' (i): the spec's T74. |
| 2847 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 2849 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY...` | ...and their colours on it. |
| 3006 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since ...` | A bond's book's (i): the spec's T75, T77 and T78. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 69 | `final FundScreen fundScreen` | The city's fund's pages and a security's (0.7.39): the area is drawn by its own class. |
| 100 | `String financeArea` | Which area is open; null is the hub. |
| 103 | `String financePage` |  |
| 208 | `String borrowType` | what the borrow page is currently asking for |
| 209 | `int borrowTerm` |  |
| 210 | `double borrowAsk` |  |
| 211 | `boolean borrowHold` |  |
| 218 | `private final java.util.Set<String> open` | What is standing open - a "details" fold, by key. |
| 221 | `private Object scrollTarget` | Where the page is to be scrolled to once it is drawn: LADDER or RESCUE on the hub, a piece of paper or a calendar year on The book; null for the top. |
| 224 | `private final java.util.Map<Object, Node> targets` | The nodes a door on this tab can scroll to, by the same keys, as the page draws them. |
| 227 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 3003 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |
| 3147 | `double fundAsk` | What the player's hand is asking to pay into the fund or draw out of it, on Rules & cash (FundScreen.moveCard()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 3163 | **type** `final class FinancesScreen` | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece and what it would cost to retire, borrowing at home and abroad, the mone... |
| 63 | 4 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 71-425)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 11 | `static String[] pagesOf(String area)` | An area's pages, by its name; the position's for a name it does not know. |
| 145 | 10 | `static String[] iconsOf(String area)` | ...and their icons on the chips. |
| 173 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 194 | 4 | `static Instrument instrument(String key)` |  |
| 200 | 3 | `static String instrumentColour(String type)` | A kind's colour on the ladder, by Debt.getType(): DebtManager.LADDER_KINDS in Palette.LADDER's three. |
| 247 | 8 | `void open(String area, String page, Object anchor)` | The tab opened on an area and a page (0.7.32, the spec's D17): the hub when `area` is null, an area's first page when `page` is not one of its own, and with `anchor` - LADDER or RESCUE on the hub, a piece of paper (a ... |
| 256 | 76 | `void showFinanceMenu()` |  |
| 334 | 16 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (GovernmentScreen's, 0.7.31). |
| 352 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 365 | 4 | `void showOnHub(String target)` | On the hub, scroll to `target` where it is; anywhere else, the hub opened on it. |
| 390 | 4 | `static String areaInfo(String area)` | An area's (i) by its name. |
| 401 | 9 | `HBox head()` | The head: "Finances" with the money blue's swatch; on an area the breadcrumb "Finances › The book", the first word a way back. |
| 412 | 13 | `static Label backDoor(String text, Runnable go)` | A door back: "‹ Finances", in the accent, underlined under the pointer (Pieces.door()'s, pointing the other way). |

### THE FIVE FIGURES (0.7.32; four until then) (lines 426-594)

| line | len | member | says |
|---:|---:|---|---|
| 442 | 1 | **type** `record Kpi(String label, String value, String note, String tone, String where, String alarm)` | One of the five figures, worked out without drawing it: its label, its figure, its note, its colour, where its click goes, and the red line under it (null: none). |
| 445 | 63 | `List<Kpi> kpis(List<CityNeeds.Need> all)` | The five figures (pure: the probe reads them as the strip shows them). |
| 509 | 25 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 536 | 22 | `void withSpark(VBox cell, String series, String change)` | A limit cell with a year of a History series as a sparkline at the right of its figure, and a line under the note when `change` is not null (GovernmentScreen.withSpark()'s shape). |
| 560 | 13 | `String rateChange()` | THE RATE's move on last month, from History's last two points: "▲ 0.01 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). |
| 575 | 7 | `Debt soonest()` | The piece that falls due soonest, or null with nothing owed. |
| 584 | 4 | `static String urgency(int monthsOff)` | A maturity's colour, by how many months off it is: red inside FALLS DUE's line (NEEDS YOU's), amber inside FALLS_DUE_SOON_MONTHS, the headings' ink after. |
| 590 | 4 | `static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | NEEDS YOU's level for a kind: 0 green, 1 amber, 2 red, -1 not measured. |

### THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line (lines 595-663)

| line | len | member | says |
|---:|---:|---|---|
| 602 | 37 | `List<Node> alertBands()` |  |
| 646 | 17 | `static HBox alertBand(String line, String whole, Node door)` | One alert band: a red ground, the alert icon, one line, its (i), and a door at the right (null: none). |

### the screen's own pieces (lines 664-852)

| line | len | member | says |
|---:|---:|---|---|
| 667 | 1 | `String sym()` | The city's money's mark where a foreign figure is on the tab: "D$" (the spec's D9). |
| 670 | 4 | `String d(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k" (the spec's D9, B9). |
| 676 | 4 | `String dFull(double thousands)` | ...with every digit. |
| 682 | 5 | `String signedD(double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 689 | 1 | `String perUsd(double rate)` | An exchange rate with its unit, as the land office writes it: "D$0.7035 per US$" (the spec's B10). |
| 692 | 1 | `static String pc(double share)` | A share as a whole per cent: "58%". |
| 695 | 1 | `static String pc1(double share)` | ...to one place: "1.5%". |
| 698 | 1 | `static String pts(double rate)` | Points of a rate, signed: "+0.02 pts". |
| 701 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 710 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 718 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 729 | 9 | `static HBox caption(String text, String info, Node right)` | ...with words at its right. |
| 740 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. |
| 749 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour - red when the card is in alarm. |
| 755 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 766 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 773 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 778 | 1 | `static Label line(String text)` | A plain line of words in a card, wrapping. |
| 781 | 1 | `static Label muted(String text)` | ...muted. |
| 784 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width (the spec's D10). |
| 792 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 797 | 8 | `static GridPane pair(Node left, Node right)` | Two nodes side by side in equal columns, each as tall as the taller. |
| 807 | 7 | `static HBox swatch(String colour, String name, boolean ghost)` | A key's entry: a swatch and a name, the swatch outlined when `ghost`. |
| 816 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 821 | 3 | `static Segment ghost(double amount, String colour, String label, String tip)` | ...hollow: what would be there, or what is not. |
| 826 | 5 | `static javafx.scene.layout.FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 833 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (GovernmentScreen.openPolicy()'s). |
| 846 | 6 | `void openTrade(String area, String page)` | Trade, on the page an area names: "The currency" and "The reserves" were Trade's areas until 0.7.35 and are its pages now - TradeScreen reads tradeArea as the page, so `page` is overwritten. |

### THE HUB (0.7.32) (lines 853-878)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 10 | `void hubPage(VBox page, List<CityNeeds.Need> all)` |  |

### when it falls due (lines 879-1046)

| line | len | member | says |
|---:|---:|---|---|
| 906 | 17 | `VBox ladderCard()` |  |
| 943 | 27 | `Pieces.Columns ladderChart(DebtManager.Ladder ladder, String extraColour, String extraName, double height)` | The ladder as columns (Pieces.columns()): a column a calendar year, each kind's payments stacked from the foot - notes, serial bonds, term loans, Palette.LADDER's three - the part owed in dollars striped down its edge... |
| 972 | 5 | `static double tallestYear(DebtManager.Ladder ladder)` | The tallest of the twelve years' columns, a proposed issue's ghost included. |
| 979 | 4 | `static boolean laterBroken(DebtManager.Ladder ladder)` | Whether "later" is drawn broken (0.7.34): more than LATER_BREAK times the tallest year. |
| 985 | 5 | `static double ladderScale(DebtManager.Ladder ladder)` | The ladder's scale: the tallest column - or, "later" broken, LATER_CAP tallest years, so the years fill the height. |
| 992 | 11 | `List<Segment> rungParts(DebtManager.Rung r, String extraColour)` | A rung's segments, foot first: each kind at home, then its dollar part striped; then a proposed issue's, as a ghost. |
| 1005 | 16 | `String rungTip(DebtManager.Rung r, double revenueYear, String extraName)` | A column's tooltip: "2210 · D$579.0M: term loans D$579.0M, D$563.3M of it in dollars · 30% of a year's take" (the spec's section 3). |
| 1023 | 23 | `HBox ladderKey(DebtManager.Ladder ladder, String extraColour, String extraName)` | The ladder's key: only the kinds drawn, "in dollars" when any is, the proposed issue's ghost; at the right, everything still to pay. |

### next due (lines 1047-1124)

| line | len | member | says |
|---:|---:|---|---|
| 1064 | 26 | `VBox nextDueCard()` | NEXT DUE: the next five pieces to fall due, beside the ladder (0.7.32; a card at the top of the hub from 0.7.24, the window's foot until then). |
| 1092 | 19 | `HBox nextDueRow(Debt debt, int month)` | One piece on NEXT DUE: its amount in its urgency, its kind's tag and its date; a click opens it on The book. |
| 1113 | 11 | `String fellDueWords()` | What fell due at the last press, for the month after it (the spec's section 5): "D$272.0M fell due · rolled", off the rollover's record or, by hand, the principal the treasury repaid; null when nothing did. |

### the two settings (lines 1125-1126)

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1127-1271)

| line | len | member | says |
|---:|---:|---|---|
| 1181 | 33 | `VBox rolloverCard()` |  |
| 1216 | 25 | `String planWords(Rollover.Plan plan)` | NEXT MONTH in one line (the spec's T37): what falls due and where each part of it goes - the bar's parts in words, so the bar needs no key. |
| 1243 | 14 | `String surplusWords(Rollover.Plan plan)` | NEXT MONTH's (i): the year's surplus and what is used of it, and the central bank's part (the old block's lines). |
| 1263 | 8 | `String lastRollWords()` | The last rollover, in a muted line (the spec's T38), or null when none has run: dated by the month what it rolled fell due in - the month after the press it ran at, Rollover.getLastMonth(). |

### WHEN THE BANK FAILS (0.7.14) (lines 1272-1358)

| line | len | member | says |
|---:|---:|---|---|
| 1297 | 40 | `VBox rescueCard()` |  |
| 1339 | 11 | `String settingsWords()` | The rollover and the rescue, as Borrow and the fund's Rules & cash (its Holdings until 0.7.39) point at them (the spec's D3): "What falls due rolls as the same structure; a failed bank is resolved automatically." |
| 1352 | 6 | `HBox settingsPointer()` | ...as a line with its door to the hub's two cards. |

### the six areas (lines 1359-1482)

| line | len | member | says |
|---:|---:|---|---|
| 1362 | 1 | **type** `record AreaCard(String area, String figure, String line, String lineTone, String chip)` | One area's card, worked out without drawing it: its figure, its line and the line's colour, and an amber chip (null: none). |
| 1365 | 40 | `List<AreaCard> areaWords()` | The six areas' figures (pure: the probe reads them as the cards show them). |
| 1406 | 10 | `GridPane areaCards()` |  |
| 1418 | 31 | `Node areaBar(int i)` | Each area's thin bar: the credit band, the principal by kind, the bank's room, M2's parts, the bonds' holders, the fund's shares against its aim. |
| 1451 | 31 | `VBox areaCard(int i, AreaCard w, Node bar)` | One area as a card (the spec's section 3 A.3): its icon, name and (i), its figure, its line, its chip, its bar; hover lights its edge, a click opens it. |

### THE POSITION (0.7.32) (lines 1483-1651)

| line | len | member | says |
|---:|---:|---|---|
| 1499 | 5 | `void positionPage(VBox page, List<CityNeeds.Need> all)` |  |
| 1513 | 29 | `VBox balanceCard()` | THE BALANCE: the cash against what is owed, two bars on one scale, and the net position (the spec's section 3 B). |
| 1544 | 25 | `VBox creditBandCard()` | THE CREDIT BAND: the rate on the band from what a spotless city pays to what a hopeless one does, its parts the floor and the two measures (the spec's T8 cut: the band labels its ends). |
| 1571 | 9 | `String economyInfo(double ratio)` | AGAINST THE ECONOMY's (i): the spec's T9, rewritten from the market's own measure (B4). |
| 1582 | 38 | `VBox economyCard()` | AGAINST THE ECONOMY: the debt, a year of its coupon and a year of revenue, each a share of a year's output, on one scale. |
| 1622 | 29 | `VBox owedChartCard()` | OWED AND THE RATE: City History's public debt and borrowing rate on two axes, the borrowing decisions as flags (the spec's section 5). |

### DEBT SERVICE (0.7.32) (lines 1652-1774)

| line | len | member | says |
|---:|---:|---|---|
| 1667 | 4 | `void debtServicePage(VBox page)` |  |
| 1683 | 37 | `Node serviceCard()` |  |
| 1728 | 24 | `VBox lastMonthCard()` | LAST MONTH: the coupon, and the principal that fell due at the last press split as the rollover booked it. |
| 1759 | 15 | `VBox foreignCard()` | FOREIGN: a year of the dollar paper's service on a bar of a year of exports, red only when the world has shut its window. |

### HOME AND ABROAD (0.7.32) (lines 1775-1877)

| line | len | member | says |
|---:|---:|---|---|
| 1794 | 19 | `void homeAndAbroadPage(VBox page)` |  |
| 1815 | 16 | `VBox holdersCard(double all)` | WHO HOLDS IT: the households, the bank, the central bank and the world, one bar, each with its amount and share. |
| 1839 | 14 | `VBox dollarDebtCard()` | THE DOLLAR DEBT: dollars times the rate is the local figure; what the currency did to it last month as a chip. |
| 1862 | 15 | `VBox behindCard()` | WHAT IS BEHIND IT: the reserves, and the import cover on a year's bar with the model's comfortable line. |

### YOUR RATE, TAKEN APART (0.7.32) (lines 1878-2112)

| line | len | member | says |
|---:|---:|---|---|
| 1893 | 19 | `void yourRatePage(VBox page)` |  |
| 1914 | 15 | `String floorInfo()` | The floor's (i): the spec's T23, rewritten (B13), and the central bank's share (0.7.15). |
| 1931 | 29 | `VBox builtUpCard()` | THE RATE, BUILT UP: the floor and the two measures on the band from nothing to the ceiling, the dial and the city's rate marked, then each part with what moves it. |
| 1962 | 9 | `static VBox rateCell(String name, String value, String moves, Node door, String colour)` | One part of the rate: its swatch and name, its figure, what moves it, and its door. |
| 1985 | 19 | `VBox curveCard()` | THE CURVE: the city's rate by maturity at home and, while the window is open, abroad, with the dial dashed and the thirty-year point taken apart. |
| 2011 | 64 | **type** `static final class CurveChart extends javafx.scene.layout.Pane` | The curve, drawn: a point a maturity at equal steps, the city's in the money blue, the world's in violet, the dial dashed across, and the point at `marked` annotated with `note` - a fixed size, as a card's picture is;... |
| 2015 | 41 | `CurveChart(double[] home, double[] abroad, double dial, int marked, String note)` _(in FinancesScreen.CurveChart)_ |  |
| 2057 | 17 | `private void draw(double[] values, java.util.function.IntToDoubleFunction x, java.util.function.DoubleUnaryOperator y, String c...` _(in FinancesScreen.CurveChart)_ |  |
| 2077 | 6 | `static String measuresInfo()` | WHAT EACH MEASURE HAS USED's (i): the spec's T25. |
| 2085 | 15 | `VBox measuresCard()` | WHAT EACH MEASURE HAS USED: how much of its worst case each of the two measures has used, and a default's scar abroad. |
| 2102 | 10 | `static VBox stressRow(String label, double stress)` | One measure: its name, a bar of how much of its worst case it has used, and the share. |

### THE BOOK (0.7.32) (lines 2113-2288)

| line | len | member | says |
|---:|---:|---|---|
| 2131 | 12 | `String bookInfo()` | The book's (i): the spec's T32 and T34. |
| 2153 | 26 | `void bookPage(VBox page)` |  |
| 2181 | 9 | `static String pieceName(Debt debt)` | A piece's name in words: "6-month note", "5-year serial bond", "20-year term loan", "... |
| 2192 | 71 | `VBox pieceCard(Debt debt)` | One piece as a card (the spec's section 3 C). |
| 2265 | 23 | `Node bookGrid(List<Debt> paper)` | The book as the old page's table, in the fold: kind, owed, coupon a month, when it matures, how far off. |

### BORROW (0.7.32) (lines 2289-2638)

| line | len | member | says |
|---:|---:|---|---|
| 2340 | 35 | `void borrowPage(VBox page, boolean foreign)` |  |
| 2377 | 44 | `VBox askCard(Instrument kit, boolean foreign)` | THE ASK: the three instruments, the terms as columns of their rate (chips abroad), and how much. |
| 2423 | 23 | `VBox instrumentTile(Instrument kit, boolean on)` | One instrument as a tile: its swatch-tinted icon, its name and (i), the range it is issued over, its line; picked, its ground and edge lit. |
| 2448 | 18 | `Node termColumns(Instrument kit)` | The terms at home as columns of their rate: the chosen in the money blue, the rest grey; a click picks one. |
| 2468 | 21 | `Node termChips(Instrument kit)` | The terms abroad as chips, each with the world's rate for it in its tooltip. |
| 2491 | 8 | `String quoteName(Instrument kit, boolean foreign)` | What the quoted paper is called on its card: "20-year term loan", "6-month note in dollars". |
| 2501 | 32 | `VBox quoteCard(Instrument kit, DebtQuote quote, boolean foreign)` | THE QUOTE: the land office's offer card on exactly the terms the button books, and why the proceeds are not the ask. |
| 2535 | 37 | `VBox whoBuysCard(DebtQuote quote)` | WHO BUYS IT: the households, then the bank, its room used as a bar with this issue as a ghost; the bank's three alarms make the card red. |
| 2574 | 10 | `VBox dollarsCard()` | AND THE DOLLARS: convert and spend, or hold as reserves (the spec's W8). |
| 2586 | 10 | `HBox foreignDoor()` | The door marked do not open, outlined in red. |
| 2608 | 30 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out: "Finances › Default abroad" (0.7.32, the spec's D14), two equal cards in the same type, "Keep paying" first. |

### MONEY (0.7.0; redrawn 0.7.32) (lines 2639-2808)

| line | len | member | says |
|---:|---:|---|---|
| 2654 | 16 | `String moneyInfo()` | The page's (i): the old page's two sentences, the spec's T64 to T66. |
| 2671 | 15 | `void moneyPage(VBox page)` |  |
| 2688 | 15 | `String centralBookInfo(CentralBank cb)` | The book's (i): the spec's T67 and T68. |
| 2705 | 32 | `VBox centralBookCard(CentralBank cb)` | THE CENTRAL BANK'S BOOK: what it holds against what it owes, one scale; its equity on the owing side, or a shortfall red on the holding side. |
| 2744 | 17 | `VBox publicCard(CentralBank cb)` | WHAT THE PUBLIC HOLDS: M2 as a bar of its parts, and M0 beside it. |
| 2763 | 28 | `VBox thisMonthCard(CentralBank cb)` | THIS MONTH: the money made and the money destroyed, each a bar of its parts on one scale, and what M0 moved by. |
| 2793 | 15 | `VBox smallChart(String title, HistorySave h, String[] keys, String[] names, String[] colours)` | History's money series as a small chart without the controls, on the last ten years (ChartModel.DEFAULT_RANGE) or the whole history while it is younger - TimeChart, as Government's Output draws it. |

### THE BOND MARKET (0.7.12; redrawn 0.7.32) (lines 2809-3134)

| line | len | member | says |
|---:|---:|---|---|
| 2824 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 2852 | 14 | `Node holdersBar(double[] held, double band, boolean key)` | A holders bar over five amounts, each keyed when `key` is set. |
| 2867 | 32 | `void bondMarketPage(VBox page)` |  |
| 2901 | 24 | `VBox issuerCard(BondMarket.Issuer is)` | One issuer as a card: its sector's icon, its face, its bonds and their coupons, its nearest maturity, its holders; a click opens its largest bond's book. |
| 2927 | 25 | `VBox bondMonthCard(BondMarket market)` | THIS MONTH on the bond market: sold, coupons, repaid, written off, and the last issue. |
| 2954 | 14 | `VBox orderBooksCard(BondMarket market)` | THE ORDER BOOKS, last month: offered for sale, how much of it sold, and how many sellers waited. |
| 2970 | 31 | `Node bondGrids(BondMarket market)` | The old page's two tables, in the fold: every bond with its price and yield, and who holds each. |
| 3011 | 46 | `void bondBookPage(VBox page)` |  |
| 3059 | 17 | `Node depthChart(OrderBook book, CorporateBond b, int month)` | The book's depth: a row a price level, the asks over the bids, best nearest the middle; bids drawn leftward, asks rightward, face as length. |
| 3078 | 28 | `Node depthRow(OrderBook.Level l, boolean bid, double most, CorporateBond b, int month)` | One price level of the depth chart. |
| 3108 | 26 | `Node levelGrids(OrderBook book, CorporateBond b, int month)` | The old page's bid and ask tables, in the fold. |

### THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) (lines 3135-3148)

### THE DEBT RESULT (lines 3149-3220)

| line | len | member | says |
|---:|---:|---|---|
| 3167 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 3177 | 43 | `void showDebtResultMenu(DebtQuote quote, boolean foreign, String summary)` | Shows the terms the player just agreed to: the quote booked as a receipt, and the ladder with it on the books; or what the booking said when nothing was booked. |


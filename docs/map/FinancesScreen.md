# FinancesScreen.java - 3,414 lines · 142 methods · 63 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (446), [DebtManager](DebtManager.md) (54), [CityCalendar](CityCalendar.md) (29), [Game](Game.md) (27), [CityNeeds](CityNeeds.md) (26), [Icons](Icons.md) (17), [Rollover](Rollover.md) (17), [Debt](Debt.md) (15), [CorporateBond](CorporateBond.md) (14), [OrderBook](OrderBook.md) (14), [HistoryScreen](HistoryScreen.md) (10), [BondMarket](BondMarket.md) (9), [Ladder](Ladder.md) (8), [Bank](Bank.md) (8), [TimeChart](TimeChart.md) (8), [FundScreen](FundScreen.md) (7), [CentralBank](CentralBank.md) (7), [TreasuryFund](TreasuryFund.md) (6), [Currency](Currency.md) (5), [ChartModel](ChartModel.md) (5), [ForeignAccounts](ForeignAccounts.md) (5), [DebtQuote](DebtQuote.md) (5), [HistorySave](HistorySave.md) (4), [NationalAccounts](NationalAccounts.md) (3), [BuildScreen](BuildScreen.md) (3), [Pieces](Pieces.md) (3), [Money](Money.md) (3), [UserInterface](UserInterface.md) (2), [TradeScreen](TradeScreen.md) (2), [HouseholdBalance](HouseholdBalance.md) (2)... and 7 more

**Used by (5):** [BankScreen](BankScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 71 | FINANCES |
| 437 | · THE FIVE FIGURES (0.7.32; four until then) |
| 606 | · THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line |
| 675 | · the screen's own pieces |
| 864 | THE HUB (0.7.32) |
| 890 | · when it falls due |
| 1058 | · next due |
| 1136 | · the two settings |
| 1138 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1283 | · WHEN THE BANK FAILS (0.7.14) |
| 1370 | · the six areas |
| 1494 | THE POSITION (0.7.32) |
| 1663 | DEBT SERVICE (0.7.32) |
| 1786 | HOME AND ABROAD (0.7.32) |
| 1889 | YOUR RATE, TAKEN APART (0.7.32) |
| 2124 | THE BOOK (0.7.32) |
| 2300 | BORROW (0.7.32) |
| 2449 | · the ask, typed (0.7.40) |
| 2833 | MONEY (0.7.0; redrawn 0.7.32) |
| 3003 | THE BOND MARKET (0.7.12; redrawn 0.7.32) |
| 3329 | THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) |
| 3343 | THE DEBT RESULT |

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
| 239 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 242 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.32; Go... |
| 245 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same... |
| 382 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "Th...` | The tab's (i): what the hub is, in a breath. |
| 388 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balan...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 652 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " +...` | What resolving a failed bank does (the spec's T41). |
| 893 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, "...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 903 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " ...` | The ladder with nothing on it (the spec's T11). |
| 907 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "ref...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 912 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 915 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1152 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month notes" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1155 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't...` | Each setting's one line (the spec's T35). |
| 1163 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and ...` | ...and each in full, on its chip (the old block's sentences). |
| 1174 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's ...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1292 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1295 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays froze...` | Each setting's one line (the spec's T40). |
| 1301 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose ...` | The rescue's (i): both settings in full (the old block's T40). |
| 1517 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to ...` | THE BALANCE's (i): the spec's T7. |
| 1684 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 1687 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 1733 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the coupon is an expense; the principal is a balance-sheet movement." +...` | LAST MONTH's (i): the spec's T14. |
| 1765 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city ...` | FOREIGN's (i): the spec's T16. |
| 1796 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none o...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 1844 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the "...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 1866 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports "...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 1984 | `FinancesScreen.CURVE_MONTHS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The curve's maturities, in months. |
| 1986 | `FinancesScreen.CURVE_NAMES` | `{ "3m", "6m", "1y", "2y", "5y", "10y", "20y", "30y", "40y", "50y" }` | ...and how they are written under it. |
| 1989 | `FinancesScreen.CURVE_INFO` | `"The note is the floor - the dial, or what the bank's money costs it, whichev...` | THE CURVE's (i): the spec's T24, and the world's curve. |
| 2024 | `FinancesScreen.CurveChart.W` | `720, H = 190, LEFT = 44, RIGHT = 16, TOP = 26, FOOT = 22` | Its size and its margins, in pixels: a card's picture, at a fixed size. |
| 2156 | `FinancesScreen.NOTE_INFO` | `"No coupon at all - the lender's return was the discount, taken out of the pr...` | A note's (i): the spec's T28. |
| 2160 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, ...` | A price against face, in words (the spec's T33, D16). |
| 2320 | `FinancesScreen.TERMS_INFO` | `"Each column is the rate this paper would cost at that term today - for the "...` | The terms' (i): the spec's T55. |
| 2327 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " ...` | The ask's (i): the spec's T56. |
| 2331 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here, with the ladder it would build...` | The quote's empty state: the spec's T57. |
| 2335 | `FinancesScreen.PROCEEDS_INFO` | `"Paper is sold in lots and the face is grossed up for the discount, so the " ...` | Why the proceeds are not the ask: the spec's T58. |
| 2340 | `FinancesScreen.BUYERS_INFO` | `"The households first, when it pays them more than the bank does: up to %s of...` | Who buys it: the spec's T61, its first half. |
| 2347 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "d...` | Where the dollars go: the spec's T62. |
| 2452 | `FinancesScreen.ASK_TYPED_INFO` | `"Type an amount in dollars: digits, with or without commas, a decimal " + "po...` | HOW MUCH's (i): what the box takes - the case rules - and what the buttons do. |
| 2460 | `FinancesScreen.ASK_WORDS` | `java.util.regex.Pattern.compile("(?:[A-Za-z]{0,3}\\$)?\\s*((?:\\d{1,3}(?:,\\d...` | A typed amount: an optional mark, digits (grouped by commas or not), an optional fraction, an optional unit. |
| 2792 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate an...` | The default page's (i). |
| 2933 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's...` | M2's (i): the spec's T69. |
| 3023 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no ...` | No bond outstanding: the spec's T70. |
| 3028 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has ...` | The prices' (i): the spec's T71. |
| 3033 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when ...` | The order books' (i): the spec's T74. |
| 3041 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 3043 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY...` | ...and their colours on it. |
| 3200 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since ...` | A bond's book's (i): the spec's T75, T77 and T78. |

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
| 213 | `private String askTyped` | What is typed in the ask's box and not yet set (0.7.40), kept through a month's redraw; null while the box is empty. |
| 215 | `private double askTypedOver` | ...the ask it was typed over: a step, a preset or an issue that moves the ask drops it. |
| 217 | `private String askRefused` | What the last entry that could not be read says, under the box until the ask is next set; null for none. |
| 219 | `private javafx.scene.control.TextField askField` | The ask's box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing (FundScreen's search box). |
| 220 | `private boolean askTyping` |  |
| 227 | `private final java.util.Set<String> open` | What is standing open - a "details" fold, by key. |
| 230 | `private Object scrollTarget` | Where the page is to be scrolled to once it is drawn: LADDER or RESCUE on the hub, a piece of paper or a calendar year on The book; null for the top. |
| 233 | `private final java.util.Map<Object, Node> targets` | The nodes a door on this tab can scroll to, by the same keys, as the page draws them. |
| 236 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 3197 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |
| 3341 | `double fundAsk` | What the player's hand is asking to pay into the fund or draw out of it, on Rules & cash (FundScreen.moveCard()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 3357 | **type** `final class FinancesScreen` | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece and what it would cost to retire, borrowing at home and abroad, the mone... |
| 63 | 4 | `FinancesScreen(UserInterface ui)` |  |

### FINANCES (lines 71-436)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 11 | `static String[] pagesOf(String area)` | An area's pages, by its name; the position's for a name it does not know. |
| 145 | 10 | `static String[] iconsOf(String area)` | ...and their icons on the chips. |
| 173 | 3 | **type** `record Instrument(String key, String name, String short_, int min, int max, int step, double rounding, Stri...` | One kind of paper the city can sell. |
| 194 | 4 | `static Instrument instrument(String key)` |  |
| 200 | 3 | `static String instrumentColour(String type)` | A kind's colour on the ladder, by Debt.getType(): DebtManager.LADDER_KINDS in Palette.LADDER's three. |
| 256 | 8 | `void open(String area, String page, Object anchor)` | The tab opened on an area and a page (0.7.32, the spec's D17): the hub when `area` is null, an area's first page when `page` is not one of its own, and with `anchor` - LADDER or RESCUE on the hub, a piece of paper (a ... |
| 265 | 78 | `void showFinanceMenu()` |  |
| 345 | 16 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (GovernmentScreen's, 0.7.31). |
| 363 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 376 | 4 | `void showOnHub(String target)` | On the hub, scroll to `target` where it is; anywhere else, the hub opened on it. |
| 401 | 4 | `static String areaInfo(String area)` | An area's (i) by its name. |
| 412 | 9 | `HBox head()` | The head: "Finances" with the money blue's swatch; on an area the breadcrumb "Finances › The book", the first word a way back. |
| 423 | 13 | `static Label backDoor(String text, Runnable go)` | A door back: "‹ Finances", in the accent, underlined under the pointer (Pieces.door()'s, pointing the other way). |

### THE FIVE FIGURES (0.7.32; four until then) (lines 437-605)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 1 | **type** `record Kpi(String label, String value, String note, String tone, String where, String alarm)` | One of the five figures, worked out without drawing it: its label, its figure, its note, its colour, where its click goes, and the red line under it (null: none). |
| 456 | 63 | `List<Kpi> kpis(List<CityNeeds.Need> all)` | The five figures (pure: the probe reads them as the strip shows them). |
| 520 | 25 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 547 | 22 | `void withSpark(VBox cell, String series, String change)` | A limit cell with a year of a History series as a sparkline at the right of its figure, and a line under the note when `change` is not null (GovernmentScreen.withSpark()'s shape). |
| 571 | 13 | `String rateChange()` | THE RATE's move on last month, from History's last two points: "▲ 0.01 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). |
| 586 | 7 | `Debt soonest()` | The piece that falls due soonest, or null with nothing owed. |
| 595 | 4 | `static String urgency(int monthsOff)` | A maturity's colour, by how many months off it is: red inside FALLS DUE's line (NEEDS YOU's), amber inside FALLS_DUE_SOON_MONTHS, the headings' ink after. |
| 601 | 4 | `static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | NEEDS YOU's level for a kind: 0 green, 1 amber, 2 red, -1 not measured. |

### THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line (lines 606-674)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 37 | `List<Node> alertBands()` |  |
| 657 | 17 | `static HBox alertBand(String line, String whole, Node door)` | One alert band: a red ground, the alert icon, one line, its (i), and a door at the right (null: none). |

### the screen's own pieces (lines 675-863)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 1 | `String sym()` | The city's money's mark where a foreign figure is on the tab: "D$" (the spec's D9). |
| 681 | 4 | `String d(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k" (the spec's D9, B9). |
| 687 | 4 | `String dFull(double thousands)` | ...with every digit. |
| 693 | 5 | `String signedD(double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 700 | 1 | `String perUsd(double rate)` | An exchange rate with its unit, as the land office writes it: "D$0.7035 per US$" (the spec's B10). |
| 703 | 1 | `static String pc(double share)` | A share as a whole per cent: "58%". |
| 706 | 1 | `static String pc1(double share)` | ...to one place: "1.5%". |
| 709 | 1 | `static String pts(double rate)` | Points of a rate, signed: "+0.02 pts". |
| 712 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 721 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 729 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 740 | 9 | `static HBox caption(String text, String info, Node right)` | ...with words at its right. |
| 751 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. |
| 760 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour - red when the card is in alarm. |
| 766 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 777 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 784 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 789 | 1 | `static Label line(String text)` | A plain line of words in a card, wrapping. |
| 792 | 1 | `static Label muted(String text)` | ...muted. |
| 795 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width (the spec's D10). |
| 803 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 808 | 8 | `static GridPane pair(Node left, Node right)` | Two nodes side by side in equal columns, each as tall as the taller. |
| 818 | 7 | `static HBox swatch(String colour, String name, boolean ghost)` | A key's entry: a swatch and a name, the swatch outlined when `ghost`. |
| 827 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 832 | 3 | `static Segment ghost(double amount, String colour, String label, String tip)` | ...hollow: what would be there, or what is not. |
| 837 | 5 | `static javafx.scene.layout.FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 844 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (GovernmentScreen.openPolicy()'s). |
| 857 | 6 | `void openTrade(String area, String page)` | Trade, on the page an area names: "The currency" and "The reserves" were Trade's areas until 0.7.35 and are its pages now - TradeScreen reads tradeArea as the page, so `page` is overwritten. |

### THE HUB (0.7.32) (lines 864-889)

| line | len | member | says |
|---:|---:|---|---|
| 879 | 10 | `void hubPage(VBox page, List<CityNeeds.Need> all)` |  |

### when it falls due (lines 890-1057)

| line | len | member | says |
|---:|---:|---|---|
| 917 | 17 | `VBox ladderCard()` |  |
| 954 | 27 | `Pieces.Columns ladderChart(DebtManager.Ladder ladder, String extraColour, String extraName, double height)` | The ladder as columns (Pieces.columns()): a column a calendar year, each kind's payments stacked from the foot - notes, serial bonds, term loans, Palette.LADDER's three - the part owed in dollars striped down its edge... |
| 983 | 5 | `static double tallestYear(DebtManager.Ladder ladder)` | The tallest of the twelve years' columns, a proposed issue's ghost included. |
| 990 | 4 | `static boolean laterBroken(DebtManager.Ladder ladder)` | Whether "later" is drawn broken (0.7.34): more than LATER_BREAK times the tallest year. |
| 996 | 5 | `static double ladderScale(DebtManager.Ladder ladder)` | The ladder's scale: the tallest column - or, "later" broken, LATER_CAP tallest years, so the years fill the height. |
| 1003 | 11 | `List<Segment> rungParts(DebtManager.Rung r, String extraColour)` | A rung's segments, foot first: each kind at home, then its dollar part striped; then a proposed issue's, as a ghost. |
| 1016 | 16 | `String rungTip(DebtManager.Rung r, double revenueYear, String extraName)` | A column's tooltip: "2210 · D$579.0M: term loans D$579.0M, D$563.3M of it in dollars · 30% of a year's take" (the spec's section 3). |
| 1034 | 23 | `HBox ladderKey(DebtManager.Ladder ladder, String extraColour, String extraName)` | The ladder's key: only the kinds drawn, "in dollars" when any is, the proposed issue's ghost; at the right, everything still to pay. |

### next due (lines 1058-1135)

| line | len | member | says |
|---:|---:|---|---|
| 1075 | 26 | `VBox nextDueCard()` | NEXT DUE: the next five pieces to fall due, beside the ladder (0.7.32; a card at the top of the hub from 0.7.24, the window's foot until then). |
| 1103 | 19 | `HBox nextDueRow(Debt debt, int month)` | One piece on NEXT DUE: its amount in its urgency, its kind's tag and its date; a click opens it on The book. |
| 1124 | 11 | `String fellDueWords()` | What fell due at the last press, for the month after it (the spec's section 5): "D$272.0M fell due · rolled", off the rollover's record or, by hand, the principal the treasury repaid; null when nothing did. |

### the two settings (lines 1136-1137)

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1138-1282)

| line | len | member | says |
|---:|---:|---|---|
| 1192 | 33 | `VBox rolloverCard()` |  |
| 1227 | 25 | `String planWords(Rollover.Plan plan)` | NEXT MONTH in one line (the spec's T37): what falls due and where each part of it goes - the bar's parts in words, so the bar needs no key. |
| 1254 | 14 | `String surplusWords(Rollover.Plan plan)` | NEXT MONTH's (i): the year's surplus and what is used of it, and the central bank's part (the old block's lines). |
| 1274 | 8 | `String lastRollWords()` | The last rollover, in a muted line (the spec's T38), or null when none has run: dated by the month what it rolled fell due in - the month after the press it ran at, Rollover.getLastMonth(). |

### WHEN THE BANK FAILS (0.7.14) (lines 1283-1369)

| line | len | member | says |
|---:|---:|---|---|
| 1308 | 40 | `VBox rescueCard()` |  |
| 1350 | 11 | `String settingsWords()` | The rollover and the rescue, as Borrow and the fund's Rules & cash (its Holdings until 0.7.39) point at them (the spec's D3): "What falls due rolls as the same structure; a failed bank is resolved automatically." |
| 1363 | 6 | `HBox settingsPointer()` | ...as a line with its door to the hub's two cards. |

### the six areas (lines 1370-1493)

| line | len | member | says |
|---:|---:|---|---|
| 1373 | 1 | **type** `record AreaCard(String area, String figure, String line, String lineTone, String chip)` | One area's card, worked out without drawing it: its figure, its line and the line's colour, and an amber chip (null: none). |
| 1376 | 40 | `List<AreaCard> areaWords()` | The six areas' figures (pure: the probe reads them as the cards show them). |
| 1417 | 10 | `GridPane areaCards()` |  |
| 1429 | 31 | `Node areaBar(int i)` | Each area's thin bar: the credit band, the principal by kind, the bank's room, M2's parts, the bonds' holders, the fund's shares against its aim. |
| 1462 | 31 | `VBox areaCard(int i, AreaCard w, Node bar)` | One area as a card (the spec's section 3 A.3): its icon, name and (i), its figure, its line, its chip, its bar; hover lights its edge, a click opens it. |

### THE POSITION (0.7.32) (lines 1494-1662)

| line | len | member | says |
|---:|---:|---|---|
| 1510 | 5 | `void positionPage(VBox page, List<CityNeeds.Need> all)` |  |
| 1524 | 29 | `VBox balanceCard()` | THE BALANCE: the cash against what is owed, two bars on one scale, and the net position (the spec's section 3 B). |
| 1555 | 25 | `VBox creditBandCard()` | THE CREDIT BAND: the rate on the band from what a spotless city pays to what a hopeless one does, its parts the floor and the two measures (the spec's T8 cut: the band labels its ends). |
| 1582 | 9 | `String economyInfo(double ratio)` | AGAINST THE ECONOMY's (i): the spec's T9, rewritten from the market's own measure (B4). |
| 1593 | 38 | `VBox economyCard()` | AGAINST THE ECONOMY: the debt, a year of its coupon and a year of revenue, each a share of a year's output, on one scale. |
| 1633 | 29 | `VBox owedChartCard()` | OWED AND THE RATE: City History's public debt and borrowing rate on two axes, the borrowing decisions as flags (the spec's section 5). |

### DEBT SERVICE (0.7.32) (lines 1663-1785)

| line | len | member | says |
|---:|---:|---|---|
| 1678 | 4 | `void debtServicePage(VBox page)` |  |
| 1694 | 37 | `Node serviceCard()` |  |
| 1739 | 24 | `VBox lastMonthCard()` | LAST MONTH: the coupon, and the principal that fell due at the last press split as the rollover booked it. |
| 1770 | 15 | `VBox foreignCard()` | FOREIGN: a year of the dollar paper's service on a bar of a year of exports, red only when the world has shut its window. |

### HOME AND ABROAD (0.7.32) (lines 1786-1888)

| line | len | member | says |
|---:|---:|---|---|
| 1805 | 19 | `void homeAndAbroadPage(VBox page)` |  |
| 1826 | 16 | `VBox holdersCard(double all)` | WHO HOLDS IT: the households, the bank, the central bank and the world, one bar, each with its amount and share. |
| 1850 | 14 | `VBox dollarDebtCard()` | THE DOLLAR DEBT: dollars times the rate is the local figure; what the currency did to it last month as a chip. |
| 1873 | 15 | `VBox behindCard()` | WHAT IS BEHIND IT: the reserves, and the import cover on a year's bar with the model's comfortable line. |

### YOUR RATE, TAKEN APART (0.7.32) (lines 1889-2123)

| line | len | member | says |
|---:|---:|---|---|
| 1904 | 19 | `void yourRatePage(VBox page)` |  |
| 1925 | 15 | `String floorInfo()` | The floor's (i): the spec's T23, rewritten (B13), and the central bank's share (0.7.15). |
| 1942 | 29 | `VBox builtUpCard()` | THE RATE, BUILT UP: the floor and the two measures on the band from nothing to the ceiling, the dial and the city's rate marked, then each part with what moves it. |
| 1973 | 9 | `static VBox rateCell(String name, String value, String moves, Node door, String colour)` | One part of the rate: its swatch and name, its figure, what moves it, and its door. |
| 1996 | 19 | `VBox curveCard()` | THE CURVE: the city's rate by maturity at home and, while the window is open, abroad, with the dial dashed and the thirty-year point taken apart. |
| 2022 | 64 | **type** `static final class CurveChart extends javafx.scene.layout.Pane` | The curve, drawn: a point a maturity at equal steps, the city's in the money blue, the world's in violet, the dial dashed across, and the point at `marked` annotated with `note` - a fixed size, as a card's picture is;... |
| 2026 | 41 | `CurveChart(double[] home, double[] abroad, double dial, int marked, String note)` _(in FinancesScreen.CurveChart)_ |  |
| 2068 | 17 | `private void draw(double[] values, java.util.function.IntToDoubleFunction x, java.util.function.DoubleUnaryOperator y, String c...` _(in FinancesScreen.CurveChart)_ |  |
| 2088 | 6 | `static String measuresInfo()` | WHAT EACH MEASURE HAS USED's (i): the spec's T25. |
| 2096 | 15 | `VBox measuresCard()` | WHAT EACH MEASURE HAS USED: how much of its worst case each of the two measures has used, and a default's scar abroad. |
| 2113 | 10 | `static VBox stressRow(String label, double stress)` | One measure: its name, a bar of how much of its worst case it has used, and the share. |

### THE BOOK (0.7.32) (lines 2124-2299)

| line | len | member | says |
|---:|---:|---|---|
| 2142 | 12 | `String bookInfo()` | The book's (i): the spec's T32 and T34. |
| 2164 | 26 | `void bookPage(VBox page)` |  |
| 2192 | 9 | `static String pieceName(Debt debt)` | A piece's name in words: "6-month note", "5-year serial bond", "20-year term loan", "... |
| 2203 | 71 | `VBox pieceCard(Debt debt)` | One piece as a card (the spec's section 3 C). |
| 2276 | 23 | `Node bookGrid(List<Debt> paper)` | The book as the old page's table, in the fold: kind, owed, coupon a month, when it matures, how far off. |

### BORROW (0.7.32) (lines 2300-2448)

| line | len | member | says |
|---:|---:|---|---|
| 2351 | 35 | `void borrowPage(VBox page, boolean foreign)` |  |
| 2388 | 60 | `VBox askCard(Instrument kit, boolean foreign)` | THE ASK: the three instruments, the terms as columns of their rate (chips abroad), and how much. |

### the ask, typed (0.7.40) (lines 2449-2832)

| line | len | member | says |
|---:|---:|---|---|
| 2464 | 14 | `static double askFromWords(String typed)` | The ask a typed amount sets, in the model's thousands (Money.toDollars()'s unit), by ASK_TYPED_INFO's rules; NaN when it is not an amount. |
| 2480 | 4 | `static String askRefusal(String typed)` | What the box says under itself when an entry cannot be read. |
| 2486 | 6 | `String[] askShown(double ask, boolean foreign)` | The ask written in full and short: {"D$2,500,000,000,000", "D$2.5T"} - the second null when it would say the same; nothing asked, {"nothing asked for yet", null}. |
| 2494 | 6 | `static double[] askSteps(double ask, double lot)` | The ± steps for an ask: one unit of its leading digit and a tenth of that - D$1B and D$100M at D$3.4B - never under a lot; at nothing, one lot. |
| 2502 | 4 | `double askBase()` | What a step or a scale starts from: what is typed and not yet set, when it reads as an amount, else the ask. |
| 2508 | 7 | `void setAsk(double ask)` | The ask set by a step, a preset or clear: what was typed and the refusal go, and the page is drawn on it. |
| 2517 | 1 | **type** `record AskPreset(String name, double ask)` | One preset of the ask: what it is called, and the model's figure it sets. |
| 2531 | 16 | `List<AskPreset> askPresets(Instrument kit, boolean foreign)` | The ask's presets, every figure a model getter's, each offered only when it is something: at home, the minimum issue (at least a lot, as "the minimum" always set it), what falls due in the next twelve months (DebtMana... |
| 2555 | 27 | `Node askBox(boolean foreign)` | The box the ask is typed in (0.7.40), the house's search box: what is typed and not yet set lives through a month's redraw, and the focus with it. |
| 2590 | 25 | `private void commitAsk(javafx.scene.control.TextField box, boolean enter)` | What is in the box, set as the ask - or, when it cannot be read, said under the box with the ask left as it was. |
| 2617 | 23 | `VBox instrumentTile(Instrument kit, boolean on)` | One instrument as a tile: its swatch-tinted icon, its name and (i), the range it is issued over, its line; picked, its ground and edge lit. |
| 2642 | 18 | `Node termColumns(Instrument kit)` | The terms at home as columns of their rate: the chosen in the money blue, the rest grey; a click picks one. |
| 2662 | 21 | `Node termChips(Instrument kit)` | The terms abroad as chips, each with the world's rate for it in its tooltip. |
| 2685 | 8 | `String quoteName(Instrument kit, boolean foreign)` | What the quoted paper is called on its card: "20-year term loan", "6-month note in dollars". |
| 2695 | 32 | `VBox quoteCard(Instrument kit, DebtQuote quote, boolean foreign)` | THE QUOTE: the land office's offer card on exactly the terms the button books, and why the proceeds are not the ask. |
| 2729 | 37 | `VBox whoBuysCard(DebtQuote quote)` | WHO BUYS IT: the households, then the bank, its room used as a bar with this issue as a ghost; the bank's three alarms make the card red. |
| 2768 | 10 | `VBox dollarsCard()` | AND THE DOLLARS: convert and spend, or hold as reserves (the spec's W8). |
| 2780 | 10 | `HBox foreignDoor()` | The door marked do not open, outlined in red. |
| 2802 | 30 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out: "Finances › Default abroad" (0.7.32, the spec's D14), two equal cards in the same type, "Keep paying" first. |

### MONEY (0.7.0; redrawn 0.7.32) (lines 2833-3002)

| line | len | member | says |
|---:|---:|---|---|
| 2848 | 16 | `String moneyInfo()` | The page's (i): the old page's two sentences, the spec's T64 to T66. |
| 2865 | 15 | `void moneyPage(VBox page)` |  |
| 2882 | 15 | `String centralBookInfo(CentralBank cb)` | The book's (i): the spec's T67 and T68. |
| 2899 | 32 | `VBox centralBookCard(CentralBank cb)` | THE CENTRAL BANK'S BOOK: what it holds against what it owes, one scale; its equity on the owing side, or a shortfall red on the holding side. |
| 2938 | 17 | `VBox publicCard(CentralBank cb)` | WHAT THE PUBLIC HOLDS: M2 as a bar of its parts, and M0 beside it. |
| 2957 | 28 | `VBox thisMonthCard(CentralBank cb)` | THIS MONTH: the money made and the money destroyed, each a bar of its parts on one scale, and what M0 moved by. |
| 2987 | 15 | `VBox smallChart(String title, HistorySave h, String[] keys, String[] names, String[] colours)` | History's money series as a small chart without the controls, on the last ten years (ChartModel.DEFAULT_RANGE) or the whole history while it is younger - TimeChart, as Government's Output draws it. |

### THE BOND MARKET (0.7.12; redrawn 0.7.32) (lines 3003-3328)

| line | len | member | says |
|---:|---:|---|---|
| 3018 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 3046 | 14 | `Node holdersBar(double[] held, double band, boolean key)` | A holders bar over five amounts, each keyed when `key` is set. |
| 3061 | 32 | `void bondMarketPage(VBox page)` |  |
| 3095 | 24 | `VBox issuerCard(BondMarket.Issuer is)` | One issuer as a card: its sector's icon, its face, its bonds and their coupons, its nearest maturity, its holders; a click opens its largest bond's book. |
| 3121 | 25 | `VBox bondMonthCard(BondMarket market)` | THIS MONTH on the bond market: sold, coupons, repaid, written off, and the last issue. |
| 3148 | 14 | `VBox orderBooksCard(BondMarket market)` | THE ORDER BOOKS, last month: offered for sale, how much of it sold, and how many sellers waited. |
| 3164 | 31 | `Node bondGrids(BondMarket market)` | The old page's two tables, in the fold: every bond with its price and yield, and who holds each. |
| 3205 | 46 | `void bondBookPage(VBox page)` |  |
| 3253 | 17 | `Node depthChart(OrderBook book, CorporateBond b, int month)` | The book's depth: a row a price level, the asks over the bids, best nearest the middle; bids drawn leftward, asks rightward, face as length. |
| 3272 | 28 | `Node depthRow(OrderBook.Level l, boolean bid, double most, CorporateBond b, int month)` | One price level of the depth chart. |
| 3302 | 26 | `Node levelGrids(OrderBook book, CorporateBond b, int month)` | The old page's bid and ask tables, in the fold. |

### THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) (lines 3329-3342)

### THE DEBT RESULT (lines 3343-3414)

| line | len | member | says |
|---:|---:|---|---|
| 3361 | 8 | `String executeDebtLogic(String type, double amount, int duration, double rounding)` |  |
| 3371 | 43 | `void showDebtResultMenu(DebtQuote quote, boolean foreign, String summary)` | Shows the terms the player just agreed to: the quote booked as a receipt, and the ladder with it on the books; or what the booking said when nothing was booked. |


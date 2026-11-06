# FinancesScreen.java - 7,038 lines · 284 methods · 115 constants · interface

`ham/citybuildersim/ui/FinancesScreen.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (745), [Kept](Kept.md) (392), [DebtManager](DebtManager.md) (75), [CityCalendar](CityCalendar.md) (47), [Debt](Debt.md) (41), [Auction](Auction.md) (40), [Game](Game.md) (32), [CityNeeds](CityNeeds.md) (26), [ForeignAccounts](ForeignAccounts.md) (25), [Buyback](Buyback.md) (21), [Rollover](Rollover.md) (19), [Icons](Icons.md) (17), [BookDesk](BookDesk.md) (17), [CorporateBond](CorporateBond.md) (15), [OrderBook](OrderBook.md) (15), [CallTree](CallTree.md) (14), [DebtQuote](DebtQuote.md) (11), [LongTermBond](LongTermBond.md) (10), [HistoryScreen](HistoryScreen.md) (10), [BondMarket](BondMarket.md) (10), [TimeChart](TimeChart.md) (10), [Bank](Bank.md) (9), [FundScreen](FundScreen.md) (8), [FloatingRateNote](FloatingRateNote.md) (8), [Ladder](Ladder.md) (8), [InflationLinkedBond](InflationLinkedBond.md) (7), [CentralBank](CentralBank.md) (7), [TreasuryFund](TreasuryFund.md) (6), [YieldCurve](YieldCurve.md) (6), [CapChips](CapChips.md) (4)... and 19 more

**Used by (8):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapChips](CapChips.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [KeptBench](KeptBench.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 82 | FINANCES |
| 250 | · the floating-rate note and the inflation-linked bond on the screens (CD5, spec-city-debt.md 2.4, 2.5, 5.4 and 7) |
| 673 | · THE FIVE FIGURES (0.7.32; four until then) |
| 852 | · THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line |
| 922 | · the screen's own pieces |
| 984 | · the city's paper in any of the eight moneys (CD8): the mark, the words and the figure of the money a piece is written in |
| 1202 | THE HUB (0.7.32) |
| 1229 | · when it falls due |
| 1404 | · next due |
| 1487 | · the two settings |
| 1489 | · ROLLING WHAT FALLS DUE (0.7.13) |
| 1668 | · WHEN THE BANK FAILS (0.7.14) |
| 1759 | · the six areas |
| 1888 | THE POSITION (0.7.32) |
| 2077 | DEBT SERVICE (0.7.32) |
| 2230 | HOME AND ABROAD (0.7.32) |
| 2349 | YOUR RATE, TAKEN APART (0.7.32; the curve first since CD6, 2026-10-23) |
| 2888 | THE BOOK (0.7.32; a desk since CD6, 2026-10-23) |
| 2960 | · the books (CD3) |
| 3543 | · a line's page (CD6, board 2) |
| 3946 | · the desk's pictures (CD6) |
| 4329 | CLOSE IT NOW (CD4, 2026-10-23) |
| 4950 | BORROW (0.7.32) |
| 5371 | · the highest yield (CD2) |
| 5382 | · the ask, typed (0.7.40) |
| 5941 | · the auction, shown (CD2) |
| 6369 | MONEY (0.7.0; redrawn 0.7.32) |
| 6545 | THE BOND MARKET (0.7.12; redrawn 0.7.32) |
| 6913 | THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) |
| 6927 | THE DEBT RESULT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 114 | `FinancesScreen.FINANCE_HOME` | `"Overview"` | The page an area opens on when none is named: the first of any area's pages that is called this. |
| 118 | `FinancesScreen.AREAS` | `{ "The position", "The book", "Borrow", "Money", "The bond market", "The city...` | The six areas, in the hub's order. |
| 120 | `FinancesScreen.AREA_ICONS` | `{ Icons.FINANCES, Icons.PAPER, Icons.COIN, Icons.BANKNOTE, Icons.REPORTS, Ico...` | ...and each one's icon, on its card. |
| 123 | `FinancesScreen.POSITION_PAGES` | `{ "Overview", "Debt service", "Home & abroad", "Your rate" }` | The position's four pages (0.7.32: "The ladder" became the hub's hero). |
| 125 | `FinancesScreen.POSITION_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.TRADE, Icons.POLICY }` | ...and their icons on the chips. |
| 132 | `FinancesScreen.BOOK_PAGES` | `{ "Every piece" }` | The book is one page since 0.7.32, a desk of lines since CD6. |
| 134 | `FinancesScreen.CLOSE_PAGE` | `"Close it now"` | The page Close it now opens on, under The book (CD4): one piece's reverse auction, its ticket, its ladder and its call. |
| 136 | `FinancesScreen.BORROW_PAGES` | `{ "At home", "Abroad" }` | Borrow's two pages: the city's own paper at home, and dollars from the world. |
| 138 | `FinancesScreen.BORROW_ICONS` | `{ Icons.HOMES, Icons.TRADE }` | ...and their icons on the chips. |
| 140 | `FinancesScreen.MONEY_PAGES` | `{ "Money" }` | The central bank's books and the money supply, on one page (0.7.0). |
| 142 | `FinancesScreen.BOND_PAGES` | `{ "Every issue", "A bond's book" }` | The businesses' bonds: every issue, and one bond's order book (0.7.12). |
| 144 | `FinancesScreen.BOND_ICONS` | `{ Icons.REPORTS, Icons.PAPER }` | ...and their icons on the chips. |
| 146 | `FinancesScreen.FUND_PAGES` | `FundScreen.PAGES` | The city's fund as a brokerage (0.7.39; "Holdings" and "By hand" from 0.7.14): FundScreen's four pages. |
| 148 | `FinancesScreen.FUND_ICONS` | `FundScreen.ICONS` | ...and their icons on the chips. |
| 212 | `FinancesScreen.INSTRUMENTS` | `{ new Instrument("Note", "Bills", "BILL", ShortTermTBill.TERMS, 1000, "months...` | The five kinds of paper the city sells, in the board's tile order: bills, bonds, the amortizing bond (CD2), then the floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md 2.4 and 2.5). |
| 322 | `FinancesScreen.KIND_NAMES` | `{ "Bills", "Amortizing bonds", "Bonds" }` | The ladder's three kinds, in DebtManager.LADDER_KINDS' order, as the key and a tooltip say them. |
| 399 | `FinancesScreen.LADDER` | `"#ladder", RESCUE = "#rescue"` | The hub's scroll targets: the ladder card and the rescue card. |
| 402 | `FinancesScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller - the head, the five figures with THE RATE's change, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.32; Go... |
| 405 | `FinancesScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - GovernmentScreen's, for the same... |
| 593 | `FinancesScreen.BOUND_TO_FRAME` | `"finances.frame", BOUND_TO_SCROLLER = "finances.scroller"` | The key in a kept scroller's properties for the frame its height is bound to, and in a kept page's for the scroller its width is bound to (frameOver()). |
| 615 | `FinancesScreen.HEAD_INFO` | `"What the city owes, when it falls due, and what it does when it does." + "Th...` | The tab's (i): what the hub is, in a breath. |
| 621 | `FinancesScreen.AREA_INFO` | `{ "What the city holds, what it owes, and why the rate is the rate: the balan...` | Each area's (i), in AREAS' order: the landing's blurbs until 0.7.32 (the spec's T2), with what the card shows. |
| 899 | `FinancesScreen.RESOLVE_INFO` | `"Resolving it costs the hole and the capital to reopen it.Its owners lose " +...` | What resolving a failed bank does (the spec's T41). |
| 1232 | `FinancesScreen.LADDER_INFO` | `"Every payment the city's paper still asks, coupons and principal together, "...` | The ladder's (i): the spec's T12, and why a column is a calendar year. |
| 1242 | `FinancesScreen.NOTHING_DUE` | `"The city owes nothing, so nothing falls due.The Borrow page is where that " ...` | The ladder with nothing on it (the spec's T11). |
| 1246 | `FinancesScreen.WALL_WORDS` | `"more than half a year of revenue - a city meets a wall like that by " + "ref...` | A wall on the ladder (the spec's T13), in the heaviest year's tooltip. |
| 1251 | `FinancesScreen.LATER_BREAK` | `2` | How many times the tallest year "later" may be before it is drawn broken (0.7.34): past it, on one scale, the twelve would be slivers. |
| 1254 | `FinancesScreen.LATER_CAP` | `1.2` | ...and how tall a broken "later" stands, in tallest years: a little above the tallest, so it still reads as the most. |
| 1507 | `FinancesScreen.ROLLOVER_CHIPS` | `{ "By hand", "Same structure", Rollover.BILL_MONTHS + "-month bills" }` | The rollover's three settings as chips, in Rollover.Mode's order. |
| 1510 | `FinancesScreen.ROLLOVER_LINES` | `{ "Nothing automatic: what falls due is paid from the cash, and what it can't...` | Each setting's one line (the spec's T35). |
| 1518 | `FinancesScreen.ROLLOVER_TIPS` | `{ "Nothing automatic: what falls due is paid out of the treasury's cash, and ...` | ...and each in full, on its chip (the old block's sentences). |
| 1529 | `FinancesScreen.ROLLOVER_INFO` | `"By hand, nothing is automatic: what falls due is paid out of the treasury's ...` | The rollover's (i): the three settings, the central bank's own roll (T36) and the new paper's size (T39). |
| 1549 | `FinancesScreen.ROLLOVER_CAP_TITLE` | `"THE HIGHEST YIELD IT WILL TAKE"` | The rollover's cap block: its caption, and its (i), which says what the cap is and what a short sale does (D28, D29). |
| 1550 | `FinancesScreen.ROLLOVER_CAP_INFO` | `"The most the rollover will pay to sell what it refinances: fair plus a " + "...` |  |
| 1677 | `FinancesScreen.RESCUE_CHIPS` | `{ "Automatic", "Wait for my button" }` | The rescue's two settings as chips, in TreasuryFund.RescueMode's order. |
| 1680 | `FinancesScreen.RESCUE_LINES` | `{ "The month it fails, the city resolves it and it reopens.", "It stays froze...` | Each setting's one line (the spec's T40). |
| 1686 | `FinancesScreen.RESCUE_INFO` | `"Automatic: the month the bank fails, the city resolves it - its owners lose ...` | The rescue's (i): both settings in full (the old block's T40). |
| 1912 | `FinancesScreen.BALANCE_INFO` | `"A negative net position is not by itself a problem - a city that borrows to ...` | THE BALANCE's (i): the spec's T7. |
| 1921 | `FinancesScreen.PAPER_VALUES_INFO` | `"The same paper three ways.Its face is what falls due.What the books " + "car...` | The line under THE BALANCE's bars: three readings of the same paper. |
| 2100 | `FinancesScreen.SERVICE_WORDS` | `{ "comfortable", "felt", "constrained" }` | The band words, by CityNeeds.serviceLevel(). |
| 2103 | `FinancesScreen.SERVICE_INFO` | `"What the city pays its lenders against what it collects, because a lender is...` | The gauge's (i): the spec's T15 and T17, on two marks. |
| 2153 | `FinancesScreen.LAST_MONTH_INFO` | `"Only the interest is an expense; the principal is a balance-sheet movement."...` | LAST MONTH's (i): the spec's T14. |
| 2161 | `FinancesScreen.UPLIFT_INFO` | `"A linked bond's principal follows the price index, and the books catch up wi...` | The inflation uplift line's (i) on LAST MONTH (CD5, spec-city-debt.md 5.4). |
| 2209 | `FinancesScreen.FOREIGN_INFO` | `"Foreign paper is repaid in somebody else's money, and the only way the city ...` | FOREIGN's (i): the spec's T16. |
| 2240 | `FinancesScreen.HOLDERS_INFO` | `"Domestic paper is bought at home, so its coupon is income at home and none o...` | WHO HOLDS IT's (i): the spec's T19 and T29. |
| 2295 | `FinancesScreen.DOLLAR_INFO` | `"Owed in dollars, which do not move; worth in the city's money whatever the "...` | THE DOLLAR DEBT's (i): the spec's T20, both ways. |
| 2301 | `FinancesScreen.ABROAD_INFO` | `"Owed abroad in US$ and the other moneys the city has sold paper in, each of ...` | ...and once the city owes in another of the eight (CD8): the same card, every money counted in US$. |
| 2326 | `FinancesScreen.BEHIND_INFO` | `"Reserves are the city's dollars.Import cover is how many months of imports "...` | WHAT IS BEHIND IT's (i): the spec's T21 and T22, as the model reads cover (ForeignAccounts.COMFORTABLE_COVER). |
| 2489 | `FinancesScreen.CURVE_INFO` | `"Each point is what the city pays to borrow for that term: the zero rate.The ...` | THE CITY'S CURVE's (i): the spec's T24, and what each line on the chart is (CD6, board 5). |
| 2607 | `FinancesScreen.CurveChart.LEFT` | `46, RIGHT = 14, TOP = 18, FOOT = 26` |  |
| 2695 | `FinancesScreen.EXPECT_INFO` | `"The dial the market expects, month by month, is the path the whole curve is ...` | WHAT THE MARKET EXPECTS THE DIAL TO DO's (i): the path the curve is built on (CD6, board 5, note A). |
| 2702 | `FinancesScreen.LEAN` | `.0005` | The gap under which two rates are the same to the eye in the page's sentences: a twentieth of a point. |
| 2758 | `FinancesScreen.PARTS_INFO` | `"The ten-year zero rate is the sum of its parts, to rounding: the dial the ma...` | THE 10-YEAR RATE's (i): what the bar is made of. |
| 2800 | `FinancesScreen.REAL_INFO` | `"A linked bond pays a real coupon on a principal that grows with the price in...` | REAL RATES's (i): what the two lines are and what the dots are. |
| 2956 | `FinancesScreen.NOTE_INFO` | `"No coupon.The lender's return was the discount, taken out of the proceeds at...` | A bill's (i): the spec's T28. |
| 2963 | `FinancesScreen.CARRIED_INFO` | `"What the books hold the piece at, and the rate they charge it at.It starts "...` | The "carried · effective" line on a line's page: its (i). |
| 2970 | `FinancesScreen.LINKED_BOOKS_INFO` | `"What the books hold the piece at, and the rate they charge it at.The books "...` | The same line on an inflation-linked bond's card (CD6): its rate is a real rate. |
| 2977 | `FinancesScreen.PREMIUM_INFO` | `"Buying a piece back pays its holders what it is worth today.Under its face, ...` | A price against face, in words (the spec's T33, D16). |
| 2984 | `FinancesScreen.CALLABLE_INFO` | `"The city sold this line with the right to call it: from its first call date ...` | A callable line's (i) on its card (CD4, spec-city-debt.md 2.6 and 6.4). |
| 2989 | `FinancesScreen.FLOATING_INFO` | `"The coupon is the policy rate plus the margin the sale fixed, a twelfth of i...` | A floating-rate note's coupon line's (i) on its card (CD5, spec-city-debt.md 2.4). |
| 2996 | `FinancesScreen.INDEXED_INFO` | `"The real coupon is paid every month on the principal as the price index has ...` | An inflation-linked bond's coupon line's (i) on its card (CD5, spec-city-debt.md 2.5). |
| 3003 | `FinancesScreen.WORTH_CALLING_INFO` | `"The present value the call would save: what this line's remaining payments "...` | "Worth calling"'s (i) (D24). |
| 3009 | `FinancesScreen.DESK_CURVE_INFO` | `"Today's zero curve is the line: what the city pays to borrow for each term.E...` | THE CURVE card's (i) (CD6, board 1): what the dots are and what is left off. |
| 3016 | `FinancesScreen.DESK_BARS_INFO` | `"Carried is what the books say the city owes: what it raised, plus the discou...` | THE THREE BARS' (i): the board's paragraph. |
| 3025 | `FinancesScreen.GAIN_INFO` | `"Green is a gain and red a loss.It is what the books carry the line at less w...` | The (i) beside a gain or a loss on a line's page (CD6, Jerus 2026-10-23): what the colours mean. |
| 3033 | `FinancesScreen.EXCHANGE_INFO` | `"Paper abroad is owed in its own money - the US$ or another of the eight - an...` | The (i) beside the D$'s move since a dollar piece was sold, on the desk's card and a line's page (CD7): what the figure and its colours mean. |
| 3041 | `FinancesScreen.DESK_INFO` | `"If closed now: the carrying value less the worth, in D$ and as a share of th...` | THE DESK's (i): the two readings under the table. |
| 3123 | `FinancesScreen.BOOK_KIND_NAMES` | `{ "bills", "amortizing bonds", "bonds", "floating-rate notes", "inflation-lin...` | The kinds of paper as the desk names them, in DebtManager.BOOK_KINDS' order. |
| 3340 | `FinancesScreen.CLOSE_CELL` | `206` | The width of the desk's If closed now column (CD6): the gain or loss, and the Close it now button beside it. |
| 3343 | `FinancesScreen.EXCHANGE_CELL` | `128` | The width of the desk's exchange column (CD7), shown only when the book holds dollar paper: "−D$725.3M · −10.0%" at the caption's size, and its heading on two lines. |
| 3346 | `FinancesScreen.CLOSE_FIGURE` | `134` | The width of the gain or loss in that cell: "−D$725.3M · −100.0%" at the figure's size. |
| 3349 | `FinancesScreen.CLOSE_BUTTON` | `66, CLOSE_GAP = 6` | The Close it now button's width, and the gap between it and the figure. |
| 3409 | `FinancesScreen.ACROSS_TABLE` | `"finances.table", TABLE_ACROSS = "finances.across"` | The keys a scroller keeps the table it holds under, and a table the scroller that holds it (acrossWhenNarrow()). |
| 3570 | `FinancesScreen.LINE_PRICE_INFO` | `"The price is the market's: what the line's remaining payments are worth on "...` | THE PRICE card's (i): board 2, note A. |
| 3577 | `FinancesScreen.LINE_CLOSE_INFO` | `"Worth is what closing the line would cost at its fair price; a buyback pays ...` | IF YOU CLOSED IT TODAY's (i): board 2, note B. |
| 3586 | `FinancesScreen.LINE_BOOKS_INFO` | `"The books in full: what the line is carried at when the month opens, the " +...` | THE NEXT SIX MONTHS' (i): board 2, note C. |
| 3593 | `FinancesScreen.LINE_SALES_INFO` | `"Each sale into the line: when, how much face, what it raised, the price it "...` | ITS SALES, AND ITS RISK's (i). |
| 3599 | `FinancesScreen.LINE_PAGE` | `"A line"` | The name of a line's page, under The book: no chip's, open while its line is on the books (lineOpenNow()). |
| 3824 | `FinancesScreen.THREE_NARROW` | `1000` | The width under which a line's page stacks its three cards (CD6): the books' table, then the other two side by side. |
| 3827 | `FinancesScreen.THREE_CARDS` | `"finances.three"` | The key a line's page keeps its three cards under on their grid, for the width listener to arrange (linePage()). |
| 4082 | `FinancesScreen.DeskCurve.H` | `232, LEFT = 46, RIGHT = 14, TOP = 12, FOOT = 26` |  |
| 4183 | `FinancesScreen.DeskBars.H` | `158, LEFT = 84, RIGHT = 76, TOP = 26, ROW = 42, BAR = 26` |  |
| 4184 | `FinancesScreen.DeskBars.ROWS` | `{ "at face", "carried", "worth today" }` |  |
| 4242 | `FinancesScreen.PathChart.H` | `212, LEFT = 40, RIGHT = 16, TOP = 14, FOOT = 26` |  |
| 4370 | `FinancesScreen.CLOSE_INFO` | `"Closing a piece early is a reverse auction, not one price.Name the face you ...` | THE TICKET's (i): the spec's 6.2. |
| 4379 | `FinancesScreen.OFFERS_INFO` | `"Each bar is one slice a holder offers: its length is the slice's face, the n...` | THE OFFERS' (i). |
| 4385 | `FinancesScreen.COMPARE_INFO` | `"One buyback, read three ways.Against carrying value is the figure the books ...` | THE THREE COMPARISONS' (i): the spec's 6.3. |
| 4393 | `FinancesScreen.CALL_INFO` | `"From its first call date the city can pay every holder 100 for each 100 of f...` | THE CALL's (i): the spec's 6.4 and D22 to D24. |
| 4510 | `FinancesScreen.CLOSE_SHOWN` | `"finances.closeShown"` | The key a Close it now box keeps the text the page was last drawn on under, for its focus listener (closeBox()). |
| 4805 | `FinancesScreen.BuybackLadder.LEFT` | `74, FACE_ROOM = 76, MARKS = 134, TOP = 26, FOOT = 10, FIRST_W = 720, LEAST_W ...` | The margins, in pixels: the prices at the left; at the right the room for a slice's face (FACE_ROOM) and for the marks' names (MARKS). |
| 4992 | `FinancesScreen.TERMS_INFO` | `"Each column is today's fair yield for this paper at that term: what the curv...` | The terms' (i): the spec's T55, on the auction's fair yield (CD2). |
| 5002 | `FinancesScreen.CAP_INFO` | `"The most the city will pay to sell this: fair plus a point, a yield you name...` | THE HIGHEST YIELD's (i): the spec's 4.1 and 4.3 in two sentences. |
| 5010 | `FinancesScreen.LOTS_INFO` | `"Issues round to a lot, and the market will not arrange anything under the " ...` | The ask's (i): the spec's T56. |
| 5014 | `FinancesScreen.NO_ASK` | `"Ask for something and the quote appears here - at home the auction itself, r...` | The quote's empty state: the spec's T57. |
| 5019 | `FinancesScreen.PROCEEDS_INFO` | `"A bill's ask is the cash it should raise, and its face is the lot that bring...` | Why a bill's proceeds are not the ask: the spec's T58 (a bill's ask is the cash; a bond's is the face). |
| 5025 | `FinancesScreen.BUYERS_INFO` | `"Each bidder bids by the rule it follows elsewhere in the game.The households...` | Who bids: the spec's T61 and 4.2 (CD2). |
| 5035 | `FinancesScreen.DOLLARS_INFO` | `"Spending it leaves a dollar debt with nothing behind it, and the next " + "d...` | Where the dollars go: the spec's T62. |
| 5120 | `FinancesScreen.MONEY_INFO` | `"The city can sell its paper abroad in any of the eight moneys, each priced o...` | IN WHICH MONEY's (i): what the figure under a money is, and what owing in it risks. |
| 5325 | `FinancesScreen.CALLABLE_SALE_INFO` | `"A bond of ten years or more can be sold callable: from its first call date "...` | The CALLABLE switch's (i) (CD4, spec-city-debt.md 2.6 and D22 to D24). |
| 5385 | `FinancesScreen.ASK_TYPED_INFO` | `"Type an amount in dollars: digits, with or without commas, a decimal " + "po...` | HOW MUCH's (i): what the box takes - the case rules - and what the buttons do. |
| 5400 | `FinancesScreen.ASK_WORDS` | `java.util.regex.Pattern.compile("(?:[A-Za-z]{0,3}[$€¥£]\|(?i:chf))?\\s*((?:\\d...` | A typed amount: an optional mark (D$, US$, $, or one of the eight's - €, ¥, £, CN¥, C$, A$, CHF), digits (grouped by commas or not), an optional fraction, an optional unit. |
| 5944 | `FinancesScreen.STOP_INFO` | `"The one yield at which the bids first cover the issue - never under fair, ne...` | STOPS AT's (i): the stop and the tail (the spec's 4.3). |
| 5949 | `FinancesScreen.COVER_INFO` | `"What was bid up to your highest yield, each bidder counted at most the issue...` | BIDS' (i): the cover. |
| 6122 | `FinancesScreen.AuctionChart.WHO_COLOURS` | `{ Palette.PEOPLE, Palette.BUSINESS, Palette.ORE, Palette.MONEY }` | The bidders' colours in Auction.Fill's order, as the holders' bar has them. |
| 6124 | `FinancesScreen.AuctionChart.WHO` | `{ "households", "the firms", "the world", "the bank" }` | ...and their names, in the same order. |
| 6126 | `FinancesScreen.AuctionChart.H` | `240, LEFT = 62, RIGHT = 16, TOP = 16, FOOT = 26, FIRST_W = 720, LEAST_W = 420` | Its height and its margins, in pixels; the width it is first drawn at, and the least it is drawn at. |
| 6128 | `FinancesScreen.AuctionChart.SAMPLES` | `60` | How many yields the bids are read at across the axis. |
| 6317 | `FinancesScreen.DEFAULT_INFO` | `"Walking away from every dollar the city owes abroad.The gain is immediate an...` | The default page's (i). |
| 6472 | `FinancesScreen.M2_INFO` | `"M2 is the bank's deposits - the households', the businesses' and the world's...` | M2's (i): the spec's T69. |
| 6565 | `FinancesScreen.NO_BONDS` | `"None outstanding.A business sells a bond when the book would take it for no ...` | No bond outstanding: the spec's T70. |
| 6570 | `FinancesScreen.PRICES_INFO` | `"The price is per 100 of face, at the last trade on its book; * where it has ...` | The prices' (i): the spec's T71. |
| 6575 | `FinancesScreen.BOOKS_INFO` | `"Everybody posts buy and sell orders at prices, and an order fills only when ...` | The order books' (i): the spec's T74. |
| 6583 | `FinancesScreen.BOND_HOLDERS` | `{ "households", "the bank", "companies", "the world", "the city's fund" }` | The bonds' holders, in a bar's order: the households, the bank, the companies, the world, the city's fund. |
| 6585 | `FinancesScreen.BOND_HOLDER_COLOURS` | `{ Palette.PEOPLE, Palette.MONEY, Palette.BUSINESS, Palette.ORE, Palette.MONEY...` | ...and their colours on it. |
| 6766 | `FinancesScreen.DEPTH_INFO` | `"What rests on the book after the month's step: a bid under every ask, since ...` | A bond's book's (i): the spec's T75, T77 and T78. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 72 | `final FundScreen fundScreen` | The city's fund's pages and a security's (0.7.39): the area is drawn by its own class. |
| 75 | `final Kept.Pool pool` | The nodes this screen made at its last draw, handed back to it at the next (KP5; study UP4, docs/studies/spec-screens-in-place.md). |
| 112 | `String financeArea` | Which area is open; null is the hub. |
| 115 | `String financePage` |  |
| 336 | `String borrowType` | what the borrow page is currently asking for |
| 337 | `int borrowTerm` |  |
| 338 | `double borrowAsk` |  |
| 339 | `boolean borrowHold` |  |
| 345 | `String borrowMoney` | The money the paper abroad is asked in (CD8, Jerus 2026-10-23): the code of one of WorldMoneys.EIGHT, the US$ by default; kept through the clock's redraw like borrowAsk, and read as the US$ again on the page when the ... |
| 352 | `boolean borrowCallable` | Whether the bond is asked callable (CD4, spec-city-debt.md 2.6): off by default, kept through the clock's redraw like borrowAsk, and in force only where it can be - a home bond of ten years or more (callableAsked()). |
| 354 | `private String callWords` | What a callable quote says of its call (CD4), struck once a redraw by borrowPage(): "from Jan 2216 · the call adds 34 bp to the coupon"; null for paper that cannot be called. |
| 360 | `Auction.Cap borrowCap` | The highest yield the sale will take (CD2, spec-city-debt.md 4.1): fair plus a point by default, a yield the player names, or Fill it. |
| 362 | `private final CapChips borrowChips` | The Borrow page's cap chips and their yield box (CapChips), and the rollover card's own: each keeps its typing through a month's redraw. |
| 363 | `private final CapChips rolloverChips` |  |
| 365 | `String askTyped` | What is typed in the ask's box and not yet set (0.7.40), kept through a month's redraw; null while the box is empty. |
| 367 | `double askTypedOver` | ...the ask it was typed over: a step, a preset or an issue that moves the ask drops it. |
| 369 | `String askRefused` | What the last entry that could not be read says, under the box until the ask is next set; null for none. |
| 371 | `javafx.scene.control.TextField askField` | The ask's box, and whether it had the focus when the page was last redrawn - a month landing rebuilds it under the typing (FundScreen's search box). |
| 372 | `private boolean askTyping` |  |
| 375 | `Debt closing` | The piece the Close it now page is on (CD4), by identity: the page is open while it is on the books. |
| 377 | `String closeFaceText` | What is typed in the Close it now page's two boxes and not yet drawn on (CD4), kept through a month's redraw: the face to buy back, and the most to pay per 100. |
| 379 | `javafx.scene.control.TextField closeFaceField, closeMaxField` | The two boxes, and which had the focus when the page was last redrawn (0 neither, 1 the face, 2 the most to pay) - a month landing rebuilds them under the typing. |
| 380 | `private int closeTyping` |  |
| 387 | `final java.util.Set<String> open` | What is standing open - a "details" fold, by key. |
| 390 | `private Object scrollTarget` | Where the page is to be scrolled to once it is drawn: LADDER or RESCUE on the hub, a piece of paper or a calendar year on The book; null for the top. |
| 393 | `private final java.util.Map<Object, Node> targets` | The nodes a door on this tab can scroll to, by the same keys, as the page draws them. |
| 396 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 2609 | `private int[] axis` |  |
| 2610 | `private List<Series> series` |  |
| 2611 | `private List<Dot> dots` |  |
| 2612 | `private final double height` |  |
| 3602 | `Debt lineOpen` | The line a line's page is open on (CD6), by identity; null while none has been. |
| 3605 | `boolean closeFromLine` | Whether Close it now was opened from the line's page, so its back door returns there. |
| 3953 | `private final double first` |  |
| 3954 | `private double drawnAt` |  |
| 3956 | `private Object[] told` | The figures it was last told (Kept, KP5): a picture on a kept page is told each month's by set(), and draws again only where they moved. |
| 4084 | `private int[] tenors` |  |
| 4085 | `private double[] zero` |  |
| 4086 | `private List<Dot> dots` |  |
| 4186 | `private double[][] byKind` |  |
| 4187 | `private double worth` |  |
| 4188 | `private java.util.function.DoubleFunction<String> money` |  |
| 4244 | `private double[] price` |  |
| 4245 | `private int firstMonth` |  |
| 4807 | `private Buyback.Result q` |  |
| 4808 | `private double maxPer100` |  |
| 4809 | `private java.util.function.DoubleFunction<String> money` |  |
| 4811 | `private double row` | A row's pitch, and the width it is drawn at now (0 before the first). |
| 4812 | `private double drawnAt` |  |
| 4814 | `private boolean told` | Whether it has been told a quote (Kept, KP5). |
| 6130 | `private Auction.Result r` |  |
| 6131 | `private Auction.Bidders bidders` |  |
| 6132 | `private java.util.function.DoubleFunction<String> money` |  |
| 6763 | `int bookBondId` | The bond whose book is open, by its number; the largest one when it has gone. |
| 6925 | `double fundAsk` | What the player's hand is asking to pay into the fund or draw out of it, on Rules & cash (FundScreen.moveCard()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 6981 | **type** `final class FinancesScreen` | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece and what it would cost to retire, borrowing at home and abroad, the mone... |
| 63 | 7 | `FinancesScreen(UserInterface ui)` |  |
| 78 | 3 | `void forget()` | Every node is made new at the next draw - the pictures on the pages with them: another city opened (UserInterface.anotherCity()). |

### FINANCES (lines 82-249)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 11 | `static String[] pagesOf(String area)` | An area's pages, by its name; the position's for a name it does not know. |
| 164 | 10 | `static String[] iconsOf(String area)` | ...and their icons on the chips. |
| 196 | 3 | **type** `record Instrument(String key, String name, String short_, int[] terms, double rounding, String unit, String...` | One kind of paper the city can sell. |

### the floating-rate note and the inflation-linked bond on the screens (CD5, spec-city-debt.md 2.4, 2.5, 5.4 and 7) (lines 250-672)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 3 | `static boolean homeOnly(Instrument kit)` | Whether an instrument is home paper only (spec-city-debt.md 2.7): the amortizing bond, the floating-rate note and the inflation-linked bond have no dollar twin, so abroad their tiles are not drawn and a page that was ... |
| 270 | 5 | `static int abroadKinds()` | How many of the kinds are on offer abroad: the ones that are not home paper only. |
| 277 | 4 | `static String marginWords(double margin)` | A floater's margin over the policy rate in words (CD5): "policy + 18 bp", "policy − 15 bp", "policy flat" under half a basis point either way. |
| 283 | 3 | `static String dialPlusMargin(double dial, double margin)` | A floater's coupon laid out as it is made (CD5): "3.00% + 0.18%", the dial it follows and the margin over it; "3.00% − 0.15%" for a margin below nothing. |
| 288 | 5 | `static int nearestTerm(Instrument kit, int term)` | The one of an instrument's terms nearest this. |
| 295 | 4 | `static Instrument instrument(String key)` | An instrument by its key; the bond for a key it does not know. |
| 301 | 9 | `static Auction.Kind auctionKind(Instrument kit)` | The auction's name for an instrument's kind of paper. |
| 312 | 3 | `static int termMonths(Instrument kit, int term)` | An instrument's term as the months the auction counts it in: a bill's months, a bond's years times twelve. |
| 317 | 3 | `static String instrumentColour(String type)` | A kind's colour on the ladder, by Debt.getType(): DebtManager.LADDER_KINDS in Palette.LADDER's three. |
| 325 | 9 | `static String kindTag(String type)` | A piece's kind as its tag says it, by Debt.getType(): the keys are the model's, the words are the board's. |
| 416 | 10 | `void open(String area, String page, Object anchor)` | The tab opened on an area and a page (0.7.32, the spec's D17): the hub when `area` is null, an area's first page when `page` is not one of its own, and with `anchor` - LADDER or RESCUE on the hub, a piece of paper (a ... |
| 432 | 3 | `private void toTop()` | The page on show scrolled to its top (KP5): a chip or a door picked draws its page from the top, and another page is a scope of its own with a scroller of its own, which opens there; but the page the pick names may be... |
| 436 | 40 | `void showFinanceMenu()` |  |
| 482 | 1 | **type** `private record PageKey(String area, String page, Object open)` | What a page is, for the kept scope it is drawn in (KP5): the area, the page, and what is open on it - the piece a line's page or Close it now is on, a security of the fund. |
| 490 | 74 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck - the hub, or the area's page `financePage` names, on wha... |
| 566 | 25 | `private void frameOver(VBox frame, VBox page)` | The fixed frame over a scrolling page, at the page's width, the page as tall as what is left under the frame as laid out (GovernmentScreen's, 0.7.31). |
| 596 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (InfrastructureScreen's). |
| 609 | 4 | `void showOnHub(String target)` | On the hub, scroll to `target` where it is; anywhere else, the hub opened on it. |
| 636 | 4 | `static String areaInfo(String area)` | An area's (i) by its name. |
| 647 | 9 | `HBox head()` | The head: "Finances" with the money blue's swatch; on an area the breadcrumb "Finances › The book", the first word a way back. |
| 658 | 14 | `static Label backDoor(String text, Runnable go)` | A door back: "‹ Finances", in the accent, underlined under the pointer (Pieces.door()'s, pointing the other way). |

### THE FIVE FIGURES (0.7.32; four until then) (lines 673-851)

| line | len | member | says |
|---:|---:|---|---|
| 689 | 1 | **type** `record Kpi(String label, String value, String note, String tone, String where, String alarm)` | One of the five figures, worked out without drawing it: its label, its figure, its note, its colour, where its click goes, and the red line under it (null: none). |
| 692 | 67 | `List<Kpi> kpis(List<CityNeeds.Need> all)` | The five figures (pure: the probe reads them as the strip shows them). |
| 760 | 28 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 790 | 25 | `void withSpark(VBox cell, String series, String change)` | A limit cell with a year of a History series as a sparkline at the right of its figure, and a line under the note when `change` is not null (GovernmentScreen.withSpark()'s shape). |
| 817 | 13 | `String rateChange()` | THE RATE's move on last month, from History's last two points: "▲ 0.01 pts on last month"; null with fewer than two or under half a hundredth of a point (the spec's section 5). |
| 832 | 7 | `Debt soonest()` | The piece that falls due soonest, or null with nothing owed. |
| 841 | 4 | `static String urgency(int monthsOff)` | A maturity's colour, by how many months off it is: red inside FALLS DUE's line (NEEDS YOU's), amber inside FALLS_DUE_SOON_MONTHS, the headings' ink after. |
| 847 | 4 | `static int needLevel(List<CityNeeds.Need> all, CityNeeds.Kind kind)` | NEEDS YOU's level for a kind: 0 green, 1 amber, 2 red, -1 not measured. |

### THE ALERT BANDS (0.7.32): the hub's standing warnings, as one red line (lines 852-921)

| line | len | member | says |
|---:|---:|---|---|
| 859 | 38 | `List<Node> alertBands()` |  |
| 904 | 17 | `static HBox alertBand(String line, String whole, Node door)` | One alert band: a red ground, the alert icon, one line, its (i), and a door at the right (null: none). |

### the screen's own pieces (lines 922-983)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 1 | `String sym()` | The city's money's mark where a foreign figure is on the tab: "D$" (the spec's D9). |
| 928 | 4 | `String d(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k" (the spec's D9, B9). |
| 934 | 4 | `String dFull(double thousands)` | ...with every digit. |
| 940 | 5 | `String signedD(double thousands)` | ...signed, as a movement: "+D$1.2M", "−D$3.5M", "D$0". |
| 952 | 4 | **type** `record Gain(String money, String percent, String tone)` | What closing a line (or the book) would book, as the screen writes it (CD6, Jerus 2026-10-23: "show green or red, and a % value and dollar value"): the D$ with its sign, the share of the carrying value with its sign (... |
| 954 | 1 | `String both()` _(in FinancesScreen.Gain)_ | "+D$5.1M · +33.8%", "−D$725k · −4.2%", or just "D$0". |
| 958 | 8 | `Gain gainOf(double gain, double share)` | `gain` (carried less worth, thousands) and its `share` of the carrying value (the model's ifClosedNowShare()) written and coloured: see Gain. |
| 968 | 5 | `Label gainFigure(Gain gain, double size)` | A gain or a loss as a bold figure in its colour, right-aligned in the room it is given. |
| 975 | 5 | `HBox gainLine(String label, Gain gain, String info)` | A line of a card whose figure is a gain or a loss: the words at the left, the figure bold and in its colour at the right, an (i) after the words when `info` is not null. |
| 982 | 1 | `String perUsd(double rate)` | An exchange rate with its unit, as the land office writes it: "D$0.7035 per US$" (the spec's B10). |

### the city's paper in any of the eight moneys (CD8): the mark, the words and the figure of the money a piece is written in (lines 984-1201)

| line | len | member | says |
|---:|---:|---|---|
| 987 | 4 | `static String markOf(String code)` | The mark a figure in one of the eight's moneys is written with, by code: "US$", "€", "¥", "CHF " (ForeignAccounts.MONEY_MARK). |
| 993 | 1 | `static String pieceMark(Debt debt)` | The mark of the money a piece is written in. |
| 996 | 1 | `static String inMoney(String code, double thousands)` | A figure in the model's thousands in one of the eight's moneys: "US$2.4M" (Money.usd() to the letter), "€2.4M", "¥310M". |
| 999 | 1 | `static String inPiece(Debt debt, double thousands)` | ...in the money a piece is written in. |
| 1002 | 1 | `static java.util.function.DoubleFunction<String> figuresIn(String code)` | inMoney() as a function, for the formatter a card hands its figures to. |
| 1005 | 3 | `static String pieceTag(Debt debt)` | What a tag says of a piece's money and kind: "US$ BOND" abroad in dollars, "€ BOND" in euros, "BOND" at home. |
| 1010 | 4 | `static String moneyWords(String code)` | A money in the words that follow "in": "dollars" for the US$, as the screen has always said it, then "euros", "yen", "Canadian dollars" (ForeignAccounts.MONEY_PLURAL). |
| 1016 | 4 | `static String paperWord(String code)` | A money's paper, lower case: "dollar paper" for the US$, as the screen has always said it, then "euro paper", "yen paper", "Canadian dollar paper". |
| 1022 | 1 | `static String upFirst(String words)` | ...a sentence's first letter in capitals. |
| 1025 | 1 | `String perMoney(String code, double rate)` | An exchange rate with its unit for any of the eight: "D$1.0731 per €", "D$0.7035 per US$" (perUsd() to the letter). |
| 1028 | 1 | `static String pc(double share)` | A share as a whole per cent: "58%". |
| 1031 | 1 | `static String pc1(double share)` | ...to one place: "1.5%". |
| 1034 | 1 | `static String pts(double rate)` | Points of a rate, signed: "+0.02 pts". |
| 1037 | 1 | `static String pct3(double rate)` | A yield to three places, as an auction stops: "3.602%". |
| 1040 | 1 | `static String bp(double rate)` | A tail in basis points: "28 bp". |
| 1043 | 8 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 1053 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 1061 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 1072 | 9 | `static HBox caption(String text, String info, Node right)` | ...with words at its right. |
| 1083 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 14. |
| 1092 | 3 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour - red when the card is in alarm. |
| 1097 | 4 | `static String cardStyle(String ground, String edge)` | ...on a ground of its own: the PINNED card of a page's raised one (Your rate's, CD6). |
| 1103 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 1114 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 1121 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 1126 | 1 | `static Label line(String text)` | A plain line of words in a card, wrapping. |
| 1129 | 1 | `static Label muted(String text)` | ...muted. |
| 1132 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width (the spec's D10). |
| 1140 | 4 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 1146 | 8 | `static GridPane pair(Node left, Node right)` | Two nodes side by side in equal columns, each as tall as the taller. |
| 1156 | 7 | `static HBox swatch(String colour, String name, boolean ghost)` | A key's entry: a swatch and a name, the swatch outlined when `ghost`. |
| 1165 | 3 | `static Segment seg(double amount, String colour, String tip)` | A stretch of a segment bar with its tooltip. |
| 1170 | 3 | `static Segment ghost(double amount, String colour, String label, String tip)` | ...hollow: what would be there, or what is not. |
| 1175 | 5 | `static javafx.scene.layout.FlowPane keyRow(Node...entries)` | A row of a key: entries with a gap, wrapping. |
| 1182 | 7 | `void openPolicy(String area, String page)` | Policy, on one of its areas and pages, at its top (GovernmentScreen.openPolicy()'s). |
| 1195 | 6 | `void openTrade(String area, String page)` | Trade, on the page an area names: "The currency" and "The reserves" were Trade's areas until 0.7.35 and are its pages now - TradeScreen reads tradeArea as the page, so `page` is overwritten. |

### THE HUB (0.7.32) (lines 1202-1228)

| line | len | member | says |
|---:|---:|---|---|
| 1217 | 11 | `void hubPage(VBox page, List<CityNeeds.Need> all)` |  |

### when it falls due (lines 1229-1403)

| line | len | member | says |
|---:|---:|---|---|
| 1256 | 18 | `VBox ladderCard()` |  |
| 1294 | 27 | `Pieces.Columns ladderChart(DebtManager.Ladder ladder, String extraColour, String extraName, double height)` | The ladder as columns (Pieces.columns()): a column a calendar year, each kind's payments stacked from the foot - notes, serial bonds, term loans, Palette.LADDER's three - the part owed in dollars striped down its edge... |
| 1323 | 5 | `static double tallestYear(DebtManager.Ladder ladder)` | The tallest of the twelve years' columns, a proposed issue's ghost included. |
| 1330 | 4 | `static boolean laterBroken(DebtManager.Ladder ladder)` | Whether "later" is drawn broken (0.7.34): more than LATER_BREAK times the tallest year. |
| 1336 | 5 | `static double ladderScale(DebtManager.Ladder ladder)` | The ladder's scale: the tallest column - or, "later" broken, LATER_CAP tallest years, so the years fill the height. |
| 1343 | 11 | `List<Segment> rungParts(DebtManager.Rung r, String extraColour)` | A rung's segments, foot first: each kind at home, then its dollar part striped; then a proposed issue's, as a ghost. |
| 1356 | 16 | `String rungTip(DebtManager.Rung r, double revenueYear, String extraName)` | A column's tooltip: "2210 · D$579.0M: bonds D$579.0M, D$563.3M of it in dollars · 30% of a year's take" (the spec's section 3). |
| 1374 | 29 | `HBox ladderKey(DebtManager.Ladder ladder, String extraColour, String extraName)` | The ladder's key: only the kinds drawn, "in dollars" when any is, the proposed issue's ghost; at the right, everything still to pay. |

### next due (lines 1404-1486)

| line | len | member | says |
|---:|---:|---|---|
| 1421 | 30 | `VBox nextDueCard()` | NEXT DUE: the next five pieces to fall due, beside the ladder (0.7.32; a card at the top of the hub from 0.7.24, the window's foot until then). |
| 1453 | 20 | `HBox nextDueRow(Debt debt, int month)` | One piece on NEXT DUE: its amount in its urgency, its kind's tag and its date; a click opens the line's page (CD6). |
| 1475 | 11 | `String fellDueWords()` | What fell due at the last press, for the month after it (the spec's section 5): "D$272.0M fell due · rolled", off the rollover's record or, by hand, the principal the treasury repaid; null when nothing did. |

### the two settings (lines 1487-1488)

### ROLLING WHAT FALLS DUE (0.7.13) (lines 1489-1667)

| line | len | member | says |
|---:|---:|---|---|
| 1560 | 7 | `static String rolloverCapWords(Rollover.Mode mode)` | The line under the rollover's chips: what the cap is for, and what a short sale does (D29); by hand, that nothing is sold for you. |
| 1569 | 3 | `double rolloverFair()` | What the rollover's "at …" chip starts from: the 10-year bond's fair yield today, since the rollover sells many papers and has no one fair. |
| 1573 | 37 | `VBox rolloverCard()` |  |
| 1612 | 25 | `String planWords(Rollover.Plan plan)` | NEXT MONTH in one line (the spec's T37): what falls due and where each part of it goes - the bar's parts in words, so the bar needs no key. |
| 1639 | 14 | `String surplusWords(Rollover.Plan plan)` | NEXT MONTH's (i): the year's surplus and what is used of it, and the central bank's part (the old block's lines). |
| 1659 | 8 | `String lastRollWords()` | The last rollover, in a muted line (the spec's T38), or null when none has run: dated by the month what it rolled fell due in - the month after the press it ran at, Rollover.getLastMonth(). |

### WHEN THE BANK FAILS (0.7.14) (lines 1668-1758)

| line | len | member | says |
|---:|---:|---|---|
| 1693 | 44 | `VBox rescueCard()` |  |
| 1739 | 11 | `String settingsWords()` | The rollover and the rescue, as Borrow and the fund's Rules & cash (its Holdings until 0.7.39) point at them (the spec's D3): "What falls due rolls as the same structure; a failed bank is resolved automatically." |
| 1752 | 6 | `HBox settingsPointer()` | ...as a line with its door to the hub's two cards. |

### the six areas (lines 1759-1887)

| line | len | member | says |
|---:|---:|---|---|
| 1762 | 1 | **type** `record AreaCard(String area, String figure, String line, String lineTone, String chip)` | One area's card, worked out without drawing it: its figure, its line and the line's colour, and an amber chip (null: none). |
| 1765 | 41 | `List<AreaCard> areaWords()` | The six areas' figures (pure: the probe reads them as the cards show them). |
| 1807 | 12 | `GridPane areaCards()` |  |
| 1821 | 32 | `Node areaBar(int i)` | Each area's thin bar: the credit band, the principal by kind, the bank's room, M2's parts, the bonds' holders, the fund's shares against its aim. |
| 1855 | 32 | `VBox areaCard(int i, AreaCard w, Node bar)` | One area as a card (the spec's section 3 A.3): its icon, name and (i), its figure, its line, its chip, its bar; hover lights its edge, a click opens it. |

### THE POSITION (0.7.32) (lines 1888-2076)

| line | len | member | says |
|---:|---:|---|---|
| 1904 | 6 | `void positionPage(VBox page, List<CityNeeds.Need> all)` |  |
| 1927 | 38 | `VBox balanceCard()` | THE BALANCE: the cash against what is owed, two bars on one scale, and the net position (the spec's section 3 B). |
| 1967 | 25 | `VBox creditBandCard()` | THE CREDIT BAND: the rate on the band from what a spotless city pays to what a hopeless one does, its parts the floor and the two measures (the spec's T8 cut: the band labels its ends). |
| 1994 | 9 | `String economyInfo(double ratio)` | AGAINST THE ECONOMY's (i): the spec's T9, rewritten from the market's own measure (B4). |
| 2005 | 39 | `VBox economyCard()` | AGAINST THE ECONOMY: the debt, a year of its coupon and a year of revenue, each a share of a year's output, on one scale. |
| 2046 | 30 | `VBox owedChartCard()` | OWED AND THE RATE: City History's public debt and borrowing rate on two axes, the borrowing decisions as flags (the spec's section 5). |

### DEBT SERVICE (0.7.32) (lines 2077-2229)

| line | len | member | says |
|---:|---:|---|---|
| 2092 | 6 | `void debtServicePage(VBox page)` |  |
| 2110 | 41 | `Node serviceCard()` |  |
| 2168 | 39 | `VBox lastMonthCard()` | LAST MONTH: the interest on the city's paper (the coupon paid and the discount wearing off), and the principal that fell due at the last press split as the rollover booked it. |
| 2214 | 15 | `VBox foreignCard()` | FOREIGN: a year of the dollar paper's service on a bar of a year of exports, red only when the world has shut its window. |

### HOME AND ABROAD (0.7.32) (lines 2230-2348)

| line | len | member | says |
|---:|---:|---|---|
| 2254 | 20 | `void homeAndAbroadPage(VBox page)` |  |
| 2276 | 17 | `VBox holdersCard(double all)` | WHO HOLDS IT: the households, the bank, the central bank, the businesses (CD2, slice 4) and the world, one bar, each with its amount and share. |
| 2307 | 17 | `VBox dollarDebtCard()` | THE DOLLAR DEBT: dollars times the rate is the local figure; what the currency did to it last month as a chip. |
| 2333 | 15 | `VBox behindCard()` | WHAT IS BEHIND IT: the reserves, and the import cover on a year's bar with the model's comfortable line. |

### YOUR RATE, TAKEN APART (0.7.32; the curve first since CD6, 2026-10-23) (lines 2349-2887)

| line | len | member | says |
|---:|---:|---|---|
| 2398 | 11 | `void yourRatePage(VBox page)` |  |
| 2411 | 17 | `static GridPane wideAndNarrow(Node left, Node right)` | Two cards in the page's wide and narrow columns, two thirds and one third, each as tall as the taller. |
| 2430 | 15 | `String floorInfo()` | The floor's (i): the spec's T23, rewritten (B13), and the central bank's share (0.7.15). |
| 2447 | 29 | `VBox builtUpCard()` | THE RATE, BUILT UP: the floor and the two measures on the band from nothing to the ceiling, the dial and the city's rate marked, then each part with what moves it. |
| 2478 | 9 | `static VBox rateCell(String name, String value, String moves, Node door, String colour)` | One part of the rate: its swatch and name, its figure, what moves it, and its door. |
| 2501 | 44 | `VBox curveCard()` | THE CITY'S CURVE: today's zero rates by maturity, a year ago and two years ago, the world's for dollar paper and the dial the market expects, with each line named under it. |
| 2547 | 5 | `static Label captionWords(String text)` | A caption-sized muted line of words that does not wrap: an entry of a key. |
| 2554 | 18 | `static HBox lineKey(String colour, double width, double[] dash, String name)` | A key's entry for a line: a short stroke in the line's colour, width and dash, and its name. |
| 2574 | 16 | `static HBox dotKey(String colour, String name)` | A key's entry for a dot off the lines: a dot in the colour, as the chart draws it, and its name. |
| 2597 | 96 | **type** `static final class CurveChart extends Drawn` | Lines on the curve's chart (CD6, board 5), months across to the square root as the desk's curve draws them, so the two curve pictures are one family: any number of series, each at its own months with its own colour, w... |
| 2602 | 1 | **type** `record Series(String name, int[] months, double[] values, String colour, double width, double[] dash, Strin...` _(in FinancesScreen.CurveChart)_ | One line: its name (a tooltip's), the months each value is at, the values, its colour, its width, its dash (empty: solid), its name at its end (null: none), and whether each of its points is marked with a dot. |
| 2605 | 1 | **type** `record Dot(double months, double value, String colour, String tip)` _(in FinancesScreen.CurveChart)_ | A mark off the lines: months, value, colour and what its tooltip says. |
| 2615 | 4 | `CurveChart(double height)` _(in FinancesScreen.CurveChart)_ | A chart `height` px tall, with its lines to come from set() (Kept, KP5: a kept chart is told each month's lines and draws again only where they moved). |
| 2621 | 13 | `void set(int[] axis, List<Series> series, List<Dot> dots)` _(in FinancesScreen.CurveChart)_ | The chart of these lines on an axis that ends at the last of `axis`' tenors: drawn again only when a figure of them moved. |
| 2635 | 57 | `void draw(double w)` _(in FinancesScreen.CurveChart)_ |  |
| 2705 | 15 | `VBox expectCard()` | WHAT THE MARKET EXPECTS THE DIAL TO DO: the dial, the rule, and where the market expects the dial in a year, in five and at last, and what that says. |
| 2725 | 23 | `static String expectWords(DebtManager l, double credibility)` | The board's flag in the game's words: which way the rule and the path lean, and that the inflation the city expects settles off the target by as much as credibility falls short. |
| 2750 | 6 | `static VBox flag(String text, String ground)` | A note on a card: words on a ground a step off the card's, with an edge. |
| 2764 | 27 | `VBox partsCard()` | THE 10-YEAR RATE, TAKEN APART: a bar of its parts, a line each and the total, with the thirty-year point's sentence under it. |
| 2793 | 5 | `static HBox partLine(String colour, boolean ghost, String label, String value)` | One part of the rate: its swatch (outlined when it is what is taken off), its words and its figure. |
| 2807 | 43 | `VBox realCard()` | REAL RATES, AND THE INFLATION THE MARKET PRICES: by maturity from a year out, the real rate a linked bond would sell at and the inflation priced, the target across, a dot a linked line on the book. |
| 2852 | 6 | `static String measuresInfo()` | WHAT EACH MEASURE HAS USED's (i): the spec's T25. |
| 2860 | 15 | `VBox measuresCard()` | WHAT EACH MEASURE HAS USED: how much of its worst case each of the two measures has used, and a default's scar abroad. |
| 2877 | 10 | `static VBox stressRow(String label, double stress)` | One measure: its name, a bar of how much of its worst case it has used, and the share. |

### THE BOOK (0.7.32; a desk since CD6, 2026-10-23) (lines 2888-2959)

| line | len | member | says |
|---:|---:|---|---|
| 2942 | 12 | `String bookInfo()` | The book's (i): the spec's T32 and T34. |

### the books (CD3) (lines 2960-3542)

| line | len | member | says |
|---:|---:|---|---|
| 3058 | 31 | `void bookPage(VBox page)` | THE BOOK, AS A DESK (CD6, spec-city-debt.md section 7, board 1): the book's six figures, where each line sits on today's curve, the book at face, carried and worth in bars, and every line a row by when it falls due - ... |
| 3095 | 17 | `static GridPane figureStrip(String[][] cells)` | Figures in the strip's look (CD6): a label, a figure and a note in each of equal columns on the strip's ground, a hairline between. |
| 3114 | 7 | `static String bookKindColour(String type)` | A kind's colour on the desk (CD6): the ladder's three for the bills, the amortizing bonds and the bonds, and a floater's and a linker's of their own, which the ladder folds into the bonds. |
| 3126 | 4 | `static int bookKindOf(String type)` | A type's place in DebtManager.BOOK_KINDS; a type that is none of them is a term loan's (the manager's own reading). |
| 3132 | 9 | `static List<Node> kindKey(boolean[] present)` | The kinds a key names: a swatch for each kind that is in `present`, in the kinds' colours. |
| 3143 | 34 | `VBox deskCurveCard(BookDesk desk, DebtManager ledger)` | WHERE EACH LINE SITS ON THE CURVE: today's zero curve at DebtManager.CURVE_TENORS, a dot a line at (months left, yield). |
| 3179 | 27 | `VBox deskBarsCard(BookDesk desk)` | AT FACE, CARRIED, AND WORTH TODAY: three bars on one scale, each stacked by kind, and what the book is worth across all three. |
| 3208 | 4 | `static boolean holdsDollars(BookDesk desk)` | CD7: whether the book holds any dollar paper (a foreign line): the desk's exchange column and the card's exchange lines show only then. |
| 3214 | 124 | `VBox deskCard(BookDesk desk)` | EVERY LINE: the desk, a row a line by when it falls due; a click on a row opens the line's page, and the ladder's columns and NEXT DUE scroll to its row. |
| 3358 | 19 | `Node closeCell(BookDesk.Line l)` | The desk's If closed now cell (CD6, Jerus 2026-10-23: "make it juicy to buyback"): what closing the line now would book - bold, in D$ and as a share of the carrying value, green for a gain and red for a loss - and, ri... |
| 3383 | 24 | `static Node acrossWhenNarrow(javafx.scene.layout.Region table)` | A table that scrolls across when the window is narrower than it, and fills the card when it is wider. |
| 3412 | 9 | `private static void fitAcross(Object scroller)` | The scroller told the height its table laid out at, and room for its bar when the table is wider than it (the pair as the last draw left it). |
| 3423 | 27 | `Node lineCell(BookDesk.Line l)` | The desk's first cell: the kind's tag, the line's name and, muted under them, its term, when it was sold, what it has left, and whether it was reopened or can be called. |
| 3452 | 8 | `static String lineCoupon(BookDesk.Line l)` | A line's coupon in words (CD6): a bill "discount", a floater "policy + 18 bp", a linker "1⅝% real", anything else its coupon in eighths. |
| 3462 | 9 | `static Node stacked(String figure, String under)` | A table figure with a muted word under it, right-aligned: "3.84%" over "real". |
| 3473 | 8 | `static List<Segment> holderParts(BookDesk.Line l)` | A line's holders as shares of its face, in the holders bar's colours: the households, the bank, the central bank, the businesses and the world. |
| 3487 | 9 | `static String couponWords(double coupon)` | A coupon in eighths of a point, the way a bond is quoted: 0.035 is "3½%", 0.04125 "4⅛%", 0.00125 "⅛%". |
| 3507 | 22 | `static String pieceName(Debt debt)` | A piece's name in words (CD2): a bond at home is its line, "3½% Jan 2211" - its coupon in eighths and the month and year it falls due, as a treasury names a line -; a bill "6-month bill"; an amortizing bond "20-year a... |
| 3531 | 11 | `Node holdersBar(Debt debt)` | Who holds a piece, one bar (the card's, and the Close it now page's): the households, the bank, the central bank, the businesses and the world; "held abroad" for a dollar piece. |

### a line's page (CD6, board 2) (lines 3543-3945)

| line | len | member | says |
|---:|---:|---|---|
| 3608 | 4 | `boolean lineOpenNow()` | True while a line's page is open on a line that is still on the books; else the book is drawn in its place. |
| 3614 | 5 | `void openLine(Debt debt)` | A desk row's, or NEXT DUE's, door: the page of this line. |
| 3621 | 10 | `static String kindNoun(Debt debt)` | A line named by its term and kind: "10-year bond", "6-month bill", "2-year floating-rate note", "5-year bond in dollars", "5-year bond in euros". |
| 3637 | 14 | `String honestSentence(Debt debt, Buyback.Result q)` | THE HONEST LINE's sentence (CD6, spec-city-debt.md 6.3), for a buyback of this quote: what it stops a month of coupon and what the price paid, borrowed again at today's rate for the months the line has left, would cos... |
| 3653 | 169 | `void linePage(VBox page)` | A line's page (CD6, board 2): see the banner above. |
| 3830 | 5 | `static void arrangeThree(VBox books, VBox holders, VBox sales, boolean narrow)` | The three cards of a line's page on a grid of twelve columns: 6, 3 and 3 across, or, narrow, the first over the other two. |
| 3837 | 17 | `VBox booksCard(Debt debt, int month, BookDesk.Line l)` | THE NEXT SIX MONTHS ON THE BOOKS: Debt.booksAhead(6) - opening, interest, coupon, amortised, repaid (only when any is) and closing. |
| 3856 | 22 | `private Node booksTable(List<Debt.BooksMonth> ahead, int month, boolean repaid)` | The books' table: a row a month, in six columns, or seven where any of the months repays principal. |
| 3880 | 24 | `VBox holdersCard(Debt debt, BookDesk.Line l)` | WHO HOLDS IT: the holders bar and a line a holder with its face and its share. |
| 3906 | 8 | `static HBox holderLine(String who, String colour, String text)` | One holder's line: its swatch and name at the left, its face and its share at the right. |
| 3916 | 29 | `VBox salesCard(Debt debt, BookDesk.Line l)` | ITS SALES, AND ITS RISK: each sale as it was made, then its duration and the value of a point. |

### the desk's pictures (CD6) (lines 3946-4328)

| line | len | member | says |
|---:|---:|---|---|
| 3952 | 119 | **type** `abstract static class Drawn extends javafx.scene.layout.Pane` | A picture that draws itself again at whatever width it is given and a fixed height (BuybackLadder's way): the desk's curve and bars and a line's price path sit in half a page, at 900 px or at 1,480. |
| 3958 | 6 | `Drawn(double least, double first, double height)` _(in FinancesScreen.Drawn)_ |  |
| 3966 | 1 | `abstract void draw(double w)` _(in FinancesScreen.Drawn)_ | Everything in the picture, drawn at this width: the children have been cleared. |
| 3972 | 5 | `final boolean moved(Object...figures)` _(in FinancesScreen.Drawn)_ | Whether these figures are other than the ones the picture was last drawn on - compared by content, arrays and nested arrays included - and remembered: the first telling is always a move. |
| 3979 | 5 | `final void drawAgain()` _(in FinancesScreen.Drawn)_ | Everything drawn again, at the width it stands at (the first width before it has stood anywhere): its figures have moved. |
| 3985 | 9 | `protected void layoutChildren()` _(in FinancesScreen.Drawn)_ |  |
| 3996 | 13 | `final Label put(String text, double x, double y, double width, Pos align, String style)` _(in FinancesScreen.Drawn)_ | Words at a place, their top left at (x, y); in a box `width` wide, aligned, when width is above nothing. |
| 4011 | 1 | `static String wordsIn(String tone)` _(in FinancesScreen.Drawn)_ | The caption's words in a tone. |
| 4014 | 1 | `static String figureIn(String tone)` _(in FinancesScreen.Drawn)_ | A figure's, in a tone. |
| 4017 | 8 | `final javafx.scene.shape.Line stroke(double x1, double y1, double x2, double y2, String colour, double width, double...dash)` _(in FinancesScreen.Drawn)_ | A straight stroke. |
| 4027 | 5 | `static Tooltip tip(String text)` _(in FinancesScreen.Drawn)_ | A tooltip that shows at once. |
| 4034 | 3 | `static String tenorName(int months)` _(in FinancesScreen.Drawn)_ | A tenor as the axis writes it: "3m", "1y". |
| 4042 | 16 | `final void tenorAxis(int[] tenors, java.util.function.DoubleUnaryOperator x, double plotB)` _(in FinancesScreen.Drawn)_ | The tenors' axis along `plotB` (CD6): every tenor has its tick, and the labels claim room round ones first (a year, five, ten, thirty...), none within 26 px of another. |
| 4060 | 10 | `static double niceStep(double span, int ticks, double...steps)` _(in FinancesScreen.Drawn)_ | A round number to put the axis's steps on: the smallest of these that fits the span into `ticks` steps or fewer. |
| 4078 | 99 | **type** `static final class DeskCurve extends Drawn` | WHERE EACH LINE SITS ON THE CURVE (CD6, board 1): today's zero curve at the tenors, months across (to the square root, as the board draws it, so the short end has room) and the yield up; each line a dot at (months lef... |
| 4080 | 1 | **type** `record Dot(double months, double yield, String colour, boolean hollow, String tip, String name)` _(in FinancesScreen.DeskCurve)_ | One line on the curve: its months left, its yield, its colour, whether it is dollar paper (hollow), what its tooltip says, and its name beside the dot (null: none). |
| 4089 | 3 | `DeskCurve()` _(in FinancesScreen.DeskCurve)_ | The picture, its curve and dots to come from set() (Kept, KP5). |
| 4094 | 7 | `void set(int[] tenors, double[] zero, List<Dot> dots)` _(in FinancesScreen.DeskCurve)_ | Today's curve at its tenors and a dot a line: drawn again only when a figure moved. |
| 4102 | 6 | `private static boolean overlaps(double[] box, List<double[]> taken)` _(in FinancesScreen.DeskCurve)_ |  |
| 4109 | 67 | `void draw(double w)` _(in FinancesScreen.DeskCurve)_ |  |
| 4182 | 53 | **type** `static final class DeskBars extends Drawn` | AT FACE, CARRIED, AND WORTH TODAY (CD6, board 1): three bars on one scale, each stacked by kind in the kinds' colours with its total at its end, and what the book is worth marked across all three as a dashed line. |
| 4191 | 3 | `DeskBars()` _(in FinancesScreen.DeskBars)_ | The picture, its bars to come from set() (Kept, KP5). |
| 4196 | 7 | `void set(double[][] byKind, double worth, java.util.function.DoubleFunction<String> money)` _(in FinancesScreen.DeskBars)_ | The three bars by kind and what the book is worth: drawn again only when a figure moved. |
| 4204 | 30 | `void draw(double w)` _(in FinancesScreen.DeskBars)_ |  |
| 4241 | 87 | **type** `static final class PathChart extends Drawn` | ITS PRICE SINCE IT WAS SOLD (CD6, board 2): the price per 100 of face a month, in the money blue, and what the line is carried at per 100, muted and dashed, against a horizontal at 100, the axis in years. |
| 4248 | 3 | `PathChart()` _(in FinancesScreen.PathChart)_ | The picture, its two paths to come from set() (Kept, KP5). |
| 4253 | 7 | `void set(double[] price, double[] carried, int firstMonth)` _(in FinancesScreen.PathChart)_ | The price and what is carried, month by month from `firstMonth`: drawn again only when a figure moved. |
| 4262 | 4 | `private static boolean ticked(int month, int step)` _(in FinancesScreen.PathChart)_ | Whether a month carries a tick of this step: January's of the years it counts in, or the months it counts in. |
| 4267 | 49 | `void draw(double w)` _(in FinancesScreen.PathChart)_ |  |
| 4317 | 10 | `private void polyline(double[] values, java.util.function.IntToDoubleFunction x, java.util.function.DoubleUnaryOperator y, Stri...` _(in FinancesScreen.PathChart)_ |  |

### CLOSE IT NOW (CD4, 2026-10-23) (lines 4329-4949)

| line | len | member | says |
|---:|---:|---|---|
| 4399 | 8 | `static String holderColour(Buyback.Holder holder)` | The colour a holder has on the Close it now page: the same as on the card's holders bar. |
| 4409 | 8 | `static String holderName(Buyback.Holder holder)` | ...and its name. |
| 4419 | 9 | `static double perHundredFromWords(String typed)` | A most-to-pay typed: dollars for each 100 of face, "98.5" (a comma may stand for the point); NaN when it is not a number above nothing and under a thousand. |
| 4430 | 4 | `static String termWords(int months)` | A term of this many months in words, as the honest line names the rate: "6-month", "10-year", "7.5-year". |
| 4436 | 4 | `boolean closeOpen()` | True while the Close it now page is open on a piece that is still on the books. |
| 4442 | 8 | `void openClose(Debt debt)` | The card's Close it now: the page opened on this piece, its boxes empty (everything on offer, no limit). |
| 4452 | 14 | `private void setCloseBox(int which, String text)` | One of the two boxes set to this, or emptied, and the page drawn on it. |
| 4474 | 34 | `private javafx.scene.control.TextField closeBox(int which, String prompt)` | One of the Close it now page's boxes (1 the face, 2 the most to pay): the house's search box, its text kept on the screen as it is typed so a month's redraw puts it back, and the focus with it. |
| 4513 | 3 | `static String boxAmount(double thousands)` | A face as a box takes it back: whole dollars with commas ("1,250,000,000"), so a chip that fills the box says it exactly. |
| 4522 | 21 | `String closeWhyNot(Buyback.Result q, Debt debt, double offerable, double asked, double typedMax)` | Why a ticket takes nothing, in a sentence (null when it takes something): nothing on offer, nothing asked, a sale not yet settled that can only go whole, or a limit under the cheapest offer. |
| 4545 | 182 | `void closePage(VBox page)` | The Close it now page (CD4): one piece's ticket, its offers, the three comparisons and the journal, the honest line and, on a callable line, the call. |
| 4729 | 8 | `HBox takenLine(String who, String colour, double face, double paid)` | One holder's line on the ticket: its swatch and name, what was taken from it, and what it is paid. |
| 4744 | 47 | `VBox callCard(LongTermBond bond)` | THE CALL (spec-city-debt.md 6.4, D22 to D24), on a callable line's page: from when it can be called, what it costs, the gain or loss against carrying value (carrying less the face), what it saves in present value and ... |
| 4803 | 146 | **type** `static final class BuybackLadder extends javafx.scene.layout.Pane` | The reverse auction's offers, drawn (board 3): a row a slice, the cheapest first, the price for each 100 at the left and the slice's face after its bar; a colour a holder, the slice outlined and faintly filled as a gh... |
| 4817 | 1 | `BuybackLadder()` _(in FinancesScreen.BuybackLadder)_ | The picture, its offers to come from set() (Kept, KP5: a kept ladder is told each month's quote and draws again only where it moved). |
| 4820 | 17 | `void set(Buyback.Result q, double maxPer100, java.util.function.DoubleFunction<String> money)` _(in FinancesScreen.BuybackLadder)_ | The quote to draw and the most to pay: drawn again, and its height set to its slices, only when either moved. |
| 4844 | 4 | `static boolean drawsAlike(Buyback.Result a, Buyback.Result b)` _(in FinancesScreen.BuybackLadder)_ | Whether two quotes draw the same ladder: the offers, the face taken and the fair price, all the picture reads. |
| 4849 | 5 | `protected void layoutChildren()` _(in FinancesScreen.BuybackLadder)_ |  |
| 4856 | 69 | `private void draw(double w)` _(in FinancesScreen.BuybackLadder)_ | The ladder at a width: everything in it, drawn again from the quote. |
| 4927 | 7 | `private void horizontal(double y, double right, String colour)` _(in FinancesScreen.BuybackLadder)_ | A dashed horizontal line across the plot at a height, in a colour. |
| 4936 | 12 | `private void label(String text, double x, double y, javafx.geometry.Pos align, double wide, String colour)` _(in FinancesScreen.BuybackLadder)_ | A label: `x` is where the text starts, ends or centres by `align`, in a box `wide` pixels across (AuctionChart's). |

### BORROW (0.7.32) (lines 4950-5370)

| line | len | member | says |
|---:|---:|---|---|
| 5040 | 9 | `static String dollarsInfo(String money)` | ...where the money borrowed is another of the eight (CD8): the same trade, said in that money. |
| 5050 | 68 | `void borrowPage(VBox page, boolean foreign)` |  |
| 5127 | 4 | `static String moneyChip(String code)` | A money on its chip: its mark and code, "€ EUR"; the code alone where the mark is the code ("CHF"). |
| 5140 | 50 | `VBox moneyCard(Instrument kit)` | IN WHICH MONEY (CD8, Jerus 2026-10-23: "Borrow gets the choice of money, with each money's fair yield beside it and the risk said plainly (a falling D$ raises what the debt costs)"): one chip a money of WorldMoneys.EI... |
| 5195 | 16 | `void pickMoney(String code)` | A money picked on the chips: the ask, which is in the money's own units, is carried across at the month's cross rates and rounded to a lot (so a US$ ask does not become a yen-sized one on the click), what was typed go... |
| 5218 | 71 | `VBox askCard(Instrument kit, boolean foreign, DebtQuote quote, double fair)` | THE ASK: the kinds as tiles, the terms as columns of today's fair yield (chips abroad), how much, and at home the highest yield the city will take. |
| 5296 | 27 | `String lineNote(Instrument kit)` | What the term's line is, in a sentence, under the columns (the board's bw-line): a bond sold into the line it reopens, or opening a new one; a bill, an amortizing bond, a floater or a linker is always its own. |
| 5335 | 3 | `boolean callableOffered(Instrument kit, boolean foreign)` | True when the Borrow page's bond may be sold callable (CD4): a bond at home of ten years or more. |
| 5340 | 3 | `boolean callableAsked(Instrument kit, boolean foreign)` | True when the switch is on and the paper may be callable: the quote, the booking and the line note all read this. |
| 5351 | 13 | `VBox callableSwitch(DebtQuote quote)` | The CALLABLE switch (CD4), beside the bond's term: two chips, off by default, kept across the clock's redraw in borrowCallable. |
| 5366 | 4 | `HBox callableLine()` | A callable sale's line on the auction card (CD4): from when it can be called and what the call adds to the coupon. |

### the highest yield (CD2) (lines 5371-5381)

| line | len | member | says |
|---:|---:|---|---|
| 5378 | 3 | `List<Node> capControls(double fair)` | THE HIGHEST YIELD: fair + a point (the default), a yield the player types in per cent, or Fill it - the cap chips (CapChips), bound to the Borrow page's own cap. |

### the ask, typed (0.7.40) (lines 5382-5940)

| line | len | member | says |
|---:|---:|---|---|
| 5393 | 5 | `static String askTypedInfo(String money)` | ...abroad in one of the eight's moneys (CD8): the same rules, the amount in that money, its mark ignored in front. |
| 5404 | 14 | `static double askFromWords(String typed)` | The ask a typed amount sets, in the model's thousands (Money.toDollars()'s unit), by ASK_TYPED_INFO's rules; NaN when it is not an amount. |
| 5420 | 4 | `static String askRefusal(String typed)` | What the box says under itself when an entry cannot be read. |
| 5426 | 6 | `String[] askShown(double ask, boolean foreign, String money)` | The ask written in full and short: {"D$2,500,000,000,000", "D$2.5T"} - the second null when it would say the same; nothing asked, {"nothing asked for yet", null}. |
| 5434 | 6 | `static double[] askSteps(double ask, double lot)` | The ± steps for an ask: one unit of its leading digit and a tenth of that - D$1B and D$100M at D$3.4B - never under a lot; at nothing, one lot. |
| 5442 | 4 | `double askBase()` | What a step or a scale starts from: what is typed and not yet set, when it reads as an amount, else the ask. |
| 5448 | 7 | `void setAsk(double ask)` | The ask set by a step, a preset or clear: what was typed and the refusal go, and the page is drawn on it. |
| 5457 | 1 | **type** `record AskPreset(String name, double ask)` | One preset of the ask: what it is called, and the model's figure it sets. |
| 5473 | 16 | `List<AskPreset> askPresets(Instrument kit, boolean foreign)` | The ask's presets, every figure a model getter's, each offered only when it is something: at home, the minimum issue (at least a lot, as "the minimum" always set it), what falls due in the next twelve months (DebtMana... |
| 5497 | 41 | `Node askBox(boolean foreign)` | The box the ask is typed in (0.7.40), the house's search box: what is typed and not yet set lives through a month's redraw, and the focus with it. |
| 5546 | 25 | `private void commitAsk(javafx.scene.control.TextField box, boolean enter)` | What is in the box, set as the ask - or, when it cannot be read, said under the box with the ask left as it was. |
| 5573 | 3 | `static String abroadLine(Instrument kit)` | A kind's tile line abroad: dollar paper is the world's, at its price, not an auction's (spec-city-debt.md 2.7). |
| 5578 | 8 | `static String abroadBlurb(Instrument kit, String money)` | ...and its (i): what paper abroad is, in the money it is asked in (CD8; the US$'s words are the ones it has always had). |
| 5592 | 27 | `VBox instrumentTile(Instrument kit, boolean on, boolean foreign)` | One instrument as a tile: its swatch-tinted icon, its name and (i), the range it is issued over, its line; picked, its ground and edge lit. |
| 5629 | 65 | `Node termColumns(Instrument kit, DebtQuote quote)` | The terms at home as columns of today's fair yield (DebtManager. |
| 5696 | 7 | `static String fairWord(Instrument kit)` | What the term columns' fair figure is called, by kind (CD5): a yield for a bill and the bonds, a margin for a floater, a real coupon for a linker. |
| 5705 | 22 | `Node termChips(Instrument kit)` | The terms abroad as chips, each with the world's rate for it in its tooltip: in the money asked in (CD8), that money's curve, and the tooltip names it. |
| 5729 | 10 | `String quoteName(Instrument kit, boolean foreign)` | What the quoted paper is called on its card: "10-year bond", "6-month bill", "20-year amortizing bond", "5-year floating-rate note", "30-year inflation-linked bond", "... |
| 5745 | 48 | `VBox quoteCard(Instrument kit, DebtQuote quote, boolean foreign)` | THE QUOTE: the offer card on exactly the terms the button books. |
| 5803 | 93 | `VBox auctionQuote(Instrument kit, DebtQuote quote, Auction.Result sale)` | THE QUOTE at home (CD2): the offer card of a sale at auction, in the land office's shape - the paper's name and (i), what the sale settles at, the face, the cash, the monthly cost and the cost all in, when it falls du... |
| 5898 | 7 | `static String monthlyWords(Instrument kit)` | What the monthly cost is read at (CD5): a floater's at today's dial, a linker's at today's index; nothing to add for the rest. |
| 5907 | 7 | `static String totalWords(Instrument kit)` | The cost of the credit's label (CD5): a floater's is at today's dial, a linker's before any indexation; the rest, all in. |
| 5920 | 14 | `String settlesAt(Instrument kit, Auction.Result r)` | What a sale settles at, in a line: a bill's yield and its price for 100; a bond's coupon, line and yield; the amortizing bond's one coupon; a floater's margin over the policy rate, reset monthly, and the coupon it mak... |
| 5936 | 4 | `static String tailTone(Auction.Result r)` | The tail's colour: none is good, some is amber, the highest yield reached is red. |

### the auction, shown (CD2) (lines 5941-6368)

| line | len | member | says |
|---:|---:|---|---|
| 5959 | 69 | `VBox auctionCard(Instrument kit, Auction.Result r, DebtQuote quote)` | THE AUCTION: the four bids against yield with the issue, fair and the highest yield drawn (AuctionChart), then the result line by line, and the bank's room. |
| 6030 | 3 | `HBox fillLine(String who, double face, double sold)` | One bidder's fill: its face, and its share of what was sold. |
| 6040 | 24 | `Node shortCard(Auction.Result r)` | THE RED CARD (the spec's section 7, board 4): a sale the bids would not cover. |
| 6071 | 35 | `List<Node> bankRoomRows(Auction.Result r)` | THE BANK'S ROOM, under the auction (what WHO BUYS IT's bar was, before CD2): its book, risk-weighted, against its room, with what the bank takes of this sale as a ghost; or why it bids nothing - failed, or no room. |
| 6120 | 164 | **type** `static final class AuctionChart extends Drawn` | The four bids against yield, drawn (board 4): the households, the firms, the world and the bank stacked from the foot, so the top edge is the four summed, over a yield axis from fair less a fifth of a point to the hig... |
| 6135 | 3 | `AuctionChart()` _(in FinancesScreen.AuctionChart)_ | The picture, its sale to come from set() (Kept, KP5). |
| 6140 | 7 | `void set(Auction.Result r, Auction.Bidders bidders, java.util.function.DoubleFunction<String> money)` _(in FinancesScreen.AuctionChart)_ | The sale, and who bids on it: drawn again only when either moved (the money's words are the screen's, a function of the city's mark). |
| 6149 | 107 | `void draw(double w)` _(in FinancesScreen.AuctionChart)_ | The chart at a width: everything in it, drawn again from the sale (the children have been cleared). |
| 6258 | 7 | `private void vertical(double x, String colour, double from, double to)` _(in FinancesScreen.AuctionChart)_ | A dashed vertical line at x, in a colour, between two heights. |
| 6271 | 12 | `private void label(String text, double x, double y, javafx.geometry.Pos align, double wide, String colour)` _(in FinancesScreen.AuctionChart)_ | A label of the chart's: `x` is where the text starts, ends or centres by `align`, in a box `wide` pixels across (a Pane gives its children their preferred size, so the box is the alignment's room). |
| 6286 | 15 | `VBox dollarsCard()` | AND THE DOLLARS: convert and spend, or hold as reserves (the spec's W8); AND THE EUROS, YEN... |
| 6303 | 12 | `HBox foreignDoor()` | The door marked do not open, outlined in red. |
| 6327 | 5 | `void showForeignDefaultMenu()` | Asking twice, with the bill written out: "Finances › Default abroad" (0.7.32, the spec's D14), two equal cards in the same type, "Keep paying" first. |
| 6334 | 34 | `void drawForeignDefault()` | The default page itself, put in the window through setPage(): what keepMenu runs as a kept draw, and what KeptBench runs kept and built new. |

### MONEY (0.7.0; redrawn 0.7.32) (lines 6369-6544)

| line | len | member | says |
|---:|---:|---|---|
| 6384 | 16 | `String moneyInfo()` | The page's (i): the old page's two sentences, the spec's T64 to T66. |
| 6401 | 18 | `void moneyPage(VBox page)` |  |
| 6421 | 15 | `String centralBookInfo(CentralBank cb)` | The book's (i): the spec's T67 and T68. |
| 6438 | 32 | `VBox centralBookCard(CentralBank cb)` | THE CENTRAL BANK'S BOOK: what it holds against what it owes, one scale; its equity on the owing side, or a shortfall red on the holding side. |
| 6477 | 17 | `VBox publicCard(CentralBank cb)` | WHAT THE PUBLIC HOLDS: M2 as a bar of its parts, and M0 beside it. |
| 6496 | 28 | `VBox thisMonthCard(CentralBank cb)` | THIS MONTH: the money made and the money destroyed, each a bar of its parts on one scale, and what M0 moved by. |
| 6526 | 18 | `VBox smallChart(String title, HistorySave h, String[] keys, String[] names, String[] colours)` | History's money series as a small chart without the controls, on the last ten years (ChartModel.DEFAULT_RANGE) or the whole history while it is younger - TimeChart, as Government's Output draws it. |

### THE BOND MARKET (0.7.12; redrawn 0.7.32) (lines 6545-6912)

| line | len | member | says |
|---:|---:|---|---|
| 6560 | 3 | `static String per100(double price)` | A bond's price, per 100 of face. |
| 6588 | 16 | `Node holdersBar(double[] held, double band, boolean key)` | A holders bar over five amounts, each keyed when `key` is set. |
| 6605 | 38 | `void bondMarketPage(VBox page)` |  |
| 6645 | 25 | `VBox issuerCard(BondMarket.Issuer is)` | One issuer as a card: its sector's icon, its face, its bonds and their coupons, its nearest maturity, its holders; a click opens its largest bond's book. |
| 6672 | 27 | `VBox bondMonthCard(BondMarket market)` | THIS MONTH on the bond market: sold, coupons, repaid, written off, and the last issue. |
| 6701 | 14 | `VBox orderBooksCard(BondMarket market)` | THE ORDER BOOKS, last month: offered for sale, how much of it sold, and how many sellers waited. |
| 6717 | 44 | `Node bondGrids(BondMarket market)` | The old page's two tables, in the fold: every bond with its price and yield, and who holds each. |
| 6771 | 49 | `void bondBookPage(VBox page)` |  |
| 6822 | 27 | `Node depthChart(OrderBook book, CorporateBond b, int month)` | The book's depth: a row a price level, the asks over the bids, best nearest the middle; bids drawn leftward, asks rightward, face as length. |
| 6851 | 27 | `Node depthRow(OrderBook.Level l, boolean bid, double most, CorporateBond b, int month)` | One price level of the depth chart. |
| 6880 | 32 | `Node levelGrids(OrderBook book, CorporateBond b, int month)` | The old page's bid and ask tables, in the fold. |

### THE CITY'S FUND (0.7.14; redrawn 0.7.32; its own class since 0.7.39) (lines 6913-6926)

### THE DEBT RESULT (lines 6927-7038)

| line | len | member | says |
|---:|---:|---|---|
| 6946 | 3 | `String executeDebtLogic(String type, double amount, int duration, double rounding, Auction.Cap cap)` | Books the sale the page quoted, at the highest yield it was quoted at (CD2): the handler struck on the same city at the same cap. |
| 6951 | 11 | `String executeDebtLogic(String type, double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...a bond sold callable if the quote was (CD4): the handler struck on the same city, at the same cap, with the same option. |
| 6964 | 6 | `void showDebtResultMenu(DebtQuote quote, boolean foreign, String money, String summary)` | Shows the terms the player just agreed to: the quote booked as a receipt, and the ladder with it on the books; or what the booking said when nothing was booked. |
| 6972 | 66 | `void drawDebtResult(DebtQuote quote, boolean foreign, String money, String summary)` | The receipt itself, put in the window through setPage(): what keepMenu runs as a kept draw, and what KeptBench runs kept and built new. |


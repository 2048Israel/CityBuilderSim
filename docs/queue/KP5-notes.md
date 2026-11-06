# KP5 - Screens kept: Bank, Finances and the fund ({next}, 2026-11-05)

**WHERE IT STANDS** (2026-11-05, 22:10): ready, see the ship file. The four slices are in and reviewed (A 9accb707, B 63986728, C cc20d3f2, D 33c0796e, with Opus's mend of the desk's fit); KP4 on main, merged in (ed34013c); `--pick` with the world files exit 3, the only red main's recorded MapCheck (§5). Its gate wants a stranger's review of the threading and the money screens (§4).

The batch's record. The study's KP5 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
rules, seven since KP2) and §6 (what checks it). Lane 2 (screens), after KP4, built on `origin/batch/KP4` at d9d28326
once its ship file was on origin; KP4's mend after its gate sent it back (`origin/batch/KP4` at e5ae16a3, carrying main
at 0.9.19) merged in at once. Opus wrote the briefs, reviewed each diff and committed; Sonnet 5.5 slices at xhigh wrote
the code, one at a time (the PC carries the other lane's gates). Nothing outside `ui/` and `KeptCheck` changes, and no
view class: the default playtest's trace cannot move, so no ensemble (EN1).

## 1. What it builds

- **The Bank tab kept, whole class** (`BankScreen`, 4,433 lines): the Overview and its six pages, Profit and the
  Balance sheet in both views (Summary, and the statement through `Statement`/`StatementView`, kept since KP4), each
  page a scope keyed by the page and the view, so another page or the switch shares no node with the last and opens
  at its top (a pick of the page on show goes back to its top: `toTop()`). Inside: the action cards at the top of
  every page (the rescue, the preferred offer and the other states, each a scope keyed by its state), the capital
  gauge, the scorecard, the rate ladder (a scope a rung), the waterfall, the sheet's bars, the borrowers (a card a
  business), the branches, the equity in parts, every "details" table and fold, History's charts (kept `TimeChart`s
  told their data, following the rates' window).
- **The Finances tab kept, whole class** (`FinancesScreen`, 6,622 lines): the hub and its six areas - the position's
  four pages, the book (its desk, a line's page, Close it now), Borrow at home and abroad (the kinds, the terms, the
  ask typed, the auction), Money, the bond market and a bond's book, the city's fund - each page a scope keyed by its
  area, its page and what is open on it (the line's piece, the piece being closed, the fund's security); and the
  class's two other screens, the foreign default and the debt result, through `keepMenu` under their own names.
- **The city's fund kept** (`FundScreen`, 2,453 lines): Portfolio, Search, Activity, Rules & cash, a security's page
  with the ticket, and the withdrawal; its two charts kept.
- **The pictures told their figures**: `CurveChart`, `DeskCurve`, `DeskBars`, `PathChart`, `AuctionChart` (on
  `Drawn`, which now draws again only where a figure moved: `moved(...)`, `drawAgain()`), `BuybackLadder`,
  `Pieces.Columns` (`columns()`) and `Pieces.RankBar`, each made through `Kept.of` and told the month's figures by a
  `set(...)`; their own parts keep their `new` and their own tooltips. `Pieces.SegmentBar.stand()`: a growth stopped
  and the bar at its figures.
- **The helpers they reach**: `Pieces`' `rateLadder`, `balanceBars`, `statusBanner`, `bandBar`, `setting`.
- **The count-up held to 1x-10x** (★A): Bank's figures pop and its capital gauge grows, the fund's worth counts on and
  a payment pops, only on a month's first draw and when `UserInterface.countsUp()`; otherwise a running one is stopped
  and the node left at rest (`BankScreen.restPip`, `SegmentBar.stand()`, `SectorScreen.stopCounting`).
- **The fields by KP2's rule**: Close it now's two boxes, the ask's box, the yield boxes of the rollover and Borrow
  (`CapChips`) and the fund's search box keep their node, text, caret and focus across a month; the refocus each did by
  hand runs only on a box made new; a chip pressed while a box has the focus sets its text, caret at the end.
- **`KeptBench` and `KeptCheck`**: the three on the bench beside the other kept screens, and KeptCheck's sections for
  them (§5).

## 2. The slices

| slice | what | commit |
|---|---|---|
| A | the Bank tab whole; `rateLadder`, `balanceBars`, `statusBanner`, `bandBar`, `RankBar`, `SegmentBar.stand()`; the Bank hands | 9accb707 |
| B | Finances' frame and wiring and its first half, through Close it now; `columns()`, `setting()`; its pictures; the Finances hands | 63986728 |
| C | Finances from Borrow on, its two other screens, `FundScreen` whole; `AuctionChart`; their hands | cc20d3f2 |
| D | the harness, and the mends it found; Opus: the desk's table at its own height (§6) | 33c0796e |

Each slice checked itself with a scratch smoke harness outside the repository: every page of its part drawn kept over
four months against the same page built new (A 175 comparisons, B its half, C 1,116 across the whole tab with every
choice that reshapes it; 0 differing at the end of each), which slice D's sections now hold in `KeptCheck`.

## 3. The ★ calls, taken the recommended way

- **★A. The money screens' month animations at 1x-10x only**, as People, History and Sectors (KP1 ★A, KP4): the
  reason Jerus's answer gave for People's LIVING HERE - at 50x a month lands every 100 ms and an animation started on
  each never ends - holds for Bank's pops and gauge and the fund's count and pop. *Recommended and taken.*
- **★B. The foreign default and debt result pages kept too**: §7 says whole screen classes (KP2 ★C, KP3 ★C).
  *Recommended and taken.*
- **★C. The fund's Search and Activity pages are too long to be cheap, kept or not.** They grow with the city: in
  KeptCheck's city at month 2,100 Search draws 13,000 nodes (every bond the businesses have sold is a row) and
  Activity every month of the fund's events. Kept, a month costs Search 58-71 ms (built new 106-173) and Activity
  124-192 ms (built new 195-253): a third to a half less, still past the 100 ms a month lands in at 50x, and in a
  younger city (month 605) a quarter of that. No keeping makes 13,000 nodes cheap; showing fewer does. *Recommended:*
  a batch of its own, FP1 (queued in the ship file): Search shows its first rows with a "Show more" (the Sectors list's
  way) and Activity its last year with the rest behind a fold, the figures unchanged. It changes what the page shows,
  so it is Jerus's to overturn.

## 4. The threading, for the gate's review

**One thread**, as KP1-KP4: the clock is an `AnimationTimer`; the month, every redraw, every handler and listener
below, every animation and every repaint of a picture run on the FX thread. **Nothing in this batch runs off it.**
`Kept.drawing` is a static only that thread touches. What a reviewer weighs is what runs inside a draw, what runs a
pulse later (`Platform.runLater`, on the FX thread) and what a kept node carries from one draw to the next.

- **The pools and the arrivals.** `BankScreen.pool` serves the Bank tab; `FinancesScreen.pool` the Finances tab, the
  fund drawn inside it, and the two other menus (their own screen names, so moving to one is keepMenu's arrival);
  `anotherCity()` forgets both. A move between pages of a tab is a kept redraw under the same screen name; what a move
  changes is the page's scope key (§1), so the old page's nodes - its scroller among them - are let go and the new
  page's are made new, at its top. The bookkeeping the show methods did before their draw (the page known, the doors'
  targets cleared, the month's first draw told, NEEDS YOU measured) is in `drawPage()` now, so the bench's draw and
  the real one are the same code.
- **The frame over the scroller.** The scroller's height (bound to the frame's) and the page's width (bound to the
  scroller's viewport) are bound once per scroller, a marker in its properties, as KP4's.
- **What runs a pulse later**: the scroll to a door's target (`runLater`, as before, reading the targets the draw just
  noted); a box left (its focus listener, added once, reads the box and the screen's fields when it fires, then
  `commitAsk` or `ui.redrawSoon()`); the refocus of a box made new (never of one that has the focus, never moving a
  caret). None captures a figure from a draw.
- **Added once** (`Kept.isNew`, or a marker): the boxes' focus listeners; the books' table listeners (`fitAcross`,
  which reads the table and its scroller from each other's properties, and since this batch the height the table wants
  at its width, not the height it was laid out at - a kept desk otherwise grew 28 px a month); the line page's width
  listener; a kept chart's `follow`. Every click is a `setOnMouseClicked` set each draw (the hand-back clears it);
  every tooltip on a node the draw makes is `Kept.tip`; hover looks are chosen from `isHover()` where the draw sets
  them; the hand cursor is added to a style once (`FundScreen.handStyle`), never built on `getStyle()`.
- **The pictures.** A `Drawn` draws again inside the page's draw when `set(...)` says a figure moved, or in a layout
  pass when its width moved; either way its parts are its own, made with `new` (no Kept draw is asked for them).
  `Columns` reads its columns' clicks from the latest `set()` when they fire.
- **Animations.** Every pop, count, growth and the fund's payment pop go through `Kept.play`, which stops the one the
  node was last given; the side that does not animate stops it too (★A).
- **The model** is read through the same getters and written only where the player's controls wrote it before (the
  ask, Close it now, the ticket, the settings' chips).

## 5. What checks it

`KeptCheck` sections 55-68, after KP4's 47-54, on its city as it stands when it reaches them (month 2,100; 439
assertions, 1,087 in all, all pass; 238 s alone on the shared PC, 105 s before). Each of 55-63 first draws its own
page; their fixtures - paper booked through the Borrow page's own calls, the fund's holdings bought as the ticket's
button buys them, the bank's book steered for the gauge (`Bank.setWeightedBook`) - touch no earlier section.

- **55-57 the Bank tab** (6, 20, 61): the Overview and a page kept (top nodes stay, nine in ten handed back, most months
  make none); signatures equal to a page built new for `MONTHS` months, the same pixels at three, the spoil proof - the
  Overview, Profit in both views, the Balance sheet as a statement; every page in both views, a line and a fold opened
  and closed, the waterfall's year on and off, and a chip picked on a page scrolled down lands at its top (`toTop()`).
- **58-60 the Finances tab** (6, 10, 113): the hub and the position kept and shown; every area and page, a line's page
  and Close it now for a piece on the books, the folds, Borrow's every kind, term, money abroad and callable, a bond's
  book, and the foreign default and debt result menus, each an arrival and then a month later - each redraw, and the
  one after a month lands, equal to a page built new.
- **61-63 the fund** (3, 5, 60): Portfolio kept and shown (no month makes more than a tenth of its nodes: the fund's
  pages show the month's events, a bond repaid or a payment, so "most months none" is not their rule); its four pages,
  nine securities with the ticket's choices, the search's words, Activity's months.
- **64 the pictures** (64): `CurveChart`, `DeskCurve`, `DeskBars`, `PathChart`, `BuybackLadder`, `AuctionChart`,
  `Columns`, `RankBar` and the kept charts stay the nodes they were, draw again only where the month's figures moved,
  draw nothing on a second redraw in the same month (a `TimeChart` paints twice a draw, its data and its size: at most
  two allowed), and equal a page built new.
- **65 the count-up only at 1x-10x** (6): at each rung of `UserInterface.SPEEDS` and paused, Bank's pops and gauge and
  the fund's count and pop run exactly where `countsUp()` allows; past it nothing animates and a running one is stopped.
- **66 the boxes** (66): Close it now's two, the ask's, Borrow's and the rollover's yield boxes and the fund's search,
  focused and typed into, keep node, text, caret and focus across a month through the real show method; a chip pressed
  under the focus sets the text, caret at the end; the page then equal to one built new.
- **67 the hover** (12): a Bank door card, a rung's name, a Finances card and a fund holding row keep their look under a
  still pointer across a kept redraw, and lose it with the pointer.
- **68 cost** (7): below.

| dial | value | page | kept, five runs (median) | built new |
|---|---:|---|---|---|
| `KEPT_BANK_MS` | 12 | the Overview | 5.2-8.3 ms (5.6) | 15.9-19.2 ms |
| `KEPT_FINANCES_MS` | 18 | Borrow at home; The book | 7.6-10.2 (8.6); 7.2-9.0 (8.6) | 16.7-23.8; 16.0-21.9 |
| `KEPT_FUND_MS` | 321 | Activity; Search | 124-192 (160); 58-71 (62) | 195-253; 106-173 |

Each twice the median, rounded up (KP1's way), measured in five runs of the money sections alone at month 2,100; the
gate judges them hard. The fund's is ★C. Broken by hand to see them caught, one at a time and put back: a `Kept.tip`
turned back into `Tooltip.install` on the gauge's bar (56, 57), `restPip` and `stopCounting` no-ops (65),
`Columns.set` building on every call (64), the ask box's and `CapChips`' press branch removed (66), the door card's
`isHover()` ignored (67).

**For the gate's review, the harness's machinery** (both pages settled alike, as KP4's change was): `KeptBench.settle()`
stops at two quiet passes, not one (a scroller under a frame takes its height a pass after the frame's children stop
moving), and lays out again a kept scroller whose page moved by more than 400 px (its bar comes or goes a layout late);
the signature leaves out the place and size inside a node that is hidden and unmanaged (nobody lays it out or sees it;
its words, style, handlers and values are still compared); `restoreKeptPage()` lets go of the scene's dirty list with a
one-pixel snapshot (every page built new stayed held, 5 MB each, and the Bank's reshaping alone spent the harness's 2 GB).

Also green: BankCheck and FundCheck on slice C's and D's code. On the merge with main after KP4 landed (ed34013c), with
the world files: `--pick` exit 3 (8 harnesses; MapCheck main's recorded red; five timing lines SOFT beside the other
lane: MapCheck 3, and KeptCheck's whole chrome 10.1 ms of 8 and the construction page 4.7 of 4 - KP5's own three dials
OK). No full suite: the diff is `ui/` and `KeptCheck`, read by no harness but KeptCheck and TimingCheck, both run.

## 6. Found in review

- **By the slices:** an emptied label keeps the height of the words it had (Bank's Balance sheet, a year ago's move:
  23 px kept against 15 new): words and none are scopes of their own, as KP4's `billRow`. A kept desk table grew 28 px
  a month (`fitAcross` fed back the laid-out height). A line page's key swatch took the wrap and width of the note that
  sat in its slot. A bond's book handed back 69% of its nodes with its levels keyed by price; keyed by place, all.
  Borrow Abroad's money chips had both a `Tooltip.install` and a `setTooltip`; a conditional "last issue" line on the
  bond market and the fund's Search and Activity rows sat in unscoped slots.
- **By Opus:** every slice's diff read for `new` left in a draw, `Tooltip.install`, `setTooltip`, `getStyle() +`,
  `addListener` and `runLater`: none but records, `Insets`, `ColumnConstraints`, path elements, `TimeChart.Line` and a
  picture's own parts; every added listener added once; the ask box's text set under the focus only where a press
  moved the ask (what is typed always reaches `askTyped`, so a focused box that differs from it is one a press changed).
  Slice D's mend of the desk's 44 px (the scroller refit inside the draw) read the table's rows as they stood before
  the draw - a kept box's children are set when the draw ends - so a row that left was still measured; two probes in a
  scratch city (48 months each, three one-month notes booked) could not make a row leave without a successor (notes
  roll over), so the case was reasoned, not seen. Mended at the root instead: the table stands at its own height in its
  scroller (`setFitToHeight(false)`), so its height moves when its rows do and its listener refits the scroller after
  the pulse has styled and laid them out, as on a page built new; the scroller is the table's height and two pixels
  either way, so the card looks as it did.
- **By the harness (slice D's mends):** `StatementView`'s optional columns (then, change, share) are scopes: a cell
  that wrapped passed its height to the cell shifted onto it (the Balance sheet with the change column off).
  `Columns`' "heaviest" tag is the picture's own part, not a `Kept.label` (it took a neighbour's wrap). `SegmentBar`'s
  empty or absent segment label keeps no stale size. `RankBar` compares its figures with `Double.compare`, so a NaN is
  no move. `CapChips`: a chip pressed while the yield box has the focus sets its text, caret at the end.

## 7. Left

- **★C, FP1**: the fund's Search and Activity, too long for any redraw (§3).
- **Not reached by a fixture** (KP5-N): the bank's rescue, the preferred offer with a shortfall, a city with no bank,
  under its minimum, the equity split and the residual (their builders drawn kept against new for the states the city
  reaches, each a scope keyed by its state); foreign money on Funding (`Bank.setForeignDeposits`) drawn both ways apart
  by one hidden empty label (13 px against 0) in a deep city, not traced; the linked and callable bonds' line pages (the
  scratch city refused to sell them). Each is drawn by the same code as the kept pages around it.
- **A `TimeChart` paints on every `setData` and `setSize`**, twice a kept draw whether or not its data moved (KP1's chart;
  64 allows two). A chart told the same data could skip its paint; for KP7, which retires the rebuild path.

# KeptCheck.java - 9,856 lines · 248 methods · 33 constants · harnesses

`ham/citybuildersim/KeptCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each
> month's redraw run over last month's nodes ends exactly where a page built new would, and costs
> a fraction of it.
> 
> WHY THIS EXISTS. A kept page (ui.Kept) runs the screen's own drawing code every month over the
> nodes it made the month before. What can go wrong is what a node carries from one month into the
> next: a value set under a condition that no longer holds, a list added to, a listener added
> twice, a picture told only half its new figures. None of it shows on the month it is written;
> all of it shows as a page that is not the page the city would draw. So this draws the page both
> ways at every month of a played stretch and compares them - every node's words, style, the
> values code set, handlers, tooltip and place, and at some months every pixel. What it costs is
> KeptTimingCheck's since FS4, so that this harness is correctness alone (its sections 5, 10, 13, 20,
> 24, 28, 32, 39, 43, 52, 68, 81 and 90 are there, under their numbers, on this harness's city).
> 
> It needs the JavaFX toolkit but no window: the page is laid out on a scene of its own
> (ui.KeptBench). Where the toolkit cannot start (the cloud has no display) it says so in one line
> and exits 0.
> 
> What it has to prove (FS5: the detailed premises live beside their sections and in their assertion labels):
> 
> The kept law is common to every page: the same top nodes, no allocation on an unchanged draw,
> HANDED_BACK of the nodes drawn again, and equality to a page built new. The signature includes
> words, styles, values, handlers, tooltips and layout. Pixels use SHADE_LEVELS and SHADED_SHARE.
> A deliberately spoiled figure must differ, and the following kept redraw must repair it.
> Choices exercise folds, views, controls and model actions; opened folds must add to the page.
> Controls keep the same node and the player's typed or dragged value until release commits it.
> Hover looks persist under a still pointer and clear when it leaves; tooltips keep their identity.
> KBP1 blocks and KRT1 layout differences are traced with the model and clock held still:
> recurring faults stay hard, and permitted nonrepeating readings are printed and counted.
> 
> Sections (each method gives its fixture and each assertion gives the complete law):
>   1-4. People: retention, signatures, pictures and reshaping.
>   6-9. Chrome: header and rail retention, signatures, pictures and changing screens.
>  11-12. The header and the three panels kept and equal to freshly built panels.
>  14-19. City History: kept pages, signatures, pictures, charts, controls and changing views.
>  21-23. Infrastructure: kept pages, equality and reshaping.
>  25-27. Services: kept pages, equality and reshaping.
>  29-31. Land: kept pages, equality and reshaping.
>  33-34. Land's shortfall pages and controls keep the player's hand.
>  35-38. Build: kept pages, equality, pictures and changing choices on caused building states.
>  40-42. Construction: kept pages, equality and reshaping, with works deliberately on site.
>  44-45. Build's refused orders, funding pages and held controls.
>  46-49. Sectors: kept pages, signatures, pictures and reshaping of the businesses and statements.
>  50-51. Industry pictures retain identity, repaint only changed figures, and count up by speed.
>  53-54. Sectors' controls and hover; the clock redraws the screen shown.
>  55-57. Bank: kept pages, signatures, pictures, statements, folds and views; KBP1 traced held still.
>  58-60. Finances: kept pages, equality and choices; the fixture books every kind of paper.
>  61-63. Fund: kept pages, equality, portfolios, lots, tickets, search and activity.
>         A new lot settles through the book; its page and both reviews are watched past settlement.
>  64-67. Money pictures, count-up, held boxes, hover and tooltips.
>  69-71. Government: kept pages, equality, folds, lines and policy doors.
>  72-74. Trade: kept pages, equality, nations, folds, controls and the currency warning's bound.
>  75-77. Policy: kept pages, equality and all choices, with a zone caused when needed.
>  78-80. Policy's held dials, Trade's count-up, and the three screens' hover and tooltip identity.
>  82-84. Household money: kept pages, pictures, signatures, cells, folds and views.
>  85-87. Rankings: kept pages, pictures, signatures and every choice; the read awaits its worker.
>  88-89. The clock never rebuilds a rail screen; Household money and Rankings keep hover and tooltips.
>  91. Door cards set their hand style once per draw, whole.
>  92-93. Unchanged pages set no property twice; real show methods retain their page and pool.
>  94-96. Research equipment and grants: kept pages, signatures, pictures, folds and choices.
> ... (10 more lines in the source)

**Uses:** [KeptBench](KeptBench.md) (703), [Game](Game.md) (160), [ResearchView](ResearchView.md) (122), [Money](Money.md) (62), [Sectors](Sectors.md) (51), [FundView](FundView.md) (36), [Debt](Debt.md) (29), [DecisionLog](DecisionLog.md) (22), [ResearchViewCheck](ResearchViewCheck.md) (20), [FundLedger](FundLedger.md) (20), [Where](Where.md) (19), [BuildingsTemplate](BuildingsTemplate.md) (18), [BuildAdvice](BuildAdvice.md) (17), [ForeignAccounts](ForeignAccounts.md) (17), [Notice](Notice.md) (16), [ResearchCatalog](ResearchCatalog.md) (15), [CityCalendar](CityCalendar.md) (13), [TreasuryFund](TreasuryFund.md) (13), [ChartModel](ChartModel.md) (10), [LandmassCheck](LandmassCheck.md) (10), [CorporateBond](CorporateBond.md) (10), [Auction](Auction.md) (9), [Equity](Equity.md) (9), [BondMarket](BondMarket.md) (8), [Research](Research.md) (7), [DebtManager](DebtManager.md) (7), [LandmassView](LandmassView.md) (6), [TaxPolicy](TaxPolicy.md) (6), [WellCheck](WellCheck.md) (6), [OilCheck](OilCheck.md) (6)... and 52 more

**Used by (8):** [ChartFitCheck](ChartFitCheck.md), [KeptTimingCheck](KeptTimingCheck.md), [MapCheck](MapCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [NewUnitCheck](NewUnitCheck.md), [ResearchViewCheck](ResearchViewCheck.md), [StepsCheck](StepsCheck.md), [ToastCheck](ToastCheck.md)

## Sections

| line | section |
|---:|---|
| 1178 | INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page |
| 1757 | FB12: the landmass table reached and kept (KP2b-N) |
| 2131 | BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, |
| 2790 | SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and |
| 2872 | THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97 |
| 3753 | THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... |
| 3971 | · Finances |
| 4263 | · the city's fund |
| 5006 | · the pictures (64) |
| 5264 | · the count-up (65) |
| 5490 | · the boxes (66) |
| 5651 | · the hover (67) and the cost (68) |
| 5711 | GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept ... |
| 5985 | · Trade |
| 6299 | · Policy |
| 6550 | · the controls (78) |
| 6880 | · the count-up (79) |
| 7087 | · the hover (80) and the cost (81) |
| 7228 | POLICY > RESEARCH'S OVERVIEW AND FUNDING (R3b, slice B), sections 99 to 101: the two pages, the hub's Research card, ... |
| 7786 | THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of ... |
| 8092 | HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page draw... |
| 8401 | · a slider greyed while held (91) |
| 8515 | THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node... |
| 8878 | THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7): sections 94 and 98 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 116 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 119 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 202 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's cached outline a le... |
| 205 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 248 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 759 | `KeptCheck.TICKED` | `"unemployment"` | The first of these lines picked and not in the preset the page opens on: a line to tick and untick. |
| 1184 | `KeptCheck.HANDED_BACK` | `0.9` | The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). |
| 1230 | `KeptCheck.HELD_STILL` | `2` | How many times a difference is looked at again with the model and the clock held still before it is a kept fault (KRT1). |
| 1436 | `KeptCheck.BLOCKS_TRACED` | `3` | The months a block a redraw made new is traced in (KBP1), at the most each section: what it made and what it dropped, in words. |
| 1724 | `KeptCheck.SERVICES_FOLDS` | `{ { "Health/Overview", "health:sick" }, { "Health/General care", "health:gene...` | The folds each sub-page of Services has, by "system/page" (a page not named has none). |
| 2137 | `KeptCheck.SITE_ROWS` | `6` | The rows the Sites tab and the timeline are kept at while Build and the construction page are watched: a site each. |
| 2140 | `KeptCheck.SITE_ORDER` | `{ "Gravel Road", "Walk-in Clinic", "Paved Road", "Bus Network", "Elementary S...` | What is ordered, in turn, to put a site on: the cheap, quick works first. |
| 2412 | `KeptCheck.FUNDING_SHORT_BY` | `4` | How many times over the funding page's run is short of what the market lends: each offer's bids reach at most this part of the gap (KBF1). |
| 2503 | `KeptCheck.RANKINGS_WORKER` | `"rankings-saved-cities"` | The name of the Rankings tab's one thread for the saved cities (RankingsScreen's WORKER). |
| 2798 | `KeptCheck.PICTURE_MONTHS` | `6` | The months the pictures are watched through (section 50): each month a repaint or not, and a page built new beside it. |
| 2804 | `KeptCheck.NOT_PLAIN` | `java.util.Set.of(Sectors.RETAIL, Sectors.RESTAURANTS, Sectors.LUXURY_RETAIL, ...` | The keys a plain business is not taken from: the shops, the kitchens, the counters, the landlords, the haulier and the fuel's two. |
| 2877 | `KeptCheck.R5_MONTHS` | `6` | The months each of the research sections plays on its page, kept over last month's nodes against a page built new. |
| 3449 | `KeptCheck.SWING_STEPS` | `3` | How many of its printed steps the swing moves the business's net income by each month (KS2): past a rounding with room to spare. |
| 3773 | `KeptCheck.BANK_FOLDS` | `{ { "Profit", "profit:statement", "Interest earned", "Fees", "Provisions for ...` | What the Bank's folds hold, by page: the fold's key, then the lines a player opens inside it (each a key the page's own bookLine opens by). |
| 3974 | `KeptCheck.PAPER` | `{ { "Note", "12", "" }, { "Float", "5", "" }, { "Serial", "10", "" }, { "Term...` | The paper the money sections sell, one of each kind the Borrow page offers (and a callable bond too): {kind, term, "callable" or ""}. |
| 3982 | `KeptCheck.SELL_ORDER` | `{ 4, 0, 1, 2, 3, 5 }` | The order sellPaper sells PAPER in: the 20-year bond first, the piece that falls due last, which sections 64, 66 and 67 draw for dozens of months, then the rest as PAPER has them. |
| 4296 | `KeptCheck.SETTLED_WATCH` | `3` | The months a lot the let-go month settled is watched through in 63: its page and its two reviews, a month landing after each. |
| 4300 | `KeptCheck.FUND_PAY_IN_ISSUES` | `200` | What 61 pays into a fund that holds nothing, in the city's smallest bond issues: a fund with something to buy whatever the treasury holds (FS5). |
| 4303 | `KeptCheck.FUND_HELD_DUES` | `3` | How many months of the fund's transfer its cash is kept over while 63's choices are watched: the transfer is paid from cash, and nothing is sold for it (KF63). |
| 5024 | `KeptCheck.MOST_TO_PAY` | `{ "98.5", "101.5" }` | The most to pay typed on Close it now in the odd months of the watch, by turns (Spot's month hand). |
| 5034 | `KeptCheck.PAPER_LASTS` | `60` | The months a piece of paper 66's boxes and 67's book row are drawn on must still run: 66 lands a dozen months, 67 fewer. |
| 5150 | `KeptCheck.DESK_KINDS` | `java.util.Set.of("CurveChart", "DeskCurve", "DeskBars", "PathChart", "Buyback...` | The kinds of picture the bond desk draws, from the curve and the book (WG1-R). |
| 5728 | `KeptCheck.TRADE_FOLDS_THAT_ADD` | `java.util.Set.of("month:ledger", "reserves:eight")` | The folds the Trade sections open that have to add to their page in any city that trades: the month's ledger and the eight moneys over the years. |
| 8005 | `KeptCheck.CLOCK_MONTHS` | `3` | The months landed, each followed by the clock's redraw, on every screen in section 88. |
| 8771 | `KeptCheck.RANKINGS_READ_GUARD_MS` | `600_000` | How long a read of the saved cities is waited on before it is called hung (KRW1, FS5, the study's FS1-C): ten minutes, ten times the minute it had to land in. |
| 8883 | `KeptCheck.SITE_COUNTS` | `{ 0, 1, 3 }` | The sites a state puts the city in: none, one (the tab says "1 site"), three (it says "3 sites"). |
| 9485 | `KeptCheck.KEPT_HEAP_SHARE` | `.75` | A full KeptCheck's objects leave at least a quarter of its suite heap for the next pages (KP2c): the live heap after release, less the render targets the software renderer keeps on it (FS5, GB5's star 5: the box's Mon... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 98 | `static int fails` |  |
| 131 | `static Runnable afterMonth` | What is done after every month played, with the log off, while a fixture has to be held through the months (KP3: the city's sites); null for nothing. |
| 1195 | `long kept, again` |  |
| 1196 | `int redraws, quiet, madeMost` |  |
| 1197 | `private long last` |  |
| 1233 | `static int heldReadings` | The differences that did not repeat held still, with their content the same: a reading, printed, not failed (KRT1). |
| 1237 | `final String directory` |  |
| 1238 | `String phase` |  |
| 1239 | `int month, look` |  |
| 1240 | `long population` |  |
| 1241 | `boolean disabled` |  |
| 1244 | `private static HealthcareCapture healthcareCapture` | FX-only scope; never holds signatures, Game or Nodes. |
| 1443 | `static int blocks, blocksRepeated, blockReadings, sectionBlocks` | The redraws that made a block (more than a tenth of what they drew again), how many of them made a node again drawn unchanged, held still (KBP1), and how many a choice's month made that did not: the month's content, r... |
| 2143 | `private static int siteTurn` |  |
| 4306 | `static int fundShortMonths` | The months, since fundHeld() began, that the fund's cash stood under FUND_HELD_DUES months' transfer after its top-up (WG1-R: none may). |
| 5718 | `static int checks` | The assertions made so far, for the count a stretch of the KP6 sections reports. |
| 6612 | `String none, reading0, dragged, kept, differs, dropped, firstHeard` |  |
| 6613 | `boolean sameReading, sameBar, heldPressed, heldStill, heldAfter, same, tray, trayGone, stagedMid, saysDragg...` |  |
| 6614 | `int heardText, heardStyle, control, arrowsBefore, arrowsDragged, arrowsMid` |  |
| 6615 | `double shown, valueNow, thumb, cityAt, cityMid, cityAfter, cityBack, step, min` |  |
| 6616 | `Double stagedAt` |  |
| 8405 | `String none, differs, dropped, reading` |  |
| 8406 | `boolean heldPressed, heldGreyed, changingGreyed, sameBar, heldAfter` |  |
| 8407 | `double value, want, back, city` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 96 | 9761 | **type** `public class KeptCheck` | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exactly where a page built new would, and costs a fraction of it. |
| 100 | 5 | `static void check(String label, boolean ok)` |  |
| 107 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure (R3b, sections 99 to 101). |
| 121 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 128 | 1 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on): the one start, FollowBench's (GB5). |
| 134 | 10 | `static void month(Game g)` | A month, as the clock runs it, its log kept off this harness's output. |
| 146 | 9 | `static<T> T quietly(Callable<T> call) throws Exception` | A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). |
| 157 | 28 | `static String firstDifference(List<String> kept, List<String> fresh)` | The first line where two signatures part, or null when they are the same. |
| 187 | 6 | `static void png(int[] px, java.nio.file.Path to) throws java.io.IOException` | A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. |
| 208 | 10 | `static boolean alike(int[] a, int[] b)` | Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. |
| 220 | 16 | `static String pixelDifference(int[] a, int[] b)` | Where two snapshots part: their sizes, or how many pixels differ and the box round them. |
| 237 | 3 | `static String clip(String s)` |  |
| 241 | 5 | `static double median(List<Double> v)` |  |
| 252 | 228 | `static void chrome(Game g, KeptBench bench) throws Exception` | Sections 6 to 10 (UP2): real refreshHeader/refreshTabRail, kept and fresh on their own scenes. |
| 482 | 11 | `static void panelStates(KeptBench bench, int at)` | The panels' states, month by month: the city panel's mode, and which of its sections are open. |
| 498 | 232 | `static void panels(Game g, KeptBench bench) throws Exception` | Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice 2b), each on a scene of its own, beside the header and the rail of 6 to 10. |
| 733 | 3 | **type** `interface Hand` | A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). |
| 734 | 1 | `String on(KeptBench bench) throws Exception` _(in KeptCheck.Hand)_ |  |
| 743 | 9 | **type** `record Choice(String name, Hand hand, boolean opens, boolean acts)` | One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2); `acts` for a choice that makes the model act (KP3: automatic building turned on or... |
| 744 | 3 | `Choice(String name, Hand hand, boolean opens)` _(in KeptCheck.Choice)_ |  |
| 748 | 3 | `Choice(String name, Hand hand)` _(in KeptCheck.Choice)_ |  |
| 754 | 3 | `static Choice acting(String name, Hand hand)` | A choice that makes the model act (see Choice.acts). |
| 765 | 221 | `static void history(Game g, KeptBench bench) throws Exception` | Sections 14 to 20: City History kept (KP1). |
| 988 | 7 | `private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved)` | Months landed until the figure has moved (at most twelve): whether it did. |
| 997 | 3 | `private static String nullIfOffered(String refusal, String what)` | The choice's refusal, as the premise it could not meet, or null when the chart is offered. |
| 1002 | 77 | `static void crosshair(Game g, KeptBench bench) throws Exception` | Section 18: ChartModel owns the window; kept charts retain the crosshair and hovered legend chip that fresh detachment clears. |
| 1081 | 96 | `static void countUp(Game g, KeptBench bench) throws Exception` | Section 19: real People/History show methods count up paused or through COUNT_UP_TOP, and set figures immediately above it. |

### INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page (lines 1178-1756)

| line | len | member | says |
|---:|---:|---|---|
| 1194 | 34 | **type** `static final class Redraws` | Kept's rule as a share, the same on any city (FS4): a redraw hands back HANDED_BACK of the nodes it draws again, that is of the smaller of the last draw and this one, summed over the redraws watched. |
| 1200 | 3 | `Redraws(long first)` _(in KeptCheck.Redraws)_ | From a first draw of `first` nodes. |
| 1205 | 9 | `void add(int made, int handed)` _(in KeptCheck.Redraws)_ | One redraw: the nodes it made and the nodes it handed back. |
| 1216 | 3 | `boolean holds(double share)` _(in KeptCheck.Redraws)_ | Whether the redraws handed back `share` of what they drew again, or more. |
| 1220 | 3 | `String counts()` _(in KeptCheck.Redraws)_ |  |
| 1224 | 3 | `String quietLine()` _(in KeptCheck.Redraws)_ |  |
| 1236 | 8 | **type** `private static final class HealthcareCapture` | WHY (FS5): clipped Healthcare reports lost the leaf; observe existing pairs without another UI operation. |
| 1242 | 1 | `HealthcareCapture(String directory)` _(in KeptCheck.HealthcareCapture)_ |  |
| 1246 | 11 | `private static String observedAgainstNewHeld(Game g, KeptBench bench, KeptBench.Page page, boolean pixels, HealthcareCapture ca...` | FX-only scope; never holds signatures, Game or Nodes. |
| 1258 | 5 | `private static Object healthcareField(Object owner, String name) throws ReflectiveOperationException` |  |
| 1264 | 3 | `private static String healthcareDimensions(javafx.scene.Node node)` |  |
| 1269 | 86 | `private static void captureHealthcarePair(KeptBench bench, List<String> kept, List<String> fresh)` | After restore only: local JSON data contains strings/primitives and the two existing string lists, never Nodes. |
| 1357 | 5 | `static List<String> unplaced(List<String> lines)` | A signature line without where layout put it: its class, words, style, values, handlers and tooltip (KRT1). |
| 1368 | 37 | `static String againstNewHeld(KeptBench bench, KeptBench.Page page, boolean pixels) throws Exception` | FS4/KRT1: compare kept and fresh signatures/pixels, retrying layout-only differences HELD_STILL times. |
| 1407 | 3 | `static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. |
| 1417 | 17 | `static String againstNew(KeptBench bench, KeptBench.Page page, boolean pixels) throws Exception` | ...and, for a page with pictures painted on canvases, which no signature holds (KP7, the Rankings table), the canvases' pixels too: the signature's difference first, else the canvases' (no pixel more than SHADE_LEVELS... |
| 1446 | 9 | `static List<String> wordsOf(java.util.Collection<javafx.scene.Node> nodes, int most)` | The words of these nodes (a Labeled's text, a Text's), sorted, the first `most` of them: what a block a month made or dropped said (KBP1). |
| 1460 | 18 | `static boolean blockHeldStill(KeptBench bench, KeptBench.Page page, java.util.Set<javafx.scene.Node> before, int made, long dra...` | FS5/KBP1: redraw a new block unchanged. |
| 1483 | 38 | `static void pageKeptCaused(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | The common kept law: same top nodes, no unchanged allocation, HANDED_BACK of what each month draws again. |
| 1523 | 71 | `static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. |
| 1600 | 3 | `static void pageReshapedCaused(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception` | Each choice and its following month equal a page built new; opened folds add to it. |
| 1605 | 3 | `static void pageReshapedCaused(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, boolean pixels) throw...` | ...and, with `pixels`, each redraw's picture against the page built new's as well (KP7: the Rankings table is painted, and no signature holds it). |
| 1609 | 72 | `private static void pageReshapedCaused(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, boolean pixel...` |  |
| 1683 | 3 | `static Choice choice(String name, Hand hand)` | A choice with no side effect on the model: a hand that changes the screen's state only. |
| 1688 | 9 | `static String ensureTransit(Game g) throws Exception` | Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. |
| 1699 | 23 | `static void infrastructure(Game g, KeptBench bench) throws Exception` | Sections 21 to 24: Infrastructure kept. |
| 1736 | 20 | `static void services(Game g, KeptBench bench) throws Exception` | Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. |

### FB12: the landmass table reached and kept (KP2b-N) (lines 1757-2130)

| line | len | member | says |
|---:|---:|---|---|
| 1760 | 6 | `static List<String> landmassWords(javafx.scene.Node node)` | Labels under a node, in the order the table puts them in its cells. |
| 1768 | 8 | `static List<javafx.scene.Node> landmassNodes(javafx.scene.Node node)` | Every authored node of the grid, omitting a control's toolkit-owned skin. |
| 1778 | 5 | `static javafx.scene.layout.GridPane landmassGrid(KeptBench bench)` | The actual page's landmass grid, identified by the model's own column heads. |
| 1785 | 15 | `static boolean landmassCells(javafx.scene.layout.GridPane grid, LandManager land)` | The table's actual cells against the model's rows; an absent table cannot pass. |
| 1802 | 56 | `static void landmassTable() throws Exception` | An Earth scratch city causes the missing table: owning a second landmass, then more of that same island. |
| 1860 | 48 | `static void land(Game g, KeptBench bench) throws Exception` | Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. |
| 1916 | 9 | `static String buyTheBestOffer(Game g, KeptBench b) throws Exception` | The best offer bought through the bench (KeptBench.landBuy(), a row's Buy), the city first given the least that pays for it where it cannot (FC2): the played city reaches this section at month 1043 in a slump of its o... |
| 1927 | 123 | `static void controls(Game g, KeptBench bench) throws Exception` | Section 34: Transit fare holds node/thumb/reading/ladder through a month, commits on release, follows external changes; History filter retains text/caret/focus. |
| 2052 | 67 | `static void hover(Game g, KeptBench bench) throws Exception` | Section 35: the hover kept. |
| 2121 | 9 | `private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result)` | One hover result: printed, counted as found when the page had the node, and a check that it held. |

### BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, (lines 2131-2789)

| line | len | member | says |
|---:|---:|---|---|
| 2146 | 12 | `static boolean buyGround(Game g, BuildingsTemplate t, int n) throws Exception` | Ground bought through the model for an order that found none: the cheapest offer that covers the shortfall, the cash it lacks borrowed on Build's bond; whether it was bought. |
| 2160 | 30 | `static String ensureSites(Game g, int rows) throws Exception` | Orders through the model until the city has `rows` sites on (a row each on the Sites tab, a bar each on the timeline): what it did, in a line. |
| 2192 | 9 | `static void keepSites(Game g)` | From now every month played tops the sites up to SITE_ROWS (the fixture holds while the pages are watched), until stopSites(). |
| 2202 | 3 | `static void stopSites()` |  |
| 2207 | 135 | `static void build(Game g, KeptBench bench) throws Exception` | Build's pages: the Overview and a city category with two orders dialled up (so that its order bar shows), kept, as built new, reshaped and costed. |
| 2344 | 66 | `static void construction(Game g, KeptBench bench) throws Exception` | The construction page, with the city's sites on: the Sites tab kept, shown, reshaped by its tabs, a pick and the find box, and costed. |
| 2415 | 77 | `static void stopped(Game g, KeptBench bench) throws Exception` | Section 44: caused buildRunStop, funding and shortfall/cap states (FUNDING_SHORT_BY); arrival and next month equal fresh. |
| 2494 | 7 | `static String bytesOf(java.nio.file.Path file)` | A file's size as the trace prints it: its bytes, or "missing". |
| 2509 | 21 | `static String workerThread(String name)` | A thread by its name, as the trace needs it: its state, its priority, the CPU it has had, the machine's CPU load, the top of its stack and its first frames in the game's own code (which file it reads); or that there i... |
| 2532 | 3 | `static double cpuLoad()` | The machine's CPU load since the last call, 0 to 1, or -1 when the platform will not say (its first call answers 0: call it once to start). |
| 2537 | 6 | `static boolean pageSays(KeptBench bench, String words)` | Whether a label on the page as drawn says these words (its whole text, or the start of it). |
| 2545 | 208 | `static void buildControls(Game g, KeptBench bench) throws Exception` | Section 45: auto-build slider holds/commits; cap-yield and construction-find boxes retain node/text/caret/focus; a stepper refills the order bar in place. |
| 2755 | 34 | `static void buildHover(Game g, KeptBench bench) throws Exception` | Section 46: the hover. |

### SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and (lines 2790-2871)

| line | len | member | says |
|---:|---:|---|---|
| 2801 | 1 | **type** `record SectorKinds(String plain, String materials)` | The businesses the Sectors sections take besides the shops, the landlords and Freight: a plain one with a plant standing, and a materials industry with works to draw. |
| 2808 | 11 | `static SectorKinds sectorKinds(Game g)` | The plain business this city has, by the model's own reading of its plants, and the materials industry its works draw: wood products, which ensureWorks() puts up a sawmill for (null when it has none standing). |
| 2825 | 18 | `static String ensureWorks(Game g) throws Exception` | A drawable materials month, with a sawmill supplied through BuildingManager when none stands. |
| 2845 | 8 | `static void arrive(KeptBench bench, KeptBench.Page page) throws Exception` | A page arrived at: back on its home, drawn and laid out, as the bench's other pages are before their sections. |
| 2855 | 6 | `static Hand on(String key, String page, boolean statement)` | The hand that opens a business's page in a view. |
| 2863 | 3 | `static String called(Game g, String key)` | What a business is called on the screen. |
| 2868 | 3 | `static String possessive(String name)` | A name's possessive as the lines say it: "Retail's", "Wood Products'". |

### THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97 (lines 2872-3752)

| line | len | member | says |
|---:|---:|---|---|
| 2880 | 5 | `static int occurrences(String whole, String part)` | How many times `part` is in `whole`. |
| 2887 | 10 | `static void researchScreens(Game g, KeptBench played) throws Exception` | Sections 96/97: Investors equipment and Build NEW in ResearchViewCheck.researchTown() (1900) and the played city without either. |
| 2899 | 124 | `static void researchEquipment(Game g, KeptBench played) throws Exception` | Section 96: equipment cards match model rows/verdicts; binder stays while tractor is unknown, then found card follows search. |
| 3025 | 129 | `static void researchBuild(Game g, KeptBench played) throws Exception` | Section 97: Electric Arc Furnace NEW chip/edge/origin/strip/dots follow seen-month, not model age (find restored to month9); lab purpose and played-city absence. |
| 3156 | 104 | `static void sectors(Game g, KeptBench bench) throws Exception` | Sections 47 to 54: the Sectors tab kept. |
| 3262 | 31 | `static void sectorsPictures(Game g, KeptBench bench, String works, String worksName) throws Exception` | Section 50: IndustryPane, LegBar and RefineryPicture retain identity/nine-in-ten shapes, repaint only changed views and equal fresh without same-month repaint. |
| 3295 | 74 | `static void picturesKept(Game g, KeptBench bench, KeptBench.Page page, String what, java.util.function.Function<KeptBench, List...` | Picture months: find may remove nodes; paints marks view-driven pictures, not retained bar parts. |
| 3371 | 10 | `static String againstNewAsIs(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands with no animation finished first, against the same page built new: the first difference, or null. |
| 3388 | 6 | `private static boolean figuresMoved(String[][] words, boolean business)` | Whether the figures a Sectors page counts have moved between last month and this: on the list its KEPT and some card's, on a business's page the business's KEPT, the one figure there that counts (countingFigures). |
| 3403 | 28 | `static Runnable businessMoving(Game g, KeptBench bench, String key) throws Exception` | KS2: the plain business's month made to move while 51 is watched, and what puts it back. |
| 3437 | 10 | `static double swungOffset(Game g, KeptBench bench, String key, double offset)` | The property offset that moves a business's net income by SWING_STEPS of the steps it is printed in this month, from the offset it has (KS2): up where the tax allows it and down otherwise. |
| 3452 | 15 | `static double printedStep(String words)` | The step a figure is printed in, in the model's thousands: its unit's tenth ("$1.2B" moves in tenths of a billion), a thousand on "$418k", a dollar below. |
| 3469 | 6 | `private static List<javafx.scene.control.Label> countingFigures(KeptBench bench, String key)` | The figures on a Sectors page that count up: the KEPT, and on the list every card's net income. |
| 3481 | 65 | `static String countRung(Game g, KeptBench bench, String key, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 51 on one page (`key` null for the list): the page is shown, a month lands and it is shown again; a count starts on each figure that moved exactly where `expect` says, the waterfall grows an... |
| 3548 | 69 | `static void sectorsCountUp(Game g, KeptBench bench, String plain) throws Exception` | Section 51: Sectors counts/waterfall/ring at each clock rung and paused obey COUNT_UP_TOP, stop past it and read current words; businessMoving (KS2) causes movement. |
| 3619 | 19 | `static Game seaTown() throws Exception` | The oil reserve's city: OilViewCheck's sea town (a platform with its wells, two land wells, a Strategic Reserve with FILL ordered), its fixture's report lines kept off this output. |
| 3640 | 24 | `static void sectorsControls() throws Exception` | Section 53: real sea-town oil FILL/RELEASE dials retain held node/thumb/reading/ladder, commit on release, follow external changes and equal fresh. |
| 3666 | 68 | `static void dial(Game sea, KeptBench bench, KeptBench.Page page, int which, String name, String key, java.util.function.DoubleF...` | One of the reserve's dials (the `which`th slider on the page), pressed, held across a month, released, and set outside a draw. |
| 3736 | 16 | `static void sectorsHover(Game g, KeptBench bench, String plain) throws Exception` | Section 54: the hover. |

### THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... (lines 3753-3970)

| line | len | member | says |
|---:|---:|---|---|
| 3760 | 3 | `private static boolean twoViews(String page)` | The Bank tab's pages that have two views, a Summary and a Statement. |
| 3765 | 6 | `static Hand bankOn(String page, boolean statement)` | The hand that opens a Bank page, in a view (the Overview when `page` is null). |
| 3780 | 96 | `static void bank(Game g, KeptBench bench) throws Exception` | Sections 55 to 57: the Bank tab kept. |
| 3881 | 14 | `static Hand changing(KeptBench.Page page, Hand hand)` | A hand that has to change the page (the premise of a line opened that adds no node: a statement line's detail is built hidden and shown): the page drawn before and after, and the choice refused as made if the two are ... |
| 3897 | 73 | `static void bankPicks(Game g, KeptBench bench) throws Exception` | Section 57: Bank Overview/open Profit have asserted scrolling room; picking same scope resets scroll to top, while another page/view opens its own scroller there. |

### Finances (lines 3971-4262)

| line | len | member | says |
|---:|---:|---|---|
| 3985 | 3 | `static String pieceWords(Debt d)` | A piece of paper in words: its kind, as the book writes it, and its maturity. |
| 3994 | 23 | `static List<Debt> sellPaper(Game g, KeptBench bench) throws Exception` | The city's books put where the Finances screens have something to draw: one piece of every kind of paper sold through the model, as the Borrow page's own button sells it (executeDebtLogic, at the default highest yield... |
| 4023 | 14 | `static Debt booked(Game g, String[] paper, double face)` | CD9-K (FS5): a piece of paper of a kind PAPER names put on the city's books directly, through the DebtManager's own booking (the one the sale and the central bank's roll use), when the market refused the sale: what th... |
| 4039 | 6 | `static Hand financesOn(String area, String page)` | The hand that puts the Finances tab on an area and a page. |
| 4047 | 7 | `static Hand lineOf(Debt piece)` | The hand that opens a piece's own page, and the piece still on the books is its premise. |
| 4056 | 7 | `static Hand closeOf(Debt piece)` | The hand that opens Close it now on a piece (from its line's page when that is open), the same premise. |
| 4065 | 81 | `static void finances(Game g, KeptBench bench) throws Exception` | Sections 58 to 60: the Finances tab kept. |
| 4151 | 18 | `static Game borrowCity() throws Exception` | Borrow's own city, with a finite bank bid that clears the normal ask and leaves the fortyfold ask short. |
| 4171 | 61 | `static List<Choice> borrowChoices(Game g, KeptBench bench) throws Exception` | Borrow's choices on the finite-bid fixture. |
| 4234 | 28 | `static void financesMenus(Game g, KeptBench bench) throws Exception` | Sections 60's last part: the foreign default menu and the debt result menu, each an arrival and a month later. |

### the city's fund (lines 4263-5005)

| line | len | member | says |
|---:|---:|---|---|
| 4266 | 6 | `static Hand fundOn(String page)` | The fund's pages by name (Portfolio, Search, Activity, Rules & cash). |
| 4274 | 6 | `static Hand securityOf(String key, String from)` | The hand that opens a security's page by its lot key, from a page of the fund. |
| 4282 | 6 | `static Hand ticket(boolean buy, boolean byAmount, double amount, String kind, int steps, boolean review)` | The hand that sets the ticket on the security open. |
| 4290 | 4 | `static String lotOf(List<String> from, String prefix, List<String> not, boolean rescue)` | The first lot key in `from` that starts with `prefix` and is not in `not` (and has no ":rescue" in it unless `rescue`), or null. |
| 4312 | 32 | `static Runnable fundHeld(Game g) throws Exception` | KF63: hold the rule over its default withdrawal and keep cash over FUND_HELD_DUES transfers. |
| 4346 | 3 | `static void fundRule(Game g, boolean letGo)` | Under fundHeld(), the rule let go (the default withdrawal: it buys) or held again (a step over it: it buys nothing), KF63. |
| 4351 | 16 | `static String lastingBond(Game g, List<String> from, List<String> not) throws Exception` | The bond among `from` and not in `not` that runs the longest (none of the rescue's), or null: a bond's page watched for a run of months must not be repaid in it (KF63). |
| 4369 | 6 | `static int monthsLeft(Game g, String key) throws Exception` | How many months the bond under `key` runs from now, 0 when it is gone. |
| 4377 | 6 | `static long automaticFundBuys(Game g)` | Automatic bids already committed before a rule hold: the following market step must drain both books. |
| 4385 | 6 | `static int cancelAutomaticFundOrders(Game g)` | Explicitly cancel the automatic strategy's outstanding commitments, leaving the hand's orders alone. |
| 4393 | 299 | `static void fund(Game g, KeptBench bench) throws Exception` | Sections 61 to 63: the city's fund kept. |
| 4694 | 10 | `static List<OrderBook.Fill> settleFundBid(BondMarket market, CorporateBond bond, TreasuryFund.HandOrder bid)` | FS5/KFP1: isolate the posted hand bid from rival bids, then cause its counterparty through the book's real settlement. |
| 4706 | 5 | `static int fundDial(String name) throws Exception` | The screen's own paging dials, also used by the bench's package-private hands. |
| 4713 | 9 | `static List<javafx.scene.layout.HBox> fundResultRows(KeptBench bench)` | Search rows in their on-screen order, leaving section heads and the paging control out. |
| 4724 | 15 | `static void fundSearchPage(Game g, KeptBench bench, int limit)` | The names in Search have the market's original order, including a held lot repeated in its market section. |
| 4741 | 43 | `static void fundYearNet(Game g) throws Exception` | FP1's real sale, income and contribution in one older year cause the summary's different money columns. |
| 4792 | 24 | `static int searchPagesLong(Game g, int rows) throws Exception` | FB6 (LX1's two Search lines): the fund's Search list made longer than two of its pages. |
| 4823 | 11 | `static int olderYears(Game g) throws Exception` | FB6 (LX1's Activity line): the fund's record given lines in two older years. |
| 4835 | 134 | `static void fundLists(Game g, KeptBench bench, KeptBench.Page tab) throws Exception` | Section 63 opens and shuts the paging controls and each older year, kept against a page built new. |
| 4971 | 34 | `static void fundWarrantRow() throws Exception` | A loaded preferred block's warrants give a reused Search row an empty move after a wrapped value. |

### the pictures (64) (lines 5006-5263)

| line | len | member | says |
|---:|---:|---|---|
| 5012 | 5 | **type** `record Spot(String label, KeptBench.Page page, Hand setup, MonthHand month)` | A place a picture is seen: its label, the page, the hand that sets the page for it after the page's home, and what the player does on the page in each month it is watched, after the month and before the draw (null: no... |
| 5013 | 3 | `Spot(String label, KeptBench.Page page, Hand setup)` _(in KeptCheck.Spot)_ |  |
| 5019 | 3 | **type** `interface MonthHand` | What the player does on a page in a month of the watch (the first month is 1). |
| 5020 | 1 | `void on(KeptBench bench, int month) throws Exception` _(in KeptCheck.MonthHand)_ |  |
| 5027 | 5 | `static Debt longestPiece(KeptBench bench) throws Exception` | The piece of paper on the books that falls due last: the one a line's page and Close it now are drawn on (null when the city has none). |
| 5042 | 16 | `static Debt paperLasting(Game g, KeptBench bench, String what) throws Exception` | KS2: the piece of paper sections 66 and 67 draw on, caused. |
| 5060 | 45 | `static List<Spot> pictureSpots(Game g, KeptBench bench, Debt piece) throws Exception` | The places the money screens' pictures are: one page of each tab that holds some, and the pages behind them. |
| 5107 | 41 | `static void moneyPictures(Game g, KeptBench bench) throws Exception` | Section 64: census money pictures on their first two pages retain nodes/nine-in-ten parts, repaint only changed figures, never repaint an unchanged draw, and equal fresh. |
| 5153 | 6 | `static double[] deskFigures(Game g)` | What those pictures read of the model, to the bit (WG1-R): the curve at 12 and 120 months and what the book is worth today. |
| 5161 | 102 | `static void pictureKept(Game g, KeptBench bench, Spot spot, String kind) throws Exception` | One kind of picture on one page, watched for PICTURE_MONTHS months. |

### the count-up (65) (lines 5264-5489)

| line | len | member | says |
|---:|---:|---|---|
| 5267 | 10 | `static int[] started(KeptBench bench, java.util.Set<Animation> before)` | The animations a draw has started since `before` that are running now: {pops, counts and growths, others}. |
| 5279 | 5 | `static java.util.Set<Animation> animationsNow(KeptBench bench)` | The animations on the page now, as a set by identity. |
| 5286 | 5 | `static int running(KeptBench bench, Class<? extends Animation> kind)` | How many animations of a kind run on the page now. |
| 5293 | 7 | `static String stillOrNot(KeptBench bench)` | What is wrong with a page on which nothing may animate: a running animation, a figure not at rest. |
| 5306 | 29 | `static String bankRung(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 65 on the Bank tab (the Overview when `page` is null): the page is shown, a month lands (one that moved the capital ratio, on the Overview) and it is shown again; the PROFIT figure pops and ... |
| 5337 | 4 | `static void steerGauge(Game g)` | The Bank's risk-weighted ratio made the one that binds (see bankRung()): the book weighed for risk set to twenty times what the bank holds. |
| 5343 | 15 | `private static String bankRungChecks(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect, java.util.Se...` | ...what the month that landed started, once shown (the weighted book still steered on the Overview, so that the page built new beside it is drawn on the same bank). |
| 5363 | 10 | `static void fundPaymentMonth(Game g)` | A holding and a payment settled into the model fields the Portfolio reads, after the real month has landed. |
| 5375 | 29 | `static String fundRung(Game g, KeptBench bench, int at, boolean paused, boolean expect) throws Exception` | ...and the fund's Portfolio, through the tab's real show method: the worth counts on and a payment pops. |
| 5406 | 83 | `static void moneyCountUp(Game g, KeptBench bench) throws Exception` | Section 65: Bank profit/capital and Fund worth/payment animations obey paused/COUNT_UP_TOP at every rung, stop above it and show current figures. |

### the boxes (66) (lines 5490-5650)

| line | len | member | says |
|---:|---:|---|---|
| 5497 | 63 | `static void boxHeld(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.Su...` | One box on a page, given the focus and typed into (a node's `setFocused` when this scene has no window to give the focus: the line says which): a month landed through the real show method twice - the same node, its te... |
| 5562 | 52 | `static void boxEnter(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.S...` | Section 66 Enter (KP2b/KP5): focused yield "4.5" commits and redraws as "4.500" on the same node/caret/focus; putBack restores the cap around the test. |
| 5616 | 34 | `static void moneyControls(Game g, KeptBench bench) throws Exception` | Section 66: the boxes keep the player's hand - Close it now's two, the ask's, the yield boxes of Borrow's and the rollover's chips (typed into, a chip pressed, and Enter), the fund's search box. |

### the hover (67) and the cost (68) (lines 5651-5710)

| line | len | member | says |
|---:|---:|---|---|
| 5654 | 30 | `static void moneyHover(Game g, KeptBench bench) throws Exception` | Section 67: the hover. |
| 5686 | 17 | `static void money(Game g, KeptBench bench) throws Exception` | Sections 55 to 67: the money screens kept (68, their cost, is KeptTimingCheck's since FS4). |
| 5705 | 5 | `private static long took(String what, long since)` | What a stretch of the money sections took, in a line; the clock's reading for the next. |

### GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept ... (lines 5711-5984)

| line | len | member | says |
|---:|---:|---|---|
| 5721 | 5 | `private static long tookWith(String what, long since, int checksSince)` | What a stretch of the KP6 sections took and how many assertions it made, in a line; the clock's reading for the next. |
| 5735 | 21 | `static double[] makeDeficit(Game g) throws Exception` | The taxes cut to nothing, the central bank printing for the treasury and two lines of arrears owed; what the dials were. |
| 5758 | 10 | `static void endDeficit(Game g, double[] was)` | The dials put back where makeDeficit() found them. |
| 5770 | 5 | `static int linesSaying(KeptBench bench, String marker)` | How many lines of the page's signature hold a marker (a card the page draws only in some states). |
| 5777 | 5 | `static List<Choice> asActing(List<Choice> choices)` | Acting choices (Choice.acts): model ranking/budget changes may replace cards; print their share without asserting retention, but still require every redraw equal fresh. |
| 5784 | 28 | `static List<Choice> governmentChoices(boolean debt)` | The Government tab's pages, folds and lines as choices (the folds that must add to their page only where the city has what they fold). |
| 5814 | 21 | `static int[] linesThatAdd(KeptBench bench, KeptBench.Page tab, String page) throws Exception` | How many of a Government page's lines add to it when opened into who pays: {added, tried}. |
| 5837 | 3 | `static void governmentComparisonsReleased(KeptBench bench) throws Exception` | FS5: drain callbacks from the non-FX driver, then judge the fresh graph's lifetime without another draw. |
| 5841 | 27 | `private static void comparisonsReleased(KeptBench bench, boolean bank) throws Exception` |  |
| 5870 | 3 | `private static boolean comparisonFreshReleased(KeptBench.ReleasedComparison r)` | The identical lifetime law for the ordinary restore and its real leaked-page negative control. |
| 5875 | 109 | `static void kp6Government(Game g, KeptBench bench) throws Exception` | Sections 69 to 71: the Government tab kept. |

### Trade (lines 5985-6298)

| line | len | member | says |
|---:|---:|---|---|
| 5988 | 6 | `static Hand tradeOn(String page)` | The hand that puts the Trade tab on a page. |
| 5996 | 6 | `static Hand tradeNationOn(int nation)` | The hand that puts the Trade tab on a nation's page behind Partners (0 is the table). |
| 6004 | 6 | `static Hand tradeFold(String key, boolean open)` | A fold or a line of the Trade tab opened or closed. |
| 6012 | 3 | **type** `interface Verify` | What a scenario of 74b checks of the warning's three texts (title, line, what is behind its (i)), null under the bound. |
| 6013 | 1 | `void on(String[] words)` _(in KeptCheck.Verify)_ |  |
| 6023 | 20 | `static void boundRun(Game g, KeptBench bench, KeptBench.Page tab, double annual, String name, ForeignAccounts.Regime regime, bo...` | One scenario of 74b (XI2): the regime is entered (a peg struck, a band centred, at the rate of the day), the treasury has dealt the pieces (shares of a year's output; the vault only, no month's cash), and the exchange... |
| 6050 | 73 | `static void kp6TradeBound(Game g, KeptBench bench, KeptBench.Page tab, String reservesName) throws Exception` | Section 74b (XI2): Trade's warning past the bound reads the month's dealing and not the deal in hand, and says what the regime does with it. |
| 6125 | 173 | `static void kp6Trade(Game g, KeptBench bench) throws Exception` | Sections 72 to 74: Trade & the World kept. |

### Policy (lines 6299-6549)

| line | len | member | says |
|---:|---:|---|---|
| 6302 | 6 | `static Hand policyOn(String area, String page)` | The hand that puts Policy on an area and a page (the hub when `area` is null). |
| 6310 | 3 | `static Choice policyPage(String area, String page)` | A choice that opens a page of Policy. |
| 6315 | 3 | `static Choice stage(String name, String key, double value)` | A choice that stages a change under a lever's key. |
| 6320 | 3 | `static Choice drop(String name)` | A choice that drops everything staged. |
| 6325 | 3 | `static Choice policyFold(String name, String key, boolean open, boolean adds)` | A fold or a line of Policy, opened or closed, as a choice (`opens` for a fold that has to add to its page). |
| 6330 | 6 | `static Choice policyAllClosed(String name)` | Every fold and line of Policy closed. |
| 6338 | 174 | `static void kp6Policy(Game g, KeptBench bench) throws Exception` | Sections 75 to 77: Policy and zoning kept. |
| 6518 | 31 | `static void peopleShort(Game g, KeptBench bench, String[] hunger) throws Exception` | People's WHO GOES SHORT list and card (sections 1 to 5 draw the page): with people short of food the card says so and draws its bar, the People page kept for a stretch of months equals a page built new, and Policy's F... |

### the controls (78) (lines 6550-6879)

| line | len | member | says |
|---:|---:|---|---|
| 6556 | 1 | **type** `record AtOnce(java.util.function.Supplier<Double> now, Runnable putBack)` | A dial that applies at once and stages nothing (the policy rate page's target and HOW STRICT): what the model reads at the dial now, and how it is put back (KP2b: the held dials were all staged ones). |
| 6559 | 4 | `static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> sliderAt(int index, double m...` | The slider at a place in a page's sliders, when it has these ends - else null (the page is not the page this expects). |
| 6565 | 7 | `static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> offsetDial(int index)` | The slider at a place among a page's sliders that run under nothing (a tax offset's dial, which reaches below zero), else null. |
| 6574 | 12 | `static String whoSet()` | The few frames of the code that made a change just heard by a listener (skipping this harness and the listener's own plumbing): who set it. |
| 6591 | 18 | `static String taxDialMoving(KeptBench bench, int index) throws Exception` | The first sector whose zone page has a tax dial (the `index`th that runs under nothing) that moves what the ITS TAXES card says it would do when it is dragged two steps, the dial put back after - or null. |
| 6611 | 7 | **type** `static final class Held` | What one held dial did, for holdDial to judge. |
| 6620 | 131 | `static void holdDial(Game g, KeptBench bench, String what, String area, String page, java.util.function.Function<List<javafx.sc...` | Section 78: Policy dials write unchanged readings once, retain held identity/thumb/reading/effects through months, then stage/apply/drop and equal fresh (KP2b). |
| 6753 | 3 | `static void policyDial(Game g, KeptBench bench, String what, String area, String page, String key, boolean trayShows) throws Ex...` | Section 78's dial, held, the first slider of a page: see holdDial. |
| 6758 | 65 | `static void heldWithCardInFront(Game g, KeptBench bench, String what, KeptBench.Page p, Hand setup, Hand front) throws Exception` | Section 78: setup then front inserts a keyed card before a held dial; the original node/thumb/reading/ladder stay with their payer/course, not list position. |
| 6825 | 25 | `static void policyDialsKeep(Game g, KeptBench bench) throws Exception` | Section 78's last part: a card in front of a held dial, on the Schools page (a course's own price in front of the loan's) and on a tax page (a payer's own move in front of another's). |
| 6852 | 27 | `static void kp6Controls(Game g, KeptBench bench) throws Exception` | Section 78: the controls on Policy - a tax page's rate dial, the policy rate's, a zone's ground dial, and a card coming in front of a held dial. |

### the count-up (79) (lines 6880-7086)

| line | len | member | says |
|---:|---:|---|---|
| 6883 | 3 | `private static boolean moved(String[] words)` | Whether a counted figure's words moved between last month and this. |
| 6893 | 56 | `static String tradeRung(Game g, KeptBench bench, String page, boolean bars, int at, boolean paused, boolean expect) throws Exce...` | One clock speed of section 79 on a Trade page: the page is shown through its real show method, a month that moved SOLD ABROAD or BOUGHT ABROAD lands and it is shown again; each of the strip's two figures that moved st... |
| 6954 | 21 | `static String quietRung(Game g, KeptBench bench, KeptBench.Page page, Runnable open, Runnable show, int at, boolean paused, boo...` | One clock speed on a page that has no animation to run (a Government or Policy page): a month lands and the page is shown through its real show method; nothing starts at any rung, nothing is running or off its rest, a... |
| 6977 | 109 | `static void kp6CountUp(Game g, KeptBench bench) throws Exception` | Section 79: Trade SOLD/BOUGHT counts and Overview/What-we-trade bars obey paused/COUNT_UP_TOP, stop past it and show current words; Government/Policy never animate. |

### the hover (80) and the cost (81) (lines 7087-7227)

| line | len | member | says |
|---:|---:|---|---|
| 7090 | 14 | `private static void hoverKP6(Game g, KeptBench bench, java.util.Map<String, Boolean> found, String where, KeptBench.Page page, ...` | The pointer put over a node of each kind on a page (its `setup` made first), hoverLine() saying what came of it; `found` notes where a node was found. |
| 7106 | 34 | `static void kp6Hover(Game g, KeptBench bench) throws Exception` | Section 80: Government/Trade/Policy doors, cells and rows retain node and mouse-entered look under a still pointer; mouse-exited restores their rest look. |
| 7142 | 55 | `static void kp6Tips(Game g, KeptBench bench) throws Exception` | Section 80: surviving nodes on all three screens (including Trade river) keep the same Tooltip across a month; Kept.tip updates its words/delay. |
| 7199 | 28 | `static void screensKP6(Game g, KeptBench bench) throws Exception` | Sections 69 to 80: Government, Trade & the World and Policy kept (81, their cost, is KeptTimingCheck's since FS4). |

### POLICY > RESEARCH'S OVERVIEW AND FUNDING (R3b, slice B), sections 99 to 101: the two pages, the hub's Research card, ... (lines 7228-7785)

| line | len | member | says |
|---:|---:|---|---|
| 7238 | 6 | `static Game researchFundingTown() throws Exception` | The city of sections 99 to 101: ResearchViewCheck.fundedTown() - a 1900 city with two Universities, an Institute of technology and a Medical school, and grants in engineering, medicine and business that bought posts -... |
| 7246 | 12 | `static Game researchDefaultTown() throws Exception` | A city founded the default way with a University and a grant in engineering that bought posts (ResearchViewCheck's section 15 city), played three months. |
| 7261 | 8 | `static ResearchView.Card syntheticCard(boolean researching, int room, int inReach, double idle, double total, boolean grantMove...` | A Funding card made by hand: the read that ResearchScreen.sayWords() chooses a sentence from. |
| 7275 | 19 | `static void sayCases() throws Exception` | The sentence under a Funding card for every case the model's reads can make, in the order the screen chooses among them: a city founded the default way; no school; nothing in reach; a prize that brings labs; one under... |
| 7295 | 4 | `static String fieldWord(ResearchCatalog.Field f)` | A field as the screens read it: "Engineering". |
| 7301 | 3 | `static String researchersWord(double n)` | "412 researchers", "1 researcher". |
| 7306 | 10 | `static List<String> shownWith(KeptBench bench, KeptBench.Page page, Hand hand) throws Exception` | The page home, a hand on it, drawn and laid out: its signature. |
| 7321 | 25 | `static void monthsAgainstNew(Game town, KeptBench bench, KeptBench.Page page, String what, int months, Hand hand) throws Exception` | A page, after a hand on it, kept over last month's nodes for `months` months: the signature equal to a page built new at each, and nine in ten of its nodes handed back on average (the share section 96 holds a page to). |
| 7348 | 23 | `static void researchFunding(Game g, KeptBench defaultBench) throws Exception` | Sections 99-101 (R3b): Research Overview/Funding in default and funded 1900 cities equal fresh; model reads, staged/held/applied dials are checked. |
| 7373 | 99 | `static void researchDefaultWay(Game g, KeptBench defaultBench) throws Exception` | Section 99, the first part: in a city founded the default way. |
| 7474 | 81 | `static void researchOverview(Game town, KeptBench bench) throws Exception` | Section 99, the second part: the Overview and the hub's Research card in the 1900 town. |
| 7557 | 156 | `static void researchFundingPage(Game town, KeptBench bench) throws Exception` | Section 100: the Funding page in the 1900 town. |
| 7715 | 70 | `static void researchSchoolsAndBooks(Game town, KeptBench bench) throws Exception` | Section 101: the schools' research posts on Services, the research line on Government's Spending list, and the wage floor's payroll. |

### THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of ... (lines 7786-8091)

| line | len | member | says |
|---:|---:|---|---|
| 7791 | 8 | `static boolean has(List<String> signature, String...words)` | Whether some line of a signature has all of these words in it. |
| 7801 | 3 | `static String label(String text)` | A label's line in a signature: `Label "text"`. |
| 7806 | 194 | `static void researchFound(Game g, KeptBench defaultBench) throws Exception` | Section 95 (R5): default quiet Found card versus researchTown finds/recipe/equipment/template/charts, kept and reshaped; pure timeline stacking/ticks/flags/colours/decisions. |
| 8002 | 1 | **type** `private record RailScreen(String label, String name, java.util.function.Consumer<KeptBench> show)` | A rail screen the clock is held to in section 88: how it is called here, the name its show method gives the window, and the call that shows it. |
| 8008 | 83 | `static void kp7Clock(Game g, KeptBench bench) throws Exception` | Section 88: prove rebuiltInPlace counts old redraws but not arrivals; all real rail screens retain tops, screen name and handed-back nodes through CLOCK_MONTHS. |

### HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page draw... (lines 8092-8400)

| line | len | member | says |
|---:|---:|---|---|
| 8099 | 57 | `static void kp7Household(Game g, KeptBench bench) throws Exception` | Sections 82 to 84: Household money kept, shown as a page built new shows it, and kept right through what reshapes it. |
| 8158 | 97 | `static void kp7Rankings(Game g, KeptBench bench) throws Exception` | Sections 85 to 87: the Rankings tab kept, shown as a page built new shows it, and kept right through what reshapes it - to the pixel, since its table is painted. |
| 8261 | 51 | `static int[] tipsAcrossMonth(Game g, KeptBench bench, KeptBench.Page page) throws Exception` | KHT1/FS5: hold FamilyModel and ledger cell counts across one month so grid cells stay open. |
| 8314 | 25 | `static void kp7Hover(Game g, KeptBench bench) throws Exception` | Section 89: Household doors/cells and Rankings rows retain identity/hover while still and clear on exit; surviving tooltips keep identity across open cells/drawers/measures. |
| 8341 | 40 | `static void kp7Tips(Game g, KeptBench bench) throws Exception` | Section 89's last part: the tooltips are kept on Household money and the Rankings tab. |
| 8383 | 17 | `static void screensKP7(Game g, KeptBench bench) throws Exception` | Sections 82 to 89: Household money and the Rankings tab kept, and the clock never rebuilding a rail screen (88); 90, their cost, is KeptTimingCheck's since FS4. |

### a slider greyed while held (91) (lines 8401-8514)

| line | len | member | says |
|---:|---:|---|---|
| 8404 | 5 | **type** `static final class Greyed` | What one greying of a held slider did, for greyedWhileHeld to judge. |
| 8411 | 61 | `static void greyedWhileHeld(Game g, KeptBench bench) throws Exception` | Section 91 (KP2b/KP2c): disabled policy-rate dial releases at scene (pressed/dragged), clears held/valueChanging, follows a new staged value and returns when dropped. |
| 8474 | 33 | `static void doorsStyledOnce(Game g, KeptBench bench) throws Exception` | Section 91 (KP2b): Policy hub/tax door cards set whole hand style once per draw; unchanged redraw fires no style changes, manual mutation proves the counter. |
| 8509 | 5 | `private static Notice add(Game g, String key, String title)` | A notice raised now, unread, in the inbox. |

### THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node... (lines 8515-8877)

| line | len | member | says |
|---:|---:|---|---|
| 8525 | 20 | `static void nothingSet(KeptBench bench, KeptBench.Page page, String name, Hand hand) throws Exception` | A page home, a hand on it, drawn and laid out, drawn once more with a listener on every style, text, graphic, padding and width on it (KeptBench.redrawSets), no month between: how many of them the second draw wrote, a... |
| 8546 | 3 | `static void nothingSet(KeptBench bench, KeptBench.Page page, String name) throws Exception` |  |
| 8554 | 18 | `static List<Debt> piecesOnTheBooks(Game g, KeptBench bench) throws Exception` | The pieces of paper on the city's books; where there are none (the months the sections before this played let every maturity pass) one sold through the model, as the Borrow page's own button sells it, the first of a T... |
| 8577 | 7 | `static void letGo(KeptBench bench, java.util.function.Consumer<KeptBench> forget) throws Exception` | Section 92 lets one tab's pages go before it opens the next (KP2b): the tab forgotten, as another city opened leaves it, and the pages built new released (KeptBench.release). |
| 8586 | 127 | `static void kp2bSets(Game g, KeptBench bench) throws Exception` | Section 92: the pages the sweep mended, each redrawn with nothing new in the city. |
| 8715 | 34 | `static void kp2bHouseholdMoney(Game g, KeptBench bench) throws Exception` | Section 92 on Household money (KP7, taken into KP2b): the page as it opens, each view, every cell of the grid opened, the statement and the books stacked under the grid. |
| 8754 | 11 | `static int rankingsSavedCities(KeptBench bench) throws Exception` | The city saved into slot 10 of the harness's own folder and read back, so that the Rankings tab has a saved city on its board: section 87 does it, and does nothing here where it has run. |
| 8778 | 9 | `static boolean rankingsReadAwaited(KeptBench bench) throws Exception` | The Rankings tab's read of the saved cities waited on to its own end: the screen's worker hands its answer to the FX thread, which clears its reading flag and keeps the snapshot (rankingsReadLanded), asked between sle... |
| 8794 | 24 | `static void kp2bRowsPicture(Game g, KeptBench bench) throws Exception` | Section 93: the Rankings table's picture (KP2b), the one node on the page that paints its rows. |
| 8824 | 53 | `static void kp2bRankings(Game g, KeptBench bench) throws Exception` | Section 92 on the Rankings tab (KP7, taken into KP2b): the page as it opens, each board, money, prices and conversion, some measures (every tenth, the last and the first no real place is ranked on), each filter, Aroun... |

### THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7): sections 94 and 98 (lines 8878-9856)

| line | len | member | says |
|---:|---:|---|---|
| 8890 | 4 | `static void putSitesOn(BuildingManager buildings, int n)` | Puts the played city's building stacks in a state with this many sites on (the stacks' own setter, the one a loaded save uses): the first n stacks under construction, the rest not, so the tab's count and its tooltip a... |
| 8896 | 4 | `static boolean cityIsScratch(Game g)` | Whether the scratch city's folder is under this JVM's TempDirs folder: then a click that saves the prefs writes nothing of the player's. |
| 8902 | 220 | `static void up7Frame(Game g, KeptBench bench) throws Exception` | Section 94 (UP7): real drawer bar/construction tab kept and fresh across pin/fold/screen/site states, with spoil/redraw/hover/tooltips/clicks. |
| 9124 | 17 | `static ClockPace paceFor(double rung, boolean short_)` | A pace on a clock moved by hand, run past a full window: at pace, or short of the rung (a rung of 50 by months of 120 ms as ClockCheck's falls, the rest by a city that runs at two fifths of it). |
| 9143 | 4 | `static void drawHeader(KeptBench bench) throws Exception` | The header drawn kept and laid out, as the clock's redraw leaves it. |
| 9149 | 183 | `static void up7Speed(Game g, KeptBench bench) throws Exception` | Section 98 (UP7): real header pace at/short of every rung uses speed-only amber or rung colour, fixed unclipped width, stationary neighbours and one changed write. |
| 9334 | 9 | `static boolean onceEach(KeptBench.Sets s)` | Whether a draw wrote the reading's words once and its style once, and nothing else. |
| 9345 | 134 | `static void fb2Repairs(Game g, KeptBench bench) throws Exception` | Section 103 (FB2): cause KL1 quote, KS1-UI header/bar, BF1-K Partners/Search and KS1-Gov recurring-line regressions independently of the played city path. |
| 9488 | 60 | `private static void rendererCacheControls(KeptBench bench) throws Exception` | Return this diagnostic frame before the final GC, so its roots cannot inflate the heap reading. |
| 9550 | 93 | `static void kp2cMends(Game g, KeptBench bench) throws Exception` | Section 102: the kept sweep's remaining draw, drag and retention conditions, each caused here. |
| 9644 | 12 | `public static void main(String[] args) throws Exception` |  |
| 9664 | 12 | `static Game playedCity(GameFiles files) throws Exception` | KeptCheck's city, Danzik: LongPlaytest's probe city, its minimum wage pinned at the going rate, played CITY_MONTHS by the advisor. |
| 9678 | 178 | `static void mainSections(Game g, KeptBench bench) throws Exception` | Every section, in order, on the played city. |


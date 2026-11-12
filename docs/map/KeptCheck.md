# KeptCheck.java - 8,862 lines · 201 methods · 43 constants · harnesses

`ham/citybuildersim/KeptCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

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
> values code set, handlers, tooltip and place, and at some months every pixel - and only then
> times the kept month.
> 
> It needs the JavaFX toolkit but no window: the page is laid out on a scene of its own
> (ui.KeptBench). Where the toolkit cannot start (the cloud has no display) it says so in one line
> and exits 0.
> 
> What it has to prove:
>   1. the page is kept: after its first draw a month's redraw hands back last month's nodes and
>      keeps the same page in the column; the nodes made new in a month are few, and on most months
>      none (counted over the stretch);
>   2. it shows what a page built new shows: for MONTHS months of a played city the kept page's
>      signature - every node's class, words, inline style, the values code set, handlers, tooltip
>      and where layout put it - equals that of the page built new at the same month, line for line;
>   3. and looks it: at three of those months the two pages are the same picture - no pixel more
>      than SHADE_LEVELS apart in a channel, and at most SHADED_SHARE of them apart at all (the
>      renderer shades a moved shape's edge a level or two apart; the count of months with the very
>      same pixels is printed);
>   4. what reshapes the page keeps it right: a details fold opened and closed, the month card's
>      year view on and off - each redraw after the change equals a page built new;
>   5. it is cheap: the kept page's month (its draw, then the CSS and layout after it) is within
>      KEPT_MONTH_MS through Timing, and below the same month built new (both printed).
> 
> Since UP2 the window's chrome too - the header and the tab rail, drawn by the real
> refreshHeader() and refreshTabRail() on scenes of their own (the header at the window's width,
> the rail at its height), the envelope drawn on its own as well:
>   6. the chrome is kept: after its first draw a month's chrome draw keeps the header's and the
>      rail's top nodes, hands back nine in ten of the nodes the first draw made, and makes few or
>      none;
>   7. it shows what a chrome built new shows: for MONTHS months the kept header's and rail's
>      signature equals that of a chrome built new (every pool swapped for an empty one), line for
>      line, the first difference printed with both lines, while the states change among the
>      months - the clock running and stopped, a "stopped because" line shown and gone, the speed's
>      rung, the screen on show (so the rail's lit tab, and a game menu with no clock), a notice
>      raised, read and put away (the envelope's count, redrawn on its own);
>   8. and looks it: at three of those months the header and the rail are the same pictures as the
>      new ones (alike(): SHADE_LEVELS, SHADED_SHARE); one figure in the kept header made wrong by
>      hand parts the signature and the picture, and the next kept draw sets it right; and what the
>      rebuild did by being one is put back: a node that had the focus loses it, a button or tile
>      drawn again under the pointer keeps its hover look and loses it when the pointer goes;
>   9. a city opened forgets the chrome: the next draw makes every node new; and a draw of one pool
>      inside a draw of another (the envelope inside the header) gives each its own nodes;
>  10. it is cheap: the kept chrome's month (its draw, then the CSS and layout of both scenes) is
>      within KEPT_CHROME_MS through Timing, the same month built new printed beside it.
> 
> ...and the three panels (slice 2b), each on a scene of its own: the city panel, the construction
> panel and the inbox's list, drawn by refreshCityPanel(), refreshConstructionPanel() and
> refreshInboxList():
>  11. the panels are kept: with the inbox open, the drawer open on every section and buildings on
>      site, a month's draw keeps their top nodes, hands back nine in ten of the first draw's nodes
> ... (250 more lines in the source)

**Uses:** [KeptBench](KeptBench.md) (675), [Game](Game.md) (142), [ResearchView](ResearchView.md) (119), [Money](Money.md) (59), [Sectors](Sectors.md) (51), [Debt](Debt.md) (21), [DecisionLog](DecisionLog.md) (21), [ResearchViewCheck](ResearchViewCheck.md) (19), [Where](Where.md) (19), [BuildAdvice](BuildAdvice.md) (17), [ForeignAccounts](ForeignAccounts.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (16), [ResearchCatalog](ResearchCatalog.md) (15), [Notice](Notice.md) (12), [ChartModel](ChartModel.md) (11), [Timing](Timing.md) (10), [CityCalendar](CityCalendar.md) (8), [Auction](Auction.md) (7), [Research](Research.md) (7), [WellCheck](WellCheck.md) (6), [OilCheck](OilCheck.md) (6), [ClockPace](ClockPace.md) (6), [BuildingManager](BuildingManager.md) (5), [Palette](Palette.md) (5), [RefineryViewCheck](RefineryViewCheck.md) (4), [OilViewCheck](OilViewCheck.md) (4), [WageBand](WageBand.md) (4), [DebtManager](DebtManager.md) (4), [IndustryView](IndustryView.md) (3), [RefineryView](RefineryView.md) (3)... and 26 more

**Used by (4):** [ChartFitCheck](ChartFitCheck.md), [MapCheck](MapCheck.md), [ResearchViewCheck](ResearchViewCheck.md), [ToastCheck](ToastCheck.md)

## Sections

| line | section |
|---:|---|
| 1482 | INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page |
| 2091 | BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, |
| 2680 | SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and |
| 2790 | THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97 |
| 3600 | THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... |
| 3838 | · Finances |
| 4075 | · the city's fund |
| 4250 | · the pictures (64) |
| 4451 | · the count-up (65) |
| 4677 | · the boxes (66) |
| 4839 | · the hover (67) and the cost (68) |
| 4969 | GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept ... |
| 5233 | · Trade |
| 5547 | · Policy |
| 5798 | · the controls (78) |
| 6141 | · the count-up (79) |
| 6354 | · the hover (80) and the cost (81) |
| 6523 | POLICY > RESEARCH'S OVERVIEW AND FUNDING (R3b, slice B), sections 99 to 101: the two pages, the hub's Research card, ... |
| 7085 | THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of ... |
| 7405 | HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page draw... |
| 7709 | · a slider greyed while held (91) |
| 7832 | THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node... |
| 8183 | THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7): sections 94 and 98 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 349 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 352 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 355 | `KeptCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 358 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 453 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's cached outline a le... |
| 456 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 499 | `KeptCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 502 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 724 | `KeptCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |
| 994 | `KeptCheck.KEPT_HISTORY_MS` | `13` | The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same mon... |
| 1024 | `KeptCheck.TICKED` | `"unemployment"` | The first of these lines picked and not in the preset the page opens on: a line to tick and untick. |
| 1488 | `KeptCheck.HANDED_BACK` | `0.9` | The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). |
| 1745 | `KeptCheck.KEPT_INFRASTRUCTURE_MS` | `9` | The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 betwe... |
| 1752 | `KeptCheck.KEPT_SERVICES_MS` | `13` | The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 betwe... |
| 1759 | `KeptCheck.KEPT_LAND_MS` | `5` | The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 ... |
| 1799 | `KeptCheck.SERVICES_FOLDS` | `{ { "Health/Overview", "health:sick" }, { "Health/General care", "health:gene...` | The folds each sub-page of Services has, by "system/page" (a page not named has none). |
| 2102 | `KeptCheck.KEPT_BUILD_MS` | `12` | The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a city category with two orders dialled up and a card's stats pinned (the heavier of ... |
| 2109 | `KeptCheck.KEPT_CONSTRUCTION_MS` | `4` | The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1... |
| 2112 | `KeptCheck.SITE_ROWS` | `6` | The rows the Sites tab and the timeline are kept at while Build and the construction page are watched: a site each. |
| 2115 | `KeptCheck.SITE_ORDER` | `{ "Gravel Road", "Walk-in Clinic", "Paved Road", "Bus Network", "Elementary S...` | What is ordered, in turn, to put a site on: the cheap, quick works first. |
| 2691 | `KeptCheck.KEPT_SECTORS_MS` | `18` | The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same mon... |
| 2700 | `KeptCheck.KEPT_SECTOR_MS` | `15` | The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the heaviest of the three pages costed (the shops' Operations; a plain business's Income stat... |
| 2703 | `KeptCheck.PICTURE_MONTHS` | `6` | The months the pictures are watched through (section 50): each month a repaint or not, and a page built new beside it. |
| 2709 | `KeptCheck.NOT_PLAIN` | `java.util.Set.of(Sectors.RETAIL, Sectors.RESTAURANTS, Sectors.LUXURY_RETAIL, ...` | The keys a plain business is not taken from: the shops, the kitchens, the counters, the landlords, the haulier and the fuel's two. |
| 2726 | `KeptCheck.WORKS_ORDER` | `{ "Sawmill", "Scrap Yard", "Wood Pellet Mill", "Panel Mill" }` | What a materials industry's works are made of, in turn, for a city that has none standing: a sawmill first, the rest if it cannot. |
| 2795 | `KeptCheck.R5_MONTHS` | `6` | The months each of the research sections plays on its page, kept over last month's nodes against a page built new. |
| 3611 | `KeptCheck.KEPT_BANK_MS` | `12` | The budget for the kept Bank month - its draw, then the pulse's CSS and layout - on the Bank's heaviest page (the Overview), on the PC: twice the kept month's median, rounded up (5.2 to 8.3 ms in five runs of the mone... |
| 3618 | `KeptCheck.KEPT_FINANCES_MS` | `18` | The budget for the kept Finances month on the heaviest of the two pages costed (Borrow at home, the page the auction draws, and the page with the most nodes), on the PC: twice the kept month's median, rounded up (the ... |
| 3627 | `KeptCheck.KEPT_FUND_MS` | `321` | The budget for the kept month on the fund's heaviest page (Search and Activity, the two costed, weigh most; their size follows the city's age: the bonds it has issued are the rows of Search, its months of events the r... |
| 3643 | `KeptCheck.BANK_FOLDS` | `{ { "Profit", "profit:statement", "Interest earned", "Fees", "Provisions for ...` | What the Bank's folds hold, by page: the fold's key, then the lines a player opens inside it (each a key the page's own bookLine opens by). |
| 3841 | `KeptCheck.PAPER` | `{ { "Note", "12", "" }, { "Float", "5", "" }, { "Serial", "10", "" }, { "Term...` | The paper the money sections sell, one of each kind the Borrow page offers (and a callable bond too): {kind, term, "callable" or ""}. |
| 3849 | `KeptCheck.SELL_ORDER` | `{ 4, 0, 1, 2, 3, 5 }` | The order sellPaper sells PAPER in: the 20-year bond first, the piece that falls due last, which sections 64, 66 and 67 draw for dozens of months, then the rest as PAPER has them. |
| 4268 | `KeptCheck.MOST_TO_PAY` | `{ "98.5", "101.5" }` | The most to pay typed on Close it now in the odd months of the watch, by turns (Spot's month hand). |
| 4980 | `KeptCheck.KEPT_GOVERNMENT_MS` | `7` | The budget for the kept Government month - its draw, then the pulse's CSS and layout - on the Overview, at month 3,500 of a played city (about where KeptCheck is when it comes to the Government tab), on the PC: twice ... |
| 4988 | `KeptCheck.KEPT_TRADE_MS` | `10` | The budget for the kept Trade month - its draw, then the pulse's CSS and layout - on the Overview and on the heaviest of its pages (the reserves), the larger of the two being the figure, at month 3,500 of a played cit... |
| 4996 | `KeptCheck.KEPT_POLICY_MS` | `10` | The budget for the kept Policy month - its draw, then the pulse's CSS and layout - on the hub and on the pages heavy with dials (Taxes' Profit page and the policy rate's), the largest of them being the figure, at mont... |
| 5005 | `KeptCheck.KP6_HANDED_BACK` | `.75` | The share of a KP6 page's nodes a month's redraw has to hand back after a choice, at least: three in four. |
| 5018 | `KeptCheck.TRADE_FOLDS_THAT_ADD` | `java.util.Set.of("month:ledger", "reserves:eight")` | The folds the Trade sections open that have to add to their page in any city that trades: the month's ledger and the eight moneys over the years. |
| 7311 | `KeptCheck.CLOCK_MONTHS` | `3` | The months landed, each followed by the clock's redraw, on every screen in section 88. |
| 7417 | `KeptCheck.KEPT_HOUSEHOLD_MS` | `8` | The budget for the kept Household money month - its draw, then the pulse's CSS and layout - on the page as it opens and with the first family cell's books open (the larger of the two being the figure), at month 3,400 ... |
| 7425 | `KeptCheck.KEPT_RANKINGS_MS` | `7` | The budget for the kept Rankings month - its draw, then the pulse's CSS and layout - on the tab as it opens and with every measure unfolded (Browse all: a line a measure; the larger of the two being the figure), at mo... |
| 7842 | `KeptCheck.NOT_KP2B` | `java.util.Set.of("UserInterface.java", "InfrastructureScreen.java", "Services...` | The files of the screens KP2b's sweep did not reach, whose writes section 92 names and does not count: the sweep was the list of screens Pieces, the statements, Sectors, Land, the Bank, the fund, Finances, Trade, Zoni... |
| 8188 | `KeptCheck.SITE_COUNTS` | `{ 0, 1, 3 }` | The sites a state puts the city in: none, one (the tab says "1 site"), three (it says "3 sites"). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 334 | `static int fails` |  |
| 382 | `static Runnable afterMonth` | What is done after every month played, with the log off, while a fixture has to be held through the months (KP3: the city's sites); null for nothing. |
| 2118 | `private static int siteTurn` |  |
| 5008 | `static int checks` | The assertions made so far, for the count a stretch of the KP6 sections reports. |
| 5860 | `String none, reading0, dragged, kept, differs, dropped, firstHeard` |  |
| 5861 | `boolean sameReading, sameBar, heldPressed, heldStill, heldAfter, same, tray, trayGone, stagedMid, saysDragg...` |  |
| 5862 | `int heardText, heardStyle, control, arrowsBefore, arrowsDragged, arrowsMid` |  |
| 5863 | `double shown, valueNow, thumb, cityAt, cityMid, cityAfter, cityBack, step, min` |  |
| 5864 | `Double stagedAt` |  |
| 7713 | `String none, differs, dropped, reading` |  |
| 7714 | `boolean heldPressed, heldGreyed, changingGreyed, sameBar, heldAfter` |  |
| 7715 | `double value, want, back, city` |  |
| 7845 | `static int ignoredSets` | The writes from NOT_KP2B's files, over the pages of section 92 (printed at its end). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 332 | 8531 | **type** `public class KeptCheck` | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exactly where a page built new would, and costs a fraction of it. |
| 336 | 5 | `static void check(String label, boolean ok)` |  |
| 343 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure (R3b, sections 99 to 101). |
| 360 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 367 | 13 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on). |
| 385 | 10 | `static void month(Game g)` | A month, as the clock runs it, its log kept off this harness's output. |
| 397 | 9 | `static<T> T quietly(Callable<T> call) throws Exception` | A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). |
| 408 | 28 | `static String firstDifference(List<String> kept, List<String> fresh)` | The first line where two signatures part, or null when they are the same. |
| 438 | 6 | `static void png(int[] px, java.nio.file.Path to) throws java.io.IOException` | A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. |
| 459 | 10 | `static boolean alike(int[] a, int[] b)` | Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. |
| 471 | 16 | `static String pixelDifference(int[] a, int[] b)` | Where two snapshots part: their sizes, or how many pixels differ and the box round them. |
| 488 | 3 | `static String clip(String s)` |  |
| 492 | 5 | `static double median(List<Double> v)` |  |
| 510 | 212 | `static void chrome(Game g, KeptBench bench) throws Exception` | Sections 6 to 10: the header and the tab rail kept (UP2). |
| 727 | 11 | `static void panelStates(KeptBench bench, int at)` | The panels' states, month by month: the city panel's mode, and which of its sections are open. |
| 743 | 245 | `static void panels(Game g, KeptBench bench) throws Exception` | Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice 2b), each on a scene of its own, beside the header and the rail of 6 to 10. |
| 998 | 3 | **type** `interface Hand` | A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). |
| 999 | 1 | `String on(KeptBench bench) throws Exception` _(in KeptCheck.Hand)_ |  |
| 1008 | 9 | **type** `record Choice(String name, Hand hand, boolean opens, boolean acts)` | One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2); `acts` for a choice that makes the model act (KP3: automatic building turned on or... |
| 1009 | 3 | `Choice(String name, Hand hand, boolean opens)` _(in KeptCheck.Choice)_ |  |
| 1013 | 3 | `Choice(String name, Hand hand)` _(in KeptCheck.Choice)_ |  |
| 1019 | 3 | `static Choice acting(String name, Hand hand)` | A choice that makes the model act (see Choice.acts). |
| 1030 | 218 | `static void history(Game g, KeptBench bench) throws Exception` | Sections 14 to 20: City History kept (KP1). |
| 1250 | 7 | `private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved)` | Months landed until the figure has moved (at most twelve): whether it did. |
| 1259 | 3 | `private static String nullIfOffered(String refusal, String what)` | The choice's refusal, as the premise it could not meet, or null when the chart is offered. |
| 1269 | 77 | `static void crosshair(Game g, KeptBench bench) throws Exception` | Section 18: what a page built new loses and a kept one keeps. |
| 1353 | 96 | `static void countUp(Game g, KeptBench bench) throws Exception` | Section 19: the count-up is for a clock that can be followed. |
| 1451 | 30 | `static void historyCost(Game g, KeptBench bench) throws Exception` | Section 20: what the kept City History month costs, against the same month built new. |

### INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page (lines 1482-2090)

| line | len | member | says |
|---:|---:|---|---|
| 1491 | 3 | `static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. |
| 1501 | 16 | `static String againstNew(KeptBench bench, KeptBench.Page page, boolean pixels) throws Exception` | ...and, for a page with pictures painted on canvases, which no signature holds (KP7, the Rankings table), the canvases' pixels too: the signature's difference first, else the canvases' (no pixel more than SHADE_LEVELS... |
| 1519 | 3 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the page is kept - a month's redraw keeps its top nodes, hands back nine in ten of the first draw's nodes, and on most months makes none. |
| 1528 | 37 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n, boolean eventful) throws Exception` | ...for a page whose content is the model's events (`eventful`, KP5: the city's fund, where a bond repaid, a payment and a quote line are rows of their own): the nodes a month makes are the month's new rows, so most mo... |
| 1567 | 71 | `static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. |
| 1644 | 3 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception` | Section n: every choice that reshapes the page, made in turn: the redraw after it and the one after a month lands are each equal to a page built new, and the month's redraw hands back nine in ten of the page's nodes. |
| 1649 | 3 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, double share) throws Exception` | ...with the share of the page a month's redraw has to hand back set by the caller (KP6: KP6_HANDED_BACK). |
| 1654 | 45 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, double share, boolean pixel...` | ...and, with `pixels`, each redraw's picture against the page built new's as well (KP7: the Rankings table is painted, and no signature holds it). |
| 1701 | 3 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception` | Section n: what the kept month on the page's home costs, against the same month built new. |
| 1706 | 28 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial, int samples) throws Excep...` | ...the median of `samples` months, for the pages whose month is long (the fund's, tens of thousands of nodes). |
| 1736 | 3 | `static Choice choice(String name, Hand hand)` | A choice with no side effect on the model: a hand that changes the screen's state only. |
| 1762 | 9 | `static String ensureTransit(Game g) throws Exception` | Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. |
| 1773 | 24 | `static void infrastructure(Game g, KeptBench bench) throws Exception` | Sections 21 to 24: Infrastructure kept. |
| 1811 | 21 | `static void services(Game g, KeptBench bench) throws Exception` | Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. |
| 1834 | 49 | `static void land(Game g, KeptBench bench) throws Exception` | Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. |
| 1890 | 123 | `static void controls(Game g, KeptBench bench) throws Exception` | Section 34: a control keeps the player's hand. |
| 2015 | 64 | `static void hover(Game g, KeptBench bench) throws Exception` | Section 35: the hover kept. |
| 2081 | 9 | `private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result)` | One hover result: printed, counted as found when the page had the node, and a check that it held. |

### BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, (lines 2091-2679)

| line | len | member | says |
|---:|---:|---|---|
| 2121 | 12 | `static boolean buyGround(Game g, BuildingsTemplate t, int n) throws Exception` | Ground bought through the model for an order that found none: the cheapest offer that covers the shortfall, the cash it lacks borrowed on Build's bond; whether it was bought. |
| 2135 | 30 | `static String ensureSites(Game g, int rows) throws Exception` | Orders through the model until the city has `rows` sites on (a row each on the Sites tab, a bar each on the timeline): what it did, in a line. |
| 2167 | 9 | `static void keepSites(Game g)` | From now every month played tops the sites up to SITE_ROWS (the fixture holds while the pages are watched), until stopSites(). |
| 2177 | 3 | `static void stopSites()` |  |
| 2182 | 137 | `static void build(Game g, KeptBench bench) throws Exception` | Build's pages: the Overview and a city category with two orders dialled up (so that its order bar shows), kept, as built new, reshaped and costed. |
| 2321 | 67 | `static void construction(Game g, KeptBench bench) throws Exception` | The construction page, with the city's sites on: the Sites tab kept, shown, reshaped by its tabs, a pick and the find box, and costed. |
| 2395 | 34 | `static void stopped(Game g, KeptBench bench) throws Exception` | Section 44: the pages a build is stopped on and the run's funding and fell-short pages, each caused where the model can cause it (a count Game.buildRunStop() refuses for the reason; a run larger than the cash) and eac... |
| 2437 | 207 | `static void buildControls(Game g, KeptBench bench) throws Exception` | Section 45: a control keeps the player's hand. |
| 2646 | 33 | `static void buildHover(Game g, KeptBench bench) throws Exception` | Section 46: the hover. |

### SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and (lines 2680-2789)

| line | len | member | says |
|---:|---:|---|---|
| 2706 | 1 | **type** `record SectorKinds(String plain, String materials)` | The businesses the Sectors sections take besides the shops, the landlords and Freight: a plain one with a plant standing, and a materials industry with works to draw. |
| 2713 | 11 | `static SectorKinds sectorKinds(Game g)` | The plain business this city has, by the model's own reading of its plants, and the materials industry its works draw: wood products, which ensureWorks() puts up a sawmill for (null when it has none standing). |
| 2735 | 6 | `static String ensureWorks(Game g) throws Exception` | Wood products' works standing in the city: the ones it has, else a sawmill put up through the model (finished at once; the city borrows what it lacks on Build's bond and buys ground where it finds none, as ensureSites... |
| 2743 | 18 | `static String putUpWorks(Game g) throws Exception` | A sawmill (or the first of WORKS_ORDER the model lets be built) put up finished: "a Sawmill put up through the model", or why none could be. |
| 2763 | 8 | `static void arrive(KeptBench bench, KeptBench.Page page) throws Exception` | A page arrived at: back on its home, drawn and laid out, as the bench's other pages are before their sections. |
| 2773 | 6 | `static Hand on(String key, String page, boolean statement)` | The hand that opens a business's page in a view. |
| 2781 | 3 | `static String called(Game g, String key)` | What a business is called on the screen. |
| 2786 | 3 | `static String possessive(String name)` | A name's possessive as the lines say it: "Retail's", "Wood Products'". |

### THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97 (lines 2790-3599)

| line | len | member | says |
|---:|---:|---|---|
| 2798 | 5 | `static int occurrences(String whole, String part)` | How many times `part` is in `whole`. |
| 2809 | 10 | `static void researchScreens(Game g, KeptBench played) throws Exception` | Sections 96 and 97: a sector's Investors page with its equipment, and the Build tab's NEW, each drawn kept over last month's nodes and built new in a city founded the 1900 way (ResearchViewCheck.researchTown(): the fi... |
| 2827 | 103 | `static void researchEquipment(Game g, KeptBench played) throws Exception` | Section 96: a sector's Investors page with its equipment, kept. |
| 2940 | 129 | `static void researchBuild(Game g, KeptBench played) throws Exception` | Section 97: a NEW build card, kept. |
| 3071 | 104 | `static void sectors(Game g, KeptBench bench) throws Exception` | Sections 47 to 54: the Sectors tab kept. |
| 3182 | 27 | `static void sectorsPictures(Game g, KeptBench bench, String works, String worksName) throws Exception` | Section 50: the pictures a business's page holds are kept. |
| 3216 | 74 | `static void picturesKept(Game g, KeptBench bench, KeptBench.Page page, String what, java.util.function.Function<KeptBench, List...` | What a month does to the pictures a page holds (`find` takes them off the page; `paints` for a picture that is told the month's view and paints it, as the refinery's and a materials industry's are, and not for a bar t... |
| 3292 | 10 | `static String againstNewAsIs(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands with no animation finished first, against the same page built new: the first difference, or null. |
| 3304 | 5 | `private static boolean figuresMoved(String[][] words, boolean business)` | Whether the figures the Sectors pages count have moved between last month and this: the list's KEPT, a business's, and some card's. |
| 3311 | 6 | `private static List<javafx.scene.control.Label> countingFigures(KeptBench bench, String key)` | The figures on a Sectors page that count up: the KEPT, and on the list every card's net income. |
| 3323 | 65 | `static String countRung(Game g, KeptBench bench, String key, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 51 on one page (`key` null for the list): the page is shown, a month lands and it is shown again; a count starts on each figure that moved exactly where `expect` says, the waterfall grows an... |
| 3395 | 65 | `static void sectorsCountUp(Game g, KeptBench bench, String plain) throws Exception` | Section 51: the count-up only at 1x-10x, on the list's KEPT figure and its cards', a business's KEPT, the Income page's waterfall and the Operations page's ring. |
| 3462 | 19 | `static Game seaTown() throws Exception` | The oil reserve's city: OilViewCheck's sea town (a platform with its wells, two land wells, a Strategic Reserve with FILL ordered), its fixture's report lines kept off this output. |
| 3488 | 24 | `static void sectorsControls() throws Exception` | Section 53: a control keeps the player's hand. |
| 3514 | 68 | `static void dial(Game sea, KeptBench bench, KeptBench.Page page, int which, String name, String key, java.util.function.DoubleF...` | One of the reserve's dials (the `which`th slider on the page), pressed, held across a month, released, and set outside a draw. |
| 3584 | 15 | `static void sectorsHover(Game g, KeptBench bench, String plain) throws Exception` | Section 54: the hover. |

### THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... (lines 3600-3837)

| line | len | member | says |
|---:|---:|---|---|
| 3630 | 3 | `private static boolean twoViews(String page)` | The Bank tab's pages that have two views, a Summary and a Statement. |
| 3635 | 6 | `static Hand bankOn(String page, boolean statement)` | The hand that opens a Bank page, in a view (the Overview when `page` is null). |
| 3650 | 88 | `static void bank(Game g, KeptBench bench) throws Exception` | Sections 55 to 57: the Bank tab kept. |
| 3743 | 14 | `static Hand changing(KeptBench.Page page, Hand hand)` | A hand that has to change the page (the premise of a line opened that adds no node: a statement line's detail is built hidden and shown): the page drawn before and after, and the choice refused as made if the two are ... |
| 3764 | 73 | `static void bankPicks(Game g, KeptBench bench) throws Exception` | Section 57's last part: a chip picked lands at the top of its page (BankScreen.toTop). |

### Finances (lines 3838-4074)

| line | len | member | says |
|---:|---:|---|---|
| 3852 | 3 | `static String pieceWords(Debt d)` | A piece of paper in words: its kind, as the book writes it, and its maturity. |
| 3861 | 19 | `static List<Debt> sellPaper(Game g, KeptBench bench) throws Exception` | The city's books put where the Finances screens have something to draw: one piece of every kind of paper sold through the model, as the Borrow page's own button sells it (executeDebtLogic, at the default highest yield... |
| 3882 | 6 | `static Hand financesOn(String area, String page)` | The hand that puts the Finances tab on an area and a page. |
| 3890 | 7 | `static Hand lineOf(Debt piece)` | The hand that opens a piece's own page, and the piece still on the books is its premise. |
| 3899 | 7 | `static Hand closeOf(Debt piece)` | The hand that opens Close it now on a piece (from its line's page when that is open), the same premise. |
| 3908 | 73 | `static void finances(Game g, KeptBench bench) throws Exception` | Sections 58 to 60: the Finances tab kept. |
| 3983 | 61 | `static List<Choice> borrowChoices(Game g, KeptBench bench) throws Exception` | Borrow's choices: the kinds, their terms, the ask, callable, the highest yield, what is typed, and abroad every money. |
| 4046 | 28 | `static void financesMenus(Game g, KeptBench bench) throws Exception` | Sections 60's last part: the foreign default menu and the debt result menu, each an arrival and a month later. |

### the city's fund (lines 4075-4249)

| line | len | member | says |
|---:|---:|---|---|
| 4078 | 6 | `static Hand fundOn(String page)` | The fund's pages by name (Portfolio, Search, Activity, Rules & cash). |
| 4086 | 6 | `static Hand securityOf(String key, String from)` | The hand that opens a security's page by its lot key, from a page of the fund. |
| 4094 | 6 | `static Hand ticket(boolean buy, boolean byAmount, double amount, String kind, int steps, boolean review)` | The hand that sets the ticket on the security open. |
| 4102 | 4 | `static String lotOf(List<String> from, String prefix, List<String> not, boolean rescue)` | The first lot key in `from` that starts with `prefix` and is not in `not` (and has no ":rescue" in it unless `rescue`), or null. |
| 4108 | 141 | `static void fund(Game g, KeptBench bench) throws Exception` | Sections 61 to 63: the city's fund kept. |

### the pictures (64) (lines 4250-4450)

| line | len | member | says |
|---:|---:|---|---|
| 4256 | 5 | **type** `record Spot(String label, KeptBench.Page page, Hand setup, MonthHand month)` | A place a picture is seen: its label, the page, the hand that sets the page for it after the page's home, and what the player does on the page in each month it is watched, after the month and before the draw (null: no... |
| 4257 | 3 | `Spot(String label, KeptBench.Page page, Hand setup)` _(in KeptCheck.Spot)_ |  |
| 4263 | 3 | **type** `interface MonthHand` | What the player does on a page in a month of the watch (the first month is 1). |
| 4264 | 1 | `void on(KeptBench bench, int month) throws Exception` _(in KeptCheck.MonthHand)_ |  |
| 4271 | 5 | `static Debt longestPiece(KeptBench bench) throws Exception` | The piece of paper on the books that falls due last: the one a line's page and Close it now are drawn on (null when the city has none). |
| 4278 | 42 | `static List<Spot> pictureSpots(Game g, KeptBench bench, Debt piece) throws Exception` | The places the money screens' pictures are: one page of each tab that holds some, and the pages behind them. |
| 4327 | 28 | `static void moneyPictures(Game g, KeptBench bench) throws Exception` | Section 64: the pictures the money screens hold are kept. |
| 4357 | 93 | `static void pictureKept(Game g, KeptBench bench, Spot spot, String kind) throws Exception` | One kind of picture on one page, watched for PICTURE_MONTHS months. |

### the count-up (65) (lines 4451-4676)

| line | len | member | says |
|---:|---:|---|---|
| 4454 | 10 | `static int[] started(KeptBench bench, java.util.Set<Animation> before)` | The animations a draw has started since `before` that are running now: {pops, counts and growths, others}. |
| 4466 | 5 | `static java.util.Set<Animation> animationsNow(KeptBench bench)` | The animations on the page now, as a set by identity. |
| 4473 | 5 | `static int running(KeptBench bench, Class<? extends Animation> kind)` | How many animations of a kind run on the page now. |
| 4480 | 7 | `static String stillOrNot(KeptBench bench)` | What is wrong with a page on which nothing may animate: a running animation, a figure not at rest. |
| 4493 | 29 | `static String bankRung(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 65 on the Bank tab (the Overview when `page` is null): the page is shown, a month lands (one that moved the capital ratio, on the Overview) and it is shown again; the PROFIT figure pops and ... |
| 4524 | 4 | `static void steerGauge(Game g)` | The Bank's risk-weighted ratio made the one that binds (see bankRung()): the book weighed for risk set to twenty times what the bank holds. |
| 4530 | 15 | `private static String bankRungChecks(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect, java.util.Se...` | ...what the month that landed started, once shown (the weighted book still steered on the Overview, so that the page built new beside it is drawn on the same bank). |
| 4547 | 41 | `static String fundRung(Game g, KeptBench bench, int at, boolean paused, boolean expect) throws Exception` | ...and the fund's Portfolio, through the tab's real show method: the worth counts on and a payment pops. |
| 4595 | 81 | `static void moneyCountUp(Game g, KeptBench bench) throws Exception` | Section 65: the count-up only at 1x-10x, on the Bank (the PROFIT figure's pop on the Overview and on a page's status strip, the capital gauge's growth) and the fund (the worth's count and a payment's pop). |

### the boxes (66) (lines 4677-4838)

| line | len | member | says |
|---:|---:|---|---|
| 4684 | 59 | `static void boxHeld(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.Su...` | One box on a page, given the focus and typed into (a node's `setFocused` when this scene has no window to give the focus: the line says which): a month landed through the real show method twice - the same node, its te... |
| 4750 | 52 | `static void boxEnter(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.S...` | Section 66's Enter (KP2b, for KP5's mend in CapChips): a yield box given the focus, "4.5" typed and Enter fired on it. |
| 4804 | 34 | `static void moneyControls(Game g, KeptBench bench) throws Exception` | Section 66: the boxes keep the player's hand - Close it now's two, the ask's, the yield boxes of Borrow's and the rollover's chips (typed into, a chip pressed, and Enter), the fund's search box. |

### the hover (67) and the cost (68) (lines 4839-4968)

| line | len | member | says |
|---:|---:|---|---|
| 4842 | 29 | `static void moneyHover(Game g, KeptBench bench) throws Exception` | Section 67: the hover. |
| 4873 | 20 | `static KeptBench.Page heaviest(KeptBench bench, String tab, List<KeptBench.Page> pages) throws Exception` | The page of a tab that weighs most (the nodes its first draw makes), by name: printed with the others. |
| 4895 | 45 | `static void moneyCost(Game g, KeptBench bench) throws Exception` | Section 68: what the kept month costs on the heaviest page of the Bank, of Finances and of the fund (and the fund's Activity and Search). |
| 4942 | 19 | `static void money(Game g, KeptBench bench) throws Exception` | Sections 55 to 68: the money screens kept. |
| 4963 | 5 | `private static long took(String what, long since)` | What a stretch of the money sections took, in a line; the clock's reading for the next. |

### GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept ... (lines 4969-5232)

| line | len | member | says |
|---:|---:|---|---|
| 5011 | 5 | `private static long tookWith(String what, long since, int checksSince)` | What a stretch of the KP6 sections took and how many assertions it made, in a line; the clock's reading for the next. |
| 5025 | 21 | `static double[] makeDeficit(Game g) throws Exception` | The taxes cut to nothing, the central bank printing for the treasury and two lines of arrears owed; what the dials were. |
| 5048 | 10 | `static void endDeficit(Game g, double[] was)` | The dials put back where makeDeficit() found them. |
| 5060 | 5 | `static int linesSaying(KeptBench bench, String marker)` | How many lines of the page's signature hold a marker (a card the page draws only in some states). |
| 5071 | 5 | `static List<Choice> asActing(List<Choice> choices)` | The choices made to act (see Choice.acts): for a page whose content is the model's ranking or the sign of its budget, where a month that moves a row in or out, or turns a surplus into a deficit, makes the nodes of the... |
| 5078 | 28 | `static List<Choice> governmentChoices(boolean debt)` | The Government tab's pages, folds and lines as choices (the folds that must add to their page only where the city has what they fold). |
| 5108 | 21 | `static int[] linesThatAdd(KeptBench bench, KeptBench.Page tab, String page) throws Exception` | How many of a Government page's lines add to it when opened into who pays: {added, tried}. |
| 5131 | 101 | `static void kp6Government(Game g, KeptBench bench) throws Exception` | Sections 69 to 71: the Government tab kept. |

### Trade (lines 5233-5546)

| line | len | member | says |
|---:|---:|---|---|
| 5236 | 6 | `static Hand tradeOn(String page)` | The hand that puts the Trade tab on a page. |
| 5244 | 6 | `static Hand tradeNationOn(int nation)` | The hand that puts the Trade tab on a nation's page behind Partners (0 is the table). |
| 5252 | 6 | `static Hand tradeFold(String key, boolean open)` | A fold or a line of the Trade tab opened or closed. |
| 5260 | 3 | **type** `interface Verify` | What a scenario of 74b checks of the warning's three texts (title, line, what is behind its (i)), null under the bound. |
| 5261 | 1 | `void on(String[] words)` _(in KeptCheck.Verify)_ |  |
| 5271 | 20 | `static void boundRun(Game g, KeptBench bench, KeptBench.Page tab, double annual, String name, ForeignAccounts.Regime regime, bo...` | One scenario of 74b (XI2): the regime is entered (a peg struck, a band centred, at the rate of the day), the treasury has dealt the pieces (shares of a year's output; the vault only, no month's cash), and the exchange... |
| 5298 | 73 | `static void kp6TradeBound(Game g, KeptBench bench, KeptBench.Page tab, String reservesName) throws Exception` | Section 74b (XI2): Trade's warning past the bound reads the month's dealing and not the deal in hand, and says what the regime does with it. |
| 5373 | 173 | `static void kp6Trade(Game g, KeptBench bench) throws Exception` | Sections 72 to 74: Trade & the World kept. |

### Policy (lines 5547-5797)

| line | len | member | says |
|---:|---:|---|---|
| 5550 | 6 | `static Hand policyOn(String area, String page)` | The hand that puts Policy on an area and a page (the hub when `area` is null). |
| 5558 | 3 | `static Choice policyPage(String area, String page)` | A choice that opens a page of Policy. |
| 5563 | 3 | `static Choice stage(String name, String key, double value)` | A choice that stages a change under a lever's key. |
| 5568 | 3 | `static Choice drop(String name)` | A choice that drops everything staged. |
| 5573 | 3 | `static Choice policyFold(String name, String key, boolean open, boolean adds)` | A fold or a line of Policy, opened or closed, as a choice (`opens` for a fold that has to add to its page). |
| 5578 | 6 | `static Choice policyAllClosed(String name)` | Every fold and line of Policy closed. |
| 5586 | 174 | `static void kp6Policy(Game g, KeptBench bench) throws Exception` | Sections 75 to 77: Policy and zoning kept. |
| 5766 | 31 | `static void peopleShort(Game g, KeptBench bench, String[] hunger) throws Exception` | People's WHO GOES SHORT list and card (sections 1 to 5 draw the page): with people short of food the card says so and draws its bar, the People page kept for a stretch of months equals a page built new, and Policy's F... |

### the controls (78) (lines 5798-6140)

| line | len | member | says |
|---:|---:|---|---|
| 5804 | 1 | **type** `record AtOnce(java.util.function.Supplier<Double> now, Runnable putBack)` | A dial that applies at once and stages nothing (the policy rate page's target and HOW STRICT): what the model reads at the dial now, and how it is put back (KP2b: the held dials were all staged ones). |
| 5807 | 4 | `static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> sliderAt(int index, double m...` | The slider at a place in a page's sliders, when it has these ends - else null (the page is not the page this expects). |
| 5813 | 7 | `static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> offsetDial(int index)` | The slider at a place among a page's sliders that run under nothing (a tax offset's dial, which reaches below zero), else null. |
| 5822 | 12 | `static String whoSet()` | The few frames of the code that made a change just heard by a listener (skipping this harness and the listener's own plumbing): who set it. |
| 5839 | 18 | `static String taxDialMoving(KeptBench bench, int index) throws Exception` | The first sector whose zone page has a tax dial (the `index`th that runs under nothing) that moves what the ITS TAXES card says it would do when it is dragged two steps, the dial put back after - or null. |
| 5859 | 7 | **type** `static final class Held` | What one held dial did, for holdDial to judge. |
| 5875 | 131 | `static void holdDial(Game g, KeptBench bench, String what, String area, String page, java.util.function.Function<List<javafx.sc...` | Section 78: a dial of Policy keeps the player's hand, through the screen's real show method. |
| 6008 | 3 | `static void policyDial(Game g, KeptBench bench, String what, String area, String page, String key, boolean trayShows) throws Ex...` | Section 78's dial, held, the first slider of a page: see holdDial. |
| 6018 | 61 | `static void heldWithCardInFront(Game g, KeptBench bench, String what, KeptBench.Page p, Hand setup, Hand front) throws Exception` | Section 78's last part: a card comes in front of a dial the player holds. |
| 6081 | 24 | `static void policyDialsKeep(Game g, KeptBench bench) throws Exception` | Section 78's last part: a card in front of a held dial, on the Schools page (a course's own price in front of the loan's) and on a tax page (a payer's own move in front of another's). |
| 6107 | 33 | `static void kp6Controls(Game g, KeptBench bench) throws Exception` | Section 78: the controls on Policy - a tax page's rate dial, the policy rate's, a zone's ground dial, and a card coming in front of a held dial. |

### the count-up (79) (lines 6141-6353)

| line | len | member | says |
|---:|---:|---|---|
| 6144 | 3 | `private static boolean moved(String[] words)` | Whether a counted figure's words moved between last month and this. |
| 6154 | 56 | `static String tradeRung(Game g, KeptBench bench, String page, boolean bars, int at, boolean paused, boolean expect) throws Exce...` | One clock speed of section 79 on a Trade page: the page is shown through its real show method, a month that moved SOLD ABROAD or BOUGHT ABROAD lands and it is shown again; each of the strip's two figures that moved st... |
| 6215 | 21 | `static String quietRung(Game g, KeptBench bench, KeptBench.Page page, Runnable open, Runnable show, int at, boolean paused, boo...` | One clock speed on a page that has no animation to run (a Government or Policy page): a month lands and the page is shown through its real show method; nothing starts at any rung, nothing is running or off its rest, a... |
| 6244 | 109 | `static void kp6CountUp(Game g, KeptBench bench) throws Exception` | Section 79: the count-up only at 1x-10x. |

### the hover (80) and the cost (81) (lines 6354-6522)

| line | len | member | says |
|---:|---:|---|---|
| 6357 | 14 | `private static void hoverKP6(Game g, KeptBench bench, java.util.Map<String, Boolean> found, String where, KeptBench.Page page, ...` | The pointer put over a node of each kind on a page (its `setup` made first), hoverLine() saying what came of it; `found` notes where a node was found. |
| 6378 | 30 | `static void kp6Hover(Game g, KeptBench bench) throws Exception` | Section 80: the hover. |
| 6414 | 55 | `static void kp6Tips(Game g, KeptBench bench) throws Exception` | Section 80's last part: the tooltips are kept. |
| 6471 | 18 | `static void kp6Cost(Game g, KeptBench bench) throws Exception` | Section 81: what the kept month costs on the Government Overview, on Trade's Overview and its heaviest page, and on Policy's hub and its dial-heavy pages. |
| 6491 | 31 | `static void screensKP6(Game g, KeptBench bench) throws Exception` | Sections 69 to 81: Government, Trade & the World and Policy kept. |

### POLICY > RESEARCH'S OVERVIEW AND FUNDING (R3b, slice B), sections 99 to 101: the two pages, the hub's Research card, ... (lines 6523-7084)

| line | len | member | says |
|---:|---:|---|---|
| 6533 | 6 | `static Game researchFundingTown() throws Exception` | The city of sections 99 to 101: ResearchViewCheck.fundedTown() - a 1900 city with two Universities, an Institute of technology and a Medical school, and grants in engineering, medicine and business that bought posts -... |
| 6541 | 12 | `static Game researchDefaultTown() throws Exception` | A city founded the default way with a University and a grant in engineering that bought posts (ResearchViewCheck's section 15 city), played three months. |
| 6556 | 8 | `static ResearchView.Card syntheticCard(boolean researching, int room, int inReach, double idle, double total, boolean grantMove...` | A Funding card made by hand: the read that ResearchScreen.sayWords() chooses a sentence from. |
| 6570 | 19 | `static void sayCases() throws Exception` | The sentence under a Funding card for every case the model's reads can make, in the order the screen chooses among them: a city founded the default way; no school; nothing in reach; a prize that brings labs; one under... |
| 6590 | 4 | `static String fieldWord(ResearchCatalog.Field f)` | A field as the screens read it: "Engineering". |
| 6596 | 3 | `static String researchersWord(double n)` | "412 researchers", "1 researcher". |
| 6601 | 10 | `static List<String> shownWith(KeptBench bench, KeptBench.Page page, Hand hand) throws Exception` | The page home, a hand on it, drawn and laid out: its signature. |
| 6616 | 25 | `static void monthsAgainstNew(Game town, KeptBench bench, KeptBench.Page page, String what, int months, Hand hand) throws Exception` | A page, after a hand on it, kept over last month's nodes for `months` months: the signature equal to a page built new at each, and nine in ten of its nodes handed back on average (the share section 96 holds a page to). |
| 6647 | 23 | `static void researchFunding(Game g, KeptBench defaultBench) throws Exception` | Sections 99 to 101: Policy > Research's Overview and Funding pages (R3b, slice B), in a city founded the default way (one quiet card; Funding says there is nothing to find) and in a city founded the 1900 way with scho... |
| 6672 | 99 | `static void researchDefaultWay(Game g, KeptBench defaultBench) throws Exception` | Section 99, the first part: in a city founded the default way. |
| 6773 | 81 | `static void researchOverview(Game town, KeptBench bench) throws Exception` | Section 99, the second part: the Overview and the hub's Research card in the 1900 town. |
| 6856 | 156 | `static void researchFundingPage(Game town, KeptBench bench) throws Exception` | Section 100: the Funding page in the 1900 town. |
| 7014 | 70 | `static void researchSchoolsAndBooks(Game town, KeptBench bench) throws Exception` | Section 101: the schools' research posts on Services, the research line on Government's Spending list, and the wage floor's payroll. |

### THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of ... (lines 7085-7404)

| line | len | member | says |
|---:|---:|---|---|
| 7090 | 8 | `static boolean has(List<String> signature, String...words)` | Whether some line of a signature has all of these words in it. |
| 7100 | 3 | `static String label(String text)` | A label's line in a signature: `Label "text"`. |
| 7112 | 194 | `static void researchFound(Game g, KeptBench defaultBench) throws Exception` | Section 95: Policy > Research > Found, the sixth Policy area's page, kept (R5, slice B1). |
| 7308 | 1 | **type** `private record RailScreen(String label, String name, java.util.function.Consumer<KeptBench> show)` | A rail screen the clock is held to in section 88: how it is called here, the name its show method gives the window, and the call that shows it. |
| 7321 | 83 | `static void kp7Clock(Game g, KeptBench bench) throws Exception` | Section 88: the clock never rebuilds a rail screen. |

### HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page draw... (lines 7405-7708)

| line | len | member | says |
|---:|---:|---|---|
| 7428 | 57 | `static void kp7Household(Game g, KeptBench bench) throws Exception` | Sections 82 to 84: Household money kept, shown as a page built new shows it, and kept right through what reshapes it. |
| 7487 | 81 | `static void kp7Rankings(Game g, KeptBench bench) throws Exception` | Sections 85 to 87: the Rankings tab kept, shown as a page built new shows it, and kept right through what reshapes it - to the pixel, since its table is painted. |
| 7570 | 24 | `static int[] tipsAcrossMonth(Game g, KeptBench bench, KeptBench.Page page) throws Exception` | The tooltips on a page across a month's kept redraw (setup made first): {tooltips before, on a node that has one after, the same Tooltip, on a node kept without one}. |
| 7602 | 25 | `static void kp7Hover(Game g, KeptBench bench) throws Exception` | Section 89: the hover and the tooltips. |
| 7629 | 31 | `static void kp7Tips(Game g, KeptBench bench) throws Exception` | Section 89's last part: the tooltips are kept on Household money and the Rankings tab. |
| 7665 | 21 | `static void kp7Cost(Game g, KeptBench bench) throws Exception` | Section 90: what the kept month costs on Household money - as it opens and with the first family cell's books open - and on the Rankings tab - as it opens and with every measure unfolded - against the same month built... |
| 7688 | 20 | `static void screensKP7(Game g, KeptBench bench) throws Exception` | Sections 82 to 90: Household money and the Rankings tab kept, and the clock never rebuilding a rail screen (88). |

### a slider greyed while held (91) (lines 7709-7831)

| line | len | member | says |
|---:|---:|---|---|
| 7712 | 5 | **type** `static final class Greyed` | What one greying of a held slider did, for greyedWhileHeld to judge. |
| 7725 | 60 | `static void greyedWhileHeld(Game g, KeptBench bench) throws Exception` | Section 91 (KP2b): a slider greyed while the player holds it lets go. |
| 7791 | 33 | `static void doorsStyledOnce(Game g, KeptBench bench) throws Exception` | Section 91's door cards (KP2b): a door card's hand style is set once a draw, whole. |
| 7826 | 5 | `private static Notice add(Game g, String key, String title)` | A notice raised now, unread, in the inbox. |

### THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node... (lines 7832-8182)

| line | len | member | says |
|---:|---:|---|---|
| 7852 | 21 | `static void nothingSet(KeptBench bench, KeptBench.Page page, String name, Hand hand) throws Exception` | A page home, a hand on it, drawn and laid out, drawn once more with a listener on every style, text, graphic, padding and width on it (KeptBench.redrawSets), no month between: how many of them the second draw wrote, a... |
| 7874 | 3 | `static void nothingSet(KeptBench bench, KeptBench.Page page, String name) throws Exception` |  |
| 7882 | 12 | `static List<Debt> piecesOnTheBooks(Game g, KeptBench bench) throws Exception` | The pieces of paper on the city's books; where there are none (the months the sections before this played let every maturity pass) one sold through the model, as the Borrow page's own button sells it, the first of a T... |
| 7899 | 7 | `static void letGo(KeptBench bench, java.util.function.Consumer<KeptBench> forget) throws Exception` | Section 92 lets one tab's pages go before it opens the next (KP2b): the tab forgotten, as another city opened leaves it, and the pages built new released (KeptBench.release). |
| 7908 | 127 | `static void kp2bSets(Game g, KeptBench bench) throws Exception` | Section 92: the pages the sweep mended, each redrawn with nothing new in the city. |
| 8037 | 34 | `static void kp2bHouseholdMoney(Game g, KeptBench bench) throws Exception` | Section 92 on Household money (KP7, taken into KP2b): the page as it opens, each view, every cell of the grid opened, the statement and the books stacked under the grid. |
| 8076 | 16 | `static int rankingsSavedCities(KeptBench bench) throws Exception` | The city saved into slot 10 of the harness's own folder and read back, so that the Rankings tab has a saved city on its board: section 87 does it, and does nothing here where it has run. |
| 8099 | 24 | `static void kp2bRowsPicture(Game g, KeptBench bench) throws Exception` | Section 93: the Rankings table's picture (KP2b), the one node on the page that paints its rows. |
| 8129 | 53 | `static void kp2bRankings(Game g, KeptBench bench) throws Exception` | Section 92 on the Rankings tab (KP7, taken into KP2b): the page as it opens, each board, money, prices and conversion, some measures (every tenth, the last and the first no real place is ranked on), each filter, Aroun... |

### THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7): sections 94 and 98 (lines 8183-8862)

| line | len | member | says |
|---:|---:|---|---|
| 8195 | 4 | `static void putSitesOn(BuildingManager buildings, int n)` | Puts the played city's building stacks in a state with this many sites on (the stacks' own setter, the one a loaded save uses): the first n stacks under construction, the rest not, so the tab's count and its tooltip a... |
| 8201 | 4 | `static boolean cityIsScratch(Game g)` | Whether the scratch city's folder is under this JVM's TempDirs folder: then a click that saves the prefs writes nothing of the player's. |
| 8212 | 220 | `static void up7Frame(Game g, KeptBench bench) throws Exception` | Section 94: the drawer's pinned bar and the construction tab, two boxes made once and drawn kept (UP7). |
| 8434 | 17 | `static ClockPace paceFor(double rung, boolean short_)` | A pace on a clock moved by hand, run past a full window: at pace, or short of the rung (a rung of 50 by months of 120 ms as ClockCheck's falls, the rest by a city that runs at two fifths of it). |
| 8453 | 4 | `static void drawHeader(KeptBench bench) throws Exception` | The header drawn kept and laid out, as the clock's redraw leaves it. |
| 8463 | 183 | `static void up7Speed(Game g, KeptBench bench) throws Exception` | Section 98: the speed's reading (UP7). |
| 8648 | 9 | `static boolean onceEach(KeptBench.Sets s)` | Whether a draw wrote the reading's words once and its style once, and nothing else. |
| 8658 | 204 | `public static void main(String[] args) throws Exception` |  |


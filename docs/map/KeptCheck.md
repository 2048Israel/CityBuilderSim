# KeptCheck.java - 7,652 lines · 177 methods · 41 constants · harnesses

`ham/citybuildersim/KeptCheck.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

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
> ... (220 more lines in the source)

**Uses:** [KeptBench](KeptBench.md) (573), [Game](Game.md) (125), [Sectors](Sectors.md) (51), [ResearchView](ResearchView.md) (45), [Debt](Debt.md) (20), [Where](Where.md) (19), [DecisionLog](DecisionLog.md) (19), [BuildAdvice](BuildAdvice.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (16), [Notice](Notice.md) (12), [ChartModel](ChartModel.md) (11), [Timing](Timing.md) (10), [ResearchViewCheck](ResearchViewCheck.md) (9), [CityCalendar](CityCalendar.md) (8), [Auction](Auction.md) (7), [WellCheck](WellCheck.md) (6), [OilCheck](OilCheck.md) (6), [ForeignAccounts](ForeignAccounts.md) (6), [Research](Research.md) (4), [RefineryViewCheck](RefineryViewCheck.md) (4), [OilViewCheck](OilViewCheck.md) (4), [WageBand](WageBand.md) (4), [DebtManager](DebtManager.md) (4), [BuildingManager](BuildingManager.md) (3), [IndustryView](IndustryView.md) (3), [RefineryView](RefineryView.md) (3), [MiningCheck](MiningCheck.md) (3), [TreasuryLine](TreasuryLine.md) (3), [Good](Good.md) (3), [PolicyDates](PolicyDates.md) (3)... and 14 more

**Used by (1):** [ChartFitCheck](ChartFitCheck.md)

## Sections

| line | section |
|---:|---|
| 1444 | INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page |
| 2053 | BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, |
| 2642 | SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and |
| 2752 | THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97 |
| 3562 | THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... |
| 3800 | · Finances |
| 4028 | · the city's fund |
| 4203 | · the pictures (64) |
| 4404 | · the count-up (65) |
| 4630 | · the boxes (66) |
| 4792 | · the hover (67) and the cost (68) |
| 4922 | GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept ... |
| 5186 | · Trade |
| 5386 | · Policy |
| 5637 | · the controls (78) |
| 5980 | · the count-up (79) |
| 6193 | · the hover (80) and the cost (81) |
| 6362 | THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of ... |
| 6682 | HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page draw... |
| 6986 | · a slider greyed while held (91) |
| 7109 | THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node... |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 311 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 314 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 317 | `KeptCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 320 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 415 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's cached outline a le... |
| 418 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 461 | `KeptCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 464 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 686 | `KeptCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |
| 956 | `KeptCheck.KEPT_HISTORY_MS` | `13` | The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same mon... |
| 986 | `KeptCheck.TICKED` | `"unemployment"` | The first of these lines picked and not in the preset the page opens on: a line to tick and untick. |
| 1450 | `KeptCheck.HANDED_BACK` | `0.9` | The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). |
| 1707 | `KeptCheck.KEPT_INFRASTRUCTURE_MS` | `9` | The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 betwe... |
| 1714 | `KeptCheck.KEPT_SERVICES_MS` | `13` | The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 betwe... |
| 1721 | `KeptCheck.KEPT_LAND_MS` | `5` | The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 ... |
| 1761 | `KeptCheck.SERVICES_FOLDS` | `{ { "Health/Overview", "health:sick" }, { "Health/General care", "health:gene...` | The folds each sub-page of Services has, by "system/page" (a page not named has none). |
| 2064 | `KeptCheck.KEPT_BUILD_MS` | `12` | The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a city category with two orders dialled up and a card's stats pinned (the heavier of ... |
| 2071 | `KeptCheck.KEPT_CONSTRUCTION_MS` | `4` | The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1... |
| 2074 | `KeptCheck.SITE_ROWS` | `6` | The rows the Sites tab and the timeline are kept at while Build and the construction page are watched: a site each. |
| 2077 | `KeptCheck.SITE_ORDER` | `{ "Gravel Road", "Walk-in Clinic", "Paved Road", "Bus Network", "Elementary S...` | What is ordered, in turn, to put a site on: the cheap, quick works first. |
| 2653 | `KeptCheck.KEPT_SECTORS_MS` | `18` | The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same mon... |
| 2662 | `KeptCheck.KEPT_SECTOR_MS` | `15` | The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the heaviest of the three pages costed (the shops' Operations; a plain business's Income stat... |
| 2665 | `KeptCheck.PICTURE_MONTHS` | `6` | The months the pictures are watched through (section 50): each month a repaint or not, and a page built new beside it. |
| 2671 | `KeptCheck.NOT_PLAIN` | `java.util.Set.of(Sectors.RETAIL, Sectors.RESTAURANTS, Sectors.LUXURY_RETAIL, ...` | The keys a plain business is not taken from: the shops, the kitchens, the counters, the landlords, the haulier and the fuel's two. |
| 2688 | `KeptCheck.WORKS_ORDER` | `{ "Sawmill", "Scrap Yard", "Wood Pellet Mill", "Panel Mill" }` | What a materials industry's works are made of, in turn, for a city that has none standing: a sawmill first, the rest if it cannot. |
| 2757 | `KeptCheck.R5_MONTHS` | `6` | The months each of the research sections plays on its page, kept over last month's nodes against a page built new. |
| 3573 | `KeptCheck.KEPT_BANK_MS` | `12` | The budget for the kept Bank month - its draw, then the pulse's CSS and layout - on the Bank's heaviest page (the Overview), on the PC: twice the kept month's median, rounded up (5.2 to 8.3 ms in five runs of the mone... |
| 3580 | `KeptCheck.KEPT_FINANCES_MS` | `18` | The budget for the kept Finances month on the heaviest of the two pages costed (Borrow at home, the page the auction draws, and the page with the most nodes), on the PC: twice the kept month's median, rounded up (the ... |
| 3589 | `KeptCheck.KEPT_FUND_MS` | `321` | The budget for the kept month on the fund's heaviest page (Search and Activity, the two costed, weigh most; their size follows the city's age: the bonds it has issued are the rows of Search, its months of events the r... |
| 3605 | `KeptCheck.BANK_FOLDS` | `{ { "Profit", "profit:statement", "Interest earned", "Fees", "Provisions for ...` | What the Bank's folds hold, by page: the fold's key, then the lines a player opens inside it (each a key the page's own bookLine opens by). |
| 3803 | `KeptCheck.PAPER` | `{ { "Note", "12", "" }, { "Float", "5", "" }, { "Serial", "10", "" }, { "Term...` | The paper the money sections sell, one of each kind the Borrow page offers (and a callable bond too): {kind, term, "callable" or ""}. |
| 4221 | `KeptCheck.MOST_TO_PAY` | `{ "98.5", "101.5" }` | The most to pay typed on Close it now in the odd months of the watch, by turns (Spot's month hand). |
| 4933 | `KeptCheck.KEPT_GOVERNMENT_MS` | `7` | The budget for the kept Government month - its draw, then the pulse's CSS and layout - on the Overview, at month 3,500 of a played city (about where KeptCheck is when it comes to the Government tab), on the PC: twice ... |
| 4941 | `KeptCheck.KEPT_TRADE_MS` | `10` | The budget for the kept Trade month - its draw, then the pulse's CSS and layout - on the Overview and on the heaviest of its pages (the reserves), the larger of the two being the figure, at month 3,500 of a played cit... |
| 4949 | `KeptCheck.KEPT_POLICY_MS` | `10` | The budget for the kept Policy month - its draw, then the pulse's CSS and layout - on the hub and on the pages heavy with dials (Taxes' Profit page and the policy rate's), the largest of them being the figure, at mont... |
| 4958 | `KeptCheck.KP6_HANDED_BACK` | `.75` | The share of a KP6 page's nodes a month's redraw has to hand back after a choice, at least: three in four. |
| 4971 | `KeptCheck.TRADE_FOLDS_THAT_ADD` | `java.util.Set.of("month:ledger", "reserves:eight")` | The folds the Trade sections open that have to add to their page in any city that trades: the month's ledger and the eight moneys over the years. |
| 6588 | `KeptCheck.CLOCK_MONTHS` | `3` | The months landed, each followed by the clock's redraw, on every screen in section 88. |
| 6694 | `KeptCheck.KEPT_HOUSEHOLD_MS` | `8` | The budget for the kept Household money month - its draw, then the pulse's CSS and layout - on the page as it opens and with the first family cell's books open (the larger of the two being the figure), at month 3,400 ... |
| 6702 | `KeptCheck.KEPT_RANKINGS_MS` | `7` | The budget for the kept Rankings month - its draw, then the pulse's CSS and layout - on the tab as it opens and with every measure unfolded (Browse all: a line a measure; the larger of the two being the figure), at mo... |
| 7119 | `KeptCheck.NOT_KP2B` | `java.util.Set.of("UserInterface.java", "InfrastructureScreen.java", "Services...` | The files of the screens KP2b's sweep did not reach, whose writes section 92 names and does not count: the sweep was the list of screens Pieces, the statements, Sectors, Land, the Bank, the fund, Finances, Trade, Zoni... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 302 | `static int fails` |  |
| 344 | `static Runnable afterMonth` | What is done after every month played, with the log off, while a fixture has to be held through the months (KP3: the city's sites); null for nothing. |
| 2080 | `private static int siteTurn` |  |
| 4961 | `static int checks` | The assertions made so far, for the count a stretch of the KP6 sections reports. |
| 5699 | `String none, reading0, dragged, kept, differs, dropped, firstHeard` |  |
| 5700 | `boolean sameReading, sameBar, heldPressed, heldStill, heldAfter, same, tray, trayGone, stagedMid, saysDragg...` |  |
| 5701 | `int heardText, heardStyle, control, arrowsBefore, arrowsDragged, arrowsMid` |  |
| 5702 | `double shown, valueNow, thumb, cityAt, cityMid, cityAfter, cityBack, step, min` |  |
| 5703 | `Double stagedAt` |  |
| 6990 | `String none, differs, dropped, reading` |  |
| 6991 | `boolean heldPressed, heldGreyed, changingGreyed, sameBar, heldAfter` |  |
| 6992 | `double value, want, back, city` |  |
| 7122 | `static int ignoredSets` | The writes from NOT_KP2B's files, over the pages of section 92 (printed at its end). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 300 | 7353 | **type** `public class KeptCheck` | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exactly where a page built new would, and costs a fraction of it. |
| 304 | 5 | `static void check(String label, boolean ok)` |  |
| 322 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 329 | 13 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on). |
| 347 | 10 | `static void month(Game g)` | A month, as the clock runs it, its log kept off this harness's output. |
| 359 | 9 | `static<T> T quietly(Callable<T> call) throws Exception` | A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). |
| 370 | 28 | `static String firstDifference(List<String> kept, List<String> fresh)` | The first line where two signatures part, or null when they are the same. |
| 400 | 6 | `static void png(int[] px, java.nio.file.Path to) throws java.io.IOException` | A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. |
| 421 | 10 | `static boolean alike(int[] a, int[] b)` | Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. |
| 433 | 16 | `static String pixelDifference(int[] a, int[] b)` | Where two snapshots part: their sizes, or how many pixels differ and the box round them. |
| 450 | 3 | `static String clip(String s)` |  |
| 454 | 5 | `static double median(List<Double> v)` |  |
| 472 | 212 | `static void chrome(Game g, KeptBench bench) throws Exception` | Sections 6 to 10: the header and the tab rail kept (UP2). |
| 689 | 11 | `static void panelStates(KeptBench bench, int at)` | The panels' states, month by month: the city panel's mode, and which of its sections are open. |
| 705 | 245 | `static void panels(Game g, KeptBench bench) throws Exception` | Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice 2b), each on a scene of its own, beside the header and the rail of 6 to 10. |
| 960 | 3 | **type** `interface Hand` | A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). |
| 961 | 1 | `String on(KeptBench bench) throws Exception` _(in KeptCheck.Hand)_ |  |
| 970 | 9 | **type** `record Choice(String name, Hand hand, boolean opens, boolean acts)` | One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2); `acts` for a choice that makes the model act (KP3: automatic building turned on or... |
| 971 | 3 | `Choice(String name, Hand hand, boolean opens)` _(in KeptCheck.Choice)_ |  |
| 975 | 3 | `Choice(String name, Hand hand)` _(in KeptCheck.Choice)_ |  |
| 981 | 3 | `static Choice acting(String name, Hand hand)` | A choice that makes the model act (see Choice.acts). |
| 992 | 218 | `static void history(Game g, KeptBench bench) throws Exception` | Sections 14 to 20: City History kept (KP1). |
| 1212 | 7 | `private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved)` | Months landed until the figure has moved (at most twelve): whether it did. |
| 1221 | 3 | `private static String nullIfOffered(String refusal, String what)` | The choice's refusal, as the premise it could not meet, or null when the chart is offered. |
| 1231 | 77 | `static void crosshair(Game g, KeptBench bench) throws Exception` | Section 18: what a page built new loses and a kept one keeps. |
| 1315 | 96 | `static void countUp(Game g, KeptBench bench) throws Exception` | Section 19: the count-up is for a clock that can be followed. |
| 1413 | 30 | `static void historyCost(Game g, KeptBench bench) throws Exception` | Section 20: what the kept City History month costs, against the same month built new. |

### INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page (lines 1444-2052)

| line | len | member | says |
|---:|---:|---|---|
| 1453 | 3 | `static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. |
| 1463 | 16 | `static String againstNew(KeptBench bench, KeptBench.Page page, boolean pixels) throws Exception` | ...and, for a page with pictures painted on canvases, which no signature holds (KP7, the Rankings table), the canvases' pixels too: the signature's difference first, else the canvases' (no pixel more than SHADE_LEVELS... |
| 1481 | 3 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the page is kept - a month's redraw keeps its top nodes, hands back nine in ten of the first draw's nodes, and on most months makes none. |
| 1490 | 37 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n, boolean eventful) throws Exception` | ...for a page whose content is the model's events (`eventful`, KP5: the city's fund, where a bond repaid, a payment and a quote line are rows of their own): the nodes a month makes are the month's new rows, so most mo... |
| 1529 | 71 | `static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. |
| 1606 | 3 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception` | Section n: every choice that reshapes the page, made in turn: the redraw after it and the one after a month lands are each equal to a page built new, and the month's redraw hands back nine in ten of the page's nodes. |
| 1611 | 3 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, double share) throws Exception` | ...with the share of the page a month's redraw has to hand back set by the caller (KP6: KP6_HANDED_BACK). |
| 1616 | 45 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, double share, boolean pixel...` | ...and, with `pixels`, each redraw's picture against the page built new's as well (KP7: the Rankings table is painted, and no signature holds it). |
| 1663 | 3 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception` | Section n: what the kept month on the page's home costs, against the same month built new. |
| 1668 | 28 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial, int samples) throws Excep...` | ...the median of `samples` months, for the pages whose month is long (the fund's, tens of thousands of nodes). |
| 1698 | 3 | `static Choice choice(String name, Hand hand)` | A choice with no side effect on the model: a hand that changes the screen's state only. |
| 1724 | 9 | `static String ensureTransit(Game g) throws Exception` | Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. |
| 1735 | 24 | `static void infrastructure(Game g, KeptBench bench) throws Exception` | Sections 21 to 24: Infrastructure kept. |
| 1773 | 21 | `static void services(Game g, KeptBench bench) throws Exception` | Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. |
| 1796 | 49 | `static void land(Game g, KeptBench bench) throws Exception` | Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. |
| 1852 | 123 | `static void controls(Game g, KeptBench bench) throws Exception` | Section 34: a control keeps the player's hand. |
| 1977 | 64 | `static void hover(Game g, KeptBench bench) throws Exception` | Section 35: the hover kept. |
| 2043 | 9 | `private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result)` | One hover result: printed, counted as found when the page had the node, and a check that it held. |

### BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, (lines 2053-2641)

| line | len | member | says |
|---:|---:|---|---|
| 2083 | 12 | `static boolean buyGround(Game g, BuildingsTemplate t, int n) throws Exception` | Ground bought through the model for an order that found none: the cheapest offer that covers the shortfall, the cash it lacks borrowed on Build's bond; whether it was bought. |
| 2097 | 30 | `static String ensureSites(Game g, int rows) throws Exception` | Orders through the model until the city has `rows` sites on (a row each on the Sites tab, a bar each on the timeline): what it did, in a line. |
| 2129 | 9 | `static void keepSites(Game g)` | From now every month played tops the sites up to SITE_ROWS (the fixture holds while the pages are watched), until stopSites(). |
| 2139 | 3 | `static void stopSites()` |  |
| 2144 | 137 | `static void build(Game g, KeptBench bench) throws Exception` | Build's pages: the Overview and a city category with two orders dialled up (so that its order bar shows), kept, as built new, reshaped and costed. |
| 2283 | 67 | `static void construction(Game g, KeptBench bench) throws Exception` | The construction page, with the city's sites on: the Sites tab kept, shown, reshaped by its tabs, a pick and the find box, and costed. |
| 2357 | 34 | `static void stopped(Game g, KeptBench bench) throws Exception` | Section 44: the pages a build is stopped on and the run's funding and fell-short pages, each caused where the model can cause it (a count Game.buildRunStop() refuses for the reason; a run larger than the cash) and eac... |
| 2399 | 207 | `static void buildControls(Game g, KeptBench bench) throws Exception` | Section 45: a control keeps the player's hand. |
| 2608 | 33 | `static void buildHover(Game g, KeptBench bench) throws Exception` | Section 46: the hover. |

### SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and (lines 2642-2751)

| line | len | member | says |
|---:|---:|---|---|
| 2668 | 1 | **type** `record SectorKinds(String plain, String materials)` | The businesses the Sectors sections take besides the shops, the landlords and Freight: a plain one with a plant standing, and a materials industry with works to draw. |
| 2675 | 11 | `static SectorKinds sectorKinds(Game g)` | The plain business this city has, by the model's own reading of its plants, and the materials industry its works draw: wood products, which ensureWorks() puts up a sawmill for (null when it has none standing). |
| 2697 | 6 | `static String ensureWorks(Game g) throws Exception` | Wood products' works standing in the city: the ones it has, else a sawmill put up through the model (finished at once; the city borrows what it lacks on Build's bond and buys ground where it finds none, as ensureSites... |
| 2705 | 18 | `static String putUpWorks(Game g) throws Exception` | A sawmill (or the first of WORKS_ORDER the model lets be built) put up finished: "a Sawmill put up through the model", or why none could be. |
| 2725 | 8 | `static void arrive(KeptBench bench, KeptBench.Page page) throws Exception` | A page arrived at: back on its home, drawn and laid out, as the bench's other pages are before their sections. |
| 2735 | 6 | `static Hand on(String key, String page, boolean statement)` | The hand that opens a business's page in a view. |
| 2743 | 3 | `static String called(Game g, String key)` | What a business is called on the screen. |
| 2748 | 3 | `static String possessive(String name)` | A name's possessive as the lines say it: "Retail's", "Wood Products'". |

### THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97 (lines 2752-3561)

| line | len | member | says |
|---:|---:|---|---|
| 2760 | 5 | `static int occurrences(String whole, String part)` | How many times `part` is in `whole`. |
| 2771 | 10 | `static void researchScreens(Game g, KeptBench played) throws Exception` | Sections 96 and 97: a sector's Investors page with its equipment, and the Build tab's NEW, each drawn kept over last month's nodes and built new in a city founded the 1900 way (ResearchViewCheck.researchTown(): the fi... |
| 2789 | 103 | `static void researchEquipment(Game g, KeptBench played) throws Exception` | Section 96: a sector's Investors page with its equipment, kept. |
| 2902 | 129 | `static void researchBuild(Game g, KeptBench played) throws Exception` | Section 97: a NEW build card, kept. |
| 3033 | 104 | `static void sectors(Game g, KeptBench bench) throws Exception` | Sections 47 to 54: the Sectors tab kept. |
| 3144 | 27 | `static void sectorsPictures(Game g, KeptBench bench, String works, String worksName) throws Exception` | Section 50: the pictures a business's page holds are kept. |
| 3178 | 74 | `static void picturesKept(Game g, KeptBench bench, KeptBench.Page page, String what, java.util.function.Function<KeptBench, List...` | What a month does to the pictures a page holds (`find` takes them off the page; `paints` for a picture that is told the month's view and paints it, as the refinery's and a materials industry's are, and not for a bar t... |
| 3254 | 10 | `static String againstNewAsIs(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands with no animation finished first, against the same page built new: the first difference, or null. |
| 3266 | 5 | `private static boolean figuresMoved(String[][] words, boolean business)` | Whether the figures the Sectors pages count have moved between last month and this: the list's KEPT, a business's, and some card's. |
| 3273 | 6 | `private static List<javafx.scene.control.Label> countingFigures(KeptBench bench, String key)` | The figures on a Sectors page that count up: the KEPT, and on the list every card's net income. |
| 3285 | 65 | `static String countRung(Game g, KeptBench bench, String key, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 51 on one page (`key` null for the list): the page is shown, a month lands and it is shown again; a count starts on each figure that moved exactly where `expect` says, the waterfall grows an... |
| 3357 | 65 | `static void sectorsCountUp(Game g, KeptBench bench, String plain) throws Exception` | Section 51: the count-up only at 1x-10x, on the list's KEPT figure and its cards', a business's KEPT, the Income page's waterfall and the Operations page's ring. |
| 3424 | 19 | `static Game seaTown() throws Exception` | The oil reserve's city: OilViewCheck's sea town (a platform with its wells, two land wells, a Strategic Reserve with FILL ordered), its fixture's report lines kept off this output. |
| 3450 | 24 | `static void sectorsControls() throws Exception` | Section 53: a control keeps the player's hand. |
| 3476 | 68 | `static void dial(Game sea, KeptBench bench, KeptBench.Page page, int which, String name, String key, java.util.function.DoubleF...` | One of the reserve's dials (the `which`th slider on the page), pressed, held across a month, released, and set outside a draw. |
| 3546 | 15 | `static void sectorsHover(Game g, KeptBench bench, String plain) throws Exception` | Section 54: the hover. |

### THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... (lines 3562-3799)

| line | len | member | says |
|---:|---:|---|---|
| 3592 | 3 | `private static boolean twoViews(String page)` | The Bank tab's pages that have two views, a Summary and a Statement. |
| 3597 | 6 | `static Hand bankOn(String page, boolean statement)` | The hand that opens a Bank page, in a view (the Overview when `page` is null). |
| 3612 | 88 | `static void bank(Game g, KeptBench bench) throws Exception` | Sections 55 to 57: the Bank tab kept. |
| 3705 | 14 | `static Hand changing(KeptBench.Page page, Hand hand)` | A hand that has to change the page (the premise of a line opened that adds no node: a statement line's detail is built hidden and shown): the page drawn before and after, and the choice refused as made if the two are ... |
| 3726 | 73 | `static void bankPicks(Game g, KeptBench bench) throws Exception` | Section 57's last part: a chip picked lands at the top of its page (BankScreen.toTop). |

### Finances (lines 3800-4027)

| line | len | member | says |
|---:|---:|---|---|
| 3806 | 3 | `static String pieceWords(Debt d)` | A piece of paper in words: its kind, as the book writes it, and its maturity. |
| 3815 | 18 | `static List<Debt> sellPaper(Game g, KeptBench bench) throws Exception` | The city's books put where the Finances screens have something to draw: one piece of every kind of paper sold through the model, as the Borrow page's own button sells it (executeDebtLogic, at the default highest yield... |
| 3835 | 6 | `static Hand financesOn(String area, String page)` | The hand that puts the Finances tab on an area and a page. |
| 3843 | 7 | `static Hand lineOf(Debt piece)` | The hand that opens a piece's own page, and the piece still on the books is its premise. |
| 3852 | 7 | `static Hand closeOf(Debt piece)` | The hand that opens Close it now on a piece (from its line's page when that is open), the same premise. |
| 3861 | 73 | `static void finances(Game g, KeptBench bench) throws Exception` | Sections 58 to 60: the Finances tab kept. |
| 3936 | 61 | `static List<Choice> borrowChoices(Game g, KeptBench bench) throws Exception` | Borrow's choices: the kinds, their terms, the ask, callable, the highest yield, what is typed, and abroad every money. |
| 3999 | 28 | `static void financesMenus(Game g, KeptBench bench) throws Exception` | Sections 60's last part: the foreign default menu and the debt result menu, each an arrival and a month later. |

### the city's fund (lines 4028-4202)

| line | len | member | says |
|---:|---:|---|---|
| 4031 | 6 | `static Hand fundOn(String page)` | The fund's pages by name (Portfolio, Search, Activity, Rules & cash). |
| 4039 | 6 | `static Hand securityOf(String key, String from)` | The hand that opens a security's page by its lot key, from a page of the fund. |
| 4047 | 6 | `static Hand ticket(boolean buy, boolean byAmount, double amount, String kind, int steps, boolean review)` | The hand that sets the ticket on the security open. |
| 4055 | 4 | `static String lotOf(List<String> from, String prefix, List<String> not, boolean rescue)` | The first lot key in `from` that starts with `prefix` and is not in `not` (and has no ":rescue" in it unless `rescue`), or null. |
| 4061 | 141 | `static void fund(Game g, KeptBench bench) throws Exception` | Sections 61 to 63: the city's fund kept. |

### the pictures (64) (lines 4203-4403)

| line | len | member | says |
|---:|---:|---|---|
| 4209 | 5 | **type** `record Spot(String label, KeptBench.Page page, Hand setup, MonthHand month)` | A place a picture is seen: its label, the page, the hand that sets the page for it after the page's home, and what the player does on the page in each month it is watched, after the month and before the draw (null: no... |
| 4210 | 3 | `Spot(String label, KeptBench.Page page, Hand setup)` _(in KeptCheck.Spot)_ |  |
| 4216 | 3 | **type** `interface MonthHand` | What the player does on a page in a month of the watch (the first month is 1). |
| 4217 | 1 | `void on(KeptBench bench, int month) throws Exception` _(in KeptCheck.MonthHand)_ |  |
| 4224 | 5 | `static Debt longestPiece(KeptBench bench) throws Exception` | The piece of paper on the books that falls due last: the one a line's page and Close it now are drawn on (null when the city has none). |
| 4231 | 42 | `static List<Spot> pictureSpots(Game g, KeptBench bench, Debt piece) throws Exception` | The places the money screens' pictures are: one page of each tab that holds some, and the pages behind them. |
| 4280 | 28 | `static void moneyPictures(Game g, KeptBench bench) throws Exception` | Section 64: the pictures the money screens hold are kept. |
| 4310 | 93 | `static void pictureKept(Game g, KeptBench bench, Spot spot, String kind) throws Exception` | One kind of picture on one page, watched for PICTURE_MONTHS months. |

### the count-up (65) (lines 4404-4629)

| line | len | member | says |
|---:|---:|---|---|
| 4407 | 10 | `static int[] started(KeptBench bench, java.util.Set<Animation> before)` | The animations a draw has started since `before` that are running now: {pops, counts and growths, others}. |
| 4419 | 5 | `static java.util.Set<Animation> animationsNow(KeptBench bench)` | The animations on the page now, as a set by identity. |
| 4426 | 5 | `static int running(KeptBench bench, Class<? extends Animation> kind)` | How many animations of a kind run on the page now. |
| 4433 | 7 | `static String stillOrNot(KeptBench bench)` | What is wrong with a page on which nothing may animate: a running animation, a figure not at rest. |
| 4446 | 29 | `static String bankRung(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 65 on the Bank tab (the Overview when `page` is null): the page is shown, a month lands (one that moved the capital ratio, on the Overview) and it is shown again; the PROFIT figure pops and ... |
| 4477 | 4 | `static void steerGauge(Game g)` | The Bank's risk-weighted ratio made the one that binds (see bankRung()): the book weighed for risk set to twenty times what the bank holds. |
| 4483 | 15 | `private static String bankRungChecks(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect, java.util.Se...` | ...what the month that landed started, once shown (the weighted book still steered on the Overview, so that the page built new beside it is drawn on the same bank). |
| 4500 | 41 | `static String fundRung(Game g, KeptBench bench, int at, boolean paused, boolean expect) throws Exception` | ...and the fund's Portfolio, through the tab's real show method: the worth counts on and a payment pops. |
| 4548 | 81 | `static void moneyCountUp(Game g, KeptBench bench) throws Exception` | Section 65: the count-up only at 1x-10x, on the Bank (the PROFIT figure's pop on the Overview and on a page's status strip, the capital gauge's growth) and the fund (the worth's count and a payment's pop). |

### the boxes (66) (lines 4630-4791)

| line | len | member | says |
|---:|---:|---|---|
| 4637 | 59 | `static void boxHeld(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.Su...` | One box on a page, given the focus and typed into (a node's `setFocused` when this scene has no window to give the focus: the line says which): a month landed through the real show method twice - the same node, its te... |
| 4703 | 52 | `static void boxEnter(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.S...` | Section 66's Enter (KP2b, for KP5's mend in CapChips): a yield box given the focus, "4.5" typed and Enter fired on it. |
| 4757 | 34 | `static void moneyControls(Game g, KeptBench bench) throws Exception` | Section 66: the boxes keep the player's hand - Close it now's two, the ask's, the yield boxes of Borrow's and the rollover's chips (typed into, a chip pressed, and Enter), the fund's search box. |

### the hover (67) and the cost (68) (lines 4792-4921)

| line | len | member | says |
|---:|---:|---|---|
| 4795 | 29 | `static void moneyHover(Game g, KeptBench bench) throws Exception` | Section 67: the hover. |
| 4826 | 20 | `static KeptBench.Page heaviest(KeptBench bench, String tab, List<KeptBench.Page> pages) throws Exception` | The page of a tab that weighs most (the nodes its first draw makes), by name: printed with the others. |
| 4848 | 45 | `static void moneyCost(Game g, KeptBench bench) throws Exception` | Section 68: what the kept month costs on the heaviest page of the Bank, of Finances and of the fund (and the fund's Activity and Search). |
| 4895 | 19 | `static void money(Game g, KeptBench bench) throws Exception` | Sections 55 to 68: the money screens kept. |
| 4916 | 5 | `private static long took(String what, long since)` | What a stretch of the money sections took, in a line; the clock's reading for the next. |

### GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept ... (lines 4922-5185)

| line | len | member | says |
|---:|---:|---|---|
| 4964 | 5 | `private static long tookWith(String what, long since, int checksSince)` | What a stretch of the KP6 sections took and how many assertions it made, in a line; the clock's reading for the next. |
| 4978 | 21 | `static double[] makeDeficit(Game g) throws Exception` | The taxes cut to nothing, the central bank printing for the treasury and two lines of arrears owed; what the dials were. |
| 5001 | 10 | `static void endDeficit(Game g, double[] was)` | The dials put back where makeDeficit() found them. |
| 5013 | 5 | `static int linesSaying(KeptBench bench, String marker)` | How many lines of the page's signature hold a marker (a card the page draws only in some states). |
| 5024 | 5 | `static List<Choice> asActing(List<Choice> choices)` | The choices made to act (see Choice.acts): for a page whose content is the model's ranking or the sign of its budget, where a month that moves a row in or out, or turns a surplus into a deficit, makes the nodes of the... |
| 5031 | 28 | `static List<Choice> governmentChoices(boolean debt)` | The Government tab's pages, folds and lines as choices (the folds that must add to their page only where the city has what they fold). |
| 5061 | 21 | `static int[] linesThatAdd(KeptBench bench, KeptBench.Page tab, String page) throws Exception` | How many of a Government page's lines add to it when opened into who pays: {added, tried}. |
| 5084 | 101 | `static void kp6Government(Game g, KeptBench bench) throws Exception` | Sections 69 to 71: the Government tab kept. |

### Trade (lines 5186-5385)

| line | len | member | says |
|---:|---:|---|---|
| 5189 | 6 | `static Hand tradeOn(String page)` | The hand that puts the Trade tab on a page. |
| 5197 | 6 | `static Hand tradeNationOn(int nation)` | The hand that puts the Trade tab on a nation's page behind Partners (0 is the table). |
| 5205 | 6 | `static Hand tradeFold(String key, boolean open)` | A fold or a line of the Trade tab opened or closed. |
| 5213 | 172 | `static void kp6Trade(Game g, KeptBench bench) throws Exception` | Sections 72 to 74: Trade & the World kept. |

### Policy (lines 5386-5636)

| line | len | member | says |
|---:|---:|---|---|
| 5389 | 6 | `static Hand policyOn(String area, String page)` | The hand that puts Policy on an area and a page (the hub when `area` is null). |
| 5397 | 3 | `static Choice policyPage(String area, String page)` | A choice that opens a page of Policy. |
| 5402 | 3 | `static Choice stage(String name, String key, double value)` | A choice that stages a change under a lever's key. |
| 5407 | 3 | `static Choice drop(String name)` | A choice that drops everything staged. |
| 5412 | 3 | `static Choice policyFold(String name, String key, boolean open, boolean adds)` | A fold or a line of Policy, opened or closed, as a choice (`opens` for a fold that has to add to its page). |
| 5417 | 6 | `static Choice policyAllClosed(String name)` | Every fold and line of Policy closed. |
| 5425 | 174 | `static void kp6Policy(Game g, KeptBench bench) throws Exception` | Sections 75 to 77: Policy and zoning kept. |
| 5605 | 31 | `static void peopleShort(Game g, KeptBench bench, String[] hunger) throws Exception` | People's WHO GOES SHORT list and card (sections 1 to 5 draw the page): with people short of food the card says so and draws its bar, the People page kept for a stretch of months equals a page built new, and Policy's F... |

### the controls (78) (lines 5637-5979)

| line | len | member | says |
|---:|---:|---|---|
| 5643 | 1 | **type** `record AtOnce(java.util.function.Supplier<Double> now, Runnable putBack)` | A dial that applies at once and stages nothing (the policy rate page's target and HOW STRICT): what the model reads at the dial now, and how it is put back (KP2b: the held dials were all staged ones). |
| 5646 | 4 | `static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> sliderAt(int index, double m...` | The slider at a place in a page's sliders, when it has these ends - else null (the page is not the page this expects). |
| 5652 | 7 | `static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> offsetDial(int index)` | The slider at a place among a page's sliders that run under nothing (a tax offset's dial, which reaches below zero), else null. |
| 5661 | 12 | `static String whoSet()` | The few frames of the code that made a change just heard by a listener (skipping this harness and the listener's own plumbing): who set it. |
| 5678 | 18 | `static String taxDialMoving(KeptBench bench, int index) throws Exception` | The first sector whose zone page has a tax dial (the `index`th that runs under nothing) that moves what the ITS TAXES card says it would do when it is dragged two steps, the dial put back after - or null. |
| 5698 | 7 | **type** `static final class Held` | What one held dial did, for holdDial to judge. |
| 5714 | 131 | `static void holdDial(Game g, KeptBench bench, String what, String area, String page, java.util.function.Function<List<javafx.sc...` | Section 78: a dial of Policy keeps the player's hand, through the screen's real show method. |
| 5847 | 3 | `static void policyDial(Game g, KeptBench bench, String what, String area, String page, String key, boolean trayShows) throws Ex...` | Section 78's dial, held, the first slider of a page: see holdDial. |
| 5857 | 61 | `static void heldWithCardInFront(Game g, KeptBench bench, String what, KeptBench.Page p, Hand setup, Hand front) throws Exception` | Section 78's last part: a card comes in front of a dial the player holds. |
| 5920 | 24 | `static void policyDialsKeep(Game g, KeptBench bench) throws Exception` | Section 78's last part: a card in front of a held dial, on the Schools page (a course's own price in front of the loan's) and on a tax page (a payer's own move in front of another's). |
| 5946 | 33 | `static void kp6Controls(Game g, KeptBench bench) throws Exception` | Section 78: the controls on Policy - a tax page's rate dial, the policy rate's, a zone's ground dial, and a card coming in front of a held dial. |

### the count-up (79) (lines 5980-6192)

| line | len | member | says |
|---:|---:|---|---|
| 5983 | 3 | `private static boolean moved(String[] words)` | Whether a counted figure's words moved between last month and this. |
| 5993 | 56 | `static String tradeRung(Game g, KeptBench bench, String page, boolean bars, int at, boolean paused, boolean expect) throws Exce...` | One clock speed of section 79 on a Trade page: the page is shown through its real show method, a month that moved SOLD ABROAD or BOUGHT ABROAD lands and it is shown again; each of the strip's two figures that moved st... |
| 6054 | 21 | `static String quietRung(Game g, KeptBench bench, KeptBench.Page page, Runnable open, Runnable show, int at, boolean paused, boo...` | One clock speed on a page that has no animation to run (a Government or Policy page): a month lands and the page is shown through its real show method; nothing starts at any rung, nothing is running or off its rest, a... |
| 6083 | 109 | `static void kp6CountUp(Game g, KeptBench bench) throws Exception` | Section 79: the count-up only at 1x-10x. |

### the hover (80) and the cost (81) (lines 6193-6361)

| line | len | member | says |
|---:|---:|---|---|
| 6196 | 14 | `private static void hoverKP6(Game g, KeptBench bench, java.util.Map<String, Boolean> found, String where, KeptBench.Page page, ...` | The pointer put over a node of each kind on a page (its `setup` made first), hoverLine() saying what came of it; `found` notes where a node was found. |
| 6217 | 30 | `static void kp6Hover(Game g, KeptBench bench) throws Exception` | Section 80: the hover. |
| 6253 | 55 | `static void kp6Tips(Game g, KeptBench bench) throws Exception` | Section 80's last part: the tooltips are kept. |
| 6310 | 18 | `static void kp6Cost(Game g, KeptBench bench) throws Exception` | Section 81: what the kept month costs on the Government Overview, on Trade's Overview and its heaviest page, and on Policy's hub and its dial-heavy pages. |
| 6330 | 31 | `static void screensKP6(Game g, KeptBench bench) throws Exception` | Sections 69 to 81: Government, Trade & the World and Policy kept. |

### THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of ... (lines 6362-6681)

| line | len | member | says |
|---:|---:|---|---|
| 6367 | 8 | `static boolean has(List<String> signature, String...words)` | Whether some line of a signature has all of these words in it. |
| 6377 | 3 | `static String label(String text)` | A label's line in a signature: `Label "text"`. |
| 6389 | 194 | `static void researchFound(Game g, KeptBench defaultBench) throws Exception` | Section 95: Policy > Research > Found, the sixth Policy area's page, kept (R5, slice B1). |
| 6585 | 1 | **type** `private record RailScreen(String label, String name, java.util.function.Consumer<KeptBench> show)` | A rail screen the clock is held to in section 88: how it is called here, the name its show method gives the window, and the call that shows it. |
| 6598 | 83 | `static void kp7Clock(Game g, KeptBench bench) throws Exception` | Section 88: the clock never rebuilds a rail screen. |

### HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page draw... (lines 6682-6985)

| line | len | member | says |
|---:|---:|---|---|
| 6705 | 57 | `static void kp7Household(Game g, KeptBench bench) throws Exception` | Sections 82 to 84: Household money kept, shown as a page built new shows it, and kept right through what reshapes it. |
| 6764 | 81 | `static void kp7Rankings(Game g, KeptBench bench) throws Exception` | Sections 85 to 87: the Rankings tab kept, shown as a page built new shows it, and kept right through what reshapes it - to the pixel, since its table is painted. |
| 6847 | 24 | `static int[] tipsAcrossMonth(Game g, KeptBench bench, KeptBench.Page page) throws Exception` | The tooltips on a page across a month's kept redraw (setup made first): {tooltips before, on a node that has one after, the same Tooltip, on a node kept without one}. |
| 6879 | 25 | `static void kp7Hover(Game g, KeptBench bench) throws Exception` | Section 89: the hover and the tooltips. |
| 6906 | 31 | `static void kp7Tips(Game g, KeptBench bench) throws Exception` | Section 89's last part: the tooltips are kept on Household money and the Rankings tab. |
| 6942 | 21 | `static void kp7Cost(Game g, KeptBench bench) throws Exception` | Section 90: what the kept month costs on Household money - as it opens and with the first family cell's books open - and on the Rankings tab - as it opens and with every measure unfolded - against the same month built... |
| 6965 | 20 | `static void screensKP7(Game g, KeptBench bench) throws Exception` | Sections 82 to 90: Household money and the Rankings tab kept, and the clock never rebuilding a rail screen (88). |

### a slider greyed while held (91) (lines 6986-7108)

| line | len | member | says |
|---:|---:|---|---|
| 6989 | 5 | **type** `static final class Greyed` | What one greying of a held slider did, for greyedWhileHeld to judge. |
| 7002 | 60 | `static void greyedWhileHeld(Game g, KeptBench bench) throws Exception` | Section 91 (KP2b): a slider greyed while the player holds it lets go. |
| 7068 | 33 | `static void doorsStyledOnce(Game g, KeptBench bench) throws Exception` | Section 91's door cards (KP2b): a door card's hand style is set once a draw, whole. |
| 7103 | 5 | `private static Notice add(Game g, String key, String title)` | A notice raised now, unread, in the inbox. |

### THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node... (lines 7109-7652)

| line | len | member | says |
|---:|---:|---|---|
| 7129 | 21 | `static void nothingSet(KeptBench bench, KeptBench.Page page, String name, Hand hand) throws Exception` | A page home, a hand on it, drawn and laid out, drawn once more with a listener on every style, text, graphic, padding and width on it (KeptBench.redrawSets), no month between: how many of them the second draw wrote, a... |
| 7151 | 3 | `static void nothingSet(KeptBench bench, KeptBench.Page page, String name) throws Exception` |  |
| 7159 | 12 | `static List<Debt> piecesOnTheBooks(Game g, KeptBench bench) throws Exception` | The pieces of paper on the city's books; where there are none (the months the sections before this played let every maturity pass) one sold through the model, as the Borrow page's own button sells it, the first of a T... |
| 7176 | 7 | `static void letGo(KeptBench bench, java.util.function.Consumer<KeptBench> forget) throws Exception` | Section 92 lets one tab's pages go before it opens the next (KP2b): the tab forgotten, as another city opened leaves it, and the pages built new released (KeptBench.release). |
| 7185 | 127 | `static void kp2bSets(Game g, KeptBench bench) throws Exception` | Section 92: the pages the sweep mended, each redrawn with nothing new in the city. |
| 7314 | 34 | `static void kp2bHouseholdMoney(Game g, KeptBench bench) throws Exception` | Section 92 on Household money (KP7, taken into KP2b): the page as it opens, each view, every cell of the grid opened, the statement and the books stacked under the grid. |
| 7353 | 16 | `static int rankingsSavedCities(KeptBench bench) throws Exception` | The city saved into slot 10 of the harness's own folder and read back, so that the Rankings tab has a saved city on its board: section 87 does it, and does nothing here where it has run. |
| 7376 | 24 | `static void kp2bRowsPicture(Game g, KeptBench bench) throws Exception` | Section 93: the Rankings table's picture (KP2b), the one node on the page that paints its rows. |
| 7406 | 53 | `static void kp2bRankings(Game g, KeptBench bench) throws Exception` | Section 92 on the Rankings tab (KP7, taken into KP2b): the page as it opens, each board, money, prices and conversion, some measures (every tenth, the last and the first no real place is ranked on), each filter, Aroun... |
| 7460 | 192 | `public static void main(String[] args) throws Exception` |  |


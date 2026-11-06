# KeptCheck.java - 4,644 lines · 107 methods · 30 constants · harnesses

`ham/citybuildersim/KeptCheck.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

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
> ... (138 more lines in the source)

**Uses:** [KeptBench](KeptBench.md) (282), [Game](Game.md) (79), [Sectors](Sectors.md) (31), [Debt](Debt.md) (16), [Notice](Notice.md) (12), [BuildAdvice](BuildAdvice.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [Timing](Timing.md) (10), [WellCheck](WellCheck.md) (6), [OilCheck](OilCheck.md) (6), [ChartModel](ChartModel.md) (4), [RefineryViewCheck](RefineryViewCheck.md) (4), [OilViewCheck](OilViewCheck.md) (4), [BuildingManager](BuildingManager.md) (3), [IndustryView](IndustryView.md) (3), [RefineryView](RefineryView.md) (3), [MiningCheck](MiningCheck.md) (3), [Auction](Auction.md) (2), [SectorFlow](SectorFlow.md) (2), [Bank](Bank.md) (2), [GameFiles](GameFiles.md) (2), [LongPlaytest](LongPlaytest.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [LandParcel](LandParcel.md) (1), [AutoBuilder](AutoBuilder.md) (1), [Sector](Sector.md) (1), [LongTermBond](LongTermBond.md) (1), [CorporateBond](CorporateBond.md) (1)

**Used by (1):** [ChartFitCheck](ChartFitCheck.md)

## Sections

| line | section |
|---:|---|
| 1361 | INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page |
| 1946 | BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, |
| 2535 | SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and |
| 3175 | THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... |
| 3413 | · Finances |
| 3641 | · the city's fund |
| 3816 | · the pictures (64) |
| 3996 | · the count-up (65) |
| 4222 | · the boxes (66) |
| 4323 | · the hover (67) and the cost (68) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 228 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 231 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 234 | `KeptCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 237 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 332 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's cached outline a le... |
| 335 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 378 | `KeptCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 381 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 603 | `KeptCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |
| 873 | `KeptCheck.KEPT_HISTORY_MS` | `13` | The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same mon... |
| 903 | `KeptCheck.TICKED` | `"unemployment"` | The first of these lines picked and not in the preset the page opens on: a line to tick and untick. |
| 1367 | `KeptCheck.HANDED_BACK` | `0.9` | The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). |
| 1600 | `KeptCheck.KEPT_INFRASTRUCTURE_MS` | `9` | The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 betwe... |
| 1607 | `KeptCheck.KEPT_SERVICES_MS` | `13` | The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 betwe... |
| 1614 | `KeptCheck.KEPT_LAND_MS` | `5` | The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 ... |
| 1654 | `KeptCheck.SERVICES_FOLDS` | `{ { "Health/Overview", "health:sick" }, { "Health/General care", "health:gene...` | The folds each sub-page of Services has, by "system/page" (a page not named has none). |
| 1957 | `KeptCheck.KEPT_BUILD_MS` | `12` | The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a city category with two orders dialled up and a card's stats pinned (the heavier of ... |
| 1964 | `KeptCheck.KEPT_CONSTRUCTION_MS` | `4` | The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1... |
| 1967 | `KeptCheck.SITE_ROWS` | `6` | The rows the Sites tab and the timeline are kept at while Build and the construction page are watched: a site each. |
| 1970 | `KeptCheck.SITE_ORDER` | `{ "Gravel Road", "Walk-in Clinic", "Paved Road", "Bus Network", "Elementary S...` | What is ordered, in turn, to put a site on: the cheap, quick works first. |
| 2546 | `KeptCheck.KEPT_SECTORS_MS` | `18` | The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same mon... |
| 2555 | `KeptCheck.KEPT_SECTOR_MS` | `15` | The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the heaviest of the three pages costed (the shops' Operations; a plain business's Income stat... |
| 2558 | `KeptCheck.PICTURE_MONTHS` | `6` | The months the pictures are watched through (section 50): each month a repaint or not, and a page built new beside it. |
| 2564 | `KeptCheck.NOT_PLAIN` | `java.util.Set.of(Sectors.RETAIL, Sectors.RESTAURANTS, Sectors.LUXURY_RETAIL, ...` | The keys a plain business is not taken from: the shops, the kitchens, the counters, the landlords, the haulier and the fuel's two. |
| 2581 | `KeptCheck.WORKS_ORDER` | `{ "Sawmill", "Scrap Yard", "Wood Pellet Mill", "Panel Mill" }` | What a materials industry's works are made of, in turn, for a city that has none standing: a sawmill first, the rest if it cannot. |
| 3186 | `KeptCheck.KEPT_BANK_MS` | `12` | The budget for the kept Bank month - its draw, then the pulse's CSS and layout - on the Bank's heaviest page (the Overview), on the PC: twice the kept month's median, rounded up (5.2 to 8.3 ms in five runs of the mone... |
| 3193 | `KeptCheck.KEPT_FINANCES_MS` | `18` | The budget for the kept Finances month on the heaviest of the two pages costed (Borrow at home, the page the auction draws, and the page with the most nodes), on the PC: twice the kept month's median, rounded up (the ... |
| 3202 | `KeptCheck.KEPT_FUND_MS` | `321` | The budget for the kept month on the fund's heaviest page (Search and Activity, the two costed, weigh most; their size follows the city's age: the bonds it has issued are the rows of Search, its months of events the r... |
| 3218 | `KeptCheck.BANK_FOLDS` | `{ { "Profit", "profit:statement", "Interest earned", "Fees", "Provisions for ...` | What the Bank's folds hold, by page: the fold's key, then the lines a player opens inside it (each a key the page's own bookLine opens by). |
| 3416 | `KeptCheck.PAPER` | `{ { "Note", "12", "" }, { "Float", "5", "" }, { "Serial", "10", "" }, { "Term...` | The paper the money sections sell, one of each kind the Borrow page offers (and a callable bond too): {kind, term, "callable" or ""}. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 220 | `static int fails` |  |
| 261 | `static Runnable afterMonth` | What is done after every month played, with the log off, while a fixture has to be held through the months (KP3: the city's sites); null for nothing. |
| 1973 | `private static int siteTurn` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 218 | 4427 | **type** `public class KeptCheck` | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exactly where a page built new would, and costs a fraction of it. |
| 222 | 4 | `static void check(String label, boolean ok)` |  |
| 239 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 246 | 13 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on). |
| 264 | 10 | `static void month(Game g)` | A month, as the clock runs it, its log kept off this harness's output. |
| 276 | 9 | `static<T> T quietly(Callable<T> call) throws Exception` | A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). |
| 287 | 28 | `static String firstDifference(List<String> kept, List<String> fresh)` | The first line where two signatures part, or null when they are the same. |
| 317 | 6 | `static void png(int[] px, java.nio.file.Path to) throws java.io.IOException` | A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. |
| 338 | 10 | `static boolean alike(int[] a, int[] b)` | Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. |
| 350 | 16 | `static String pixelDifference(int[] a, int[] b)` | Where two snapshots part: their sizes, or how many pixels differ and the box round them. |
| 367 | 3 | `static String clip(String s)` |  |
| 371 | 5 | `static double median(List<Double> v)` |  |
| 389 | 212 | `static void chrome(Game g, KeptBench bench) throws Exception` | Sections 6 to 10: the header and the tab rail kept (UP2). |
| 606 | 11 | `static void panelStates(KeptBench bench, int at)` | The panels' states, month by month: the city panel's mode, and which of its sections are open. |
| 622 | 245 | `static void panels(Game g, KeptBench bench) throws Exception` | Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice 2b), each on a scene of its own, beside the header and the rail of 6 to 10. |
| 877 | 3 | **type** `interface Hand` | A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). |
| 878 | 1 | `String on(KeptBench bench) throws Exception` _(in KeptCheck.Hand)_ |  |
| 887 | 9 | **type** `record Choice(String name, Hand hand, boolean opens, boolean acts)` | One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2); `acts` for a choice that makes the model act (KP3: automatic building turned on or... |
| 888 | 3 | `Choice(String name, Hand hand, boolean opens)` _(in KeptCheck.Choice)_ |  |
| 892 | 3 | `Choice(String name, Hand hand)` _(in KeptCheck.Choice)_ |  |
| 898 | 3 | `static Choice acting(String name, Hand hand)` | A choice that makes the model act (see Choice.acts). |
| 909 | 218 | `static void history(Game g, KeptBench bench) throws Exception` | Sections 14 to 20: City History kept (KP1). |
| 1129 | 7 | `private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved)` | Months landed until the figure has moved (at most twelve): whether it did. |
| 1138 | 3 | `private static String nullIfOffered(String refusal, String what)` | The choice's refusal, as the premise it could not meet, or null when the chart is offered. |
| 1148 | 77 | `static void crosshair(Game g, KeptBench bench) throws Exception` | Section 18: what a page built new loses and a kept one keeps. |
| 1232 | 96 | `static void countUp(Game g, KeptBench bench) throws Exception` | Section 19: the count-up is for a clock that can be followed. |
| 1330 | 30 | `static void historyCost(Game g, KeptBench bench) throws Exception` | Section 20: what the kept City History month costs, against the same month built new. |

### INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page (lines 1361-1945)

| line | len | member | says |
|---:|---:|---|---|
| 1370 | 12 | `static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. |
| 1384 | 3 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the page is kept - a month's redraw keeps its top nodes, hands back nine in ten of the first draw's nodes, and on most months makes none. |
| 1393 | 37 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n, boolean eventful) throws Exception` | ...for a page whose content is the model's events (`eventful`, KP5: the city's fund, where a bond repaid, a payment and a quote line are rows of their own): the nodes a month makes are the month's new rows, so most mo... |
| 1432 | 71 | `static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. |
| 1509 | 45 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception` | Section n: every choice that reshapes the page, made in turn: the redraw after it and the one after a month lands are each equal to a page built new, and the month's redraw hands back nine in ten of the page's nodes. |
| 1556 | 3 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception` | Section n: what the kept month on the page's home costs, against the same month built new. |
| 1561 | 28 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial, int samples) throws Excep...` | ...the median of `samples` months, for the pages whose month is long (the fund's, tens of thousands of nodes). |
| 1591 | 3 | `static Choice choice(String name, Hand hand)` | A choice with no side effect on the model: a hand that changes the screen's state only. |
| 1617 | 9 | `static String ensureTransit(Game g) throws Exception` | Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. |
| 1628 | 24 | `static void infrastructure(Game g, KeptBench bench) throws Exception` | Sections 21 to 24: Infrastructure kept. |
| 1666 | 21 | `static void services(Game g, KeptBench bench) throws Exception` | Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. |
| 1689 | 49 | `static void land(Game g, KeptBench bench) throws Exception` | Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. |
| 1745 | 123 | `static void controls(Game g, KeptBench bench) throws Exception` | Section 34: a control keeps the player's hand. |
| 1870 | 64 | `static void hover(Game g, KeptBench bench) throws Exception` | Section 35: the hover kept. |
| 1936 | 9 | `private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result)` | One hover result: printed, counted as found when the page had the node, and a check that it held. |

### BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, (lines 1946-2534)

| line | len | member | says |
|---:|---:|---|---|
| 1976 | 12 | `static boolean buyGround(Game g, BuildingsTemplate t, int n) throws Exception` | Ground bought through the model for an order that found none: the cheapest offer that covers the shortfall, the cash it lacks borrowed on Build's bond; whether it was bought. |
| 1990 | 30 | `static String ensureSites(Game g, int rows) throws Exception` | Orders through the model until the city has `rows` sites on (a row each on the Sites tab, a bar each on the timeline): what it did, in a line. |
| 2022 | 9 | `static void keepSites(Game g)` | From now every month played tops the sites up to SITE_ROWS (the fixture holds while the pages are watched), until stopSites(). |
| 2032 | 3 | `static void stopSites()` |  |
| 2037 | 137 | `static void build(Game g, KeptBench bench) throws Exception` | Build's pages: the Overview and a city category with two orders dialled up (so that its order bar shows), kept, as built new, reshaped and costed. |
| 2176 | 67 | `static void construction(Game g, KeptBench bench) throws Exception` | The construction page, with the city's sites on: the Sites tab kept, shown, reshaped by its tabs, a pick and the find box, and costed. |
| 2250 | 34 | `static void stopped(Game g, KeptBench bench) throws Exception` | Section 44: the pages a build is stopped on and the run's funding and fell-short pages, each caused where the model can cause it (a count Game.buildRunStop() refuses for the reason; a run larger than the cash) and eac... |
| 2292 | 207 | `static void buildControls(Game g, KeptBench bench) throws Exception` | Section 45: a control keeps the player's hand. |
| 2501 | 33 | `static void buildHover(Game g, KeptBench bench) throws Exception` | Section 46: the hover. |

### SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and (lines 2535-3174)

| line | len | member | says |
|---:|---:|---|---|
| 2561 | 1 | **type** `record SectorKinds(String plain, String materials)` | The businesses the Sectors sections take besides the shops, the landlords and Freight: a plain one with a plant standing, and a materials industry with works to draw. |
| 2568 | 11 | `static SectorKinds sectorKinds(Game g)` | The plain business this city has, by the model's own reading of its plants, and the materials industry its works draw: wood products, which ensureWorks() puts up a sawmill for (null when it has none standing). |
| 2590 | 6 | `static String ensureWorks(Game g) throws Exception` | Wood products' works standing in the city: the ones it has, else a sawmill put up through the model (finished at once; the city borrows what it lacks on Build's bond and buys ground where it finds none, as ensureSites... |
| 2598 | 18 | `static String putUpWorks(Game g) throws Exception` | A sawmill (or the first of WORKS_ORDER the model lets be built) put up finished: "a Sawmill put up through the model", or why none could be. |
| 2618 | 8 | `static void arrive(KeptBench bench, KeptBench.Page page) throws Exception` | A page arrived at: back on its home, drawn and laid out, as the bench's other pages are before their sections. |
| 2628 | 6 | `static Hand on(String key, String page, boolean statement)` | The hand that opens a business's page in a view. |
| 2636 | 3 | `static String called(Game g, String key)` | What a business is called on the screen. |
| 2641 | 3 | `static String possessive(String name)` | A name's possessive as the lines say it: "Retail's", "Wood Products'". |
| 2646 | 104 | `static void sectors(Game g, KeptBench bench) throws Exception` | Sections 47 to 54: the Sectors tab kept. |
| 2757 | 27 | `static void sectorsPictures(Game g, KeptBench bench, String works, String worksName) throws Exception` | Section 50: the pictures a business's page holds are kept. |
| 2791 | 74 | `static void picturesKept(Game g, KeptBench bench, KeptBench.Page page, String what, java.util.function.Function<KeptBench, List...` | What a month does to the pictures a page holds (`find` takes them off the page; `paints` for a picture that is told the month's view and paints it, as the refinery's and a materials industry's are, and not for a bar t... |
| 2867 | 10 | `static String againstNewAsIs(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands with no animation finished first, against the same page built new: the first difference, or null. |
| 2879 | 5 | `private static boolean figuresMoved(String[][] words, boolean business)` | Whether the figures the Sectors pages count have moved between last month and this: the list's KEPT, a business's, and some card's. |
| 2886 | 6 | `private static List<javafx.scene.control.Label> countingFigures(KeptBench bench, String key)` | The figures on a Sectors page that count up: the KEPT, and on the list every card's net income. |
| 2898 | 65 | `static String countRung(Game g, KeptBench bench, String key, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 51 on one page (`key` null for the list): the page is shown, a month lands and it is shown again; a count starts on each figure that moved exactly where `expect` says, the waterfall grows an... |
| 2970 | 65 | `static void sectorsCountUp(Game g, KeptBench bench, String plain) throws Exception` | Section 51: the count-up only at 1x-10x, on the list's KEPT figure and its cards', a business's KEPT, the Income page's waterfall and the Operations page's ring. |
| 3037 | 19 | `static Game seaTown() throws Exception` | The oil reserve's city: OilViewCheck's sea town (a platform with its wells, two land wells, a Strategic Reserve with FILL ordered), its fixture's report lines kept off this output. |
| 3063 | 24 | `static void sectorsControls() throws Exception` | Section 53: a control keeps the player's hand. |
| 3089 | 68 | `static void dial(Game sea, KeptBench bench, KeptBench.Page page, int which, String name, String key, java.util.function.DoubleF...` | One of the reserve's dials (the `which`th slider on the page), pressed, held across a month, released, and set outside a draw. |
| 3159 | 15 | `static void sectorsHover(Game g, KeptBench bench, String plain) throws Exception` | Section 54: the hover. |

### THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept ... (lines 3175-3412)

| line | len | member | says |
|---:|---:|---|---|
| 3205 | 3 | `private static boolean twoViews(String page)` | The Bank tab's pages that have two views, a Summary and a Statement. |
| 3210 | 6 | `static Hand bankOn(String page, boolean statement)` | The hand that opens a Bank page, in a view (the Overview when `page` is null). |
| 3225 | 88 | `static void bank(Game g, KeptBench bench) throws Exception` | Sections 55 to 57: the Bank tab kept. |
| 3318 | 14 | `static Hand changing(KeptBench.Page page, Hand hand)` | A hand that has to change the page (the premise of a line opened that adds no node: a statement line's detail is built hidden and shown): the page drawn before and after, and the choice refused as made if the two are ... |
| 3339 | 73 | `static void bankPicks(Game g, KeptBench bench) throws Exception` | Section 57's last part: a chip picked lands at the top of its page (BankScreen.toTop). |

### Finances (lines 3413-3640)

| line | len | member | says |
|---:|---:|---|---|
| 3419 | 3 | `static String pieceWords(Debt d)` | A piece of paper in words: its kind, as the book writes it, and its maturity. |
| 3428 | 18 | `static List<Debt> sellPaper(Game g, KeptBench bench) throws Exception` | The city's books put where the Finances screens have something to draw: one piece of every kind of paper sold through the model, as the Borrow page's own button sells it (executeDebtLogic, at the default highest yield... |
| 3448 | 6 | `static Hand financesOn(String area, String page)` | The hand that puts the Finances tab on an area and a page. |
| 3456 | 7 | `static Hand lineOf(Debt piece)` | The hand that opens a piece's own page, and the piece still on the books is its premise. |
| 3465 | 7 | `static Hand closeOf(Debt piece)` | The hand that opens Close it now on a piece (from its line's page when that is open), the same premise. |
| 3474 | 73 | `static void finances(Game g, KeptBench bench) throws Exception` | Sections 58 to 60: the Finances tab kept. |
| 3549 | 61 | `static List<Choice> borrowChoices(Game g, KeptBench bench) throws Exception` | Borrow's choices: the kinds, their terms, the ask, callable, the highest yield, what is typed, and abroad every money. |
| 3612 | 28 | `static void financesMenus(Game g, KeptBench bench) throws Exception` | Sections 60's last part: the foreign default menu and the debt result menu, each an arrival and a month later. |

### the city's fund (lines 3641-3815)

| line | len | member | says |
|---:|---:|---|---|
| 3644 | 6 | `static Hand fundOn(String page)` | The fund's pages by name (Portfolio, Search, Activity, Rules & cash). |
| 3652 | 6 | `static Hand securityOf(String key, String from)` | The hand that opens a security's page by its lot key, from a page of the fund. |
| 3660 | 6 | `static Hand ticket(boolean buy, boolean byAmount, double amount, String kind, int steps, boolean review)` | The hand that sets the ticket on the security open. |
| 3668 | 4 | `static String lotOf(List<String> from, String prefix, List<String> not, boolean rescue)` | The first lot key in `from` that starts with `prefix` and is not in `not` (and has no ":rescue" in it unless `rescue`), or null. |
| 3674 | 141 | `static void fund(Game g, KeptBench bench) throws Exception` | Sections 61 to 63: the city's fund kept. |

### the pictures (64) (lines 3816-3995)

| line | len | member | says |
|---:|---:|---|---|
| 3819 | 1 | **type** `record Spot(String label, KeptBench.Page page, Hand setup)` | A place a picture is seen: its label, the page, and the hand that sets the page for it after the page's home. |
| 3822 | 5 | `static Debt longestPiece(KeptBench bench) throws Exception` | The piece of paper on the books that falls due last: the one a line's page and Close it now are drawn on (null when the city has none). |
| 3829 | 38 | `static List<Spot> pictureSpots(Game g, KeptBench bench, Debt piece) throws Exception` | The places the money screens' pictures are: one page of each tab that holds some, and the pages behind them. |
| 3874 | 28 | `static void moneyPictures(Game g, KeptBench bench) throws Exception` | Section 64: the pictures the money screens hold are kept. |
| 3904 | 91 | `static void pictureKept(Game g, KeptBench bench, Spot spot, String kind) throws Exception` | One kind of picture on one page, watched for PICTURE_MONTHS months. |

### the count-up (65) (lines 3996-4221)

| line | len | member | says |
|---:|---:|---|---|
| 3999 | 10 | `static int[] started(KeptBench bench, java.util.Set<Animation> before)` | The animations a draw has started since `before` that are running now: {pops, counts and growths, others}. |
| 4011 | 5 | `static java.util.Set<Animation> animationsNow(KeptBench bench)` | The animations on the page now, as a set by identity. |
| 4018 | 5 | `static int running(KeptBench bench, Class<? extends Animation> kind)` | How many animations of a kind run on the page now. |
| 4025 | 7 | `static String stillOrNot(KeptBench bench)` | What is wrong with a page on which nothing may animate: a running animation, a figure not at rest. |
| 4038 | 29 | `static String bankRung(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 65 on the Bank tab (the Overview when `page` is null): the page is shown, a month lands (one that moved the capital ratio, on the Overview) and it is shown again; the PROFIT figure pops and ... |
| 4069 | 4 | `static void steerGauge(Game g)` | The Bank's risk-weighted ratio made the one that binds (see bankRung()): the book weighed for risk set to twenty times what the bank holds. |
| 4075 | 15 | `private static String bankRungChecks(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect, java.util.Se...` | ...what the month that landed started, once shown (the weighted book still steered on the Overview, so that the page built new beside it is drawn on the same bank). |
| 4092 | 41 | `static String fundRung(Game g, KeptBench bench, int at, boolean paused, boolean expect) throws Exception` | ...and the fund's Portfolio, through the tab's real show method: the worth counts on and a payment pops. |
| 4140 | 81 | `static void moneyCountUp(Game g, KeptBench bench) throws Exception` | Section 65: the count-up only at 1x-10x, on the Bank (the PROFIT figure's pop on the Overview and on a page's status strip, the capital gauge's growth) and the fund (the worth's count and a payment's pop). |

### the boxes (66) (lines 4222-4322)

| line | len | member | says |
|---:|---:|---|---|
| 4229 | 59 | `static void boxHeld(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show, java.util.function.Su...` | One box on a page, given the focus and typed into (a node's `setFocused` when this scene has no window to give the focus: the line says which): a month landed through the real show method twice - the same node, its te... |
| 4290 | 32 | `static void moneyControls(Game g, KeptBench bench) throws Exception` | Section 66: the boxes keep the player's hand - Close it now's two, the ask's, the yield boxes of Borrow's and the rollover's chips, the fund's search box. |

### the hover (67) and the cost (68) (lines 4323-4644)

| line | len | member | says |
|---:|---:|---|---|
| 4326 | 29 | `static void moneyHover(Game g, KeptBench bench) throws Exception` | Section 67: the hover. |
| 4357 | 20 | `static KeptBench.Page heaviest(KeptBench bench, String tab, List<KeptBench.Page> pages) throws Exception` | The page of a tab that weighs most (the nodes its first draw makes), by name: printed with the others. |
| 4379 | 45 | `static void moneyCost(Game g, KeptBench bench) throws Exception` | Section 68: what the kept month costs on the heaviest page of the Bank, of Finances and of the fund (and the fund's Activity and Search). |
| 4426 | 19 | `static void money(Game g, KeptBench bench) throws Exception` | Sections 55 to 68: the money screens kept. |
| 4447 | 5 | `private static long took(String what, long since)` | What a stretch of the money sections took, in a line; the clock's reading for the next. |
| 4454 | 5 | `private static Notice add(Game g, String key, String title)` | A notice raised now, unread, in the inbox. |
| 4460 | 184 | `public static void main(String[] args) throws Exception` |  |


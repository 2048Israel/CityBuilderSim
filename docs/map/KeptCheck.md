# KeptCheck.java - 3,318 lines · 67 methods · 25 constants · harnesses

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
> ... (114 more lines in the source)

**Uses:** [KeptBench](KeptBench.md) (172), [Game](Game.md) (57), [Sectors](Sectors.md) (31), [Notice](Notice.md) (12), [BuildAdvice](BuildAdvice.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (11), [Timing](Timing.md) (10), [WellCheck](WellCheck.md) (6), [OilCheck](OilCheck.md) (6), [ChartModel](ChartModel.md) (4), [RefineryViewCheck](RefineryViewCheck.md) (4), [OilViewCheck](OilViewCheck.md) (4), [BuildingManager](BuildingManager.md) (3), [IndustryView](IndustryView.md) (3), [RefineryView](RefineryView.md) (3), [MiningCheck](MiningCheck.md) (3), [SectorFlow](SectorFlow.md) (2), [GameFiles](GameFiles.md) (2), [LongPlaytest](LongPlaytest.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [LandParcel](LandParcel.md) (1), [AutoBuilder](AutoBuilder.md) (1), [Auction](Auction.md) (1), [Sector](Sector.md) (1)

**Used by (1):** [ChartFitCheck](ChartFitCheck.md)

## Sections

| line | section |
|---:|---|
| 1336 | INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page |
| 1899 | BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, |
| 2488 | SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 203 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 206 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 209 | `KeptCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 212 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 307 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's cached outline a le... |
| 310 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 353 | `KeptCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 356 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 578 | `KeptCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |
| 848 | `KeptCheck.KEPT_HISTORY_MS` | `13` | The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same mon... |
| 878 | `KeptCheck.TICKED` | `"unemployment"` | The first of these lines picked and not in the preset the page opens on: a line to tick and untick. |
| 1342 | `KeptCheck.HANDED_BACK` | `0.9` | The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). |
| 1553 | `KeptCheck.KEPT_INFRASTRUCTURE_MS` | `9` | The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 betwe... |
| 1560 | `KeptCheck.KEPT_SERVICES_MS` | `13` | The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 betwe... |
| 1567 | `KeptCheck.KEPT_LAND_MS` | `5` | The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 ... |
| 1607 | `KeptCheck.SERVICES_FOLDS` | `{ { "Health/Overview", "health:sick" }, { "Health/General care", "health:gene...` | The folds each sub-page of Services has, by "system/page" (a page not named has none). |
| 1910 | `KeptCheck.KEPT_BUILD_MS` | `12` | The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a city category with two orders dialled up and a card's stats pinned (the heavier of ... |
| 1917 | `KeptCheck.KEPT_CONSTRUCTION_MS` | `4` | The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1... |
| 1920 | `KeptCheck.SITE_ROWS` | `6` | The rows the Sites tab and the timeline are kept at while Build and the construction page are watched: a site each. |
| 1923 | `KeptCheck.SITE_ORDER` | `{ "Gravel Road", "Walk-in Clinic", "Paved Road", "Bus Network", "Elementary S...` | What is ordered, in turn, to put a site on: the cheap, quick works first. |
| 2499 | `KeptCheck.KEPT_SECTORS_MS` | `18` | The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same mon... |
| 2508 | `KeptCheck.KEPT_SECTOR_MS` | `15` | The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the heaviest of the three pages costed (the shops' Operations; a plain business's Income stat... |
| 2511 | `KeptCheck.PICTURE_MONTHS` | `6` | The months the pictures are watched through (section 50): each month a repaint or not, and a page built new beside it. |
| 2517 | `KeptCheck.NOT_PLAIN` | `java.util.Set.of(Sectors.RETAIL, Sectors.RESTAURANTS, Sectors.LUXURY_RETAIL, ...` | The keys a plain business is not taken from: the shops, the kitchens, the counters, the landlords, the haulier and the fuel's two. |
| 2534 | `KeptCheck.WORKS_ORDER` | `{ "Sawmill", "Scrap Yard", "Wood Pellet Mill", "Panel Mill" }` | What a materials industry's works are made of, in turn, for a city that has none standing: a sawmill first, the rest if it cannot. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 195 | `static int fails` |  |
| 236 | `static Runnable afterMonth` | What is done after every month played, with the log off, while a fixture has to be held through the months (KP3: the city's sites); null for nothing. |
| 1926 | `private static int siteTurn` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 193 | 3126 | **type** `public class KeptCheck` | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exactly where a page built new would, and costs a fraction of it. |
| 197 | 4 | `static void check(String label, boolean ok)` |  |
| 214 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 221 | 13 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on). |
| 239 | 10 | `static void month(Game g)` | A month, as the clock runs it, its log kept off this harness's output. |
| 251 | 9 | `static<T> T quietly(Callable<T> call) throws Exception` | A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). |
| 262 | 28 | `static String firstDifference(List<String> kept, List<String> fresh)` | The first line where two signatures part, or null when they are the same. |
| 292 | 6 | `static void png(int[] px, java.nio.file.Path to) throws java.io.IOException` | A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. |
| 313 | 10 | `static boolean alike(int[] a, int[] b)` | Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. |
| 325 | 16 | `static String pixelDifference(int[] a, int[] b)` | Where two snapshots part: their sizes, or how many pixels differ and the box round them. |
| 342 | 3 | `static String clip(String s)` |  |
| 346 | 5 | `static double median(List<Double> v)` |  |
| 364 | 212 | `static void chrome(Game g, KeptBench bench) throws Exception` | Sections 6 to 10: the header and the tab rail kept (UP2). |
| 581 | 11 | `static void panelStates(KeptBench bench, int at)` | The panels' states, month by month: the city panel's mode, and which of its sections are open. |
| 597 | 245 | `static void panels(Game g, KeptBench bench) throws Exception` | Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice 2b), each on a scene of its own, beside the header and the rail of 6 to 10. |
| 852 | 3 | **type** `interface Hand` | A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). |
| 853 | 1 | `String on(KeptBench bench) throws Exception` _(in KeptCheck.Hand)_ |  |
| 862 | 9 | **type** `record Choice(String name, Hand hand, boolean opens, boolean acts)` | One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2); `acts` for a choice that makes the model act (KP3: automatic building turned on or... |
| 863 | 3 | `Choice(String name, Hand hand, boolean opens)` _(in KeptCheck.Choice)_ |  |
| 867 | 3 | `Choice(String name, Hand hand)` _(in KeptCheck.Choice)_ |  |
| 873 | 3 | `static Choice acting(String name, Hand hand)` | A choice that makes the model act (see Choice.acts). |
| 884 | 218 | `static void history(Game g, KeptBench bench) throws Exception` | Sections 14 to 20: City History kept (KP1). |
| 1104 | 7 | `private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved)` | Months landed until the figure has moved (at most twelve): whether it did. |
| 1113 | 3 | `private static String nullIfOffered(String refusal, String what)` | The choice's refusal, as the premise it could not meet, or null when the chart is offered. |
| 1123 | 77 | `static void crosshair(Game g, KeptBench bench) throws Exception` | Section 18: what a page built new loses and a kept one keeps. |
| 1207 | 96 | `static void countUp(Game g, KeptBench bench) throws Exception` | Section 19: the count-up is for a clock that can be followed. |
| 1305 | 30 | `static void historyCost(Game g, KeptBench bench) throws Exception` | Section 20: what the kept City History month costs, against the same month built new. |

### INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page (lines 1336-1898)

| line | len | member | says |
|---:|---:|---|---|
| 1345 | 12 | `static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. |
| 1359 | 32 | `static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the page is kept - a month's redraw keeps its top nodes, hands back nine in ten of the first draw's nodes, and on most months makes none. |
| 1393 | 71 | `static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception` | Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. |
| 1470 | 42 | `static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception` | Section n: every choice that reshapes the page, made in turn: the redraw after it and the one after a month lands are each equal to a page built new, and the month's redraw hands back nine in ten of the page's nodes. |
| 1514 | 28 | `static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception` | Section n: what the kept month on the page's home costs, against the same month built new. |
| 1544 | 3 | `static Choice choice(String name, Hand hand)` | A choice with no side effect on the model: a hand that changes the screen's state only. |
| 1570 | 9 | `static String ensureTransit(Game g) throws Exception` | Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. |
| 1581 | 24 | `static void infrastructure(Game g, KeptBench bench) throws Exception` | Sections 21 to 24: Infrastructure kept. |
| 1619 | 21 | `static void services(Game g, KeptBench bench) throws Exception` | Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. |
| 1642 | 49 | `static void land(Game g, KeptBench bench) throws Exception` | Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. |
| 1698 | 123 | `static void controls(Game g, KeptBench bench) throws Exception` | Section 34: a control keeps the player's hand. |
| 1823 | 64 | `static void hover(Game g, KeptBench bench) throws Exception` | Section 35: the hover kept. |
| 1889 | 9 | `private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result)` | One hover result: printed, counted as found when the page had the node, and a check that it held. |

### BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it, (lines 1899-2487)

| line | len | member | says |
|---:|---:|---|---|
| 1929 | 12 | `static boolean buyGround(Game g, BuildingsTemplate t, int n) throws Exception` | Ground bought through the model for an order that found none: the cheapest offer that covers the shortfall, the cash it lacks borrowed on Build's bond; whether it was bought. |
| 1943 | 30 | `static String ensureSites(Game g, int rows) throws Exception` | Orders through the model until the city has `rows` sites on (a row each on the Sites tab, a bar each on the timeline): what it did, in a line. |
| 1975 | 9 | `static void keepSites(Game g)` | From now every month played tops the sites up to SITE_ROWS (the fixture holds while the pages are watched), until stopSites(). |
| 1985 | 3 | `static void stopSites()` |  |
| 1990 | 137 | `static void build(Game g, KeptBench bench) throws Exception` | Build's pages: the Overview and a city category with two orders dialled up (so that its order bar shows), kept, as built new, reshaped and costed. |
| 2129 | 67 | `static void construction(Game g, KeptBench bench) throws Exception` | The construction page, with the city's sites on: the Sites tab kept, shown, reshaped by its tabs, a pick and the find box, and costed. |
| 2203 | 34 | `static void stopped(Game g, KeptBench bench) throws Exception` | Section 44: the pages a build is stopped on and the run's funding and fell-short pages, each caused where the model can cause it (a count Game.buildRunStop() refuses for the reason; a run larger than the cash) and eac... |
| 2245 | 207 | `static void buildControls(Game g, KeptBench bench) throws Exception` | Section 45: a control keeps the player's hand. |
| 2454 | 33 | `static void buildHover(Game g, KeptBench bench) throws Exception` | Section 46: the hover. |

### SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and (lines 2488-3318)

| line | len | member | says |
|---:|---:|---|---|
| 2514 | 1 | **type** `record SectorKinds(String plain, String materials)` | The businesses the Sectors sections take besides the shops, the landlords and Freight: a plain one with a plant standing, and a materials industry with works to draw. |
| 2521 | 11 | `static SectorKinds sectorKinds(Game g)` | The plain business this city has, by the model's own reading of its plants, and the materials industry its works draw: wood products, which ensureWorks() puts up a sawmill for (null when it has none standing). |
| 2543 | 6 | `static String ensureWorks(Game g) throws Exception` | Wood products' works standing in the city: the ones it has, else a sawmill put up through the model (finished at once; the city borrows what it lacks on Build's bond and buys ground where it finds none, as ensureSites... |
| 2551 | 18 | `static String putUpWorks(Game g) throws Exception` | A sawmill (or the first of WORKS_ORDER the model lets be built) put up finished: "a Sawmill put up through the model", or why none could be. |
| 2571 | 8 | `static void arrive(KeptBench bench, KeptBench.Page page) throws Exception` | A page arrived at: back on its home, drawn and laid out, as the bench's other pages are before their sections. |
| 2581 | 6 | `static Hand on(String key, String page, boolean statement)` | The hand that opens a business's page in a view. |
| 2589 | 3 | `static String called(Game g, String key)` | What a business is called on the screen. |
| 2594 | 3 | `static String possessive(String name)` | A name's possessive as the lines say it: "Retail's", "Wood Products'". |
| 2599 | 104 | `static void sectors(Game g, KeptBench bench) throws Exception` | Sections 47 to 54: the Sectors tab kept. |
| 2710 | 27 | `static void sectorsPictures(Game g, KeptBench bench, String works, String worksName) throws Exception` | Section 50: the pictures a business's page holds are kept. |
| 2744 | 74 | `static void picturesKept(Game g, KeptBench bench, KeptBench.Page page, String what, java.util.function.Function<KeptBench, List...` | What a month does to the pictures a page holds (`find` takes them off the page; `paints` for a picture that is told the month's view and paints it, as the refinery's and a materials industry's are, and not for a bar t... |
| 2820 | 10 | `static String againstNewAsIs(KeptBench bench, KeptBench.Page page) throws Exception` | The page as it stands with no animation finished first, against the same page built new: the first difference, or null. |
| 2832 | 5 | `private static boolean figuresMoved(String[][] words, boolean business)` | Whether the figures the Sectors pages count have moved between last month and this: the list's KEPT, a business's, and some card's. |
| 2839 | 6 | `private static List<javafx.scene.control.Label> countingFigures(KeptBench bench, String key)` | The figures on a Sectors page that count up: the KEPT, and on the list every card's net income. |
| 2851 | 65 | `static String countRung(Game g, KeptBench bench, String key, String page, int at, boolean paused, boolean expect) throws Exception` | One clock speed of section 51 on one page (`key` null for the list): the page is shown, a month lands and it is shown again; a count starts on each figure that moved exactly where `expect` says, the waterfall grows an... |
| 2923 | 65 | `static void sectorsCountUp(Game g, KeptBench bench, String plain) throws Exception` | Section 51: the count-up only at 1x-10x, on the list's KEPT figure and its cards', a business's KEPT, the Income page's waterfall and the Operations page's ring. |
| 2990 | 19 | `static Game seaTown() throws Exception` | The oil reserve's city: OilViewCheck's sea town (a platform with its wells, two land wells, a Strategic Reserve with FILL ordered), its fixture's report lines kept off this output. |
| 3016 | 24 | `static void sectorsControls() throws Exception` | Section 53: a control keeps the player's hand. |
| 3042 | 68 | `static void dial(Game sea, KeptBench bench, KeptBench.Page page, int which, String name, String key, java.util.function.DoubleF...` | One of the reserve's dials (the `which`th slider on the page), pressed, held across a month, released, and set outside a draw. |
| 3112 | 15 | `static void sectorsHover(Game g, KeptBench bench, String plain) throws Exception` | Section 54: the hover. |
| 3129 | 5 | `private static Notice add(Game g, String key, String title)` | A notice raised now, unread, in the inbox. |
| 3135 | 183 | `public static void main(String[] args) throws Exception` |  |


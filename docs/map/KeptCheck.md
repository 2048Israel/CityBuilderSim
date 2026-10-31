# KeptCheck.java - 1,392 lines · 22 methods · 11 constants · harnesses

`ham/citybuildersim/KeptCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> ... (35 more lines in the source)

**Uses:** [KeptBench](KeptBench.md) (23), [Notice](Notice.md) (12), [Game](Game.md) (10), [Timing](Timing.md) (8), [ChartModel](ChartModel.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [LongPlaytest](LongPlaytest.md) (2), [BuildingManager](BuildingManager.md) (1)

**Used by (1):** [ChartFitCheck](ChartFitCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 124 | `KeptCheck.CITY_MONTHS` | `600` | The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. |
| 127 | `KeptCheck.MONTHS` | `25` | The months watched, kept against new: two years and a month, so a January and a year's turn are among them. |
| 130 | `KeptCheck.KEPT_MONTH_MS` | `12` | The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. |
| 133 | `KeptCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` | Where a month's log goes while month() runs it: nowhere. |
| 202 | `KeptCheck.SHADE_LEVELS` | `64` | The most a pixel of the kept page may differ from the new page's, in any channel, out of 255: the edge of a small shape a kept picture moved to another place on the page is shaded by the renderer's cached outline a le... |
| 205 | `KeptCheck.SHADED_SHARE` | `0.001` | ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). |
| 248 | `KeptCheck.KEPT_CHROME_MS` | `4` | The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. |
| 251 | `KeptCheck.CHROME_SCREENS` | `{ "showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",...` | The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). |
| 473 | `KeptCheck.KEPT_ALL_CHROME_MS` | `8` | The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. |
| 743 | `KeptCheck.KEPT_HISTORY_MS` | `12` | The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (5.0 to 6.7 ms in four runs, 5.6 between them; the same mon... |
| 755 | `KeptCheck.TICKED` | `"unemployment"` | The first of these lines picked and not in the preset the page opens on: a line to tick and untick. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 116 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1279 | **type** `public class KeptCheck` | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exactly where a page built new would, and costs a fraction of it. |
| 118 | 4 | `static void check(String label, boolean ok)` |  |
| 135 | 5 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 142 | 13 | `static String startJavaFx()` | The toolkit started, or why not (then this harness has nothing to run on). |
| 157 | 9 | `static void month(Game g)` | A month, as the clock runs it, its log kept off this harness's output. |
| 168 | 17 | `static String firstDifference(List<String> kept, List<String> fresh)` | The first line where two signatures part, or null when they are the same. |
| 187 | 6 | `static void png(int[] px, java.nio.file.Path to) throws java.io.IOException` | A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. |
| 208 | 10 | `static boolean alike(int[] a, int[] b)` | Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. |
| 220 | 16 | `static String pixelDifference(int[] a, int[] b)` | Where two snapshots part: their sizes, or how many pixels differ and the box round them. |
| 237 | 3 | `static String clip(String s)` |  |
| 241 | 5 | `static double median(List<Double> v)` |  |
| 259 | 212 | `static void chrome(Game g, KeptBench bench) throws Exception` | Sections 6 to 10: the header and the tab rail kept (UP2). |
| 476 | 11 | `static void panelStates(KeptBench bench, int at)` | The panels' states, month by month: the city panel's mode, and which of its sections are open. |
| 492 | 245 | `static void panels(Game g, KeptBench bench) throws Exception` | Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice 2b), each on a scene of its own, beside the header and the rail of 6 to 10. |
| 747 | 3 | **type** `interface Hand` | A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). |
| 748 | 1 | `String on(KeptBench bench) throws Exception` _(in KeptCheck.Hand)_ |  |
| 752 | 1 | **type** `record Choice(String name, Hand hand)` | One choice that reshapes the page: its name and the hand that makes it. |
| 761 | 218 | `static void history(Game g, KeptBench bench) throws Exception` | Sections 14 to 20: City History kept (KP1). |
| 981 | 7 | `private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved)` | Months landed until the figure has moved (at most twelve): whether it did. |
| 990 | 3 | `private static String nullIfOffered(String refusal, String what)` | The choice's refusal, as the premise it could not meet, or null when the chart is offered. |
| 1000 | 77 | `static void crosshair(Game g, KeptBench bench) throws Exception` | Section 18: what a page built new loses and a kept one keeps. |
| 1084 | 96 | `static void countUp(Game g, KeptBench bench) throws Exception` | Section 19: the count-up is for a clock that can be followed. |
| 1182 | 30 | `static void historyCost(Game g, KeptBench bench) throws Exception` | Section 20: what the kept City History month costs, against the same month built new. |
| 1214 | 5 | `private static Notice add(Game g, String key, String title)` | A notice raised now, unread, in the inbox. |
| 1220 | 172 | `public static void main(String[] args) throws Exception` |  |


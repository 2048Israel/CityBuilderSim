# RankingsScreen.java - 1,557 lines · 73 methods · 16 constants · interface

`ham/citybuildersim/ui/RankingsScreen.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The Rankings tab: the active city on a board of the real world's nations and city regions (or of the
> nations of its own world), measure by measure, in the money and at the prices the player picked, with the
> sentence that says exactly what is on screen. The screen draws what {@link Rankings} and {@link CityMeasures}
> hand it and computes none of it.
> 
> WHY. Players ask where their city stands, and the answer is only as honest as the money it is in; the
> model (Rankings, LB2's first slice) turns every figure into real 2015 dollars once and shows it in the money
> picked, so this screen's job is to keep the choice in front of the player: four controls, one sentence under
> them, three columns. It builds spec-leaderboards 9 (the screen), 7 (the (i) and the marks) and 3 (the
> sentence, the lock on Prices) and Jerus' calls of 2026-10-21: a rail tab after History with Lucide's trophy in
> money blue (D5), the (i) and a mark on every row (D6), it opens on GDP and the full list (D9), a measure no
> real place publishes ranks only your cities and its real rows are "-" (D10), and the boards are named Real
> world 2025 and Your world (D16).
> 
> LB3 puts the player's other saved cities on the board (Your cities, Your cities at Now / Your age, a row and a drawer of their
> own, Prices open beside them: D2, D3, D5), read off the screen's thread by SavedCities, and the city's rank over the
> years on its card (D12).

**Uses:** [Palette](Palette.md) (171), [Rankings](Rankings.md) (114), [WorldMeasures](WorldMeasures.md) (58), [SavedCities](SavedCities.md) (12), [Money](Money.md) (10), [CityCalendar](CityCalendar.md) (9), [Pieces](Pieces.md) (9), [HistoryScreen](HistoryScreen.md) (9), [Nations](Nations.md) (6), [Icons](Icons.md) (5), [UserInterface](UserInterface.md) (2), [Game](Game.md) (2), [Currency](Currency.md) (2), [GameFiles](GameFiles.md) (1), [GameLog](GameLog.md) (1), [CityMeasures](CityMeasures.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 59 | THE RANKINGS TAB (LB2): THE STATE |
| 132 | THE RANKINGS TAB (LB3): YOUR SAVED CITIES |
| 276 | THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME |
| 413 | THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE |
| 522 | THE RANKINGS TAB (LB2): THE (i) |
| 557 | THE RANKINGS TAB (LB2): FIGURES AS WORDS |
| 664 | THE RANKINGS TAB (LB2): THE MEASURES COLUMN |
| 747 | THE RANKINGS TAB (LB2): THE TABLE |
| 980 | THE RANKINGS TAB (LB2): THE ROWS, DRAWN |
| 1185 | THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER |
| 1322 | · rank over the years (LB3, D12) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 91 | `RankingsScreen.Filter.ALL` |  |
| 91 | `RankingsScreen.Filter.NATIONS` |  |
| 91 | `RankingsScreen.Filter.CITIES` |  |
| 91 | `RankingsScreen.Filter.YOUR_SIZE` |  |
| 91 | `RankingsScreen.Filter.YOUR_CITIES` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `RankingsScreen.OPENS_ON` | `"gdp"` | The measure the tab opens on (D9): GDP. |
| 77 | `RankingsScreen.PINNED_AT_FIRST` | `List.of("gdp", "gdp_pp", "growth_real", "people", "unemployment", "inflation")` | The measures pinned the first time the tab opens: the six a player most often asks of a city. |
| 80 | `RankingsScreen.GROUPS` | `Map.of("economy", "Economy", "work", "Work & pay", "prices", "Prices & money"...` | The ten groups of the file, by their key, as the spec's table names them (spec-leaderboards 6). |
| 158 | `RankingsScreen.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "rankings-s...` | The one thread the saved cities are read on, away from the screen; it dies with the window. |
| 288 | `RankingsScreen.FRAME_CHROME` | `210` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 291 | `RankingsScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out: City History's. |
| 294 | `RankingsScreen.FIRST_WIDTH` | `1100, MEASURES_W = 250, CARD_W = 400, TABLE_MIN = 560, COLUMN_GAP = 16` | The page's width before the scroller says it, and the widths of the columns. |
| 770 | `RankingsScreen.RANK_W` | `40, KIND_W = 16, OF_W = 84, ASOF_W = 54, VALUE_W = 80, BAR_W = 52, MARK_W = 1...` |  |
| 999 | `RankingsScreen.ROW_H` | `24, PAD = 8, SPACE = 6` |  |
| 1002 | `RankingsScreen.TILE_ROWS` | `40` | Rows to a canvas: 40 of 24 pixels is under a thousand, well inside any texture. |
| 1007 | `RankingsScreen.Entry.GAP` | `new Entry(null, "", "", false, "", "", "", "", Double.NaN, "")` |  |
| 1014 | `RankingsScreen.RowsPicture.TEXT` | `Color.web(Palette.TEXT), HEAD = Color.web(Palette.TEXT_HEAD), BODY = Color.we...` |  |
| 1020 | `RankingsScreen.RowsPicture.RULER` | `new javafx.scene.text.Text()` | Measures a string against a column; one node, on the FX thread, where the drawing is. |
| 1200 | `RankingsScreen.D_NAME` | `128, D_REAL = 66, D_MARK = 12, D_SOURCE = 66, D_YOURS = 60` | The drawer's columns: the measure, the real figure and its mark and source, Your world's figure and its mark. |
| 1358 | `RankingsScreen.RankLine.H` | `76, SIDE = 36, TOP = 12, BOTTOM = 18, YEAR_GAP = 40` |  |
| 1360 | `RankingsScreen.RankLine.LINE` | `Color.web(Palette.MONEY), FLOOR = Color.web(Palette.EDGE), RANK = Color.web(P...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `final String label` |  |
| 98 | `private final UserInterface ui` |  |
| 100 | `private Rankings.Settings settings` |  |
| 101 | `private String measure` |  |
| 102 | `private boolean aroundYou` |  |
| 103 | `private Filter filter` |  |
| 104 | `private final Set<String> pinned` |  |
| 105 | `private String drawer` |  |
| 106 | `private boolean browseAll` |  |
| 108 | `private Rankings.City city` |  |
| 109 | `private Game cityOf` |  |
| 110 | `private String cityNamed` |  |
| 111 | `private int cityMonth` |  |
| 166 | `private volatile SavedCities saved` | The saved cities as the last read found them; null until the first read has come back. |
| 168 | `private boolean reading` | A read is on WORKER (the FX thread's flag: set when it is handed over, cleared when its answer arrives). |
| 170 | `private boolean atAge` | Your cities at: the saved cities as they were when the active city was as old as it is now (D3), else as last saved. |
| 173 | `private SavedCities onBoardOf` | The saved cities on the board, by the snapshot, the active city's month and Now / Your age they were made for; the lines about them are made with them. |
| 174 | `private int onBoardMonth` |  |
| 175 | `private boolean onBoardAtAge` |  |
| 176 | `private List<Rankings.Saved> onBoard` |  |
| 177 | `private Map<String, Rankings.Saved> onBoardById` |  |
| 178 | `private boolean savedReadable` |  |
| 179 | `private String notAtAge, leftOut` |  |
| 182 | `private List<Rankings.RankPoint> overYears` | The rank over the years, kept for the measure and the conversion it was worked out for; worked out again on entering the tab. |
| 183 | `private String overKey` |  |
| 296 | `private double pageWidth` |  |
| 297 | `private VBox columns, measuresBox, tableBox, cardBox` |  |
| 1022 | `private final List<Entry> entries` |  |
| 1023 | `private final boolean signed` |  |
| 1024 | `private final String open` |  |
| 1025 | `private final List<Canvas> tiles` |  |
| 1026 | `private final Region hover` |  |
| 1027 | `private final javafx.scene.text.Font words` |  |
| 1032 | `private double nameX, nameW, ofX, asOfX, valueX, barX, markX` | The columns' left edges and the name's room, laid from the right at the width drawn. |
| 1363 | `private final List<Rankings.RankPoint> points` |  |
| 1364 | `private final Canvas canvas` |  |
| 1365 | `private final javafx.scene.text.Font ranks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1501 | **type** `final class RankingsScreen` | The Rankings tab: the active city on a board of the real world's nations and city regions (or of the nations of its own world), measure by measure, in the money and at the prices the player picked, with the sentence t... |

### THE RANKINGS TAB (LB2): THE STATE (lines 59-131)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 7 | **type** `enum Filter` | The lists the filter chips keep; the city's own row stays in each, so the player is always on the board they are looking at. |
| 95 | 1 | `Filter(String label)` _(in RankingsScreen.Filter)_ |  |
| 113 | 1 | `RankingsScreen(UserInterface ui)` |  |
| 116 | 11 | `private Rankings.City cityNow()` | The active city at its month: made again only when the game, the city or the month is not the one it was made for. |
| 128 | 3 | `private void with(Rankings.Board board, Rankings.Money money, Rankings.Prices prices, Rankings.Convert convert)` |  |

### THE RANKINGS TAB (LB3): YOUR SAVED CITIES (lines 132-275)

| line | len | member | says |
|---:|---:|---|---|
| 186 | 17 | `private void readSaved()` | Starts a read of the saved cities on WORKER unless one is in flight; the answer redraws the tab if it is still on screen. |
| 205 | 6 | `private void arrived(SavedCities answer, boolean hadOne)` | A read's answer, on the FX thread: kept, and the tab drawn again if it is on screen and the answer is news. |
| 216 | 28 | `private void placeSaved(Rankings.City now)` | The saved cities on the board for the active city's month, and the lines under the table about the ones that are not: made again only when the snapshot, the month or Now / Your age is not what they were made for. |
| 246 | 19 | `private static String leftOutLine(SavedCities.LeftOut left)` | The quiet line about the saves the board left out: "2 saved cities from older builds are left out."; null when none was. |
| 270 | 5 | `void openSaved()` | The door in from the main menu's Compare (D5): the tab on the Your cities filter, the board and the money as the player had them. |

### THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME (lines 276-412)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 31 | `void showRankingsMenu()` |  |
| 335 | 5 | `void openOn(String placeId)` | The nation's card on the map is the door in (spec-leaderboards 9, "All its measures"): the tab opens on Real world 2025 with that place's drawer open, the measure kept. |
| 342 | 20 | `private void frameOver(VBox frame, VBox body)` | The fixed frame over a scrolling page, at the page's width; the page as tall as what is left under the frame (City History's frameOver). |
| 364 | 3 | `private static double given(double viewport)` | The page's width for a viewport this wide: all of it, from 320 up to PAGE_WIDE. |
| 369 | 5 | `private void follow(double width)` | The page laid out at a new width, without a redraw: the columns go side by side, or stack. |
| 380 | 26 | `private void arrange(double inner)` | The three columns for a page this wide: side by side from MEASURES_W + TABLE_MIN + CARD_W and the gaps; below that the card goes over the other two (it is the answer, and a player on a small window should not have to ... |
| 407 | 5 | `private static void width(Region box, double w)` |  |

### THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE (lines 413-521)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 7 | `private Node head(WorldMeasures wm, Rankings.City now)` |  |
| 442 | 32 | `private Node controls(Rankings.City now, WorldMeasures.Measure m, boolean haveNations)` |  |
| 480 | 15 | `private HBox strip(String[] names, String current, Consumer<Integer> pick)` | A strip of chips that hands the pick's index back and draws the page again. |
| 497 | 6 | `private static void grey(HBox strip, int index, String why)` | A chip greyed with its reason as its tooltip: it keeps its place and its mark if it is the one chosen, and a click does nothing. |
| 504 | 7 | `private static HBox control(String name, HBox strip)` |  |
| 513 | 8 | `private Node sentenceLine(Rankings.City now, WorldMeasures.Measure m, Nations nations)` | The sentence under the controls (spec-leaderboards 3: "the most important piece of the UI"), at body size in the head's ink. |

### THE RANKINGS TAB (LB2): THE (i) (lines 522-556)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 11 | `private String info(WorldMeasures wm)` |  |
| 543 | 1 | `private static String count(int n)` |  |
| 546 | 4 | `private static String mark(WorldMeasures wm, int mark)` | A mark as the file writes it: its symbol and its name. |
| 552 | 4 | `private static String glyph(WorldMeasures wm, int mark)` | A row's mark: the symbol alone, nothing for the city (mark 0). |

### THE RANKINGS TAB (LB2): FIGURES AS WORDS (lines 557-663)

| line | len | member | says |
|---:|---:|---|---|
| 568 | 3 | `private String symbol()` |  |
| 573 | 5 | `private String figure(WorldMeasures.Measure m, double v)` | A measure's figure as the screen writes it; "—" where there is none. |
| 580 | 24 | `static String plain(String unit, double v)` | A figure that is not money, by its unit: a percent to a place, a ratio to two, anything else abbreviated from a million. |
| 606 | 3 | `private String unitText(WorldMeasures.Measure m)` | A measure's unit as the screen names it: the money picked in place of "US$". |
| 610 | 7 | `private static String arrowWords(WorldMeasures.Measure m)` |  |
| 619 | 5 | `private String withUnit(WorldMeasures.Measure m, double v)` | A figure with its unit after it, as a sentence wants it: "US$30.8T", "12.3% of GDP", "78.4 years". |
| 626 | 5 | `private String gapText(WorldMeasures.Measure m, double gap)` | A gap between two figures of a measure: money as money, a percent in points, any other figure with its unit. |
| 632 | 3 | `private static String up(String s)` |  |
| 636 | 5 | `private static Label words(String text, int size, String colour)` |  |
| 642 | 7 | `private static Label wrapped(String text, int size, String colour)` |  |
| 651 | 5 | `private static VBox panel()` | A column's block: the panel ground, padded. |
| 658 | 5 | `private static Label caption(String text)` | A small heading inside a column. |

### THE RANKINGS TAB (LB2): THE MEASURES COLUMN (lines 664-746)

| line | len | member | says |
|---:|---:|---|---|
| 674 | 32 | `private VBox measuresColumn(WorldMeasures wm, WorldMeasures.Measure shown)` |  |
| 707 | 39 | `private Node measureLine(WorldMeasures.Measure p, WorldMeasures.Measure shown, boolean yourWorldBoard)` |  |

### THE RANKINGS TAB (LB2): THE TABLE (lines 747-979)

| line | len | member | says |
|---:|---:|---|---|
| 772 | 37 | `private VBox tableColumn(WorldMeasures wm, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 811 | 15 | `private List<Rankings.Row> filtered(Rankings.Table t)` | The rows a filter keeps: a kind's, the city's own among them (the saved cities are in Your cities and not in Nations or Cities: they are not the real world's places). |
| 828 | 8 | `private void savedNotes(VBox box)` | The quiet lines under the rows about the saved cities: the ones that stopped short of the active city's age, and the saves the board left out. |
| 838 | 5 | `private static Rankings.Table listOf(Rankings.Table t, List<Rankings.Row> rows)` | The board's table cut to a list of its rows, for Rankings.around(): the city's place in it found again. |
| 845 | 28 | `private Node filters(Rankings.Table t, Rankings.City now)` | The filter chips with the number of places each keeps, and Full list \| Around you beside them. |
| 875 | 8 | `private Node ageControl(Rankings.City now)` | Your cities at: Now \| Your age (D3), beside the Your cities chip; shown only when a saved city is readable. |
| 884 | 29 | `private void rows(VBox box, WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, List<Rankings.Row> list, List<Ranking...` |  |
| 915 | 11 | `private Entry entry(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, Rankings.Row r, double largest)` | One row of the table as the picture draws it: the figures already written. |
| 927 | 8 | `private static String kindIcon(Rankings.Kind kind)` |  |
| 936 | 19 | `private Node header()` |  |
| 957 | 9 | `private static Label fixed(String text, double w, Pos at, String style)` | A label of one width, its text clipped to it. |
| 968 | 11 | `private Node legend(WorldMeasures wm, Rankings.Table t)` | The legend under the table: the marks, each a few words, and how many of the figures are the game's own estimates. |

### THE RANKINGS TAB (LB2): THE ROWS, DRAWN (lines 980-1184)

| line | len | member | says |
|---:|---:|---|---|
| 1005 | 6 | **type** `private record Entry(String id, String rank, String kindIcon, boolean you, String name, String of, String a...` | A row of the table as the picture draws it, its figures already written; GAP stands for the rows a list skips. |
| 1009 | 1 | `boolean gap()` _(in RankingsScreen.Entry)_ |  |
| 1012 | 172 | **type** `private static final class RowsPicture extends Pane` |  |
| 1034 | 30 | `RowsPicture(List<Entry> entries, boolean signed, Consumer<String> pick, String open)` _(in RankingsScreen.RowsPicture)_ |  |
| 1065 | 4 | `private Entry rowAt(double y)` _(in RankingsScreen.RowsPicture)_ |  |
| 1071 | 6 | `private void lightRowAt(double y)` _(in RankingsScreen.RowsPicture)_ | The row under the pointer lit, unless it is the city's or the open one, which are raised already. |
| 1078 | 19 | `private void draw(double w)` _(in RankingsScreen.RowsPicture)_ |  |
| 1098 | 67 | `private void paint(GraphicsContext gc, Entry e, double y, double w)` _(in RankingsScreen.RowsPicture)_ |  |
| 1167 | 10 | `private static String fit(String text, javafx.scene.text.Font font, double room)` _(in RankingsScreen.RowsPicture)_ | A string cut to a column's room with an ellipsis, measured in the font it is drawn in. |
| 1178 | 5 | `private static double widthOf(String text, javafx.scene.text.Font font)` _(in RankingsScreen.RowsPicture)_ |  |

### THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER (lines 1185-1321)

| line | len | member | says |
|---:|---:|---|---|
| 1202 | 9 | `private VBox cardColumn(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 1212 | 5 | `private static VBox raised()` |  |
| 1218 | 47 | `private Node rankCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 1267 | 25 | `private Node neighbour(String which, Rankings.Row r, WorldMeasures.Measure m, String none)` | The place above or below the city: what it is, its name and its figure; a click opens its drawer. |
| 1294 | 27 | `private Node pinnedRank(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table shownTable, WorldMeasures.Measure p)` | The city's rank on a pinned measure, "5 of 259", on the board and the money shown; a click shows that measure. |

### rank over the years (LB3, D12) (lines 1322-1557)

| line | len | member | says |
|---:|---:|---|---|
| 1330 | 24 | `private Node rankOverYears(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m)` | The city's rank on the measure shown at each December of its history, against Real world 2025 (which holds still, so the line moves only because the city did): one point a December, rank 1 at the top, the years along ... |
| 1356 | 82 | **type** `private static final class RankLine extends Pane` | The rank over the years drawn on one canvas: no nodes to the point, and it draws again when its width moves. |
| 1367 | 10 | `RankLine(List<Rankings.RankPoint> points)` _(in RankingsScreen.RankLine)_ |  |
| 1378 | 59 | `private void draw(double w)` _(in RankingsScreen.RankLine)_ |  |
| 1439 | 55 | `private Node drawerCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t)` |  |
| 1496 | 9 | `private String placeName(WorldMeasures wm, Rankings.City now, Rankings.Table t)` | A place's name: the city's, a saved city's, the file's, or the row's on the board (a nation of the game's world the file does not know). |
| 1507 | 29 | `private Node drawerLine(WorldMeasures wm, Rankings.City owner, Rankings.Line line)` | One measure in the drawer; owner is the city whose figure it is (the active city or a saved one: its own words for how it was worked out), null for a place of the file. |
| 1538 | 10 | `private static String sourceName(WorldMeasures wm, String id)` | A source's name in a few words: the game's world and the city by what they are, a published source by its name up to the first comma. |
| 1550 | 7 | `private static String sourceFull(WorldMeasures wm, String id)` | ...and in full, for the tooltip: its name and its licence. |


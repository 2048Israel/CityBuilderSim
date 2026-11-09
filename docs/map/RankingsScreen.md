# RankingsScreen.java - 1,773 lines · 81 methods · 18 constants · interface

`ham/citybuildersim/ui/RankingsScreen.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (167), [Rankings](Rankings.md) (116), [Kept](Kept.md) (104), [WorldMeasures](WorldMeasures.md) (58), [SavedCities](SavedCities.md) (15), [Pieces](Pieces.md) (11), [Money](Money.md) (10), [CityCalendar](CityCalendar.md) (9), [HistoryScreen](HistoryScreen.md) (9), [Nations](Nations.md) (6), [Icons](Icons.md) (5), [UserInterface](UserInterface.md) (2), [Game](Game.md) (2), [Currency](Currency.md) (2), [GameFiles](GameFiles.md) (1), [GameLog](GameLog.md) (1), [CityMeasures](CityMeasures.md) (1)

**Used by (2):** [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 58 | THE RANKINGS TAB (LB2): THE STATE |
| 143 | THE RANKINGS TAB (LB3): YOUR SAVED CITIES |
| 304 | THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME |
| 483 | THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE |
| 615 | THE RANKINGS TAB (LB2): THE (i) |
| 650 | THE RANKINGS TAB (LB2): FIGURES AS WORDS |
| 763 | THE RANKINGS TAB (LB2): THE MEASURES COLUMN |
| 858 | THE RANKINGS TAB (LB2): THE TABLE |
| 1119 | THE RANKINGS TAB (LB2): THE ROWS, DRAWN |
| 1384 | THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER |
| 1524 | · rank over the years (LB3, D12) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 90 | `RankingsScreen.Filter.ALL` |  |
| 90 | `RankingsScreen.Filter.NATIONS` |  |
| 90 | `RankingsScreen.Filter.CITIES` |  |
| 90 | `RankingsScreen.Filter.YOUR_SIZE` |  |
| 90 | `RankingsScreen.Filter.YOUR_CITIES` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `RankingsScreen.OPENS_ON` | `"gdp"` | The measure the tab opens on (D9): GDP. |
| 76 | `RankingsScreen.PINNED_AT_FIRST` | `List.of("gdp", "gdp_pp", "growth_real", "people", "unemployment", "inflation")` | The measures pinned the first time the tab opens: the six a player most often asks of a city. |
| 79 | `RankingsScreen.GROUPS` | `Map.of("economy", "Economy", "work", "Work & pay", "prices", "Prices & money"...` | The ten groups of the file, by their key, as the spec's table names them (spec-leaderboards 6). |
| 170 | `RankingsScreen.WORKER` | `Executors.newSingleThreadExecutor(r -> { Thread t = new Thread(r, "rankings-s...` | The one thread the saved cities are read on, away from the screen; it dies with the window. |
| 316 | `RankingsScreen.FRAME_CHROME` | `210` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 319 | `RankingsScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out: City History's. |
| 322 | `RankingsScreen.FIRST_WIDTH` | `1100, MEASURES_W = 250, CARD_W = 400, TABLE_MIN = 560, COLUMN_GAP = 16` | The page's width before the scroller says it, and the widths of the columns. |
| 395 | `RankingsScreen.BOUND_TO_FRAME` | `"rankings.frame", BOUND_TO_SCROLLER = "rankings.scroller", FOLLOWS = "ranking...` | The keys in a node's properties for what frameOver() has bound it to or added to it: the scroller's height to the frame, a box's width to the scroller, the scroller's width listener. |
| 813 | `RankingsScreen.PIN_ROOM` | `new javafx.geometry.Insets(0, 2, 0, 4), BROWSE_ROOM = new javafx.geometry.Ins...` | The room round a measure line's pin, and over Browse all: one object each (KP2b). |
| 881 | `RankingsScreen.RANK_W` | `40, KIND_W = 16, OF_W = 84, ASOF_W = 54, VALUE_W = 80, BAR_W = 52, MARK_W = 1...` |  |
| 1138 | `RankingsScreen.ROW_H` | `24, PAD = 8, SPACE = 6` |  |
| 1141 | `RankingsScreen.TILE_ROWS` | `40` | Rows to a canvas: 40 of 24 pixels is under a thousand, well inside any texture. |
| 1146 | `RankingsScreen.Entry.GAP` | `new Entry(null, "", "", false, "", "", "", "", Double.NaN, "")` |  |
| 1153 | `RankingsScreen.RowsPicture.TEXT` | `Color.web(Palette.TEXT), HEAD = Color.web(Palette.TEXT_HEAD), BODY = Color.we...` |  |
| 1159 | `RankingsScreen.RowsPicture.RULER` | `new javafx.scene.text.Text()` | Measures a string against a column; one node, on the FX thread, where the drawing is. |
| 1399 | `RankingsScreen.D_NAME` | `128, D_REAL = 66, D_MARK = 12, D_SOURCE = 66, D_YOURS = 60` | The drawer's columns: the measure, the real figure and its mark and source, Your world's figure and its mark. |
| 1564 | `RankingsScreen.RankLine.H` | `76, SIDE = 36, TOP = 12, BOTTOM = 18, YEAR_GAP = 40` |  |
| 1566 | `RankingsScreen.RankLine.LINE` | `Color.web(Palette.MONEY), FLOOR = Color.web(Palette.EDGE), RANK = Color.web(P...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 92 | `final String label` |  |
| 97 | `private final UserInterface ui` |  |
| 100 | `final Kept.Pool pool` | The nodes this screen made at its last draw, handed back to it at the next (KP7; study UP4, docs/studies/spec-screens-in-place.md). |
| 111 | `Rankings.Settings settings` | (KP7: the choices are package-private, for KeptBench's hands: what a click on a chip, a line or a row would set.) |
| 112 | `String measure` |  |
| 113 | `boolean aroundYou` |  |
| 114 | `Filter filter` |  |
| 115 | `final Set<String> pinned` |  |
| 116 | `String drawer` |  |
| 117 | `boolean browseAll` |  |
| 119 | `private Rankings.City city` |  |
| 120 | `private Game cityOf` |  |
| 121 | `private String cityNamed` |  |
| 122 | `private int cityMonth` |  |
| 178 | `volatile SavedCities saved` | The saved cities as the last read found them; null until the first read has come back. |
| 180 | `boolean reading` | A read is on WORKER (the FX thread's flag: set when it is handed over, cleared when its answer arrives). |
| 182 | `private boolean readAgain` | A read was asked for while one was on WORKER (an entry, Compare): it starts when that one's answer arrives, so a slot saved meanwhile is not missed. |
| 184 | `boolean atAge` | Your cities at: the saved cities as they were when the active city was as old as it is now (D3), else as last saved. |
| 190 | `private SavedCities onBoardOf` | The saved cities on the board, by the snapshot, the active city's month and Now / Your age they were made for, and the city in play (LB4: a load from a slot leaves the snapshot and the month as they were, and changes ... |
| 191 | `private int onBoardMonth` |  |
| 192 | `private boolean onBoardAtAge` |  |
| 193 | `private SavedCities.Played onBoardPlayed` |  |
| 194 | `private List<Rankings.Saved> onBoard` |  |
| 195 | `private Map<String, Rankings.Saved> onBoardById` |  |
| 196 | `private boolean savedReadable` |  |
| 197 | `private String notAtAge, leftOut` |  |
| 200 | `private List<Rankings.RankPoint> overYears` | The rank over the years, kept for the measure and the conversion it was worked out for; worked out again on entering the tab. |
| 201 | `private String overKey` |  |
| 324 | `private double pageWidth` |  |
| 325 | `private VBox columns, measuresBox, tableBox, cardBox` |  |
| 327 | `private HBox across, pair` | The two rows the columns go across in (KP7): made at every draw and kept, empty when not in use, so a width that moves moves the boxes between them and makes no node. |
| 1162 | `private List<Entry> entries` | What it draws now, told by set() (KP7: a picture is kept and told its figures, not made again for them). |
| 1163 | `private boolean signed` |  |
| 1164 | `private String open` |  |
| 1165 | `private final List<Canvas> tiles` |  |
| 1166 | `private final Region hover` |  |
| 1173 | `private double pointerSceneY` | Where the pointer was last over the rows, in the scene (NaN when it is not over them), so that the row it is over is worked out from where it is and not from a place in the picture: the page scrolls under a still poin... |
| 1174 | `private final javafx.scene.text.Font words` |  |
| 1179 | `private double nameX, nameW, ofX, asOfX, valueX, barX, markX` | The columns' left edges and the name's room, laid from the right at the width drawn. |
| 1570 | `private List<Rankings.RankPoint> points` | The points it draws now, told by set() (KP7: a picture is kept and told its figures). |
| 1571 | `private final Canvas canvas` |  |
| 1572 | `private final javafx.scene.text.Font ranks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 1718 | **type** `final class RankingsScreen` | The Rankings tab: the active city on a board of the real world's nations and city regions (or of the nations of its own world), measure by measure, in the money and at the prices the player picked, with the sentence t... |

### THE RANKINGS TAB (LB2): THE STATE (lines 58-142)

| line | len | member | says |
|---:|---:|---|---|
| 89 | 7 | **type** `enum Filter` | The lists the filter chips keep; the city's own row stays in each, so the player is always on the board they are looking at. |
| 94 | 1 | `Filter(String label)` _(in RankingsScreen.Filter)_ |  |
| 103 | 6 | `void forget()` | Every node is made new at the next draw: another city opened (UserInterface.anotherCity()). |
| 124 | 1 | `RankingsScreen(UserInterface ui)` |  |
| 127 | 11 | `private Rankings.City cityNow()` | The active city at its month: made again only when the game, the city or the month is not the one it was made for. |
| 139 | 3 | `private void with(Rankings.Board board, Rankings.Money money, Rankings.Prices prices, Rankings.Convert convert)` |  |

### THE RANKINGS TAB (LB3): YOUR SAVED CITIES (lines 143-303)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 20 | `void readSaved()` | Starts a read of the saved cities on WORKER, or, with one in flight, queues one more for when it is back; the answer redraws the tab if it is still on screen. |
| 226 | 10 | `private void arrived(SavedCities answer, boolean hadOne)` | A read's answer, on the FX thread: kept, and the tab drawn again if it is on screen and the answer is news. |
| 241 | 31 | `private void placeSaved(Rankings.City now)` | The saved cities on the board for the active city's month, and the lines under the table about the ones that are not: made again only when the snapshot, the month or Now / Your age is not what they were made for. |
| 274 | 19 | `private static String leftOutLine(SavedCities.LeftOut left)` | The quiet line about the saves the board left out: "2 saved cities from older builds are left out."; null when none was. |
| 298 | 5 | `void openSaved()` | The door in from the main menu's Compare (D5): the tab on the Your cities filter, the board and the money as the player had them. |

### THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME (lines 304-482)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 11 | `void showRankingsMenu()` |  |
| 347 | 36 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck. |
| 388 | 5 | `void openOn(String placeId)` | The nation's card on the map is the door in (spec-leaderboards 9, "All its measures"): the tab opens on Real world 2025 with that place's drawer open, the measure kept. |
| 398 | 32 | `private void frameOver(VBox frame, VBox body)` | The fixed frame over a scrolling page, at the page's width; the page as tall as what is left under the frame (City History's frameOver). |
| 432 | 3 | `private static double given(double viewport)` | The page's width for a viewport this wide: all of it, from 320 up to PAGE_WIDE. |
| 437 | 5 | `private void follow(double width)` | The page laid out at a new width, without a redraw: the columns go side by side, or stack. |
| 448 | 28 | `private void arrange(double inner)` | The three columns for a page this wide: side by side from MEASURES_W + TABLE_MIN + CARD_W and the gaps; below that the card goes over the other two (it is the answer, and a player on a small window should not have to ... |
| 477 | 5 | `private static void width(Region box, double w)` |  |

### THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE (lines 483-614)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 7 | `private Node head(WorldMeasures wm, Rankings.City now)` |  |
| 512 | 43 | `private Node controls(Rankings.City now, WorldMeasures.Measure m, boolean haveNations)` |  |
| 561 | 3 | `private HBox strip(String[] names, String current, Consumer<Integer> pick)` | A strip of chips that hands the pick's index back and draws the page again. |
| 569 | 19 | `private HBox strip(String[] names, String current, java.util.function.Predicate<String> spent, Consumer<Integer> pick)` | ...with the chips `spent` names greyed where they are made (KP2b: Pieces.chipStripSaying's spent look, whole, in the one write; a style set on top of the chip's was two writes at each draw, and the first of them took ... |
| 593 | 3 | `private static void grey(HBox strip, int index, String why)` | A chip greyed (strip() made it so: the spent look, its mark kept if it is the one chosen, and a click that does nothing) says why in its tooltip. |
| 597 | 7 | `private static HBox control(String name, HBox strip)` |  |
| 606 | 8 | `private Node sentenceLine(Rankings.City now, WorldMeasures.Measure m, Nations nations)` | The sentence under the controls (spec-leaderboards 3: "the most important piece of the UI"), at body size in the head's ink. |

### THE RANKINGS TAB (LB2): THE (i) (lines 615-649)

| line | len | member | says |
|---:|---:|---|---|
| 624 | 11 | `private String info(WorldMeasures wm)` |  |
| 636 | 1 | `private static String count(int n)` |  |
| 639 | 4 | `private static String mark(WorldMeasures wm, int mark)` | A mark as the file writes it: its symbol and its name. |
| 645 | 4 | `private static String glyph(WorldMeasures wm, int mark)` | A row's mark: the symbol alone, nothing for the city (mark 0). |

### THE RANKINGS TAB (LB2): FIGURES AS WORDS (lines 650-762)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 3 | `private String symbol()` |  |
| 666 | 5 | `private String figure(WorldMeasures.Measure m, double v)` | A measure's figure as the screen writes it; "—" where there is none. |
| 673 | 24 | `static String plain(String unit, double v)` | A figure that is not money, by its unit: a percent to a place, a ratio to two, anything else abbreviated from a million. |
| 699 | 3 | `private String unitText(WorldMeasures.Measure m)` | A measure's unit as the screen names it: the money picked in place of "US$". |
| 703 | 7 | `private static String arrowWords(WorldMeasures.Measure m)` |  |
| 712 | 5 | `private String withUnit(WorldMeasures.Measure m, double v)` | A figure with its unit after it, as a sentence wants it: "US$30.8T", "12.3% of GDP", "78.4 years". |
| 719 | 5 | `private String gapText(WorldMeasures.Measure m, double gap)` | A gap between two figures of a measure: money as money, a percent in points, any other figure with its unit. |
| 725 | 3 | `private static String up(String s)` |  |
| 729 | 5 | `private static Label words(String text, int size, String colour)` |  |
| 735 | 3 | `private static Label wrapped(String text, int size, String colour)` |  |
| 740 | 8 | `private static Label wrapped(String text, String style)` | ...in a style of the caller's own (KP7: set once, whole, so that a label wanting more than the words' look builds it in one go). |
| 750 | 5 | `private static VBox panel(double spacing)` | A column's block: the panel ground, padded. |
| 757 | 5 | `private static Label caption(String text)` | A small heading inside a column. |

### THE RANKINGS TAB (LB2): THE MEASURES COLUMN (lines 763-857)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 35 | `private VBox measuresColumn(WorldMeasures wm, WorldMeasures.Measure shown)` |  |
| 815 | 42 | `private Node measureLine(WorldMeasures.Measure p, WorldMeasures.Measure shown, boolean yourWorldBoard)` |  |

### THE RANKINGS TAB (LB2): THE TABLE (lines 858-1118)

| line | len | member | says |
|---:|---:|---|---|
| 883 | 46 | `private VBox tableColumn(WorldMeasures wm, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 931 | 15 | `private List<Rankings.Row> filtered(Rankings.Table t)` | The rows a filter keeps: a kind's, the city's own among them (the saved cities are in Your cities and not in Nations or Cities: they are not the real world's places). |
| 948 | 11 | `private void savedNotes(VBox box)` | The quiet lines under the rows about the saved cities: the ones that stopped short of the active city's age, and the saves the board left out. |
| 961 | 5 | `private static Rankings.Table listOf(Rankings.Table t, List<Rankings.Row> rows)` | The board's table cut to a list of its rows, for Rankings.around(): the city's place in it found again. |
| 968 | 28 | `private Node filters(Rankings.Table t, Rankings.City now)` | The filter chips with the number of places each keeps, and Full list \| Around you beside them. |
| 998 | 8 | `private Node ageControl(Rankings.City now)` | Your cities at: Now \| Your age (D3), beside the Your cities chip; shown only when a saved city is readable. |
| 1007 | 37 | `private void rows(VBox box, WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, List<Rankings.Row> list, List<Ranking...` |  |
| 1046 | 11 | `private Entry entry(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, Rankings.Row r, double largest)` | One row of the table as the picture draws it: the figures already written. |
| 1058 | 8 | `private static String kindIcon(Rankings.Kind kind)` |  |
| 1067 | 19 | `private Node header()` |  |
| 1088 | 9 | `private static Label fixed(String text, double w, Pos at, String style)` | A label of one width, its text clipped to it. |
| 1099 | 3 | `private static Label fixedMaybe(String role, String text, double w, Pos at, String style)` | ...for a cell whose words may be none: one scope while there are words, another while there are not (KP7: an emptied label keeps the height of the words it had). |
| 1104 | 14 | `private Node legend(WorldMeasures wm, Rankings.Table t)` | The legend under the table: the marks, each a few words, and how many of the figures are the game's own estimates. |

### THE RANKINGS TAB (LB2): THE ROWS, DRAWN (lines 1119-1383)

| line | len | member | says |
|---:|---:|---|---|
| 1144 | 6 | **type** `private record Entry(String id, String rank, String kindIcon, boolean you, String name, String of, String a...` | A row of the table as the picture draws it, its figures already written; GAP stands for the rows a list skips. |
| 1148 | 1 | `boolean gap()` _(in RankingsScreen.Entry)_ |  |
| 1151 | 232 | **type** `private static final class RowsPicture extends Pane` |  |
| 1186 | 16 | `RowsPicture()` _(in RankingsScreen.RowsPicture)_ | The hover's region, the hand, and the width and scroll listeners, made once in the picture's life; the rows come with set(). |
| 1207 | 49 | `void set(List<Entry> entries, boolean signed, Consumer<String> pick, String open)` _(in RankingsScreen.RowsPicture)_ | The rows it draws now, and what a click on one does. |
| 1258 | 5 | `private void lightUnderPointer()` _(in RankingsScreen.RowsPicture)_ | The row the pointer is over now lit, from where it last was in the scene (the rows may have moved since), and none when it is not over them. |
| 1264 | 4 | `private Entry rowAt(double y)` _(in RankingsScreen.RowsPicture)_ |  |
| 1270 | 6 | `private void lightRowAt(double y)` _(in RankingsScreen.RowsPicture)_ | The row under the pointer lit, unless it is the city's or the open one, which are raised already. |
| 1277 | 19 | `private void draw(double w)` _(in RankingsScreen.RowsPicture)_ |  |
| 1297 | 67 | `private void paint(GraphicsContext gc, Entry e, double y, double w)` _(in RankingsScreen.RowsPicture)_ |  |
| 1366 | 10 | `private static String fit(String text, javafx.scene.text.Font font, double room)` _(in RankingsScreen.RowsPicture)_ | A string cut to a column's room with an ellipsis, measured in the font it is drawn in. |
| 1377 | 5 | `private static double widthOf(String text, javafx.scene.text.Font font)` _(in RankingsScreen.RowsPicture)_ |  |

### THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER (lines 1384-1523)

| line | len | member | says |
|---:|---:|---|---|
| 1401 | 10 | `private VBox cardColumn(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 1412 | 5 | `private static VBox raised()` |  |
| 1418 | 49 | `private Node rankCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 1469 | 25 | `private Node neighbour(String which, Rankings.Row r, WorldMeasures.Measure m, String none)` | The place above or below the city: what it is, its name and its figure; a click opens its drawer. |
| 1496 | 27 | `private Node pinnedRank(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table shownTable, WorldMeasures.Measure p)` | The city's rank on a pinned measure, "5 of 259", on the board and the money shown; a click shows that measure. |

### rank over the years (LB3, D12) (lines 1524-1773)

| line | len | member | says |
|---:|---:|---|---|
| 1532 | 28 | `private Node rankOverYears(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m)` | The city's rank on the measure shown at each December of its history, against Real world 2025 (which holds still, so the line moves only because the city did): one point a December, rank 1 at the top, the years along ... |
| 1562 | 90 | **type** `private static final class RankLine extends Pane` | The rank over the years drawn on one canvas: no nodes to the point, and it draws again when its width moves. |
| 1575 | 9 | `RankLine()` _(in RankingsScreen.RankLine)_ | The canvas and the width listener, made once in the picture's life; the points come with set(). |
| 1586 | 5 | `void set(List<Rankings.RankPoint> points)` _(in RankingsScreen.RankLine)_ | The points it draws now: painted again only when they are not the ones it was told last. |
| 1592 | 59 | `private void draw(double w)` _(in RankingsScreen.RankLine)_ |  |
| 1653 | 56 | `private Node drawerCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t)` |  |
| 1711 | 9 | `private String placeName(WorldMeasures wm, Rankings.City now, Rankings.Table t)` | A place's name: the city's, a saved city's, the file's, or the row's on the board (a nation of the game's world the file does not know). |
| 1722 | 30 | `private Node drawerLine(WorldMeasures wm, Rankings.City owner, Rankings.Line line)` | One measure in the drawer; owner is the city whose figure it is (the active city or a saved one: its own words for how it was worked out), null for a place of the file. |
| 1754 | 10 | `private static String sourceName(WorldMeasures wm, String id)` | A source's name in a few words: the game's world and the city by what they are, a published source by its name up to the first comma. |
| 1766 | 7 | `private static String sourceFull(WorldMeasures wm, String id)` | ...and in full, for the tooltip: its name and its licence. |


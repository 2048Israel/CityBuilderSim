# RankingsScreen.java - 1,209 lines · 63 methods · 13 constants · interface

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

**Uses:** [Palette](Palette.md) (155), [Rankings](Rankings.md) (84), [WorldMeasures](WorldMeasures.md) (56), [Money](Money.md) (10), [Pieces](Pieces.md) (9), [Nations](Nations.md) (6), [HistoryScreen](HistoryScreen.md) (6), [Icons](Icons.md) (4), [UserInterface](UserInterface.md) (2), [Game](Game.md) (2), [CityCalendar](CityCalendar.md) (2), [Currency](Currency.md) (2), [CityMeasures](CityMeasures.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 50 | THE RANKINGS TAB (LB2): THE STATE |
| 119 | THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME |
| 249 | THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE |
| 356 | THE RANKINGS TAB (LB2): THE (i) |
| 391 | THE RANKINGS TAB (LB2): FIGURES AS WORDS |
| 498 | THE RANKINGS TAB (LB2): THE MEASURES COLUMN |
| 581 | THE RANKINGS TAB (LB2): THE TABLE |
| 767 | THE RANKINGS TAB (LB2): THE ROWS, DRAWN |
| 972 | THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER |

## Enum constants

| line | constant | says |
|---:|---|---|
| 78 | `RankingsScreen.Filter.ALL` |  |
| 78 | `RankingsScreen.Filter.NATIONS` |  |
| 78 | `RankingsScreen.Filter.CITIES` |  |
| 78 | `RankingsScreen.Filter.YOUR_SIZE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `RankingsScreen.OPENS_ON` | `"gdp"` | The measure the tab opens on (D9): GDP. |
| 68 | `RankingsScreen.PINNED_AT_FIRST` | `List.of("gdp", "gdp_pp", "growth_real", "people", "unemployment", "inflation")` | The measures pinned the first time the tab opens: the six a player most often asks of a city. |
| 71 | `RankingsScreen.GROUPS` | `Map.of("economy", "Economy", "work", "Work & pay", "prices", "Prices & money"...` | The ten groups of the file, by their key, as the spec's table names them (spec-leaderboards 6). |
| 131 | `RankingsScreen.FRAME_CHROME` | `210` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 134 | `RankingsScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out: City History's. |
| 137 | `RankingsScreen.FIRST_WIDTH` | `1100, MEASURES_W = 250, CARD_W = 400, TABLE_MIN = 560, COLUMN_GAP = 16` | The page's width before the scroller says it, and the widths of the columns. |
| 604 | `RankingsScreen.RANK_W` | `40, KIND_W = 16, OF_W = 84, ASOF_W = 54, VALUE_W = 80, BAR_W = 52, MARK_W = 1...` |  |
| 786 | `RankingsScreen.ROW_H` | `24, PAD = 8, SPACE = 6` |  |
| 789 | `RankingsScreen.TILE_ROWS` | `40` | Rows to a canvas: 40 of 24 pixels is under a thousand, well inside any texture. |
| 794 | `RankingsScreen.Entry.GAP` | `new Entry(null, "", "", false, "", "", "", "", Double.NaN, "")` |  |
| 801 | `RankingsScreen.RowsPicture.TEXT` | `Color.web(Palette.TEXT), HEAD = Color.web(Palette.TEXT_HEAD), BODY = Color.we...` |  |
| 807 | `RankingsScreen.RowsPicture.RULER` | `new javafx.scene.text.Text()` | Measures a string against a column; one node, on the FX thread, where the drawing is. |
| 985 | `RankingsScreen.D_NAME` | `128, D_REAL = 66, D_MARK = 12, D_SOURCE = 66, D_YOURS = 60` | The drawer's columns: the measure, the real figure and its mark and source, Your world's figure and its mark. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 80 | `final String label` |  |
| 85 | `private final UserInterface ui` |  |
| 87 | `private Rankings.Settings settings` |  |
| 88 | `private String measure` |  |
| 89 | `private boolean aroundYou` |  |
| 90 | `private Filter filter` |  |
| 91 | `private final Set<String> pinned` |  |
| 92 | `private String drawer` |  |
| 93 | `private boolean browseAll` |  |
| 95 | `private Rankings.City city` |  |
| 96 | `private Game cityOf` |  |
| 97 | `private String cityNamed` |  |
| 98 | `private int cityMonth` |  |
| 139 | `private double pageWidth` |  |
| 140 | `private VBox columns, measuresBox, tableBox, cardBox` |  |
| 809 | `private final List<Entry> entries` |  |
| 810 | `private final boolean signed` |  |
| 811 | `private final String open` |  |
| 812 | `private final List<Canvas> tiles` |  |
| 813 | `private final Region hover` |  |
| 814 | `private final javafx.scene.text.Font words` |  |
| 819 | `private double nameX, nameW, ofX, asOfX, valueX, barX, markX` | The columns' left edges and the name's room, laid from the right at the width drawn. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 1162 | **type** `final class RankingsScreen` | The Rankings tab: the active city on a board of the real world's nations and city regions (or of the nations of its own world), measure by measure, in the money and at the prices the player picked, with the sentence t... |

### THE RANKINGS TAB (LB2): THE STATE (lines 50-118)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 7 | **type** `enum Filter` | The lists the filter chips keep; the city's own row stays in each, so the player is always on the board they are looking at. |
| 82 | 1 | `Filter(String label)` _(in RankingsScreen.Filter)_ |  |
| 100 | 1 | `RankingsScreen(UserInterface ui)` |  |
| 103 | 11 | `private Rankings.City cityNow()` | The active city at its month: made again only when the game, the city or the month is not the one it was made for. |
| 115 | 3 | `private void with(Rankings.Board board, Rankings.Money money, Rankings.Prices prices, Rankings.Convert convert)` |  |

### THE RANKINGS TAB (LB2): THE DOOR AND THE FRAME (lines 119-248)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 24 | `void showRankingsMenu()` |  |
| 171 | 5 | `void openOn(String placeId)` | The nation's card on the map is the door in (spec-leaderboards 9, "All its measures"): the tab opens on Real world 2025 with that place's drawer open, the measure kept. |
| 178 | 20 | `private void frameOver(VBox frame, VBox body)` | The fixed frame over a scrolling page, at the page's width; the page as tall as what is left under the frame (City History's frameOver). |
| 200 | 3 | `private static double given(double viewport)` | The page's width for a viewport this wide: all of it, from 320 up to PAGE_WIDE. |
| 205 | 5 | `private void follow(double width)` | The page laid out at a new width, without a redraw: the columns go side by side, or stack. |
| 216 | 26 | `private void arrange(double inner)` | The three columns for a page this wide: side by side from MEASURES_W + TABLE_MIN + CARD_W and the gaps; below that the card goes over the other two (it is the answer, and a player on a small window should not have to ... |
| 243 | 5 | `private static void width(Region box, double w)` |  |

### THE RANKINGS TAB (LB2): THE HEAD, THE CONTROLS AND THE SENTENCE (lines 249-355)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 7 | `private Node head(WorldMeasures wm, Rankings.City now)` |  |
| 277 | 31 | `private Node controls(Rankings.City now, WorldMeasures.Measure m, boolean haveNations)` |  |
| 314 | 15 | `private HBox strip(String[] names, String current, Consumer<Integer> pick)` | A strip of chips that hands the pick's index back and draws the page again. |
| 331 | 6 | `private static void grey(HBox strip, int index, String why)` | A chip greyed with its reason as its tooltip: it keeps its place and its mark if it is the one chosen, and a click does nothing. |
| 338 | 7 | `private static HBox control(String name, HBox strip)` |  |
| 347 | 8 | `private Node sentenceLine(Rankings.City now, WorldMeasures.Measure m, Nations nations)` | The sentence under the controls (spec-leaderboards 3: "the most important piece of the UI"), at body size in the head's ink. |

### THE RANKINGS TAB (LB2): THE (i) (lines 356-390)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 11 | `private String info(WorldMeasures wm)` |  |
| 377 | 1 | `private static String count(int n)` |  |
| 380 | 4 | `private static String mark(WorldMeasures wm, int mark)` | A mark as the file writes it: its symbol and its name. |
| 386 | 4 | `private static String glyph(WorldMeasures wm, int mark)` | A row's mark: the symbol alone, nothing for the city (mark 0). |

### THE RANKINGS TAB (LB2): FIGURES AS WORDS (lines 391-497)

| line | len | member | says |
|---:|---:|---|---|
| 402 | 3 | `private String symbol()` |  |
| 407 | 5 | `private String figure(WorldMeasures.Measure m, double v)` | A measure's figure as the screen writes it; "—" where there is none. |
| 414 | 24 | `static String plain(String unit, double v)` | A figure that is not money, by its unit: a percent to a place, a ratio to two, anything else abbreviated from a million. |
| 440 | 3 | `private String unitText(WorldMeasures.Measure m)` | A measure's unit as the screen names it: the money picked in place of "US$". |
| 444 | 7 | `private static String arrowWords(WorldMeasures.Measure m)` |  |
| 453 | 5 | `private String withUnit(WorldMeasures.Measure m, double v)` | A figure with its unit after it, as a sentence wants it: "US$30.8T", "12.3% of GDP", "78.4 years". |
| 460 | 5 | `private String gapText(WorldMeasures.Measure m, double gap)` | A gap between two figures of a measure: money as money, a percent in points, any other figure with its unit. |
| 466 | 3 | `private static String up(String s)` |  |
| 470 | 5 | `private static Label words(String text, int size, String colour)` |  |
| 476 | 7 | `private static Label wrapped(String text, int size, String colour)` |  |
| 485 | 5 | `private static VBox panel()` | A column's block: the panel ground, padded. |
| 492 | 5 | `private static Label caption(String text)` | A small heading inside a column. |

### THE RANKINGS TAB (LB2): THE MEASURES COLUMN (lines 498-580)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 32 | `private VBox measuresColumn(WorldMeasures wm, WorldMeasures.Measure shown)` |  |
| 541 | 39 | `private Node measureLine(WorldMeasures.Measure p, WorldMeasures.Measure shown, boolean yourWorldBoard)` |  |

### THE RANKINGS TAB (LB2): THE TABLE (lines 581-766)

| line | len | member | says |
|---:|---:|---|---|
| 606 | 29 | `private VBox tableColumn(WorldMeasures wm, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 637 | 10 | `private List<Rankings.Row> filtered(Rankings.Table t)` | The rows a filter keeps: a kind's, the city's own among them. |
| 649 | 5 | `private static Rankings.Table listOf(Rankings.Table t, List<Rankings.Row> rows)` | The board's table cut to a list of its rows, for Rankings.around(): the city's place in it found again. |
| 656 | 19 | `private Node filters(Rankings.Table t)` | The filter chips with the number of places each keeps, and Full list \| Around you beside them. |
| 676 | 29 | `private void rows(VBox box, WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, List<Rankings.Row> list, List<Ranking...` |  |
| 707 | 7 | `private Entry entry(WorldMeasures wm, Rankings.City now, WorldMeasures.Measure m, Rankings.Row r, double largest)` | One row of the table as the picture draws it: the figures already written. |
| 715 | 7 | `private static String kindIcon(Rankings.Kind kind)` |  |
| 723 | 19 | `private Node header()` |  |
| 744 | 9 | `private static Label fixed(String text, double w, Pos at, String style)` | A label of one width, its text clipped to it. |
| 755 | 11 | `private Node legend(WorldMeasures wm, Rankings.Table t)` | The legend under the table: the marks, each a few words, and how many of the figures are the game's own estimates. |

### THE RANKINGS TAB (LB2): THE ROWS, DRAWN (lines 767-971)

| line | len | member | says |
|---:|---:|---|---|
| 792 | 6 | **type** `private record Entry(String id, String rank, String kindIcon, boolean you, String name, String of, String a...` | A row of the table as the picture draws it, its figures already written; GAP stands for the rows a list skips. |
| 796 | 1 | `boolean gap()` _(in RankingsScreen.Entry)_ |  |
| 799 | 172 | **type** `private static final class RowsPicture extends Pane` |  |
| 821 | 30 | `RowsPicture(List<Entry> entries, boolean signed, Consumer<String> pick, String open)` _(in RankingsScreen.RowsPicture)_ |  |
| 852 | 4 | `private Entry rowAt(double y)` _(in RankingsScreen.RowsPicture)_ |  |
| 858 | 6 | `private void lightRowAt(double y)` _(in RankingsScreen.RowsPicture)_ | The row under the pointer lit, unless it is the city's or the open one, which are raised already. |
| 865 | 19 | `private void draw(double w)` _(in RankingsScreen.RowsPicture)_ |  |
| 885 | 67 | `private void paint(GraphicsContext gc, Entry e, double y, double w)` _(in RankingsScreen.RowsPicture)_ |  |
| 954 | 10 | `private static String fit(String text, javafx.scene.text.Font font, double room)` _(in RankingsScreen.RowsPicture)_ | A string cut to a column's room with an ellipsis, measured in the font it is drawn in. |
| 965 | 5 | `private static double widthOf(String text, javafx.scene.text.Font font)` _(in RankingsScreen.RowsPicture)_ |  |

### THE RANKINGS TAB (LB2): THE CARD AND THE DRAWER (lines 972-1209)

| line | len | member | says |
|---:|---:|---|---|
| 987 | 6 | `private VBox cardColumn(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 994 | 5 | `private static VBox raised()` |  |
| 1000 | 46 | `private Node rankCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t, WorldMeasures.Measure m)` |  |
| 1048 | 25 | `private Node neighbour(String which, Rankings.Row r, WorldMeasures.Measure m, String none)` | The place above or below the city: what it is, its name and its figure; a click opens its drawer. |
| 1075 | 27 | `private Node pinnedRank(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table shownTable, WorldMeasures.Measure p)` | The city's rank on a pinned measure, "5 of 259", on the board and the money shown; a click shows that measure. |
| 1103 | 49 | `private Node drawerCard(WorldMeasures wm, Nations nations, Rankings.City now, Rankings.Table t)` |  |
| 1154 | 7 | `private String placeName(WorldMeasures wm, Rankings.City now, Rankings.Table t)` | A place's name: the city's, the file's, or the row's on the board (a nation of the game's world the file does not know). |
| 1162 | 28 | `private Node drawerLine(WorldMeasures wm, Rankings.City now, Rankings.Line line, boolean mine)` |  |
| 1192 | 9 | `private static String sourceName(WorldMeasures wm, String id)` | A source's name in a few words: the game's world and the city by what they are, a published source by its name up to the first comma. |
| 1203 | 6 | `private static String sourceFull(WorldMeasures wm, String id)` | ...and in full, for the tooltip: its name and its licence. |


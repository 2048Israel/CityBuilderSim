# Pieces.java - 840 lines · 35 methods · 7 constants · interface

`ham/citybuildersim/ui/Pieces.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The small pieces of text and layout every screen is made from: a sentence,
> an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a
> stacked bar, a limit cell - and, under the divider half way down, the
> helpers two screens turned out to share: the trend chart, the swatch, the
> step chip, the keyed bar, the (i) and its popover (0.7.21), the payer row,
> and the tile the build menu and the land office both lay out three across.
> 
> Static for the same reason as Money and Statement: no state, used
> everywhere, called unqualified through an import static. The scroller is
> not here: scrolled() reads the shell's current screen, so it stayed there.

**Uses:** [Palette](Palette.md) (95), [ChartModel](ChartModel.md) (3), [NationalAccounts](NationalAccounts.md) (3), [PayTier](PayTier.md) (1), [TimeChart](TimeChart.md) (1), [Icons](Icons.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 39 | A FIGURE THAT GOES WHERE IT IS DECIDED. |
| 273 | · THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): |
| 373 | · · where the recording starts |
| 399 | · · the scale |
| 430 | · · the grid |
| 439 | · · the years (0.7.23) |
| 466 | · · the lines |
| 495 | · · the labels |
| 512 | · · the key |
| 676 | TEXT IN THREE LAYERS: THE (i) (0.7.21) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `Pieces.LIMIT_CELL` | `190` | How wide a limit cell is, its padding included; its note wraps inside it. |
| 696 | `Pieces.POPOVER_WIDTH` | `360` | How wide a popover's text wraps. |
| 699 | `Pieces.MORE_IN_THE_MANUAL` | `"More in the manual"` | The line a popover may end with: the third layer's door, with no link until the manual has one. |
| 741 | `Pieces.INFO_SIZE` | `16` | How big the (i) is drawn: its 24-unit grid at this many pixels. |
| 835 | `Pieces.TILE_WIDTH` | `250` | A tile's width. |
| 837 | `Pieces.TILE_HEIGHT` | `232` | A tile's height, the same for a card and a plot. |
| 839 | `Pieces.TILE_GAP` | `10` | The gap between tiles. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 702 | `private static javafx.stage.Popup popover` | The popover showing now, so opening another puts the first away. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 809 | **type** `public final class Pieces` | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a stacked bar, a limit cell - and, under the divider half way down,... |
| 35 | 3 | `public static VBox limitCell(String label, String value, String note, String tone)` | One figure in the constraints bar: what it is, what it reads, what it means. |

### A FIGURE THAT GOES WHERE IT IS DECIDED. (lines 39-272)

| line | len | member | says |
|---:|---:|---|---|
| 59 | 38 | `public static VBox limitCell(String label, String value, String note, String tone, String where, Runnable go)` |  |
| 108 | 20 | `public static javafx.scene.layout.FlowPane chipStrip(String[] names, String current, int size, Consumer<String> pick)` | A row of chips, which is the build strip's shape at two sizes. |
| 130 | 10 | `public static HBox vitalsBar(VBox...cells)` | The bar itself: four cells, the last one without its dividing rule. |
| 142 | 8 | `public static Label sentence(String text, String tone)` | A paragraph in the statement column: wrapped, at body size, in a tone. |
| 152 | 6 | `public static Label subHead(String text)` | A heading inside a section, for a table that needs naming. |
| 167 | 17 | `public static VBox alert(String heading, String body)` | Something that is going wrong, in a block of its own. |
| 186 | 13 | `public static javafx.scene.layout.GridPane grid(double[] widths, javafx.geometry.HPos[] align)` | A table with fixed columns, each aligned the way its figures want. |
| 201 | 8 | `public static void gridHead(javafx.scene.layout.GridPane table, String...names)` | Row zero: the column names, in the label grey, aligned as their column is. |
| 211 | 6 | `public static javafx.geometry.HPos[] rightAfterFirst(int columns)` | Right-hand columns are all the same width in every table on this screen. |
| 219 | 9 | `public static Label gridCell(String text, String tone, int size, boolean rightAlign)` | One cell of a table: a figure right, a name left. |
| 229 | 5 | `public static Label monoLabel(String text)` |  |
| 236 | 1 | **type** `public record Slice(String name, double amount, String colour)` | A named amount, with the colour it was assigned and what it opens into. |
| 245 | 27 | `public static VBox stackedBar(java.util.List<Slice> parts, double width)` | A part-to-whole bar, which is the right form when the parts can be negative and a ring cannot be drawn at all. |

### THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): (lines 273-675)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 5 | `public static void showIf(javafx.scene.Node node, boolean visible)` | Show or hide an overlay without it still taking up its space. |
| 286 | 3 | `public static String cell(double value)` | A count, or a dot where there is nothing - a grid of zeros reads as data. |
| 297 | 10 | `public static String shortTier(PayTier tier)` | Pay tiers, shortened to fit six across. |
| 321 | 5 | `public static String flowText(double value)` | A monthly flow of people, in whole people (0.7.20). |
| 331 | 4 | `public static String flowSigned(String sign, double value)` | The same with its direction in front - "+12", "-3" - and none on a flow that reads "0" or "under 1", which have no direction to give. |
| 357 | 3 | `public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours)` | A small line chart, in the statement's own language. |
| 367 | 166 | `public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours, java.util.function.Doubl...` | ...with the figures written by `figure` rather than by the first series' name (chartFigure()): for a chart whose unit no name says - a rate, a ratio. |
| 535 | 6 | `public static double latest(double[] series)` | The last recorded value of a series, or NaN if there is none. |
| 549 | 12 | `public static String chartFigure(double value, String name)` | A figure on a chart axis, in whatever the series is counted in. |
| 562 | 12 | `public static HBox keySwatch(String colour, String name)` |  |
| 576 | 9 | `public static Label stepChip(String text, Runnable act, boolean quiet)` | A small clickable chip that does something rather than picking a page. |
| 593 | 22 | `public static VBox keyedBar(java.util.List<Slice> parts, double width)` | A stacked bar with its own swatch legend under it. |
| 629 | 6 | `public static double annualGdp(NationalAccounts na)` | A year of output — annualised when the city has not lived a year yet. |
| 641 | 4 | `public static String monthsWait(double months)` | A build time, as every place that quotes one writes it (0.7.20): "~8 mo", "under a month" below one, "stalled" with no site output (NaN) - the order line's own forms. |
| 655 | 4 | `public static String atTodaysQueue(double months)` | ...and said for what it is (0.7.20): "~5 mo at today's queue". |
| 667 | 3 | `public static String ofAnnualGdp(NationalAccounts na)` | What a ratio to that year of output is OF (0.7.20): "of annual GDP", or "of GDP, annualised" while the year is scaled up from fewer than twelve months - so a founding city's "169.7%" says which kind of year it is agai... |
| 671 | 4 | `public static boolean gdpEstimated(NationalAccounts na)` |  |

### TEXT IN THREE LAYERS: THE (i) (0.7.21) (lines 676-840)

| line | len | member | says |
|---:|---:|---|---|
| 711 | 28 | `public static javafx.scene.Node infoButton(String whole, boolean manual)` | The (i): a small circled i that opens `whole` in a popover under it. |
| 747 | 10 | `public static HBox infoLine(String shown, String whole, boolean manual, int size, String tone, double wide)` | A short line with its (i): `shown` on the screen, `whole` one click away. |
| 759 | 30 | `static void openPopover(javafx.scene.Node anchor, String whole, boolean manual)` | The popover itself: the whole text on the raised ground, under the (i), until a click elsewhere or Esc. |
| 791 | 3 | `public static void closePopover()` | The popover put away, if one is open: clearMenu() calls it when the screen changes; a redraw of the same screen (a month landing) leaves it up. |
| 796 | 36 | `public static HBox payerRow(String who, double amount, double total, String rate)` | One payer inside an opened line: who, how much, at what rate. |


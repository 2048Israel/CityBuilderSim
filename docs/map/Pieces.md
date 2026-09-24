# Pieces.java - 631 lines · 27 methods · 3 constants · interface

`ham/citybuildersim/ui/Pieces.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The small pieces of text and layout every screen is made from: a sentence,
> an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a
> stacked bar, a limit cell - and, under the divider half way down, the
> helpers two screens turned out to share: the trend chart, the swatch, the
> step chip, the keyed bar, the payer row, and the tile the build menu and the
> land office both lay out three across.
> 
> Static for the same reason as Money and Statement: no state, used
> everywhere, called unqualified through an import static. The scroller is
> not here: scrolled() reads the shell's current screen, so it stayed there.

**Uses:** [Palette](Palette.md) (80), [NationalAccounts](NationalAccounts.md) (2), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 38 | A FIGURE THAT GOES WHERE IT IS DECIDED. |
| 264 | · THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): |
| 345 | · · where the recording starts |
| 371 | · · the scale |
| 402 | · · the grid |
| 411 | · · the lines |
| 440 | · · the labels |
| 457 | · · the key |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 626 | `Pieces.TILE_WIDTH` | `250` | A tile's width. |
| 628 | `Pieces.TILE_HEIGHT` | `232` | A tile's height, the same for a card and a plot. |
| 630 | `Pieces.TILE_GAP` | `10` | The gap between tiles. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 601 | **type** `public final class Pieces` | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a stacked bar, a limit cell - and, under the divider half way down,... |
| 34 | 3 | `public static VBox limitCell(String label, String value, String note, String tone)` | One figure in the constraints bar: what it is, what it reads, what it means. |

### A FIGURE THAT GOES WHERE IT IS DECIDED. (lines 38-263)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 33 | `public static VBox limitCell(String label, String value, String note, String tone, String where, Runnable go)` |  |
| 99 | 20 | `public static javafx.scene.layout.FlowPane chipStrip(String[] names, String current, int size, Consumer<String> pick)` | A row of chips, which is the build strip's shape at two sizes. |
| 121 | 10 | `public static HBox vitalsBar(VBox...cells)` | The bar itself: four cells, the last one without its dividing rule. |
| 133 | 8 | `public static Label sentence(String text, String tone)` | A paragraph in the statement column: wrapped, at body size, in a tone. |
| 143 | 6 | `public static Label subHead(String text)` | A heading inside a section, for a table that needs naming. |
| 158 | 17 | `public static VBox alert(String heading, String body)` | Something that is going wrong, in a block of its own. |
| 177 | 13 | `public static javafx.scene.layout.GridPane grid(double[] widths, javafx.geometry.HPos[] align)` | A table with fixed columns, each aligned the way its figures want. |
| 192 | 8 | `public static void gridHead(javafx.scene.layout.GridPane table, String...names)` | Row zero: the column names, in the label grey, aligned as their column is. |
| 202 | 6 | `public static javafx.geometry.HPos[] rightAfterFirst(int columns)` | Right-hand columns are all the same width in every table on this screen. |
| 210 | 9 | `public static Label gridCell(String text, String tone, int size, boolean rightAlign)` | One cell of a table: a figure right, a name left. |
| 220 | 5 | `public static Label monoLabel(String text)` |  |
| 227 | 1 | **type** `public record Slice(String name, double amount, String colour)` | A named amount, with the colour it was assigned and what it opens into. |
| 236 | 27 | `public static VBox stackedBar(java.util.List<Slice> parts, double width)` | A part-to-whole bar, which is the right form when the parts can be negative and a ring cannot be drawn at all. |

### THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): (lines 264-631)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 5 | `public static void showIf(javafx.scene.Node node, boolean visible)` | Show or hide an overlay without it still taking up its space. |
| 277 | 3 | `public static String cell(double value)` | A count, or a dot where there is nothing - a grid of zeros reads as data. |
| 288 | 10 | `public static String shortTier(PayTier tier)` | Pay tiers, shortened to fit six across. |
| 308 | 6 | `public static String flowText(double value)` | A monthly flow, at a precision that stays honest in a small city. |
| 329 | 3 | `public static VBox trendChart(String[] names, double[][] series, String[] colours)` | A small line chart, in the statement's own language. |
| 339 | 139 | `public static VBox trendChart(String[] names, double[][] series, String[] colours, java.util.function.DoubleFunction<String> fi...` | ...with the figures written by `figure` rather than by the first series' name (chartFigure()): for a chart whose unit no name says - a rate, a ratio. |
| 480 | 6 | `public static double latest(double[] series)` | The last recorded value of a series, or NaN if there is none. |
| 494 | 12 | `public static String chartFigure(double value, String name)` | A figure on a chart axis, in whatever the series is counted in. |
| 507 | 12 | `public static HBox keySwatch(String colour, String name)` |  |
| 521 | 9 | `public static Label stepChip(String text, Runnable act, boolean quiet)` | A small clickable chip that does something rather than picking a page. |
| 538 | 22 | `public static VBox keyedBar(java.util.List<Slice> parts, double width)` | A stacked bar with its own swatch legend under it. |
| 574 | 6 | `public static double annualGdp(NationalAccounts na)` | A year of output — annualised when the city has not lived a year yet. |
| 581 | 4 | `public static boolean gdpEstimated(NationalAccounts na)` |  |
| 587 | 36 | `public static HBox payerRow(String who, double amount, double total, String rate)` | One payer inside an opened line: who, how much, at what rate. |


# Pieces.java - 5,552 lines · 241 methods · 28 constants · interface

`ham/citybuildersim/ui/Pieces.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The small pieces of text and layout every screen is made from: a sentence,
> an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a
> stacked bar, a limit cell - and, under the divider half way down, the
> helpers two screens turned out to share: the trend chart, the swatch, the
> step chip, the keyed bar, the (i) and its popover (0.7.21), the payer row,
> the icon, the ring and the bar (0.7.24), and since 0.7.26 what a redrawn
> screen is built from - the wide page, the section head and its hint, the
> grid of equal columns, the icon square, the tag, the stepper, a bar of
> segments and ticks, and a loan offered as a card; and since 0.7.27 the
> page's head, the chip, a ring as a card, a waterfall and a bullet bar;
> and since 0.7.28 a cause bar, a supply bar, a funnel, an effect scale,
> cohort bars, a door and the header's sparkline at any size; and since
> 0.7.29 rows on one scale, a hero card, a details fold kept on its
> screen, and a chip strip with an icon a chip; and since 0.7.30 pips and a
> grid of a sector's own figures; and since 0.7.31 a split ring and its
> key, ranked bars, and a bridge of figures with the steps between them;
> and since 0.7.32 columns of stacked segments and a setting's chips with
> the chosen one's line; and since 0.7.33 a ratio in its band, a ladder of
> rates drawn in their parts, two bars on one scale and a status banner;
> and since 0.7.34 a button that asks to be pressed and a door as a pill;
> and since 0.7.35 mirrored bars, a band track and a gauge card around it,
> diverging bars, and the Trade tab's band meter, moved here; and since
> 0.7.36 a figure before and after a move, and the staged tray; and since
> 0.7.37 a chart card's head and a range bar; and since 0.7.39 a search box
> and a gain or a loss in its colour; and since 0.7.45 who goes without,
> the hunger in its two halves.
> 
> Static for the same reason as Money and Statement: no state, used
> everywhere, called unqualified through an import static. The scroller is
> not here: scrolled() reads the shell's current screen, so it stayed there.

**Uses:** [Palette](Palette.md) (569), [Kept](Kept.md) (83), [DebtQuote](DebtQuote.md) (5), [Money](Money.md) (4), [PolicyScreen](PolicyScreen.md) (4), [ChartModel](ChartModel.md) (3), [NationalAccounts](NationalAccounts.md) (3), [Icons](Icons.md) (3), [UserInterface](UserInterface.md) (3), [Sector](Sector.md) (3), [Game](Game.md) (2), [PayTier](PayTier.md) (1), [TimeChart](TimeChart.md) (1), [DebtManager](DebtManager.md) (1), [BuildScreen](BuildScreen.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (14):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 59 | A FIGURE THAT GOES WHERE IT IS DECIDED. |
| 336 | · THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): |
| 442 | · · where the recording starts |
| 468 | · · the scale |
| 499 | · · the grid |
| 508 | · · the years (0.7.23) |
| 535 | · · the lines |
| 564 | · · the labels |
| 581 | · · the key |
| 751 | TEXT IN THREE LAYERS: THE (i) (0.7.21) |
| 925 | AN ICON, AND A RING (0.7.24) |
| 1070 | THE PIECES A REDRAWN SCREEN IS BUILT FROM (0.7.26) |
| 1199 | · a bar of segments |
| 1464 | · a loan as a card |
| 1605 | THE PIECES THE PEOPLE PAGE ADDED (0.7.27) |
| 1701 | · a ring as a card |
| 1782 | · a waterfall |
| 2071 | · a bullet bar |
| 2100 | THE PIECES THE SERVICES SCREEN ADDED (0.7.28) |
| 2123 | · a cause bar |
| 2305 | · a supply bar |
| 2336 | · a funnel |
| 2396 | · an effect scale |
| 2494 | · cohort bars |
| 2591 | · a door, and a spark |
| 2628 | THE PIECES THE INFRASTRUCTURE SCREEN ADDED (0.7.29) |
| 2696 | · rows on one scale |
| 3097 | THE PIECES THE SECTORS SCREEN ADDED (0.7.30) |
| 3219 | THE PIECES THE GOVERNMENT SCREEN ADDED (0.7.31) |
| 3403 | · ranked bars |
| 3557 | · a bridge |
| 3749 | THE PIECES THE FINANCES SCREEN ADDED (0.7.32) |
| 4023 | THE PIECES THE BANK SCREEN ADDED (0.7.33) |
| 4073 | · a ladder of rates |
| 4194 | · two bars on one scale |
| 4226 | · a status banner |
| 4252 | THE BUTTON THAT ASKS TO BE PRESSED (0.7.34) |
| 4478 | THE PIECES THE TRADE SCREEN ADDED (0.7.35) |
| 4572 | · a band track |
| 4674 | · a gauge card |
| 4721 | · mirrored bars |
| 4949 | · diverging bars |
| 5105 | THE PIECES THE POLICY SCREEN ADDED (0.7.36) |
| 5280 | THE PIECES CITY HISTORY ADDED (0.7.37) |
| 5412 | THE PIECES THE FUND ADDED (0.7.39) |
| 5493 | WHO GOES WITHOUT (0.7.45) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 4287 | `Pieces.Look.GO` |  |
| 4287 | `Pieces.Look.CHOOSE` |  |
| 4287 | `Pieces.Look.CREDIT` |  |
| 4287 | `Pieces.Look.HELD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 117 | `Pieces.LIMIT_CELL` | `190` | How wide a limit cell is, its padding included; its note wraps inside it. |
| 771 | `Pieces.POPOVER_WIDTH` | `360` | How wide a popover's text wraps. |
| 774 | `Pieces.MORE_IN_THE_MANUAL` | `"More in the manual"` | The line a popover may end with: the third layer's door, with no link until the manual has one. |
| 817 | `Pieces.INFO_SIZE` | `16` | How big the (i) is drawn: its 24-unit grid at this many pixels. |
| 923 | `Pieces.TILE_GAP` | `10` | The gap between cards in a row or a grid: Build's cards in their flow, the land office's shelf. |
| 1091 | `Pieces.PAGE_WIDE` | `1480` | The widest a redrawn page is laid out - Build's Overview and categories since 0.7.24, the land office since 0.7.26 - so a 1,920 window does not stretch a row of cards across the glass. |
| 1847 | `Pieces.Waterfall.FIGURE_ROOM` | `16, NAME_ROOM = 12, ICON = 13` | The figures' line over the plot, the least the names under it take, and an icon's size. |
| 2143 | `Pieces.CAUSE_LABEL_ROOM` | `70` | How wide a part must be drawn to carry its name and figure under it; a narrower one is keyed. |
| 2169 | `Pieces.CauseBar.GAP` | `2, UNDER = 3, KEY_GAP = 12, KEY_ROW = 3` | Between the parts, under the bar, and between the key's entries and rows. |
| 2447 | `Pieces.EffectScale.TALL` | `44, Y = 22, DOT = 10` | Its height, how far down it the line runs, and the mark's size. |
| 2523 | `Pieces.CohortBars.AXIS` | `15` | Room under the bars for the near and far names. |
| 2784 | `Pieces.ScaleRows.ROW` | `26, GAP = 10, RULE_NAMES = 16, TAG_GAP = 6, ICON = 12` | A row's least height, the gap either side of the bar, the room the rules' names take over the rows, the gap before a tag, and an icon's size. |
| 3273 | `Pieces.EVERYTHING_ELSE` | `"Everything else"` | What topSlices() calls the slices past its ramp, folded into one. |
| 3420 | `Pieces.RANK_BAND` | `12, RANK_ROW = 36` | How tall a ranked bar's band is, and the least a row takes. |
| 3423 | `Pieces.RANK_KEY` | `"rankBars.key"` | The property a ranked bar's row carries its line's key under, so a screen can find the row to scroll to. |
| 3584 | `Pieces.BRIDGE_TILE` | `200, BRIDGE_BAR = 56, BRIDGE_FIGURE = 64` | A bridge's tiles' width, its step bars' and its figures' (0.7.31). |
| 3587 | `Pieces.BRIDGE_NOTHING` | `.5` | A step under this, in the model's thousands, is nothing: half a thousand, below which signedTight() writes "$0". |
| 3806 | `Pieces.Columns.TOP` | `34, FOOT = 30, GAP = 8` | The room over the plot for a tag and a figure, under it for a label and its second line, and the gap between columns. |
| 4042 | `Pieces.BAND_SCALE` | `1.6` | How far past the top of its band a ratio's bar runs, as a multiple of the top: the band sits in the left of it, so a ratio well over its band reads as full (BankScreen's capital band's since 0.7.9). |
| 4107 | `Pieces.RUNG_ROW` | `30, RUNG_BAND = 12` | A rung's least height, and its bar's band. |
| 4296 | `Pieces.ACTION_TALL` | `40` | An action button's height on a card, where it is the thing the card is for. |
| 4299 | `Pieces.ACTION_INLINE` | `32` | ...beside a heading, where it shares a row with words: Build's "Build all three", the land office's "Buy the next 5". |
| 4302 | `Pieces.DOOR_TALL` | `30` | A door pill's height. |
| 4468 | `Pieces.DOOR_ACCENT` | `"pieces.doorAccent"` | The key under which a door pill carries its accent, for the listeners added once to read. |
| 4764 | `Pieces.MirrorRows.ROW` | `28, BAR = 12, ICON = 18, GAP = 8` | A row's least height, its bar's thickness, an icon's size and the gaps between the columns. |
| 4989 | `Pieces.DivergingBars.ROW` | `30, BAR = 12` | A row's least height and its bar's thickness. |
| 5359 | `Pieces.RangeBar.TRACK` | `6, DOT = 10, TICK_W = 5, TICK_H = 14, PAD = 6` | The track's height, the dot's size, the tick's width and height, and the room either end so the dot is never cut. |
| 5511 | `Pieces.HUNGER_INFO` | `"The measure is struck at the top of the month on the last sale: what each " ...` | The (i): when the measure is struck, and what each half means. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 777 | `private static javafx.stage.Popup popover` | The popover showing now, so opening another puts the first away. |
| 1251 | `private List<Segment> parts` |  |
| 1252 | `private List<Tick> ticks` |  |
| 1253 | `private double scale, band` |  |
| 1254 | `private boolean told` |  |
| 1255 | `private final Region track` |  |
| 1256 | `private final List<Region> drawn` |  |
| 1257 | `private final List<Label> labels` |  |
| 1258 | `private final List<Region> marks` |  |
| 1260 | `private final List<Tooltip> segTips` | The tooltip each segment's and each tick's region carries, or null where it has none. |
| 1262 | `private final javafx.beans.property.DoubleProperty first` | The first segment's amount while it grows (animateFirst()), or NaN when it stands still. |
| 1835 | `private List<Step> steps` |  |
| 1836 | `private double[] from` |  |
| 1837 | `private double low, high` |  |
| 1838 | `private final List<StepColumn> columns` |  |
| 1839 | `private final javafx.scene.shape.Line zero` |  |
| 1841 | `private final javafx.beans.property.DoubleProperty grown` | How far the bars have grown out of the zero line, 0 to 1 (animate()). |
| 1843 | `private double[] layoutKey` | The figures the layout read at the last set(): the amounts and the words, to lay out again only when one moved. |
| 1844 | `private String layoutText` |  |
| 1851 | `final String shape` |  |
| 1852 | `final List<Region> bars` |  |
| 1853 | `final List<Tooltip> tips` |  |
| 1854 | `final Label figure` |  |
| 1855 | `final javafx.scene.Node mark` |  |
| 1856 | `final javafx.scene.shape.Line link` |  |
| 2163 | `private final List<Part> parts` |  |
| 2164 | `private final double band, total` |  |
| 2165 | `private final List<Region> blocks` |  |
| 2166 | `private final List<VBox> names` |  |
| 2167 | `private final List<HBox> keys` |  |
| 2443 | `private final double at` |  |
| 2444 | `private final Region line` |  |
| 2445 | `private final Label now, none, all` |  |
| 2516 | `private final double[] counts` |  |
| 2517 | `private final double peak` |  |
| 2518 | `private final int dangerFrom` |  |
| 2519 | `private final List<Region> bars` |  |
| 2520 | `private final javafx.scene.shape.Line divider` |  |
| 2521 | `private final Label dividerName, near, far` |  |
| 2785 | `private final List<ScaleRow> rows` |  |
| 2786 | `private final List<Rule> rules` |  |
| 2787 | `private final double scale, nameWidth, figureWidth, band` |  |
| 2788 | `private final List<Label> names` |  |
| 2789 | `private final List<Label> empties` |  |
| 2790 | `private final List<Label> overs` |  |
| 2791 | `private final List<javafx.scene.Node> infos` |  |
| 2792 | `private final List<Region> tracks` |  |
| 2793 | `private final List<List<Region>> drawn` |  |
| 2794 | `private final List<javafx.scene.shape.Line> lines` |  |
| 2795 | `private final List<Label> ruleNames` |  |
| 3525 | `private final double amount, pos, neg` |  |
| 3526 | `private final Region track` |  |
| 3807 | `private final List<Column> cols` |  |
| 3808 | `private final double scale` |  |
| 3809 | `private final List<List<Region>> drawn` |  |
| 3810 | `private final List<Label> figures` |  |
| 3812 | `private final List<javafx.scene.Node> tags` |  |
| 3813 | `private final List<Region> hits` |  |
| 3815 | `private final List<javafx.scene.shape.Shape[]> breaks` | A broken column's break (0.7.34): the gap, in the card's ground, and its two slashes; null for a whole column. |
| 4316 | `private final String svg, accent` |  |
| 4317 | `private final double tall` |  |
| 4318 | `private final HBox row` |  |
| 4319 | `private final Label main` |  |
| 4320 | `private Press press` |  |
| 4321 | `private Runnable go` |  |
| 4589 | `private final double at, band` |  |
| 4590 | `private final double[] edges` |  |
| 4591 | `private final List<Tick> ticks` |  |
| 4592 | `private final boolean muted` |  |
| 4593 | `private final List<Region> bands` |  |
| 4594 | `private final List<Label> names` |  |
| 4595 | `private final Region mark` |  |
| 4751 | `private final List<Mirror> rows` |  |
| 4752 | `private final double scale, nameWidth, netWidth` |  |
| 4753 | `private final List<javafx.scene.Node> icons` |  |
| 4754 | `private final List<Label> names` |  |
| 4756 | `private final List<Region> leftBars` |  |
| 4758 | `private final Region axis` |  |
| 4759 | `private final Label leftHead, rightHead` |  |
| 4761 | `private final javafx.beans.property.DoubleProperty grown` | How far the bars have grown out of the axis, 0 to 1 (grow()). |
| 4980 | `private final List<Force> rows` |  |
| 4981 | `private final double scale, nameWidth, figureWidth` |  |
| 4982 | `private final List<VBox> names` |  |
| 4983 | `private final List<Region> bars` |  |
| 4984 | `private final List<Label> figures` |  |
| 4985 | `private final Region axis` |  |
| 4986 | `private final Label leftWords, rightWords` |  |
| 5297 | `public final Label name, figure, change` | The line's name, its figure now, and its move over the window. |
| 5360 | `private final double lo, hi, first, last` |  |
| 5361 | `private final Region track` |  |
| 5363 | `private final javafx.beans.property.DoubleProperty at` | Where the dot is drawn, as a share of the track: the end's own, or on its way there (slideFrom()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 5501 | **type** `public final class Pieces` | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a stacked bar, a limit cell - and, under the divider half way down,... |
| 55 | 3 | `public static VBox limitCell(String label, String value, String note, String tone)` | One figure in the constraints bar: what it is, what it reads, what it means. |

### A FIGURE THAT GOES WHERE IT IS DECIDED. (lines 59-335)

| line | len | member | says |
|---:|---:|---|---|
| 79 | 36 | `public static VBox limitCell(String label, String value, String note, String tone, String where, Runnable go)` |  |
| 126 | 24 | `public static javafx.scene.layout.FlowPane chipStrip(String[] names, String current, int size, Consumer<String> pick)` | A row of chips, which is the build strip's shape at two sizes. |
| 157 | 12 | `public static javafx.scene.layout.FlowPane chipStrip(String[] names, String[] icons, String current, int size, Consumer<String>...` | ...with an icon before each name (0.7.29: the Infrastructure tab's pages, the road, the bus, the train and the lorry), in the accent while its page is open and the label grey otherwise. |
| 171 | 23 | `public static HBox vitalsBar(VBox...cells)` | The bar itself: its cells - four, and People's five since 0.7.27 - the last one without its dividing rule. |
| 196 | 8 | `public static Label sentence(String text, String tone)` | A paragraph in the statement column: wrapped, at body size, in a tone. |
| 206 | 6 | `public static Label subHead(String text)` | A heading inside a section, for a table that needs naming. |
| 221 | 17 | `public static VBox alert(String heading, String body)` | Something that is going wrong, in a block of its own. |
| 240 | 13 | `public static javafx.scene.layout.GridPane grid(double[] widths, javafx.geometry.HPos[] align)` | A table with fixed columns, each aligned the way its figures want. |
| 255 | 8 | `public static void gridHead(javafx.scene.layout.GridPane table, String...names)` | Row zero: the column names, in the label grey, aligned as their column is. |
| 265 | 6 | `public static javafx.geometry.HPos[] rightAfterFirst(int columns)` | Right-hand columns are all the same width in every table on this screen. |
| 273 | 9 | `public static Label gridCell(String text, String tone, int size, boolean rightAlign)` | One cell of a table: a figure right, a name left. |
| 283 | 5 | `public static Label monoLabel(String text)` |  |
| 290 | 1 | **type** `public record Slice(String name, double amount, String colour)` | A named amount, with the colour it was assigned and what it opens into. |
| 299 | 3 | `public static VBox stackedBar(java.util.List<Slice> parts, double width)` | A part-to-whole bar, which is the right form when the parts can be negative and a ring cannot be drawn at all. |
| 308 | 27 | `public static VBox stackedBar(java.util.List<Slice> parts, double width, java.util.function.DoubleFunction<String> figure)` | ...with each part's tooltip figure written by `figure` (0.7.27): the bar printed money whatever it held, so it could not carry people. |

### THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): (lines 336-750)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 5 | `public static void showIf(javafx.scene.Node node, boolean visible)` | Show or hide an overlay without it still taking up its space. |
| 355 | 3 | `public static String cell(double value)` | A count, or a dot where there is nothing - a grid of zeros reads as data. |
| 366 | 10 | `public static String shortTier(PayTier tier)` | Pay tiers, shortened to fit six across. |
| 390 | 5 | `public static String flowText(double value)` | A monthly flow of people, in whole people (0.7.20). |
| 400 | 4 | `public static String flowSigned(String sign, double value)` | The same with its direction in front - "+12", "-3" - and none on a flow that reads "0" or "under 1", which have no direction to give. |
| 426 | 3 | `public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours)` | A small line chart, in the statement's own language. |
| 436 | 166 | `public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours, java.util.function.Doubl...` | ...with the figures written by `figure` rather than by the first series' name (chartFigure()): for a chart whose unit no name says - a rate, a ratio. |
| 604 | 6 | `public static double latest(double[] series)` | The last recorded value of a series, or NaN if there is none. |
| 618 | 12 | `public static String chartFigure(double value, String name)` | A figure on a chart axis, in whatever the series is counted in. |
| 631 | 12 | `public static HBox keySwatch(String colour, String name)` |  |
| 645 | 9 | `public static Label stepChip(String text, Runnable act, boolean quiet)` | A small clickable chip that does something rather than picking a page. |
| 662 | 3 | `public static VBox keyedBar(java.util.List<Slice> parts, double width)` | A stacked bar with its own swatch legend under it. |
| 667 | 23 | `public static VBox keyedBar(java.util.List<Slice> parts, double width, java.util.function.DoubleFunction<String> figure)` | ...with its tooltips' figures written by `figure` (0.7.27), as stackedBar's. |
| 704 | 6 | `public static double annualGdp(NationalAccounts na)` | A year of output — annualised when the city has not lived a year yet. |
| 716 | 4 | `public static String monthsWait(double months)` | A build time, as every place that quotes one writes it (0.7.20): "~8 mo", "under a month" below one, "stalled" with no site output (NaN) - the order line's own forms. |
| 730 | 4 | `public static String atTodaysQueue(double months)` | ...and said for what it is (0.7.20): "~5 mo at today's queue". |
| 742 | 3 | `public static String ofAnnualGdp(NationalAccounts na)` | What a ratio to that year of output is OF (0.7.20): "of annual GDP", or "of GDP, annualised" while the year is scaled up from fewer than twelve months - so a founding city's "169.7%" says which kind of year it is agai... |
| 746 | 4 | `public static boolean gdpEstimated(NationalAccounts na)` |  |

### TEXT IN THREE LAYERS: THE (i) (0.7.21) (lines 751-924)

| line | len | member | says |
|---:|---:|---|---|
| 786 | 29 | `public static javafx.scene.Node infoButton(String whole, boolean manual)` | The (i): a small circled i that opens `whole` in a popover under it. |
| 823 | 10 | `public static HBox infoLine(String shown, String whole, boolean manual, int size, String tone, double wide)` | A short line with its (i): `shown` on the screen, `whole` one click away. |
| 835 | 15 | `static void openPopover(javafx.scene.Node anchor, String whole, boolean manual)` | The popover itself: the whole text on the raised ground, under the (i), until a click elsewhere or Esc. |
| 856 | 18 | `public static void openPopover(javafx.scene.Node anchor, javafx.scene.Node body)` | ...or anything else on the same card (0.7.27): the People page's "Moved in" opens a skill bar with its note under it. |
| 876 | 3 | `public static void closePopover()` | The popover put away, if one is open: clearMenu() calls it when the screen changes; a redraw of the same screen (a month landing) leaves it up. |
| 881 | 40 | `public static HBox payerRow(String who, double amount, double total, String rate)` | One payer inside an opened line: who, how much, at what rate. |

### AN ICON, AND A RING (0.7.24) (lines 925-1069)

| line | len | member | says |
|---:|---:|---|---|
| 940 | 3 | `public static Region icon(String svg, String colour, double size)` | An outline icon from Icons, stroked in a colour at a size: its 24-unit grid in a Pane of exactly that size, scaled - the rail's way (UserInterface.railButton()), so every icon sits at the coordinates it was drawn at w... |
| 949 | 26 | `static Region makeIcon(String svg, String colour, double size, boolean kept)` | ...made through Kept where `kept` (a page's own icon, handed back each redraw), with new where not (an icon a picture owns, as the waterfall's under its columns: Kept's pool is the page's and a picture's parts are its... |
| 983 | 60 | `public static Region ring(double share, String colour, double size, double stroke, String label, double labelSize)` | A ring: a track, an arc of `share` round it from twelve o'clock in a colour, and a label in the middle - or a tick, for "nothing needed". |
| 1049 | 20 | `public static Region bar(double share, String colour, double width, double height)` | A bar: a track the width given and a fill of `share` of it, in a colour - the Build cards' two bars (0.7.24 on the city's, every building's since 0.7.25). |

### THE PIECES A REDRAWN SCREEN IS BUILT FROM (0.7.26) (lines 1070-1198)

| line | len | member | says |
|---:|---:|---|---|
| 1094 | 7 | `public static VBox widePage()` | A page as wide as the stage, up to PAGE_WIDE, with the room under it every page keeps. |
| 1103 | 3 | `public static HBox sectionHead(String title, javafx.scene.Node right)` | A section's heading, and whatever sits at its right. |
| 1112 | 14 | `public static HBox sectionHead(String title, String info, javafx.scene.Node right)` | ...with an (i) after the heading when `info` is not null (0.7.26: the land office's "ON OFFER", whose paragraph moved in there). |
| 1128 | 5 | `public static Label hint(String text)` | A section heading's quiet words at its right: "tap one to build for it". |
| 1135 | 14 | `public static javafx.scene.layout.GridPane equalColumns(int n, double gap)` | A grid of equal columns, as wide as its page. |
| 1151 | 8 | `public static Region iconSquare(String svg, String colour, double box, double size)` | An icon in a rounded square tinted with its colour, as the mockups set one beside a heading (0.7.24; the window's until 0.7.26). |
| 1166 | 8 | `public static Label tag(String text, String colour)` | A tag: a few words in a pill outlined and tinted in a colour - a Build card's "cheapest per resident" in green, and since 0.7.26 the land office's BEST VALUE (green), ORE and MOST ORE (Palette.ORE) and NEW (the buildi... |
| 1176 | 22 | `public static Button stepper(String glyph, double width, Runnable go)` | A small square button in a quantity stepper: Build's "−", "+", "+10", "+100" and "↺", the land office's "−" and "+". |

### a bar of segments (lines 1199-1463)

| line | len | member | says |
|---:|---:|---|---|
| 1208 | 7 | **type** `public record Segment(double amount, String colour, boolean ghost, String stripe, String label, String tip,...` | One stretch of a segment bar: how much of the bar's units, its colour, and - each may be null - whether it is a GHOST (outlined and tinted, not filled: what would be there, not what is), a stripe along its foot in ano... |
| 1211 | 3 | `public static Segment of(double amount, String colour)` _(in Pieces.Segment)_ | A plain filled stretch. |
| 1217 | 1 | **type** `public record Tick(double at, String colour, double width, String name, String tip)` | A mark across a segment bar: where, in the bar's units; its colour and width in pixels; its name under the bar, and a tooltip (either may be null). |
| 1236 | 7 | `public static SegmentBar segmentBar(List<Segment> parts, double scale, List<Tick> ticks, double width, double band)` | A bar of segments and ticks (0.7.26): a track, the segments laid end to end from the left, each `amount` of `scale` (scale 0 or less: their sum), and the ticks drawn across it, each named under the bar if it has a name. |
| 1250 | 213 | **type** `public static final class SegmentBar extends javafx.scene.layout.Pane` | The bar segmentBar() draws: a Pane that lays its parts out at whatever width it is given. |
| 1266 | 4 | `SegmentBar()` _(in Pieces.SegmentBar)_ | Its track and its listener, made once in its life; the segments and ticks come with set(). |
| 1276 | 100 | `void set(List<Segment> parts, double scale, List<Tick> ticks, double width, double band)` _(in Pieces.SegmentBar)_ | What the bar draws now: the parts and ticks, the scale (0 or less: their sum), the width (0 or less: what holds it) and the band. |
| 1378 | 14 | `private static Tooltip tipped(Region on, Tooltip had, String text)` _(in Pieces.SegmentBar)_ | The tooltip a region should carry for this text: its own, its text set, or a new one installed, or none (and the old one taken off). |
| 1394 | 1 | `private double proud()` _(in Pieces.SegmentBar)_ | How far the ticks stand proud of the band, each way. |
| 1397 | 4 | `private boolean named()` _(in Pieces.SegmentBar)_ | Whether any tick carries a name, which takes a line under the bar. |
| 1403 | 1 | `private double tall()` _(in Pieces.SegmentBar)_ | The bar's own height: the band, the ticks proud of it, and the names' line. |
| 1410 | 11 | `public void animateFirst(double from, double millis)` _(in Pieces.SegmentBar)_ | The first segment grows from `from` to its own amount over `millis` - the land office's free ground widening after a purchase - with the segments after it carried along. |
| 1422 | 40 | `protected void layoutChildren()` _(in Pieces.SegmentBar)_ |  |

### a loan as a card (lines 1464-1604)

| line | len | member | says |
|---:|---:|---|---|
| 1481 | 8 | `public static String rateColour(DebtQuote quote, DebtManager market)` | A quoted rate's verdict colour, by how far up the market's own band it sits - the rule Build's old credit page coloured its rates by, here since 0.7.26 so a card can colour its offer. |
| 1504 | 11 | `public static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone, String ending, String action,...` | A loan on offer, as a card (0.7.26): the land office's funding page sets two or three side by side, and Build's two since 0.7.40 (it stacked the same figures as a statement, INSUFFICIENT FUNDS, until then). |
| 1522 | 5 | `public static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone, String ending, Press press, S...` | ...with the action button across its foot (0.7.34): the land office's offers, whose press borrows and then buys - "Buy 5 plots · D$9.8B", and the paper under it - and since 0.7.40 Build's, whose press borrows and then... |
| 1528 | 38 | `static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone, String ending, javafx.scene.Node go,...` |  |
| 1576 | 12 | `public static VBox coverCash(VBox card, DebtQuote quote)` | A funding card's loan that came up short (CD2): the auction could not sell the paper that raises the cash this card is for at the highest yield the city takes. |
| 1590 | 14 | `static HBox offerLine(String label, String value, String tone)` | One figure on an offer card: what it is at the left, wrapping, and the figure at the right. |

### THE PIECES THE PEOPLE PAGE ADDED (0.7.27) (lines 1605-1700)

| line | len | member | says |
|---:|---:|---|---|
| 1622 | 3 | `public static HBox pageHead(String title, String areaColour, javafx.scene.Node...right)` | A page's head: its title, its area's swatch, and what sits at its right. |
| 1632 | 51 | `public static HBox pageHead(String title, String areaColour, Runnable back, String sub, String info, javafx.scene.Node...right)` | ...with a sub-page after "›" - the title is then a way back, `back` - and an (i) holding `info` after them when it is not null: "People › Household money (i)". |
| 1685 | 8 | `public static Label chip(String text, String colour)` | A chip: the words in a colour on a faint ground of it (Construction's, 0.7.22; here since 0.7.27). |
| 1695 | 5 | `public static String tint(String hex, double alpha)` | A colour at an opacity, for a chip's ground. |

### a ring as a card (lines 1701-1781)

| line | len | member | says |
|---:|---:|---|---|
| 1710 | 5 | `public static HBox ringCard(String figure, double arc, String tone, String title, String line, String foot, double ringSize, Ru...` | A ring as a card: the ring at the left with its figure in it, and beside it a title, a line and a foot - Build's measure card (0.7.24), on the Build card ground (RAISED, radius 8, a 1 px EDGE that turns ACCENT under t... |
| 1723 | 58 | `public static HBox ringCard(String figure, double arc, String tone, String title, String info, String line, String foot, String...` | ...with an (i) after the title holding `info`, the foot in a colour of its own (Build's "N on site" in the building pink), the PICKED form - the pinned ground and a pink edge, Build's ring whose buildings are shown un... |

### a waterfall (lines 1782-2070)

| line | len | member | says |
|---:|---:|---|---|
| 1793 | 17 | **type** `public record Step(String name, double amount, String colour, boolean total, List<Slice> parts, String icon...` | One column of a waterfall: its name; its amount, which a plain step adds to the running total (negative takes away) and a TOTAL step sets - drawn from zero, the model's own figure, so a column the model sums is never ... |
| 1796 | 3 | `public static Step of(String name, double amount, String colour)` _(in Pieces.Step)_ | A plain step. |
| 1800 | 3 | `public static Step total(String name, double amount, String colour)` _(in Pieces.Step)_ | A total, drawn from zero to the figure given. |
| 1803 | 1 | `public Step parts(List<Slice> p)` _(in Pieces.Step)_ |  |
| 1804 | 1 | `public Step icon(String svg)` _(in Pieces.Step)_ |  |
| 1805 | 1 | `public Step tip(String t)` _(in Pieces.Step)_ |  |
| 1806 | 3 | `public Step go(java.util.function.Consumer<javafx.scene.Node> g)` _(in Pieces.Step)_ |  |
| 1820 | 7 | `public static Waterfall waterfall(List<Step> steps, java.util.function.DoubleFunction<String> figure, double width, double height)` | A waterfall (0.7.27): the steps as columns, each bar standing where the running total was before it and reaching where it is after, a zero line across, a hairline from each bar to the next, the figure over each bar wr... |
| 1834 | 236 | **type** `public static final class Waterfall extends javafx.scene.layout.Pane` | The bars waterfall() draws: a Pane that lays its columns out at whatever width it is given. |
| 1850 | 52 | **type** `private static final class StepColumn` _(in Pieces.Waterfall)_ | One column: the bars it stands on (one, or a bar per part), the figure over them, its name, its icon and the hairline to the next column. |
| 1858 | 18 | `StepColumn(String shape, int bars, String icon)` _(in Pieces.Waterfall.StepColumn)_ |  |
| 1878 | 23 | `void fill(Step s, List<Slice> slices, java.util.function.DoubleFunction<String> written)` _(in Pieces.Waterfall.StepColumn)_ | The step's bars, words and click set again. |
| 1904 | 5 | `Waterfall()` _(in Pieces.Waterfall)_ | Its zero line and its listener, made once in its life; the columns come with set(). |
| 1911 | 4 | `private static List<Slice> slicesOf(Step s)` _(in Pieces.Waterfall)_ | The slices a step's bar is stacked from: its parts, or one slice of its own. |
| 1920 | 75 | `void set(List<Step> steps, java.util.function.DoubleFunction<String> written, double width, double height)` _(in Pieces.Waterfall)_ | The steps drawn now, the written figures, the width (0 or less: what holds it) and the whole height. |
| 1997 | 8 | `public void animate(double millis)` _(in Pieces.Waterfall)_ | The bars grow out of the zero line over `millis` - the People page's month landing. |
| 2006 | 63 | `protected void layoutChildren()` _(in Pieces.Waterfall)_ |  |

### a bullet bar (lines 2071-2099)

| line | len | member | says |
|---:|---:|---|---|
| 2081 | 18 | `public static VBox bulletBar(double value, double target, String colour, double width, String label)` | A figure against the line it is read against (0.7.27): a bar of `value` in a colour with a tick at `target`, on a scale of the larger of the two, and `label` under it - the People page's living here against what the c... |

### THE PIECES THE SERVICES SCREEN ADDED (0.7.28) (lines 2100-2122)

### a cause bar (lines 2123-2304)

| line | len | member | says |
|---:|---:|---|---|
| 2132 | 9 | **type** `public record Part(String name, double amount, String colour, Runnable go, boolean hatched, String tip)` | One part of a cause bar: its name, its amount in the bar's units (one at zero or below draws nothing and is keyed as "none"), its colour, what a click on it opens (null: nothing), whether it is HATCHED - what is left ... |
| 2134 | 3 | `public static Part of(String name, double amount, String colour)` _(in Pieces.Part)_ | A plain part. |
| 2137 | 1 | `public Part go(Runnable g)` _(in Pieces.Part)_ |  |
| 2138 | 1 | `public Part tip(String t)` _(in Pieces.Part)_ |  |
| 2139 | 1 | `public Part leftOver()` _(in Pieces.Part)_ |  |
| 2156 | 4 | `public static CauseBar causeBar(List<Part> parts, double width, double band, java.util.function.DoubleFunction<String> figure)` | A cause bar (0.7.28): the parts laid end to end on a scale of their sum, so the bar is the whole they make - a part wide enough named under itself with its figure, the narrower ones and the parts at nothing in a key u... |
| 2162 | 142 | **type** `public static final class CauseBar extends javafx.scene.layout.Pane` | The bar causeBar() draws: a Pane that lays its parts, their names and its key out at whatever width it is given. |
| 2171 | 52 | `CauseBar(List<Part> parts, double width, double band, java.util.function.DoubleFunction<String> figure)` _(in Pieces.CauseBar)_ |  |
| 2224 | 1 | `private static double finite(double v)` _(in Pieces.CauseBar)_ |  |
| 2227 | 11 | `private double[] widths(double w)` _(in Pieces.CauseBar)_ | Each part's drawn width at a bar this wide, 0 for a part at nothing. |
| 2240 | 1 | `private static boolean named(double drawn)` _(in Pieces.CauseBar)_ | Whether a part is named under the bar, at that width. |
| 2242 | 1 | `public javafx.geometry.Orientation getContentBias()` _(in Pieces.CauseBar)_ |  |
| 2244 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.CauseBar)_ |  |
| 2246 | 22 | `protected double computePrefHeight(double width)` _(in Pieces.CauseBar)_ |  |
| 2269 | 34 | `protected void layoutChildren()` _(in Pieces.CauseBar)_ |  |

### a supply bar (lines 2305-2335)

| line | len | member | says |
|---:|---:|---|---|
| 2317 | 18 | `public static SegmentBar supplyBar(double need, double built, double working, String colour, String needName, List<Tick> more, ...` | A supply bar (0.7.28): what is working, inside what is built, against what is needed - a segment bar of `working` in `colour` and the rest of `built` in a light step of it, on a scale of the larger of the need and wha... |

### a funnel (lines 2336-2395)

| line | len | member | says |
|---:|---:|---|---|
| 2339 | 1 | **type** `public record FunnelStep(String name, double amount, String figure, String tip)` | One step of a funnel: its name, its amount on the funnel's scale, its figure as written, and its tooltip (null: none). |
| 2350 | 45 | `public static VBox funnel(List<FunnelStep> steps, double width, int binding, String colour)` | A funnel (0.7.28): the steps a figure narrows through, a row each - its name at the left, a bar on the scale of the largest step, its figure at the right - with the step at `binding` (-1: none) outlined and its name i... |

### an effect scale (lines 2396-2493)

| line | len | member | says |
|---:|---:|---|---|
| 2409 | 31 | `public static HBox effectScale(String label, double now, double atNone, double atAll, boolean log, java.util.function.DoubleFun...` | An effect scale (0.7.28): what a coverage buys, as a line from what it is with nobody covered to what it is with everybody, today marked on it with its figure over the mark - `label` at the left in a column `labelWidt... |
| 2442 | 51 | **type** `public static final class EffectScale extends javafx.scene.layout.Pane` | The line effectScale() draws: a Pane that lays its ends and today's mark out at whatever width it is given. |
| 2449 | 27 | `EffectScale(double now, double atNone, double atAll, boolean log, java.util.function.DoubleFunction<String> figure, String colour)` _(in Pieces.EffectScale)_ |  |
| 2477 | 15 | `protected void layoutChildren()` _(in Pieces.EffectScale)_ |  |

### cohort bars (lines 2494-2590)

| line | len | member | says |
|---:|---:|---|---|
| 2507 | 6 | `public static CohortBars cohortBars(double[] counts, double width, double height, String colour, int dangerFrom, String danger,...` | Cohort bars (0.7.28): everybody part way through something, a bar each on the tallest bar's scale - the nearest end at the left, named `near` under it, the farthest at the right, `far` - each bar's figure and `slotNam... |
| 2515 | 75 | **type** `public static final class CohortBars extends javafx.scene.layout.Pane` | The bars cohortBars() draws: a Pane that lays them out at whatever width it is given. |
| 2525 | 38 | `CohortBars(double[] counts, double width, double height, String colour, int dangerFrom, String danger, String dangerName, Strin...` _(in Pieces.CohortBars)_ |  |
| 2564 | 25 | `protected void layoutChildren()` _(in Pieces.CohortBars)_ |  |

### a door, and a spark (lines 2591-2627)

| line | len | member | says |
|---:|---:|---|---|
| 2600 | 13 | `public static Label door(String text, String colour, Runnable go)` | A door (0.7.28): a few words and a "›" in a colour, underlined under the pointer - Build's "why ›" in the people teal, "Set the fee ›"; the Services cards' "Build for it ›" was one until 0.7.34, when a door to Build o... |
| 2620 | 7 | `public static Region sparkline(double[] series, List<Integer> months, String colour, double width, double height)` | A sparkline of a history series (0.7.28): the header tiles' own (UserInterface.Sparkline - the last ten years, a dot on the latest month, each January marked), at the caller's size: the Services KPI cells' 64 x 16. |

### THE PIECES THE INFRASTRUCTURE SCREEN ADDED (0.7.29) (lines 2628-2695)

| line | len | member | says |
|---:|---:|---|---|
| 2647 | 21 | `public static HBox heroCard(javafx.scene.Node left, double leftWidth, javafx.scene.Node right)` | A page's lead picture as a card (0.7.29): `left`, `leftWidth` wide, its words; `right`, taking the rest, the picture. |
| 2677 | 18 | `public static VBox details(String key, String caption, java.util.Set<String> open, Runnable redraw, java.util.function.Supplier...` | A fold (0.7.29): "details ▸ caption", and what is inside when it is open - built only then, by `body`. |

### rows on one scale (lines 2696-3096)

| line | len | member | says |
|---:|---:|---|---|
| 2705 | 9 | **type** `public record Run(double from, double amount, String colour, boolean hollow, String tip, Runnable go)` | One stretch of a row on a shared scale (0.7.29): where it starts and how long it is, in the scale's units; its colour; whether it is HOLLOW (outlined()) - outlined and faintly tinted, what is taken away or what is not... |
| 2707 | 3 | `public static Run of(double from, double amount, String colour)` _(in Pieces.Run)_ | A filled stretch. |
| 2710 | 1 | `public Run outlined()` _(in Pieces.Run)_ |  |
| 2711 | 1 | `public Run tip(String t)` _(in Pieces.Run)_ |  |
| 2712 | 1 | `public Run go(Runnable g)` _(in Pieces.Run)_ |  |
| 2727 | 34 | **type** `public record ScaleRow(String name, String figure, List<Run> runs, List<Tick> marks, String empty, String i...` | One row of scaleRows() (0.7.29): its name at the left, which wraps and is never cut; its runs; its figure at the right; and - each may be null - marks across this row alone (a good's "by lorry"), the words a row at no... |
| 2730 | 3 | `public static ScaleRow of(String name, String figure, List<Run> runs)` _(in Pieces.ScaleRow)_ |  |
| 2733 | 3 | `public static ScaleRow caption(String name, String info)` _(in Pieces.ScaleRow)_ |  |
| 2736 | 3 | `public ScaleRow marks(List<Tick> m)` _(in Pieces.ScaleRow)_ |  |
| 2739 | 3 | `public ScaleRow empty(String words)` _(in Pieces.ScaleRow)_ |  |
| 2742 | 3 | `public ScaleRow info(String i)` _(in Pieces.ScaleRow)_ |  |
| 2745 | 3 | `public ScaleRow tag(String t, String colour)` _(in Pieces.ScaleRow)_ |  |
| 2748 | 3 | `public ScaleRow icon(String svg)` _(in Pieces.ScaleRow)_ |  |
| 2751 | 3 | `public ScaleRow tip(String t)` _(in Pieces.ScaleRow)_ |  |
| 2754 | 3 | `public ScaleRow go(Runnable g)` _(in Pieces.ScaleRow)_ |  |
| 2757 | 3 | `public ScaleRow strong()` _(in Pieces.ScaleRow)_ |  |
| 2763 | 1 | **type** `public record Rule(double at, String colour, boolean dashed, String name, String tip, Runnable go)` | A rule through every row of scaleRows() (0.7.29): where, in the scale's units; its colour; dashed or solid; its name over the rows, its tooltip, and what a click on the name does (each may be null). |
| 2776 | 4 | `public static ScaleRows scaleRows(List<ScaleRow> rows, double scale, List<Rule> rules, double nameWidth, double figureWidth, do...` | Rows on one scale (0.7.29): each row's name in a column `nameWidth` wide, its bar - a track `band` tall with its runs laid on it where they start, on a scale of `scale` - and its figure in a column `figureWidth` wide;... |
| 2782 | 314 | **type** `public static final class ScaleRows extends javafx.scene.layout.Pane` | The stack scaleRows() draws: a Pane that lays its rows, their runs and its rules out at whatever width it is given. |
| 2797 | 127 | `ScaleRows(List<ScaleRow> rows, double scale, List<Rule> rules, double nameWidth, double figureWidth, double band)` _(in Pieces.ScaleRows)_ |  |
| 2935 | 37 | `private double[][] ruleNameAt(double w)` _(in Pieces.ScaleRows)_ | Where each rule's name goes at a width, as {left, line} per rule, and how many lines of names that takes (the last entry's line + 1, 0 with none named). |
| 2974 | 3 | `private double ruleRoom(double w)` _(in Pieces.ScaleRows)_ | The room the rules' names take over the rows, at a width. |
| 2979 | 7 | `private double nameRoom(int i, double w)` _(in Pieces.ScaleRows)_ | The room a row's name has, after its icon and its (i). |
| 2988 | 5 | `private double rowHeight(int i, double w)` _(in Pieces.ScaleRows)_ | A row's height: its name's lines, and never less than ROW (a caption a little less). |
| 2995 | 1 | `public javafx.geometry.Orientation getContentBias()` _(in Pieces.ScaleRows)_ | Its height follows its width - the rules' names take a second line when two would touch - so a parent asks at the width it will give. |
| 2997 | 6 | `protected double computePrefHeight(double width)` _(in Pieces.ScaleRows)_ |  |
| 3004 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.ScaleRows)_ |  |
| 3006 | 89 | `protected void layoutChildren()` _(in Pieces.ScaleRows)_ |  |

### THE PIECES THE SECTORS SCREEN ADDED (0.7.30) (lines 3097-3218)

| line | len | member | says |
|---:|---:|---|---|
| 3117 | 16 | `public static HBox pips(int n, int of, String colour, double size)` | Pips: `of` dots in a row, the first `n` filled in `colour` and the rest an outline - "4 of 6". |
| 3144 | 68 | `public static javafx.scene.layout.GridPane factGrid(List<ham.citybuildersim.Sector.Line> lines, int columns, java.util.function...` | A fact grid: a sector's lines as figures. |
| 3214 | 4 | `static String firstWords(String text)` | A note's first sentence, for a note shown on its own line with the whole of it behind the (i). |

### THE PIECES THE GOVERNMENT SCREEN ADDED (0.7.31) (lines 3219-3402)

| line | len | member | says |
|---:|---:|---|---|
| 3251 | 20 | `public static List<Slice> topSlices(List<String> names, List<Double> amounts, String[] ramp)` | Slices, biggest first, with everything past the ramp's last folded into one grey "Everything else" (RAMP_REST). |
| 3288 | 52 | `public static javafx.scene.layout.StackPane splitRing(List<Slice> slices, String top, String figure, String tone, double size, ...` | A ring split into its slices, with a caption and a figure in the hole - the Government's two (GovernmentScreen's own donut until 0.7.31). |
| 3355 | 47 | `public static VBox ringKey(List<Slice> slices, double width, Consumer<Slice> pick)` | The key beside a split ring: a row a slice - its swatch, its name (wrapping, never cut), its share of the arcs drawn and its money - each a click that hands its slice to `pick` (null: none). |

### ranked bars (lines 3403-3556)

| line | len | member | says |
|---:|---:|---|---|
| 3415 | 3 | **type** `public record RankRow(String key, String icon, String name, double amount, String colour, List<String> figu...` | One line of rankBars() (0.7.31): `key`, the name its open state is kept under; its icon (null: none) and its name, which wraps and is never cut; its amount, which the bar draws - leftward from the axis when it is belo... |
| 3437 | 85 | `public static VBox rankBars(List<RankRow> rows, double scale, double[] columns, double doorWidth, java.util.Set<String> open, R...` | Ranked bars (0.7.31): a row a line - its icon in a square tinted in its colour, its name and, when it opens, "▸ who"; its bar on one scale for every row, `scale` of the bar's units across (widened to the largest line ... |
| 3524 | 32 | **type** `static final class RankBar extends javafx.scene.layout.Pane` | The bar rankBars() draws for one row: a Pane that lays its track, its fill and its axis out at whatever width it is given. |
| 3528 | 14 | `RankBar(double amount, double pos, double neg, String colour)` _(in Pieces.RankBar)_ |  |
| 3543 | 12 | `protected void layoutChildren()` _(in Pieces.RankBar)_ |  |

### a bridge (lines 3557-3748)

| line | len | member | says |
|---:|---:|---|---|
| 3564 | 2 | **type** `public record Anchor(String caption, String icon, String figure, String line, List<javafx.scene.Node> chips...` | One figure a bridge walks from or to (0.7.31): its caption and its icon, the figure in large type, one line under it, any chips, and what a click on it does (null: nothing). |
| 3574 | 8 | **type** `public record BridgeStep(String label, double amount, String icon, String tip, Runnable go, boolean atNothing)` | One step between two anchors (0.7.31): its words, its amount as it moves the figure (+ adds, - takes away), its icon (null: none), its tooltip (null: none), what a click on it opens (null: nothing), and whether it is ... |
| 3575 | 3 | `public static BridgeStep of(String label, double amount, String icon)` _(in Pieces.BridgeStep)_ |  |
| 3578 | 1 | `public BridgeStep tip(String t)` _(in Pieces.BridgeStep)_ |  |
| 3579 | 1 | `public BridgeStep go(Runnable g)` _(in Pieces.BridgeStep)_ |  |
| 3580 | 1 | `public BridgeStep always()` _(in Pieces.BridgeStep)_ |  |
| 3601 | 21 | `public static HBox bridge(List<Anchor> anchors, List<List<BridgeStep>> steps, java.util.function.DoubleFunction<String> figure,...` | A bridge (0.7.31): the anchors as tiles with a column of steps between each pair and a muted "›" between each - steps.get(i) between anchor i and anchor i + 1. |
| 3624 | 6 | `static Label bridgeChevron()` | The muted "›" between a tile and a column. |
| 3632 | 37 | `static VBox bridgeTile(Anchor a)` | One anchor as a tile: caption and icon, the figure, its line, its chips. |
| 3671 | 35 | `static VBox bridgeColumn(List<BridgeStep> col, double scale, java.util.function.DoubleFunction<String> figure, String in, Strin...` | One column of a bridge's steps. |
| 3708 | 40 | `static HBox bridgeRow(BridgeStep s, double scale, java.util.function.DoubleFunction<String> figure, String in, String out)` | One step: sign, icon, words, bar, figure. |

### THE PIECES THE FINANCES SCREEN ADDED (0.7.32) (lines 3749-4022)

| line | len | member | says |
|---:|---:|---|---|
| 3778 | 9 | **type** `public record Column(String label, String sub, String figure, List<Segment> parts, boolean ghost, String ta...` | One column of columns() (0.7.32): its label and a second line under it (either may be null); its figure, written over its top (null: none - the caller says only what is worth saying); its segments, stacked from the fo... |
| 3782 | 4 | `public Column(String label, String sub, String figure, List<Segment> parts, boolean ghost, String tag, String tagColour, String...` _(in Pieces.Column)_ | ...whole, as every column was until 0.7.34. |
| 3799 | 3 | `public static Columns columns(List<Column> cols, double scale, double width, double height)` | Columns (0.7.32): each column's segments stacked from the foot on one scale - `scale` of its units to the plot's height, the largest column's total when it is 0 or less - the columns side by side across `width` (0 or ... |
| 3804 | 174 | **type** `public static final class Columns extends javafx.scene.layout.Pane` | The chart columns() draws: a Pane that lays its columns out at whatever width it is given. |
| 3817 | 97 | `Columns(List<Column> cols, double scale, double width, double height)` _(in Pieces.Columns)_ |  |
| 3915 | 62 | `protected void layoutChildren()` _(in Pieces.Columns)_ |  |
| 3986 | 36 | `public static VBox setting(String title, String info, String[] names, String current, String[] lines, String[] tips, Consumer<S...` | A setting (0.7.32): its title in capitals with an (i) holding `info` (either may be null), its choices as chips with `tips` as their tooltips (null: none), and under them the chosen one's line from `lines` - what the ... |

### THE PIECES THE BANK SCREEN ADDED (0.7.33) (lines 4023-4072)

| line | len | member | says |
|---:|---:|---|---|
| 4053 | 19 | `public static SegmentBar bandBar(double value, double min, double target, double top, String tone, double width, double band, b...` | A ratio in its band (0.7.33): a bar of `value` in `tone` on a scale of BAND_SCALE times the top, pinned at the end past it, with ticks at the minimum and the top in the muted grey and at the target in the heading ink ... |

### a ladder of rates (lines 4073-4193)

| line | len | member | says |
|---:|---:|---|---|
| 4087 | 18 | **type** `public record Rung(String svg, String colour, String name, String nameTone, List<Segment> parts, List<Tick>...` | One rung of rateLadder() (0.7.33): its icon and the icon's colour (null: none); its name, which wraps and is never cut, and the name's ink (null: the body's; a rung that is shut out reads red); its rate's parts as seg... |
| 4090 | 3 | `public static Rung of(String svg, String colour, String name, List<Segment> parts, double rate, String step)` _(in Pieces.Rung)_ |  |
| 4093 | 4 | `public static Rung caption(String heading)` _(in Pieces.Rung)_ |  |
| 4097 | 1 | `public Rung ticks(List<Tick> t)` _(in Pieces.Rung)_ |  |
| 4098 | 1 | `public Rung info(String i)` _(in Pieces.Rung)_ |  |
| 4099 | 1 | `public Rung go(Runnable g)` _(in Pieces.Rung)_ |  |
| 4100 | 1 | `public Rung tone(String c)` _(in Pieces.Rung)_ |  |
| 4101 | 1 | `public Rung tag(String t, String c)` _(in Pieces.Rung)_ |  |
| 4103 | 1 | `public Rung under()` _(in Pieces.Rung)_ | ...set under the rung before it. |
| 4119 | 74 | `public static VBox rateLadder(List<Rung> rungs, double scale, double nameWidth, double rateWidth, double stepWidth)` | A ladder of rates drawn in their parts (0.7.33): a row a rung - its icon, its name (in the accent with "›" when it is a door: a click opens where the rate is decided; in its own tone when it has one, a door or not), i... |

### two bars on one scale (lines 4194-4225)

| line | len | member | says |
|---:|---:|---|---|
| 4203 | 22 | `public static VBox balanceBars(String topWords, List<Segment> top, List<Tick> topTicks, String underWords, List<Segment> under,...` | Two bars on one scale (0.7.33): `top` and `under`, each a segment bar on `scale` with its own ticks, and each with its words in a column `labelWidth` wide at its left, which wrap and are never cut - the Bank's balance... |

### a status banner (lines 4226-4251)

| line | len | member | says |
|---:|---:|---|---|
| 4235 | 16 | `public static HBox statusBanner(String word, String tone, String sentence, String svg)` | A status banner (0.7.33): a state's icon in a square tinted in its colour, its word as a chip, and the model's own sentence for it, whole and wrapping - on the raised ground with a 3 px edge at the left in the same co... |

### THE BUTTON THAT ASKS TO BE PRESSED (0.7.34) (lines 4252-4477)

| line | len | member | says |
|---:|---:|---|---|
| 4287 | 1 | **type** `public enum Look` | How an action button looks: GO filled, ready; CHOOSE outlined, nothing chosen yet; CREDIT outlined, it goes ahead on a loan; HELD neutral, it cannot go ahead as it stands. |
| 4290 | 4 | **type** `public record Press(Look look, String main, String sub)` | What an action button says: its look, its words, and a smaller line under them (null: none). |
| 4292 | 1 | `public String words()` _(in Pieces.Press)_ | Both lines as one, for a probe and the reader's accessible text. |
| 4305 | 3 | `public static ActionButton actionButton(String svg, String accent, double tall, Press press, Runnable go)` | An action button: a Press in its look, and what a press does. |
| 4315 | 105 | **type** `public static final class ActionButton extends javafx.scene.layout.StackPane` | The action button (0.7.34): a region the height of `tall` or what its words need, as wide as its parent gives it, its words wrapping inside it; show() changes what it says in place - a card's stepper reprices it witho... |
| 4323 | 29 | `ActionButton(String svg, String accent, double tall, Press press, Runnable go)` _(in Pieces.ActionButton)_ |  |
| 4354 | 10 | `public ActionButton show(Press p)` _(in Pieces.ActionButton)_ | Says a new Press, restyled in place. |
| 4366 | 1 | `public Press press()` _(in Pieces.ActionButton)_ | What it says now. |
| 4369 | 1 | `public void onPress(Runnable go)` _(in Pieces.ActionButton)_ | What a press does from now on. |
| 4371 | 36 | `private void dress()` _(in Pieces.ActionButton)_ |  |
| 4408 | 3 | `protected double computeMinHeight(double width)` _(in Pieces.ActionButton)_ |  |
| 4412 | 3 | `protected double computePrefHeight(double width)` _(in Pieces.ActionButton)_ |  |
| 4416 | 3 | `protected double computeMaxHeight(double width)` _(in Pieces.ActionButton)_ |  |
| 4422 | 5 | `static String mix(String a, String b, double t)` | Two colours mixed, `t` of the way from `a` to `b`, as "#rrggbb". |
| 4436 | 30 | `public static HBox doorPill(String text, String svg, String accent, Runnable go)` | A door as a pill (0.7.34): a way to Build or the land office - "Build for it", "Build · Roads & transit", "Land office" - outlined in the area's colour, its icon, its words whole and an arrow; tinted a little deeper u... |
| 4471 | 6 | `private static void dressDoor(HBox pill)` | A door pill's style: its accent, tinted a little deeper under the pointer and deeper again pressed. |

### THE PIECES THE TRADE SCREEN ADDED (0.7.35) (lines 4478-4571)

| line | len | member | says |
|---:|---:|---|---|
| 4511 | 60 | `public static VBox bandMeter(String label, String reading, double at, double[] edges, String[] tones, String note, boolean muted)` | A meter with named bands and the city's mark on it (TradeScreen's THE THREE QUIET GAUGES until 0.7.35, moved unchanged; the gauge cards draw the same bands with bandTrack()). |

### a band track (lines 4572-4673)

| line | len | member | says |
|---:|---:|---|---|
| 4582 | 4 | `public static BandTrack bandTrack(double at, double[] edges, String[] tones, List<Tick> ticks, double width, double band, boole...` | A meter's bands at any width (0.7.35): the bands laid end to end in their tones, faint, each `edges[i]` the right-hand end of band i in a 0-to-1 space; the city's mark at `at` (0 to 1), standing proud of the band, or ... |
| 4588 | 85 | **type** `public static final class BandTrack extends javafx.scene.layout.Pane` | The track bandTrack() draws: a Pane that lays its bands, ticks and mark out at whatever width it is given. |
| 4597 | 40 | `BandTrack(double at, double[] edges, String[] tones, List<Tick> ticks, double width, double band, boolean muted)` _(in Pieces.BandTrack)_ |  |
| 4638 | 4 | `private boolean named()` _(in Pieces.BandTrack)_ |  |
| 4644 | 1 | `private double tall()` _(in Pieces.BandTrack)_ | The band, the mark standing 4 px proud of it each way, and a line of names under it when a tick has one. |
| 4646 | 26 | `protected void layoutChildren()` _(in Pieces.BandTrack)_ |  |

### a gauge card (lines 4674-4720)

| line | len | member | says |
|---:|---:|---|---|
| 4684 | 36 | `public static VBox gaugeCard(String svg, String colour, String title, String info, String reading, String readingTone, String c...` | A gauge as a card (0.7.35): its icon in a tinted square, its title and an (i) holding `info`; the reading in mono 22 beside its verdict as a chip in `chipTone` (no chip when `chip` is null); the bands with the city's ... |

### mirrored bars (lines 4721-4948)

| line | len | member | says |
|---:|---:|---|---|
| 4731 | 2 | **type** `public record Mirror(String svg, String name, double left, double right, String leftFigure, String rightFig...` | One row of mirrorRows() (0.7.35): its icon (null: none) and its name, which wraps and is never cut; what goes left of the axis and what goes right, in the rows' one unit, each with its figure as written (null: the sid... |
| 4744 | 4 | `public static MirrorRows mirrorRows(List<Mirror> rows, double scale, String leftColour, String rightColour, String leftHead, St...` | Mirrored bars (0.7.35): a row each, its icon and name at the left, then a lane either side of a centre axis - `left` growing leftward from it in `leftColour`, `right` rightward in `rightColour`, both on `scale` (0 or ... |
| 4750 | 198 | **type** `public static final class MirrorRows extends javafx.scene.layout.Pane` | The rows mirrorRows() draws: a Pane that lays them out at whatever width it is given. |
| 4766 | 59 | `MirrorRows(List<Mirror> rows, double scale, String leftColour, String rightColour, String leftHead, String rightHead, double na...` _(in Pieces.MirrorRows)_ |  |
| 4826 | 1 | `private static double finite(double v)` _(in Pieces.MirrorRows)_ |  |
| 4829 | 1 | `public Region row(int i)` _(in Pieces.MirrorRows)_ | Row i's ground - the node its click is handed, to anchor a popover - or null past the rows. |
| 4831 | 15 | `private Label head(String text, String colour)` _(in Pieces.MirrorRows)_ |  |
| 4847 | 6 | `private Region bar(String colour)` _(in Pieces.MirrorRows)_ |  |
| 4854 | 8 | `private Label figure(String text)` _(in Pieces.MirrorRows)_ |  |
| 4864 | 7 | `public void grow(double millis)` _(in Pieces.MirrorRows)_ | The bars grow out of the axis over `millis` - a month landing on the page. |
| 4872 | 1 | `private double headRoom()` _(in Pieces.MirrorRows)_ |  |
| 4875 | 1 | `private double nameRoom()` _(in Pieces.MirrorRows)_ | The name column's own width: what is left of it after the icon. |
| 4877 | 3 | `private double rowHeight(int i)` _(in Pieces.MirrorRows)_ |  |
| 4881 | 1 | `public javafx.geometry.Orientation getContentBias()` _(in Pieces.MirrorRows)_ |  |
| 4883 | 5 | `protected double computePrefHeight(double width)` _(in Pieces.MirrorRows)_ |  |
| 4889 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.MirrorRows)_ |  |
| 4891 | 56 | `protected void layoutChildren()` _(in Pieces.MirrorRows)_ |  |

### diverging bars (lines 4949-5104)

| line | len | member | says |
|---:|---:|---|---|
| 4959 | 1 | **type** `public record Force(String name, double value, boolean total, boolean ghost, String info, String line)` | One row of divergingBars() (0.7.35): its name; its value, signed - below nothing drawn left of the centre line, above it right; a TOTAL row is the model's own sum, drawn in the stronger colour; a GHOST row is outlined... |
| 4972 | 5 | `public static DivergingBars divergingBars(List<Force> rows, double scale, double nameWidth, double figureWidth, String colour, ...` | Diverging bars (0.7.35): a row each - its name with its (i) and line in a column `nameWidth` wide, a lane split by a centre line, the bar from the line to the value on a scale of plus or minus `scale` (one past it sto... |
| 4979 | 125 | **type** `public static final class DivergingBars extends javafx.scene.layout.Pane` | The rows divergingBars() draws: a Pane that lays them out at whatever width it is given. |
| 4991 | 55 | `DivergingBars(List<Force> rows, double scale, double nameWidth, double figureWidth, String colour, String totalColour, String l...` _(in Pieces.DivergingBars)_ |  |
| 5047 | 8 | `private Label words(String text)` _(in Pieces.DivergingBars)_ |  |
| 5056 | 1 | `private double headRoom()` _(in Pieces.DivergingBars)_ |  |
| 5058 | 1 | `private double rowHeight(int i)` _(in Pieces.DivergingBars)_ |  |
| 5060 | 5 | `protected double computePrefHeight(double width)` _(in Pieces.DivergingBars)_ |  |
| 5066 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.DivergingBars)_ |  |
| 5068 | 35 | `protected void layoutChildren()` _(in Pieces.DivergingBars)_ |  |

### THE PIECES THE POLICY SCREEN ADDED (0.7.36) (lines 5105-5279)

| line | len | member | says |
|---:|---:|---|---|
| 5128 | 26 | **type** `public record Effect(String label, double before, double after, java.util.function.DoubleFunction<String> f...` | One effect of a move (0.7.36): its words; the figure before and after, written by `fmt`; the move written by `delta` (null: no chip); the bars' colour; the move's VERDICT colour when the after crosses a line the model... |
| 5132 | 3 | `public static Effect of(String label, double before, double after, java.util.function.DoubleFunction<String> fmt)` _(in Pieces.Effect)_ | An effect in money's blue with no chip, no verdict and its own scale. |
| 5135 | 3 | `public Effect delta(java.util.function.DoubleFunction<String> d)` _(in Pieces.Effect)_ |  |
| 5138 | 1 | `public Effect colour(String c)` _(in Pieces.Effect)_ |  |
| 5139 | 1 | `public Effect verdict(String v)` _(in Pieces.Effect)_ |  |
| 5140 | 1 | `public Effect scale(double s)` _(in Pieces.Effect)_ |  |
| 5141 | 1 | `public Effect info(String i)` _(in Pieces.Effect)_ |  |
| 5143 | 4 | `public boolean moved()` _(in Pieces.Effect)_ | Whether the move shows: the two figures are written differently, or the move's chip says something (a $52k move on a $5.1M budget). |
| 5148 | 5 | `public String words()` _(in Pieces.Effect)_ | The row in words, for a probe: "Profit tax $23.3M -> $24.8M (+$1.5M)". |
| 5163 | 50 | `public static VBox beforeAfter(Effect e, double width)` | An effect as a row (0.7.36): its words at the left, wrapping and never cut, with its (i); at the right the figure before and, once something has moved it, an arrow and the after in the accent and the move as a chip; u... |
| 5215 | 21 | `public static HBox stagedChip(String text, Runnable remove)` | A staged change as a chip in the tray (0.7.36): its words in the accent, and a × that takes it back out of the set. |
| 5245 | 34 | `public static HBox stagedTray(List<? extends javafx.scene.Node> chips, Effect budget, javafx.scene.Node apply, javafx.scene.Nod...` | The staged tray (0.7.36): what a page has staged, as chips that each come out with their ×, THE BUDGET before and after the set (`budget`, null: none), and the page's Apply and Discard - on the raised ground with an a... |

### THE PIECES CITY HISTORY ADDED (0.7.37) (lines 5280-5411)

| line | len | member | says |
|---:|---:|---|---|
| 5295 | 11 | **type** `public static final class CardHead extends HBox` | A chart card's head (0.7.37): its parts, so a month landing can count the figure up and a click can open the line. |
| 5299 | 6 | `CardHead(Label name, Label figure, Label change)` _(in Pieces.CardHead)_ |  |
| 5314 | 28 | `public static CardHead chartCardHead(String name, String figure, String change, Runnable open, javafx.scene.Node...right)` | A chart card's head (0.7.37): `name` in the label grey, `figure` in Plex Mono 17, `change` beside it in the body's ink - never a verdict's colour: a line rising is not good news by rising - then a gap and whatever `ri... |
| 5352 | 3 | `public static RangeBar rangeBar(double lo, double hi, double first, double last, String colour, double width)` | A range bar (0.7.37): where a line ended in its own range over a window. |
| 5357 | 54 | **type** `public static final class RangeBar extends javafx.scene.layout.Pane` | The bar rangeBar() draws: a Pane that lays its track, its tick and its dot out at whatever width it is given. |
| 5365 | 19 | `RangeBar(double lo, double hi, double first, double last, String colour, double width)` _(in Pieces.RangeBar)_ |  |
| 5386 | 1 | `public boolean flat()` _(in Pieces.RangeBar)_ | Whether the line never moved over the window: one dot, no tick. |
| 5389 | 4 | `public double share(double v)` _(in Pieces.RangeBar)_ | Where a value sits on the track, 0 at the low and 1 at the high; a flat line's one value in the middle. |
| 5395 | 8 | `public void slideFrom(double was, double millis)` _(in Pieces.RangeBar)_ | The dot slides to where it ends from `was` (a share of the track) over `millis` - a month landing on the page. |
| 5404 | 6 | `protected void layoutChildren()` _(in Pieces.RangeBar)_ |  |

### THE PIECES THE FUND ADDED (0.7.39) (lines 5412-5492)

| line | len | member | says |
|---:|---:|---|---|
| 5434 | 18 | `public static javafx.scene.control.TextField searchBox(String text, String prompt, double width, Consumer<String> typed, Runnab...` | A search box (0.7.39): `text` in it, `prompt` when empty, `width` wide; `typed` on every key, `entered` on Enter. |
| 5454 | 1 | **type** `public record Pnl(String text, String tone)` | A gain or a loss in words and its colour: "▲ +D$149.6M (+420.2%)" green, "▼ −D$5,141 (−87.0%)" red, "▲ +D$368 (+0.2%)" and "D$0" in the body's ink. |
| 5462 | 15 | `public static Pnl pnl(String sym, double amount, double share)` | P&L (0.7.39, the spec's D7): `amount` in the city's money with its mark `sym`, signed, with an arrow, and `share` of the cost after it as a percentage (NaN: none). |
| 5479 | 4 | `public static String tidyMoney(String shown)` | Money.money()'s words at a unit's edge in the next unit's: "$1000k" is "$1.0M", "$1000.0M" "$1.0B", "$1000.0B" "$1.0T" (0.7.39) - none of which Money writes since 0.7.40 (tightMoney()), so it changes nothing now. |
| 5485 | 7 | `public static Label pnlLabel(String sym, double amount, double share, double size)` | ...as a figure that is never cut, at a size. |

### WHO GOES WITHOUT (0.7.45) (lines 5493-5552)

| line | len | member | says |
|---:|---:|---|---|
| 5507 | 2 | **type** `public record Hunger(double rate, double pricedOut, double shortOfStock, String figure, String pricedOutWor...` | WHO GOES WITHOUT's words (pure: the probe reads them): the figure, each half's key, whether the shelves are the larger half, the sentence and the (i). |
| 5517 | 9 | `public static Hunger hunger(Game g)` |  |
| 5528 | 6 | `public static String smallShare(double share)` | A share of the people, to one place, two under a tenth of a point: "26.8%", "0.04%", "none". |
| 5536 | 16 | `public static VBox whoGoesWithout(Game g, Runnable shops)` | WHO GOES WITHOUT as a card: the figure, the two halves on a bar of the whole city, the sentence, and a door to the shops when they are the larger half. |


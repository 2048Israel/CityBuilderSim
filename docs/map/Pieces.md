# Pieces.java - 5,240 lines · 229 methods · 26 constants · interface

`ham/citybuildersim/ui/Pieces.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

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
> and a gain or a loss in its colour.
> 
> Static for the same reason as Money and Statement: no state, used
> everywhere, called unqualified through an import static. The scroller is
> not here: scrolled() reads the shell's current screen, so it stayed there.

**Uses:** [Palette](Palette.md) (556), [DebtQuote](DebtQuote.md) (4), [ChartModel](ChartModel.md) (3), [NationalAccounts](NationalAccounts.md) (3), [UserInterface](UserInterface.md) (3), [Sector](Sector.md) (3), [Money](Money.md) (2), [PayTier](PayTier.md) (1), [TimeChart](TimeChart.md) (1), [Icons](Icons.md) (1), [DebtManager](DebtManager.md) (1), [BuildScreen](BuildScreen.md) (1)

**Used by (12):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [HistoryScreen](HistoryScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 58 | A FIGURE THAT GOES WHERE IT IS DECIDED. |
| 333 | · THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): |
| 439 | · · where the recording starts |
| 465 | · · the scale |
| 496 | · · the grid |
| 505 | · · the years (0.7.23) |
| 532 | · · the lines |
| 561 | · · the labels |
| 578 | · · the key |
| 748 | TEXT IN THREE LAYERS: THE (i) (0.7.21) |
| 921 | AN ICON, AND A RING (0.7.24) |
| 1037 | THE PIECES A REDRAWN SCREEN IS BUILT FROM (0.7.26) |
| 1166 | · a bar of segments |
| 1353 | · a loan as a card |
| 1472 | THE PIECES THE PEOPLE PAGE ADDED (0.7.27) |
| 1559 | · a ring as a card |
| 1635 | · a waterfall |
| 1840 | · a bullet bar |
| 1867 | THE PIECES THE SERVICES SCREEN ADDED (0.7.28) |
| 1890 | · a cause bar |
| 2072 | · a supply bar |
| 2103 | · a funnel |
| 2163 | · an effect scale |
| 2261 | · cohort bars |
| 2358 | · a door, and a spark |
| 2395 | THE PIECES THE INFRASTRUCTURE SCREEN ADDED (0.7.29) |
| 2463 | · rows on one scale |
| 2858 | THE PIECES THE SECTORS SCREEN ADDED (0.7.30) |
| 2980 | THE PIECES THE GOVERNMENT SCREEN ADDED (0.7.31) |
| 3164 | · ranked bars |
| 3318 | · a bridge |
| 3510 | THE PIECES THE FINANCES SCREEN ADDED (0.7.32) |
| 3784 | THE PIECES THE BANK SCREEN ADDED (0.7.33) |
| 3834 | · a ladder of rates |
| 3955 | · two bars on one scale |
| 3987 | · a status banner |
| 4013 | THE BUTTON THAT ASKS TO BE PRESSED (0.7.34) |
| 4226 | THE PIECES THE TRADE SCREEN ADDED (0.7.35) |
| 4320 | · a band track |
| 4422 | · a gauge card |
| 4469 | · mirrored bars |
| 4697 | · diverging bars |
| 4853 | THE PIECES THE POLICY SCREEN ADDED (0.7.36) |
| 5028 | THE PIECES CITY HISTORY ADDED (0.7.37) |
| 5160 | THE PIECES THE FUND ADDED (0.7.39) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 4048 | `Pieces.Look.GO` |  |
| 4048 | `Pieces.Look.CHOOSE` |  |
| 4048 | `Pieces.Look.CREDIT` |  |
| 4048 | `Pieces.Look.HELD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 118 | `Pieces.LIMIT_CELL` | `190` | How wide a limit cell is, its padding included; its note wraps inside it. |
| 768 | `Pieces.POPOVER_WIDTH` | `360` | How wide a popover's text wraps. |
| 771 | `Pieces.MORE_IN_THE_MANUAL` | `"More in the manual"` | The line a popover may end with: the third layer's door, with no link until the manual has one. |
| 813 | `Pieces.INFO_SIZE` | `16` | How big the (i) is drawn: its 24-unit grid at this many pixels. |
| 919 | `Pieces.TILE_GAP` | `10` | The gap between cards in a row or a grid: Build's cards in their flow, the land office's shelf. |
| 1058 | `Pieces.PAGE_WIDE` | `1480` | The widest a redrawn page is laid out - Build's Overview and categories since 0.7.24, the land office since 0.7.26 - so a 1,920 window does not stretch a row of cards across the glass. |
| 1692 | `Pieces.Waterfall.FIGURE_ROOM` | `16, NAME_ROOM = 12, ICON = 13` | The figures' line over the plot, the least the names under it take, and an icon's size. |
| 1910 | `Pieces.CAUSE_LABEL_ROOM` | `70` | How wide a part must be drawn to carry its name and figure under it; a narrower one is keyed. |
| 1936 | `Pieces.CauseBar.GAP` | `2, UNDER = 3, KEY_GAP = 12, KEY_ROW = 3` | Between the parts, under the bar, and between the key's entries and rows. |
| 2214 | `Pieces.EffectScale.TALL` | `44, Y = 22, DOT = 10` | Its height, how far down it the line runs, and the mark's size. |
| 2290 | `Pieces.CohortBars.AXIS` | `15` | Room under the bars for the near and far names. |
| 2551 | `Pieces.ScaleRows.ROW` | `26, GAP = 10, RULE_NAMES = 16, TAG_GAP = 6, ICON = 12` | A row's least height, the gap either side of the bar, the room the rules' names take over the rows, the gap before a tag, and an icon's size. |
| 3034 | `Pieces.EVERYTHING_ELSE` | `"Everything else"` | What topSlices() calls the slices past its ramp, folded into one. |
| 3181 | `Pieces.RANK_BAND` | `12, RANK_ROW = 36` | How tall a ranked bar's band is, and the least a row takes. |
| 3184 | `Pieces.RANK_KEY` | `"rankBars.key"` | The property a ranked bar's row carries its line's key under, so a screen can find the row to scroll to. |
| 3345 | `Pieces.BRIDGE_TILE` | `200, BRIDGE_BAR = 56, BRIDGE_FIGURE = 64` | A bridge's tiles' width, its step bars' and its figures' (0.7.31). |
| 3348 | `Pieces.BRIDGE_NOTHING` | `.5` | A step under this, in the model's thousands, is nothing: half a thousand, below which signedTight() writes "$0". |
| 3567 | `Pieces.Columns.TOP` | `34, FOOT = 30, GAP = 8` | The room over the plot for a tag and a figure, under it for a label and its second line, and the gap between columns. |
| 3803 | `Pieces.BAND_SCALE` | `1.6` | How far past the top of its band a ratio's bar runs, as a multiple of the top: the band sits in the left of it, so a ratio well over its band reads as full (BankScreen's capital band's since 0.7.9). |
| 3868 | `Pieces.RUNG_ROW` | `30, RUNG_BAND = 12` | A rung's least height, and its bar's band. |
| 4057 | `Pieces.ACTION_TALL` | `40` | An action button's height on a card, where it is the thing the card is for. |
| 4060 | `Pieces.ACTION_INLINE` | `32` | ...beside a heading, where it shares a row with words: Build's "Build all three", the land office's "Buy the next 5". |
| 4063 | `Pieces.DOOR_TALL` | `30` | A door pill's height. |
| 4512 | `Pieces.MirrorRows.ROW` | `28, BAR = 12, ICON = 18, GAP = 8` | A row's least height, its bar's thickness, an icon's size and the gaps between the columns. |
| 4737 | `Pieces.DivergingBars.ROW` | `30, BAR = 12` | A row's least height and its bar's thickness. |
| 5107 | `Pieces.RangeBar.TRACK` | `6, DOT = 10, TICK_W = 5, TICK_H = 14, PAD = 6` | The track's height, the dot's size, the tick's width and height, and the room either end so the dot is never cut. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 774 | `private static javafx.stage.Popup popover` | The popover showing now, so opening another puts the first away. |
| 1210 | `private final List<Segment> parts` |  |
| 1211 | `private final List<Tick> ticks` |  |
| 1212 | `private final double scale, band` |  |
| 1213 | `private final Region track` |  |
| 1214 | `private final List<Region> drawn` |  |
| 1215 | `private final List<Label> labels` |  |
| 1216 | `private final List<Region> marks` |  |
| 1218 | `private final javafx.beans.property.DoubleProperty first` | The first segment's amount while it grows (animateFirst()), or NaN when it stands still. |
| 1680 | `private final List<Step> steps` |  |
| 1681 | `private final double[] from, to` |  |
| 1682 | `private final double low, high` |  |
| 1683 | `private final List<List<Region>> bars` |  |
| 1684 | `private final List<Label> figures` |  |
| 1685 | `private final List<javafx.scene.Node> icons` |  |
| 1686 | `private final List<javafx.scene.shape.Line> links` |  |
| 1687 | `private final javafx.scene.shape.Line zero` |  |
| 1689 | `private final javafx.beans.property.DoubleProperty grown` | How far the bars have grown out of the zero line, 0 to 1 (animate()). |
| 1930 | `private final List<Part> parts` |  |
| 1931 | `private final double band, total` |  |
| 1932 | `private final List<Region> blocks` |  |
| 1933 | `private final List<VBox> names` |  |
| 1934 | `private final List<HBox> keys` |  |
| 2210 | `private final double at` |  |
| 2211 | `private final Region line` |  |
| 2212 | `private final Label now, none, all` |  |
| 2283 | `private final double[] counts` |  |
| 2284 | `private final double peak` |  |
| 2285 | `private final int dangerFrom` |  |
| 2286 | `private final List<Region> bars` |  |
| 2287 | `private final javafx.scene.shape.Line divider` |  |
| 2288 | `private final Label dividerName, near, far` |  |
| 2552 | `private final List<ScaleRow> rows` |  |
| 2553 | `private final List<Rule> rules` |  |
| 2554 | `private final double scale, nameWidth, figureWidth, band` |  |
| 2555 | `private final List<Label> names` |  |
| 2556 | `private final List<Label> empties` |  |
| 2557 | `private final List<Label> overs` |  |
| 2558 | `private final List<javafx.scene.Node> infos` |  |
| 2559 | `private final List<Region> tracks` |  |
| 2560 | `private final List<List<Region>> drawn` |  |
| 2561 | `private final List<javafx.scene.shape.Line> lines` |  |
| 2562 | `private final List<Label> ruleNames` |  |
| 3286 | `private final double amount, pos, neg` |  |
| 3287 | `private final Region track` |  |
| 3568 | `private final List<Column> cols` |  |
| 3569 | `private final double scale` |  |
| 3570 | `private final List<List<Region>> drawn` |  |
| 3571 | `private final List<Label> figures` |  |
| 3573 | `private final List<javafx.scene.Node> tags` |  |
| 3574 | `private final List<Region> hits` |  |
| 3576 | `private final List<javafx.scene.shape.Shape[]> breaks` | A broken column's break (0.7.34): the gap, in the card's ground, and its two slashes; null for a whole column. |
| 4077 | `private final String svg, accent` |  |
| 4078 | `private final double tall` |  |
| 4079 | `private final HBox row` |  |
| 4080 | `private final Label main` |  |
| 4081 | `private Press press` |  |
| 4082 | `private Runnable go` |  |
| 4337 | `private final double at, band` |  |
| 4338 | `private final double[] edges` |  |
| 4339 | `private final List<Tick> ticks` |  |
| 4340 | `private final boolean muted` |  |
| 4341 | `private final List<Region> bands` |  |
| 4342 | `private final List<Label> names` |  |
| 4343 | `private final Region mark` |  |
| 4499 | `private final List<Mirror> rows` |  |
| 4500 | `private final double scale, nameWidth, netWidth` |  |
| 4501 | `private final List<javafx.scene.Node> icons` |  |
| 4502 | `private final List<Label> names` |  |
| 4504 | `private final List<Region> leftBars` |  |
| 4506 | `private final Region axis` |  |
| 4507 | `private final Label leftHead, rightHead` |  |
| 4509 | `private final javafx.beans.property.DoubleProperty grown` | How far the bars have grown out of the axis, 0 to 1 (grow()). |
| 4728 | `private final List<Force> rows` |  |
| 4729 | `private final double scale, nameWidth, figureWidth` |  |
| 4730 | `private final List<VBox> names` |  |
| 4731 | `private final List<Region> bars` |  |
| 4732 | `private final List<Label> figures` |  |
| 4733 | `private final Region axis` |  |
| 4734 | `private final Label leftWords, rightWords` |  |
| 5045 | `public final Label name, figure, change` | The line's name, its figure now, and its move over the window. |
| 5108 | `private final double lo, hi, first, last` |  |
| 5109 | `private final Region track` |  |
| 5111 | `private final javafx.beans.property.DoubleProperty at` | Where the dot is drawn, as a share of the track: the end's own, or on its way there (slideFrom()). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 5190 | **type** `public final class Pieces` | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a stacked bar, a limit cell - and, under the divider half way down,... |
| 54 | 3 | `public static VBox limitCell(String label, String value, String note, String tone)` | One figure in the constraints bar: what it is, what it reads, what it means. |

### A FIGURE THAT GOES WHERE IT IS DECIDED. (lines 58-332)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 38 | `public static VBox limitCell(String label, String value, String note, String tone, String where, Runnable go)` |  |
| 127 | 20 | `public static javafx.scene.layout.FlowPane chipStrip(String[] names, String current, int size, Consumer<String> pick)` | A row of chips, which is the build strip's shape at two sizes. |
| 154 | 12 | `public static javafx.scene.layout.FlowPane chipStrip(String[] names, String[] icons, String current, int size, Consumer<String>...` | ...with an icon before each name (0.7.29: the Infrastructure tab's pages, the road, the bus, the train and the lorry), in the accent while its page is open and the label grey otherwise. |
| 168 | 23 | `public static HBox vitalsBar(VBox...cells)` | The bar itself: its cells - four, and People's five since 0.7.27 - the last one without its dividing rule. |
| 193 | 8 | `public static Label sentence(String text, String tone)` | A paragraph in the statement column: wrapped, at body size, in a tone. |
| 203 | 6 | `public static Label subHead(String text)` | A heading inside a section, for a table that needs naming. |
| 218 | 17 | `public static VBox alert(String heading, String body)` | Something that is going wrong, in a block of its own. |
| 237 | 13 | `public static javafx.scene.layout.GridPane grid(double[] widths, javafx.geometry.HPos[] align)` | A table with fixed columns, each aligned the way its figures want. |
| 252 | 8 | `public static void gridHead(javafx.scene.layout.GridPane table, String...names)` | Row zero: the column names, in the label grey, aligned as their column is. |
| 262 | 6 | `public static javafx.geometry.HPos[] rightAfterFirst(int columns)` | Right-hand columns are all the same width in every table on this screen. |
| 270 | 9 | `public static Label gridCell(String text, String tone, int size, boolean rightAlign)` | One cell of a table: a figure right, a name left. |
| 280 | 5 | `public static Label monoLabel(String text)` |  |
| 287 | 1 | **type** `public record Slice(String name, double amount, String colour)` | A named amount, with the colour it was assigned and what it opens into. |
| 296 | 3 | `public static VBox stackedBar(java.util.List<Slice> parts, double width)` | A part-to-whole bar, which is the right form when the parts can be negative and a ring cannot be drawn at all. |
| 305 | 27 | `public static VBox stackedBar(java.util.List<Slice> parts, double width, java.util.function.DoubleFunction<String> figure)` | ...with each part's tooltip figure written by `figure` (0.7.27): the bar printed money whatever it held, so it could not carry people. |

### THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass): (lines 333-747)

| line | len | member | says |
|---:|---:|---|---|
| 339 | 5 | `public static void showIf(javafx.scene.Node node, boolean visible)` | Show or hide an overlay without it still taking up its space. |
| 352 | 3 | `public static String cell(double value)` | A count, or a dot where there is nothing - a grid of zeros reads as data. |
| 363 | 10 | `public static String shortTier(PayTier tier)` | Pay tiers, shortened to fit six across. |
| 387 | 5 | `public static String flowText(double value)` | A monthly flow of people, in whole people (0.7.20). |
| 397 | 4 | `public static String flowSigned(String sign, double value)` | The same with its direction in front - "+12", "-3" - and none on a flow that reads "0" or "under 1", which have no direction to give. |
| 423 | 3 | `public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours)` | A small line chart, in the statement's own language. |
| 433 | 166 | `public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours, java.util.function.Doubl...` | ...with the figures written by `figure` rather than by the first series' name (chartFigure()): for a chart whose unit no name says - a rate, a ratio. |
| 601 | 6 | `public static double latest(double[] series)` | The last recorded value of a series, or NaN if there is none. |
| 615 | 12 | `public static String chartFigure(double value, String name)` | A figure on a chart axis, in whatever the series is counted in. |
| 628 | 12 | `public static HBox keySwatch(String colour, String name)` |  |
| 642 | 9 | `public static Label stepChip(String text, Runnable act, boolean quiet)` | A small clickable chip that does something rather than picking a page. |
| 659 | 3 | `public static VBox keyedBar(java.util.List<Slice> parts, double width)` | A stacked bar with its own swatch legend under it. |
| 664 | 23 | `public static VBox keyedBar(java.util.List<Slice> parts, double width, java.util.function.DoubleFunction<String> figure)` | ...with its tooltips' figures written by `figure` (0.7.27), as stackedBar's. |
| 701 | 6 | `public static double annualGdp(NationalAccounts na)` | A year of output — annualised when the city has not lived a year yet. |
| 713 | 4 | `public static String monthsWait(double months)` | A build time, as every place that quotes one writes it (0.7.20): "~8 mo", "under a month" below one, "stalled" with no site output (NaN) - the order line's own forms. |
| 727 | 4 | `public static String atTodaysQueue(double months)` | ...and said for what it is (0.7.20): "~5 mo at today's queue". |
| 739 | 3 | `public static String ofAnnualGdp(NationalAccounts na)` | What a ratio to that year of output is OF (0.7.20): "of annual GDP", or "of GDP, annualised" while the year is scaled up from fewer than twelve months - so a founding city's "169.7%" says which kind of year it is agai... |
| 743 | 4 | `public static boolean gdpEstimated(NationalAccounts na)` |  |

### TEXT IN THREE LAYERS: THE (i) (0.7.21) (lines 748-920)

| line | len | member | says |
|---:|---:|---|---|
| 783 | 28 | `public static javafx.scene.Node infoButton(String whole, boolean manual)` | The (i): a small circled i that opens `whole` in a popover under it. |
| 819 | 10 | `public static HBox infoLine(String shown, String whole, boolean manual, int size, String tone, double wide)` | A short line with its (i): `shown` on the screen, `whole` one click away. |
| 831 | 15 | `static void openPopover(javafx.scene.Node anchor, String whole, boolean manual)` | The popover itself: the whole text on the raised ground, under the (i), until a click elsewhere or Esc. |
| 852 | 18 | `public static void openPopover(javafx.scene.Node anchor, javafx.scene.Node body)` | ...or anything else on the same card (0.7.27): the People page's "Moved in" opens a skill bar with its note under it. |
| 872 | 3 | `public static void closePopover()` | The popover put away, if one is open: clearMenu() calls it when the screen changes; a redraw of the same screen (a month landing) leaves it up. |
| 877 | 40 | `public static HBox payerRow(String who, double amount, double total, String rate)` | One payer inside an opened line: who, how much, at what rate. |

### AN ICON, AND A RING (0.7.24) (lines 921-1036)

| line | len | member | says |
|---:|---:|---|---|
| 936 | 22 | `public static Region icon(String svg, String colour, double size)` | An outline icon from Icons, stroked in a colour at a size: its 24-unit grid in a Pane of exactly that size, scaled - the rail's way (UserInterface.railButton()), so every icon sits at the coordinates it was drawn at w... |
| 966 | 44 | `public static Region ring(double share, String colour, double size, double stroke, String label, double labelSize)` | A ring: a track, an arc of `share` round it from twelve o'clock in a colour, and a label in the middle - or a tick, for "nothing needed". |
| 1016 | 20 | `public static Region bar(double share, String colour, double width, double height)` | A bar: a track the width given and a fill of `share` of it, in a colour - the Build cards' two bars (0.7.24 on the city's, every building's since 0.7.25). |

### THE PIECES A REDRAWN SCREEN IS BUILT FROM (0.7.26) (lines 1037-1165)

| line | len | member | says |
|---:|---:|---|---|
| 1061 | 7 | `public static VBox widePage()` | A page as wide as the stage, up to PAGE_WIDE, with the room under it every page keeps. |
| 1070 | 3 | `public static HBox sectionHead(String title, javafx.scene.Node right)` | A section's heading, and whatever sits at its right. |
| 1079 | 14 | `public static HBox sectionHead(String title, String info, javafx.scene.Node right)` | ...with an (i) after the heading when `info` is not null (0.7.26: the land office's "ON OFFER", whose paragraph moved in there). |
| 1095 | 5 | `public static Label hint(String text)` | A section heading's quiet words at its right: "tap one to build for it". |
| 1102 | 14 | `public static javafx.scene.layout.GridPane equalColumns(int n, double gap)` | A grid of equal columns, as wide as its page. |
| 1118 | 8 | `public static Region iconSquare(String svg, String colour, double box, double size)` | An icon in a rounded square tinted with its colour, as the mockups set one beside a heading (0.7.24; the window's until 0.7.26). |
| 1133 | 8 | `public static Label tag(String text, String colour)` | A tag: a few words in a pill outlined and tinted in a colour - a Build card's "cheapest per resident" in green, and since 0.7.26 the land office's BEST VALUE (green), ORE and MOST ORE (Palette.ORE) and NEW (the buildi... |
| 1143 | 22 | `public static Button stepper(String glyph, double width, Runnable go)` | A small square button in a quantity stepper: Build's "−", "+", "+10", "+100" and "↺", the land office's "−" and "+". |

### a bar of segments (lines 1166-1352)

| line | len | member | says |
|---:|---:|---|---|
| 1175 | 7 | **type** `public record Segment(double amount, String colour, boolean ghost, String stripe, String label, String tip,...` | One stretch of a segment bar: how much of the bar's units, its colour, and - each may be null - whether it is a GHOST (outlined and tinted, not filled: what would be there, not what is), a stripe along its foot in ano... |
| 1178 | 3 | `public static Segment of(double amount, String colour)` _(in Pieces.Segment)_ | A plain filled stretch. |
| 1184 | 1 | **type** `public record Tick(double at, String colour, double width, String name, String tip)` | A mark across a segment bar: where, in the bar's units; its colour and width in pixels; its name under the bar, and a tooltip (either may be null). |
| 1203 | 4 | `public static SegmentBar segmentBar(List<Segment> parts, double scale, List<Tick> ticks, double width, double band)` | A bar of segments and ticks (0.7.26): a track, the segments laid end to end from the left, each `amount` of `scale` (scale 0 or less: their sum), and the ticks drawn across it, each named under the bar if it has a name. |
| 1209 | 143 | **type** `public static final class SegmentBar extends javafx.scene.layout.Pane` | The bar segmentBar() draws: a Pane that lays its parts out at whatever width it is given. |
| 1221 | 61 | `SegmentBar(List<Segment> parts, double scale, List<Tick> ticks, double width, double band)` _(in Pieces.SegmentBar)_ |  |
| 1284 | 1 | `private double proud()` _(in Pieces.SegmentBar)_ | How far the ticks stand proud of the band, each way. |
| 1287 | 4 | `private boolean named()` _(in Pieces.SegmentBar)_ | Whether any tick carries a name, which takes a line under the bar. |
| 1293 | 1 | `private double tall()` _(in Pieces.SegmentBar)_ | The bar's own height: the band, the ticks proud of it, and the names' line. |
| 1300 | 10 | `public void animateFirst(double from, double millis)` _(in Pieces.SegmentBar)_ | The first segment grows from `from` to its own amount over `millis` - the land office's free ground widening after a purchase - with the segments after it carried along. |
| 1311 | 40 | `protected void layoutChildren()` _(in Pieces.SegmentBar)_ |  |

### a loan as a card (lines 1353-1471)

| line | len | member | says |
|---:|---:|---|---|
| 1370 | 8 | `public static String rateColour(DebtQuote quote, DebtManager market)` | A quoted rate's verdict colour, by how far up the market's own band it sits - the rule Build's old credit page coloured its rates by, here since 0.7.26 so a card can colour its offer. |
| 1393 | 11 | `public static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone, String ending, String action,...` | A loan on offer, as a card (0.7.26): the land office's funding page sets two or three side by side, and Build's two since 0.7.40 (it stacked the same figures as a statement, INSUFFICIENT FUNDS, until then). |
| 1411 | 5 | `public static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone, String ending, Press press, S...` | ...with the action button across its foot (0.7.34): the land office's offers, whose press borrows and then buys - "Buy 5 plots · D$9.8B", and the paper under it - and since 0.7.40 Build's, whose press borrows and then... |
| 1417 | 38 | `static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone, String ending, javafx.scene.Node go,...` |  |
| 1457 | 14 | `static HBox offerLine(String label, String value, String tone)` | One figure on an offer card: what it is at the left, wrapping, and the figure at the right. |

### THE PIECES THE PEOPLE PAGE ADDED (0.7.27) (lines 1472-1558)

| line | len | member | says |
|---:|---:|---|---|
| 1489 | 3 | `public static HBox pageHead(String title, String areaColour, javafx.scene.Node...right)` | A page's head: its title, its area's swatch, and what sits at its right. |
| 1499 | 42 | `public static HBox pageHead(String title, String areaColour, Runnable back, String sub, String info, javafx.scene.Node...right)` | ...with a sub-page after "›" - the title is then a way back, `back` - and an (i) holding `info` after them when it is not null: "People › Household money (i)". |
| 1543 | 8 | `public static Label chip(String text, String colour)` | A chip: the words in a colour on a faint ground of it (Construction's, 0.7.22; here since 0.7.27). |
| 1553 | 5 | `public static String tint(String hex, double alpha)` | A colour at an opacity, for a chip's ground. |

### a ring as a card (lines 1559-1634)

| line | len | member | says |
|---:|---:|---|---|
| 1568 | 5 | `public static HBox ringCard(String figure, double arc, String tone, String title, String line, String foot, double ringSize, Ru...` | A ring as a card: the ring at the left with its figure in it, and beside it a title, a line and a foot - Build's measure card (0.7.24), on the Build card ground (RAISED, radius 8, a 1 px EDGE that turns ACCENT under t... |
| 1581 | 53 | `public static HBox ringCard(String figure, double arc, String tone, String title, String info, String line, String foot, String...` | ...with an (i) after the title holding `info`, the foot in a colour of its own (Build's "N on site" in the building pink), the PICKED form - the pinned ground and a pink edge, Build's ring whose buildings are shown un... |

### a waterfall (lines 1635-1839)

| line | len | member | says |
|---:|---:|---|---|
| 1646 | 17 | **type** `public record Step(String name, double amount, String colour, boolean total, List<Slice> parts, String icon...` | One column of a waterfall: its name; its amount, which a plain step adds to the running total (negative takes away) and a TOTAL step sets - drawn from zero, the model's own figure, so a column the model sums is never ... |
| 1649 | 3 | `public static Step of(String name, double amount, String colour)` _(in Pieces.Step)_ | A plain step. |
| 1653 | 3 | `public static Step total(String name, double amount, String colour)` _(in Pieces.Step)_ | A total, drawn from zero to the figure given. |
| 1656 | 1 | `public Step parts(List<Slice> p)` _(in Pieces.Step)_ |  |
| 1657 | 1 | `public Step icon(String svg)` _(in Pieces.Step)_ |  |
| 1658 | 1 | `public Step tip(String t)` _(in Pieces.Step)_ |  |
| 1659 | 3 | `public Step go(java.util.function.Consumer<javafx.scene.Node> g)` _(in Pieces.Step)_ |  |
| 1673 | 4 | `public static Waterfall waterfall(List<Step> steps, java.util.function.DoubleFunction<String> figure, double width, double height)` | A waterfall (0.7.27): the steps as columns, each bar standing where the running total was before it and reaching where it is after, a zero line across, a hairline from each bar to the next, the figure over each bar wr... |
| 1679 | 160 | **type** `public static final class Waterfall extends javafx.scene.layout.Pane` | The bars waterfall() draws: a Pane that lays its columns out at whatever width it is given. |
| 1694 | 72 | `Waterfall(List<Step> steps, java.util.function.DoubleFunction<String> figure, double width, double height)` _(in Pieces.Waterfall)_ |  |
| 1768 | 7 | `public void animate(double millis)` _(in Pieces.Waterfall)_ | The bars grow out of the zero line over `millis` - the People page's month landing. |
| 1776 | 62 | `protected void layoutChildren()` _(in Pieces.Waterfall)_ |  |

### a bullet bar (lines 1840-1866)

| line | len | member | says |
|---:|---:|---|---|
| 1850 | 16 | `public static VBox bulletBar(double value, double target, String colour, double width, String label)` | A figure against the line it is read against (0.7.27): a bar of `value` in a colour with a tick at `target`, on a scale of the larger of the two, and `label` under it - the People page's living here against what the c... |

### THE PIECES THE SERVICES SCREEN ADDED (0.7.28) (lines 1867-1889)

### a cause bar (lines 1890-2071)

| line | len | member | says |
|---:|---:|---|---|
| 1899 | 9 | **type** `public record Part(String name, double amount, String colour, Runnable go, boolean hatched, String tip)` | One part of a cause bar: its name, its amount in the bar's units (one at zero or below draws nothing and is keyed as "none"), its colour, what a click on it opens (null: nothing), whether it is HATCHED - what is left ... |
| 1901 | 3 | `public static Part of(String name, double amount, String colour)` _(in Pieces.Part)_ | A plain part. |
| 1904 | 1 | `public Part go(Runnable g)` _(in Pieces.Part)_ |  |
| 1905 | 1 | `public Part tip(String t)` _(in Pieces.Part)_ |  |
| 1906 | 1 | `public Part leftOver()` _(in Pieces.Part)_ |  |
| 1923 | 4 | `public static CauseBar causeBar(List<Part> parts, double width, double band, java.util.function.DoubleFunction<String> figure)` | A cause bar (0.7.28): the parts laid end to end on a scale of their sum, so the bar is the whole they make - a part wide enough named under itself with its figure, the narrower ones and the parts at nothing in a key u... |
| 1929 | 142 | **type** `public static final class CauseBar extends javafx.scene.layout.Pane` | The bar causeBar() draws: a Pane that lays its parts, their names and its key out at whatever width it is given. |
| 1938 | 52 | `CauseBar(List<Part> parts, double width, double band, java.util.function.DoubleFunction<String> figure)` _(in Pieces.CauseBar)_ |  |
| 1991 | 1 | `private static double finite(double v)` _(in Pieces.CauseBar)_ |  |
| 1994 | 11 | `private double[] widths(double w)` _(in Pieces.CauseBar)_ | Each part's drawn width at a bar this wide, 0 for a part at nothing. |
| 2007 | 1 | `private static boolean named(double drawn)` _(in Pieces.CauseBar)_ | Whether a part is named under the bar, at that width. |
| 2009 | 1 | `public javafx.geometry.Orientation getContentBias()` _(in Pieces.CauseBar)_ |  |
| 2011 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.CauseBar)_ |  |
| 2013 | 22 | `protected double computePrefHeight(double width)` _(in Pieces.CauseBar)_ |  |
| 2036 | 34 | `protected void layoutChildren()` _(in Pieces.CauseBar)_ |  |

### a supply bar (lines 2072-2102)

| line | len | member | says |
|---:|---:|---|---|
| 2084 | 18 | `public static SegmentBar supplyBar(double need, double built, double working, String colour, String needName, List<Tick> more, ...` | A supply bar (0.7.28): what is working, inside what is built, against what is needed - a segment bar of `working` in `colour` and the rest of `built` in a light step of it, on a scale of the larger of the need and wha... |

### a funnel (lines 2103-2162)

| line | len | member | says |
|---:|---:|---|---|
| 2106 | 1 | **type** `public record FunnelStep(String name, double amount, String figure, String tip)` | One step of a funnel: its name, its amount on the funnel's scale, its figure as written, and its tooltip (null: none). |
| 2117 | 45 | `public static VBox funnel(List<FunnelStep> steps, double width, int binding, String colour)` | A funnel (0.7.28): the steps a figure narrows through, a row each - its name at the left, a bar on the scale of the largest step, its figure at the right - with the step at `binding` (-1: none) outlined and its name i... |

### an effect scale (lines 2163-2260)

| line | len | member | says |
|---:|---:|---|---|
| 2176 | 31 | `public static HBox effectScale(String label, double now, double atNone, double atAll, boolean log, java.util.function.DoubleFun...` | An effect scale (0.7.28): what a coverage buys, as a line from what it is with nobody covered to what it is with everybody, today marked on it with its figure over the mark - `label` at the left in a column `labelWidt... |
| 2209 | 51 | **type** `public static final class EffectScale extends javafx.scene.layout.Pane` | The line effectScale() draws: a Pane that lays its ends and today's mark out at whatever width it is given. |
| 2216 | 27 | `EffectScale(double now, double atNone, double atAll, boolean log, java.util.function.DoubleFunction<String> figure, String colour)` _(in Pieces.EffectScale)_ |  |
| 2244 | 15 | `protected void layoutChildren()` _(in Pieces.EffectScale)_ |  |

### cohort bars (lines 2261-2357)

| line | len | member | says |
|---:|---:|---|---|
| 2274 | 6 | `public static CohortBars cohortBars(double[] counts, double width, double height, String colour, int dangerFrom, String danger,...` | Cohort bars (0.7.28): everybody part way through something, a bar each on the tallest bar's scale - the nearest end at the left, named `near` under it, the farthest at the right, `far` - each bar's figure and `slotNam... |
| 2282 | 75 | **type** `public static final class CohortBars extends javafx.scene.layout.Pane` | The bars cohortBars() draws: a Pane that lays them out at whatever width it is given. |
| 2292 | 38 | `CohortBars(double[] counts, double width, double height, String colour, int dangerFrom, String danger, String dangerName, Strin...` _(in Pieces.CohortBars)_ |  |
| 2331 | 25 | `protected void layoutChildren()` _(in Pieces.CohortBars)_ |  |

### a door, and a spark (lines 2358-2394)

| line | len | member | says |
|---:|---:|---|---|
| 2367 | 13 | `public static Label door(String text, String colour, Runnable go)` | A door (0.7.28): a few words and a "›" in a colour, underlined under the pointer - Build's "why ›" in the people teal, "Set the fee ›"; the Services cards' "Build for it ›" was one until 0.7.34, when a door to Build o... |
| 2387 | 7 | `public static Region sparkline(double[] series, List<Integer> months, String colour, double width, double height)` | A sparkline of a history series (0.7.28): the header tiles' own (UserInterface.Sparkline - the last ten years, a dot on the latest month, each January marked), at the caller's size: the Services KPI cells' 64 x 16. |

### THE PIECES THE INFRASTRUCTURE SCREEN ADDED (0.7.29) (lines 2395-2462)

| line | len | member | says |
|---:|---:|---|---|
| 2414 | 21 | `public static HBox heroCard(javafx.scene.Node left, double leftWidth, javafx.scene.Node right)` | A page's lead picture as a card (0.7.29): `left`, `leftWidth` wide, its words; `right`, taking the rest, the picture. |
| 2444 | 18 | `public static VBox details(String key, String caption, java.util.Set<String> open, Runnable redraw, java.util.function.Supplier...` | A fold (0.7.29): "details ▸ caption", and what is inside when it is open - built only then, by `body`. |

### rows on one scale (lines 2463-2857)

| line | len | member | says |
|---:|---:|---|---|
| 2472 | 9 | **type** `public record Run(double from, double amount, String colour, boolean hollow, String tip, Runnable go)` | One stretch of a row on a shared scale (0.7.29): where it starts and how long it is, in the scale's units; its colour; whether it is HOLLOW (outlined()) - outlined and faintly tinted, what is taken away or what is not... |
| 2474 | 3 | `public static Run of(double from, double amount, String colour)` _(in Pieces.Run)_ | A filled stretch. |
| 2477 | 1 | `public Run outlined()` _(in Pieces.Run)_ |  |
| 2478 | 1 | `public Run tip(String t)` _(in Pieces.Run)_ |  |
| 2479 | 1 | `public Run go(Runnable g)` _(in Pieces.Run)_ |  |
| 2494 | 34 | **type** `public record ScaleRow(String name, String figure, List<Run> runs, List<Tick> marks, String empty, String i...` | One row of scaleRows() (0.7.29): its name at the left, which wraps and is never cut; its runs; its figure at the right; and - each may be null - marks across this row alone (a good's "by lorry"), the words a row at no... |
| 2497 | 3 | `public static ScaleRow of(String name, String figure, List<Run> runs)` _(in Pieces.ScaleRow)_ |  |
| 2500 | 3 | `public static ScaleRow caption(String name, String info)` _(in Pieces.ScaleRow)_ |  |
| 2503 | 3 | `public ScaleRow marks(List<Tick> m)` _(in Pieces.ScaleRow)_ |  |
| 2506 | 3 | `public ScaleRow empty(String words)` _(in Pieces.ScaleRow)_ |  |
| 2509 | 3 | `public ScaleRow info(String i)` _(in Pieces.ScaleRow)_ |  |
| 2512 | 3 | `public ScaleRow tag(String t, String colour)` _(in Pieces.ScaleRow)_ |  |
| 2515 | 3 | `public ScaleRow icon(String svg)` _(in Pieces.ScaleRow)_ |  |
| 2518 | 3 | `public ScaleRow tip(String t)` _(in Pieces.ScaleRow)_ |  |
| 2521 | 3 | `public ScaleRow go(Runnable g)` _(in Pieces.ScaleRow)_ |  |
| 2524 | 3 | `public ScaleRow strong()` _(in Pieces.ScaleRow)_ |  |
| 2530 | 1 | **type** `public record Rule(double at, String colour, boolean dashed, String name, String tip, Runnable go)` | A rule through every row of scaleRows() (0.7.29): where, in the scale's units; its colour; dashed or solid; its name over the rows, its tooltip, and what a click on the name does (each may be null). |
| 2543 | 4 | `public static ScaleRows scaleRows(List<ScaleRow> rows, double scale, List<Rule> rules, double nameWidth, double figureWidth, do...` | Rows on one scale (0.7.29): each row's name in a column `nameWidth` wide, its bar - a track `band` tall with its runs laid on it where they start, on a scale of `scale` - and its figure in a column `figureWidth` wide;... |
| 2549 | 308 | **type** `public static final class ScaleRows extends javafx.scene.layout.Pane` | The stack scaleRows() draws: a Pane that lays its rows, their runs and its rules out at whatever width it is given. |
| 2564 | 127 | `ScaleRows(List<ScaleRow> rows, double scale, List<Rule> rules, double nameWidth, double figureWidth, double band)` _(in Pieces.ScaleRows)_ |  |
| 2699 | 34 | `private double[][] ruleNameAt(double w)` _(in Pieces.ScaleRows)_ | Where each rule's name goes at a width, as {left, line} per rule, and how many lines of names that takes (the last entry's line + 1, 0 with none named). |
| 2735 | 3 | `private double ruleRoom(double w)` _(in Pieces.ScaleRows)_ | The room the rules' names take over the rows, at a width. |
| 2740 | 7 | `private double nameRoom(int i, double w)` _(in Pieces.ScaleRows)_ | The room a row's name has, after its icon and its (i). |
| 2749 | 5 | `private double rowHeight(int i, double w)` _(in Pieces.ScaleRows)_ | A row's height: its name's lines, and never less than ROW (a caption a little less). |
| 2756 | 1 | `public javafx.geometry.Orientation getContentBias()` _(in Pieces.ScaleRows)_ | Its height follows its width - the rules' names take a second line when two would touch - so a parent asks at the width it will give. |
| 2758 | 6 | `protected double computePrefHeight(double width)` _(in Pieces.ScaleRows)_ |  |
| 2765 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.ScaleRows)_ |  |
| 2767 | 89 | `protected void layoutChildren()` _(in Pieces.ScaleRows)_ |  |

### THE PIECES THE SECTORS SCREEN ADDED (0.7.30) (lines 2858-2979)

| line | len | member | says |
|---:|---:|---|---|
| 2878 | 16 | `public static HBox pips(int n, int of, String colour, double size)` | Pips: `of` dots in a row, the first `n` filled in `colour` and the rest an outline - "4 of 6". |
| 2905 | 68 | `public static javafx.scene.layout.GridPane factGrid(List<ham.citybuildersim.Sector.Line> lines, int columns, java.util.function...` | A fact grid: a sector's lines as figures. |
| 2975 | 4 | `static String firstWords(String text)` | A note's first sentence, for a note shown on its own line with the whole of it behind the (i). |

### THE PIECES THE GOVERNMENT SCREEN ADDED (0.7.31) (lines 2980-3163)

| line | len | member | says |
|---:|---:|---|---|
| 3012 | 20 | `public static List<Slice> topSlices(List<String> names, List<Double> amounts, String[] ramp)` | Slices, biggest first, with everything past the ramp's last folded into one grey "Everything else" (RAMP_REST). |
| 3049 | 52 | `public static javafx.scene.layout.StackPane splitRing(List<Slice> slices, String top, String figure, String tone, double size, ...` | A ring split into its slices, with a caption and a figure in the hole - the Government's two (GovernmentScreen's own donut until 0.7.31). |
| 3116 | 47 | `public static VBox ringKey(List<Slice> slices, double width, Consumer<Slice> pick)` | The key beside a split ring: a row a slice - its swatch, its name (wrapping, never cut), its share of the arcs drawn and its money - each a click that hands its slice to `pick` (null: none). |

### ranked bars (lines 3164-3317)

| line | len | member | says |
|---:|---:|---|---|
| 3176 | 3 | **type** `public record RankRow(String key, String icon, String name, double amount, String colour, List<String> figu...` | One line of rankBars() (0.7.31): `key`, the name its open state is kept under; its icon (null: none) and its name, which wraps and is never cut; its amount, which the bar draws - leftward from the axis when it is belo... |
| 3198 | 85 | `public static VBox rankBars(List<RankRow> rows, double scale, double[] columns, double doorWidth, java.util.Set<String> open, R...` | Ranked bars (0.7.31): a row a line - its icon in a square tinted in its colour, its name and, when it opens, "▸ who"; its bar on one scale for every row, `scale` of the bar's units across (widened to the largest line ... |
| 3285 | 32 | **type** `static final class RankBar extends javafx.scene.layout.Pane` | The bar rankBars() draws for one row: a Pane that lays its track, its fill and its axis out at whatever width it is given. |
| 3289 | 14 | `RankBar(double amount, double pos, double neg, String colour)` _(in Pieces.RankBar)_ |  |
| 3304 | 12 | `protected void layoutChildren()` _(in Pieces.RankBar)_ |  |

### a bridge (lines 3318-3509)

| line | len | member | says |
|---:|---:|---|---|
| 3325 | 2 | **type** `public record Anchor(String caption, String icon, String figure, String line, List<javafx.scene.Node> chips...` | One figure a bridge walks from or to (0.7.31): its caption and its icon, the figure in large type, one line under it, any chips, and what a click on it does (null: nothing). |
| 3335 | 8 | **type** `public record BridgeStep(String label, double amount, String icon, String tip, Runnable go, boolean atNothing)` | One step between two anchors (0.7.31): its words, its amount as it moves the figure (+ adds, - takes away), its icon (null: none), its tooltip (null: none), what a click on it opens (null: nothing), and whether it is ... |
| 3336 | 3 | `public static BridgeStep of(String label, double amount, String icon)` _(in Pieces.BridgeStep)_ |  |
| 3339 | 1 | `public BridgeStep tip(String t)` _(in Pieces.BridgeStep)_ |  |
| 3340 | 1 | `public BridgeStep go(Runnable g)` _(in Pieces.BridgeStep)_ |  |
| 3341 | 1 | `public BridgeStep always()` _(in Pieces.BridgeStep)_ |  |
| 3362 | 21 | `public static HBox bridge(List<Anchor> anchors, List<List<BridgeStep>> steps, java.util.function.DoubleFunction<String> figure,...` | A bridge (0.7.31): the anchors as tiles with a column of steps between each pair and a muted "›" between each - steps.get(i) between anchor i and anchor i + 1. |
| 3385 | 6 | `static Label bridgeChevron()` | The muted "›" between a tile and a column. |
| 3393 | 37 | `static VBox bridgeTile(Anchor a)` | One anchor as a tile: caption and icon, the figure, its line, its chips. |
| 3432 | 35 | `static VBox bridgeColumn(List<BridgeStep> col, double scale, java.util.function.DoubleFunction<String> figure, String in, Strin...` | One column of a bridge's steps. |
| 3469 | 40 | `static HBox bridgeRow(BridgeStep s, double scale, java.util.function.DoubleFunction<String> figure, String in, String out)` | One step: sign, icon, words, bar, figure. |

### THE PIECES THE FINANCES SCREEN ADDED (0.7.32) (lines 3510-3783)

| line | len | member | says |
|---:|---:|---|---|
| 3539 | 9 | **type** `public record Column(String label, String sub, String figure, List<Segment> parts, boolean ghost, String ta...` | One column of columns() (0.7.32): its label and a second line under it (either may be null); its figure, written over its top (null: none - the caller says only what is worth saying); its segments, stacked from the fo... |
| 3543 | 4 | `public Column(String label, String sub, String figure, List<Segment> parts, boolean ghost, String tag, String tagColour, String...` _(in Pieces.Column)_ | ...whole, as every column was until 0.7.34. |
| 3560 | 3 | `public static Columns columns(List<Column> cols, double scale, double width, double height)` | Columns (0.7.32): each column's segments stacked from the foot on one scale - `scale` of its units to the plot's height, the largest column's total when it is 0 or less - the columns side by side across `width` (0 or ... |
| 3565 | 174 | **type** `public static final class Columns extends javafx.scene.layout.Pane` | The chart columns() draws: a Pane that lays its columns out at whatever width it is given. |
| 3578 | 97 | `Columns(List<Column> cols, double scale, double width, double height)` _(in Pieces.Columns)_ |  |
| 3676 | 62 | `protected void layoutChildren()` _(in Pieces.Columns)_ |  |
| 3747 | 36 | `public static VBox setting(String title, String info, String[] names, String current, String[] lines, String[] tips, Consumer<S...` | A setting (0.7.32): its title in capitals with an (i) holding `info` (either may be null), its choices as chips with `tips` as their tooltips (null: none), and under them the chosen one's line from `lines` - what the ... |

### THE PIECES THE BANK SCREEN ADDED (0.7.33) (lines 3784-3833)

| line | len | member | says |
|---:|---:|---|---|
| 3814 | 19 | `public static SegmentBar bandBar(double value, double min, double target, double top, String tone, double width, double band, b...` | A ratio in its band (0.7.33): a bar of `value` in `tone` on a scale of BAND_SCALE times the top, pinned at the end past it, with ticks at the minimum and the top in the muted grey and at the target in the heading ink ... |

### a ladder of rates (lines 3834-3954)

| line | len | member | says |
|---:|---:|---|---|
| 3848 | 18 | **type** `public record Rung(String svg, String colour, String name, String nameTone, List<Segment> parts, List<Tick>...` | One rung of rateLadder() (0.7.33): its icon and the icon's colour (null: none); its name, which wraps and is never cut, and the name's ink (null: the body's; a rung that is shut out reads red); its rate's parts as seg... |
| 3851 | 3 | `public static Rung of(String svg, String colour, String name, List<Segment> parts, double rate, String step)` _(in Pieces.Rung)_ |  |
| 3854 | 4 | `public static Rung caption(String heading)` _(in Pieces.Rung)_ |  |
| 3858 | 1 | `public Rung ticks(List<Tick> t)` _(in Pieces.Rung)_ |  |
| 3859 | 1 | `public Rung info(String i)` _(in Pieces.Rung)_ |  |
| 3860 | 1 | `public Rung go(Runnable g)` _(in Pieces.Rung)_ |  |
| 3861 | 1 | `public Rung tone(String c)` _(in Pieces.Rung)_ |  |
| 3862 | 1 | `public Rung tag(String t, String c)` _(in Pieces.Rung)_ |  |
| 3864 | 1 | `public Rung under()` _(in Pieces.Rung)_ | ...set under the rung before it. |
| 3880 | 74 | `public static VBox rateLadder(List<Rung> rungs, double scale, double nameWidth, double rateWidth, double stepWidth)` | A ladder of rates drawn in their parts (0.7.33): a row a rung - its icon, its name (in the accent with "›" when it is a door: a click opens where the rate is decided; in its own tone when it has one, a door or not), i... |

### two bars on one scale (lines 3955-3986)

| line | len | member | says |
|---:|---:|---|---|
| 3964 | 22 | `public static VBox balanceBars(String topWords, List<Segment> top, List<Tick> topTicks, String underWords, List<Segment> under,...` | Two bars on one scale (0.7.33): `top` and `under`, each a segment bar on `scale` with its own ticks, and each with its words in a column `labelWidth` wide at its left, which wrap and are never cut - the Bank's balance... |

### a status banner (lines 3987-4012)

| line | len | member | says |
|---:|---:|---|---|
| 3996 | 16 | `public static HBox statusBanner(String word, String tone, String sentence, String svg)` | A status banner (0.7.33): a state's icon in a square tinted in its colour, its word as a chip, and the model's own sentence for it, whole and wrapping - on the raised ground with a 3 px edge at the left in the same co... |

### THE BUTTON THAT ASKS TO BE PRESSED (0.7.34) (lines 4013-4225)

| line | len | member | says |
|---:|---:|---|---|
| 4048 | 1 | **type** `public enum Look` | How an action button looks: GO filled, ready; CHOOSE outlined, nothing chosen yet; CREDIT outlined, it goes ahead on a loan; HELD neutral, it cannot go ahead as it stands. |
| 4051 | 4 | **type** `public record Press(Look look, String main, String sub)` | What an action button says: its look, its words, and a smaller line under them (null: none). |
| 4053 | 1 | `public String words()` _(in Pieces.Press)_ | Both lines as one, for a probe and the reader's accessible text. |
| 4066 | 3 | `public static ActionButton actionButton(String svg, String accent, double tall, Press press, Runnable go)` | An action button: a Press in its look, and what a press does. |
| 4076 | 105 | **type** `public static final class ActionButton extends javafx.scene.layout.StackPane` | The action button (0.7.34): a region the height of `tall` or what its words need, as wide as its parent gives it, its words wrapping inside it; show() changes what it says in place - a card's stepper reprices it witho... |
| 4084 | 29 | `ActionButton(String svg, String accent, double tall, Press press, Runnable go)` _(in Pieces.ActionButton)_ |  |
| 4115 | 10 | `public ActionButton show(Press p)` _(in Pieces.ActionButton)_ | Says a new Press, restyled in place. |
| 4127 | 1 | `public Press press()` _(in Pieces.ActionButton)_ | What it says now. |
| 4130 | 1 | `public void onPress(Runnable go)` _(in Pieces.ActionButton)_ | What a press does from now on. |
| 4132 | 36 | `private void dress()` _(in Pieces.ActionButton)_ |  |
| 4169 | 3 | `protected double computeMinHeight(double width)` _(in Pieces.ActionButton)_ |  |
| 4173 | 3 | `protected double computePrefHeight(double width)` _(in Pieces.ActionButton)_ |  |
| 4177 | 3 | `protected double computeMaxHeight(double width)` _(in Pieces.ActionButton)_ |  |
| 4183 | 5 | `static String mix(String a, String b, double t)` | Two colours mixed, `t` of the way from `a` to `b`, as "#rrggbb". |
| 4197 | 28 | `public static HBox doorPill(String text, String svg, String accent, Runnable go)` | A door as a pill (0.7.34): a way to Build or the land office - "Build for it", "Build · Roads & transit", "Land office" - outlined in the area's colour, its icon, its words whole and an arrow; tinted a little deeper u... |

### THE PIECES THE TRADE SCREEN ADDED (0.7.35) (lines 4226-4319)

| line | len | member | says |
|---:|---:|---|---|
| 4259 | 60 | `public static VBox bandMeter(String label, String reading, double at, double[] edges, String[] tones, String note, boolean muted)` | A meter with named bands and the city's mark on it (TradeScreen's THE THREE QUIET GAUGES until 0.7.35, moved unchanged; the gauge cards draw the same bands with bandTrack()). |

### a band track (lines 4320-4421)

| line | len | member | says |
|---:|---:|---|---|
| 4330 | 4 | `public static BandTrack bandTrack(double at, double[] edges, String[] tones, List<Tick> ticks, double width, double band, boole...` | A meter's bands at any width (0.7.35): the bands laid end to end in their tones, faint, each `edges[i]` the right-hand end of band i in a 0-to-1 space; the city's mark at `at` (0 to 1), standing proud of the band, or ... |
| 4336 | 85 | **type** `public static final class BandTrack extends javafx.scene.layout.Pane` | The track bandTrack() draws: a Pane that lays its bands, ticks and mark out at whatever width it is given. |
| 4345 | 40 | `BandTrack(double at, double[] edges, String[] tones, List<Tick> ticks, double width, double band, boolean muted)` _(in Pieces.BandTrack)_ |  |
| 4386 | 4 | `private boolean named()` _(in Pieces.BandTrack)_ |  |
| 4392 | 1 | `private double tall()` _(in Pieces.BandTrack)_ | The band, the mark standing 4 px proud of it each way, and a line of names under it when a tick has one. |
| 4394 | 26 | `protected void layoutChildren()` _(in Pieces.BandTrack)_ |  |

### a gauge card (lines 4422-4468)

| line | len | member | says |
|---:|---:|---|---|
| 4432 | 36 | `public static VBox gaugeCard(String svg, String colour, String title, String info, String reading, String readingTone, String c...` | A gauge as a card (0.7.35): its icon in a tinted square, its title and an (i) holding `info`; the reading in mono 22 beside its verdict as a chip in `chipTone` (no chip when `chip` is null); the bands with the city's ... |

### mirrored bars (lines 4469-4696)

| line | len | member | says |
|---:|---:|---|---|
| 4479 | 2 | **type** `public record Mirror(String svg, String name, double left, double right, String leftFigure, String rightFig...` | One row of mirrorRows() (0.7.35): its icon (null: none) and its name, which wraps and is never cut; what goes left of the axis and what goes right, in the rows' one unit, each with its figure as written (null: the sid... |
| 4492 | 4 | `public static MirrorRows mirrorRows(List<Mirror> rows, double scale, String leftColour, String rightColour, String leftHead, St...` | Mirrored bars (0.7.35): a row each, its icon and name at the left, then a lane either side of a centre axis - `left` growing leftward from it in `leftColour`, `right` rightward in `rightColour`, both on `scale` (0 or ... |
| 4498 | 198 | **type** `public static final class MirrorRows extends javafx.scene.layout.Pane` | The rows mirrorRows() draws: a Pane that lays them out at whatever width it is given. |
| 4514 | 59 | `MirrorRows(List<Mirror> rows, double scale, String leftColour, String rightColour, String leftHead, String rightHead, double na...` _(in Pieces.MirrorRows)_ |  |
| 4574 | 1 | `private static double finite(double v)` _(in Pieces.MirrorRows)_ |  |
| 4577 | 1 | `public Region row(int i)` _(in Pieces.MirrorRows)_ | Row i's ground - the node its click is handed, to anchor a popover - or null past the rows. |
| 4579 | 15 | `private Label head(String text, String colour)` _(in Pieces.MirrorRows)_ |  |
| 4595 | 6 | `private Region bar(String colour)` _(in Pieces.MirrorRows)_ |  |
| 4602 | 8 | `private Label figure(String text)` _(in Pieces.MirrorRows)_ |  |
| 4612 | 7 | `public void grow(double millis)` _(in Pieces.MirrorRows)_ | The bars grow out of the axis over `millis` - a month landing on the page. |
| 4620 | 1 | `private double headRoom()` _(in Pieces.MirrorRows)_ |  |
| 4623 | 1 | `private double nameRoom()` _(in Pieces.MirrorRows)_ | The name column's own width: what is left of it after the icon. |
| 4625 | 3 | `private double rowHeight(int i)` _(in Pieces.MirrorRows)_ |  |
| 4629 | 1 | `public javafx.geometry.Orientation getContentBias()` _(in Pieces.MirrorRows)_ |  |
| 4631 | 5 | `protected double computePrefHeight(double width)` _(in Pieces.MirrorRows)_ |  |
| 4637 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.MirrorRows)_ |  |
| 4639 | 56 | `protected void layoutChildren()` _(in Pieces.MirrorRows)_ |  |

### diverging bars (lines 4697-4852)

| line | len | member | says |
|---:|---:|---|---|
| 4707 | 1 | **type** `public record Force(String name, double value, boolean total, boolean ghost, String info, String line)` | One row of divergingBars() (0.7.35): its name; its value, signed - below nothing drawn left of the centre line, above it right; a TOTAL row is the model's own sum, drawn in the stronger colour; a GHOST row is outlined... |
| 4720 | 5 | `public static DivergingBars divergingBars(List<Force> rows, double scale, double nameWidth, double figureWidth, String colour, ...` | Diverging bars (0.7.35): a row each - its name with its (i) and line in a column `nameWidth` wide, a lane split by a centre line, the bar from the line to the value on a scale of plus or minus `scale` (one past it sto... |
| 4727 | 125 | **type** `public static final class DivergingBars extends javafx.scene.layout.Pane` | The rows divergingBars() draws: a Pane that lays them out at whatever width it is given. |
| 4739 | 55 | `DivergingBars(List<Force> rows, double scale, double nameWidth, double figureWidth, String colour, String totalColour, String l...` _(in Pieces.DivergingBars)_ |  |
| 4795 | 8 | `private Label words(String text)` _(in Pieces.DivergingBars)_ |  |
| 4804 | 1 | `private double headRoom()` _(in Pieces.DivergingBars)_ |  |
| 4806 | 1 | `private double rowHeight(int i)` _(in Pieces.DivergingBars)_ |  |
| 4808 | 5 | `protected double computePrefHeight(double width)` _(in Pieces.DivergingBars)_ |  |
| 4814 | 1 | `protected double computeMinHeight(double width)` _(in Pieces.DivergingBars)_ |  |
| 4816 | 35 | `protected void layoutChildren()` _(in Pieces.DivergingBars)_ |  |

### THE PIECES THE POLICY SCREEN ADDED (0.7.36) (lines 4853-5027)

| line | len | member | says |
|---:|---:|---|---|
| 4876 | 26 | **type** `public record Effect(String label, double before, double after, java.util.function.DoubleFunction<String> f...` | One effect of a move (0.7.36): its words; the figure before and after, written by `fmt`; the move written by `delta` (null: no chip); the bars' colour; the move's VERDICT colour when the after crosses a line the model... |
| 4880 | 3 | `public static Effect of(String label, double before, double after, java.util.function.DoubleFunction<String> fmt)` _(in Pieces.Effect)_ | An effect in money's blue with no chip, no verdict and its own scale. |
| 4883 | 3 | `public Effect delta(java.util.function.DoubleFunction<String> d)` _(in Pieces.Effect)_ |  |
| 4886 | 1 | `public Effect colour(String c)` _(in Pieces.Effect)_ |  |
| 4887 | 1 | `public Effect verdict(String v)` _(in Pieces.Effect)_ |  |
| 4888 | 1 | `public Effect scale(double s)` _(in Pieces.Effect)_ |  |
| 4889 | 1 | `public Effect info(String i)` _(in Pieces.Effect)_ |  |
| 4891 | 4 | `public boolean moved()` _(in Pieces.Effect)_ | Whether the move shows: the two figures are written differently, or the move's chip says something (a $52k move on a $5.1M budget). |
| 4896 | 5 | `public String words()` _(in Pieces.Effect)_ | The row in words, for a probe: "Profit tax $23.3M -> $24.8M (+$1.5M)". |
| 4911 | 50 | `public static VBox beforeAfter(Effect e, double width)` | An effect as a row (0.7.36): its words at the left, wrapping and never cut, with its (i); at the right the figure before and, once something has moved it, an arrow and the after in the accent and the move as a chip; u... |
| 4963 | 21 | `public static HBox stagedChip(String text, Runnable remove)` | A staged change as a chip in the tray (0.7.36): its words in the accent, and a × that takes it back out of the set. |
| 4993 | 34 | `public static HBox stagedTray(List<? extends javafx.scene.Node> chips, Effect budget, javafx.scene.Node apply, javafx.scene.Nod...` | The staged tray (0.7.36): what a page has staged, as chips that each come out with their ×, THE BUDGET before and after the set (`budget`, null: none), and the page's Apply and Discard - on the raised ground with an a... |

### THE PIECES CITY HISTORY ADDED (0.7.37) (lines 5028-5159)

| line | len | member | says |
|---:|---:|---|---|
| 5043 | 11 | **type** `public static final class CardHead extends HBox` | A chart card's head (0.7.37): its parts, so a month landing can count the figure up and a click can open the line. |
| 5047 | 6 | `CardHead(Label name, Label figure, Label change)` _(in Pieces.CardHead)_ |  |
| 5062 | 28 | `public static CardHead chartCardHead(String name, String figure, String change, Runnable open, javafx.scene.Node...right)` | A chart card's head (0.7.37): `name` in the label grey, `figure` in Plex Mono 17, `change` beside it in the body's ink - never a verdict's colour: a line rising is not good news by rising - then a gap and whatever `ri... |
| 5100 | 3 | `public static RangeBar rangeBar(double lo, double hi, double first, double last, String colour, double width)` | A range bar (0.7.37): where a line ended in its own range over a window. |
| 5105 | 54 | **type** `public static final class RangeBar extends javafx.scene.layout.Pane` | The bar rangeBar() draws: a Pane that lays its track, its tick and its dot out at whatever width it is given. |
| 5113 | 19 | `RangeBar(double lo, double hi, double first, double last, String colour, double width)` _(in Pieces.RangeBar)_ |  |
| 5134 | 1 | `public boolean flat()` _(in Pieces.RangeBar)_ | Whether the line never moved over the window: one dot, no tick. |
| 5137 | 4 | `public double share(double v)` _(in Pieces.RangeBar)_ | Where a value sits on the track, 0 at the low and 1 at the high; a flat line's one value in the middle. |
| 5143 | 8 | `public void slideFrom(double was, double millis)` _(in Pieces.RangeBar)_ | The dot slides to where it ends from `was` (a share of the track) over `millis` - a month landing on the page. |
| 5152 | 6 | `protected void layoutChildren()` _(in Pieces.RangeBar)_ |  |

### THE PIECES THE FUND ADDED (0.7.39) (lines 5160-5240)

| line | len | member | says |
|---:|---:|---|---|
| 5182 | 18 | `public static javafx.scene.control.TextField searchBox(String text, String prompt, double width, Consumer<String> typed, Runnab...` | A search box (0.7.39): `text` in it, `prompt` when empty, `width` wide; `typed` on every key, `entered` on Enter. |
| 5202 | 1 | **type** `public record Pnl(String text, String tone)` | A gain or a loss in words and its colour: "▲ +D$149.6M (+420.2%)" green, "▼ −D$5,141 (−87.0%)" red, "▲ +D$368 (+0.2%)" and "D$0" in the body's ink. |
| 5210 | 15 | `public static Pnl pnl(String sym, double amount, double share)` | P&L (0.7.39, the spec's D7): `amount` in the city's money with its mark `sym`, signed, with an arrow, and `share` of the cost after it as a percentage (NaN: none). |
| 5227 | 4 | `public static String tidyMoney(String shown)` | Money.money()'s words at a unit's edge in the next unit's: "$1000k" is "$1.0M", "$1000.0M" "$1.0B", "$1000.0B" "$1.0T" (0.7.39) - none of which Money writes since 0.7.40 (tightMoney()), so it changes nothing now. |
| 5233 | 7 | `public static Label pnlLabel(String sym, double amount, double share, double size)` | ...as a figure that is never cut, at a size. |


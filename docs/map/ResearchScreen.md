# ResearchScreen.java - 736 lines · 43 methods · 19 constants · interface

`ham/citybuildersim/ui/ResearchScreen.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> Policy › Research (R5, slice B1): the sixth Policy area, drawn in Policy's own style - at this slice its one page, Found, what the city has
> discovered and what reached it from abroad.
> 
> WHY. Research-design 5.5 and 6.2 and mockup 3: "no tree to click". A player who funds research (R3b's dial cards) and a player who never does both
> need to see what the city knows and how it came to know it, on one screen: every discovery on a timeline by field (a hollow dot from abroad on
> history's date, a filled one found here with a line to where the world got it and how far ahead the city was), then the recipes and the equipment
> now at work in its buildings, and the researchers, the money and the equipment's spread over time. Every figure is ResearchView's, ResearchCatalog's
> or HistorySave's: nothing here adds up a thing the model has not already. In a city founded the default way (every discovery known from the start,
> R3 call 2) there is nothing to show, and the page says so in one quiet card.
> 
> DRAWN KEPT (KP6's way): PolicyScreen's pool serves the page, each section is a scope, each row a scope keyed by what it shows (a code, a template and
> its piece, a recipe), so a discovery that arrives hands nobody's node to a neighbour. The timeline is a pane that PLACES its children when it is laid
> out and not when it is drawn (Timeline): where a dot stands, how many dots stack and how tall a row is all depend on the width the pane is given, which a
> draw does not know, and a page drawn kept and the same page built new must agree to the pixel at the width they are laid out at.
> 
> FOR R3B: the Overview and Funding pages go in PAGES before FOUND (and PAGE_ICONS beside them), and page() switches on policy.policyPage; the hub's sixth
> card, with the Overview it opens, is R3b's - this slice adds no card to the hub.

**Uses:** [Palette](Palette.md) (53), [PolicyScreen](PolicyScreen.md) (37), [Kept](Kept.md) (34), [ResearchView](ResearchView.md) (18), [HistorySave](HistorySave.md) (12), [TimeChart](TimeChart.md) (9), [CityCalendar](CityCalendar.md) (7), [Icons](Icons.md) (3), [Pieces](Pieces.md) (3), [UserInterface](UserInterface.md) (2), [EquipmentFit](EquipmentFit.md) (2), [HistoryScreen](HistoryScreen.md) (2), [BankScreen](BankScreen.md) (2), [Game](Game.md) (1), [ResearchCatalog](ResearchCatalog.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Sector](Sector.md) (1), [ChartModel](ChartModel.md) (1)

**Used by (2):** [KeptBench](KeptBench.md), [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 70 | THE PAGES |
| 93 | THE FOUND PAGE (mockup 3) |
| 199 | · the words |
| 266 | · the rows |
| 344 | THE TIMELINE |
| 652 | OVER TIME: four small charts, made once and handed the month's data |

## Enum constants

| line | constant | says |
|---:|---|---|
| 416 | `ResearchScreen.Kind.LABEL` |  |
| 416 | `ResearchScreen.Kind.DOT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `ResearchScreen.FOUND` | `"Found"` | The area's page: what has been discovered. |
| 78 | `ResearchScreen.PAGES` | `{ FOUND }` | ...the area's pages, in their tabs' order (a strip of one tab draws no tabs). |
| 81 | `ResearchScreen.PAGE_ICONS` | `{ Icons.REPORTS }` | ...and their icons. |
| 84 | `ResearchScreen.AREA_INFO` | `"What the city's researchers have found, and what reached the city from abroa...` | The area's (i). |
| 97 | `ResearchScreen.TIMELINE_INFO` | `"Every discovery the city has come to know since its founding, a row a field....` |  |
| 101 | `ResearchScreen.FOUND_INFO` | `"What the city's own researchers found before the world did, the newest first...` |  |
| 103 | `ResearchScreen.RECIPES_INFO` | `"The ways of running a building, other than its usual one, that the city know...` |  |
| 106 | `ResearchScreen.EQUIPMENT_INFO` | `"The pieces of equipment the city knows, on the buildings they suit: how many...` |  |
| 109 | `ResearchScreen.OVER_TIME_INFO` | `"Four charts of the city's research since its founding: the researchers at wo...` |  |
| 278 | `ResearchScreen.ROW_STYLE` | `"-fx-padding: 5 6 5 6; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border...` | A row's ground: a hairline under it. |
| 281 | `ResearchScreen.DOOR_STYLE` | `ROW_STYLE + " -fx-background-radius: 6; -fx-cursor: hand;"` | ...and a row that is a door's: the pointer's hand and, under it, a ground. |
| 282 | `ResearchScreen.DOOR_LIT` | `DOOR_STYLE + " -fx-background-color: " + Palette.CONTROL + ";"` |  |
| 349 | `ResearchScreen.DOT` | `12, DOT_GAP = 3` | A dot's width, and the gap two dots of a row keep from each other before one stacks above the other. |
| 352 | `ResearchScreen.LEVEL` | `26` | How far a stacked dot stands above the one under it: the dot, the figure that sits over it and a margin. |
| 355 | `ResearchScreen.LABEL_W` | `150, PAD_R = 16, TICK_ROOM = 60` | The room a row's name takes at the left, the room kept at the right, and the least apart two year ticks stand. |
| 358 | `ResearchScreen.ROW_TOP` | `18, ROW_FOOT = 8, ROW_MIN = 46` | A row's room over its top dot (the figure "+1.5y"), under its bottom one, and its least height (its two lines of words). |
| 361 | `ResearchScreen.AXIS_H` | `20` | The room under the last row for the years' names. |
| 426 | `ResearchScreen.PLACE` | `"research.place"` | The key a timeline child carries its Place under. |
| 657 | `ResearchScreen.CHART_W` | `575, CHART_H = 150` | A small chart's size: the Bank's two-across cards'. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private final PolicyScreen policy` |  |
| 63 | `private final UserInterface ui` |  |
| 486 | `private ObservableList<Node> stage` |  |
| 487 | `private final Canvas canvas` |  |
| 488 | `private int first` |  |
| 532 | `double x0, x1, rowsEnd, total` |  |
| 533 | `double[] top, height` |  |
| 534 | `final Map<String, Integer> level` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 677 | **type** `final class ResearchScreen` | Policy › Research (R5, slice B1): the sixth Policy area, drawn in Policy's own style - at this slice its one page, Found, what the city has discovered and what reached it from abroad. |
| 65 | 4 | `ResearchScreen(PolicyScreen policy, UserInterface ui)` |  |

### THE PAGES (lines 70-92)

| line | len | member | says |
|---:|---:|---|---|
| 89 | 3 | `void page(VBox page)` | The page open, drawn into `page` (R3b: its Overview and Funding pages are switched here, before Found). |

### THE FOUND PAGE (mockup 3) (lines 93-198)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 62 | `void foundPage(VBox page)` |  |
| 176 | 9 | `VBox defaultCard()` | What a city founded the default way says: it knew every discovery from the start, so nothing arrives and nothing is found. |
| 187 | 11 | `VBox leadCard(int here, int abroad)` | The lead: how many the city found itself and how many came from abroad, and how many it knew at the founding. |

### the words (lines 199-265)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 4 | `static String fieldName(Field f)` | A field as it is read: "Engineering". |
| 208 | 8 | `static String fieldColour(Field f)` | A field's colour: engineering the money blue, medicine the people teal, business the business violet, law the sand (the mockup's). |
| 218 | 5 | `static String whenWords(Discovery d)` | How the city came to know a discovery: "found here in June 1973, 4.6 years before the world" or "from abroad, June 1955". |
| 225 | 3 | `static String fromWords(Discovery d)` | ...and "known since the founding" for what no discovery made known. |
| 230 | 6 | `static String dotWords(Discovery d)` | A dot's tooltip: the name, its field and how the city came to know it, what it touches and what it does. |
| 238 | 3 | `static String aheadWords(Discovery d)` | "+1.5y": how many years ahead of the world a find was, one decimal. |
| 243 | 3 | `static String ofWords(int n, int of, String what)` | "12 of 20 buildings". |
| 248 | 8 | `static String verdictWords(EquipmentFit.Kind kind)` | The sector's verdict on a piece, in words. |
| 258 | 7 | `static String verdictColour(EquipmentFit.Kind kind)` | ...and its colour: a verdict says what to do, never how big. |

### the rows (lines 266-343)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 7 | `private static VBox cell(double width, Node...lines)` | A fixed-width cell of a row: its lines one under another. |
| 285 | 12 | `HBox foundRow(Discovery d)` | One find of the city's own: its name, its field, its month and how far ahead of the world. |
| 299 | 13 | `HBox recipeRow(ResearchView.RecipeUse u)` | One way of running a building: its recipe and building, how many of the stack run it as a share, and where it came from. |
| 314 | 29 | `HBox pieceRow(ResearchView.PieceRow r)` | One piece on one stack: its name and building, how many of the buildings have it fitted as a share, the sector's verdict, and a door to the sector. |

### THE TIMELINE (lines 344-651)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 5 | `static double xOf(int month, int first, int last, double plotWidth)` | Where a month stands across a plot this wide, from its first month to its last: 0 at the first, the width at the last, a month outside them held at the nearer end. |
| 376 | 16 | `static int[] levels(int[] months, int first, int last, double plotWidth, double slot)` | How high each dot of a row stands (pure): 0 on the row's floor, 1 above it, and so on. |
| 394 | 20 | `static int[] yearTicks(int first, int last, double plotWidth)` | The months of the year ticks (pure): a January in every `step`th year, the smallest step that keeps two ticks TICK_ROOM apart, inside the axis. |
| 416 | 1 | **type** `private enum Kind` | What a timeline child is: a field's name at the left, or a dot. |
| 423 | 1 | **type** `private record Place(Kind kind, int row, int month, int to, String code, boolean here, String colour, Strin...` | Where a timeline child belongs, as the draw says it and layout places it: its row (a field's number); for a dot its month, the month its line ends at (the world's, held at the city's month), its code, whether it was f... |
| 428 | 3 | `private static Place placeOf(Node n)` |  |
| 433 | 12 | `VBox timelineCard(List<Discovery> timeline)` | The card with the timeline in it. |
| 447 | 11 | `VBox rowLabel(Field f)` | A field's name in its colour, and its school's words under it. |
| 460 | 16 | `Region dot(Discovery d, int row, int now)` | A discovery as a dot: hollow from abroad, filled found here; its words the tooltip. |
| 484 | 167 | **type** `static final class Timeline extends Pane implements Kept.Staging` | The timeline's pane: a row a field, its name at the left, a dot a discovery, the year ticks along the foot and each found-here dot's line to the world's date. |
| 490 | 5 | `Timeline()` _(in ResearchScreen.Timeline)_ |  |
| 497 | 8 | `List<Node> dots()` _(in ResearchScreen.Timeline)_ | The dots among its children, in the order they were drawn (the harness measures where layout put them). |
| 507 | 5 | `void set(int first, int last, int rows)` _(in ResearchScreen.Timeline)_ | The months the axis runs between and how many rows it has (set by every draw). |
| 513 | 3 | `public ObservableList<Node> getChildren()` _(in ResearchScreen.Timeline)_ |  |
| 517 | 4 | `public void stage()` _(in ResearchScreen.Timeline)_ |  |
| 522 | 7 | `public void commit()` _(in ResearchScreen.Timeline)_ |  |
| 531 | 5 | **type** `private static final class Geometry` _(in ResearchScreen.Timeline)_ | Where everything stands at a width: the rows' tops and heights, each dot's level, and the plot's left and right. |
| 537 | 32 | `private Geometry geometry(double width)` _(in ResearchScreen.Timeline)_ |  |
| 571 | 3 | `private static double centreY(Geometry g, Place p)` _(in ResearchScreen.Timeline)_ | The height of a dot's centre: its level up from the foot of its row. |
| 575 | 3 | `public Orientation getContentBias()` _(in ResearchScreen.Timeline)_ |  |
| 579 | 3 | `protected double computePrefWidth(double height)` _(in ResearchScreen.Timeline)_ |  |
| 583 | 4 | `protected double computePrefHeight(double width)` _(in ResearchScreen.Timeline)_ |  |
| 588 | 22 | `protected void layoutChildren()` _(in ResearchScreen.Timeline)_ |  |
| 612 | 38 | `private void paint(Geometry g, double width)` _(in ResearchScreen.Timeline)_ | The canvas: a rule under each row, the year ticks and their names, and the found-here dots' lines, their figures and the ends of those that run past now. |

### OVER TIME: four small charts, made once and handed the month's data (lines 652-736)

| line | len | member | says |
|---:|---:|---|---|
| 660 | 7 | `static List<String> seriesNames()` | The names of the history's research series: a field's researchers, the month's bill, the share of the equipment fitted. |
| 669 | 4 | `static boolean recorded(HistorySave h)` | Whether the history holds any of the research series (a city founded the 1900 way before R5 has none, and a city founded the default way never). |
| 674 | 43 | `Node overTime(List<Discovery> timeline)` |  |
| 719 | 4 | `TimeChart.Line line(String key, String label, String colour, String unit, double[] values)` | One line of a chart, in a unit of History's (HistoryScreen.plotScale() and fmtUnit()). |
| 725 | 11 | `VBox chartCard(String title, String info, String unit, List<TimeChart.Line> lines, HistorySave h)` | A chart's card: its title and (i), the chart - made once in the card's scope, handed the month's data and its size at every draw - and its key. |


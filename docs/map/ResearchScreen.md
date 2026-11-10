# ResearchScreen.java - 1,199 lines · 73 methods · 28 constants · interface

`ham/citybuildersim/ui/ResearchScreen.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> Policy › Research (R5, slice B1; R3b, slice B): the sixth Policy area, drawn in Policy's own style - three pages: the Overview (who is at research and
> what is in their reach), Funding (a grant and a prize for each field, staged through Policy's set and tray) and Found (what the city has discovered
> and what reached it from abroad).
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
> R3B (slice B) put the Overview and Funding pages in PAGES before FOUND, page() switches on policy.policyPage, and the hub's sixth card (areaCard())
> opens the Overview. Funding's dials are Policy's Ladders (ladderOf()), their rows ResearchView.card()'s drawn at the value each Ladder shows, so a month
> that lands under a held thumb leaves the thumb and its rows where the hand left them (KP6).

**Uses:** [Palette](Palette.md) (96), [ResearchView](ResearchView.md) (80), [PolicyScreen](PolicyScreen.md) (73), [Kept](Kept.md) (64), [HistorySave](HistorySave.md) (12), [Icons](Icons.md) (11), [TimeChart](TimeChart.md) (9), [CityCalendar](CityCalendar.md) (7), [ResearchPreview](ResearchPreview.md) (6), [Game](Game.md) (4), [DecisionLog](DecisionLog.md) (4), [Research](Research.md) (3), [Pieces](Pieces.md) (3), [UserInterface](UserInterface.md) (2), [Ladder](Ladder.md) (2), [EquipmentFit](EquipmentFit.md) (2), [HistoryScreen](HistoryScreen.md) (2), [BankScreen](BankScreen.md) (2), [ResearchCatalog](ResearchCatalog.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Sector](Sector.md) (1), [ChartModel](ChartModel.md) (1)

**Used by (3):** [KeptBench](KeptBench.md), [PolicyScreen](PolicyScreen.md), [ServicesScreen](ServicesScreen.md)

## Sections

| line | section |
|---:|---|
| 78 | THE PAGES |
| 117 | THE FOUR FIGURES (R3b: the frame's, across the Overview and Funding pages: the Overview's lead and Funding's, one piece) |
| 170 | THE OVERVIEW PAGE (R3b; research-design 5.5 and 6.2): who is at research, in what, and what is in their reach |
| 290 | THE HUB'S SIXTH CARD (R3b): the Overview's headline figures, a door to it |
| 307 | THE FUNDING PAGE (R3b; mockup 1): a card a field, a grant and a prize, staged through Policy's set and tray |
| 562 | THE FOUND PAGE (mockup 3) |
| 668 | · the words |
| 735 | · the rows |
| 811 | THE TIMELINE |
| 1119 | OVER TIME: four small charts, made once and handed the month's data |

## Enum constants

| line | constant | says |
|---:|---|---|
| 883 | `ResearchScreen.Kind.LABEL` |  |
| 883 | `ResearchScreen.Kind.DOT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ResearchScreen.OVERVIEW` | `"Overview"` | The area's first page, where a door to the area lands: who is at research and what is in reach (R3b). |
| 86 | `ResearchScreen.FUNDING` | `"Funding"` | ...its second: the grant and the prize of each field, staged like every dial of Policy (R3b). |
| 89 | `ResearchScreen.FOUND` | `"Found"` | ...and its third: what has been discovered. |
| 92 | `ResearchScreen.PAGES` | `{ OVERVIEW, FUNDING, FOUND }` | ...the area's pages, in their tabs' order (a strip of one tab draws no tabs). |
| 95 | `ResearchScreen.PAGE_ICONS` | `{ Icons.OVERVIEW, Icons.COIN, Icons.REPORTS }` | ...and their icons. |
| 98 | `ResearchScreen.AREA_INFO` | `"Who is at research and what is in reach of them, the grant and the prize of ...` | The area's (i). |
| 174 | `ResearchScreen.FIELDS_INFO` | `"Each field's researchers - the city's own, from the schools' faculty and the...` |  |
| 178 | `ResearchScreen.SCHOOLS_INFO` | `"The kinds of school that host research: the University, hosting every field,...` |  |
| 320 | `ResearchScreen.GRANT_INFO` | `"A month's money for research posts at the schools that host the field: one p...` |  |
| 324 | `ResearchScreen.PRIZE_INFO` | `"What the city pays for a discovery made here, once, and only on a find: noth...` |  |
| 328 | `ResearchScreen.PREVIEW_INFO` | `"What the field's researchers would find here before the world does over the ...` |  |
| 332 | `ResearchScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press: each field's ...` |  |
| 337 | `ResearchScreen.CARD_W` | `540, INNER_W = CARD_W - 24` | A card's width, and what its rows and dials are drawn within it (the card's padding takes twelve a side). |
| 566 | `ResearchScreen.TIMELINE_INFO` | `"Every discovery the city has come to know since its founding, a row a field....` |  |
| 570 | `ResearchScreen.FOUND_INFO` | `"What the city's own researchers found before the world did, the newest first...` |  |
| 572 | `ResearchScreen.RECIPES_INFO` | `"The ways of running a building, other than its usual one, that the city know...` |  |
| 575 | `ResearchScreen.EQUIPMENT_INFO` | `"The pieces of equipment the city knows, on the buildings they suit: how many...` |  |
| 578 | `ResearchScreen.OVER_TIME_INFO` | `"Four charts of the city's research since its founding: the researchers at wo...` |  |
| 747 | `ResearchScreen.ROW_STYLE` | `"-fx-padding: 5 6 5 6; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border...` | A row's ground: a hairline under it. |
| 750 | `ResearchScreen.DOOR_STYLE` | `ROW_STYLE + " -fx-background-radius: 6; -fx-cursor: hand;"` | ...and a row that is a door's: the pointer's hand and, under it, a ground. |
| 751 | `ResearchScreen.DOOR_LIT` | `DOOR_STYLE + " -fx-background-color: " + Palette.CONTROL + ";"` |  |
| 816 | `ResearchScreen.DOT` | `12, DOT_GAP = 3` | A dot's width, and the gap two dots of a row keep from each other before one stacks above the other. |
| 819 | `ResearchScreen.LEVEL` | `26` | How far a stacked dot stands above the one under it: the dot, the figure that sits over it and a margin. |
| 822 | `ResearchScreen.LABEL_W` | `150, PAD_R = 16, TICK_ROOM = 60` | The room a row's name takes at the left, the room kept at the right, and the least apart two year ticks stand. |
| 825 | `ResearchScreen.ROW_TOP` | `18, ROW_FOOT = 8, ROW_MIN = 46` | A row's room over its top dot (the figure "+1.5y"), under its bottom one, and its least height (its two lines of words). |
| 828 | `ResearchScreen.AXIS_H` | `20` | The room under the last row for the years' names. |
| 893 | `ResearchScreen.PLACE` | `"research.place"` | The key a timeline child carries its Place under. |
| 1124 | `ResearchScreen.CHART_W` | `575, CHART_H = 150` | A small chart's size: the Bank's two-across cards'. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 70 | `private final PolicyScreen policy` |  |
| 71 | `private final UserInterface ui` |  |
| 953 | `private ObservableList<Node> stage` |  |
| 954 | `private final Canvas canvas` |  |
| 955 | `private int first` |  |
| 999 | `double x0, x1, rowsEnd, total` |  |
| 1000 | `double[] top, height` |  |
| 1001 | `final Map<String, Integer> level` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1132 | **type** `final class ResearchScreen` | Policy › Research (R5, slice B1; R3b, slice B): the sixth Policy area, drawn in Policy's own style - three pages: the Overview (who is at research and what is in their reach), Funding (a grant and a prize for each fie... |
| 73 | 4 | `ResearchScreen(PolicyScreen policy, UserInterface ui)` |  |

### THE PAGES (lines 78-116)

| line | len | member | says |
|---:|---:|---|---|
| 104 | 7 | `void page(VBox page)` | The page open, drawn into `page`. |
| 113 | 3 | `private void go(String pageName)` | A door to a page of the area. |

### THE FOUR FIGURES (R3b: the frame's, across the Overview and Funding pages: the Overview's lead and Funding's, one piece) (lines 117-169)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 3 | `HBox vitals()` | The four figures across the top of the Overview and Funding (mockups 1 and 2), in the frame in place of Policy's four, as Zoning's are: the researchers at work with the idle among them and to each 1,000 workers; a mon... |
| 131 | 14 | `HBox figures(ResearchView.Overview ov, String onPage)` | ...the strip itself, for the page `onPage` of the area. |
| 147 | 6 | `static String researchersNote(ResearchView.Overview ov)` | "3 idle · 3.6 per 1,000 workers": the idle among the researchers, and how many there are to each 1,000 people at work. |
| 155 | 4 | `static String billNote(ResearchView.Overview ov)` | "posts and labs, prizes paid $2.1M" or "nothing spent on research": the month's bill, and the prizes the treasury paid the labs. |
| 161 | 8 | `static String fieldIcon(Field f)` | A field's icon: the gear, the heart, the offices and the state (the mockup's cog, heart, briefcase and gavel). |

### THE OVERVIEW PAGE (R3b; research-design 5.5 and 6.2): who is at research, in what, and what is in their reach (lines 170-289)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 28 | `void overviewPage(VBox page)` |  |
| 212 | 18 | `VBox defaultOverviewCard(ResearchView.Overview ov)` | What a city founded the default way says on the Overview: nothing is in reach and nothing is found, and a grant's posts, if one is set, have nothing to work on. |
| 232 | 3 | `static String researchersWords(double n)` | "412 researchers" - and "1 researcher". |
| 237 | 30 | `HBox fieldRow(ResearchView.FieldReach r)` | A field's row: its name and schools, its researchers (the labs' and the idle among them), what is in reach, what it found here and had from abroad; a door to its funding. |
| 269 | 12 | `HBox schoolRow(ResearchView.SchoolRow s)` | A kind of school's row: how many stand, and what it carries of each field it hosts. |
| 283 | 6 | `static String postsWords(List<ResearchView.SchoolPosts> posts)` | What a kind of school carries of the city's research posts, a field at a time ("Engineering: 12 filled, room for 40"), or null where it carries none. |

### THE HUB'S SIXTH CARD (R3b): the Overview's headline figures, a door to it (lines 290-306)

| line | len | member | says |
|---:|---:|---|---|
| 295 | 11 | `VBox areaCard()` | Policy's RESEARCH area card: the researchers at work as slices by field, the month's bill and what was found here. |

### THE FUNDING PAGE (R3b; mockup 1): a card a field, a grant and a prize, staged through Policy's set and tray (lines 307-561)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 3 | `static String grantKey(Field f)` | The keys a field's grant and prize are staged under in Policy's set. |
| 316 | 3 | `static String prizeKey(Field f)` |  |
| 340 | 8 | `ResearchView.Staged stagedSet()` | What is staged on the page, as the model's preview takes it. |
| 350 | 9 | `static ResearchView.Staged shownSet(ResearchView.Staged base, ResearchView.Funding fu, double grant, double prize)` | ...with a field's two dials as they are shown (a thumb held through a month's landing is not at the staged value): the same set where they are the set's own. |
| 361 | 5 | `Effect budgetEffect()` | THE BUDGET of the tray: the balance as the books have it, and with the staged grants' posts (ResearchView.budget()). |
| 367 | 13 | `void fundingPage(VBox page)` |  |
| 382 | 5 | `ResearchView.Card cardAt(ResearchView.FieldReach reach, ResearchView.Funding fu, ResearchView.Staged base, ResearchPreview.Resu...` | A field's card as the dials show it: the model's read at the grant and prize on the thumbs (not the staged set's, when a month lands under a held thumb). |
| 388 | 57 | `VBox fieldCard(ResearchView.FieldReach reach, ResearchView.Staged base, ResearchPreview.Result before, ResearchPreview.Result a...` |  |
| 447 | 7 | `private void setGrant(Field f, double v)` | A grant set at a dial, and logged as the other promises are. |
| 456 | 7 | `private void setPrize(Field f, double v)` | ...and a prize. |
| 465 | 7 | `static HBox dialHead(String name, String unit, String info)` | A dial's caption: its name, what it is counted in and an (i). |
| 474 | 5 | `static String grantSub(ResearchView.Funding fu)` | Under the grant dial: the room, and what a post costs. |
| 481 | 5 | `static String prizeSub(ResearchView.Funding fu, boolean researching)` | Under the prize dial: where labs come, and that it is paid only on a find. |
| 488 | 21 | `static List<Effect> rowsOf(ResearchView.Card c)` | A card's rows, now against after: the researchers, the posts, the labs, what it costs a month, the prize and what the field would find here first. |
| 511 | 3 | `static String labsWords(int labs)` | "2 labs". |
| 516 | 37 | `static String sayWords(ResearchView.Card c)` | The sentence a card ends in, chosen among the cases the model's reads make (no arithmetic here): what holds the field back, or what the dials would do. |
| 555 | 6 | `static VBox sayBox(String text)` | A card's closing sentence, in a box of its own. |

### THE FOUND PAGE (mockup 3) (lines 562-667)

| line | len | member | says |
|---:|---:|---|---|
| 581 | 62 | `void foundPage(VBox page)` |  |
| 645 | 9 | `VBox defaultCard()` | What a city founded the default way says: it knew every discovery from the start, so nothing arrives and nothing is found. |
| 656 | 11 | `VBox leadCard(int here, int abroad)` | The lead: how many the city found itself and how many came from abroad, and how many it knew at the founding. |

### the words (lines 668-734)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 4 | `static String fieldName(Field f)` | A field as it is read: "Engineering". |
| 677 | 8 | `static String fieldColour(Field f)` | A field's colour: engineering the money blue, medicine the people teal, business the business violet, law the sand (the mockup's). |
| 687 | 5 | `static String whenWords(Discovery d)` | How the city came to know a discovery: "found here in June 1973, 4.6 years before the world" or "from abroad, June 1955". |
| 694 | 3 | `static String fromWords(Discovery d)` | ...and "known since the founding" for what no discovery made known. |
| 699 | 6 | `static String dotWords(Discovery d)` | A dot's tooltip: the name, its field and how the city came to know it, what it touches and what it does. |
| 707 | 3 | `static String aheadWords(Discovery d)` | "+1.5y": how many years ahead of the world a find was, one decimal. |
| 712 | 3 | `static String ofWords(int n, int of, String what)` | "12 of 20 buildings". |
| 717 | 8 | `static String verdictWords(EquipmentFit.Kind kind)` | The sector's verdict on a piece, in words. |
| 727 | 7 | `static String verdictColour(EquipmentFit.Kind kind)` | ...and its colour: a verdict says what to do, never how big. |

### the rows (lines 735-810)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 7 | `private static VBox cell(double width, Node...lines)` | A fixed-width cell of a row: its lines one under another. |
| 754 | 12 | `HBox foundRow(Discovery d)` | One find of the city's own: its name, its field, its month and how far ahead of the world. |
| 768 | 12 | `HBox recipeRow(ResearchView.RecipeUse u)` | One way of running a building: its recipe and building, how many of the stack run it as a share, and where it came from. |
| 782 | 28 | `HBox pieceRow(ResearchView.PieceRow r)` | One piece on one stack: its name and building, how many of the buildings have it fitted as a share, the sector's verdict, and a door to the sector. |

### THE TIMELINE (lines 811-1118)

| line | len | member | says |
|---:|---:|---|---|
| 831 | 5 | `static double xOf(int month, int first, int last, double plotWidth)` | Where a month stands across a plot this wide, from its first month to its last: 0 at the first, the width at the last, a month outside them held at the nearer end. |
| 843 | 16 | `static int[] levels(int[] months, int first, int last, double plotWidth, double slot)` | How high each dot of a row stands (pure): 0 on the row's floor, 1 above it, and so on. |
| 861 | 20 | `static int[] yearTicks(int first, int last, double plotWidth)` | The months of the year ticks (pure): a January in every `step`th year, the smallest step that keeps two ticks TICK_ROOM apart, inside the axis. |
| 883 | 1 | **type** `private enum Kind` | What a timeline child is: a field's name at the left, or a dot. |
| 890 | 1 | **type** `private record Place(Kind kind, int row, int month, int to, String code, boolean here, String colour, Strin...` | Where a timeline child belongs, as the draw says it and layout places it: its row (a field's number); for a dot its month, the month its line ends at (the world's, held at the city's month), its code, whether it was f... |
| 895 | 3 | `private static Place placeOf(Node n)` |  |
| 900 | 12 | `VBox timelineCard(List<Discovery> timeline)` | The card with the timeline in it. |
| 914 | 11 | `VBox rowLabel(Field f)` | A field's name in its colour, and its school's words under it. |
| 927 | 16 | `Region dot(Discovery d, int row, int now)` | A discovery as a dot: hollow from abroad, filled found here; its words the tooltip. |
| 951 | 167 | **type** `static final class Timeline extends Pane implements Kept.Staging` | The timeline's pane: a row a field, its name at the left, a dot a discovery, the year ticks along the foot and each found-here dot's line to the world's date. |
| 957 | 5 | `Timeline()` _(in ResearchScreen.Timeline)_ |  |
| 964 | 8 | `List<Node> dots()` _(in ResearchScreen.Timeline)_ | The dots among its children, in the order they were drawn (the harness measures where layout put them). |
| 974 | 5 | `void set(int first, int last, int rows)` _(in ResearchScreen.Timeline)_ | The months the axis runs between and how many rows it has (set by every draw). |
| 980 | 3 | `public ObservableList<Node> getChildren()` _(in ResearchScreen.Timeline)_ |  |
| 984 | 4 | `public void stage()` _(in ResearchScreen.Timeline)_ |  |
| 989 | 7 | `public void commit()` _(in ResearchScreen.Timeline)_ |  |
| 998 | 5 | **type** `private static final class Geometry` _(in ResearchScreen.Timeline)_ | Where everything stands at a width: the rows' tops and heights, each dot's level, and the plot's left and right. |
| 1004 | 32 | `private Geometry geometry(double width)` _(in ResearchScreen.Timeline)_ |  |
| 1038 | 3 | `private static double centreY(Geometry g, Place p)` _(in ResearchScreen.Timeline)_ | The height of a dot's centre: its level up from the foot of its row. |
| 1042 | 3 | `public Orientation getContentBias()` _(in ResearchScreen.Timeline)_ |  |
| 1046 | 3 | `protected double computePrefWidth(double height)` _(in ResearchScreen.Timeline)_ |  |
| 1050 | 4 | `protected double computePrefHeight(double width)` _(in ResearchScreen.Timeline)_ |  |
| 1055 | 22 | `protected void layoutChildren()` _(in ResearchScreen.Timeline)_ |  |
| 1079 | 38 | `private void paint(Geometry g, double width)` _(in ResearchScreen.Timeline)_ | The canvas: a rule under each row, the year ticks and their names, and the found-here dots' lines, their figures and the ends of those that run past now. |

### OVER TIME: four small charts, made once and handed the month's data (lines 1119-1199)

| line | len | member | says |
|---:|---:|---|---|
| 1127 | 7 | `static List<String> seriesNames()` | The names of the history's research series: a field's researchers, the month's bill, the share of the equipment fitted. |
| 1136 | 4 | `static boolean recorded(HistorySave h)` | Whether the history holds any of the research series (a city founded the 1900 way before R5 has none, and a city founded the default way never). |
| 1141 | 39 | `Node overTime(List<Discovery> timeline)` |  |
| 1182 | 4 | `TimeChart.Line line(String key, String label, String colour, String unit, double[] values)` | One line of a chart, in a unit of History's (HistoryScreen.plotScale() and fmtUnit()). |
| 1188 | 11 | `VBox chartCard(String title, String info, String unit, List<TimeChart.Line> lines, HistorySave h)` | A chart's card: its title and (i), the chart - made once in the card's scope, handed the month's data and its size at every draw - and its key. |


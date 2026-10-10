# OilView.java - 1,333 lines · 70 methods · 41 constants · model

`ham/citybuildersim/OilView.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The oil industry on one page (0.7.96, batch O12; runs/spec-oil.md 2.13):
> the city's wells by kind with what they would lift over the next ten years
> if nothing new were built, the refinery's units with each one's spread and
> the gate that stops one more, every product's price and month, the
> planners' words, and the strategic reserve with its two levers.
> 
> WHY. Oil's Operations page drew the wells as every business is drawn
> (SectorFlow): crude out, a ring of the rate, and a grid of its own lines.
> Since 0.7.84 a well declines, since 0.7.91 a platform holds a plateau and
> then falls, since 0.7.93 the city's crude is two pools - and none of that
> answered "how long does our own crude last?". The refinery's units were
> each a building on the Build tab with no word of why the refiners order
> one and not another, and the city's reserve, built since 0.7.85, had no
> lever at all (fixO8-notes.md: "the reserve's Fill and Release are O12's").
> Jerus's oil industry screen, drawn as the research's mockup 2
> (claude/oil-and-ports-research.md 6): four figures, the wells by type and
> their chart, the units table, the products table. These are its figures
> and its words, held by a harness (OilViewCheck) rather than worked out in
> the screen: the wells to Oil's own reads, the forecast to the vintages'
> profiles and each pool's tonnes, the units and the products to the
> refinery's picture (RefineryView, whose month and takers they are - so
> the two pages cannot disagree), the reserve to StrategicReserve and the
> levers to Game.fillReserve() and releaseReserve(). The screen
> (ui/SectorScreen, OPERATIONS · THE OIL INDUSTRY) only paints them.
> 
> WHICH MONTH. The month just run, as the refinery's picture reads it: its
> lift by pool (Oil.getLiftedOnGround(), getLiftedAtSea()), crude's and the
> products' clearings, the refinery's rows. None of it is saved, so after a
> load the month's figures read "not counted" until a month runs (counted
> is RefineryView's: the refiners kept a month). The wells, the pools, the
> units standing, the spreads and gates, the prices and the reserve are the
> city's state, read at any time.
> 
> Pure: it reads the city and changes nothing (the planner's appraisals are
> the build card's reads).

**Uses:** [RefineryView](RefineryView.md) (97), [Good](Good.md) (23), [Oil](Oil.md) (19), [Refining](Refining.md) (15), [RefineryFlow](RefineryFlow.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (10), [SpreadPlanner](SpreadPlanner.md) (9), [LandManager](LandManager.md) (7), [Sectors](Sectors.md) (6), [Game](Game.md) (5), [GoodsMarket](GoodsMarket.md) (3), [StrategicReserve](StrategicReserve.md) (3), [CityLand](CityLand.md) (3), [ChartModel](ChartModel.md) (3), [Trade](Trade.md) (2), [World](World.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [CityCalendar](CityCalendar.md) (2), [YearBook](YearBook.md) (1), [Resource](Resource.md) (1), [BuildingManager](BuildingManager.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Formats](Formats.md) (1)

**Used by (2):** [OilViewCheck](OilViewCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 58 | THE FIGURES |
| 266 | · · the wells |
| 324 | · · crude's month, and the products' across the edge |
| 351 | · · the units |
| 354 | · · the products |
| 383 | · · the reserve |
| 448 | THE WELLS AHEAD: what the wells standing would lift if nothing new |
| 537 | THE UNITS: the crude units, then every kind of conversion unit - |
| 614 | THE LAYOUT (mockup 2's, at 1,389 x 868): the page's 1,234 px, the |
| 652 | THE CHART (mockup 2's LOCAL CRUDE · B/D): a bar a year, the month just |
| 788 | THE WORDS |
| 828 | · the four figures (mockup 2's) |
| 888 | · the wells by type (mockup 2's two cards) |
| 998 | · the units table (mockup 2's THE REFINERY) |
| 1157 | · the products table (mockup 2's PRODUCTS) |
| 1257 | · the strategic reserve, and its two levers |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1313 | `OilView.Unit.TONNES` |  |
| 1313 | `OilView.Unit.MONEY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `OilView.FORECAST_YEARS` | `10` | Years the wells' chart looks ahead (mockup 2's "the next ten are pale"). |
| 66 | `OilView.PLATFORMS_LISTED` | `3` | Platforms the wells card names one by one and the chart draws apart; past them the rest are one line and one series. |
| 69 | `OilView.MONTHS_A_YEAR` | `YearBook.MONTHS_A_YEAR` | The months in a year the forecast averages. |
| 72 | `OilView.LITRES_A_BARREL` | `158.987` | A barrel's litres, 158.987 (RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY is 30.44 days of it): the reserves in barrels. |
| 622 | `OilView.PAGE_AT_1389` | `1234` | The page's width at the 1,389 x 868 window (the refinery picture's card's 1,234). |
| 625 | `OilView.BOX_PAD_X` | `12, BOX_PAD_Y = 10, BOX_EDGE = 1` | A box's padding across and down, and its border (mockup 2's .box: 10 px 12 px, 1 px). |
| 628 | `OilView.BOX_GAP` | `10, TILE_GAP = 8` | The gap between the boxes and between the figures' tiles (mockup 2's 10 and 8). |
| 631 | `OilView.WELLS_W` | `548` | The wells' box's width (mockup 2's 548); the units' box takes the rest of the row. |
| 634 | `OilView.CHART_W` | `WELLS_W - 2 * BOX_PAD_X - 2 * BOX_EDGE` | The chart's width: the wells' box inside its padding and border (mockup 2's 522). |
| 637 | `OilView.TILE_PAD_X` | `12, CARD_PAD_X = 10, CARD_GAP = 8` | A figure tile's padding across (mockup 2's .fig: 12 px), and a wells card's (.wc: 10 px). |
| 640 | `OilView.FIGURE_SIZE` | `21, TILE_WORDS = 11, CARD_WORDS = 11, CARD_HEAD = 12, CELL = 11.5, CELL_NOTE ...` | The sizes the page sets its words in (mockup 2's): a figure, a tile's line, a card's line, a table's cells and its notes. |
| 644 | `OilView.DOT` | `10, DOT_GAP = 6` | A series' or a product's swatch, and the gap after it (mockup 2's .dot: 10 px; .plat: 6 px). |
| 647 | `OilView.PILL_PAD_X` | `6, PILL_GAP = 6` | A pill's padding across and the gap after it (mockup 2's .pill: 6 px, and the cell's space). |
| 650 | `OilView.CELL_PAD_X` | `6` | A table cell's padding across (mockup 2's td: 6 px). |
| 662 | `OilView.CHART_HEIGHT` | `146` | The chart's height on the page (mockup 2's 146 px). |
| 665 | `OilView.AXIS_W` | `40, YEARS_H = 20, CHART_TOP = 10, CHART_RIGHT = 8` | Room at the left for the scale's figures, under the bars for the years, over them, and at the right (mockup 2's 40, 20, 10, 8). |
| 668 | `OilView.BAR_GAP` | `3, NOW_OPACITY =.95, AHEAD_OPACITY =.32` | The gap between bars, and a bar's opacity now and ahead (mockup 2's 3 px, .95 and .32). |
| 671 | `OilView.GRIDLINES` | `5` | Gridlines the scale aims at. |
| 674 | `OilView.YEAR_EVERY` | `5` | Every how many bars a year is written under the axis (mockup 2's five). |
| 677 | `OilView.CHART_WORDS` | `9.5` | The faces' sizes: the scale's and the years' figures, the now line's words (mockup 2's 9.5). |
| 680 | `OilView.LAND` | `"#c9b68f"` | The land wells' colour (mockup 2's, the ore's), and the platforms' (mockup 2's two blues; a lighter and a darker step of the same hue for a third and the rest, ★ O12-6). |
| 681 | `OilView.PLATFORM_COLOURS` | `{ "#4fa3c7", "#2c6f8f", "#86c6e2" }` |  |
| 682 | `OilView.OTHER_PLATFORMS` | `"#1d4b62"` |  |
| 685 | `OilView.GRID` | `"#1d2b3c", ACCENT = "#5aa9ff"` | A gridline, and the now line (mockup 2's). |
| 752 | `OilView.NOTHING_LIFTED` | `"no well stands, and none would lift"` | What the chart says with nothing to draw. |
| 776 | `OilView.AHEAD_WORDS` | `"if nothing new is built ›"` | ...and under them. |
| 826 | `OilView.NOT_COUNTED` | `"not counted until a month runs"` | What the page says while the month is not counted. |
| 842 | `OilView.TEXT` | `RefineryView.TEXT, GOOD = "#3fb950", BAD = "#f85149", WARN = RefineryView.HOT` | The colour of a figure that is the answer, of good news and bad, and of a warning. |
| 897 | `OilView.FACT` | `RefineryView.TEXT_2` | The figure's colour on a card: the secondary grey (mockup 2's .kv b). |
| 1019 | `OilView.UNIT_HEADS` | `{ "Unit · what it turns into what", "b/d", "running", "spread", "state" }` | The table's heads, in its columns' order (mockup 2's). |
| 1022 | `OilView.UNIT_WIDTHS` | `{ 0, 64, 62, 76, 210 }` | The columns' widths (b/d, running, spread, the state; the first takes the rest - 238 px in the units' box's 650 at the 1,389 window). |
| 1025 | `OilView.PILL` | `RefineryView.TEXT_2, PILL_BUILDING = ACCENT, PILL_GOOD = GOOD, PILL_GATE = WA...` | The pills' colours: neutral, under way, good, a gate the player can clear, a loss (mockup 2's .pill, .b, .w, .r). |
| 1080 | `OilView.NO_BREAK` | `'\u00a0'` | The space inside a table's item, where a line does not break: "fuel oil 7" stays whole. |
| 1169 | `OilView.PRODUCT_HEADS` | `{ "Product", "price here", "world", "× crude", "made here", "used here", "imp...` | The table's heads, in its columns' order (mockup 2's). |
| 1173 | `OilView.PRODUCT_WIDTHS` | `{ 150, 76, 76, 58, 72, 72, 72, 72, 0 }` | The columns' widths (the product, the seven figures; where it went takes the rest - 560 px in the box's 1,208 at the 1,389 window). |
| 1176 | `OilView.PRODUCTS_WORDS` | `"this month · litres(M a million, k a thousand); crude, bitumen and coke in" ...` | The products table's line over it. |
| 1180 | `OilView.PRODUCTS_RIGHT` | `"world: halfway between the import and the export price · × crude: the" + " w...` | ...and at its right. |
| 1184 | `OilView.CRUDE` | `RefineryView.CRUDE` | Crude's colour in the table (mockup 1's). |
| 1207 | `OilView.HALF` | `.5` | Under half a unit, a figure is written as nothing (RefineryView.figure() would write "0"). |
| 1260 | `OilView.LADDER_STEPS` | `100` | The steps a lever's ladder takes from nothing to the most it may be set to: a hundredth of its reach a step (★ O12-8). |
| 1279 | `OilView.NO_RESERVE` | `"No Strategic Reserve stands.Build one under Build › Industry › Oil storage: ...` | What the reserve's section says when none stands and none is held. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 1280 | **type** `public final class OilView` | The oil industry on one page (0.7.96, batch O12; runs/spec-oil.md 2.13): the city's wells by kind with what they would lift over the next ten years if nothing new were built, the refinery's units with each one's sprea... |
| 56 | 1 | `private OilView()` |  |

### THE FIGURES (lines 58-447)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 3 | `public static double barrelsADay(double tonnes)` | Barrels a day of crude lifted at `tonnes` a month: its litres (Refining.CRUDE_LITRES_PER_TONNE) over a barrel a day's month. |
| 90 | 1 | **type** `public record Platform(String name, double depth, int km, int wells, int slots, int oldest, double lifted)` | One platform as it stands. |
| 102 | 1 | **type** `public record Series(String name, String colour, double now, double[] ahead)` | A series of the wells' chart: the land wells, a platform, or the platforms past PLATFORMS_LISTED together. |
| 121 | 4 | **type** `public record UnitRow(Kind kind, int standing, int onSite, double feed, double run, double share, double ci...` | One row of the units table: a kind of conversion unit, or the crude units (kind null). |
| 123 | 1 | `public boolean crude()` _(in OilView.UnitRow)_ |  |
| 141 | 2 | **type** `public record Product(Good good, double price, double world, double ladder, double made, double used, doubl...` | One product's month, and crude's. |
| 145 | 1 | **type** `public record Taker(String name, double units)` | A taker of a product in its row: a name, and the units it took. |
| 165 | 9 | **type** `public record Reserve(int standing, double room, double held, double book, double fill, double release, dou...` | The city's strategic reserve (StrategicReserve), and its levers' reach. |
| 168 | 1 | `public double roomLeft()` _(in OilView.Reserve)_ | Tonnes of room the tanks have left. |
| 170 | 1 | `public double releaseMost()` _(in OilView.Reserve)_ | The most a release may be set to: what it holds, or what is set already. |
| 172 | 1 | `public boolean shown()` _(in OilView.Reserve)_ | Whether there is a reserve to show: one standing, or crude still held. |
| 212 | 43 | **type** `public record View(boolean counted, int month, double rate, int landWells, int platformWells, List<Platform...` | The page. |
| 221 | 5 | `public double lifted()` _(in OilView.View)_ | The month's lift, tonnes: the two pools'; NaN not counted. |
| 228 | 1 | `public double liftedOnGround()` _(in OilView.View)_ | ...the land wells' alone. |
| 231 | 1 | `public double left()` _(in OilView.View)_ | Tonnes left in both pools. |
| 234 | 5 | `public double[] ahead()` _(in OilView.View)_ | Each year ahead's mean month, all the series together, tonnes. |
| 241 | 4 | `public Product product(Good g)` _(in OilView.View)_ | One product's row, or null. |
| 247 | 4 | `public UnitRow unit(Kind k)` _(in OilView.View)_ | The crude units' row, or a kind's. |
| 253 | 1 | `public RefineryView.Picture chart(double width, double height)` _(in OilView.View)_ | The chart at a width and a height (THE CHART). |
| 257 | 147 | `public static View of(Game game)` | The oil industry in `game` (pure). |
| 406 | 3 | `static String platformName(int i)` | A platform's name by its place: "Platform A" to "Platform Z", then "Platform 27" on. |
| 410 | 4 | `private static LandManager.SeaField fieldOf(List<LandManager.SeaField> fields, int cell, int index)` |  |
| 416 | 12 | `static int[] fieldsByPool(Game game)` | The oil fields the city owns, {on dry ground, in the sea}: each field once, by where its centre lies (World.depthAt()). |
| 430 | 4 | `static double priceOf(Game game, Good g)` | A product's price here, a unit; NaN with no market. |
| 436 | 4 | `static double worldOf(Game game, Good g)` | ...the world's: halfway between its import and its export price, in city money. |
| 442 | 5 | `public static double ladder(Good g)` | A good's world price over crude's (pure): a litre against a litre of crude (Refining.CRUDE_LITRES_PER_TONNE), a tonne good against a tonne. |

### THE WELLS AHEAD: what the wells standing would lift if nothing new (lines 448-536)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 5 | `static List<Vintage> landOf(List<Vintage> vintages)` | The land vintages, oldest first. |
| 474 | 24 | `public static List<List<Vintage>> deal(List<Vintage> vintages, List<Oil.Platform> platforms)` | The platform vintages dealt to the platforms, oldest to the oldest, each platform its wells in slots (pure). |
| 500 | 9 | `public static double ask(List<Vintage> vintages, double each, WellKind kind, int month)` | What `vintages` of `kind` ask in `month` at nameplate (pure): each one's wells x `each` x its profile at its age, nothing once worn out. |
| 515 | 21 | `public static double[][] forecast(List<Vintage> land, List<List<Vintage>> platforms, double landEach, double seaEach, double gr...` | The years ahead (pure): {the land wells', then each platform's} mean tonnes a month in each of `years` years, the first from `month` + 1, at `rate`, each pool's lift no more than it has left. |

### THE UNITS: the crude units, then every kind of conversion unit - (lines 537-613)

| line | len | member | says |
|---:|---:|---|---|
| 545 | 54 | `static List<UnitRow> units(Game game, Refining r, RefineryView.View refinery)` |  |
| 601 | 12 | `static SpreadPlanner.Candidate oneMoreCrude(Refining r, BusinessInvestment plans, List<BuildingsTemplate> sizes)` | One more crude unit, weighed by the planner (Refining.appraise(), the build card's read): of its sizes, the one that passes and earns most on its cost, or the smallest's refusal. |

### THE LAYOUT (mockup 2's, at 1,389 x 868): the page's 1,234 px, the (lines 614-651)

### THE CHART (mockup 2's LOCAL CRUDE · B/D): a bar a year, the month just (lines 652-787)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 62 | `static RefineryView.Picture chart(View v, double width, double height)` | The chart at `width` x `height` (pure; mockup 2's chart of the wells, laid out as it lays it). |
| 755 | 14 | `static String axisWords(double barrels)` | The scale's figures: "10k", "2.5k", "500", "2.5", "5M", "1.5B". |
| 771 | 3 | `static String nowWords(View v)` | The now line's words: "now · 45,300 b/d", or that the month is not counted. |
| 779 | 4 | `static String barTip(View v, Series s, int i, double barrels)` | Under the pointer, a bar's part: "Platform A, 2031: 1,200 b/d (the next twelve months' mean)". |
| 784 | 3 | `private static double tonnesOf(double barrels)` |  |

### THE WORDS (lines 788-827)

| line | len | member | says |
|---:|---:|---|---|
| 793 | 3 | `public static String barrelsWords(double tonnes)` | Crude a month as barrels a day, to three figures: "45,300 b/d"; "—" not counted. |
| 798 | 8 | `public static String megaTonnes(double t)` | Tonnes in three or four figures: "26.2 Mt", "1.42 Mt", "830k t", "640 t". |
| 808 | 3 | `static String percent(double share)` | A share in whole per cent: "43%". |
| 813 | 4 | `static String money(double thousands)` | A sentence's money (Formats.amount()): "$78.8M"; what rounds to nothing, "$0". |
| 819 | 5 | `static String signedMoney(double thousands)` | ...signed: "+$11.5M", "−$2.0M". |

### the four figures (mockup 2's) (lines 828-887)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 1 | **type** `public record Figure(String label, String value, String line, String colour, String tip)` | One of the four figures over the page. |
| 845 | 37 | `public static List<Figure> figures(View v)` | LIFTED HERE, RESERVES LEFT, CRUDE IMPORTED, PRODUCTS ACROSS THE EDGE. |
| 884 | 3 | `static String countWords(int n, String noun)` | "160 wells", "1 well". |

### the wells by type (mockup 2's two cards) (lines 888-997)

| line | len | member | says |
|---:|---:|---|---|
| 891 | 1 | **type** `public record Fact(String label, String value, String colour)` | A line of a wells card: its words, its figure, the figure's colour. |
| 894 | 1 | **type** `public record PlatformLine(String colour, String name, String words, String figure, String tip)` | A platform's line on its card: its colour, its name, its words (depth, distance, phase), its figure and its tip. |
| 900 | 12 | `public static List<Fact> landFacts(View v)` | The land wells' card: lifting, a well's average, the decline, the ground pool, the wells drilled in the last year and wearing out in the next. |
| 914 | 24 | `public static List<PlatformLine> platformLines(View v)` | The platforms' lines: one each to PLATFORMS_LISTED, then the rest as one. |
| 940 | 8 | `static String phaseWords(Platform p)` | A platform's phase, by its oldest well: "plateau, year 2 of 3", "−8.5% a year", "no well yet". |
| 950 | 3 | `static String declineWords()` | A platform well's decline past its plateau: "−8.5% a year" (Oil.PLATFORM_KEEPS_A_YEAR). |
| 954 | 5 | `static String phaseTip(Platform p)` |  |
| 961 | 15 | `public static List<Fact> seaFacts(View v)` | The platforms' card under its lines: the offshore pool, how the crude comes ashore, and what is on site. |
| 978 | 6 | `public static String noPlatformWords(View v)` | What the platforms' card says with no platform standing. |
| 986 | 6 | `public static List<String> drillingWords(View v)` | The wells' and the sea's investors' words, for the foot of the wells' box. |
| 994 | 3 | `public static String orderingWords(View v)` | The refiners' investors' word, for the foot of the units' box. |

### the units table (mockup 2's THE REFINERY) (lines 998-1156)

| line | len | member | says |
|---:|---:|---|---|
| 1015 | 2 | **type** `public record UnitLine(String name, String count, String what, String barrels, String running, String sprea...` | A row of the units table as it is written. |
| 1029 | 5 | `public static List<UnitLine> unitLines(View v)` | The units table's rows. |
| 1035 | 14 | `static UnitLine unitLine(View v, UnitRow u)` |  |
| 1055 | 17 | `public static String whatWords(Kind k)` | A kind's feed and yields, the litres largest first and coke last: "residue → diesel 35 · naphtha 15 · fuel oil 12 · gas 8 · coke 30"; each item's words joined by NO_BREAK, so a line wraps only between items. |
| 1074 | 4 | `static String barrelsFigure(double litresAMonth)` | A nameplate in barrels a day for the table, to three figures: "2,000", "99,900", past a million "206M". |
| 1082 | 3 | `private static String item(String product, long percent)` |  |
| 1087 | 19 | `static String[] stateOf(UnitRow u)` | The pill, its colour and its words: {state, colour, words}. |
| 1108 | 11 | `static String oneMoreWords(UnitRow u)` | One more of a kind standing, in a few words: what stops it, or what it would earn. |
| 1121 | 3 | `static String noCrudeWords(UnitRow u)` | A crude unit refused its feed: the petrol and diesel the city is short, and the crude its wells have spare - both under the gate. |
| 1126 | 30 | `static String unitTip(UnitRow u)` | Under the pointer, a row: its buildings, its run, its spread, and one more weighed by the planner with its reason whole. |

### the products table (mockup 2's PRODUCTS) (lines 1157-1256)

| line | len | member | says |
|---:|---:|---|---|
| 1164 | 3 | **type** `public record ProductLine(Good good, String colour, String name, String note, String price, String world, S...` | A row of the products table as it is written. |
| 1187 | 13 | `public static List<ProductLine> productLines(View v)` | The products table's rows: crude, then the nine. |
| 1202 | 3 | `static String figure(Good g, double units)` | A figure in the product's unit, one that rounds to nothing as "—": "2.00M", "830k t". |
| 1214 | 14 | `static String whereWords(Product p, boolean counted)` | Where it went: its takers here, largest first, at most RefineryView.LIST_MOST - "cars 1.20M · rail 40.0k and more" - then into the tanks and idled, each always named. |
| 1230 | 3 | `static boolean isKept(Taker t)` | Whether a taker is the tanks or the idled, not a buyer. |
| 1235 | 6 | `static String lower(String name)` | A taker's name in a list: "Cars" is "cars"; a sector's label kept as it is ("Business Services"). |
| 1242 | 14 | `static String productTip(Product p, boolean counted)` |  |

### the strategic reserve, and its two levers (lines 1257-1333)

| line | len | member | says |
|---:|---:|---|---|
| 1263 | 14 | `public static List<Fact> reserveFacts(View v)` | The reserve's lines: what stands and holds, its book, the fill ordered, the release, the month's trades and the last settlement. |
| 1283 | 3 | `public static String fillReading(Reserve r)` | The fill's reading. |
| 1288 | 7 | `public static String fillStatus(Reserve r)` | ...its status: the room and what the treasury could pay, or why it can take nothing. |
| 1297 | 3 | `public static String releaseReading(Reserve r)` | The release's reading. |
| 1302 | 4 | `public static String releaseStatus(Reserve r)` | ...its status. |
| 1308 | 3 | `public static double step(double most)` | A lever's step: its reach over LADDER_STEPS, or nothing with no reach. |
| 1313 | 1 | **type** `public enum Unit` | What an effect is written in. |
| 1316 | 1 | **type** `public record Effect(String label, double before, double after, Unit unit)` | One effect of a lever at a value: its words, before and after, and its unit. |
| 1319 | 6 | `public static List<Effect> fillEffects(Reserve r, double t)` | What a fill of `t` would do: the crude held, the room left, its cost at today's import price. |
| 1327 | 6 | `public static List<Effect> releaseEffects(Reserve r, double t)` | What a release of `t` a month would do: a month's release, what is held after it, a month's worth at today's price here. |


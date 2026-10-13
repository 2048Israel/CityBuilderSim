# IndustryPicture.java - 1,613 lines · 85 methods · 35 constants · model

`ham/citybuildersim/IndustryPicture.java` - generated 2026-10-13 by CodeMap; line numbers are as of that run.

> Each materials industry's month drawn as a picture, laid out to scale
> (0.7.143, batch I12; docs/studies/spec-materials.md 2.13, the study's
> mockups 1 to 5): the fields as vessels filled to what remains, or the feeds
> by where they came from, the imports hatched; each kind of its works with
> its run and the gate one more would fail; the tank of what it made under
> its industry's roof; who took it; the power its works made and sold - every
> ribbon one scale in the chain's own unit, and the strip of cells under it.
> 
> WHY. IndustryView (batch I11) holds each of the nine industries' month as
> figures, words and colours, and its ★1 left the layout to this batch: the
> refinery keeps its picture in the model (RefineryView's THE PICTURE) so a
> harness can hold its scale, and the materials pictures are the refinery
> pictogram made general (the study's 11). Worked out in the screen, a
> ribbon that did not foot to the tonnes beside it, a taker's bar taller than
> what it took, or a label run off the card's edge at 1,389 x 868 would be
> checked only by eye. Here the flow is traced first - each good's month
> split over the kinds that made and used it - and laid out second, and
> IndustryViewCheck holds both; ui/SectorScreen's OPERATIONS only paints
> them, with Refining's painter.
> 
> THE FLOW (trace()). Every quantity is IndustryView's: a product's made is
> split over the kinds that make it in proportion to what each made at its
> run (Unit.makes()), a feed's parts over the kinds that use it by what each
> used (Unit.uses()); what a works kept for its own (captive) runs from its
> makers to its users; what left the works runs into its band in the tank,
> and from the band - and from the warehouse, when the month sold more than
> it made - to whoever took it. A mine's ribbon from its field is what it
> made times its site's dig (the magnetite's three tonnes of rock a tonne,
> the copper field's copper). Three industries are drawn by their own rule:
> Coal Mining's coking coal is the wash plant's, worked back from the month's
> coking coal at CoalMining's yields, with the raw coal the underground mines
> send it; Quarrying's quarries on ANY dig the stone of the field the packing
> puts them on (Resource.goodOf()), and the limestone no buyer took runs
> into the aggregate's band (Quarrying, THE CARRY); Forestry's salvage cut
> comes from the cleared forest, half and half (Forestry.SAWLOG_SHARE).
> 
> THE UNIT. Ribbons are drawn in cubic metres where every good the industry
> draws is counted in them (Forestry), and in tonnes otherwise
> (Good.tonnesPerUnit()); a figure is written in its good's own unit.
> ★ Copper is drawn in tonnes of the good, not of copper contained: the model
> holds no copper content for a good (the concentrate's 30% is a comment).
> 
> Pure: it reads a View and builds nothing in the city.

**Uses:** [IndustryView](IndustryView.md) (89), [Good](Good.md) (82), [RefineryView](RefineryView.md) (59), [Sectors](Sectors.md) (14), [Resource](Resource.md) (13), [CoalMining](CoalMining.md) (12), [Deposit](Deposit.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [Trade](Trade.md) (6), [HeavyIndustry](HeavyIndustry.md) (5), [Game](Game.md) (5), [Forestry](Forestry.md) (3), [Quarrying](Quarrying.md) (1), [SpreadPlanner](SpreadPlanner.md) (1)

**Used by (2):** [IndustryViewCheck](IndustryViewCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 81 | THE FLOW, TRACED |
| 154 | · · the products: each split over its makers, and where each maker's part went |
| 187 | · · the fields: each kind's dig, from the field its ground is |
| 200 | · · the feeds: each part over its users, the lorries' diesel to the lorries |
| 231 | · · the takers: each band to whoever took it, and the warehouse's part |
| 412 | THE NODES: the sources, the works by rank, the tank, the takers |
| 454 | THE PICTURE (the study's 11: the oil mockups' frame at 1,389 x 868, |
| 623 | · · the columns |
| 697 | · · one scale: the largest at which every column fits |
| 714 | · · where each column stands |
| 748 | · · the ribbons, each from its node's right edge to the next's left |
| 791 | · · the headings and the notes |
| 824 | · · the sources: the fields' vessels and bars, the feeds' bars, their words |
| 872 | · · the works |
| 903 | · · the tank: its roof, its bands, the warehouse's block |
| 944 | · · the takers: their segments, and their labels spread apart |
| 1103 | · the colours |
| 1139 | · the roofs (the spec's 2.13, the mockups' roof()) |
| 1201 | THE WORDS |
| 1502 | THE STRIP: THE FIELDS AND THE PRODUCTS, A CELL EACH (O11's strip) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 558 | `IndustryPicture.Icon.FIELD` |  |
| 558 | `IndustryPicture.Icon.FOREST` |  |
| 558 | `IndustryPicture.Icon.SECTOR` |  |
| 558 | `IndustryPicture.Icon.HOUSEHOLDS` |  |
| 558 | `IndustryPicture.Icon.CITY` |  |
| 558 | `IndustryPicture.Icon.GLOBE` |  |
| 558 | `IndustryPicture.Icon.SHIP` |  |
| 558 | `IndustryPicture.Icon.OLD_METAL` |  |
| 558 | `IndustryPicture.Icon.WAREHOUSE` |  |
| 558 | `IndustryPicture.Icon.LOST` |  |
| 558 | `IndustryPicture.Icon.WORKS` |  |
| 558 | `IndustryPicture.Icon.LORRY` |  |
| 558 | `IndustryPicture.Icon.POWER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `IndustryPicture.SALVAGE` | `"salvage"` | ...the forest cleared for building, Forestry's salvage cut. |
| 94 | `IndustryPicture.LORRIES` | `"lorries"` | ...the lorries' diesel. |
| 104 | `IndustryPicture.ABROAD` | `"ABROAD", WAREHOUSE = "WAREHOUSE", LOST = "LOST"` | The takers that are not a party: the world, the warehouse, what was lost. |
| 116 | `IndustryPicture.LEAST_PART` | `.5` | The least part of a feed, a sale or the old metal the picture draws, in its good's unit: under half a unit its figure reads "0 t", and it is left out (I11's ★11: Iron & Steel buys a sliver of its own scrap). |
| 474 | `IndustryPicture.HEIGHT` | `420` | The picture's height on the page (the materials mockups' 420 px at 1,389 x 868). |
| 477 | `IndustryPicture.LEAST_WIDTH` | `RefineryView.LEAST_WIDTH` | The least width it is laid out at (RefineryView's: the 1,280 window's card is about 1,094). |
| 480 | `IndustryPicture.TOP` | `60` | Room above the columns for their headings and a line of notes (the mockups' 60). |
| 483 | `IndustryPicture.HEAD_Y` | `14, SUB_Y = 27, NOTE_Y = 46` | ...the headings' baselines, their lines' and the notes' (the mockups' 14, 27 and 46). |
| 486 | `IndustryPicture.PAD` | `4` | ...and the margin under and over what the columns hold. |
| 489 | `IndustryPicture.SOURCE_ZONE` | `214` | Where the sources' bar stands: its words, an icon and a field's vessel at its left (the mockups' 196 to 232). |
| 492 | `IndustryPicture.SOURCE_W` | `12` | The sources' bar (the mockups' 10 and 12). |
| 495 | `IndustryPicture.VESSEL_W` | `22, VESSEL_H = 40, VESSEL_GAP = 12` | A field's vessel, and its gap to the bar (the mockups' 22 x 40, 12). |
| 498 | `IndustryPicture.FIELD_ROOM` | `44` | The least room a field's row takes: its vessel and three lines of words (the mockups' 42). |
| 501 | `IndustryPicture.BUYER_ZONE` | `236` | The takers' room at the right: the bar, an icon, a name and its figure, a line of words (RefineryView's). |
| 504 | `IndustryPicture.BUYER_W` | `12` | The takers' bar. |
| 507 | `IndustryPicture.UNIT_W` | `60` | A works' box (the mockups' 54 to 64). |
| 510 | `IndustryPicture.TANK_W` | `150` | The tank's width: a product's name and its figure inside its band (the mockups' 136 to 156). |
| 513 | `IndustryPicture.TANK_TO_TAKERS` | `60` | The room between the tank and the takers' bar, for the ribbons to cross (the mockups' 44 to 60). |
| 516 | `IndustryPicture.UNIT_GAP` | `42` | The gap over a works' box: its three lines of words - its name, its run, one more in a word. |
| 519 | `IndustryPicture.FIRST_RANK_GAP` | `70, LAST_RANK_ROOM = 130` | ...the room between the sources' bar and the first rank of works, for the ribbons to turn; and the room the last rank's words have before the tank. |
| 522 | `IndustryPicture.CAPITAL` | `.74, LETTER =.6` | The share of its size a letter of the picture's words is set at, at most: a capital's and a lower-case letter's or a figure's (IBM Plex Sans's average is .55, measured at 1,389 x 868 by the batch's probe; Plex Mono's ... |
| 525 | `IndustryPicture.SOURCE_GAP` | `22, FIELD_GAP = 14, BUYER_GAP = 12, BLOCK_GAP = 22` | The gap between the sources, between the fields' rows, between the takers, and over the warehouse's block. |
| 528 | `IndustryPicture.BLOCK_WORDS` | `16` | The room under the warehouse's block for its line: its baseline 13 px under, and the line's descent. |
| 531 | `IndustryPicture.ROOF_ROOM` | `48` | The room over the tank for its roof: the furnace's stack and flame, the tallest. |
| 534 | `IndustryPicture.FOOT_ROOM` | `8` | The room under the tank for its foot (the mockups' 7), when no warehouse's block stands under it. |
| 537 | `IndustryPicture.SOURCE_SPACING` | `30, LABEL_SPACING = 31` | The least a source's or a taker's label centre stands from the next (the mockups' 30 and 31). |
| 540 | `IndustryPicture.WORDS_IN_BAND` | `12` | The least band a product's name and figure are written in (the mockups' 12). |
| 543 | `IndustryPicture.FIGURE_IN_BAND` | `52, FIGURE_ROOM = 56` | The room a product's figure has at the right of its band, and a taker's at the right of its name. |
| 546 | `IndustryPicture.PASS_GAP` | `3` | The gap over a band through a works' column (RefineryView's PASS_GAP). |
| 549 | `IndustryPicture.LEAST_NODE` | `1.5, ON_SITE_NODE = 6` | The least a node is drawn, so a trickle shows; and a kind only on site, drawn dashed with nothing through it. |
| 552 | `IndustryPicture.ICON_ROOM` | `22` | The least a box is drawn with an icon in it (the mockups' 22). |
| 555 | `IndustryPicture.RIBBON` | `.5, HATCHED =.95` | The opacity of a ribbon, and of an imported one, hatched (the mockups' .5 and .95). |
| 1507 | `IndustryPicture.STRIP_MOST` | `6` | The most cells the strip holds: six at about 200 px each at 1,389 x 868. |
| 1510 | `IndustryPicture.STRIP_HEAD` | `"THIS MONTH, CELL BY CELL"` | The strip's heading. |
| 1522 | `IndustryPicture.STRIP_WORDS` | `"a field's bar what is left of it · a product's: used here, shipped(pale), in...` | The strip's line: what its bars show. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 591 | `final String key` |  |
| 592 | `final double q, least, gap` |  |
| 593 | `double x, y, w, h, inAt, outAt, inQ, outQ` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1537 | **type** `public final class IndustryPicture` | Each materials industry's month drawn as a picture, laid out to scale (0.7.143, batch I12; docs/studies/spec-materials.md 2.13, the study's mockups 1 to 5): the fields as vessels filled to what remains, or the feeds b... |
| 79 | 1 | `private IndustryPicture()` |  |

### THE FLOW, TRACED (lines 81-411)

| line | len | member | says |
|---:|---:|---|---|
| 86 | 1 | `public static String fieldKey(Deposit.FieldType t)` | A node's key: a field by type, the salvage cut, a feed by its good and where it came from, a kind of works, the lorries, a product's band, what left the warehouse, a taker. |
| 90 | 1 | `public static String sourceKey(Good g, Origin o)` | ...a feed's part: "source:IRON:HOME", "source:SAWLOGS:WORLD", "source:STEEL:OLD_METAL" (Iron & Steel's old steel, not a good). |
| 92 | 1 | `public static String unitKey(Unit u)` | ...a kind of works by its permanent id. |
| 96 | 1 | `public static String bandKey(Good g)` | ...a product's band in the tank: what left the works. |
| 98 | 1 | `public static String storedKey(Good g)` | ...what was taken out of its warehouse. |
| 100 | 1 | `public static String passKey(int rank, Flow f)` | ...a flow's band through the r-th rank of works' column: "pass:1:unit:44>made:COKE>COKE". |
| 102 | 1 | `public static String takerKey(String who)` | ...a taker: a party in the city (a sector's key, Trade.HOUSEHOLDS, Trade.CITY), or ABROAD, WAREHOUSE, LOST. |
| 113 | 1 | **type** `public record Flow(String from, String to, Good good, double units, double drawn)` | Units of a good, or of a field's ground, a month from one node to another. |
| 119 | 10 | `public static String unitOf(View v)` | The unit a picture is drawn in: "m³" where every good the industry makes or uses is counted in cubic metres (Forestry), "t" otherwise. |
| 130 | 3 | `static boolean isCubic(Good g)` |  |
| 135 | 4 | `public static double drawn(View v, Good g, double units)` | Units of a good in the picture's unit: its own cubic metres in a picture in them, else its tonnes. |
| 141 | 4 | `public static double drawnGround(View v, Resource r, double amount)` | A field's ground in the picture's unit: a mineral field's tonnes, the forest's cubic metres - as tonnes, in a picture in tonnes, at the logs' weight. |
| 147 | 105 | `public static List<Flow> trace(View v)` | The month traced end to end (pure): every flow from a field, the salvage, a feed or a kind of works to a kind, a band or a taker; empty when there is no month to draw. |
| 254 | 7 | `public static List<Part> takers(Product p)` | A product's takers in drawing order: each buyer in the city, into the warehouse, lost, the world - a part's party WAREHOUSE or LOST for those two. |
| 269 | 44 | `static void coal(View v, List<Unit> built, Map<String, Flow> merged, Map<Unit, Map<Good, Double>> dug)` | Coal Mining's month (the spec's 2.10): the coking coal is the wash plants', washed = coking ÷ COKING_YIELD out of the raw coal the underground mines sent them, the middlings THERMAL_YIELD of it; the rest of the therma... |
| 315 | 5 | `public static double[] washOf(View v)` | The raw coal the month washed and the spoil it left, tonnes (Coal Mining's wash, as trace() works it): {washed, kept, spoil}; nothing for any other industry. |
| 322 | 11 | `static Vessel fieldOf(View v, Unit u, Good g)` | The field a kind dug a good from: its site's type, or for a kind on ANY the type whose ground is that good (Resource.goodOf()); the forest for Forestry's crews; null where the city holds none. |
| 335 | 20 | `static Map<Unit, Double> makers(View v, List<Unit> built, Good g)` | What each kind standing made of a good at its run (Unit.makes()); Quarrying's quarries on ANY by what they dig, whichever stone it is (Quarrying's rule). |
| 357 | 6 | `static Map<Unit, Double> users(List<Unit> built, Good g)` | What each kind standing used of a good at its run (Unit.uses()), or at nameplate where none ran. |
| 365 | 8 | `static Map<Unit, Double> oldMetalUsers(List<Unit> built, Good metal)` | The kinds that take the city's old steel or old copper, by what they take at their run. |
| 374 | 5 | `static Map<Unit, Double> weights(List<Unit> units, Good g)` |  |
| 380 | 5 | `static Map<Unit, Double> washWeights(List<Unit> plants)` |  |
| 387 | 8 | `static Map<Unit, Double> split(double total, Map<Unit, Double> weights)` | `total` split in proportion to the weights; empty where they add to nothing. |
| 396 | 3 | `static double clamp(double x, double most)` |  |
| 400 | 4 | `private static void add(Map<String, Flow> merged, View v, String from, String to, Good g, double units)` |  |
| 405 | 6 | `private static void merge(Map<String, Flow> merged, Flow f)` |  |

### THE NODES: the sources, the works by rank, the tank, the takers (lines 412-453)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 18 | `public static Map<Unit, Integer> ranks(View v)` | The ranks of the works, left to right: a kind that uses a good its own industry keeps for itself (Sector.captive()) stands right of the kinds that make it - coke ovens, then the blast furnace, then the oxygen shop; Co... |
| 443 | 10 | `static boolean feeds(View v, Unit a, Unit b)` | Whether kind `a` hands kind `b` what it makes: a good its industry keeps for its own works, or Coal Mining's raw coal. |

### THE PICTURE (the study's 11: the oil mockups' frame at 1,389 x 868, (lines 454-1102)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 1 | **type** `public enum Icon` | The icons the picture draws, by what they stand for; the screen picks each one's outline. |
| 561 | 1 | **type** `public record Mark(Icon icon, String key, double x, double y, double size, String colour)` | An icon, `size` px square from its top left; `key` a sector's key for SECTOR, the roof for WORKS, the resource for FIELD. |
| 571 | 17 | **type** `public record Picture(double width, double height, double scale, String unit, List<Ribbon> ribbons, List<Bo...` | The picture, in painting order: the ribbons, then the boxes over them, the outlines, lines, icons and words; and its nodes by name (the flow's keys). |
| 573 | 1 | `public boolean empty()` _(in IndustryPicture.Picture)_ |  |
| 576 | 4 | `public Place place(String key)` _(in IndustryPicture.Picture)_ | A node by its name, or null. |
| 582 | 5 | `public List<String> texts()` _(in IndustryPicture.Picture)_ | Every word it writes, a line each, in painting order. |
| 590 | 12 | **type** `private static final class Node` | A node while the picture is laid out: its key, what passes through it, its least height, the gap over it, and where it stands. |
| 594 | 6 | `Node(String key, double q, double least, double gap)` _(in IndustryPicture.Node)_ |  |
| 600 | 1 | `double right()` _(in IndustryPicture.Node)_ |  |
| 604 | 365 | `public static Picture picture(Game game, View v, double width, double height)` | The picture of `v` at a width and a height (pure); empty when there is no month to draw. |
| 971 | 7 | `public static double widthOf(Words w)` | The most a line of words is set wide, px: each capital at CAPITAL of its size, every other letter at LETTER - an upper bound on IBM Plex at the picture's sizes. |
| 980 | 11 | `private static double[] wordsOver(Node n, Unit u, View v)` | The words over a works' box, as {left, top, right, bottom}: its name and its lines, as wide as the widest (widthOf()); null for a band through the column, which has none. |
| 1000 | 33 | `private static void apart(List<List<Node>> ranks, Map<String, Unit> unitOfKey, View v, double top, double bottom)` | Each rank of works moved up or down, whole, until no works' words run into the next rank's boxes or words (a rank's words reach right, past its boxes, toward the tank): the least move that clears them and keeps the ra... |
| 1035 | 11 | `private static double fit(List<Node> column, double room, double k)` | The scale at which a column fits `room`, its nodes under their least at the last scale `k` held at their least (the mockups' three passes). |
| 1047 | 3 | `private static void size(List<Node> column, double k)` |  |
| 1052 | 14 | `private static void stack(List<Node> column, double x, double w, double mid)` | A column's nodes stacked from the top, centred on `mid`. |
| 1067 | 4 | `private static boolean has(List<Node> nodes, String key)` |  |
| 1072 | 4 | `private static double in(Map<String, double[]> sides, String key)` |  |
| 1077 | 4 | `private static double out(Map<String, double[]> sides, String key)` |  |
| 1082 | 5 | `private static double totalOut(Map<String, double[]> sides, List<Node> nodes)` |  |
| 1089 | 13 | `static int takerRank(String key)` | A taker's place: the city's parties in Sectors.KEYS' order, the households and the city, then the warehouse, the lost and the world. |

### the colours (lines 1103-1138)

| line | len | member | says |
|---:|---:|---|---|
| 1106 | 9 | `static String colourFrom(View v, Flow f)` | A ribbon's colour where it leaves: a field's its ground's, the raw coal the coking field's, the old steel the old metal's, a good its own. |
| 1117 | 4 | `static String colourTo(View v, Flow f)` | ...and where it arrives: the band's good (the limestone carried into aggregate turns its colour), else the colour it left with. |
| 1122 | 8 | `static String sourceColour(View v, String key)` |  |
| 1132 | 6 | `static boolean dark(Good g)` | Whether a band's words are written light: the dark goods. |

### the roofs (the spec's 2.13, the mockups' roof()) (lines 1139-1200)

| line | len | member | says |
|---:|---:|---|---|
| 1142 | 42 | `static List<Outline> roof(IndustryView.Roof r, double x, double y, double w)` | The roof over the tank, by the industry: a pit's headframe, the crews' logs, a furnace's stack and flame, a kiln's silo, a mill's saw-tooth. |
| 1186 | 8 | `static String path(Object...parts)` | An outline's path: commands and coordinates, two decimals (RefineryView's). |
| 1196 | 4 | `static String circle(double cx, double cy, double r)` | A circle as four quadratic curves. |

### THE WORDS (lines 1201-1501)

| line | len | member | says |
|---:|---:|---|---|
| 1206 | 11 | `public static String headWords(View v)` | The card's heading, by the industry (the mockups'): "THE MINES THIS MONTH", "THE STEELWORKS THIS MONTH". |
| 1219 | 5 | `public static String subWords(View v)` | ...and its line: where the picture runs from and its unit. |
| 1226 | 3 | `public static String scaleWords(Picture p)` | The scale, for the heading's right: "10 px of ribbon = 1,234 t". |
| 1231 | 8 | `public static String emptyWords(View v)` | What the card says when there is no month to draw: nothing standing, or a month not yet run since the city was loaded. |
| 1241 | 8 | `static String worksHead(View v)` | The works' heading: the mines', the quarries', the crews', or the works'. |
| 1251 | 4 | `static String tankHead(View v)` | The tank's heading: what the mines dug, what the crews cut, what the works made. |
| 1257 | 16 | `public static String noteWords(View v)` | The line of notes over the works: Coal Mining's wash, Quarrying's carry, the forest's cut, the old metal's month. |
| 1275 | 3 | `public static String figureOf(String unit, double q)` | A quantity in a picture's unit, short: "1,234 t", "830k m³". |
| 1280 | 6 | `static String vesselLine(View v, Vessel f)` | A vessel's line under its name: its sites worked, or the forest the crews may cut. |
| 1288 | 4 | `static String vesselFigure(Vessel f)` | ...and its figure: what remains of it and the share. |
| 1294 | 6 | `static String sourceName(View v, String key)` | A source's name: the good, or the city's old steel. |
| 1302 | 12 | `static String sourceLine(Game game, View v, String key)` | ...its line: who sold it - the first seller here and how many more ("Coal Mining +1") - imported, the city's old metal, the forest cleared for building. |
| 1316 | 12 | `static String sourceTip(Game game, View v, String key)` | Under the pointer, a source: its name, every seller here with what each sold, or where it came from, and its figure. |
| 1330 | 12 | `static String sourceFigure(View v, String key)` | ...and its figure, in its good's own unit. |
| 1343 | 6 | `static Icon sourceIcon(String key)` |  |
| 1351 | 6 | `static String sellerKey(View v, String key)` | The first seller of a source bought here, for its icon; null for any other. |
| 1359 | 4 | `public static String unitName(Unit u)` | A kind's name over its box: "Coke Ovens ×2"; a kind only on site its name alone. |
| 1365 | 9 | `public static String runLine(Unit u)` | ...its run: "runs 87%", "idle", "worked out", "grows 500 m³ a month", "2 on site" - and those on site beside it, "+1 on site". |
| 1376 | 5 | `public static String gateLine(Unit u)` | ...and one more in a word: "one more pays 1.20% a month", or the first gate it fails - "one more: the money"; "" for a kind the plan does not weigh. |
| 1383 | 3 | `static String lorriesLine(View v)` | The lorries' line: the diesel they burned. |
| 1388 | 3 | `static boolean idle(Unit u)` | Whether a kind standing ran at nothing. |
| 1393 | 8 | `public static String unitTip(Unit u)` | Under the pointer, a kind: its line, what the last one earns, and one more in the plan's words. |
| 1403 | 11 | `public static String bandName(Good g)` | A band's name in the tank: the good's, short where the label would not fit. |
| 1416 | 13 | `static String productTip(View v, Product p)` | Under the pointer, a product's band: made, kept, sold here, shipped, into or out of its warehouse. |
| 1431 | 10 | `static String takerName(Game game, View v, String who)` | A taker's name: a sector's label, "Households", "The city", "Abroad", "Into the warehouse", "Lost". |
| 1442 | 10 | `static Icon takerIcon(String who)` |  |
| 1454 | 13 | `static Map<Good, Double> took(List<Flow> flows, String key)` | What a taker took, by good, in the goods' own units, largest drawn first. |
| 1469 | 10 | `static String takerFigure(View v, List<Flow> flows, String key)` | A taker's figure: its one good in its own unit, or all it took in the picture's. |
| 1481 | 8 | `static String takerLine(View v, List<Flow> flows, String key)` | ...its line: what it took, largest first, at most three - "pig iron, scrap"; the warehouse's and the lost's what they are. |
| 1491 | 10 | `static String takerTip(Game game, View v, List<Flow> flows, String key)` | Under the pointer, a taker: each good it took. |

### THE STRIP: THE FIELDS AND THE PRODUCTS, A CELL EACH (O11's strip) (lines 1502-1613)

| line | len | member | says |
|---:|---:|---|---|
| 1513 | 1 | **type** `public record Segment(double share, String colour, double opacity, boolean hatched)` | A part of a cell's bar: its share, its colour, and whether it is pale or hatched. |
| 1516 | 1 | **type** `public record Row(String label, String figure, boolean hot)` | A line of a cell: its words, its figure, and whether the figure is a warning's colour. |
| 1519 | 1 | **type** `public record Cell(String name, String colour, List<Segment> bar, List<Row> rows)` | One cell under the picture: a field, the forest, a product, the old metal, the power. |
| 1525 | 74 | `public static List<Cell> strip(View v)` | The strip's cells (pure): the fields or the forest first, then the products by tonnes, then the old metal and the power - at most STRIP_MOST. |
| 1601 | 4 | `public static String fieldName(Vessel f)` | A field type's cell's name: "Hematite fields", "Coking fields"; the forest "Forest" - apart from a product's cell of the same good ("Limestone"). |
| 1607 | 6 | `public static String lastsWords(double remaining, double dugAMonth)` | How long a field lasts at this month's dig: "38 years", "11 months", "—" with nothing dug. |


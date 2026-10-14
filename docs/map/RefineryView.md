# RefineryView.java - 1,602 lines · 82 methods · 43 constants · model

`ham/citybuildersim/RefineryView.java` - generated 2026-10-14 by CodeMap; line numbers are as of that run.

> The refinery's month as a picture (0.7.95, batch O11;
> docs/studies/spec-oil.md 2.12): the crude it ran and where that came from,
> the column's cuts, each conversion unit's run, spread and gate, the products
> and who took them - home, abroad, into the tanks - with the imports beside
> them, and the residue burned; and the pictogram those figures draw, laid out
> to scale.
> 
> WHY. Refining's Operations page drew the refinery as every business is
> drawn (SectorFlow): crude in, nine products out, a ring of the rate. Since
> 0.7.80 a refinery is a campus - crude units cutting the crude, conversion
> units turning the cheap cuts into dear products (RefineryFlow) - and none
> of that showed: not the cuts, not which unit made what, not why a unit
> idles, not the residue burned for want of diesel, not who bought the
> petrol. Jerus's idea, drawn as the research's mockup 1
> (claude/oil-and-ports-research.md 6): crude in on the left, the column
> filled with its cuts, the units, the tank filled by product, the buyers on
> the right, every ribbon to scale. These are its figures and its layout,
> held by a harness (RefineryViewCheck) rather than worked out in the
> screen: the ribbons foot to the flow's litres, the products to the
> production rows, the buyers to the refinery's sales; the screen
> (ui/SectorScreen, OPERATIONS · THE REFINERY) only paints them.
> 
> WHICH MONTH. The flow the month's products were made on, its mix and its
> rate (Refining.monthsFlow(), THE MONTH AS IT RAN), so each product's run
> less what idled is the production row's made, to the bit. The takers are
> the buyers' own rows (Sector.inputRow()): every sector's purchase of a
> product since the month's strike - the forecourts' petrol (Retail), the
> vans' diesel, the lubricants, the builders' bitumen - and the railway's
> diesel, drawn at the top of the month before the strike clears its row,
> from its own record of the haul (Rail.getFuelLitres()). The crude is the
> month's run, credited to where the month's crude was bought (crude's
> clearing, GoodsMarket.getTrades()). After a load nothing is counted until
> a month runs: the rows are not saved (SectorFlow's rule), and nor is the
> month's flow.
> 
> Pure: it reads the city and changes nothing (the planner's appraisals of
> one more unit are the build card's reads).

**Uses:** [Good](Good.md) (69), [Refining](Refining.md) (17), [RefineryFlow](RefineryFlow.md) (9), [SpreadPlanner](SpreadPlanner.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (6), [Sector](Sector.md) (4), [Deposit](Deposit.md) (4), [Rail](Rail.md) (3), [Game](Game.md) (2), [Sectors](Sectors.md) (2), [GoodsMarket](GoodsMarket.md) (2), [Trade](Trade.md) (2), [Formats](Formats.md) (2), [BuildingManager](BuildingManager.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Oil](Oil.md) (1), [Retail](Retail.md) (1)

**Used by (8):** [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 62 | THE FIGURES |
| 269 | · · the units, and one more of each kind weighed |
| 287 | · · the crude: where it was bought, the run credited to it |
| 314 | · · who took the products, off their own rows |
| 341 | · · each product's month |
| 365 | · · the buyers, in BuyerKind's order |
| 442 | THE FLOW, TRACED: which cut went through which unit into which product |
| 526 | THE PICTURE (mockup 1, 2.12's "ribbons to scale") |
| 599 | · the colours (mockup 1's, on the card's raised ground) |
| 806 | · · what each column holds |
| 855 | · · the sources, and the crude into the column |
| 876 | · · the column |
| 886 | · · the units' columns: each unit, each band through them, the furnaces |
| 911 | · · the tank: made, out of the tanks, imported |
| 950 | · · the takers |
| 961 | · · the ribbons through the refinery: each link a hop, or two through its band |
| 1004 | · · the tank's takers: each product's band to its takers, out of the tanks and landed, each taker's own |
| 1043 | · · the nodes over the ribbons |
| 1272 | THE WORDS |
| 1548 | THE STRIP: THIS MONTH, BY PRODUCT |

## Enum constants

| line | constant | says |
|---:|---|---|
| 83 | `RefineryView.SourceKind.LAND_WELLS` |  |
| 83 | `RefineryView.SourceKind.PLATFORMS` |  |
| 83 | `RefineryView.SourceKind.RESERVE` |  |
| 83 | `RefineryView.SourceKind.IMPORTED` |  |
| 83 | `RefineryView.SourceKind.TANK_FARM` |  |
| 121 | `RefineryView.BuyerKind.CARS` |  |
| 121 | `RefineryView.BuyerKind.VANS` |  |
| 121 | `RefineryView.BuyerKind.RAIL` |  |
| 121 | `RefineryView.BuyerKind.FACTORIES` |  |
| 121 | `RefineryView.BuyerKind.ROADS` |  |
| 121 | `RefineryView.BuyerKind.SECTOR` |  |
| 121 | `RefineryView.BuyerKind.TANKS` |  |
| 121 | `RefineryView.BuyerKind.IDLED` |  |
| 121 | `RefineryView.BuyerKind.ABROAD` |  |
| 664 | `RefineryView.Face.SANS` |  |
| 664 | `RefineryView.Face.SANS_MEDIUM` |  |
| 664 | `RefineryView.Face.SANS_STRONG` |  |
| 664 | `RefineryView.Face.MONO` |  |
| 664 | `RefineryView.Face.MONO_STRONG` |  |
| 667 | `RefineryView.Align.START` |  |
| 667 | `RefineryView.Align.MIDDLE` |  |
| 667 | `RefineryView.Align.END` |  |
| 670 | `RefineryView.Icon.WELL` |  |
| 670 | `RefineryView.Icon.PLATFORM` |  |
| 670 | `RefineryView.Icon.RESERVE` |  |
| 670 | `RefineryView.Icon.TANKER` |  |
| 670 | `RefineryView.Icon.TANK` |  |
| 670 | `RefineryView.Icon.CAR` |  |
| 670 | `RefineryView.Icon.LORRY` |  |
| 670 | `RefineryView.Icon.TRAIN` |  |
| 670 | `RefineryView.Icon.FACTORY` |  |
| 670 | `RefineryView.Icon.ROAD` |  |
| 670 | `RefineryView.Icon.SECTOR` |  |
| 670 | `RefineryView.Icon.GLOBE` |  |
| 670 | `RefineryView.Icon.IDLE` |  |
| 670 | `RefineryView.Icon.UNIT` |  |
| 670 | `RefineryView.Icon.FLAME` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `RefineryView.TANK_ORDER` | `{ Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS...` | The products in the tank's order, top to bottom: light to heavy, as the column's cuts boil (mockup 1), and the coker's coke last. |
| 71 | `RefineryView.COLUMN` | `{ Stream.GAS, Stream.LIGHT_NAPHTHA, Stream.HEAVY_NAPHTHA, Stream.KEROSENE, St...` | The straight-run cuts the column holds, top to bottom: the slate's CUT_ order; the heavy crude's residue is drawn as the residue's. |
| 75 | `RefineryView.LITRES_A_TONNE_DRAWN` | `RefineryFlow.RESIDUE_LITRES_PER_TONNE` | Litres a tonne of bitumen and coke are drawn at: the residue's, RefineryFlow.RESIDUE_LITRES_PER_TONNE - the weight the flow strikes them at, so a unit's ribbons balance to the litre. |
| 175 | `RefineryView.End.FURNACES` | `new End(null, null, null)` | The furnaces, where the residue no unit takes is burned: an end with no cut, unit or product. |
| 546 | `RefineryView.HEIGHT` | `404` | The picture's height on the page (mockup 1's 404 px at 1,389 x 868). |
| 549 | `RefineryView.LEAST_WIDTH` | `1000` | The least width it is laid out at: a second rank's line of words fits before the tank (the 1,280 window's card is about 1,094). |
| 552 | `RefineryView.TOP` | `64` | Room above the columns for their headings: three lines over the column. |
| 555 | `RefineryView.PAD` | `4` | ...and the margin under and over what the columns hold. |
| 558 | `RefineryView.SOURCE_ZONE` | `182` | The sources' room at the left: an icon, three lines of words and the bar at its right edge. |
| 561 | `RefineryView.SOURCE_W` | `10` | The sources' bar. |
| 564 | `RefineryView.BUYER_ZONE` | `236` | The takers' room at the right: the bar, an icon, a name and its figure, a line of words. |
| 567 | `RefineryView.BUYER_W` | `12` | The takers' bar. |
| 570 | `RefineryView.COLUMN_W` | `82` | The column's width (mockup 1's 82). |
| 573 | `RefineryView.UNIT_W` | `56` | A unit's box (mockup 1's 66, narrower for the second rank beside the first). |
| 576 | `RefineryView.TANK_W` | `140` | The tank's width: a product's name and its figure inside its band, and a block's line under it. |
| 579 | `RefineryView.COLUMN_AT` | `.10, FIRST_RANK_AT =.29, SECOND_RANK_AT =.42, TANK_AT =.69` | Where the column, each rank of units and the tank stand, as a share of the room between the sources' bar and the takers': a rank's line of words fits between its box and the tank. |
| 582 | `RefineryView.UNIT_GAP` | `18` | The gap over a unit's box: its line of words (mockup 1's 18). |
| 585 | `RefineryView.PASS_GAP` | `3` | The gap between bands that run through the units' columns, and between a column's bands of different ends. |
| 588 | `RefineryView.SOURCE_GAP` | `26, BUYER_GAP = 12, BLOCK_GAP = 22` | The gap between the sources, between the takers, and between the tank's blocks (made, from the tanks, imported). |
| 591 | `RefineryView.LABEL_SPACING` | `33` | The least a taker's label centre stands from the next (mockup 1's 33). |
| 594 | `RefineryView.WORDS_IN_BAND` | `11` | The least band a word is written in: a cut's name in the column, a product's in the tank. |
| 597 | `RefineryView.LEAST_NODE` | `1.5` | The least a node is drawn, so a trickle shows. |
| 602 | `RefineryView.GROUND` | `"#152130"` | The ground the picture sits on: the card's (ui/Palette.RAISED), and a word's halo. |
| 604 | `RefineryView.CAP` | `"#101924"` | A roof, a cap, a dome. |
| 606 | `RefineryView.FRAME` | `"#3a4f6a"` | Their edges, and the column's and the tank's frame. |
| 608 | `RefineryView.CRUDE` | `"#a07d4a"` | Crude, in a ribbon and a source's bar. |
| 610 | `RefineryView.UNIT_FILL` | `"#1b2a3d", UNIT_EDGE = "#4a6283"` | A unit's box and its edge. |
| 612 | `RefineryView.FURNACE_FILL` | `"#2a1f1a", FURNACE_EDGE = "#6b4a3a", FLARE = "#ffb454"` | The furnaces' box and its edge, and the flare. |
| 614 | `RefineryView.TEXT` | `"#e6edf3", TEXT_2 = "#a9b8c9", TEXT_3 = "#8496ab"` | The words: the head's, the body's, the faint. |
| 616 | `RefineryView.HOT` | `"#e3b341"` | A word that something is imported. |
| 618 | `RefineryView.ON_BAND` | `"#0b1118"` | The words on a pale band. |
| 1210 | `RefineryView.AFTER_UNIT_GAP` | `6` | The gap under a unit's box before a band runs on. |
| 1213 | `RefineryView.BLOCK_WORDS` | `16` | The room under each of the tank's blocks for its line ("IMPORTED" and its litres): its baseline 13 px under, and the line's descent. |
| 1216 | `RefineryView.IMPORT_EDGE` | `"#5a6d84"` | The edge of the tank's imported block (mockup 1's). |
| 1219 | `RefineryView.FURNACE_WORDS` | `"burned %s · no diesel to cut it"` | The furnaces' line under their name. |
| 1222 | `RefineryView.NO_UNITS` | `"NO CONVERSION UNIT · EACH CUT SOLD AS IT IS"` | The units' heading when none stands. |
| 1225 | `RefineryView.FIGURE_IN_BAND` | `46` | The room a product's figure has at the right of its band in the tank. |
| 1228 | `RefineryView.BLOCK_FIGURE` | `52` | ...and a block's litres at the right of its line under it. |
| 1231 | `RefineryView.FIGURE_ROOM` | `58` | The room a figure has at the right of a band or a taker's name. |
| 1509 | `RefineryView.LIST_MOST` | `3` | The most products a taker's line names before "and more". |
| 1561 | `RefineryView.STRIP_HEAD` | `"THIS MONTH, BY PRODUCT"` | The strip's heading words. |
| 1564 | `RefineryView.STRIP_WORDS` | `"litres(M a million, k a thousand), bitumen and coke in tonnes · bar: taken h...` | ...and its line. |
| 1582 | `RefineryView.CELL_LINES` | `{ "made here", "imported", "exported", "tanks", "price here" }` | The strip's line names, in a cell's order. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 764 | `final double x, w` |  |
| 765 | `double y, h, inAt, outAt` |  |
| 1188 | `final Node from, to` |  |
| 1189 | `final double h` |  |
| 1190 | `final String c0, c1` |  |
| 1191 | `double a0, b0` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1545 | **type** `public final class RefineryView` | The refinery's month as a picture (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): the crude it ran and where that came from, the column's cuts, each conversion unit's run, spread and gate, the products and who too... |
| 60 | 1 | `private RefineryView()` |  |

### THE FIGURES (lines 62-441)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 3 | `public static double litresOf(Good g, double units)` | A product's units as litres for the picture: a litre good's own, a tonne of bitumen or coke at LITRES_A_TONNE_DRAWN. |
| 83 | 1 | **type** `public enum SourceKind` | Where a part of the month's crude came from. |
| 95 | 4 | **type** `public record Source(SourceKind kind, int count, double bought, double tonnes, double[] grades)` | A part of the month's run, by where it was bought. |
| 97 | 1 | `public double litres()` _(in RefineryView.Source)_ | The run's litres credited to it. |
| 114 | 5 | **type** `public record Unit(Kind kind, int standing, int onSite, double feed, double run, double share, double sprea...` | One kind of conversion unit, all its buildings together. |
| 117 | 1 | `public boolean idle()` _(in RefineryView.Unit)_ | Whether it stood and took nothing: no feed spare, or a spread of nothing or less - or, since 0.7.105, a month the crude units ran nothing (Refining's THE RUN), which the picture does not draw. |
| 121 | 1 | **type** `public enum BuyerKind` | Who took a product. |
| 132 | 16 | **type** `public record Buyer(BuyerKind kind, String sector, Map<Good, Double> local, Map<Good, Double> imported)` | A taker of the month's products. |
| 134 | 1 | `public double of(Good g)` _(in RefineryView.Buyer)_ | Units of one product it took, home and abroad. |
| 136 | 5 | `public double litres()` _(in RefineryView.Buyer)_ | ...and all it took, as the picture's litres (litresOf()). |
| 142 | 5 | `public double importedLitres()` _(in RefineryView.Buyer)_ | ...of it imported, as litres. |
| 162 | 9 | **type** `public record Product(Good good, double run, double made, double idled, double local, double imported, doub...` | One product's month. |
| 165 | 1 | `public double tanks()` _(in RefineryView.Product)_ | Into the refiners' tanks (above nothing) or out of them (below): made less what was taken from them. |
| 166 | 1 | `public double intoTanks()` _(in RefineryView.Product)_ |  |
| 167 | 1 | `public double fromTanks()` _(in RefineryView.Product)_ |  |
| 169 | 1 | `public double used()` _(in RefineryView.Product)_ | Everything the city took of it, home-made and landed. |
| 173 | 8 | **type** `public record End(Stream cut, Kind unit, Good product)` | One end of a traced link: a straight-run cut, a kind of unit, a product, or the furnaces. |
| 176 | 1 | `public static End cut(Stream s)` _(in RefineryView.End)_ |  |
| 177 | 1 | `public static End unit(Kind k)` _(in RefineryView.End)_ |  |
| 178 | 1 | `public static End product(Good g)` _(in RefineryView.End)_ |  |
| 179 | 1 | `public boolean furnaces()` _(in RefineryView.End)_ |  |
| 183 | 1 | **type** `public record Link(End from, End to, double litres)` | Litres a month from one end to another, at the month's rate (bitumen and coke at LITRES_A_TONNE_DRAWN). |
| 204 | 36 | **type** `public record View(boolean standing, boolean counted, int crudeUnits, double rate, RefineryFlow.Flow flow, ...` | The month. |
| 209 | 1 | `public double runLitres()` _(in RefineryView.View)_ | The crude the month ran, litres. |
| 212 | 1 | `public double runTonnes()` _(in RefineryView.View)_ | ...and tonnes. |
| 215 | 6 | `public double cut(Stream s)` _(in RefineryView.View)_ | A straight-run cut of the month's run, litres - the residue with the heavy crude's. |
| 223 | 4 | `public Product product(Good g)` _(in RefineryView.View)_ | One product's month, or null for one it neither made nor took. |
| 229 | 4 | `public Unit unit(Kind k)` _(in RefineryView.View)_ | One kind of unit, or null for a kind with nothing standing or on site. |
| 235 | 1 | `public boolean drawn()` _(in RefineryView.View)_ | Whether there is a month to draw: a crude unit standing, counted, that ran something. |
| 238 | 1 | `public Picture picture(double width, double height)` _(in RefineryView.View)_ | The picture at a width and a height (THE PICTURE). |
| 242 | 149 | `public static View of(Game game)` | The refinery's month in `game` (pure). |
| 392 | 3 | `private static void addSource(List<Source> to, SourceKind kind, int count, double bought, double tonnes, double[] grades)` |  |
| 402 | 12 | `static double[] wellsGrades(double[] purchases, double local, double imported)` | The grades of the crude bought at home this month (pure): what the month's purchases' mix (Refining.monthsMix() of them, getCrudeMix() at the month's end) holds once the imports' MEDIUM is taken out; MEDIUM with none ... |
| 416 | 7 | `static BuyerKind kindOf(Sector s, Good g)` | What a sector's purchase of a product is, as a taker: the forecourts' petrol the cars', diesel the vans' (the railway's its own), lubricants the factories', bitumen the roads'. |
| 429 | 12 | `static SpreadPlanner.Candidate oneMore(Refining r, Game game, List<BuildingsTemplate> sizes)` | One more of a kind, weighed by the planner (Refining.appraise(), the build card's read): of its sizes, the one that passes every gate and earns most on its cost, or - with none passing - the smallest's. |

### THE FLOW, TRACED: which cut went through which unit into which product (lines 442-525)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 60 | `public static List<Link> trace(RefineryFlow.Flow f, double rate)` | The flow `f` (at nameplate) traced end to end, each link's litres at `rate` (pure). |
| 521 | 4 | `private static void link(Map<String, Link> links, End from, End to, double litres)` |  |

### THE PICTURE (mockup 1, 2.12's "ribbons to scale") (lines 526-598)

### the colours (mockup 1's, on the card's raised ground) (lines 599-1271)

| line | len | member | says |
|---:|---:|---|---|
| 621 | 14 | `public static String colour(Good g)` | A product's colour (mockup 1's; coke the coal's grey). |
| 637 | 11 | `public static String colour(Stream s)` | A cut's colour (mockup 1's). |
| 650 | 12 | `public static String cutName(Stream s)` | A cut's name in the column (mockup 1's). |
| 664 | 1 | **type** `public enum Face` | The faces a word is set in: Plex Sans, at its weights, and Plex Mono for a figure. |
| 667 | 1 | **type** `public enum Align` | Where a word stands on its x: its start, its middle or its end. |
| 670 | 1 | **type** `public enum Icon` | The icons the picture draws, by what they stand for; the screen picks each one's outline. |
| 684 | 2 | **type** `public record Box(double x, double y, double w, double h, String fill, double opacity, String stroke, boole...` | A box: a source's bar, a cut, a unit, a band through the units, the furnaces, a product's band, a taker's segment. |
| 695 | 4 | **type** `public record Ribbon(double x1, double a0, double a1, double x2, double b0, double b1, String from, String ...` | A ribbon from (x1, a0..a1) to (x2, b0..b1), its two edges each a curve with its handles at the middle x (mockup 1's band()). |
| 697 | 1 | `public double height()` _(in RefineryView.Ribbon)_ |  |
| 701 | 1 | **type** `public record Outline(String path, String fill, double opacity, String stroke)` | An outline in SVG's absolute M, L, Q and Z, filled and edged: the column's dome, the tank's roof, the stack, the flare. |
| 704 | 1 | **type** `public record Line(double x1, double y1, double x2, double y2, String colour)` | A straight line: a leader from a taker's bar to its moved label, a band's divider. |
| 715 | 15 | **type** `public record Words(String text, double x, double y, Face face, double size, String colour, Align align, do...` | A word, with its baseline at y. |
| 717 | 4 | `public Words(String text, double x, double y, Face face, double size, String colour, Align align, double slot, boolean halo)` _(in RefineryView.Words)_ |  |
| 723 | 3 | `public static Words run(String text, Face face, double size, String colour)` _(in RefineryView.Words)_ | A run to follow a word on its line. |
| 728 | 1 | `public String line()` _(in RefineryView.Words)_ | The whole line's text, both runs. |
| 732 | 1 | **type** `public record Mark(Icon icon, String sector, double x, double y, double size, String colour)` | An icon, `size` px square from its top left. |
| 741 | 3 | **type** `public record Place(String key, double x, double y, double w, double h)` | A node of the picture by name, where it stands - for a check to attach each ribbon to its two ends: "source:IMPORTED", "cut:GAS_OIL", "unit:REFORMER", "band:<from>><to>" (a cut through the units' columns), "furnaces",... |
| 742 | 1 | `public double right()` _(in RefineryView.Place)_ |  |
| 751 | 10 | **type** `public record Picture(double width, double height, double scale, List<Ribbon> ribbons, List<Box> boxes, Lis...` | The picture, in painting order: the ribbons, then the boxes over them, the outlines, lines, icons and words; and its nodes by name. |
| 753 | 1 | `public boolean empty()` _(in RefineryView.Picture)_ |  |
| 756 | 4 | `public Place place(String key)` _(in RefineryView.Picture)_ | A node by its name, or null. |
| 763 | 13 | **type** `private static final class Node` | A node of the picture while it is laid out: where it stands, and where its ribbons have reached on each side. |
| 766 | 8 | `Node(double x, double y, double w, double h)` _(in RefineryView.Node)_ |  |
| 774 | 1 | `double right()` _(in RefineryView.Node)_ |  |
| 778 | 1 | **type** `private record Slot(Kind unit, Link pass, boolean secondRank)` | One slot in the units' columns: a kind of unit, a band from a cut to a product or the furnaces. |
| 781 | 3 | `static boolean firstRank(Kind k)` | The units fed from the column's gas oil and residue, the first rank; the rest are the second (the pools other units feed). |
| 786 | 3 | `static Stream besideCut(Kind k)` | The column cut a kind's slot is drawn beside: its feed's, alkylation's the gas oil's (its cracked gas comes from the cracking units). |
| 790 | 395 | `static Picture picture(View v, double width, double height)` |  |
| 1187 | 13 | **type** `private static final class Hop` | One ribbon while it is laid out: its two nodes, its height, its colours, and where it leaves and arrives. |
| 1192 | 7 | `Hop(Node from, Node to, double h, String c0, String c1)` _(in RefineryView.Hop)_ |  |
| 1202 | 6 | `private static double gapBefore(List<Slot> slots, int i)` | The gap over slot `i` in the units' columns: a unit's two lines of words; a band after a unit a little; a band after a band PASS_GAP. |
| 1233 | 4 | `private static double side(Map<Kind, double[]> sides, Kind k)` |  |
| 1238 | 4 | `private static Node furnacesOf(Map<Link, Node> passOf)` |  |
| 1244 | 5 | `private static double arrivalY(Link l, java.util.function.Function<End, Node> target, Map<Link, Node> passOf)` | Where a link arrives, for the order its source sends it in: the band it runs through, or its end. |
| 1251 | 5 | `private static double departureY(Link l, java.util.function.Function<End, Node> source, Map<Link, Node> passOf)` | Where a link leaves, for the order its end takes it in: the band it ran through, or its source. |
| 1258 | 13 | `static void spread(double[] at, double least, double top, double bottom)` | The takers' labels spread apart: each pair closer than `least` pushed apart evenly, held between `top` and `bottom` (mockup 1's sixty rounds). |

### THE WORDS (lines 1272-1547)

| line | len | member | says |
|---:|---:|---|---|
| 1277 | 3 | `public static String litres(double l)` | Litres in three or four figures: "9.68M L", "830k L", "640 L", "23.0B L" - never more than eight characters short of a sign. |
| 1282 | 3 | `public static String tonnes(double t)` | Tonnes, whole: "8,306 t". |
| 1287 | 9 | `static String compact(double v)` | A quantity in three or four figures: "9.68M", "830k", "640", "23.0B". |
| 1298 | 4 | `public static String figure(Good g, double units)` | A product's quantity in its own unit, short, for a band or a strip's cell: litres "2.00M" ("830k", "640"), tonnes "1,234 t" ("830k t"). |
| 1304 | 3 | `public static String unitsWords(Good g, double units)` | ...and with its unit: "2.00M L", "1,234 t". |
| 1309 | 8 | `public static String price(double thousands)` | A price a litre or a tonne, as the screens write one (ui/Money.unitPrice()): "$0.5620", "$642.50". |
| 1319 | 5 | `public static String spreadWords(double thousands)` | A spread a litre, signed: "+$0.0840", "−$0.0310". |
| 1326 | 6 | `public static String barrels(double litresAMonth)` | Barrels a day of a feed a month, to three figures: "2,000 b/d" (an Oil Refinery's 8,300 t), "240 b/d". |
| 1334 | 3 | `static String columnWords(View v)` | The column's line: its barrels a day and the share it ran: "2,000 b/d · runs 100%". |
| 1339 | 13 | `public static String gradeWords(double[] mix)` | A mix of grades in words: "medium crude", "light 62% · medium 38%". |
| 1354 | 9 | `public static String sourceName(Source s)` | A source's name: "Land wells ×22", "Platform wells ×4", "Strategic reserve", "Imported crude", "Tank farm". |
| 1365 | 8 | `public static String sourceWords(Source s)` | ...its line: the tonnes and the grade - "8,306 t · light", "8,306 t · mixed grades" (the column gives the run's shares), "1,200 t released". |
| 1375 | 4 | `static String oneGrade(double[] mix)` | A mix's one grade in a word, or "mixed grades". |
| 1380 | 9 | `static Icon sourceIcon(SourceKind k)` |  |
| 1391 | 3 | `public static String unitName(Unit u)` | A unit's name over its box: "Reformer", "Cracking Unit ×2". |
| 1396 | 5 | `public static String unitWords(Unit u)` | ...and its line after its name (mockup 1's): "300 b/d · 100%", "300 b/d · idle" - its spread and its gate are under the pointer (unitTip()). |
| 1403 | 24 | `public static String unitTip(Unit u)` | Under the pointer, a unit: its run, its spread at the local and at the city's own prices, what idles it, and one more weighed by the planner. |
| 1429 | 9 | `public static String gateName(SpreadPlanner.Gate g)` | A planner's gate in a word. |
| 1440 | 8 | `static String productTip(Product p)` | Under the pointer, a product's band: made, idled, its takers. |
| 1450 | 13 | `public static String buyerName(Buyer b)` | A taker's name: "Cars", "Vans & lorries", "Rail", "Factories", "Roads", a sector's label, "Into the tanks", "Idled", "Abroad". |
| 1464 | 13 | `static Icon buyerIcon(BuyerKind k)` |  |
| 1479 | 8 | `public static String buyerFigure(Buyer b)` | A taker's figure: its litres, or its tonnes when it took only bitumen or coke. |
| 1493 | 14 | `public static String buyerWords(Buyer b)` | ...and its line: what it took (mockup 1's), and with some of it imported, the product and how much - "petrol at the pump", "diesel · 0.10M L of it imported". |
| 1512 | 12 | `static String goodsWords(Buyer b, int most)` | The goods a taker took, largest first, at most `most`, by their short names: "fuel oil, jet fuel, gas and more". |
| 1526 | 3 | `static String shortName(Good g)` | A product in a list: its name, the gas and the coke shortened as mockup 1 shortens them. |
| 1531 | 11 | `static String buyerTip(Buyer b)` | Under the pointer, a taker: each product it took, home and imported. |
| 1544 | 3 | `public static String scaleWords(double pxALitre)` | The scale, for the heading's right: "10 px of ribbon = 1.30M L". |

### THE STRIP: THIS MONTH, BY PRODUCT (lines 1548-1602)

| line | len | member | says |
|---:|---:|---|---|
| 1557 | 2 | **type** `public record Cell(Good good, String name, String made, String imported, boolean hot, String exported, Stri...` | One product's cell under the picture (mockup 1's strip). |
| 1568 | 12 | `public static List<Cell> strip(View v)` | The strip's cells, in TANK_ORDER: every product the month made, took or shipped. |
| 1585 | 6 | `public static String emptyWords(View v)` | What the card says when there is no month to draw: no crude unit, a month not counted, a month that ran nothing - and since 0.7.105 why, when the run stopped it (Refining.runWords()). |
| 1593 | 4 | `public static String runTip(View v)` | The run in a sentence or two (0.7.105): its share of nameplate and why (Refining.runWords()); empty with no crude unit. |
| 1599 | 3 | `static String withRun(View v)` | ...on a line of its own under a cut's or a unit's pointer while the run is short of nameplate; nothing at the whole run. |


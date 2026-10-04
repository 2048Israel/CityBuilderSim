# InfrastructureScreen.java - 1,528 lines · 56 methods · 24 constants · interface

`ham/citybuildersim/ui/InfrastructureScreen.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

> The infrastructure tab: the roads, the trams, the railway and the freight -
> four pages under the five figures the whole tab is about, each page led by
> the one picture that answers its question.
> 
> WHY its own class (0.7.28): it shared ServicesScreen with the Services tab
> from the 2026-09-18 split, two rail tabs in one file of 2,550 lines, and the
> two are redrawn one after the other - so the INFRASTRUCTURE banner and its
> four pages moved here verbatim first. Redrawn in 0.7.29 in Build's style
> (the project's spec-infra-0729.md): each page was a 560 px statement column
> of label-and-figure rows, two band meters, three grids and two dozen grey
> paragraphs. The pictures lead now - the flow curve with the city's dot on
> it beside the walk from the trips the city makes to what is on its road; a
> funnel through transit's three ceilings; the month's freight bill split
> three ways; a bar a good - the old statements and grids sit behind
> "details" and the paragraphs behind an (i). Infrastructure explains and
> Build acts: every page has a door to Build's Roads & transit, and Build's
> rings have one back. The rail's Infrastructure tab opens
> showInfrastructureMenu(); open() is every other door in (infraPage).

**Uses:** [Palette](Palette.md) (259), [InfrastructureManager](InfrastructureManager.md) (45), [Traffic](Traffic.md) (31), [CityNeeds](CityNeeds.md) (29), [Icons](Icons.md) (23), [BuildAdvice](BuildAdvice.md) (17), [Rail](Rail.md) (15), [ServicesScreen](ServicesScreen.md) (11), [Sector](Sector.md) (9), [BuildScreen](BuildScreen.md) (8), [TaxPolicy](TaxPolicy.md) (7), [EconomyManager](EconomyManager.md) (5), [Pieces](Pieces.md) (5), [Good](Good.md) (4), [CareType](CareType.md) (3), [PolicyScreen](PolicyScreen.md) (3), [UserInterface](UserInterface.md) (2), [Money](Money.md) (2), [GoodsMarket](GoodsMarket.md) (2), [SummaryScreen](SummaryScreen.md) (1), [Ladder](Ladder.md) (1), [Levers](Levers.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 54 | INFRASTRUCTURE - the roads, the trams, the railway and the freight |
| 191 | · THE FIVE FIGURES (0.7.29; four until then) |
| 279 | · the screen's own pieces |
| 363 | ROADS (0.7.29) |
| 784 | TRANSIT - and the one control on this tab (0.7.29) |
| 1034 | THE RAILWAY - the twelfth sector, and the only mode the city does not |
| 1289 | FREIGHT - the band, the bill, and the lorries (0.7.29) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 100 | `InfrastructureScreen.INFRA_PAGES` | `{ "Roads", "Transit", "The railway", "Freight" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. |
| 104 | `InfrastructureScreen.INFRA_ICONS` | `{ Icons.ROADS, Icons.BUS, Icons.RAIL, Icons.LORRY }` | Each page's icon on its chip (0.7.29): the road, the bus, the train and the lorry. |
| 115 | `InfrastructureScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). |
| 118 | `InfrastructureScreen.CURVE_MAX` | `2.0` | The flow curve's x scale: what the road serves, from nothing to this (0.7.41; its use, to 260%, until then), fixed so the dot's motion reads month to month; the floor ends at MIN_THROUGHPUT / FREE_FLOW, 39%, and a roa... |
| 277 | `InfrastructureScreen.UNKNOWN_YET` | `"known after a month: the save predates 0.7.29"` | What a dash means, where one is shown. |
| 419 | `InfrastructureScreen.FLOW_INFO` | `String.format("Every business in the city multiplies its output by its " + "f...` | The flow's (i): the old page's paragraph under its big figure, said in served since 0.7.41. |
| 510 | `InfrastructureScreen.WALK_INFO` | `"Every building makes trips - its staff to and from work, its goods and its "...` | The walk's (i). |
| 517 | `InfrastructureScreen.CARS_INFO` | `String.format("A fully motorised city asks %.1f times the commuter road a cit...` | The car row's (i): the old page's car paragraph (P4). |
| 525 | `InfrastructureScreen.NO_CARS_INFO` | `"Nobody in this city owns one yet, so a commuter costs the road exactly " + "...` | ...and with nobody driving (P3). |
| 530 | `InfrastructureScreen.COSTS_INFO` | `"\"Costs\" is what one trip of this kind asks of the street once it is on " +...` | The stream cards' "costs" (P2, rewritten: a commuter is not always one). |
| 649 | `InfrastructureScreen.FlowCurve.W` | `270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5` | The plot's size, and the room for the axes' figures at its left and under it. |
| 839 | `InfrastructureScreen.NO_TRANSIT_INFO` | `"This city has built no transit at all.A Bus Network is the cheap rung " + "a...` | The no-transit card's (i) (P6). |
| 845 | `InfrastructureScreen.CEILINGS_INFO` | `"A city that builds a metro and no streets gets a metro nobody can reach, " +...` | The ceilings' (i) (P7). |
| 960 | `InfrastructureScreen.FARE_LADDER` | `520` | The fare's ladder, and its effects under it: the card is one of two across the page. |
| 990 | `InfrastructureScreen.FARE_INFO` | `"A FARE IS A PRICE AND NOT A CHARGE, which is what makes this different " + "...` | The fare's (i) (P9). |
| 998 | `InfrastructureScreen.PREVIEW_INFO` | `"The riders line is this month's ceilings at the new fare, which is the " + "...` | The preview's (i) (P10). |
| 1093 | `InfrastructureScreen.QUOTE_INFO` | `"Of what a lorry would charge for the same tonne.The railway is a " + "privat...` | The quote's (i) (P12). |
| 1100 | `InfrastructureScreen.NO_TRACK_INFO` | `"Nobody has laid a line in this city, so every tonne that leaves " + "it leav...` | No track (P11). |
| 1105 | `InfrastructureScreen.LINE_RULE_INFO` | `String.format("It will not lay a line it cannot fill to %.0f%%, which is why ...` | The line rule (P17). |
| 1111 | `InfrastructureScreen.BILL_INFO` | `"The month's lorry bill is what the tonnes that crossed the city's boundary "...` | The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). |
| 1207 | `InfrastructureScreen.RELIEF_INFO` | `String.format("A tonne that leaves by train does not drive across the city to...` | The relief's (i) (P15). |
| 1241 | `InfrastructureScreen.BIGGER_INFO` | `"It bills under three quarters of what the rule allows.It is allowed to " + "...` | The too-big railway (P16). |
| 1333 | `InfrastructureScreen.BAND_INFO` | `"Every traded good's price has a cost of MOVING it inside the gap between " +...` | The bars' (i) (P18 and P20, for bars rather than a grid). |
| 1472 | `InfrastructureScreen.LORRY_INFO` | `String.format("A vehicle moves about %,.0f tonnes a month and lasts %.0f year...` | The lorries' (i) (P23). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 106 | `String infraPage` |  |
| 109 | `private final java.util.Set<String> detailsOpen` | Which "details" folds are open, by page - kept while the game runs, not saved (0.7.29). |
| 112 | `private javafx.scene.control.ScrollPane body` | The page's scroller, for a part of a picture that scrolls the page to where it is decided (Transit's fare). |
| 650 | `private final Label useLabel, flowLabel, xLow, xHigh, yHigh, yLow, over` |  |
| 652 | `private final double dotX, dotY` | Where the dot's drops meet the axes. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 1489 | **type** `final class InfrastructureScreen` | The infrastructure tab: the roads, the trams, the railway and the freight - four pages under the five figures the whole tab is about, each page led by the one picture that answers its question. |
| 45 | 1 | `InfrastructureScreen(UserInterface ui)` |  |
| 48 | 5 | `void open(String page)` | The tab opened on one of its pages, at the top of it (0.7.28): Services' road card, Build's road and transit doors, a figure or a part of a picture on this tab. |

### INFRASTRUCTURE - the roads, the trams, the railway and the freight (lines 54-190)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 7 | `static String streamColour(Traffic s)` | The three streams' colours - categories, never verdicts: commuters the people teal, goods the business violet, bulk the ore sand. |
| 130 | 7 | `static String streamIcon(Traffic s)` | ...and their icons: a person, a shop, a plant. |
| 139 | 1 | `String reliefPage(Traffic s)` | The page that relieves a stream: transit for the commuters, the railway for the freight. |
| 141 | 34 | `void showInfrastructureMenu()` |  |
| 183 | 7 | `HBox head()` | The head: "Infrastructure" with the people teal's swatch, and at its right the two doors every page has - Build's Roads & transit, opened on the ring this page explains (transit's on Transit, the road's elsewhere), in... |

### THE FIVE FIGURES (0.7.29; four until then) (lines 191-278)

| line | len | member | says |
|---:|---:|---|---|
| 203 | 39 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 244 | 4 | `static String flowWords(InfrastructureManager roads)` | FLOW's note: the road's status in a word, with what it costs once it bites. |
| 250 | 14 | `String flowChange()` | FLOW's change on last month, from History's road throughput (its last two months kept): "▼ 0.6 pts on last month"; null with fewer than two. |
| 266 | 3 | `static CityNeeds.Served roadServed(InfrastructureManager roads)` | What the road serves and the one verdict on it (0.7.41) - the road's one judge, as every road gauge reads it; its colour was NEEDS YOU's ROADS row's level, amber while road sites were on the way, until then. |
| 271 | 1 | `static String pct(double share)` | A share as a whole per cent, as Build's rings write it. |
| 274 | 1 | `static String moneyOr(double thousands)` | Money, or a dash for a figure the model does not know yet (the railway's allowed bill and its split, after loading a save from before 0.7.29, until a month runs). |

### the screen's own pieces (lines 279-362)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 6 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 290 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 298 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 309 | 9 | `static javafx.scene.layout.FlowPane pairLine(String a, String aTone, String aWords, String b, String bTone, String bWords)` | "62% served → 56% flow" ("162% full" until 0.7.41): the two figures at 28 px, the words between them smaller. |
| 320 | 9 | `static HBox cardHead(String svg, String colour, String name, String info)` | A card's head: its icon in a square tinted in its colour, its name, and its (i). |
| 331 | 10 | `static javafx.scene.layout.FlowPane figureLine(Object...parts)` | A line of figures and words that wraps: "102k trips · 135k on the road". |
| 343 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 351 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (Transit's "the fare" step, which opens the fare card). |

### ROADS (0.7.29) (lines 363-783)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 11 | `void roadsPage(VBox page, List<CityNeeds.Need> all)` |  |
| 389 | 28 | `VBox roadHeroLeft(InfrastructureManager roads, List<CityNeeds.Need> all)` | The hero's left: THE ROAD, both figures as a pair, what the flow costs, the status chip, the curve and what is on site. |
| 432 | 6 | `static String congestedWords(double flow)` | The congested alert's words (P5), now the (i)'s second half. |
| 440 | 55 | `VBox roadWalk(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | The hero's right: FROM TRIPS TO THE ROAD, five rows on one scale with the capacity and the free-flow line through them. |
| 497 | 11 | `List<Run> streams(double[] amounts)` | The three streams laid end to end from nothing, each a door to the page that relieves it. |
| 537 | 28 | `javafx.scene.layout.GridPane streamCards(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | A card a stream: what it makes and puts on the road, what gets through, what a trip costs, and what relieves it. |
| 567 | 17 | `HBox networkStrip(InfrastructureManager roads)` | The network in four figures: what it can carry, the room before it slows (on the load the curve reads), the lorry share, the cars. |
| 586 | 49 | `VBox roadsStatement(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | The old Roads page, line by line, in its fold: the network, the streams' grid and the cars (B3's room and B6's costs fixed). |
| 647 | 136 | **type** `static final class FlowCurve extends javafx.scene.layout.Pane` | The flow curve (0.7.29): the road's flow against what it serves (0.7.41; against how full it was until then), from nothing to CURVE_MAX - the model's own curve (InfrastructureManager.throughputAtServed()) as a line ov... |
| 654 | 92 | `FlowCurve(double served, double then, String tone)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 747 | 7 | `private static Label label(String text, String tone)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 755 | 1 | `private static double x(double served)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 757 | 3 | `private static double y(double flow, double span)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 761 | 16 | `protected void layoutChildren()` _(in InfrastructureScreen.FlowCurve)_ |  |
| 778 | 4 | `private static void place(Label l, double x, double y)` _(in InfrastructureScreen.FlowCurve)_ |  |

### TRANSIT - and the one control on this tab (0.7.29) (lines 784-1033)

| line | len | member | says |
|---:|---:|---|---|
| 794 | 43 | `void transitPage(VBox page)` |  |
| 850 | 14 | `static String carsRideInfo(InfrastructureManager roads)` | The cars' step's (i) (P8): how the remembered commute walks the drivers onto the tram. |
| 866 | 45 | `VBox transitFunnel(InfrastructureManager roads, VBox fareCard, BuildAdvice.Measure transit)` | The funnel: commuters, the three ceilings with the lowest tagged, the fare's step and the cars' to the riders - on one scale, the commuters full. |
| 913 | 18 | `VBox transitBooks(EconomyManager econ)` | WHAT IT COSTS THE CITY: the bill as a bar, the fares in it, and the net - neutral, with signs: a net cost is a policy, not a failure. |
| 941 | 17 | `VBox fareCard(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy)` | THE FARE on its dial card (0.7.38, the Policy spec's D18 step 6), as Policy draws every dial: the reading and what a month of riding costs, the ladder, and under it what the fare would do, before and after, following ... |
| 971 | 11 | `java.util.function.DoubleFunction<List<Pieces.Effect>> fareEffects(InfrastructureManager roads, EconomyManager econ)` | What the fare would do at any value of its thumb (pure: the probe reads them), each the model's read at that fare against this month's: the riders (InfrastructureManager.ridersAt()), a month of fares from them (faresA... |
| 984 | 4 | `static String trips(double n)` | Trips put onto the road, signed with a true minus for trips taken off it: "+1,240 trips", "−310 trips", "none". |
| 1004 | 29 | `VBox transitStatement(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy)` | The old Transit page, line by line, in its fold. |

### THE RAILWAY - the twelfth sector, and the only mode the city does not (lines 1034-1288)

| line | len | member | says |
|---:|---:|---|---|
| 1045 | 46 | `void railwayPage(VBox page)` |  |
| 1120 | 33 | `VBox freightBill(ham.citybuildersim.sectors.Rail rail)` | The month's freight bill, three ways: one bar as long as the lorry bill, its key with the figures, and the fuel. |
| 1155 | 30 | `VBox trackCard(ham.citybuildersim.sectors.Rail rail)` | TRACK AND TRAINS: the track, the sets against what it needs, what it can carry, and the trains it is short or the ones it wears out. |
| 1187 | 18 | `VBox haulsCard(ham.citybuildersim.sectors.Rail rail)` | WHAT IT HAULS: what crossed and what it took, each stream's share, and what that takes off the road. |
| 1215 | 24 | `VBox businessCard(ham.citybuildersim.sectors.Rail rail)` | THE BUSINESS: billed against what the rule allows, the fuel, the net income, the capital - and the door to its books. |
| 1248 | 40 | `VBox railwayStatement(ham.citybuildersim.sectors.Rail rail, InfrastructureManager roads)` | The old railway page, line by line, in its fold. |

### FREIGHT - the band, the bill, and the lorries (0.7.29) (lines 1289-1528)

| line | len | member | says |
|---:|---:|---|---|
| 1300 | 31 | `void freightPage(VBox page)` |  |
| 1347 | 17 | `VBox bandKey()` | The bars' key. |
| 1366 | 2 | **type** `record GoodBar(Good good, double margin, double abroad, double rail, double byLorry, double delivered, doub...` | One good's bar, worked out without drawing it: its shares of today's import price, and its words. |
| 1370 | 23 | `List<GoodBar> goodBars()` | Every good the city can both buy and sell, as GoodBar - bulk first, then the rest, each by its delivered share, the widest first. |
| 1395 | 23 | `Node goodsBars()` | The bars: a caption a group, a row a good on one scale - today's import price, or more if a good's gap is wider. |
| 1420 | 50 | `VBox lorriesCard()` | The lorries: one line while nobody is short; a bar a short sector otherwise. |
| 1485 | 4 | `static boolean shownFleet(Sector s)` | Whether a sector's fleet is worth a row: it moves a tonne or owns or needs a lorry - and not a row of noughts (spec bug B11: a fraction of a tonne or of a van got past the old test and printed "0 · 0 · 0 · 100%"). |
| 1491 | 37 | `VBox freightStatement()` | The old Freight page's two grids, in its fold. |


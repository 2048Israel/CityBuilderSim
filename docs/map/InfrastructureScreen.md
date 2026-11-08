# InfrastructureScreen.java - 2,215 lines · 77 methods · 33 constants · interface

`ham/citybuildersim/ui/InfrastructureScreen.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The infrastructure tab: the roads, the trams, the freight and the ports -
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
> Build acts: every page has a door to Build's Infrastructure (Roads & transit until FR4), and Build's
> rings have one back. The rail's Infrastructure tab opens
> showInfrastructureMenu(); open() is every other door in (infraPage).
> 
> FR4 (2026-10-19): the railway and freight pages became one Freight page with
> a division strip (All, Haulage, Rail, Ports), and the Ports are a page of
> their own (FREIGHT AND PORTS, below). open("The railway") is still a door in.

**Uses:** [Palette](Palette.md) (365), [Kept](Kept.md) (154), [InfrastructureManager](InfrastructureManager.md) (47), [Icons](Icons.md) (32), [Traffic](Traffic.md) (31), [CityNeeds](CityNeeds.md) (30), [BuildAdvice](BuildAdvice.md) (21), [Freight](Freight.md) (20), [ServicesScreen](ServicesScreen.md) (16), [FreightView](FreightView.md) (13), [TaxPolicy](TaxPolicy.md) (10), [Sector](Sector.md) (9), [BuildScreen](BuildScreen.md) (8), [Ports](Ports.md) (7), [Pieces](Pieces.md) (6), [EconomyManager](EconomyManager.md) (5), [Money](Money.md) (5), [Good](Good.md) (4), [MiniMapFrame](MiniMapFrame.md) (4), [CareType](CareType.md) (3), [PolicyScreen](PolicyScreen.md) (3), [Haulage](Haulage.md) (3), [UserInterface](UserInterface.md) (2), [GoodsMarket](GoodsMarket.md) (2), [SummaryScreen](SummaryScreen.md) (1), [Ladder](Ladder.md) (1), [Levers](Levers.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1), [PayTier](PayTier.md) (1), [BoatSchedule](BoatSchedule.md) (1)... and 1 more

**Used by (2):** [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 76 | INFRASTRUCTURE - the roads, the trams, the freight and the ports |
| 302 | · THE FIVE FIGURES (0.7.29; four until then) |
| 398 | · the screen's own pieces |
| 485 | ROADS (0.7.29) |
| 1009 | TRANSIT - and the one control on this tab (0.7.29) |
| 1356 | THE RAILWAY - the twelfth sector, and the only mode the city does not |
| 1631 | FREIGHT - the band, the bill, and the lorries (0.7.29) |
| 1907 | FREIGHT AND PORTS (FR4, 2026-10-19) |
| 1976 | · Haulage |
| 2072 | · Ports |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 125 | `InfrastructureScreen.INFRA_PAGES` | `{ "Roads", "Transit", "Freight", "Ports" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know (FR4: "The railway" and "Freight" are one page, and the Ports a new one; open("The railway")... |
| 129 | `InfrastructureScreen.INFRA_ICONS` | `{ Icons.ROADS, Icons.BUS, Icons.LORRY, Icons.TANKER }` | Each page's icon on its chip (0.7.29; FR4): the road, the bus, the lorry and the ship. |
| 132 | `InfrastructureScreen.DIVISIONS` | `{ "All", "Haulage", "Rail", "Ports" }` | The freight business's divisions, in the strip's order (FR4; spec-freight-and-infrastructure 6): All is today's Freight page, Haulage the trucks, Rail the railway, Ports the Ports page. |
| 146 | `InfrastructureScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). |
| 149 | `InfrastructureScreen.DIVISION_CHROME` | `38` | ...and what the division strip adds on the Freight and Ports pages (FR4): its chips and the gap above them. |
| 152 | `InfrastructureScreen.CURVE_MAX` | `2.0` | The flow curve's x scale: what the road serves, from nothing to this (0.7.41; its use, to 260%, until then), fixed so the dot's motion reads month to month; the floor ends at MIN_THROUGHPUT / FREE_FLOW, 39%, and a roa... |
| 258 | `InfrastructureScreen.BOUND_TO_SCROLLER` | `"infrastructure.scroller"` | The key in a kept page's properties for the scroller its width is bound to (drawPage()). |
| 396 | `InfrastructureScreen.UNKNOWN_YET` | `"known after a month: the save predates 0.7.29"` | What a dash means, where one is shown. |
| 555 | `InfrastructureScreen.FLOW_INFO` | `String.format("Every business in the city multiplies its output by its " + "f...` | The flow's (i): the old page's paragraph under its big figure, said in served since 0.7.41. |
| 648 | `InfrastructureScreen.WALK_INFO` | `"Every building makes trips - its staff to and from work, its goods and its "...` | The walk's (i). |
| 655 | `InfrastructureScreen.CARS_INFO` | `String.format("A fully motorised city asks %.1f times the commuter road a cit...` | The car row's (i): the old page's car paragraph (P4). |
| 663 | `InfrastructureScreen.NO_CARS_INFO` | `"Nobody in this city owns one yet, so a commuter costs the road exactly " + "...` | ...and with nobody driving (P3). |
| 668 | `InfrastructureScreen.COSTS_INFO` | `"\"Costs\" is what one trip of this kind asks of the street once it is on " +...` | The stream cards' "costs" (P2, rewritten: a commuter is not always one). |
| 797 | `InfrastructureScreen.FlowCurve.W` | `270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5` | The plot's size, and the room for the axes' figures at its left and under it. |
| 1072 | `InfrastructureScreen.NO_TRANSIT_INFO` | `"This city has built no transit at all.A Bus Network is the cheap rung " + "a...` | The no-transit card's (i) (P6). |
| 1078 | `InfrastructureScreen.CEILINGS_INFO` | `"A city that builds a metro and no streets gets a metro nobody can reach, " +...` | The ceilings' (i) (P7). |
| 1169 | `InfrastructureScreen.RIDERS_INFO` | `"A household holds one car at most, so a couple's second earner and four of f...` | The riders' caption's (i) (0.7.49). |
| 1268 | `InfrastructureScreen.FARE_LADDER` | `520` | The fare's ladder, and its effects under it: the card is one of two across the page. |
| 1305 | `InfrastructureScreen.FARE_INFO` | `"A FARE IS A PRICE TO CAR OWNERS AND A CHARGE TO EVERYONE ELSE.Commuters with...` | The fare's (i) (P9). |
| 1311 | `InfrastructureScreen.PREVIEW_INFO` | `"The riders line is this month's ceilings at the new fare, which is the " + "...` | The preview's (i) (P10). |
| 1423 | `InfrastructureScreen.QUOTE_INFO` | `"Of what a lorry would charge for the same tonne.The railway is a " + "privat...` | The quote's (i) (P12). |
| 1430 | `InfrastructureScreen.NO_TRACK_INFO` | `"Nobody has laid a line in this city, so every tonne that leaves " + "it leav...` | No track (P11). |
| 1435 | `InfrastructureScreen.LINE_RULE_INFO` | `String.format("It will not lay a line it cannot fill to %.0f%%, which is why ...` | The line rule (P17). |
| 1441 | `InfrastructureScreen.BILL_INFO` | `"The month's lorry bill is what the tonnes that crossed the city's boundary "...` | The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). |
| 1546 | `InfrastructureScreen.RELIEF_INFO` | `String.format("A tonne that leaves by train does not drive across the city to...` | The relief's (i) (P15). |
| 1583 | `InfrastructureScreen.BIGGER_INFO` | `"It bills under three quarters of what the rule allows.It is allowed to " + "...` | The too-big railway (P16). |
| 1678 | `InfrastructureScreen.BAND_INFO` | `"Every traded good's price has a cost of MOVING it inside the gap between " +...` | The bars' (i) (P18 and P20, for bars rather than a grid). |
| 1828 | `InfrastructureScreen.LORRY_INFO` | `String.format("A vehicle moves about %,.0f tonnes a month and lasts %.0f year...` | The lorries' (i) (P23). |
| 1930 | `InfrastructureScreen.HAULAGE_QUOTE_INFO` | `String.format("What it charges for a tonne's city leg, from the depot or the ...` | Haulage's quote (i) (FR4). |
| 1939 | `InfrastructureScreen.TRUCKS_INFO` | `String.format("A truck carries about %,.0f tonnes a month.Haulage owns the fl...` | The trucks' (i) (FR4). |
| 1947 | `InfrastructureScreen.DEPOTS_INFO` | `String.format("A Truck Depot keeps trucks, and its posts are the drivers and ...` | The depots' (i) (FR4). |
| 1954 | `InfrastructureScreen.PORTS_INFO` | `"The sea terminals are the city's: only the player builds them.Freight works ...` | The ports' (i) (FR4). |
| 1961 | `InfrastructureScreen.BERTHS_INFO` | `"A berth handles one kind of cargo: liquid bulk, dry bulk, containers or " + ...` | The berths' (i) (FR4). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 66 | `final Kept.Pool pool` | The Infrastructure tab's kept nodes (Kept; study UP4, KP2): the page is drawn over them on each redraw. |
| 69 | `private boolean toTop` | A page was opened (open()): the page's scroller goes to its top once drawn. |
| 134 | `String infraPage` |  |
| 137 | `String freightDivision` | Which of the Freight page's divisions is open - All, Haulage or Rail; the Ports are a page of their own - kept while the game runs, not saved (FR4, as detailsOpen). |
| 140 | `final java.util.Set<String> detailsOpen` | Which "details" folds are open, by page - kept while the game runs, not saved (0.7.29). |
| 143 | `private javafx.scene.control.ScrollPane body` | The page's scroller, for a part of a picture that scrolls the page to where it is decided (Transit's fare). |
| 798 | `private final Label useLabel, flowLabel, xLow, xHigh, yHigh, yLow, over` |  |
| 799 | `private final javafx.scene.shape.Line xAxis, yAxis, down, across` |  |
| 800 | `private final Region[] bands` |  |
| 801 | `private final Tooltip[] bandTips` |  |
| 802 | `private final javafx.scene.shape.Polyline curve` |  |
| 803 | `private final javafx.scene.shape.Circle hollow, dot` |  |
| 804 | `private final Tooltip hollowTip, dotTip` |  |
| 806 | `private double dotX, dotY` | Where the dot's drops meet the axes. |
| 808 | `private double served` | What the curve was last told: the road's served share, the lines it was drawn against, the dot's colour. |
| 809 | `private double[] lines` |  |
| 810 | `private String tone` |  |
| 812 | `private boolean hollowShown, told` | Whether the hollow dot is among the children, and whether the curve has been told anything yet. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 2172 | **type** `final class InfrastructureScreen` | The infrastructure tab: the roads, the trams, the freight and the ports - four pages under the five figures the whole tab is about, each page led by the one picture that answers its question. |
| 49 | 1 | `InfrastructureScreen(UserInterface ui)` |  |
| 52 | 12 | `void open(String page)` | The tab opened on one of its pages, at the top of it (0.7.28): Services' road card, Build's road and transit doors, a figure or a part of a picture on this tab. |
| 72 | 3 | `void forget()` | A new city or a load: nothing of the last city's page is kept. |

### INFRASTRUCTURE - the roads, the trams, the freight and the ports (lines 76-301)

| line | len | member | says |
|---:|---:|---|---|
| 155 | 7 | `static String streamColour(Traffic s)` | The three streams' colours - categories, never verdicts: commuters the people teal, goods the business violet, bulk the ore sand. |
| 164 | 7 | `static String streamIcon(Traffic s)` | ...and their icons: a person, a shop, a plant. |
| 173 | 1 | `String reliefPage(Traffic s)` | The page that relieves a stream: transit for the commuters, freight (its railway) for the freight. |
| 176 | 8 | `void openDivision(String division)` | A division of the freight business opened (FR4): the Ports are their own page, the others the Freight page on that division. |
| 185 | 19 | `void showInfrastructureMenu()` |  |
| 210 | 46 | `void drawPage(List<CityNeeds.Need> all)` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck, at the page `infraPage` (and, on Freight, the division `... |
| 268 | 16 | `HBox head()` | The head: "Infrastructure" with the people teal's swatch, and at its right the two doors every page has - Build's Infrastructure (Roads & transit until FR4), opened on the ring this page explains (transit's on Transit... |
| 291 | 10 | `HBox divisionStrip()` | The division strip (FR4), under the page chips on the Freight and Ports pages: All, Haulage, Rail and Ports, the open one lit. |

### THE FIVE FIGURES (0.7.29; four until then) (lines 302-397)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 47 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 363 | 4 | `static String flowWords(InfrastructureManager roads)` | FLOW's note: the road's status in a word, with what it costs once it bites. |
| 369 | 14 | `String flowChange()` | FLOW's change on last month, from History's road throughput (its last two months kept): "▼ 0.6 pts on last month"; null with fewer than two. |
| 385 | 3 | `static CityNeeds.Served roadServed(InfrastructureManager roads)` | What the road serves and the one verdict on it (0.7.41) - the road's one judge, as every road gauge reads it; its colour was NEEDS YOU's ROADS row's level, amber while road sites were on the way, until then. |
| 390 | 1 | `static String pct(double share)` | A share as a whole per cent, as Build's rings write it. |
| 393 | 1 | `static String moneyOr(double thousands)` | Money, or a dash for a figure the model does not know yet (the railway's allowed bill and its split, after loading a save from before 0.7.29, until a month runs). |

### the screen's own pieces (lines 398-484)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 8 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 411 | 7 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 420 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 431 | 9 | `static javafx.scene.layout.FlowPane pairLine(String a, String aTone, String aWords, String b, String bTone, String bWords)` | "62% served → 56% flow" ("162% full" until 0.7.41): the two figures at 28 px, the words between them smaller. |
| 442 | 9 | `static HBox cardHead(String svg, String colour, String name, String info)` | A card's head: its icon in a square tinted in its colour, its name, and its (i). |
| 453 | 10 | `static javafx.scene.layout.FlowPane figureLine(Object...parts)` | A line of figures and words that wraps: "102k trips · 135k on the road". |
| 465 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 473 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (Transit's "the fare" step, which opens the fare card). |

### ROADS (0.7.29) (lines 485-1008)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 13 | `void roadsPage(VBox page, List<CityNeeds.Need> all)` |  |
| 513 | 33 | `VBox roadHeroLeft(InfrastructureManager roads, List<CityNeeds.Need> all)` | The hero's left: THE ROAD, both figures as a pair, what the flow costs, the status chip, the curve and what is on site. |
| 548 | 5 | `static FlowCurve flowCurve(double served, double then, String tone)` | The flow curve, made once and told its figures (Kept; a curve outside a draw is a new one). |
| 568 | 6 | `static String congestedWords(double flow)` | The congested alert's words (P5), now the (i)'s second half. |
| 576 | 57 | `VBox roadWalk(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | The hero's right: FROM TRIPS TO THE ROAD, five rows on one scale with the capacity and the free-flow line through them. |
| 635 | 11 | `List<Run> streams(double[] amounts)` | The three streams laid end to end from nothing, each a door to the page that relieves it. |
| 675 | 33 | `javafx.scene.layout.GridPane streamCards(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | A card a stream: what it makes and puts on the road, what gets through, what a trip costs, and what relieves it. |
| 710 | 17 | `HBox networkStrip(InfrastructureManager roads)` | The network in four figures: what it can carry, the room before it slows (on the load the curve reads), the lorry share, the cars. |
| 729 | 50 | `VBox roadsStatement(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | The old Roads page, line by line, in its fold: the network, the streams' grid and the cars (B3's room and B6's costs fixed). |
| 795 | 213 | **type** `static final class FlowCurve extends javafx.scene.layout.Pane` | The flow curve (0.7.29): the road's flow against what it serves (0.7.41; against how full it was until then), from nothing to CURVE_MAX - the model's own curve (InfrastructureManager.throughputAtServed()) as a line ov... |
| 815 | 51 | `FlowCurve()` _(in InfrastructureScreen.FlowCurve)_ | Its parts, made once in its life (Kept: a kept curve is told each month's figures by set()). |
| 872 | 99 | `void set(double served, double then, String tone)` _(in InfrastructureScreen.FlowCurve)_ | What the curve draws now: the road's served share, the served share the road sites on site would leave it at (NaN with none), and the colour of the verdict on the dot. |
| 972 | 7 | `private static Label label(String tone)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 980 | 1 | `private static double x(double served)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 982 | 3 | `private static double y(double flow, double span)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 986 | 16 | `protected void layoutChildren()` _(in InfrastructureScreen.FlowCurve)_ |  |
| 1003 | 4 | `private static void place(Label l, double x, double y)` _(in InfrastructureScreen.FlowCurve)_ |  |

### TRANSIT - and the one control on this tab (0.7.29) (lines 1009-1355)

| line | len | member | says |
|---:|---:|---|---|
| 1021 | 49 | `void transitPage(VBox page)` |  |
| 1088 | 10 | `static String carsRideInfo(InfrastructureManager roads, TaxPolicy policy)` | The owners' row's (i) (P8, rewritten for 0.7.49): what a ride costs against a journey's fuel and the share that choose the bus on it (InfrastructureManager.ownersChoosingAt()), and what the remembered commute adds (ow... |
| 1100 | 38 | `VBox transitFunnel(InfrastructureManager roads, TaxPolicy policy, BuildAdvice.Measure transit)` | The funnel: commuters, the three ceilings with the lowest tagged, and the riders by reason (0.7.49) - on one scale, the commuters full. |
| 1145 | 22 | `List<ScaleRow> ridersByReason(InfrastructureManager roads, TaxPolicy policy)` | THE RIDERS BY REASON (0.7.49), under the ceilings: the commuters with no car of their own - in reach, riding, and walking, out of reach or with no seat - then the owners - in reach, and who chose the bus - and those w... |
| 1175 | 21 | `VBox transitBooks(EconomyManager econ)` | WHAT IT COSTS THE CITY: the bill as a bar, the fares in it, and the net - neutral, with signs: a net cost is a policy, not a failure. |
| 1206 | 36 | `VBox fareCard(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy)` | THE FARE on its dial card (0.7.38, the Policy spec's D18 step 6), as Policy draws every dial: the reading and what a month of riding costs, the ladder, and under it what the fare would do, before and after, following ... |
| 1249 | 10 | `String fareWeighs(InfrastructureManager roads, TaxPolicy policy)` | The fare card's third line (0.7.49): a drive's fuel a journey, as the month struck it at the exchange rate, and a month's pass as a share of an unskilled household's take-home - its row's take-home over its households... |
| 1261 | 5 | `static String fareInfo(TaxPolicy policy)` | The fare's (i) with the dial behind it (0.7.45): its founding figure and the level it is charged at. |
| 1280 | 17 | `java.util.function.DoubleFunction<List<Pieces.Effect>> fareEffects(InfrastructureManager roads, EconomyManager econ)` | What the fare would do at any value of its thumb (pure: the probe reads them), each the model's read at that fare against this month's: the riders (InfrastructureManager.ridersAt()) and the car owners among them (choi... |
| 1299 | 4 | `static String trips(double n)` | Trips put onto the road, signed with a true minus for trips taken off it: "+1,240 trips", "−310 trips", "none". |
| 1317 | 38 | `VBox transitStatement(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy)` | The old Transit page, line by line, in its fold. |

### THE RAILWAY - the twelfth sector, and the only mode the city does not (lines 1356-1630)

| line | len | member | says |
|---:|---:|---|---|
| 1367 | 54 | `void railwayPage(VBox page)` |  |
| 1450 | 38 | `VBox freightBill(ham.citybuildersim.sectors.Freight rail)` | The month's freight bill, three ways: one bar as long as the lorry bill, its key with the figures, and the fuel. |
| 1490 | 34 | `VBox trackCard(ham.citybuildersim.sectors.Freight rail)` | TRACK AND TRAINS: the track, the sets against what it needs, what it can carry, and the trains it is short or the ones it wears out. |
| 1526 | 18 | `VBox haulsCard(ham.citybuildersim.sectors.Freight rail)` | WHAT IT HAULS: what crossed and what it took, each stream's share, and what that takes off the road. |
| 1554 | 27 | `VBox businessCard(ham.citybuildersim.sectors.Freight rail)` | THE BUSINESS: billed against what the rule allows, the fuel, the net income, the capital - and the door to its books. |
| 1590 | 40 | `VBox railwayStatement(ham.citybuildersim.sectors.Freight rail, InfrastructureManager roads)` | The old railway page, line by line, in its fold. |

### FREIGHT - the band, the bill, and the lorries (0.7.29) (lines 1631-1906)

| line | len | member | says |
|---:|---:|---|---|
| 1642 | 34 | `void freightPage(VBox page)` |  |
| 1692 | 17 | `VBox bandKey()` | The bars' key. |
| 1711 | 2 | **type** `record GoodBar(Good good, double margin, double abroad, double rail, double byLorry, double delivered, doub...` | One good's bar, worked out without drawing it: its shares of today's import price, and its words. |
| 1715 | 23 | `List<GoodBar> goodBars()` | Every good the city can both buy and sell, as GoodBar - bulk first, then the rest, each by its delivered share, the widest first. |
| 1740 | 24 | `Node goodsBars()` | The bars: a caption a group, a row a good on one scale - today's import price, or more if a good's gap is wider. |
| 1766 | 60 | `VBox lorriesCard()` | The lorries: one line while nobody is short; a bar a short sector otherwise. |
| 1841 | 4 | `static boolean shownFleet(Sector s)` | Whether a sector's fleet is worth a row: it moves a tonne or owns or needs a lorry - and not a row of noughts (spec bug B11: a fraction of a tonne or of a van got past the old test and printed "0 · 0 · 0 · 100%"). |
| 1847 | 48 | `VBox freightStatement()` | The old Freight page's two grids, in its fold. |
| 1901 | 5 | `static String abroadWords(double bill, double imported, String whole)` | How much of a fuel bill was bought abroad (0.7.62), after the bill's own words: all of it, `whole` (", bought abroad"); part, ", $1.2M of it bought abroad"; none, ", off the city's refineries". |

### FREIGHT AND PORTS (FR4, 2026-10-19) (lines 1907-1975)

| line | len | member | says |
|---:|---:|---|---|
| 1967 | 8 | `static String cargoColour(Ports.Cargo kind)` | A kind of cargo's colour on this screen: categories, never verdicts. |

### Haulage (lines 1976-2071)

| line | len | member | says |
|---:|---:|---|---|
| 1979 | 40 | `void haulagePage(VBox page)` | The Haulage division: the quote, the month's city leg, and the trucks, the depots and the business against them. |
| 2021 | 21 | `VBox trucksCard(FreightView.HaulagePage h)` | THE TRUCKS: owned against what the plant asks, the ones it is short, the ones it wore out. |
| 2044 | 14 | `VBox depotsCard(FreightView.HaulagePage h)` | THE DEPOTS: how many stand, the trucks they keep, and the ones parked on the street. |
| 2060 | 11 | `VBox haulageBusinessCard(ham.citybuildersim.sectors.Freight freight, FreightView.HaulagePage h)` | THE BUSINESS: billed against what the rule allows, the diesel, the share of the need its trucks reach, and the door to the books. |

### Ports (lines 2072-2215)

| line | len | member | says |
|---:|---:|---|---|
| 2075 | 34 | `void portsPage(VBox page)` | The Ports page: the month's handling and dues, the berths by kind, then each kind's bill and the ships that called. |
| 2111 | 16 | `VBox berthRow(FreightView.Berth b)` | One kind of berth: its name, how many stand and the room they are, and the month's tonnes against it. |
| 2129 | 26 | `VBox portBillCard(FreightView.PortsPage p)` | HANDLING AND DUES, BY KIND: what a tonne is billed, what the kind billed, and the dues it paid the city. |
| 2157 | 29 | `VBox shipsCard(FreightView.PortsPage p)` | THE SHIPS THAT CALLED: the month's calls by ship class, and what a call carries. |
| 2188 | 7 | `java.util.Map<Ports.Ship, Integer> callsByShip()` | The month's calls by ship class, in the classes' order, those with none left out. |
| 2203 | 12 | `void openMapAtPort()` | "The ships on the map": the land office's map over the window on the city's terminals (the same door the minimap opens, UserInterface .openMapOn()) - the box round every berthed work on the shore, its quay's end inclu... |


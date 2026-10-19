# InfrastructureScreen.java - 1,982 lines · 73 methods · 32 constants · interface

`ham/citybuildersim/ui/InfrastructureScreen.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (365), [InfrastructureManager](InfrastructureManager.md) (47), [Icons](Icons.md) (32), [Traffic](Traffic.md) (31), [CityNeeds](CityNeeds.md) (29), [BuildAdvice](BuildAdvice.md) (21), [Freight](Freight.md) (20), [ServicesScreen](ServicesScreen.md) (16), [FreightView](FreightView.md) (13), [TaxPolicy](TaxPolicy.md) (10), [Sector](Sector.md) (9), [BuildScreen](BuildScreen.md) (8), [Ports](Ports.md) (7), [Pieces](Pieces.md) (6), [EconomyManager](EconomyManager.md) (5), [Money](Money.md) (5), [Good](Good.md) (4), [MiniMapFrame](MiniMapFrame.md) (4), [CareType](CareType.md) (3), [PolicyScreen](PolicyScreen.md) (3), [Haulage](Haulage.md) (3), [UserInterface](UserInterface.md) (2), [GoodsMarket](GoodsMarket.md) (2), [SummaryScreen](SummaryScreen.md) (1), [Ladder](Ladder.md) (1), [Levers](Levers.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1), [PayTier](PayTier.md) (1), [BoatSchedule](BoatSchedule.md) (1), [CityShore](CityShore.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 62 | INFRASTRUCTURE - the roads, the trams, the freight and the ports |
| 261 | · THE FIVE FIGURES (0.7.29; four until then) |
| 350 | · the screen's own pieces |
| 434 | ROADS (0.7.29) |
| 855 | TRANSIT - and the one control on this tab (0.7.29) |
| 1196 | THE RAILWAY - the twelfth sector, and the only mode the city does not |
| 1453 | FREIGHT - the band, the bill, and the lorries (0.7.29) |
| 1704 | FREIGHT AND PORTS (FR4, 2026-10-19) |
| 1773 | · Haulage |
| 1854 | · Ports |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 111 | `InfrastructureScreen.INFRA_PAGES` | `{ "Roads", "Transit", "Freight", "Ports" }` | The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know (FR4: "The railway" and "Freight" are one page, and the Ports a new one; open("The railway")... |
| 115 | `InfrastructureScreen.INFRA_ICONS` | `{ Icons.ROADS, Icons.BUS, Icons.LORRY, Icons.TANKER }` | Each page's icon on its chip (0.7.29; FR4): the road, the bus, the lorry and the ship. |
| 118 | `InfrastructureScreen.DIVISIONS` | `{ "All", "Haulage", "Rail", "Ports" }` | The freight business's divisions, in the strip's order (FR4; spec-freight-and-infrastructure 6): All is today's Freight page, Haulage the trucks, Rail the railway, Ports the Ports page. |
| 132 | `InfrastructureScreen.FRAME_CHROME` | `232` | How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). |
| 135 | `InfrastructureScreen.DIVISION_CHROME` | `38` | ...and what the division strip adds on the Freight and Ports pages (FR4): its chips and the gap above them. |
| 138 | `InfrastructureScreen.CURVE_MAX` | `2.0` | The flow curve's x scale: what the road serves, from nothing to this (0.7.41; its use, to 260%, until then), fixed so the dot's motion reads month to month; the floor ends at MIN_THROUGHPUT / FREE_FLOW, 39%, and a roa... |
| 348 | `InfrastructureScreen.UNKNOWN_YET` | `"known after a month: the save predates 0.7.29"` | What a dash means, where one is shown. |
| 490 | `InfrastructureScreen.FLOW_INFO` | `String.format("Every business in the city multiplies its output by its " + "f...` | The flow's (i): the old page's paragraph under its big figure, said in served since 0.7.41. |
| 581 | `InfrastructureScreen.WALK_INFO` | `"Every building makes trips - its staff to and from work, its goods and its "...` | The walk's (i). |
| 588 | `InfrastructureScreen.CARS_INFO` | `String.format("A fully motorised city asks %.1f times the commuter road a cit...` | The car row's (i): the old page's car paragraph (P4). |
| 596 | `InfrastructureScreen.NO_CARS_INFO` | `"Nobody in this city owns one yet, so a commuter costs the road exactly " + "...` | ...and with nobody driving (P3). |
| 601 | `InfrastructureScreen.COSTS_INFO` | `"\"Costs\" is what one trip of this kind asks of the street once it is on " +...` | The stream cards' "costs" (P2, rewritten: a commuter is not always one). |
| 720 | `InfrastructureScreen.FlowCurve.W` | `270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5` | The plot's size, and the room for the axes' figures at its left and under it. |
| 912 | `InfrastructureScreen.NO_TRANSIT_INFO` | `"This city has built no transit at all.A Bus Network is the cheap rung " + "a...` | The no-transit card's (i) (P6). |
| 918 | `InfrastructureScreen.CEILINGS_INFO` | `"A city that builds a metro and no streets gets a metro nobody can reach, " +...` | The ceilings' (i) (P7). |
| 1009 | `InfrastructureScreen.RIDERS_INFO` | `"A household holds one car at most, so a couple's second earner and four of f...` | The riders' caption's (i) (0.7.49). |
| 1108 | `InfrastructureScreen.FARE_LADDER` | `520` | The fare's ladder, and its effects under it: the card is one of two across the page. |
| 1145 | `InfrastructureScreen.FARE_INFO` | `"A FARE IS A PRICE TO CAR OWNERS AND A CHARGE TO EVERYONE ELSE.Commuters with...` | The fare's (i) (P9). |
| 1151 | `InfrastructureScreen.PREVIEW_INFO` | `"The riders line is this month's ceilings at the new fare, which is the " + "...` | The preview's (i) (P10). |
| 1255 | `InfrastructureScreen.QUOTE_INFO` | `"Of what a lorry would charge for the same tonne.The railway is a " + "privat...` | The quote's (i) (P12). |
| 1262 | `InfrastructureScreen.NO_TRACK_INFO` | `"Nobody has laid a line in this city, so every tonne that leaves " + "it leav...` | No track (P11). |
| 1267 | `InfrastructureScreen.LINE_RULE_INFO` | `String.format("It will not lay a line it cannot fill to %.0f%%, which is why ...` | The line rule (P17). |
| 1273 | `InfrastructureScreen.BILL_INFO` | `"The month's lorry bill is what the tonnes that crossed the city's boundary "...` | The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). |
| 1370 | `InfrastructureScreen.RELIEF_INFO` | `String.format("A tonne that leaves by train does not drive across the city to...` | The relief's (i) (P15). |
| 1405 | `InfrastructureScreen.BIGGER_INFO` | `"It bills under three quarters of what the rule allows.It is allowed to " + "...` | The too-big railway (P16). |
| 1497 | `InfrastructureScreen.BAND_INFO` | `"Every traded good's price has a cost of MOVING it inside the gap between " +...` | The bars' (i) (P18 and P20, for bars rather than a grid). |
| 1636 | `InfrastructureScreen.LORRY_INFO` | `String.format("A vehicle moves about %,.0f tonnes a month and lasts %.0f year...` | The lorries' (i) (P23). |
| 1727 | `InfrastructureScreen.HAULAGE_QUOTE_INFO` | `String.format("What it charges for a tonne's city leg, from the depot or the ...` | Haulage's quote (i) (FR4). |
| 1736 | `InfrastructureScreen.TRUCKS_INFO` | `String.format("A truck carries about %,.0f tonnes a month.Haulage owns the fl...` | The trucks' (i) (FR4). |
| 1744 | `InfrastructureScreen.DEPOTS_INFO` | `String.format("A Truck Depot keeps trucks, and its posts are the drivers and ...` | The depots' (i) (FR4). |
| 1751 | `InfrastructureScreen.PORTS_INFO` | `"The sea terminals are the city's: only the player builds them.Freight works ...` | The ports' (i) (FR4). |
| 1758 | `InfrastructureScreen.BERTHS_INFO` | `"A berth handles one kind of cargo: liquid bulk, dry bulk, containers or " + ...` | The berths' (i) (FR4). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 120 | `String infraPage` |  |
| 123 | `String freightDivision` | Which of the Freight page's divisions is open - All, Haulage or Rail; the Ports are a page of their own - kept while the game runs, not saved (FR4, as detailsOpen). |
| 126 | `private final java.util.Set<String> detailsOpen` | Which "details" folds are open, by page - kept while the game runs, not saved (0.7.29). |
| 129 | `private javafx.scene.control.ScrollPane body` | The page's scroller, for a part of a picture that scrolls the page to where it is decided (Transit's fare). |
| 721 | `private final Label useLabel, flowLabel, xLow, xHigh, yHigh, yLow, over` |  |
| 723 | `private final double dotX, dotY` | Where the dot's drops meet the axes. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 1939 | **type** `final class InfrastructureScreen` | The infrastructure tab: the roads, the trams, the freight and the ports - four pages under the five figures the whole tab is about, each page led by the one picture that answers its question. |
| 49 | 1 | `InfrastructureScreen(UserInterface ui)` |  |
| 52 | 9 | `void open(String page)` | The tab opened on one of its pages, at the top of it (0.7.28): Services' road card, Build's road and transit doors, a figure or a part of a picture on this tab. |

### INFRASTRUCTURE - the roads, the trams, the freight and the ports (lines 62-260)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 7 | `static String streamColour(Traffic s)` | The three streams' colours - categories, never verdicts: commuters the people teal, goods the business violet, bulk the ore sand. |
| 150 | 7 | `static String streamIcon(Traffic s)` | ...and their icons: a person, a shop, a plant. |
| 159 | 1 | `String reliefPage(Traffic s)` | The page that relieves a stream: transit for the commuters, freight (its railway) for the freight. |
| 162 | 8 | `void openDivision(String division)` | A division of the freight business opened (FR4): the Ports are their own page, the others the Freight page on that division. |
| 171 | 47 | `void showInfrastructureMenu()` |  |
| 227 | 16 | `HBox head()` | The head: "Infrastructure" with the people teal's swatch, and at its right the two doors every page has - Build's Infrastructure (Roads & transit until FR4), opened on the ring this page explains (transit's on Transit... |
| 250 | 10 | `HBox divisionStrip()` | The division strip (FR4), under the page chips on the Freight and Ports pages: All, Haulage, Rail and Ports, the open one lit. |

### THE FIVE FIGURES (0.7.29; four until then) (lines 261-349)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 40 | `HBox vitals(List<CityNeeds.Need> all)` |  |
| 315 | 4 | `static String flowWords(InfrastructureManager roads)` | FLOW's note: the road's status in a word, with what it costs once it bites. |
| 321 | 14 | `String flowChange()` | FLOW's change on last month, from History's road throughput (its last two months kept): "▼ 0.6 pts on last month"; null with fewer than two. |
| 337 | 3 | `static CityNeeds.Served roadServed(InfrastructureManager roads)` | What the road serves and the one verdict on it (0.7.41) - the road's one judge, as every road gauge reads it; its colour was NEEDS YOU's ROADS row's level, amber while road sites were on the way, until then. |
| 342 | 1 | `static String pct(double share)` | A share as a whole per cent, as Build's rings write it. |
| 345 | 1 | `static String moneyOr(double thousands)` | Money, or a dash for a figure the model does not know yet (the railway's allowed bill and its split, after loading a save from before 0.7.29, until a month runs). |

### the screen's own pieces (lines 350-433)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 6 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 361 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 369 | 9 | `static HBox caption(String text, String info)` | A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. |
| 380 | 9 | `static javafx.scene.layout.FlowPane pairLine(String a, String aTone, String aWords, String b, String bTone, String bWords)` | "62% served → 56% flow" ("162% full" until 0.7.41): the two figures at 28 px, the words between them smaller. |
| 391 | 9 | `static HBox cardHead(String svg, String colour, String name, String info)` | A card's head: its icon in a square tinted in its colour, its name, and its (i). |
| 402 | 10 | `static javafx.scene.layout.FlowPane figureLine(Object...parts)` | A line of figures and words that wraps: "102k trips · 135k on the road". |
| 414 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 422 | 11 | `void scrollTo(Node target)` | Scroll the page to a node on it (Transit's "the fare" step, which opens the fare card). |

### ROADS (0.7.29) (lines 434-854)

| line | len | member | says |
|---:|---:|---|---|
| 447 | 11 | `void roadsPage(VBox page, List<CityNeeds.Need> all)` |  |
| 460 | 28 | `VBox roadHeroLeft(InfrastructureManager roads, List<CityNeeds.Need> all)` | The hero's left: THE ROAD, both figures as a pair, what the flow costs, the status chip, the curve and what is on site. |
| 503 | 6 | `static String congestedWords(double flow)` | The congested alert's words (P5), now the (i)'s second half. |
| 511 | 55 | `VBox roadWalk(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | The hero's right: FROM TRIPS TO THE ROAD, five rows on one scale with the capacity and the free-flow line through them. |
| 568 | 11 | `List<Run> streams(double[] amounts)` | The three streams laid end to end from nothing, each a door to the page that relieves it. |
| 608 | 28 | `javafx.scene.layout.GridPane streamCards(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | A card a stream: what it makes and puts on the road, what gets through, what a trip costs, and what relieves it. |
| 638 | 17 | `HBox networkStrip(InfrastructureManager roads)` | The network in four figures: what it can carry, the room before it slows (on the load the curve reads), the lorry share, the cars. |
| 657 | 49 | `VBox roadsStatement(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk)` | The old Roads page, line by line, in its fold: the network, the streams' grid and the cars (B3's room and B6's costs fixed). |
| 718 | 136 | **type** `static final class FlowCurve extends javafx.scene.layout.Pane` | The flow curve (0.7.29): the road's flow against what it serves (0.7.41; against how full it was until then), from nothing to CURVE_MAX - the model's own curve (InfrastructureManager.throughputAtServed()) as a line ov... |
| 725 | 92 | `FlowCurve(double served, double then, String tone)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 818 | 7 | `private static Label label(String text, String tone)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 826 | 1 | `private static double x(double served)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 828 | 3 | `private static double y(double flow, double span)` _(in InfrastructureScreen.FlowCurve)_ |  |
| 832 | 16 | `protected void layoutChildren()` _(in InfrastructureScreen.FlowCurve)_ |  |
| 849 | 4 | `private static void place(Label l, double x, double y)` _(in InfrastructureScreen.FlowCurve)_ |  |

### TRANSIT - and the one control on this tab (0.7.29) (lines 855-1195)

| line | len | member | says |
|---:|---:|---|---|
| 867 | 43 | `void transitPage(VBox page)` |  |
| 928 | 10 | `static String carsRideInfo(InfrastructureManager roads, TaxPolicy policy)` | The owners' row's (i) (P8, rewritten for 0.7.49): what a ride costs against a journey's fuel and the share that choose the bus on it (InfrastructureManager.ownersChoosingAt()), and what the remembered commute adds (ow... |
| 940 | 38 | `VBox transitFunnel(InfrastructureManager roads, TaxPolicy policy, BuildAdvice.Measure transit)` | The funnel: commuters, the three ceilings with the lowest tagged, and the riders by reason (0.7.49) - on one scale, the commuters full. |
| 985 | 22 | `List<ScaleRow> ridersByReason(InfrastructureManager roads, TaxPolicy policy)` | THE RIDERS BY REASON (0.7.49), under the ceilings: the commuters with no car of their own - in reach, riding, and walking, out of reach or with no seat - then the owners - in reach, and who chose the bus - and those w... |
| 1015 | 21 | `VBox transitBooks(EconomyManager econ)` | WHAT IT COSTS THE CITY: the bill as a bar, the fares in it, and the net - neutral, with signs: a net cost is a policy, not a failure. |
| 1046 | 36 | `VBox fareCard(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy)` | THE FARE on its dial card (0.7.38, the Policy spec's D18 step 6), as Policy draws every dial: the reading and what a month of riding costs, the ladder, and under it what the fare would do, before and after, following ... |
| 1089 | 10 | `String fareWeighs(InfrastructureManager roads, TaxPolicy policy)` | The fare card's third line (0.7.49): a drive's fuel a journey, as the month struck it at the exchange rate, and a month's pass as a share of an unskilled household's take-home - its row's take-home over its households... |
| 1101 | 5 | `static String fareInfo(TaxPolicy policy)` | The fare's (i) with the dial behind it (0.7.45): its founding figure and the level it is charged at. |
| 1120 | 17 | `java.util.function.DoubleFunction<List<Pieces.Effect>> fareEffects(InfrastructureManager roads, EconomyManager econ)` | What the fare would do at any value of its thumb (pure: the probe reads them), each the model's read at that fare against this month's: the riders (InfrastructureManager.ridersAt()) and the car owners among them (choi... |
| 1139 | 4 | `static String trips(double n)` | Trips put onto the road, signed with a true minus for trips taken off it: "+1,240 trips", "−310 trips", "none". |
| 1157 | 38 | `VBox transitStatement(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy)` | The old Transit page, line by line, in its fold. |

### THE RAILWAY - the twelfth sector, and the only mode the city does not (lines 1196-1452)

| line | len | member | says |
|---:|---:|---|---|
| 1207 | 46 | `void railwayPage(VBox page)` |  |
| 1282 | 34 | `VBox freightBill(ham.citybuildersim.sectors.Freight rail)` | The month's freight bill, three ways: one bar as long as the lorry bill, its key with the figures, and the fuel. |
| 1318 | 30 | `VBox trackCard(ham.citybuildersim.sectors.Freight rail)` | TRACK AND TRAINS: the track, the sets against what it needs, what it can carry, and the trains it is short or the ones it wears out. |
| 1350 | 18 | `VBox haulsCard(ham.citybuildersim.sectors.Freight rail)` | WHAT IT HAULS: what crossed and what it took, each stream's share, and what that takes off the road. |
| 1378 | 25 | `VBox businessCard(ham.citybuildersim.sectors.Freight rail)` | THE BUSINESS: billed against what the rule allows, the fuel, the net income, the capital - and the door to its books. |
| 1412 | 40 | `VBox railwayStatement(ham.citybuildersim.sectors.Freight rail, InfrastructureManager roads)` | The old railway page, line by line, in its fold. |

### FREIGHT - the band, the bill, and the lorries (0.7.29) (lines 1453-1703)

| line | len | member | says |
|---:|---:|---|---|
| 1464 | 31 | `void freightPage(VBox page)` |  |
| 1511 | 17 | `VBox bandKey()` | The bars' key. |
| 1530 | 2 | **type** `record GoodBar(Good good, double margin, double abroad, double rail, double byLorry, double delivered, doub...` | One good's bar, worked out without drawing it: its shares of today's import price, and its words. |
| 1534 | 23 | `List<GoodBar> goodBars()` | Every good the city can both buy and sell, as GoodBar - bulk first, then the rest, each by its delivered share, the widest first. |
| 1559 | 23 | `Node goodsBars()` | The bars: a caption a group, a row a good on one scale - today's import price, or more if a good's gap is wider. |
| 1584 | 50 | `VBox lorriesCard()` | The lorries: one line while nobody is short; a bar a short sector otherwise. |
| 1649 | 4 | `static boolean shownFleet(Sector s)` | Whether a sector's fleet is worth a row: it moves a tonne or owns or needs a lorry - and not a row of noughts (spec bug B11: a fraction of a tonne or of a van got past the old test and printed "0 · 0 · 0 · 100%"). |
| 1655 | 37 | `VBox freightStatement()` | The old Freight page's two grids, in its fold. |
| 1698 | 5 | `static String abroadWords(double bill, double imported, String whole)` | How much of a fuel bill was bought abroad (0.7.62), after the bill's own words: all of it, `whole` (", bought abroad"); part, ", $1.2M of it bought abroad"; none, ", off the city's refineries". |

### FREIGHT AND PORTS (FR4, 2026-10-19) (lines 1704-1772)

| line | len | member | says |
|---:|---:|---|---|
| 1764 | 8 | `static String cargoColour(Ports.Cargo kind)` | A kind of cargo's colour on this screen: categories, never verdicts. |

### Haulage (lines 1773-1853)

| line | len | member | says |
|---:|---:|---|---|
| 1776 | 33 | `void haulagePage(VBox page)` | The Haulage division: the quote, the month's city leg, and the trucks, the depots and the business against them. |
| 1811 | 13 | `VBox trucksCard(FreightView.HaulagePage h)` | THE TRUCKS: owned against what the plant asks, the ones it is short, the ones it wore out. |
| 1826 | 14 | `VBox depotsCard(FreightView.HaulagePage h)` | THE DEPOTS: how many stand, the trucks they keep, and the ones parked on the street. |
| 1842 | 11 | `VBox haulageBusinessCard(ham.citybuildersim.sectors.Freight freight, FreightView.HaulagePage h)` | THE BUSINESS: billed against what the rule allows, the diesel, the share of the need its trucks reach, and the door to the books. |

### Ports (lines 1854-1982)

| line | len | member | says |
|---:|---:|---|---|
| 1857 | 29 | `void portsPage(VBox page)` | The Ports page: the month's handling and dues, the berths by kind, then each kind's bill and the ships that called. |
| 1888 | 14 | `VBox berthRow(FreightView.Berth b)` | One kind of berth: its name, how many stand and the room they are, and the month's tonnes against it. |
| 1904 | 23 | `VBox portBillCard(FreightView.PortsPage p)` | HANDLING AND DUES, BY KIND: what a tonne is billed, what the kind billed, and the dues it paid the city. |
| 1929 | 24 | `VBox shipsCard(FreightView.PortsPage p)` | THE SHIPS THAT CALLED: the month's calls by ship class, and what a call carries. |
| 1955 | 7 | `java.util.Map<Ports.Ship, Integer> callsByShip()` | The month's calls by ship class, in the classes' order, those with none left out. |
| 1970 | 12 | `void openMapAtPort()` | "The ships on the map": the land office's map over the window on the city's terminals (the same door the minimap opens, UserInterface .openMapOn()) - the box round every berthed work on the shore, its quay's end inclu... |


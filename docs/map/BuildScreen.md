# BuildScreen.java - 5,687 lines · 191 methods · 21 constants · interface

`ham/citybuildersim/ui/BuildScreen.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The build tab: the Overview it opens on and the city's five categories
> opened on their needs (both 0.7.24), the market's nine in their groups
> (0.7.25), the strip of categories, the constraints bar that says what
> stops a build, the line that says who builds these, and every building as
> the one card (0.7.25) - its head, hero, bars, investors' line, needs,
> order line and stats - with the stat card's own vocabulary for what a
> building does and what care it gives.
> 
> Split out of UserInterface on 2026-09-18, the shell's members reached
> through ui: BUILD: THE CATEGORY SCREEN IS GONE TOO, THE HALF OF THE
> CATALOGUE THAT BUILDS ITSELF, THE BUILD MENU, ONE BUILDING, AS A CARD and
> THE STAT CARD came over then; THE OVERVIEW and A CITY CATEGORY, OPENED ON
> ITS NEEDS joined them in 0.7.24, and A MARKET CATEGORY, IN ITS GROUPS in
> 0.7.25, when ONE BUILDING, AS A CARD became the one card for every
> building. The shell still reads
> which category is open (buildCategory) for the rail and the scroll memory,
> and the inbox and the panels send the player into a category through
> openCategory(), which lands in handleAllBuildingMenus(). BuildMenuCheck
> reads the three card methods, and since 0.7.24 the pages through the
> window's buildPages() and buildOpensOn().
> Since 0.7.5 the shell's key filter also calls buildPending() and
> clearPending(): Enter, and Backspace or Delete, on the page showing.

**Uses:** [Palette](Palette.md) (448), [BuildAdvice](BuildAdvice.md) (153), [BuildCard](BuildCard.md) (108), [Pieces](Pieces.md) (74), [BuildingsTemplate](BuildingsTemplate.md) (74), [CityNeeds](CityNeeds.md) (72), [Game](Game.md) (46), [BuildingType](BuildingType.md) (45), [Icons](Icons.md) (26), [LandManager](LandManager.md) (26), [Good](Good.md) (24), [AutoBuilder](AutoBuilder.md) (21), [Resource](Resource.md) (15), [LandmassView](LandmassView.md) (13), [LandParcel](LandParcel.md) (11), [JobType](JobType.md) (11), [Refining](Refining.md) (9), [Auction](Auction.md) (8), [CareType](CareType.md) (8), [ConstructionControl](ConstructionControl.md) (7), [Ports](Ports.md) (7), [Landmasses](Landmasses.md) (6), [RefineryFlow](RefineryFlow.md) (5), [Forestry](Forestry.md) (5), [CapChips](CapChips.md) (4), [LandMap](LandMap.md) (4), [Formats](Formats.md) (4), [Retail](Retail.md) (4), [Health](Health.md) (4), [DebtQuote](DebtQuote.md) (4)... and 33 more

**Used by (14):** [BankScreen](BankScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 78 | BUILD: THE CATEGORY SCREEN IS GONE TOO. |
| 101 | THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. |
| 455 | THE BUILD MENU |
| 590 | · THE BUILD SHORTCUT (0.7.61, spec-land star 14) |
| 708 | ONE BUILDING, AS A CARD. |
| 835 | · PAVING, ON THE GRAVEL ROAD'S CARD (0.7.70) |
| 1582 | THE MATERIALS' LINES (0.7.142, batch I13; docs/studies/spec-materials.md |
| 2152 | · the keyboard |
| 2244 | THE OVERVIEW (0.7.24) |
| 2337 | · the city's job |
| 2556 | · what would help most |
| 2914 | · AUTOMATIC BUILDING (0.7.73) |
| 3252 | · the market builds these |
| 3292 | A CITY CATEGORY, OPENED ON ITS NEEDS (0.7.24) |
| 3897 | A MARKET CATEGORY, IN ITS GROUPS (0.7.25) |
| 4082 | THE STAT CARD |
| 4667 | · the last purchases (0.7.20) |
| 5208 | ONE FUNDING PAGE FOR THE RUN (0.7.40) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 211 | `BuildScreen.BUILD_HOME` | `BuildAdvice.OVERVIEW` | Where Build opens (BUILD_HOME), and which category the player was last looking at (buildCategory). |
| 383 | `BuildScreen.CITY_DOT` | `Palette.MONEY` | The colour of "only the city builds these": the money blue - the city's own account. |
| 386 | `BuildScreen.INVESTOR_DOT` | `Palette.BUSINESS` | The colour of "investors build these too": the business violet. |
| 612 | `BuildScreen.LAND_FREE_CELL` | `236` | LAND FREE's width with its shortcut under it: the cell's own 190 and room for the shortcut's words on one line. |
| 911 | `BuildScreen.PAVE_HEAD` | `"PAVE TO A PAVED ROAD"` | The paving's heading on the Gravel Road card. |
| 914 | `BuildScreen.PAVE_TAG` | `"paving beats a new Paved Road"` | ...its tag, when paving beats a new Paved Road over its life a trip. |
| 917 | `BuildScreen.PAVE_INFO` | `"Paving lays a Paved Road on a gravel road's ground: a Paved Road's price, le...` | ...and the (i) on its two figures: what it costs and why it may win. |
| 1027 | `BuildScreen.ROAD_TAG` | `"cheapest per trip over its life"` | A road card's first tag (0.7.70): the cheapest a trip off the road over its life, its ground in it - BuildCard.ROAD_PER, shortened. |
| 1030 | `BuildScreen.ROAD_OFF` | `"trip off the road"` | ...and what its land bar is per: a trip it takes off the road (BuildAdvice.unit()). |
| 2277 | `BuildScreen.JOB_RING` | `58` | A ring's size on the Overview's tiles (its stroke is 6 px). |
| 2943 | `BuildScreen.AUTO_CHIP` | `"Automatic building on ›"` | Build's heading chip on every page but the Overview while it is on: the way back to its cards. |
| 2946 | `BuildScreen.AUTO_INFO` | `"Turned on, it orders every month what this page advises for the city's works...` | The section's (i). |
| 2972 | `BuildScreen.AUTO_DIAL` | `(1389 - Palette.RAIL - 36 - 20) / 3.0 - 24` | A dial's width in its card at the 1,389 window: a third of the page less the card's padding. |
| 3113 | `BuildScreen.AUTO_CAP_TITLE` | `"THE HIGHEST YIELD ITS BOND WILL TAKE"` | The cap block's caption and (i) on automatic building's debt card (CD2, D28 and D30). |
| 3114 | `BuildScreen.AUTO_CAP_INFO` | `"The most automatic building will pay to sell the " + Game.BUILD_BOND_YEARS +...` |  |
| 3124 | `BuildScreen.AUTO_CAP_LINE` | `"A sale that falls short at the cap keeps its ask open, offered again every m...` | The line under the chips: what a short sale does here (D30). |
| 3128 | `BuildScreen.ASKS_INFO` | `"An order whose bond sold short at the cap waits here for its money, and the ...` | THE OPEN ASKS' heading (i). |
| 3336 | `BuildScreen.NEED_CARD` | `300` | A card's width, on every Build page since 0.7.25 (a city category's only, in 0.7.24). |
| 4676 | `BuildScreen.RECEIPTS` | `5` | How many purchases the receipt keeps. |
| 4977 | `BuildScreen.PAYBACK_LINE` | `2 * NEED_CARD` | The pay-back line's width on the no-deposit page (0.7.101): two of Build's need cards, about as wide as the WHY section's hand-wrapped lines above it. |
| 5463 | `BuildScreen.FUND_CAP_INFO` | `"The previews above are the auction for the cash this run is short of, at the...` | The cap block's (i) on the funding page (CD2, D30). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 62 | `private final CapChips autoChips` | Automatic building's cap chips (CD2, D28): its own cap, AutoBuilder.getCap(). |
| 65 | `private final CapChips fundChips` | The funding page's cap chips (CD2, D30): the cap its previews are quoted at, which starts as the builder's. |
| 68 | `private Auction.Cap fundCap` | The cap the funding page's previews were last set to by its chips; null until the player moves them, and then the builder's cap is the one used. |
| 71 | `private Runnable fundingAgain` | Draws the funding page again as it stands, for its chips; set as the page is drawn. |
| 212 | `String buildCategory` |  |
| 257 | `private String landOn` | The building openOnBuilding() is landing on while the page is drawn, and its card once card() has made it. |
| 258 | `private StackPane landCard` |  |
| 764 | `final java.util.Set<String> pinnedStats` | Which cards are showing their stats, by building name. |
| 767 | `final java.util.Map<String, Integer> orderQty` | How many of each the player has dialled up, by building name. |
| 838 | `int paveQty` | How many gravel roads the card's paving is set to: apart from orderQty, so Enter and the order bar never place it. |
| 2175 | `final List<PageCard> pageCards` | The cards on the category page showing now, in the order they are laid out; each draw starts it again. |
| 2178 | `private String pageTitle` | Which page those are on, so an order placed from the keyboard comes back to it. |
| 2179 | `private EnumSet<BuildingType> pageCategories` |  |
| 2182 | `private Label pendingHint` | The caption under the grid that says the two keys; shown only while something on the page is pending. |
| 3333 | `final java.util.Map<String, BuildAdvice.Measure> measurePicked` | Which ring each city category has picked, by name; none picks its worst. |
| 3339 | `private VBox orderBar` | The order bar, refilled in place as a stepper moves. |
| 3342 | `private BuildAdvice.Measure orderMeasure` | The measure the order bar reads, the picked one; null on a market page (orderMarket). |
| 3924 | `private boolean orderMarket` | Whether the order bar is a market page's, which has no measure to draw. |
| 4686 | `final java.util.ArrayDeque<Receipt> receipts` | The last RECEIPTS purchases, newest first. |
| 4689 | `private javafx.stage.Popup receiptPopup` | The receipt's popover while it is open, or null. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 5640 | **type** `final class BuildScreen` | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.25), the strip of categories, the constraints bar that says what stops... |
| 53 | 7 | `BuildScreen(UserInterface ui)` |  |
| 74 | 3 | `Auction.Cap fundingCap()` | The cap the funding page quotes at: the player's if they moved the chips on this page, otherwise automatic building's. |

### BUILD: THE CATEGORY SCREEN IS GONE TOO. (lines 78-100)

| line | len | member | says |
|---:|---:|---|---|
| 99 | 1 | **type** `record BuildCategory(String name, EnumSet<BuildingType> types)` | One tab on the build strip: what it is called and what it contains. |

### THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. (lines 101-454)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 8 | `static EnumSet<BuildingType> investorTypes()` |  |
| 134 | 3 | `static boolean investorBuilt(EnumSet<BuildingType> types)` | True when everything in this category is something investors put up. |
| 145 | 4 | `static EnumSet<BuildingType> industrialTypes()` | What a private business builds to sell something. |
| 161 | 3 | `static EnumSet<BuildingType> utilityTypes()` | ...and what the city runs because everything else needs it. |
| 189 | 6 | `static BuildCategory[] buildCategories()` | The strip, since 0.7.24 in BuildAdvice's order: the five only the city builds - Utilities, Infrastructure (its roads and transit, its ports and its Strategic Reserve since FR4), Healthcare, Education, Safety - then th... |
| 215 | 3 | `void showBuildMenu()` | The Build tab: the Overview, or whichever category you were last in. |
| 226 | 9 | `void openCategory(String name)` | A category, by its name - or by its label before 0.7.24, which lands in the same place - or the Overview. |
| 243 | 12 | `void openOnBuilding(String category, String buildingName)` | A category opened on one building's card (PT1): the Bank page's "Build a Commercial Bank" landed at the top of Shops while the bank's card is the last on the page, two scrolls down. |
| 261 | 11 | `private void bringToTop(javafx.scene.Node target)` | The page brought to a card: the menu's scroller set so the card sits near the top (LandScreen's way). |
| 279 | 9 | `void openOn(BuildAdvice.Measure m)` | A city category opened on one ring (0.7.28): the Services screen's "Build for it ›" doors - opened as openCategory() opens it, with this measure picked rather than its worst. |
| 295 | 10 | `void why(BuildAdvice.Measure m)` | ...and the way back to the why (0.7.28): the "why ›" at a ring's heading opens the Services page that explains it - the road and transit rings, Infrastructure's, behind "what is on the road" and "who rides" since 0.7.29. |
| 327 | 54 | `javafx.scene.layout.FlowPane buildStrip(String current)` | SINCE 0.7.24 it shows on a category page only, not on the Overview, and reads "‹ Overview", then the city's five with their blue dots, a rule, then the market's nine with their violet ones, left-aligned under the page... |
| 389 | 9 | `static Region whoDot(boolean investors)` | A tab's dot: who builds what is behind it (0.7.21). |
| 400 | 14 | `HBox keyLine()` | The key to the dots, at the right of the page's title: "● only the city builds  ● investors build too". |
| 424 | 29 | `HBox buildHead(String menuTitle, EnumSet<BuildingType> categories)` | The page's head (0.7.21): its title with the building area's swatch, the key to the tabs' dots at the right, the way to the construction page (0.7.22), and the receipt's dot beyond it. |

### THE BUILD MENU (lines 455-589)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 18 | `void handleAllBuildingMenus(String asked, EnumSet<BuildingType> categories)` | A Build page by its name: the Overview, one of the city's five opened on its needs (showCityCategory()), or one of the market's nine in its groups (showMarketCategory(), 0.7.25). |
| 515 | 20 | `HBox whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories)` | One line under the strip saying whether this is your job. |
| 552 | 37 | `HBox constraintsBar()` | WHAT STOPS A BUILD, across the top, above the categories. |

### THE BUILD SHORTCUT (0.7.61, spec-land star 14) (lines 590-707)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 40 | `VBox landFreeCell(double free, double used)` | LAND FREE: the ground free in km2, the share used, a door to the land office, and the shortcut to buy the best land - and with a zone standing (0.7.109, batch Z2), "free for anyone" in its note and a door under it to ... |
| 662 | 6 | `String zonedWords()` | LAND FREE's zoned line (0.7.109): "zoned for Iron Mining 1.6 km² ›" for one zone, "zoned for 3 industries 4.0 km² ›" for more - what they hold, free for them alone; null with no zone. |
| 670 | 3 | `static String bestLandWords(LandParcel p)` | The shortcut's words: "Buy the best: North 3 · 0.0288 km² · US$12.1M ›" - the offer's place (0.7.69, spec-grid 2.5), its size and its listed price. |
| 675 | 6 | `static String bestLandTip(LandParcel p, boolean funding)` | ...and its tooltip: which offer, why it is the best, and what a short city's click opens. |
| 683 | 9 | `void buyBestLand(LandParcel p, Runnable after)` | Buys an offer as the land office's Buy would, then `after`; short, the land office's funding page for it. |
| 694 | 8 | `Pieces.ActionButton bestLandButton(LandParcel p, String words, String sub, Runnable after)` | A refusal page's Buy: Pieces' action button, outlined and "on credit" when the city is short. |
| 704 | 3 | `static String groundTone(int level)` | LAND FREE's colour: the GROUND row's verdict, amber or red, and the strip's plain figure while it is fine. |

### ONE BUILDING, AS A CARD. (lines 708-834)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 56 | `StackPane card(BuildCard.Figures f, BuildCard.Group group, BuildAdvice.Category c, BuildAdvice.Measure m)` | One building as a card, on a city category's page (its picked ring's group) or a market category's (its owning sector's group). |

### PAVING, ON THE GRAVEL ROAD'S CARD (0.7.70) (lines 835-1581)

| line | len | member | says |
|---:|---:|---|---|
| 852 | 57 | `VBox pavingBlock(BuildingsTemplate gravel, String page, EnumSet<BuildingType> types)` | PAVE THEM (0.7.70; ConstructionControl, F). |
| 928 | 16 | `String[] pavingWords(BuildCard.Paving p, int n)` | The paving's words (0.7.70), worked out without drawing them (a probe reads them): {what one does and costs, its figure over its life against a new Paved Road's, the quote's line for n, "of N to pave"}. |
| 951 | 10 | `Pieces.Press pavePress(BuildCard.Paving p, int n)` | The paving's button (0.7.70): "Pave" and "choose how many" with none chosen; "Pave 3 · $85.1k" ready; held with nothing to pave, or short of the cash - paving is paid out of the treasury (Game.paveRoads()). |
| 963 | 6 | `static javafx.scene.text.Text textRun(String s, String font, double size, String colour)` | A run of words in a TextFlow, at a size and in a colour - so a line wraps where it must and never ends in "...". |
| 971 | 7 | `static javafx.scene.text.TextFlow flow(javafx.scene.Node...runs)` | ...and a TextFlow of them, as wide as a card's face. |
| 987 | 27 | `HBox cardHead(BuildCard.Figures f, String svg, String dot, boolean afford)` | The head: an icon square in who-builds-it's colour - the strip's dots, blue for the city's five, violet for the market's nine - the name, faint when one costs more than the cash, "you have N" or "none built", and what... |
| 1019 | 6 | `javafx.scene.layout.FlowPane cardTags(BuildCard.Figures f, BuildCard.Group group)` | The tags: the best of the group on each bar (BuildCard.Group.best1() and best2()), in a flow so two never cut each other. |
| 1033 | 16 | `static List<String> tagWords(BuildCard.Figures f, BuildCard.Group group)` | The tags' words, for the bars the card is the best of: "cheapest per resident", "adds the most per $". |
| 1056 | 21 | `VBox heroRow(BuildCard.Figures f)` | The hero: what the building gives the city, in its own verb, a big figure and its unit - "houses 252 residents", "makes 1,200 t of steel a month", "puts 87 officers on the street · 120 fully staffed" - and a smaller l... |
| 1085 | 75 | `String heroDetail(BuildCard.Figures f)` | The hero's smaller line, by kind: a home's doors and who may live behind them; the branch's capital; the builders' output; an office's exports at the price of a seat; a maker's other goods, what it uses and what it ad... |
| 1167 | 14 | `static String crudeUnitWords(java.util.Map<Good, Double> made)` | What a crude unit's crude becomes, for its card's line (0.7.76): the petrol and diesel the city burns, and the rest it ships, in litres - "into 580,170 L of petrol and 1,402,078 L of diesel, and 7.7M L of four product... |
| 1188 | 33 | `static List<String> conversionUnitLines(BuildingsTemplate t)` | What a refinery's conversion unit is, in sentences, before its products (0.7.80, batch O4): its size and its feed, where the feed comes from, what its kind needs or does that the others do not, and when it runs - off ... |
| 1223 | 8 | `static String goodsList(java.util.Map<Good, Double> goods)` | Goods and their counts: "1,320 t of iron ore", "5,000 kg of dairy and eggs and 1,400 kg of meat". |
| 1233 | 12 | `HBox priceRow(BuildCard.Figures f, boolean afford)` | The price all in, green when the cash covers it and red when it does not, with the sticker smaller. |
| 1256 | 19 | `List<javafx.scene.Node> cardBars(BuildCard.Figures f, BuildCard.Group group, BuildAdvice.Measure m)` | The two bars. |
| 1277 | 25 | `static String[] barWords(BuildCard.Figures f, BuildAdvice.Measure m)` | The bars' labels and figures: {label 1, figure 1, label 2, figure 2}, label 2 null for no second bar. |
| 1304 | 4 | `static String landWords(double sqFt)` | The land a bar reads, kept in square feet, as every area reads since 0.7.68 (LandManager.areaWords(): "23.2 m²", "0.0743 km²"); a dash for none. |
| 1320 | 28 | `HBox investorsLine(BuildingsTemplate t)` | THE INVESTORS' LINE, on a market card: if investors have an order on its site, that, with the count and the wait; otherwise the sector's own word for the month (Game.getLastInvestment(), filed per sector, so it can na... |
| 1354 | 11 | `String depotWords(BuildCard.Depot d, BuildCard.Investors inv)` | The Truck Depot's line after "Freight" (FR4, the study's board 4): while a depot of it is on site for investors it is building (investorsWords()' own count and wait); else whether Freight would build one now - the tru... |
| 1367 | 6 | `static String tonnesAYear(double tonnes)` | Tonnes a year as the berth cards say them: "2.4 Mt a year", "310 kt a year", "850 t a year". |
| 1385 | 53 | `HBox paybackLine(BuildAdvice.BerthPayback p)` | A SEA TERMINAL'S PAYBACK LINE (FR4; the study's board 4: "a line ports never had"). |
| 1448 | 32 | `VBox portChooser(BuildingsTemplate template, Runnable after)` | WHERE A SEA TERMINAL STANDS, once the city owns shore on more than one landmass (ET5, 2026-10-25): a chip for each landmass with shore (Game.portLandmassChoices(), home first; an island no port links says so), the one... |
| 1486 | 9 | `void openPortCardFor(int landmass)` | "Build a port there" (ET5): the sea terminal's card on the Infrastructure page, with the landmass chosen for its next order (Game.choosePortLandmass(): one order's), so that a notice's button and the no-land page's do... |
| 1497 | 5 | `String noCoastWhy(BuildingsTemplate t, String old)` | NO_COAST's reason for an order in the words of the old refusal (`old`), or - once the city owns ground on more than one landmass - on the landmass the card picked (LandmassView.noShoreQuote()). |
| 1504 | 7 | `static String yearsWords(double years)` | Years as a payback says them: "11 years", "1.5 years", "over 100 years". |
| 1516 | 19 | `String[] investorsWords(BuildCard.Investors inv)` | The line's words after "Investors": {what they are doing or last said, " · this one: " and the gate - or null}. |
| 1537 | 27 | `String gateWords(BuildCard.Gate gate)` | A gate, in the words its own refusal uses: the deposit, the licence, the staffing test's why, landReason()'s land, the estimate's loss, and consider()'s interest on its whole cost (0.7.122). |
| 1573 | 8 | `static String interestWords(BuildCard.Gate gate)` | An INTEREST gate in words (0.7.122, batch C1): what one would earn a month against PROFIT_OVER_INTEREST times its interest on its whole cost - a month's interest and the rate it is struck at, the real one the test rea... |

### THE MATERIALS' LINES (0.7.142, batch I13; docs/studies/spec-materials.md (lines 1582-2151)

| line | len | member | says |
|---:|---:|---|---|
| 1600 | 11 | `VBox materialsBlock(BuildingsTemplate t)` | The block: one label a line, each {words, colour} of materialsWords(). |
| 1613 | 19 | `List<String[]> materialsWords(BuildingsTemplate t)` | A materials building's lines, worked out without drawing them (a probe reads them): each {words, colour}, in the block's order. |
| 1640 | 24 | `static String[] standsWords(BuildCard.Stands at)` | Where it stands: "stands on 20 sites of one hematite field · the city's: 4 hematite, 23 magnetite, 3 of its kind spoken for · 161.7M t of hematite ore left · fits" - "does not fit" in red, the field it lacks being the... |
| 1666 | 14 | `static String weighedWords(BuildCard.Weighed w)` | How its sector weighs one: "its whole cost $268.6M, its ground and 2,538 lorries ($173.0M) with it · it would earn $1.2M a month on it, 0.45% a month · the money gate asks $1.5M". |
| 1682 | 9 | `static String powerWords(BuildCard.Power p)` | Its own power: "makes 60,000 kW of its own power: its 51,400 kW draw, and 8,600 kW sold to the grid, $X a month at the tariff". |
| 1693 | 7 | `static String oldMetalWords(BuildCard.OldMetalBought o)` | The old metal it buys: "buys the city's old steel first: 10,500 t a month at $X a tonne · 12,300 t arose last month, 10,500 t of it the yards standing take". |
| 1702 | 15 | `static String outletWords(List<BuildCard.Outlet> outlets)` | Where what it makes goes: "acid to Copper Mining, 3,500 t a month at nameplate · anode to its own works first" - or "nobody here buys lumber: it ships". |
| 1719 | 6 | `static String crewWords(BuildCard.CrewRule rule, BuildingsTemplate t)` | A crew's E: "E: the forest it may cut, 3.5M m³, would last 150 months of the crews' cut with one more; it asks 120 · room for 3 more, 7 in all". |
| 1734 | 6 | `Label needsLine(BuildCard.Figures f)` | What it needs: its posts by job - or "needs no staff" - then, on a market card, the share of them the owner's staffing test says the city could fill, below 99.5%; then its land, red with " - more than is free" when it... |
| 1742 | 20 | `String needsWords(BuildCard.Figures f)` | ...its words. |
| 1764 | 1 | **type** `record Order(HBox steps, Pieces.ActionButton build, Label quoted, Runnable reprice)` | A card's order line: the stepper, Build under it (0.7.34), the quote under that, and how they reprice in place. |
| 1795 | 101 | `Order orderControls(BuildingsTemplate template, String key, String page, EnumSet<BuildingType> types, Runnable after)` | − N + +10 +100 and ↺, then Build, and the quote under them - as the 0.7.21 card had them, on every card now (the city's had lost +100). |
| 1903 | 5 | `static String prepWords(Game.BuildQuote q)` | The quote's line for a foothill site (ET2; Game.BuildQuote's prepSqFt, prepPoints, prepCost): "Preparing 743 m² of foothill: 3.1 points of builders' work, $82.4k" - the ground the order has to cut pads in before its f... |
| 1917 | 13 | `static String worksWords(Game.BuildQuote q)` | The quote's line for a highway or a railway over the city's slopes (ET4; Game.BuildQuote's worksSqFt, worksPoints, worksCost and worksMix): "Cuts, viaducts and tunnels for its 5,853 m² over the city's ground (foothill... |
| 1942 | 3 | `Pieces.Press orderPress(BuildCard.Verdict v, int n)` | What a Build button says for n of a building (0.7.34), off the verdict the quote line reads (BuildCard.verdict(), Game.quoteBuild()): with none chosen, "Build" and "choose how many"; ready, "Build 3 · $37.5M", the cou... |
| 1947 | 21 | `Pieces.Press orderPress(BuildCard.Verdict v, int n, BuildingsTemplate t)` | ...for the building ordered (ET5): once the city owns ground on more than one landmass a sea terminal's NO_COAST names the landmass it was asked for ("No shore on Island 2"), a plant's the linked ground; `t` null, or ... |
| 1977 | 3 | `String[] quoteVerdict(BuildCard.Verdict v)` | The quote's verdict and its colour, in buildStack()'s order: no deposit, no coast (0.7.59) and nobody licensed in red - warned here since 0.7.25, where they were found only after the click - then short of land in red,... |
| 1982 | 10 | `String[] quoteVerdict(BuildCard.Verdict v, BuildingsTemplate t)` | ...for the building ordered (ET5): NO_COAST on a city that owns ground on more than one landmass says which ("no shore on Island 2"); `t` null, or a city on one landmass, is the old words. |
| 1996 | 24 | `Label infoDot(String key, VBox cover)` | The (i): hover shows the stat cover over the whole card, a click keeps it there (the dot turns blue), a second puts it away. |
| 2035 | 84 | `VBox statCover(BuildingsTemplate t, BuildCard.Figures f)` | The stat cover, as tall as the card it covers: what the building does in sentences (whatItDoes(), which BuildMenuCheck holds for every one, 133 since 0.7.138), the figures the face does not say - materials, build poin... |
| 2121 | 11 | `HBox statPair(String label, String value)` | A label and a figure, on one line, inside a stat cover. |
| 2147 | 4 | `boolean placeOrder(BuildingsTemplate template, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Placing the order, and everything the city can say back. |

### the keyboard (lines 2152-2243)

| line | len | member | says |
|---:|---:|---|---|
| 2172 | 1 | **type** `record PageCard(BuildingsTemplate template, Runnable reprice)` | One card on the page showing now: what it builds, and how it reprices itself in place. |
| 2197 | 10 | `boolean buildPending()` | Every pending order on the page, placed as its own Build button would place it, in the page's order. |
| 2214 | 10 | `boolean clearPending()` | Every quantity on the page back to none - the ↺ on every card at once, and like it, each card repriced in place rather than the page redrawn (see REPRICED IN PLACE, in orderControls()). |
| 2230 | 12 | `void showPendingHint()` | The caption under the grid, shown while any card on the page has a quantity - and its line kept while it is not (0.7.20): it was taken out of the layout, so pressing "+" made the page a line taller and moved it. |

### THE OVERVIEW (0.7.24) (lines 2244-2336)

| line | len | member | says |
|---:|---:|---|---|
| 2280 | 30 | `void showOverview()` | The Build tab's front page. |
| 2312 | 18 | `HBox overviewLead()` | The one line: whose job the city's works are. |

### the city's job (lines 2337-2555)

| line | len | member | says |
|---:|---:|---|---|
| 2340 | 7 | `javafx.scene.layout.GridPane cityJob(List<CityNeeds.Need> all)` | The five, a tile each. |
| 2349 | 10 | `static CityNeeds.Go goOf(String category)` | Which of NEEDS YOU's doors a city category is. |
| 2361 | 3 | `static String verdict(int level)` | A verdict's colour: green, amber or red, as NEEDS YOU colours its rows; -1, no verdict, is the city's blue. |
| 2369 | 1 | **type** `record Shown(String figure, double arc, String caption, String detail)` | What a ring says: its figure, how much of it is drawn, a caption of a few words and a smaller line. |
| 2377 | 58 | `Shown shown(CityNeeds.Need n)` | A need, as a ring shows it: the figure NEEDS YOU judged, worded for the ring. |
| 2437 | 1 | `static String pct(double share)` | A share as a whole per cent; a dash for one that is not a number (a network with nothing supplying it). |
| 2440 | 3 | `static String servedWords(CityNeeds.Served s)` | A served gauge's words after its figure (0.7.41), the same on every screen: "served, short", "served, tight", "served, enough" - or "served" alone where no line judges it (transit). |
| 2445 | 3 | `static String servedTone(CityNeeds.Served s)` | ...its colour: the one verdict's, or the city's blue where no line judges it. |
| 2450 | 4 | `static double arc(CityNeeds.Served s)` | ...and its ring's arc: the share held to a whole ring, so a ring is full when the need is met (0.7.41: a load's ring was full when it was over). |
| 2456 | 17 | `Shown fine(String category, List<CityNeeds.Need> all)` | A category with nothing near its line: a few words, and the figure nearest one. |
| 2475 | 7 | `int onSiteIn(BuildAdvice.Category c)` | Buildings on site in a category, for anybody's order (the Strategic Reserve in Infrastructure's, not Industry's: Category.holds()). |
| 2484 | 6 | `int standingIn(BuildAdvice.Category c)` | ...and standing. |
| 2492 | 9 | `HBox onSiteLine(int n, String none)` | A crane and "N on site", in the building colour, or a quiet word when there is nothing. |
| 2510 | 45 | `VBox jobTile(BuildAdvice.Category c, List<CityNeeds.Need> all)` | One of the city's five: its worst need as a ring, a caption, a line, and what is on site. |

### what would help most (lines 2556-2913)

| line | len | member | says |
|---:|---:|---|---|
| 2566 | 28 | `javafx.scene.Node adviceTotal(List<BuildAdvice.Suggestion> cards)` | "all three ≈ $X of your $Y", and the button that orders them ("Build all three", 0.7.34; a step chip, "Order all three", before). |
| 2602 | 30 | `Pieces.Press allPress(List<BuildAdvice.Suggestion> cards)` | "Build all three" or "Build both" (0.7.34), read off the run the model will place (0.7.51; each card's own look, until then, said GO for runs the model stopped): held when Game.buildRunAhead() stops it short, with the... |
| 2634 | 3 | `void orderAll(List<BuildAdvice.Suggestion> advice)` | The suggestions as one run (0.7.40, placeRun(); BuildAdvice.run() since 0.7.51): funded whole when it is more than the cash, each placed in turn by its own button's path; the first refusal stops the run, as Enter's does. |
| 2639 | 11 | `javafx.scene.Node adviceRow(List<BuildAdvice.Suggestion> advice)` | Up to three cards, or a line saying there is nothing to suggest. |
| 2652 | 4 | `String gaugeText(BuildAdvice.Measure m, java.util.Map<BuildingsTemplate, Integer> added)` | A measure's gauge with these buildings standing as well, as its ring writes it (0.7.41): what a served measure serves, else figureText() of its figure. |
| 2658 | 15 | `String figureText(BuildAdvice.Measure m, double f)` | A measure's figure, worded as its ring words it: a share, a load, months, people, or a multiple of Canada's crime - for a served measure (0.7.41) gaugeText() reads what it serves instead. |
| 2680 | 24 | `String doesWhat(BuildAdvice.Suggestion s)` | What a suggested order does, in a line. |
| 2706 | 6 | `static BuildAdvice.Measure showOn(BuildAdvice.Suggestion s)` | The measure Show opens a category on, for a suggestion: transit's ring for a line, death care's for the plots. |
| 2714 | 100 | `VBox suggestionCard(BuildAdvice.Suggestion s)` | One suggested order: the need, before and after, the count and the building, its three lines (cardWords(), 0.7.51), its price, Show, and its Build button. |
| 2826 | 1 | **type** `record CardWords(String does, String sized, String land, String landTip, String heldBack)` | A suggestion card's three lines (0.7.51), worked out without drawing them (a probe reads them): what the order does - and, on credit, by how much the cash the cards before leave falls short; when it opens and what it ... |
| 2828 | 55 | `CardWords cardWords(BuildAdvice.Suggestion s)` |  |
| 2885 | 3 | `static String suggestionName(BuildAdvice.Suggestion s)` | A suggestion's building as its card names it (0.7.70): its own name, or the gravel roads a paving paves. |
| 2890 | 1 | `static String pct1(double share)` | A share as a percentage to one place: .0135 reads "1.4%" (the card's growth line puts its own "+"). |
| 2899 | 14 | `Pieces.Press suggestionPress(BuildAdvice.Suggestion s)` | A suggestion's button (0.7.34): a card's words for its count, off the same verdict (orderPress()) - on credit as well when its quote is more than the cash the suggestions before it leave (Suggestion.needsCredit(), the... |

### AUTOMATIC BUILDING (0.7.73) (lines 2914-3251)

| line | len | member | says |
|---:|---:|---|---|
| 2957 | 3 | `static String autoHint(AutoBuilder ab)` | The heading's quiet words: on or off. |
| 2962 | 8 | `javafx.scene.layout.GridPane autoRow()` | The two dials and what it has done, a card each. |
| 2975 | 5 | `static String[] autoMarginWords(double slack)` | The spare margin's card's two lines: its title and what it means. |
| 2982 | 13 | `static String[] autoLimitWords(AutoBuilder ab, Game g)` | The debt limit's card's two lines, and the mark under its slider: where the city stands now (0.7.81: its debt over a year of GDP; since 0.7.101 all it owes over the output of its last twelve months). |
| 2997 | 5 | `static String[] autoCashWords()` | "Build from cash anyway" (0.7.81): the lead, the two chips, and each chip's tooltip. |
| 3008 | 31 | `static List<String> autoStatusWords(AutoBuilder ab, int month)` | The third card's words: on or off, what it has done all told, its latest orders (each with why, for its tooltip), and what holds it back - {state, line, totals, held, held tooltip, then order and why in pairs}. |
| 3041 | 4 | `static Pieces.Press autoPress(boolean on)` | The switch, as its button says it. |
| 3047 | 12 | `VBox autoCard(String title, javafx.scene.Node dial, String words)` | A card in the row: its title, a dial and its words. |
| 3060 | 8 | `VBox autoMarginCard(AutoBuilder ab)` |  |
| 3069 | 37 | `VBox autoLimitCard(AutoBuilder ab)` |  |
| 3108 | 3 | `double autoFair()` | What the builder's "at …" chip starts from: the fair yield today of the bond it sells. |
| 3141 | 10 | `static String[] askWords(AutoBuilder.OpenAsk a, int now)` | One open ask in words, apart from the nodes (a probe reads them): {the order, the measure it keeps ahead, what the order costs, raised so far, still to raise, the months open}. |
| 3158 | 40 | `List<javafx.scene.Node> autoAsks()` | THE OPEN ASKS (CD2, D30): a row for each order automatic building is waiting on - the order and what it keeps ahead, what it costs, what is raised so far, what is still to raise, the months open - with Withdraw. |
| 3200 | 9 | `private VBox askFigure(String label, String value, String tone)` | One figure of an open ask's row: its small label over its value. |
| 3210 | 41 | `VBox autoStatusCard(AutoBuilder ab)` |  |

### the market builds these (lines 3252-3291)

| line | len | member | says |
|---:|---:|---|---|
| 3255 | 7 | `javafx.scene.layout.GridPane marketRow()` | The nine, quieter. |
| 3264 | 27 | `HBox marketTile(BuildAdvice.Category c)` | One of the market's: its icon, its name, what stands and what is on site. |

### A CITY CATEGORY, OPENED ON ITS NEEDS (0.7.24) (lines 3292-3896)

| line | len | member | says |
|---:|---:|---|---|
| 3345 | 6 | `BuildAdvice.Measure worstMeasure(BuildAdvice.Category c, List<BuildAdvice.Measure> measures, List<CityNeeds.Need> all)` | The worst of a category's measures: its NEEDS YOU row first in the panel's order, else the first ring. |
| 3360 | 5 | `int levelOf(BuildAdvice.Measure m, List<CityNeeds.Need> all)` | A measure's verdict. |
| 3367 | 81 | `void showCityCategory(BuildAdvice.Category c)` | A city category's page. |
| 3450 | 19 | `String measureSubtitle(BuildAdvice.Measure m)` | A few words on what a measure is for, beside its heading. |
| 3471 | 5 | `Label scarceNote(List<BuildingsTemplate> shown, double[] fill)` | At the heading's right: which of these buildings' staff the city fills worst. |
| 3478 | 13 | `String scarceWords(List<BuildingsTemplate> shown, double[] fill)` | ...its words, worked out without the label (0.7.28: the Services care heading says them too). |
| 3493 | 16 | `String jobPlural(JobType job)` | A job type in plain words, plural: "doctors", "nurses", "unskilled workers". |
| 3511 | 14 | `static String unitWords(BuildAdvice.Measure m)` | What a measure is counted in, for a ring's "short by N ..." - only care reaches it since 0.7.25, when the cards' "serves N ..." moved to BuildCard.doesWords(). |
| 3527 | 3 | `static String perTenThousand(double staff)` | The staff bar's figure: a tenth below a hundred, whole and grouped above. |
| 3537 | 2 | **type** `record RingWords(String figure, double arc, String tone, String shortLine, String onSite, int units, CityNe...` | What a ring says, worked out without drawing it: its figure, how much of it is drawn, its verdict's colour, how far short in people or places, and what is on site with its wait. |
| 3540 | 108 | `RingWords ringWords(BuildAdvice.Measure m, List<CityNeeds.Need> all)` |  |
| 3650 | 57 | `HBox measureCard(BuildAdvice.Category c, BuildAdvice.Measure m, List<CityNeeds.Need> all, boolean picked, boolean small)` | One ring: its figure, how far short, and what is on site with its wait; a click picks it. |
| 3709 | 3 | `static String wordsAt(double size, String colour)` | Words at a size between Palette's steps (0.7.24's cards and rings), in a colour. |
| 3714 | 3 | `static String figureAt(double size, String colour)` | A figure at a size between Palette's steps: Palette.figure()'s face. |
| 3719 | 3 | `static String shortOrWhole(double v)` | A figure that may be large: "2,500", "120k", "1.2M". |
| 3726 | 5 | `String moneyBarInfo()` | The money bar's (i) (0.7.45): what a build's money is struck at this month. |
| 3733 | 7 | `String roadBarInfo()` | A road card's money bar's (i) (0.7.70): what its cost over its life is made of - BuildAdvice.lifetime(), the advice's own figure. |
| 3747 | 20 | `VBox barRow(String label, String value, double share, String colour, double wide, String svg, boolean track)` | One of the card's two bars: what it measures, its figure, and the bar scaled across its group - with no track when `track` is false (0.7.25): a group of one, where a full bar compares nothing, or a card with nothing t... |
| 3776 | 114 | `void refreshOrderBar()` | The order bar: what the page's steppers add up to, its price against the cash, the picked measure now, when what is on site opens and with the order - as a stacked bar and in figures - what the order needs, and one Bu... |
| 3892 | 4 | `Pieces.Press orderBarPress(int units, double total, double cash)` | The order bar's Build (0.7.34): "Build 5 · $X" with the order's total, or "Build 5 on credit · $X" past the cash. |

### A MARKET CATEGORY, IN ITS GROUPS (0.7.25) (lines 3897-4081)

| line | len | member | says |
|---:|---:|---|---|
| 3927 | 47 | `void showMarketCategory(BuildAdvice.Category c)` | A market category's page. |
| 3981 | 18 | `HBox groupHead(BuildCard.Group group)` | A market group's heading - "FOOD MILLS · bread and bakery goods from crops", or "STEEL · from iron ore" where the goods would repeat the name - and at its right the sector's figure the group answers to, both wrapping ... |
| 4005 | 31 | `String groupSubtitle(BuildCard.Group group)` | What a market group is for, beside its name: the goods a maker group makes and what from, off the templates' own goods; a few words for the rest. |
| 4038 | 4 | `static String andList(List<String> words)` | "a, b and c". |
| 4044 | 30 | `String groupNote(BuildCard.Note n)` | A group's note, in words (BuildCard.NoteKind). |
| 4076 | 5 | `static String doors(double shortfall)` | Households of a segment without a door, or the doors to spare (RealEstate.doorShortfall(), negative when there are). |

### THE STAT CARD (lines 4082-4666)

| line | len | member | says |
|---:|---:|---|---|
| 4097 | 5 | `Region cardGap()` | A hairline of space, used where a blank line would be too much. |
| 4104 | 8 | `static String cargoWords(ham.citybuildersim.Ports.Cargo k)` | A kind of cargo's goods, in words (0.7.86; Good.cargo()). |
| 4131 | 326 | `public List<String> whatItDoes(BuildingsTemplate t)` | Package-private, not private, so BuildMenuCheck can read the sentences back. |
| 4464 | 29 | `List<String> whatSafetyItGives(BuildingsTemplate t)` | The police and the prisons (2026-09-11). |
| 4502 | 75 | `public List<String> whatCareItGives(BuildingsTemplate t)` | The healthcare version, which needs the care type and not the category. |
| 4579 | 16 | `public String jobLabel(JobType job)` | JobType, in words a player reads rather than the enum constant. |
| 4606 | 60 | `HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories)` | The receipt dot, top-right of the menu, and the popover it opens with the last purchases (0.7.20; the card was in the page and showed one). |

### the last purchases (0.7.20) (lines 4667-5207)

| line | len | member | says |
|---:|---:|---|---|
| 4673 | 1 | **type** `record Receipt(int serial, String name, int quantity, double total, double salesTax, int month)` | One purchase, as the receipt shows it: what and how many, what it cost all in, the sales tax in that, and when. |
| 4697 | 7 | `void noteReceipt(Game.BuildQuote quote)` | After an order went through: the receipt the model wrote for it, with the sales tax off the quote it was charged on - quoted just before the order, against the city the order was then placed in, as Game.processBuildOr... |
| 4706 | 4 | `void forgetReceipts()` | Another city, founded or loaded (UserInterface.anotherCity()): its purchases are not this one's. |
| 4711 | 18 | `private void openReceipt(Button dot)` |  |
| 4731 | 6 | `void closeReceipt()` | Close the receipt, if it is open. |
| 4739 | 20 | `private VBox receiptCard()` | The card in the popover: the last purchases, newest first, each with its total and the tax in it. |
| 4760 | 6 | `Label receiptLine(String label, String value)` |  |
| 4775 | 12 | `String schoolHeading(EducationType type)` | A school's heading, and what it is actually for. |
| 4796 | 19 | `String schoolSubtitle(EducationType type)` | One line, like the care subtitles beside it. |
| 4817 | 10 | `String careHeading(CareType care)` | The group's name, in the player's words rather than the enum's. |
| 4835 | 22 | `String careSubtitle(CareType care)` | What building one of these actually gets you. |
| 4887 | 44 | `void showNoDepositMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | The city has the money, the land, and nothing to dig. |
| 4937 | 14 | `static String[] noDepositPage(Resource site, String building, int quantity, int owned, int committed, double left)` | The no-deposit page's words, for iron or oil (0.7.62; pure, so a probe measures them): the title, the WHY section's five lines, the reserve line and the Buy's sub-line. |
| 4959 | 16 | `static String[] noDepositAtSea(boolean platform, String building, int quantity, int owned, int committed, double left)` | ...and for the oil at sea (0.7.91; pure, as above): an Offshore Platform with no shallow sea field to take it, or a Platform Well with no free slot - `owned` is Game.sitesFor()'s, the platforms the fields could still ... |
| 4988 | 9 | `static String ironPaybackWords(BuildAdvice.Payback p)` | Whether the cheapest iron field pays back here, as the no-deposit page says it under its Buy (0.7.101, Jerus's decision B: "the build advice says when an iron field won't pay back for a small town"): the test player's... |
| 4999 | 7 | `static String noDepositWords(LandParcel p, Resource site)` | The no-deposit page's Buy: "Buy the cheapest: East 7 · 1 iron site, 12.8 Mt for US$6.16M" (or oil sites, 0.7.62) - its sites and, since 0.7.64, its tonnes: the whole of every field centred in it; "the cheapest" since ... |
| 5017 | 53 | `void showNoCoastMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | The city has the money and the land, and no sea to draw (0.7.59). |
| 5078 | 41 | `void showNoLicenceMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Nobody licensed to practise in it. |
| 5131 | 64 | `void showNoLandMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | The city has the money and nowhere to put the building. |
| 5197 | 3 | `static String noLandWords(LandParcel p)` | The no-land page's Buy: "Buy the best: 3.4 km² for US$12.1M" (spec-land 2.8). |
| 5202 | 5 | `static String noLandSub(LandParcel p, double shortSqFt)` | ...and what it is: bare ground that covers the shortfall, the ore left to the deposit's own page (batch J1c) - or, with no bare offer big enough, the best value, and buy again. |

### ONE FUNDING PAGE FOR THE RUN (0.7.40) (lines 5208-5687)

| line | len | member | says |
|---:|---:|---|---|
| 5260 | 1 | **type** `record RunFrom(String title, EnumSet<BuildingType> categories, boolean cards)` | Where a run came from: the page to come back to, and whether its orders are that page's cards - each taken off its card once placed, the rest left on theirs. |
| 5263 | 5 | `static java.util.LinkedHashMap<BuildingsTemplate, Integer> runOf(BuildingsTemplate t, int n)` | One order as a run of one. |
| 5270 | 8 | `static java.util.LinkedHashMap<BuildingsTemplate, Integer> firstOf(java.util.Map<BuildingsTemplate, Integer> run, int n)` | The first `n` orders of a run. |
| 5280 | 8 | `static String runNames(java.util.Map<BuildingsTemplate, Integer> run)` | A run's orders in words, the order bar's way: "5 × Walk-in Clinic + 3 × Paved Road". |
| 5295 | 12 | `boolean placeRun(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | A run placed (0.7.40): funded first when the orders that can go ahead cost more than the cash (showBuildFunding()), otherwise placed in turn as they always were. |
| 5325 | 20 | `private boolean goAhead(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from, String paper)` | Every order of the run in turn, through buildStack() - the path each card's Build always took - each placed one taken off its card. |
| 5347 | 2 | **type** `record Placed(java.util.LinkedHashMap<BuildingsTemplate, Integer> built, java.util.LinkedHashMap<BuildingsT...` | What placing a run in turn did: the orders placed, the orders left - the one that stopped it first - and buildStack()'s answer there, SUCCESS when nothing stopped it. |
| 5351 | 17 | `Placed placeInTurn(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | goAhead()'s placing, without the page it then draws (a probe runs it): each order through buildStack() until one is not placed, each placed one's receipt noted and, from the cards, its card emptied. |
| 5376 | 2 | **type** `record RunWords(java.util.LinkedHashMap<BuildingsTemplate, Integer> funded, double invoice, double gap, Str...` | What the funding page says about a run, worked out without drawing it (a probe reads it): the orders that can go ahead, their invoice and the gap, the summary's line, what the treasury holds and the bar's part for it,... |
| 5379 | 31 | `RunWords runWords(java.util.Map<BuildingsTemplate, Integer> run)` |  |
| 5412 | 2 | **type** `record RunOffer(String name, DebtQuote quote, String rate, String tone, String ending, Pieces.Press action,...` | One loan the funding page offers, worded - what Pieces.offerCard() draws, as the land office's Offer is: its name, quote, rate's words and colour, ending, button; what booking it does to the model (a probe runs it); a... |
| 5424 | 37 | `List<RunOffer> runOffers(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | The two offers, the old page's on the run's gap: the bond first - what the page recommends, since a building outlives either loan and the matching principle pays for a long-lived asset with long-lived debt - then the ... |
| 5479 | 26 | `static List<String> shortWords(List<RunOffer> loans)` | What the funding page says under a preview that does not cover, apart from the nodes (a probe reads it): {the heading, one sentence for each offer that is short, then what can be done}. |
| 5515 | 35 | `javafx.scene.Node shortAnswers(List<RunOffer> loans)` | The funding card's answers to a short preview (CD2, D30, §4.5): the red words (shortWords()) and the cap chips, which quote both offers again at the cap they name. |
| 5552 | 20 | `HBox runHead(String sub, RunFrom from)` | The run's pages' head: "Build › Funding", the title a way back to the page the run came from, and "‹ <that page>" at the right - the land office's head() on Build. |
| 5579 | 67 | `void showBuildFunding(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | The funding page for a run. |
| 5652 | 14 | `String[] fellShortWords(java.util.Map<BuildingsTemplate, Integer> built, java.util.Map<BuildingsTemplate, Integer> left, RunFro...` | What the fell-short card says, worked out without drawing it: its heading, then what was built, what was not and what it is still short of - the model's gap for the orders left, as it stands now. |
| 5676 | 11 | `void showBuildFellShort(java.util.Map<BuildingsTemplate, Integer> built, java.util.Map<BuildingsTemplate, Integer> left, RunFro...` | The loan went through and an order still did not: one red card in Build's frame, as the land office's "Not bought" (0.7.40; it was THE MONEY IS IN, THE BUILDING IS NOT, a page of its own). |


# BuildScreen.java - 3,817 lines · 128 methods · 6 constants · interface

`ham/citybuildersim/ui/BuildScreen.java` - generated 2026-10-04 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (332), [BuildAdvice](BuildAdvice.md) (131), [CityNeeds](CityNeeds.md) (76), [BuildingsTemplate](BuildingsTemplate.md) (59), [BuildCard](BuildCard.md) (46), [Pieces](Pieces.md) (45), [BuildingType](BuildingType.md) (43), [Game](Game.md) (22), [Icons](Icons.md) (19), [Good](Good.md) (13), [JobType](JobType.md) (11), [CareType](CareType.md) (8), [LandManager](LandManager.md) (7), [Health](Health.md) (4), [SummaryScreen](SummaryScreen.md) (3), [Healthcare](Healthcare.md) (3), [EducationType](EducationType.md) (3), [Crime](Crime.md) (3), [DebtQuote](DebtQuote.md) (3), [UserInterface](UserInterface.md) (2), [BuildingManager](BuildingManager.md) (2), [Formats](Formats.md) (2), [Bank](Bank.md) (2), [SafetyType](SafetyType.md) (2), [Sickness](Sickness.md) (2), [Migration](Migration.md) (2), [ServicesScreen](ServicesScreen.md) (1), [ConstructionControl](ConstructionControl.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Rail](Rail.md) (1)... and 3 more

**Used by (14):** [BankScreen](BankScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 55 | BUILD: THE CATEGORY SCREEN IS GONE TOO. |
| 78 | THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. |
| 388 | THE BUILD MENU |
| 529 | ONE BUILDING, AS A CARD. |
| 1305 | · the keyboard |
| 1397 | THE OVERVIEW (0.7.24) |
| 1476 | · the city's job |
| 1685 | · what would help most |
| 1932 | · the market builds these |
| 1972 | A CITY CATEGORY, OPENED ON ITS NEEDS (0.7.24) |
| 2512 | A MARKET CATEGORY, IN ITS GROUPS (0.7.25) |
| 2688 | THE STAT CARD |
| 3129 | · the last purchases (0.7.20) |
| 3452 | ONE FUNDING PAGE FOR THE RUN (0.7.40) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 187 | `BuildScreen.BUILD_HOME` | `BuildAdvice.OVERVIEW` | Where Build opens (BUILD_HOME), and which category the player was last looking at (buildCategory). |
| 322 | `BuildScreen.CITY_DOT` | `Palette.MONEY` | The colour of "only the city builds these": the money blue - the city's own account. |
| 325 | `BuildScreen.INVESTOR_DOT` | `Palette.BUSINESS` | The colour of "investors build these too": the business violet. |
| 1422 | `BuildScreen.JOB_RING` | `58` | A ring's size on the Overview's tiles (its stroke is 6 px). |
| 2005 | `BuildScreen.NEED_CARD` | `300` | A card's width, on every Build page since 0.7.25 (a city category's only, in 0.7.24). |
| 3138 | `BuildScreen.RECEIPTS` | `5` | How many purchases the receipt keeps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 188 | `String buildCategory` |  |
| 583 | `final java.util.Set<String> pinnedStats` | Which cards are showing their stats, by building name. |
| 586 | `final java.util.Map<String, Integer> orderQty` | How many of each the player has dialled up, by building name. |
| 1328 | `final List<PageCard> pageCards` | The cards on the category page showing now, in the order they are laid out; each draw starts it again. |
| 1331 | `private String pageTitle` | Which page those are on, so an order placed from the keyboard comes back to it. |
| 1332 | `private EnumSet<BuildingType> pageCategories` |  |
| 1335 | `private Label pendingHint` | The caption under the grid that says the two keys; shown only while something on the page is pending. |
| 2002 | `final java.util.Map<String, BuildAdvice.Measure> measurePicked` | Which ring each city category has picked, by name; none picks its worst. |
| 2008 | `private VBox orderBar` | The order bar, refilled in place as a stepper moves. |
| 2011 | `private BuildAdvice.Measure orderMeasure` | The measure the order bar reads, the picked one; null on a market page (orderMarket). |
| 2539 | `private boolean orderMarket` | Whether the order bar is a market page's, which has no measure to draw. |
| 3148 | `final java.util.ArrayDeque<Receipt> receipts` | The last RECEIPTS purchases, newest first. |
| 3151 | `private javafx.stage.Popup receiptPopup` | The receipt's popover while it is open, or null. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 3770 | **type** `final class BuildScreen` | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.25), the strip of categories, the constraints bar that says what stops... |
| 53 | 1 | `BuildScreen(UserInterface ui)` |  |

### BUILD: THE CATEGORY SCREEN IS GONE TOO. (lines 55-77)

| line | len | member | says |
|---:|---:|---|---|
| 76 | 1 | **type** `record BuildCategory(String name, EnumSet<BuildingType> types)` | One tab on the build strip: what it is called and what it contains. |

### THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. (lines 78-387)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 8 | `static EnumSet<BuildingType> investorTypes()` |  |
| 111 | 3 | `static boolean investorBuilt(EnumSet<BuildingType> types)` | True when everything in this category is something investors put up. |
| 122 | 4 | `static EnumSet<BuildingType> industrialTypes()` | What a private business builds to sell something. |
| 138 | 3 | `static EnumSet<BuildingType> utilityTypes()` | ...and what the city runs because everything else needs it. |
| 165 | 6 | `static BuildCategory[] buildCategories()` | The strip, since 0.7.24 in BuildAdvice's order: the five only the city builds - Utilities, Roads & transit, Healthcare, Education, Safety - then the nine investors build too - Homes, Shops, Industry, Offices, Farms, R... |
| 191 | 3 | `void showBuildMenu()` | The Build tab: the Overview, or whichever category you were last in. |
| 202 | 9 | `void openCategory(String name)` | A category, by its name - or by its label before 0.7.24, which lands in the same place - or the Overview. |
| 218 | 9 | `void openOn(BuildAdvice.Measure m)` | A city category opened on one ring (0.7.28): the Services screen's "Build for it ›" doors - opened as openCategory() opens it, with this measure picked rather than its worst. |
| 234 | 10 | `void why(BuildAdvice.Measure m)` | ...and the way back to the why (0.7.28): the "why ›" at a ring's heading opens the Services page that explains it - the road and transit rings, Infrastructure's, behind "what is on the road" and "who rides" since 0.7.29. |
| 266 | 54 | `javafx.scene.layout.FlowPane buildStrip(String current)` | SINCE 0.7.24 it shows on a category page only, not on the Overview, and reads "‹ Overview", then the city's five with their blue dots, a rule, then the market's nine with their violet ones, left-aligned under the page... |
| 328 | 9 | `static Region whoDot(boolean investors)` | A tab's dot: who builds what is behind it (0.7.21). |
| 339 | 14 | `HBox keyLine()` | The key to the dots, at the right of the page's title: "● only the city builds  ● investors build too". |
| 363 | 23 | `HBox buildHead(String menuTitle, EnumSet<BuildingType> categories)` | The page's head (0.7.21): its title with the building area's swatch, the key to the tabs' dots at the right, the way to the construction page (0.7.22), and the receipt's dot beyond it. |

### THE BUILD MENU (lines 388-528)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 18 | `void handleAllBuildingMenus(String asked, EnumSet<BuildingType> categories)` | A Build page by its name: the Overview, one of the city's five opened on its needs (showCityCategory()), or one of the market's nine in its groups (showMarketCategory(), 0.7.25). |
| 448 | 17 | `HBox whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories)` | One line under the strip saying whether this is your job. |
| 482 | 41 | `HBox constraintsBar()` | WHAT STOPS A BUILD, across the top, above the categories. |
| 525 | 3 | `static String groundTone(int level)` | LAND FREE's colour: the GROUND row's verdict, amber or red, and the strip's plain figure while it is fine. |

### ONE BUILDING, AS A CARD. (lines 529-1304)

| line | len | member | says |
|---:|---:|---|---|
| 597 | 38 | `StackPane card(BuildCard.Figures f, BuildCard.Group group, BuildAdvice.Category c, BuildAdvice.Measure m)` | One building as a card, on a city category's page (its picked ring's group) or a market category's (its owning sector's group). |
| 637 | 6 | `static javafx.scene.text.Text textRun(String s, String font, double size, String colour)` | A run of words in a TextFlow, at a size and in a colour - so a line wraps where it must and never ends in "...". |
| 645 | 7 | `static javafx.scene.text.TextFlow flow(javafx.scene.Node...runs)` | ...and a TextFlow of them, as wide as a card's face. |
| 661 | 27 | `HBox cardHead(BuildCard.Figures f, String svg, String dot, boolean afford)` | The head: an icon square in who-builds-it's colour - the strip's dots, blue for the city's five, violet for the market's nine - the name, faint when one costs more than the cash, "you have N" or "none built", and what... |
| 693 | 6 | `javafx.scene.layout.FlowPane cardTags(BuildCard.Figures f, BuildCard.Group group)` | The tags: the best of the group on each bar (BuildCard.Group.best1() and best2()), in a flow so two never cut each other. |
| 701 | 13 | `static List<String> tagWords(BuildCard.Figures f, BuildCard.Group group)` | The tags' words, for the bars the card is the best of: "cheapest per resident", "adds the most per $". |
| 721 | 21 | `VBox heroRow(BuildCard.Figures f)` | The hero: what the building gives the city, in its own verb, a big figure and its unit - "houses 252 residents", "makes 1,200 t of steel a month", "puts 87 officers on the street · 120 fully staffed" - and a smaller l... |
| 750 | 32 | `String heroDetail(BuildCard.Figures f)` | The hero's smaller line, by kind: a home's doors and who may live behind them; the branch's capital; the builders' output; an office's exports at the price of a seat; a maker's other goods, what it uses and what it ad... |
| 784 | 8 | `static String goodsList(java.util.Map<Good, Double> goods)` | Goods and their counts: "1,320 t of iron ore", "5,000 kg of dairy and eggs and 1,400 kg of meat". |
| 794 | 12 | `HBox priceRow(BuildCard.Figures f, boolean afford)` | The price all in, green when the cash covers it and red when it does not, with the sticker smaller. |
| 817 | 13 | `List<javafx.scene.Node> cardBars(BuildCard.Figures f, BuildCard.Group group, BuildAdvice.Measure m)` | The two bars. |
| 832 | 23 | `static String[] barWords(BuildCard.Figures f, BuildAdvice.Measure m)` | The bars' labels and figures: {label 1, figure 1, label 2, figure 2}, label 2 null for no second bar. |
| 857 | 4 | `static String sqFt(double v)` | Square feet a bar reads: a tenth below a hundred, whole and grouped above; a dash for none. |
| 873 | 24 | `HBox investorsLine(BuildingsTemplate t)` | THE INVESTORS' LINE, on a market card: if investors have an order on its site, that, with the count and the wait; otherwise the sector's own word for the month (Game.getLastInvestment(), filed per sector, so it can na... |
| 902 | 15 | `String[] investorsWords(BuildCard.Investors inv)` | The line's words after "Investors": {what they are doing or last said, " · this one: " and the gate - or null}. |
| 919 | 17 | `String gateWords(BuildCard.Gate gate)` | A gate, in the words its own refusal uses: the deposit, the licence, the staffing test's why, landReason()'s land, the estimate's loss. |
| 945 | 6 | `Label needsLine(BuildCard.Figures f)` | What it needs: its posts by job - or "needs no staff" - then, on a market card, the share of them the owner's staffing test says the city could fill, below 99.5%; then its land, red with " - more than is free" when it... |
| 953 | 20 | `String needsWords(BuildCard.Figures f)` | ...its words. |
| 975 | 1 | **type** `record Order(HBox steps, Pieces.ActionButton build, Label quoted, Runnable reprice)` | A card's order line: the stepper, Build under it (0.7.34), the quote under that, and how they reprice in place. |
| 1006 | 97 | `Order orderControls(BuildingsTemplate template, String key, String page, EnumSet<BuildingType> types, Runnable after)` | − N + +10 +100 and ↺, then Build, and the quote under them - as the 0.7.21 card had them, on every card now (the city's had lost +100). |
| 1115 | 14 | `Pieces.Press orderPress(BuildCard.Verdict v, int n)` | What a Build button says for n of a building (0.7.34), off the verdict the quote line reads (BuildCard.verdict(), Game.quoteBuild()): with none chosen, "Build" and "choose how many"; ready, "Build 3 · $37.5M", the cou... |
| 1137 | 9 | `String[] quoteVerdict(BuildCard.Verdict v)` | The quote's verdict and its colour, in buildStack()'s order: no deposit and nobody licensed in red - warned here since 0.7.25, where they were found only after the click - then short of land in red, short of cash in a... |
| 1150 | 24 | `Label infoDot(String key, VBox cover)` | The (i): hover shows the stat cover over the whole card, a click keeps it there (the dot turns blue), a second puts it away. |
| 1188 | 84 | `VBox statCover(BuildingsTemplate t, BuildCard.Figures f)` | The stat cover, as tall as the card it covers: what the building does in sentences (whatItDoes(), which BuildMenuCheck holds for all 73), the figures the face does not say - materials, build points, road load, electri... |
| 1274 | 11 | `HBox statPair(String label, String value)` | A label and a figure, on one line, inside a stat cover. |
| 1300 | 4 | `boolean placeOrder(BuildingsTemplate template, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Placing the order, and everything the city can say back. |

### the keyboard (lines 1305-1396)

| line | len | member | says |
|---:|---:|---|---|
| 1325 | 1 | **type** `record PageCard(BuildingsTemplate template, Runnable reprice)` | One card on the page showing now: what it builds, and how it reprices itself in place. |
| 1350 | 10 | `boolean buildPending()` | Every pending order on the page, placed as its own Build button would place it, in the page's order. |
| 1367 | 10 | `boolean clearPending()` | Every quantity on the page back to none - the ↺ on every card at once, and like it, each card repriced in place rather than the page redrawn (see REPRICED IN PLACE, in orderControls()). |
| 1383 | 12 | `void showPendingHint()` | The caption under the grid, shown while any card on the page has a quantity - and its line kept while it is not (0.7.20): it was taken out of the layout, so pressing "+" made the page a line taller and moved it. |

### THE OVERVIEW (0.7.24) (lines 1397-1475)

| line | len | member | says |
|---:|---:|---|---|
| 1425 | 24 | `void showOverview()` | The Build tab's front page. |
| 1451 | 18 | `HBox overviewLead()` | The one line: whose job the city's works are. |

### the city's job (lines 1476-1684)

| line | len | member | says |
|---:|---:|---|---|
| 1479 | 7 | `javafx.scene.layout.GridPane cityJob(List<CityNeeds.Need> all)` | The five, a tile each. |
| 1488 | 10 | `static CityNeeds.Go goOf(String category)` | Which of NEEDS YOU's doors a city category is. |
| 1500 | 3 | `static String verdict(int level)` | A verdict's colour: green, amber or red, as NEEDS YOU colours its rows; -1, no verdict, is the city's blue. |
| 1508 | 1 | **type** `record Shown(String figure, double arc, String caption, String detail)` | What a ring says: its figure, how much of it is drawn, a caption of a few words and a smaller line. |
| 1516 | 48 | `Shown shown(CityNeeds.Need n)` | A need, as a ring shows it: the figure NEEDS YOU judged, worded for the ring. |
| 1566 | 1 | `static String pct(double share)` | A share as a whole per cent; a dash for one that is not a number (a network with nothing supplying it). |
| 1569 | 3 | `static String servedWords(CityNeeds.Served s)` | A served gauge's words after its figure (0.7.41), the same on every screen: "served, short", "served, tight", "served, enough" - or "served" alone where no line judges it (transit). |
| 1574 | 3 | `static String servedTone(CityNeeds.Served s)` | ...its colour: the one verdict's, or the city's blue where no line judges it. |
| 1579 | 4 | `static double arc(CityNeeds.Served s)` | ...and its ring's arc: the share held to a whole ring, so a ring is full when the need is met (0.7.41: a load's ring was full when it was over). |
| 1585 | 17 | `Shown fine(String category, List<CityNeeds.Need> all)` | A category with nothing near its line: a few words, and the figure nearest one. |
| 1604 | 7 | `int onSiteIn(EnumSet<BuildingType> types)` | Buildings on site in a category, for anybody's order. |
| 1613 | 6 | `int standingIn(EnumSet<BuildingType> types)` | ...and standing. |
| 1621 | 9 | `HBox onSiteLine(int n, String none)` | A crane and "N on site", in the building colour, or a quiet word when there is nothing. |
| 1639 | 45 | `VBox jobTile(BuildAdvice.Category c, List<CityNeeds.Need> all)` | One of the city's five: its worst need as a ring, a caption, a line, and what is on site. |

### what would help most (lines 1685-1931)

| line | len | member | says |
|---:|---:|---|---|
| 1688 | 26 | `javafx.scene.Node adviceTotal(List<BuildAdvice.Suggestion> advice)` | "all three ≈ $X of your $Y", and the button that orders them ("Build all three", 0.7.34; a step chip, "Order all three", before). |
| 1721 | 13 | `Pieces.Press allPress(List<BuildAdvice.Suggestion> advice, boolean overCash)` | "Build all three" or "Build both" (0.7.34), in the look of the cards under it: held when one of them is - orderAll() stops at the first the city refuses, so it says so - else "... |
| 1736 | 5 | `void orderAll(List<BuildAdvice.Suggestion> advice)` | The suggestions as one run (0.7.40, placeRun()): funded whole when it is more than the cash, each placed in turn by its own button's path; the first refusal stops the run, as Enter's does. |
| 1743 | 11 | `javafx.scene.Node adviceRow(List<BuildAdvice.Suggestion> advice)` | Up to three cards, or a line saying there is nothing to suggest. |
| 1756 | 4 | `String gaugeText(BuildAdvice.Measure m, java.util.Map<BuildingsTemplate, Integer> added)` | A measure's gauge with these buildings standing as well, as its ring writes it (0.7.41): what a served measure serves, else figureText() of its figure. |
| 1762 | 14 | `String figureText(BuildAdvice.Measure m, double f)` | A measure's figure, worded as its ring words it: a share, a load, months, people, or a multiple of Canada's crime - for a served measure (0.7.41) gaugeText() reads what it serves instead. |
| 1783 | 22 | `String doesWhat(BuildAdvice.Suggestion s)` | What a suggested order does, in a line. |
| 1807 | 17 | `static String goal(BuildAdvice.Measure m)` | What closing the need means, for the line that gives the full count - a served measure's line read as served (0.7.41: "bring the load under 75%" became "bring power over 133% served"). |
| 1826 | 6 | `static BuildAdvice.Measure showOn(BuildAdvice.Suggestion s)` | The measure Show opens a category on, for a suggestion: transit's ring for a line, death care's for the plots. |
| 1834 | 81 | `VBox suggestionCard(BuildAdvice.Suggestion s)` | One suggested order: the need, before and after, the count and the building, a line, its price, Show, and its Build button. |
| 1922 | 9 | `Pieces.Press suggestionPress(BuildAdvice.Suggestion s)` | A suggestion's button (0.7.34): a card's words for its count, off the same verdict (orderPress()) - on credit as well when the cash left after the suggestions before it affords none (Suggestion.needsCredit(), the old ... |

### the market builds these (lines 1932-1971)

| line | len | member | says |
|---:|---:|---|---|
| 1935 | 7 | `javafx.scene.layout.GridPane marketRow()` | The nine, quieter. |
| 1944 | 27 | `HBox marketTile(BuildAdvice.Category c)` | One of the market's: its icon, its name, what stands and what is on site. |

### A CITY CATEGORY, OPENED ON ITS NEEDS (0.7.24) (lines 1972-2511)

| line | len | member | says |
|---:|---:|---|---|
| 2014 | 6 | `BuildAdvice.Measure worstMeasure(BuildAdvice.Category c, List<BuildAdvice.Measure> measures, List<CityNeeds.Need> all)` | The worst of a category's measures: its NEEDS YOU row first in the panel's order, else the first ring. |
| 2029 | 5 | `int levelOf(BuildAdvice.Measure m, List<CityNeeds.Need> all)` | A measure's verdict. |
| 2036 | 66 | `void showCityCategory(BuildAdvice.Category c)` | A city category's page. |
| 2104 | 17 | `String measureSubtitle(BuildAdvice.Measure m)` | A few words on what a measure is for, beside its heading. |
| 2123 | 5 | `Label scarceNote(List<BuildingsTemplate> shown, double[] fill)` | At the heading's right: which of these buildings' staff the city fills worst. |
| 2130 | 13 | `String scarceWords(List<BuildingsTemplate> shown, double[] fill)` | ...its words, worked out without the label (0.7.28: the Services care heading says them too). |
| 2145 | 16 | `String jobPlural(JobType job)` | A job type in plain words, plural: "doctors", "nurses", "unskilled workers". |
| 2163 | 13 | `static String unitWords(BuildAdvice.Measure m)` | What a measure is counted in, for a ring's "short by N ..." - only care reaches it since 0.7.25, when the cards' "serves N ..." moved to BuildCard.doesWords(). |
| 2178 | 3 | `static String perTenThousand(double staff)` | The staff bar's figure: a tenth below a hundred, whole and grouped above. |
| 2188 | 2 | **type** `record RingWords(String figure, double arc, String tone, String shortLine, String onSite, int units, CityNe...` | What a ring says, worked out without drawing it: its figure, how much of it is drawn, its verdict's colour, how far short in people or places, and what is on site with its wait. |
| 2191 | 92 | `RingWords ringWords(BuildAdvice.Measure m, List<CityNeeds.Need> all)` |  |
| 2285 | 57 | `HBox measureCard(BuildAdvice.Category c, BuildAdvice.Measure m, List<CityNeeds.Need> all, boolean picked, boolean small)` | One ring: its figure, how far short, and what is on site with its wait; a click picks it. |
| 2344 | 3 | `static String wordsAt(double size, String colour)` | Words at a size between Palette's steps (0.7.24's cards and rings), in a colour. |
| 2349 | 3 | `static String figureAt(double size, String colour)` | A figure at a size between Palette's steps: Palette.figure()'s face. |
| 2354 | 3 | `static String shortOrWhole(double v)` | A figure that may be large: "2,500", "120k", "1.2M". |
| 2366 | 20 | `VBox barRow(String label, String value, double share, String colour, double wide, String svg, boolean track)` | One of the card's two bars: what it measures, its figure, and the bar scaled across its group - with no track when `track` is false (0.7.25): a group of one, where a full bar compares nothing, or a card with nothing t... |
| 2395 | 110 | `void refreshOrderBar()` | The order bar: what the page's steppers add up to, its price against the cash, the picked measure now, when what is on site opens and with the order - as a stacked bar and in figures - what the order needs, and one Bu... |
| 2507 | 4 | `Pieces.Press orderBarPress(int units, double total, double cash)` | The order bar's Build (0.7.34): "Build 5 · $X" with the order's total, or "Build 5 on credit · $X" past the cash. |

### A MARKET CATEGORY, IN ITS GROUPS (0.7.25) (lines 2512-2687)

| line | len | member | says |
|---:|---:|---|---|
| 2542 | 47 | `void showMarketCategory(BuildAdvice.Category c)` | A market category's page. |
| 2596 | 18 | `HBox groupHead(BuildCard.Group group)` | A market group's heading - "FOOD MILLS · bread and bakery goods from crops", or "STEEL · from iron ore" where the goods would repeat the name - and at its right the sector's figure the group answers to, both wrapping ... |
| 2620 | 25 | `String groupSubtitle(BuildCard.Group group)` | What a market group is for, beside its name: the goods a maker group makes and what from, off the templates' own goods; a few words for the rest. |
| 2647 | 4 | `static String andList(List<String> words)` | "a, b and c". |
| 2653 | 27 | `String groupNote(BuildCard.Note n)` | A group's note, in words (BuildCard.NoteKind). |
| 2682 | 5 | `static String doors(double shortfall)` | Households of a segment without a door, or the doors to spare (RealEstate.doorShortfall(), negative when there are). |

### THE STAT CARD (lines 2688-3128)

| line | len | member | says |
|---:|---:|---|---|
| 2703 | 5 | `Region cardGap()` | A hairline of space, used where a blank line would be too much. |
| 2727 | 192 | `public List<String> whatItDoes(BuildingsTemplate t)` | Package-private, not private, so BuildMenuCheck can read the sentences back. |
| 2926 | 29 | `List<String> whatSafetyItGives(BuildingsTemplate t)` | The police and the prisons (2026-09-11). |
| 2964 | 75 | `public List<String> whatCareItGives(BuildingsTemplate t)` | The healthcare version, which needs the care type and not the category. |
| 3041 | 16 | `public String jobLabel(JobType job)` | JobType, in words a player reads rather than the enum constant. |
| 3068 | 60 | `HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories)` | The receipt dot, top-right of the menu, and the popover it opens with the last purchases (0.7.20; the card was in the page and showed one). |

### the last purchases (0.7.20) (lines 3129-3451)

| line | len | member | says |
|---:|---:|---|---|
| 3135 | 1 | **type** `record Receipt(int serial, String name, int quantity, double total, double salesTax, int month)` | One purchase, as the receipt shows it: what and how many, what it cost all in, the sales tax in that, and when. |
| 3159 | 7 | `void noteReceipt(Game.BuildQuote quote)` | After an order went through: the receipt the model wrote for it, with the sales tax off the quote it was charged on - quoted just before the order, against the city the order was then placed in, as Game.processBuildOr... |
| 3168 | 4 | `void forgetReceipts()` | Another city, founded or loaded (UserInterface.anotherCity()): its purchases are not this one's. |
| 3173 | 18 | `private void openReceipt(Button dot)` |  |
| 3193 | 6 | `void closeReceipt()` | Close the receipt, if it is open. |
| 3201 | 20 | `private VBox receiptCard()` | The card in the popover: the last purchases, newest first, each with its total and the tax in it. |
| 3222 | 6 | `Label receiptLine(String label, String value)` |  |
| 3237 | 12 | `String schoolHeading(EducationType type)` | A school's heading, and what it is actually for. |
| 3258 | 19 | `String schoolSubtitle(EducationType type)` | One line, like the care subtitles beside it. |
| 3279 | 10 | `String careHeading(CareType care)` | The group's name, in the player's words rather than the enum's. |
| 3297 | 22 | `String careSubtitle(CareType care)` | What building one of these actually gets you. |
| 3329 | 30 | `void showNoDepositMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | The city has the money, the land, and nothing to dig. |
| 3367 | 41 | `void showNoLicenceMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Nobody licensed to practise in it. |
| 3417 | 34 | `void showNoLandMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | The city has the money and nowhere to put the building. |

### ONE FUNDING PAGE FOR THE RUN (0.7.40) (lines 3452-3817)

| line | len | member | says |
|---:|---:|---|---|
| 3492 | 1 | **type** `record RunFrom(String title, EnumSet<BuildingType> categories, boolean cards)` | Where a run came from: the page to come back to, and whether its orders are that page's cards - each taken off its card once placed, the rest left on theirs. |
| 3495 | 5 | `static java.util.LinkedHashMap<BuildingsTemplate, Integer> runOf(BuildingsTemplate t, int n)` | One order as a run of one. |
| 3502 | 8 | `static java.util.LinkedHashMap<BuildingsTemplate, Integer> firstOf(java.util.Map<BuildingsTemplate, Integer> run, int n)` | The first `n` orders of a run. |
| 3512 | 8 | `static String runNames(java.util.Map<BuildingsTemplate, Integer> run)` | A run's orders in words, the order bar's way: "5 × Walk-in Clinic + 3 × Paved Road". |
| 3527 | 10 | `boolean placeRun(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | A run placed (0.7.40): funded first when the orders that can go ahead cost more than the cash (showBuildFunding()), otherwise placed in turn as they always were. |
| 3555 | 19 | `private boolean goAhead(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from, String paper)` | Every order of the run in turn, through buildStack() - the path each card's Build always took - each placed one taken off its card. |
| 3576 | 2 | **type** `record Placed(java.util.LinkedHashMap<BuildingsTemplate, Integer> built, java.util.LinkedHashMap<BuildingsT...` | What placing a run in turn did: the orders placed, the orders left - the one that stopped it first - and buildStack()'s answer there, SUCCESS when nothing stopped it. |
| 3580 | 17 | `Placed placeInTurn(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | goAhead()'s placing, without the page it then draws (a probe runs it): each order through buildStack() until one is not placed, each placed one's receipt noted and, from the cards, its card emptied. |
| 3605 | 2 | **type** `record RunWords(java.util.LinkedHashMap<BuildingsTemplate, Integer> funded, double invoice, double gap, Str...` | What the funding page says about a run, worked out without drawing it (a probe reads it): the orders that can go ahead, their invoice and the gap, the summary's line, what the treasury holds and the bar's part for it,... |
| 3608 | 30 | `RunWords runWords(java.util.Map<BuildingsTemplate, Integer> run)` |  |
| 3640 | 2 | **type** `record RunOffer(String name, DebtQuote quote, String rate, String tone, String ending, Pieces.Press action,...` | One loan the funding page offers, worded - what Pieces.offerCard() draws, as the land office's Offer is: its name, quote, rate's words and colour, ending, button; what booking it does to the model (a probe runs it); a... |
| 3651 | 35 | `List<RunOffer> runOffers(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | The two offers, the old page's on the run's gap: the bond first - what the page recommends, since a building outlives either loan and the matching principle pays for a long-lived asset with long-lived debt - then the ... |
| 3688 | 20 | `HBox runHead(String sub, RunFrom from)` | The run's pages' head: "Build › Funding", the title a way back to the page the run came from, and "‹ <that page>" at the right - the land office's head() on Build. |
| 3715 | 61 | `void showBuildFunding(java.util.Map<BuildingsTemplate, Integer> run, RunFrom from)` | The funding page for a run. |
| 3782 | 14 | `String[] fellShortWords(java.util.Map<BuildingsTemplate, Integer> built, java.util.Map<BuildingsTemplate, Integer> left, RunFro...` | What the fell-short card says, worked out without drawing it: its heading, then what was built, what was not and what it is still short of - the model's gap for the orders left, as it stands now. |
| 3806 | 11 | `void showBuildFellShort(java.util.Map<BuildingsTemplate, Integer> built, java.util.Map<BuildingsTemplate, Integer> left, RunFro...` | The loan went through and an order still did not: one red card in Build's frame, as the land office's "Not bought" (0.7.40; it was THE MONEY IS IN, THE BUILDING IS NOT, a page of its own). |


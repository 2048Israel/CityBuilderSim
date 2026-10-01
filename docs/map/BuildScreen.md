# BuildScreen.java - 2,129 lines · 51 methods · 4 constants · interface

`ham/citybuildersim/ui/BuildScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The build tab: the strip of categories across the top, the constraints bar
> that says what stops a build, the line that says who builds these, and every
> building as a card - its face, its order line, its stats - with the stat
> card's own vocabulary for what a building does and what care it gives.
> 
> Split out of UserInterface on 2026-09-18: the five banners BUILD: THE
> CATEGORY SCREEN IS GONE TOO, THE HALF OF THE CATALOGUE THAT BUILDS ITSELF,
> THE BUILD MENU, ONE BUILDING, AS A CARD and THE STAT CARD exactly as they
> were, the shell's members reached through ui. The shell still reads which
> category is open (buildCategory) for the rail and the scroll memory, and the
> inbox and the panels send the player into a category through
> handleAllBuildingMenus(). BuildMenuCheck reads the three card methods.
> Since 0.7.5 the shell's key filter also calls buildPending() and
> clearPending(): Enter, and Backspace or Delete, on the page showing.

**Uses:** [Palette](Palette.md) (138), [BuildingType](BuildingType.md) (52), [Game](Game.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (19), [CareType](CareType.md) (12), [EducationType](EducationType.md) (9), [LandManager](LandManager.md) (7), [JobType](JobType.md) (6), [Good](Good.md) (6), [DebtQuote](DebtQuote.md) (5), [Health](Health.md) (4), [UserInterface](UserInterface.md) (3), [Crime](Crime.md) (3), [BuildingManager](BuildingManager.md) (2), [Formats](Formats.md) (2), [Bank](Bank.md) (2), [SafetyType](SafetyType.md) (2), [Healthcare](Healthcare.md) (2), [Sickness](Sickness.md) (2), [Migration](Migration.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [ConstructionControl](ConstructionControl.md) (1), [Rail](Rail.md) (1), [CityCalendar](CityCalendar.md) (1), [Rollover](Rollover.md) (1), [Money](Money.md) (1), [DebtManager](DebtManager.md) (1)

**Used by (2):** [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 47 | BUILD: THE CATEGORY SCREEN IS GONE TOO. |
| 69 | THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. |
| 309 | THE BUILD MENU |
| 351 | · · 2. THE BUILDINGS, GROUPED BY WHAT THEY ACTUALLY DO |
| 547 | ONE BUILDING, AS A CARD. |
| 590 | · · the face |
| 649 | · · the order line |
| 803 | · · the stats |
| 1016 | · the keyboard |
| 1114 | THE STAT CARD |
| 1556 | · the last purchases (0.7.20) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 190 | `BuildScreen.BUILD_HOME` | `"Residential"` | Which category the player was last looking at. |
| 257 | `BuildScreen.CITY_DOT` | `Palette.MONEY` | The colour of "only the city builds these": the money blue - the city's own account. |
| 260 | `BuildScreen.INVESTOR_DOT` | `Palette.BUSINESS` | The colour of "investors build these too": the business violet. |
| 1565 | `BuildScreen.RECEIPTS` | `5` | How many purchases the receipt keeps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 191 | `String buildCategory` |  |
| 570 | `final java.util.Set<String> pinnedStats` | Which cards are showing their stats, by building name. |
| 573 | `final java.util.Map<String, Integer> orderQty` | How many of each the player has dialled up, by building name. |
| 1039 | `final List<PageCard> pageCards` | The cards on the category page showing now, in the order they are laid out; each draw starts it again. |
| 1042 | `private String pageTitle` | Which page those are on, so an order placed from the keyboard comes back to it. |
| 1043 | `private EnumSet<BuildingType> pageCategories` |  |
| 1046 | `private Label pendingHint` | The caption under the grid that says the two keys; shown only while something on the page is pending. |
| 1575 | `final java.util.ArrayDeque<Receipt> receipts` | The last RECEIPTS purchases, newest first. |
| 1578 | `private javafx.stage.Popup receiptPopup` | The receipt's popover while it is open, or null. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 2090 | **type** `final class BuildScreen` | The build tab: the strip of categories across the top, the constraints bar that says what stops a build, the line that says who builds these, and every building as a card - its face, its order line, its stats - with t... |
| 45 | 1 | `BuildScreen(UserInterface ui)` |  |

### BUILD: THE CATEGORY SCREEN IS GONE TOO. (lines 47-68)

| line | len | member | says |
|---:|---:|---|---|
| 67 | 1 | **type** `record BuildCategory(String name, EnumSet<BuildingType> types)` | One tab on the build strip: what it is called and what it contains. |

### THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. (lines 69-308)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 8 | `static EnumSet<BuildingType> investorTypes()` |  |
| 102 | 3 | `static boolean investorBuilt(EnumSet<BuildingType> types)` | True when everything in this category is something investors put up. |
| 113 | 4 | `static EnumSet<BuildingType> industrialTypes()` | What a private business builds to sell something. |
| 129 | 3 | `static EnumSet<BuildingType> utilityTypes()` | ...and what the city runs because everything else needs it. |
| 141 | 40 | `static BuildCategory[] buildCategories()` | The strip, in the order a city is actually built. |
| 194 | 10 | `void showBuildMenu()` | The Build tab: the list, in whichever category you were last in. |
| 220 | 35 | `javafx.scene.layout.FlowPane buildStrip(String current)` | The strip itself. |
| 263 | 9 | `static Region whoDot(boolean investors)` | A tab's dot: who builds what is behind it (0.7.21). |
| 274 | 14 | `HBox keyLine()` | The key to the dots, at the right of the page's title: "● only the city builds  ● investors build too". |
| 295 | 12 | `HBox buildHead(String menuTitle, EnumSet<BuildingType> categories)` | The page's head (0.7.21): its title with the building area's swatch, the key to the tabs' dots at the right, the way to the construction page (0.7.22), and the receipt's dot beyond it. |

### THE BUILD MENU (lines 309-546)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 137 | `void handleAllBuildingMenus(String menuTitle, EnumSet<BuildingType> categories)` |  |
| 477 | 17 | `HBox whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories)` | One line under the strip saying whether this is your job. |
| 509 | 37 | `HBox constraintsBar()` | WHAT STOPS A BUILD, across the top, above the categories. |

### ONE BUILDING, AS A CARD. (lines 547-1015)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 267 | `StackPane buildingTile(BuildingsTemplate template, String menuTitle, EnumSet<BuildingType> categories)` |  |
| 844 | 22 | `Button stepper(String glyph, double width, Runnable go)` | A small square button in the quantity stepper. |
| 873 | 16 | `String runningCost(BuildingsTemplate t)` | What one costs to keep, which is not what it costs to buy. |
| 897 | 71 | `VBox tileStats(BuildingsTemplate t)` | The stat block, sized to cover its own card. |
| 970 | 11 | `HBox statPair(String label, String value)` | A label and a figure, on one line, inside a stat cover. |
| 994 | 21 | `boolean placeOrder(BuildingsTemplate template, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Placing the order, and everything the city can say back. |

### the keyboard (lines 1016-1113)

| line | len | member | says |
|---:|---:|---|---|
| 1036 | 1 | **type** `record PageCard(BuildingsTemplate template, Runnable reprice)` | One card on the page showing now: what it builds, and how it reprices itself in place. |
| 1059 | 18 | `boolean buildPending()` | Every pending order on the page, placed as its own Build button would place it, in the page's order. |
| 1084 | 10 | `boolean clearPending()` | Every quantity on the page back to none - the ↺ on every card at once, and like it, each card repriced in place rather than the page redrawn (see REPRICED IN PLACE, in buildingTile). |
| 1100 | 12 | `void showPendingHint()` | The caption under the grid, shown while any card on the page has a quantity - and its line kept while it is not (0.7.20): it was taken out of the layout, so pressing "+" made the page a line taller and moved it. |

### THE STAT CARD (lines 1114-1555)

| line | len | member | says |
|---:|---:|---|---|
| 1129 | 5 | `Region cardGap()` | A hairline of space, used where a blank line would be too much. |
| 1153 | 193 | `public List<String> whatItDoes(BuildingsTemplate t)` | Package-private, not private, so BuildMenuCheck can read the sentences back. |
| 1353 | 29 | `List<String> whatSafetyItGives(BuildingsTemplate t)` | The police and the prisons (2026-09-11). |
| 1391 | 75 | `public List<String> whatCareItGives(BuildingsTemplate t)` | The healthcare version, which needs the care type and not the category. |
| 1468 | 16 | `public String jobLabel(JobType job)` | JobType, in words a player reads rather than the enum constant. |
| 1495 | 60 | `HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories)` | The receipt dot, top-right of the menu, and the popover it opens with the last purchases (0.7.20; the card was in the page and showed one). |

### the last purchases (0.7.20) (lines 1556-2129)

| line | len | member | says |
|---:|---:|---|---|
| 1562 | 1 | **type** `record Receipt(int serial, String name, int quantity, double total, double salesTax, int month)` | One purchase, as the receipt shows it: what and how many, what it cost all in, the sales tax in that, and when. |
| 1586 | 7 | `void noteReceipt(Game.BuildQuote quote)` | After an order went through: the receipt the model wrote for it, with the sales tax off the quote it was charged on - quoted just before the order, against the city the order was then placed in, as Game.processBuildOr... |
| 1595 | 4 | `void forgetReceipts()` | Another city, founded or loaded (UserInterface.anotherCity()): its purchases are not this one's. |
| 1600 | 18 | `private void openReceipt(Button dot)` |  |
| 1620 | 6 | `void closeReceipt()` | Close the receipt, if it is open. |
| 1628 | 20 | `private VBox receiptCard()` | The card in the popover: the last purchases, newest first, each with its total and the tax in it. |
| 1649 | 6 | `Label receiptLine(String label, String value)` |  |
| 1657 | 5 | `Object groupKeyOf(BuildingsTemplate template)` | Which heading a building belongs under: its care type, or what it teaches. |
| 1663 | 5 | `String groupHeading(Object key)` |  |
| 1669 | 5 | `String groupSubtitle(Object key)` |  |
| 1683 | 12 | `String schoolHeading(EducationType type)` | A school's heading, and what it is actually for. |
| 1704 | 19 | `String schoolSubtitle(EducationType type)` | One line, like the care subtitles beside it. |
| 1725 | 10 | `String careHeading(CareType care)` | The group's name, in the player's words rather than the enum's. |
| 1743 | 22 | `String careSubtitle(CareType care)` | What building one of these actually gets you. |
| 1775 | 31 | `void showNoDepositMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | The city has the money, the land, and nothing to dig. |
| 1814 | 42 | `void showNoLicenceMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Nobody licensed to practise in it. |
| 1865 | 35 | `void showNoLandMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | The city has the money and nowhere to put the building. |
| 1917 | 66 | `void showQuickDebtMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | "You cannot afford this - borrow for it?" with the terms on the screen. |
| 1990 | 4 | `VBox fundingOffer(String name, DebtQuote quote, String rate, String atTheEnd, String action, Runnable issue)` | One of the funding page's offers: its rate, its face, the cash it brings, what it costs a month and in all, and what happens at the end - every figure off the quote - then what asking this much does to the city's rate... |
| 2002 | 25 | `VBox fundingOffer(String name, DebtQuote quote, String rate, String atTheEnd, String action, Runnable issue, java.util.function...` | ...with its figures written by `written` - the land office's dollar offers (0.7.13) print theirs in US dollars, since a dollar quote's every figure is in dollars (Game.quoteForeign()). |
| 2048 | 14 | `private void buildOnTheLoan(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats, String ...` | Either offer's money is in: the order is placed again, and whatever it answers is shown. |
| 2073 | 30 | `void showFundingFellShortMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats, Strin...` | The loan went through and the building still did not. |
| 2111 | 18 | `String rateStyle(DebtQuote quote)` | Colours a quoted rate by how punishing it is. |


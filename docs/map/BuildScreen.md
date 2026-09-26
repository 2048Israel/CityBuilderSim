# BuildScreen.java - 1,931 lines · 42 methods · 1 constants · interface

`ham/citybuildersim/ui/BuildScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (109), [BuildingType](BuildingType.md) (51), [BuildingsTemplate](BuildingsTemplate.md) (19), [Game](Game.md) (18), [CareType](CareType.md) (12), [EducationType](EducationType.md) (9), [JobType](JobType.md) (6), [Good](Good.md) (6), [LandManager](LandManager.md) (4), [Health](Health.md) (4), [DebtQuote](DebtQuote.md) (4), [Crime](Crime.md) (3), [UserInterface](UserInterface.md) (2), [BuildingManager](BuildingManager.md) (2), [Bank](Bank.md) (2), [SafetyType](SafetyType.md) (2), [Healthcare](Healthcare.md) (2), [Sickness](Sickness.md) (2), [Migration](Migration.md) (2), [Rail](Rail.md) (1), [DebtManager](DebtManager.md) (1)

**Used by (2):** [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 47 | BUILD: THE CATEGORY SCREEN IS GONE TOO. |
| 69 | THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. |
| 251 | THE BUILD MENU |
| 293 | · · 2. THE BUILDINGS, GROUPED BY WHAT THEY ACTUALLY DO |
| 484 | ONE BUILDING, AS A CARD. |
| 524 | · · the face |
| 562 | · · the order line |
| 714 | · · the stats |
| 924 | · the keyboard |
| 1016 | THE STAT CARD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 190 | `BuildScreen.BUILD_HOME` | `"Residential"` | Which category the player was last looking at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 191 | `String buildCategory` |  |
| 507 | `final java.util.Set<String> pinnedStats` | Which cards are showing their stats, by building name. |
| 510 | `final java.util.Map<String, Integer> orderQty` | How many of each the player has dialled up, by building name. |
| 947 | `final List<PageCard> pageCards` | The cards on the category page showing now, in the order they are laid out; each draw starts it again. |
| 950 | `private String pageTitle` | Which page those are on, so an order placed from the keyboard comes back to it. |
| 951 | `private EnumSet<BuildingType> pageCategories` |  |
| 954 | `private Label pendingHint` | The caption under the grid that says the two keys; shown only while something on the page is pending. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 1892 | **type** `final class BuildScreen` | The build tab: the strip of categories across the top, the constraints bar that says what stops a build, the line that says who builds these, and every building as a card - its face, its order line, its stats - with t... |
| 45 | 1 | `BuildScreen(UserInterface ui)` |  |

### BUILD: THE CATEGORY SCREEN IS GONE TOO. (lines 47-68)

| line | len | member | says |
|---:|---:|---|---|
| 67 | 1 | **type** `record BuildCategory(String name, EnumSet<BuildingType> types)` | One tab on the build strip: what it is called and what it contains. |

### THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. (lines 69-250)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 8 | `static EnumSet<BuildingType> investorTypes()` |  |
| 102 | 3 | `static boolean investorBuilt(EnumSet<BuildingType> types)` | True when everything in this category is something investors put up. |
| 113 | 4 | `static EnumSet<BuildingType> industrialTypes()` | What a private business builds to sell something. |
| 129 | 3 | `static EnumSet<BuildingType> utilityTypes()` | ...and what the city runs because everything else needs it. |
| 141 | 40 | `static BuildCategory[] buildCategories()` | The strip, in the order a city is actually built. |
| 194 | 10 | `void showBuildMenu()` | The Build tab: the list, in whichever category you were last in. |
| 212 | 37 | `javafx.scene.layout.FlowPane buildStrip(String current)` | The strip itself. |

### THE BUILD MENU (lines 251-483)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 133 | `void handleAllBuildingMenus(String menuTitle, EnumSet<BuildingType> categories)` |  |
| 410 | 21 | `Label whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories)` | One line under the strip saying whether this is your job. |
| 446 | 37 | `HBox constraintsBar()` | WHAT STOPS A BUILD, across the top, above the categories. |

### ONE BUILDING, AS A CARD. (lines 484-923)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 241 | `StackPane buildingTile(BuildingsTemplate template, String menuTitle, EnumSet<BuildingType> categories)` |  |
| 755 | 22 | `Button stepper(String glyph, double width, Runnable go)` | A small square button in the quantity stepper. |
| 784 | 16 | `String runningCost(BuildingsTemplate t)` | What one costs to keep, which is not what it costs to buy. |
| 808 | 71 | `VBox tileStats(BuildingsTemplate t)` | The stat block, sized to cover its own card. |
| 881 | 11 | `HBox statPair(String label, String value)` | A label and a figure, on one line, inside a stat cover. |
| 905 | 18 | `boolean placeOrder(BuildingsTemplate template, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Placing the order, and everything the city can say back. |

### the keyboard (lines 924-1015)

| line | len | member | says |
|---:|---:|---|---|
| 944 | 1 | **type** `record PageCard(BuildingsTemplate template, Runnable reprice)` | One card on the page showing now: what it builds, and how it reprices itself in place. |
| 967 | 18 | `boolean buildPending()` | Every pending order on the page, placed as its own Build button would place it, in the page's order. |
| 992 | 10 | `boolean clearPending()` | Every quantity on the page back to none - the ↺ on every card at once, and like it, each card repriced in place rather than the page redrawn (see REPRICED IN PLACE, in buildingTile). |
| 1004 | 10 | `void showPendingHint()` | The caption under the grid, shown while any card on the page has a quantity. |

### THE STAT CARD (lines 1016-1931)

| line | len | member | says |
|---:|---:|---|---|
| 1031 | 5 | `Region cardGap()` | A hairline of space, used where a blank line would be too much. |
| 1055 | 193 | `public List<String> whatItDoes(BuildingsTemplate t)` | Package-private, not private, so BuildMenuCheck can read the sentences back. |
| 1255 | 29 | `List<String> whatSafetyItGives(BuildingsTemplate t)` | The police and the prisons (2026-09-11). |
| 1293 | 75 | `public List<String> whatCareItGives(BuildingsTemplate t)` | The healthcare version, which needs the care type and not the category. |
| 1370 | 16 | `public String jobLabel(JobType job)` | JobType, in words a player reads rather than the enum constant. |
| 1396 | 69 | `HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories)` | The receipt dot, top-right of the menu, and the card it opens. |
| 1466 | 6 | `Label receiptLine(String label, String value)` |  |
| 1474 | 5 | `Object groupKeyOf(BuildingsTemplate template)` | Which heading a building belongs under: its care type, or what it teaches. |
| 1480 | 5 | `String groupHeading(Object key)` |  |
| 1486 | 5 | `String groupSubtitle(Object key)` |  |
| 1500 | 12 | `String schoolHeading(EducationType type)` | A school's heading, and what it is actually for. |
| 1521 | 19 | `String schoolSubtitle(EducationType type)` | One line, like the care subtitles beside it. |
| 1542 | 10 | `String careHeading(CareType care)` | The group's name, in the player's words rather than the enum's. |
| 1560 | 22 | `String careSubtitle(CareType care)` | What building one of these actually gets you. |
| 1592 | 31 | `void showNoDepositMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | The city has the money, the land, and nothing to dig. |
| 1631 | 42 | `void showNoLicenceMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Nobody licensed to practise in it. |
| 1682 | 35 | `void showNoLandMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | The city has the money and nowhere to put the building. |
| 1734 | 64 | `void showQuickDebtMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | "You cannot afford this - borrow for it?" with the terms on the screen. |
| 1805 | 25 | `private VBox fundingOffer(String name, DebtQuote quote, String rate, String atTheEnd, String action, Runnable issue)` | One of the funding page's offers: its rate, its face, the cash it brings, what it costs a month and in all, and what happens at the end - every figure off the quote - then what asking this much does to the city's rate... |
| 1851 | 13 | `private void buildOnTheLoan(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats, String ...` | Either offer's money is in: the order is placed again, and whatever it answers is shown. |
| 1875 | 30 | `void showFundingFellShortMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats, Strin...` | The loan went through and the building still did not. |
| 1913 | 18 | `String rateStyle(DebtQuote quote)` | Colours a quoted rate by how punishing it is. |


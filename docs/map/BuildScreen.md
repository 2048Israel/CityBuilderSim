# BuildScreen.java - 1,724 lines · 37 methods · 1 constants · interface

`ham/citybuildersim/ui/BuildScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (101), [BuildingType](BuildingType.md) (47), [BuildingsTemplate](BuildingsTemplate.md) (17), [CareType](CareType.md) (12), [EducationType](EducationType.md) (9), [Game](Game.md) (9), [JobType](JobType.md) (6), [Good](Good.md) (6), [LandManager](LandManager.md) (4), [Health](Health.md) (4), [Crime](Crime.md) (3), [UserInterface](UserInterface.md) (2), [BuildingManager](BuildingManager.md) (2), [Bank](Bank.md) (2), [SafetyType](SafetyType.md) (2), [Healthcare](Healthcare.md) (2), [Sickness](Sickness.md) (2), [Migration](Migration.md) (2), [DebtQuote](DebtQuote.md) (2), [Rail](Rail.md) (1), [DebtManager](DebtManager.md) (1)

**Used by (2):** [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 45 | BUILD: THE CATEGORY SCREEN IS GONE TOO. |
| 67 | THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. |
| 249 | THE BUILD MENU |
| 278 | · · 2. THE BUILDINGS, GROUPED BY WHAT THEY ACTUALLY DO |
| 464 | ONE BUILDING, AS A CARD. |
| 504 | · · the face |
| 542 | · · the order line |
| 682 | · · the stats |
| 887 | THE STAT CARD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 188 | `BuildScreen.BUILD_HOME` | `"Residential"` | Which category the player was last looking at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 189 | `String buildCategory` |  |
| 487 | `final java.util.Set<String> pinnedStats` | Which cards are showing their stats, by building name. |
| 490 | `final java.util.Map<String, Integer> orderQty` | How many of each the player has dialled up, by building name. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 1687 | **type** `final class BuildScreen` | The build tab: the strip of categories across the top, the constraints bar that says what stops a build, the line that says who builds these, and every building as a card - its face, its order line, its stats - with t... |
| 43 | 1 | `BuildScreen(UserInterface ui)` |  |

### BUILD: THE CATEGORY SCREEN IS GONE TOO. (lines 45-66)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1 | **type** `record BuildCategory(String name, EnumSet<BuildingType> types)` | One tab on the build strip: what it is called and what it contains. |

### THE HALF OF THE CATALOGUE THAT BUILDS ITSELF. (lines 67-248)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 8 | `static EnumSet<BuildingType> investorTypes()` |  |
| 100 | 3 | `static boolean investorBuilt(EnumSet<BuildingType> types)` | True when everything in this category is something investors put up. |
| 111 | 4 | `static EnumSet<BuildingType> industrialTypes()` | What a private business builds to sell something. |
| 127 | 3 | `static EnumSet<BuildingType> utilityTypes()` | ...and what the city runs because everything else needs it. |
| 139 | 40 | `static BuildCategory[] buildCategories()` | The strip, in the order a city is actually built. |
| 192 | 10 | `void showBuildMenu()` | The Build tab: the list, in whichever category you were last in. |
| 210 | 37 | `javafx.scene.layout.FlowPane buildStrip(String current)` | The strip itself. |

### THE BUILD MENU (lines 249-463)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 115 | `void handleAllBuildingMenus(String menuTitle, EnumSet<BuildingType> categories)` |  |
| 390 | 21 | `Label whoBuildsThis(String menuTitle, EnumSet<BuildingType> categories)` | One line under the strip saying whether this is your job. |
| 426 | 37 | `HBox constraintsBar()` | WHAT STOPS A BUILD, across the top, above the categories. |

### ONE BUILDING, AS A CARD. (lines 464-886)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 228 | `StackPane buildingTile(BuildingsTemplate template, String menuTitle, EnumSet<BuildingType> categories)` |  |
| 722 | 22 | `Button stepper(String glyph, double width, Runnable go)` | A small square button in the quantity stepper. |
| 751 | 16 | `String runningCost(BuildingsTemplate t)` | What one costs to keep, which is not what it costs to buy. |
| 775 | 71 | `VBox tileStats(BuildingsTemplate t)` | The stat block, sized to cover its own card. |
| 848 | 11 | `HBox statPair(String label, String value)` | A label and a figure, on one line, inside a stat cover. |
| 868 | 17 | `void placeOrder(BuildingsTemplate template, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Placing the order, and everything the city can say back. |

### THE STAT CARD (lines 887-1724)

| line | len | member | says |
|---:|---:|---|---|
| 902 | 5 | `Region cardGap()` | A hairline of space, used where a blank line would be too much. |
| 926 | 193 | `public List<String> whatItDoes(BuildingsTemplate t)` | Package-private, not private, so BuildMenuCheck can read the sentences back. |
| 1126 | 29 | `List<String> whatSafetyItGives(BuildingsTemplate t)` | The police and the prisons (2026-09-11). |
| 1164 | 75 | `public List<String> whatCareItGives(BuildingsTemplate t)` | The healthcare version, which needs the care type and not the category. |
| 1241 | 16 | `public String jobLabel(JobType job)` | JobType, in words a player reads rather than the enum constant. |
| 1267 | 69 | `HBox receiptCorner(String menuTitle, EnumSet<BuildingType> categories)` | The receipt dot, top-right of the menu, and the card it opens. |
| 1337 | 6 | `Label receiptLine(String label, String value)` |  |
| 1345 | 5 | `Object groupKeyOf(BuildingsTemplate template)` | Which heading a building belongs under: its care type, or what it teaches. |
| 1351 | 5 | `String groupHeading(Object key)` |  |
| 1357 | 5 | `String groupSubtitle(Object key)` |  |
| 1371 | 12 | `String schoolHeading(EducationType type)` | A school's heading, and what it is actually for. |
| 1392 | 19 | `String schoolSubtitle(EducationType type)` | One line, like the care subtitles beside it. |
| 1413 | 10 | `String careHeading(CareType care)` | The group's name, in the player's words rather than the enum's. |
| 1431 | 22 | `String careSubtitle(CareType care)` | What building one of these actually gets you. |
| 1471 | 31 | `void showNoDepositMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | The city has the money, the land, and nothing to dig. |
| 1510 | 42 | `void showNoLicenceMenu(BuildingsTemplate selected, int quantity, String menuTitle, EnumSet<BuildingType> categories)` | Nobody licensed to practise in it. |
| 1553 | 35 | `void showNoLandMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` |  |
| 1598 | 61 | `void showQuickDebtMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | "You cannot afford this - borrow for it?" with the terms on the screen. |
| 1668 | 30 | `void showFundingFellShortMenu(BuildingsTemplate selected, int quantity, String prevTitle, EnumSet<BuildingType> prevCats)` | The note went through and the building still did not. |
| 1706 | 18 | `String rateStyle(DebtQuote quote)` | Colours a quoted rate by how punishing it is. |


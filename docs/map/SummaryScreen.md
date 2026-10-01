# SummaryScreen.java - 1,695 lines · 29 methods · 6 constants · interface

`ham/citybuildersim/ui/SummaryScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The left panel's content: the summary and the dashboard - the vitals, the
> alert block, the six lines that are always worth a glance or the thirteen
> folded sections - the problem list that decides what goes red, and every
> row's own reading of the city, seats against who would come.
> 
> Split out of UserInterface on 2026-09-18: the six banners CITY OVERVIEW
> PANEL, THE LEFT PANEL, SUMMARY, OR DASHBOARD, HEADROOM, NOT SATISFACTION,
> THE SUMMARY IS A PROBLEM LIST NOW and SEATS AGAINST WHO WOULD COME exactly
> as they were, the shell's members reached through ui. The shell owns the
> panel itself (cityPanel) and its scroller, and calls refreshCityPanel() on
> the clock; this class owns what is drawn into it, and which sections the
> player has opened (panelOpen).

**Uses:** [Palette](Palette.md) (29), [CareType](CareType.md) (17), [BuildingType](BuildingType.md) (15), [EducationType](EducationType.md) (7), [Crime](Crime.md) (6), [InfrastructureManager](InfrastructureManager.md) (5), [LandManager](LandManager.md) (5), [Health](Health.md) (5), [Education](Education.md) (4), [PopulationManager](PopulationManager.md) (4), [Healthcare](Healthcare.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [LabourMarket](LabourMarket.md) (3), [EconomyManager](EconomyManager.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [WageBand](WageBand.md) (3), [UserInterface](UserInterface.md) (2), [Bank](Bank.md) (2), [SafetyType](SafetyType.md) (2), [PolicyScreen](PolicyScreen.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [BuildingManager](BuildingManager.md) (2), [Currency](Currency.md) (2), [CapitalFlows](CapitalFlows.md) (2), [BuildingsStacks](BuildingsStacks.md) (1), [FamilyModel](FamilyModel.md) (1), [Game](Game.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Good](Good.md) (1)... and 2 more

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 39 | HEADROOM, NOT SATISFACTION |
| 111 | CITY OVERVIEW PANEL |
| 123 | THE LEFT PANEL |
| 249 | SUMMARY, OR DASHBOARD |
| 391 | THE SUMMARY IS A PROBLEM LIST NOW. |
| 492 | SEATS AGAINST WHO WOULD COME. |
| 617 | · · the networks |
| 653 | · · the care |
| 701 | · · the schools |
| 723 | · · and the schools above them |
| 726 | · · the police |
| 740 | · · the housing |
| 752 | · · the ground |
| 779 | · · the money |
| 802 | · · the promises |
| 936 | · · what needs you |
| 964 | · · the symptoms |
| 1027 | · ECONOMY |
| 1052 | · BANK |
| 1088 | · TRADE - the city's edge, in one line. |
| 1120 | · THE TWO POCKETS, AND WHICH MONEY EACH IS IN. |
| 1223 | · TAX |
| 1245 | · LABOUR - and this is the one Jerus asked for by name. |
| 1338 | · SCHOOLS |
| 1374 | · PEOPLE |
| 1392 | · HEALTH |
| 1421 | · SAFETY (2026-09-11) |
| 1451 | · RESOURCES |
| 1476 | · LAND |
| 1497 | · SECTOR CASH |
| 1509 | · BUILDINGS, and this is where the folding pays for itself. |
| 1578 | · THE VITALS, which are never folded away. |
| 1610 | · AND WHATEVER IS ACTUALLY WRONG. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 148 | `SummaryScreen.PANEL_LABEL` | `Palette.TEXT_LABEL` |  |
| 149 | `SummaryScreen.PANEL_VALUE` | `Palette.TEXT_HEAD` |  |
| 150 | `SummaryScreen.PANEL_GOOD` | `Palette.GOOD` |  |
| 151 | `SummaryScreen.PANEL_WARN` | `Palette.WARN` |  |
| 152 | `SummaryScreen.PANEL_BAD` | `Palette.BAD` |  |
| 377 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "sa...` | Every section key, so open-all does not have to be kept in step by hand. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 146 | `final java.util.Set<String> panelOpen` | Which sections and rows the player has opened. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 1664 | **type** `final class SummaryScreen` | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen folded sections - the problem list that decides what goes red, and ev... |
| 37 | 1 | `SummaryScreen(UserInterface ui)` |  |

### HEADROOM, NOT SATISFACTION (lines 39-110)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 15 | `HBox utilityLine(String label, double consumption, double production)` | Amber from three-quarters, red once there is no headroom left. |
| 87 | 7 | `HBox roadLine()` | Roads, red once traffic is actually being held up. |
| 103 | 7 | `String roadSummary()` | Roads on the city overview, in one cell. |

### CITY OVERVIEW PANEL (lines 111-122)

### THE LEFT PANEL (lines 123-248)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 3 | `HBox statLine(String label, String value)` |  |
| 165 | 19 | `HBox statLine(String label, String value, String tone)` | One row: what it is on the left, what it reads on the right. |
| 198 | 43 | `VBox panelSection(String key, String heading, String summary, String tone, java.util.function.Supplier<VBox> detail)` | A row that hides something, and says so. |
| 243 | 5 | `VBox panelBody(javafx.scene.Node...rows)` | A section's detail, built from rows. |

### SUMMARY, OR DASHBOARD (lines 249-390)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 10 | `HBox panelModeSwitch()` |  |
| 295 | 16 | `Label panelModeChip(String text, boolean on, boolean dashboard)` |  |
| 327 | 22 | `VBox summaryRow(String heading, String value, String tone, Runnable go)` | One row of the summary: what it is, and what it reads. |
| 351 | 16 | `HBox panelFoldAll()` | Open everything, or close it. |
| 368 | 7 | `Label foldLink(String text, Runnable act)` |  |
| 383 | 7 | `Label panelNote(String text)` | A caption inside an open section - a sub-heading, or a note. |

### THE SUMMARY IS A PROBLEM LIST NOW. (lines 391-491)

| line | len | member | says |
|---:|---:|---|---|
| 435 | 1 | **type** `record Watch(String label, String reading, int level, double near, Runnable go)` | One thing being watched. |
| 438 | 6 | `void over(java.util.List<Watch> out, String label, String reading, double value, double yellow, double red, Runnable go)` | Higher is worse. |
| 446 | 6 | `void under(java.util.List<Watch> out, String label, String reading, double value, double yellow, double red, Runnable go)` | Lower is worse. |
| 454 | 4 | `void flag(java.util.List<Watch> out, String label, String reading, boolean bad, boolean severe, Runnable go)` | A thing that is simply true or not. |
| 470 | 16 | `void onTheWay(java.util.List<Watch> out, int at, java.util.function.Predicate<BuildingsTemplate> serves)` | ...AND WHAT IS ALREADY ON THE WAY (0.7.20). |
| 488 | 3 | `void onTheWay(java.util.List<Watch> out, java.util.function.Predicate<BuildingsTemplate> serves)` | The same, for the watch just measured. |

### SEATS AGAINST WHO WOULD COME. (lines 492-1695)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 38 | `void seatsWanted(java.util.List<Watch> out)` |  |
| 570 | 22 | `void network(java.util.List<Watch> out, String label, double demand, double supply, double ratio)` | One network: how much of its capacity is spoken for, and whether it is still meeting demand. |
| 600 | 247 | `java.util.List<Watch> watchAll()` | Everything with a lever, measured against its own line. |
| 856 | 74 | `java.util.List<Watch> citySymptoms()` | The readings with no dial of their own. |
| 931 | 49 | `void panelSummaryRows(VBox body)` |  |
| 982 | 16 | `VBox panelHeading(String text)` | A rule and a caption, dividing the panel's two halves. |
| 1007 | 538 | `void panelDashboardSections(VBox body)` | The thirteen sections, folded the way the player left them. |
| 1546 | 124 | `void refreshCityPanel()` |  |
| 1681 | 9 | `HBox careLine(String label, CareType care, double needed, double[] staffing)` | One coverage row: the percentage, and the two numbers behind it. |
| 1692 | 3 | `String shorten(String name)` | Keeps building names inside the panel's fixed-width column. |


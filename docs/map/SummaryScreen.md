# SummaryScreen.java - 1,497 lines · 30 methods · 7 constants · interface

`ham/citybuildersim/ui/SummaryScreen.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The left panel's content: the summary and the dashboard - the vitals, the
> alert block, the six lines that are always worth a glance or the thirteen
> folded sections - and the problem list that decides what goes red, drawn
> from CityNeeds.
> 
> Split out of UserInterface on 2026-09-18, with the banners CITY OVERVIEW
> PANEL, THE LEFT PANEL, SUMMARY, OR DASHBOARD, HEADROOM, NOT SATISFACTION,
> THE SUMMARY IS A PROBLEM LIST NOW and SEATS AGAINST WHO WOULD COME exactly
> as they were, the shell's members reached through ui; five since 0.7.24,
> when the last went to the model (below). The shell owns the
> panel itself (cityPanel) and calls refreshCityPanel() on the clock; this
> class owns what is drawn into it, and which sections the player has opened
> (panelOpen). Since 0.7.24 the panel is a drawer the header's "Needs you"
> chip opens (UserInterface, THE FRAME FOLDS AWAY) - always on NEEDS YOU
> (needsView) - and NEEDS YOU is measured in the
> model, CityNeeds - the list, its lines and SEATS AGAINST WHO WOULD COME
> moved there whole - so the Build tab and the chip read the same verdicts.

**Uses:** [CityNeeds](CityNeeds.md) (50), [Palette](Palette.md) (29), [CareType](CareType.md) (14), [BuildScreen](BuildScreen.md) (7), [ForeignAccounts](ForeignAccounts.md) (7), [BuildAdvice](BuildAdvice.md) (6), [Health](Health.md) (5), [LandManager](LandManager.md) (5), [Crime](Crime.md) (5), [PolicyScreen](PolicyScreen.md) (4), [Money](Money.md) (3), [PopulationManager](PopulationManager.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [Healthcare](Healthcare.md) (3), [WageBand](WageBand.md) (3), [EducationType](EducationType.md) (3), [UserInterface](UserInterface.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [PeopleScreen](PeopleScreen.md) (2), [EconomyManager](EconomyManager.md) (2), [BuildingManager](BuildingManager.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [LabourMarket](LabourMarket.md) (2), [Currency](Currency.md) (2), [CapitalFlows](CapitalFlows.md) (2), [Pieces](Pieces.md) (1), [FinancesScreen](FinancesScreen.md) (1), [TradeScreen](TradeScreen.md) (1), [Bank](Bank.md) (1), [Game](Game.md) (1)... and 6 more

**Used by (9):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 44 | HEADROOM, NOT SATISFACTION |
| 124 | CITY OVERVIEW PANEL |
| 136 | THE LEFT PANEL |
| 262 | SUMMARY, OR DASHBOARD |
| 422 | THE SUMMARY IS A PROBLEM LIST NOW. |
| 693 | · · what needs you |
| 722 | · · the symptoms |
| 787 | · ECONOMY |
| 815 | · BANK |
| 851 | · TRADE - the city's edge, in one line. |
| 886 | · THE TWO POCKETS, AND WHICH MONEY EACH IS IN. |
| 989 | · TAX |
| 1011 | · LABOUR - and this is the one Jerus asked for by name. |
| 1104 | · SCHOOLS |
| 1142 | · PEOPLE |
| 1160 | · HEALTH |
| 1189 | · SAFETY (2026-09-11) |
| 1219 | · RESOURCES |
| 1250 | · LAND |
| 1289 | · SECTOR CASH |
| 1301 | · BUILDINGS, and this is where the folding pays for itself. |
| 1370 | · THE VITALS, which are never folded away. |
| 1402 | · AND WHATEVER IS ACTUALLY WRONG. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 161 | `SummaryScreen.PANEL_LABEL` | `Palette.TEXT_LABEL` |  |
| 162 | `SummaryScreen.PANEL_VALUE` | `Palette.TEXT_HEAD` |  |
| 163 | `SummaryScreen.PANEL_GOOD` | `Palette.GOOD` |  |
| 164 | `SummaryScreen.PANEL_WARN` | `Palette.WARN` |  |
| 165 | `SummaryScreen.PANEL_BAD` | `Palette.BAD` |  |
| 408 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "sa...` | Every section key, so open-all does not have to be kept in step by hand. |
| 502 | `SummaryScreen.WORDS` | `new CityNeeds.Words() { @ Override public String people(double count) { retur...` | The interface's own words for a figure, which the needs are read in (CityNeeds.Words). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 159 | `final java.util.Set<String> panelOpen` | Which sections and rows the player has opened. |
| 306 | `boolean needsView` | THE CHIP OPENS ON NEEDS YOU (0.7.24, after the PC check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 1461 | **type** `final class SummaryScreen` | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen folded sections - and the problem list that decides what goes red, dr... |
| 42 | 1 | `SummaryScreen(UserInterface ui)` |  |

### HEADROOM, NOT SATISFACTION (lines 44-123)

| line | len | member | says |
|---:|---:|---|---|
| 62 | 13 | `HBox utilityLine(String label, double served, double consumption, double production)` | What a network serves (0.7.41; it was "% used"), in the one verdict: amber from a third in hand, red once it serves no more than it is asked. |
| 93 | 6 | `HBox roadLine()` | Roads, in NEEDS YOU's colour for them (0.7.29) - in the one verdict on what the road serves since 0.7.41. |
| 101 | 4 | `String roadColour()` | The road's verdict: since 0.7.41 the one verdict on what it serves, as every road gauge has it (it was NEEDS YOU's ROADS row's level, amber while road sites were on the way). |
| 118 | 5 | `String roadSummary()` | Roads on the city overview, in one cell: "62% served · 56% flow" (0.7.29; "162% full" until 0.7.41). |

### CITY OVERVIEW PANEL (lines 124-135)

### THE LEFT PANEL (lines 136-261)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 3 | `HBox statLine(String label, String value)` |  |
| 178 | 19 | `HBox statLine(String label, String value, String tone)` | One row: what it is on the left, what it reads on the right. |
| 211 | 43 | `VBox panelSection(String key, String heading, String summary, String tone, java.util.function.Supplier<VBox> detail)` | A row that hides something, and says so. |
| 256 | 5 | `VBox panelBody(javafx.scene.Node...rows)` | A section's detail, built from rows. |

### SUMMARY, OR DASHBOARD (lines 262-421)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 3 | `boolean dashboardShown()` | Whether the panel draws as Dashboard now: the stored mode, unless the chip opened it on NEEDS YOU. |
| 313 | 10 | `HBox panelModeSwitch()` |  |
| 324 | 18 | `Label panelModeChip(String text, boolean on, boolean dashboard)` |  |
| 358 | 22 | `VBox summaryRow(String heading, String value, String tone, Runnable go)` | One row of the summary: what it is, and what it reads. |
| 382 | 16 | `HBox panelFoldAll()` | Open everything, or close it. |
| 399 | 7 | `Label foldLink(String text, Runnable act)` |  |
| 414 | 7 | `Label panelNote(String text)` | A caption inside an open section - a sub-heading, or a note. |

### THE SUMMARY IS A PROBLEM LIST NOW. (lines 422-1497)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 8 | **type** `record Watch(String label, String reading, int level, double near, Runnable go, String tip, int verdict)` | One thing being watched. |
| 477 | 3 | `Watch(String label, String reading, int level, double near, Runnable go)` _(in SummaryScreen.Watch)_ |  |
| 480 | 3 | `Watch(String label, String reading, int level, double near, Runnable go, String tip)` _(in SummaryScreen.Watch)_ |  |
| 486 | 6 | `void over(java.util.List<Watch> out, String label, String reading, double value, double yellow, double red, Runnable go)` | Higher is worse. |
| 494 | 6 | `void under(java.util.List<Watch> out, String label, String reading, double value, double yellow, double red, Runnable go)` | Lower is worse. |
| 516 | 10 | `java.util.List<Watch> watchAll()` | Everything with a lever, measured against its own line: CityNeeds' list (0.7.24; it was measured here), each need with the door to the screen that answers it. |
| 533 | 7 | `Runnable financesDoor(CityNeeds.Kind kind)` | Where a Finances row goes (0.7.32, the Finances spec's D17), through FinancesScreen.open() so it lands where it says rather than on the last page the tab had open (its B8): TREASURY on the hub, FALLS DUE on the hub's ... |
| 547 | 39 | `Runnable goTo(CityNeeds.Go go)` | Where a need's row goes: the Build category that answers it, the land office, the builders' books, Finances, the bank, or the Policy page of the promise, the wage floor or the taxes - the doors watchAll() opened befor... |
| 595 | 92 | `java.util.List<Watch> citySymptoms()` | The readings with no dial of their own. |
| 688 | 52 | `void panelSummaryRows(VBox body)` |  |
| 742 | 16 | `VBox panelHeading(String text)` | A rule and a caption, dividing the panel's two halves. |
| 767 | 570 | `void panelDashboardSections(VBox body)` | The thirteen sections, folded the way the player left them. |
| 1338 | 129 | `void refreshCityPanel()` |  |
| 1480 | 12 | `HBox careLine(String label, CareType care, double needed, double[] staffing)` | One coverage row: the percentage, and the two numbers behind it. |
| 1494 | 3 | `String shorten(String name)` | Keeps building names inside the panel's fixed-width column. |


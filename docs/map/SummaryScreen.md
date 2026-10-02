# SummaryScreen.java - 1,456 lines · 29 methods · 7 constants · interface

`ham/citybuildersim/ui/SummaryScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (29), [CityNeeds](CityNeeds.md) (15), [ForeignAccounts](ForeignAccounts.md) (7), [CareType](CareType.md) (7), [BuildAdvice](BuildAdvice.md) (6), [Health](Health.md) (5), [Crime](Crime.md) (5), [LandManager](LandManager.md) (4), [Money](Money.md) (3), [PopulationManager](PopulationManager.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [Healthcare](Healthcare.md) (3), [WageBand](WageBand.md) (3), [EducationType](EducationType.md) (3), [UserInterface](UserInterface.md) (2), [BuildScreen](BuildScreen.md) (2), [PolicyScreen](PolicyScreen.md) (2), [PeopleScreen](PeopleScreen.md) (2), [EconomyManager](EconomyManager.md) (2), [BuildingManager](BuildingManager.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [LabourMarket](LabourMarket.md) (2), [Currency](Currency.md) (2), [CapitalFlows](CapitalFlows.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [Pieces](Pieces.md) (1), [FinancesScreen](FinancesScreen.md) (1), [TradeScreen](TradeScreen.md) (1), [Bank](Bank.md) (1), [Game](Game.md) (1)... and 6 more

**Used by (9):** [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 44 | HEADROOM, NOT SATISFACTION |
| 123 | CITY OVERVIEW PANEL |
| 135 | THE LEFT PANEL |
| 261 | SUMMARY, OR DASHBOARD |
| 421 | THE SUMMARY IS A PROBLEM LIST NOW. |
| 677 | · · what needs you |
| 705 | · · the symptoms |
| 770 | · ECONOMY |
| 795 | · BANK |
| 831 | · TRADE - the city's edge, in one line. |
| 866 | · THE TWO POCKETS, AND WHICH MONEY EACH IS IN. |
| 969 | · TAX |
| 991 | · LABOUR - and this is the one Jerus asked for by name. |
| 1084 | · SCHOOLS |
| 1120 | · PEOPLE |
| 1138 | · HEALTH |
| 1167 | · SAFETY (2026-09-11) |
| 1197 | · RESOURCES |
| 1222 | · LAND |
| 1254 | · SECTOR CASH |
| 1266 | · BUILDINGS, and this is where the folding pays for itself. |
| 1335 | · THE VITALS, which are never folded away. |
| 1367 | · AND WHATEVER IS ACTUALLY WRONG. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 160 | `SummaryScreen.PANEL_LABEL` | `Palette.TEXT_LABEL` |  |
| 161 | `SummaryScreen.PANEL_VALUE` | `Palette.TEXT_HEAD` |  |
| 162 | `SummaryScreen.PANEL_GOOD` | `Palette.GOOD` |  |
| 163 | `SummaryScreen.PANEL_WARN` | `Palette.WARN` |  |
| 164 | `SummaryScreen.PANEL_BAD` | `Palette.BAD` |  |
| 407 | `SummaryScreen.PANEL_SECTIONS` | `{ "econ", "bank", "trade", "tax", "labour", "school", "people", "health", "sa...` | Every section key, so open-all does not have to be kept in step by hand. |
| 495 | `SummaryScreen.WORDS` | `new CityNeeds.Words() { @ Override public String people(double count) { retur...` | The interface's own words for a figure, which the needs are read in (CityNeeds.Words). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 158 | `final java.util.Set<String> panelOpen` | Which sections and rows the player has opened. |
| 305 | `boolean needsView` | THE CHIP OPENS ON NEEDS YOU (0.7.24, after the PC check). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 1420 | **type** `final class SummaryScreen` | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen folded sections - and the problem list that decides what goes red, dr... |
| 42 | 1 | `SummaryScreen(UserInterface ui)` |  |

### HEADROOM, NOT SATISFACTION (lines 44-122)

| line | len | member | says |
|---:|---:|---|---|
| 63 | 15 | `HBox utilityLine(String label, double consumption, double production)` | Amber from three-quarters, red once there is no headroom left. |
| 92 | 6 | `HBox roadLine()` | Roads, in NEEDS YOU's colour for them (0.7.29). |
| 100 | 7 | `String roadColour()` | The road's verdict: NEEDS YOU's ROADS row's level - amber while road sites are on the way. |
| 118 | 4 | `String roadSummary()` | Roads on the city overview, in one cell: "162% full · 56% flow" (0.7.29). |

### CITY OVERVIEW PANEL (lines 123-134)

### THE LEFT PANEL (lines 135-260)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 3 | `HBox statLine(String label, String value)` |  |
| 177 | 19 | `HBox statLine(String label, String value, String tone)` | One row: what it is on the left, what it reads on the right. |
| 210 | 43 | `VBox panelSection(String key, String heading, String summary, String tone, java.util.function.Supplier<VBox> detail)` | A row that hides something, and says so. |
| 255 | 5 | `VBox panelBody(javafx.scene.Node...rows)` | A section's detail, built from rows. |

### SUMMARY, OR DASHBOARD (lines 261-420)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 3 | `boolean dashboardShown()` | Whether the panel draws as Dashboard now: the stored mode, unless the chip opened it on NEEDS YOU. |
| 312 | 10 | `HBox panelModeSwitch()` |  |
| 323 | 18 | `Label panelModeChip(String text, boolean on, boolean dashboard)` |  |
| 357 | 22 | `VBox summaryRow(String heading, String value, String tone, Runnable go)` | One row of the summary: what it is, and what it reads. |
| 381 | 16 | `HBox panelFoldAll()` | Open everything, or close it. |
| 398 | 7 | `Label foldLink(String text, Runnable act)` |  |
| 413 | 7 | `Label panelNote(String text)` | A caption inside an open section - a sub-heading, or a note. |

### THE SUMMARY IS A PROBLEM LIST NOW. (lines 421-1456)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 5 | **type** `record Watch(String label, String reading, int level, double near, Runnable go, String tip)` | One thing being watched. |
| 473 | 3 | `Watch(String label, String reading, int level, double near, Runnable go)` _(in SummaryScreen.Watch)_ |  |
| 479 | 6 | `void over(java.util.List<Watch> out, String label, String reading, double value, double yellow, double red, Runnable go)` | Higher is worse. |
| 487 | 6 | `void under(java.util.List<Watch> out, String label, String reading, double value, double yellow, double red, Runnable go)` | Lower is worse. |
| 509 | 8 | `java.util.List<Watch> watchAll()` | Everything with a lever, measured against its own line: CityNeeds' list (0.7.24; it was measured here), each need with the door to the screen that answers it. |
| 524 | 7 | `Runnable financesDoor(CityNeeds.Kind kind)` | Where a Finances row goes (0.7.32, the Finances spec's D17), through FinancesScreen.open() so it lands where it says rather than on the last page the tab had open (its B8): TREASURY on the hub, FALLS DUE on the hub's ... |
| 538 | 32 | `Runnable goTo(CityNeeds.Go go)` | Where a need's row goes: the Build category that answers it, the land office, the builders' books, Finances, the bank, or the Policy page of the promise, the wage floor or the taxes - the doors watchAll() opened befor... |
| 579 | 92 | `java.util.List<Watch> citySymptoms()` | The readings with no dial of their own. |
| 672 | 51 | `void panelSummaryRows(VBox body)` |  |
| 725 | 16 | `VBox panelHeading(String text)` | A rule and a caption, dividing the panel's two halves. |
| 750 | 552 | `void panelDashboardSections(VBox body)` | The thirteen sections, folded the way the player left them. |
| 1303 | 128 | `void refreshCityPanel()` |  |
| 1442 | 9 | `HBox careLine(String label, CareType care, double needed, double[] staffing)` | One coverage row: the percentage, and the two numbers behind it. |
| 1453 | 3 | `String shorten(String name)` | Keeps building names inside the panel's fixed-width column. |


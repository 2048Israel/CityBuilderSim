# ConstructionScreen.java - 1,144 lines · 39 methods · 7 constants · interface

`ham/citybuildersim/ui/ConstructionScreen.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> The construction page (0.7.22): the builders' gauge, every site with its
> order, its crews, its time and its money and the player's hand on it -
> priority, rush, cancel, restart - a timeline of when each finishes, and
> the Demolish tab, where the city's buildings come down and a business's
> or a landlord's are bought out first.
> 
> WHY. Jerus, on the play-through of 0.7.19: "whats getting built and all,
> and what you already are building, and like not just a blue loading
> screen" - the right panel's list of progress bars was all a player had -
> and then "not only repirotize and cancel but also destroy buildings, like
> you yourself destroy buildings". Built to the mockups he saw
> (Construction.dc.html, Demolish.dc.html); every figure the mockups left
> in brackets is the model's, read through its public getters: the plan
> the month will apply (BuildingManager.plan()), each site's time at
> today's queue (siteMonths()), and the quotes (Game.quoteDemolition(),
> quoteBuyOut(), quoteRestart()). The rules and their sources are
> ConstructionControl's; the page says them in a line and an (i). Since
> 0.7.129, while the dead wait and the city's death care is served first
> (BuildingManager, DEATH CARE FIRST WHILE THE DEAD WAIT), the plan's own
> line says so under the caption, and the arrows move a site within its
> group - the death care at the top, the rest below - never across. Since
> 0.7.130, while a depot on site would be starved by the crews' rule the
> builders build it first, off the top of their month, and the death care
> beside it at the same priority
> (BuildingManager, THE BUILDERS' OWN DEPOTS FIRST): the plan's line says
> that instead, and a depot of the city's own heads its order with the
> death care.
> 
> Reached from the right panel's "Open" and from Build's head; a build
> card's "N on site", or a site's name on the right panel, opens it at that
> site. The cancel, the demolition and the buy-out ask first, with the
> money in the dialog (UserInterface.confirm()).

**Uses:** [Palette](Palette.md) (188), [Game](Game.md) (24), [ConstructionControl](ConstructionControl.md) (16), [BuildingManager](BuildingManager.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (6), [BuildingsStacks](BuildingsStacks.md) (4), [LandManager](LandManager.md) (4), [UserInterface](UserInterface.md) (3), [BuildersShort](BuildersShort.md) (3), [Sectors](Sectors.md) (2), [SafetyType](SafetyType.md) (2), [BuildScreen](BuildScreen.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [EducationType](EducationType.md) (1), [CareType](CareType.md) (1), [Sector](Sector.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 115 | THE HEAD: the title with the building area's swatch, the three tabs, |
| 144 | THE BUILDERS' GAUGE: what they do a month, everything owed on site, |
| 236 | THE SITES |
| 566 | THE TIMELINE: a bar a site, from now to when it finishes at today's |
| 695 | DEMOLISH: the city's buildings by type, the stopped shells, and the |
| 962 | THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). |
| 1064 | THE PIECES this page is drawn with. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 234 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 248 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 616 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 621 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |
| 967 | `ConstructionScreen.PAVING_WORDS` | `"%,d paving · no stop"` | The Paved Road site's words while it paves gravel roads (0.7.70), where Cancel would be: how many, and that it runs on. |
| 970 | `ConstructionScreen.PAVING_TIP` | `"Some of these Paved Roads pave gravel roads, which carry their traffic until...` | ...and why. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private final UserInterface ui` |  |
| 67 | `private String tab` | The open tab - Sites, Timeline or Demolish - kept for the session. |
| 70 | `private String focus` | The site the page was opened at from a build card or the right panel's site name, drawn raised until another tab is opened. |
| 73 | `private String pick` | The Demolish tab's staging: what is picked - a template's name, or a shell's - and how many. |
| 74 | `private boolean pickShell` |  |
| 75 | `private int count` |  |
| 76 | `private String filter` |  |
| 622 | `private final List<Double> months` |  |
| 623 | `private final double axis` |  |
| 624 | `private final List<Region> bars` |  |
| 625 | `private final List<Label> names` |  |
| 626 | `private final List<Region> ticks` |  |
| 627 | `private final List<Label> tickNames` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1088 | **type** `final class ConstructionScreen` | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - priority, rush, cancel, restart - a timeline of when each finishes, and t... |
| 64 | 1 | `ConstructionScreen(UserInterface ui)` |  |
| 79 | 1 | `void show()` | The page, on the tab it was left on. |
| 82 | 5 | `void showSite(String key)` | ...at one site, from a build card's "N on site" or a site's name on the right panel. |
| 88 | 5 | `void showTab(String which)` |  |
| 94 | 20 | `private void draw()` |  |

### THE HEAD: the title with the building area's swatch, the three tabs, (lines 115-143)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 23 | `private HBox head()` |  |

### THE BUILDERS' GAUGE: what they do a month, everything owed on site, (lines 144-235)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 75 | `private VBox gauge()` |  |

### THE SITES (lines 236-565)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 92 | `private VBox sites()` |  |
| 344 | 11 | `private HBox headerRow()` |  |
| 357 | 165 | `private Node siteRow(String key, int rank, int ranks, BuildingManager.Plan plan, double output, double atEveryPost)` | One site on site: a stack or a demolition. |
| 524 | 41 | `private Node shellRow(ConstructionControl.Shell shell)` | A stopped shell: what it holds, and Restart or Demolish. |

### THE TIMELINE: a bar a site, from now to when it finishes at today's (lines 566-694)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 42 | `private VBox timeline()` |  |
| 619 | 75 | **type** `private static final class Timeline extends Pane` | The bars, laid out to the width they are given. |
| 629 | 38 | `Timeline(List<String> siteNames, List<Double> months, List<String> colours, double axis)` _(in ConstructionScreen.Timeline)_ |  |
| 668 | 1 | `protected double computePrefWidth(double height)` _(in ConstructionScreen.Timeline)_ |  |
| 670 | 23 | `protected void layoutChildren()` _(in ConstructionScreen.Timeline)_ |  |

### DEMOLISH: the city's buildings by type, the stopped shells, and the (lines 695-961)

| line | len | member | says |
|---:|---:|---|---|
| 702 | 30 | `private HBox demolish()` |  |
| 734 | 28 | `private void fillPicker(VBox list)` | The picker's rows: the city's buildings, the shells, then the businesses' and the landlords'. |
| 763 | 5 | `private Label listHead(String text)` |  |
| 769 | 20 | `private Node pickRow(String name, int n, boolean shell)` |  |
| 790 | 124 | `private VBox stagingCard()` |  |
| 916 | 20 | `private HBox rulesLine()` | The rules behind the card, a line and an (i). |
| 937 | 7 | `private static String placesWord(BuildingsTemplate t)` |  |
| 946 | 15 | `private HBox effect(String label, String value, String note, String tone)` | One line of the staging card: what, its figure, and a note under it. |

### THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). (lines 962-1063)

| line | len | member | says |
|---:|---:|---|---|
| 973 | 13 | `private void confirmCancel(String key)` |  |
| 987 | 15 | `private void confirmDemolition(BuildingsTemplate t, int n, Game.DemolitionQuote q)` |  |
| 1003 | 19 | `private void confirmBuyOut(BuildingsTemplate t, int n, Game.BuyOutQuote q)` |  |
| 1023 | 18 | `private void confirmRestart(ConstructionControl.Shell shell)` |  |
| 1042 | 16 | `private void confirmShellDemolition(ConstructionControl.Shell shell)` |  |
| 1060 | 3 | `private void refused(String why)` | A hand the model refused, said where the player is looking. |

### THE PIECES this page is drawn with. (lines 1064-1144)

| line | len | member | says |
|---:|---:|---|---|
| 1069 | 6 | `static String payerLabel(Game g, String payer)` | Who placed an order, in the player's words: the city, the landlords, or the business by its name. |
| 1076 | 5 | `private static Label sectionCaption(String text)` |  |
| 1082 | 5 | `private static Label head(String text)` |  |
| 1088 | 6 | `private static Label caption(String text)` |  |
| 1095 | 5 | `private static Label figureLabel(String text, String tone)` |  |
| 1103 | 8 | `private static Button action(String text, String tone, Runnable act)` | The chip and its tint are Pieces.chip() and Pieces.tint() since 0.7.27. |
| 1112 | 7 | `private static Label arrow(String glyph, boolean live, Runnable act)` |  |
| 1120 | 5 | `private static void tip(Node node, String text)` |  |
| 1126 | 8 | `private static Region cell(Node content, double width)` |  |
| 1135 | 9 | `private static Region grow(Node content)` |  |


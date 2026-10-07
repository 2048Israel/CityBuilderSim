# ConstructionScreen.java - 1,064 lines · 39 methods · 5 constants · interface

`ham/citybuildersim/ui/ConstructionScreen.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> ConstructionControl's; the page says them in a line and an (i).
> 
> Reached from the right panel's "Open" and from Build's head; a build
> card's "N on site", or a site's name on the right panel, opens it at that
> site. The cancel, the demolition and the buy-out ask first, with the
> money in the dialog (UserInterface.confirm()).

**Uses:** [Palette](Palette.md) (178), [Game](Game.md) (24), [ConstructionControl](ConstructionControl.md) (14), [BuildingManager](BuildingManager.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (6), [BuildingsStacks](BuildingsStacks.md) (4), [LandManager](LandManager.md) (4), [UserInterface](UserInterface.md) (3), [Sectors](Sectors.md) (2), [SafetyType](SafetyType.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [EducationType](EducationType.md) (1), [CareType](CareType.md) (1), [Sector](Sector.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 105 | THE HEAD: the title with the building area's swatch, the three tabs, |
| 134 | THE BUILDERS' GAUGE: what they do a month, everything owed on site, |
| 204 | THE SITES |
| 497 | THE TIMELINE: a bar a site, from now to when it finishes at today's |
| 626 | DEMOLISH: the city's buildings by type, the stopped shells, and the |
| 889 | THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). |
| 984 | THE PIECES this page is drawn with. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 202 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 216 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 547 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 552 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `private final UserInterface ui` |  |
| 57 | `private String tab` | The open tab - Sites, Timeline or Demolish - kept for the session. |
| 60 | `private String focus` | The site the page was opened at from a build card or the right panel's site name, drawn raised until another tab is opened. |
| 63 | `private String pick` | The Demolish tab's staging: what is picked - a template's name, or a shell's - and how many. |
| 64 | `private boolean pickShell` |  |
| 65 | `private int count` |  |
| 66 | `private String filter` |  |
| 553 | `private final List<Double> months` |  |
| 554 | `private final double axis` |  |
| 555 | `private final List<Region> bars` |  |
| 556 | `private final List<Label> names` |  |
| 557 | `private final List<Region> ticks` |  |
| 558 | `private final List<Label> tickNames` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 1018 | **type** `final class ConstructionScreen` | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - priority, rush, cancel, restart - a timeline of when each finishes, and t... |
| 54 | 1 | `ConstructionScreen(UserInterface ui)` |  |
| 69 | 1 | `void show()` | The page, on the tab it was left on. |
| 72 | 5 | `void showSite(String key)` | ...at one site, from a build card's "N on site" or a site's name on the right panel. |
| 78 | 5 | `void showTab(String which)` |  |
| 84 | 20 | `private void draw()` |  |

### THE HEAD: the title with the building area's swatch, the three tabs, (lines 105-133)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 23 | `private HBox head()` |  |

### THE BUILDERS' GAUGE: what they do a month, everything owed on site, (lines 134-203)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 54 | `private VBox gauge()` |  |

### THE SITES (lines 204-496)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 66 | `private VBox sites()` |  |
| 286 | 11 | `private HBox headerRow()` |  |
| 299 | 154 | `private Node siteRow(String key, int rank, int ranks, BuildingManager.Plan plan, double output, double atEveryPost)` | One site on site: a stack or a demolition. |
| 455 | 41 | `private Node shellRow(ConstructionControl.Shell shell)` | A stopped shell: what it holds, and Restart or Demolish. |

### THE TIMELINE: a bar a site, from now to when it finishes at today's (lines 497-625)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 42 | `private VBox timeline()` |  |
| 550 | 75 | **type** `private static final class Timeline extends Pane` | The bars, laid out to the width they are given. |
| 560 | 38 | `Timeline(List<String> siteNames, List<Double> months, List<String> colours, double axis)` _(in ConstructionScreen.Timeline)_ |  |
| 599 | 1 | `protected double computePrefWidth(double height)` _(in ConstructionScreen.Timeline)_ |  |
| 601 | 23 | `protected void layoutChildren()` _(in ConstructionScreen.Timeline)_ |  |

### DEMOLISH: the city's buildings by type, the stopped shells, and the (lines 626-888)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 30 | `private HBox demolish()` |  |
| 665 | 28 | `private void fillPicker(VBox list)` | The picker's rows: the city's buildings, the shells, then the businesses' and the landlords'. |
| 694 | 5 | `private Label listHead(String text)` |  |
| 700 | 20 | `private Node pickRow(String name, int n, boolean shell)` |  |
| 721 | 122 | `private VBox stagingCard()` |  |
| 845 | 18 | `private HBox rulesLine()` | The rules behind the card, a line and an (i). |
| 864 | 7 | `private static String placesWord(BuildingsTemplate t)` |  |
| 873 | 15 | `private HBox effect(String label, String value, String note, String tone)` | One line of the staging card: what, its figure, and a note under it. |

### THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). (lines 889-983)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 13 | `private void confirmCancel(String key)` |  |
| 907 | 15 | `private void confirmDemolition(BuildingsTemplate t, int n, Game.DemolitionQuote q)` |  |
| 923 | 19 | `private void confirmBuyOut(BuildingsTemplate t, int n, Game.BuyOutQuote q)` |  |
| 943 | 18 | `private void confirmRestart(ConstructionControl.Shell shell)` |  |
| 962 | 16 | `private void confirmShellDemolition(ConstructionControl.Shell shell)` |  |
| 980 | 3 | `private void refused(String why)` | A hand the model refused, said where the player is looking. |

### THE PIECES this page is drawn with. (lines 984-1064)

| line | len | member | says |
|---:|---:|---|---|
| 989 | 6 | `static String payerLabel(Game g, String payer)` | Who placed an order, in the player's words: the city, the landlords, or the business by its name. |
| 996 | 5 | `private static Label sectionCaption(String text)` |  |
| 1002 | 5 | `private static Label head(String text)` |  |
| 1008 | 6 | `private static Label caption(String text)` |  |
| 1015 | 5 | `private static Label figureLabel(String text, String tone)` |  |
| 1023 | 8 | `private static Button action(String text, String tone, Runnable act)` | The chip and its tint are Pieces.chip() and Pieces.tint() since 0.7.27. |
| 1032 | 7 | `private static Label arrow(String glyph, boolean live, Runnable act)` |  |
| 1040 | 5 | `private static void tip(Node node, String text)` |  |
| 1046 | 8 | `private static Region cell(Node content, double width)` |  |
| 1055 | 9 | `private static Region grow(Node content)` |  |


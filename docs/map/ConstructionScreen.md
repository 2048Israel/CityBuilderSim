# ConstructionScreen.java - 1,319 lines · 45 methods · 7 constants · interface

`ham/citybuildersim/ui/ConstructionScreen.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (188), [Kept](Kept.md) (151), [Game](Game.md) (26), [ConstructionControl](ConstructionControl.md) (18), [BuildingManager](BuildingManager.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (7), [BuildingsStacks](BuildingsStacks.md) (4), [LandManager](LandManager.md) (4), [UserInterface](UserInterface.md) (3), [BuildersShort](BuildersShort.md) (3), [Sectors](Sectors.md) (2), [SafetyType](SafetyType.md) (2), [BuildScreen](BuildScreen.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [EducationType](EducationType.md) (1), [CareType](CareType.md) (1), [Sector](Sector.md) (1)

**Used by (2):** [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 137 | THE HEAD: the title with the building area's swatch, the three tabs, |
| 167 | THE BUILDERS' GAUGE: what they do a month, everything owed on site, |
| 263 | THE SITES |
| 645 | THE TIMELINE: a bar a site, from now to when it finishes at today's |
| 819 | DEMOLISH: the city's buildings by type, the stopped shells, and the |
| 1129 | THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). |
| 1231 | THE PIECES this page is drawn with. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 261 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 275 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 700 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 710 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |
| 1134 | `ConstructionScreen.PAVING_WORDS` | `"%,d paving · no stop"` | The Paved Road site's words while it paves gravel roads (0.7.70), where Cancel would be: how many, and that it runs on. |
| 1137 | `ConstructionScreen.PAVING_TIP` | `"Some of these Paved Roads pave gravel roads, which carry their traffic until...` | ...and why. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `private final UserInterface ui` |  |
| 65 | `String tab` | The open tab - Sites, Timeline or Demolish - kept for the session. |
| 68 | `String focus` | The site the page was opened at from a build card or the right panel's site name, drawn raised until another tab is opened. |
| 71 | `String pick` | The Demolish tab's staging: what is picked - a template's name, or a shell's - and how many. |
| 72 | `boolean pickShell` |  |
| 73 | `int count` |  |
| 74 | `String filter` |  |
| 93 | `final Kept.Pool pool` | The Construction tab's kept nodes (Kept; study UP4, KP3): the page is drawn over them on each redraw, each tab in a scope of its own. |
| 711 | `private List<Double> months` |  |
| 712 | `private double axis` |  |
| 713 | `private final List<Region> bars` |  |
| 714 | `private final List<Label> names` |  |
| 715 | `private final List<Region> ticks` |  |
| 716 | `private final List<Label> tickNames` |  |
| 718 | `private int tickCount` | How many ticks and how many sites the parts were made for at the last set(); -1 before the first. |
| 827 | `private VBox pickerList` | The picker's list of rows as the last draw made it, which the find box refills in place as it is typed in. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 1265 | **type** `final class ConstructionScreen` | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - priority, rush, cancel, restart - a timeline of when each finishes, and t... |
| 62 | 1 | `ConstructionScreen(UserInterface ui)` |  |
| 77 | 1 | `void show()` | The page, on the tab it was left on. |
| 80 | 5 | `void showSite(String key)` | ...at one site, from a build card's "N on site" or a site's name on the right panel. |
| 86 | 5 | `void showTab(String which)` |  |
| 96 | 4 | `void forget()` | A new city or a load: nothing of the last city's page is kept. |
| 101 | 5 | `private void draw()` |  |
| 112 | 24 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck, on the tab the screen is on (Sites, Timeline or Demolish). |

### THE HEAD: the title with the building area's swatch, the three tabs, (lines 137-166)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 24 | `private HBox head()` |  |

### THE BUILDERS' GAUGE: what they do a month, everything owed on site, (lines 167-262)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 79 | `private VBox gauge()` |  |

### THE SITES (lines 263-644)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 53 | `private VBox sites()` |  |
| 333 | 49 | `private HBox sitesTop(Game g, ConstructionControl control)` | The table's top: its caption, a line on the crews' order, the (i) and, once an order is set, the way back to the crews' rule. |
| 383 | 11 | `private HBox headerRow()` |  |
| 400 | 192 | `private Node siteRow(String key, int rank, int ranks, BuildingManager.Plan plan, double output, double atEveryPost)` | One site on site: a stack or a demolition. |
| 594 | 50 | `private Node shellRow(ConstructionControl.Shell shell)` | A stopped shell: what it holds, and Restart or Demolish. |

### THE TIMELINE: a bar a site, from now to when it finishes at today's (lines 645-818)

| line | len | member | says |
|---:|---:|---|---|
| 651 | 47 | `private VBox timeline()` |  |
| 708 | 110 | **type** `private static final class Timeline extends Pane` | The bars, laid out to the width they are given. |
| 720 | 1 | `Timeline()` _(in ConstructionScreen.Timeline)_ |  |
| 723 | 34 | `void set(List<String> siteNames, List<Double> months, List<String> colours, double axis)` _(in ConstructionScreen.Timeline)_ | The sites drawn now: their names, months to finish (NaN: stalled), bar colours, and the axis' length in months. |
| 759 | 32 | `private void build()` _(in ConstructionScreen.Timeline)_ | The parts for tickCount ticks and siteCount sites: their text and colours are set() at once after. |
| 792 | 1 | `protected double computePrefWidth(double height)` _(in ConstructionScreen.Timeline)_ |  |
| 794 | 23 | `protected void layoutChildren()` _(in ConstructionScreen.Timeline)_ |  |

### DEMOLISH: the city's buildings by type, the stopped shells, and the (lines 819-1128)

| line | len | member | says |
|---:|---:|---|---|
| 829 | 40 | `private HBox demolish()` |  |
| 871 | 29 | `private void fillPicker(VBox list)` | The picker's rows: the city's buildings, the shells, then the businesses' and the landlords'. |
| 901 | 6 | `private Label listHead(String text)` |  |
| 908 | 24 | `private Node pickRow(String name, int n, boolean shell)` |  |
| 933 | 22 | `private VBox stagingCard()` |  |
| 957 | 121 | `private List<Node> pickedParts(BuildingsTemplate t, ConstructionControl.Shell shell)` | The staging card's parts for a pick: its head with the count, what it would do, the buttons, the rules. |
| 1080 | 20 | `private HBox rulesLine()` | The rules behind the card, a line and an (i). |
| 1101 | 7 | `private static String placesWord(BuildingsTemplate t)` |  |
| 1110 | 18 | `private HBox effect(String label, String value, String note, String tone)` | One line of the staging card: what, its figure, and a note under it. |

### THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). (lines 1129-1230)

| line | len | member | says |
|---:|---:|---|---|
| 1140 | 13 | `private void confirmCancel(String key)` |  |
| 1154 | 15 | `private void confirmDemolition(BuildingsTemplate t, int n, Game.DemolitionQuote q)` |  |
| 1170 | 19 | `private void confirmBuyOut(BuildingsTemplate t, int n, Game.BuyOutQuote q)` |  |
| 1190 | 18 | `private void confirmRestart(ConstructionControl.Shell shell)` |  |
| 1209 | 16 | `private void confirmShellDemolition(ConstructionControl.Shell shell)` |  |
| 1227 | 3 | `private void refused(String why)` | A hand the model refused, said where the player is looking. |

### THE PIECES this page is drawn with. (lines 1231-1319)

| line | len | member | says |
|---:|---:|---|---|
| 1236 | 6 | `static String payerLabel(Game g, String payer)` | Who placed an order, in the player's words: the city, the landlords, or the business by its name. |
| 1246 | 6 | `private static Label sectionCaption(String text)` | Kept (KP3): every node of the page comes through Kept (new outside a draw), and each helper sets what it sets on both sides of its conditions, so a label or a button handed back from another place is made what a new o... |
| 1253 | 6 | `private static Label head(String text)` |  |
| 1260 | 6 | `private static Label caption(String text)` |  |
| 1267 | 6 | `private static Label figureLabel(String text, String tone)` |  |
| 1276 | 9 | `private static Button action(String text, String tone, Runnable act)` | The chip and its tint are Pieces.chip() and Pieces.tint() since 0.7.27. |
| 1286 | 8 | `private static Label arrow(String glyph, boolean live, Runnable act)` |  |
| 1295 | 3 | `private static void tip(Node node, String text)` |  |
| 1299 | 10 | `private static Region cell(Node content, double width)` |  |
| 1310 | 9 | `private static Region grow(Node content)` |  |


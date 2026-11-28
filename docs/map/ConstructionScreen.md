# ConstructionScreen.java - 1,426 lines · 51 methods · 7 constants · interface

`ham/citybuildersim/ui/ConstructionScreen.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

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
> the month will apply (BuildingManager.plan()), each site's months left at
> its pace (Game.sitePaceMonths(), ASH1), and the quotes (Game.quoteDemolition(),
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

**Uses:** [Palette](Palette.md) (194), [Kept](Kept.md) (166), [Game](Game.md) (31), [ConstructionControl](ConstructionControl.md) (19), [BuildingManager](BuildingManager.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (7), [BuildingsStacks](BuildingsStacks.md) (4), [LandManager](LandManager.md) (4), [UserInterface](UserInterface.md) (3), [BuildersShort](BuildersShort.md) (2), [Sectors](Sectors.md) (2), [SafetyType](SafetyType.md) (2), [BuildScreen](BuildScreen.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [EducationType](EducationType.md) (1), [CareType](CareType.md) (1), [Sector](Sector.md) (1)

**Used by (2):** [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 138 | THE HEAD: the title with the building area's swatch, the three tabs, |
| 168 | THE BUILDERS' GAUGE: what they do a month, everything owed on site, |
| 286 | THE SITES |
| 694 | THE TIMELINE: a bar a site, from now to when it finishes at today's |
| 869 | DEMOLISH: the city's buildings by type, the stopped shells, and the |
| 1179 | THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). |
| 1338 | THE PIECES this page is drawn with. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `ConstructionScreen.SCREEN` | `"showConstruction"` | The screen's name, for clearMenu() and the rail (the Build tab owns it). |
| 284 | `ConstructionScreen.BAR_BAND` | `10` | How tall the page's bars are: the gauge's queue and each site's and stopped shell's progress. |
| 298 | `ConstructionScreen.COL_RANK` | `34, COL_PROGRESS = 140, COL_CREWS = 100, COL_MONEY = 104, COL_STATUS = 168` | The columns, by width: the order, the building, its progress, crews and time, money, status and the hand. |
| 750 | `ConstructionScreen.TIMELINE_MAX` | `600` | The longest the timeline's axis runs, in months: fifty years; a site later than that runs off its end. |
| 760 | `ConstructionScreen.Timeline.NAME` | `210, ROW = 26, TOP = 18` | The names' column, a row, and the band the years are labelled in above the bars, in pixels. |
| 1184 | `ConstructionScreen.PAVING_WORDS` | `"%,d paving · no stop"` | The Paved Road site's words while it paves gravel roads (0.7.70), where Cancel would be: how many, and that it runs on. |
| 1187 | `ConstructionScreen.PAVING_TIP` | `"Some of these Paved Roads pave gravel roads, which carry their traffic until...` | ...and why. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final UserInterface ui` |  |
| 66 | `String tab` | The open tab - Sites, Timeline or Demolish - kept for the session. |
| 69 | `String focus` | The site the page was opened at from a build card or the right panel's site name, drawn raised until another tab is opened. |
| 72 | `String pick` | The Demolish tab's staging: what is picked - a template's name, or a shell's - and how many. |
| 73 | `boolean pickShell` |  |
| 74 | `long count` |  |
| 75 | `String filter` |  |
| 94 | `final Kept.Pool pool` | The Construction tab's kept nodes (Kept; study UP4, KP3): the page is drawn over them on each redraw, each tab in a scope of its own. |
| 761 | `private List<Double> months` |  |
| 762 | `private double axis` |  |
| 763 | `private final List<Region> bars` |  |
| 764 | `private final List<Label> names` |  |
| 765 | `private final List<Region> ticks` |  |
| 766 | `private final List<Label> tickNames` |  |
| 768 | `private int tickCount` | How many ticks and how many sites the parts were made for at the last set(); -1 before the first. |
| 877 | `private VBox pickerList` | The picker's list of rows as the last draw made it, which the find box refills in place as it is typed in. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 1371 | **type** `final class ConstructionScreen` | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - priority, rush, cancel, restart - a timeline of when each finishes, and t... |
| 63 | 1 | `ConstructionScreen(UserInterface ui)` |  |
| 78 | 1 | `void show()` | The page, on the tab it was left on. |
| 81 | 5 | `void showSite(String key)` | ...at one site, from a build card's "N on site" or a site's name on the right panel. |
| 87 | 5 | `void showTab(String which)` |  |
| 97 | 4 | `void forget()` | A new city or a load: nothing of the last city's page is kept. |
| 102 | 5 | `private void draw()` |  |
| 113 | 24 | `void drawPage()` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck, on the tab the screen is on (Sites, Timeline or Demolish). |

### THE HEAD: the title with the building area's swatch, the three tabs, (lines 138-167)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 24 | `private HBox head()` |  |

### THE BUILDERS' GAUGE: what they do a month, everything owed on site, (lines 168-285)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 101 | `private VBox gauge()` |  |

### THE SITES (lines 286-693)

| line | len | member | says |
|---:|---:|---|---|
| 301 | 55 | `private VBox sites()` |  |
| 358 | 49 | `private HBox sitesTop(Game g, ConstructionControl control)` | The table's top: its caption, a line on the crews' order, the (i) and, once an order is set, the way back to the crews' rule. |
| 408 | 11 | `private HBox headerRow()` |  |
| 425 | 216 | `private Node siteRow(String key, int rank, int ranks, BuildingManager.Plan plan, BuildingManager.Plan pace, double output, doub...` | One site on site: a stack or a demolition. |
| 643 | 50 | `private Node shellRow(ConstructionControl.Shell shell)` | A stopped shell: what it holds, and Restart or Demolish. |

### THE TIMELINE: a bar a site, from now to when it finishes at today's (lines 694-868)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 48 | `private VBox timeline()` |  |
| 758 | 110 | **type** `private static final class Timeline extends Pane` | The bars, laid out to the width they are given. |
| 770 | 1 | `Timeline()` _(in ConstructionScreen.Timeline)_ |  |
| 773 | 34 | `void set(List<String> siteNames, List<Double> months, List<String> colours, double axis)` _(in ConstructionScreen.Timeline)_ | The sites drawn now: their names, months to finish (NaN: stalled), bar colours, and the axis' length in months. |
| 809 | 32 | `private void build()` _(in ConstructionScreen.Timeline)_ | The parts for tickCount ticks and siteCount sites: their text and colours are set() at once after. |
| 842 | 1 | `protected double computePrefWidth(double height)` _(in ConstructionScreen.Timeline)_ |  |
| 844 | 23 | `protected void layoutChildren()` _(in ConstructionScreen.Timeline)_ |  |

### DEMOLISH: the city's buildings by type, the stopped shells, and the (lines 869-1178)

| line | len | member | says |
|---:|---:|---|---|
| 879 | 40 | `private HBox demolish()` |  |
| 921 | 29 | `private void fillPicker(VBox list)` | The picker's rows: the city's buildings, the shells, then the businesses' and the landlords'. |
| 951 | 6 | `private Label listHead(String text)` |  |
| 958 | 24 | `private Node pickRow(String name, long n, boolean shell)` |  |
| 983 | 22 | `private VBox stagingCard()` |  |
| 1007 | 121 | `private List<Node> pickedParts(BuildingsTemplate t, ConstructionControl.Shell shell)` | The staging card's parts for a pick: its head with the count, what it would do, the buttons, the rules. |
| 1130 | 20 | `private HBox rulesLine()` | The rules behind the card, a line and an (i). |
| 1151 | 7 | `private static String placesWord(BuildingsTemplate t)` |  |
| 1160 | 18 | `private HBox effect(String label, String value, String note, String tone)` | One line of the staging card: what, its figure, and a note under it. |

### THE CONFIRMATIONS, with the money in them (UserInterface.confirm()). (lines 1179-1337)

| line | len | member | says |
|---:|---:|---|---|
| 1190 | 13 | `private void confirmCancel(String key)` |  |
| 1208 | 21 | `private void confirmAbroad(String key)` | The city hands one of its sites to the foreign crews (FC2b): the card as the model strikes it now, said before it is paid. |
| 1231 | 5 | `private static String abroadTip(Game.AbroadCard card)` | What the Foreign crews button says it does (FC2b), from the card's own getters. |
| 1238 | 5 | `private static String againstHome(Game.AbroadCard card)` | The months abroad against the months at home, and the months saved (FC2b): "3 months against 10 months at home, 7 months sooner", or where home never finishes, that. |
| 1245 | 5 | `private static String wholeMonths(double months)` | The months the foreign crews' figures are worded in (FC2b): "1 month", "3 months", "never" for endless. |
| 1252 | 3 | `private static String soonerWords(double monthsSaved)` | The months a handover saves, as the card's caption says them: "3 months sooner", or "never done at home" where the wait at home is endless. |
| 1257 | 3 | `private static String plusMoney(double thousands)` | A sum the city pays over, with its plus (thousands, as money() takes them): "+$1.2M", and "-$40K" when it is a saving. |
| 1261 | 15 | `private void confirmDemolition(BuildingsTemplate t, long n, Game.DemolitionQuote q)` |  |
| 1277 | 19 | `private void confirmBuyOut(BuildingsTemplate t, long n, Game.BuyOutQuote q)` |  |
| 1297 | 18 | `private void confirmRestart(ConstructionControl.Shell shell)` |  |
| 1316 | 16 | `private void confirmShellDemolition(ConstructionControl.Shell shell)` |  |
| 1334 | 3 | `private void refused(String why)` | A hand the model refused, said where the player is looking. |

### THE PIECES this page is drawn with. (lines 1338-1426)

| line | len | member | says |
|---:|---:|---|---|
| 1343 | 6 | `static String payerLabel(Game g, String payer)` | Who placed an order, in the player's words: the city, the landlords, or the business by its name. |
| 1353 | 6 | `private static Label sectionCaption(String text)` | Kept (KP3): every node of the page comes through Kept (new outside a draw), and each helper sets what it sets on both sides of its conditions, so a label or a button handed back from another place is made what a new o... |
| 1360 | 6 | `private static Label head(String text)` |  |
| 1367 | 6 | `private static Label caption(String text)` |  |
| 1374 | 6 | `private static Label figureLabel(String text, String tone)` |  |
| 1383 | 9 | `private static Button action(String text, String tone, Runnable act)` | The chip and its tint are Pieces.chip() and Pieces.tint() since 0.7.27. |
| 1393 | 8 | `private static Label arrow(String glyph, boolean live, Runnable act)` |  |
| 1402 | 3 | `private static void tip(Node node, String text)` |  |
| 1406 | 10 | `private static Region cell(Node content, double width)` |  |
| 1417 | 9 | `private static Region grow(Node content)` |  |


# ServicesScreen.java - 3,452 lines · 134 methods · 25 constants · interface

`ham/citybuildersim/ui/ServicesScreen.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> The services tab: the four systems the city runs for its people - health,
> education, utilities and safety - each opening on an Overview whose one
> picture answers the system's question, then the pages behind it and the
> books it keeps, under the four figures for whichever is open.
> 
> WHY. Split out of UserInterface on 2026-09-18, the banners from SERVICES
> to THE BOOKS exactly as they were; redrawn in 0.7.28 in Build's style
> (the project's spec-services-0728.md). Each page was a 560 px statement
> column of label-and-figure rows with a grey paragraph under most of them,
> and more than half the centre stood empty. The pictures lead now - the
> sick rate as its causes over the care the city runs, the schools as a
> pipeline, the utilities as capacity rows, the crime as its causes - the
> tables sit behind "details" and the paragraphs behind an (i). Services
> shows what the city's buildings DO and why; what is on site and what to
> order is Build's, one door away both ways. The Infrastructure tab shared
> this file until 0.7.28 (InfrastructureScreen). The shell reads which
> system and page are open (serviceArea, servicePage) for the rail and the
> scroll memory.

**Uses:** [Palette](Palette.md) (437), [Kept](Kept.md) (217), [CityNeeds](CityNeeds.md) (136), [CareType](CareType.md) (88), [EducationType](EducationType.md) (51), [BuildAdvice](BuildAdvice.md) (46), [Healthcare](Healthcare.md) (40), [BuildScreen](BuildScreen.md) (32), [Icons](Icons.md) (28), [Crime](Crime.md) (26), [AgeBand](AgeBand.md) (25), [Sickness](Sickness.md) (22), [Education](Education.md) (21), [BuildingManager](BuildingManager.md) (19), [Health](Health.md) (17), [SafetyType](SafetyType.md) (17), [CausesOfDeath](CausesOfDeath.md) (14), [Money](Money.md) (11), [Migration](Migration.md) (7), [UtilitiesHandler](UtilitiesHandler.md) (6), [LabourMarket](LabourMarket.md) (5), [Pieces](Pieces.md) (4), [PopulationManager](PopulationManager.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [UserInterface](UserInterface.md) (2), [BuildingType](BuildingType.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [SummaryScreen](SummaryScreen.md) (1), [HistorySave](HistorySave.md) (1)... and 6 more

**Used by (5):** [BuildScreen](BuildScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 44 | SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. |
| 124 | THE FRAME (0.7.28) |
| 385 | THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. |
| 498 | EVENTS (0.7.28) |
| 583 | · the screen's own pieces |
| 756 | HEALTH |
| 868 | · the Overview |
| 1026 | · what people die of (R0b) |
| 1149 | · the care cards |
| 1314 | · general care |
| 1494 | · childcare and senior care |
| 1581 | · death care |
| 1713 | EDUCATION |
| 1808 | · the pipeline |
| 2023 | · the basic ladder |
| 2112 | · a course |
| 2424 | UTILITIES |
| 2692 | SAFETY (2026-09-11) |
| 3005 | THE BOOKS. |
| 3030 | · HEALTH |
| 3118 | · · what it costs |
| 3130 | · · where the money goes |
| 3182 | · EDUCATION |
| 3254 | · · what it costs |
| 3263 | · · where the money goes |
| 3293 | · UTILITIES |
| 3403 | · SAFETY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `ServicesScreen.OVERVIEW` | `"Overview", BOOKS = "Books"` | The page every system opens on (0.7.28), and the last chip of each, its books. |
| 105 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 108 | `ServicesScreen.SERVICE_HOME` | `OVERVIEW` | ...and every system opens on its Overview (0.7.28; General care until then). |
| 134 | `ServicesScreen.FRAME_CHROME` | `268` | How much of the stage the fixed frame takes above the page's scroller: the head, the systems, the four figures with their sparklines and changes, the pages, and their gaps. |
| 237 | `ServicesScreen.BOUND_TO_SCROLLER` | `"services.scroller"` | The key in the page's properties for the scroller its width is bound to (drawPage()). |
| 894 | `ServicesScreen.SICK_INFO` | `String.format("The bar is today's sick rate split into what makes it, in poin...` | The sick rate's (i): what the bar is, its floor and its cap, and why it is a cost of output and not of wages. |
| 1033 | `ServicesScreen.CAUSES_OF_DEATH_INFO` | `"What people died of in 1900, and how many still do.Each cause " + "follows C...` | What people die of's (i): the seven causes of 1900, that each follows Canada's own history, that a city short of water, food or care dies of them sooner, and that the children's share sits inside childcare's deaths. |
| 1274 | `ServicesScreen.BURIAL_INFO` | `"A household that can save a plot's price over ten years chooses burial; " + ...` | The burial choice (the death care page's note). |
| 1384 | `ServicesScreen.LONG_SICK_INFO` | `String.format("Everybody sick this month, by how long they have been ill: the...` | The long sick's (i): who can die of it, at what chance by age (the elders' too, which the old note left out). |
| 1393 | `ServicesScreen.RECOVERY_INFO` | `String.format("The share of the sick who get better in a month: %.0f%% with n...` | What care cures, in words. |
| 1642 | `ServicesScreen.PLOTS_INFO` | `"Plots are consumed permanently — the land never comes back, and a cemetery "...` | Plots are permanent (the ground's note). |
| 1646 | `ServicesScreen.CREMATORIA_INFO` | `"A rate rather than a stock, and it needs almost no land — which makes it " +...` | The crematoria's note. |
| 2002 | `ServicesScreen.DIPLOMA_INFO` | `"Teens age out at a steady rate and the ones who were in school leave with " ...` | The diplomas' (i): the teens' note, and what the figure is (and is not). |
| 2010 | `ServicesScreen.LADDER_INFO` | `String.format("The ladder serves the minimum of its three stages, not the ave...` | The basic ladder's notes: the minimum of three, and the four-and-three split. |
| 2018 | `ServicesScreen.PROFESSIONS_INFO` | `"A band row on the People screen can say the city has eight hundred " + "grad...` | The professions page's sentence. |
| 2247 | `ServicesScreen.GATES_INFO` | `String.format("The funnel is why that many and not more.Who could enrol holds...` | The gates' (i): why that many and not more, as the old page said it under its five lines. |
| 2259 | `ServicesScreen.COULD_ENROL` | `"Who holds the level this course takes, in the workforce - and for a " + "pro...` | Who could enrol, in words (the old page's note). |
| 2263 | `ServicesScreen.RETURN_INFO_PREFIX` | `"How much better off somebody is for doing it - 0 means not worth it.\n\n"` | The wage return's (i) opens on this, then says what the return is measured against (returnNote()). |
| 2636 | `ServicesScreen.POWER_INFO` | `"Power is counted in kilowatts, a rate - the screens wrote watts until " + "0...` | The power row's (i): the unit, the staff discount, one workforce, who is billed. |
| 2646 | `ServicesScreen.BROWNOUT_INFO` | `"Every industrial and commercial building's output is cut in proportion " + "...` | A brownout's (i), as the grid's old alert said it. |
| 2651 | `ServicesScreen.WATER_INFO` | `"Water is counted in units of 10,000 gallons a month.The people draw " + "the...` | The water row's (i). |
| 2852 | `ServicesScreen.CAUSES_INFO` | `"Every adult at liberty is counted once, at the heaviest reason they have.The...` | The causes' (i). |
| 2858 | `ServicesScreen.STOLEN_INFO` | `"What is stolen goes to the offenders' households.The killings are next" + " ...` | What is stolen, and the injured (the old "what it did" note). |
| 2862 | `ServicesScreen.CAUGHT_INFO` | `"Anybody caught with no staffed cell free stays on the street and keeps" + " ...` | Anybody caught with no cell (the prisons' note). |
| 2867 | `ServicesScreen.OFFICERS_INFO` | `crime -> String.format("%s officers per 100,000 people.Canada has %s; full co...` | The officers against Canada, and the founding constabulary. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 106 | `String serviceArea` |  |
| 109 | `String servicePage` |  |
| 119 | `final java.util.Set<String> detailsOpen` | Which "details" folds are open, by name - remembered while the game runs, not saved. |
| 122 | `private final java.util.Set<String> seen` | The events whose system has been opened since they happened, so their "!" goes (by kind and month). |
| 141 | `final Kept.Pool pool` | The services tab's kept nodes (Kept; study UP4, KP2): the page is drawn over last month's on each redraw, each system's sub-page a scope of its own. |
| 175 | `private boolean atTop` | Whether the page just drawn was picked by the player and opens at its top; and the scroller drawPage() last made. |
| 176 | `private javafx.scene.control.ScrollPane scroller` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 3416 | **type** `final class ServicesScreen` | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one picture answers the system's question, then the pages behind it and the... |
| 42 | 1 | `ServicesScreen(UserInterface ui)` |  |

### SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. (lines 44-123)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 1 | **type** `record ServiceArea(String name, String[] pages)` | A system, and the parts of it the second strip offers. |
| 89 | 14 | `static ServiceArea[] serviceAreas()` |  |
| 111 | 6 | `ServiceArea currentArea()` |  |

### THE FRAME (0.7.28) (lines 124-384)

| line | len | member | says |
|---:|---:|---|---|
| 144 | 3 | `void forget()` | A new city or a load: nothing of the last city's page is kept. |
| 148 | 25 | `void showServicesStatsMenu()` |  |
| 184 | 51 | `void drawPage(List<CityNeeds.Need> all, List<Event> events)` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck. |
| 249 | 5 | `void openPage()` | Draw a page the player just picked, at the top of itself. |
| 256 | 7 | `void open(String area, String page)` | One system on one of its pages, at the top of it (0.7.28): every door into Services - the left panel's OFF SICK, Build's "why ›", a card, a part of a bar. |
| 269 | 19 | `static String[] pageFor(BuildAdvice.Measure m)` | The page that explains a Build ring (0.7.28), as {system, page}: Build's "why ›". |
| 289 | 6 | `static String[] areaNames()` |  |
| 296 | 6 | `static ServiceArea currentAreaFor(String name)` |  |
| 304 | 4 | `HBox head()` | The head: "Services › Health" with the people teal's swatch - the title a way back to the system's Overview - and Build's door at the right (a pill since 0.7.34). |
| 310 | 8 | `static String areaIcon(String area)` | A system's icon: the Build category's it is built in, Utilities the power's. |
| 320 | 8 | `static boolean answers(String area, CityNeeds.Go go)` | The NEEDS YOU rows a system answers: Health's healthcare, Education's, Utilities' power, water and the road, Safety's. |
| 330 | 8 | `static CityNeeds.Need areaWorst(String area, List<CityNeeds.Need> all)` | A system's verdict: the worst of the rows it answers - by each row's colour since 0.7.41 (Need.verdictLevel(): a served row short of 100% is amber though not listed) - and that row (null when none is near its line). |
| 345 | 39 | `javafx.scene.layout.FlowPane areaStrip(List<CityNeeds.Need> all, List<Event> events)` | The systems, as chips (0.7.28): each its icon, its name and a dot in the colour of the worst NEEDS YOU row it answers, and a "!" while something happened there this month that the player has not opened. |

### THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. (lines 385-497)

| line | len | member | says |
|---:|---:|---|---|
| 404 | 2 | **type** `record Kpi(String label, String value, String note, String tone, String where, Runnable go, String series, ...` | One of the four: its label, figure, note and colour (a verdict only where a NEEDS YOU row judges it), its door - where, and what it opens - the history series its sparkline draws (null: none kept) and how its change o... |
| 407 | 10 | `HBox areaVitals(List<CityNeeds.Need> all)` |  |
| 418 | 8 | `List<Kpi> kpis(String area, List<CityNeeds.Need> all)` |  |
| 428 | 23 | `VBox kpiCell(Kpi k)` | A limit cell with its sparkline at the right of its figure and its change under the note. |
| 453 | 10 | `double[] lastTwo(String series)` | The last two months a series recorded, oldest first; NaN where it has fewer. |
| 465 | 9 | `String change(String series, java.util.function.DoubleFunction<String> written)` | A figure's change on last month, from its history: "▲ 0.1 pts on last month", "no change on last month"; null with under two months kept. |
| 476 | 4 | `static String points(double rate)` | A share of the workforce or a rate, in points: "13.8 pts", "0.03 pts" under a tenth. |
| 482 | 1 | `static String pct(double share)` | A share, as Build's rings write it. |
| 485 | 1 | `static String tone(CityNeeds.Need n)` | A row's verdict colour - a served row's the one verdict on what it serves (0.7.41), any other's its level - or the plain figure's where no row judges it. |
| 488 | 9 | `static CityNeeds.Need need(List<CityNeeds.Need> all, CityNeeds.Kind kind, CareType care, EducationType school)` | The NEEDS YOU row of a kind - for CARE, of a care type; for HIGHER_SCHOOL, of a school - or null. |

### EVENTS (0.7.28) (lines 498-582)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 1 | **type** `record Event(String area, String key, String words, String whole, String tone)` | One event: its system, a key unique to it and its month, the line, its (i) (null: none) and its colour - a verdict, the event is news. |
| 514 | 37 | `List<Event> events()` |  |
| 553 | 14 | `HBox eventLine(String words, String whole, String tone)` | An event, or a state worth a line: a tinted line with its icon, its words wrapping, and its (i). |
| 569 | 3 | `void eventLines(VBox page, String area, List<Event> events)` | This system's events as lines. |
| 577 | 5 | `HBox eventScope(Event e)` | One event's line, in a scope keyed by its kind (its key less the month it began in, "outbreak:412" being "outbreak"), so a line that comes and goes does not move the others' nodes. |

### the screen's own pieces (lines 583-755)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 595 | 7 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 604 | 8 | `static VBox card(Node...children)` | A card on the Build ground: raised, rounded, a hairline edge. |
| 614 | 11 | `static VBox doorCard(VBox c, Runnable go, String where)` | ...made a door: a hand, an accent edge under the pointer, a click anywhere opens `go`; its tooltip says where. |
| 627 | 12 | `static HBox cardHead(String svg, String name, String info, Node right)` | A card's head: its icon in a teal square, its name, and its (i) at the right. |
| 650 | 18 | `VBox details(String name, String caption, java.util.function.Supplier<Node[]> inside)` | A fold: "details ▸ caption", and what is inside when it is open - remembered by name while the game runs (the land office's and the People page's way). |
| 670 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 678 | 11 | `HBox priceLine(String text, String whole, String door, String policyPage)` | "fee $10 a visit · Set the fee ›": a price, and the door to the dial on the Policy tab. |
| 696 | 18 | `VBox moneyBars(String topName, List<Slice> top, String bottomName, List<Slice> bottom, Node right)` | Two stacked bars on one money scale, each with its total at the right, a key under them, and the bottom line beside them (0.7.28): the books' cost against what came back, a utility's sales against its wages. |
| 716 | 17 | `static HBox moneyRow(String name, List<Slice> parts, double total, double scale)` | One of moneyBars()'s rows: its name, the bar of its parts, its total. |
| 735 | 7 | `static VBox bottomLine(String value, String name, String line, String tone)` | The bottom line beside a books card: a figure, its name and a line under. |
| 744 | 3 | `HBox buildFor(BuildAdvice.Measure m)` | "Build for it ›": Build's category opened on this ring, in the building pink - a pill since 0.7.34 (Pieces.doorPill()). |
| 749 | 6 | `static double sum(double[] values)` | A double[] in one figure - the education arrays are per-type. |

### HEALTH (lines 756-867)

| line | len | member | says |
|---:|---:|---|---|
| 770 | 45 | `List<Kpi> healthKpis(List<CityNeeds.Need> all)` | The four figures: the sick rate, the thinnest care, the ground (or the unburied), the bill. |
| 817 | 9 | `CareType thinnest()` | The living care with the least coverage the month applied: general care first of equals. |
| 834 | 11 | `String groundWords()` | The ground in a few words. |
| 847 | 9 | `String groundNote()` | ...and the line under it. |
| 857 | 10 | `void healthPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |

### the Overview (lines 868-1025)

| line | len | member | says |
|---:|---:|---|---|
| 870 | 22 | `void healthOverview(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 914 | 29 | `List<Part> sickParts()` | The sick rate's parts, in points, in the order the bar draws them - the floor, what the missing doctors add, hunger, no home, the unburied, an outbreak, violence, the 1918 influenza in its months (R0b) - and anything ... |
| 945 | 4 | `static double named(Health h)` | Health's named parts, added: what the old breakdown summed. |
| 951 | 5 | `double sickTrimmed()` | What the cap took off the parts, as a positive rate; 0 when it took nothing. |
| 958 | 29 | `HBox sickCard()` | The sick rate's card: the rate itself, big, in OFF SICK's colour, and the bar of its causes. |
| 989 | 36 | `VBox sickStatement()` | The old breakdown, every line as it was: the fold under the bar. |

### what people die of (R0b) (lines 1026-1148)

| line | len | member | says |
|---:|---:|---|---|
| 1040 | 11 | `static String lowers(CausesOfDeath.Cause cause)` | What takes a cause away, in a few words: the catalogue's finds and the care that blunts it. |
| 1053 | 6 | `void causesOfDeathSection(VBox page)` | The section: its heading, and the table of the seven causes in a card. |
| 1066 | 31 | `javafx.scene.layout.GridPane causesOfDeathTable()` | One row a cause: its name over what lowers it, the dead of it in the last twelve months, that as a rate a year (per 100,000 people; mothers per 1,000 births), and what it was in 1900. |
| 1099 | 10 | `VBox causesOfDeathNotes()` | Under the table: the TB sick, and, when nobody died of any of them, that the diseases of 1900 are gone. |
| 1116 | 9 | `boolean diseasesGone()` | Whether the diseases of 1900 are gone: nobody died of any in the last twelve months, and history has taken each of them to nought (it does by 1980; the influenza is a set of months, not a path). |
| 1127 | 5 | `double yearBirths()` | The births of the last twelve months, from the history's series; 0 when none are kept. |
| 1134 | 3 | `Node careScarceNote()` | At the right of the care's heading: which of the city's healthcare buildings' staff it fills worst (Build's note, on what stands). |
| 1139 | 9 | `String careScarceWords()` | ...its words. |

### the care cards (lines 1149-1313)

| line | len | member | says |
|---:|---:|---|---|
| 1158 | 3 | **type** `record CareWords(String name, String icon, String ring, double arc, String tone, String buys, String buysWo...` | What one care card says, worked out without drawing it (a probe reads it): its ring - the coverage the month applied, Healthcare.getCoverage(), in its NEEDS YOU row's colour - the one thing it buys and that figure's r... |
| 1162 | 45 | `CareWords careWords(CareType care, List<CityNeeds.Need> all)` |  |
| 1209 | 5 | `static String times(double factor)` | A factor, as the care pages write one: "×36.5", "×0.025". |
| 1216 | 8 | `String careInfo(CareType care, double cover)` | Who a kind of care serves, and what its ring is (the old page's three notes, and the month's coverage). |
| 1230 | 8 | `String seniorNeedWords()` | SENIOR CARE'S DENOMINATOR IS PLACES, not people (0.7.28; the page read "People to serve" over a figure of places): a senior needs a fifth of a place, an elder a whole one (CareType.placesPerHead()). |
| 1245 | 9 | `String childNeedWords()` | CHILDCARE'S DENOMINATOR IS PLACES TOO (0.7.139, batch CC1): a baby wants about a third of a place - the share of under-6s in a centre - and a child a little under a fifth, the share of 6-to-12s in a before- or after-s... |
| 1256 | 16 | `CareWords deathWords(List<CityNeeds.Need> all)` | Death care's card: the dead dealt with as its ring, the ground left as what it buys, the plots as its bar. |
| 1280 | 24 | `VBox careCard(CareWords w)` | One care card: head, ring and what it buys, its places as a supply bar, and its two doors. |
| 1306 | 7 | `static javafx.scene.text.Text textRun(String text, double size, String tone, boolean mono)` | A run of text in a flow: a figure in mono, or words. |

### general care (lines 1314-1493)

| line | len | member | says |
|---:|---:|---|---|
| 1323 | 52 | `void generalCarePage(VBox page, List<CityNeeds.Need> all, List<Event> events)` | General care: how many of the sick does care cure, and how many stay ill long enough to die? |
| 1377 | 5 | `double[] longSick()` | The long sick, a bar a slot: everybody ill under a month to a year or more, in people (Sickness.peopleInSlot()). |
| 1400 | 14 | `javafx.scene.layout.GridPane deathChances()` | The death chances by age, with the share of each band past two months and its dead last month. |
| 1423 | 23 | `VBox careFunnel(CareType care)` | Who a kind of care reaches, as a funnel: the people (or, for senior care, the places) it is measured against, the places built, the places staffed, and those it treated - with the people the fee turned away as a step ... |
| 1448 | 21 | `List<FunnelStep> careSteps(CareType care)` | ...its steps, worked out without drawing them. |
| 1476 | 6 | `boolean healthNotRunYet()` | True when a save from before 0.7.46 has been loaded and no month has run since: care that stands treated nobody, by the count (Healthcare's served[] was not saved until A3), while the fees it raised were. |
| 1484 | 9 | `HBox feeLine(CareType care, String per)` | "fee $10 a visit · Set the fee ›": the price of a kind of care, and the dial (Policy › Promises › Health). |

### childcare and senior care (lines 1494-1580)

| line | len | member | says |
|---:|---:|---|---|
| 1504 | 45 | `void livingCarePage(VBox page, CareType care, List<CityNeeds.Need> all)` | Childcare or senior care: what does it buy? |
| 1551 | 4 | `HBox scaleRow(String label, double now, double atNone, double atAll, boolean log, String whole)` | One effect: its name, the scale, and today's figure with the old note behind its (i). |
| 1557 | 23 | `VBox careStatement(CareType care)` | The old page's first block - covers, the people or places it serves, built, staffed, short by - as it was. |

### death care (lines 1581-1712)

| line | len | member | says |
|---:|---:|---|---|
| 1591 | 49 | `void deathCarePage(VBox page, List<CityNeeds.Need> all, List<Event> events)` | Death care, which is the one service in the game that is a STOCK. |
| 1650 | 28 | `void deathNotices(VBox page)` | The two alerts the death care page always had - nowhere to bury them, filling up - as lines, on that page and on Health's Overview. |
| 1680 | 8 | `String outbreakWhole()` | The outbreak's (i), as the old alert said it. |
| 1690 | 22 | `VBox deathStatement()` | The old page's three blocks - this month, the ground, the crematoria - as they were, but for the ground's words. |

### EDUCATION (lines 1713-1807)

| line | len | member | says |
|---:|---:|---|---|
| 1734 | 34 | `List<Kpi> educationKpis(List<CityNeeds.Need> all)` |  |
| 1782 | 5 | `boolean educationNotRunYet()` | True when a save has been loaded and no month has run since. |
| 1788 | 19 | `void educationPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |

### the pipeline (lines 1808-2022)

| line | len | member | says |
|---:|---:|---|---|
| 1818 | 2 | **type** `record SchoolNode(EducationType type, String ring, double arc, String ringTone, String line1, String line2,...` | One school as the pipeline draws it, worked out without drawing it: its ring - since 0.7.41 what it serves in the one verdict on it: a basic stage's coverage, a school above the ladder its seats over who would come (i... |
| 1821 | 34 | `SchoolNode schoolNode(EducationType t, List<CityNeeds.Need> all)` |  |
| 1863 | 1 | **type** `record Gate(String binding, String words)` | Which gate holds a course above the ladder, from the reads the course page draws its funnel from: no seats built (how many would come, and since 0.7.51 the posts for them, CityNeeds.hires()), built and unstaffed, the ... |
| 1865 | 20 | `Gate gate(EducationType t)` |  |
| 1886 | 40 | `void educationOverview(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 1932 | 6 | `static Node lane(List<? extends Node> nodes)` | A lane of nodes that wraps rather than overflowing a narrow window. |
| 1939 | 6 | `static Label chevron()` |  |
| 1947 | 34 | `VBox node(SchoolNode n)` | One node of the pipeline: icon, name and ring; two lines; the gate as a chip. |
| 1983 | 17 | `VBox diplomaNode()` | The ladder's end: the diplomas the school leavers took this month (Education.getNewDiplomas(), gross), or "not recorded yet" after a load. |

### the basic ladder (lines 2023-2111)

| line | len | member | says |
|---:|---:|---|---|
| 2033 | 54 | `void basicLadderPage(VBox page, List<CityNeeds.Need> all)` | The three stages a child passes through, and the one holding up the rest. |
| 2089 | 22 | `javafx.scene.layout.GridPane ladderTable()` | The old ladder table: stage, to teach, seats, staffed, in class, served ("covered" until 0.7.41). |

### a course (lines 2112-2423)

| line | len | member | says |
|---:|---:|---|---|
| 2123 | 7 | `void coursePage(VBox page, EducationType course, List<CityNeeds.Need> all)` | One adult course: why that many and not more. |
| 2132 | 4 | `static String courseHead(EducationType course)` | A course's heading line: its length and who it takes. |
| 2144 | 69 | `VBox courseView(EducationType course, boolean compact)` | The course's picture: the funnel (could enrol, willing, start a month, seats free, enrolling) with the gate that binds outlined, the two gauges (the wage return, who can afford it), and the cohort bars - or, for a cou... |
| 2219 | 26 | `List<FunnelStep> courseSteps(EducationType course)` | A course's gates as funnel steps, worked out without drawing them: who could enrol, of whom willing, who start in a month, the seats free and who enrols - the old page's figures, from the same reads. |
| 2266 | 15 | `static String affordNote(double afford)` | Who can afford it, in words. |
| 2283 | 18 | `String returnNote(EducationType course, LabourMarket market, double back)` | What the wage return is actually comparing, in words. |
| 2308 | 43 | `static VBox gauge(double share, String name, String info, double size)` | A gauge: half a ring, filled to a share (held to one - a return past a doubling is full participation already), its figure in the middle and its name under it, with its (i). |
| 2353 | 20 | `void professionsPage(VBox page, List<CityNeeds.Need> all)` | The four schools that gate a job rather than raise a level, two by two. |
| 2375 | 28 | `VBox courseStatement(EducationType course)` | The old course block's lines - the pipeline, the gates, the two things that move it - as they were. |
| 2411 | 12 | `HBox tuitionLine(EducationType course)` | The price of a seat, and the dial that decides who pays it, in one line (0.7.28: "What a seat costs" and its three lines). |

### UTILITIES (lines 2424-2691)

| line | len | member | says |
|---:|---:|---|---|
| 2448 | 26 | `List<Kpi> utilityKpis(List<CityNeeds.Need> all)` | SERVED (0.7.41): POWER, WATER and ROADS read what they serve, in the one verdict - they were the share supplied, held to 100%, and the road's flow, in NEEDS YOU's colour for the load (ui9's decision 5). |
| 2475 | 12 | `void utilityPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 2496 | 26 | `VBox roadCard(List<CityNeeds.Need> all)` | The road, as one card (0.7.29): its two figures as every screen writes them - "62% served · 56% flow" since 0.7.41 ("162% full" until then) - the first in the one verdict on what it serves, and the door to Infrastruct... |
| 2534 | 4 | **type** `record UtilityRow(String name, String icon, String figure, String figureWords, String tone, double could, d...` | One capacity row, worked out without drawing it: its name and icon; the verdict figure - what it serves, unclamped, in the one verdict on it (0.7.41; the share supplied, held to 100%, in NEEDS YOU's colour for the loa... |
| 2539 | 38 | `List<UtilityRow> utilityRows(List<CityNeeds.Need> all)` |  |
| 2585 | 7 | `static String shortWords(double asked, double now, double could, java.util.function.DoubleFunction<String> unit)` | How short or spare a supply is, both ways (spec bug 17: the page's "Short by" was full staff against the draw and its alert was now against the draw, and neither said which): "short 38.9 MW now · 26.6 MW even fully st... |
| 2594 | 40 | `VBox utilityCard(UtilityRow r)` | One capacity row: the name and its verdict figure; the bar with who draws it under it; how short, and the door. |
| 2666 | 25 | `VBox utilityStatement(String key)` | A row's old lines, in its fold - kW for watts, and the homes for "resident draw". |

### SAFETY (2026-09-11) (lines 2692-3004)

| line | len | member | says |
|---:|---:|---|---|
| 2707 | 21 | `List<Kpi> safetyKpis(List<CityNeeds.Need> all)` |  |
| 2729 | 8 | `void safetyPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 2744 | 14 | `List<Part> crimeParts()` | The crime's reasons as parts of the month's crime: Crime's own split (getCrimes(cause), each cause's share of the pressure), in the causes' order and the categories' colours - never a verdict's. |
| 2760 | 74 | `void crimeOverview(VBox page, List<CityNeeds.Need> all)` | Where the crime comes from, and what the police and the cells are doing about it. |
| 2836 | 7 | `HBox footDoors(String pageName, BuildAdvice.Measure m)` | A card's two doors: its page, and Build on its ring. |
| 2845 | 5 | `static VBox didLine(String name, String value, String line)` | One line of what the crime did: a count, its name, and a smaller line. |
| 2875 | 23 | `VBox causesTable()` | The old "Where it comes from" table: reason, adults, weight, crimes. |
| 2900 | 43 | `void policePage(VBox page)` | What the police are doing, and what more of them would: with no police against with these, the officers, one more station. |
| 2945 | 10 | `static HBox crimeRow(String name, double crimes, double scale, String colour)` | One of the police page's two bars: its name, the bar on the shared scale, the crimes a month. |
| 2957 | 47 | `void prisonsPage(VBox page)` | Who is inside, and whether the city can hold who the police catch. |

### THE BOOKS. (lines 3005-3029)

| line | len | member | says |
|---:|---:|---|---|
| 3026 | 3 | `HBox bookRow(String label, double thousands, String tone)` | One line of a set of books: label, figure, and a colour when it matters. |

### HEALTH (lines 3030-3181)

| line | len | member | says |
|---:|---:|---|---|
| 3032 | 18 | `void healthBooksPage(VBox page)` |  |
| 3052 | 13 | `String pricedOutWords()` | ...and who the price turned away (2026-09-19): the dial is on the Policy tab's Health page; this line says what it did. |
| 3073 | 108 | `VBox healthBooksStatement()` | The old Health books, every line: what care charges for, what it costs, where the money goes. |

### EDUCATION (lines 3182-3292)

| line | len | member | says |
|---:|---:|---|---|
| 3184 | 20 | `void educationBooksPage(VBox page)` |  |
| 3212 | 80 | `VBox educationBooksStatement()` | The old Education books, every line but two: "Tuition the city covers" listed as a cost and the alert that the subsidy was counted twice. |

### UTILITIES (lines 3293-3402)

| line | len | member | says |
|---:|---:|---|---|
| 3295 | 28 | `void utilityBooksPage(VBox page)` |  |
| 3336 | 4 | `double roadUpkeep()` | THE ROADS' REPAIR BILL, which until 2026-09-09 could only ever read ZERO. |
| 3342 | 1 | `static double billedShare(double billed, double draw)` | The share of a draw that is billed: the measured share, where the books said "four fifths" (0.7.28). |
| 3352 | 50 | `VBox utilityBooksStatement()` | The old utilities' books, every line - but: kW for watts; the unbilled draw is the homes and the city's own buildings, not "resident draw"; households are not billed, rather than "have no cash account" (they have bala... |

### SAFETY (lines 3403-3452)

| line | len | member | says |
|---:|---:|---|---|
| 3406 | 38 | `void safetyBooksPage(VBox page)` | What it costs. |
| 3446 | 6 | `static String perHead(Crime crime, double police, double prisons)` | "$X an officer on shift a year, and $Y a prisoner a year" - the officers past the founding constabulary, which costs nothing. |


# ServicesScreen.java - 3,150 lines · 124 methods · 23 constants · interface

`ham/citybuildersim/ui/ServicesScreen.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (420), [CityNeeds](CityNeeds.md) (135), [CareType](CareType.md) (89), [EducationType](EducationType.md) (49), [BuildAdvice](BuildAdvice.md) (46), [Healthcare](Healthcare.md) (40), [BuildScreen](BuildScreen.md) (32), [Icons](Icons.md) (28), [Crime](Crime.md) (26), [Sickness](Sickness.md) (22), [AgeBand](AgeBand.md) (21), [Education](Education.md) (21), [BuildingManager](BuildingManager.md) (19), [Health](Health.md) (17), [SafetyType](SafetyType.md) (17), [Money](Money.md) (11), [Migration](Migration.md) (7), [UtilitiesHandler](UtilitiesHandler.md) (6), [LabourMarket](LabourMarket.md) (5), [Pieces](Pieces.md) (4), [PopulationManager](PopulationManager.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [UserInterface](UserInterface.md) (2), [BuildingType](BuildingType.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [SummaryScreen](SummaryScreen.md) (1), [HistorySave](HistorySave.md) (1), [CityCalendar](CityCalendar.md) (1), [WageBand](WageBand.md) (1)... and 2 more

**Used by (4):** [BuildScreen](BuildScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 46 | SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. |
| 126 | THE FRAME (0.7.28) |
| 328 | THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. |
| 435 | EVENTS (0.7.28) |
| 510 | · the screen's own pieces |
| 674 | HEALTH |
| 786 | · the Overview |
| 944 | · the care cards |
| 1090 | · general care |
| 1268 | · childcare and senior care |
| 1353 | · death care |
| 1487 | EDUCATION |
| 1582 | · the pipeline |
| 1778 | · the basic ladder |
| 1863 | · a course |
| 2153 | UTILITIES |
| 2418 | SAFETY (2026-09-11) |
| 2714 | THE BOOKS. |
| 2739 | · HEALTH |
| 2827 | · · what it costs |
| 2839 | · · where the money goes |
| 2887 | · EDUCATION |
| 2955 | · · what it costs |
| 2964 | · · where the money goes |
| 2991 | · UTILITIES |
| 3101 | · SAFETY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `ServicesScreen.OVERVIEW` | `"Overview", BOOKS = "Books"` | The page every system opens on (0.7.28), and the last chip of each, its books. |
| 107 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 110 | `ServicesScreen.SERVICE_HOME` | `OVERVIEW` | ...and every system opens on its Overview (0.7.28; General care until then). |
| 136 | `ServicesScreen.FRAME_CHROME` | `268` | How much of the stage the fixed frame takes above the page's scroller: the head, the systems, the four figures with their sparklines and changes, the pages, and their gaps. |
| 809 | `ServicesScreen.SICK_INFO` | `String.format("The bar is today's sick rate split into what makes it, in poin...` | The sick rate's (i): what the bar is, its floor and its cap, and why it is a cost of output and not of wages. |
| 1053 | `ServicesScreen.BURIAL_INFO` | `"A household that can save a plot's price over ten years chooses burial; " + ...` | The burial choice (the death care page's note). |
| 1159 | `ServicesScreen.LONG_SICK_INFO` | `String.format("Everybody sick this month, by how long they have been ill: the...` | The long sick's (i): who can die of it, at what chance by age (the elders' too, which the old note left out). |
| 1168 | `ServicesScreen.RECOVERY_INFO` | `String.format("The share of the sick who get better in a month: %.0f%% with n...` | What care cures, in words. |
| 1416 | `ServicesScreen.PLOTS_INFO` | `"Plots are consumed permanently — the land never comes back, and a cemetery "...` | Plots are permanent (the ground's note). |
| 1420 | `ServicesScreen.CREMATORIA_INFO` | `"A rate rather than a stock, and it needs almost no land — which makes it " +...` | The crematoria's note. |
| 1757 | `ServicesScreen.DIPLOMA_INFO` | `"Teens age out at a steady rate and the ones who were in school leave with " ...` | The diplomas' (i): the teens' note, and what the figure is (and is not). |
| 1765 | `ServicesScreen.LADDER_INFO` | `String.format("The ladder serves the minimum of its three stages, not the ave...` | The basic ladder's notes: the minimum of three, and the four-and-three split. |
| 1773 | `ServicesScreen.PROFESSIONS_INFO` | `"A band row on the People screen can say the city has eight hundred " + "grad...` | The professions page's sentence. |
| 1990 | `ServicesScreen.GATES_INFO` | `String.format("The funnel is why that many and not more.Who could enrol holds...` | The gates' (i): why that many and not more, as the old page said it under its five lines. |
| 2002 | `ServicesScreen.COULD_ENROL` | `"Who holds the level this course takes, in the workforce - and for a " + "pro...` | Who could enrol, in words (the old page's note). |
| 2006 | `ServicesScreen.RETURN_INFO_PREFIX` | `"How much better off somebody is for doing it - 0 means not worth it.\n\n"` | The wage return's (i) opens on this, then says what the return is measured against (returnNote()). |
| 2363 | `ServicesScreen.POWER_INFO` | `"Power is counted in kilowatts, a rate - the screens wrote watts until " + "0...` | The power row's (i): the unit, the staff discount, one workforce, who is billed. |
| 2373 | `ServicesScreen.BROWNOUT_INFO` | `"Every industrial and commercial building's output is cut in proportion " + "...` | A brownout's (i), as the grid's old alert said it. |
| 2378 | `ServicesScreen.WATER_INFO` | `"Water is counted in units of 10,000 gallons a month.The people draw " + "the...` | The water row's (i). |
| 2573 | `ServicesScreen.CAUSES_INFO` | `"Every adult at liberty is counted once, at the heaviest reason they have.The...` | The causes' (i). |
| 2579 | `ServicesScreen.STOLEN_INFO` | `"What is stolen goes to the offenders' households.The killings are next" + " ...` | What is stolen, and the injured (the old "what it did" note). |
| 2583 | `ServicesScreen.CAUGHT_INFO` | `"Anybody caught with no staffed cell free stays on the street and keeps" + " ...` | Anybody caught with no cell (the prisons' note). |
| 2588 | `ServicesScreen.OFFICERS_INFO` | `crime -> String.format("%s officers per 100,000 people.Canada has %s; full co...` | The officers against Canada, and the founding constabulary. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 108 | `String serviceArea` |  |
| 111 | `String servicePage` |  |
| 121 | `private final java.util.Set<String> detailsOpen` | Which "details" folds are open, by name - remembered while the game runs, not saved. |
| 124 | `private final java.util.Set<String> seen` | The events whose system has been opened since they happened, so their "!" goes (by kind and month). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 3112 | **type** `final class ServicesScreen` | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one picture answers the system's question, then the pages behind it and the... |
| 44 | 1 | `ServicesScreen(UserInterface ui)` |  |

### SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. (lines 46-125)

| line | len | member | says |
|---:|---:|---|---|
| 86 | 1 | **type** `record ServiceArea(String name, String[] pages)` | A system, and the parts of it the second strip offers. |
| 91 | 14 | `static ServiceArea[] serviceAreas()` |  |
| 113 | 6 | `ServiceArea currentArea()` |  |

### THE FRAME (0.7.28) (lines 126-327)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 49 | `void showServicesStatsMenu()` |  |
| 198 | 4 | `void openPage()` | Draw a page the player just picked, at the top of itself. |
| 204 | 6 | `void open(String area, String page)` | One system on one of its pages, at the top of it (0.7.28): every door into Services - the left panel's OFF SICK, Build's "why ›", a card, a part of a bar. |
| 216 | 19 | `static String[] pageFor(BuildAdvice.Measure m)` | The page that explains a Build ring (0.7.28), as {system, page}: Build's "why ›". |
| 236 | 6 | `static String[] areaNames()` |  |
| 243 | 6 | `static ServiceArea currentAreaFor(String name)` |  |
| 251 | 4 | `HBox head()` | The head: "Services › Health" with the people teal's swatch - the title a way back to the system's Overview - and Build's door at the right (a pill since 0.7.34). |
| 257 | 8 | `static String areaIcon(String area)` | A system's icon: the Build category's it is built in, Utilities the power's. |
| 267 | 8 | `static boolean answers(String area, CityNeeds.Go go)` | The NEEDS YOU rows a system answers: Health's healthcare, Education's, Utilities' power, water and the road, Safety's. |
| 277 | 8 | `static CityNeeds.Need areaWorst(String area, List<CityNeeds.Need> all)` | A system's verdict: the worst of the rows it answers - by each row's colour since 0.7.41 (Need.verdictLevel(): a served row short of 100% is amber though not listed) - and that row (null when none is near its line). |
| 292 | 35 | `javafx.scene.layout.FlowPane areaStrip(List<CityNeeds.Need> all, List<Event> events)` | The systems, as chips (0.7.28): each its icon, its name and a dot in the colour of the worst NEEDS YOU row it answers, and a "!" while something happened there this month that the player has not opened. |

### THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. (lines 328-434)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 2 | **type** `record Kpi(String label, String value, String note, String tone, String where, Runnable go, String series, ...` | One of the four: its label, figure, note and colour (a verdict only where a NEEDS YOU row judges it), its door - where, and what it opens - the history series its sparkline draws (null: none kept) and how its change o... |
| 350 | 6 | `HBox areaVitals(List<CityNeeds.Need> all)` |  |
| 357 | 8 | `List<Kpi> kpis(String area, List<CityNeeds.Need> all)` |  |
| 367 | 21 | `VBox kpiCell(Kpi k)` | A limit cell with its sparkline at the right of its figure and its change under the note. |
| 390 | 10 | `double[] lastTwo(String series)` | The last two months a series recorded, oldest first; NaN where it has fewer. |
| 402 | 9 | `String change(String series, java.util.function.DoubleFunction<String> written)` | A figure's change on last month, from its history: "▲ 0.1 pts on last month", "no change on last month"; null with under two months kept. |
| 413 | 4 | `static String points(double rate)` | A share of the workforce or a rate, in points: "13.8 pts", "0.03 pts" under a tenth. |
| 419 | 1 | `static String pct(double share)` | A share, as Build's rings write it. |
| 422 | 1 | `static String tone(CityNeeds.Need n)` | A row's verdict colour - a served row's the one verdict on what it serves (0.7.41), any other's its level - or the plain figure's where no row judges it. |
| 425 | 9 | `static CityNeeds.Need need(List<CityNeeds.Need> all, CityNeeds.Kind kind, CareType care, EducationType school)` | The NEEDS YOU row of a kind - for CARE, of a care type; for HIGHER_SCHOOL, of a school - or null. |

### EVENTS (0.7.28) (lines 435-509)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 1 | **type** `record Event(String area, String key, String words, String whole, String tone)` | One event: its system, a key unique to it and its month, the line, its (i) (null: none) and its colour - a verdict, the event is news. |
| 451 | 37 | `List<Event> events()` |  |
| 490 | 14 | `HBox eventLine(String words, String whole, String tone)` | An event, or a state worth a line: a tinted line with its icon, its words wrapping, and its (i). |
| 506 | 3 | `void eventLines(VBox page, String area, List<Event> events)` | This system's events as lines. |

### the screen's own pieces (lines 510-673)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 6 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 521 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 529 | 8 | `static VBox card(Node...children)` | A card on the Build ground: raised, rounded, a hairline edge. |
| 539 | 13 | `static VBox doorCard(VBox c, Runnable go, String where)` | ...made a door: a hand, an accent edge under the pointer, a click anywhere opens `go`; its tooltip says where. |
| 554 | 12 | `static HBox cardHead(String svg, String name, String info, Node right)` | A card's head: its icon in a teal square, its name, and its (i) at the right. |
| 573 | 14 | `VBox details(String name, String caption, Node...inside)` | A fold: "details ▸ caption", and what is inside when it is open - remembered by name while the game runs (the land office's and the People page's way). |
| 589 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 597 | 11 | `HBox priceLine(String text, String whole, String door, String policyPage)` | "fee $10 a visit · Set the fee ›": a price, and the door to the dial on the Policy tab. |
| 615 | 17 | `VBox moneyBars(String topName, List<Slice> top, String bottomName, List<Slice> bottom, Node right)` | Two stacked bars on one money scale, each with its total at the right, a key under them, and the bottom line beside them (0.7.28): the books' cost against what came back, a utility's sales against its wages. |
| 634 | 17 | `static HBox moneyRow(String name, List<Slice> parts, double total, double scale)` | One of moneyBars()'s rows: its name, the bar of its parts, its total. |
| 653 | 7 | `static VBox bottomLine(String value, String name, String line, String tone)` | The bottom line beside a books card: a figure, its name and a line under. |
| 662 | 3 | `HBox buildFor(BuildAdvice.Measure m)` | "Build for it ›": Build's category opened on this ring, in the building pink - a pill since 0.7.34 (Pieces.doorPill()). |
| 667 | 6 | `static double sum(double[] values)` | A double[] in one figure - the education arrays are per-type. |

### HEALTH (lines 674-785)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 45 | `List<Kpi> healthKpis(List<CityNeeds.Need> all)` | The four figures: the sick rate, the thinnest care, the ground (or the unburied), the bill. |
| 735 | 9 | `CareType thinnest()` | The living care with the least coverage the month applied: general care first of equals. |
| 752 | 11 | `String groundWords()` | The ground in a few words. |
| 765 | 9 | `String groundNote()` | ...and the line under it. |
| 775 | 10 | `void healthPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |

### the Overview (lines 786-943)

| line | len | member | says |
|---:|---:|---|---|
| 788 | 19 | `void healthOverview(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 828 | 23 | `List<Part> sickParts()` | The sick rate's parts, in points, in the order the bar draws them - the floor, what the missing doctors add, hunger, no home, the unburied, an outbreak, violence - and anything left over, hatched: Health's own figures... |
| 853 | 4 | `static double named(Health h)` | Health's named parts, added: what the old breakdown summed. |
| 859 | 5 | `double sickTrimmed()` | What the cap took off the parts, as a positive rate; 0 when it took nothing. |
| 866 | 28 | `HBox sickCard()` | The sick rate's card: the rate itself, big, in OFF SICK's colour, and the bar of its causes. |
| 896 | 31 | `VBox sickStatement()` | The old breakdown, every line as it was: the fold under the bar. |
| 929 | 3 | `Node careScarceNote()` | At the right of the care's heading: which of the city's healthcare buildings' staff it fills worst (Build's note, on what stands). |
| 934 | 9 | `String careScarceWords()` | ...its words. |

### the care cards (lines 944-1089)

| line | len | member | says |
|---:|---:|---|---|
| 953 | 3 | **type** `record CareWords(String name, String icon, String ring, double arc, String tone, String buys, String buysWo...` | What one care card says, worked out without drawing it (a probe reads it): its ring - the coverage the month applied, Healthcare.getCoverage(), in its NEEDS YOU row's colour - the one thing it buys and that figure's r... |
| 957 | 45 | `CareWords careWords(CareType care, List<CityNeeds.Need> all)` |  |
| 1004 | 5 | `static String times(double factor)` | A factor, as the care pages write one: "×36.5", "×0.025". |
| 1011 | 8 | `String careInfo(CareType care, double cover)` | Who a kind of care serves, and what its ring is (the old page's three notes, and the month's coverage). |
| 1025 | 8 | `String seniorNeedWords()` | SENIOR CARE'S DENOMINATOR IS PLACES, not people (0.7.28; the page read "People to serve" over a figure of places): a senior needs a fifth of a place, an elder a whole one (CareType.placesPerHead()). |
| 1035 | 16 | `CareWords deathWords(List<CityNeeds.Need> all)` | Death care's card: the dead dealt with as its ring, the ground left as what it buys, the plots as its bar. |
| 1059 | 22 | `VBox careCard(CareWords w)` | One care card: head, ring and what it buys, its places as a supply bar, and its two doors. |
| 1083 | 6 | `static javafx.scene.text.Text textRun(String text, double size, String tone, boolean mono)` | A run of text in a flow: a figure in mono, or words. |

### general care (lines 1090-1267)

| line | len | member | says |
|---:|---:|---|---|
| 1099 | 51 | `void generalCarePage(VBox page, List<CityNeeds.Need> all, List<Event> events)` | General care: how many of the sick does care cure, and how many stay ill long enough to die? |
| 1152 | 5 | `double[] longSick()` | The long sick, a bar a slot: everybody ill under a month to a year or more, in people (Sickness.peopleInSlot()). |
| 1175 | 14 | `javafx.scene.layout.GridPane deathChances()` | The death chances by age, with the share of each band past two months and its dead last month. |
| 1198 | 22 | `VBox careFunnel(CareType care)` | Who a kind of care reaches, as a funnel: the people (or, for senior care, the places) it is measured against, the places built, the places staffed, and those it treated - with the people the fee turned away as a step ... |
| 1222 | 21 | `List<FunnelStep> careSteps(CareType care)` | ...its steps, worked out without drawing them. |
| 1250 | 6 | `boolean healthNotRunYet()` | True when a save from before 0.7.46 has been loaded and no month has run since: care that stands treated nobody, by the count (Healthcare's served[] was not saved until A3), while the fees it raised were. |
| 1258 | 9 | `HBox feeLine(CareType care, String per)` | "fee $10 a visit · Set the fee ›": the price of a kind of care, and the dial (Policy › Promises › Health). |

### childcare and senior care (lines 1268-1352)

| line | len | member | says |
|---:|---:|---|---|
| 1278 | 43 | `void livingCarePage(VBox page, CareType care, List<CityNeeds.Need> all)` | Childcare or senior care: what does it buy? |
| 1323 | 4 | `HBox scaleRow(String label, double now, double atNone, double atAll, boolean log, String whole)` | One effect: its name, the scale, and today's figure with the old note behind its (i). |
| 1329 | 23 | `VBox careStatement(CareType care)` | The old page's first block - covers, the people or places it serves, built, staffed, short by - as it was. |

### death care (lines 1353-1486)

| line | len | member | says |
|---:|---:|---|---|
| 1363 | 51 | `void deathCarePage(VBox page, List<CityNeeds.Need> all, List<Event> events)` | Death care, which is the one service in the game that is a STOCK. |
| 1424 | 28 | `void deathNotices(VBox page)` | The two alerts the death care page always had - nowhere to bury them, filling up - as lines, on that page and on Health's Overview. |
| 1454 | 8 | `String outbreakWhole()` | The outbreak's (i), as the old alert said it. |
| 1464 | 22 | `VBox deathStatement()` | The old page's three blocks - this month, the ground, the crematoria - as they were, but for the ground's words. |

### EDUCATION (lines 1487-1581)

| line | len | member | says |
|---:|---:|---|---|
| 1508 | 34 | `List<Kpi> educationKpis(List<CityNeeds.Need> all)` |  |
| 1556 | 5 | `boolean educationNotRunYet()` | True when a save has been loaded and no month has run since. |
| 1562 | 19 | `void educationPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |

### the pipeline (lines 1582-1777)

| line | len | member | says |
|---:|---:|---|---|
| 1592 | 2 | **type** `record SchoolNode(EducationType type, String ring, double arc, String ringTone, String line1, String line2,...` | One school as the pipeline draws it, worked out without drawing it: its ring - since 0.7.41 what it serves in the one verdict on it: a basic stage's coverage, a school above the ladder its seats over who would come (i... |
| 1595 | 34 | `SchoolNode schoolNode(EducationType t, List<CityNeeds.Need> all)` |  |
| 1637 | 1 | **type** `record Gate(String binding, String words)` | Which gate holds a course above the ladder, from the reads the course page draws its funnel from: no seats built (how many would come, and since 0.7.51 the posts for them, CityNeeds.hires()), built and unstaffed, the ... |
| 1639 | 20 | `Gate gate(EducationType t)` |  |
| 1660 | 33 | `void educationOverview(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 1695 | 7 | `static Node lane(HBox row)` | A lane of nodes that wraps rather than overflowing a narrow window. |
| 1703 | 6 | `static Label chevron()` |  |
| 1711 | 26 | `VBox node(SchoolNode n)` | One node of the pipeline: icon, name and ring; two lines; the gate as a chip. |
| 1739 | 16 | `VBox diplomaNode()` | The ladder's end: the diplomas the school leavers took this month (Education.getNewDiplomas(), gross), or "not recorded yet" after a load. |

### the basic ladder (lines 1778-1862)

| line | len | member | says |
|---:|---:|---|---|
| 1788 | 50 | `void basicLadderPage(VBox page, List<CityNeeds.Need> all)` | The three stages a child passes through, and the one holding up the rest. |
| 1840 | 22 | `javafx.scene.layout.GridPane ladderTable()` | The old ladder table: stage, to teach, seats, staffed, in class, served ("covered" until 0.7.41). |

### a course (lines 1863-2152)

| line | len | member | says |
|---:|---:|---|---|
| 1874 | 7 | `void coursePage(VBox page, EducationType course, List<CityNeeds.Need> all)` | One adult course: why that many and not more. |
| 1883 | 4 | `static String courseHead(EducationType course)` | A course's heading line: its length and who it takes. |
| 1895 | 61 | `VBox courseView(EducationType course, boolean compact)` | The course's picture: the funnel (could enrol, willing, start a month, seats free, enrolling) with the gate that binds outlined, the two gauges (the wage return, who can afford it), and the cohort bars - or, for a cou... |
| 1962 | 26 | `List<FunnelStep> courseSteps(EducationType course)` | A course's gates as funnel steps, worked out without drawing them: who could enrol, of whom willing, who start in a month, the seats free and who enrols - the old page's figures, from the same reads. |
| 2009 | 15 | `static String affordNote(double afford)` | Who can afford it, in words. |
| 2026 | 18 | `String returnNote(EducationType course, LabourMarket market, double back)` | What the wage return is actually comparing, in words. |
| 2051 | 30 | `static VBox gauge(double share, String name, String info, double size)` | A gauge: half a ring, filled to a share (held to one - a return past a doubling is full participation already), its figure in the middle and its name under it, with its (i). |
| 2083 | 19 | `void professionsPage(VBox page, List<CityNeeds.Need> all)` | The four schools that gate a job rather than raise a level, two by two. |
| 2104 | 28 | `VBox courseStatement(EducationType course)` | The old course block's lines - the pipeline, the gates, the two things that move it - as they were. |
| 2140 | 12 | `HBox tuitionLine(EducationType course)` | The price of a seat, and the dial that decides who pays it, in one line (0.7.28: "What a seat costs" and its three lines). |

### UTILITIES (lines 2153-2417)

| line | len | member | says |
|---:|---:|---|---|
| 2177 | 26 | `List<Kpi> utilityKpis(List<CityNeeds.Need> all)` | SERVED (0.7.41): POWER, WATER and ROADS read what they serve, in the one verdict - they were the share supplied, held to 100%, and the road's flow, in NEEDS YOU's colour for the load (ui9's decision 5). |
| 2204 | 11 | `void utilityPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 2224 | 26 | `VBox roadCard(List<CityNeeds.Need> all)` | The road, as one card (0.7.29): its two figures as every screen writes them - "62% served · 56% flow" since 0.7.41 ("162% full" until then) - the first in the one verdict on what it serves, and the door to Infrastruct... |
| 2262 | 4 | **type** `record UtilityRow(String name, String icon, String figure, String figureWords, String tone, double could, d...` | One capacity row, worked out without drawing it: its name and icon; the verdict figure - what it serves, unclamped, in the one verdict on it (0.7.41; the share supplied, held to 100%, in NEEDS YOU's colour for the loa... |
| 2267 | 38 | `List<UtilityRow> utilityRows(List<CityNeeds.Need> all)` |  |
| 2313 | 7 | `static String shortWords(double asked, double now, double could, java.util.function.DoubleFunction<String> unit)` | How short or spare a supply is, both ways (spec bug 17: the page's "Short by" was full staff against the draw and its alert was now against the draw, and neither said which): "short 38.9 MW now · 26.6 MW even fully st... |
| 2322 | 39 | `VBox utilityCard(UtilityRow r)` | One capacity row: the name and its verdict figure; the bar with who draws it under it; how short, and the door. |
| 2392 | 25 | `VBox utilityStatement(String key)` | A row's old lines, in its fold - kW for watts, and the homes for "resident draw". |

### SAFETY (2026-09-11) (lines 2418-2713)

| line | len | member | says |
|---:|---:|---|---|
| 2433 | 21 | `List<Kpi> safetyKpis(List<CityNeeds.Need> all)` |  |
| 2455 | 8 | `void safetyPage(VBox page, List<CityNeeds.Need> all, List<Event> events)` |  |
| 2470 | 14 | `List<Part> crimeParts()` | The crime's reasons as parts of the month's crime: Crime's own split (getCrimes(cause), each cause's share of the pressure), in the causes' order and the categories' colours - never a verdict's. |
| 2486 | 69 | `void crimeOverview(VBox page, List<CityNeeds.Need> all)` | Where the crime comes from, and what the police and the cells are doing about it. |
| 2557 | 7 | `HBox footDoors(String pageName, BuildAdvice.Measure m)` | A card's two doors: its page, and Build on its ring. |
| 2566 | 5 | `static VBox didLine(String name, String value, String line)` | One line of what the crime did: a count, its name, and a smaller line. |
| 2596 | 19 | `VBox causesTable()` | The old "Where it comes from" table: reason, adults, weight, crimes. |
| 2617 | 40 | `void policePage(VBox page)` | What the police are doing, and what more of them would: with no police against with these, the officers, one more station. |
| 2659 | 10 | `static HBox crimeRow(String name, double crimes, double scale, String colour)` | One of the police page's two bars: its name, the bar on the shared scale, the crimes a month. |
| 2671 | 42 | `void prisonsPage(VBox page)` | Who is inside, and whether the city can hold who the police catch. |

### THE BOOKS. (lines 2714-2738)

| line | len | member | says |
|---:|---:|---|---|
| 2735 | 3 | `HBox bookRow(String label, double thousands, String tone)` | One line of a set of books: label, figure, and a colour when it matters. |

### HEALTH (lines 2739-2886)

| line | len | member | says |
|---:|---:|---|---|
| 2741 | 18 | `void healthBooksPage(VBox page)` |  |
| 2761 | 13 | `String pricedOutWords()` | ...and who the price turned away (2026-09-19): the dial is on the Policy tab's Health page; this line says what it did. |
| 2782 | 104 | `VBox healthBooksStatement()` | The old Health books, every line: what care charges for, what it costs, where the money goes. |

### EDUCATION (lines 2887-2990)

| line | len | member | says |
|---:|---:|---|---|
| 2889 | 20 | `void educationBooksPage(VBox page)` |  |
| 2917 | 73 | `VBox educationBooksStatement()` | The old Education books, every line but two: "Tuition the city covers" listed as a cost and the alert that the subsidy was counted twice. |

### UTILITIES (lines 2991-3100)

| line | len | member | says |
|---:|---:|---|---|
| 2993 | 28 | `void utilityBooksPage(VBox page)` |  |
| 3034 | 4 | `double roadUpkeep()` | THE ROADS' REPAIR BILL, which until 2026-09-09 could only ever read ZERO. |
| 3040 | 1 | `static double billedShare(double billed, double draw)` | The share of a draw that is billed: the measured share, where the books said "four fifths" (0.7.28). |
| 3050 | 50 | `VBox utilityBooksStatement()` | The old utilities' books, every line - but: kW for watts; the unbilled draw is the homes and the city's own buildings, not "resident draw"; households are not billed, rather than "have no cash account" (they have bala... |

### SAFETY (lines 3101-3150)

| line | len | member | says |
|---:|---:|---|---|
| 3104 | 38 | `void safetyBooksPage(VBox page)` | What it costs. |
| 3144 | 6 | `static String perHead(Crime crime, double police, double prisons)` | "$X an officer on shift a year, and $Y a prisoner a year" - the officers past the founding constabulary, which costs nothing. |


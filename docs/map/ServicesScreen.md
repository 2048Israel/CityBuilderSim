# ServicesScreen.java - 2,549 lines · 49 methods · 3 constants · interface

`ham/citybuildersim/ui/ServicesScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The services tab: the systems the city runs and how well each covers -
> infrastructure (roads, transit, the railway, freight), safety, health,
> education, utilities - two strips picking the system and the part of it,
> the four figures for whichever is open, and the books each keeps.
> 
> Split out of UserInterface on 2026-09-18: the eight banners from SERVICES to
> THE BOOKS exactly as they were, the shell's members reached through ui. The
> shell still reads which system and page are open (serviceArea, servicePage)
> for the rail and the scroll memory, and the summary asks it for careCover().

**Uses:** [Palette](Palette.md) (398), [CareType](CareType.md) (42), [InfrastructureManager](InfrastructureManager.md) (24), [EducationType](EducationType.md) (22), [Crime](Crime.md) (14), [Education](Education.md) (13), [Healthcare](Healthcare.md) (12), [BuildingType](BuildingType.md) (12), [BuildingManager](BuildingManager.md) (10), [Rail](Rail.md) (9), [SafetyType](SafetyType.md) (9), [Health](Health.md) (9), [Sickness](Sickness.md) (9), [Traffic](Traffic.md) (8), [AgeBand](AgeBand.md) (7), [TaxPolicy](TaxPolicy.md) (5), [Sector](Sector.md) (5), [PopulationCohorts](PopulationCohorts.md) (4), [LabourMarket](LabourMarket.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [UserInterface](UserInterface.md) (2), [Good](Good.md) (2), [Migration](Migration.md) (2), [WageBand](WageBand.md) (2), [EconomyManager](EconomyManager.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [PrisonerHousehold](PrisonerHousehold.md) (1), [CityCalendar](CityCalendar.md) (1), [PopulationManager](PopulationManager.md) (1)... and 2 more

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 35 | SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. |
| 95 | INFRASTRUCTURE - the roads, the trams, the railway and the freight |
| 200 | · ROADS |
| 264 | · · the three streams |
| 294 | · · the cars |
| 341 | · TRANSIT - and the one control on this tab |
| 377 | · · three ceilings, lowest wins |
| 399 | · · and then two things walk it down |
| 421 | · · the books |
| 432 | · · the dial |
| 486 | · THE RAILWAY - the twelfth sector, and the only mode the city does not own |
| 524 | · · track and trains |
| 554 | · · what it hauls |
| 590 | · · the business |
| 624 | · FREIGHT - the band, the bill, and the lorries |
| 641 | · · the band |
| 691 | · · the bill |
| 708 | · · the lorries |
| 837 | THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. |
| 853 | SAFETY (2026-09-11) |
| 911 | · · where it comes from |
| 941 | · · what it did |
| 1110 | HEALTH |
| 1231 | · · what it buys |
| 1368 | · · the bill |
| 1421 | · · the ground |
| 1440 | · · the crematoria |
| 1452 | · · the bill |
| 1486 | EDUCATION |
| 1625 | · · what comes out |
| 1716 | · · the pipeline |
| 1734 | · · the gates |
| 1765 | · · the two things that move it |
| 1924 | UTILITIES |
| 2145 | THE BOOKS. |
| 2166 | · HEALTH |
| 2233 | · · what it costs |
| 2248 | · · where the money goes |
| 2308 | · EDUCATION |
| 2362 | · · what it costs |
| 2400 | · · where the money goes |
| 2437 | · UTILITIES |
| 2509 | · · roads |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `ServicesScreen.SERVICE_AREA_HOME` | `"Health"` | Which system, and which part of it - remembered like the build category. |
| 85 | `ServicesScreen.SERVICE_HOME` | `"General care"` |  |
| 125 | `ServicesScreen.INFRA_PAGES` | `{ "Roads", "Transit", "The railway", "Freight" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 31 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 84 | `String serviceArea` |  |
| 86 | `String servicePage` |  |
| 128 | `String infraPage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 2522 | **type** `final class ServicesScreen` | The services tab: the systems the city runs and how well each covers - infrastructure (roads, transit, the railway, freight), safety, health, education, utilities - two strips picking the system and the part of it, th... |
| 33 | 1 | `ServicesScreen(UserInterface ui)` |  |

### SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. (lines 35-94)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1 | **type** `record ServiceArea(String name, String[] pages)` | A system, and the parts of it the second strip offers. |
| 67 | 14 | `static ServiceArea[] serviceAreas()` |  |
| 88 | 6 | `ServiceArea currentArea()` |  |

### INFRASTRUCTURE - the roads, the trams, the railway and the freight (lines 95-199)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 30 | `void showInfrastructureMenu()` |  |
| 169 | 30 | `HBox infraVitals()` | The four figures the whole tab is about, on every page of it. |

### ROADS (lines 200-340)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 136 | `void infraRoadsPage(VBox column)` |  |

### TRANSIT - and the one control on this tab (lines 341-485)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 140 | `void transitPage(VBox column)` |  |

### THE RAILWAY - the twelfth sector, and the only mode the city does not own (lines 486-623)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 133 | `void railwayPage(VBox column)` |  |

### FREIGHT - the band, the bill, and the lorries (lines 624-836)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 132 | `void freightPage(VBox column)` |  |
| 761 | 46 | `void showServicesStatsMenu()` |  |
| 818 | 4 | `void openPage()` | Draw a page the player just picked, at the top of itself. |
| 823 | 6 | `static String[] areaNames()` |  |
| 830 | 6 | `static ServiceArea currentAreaFor(String name)` |  |

### THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. (lines 837-852)

| line | len | member | says |
|---:|---:|---|---|
| 844 | 8 | `HBox areaVitals()` |  |

### SAFETY (2026-09-11) (lines 853-1109)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 3 | `static String crimeTone(double vsCanada)` |  |
| 868 | 18 | `HBox safetyVitals()` |  |
| 887 | 8 | `void safetyPage(VBox column)` |  |
| 897 | 68 | `void crimePage(VBox column)` | Where the crime comes from, and what it did. |
| 967 | 53 | `void policePage(VBox column)` | What the police are doing, and what more of them would. |
| 1022 | 50 | `void prisonsPage(VBox column)` | Who is inside, and whether the city can hold who the police catch. |
| 1074 | 35 | `void safetyBooksPage(VBox column)` | What it costs. |

### HEALTH (lines 1110-1485)

| line | len | member | says |
|---:|---:|---|---|
| 1115 | 5 | `double careCover(CareType care, PopulationCohorts cohorts, double[] staffing)` | How much of the people who need one kind of care can get it. |
| 1121 | 48 | `HBox healthVitals()` |  |
| 1170 | 9 | `void healthPage(VBox column)` |  |
| 1188 | 199 | `void livingCarePage(VBox column, CareType care)` | One kind of care for living people: what it covers, and what that buys. |
| 1396 | 89 | `void deathCarePage(VBox column)` | Death care, which is the one service in the game that is a STOCK. |

### EDUCATION (lines 1486-1923)

| line | len | member | says |
|---:|---:|---|---|
| 1501 | 22 | `HBox educationVitals()` |  |
| 1537 | 5 | `boolean educationNotRunYet()` | True when a save has been loaded and no month has run since. |
| 1543 | 18 | `void educationPage(VBox column)` |  |
| 1570 | 72 | `void basicLadderPage(VBox column)` | The three stages a child passes through, and the one holding up the rest. |
| 1644 | 5 | `void coursePage(VBox column, EducationType course)` | One adult course, in full. |
| 1651 | 18 | `void professionsPage(VBox column)` | The four schools that gate a job rather than raise a level. |
| 1678 | 114 | `VBox courseBlock(EducationType course)` | One adult course: the pipeline, the three gates, and the money. |
| 1794 | 16 | `VBox buildingItWouldNeed(EducationType course, LabourMarket market, double back, double afford)` | What a course would need, for a city that has not built one. |
| 1812 | 18 | `String returnNote(EducationType course, LabourMarket market, double back)` | What the wage return is actually comparing, in words. |
| 1839 | 42 | `VBox pipelineBars(double[] queue)` | Everybody part way through, as a bar per month. |
| 1889 | 34 | `VBox tuitionBlock(EducationType course)` | The price of a seat, and the dial that decides who pays it. |

### UTILITIES (lines 1924-2144)

| line | len | member | says |
|---:|---:|---|---|
| 1934 | 23 | `HBox utilityVitals()` |  |
| 1958 | 8 | `void utilityPage(VBox column)` |  |
| 1967 | 53 | `void powerPage(VBox column)` |  |
| 2021 | 55 | `void waterPage(VBox column)` |  |
| 2077 | 54 | `void roadsPage(VBox column)` |  |
| 2136 | 8 | `javafx.scene.layout.FlowPane utilityLinks(BuildingType type)` | Build the plant. |

### THE BOOKS. (lines 2145-2165)

| line | len | member | says |
|---:|---:|---|---|
| 2162 | 3 | `HBox bookRow(String label, double thousands, String tone)` | One line of a set of books: label, figure, and a colour when it matters. |

### HEALTH (lines 2166-2307)

| line | len | member | says |
|---:|---:|---|---|
| 2168 | 139 | `void healthBooksPage(VBox column)` |  |

### EDUCATION (lines 2308-2436)

| line | len | member | says |
|---:|---:|---|---|
| 2310 | 126 | `void educationBooksPage(VBox column)` |  |

### UTILITIES (lines 2437-2549)

| line | len | member | says |
|---:|---:|---|---|
| 2439 | 92 | `void utilityBooksPage(VBox column)` |  |
| 2533 | 8 | `Button buildLink(String label, String category, EnumSet<BuildingType> types)` | A button that goes straight to a category of the build list. |
| 2543 | 6 | `static double sum(double[] values)` | A double[] in one figure - the education arrays are per-type. |


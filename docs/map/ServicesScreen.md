# ServicesScreen.java - 2,550 lines · 49 methods · 3 constants · interface

`ham/citybuildersim/ui/ServicesScreen.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The services tab: the systems the city runs and how well each covers -
> infrastructure (roads, transit, the railway, freight), safety, health,
> education, utilities - two strips picking the system and the part of it,
> the four figures for whichever is open, and the books each keeps.
> 
> Split out of UserInterface on 2026-09-18: the eight banners from SERVICES to
> THE BOOKS exactly as they were, the shell's members reached through ui. The
> shell still reads which system and page are open (serviceArea, servicePage)
> for the rail and the scroll memory, and the summary asks it for careCover().

**Uses:** [Palette](Palette.md) (404), [CareType](CareType.md) (42), [InfrastructureManager](InfrastructureManager.md) (24), [EducationType](EducationType.md) (22), [Crime](Crime.md) (14), [Education](Education.md) (13), [Healthcare](Healthcare.md) (12), [BuildingType](BuildingType.md) (12), [BuildingManager](BuildingManager.md) (10), [Rail](Rail.md) (9), [SafetyType](SafetyType.md) (9), [Health](Health.md) (9), [Sickness](Sickness.md) (9), [Traffic](Traffic.md) (8), [AgeBand](AgeBand.md) (7), [TaxPolicy](TaxPolicy.md) (5), [Sector](Sector.md) (5), [PopulationCohorts](PopulationCohorts.md) (4), [LabourMarket](LabourMarket.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [UserInterface](UserInterface.md) (2), [Good](Good.md) (2), [Migration](Migration.md) (2), [WageBand](WageBand.md) (2), [EconomyManager](EconomyManager.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [PrisonerHousehold](PrisonerHousehold.md) (1), [CityCalendar](CityCalendar.md) (1), [PopulationManager](PopulationManager.md) (1)... and 2 more

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 35 | SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. |
| 95 | INFRASTRUCTURE - the roads, the trams, the railway and the freight |
| 202 | · ROADS |
| 266 | · · the three streams |
| 296 | · · the cars |
| 343 | · TRANSIT - and the one control on this tab |
| 379 | · · three ceilings, lowest wins |
| 401 | · · and then two things walk it down |
| 423 | · · the books |
| 434 | · · the dial |
| 488 | · THE RAILWAY - the twelfth sector, and the only mode the city does not own |
| 526 | · · track and trains |
| 556 | · · what it hauls |
| 592 | · · the business |
| 626 | · FREIGHT - the band, the bill, and the lorries |
| 643 | · · the band |
| 693 | · · the bill |
| 710 | · · the lorries |
| 841 | THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. |
| 857 | SAFETY (2026-09-11) |
| 915 | · · where it comes from |
| 945 | · · what it did |
| 1114 | HEALTH |
| 1232 | · · what it buys |
| 1369 | · · the bill |
| 1422 | · · the ground |
| 1441 | · · the crematoria |
| 1453 | · · the bill |
| 1487 | EDUCATION |
| 1626 | · · what comes out |
| 1717 | · · the pipeline |
| 1735 | · · the gates |
| 1766 | · · the two things that move it |
| 1925 | UTILITIES |
| 2146 | THE BOOKS. |
| 2167 | · HEALTH |
| 2234 | · · what it costs |
| 2249 | · · where the money goes |
| 2309 | · EDUCATION |
| 2363 | · · what it costs |
| 2401 | · · where the money goes |
| 2438 | · UTILITIES |
| 2510 | · · roads |

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
| 28 | 2523 | **type** `final class ServicesScreen` | The services tab: the systems the city runs and how well each covers - infrastructure (roads, transit, the railway, freight), safety, health, education, utilities - two strips picking the system and the part of it, th... |
| 33 | 1 | `ServicesScreen(UserInterface ui)` |  |

### SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS. (lines 35-94)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1 | **type** `record ServiceArea(String name, String[] pages)` | A system, and the parts of it the second strip offers. |
| 67 | 14 | `static ServiceArea[] serviceAreas()` |  |
| 88 | 6 | `ServiceArea currentArea()` |  |

### INFRASTRUCTURE - the roads, the trams, the railway and the freight (lines 95-201)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 32 | `void showInfrastructureMenu()` |  |
| 171 | 30 | `HBox infraVitals()` | The four figures the whole tab is about, on every page of it. |

### ROADS (lines 202-342)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 136 | `void infraRoadsPage(VBox column)` |  |

### TRANSIT - and the one control on this tab (lines 343-487)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 140 | `void transitPage(VBox column)` |  |

### THE RAILWAY - the twelfth sector, and the only mode the city does not own (lines 488-625)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 133 | `void railwayPage(VBox column)` |  |

### FREIGHT - the band, the bill, and the lorries (lines 626-840)

| line | len | member | says |
|---:|---:|---|---|
| 630 | 132 | `void freightPage(VBox column)` |  |
| 763 | 48 | `void showServicesStatsMenu()` |  |
| 822 | 4 | `void openPage()` | Draw a page the player just picked, at the top of itself. |
| 827 | 6 | `static String[] areaNames()` |  |
| 834 | 6 | `static ServiceArea currentAreaFor(String name)` |  |

### THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN. (lines 841-856)

| line | len | member | says |
|---:|---:|---|---|
| 848 | 8 | `HBox areaVitals()` |  |

### SAFETY (2026-09-11) (lines 857-1113)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 3 | `static String crimeTone(double vsCanada)` |  |
| 872 | 18 | `HBox safetyVitals()` |  |
| 891 | 8 | `void safetyPage(VBox column)` |  |
| 901 | 68 | `void crimePage(VBox column)` | Where the crime comes from, and what it did. |
| 971 | 53 | `void policePage(VBox column)` | What the police are doing, and what more of them would. |
| 1026 | 50 | `void prisonsPage(VBox column)` | Who is inside, and whether the city can hold who the police catch. |
| 1078 | 35 | `void safetyBooksPage(VBox column)` | What it costs. |

### HEALTH (lines 1114-1486)

| line | len | member | says |
|---:|---:|---|---|
| 1119 | 5 | `double careCover(CareType care, PopulationCohorts cohorts, double[] staffing)` | How much of the people who need one kind of care can get it. |
| 1125 | 45 | `HBox healthVitals()` |  |
| 1171 | 9 | `void healthPage(VBox column)` |  |
| 1189 | 199 | `void livingCarePage(VBox column, CareType care)` | One kind of care for living people: what it covers, and what that buys. |
| 1397 | 89 | `void deathCarePage(VBox column)` | Death care, which is the one service in the game that is a STOCK. |

### EDUCATION (lines 1487-1924)

| line | len | member | says |
|---:|---:|---|---|
| 1502 | 22 | `HBox educationVitals()` |  |
| 1538 | 5 | `boolean educationNotRunYet()` | True when a save has been loaded and no month has run since. |
| 1544 | 18 | `void educationPage(VBox column)` |  |
| 1571 | 72 | `void basicLadderPage(VBox column)` | The three stages a child passes through, and the one holding up the rest. |
| 1645 | 5 | `void coursePage(VBox column, EducationType course)` | One adult course, in full. |
| 1652 | 18 | `void professionsPage(VBox column)` | The four schools that gate a job rather than raise a level. |
| 1679 | 114 | `VBox courseBlock(EducationType course)` | One adult course: the pipeline, the three gates, and the money. |
| 1795 | 16 | `VBox buildingItWouldNeed(EducationType course, LabourMarket market, double back, double afford)` | What a course would need, for a city that has not built one. |
| 1813 | 18 | `String returnNote(EducationType course, LabourMarket market, double back)` | What the wage return is actually comparing, in words. |
| 1840 | 42 | `VBox pipelineBars(double[] queue)` | Everybody part way through, as a bar per month. |
| 1890 | 34 | `VBox tuitionBlock(EducationType course)` | The price of a seat, and the dial that decides who pays it. |

### UTILITIES (lines 1925-2145)

| line | len | member | says |
|---:|---:|---|---|
| 1935 | 23 | `HBox utilityVitals()` |  |
| 1959 | 8 | `void utilityPage(VBox column)` |  |
| 1968 | 53 | `void powerPage(VBox column)` |  |
| 2022 | 55 | `void waterPage(VBox column)` |  |
| 2078 | 54 | `void roadsPage(VBox column)` |  |
| 2137 | 8 | `javafx.scene.layout.FlowPane utilityLinks(BuildingType type)` | Build the plant. |

### THE BOOKS. (lines 2146-2166)

| line | len | member | says |
|---:|---:|---|---|
| 2163 | 3 | `HBox bookRow(String label, double thousands, String tone)` | One line of a set of books: label, figure, and a colour when it matters. |

### HEALTH (lines 2167-2308)

| line | len | member | says |
|---:|---:|---|---|
| 2169 | 139 | `void healthBooksPage(VBox column)` |  |

### EDUCATION (lines 2309-2437)

| line | len | member | says |
|---:|---:|---|---|
| 2311 | 126 | `void educationBooksPage(VBox column)` |  |

### UTILITIES (lines 2438-2550)

| line | len | member | says |
|---:|---:|---|---|
| 2440 | 92 | `void utilityBooksPage(VBox column)` |  |
| 2534 | 8 | `Button buildLink(String label, String category, EnumSet<BuildingType> types)` | A button that goes straight to a category of the build list. |
| 2544 | 6 | `static double sum(double[] values)` | A double[] in one figure - the education arrays are per-type. |


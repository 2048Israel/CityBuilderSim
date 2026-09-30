# PeopleScreen.java - 2,635 lines · 33 methods · 3 constants · interface

`ham/citybuildersim/ui/PeopleScreen.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> The People tab and the household screen behind it.
> 
> Who lives here by age and by household shape, the people outside the
> families (the out of work, the students, the orphans, the prisoners), the
> grid of household cells with a cell that opens into its own books, the
> money blocks both panels share, and the tier table. Split out of
> UserInterface on 2026-09-18: the six banners PEOPLE, THE PIECES THE PEOPLE
> SCREEN IS BUILT FROM, THE PEOPLE OUTSIDE THE FAMILIES, CAN THE PEOPLE OF
> THIS CITY AFFORD TO LIVE IN IT, THE MONEY BLOCKS BOTH PANELS SHARE and THE
> TIER TABLE, exactly as they were, with the shell's members reached through
> ui. The sector report the household grid opens into is still the shell's
> (showSectorReport), and it reads revealTop and revealBottom from here.

**Uses:** [Palette](Palette.md) (318), [AgeBand](AgeBand.md) (19), [PayTier](PayTier.md) (18), [HouseholdAccounts](HouseholdAccounts.md) (18), [WageBand](WageBand.md) (14), [Household](Household.md) (13), [FamilyModel](FamilyModel.md) (12), [FamilyStructure](FamilyStructure.md) (12), [CareType](CareType.md) (10), [HouseholdBalance](HouseholdBalance.md) (7), [PopulationManager](PopulationManager.md) (6), [Healthcare](Healthcare.md) (6), [JobType](JobType.md) (6), [Equity](Equity.md) (5), [Migration](Migration.md) (4), [Sickness](Sickness.md) (4), [EducationType](EducationType.md) (4), [Statement](Statement.md) (4), [LabourMarket](LabourMarket.md) (3), [UserInterface](UserInterface.md) (2), [Health](Health.md) (2), [Unemployment](Unemployment.md) (2), [UnemployedHousehold](UnemployedHousehold.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [BuildingManager](BuildingManager.md) (1), [CityCalendar](CityCalendar.md) (1), [EconomyManager](EconomyManager.md) (1), [Exchange](Exchange.md) (1), [RetiredHousehold](RetiredHousehold.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | PEOPLE. |
| 112 | · · the vitals |
| 151 | · WHO IS HERE |
| 180 | · THIS MONTH |
| 211 | · · WHO MOVED IN |
| 265 | · · AND WHO LEFT |
| 291 | · WHY THEY COME |
| 340 | · THE PYRAMID |
| 351 | · HEALTH |
| 472 | · DEATH CARE |
| 536 | · HOMES |
| 565 | · HOUSEHOLDS |
| 609 | · OUTSIDE THE FAMILIES (2026-09-11) |
| 612 | · THE LABOUR MARKET |
| 638 | · · THE SKILL LADDER, which is the explanation for everything below it. |
| 723 | · WHAT IT CANNOT DO YET |
| 758 | THE PIECES THE PEOPLE SCREEN IS BUILT FROM. |
| 842 | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) |
| 1237 | CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? |
| 1304 | · · the vitals |
| 1332 | · THE GRID |
| 1386 | · · and the cell somebody has clicked on |
| 1404 | · THE CITY'S OWN MONTH |
| 1461 | · WHAT THEY HAVE |
| 1540 | · · the order things get paid in |
| 1710 | · · the retired |
| 1727 | · · and outside the families |
| 1945 | THE MONEY BLOCKS BOTH PANELS SHARE |
| 2333 | · · the month |
| 2528 | THE TIER TABLE, AS A TABLE. |
| 2631 | · Small helpers so the report screens stay readable. Shared by the sector |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 769 | `PeopleScreen.PYRAMID_BAR` | `200` | How wide the age bars are drawn. |
| 1577 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 1578 | `PeopleScreen.SHAPE_COL` | `168` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 1235 | `boolean householdPerFamily` | Whether the per-tier table shows one family or the whole city. |
| 1268 | `String openCell` | Which cell of the grid is open, as "tier:shape". |
| 1271 | `boolean revealOpened` | Set by a click that opens a cell, so the panel it opened is scrolled to. |
| 1274 | `javafx.scene.Node revealTop` | What showSectorReport should bring into view on this draw, once. |
| 1275 | `javafx.scene.Node revealBottom` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 2602 | **type** `final class PeopleScreen` | The People tab and the household screen behind it. |
| 39 | 1 | `PeopleScreen(UserInterface ui)` |  |

### PEOPLE. (lines 41-757)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 687 | `void showPopulationInfoMenu()` |  |

### THE PIECES THE PEOPLE SCREEN IS BUILT FROM. (lines 758-841)

| line | len | member | says |
|---:|---:|---|---|
| 779 | 42 | `HBox pyramidRow(String label, double count, double share, boolean workingAge)` | One age band: name, headcount, share, and a bar. |
| 823 | 18 | `javafx.scene.layout.GridPane mixTable(double[] mix, double total)` | Who arrived, or who left, by the skill they hold. |

### THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) (lines 842-1236)

| line | len | member | says |
|---:|---:|---|---|
| 851 | 93 | `void outsideBlock(VBox column)` |  |
| 946 | 37 | `javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families, double[] noDoor)` | Everybody outside the families, by age. |
| 992 | 60 | `javafx.scene.layout.GridPane shapeMatrix(FamilyModel families)` | Household shape down the side, pay tier across the top. |
| 1058 | 24 | `String oneMarketNote(PopulationManager pm)` | The bands this month's fill joined into one market, in a sentence, or null when none were (0.7.18): workers take the best-paid post they qualify for, so bands paying the same share the shortage. |
| 1084 | 56 | `javafx.scene.layout.GridPane ladderTable(PopulationManager pm, LabourMarket market, double[] studying)` | The skill ladder: what each band has, what it can take, and what it costs. |
| 1142 | 54 | `javafx.scene.layout.GridPane professionTable(PopulationManager pm, LabourMarket market)` | The posts only a licence can fill. |
| 1198 | 28 | `javafx.scene.layout.GridPane jobTable(int[] jobs, int[] vacancies, double[] fillRates, double[] jobWage)` | Every kind of post the city has built, and how well it is staffed. |

### CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? (lines 1237-1944)

| line | len | member | says |
|---:|---:|---|---|
| 1293 | 269 | `void showHouseholdMenu()` | The residents' own books - the last participant in this economy that did not have any. |
| 1564 | 11 | `Button viewTab(String label, boolean on, Runnable go)` | One of the two view buttons over the grid. |
| 1584 | 97 | `VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | Household shape down the side, pay tier across the top, and in every cell what that household has left at the end of the month. |
| 1683 | 11 | `int gridSectionRow(javafx.scene.layout.GridPane grid, String caption, int line)` | A rule and a caption across the matrix: a different kind of household below. |
| 1705 | 53 | `int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families, int line)` | The retired, the out of work, the students, the orphans and the prison, as rows of the same matrix. |
| 1760 | 12 | `int otherRow(javafx.scene.layout.GridPane grid, String label, Household own, double perHousehold, double homes, int line)` | One matrix row for a household with no tier: a name, then one wide cell. |
| 1783 | 48 | `javafx.scene.Node wideCell(Household own, double perHousehold, double homes)` | cashCell's cell, one row wide: the same tint, the same ring, the same click - with the headcount inside it, because the six tier columns it spans have nothing of their own to say about this household. |
| 1840 | 45 | `Label cashCell(HouseholdAccounts hh, FamilyModel families, FamilyStructure shape, PayTier tier, int tierIndex)` | One cell: what this household has left, and how badly. |
| 1898 | 46 | `VBox basketBlock(Household cell)` | What a full basket would have been, what they ate, and where the difference came from. |

### THE MONEY BLOCKS BOTH PANELS SHARE (lines 1945-2527)

| line | len | member | says |
|---:|---:|---|---|
| 1964 | 4 | `String costMoney(double dollars)` | A cost, printed negative - but a cost of nothing reads "$0", never "-$0". |
| 1970 | 7 | `static String stakePct(double share)` | A stake, with enough decimals left on it to still say something. |
| 1985 | 7 | `static String shareOf(double part, double whole)` | What part is of whole - or a dash, when there is no whole to be part of. |
| 1994 | 6 | `static String monthsRun(double months)` | A run of months, said the way a person would say it. |
| 2002 | 6 | `Label panelBlockHead(String text)` | The small heading that divides a statement panel into blocks. |
| 2010 | 17 | `Label panelWho(HouseholdBalance bal, Household cell)` | How many of them there are, how big each one is, and how much of the city that is. |
| 2037 | 227 | `void positionBlock(VBox panel, HouseholdBalance bal, Household own)` | What one of these households HAS, what it OWES, what it OWNS and what all of that leaves it worth. |
| 2273 | 31 | `void ratioBlock(VBox panel, Household own, double income, double tax, String billsLabel, double bills, double fees, double shop...` | The same month again, as shares rather than figures. |
| 2314 | 38 | `VBox outsideStatement(HouseholdBalance bal)` | One of the people outside the families, and what a month does to them. |
| 2361 | 166 | `VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | The month of whichever cell is open, in full. |

### THE TIER TABLE, AS A TABLE. (lines 2528-2630)

| line | len | member | says |
|---:|---:|---|---|
| 2569 | 26 | `HBox flowBar(double tax, double rent, double fees, double shops, double left, double width)` | Where a household's month went, as one bar. |
| 2597 | 10 | `Region barPart(double width, String colour, String what)` | One segment. |
| 2609 | 21 | `HBox flowKey()` | The key under the bar, so the colours mean something the first time. |


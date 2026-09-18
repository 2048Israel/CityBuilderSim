# PeopleScreen.java - 2,528 lines · 32 methods · 3 constants · interface

`ham/citybuildersim/ui/PeopleScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (310), [AgeBand](AgeBand.md) (19), [PayTier](PayTier.md) (18), [HouseholdAccounts](HouseholdAccounts.md) (18), [Household](Household.md) (13), [FamilyModel](FamilyModel.md) (12), [FamilyStructure](FamilyStructure.md) (12), [WageBand](WageBand.md) (10), [CareType](CareType.md) (8), [HouseholdBalance](HouseholdBalance.md) (7), [Equity](Equity.md) (7), [Healthcare](Healthcare.md) (6), [JobType](JobType.md) (6), [PopulationManager](PopulationManager.md) (4), [Migration](Migration.md) (4), [Sickness](Sickness.md) (4), [EducationType](EducationType.md) (4), [Statement](Statement.md) (4), [Health](Health.md) (3), [LabourMarket](LabourMarket.md) (3), [UserInterface](UserInterface.md) (2), [Unemployment](Unemployment.md) (2), [UnemployedHousehold](UnemployedHousehold.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [BuildingManager](BuildingManager.md) (1), [CityCalendar](CityCalendar.md) (1), [EconomyManager](EconomyManager.md) (1), [Exchange](Exchange.md) (1), [RetiredHousehold](RetiredHousehold.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | PEOPLE. |
| 112 | · · the vitals |
| 151 | · WHO IS HERE |
| 180 | · THIS MONTH |
| 211 | · · WHO MOVED IN |
| 263 | · · AND WHO LEFT |
| 289 | · WHY THEY COME |
| 338 | · THE PYRAMID |
| 349 | · HEALTH |
| 453 | · DEATH CARE |
| 517 | · HOMES |
| 546 | · HOUSEHOLDS |
| 590 | · OUTSIDE THE FAMILIES (2026-09-11) |
| 593 | · THE LABOUR MARKET |
| 619 | · · THE SKILL LADDER, which is the explanation for everything below it. |
| 691 | · WHAT IT CANNOT DO YET |
| 726 | THE PIECES THE PEOPLE SCREEN IS BUILT FROM. |
| 818 | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) |
| 1194 | CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? |
| 1245 | · · the vitals |
| 1273 | · THE GRID |
| 1327 | · · and the cell somebody has clicked on |
| 1345 | · THE CITY'S OWN MONTH |
| 1397 | · WHAT THEY HAVE |
| 1476 | · · the order things get paid in |
| 1646 | · · the retired |
| 1663 | · · and outside the families |
| 1881 | THE MONEY BLOCKS BOTH PANELS SHARE |
| 2226 | · · the month |
| 2421 | THE TIER TABLE, AS A TABLE. |
| 2524 | · Small helpers so the report screens stay readable. Shared by the sector |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 737 | `PeopleScreen.PYRAMID_BAR` | `200` | How wide the age bars are drawn. |
| 1513 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 1514 | `PeopleScreen.SHAPE_COL` | `168` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 1176 | `boolean householdPerFamily` | Whether the per-tier table shows one family or the whole city. |
| 1225 | `String openCell` | Which cell of the grid is open, as "tier:shape". |
| 1228 | `boolean revealOpened` | Set by a click that opens a cell, so the panel it opened is scrolled to. |
| 1231 | `javafx.scene.Node revealTop` | What showSectorReport should bring into view on this draw, once. |
| 1232 | `javafx.scene.Node revealBottom` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 2495 | **type** `final class PeopleScreen` | The People tab and the household screen behind it. |
| 39 | 1 | `PeopleScreen(UserInterface ui)` |  |

### PEOPLE. (lines 41-725)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 655 | `void showPopulationInfoMenu()` |  |

### THE PIECES THE PEOPLE SCREEN IS BUILT FROM. (lines 726-817)

| line | len | member | says |
|---:|---:|---|---|
| 747 | 42 | `HBox pyramidRow(String label, double count, double share, boolean workingAge)` | One age band: name, headcount, share, and a bar. |
| 791 | 18 | `javafx.scene.layout.GridPane mixTable(double[] mix, double total)` | Who arrived, or who left, by the skill they hold. |

### THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) (lines 818-1193)

| line | len | member | says |
|---:|---:|---|---|
| 827 | 87 | `void outsideBlock(VBox column)` |  |
| 916 | 37 | `javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families, double[] noDoor)` | Everybody outside the families, by age. |
| 954 | 60 | `javafx.scene.layout.GridPane shapeMatrix(FamilyModel families)` |  |
| 1016 | 55 | `javafx.scene.layout.GridPane ladderTable(PopulationManager pm, LabourMarket market, double[] studying)` | The skill ladder: what each band has, what it can take, and what it costs. |
| 1073 | 54 | `javafx.scene.layout.GridPane professionTable(PopulationManager pm, LabourMarket market)` | The posts only a licence can fill. |
| 1129 | 28 | `javafx.scene.layout.GridPane jobTable(int[] jobs, int[] vacancies, double[] fillRates, double[] jobWage)` | Every kind of post the city has built, and how well it is staffed. |

### CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? (lines 1194-1880)

| line | len | member | says |
|---:|---:|---|---|
| 1234 | 264 | `void showHouseholdMenu()` |  |
| 1500 | 11 | `Button viewTab(String label, boolean on, Runnable go)` | One of the two view buttons over the grid. |
| 1520 | 97 | `VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | Household shape down the side, pay tier across the top, and in every cell what that household has left at the end of the month. |
| 1619 | 11 | `int gridSectionRow(javafx.scene.layout.GridPane grid, String caption, int line)` | A rule and a caption across the matrix: a different kind of household below. |
| 1641 | 53 | `int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families, int line)` | The retired, the out of work, the students, the orphans and the prison, as rows of the same matrix. |
| 1696 | 12 | `int otherRow(javafx.scene.layout.GridPane grid, String label, Household own, double perHousehold, double homes, int line)` | One matrix row for a household with no tier: a name, then one wide cell. |
| 1719 | 48 | `javafx.scene.Node wideCell(Household own, double perHousehold, double homes)` | cashCell's cell, one row wide: the same tint, the same ring, the same click - with the headcount inside it, because the six tier columns it spans have nothing of their own to say about this household. |
| 1776 | 45 | `Label cashCell(HouseholdAccounts hh, FamilyModel families, FamilyStructure shape, PayTier tier, int tierIndex)` | One cell: what this household has left, and how badly. |
| 1834 | 46 | `VBox basketBlock(Household cell)` | What a full basket would have been, what they ate, and where the difference came from. |

### THE MONEY BLOCKS BOTH PANELS SHARE (lines 1881-2420)

| line | len | member | says |
|---:|---:|---|---|
| 1900 | 4 | `String costMoney(double dollars)` | A cost, printed negative - but a cost of nothing reads "$0", never "-$0". |
| 1906 | 7 | `static String stakePct(double share)` | A stake, with enough decimals left on it to still say something. |
| 1921 | 7 | `static String shareOf(double part, double whole)` | What part is of whole - or a dash, when there is no whole to be part of. |
| 1930 | 6 | `static String monthsRun(double months)` | A run of months, said the way a person would say it. |
| 1938 | 6 | `Label panelBlockHead(String text)` | The small heading that divides a statement panel into blocks. |
| 1946 | 17 | `Label panelWho(HouseholdBalance bal, Household cell)` | How many of them there are, how big each one is, and how much of the city that is. |
| 1973 | 184 | `void positionBlock(VBox panel, HouseholdBalance bal, Household own)` | What one of these households HAS, what it OWES, what it OWNS and what all of that leaves it worth. |
| 2166 | 31 | `void ratioBlock(VBox panel, Household own, double income, double tax, String billsLabel, double bills, double fees, double shop...` | The same month again, as shares rather than figures. |
| 2207 | 38 | `VBox outsideStatement(HouseholdBalance bal)` | One of the people outside the families, and what a month does to them. |
| 2254 | 166 | `VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | The month of whichever cell is open, in full. |

### THE TIER TABLE, AS A TABLE. (lines 2421-2523)

| line | len | member | says |
|---:|---:|---|---|
| 2462 | 26 | `HBox flowBar(double tax, double rent, double fees, double shops, double left, double width)` | Where a household's month went, as one bar. |
| 2490 | 10 | `Region barPart(double width, String colour, String what)` | One segment. |
| 2502 | 21 | `HBox flowKey()` | The key under the bar, so the colours mean something the first time. |


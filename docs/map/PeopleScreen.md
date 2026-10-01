# PeopleScreen.java - 2,658 lines · 34 methods · 6 constants · interface

`ham/citybuildersim/ui/PeopleScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (322), [AgeBand](AgeBand.md) (19), [PayTier](PayTier.md) (18), [HouseholdAccounts](HouseholdAccounts.md) (18), [WageBand](WageBand.md) (14), [Household](Household.md) (13), [FamilyModel](FamilyModel.md) (12), [FamilyStructure](FamilyStructure.md) (12), [CareType](CareType.md) (10), [HouseholdBalance](HouseholdBalance.md) (7), [PopulationManager](PopulationManager.md) (6), [Healthcare](Healthcare.md) (6), [JobType](JobType.md) (6), [Equity](Equity.md) (5), [Migration](Migration.md) (4), [Sickness](Sickness.md) (4), [EducationType](EducationType.md) (4), [Statement](Statement.md) (4), [LabourMarket](LabourMarket.md) (3), [UserInterface](UserInterface.md) (2), [Health](Health.md) (2), [Unemployment](Unemployment.md) (2), [UnemployedHousehold](UnemployedHousehold.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [BuildingManager](BuildingManager.md) (1), [CityCalendar](CityCalendar.md) (1), [EconomyManager](EconomyManager.md) (1), [Exchange](Exchange.md) (1), [RetiredHousehold](RetiredHousehold.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | PEOPLE. |
| 136 | · · the vitals |
| 174 | · WHO IS HERE |
| 201 | · THIS MONTH |
| 232 | · · WHO MOVED IN |
| 286 | · · AND WHO LEFT |
| 312 | · WHY THEY COME |
| 361 | · THE PYRAMID |
| 372 | · HEALTH |
| 493 | · DEATH CARE |
| 557 | · HOMES |
| 586 | · HOUSEHOLDS |
| 630 | · OUTSIDE THE FAMILIES (2026-09-11) |
| 633 | · THE LABOUR MARKET |
| 659 | · · THE SKILL LADDER, which is the explanation for everything below it. |
| 744 | · WHAT IT CANNOT DO YET |
| 779 | THE PIECES THE PEOPLE SCREEN IS BUILT FROM. |
| 863 | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) |
| 1258 | CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? |
| 1325 | · · the vitals |
| 1353 | · THE GRID |
| 1407 | · · and the cell somebody has clicked on |
| 1425 | · THE CITY'S OWN MONTH |
| 1482 | · WHAT THEY HAVE |
| 1561 | · · the order things get paid in |
| 1731 | · · the retired |
| 1748 | · · and outside the families |
| 1966 | THE MONEY BLOCKS BOTH PANELS SHARE |
| 2354 | · · the month |
| 2549 | THE TIER TABLE, AS A TABLE. |
| 2654 | · Small helpers so the report screens stay readable. Shared by the sector |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `PeopleScreen.OUT_OF_WORK_SHORT` | `.03` | Under this share out of work, nobody is spare: amber, "jobs going unfilled". |
| 84 | `PeopleScreen.OUT_OF_WORK_HIGH` | `.15` | Over this, high: amber. |
| 87 | `PeopleScreen.OUT_OF_WORK_FAR` | `.25` | Over this, far too many adults with nothing to do: red. |
| 790 | `PeopleScreen.PYRAMID_BAR` | `200` | How wide the age bars are drawn. |
| 1598 | `PeopleScreen.TIER_COL` | `88` | How wide a tier column is. |
| 1599 | `PeopleScreen.SHAPE_COL` | `168` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 1256 | `boolean householdPerFamily` | Whether the per-tier table shows one family or the whole city. |
| 1289 | `String openCell` | Which cell of the grid is open, as "tier:shape". |
| 1292 | `boolean revealOpened` | Set by a click that opens a cell, so the panel it opened is scrolled to. |
| 1295 | `javafx.scene.Node revealTop` | What showSectorReport should bring into view on this draw, once. |
| 1296 | `javafx.scene.Node revealBottom` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 2625 | **type** `final class PeopleScreen` | The People tab and the household screen behind it. |
| 39 | 1 | `PeopleScreen(UserInterface ui)` |  |

### PEOPLE. (lines 41-778)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 5 | `static String outOfWorkTone(double jobless)` | The out-of-work rate's verdict: red far too high, amber high or short of hands, green between. |
| 96 | 682 | `void showPopulationInfoMenu()` |  |

### THE PIECES THE PEOPLE SCREEN IS BUILT FROM. (lines 779-862)

| line | len | member | says |
|---:|---:|---|---|
| 800 | 42 | `HBox pyramidRow(String label, double count, double share, boolean workingAge)` | One age band: name, headcount, share, and a bar. |
| 844 | 18 | `javafx.scene.layout.GridPane mixTable(double[] mix, double total)` | Who arrived, or who left, by the skill they hold. |

### THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11) (lines 863-1257)

| line | len | member | says |
|---:|---:|---|---|
| 872 | 93 | `void outsideBlock(VBox column)` |  |
| 967 | 37 | `javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families, double[] noDoor)` | Everybody outside the families, by age. |
| 1013 | 60 | `javafx.scene.layout.GridPane shapeMatrix(FamilyModel families)` | Household shape down the side, pay tier across the top. |
| 1079 | 24 | `String oneMarketNote(PopulationManager pm)` | The bands this month's fill joined into one market, in a sentence, or null when none were (0.7.18): workers take the best-paid post they qualify for, so bands paying the same share the shortage. |
| 1105 | 56 | `javafx.scene.layout.GridPane ladderTable(PopulationManager pm, LabourMarket market, double[] studying)` | The skill ladder: what each band has, what it can take, and what it costs. |
| 1163 | 54 | `javafx.scene.layout.GridPane professionTable(PopulationManager pm, LabourMarket market)` | The posts only a licence can fill. |
| 1219 | 28 | `javafx.scene.layout.GridPane jobTable(int[] jobs, int[] vacancies, double[] fillRates, double[] jobWage)` | Every kind of post the city has built, and how well it is staffed. |

### CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT? (lines 1258-1965)

| line | len | member | says |
|---:|---:|---|---|
| 1314 | 269 | `void showHouseholdMenu()` | The residents' own books - the last participant in this economy that did not have any. |
| 1585 | 11 | `Button viewTab(String label, boolean on, Runnable go)` | One of the two view buttons over the grid. |
| 1605 | 97 | `VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | Household shape down the side, pay tier across the top, and in every cell what that household has left at the end of the month. |
| 1704 | 11 | `int gridSectionRow(javafx.scene.layout.GridPane grid, String caption, int line)` | A rule and a caption across the matrix: a different kind of household below. |
| 1726 | 53 | `int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families, int line)` | The retired, the out of work, the students, the orphans and the prison, as rows of the same matrix. |
| 1781 | 12 | `int otherRow(javafx.scene.layout.GridPane grid, String label, Household own, double perHousehold, double homes, int line)` | One matrix row for a household with no tier: a name, then one wide cell. |
| 1804 | 48 | `javafx.scene.Node wideCell(Household own, double perHousehold, double homes)` | cashCell's cell, one row wide: the same tint, the same ring, the same click - with the headcount inside it, because the six tier columns it spans have nothing of their own to say about this household. |
| 1861 | 45 | `Label cashCell(HouseholdAccounts hh, FamilyModel families, FamilyStructure shape, PayTier tier, int tierIndex)` | One cell: what this household has left, and how badly. |
| 1919 | 46 | `VBox basketBlock(Household cell)` | What a full basket would have been, what they ate, and where the difference came from. |

### THE MONEY BLOCKS BOTH PANELS SHARE (lines 1966-2548)

| line | len | member | says |
|---:|---:|---|---|
| 1985 | 4 | `String costMoney(double dollars)` | A cost, printed negative - but a cost of nothing reads "$0", never "-$0". |
| 1991 | 7 | `static String stakePct(double share)` | A stake, with enough decimals left on it to still say something. |
| 2006 | 7 | `static String shareOf(double part, double whole)` | What part is of whole - or a dash, when there is no whole to be part of. |
| 2015 | 6 | `static String monthsRun(double months)` | A run of months, said the way a person would say it. |
| 2023 | 6 | `Label panelBlockHead(String text)` | The small heading that divides a statement panel into blocks. |
| 2031 | 17 | `Label panelWho(HouseholdBalance bal, Household cell)` | How many of them there are, how big each one is, and how much of the city that is. |
| 2058 | 227 | `void positionBlock(VBox panel, HouseholdBalance bal, Household own)` | What one of these households HAS, what it OWES, what it OWNS and what all of that leaves it worth. |
| 2294 | 31 | `void ratioBlock(VBox panel, Household own, double income, double tax, String billsLabel, double bills, double fees, double shop...` | The same month again, as shares rather than figures. |
| 2335 | 38 | `VBox outsideStatement(HouseholdBalance bal)` | One of the people outside the families, and what a month does to them. |
| 2382 | 166 | `VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal, FamilyModel families)` | The month of whichever cell is open, in full. |

### THE TIER TABLE, AS A TABLE. (lines 2549-2653)

| line | len | member | says |
|---:|---:|---|---|
| 2592 | 26 | `HBox flowBar(double tax, double rent, double fees, double shops, double left, double width)` | Where a household's month went, as one bar. |
| 2620 | 10 | `Region barPart(double width, String colour, String what)` | One segment. |
| 2632 | 21 | `HBox flowKey()` | The key under the bar, so the colours mean something the first time. |


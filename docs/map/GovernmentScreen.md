# GovernmentScreen.java - 1,417 lines · 37 methods · 1 constants · interface

`ham/citybuildersim/ui/GovernmentScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The government tab: the budget as two rings and a balance, what the
> treasury actually did against the size of the economy, the two lists - who
> pays what and what it spends, every revenue line opening into who paid it -
> what the debt costs by the paper it is owed on, and the output.
> 
> Split out of UserInterface on 2026-09-18: the four banners from THE
> GOVERNMENT to WHAT THE DEBT COSTS exactly as they were, the shell's members
> reached through ui. The rail keeps no place inside this tab; the one thing
> the shell touches is govPage, which the income dome sets to Overview before
> it opens the tab.

**Uses:** [Palette](Palette.md) (154), [CareType](CareType.md) (16), [EconomyManager](EconomyManager.md) (11), [NationalAccounts](NationalAccounts.md) (8), [HouseholdAccounts](HouseholdAccounts.md) (6), [EducationType](EducationType.md) (6), [TaxPolicy](TaxPolicy.md) (3), [Sector](Sector.md) (3), [UserInterface](UserInterface.md) (2), [CityCalendar](CityCalendar.md) (2), [BuildingManager](BuildingManager.md) (2), [SectorBooks](SectorBooks.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1), [Debt](Debt.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | THE GOVERNMENT. |
| 69 | · ONE SLICE OF A DONUT, AND THE DONUT. |
| 278 | THE SCREEN |
| 351 | · THE OVERVIEW |
| 365 | · · the two rings |
| 395 | · · the balance |
| 416 | · ...AND WHAT THE TREASURY ACTUALLY DID |
| 507 | · · against the size of the economy |
| 590 | THE TWO LISTS. |
| 756 | · WHO PAYS WHAT |
| 932 | · WHAT IT SPENDS |
| 977 | WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON |
| 1073 | · · and the money that is not on this statement |
| 1153 | · WHAT THE DEBT IS COSTING |
| 1202 | · · the term loans |
| 1229 | · · when the budget line disagrees with the paper |
| 1275 | · · the two services, as businesses |
| 1298 | · · the pension gap |
| 1311 | · THE OUTPUT |
| 1373 | · · and growth |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 64 | `String govPage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 1384 | **type** `final class GovernmentScreen` | The government tab: the budget as two rings and a balance, what the treasury actually did against the size of the economy, the two lists - who pays what and what it spends, every revenue line opening into who paid it ... |
| 39 | 1 | `GovernmentScreen(UserInterface ui)` |  |

### THE GOVERNMENT. (lines 41-68)

### ONE SLICE OF A DONUT, AND THE DONUT. (lines 69-277)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 23 | `java.util.List<Slice> topSlices(java.util.List<String> names, java.util.List<Double> amounts, String[] ramp)` | Slices, biggest first, with everything past the fifth folded into one. |
| 116 | 51 | `StackPane donut(java.util.List<Slice> slices, String centreTop, String centreFigure, String centreTone, double size)` | A ring, with the total in the hole. |
| 177 | 39 | `VBox donutKey(java.util.List<Slice> slices, double total, double annualGdp)` | The key beside a ring. |
| 225 | 20 | `VBox balanceBars(double revenue, double spending)` | Revenue against spending, to one scale, with the difference shown. |
| 247 | 30 | `HBox balanceBar(String label, double value, double scale, double width, String colour, double offset)` | One bar: a name, a length, and the figure on the end of it. |

### THE SCREEN (lines 278-350)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 33 | `void showGovernmentMenu()` |  |
| 317 | 4 | `String ofGdp(double monthly, double annualGdp)` | What a share of the year's output comes to, in words. |
| 322 | 28 | `HBox governmentVitals(EconomyManager em, NationalAccounts na)` |  |

### THE OVERVIEW (lines 351-589)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 205 | `void budgetOverview(VBox column, EconomyManager em, NationalAccounts na)` |  |
| 560 | 16 | `int bridgeRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double change, String tone)` | One row of the treasury bridge: a signed movement and its share of the change. |
| 578 | 11 | `int shareRow(javafx.scene.layout.GridPane table, int line, String label, double monthly, double annual, String tone)` | One row of the share table: a month, a year, and a percentage of GDP. |

### THE TWO LISTS. (lines 590-755)

| line | len | member | says |
|---:|---:|---|---|
| 603 | 5 | `static java.util.List<String> revenueNames()` |  |
| 609 | 13 | `java.util.List<Double> revenueAmounts(EconomyManager em, NationalAccounts na)` |  |
| 623 | 4 | `static java.util.List<String> spendingNames()` |  |
| 639 | 13 | `java.util.List<Double> spendingAmounts(EconomyManager em, NationalAccounts na)` | REPAIRS JOINED THIS LIST ON 2026-09-09, and it is a real line rather than a nicety. |
| 660 | 4 | `VBox budgetLine(String label, double amount, double total, double annual, String colour, VBox detail)` | A line of the budget that opens into whoever paid it. |
| 670 | 61 | `VBox budgetLine(String label, double amount, double total, double annual, String colour, VBox detail, String word)` | city pays interest. |
| 733 | 13 | `HBox budgetHead(String left)` | The heading over a budget list: what the three right-hand columns are. |
| 747 | 8 | `Label head(String text, double width)` |  |

### WHO PAYS WHAT (lines 756-931)

| line | len | member | says |
|---:|---:|---|---|
| 759 | 16 | `VBox businessTaxDetail(double total)` | Business tax, by the companies that pay it - every sector, and the bank. |
| 777 | 15 | `VBox salesTaxDetail(double total)` | Sales tax, by the sector that remitted it. |
| 794 | 16 | `VBox wageTaxDetail(double total)` | Wage tax, by the pay tier that earned the wages. |
| 812 | 13 | `VBox contributionsDetail(double total)` | Pension contributions, by the tier that paid them. |
| 827 | 16 | `VBox propertyTaxDetail(double total)` | Property tax, by the sector it is assessed on. |
| 845 | 19 | `VBox healthFeeDetail(double total)` | Healthcare fees, by the kind of care that charged them. |
| 866 | 17 | `VBox schoolFeeDetail(double total)` | School fees, by the course. |
| 884 | 34 | `void revenuePage(VBox column, EconomyManager em, NationalAccounts na)` |  |
| 919 | 12 | `VBox revenueDetail(String name, double amount)` |  |

### WHAT IT SPENDS (lines 932-976)

| line | len | member | says |
|---:|---:|---|---|
| 935 | 19 | `VBox healthSpendDetail(double total)` | Healthcare spending, by the kind of care it is spent on. |
| 956 | 20 | `VBox educationSpendDetail(double total)` | Education spending, by the school it is spent on. |

### WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON (lines 977-1310)

| line | len | member | says |
|---:|---:|---|---|
| 1008 | 2 | **type** `record PaperKind(String name, int count, double principal, double coupon, String note)` | One kind of paper, totalled. |
| 1011 | 27 | `java.util.List<PaperKind> paperKinds()` |  |
| 1040 | 6 | `PaperKind paperKind(String name)` | One kind's row, or an empty one. |
| 1053 | 48 | `VBox debtServiceDetail(double total)` | The interest line, opened into the paper it is charged on. |
| 1103 | 16 | `VBox pensionDetail(double total)` | Pensions, and who they go to. |
| 1120 | 190 | `void spendingPage(VBox column, EconomyManager em, NationalAccounts na)` |  |

### THE OUTPUT (lines 1311-1417)

| line | len | member | says |
|---:|---:|---|---|
| 1313 | 104 | `void outputPage(VBox column, EconomyManager em, NationalAccounts na)` |  |


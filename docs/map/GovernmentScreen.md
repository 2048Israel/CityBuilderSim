# GovernmentScreen.java - 1,676 lines · 41 methods · 1 constants · interface

`ham/citybuildersim/ui/GovernmentScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (179), [CareType](CareType.md) (16), [EconomyManager](EconomyManager.md) (11), [NationalAccounts](NationalAccounts.md) (9), [HouseholdAccounts](HouseholdAccounts.md) (6), [EducationType](EducationType.md) (6), [TreasuryJournal](TreasuryJournal.md) (5), [TaxPolicy](TaxPolicy.md) (3), [Sector](Sector.md) (3), [UserInterface](UserInterface.md) (2), [TreasuryLine](TreasuryLine.md) (2), [CityCalendar](CityCalendar.md) (2), [BuildingManager](BuildingManager.md) (2), [CentralBank](CentralBank.md) (1), [SectorBooks](SectorBooks.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [Mortgage](Mortgage.md) (1), [Debt](Debt.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

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
| 549 | · ...AND WHAT IT OWES ITS CENTRAL BANK (0.7.0) |
| 599 | · · against the size of the economy |
| 755 | THE TWO LISTS. |
| 947 | · WHO PAYS WHAT |
| 1160 | · WHAT IT SPENDS |
| 1205 | WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON |
| 1301 | · · and the money that is not on this statement |
| 1412 | · WHAT THE DEBT IS COSTING |
| 1461 | · · the term loans |
| 1488 | · · when the budget line disagrees with the paper |
| 1534 | · · the two services, as businesses |
| 1557 | · · the pension gap |
| 1570 | · THE OUTPUT |
| 1632 | · · and growth |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `GovernmentScreen.GOV_PAGES` | `{ "Overview", "Revenue", "Spending", "Output" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 64 | `String govPage` |  |
| 682 | `private boolean bridgeOpen` | Whether the bridge's last row is standing open. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 1643 | **type** `final class GovernmentScreen` | The government tab: the budget as two rings and a balance, what the treasury actually did against the size of the economy, the two lists - who pays what and what it spends, every revenue line opening into who paid it ... |
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

### THE OVERVIEW (lines 351-754)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 297 | `void budgetOverview(VBox column, EconomyManager em, NationalAccounts na)` |  |
| 652 | 5 | `int bridgeRow(javafx.scene.layout.GridPane table, int line, String label, double amount, double change, String tone)` | One row of the treasury bridge: a signed movement and its share of the change. |
| 659 | 16 | `int bridgeRow(javafx.scene.layout.GridPane table, int line, javafx.scene.Node labelCell, double amount, double change, String t...` | The same, with the label cell already made - the disclosure puts a mark in it. |
| 697 | 44 | `int bridgeDisclosure(javafx.scene.layout.GridPane table, int line, String label, double amount, double change, String tone, jav...` | The bridge's last row as a disclosure: closed, the row as it always was; opened, the journal's lines under it in the same three columns, and a last line "Not accounted for" carrying the residual. |
| 743 | 11 | `int shareRow(javafx.scene.layout.GridPane table, int line, String label, double monthly, double annual, String tone)` | One row of the share table: a month, a year, and a percentage of GDP. |

### THE TWO LISTS. (lines 755-946)

| line | len | member | says |
|---:|---:|---|---|
| 768 | 6 | `static java.util.List<String> revenueNames()` |  |
| 775 | 26 | `java.util.List<Double> revenueAmounts(EconomyManager em, NationalAccounts na)` |  |
| 802 | 5 | `static java.util.List<String> spendingNames()` |  |
| 819 | 24 | `java.util.List<Double> spendingAmounts(EconomyManager em, NationalAccounts na)` | REPAIRS JOINED THIS LIST ON 2026-09-09, and it is a real line rather than a nicety. |
| 851 | 4 | `VBox budgetLine(String label, double amount, double total, double annual, String colour, VBox detail)` | A line of the budget that opens into whoever paid it. |
| 861 | 61 | `VBox budgetLine(String label, double amount, double total, double annual, String colour, VBox detail, String word)` | city pays interest. |
| 924 | 13 | `HBox budgetHead(String left)` | The heading over a budget list: what the three right-hand columns are. |
| 938 | 8 | `Label head(String text, double width)` |  |

### WHO PAYS WHAT (lines 947-1159)

| line | len | member | says |
|---:|---:|---|---|
| 950 | 19 | `VBox businessTaxDetail(double total)` | Business tax, by the companies that pay it - every sector, and the bank. |
| 971 | 15 | `VBox salesTaxDetail(double total)` | Sales tax, by the sector that remitted it. |
| 988 | 16 | `VBox wageTaxDetail(double total)` | Wage tax, by the pay tier that earned the wages. |
| 1006 | 13 | `VBox contributionsDetail(double total)` | Pension contributions, by the tier that paid them. |
| 1021 | 16 | `VBox propertyTaxDetail(double total)` | Property tax, by the sector it is assessed on. |
| 1039 | 19 | `VBox healthFeeDetail(double total)` | Healthcare fees, by the kind of care that charged them. |
| 1060 | 17 | `VBox schoolFeeDetail(double total)` | School fees, by the course. |
| 1078 | 34 | `void revenuePage(VBox column, EconomyManager em, NationalAccounts na)` |  |
| 1113 | 13 | `VBox revenueDetail(String name, double amount)` |  |
| 1133 | 26 | `VBox mortgageInsuranceDetail()` | THE CITY'S MORTGAGE INSURANCE (0.7.11), opened from either of its two lines: the premiums taken this month and what they were on, what the insurance paid the bank, and the book over the city's life - does the city mak... |

### WHAT IT SPENDS (lines 1160-1204)

| line | len | member | says |
|---:|---:|---|---|
| 1163 | 19 | `VBox healthSpendDetail(double total)` | Healthcare spending, by the kind of care it is spent on. |
| 1184 | 20 | `VBox educationSpendDetail(double total)` | Education spending, by the school it is spent on. |

### WHAT THE DEBT COSTS, BY THE PAPER IT IS OWED ON (lines 1205-1569)

| line | len | member | says |
|---:|---:|---|---|
| 1236 | 2 | **type** `record PaperKind(String name, int count, double principal, double coupon, String note)` | One kind of paper, totalled. |
| 1239 | 27 | `java.util.List<PaperKind> paperKinds()` |  |
| 1268 | 6 | `PaperKind paperKind(String name)` | One kind's row, or an empty one. |
| 1281 | 48 | `VBox debtServiceDetail(double total)` | The interest line, opened into the paper it is charged on. |
| 1331 | 16 | `VBox pensionDetail(double total)` | Pensions, and who they go to. |
| 1356 | 20 | `VBox landSpendDetail(double total)` | The land line, opened (0.7.6): the land office is paid in US dollars, and the budget carries it at what it cost in local money on the day - paid by converting cash, or out of the vault with no cash moving at all (the ... |
| 1377 | 192 | `void spendingPage(VBox column, EconomyManager em, NationalAccounts na)` |  |

### THE OUTPUT (lines 1570-1676)

| line | len | member | says |
|---:|---:|---|---|
| 1572 | 104 | `void outputPage(VBox column, EconomyManager em, NationalAccounts na)` |  |


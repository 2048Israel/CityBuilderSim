# BankScreen.java - 1,624 lines · 23 methods · 7 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The bank tab: the gauge, the two limits, another branch, who owes it, where
> the money comes from, the books, the rescue, and its history.
> 
> Split out of UserInterface on 2026-09-18: the ten banners from THE BANK to
> ITS HISTORY exactly as they were, the shell's members reached through ui. The
> shell still reads which page and area are open (bankPage, bankArea) for the
> rail and the scroll memory.

**Uses:** [Palette](Palette.md) (269), [Bank](Bank.md) (57), [Equity](Equity.md) (4), [HistorySave](HistorySave.md) (4), [UserInterface](UserInterface.md) (2), [DebtManager](DebtManager.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [Sectors](Sectors.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Exchange](Exchange.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 37 | THE BANK |
| 117 | · · there is no bank |
| 266 | ONE SUBJECT, ITS OWN STRIP |
| 330 | THE GAUGE |
| 371 | · · and in words |
| 390 | · · what it is measured on |
| 412 | · · who pays for it |
| 448 | · · the bands |
| 469 | · · and the reading |
| 485 | · · the mark |
| 496 | · · the ticks |
| 514 | · · the middle |
| 586 | THE TWO LIMITS |
| 630 | · · what a dollar weighs |
| 659 | · · the ratio |
| 765 | ANOTHER BRANCH |
| 811 | · · the verdict |
| 852 | WHO OWES IT |
| 899 | · · the businesses, split |
| 933 | · · and the families |
| 954 | · (untitled) |
| 975 | · · by borrower |
| 1021 | · · and if it failed |
| 1036 | WHERE THE MONEY COMES FROM |
| 1062 | · · reached, and out of reach |
| 1097 | · · whose |
| 1117 | · · hot money |
| 1132 | · (untitled) |
| 1179 | · · and whose money |
| 1196 | THE BOOKS |
| 1207 | · · interest, opened by borrower |
| 1290 | · · the margin |
| 1311 | · (untitled) |
| 1350 | · · and how equity moved |
| 1409 | · AND IF IT FAILED |
| 1424 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 1496 | ITS HISTORY |
| 1579 | · · how long it has hurt |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `BankScreen.BANK_HOME` | `"The gauge"` | null is the landing |
| 70 | `BankScreen.BANK_LEND_PAGES` | `{ "The gauge", "The two limits", "Another branch" }` |  |
| 72 | `BankScreen.BANK_OWED_PAGES` | `{ "By borrower", "In trouble" }` |  |
| 73 | `BankScreen.BANK_MONEY_PAGES` | `{ "Deposits", "Funding" }` |  |
| 74 | `BankScreen.BANK_BOOKS_PAGES` | `{ "Income", "Balance sheet" }` |  |
| 75 | `BankScreen.BANK_PAST_PAGES` | `{ "Lending", "Strain", "Capital" }` |  |
| 346 | `BankScreen.GAUGE_MAX` | `2.0` | The top of the gauge's scale, as a multiple of capacity. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 66 | `String bankArea` | null is the landing |
| 68 | `String bankPage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 1595 | **type** `final class BankScreen` | The bank tab: the gauge, the two limits, another branch, who owes it, where the money comes from, the books, the rescue, and its history. |
| 35 | 1 | `BankScreen(UserInterface ui)` |  |

### THE BANK (lines 37-265)

| line | len | member | says |
|---:|---:|---|---|
| 94 | 92 | `void showBankMenu()` | THE BANK. |
| 188 | 42 | `HBox bankRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the bank's landing page. |
| 232 | 33 | `HBox bankVitals()` | The four figures that are true of the whole tab. |

### ONE SUBJECT, ITS OWN STRIP (lines 266-329)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 59 | `void drawBankScreen()` |  |

### THE GAUGE (lines 330-585)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 78 | `void bankGaugePage(VBox column)` |  |
| 437 | 99 | `StackPane strainGauge(double strain, double premium, double size)` | An arc with the bands on it, the needle where the bank is, and the premium in the hole. |
| 538 | 47 | `VBox gaugeKey(Bank bank)` | The three bands, said in words beside the gauge. |

### THE TWO LIMITS (lines 586-764)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 85 | `void bankLimitsPage(VBox column)` |  |
| 681 | 11 | `int weightRow(javafx.scene.layout.GridPane table, int line, String label, double face, double weight)` |  |
| 706 | 58 | `VBox limitBar(String name, String how, double limit, double book, boolean binds)` | One of the two limits, with the book drawn into it. |

### ANOTHER BRANCH (lines 765-851)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 76 | `void bankBranchPage(VBox column)` |  |

### WHO OWES IT (lines 852-953)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 97 | `void bankBorrowerPage(VBox column)` |  |

### (untitled) (lines 954-1035)

| line | len | member | says |
|---:|---:|---|---|
| 956 | 79 | `void bankTroublePage(VBox column)` |  |

### WHERE THE MONEY COMES FROM (lines 1036-1131)

| line | len | member | says |
|---:|---:|---|---|
| 1045 | 86 | `void bankDepositPage(VBox column)` |  |

### (untitled) (lines 1132-1195)

| line | len | member | says |
|---:|---:|---|---|
| 1134 | 61 | `void bankFundingPage(VBox column)` |  |

### THE BOOKS (lines 1196-1310)

| line | len | member | says |
|---:|---:|---|---|
| 1200 | 110 | `void bankIncomePage(VBox column)` |  |

### (untitled) (lines 1311-1423)

| line | len | member | says |
|---:|---:|---|---|
| 1313 | 110 | `void bankBalancePage(VBox column)` |  |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 1424-1495)

| line | len | member | says |
|---:|---:|---|---|
| 1444 | 51 | `VBox bankRescue()` |  |

### ITS HISTORY (lines 1496-1624)

| line | len | member | says |
|---:|---:|---|---|
| 1512 | 19 | `void bankHistoryPage(VBox column, String page)` |  |
| 1532 | 23 | `void bankLendingHistory(VBox column, HistorySave h)` |  |
| 1556 | 37 | `void bankStrainHistory(VBox column, HistorySave h)` |  |
| 1594 | 30 | `void bankCapitalHistory(VBox column, HistorySave h)` |  |


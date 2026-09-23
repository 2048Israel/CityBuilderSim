# BankScreen.java - 1,670 lines · 23 methods · 7 constants · interface

`ham/citybuildersim/ui/BankScreen.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The bank tab: the gauge, the two limits, another branch, who owes it, where
> the money comes from, the books, the rescue, and its history.
> 
> Split out of UserInterface on 2026-09-18: the ten banners from THE BANK to
> ITS HISTORY exactly as they were, the shell's members reached through ui. The
> shell still reads which page and area are open (bankPage, bankArea) for the
> rail and the scroll memory.

**Uses:** [Palette](Palette.md) (274), [Bank](Bank.md) (55), [Equity](Equity.md) (4), [HistorySave](HistorySave.md) (4), [UserInterface](UserInterface.md) (2), [DebtManager](DebtManager.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [Sectors](Sectors.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [CentralBank](CentralBank.md) (1), [Exchange](Exchange.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 37 | THE BANK |
| 118 | · · there is no bank |
| 267 | ONE SUBJECT, ITS OWN STRIP |
| 331 | THE GAUGE |
| 372 | · · and in words |
| 391 | · · what it is measured on |
| 413 | · · who pays for it |
| 449 | · · the bands |
| 470 | · · and the reading |
| 486 | · · the mark |
| 497 | · · the ticks |
| 515 | · · the middle |
| 587 | THE TWO LIMITS |
| 631 | · · what a dollar weighs |
| 660 | · · the ratio |
| 766 | ANOTHER BRANCH |
| 812 | · · the verdict |
| 853 | WHO OWES IT |
| 900 | · · the businesses, split |
| 934 | · · and the families |
| 955 | · (untitled) |
| 976 | · · by borrower |
| 1022 | · · and if it failed |
| 1042 | WHERE THE MONEY COMES FROM |
| 1068 | · · reached, and out of reach |
| 1103 | · · whose |
| 1123 | · · hot money |
| 1138 | · (untitled) |
| 1194 | THE BOOKS |
| 1205 | · · interest, opened by borrower |
| 1325 | · · the margin |
| 1351 | · (untitled) |
| 1396 | · · and how equity moved |
| 1455 | · AND IF IT FAILED |
| 1470 | THE RESCUE, WHEREVER THE PLAYER IS LOOKING. |
| 1542 | ITS HISTORY |
| 1625 | · · how long it has hurt |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `BankScreen.BANK_HOME` | `"The gauge"` | null is the landing |
| 71 | `BankScreen.BANK_LEND_PAGES` | `{ "The gauge", "The two limits", "Another branch" }` |  |
| 73 | `BankScreen.BANK_OWED_PAGES` | `{ "By borrower", "In trouble" }` |  |
| 74 | `BankScreen.BANK_MONEY_PAGES` | `{ "Deposits", "Funding" }` |  |
| 75 | `BankScreen.BANK_BOOKS_PAGES` | `{ "Income", "Balance sheet" }` |  |
| 76 | `BankScreen.BANK_PAST_PAGES` | `{ "Lending", "Strain", "Capital" }` |  |
| 347 | `BankScreen.GAUGE_MAX` | `2.0` | The top of the gauge's scale, as a multiple of capacity. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 67 | `String bankArea` | null is the landing |
| 69 | `String bankPage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 1641 | **type** `final class BankScreen` | The bank tab: the gauge, the two limits, another branch, who owes it, where the money comes from, the books, the rescue, and its history. |
| 35 | 1 | `BankScreen(UserInterface ui)` |  |

### THE BANK (lines 37-266)

| line | len | member | says |
|---:|---:|---|---|
| 95 | 92 | `void showBankMenu()` | THE BANK. |
| 189 | 42 | `HBox bankRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` | One subject on the bank's landing page. |
| 233 | 33 | `HBox bankVitals()` | The four figures that are true of the whole tab. |

### ONE SUBJECT, ITS OWN STRIP (lines 267-330)

| line | len | member | says |
|---:|---:|---|---|
| 271 | 59 | `void drawBankScreen()` |  |

### THE GAUGE (lines 331-586)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 78 | `void bankGaugePage(VBox column)` |  |
| 438 | 99 | `StackPane strainGauge(double strain, double premium, double size)` | An arc with the bands on it, the needle where the bank is, and the premium in the hole. |
| 539 | 47 | `VBox gaugeKey(Bank bank)` | The three bands, said in words beside the gauge. |

### THE TWO LIMITS (lines 587-765)

| line | len | member | says |
|---:|---:|---|---|
| 596 | 85 | `void bankLimitsPage(VBox column)` |  |
| 682 | 11 | `int weightRow(javafx.scene.layout.GridPane table, int line, String label, double face, double weight)` |  |
| 707 | 58 | `VBox limitBar(String name, String how, double limit, double book, boolean binds)` | One of the two limits, with the book drawn into it. |

### ANOTHER BRANCH (lines 766-852)

| line | len | member | says |
|---:|---:|---|---|
| 776 | 76 | `void bankBranchPage(VBox column)` |  |

### WHO OWES IT (lines 853-954)

| line | len | member | says |
|---:|---:|---|---|
| 857 | 97 | `void bankBorrowerPage(VBox column)` |  |

### (untitled) (lines 955-1041)

| line | len | member | says |
|---:|---:|---|---|
| 957 | 84 | `void bankTroublePage(VBox column)` |  |

### WHERE THE MONEY COMES FROM (lines 1042-1137)

| line | len | member | says |
|---:|---:|---|---|
| 1051 | 86 | `void bankDepositPage(VBox column)` |  |

### (untitled) (lines 1138-1193)

| line | len | member | says |
|---:|---:|---|---|
| 1140 | 53 | `void bankFundingPage(VBox column)` |  |

### THE BOOKS (lines 1194-1350)

| line | len | member | says |
|---:|---:|---|---|
| 1198 | 152 | `void bankIncomePage(VBox column)` |  |

### (untitled) (lines 1351-1469)

| line | len | member | says |
|---:|---:|---|---|
| 1353 | 116 | `void bankBalancePage(VBox column)` |  |

### THE RESCUE, WHEREVER THE PLAYER IS LOOKING. (lines 1470-1541)

| line | len | member | says |
|---:|---:|---|---|
| 1490 | 51 | `VBox bankRescue()` |  |

### ITS HISTORY (lines 1542-1670)

| line | len | member | says |
|---:|---:|---|---|
| 1558 | 19 | `void bankHistoryPage(VBox column, String page)` |  |
| 1578 | 23 | `void bankLendingHistory(VBox column, HistorySave h)` |  |
| 1602 | 37 | `void bankStrainHistory(VBox column, HistorySave h)` |  |
| 1640 | 30 | `void bankCapitalHistory(VBox column, HistorySave h)` |  |


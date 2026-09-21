# PolicyScreen.java - 2,899 lines · 60 methods · 8 constants · interface

`ham/citybuildersim/ui/PolicyScreen.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The policy tab: the four rows of levers - taxes, wages, money, promises -
> the staged set every dial writes into, the ladder and the batch preview that
> show what a proposal would cost before it is applied, and the pages each
> lever opens.
> 
> Split out of UserInterface on 2026-09-18: the nineteen banners from THE
> POLICY TAB to PROMISES - the standing subsidies, the shell's members reached
> through ui. Eighteen of them moved exactly as they were; the nineteenth, the
> clinic's price and a premium, was added on 2026-09-19. The shell still reads
> which area and page are open (policyArea, policyPage) for the rail and the
> scroll memory, and the staged set (staged, isStaged, taxRaised, applyBar,
> stageSlider, dropProposal, pinnedBands) because other screens offer the same
> levers.

**Uses:** [Palette](Palette.md) (380), [TaxPolicy](TaxPolicy.md) (76), [WageBand](WageBand.md) (24), [Sector](Sector.md) (16), [DebtManager](DebtManager.md) (16), [EconomyManager](EconomyManager.md) (15), [JobType](JobType.md) (11), [Money](Money.md) (11), [CareType](CareType.md) (10), [LabourMarket](LabourMarket.md) (8), [Education](Education.md) (8), [Denomination](Denomination.md) (8), [SectorBooks](SectorBooks.md) (7), [NationalAccounts](NationalAccounts.md) (6), [EducationType](EducationType.md) (6), [Currency](Currency.md) (5), [Sectors](Sectors.md) (4), [FamilyModel](FamilyModel.md) (4), [SalesTaxLedger](SalesTaxLedger.md) (3), [PopulationManager](PopulationManager.md) (3), [PayTier](PayTier.md) (3), [UserInterface](UserInterface.md) (2), [PriceIndex](PriceIndex.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [Healthcare](Healthcare.md) (2), [Bank](Bank.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Game](Game.md) (1), [WorldEconomy](WorldEconomy.md) (1)... and 5 more

**Used by (2):** [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 40 | THE POLICY TAB |
| 99 | THE STAGED SET. |
| 151 | THE LADDER |
| 289 | WHAT THE WHOLE BATCH WOULD DO |
| 432 | THE FOUR TAXES |
| 459 | · EVERYTHING |
| 573 | · PROFIT |
| 641 | · SALES |
| 711 | · WAGE |
| 750 | · · what a payslip actually loses |
| 826 | · PROPERTY |
| 919 | THE LANDING |
| 977 | · WHAT IS ACTUALLY BITING |
| 1070 | · · the wage floor |
| 1081 | · · who is shut out |
| 1092 | · · tuition |
| 1116 | · · pensions |
| 1125 | · · subsidies |
| 1139 | · · the price of money |
| 1151 | · · the money itself |
| 1159 | · · at the stops |
| 1231 | ONE SUBJECT, ITS OWN STRIP |
| 1315 | THE PIECES A LEVER IS MADE OF |
| 1421 | TAXES - the two rates |
| 1497 | · the arithmetic behind it |
| 1584 | TAXES - by wage band |
| 1636 | TAXES - by sector |
| 1640 | WAGES - the floor |
| 1673 | · · the ladder |
| 1700 | · · who is pinned |
| 1725 | · · and the city's own bill |
| 1769 | MONEY - the policy rate |
| 1855 | · · prices, in words |
| 1861 | · · where it has been |
| 1951 | MONEY - the currency reform |
| 2078 | PROMISES - the pension |
| 2129 | · · and what it does to people |
| 2162 | · · what workers pay |
| 2194 | · · what seniors receive |
| 2235 | PROMISES - the schools: the price of a place, who pays it, and the |
| 2309 | · · the five levers, registered for the one bar |
| 2325 | · · what a family has to find |
| 2368 | · · the schools this month |
| 2383 | · · the price of a place |
| 2426 | · · the grant |
| 2506 | · · the loan |
| 2553 | PROMISES - the out of work and the students (2026-09-11) |
| 2654 | PROMISES - the clinic's price, and a premium (2026-09-19) |
| 2827 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `PolicyScreen.POLICY_HOME` | `"Everything"` | null is the landing |
| 93 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 95 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 96 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 97 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Health", "Schools", "Subsidies" }` |  |
| 170 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 173 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 175 | `PolicyScreen.LADDER_READ` | `118` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 75 | `String policyArea` | null is the landing |
| 77 | `String policyPage` |  |
| 112 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 124 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 2867 | **type** `final class PolicyScreen` | The policy tab: the four rows of levers - taxes, wages, money, promises - the staged set every dial writes into, the ladder and the batch preview that show what a proposal would cost before it is applied, and the page... |
| 38 | 1 | `PolicyScreen(UserInterface ui)` |  |

### THE POLICY TAB (lines 40-98)

### THE STAGED SET. (lines 99-150)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 133 | 1 | `boolean isStaged(String key)` |  |
| 135 | 4 | `double staged(String key, double current)` |  |
| 140 | 1 | `void stage(String key, double value)` |  |
| 142 | 1 | `void unstage(String key)` |  |
| 144 | 1 | `void dropProposal()` |  |
| 147 | 3 | `static double clampRate(double value, double max)` | TaxPolicy.clamp(), which is private there and needed here to preview it. |

### THE LADDER (lines 151-288)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 63 | `VBox taxLadder(Lever lever)` |  |
| 250 | 14 | `Button stepButton(String glyph, boolean live, Runnable go)` | One notch, in the units the dial is read in. |
| 265 | 4 | `static String stepWord(double step)` |  |
| 270 | 3 | `static double bounded(double value, Lever lever)` |  |
| 281 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |

### WHAT THE WHOLE BATCH WOULD DO (lines 289-431)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 46 | `double taxTotalUnder(boolean proposed)` |  |
| 352 | 9 | `double assessedUnder(EconomyManager em, TaxPolicy p, Sector s, boolean proposed)` | What a sector is on the roll for, with a staged farmland relief applied. |
| 363 | 1 | `VBox stagedBar()` | The foot bar: everything pending, what it adds up to, and one button. |
| 371 | 50 | `VBox stagedBar(boolean taxes)` | The same bar with or without the tax total: the Schools page's dials (2026-09-21) are not taxes, and a bar that footed them to "Tax a month: no difference" would be answering a question nobody asked. |
| 423 | 8 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### THE FOUR TAXES (lines 432-458)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 12 | `void cityRateLever(VBox column, TaxPolicy policy, String which)` | Profit, sales and wage all move off the income rate, so it is on all three. |

### EVERYTHING (lines 459-572)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 65 | `void taxOverviewPage(VBox column)` |  |
| 528 | 44 | `HBox taxSourceRow(String name, double raised, double all, String rate, String what, String page)` | One tax on the overview: what it raised, its share, its rate, and a door. |

### PROFIT (lines 573-640)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 65 | `void profitTaxPage(VBox column)` |  |

### SALES (lines 641-710)

| line | len | member | says |
|---:|---:|---|---|
| 643 | 67 | `void salesTaxPage(VBox column)` |  |

### WAGE (lines 711-825)

| line | len | member | says |
|---:|---:|---|---|
| 713 | 61 | `void wageTaxPage(VBox column)` |  |
| 788 | 37 | `void payrollBurden(VBox column, TaxPolicy policy, double base)` | The whole charge on a payslip, which no screen has ever totalled. |

### PROPERTY (lines 826-918)

| line | len | member | says |
|---:|---:|---|---|
| 828 | 90 | `void propertyTaxPage(VBox column)` |  |

### THE LANDING (lines 919-1230)

| line | len | member | says |
|---:|---:|---|---|
| 923 | 77 | `void showPolicyMenu()` |  |
| 1002 | 5 | `double taxRaised()` | Every tax line the city collected last month. |
| 1009 | 7 | `double promisesCost()` | What the three promises cost the treasury in a month, net of what they collect. |
| 1017 | 9 | `int pinnedBands()` |  |
| 1027 | 23 | `HBox policyVitals()` |  |
| 1058 | 110 | `java.util.List<String[]> policyFlags()` | The levers that are currently forcing something. |
| 1169 | 17 | `VBox flagLine(String tone, String heading, String body)` |  |
| 1187 | 43 | `HBox policyRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 1231-1314)

| line | len | member | says |
|---:|---:|---|---|
| 1235 | 79 | `void drawPolicyScreen()` |  |

### THE PIECES A LEVER IS MADE OF (lines 1315-1420)

| line | len | member | says |
|---:|---:|---|---|
| 1350 | 48 | `VBox stageSlider(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> label)` | The lever itself: a slider that STAGES a change rather than making one. |
| 1400 | 20 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back. |

### TAXES - the two rates (lines 1421-1496)

| line | len | member | says |
|---:|---:|---|---|
| 1440 | 56 | `void farmlandRelief(javafx.scene.layout.VBox column, Game game, TaxPolicy policy, EconomyManager em)` | The one dial that decides whether the city keeps its fields. |

### the arithmetic behind it (lines 1497-1583)

| line | len | member | says |
|---:|---:|---|---|
| 1499 | 3 | `static double scaled(double amount, double factor)` |  |
| 1504 | 15 | `double profitFactor(TaxPolicy policy, double from, double to)` | How the profit tax moves with the base rate, weighted by what each sector paid. |
| 1521 | 13 | `double salesFactor(TaxPolicy policy, double from, double to)` | ...and the sales tax, weighted by what each sector actually sold. |
| 1543 | 3 | `double wageTaxAtBase(TaxPolicy policy, double base)` | The wage tax, recomputed rather than scaled. |
| 1553 | 3 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset)` |  |
| 1557 | 16 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset, WageBand onlyBand)` |  |
| 1575 | 8 | `double payrollIn(WageBand band)` | The payroll a band carries, which is what its rate is charged on. |

### TAXES - by wage band (lines 1584-1635)

| line | len | member | says |
|---:|---:|---|---|
| 1604 | 31 | `VBox bandLever(String name, String offset, String effective, String note, double signedOffset)` | The heading row of one band or one sector's line: what it is, and where it sits. |

### TAXES - by sector (lines 1636-1639)

### WAGES - the floor (lines 1640-1768)

| line | len | member | says |
|---:|---:|---|---|
| 1656 | 103 | `void minimumWagePage(VBox column)` |  |
| 1761 | 7 | `double bestWageIn(LabourMarket market, WageBand band)` | The best-paid job somebody in this band can hold. |

### MONEY - the policy rate (lines 1769-1950)

| line | len | member | says |
|---:|---:|---|---|
| 1782 | 168 | `void policyRatePage(VBox column)` |  |

### MONEY - the currency reform (lines 1951-2077)

| line | len | member | says |
|---:|---:|---|---|
| 1976 | 93 | `void currencyReformPage(VBox column)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 2071 | 6 | `static String afterName(Denomination unit, double factor)` | What the money would be called after lopping by this factor. |

### PROMISES - the pension (lines 2078-2234)

| line | len | member | says |
|---:|---:|---|---|
| 2093 | 141 | `void pensionPage(VBox column)` |  |

### PROMISES - the schools: the price of a place, who pays it, and the (lines 2235-2552)

| line | len | member | says |
|---:|---:|---|---|
| 2268 | 9 | `static String grantWords(TaxPolicy.GrantBasis basis, double amount)` | The grant in words, for a line that names it: "15% of an unskilled wage a month". |
| 2279 | 8 | `static String basisName(TaxPolicy.GrantBasis basis)` | What a basis is called on its chip. |
| 2289 | 4 | `static String amountWords(TaxPolicy.GrantBasis basis, double amount)` | How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. |
| 2294 | 252 | `void schoolsPage(VBox column)` |  |
| 2548 | 4 | `Lever register(Lever lever)` | A dial on this page, registered so the foot bar can name and apply it. |

### PROMISES - the out of work and the students (2026-09-11) (lines 2553-2653)

| line | len | member | says |
|---:|---:|---|---|
| 2565 | 83 | `void outOfWorkPage(VBox column)` |  |
| 2649 | 4 | `static String burdenTone(double share)` |  |

### PROMISES - the clinic's price, and a premium (2026-09-19) (lines 2654-2826)

| line | len | member | says |
|---:|---:|---|---|
| 2673 | 153 | `void healthPage(VBox column)` |  |

### PROMISES - the standing subsidies (lines 2827-2899)

| line | len | member | says |
|---:|---:|---|---|
| 2836 | 63 | `void subsidyPage(VBox column)` |  |


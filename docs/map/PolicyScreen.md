# PolicyScreen.java - 2,519 lines · 54 methods · 8 constants · interface

`ham/citybuildersim/ui/PolicyScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The policy tab: the four rows of levers - taxes, wages, money, promises -
> the staged set every dial writes into, the ladder and the batch preview that
> show what a proposal would cost before it is applied, and the pages each
> lever opens.
> 
> Split out of UserInterface on 2026-09-18: the eighteen banners from THE
> POLICY TAB to PROMISES - the standing subsidies exactly as they were, the
> shell's members reached through ui. The shell still reads which area and
> page are open (policyArea, policyPage) for the rail and the scroll memory,
> and the staged set (staged, isStaged, taxRaised, applyBar, stageSlider,
> dropProposal, pinnedBands) because other screens offer the same levers.

**Uses:** [Palette](Palette.md) (342), [TaxPolicy](TaxPolicy.md) (52), [WageBand](WageBand.md) (24), [Sector](Sector.md) (16), [DebtManager](DebtManager.md) (16), [EconomyManager](EconomyManager.md) (13), [JobType](JobType.md) (11), [Money](Money.md) (8), [LabourMarket](LabourMarket.md) (8), [Education](Education.md) (8), [Denomination](Denomination.md) (8), [SectorBooks](SectorBooks.md) (7), [NationalAccounts](NationalAccounts.md) (6), [EducationType](EducationType.md) (6), [Currency](Currency.md) (5), [Sectors](Sectors.md) (4), [SalesTaxLedger](SalesTaxLedger.md) (3), [PopulationManager](PopulationManager.md) (3), [PayTier](PayTier.md) (3), [UserInterface](UserInterface.md) (2), [PriceIndex](PriceIndex.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [Bank](Bank.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [FamilyModel](FamilyModel.md) (2), [Game](Game.md) (1), [Healthcare](Healthcare.md) (1), [WorldEconomy](WorldEconomy.md) (1), [Retail](Retail.md) (1), [RealEstate](RealEstate.md) (1)... and 4 more

**Used by (2):** [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 38 | THE POLICY TAB |
| 97 | THE STAGED SET. |
| 149 | THE LADDER |
| 287 | WHAT THE WHOLE BATCH WOULD DO |
| 420 | THE FOUR TAXES |
| 447 | · EVERYTHING |
| 561 | · PROFIT |
| 629 | · SALES |
| 699 | · WAGE |
| 738 | · · what a payslip actually loses |
| 810 | · PROPERTY |
| 903 | THE LANDING |
| 961 | · WHAT IS ACTUALLY BITING |
| 1054 | · · the wage floor |
| 1065 | · · who is shut out |
| 1076 | · · tuition |
| 1100 | · · pensions |
| 1109 | · · subsidies |
| 1123 | · · the price of money |
| 1135 | · · the money itself |
| 1143 | · · at the stops |
| 1215 | ONE SUBJECT, ITS OWN STRIP |
| 1294 | THE PIECES A LEVER IS MADE OF |
| 1397 | TAXES - the two rates |
| 1473 | · the arithmetic behind it |
| 1560 | TAXES - by wage band |
| 1612 | TAXES - by sector |
| 1616 | WAGES - the floor |
| 1649 | · · the ladder |
| 1676 | · · who is pinned |
| 1701 | · · and the city's own bill |
| 1745 | MONEY - the policy rate |
| 1831 | · · prices, in words |
| 1837 | · · where it has been |
| 1927 | MONEY - the currency reform |
| 2054 | PROMISES - the pension |
| 2105 | · · and what it does to people |
| 2138 | · · what workers pay |
| 2170 | · · what seniors receive |
| 2211 | PROMISES - tuition |
| 2242 | · · what a family has to find |
| 2280 | · · the schools this month |
| 2325 | PROMISES - the out of work and the students (2026-09-11) |
| 2447 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `PolicyScreen.POLICY_HOME` | `"Everything"` | null is the landing |
| 91 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 93 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 94 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 95 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Tuition", "Subsidies" }` |  |
| 168 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 171 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 173 | `PolicyScreen.LADDER_READ` | `118` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 73 | `String policyArea` | null is the landing |
| 75 | `String policyPage` |  |
| 110 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 122 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 2489 | **type** `final class PolicyScreen` | The policy tab: the four rows of levers - taxes, wages, money, promises - the staged set every dial writes into, the ladder and the batch preview that show what a proposal would cost before it is applied, and the page... |
| 36 | 1 | `PolicyScreen(UserInterface ui)` |  |

### THE POLICY TAB (lines 38-96)

### THE STAGED SET. (lines 97-148)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 131 | 1 | `boolean isStaged(String key)` |  |
| 133 | 4 | `double staged(String key, double current)` |  |
| 138 | 1 | `void stage(String key, double value)` |  |
| 140 | 1 | `void unstage(String key)` |  |
| 142 | 1 | `void dropProposal()` |  |
| 145 | 3 | `static double clampRate(double value, double max)` | TaxPolicy.clamp(), which is private there and needed here to preview it. |

### THE LADDER (lines 149-286)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 63 | `VBox taxLadder(Lever lever)` |  |
| 248 | 14 | `Button stepButton(String glyph, boolean live, Runnable go)` | One notch, in the units the dial is read in. |
| 263 | 4 | `static String stepWord(double step)` |  |
| 268 | 3 | `static double bounded(double value, Lever lever)` |  |
| 279 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |

### WHAT THE WHOLE BATCH WOULD DO (lines 287-419)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 46 | `double taxTotalUnder(boolean proposed)` |  |
| 350 | 9 | `double assessedUnder(EconomyManager em, TaxPolicy p, Sector s, boolean proposed)` | What a sector is on the roll for, with a staged farmland relief applied. |
| 361 | 48 | `VBox stagedBar()` | The foot bar: everything pending, what it adds up to, and one button. |
| 411 | 8 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### THE FOUR TAXES (lines 420-446)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 12 | `void cityRateLever(VBox column, TaxPolicy policy, String which)` | Profit, sales and wage all move off the income rate, so it is on all three. |

### EVERYTHING (lines 447-560)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 65 | `void taxOverviewPage(VBox column)` |  |
| 516 | 44 | `HBox taxSourceRow(String name, double raised, double all, String rate, String what, String page)` | One tax on the overview: what it raised, its share, its rate, and a door. |

### PROFIT (lines 561-628)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 65 | `void profitTaxPage(VBox column)` |  |

### SALES (lines 629-698)

| line | len | member | says |
|---:|---:|---|---|
| 631 | 67 | `void salesTaxPage(VBox column)` |  |

### WAGE (lines 699-809)

| line | len | member | says |
|---:|---:|---|---|
| 701 | 61 | `void wageTaxPage(VBox column)` |  |
| 776 | 33 | `void payrollBurden(VBox column, TaxPolicy policy, double base)` | The whole charge on a payslip, which no screen has ever totalled. |

### PROPERTY (lines 810-902)

| line | len | member | says |
|---:|---:|---|---|
| 812 | 90 | `void propertyTaxPage(VBox column)` |  |

### THE LANDING (lines 903-1214)

| line | len | member | says |
|---:|---:|---|---|
| 907 | 77 | `void showPolicyMenu()` |  |
| 986 | 5 | `double taxRaised()` | Every tax line the city collected last month. |
| 993 | 7 | `double promisesCost()` | What the three promises cost the treasury in a month, net of what they collect. |
| 1001 | 9 | `int pinnedBands()` |  |
| 1011 | 23 | `HBox policyVitals()` |  |
| 1042 | 110 | `java.util.List<String[]> policyFlags()` | The levers that are currently forcing something. |
| 1153 | 17 | `VBox flagLine(String tone, String heading, String body)` |  |
| 1171 | 43 | `HBox policyRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 1215-1293)

| line | len | member | says |
|---:|---:|---|---|
| 1219 | 74 | `void drawPolicyScreen()` |  |

### THE PIECES A LEVER IS MADE OF (lines 1294-1396)

| line | len | member | says |
|---:|---:|---|---|
| 1326 | 48 | `VBox stageSlider(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> label)` | The lever itself: a slider that STAGES a change rather than making one. |
| 1376 | 20 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back. |

### TAXES - the two rates (lines 1397-1472)

| line | len | member | says |
|---:|---:|---|---|
| 1416 | 56 | `void farmlandRelief(javafx.scene.layout.VBox column, Game game, TaxPolicy policy, EconomyManager em)` | The one dial that decides whether the city keeps its fields. |

### the arithmetic behind it (lines 1473-1559)

| line | len | member | says |
|---:|---:|---|---|
| 1475 | 3 | `static double scaled(double amount, double factor)` |  |
| 1480 | 15 | `double profitFactor(TaxPolicy policy, double from, double to)` | How the profit tax moves with the base rate, weighted by what each sector paid. |
| 1497 | 13 | `double salesFactor(TaxPolicy policy, double from, double to)` | ...and the sales tax, weighted by what each sector actually sold. |
| 1519 | 3 | `double wageTaxAtBase(TaxPolicy policy, double base)` | The wage tax, recomputed rather than scaled. |
| 1529 | 3 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset)` |  |
| 1533 | 16 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset, WageBand onlyBand)` |  |
| 1551 | 8 | `double payrollIn(WageBand band)` | The payroll a band carries, which is what its rate is charged on. |

### TAXES - by wage band (lines 1560-1611)

| line | len | member | says |
|---:|---:|---|---|
| 1580 | 31 | `VBox bandLever(String name, String offset, String effective, String note, double signedOffset)` | The heading row of one band or one sector's line: what it is, and where it sits. |

### TAXES - by sector (lines 1612-1615)

### WAGES - the floor (lines 1616-1744)

| line | len | member | says |
|---:|---:|---|---|
| 1632 | 103 | `void minimumWagePage(VBox column)` |  |
| 1737 | 7 | `double bestWageIn(LabourMarket market, WageBand band)` | The best-paid job somebody in this band can hold. |

### MONEY - the policy rate (lines 1745-1926)

| line | len | member | says |
|---:|---:|---|---|
| 1758 | 168 | `void policyRatePage(VBox column)` |  |

### MONEY - the currency reform (lines 1927-2053)

| line | len | member | says |
|---:|---:|---|---|
| 1952 | 93 | `void currencyReformPage(VBox column)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 2047 | 6 | `static String afterName(Denomination unit, double factor)` | What the money would be called after lopping by this factor. |

### PROMISES - the pension (lines 2054-2210)

| line | len | member | says |
|---:|---:|---|---|
| 2069 | 141 | `void pensionPage(VBox column)` |  |

### PROMISES - tuition (lines 2211-2324)

| line | len | member | says |
|---:|---:|---|---|
| 2226 | 98 | `void tuitionPage(VBox column)` |  |

### PROMISES - the out of work and the students (2026-09-11) (lines 2325-2446)

| line | len | member | says |
|---:|---:|---|---|
| 2336 | 105 | `void outOfWorkPage(VBox column)` |  |
| 2442 | 4 | `static String burdenTone(double share)` |  |

### PROMISES - the standing subsidies (lines 2447-2519)

| line | len | member | says |
|---:|---:|---|---|
| 2456 | 63 | `void subsidyPage(VBox column)` |  |


# PolicyScreen.java - 2,527 lines · 54 methods · 8 constants · interface

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
| 106 | THE STAGED SET. |
| 158 | THE LADDER |
| 296 | WHAT THE WHOLE BATCH WOULD DO |
| 429 | THE FOUR TAXES |
| 456 | · EVERYTHING |
| 570 | · PROFIT |
| 638 | · SALES |
| 708 | · WAGE |
| 747 | · · what a payslip actually loses |
| 819 | · PROPERTY |
| 912 | THE LANDING |
| 970 | · WHAT IS ACTUALLY BITING |
| 1063 | · · the wage floor |
| 1074 | · · who is shut out |
| 1085 | · · tuition |
| 1109 | · · pensions |
| 1118 | · · subsidies |
| 1132 | · · the price of money |
| 1144 | · · the money itself |
| 1152 | · · at the stops |
| 1224 | ONE SUBJECT, ITS OWN STRIP |
| 1303 | THE PIECES A LEVER IS MADE OF |
| 1406 | TAXES - the two rates |
| 1482 | · the arithmetic behind it |
| 1569 | TAXES - by wage band |
| 1620 | TAXES - by sector |
| 1624 | WAGES - the floor |
| 1657 | · · the ladder |
| 1684 | · · who is pinned |
| 1709 | · · and the city's own bill |
| 1753 | MONEY - the policy rate |
| 1839 | · · prices, in words |
| 1845 | · · where it has been |
| 1935 | MONEY - the currency reform |
| 2062 | PROMISES - the pension |
| 2113 | · · and what it does to people |
| 2146 | · · what workers pay |
| 2178 | · · what seniors receive |
| 2219 | PROMISES - tuition |
| 2250 | · · what a family has to find |
| 2288 | · · the schools this month |
| 2333 | PROMISES - the out of work and the students (2026-09-11) |
| 2455 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `PolicyScreen.POLICY_HOME` | `"Everything"` | null is the landing |
| 91 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 93 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 94 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 95 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Tuition", "Subsidies" }` |  |
| 177 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 180 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 182 | `PolicyScreen.LADDER_READ` | `118` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 73 | `String policyArea` | null is the landing |
| 75 | `String policyPage` |  |
| 119 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 131 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 2497 | **type** `final class PolicyScreen` | The policy tab: the four rows of levers - taxes, wages, money, promises - the staged set every dial writes into, the ladder and the batch preview that show what a proposal would cost before it is applied, and the page... |
| 36 | 1 | `PolicyScreen(UserInterface ui)` |  |

### THE POLICY TAB (lines 38-105)

### THE STAGED SET. (lines 106-157)

| line | len | member | says |
|---:|---:|---|---|
| 135 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 140 | 1 | `boolean isStaged(String key)` |  |
| 142 | 4 | `double staged(String key, double current)` |  |
| 147 | 1 | `void stage(String key, double value)` |  |
| 149 | 1 | `void unstage(String key)` |  |
| 151 | 1 | `void dropProposal()` |  |
| 154 | 3 | `static double clampRate(double value, double max)` | TaxPolicy.clamp(), which is private there and needed here to preview it. |

### THE LADDER (lines 158-295)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 63 | `VBox taxLadder(Lever lever)` |  |
| 257 | 14 | `Button stepButton(String glyph, boolean live, Runnable go)` | One notch, in the units the dial is read in. |
| 272 | 4 | `static String stepWord(double step)` |  |
| 277 | 3 | `static double bounded(double value, Lever lever)` |  |
| 288 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |

### WHAT THE WHOLE BATCH WOULD DO (lines 296-428)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 46 | `double taxTotalUnder(boolean proposed)` |  |
| 359 | 9 | `double assessedUnder(EconomyManager em, TaxPolicy p, Sector s, boolean proposed)` | What a sector is on the roll for, with a staged farmland relief applied. |
| 370 | 48 | `VBox stagedBar()` | The foot bar: everything pending, what it adds up to, and one button. |
| 420 | 8 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### THE FOUR TAXES (lines 429-455)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 12 | `void cityRateLever(VBox column, TaxPolicy policy, String which)` | Profit, sales and wage all move off the income rate, so it is on all three. |

### EVERYTHING (lines 456-569)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 65 | `void taxOverviewPage(VBox column)` |  |
| 525 | 44 | `HBox taxSourceRow(String name, double raised, double all, String rate, String what, String page)` | One tax on the overview: what it raised, its share, its rate, and a door. |

### PROFIT (lines 570-637)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 65 | `void profitTaxPage(VBox column)` |  |

### SALES (lines 638-707)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 67 | `void salesTaxPage(VBox column)` |  |

### WAGE (lines 708-818)

| line | len | member | says |
|---:|---:|---|---|
| 710 | 61 | `void wageTaxPage(VBox column)` |  |
| 785 | 33 | `void payrollBurden(VBox column, TaxPolicy policy, double base)` | The whole charge on a payslip, which no screen has ever totalled. |

### PROPERTY (lines 819-911)

| line | len | member | says |
|---:|---:|---|---|
| 821 | 90 | `void propertyTaxPage(VBox column)` |  |

### THE LANDING (lines 912-1223)

| line | len | member | says |
|---:|---:|---|---|
| 916 | 77 | `void showPolicyMenu()` |  |
| 995 | 5 | `double taxRaised()` | Every tax line the city collected last month. |
| 1002 | 7 | `double promisesCost()` | What the three promises cost the treasury in a month, net of what they collect. |
| 1010 | 9 | `int pinnedBands()` |  |
| 1020 | 23 | `HBox policyVitals()` |  |
| 1051 | 110 | `java.util.List<String[]> policyFlags()` | The levers that are currently forcing something. |
| 1162 | 17 | `VBox flagLine(String tone, String heading, String body)` |  |
| 1180 | 43 | `HBox policyRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 1224-1302)

| line | len | member | says |
|---:|---:|---|---|
| 1228 | 74 | `void drawPolicyScreen()` |  |

### THE PIECES A LEVER IS MADE OF (lines 1303-1405)

| line | len | member | says |
|---:|---:|---|---|
| 1335 | 48 | `VBox stageSlider(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> label)` | The lever itself: a slider that STAGES a change rather than making one. |
| 1385 | 20 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back. |

### TAXES - the two rates (lines 1406-1481)

| line | len | member | says |
|---:|---:|---|---|
| 1425 | 56 | `void farmlandRelief(javafx.scene.layout.VBox column, Game game, TaxPolicy policy, EconomyManager em)` | The one dial that decides whether the city keeps its fields. |

### the arithmetic behind it (lines 1482-1568)

| line | len | member | says |
|---:|---:|---|---|
| 1484 | 3 | `static double scaled(double amount, double factor)` |  |
| 1489 | 15 | `double profitFactor(TaxPolicy policy, double from, double to)` | How the profit tax moves with the base rate, weighted by what each sector paid. |
| 1506 | 13 | `double salesFactor(TaxPolicy policy, double from, double to)` | ...and the sales tax, weighted by what each sector actually sold. |
| 1528 | 3 | `double wageTaxAtBase(TaxPolicy policy, double base)` | The wage tax, recomputed rather than scaled. |
| 1538 | 3 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset)` |  |
| 1542 | 16 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset, WageBand onlyBand)` |  |
| 1560 | 8 | `double payrollIn(WageBand band)` | The payroll a band carries, which is what its rate is charged on. |

### TAXES - by wage band (lines 1569-1619)

| line | len | member | says |
|---:|---:|---|---|
| 1588 | 31 | `VBox bandLever(String name, String offset, String effective, String note, double signedOffset)` | page is staged that reaches it, what it WOULD be charged at, written "20.00%  \u2192  20.25%". |

### TAXES - by sector (lines 1620-1623)

### WAGES - the floor (lines 1624-1752)

| line | len | member | says |
|---:|---:|---|---|
| 1640 | 103 | `void minimumWagePage(VBox column)` |  |
| 1745 | 7 | `double bestWageIn(LabourMarket market, WageBand band)` | The best-paid job somebody in this band can hold. |

### MONEY - the policy rate (lines 1753-1934)

| line | len | member | says |
|---:|---:|---|---|
| 1766 | 168 | `void policyRatePage(VBox column)` |  |

### MONEY - the currency reform (lines 1935-2061)

| line | len | member | says |
|---:|---:|---|---|
| 1960 | 93 | `void currencyReformPage(VBox column)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 2055 | 6 | `static String afterName(Denomination unit, double factor)` | What the money would be called after lopping by this factor. |

### PROMISES - the pension (lines 2062-2218)

| line | len | member | says |
|---:|---:|---|---|
| 2077 | 141 | `void pensionPage(VBox column)` |  |

### PROMISES - tuition (lines 2219-2332)

| line | len | member | says |
|---:|---:|---|---|
| 2234 | 98 | `void tuitionPage(VBox column)` |  |

### PROMISES - the out of work and the students (2026-09-11) (lines 2333-2454)

| line | len | member | says |
|---:|---:|---|---|
| 2344 | 105 | `void outOfWorkPage(VBox column)` |  |
| 2450 | 4 | `static String burdenTone(double share)` |  |

### PROMISES - the standing subsidies (lines 2455-2527)

| line | len | member | says |
|---:|---:|---|---|
| 2464 | 63 | `void subsidyPage(VBox column)` |  |


# PolicyScreen.java - 3,320 lines · 75 methods · 17 constants · interface

`ham/citybuildersim/ui/PolicyScreen.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
> stagedLadder, dropProposal, pinnedBands) because other screens offer the
> same levers. Every dial here is drawn by one class, Ladder (0.7.6), which
> replaced the tax pages' taxLadder and everybody else's stageSlider.

**Uses:** [Palette](Palette.md) (398), [TaxPolicy](TaxPolicy.md) (80), [WageBand](WageBand.md) (24), [DebtManager](DebtManager.md) (21), [EducationType](EducationType.md) (21), [Sector](Sector.md) (16), [EconomyManager](EconomyManager.md) (15), [JobType](JobType.md) (11), [Money](Money.md) (11), [CareType](CareType.md) (10), [Education](Education.md) (9), [LabourMarket](LabourMarket.md) (8), [Denomination](Denomination.md) (8), [Ladder](Ladder.md) (7), [SectorBooks](SectorBooks.md) (7), [NationalAccounts](NationalAccounts.md) (6), [CentralBank](CentralBank.md) (6), [Sectors](Sectors.md) (4), [HouseholdBalance](HouseholdBalance.md) (4), [FamilyModel](FamilyModel.md) (4), [SalesTaxLedger](SalesTaxLedger.md) (3), [PopulationManager](PopulationManager.md) (3), [PayTier](PayTier.md) (3), [UserInterface](UserInterface.md) (2), [PriceIndex](PriceIndex.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [Healthcare](Healthcare.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [Game](Game.md) (1), [Bank](Bank.md) (1)... and 7 more

**Used by (2):** [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 41 | THE POLICY TAB |
| 101 | THE STAGED SET. |
| 153 | THE LADDER |
| 274 | WHAT THE WHOLE BATCH WOULD DO |
| 420 | THE FOUR TAXES |
| 461 | · EVERYTHING |
| 596 | · PROFIT |
| 665 | · SALES |
| 737 | · WAGE |
| 777 | · · what a payslip actually loses |
| 853 | · PROPERTY |
| 946 | THE LANDING |
| 1010 | · WHAT IS ACTUALLY BITING |
| 1103 | · · the wage floor |
| 1114 | · · who is shut out |
| 1125 | · · tuition |
| 1149 | · · pensions |
| 1158 | · · subsidies |
| 1172 | · · the price of money |
| 1189 | · · the money itself |
| 1197 | · · at the stops |
| 1286 | ONE SUBJECT, ITS OWN STRIP |
| 1371 | THE PIECES A LEVER IS MADE OF |
| 1440 | TAXES - the two rates |
| 1516 | · the arithmetic behind it |
| 1603 | TAXES - by wage band |
| 1655 | TAXES - by sector |
| 1659 | WAGES - the floor |
| 1692 | · · the ladder |
| 1719 | · · who is pinned |
| 1744 | · · and the city's own bill |
| 1788 | MONEY - the policy rate |
| 1921 | · · prices, in words |
| 1927 | · · where it has been |
| 2105 | · · THE HOLDINGS DIAL (0.7.1) |
| 2144 | · · THE CEILING, AS A DIAL (0.7.2) |
| 2212 | MONEY - the currency reform |
| 2339 | PROMISES - the pension |
| 2390 | · · and what it does to people |
| 2423 | · · what workers pay |
| 2455 | · · what seniors receive |
| 2496 | PROMISES - the schools: the price of a place, who pays it, and the |
| 2580 | · · the levers, registered for the one bar |
| 2596 | · · what a family has to find |
| 2640 | · · the schools this month |
| 2654 | · · the price of a place |
| 2739 | · · the grant |
| 2817 | · · the loan |
| 2974 | PROMISES - the out of work and the students (2026-09-11) |
| 3075 | PROMISES - the clinic's price, and a premium (2026-09-19) |
| 3248 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `PolicyScreen.POLICY_HOME` | `"Everything"` | null is the landing |
| 95 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 97 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 98 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 99 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Health", "Schools", "Subsidies" }` |  |
| 180 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 183 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 186 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 2185 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2188 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2191 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2194 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2197 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2200 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2864 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 2867 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 2902 | `PolicyScreen.SCHOOL_FIGURES` | `150` | How wide the four figures beside a school kind's ladder are held. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 77 | `String policyArea` | null is the landing |
| 79 | `String policyPage` |  |
| 114 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 126 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 3287 | **type** `final class PolicyScreen` | The policy tab: the four rows of levers - taxes, wages, money, promises - the staged set every dial writes into, the ladder and the batch preview that show what a proposal would cost before it is applied, and the page... |
| 39 | 1 | `PolicyScreen(UserInterface ui)` |  |

### THE POLICY TAB (lines 41-100)

### THE STAGED SET. (lines 101-152)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 135 | 1 | `boolean isStaged(String key)` |  |
| 137 | 4 | `double staged(String key, double current)` |  |
| 142 | 1 | `void stage(String key, double value)` |  |
| 144 | 1 | `void unstage(String key)` |  |
| 146 | 1 | `void dropProposal()` |  |
| 149 | 3 | `static double clampRate(double value, double max)` | TaxPolicy.clamp(), which is private there and needed here to preview it. |

### THE LADDER (lines 153-273)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 1 | `static String baseKey(String tax)` | The staged key of one income tax's own base (0.7.4): "base:profit", "base:sales" or "base:wage". |
| 197 | 3 | `double stagedBase(String tax, double current)` | The base a preview prices one income tax at: its own lever if staged, else the every-tax lever if that is, else what it is. |
| 202 | 3 | `boolean baseStaged(String tax)` | Whether anything staged moves this income tax's base: its own lever, or every tax at once. |
| 213 | 9 | `Ladder ladderOf(Lever lever)` | A registered dial's Ladder, wired to the staged set: the lever goes in the register the foot bar names and applies from, the thumb sits at what is staged, and every move goes through propose(). |
| 224 | 3 | `VBox ladder(Lever lever)` | A tax page's dial, and the farmland relief's: a registered Ladder whose step reads in points. |
| 229 | 4 | `static String stepWord(double step)` | How one step reads on a tax page's ends line: in points of the rate. |
| 234 | 3 | `static double bounded(double value, Lever lever)` |  |
| 245 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |
| 261 | 5 | `boolean changesAtCurrent(Lever lever)` | Whether a lever still changes something at the value it reads as current (0.7.4): "Every tax at once" once the three income bases have parted. |
| 268 | 4 | `String nowReads(Lever lever)` | What a lever reads today, in words: its current value, or "three rates" and "a price per school" for the every-tax and every-school levers once they have parted. |

### WHAT THE WHOLE BATCH WOULD DO (lines 274-419)

| line | len | member | says |
|---:|---:|---|---|
| 283 | 49 | `double taxTotalUnder(boolean proposed)` |  |
| 340 | 9 | `double assessedUnder(EconomyManager em, TaxPolicy p, Sector s, boolean proposed)` | What a sector is on the roll for, with a staged farmland relief applied. |
| 351 | 1 | `VBox stagedBar()` | The foot bar: everything pending, what it adds up to, and one button. |
| 359 | 50 | `VBox stagedBar(boolean taxes)` | The same bar with or without the tax total: the Schools page's dials (2026-09-21) are not taxes, and a bar that footed them to "Tax a month: no difference" would be answering a question nobody asked. |
| 411 | 8 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### THE FOUR TAXES (lines 420-460)

| line | len | member | says |
|---:|---:|---|---|
| 448 | 12 | `void baseRateLever(VBox column, String tax, String title, String offsets, double current, java.util.function.DoubleConsumer apply)` | One income tax's own base (0.7.4): the top lever of the profit, sales and wage pages. |

### EVERYTHING (lines 461-595)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 86 | `void taxOverviewPage(VBox column)` |  |
| 551 | 44 | `HBox taxSourceRow(String name, double raised, double all, String rate, String what, String page)` | One tax on the overview: what it raised, its share, its rate, and a door. |

### PROFIT (lines 596-664)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 66 | `void profitTaxPage(VBox column)` |  |

### SALES (lines 665-736)

| line | len | member | says |
|---:|---:|---|---|
| 667 | 69 | `void salesTaxPage(VBox column)` |  |

### WAGE (lines 737-852)

| line | len | member | says |
|---:|---:|---|---|
| 739 | 62 | `void wageTaxPage(VBox column)` |  |
| 815 | 37 | `void payrollBurden(VBox column, TaxPolicy policy, double base)` | The whole charge on a payslip, which no screen has ever totalled. |

### PROPERTY (lines 853-945)

| line | len | member | says |
|---:|---:|---|---|
| 855 | 90 | `void propertyTaxPage(VBox column)` |  |

### THE LANDING (lines 946-1285)

| line | len | member | says |
|---:|---:|---|---|
| 950 | 83 | `void showPolicyMenu()` |  |
| 1035 | 5 | `double taxRaised()` | Every tax line the city collected last month. |
| 1042 | 7 | `double promisesCost()` | What the three promises cost the treasury in a month, net of what they collect. |
| 1050 | 9 | `int pinnedBands()` |  |
| 1060 | 23 | `HBox policyVitals()` |  |
| 1091 | 127 | `java.util.List<String[]> policyFlags()` | The levers that are currently forcing something. |
| 1220 | 3 | `static String capitalised(String word)` | "sales" to "Sales", for a flag that opens with a tax's name. |
| 1224 | 17 | `VBox flagLine(String tone, String heading, String body)` |  |
| 1242 | 43 | `HBox policyRow(String name, String blurb, String figure, String sub, String tone, String area, String page)` |  |

### ONE SUBJECT, ITS OWN STRIP (lines 1286-1370)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 80 | `void drawPolicyScreen()` |  |

### THE PIECES A LEVER IS MADE OF (lines 1371-1439)

| line | len | member | says |
|---:|---:|---|---|
| 1406 | 11 | `VBox stagedLadder(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> la...` | The lever itself: a Ladder (0.7.6) that STAGES a change rather than making one, for a dial that keeps its own apply bar - the wage floor, the policy rate, the pension's two, EI's two, the clinic's two, and the fare on... |
| 1419 | 20 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back. |

### TAXES - the two rates (lines 1440-1515)

| line | len | member | says |
|---:|---:|---|---|
| 1459 | 56 | `void farmlandRelief(javafx.scene.layout.VBox column, Game game, TaxPolicy policy, EconomyManager em)` | The one dial that decides whether the city keeps its fields. |

### the arithmetic behind it (lines 1516-1602)

| line | len | member | says |
|---:|---:|---|---|
| 1518 | 3 | `static double scaled(double amount, double factor)` |  |
| 1523 | 15 | `double profitFactor(TaxPolicy policy, double from, double to)` | How the profit tax moves with the base rate, weighted by what each sector paid. |
| 1540 | 13 | `double salesFactor(TaxPolicy policy, double from, double to)` | ...and the sales tax, weighted by what each sector actually sold. |
| 1562 | 3 | `double wageTaxAtBase(TaxPolicy policy, double base)` | The wage tax, recomputed rather than scaled. |
| 1572 | 3 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset)` |  |
| 1576 | 16 | `double wageTaxWith(TaxPolicy policy, WageBand band, double base, double offset, WageBand onlyBand)` |  |
| 1594 | 8 | `double payrollIn(WageBand band)` | The payroll a band carries, which is what its rate is charged on. |

### TAXES - by wage band (lines 1603-1654)

| line | len | member | says |
|---:|---:|---|---|
| 1623 | 31 | `VBox bandLever(String name, String offset, String effective, String note, double signedOffset)` | The heading row of one band or one sector's line: what it is, and where it sits. |

### TAXES - by sector (lines 1655-1658)

### WAGES - the floor (lines 1659-1787)

| line | len | member | says |
|---:|---:|---|---|
| 1675 | 103 | `void minimumWagePage(VBox column)` |  |
| 1780 | 7 | `double bestWageIn(LabourMarket market, WageBand band)` | The best-paid job somebody in this band can hold. |

### MONEY - the policy rate (lines 1788-2211)

| line | len | member | says |
|---:|---:|---|---|
| 1803 | 380 | `void policyRatePage(VBox column)` |  |
| 2203 | 3 | `static String monthsWords(double months)` | A ceiling as the ladder reads it: "1 month", "6 months". |
| 2208 | 3 | `static double halfPoint(double target)` | A target on the half-point grid, so a run of steps lands on 3% and not on 3.0000000000000004%. |

### MONEY - the currency reform (lines 2212-2338)

| line | len | member | says |
|---:|---:|---|---|
| 2237 | 93 | `void currencyReformPage(VBox column)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 2332 | 6 | `static String afterName(Denomination unit, double factor, Currency money)` | What the city's money would be called after lopping by this factor. |

### PROMISES - the pension (lines 2339-2495)

| line | len | member | says |
|---:|---:|---|---|
| 2354 | 141 | `void pensionPage(VBox column)` |  |

### PROMISES - the schools: the price of a place, who pays it, and the (lines 2496-2973)

| line | len | member | says |
|---:|---:|---|---|
| 2538 | 9 | `static String grantWords(TaxPolicy.GrantBasis basis, double amount)` | The grant in words, for a line that names it: "15% of an unskilled wage a month". |
| 2549 | 8 | `static String basisName(TaxPolicy.GrantBasis basis)` | What a basis is called on its chip. |
| 2559 | 4 | `static String amountWords(TaxPolicy.GrantBasis basis, double amount)` | How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. |
| 2564 | 292 | `void schoolsPage(VBox column)` |  |
| 2858 | 4 | `Lever register(Lever lever)` | A dial on this page, registered so the foot bar can name and apply it. |
| 2870 | 1 | `static String schoolKey(EducationType kind)` | The staged key of one school kind's own price (0.7.6): "tuitionScale:UNIVERSITY". |
| 2873 | 1 | `static String scaleWords(double scale)` | A tuition scale as the page writes it: "x1.00". |
| 2881 | 3 | `double stagedScaleOf(TaxPolicy policy, EducationType kind)` | The scale a preview prices one kind of school at: its own lever if staged, else every school at once if that is, else what it is (0.7.6). |
| 2886 | 7 | `boolean anyScaleStaged()` | Whether anything staged moves a price of a place: every school at once, or any kind's own. |
| 2895 | 5 | `static Label schoolPriceHead(String name)` | The small label over a price-of-a-place ladder: which school it prices. |
| 2910 | 48 | `HBox schoolPriceRow(EducationType kind, TaxPolicy policy, Education schools, double places)` | One school kind's row: its name and its own ladder on the left, and on the right the four figures the model holds for it - places, students, cost and revenue - or "no school" and a greyed dial when nothing of that kin... |
| 2960 | 13 | `static HBox schoolFigure(String word, String figure, String tone)` | One of the four figures beside a school kind's ladder: its word on the left, the figure on the right. |

### PROMISES - the out of work and the students (2026-09-11) (lines 2974-3074)

| line | len | member | says |
|---:|---:|---|---|
| 2986 | 83 | `void outOfWorkPage(VBox column)` |  |
| 3070 | 4 | `static String burdenTone(double share)` |  |

### PROMISES - the clinic's price, and a premium (2026-09-19) (lines 3075-3247)

| line | len | member | says |
|---:|---:|---|---|
| 3094 | 153 | `void healthPage(VBox column)` |  |

### PROMISES - the standing subsidies (lines 3248-3320)

| line | len | member | says |
|---:|---:|---|---|
| 3257 | 63 | `void subsidyPage(VBox column)` |  |


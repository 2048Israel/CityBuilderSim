# PolicyScreen.java - 4,340 lines · 212 methods · 77 constants · interface

`ham/citybuildersim/ui/PolicyScreen.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> The Policy tab: every number the city sets for itself - the taxes, the
> wage floor, the price of money and the promises - each a dial with what
> it would do beside it, on a hub of four area cards and the levers that are
> biting, and the pages behind them, a picture a page.
> 
> WHY THIS SHAPE (0.7.36). It was a 560 px statement column in a 1,270 px
> centre: a landing of four rows and a list of flags, and behind it thirteen
> pages, each a lever's figure in 20 px type, a paragraph, the ladder, and
> once moved a statement of what it would do - the paragraphs most of the
> screen. Jerus, on the screens not yet redone: "the others are still full
> of text and the design could be more intuitive and fun". Redrawn in
> Build's style (the project's spec-policy-0735.md): the hub's four AREA
> CARDS, each a door with its figure and a small picture, over WHAT IS
> BITING, a card a lever forcing someone's hand, and the decisions lately
> made; four areas with a chip each in the head and their pages as tabs;
> every page a picture first - the tax take as a bar, the payers ranked, the
> wage ladder against the floor, the rates on one line, the pension's cover
> beside a pensioner's month - and every dial a DIAL CARD (Levers.dialCard())
> with what it does beside it, before and after, following the thumb. Every
> paragraph is behind an (i); every old table behind "details".
> 
> EVERY "AFTER" IS THE MODEL'S (the spec's D2). A staged set goes through a
> detached copy of the city's policy (TaxPolicy.copy()) and its own setters,
> and each figure is the owner's read of the copy - PolicyPreview's tax take
> and THE BUDGET, EconomyManager's payroll lines, Unemployment's pool, the
> bank's own rule for its savers. The screen adds nothing up.
> 
> AND EVERY DIAL IS STILL A PROPOSAL UNTIL IT IS APPLIED (the spec's D4,
> unchanged): the taxes' and the schools' dials stage into the tray at the
> foot of the stage and one press applies them; the wage floor, the policy
> rate and the promises' dials keep an Apply on their own card; the target
> and since 0.7.52 how strictly the rule holds it, the central bank's
> holdings and ceiling, the hand on the dial and the subsidies apply at
> once; and a page change throws the staged set away.
> 
> Split out of UserInterface on 2026-09-18. The shell reads which area and
> page are open (policyArea, policyPage) for the rail and the scroll memory,
> and other screens open a page by setting them and calling showPolicyMenu();
> the transit fare on Infrastructure, and since 0.7.48 the fund's
> withdrawal on Finances, are staged with ownLadder() and applied with
> applyFoot(), which is why they stay here. Every dial is drawn
> by one class, Ladder (0.7.6).

**Uses:** [Palette](Palette.md) (348), [TaxPolicy](TaxPolicy.md) (134), [PolicyPreview](PolicyPreview.md) (84), [Icons](Icons.md) (78), [DebtManager](DebtManager.md) (55), [Money](Money.md) (38), [Ladder](Ladder.md) (30), [Education](Education.md) (20), [EducationType](EducationType.md) (18), [WageBand](WageBand.md) (17), [PriceIndex](PriceIndex.md) (16), [DecisionLog](DecisionLog.md) (16), [EconomyManager](EconomyManager.md) (16), [CityNeeds](CityNeeds.md) (14), [Expectations](Expectations.md) (12), [HouseholdBalance](HouseholdBalance.md) (12), [LabourMarket](LabourMarket.md) (11), [Sector](Sector.md) (10), [CareType](CareType.md) (10), [JobType](JobType.md) (9), [Denomination](Denomination.md) (9), [CentralBank](CentralBank.md) (8), [Pieces](Pieces.md) (7), [Healthcare](Healthcare.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (6), [Sectors](Sectors.md) (5), [HistorySave](HistorySave.md) (5), [TimeChart](TimeChart.md) (5), [HistoryScreen](HistoryScreen.md) (5), [PopulationManager](PopulationManager.md) (3)... and 20 more

**Used by (8):** [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 88 | POLICY (0.7.36): THE FRAME |
| 326 | · the four figures |
| 409 | THE STAGED SET. |
| 456 | THE LADDER |
| 554 | · the staged set, through a copy |
| 657 | · the tray |
| 721 | · a dial that keeps its own Apply |
| 781 | · the screen's own pieces |
| 980 | THE HUB (0.7.36; the landing's four rows until then) |
| 1062 | · the area cards |
| 1180 | · what is biting |
| 1210 | · · the wage floor |
| 1224 | · · who is shut out |
| 1235 | · · tuition |
| 1257 | · · pensions |
| 1266 | · · subsidies |
| 1280 | · · the price of money |
| 1298 | · · trust in the bank (0.7.45) |
| 1310 | · · the money itself |
| 1318 | · · at the stops |
| 1365 | TAXES (0.7.36; THE FOUR TAXES until then) |
| 1400 | · EVERYTHING |
| 1551 | · PROFIT, SALES, WAGE, PROPERTY |
| 2101 | WAGES - the floor |
| 2260 | MONEY - the policy rate |
| 2666 | · HOW STRICT (0.7.52): the dial beside the target - DebtManager's HOW STRICT |
| 2799 | · THE ANCHOR AND THE DRIFT (0.7.45) |
| 2964 | · THE BASKET (0.7.45) |
| 3074 | MONEY - the currency reform |
| 3204 | PROMISES - the pension |
| 3379 | PROMISES - the out of work and the students (2026-09-11) |
| 3433 | PROMISES - food (0.7.45; the UI spec's 2.3 and D6) |
| 3597 | PROMISES - the clinic's price, and a premium (2026-09-19) |
| 3794 | PROMISES - the schools: the price of a place, who pays it, and the |
| 4290 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 100 | `PolicyScreen.TAXES` | `"Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises"` | The four areas, by what kind of lever each is - Jerus picked the grouping. |
| 103 | `PolicyScreen.AREAS` | `{ TAXES, WAGES, MONEY, PROMISES }` | ...as the head's chips list them. |
| 106 | `PolicyScreen.AREA_ICONS` | `{ Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION }` | ...and each one's icon: the coin, the staff, the bank, the people. |
| 111 | `PolicyScreen.POLICY_HOME` | `"Everything"` | The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. |
| 128 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 130 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 131 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 132 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Food", "Health", "Schools", "Subsidies" }` |  |
| 135 | `PolicyScreen.TAX_ICONS` | `{ Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES }` | Each page's icon on its tab, in the pages' order. |
| 137 | `PolicyScreen.WAGE_ICONS` | `{ Icons.STAFF }` | ...Wages' one, the staff. |
| 139 | `PolicyScreen.MONEY_ICONS` | `{ Icons.BANK, Icons.BANKNOTE }` | ...Money's: the bank for the policy rate, the banknote for the currency reform. |
| 141 | `PolicyScreen.PROMISE_ICONS` | `{ Icons.CANE, Icons.STAFF, Icons.FOOD, Icons.HEALTH, Icons.EDUCATION, Icons.S...` | ...and the promises': the cane, the staff, food, health, education and the sector icon for the subsidies. |
| 153 | `PolicyScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. |
| 156 | `PolicyScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). |
| 159 | `PolicyScreen.TRAY_CHROME` | `90` | How tall the staged tray is taken to be until it is laid out. |
| 291 | `PolicyScreen.LEAD_INFO` | `"Every number the city sets for itself, and what each one is doing this month...` | The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). |
| 482 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 485 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 488 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 651 | `PolicyScreen.BUDGET_INFO` | `"Last month's surplus or deficit, and what it would be with this change: ever...` | THE BUDGET's (i). |
| 660 | `PolicyScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press.THE BUDGET is ...` | P20, the tray's (i). |
| 1031 | `PolicyScreen.NOTHING_BINDING_INFO` | `"Nothing is binding.Every lever has room to move, no sector is shut " + "out ...` | P2: what the hub says when nothing is binding. |
| 1076 | `PolicyScreen.TAX_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST }` | The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. |
| 1085 | `PolicyScreen.TAX_NAMES` | `{ "Profit", "Sales", "Wage", "Property" }` | The four taxes by name, profit to property: their cards, the bar's parts and their pages. |
| 1388 | `PolicyScreen.CAVEAT` | `"Nothing here knows that the new rate changes what anybody does next month - ...` | What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. |
| 1392 | `PolicyScreen.CAVEAT_LINE` | `"Struck on this month's books; behaviour is not projected."` | The caveat's line on a card. |
| 1403 | `PolicyScreen.TAKE_INFO` | `"Last month, by tax.Each has a rate of its own, and three of the four a " + "...` | P3, THE TAX TAKE's (i). |
| 1499 | `PolicyScreen.EVERY_TAX_INFO` | `"Moves all three rates to one number.A sector or wage band you've set apart "...` | P5: every tax at once, its (i). |
| 1505 | `PolicyScreen.PROPERTY_NOT` | `"Property is not on this dial: it is charged on value rather than on income, ...` | P6. |
| 1554 | `PolicyScreen.PROFIT_INFO` | `"Charged on what each sector earned before tax.A sector that lost money " + "...` | P8, the profit tax's head. |
| 1559 | `PolicyScreen.LOSS_INFO` | `"A sector that lost money paid no profit tax at all, which is why the " + "fi...` | P4. |
| 1563 | `PolicyScreen.SALES_INFO` | `"Charged on VALUE ADDED - what a sector sells, less the tax it already " + "p...` | P10, the sales tax's head. |
| 1569 | `PolicyScreen.WAGE_INFO` | `"Taken off every payroll in the city before the household sees it.The " + "jo...` | P12, the wage tax's head. |
| 1574 | `PolicyScreen.PROPERTY_INFO` | `"Charged on what land and buildings are assessed at, whether or not the " + "...` | P14, the property tax's head. |
| 1579 | `PolicyScreen.PROPERTY_STEP_INFO` | `"A twentieth of a point a step rather than the quarter the other three " + "g...` | P15. |
| 1584 | `PolicyScreen.REFUND_INFO` | `"\"In refund\" means a sector's credits on what it bought exceed the tax " + ...` | P11. |
| 1590 | `PolicyScreen.ROLL_INFO` | `"The power and water plants are the city's own and exempt.Raising what " + "l...` | P16. |
| 1595 | `PolicyScreen.SALES_SCALED` | `"Scaled, not recomputed: each sector's net remittance moves by the ratio of i...` | The sales tax's preview, scaled (the spec's D11, B6). |
| 1632 | `PolicyScreen.OFFSET_INFO` | `"A move is in POINTS off the tax's own rate, so a row left at zero is taxed "...` | P9, the offsets' note. |
| 1702 | `PolicyScreen.BANK_INFO` | `"The commercial bank's profit is taxed at Retail's rate - a Commercial Bank i...` | The bank's line (B5, D12). |
| 1944 | `PolicyScreen.PAYSLIP_INFO` | `"Only the first is a tax and only the first is set here - the other three " +...` | P13. |
| 1975 | `PolicyScreen.FARMLAND_INFO` | `"A field is worth what a developer would pay for it and grows what a " + "far...` | P17. |
| 1982 | `PolicyScreen.FARMLAND_CAVEAT` | `"Exact against today's land price.What it actually decides is whether " + "th...` | P19. |
| 2129 | `PolicyScreen.FLOOR_INFO` | `"Every wage in the city is a multiple of this number, so moving it moves " + ...` | P22, the floor's (i). |
| 2135 | `PolicyScreen.PINNED_WORDS` | `"At least one skill level is oversupplied AND cannot get any cheaper, so " + ...` | P23, both forms. |
| 2140 | `PolicyScreen.FREE_WORDS` | `"No skill level is pinned against the floor, so the labour market is " + "cle...` | ...the other: no band pinned. |
| 2145 | `PolicyScreen.FLOOR_CAVEAT` | `"Once wages have walked there, which takes about a year.Nothing about " + "th...` | P24. |
| 2293 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2296 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2299 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2302 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2305 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2308 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2334 | `PolicyScreen.OVER_INFO` | `"Hot money follows this difference in, and leaves the day it closes." + "It i...` | P28. |
| 2340 | `PolicyScreen.REAL_INFO` | `"The currency follows this one: a dial under the inflation people expect is a...` | P28's second half: the real differential - against the inflation people expect since 0.7.42 (the UI spec's B9). |
| 2346 | `PolicyScreen.REAL_DIAL_INFO` | `"The dial less the inflation people expect: what saving at the dial earns " +...` | The dial's own real rate (0.7.45): REAL_INFO's first half, for the dial alone. |
| 2500 | `PolicyScreen.HAND_INFO` | `"Jerus's autopilot: the rule can hold the dial.Every month, before anything i...` | P32. |
| 2504 | `PolicyScreen.REPRICE_INFO` | `"This does not reprice a single bond the city has already sold - every " + "c...` | P34: the dial card's caveat. |
| 2731 | `PolicyScreen.QE_LINE` | `"Buys term paper only: it reaches prices through what people own and through ...` | How QE reaches prices, in one plain line on the holdings card (0.7.45; the UI spec's D20). |
| 2764 | `PolicyScreen.CEILING_INFO` | `String.format("When the treasury runs dry the central bank advances the gap i...` | P36. |
| 2810 | `PolicyScreen.ANCHOR_INFO` | `"What people expect prices to do decides what they do next.Wages are asked " ...` | THE ANCHOR's (i): Expectations, in a player's words. |
| 2821 | `PolicyScreen.STRUCK_INFO` | `"Fees, build costs and upkeep, land, the shelf's floor, the pension's wage ba...` | The struck level's (i). |
| 2826 | `PolicyScreen.DRIFT_INFO` | `"The currency slides every month by the credible part of the " + "inflation p...` | THE CURRENCY'S DRIFT's (i). |
| 2967 | `PolicyScreen.BASKET_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST, ...` | The basket's five parts' colours, groceries to services: the money ramp, then the rest - areas, not verdicts. |
| 3013 | `PolicyScreen.SWING_INFO` | `"Prices here have more than doubled and come back at some point." + "Wages, r...` | P30. |
| 3018 | `PolicyScreen.REFORMED_INFO` | `"The index is measured against the city's first basket in founding money, " +...` | P31, once the money has been reformed. |
| 3227 | `PolicyScreen.COVER_INFO` | `"The rest is general revenue - the same pot the schools and the hospitals " +...` | P41 and P47. |
| 3396 | `PolicyScreen.EI_INFO` | `"EI only pays the first twelve months, so a long bust costs less in EI than "...` | P57. |
| 3839 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 3842 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 3882 | `PolicyScreen.SHARE_INFO` | `"The city's share of every course fee.Households pay the rest out of a " + "m...` | P48. |
| 3889 | `PolicyScreen.BURDEN_INFO` | `String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is...` | P49. |
| 3895 | `PolicyScreen.PRICE_INFO` | `"The founding tuition table times this, before the city's share comes off." +...` | P50. |
| 3903 | `PolicyScreen.SCHOOL_CAVEAT` | `"Against the courses being taken now, each kind's students at its own " + "pr...` | P51/P52: the schools' preview caveat. |
| 4254 | `PolicyScreen.LOAN_INFO` | `"Charged on a graduate's balance while they repay it, and on nothing while " ...` | P55. |
| 4260 | `PolicyScreen.LOAN_CAVEAT` | `"Against the balances the graduates owe today.The instalment itself does " + ...` | P56. |
| 4305 | `PolicyScreen.SUBSIDY_INFO` | `"A protected sector is topped up to break-even every month it loses money, " ...` | P69. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 109 | `String policyArea` | The area open, one of AREAS, or null for the hub. |
| 112 | `String policyPage` |  |
| 144 | `final Set<String> openLines` | What the player has opened - a fold, a payer's own dial, the folded zero payers - by key, so a redraw on the clock leaves it open (Land's rule: kept while the game runs, not saved; the spec's D15). |
| 147 | `private final Map<String, Integer> flagSeen` | The month each biting flag was first drawn in, by its heading: a flag first seen this month carries NEW until the month turns. |
| 150 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 422 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 434 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 4260 | **type** `final class PolicyScreen` | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it would do beside it, on a hub of four area cards and the levers that are... |
| 86 | 1 | `PolicyScreen(UserInterface ui)` |  |

### POLICY (0.7.36): THE FRAME (lines 88-325)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 9 | `static String[] pagesOf(String area)` | The pages of an area, in their tabs' order. |
| 173 | 9 | `static String[] iconsOf(String area)` | ...and their icons. |
| 188 | 54 | `void showPolicyMenu()` | The tab's entry point: the hub, or the area and page open. |
| 244 | 6 | `void openHub()` | The hub, at its top. |
| 252 | 7 | `void open(String area, String page)` | An area, on its first page or the one named, at its top (every door on this tab, and the shell's). |
| 261 | 1 | `void openArea(String area)` | An area's first page: a chip in the head. |
| 264 | 1 | `void openPage(String page)` | A page of the open area: a tab. |
| 267 | 22 | `private void frameOver(VBox frame, VBox page, Region tray)` | The fixed frame over a scrolling page and, when one is given, the tray under it: the page as tall as what is left between them (BankScreen's frame, with a foot). |
| 298 | 15 | `static String areaInfo(String area)` | Each area's (i): what it holds (the landing's row blurbs until 0.7.36). |
| 320 | 5 | `HBox head()` | The head: "Policy" with the money blue's swatch, or the breadcrumb "Policy › Taxes" with "Policy" a way back to the hub; the four areas as chips at its right, the open one lit (the spec's D14: the breadcrumb and the c... |

### the four figures (lines 326-408)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 338 | 24 | `List<Cell> vitalCells()` | THE FOUR (pure: the probe reads them as the strip shows them). |
| 364 | 11 | `HBox vitals()` | The strip: the four as limit cells, each a door. |
| 377 | 7 | `static String ratesWords(TaxPolicy p)` | The rates in words: "15.00% on income · 1.50% a year on property", or the three once they have parted (B2: two places, as Money.pct2() says). |
| 386 | 4 | `static int needLevel(List<CityNeeds.Need> needs, CityNeeds.Kind kind)` | A NEEDS YOU row's level by its kind, in the list measured once a draw: 0 when it is not listed. |
| 392 | 3 | `static String levelTone(int level)` | A level's colour: plain, amber near the line, red past it. |
| 397 | 1 | `TaxPolicy policy()` | The city's policy. |
| 400 | 3 | `double taxRaised()` | Every tax line the city collected last month - CityNeeds' own sum since 0.7.24, which NEEDS YOU's TREASURY line reads too. |
| 405 | 3 | `int pinnedBands()` | Wage bands pinned to the floor with people spare in them - CityNeeds' count since 0.7.24, NEEDS YOU's WAGES line's. |

### THE STAGED SET. (lines 409-455)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 443 | 1 | `boolean isStaged(String key)` |  |
| 445 | 4 | `double staged(String key, double current)` |  |
| 450 | 1 | `void stage(String key, double value)` |  |
| 452 | 1 | `void unstage(String key)` |  |
| 454 | 1 | `void dropProposal()` |  |

### THE LADDER (lines 456-553)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 1 | `static String baseKey(String tax)` | The staged key of one income tax's own base (0.7.4): "base:profit", "base:sales" or "base:wage". |
| 499 | 9 | `Ladder ladderOf(Lever lever)` | A registered dial's Ladder, wired to the staged set: the lever goes in the register the tray names and applies from, the thumb sits at what is staged, and every move goes through propose(). |
| 510 | 4 | `static String stepWord(double step)` | How one step reads on a tax page's ends line: in points of the rate. |
| 515 | 3 | `static double bounded(double value, Lever lever)` |  |
| 526 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |
| 542 | 5 | `boolean changesAtCurrent(Lever lever)` | Whether a lever still changes something at the value it reads as current (0.7.4): "Every tax at once" once the three income bases have parted. |
| 549 | 4 | `String nowReads(Lever lever)` | What a lever reads today, in words: its current value, or "three rates" and "a price per school" for the every-tax and every-school levers once they have parted. |

### the staged set, through a copy (lines 554-656)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 32 | `static void onCopy(TaxPolicy p, String key, double v)` | What a staged key sets, on any policy: the one place a key is read as a dial for the preview (0.7.36). |
| 601 | 1 | `TaxPolicy stagedPolicy()` | The city's policy with everything staged put through a copy - every key in the order it was moved, and the grant's basis and amount together. |
| 604 | 17 | `TaxPolicy stagedPolicyWith(String key, double value)` | ...and one key at a value of the caller's on top (a dial card's thumb, while it is dragged). |
| 623 | 5 | `TaxPolicy with(String key, double value)` | The city's policy with one key moved and nothing else: a dial with its own Apply previews its own move. |
| 630 | 4 | `TaxPolicy.GrantBasis stagedBasis(TaxPolicy live)` | The grant's basis as staged, else the city's. |
| 636 | 1 | `double stagedShare()` | The city's share of tuition as staged (Education's dial, not the policy's). |
| 639 | 3 | `PolicyPreview.Budget stagedBudget()` | THE BUDGET under everything staged: PolicyPreview's (M8). |
| 644 | 5 | `Effect budgetEffect(TaxPolicy after, double share)` | THE BUDGET as an effect row, before and after a policy and a share of the caller's. |

### the tray (lines 657-720)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 24 | `Region tray()` | The staged tray (the spec's 3.0): on the pages whose dials batch - the taxes' and the schools' - once anything is staged: a chip a change that comes out with its ×, THE BUDGET before and after the set, and Apply and D... |
| 699 | 1 | **type** `record TrayItem(String key, String words)` | One staged change as the tray lists it: its key, and "The wage rate 15.00% → 16.00%". |
| 702 | 9 | `List<TrayItem> trayItems()` | Each staged change the tray lists, in the order moved (pure: the probe reads them). |
| 713 | 7 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### a dial that keeps its own Apply (lines 721-780)

| line | len | member | says |
|---:|---:|---|---|
| 743 | 10 | `Ladder ownLadder(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> label)` | The lever itself: a Ladder (0.7.6) that STAGES a change rather than making one, for a dial that keeps its own Apply - the wage floor, the policy rate, the pension's two, EI's two, the clinic's two, the fare on Infrast... |
| 759 | 16 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back (0.7.34's action button since 0.7.36: a GO in the money blue that says what it sets, and "Leave it as it is" beside it). |
| 777 | 3 | `List<Node> applyFoot(String key, String label, Runnable apply)` | The foot of a card whose dial keeps its own Apply: the bar once its key is staged, nothing before. |

### the screen's own pieces (lines 781-979)

| line | len | member | says |
|---:|---:|---|---|
| 784 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 793 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 805 | 5 | `String struckWords(String what)` | A Pe-struck price's sentence for an (i) (0.7.45; the UI spec's D13): the level its founding figure is struck at this month - never set against the index, which an old save's level of 1 would misread. |
| 812 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. |
| 831 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 840 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 846 | 9 | `static VBox doorCard(VBox c, Runnable go)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 857 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 868 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 875 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 880 | 4 | `static Node line(String text, String whole)` | A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. |
| 886 | 1 | `static Label muted(String text)` | ...muted. |
| 889 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut. |
| 896 | 5 | `static FlowPane chips(Node...cs)` | A row of chips, wrapping. |
| 903 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 911 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 916 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 930 | 10 | `static HBox split(Region first, Region second, double share)` | Two cards side by side, the first `share` of the width (the floor's dial and the city's payroll). |
| 942 | 7 | `Region spark(double[] all, int n, String colour, double width, double height)` | The last `n` months of a History series, for a sparkline - BankScreen's. |
| 951 | 1 | `Sector sector(String key)` | A sector by its key, or null. |
| 954 | 1 | `static String ptsMove(double d)` | A signed share in points: "+1.00 pts". |
| 957 | 1 | `static String moneyMove(double d)` | Money with its sign, as a move: "+$2.5M", "−$1.2M", "$0". |
| 960 | 4 | `static String moneyMoveFull(double d)` | ...every digit, for one household's or one person's money: "+$173", "−$41", "$0" under half a dollar. |
| 966 | 4 | `static String amount(double v)` | Money that can be below nothing - a sector in refund, a surplus where a cost was - with a true minus: "$2.5M", "−$365k". |
| 972 | 1 | `static String rateMove(double d)` | A rate's move in points: "+0.25 points". |
| 975 | 1 | `static String pct0(double share)` | A percentage to none: "45%". |
| 978 | 1 | `static String times(double scale)` | "x1.00". |

### THE HUB (0.7.36; the landing's four rows until then) (lines 980-1061)

| line | len | member | says |
|---:|---:|---|---|
| 992 | 37 | `void hubPage(VBox page)` |  |
| 1035 | 9 | `List<DecisionLog.Entry> recentDecisions(int n)` | The last `n` decisions of the kinds this tab makes - taxes, promises, the central bank, the currency - newest first. |
| 1046 | 5 | `DecisionLog.Entry lastDecision(String kind)` | The last decision of one kind, or null. |
| 1052 | 4 | `static boolean isPolicyKind(String kind)` |  |
| 1058 | 3 | `static String decisionWords(DecisionLog.Entry e)` | A decision as a chip: "m1,204 · Taxes to 15%". |

### the area cards (lines 1062-1179)

| line | len | member | says |
|---:|---:|---|---|
| 1065 | 9 | `VBox areaCard(String svg, String title, String figure, Node picture, String line, String foot, Runnable go)` | One area card: its icon and title with "›", its figure, a small picture, one line and a foot - the whole a door. |
| 1079 | 4 | `double[] taxesBooked()` | The four taxes last month, profit · sales · wage · property, as NationalAccounts booked them. |
| 1087 | 9 | `VBox taxesCard()` |  |
| 1097 | 12 | `VBox wagesCard()` |  |
| 1110 | 22 | `VBox moneyCard()` |  |
| 1133 | 18 | `VBox promisesCard()` |  |
| 1153 | 4 | `static String promisesDoor(PolicyPreview.Promises p)` | Where the PROMISES card opens (0.7.45): Food when the vouchers are the largest slice, else Pensions. |
| 1159 | 8 | `static SegmentBar sliceBar(List<Slice> slices)` | Slices as one bar filling its card, each part's name and money its tooltip (a part below nothing draws nothing). |
| 1169 | 4 | `static String areaIcon(String area)` | An area's icon, as its chip has it. |
| 1175 | 4 | `static String signedPct1(double v)` | Inflation as a signed share to one place, with a true minus: "+0.2%". |

### what is biting (lines 1180-1364)

| line | len | member | says |
|---:|---:|---|---|
| 1187 | 1 | **type** `record Flag(String tone, String heading, String body, String area, String page, String door)` | A lever that is forcing something: its colour (a verdict), its heading and what it means, and where it is undone - the area and page its door opens, and the door's words. |
| 1198 | 143 | `List<Flag> policyFlags()` | The levers that are currently forcing something. |
| 1343 | 3 | `static String capitalised(String word)` | "sales" to "Sales", for a flag that opens with a tax's name. |
| 1348 | 16 | `VBox flagCard(Flag f, boolean isNew)` | A biting lever as a card: its alert in its verdict's colour, its heading (NEW the month it is first seen), its first sentence with the rest behind an (i), and a door to where it is undone. |

### TAXES (0.7.36; THE FOUR TAXES until then) (lines 1365-1399)

| line | len | member | says |
|---:|---:|---|---|
| 1395 | 4 | `TaxPolicy previewFor(String key, double v, double current, double step)` | The policy a card previews at a thumb's value: everything staged, with the card's key at `v` - or exactly what is staged while the thumb rests where nothing of its own is staged (a parted every-tax dial at its current... |

### EVERYTHING (lines 1400-1550)

| line | len | member | says |
|---:|---:|---|---|
| 1408 | 26 | `void everythingPage(VBox page)` |  |
| 1436 | 35 | `VBox takeCard(double[] booked, TaxPolicy live)` | THE TAX TAKE: last month's four as one bar, a click on a part its page; and once anything is staged, the four at the staged rates, a moved part a ghost with its move over it. |
| 1473 | 10 | `double[][] taxHistory()` | Ten years of each tax from City History, profit · sales · wage · property; the profit line is the business and the food industry together, as the take's first part is. |
| 1485 | 12 | `VBox taxCard(int i, double raised, double all, String rate, double[] history, boolean atStop, String info)` | One tax as a card: its icon and name, its rate, what it raised and its share, its ten years, a chip at its legal stop - the whole a door to its page. |
| 1516 | 19 | `VBox everyTaxCard(TaxPolicy live)` | EVERY TAX AT ONCE (0.7.4; a dial card since 0.7.36) - what the one city rate became once each income tax had its own. |
| 1537 | 13 | `DoubleFunction<List<Effect>> everyTaxEffects(TaxPolicy live)` | Every tax at once's effects at any value of its thumb (pure: the probe reads them): the three income taxes and THE BUDGET. |

### PROFIT, SALES, WAGE, PROPERTY (lines 1551-2100)

| line | len | member | says |
|---:|---:|---|---|
| 1601 | 5 | `static String baseInfo(String offsets)` | P7, a base rate's (i): what moves off it. |
| 1608 | 22 | `void taxPage(VBox page, String which)` | One tax's page: its base on a dial card, who pays it ranked with each row's own move, the payslip (wage) or the farmland (property), and the old table under "details". |
| 1639 | 38 | `VBox baseCard(String which, TaxPolicy live)` | A tax's base on a dial card: what it raises and THE BUDGET before and after (and, for profit, the bank at Retail's rate - B5). |
| 1679 | 21 | `DoubleFunction<List<Effect>> baseEffects(String which, String key, double current, double step, TaxPolicy live)` | A tax's base's effects at any value of its thumb (pure: the probe reads them): the tax a month, the bank on Profit, THE BUDGET. |
| 1708 | 3 | **type** `record Payer(String key, Lever lever, String svg, String name, double paid, String figure, double rateNow, ...` | One payer of a tax: its row's key and lever (null: no dial - the bank), its icon and name, what it paid last month, its figure's words, its rate now and staged, its offset, a chip (null: none), and its line of the tak... |
| 1713 | 78 | `List<Payer> payersOf(String which, TaxPolicy live)` | The payers of a tax (pure: the probe reads them): every sector, or every band, with what the take strikes for each at the city's dials and the staged ones; the bank last on Profit. |
| 1793 | 3 | `static boolean paidNothing(Payer p)` | Whether a payer paid nothing to speak of and is folded into one row (a sector in refund is not: it is owed). |
| 1798 | 26 | `VBox payers(String which, TaxPolicy live)` | WHO PAYS: the payers ranked by what they paid, a row opening into its own move; those that paid nothing folded into one muted row that opens to the same rows (the spec's D15). |
| 1826 | 7 | `void addPayer(VBox list, Payer p, double scale, String which)` | A payer's row, and its own dial under it while it is open. |
| 1841 | 58 | `HBox payerRow(Payer p, double scale, boolean shown, Runnable toggle)` | One payer: its icon and name ("▸ its own move" under a name that opens), what it paid as a bar - with the staged take as a ghost bar under it once anything staged reaches it - its figure, and its rate as a chip ("15.0... |
| 1901 | 11 | `VBox offsetCard(Payer p, String which)` | A payer's own move, opened in place: its offset's dial, and that payer's tax and THE BUDGET before and after. |
| 1914 | 28 | `DoubleFunction<List<Effect>> offsetEffects(Payer p, String which)` | A payer's own move's effects at any value of its thumb (pure: the probe reads them): its tax, the bank beside Retail's, THE BUDGET. |
| 1951 | 22 | `VBox payslipCard(TaxPolicy live)` | THE PAYSLIP (T12): what a wage taxed at the wage rate loses, as a bar of the wage in four parts, and each band moved off the rate with its own total; the door to the promises' dials. |
| 1998 | 14 | `VBox farmlandCard(TaxPolicy live)` | The one dial that decides whether the city keeps its fields (T15). |
| 2014 | 16 | `DoubleFunction<List<Effect>> farmlandEffects(TaxPolicy live)` | The farmland relief's effects at any value of its thumb (pure: the probe reads them). |
| 2032 | 68 | `Node taxTable(String which, TaxPolicy policy)` | The old table of a tax's page (T9-T11, T14), behind "details": every sector or band with its figures as they were drawn. |

### WAGES - the floor (lines 2101-2259)

| line | len | member | says |
|---:|---:|---|---|
| 2149 | 17 | `void floorPage(VBox page)` |  |
| 2168 | 29 | `Node wageLadder(LabourMarket market, PopulationManager people, double floor, double want, boolean moved)` | The eleven jobs under their bands on one scale, the floor a rule across them; a band's chip says whether it is pinned (LabourMarket.isPinned(), as the model has it). |
| 2199 | 17 | `VBox floorCard(LabourMarket market, double floor)` | THE FLOOR on its dial card: read in today's money, set in founding money (D6); the floor, the unskilled wage, every wage and the city's payroll before and after. |
| 2218 | 12 | `DoubleFunction<List<Effect>> floorEffects(LabourMarket market)` | The floor's effects at any value of its thumb (pure: the probe reads them). |
| 2232 | 10 | `VBox cityPayrollCard()` | What the city itself pays (W4), neutral (B14: a payroll is not bad news by being one). |
| 2244 | 15 | `Node floorTable(LabourMarket market, double want, boolean moved)` | The ladder as a table (W2), behind "details", in today's money: each job's multiple of the floor, what it is paid, and where it heads at a staged floor. |

### MONEY - the policy rate (lines 2260-2665)

| line | len | member | says |
|---:|---:|---|---|
| 2311 | 3 | `static String monthsWords(double months)` | A ceiling as the ladder reads it: "1 month", "6 months". |
| 2316 | 3 | `static double halfPoint(double target)` | A target on the half-point grid, so a run of steps lands on 3% and not on 3.0000000000000004%. |
| 2321 | 11 | `String rateInfo()` | P25, the dial's line, and P26 corrected (the spec's B16: the city's paper is priced from the higher of the dial and the bank's cost of funds, not "at the dial"). |
| 2351 | 28 | `void ratePage(VBox page)` |  |
| 2387 | 21 | `Node priceRateChart()` | Behind the PRICES strip's "details" (the spec's R4): City History's price level (the index, left axis) and policy rate (percent, right axis) on one small chart - no legend, so the key under it names them. |
| 2410 | 9 | `static VBox rateCell(String label, String value, String line, String info)` | A cell under the rate line: its label, its figure, a line, and the paragraph behind an (i). |
| 2421 | 1 | **type** `record RateMark(double at, String name, String colour, boolean dashed, String tip)` | One rate on the line, worked out without drawing it: where, its name and colour, dashed or not, and its tooltip. |
| 2429 | 49 | `List<RateMark> rateMarks(double rate, double want, boolean moved)` | THE RATE LINE's marks (pure: the probe reads them): inflation (amber only past a point from the target, the old page's line), the target, the dial, the rule, savers, the city, prime and the world - and, staged, the di... |
| 2480 | 18 | `Node rateLine(double rate, double want, boolean moved)` | THE RATE LINE: the dial as a bar, every other rate a named rule across it, on 0 to a quarter past the highest (never under 3%). |
| 2510 | 1 | `static String spendPct(double v)` | The spend factor to one place: the dial reaches it only through the savers' rate, a few tenths of a point at most, which a whole percent would round away. |
| 2513 | 9 | `String spendInfo()` | P29. |
| 2532 | 29 | `VBox theDialCard(DebtManager market, PriceIndex px, double rate, double want)` | THE DIAL (R5, R7): the ladder with the rule's rate marked on it, the thirteen chips and "do what the rule says", whose hand it is in; what a move does to the city's rate, the savers', prime and what households spend -... |
| 2563 | 13 | `DoubleFunction<List<Effect>> dialEffects(double rate)` | The policy rate's effects at any value of its thumb (pure: the probe reads them). |
| 2584 | 75 | `VBox ruleCard(DebtManager market, PriceIndex px)` | THE RULE (R6): what it aims at - the target, set at once by its chips and its ladder (0.7.4: it moves no price this month, only what the rule says) - and what it says at two targets, and inflation against the target a... |
| 2661 | 1 | `static String plain(double v)` | A weight or a move in points as it is: "1.5", "1.375", "2" - one place would read 1.375 as 1.4. |
| 2664 | 1 | `static String pointsOf(double v)` | ...as points: "1 point", "1.25 points". |

### HOW STRICT (0.7.52): the dial beside the target - DebtManager's HOW STRICT (lines 2666-2798)

| line | len | member | says |
|---:|---:|---|---|
| 2669 | 12 | `static String strictInfo()` | HOW STRICT's (i): what each end does, from the model's own numbers. |
| 2683 | 4 | `static String strictWords(PolicyPreview.RuleAt r)` | The preview's words at a step (pure: the probe reads them): "Very strict: aims under 2.0%, at 1.0% · on target it sets 4.00% · at this inflation 5.10%". |
| 2689 | 1 | `static DebtManager.Strictness strictAt(double v)` | The step under a ladder's thumb. |
| 2697 | 13 | `VBox strictPart(DebtManager market)` | HOW STRICT (0.7.52): Jerus's dial beside the target, its five steps on a Ladder set at once like the target's, and under it what the bank would aim at and what the rule would set at the step under the thumb (PolicyPre... |
| 2712 | 17 | `String holdingsInfo()` | P35, both layers. |
| 2735 | 19 | `VBox holdingsCard(DebtManager market)` | THE CENTRAL BANK's holdings (R8): the share of the term paper it aims to hold, set at once; the thirty-year rate once the book has moved there (M9). |
| 2756 | 6 | `DoubleFunction<List<Effect>> holdingsEffects()` | The holdings' effects at any value of its thumb (pure: the probe reads them). |
| 2773 | 18 | `VBox ceilingCard()` | THE CENTRAL BANK's ceiling (R9): how far it will advance the treasury, set at once; the ceiling in money at another setting (M10); what is owed, red only when it binds (ceilingBound(): a verdict). |
| 2793 | 5 | `DoubleFunction<List<Effect>> ceilingEffects()` | The ceiling's effects at any value of its thumb (pure: the probe reads them). |

### THE ANCHOR AND THE DRIFT (0.7.45) (lines 2799-2963)

| line | len | member | says |
|---:|---:|---|---|
| 2807 | 1 | `static boolean offTarget(Expectations e, double target)` | True when what trust is judged on - inflation smoothed over a year - is off the target by more than Expectations.TOLERANCE: the rate line's inflation tick and the rule's band go amber (the UI spec's D4). |
| 2833 | 2 | **type** `record AnchorWords(String figure, double trust, String trustWords, String move, String moveTone, String lea...` | The anchor's words, worked out without drawing them (pure: the probe reads them). |
| 2836 | 31 | `AnchorWords anchorWords()` |  |
| 2876 | 16 | `static String notLeaningWords(Game g, double miss)` | Why the month's lean is nothing, on the model's own condition (Expectations.takeMonth()): the lean counts only when the advice that holds the target - the Standard rule's at any strictness since 0.7.52 (DebtManager.ho... |
| 2894 | 5 | `static String stepPts(double step)` | A month's move in trust, in points, signed with a true minus: "+0.2 pts", "−0.91 pts" (two places under a twentieth of a point). |
| 2901 | 1 | `static String rate2(double rate)` | A yearly rate to two places with a true minus: "2.15%", "−0.40%". |
| 2911 | 16 | `VBox anchorCard()` | THE ANCHOR (the UI spec's 2.2 item 2): what people expect; how far they trust the bank, as a gauge from nothing to all with the least and the most it can be; how trust moved this month and whether the rate leans again... |
| 2929 | 1 | **type** `record DriftWords(String figure, String line, String uip)` | The drift's words, worked out without drawing them (pure: the probe reads them). |
| 2931 | 16 | `DriftWords driftWords()` |  |
| 2949 | 4 | `static String parityWords(double deviation)` | Where the currency stands against parity, in words: "25.9% weaker than parity", "25.2% stronger", "at parity". |
| 2955 | 8 | `VBox driftCard()` | THE CURRENCY'S DRIFT (the UI spec's 2.2 item 2): the slide the anchor hands the currency, what it is struck from, and where the real rate alone would hold it. |

### THE BASKET (0.7.45) (lines 2964-3073)

| line | len | member | says |
|---:|---:|---|---|
| 2970 | 1 | **type** `record BasketPart(String name, double weight, String colour, String words)` | One part of the basket: its name, its weight, its colour and what its key says. |
| 2978 | 25 | `List<BasketPart> basketParts()` | The basket in force, part by part (pure: the probe reads them): each component's weight (PriceIndex.getWeight()) and its own inflation over the year off History's line for it (YearBook.componentInflation()) - or, with... |
| 3005 | 6 | `String basketWords()` | What the PRICES card says of the basket in force: when it was struck, or that it is still to be. |
| 3028 | 45 | `VBox pricesCard(PriceIndex px)` | THE PRICES (R4): the index since founding; where it has been - the dearest and the cheapest, on a bar, today marked (Jerus, 2026-09-14: "something as well that stores the highest price index and lowest") - once the tw... |

### MONEY - the currency reform (lines 3074-3203)

| line | len | member | says |
|---:|---:|---|---|
| 3091 | 7 | `String reformInfo()` | P37. |
| 3113 | 65 | `void reformPage(VBox page)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 3180 | 6 | `String foreignInfo()` | P40. |
| 3188 | 7 | `static VBox priceTag(String what, String before, String after)` | One price tag: what it is, today, and after the reform in the accent. |
| 3197 | 6 | `static String afterName(Denomination unit, double factor, Currency money)` | What the city's money would be called after lopping by this factor. |

### PROMISES - the pension (lines 3204-3378)

| line | len | member | says |
|---:|---:|---|---|
| 3236 | 1 | **type** `record Month(String word, String says, String tone)` | What a household's month means, in one of three forms (0.7.27, P42): its chip's word, its sentence and its verdict colour. |
| 3239 | 15 | `Month pensionerMonth()` | A pensioner household's month (pure: the probe reads it). |
| 3255 | 46 | `void pensionPage(VBox page)` |  |
| 3303 | 12 | `VBox contributionCard(EconomyManager em, TaxPolicy policy)` | WHAT WORKERS PAY IN (P-4): the contribution, its own Apply; collected, covered, out of the treasury and a payslip before and after (M3: struck on the wage bill, so a dial at nothing previews something - B9). |
| 3317 | 16 | `DoubleFunction<List<Effect>> contributionEffects(TaxPolicy policy)` | The contribution's effects at any value of its thumb (pure: the probe reads them). |
| 3335 | 19 | `VBox pensionDialCard(EconomyManager em, TaxPolicy policy, HouseholdBalance bal)` | WHAT SENIORS RECEIVE (P-5): the pension, of the FOUNDING unskilled wage (B10, D9), its own Apply; each senior, paid, out of the treasury and what one pensioner household would have. |
| 3356 | 22 | `DoubleFunction<List<Effect>> pensionEffects(TaxPolicy policy)` | The pension's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the out of work and the students (2026-09-11) (lines 3379-3432)

| line | len | member | says |
|---:|---:|---|---|
| 3402 | 3 | `static String cost(double v)` | Money a line takes out of the treasury, a surplus with a true minus: "$2.5M", "−$1.0M". |
| 3406 | 26 | `void outOfWorkPage(VBox page)` |  |

### PROMISES - food (0.7.45; the UI spec's 2.3 and D6) (lines 3433-3596)

| line | len | member | says |
|---:|---:|---|---|
| 3445 | 7 | `void foodPage(VBox page)` |  |
| 3454 | 6 | `String foodStatus(double share)` | The dial card's status (pure: the probe reads it): who a voucher would go to at the last sale's price. |
| 3469 | 15 | `VBox foodAssistanceCard(TaxPolicy policy)` | FOOD ASSISTANCE (0.7.43; its own tab since 0.7.45), its own Apply: the treasury's voucher toward an eligible household's baskets - what it would have paid at the last sale, the households who would hold one, the baske... |
| 3486 | 11 | `DoubleFunction<List<Effect>> foodEffects(TaxPolicy policy)` | The food assistance dial's effects at any value of its thumb (pure: the probe reads them). |
| 3499 | 1 | **type** `record AidRow(int row, String name, double got, String gotWords, String figures)` | One row of WHO WOULD GET IT, worked out without drawing it (pure: the probe reads them). |
| 3502 | 18 | `List<AidRow> aidRows()` | WHO WOULD GET IT's rows: every household row a voucher would reach or did, most eligible first. |
| 3522 | 4 | `static String basketShare(double share)` | Baskets got per basket needed, as a row says it: "got .58 of a basket", "got every basket". |
| 3528 | 18 | `VBox whoWouldGetIt()` | WHO WOULD GET IT (the UI spec's 2.3 item 3): a row a household kind, its baskets got per basket needed, who a voucher reaches. |
| 3548 | 11 | `VBox eiPremiumCard(EconomyManager em, TaxPolicy policy)` | What workers pay for EI (E-3), its own Apply: raised, and what EI and the grants take from the treasury. |
| 3561 | 8 | `DoubleFunction<List<Effect>> eiPremiumEffects(TaxPolicy policy)` | The EI premium's effects at any value of its thumb (pure: the probe reads them). |
| 3571 | 13 | `VBox eiBenefitCard(EconomyManager em, TaxPolicy policy, Unemployment u)` | What EI replaces (E-3), its own Apply: the pool's bill, a claimant's cheque and the treasury's share, struck on the pool (M5). |
| 3586 | 10 | `DoubleFunction<List<Effect>> eiBenefitEffects(TaxPolicy policy)` | The EI benefit's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the clinic's price, and a premium (2026-09-19) (lines 3597-3793)

| line | len | member | says |
|---:|---:|---|---|
| 3622 | 1 | **type** `record Corner(String word, String says)` | The corner the city is in (P62): its chip's word, and its sentence. |
| 3624 | 23 | `Corner corner()` |  |
| 3649 | 8 | `String coverWords()` | P63's line: what the fees and the premium cover - or that the service cost nothing (B19: it read "Fees cover 0% of the cost" in a city with fees and no cost). |
| 3658 | 40 | `void healthPage(VBox page)` |  |
| 3700 | 16 | `String turnedAwayInfo()` | P64, both layers. |
| 3718 | 26 | `VBox feeCard(Healthcare care, TaxPolicy policy)` | WHAT A PATIENT PAYS (S-4), its own Apply: the break-even marked on the dial, the three fees, the fees at full and the treasury's share - at full service both sides (B13). |
| 3746 | 18 | `DoubleFunction<List<Effect>> feeEffects(TaxPolicy policy)` | The care fee's effects at any value of its thumb (pure: the probe reads them). |
| 3766 | 17 | `VBox healthPremiumCard(EconomyManager em, Healthcare care, TaxPolicy policy)` | WHAT EVERY WAGE PAYS (S-5), its own Apply: raised, and the treasury's share. |
| 3785 | 8 | `DoubleFunction<List<Effect>> healthPremiumEffects(TaxPolicy policy)` | The health premium's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the schools: the price of a place, who pays it, and the (lines 3794-4289)

| line | len | member | says |
|---:|---:|---|---|
| 3845 | 1 | `static String schoolKey(EducationType kind)` | The staged key of one school kind's own price (0.7.6): "tuitionScale:UNIVERSITY". |
| 3848 | 1 | `static String scaleWords(double scale)` | A tuition scale as the page writes it: "x1.00". |
| 3851 | 13 | `static String grantWords(TaxPolicy.GrantBasis basis, double amount, double level)` | The grant in words, for a line that names it: "15% of an unskilled wage a month". |
| 3866 | 8 | `static String basisName(TaxPolicy.GrantBasis basis)` | What a basis is called on its chip. |
| 3876 | 4 | `static String amountWords(TaxPolicy.GrantBasis basis, double amount, double level)` | How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. |
| 3909 | 3 | `double stagedScaleOf(TaxPolicy policy, EducationType kind)` | The scale a preview prices one kind of school at: its own lever if staged, else every school at once if that is, else what it is (0.7.6). |
| 3913 | 34 | `void schoolsPage(VBox page)` |  |
| 3949 | 5 | `static List<EducationType> schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (TaxPolicy's own list, which it keeps to itself). |
| 3956 | 4 | `Lever register(Lever lever)` | A dial on this page, registered so the tray can name and apply it. |
| 3962 | 4 | `Lever kindLever(EducationType kind, TaxPolicy policy)` | One kind's own price, as a lever. |
| 3968 | 2 | **type** `record Course(EducationType kind, double now, double then, boolean standing, double places, double students...` | One course's row, worked out without drawing it: its burden now and staged, whether a school of it stands, and its four figures. |
| 3972 | 11 | `List<Course> courses(Education schools, TaxPolicy policy, double[] places)` | The nine courses (pure: the probe reads them): what a place costs a family as a share of a month's wage, at the city's price and share and at the staged ones (PolicyPreview.schoolBurden()). |
| 3985 | 14 | `VBox burdenRows(Education schools, TaxPolicy policy, double[] places)` | WHO CAN AFFORD A PLACE: the nine as bars on 0 to 100% of a month's wage with a tick at MAX_BURDEN, red past it; each opens into its own price. |
| 4001 | 44 | `HBox courseRow(Course c, double scale, boolean shown, Runnable toggle)` | One course: its name ("▸ its price"), its bar - red past the line where nobody enrols - with the staged burden a ghost, its figure, and "no school" when none stands. |
| 4053 | 28 | `VBox kindCard(Course c, TaxPolicy policy)` | One kind's own price, opened in place (K-3): its dial - greyed with nothing standing, since a price for a school the city does not have moves nothing today, though every school at once still sets it - its places, stud... |
| 4083 | 16 | `DoubleFunction<List<Effect>> kindEffects(Course c, TaxPolicy policy)` | A kind's own price's effects at any value of its thumb (pure: the probe reads them). |
| 4101 | 7 | `VBox shareCard(Lever lever, Education schools, TaxPolicy policy)` | The city's share of tuition (K-1, registered): what households pay and what the city waives before and after, and THE BUDGET. |
| 4110 | 14 | `DoubleFunction<List<Effect>> shareEffects(TaxPolicy policy)` | The city's share's effects at any value of its thumb (pure: the probe reads them). |
| 4126 | 11 | `VBox schoolsMonthCard(Education schools)` | THE SCHOOLS THIS MONTH (K-2), footing: staff and buildings, less what households paid, is the net cost; the tuition waived beside it in grey, outside the sum (B17, D13). |
| 4139 | 14 | `VBox everySchoolCard(Lever lever, TaxPolicy policy)` | Every school at once (K-3, registered): its dial ("a price per school" until moved, once the nine have parted), what the courses are billed and households pay, and THE BUDGET. |
| 4155 | 13 | `DoubleFunction<List<Effect>> everySchoolEffects(TaxPolicy policy)` | Every school's price's effects at any value of its thumb (pure: the probe reads them). |
| 4170 | 16 | `String grantInfo(TaxPolicy policy)` | P53, with its figures. |
| 4194 | 41 | `VBox grantCard(TaxPolicy policy)` | What a student is granted (K-5, registered): the basis as chips - a chip stages the basis AND today's grant re-expressed in its unit, so the switch alone changes nothing until the amount moves - and the amount's dial ... |
| 4237 | 15 | `DoubleFunction<List<Effect>> grantEffects(TaxPolicy policy)` | The grant's amount's effects at any value of its thumb (pure: the probe reads them). |
| 4265 | 14 | `VBox loanCard(Lever lever, TaxPolicy policy)` | What the loan costs them afterwards (K-6, registered): what is owed and repaid, and a month's interest and THE BUDGET before and after (HouseholdBalance.studentInterestAt()). |
| 4281 | 8 | `DoubleFunction<List<Effect>> loanEffects(TaxPolicy policy)` | The loan rate's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the standing subsidies (lines 4290-4340)

| line | len | member | says |
|---:|---:|---|---|
| 4312 | 28 | `void subsidyPage(VBox page)` |  |


# PolicyScreen.java - 4,502 lines · 219 methods · 85 constants · interface

`ham/citybuildersim/ui/PolicyScreen.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The Policy tab: every number the city sets for itself - the taxes, the
> wage floor, the price of money and the promises - each a dial with what
> it would do beside it, on a hub of four area cards and the levers that are
> biting, and the pages behind them, a picture a page. Since 0.7.109 (batch
> Z2) a fifth area and card, Zoning - the city's ground, its zones and the
> forest law - whose pages ZoningScreen draws, staged and applied through
> this screen's set and tray.
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

**Uses:** [Palette](Palette.md) (350), [TaxPolicy](TaxPolicy.md) (148), [PolicyPreview](PolicyPreview.md) (96), [Icons](Icons.md) (83), [DebtManager](DebtManager.md) (56), [Money](Money.md) (40), [Ladder](Ladder.md) (32), [Education](Education.md) (20), [EducationType](EducationType.md) (18), [WageBand](WageBand.md) (17), [DecisionLog](DecisionLog.md) (17), [PriceIndex](PriceIndex.md) (16), [EconomyManager](EconomyManager.md) (16), [CityNeeds](CityNeeds.md) (14), [Expectations](Expectations.md) (12), [HouseholdBalance](HouseholdBalance.md) (12), [LabourMarket](LabourMarket.md) (11), [Sector](Sector.md) (10), [CareType](CareType.md) (10), [JobType](JobType.md) (9), [Denomination](Denomination.md) (9), [CentralBank](CentralBank.md) (8), [Pieces](Pieces.md) (7), [Healthcare](Healthcare.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (6), [ZoningScreen](ZoningScreen.md) (5), [Sectors](Sectors.md) (5), [HistorySave](HistorySave.md) (5), [TimeChart](TimeChart.md) (5), [HistoryScreen](HistoryScreen.md) (5)... and 23 more

**Used by (10):** [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 97 | POLICY (0.7.36): THE FRAME |
| 349 | · the four figures |
| 432 | THE STAGED SET. |
| 479 | THE LADDER |
| 577 | · the staged set, through a copy |
| 682 | · the tray |
| 746 | · a dial that keeps its own Apply |
| 806 | · the screen's own pieces |
| 1005 | THE HUB (0.7.36; the landing's four rows until then) |
| 1089 | · the area cards |
| 1207 | · what is biting |
| 1237 | · · the wage floor |
| 1251 | · · who is shut out |
| 1262 | · · tuition |
| 1284 | · · pensions |
| 1293 | · · subsidies |
| 1307 | · · the price of money |
| 1325 | · · trust in the bank (0.7.45) |
| 1337 | · · the money itself |
| 1345 | · · at the stops |
| 1392 | TAXES (0.7.36; THE FOUR TAXES until then) |
| 1427 | · EVERYTHING |
| 1531 | · FUEL (0.7.103) |
| 1588 | · PORT DUES (FR4) |
| 1711 | · PROFIT, SALES, WAGE, PROPERTY |
| 2261 | WAGES - the floor |
| 2420 | MONEY - the policy rate |
| 2828 | · HOW STRICT (0.7.52): the dial beside the target - DebtManager's HOW STRICT |
| 2961 | · THE ANCHOR AND THE DRIFT (0.7.45) |
| 3126 | · THE BASKET (0.7.45) |
| 3236 | MONEY - the currency reform |
| 3366 | PROMISES - the pension |
| 3541 | PROMISES - the out of work and the students (2026-09-11) |
| 3595 | PROMISES - food (0.7.45; the UI spec's 2.3 and D6) |
| 3759 | PROMISES - the clinic's price, and a premium (2026-09-19) |
| 3956 | PROMISES - the schools: the price of a place, who pays it, and the |
| 4452 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 111 | `PolicyScreen.TAXES` | `"Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises", ZONING = "Z...` | The four areas, by what kind of lever each is - Jerus picked the grouping - and since 0.7.109 (batch Z2) the fifth, the city's ground: Zoning (ZoningScreen draws it). |
| 114 | `PolicyScreen.AREAS` | `{ TAXES, WAGES, MONEY, PROMISES, ZONING }` | ...as the head's chips list them. |
| 117 | `PolicyScreen.AREA_ICONS` | `{ Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION, Icons.ZONING }` | ...and each one's icon: the coin, the staff, the bank, the people, the flag on a plot. |
| 122 | `PolicyScreen.POLICY_HOME` | `"Everything"` | The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. |
| 139 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property", "Fuel", "Port dues" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 141 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 142 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", "Currency reform" }` |  |
| 143 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Food", "Health", "Schools", "Subsidies" }` |  |
| 146 | `PolicyScreen.TAX_ICONS` | `{ Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES, Icons....` | Each page's icon on its tab, in the pages' order (the crane for the port dues, FR4). |
| 148 | `PolicyScreen.WAGE_ICONS` | `{ Icons.STAFF }` | ...Wages' one, the staff. |
| 150 | `PolicyScreen.MONEY_ICONS` | `{ Icons.BANK, Icons.BANKNOTE }` | ...Money's: the bank for the policy rate, the banknote for the currency reform. |
| 152 | `PolicyScreen.PROMISE_ICONS` | `{ Icons.CANE, Icons.STAFF, Icons.FOOD, Icons.HEALTH, Icons.EDUCATION, Icons.S...` | ...and the promises': the cane, the staff, food, health, education and the sector icon for the subsidies. |
| 164 | `PolicyScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. |
| 167 | `PolicyScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). |
| 170 | `PolicyScreen.TRAY_CHROME` | `90` | How tall the staged tray is taken to be until it is laid out. |
| 311 | `PolicyScreen.LEAD_INFO` | `"Every number the city sets for itself, and what each one is doing this month...` | The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). |
| 505 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 508 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 511 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 676 | `PolicyScreen.BUDGET_INFO` | `"Last month's surplus or deficit, and what it would be with this change: ever...` | THE BUDGET's (i). |
| 685 | `PolicyScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press.THE BUDGET is ...` | P20, the tray's (i). |
| 1057 | `PolicyScreen.NOTHING_BINDING_INFO` | `"Nothing is binding.Every lever has room to move, no sector is shut " + "out ...` | P2: what the hub says when nothing is binding. |
| 1103 | `PolicyScreen.TAX_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST }` | The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. |
| 1112 | `PolicyScreen.TAX_NAMES` | `{ "Profit", "Sales", "Wage", "Property" }` | The four taxes by name, profit to property: their cards, the bar's parts and their pages. |
| 1415 | `PolicyScreen.CAVEAT` | `"Nothing here knows that the new rate changes what anybody does next month - ...` | What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. |
| 1419 | `PolicyScreen.CAVEAT_LINE` | `"Struck on this month's books; behaviour is not projected."` | The caveat's line on a card. |
| 1430 | `PolicyScreen.TAKE_INFO` | `"Last month, by tax.Each has a rate of its own, and three of the four a " + "...` | P3, THE TAX TAKE's (i). |
| 1526 | `PolicyScreen.EVERY_TAX_INFO` | `"Moves all three rates to one number.A sector or wage band you've set apart "...` | P5: every tax at once, its (i). |
| 1534 | `PolicyScreen.FUEL_DUTY` | `"fuelDuty"` | The staged key of the fuel duty dial (0.7.103, batch P4; Jerus's A7). |
| 1537 | `PolicyScreen.FUEL_DUTY_STEP` | `.00001` | One step of the fuel duty's ladder: a cent a litre at founding prices, in thousands. |
| 1540 | `PolicyScreen.FUEL_INFO` | `"A duty a litre on the petrol the drivers buy at the pump, paid to the treasu...` | The fuel duty's (i). |
| 1547 | `PolicyScreen.FUEL_CAVEAT` | `"Struck on last month's litres at the pump: the owners who would take the bus...` | The fuel duty's caveat, whole. |
| 1591 | `PolicyScreen.PORT_DUES_PAGE` | `"Port dues"` | The Taxes area's page of the port dues dial (FR4; the dial and its collection are FR3's, TaxPolicy.getPortDues() and sectors.PortDivision). |
| 1594 | `PolicyScreen.PORT_DUES` | `"portDues"` | The staged key of the port dues dial (FR4). |
| 1597 | `PolicyScreen.PORT_DUES_STEP` | `TaxPolicy.MAX_PORT_DUES / 40` | One step of the port dues' ladder: a fortieth of the dial's top, so the default (1) and the top are both on a step. |
| 1611 | `PolicyScreen.PORT_DUES_CAVEAT` | `"Struck on last month's tonnes across the berths.The handling charge carries ...` | The port dues' caveat, whole. |
| 1665 | `PolicyScreen.PROPERTY_NOT` | `"Property is not on this dial: it is charged on value rather than on income, ...` | P6. |
| 1714 | `PolicyScreen.PROFIT_INFO` | `"Charged on what each sector earned before tax.A sector that lost money " + "...` | P8, the profit tax's head. |
| 1719 | `PolicyScreen.LOSS_INFO` | `"A sector that lost money paid no profit tax at all, which is why the " + "fi...` | P4. |
| 1723 | `PolicyScreen.SALES_INFO` | `"Charged on VALUE ADDED - what a sector sells, less the tax it already " + "p...` | P10, the sales tax's head. |
| 1729 | `PolicyScreen.WAGE_INFO` | `"Taken off every payroll in the city before the household sees it.The " + "jo...` | P12, the wage tax's head. |
| 1734 | `PolicyScreen.PROPERTY_INFO` | `"Charged on what land and buildings are assessed at, whether or not the " + "...` | P14, the property tax's head. |
| 1739 | `PolicyScreen.PROPERTY_STEP_INFO` | `"A twentieth of a point a step rather than the quarter the other three " + "g...` | P15. |
| 1744 | `PolicyScreen.REFUND_INFO` | `"\"In refund\" means a sector's credits on what it bought exceed the tax " + ...` | P11. |
| 1750 | `PolicyScreen.ROLL_INFO` | `"The power and water plants are the city's own and exempt.Raising what " + "l...` | P16. |
| 1755 | `PolicyScreen.SALES_SCALED` | `"Scaled, not recomputed: each sector's net remittance moves by the ratio of i...` | The sales tax's preview, scaled (the spec's D11, B6). |
| 1792 | `PolicyScreen.OFFSET_INFO` | `"A move is in POINTS off the tax's own rate, so a row left at zero is taxed "...` | P9, the offsets' note. |
| 1862 | `PolicyScreen.BANK_INFO` | `"The commercial bank's profit is taxed at Retail's rate - a Commercial Bank i...` | The bank's line (B5, D12). |
| 2104 | `PolicyScreen.PAYSLIP_INFO` | `"Only the first is a tax and only the first is set here - the other three " +...` | P13. |
| 2135 | `PolicyScreen.FARMLAND_INFO` | `"A field is worth what a developer would pay for it and grows what a " + "far...` | P17. |
| 2142 | `PolicyScreen.FARMLAND_CAVEAT` | `"Exact against today's land price.What it actually decides is whether " + "th...` | P19. |
| 2289 | `PolicyScreen.FLOOR_INFO` | `"Every wage in the city is a multiple of this number, so moving it moves " + ...` | P22, the floor's (i). |
| 2295 | `PolicyScreen.PINNED_WORDS` | `"At least one skill level is oversupplied AND cannot get any cheaper, so " + ...` | P23, both forms. |
| 2300 | `PolicyScreen.FREE_WORDS` | `"No skill level is pinned against the floor, so the labour market is " + "cle...` | ...the other: no band pinned. |
| 2305 | `PolicyScreen.FLOOR_CAVEAT` | `"Once wages have walked there, which takes about a year.Nothing about " + "th...` | P24. |
| 2453 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2456 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2459 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2462 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2465 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2468 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2494 | `PolicyScreen.OVER_INFO` | `"Hot money follows this difference in, and leaves the day it closes." + "It i...` | P28. |
| 2500 | `PolicyScreen.REAL_INFO` | `"The currency follows this one: a dial under the inflation people expect is a...` | P28's second half: the real differential - against the inflation people expect since 0.7.42 (the UI spec's B9). |
| 2506 | `PolicyScreen.REAL_DIAL_INFO` | `"The dial less the inflation people expect: what saving at the dial earns " +...` | The dial's own real rate (0.7.45): REAL_INFO's first half, for the dial alone. |
| 2660 | `PolicyScreen.HAND_INFO` | `"Jerus's autopilot: the rule can hold the dial.Every month, before anything i...` | P32. |
| 2664 | `PolicyScreen.REPRICE_INFO` | `"This does not reprice a single bond the city has already sold - every " + "c...` | P34: the dial card's caveat. |
| 2893 | `PolicyScreen.QE_LINE` | `"Buys term paper only: it reaches prices through what people own and through ...` | How QE reaches prices, in one plain line on the holdings card (0.7.45; the UI spec's D20). |
| 2926 | `PolicyScreen.CEILING_INFO` | `String.format("When the treasury runs dry the central bank advances the gap i...` | P36. |
| 2972 | `PolicyScreen.ANCHOR_INFO` | `"What people expect prices to do decides what they do next.Wages are asked " ...` | THE ANCHOR's (i): Expectations, in a player's words. |
| 2983 | `PolicyScreen.STRUCK_INFO` | `"Fees, build costs and upkeep, land, the shelf's floor, the pension's wage ba...` | The struck level's (i). |
| 2988 | `PolicyScreen.DRIFT_INFO` | `"The currency slides every month by the credible part of the " + "inflation p...` | THE CURRENCY'S DRIFT's (i). |
| 3129 | `PolicyScreen.BASKET_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST, ...` | The basket's five parts' colours, groceries to services: the money ramp, then the rest - areas, not verdicts. |
| 3175 | `PolicyScreen.SWING_INFO` | `"Prices here have more than doubled and come back at some point." + "Wages, r...` | P30. |
| 3180 | `PolicyScreen.REFORMED_INFO` | `"The index is measured against the city's first basket in founding money, " +...` | P31, once the money has been reformed. |
| 3389 | `PolicyScreen.COVER_INFO` | `"The rest is general revenue - the same pot the schools and the hospitals " +...` | P41 and P47. |
| 3558 | `PolicyScreen.EI_INFO` | `"EI only pays the first twelve months, so a long bust costs less in EI than "...` | P57. |
| 4001 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 4004 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 4044 | `PolicyScreen.SHARE_INFO` | `"The city's share of every course fee.Households pay the rest out of a " + "m...` | P48. |
| 4051 | `PolicyScreen.BURDEN_INFO` | `String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is...` | P49. |
| 4057 | `PolicyScreen.PRICE_INFO` | `"The founding tuition table times this, before the city's share comes off." +...` | P50. |
| 4065 | `PolicyScreen.SCHOOL_CAVEAT` | `"Against the courses being taken now, each kind's students at its own " + "pr...` | P51/P52: the schools' preview caveat. |
| 4416 | `PolicyScreen.LOAN_INFO` | `"Charged on a graduate's balance while they repay it, and on nothing while " ...` | P55. |
| 4422 | `PolicyScreen.LOAN_CAVEAT` | `"Against the balances the graduates owe today.The instalment itself does " + ...` | P56. |
| 4467 | `PolicyScreen.SUBSIDY_INFO` | `"A protected sector is topped up to break-even every month it loses money, " ...` | P69. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 90 | `final ZoningScreen zoning` | The Zoning area's pages (0.7.109, batch Z2): drawn there, staged and applied through this screen's set and tray. |
| 120 | `String policyArea` | The area open, one of AREAS, or null for the hub. |
| 123 | `String policyPage` |  |
| 155 | `final Set<String> openLines` | What the player has opened - a fold, a payer's own dial, the folded zero payers - by key, so a redraw on the clock leaves it open (Land's rule: kept while the game runs, not saved; the spec's D15). |
| 158 | `private final Map<String, Integer> flagSeen` | The month each biting flag was first drawn in, by its heading: a flag first seen this month carries NEW until the month turns. |
| 161 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 445 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 457 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 84 | 4419 | **type** `final class PolicyScreen` | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it would do beside it, on a hub of four area cards and the levers that are... |
| 92 | 4 | `PolicyScreen(UserInterface ui)` |  |

### POLICY (0.7.36): THE FRAME (lines 97-348)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 10 | `static String[] pagesOf(String area)` | The pages of an area, in their tabs' order. |
| 185 | 10 | `static String[] iconsOf(String area)` | ...and their icons. |
| 201 | 61 | `void showPolicyMenu()` | The tab's entry point: the hub, or the area and page open. |
| 264 | 6 | `void openHub()` | The hub, at its top. |
| 272 | 7 | `void open(String area, String page)` | An area, on its first page or the one named, at its top (every door on this tab, and the shell's). |
| 281 | 1 | `void openArea(String area)` | An area's first page: a chip in the head. |
| 284 | 1 | `void openPage(String page)` | A page of the open area: a tab. |
| 287 | 22 | `private void frameOver(VBox frame, VBox page, Region tray)` | The fixed frame over a scrolling page and, when one is given, the tray under it: the page as tall as what is left between them (BankScreen's frame, with a foot). |
| 318 | 16 | `static String areaInfo(String area)` | Each area's (i): what it holds (the landing's row blurbs until 0.7.36). |
| 341 | 7 | `HBox head()` | The head: "Policy" with the money blue's swatch, or the breadcrumb "Policy › Taxes" with "Policy" a way back to the hub; the areas (five since 0.7.109) as chips at its right, the open one lit (the spec's D14: the brea... |

### the four figures (lines 349-431)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 361 | 24 | `List<Cell> vitalCells()` | THE FOUR (pure: the probe reads them as the strip shows them). |
| 387 | 11 | `HBox vitals()` | The strip: the four as limit cells, each a door. |
| 400 | 7 | `static String ratesWords(TaxPolicy p)` | The rates in words: "15.00% on income · 1.50% a year on property", or the three once they have parted (B2: two places, as Money.pct2() says). |
| 409 | 4 | `static int needLevel(List<CityNeeds.Need> needs, CityNeeds.Kind kind)` | A NEEDS YOU row's level by its kind, in the list measured once a draw: 0 when it is not listed. |
| 415 | 3 | `static String levelTone(int level)` | A level's colour: plain, amber near the line, red past it. |
| 420 | 1 | `TaxPolicy policy()` | The city's policy. |
| 423 | 3 | `double taxRaised()` | Every tax line the city collected last month - CityNeeds' own sum since 0.7.24, which NEEDS YOU's TREASURY line reads too. |
| 428 | 3 | `int pinnedBands()` | Wage bands pinned to the floor with people spare in them - CityNeeds' count since 0.7.24, NEEDS YOU's WAGES line's. |

### THE STAGED SET. (lines 432-478)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 466 | 1 | `boolean isStaged(String key)` |  |
| 468 | 4 | `double staged(String key, double current)` |  |
| 473 | 1 | `void stage(String key, double value)` |  |
| 475 | 1 | `void unstage(String key)` |  |
| 477 | 1 | `void dropProposal()` |  |

### THE LADDER (lines 479-576)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 1 | `static String baseKey(String tax)` | The staged key of one income tax's own base (0.7.4): "base:profit", "base:sales" or "base:wage". |
| 522 | 9 | `Ladder ladderOf(Lever lever)` | A registered dial's Ladder, wired to the staged set: the lever goes in the register the tray names and applies from, the thumb sits at what is staged, and every move goes through propose(). |
| 533 | 4 | `static String stepWord(double step)` | How one step reads on a tax page's ends line: in points of the rate. |
| 538 | 3 | `static double bounded(double value, Lever lever)` |  |
| 549 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |
| 565 | 5 | `boolean changesAtCurrent(Lever lever)` | Whether a lever still changes something at the value it reads as current (0.7.4): "Every tax at once" once the three income bases have parted. |
| 572 | 4 | `String nowReads(Lever lever)` | What a lever reads today, in words: its current value, or "three rates" and "a price per school" for the every-tax and every-school levers once they have parted. |

### the staged set, through a copy (lines 577-681)

| line | len | member | says |
|---:|---:|---|---|
| 590 | 34 | `static void onCopy(TaxPolicy p, String key, double v)` | What a staged key sets, on any policy: the one place a key is read as a dial for the preview (0.7.36). |
| 626 | 1 | `TaxPolicy stagedPolicy()` | The city's policy with everything staged put through a copy - every key in the order it was moved, and the grant's basis and amount together. |
| 629 | 17 | `TaxPolicy stagedPolicyWith(String key, double value)` | ...and one key at a value of the caller's on top (a dial card's thumb, while it is dragged). |
| 648 | 5 | `TaxPolicy with(String key, double value)` | The city's policy with one key moved and nothing else: a dial with its own Apply previews its own move. |
| 655 | 4 | `TaxPolicy.GrantBasis stagedBasis(TaxPolicy live)` | The grant's basis as staged, else the city's. |
| 661 | 1 | `double stagedShare()` | The city's share of tuition as staged (Education's dial, not the policy's). |
| 664 | 3 | `PolicyPreview.Budget stagedBudget()` | THE BUDGET under everything staged: PolicyPreview's (M8). |
| 669 | 5 | `Effect budgetEffect(TaxPolicy after, double share)` | THE BUDGET as an effect row, before and after a policy and a share of the caller's. |

### the tray (lines 682-745)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 24 | `Region tray()` | The staged tray (the spec's 3.0): on the pages whose dials batch - the taxes' and the schools' - once anything is staged: a chip a change that comes out with its ×, THE BUDGET before and after the set, and Apply and D... |
| 724 | 1 | **type** `record TrayItem(String key, String words)` | One staged change as the tray lists it: its key, and "The wage rate 15.00% → 16.00%". |
| 727 | 9 | `List<TrayItem> trayItems()` | Each staged change the tray lists, in the order moved (pure: the probe reads them). |
| 738 | 7 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### a dial that keeps its own Apply (lines 746-805)

| line | len | member | says |
|---:|---:|---|---|
| 768 | 10 | `Ladder ownLadder(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> label)` | The lever itself: a Ladder (0.7.6) that STAGES a change rather than making one, for a dial that keeps its own Apply - the wage floor, the policy rate, the pension's two, EI's two, the clinic's two, the fare on Infrast... |
| 784 | 16 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back (0.7.34's action button since 0.7.36: a GO in the money blue that says what it sets, and "Leave it as it is" beside it). |
| 802 | 3 | `List<Node> applyFoot(String key, String label, Runnable apply)` | The foot of a card whose dial keeps its own Apply: the bar once its key is staged, nothing before. |

### the screen's own pieces (lines 806-1004)

| line | len | member | says |
|---:|---:|---|---|
| 809 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 818 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 830 | 5 | `String struckWords(String what)` | A Pe-struck price's sentence for an (i) (0.7.45; the UI spec's D13): the level its founding figure is struck at this month - never set against the index, which an old save's level of 1 would misread. |
| 837 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. |
| 856 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 865 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 871 | 9 | `static VBox doorCard(VBox c, Runnable go)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 882 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 893 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 900 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 905 | 4 | `static Node line(String text, String whole)` | A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. |
| 911 | 1 | `static Label muted(String text)` | ...muted. |
| 914 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut. |
| 921 | 5 | `static FlowPane chips(Node...cs)` | A row of chips, wrapping. |
| 928 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 936 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 941 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 955 | 10 | `static HBox split(Region first, Region second, double share)` | Two cards side by side, the first `share` of the width (the floor's dial and the city's payroll). |
| 967 | 7 | `Region spark(double[] all, int n, String colour, double width, double height)` | The last `n` months of a History series, for a sparkline - BankScreen's. |
| 976 | 1 | `Sector sector(String key)` | A sector by its key, or null. |
| 979 | 1 | `static String ptsMove(double d)` | A signed share in points: "+1.00 pts". |
| 982 | 1 | `static String moneyMove(double d)` | Money with its sign, as a move: "+$2.5M", "−$1.2M", "$0". |
| 985 | 4 | `static String moneyMoveFull(double d)` | ...every digit, for one household's or one person's money: "+$173", "−$41", "$0" under half a dollar. |
| 991 | 4 | `static String amount(double v)` | Money that can be below nothing - a sector in refund, a surplus where a cost was - with a true minus: "$2.5M", "−$365k". |
| 997 | 1 | `static String rateMove(double d)` | A rate's move in points: "+0.25 points". |
| 1000 | 1 | `static String pct0(double share)` | A percentage to none: "45%". |
| 1003 | 1 | `static String times(double scale)` | "x1.00". |

### THE HUB (0.7.36; the landing's four rows until then) (lines 1005-1088)

| line | len | member | says |
|---:|---:|---|---|
| 1018 | 37 | `void hubPage(VBox page)` |  |
| 1061 | 9 | `List<DecisionLog.Entry> recentDecisions(int n)` | The last `n` decisions of the kinds this tab makes - taxes, promises, the central bank, the currency, zoning - newest first. |
| 1072 | 5 | `DecisionLog.Entry lastDecision(String kind)` | The last decision of one kind, or null. |
| 1078 | 5 | `static boolean isPolicyKind(String kind)` |  |
| 1085 | 3 | `static String decisionWords(DecisionLog.Entry e)` | A decision as a chip: "m1,204 · Taxes to 15%". |

### the area cards (lines 1089-1206)

| line | len | member | says |
|---:|---:|---|---|
| 1092 | 9 | `VBox areaCard(String svg, String title, String figure, Node picture, String line, String foot, Runnable go)` | One area card: its icon and title with "›", its figure, a small picture, one line and a foot - the whole a door. |
| 1106 | 4 | `double[] taxesBooked()` | The four taxes last month, profit · sales · wage · property, as NationalAccounts booked them. |
| 1114 | 9 | `VBox taxesCard()` |  |
| 1124 | 12 | `VBox wagesCard()` |  |
| 1137 | 22 | `VBox moneyCard()` |  |
| 1160 | 18 | `VBox promisesCard()` |  |
| 1180 | 4 | `static String promisesDoor(PolicyPreview.Promises p)` | Where the PROMISES card opens (0.7.45): Food when the vouchers are the largest slice, else Pensions. |
| 1186 | 8 | `static SegmentBar sliceBar(List<Slice> slices)` | Slices as one bar filling its card, each part's name and money its tooltip (a part below nothing draws nothing). |
| 1196 | 4 | `static String areaIcon(String area)` | An area's icon, as its chip has it. |
| 1202 | 4 | `static String signedPct1(double v)` | Inflation as a signed share to one place, with a true minus: "+0.2%". |

### what is biting (lines 1207-1391)

| line | len | member | says |
|---:|---:|---|---|
| 1214 | 1 | **type** `record Flag(String tone, String heading, String body, String area, String page, String door)` | A lever that is forcing something: its colour (a verdict), its heading and what it means, and where it is undone - the area and page its door opens, and the door's words. |
| 1225 | 143 | `List<Flag> policyFlags()` | The levers that are currently forcing something. |
| 1370 | 3 | `static String capitalised(String word)` | "sales" to "Sales", for a flag that opens with a tax's name. |
| 1375 | 16 | `VBox flagCard(Flag f, boolean isNew)` | A biting lever as a card: its alert in its verdict's colour, its heading (NEW the month it is first seen), its first sentence with the rest behind an (i), and a door to where it is undone. |

### TAXES (0.7.36; THE FOUR TAXES until then) (lines 1392-1426)

| line | len | member | says |
|---:|---:|---|---|
| 1422 | 4 | `TaxPolicy previewFor(String key, double v, double current, double step)` | The policy a card previews at a thumb's value: everything staged, with the card's key at `v` - or exactly what is staged while the thumb rests where nothing of its own is staged (a parted every-tax dial at its current... |

### EVERYTHING (lines 1427-1530)

| line | len | member | says |
|---:|---:|---|---|
| 1435 | 26 | `void everythingPage(VBox page)` |  |
| 1463 | 35 | `VBox takeCard(double[] booked, TaxPolicy live)` | THE TAX TAKE: last month's four as one bar, a click on a part its page; and once anything is staged, the four at the staged rates, a moved part a ghost with its move over it. |
| 1500 | 10 | `double[][] taxHistory()` | Ten years of each tax from City History, profit · sales · wage · property; the profit line is the business and the food industry together, as the take's first part is. |
| 1512 | 12 | `VBox taxCard(int i, double raised, double all, String rate, double[] history, boolean atStop, String info)` | One tax as a card: its icon and name, its rate, what it raised and its share, its ten years, a chip at its legal stop - the whole a door to its page. |

### FUEL (0.7.103) (lines 1531-1587)

| line | len | member | says |
|---:|---:|---|---|
| 1551 | 7 | `void fuelPage(VBox page)` | THE FUEL DUTY (0.7.103): its month, and its dial, staged into the tray with the taxes. |
| 1560 | 13 | `VBox fuelDutyCard(TaxPolicy live)` | The fuel duty's dial card: the duty a litre in today's money, what it would raise, the pump's litre and THE BUDGET. |
| 1575 | 12 | `DoubleFunction<List<Effect>> fuelDutyEffects(TaxPolicy live)` | The fuel duty's effects at any value of its thumb (pure: the probe reads them): the duty a month, a litre at the pump, THE BUDGET. |

### PORT DUES (FR4) (lines 1588-1710)

| line | len | member | says |
|---:|---:|---|---|
| 1600 | 9 | `static String portDuesInfo()` | The port dues' (i): what the dues are, who pays and who they pass to, what the default is and what the dial does. |
| 1616 | 8 | `void portDuesPage(VBox page)` | THE PORT DUES (FR4): last month's, and the dial, staged into the tray with the taxes. |
| 1626 | 15 | `VBox portDuesCard(TaxPolicy live)` | The port dues' dial card: the multiple of the default due, what the month's tonnes would pay, the handling charge a tonne by kind and THE BUDGET. |
| 1643 | 20 | `DoubleFunction<List<Effect>> portDuesEffects(TaxPolicy live)` | The port dues' effects at any value of its thumb (pure: the probe reads them): the dues a month, the handling a tonne of each kind with a berth, THE BUDGET. |
| 1676 | 19 | `VBox everyTaxCard(TaxPolicy live)` | EVERY TAX AT ONCE (0.7.4; a dial card since 0.7.36) - what the one city rate became once each income tax had its own. |
| 1697 | 13 | `DoubleFunction<List<Effect>> everyTaxEffects(TaxPolicy live)` | Every tax at once's effects at any value of its thumb (pure: the probe reads them): the three income taxes and THE BUDGET. |

### PROFIT, SALES, WAGE, PROPERTY (lines 1711-2260)

| line | len | member | says |
|---:|---:|---|---|
| 1761 | 5 | `static String baseInfo(String offsets)` | P7, a base rate's (i): what moves off it. |
| 1768 | 22 | `void taxPage(VBox page, String which)` | One tax's page: its base on a dial card, who pays it ranked with each row's own move, the payslip (wage) or the farmland (property), and the old table under "details". |
| 1799 | 38 | `VBox baseCard(String which, TaxPolicy live)` | A tax's base on a dial card: what it raises and THE BUDGET before and after (and, for profit, the bank at Retail's rate - B5). |
| 1839 | 21 | `DoubleFunction<List<Effect>> baseEffects(String which, String key, double current, double step, TaxPolicy live)` | A tax's base's effects at any value of its thumb (pure: the probe reads them): the tax a month, the bank on Profit, THE BUDGET. |
| 1868 | 3 | **type** `record Payer(String key, Lever lever, String svg, String name, double paid, String figure, double rateNow, ...` | One payer of a tax: its row's key and lever (null: no dial - the bank), its icon and name, what it paid last month, its figure's words, its rate now and staged, its offset, a chip (null: none), and its line of the tak... |
| 1873 | 78 | `List<Payer> payersOf(String which, TaxPolicy live)` | The payers of a tax (pure: the probe reads them): every sector, or every band, with what the take strikes for each at the city's dials and the staged ones; the bank last on Profit. |
| 1953 | 3 | `static boolean paidNothing(Payer p)` | Whether a payer paid nothing to speak of and is folded into one row (a sector in refund is not: it is owed). |
| 1958 | 26 | `VBox payers(String which, TaxPolicy live)` | WHO PAYS: the payers ranked by what they paid, a row opening into its own move; those that paid nothing folded into one muted row that opens to the same rows (the spec's D15). |
| 1986 | 7 | `void addPayer(VBox list, Payer p, double scale, String which)` | A payer's row, and its own dial under it while it is open. |
| 2001 | 58 | `HBox payerRow(Payer p, double scale, boolean shown, Runnable toggle)` | One payer: its icon and name ("▸ its own move" under a name that opens), what it paid as a bar - with the staged take as a ghost bar under it once anything staged reaches it - its figure, and its rate as a chip ("15.0... |
| 2061 | 11 | `VBox offsetCard(Payer p, String which)` | A payer's own move, opened in place: its offset's dial, and that payer's tax and THE BUDGET before and after. |
| 2074 | 28 | `DoubleFunction<List<Effect>> offsetEffects(Payer p, String which)` | A payer's own move's effects at any value of its thumb (pure: the probe reads them): its tax, the bank beside Retail's, THE BUDGET. |
| 2111 | 22 | `VBox payslipCard(TaxPolicy live)` | THE PAYSLIP (T12): what a wage taxed at the wage rate loses, as a bar of the wage in four parts, and each band moved off the rate with its own total; the door to the promises' dials. |
| 2158 | 14 | `VBox farmlandCard(TaxPolicy live)` | The one dial that decides whether the city keeps its fields (T15). |
| 2174 | 16 | `DoubleFunction<List<Effect>> farmlandEffects(TaxPolicy live)` | The farmland relief's effects at any value of its thumb (pure: the probe reads them). |
| 2192 | 68 | `Node taxTable(String which, TaxPolicy policy)` | The old table of a tax's page (T9-T11, T14), behind "details": every sector or band with its figures as they were drawn. |

### WAGES - the floor (lines 2261-2419)

| line | len | member | says |
|---:|---:|---|---|
| 2309 | 17 | `void floorPage(VBox page)` |  |
| 2328 | 29 | `Node wageLadder(LabourMarket market, PopulationManager people, double floor, double want, boolean moved)` | The eleven jobs under their bands on one scale, the floor a rule across them; a band's chip says whether it is pinned (LabourMarket.isPinned(), as the model has it). |
| 2359 | 17 | `VBox floorCard(LabourMarket market, double floor)` | THE FLOOR on its dial card: read in today's money, set in founding money (D6); the floor, the unskilled wage, every wage and the city's payroll before and after. |
| 2378 | 12 | `DoubleFunction<List<Effect>> floorEffects(LabourMarket market)` | The floor's effects at any value of its thumb (pure: the probe reads them). |
| 2392 | 10 | `VBox cityPayrollCard()` | What the city itself pays (W4), neutral (B14: a payroll is not bad news by being one). |
| 2404 | 15 | `Node floorTable(LabourMarket market, double want, boolean moved)` | The ladder as a table (W2), behind "details", in today's money: each job's multiple of the floor, what it is paid, and where it heads at a staged floor. |

### MONEY - the policy rate (lines 2420-2827)

| line | len | member | says |
|---:|---:|---|---|
| 2471 | 3 | `static String monthsWords(double months)` | A ceiling as the ladder reads it: "1 month", "6 months". |
| 2476 | 3 | `static double halfPoint(double target)` | A target on the half-point grid, so a run of steps lands on 3% and not on 3.0000000000000004%. |
| 2481 | 11 | `String rateInfo()` | P25, the dial's line, and P26 corrected (the spec's B16: the city's paper is priced from the higher of the dial and the bank's cost of funds, not "at the dial"). |
| 2511 | 28 | `void ratePage(VBox page)` |  |
| 2547 | 21 | `Node priceRateChart()` | Behind the PRICES strip's "details" (the spec's R4): City History's price level (the index, left axis) and policy rate (percent, right axis) on one small chart - no legend, so the key under it names them. |
| 2570 | 9 | `static VBox rateCell(String label, String value, String line, String info)` | A cell under the rate line: its label, its figure, a line, and the paragraph behind an (i). |
| 2581 | 1 | **type** `record RateMark(double at, String name, String colour, boolean dashed, String tip)` | One rate on the line, worked out without drawing it: where, its name and colour, dashed or not, and its tooltip. |
| 2589 | 49 | `List<RateMark> rateMarks(double rate, double want, boolean moved)` | THE RATE LINE's marks (pure: the probe reads them): inflation (amber only past a point from the target, the old page's line), the target, the dial, the rule, savers, the city, prime and the world - and, staged, the di... |
| 2640 | 18 | `Node rateLine(double rate, double want, boolean moved)` | THE RATE LINE: the dial as a bar, every other rate a named rule across it, on 0 to a quarter past the highest (never under 3%). |
| 2670 | 1 | `static String spendPct(double v)` | The spend factor to one place: the dial reaches it only through the savers' rate, a few tenths of a point at most, which a whole percent would round away. |
| 2673 | 9 | `String spendInfo()` | P29. |
| 2692 | 29 | `VBox theDialCard(DebtManager market, PriceIndex px, double rate, double want)` | THE DIAL (R5, R7): the ladder with the rule's rate marked on it, the thirteen chips and "do what the rule says", whose hand it is in; what a move does to the city's rate, the savers', prime and what households spend -... |
| 2723 | 13 | `DoubleFunction<List<Effect>> dialEffects(double rate)` | The policy rate's effects at any value of its thumb (pure: the probe reads them). |
| 2744 | 77 | `VBox ruleCard(DebtManager market, PriceIndex px)` | THE RULE (R6): what it aims at - the target, set at once by its chips and its ladder (0.7.4: it moves no price this month, only what the rule says) - and what it says at two targets, and inflation against the target a... |
| 2823 | 1 | `static String plain(double v)` | A weight or a move in points as it is: "1.5", "1.375", "2" - one place would read 1.375 as 1.4. |
| 2826 | 1 | `static String pointsOf(double v)` | ...as points: "1 point", "1.25 points". |

### HOW STRICT (0.7.52): the dial beside the target - DebtManager's HOW STRICT (lines 2828-2960)

| line | len | member | says |
|---:|---:|---|---|
| 2831 | 12 | `static String strictInfo()` | HOW STRICT's (i): what each end does, from the model's own numbers. |
| 2845 | 4 | `static String strictWords(PolicyPreview.RuleAt r)` | The preview's words at a step (pure: the probe reads them): "Very strict: aims under 2.0%, at 1.0% · on target it sets 4.00% · at this inflation 5.10%". |
| 2851 | 1 | `static DebtManager.Strictness strictAt(double v)` | The step under a ladder's thumb. |
| 2859 | 13 | `VBox strictPart(DebtManager market)` | HOW STRICT (0.7.52): Jerus's dial beside the target, its five steps on a Ladder set at once like the target's, and under it what the bank would aim at and what the rule would set at the step under the thumb (PolicyPre... |
| 2874 | 17 | `String holdingsInfo()` | P35, both layers. |
| 2897 | 19 | `VBox holdingsCard(DebtManager market)` | THE CENTRAL BANK's holdings (R8): the share of the term paper it aims to hold, set at once; the thirty-year rate once the book has moved there (M9). |
| 2918 | 6 | `DoubleFunction<List<Effect>> holdingsEffects()` | The holdings' effects at any value of its thumb (pure: the probe reads them). |
| 2935 | 18 | `VBox ceilingCard()` | THE CENTRAL BANK's ceiling (R9): how far it will advance the treasury, set at once; the ceiling in money at another setting (M10); what is owed, red only when it binds (ceilingBound(): a verdict). |
| 2955 | 5 | `DoubleFunction<List<Effect>> ceilingEffects()` | The ceiling's effects at any value of its thumb (pure: the probe reads them). |

### THE ANCHOR AND THE DRIFT (0.7.45) (lines 2961-3125)

| line | len | member | says |
|---:|---:|---|---|
| 2969 | 1 | `static boolean offTarget(Expectations e, double target)` | True when what trust is judged on - inflation smoothed over a year - is off the target by more than Expectations.TOLERANCE: the rate line's inflation tick and the rule's band go amber (the UI spec's D4). |
| 2995 | 2 | **type** `record AnchorWords(String figure, double trust, String trustWords, String move, String moveTone, String lea...` | The anchor's words, worked out without drawing them (pure: the probe reads them). |
| 2998 | 31 | `AnchorWords anchorWords()` |  |
| 3038 | 16 | `static String notLeaningWords(Game g, double miss)` | Why the month's lean is nothing, on the model's own condition (Expectations.takeMonth()): the lean counts only when the advice that holds the target - the Standard rule's at any strictness since 0.7.52 (DebtManager.ho... |
| 3056 | 5 | `static String stepPts(double step)` | A month's move in trust, in points, signed with a true minus: "+0.2 pts", "−0.91 pts" (two places under a twentieth of a point). |
| 3063 | 1 | `static String rate2(double rate)` | A yearly rate to two places with a true minus: "2.15%", "−0.40%". |
| 3073 | 16 | `VBox anchorCard()` | THE ANCHOR (the UI spec's 2.2 item 2): what people expect; how far they trust the bank, as a gauge from nothing to all with the least and the most it can be; how trust moved this month and whether the rate leans again... |
| 3091 | 1 | **type** `record DriftWords(String figure, String line, String uip)` | The drift's words, worked out without drawing them (pure: the probe reads them). |
| 3093 | 16 | `DriftWords driftWords()` |  |
| 3111 | 4 | `static String parityWords(double deviation)` | Where the currency stands against parity, in words: "25.9% weaker than parity", "25.2% stronger", "at parity". |
| 3117 | 8 | `VBox driftCard()` | THE CURRENCY'S DRIFT (the UI spec's 2.2 item 2): the slide the anchor hands the currency, what it is struck from, and where the real rate alone would hold it. |

### THE BASKET (0.7.45) (lines 3126-3235)

| line | len | member | says |
|---:|---:|---|---|
| 3132 | 1 | **type** `record BasketPart(String name, double weight, String colour, String words)` | One part of the basket: its name, its weight, its colour and what its key says. |
| 3140 | 25 | `List<BasketPart> basketParts()` | The basket in force, part by part (pure: the probe reads them): each component's weight (PriceIndex.getWeight()) and its own inflation over the year off History's line for it (YearBook.componentInflation()) - or, with... |
| 3167 | 6 | `String basketWords()` | What the PRICES card says of the basket in force: when it was struck, or that it is still to be. |
| 3190 | 45 | `VBox pricesCard(PriceIndex px)` | THE PRICES (R4): the index since founding; where it has been - the dearest and the cheapest, on a bar, today marked (Jerus, 2026-09-14: "something as well that stores the highest price index and lowest") - once the tw... |

### MONEY - the currency reform (lines 3236-3365)

| line | len | member | says |
|---:|---:|---|---|
| 3253 | 7 | `String reformInfo()` | P37. |
| 3275 | 65 | `void reformPage(VBox page)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 3342 | 6 | `String foreignInfo()` | P40. |
| 3350 | 7 | `static VBox priceTag(String what, String before, String after)` | One price tag: what it is, today, and after the reform in the accent. |
| 3359 | 6 | `static String afterName(Denomination unit, double factor, Currency money)` | What the city's money would be called after lopping by this factor. |

### PROMISES - the pension (lines 3366-3540)

| line | len | member | says |
|---:|---:|---|---|
| 3398 | 1 | **type** `record Month(String word, String says, String tone)` | What a household's month means, in one of three forms (0.7.27, P42): its chip's word, its sentence and its verdict colour. |
| 3401 | 15 | `Month pensionerMonth()` | A pensioner household's month (pure: the probe reads it). |
| 3417 | 46 | `void pensionPage(VBox page)` |  |
| 3465 | 12 | `VBox contributionCard(EconomyManager em, TaxPolicy policy)` | WHAT WORKERS PAY IN (P-4): the contribution, its own Apply; collected, covered, out of the treasury and a payslip before and after (M3: struck on the wage bill, so a dial at nothing previews something - B9). |
| 3479 | 16 | `DoubleFunction<List<Effect>> contributionEffects(TaxPolicy policy)` | The contribution's effects at any value of its thumb (pure: the probe reads them). |
| 3497 | 19 | `VBox pensionDialCard(EconomyManager em, TaxPolicy policy, HouseholdBalance bal)` | WHAT SENIORS RECEIVE (P-5): the pension, of the FOUNDING unskilled wage (B10, D9), its own Apply; each senior, paid, out of the treasury and what one pensioner household would have. |
| 3518 | 22 | `DoubleFunction<List<Effect>> pensionEffects(TaxPolicy policy)` | The pension's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the out of work and the students (2026-09-11) (lines 3541-3594)

| line | len | member | says |
|---:|---:|---|---|
| 3564 | 3 | `static String cost(double v)` | Money a line takes out of the treasury, a surplus with a true minus: "$2.5M", "−$1.0M". |
| 3568 | 26 | `void outOfWorkPage(VBox page)` |  |

### PROMISES - food (0.7.45; the UI spec's 2.3 and D6) (lines 3595-3758)

| line | len | member | says |
|---:|---:|---|---|
| 3607 | 7 | `void foodPage(VBox page)` |  |
| 3616 | 6 | `String foodStatus(double share)` | The dial card's status (pure: the probe reads it): who a voucher would go to at the last sale's price. |
| 3631 | 15 | `VBox foodAssistanceCard(TaxPolicy policy)` | FOOD ASSISTANCE (0.7.43; its own tab since 0.7.45), its own Apply: the treasury's voucher toward an eligible household's baskets - what it would have paid at the last sale, the households who would hold one, the baske... |
| 3648 | 11 | `DoubleFunction<List<Effect>> foodEffects(TaxPolicy policy)` | The food assistance dial's effects at any value of its thumb (pure: the probe reads them). |
| 3661 | 1 | **type** `record AidRow(int row, String name, double got, String gotWords, String figures)` | One row of WHO WOULD GET IT, worked out without drawing it (pure: the probe reads them). |
| 3664 | 18 | `List<AidRow> aidRows()` | WHO WOULD GET IT's rows: every household row a voucher would reach or did, most eligible first. |
| 3684 | 4 | `static String basketShare(double share)` | Baskets got per basket needed, as a row says it: "got .58 of a basket", "got every basket". |
| 3690 | 18 | `VBox whoWouldGetIt()` | WHO WOULD GET IT (the UI spec's 2.3 item 3): a row a household kind, its baskets got per basket needed, who a voucher reaches. |
| 3710 | 11 | `VBox eiPremiumCard(EconomyManager em, TaxPolicy policy)` | What workers pay for EI (E-3), its own Apply: raised, and what EI and the grants take from the treasury. |
| 3723 | 8 | `DoubleFunction<List<Effect>> eiPremiumEffects(TaxPolicy policy)` | The EI premium's effects at any value of its thumb (pure: the probe reads them). |
| 3733 | 13 | `VBox eiBenefitCard(EconomyManager em, TaxPolicy policy, Unemployment u)` | What EI replaces (E-3), its own Apply: the pool's bill, a claimant's cheque and the treasury's share, struck on the pool (M5). |
| 3748 | 10 | `DoubleFunction<List<Effect>> eiBenefitEffects(TaxPolicy policy)` | The EI benefit's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the clinic's price, and a premium (2026-09-19) (lines 3759-3955)

| line | len | member | says |
|---:|---:|---|---|
| 3784 | 1 | **type** `record Corner(String word, String says)` | The corner the city is in (P62): its chip's word, and its sentence. |
| 3786 | 23 | `Corner corner()` |  |
| 3811 | 8 | `String coverWords()` | P63's line: what the fees and the premium cover - or that the service cost nothing (B19: it read "Fees cover 0% of the cost" in a city with fees and no cost). |
| 3820 | 40 | `void healthPage(VBox page)` |  |
| 3862 | 16 | `String turnedAwayInfo()` | P64, both layers. |
| 3880 | 26 | `VBox feeCard(Healthcare care, TaxPolicy policy)` | WHAT A PATIENT PAYS (S-4), its own Apply: the break-even marked on the dial, the three fees, the fees at full and the treasury's share - at full service both sides (B13). |
| 3908 | 18 | `DoubleFunction<List<Effect>> feeEffects(TaxPolicy policy)` | The care fee's effects at any value of its thumb (pure: the probe reads them). |
| 3928 | 17 | `VBox healthPremiumCard(EconomyManager em, Healthcare care, TaxPolicy policy)` | WHAT EVERY WAGE PAYS (S-5), its own Apply: raised, and the treasury's share. |
| 3947 | 8 | `DoubleFunction<List<Effect>> healthPremiumEffects(TaxPolicy policy)` | The health premium's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the schools: the price of a place, who pays it, and the (lines 3956-4451)

| line | len | member | says |
|---:|---:|---|---|
| 4007 | 1 | `static String schoolKey(EducationType kind)` | The staged key of one school kind's own price (0.7.6): "tuitionScale:UNIVERSITY". |
| 4010 | 1 | `static String scaleWords(double scale)` | A tuition scale as the page writes it: "x1.00". |
| 4013 | 13 | `static String grantWords(TaxPolicy.GrantBasis basis, double amount, double level)` | The grant in words, for a line that names it: "15% of an unskilled wage a month". |
| 4028 | 8 | `static String basisName(TaxPolicy.GrantBasis basis)` | What a basis is called on its chip. |
| 4038 | 4 | `static String amountWords(TaxPolicy.GrantBasis basis, double amount, double level)` | How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. |
| 4071 | 3 | `double stagedScaleOf(TaxPolicy policy, EducationType kind)` | The scale a preview prices one kind of school at: its own lever if staged, else every school at once if that is, else what it is (0.7.6). |
| 4075 | 34 | `void schoolsPage(VBox page)` |  |
| 4111 | 5 | `static List<EducationType> schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (TaxPolicy's own list, which it keeps to itself). |
| 4118 | 4 | `Lever register(Lever lever)` | A dial on this page, registered so the tray can name and apply it. |
| 4124 | 4 | `Lever kindLever(EducationType kind, TaxPolicy policy)` | One kind's own price, as a lever. |
| 4130 | 2 | **type** `record Course(EducationType kind, double now, double then, boolean standing, double places, double students...` | One course's row, worked out without drawing it: its burden now and staged, whether a school of it stands, and its four figures. |
| 4134 | 11 | `List<Course> courses(Education schools, TaxPolicy policy, double[] places)` | The nine courses (pure: the probe reads them): what a place costs a family as a share of a month's wage, at the city's price and share and at the staged ones (PolicyPreview.schoolBurden()). |
| 4147 | 14 | `VBox burdenRows(Education schools, TaxPolicy policy, double[] places)` | WHO CAN AFFORD A PLACE: the nine as bars on 0 to 100% of a month's wage with a tick at MAX_BURDEN, red past it; each opens into its own price. |
| 4163 | 44 | `HBox courseRow(Course c, double scale, boolean shown, Runnable toggle)` | One course: its name ("▸ its price"), its bar - red past the line where nobody enrols - with the staged burden a ghost, its figure, and "no school" when none stands. |
| 4215 | 28 | `VBox kindCard(Course c, TaxPolicy policy)` | One kind's own price, opened in place (K-3): its dial - greyed with nothing standing, since a price for a school the city does not have moves nothing today, though every school at once still sets it - its places, stud... |
| 4245 | 16 | `DoubleFunction<List<Effect>> kindEffects(Course c, TaxPolicy policy)` | A kind's own price's effects at any value of its thumb (pure: the probe reads them). |
| 4263 | 7 | `VBox shareCard(Lever lever, Education schools, TaxPolicy policy)` | The city's share of tuition (K-1, registered): what households pay and what the city waives before and after, and THE BUDGET. |
| 4272 | 14 | `DoubleFunction<List<Effect>> shareEffects(TaxPolicy policy)` | The city's share's effects at any value of its thumb (pure: the probe reads them). |
| 4288 | 11 | `VBox schoolsMonthCard(Education schools)` | THE SCHOOLS THIS MONTH (K-2), footing: staff and buildings, less what households paid, is the net cost; the tuition waived beside it in grey, outside the sum (B17, D13). |
| 4301 | 14 | `VBox everySchoolCard(Lever lever, TaxPolicy policy)` | Every school at once (K-3, registered): its dial ("a price per school" until moved, once the nine have parted), what the courses are billed and households pay, and THE BUDGET. |
| 4317 | 13 | `DoubleFunction<List<Effect>> everySchoolEffects(TaxPolicy policy)` | Every school's price's effects at any value of its thumb (pure: the probe reads them). |
| 4332 | 16 | `String grantInfo(TaxPolicy policy)` | P53, with its figures. |
| 4356 | 41 | `VBox grantCard(TaxPolicy policy)` | What a student is granted (K-5, registered): the basis as chips - a chip stages the basis AND today's grant re-expressed in its unit, so the switch alone changes nothing until the amount moves - and the amount's dial ... |
| 4399 | 15 | `DoubleFunction<List<Effect>> grantEffects(TaxPolicy policy)` | The grant's amount's effects at any value of its thumb (pure: the probe reads them). |
| 4427 | 14 | `VBox loanCard(Lever lever, TaxPolicy policy)` | What the loan costs them afterwards (K-6, registered): what is owed and repaid, and a month's interest and THE BUDGET before and after (HouseholdBalance.studentInterestAt()). |
| 4443 | 8 | `DoubleFunction<List<Effect>> loanEffects(TaxPolicy policy)` | The loan rate's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the standing subsidies (lines 4452-4502)

| line | len | member | says |
|---:|---:|---|---|
| 4474 | 28 | `void subsidyPage(VBox page)` |  |


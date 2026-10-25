# PolicyScreen.java - 5,320 lines · 270 methods · 94 constants · interface

`ham/citybuildersim/ui/PolicyScreen.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> rate and the promises' dials keep an Apply on their own card, as since XR2
> (0.8.22) does each card of Money's currency page; the target
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

**Uses:** [Palette](Palette.md) (422), [TaxPolicy](TaxPolicy.md) (148), [Icons](Icons.md) (99), [ForeignAccounts](ForeignAccounts.md) (99), [PolicyPreview](PolicyPreview.md) (96), [DebtManager](DebtManager.md) (86), [Money](Money.md) (40), [Ladder](Ladder.md) (32), [Education](Education.md) (20), [EducationType](EducationType.md) (18), [PriceIndex](PriceIndex.md) (17), [WageBand](WageBand.md) (17), [DecisionLog](DecisionLog.md) (17), [EconomyManager](EconomyManager.md) (16), [TimeChart](TimeChart.md) (16), [CityNeeds](CityNeeds.md) (14), [Expectations](Expectations.md) (13), [Currency](Currency.md) (13), [HouseholdBalance](HouseholdBalance.md) (12), [LabourMarket](LabourMarket.md) (11), [Sector](Sector.md) (10), [CareType](CareType.md) (10), [JobType](JobType.md) (9), [Pieces](Pieces.md) (9), [Denomination](Denomination.md) (9), [CentralBank](CentralBank.md) (8), [HistorySave](HistorySave.md) (7), [Healthcare](Healthcare.md) (7), [HistoryScreen](HistoryScreen.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (6)... and 23 more

**Used by (10):** [BuildScreen](BuildScreen.md), [FinancesScreen](FinancesScreen.md), [GovernmentScreen](GovernmentScreen.md), [InfrastructureScreen](InfrastructureScreen.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 98 | POLICY (0.7.36): THE FRAME |
| 354 | · the four figures |
| 437 | THE STAGED SET. |
| 484 | THE LADDER |
| 582 | · the staged set, through a copy |
| 687 | · the tray |
| 751 | · a dial that keeps its own Apply |
| 811 | · the screen's own pieces |
| 1010 | THE HUB (0.7.36; the landing's four rows until then) |
| 1094 | · the area cards |
| 1212 | · what is biting |
| 1242 | · · the wage floor |
| 1256 | · · who is shut out |
| 1267 | · · tuition |
| 1289 | · · pensions |
| 1298 | · · subsidies |
| 1312 | · · the price of money |
| 1330 | · · trust in the bank (0.7.45) |
| 1342 | · · the peg (XR2, the study's alerts) |
| 1360 | · · the money itself |
| 1368 | · · at the stops |
| 1415 | TAXES (0.7.36; THE FOUR TAXES until then) |
| 1450 | · EVERYTHING |
| 1554 | · FUEL (0.7.103) |
| 1611 | · PORT DUES (FR4) |
| 1734 | · PROFIT, SALES, WAGE, PROPERTY |
| 2284 | WAGES - the floor |
| 2443 | MONEY - the policy rate |
| 2879 | · HOW STRICT (0.7.52): the dial beside the target - DebtManager's HOW STRICT |
| 3012 | · THE ANCHOR AND THE CURRENCY'S LEVEL (0.7.45) |
| 3175 | · THE BASKET (0.7.45) |
| 3285 | MONEY - the currency (XR2, 2026-10-22) |
| 3406 | · the pieces the currency page is built from |
| 3485 | · HOW THE D$ IS SET |
| 3588 | · THE PEG |
| 3741 | · THE BAND |
| 3831 | · WHO BUYS AT THE STRONG EDGE |
| 3884 | · WHAT THE DIAL AIMS AT |
| 3968 | · THE DIAL UNDER A PEG |
| 4021 | · BUY OR SELL THE D$ |
| 4054 | MONEY - the currency reform |
| 4184 | PROMISES - the pension |
| 4359 | PROMISES - the out of work and the students (2026-09-11) |
| 4413 | PROMISES - food (0.7.45; the UI spec's 2.3 and D6) |
| 4577 | PROMISES - the clinic's price, and a premium (2026-09-19) |
| 4774 | PROMISES - the schools: the price of a place, who pays it, and the |
| 5270 | PROMISES - the standing subsidies |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `PolicyScreen.TAXES` | `"Taxes", WAGES = "Wages", MONEY = "Money", PROMISES = "Promises", ZONING = "Z...` | The four areas, by what kind of lever each is - Jerus picked the grouping - and since 0.7.109 (batch Z2) the fifth, the city's ground: Zoning (ZoningScreen draws it). |
| 115 | `PolicyScreen.AREAS` | `{ TAXES, WAGES, MONEY, PROMISES, ZONING }` | ...as the head's chips list them. |
| 118 | `PolicyScreen.AREA_ICONS` | `{ Icons.COIN, Icons.STAFF, Icons.BANK, Icons.POPULATION, Icons.ZONING }` | ...and each one's icon: the coin, the staff, the bank, the people, the flag on a plot. |
| 123 | `PolicyScreen.POLICY_HOME` | `"Everything"` | The Taxes area's first page: where a door to the taxes lands, and the page the shell resets to. |
| 140 | `PolicyScreen.POLICY_TAX_PAGES` | `{ "Everything", "Profit", "Sales", "Wage", "Property", "Fuel", "Port dues" }` | BY WHICH TAX IT IS, not by where the modifier lives. |
| 142 | `PolicyScreen.POLICY_WAGE_PAGES` | `{ "The floor" }` |  |
| 144 | `PolicyScreen.CURRENCY_PAGE` | `"The currency"` | Money's middle page (XR2): how the D$ is held - its regime, the peg or the band, who pays at the strong edge, what the dial aims at. |
| 145 | `PolicyScreen.POLICY_MONEY_PAGES` | `{ "The policy rate", CURRENCY_PAGE, "Currency reform" }` |  |
| 146 | `PolicyScreen.POLICY_PROMISE_PAGES` | `{ "Pensions", "Out of work", "Food", "Health", "Schools", "Subsidies" }` |  |
| 149 | `PolicyScreen.TAX_ICONS` | `{ Icons.OVERVIEW, Icons.SECTOR, Icons.SHOPS, Icons.STAFF, Icons.HOMES, Icons....` | Each page's icon on its tab, in the pages' order (the crane for the port dues, FR4). |
| 151 | `PolicyScreen.WAGE_ICONS` | `{ Icons.STAFF }` | ...Wages' one, the staff. |
| 153 | `PolicyScreen.MONEY_ICONS` | `{ Icons.BANK, Icons.EXCHANGE, Icons.BANKNOTE }` | ...Money's: the bank for the policy rate, the two arrows for the currency (XR2), the banknote for the currency reform. |
| 155 | `PolicyScreen.PROMISE_ICONS` | `{ Icons.CANE, Icons.STAFF, Icons.FOOD, Icons.HEALTH, Icons.EDUCATION, Icons.S...` | ...and the promises': the cane, the staff, food, health, education and the sector icon for the subsidies. |
| 167 | `PolicyScreen.FRAME_CHROME` | `200` | How much of the stage the fixed frame takes above the page's scroller - the head, the tabs and the four figures - until it is laid out and its own height read. |
| 170 | `PolicyScreen.STAGE_REST` | `36` | What the menu spends around the frame and the page: its padding over and under them and the gap between, and four pixels of slack (BankScreen's, for the same frame). |
| 173 | `PolicyScreen.TRAY_CHROME` | `90` | How tall the staged tray is taken to be until it is laid out. |
| 315 | `PolicyScreen.LEAD_INFO` | `"Every number the city sets for itself, and what each one is doing this month...` | The tab's (i): the landing's lead, and the banner's line that every dial is a proposal (P1). |
| 510 | `PolicyScreen.STEP_INCOME` | `.0025` | A quarter of a point - every rate that moves off the income tax. |
| 513 | `PolicyScreen.STEP_PROPERTY` | `.0005` | A twentieth of a point - property, where a quarter is a quarter of the tax. |
| 516 | `PolicyScreen.EVERY_TAX` | `"income"` | The staged key of "Every tax at once" on the Everything page - the one city rate's key, which is what that rate became in 0.7.4. |
| 681 | `PolicyScreen.BUDGET_INFO` | `"Last month's surplus or deficit, and what it would be with this change: ever...` | THE BUDGET's (i). |
| 690 | `PolicyScreen.TRAY_INFO` | `"Everything staged on this page, applied together by one press.THE BUDGET is ...` | P20, the tray's (i). |
| 1062 | `PolicyScreen.NOTHING_BINDING_INFO` | `"Nothing is binding.Every lever has room to move, no sector is shut " + "out ...` | P2: what the hub says when nothing is binding. |
| 1108 | `PolicyScreen.TAX_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST }` | The money ramp the four taxes are drawn in, profit to property (the spec's 3.1): darker to lighter, then the rest. |
| 1117 | `PolicyScreen.TAX_NAMES` | `{ "Profit", "Sales", "Wage", "Property" }` | The four taxes by name, profit to property: their cards, the bar's parts and their pages. |
| 1438 | `PolicyScreen.CAVEAT` | `"Nothing here knows that the new rate changes what anybody does next month - ...` | What every preview on the tab owes the player, in one sentence (P21, the one every preview ended with until 0.7.36): behind each card's (i), once. |
| 1442 | `PolicyScreen.CAVEAT_LINE` | `"Struck on this month's books; behaviour is not projected."` | The caveat's line on a card. |
| 1453 | `PolicyScreen.TAKE_INFO` | `"Last month, by tax.Each has a rate of its own, and three of the four a " + "...` | P3, THE TAX TAKE's (i). |
| 1549 | `PolicyScreen.EVERY_TAX_INFO` | `"Moves all three rates to one number.A sector or wage band you've set apart "...` | P5: every tax at once, its (i). |
| 1557 | `PolicyScreen.FUEL_DUTY` | `"fuelDuty"` | The staged key of the fuel duty dial (0.7.103, batch P4; Jerus's A7). |
| 1560 | `PolicyScreen.FUEL_DUTY_STEP` | `.00001` | One step of the fuel duty's ladder: a cent a litre at founding prices, in thousands. |
| 1563 | `PolicyScreen.FUEL_INFO` | `"A duty a litre on the petrol the drivers buy at the pump, paid to the treasu...` | The fuel duty's (i). |
| 1570 | `PolicyScreen.FUEL_CAVEAT` | `"Struck on last month's litres at the pump: the owners who would take the bus...` | The fuel duty's caveat, whole. |
| 1614 | `PolicyScreen.PORT_DUES_PAGE` | `"Port dues"` | The Taxes area's page of the port dues dial (FR4; the dial and its collection are FR3's, TaxPolicy.getPortDues() and sectors.PortDivision). |
| 1617 | `PolicyScreen.PORT_DUES` | `"portDues"` | The staged key of the port dues dial (FR4). |
| 1620 | `PolicyScreen.PORT_DUES_STEP` | `TaxPolicy.MAX_PORT_DUES / 40` | One step of the port dues' ladder: a fortieth of the dial's top, so the default (1) and the top are both on a step. |
| 1634 | `PolicyScreen.PORT_DUES_CAVEAT` | `"Struck on last month's tonnes across the berths.The handling charge carries ...` | The port dues' caveat, whole. |
| 1688 | `PolicyScreen.PROPERTY_NOT` | `"Property is not on this dial: it is charged on value rather than on income, ...` | P6. |
| 1737 | `PolicyScreen.PROFIT_INFO` | `"Charged on what each sector earned before tax.A sector that lost money " + "...` | P8, the profit tax's head. |
| 1742 | `PolicyScreen.LOSS_INFO` | `"A sector that lost money paid no profit tax at all, which is why the " + "fi...` | P4. |
| 1746 | `PolicyScreen.SALES_INFO` | `"Charged on VALUE ADDED - what a sector sells, less the tax it already " + "p...` | P10, the sales tax's head. |
| 1752 | `PolicyScreen.WAGE_INFO` | `"Taken off every payroll in the city before the household sees it.The " + "jo...` | P12, the wage tax's head. |
| 1757 | `PolicyScreen.PROPERTY_INFO` | `"Charged on what land and buildings are assessed at, whether or not the " + "...` | P14, the property tax's head. |
| 1762 | `PolicyScreen.PROPERTY_STEP_INFO` | `"A twentieth of a point a step rather than the quarter the other three " + "g...` | P15. |
| 1767 | `PolicyScreen.REFUND_INFO` | `"\"In refund\" means a sector's credits on what it bought exceed the tax " + ...` | P11. |
| 1773 | `PolicyScreen.ROLL_INFO` | `"The power and water plants are the city's own and exempt.Raising what " + "l...` | P16. |
| 1778 | `PolicyScreen.SALES_SCALED` | `"Scaled, not recomputed: each sector's net remittance moves by the ratio of i...` | The sales tax's preview, scaled (the spec's D11, B6). |
| 1815 | `PolicyScreen.OFFSET_INFO` | `"A move is in POINTS off the tax's own rate, so a row left at zero is taxed "...` | P9, the offsets' note. |
| 1885 | `PolicyScreen.BANK_INFO` | `"The commercial bank's profit is taxed at Retail's rate - a Commercial Bank i...` | The bank's line (B5, D12). |
| 2127 | `PolicyScreen.PAYSLIP_INFO` | `"Only the first is a tax and only the first is set here - the other three " +...` | P13. |
| 2158 | `PolicyScreen.FARMLAND_INFO` | `"A field is worth what a developer would pay for it and grows what a " + "far...` | P17. |
| 2165 | `PolicyScreen.FARMLAND_CAVEAT` | `"Exact against today's land price.What it actually decides is whether " + "th...` | P19. |
| 2312 | `PolicyScreen.FLOOR_INFO` | `"Every wage in the city is a multiple of this number, so moving it moves " + ...` | P22, the floor's (i). |
| 2318 | `PolicyScreen.PINNED_WORDS` | `"At least one skill level is oversupplied AND cannot get any cheaper, so " + ...` | P23, both forms. |
| 2323 | `PolicyScreen.FREE_WORDS` | `"No skill level is pinned against the floor, so the labour market is " + "cle...` | ...the other: no band pinned. |
| 2328 | `PolicyScreen.FLOOR_CAVEAT` | `"Once wages have walked there, which takes about a year.Nothing about " + "th...` | P24. |
| 2476 | `PolicyScreen.DIAL_STEPS` | `{ 0, 1, 2, 3, 5, 8, 10, 15, 20, 30, 50, 75, 100 }` | The policy rates the dial's chips stage, in percent (0.7.2): the everyday range finely, the spiral's coarsely. |
| 2479 | `PolicyScreen.CEILING_STEPS` | `{ 3, 6, 12, 24, 36 }` | The advances ceiling's settings, in months of revenue (0.7.2), up to CentralBank.MAX_ADVANCES_CEILING. |
| 2482 | `PolicyScreen.TARGET_STEPS` | `{ 0, 1, 2, 3, 4, 5 }` | The inflation targets the chips set at once, in percent (0.7.4): the everyday range, the ladder beside them reaching the rest. |
| 2485 | `PolicyScreen.TARGET_STEP` | `.005` | One step of the target's ladder (0.7.4): half a point. |
| 2488 | `PolicyScreen.HOLDINGS_STEP` | `.10` | One step of the holdings' ladder (0.7.6): ten points of the term paper, the chips' own spacing. |
| 2491 | `PolicyScreen.CEILING_STEP` | `1` | One step of the ceiling's ladder (0.7.6): a month of revenue, the unit the chips are in. |
| 2517 | `PolicyScreen.OVER_INFO` | `"Hot money follows this difference in, and leaves the day it closes." + "It i...` | P28. |
| 2523 | `PolicyScreen.REAL_INFO` | `"The currency follows this one: a dial under the inflation people expect is a...` | P28's second half: the real differential - against the inflation people expect since 0.7.42 (the UI spec's B9). |
| 2529 | `PolicyScreen.REAL_DIAL_INFO` | `"The dial less the inflation people expect: what saving at the dial earns " +...` | The dial's own real rate (0.7.45): REAL_INFO's first half, for the dial alone. |
| 2683 | `PolicyScreen.HAND_INFO` | `"Jerus's autopilot: the rule can hold the dial.Every month, before anything i...` | P32. |
| 2687 | `PolicyScreen.REPRICE_INFO` | `"This does not reprice a single bond the city has already sold - every " + "c...` | P34: the dial card's caveat. |
| 2944 | `PolicyScreen.QE_LINE` | `"Buys term paper only: it reaches prices through what people own and through ...` | How QE reaches prices, in one plain line on the holdings card (0.7.45; the UI spec's D20). |
| 2977 | `PolicyScreen.CEILING_INFO` | `String.format("When the treasury runs dry the central bank advances the gap i...` | P36. |
| 3024 | `PolicyScreen.ANCHOR_INFO` | `"What people expect prices to do decides what they do next.Wages are asked " ...` | THE ANCHOR's (i): Expectations, in a player's words. |
| 3035 | `PolicyScreen.STRUCK_INFO` | `"Fees, build costs and upkeep, land, the shelf's floor, the pension's wage ba...` | The struck level's (i). |
| 3040 | `PolicyScreen.GAP_INFO` | `"A real rate over the world's brings money here and holds the currency dearer...` | THE CURRENCY'S LEVEL's (i). |
| 3178 | `PolicyScreen.BASKET_RAMP` | `{ Palette.MONEY_DARK, Palette.MONEY, Palette.MONEY_LIGHT, Palette.RAMP_REST, ...` | The basket's five parts' colours, groceries to services: the money ramp, then the rest - areas, not verdicts. |
| 3224 | `PolicyScreen.SWING_INFO` | `"Prices here have more than doubled and come back at some point." + "Wages, r...` | P30. |
| 3229 | `PolicyScreen.REFORMED_INFO` | `"The index is measured against the city's first basket in founding money, " +...` | P31, once the money has been reformed. |
| 3317 | `PolicyScreen.XR_REGIME` | `"xr:regime", XR_PAYER = "xr:payer", XR_AIM = "xr:aim", XR_PEG = "xr:peg", XR_...` | The staged keys of the currency page: the regime, who pays at the strong edge and what the dial aims at by ordinal; the peg; the band's centre, slope and width; the spread over the US's rate under a peg. |
| 3321 | `PolicyScreen.PEG_STEP` | `.01` | One step of the peg's chips: a hundredth of the peg, up to devalue and down to revalue. |
| 3324 | `PolicyScreen.CENTRE_STEP` | `.01` | One step of the band's centre: a hundredth of it. |
| 3327 | `PolicyScreen.SLOPE_STEP` | `.005, WIDTH_STEP =.005` | One step of the band's slope and of its width: half a point. |
| 3330 | `PolicyScreen.SPREAD_STEP` | `.0025` | One step of the spread over the US's rate: a quarter of a point. |
| 3333 | `PolicyScreen.CURRENCY_CHART_MONTHS` | `60` | The months the currency page's charts show: five years. |
| 3336 | `PolicyScreen.COVER_CHART_TOP` | `12` | The most months of import cover the vault's chart draws: a year. |
| 3339 | `PolicyScreen.DEAL_SHARE` | `.01` | The size of a deal the BUY OR SELL card weighs, a share of a year's output. |
| 4207 | `PolicyScreen.COVER_INFO` | `"The rest is general revenue - the same pot the schools and the hospitals " +...` | P41 and P47. |
| 4376 | `PolicyScreen.EI_INFO` | `"EI only pays the first twelve months, so a long bust costs less in EI than "...` | P57. |
| 4819 | `PolicyScreen.EVERY_SCHOOL` | `"tuitionScale"` | The staged key of "Every school at once" on the Schools page - the one tuition scale's key, which is what that scale became in 0.7.6. |
| 4822 | `PolicyScreen.TUITION_STEP` | `.05` | One step of every price-of-a-place ladder: a twentieth of the founding table. |
| 4862 | `PolicyScreen.SHARE_INFO` | `"The city's share of every course fee.Households pay the rest out of a " + "m...` | P48. |
| 4869 | `PolicyScreen.BURDEN_INFO` | `String.format("At %.0f%% of a month's wage nobody enrols at all: a red bar is...` | P49. |
| 4875 | `PolicyScreen.PRICE_INFO` | `"The founding tuition table times this, before the city's share comes off." +...` | P50. |
| 4883 | `PolicyScreen.SCHOOL_CAVEAT` | `"Against the courses being taken now, each kind's students at its own " + "pr...` | P51/P52: the schools' preview caveat. |
| 5234 | `PolicyScreen.LOAN_INFO` | `"Charged on a graduate's balance while they repay it, and on nothing while " ...` | P55. |
| 5240 | `PolicyScreen.LOAN_CAVEAT` | `"Against the balances the graduates owe today.The instalment itself does " + ...` | P56. |
| 5285 | `PolicyScreen.SUBSIDY_INFO` | `"A protected sector is topped up to break-even every month it loses money, " ...` | P69. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 91 | `final ZoningScreen zoning` | The Zoning area's pages (0.7.109, batch Z2): drawn there, staged and applied through this screen's set and tray. |
| 121 | `String policyArea` | The area open, one of AREAS, or null for the hub. |
| 124 | `String policyPage` |  |
| 158 | `final Set<String> openLines` | What the player has opened - a fold, a payer's own dial, the folded zero payers - by key, so a redraw on the clock leaves it open (Land's rule: kept while the game runs, not saved; the spec's D15). |
| 161 | `private final Map<String, Integer> flagSeen` | The month each biting flag was first drawn in, by its heading: a flag first seen this month carries NEW until the month turns. |
| 164 | `private javafx.scene.control.ScrollPane body` | The page's scroller. |
| 450 | `final java.util.LinkedHashMap<String, Double> policyStaged` |  |
| 462 | `final java.util.LinkedHashMap<String, Lever> policyLevers` | Every dial DRAWN this pass, by key. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 5236 | **type** `final class PolicyScreen` | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it would do beside it, on a hub of four area cards and the levers that are... |
| 93 | 4 | `PolicyScreen(UserInterface ui)` |  |

### POLICY (0.7.36): THE FRAME (lines 98-353)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 10 | `static String[] pagesOf(String area)` | The pages of an area, in their tabs' order. |
| 188 | 10 | `static String[] iconsOf(String area)` | ...and their icons. |
| 204 | 62 | `void showPolicyMenu()` | The tab's entry point: the hub, or the area and page open. |
| 268 | 6 | `void openHub()` | The hub, at its top. |
| 276 | 7 | `void open(String area, String page)` | An area, on its first page or the one named, at its top (every door on this tab, and the shell's). |
| 285 | 1 | `void openArea(String area)` | An area's first page: a chip in the head. |
| 288 | 1 | `void openPage(String page)` | A page of the open area: a tab. |
| 291 | 22 | `private void frameOver(VBox frame, VBox page, Region tray)` | The fixed frame over a scrolling page and, when one is given, the tray under it: the page as tall as what is left between them (BankScreen's frame, with a foot). |
| 322 | 17 | `static String areaInfo(String area)` | Each area's (i): what it holds (the landing's row blurbs until 0.7.36). |
| 346 | 7 | `HBox head()` | The head: "Policy" with the money blue's swatch, or the breadcrumb "Policy › Taxes" with "Policy" a way back to the hub; the areas (five since 0.7.109) as chips at its right, the open one lit (the spec's D14: the brea... |

### the four figures (lines 354-436)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where)` | One figure of the strip, worked out without drawing it: its label, its figure, its note, its colour, and where its click goes. |
| 366 | 24 | `List<Cell> vitalCells()` | THE FOUR (pure: the probe reads them as the strip shows them). |
| 392 | 11 | `HBox vitals()` | The strip: the four as limit cells, each a door. |
| 405 | 7 | `static String ratesWords(TaxPolicy p)` | The rates in words: "15.00% on income · 1.50% a year on property", or the three once they have parted (B2: two places, as Money.pct2() says). |
| 414 | 4 | `static int needLevel(List<CityNeeds.Need> needs, CityNeeds.Kind kind)` | A NEEDS YOU row's level by its kind, in the list measured once a draw: 0 when it is not listed. |
| 420 | 3 | `static String levelTone(int level)` | A level's colour: plain, amber near the line, red past it. |
| 425 | 1 | `TaxPolicy policy()` | The city's policy. |
| 428 | 3 | `double taxRaised()` | Every tax line the city collected last month - CityNeeds' own sum since 0.7.24, which NEEDS YOU's TREASURY line reads too. |
| 433 | 3 | `int pinnedBands()` | Wage bands pinned to the floor with people spare in them - CityNeeds' count since 0.7.24, NEEDS YOU's WAGES line's. |

### THE STAGED SET. (lines 437-483)

| line | len | member | says |
|---:|---:|---|---|
| 466 | 4 | **type** `record Lever(String key, String name, double current, double min, double max, double step, java.util.functi...` | One dial: what it is, where it can go, how to read it, how to set it. |
| 471 | 1 | `boolean isStaged(String key)` |  |
| 473 | 4 | `double staged(String key, double current)` |  |
| 478 | 1 | `void stage(String key, double value)` |  |
| 480 | 1 | `void unstage(String key)` |  |
| 482 | 1 | `void dropProposal()` |  |

### THE LADDER (lines 484-581)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 1 | `static String baseKey(String tax)` | The staged key of one income tax's own base (0.7.4): "base:profit", "base:sales" or "base:wage". |
| 527 | 9 | `Ladder ladderOf(Lever lever)` | A registered dial's Ladder, wired to the staged set: the lever goes in the register the tray names and applies from, the thumb sits at what is staged, and every move goes through propose(). |
| 538 | 4 | `static String stepWord(double step)` | How one step reads on a tax page's ends line: in points of the rate. |
| 543 | 3 | `static double bounded(double value, Lever lever)` |  |
| 554 | 6 | `void propose(Lever lever, double value)` | A dial has been moved to a value. |
| 570 | 5 | `boolean changesAtCurrent(Lever lever)` | Whether a lever still changes something at the value it reads as current (0.7.4): "Every tax at once" once the three income bases have parted. |
| 577 | 4 | `String nowReads(Lever lever)` | What a lever reads today, in words: its current value, or "three rates" and "a price per school" for the every-tax and every-school levers once they have parted. |

### the staged set, through a copy (lines 582-686)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 34 | `static void onCopy(TaxPolicy p, String key, double v)` | What a staged key sets, on any policy: the one place a key is read as a dial for the preview (0.7.36). |
| 631 | 1 | `TaxPolicy stagedPolicy()` | The city's policy with everything staged put through a copy - every key in the order it was moved, and the grant's basis and amount together. |
| 634 | 17 | `TaxPolicy stagedPolicyWith(String key, double value)` | ...and one key at a value of the caller's on top (a dial card's thumb, while it is dragged). |
| 653 | 5 | `TaxPolicy with(String key, double value)` | The city's policy with one key moved and nothing else: a dial with its own Apply previews its own move. |
| 660 | 4 | `TaxPolicy.GrantBasis stagedBasis(TaxPolicy live)` | The grant's basis as staged, else the city's. |
| 666 | 1 | `double stagedShare()` | The city's share of tuition as staged (Education's dial, not the policy's). |
| 669 | 3 | `PolicyPreview.Budget stagedBudget()` | THE BUDGET under everything staged: PolicyPreview's (M8). |
| 674 | 5 | `Effect budgetEffect(TaxPolicy after, double share)` | THE BUDGET as an effect row, before and after a policy and a share of the caller's. |

### the tray (lines 687-750)

| line | len | member | says |
|---:|---:|---|---|
| 703 | 24 | `Region tray()` | The staged tray (the spec's 3.0): on the pages whose dials batch - the taxes' and the schools' - once anything is staged: a chip a change that comes out with its ×, THE BUDGET before and after the set, and Apply and D... |
| 729 | 1 | **type** `record TrayItem(String key, String words)` | One staged change as the tray lists it: its key, and "The wage rate 15.00% → 16.00%". |
| 732 | 9 | `List<TrayItem> trayItems()` | Each staged change the tray lists, in the order moved (pure: the probe reads them). |
| 743 | 7 | `void applyStaged()` | Each staged value through its own dial's setter, then the set is empty. |

### a dial that keeps its own Apply (lines 751-810)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 10 | `Ladder ownLadder(String key, double current, double min, double max, double step, java.util.function.DoubleFunction<String> label)` | The lever itself: a Ladder (0.7.6) that STAGES a change rather than making one, for a dial that keeps its own Apply - the wage floor, the policy rate, the pension's two, EI's two, the clinic's two, the fare on Infrast... |
| 789 | 16 | `HBox applyBar(String label, Runnable apply)` | Apply, or put it back (0.7.34's action button since 0.7.36: a GO in the money blue that says what it sets, and "Leave it as it is" beside it). |
| 807 | 3 | `List<Node> applyFoot(String key, String label, Runnable apply)` | The foot of a card whose dial keeps its own Apply: the bar once its key is staged, nothing before. |

### the screen's own pieces (lines 811-1009)

| line | len | member | says |
|---:|---:|---|---|
| 814 | 7 | `static Label words(String text, double size, String tone)` | Words that wrap, at a size, in a colour. |
| 823 | 6 | `static Label figure(String text, double size, String tone)` | A figure that is never cut, at a size, in a colour. |
| 835 | 5 | `String struckWords(String what)` | A Pe-struck price's sentence for an (i) (0.7.45; the UI spec's D13): the level its founding figure is struck at this month - never set against the index, which an old save's level of 1 would misread. |
| 842 | 17 | `static HBox cardHead(String svg, String colour, String title, String info, Node right)` | A card's head: its icon in a square tinted in its colour, its title in capitals, an (i) when `info` is not null, and at its right `right` (null: nothing) - BankScreen's. |
| 861 | 7 | `static VBox card(Node...rows)` | A card on Build's ground: RAISED, radius 8, a 1 px edge, padding 12. |
| 870 | 4 | `static String cardStyle(String edge)` | The card's ground, with its edge in a colour. |
| 876 | 9 | `static VBox doorCard(VBox c, Runnable go)` | A card that opens somewhere: the accent edge under the pointer, and a click goes. |
| 887 | 9 | `static HBox cardLine(String label, String value, String tone)` | A line of a card: words at the left, wrapping, and a figure at the right. |
| 898 | 5 | `static HBox cardLine(String label, String value, String tone, String info)` | ...with an (i) after its words. |
| 905 | 3 | `static HBox noteLine(String shown, String whole, double wide)` | A muted line with its whole behind an (i), wrapping at `wide`. |
| 910 | 4 | `static Node line(String text, String whole)` | A plain line of words in a card, wrapping, with its whole behind an (i) when `whole` is not null. |
| 916 | 1 | `static Label muted(String text)` | ...muted. |
| 919 | 5 | `static Label quiet(String text)` | A heading's quiet words at its right, never cut. |
| 926 | 5 | `static FlowPane chips(Node...cs)` | A row of chips, wrapping. |
| 933 | 6 | `static VBox column(Node...rows)` | A statement column, for a fold: the old rows at their old width. |
| 941 | 3 | `VBox fold(String key, String caption, java.util.function.Supplier<Node> inside)` | A fold kept on this screen: "details ▸ caption". |
| 946 | 12 | `static GridPane row(Node...cards)` | Cards side by side in equal columns, each as tall as the tallest. |
| 960 | 10 | `static HBox split(Region first, Region second, double share)` | Two cards side by side, the first `share` of the width (the floor's dial and the city's payroll). |
| 972 | 7 | `Region spark(double[] all, int n, String colour, double width, double height)` | The last `n` months of a History series, for a sparkline - BankScreen's. |
| 981 | 1 | `Sector sector(String key)` | A sector by its key, or null. |
| 984 | 1 | `static String ptsMove(double d)` | A signed share in points: "+1.00 pts". |
| 987 | 1 | `static String moneyMove(double d)` | Money with its sign, as a move: "+$2.5M", "−$1.2M", "$0". |
| 990 | 4 | `static String moneyMoveFull(double d)` | ...every digit, for one household's or one person's money: "+$173", "−$41", "$0" under half a dollar. |
| 996 | 4 | `static String amount(double v)` | Money that can be below nothing - a sector in refund, a surplus where a cost was - with a true minus: "$2.5M", "−$365k". |
| 1002 | 1 | `static String rateMove(double d)` | A rate's move in points: "+0.25 points". |
| 1005 | 1 | `static String pct0(double share)` | A percentage to none: "45%". |
| 1008 | 1 | `static String times(double scale)` | "x1.00". |

### THE HUB (0.7.36; the landing's four rows until then) (lines 1010-1093)

| line | len | member | says |
|---:|---:|---|---|
| 1023 | 37 | `void hubPage(VBox page)` |  |
| 1066 | 9 | `List<DecisionLog.Entry> recentDecisions(int n)` | The last `n` decisions of the kinds this tab makes - taxes, promises, the central bank, the currency, zoning - newest first. |
| 1077 | 5 | `DecisionLog.Entry lastDecision(String kind)` | The last decision of one kind, or null. |
| 1083 | 5 | `static boolean isPolicyKind(String kind)` |  |
| 1090 | 3 | `static String decisionWords(DecisionLog.Entry e)` | A decision as a chip: "m1,204 · Taxes to 15%". |

### the area cards (lines 1094-1211)

| line | len | member | says |
|---:|---:|---|---|
| 1097 | 9 | `VBox areaCard(String svg, String title, String figure, Node picture, String line, String foot, Runnable go)` | One area card: its icon and title with "›", its figure, a small picture, one line and a foot - the whole a door. |
| 1111 | 4 | `double[] taxesBooked()` | The four taxes last month, profit · sales · wage · property, as NationalAccounts booked them. |
| 1119 | 9 | `VBox taxesCard()` |  |
| 1129 | 12 | `VBox wagesCard()` |  |
| 1142 | 22 | `VBox moneyCard()` |  |
| 1165 | 18 | `VBox promisesCard()` |  |
| 1185 | 4 | `static String promisesDoor(PolicyPreview.Promises p)` | Where the PROMISES card opens (0.7.45): Food when the vouchers are the largest slice, else Pensions. |
| 1191 | 8 | `static SegmentBar sliceBar(List<Slice> slices)` | Slices as one bar filling its card, each part's name and money its tooltip (a part below nothing draws nothing). |
| 1201 | 4 | `static String areaIcon(String area)` | An area's icon, as its chip has it. |
| 1207 | 4 | `static String signedPct1(double v)` | Inflation as a signed share to one place, with a true minus: "+0.2%". |

### what is biting (lines 1212-1414)

| line | len | member | says |
|---:|---:|---|---|
| 1219 | 1 | **type** `record Flag(String tone, String heading, String body, String area, String page, String door)` | A lever that is forcing something: its colour (a verdict), its heading and what it means, and where it is undone - the area and page its door opens, and the door's words. |
| 1230 | 161 | `List<Flag> policyFlags()` | The levers that are currently forcing something. |
| 1393 | 3 | `static String capitalised(String word)` | "sales" to "Sales", for a flag that opens with a tax's name. |
| 1398 | 16 | `VBox flagCard(Flag f, boolean isNew)` | A biting lever as a card: its alert in its verdict's colour, its heading (NEW the month it is first seen), its first sentence with the rest behind an (i), and a door to where it is undone. |

### TAXES (0.7.36; THE FOUR TAXES until then) (lines 1415-1449)

| line | len | member | says |
|---:|---:|---|---|
| 1445 | 4 | `TaxPolicy previewFor(String key, double v, double current, double step)` | The policy a card previews at a thumb's value: everything staged, with the card's key at `v` - or exactly what is staged while the thumb rests where nothing of its own is staged (a parted every-tax dial at its current... |

### EVERYTHING (lines 1450-1553)

| line | len | member | says |
|---:|---:|---|---|
| 1458 | 26 | `void everythingPage(VBox page)` |  |
| 1486 | 35 | `VBox takeCard(double[] booked, TaxPolicy live)` | THE TAX TAKE: last month's four as one bar, a click on a part its page; and once anything is staged, the four at the staged rates, a moved part a ghost with its move over it. |
| 1523 | 10 | `double[][] taxHistory()` | Ten years of each tax from City History, profit · sales · wage · property; the profit line is the business and the food industry together, as the take's first part is. |
| 1535 | 12 | `VBox taxCard(int i, double raised, double all, String rate, double[] history, boolean atStop, String info)` | One tax as a card: its icon and name, its rate, what it raised and its share, its ten years, a chip at its legal stop - the whole a door to its page. |

### FUEL (0.7.103) (lines 1554-1610)

| line | len | member | says |
|---:|---:|---|---|
| 1574 | 7 | `void fuelPage(VBox page)` | THE FUEL DUTY (0.7.103): its month, and its dial, staged into the tray with the taxes. |
| 1583 | 13 | `VBox fuelDutyCard(TaxPolicy live)` | The fuel duty's dial card: the duty a litre in today's money, what it would raise, the pump's litre and THE BUDGET. |
| 1598 | 12 | `DoubleFunction<List<Effect>> fuelDutyEffects(TaxPolicy live)` | The fuel duty's effects at any value of its thumb (pure: the probe reads them): the duty a month, a litre at the pump, THE BUDGET. |

### PORT DUES (FR4) (lines 1611-1733)

| line | len | member | says |
|---:|---:|---|---|
| 1623 | 9 | `static String portDuesInfo()` | The port dues' (i): what the dues are, who pays and who they pass to, what the default is and what the dial does. |
| 1639 | 8 | `void portDuesPage(VBox page)` | THE PORT DUES (FR4): last month's, and the dial, staged into the tray with the taxes. |
| 1649 | 15 | `VBox portDuesCard(TaxPolicy live)` | The port dues' dial card: the multiple of the default due, what the month's tonnes would pay, the handling charge a tonne by kind and THE BUDGET. |
| 1666 | 20 | `DoubleFunction<List<Effect>> portDuesEffects(TaxPolicy live)` | The port dues' effects at any value of its thumb (pure: the probe reads them): the dues a month, the handling a tonne of each kind with a berth, THE BUDGET. |
| 1699 | 19 | `VBox everyTaxCard(TaxPolicy live)` | EVERY TAX AT ONCE (0.7.4; a dial card since 0.7.36) - what the one city rate became once each income tax had its own. |
| 1720 | 13 | `DoubleFunction<List<Effect>> everyTaxEffects(TaxPolicy live)` | Every tax at once's effects at any value of its thumb (pure: the probe reads them): the three income taxes and THE BUDGET. |

### PROFIT, SALES, WAGE, PROPERTY (lines 1734-2283)

| line | len | member | says |
|---:|---:|---|---|
| 1784 | 5 | `static String baseInfo(String offsets)` | P7, a base rate's (i): what moves off it. |
| 1791 | 22 | `void taxPage(VBox page, String which)` | One tax's page: its base on a dial card, who pays it ranked with each row's own move, the payslip (wage) or the farmland (property), and the old table under "details". |
| 1822 | 38 | `VBox baseCard(String which, TaxPolicy live)` | A tax's base on a dial card: what it raises and THE BUDGET before and after (and, for profit, the bank at Retail's rate - B5). |
| 1862 | 21 | `DoubleFunction<List<Effect>> baseEffects(String which, String key, double current, double step, TaxPolicy live)` | A tax's base's effects at any value of its thumb (pure: the probe reads them): the tax a month, the bank on Profit, THE BUDGET. |
| 1891 | 3 | **type** `record Payer(String key, Lever lever, String svg, String name, double paid, String figure, double rateNow, ...` | One payer of a tax: its row's key and lever (null: no dial - the bank), its icon and name, what it paid last month, its figure's words, its rate now and staged, its offset, a chip (null: none), and its line of the tak... |
| 1896 | 78 | `List<Payer> payersOf(String which, TaxPolicy live)` | The payers of a tax (pure: the probe reads them): every sector, or every band, with what the take strikes for each at the city's dials and the staged ones; the bank last on Profit. |
| 1976 | 3 | `static boolean paidNothing(Payer p)` | Whether a payer paid nothing to speak of and is folded into one row (a sector in refund is not: it is owed). |
| 1981 | 26 | `VBox payers(String which, TaxPolicy live)` | WHO PAYS: the payers ranked by what they paid, a row opening into its own move; those that paid nothing folded into one muted row that opens to the same rows (the spec's D15). |
| 2009 | 7 | `void addPayer(VBox list, Payer p, double scale, String which)` | A payer's row, and its own dial under it while it is open. |
| 2024 | 58 | `HBox payerRow(Payer p, double scale, boolean shown, Runnable toggle)` | One payer: its icon and name ("▸ its own move" under a name that opens), what it paid as a bar - with the staged take as a ghost bar under it once anything staged reaches it - its figure, and its rate as a chip ("15.0... |
| 2084 | 11 | `VBox offsetCard(Payer p, String which)` | A payer's own move, opened in place: its offset's dial, and that payer's tax and THE BUDGET before and after. |
| 2097 | 28 | `DoubleFunction<List<Effect>> offsetEffects(Payer p, String which)` | A payer's own move's effects at any value of its thumb (pure: the probe reads them): its tax, the bank beside Retail's, THE BUDGET. |
| 2134 | 22 | `VBox payslipCard(TaxPolicy live)` | THE PAYSLIP (T12): what a wage taxed at the wage rate loses, as a bar of the wage in four parts, and each band moved off the rate with its own total; the door to the promises' dials. |
| 2181 | 14 | `VBox farmlandCard(TaxPolicy live)` | The one dial that decides whether the city keeps its fields (T15). |
| 2197 | 16 | `DoubleFunction<List<Effect>> farmlandEffects(TaxPolicy live)` | The farmland relief's effects at any value of its thumb (pure: the probe reads them). |
| 2215 | 68 | `Node taxTable(String which, TaxPolicy policy)` | The old table of a tax's page (T9-T11, T14), behind "details": every sector or band with its figures as they were drawn. |

### WAGES - the floor (lines 2284-2442)

| line | len | member | says |
|---:|---:|---|---|
| 2332 | 17 | `void floorPage(VBox page)` |  |
| 2351 | 29 | `Node wageLadder(LabourMarket market, PopulationManager people, double floor, double want, boolean moved)` | The eleven jobs under their bands on one scale, the floor a rule across them; a band's chip says whether it is pinned (LabourMarket.isPinned(), as the model has it). |
| 2382 | 17 | `VBox floorCard(LabourMarket market, double floor)` | THE FLOOR on its dial card: read in today's money, set in founding money (D6); the floor, the unskilled wage, every wage and the city's payroll before and after. |
| 2401 | 12 | `DoubleFunction<List<Effect>> floorEffects(LabourMarket market)` | The floor's effects at any value of its thumb (pure: the probe reads them). |
| 2415 | 10 | `VBox cityPayrollCard()` | What the city itself pays (W4), neutral (B14: a payroll is not bad news by being one). |
| 2427 | 15 | `Node floorTable(LabourMarket market, double want, boolean moved)` | The ladder as a table (W2), behind "details", in today's money: each job's multiple of the floor, what it is paid, and where it heads at a staged floor. |

### MONEY - the policy rate (lines 2443-2878)

| line | len | member | says |
|---:|---:|---|---|
| 2494 | 3 | `static String monthsWords(double months)` | A ceiling as the ladder reads it: "1 month", "6 months". |
| 2499 | 3 | `static double halfPoint(double target)` | A target on the half-point grid, so a run of steps lands on 3% and not on 3.0000000000000004%. |
| 2504 | 11 | `String rateInfo()` | P25, the dial's line, and P26 corrected (the spec's B16: the city's paper is priced from the higher of the dial and the bank's cost of funds, not "at the dial"). |
| 2534 | 28 | `void ratePage(VBox page)` |  |
| 2570 | 21 | `Node priceRateChart()` | Behind the PRICES strip's "details" (the spec's R4): City History's price level (the index, left axis) and policy rate (percent, right axis) on one small chart - no legend, so the key under it names them. |
| 2593 | 9 | `static VBox rateCell(String label, String value, String line, String info)` | A cell under the rate line: its label, its figure, a line, and the paragraph behind an (i). |
| 2604 | 1 | **type** `record RateMark(double at, String name, String colour, boolean dashed, String tip)` | One rate on the line, worked out without drawing it: where, its name and colour, dashed or not, and its tooltip. |
| 2612 | 49 | `List<RateMark> rateMarks(double rate, double want, boolean moved)` | THE RATE LINE's marks (pure: the probe reads them): inflation (amber only past a point from the target, the old page's line), the target, the dial, the rule, savers, the city, prime and the world - and, staged, the di... |
| 2663 | 18 | `Node rateLine(double rate, double want, boolean moved)` | THE RATE LINE: the dial as a bar, every other rate a named rule across it, on 0 to a quarter past the highest (never under 3%). |
| 2693 | 1 | `static String spendPct(double v)` | The spend factor to one place: the dial reaches it only through the savers' rate, a few tenths of a point at most, which a whole percent would round away. |
| 2696 | 9 | `String spendInfo()` | P29. |
| 2715 | 30 | `VBox theDialCard(DebtManager market, PriceIndex px, double rate, double want)` | THE DIAL (R5, R7): the ladder with the rule's rate marked on it, the thirteen chips and "do what the rule says", whose hand it is in; what a move does to the city's rate, the savers', prime and what households spend -... |
| 2753 | 19 | `VBox tiedDialCard(DebtManager market, double rate)` | THE DIAL under a peg (XR2, star 5): tied to the US's rate, so it offers no chips and no Apply - the dial as the US's rate plus the spread in one line, a door to where the spread is set (Policy › Money › The currency),... |
| 2774 | 13 | `DoubleFunction<List<Effect>> dialEffects(double rate)` | The policy rate's effects at any value of its thumb (pure: the probe reads them). |
| 2795 | 77 | `VBox ruleCard(DebtManager market, PriceIndex px)` | THE RULE (R6): what it aims at - the target, set at once by its chips and its ladder (0.7.4: it moves no price this month, only what the rule says) - and what it says at two targets, and inflation against the target a... |
| 2874 | 1 | `static String plain(double v)` | A weight or a move in points as it is: "1.5", "1.375", "2" - one place would read 1.375 as 1.4. |
| 2877 | 1 | `static String pointsOf(double v)` | ...as points: "1 point", "1.25 points". |

### HOW STRICT (0.7.52): the dial beside the target - DebtManager's HOW STRICT (lines 2879-3011)

| line | len | member | says |
|---:|---:|---|---|
| 2882 | 12 | `static String strictInfo()` | HOW STRICT's (i): what each end does, from the model's own numbers. |
| 2896 | 4 | `static String strictWords(PolicyPreview.RuleAt r)` | The preview's words at a step (pure: the probe reads them): "Very strict: aims under 2.0%, at 1.0% · on target it sets 4.00% · at this inflation 5.10%". |
| 2902 | 1 | `static DebtManager.Strictness strictAt(double v)` | The step under a ladder's thumb. |
| 2910 | 13 | `VBox strictPart(DebtManager market)` | HOW STRICT (0.7.52): Jerus's dial beside the target, its five steps on a Ladder set at once like the target's, and under it what the bank would aim at and what the rule would set at the step under the thumb (PolicyPre... |
| 2925 | 17 | `String holdingsInfo()` | P35, both layers. |
| 2948 | 19 | `VBox holdingsCard(DebtManager market)` | THE CENTRAL BANK's holdings (R8): the share of the term paper it aims to hold, set at once; the thirty-year rate once the book has moved there (M9). |
| 2969 | 6 | `DoubleFunction<List<Effect>> holdingsEffects()` | The holdings' effects at any value of its thumb (pure: the probe reads them). |
| 2986 | 18 | `VBox ceilingCard()` | THE CENTRAL BANK's ceiling (R9): how far it will advance the treasury, set at once; the ceiling in money at another setting (M10); what is owed, red only when it binds (ceilingBound(): a verdict). |
| 3006 | 5 | `DoubleFunction<List<Effect>> ceilingEffects()` | The ceiling's effects at any value of its thumb (pure: the probe reads them). |

### THE ANCHOR AND THE CURRENCY'S LEVEL (0.7.45) (lines 3012-3174)

| line | len | member | says |
|---:|---:|---|---|
| 3021 | 1 | `static boolean offTarget(Expectations e, double target)` | True when what trust is judged on - inflation smoothed over a year - is off the target by more than Expectations.TOLERANCE: the rate line's inflation tick and the rule's band go amber (the UI spec's D4). |
| 3047 | 2 | **type** `record AnchorWords(String figure, double trust, String trustWords, String move, String moveTone, String lea...` | The anchor's words, worked out without drawing them (pure: the probe reads them). |
| 3050 | 31 | `AnchorWords anchorWords()` |  |
| 3090 | 16 | `static String notLeaningWords(Game g, double miss)` | Why the month's lean is nothing, on the model's own condition (Expectations.takeMonth()): the lean counts only when the advice that holds the target - the Standard rule's at any strictness since 0.7.52 (DebtManager.ho... |
| 3108 | 5 | `static String stepPts(double step)` | A month's move in trust, in points, signed with a true minus: "+0.2 pts", "−0.91 pts" (two places under a twentieth of a point). |
| 3115 | 1 | `static String rate2(double rate)` | A yearly rate to two places with a true minus: "2.15%", "−0.40%". |
| 3125 | 16 | `VBox anchorCard()` | THE ANCHOR (the UI spec's 2.2 item 2): what people expect; how far they trust the bank, as a gauge from nothing to all with the least and the most it can be; how trust moved this month and whether the rate leans again... |
| 3143 | 1 | **type** `record GapWords(String figure, String line, String parity)` | The currency's level words, worked out without drawing them (pure: the probe reads them). |
| 3145 | 13 | `GapWords gapWords()` |  |
| 3160 | 4 | `static String parityWords(double deviation)` | Where the currency stands against parity, in words: "25.9% weaker than parity", "25.2% stronger", "at parity". |
| 3166 | 8 | `VBox gapCard()` | THE CURRENCY'S LEVEL (XR1; the UI spec's 2.2 item 2 as the drift): where the real rate holds the D$ against parity's trend, the gap that does it, and where the rate stands against parity. |

### THE BASKET (0.7.45) (lines 3175-3284)

| line | len | member | says |
|---:|---:|---|---|
| 3181 | 1 | **type** `record BasketPart(String name, double weight, String colour, String words)` | One part of the basket: its name, its weight, its colour and what its key says. |
| 3189 | 25 | `List<BasketPart> basketParts()` | The basket in force, part by part (pure: the probe reads them): each component's weight (PriceIndex.getWeight()) and its own inflation over the year off History's line for it (YearBook.componentInflation()) - or, with... |
| 3216 | 6 | `String basketWords()` | What the PRICES card says of the basket in force: when it was struck, or that it is still to be. |
| 3239 | 45 | `VBox pricesCard(PriceIndex px)` | THE PRICES (R4): the index since founding; where it has been - the dearest and the cheapest, on a bar, today marked (Jerus, 2026-09-14: "something as well that stores the highest price index and lowest") - once the tw... |

### MONEY - the currency (XR2, 2026-10-22) (lines 3285-3405)

| line | len | member | says |
|---:|---:|---|---|
| 3342 | 1 | `private String sym()` | The city's money's mark: "D$". |
| 3345 | 1 | `private String curRate(double rate)` | An exchange rate with the D$'s mark: "D$1.411". |
| 3348 | 4 | `private String curLocal(double thousands)` | Local money with its mark and a true minus: "D$2.4M", "−D$725k". |
| 3354 | 4 | `static String curGap(double gap, String target)` | A gap of the shadow from its target in words: "8.5% weaker than the peg", "3.0% stronger than the centre", "level with the peg" (pure). |
| 3360 | 3 | `static String curMonths(double months)` | A number of months of imports in words: "1 month", "3 months", "1.5 months" (pure). |
| 3365 | 5 | `private<E extends Enum<E>> E stagedChoice(String key, E now, E[] all)` | The choice staged under `key`, or the one in force: an enum staged by its ordinal. |
| 3372 | 4 | `private<E extends Enum<E>> void choose(String key, E now, E pick)` | Stages a choice, or puts the staging back when it is the one in force; and draws the page again. |
| 3378 | 3 | `ForeignAccounts.Regime stagedRegime(ForeignAccounts.Regime now)` | The regime staged, or the one in force. |
| 3388 | 17 | `void currencyPage(VBox page)` | THE CURRENCY PAGE: the regime and what entering it does; the peg or the band, whichever is in force (a note when one is staged and not yet); who pays at the strong edge and what the dial aims at; and under a peg the d... |

### the pieces the currency page is built from (lines 3406-3484)

| line | len | member | says |
|---:|---:|---|---|
| 3409 | 15 | `private VBox option(String name, String says, boolean lit, boolean inForce, Runnable pick)` | One option of a choice: its chip (lit when it is the one staged or in force), "in force" beside it when it is, and what it does underneath. |
| 3426 | 12 | `private static VBox fxCell(String label, String value, String line, String info)` | One figure on a panel, as the rate line's cells are: its label (with an (i) when `info` is given), its figure and a line under it. |
| 3440 | 8 | `private HBox stepper(String key, double now, double step, double lo, double hi, String down, String up)` | A figure's − and + steps, staged under `key` against what it is now; `down` and `up` say what each does. |
| 3450 | 5 | `private void restage(String key, double now, double to, double step, double lo, double hi)` | A stepped value staged on the grid of its steps from what it is now, held between its limits, or put back when it is where it started. |
| 3462 | 22 | `private List<Node> currencyFoot(String label, Runnable apply, String...keys)` | The foot of a card of this page: Apply, and "Leave it as it is", once any of its keys is staged - and only theirs go when it is pressed, so a second card's staging survives the first one's Apply (applyFoot() drops the... |

### HOW THE D$ IS SET (lines 3485-3587)

| line | len | member | says |
|---:|---:|---|---|
| 3488 | 8 | `static String regimeSays(ForeignAccounts.Regime r)` | What each regime does, in a line (the board's own words). |
| 3498 | 14 | `String regimeInfo()` | The regime card's (i): the four ways, and what history says of a peg. |
| 3514 | 12 | `String regimeNow(ForeignAccounts fx)` | What the regime in force means now, in a line (pure). |
| 3528 | 12 | `String regimeEntering(ForeignAccounts fx, ForeignAccounts.Regime r)` | What entering a regime does, today (pure): a peg pegs at the rate of the day, a band centres on it. |
| 3542 | 8 | `private String regimeApplies(ForeignAccounts fx, ForeignAccounts.Regime r)` | The chip row's Apply button: what pressing it does. |
| 3552 | 8 | `String breakLine(ForeignAccounts fx)` | The last peg that broke and how many have, in words; null before the first (pure). |
| 3565 | 22 | `VBox regimeCard(ForeignAccounts fx, ForeignAccounts.Regime now, ForeignAccounts.Regime want)` | HOW THE D$ IS SET: the four regimes as chips with a line each, the one in force marked, a choice staged until Apply, and what entering it does. |

### THE PEG (lines 3588-3740)

| line | len | member | says |
|---:|---:|---|---|
| 3591 | 13 | `String pegInfo()` | THE PEG's (i): what holds it, how it breaks, and what a devaluation is. |
| 3606 | 7 | `String pegBreakWords(ForeignAccounts fx)` | The break sentence: what breaks a peg, this month's chance, and where the D$ would land (pure). |
| 3615 | 7 | `static String floorWords(double months)` | The vault's life at this pace, in words: "the floor in 8 months", "under its floor", "not falling" (pure). |
| 3624 | 25 | `private Node[] pegCells(ForeignAccounts fx, double peg)` | The four figures under the peg: the shadow rate, what the vault did this month, the vault and its pace (pure of drawing). |
| 3656 | 27 | `VBox pegCard(ForeignAccounts fx, ForeignAccounts.Regime now)` | THE PEG (when it is in force; a short note when it is only staged): the peg with steps to devalue or revalue, the shadow rate, what the vault did this month, its cover and its pace to the floor, how a peg breaks, the ... |
| 3685 | 3 | `private static double[] curTail(double[] all, int from)` | The last CURRENCY_CHART_MONTHS months of a History series (aligned to the months), from the index `from`. |
| 3690 | 5 | `private static double[] curFlat(int n, double level)` | A flat line `n` months long: a level the chart draws across it. |
| 3697 | 7 | `private TimeChart curChart(List<Integer> months, List<TimeChart.Line> lines, TimeChart.Axis axis, double width, double height)` | A small chart of the given lines over `months`, on the left axis given. |
| 3706 | 4 | `private TimeChart.Line curLine(String key, String label, String colour, double[] values, java.util.function.DoubleFunction<Stri...` | One of the page's lines: History's series `key` over the last months, on the left axis, drawn in `colour`, read with `reads`. |
| 3712 | 28 | `Node pegCharts()` | Two small charts under a peg: the rate (the peg while it holds) beside the shadow, and the vault's cover with its floor marked. |

### THE BAND (lines 3741-3830)

| line | len | member | says |
|---:|---:|---|---|
| 3744 | 10 | `String bandInfo()` | THE BAND's (i): the three settings, the two edges and what the vault does at each. |
| 3756 | 15 | `String bandMonthWords(ForeignAccounts fx)` | What the band did this month in a line: the weak edge touched, the strong, or inside (pure). |
| 3778 | 33 | `VBox bandCard(ForeignAccounts fx, ForeignAccounts.Regime now)` | THE BAND (when it is in force; a short note when it is only staged): the centre, slope and width with steps, the two edges, what the band did this month, the shadow's gap from the centre, and the rate and the shadow o... |
| 3813 | 17 | `Node bandChart(ForeignAccounts fx)` | The rate and the shadow over the last CURRENCY_CHART_MONTHS months with today's two edges drawn flat across them, labelled "the band as set now". |

### WHO BUYS AT THE STRONG EDGE (lines 3831-3883)

| line | len | member | says |
|---:|---:|---|---|
| 3834 | 6 | `static String payerSays(ForeignAccounts.EdgePayer p)` | Each payer's line, under its chip: what paying that way does (star 6's own words). |
| 3842 | 6 | `static String payerApplies(ForeignAccounts.EdgePayer p)` | What the payer's Apply says: the chip's words, and that it buys at the strong edge. |
| 3850 | 8 | `static String payerInfo()` | WHO BUYS AT THE STRONG EDGE's (i). |
| 3860 | 23 | `VBox payerCard(ForeignAccounts fx)` | WHO BUYS AT THE STRONG EDGE (star 6): the two payers as chips, the default marked, what is owed now and what has been bought since founding. |

### WHAT THE DIAL AIMS AT (lines 3884-3967)

| line | len | member | says |
|---:|---:|---|---|
| 3887 | 9 | `String aimSays(DebtManager.Aim a)` | Each aim's line, from the model's constants (star 13). |
| 3898 | 10 | `String aimInfo()` | WHAT THE DIAL AIMS AT's (i). |
| 3910 | 11 | `String aimDialNow(DebtManager.Aim a, ForeignAccounts fx)` | What each aim would set the dial to now, under the regime in force (pure): a rate; null for a rule that reads inflation before there is a year of prices to read. |
| 3923 | 4 | `String aimWouldSet(DebtManager.Aim a, ForeignAccounts fx)` | ...as a sentence: "It would set the dial to 3.10% now.", or that it has nothing to read yet. |
| 3935 | 32 | `VBox aimCard(ForeignAccounts fx)` | WHAT THE DIAL AIMS AT (star 13): inflation, the currency or a mix as chips, each with the dial it would set now under the regime in force; that it acts only with the autopilot on, with a door to where that is set; and... |

### THE DIAL UNDER A PEG (lines 3968-4020)

| line | len | member | says |
|---:|---:|---|---|
| 3971 | 8 | `static String pegDialInfo()` | THE DIAL UNDER A PEG's (i): the tie, the spread, and the trinity. |
| 3981 | 4 | `static String tiedWords(double dial, double spread, String plus, String minus)` | The tied dial in words: "2.50% = the US's 2.00% + the spread 0.50%", or with the joiners "plus" and "less" (pure). |
| 3992 | 28 | `VBox pegDialCard(ForeignAccounts fx)` | THE DIAL UNDER A PEG (star 5; the board's "the dial is still yours" became the tie): the dial as the US's rate plus the spread, the spread with quarter-point steps - or the rule's, with the autopilot aiming at the cur... |

### BUY OR SELL THE D$ (lines 4021-4053)

| line | len | member | says |
|---:|---:|---|---|
| 4024 | 12 | `String dealWords(ForeignAccounts fx)` | What a deal of DEAL_SHARE of a year's output would do to the rate, under the regime in force (pure). |
| 4038 | 7 | `String dealInfo()` | BUY OR SELL THE D$'s (i). |
| 4047 | 6 | `VBox dealCard(ForeignAccounts fx)` | BUY OR SELL THE D$: what a deal of 1% of a year's output would do, and a door to Trade's reserves, where the dealing is. |

### MONEY - the currency reform (lines 4054-4183)

| line | len | member | says |
|---:|---:|---|---|
| 4071 | 7 | `String reformInfo()` | P37. |
| 4093 | 65 | `void reformPage(VBox page)` | THE CURRENCY REFORM, which is a change of units and says so. |
| 4160 | 6 | `String foreignInfo()` | P40. |
| 4168 | 7 | `static VBox priceTag(String what, String before, String after)` | One price tag: what it is, today, and after the reform in the accent. |
| 4177 | 6 | `static String afterName(Denomination unit, double factor, Currency money)` | What the city's money would be called after lopping by this factor. |

### PROMISES - the pension (lines 4184-4358)

| line | len | member | says |
|---:|---:|---|---|
| 4216 | 1 | **type** `record Month(String word, String says, String tone)` | What a household's month means, in one of three forms (0.7.27, P42): its chip's word, its sentence and its verdict colour. |
| 4219 | 15 | `Month pensionerMonth()` | A pensioner household's month (pure: the probe reads it). |
| 4235 | 46 | `void pensionPage(VBox page)` |  |
| 4283 | 12 | `VBox contributionCard(EconomyManager em, TaxPolicy policy)` | WHAT WORKERS PAY IN (P-4): the contribution, its own Apply; collected, covered, out of the treasury and a payslip before and after (M3: struck on the wage bill, so a dial at nothing previews something - B9). |
| 4297 | 16 | `DoubleFunction<List<Effect>> contributionEffects(TaxPolicy policy)` | The contribution's effects at any value of its thumb (pure: the probe reads them). |
| 4315 | 19 | `VBox pensionDialCard(EconomyManager em, TaxPolicy policy, HouseholdBalance bal)` | WHAT SENIORS RECEIVE (P-5): the pension, of the FOUNDING unskilled wage (B10, D9), its own Apply; each senior, paid, out of the treasury and what one pensioner household would have. |
| 4336 | 22 | `DoubleFunction<List<Effect>> pensionEffects(TaxPolicy policy)` | The pension's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the out of work and the students (2026-09-11) (lines 4359-4412)

| line | len | member | says |
|---:|---:|---|---|
| 4382 | 3 | `static String cost(double v)` | Money a line takes out of the treasury, a surplus with a true minus: "$2.5M", "−$1.0M". |
| 4386 | 26 | `void outOfWorkPage(VBox page)` |  |

### PROMISES - food (0.7.45; the UI spec's 2.3 and D6) (lines 4413-4576)

| line | len | member | says |
|---:|---:|---|---|
| 4425 | 7 | `void foodPage(VBox page)` |  |
| 4434 | 6 | `String foodStatus(double share)` | The dial card's status (pure: the probe reads it): who a voucher would go to at the last sale's price. |
| 4449 | 15 | `VBox foodAssistanceCard(TaxPolicy policy)` | FOOD ASSISTANCE (0.7.43; its own tab since 0.7.45), its own Apply: the treasury's voucher toward an eligible household's baskets - what it would have paid at the last sale, the households who would hold one, the baske... |
| 4466 | 11 | `DoubleFunction<List<Effect>> foodEffects(TaxPolicy policy)` | The food assistance dial's effects at any value of its thumb (pure: the probe reads them). |
| 4479 | 1 | **type** `record AidRow(int row, String name, double got, String gotWords, String figures)` | One row of WHO WOULD GET IT, worked out without drawing it (pure: the probe reads them). |
| 4482 | 18 | `List<AidRow> aidRows()` | WHO WOULD GET IT's rows: every household row a voucher would reach or did, most eligible first. |
| 4502 | 4 | `static String basketShare(double share)` | Baskets got per basket needed, as a row says it: "got .58 of a basket", "got every basket". |
| 4508 | 18 | `VBox whoWouldGetIt()` | WHO WOULD GET IT (the UI spec's 2.3 item 3): a row a household kind, its baskets got per basket needed, who a voucher reaches. |
| 4528 | 11 | `VBox eiPremiumCard(EconomyManager em, TaxPolicy policy)` | What workers pay for EI (E-3), its own Apply: raised, and what EI and the grants take from the treasury. |
| 4541 | 8 | `DoubleFunction<List<Effect>> eiPremiumEffects(TaxPolicy policy)` | The EI premium's effects at any value of its thumb (pure: the probe reads them). |
| 4551 | 13 | `VBox eiBenefitCard(EconomyManager em, TaxPolicy policy, Unemployment u)` | What EI replaces (E-3), its own Apply: the pool's bill, a claimant's cheque and the treasury's share, struck on the pool (M5). |
| 4566 | 10 | `DoubleFunction<List<Effect>> eiBenefitEffects(TaxPolicy policy)` | The EI benefit's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the clinic's price, and a premium (2026-09-19) (lines 4577-4773)

| line | len | member | says |
|---:|---:|---|---|
| 4602 | 1 | **type** `record Corner(String word, String says)` | The corner the city is in (P62): its chip's word, and its sentence. |
| 4604 | 23 | `Corner corner()` |  |
| 4629 | 8 | `String coverWords()` | P63's line: what the fees and the premium cover - or that the service cost nothing (B19: it read "Fees cover 0% of the cost" in a city with fees and no cost). |
| 4638 | 40 | `void healthPage(VBox page)` |  |
| 4680 | 16 | `String turnedAwayInfo()` | P64, both layers. |
| 4698 | 26 | `VBox feeCard(Healthcare care, TaxPolicy policy)` | WHAT A PATIENT PAYS (S-4), its own Apply: the break-even marked on the dial, the three fees, the fees at full and the treasury's share - at full service both sides (B13). |
| 4726 | 18 | `DoubleFunction<List<Effect>> feeEffects(TaxPolicy policy)` | The care fee's effects at any value of its thumb (pure: the probe reads them). |
| 4746 | 17 | `VBox healthPremiumCard(EconomyManager em, Healthcare care, TaxPolicy policy)` | WHAT EVERY WAGE PAYS (S-5), its own Apply: raised, and the treasury's share. |
| 4765 | 8 | `DoubleFunction<List<Effect>> healthPremiumEffects(TaxPolicy policy)` | The health premium's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the schools: the price of a place, who pays it, and the (lines 4774-5269)

| line | len | member | says |
|---:|---:|---|---|
| 4825 | 1 | `static String schoolKey(EducationType kind)` | The staged key of one school kind's own price (0.7.6): "tuitionScale:UNIVERSITY". |
| 4828 | 1 | `static String scaleWords(double scale)` | A tuition scale as the page writes it: "x1.00". |
| 4831 | 13 | `static String grantWords(TaxPolicy.GrantBasis basis, double amount, double level)` | The grant in words, for a line that names it: "15% of an unskilled wage a month". |
| 4846 | 8 | `static String basisName(TaxPolicy.GrantBasis basis)` | What a basis is called on its chip. |
| 4856 | 4 | `static String amountWords(TaxPolicy.GrantBasis basis, double amount, double level)` | How a basis's amount reads on its dial: dollars, or a percentage of the thing it is a share of. |
| 4889 | 3 | `double stagedScaleOf(TaxPolicy policy, EducationType kind)` | The scale a preview prices one kind of school at: its own lever if staged, else every school at once if that is, else what it is (0.7.6). |
| 4893 | 34 | `void schoolsPage(VBox page)` |  |
| 4929 | 5 | `static List<EducationType> schoolKinds()` | The nine kinds a school can be, in EducationType order: everything bar NONE (TaxPolicy's own list, which it keeps to itself). |
| 4936 | 4 | `Lever register(Lever lever)` | A dial on this page, registered so the tray can name and apply it. |
| 4942 | 4 | `Lever kindLever(EducationType kind, TaxPolicy policy)` | One kind's own price, as a lever. |
| 4948 | 2 | **type** `record Course(EducationType kind, double now, double then, boolean standing, double places, double students...` | One course's row, worked out without drawing it: its burden now and staged, whether a school of it stands, and its four figures. |
| 4952 | 11 | `List<Course> courses(Education schools, TaxPolicy policy, double[] places)` | The nine courses (pure: the probe reads them): what a place costs a family as a share of a month's wage, at the city's price and share and at the staged ones (PolicyPreview.schoolBurden()). |
| 4965 | 14 | `VBox burdenRows(Education schools, TaxPolicy policy, double[] places)` | WHO CAN AFFORD A PLACE: the nine as bars on 0 to 100% of a month's wage with a tick at MAX_BURDEN, red past it; each opens into its own price. |
| 4981 | 44 | `HBox courseRow(Course c, double scale, boolean shown, Runnable toggle)` | One course: its name ("▸ its price"), its bar - red past the line where nobody enrols - with the staged burden a ghost, its figure, and "no school" when none stands. |
| 5033 | 28 | `VBox kindCard(Course c, TaxPolicy policy)` | One kind's own price, opened in place (K-3): its dial - greyed with nothing standing, since a price for a school the city does not have moves nothing today, though every school at once still sets it - its places, stud... |
| 5063 | 16 | `DoubleFunction<List<Effect>> kindEffects(Course c, TaxPolicy policy)` | A kind's own price's effects at any value of its thumb (pure: the probe reads them). |
| 5081 | 7 | `VBox shareCard(Lever lever, Education schools, TaxPolicy policy)` | The city's share of tuition (K-1, registered): what households pay and what the city waives before and after, and THE BUDGET. |
| 5090 | 14 | `DoubleFunction<List<Effect>> shareEffects(TaxPolicy policy)` | The city's share's effects at any value of its thumb (pure: the probe reads them). |
| 5106 | 11 | `VBox schoolsMonthCard(Education schools)` | THE SCHOOLS THIS MONTH (K-2), footing: staff and buildings, less what households paid, is the net cost; the tuition waived beside it in grey, outside the sum (B17, D13). |
| 5119 | 14 | `VBox everySchoolCard(Lever lever, TaxPolicy policy)` | Every school at once (K-3, registered): its dial ("a price per school" until moved, once the nine have parted), what the courses are billed and households pay, and THE BUDGET. |
| 5135 | 13 | `DoubleFunction<List<Effect>> everySchoolEffects(TaxPolicy policy)` | Every school's price's effects at any value of its thumb (pure: the probe reads them). |
| 5150 | 16 | `String grantInfo(TaxPolicy policy)` | P53, with its figures. |
| 5174 | 41 | `VBox grantCard(TaxPolicy policy)` | What a student is granted (K-5, registered): the basis as chips - a chip stages the basis AND today's grant re-expressed in its unit, so the switch alone changes nothing until the amount moves - and the amount's dial ... |
| 5217 | 15 | `DoubleFunction<List<Effect>> grantEffects(TaxPolicy policy)` | The grant's amount's effects at any value of its thumb (pure: the probe reads them). |
| 5245 | 14 | `VBox loanCard(Lever lever, TaxPolicy policy)` | What the loan costs them afterwards (K-6, registered): what is owed and repaid, and a month's interest and THE BUDGET before and after (HouseholdBalance.studentInterestAt()). |
| 5261 | 8 | `DoubleFunction<List<Effect>> loanEffects(TaxPolicy policy)` | The loan rate's effects at any value of its thumb (pure: the probe reads them). |

### PROMISES - the standing subsidies (lines 5270-5320)

| line | len | member | says |
|---:|---:|---|---|
| 5292 | 28 | `void subsidyPage(VBox page)` |  |


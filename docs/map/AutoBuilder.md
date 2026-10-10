# AutoBuilder.java - 1,147 lines · 86 methods · 10 constants · model

`ham/citybuildersim/AutoBuilder.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Automatic building (0.7.73): the city's own works ordered for the player
> once a month - power, water, the roads and their lines, care, schools and
> safety - each kept ahead of its demand with the player's spare margin past
> it, by the build advice's own ranking and count, paid out of the cash and,
> when the cash runs out, on the funding page's bond no further than the
> player's debt limit - since 0.7.81 the city's debt over a year of its GDP,
> over which it builds nothing, or with "Build from cash anyway" on, only
> what the cash pays for.
> 
> WHY. Jerus: "an automatic build and acquire debt button for basically
> automatic building, with a required slack button that you add, aka
> maintain say 15% surplus service of everything ... right in the build
> menu, and on/off, so that one can focus on other things." His decisions
> (2026-10-08): it builds services and infrastructure only - power, water,
> roads, transit, schools, health, childcare and safety; not land, not
> mines, wells or industry - and since 0.7.77 it buys the ground its own
> orders need (THE GROUND ITS ORDERS NEED, below). A slider for the debt
> limit - until 0.7.81 debt payments at most 15% of the city's revenue, and
> since then the city's debt over a year of GDP (THE DEBT LIMIT, A SHARE OF
> GDP, below) - and it borrows on the funding page's bonds only when the
> cash runs out. A slider for the spare
> margin, 15% by default, held for every service. On and off in the Build
> menu, off by default. Until now only the test player (LongPlaytest) built
> a city by itself, by rules of its own; this is the player's, in the game,
> and it builds what the Build overview advises.
> 
> THE MONTH (pass()), the first thing Game.nextMonth() does - between the
> presses, where the player's own Build lands, on the city the last press
> left and at its prices:
>   - THE MEASURES (remit()): every one the city's five Build categories
>     open on (BuildAdvice.measuresOf()) and the burial plots, NEEDS YOU's
>     listed ones first in its order, the rest in the rings' order. Transit
>     is not one of its own - it has no line, and the road's candidates are
>     the three roads and the three lines alike (BuildAdvice, A ROAD OVER
>     ITS LIFE), so a bus is built where it keeps the road ahead for less
>     over its life. A FIRST BUILDING WAITS FOR HALF ITS WORTH where one is
>     a town's worth (kept()): a school (CityNeeds.listsSchool(), the 0.7.51
>     rule above the ladder, down the ladder too), a police station, a
>     prison - FIRST_SHARE, the firms' first-plant share.
>   - THE ORDER: the advice's own card for the measure at the player's
>     margin (BuildAdvice.suggestFor(..., slack)) - nothing while what
>     stands and what is on site keep it ahead of the demand it will have
>     once an order now opens, with the margin past it; otherwise the
>     building the Build tab ranks first and the count that keeps it ahead.
>   - CUT, IN TURN: to THE BUILDERS - no more than opens inside
>     BusinessInvestment.MAX_ORDER_MONTHS, the businesses' own rule for an
>     order the queue can deliver, and one when none would and nothing for
>     the measure is on site, so a slow plant is not put off for ever; to
>     THE BUDGET, for a building the budget runs (staff, upkeep, a line's
>     crews) - what the year's revenue leaves (budgetRoom()), and past one
>     it cannot run the next in the advice's ranking; and to THE GROUND AND
>     THE MONEY together (0.7.77; the ground before the budget, and none
>     bought, until then) - the ground the order lacks bought as Build's
>     land shortcut buys it (groundFor()), and the ground and the order paid
>     for out of the cash over a month's tax (reserve()), then for the rest
>     the funding page's bond (Game.handleLongBondForCash(),
>     BUILD_BOND_YEARS), only as large as keeps the city's debt within the
>     limit (canPay()) - and with the debt already over it, nothing at all,
>     or with "Build from cash anyway" on, only what the cash pays for.
>   - PLACED through Build's own path (Game.buildStack(); Game.paveRoads()
> ... (24 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (58), [Game](Game.md) (41), [CityNeeds](CityNeeds.md) (15), [DecisionLog](DecisionLog.md) (14), [LandParcel](LandParcel.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (11), [LandManager](LandManager.md) (7), [DebtQuote](DebtQuote.md) (6), [NationalAccounts](NationalAccounts.md) (3), [BuildingManager](BuildingManager.md) (2), [ConstructionControl](ConstructionControl.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [LandMarket](LandMarket.md) (2), [Sector](Sector.md) (1), [Statement](Statement.md) (1), [CareType](CareType.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1), [GameLog](GameLog.md) (1)

**Used by (5):** [AutoBuildCheck](AutoBuildCheck.md), [BuildScreen](BuildScreen.md), [DataSave](DataSave.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md)

## Sections

| line | section |
|---:|---|
| 99 | THE SETTINGS |
| 196 | WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK |
| 292 | THE MEASURES |
| 391 | THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6) |
| 550 | THE PASS |
| 874 | THE GROUND ITS ORDERS NEED (0.7.77, batch N5) |
| 1064 | THE SAVE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 557 | `AutoBuilder.Outcome.AHEAD` | What stands and what is on site keep it ahead of its projection with the margin. |
| 559 | `AutoBuilder.Outcome.ORDERED` | An order placed. |
| 561 | `AutoBuilder.Outcome.WAITING` | Works for it are on site and the builders could open no more inside a year. |
| 563 | `AutoBuilder.Outcome.HELD` | The builders have no site output, or no bare ground is to be had for one (since 0.7.77 it buys the rest), or the budget cannot run one, or the limit - over it, the cash - would not pay for one. |
| 565 | `AutoBuilder.Outcome.NOTHING` | No building the city could staff moves it (the advice offers nothing). |
| 569 | `AutoBuilder.Cut.NONE` |  |
| 569 | `AutoBuilder.Cut.BUILDERS` |  |
| 569 | `AutoBuilder.Cut.GROUND` |  |
| 569 | `AutoBuilder.Cut.BUDGET` |  |
| 569 | `AutoBuilder.Cut.DEBT` |  |
| 569 | `AutoBuilder.Cut.CASH` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `AutoBuilder.DEFAULT_SLACK` | `.15` | The spare margin a new city is given (Jerus, 2026-10-08: "maintain say 15% surplus service of everything"). |
| 113 | `AutoBuilder.DEFAULT_DEBT_LIMIT` | `.60` | The debt limit a new city is given, the city's debt over a year of its GDP (0.7.81, star N6-2): the Maastricht Treaty's reference value for government debt, 60% of GDP (Treaty on the Functioning of the European Union,... |
| 116 | `AutoBuilder.SLACK_MOST` | `.50` | The spare margin's slider runs from none - just enough - to this: half again what the city uses of every service (star N4-3). |
| 124 | `AutoBuilder.DEBT_LIMIT_MOST` | `3.0` | The debt limit's slider runs from none - with any debt it builds nothing - to three years of GDP (star N6-2): past the most any large government has owed in peace or war, about two and a half years - Britain's after W... |
| 127 | `AutoBuilder.STEP` | `.01` | The spare margin's step: a whole per cent (the debt limit's until 0.7.81). |
| 130 | `AutoBuilder.DEBT_STEP` | `.05` | The debt limit's step (0.7.81, star N6-4): five per cent of a year's GDP - sixty steps to DEBT_LIMIT_MOST, where whole per cents were three hundred on a slider a third of the Build page wide; DEFAULT_DEBT_LIMIT is one... |
| 133 | `AutoBuilder.LOG_MOST` | `40` | The most of its orders the log keeps, newest kept (BuildLog's cap). |
| 337 | `AutoBuilder.FIRST_SHARE` | `CityNeeds.FIRST_SCHOOL_SHARE` | A first building's share of need before it is built: the firms' first-plant share, as NEEDS YOU's first school (CityNeeds.FIRST_SCHOOL_SHARE). |
| 479 | `AutoBuilder.REVENUE_MONTHS` | `12` | Months of monthRevenue() the year's figure averages: a year, as a lender reads a city's accounts (star N4-2). |
| 908 | `AutoBuilder.Ground.NONE` | `new Ground(List.of(), 0, true)` | No ground to buy: the order fits what is free. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `private boolean on` |  |
| 136 | `private double slack` |  |
| 137 | `private double debtLimit` |  |
| 139 | `private boolean cashAnyway` | "Build from cash anyway" (0.7.81): over the debt limit, build what the cash alone pays for, never borrowing. |
| 202 | `public int month` |  |
| 203 | `public String measure` |  |
| 204 | `public String building` |  |
| 205 | `public int count` |  |
| 207 | `public int wanted` | What the advice's card asked for; more than count when the builders, the ground, the budget or the limit cut it. |
| 208 | `public double cost` |  |
| 210 | `public double borrowed` | What the funding page's bond brought for it; 0 out of the cash. |
| 211 | `public boolean paving` |  |
| 212 | `public String why` |  |
| 214 | `public double landSqFt` | The ground it bought for the order (0.7.77): its dry square feet, its price in local money at the day's rate, and the offers' places ("West 3, North 1"); 0 and null for none. |
| 215 | `public double landCost` |  |
| 216 | `public String landWhere` |  |
| 247 | `private final List<Entry> log` |  |
| 250 | `private final List<String> held` | What held a service back at the last pass: one line a measure, for the inbox and the card. |
| 253 | `private int lastPass` | The month of the last pass, 0 for none. |
| 255 | `private int orders` |  |
| 256 | `private long buildings` |  |
| 257 | `private double spent, borrowed` |  |
| 258 | `private int bonds` |  |
| 260 | `private double landSqFt, landSpent` | The ground it has bought for its orders (0.7.77): dry square feet, its price in local money, and how many offers. |
| 261 | `private int landOffers` |  |
| 264 | `private final List<String> bought` | The ground the last pass bought, a line a purchase, for the inbox (not saved: the pass that writes it runs before the month's notices are taken, loaded or not). |
| 482 | `private final List<Double> recent` | monthRevenue() at each of the last REVENUE_MONTHS passes, oldest first: saved. |
| 592 | `private final List<Step> steps` |  |
| 991 | `private boolean overSaid` | Whether this pass has said why over the limit it builds nothing: the first such line says it whole, the rest in short (0.7.81). |
| 1070 | `public boolean on` |  |
| 1071 | `public double slack` |  |
| 1073 | `public Double debtToGdp` | The debt limit over a year of GDP (0.7.81): a save from before has none - its "debtLimit" was a share of revenue, which nothing reads now - and loads DEFAULT_DEBT_LIMIT. |
| 1075 | `public boolean cashAnyway` | "Build from cash anyway" (0.7.81): off in a save from before. |
| 1076 | `public List<Entry> log` |  |
| 1077 | `public List<String> held` |  |
| 1078 | `public int lastPass` |  |
| 1079 | `public int orders` |  |
| 1080 | `public long buildings` |  |
| 1081 | `public double spent` |  |
| 1082 | `public double borrowed` |  |
| 1083 | `public int bonds` |  |
| 1084 | `public List<Double> recent` |  |
| 1086 | `public double landSqFt` | The ground bought for its orders (0.7.77): a save from before has none. |
| 1087 | `public double landSpent` |  |
| 1088 | `public int landOffers` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 97 | 1051 | **type** `public final class AutoBuilder` | Automatic building (0.7.73): the city's own works ordered for the player once a month - power, water, the roads and their lines, care, schools and safety - each kept ahead of its demand with the player's spare margin ... |

### THE SETTINGS (lines 99-195)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 1 | `public boolean isOn()` |  |
| 142 | 1 | `public double getSlack()` |  |
| 144 | 1 | `public double getDebtLimit()` | The debt limit: the city's debt over a year of its GDP (0.7.81; debt payments over revenue until then). |
| 145 | 1 | `public boolean isCashAnyway()` |  |
| 148 | 3 | `static double onSteps(double v, double most)` | A slider's value on its own steps and inside its ends. |
| 153 | 5 | `static double onSteps(double v, double most, double step)` | ...on steps of the caller's. |
| 160 | 9 | `public void setOn(boolean on, DecisionLog decisions)` | On or off; the change is a decision (DecisionLog.CONSTRUCTION). |
| 171 | 7 | `public void setSlack(double slack, DecisionLog decisions)` | The spare margin, on its slider's steps. |
| 180 | 7 | `public void setDebtLimit(double limit, DecisionLog decisions)` | The debt limit, on its slider's steps. |
| 189 | 6 | `public void setCashAnyway(boolean anyway, DecisionLog decisions)` | "Build from cash anyway", on or off; the change is a decision. |

### WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK (lines 196-291)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 45 | **type** `public static final class Entry` | One order it placed, and why. |
| 218 | 1 | `public Entry()` _(in AutoBuilder.Entry)_ |  |
| 220 | 12 | `Entry(int month, String measure, String building, int count, int wanted, double cost, double borrowed, boolean paving, String why)` _(in AutoBuilder.Entry)_ |  |
| 233 | 1 | `public int month()` _(in AutoBuilder.Entry)_ |  |
| 234 | 1 | `public String measure()` _(in AutoBuilder.Entry)_ |  |
| 235 | 1 | `public String building()` _(in AutoBuilder.Entry)_ |  |
| 236 | 1 | `public int count()` _(in AutoBuilder.Entry)_ |  |
| 237 | 1 | `public int wanted()` _(in AutoBuilder.Entry)_ |  |
| 238 | 1 | `public double cost()` _(in AutoBuilder.Entry)_ |  |
| 239 | 1 | `public double borrowed()` _(in AutoBuilder.Entry)_ |  |
| 240 | 1 | `public boolean paving()` _(in AutoBuilder.Entry)_ |  |
| 241 | 1 | `public String why()` _(in AutoBuilder.Entry)_ |  |
| 242 | 1 | `public double landSqFt()` _(in AutoBuilder.Entry)_ |  |
| 243 | 1 | `public double landCost()` _(in AutoBuilder.Entry)_ |  |
| 244 | 1 | `public String landWhere()` _(in AutoBuilder.Entry)_ |  |
| 267 | 1 | `public List<Entry> log()` | Its orders, oldest first. |
| 270 | 5 | `public List<Entry> latest(int n)` | ...newest first, at most n. |
| 277 | 1 | `public List<String> held()` | What held it back at the last pass, a line a measure; empty when nothing did. |
| 279 | 1 | `public int getLastPass()` |  |
| 280 | 1 | `public int getOrders()` |  |
| 281 | 1 | `public long getBuildings()` |  |
| 282 | 1 | `public double getSpent()` |  |
| 283 | 1 | `public double getBorrowed()` |  |
| 284 | 1 | `public int getBonds()` |  |
| 285 | 1 | `public double getLandSqFt()` |  |
| 286 | 1 | `public double getLandSpent()` |  |
| 287 | 1 | `public int getLandOffers()` |  |
| 290 | 1 | `public List<String> bought()` | The ground the last pass bought for its orders, a line a purchase saying what and why; empty when it bought none. |

### THE MEASURES (lines 292-390)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 5 | `public static boolean inRemit(BuildingsTemplate t)` | A remit building: one of the city's five Build categories - utilities, roads and transit, healthcare, education, safety. |
| 304 | 6 | `static CityNeeds.Need rowFor(List<CityNeeds.Need> all, BuildAdvice.Measure m)` | NEEDS YOU's row for exactly this measure (the dead, not the plots, for death care), or null for none. |
| 327 | 8 | `static boolean kept(Game game, BuildAdvice.Measure m, List<CityNeeds.Need> all)` | Whether a measure is one the pass keeps this month: transit never (the road's candidates carry it); a school above the ladder only with a NEEDS YOU row; and a FIRST SCHOOL ON THE LADDER, too, only for a school's worth... |
| 340 | 11 | `static int serving(Game game, BuildAdvice.Measure m)` | Buildings that serve a measure, standing and on site. |
| 353 | 10 | `static boolean firstWarranted(Game game, BuildAdvice.Measure m)` | With one standing or on site, yes; with none, whether what the city lacks today fills FIRST_SHARE of the smallest building that serves the measure (supplyDemand()'s unit: officers, cells). |
| 365 | 17 | `public static List<BuildAdvice.Measure> remit(Game game, List<CityNeeds.Need> all)` | Every measure the pass keeps this month, in its order: NEEDS YOU's listed ones first, in its order, then the rest in the rings' order. |
| 384 | 1 | `public BuildAdvice.Ahead target()` | The spare margin's demand: today's, padded by the slack, no projection. |
| 387 | 3 | `public boolean atTarget(Game game, BuildAdvice.Measure m)` | Whether what stands keeps a measure at its target: off NEEDS YOU's list, and a served gauge at 100%, of today's demand with the spare margin past it (BuildAdvice.ahead()). |

### THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6) (lines 391-549)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 6 | `public static double annualGdp(Game game)` | A year of the city's output: the last twelve months', scaled up to a year from fewer (as the screens read it, Pieces.annualGdp()); 0 with none recorded. |
| 431 | 3 | `public static double debt(Game game)` | The city's debt (star N6-3): its bonds and bills, as the left panel's Debt/GDP reads it. |
| 436 | 3 | `public static double debtAfter(Game game, DebtQuote q)` | ...once a bond is on the books: its face added. |
| 441 | 3 | `public static double ratio(Game game)` | The city's debt over a year of its GDP: 0 owing nothing, +∞ owing with no GDP recorded. |
| 446 | 3 | `public static double ratioAfter(Game game, DebtQuote q)` | ...once a bond is on the books. |
| 450 | 4 | `private static double over(double debt, double gdp)` |  |
| 456 | 3 | `public boolean within(Game game)` | Whether the city's debt is at or under the limit: it builds, and may borrow up to it; over it, it builds only from cash with cashAnyway on. |
| 471 | 6 | `public static double monthRevenue(Game game)` | A month's revenue the city can count on (star N4-2): the month's take, the Finances tab's, less what the city's growth brings in once - its land sales, and the builders' sales tax and profit tax, which follow the buil... |
| 494 | 7 | `public double revenue(Game game)` | The revenue the budget is read against (star N4-2; the limit too until 0.7.81): the lesser of the month's (monthRevenue()) and its average over the last REVENUE_MONTHS passes, so a month that swells - a batch of mortg... |
| 503 | 4 | `void remember(Game game)` | What a pass records first: this month's monthRevenue(), the oldest dropped past REVENUE_MONTHS. |
| 509 | 5 | `static DebtQuote bondFor(Game game, double gap)` | The funding page's bond for `gap` of cash: its quote, or null for none to be had. |
| 525 | 3 | `public static double reserve(Game game)` | The cash it keeps (star N4-5): a month's tax, the line under which NEEDS YOU lists the treasury (CityNeeds.taxRaised()). |
| 530 | 4 | `public static double spendable(Game game)` | What an order may take of the cash: what is over the reserve; an overdraft counted in full, as Build's funding page counts it (Game.buildFundingGap()). |
| 536 | 3 | `public static double gapFor(Game game, double total)` | What a bond must bring for an order of `total`: the part the spendable cash does not cover. |
| 541 | 8 | `boolean canPay(Game game, double total)` | Whether `total` is paid for: at or under the limit, by the cash and past it a bond that keeps the debt within it; over it, by the cash alone with cashAnyway on, and otherwise not at all (0.7.81). |

### THE PASS (lines 550-873)

| line | len | member | says |
|---:|---:|---|---|
| 555 | 12 | **type** `public enum Outcome` | What the pass did for one measure. |
| 569 | 1 | **type** `public enum Cut` | Why a count was cut or an order held: DEBT the limit - over it, or a bond that would cross it; CASH (0.7.81) over it with "Build from cash anyway" on, to what the cash pays for. |
| 577 | 14 | **type** `public record Step(BuildAdvice.Measure measure, Outcome outcome, String building, int wanted, int ordered, ...` | One measure at the last pass: what it did, and why - and since 0.7.77 the ground its order lacked when it was weighed (landShort, square feet past what was free), the offers it bought for it and their price in local m... |
| 579 | 4 | `Step(BuildAdvice.Measure measure, Outcome outcome, String building, int wanted, int ordered, Cut cut, double cost, double borro...` _(in AutoBuilder.Step)_ |  |
| 585 | 5 | `public double landSqFt()` _(in AutoBuilder.Step)_ | The dry ground it bought, in square feet. |
| 614 | 4 | `public static double runningSpend(Game game)` | A month's spending that is not a building or land: what the city pays to run itself (NationalAccounts). |
| 620 | 10 | `public static double onSiteRunning(Game game)` | A month of running the staffed services on site, once they open. |
| 632 | 4 | `public static boolean staffedBuilding(BuildingsTemplate t)` | A building the budget runs: care, death care, a school, the police, the cells - and a transit line, whose crews the treasury pays (0.7.49). |
| 638 | 3 | `public double budgetRoom(Game game)` | What the year's revenue leaves a month for running a new staffed service: revenue() less runningSpend() and onSiteRunning(). |
| 643 | 1 | `public List<Step> steps()` | The last pass, a step a measure (not saved: a harness reads it the month it is made). |
| 649 | 17 | `public void pass(Game game)` | The month's pass (see the header): nothing while it is off. |
| 668 | 114 | `private void step(Game game, List<CityNeeds.Need> all, BuildAdvice.Measure m)` | One measure's order, cut and placed. |
| 784 | 65 | `private void place(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, String name, int wanted, int n, Cut cut, Ground ...` | Places n of a suggestion, its ground bought first (0.7.77), borrowing what the spendable cash does not cover, and writes it down. |
| 851 | 3 | `private static String money(double thousands)` | Thousands of local money as the decision log writes them ("$1.2M"). |
| 862 | 11 | `private static String boughtWords(BuildAdvice.Measure m, String name, int n, List<LandParcel> got, double landCost, double land...` | The inbox's line for ground bought (0.7.77): which offers, how much and at what price, the order it was for and what that order lacked - and which of the shortcut's rules picked it: one bare offer covering the shortfa... |

### THE GROUND ITS ORDERS NEED (0.7.77, batch N5) (lines 874-1063)

| line | len | member | says |
|---:|---:|---|---|
| 906 | 11 | **type** `public record Ground(List<LandParcel> offers, double cash, boolean covers)` | The ground an order lacks, as the offers it would buy for it in turn, the cash that takes, and whether one offer covers it all. |
| 911 | 5 | `public double sqFt()` _(in AutoBuilder.Ground)_ | Their dry ground, in square feet. |
| 919 | 17 | `public static Ground groundFor(Game game, BuildingsTemplate t, int n)` | The ground n of a building lack past what is free, as groundFor() plans it: NONE when it fits, null when no bare ground on offer covers it. |
| 946 | 4 | `public static double lacks(Game game, BuildingsTemplate t, int n)` | The ground n of a building lack, in square feet: what they need past what the city owns less what its buildings use - which a city whose buildings use more than it owns has below nothing (Jerus's at month 416, runs/fi... |
| 959 | 12 | `public static LandParcel shortcutOffer(Game game, double lackSqFt, Set<Integer> taken)` | The offer Build's land shortcut would buy for `lackSqFt` of dry ground, the offers in `taken` passed over, bare ground only: the cheapest whose dry ground covers it, the nearer on a tie (Game.bestOffer()'s shortfall r... |
| 973 | 4 | `static double wait(Game game, BuildAdvice.Suggestion s, int n)` | An order's wait for n: a paving's on the Paved Road site, a building's in the queue. |
| 979 | 4 | `static double total(Game game, BuildAdvice.Suggestion s, int n)` | What n of the order costs: Game.quoteBuild(), or Game.quotePave() for a paving. |
| 984 | 5 | `private void hold(BuildAdvice.Measure m, BuildAdvice.Suggestion s, Cut cut, int wanted, String line)` |  |
| 994 | 5 | `public static String gdpShare(double share)` | A share of GDP as the card writes it: "262%", "7%", "0%", and a place under a tenth: "0.4%", "7.2%". |
| 1001 | 25 | `private String debtWords(Game game, BuildAdvice.Suggestion s, Ground ground)` | Why the money pays for none: one more and (0.7.77) the ground it lacks - over the limit, or a bond that would cross it (0.7.81). |
| 1028 | 10 | `static String nothingWords(Game game, BuildAdvice.Measure m)` | Why nothing the city could build moves a measure short of its margin: water past the fresh water it owns with no sea, or no building it could staff. |
| 1039 | 11 | `static String cutWords(Cut cut, int wanted, int n)` |  |
| 1052 | 11 | `public static String reading(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added)` | A measure's figure as the Build tab reads it: served for a served gauge, its own reading for the rest. |

### THE SAVE (lines 1064-1147)

| line | len | member | says |
|---:|---:|---|---|
| 1069 | 21 | **type** `public static final class State` | What is saved, under one key (DataSave.autoBuild). |
| 1091 | 20 | `public State toState()` |  |
| 1113 | 34 | `public void restore(State s)` | Puts a saved state back; null - a save from before 0.7.73 - is off, at the defaults, with nothing done; one from 0.7.73 to 0.7.80 keeps its switch and margin and reads the default limit, "Build from cash anyway" off. |


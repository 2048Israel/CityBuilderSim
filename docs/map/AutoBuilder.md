# AutoBuilder.java - 2,168 lines · 145 methods · 11 constants · model

`ham/citybuildersim/AutoBuilder.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Automatic building (0.7.73): the city's own works ordered for the player
> once a month - power, water, the roads and their lines, care, schools and
> safety - each kept ahead of its demand with the player's spare margin past
> it, by the build advice's own ranking and count, paid out of the cash and,
> when the cash runs out, on the funding page's bond no further than the
> player's debt limit - since 0.7.81 the city's debt over a year of its GDP,
> since 0.7.101 all it owes over the output it has recorded - over which it
> builds nothing, or with "Build from cash anyway" on, only
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
> FIXED, 0.7.101 (batch P2; Jerus's decisions A4 to A6 of 2026-10-10, "Fix
> it"): the auto-built playtest's seeds 0 to 7 stalled or collapsed in five
> cities of eight (runs/fixP2-notes.md). It now chooses by the long run - the
> advice's figure is each building's order over its life for what it will
> serve (BuildAdvice, A BUILDING OVER ITS LIFE); builds the first of a kind
> where there is none, from the cash alone (kept(), pays()); walks down the
> advice's ranking past a building the money, the ground or the budget
> cannot pay for or run one of (step()); buys only an order's own ground
> (lacks()); and its debt limit counts everything the city owes over the
> output it has recorded (THE DEBT LIMIT), 240% of it by default, the dial
> to 600%. THE BUDGET STAYED (star P2-3): Jerus asked for the debt limit
> alone, and without the budget a village's first school, paid from its
> founding cash, ran it into a deficit no debt limit sees before the order
> - the limit binds once the debt is there and the deficit goes on - so
> three of the eight auto-built seeds stalled for a thousand months or more
> even with the limit counting everything; with it all eight passed 1.7M
> people (runs/fixP2-notes.md).
> 
> SMARTER, 0.7.104 (batch P2b; Jerus's decision of 2026-10-10 on the budget
> P2 kept: "don't keep it as is, and don't remove it. Make it smarter"): an
> ESSENTIAL SERVICE - power, water, care of every kind, death care - is
> held by the debt limit, the money and the ground alone, and a deficit to
> run it is the city's to carry (essential()); every other building the
> treasury runs - a school, a police station, a prison, a transit line - is
> ordered only as far as it PAYS BACK over its life: its running against
> what the residents it covers pay, past what each already costs the city
> (THE PAYBACK, payback()). The budget is gone (runs/fixP2b-notes.md).
> 
> THE MONTH (pass()), the first thing Game.nextMonth() does - between the
> presses, where the player's own Build lands, on the city the last press
> left and at its prices:
>   - THE MEASURES (remit()): every one the city's five Build categories
>     open on (BuildAdvice.measuresOf()) and the burial plots, NEEDS YOU's
> ... (89 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (105), [Game](Game.md) (69), [BuildingsTemplate](BuildingsTemplate.md) (23), [DecisionLog](DecisionLog.md) (19), [DebtQuote](DebtQuote.md) (16), [LandParcel](LandParcel.md) (16), [CityNeeds](CityNeeds.md) (15), [Auction](Auction.md) (10), [LandManager](LandManager.md) (9), [CapSetting](CapSetting.md) (8), [GameLog](GameLog.md) (4), [LandMarket](LandMarket.md) (4), [NationalAccounts](NationalAccounts.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [ConstructionControl](ConstructionControl.md) (3), [Landmasses](Landmasses.md) (3), [Ports](Ports.md) (2), [BuildingManager](BuildingManager.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [StrategicReserve](StrategicReserve.md) (1), [CareType](CareType.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1), [Sector](Sector.md) (1), [Statement](Statement.md) (1), [BuildingType](BuildingType.md) (1), [TimeSkipReport](TimeSkipReport.md) (1), [SaveCopy](SaveCopy.md) (1)

**Used by (10):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [DataSave](DataSave.md), [Game](Game.md), [LandClassCheck](LandClassCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [SaveSlotCheck](SaveSlotCheck.md)

## Sections

| line | section |
|---:|---|
| 165 | THE SETTINGS |
| 276 | WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK |
| 434 | THE MEASURES |
| 546 | THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6) |
| 747 | THE PASS |
| 1405 | THE GROUND ITS ORDERS NEED (0.7.77, batch N5) |
| 1651 | THE CAP AND THE OPEN ASKS (CD2, slice 6; D28 and D30) |
| 2022 | THE SAVE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 754 | `AutoBuilder.Outcome.AHEAD` | What stands and what is on site keep it ahead of its projection with the margin. |
| 756 | `AutoBuilder.Outcome.ORDERED` | An order placed. |
| 758 | `AutoBuilder.Outcome.WAITING` | Works for it are on site and the builders could open no more inside a year. |
| 760 | `AutoBuilder.Outcome.HELD` | The builders have no site output, or no ground is to be had for one (since 0.7.77 it buys the rest), or none would pay back (since 0.7.104; the budget could not run one until then), or the limit - over it, the cash - ... |
| 762 | `AutoBuilder.Outcome.NOTHING` | No building the city could staff moves it (the advice offers nothing). |
| 766 | `AutoBuilder.Cut.NONE` |  |
| 766 | `AutoBuilder.Cut.BUILDERS` |  |
| 766 | `AutoBuilder.Cut.GROUND` |  |
| 766 | `AutoBuilder.Cut.PAYBACK` |  |
| 766 | `AutoBuilder.Cut.DEBT` |  |
| 766 | `AutoBuilder.Cut.CASH` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 170 | `AutoBuilder.DEFAULT_SLACK` | `.15` | The spare margin a new city is given (Jerus, 2026-10-08: "maintain say 15% surplus service of everything"). |
| 178 | `AutoBuilder.DEFAULT_DEBT_LIMIT` | `2.40` | The debt limit a new city is given, all the city owes over a year of its output (THE DEBT LIMIT): 240% (0.7.101, Jerus's decision A6 of 2026-10-10, "have the default at 240%"). |
| 185 | `AutoBuilder.OLD_DEFAULT_DEBT_LIMIT` | `.60` | The default from 0.7.81 to 0.7.100 (star N6-2): an older save that carries exactly it loads at DEFAULT_DEBT_LIMIT (star P2-5) - every save since 0.7.81 writes its limit whether or not the dial was touched, and automat... |
| 188 | `AutoBuilder.SLACK_MOST` | `.50` | The spare margin's slider runs from none - just enough - to this: half again what the city uses of every service (star N4-3). |
| 197 | `AutoBuilder.DEBT_LIMIT_MOST` | `6.0` | The debt limit's slider runs from none - with any debt it builds nothing - to six years of GDP (0.7.101, star P2-4): Jerus, "one should have the option for beyond 300%" - twice the old top, so a city past what any lar... |
| 200 | `AutoBuilder.STEP` | `.01` | The spare margin's step: a whole per cent (the debt limit's until 0.7.81). |
| 203 | `AutoBuilder.DEBT_STEP` | `.10` | The debt limit's step (0.7.101, star P2-4): ten per cent of a year's GDP - sixty steps to DEBT_LIMIT_MOST, as N6-4's five per cents were to three years; DEFAULT_DEBT_LIMIT and OLD_DEFAULT_DEBT_LIMIT are both on them. |
| 206 | `AutoBuilder.LOG_MOST` | `40` | The most of its orders the log keeps, newest kept (BuildLog's cap). |
| 400 | `AutoBuilder.HELD_BACK` | `"Automatic building holds it back; "` | What the advice card's line opens with (PT1-M3); the inbox's words for the payback follow. |
| 638 | `AutoBuilder.PAID_MONTHS` | `12` | Months of monthPaid() the payback's figure averages: a year, as a lender reads a city's accounts - a year of its own recurring revenue, the sale of its property left out, as a city's debt limit is set (Ontario's annua... |
| 1443 | `AutoBuilder.Ground.NONE` | `new Ground(List.of(), 0, true)` | No ground to buy: the order fits what is free. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 208 | `private boolean on` |  |
| 209 | `private double slack` |  |
| 210 | `private double debtLimit` |  |
| 212 | `private boolean cashAnyway` | "Build from cash anyway" (0.7.81): over the debt limit, build what the cash alone pays for, never borrowing. |
| 282 | `public int month` |  |
| 283 | `public String measure` |  |
| 284 | `public String building` |  |
| 285 | `public int count` |  |
| 287 | `public int wanted` | What the advice's card asked for; more than count when the builders, the ground, the payback (the budget until 0.7.104) or the limit cut it. |
| 288 | `public double cost` |  |
| 290 | `public double borrowed` | What the funding page's bond brought for it; 0 out of the cash. |
| 291 | `public boolean paving` |  |
| 292 | `public String why` |  |
| 294 | `public double landSqFt` | The ground it bought for the order (0.7.77): its dry square feet, its price in local money at the day's rate, and the offers' places ("West 3, North 1"); 0 and null for none. |
| 295 | `public double landCost` |  |
| 296 | `public String landWhere` |  |
| 336 | `private final List<Entry> log` |  |
| 339 | `private final List<String> held` | What held a service back at the last pass: one line a measure, for the inbox and the card. |
| 342 | `private int lastPass` | The month of the last pass, 0 for none. |
| 344 | `private int orders` |  |
| 345 | `private long buildings` |  |
| 346 | `private double spent, borrowed` |  |
| 347 | `private int bonds` |  |
| 349 | `private double landSqFt, landSpent` | The ground it has bought for its orders (0.7.77): dry square feet, its price in local money, and how many offers. |
| 350 | `private int landOffers` |  |
| 353 | `private final List<String> bought` | The ground the last pass bought, a line a purchase, for the inbox (not saved: the pass that writes it runs before the month's notices are taken, loaded or not). |
| 377 | `public String measure` |  |
| 378 | `public int templateId` |  |
| 379 | `public double runs, covered, paid, costs` |  |
| 397 | `private final List<PaybackHold> paybackHolds` | The last pass's payback holds (PT1-M3), in the order it weighed them. |
| 641 | `private final List<Double> paidMonths` | monthPaid() at each of the last PAID_MONTHS passes, oldest first: saved (0.7.104; the budget's revenue under "recent" until then, which nothing reads now). |
| 793 | `private final List<Step> steps` |  |
| 970 | `private final Map<BuildingsTemplate, Double> runningNow` | running() of each building this month, read once (costs() reads every one of the measure's own and every one on site, each weighing): not saved. |
| 971 | `private Game runningGame` |  |
| 972 | `private int runningMonth` |  |
| 1551 | `private boolean overSaid` | Whether this pass has said why over the limit it builds nothing: the first such line says it whole, the rest in short (0.7.81). |
| 1706 | `private Auction.Cap cap` | The highest yield the builder's bonds take (D28): fair plus a point by default. |
| 1729 | `public String measure` |  |
| 1730 | `public String building` |  |
| 1731 | `public int templateId` |  |
| 1732 | `public boolean paving` |  |
| 1733 | `public int count` |  |
| 1734 | `public double cost` |  |
| 1735 | `public double raised` |  |
| 1736 | `public double toRaise` |  |
| 1737 | `public int month` |  |
| 1758 | `private final List<OpenAsk> asks` | The open asks, oldest first; at most one a measure. |
| 1761 | `private final List<String> asked` | What the asks did at the last pass, a line each time one opened, was placed or was withdrawn: for the inbox (not saved, as bought is). |
| 2028 | `public boolean on` |  |
| 2029 | `public double slack` |  |
| 2031 | `public Double debtToGdp` | The debt limit over a year of GDP (0.7.81): a save from before has none - its "debtLimit" was a share of revenue, which nothing reads now - and loads DEFAULT_DEBT_LIMIT; one at OLD_DEFAULT_DEBT_LIMIT, the default unti... |
| 2033 | `public boolean cashAnyway` | "Build from cash anyway" (0.7.81): off in a save from before. |
| 2034 | `public List<Entry> log` |  |
| 2035 | `public List<String> held` |  |
| 2036 | `public int lastPass` |  |
| 2037 | `public int orders` |  |
| 2038 | `public long buildings` |  |
| 2039 | `public double spent` |  |
| 2040 | `public double borrowed` |  |
| 2041 | `public int bonds` |  |
| 2043 | `public List<Double> paid` | monthPaid() at the last PAID_MONTHS passes (0.7.104): a save from before has none - its "recent", the budget's revenue, is not read - and reads the month's until a pass records one. |
| 2045 | `public double landSqFt` | The ground bought for its orders (0.7.77): a save from before has none. |
| 2046 | `public double landSpent` |  |
| 2047 | `public int landOffers` |  |
| 2049 | `public CapSetting.Saved cap` | The cap its bonds take (CD2, D28), the mode by name and the tail or yield: a save without one reads the default, fair plus a point. |
| 2051 | `public List<OpenAsk> asks` | The asks open when it was saved (CD2, D30): each its order, what it raised, what is to raise and the month it opened. |
| 2053 | `public List<PaybackHold> paybackHolds` | What the last pass held back as not paying back (PT1-M3), for the advice's card: a save from before has none, and its cards say nothing until the next pass. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 163 | 2006 | **type** `public final class AutoBuilder` | Automatic building (0.7.73): the city's own works ordered for the player once a month - power, water, the roads and their lines, care, schools and safety - each kept ahead of its demand with the player's spare margin ... |

### THE SETTINGS (lines 165-275)

| line | len | member | says |
|---:|---:|---|---|
| 214 | 1 | `public boolean isOn()` |  |
| 215 | 1 | `public double getSlack()` |  |
| 217 | 1 | `public double getDebtLimit()` | The debt limit: all the city owes over a year of its output (0.7.101, THE DEBT LIMIT; its bonds and bills over a year of GDP from 0.7.81, debt payments over revenue until then). |
| 218 | 1 | `public boolean isCashAnyway()` |  |
| 221 | 3 | `static double onSteps(double v, double most)` | A slider's value on its own steps and inside its ends. |
| 226 | 5 | `static double onSteps(double v, double most, double step)` | ...on steps of the caller's. |
| 233 | 16 | `public void setOn(boolean on, DecisionLog decisions)` | On or off; the change is a decision (DecisionLog.CONSTRUCTION). |
| 251 | 7 | `public void setSlack(double slack, DecisionLog decisions)` | The spare margin, on its slider's steps. |
| 260 | 7 | `public void setDebtLimit(double limit, DecisionLog decisions)` | The debt limit, on its slider's steps. |
| 269 | 6 | `public void setCashAnyway(boolean anyway, DecisionLog decisions)` | "Build from cash anyway", on or off; the change is a decision. |

### WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK (lines 276-433)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 54 | **type** `public static final class Entry implements Cloneable` | One order it placed, and why. |
| 298 | 1 | `public Entry()` _(in AutoBuilder.Entry)_ |  |
| 301 | 7 | `Entry copy()` _(in AutoBuilder.Entry)_ | A copy that shares nothing with this entry: toState()'s, for the autosave (UP1). |
| 309 | 12 | `Entry(int month, String measure, String building, int count, int wanted, double cost, double borrowed, boolean paving, String why)` _(in AutoBuilder.Entry)_ |  |
| 322 | 1 | `public int month()` _(in AutoBuilder.Entry)_ |  |
| 323 | 1 | `public String measure()` _(in AutoBuilder.Entry)_ |  |
| 324 | 1 | `public String building()` _(in AutoBuilder.Entry)_ |  |
| 325 | 1 | `public int count()` _(in AutoBuilder.Entry)_ |  |
| 326 | 1 | `public int wanted()` _(in AutoBuilder.Entry)_ |  |
| 327 | 1 | `public double cost()` _(in AutoBuilder.Entry)_ |  |
| 328 | 1 | `public double borrowed()` _(in AutoBuilder.Entry)_ |  |
| 329 | 1 | `public boolean paving()` _(in AutoBuilder.Entry)_ |  |
| 330 | 1 | `public String why()` _(in AutoBuilder.Entry)_ |  |
| 331 | 1 | `public double landSqFt()` _(in AutoBuilder.Entry)_ |  |
| 332 | 1 | `public double landCost()` _(in AutoBuilder.Entry)_ |  |
| 333 | 1 | `public String landWhere()` _(in AutoBuilder.Entry)_ |  |
| 356 | 1 | `public List<Entry> log()` | Its orders, oldest first. |
| 359 | 5 | `public List<Entry> latest(int n)` | ...newest first, at most n. |
| 366 | 1 | `public List<String> held()` | What held it back at the last pass, a line a measure; empty when nothing did. |
| 376 | 19 | **type** `public static final class PaybackHold` | A building the last pass held back as not paying back (PT1-M3): its measure by label (as an ask names it), the building by template id, and the payback weighed for one, whose figures the inbox's line gives (paybackWor... |
| 381 | 1 | `public PaybackHold()` _(in AutoBuilder.PaybackHold)_ |  |
| 383 | 8 | `PaybackHold(String measure, int templateId, Payback p)` _(in AutoBuilder.PaybackHold)_ |  |
| 393 | 1 | `public Payback payback()` _(in AutoBuilder.PaybackHold)_ | The payback it weighed. |
| 407 | 7 | `public Payback heldForPayback(BuildAdvice.Suggestion s)` | The payback the last pass weighed for one of a card's building where it held it back as not paying back (PT1-M3): null where it did not - off, a paving, or not so for the card's measure and building. |
| 416 | 4 | `public String heldBackWords(BuildAdvice.Suggestion s)` | ...as the advice's card says it: HELD_BACK and the inbox's words (paybackWords()); null where it did not hold it back. |
| 421 | 1 | `public int getLastPass()` |  |
| 422 | 1 | `public int getOrders()` |  |
| 423 | 1 | `public long getBuildings()` |  |
| 424 | 1 | `public double getSpent()` |  |
| 425 | 1 | `public double getBorrowed()` |  |
| 426 | 1 | `public int getBonds()` |  |
| 427 | 1 | `public double getLandSqFt()` |  |
| 428 | 1 | `public double getLandSpent()` |  |
| 429 | 1 | `public int getLandOffers()` |  |
| 432 | 1 | `public List<String> bought()` | The ground the last pass bought for its orders, a line a purchase saying what and why; empty when it bought none. |

### THE MEASURES (lines 434-545)

| line | len | member | says |
|---:|---:|---|---|
| 444 | 6 | `public static boolean inRemit(BuildingsTemplate t)` | A remit building: one of the city's five Build categories - utilities, infrastructure, healthcare, education, safety. |
| 452 | 6 | `static CityNeeds.Need rowFor(List<CityNeeds.Need> all, BuildAdvice.Measure m)` | NEEDS YOU's row for exactly this measure (the dead, not the plots, for death care), or null for none. |
| 472 | 7 | `static boolean kept(Game game, BuildAdvice.Measure m, List<CityNeeds.Need> all)` | Whether a measure is one the pass keeps this month: transit never (the road's candidates carry it), and a school above the ladder only with a NEEDS YOU row (CityNeeds.listsSchool(), Jerus's 0.7.51 rule: "way too early... |
| 481 | 11 | `static int serving(Game game, BuildAdvice.Measure m)` | Buildings that serve a measure, standing and on site. |
| 499 | 5 | `public static boolean firstOfAKind(Game game, BuildAdvice.Measure m)` | Whether an order for a measure is a FIRST OF A KIND WHERE THERE IS NONE (0.7.101): a school down the ladder, a police station or a prison - the buildings that waited for half their worth until then - with none standin... |
| 514 | 3 | `public static boolean orderable(BuildingsTemplate t)` | A building the pass may order (FR3): a remit building (inRemit()) or a sea terminal, a berth of a port measure. |
| 519 | 18 | `public static List<BuildAdvice.Measure> remit(Game game, List<CityNeeds.Need> all)` | Every measure the pass keeps this month, in its order: NEEDS YOU's listed ones first, in its order, then the rest in the rings' order - and last (FR3) the berths of each kind of cargo, which NEEDS YOU has no row for. |
| 539 | 1 | `public BuildAdvice.Ahead target()` | The spare margin's demand: today's, padded by the slack, no projection. |
| 542 | 3 | `public boolean atTarget(Game game, BuildAdvice.Measure m)` | Whether what stands keeps a measure at its target: off NEEDS YOU's list, and a served gauge at 100%, of today's demand with the spare margin past it (BuildAdvice.ahead()). |

### THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6) (lines 546-746)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 4 | `public static double annualGdp(Game game)` | A year of the city's output (0.7.101): the last twelve months' as recorded (NationalAccounts.getAnnualGdp()) - a city younger than a year at what it has produced; 0 with none recorded. |
| 595 | 3 | `static double overdraft(double cash)` | What the treasury is overdrawn, 0 when it is not. |
| 600 | 4 | `public static double debt(Game game)` | The city's debt (0.7.101, star P2-9): everything it owes - its bonds and bills, what it is overdrawn, and its central bank's advances (DebtManager.getPricedDebt()'s three, read live); its bonds and bills alone until t... |
| 606 | 4 | `public static double debtAfter(Game game, DebtQuote q)` | ...once a bond is on the books: its face added, and the overdraft its cash clears taken off. |
| 612 | 3 | `public static double ratio(Game game)` | The city's debt over a year of its GDP: 0 owing nothing, +∞ owing with no GDP recorded. |
| 617 | 3 | `public static double ratioAfter(Game game, DebtQuote q)` | ...once a bond is on the books. |
| 621 | 4 | `private static double over(double debt, double gdp)` |  |
| 627 | 3 | `public boolean within(Game game)` | Whether the city's debt is at or under the limit: it builds, and may borrow up to it; over it, it builds only from cash with cashAnyway on. |
| 644 | 4 | `void remember(Game game)` | What a pass records first: this month's monthPaid(), the oldest dropped past PAID_MONTHS. |
| 659 | 3 | `static DebtQuote bondFor(Game game, double gap)` | The funding page's bond for `gap` of cash at the builder's own cap (D28): its quote, or null for none to be had. |
| 664 | 5 | `static DebtQuote bondFor(Game game, double gap, Auction.Cap cap)` | ...at a cap of the caller's. |
| 671 | 4 | `static DebtQuote bondQuote(Game game, double gap, Auction.Cap cap)` | The bond for `gap` as the auction would sell it, short or not: null where there is no gap or the paper cannot be issued. |
| 686 | 3 | `public static double reserve(Game game)` | The cash it keeps (star N4-5): a month's tax, the line under which NEEDS YOU lists the treasury (CityNeeds.taxRaised()). |
| 696 | 3 | `public static double spendable(Game game)` | What an order may take of the cash: what is over the reserve; an overdraft counted in full, as Build's funding page counts it (Game.buildFundingGap()); and since D30 less what open asks have raised (spendableFor()) - ... |
| 701 | 4 | `private static double overReserve(Game game)` | The cash over the reserve, or the overdraft in full: what spendable() was before D30's earmarks. |
| 714 | 7 | `double spendableFor(Game game, OpenAsk own)` | What the cash over the reserve leaves for ONE ask's order (D30): the spendable cash less what every OTHER open ask has raised, as far as the cash goes - the treasury's other bills may still draw on it, and when they h... |
| 723 | 3 | `public static double gapFor(Game game, double total)` | What a bond must bring for an order of `total`: the part the spendable cash does not cover. |
| 733 | 3 | `boolean pays(Game game, double total, boolean fromCash)` | Whether `total` is paid for an order (0.7.101): as canPay() says, and a FIRST OF A KIND WHERE THERE IS NONE (firstOfAKind()) out of the cash over the reserve alone, never on a bond (star P2-1): Jerus, "at first it doe... |
| 738 | 8 | `boolean canPay(Game game, double total)` | Whether `total` is paid for: at or under the limit, by the cash and past it a bond that keeps the debt within it; over it, by the cash alone with cashAnyway on, and otherwise not at all (0.7.81). |

### THE PASS (lines 747-1404)

| line | len | member | says |
|---:|---:|---|---|
| 752 | 12 | **type** `public enum Outcome` | What the pass did for one measure. |
| 766 | 1 | **type** `public enum Cut` | Why a count was cut or an order held: PAYBACK (0.7.104) what pays back over its life, for a building the treasury runs that is not an essential service - BUDGET, what the year's revenue left to run it, until then; DEB... |
| 777 | 15 | **type** `public record Step(BuildAdvice.Measure measure, Outcome outcome, String building, int wanted, int ordered, ...` | One measure at the last pass: what it did, and why - and since 0.7.77 the ground its order lacked when it was weighed (landShort, square feet past what was free), the offers it bought for it and their price in local m... |
| 780 | 4 | `Step(BuildAdvice.Measure measure, Outcome outcome, String building, int wanted, int ordered, Cut cut, double cost, double borro...` _(in AutoBuilder.Step)_ |  |
| 786 | 5 | `public double landSqFt()` _(in AutoBuilder.Step)_ | The dry ground it bought, in square feet. |
| 873 | 4 | `public static boolean staffedBuilding(BuildingsTemplate t)` | A building the treasury runs: care, death care, a school, the police, the cells - and a transit line, whose crews the treasury pays (0.7.49). |
| 890 | 6 | `public static boolean essential(BuildAdvice.Measure m)` | An ESSENTIAL SERVICE (0.7.104, star P2b-1): power, water, care of every kind and death care. |
| 898 | 3 | `public static boolean paysBackFirst(BuildAdvice.Measure m, BuildingsTemplate t)` | Whether the payback holds an order of t for measure m (0.7.104): a building the treasury runs (staffedBuilding()) for a service that is not essential (essential()) - a school, a police station, a prison, a transit line. |
| 914 | 9 | `public static double monthPaid(Game game)` | What a resident pays the city a month (0.7.104, star P2b-3): the city's recurring revenue - its taxes on wages, sales, property, business and industry, its contributions and premiums, its fees and fares, what its util... |
| 932 | 7 | `public double paid(Game game)` | What a resident pays the city a month, as the payback reads it (0.7.104): the lesser of this month's (monthPaid()) and its average over the last PAID_MONTHS passes, so a month that swells - a batch of mortgages insure... |
| 952 | 16 | `public double costs(Game game, BuildAdvice.Measure m)` | What each resident already costs the city a month (0.7.104, star P2b-3 - A BUILDING THAT HELPS THE CITY GROW ONLY WITH OTHERS): its running spend - all it spends but buildings and land, and but the arrears it paid dow... |
| 980 | 8 | `double running(Game game, BuildingsTemplate t)` | A month of running one (BuildAdvice.running(), held at nothing), read once a month: its wages, prices and fares are the month's, and what a pass orders goes on site, not into the network a line's fares are read on - s... |
| 990 | 3 | `static boolean ownMeasure(BuildAdvice.Measure m, BuildingsTemplate t)` | Whether t is one of the measure's own buildings (0.7.104): a road's or a line's, any of the Infrastructure category's roads and lines; otherwise those that serve it. |
| 1005 | 14 | `static double[] need(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added, BuildAdvice.Ahead at)` | A measure's need and its shortfall, in its own unit, with these buildings standing as well, at `at`'s demand (0.7.104): {need, short}. |
| 1032 | 21 | `public static double coveredOverLife(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> site, Map<BuildingsTempl...` | The residents an order covers, a month on average over its life (0.7.104, star P2b-2): each of its LIFE_YEARS years read at its middle month as the advice reads what an order serves (servedOverLife()) - from its openi... |
| 1061 | 8 | **type** `public record Payback(double runs, double covered, double paid, double costs)` | An order's payback (0.7.104), a month on average over its life at today's prices: what it costs to run (runs: running() for each), the residents it covers (coveredOverLife()), what each pays the city (paid()) and what... |
| 1063 | 1 | `public double net()` _(in AutoBuilder.Payback)_ | What the residents it covers pay the city a month past what they already cost it. |
| 1065 | 1 | `public boolean paysBack()` _(in AutoBuilder.Payback)_ | Whether it pays back: net() at least runs. |
| 1067 | 1 | `public double share()` _(in AutoBuilder.Payback)_ | net() over runs, nothing under nothing: 1 or more pays back. |
| 1071 | 7 | `public Payback payback(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, int n)` | n of a suggestion's building, weighed for its payback at the demand its count is sized to (its ahead()). |
| 1080 | 1 | `public List<Step> steps()` | The last pass, a step a measure (not saved: a harness reads it the month it is made). |
| 1086 | 23 | `public void pass(Game game)` | The month's pass (see the header): nothing while it is off. |
| 1111 | 1 | **type** `private record Refused(BuildAdvice.Suggestion s, Cut cut, int wanted, String line, Payback payback)` | A card the money or the ground could not pay for one of (0.7.101), or none of which would pay back (0.7.104): the suggestion, why, how many it asked for, the inbox's line for it, and the payback weighed for one (null ... |
| 1128 | 133 | `private void step(Game game, List<CityNeeds.Need> all, BuildAdvice.Measure m)` | One measure's order, cut and placed. |
| 1267 | 9 | `private static String groundWords(Game game, BuildingsTemplate t)` | Why no ground can be had for one (0.7.77; 0.7.101): no bare ground on offer to buy for it - or a city whose buildings already stand on more ground than it owns, where an order on ground of its own could not be placed ... |
| 1286 | 89 | `private boolean place(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, String name, int wanted, int n, Cut cut, Grou...` | Places n of a suggestion, its ground bought first (0.7.77), borrowing what the spendable cash does not cover, and writes it down; false where it could not (the order is then held, and an ask stays open). |
| 1377 | 3 | `private static String refusedName(BuildAdvice.Suggestion s)` | A refused card's building as its words name it: its own name, or the gravel roads a paving paves. |
| 1382 | 3 | `private static String money(double thousands)` | Thousands of local money as the decision log writes them ("$1.2M"). |
| 1393 | 11 | `private static String boughtWords(BuildAdvice.Measure m, String name, int n, List<LandParcel> got, double landCost, double land...` | The inbox's line for ground bought (0.7.77): which offers, how much and at what price, the order it was for and what that order lacked - and which of the shortcut's rules picked it: one bare offer covering the shortfa... |

### THE GROUND ITS ORDERS NEED (0.7.77, batch N5) (lines 1405-1650)

| line | len | member | says |
|---:|---:|---|---|
| 1441 | 11 | **type** `public record Ground(List<LandParcel> offers, double cash, boolean covers)` | The ground an order lacks, as the offers it would buy for it in turn, the cash that takes, and whether one offer covers it all. |
| 1446 | 5 | `public double sqFt()` _(in AutoBuilder.Ground)_ | Their dry ground, in square feet. |
| 1454 | 18 | `public static Ground groundFor(Game game, BuildingsTemplate t, int n)` | The ground n of a building lack past what is free, as groundFor() plans it: NONE when it fits, null when no bare ground on offer covers it - or (0.7.101) when the city's buildings already stand on more ground than it ... |
| 1474 | 3 | `private static double linkedSqFt(LandParcel o)` | An offer's dry ground that is room for the order, square feet: its whole dry ground (getSizeSqFt()) less what of it is on a landmass the city has not linked (ET5; LandParcel.getUnlinkedDryKm2()); the whole of it for a... |
| 1491 | 4 | `public static double lacks(Game game, BuildingsTemplate t, int n)` | The ground n of a building lack, in square feet: what they need past what is free (0.7.101) - only the order's own ground. |
| 1497 | 4 | `public static double overFull(Game game)` | How much more ground the city's buildings stand on than it owns, in square feet (0.7.101): 0 for a city within its ground. |
| 1510 | 21 | `public static LandParcel shortcutOffer(Game game, double lackSqFt, Set<Integer> taken)` | The offer Build's land shortcut would buy for `lackSqFt` of dry ground, the offers in `taken` passed over, bare ground only: the cheapest whose dry ground covers it, the nearer on a tie (Game.bestOffer()'s shortfall r... |
| 1533 | 4 | `static double wait(Game game, BuildAdvice.Suggestion s, int n)` | An order's wait for n: a paving's on the Paved Road site, a building's in the queue (a sea terminal's quoted on the landmass the rule gives it, as buildStack() is asked, ET5). |
| 1539 | 4 | `static double total(Game game, BuildAdvice.Suggestion s, int n)` | What n of the order costs: Game.quoteBuildOn() by the rule (ET5; Game.quoteBuild() for everything but a sea terminal), or Game.quotePave() for a paving. |
| 1544 | 5 | `private void hold(BuildAdvice.Measure m, BuildAdvice.Suggestion s, Cut cut, int wanted, String line, Payback pb)` |  |
| 1554 | 5 | `public static String gdpShare(double share)` | A share of GDP as the card writes it: "262%", "7%", "0%", and a place under a tenth: "0.4%", "7.2%". |
| 1561 | 25 | `private String debtWords(Game game, BuildAdvice.Suggestion s, Ground ground)` | Why the money pays for none: one more and (0.7.77) the ground it lacks - over the limit, or a bond that would cross it (0.7.81). |
| 1588 | 6 | `private String firstWords(Game game, BuildAdvice.Suggestion s, Ground ground)` | Why the cash pays for no first of a kind (0.7.101): built from the cash alone, and the cash over a month's tax short of one and its ground. |
| 1604 | 8 | `static String paybackWords(Payback p)` | Why one would not pay back (0.7.104), with its figure: what each of the residents it would cover pays the city a month and what each already costs it, on average over its life, and what that leaves against its running... |
| 1614 | 10 | `static String nothingWords(Game game, BuildAdvice.Measure m)` | Why nothing the city could build moves a measure short of its margin: water past the fresh water it owns with no sea, or no building it could staff. |
| 1625 | 11 | `static String cutWords(Cut cut, int wanted, int n)` |  |
| 1638 | 12 | `public static String reading(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added)` | A measure's figure as the Build tab reads it: served for a served gauge, its own reading for the rest. |

### THE CAP AND THE OPEN ASKS (CD2, slice 6; D28 and D30) (lines 1651-2021)

| line | len | member | says |
|---:|---:|---|---|
| 1709 | 1 | `public Auction.Cap getCap()` | The builder's own cap. |
| 1712 | 7 | `public void setCap(Auction.Cap cap, DecisionLog decisions)` | The cap, as the panel's chips set it; the change is a decision. |
| 1728 | 28 | **type** `public static final class OpenAsk` | One order whose bond sold short and which waits for its money (D30): the measure by its label, the building by its name and template id (-1 and paving for the gravel roads' paving), how many, what the order costs with... |
| 1739 | 1 | `public OpenAsk()` _(in AutoBuilder.OpenAsk)_ |  |
| 1741 | 1 | `public String measure()` _(in AutoBuilder.OpenAsk)_ |  |
| 1742 | 1 | `public String building()` _(in AutoBuilder.OpenAsk)_ |  |
| 1743 | 1 | `public boolean paving()` _(in AutoBuilder.OpenAsk)_ |  |
| 1744 | 1 | `public int count()` _(in AutoBuilder.OpenAsk)_ |  |
| 1746 | 1 | `public double cost()` _(in AutoBuilder.OpenAsk)_ | What the order costs, its ground with it, as last priced. |
| 1748 | 1 | `public double raised()` _(in AutoBuilder.OpenAsk)_ | What its bond sales have raised for it. |
| 1750 | 1 | `public double toRaise()` _(in AutoBuilder.OpenAsk)_ | What is still to raise. |
| 1752 | 1 | `public int monthOpened()` _(in AutoBuilder.OpenAsk)_ | The month it opened in. |
| 1754 | 1 | `public int monthsOpen(int now)` _(in AutoBuilder.OpenAsk)_ | How many months it has been open at `now`. |
| 1764 | 1 | `public List<OpenAsk> asks()` | The open asks, for the panel: the order, what is raised, what is to raise, the months open. |
| 1767 | 1 | `public List<String> asked()` | What the last pass did with the asks, a line an event; empty when nothing happened. |
| 1770 | 4 | `OpenAsk askFor(BuildAdvice.Measure m)` | The ask open for a measure, or null. |
| 1776 | 4 | `private boolean askOpenFor(String label)` | Whether an ask is open for the measure of this label. |
| 1789 | 3 | `double held(Game game, OpenAsk own)` | What the treasury holds for one ask's order right now: the cash over the reserve, less what every other ask has raised, and never under nothing (an overdraft is the treasury's, not a debt of the order's). |
| 1794 | 5 | `public double askedRaised()` | Everything open asks have raised: the money the builder holds for their orders. |
| 1805 | 5 | `public boolean withdraw(int index, DecisionLog decisions)` | The player withdraws an ask (the panel's button): what was raised stays in the treasury, and the log says so. |
| 1812 | 3 | `public boolean withdraw(int index)` | ...with no decision recorded. |
| 1817 | 10 | `private void withdrawAsk(OpenAsk a, String why, DecisionLog decisions)` | Takes an ask off the books and says why; what it raised is the treasury's. |
| 1829 | 1 | **type** `private record Askable(BuildAdvice.Suggestion s, String name, double cost, double gap, DebtQuote quote)` | A card the money refused because its bond came up short at the builder's cap: the card, its words, what one costs with its ground, the gap past the spendable cash, and the short quote. |
| 1832 | 9 | `private Askable askableFor(Game game, BuildAdvice.Suggestion s, String name, Ground one)` | The ask for one of a card, if its bond for the gap comes up short at the cap and the sale itself would stay within the limit; null where something else refused it. |
| 1843 | 1 | **type** `private record Sold(DebtQuote quote, double raised)` | What an offer of the bond raised, and the quote it was. |
| 1846 | 13 | `private Sold sell(Game game, OpenAsk ask, double need)` | Offers `need` of cash on the bond at the builder's cap and books what sells, for an ask: the money raised is the ask's. |
| 1861 | 8 | `private void noteSale(Game game, DebtQuote q, double raisedCash, double heldOpen)` | A bond the builder sold, entered for the time skip's report (TimeSkipReport.AutoSale). |
| 1871 | 15 | `private String askWords(OpenAsk a, DebtQuote q, double held)` | Where an ask stands, for the inbox and the card: its bond, what is raised and what is to raise. |
| 1892 | 20 | `private boolean openAsk(Game game, BuildAdvice.Measure m, Askable a)` | Opens the ask for a card (D30): sells the bond for what it will sell, keeps the money for the order, and holds the measure on it. |
| 1914 | 12 | `private BuildAdvice.Suggestion cardFor(Game game, List<CityNeeds.Need> all, BuildAdvice.Measure m, OpenAsk a)` | The advice's card for an ask's building, walking the ranking as the pass walks it; null where the advice no longer ranks it. |
| 1936 | 64 | `private void reoffer(Game game, List<CityNeeds.Need> all, List<BuildAdvice.Measure> kept, OpenAsk a)` | An open ask, each month before the pass ranks anything new (D30): still the advice's order? |
| 2007 | 3 | `private boolean covered(Game game, OpenAsk a)` | Whether what the treasury holds for an ask's order pays for it: the cash over the reserve, less every other ask's raised money (held()), at least the order's cost as priced now. |
| 2018 | 3 | `private double stillToRaise(Game game, OpenAsk a)` | What an ask has still to raise: the order's cost less the greater of what the treasury holds for it and what its sales have raised in all. |

### THE SAVE (lines 2022-2168)

| line | len | member | says |
|---:|---:|---|---|
| 2027 | 28 | **type** `public static final class State` | What is saved, under one key (DataSave.autoBuild). |
| 2056 | 25 | `public State toState()` |  |
| 2082 | 13 | `private static OpenAsk copyOf(OpenAsk a)` |  |
| 2097 | 12 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM): the open asks', and (PT1-M3) the payback holds'. |
| 2111 | 57 | `public void restore(State s)` | Puts a saved state back; null - a save from before 0.7.73 - is off, at the defaults, with nothing done; one from 0.7.73 to 0.7.80 keeps its switch and margin and reads the default limit, "Build from cash anyway" off; ... |


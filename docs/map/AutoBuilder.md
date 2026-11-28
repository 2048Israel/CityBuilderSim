# AutoBuilder.java - 3,068 lines · 182 methods · 16 constants · model

`ham/citybuildersim/AutoBuilder.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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
> ... (94 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (205), [Game](Game.md) (101), [BuildingsTemplate](BuildingsTemplate.md) (40), [LandParcel](LandParcel.md) (31), [DecisionLog](DecisionLog.md) (20), [DebtQuote](DebtQuote.md) (17), [CityNeeds](CityNeeds.md) (16), [LandManager](LandManager.md) (14), [Auction](Auction.md) (10), [WorksValue](WorksValue.md) (9), [LandMarket](LandMarket.md) (9), [Landmasses](Landmasses.md) (8), [CapSetting](CapSetting.md) (8), [Currency](Currency.md) (5), [GameLog](GameLog.md) (5), [ConstructionControl](ConstructionControl.md) (4), [BuildingManager](BuildingManager.md) (3), [NationalAccounts](NationalAccounts.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [Ports](Ports.md) (2), [NewUnit](NewUnit.md) (2), [BridgeView](BridgeView.md) (2), [StrategicReserve](StrategicReserve.md) (1), [CareType](CareType.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1), [Sector](Sector.md) (1), [Statement](Statement.md) (1), [LicenceFees](LicenceFees.md) (1), [BuildingType](BuildingType.md) (1)... and 4 more

**Used by (30):** [AdviceValueOracle](AdviceValueOracle.md), [AskProceedsCheck](AskProceedsCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [DataSave](DataSave.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FarmlandReserveCheck](FarmlandReserveCheck.md), [Game](Game.md), [HydroCheck](HydroCheck.md), [InvestCheck](InvestCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [KeptCheck](KeptCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerWiresCheck](PowerWiresCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SubstationCheck](SubstationCheck.md), [TownGridCheck](TownGridCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md)

## Sections

| line | section |
|---:|---|
| 170 | THE SETTINGS |
| 289 | WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK |
| 465 | THE MEASURES |
| 616 | THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6) |
| 852 | THE PASS |
| 1398 | · THE WIRES WITH THE POWER (PW9-G, 2026-11-28) |
| 1914 | THE ISLAND LINKS (AL1, 2026-11-09) |
| 2116 | THE GROUND ITS ORDERS NEED (0.7.77, batch N5) |
| 2452 | THE CAP AND THE OPEN ASKS (CD2, slice 6; D28 and D30) |
| 2900 | THE SAVE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 868 | `AutoBuilder.Outcome.AHEAD` | What stands and what is on site keep it ahead of its projection with the margin. |
| 870 | `AutoBuilder.Outcome.ORDERED` | An order placed. |
| 872 | `AutoBuilder.Outcome.WAITING` | Works for it are on site and the builders could open no more inside a year. |
| 874 | `AutoBuilder.Outcome.HELD` | The builders have no site output, or no ground is to be had for one (since 0.7.77 it buys the rest), or none would pay back (since 0.7.104; the budget could not run one until then), or the limit - over it, the cash - ... |
| 876 | `AutoBuilder.Outcome.NOTHING` | No building the city could staff moves it (the advice offers nothing). |
| 880 | `AutoBuilder.Cut.NONE` |  |
| 880 | `AutoBuilder.Cut.BUILDERS` |  |
| 880 | `AutoBuilder.Cut.GROUND` |  |
| 880 | `AutoBuilder.Cut.PAYBACK` |  |
| 880 | `AutoBuilder.Cut.DEBT` |  |
| 880 | `AutoBuilder.Cut.CASH` |  |
| 880 | `AutoBuilder.Cut.WIRES` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 175 | `AutoBuilder.DEFAULT_SLACK` | `.15` | The spare margin a new city is given (Jerus, 2026-10-08: "maintain say 15% surplus service of everything"). |
| 183 | `AutoBuilder.DEFAULT_DEBT_LIMIT` | `2.40` | The debt limit a new city is given, all the city owes over a year of its output (THE DEBT LIMIT): 240% (0.7.101, Jerus's decision A6 of 2026-10-10, "have the default at 240%"). |
| 190 | `AutoBuilder.OLD_DEFAULT_DEBT_LIMIT` | `.60` | The default from 0.7.81 to 0.7.100 (star N6-2): an older save that carries exactly it loads at DEFAULT_DEBT_LIMIT (star P2-5) - every save since 0.7.81 writes its limit whether or not the dial was touched, and automat... |
| 193 | `AutoBuilder.SLACK_MOST` | `.50` | The spare margin's slider runs from none - just enough - to this: half again what the city uses of every service (star N4-3). |
| 202 | `AutoBuilder.DEBT_LIMIT_MOST` | `6.0` | The debt limit's slider runs from none - with any debt it builds nothing - to six years of GDP (0.7.101, star P2-4): Jerus, "one should have the option for beyond 300%" - twice the old top, so a city past what any lar... |
| 205 | `AutoBuilder.STEP` | `.01` | The spare margin's step: a whole per cent (the debt limit's until 0.7.81). |
| 208 | `AutoBuilder.DEBT_STEP` | `.10` | The debt limit's step (0.7.101, star P2-4): ten per cent of a year's GDP - sixty steps to DEBT_LIMIT_MOST, as N6-4's five per cents were to three years; DEFAULT_DEBT_LIMIT and OLD_DEFAULT_DEBT_LIMIT are both on them. |
| 211 | `AutoBuilder.LOG_MOST` | `40` | The most of its orders the log keeps, newest kept (BuildLog's cap). |
| 217 | `AutoBuilder.ORDER_MONTHS` | `12` | No more than opens inside this many months: the businesses' own rule for an order until BA1 (BusinessInvestment.MAX_ORDER_MONTHS, which went with the cap on orders), kept for the city's automatic orders, which have no... |
| 422 | `AutoBuilder.HELD_BACK` | `"Automatic building holds it back; "` | What the advice card's line opens with (PT1-M3); the inbox's words for the payback follow. |
| 708 | `AutoBuilder.PAID_MONTHS` | `12` | Months of monthPaid() the payback's figure averages: a year, as a lender reads a city's accounts - a year of its own recurring revenue, the sale of its property left out, as a city's debt limit is set (Ontario's annua... |
| 1417 | `AutoBuilder.GRID` | `BuildAdvice.Measure.of(BuildAdvice.Kind.GRID)` | The grid's measure: the wires the plants' power runs on (PW9-G). |
| 2057 | `AutoBuilder.LINKS_LABEL` | `"Island links"` | The measure label an island link is entered under in the log. |
| 2160 | `AutoBuilder.Ground.NONE` | `new Ground(List.of(), 0, true)` | No ground to buy: the order fits what is free. |
| 2185 | `AutoBuilder.ASSEMBLE_MOST` | `4 * LandMarket.OFFERS` | The most offers one order assembles its ground from (PW3, Jerus 2026-11-23: "you just buy more land"): four listings' worth (LandMarket.OFFERS), past the 18 a 325 MW coal plant's 200 acres took on a founded city's gro... |
| 2842 | `AutoBuilder.PROCEEDS_WAIT_MONTHS` | `6` | The most months an ask whose bond has raised its order's cost waits, open, for a treasury that cannot pay it - the month's bills spent the proceeds and the central bank advances no more - before it is withdrawn: six, ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 219 | `private boolean on` |  |
| 220 | `private double slack` |  |
| 221 | `private double debtLimit` |  |
| 223 | `private boolean cashAnyway` | "Build from cash anyway" (0.7.81): over the debt limit, build what the cash alone pays for, never borrowing. |
| 295 | `public int month` |  |
| 296 | `public String measure` |  |
| 297 | `public String building` |  |
| 298 | `public long count` |  |
| 300 | `public long wanted` | What the advice's card asked for; more than count when the builders, the ground, the payback (the budget until 0.7.104) or the limit cut it. |
| 301 | `public double cost` |  |
| 303 | `public double borrowed` | What the funding page's bond brought for it; 0 out of the cash. |
| 304 | `public boolean paving` |  |
| 305 | `public String why` |  |
| 307 | `public double landSqFt` | The ground it bought for the order (0.7.77): its dry square feet, its price in local money at the day's rate, and the offers' names ("West 21, North 1", LandParcel.where()); 0 and null for none. |
| 308 | `public double landCost` |  |
| 309 | `public String landWhere` |  |
| 349 | `private final List<Entry> log` |  |
| 352 | `private final List<String> held` | What held a service back at the last pass: one line a measure, for the inbox and the card. |
| 355 | `private int lastPass` | The month of the last pass, 0 for none. |
| 357 | `private int orders` |  |
| 358 | `private long buildings` |  |
| 359 | `private double spent, borrowed` |  |
| 360 | `private int bonds` |  |
| 362 | `private double landSqFt, landSpent` | The ground it has bought for its orders (0.7.77): dry square feet, its price in local money, and how many offers. |
| 363 | `private int landOffers` |  |
| 365 | `private int assembledOffers` | Offers bought past the listing an order's ground was planned from (PW3, ASSEMBLE_MOST): PowerPlanCheck's count, not saved. |
| 368 | `private final List<String> bought` | The ground the last pass bought, a line a purchase, for the inbox (not saved: the pass that writes it runs before the month's notices are taken, loaded or not). |
| 392 | `public String measure` |  |
| 393 | `public int templateId` |  |
| 394 | `public double runs, covered, paid, costs, prevented` |  |
| 413 | `private final List<PaybackHold> paybackHolds` | The last pass's payback holds (PT1-M3), in the order it weighed them. |
| 416 | `private final Map<String, String> fundingHolds` | Last-pass funding reasons shown on cards, saved and cleared with automation's switch. |
| 419 | `private final Map<String, String> crewHolds` | Reserve plans limited by an unserved essential work's saved construction promise. |
| 711 | `private final List<Double> paidMonths` | monthPaid() at each of the last PAID_MONTHS passes, oldest first: saved (0.7.104; the budget's revenue under "recent" until then, which nothing reads now). |
| 907 | `private final List<Step> steps` |  |
| 909 | `private final List<String> powerValues` | The last pass's actual power orders, with the comparison's components before any ground or cash changed (HW1). |
| 921 | `private final List<LinkStep> linkSteps` | The island links the last pass ordered or held, one at most a pass (not saved: a harness reads it the month it is made). |
| 924 | `private boolean groundShort` | Whether the last pass met an order short of ground it would have bought (the measure it reads before it buys ground): the island links' cue (AL1). |
| 1099 | `private final Map<BuildingsTemplate, Double> runningNow` | running() of each building this month, read once (costs() reads every one of the measure's own and every one on site, each weighing): not saved. |
| 1101 | `private final Map<BuildingsTemplate, Double> standingNow` | ...of each standing one, at the figures it stands with (QN1; running() reads one new building, NewUnit): not saved. |
| 1102 | `private Game runningGame` |  |
| 1103 | `private int runningMonth` |  |
| 1235 | `private final Map<String, Double> pendingUntil` | Fixed first-completion dates for pending service, saved across reloads (HW1). |
| 1420 | `private BuildAdvice.Ahead wiresAt` | While the wires a plant takes with it are stepped (PW9-G): the plant's date and demand, which the grid's suggestion is sized at; null otherwise. |
| 1422 | `private boolean wiresStepped` | Whether this power step has stepped its wires already: once a step, whichever plant in the ranking asked (never saved). |
| 1424 | `static boolean wiresOffForTest` | A harness's door (PowerWiresCheck's before and after): true, no plant waits for its wires and none are ordered with it, as on 0.9.218. |
| 2345 | `private boolean overSaid` | Whether this pass has said why over the limit it builds nothing: the first such line says it whole, the rest in short (0.7.81). |
| 2520 | `private Auction.Cap cap` | The highest yield the builder's bonds take (D28): fair plus a point by default. |
| 2543 | `public String measure` |  |
| 2544 | `public String building` |  |
| 2545 | `public int templateId` |  |
| 2546 | `public boolean paving` |  |
| 2547 | `public long count` |  |
| 2548 | `public double cost` |  |
| 2549 | `public double raised` |  |
| 2550 | `public double toRaise` |  |
| 2551 | `public int month` |  |
| 2572 | `private final List<OpenAsk> asks` | The open asks, oldest first; at most one a measure. |
| 2575 | `private final List<String> asked` | What the asks did at the last pass, a line each time one opened, was placed or was withdrawn: for the inbox (not saved, as bought is). |
| 2906 | `public boolean on` |  |
| 2907 | `public double slack` |  |
| 2909 | `public Double debtToGdp` | The debt limit over a year of GDP (0.7.81): a save from before has none - its "debtLimit" was a share of revenue, which nothing reads now - and loads DEFAULT_DEBT_LIMIT; one at OLD_DEFAULT_DEBT_LIMIT, the default unti... |
| 2911 | `public boolean cashAnyway` | "Build from cash anyway" (0.7.81): off in a save from before. |
| 2912 | `public List<Entry> log` |  |
| 2913 | `public List<String> held` |  |
| 2914 | `public int lastPass` |  |
| 2915 | `public int orders` |  |
| 2916 | `public long buildings` |  |
| 2917 | `public double spent` |  |
| 2918 | `public double borrowed` |  |
| 2919 | `public int bonds` |  |
| 2921 | `public List<Double> paid` | monthPaid() at the last PAID_MONTHS passes (0.7.104): a save from before has none - its "recent", the budget's revenue, is not read - and reads the month's until a pass records one. |
| 2923 | `public double landSqFt` | The ground bought for its orders (0.7.77): a save from before has none. |
| 2924 | `public double landSpent` |  |
| 2925 | `public int landOffers` |  |
| 2927 | `public CapSetting.Saved cap` | The cap its bonds take (CD2, D28), the mode by name and the tail or yield: a save without one reads the default, fair plus a point. |
| 2929 | `public List<OpenAsk> asks` | The asks open when it was saved (CD2, D30): each its order, what it raised, what is to raise and the month it opened. |
| 2931 | `public List<PaybackHold> paybackHolds` | What the last pass held back as not paying back (PT1-M3), for the advice's card: a save from before has none, and its cards say nothing until the next pass. |
| 2933 | `public Map<String, Double> pendingUntil` | Original first-completion dates for pending capacity, by service label (HW1). |
| 2935 | `public Map<String, String> fundingHolds` | Last-pass essential funding reasons for the advice cards. |
| 2937 | `public Map<String, String> crewHolds` | Reserve plans limited by an essential construction promise. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 168 | 2901 | **type** `public final class AutoBuilder` | Automatic building (0.7.73): the city's own works ordered for the player once a month - power, water, the roads and their lines, care, schools and safety - each kept ahead of its demand with the player's spare margin ... |

### THE SETTINGS (lines 170-288)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 1 | `public boolean isOn()` |  |
| 226 | 1 | `public double getSlack()` |  |
| 228 | 1 | `public double getDebtLimit()` | The debt limit: all the city owes over a year of its output (0.7.101, THE DEBT LIMIT; its bonds and bills over a year of GDP from 0.7.81, debt payments over revenue until then). |
| 229 | 1 | `public boolean isCashAnyway()` |  |
| 232 | 3 | `static double onSteps(double v, double most)` | A slider's value on its own steps and inside its ends. |
| 237 | 5 | `static double onSteps(double v, double most, double step)` | ...on steps of the caller's. |
| 244 | 18 | `public void setOn(boolean on, DecisionLog decisions)` | On or off; the change is a decision (DecisionLog.CONSTRUCTION). |
| 264 | 7 | `public void setSlack(double slack, DecisionLog decisions)` | The spare margin, on its slider's steps. |
| 273 | 7 | `public void setDebtLimit(double limit, DecisionLog decisions)` | The debt limit, on its slider's steps. |
| 282 | 6 | `public void setCashAnyway(boolean anyway, DecisionLog decisions)` | "Build from cash anyway", on or off; the change is a decision. |

### WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK (lines 289-464)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 54 | **type** `public static final class Entry implements Cloneable` | One order it placed, and why. |
| 311 | 1 | `public Entry()` _(in AutoBuilder.Entry)_ |  |
| 314 | 7 | `Entry copy()` _(in AutoBuilder.Entry)_ | A copy that shares nothing with this entry: toState()'s, for the autosave (UP1). |
| 322 | 12 | `Entry(int month, String measure, String building, long count, long wanted, double cost, double borrowed, boolean paving, String...` _(in AutoBuilder.Entry)_ |  |
| 335 | 1 | `public int month()` _(in AutoBuilder.Entry)_ |  |
| 336 | 1 | `public String measure()` _(in AutoBuilder.Entry)_ |  |
| 337 | 1 | `public String building()` _(in AutoBuilder.Entry)_ |  |
| 338 | 1 | `public long count()` _(in AutoBuilder.Entry)_ |  |
| 339 | 1 | `public long wanted()` _(in AutoBuilder.Entry)_ |  |
| 340 | 1 | `public double cost()` _(in AutoBuilder.Entry)_ |  |
| 341 | 1 | `public double borrowed()` _(in AutoBuilder.Entry)_ |  |
| 342 | 1 | `public boolean paving()` _(in AutoBuilder.Entry)_ |  |
| 343 | 1 | `public String why()` _(in AutoBuilder.Entry)_ |  |
| 344 | 1 | `public double landSqFt()` _(in AutoBuilder.Entry)_ |  |
| 345 | 1 | `public double landCost()` _(in AutoBuilder.Entry)_ |  |
| 346 | 1 | `public String landWhere()` _(in AutoBuilder.Entry)_ |  |
| 371 | 1 | `public List<Entry> log()` | Its orders, oldest first. |
| 374 | 5 | `public List<Entry> latest(int n)` | ...newest first, at most n. |
| 381 | 1 | `public List<String> held()` | What held it back at the last pass, a line a measure; empty when nothing did. |
| 391 | 20 | **type** `public static final class PaybackHold` | A building the last pass held back as not paying back (PT1-M3): its measure by label (as an ask names it), the building by template id, and the payback weighed for one, whose figures the inbox's line gives (paybackWor... |
| 396 | 1 | `public PaybackHold()` _(in AutoBuilder.PaybackHold)_ |  |
| 398 | 9 | `PaybackHold(String measure, int templateId, Payback p)` _(in AutoBuilder.PaybackHold)_ |  |
| 409 | 1 | `public Payback payback()` _(in AutoBuilder.PaybackHold)_ | The payback it weighed. |
| 429 | 7 | `public Payback heldForPayback(BuildAdvice.Suggestion s)` | The payback the last pass weighed for one of a card's building where it held it back as not paying back (PT1-M3): null where it did not - off, a paving, or not so for the card's measure and building. |
| 438 | 11 | `public String heldBackWords(BuildAdvice.Suggestion s)` | ...as the advice's card says it: HELD_BACK and the inbox's words (paybackWords()); null where it did not hold it back. |
| 450 | 1 | `public int getLastPass()` |  |
| 451 | 1 | `public int getOrders()` |  |
| 452 | 1 | `public long getBuildings()` |  |
| 453 | 1 | `public double getSpent()` |  |
| 454 | 1 | `public double getBorrowed()` |  |
| 455 | 1 | `public int getBonds()` |  |
| 456 | 1 | `public double getLandSqFt()` |  |
| 457 | 1 | `public double getLandSpent()` |  |
| 458 | 1 | `public int getLandOffers()` |  |
| 460 | 1 | `public int getAssembledOffers()` | Offers bought past the listing an order's ground was planned from since the city was opened (PW3): not saved. |
| 463 | 1 | `public List<String> bought()` | The ground the last pass bought for its orders, a line a purchase saying what and why; empty when it bought none. |

### THE MEASURES (lines 465-615)

| line | len | member | says |
|---:|---:|---|---|
| 475 | 6 | `public static boolean inRemit(BuildingsTemplate t)` | A remit building: one of the city's five Build categories - utilities, infrastructure, healthcare, education, safety. |
| 483 | 6 | `static CityNeeds.Need rowFor(List<CityNeeds.Need> all, BuildAdvice.Measure m)` | NEEDS YOU's row for exactly this measure (the dead, not the plots, for death care), or null for none. |
| 503 | 7 | `static boolean kept(Game game, BuildAdvice.Measure m, List<CityNeeds.Need> all)` | Whether a measure is one the pass keeps this month: transit never (the road's candidates carry it), and a school only when its demand warrants a work (HW1: thirty percent of the smallest, including professional school... |
| 512 | 11 | `static long serving(Game game, BuildAdvice.Measure m)` | Buildings that serve a measure, standing and on site. |
| 530 | 5 | `public static boolean firstOfAKind(Game game, BuildAdvice.Measure m)` | Whether an order for a measure is a FIRST OF A KIND WHERE THERE IS NONE (0.7.101): a school down the ladder, a police station or a prison - the buildings that waited for half their worth until then - with none standin... |
| 545 | 3 | `public static boolean orderable(BuildingsTemplate t)` | A building the pass may order (FR3): a remit building (inRemit()) or a sea terminal, a berth of a port measure. |
| 550 | 23 | `public static List<BuildAdvice.Measure> remit(Game game, List<CityNeeds.Need> all)` | Current essential shortages by preventable output loss and missing share, then reserve expansion and other works. |
| 574 | 1 | **type** `private record Priority(int order, double loss, double missing)` |  |
| 577 | 7 | `private static Priority priority(Game game, BuildAdvice.Measure m)` | Operating shortages precede reserve expansion, with the model's preventable loss first inside that group. |
| 586 | 21 | `private static double firstCost(Game game, BuildAdvice.Measure m)` | The smallest available useful step, including the actual ground offers it would buy. |
| 609 | 1 | `public BuildAdvice.Ahead target()` | The spare margin's demand: today's, padded by the slack, no projection. |
| 612 | 3 | `public boolean atTarget(Game game, BuildAdvice.Measure m)` | Whether what stands supplies the whole warranted demand with the spare margin past it (HW1). |

### THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6) (lines 616-851)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 4 | `public static double annualGdp(Game game)` | A year of the city's output (0.7.101): the last twelve months' as recorded (NationalAccounts.getAnnualGdp()) - a city younger than a year at what it has produced; 0 with none recorded. |
| 665 | 3 | `static double overdraft(double cash)` | What the treasury is overdrawn, 0 when it is not. |
| 670 | 4 | `public static double debt(Game game)` | The city's debt (0.7.101, star P2-9): everything it owes - its bonds and bills, what it is overdrawn, and its central bank's advances (DebtManager.getPricedDebt()'s three, read live); its bonds and bills alone until t... |
| 676 | 4 | `public static double debtAfter(Game game, DebtQuote q)` | ...once a bond is on the books: its face added, and the overdraft its cash clears taken off. |
| 682 | 3 | `public static double ratio(Game game)` | The city's debt over a year of its GDP: 0 owing nothing, +∞ owing with no GDP recorded. |
| 687 | 3 | `public static double ratioAfter(Game game, DebtQuote q)` | ...once a bond is on the books. |
| 691 | 4 | `private static double over(double debt, double gdp)` |  |
| 697 | 3 | `public boolean within(Game game)` | Whether the city's debt is at or under the limit: it builds, and may borrow up to it; over it, it builds only from cash with cashAnyway on. |
| 714 | 4 | `void remember(Game game)` | What a pass records first: this month's monthPaid(), the oldest dropped past PAID_MONTHS. |
| 729 | 3 | `static DebtQuote bondFor(Game game, double gap)` | The funding page's bond for `gap` of cash at the builder's own cap (D28): its quote, or null for none to be had. |
| 734 | 5 | `static DebtQuote bondFor(Game game, double gap, Auction.Cap cap)` | ...at a cap of the caller's. |
| 741 | 4 | `static DebtQuote bondQuote(Game game, double gap, Auction.Cap cap)` | The bond for `gap` as the auction would sell it, short or not: null where there is no gap or the paper cannot be issued. |
| 756 | 3 | `public static double reserve(Game game)` | The cash it keeps (star N4-5): a month's tax, the line under which NEEDS YOU lists the treasury (CityNeeds.taxRaised()). |
| 767 | 3 | `public static double spendable(Game game)` | What an order may take of the cash: what is over the reserve; an overdraft counted in full, as Build's funding page counts it (Game.buildFundingGap()); and since D30 beside what open asks have raised, as far as the ca... |
| 772 | 4 | `private static double overReserve(Game game)` | The cash over the reserve, or the overdraft in full: what spendable() was before D30's earmarks. |
| 803 | 3 | `double spendableForMeasure(Game game, BuildAdvice.Measure m)` | What the treasury holds for ONE ask's order (D30, OA1): its own bond proceeds, as far as the cash still holds them, and beside them the cash over the reserve that no ask's proceeds take. |
| 807 | 10 | `double spendableFor(Game game, OpenAsk own)` |  |
| 819 | 3 | `public static double gapFor(Game game, double total)` | What a bond must bring for an order of `total`: the part the spendable cash does not cover. |
| 829 | 3 | `boolean pays(Game game, double total, boolean fromCash)` | Whether `total` is paid for an order (0.7.101): as canPay() says, and a FIRST OF A KIND WHERE THERE IS NONE (firstOfAKind()) out of the cash over the reserve alone, never on a bond (star P2-1): Jerus, "at first it doe... |
| 834 | 3 | `boolean canPay(Game game, double total)` | Whether `total` is paid for: at or under the limit, by the cash and past it a bond that keeps the debt within it; over it, by the cash alone with cashAnyway on, and otherwise not at all (0.7.81). |
| 838 | 13 | `private boolean canPay(Game game, double total, OpenAsk own)` |  |

### THE PASS (lines 852-1397)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 12 | **type** `public enum Outcome` | What the pass did for one measure. |
| 880 | 1 | **type** `public enum Cut` | Why a count was cut or an order held: PAYBACK (0.7.104) what pays back over its life, for a building the treasury runs that is not an essential service - BUDGET, what the year's revenue left to run it, until then; DEB... |
| 891 | 15 | **type** `public record Step(BuildAdvice.Measure measure, Outcome outcome, String building, long wanted, long ordered...` | One measure at the last pass: what it did, and why - and since 0.7.77 the ground its order lacked when it was weighed (landShort, square feet past what was free), the offers it bought for it and their price in local m... |
| 894 | 4 | `Step(BuildAdvice.Measure measure, Outcome outcome, String building, long wanted, long ordered, Cut cut, double cost, double bor...` _(in AutoBuilder.Step)_ |  |
| 900 | 5 | `public double landSqFt()` _(in AutoBuilder.Step)_ | The dry ground it bought, in square feet. |
| 910 | 1 | `public List<String> powerValues()` |  |
| 917 | 2 | **type** `public record LinkStep(int month, int landmass, BuildAdvice.LinkKind kind, Outcome outcome, String building...` | What the pass did to link an island (AL1): the month, the island's id, how it is linked (BuildAdvice.LinkKind), ORDERED or HELD, the orders' buildings as the log words them, their price (thousands) and the line - the ... |
| 1004 | 4 | `public static boolean staffedBuilding(BuildingsTemplate t)` | A building the treasury runs: care, death care, a school, the police, the cells - and a transit line, whose crews the treasury pays (0.7.49). |
| 1015 | 7 | `public static boolean essential(BuildAdvice.Measure m)` | An essential work (HW1): utilities, streets, highways, care of every kind, death care and plots, all schools, police and cells. |
| 1024 | 3 | `public static boolean paysBackFirst(BuildAdvice.Measure m, BuildingsTemplate t)` | Whether a staffed non-essential work must pay back; the essential floor takes precedence (HW1). |
| 1040 | 9 | `public static double monthPaid(Game game)` | What a resident pays the city a month (0.7.104, star P2b-3): the city's recurring revenue - its taxes on wages, sales, property, business and industry, its contributions and premiums, its fees and fares, what its util... |
| 1058 | 7 | `public double paid(Game game)` | What a resident pays the city a month, as the payback reads it (0.7.104): the lesser of this month's (monthPaid()) and its average over the last PAID_MONTHS passes, so a month that swells - a batch of mortgages insure... |
| 1078 | 19 | `public double costs(Game game, BuildAdvice.Measure m)` | What each resident already costs the city a month (0.7.104, star P2b-3 - A BUILDING THAT HELPS THE CITY GROW ONLY WITH OTHERS): its running spend - all it spends but buildings and land, and but the arrears it paid dow... |
| 1111 | 4 | `double running(Game game, BuildingsTemplate t)` | A month of running one (BuildAdvice.running(), held at nothing), read once a month: its wages, prices and fares are the month's, and what a pass orders goes on site, not into the network a line's fares are read on - s... |
| 1117 | 8 | `private void sameMonth(Game game)` | The month the running caches hold: emptied when the game or the month moves. |
| 1127 | 4 | `double standingRunning(Game game, BuildingsTemplate t)` | running() of one already standing (QN1): the figures it stands with, not a new one's (NewUnit.standing()); held with running()'s month. |
| 1133 | 3 | `static boolean ownMeasure(BuildAdvice.Measure m, BuildingsTemplate t)` | Whether t is one of the measure's own buildings (0.7.104): a road's or a line's, any of the Infrastructure category's roads and lines; otherwise those that serve it. |
| 1148 | 14 | `static double[] need(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> added, BuildAdvice.Ahead at)` | A measure's need and its shortfall, in its own unit, with these buildings standing as well, at `at`'s demand (0.7.104): {need, short}. |
| 1175 | 21 | `public static double coveredOverLife(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, Map<BuildingsTemplate...` | The residents an order covers, a month on average over its life (0.7.104, star P2b-2): each of its LIFE_YEARS years read at its middle month as the advice reads what an order serves (servedOverLife()) - from its openi... |
| 1204 | 9 | **type** `public record Payback(double runs, double covered, double paid, double costs, double prevented)` | An order's payback (0.7.104), a month on average over its life at today's prices: what it costs to run (runs: running() for each), the residents it covers (coveredOverLife()), what each pays the city (paid()) and what... |
| 1205 | 1 | `public Payback(double runs, double covered, double paid, double costs)` _(in AutoBuilder.Payback)_ |  |
| 1207 | 1 | `public double net()` _(in AutoBuilder.Payback)_ | What the residents it covers pay the city a month past what they already cost it. |
| 1209 | 1 | `public boolean paysBack()` _(in AutoBuilder.Payback)_ | Whether it pays back: net() at least runs. |
| 1211 | 1 | `public double share()` _(in AutoBuilder.Payback)_ | net() over runs, nothing under nothing: 1 or more pays back. |
| 1215 | 9 | `public Payback payback(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, long n)` | n of a suggestion's building, weighed for its payback at the demand its count is sized to (its ahead()). |
| 1226 | 1 | `public List<Step> steps()` | The last pass, a step a measure (not saved: a harness reads it the month it is made). |
| 1229 | 1 | `public List<LinkStep> linkSteps()` | The island links the last pass ordered or held (AL1), a LinkStep each; empty where nothing is short of ground or nothing is unlinked. |
| 1232 | 1 | `public boolean groundShort()` | Whether the last pass met an order short of ground (AL1): what sets the island links going. |
| 1238 | 36 | `public Map<BuildingsTemplate, Long> pendingCapacity(Game game, BuildAdvice.Measure m)` | Pending capacity stops covering an operating shortage after its original quote expires. |
| 1283 | 4 | `public double promisedMonths(Game game, BuildAdvice.Measure m)` | The months until a measure's pending first completion is due (HW1's saved promise), from this month; NaN with none. |
| 1288 | 15 | `private void rememberPending(Game game, BuildAdvice.Measure m, boolean newOrder)` |  |
| 1308 | 89 | `public void pass(Game game)` | The month's pass (see the header): nothing while it is off. |

### THE WIRES WITH THE POWER (PW9-G, 2026-11-28) (lines 1398-1913)

| line | len | member | says |
|---:|---:|---|---|
| 1427 | 4 | `static double servedAt(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> site, BuildAdvice.Ahead p)` | A measure's supply over its demand at p, with these buildings standing as well: infinite where nothing is asked. |
| 1433 | 25 | `private long wiresCarry(Game game, List<CityNeeds.Need> all, List<BuildAdvice.Measure> kept, BuildAdvice.Suggestion s, long n)` | How many of n of a power suggestion the wires will carry at its date, the wires short of its load there ordered first (THE WIRES WITH THE POWER). |
| 1465 | 5 | `private boolean wiresAllow(Game game, BuildAdvice.Suggestion s, Map<BuildingsTemplate, Long> at, BuildAdvice.Ahead load, long m)` | Whether the wires allow the m-th unit of a power suggestion (THE WIRES WITH THE POWER): with m units on the crews the wires that open by its date still carry all the load there, or with m - 1 the plants still serve le... |
| 1472 | 6 | `private double wiresWith(Game game, BuildAdvice.Suggestion s, long m, BuildAdvice.Ahead load)` | The wires' served share at `load` with m of a power suggestion on the crews: the units on site the builders open by then, each slowed by the order's points (WorksValue.openByAfter()). |
| 1480 | 5 | `private static double orderPoints(Game game, BuildAdvice.Suggestion s, long count)` | The construction points an order of `count` of a suggestion puts on the crews: its buildings at the points their equipment takes and its works (R1e). |
| 1486 | 1 | **type** `private record CrewLimit(long count, String service)` |  |
| 1488 | 3 | `private static String crewWords(BuildAdvice.Measure measure, String service)` |  |
| 1492 | 18 | `private CrewLimit crewLimit(Game game, List<BuildAdvice.Measure> kept, BuildAdvice.Measure measure, BuildAdvice.Suggestion sugg...` |  |
| 1511 | 29 | `private String pendingDelay(Game game, List<BuildAdvice.Measure> kept, BuildAdvice.Measure measure, BuildAdvice.Suggestion sugg...` |  |
| 1542 | 1 | **type** `private record Refused(BuildAdvice.Suggestion s, Cut cut, long wanted, String line, Payback payback)` | A card the money or the ground could not pay for one of (0.7.101), or none of which would pay back (0.7.104): the suggestion, why, how many it asked for, the inbox's line for it, and the payback weighed for one (null ... |
| 1559 | 170 | `private void step(Game game, List<CityNeeds.Need> all, List<BuildAdvice.Measure> kept, BuildAdvice.Measure m)` | One measure's order, cut and placed. |
| 1735 | 9 | `private static String groundWords(Game game, BuildingsTemplate t)` | Why no ground can be had for one (0.7.77; 0.7.101): no bare ground on offer to buy for it - or a city whose buildings already stand on more ground than it owns, where an order on ground of its own could not be placed ... |
| 1749 | 6 | `boolean placeForTest(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, long n)` | A harness's door to place() (PW3, REV-PW3 #2; PowerPlanCheck 7c): n of a suggestion the pass would take, its ground planned as the pass plans it, placed as the pass places it. |
| 1765 | 148 | `private boolean place(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, String name, long wanted, long n, Cut cut, Gr...` | Places n of a suggestion, its ground bought first (0.7.77), borrowing what the spendable cash does not cover, and writes it down; false where it could not (the order is then held, and an ask stays open). |

### THE ISLAND LINKS (AL1, 2026-11-09) (lines 1914-2115)

| line | len | member | says |
|---:|---:|---|---|
| 1935 | 120 | `void link(Game game)` | The island link the pass would order, placed (AL1); a hold or an order is a LinkStep and, held, a line in the inbox. |
| 2060 | 4 | `private void holdLink(Game game, BuildAdvice.Link l, String building, String line)` | A hold of an island link: one line in the inbox, and a LinkStep. |
| 2066 | 5 | `private static String orderName(BuildAdvice.Link l, BuildAdvice.LinkOrder o)` | One order of a link by name: "Road Bridge", or "Bulk Berth on Island 2" for a terminal on the island and "Bulk Berth on linked ground" for the one the rule places. |
| 2073 | 5 | `private static String linkWords(BuildAdvice.Link l)` | A link's orders as the log words them: "1 Road Bridge", "1 Bulk Berth on Island 2 and 1 Bulk Berth on linked ground". |
| 2080 | 6 | `private static String linkReason(Game game, Landmasses lms, BuildAdvice.Link l)` | Why this link and not another, for the log: the crossing a bridge is surveyed over, or the terminals that stand in for one. |
| 2088 | 3 | `private static String refusedName(BuildAdvice.Suggestion s)` | A refused card's building as its words name it: its own name, or the gravel roads a paving paves. |
| 2093 | 3 | `private static String money(double thousands)` | Thousands of local money as the decision log writes them ("$1.2M"). |
| 2104 | 11 | `private static String boughtWords(BuildAdvice.Measure m, String name, long n, List<LandParcel> got, double landCost, double lan...` | The inbox's line for ground bought (0.7.77): which offers, how much and at what price, the order it was for and what that order lacked - and which of the shortcut's rules picked it: one bare offer covering the shortfa... |

### THE GROUND ITS ORDERS NEED (0.7.77, batch N5) (lines 2116-2451)

| line | len | member | says |
|---:|---:|---|---|
| 2158 | 14 | **type** `public record Ground(List<LandParcel> offers, double cash, boolean covers, double more)` | The ground an order lacks, as the offers it would buy for it in turn, the cash that takes, and whether one offer covers it all; `more` the dry ground past those offers it buys offer by offer as each purchase lists the... |
| 2163 | 1 | `public Ground(List<LandParcel> offers, double cash, boolean covers)` _(in AutoBuilder.Ground)_ | Ground planned from the listing alone, nothing past it. |
| 2166 | 5 | `public double sqFt()` _(in AutoBuilder.Ground)_ | Their dry ground, in square feet. |
| 2174 | 6 | `public static Ground groundFor(Game game, BuildingsTemplate t, long n)` | The ground n of a building lack past what is free, as groundFor() plans it: NONE when it fits, null when no bare ground on offer covers it - or (0.7.101) when the city's buildings already stand on more ground than it ... |
| 2194 | 21 | `static List<LandParcel> assembled(Game game, double lack, List<LandParcel> listed)` | The offers an order buys for `lack` square feet of ground, in the order it buys them (PW3, REV-PW3 #1, #2): the planned listing's offers first, then - while what is held does not cover the lack - the offer shortcutOff... |
| 2223 | 32 | `static Ground groundFor(Game game, double lack, Set<Integer> alreadyTaken)` | The same whole-parcel selection for a projected shortage, omitting already bought offers. |
| 2257 | 3 | `private static double linkedSqFt(LandParcel o)` | An offer's dry ground that is room for the order, square feet: its whole dry ground (getSizeSqFt()) less what of it is on a landmass the city has not linked (ET5; LandParcel.getUnlinkedDryKm2()); the whole of it for a... |
| 2274 | 4 | `public static double lacks(Game game, BuildingsTemplate t, long n)` | The ground n of a building lack, in square feet: what they need past what is free (0.7.101) - only the order's own ground. |
| 2280 | 5 | `static double freeGround(Game game, BuildingsTemplate t)` | The ground the city may build one of these on without buying (RG1): free for anyone, and for a Wind or Solar Farm what the farmland reserve holds too while one stands (LandManager.reserveKeyOf()). |
| 2287 | 4 | `public static double overFull(Game game)` | How much more ground the city's buildings stand on than it owns, in square feet (0.7.101): 0 for a city within its ground. |
| 2300 | 3 | `public static LandParcel shortcutOffer(Game game, double lackSqFt, Set<Integer> taken)` | The offer Build's land shortcut would buy for `lackSqFt` of dry ground, the offers in `taken` passed over, bare ground only: the cheapest whose dry ground covers it, the nearer on a tie (Game.bestOffer()'s shortfall r... |
| 2305 | 20 | `static LandParcel shortcutOffer(LandMarket market, double lackSqFt, Set<Integer> taken)` | ...on any office: the city's own, or a rehearsal's copy of it (assembled()). |
| 2327 | 4 | `static double wait(Game game, BuildAdvice.Suggestion s, long n)` | An order's wait for n: a paving's on the Paved Road site, a building's in the queue (a sea terminal's quoted on the landmass the rule gives it, as buildStack() is asked, ET5). |
| 2333 | 4 | `static double total(Game game, BuildAdvice.Suggestion s, long n)` | What n of the order costs: Game.quoteBuildOn() by the rule (ET5; Game.quoteBuild() for everything but a sea terminal), or Game.quotePave() for a paving. |
| 2338 | 5 | `private void hold(BuildAdvice.Measure m, BuildAdvice.Suggestion s, Cut cut, long wanted, String line, Payback pb)` |  |
| 2348 | 5 | `public static String gdpShare(double share)` | A share of GDP as the card writes it: "262%", "7%", "0%", and a place under a tenth: "0.4%", "7.2%". |
| 2355 | 3 | `private String debtWords(Game game, BuildAdvice.Suggestion s, Ground ground)` | Why the money pays for none: one more and (0.7.77) the ground it lacks - over the limit, or a bond that would cross it (0.7.81). |
| 2360 | 25 | `private String debtWords(Game game, java.util.function.DoubleSupplier price, String what, Ground ground)` | ...for an order that is not a suggestion's (AL1: an island's link): its price, asked only where the words need it, and what it is called ("one", "the link"). |
| 2387 | 6 | `private String firstWords(Game game, BuildAdvice.Suggestion s, Ground ground)` | Why the cash pays for no first of a kind (0.7.101): built from the cash alone, and the cash over a month's tax short of one and its ground. |
| 2403 | 9 | `static String paybackWords(Payback p)` | Why one would not pay back (0.7.104), with its figure: what each of the residents it would cover pays the city a month and what each already costs it, on average over its life, and what that leaves against its running... |
| 2414 | 10 | `static String nothingWords(Game game, BuildAdvice.Measure m)` | Why nothing the city could build moves a measure short of its margin: water past the fresh water it owns with no sea, or no building it could staff. |
| 2425 | 12 | `static String cutWords(Cut cut, long wanted, long n)` |  |
| 2439 | 12 | `public static String reading(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Long> added)` | A measure's figure as the Build tab reads it: served for a served gauge, its own reading for the rest. |

### THE CAP AND THE OPEN ASKS (CD2, slice 6; D28 and D30) (lines 2452-2899)

| line | len | member | says |
|---:|---:|---|---|
| 2523 | 1 | `public Auction.Cap getCap()` | The builder's own cap. |
| 2526 | 7 | `public void setCap(Auction.Cap cap, DecisionLog decisions)` | The cap, as the panel's chips set it; the change is a decision. |
| 2542 | 28 | **type** `public static final class OpenAsk` | One order whose bond sold short and which waits for its money (D30): the measure by its label, the building by its name and template id (-1 and paving for the gravel roads' paving), how many, what the order costs with... |
| 2553 | 1 | `public OpenAsk()` _(in AutoBuilder.OpenAsk)_ |  |
| 2555 | 1 | `public String measure()` _(in AutoBuilder.OpenAsk)_ |  |
| 2556 | 1 | `public String building()` _(in AutoBuilder.OpenAsk)_ |  |
| 2557 | 1 | `public boolean paving()` _(in AutoBuilder.OpenAsk)_ |  |
| 2558 | 1 | `public long count()` _(in AutoBuilder.OpenAsk)_ |  |
| 2560 | 1 | `public double cost()` _(in AutoBuilder.OpenAsk)_ | What the order costs, its ground with it, as last priced. |
| 2562 | 1 | `public double raised()` _(in AutoBuilder.OpenAsk)_ | What its bond sales have raised for it. |
| 2564 | 1 | `public double toRaise()` _(in AutoBuilder.OpenAsk)_ | What is still to raise. |
| 2566 | 1 | `public int monthOpened()` _(in AutoBuilder.OpenAsk)_ | The month it opened in. |
| 2568 | 1 | `public int monthsOpen(int now)` _(in AutoBuilder.OpenAsk)_ | How many months it has been open at `now`. |
| 2578 | 1 | `public List<OpenAsk> asks()` | The open asks, for the panel: the order, what is raised, what is to raise, the months open. |
| 2581 | 1 | `public List<String> asked()` | What the last pass did with the asks, a line an event; empty when nothing happened. |
| 2584 | 4 | `OpenAsk askFor(BuildAdvice.Measure m)` | The ask open for a measure, or null. |
| 2590 | 4 | `private boolean askOpenFor(String label)` | Whether an ask is open for the measure of this label. |
| 2604 | 3 | `double held(Game game, OpenAsk own)` | What the treasury holds for one ask's order right now: its own proceeds as far as the cash still holds them, the reserve not taken out of them, and the cash over the reserve beside every ask's proceeds (spendableFor()... |
| 2609 | 5 | `public double askedRaised()` | Everything open asks have raised: the money the builder holds for their orders. |
| 2620 | 5 | `public boolean withdraw(int index, DecisionLog decisions)` | The player withdraws an ask (the panel's button): what was raised stays in the treasury, and the log says so. |
| 2627 | 3 | `public boolean withdraw(int index)` | ...with no decision recorded. |
| 2632 | 10 | `private void withdrawAsk(OpenAsk a, String why, DecisionLog decisions)` | Takes an ask off the books and says why; what it raised is the treasury's. |
| 2644 | 1 | **type** `private record Askable(BuildAdvice.Suggestion s, String name, double cost, double gap, DebtQuote quote)` | A card the money refused because its bond came up short at the builder's cap: the card, its words, what one costs with its ground, the gap past the spendable cash, and the short quote. |
| 2647 | 9 | `private Askable askableFor(Game game, BuildAdvice.Suggestion s, String name, Ground one)` | The ask for one of a card, if its bond for the gap comes up short at the cap and the sale itself would stay within the limit; null where something else refused it. |
| 2658 | 1 | **type** `private record Sold(DebtQuote quote, double raised)` | What an offer of the bond raised, and the quote it was. |
| 2661 | 13 | `private Sold sell(Game game, OpenAsk ask, double need)` | Offers `need` of cash on the bond at the builder's cap and books what sells, for an ask: the money raised is the ask's. |
| 2676 | 8 | `private void noteSale(Game game, DebtQuote q, double raisedCash, double heldOpen)` | A bond the builder sold, entered for the time skip's report (TimeSkipReport.AutoSale). |
| 2686 | 17 | `private String askWords(OpenAsk a, DebtQuote q, double held)` | Where an ask stands, for the inbox and the card: its bond, what is raised and what is to raise. |
| 2709 | 20 | `private boolean openAsk(Game game, BuildAdvice.Measure m, Askable a)` | Opens the ask for a card (D30): sells the bond for what it will sell, keeps the money for the order, and holds the measure on it. |
| 2731 | 12 | `private BuildAdvice.Suggestion cardFor(Game game, List<CityNeeds.Need> all, BuildAdvice.Measure m, OpenAsk a)` | The advice's card for an ask's building, walking the ranking as the pass walks it; null where the advice no longer ranks it. |
| 2753 | 81 | `private void reoffer(Game game, List<CityNeeds.Need> all, List<BuildAdvice.Measure> kept, OpenAsk a)` | An open ask, in the month's need-ranked pass (D30): still the advice's order? |
| 2851 | 3 | `private boolean covered(Game game, OpenAsk a)` | Whether an ask's order is paid for (OA1): its bond's proceeds, or what the treasury holds for it beside them where that is more (funded()), at least the order's cost as priced now, and the treasury able to pay it toda... |
| 2861 | 3 | `private double funded(Game game, OpenAsk a)` | What an ask's order is paid from (OA1): its bond's proceeds, which are the works's whether or not the month's bills have drawn the cash under them, or what the treasury holds for it (held(): the proceeds still in the ... |
| 2871 | 3 | `private static boolean payable(Game game, double total)` | Whether the treasury can pay `total` today: its cash and, past it, what the central bank still advances (Game.discretionaryRoom()). |
| 2892 | 3 | `private double stillToRaise(Game game, OpenAsk a)` | What an ask has still to raise: its order's cost less its proceeds, or less what the treasury holds for it where that is more (funded()). |
| 2896 | 3 | `private double stillToRaise(Game game, OpenAsk a, double cost)` |  |

### THE SAVE (lines 2900-3068)

| line | len | member | says |
|---:|---:|---|---|
| 2905 | 34 | **type** `public static final class State` | What is saved, under one key (DataSave.autoBuild). |
| 2940 | 28 | `public State toState()` |  |
| 2969 | 13 | `private static OpenAsk copyOf(OpenAsk a)` |  |
| 2984 | 13 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM): the open asks', and (PT1-M3) the payback holds'. |
| 2999 | 69 | `public void restore(State s)` | Puts a saved state back; null - a save from before 0.7.73 - is off, at the defaults, with nothing done; one from 0.7.73 to 0.7.80 keeps its switch and margin and reads the default limit, "Build from cash anyway" off; ... |


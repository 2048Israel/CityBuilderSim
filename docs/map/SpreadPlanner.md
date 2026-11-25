# SpreadPlanner.java - 858 lines · 72 methods · 10 constants · sectors

`ham/citybuildersim/sectors/SpreadPlanner.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The spread planner (0.7.82, batch O5; docs/studies/spec-oil.md 2.4, and
> docs/studies/spec-materials.md 2.6): what a processing sector orders - of the
> buildings that pass its gates, the one that earns most on its cost at the
> city's own prices - and which of its kinds it may sell back once they
> stand idle.
> 
> WHY. Until 0.7.82 the refinery's investors ordered one building, the Oil
> Refinery, on K's gate - a whole plant's worth of the city's own petrol and
> diesel - and the maker's estimate over its slate (Refining.plan() as it
> was). Nothing ordered the conversion units, and a refinery on imported
> crude was judged on the crude unit alone, which fails on the wholesale
> ladder in any city (spec-oil 2.4's measurement). A real refiner builds a
> unit where its spread - what it makes of a litre less what the litre is
> worth without it - pays for the unit, and a crude unit with the units its
> cuts would feed. The materials chains that follow oil (Iron & Steel,
> Copper Refining, Wood Products, Cement & Concrete) build their works by
> the same rule, so the rule is here, shared, and not inside Refining.
> 
> THE PIECES, each one a client's to use:
>   1. THE CITY'S OWN PRICE (cityValue()): what one more unit of a good is
>      worth here - the landed import price while the city imports it, the
>      export price it nets otherwise (at the margin since 0.7.105) - and
>      since 0.7.112 (batch I2; spec-materials 2.6) what `u` units are
>      worth, split at the month's trade (outValue(), inValue()): made
>      here, the import price for as much as the city imports and the
>      export price past it; used here, the mirror.
>   2. A CANDIDATE (Candidate): a building, what one would earn a month once
>      running - its spread on its feed at the rate the sector runs, less
>      its wages, power and water and its repairs and property tax - and
>      what it costs to order, its ground with it. The client works out the
>      earnings: Refining from its flow (a unit's spread, or a crude unit's
>      package), a works from its makes and uses.
>   3. THE GATES, in order (judge()): its feed (the client's own question:
>      is there spare stream for 60% of one?), the ground, the staff, and the
>      money - every gate asked, so a refusal names the first and the land
>      office hears when ground was all that stood in the way.
>   4. THE MONEY GATE IS ONE REPLACEABLE PIECE (MoneyGate). In force since
>      0.7.112 (batch I2; Jerus's decision A, 2026-10-08: "test every order
>      on its whole cost, whoever pays"): 1.25 times the interest at the
>      real rate on the order's whole cost - its ground at its zone's price
>      with it - whatever the sector holds (ON_ITS_WHOLE_COST). To 0.7.111
>      the rule was the game's own test on what the order would borrow
>      (ON_ITS_BORROWING), which asked nothing of an order the sector's
>      cash paid; it stays, named, for the counterfactual. Since 0.7.113
>      (batch I2b) Game.consider() asks every order the same, through the
>      same piece (Game.orderGate()).
>   5. THE ORDER (best()): the best earnings on cost of those that pass. No
>      order in flight is waited for (BA1, 2026-11-02; the client held to one
>      before): what the client already has on site is counted in what it
>      weighs next (after()), and the price of a point and the interest on the
>      wait (MoneyGate, carryOf()) ration the orders.
>   6. IDLE, THEN SHED (noteMonth(), idleMonths(), mayShed()): the months
>      each kind has stood with nothing to do, by name, saved in the
>      client's extras ("idleMonths.<KIND>"); a kind idle IDLE_MONTHS
>      running is the one its sector may sell back.
>   7. OVER ITS YEAR (CY1, 2026-11-24; noteMargin(), marginOverYear(),
>      lesser()): each kind's appraisals of the last TRAILING_MONTHS, saved
>      in the client's extras. A client tests an order on the lesser of the
>      month's earnings and the year's mean, and counts a kind idle while
>      the last one standing earned below nothing in the month or over its
> ... (10 more lines in the source)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (25), [Good](Good.md) (14), [NewUnit](NewUnit.md) (12), [BusinessInvestment](BusinessInvestment.md) (9), [Sector](Sector.md) (7), [GoodsMarket](GoodsMarket.md) (6), [Game](Game.md) (6), [Currency](Currency.md) (4), [Refining](Refining.md) (2), [Freight](Freight.md) (1), [Markets](Markets.md) (1), [OldMetal](OldMetal.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1)

**Used by (45):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [ExtractionSector](ExtractionSector.md), [FittingCheck](FittingCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [HaulageCheck](HaulageCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [Manufacturing](Manufacturing.md), [MaterialsCheck](MaterialsCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PortCheck](PortCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RecipeChanges](RecipeChanges.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ResearchCheck](ResearchCheck.md), [SaveFileCheck](SaveFileCheck.md), [SulphideLeachCheck](SulphideLeachCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 104 | · 1. the city's own price (spec-oil 2.4, cityValue) |
| 295 | · the study's four cases (materials-chains-research.md 2.5) |
| 349 | · 2. and 3. a candidate, and the gates |
| 451 | · 5. the order |
| 483 | · 4. the money gate, one replaceable piece |
| 627 | · 6. idle, then shed |
| 723 | · 7. over its year (CY1) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 300 | `SpreadPlanner.Case.A` | Feed imported, output exported: what freight has to hold under the hurdle (the study's 1.2). |
| 302 | `SpreadPlanner.Case.B` | Feed local, valued at its export price; output exported. |
| 304 | `SpreadPlanner.Case.C` | Feed local; output replacing imports. |
| 306 | `SpreadPlanner.Case.D` | Both at import prices. |
| 354 | `SpreadPlanner.Gate.FEED` | Spare stream for FEED_GATE of one: a unit's feed, or for a crude unit the city's imports or the wells' spare. |
| 356 | `SpreadPlanner.Gate.LAND` | Ground for one (BusinessInvestment.plotsAvailableFor()). |
| 358 | `SpreadPlanner.Gate.STAFF` | Staff for one (Sector.staffing()). |
| 360 | `SpreadPlanner.Gate.MONEY` | It earns something, and enough for the money gate (MoneyGate). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `SpreadPlanner.FEED_GATE` | `Freight.MIN_LINE_UTILISATION` | The least share of one building's feed the spare stream must cover for it to be weighed at all: the railway's MIN_LINE_UTILISATION, nothing built to stand idle (spec-oil 2.4). |
| 99 | `SpreadPlanner.IDLE_MONTHS` | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | Months a kind must stand idle running before its sector may sell it back (spec-oil 2.4): six, RETIREMENT_LOSS_MONTHS - as long as the spare-capacity rule waits on losses. |
| 102 | `SpreadPlanner.IDLE_KEY` | `"idleMonths."` | The prefix of each kind's idle months in a client's saved extras. |
| 502 | `SpreadPlanner.ON_ITS_BORROWING` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash)...` | THE RULE TO 0.7.111, KEPT AS A NAMED HISTORY: on what the order would borrow, its cost less the sector's cash - Game.consider()'s own test until 0.7.113, which asked nothing of an order the till paid; for the counterf... |
| 517 | `SpreadPlanner.ON_ITS_WHOLE_COST` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash)...` | THE RULE IN FORCE SINCE 0.7.112 (batch I2; Jerus's decision A, 2026-10-08: "test every order on its whole cost, whoever pays"): on the whole cost - its ground at its zone's price with it (BusinessInvestment.getCostOf(... |
| 524 | `SpreadPlanner.DEFAULT_MONEY_GATE` | `ON_ITS_WHOLE_COST` | The money gate every SpreadPlanner tests its sector's orders by unless a check or a probe sets another: ON_ITS_WHOLE_COST since 0.7.112 (decision A). |
| 741 | `SpreadPlanner.TRAILING_MONTHS` | `12` | Months of a kind's appraisals the planner reads its margin over (CY1): a year, which counts a one-month spike a twelfth and a three-month cycle of the tallies at its mean, and lags a city that grows into a works by ha... |
| 744 | `SpreadPlanner.MARGIN_KEY` | `"margin."` | The prefix of each kind's appraisals of one more, newest first, in a client's saved extras ("margin.<KIND>.<age in months>"). |
| 747 | `SpreadPlanner.LAST_KEY` | `"lastMargin."` | ...and of what its last one standing earned ("lastMargin.<KIND>.<age in months>"). |
| 750 | `SpreadPlanner.HOLDING_KEY` | `"lastHolding."` | ...and the holding its last one's figures were noted at ("lastHolding.<KIND>"): a smaller one starts the year again. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 308 | `private final boolean feedAtImport, outputAtImport` |  |
| 526 | `private MoneyGate moneyGate` |  |
| 630 | `private final Map<String, Integer> idle` | Months each kind has stood idle running, by its name. |
| 754 | `private final java.util.ArrayDeque<Double> figures` |  |
| 755 | `private int month` |  |
| 756 | `private double holding` |  |
| 776 | `private final Map<String, Trail> margins` | What one more of each kind was appraised to earn, its last TRAILING_MONTHS. |
| 779 | `private final Map<String, Trail> lasts` | What the last one of each kind standing earned, its last TRAILING_MONTHS. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 766 | **type** `public final class SpreadPlanner` | The spread planner (0.7.82, batch O5; docs/studies/spec-oil.md 2.4, and docs/studies/spec-materials.md 2.6): what a processing sector orders - of the buildings that pass its gates, the one that earns most on its cost ... |

### 1. the city's own price (spec-oil 2.4, cityValue) (lines 104-294)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 6 | `public static double cityValue(GoodsMarket m)` | What one more unit of a good is worth to the city (pure): the net import price (landed and hauled) if its market imported any this month - what the city would pay for the unit made here instead - else the net export p... |
| 160 | 5 | `private static double priceOr(GoodsMarket m, double price)` | A price for one part of a split: itself, or the local price where it is not a number, or nothing where neither is (cityValue()'s fallback). |
| 172 | 8 | `public static double outValue(GoodsMarket m, double imported, double u)` | What `u` units of a good made here are worth to the city (pure; spec-materials 2.6, outValue): the net import price for as many as the city imported this month (`imported`, units), the net export price for the rest. |
| 187 | 8 | `public static double inValue(GoodsMarket m, double exported, double u)` | ...and what `u` units of a good used here cost the city (pure; inValue, the mirror): the net export price for as many as the city exported this month (`exported`, units) - what it would no longer ship - and the net im... |
| 197 | 3 | `public static double outValue(GoodsMarket m, double u)` | ...at the market's own tally of the month: what it imported. |
| 202 | 3 | `public static double inValue(GoodsMarket m, double u)` | ...at the market's own tally of the month: what it exported. |
| 207 | 6 | **type** `public interface Values` | What units of each good are worth to the city at the margin, made here (out) and used here (in): the city's this month (values()) or a check's own frame. |
| 209 | 1 | `double out(Good g, double u)` _(in SpreadPlanner.Values)_ | What `u` units of `g` made here are worth. |
| 211 | 1 | `double in(Good g, double u)` _(in SpreadPlanner.Values)_ | What `u` units of `g` used here cost. |
| 215 | 6 | `public static Values values(Markets markets)` | The city's values this month: outValue() and inValue() at each good's market and its month's tally; nothing for a good with no market. |
| 241 | 12 | `public static Values after(Values v, ToDoubleFunction<Good> comingOut, ToDoubleFunction<Good> comingIn)` | The city's values for one more works after those a sector already has on site (BA1, 2026-11-02; pure in `v`): what `u` units are worth past the `comingOut` units of that good the works on site will make, and the `comi... |
| 270 | 3 | `public static double ownCosts(BuildingsTemplate t, double rate, double factor, double tariff)` | A works' consumables at `factor` and `rate`, less its power at the one flat `tariff` (pure; above): what its earnings lose past running and standing. |
| 275 | 3 | `public static double ownCosts(NewUnit t, double rate, double factor, double tariff)` | ...for one new building already resolved (QN1): its own consumables and power, at the one flat `tariff`. |
| 280 | 3 | `public static double ownCosts(BuildingsTemplate t, double rate, double factor, double tariff, double worksTariff)` | A works' consumables at `factor` and `rate`, less its power as the bill pays it (PW1-R): what replaces its draw at its class's `tariff`, its surplus past the draw at `worksTariff`. |
| 285 | 9 | `public static double ownCosts(NewUnit t, double rate, double factor, double tariff, double worksTariff)` | ...for one new building already resolved (QN1): its own consumables and power, its draw at `tariff` and its surplus at `worksTariff`. |

### the study's four cases (materials-chains-research.md 2.5) (lines 295-348)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 22 | **type** `public enum Case` | The study's four cases: a works' feed and its output each at the import or the export price. |
| 310 | 4 | `Case(boolean feedAtImport, boolean outputAtImport)` _(in SpreadPlanner.Case)_ |  |
| 316 | 1 | `public boolean feedAtImport()` _(in SpreadPlanner.Case)_ | Whether its feed is priced at the import price (else the export price). |
| 318 | 1 | `public boolean outputAtImport()` _(in SpreadPlanner.Case)_ | Whether its output is priced at the import price (else the export price). |
| 330 | 18 | `public static double caseEarns(BuildingsTemplate t, Case c, ToDoubleFunction<Good> importPrice, ToDoubleFunction<Good> exportPr...` | What a works earns a month at nameplate in one of the study's cases (pure; the study's 2.5, at parity with `factor` 1): each good it makes at the case's price for its output, less each it uses at the case's price for ... |

### 2. and 3. a candidate, and the gates (lines 349-450)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 10 | **type** `public enum Gate` | The gates a candidate is asked, in order (spec-oil 2.4). |
| 374 | 15 | **type** `public record Candidate(BuildingsTemplate template, double earns, double cost, Gate failed, String why, Map...` | One building weighed. |
| 378 | 1 | `public double score()` _(in SpreadPlanner.Candidate)_ | What it earns a month on its cost; 0 with no cost. |
| 381 | 1 | `public boolean passes()` _(in SpreadPlanner.Candidate)_ | Whether it passes every gate. |
| 384 | 1 | `public boolean onlyLand()` _(in SpreadPlanner.Candidate)_ | Whether ground is the one gate it fails - the refusal the player can clear, by buying ground. |
| 387 | 1 | `public String refusal(Gate g)` _(in SpreadPlanner.Candidate)_ | A gate's refusal in words; null when it passes that gate. |
| 391 | 29 | **type** `public interface City` | What the planner asks of the city about one building: its costs and its gates - the game's (city()) or a check's own frame. |
| 393 | 1 | `double cost(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | What one costs to order, its ground with it. |
| 395 | 1 | `double running(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | A month's wages, power and water for one, at today's prices. |
| 397 | 1 | `default double running(NewUnit u)` _(in SpreadPlanner.City)_ | ...for one new building already resolved (QN1): a frame that knows no unit answers running() of its template. |
| 399 | 1 | `default NewUnit unit(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | One new building of `t` as this frame values it (QN1): the game's resolves the new unit (BusinessInvestment.newUnit()); a frame that knows none, the template as it stands. |
| 401 | 1 | `double standing(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | A month's repairs and property tax on one. |
| 403 | 1 | `double rate()` _(in SpreadPlanner.City)_ | The share of nameplate the sector's buildings run at. |
| 405 | 1 | `default double rate(NewUnit u)` _(in SpreadPlanner.City)_ | ...as an order weighs one new building at it (OE1; Sector.plannedRate()): the game's reads a first plant's own rate in a sector with nothing running; a frame that knows none, rate(). |
| 407 | 1 | `String land(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | Null when there is ground for one, else why not. |
| 409 | 1 | `String staff(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | Null when the city could staff one, else why not. |
| 411 | 1 | `String money(double earns, double cost)` _(in SpreadPlanner.City)_ | Null when an order earning `earns` a month and costing `cost` passes the money gate, else why not. |
| 418 | 1 | `default String money(BuildingsTemplate t, double earns, double cost)` _(in SpreadPlanner.City)_ | ...for an order of one `t` (BA1, 2026-11-02): the gate also strikes the interest on the wait for it, which depends on the building ordered (the points it owes the builders). |
| 422 | 3 | `public static double earns(BuildingsTemplate t, double gross, City city)` | What one building's spread on its feed earns a month once running: `gross` (at nameplate) at the rate, less its running and standing costs. |
| 427 | 3 | `public static double earns(NewUnit t, double gross, City city)` | ...for one new building already resolved (QN1): its own posts, draw and water in its running costs. |
| 438 | 12 | `public static Candidate judge(BuildingsTemplate t, double earns, double cost, String feed, City city, List<BuildingsTemplate> w...` | Asks a building every gate (pure in `city`): its feed (`feed`, the client's refusal, or null when it has the feed), the ground and the staff for `t`, and the money for `earns` on `cost`. |

### 5. the order (lines 451-482)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 8 | `public static Candidate best(List<Candidate> all)` | The candidate that passes every gate and earns most on its cost (the first on a tie, in the order weighed); null for none. |
| 464 | 8 | `public static Candidate bestBlockedByLand(List<Candidate> all)` | Of the candidates whose only refusal is the ground, the one that earns most on its cost; null for none - the land office's case. |
| 474 | 8 | `public static Candidate bestFed(List<Candidate> all)` | Of the candidates that had their feed, the one that earns most on its cost, gates or not; null when none had - what a refusal is worded by. |

### 4. the money gate, one replaceable piece (lines 483-626)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 6 | **type** `public interface MoneyGate` | What the money gate tests an order on (spec-materials A): the amount the interest is struck on. |
| 496 | 1 | `double testedOn(double cost, double cash)` _(in SpreadPlanner.MoneyGate)_ | The amount the interest is struck on, for an order costing `cost` by a sector holding `cash`. |
| 498 | 1 | `String words()` _(in SpreadPlanner.MoneyGate)_ | The rule in a phrase, for the words. |
| 529 | 1 | `public MoneyGate moneyGate()` | The money gate this sector's orders are tested by: DEFAULT_MONEY_GATE unless a check or a probe set another. |
| 532 | 1 | `public void setMoneyGate(MoneyGate gate)` | A check's or a probe's: tests this sector's orders by another rule (the counterfactual); null puts the default back. |
| 540 | 3 | `public double hurdle(Sector sector, BusinessInvestment plans, Game game, double cost)` | What a month's earnings must reach for an order of `cost` by `sector` to pass the money gate: PROFIT_OVER_INTEREST times a month's interest at the real rate on what the gate tests, as servicesItsOwnDebt() strikes it; ... |
| 550 | 6 | `public double hurdle(Sector sector, BusinessInvestment plans, Game game, BuildingsTemplate t, int n, double cost)` | ...for an order of `n` of `t` (BA1, 2026-11-02): what the gate tests carries the interest on the order's wait (BusinessInvestment.carryOf()), so an order years out must earn it. |
| 558 | 3 | `public boolean passesMoney(Sector sector, BusinessInvestment plans, Game game, double earns, double cost)` | Whether an order earning `earns` a month and costing `cost` passes the money gate: it earns something, and servicesItsOwnDebt() on what the gate tests. |
| 570 | 7 | `public boolean passesMoney(Sector sector, BusinessInvestment plans, Game game, BuildingsTemplate t, int n, double earns, double...` | ...for an order of `n` of `t` (BA1, 2026-11-02): servicesItsOwnDebt() on what the gate tests grown by the wait for the building (BusinessInvestment.carryOf(), >= 1) at the rate a loan of it would be written at - an or... |
| 579 | 3 | `private static double carryOf(BusinessInvestment plans, BuildingsTemplate t, int n, double rate)` | The factor an order's cost grows by over its wait (BusinessInvestment.carryOf()); 1 for no building. |
| 584 | 5 | `private static double rateFor(Sector sector, Game game, double amount)` | The rate a loan of `amount` would be written at for the sector (BusinessDebtManager.projectRate()); prime's placeholder 0 with no game. |
| 591 | 35 | `public City city(Sector sector, BusinessInvestment plans, Game game)` | The game's answers for `sector`'s orders this month: BusinessInvestment's costs, ground and staffing, and this planner's money gate. |

### 6. idle, then shed (lines 627-722)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 4 | `public void noteMonth(String kind, boolean standing, boolean idleThisMonth)` | The month's end for one kind: none standing clears its count; standing and idle adds a month; standing and working starts it again. |
| 649 | 16 | `public void noteMonth(String kind, double holding, int month, double lastEarns)` | ...with what the last one of `holding` standing earned in `month` (CY1): a kind counts a month idle when its last one earned below nothing in the month or over its year (OVER ITS YEAR), and starts again only when it e... |
| 667 | 3 | `public void noteMonth(String kind, boolean standing, int month, double lastEarns)` | ...for a kind standing or not, its holding not compared. |
| 672 | 1 | `public int idleMonths(String kind)` | Months a kind has stood idle running; 0 for one working or with none standing. |
| 681 | 5 | `public boolean mayShed(String kind)` | Whether a kind has stood idle IDLE_MONTHS running and, where its last one's earnings are noted (CY1), lost in the last month noted and over its year: the one its sector may sell back. |
| 688 | 3 | `public void setIdleMonthsForTest(String kind, int months)` | A fixture's: a kind's idle months as a save would give them. |
| 693 | 3 | `public void saveIdle(Map<String, Double> extras, Iterable<String> kinds)` | Into a client's extras: each kind's idle months, every kind named (IDLE_KEY + its name). |
| 698 | 7 | `public void restoreIdle(Map<String, Double> extras, Iterable<String> kinds)` | ...and back: a kind missing, or not a whole number of months, reads none (a save from before 0.7.82). |
| 707 | 1 | `public java.util.Set<String> idleKinds()` | The kinds counting idle months now (0.7.112): what a client whose kinds are its templates' ids saves (ProcessingSector). |
| 710 | 5 | `public void restoreIdle(Map<String, Double> extras)` | ...and back, every kind the extras name (0.7.112): for a client whose kinds are not known until its buildings are (ProcessingSector). |
| 717 | 5 | `public void reset()` | A founding sector: no kind idle. |

### 7. over its year (CY1) (lines 723-858)

| line | len | member | says |
|---:|---:|---|---|
| 753 | 21 | **type** `private static final class Trail` | One kind's figures over its year: the last TRAILING_MONTHS, newest first, the month the newest was noted, and the holding they were noted at (the last one's; NaN where none is known). |
| 759 | 6 | `void note(int m, double x)` _(in SpreadPlanner.Trail)_ | The month's figure: a second in the same month replaces the first (a check that weighs twice, the planner asked again). |
| 767 | 6 | `double mean()` _(in SpreadPlanner.Trail)_ | Their mean; NaN with none. |
| 782 | 3 | `public static double lesser(double month, double year)` | The lesser of a month's figure and its year's mean (pure): the month's where the year is not a number. |
| 787 | 3 | `public void noteMargin(String kind, int month, double earns)` | Notes what one more of `kind` was appraised to earn in `month`; a second note in the same month replaces the first, and a figure that is not finite is not noted. |
| 792 | 4 | `public double marginOverYear(String kind)` | The mean of what one more of `kind` was appraised to earn over its year, this month's with it; NaN with none noted. |
| 798 | 4 | `public int marginMonths(String kind)` | The months of `kind`'s appraisals noted, at most TRAILING_MONTHS. |
| 804 | 4 | `public double lastOverYear(String kind)` | The mean of what the last one of `kind` standing earned over its year; NaN with none standing or none noted. |
| 810 | 7 | `public void saveMargins(Map<String, Double> extras)` | Into a client's extras: each kind's year, newest first (MARGIN_KEY and LAST_KEY + its name + "." + the figure's age). |
| 819 | 11 | `public void restoreMargins(Map<String, Double> extras)` | ...and back, every kind the extras name; a save from before CY1 reads none, and its planner reads the month alone until its year fills. |
| 831 | 6 | `private static void save(Map<String, Double> extras, String prefix, Map<String, Trail> trails)` |  |
| 838 | 20 | `private static void restore(Map<String, Double> extras, String prefix, Map<String, Trail> trails)` |  |


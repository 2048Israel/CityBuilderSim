# SpreadPlanner.java - 343 lines · 32 methods · 5 constants · sectors

`ham/citybuildersim/sectors/SpreadPlanner.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The spread planner (0.7.82, batch O5; runs/spec-oil.md 2.4, and
> runs/spec-materials.md 2.6): what a processing sector orders - of the
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
>      export price it nets while it exports it, the local price otherwise.
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
>   4. THE MONEY GATE IS ONE REPLACEABLE PIECE (MoneyGate). In force: the
>      game's own test, 1.25 times the interest at the real rate on what the
>      order would borrow (ON_ITS_BORROWING) - which asks nothing of an
>      order the sector's cash pays. The stricter rule spec-materials.md
>      puts to Jerus (its item A) tests the whole cost whoever pays
>      (ON_ITS_WHOLE_COST); it is here to be measured, not in force.
>   5. THE ORDER (best()): the best earnings on cost of those that pass; the
>      client holds to one order in flight (MAX_CONCURRENT_ORDERS) first.
>   6. IDLE, THEN SHED (noteMonth(), idleMonths(), mayShed()): the months
>      each kind has stood with nothing to do, by name, saved in the
>      client's extras ("idleMonths.<KIND>"); a kind idle IDLE_MONTHS
>      running is the one its sector may sell back.
> 
> One planner a client sector, held by it (Refining.planner()): its money
> gate and its idle months are that sector's.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (15), [BusinessInvestment](BusinessInvestment.md) (6), [Sector](Sector.md) (5), [Game](Game.md) (4), [Rail](Rail.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1)

**Used by (9):** [LongPlaytest](LongPlaytest.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md)

## Sections

| line | section |
|---:|---|
| 76 | · 1. the city's own price (spec-oil 2.4, cityValue) |
| 97 | · 2. and 3. a candidate, and the gates |
| 181 | · 5. the order |
| 213 | · 4. the money gate, one replaceable piece |
| 303 | · 6. idle, then shed |

## Enum constants

| line | constant | says |
|---:|---|---|
| 102 | `SpreadPlanner.Gate.FEED` | Spare stream for FEED_GATE of one: a unit's feed, or for a crude unit the city's imports or the wells' spare. |
| 104 | `SpreadPlanner.Gate.LAND` | Ground for one (BusinessInvestment.plotsAvailableFor()). |
| 106 | `SpreadPlanner.Gate.STAFF` | Staff for one (Sector.staffing()). |
| 108 | `SpreadPlanner.Gate.MONEY` | It earns something, and enough for the money gate (MoneyGate). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `SpreadPlanner.FEED_GATE` | `Rail.MIN_LINE_UTILISATION` | The least share of one building's feed the spare stream must cover for it to be weighed at all: the railway's MIN_LINE_UTILISATION, nothing built to stand idle (spec-oil 2.4). |
| 71 | `SpreadPlanner.IDLE_MONTHS` | `BusinessInvestment.RETIREMENT_LOSS_MONTHS` | Months a kind must stand idle running before its sector may sell it back (spec-oil 2.4): six, RETIREMENT_LOSS_MONTHS - as long as the spare-capacity rule waits on losses. |
| 74 | `SpreadPlanner.IDLE_KEY` | `"idleMonths."` | The prefix of each kind's idle months in a client's saved extras. |
| 232 | `SpreadPlanner.ON_ITS_BORROWING` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash)...` | THE RULE IN FORCE: on what the order would borrow, its cost less the sector's cash - Game.consider()'s own test, which asks nothing of an order the till pays. |
| 239 | `SpreadPlanner.ON_ITS_WHOLE_COST` | `new MoneyGate() { @ Override public double testedOn(double cost, double cash)...` | THE STRICTER RULE, NOT IN FORCE (spec-materials.md, Jerus's item A): on the whole cost, whoever pays - for the counterfactual (LongPlaytest's -Dplaytest.moneyGate=whole, the probes). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 245 | `private MoneyGate moneyGate` |  |
| 306 | `private final Map<String, Integer> idle` | Months each kind has stood idle running, by its name. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 279 | **type** `public final class SpreadPlanner` | The spread planner (0.7.82, batch O5; runs/spec-oil.md 2.4, and runs/spec-materials.md 2.6): what a processing sector orders - of the buildings that pass its gates, the one that earns most on its cost at the city's ow... |

### 1. the city's own price (spec-oil 2.4, cityValue) (lines 76-96)

| line | len | member | says |
|---:|---:|---|---|
| 89 | 7 | `public static double cityValue(GoodsMarket m)` | What one more unit of a good is worth to the city (pure): the net import price (landed and hauled) if its market imported any this month, else the net export price if it exported any, else the local price. |

### 2. and 3. a candidate, and the gates (lines 97-180)

| line | len | member | says |
|---:|---:|---|---|
| 100 | 10 | **type** `public enum Gate` | The gates a candidate is asked, in order (spec-oil 2.4). |
| 122 | 15 | **type** `public record Candidate(BuildingsTemplate template, double earns, double cost, Gate failed, String why, Map...` | One building weighed. |
| 126 | 1 | `public double score()` _(in SpreadPlanner.Candidate)_ | What it earns a month on its cost; 0 with no cost. |
| 129 | 1 | `public boolean passes()` _(in SpreadPlanner.Candidate)_ | Whether it passes every gate. |
| 132 | 1 | `public boolean onlyLand()` _(in SpreadPlanner.Candidate)_ | Whether ground is the one gate it fails - the refusal the player can clear, by buying ground. |
| 135 | 1 | `public String refusal(Gate g)` _(in SpreadPlanner.Candidate)_ | A gate's refusal in words; null when it passes that gate. |
| 139 | 16 | **type** `public interface City` | What the planner asks of the city about one building: its costs and its gates - the game's (city()) or a check's own frame. |
| 141 | 1 | `double cost(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | What one costs to order, its ground with it. |
| 143 | 1 | `double running(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | A month's wages, power and water for one, at today's prices. |
| 145 | 1 | `double standing(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | A month's repairs and property tax on one. |
| 147 | 1 | `double rate()` _(in SpreadPlanner.City)_ | The share of nameplate the sector's buildings run at. |
| 149 | 1 | `String land(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | Null when there is ground for one, else why not. |
| 151 | 1 | `String staff(BuildingsTemplate t)` _(in SpreadPlanner.City)_ | Null when the city could staff one, else why not. |
| 153 | 1 | `String money(double earns, double cost)` _(in SpreadPlanner.City)_ | Null when an order earning `earns` a month and costing `cost` passes the money gate, else why not. |
| 157 | 3 | `public static double earns(BuildingsTemplate t, double gross, City city)` | What one building's spread on its feed earns a month once running: `gross` (at nameplate) at the rate, less its running and standing costs. |
| 168 | 12 | `public static Candidate judge(BuildingsTemplate t, double earns, double cost, String feed, City city, List<BuildingsTemplate> w...` | Asks a building every gate (pure in `city`): its feed (`feed`, the client's refusal, or null when it has the feed), the ground and the staff for `t`, and the money for `earns` on `cost`. |

### 5. the order (lines 181-212)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 8 | `public static Candidate best(List<Candidate> all)` | The candidate that passes every gate and earns most on its cost (the first on a tie, in the order weighed); null for none. |
| 194 | 8 | `public static Candidate bestBlockedByLand(List<Candidate> all)` | Of the candidates whose only refusal is the ground, the one that earns most on its cost; null for none - the land office's case. |
| 204 | 8 | `public static Candidate bestFed(List<Candidate> all)` | Of the candidates that had their feed, the one that earns most on its cost, gates or not; null when none had - what a refusal is worded by. |

### 4. the money gate, one replaceable piece (lines 213-302)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 6 | **type** `public interface MoneyGate` | What the money gate tests an order on (spec-materials A): the amount the interest is struck on. |
| 226 | 1 | `double testedOn(double cost, double cash)` _(in SpreadPlanner.MoneyGate)_ | The amount the interest is struck on, for an order costing `cost` by a sector holding `cash`. |
| 228 | 1 | `String words()` _(in SpreadPlanner.MoneyGate)_ | The rule in a phrase, for the words. |
| 248 | 1 | `public MoneyGate moneyGate()` | The money gate this sector's orders are tested by: ON_ITS_BORROWING unless a check or a probe set another. |
| 251 | 1 | `public void setMoneyGate(MoneyGate gate)` | A check's or a probe's: tests this sector's orders by another rule (the counterfactual). |
| 259 | 6 | `public double hurdle(Sector sector, BusinessInvestment plans, Game game, double cost)` | What a month's earnings must reach for an order of `cost` by `sector` to pass the money gate: PROFIT_OVER_INTEREST times a month's interest at the real rate on what the gate tests, as servicesItsOwnDebt() strikes it; ... |
| 267 | 5 | `public boolean passesMoney(Sector sector, BusinessInvestment plans, Game game, double earns, double cost)` | Whether an order earning `earns` a month and costing `cost` passes the money gate: it earns something, and servicesItsOwnDebt() on what the gate tests. |
| 274 | 5 | `private static double rateFor(Sector sector, Game game, double amount)` | The rate a loan of `amount` would be written at for the sector (BusinessDebtManager.projectRate()); prime's placeholder 0 with no game. |
| 281 | 21 | `public City city(Sector sector, BusinessInvestment plans, Game game)` | The game's answers for `sector`'s orders this month: BusinessInvestment's costs, ground and staffing, and this planner's money gate. |

### 6. idle, then shed (lines 303-343)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 4 | `public void noteMonth(String kind, boolean standing, boolean idleThisMonth)` | The month's end for one kind: none standing clears its count; standing and idle adds a month; standing and working starts it again. |
| 315 | 1 | `public int idleMonths(String kind)` | Months a kind has stood idle running; 0 for one working or with none standing. |
| 318 | 1 | `public boolean mayShed(String kind)` | Whether a kind has stood idle IDLE_MONTHS running: the one its sector may sell back. |
| 321 | 3 | `public void setIdleMonthsForTest(String kind, int months)` | A fixture's: a kind's idle months as a save would give them. |
| 326 | 3 | `public void saveIdle(Map<String, Double> extras, Iterable<String> kinds)` | Into a client's extras: each kind's idle months, every kind named (IDLE_KEY + its name). |
| 331 | 7 | `public void restoreIdle(Map<String, Double> extras, Iterable<String> kinds)` | ...and back: a kind missing, or not a whole number of months, reads none (a save from before 0.7.82). |
| 340 | 3 | `public void reset()` | A founding sector: no kind idle. |


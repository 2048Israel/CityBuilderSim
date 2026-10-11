# ProcessingSector.java - 411 lines · 36 methods · 0 constants · sectors

`ham/citybuildersim/sectors/ProcessingSector.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> A sector that turns goods into other goods in works it builds by what
> pays: the template every processing industry of the materials chains
> extends (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.6a and 2.7).
> 
> WHY. The materials chains bring four processing industries - Iron & Steel
> (Heavy Industry, from batch I3), Cement & Concrete, Copper Refining and
> Wood Products - each "only one sector and basically that sector just
> decides which buildings to build to make the most profit, aka following
> the demand" (Jerus, the study's 1.1). The refinery has built that way
> since 0.7.82 (SpreadPlanner, its first client), but in its own words: its
> flow, its packages, its run. A works is simpler - it buys its feed and
> sells its products on the city's markets - and the rule is the same for
> all four, so it is written here once, and a chain's processing sector is a
> declaration: what it makes, what it uses, and its buildings' rows.
> 
> THE PLAN (plan(); spec-materials 2.6a, the study's 1.2): every building of
> its that makes something (weighs()) is a candidate (estimate()), earning
> a month at the rate the sector runs
> 
>     what it makes, valued made here (SpreadPlanner.outValue(): the import
>       price for as much as the city imports, the export price past it)
>   - what it uses, valued used here (SpreadPlanner.inValue(): the export
>       price for as much as the city exports, the import price past it)
>   - its running cost (wages, power and water) and its standing cost
>       (repairs and property tax, at its zone's price - Z4)
>   - its consumables at the exchange factor, + its own power at the tariff
>       (SpreadPlanner.ownCosts())
>   - since 0.7.123 (batch I4) a feed no market sells (offMarketFeed(): the
>       Scrap Yard's old steel) and its lorries' wear and diesel
>       (fleetRunning()), on its cost with the vans it must buy
>       (fleetCapital()) - and at the share of nameplate its feed fills
>       (feedShare())
> 
> and is asked the gates in SpreadPlanner's order - its feed (feedRefusal():
> none by default, a works' feed being the market's), the ground
> (BusinessInvestment.plotsAvailableFor(), its zone's held ground with it),
> the staff and the money, which since 0.7.112 tests the whole cost whoever
> pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus's decision A). The one that
> earns most on its cost is ordered, one order in flight. What Game.consider()
> tests the order on is that same estimate (estimatedMonthlyProfit()).
> 
> CAPTIVE USE (spec-materials 2.7): every good it both makes and uses goes
> to its own need first (captive(): Sector, CAPTIVE USE).
> 
> IDLE, THEN SHED (O5's rule, the refinery's; extras idleMonths.<id>): at
> each month's end every kind of its works standing is counted idle or
> working by what the last one of them earns at the month's prices
> (lastEarns()) - ★ the mirror of a candidate's: what it makes valued at
> what losing it would cost the city (the export price for what the city
> exported of it, the import price past it) and what it uses at what losing
> it would free (the import price for what the city imported, the export
> price past it), so a works that replaces imports is not shed because one
> more would have to export. A kind below nothing IDLE_MONTHS running is the
> one mayRetire() passes, and retirementDemandAndCapacity() is then {0, its
> buildings}, so the spare-capacity rule sells it while the sector loses
> money; with no kind idle so long, every kind is the distress rule's, as
> every maker's plant is. Its kinds are its templates' ids, which are
> permanent: a rename (the chains rename buildings) keeps the count.
> 
> HEAVY INDUSTRY - IRON & STEEL - EXTENDS IT SINCE 0.7.120 (batch I3), its
> ... (6 more lines in the source)

**Uses:** [SpreadPlanner](SpreadPlanner.md) (45), [BuildingsTemplate](BuildingsTemplate.md) (24), [Good](Good.md) (11), [BusinessInvestment](BusinessInvestment.md) (11), [Game](Game.md) (5), [Sector](Sector.md) (4), [BuildingType](BuildingType.md) (1)

**Used by (3):** [ExtractionSector](ExtractionSector.md), [HeavyIndustry](HeavyIndustry.md), [MaterialsCheck](MaterialsCheck.md)

## Sections

| line | section |
|---:|---|
| 94 | · captive use (Sector, CAPTIVE USE) |
| 102 | · the candidates |
| 285 | · the plan |
| 331 | · idle, then shed |
| 394 | · its idle months across a save |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `private final SpreadPlanner planner` | Its spread planner: its money gate and its kinds' idle months. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 330 | **type** `public abstract class ProcessingSector extends Sector` | A sector that turns goods into other goods in works it builds by what pays: the template every processing industry of the materials chains extends (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.6a and 2.7). |
| 87 | 3 | `protected ProcessingSector(String key, String label, BuildingType group)` |  |
| 92 | 1 | `public SpreadPlanner planner()` | Its spread planner (a check or a probe sets its money gate for the counterfactual). |

### captive use (Sector, CAPTIVE USE) (lines 94-101)

| line | len | member | says |
|---:|---:|---|---|
| 98 | 3 | `public boolean captive(Good g)` | Every good it both makes and uses goes to its own need first. |

### the candidates (lines 102-284)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 3 | `protected boolean weighs(BuildingsTemplate t)` | Whether the planner weighs a building of its: one that makes something. |
| 110 | 3 | `protected String feedRefusal(BuildingsTemplate t)` | Its feed gate for a building, in words, or null when it has its feed: none by default - a works buys its feed on the market, imported past what the city makes. |
| 126 | 3 | `protected double feedShare(BuildingsTemplate t)` | The share of its nameplate one more of `t` would run at for want of a feed no market sells: all of it by default (HeavyIndustry's Scrap Yard, on the city's old steel). |
| 131 | 3 | `protected double feedShareStanding(BuildingsTemplate t)` | ...and the last one of `t` standing (IDLE, THEN SHED): all of it by default. |
| 136 | 3 | `protected double offMarketFeed(BuildingsTemplate t, double rate)` | What one of `t` pays a month at `rate` (the share of nameplate it runs at) for a feed no market sells: nothing by default (HeavyIndustry's Scrap Yard: its old steel). |
| 141 | 3 | `public double exchangeFactor()` | What its works' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. |
| 146 | 3 | `public double tariff()` | What its works' power sells and buys at: the tariff, as it is billed. |
| 151 | 6 | `public static double gross(BuildingsTemplate t, SpreadPlanner.Values v, double rate)` | What one of `t` would make less what it would use a month at `rate`, each valued as one more of it would change the city's trade (pure): made at Values.out(), used at Values.in(). |
| 159 | 6 | `public static double grossStanding(BuildingsTemplate t, SpreadPlanner.Values v, double rate)` | ...and what the last one of `t` standing makes less what it uses (pure; the mirror): made at Values.in() - what losing it would cost - and used at Values.out(), what losing it would free. |
| 172 | 6 | `public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double fac...` | One building weighed (pure in its arguments): what one more would earn a month at the city's values and the frame's rate - gross() less its running and standing costs and SpreadPlanner.ownCosts() - asked every gate, i... |
| 188 | 9 | `public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double fac...` | ...run at `share` of the sector's rate for want of a feed no market sells, paying `other` a month past its running, standing and own costs - that feed (offMarketFeed()) and its lorries (fleetRunning()) - and costing `... |
| 199 | 5 | `public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double factor, double tariff)` | What the last one of `t` standing earns a month (pure): grossStanding() less the same costs a candidate's earnings lose. |
| 206 | 7 | `public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double factor, double tari...` | ...run at `share` of the sector's rate and paying `other` a month past its running, standing and own costs (0.7.123): lastEarns() above at a share of 1 and none. |
| 236 | 9 | `public static double vansFor(BuildingsTemplate t)` | Vans one of `t` keeps: the tonnes it moves in and out at nameplate over Sector.TONNES_PER_VAN (pure). |
| 247 | 5 | `public static double fleetRunning(BuildingsTemplate t, SpreadPlanner.Values v, double rate)` | What one of `t`'s lorries cost a month at `rate` (pure): a Sector.VAN_LIFE_MONTHS-th of its vans worn out, and the diesel its vans burn at the rate, each at what it costs here. |
| 254 | 4 | `public static double fleetCapital(BuildingsTemplate t, SpreadPlanner.Values v)` | ...and buying them (pure): its vans at what a van costs here. |
| 260 | 6 | `private SpreadPlanner.Candidate weigh(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city)` | One of `t` weighed by this sector at the city's values: its feed's share and price, and its lorries, with it. |
| 268 | 11 | `public List<SpreadPlanner.Candidate> appraise(BusinessInvestment plans, Game game)` | Every building of its the planner weighs, weighed now at the city's values and the game's gates, in the catalogue's order. |
| 281 | 3 | `public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans)` | One building of its weighed now. |

### the plan (lines 285-330)

| line | len | member | says |
|---:|---:|---|---|
| 288 | 3 | `static String a(String name)` | "a Coke Oven", "an Electric Arc Furnace". |
| 300 | 23 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to build, and what (THE PLAN, above): one order in flight; of its works that pass the gates, the one that earns most on its cost; with none, ground if ground was all that stood in the way, else the best-fed ca... |
| 326 | 4 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more of its works would earn its owner a month: the planner's estimate (THE PLAN) - what Game.consider() tests the order on, and the build card shows. |

### idle, then shed (lines 331-393)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 3 | `public static String kindOf(BuildingsTemplate t)` | A kind's name in the idle book and the extras: its template's id, which is permanent. |
| 340 | 3 | `public void endOfMonth(Game game)` | The month's end: each kind of its works counted idle or working (IDLE, THEN SHED). |
| 345 | 13 | `protected void noteIdleWorks(Game game)` | Each kind of its works standing counted idle (the last one earns below nothing at the month's values) or working; a kind with none standing clears its count. |
| 360 | 3 | `public int idleMonths(BuildingsTemplate t)` | Months a kind of its works has stood idle running (extras idleMonths.<id>). |
| 365 | 8 | `public double idleBuildings()` | Its buildings of the kinds idle long enough to shed, standing. |
| 376 | 4 | `public boolean mayRetire(BuildingsTemplate t)` | A kind idle long enough while one is; any of its buildings while none is (the distress rule's, as every maker's plant is). |
| 383 | 4 | `public double[] retirementDemandAndCapacity(Game game)` | The spare-capacity rule's measure: {0, the idle kinds' buildings} while a kind stands idle long enough to shed; otherwise none - a works shrinks on distress (the mills' rule to 0.7.119). |
| 390 | 3 | `public double unitsOf(BuildingsTemplate t)` | What one of its buildings is measured in, for the retirement rules: one building, for a works. |

### its idle months across a save (lines 394-411)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 3 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 403 | 3 | `protected void restoreExtras(Map<String, Double> extras)` | A save without them reads none. |
| 408 | 3 | `protected void resetExtras()` |  |


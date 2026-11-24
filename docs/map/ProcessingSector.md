# ProcessingSector.java - 733 lines · 61 methods · 0 constants · sectors

`ham/citybuildersim/sectors/ProcessingSector.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> A sector that turns goods into other goods in works it builds by what
> pays: the template every processing industry of the materials chains
> extends (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.6a and
> 2.7).
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
>       Scrap Yard's old steel) and, since FR2, its freight: its vans' wear
>       and diesel and its bulk's city legs at Haulage's quote
>       (freightRunning()), on its cost with the vans it must buy
>       (fleetCapital()) - and at the share of nameplate its feed fills
>       (feedShare())
> 
> and is asked the gates in SpreadPlanner's order - its feed (feedRefusal():
> none by default, a works' feed being the market's), the ground
> (BusinessInvestment.plotsAvailableFor(), its zone's held ground with it),
> the staff and the money, which since 0.7.112 tests the whole cost whoever
> pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus's decision A). The one that
> earns most on its cost is ordered; no order in flight is waited for (BA1,
> 2026-11-02; it was one), because what is on site is counted in what the next
> is weighed against (plannedValues()). What Game.consider()
> tests the order on is that same estimate (estimatedMonthlyProfit()).
> The city's values a works is weighed at are values() (0.7.131, batch I7):
> SpreadPlanner.values(), with the limestone the quarries carried into
> aggregate counted as exported - and a sector may weigh a feed of its own
> there too (Copper Refining's old copper, 0.7.133).
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
> ... (15 more lines in the source)

**Uses:** [SpreadPlanner](SpreadPlanner.md) (81), [BuildingsTemplate](BuildingsTemplate.md) (44), [Good](Good.md) (27), [NewUnit](NewUnit.md) (21), [BusinessInvestment](BusinessInvestment.md) (13), [Game](Game.md) (6), [Sector](Sector.md) (4), [Traffic](Traffic.md) (4), [Haulage](Haulage.md) (4), [Currency](Currency.md) (3), [GoodsMarket](GoodsMarket.md) (2), [BuildingType](BuildingType.md) (1), [Trade](Trade.md) (1)

**Used by (22):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BusinessInvestment](BusinessInvestment.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [ExtractionSector](ExtractionSector.md), [Forestry](Forestry.md), [HaulageCheck](HaulageCheck.md), [HeavyIndustry](HeavyIndustry.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [Manufacturing](Manufacturing.md), [MaterialsCheck](MaterialsCheck.md), [RecipeCheck](RecipeCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 125 | · captive use (Sector, CAPTIVE USE) |
| 133 | · the candidates |
| 473 | · the plan |
| 561 | · idle, then shed |
| 622 | · the run its works made (CY1) |
| 664 | · its idle months across a save |
| 684 | THE PICTOGRAMS' READS (0.7.141, batch I11; docs/studies/spec-materials.md |

## Fields (state)

| line | field | says |
|---:|---|---|
| 99 | `private final SpreadPlanner planner` | Its spread planner: its money gate and its kinds' idle months. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 96 | 638 | **type** `public abstract class ProcessingSector extends Sector` | A sector that turns goods into other goods in works it builds by what pays: the template every processing industry of the materials chains extends (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.6a and 2.7). |
| 101 | 3 | `protected ProcessingSector(String key, String label, BuildingType group)` |  |
| 106 | 1 | `public SpreadPlanner planner()` | Its spread planner (a check or a probe sets its money gate for the counterfactual). |
| 115 | 9 | `public void shipUnsoldStock(Good g, GoodsMarket market)` | The planner's world buyers also buy from its wholesale shed (BF1). |

### captive use (Sector, CAPTIVE USE) (lines 125-132)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 3 | `public boolean captive(Good g)` | Every good it both makes and uses goes to its own need first. |

### the candidates (lines 133-472)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 3 | `protected boolean weighs(BuildingsTemplate t)` | Whether the planner weighs a building of its: one that makes something. |
| 141 | 3 | `protected boolean weighs(NewUnit t)` | ...for one new building already resolved (QN1): whether it makes something. |
| 146 | 3 | `protected String feedRefusal(BuildingsTemplate t)` | Its feed gate for a building, in words, or null when it has its feed: none by default - a works buys its feed on the market, imported past what the city makes. |
| 162 | 3 | `protected double feedShare(BuildingsTemplate t)` | The share of its nameplate one more of `t` would run at for want of a feed no market sells: all of it by default (HeavyIndustry's Scrap Yard, on the city's old steel). |
| 167 | 3 | `protected double feedShareStanding(BuildingsTemplate t)` | ...and the last one of `t` standing (IDLE, THEN SHED): all of it by default. |
| 172 | 3 | `protected double offMarketFeed(BuildingsTemplate t, double rate)` | What one of `t` pays a month at `rate` (the share of nameplate it runs at) for a feed no market sells: nothing by default (HeavyIndustry's Scrap Yard: its old steel). |
| 177 | 3 | `public double exchangeFactor()` | What its works' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. |
| 182 | 3 | `public double tariff()` | What its works' power buys at: the industry price of the struck book, as Heavy Industry is billed (every works of a processing sector is Heavy Industry: high voltage, the energy and the long wires, spec-electricity 2.6). |
| 187 | 3 | `public double worksTariff()` | What its works' surplus is paid: the struck book's price of a kW the works sold the grid (Sector.getPowerSoldValue()). |
| 192 | 3 | `public static double gross(BuildingsTemplate t, SpreadPlanner.Values v, double rate)` | What one of `t` would make less what it would use a month at `rate`, each valued as one more of it would change the city's trade (pure): made at Values.out(), used at Values.in(). |
| 197 | 6 | `public static double gross(NewUnit t, SpreadPlanner.Values v, double rate)` | ...for one new building already resolved (QN1): its own makes and uses. |
| 205 | 6 | `public static double grossStanding(BuildingsTemplate t, SpreadPlanner.Values v, double rate)` | ...and what the last one of `t` standing makes less what it uses (pure; the mirror): made at Values.in() - what losing it would cost - and used at Values.out(), what losing it would free. |
| 218 | 4 | `public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double fac...` | One building weighed (pure in its arguments): what one more would earn a month at the city's values and the frame's rate - gross() less its running and standing costs and SpreadPlanner.ownCosts() - asked every gate, i... |
| 224 | 7 | `public static SpreadPlanner.Candidate estimate(NewUnit u, SpreadPlanner.Values v, SpreadPlanner.City city, double factor, doubl...` | ...for one new building already resolved (QN1): its own makes, uses, posts, draw, consumables and power. |
| 241 | 5 | `public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double fac...` | ...run at `share` of the sector's rate for want of a feed no market sells, paying `other` a month past its running, standing and own costs - that feed (offMarketFeed()) and its freight (freightRunning()) - and costing... |
| 248 | 10 | `public static SpreadPlanner.Candidate estimate(NewUnit u, SpreadPlanner.Values v, SpreadPlanner.City city, double factor, doubl...` | ...for one new building already resolved (QN1). |
| 260 | 5 | `public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double factor, double tariff)` | What the last one of `t` standing earns a month (pure): grossStanding() less the same costs a candidate's earnings lose. |
| 267 | 7 | `public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city, double factor, double tari...` | ...run at `share` of the sector's rate and paying `other` a month past its running, standing and own costs (0.7.123): lastEarns() above at a share of 1 and none. |
| 313 | 3 | `public static double vansFor(BuildingsTemplate t)` | Vans one of `t` keeps: the GOODS tonnes it moves in and out at nameplate over Sector.TONNES_PER_VAN (pure; Sector.tonnesMoved()'s count, template by template). |
| 318 | 13 | `public static double vansFor(NewUnit t)` | ...for one new building already resolved (QN1): the goods tonnes its own recipe moves. |
| 333 | 3 | `public static double fleetRunning(BuildingsTemplate t, SpreadPlanner.Values v, double rate)` | What one of `t`'s vans cost a month at `rate` (pure): a Sector.VAN_LIFE_MONTHS-th of its vans worn out, and the diesel its vans burn at the rate, each at what it costs here. |
| 338 | 5 | `public static double fleetRunning(NewUnit t, SpreadPlanner.Values v, double rate)` | ...for one new building already resolved (QN1). |
| 345 | 3 | `public static double fleetCapital(BuildingsTemplate t, SpreadPlanner.Values v)` | ...and buying them (pure): its vans at what a van costs here. |
| 350 | 4 | `public static double fleetCapital(NewUnit t, SpreadPlanner.Values v)` | ...for one new building already resolved (QN1). |
| 362 | 3 | `public static double cityLegs(BuildingsTemplate t, double rate, Haulage.LegPrice p)` | What the city legs of `t`'s bulk cost a month at `rate` (pure; FR2): `rate` times every bulk unit it uses at nameplate, in tonnes, at what a received tonne's leg costs (p.in()) plus every bulk unit it makes, in tonnes... |
| 367 | 15 | `public static double cityLegs(NewUnit t, double rate, Haulage.LegPrice p)` | ...for one new building already resolved (QN1): the bulk its own recipe uses and makes. |
| 384 | 3 | `public static double freightRunning(BuildingsTemplate t, SpreadPlanner.Values v, double rate, Haulage.LegPrice p)` | What one more of `t` pays for its freight a month at `rate` (pure; FR2): its vans' wear and diesel, and its bulk's city legs at the quote. |
| 389 | 3 | `public static double freightRunning(NewUnit t, SpreadPlanner.Values v, double rate, Haulage.LegPrice p)` | ...for one new building already resolved (QN1). |
| 394 | 3 | `private SpreadPlanner.Candidate weigh(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city)` | One of `t` weighed by this sector at the city's values: its feed's share and price, and its freight, with it. |
| 399 | 7 | `private SpreadPlanner.Candidate weigh(NewUnit u, SpreadPlanner.Values v, SpreadPlanner.City city)` | ...for one new building already resolved (QN1): the feed it takes and the freight it moves are its own recipe's. |
| 421 | 12 | `protected SpreadPlanner.Values values()` | The city's values a works of its is weighed at this month: SpreadPlanner.values(), limestone carried into aggregate counted as exported (above). |
| 443 | 6 | `protected SpreadPlanner.Values plannedValues()` | The city's values one more works of its is weighed at (BA1, 2026-11-02): values(), past what its works on site will make and use (SpreadPlanner.after()). |
| 451 | 11 | `public List<SpreadPlanner.Candidate> appraise(BusinessInvestment plans, Game game)` | Every building of its the planner weighs, weighed now at the city's values and the game's gates, in the catalogue's order. |
| 464 | 3 | `public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans)` | One building of its weighed now. |
| 469 | 3 | `public SpreadPlanner.Candidate appraise(NewUnit u, BusinessInvestment plans)` | ...for one new building already resolved (QN1). |

### the plan (lines 473-560)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 3 | `static String a(String name)` | "a Coke Oven", "an Electric Arc Furnace". |
| 492 | 26 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to build, and what (THE PLAN, above): of its works that pass the gates, the one that earns most on its cost; with none, ground if ground was all that stood in the way, else the best-fed candidate's refusal, wo... |
| 525 | 19 | `private List<SpreadPlanner.Candidate> overYear(List<SpreadPlanner.Candidate> month, BusinessInvestment plans, Game game, Map<Bu...` | Each candidate tested on the lesser of its month's earnings and its year's (SpreadPlanner, OVER ITS YEAR; CY1): the month's noted for its kind first, then a candidate whose year reads lower is asked its gates again on... |
| 546 | 6 | `private String yearWords(BuildingsTemplate t, Map<BuildingsTemplate, Double> held)` | The plan's words for a candidate its year held lower: its mean over the months noted, against the month's; nothing for one the month decided. |
| 555 | 5 | `public double estimatedMonthlyProfit(NewUnit u, BusinessInvestment plans)` | What one more of its works would earn its owner a month: the planner's estimate (THE PLAN) - what Game.consider() tests the order on, and the build card shows. |

### idle, then shed (lines 561-621)

| line | len | member | says |
|---:|---:|---|---|
| 564 | 3 | `public static String kindOf(BuildingsTemplate t)` | A kind's name in the idle book and the extras: its template's id, which is permanent. |
| 570 | 3 | `public void endOfMonth(Game game)` | The month's end: each kind of its works counted idle or working (IDLE, THEN SHED). |
| 575 | 11 | `protected void noteIdleWorks(Game game)` | Each kind of its works standing counted idle (the last one earns below nothing at the month's values) or working; a kind with none standing clears its count. |
| 588 | 3 | `public int idleMonths(BuildingsTemplate t)` | Months a kind of its works has stood idle running (extras idleMonths.<id>). |
| 593 | 8 | `public double idleBuildings()` | Its buildings of the kinds idle long enough to shed, standing. |
| 604 | 4 | `public boolean mayRetire(BuildingsTemplate t)` | A kind idle long enough while one is; any of its buildings while none is (the distress rule's, as every maker's plant is). |
| 611 | 4 | `public double[] retirementDemandAndCapacity(Game game)` | The spare-capacity rule's measure: {0, the idle kinds' buildings} while a kind stands idle long enough to shed; otherwise none - a works shrinks on distress (the mills' rule to 0.7.119). |
| 618 | 3 | `public double unitsOf(BuildingsTemplate t)` | What one of its buildings is measured in, for the retirement rules: one building, for a works. |

### the run its works made (CY1) (lines 622-663)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 14 | `public double standingRun(BuildingsTemplate t)` | The share of nameplate the works that make `t`'s goods would run at on last month's make, counted over those standing and on site now (CY1): of each good `t` makes, what was made (Output's produced, export-bound and c... |
| 655 | 8 | `protected String underRunRefusal(BuildingsTemplate t)` | A feed gate on the run (CY1; a client's feedRefusal() may return it): no more of `t` while the works standing and on site that make its goods would run under FEED_GATE of nameplate on last month's make - the gate Heav... |

### its idle months across a save (lines 664-683)

| line | len | member | says |
|---:|---:|---|---|
| 667 | 4 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 674 | 4 | `protected void restoreExtras(Map<String, Double> extras)` | A save without them reads none; one from before CY1 no year, which its planner fills again (SpreadPlanner.restoreMargins()). |
| 680 | 3 | `protected void resetExtras()` |  |

### THE PICTOGRAMS' READS (0.7.141, batch I11; docs/studies/spec-materials.md (lines 684-733)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 14 | `private double lastOneEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city)` | What the last one of `t` standing earns a month at `v` and `city`: lastEarns() at the share of nameplate its feed fills (feedShareStanding()), that feed and its lorries in its costs - IDLE, THEN SHED's test, and the p... |
| 714 | 3 | `public boolean isWeighed(BuildingsTemplate t)` | Whether the plan weighs a building of its, and the idle book counts its kind (weighs()): one that makes something. |
| 719 | 4 | `public double lastEarns(BuildingsTemplate t)` | What the last one of `t` standing earns a month at this month's values, as IDLE, THEN SHED counts it (lastOneEarns()) - below nothing an idle month; NaN with no city or markets. |
| 725 | 8 | `public double lastEarnsAt(BuildingsTemplate t, double rate)` | Standing earnings on the production actually dispatched, whose rate already includes feed/order limits (R2c). |


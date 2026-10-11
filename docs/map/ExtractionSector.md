# ExtractionSector.java - 264 lines · 14 methods · 0 constants · sectors

`ham/citybuildersim/sectors/ExtractionSector.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> A sector that digs or cuts one resource out of the ground the city owns:
> the template every extraction industry of the materials chains extends
> (0.7.112, batch I2; runs/spec-materials.md 2.1 and 2.6b).
> 
> WHY. The materials chains bring four more industries that lift their
> good off the city's fields - Coal Mining, Quarrying, Copper Mining and
> Forestry - and each is Mining's shape: the ground, not the plant, is the
> limit; a mine stands on a site of a field the city bought; one order at a
> time, the best return on its cost; and a mine over a worked-out field is
> spare by any measure. Until 0.7.112 that shape was Mining's own code, for
> iron. Written here once, a chain's extraction sector says only which
> resource it lifts and what it makes, and stays a declaration.
> 
> WHAT IT HOLDS, from Mining as it was to 0.7.111, to the bit:
>   - THE GROUND (groundLimit()): what the month lifts of its resource's
>     good is what the city's ground still holds of it (dig(): LandManager
>     .extract()), nothing once it is out;
>   - THE PLAN (plan()): one order in flight; a free site of its resource
>     (the sites owned less those its buildings stand on, Game.committedOn())
>     and the resource still in the ground; of the buildings that make its
>     good and the city could staff, the best estimatedMonthlyProfit() over
>     its cost; then ground for it. To 0.7.119 the money gate was
>     Game.consider()'s alone (on the order's whole cost since 0.7.113,
>     batch I2b); since 0.7.120 (batch I3; spec-materials 2.6b) the plan
>     asks it first, through SpreadPlanner's MONEY gate (planner(): on the
>     whole cost, decision A), so a mine that would not carry its interest
>     is not the one ordered and the sector says why - consider() asks the
>     same of the order after it;
>   - THE ESTIMATE (estimatedMonthlyProfit()): the maker's (Sector's), less
>     what a mine buys abroad that is not a good, its consumables at the
>     exchange factor (SpreadPlanner.ownCosts(), since 0.7.120: the Iron
>     Mine's explosives and wear parts) - what the plan, consider() and the
>     build card read;
>   - THE RETIREMENT (retirementDemandAndCapacity()): a price-taking
>     exporter shrinks on distress only, while there is ore; past it, all
>     its plant is spare;
>   - THE PAGE (ownLines()): the deposits found and the ones being worked.
> 
> WHAT IS LEFT FOR THE CHAINS (spec-materials 2.4, 2.6b): the dig by field
> type and a building's site (BuildingsTemplate.Site: type, sites, dig) -
> LandManager.fits() gating the plan and the ground limit split by each
> type's capacity (I5) - and Forestry's crews on the cuttable forest (I9).
> Nothing here reads a site's type or dig yet: Mining's one building stands
> on ANY, one site, a tonne of field a tonne lifted.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (12), [SpreadPlanner](SpreadPlanner.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [Resource](Resource.md) (3), [Good](Good.md) (3), [LandManager](LandManager.md) (3), [Game](Game.md) (3), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [ProcessingSector](ProcessingSector.md) (1)

**Used by (2):** [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md)

## Sections

| line | section |
|---:|---|
| 80 | · the ground |
| 98 | · the estimate |
| 123 | · the dig |
| 140 | · the plan |
| 220 | · the retirement |
| 247 | · the page |

## Fields (state)

| line | field | says |
|---:|---|---|
| 64 | `private final Resource resource` | The resource whose ground it lifts. |
| 67 | `private final SpreadPlanner planner` | Its spread planner, for its money gate (0.7.120, batch I3; spec-materials 2.6b): ON_ITS_WHOLE_COST unless a check or a probe sets another. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 61 | 204 | **type** `public abstract class ExtractionSector extends Sector` | A sector that digs or cuts one resource out of the ground the city owns: the template every extraction industry of the materials chains extends (0.7.112, batch I2; runs/spec-materials.md 2.1 and 2.6b). |
| 69 | 4 | `protected ExtractionSector(String key, String label, BuildingType group, Resource resource)` |  |
| 75 | 1 | `public final Resource resource()` | The resource whose ground it lifts. |
| 78 | 1 | `public final Good groundGood()` | The good its ground limits: its resource's (Resource.good()). |

### the ground (lines 80-97)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 5 | `protected double groundLimit(Good g, double asked)` | The ground, not the mine, decides what comes up. |
| 91 | 3 | `protected double dig(LandManager land, double asked)` | What the ground gives of `asked` this month: its resource's remainder taken out (LandManager.extract()). |
| 96 | 1 | `public SpreadPlanner planner()` | Its spread planner (0.7.120): its money gate - a check or a probe sets the counterfactual on it. |

### the estimate (lines 98-122)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 3 | `public double exchangeFactor()` | What its mines' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. |
| 116 | 6 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more of its mines would clear a month (0.7.120, batch I3): the maker's estimate (Sector's - its good at home and abroad, its inputs, its wages, power, water, repairs and tax, at the rate its plant runs) less ... |

### the dig (lines 123-139)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 3 | `public double getPotentialOutput()` | What its buildings could lift this month if the ground allowed it. |
| 131 | 3 | `public int sitesOwned()` | The sites of its resource the city owns: how many of its buildings can stand. |
| 136 | 3 | `public int sitesWorked()` | ...and those its buildings stand on, standing, on site or ordered (Game.committedOn()). |

### the plan (lines 140-219)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 67 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to open another mine. |

### the retirement (lines 220-246)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 8 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter always sells what it lifts: it shrinks on distress only - WHILE THERE IS ORE. |

### the page (lines 247-264)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 14 | `public List<Line> ownLines(Game game)` |  |


# ExtractionSector.java - 207 lines · 11 methods · 0 constants · sectors

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
>     its cost; then ground for it. The money gate is Game.consider()'s, as
>     it was (on the order's whole cost since 0.7.113, batch I2b) -
>     spec-materials 2.6b brings SpreadPlanner's to extraction at batch I3,
>     so batch I2 moved no order;
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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (9), [Resource](Resource.md) (3), [Good](Good.md) (3), [LandManager](LandManager.md) (3), [Game](Game.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (2):** [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md)

## Sections

| line | section |
|---:|---|
| 69 | · the ground |
| 99 | · the plan |
| 163 | · the retirement |
| 190 | · the page |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `private final Resource resource` | The resource whose ground it lifts. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 155 | **type** `public abstract class ExtractionSector extends Sector` | A sector that digs or cuts one resource out of the ground the city owns: the template every extraction industry of the materials chains extends (0.7.112, batch I2; runs/spec-materials.md 2.1 and 2.6b). |
| 58 | 4 | `protected ExtractionSector(String key, String label, BuildingType group, Resource resource)` |  |
| 64 | 1 | `public final Resource resource()` | The resource whose ground it lifts. |
| 67 | 1 | `public final Good groundGood()` | The good its ground limits: its resource's (Resource.good()). |

### the ground (lines 69-98)

| line | len | member | says |
|---:|---:|---|---|
| 73 | 5 | `protected double groundLimit(Good g, double asked)` | The ground, not the mine, decides what comes up. |
| 80 | 3 | `protected double dig(LandManager land, double asked)` | What the ground gives of `asked` this month: its resource's remainder taken out (LandManager.extract()). |
| 85 | 3 | `public double getPotentialOutput()` | What its buildings could lift this month if the ground allowed it. |
| 90 | 3 | `public int sitesOwned()` | The sites of its resource the city owns: how many of its buildings can stand. |
| 95 | 3 | `public int sitesWorked()` | ...and those its buildings stand on, standing, on site or ordered (Game.committedOn()). |

### the plan (lines 99-162)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 51 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to open another mine. |

### the retirement (lines 163-189)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 8 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter always sells what it lifts: it shrinks on distress only - WHILE THERE IS ORE. |

### the page (lines 190-207)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 14 | `public List<Line> ownLines(Game game)` |  |


# HeavyIndustry.java - 105 lines · 6 methods · 0 constants · sectors

`ham/citybuildersim/sectors/HeavyIndustry.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The mills. Buy iron - local ore first, imported scrap for the rest - and
> sell steel abroad, because nothing in the city buys steel.
> 
> Nothing here about the books, the ore market, the scrap fallback or the
> export: IRON is importable so the shortfall comes from the world at the
> scrap price, STEEL is exportable and has no local buyer so every tonne
> leaves at the export price. The one thing a mill knows that a factory
> does not is when to be built: only into ore that exists.
> 
> Exists mainly so the city has somewhere to put people - see the old
> HeavyIndustryHandler's note, which still holds: the return on a steel
> mill is the city that grows around it.

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (8), [Game](Game.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Mining](Mining.md) (1)

**Used by (1):** [Sectors](Sectors.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 82 | **type** `public final class HeavyIndustry extends Sector` | The mills. |
| 26 | 8 | `public HeavyIndustry()` |  |
| 36 | 3 | `public double getOreDemand()` | Tonnes of iron the mills want this month, at the rate they are running. |
| 41 | 3 | `public double getScrapPricePerTonne()` | What a tonne of imported scrap costs - the mills' fallback, and the ore market's ceiling. |
| 46 | 8 | `public double getConversionMargin()` | What a tonne of steel fetches over what the iron in it cost. |
| 65 | 36 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to build another mill. |
| 104 | 1 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter always sells what it makes: it shrinks on distress only. |


# Refining.java - 188 lines · 6 methods · 1 constants · sectors

`ham/citybuildersim/sectors/Refining.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The refinery. THE SEVENTEENTH SECTOR (0.7.62, batch K; the project's
> spec-land.md 2.7).
> 
> HEAVY INDUSTRY'S SHAPE, ON OIL. A refinery buys crude - the city's wells'
> first, the world's for the rest (CRUDE is importable, so the shortfall is
> the template's) - and makes FUEL, a thousand litres from a tonne, into its
> tanks. The city's drivers (Motoring, at 6d) and its railway (Rail.haul())
> draw on those tanks before they import, so the refiners' shelf replaces
> the fuel the city used to buy abroad, and its price is the market's.
> 
> WHEN TO BUILD ONE is the one thing it knows (plan()): only for a whole
> plant's worth of the city's own fuel not yet covered, or of crude its
> wells lift that no refinery takes - HeavyIndustry's rule - and then while
> the fuel it would make clears above what it costs to make at nameplate:
> the investors' own estimate (BusinessInvestment.estimatedMakerProfit()),
> which values what the city will take at the local price and the rest at
> the export price, with its crude at the local price for the wells' spare
> and at what an import costs landed and hauled for the rest
> (estimatedMonthlyProfit()). The interest test
> (BusinessInvestment.servicesItsOwnDebt()) holds it as it holds any plant.

**Uses:** [Good](Good.md) (17), [BusinessInvestment](BusinessInvestment.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (6), [Game](Game.md) (2), [GoodsMarket](GoodsMarket.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Formats](Formats.md) (1), [Oil](Oil.md) (1)

**Used by (3):** [BuildingDataCheck](BuildingDataCheck.md), [OilCheck](OilCheck.md), [Sectors](Sectors.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `Refining.LITRES_PER_TONNE` | `1000` | Litres of fuel a tonne of crude makes: 86% of a barrel's 1,165 litres as transport fuels, at 7.33 barrels a tonne, rounded to the refinery's own figures (8,300 t into 8,300,000 L). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 155 | **type** `public final class Refining extends Sector` | The refinery. |
| 39 | 8 | `public Refining()` |  |
| 49 | 3 | `public double getCrudeDemand()` | Tonnes of crude the refineries want this month, at the rate they are running. |
| 76 | 61 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to build another refinery: the best template by what the investors' estimate says it would clear a month over its cost, while that clears at all (spec-land 2.7) - and only for one of the city's own two reasons... |
| 155 | 11 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more refinery would clear a month: the investors' estimate (BusinessInvestment.estimatedMakerProfit()), with its crude at what it would actually cost - the wells' spare crude at the local price and the rest a... |
| 173 | 11 | `public double spareCrude(BuildingsTemplate t)` | The crude the city's wells could lift this month that no refinery standing or on site will take, in tonnes (never below nothing): those on site at a template's crude a litre - `t`'s, or the catalogue's first refinery'... |
| 187 | 1 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter always sells what it makes: it shrinks on distress only (HeavyIndustry's rule). |


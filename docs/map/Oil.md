# Oil.java - 143 lines · 6 methods · 0 constants · sectors

`ham/citybuildersim/sectors/Oil.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Oil wells. THE SIXTEENTH SECTOR (0.7.62, batch K; the project's
> spec-land.md 2.7).
> 
> MINING'S SHAPE, ON THE OTHER RESOURCE. The world laid oil in fields from
> the start (Resource.OIL, batch J1a) and the land office has sold the
> ground over it since J1b; this is what lifts it. A well stands on an
> unworked oil site the city owns (Game.hasDepositFor(), by its good), lifts
> 415 t of crude a month - a hundred barrels a day - and ships what it
> lifts: CRUDE is a flow good, so what the refiners do not take at the
> local price leaves at the export price the same month, which is why a
> well is worth drilling before there is a refinery. The ground limits it
> through the template's hook (groundLimit()), and the wells retire when
> the oil runs out, as the mines do when the ore does.
> 
> Everything else - the books, the credit, the market, the payroll, the
> export - is the template's.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (9), [Good](Good.md) (6), [LandManager](LandManager.md) (3), [Game](Game.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (3):** [OilCheck](OilCheck.md), [Refining](Refining.md), [Sectors](Sectors.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 32 | 112 | **type** `public final class Oil extends Sector` | Oil wells. |
| 34 | 7 | `public Oil()` |  |
| 44 | 5 | `protected double groundLimit(Good g, double asked)` | The ground, not the well, decides what comes up. |
| 51 | 3 | `public double getPotentialOutput()` | What the wells could lift this month if the ground allowed it. |
| 62 | 49 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to drill another well: one a month while an owned oil site is unworked and oil is left in the ground - Mining's rule, on oil (spec-land 2.7). |
| 118 | 8 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter sells what it lifts and shrinks on distress - while there is oil. |
| 128 | 15 | `public List<Line> ownLines(Game game)` |  |


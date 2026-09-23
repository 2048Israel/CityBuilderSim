# Mining.java - 139 lines · 6 methods · 0 constants · sectors

`ham/citybuildersim/sectors/Mining.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Iron mines. Its own sector because the ore has a price.
> 
> THE SMALLEST SECTOR CLASS THERE IS, and the shape a thousand of them
> should have: it says what it makes, and the one thing that is unlike a
> factory - the ground is the limit. Everything else - the books, the
> credit, the market, the payroll, the export of what the mills do not
> want - is the template's.
> 
> A mine ships what it lifts: the ore is not stockable (see Good.IRON), so
> what the mills will not take at the local price leaves at the export
> price the same month, which is why a mine is worth building before the
> mills exist. LandManager decides how much of what was asked for is
> actually there, and nothing once the deposit is out.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (9), [Good](Good.md) (6), [LandManager](LandManager.md) (3), [Game](Game.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (4):** [HeavyIndustry](HeavyIndustry.md), [MiningCheck](MiningCheck.md), [ReadPathCheck](ReadPathCheck.md), [Sectors](Sectors.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 111 | **type** `public final class Mining extends Sector` | Iron mines. |
| 31 | 7 | `public Mining()` |  |
| 41 | 5 | `protected double groundLimit(Good g, double asked)` | The ground, not the mine, decides what comes up. |
| 48 | 3 | `public double getPotentialOutput()` | What the mines could lift this month if the ground allowed it. |
| 62 | 36 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to open another mine. |
| 115 | 8 | `public double[] retirementDemandAndCapacity(Game game)` | A price-taking exporter always sells what it lifts: it shrinks on distress only - WHILE THERE IS ORE. |
| 125 | 14 | `public List<Line> operations(Game game)` |  |


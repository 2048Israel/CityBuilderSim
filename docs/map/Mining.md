# Mining.java - 41 lines · 2 methods · 0 constants · sectors

`ham/citybuildersim/sectors/Mining.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> Iron mines. Its own sector because the ore has a price.
> 
> THE SMALLEST SECTOR CLASS THERE IS, and the shape a thousand of them
> should have: it says what it makes, and which resource's ground it lifts -
> everything else is a template's. The books, the credit, the market, the
> payroll and the export of what the mills do not want are Sector's; the
> ground as the limit, the plan that sinks a mine on a free site, and the
> retirement once the ore is out are ExtractionSector's, since 0.7.112
> (batch I2; runs/spec-materials.md 2.1) - Mining's own code until then,
> moved there whole so the materials chains' mines and quarries share it.
> 
> A mine ships what it lifts: the ore is not stockable (see Good.IRON), so
> what the mills will not take at the local price leaves at the export
> price the same month, which is why a mine is worth building before the
> mills exist. LandManager decides how much of what was asked for is
> actually there, and nothing once the deposit is out.

**Uses:** [ExtractionSector](ExtractionSector.md) (1), [BuildingType](BuildingType.md) (1), [Resource](Resource.md) (1), [Good](Good.md) (1), [LandManager](LandManager.md) (1)

**Used by (5):** [HeavyIndustry](HeavyIndustry.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [ReadPathCheck](ReadPathCheck.md), [Sectors](Sectors.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 16 | **type** `public final class Mining extends ExtractionSector` | Iron mines. |
| 28 | 7 | `public Mining()` |  |
| 38 | 3 | `protected double dig(LandManager land, double asked)` | The iron the ground gives, with the month's tonnes kept for the land office (LandManager.extractIron()). |


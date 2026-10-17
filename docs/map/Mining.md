# Mining.java - 85 lines · 4 methods · 4 constants · sectors

`ham/citybuildersim/sectors/Mining.java` - generated 2026-10-17 by CodeMap; line numbers are as of that run.

> Iron mines - Iron Mining since 0.7.120, under the key it was saved by,
> "Mining" (Jerus's Q2, 2026-10-08: rename, keeping the saved keys). Its own
> sector because the ore has a price.
> 
> THE SMALLEST MINE THERE IS (since 0.7.131 Cement & Concrete, a works on
> ProcessingSector, is the smallest sector class of all), and the shape a
> thousand of them should have: it says what it makes, and which resource's
> ground it lifts -
> everything else is a template's. The books, the credit, the market, the
> payroll and the export of what the mills do not want are Sector's; the
> ground as the limit, the plan that sinks a mine on a free site, and the
> retirement once the ore is out are ExtractionSector's, since 0.7.112
> (batch I2; docs/studies/spec-materials.md 2.1) - Mining's own code until
> then, moved there whole so the materials chains' mines and quarries share it.
> 
> A mine ships what it lifts: the ore is not stockable (see Good.IRON), so
> what the works will not take at the local price leaves at the export
> price the same month, which is why a mine is worth building before the
> works exist. From 0.7.120 (batch I3) nothing in the city used ore - the
> arc furnaces melt SCRAP - until the integrated route (0.7.123, batch I4):
> Iron & Steel's Pelletising Plant and blast furnaces buy it now. LandManager
> decides how much of what was asked for is actually there, and nothing once
> the deposit is out.
> 
> FOUR MINES, BY TYPE AND SIZE, SINCE 0.7.124 (batch I5; Jerus's Q3 and Q19,
> 2026-10-08; docs/studies/spec-materials.md 2.4): on hematite, shipped as dug,
> the Iron Mine (one site) and the Large Open-Pit Mine (twenty of one field);
> on magnetite, ground and concentrated three tonnes of rock to one, the
> Magnetite Mine & Concentrator (one) and the Large Magnetite Complex
> (twenty). Which the sector sinks is ExtractionSector's rule: the best
> earnings on the whole cost among those that fit the fields it owns.

**Uses:** [Good](Good.md) (2), [LandManager](LandManager.md) (2), [ExtractionSector](ExtractionSector.md) (1), [BuildingType](BuildingType.md) (1), [Resource](Resource.md) (1), [Game](Game.md) (1), [BuildCard](BuildCard.md) (1), [Deposit](Deposit.md) (1)

**Used by (22):** [BuildAdvice](BuildAdvice.md), [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [ConservationCheck](ConservationCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [DenominationCheck](DenominationCheck.md), [ForestryCheck](ForestryCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [PlanCheck](PlanCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sectors](Sectors.md), [TradeCostCheck](TradeCostCheck.md), [WellCheck](WellCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `Mining.IRON_MINE` | `14` | The Iron Mine's permanent id in buildings.json (0.7.120, batch I3; spec-materials 4): what the harnesses, the test player and the build advice find it by - a name is a label, and the chains rename buildings. |
| 48 | `Mining.LARGE_OPEN_PIT` | `108` | The Large Open-Pit Mine's permanent id (0.7.124, batch I5): 500,000 t of hematite ore a month on twenty sites of one field. |
| 51 | `Mining.MAGNETITE_MINE` | `109` | The Magnetite Mine & Concentrator's (0.7.124): 20,000 t of concentrate a month from 60,000 t of rock, on one magnetite site. |
| 54 | `Mining.LARGE_MAGNETITE` | `110` | The Large Magnetite Complex's (0.7.124): 400,000 t of concentrate a month from 1.2 Mt of rock, on twenty sites of one magnetite field. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 42 | 44 | **type** `public final class Mining extends ExtractionSector` | Iron mines - Iron Mining since 0.7.120, under the key it was saved by, "Mining" (Jerus's Q2, 2026-10-08: rename, keeping the saved keys). |
| 56 | 9 | `public Mining()` |  |
| 68 | 5 | `public java.util.List<Line> ownLines(ham.citybuildersim.Game game)` | The page (0.7.142, batch I13): what is under the ground, then where its ore goes - Iron & Steel's pellets and furnaces at nameplate, or the world (BuildCard.chainLines()). |
| 76 | 3 | `protected double dig(LandManager land, double asked)` | The iron the ground gives, with the month's tonnes kept for the land office (LandManager.extractIron()). |
| 82 | 3 | `protected double dig(LandManager land, Deposit.FieldType type, double fieldTonnes, boolean more)` | ...of one type (0.7.124): its ground's, the month's tonnes of ground kept for the land office, added up across the types. |


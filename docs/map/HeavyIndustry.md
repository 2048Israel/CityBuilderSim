# HeavyIndustry.java - 278 lines · 13 methods · 9 constants · sectors

`ham/citybuildersim/sectors/HeavyIndustry.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

> The mills - Iron & Steel since 0.7.120, under the key it was saved by,
> "Heavy Industry": the arc furnaces, which melt scrap into steel, and since
> 0.7.123 the integrated route from ore and coke to steel and the yards that
> sort the city's old steel into scrap - steel sold to the city's fabricators
> or abroad, every works built by what pays.
> 
> WHY (0.7.120, batch I3; docs/studies/spec-materials.md 2.1 and 2.3, and
> Jerus's Q2, Q4 and Q6 of 2026-10-08). Until then a mill bought IRON - "iron
> ore, and the scrap that stands in for it" - local ore first and imported
> scrap for the rest, sold steel at the US band (1.284 / .847), and was built
> only into spare local ore: "no spare local ore to smelt" was its plan's gate,
> and an order the sector could not make a case for was the borrowing
> spiral the investment engine was written to end. Three of Jerus's answers
> took that apart. Scrap splits from ore (Q4): the furnaces take SCRAP, and
> IRON is ore alone, which nothing here uses until the integrated route
> (batch I4). Steel goes to the world's price (Q6: .650 / .470), at which a
> mill on imported scrap loses money exporting and earns a little replacing
> imports. And the sector is renamed Iron & Steel (Q2), its saved key kept.
> 
> SO IT IS A ProcessingSector (I2's base), and its plan is SpreadPlanner's
> like every works of the materials chains (the study's 1.2): each furnace a
> candidate earning its steel at what the city would otherwise pay or get
> for it (outValue(): the import price for as much as the city imports, the
> export price past it) less its scrap at what the city would otherwise get
> or pay for that (inValue()), less its wages, power, water, repairs, tax
> and consumables, asked the ground, the staff and the money on its whole
> cost (decision A); the best on its cost ordered, one in flight; a kind
> whose last furnace earns below nothing six months running the one the
> spare-capacity rule may sell (idle, then shed). The study's four cases
> for the two furnaces (its 2.5, at parity): the Electric Arc Furnace -0.5,
> -0.1, 0.7 and 0.2% of its cost a month, the Steel Mini-Mill -0.5, 0.1, 1.0
> and 0.4% - thin, and left as they are (its 10.2): built where the city's
> fabricators import steel and its own scrap is cheap, shed where neither
> holds. MaterialsCheck 9 holds the rows to the study.
> 
> THE INTEGRATED ROUTE AND OLD STEEL (0.7.123, batch I4;
> docs/studies/spec-materials.md 2.2, 2.7, 2.8 and 2.9, the study's 2.4 to 2.7;
> Jerus's Q4, Q6, Q7, Q8, Q19 and Q20 of 2026-10-08). Seven more rows, each a
> candidate the same way:
>   - the SCRAP YARD sorts the city's old steel (OldMetal) into scrap;
>   - the PELLETISING PLANT fires ore into pellets, which ship (nothing here
>     uses them until a DRI plant exists, the study's 2.8);
>   - the COKE OVENS bake coking coal into coke;
>   - the MINI BLAST FURNACE and the BLAST FURNACE smelt ore, coke and
>     limestone into pig iron;
>   - the OXYGEN STEEL SHOP blows pig iron, scrap and lime into steel;
>   - the LARGE MINI-MILL melts scrap, the big arc-furnace plant (Q19: two
>     sizes where real plants are big).
> Coal, limestone and lime are imported until Coal Mining (batch I6) and
> Quarrying and Cement & Concrete (I7) make them. The study's four cases for
> each (its 2.5, at parity) MaterialsCheck 11 holds: what pays on imported
> feed for export is under every hurdle (case A, at most 0.2%), and the route
> pays on the city's own feed or replacing imports (cases B to D).
> 
> CAPTIVE (ProcessingSector, the spec's 2.7): coke, pig iron and scrap go to
> its own works' need first - the ovens' coke to the furnaces, the furnaces'
> pig iron to the oxygen shop, the yards' scrap to the shop and the arc
> furnaces - and only the rest is bought or sold.
> 
> ITS OWN POWER (the spec's 2.8, Q20): the Coke Ovens make 8,000 kW against a
> ... (14 more lines in the source)

**Uses:** [Good](Good.md) (25), [OldMetal](OldMetal.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (4), [Formats](Formats.md) (2), [Trade](Trade.md) (2), [ProcessingSector](ProcessingSector.md) (1), [BuildingType](BuildingType.md) (1), [Game](Game.md) (1), [BuildCard](BuildCard.md) (1), [Sector](Sector.md) (1), [SpreadPlanner](SpreadPlanner.md) (1)

**Used by (27):** [AgricultureCheck](AgricultureCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DenominationCheck](DenominationCheck.md), [GdpCheck](GdpCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [Sectors](Sectors.md), [TradeCostCheck](TradeCostCheck.md), [VanCheck](VanCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 135 | THE OLD STEEL (0.7.123, batch I4; OldMetal, the spec's 2.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `HeavyIndustry.ARC_FURNACE` | `11` | The Electric Arc Furnace's permanent id in buildings.json, the Steel Foundry's until 0.7.120 (batch I3; spec-materials 4): what the harnesses and the test player find it by. |
| 88 | `HeavyIndustry.MINI_MILL` | `12` | The Steel Mini-Mill's permanent id in buildings.json (0.7.120). |
| 91 | `HeavyIndustry.SCRAP_YARD` | `101` | The Scrap Yard's permanent id in buildings.json (0.7.123, batch I4): the city's old steel into scrap. |
| 94 | `HeavyIndustry.PELLETISING_PLANT` | `102` | The Pelletising Plant's permanent id (0.7.123): ore into pellets. |
| 97 | `HeavyIndustry.COKE_OVENS` | `103` | The Coke Ovens' permanent id (0.7.123): coking coal into coke, and power. |
| 100 | `HeavyIndustry.MINI_BLAST_FURNACE` | `104` | The Mini Blast Furnace's permanent id (0.7.123): ore, coke and limestone into pig iron. |
| 103 | `HeavyIndustry.BLAST_FURNACE` | `105` | The Blast Furnace's permanent id (0.7.123): the big furnace, its sinter plant and its top-gas turbine. |
| 106 | `HeavyIndustry.OXYGEN_STEEL_SHOP` | `106` | The Oxygen Steel Shop's permanent id (0.7.123): pig iron, scrap and lime into steel. |
| 109 | `HeavyIndustry.LARGE_MINI_MILL` | `107` | The Large Mini-Mill's permanent id (0.7.123): the big arc-furnace plant. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 197 | **type** `public final class HeavyIndustry extends ProcessingSector` | The mills - Iron & Steel since 0.7.120, under the key it was saved by, "Heavy Industry": the arc furnaces, which melt scrap into steel, and since 0.7.123 the integrated route from ore and coke to steel and the yards t... |
| 111 | 23 | `public HeavyIndustry()` |  |

### THE OLD STEEL (0.7.123, batch I4; OldMetal, the spec's 2.9) (lines 135-278)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 3 | `private ham.citybuildersim.OldMetal pool()` | The city's old steel, or null with no city (a fixture's bare sector). |
| 145 | 3 | `public double oldSteelIntake()` | Tonnes of old steel its standing yards take a month at nameplate. |
| 150 | 4 | `public double oldSteelPerTonneOfScrap()` | ...and the tonnes of old steel a tonne of their scrap takes: their intake over their scrap at nameplate (10,500 into 10,000), 0 with no yard standing. |
| 156 | 5 | `public double oldSteelPrice()` | What its yards pay a tonne of old steel today: OldMetal.PAY_SHARE of scrap's export price (Jerus's Q8); 0 with no markets. |
| 170 | 17 | `public java.util.List<Line> ownLines(ham.citybuildersim.Game game)` | The page (0.7.142, batch I13): the city's old steel last month - what arose, what its yards bought and what went to landfill (OldMetal), as Copper Refining's page says its old copper - and the price they pay; then its... |
| 189 | 3 | `private double spareOldSteel(ham.citybuildersim.OldMetal pool)` | Old steel arisen in the pool's last month past what the yards standing take: what one more would run on. |
| 195 | 7 | `protected double groundLimit(Good g, double asked)` | Its scrap is limited by the old steel that arose (the yards make no scrap past it); every other good as Sector's. |
| 216 | 23 | `public void produceFlow(Good g)` | Makes a flow good for the month; its scrap, from the old steel its yards buy now (THE OLD STEEL): what they made at the rate, at oldSteelPerTonneOfScrap() a tonne, bought from the pool pro rata to each seller at oldSt... |
| 242 | 9 | `protected String feedRefusal(ham.citybuildersim.BuildingsTemplate t)` | A yard is fed only while old steel for FEED_GATE of one arises past what the yards standing take; every other works buys its feed on the market. |
| 254 | 6 | `protected double feedShare(ham.citybuildersim.BuildingsTemplate t)` | One more yard runs on the old steel past what the yards standing take, at most its nameplate; every other works on all of it. |
| 263 | 8 | `protected double feedShareStanding(ham.citybuildersim.BuildingsTemplate t)` | The last yard standing runs on what the others leave of the old steel that arose, at most its nameplate. |
| 274 | 4 | `protected double offMarketFeed(ham.citybuildersim.BuildingsTemplate t, double rate)` | What a yard pays a month for the old steel it takes at `rate`: its intake at the rate, at oldSteelPrice(). |


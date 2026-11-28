# HeavyIndustry.java - 370 lines · 20 methods · 15 constants · sectors

`ham/citybuildersim/sectors/HeavyIndustry.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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
>   - the PELLETISING PLANT fires ore into pellets, which ship (since R6b the
>     Hydrogen Steel Works, FE4, takes them first: HYDROGEN-REDUCED IRON below);
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
> CAPTIVE (ProcessingSector, the spec's 2.7): coke, pig iron, scrap and since
> R6b pellets go to its own works' need first - the ovens' coke to the
> furnaces, the furnaces' pig iron to the oxygen shop, the yards' scrap to
> the shop and the arc furnaces, the pelletising plants' pellets to the
> Hydrogen Steel Works - and only the rest is bought or sold.
> 
> ... (29 more lines in the source)

**Uses:** [Good](Good.md) (28), [OldMetal](OldMetal.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (5), [Formats](Formats.md) (2), [Trade](Trade.md) (2), [ProcessingSector](ProcessingSector.md) (1), [HydroSites](HydroSites.md) (1), [BuildingType](BuildingType.md) (1), [Game](Game.md) (1), [BuildCard](BuildCard.md) (1), [Sector](Sector.md) (1), [SpreadPlanner](SpreadPlanner.md) (1)

**Used by (35):** [AgricultureCheck](AgricultureCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DenominationCheck](DenominationCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentFit](EquipmentFit.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FreightViewCheck](FreightViewCheck.md), [GdpCheck](GdpCheck.md), [HaulageCheck](HaulageCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [TradeCostCheck](TradeCostCheck.md), [VanCheck](VanCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 217 | THE OLD STEEL (0.7.123, batch I4; OldMetal, the spec's 2.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 102 | `HeavyIndustry.ARC_FURNACE` | `11` | The Electric Arc Furnace's permanent id in buildings.json, the Steel Foundry's until 0.7.120 (batch I3; spec-materials 4): what the harnesses and the test player find it by. |
| 105 | `HeavyIndustry.MINI_MILL` | `12` | The Steel Mini-Mill's permanent id in buildings.json (0.7.120). |
| 108 | `HeavyIndustry.SCRAP_YARD` | `101` | The Scrap Yard's permanent id in buildings.json (0.7.123, batch I4): the city's old steel into scrap. |
| 111 | `HeavyIndustry.PELLETISING_PLANT` | `102` | The Pelletising Plant's permanent id (0.7.123): ore into pellets. |
| 114 | `HeavyIndustry.COKE_OVENS` | `103` | The Coke Ovens' permanent id (0.7.123): coking coal into coke, and power. |
| 117 | `HeavyIndustry.MINI_BLAST_FURNACE` | `104` | The Mini Blast Furnace's permanent id (0.7.123): ore, coke and limestone into pig iron. |
| 120 | `HeavyIndustry.BLAST_FURNACE` | `105` | The Blast Furnace's permanent id (0.7.123): the big furnace, its sinter plant and its top-gas turbine. |
| 123 | `HeavyIndustry.OXYGEN_STEEL_SHOP` | `106` | The Oxygen Steel Shop's permanent id (0.7.123): pig iron, scrap and lime into steel. |
| 126 | `HeavyIndustry.LARGE_MINI_MILL` | `107` | The Large Mini-Mill's permanent id (0.7.123): the big arc-furnace plant. |
| 129 | `HeavyIndustry.HYDROGEN_WORKS` | `154` | The Hydrogen Steel Works' permanent id (R6b, FE4): pellets and power into steel by hydrogen reduction, no coke - one integrated works, the game having no DRI good. |
| 132 | `HeavyIndustry.HYDROGEN_MWH_A_TONNE` | `3.48` | The grid's megawatt-hours a tonne of liquid steel the Hydrogen Steel Works draws, 3.48: the study's N9 and N10, the electrolysers' hydrogen, the shaft furnace and the arc furnace together (an estimate). |
| 135 | `HeavyIndustry.HYDROGEN_KG_A_TONNE` | `54` | The kilograms of hydrogen a tonne of iron takes by the chemistry, 54 (FE4's note, research-near-future 3). |
| 138 | `HeavyIndustry.ELECTROLYSIS_KWH_A_KG` | `55` | The kilowatt-hours an electrolysis system takes to make a kilogram of hydrogen today, 55 (61% of the fuel's energy): the US energy department's 2022 status for PEM electrolysis, its page of 29 May 2026 (research-specu... |
| 141 | `HeavyIndustry.ELECTROLYSIS_KWH_A_KG_FE10` | `46` | The figure FE10's electrolysers reach, 46 (72%): the same page's ultimate target for the whole system. |
| 144 | `HeavyIndustry.HYDROGEN_MWH_SAVED_A_TONNE` | `HYDROGEN_KG_A_TONNE *(ELECTROLYSIS_KWH_A_KG - ELECTROLYSIS_KWH_A_KG_FE10) / 1000` | The megawatt-hours a tonne of steel FE10's electrolysers save: HYDROGEN_KG_A_TONNE kilograms at ELECTROLYSIS_KWH_A_KG less ELECTROLYSIS_KWH_A_KG_FE10 each, 0.486 (calc). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private long electrolyserWorks` | The Hydrogen Steel Works that carry FE10's electrolysers (R6d): EquipmentForms sets it from the stacks' counts, in a city founded the 1900 way only (a city founded today's way never knows FE10, and it is 0 there). |
| 153 | `private double drawBeforeSaving` | The draw the buildings' templates sum to, before FE10's saving: kept so that a count set after the month's sum can take its saving off the same figure. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 99 | 272 | **type** `public final class HeavyIndustry extends ProcessingSector` | The mills - Iron & Steel since 0.7.120, under the key it was saved by, "Heavy Industry": the arc furnaces, which melt scrap into steel, and since 0.7.123 the integrated route from ore and coke to steel and the yards t... |
| 156 | 3 | `public static double unitsSavedAWorks(BuildingsTemplate t)` | The grid units a month's draw one Hydrogen Steel Works saves at nameplate with FE10's electrolysers: its steel a month at HYDROGEN_MWH_SAVED_A_TONNE, in the units the sectors are billed (HydroSites.KWH_PER_UNIT). |
| 161 | 5 | `public void setElectrolyserWorks(long n)` | Sets how many Hydrogen Steel Works carry FE10's electrolysers (never below 0) and takes their saving off the month's draw. |
| 167 | 1 | `public long getElectrolyserWorks()` |  |
| 170 | 3 | `public double getElectrolyserUnitsSaved()` | The grid units the fitted works save at nameplate: 0 with none fitted. |
| 175 | 4 | `public double getGridUnitsSaved()` | What the works with FE10's electrolysers save the GRID at nameplate: getElectrolyserUnitsSaved() less the part of the sector's draw its steam form turns on site (1 - getSteamShare()), the draw the sector is billed on ... |
| 185 | 4 | `public void setElectricityConsumption(double kw)` | The sector's draw at nameplate: the buildings' (EconomyManager.setElectricityConsumption()) less what the works with FE10's electrolysers save, kept at 0 or more. |
| 190 | 26 | `public HeavyIndustry()` |  |

### THE OLD STEEL (0.7.123, batch I4; OldMetal, the spec's 2.9) (lines 217-370)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 3 | `private ham.citybuildersim.OldMetal pool()` | The city's old steel, or null with no city (a fixture's bare sector). |
| 227 | 3 | `public double oldSteelIntake()` | Tonnes of old steel its standing yards take a month at nameplate. |
| 237 | 3 | `public double oldSteelOnSite()` | ...and what its yards on site will take once they open (BA1, 2026-11-02): the yard feed gate counted the yards standing only while a sector could have one order on site, and with no cap on orders a second yard would b... |
| 242 | 4 | `public double oldSteelPerTonneOfScrap()` | ...and the tonnes of old steel a tonne of their scrap takes: their intake over their scrap at nameplate (10,500 into 10,000), 0 with no yard standing. |
| 248 | 5 | `public double oldSteelPrice()` | What its yards pay a tonne of old steel today: OldMetal.PAY_SHARE of scrap's export price (Jerus's Q8); 0 with no markets. |
| 262 | 17 | `public java.util.List<Line> ownLines(ham.citybuildersim.Game game)` | The page (0.7.142, batch I13): the city's old steel last month - what arose, what its yards bought and what went to landfill (OldMetal), as Copper Refining's page says its old copper - and the price they pay; then its... |
| 281 | 3 | `private double spareOldSteel(ham.citybuildersim.OldMetal pool)` | Old steel arisen in the pool's last month past what the yards standing and on site take: what one more would run on. |
| 287 | 7 | `protected double groundLimit(Good g, double asked)` | Its scrap is limited by the old steel that arose (the yards make no scrap past it); every other good as Sector's. |
| 308 | 23 | `public void produceFlow(Good g)` | Makes a flow good for the month; its scrap, from the old steel its yards buy now (THE OLD STEEL): what they made at the rate, at oldSteelPerTonneOfScrap() a tonne, bought from the pool pro rata to each seller at oldSt... |
| 334 | 9 | `protected String feedRefusal(ham.citybuildersim.BuildingsTemplate t)` | A yard is fed only while old steel for FEED_GATE of one arises past what the yards standing and on site take; every other works buys its feed on the market. |
| 346 | 6 | `protected double feedShare(ham.citybuildersim.BuildingsTemplate t)` | One more yard runs on the old steel past what the yards standing and on site take, at most its nameplate; every other works on all of it. |
| 355 | 8 | `protected double feedShareStanding(ham.citybuildersim.BuildingsTemplate t)` | The last yard standing runs on what the others leave of the old steel that arose, at most its nameplate. |
| 366 | 4 | `protected double offMarketFeed(ham.citybuildersim.BuildingsTemplate t, double rate)` | What a yard pays a month for the old steel it takes at `rate`: its intake at the rate, at oldSteelPrice(). |


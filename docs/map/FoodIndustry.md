# FoodIndustry.java - 98 lines · 6 methods · 0 constants · sectors

`ham/citybuildersim/sectors/FoodIndustry.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The mills and the plants that feed the shops.
> 
> KEYED "Industry", which is what every save and every screen has called it,
> and it makes FOOD from both its buildings - the Textile Mill included.
> Jerus, asked whether cloth should stop being bread: "keep it all as food
> for now." When the households' basket grows a good, the mill gets one.
> 
> IT BUYS ITS RAW MATERIAL NOW (2026-09-13), which it never did before, and
> that is the whole of what changed here. For the life of this project the two
> plants made food OUT OF NOTHING: no input line, no supplier, nothing on the
> cost side but wages and the lights. It is why they ran a 72-77% operating
> margin against every other plant's 28-31%, and why the Food Processing Plant
> returned 6.2% of its build cost a month - the most profitable building in the
> game, on a raw material that was free because there was none.
> 
> Now there is one. Crops come from the tenth sector's fields if the city has
> any and from the world if it has not, and the gap between those two prices is
> what a farm next door is worth to a mill - the same sentence as a mine next
> door to a mill, and a mill next door to a fabricator, one chain over. See
> sectors.Agriculture and claude/farms.md.
> 
> Everything else is the generic template, which IS IndustrialHandler's rules
> lifted: it plans output against the shops' demand rather than flooding its
> own shed, withholds below marginal cost, dumps above the shed's line, exports
> spare nameplate when the world's price clears the power it costs, and reports
> what it wrote off when a warehouse was demolished. See Sector.produceStock()
> and offer().

**Uses:** [Good](Good.md) (8), [Sector](Sector.md) (1), [BuildingType](BuildingType.md) (1), [Game](Game.md) (1), [Agriculture](Agriculture.md) (1)

**Used by (4):** [BooksCheck](BooksCheck.md), [CreditCheck](CreditCheck.md), [Sectors](Sectors.md), [WaterCheck](WaterCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 62 | **type** `public final class FoodIndustry extends Sector` | The mills and the plants that feed the shops. |
| 39 | 24 | `public FoodIndustry()` |  |
| 65 | 3 | `public double getCropDemand()` | Tonnes of crops the mills want this month, at the rate they are running. |
| 70 | 3 | `public double getCropPrice()` | What a tonne is costing them - the number that decides whether milling pays here. |
| 79 | 3 | `public double getCropPosition()` | Where the crop price sits between a farm's export floor and the world's delivered ceiling: 0 is fields with nobody else to sell to, 1 is a city with no fields at all. |
| 84 | 4 | `public double cropShare()` | The crop bill as a share of what the mills sold. |
| 91 | 7 | `public double[] retirementDemandAndCapacity(Game game)` | The spare-capacity rule measures the mills against what the shops can serve, as it always has. |


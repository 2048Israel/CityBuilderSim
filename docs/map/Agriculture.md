# Agriculture.java - 393 lines · 12 methods · 3 constants · sectors

`ham/citybuildersim/sectors/Agriculture.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The fields, and what they cost the city in ground.
> 
> THE TENTH SECTOR (2026-09-13, Jerus's call): "lets add farms, which use land
> a lot, but are labour and energy cheap, and give the textile or whatever its
> called the goods to manufacture food, and yes that means imports also exist
> if needed."
> 
> It closes the last open end in the goods economy. Ore becomes steel becomes a
> beam that leaves; building material has a plant; a seat-month has a client.
> Food had nothing behind it at all - the mills conjured it - and that is why
> the Food Processing Plant was the most profitable building in the game. Now
> there is a field behind the loaf, and the chain is four links deep:
> 
>     ground -> crops -> food -> groceries
> 
> WHAT MAKES IT DIFFERENT FROM EVERY OTHER SECTOR is the shape of its costs.
> Labour is a tenth of what a farm sells and power is a fiftieth; what it
> spends is GROUND, and it spends more of it than anything else in the game by
> a factor of three. A Mixed Farm stands on twenty-four city blocks. The
> Fabrication Works, which was the largest thing an investor could build until
> this morning, stands on eleven and a half; a coal power station on twenty.
> 
> SO THE SECTOR IS A CLOCK, and the clock is the land price. A young city buys
> its fields at a dollar and change a square foot and farming is the best
> return on the board; a city of a hundred thousand is paying sixty dollars,
> the ground under one farm is worth more than forty farms' worth of buildings,
> and no investor will ever sink another. That is not a defect. It is what
> happened to every market garden that was ever within a day's cart of a
> growing town, and the game now says it with numbers.
> 
> WHICH IS WHY THE PLAYER GETS A DIAL. Farmland is assessed at USE value rather
> than development value almost everywhere in Canada and the United States,
> for exactly this reason - without it the tax on the ground exceeds what the
> ground can grow long before the city reaches the fence. See
> TaxPolicy.FARMLAND_RELIEF: at nothing the fields become suburbs and the city
> imports its dinner; at full relief it keeps them, and forgoes the tax on what
> by then is a good share of its whole assessment. Jerus's call, taken against
> the alternative of simply making an acre of wheat out-earn an acre of houses,
> which is the opposite of the reason cities exist.
> 
> AND A THIRD RUNG THAT ESCAPES THE CLOCK. A Greenhouse Complex grows twenty
> times as much off an acre as a field does and pays for it in electricity and
> people - which is the Netherlands, and Leamington, and every square mile of
> glass anybody ever built next to a city instead of away from one. Early it is
> an expensive way to do what a field does cheaply. Late, when the ground under
> a field costs more than the field earns in a decade, it is the only farming
> left. The sector has a late game because that building is in it.
> 
> See claude/farms.md.

**Uses:** [Good](Good.md) (11), [BusinessInvestment](BusinessInvestment.md) (6), [Game](Game.md) (4), [Formats](Formats.md) (4), [Sector](Sector.md) (2), [BuildingType](BuildingType.md) (1), [EconomyManager](EconomyManager.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (4):** [AgricultureCheck](AgricultureCheck.md), [FoodIndustry](FoodIndustry.md), [LongPlaytest](LongPlaytest.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 92 | · reading |
| 158 | · plan |
| 198 | · NOBODY BREAKS GROUND ON A FIELD WHILE SOMEBODY IS SLEEPING OUTSIDE |
| 305 | · the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 127 | `Agriculture.BAKED_KG_PER_TONNE` | `525` | Kilograms of bread and bakery goods a tonne of crops becomes. |
| 130 | `Agriculture.BAKED_KG_A_HEAD` | `5.0` | What one person eats of the city's own baking a month: 3.5kg of bread, 1.5kg of the rest. |
| 300 | `Agriculture.FIRST_FARM_UTILISATION` | `.5` | Half a farm's nameplate, a month, before the first one is sunk. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 330 | **type** `public final class Agriculture extends Sector` | The fields, and what they cost the city in ground. |
| 66 | 25 | `public Agriculture()` |  |

### reading (lines 92-157)

| line | len | member | says |
|---:|---:|---|---|
| 95 | 3 | `public double getHarvest()` | Tonnes the fields can bring in a month, at the rate they are running. |
| 113 | 5 | `public double getSelfSufficiency(Game game)` | Months of the city's BAKED eating the fields cover. |
| 133 | 3 | `public double getLandSqFt()` | Ground the sector stands on, in square feet. |
| 138 | 3 | `public double getLandValue(EconomyManager economy)` | ...and what the city would charge for it if it taxed it like anything else. |
| 147 | 4 | `public double groundShare()` | The ground bill as a share of what the fields sell - the clock, in one number. |
| 153 | 4 | `public double payrollShare()` | Wages as a share of the same, which is the number that should stay small. |

### plan (lines 158-304)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 50 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | THE GENERIC MAKER'S RULE DECIDES WHAT, AND THE GROUND DECIDES WHETHER. |
| 269 | 20 | `private double worthAtTheFloor(ham.citybuildersim.BuildingsTemplate t, BusinessInvestment plans)` | What a farm would clear in a month at the EXPORT FLOOR - the worst price the world will ever hand it - rather than at whatever crops happen to fetch the month somebody is deciding. |
| 303 | 1 | `public double firstPlantUtilisation()` |  |

### the screen (lines 305-393)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 58 | `public List<Sector.Line> operations(Game game)` |  |
| 375 | 18 | `public double[] retirementDemandAndCapacity(Game game)` | A price taker with a silo: it holds a harvest, sells what the mills want and ships the rest, and shrinks only on distress. |


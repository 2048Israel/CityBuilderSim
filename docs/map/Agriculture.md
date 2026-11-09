# Agriculture.java - 539 lines · 22 methods · 5 constants · sectors

`ham/citybuildersim/sectors/Agriculture.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (22), [BusinessInvestment](BusinessInvestment.md) (8), [Game](Game.md) (4), [Formats](Formats.md) (4), [Sector](Sector.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingType](BuildingType.md) (1), [EconomyManager](EconomyManager.md) (1), [LandManager](LandManager.md) (1)

**Used by (9):** [AgricultureCheck](AgricultureCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentForms](EquipmentForms.md), [FittingCheck](FittingCheck.md), [FoodIndustry](FoodIndustry.md), [Forms1900Check](Forms1900Check.md), [LongPlaytest](LongPlaytest.md), [Sectors](Sectors.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 98 | · reading |
| 141 | THE HORSE FORM (R0c, 2026-11-05; research-1900 section 4, [P2.47]) |
| 261 | · plan |
| 301 | · NOBODY BREAKS GROUND ON A FIELD WHILE SOMEBODY IS SLEEPING OUTSIDE |
| 429 | · the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 136 | `Agriculture.BAKED_KG_PER_TONNE` | `525` | Kilograms of bread and bakery goods a tonne of crops becomes. |
| 139 | `Agriculture.BAKED_KG_A_HEAD` | `5.0` | What one person eats of the city's own baking a month: 3.5kg of bread, 1.5kg of the rest. |
| 163 | `Agriculture.HORSE_FEED_SHARE` | `0.22` | The share of a horse farm's crop its horses eat: 22% of US cropland fed farm horses and mules (research-1900 section 4, [P2.47]); Canada's 1.58 million horses (1901) at about 3 acres each agree (est.). |
| 166 | `Agriculture.HORSE_POSTS_FACTOR` | `1 /(1 - 0.273)` | A horse farm's posts against a tractor farm's: the tractor took 27.3% of US farm jobs, 1910-60 (PE14, [P2.47], est.). |
| 424 | `Agriculture.FIRST_FARM_UTILISATION` | `.5` | Half a farm's nameplate, a month, before the first one is sunk. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 169 | `private double horseShare` | The share of the farms that run on horses, 0 to 1: 0 until a harness or R1 sets it; not saved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 66 | 474 | **type** `public final class Agriculture extends Sector` | The fields, and what they cost the city in ground. |
| 68 | 29 | `public Agriculture()` |  |

### reading (lines 98-140)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 3 | `public double getHarvest()` | Tonnes the fields can bring in a month, at the rate they are running. |
| 119 | 8 | `public double getSelfSufficiency(Game game)` | Months of the city's BAKED eating the fields cover. |

### THE HORSE FORM (R0c, 2026-11-05; research-1900 section 4, [P2.47]) (lines 141-260)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 4 | `public void setHorseShare(double h)` | Sets the horse share, clamped to [0, 1] (not a number reads 0); above 0 it registers CROPS as an input, once, so a city that never sets it has the farms as no buyer of crops at all. |
| 177 | 1 | `public double getHorseShare()` |  |
| 180 | 3 | `public double getHorseFeedAtCapacity()` | The tonnes of crop the horses eat a month at nameplate: the horse share of HORSE_FEED_SHARE of what the farms can make. |
| 186 | 4 | `protected double formInput(Good g, boolean ordered)` | The horses' feed is a use of CROPS past the buildings' (Sector.formInput()), at the same figure whether or not the month's inputs are ordered for what it will make: the horses eat whatever the market does. |
| 198 | 6 | `public double getCostPerUnit(Good g)` | A tonne of crop's cost, over the tonnes the farms SELL: the horses' feed is part of what the fields make and none of what is sold, so the joint bill is spread over the harvest less the feed, h x HORSE_FEED_SHARE of it... |
| 207 | 3 | `public boolean captive(Good g)` | The farm feeds its horses from its own harvest first (Sector, CAPTIVE USE), while any run on horses. |
| 212 | 3 | `private long horsePosts(long base)` | A tier's posts on a horse farm: the buildings' times 1 + h x (HORSE_POSTS_FACTOR - 1), rounded. |
| 218 | 6 | `public long[] postsPerTier()` | The posts per tier: the buildings' own at a horse share of 0, untouched; more hands past it. |
| 227 | 7 | `public long[] postsPastBuildings()` | ...and the posts past the buildings' the city's own count must add (BuildingManager.setPostsPast()), so the labour market is offered the posts the payroll is struck on. |
| 236 | 3 | `public double getLandSqFt()` | Ground the sector stands on, in square feet. |
| 241 | 3 | `public double getLandValue(EconomyManager economy)` | ...and what the city would charge for it if it taxed it like anything else. |
| 250 | 4 | `public double groundShare()` | The ground bill as a share of what the fields sell - the clock, in one number. |
| 256 | 4 | `public double payrollShare()` | Wages as a share of the same, which is the number that should stay small. |

### plan (lines 261-428)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 71 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | THE GENERIC MAKER'S RULE DECIDES WHAT, AND THE GROUND DECIDES WHETHER. |
| 393 | 20 | `private double worthAtTheFloor(ham.citybuildersim.BuildingsTemplate t, BusinessInvestment plans)` | What a farm would clear in a month at the EXPORT FLOOR - the worst price the world will ever hand it - rather than at whatever crops happen to fetch the month somebody is deciding. |
| 427 | 1 | `public double firstPlantUtilisation()` |  |

### the screen (lines 429-539)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 58 | `public List<Sector.Line> ownLines(Game game)` |  |
| 499 | 18 | `public double[] retirementDemandAndCapacity(Game game)` | A price taker with a silo: it holds a harvest, sells what the mills want and ships the rest, and shrinks only on distress. |
| 529 | 10 | `public double unitsOf(BuildingsTemplate t)` | What one building contributes to the measure above: its harvest, each good valued at that good's export floor - one building's share of the capacity figure retirementDemandAndCapacity() adds up, in the same money. |


# InfrastructureCheck.java - 802 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/InfrastructureCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The road network, from the curve up to a city that actually jams.
> 
> Three things have to hold and none of them is obvious from the code:
> 
>   1. The response curve is right - flat until it isn't, and floored.
>   2. A NEW city is never congested. The base network exists precisely so
>      that the opening hour is not a wall, which is the mistake the land pass
>      made when the starting allocation could not fit a power plant.
>   3. Building a road actually fixes it, and the fix survives a save.
> 
> The last one is the point of the whole feature. A mechanic the player cannot
> see themselves solve is just a tax.

**Uses:** [InfrastructureManager](InfrastructureManager.md) (30), [Game](Game.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (13), [TaxPolicy](TaxPolicy.md) (3), [Traffic](Traffic.md) (3), [Retail](Retail.md) (2), [GameFiles](GameFiles.md) (2), [Markets](Markets.md) (2), [Good](Good.md) (2), [Sector](Sector.md) (2), [BuildingType](BuildingType.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 72 | · 1. the curve, on its own |
| 111 | · 2. monotonic, with no cliff |
| 127 | · 3. capacity is what you paid for |
| 144 | · 4. a real city |
| 192 | · 5. growth jams it |
| 249 | · 6. building a road fixes it |
| 381 | · · AND THE CLEANER SIGNAL UNDERNEATH IT, WHICH IS NOT CONSUMPTION. |
| 418 | · · AND THERE IS NO SECOND OUTCOME ASSERTION HERE, ON PURPOSE. |
| 456 | · 7. across a save |
| 543 | · 7b. THREE ROADS, AND ALL THREE USEFUL |
| 659 | · 8. a new game forgets the traffic |
| 669 | · 9. EVERY FIGURE THE INFRASTRUCTURE TAB READS |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 783 | **type** `public class InfrastructureCheck` | The road network, from the curve up to a city that actually jams. |
| 24 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 30 | 3 | `static boolean sane(double value, double low, double high)` | Finite, and inside the range the screen's own label claims for it. |
| 34 | 9 | `static void close(String label, double actual, double expected)` |  |
| 53 | 3 | `static boolean steady(ham.citybuildersim.sectors.Retail shops)` | True once the month in progress reads the same as the month last struck. |
| 58 | 4 | `static int shopsFor(Game g, double coverage)` | How many convenience stores give this much coverage - the fixture's shape, not a count. |
| 63 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 70 | 724 | `public static void main(String[] args) throws Exception` |  |
| 795 | 7 | `static void cleanUp(Path root)` |  |


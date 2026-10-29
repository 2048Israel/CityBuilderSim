# MiningCheck.java - 600 lines · 10 methods · 3 constants · harnesses

`ham/citybuildersim/MiningCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> Ore and scrap, from the bands they clear in to what an arc furnace makes of scrap and a mine of ore.
> 
> Until 0.7.119 the point of this harness was one number: what a Steel
> Foundry earned on imported scrap against local ore - IRON was both, its
> import price the mills' scrap and its export price the mines' ore - and
> whether a mine next door made steel "a different business, not a better
> month". Since 0.7.120 (batch I3; Jerus's Q4, 2026-10-08: "split") ore and
> scrap are two goods, the foundry is the Electric Arc Furnace on SCRAP, and
> steel is at the world's price, .650 / .470 (Q6). So the number is the
> furnace's margin on its sales at home - steel to the city's fabricators at
> what imported steel would cost - on imported scrap against the city's own
> (the study's 2.5: about 7% and 25% for the furnace, 13% and 31% for the
> Mini-Mill; the spec's 4: 5-15% and 20-35% of its sales). Section 3
> measures both furnaces directly, on a month walked by hand; section 4 asks
> whether a mine pays with every tonne shipped at the floor.

**Uses:** [Good](Good.md) (47), [JobType](JobType.md) (13), [Game](Game.md) (11), [HeavyIndustry](HeavyIndustry.md) (8), [BuildingManager](BuildingManager.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [World](World.md) (6), [GoodsMarket](GoodsMarket.md) (6), [LandParcel](LandParcel.md) (5), [Mining](Mining.md) (4), [Markets](Markets.md) (4), [Sector](Sector.md) (4), [Trade](Trade.md) (3), [Resource](Resource.md) (2), [Deposit](Deposit.md) (2), [GameFiles](GameFiles.md) (2), [PayTier](PayTier.md) (2), [Statement](Statement.md) (2), [CityLand](CityLand.md) (1), [LegacyLand](LegacyLand.md) (1), [LandMarket](LandMarket.md) (1), [TempDirs](TempDirs.md) (1), [Founding](Founding.md) (1), [BuildingType](BuildingType.md) (1), [Sectors](Sectors.md) (1)

**Used by (9):** [EarthFoundingCheck](EarthFoundingCheck.md), [LandCheck](LandCheck.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [OilCheck](OilCheck.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 164 | · 1. the band |
| 254 | · 2. a mine needs ground with ore in it |
| 288 | · THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT |
| 377 | · 3. what an arc furnace makes of scrap |
| 430 | · 4. and is the mine worth sinking? |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `MiningCheck.IRON_SEED` | `35` | A world whose founding site has an iron field near it, as the default world's had until 0.7.98 (35 sites, 449 Mt, 0.87 km out): 35, whose nearest is 36 sites, 471 Mt, 0.97 km out on dry ground, none in a new city's ce... |
| 154 | `MiningCheck.IMPORTED_SCRAP_LOW` | `.05, IMPORTED_SCRAP_HIGH =.15` | The share of its sales an arc furnace keeps on imported scrap, selling its steel at home, at least and at most (docs/studies/spec-materials.md 4, from the study's 2.5: the furnace about 7%, the Mini-Mill 13%). |
| 157 | `MiningCheck.LOCAL_SCRAP_LOW` | `.20, LOCAL_SCRAP_HIGH =.35` | ...and on the city's own scrap, at its export price (the study's 25% and 31%). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `static int fails` |  |
| 31 | `static PrintStream out` |  |
| 32 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 573 | **type** `public class MiningCheck` | Ore and scrap, from the bands they clear in to what an arc furnace makes of scrap and a mine of ore. |
| 46 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 51 | 9 | `static void close(String label, double actual, double expected)` |  |
| 61 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 81 | 27 | `static void makeRoom(Game game, double sqFt, boolean wantDeposit)` | Buys land until the city can fit what is coming, deposits included. |
| 110 | 20 | `static LandParcel towardIron(Game game)` | The offer standing nearest the iron field nearest the city's site that it does not own: buying toward it is the shortest way to a deposit (0.7.67; that field's lane pushed out until 0.7.66). |
| 138 | 14 | `static LandParcel nearestOffer(LandMarket market, long x, long y)` | The offer standing nearest plot (x, y), L-infinity from its rectangle, the lower id on a tie; null with nothing listed. |
| 159 | 379 | `public static void main(String[] args) throws Exception` |  |
| 539 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 553 | 39 | `static double[] furnaceMonth(Game city, int id, boolean ownScrap)` | One arc furnace's month, walked by hand (section 3): one standing, a market at parity, the game's founding wages (PayTier) and the city's tariffs; its steel sold at home at the landed price, its scrap bought from the ... |
| 593 | 7 | `static void cleanUp(Path root)` |  |


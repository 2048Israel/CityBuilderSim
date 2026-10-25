# ReadPathCheck.java - 2,045 lines · 12 methods · 0 constants · harnesses

`ham/citybuildersim/ReadPathCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> Reading the city must not change the city.
> 
> WHY THIS EXISTS
> 
> Three of the worst bugs this project has had were the same bug:
> 
>   printCommercialInfo()      banked a month of net income every time it ran,
>                              so opening the sector screen twice paid the
>                              shops twice
>   getIndustrialTaxIncome()   recomputed industry's month from live fields and
>                              rewrote two report figures doing it, so a
>                              reloaded city collected $0 where the live one
>                              collected $89,347
>   getStoreIncome()           assigned productsSold - uncapped by stock - on
>                              the tax path, and updateCommercialHandler() then
>                              took that quantity off the shelf
> 
> Each was found by hand, months apart, after it had already corrupted
> something. The backlog ends with a note recommending "a periodic sweep: any
> get*() on the tax or income path that assigns a field is a bug waiting for a
> save to expose it". This is that sweep, automated.
> 
> HOW IT WORKS
> 
> It does not inspect the code. It plays a real city into a state where every
> sector has money moving, fingerprints ~90 fields, then calls every read path
> the UI and the treasury use - repeatedly, in a jumbled order, the way a player
> clicking between screens would - and fingerprints again. Any field that moved
> is a getter that is not a getter, including ones that do not exist yet.
> 
> The repetition is the point. A read path that mutates ONCE and then settles
> would pass a before/after comparison; one that accumulates shows up as a field
> that drifts further the more the screens are opened.

**Uses:** [Game](Game.md) (29), [Good](Good.md) (29), [BuildAdvice](BuildAdvice.md) (27), [FundView](FundView.md) (23), [CityLand](CityLand.md) (22), [Bank](Bank.md) (18), [CityNeeds](CityNeeds.md) (16), [BuildCard](BuildCard.md) (16), [Ports](Ports.md) (16), [BuildingsTemplate](BuildingsTemplate.md) (15), [PolicyPreview](PolicyPreview.md) (14), [Resource](Resource.md) (14), [OrderBook](OrderBook.md) (12), [TrainSchedule](TrainSchedule.md) (12), [Sectors](Sectors.md) (9), [CorporateBond](CorporateBond.md) (9), [OldMetal](OldMetal.md) (8), [Sector](Sector.md) (8), [Founding](Founding.md) (8), [World](World.md) (8), [BondMarket](BondMarket.md) (7), [Retail](Retail.md) (7), [CareType](CareType.md) (7), [Refining](Refining.md) (7), [TaxPolicy](TaxPolicy.md) (6), [ChartModel](ChartModel.md) (6), [Equity](Equity.md) (5), [Debt](Debt.md) (5), [TilePainter](TilePainter.md) (5), [BusinessDebtManager](BusinessDebtManager.md) (4)... and 60 more

**Used by (1):** [LandCheck](LandCheck.md)

## Sections

| line | section |
|---:|---|
| 1491 | · a city with money moving in every sector |
| 1594 | · the FIRST read, which is the hard one |
| 1671 | · read it, and read it again |
| 1700 | · and the specific one item 7 was about |
| 1768 | · the tax the city takes is the tax it shows |
| 1785 | · a rate change reaches the treasury at once |
| 1818 | · the build advice's reads, after a load (0.7.51) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 51 | `static PrintStream out` |  |
| 52 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 48 | 1998 | **type** `public class ReadPathCheck` | Reading the city must not change the city. |
| 54 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 71 | 181 | `static void bankPrint(Game g, Map<String, Double> into)` | The bank's own fields beside NewGameCheck's (0.7.7): its price is now struck from records it keeps, and a read that struck it again would move the price and nothing in the shared snapshot. |
| 254 | 5 | `static int restingOrders(BondMarket bm)` | What rests on every bond's book. |
| 268 | 1214 | `static void readEverything(Game g)` | Everything a screen can ask the game, called the way a player browsing would call it. |
| 1483 | 346 | `public static void main(String[] args) throws Exception` |  |
| 1831 | 36 | `static Map<String, Double> adviceReads(Game g)` | The build advice's reads (0.7.51), each a figure: what readEverything() reads, and afterALoad() compares. |
| 1869 | 3 | `static long shallowOffers(LandMarket market)` | How many of a land office's offers standing hold shallow sea (ET1). |
| 1874 | 3 | `static double[] handRelief(double dry)` | A ground by relief given by hand (ET1): of `dry` km2 of dry ground, half flat, three tenths foothill, 15% steep and 5% very steep, and 0.004 km2 of shallow sea (a fiat figure: the fixture's world has no sea) - RELIEF_... |
| 1886 | 89 | `static void afterALoad(Game g, GameFiles files)` | THE BUILD ADVICE READS SAVED STATE (0.7.51): its new reads - the businesses' growth, the land office's price, the high schools' leavers (not getNewDiplomas(), this month's flow, NaN after a load), the higher schools' ... |
| 1984 | 53 | `static void onEarth(GameFiles files) throws Exception` | A CITY ON THE REAL EARTH (0.7.121, batch E4): reading its world - the founding card of its own spot, the hover's place and province, the sea's depth (the coast's distance, kept by plot), the founding's problem and rec... |
| 2038 | 7 | `static void cleanUp(Path root)` |  |


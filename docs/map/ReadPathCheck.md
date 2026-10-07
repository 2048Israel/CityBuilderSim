# ReadPathCheck.java - 1,609 lines · 9 methods · 0 constants · harnesses

`ham/citybuildersim/ReadPathCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildAdvice](BuildAdvice.md) (27), [Game](Game.md) (25), [FundView](FundView.md) (23), [Bank](Bank.md) (18), [CityNeeds](CityNeeds.md) (16), [Good](Good.md) (15), [PolicyPreview](PolicyPreview.md) (11), [OrderBook](OrderBook.md) (10), [Sectors](Sectors.md) (8), [CorporateBond](CorporateBond.md) (7), [Sector](Sector.md) (7), [CareType](CareType.md) (7), [Retail](Retail.md) (6), [ChartModel](ChartModel.md) (6), [BuildCard](BuildCard.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [TaxPolicy](TaxPolicy.md) (5), [Resource](Resource.md) (5), [TilePainter](TilePainter.md) (5), [BusinessDebtManager](BusinessDebtManager.md) (4), [Equity](Equity.md) (4), [FundLedger](FundLedger.md) (4), [BondMarket](BondMarket.md) (4), [EducationType](EducationType.md) (4), [Founding](Founding.md) (4), [World](World.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Mortgage](Mortgage.md) (3), [TreasuryFund](TreasuryFund.md) (3), [ForeignAccounts](ForeignAccounts.md) (3)... and 36 more

## Sections

| line | section |
|---:|---|
| 1210 | · a city with money moving in every sector |
| 1291 | · the FIRST read, which is the hard one |
| 1368 | · read it, and read it again |
| 1397 | · and the specific one item 7 was about |
| 1465 | · the tax the city takes is the tax it shows |
| 1482 | · a rate change reaches the treasury at once |
| 1515 | · the build advice's reads, after a load (0.7.51) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 1565 | **type** `public class ReadPathCheck` | Reading the city must not change the city. |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 56 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 68 | 162 | `static void bankPrint(Game g, Map<String, Double> into)` | The bank's own fields beside NewGameCheck's (0.7.7): its price is now struck from records it keeps, and a read that struck it again would move the price and nothing in the shared snapshot. |
| 232 | 5 | `static int restingOrders(BondMarket bm)` | What rests on every bond's book. |
| 246 | 955 | `static void readEverything(Game g)` | Everything a screen can ask the game, called the way a player browsing would call it. |
| 1202 | 322 | `public static void main(String[] args) throws Exception` |  |
| 1526 | 36 | `static Map<String, Double> adviceReads(Game g)` | The build advice's reads (0.7.51), each a figure: what readEverything() reads, and afterALoad() compares. |
| 1571 | 30 | `static void afterALoad(Game g, GameFiles files)` | THE BUILD ADVICE READS SAVED STATE (0.7.51): its new reads - the businesses' growth, the land office's price, the high schools' leavers (not getNewDiplomas(), this month's flow, NaN after a load), the higher schools' ... |
| 1602 | 7 | `static void cleanUp(Path root)` |  |


# ReadPathCheck.java - 1,384 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/ReadPathCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [FundView](FundView.md) (23), [Bank](Bank.md) (18), [Game](Game.md) (17), [Good](Good.md) (13), [OrderBook](OrderBook.md) (10), [Sectors](Sectors.md) (8), [CorporateBond](CorporateBond.md) (7), [PolicyPreview](PolicyPreview.md) (7), [Sector](Sector.md) (6), [BuildCard](BuildCard.md) (6), [Retail](Retail.md) (5), [TaxPolicy](TaxPolicy.md) (5), [ChartModel](ChartModel.md) (5), [BusinessDebtManager](BusinessDebtManager.md) (4), [Equity](Equity.md) (4), [FundLedger](FundLedger.md) (4), [BondMarket](BondMarket.md) (4), [Founding](Founding.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Mortgage](Mortgage.md) (3), [TreasuryFund](TreasuryFund.md) (3), [ForeignAccounts](ForeignAccounts.md) (3), [Debt](Debt.md) (3), [DecisionLog](DecisionLog.md) (3), [BuildAdvice](BuildAdvice.md) (3), [EconomyManager](EconomyManager.md) (2), [CareType](CareType.md) (2), [EducationType](EducationType.md) (2), [GoodsMarket](GoodsMarket.md) (2), [HistorySave](HistorySave.md) (2)... and 25 more

## Sections

| line | section |
|---:|---|
| 1066 | · a city with money moving in every sector |
| 1147 | · the FIRST read, which is the hard one |
| 1224 | · read it, and read it again |
| 1253 | · and the specific one item 7 was about |
| 1321 | · the tax the city takes is the tax it shows |
| 1338 | · a rate change reaches the treasury at once |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 1340 | **type** `public class ReadPathCheck` | Reading the city must not change the city. |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 56 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 68 | 158 | `static void bankPrint(Game g, Map<String, Double> into)` | The bank's own fields beside NewGameCheck's (0.7.7): its price is now struck from records it keeps, and a read that struck it again would move the price and nothing in the shared snapshot. |
| 228 | 5 | `static int restingOrders(BondMarket bm)` | What rests on every bond's book. |
| 242 | 815 | `static void readEverything(Game g)` | Everything a screen can ask the game, called the way a player browsing would call it. |
| 1058 | 318 | `public static void main(String[] args) throws Exception` |  |
| 1377 | 7 | `static void cleanUp(Path root)` |  |


# ReadPathCheck.java - 1,001 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/ReadPathCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Bank](Bank.md) (13), [Good](Good.md) (11), [OrderBook](OrderBook.md) (10), [Game](Game.md) (8), [CorporateBond](CorporateBond.md) (6), [Sector](Sector.md) (6), [Retail](Retail.md) (5), [Sectors](Sectors.md) (5), [BusinessDebtManager](BusinessDebtManager.md) (4), [BondMarket](BondMarket.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Mortgage](Mortgage.md) (3), [Founding](Founding.md) (3), [EconomyManager](EconomyManager.md) (2), [CareType](CareType.md) (2), [TaxPolicy](TaxPolicy.md) (2), [EducationType](EducationType.md) (2), [Equity](Equity.md) (2), [WorldEconomy](WorldEconomy.md) (2), [GameFiles](GameFiles.md) (2), [NewGameCheck](NewGameCheck.md) (2), [RealEstate](RealEstate.md) (1), [Mining](Mining.md) (1), [ServicesManager](ServicesManager.md) (1), [Exchange](Exchange.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Household](Household.md) (1), [HistorySave](HistorySave.md) (1), [DebtManager](DebtManager.md) (1), [LongTermBond](LongTermBond.md) (1)... and 5 more

## Sections

| line | section |
|---:|---|
| 714 | · a city with money moving in every sector |
| 773 | · the FIRST read, which is the hard one |
| 850 | · read it, and read it again |
| 879 | · and the specific one item 7 was about |
| 947 | · the tax the city takes is the tax it shows |
| 964 | · a rate change reaches the treasury at once |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 957 | **type** `public class ReadPathCheck` | Reading the city must not change the city. |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 56 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 68 | 105 | `static void bankPrint(Game g, Map<String, Double> into)` | The bank's own fields beside NewGameCheck's (0.7.7): its price is now struck from records it keeps, and a read that struck it again would move the price and nothing in the shared snapshot. |
| 175 | 5 | `static int restingOrders(BondMarket bm)` | What rests on every bond's book. |
| 189 | 516 | `static void readEverything(Game g)` | Everything a screen can ask the game, called the way a player browsing would call it. |
| 706 | 287 | `public static void main(String[] args) throws Exception` |  |
| 994 | 7 | `static void cleanUp(Path root)` |  |


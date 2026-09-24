# ReadPathCheck.java - 757 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/ReadPathCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (11), [Bank](Bank.md) (9), [Sector](Sector.md) (6), [Game](Game.md) (5), [Retail](Retail.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [EconomyManager](EconomyManager.md) (2), [CareType](CareType.md) (2), [TaxPolicy](TaxPolicy.md) (2), [EducationType](EducationType.md) (2), [Equity](Equity.md) (2), [Exchange](Exchange.md) (2), [GameFiles](GameFiles.md) (2), [NewGameCheck](NewGameCheck.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (1), [RealEstate](RealEstate.md) (1), [Mining](Mining.md) (1), [ServicesManager](ServicesManager.md) (1), [Sectors](Sectors.md) (1), [HistorySave](HistorySave.md) (1), [DebtManager](DebtManager.md) (1), [LongTermBond](LongTermBond.md) (1), [Debt](Debt.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingManager](BuildingManager.md) (1), [LandManager](LandManager.md) (1)

## Sections

| line | section |
|---:|---|
| 490 | · a city with money moving in every sector |
| 533 | · the FIRST read, which is the hard one |
| 606 | · read it, and read it again |
| 635 | · and the specific one item 7 was about |
| 703 | · the tax the city takes is the tax it shows |
| 720 | · a rate change reaches the treasury at once |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 713 | **type** `public class ReadPathCheck` | Reading the city must not change the city. |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 56 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 68 | 40 | `static void bankPrint(Game g, Map<String, Double> into)` | The bank's own fields beside NewGameCheck's (0.7.7): its price is now struck from records it keeps, and a read that struck it again would move the price and nothing in the shared snapshot. |
| 117 | 364 | `static void readEverything(Game g)` | Everything a screen can ask the game, called the way a player browsing would call it. |
| 482 | 267 | `public static void main(String[] args) throws Exception` |  |
| 750 | 7 | `static void cleanUp(Path root)` |  |


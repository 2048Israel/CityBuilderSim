# ForeignCheck.java - 2,086 lines · 17 methods · 1 constants · harnesses

`ham/citybuildersim/ForeignCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> The balance of payments, and whether the boundary it is drawn on is honest.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
> MoneyAudit has always tracked every flow across the city's edge. What it could
> not do was tell a HOUSEHOLD from a FOREIGNER - both sat outside the audited
> pools, for entirely different reasons - and the balance of payments is exactly
> the foreign half of that list.
> 
> So the question this file exists for is not "do the numbers look plausible".
> It is:
> 
>   1. Is the split EXHAUSTIVE? Every dollar the audit says crossed the edge has
>      to be either domestic or foreign, and never both. A flow added to
>      MoneyAudit and forgotten here would silently leave the balance of
>      payments understating the city's trade, and nothing else in the game
>      would notice.
> 
>   2. Is the STOCK the sum of the FLOWS? The cumulative balance is an
>      accumulation, and an accumulation that has drifted from what it
>      accumulated is two sets of books wearing one name. (It was "the
>      reserve" when this was written. The vault, split from it since, is
>      bought and sold rather than accumulated: sections 5b and 9 to 13.)
> 
>   3. Does it survive a reload?
> 
>   4. And - the whole promise of phase one - does the city behave EXACTLY as it
>      did before any of this went in?
> 
> And since 2026-09-21, sections 9 to 12: is the vault kept in the money it
> actually is? Dollars that stay dollars when the currency moves, a local
> value that moves with it, a revaluation that is not a flow, a reform that
> cannot reach them, and an older save whose vault comes back at the rate it
> was saved at. Section 13: does the vault defend the currency without
> holding it down - and, since XR1, does its sale lift the D$ at its real
> size? Section 14 (0.7.6): is converting cash for land no intervention on
> the rate, where buying the same dollars for the vault is one (XR1)?
> And since 0.7.35, for the Trade tab: does the month's trade read good by
> good off the businesses' books foot to the balance of payments, every
> month of section 2's city; and section 15, do the six moves the tab
> draws come to the move the reprice makes and do its readers change
> nothing, does a loaded city say its month is not counted yet, and do the
> verdicts and the cover a purchase buys stand where their constants say?
> 
> And what causes each premise (FS10), where a played city was left to reach it: section 2 reads its trade in the months its
> bakery, shops and drivers' fuel bill stand, and its payroll against the trade balance in every year its ordered town
> stands; section 5's jam is the roadless build's, read in every month the shops sell, its restock bound the model's own
> Retail.STORE_COVER_MONTHS; section 8's twins buy what the model's own quotes priced and sell the bakeries' food from the
> month the first stands, and the border prices at the two rates are read on the goods markets at one month.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (102), [Game](Game.md) (22), [Sectors](Sectors.md) (17), [Retail](Retail.md) (9), [GameFiles](GameFiles.md) (8), [TempDirs](TempDirs.md) (7), [Good](Good.md) (7), [MoneyAudit](MoneyAudit.md) (6), [CurrencyCheck](CurrencyCheck.md) (6), [Founding](Founding.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [WorldMarket](WorldMarket.md) (4), [Landmasses](Landmasses.md) (4), [Sector](Sector.md) (2), [GoodsMarket](GoodsMarket.md) (2), [BuildingManager](BuildingManager.md) (1), [CentralBank](CentralBank.md) (1), [Consumption](Consumption.md) (1)

**Used by (7):** [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [PassThroughCheck](PassThroughCheck.md), [RealDollarCheck](RealDollarCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 119 | · 1. the rate is pinned |
| 130 | · 2. a city that trades |
| 397 | · 3. across a reload |
| 469 | · 4. nothing behaves differently |
| 501 | · 5. every unit that leaves the shelf is paid for |
| 638 | · 5b. reserves you sell are reserves you no longer have |
| 737 | · 6. the rate is bounded, and moves the right way |
| 826 | · 7. and it comes home |
| 869 | · 8. a devaluation improves the current account |
| 1137 | · 9. the vault is held in dollars |
| 1211 | · 10. and the move is not money anybody moved |
| 1251 | · 11. a reform does not reach the dollars |
| 1280 | · 12. an older save |
| 1387 | · 13. the vault defends, it does not hold down |
| 1489 | 14. land bought by conversion is no intervention |
| 1736 | · ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN |
| 1931 | 15. what the Trade tab reads (0.7.35) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `ForeignCheck.TRADED_BEFORE` | `24` | The months a fixture trades before its test month: 24, the currency's settling months until XR1, kept so the fixtures are the cities they always were. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 61 | `static PrintStream out` |  |
| 62 | `static PrintStream quiet` |  |
| 65 | `static double scrappedPlantBuiltWith` | What the last devaluationCity() built from its own scrapped plant, in dollars at the landed price. |
| 68 | `static double programmeQuotedImports` | What the model quoted the last devaluationCity()'s orders to import from the world, each quoted as it was placed, in local money at the rate the city was pinned at: the programme it must buy. |
| 71 | `static int bakeryFirstMonth` | The first month the last devaluationCity() began with an Industrial Bakery standing (-1 if none ever did), and whether food crossed the border outward in that month. |
| 72 | `static boolean bakerySoldFirstMonth` |  |
| 1911 | `static double lastCash` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 2029 | **type** `public class ForeignCheck` | The balance of payments, and whether the boundary it is drawn on is honest. |
| 77 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 82 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 99 | 1023 | `public static void main(String[] args) throws Exception` |  |
| 1135 | 204 | `static void vaultInDollars() throws Exception` | Sections 9 to 12: the vault kept in dollars (2026-09-21). |
| 1341 | 3 | `static double lift(ForeignAccounts fx)` | What a sale of what the defence fetched lifts the D$ by, in logs (ForeignAccounts, THE FLOAT, step 7): INTERVENTION_IMPACT x the local money over a year of the GDP the model weighs it against. |
| 1346 | 8 | `static ForeignAccounts deepVaultCity(double exports, double imports, double gdp)` | A city that has traded its months, with a vault twice the cover that earns the most absorption - its purchase the fixture's set-up, not a purchase under test, so the rate forgets it (forgetDealing()) as it forgets a f... |
| 1385 | 103 | `static void reserveDefends()` | Section 13: the vault damps a fall and nothing else (2026-09-21). |

### 14. land bought by conversion is no intervention (lines 1489-1930)

| line | len | member | says |
|---:|---:|---|---|
| 1509 | 89 | `static void landByConversion()` |  |
| 1600 | 5 | `static ForeignAccounts copyOf(ForeignAccounts fx)` | A copy of the accounts as they stand: a save and a restore, so a month can be tried without moving the original. |
| 1607 | 6 | `static MoneyAudit.Result intervention(double soldIn, double boughtOut)` | A month whose only foreign flow is the treasury working its own vault. |
| 1620 | 6 | `static MoneyAudit.Result month(double exports, double imports)` | A synthetic month, which is the only honest way to test the rate rule. |
| 1632 | 229 | `static Game devaluationCity(Path dir, double rate, double[] food) throws Exception` | The same city twice, differing only in what its currency is worth. |
| 1863 | 26 | `static double worldFoodPrice(double rate) throws Exception` | What a foreign basket costs in local money at a given rate. |
| 1891 | 19 | `static double[][] borderPrices(double rate) throws Exception` | The border prices of every shelf good at a pinned rate, read at the month the rate is struck: [0] what the world pays for one (exportPrice()), [1] what one costs landed (importPrice()), in local money, NaN where the w... |
| 1914 | 16 | `static long run(Path dir) throws Exception` | One deterministic city, played the same way twice. |

### 15. what the Trade tab reads (0.7.35) (lines 1931-2086)

| line | len | member | says |
|---:|---:|---|---|
| 1946 | 140 | `static void whatTheTradeTabReads()` |  |


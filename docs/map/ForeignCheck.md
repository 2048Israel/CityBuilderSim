# ForeignCheck.java - 1,698 lines · 13 methods · 0 constants · harnesses

`ham/citybuildersim/ForeignCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> holding it down - and can a screen show the push without rewriting the
> month's record of it? Section 14 (0.7.6): does converting cash for land
> push the rate exactly as buying the same dollars for the vault would?
> And since 0.7.35, for the Trade tab: does the month's trade read good by
> good off the businesses' books foot to the balance of payments, every
> month of section 2's city; and section 15, do the push and the pull the
> tab draws come to the move the reprice makes, does a loaded city say its
> month is not counted yet, and do the verdicts and the cover a purchase
> buys stand where their constants say?

**Uses:** [ForeignAccounts](ForeignAccounts.md) (98), [Game](Game.md) (19), [Sectors](Sectors.md) (13), [GameFiles](GameFiles.md) (7), [MoneyAudit](MoneyAudit.md) (6), [Good](Good.md) (6), [Founding](Founding.md) (5), [Retail](Retail.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [CentralBank](CentralBank.md) (1), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Consumption](Consumption.md) (1)

**Used by (1):** [CurrencyCheck](CurrencyCheck.md)

## Sections

| line | section |
|---:|---|
| 89 | · 1. the rate is pinned |
| 100 | · 2. a city that trades |
| 319 | · 3. across a reload |
| 375 | · 4. nothing behaves differently |
| 407 | · 5. every unit that leaves the shelf is paid for |
| 523 | · 5b. reserves you sell are reserves you no longer have |
| 622 | · 6. the rate is bounded, and moves the right way |
| 700 | · 7. and it comes home |
| 737 | · 8. a devaluation improves the current account |
| 945 | · 9. the vault is held in dollars |
| 1019 | · 10. and the move is not money anybody moved |
| 1057 | · 11. a reform does not reach the dollars |
| 1086 | · 12. an older save |
| 1170 | · 13. the vault defends, it does not hold down |
| 1263 | 14. land bought by conversion pushes as reserves would |
| 1465 | · ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN |
| 1591 | 15. what the Trade tab reads (0.7.35) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 56 | `static PrintStream out` |  |
| 57 | `static PrintStream quiet` |  |
| 60 | `static double scrappedPlantBuiltWith` | What the last devaluationCity() built from its own scrapped plant, in dollars at the landed price. |
| 1571 | `static double lastCash` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 1646 | **type** `public class ForeignCheck` | The balance of payments, and whether the boundary it is drawn on is honest. |
| 62 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 67 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 77 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 84 | 846 | `public static void main(String[] args) throws Exception` |  |
| 943 | 202 | `static void vaultInDollars() throws Exception` | Sections 9 to 12: the vault kept in dollars (2026-09-21). |
| 1168 | 94 | `static void reserveDefends()` | Section 13: the vault damps a fall and nothing else (2026-09-21). |

### 14. land bought by conversion pushes as reserves would (lines 1263-1590)

| line | len | member | says |
|---:|---:|---|---|
| 1276 | 68 | `static void landByConversion()` |  |
| 1346 | 6 | `static MoneyAudit.Result intervention(double soldIn, double boughtOut)` | A month whose only foreign flow is the treasury working its own vault. |
| 1359 | 6 | `static MoneyAudit.Result month(double exports, double imports)` | A synthetic month, which is the only honest way to test the rate rule. |
| 1371 | 171 | `static Game devaluationCity(Path dir, double rate, double[] food) throws Exception` | The same city twice, differing only in what its currency is worth. |
| 1544 | 26 | `static double worldFoodPrice(double rate) throws Exception` | What a foreign basket costs in local money at a given rate. |
| 1574 | 16 | `static long run(Path dir) throws Exception` | One deterministic city, played the same way twice. |

### 15. what the Trade tab reads (0.7.35) (lines 1591-1698)

| line | len | member | says |
|---:|---:|---|---|
| 1599 | 99 | `static void whatTheTradeTabReads()` |  |


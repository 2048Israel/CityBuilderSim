# ForeignCheck.java - 1,647 lines · 13 methods · 0 constants · harnesses

`ham/citybuildersim/ForeignCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [ForeignAccounts](ForeignAccounts.md) (96), [Game](Game.md) (19), [Sectors](Sectors.md) (11), [GameFiles](GameFiles.md) (7), [MoneyAudit](MoneyAudit.md) (6), [Founding](Founding.md) (5), [Good](Good.md) (5), [Retail](Retail.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [CentralBank](CentralBank.md) (1), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Consumption](Consumption.md) (1)

**Used by (1):** [CurrencyCheck](CurrencyCheck.md)

## Sections

| line | section |
|---:|---|
| 89 | · 1. the rate is pinned |
| 100 | · 2. a city that trades |
| 305 | · 3. across a reload |
| 361 | · 4. nothing behaves differently |
| 393 | · 5. every unit that leaves the shelf is paid for |
| 509 | · 5b. reserves you sell are reserves you no longer have |
| 608 | · 6. the rate is bounded, and moves the right way |
| 686 | · 7. and it comes home |
| 723 | · 8. a devaluation improves the current account |
| 931 | · 9. the vault is held in dollars |
| 1005 | · 10. and the move is not money anybody moved |
| 1043 | · 11. a reform does not reach the dollars |
| 1072 | · 12. an older save |
| 1156 | · 13. the vault defends, it does not hold down |
| 1249 | 14. land bought by conversion pushes as reserves would |
| 1451 | · ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN |
| 1577 | 15. what the Trade tab reads (0.7.35) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 56 | `static PrintStream out` |  |
| 57 | `static PrintStream quiet` |  |
| 60 | `static double scrappedPlantBuiltWith` | What the last devaluationCity() built from its own scrapped plant, in dollars at the landed price. |
| 1557 | `static double lastCash` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 1595 | **type** `public class ForeignCheck` | The balance of payments, and whether the boundary it is drawn on is honest. |
| 62 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 67 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 77 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 84 | 832 | `public static void main(String[] args) throws Exception` |  |
| 929 | 202 | `static void vaultInDollars() throws Exception` | Sections 9 to 12: the vault kept in dollars (2026-09-21). |
| 1154 | 94 | `static void reserveDefends()` | Section 13: the vault damps a fall and nothing else (2026-09-21). |

### 14. land bought by conversion pushes as reserves would (lines 1249-1576)

| line | len | member | says |
|---:|---:|---|---|
| 1262 | 68 | `static void landByConversion()` |  |
| 1332 | 6 | `static MoneyAudit.Result intervention(double soldIn, double boughtOut)` | A month whose only foreign flow is the treasury working its own vault. |
| 1345 | 6 | `static MoneyAudit.Result month(double exports, double imports)` | A synthetic month, which is the only honest way to test the rate rule. |
| 1357 | 171 | `static Game devaluationCity(Path dir, double rate, double[] food) throws Exception` | The same city twice, differing only in what its currency is worth. |
| 1530 | 26 | `static double worldFoodPrice(double rate) throws Exception` | What a foreign basket costs in local money at a given rate. |
| 1560 | 16 | `static int run(Path dir) throws Exception` | One deterministic city, played the same way twice. |

### 15. what the Trade tab reads (0.7.35) (lines 1577-1647)

| line | len | member | says |
|---:|---:|---|---|
| 1585 | 62 | `static void whatTheTradeTabReads()` |  |


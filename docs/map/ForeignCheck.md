# ForeignCheck.java - 1,328 lines · 11 methods · 0 constants · harnesses

`ham/citybuildersim/ForeignCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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
> holding it down?

**Uses:** [ForeignAccounts](ForeignAccounts.md) (55), [Game](Game.md) (18), [Sectors](Sectors.md) (10), [GameFiles](GameFiles.md) (7), [MoneyAudit](MoneyAudit.md) (6), [Retail](Retail.md) (4), [Good](Good.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Consumption](Consumption.md) (1)

## Sections

| line | section |
|---:|---|
| 78 | · 1. the rate is pinned |
| 89 | · 2. a city that trades |
| 221 | · 3. across a reload |
| 277 | · 4. nothing behaves differently |
| 309 | · 5. every unit that leaves the shelf is paid for |
| 421 | · 5b. reserves you sell are reserves you no longer have |
| 520 | · 6. the rate is bounded, and moves the right way |
| 598 | · 7. and it comes home |
| 635 | · 8. a devaluation improves the current account |
| 822 | · 9. the vault is held in dollars |
| 896 | · 10. and the move is not money anybody moved |
| 934 | · 11. a reform does not reach the dollars |
| 963 | · 12. an older save |
| 1037 | · 13. the vault defends, it does not hold down |
| 1217 | · ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |
| 1313 | `static double lastCash` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 1284 | **type** `public class ForeignCheck` | The balance of payments, and whether the boundary it is drawn on is honest. |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 56 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 66 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 73 | 734 | `public static void main(String[] args) throws Exception` |  |
| 820 | 202 | `static void vaultInDollars() throws Exception` | Sections 9 to 12: the vault kept in dollars (2026-09-21). |
| 1035 | 69 | `static void reserveDefends()` | Section 13: the vault damps a fall and nothing else (2026-09-21). |
| 1106 | 6 | `static MoneyAudit.Result intervention(double soldIn, double boughtOut)` | A month whose only foreign flow is the treasury working its own vault. |
| 1119 | 6 | `static MoneyAudit.Result month(double exports, double imports)` | A synthetic month, which is the only honest way to test the rate rule. |
| 1131 | 153 | `static Game devaluationCity(Path dir, double rate, double[] food) throws Exception` | The same city twice, differing only in what its currency is worth. |
| 1286 | 26 | `static double worldFoodPrice(double rate) throws Exception` | What a foreign basket costs in local money at a given rate. |
| 1316 | 12 | `static int run(Path dir) throws Exception` | One deterministic city, played the same way twice. |


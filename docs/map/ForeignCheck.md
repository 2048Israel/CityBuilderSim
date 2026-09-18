# ForeignCheck.java - 990 lines · 9 methods · 0 constants · harnesses

`ham/citybuildersim/ForeignCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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
>   2. Is the STOCK the sum of the FLOWS? The reserve is an accumulation, and an
>      accumulation that has drifted from what it accumulated is two sets of
>      books wearing one name.
> 
>   3. Does it survive a reload?
> 
>   4. And - the whole promise of phase one - does the city behave EXACTLY as it
>      did before any of this went in?

**Uses:** [ForeignAccounts](ForeignAccounts.md) (25), [Game](Game.md) (14), [Sectors](Sectors.md) (10), [GameFiles](GameFiles.md) (6), [MoneyAudit](MoneyAudit.md) (6), [Retail](Retail.md) (4), [Good](Good.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [Consumption](Consumption.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | · 1. the rate is pinned |
| 80 | · 2. a city that trades |
| 199 | · 3. across a reload |
| 253 | · 4. nothing behaves differently |
| 285 | · 5. every unit that leaves the shelf is paid for |
| 397 | · 5b. reserves you sell are reserves you no longer have |
| 496 | · 6. the rate is bounded, and moves the right way |
| 574 | · 7. and it comes home |
| 611 | · 8. a devaluation improves the current account |
| 879 | · ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |
| 975 | `static double lastCash` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 955 | **type** `public class ForeignCheck` | The balance of payments, and whether the boundary it is drawn on is honest. |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 57 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 64 | 716 | `public static void main(String[] args) throws Exception` |  |
| 782 | 6 | `static MoneyAudit.Result intervention(double soldIn, double boughtOut)` | A month whose only foreign flow is the treasury working its own vault. |
| 795 | 6 | `static MoneyAudit.Result month(double exports, double imports)` | A synthetic month, which is the only honest way to test the rate rule. |
| 807 | 139 | `static Game devaluationCity(Path dir, double rate, double[] food) throws Exception` | The same city twice, differing only in what its currency is worth. |
| 948 | 26 | `static double worldFoodPrice(double rate) throws Exception` | What a foreign basket costs in local money at a given rate. |
| 978 | 12 | `static int run(Path dir) throws Exception` | One deterministic city, played the same way twice. |


# SupplierCreditCheck.java - 340 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/SupplierCreditCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit
> when the till cannot pay for it, owed for a month, repaid out of the sale it
> stocked - on both sides' books, through the money audit to the cent.
> 
> WHY. Since 0.7.12 a firm restocks only as far as its cash and its credit
> reach, and since 0.7.43 the shops sell what is on their shelf and no more;
> a grocer whose till ran dry bought nothing, sold nothing the next month and
> stayed dry, and 11.9% of the autopilot's months after month 240 delivered
> under 5% of what was wanted (runs/diag-0743.md, section 5). SupplierCredit
> is the design; EconomyManager.purchaseBudget() opens it, Sector.bookPurchase()
> and closePurchases() strike it, EconomyManager.settleSupplierCredit() moves
> the money at the strike, and MoneyAudit declares the world's share.
> 
> WHAT THIS HAS TO PROVE
> 
>   1. The rule alone: a till that covers the stock takes no credit; one that
>      cannot takes the part it cannot cover, never more than the limit nor
>      than the covered goods' bill - the fleet is not covered - shared over
>      the suppliers by what each filled; nothing is noted outside a
>      clearing; the strike makes what was bought owed and what was owed
>      repaid; it saves, restores and moves with a reform.
>   2. In a city, a grocer whose till and lender cover none of its stock -
>      the trap - restocks on its suppliers' credit, all of it on credit and
>      no more than they would wait for: a month of the baskets it expects to
>      sell, at what they cost to bring in.
>   3. At the strike it owes that, on its balance sheet; each local supplier
>      is owed its share on its own; every sector's cash flow still adds up,
>      the world's share crosses the audit as a financial inflow to the cent,
>      and the lender reads both sides as if the credit were not there.
>   4. The stock sold, and the strike after repays what was owed out of the
>      takings of that sale, which were more than it; the suppliers are paid;
>      the world's repayment crosses the audit as a financial outflow to the
>      cent; the audit closes every month.
>   5. A grocer whose till covers its stock takes none.
>   6. Saved and loaded, what it owes and what it bought on credit come back.

**Uses:** [SupplierCredit](SupplierCredit.md) (16), [Good](Good.md) (13), [Trade](Trade.md) (12), [Sectors](Sectors.md) (8), [Game](Game.md) (7), [BondCheck](BondCheck.md) (6), [Retail](Retail.md) (4), [GameFiles](GameFiles.md) (3), [MoneyAudit](MoneyAudit.md) (3), [Sector](Sector.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [SectorBooksCheck](SectorBooksCheck.md) (2), [BalanceSheet](BalanceSheet.md) (2), [TempDirs](TempDirs.md) (1), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1), [EconomyManager](EconomyManager.md) (1), [SectorBooks](SectorBooks.md) (1)

## Sections

| line | section |
|---:|---|
| 82 | 1. THE RULE ALONE |
| 150 | 2-6. IN A CITY |
| 253 | · · 3. AT THE STRIKE |
| 299 | · · 6. SAVED AND LOADED |
| 314 | · · 4. REPAID |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `SupplierCreditCheck.FARM` | `Sectors.AGRICULTURE, LORRIES = Sectors.AUTOMOTIVE` | The two local suppliers the rule's fixtures name: the farms, which fill the shelf's meat, and the car makers, which fill the fleet's vans. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `static int fails` |  |
| 50 | `static PrintStream out` |  |
| 51 | `static PrintStream quiet` |  |
| 173 | `static int closedMonths, playedMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 294 | **type** `public class SupplierCreditCheck` | The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit when the till cannot pay for it, owed for a month, repaid out of the sale it stocked - on both sides' books, through the money audit to the ... |
| 53 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 58 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 64 | 5 | `static void quietly(Runnable r)` |  |
| 70 | 11 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE ALONE (lines 82-149)

| line | len | member | says |
|---:|---:|---|---|
| 88 | 9 | `static SupplierCredit clearing(double limit, double till)` | One clearing on a fresh ledger covering meat and grains: 200 of meat from the farms, 50 of grains from the world, 30 of vans. |
| 98 | 51 | `static void theRule()` |  |

### 2-6. IN A CITY (lines 150-340)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 19 | `static Game city(Path root, String name)` | A city with households, shops, a bank and the food plants: founded, built, played two years. |
| 175 | 8 | `static MoneyAudit.Result play(Game g)` |  |
| 185 | 8 | `static double shelfBought(ham.citybuildersim.sectors.Retail shops)` | What the shops bought of the shelf's goods in the month just played, in money. |
| 194 | 146 | `static void inACity(Path root) throws Exception` |  |


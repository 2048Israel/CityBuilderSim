# SupplierCreditCheck.java - 402 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/SupplierCreditCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
> 
> FIXTURES (FS9). Items 2-6 run in a town stood at once (addStack, no construction, farms
> among it so there is a supplier in the city) and played only until the shops expect to sell
> (Retail.expectedBaskets() > 0, at most WARM_AT_MOST months, a fixture that fails when the
> bound passes): a sale and a running shop are what the suppliers' limit is read from, and
> neither has a door to set. Each month's order is then set by hand between months: the
> shelf emptied (setStoreInventory) and the till set (setSectorCash) - covering, in the month
> of item 5, and nought in the trap's; the lender's room shut by a claim of 1.2 times its
> assets and its quarter (issueLoan, recordStatement), no month played between them; the shelf
> made up to the baskets the credit was lent for before the strike that repays it, once the
> stock is asserted to have reached it, so the sale the credit is repaid out of sells what it
> was lent for. WHY: the town played two years moved under the sizes and the sale these were
> read off, and a month of the stood town's lopsided delivery sells a third of what was lent for.

**Uses:** [SupplierCredit](SupplierCredit.md) (16), [Good](Good.md) (13), [Trade](Trade.md) (12), [Sectors](Sectors.md) (8), [Game](Game.md) (7), [BondCheck](BondCheck.md) (6), [Retail](Retail.md) (5), [GameFiles](GameFiles.md) (3), [MoneyAudit](MoneyAudit.md) (3), [Founding](Founding.md) (2), [Sector](Sector.md) (2), [BusinessDebtManager](BusinessDebtManager.md) (2), [SectorStatementCheck](SectorStatementCheck.md) (2), [BalanceSheet](BalanceSheet.md) (2), [TempDirs](TempDirs.md) (1), [BuildingManager](BuildingManager.md) (1), [EconomyManager](EconomyManager.md) (1), [SectorBooks](SectorBooks.md) (1)

## Sections

| line | section |
|---:|---|
| 95 | 1. THE RULE ALONE |
| 163 | 2-6. IN A CITY |
| 315 | · · 3. AT THE STRIKE |
| 361 | · · 6. SAVED AND LOADED |
| 376 | · · 4. REPAID |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `SupplierCreditCheck.FARM` | `Sectors.AGRICULTURE, LORRIES = Sectors.AUTOMOTIVE` | The two local suppliers the rule's fixtures name: the farms, which fill the shelf's meat, and the car makers, which fill the fleet's vans. |
| 190 | `SupplierCreditCheck.WARM_AT_MOST` | `12` | The most months the town is played for its shops to expect a sale: a bound, not a count - the fixture fails when it passes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 63 | `static PrintStream out` |  |
| 64 | `static PrintStream quiet` |  |
| 192 | `static int closedMonths, playedMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 343 | **type** `public class SupplierCreditCheck` | The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit when the till cannot pay for it, owed for a month, repaid out of the sale it stocked - on both sides' books, through the money audit to the ... |
| 66 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 71 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 77 | 5 | `static void quietly(Runnable r)` |  |
| 83 | 11 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE ALONE (lines 95-162)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 9 | `static SupplierCredit clearing(double limit, double till)` | One clearing on a fresh ledger covering meat and grains: 200 of meat from the farms, 50 of grains from the world, 30 of vans. |
| 111 | 51 | `static void theRule()` |  |

### 2-6. IN A CITY (lines 163-402)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 19 | `static Game city(Path root, String name)` | A town with households, shops, a bank and the food plants: founded and stood at once, no month played (FS9; it was played two years, BondCheck's, without its second branch). |
| 194 | 8 | `static MoneyAudit.Result play(Game g)` |  |
| 204 | 8 | `static double shelfBought(ham.citybuildersim.sectors.Retail shops)` | What the shops bought of the shelf's goods in the month just played, in money. |
| 213 | 189 | `static void inACity(Path root) throws Exception` |  |


# BankFailureCheck.java - 475 lines · 15 methods · 2 constants · harnesses

`ham/citybuildersim/BankFailureCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The hands-off bank's failures, caused separately and counted across Random cities.
> WHY. BF1 follows LD2's sawmill loans: a warehouse with no buyers, an empty
> borrower still owing its plant, growth credit after losses, and an apparent
> disappearance of shares which must be distinguished from a consolidation.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (28), [Sectors](Sectors.md) (24), [Good](Good.md) (21), [Game](Game.md) (19), [MoneyAudit](MoneyAudit.md) (8), [Bank](Bank.md) (8), [BondMarket](BondMarket.md) (7), [OrderBook](OrderBook.md) (6), [Sector](Sector.md) (5), [GameFiles](GameFiles.md) (4), [WoodProducts](WoodProducts.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Investor](Investor.md) (4), [BusinessLoan](BusinessLoan.md) (4), [Equity](Equity.md) (4), [Founding](Founding.md) (3), [Nations](Nations.md) (3), [TreasuryFund](TreasuryFund.md) (3), [Exchange](Exchange.md) (3), [CorporateBond](CorporateBond.md) (3), [BuildingManager](BuildingManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessDebt](BusinessDebt.md) (2), [PriceLevel](PriceLevel.md) (2), [EconomyManager](EconomyManager.md) (2), [ExchangeCheck](ExchangeCheck.md) (2), [Markets](Markets.md) (1), [InterimLoan](InterimLoan.md) (1), [Mortgage](Mortgage.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `BankFailureCheck.out` | `System.out` |  |
| 17 | `BankFailureCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 461 | **type** `public final class BankFailureCheck` | The hands-off bank's failures, caused separately and counted across Random cities. |
| 20 | 4 | `static void check(String label, boolean ok)` |  |
| 25 | 3 | `static void close(String label, double a, double b)` |  |
| 29 | 6 | `static Game town(String label)` |  |
| 36 | 32 | `static void mill()` |  |
| 69 | 40 | `static void closing() throws Exception` |  |
| 110 | 4 | `static void refineryClosing() throws Exception` |  |
| 115 | 72 | `static void refineryClosing(boolean maturesWhilePending) throws Exception` |  |
| 188 | 15 | `static void writtenOffRetirement()` |  |
| 204 | 137 | `static void credit() throws Exception` |  |
| 342 | 4 | `static double issueAfterSettlement(BusinessDebtManager claims, double room, int month)` |  |
| 347 | 29 | `static void shares()` |  |
| 377 | 34 | `static void rounding()` |  |
| 412 | 17 | `static void bondDust() throws Exception` |  |
| 430 | 35 | `static void seeds()` |  |
| 466 | 9 | `public static void main(String[] args) throws Exception` |  |


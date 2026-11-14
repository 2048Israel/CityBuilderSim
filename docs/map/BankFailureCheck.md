# BankFailureCheck.java - 282 lines · 11 methods · 2 constants · harnesses

`ham/citybuildersim/BankFailureCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The hands-off bank's failures, caused separately and counted across Random cities.
> WHY. BF1 follows LD2's sawmill loans: a warehouse with no buyers, an empty
> borrower still owing its plant, growth credit after losses, and an apparent
> disappearance of shares which must be distinguished from a consolidation.

**Uses:** [Good](Good.md) (16), [BusinessDebtManager](BusinessDebtManager.md) (14), [Sectors](Sectors.md) (13), [Game](Game.md) (12), [OrderBook](OrderBook.md) (6), [MoneyAudit](MoneyAudit.md) (5), [Sector](Sector.md) (5), [WoodProducts](WoodProducts.md) (4), [Equity](Equity.md) (4), [Investor](Investor.md) (3), [Bank](Bank.md) (3), [Exchange](Exchange.md) (3), [BondMarket](BondMarket.md) (3), [CorporateBond](CorporateBond.md) (3), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (2), [Nations](Nations.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [BusinessLoan](BusinessLoan.md) (2), [ExchangeCheck](ExchangeCheck.md) (2), [Markets](Markets.md) (1), [InterimLoan](InterimLoan.md) (1), [Mortgage](Mortgage.md) (1), [PriceLevel](PriceLevel.md) (1)

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
| 15 | 268 | **type** `public final class BankFailureCheck` | The hands-off bank's failures, caused separately and counted across Random cities. |
| 20 | 4 | `static void check(String label, boolean ok)` |  |
| 25 | 3 | `static void close(String label, double a, double b)` |  |
| 29 | 6 | `static Game town(String label)` |  |
| 36 | 32 | `static void mill()` |  |
| 69 | 40 | `static void closing() throws Exception` |  |
| 110 | 43 | `static void credit() throws Exception` |  |
| 154 | 29 | `static void shares()` |  |
| 184 | 34 | `static void rounding()` |  |
| 219 | 17 | `static void bondDust() throws Exception` |  |
| 237 | 35 | `static void seeds()` |  |
| 273 | 9 | `public static void main(String[] args) throws Exception` |  |


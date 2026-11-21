# BankFailureCheck.java - 613 lines · 20 methods · 3 constants · harnesses

`ham/citybuildersim/BankFailureCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> The hands-off bank's failures, caused separately and counted across Random cities.
> WHY. BF1 follows LD2's sawmill loans: a warehouse with no buyers, an empty
> borrower still owing its plant, growth credit after losses, and an apparent
> disappearance of shares which must be distinguished from a consolidation.
> MC2 (section 9): BF1's room to the target is none under it, so a bank in
> its band lent no growth; under the target the growth limit's share now
> is the room, over it the room to the target.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (40), [Sectors](Sectors.md) (33), [Good](Good.md) (21), [Game](Game.md) (19), [Bank](Bank.md) (19), [MoneyAudit](MoneyAudit.md) (9), [BondMarket](BondMarket.md) (7), [OrderBook](OrderBook.md) (6), [Sector](Sector.md) (5), [GameFiles](GameFiles.md) (4), [WoodProducts](WoodProducts.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Investor](Investor.md) (4), [BusinessLoan](BusinessLoan.md) (4), [Equity](Equity.md) (4), [Founding](Founding.md) (3), [Nations](Nations.md) (3), [TreasuryFund](TreasuryFund.md) (3), [Exchange](Exchange.md) (3), [CorporateBond](CorporateBond.md) (3), [BuildingManager](BuildingManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessDebt](BusinessDebt.md) (2), [PriceLevel](PriceLevel.md) (2), [EconomyManager](EconomyManager.md) (2), [Mortgage](Mortgage.md) (2), [ExchangeCheck](ExchangeCheck.md) (2), [Markets](Markets.md) (1), [InterimLoan](InterimLoan.md) (1)

## Sections

| line | section |
|---:|---|
| 345 | · 9. THE BAND'S SHARE UNDER THE LEVERAGE TARGET (MC2, 2026-11-20) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 19 | `BankFailureCheck.out` | `System.out` |  |
| 20 | `BankFailureCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 354 | `BankFailureCheck.BAND_OWED` | `1_000_000, BAND_OWNED = 100_000_000, BAND_EXPOSURE = 10_000_000` | What the landlords and the works owe before the month, what each owns, and the hand-built banks' exposure. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 596 | **type** `public final class BankFailureCheck` | The hands-off bank's failures, caused separately and counted across Random cities. |
| 23 | 4 | `static void check(String label, boolean ok)` |  |
| 28 | 3 | `static void close(String label, double a, double b)` |  |
| 32 | 6 | `static Game town(String label)` |  |
| 39 | 32 | `static void mill()` |  |
| 72 | 40 | `static void closing() throws Exception` |  |
| 113 | 4 | `static void refineryClosing() throws Exception` |  |
| 118 | 72 | `static void refineryClosing(boolean maturesWhilePending) throws Exception` |  |
| 191 | 15 | `static void writtenOffRetirement()` |  |
| 207 | 137 | `static void credit() throws Exception` |  |

### 9. THE BAND'S SHARE UNDER THE LEVERAGE TARGET (MC2, 2026-11-20) (lines 345-613)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 8 | `static Bank leverageBankAt(double x, double exposure)` | A bank of one branch whose exposure is its cash, funded from abroad: nothing weighs, so its leverage requirement is the larger, and its leverage ratio stands x of the way from LEVERAGE_RATIO_MIN to its leverage target... |
| 372 | 12 | `static BusinessDebtManager deskOn(Bank bank)` | The landlords owing an insured mortgage and the works a loan, under this bank's capital rule for the month, as Game hands it to them. |
| 386 | 3 | `static boolean mortgage(BusinessDebtManager desk, double shortfall)` | The mortgage door's answer for the landlords on this shortfall, on a building costing ten times it. |
| 390 | 3 | `static boolean near(double a, double b)` |  |
| 394 | 85 | `static void band()` |  |
| 480 | 4 | `static double issueAfterSettlement(BusinessDebtManager claims, double room, int month)` |  |
| 485 | 29 | `static void shares()` |  |
| 515 | 34 | `static void rounding()` |  |
| 550 | 17 | `static void bondDust() throws Exception` |  |
| 568 | 35 | `static void seeds()` |  |
| 604 | 9 | `public static void main(String[] args) throws Exception` |  |


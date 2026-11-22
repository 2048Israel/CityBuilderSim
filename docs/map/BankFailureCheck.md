# BankFailureCheck.java - 726 lines · 23 methods · 4 constants · harnesses

`ham/citybuildersim/BankFailureCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> The hands-off bank's failures, caused separately and counted across Random cities.
> WHY. BF1 follows LD2's sawmill loans: a warehouse with no buyers, an empty
> borrower still owing its plant, growth credit after losses, and an apparent
> disappearance of shares which must be distinguished from a consolidation.
> MC2 (section 9): BF1's room to the target is none under it, so a bank in
> its band lent no growth; under the target the growth limit's share now
> is the room, never past the room to the minimum; over it the room to the
> target, and none while a default takes it under before the settle.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (45), [Bank](Bank.md) (38), [Sectors](Sectors.md) (35), [Good](Good.md) (21), [Game](Game.md) (19), [BondMarket](BondMarket.md) (10), [MoneyAudit](MoneyAudit.md) (9), [OrderBook](OrderBook.md) (6), [Sector](Sector.md) (5), [GameFiles](GameFiles.md) (4), [WoodProducts](WoodProducts.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [Investor](Investor.md) (4), [BusinessLoan](BusinessLoan.md) (4), [EconomyManager](EconomyManager.md) (4), [Equity](Equity.md) (4), [Founding](Founding.md) (3), [Nations](Nations.md) (3), [TreasuryFund](TreasuryFund.md) (3), [BuildingManager](BuildingManager.md) (3), [Exchange](Exchange.md) (3), [CorporateBond](CorporateBond.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessDebt](BusinessDebt.md) (2), [PriceLevel](PriceLevel.md) (2), [Mortgage](Mortgage.md) (2), [ExchangeCheck](ExchangeCheck.md) (2), [Markets](Markets.md) (1), [InterimLoan](InterimLoan.md) (1)

## Sections

| line | section |
|---:|---|
| 346 | · 9. THE BAND'S SHARE UNDER THE LEVERAGE TARGET (MC2, 2026-11-20) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `BankFailureCheck.out` | `System.out` |  |
| 21 | `BankFailureCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 358 | `BankFailureCheck.BAND_OWED` | `1_000_000, BAND_OWNED = 100_000_000, BAND_EXPOSURE = 10_000_000` | What the landlords and the works owe before the month, what each owns, and the hand-built banks' exposure. |
| 544 | `BankFailureCheck.BAND_EDGE` | `1e-6` | The margin, a share of the room, by which section 9 lends either side of the room to the minimum. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 708 | **type** `public final class BankFailureCheck` | The hands-off bank's failures, caused separately and counted across Random cities. |
| 24 | 4 | `static void check(String label, boolean ok)` |  |
| 29 | 3 | `static void close(String label, double a, double b)` |  |
| 33 | 6 | `static Game town(String label)` |  |
| 40 | 32 | `static void mill()` |  |
| 73 | 40 | `static void closing() throws Exception` |  |
| 114 | 4 | `static void refineryClosing() throws Exception` |  |
| 119 | 72 | `static void refineryClosing(boolean maturesWhilePending) throws Exception` |  |
| 192 | 15 | `static void writtenOffRetirement()` |  |
| 208 | 137 | `static void credit() throws Exception` |  |

### 9. THE BAND'S SHARE UNDER THE LEVERAGE TARGET (MC2, 2026-11-20) (lines 346-726)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 8 | `static Bank leverageBankAt(double x, double exposure)` | A bank of one branch whose exposure is its cash, funded from abroad: nothing weighs, so its leverage requirement is the larger, and its leverage ratio stands x of the way from LEVERAGE_RATIO_MIN to its leverage target... |
| 376 | 12 | `static BusinessDebtManager deskOn(Bank bank)` | The landlords owing an insured mortgage and the works a loan, under this bank's capital rule for the month, as Game hands it to them. |
| 390 | 3 | `static boolean mortgage(BusinessDebtManager desk, double shortfall)` | The mortgage door's answer for the landlords on this shortfall, on a building costing ten times it. |
| 394 | 3 | `static boolean near(double a, double b)` |  |
| 398 | 144 | `static void band()` |  |
| 547 | 7 | `static Bank lentFrom(Bank bank, double lent)` | A bank with this one's equity and its exposure grown by what it lent, by its restore setters. |
| 560 | 8 | `static Bank riskBankAt(double x, double book)` | A bank whose whole exposure is a business book weighing in full, funded from abroad: its risk weights bind, and its capital ratio stands x of the way from CAPITAL_RATIO to its capital target. |
| 570 | 22 | `static double bankBuysOf(Bank bank, double face)` | What this bank buys of a works' issue of this face, its desk's allowance struck on the bank as Game strikes it after the month's defaults. |
| 593 | 4 | `static double issueAfterSettlement(BusinessDebtManager claims, double room, int month)` |  |
| 598 | 29 | `static void shares()` |  |
| 628 | 34 | `static void rounding()` |  |
| 663 | 17 | `static void bondDust() throws Exception` |  |
| 681 | 35 | `static void seeds()` |  |
| 717 | 9 | `public static void main(String[] args) throws Exception` |  |


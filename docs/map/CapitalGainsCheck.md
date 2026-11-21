# CapitalGainsCheck.java - 285 lines · 13 methods · 1 constants · harnesses

`ham/citybuildersim/CapitalGainsCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Household average-cost stocks and investment taxes, through the real fills, census and city (HT1).
> WHY: the fund already tracked average cost, but a household sale previously lost its acquisition cost.

**Uses:** [HouseholdFlowCheck](HouseholdFlowCheck.md) (10), [Household](Household.md) (9), [FamilyStructure](FamilyStructure.md) (9), [PayTier](PayTier.md) (9), [ExchangeCheck](ExchangeCheck.md) (8), [Exchange](Exchange.md) (8), [MoneyAudit](MoneyAudit.md) (6), [HouseholdBalance](HouseholdBalance.md) (6), [Game](Game.md) (6), [Equity](Equity.md) (5), [YearBook](YearBook.md) (4), [OrderBook](OrderBook.md) (3), [GameFiles](GameFiles.md) (3), [GameVersion](GameVersion.md) (3), [TaxPolicy](TaxPolicy.md) (2), [WageBand](WageBand.md) (2), [LongPlaytest](LongPlaytest.md) (2), [Bank](Bank.md) (1), [DebtManager](DebtManager.md) (1), [FundLedgerCheck](FundLedgerCheck.md) (1), [Sectors](Sectors.md) (1), [AdultStage](AdultStage.md) (1)

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 14 | `CapitalGainsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 12 | `static int fails` |  |
| 13 | `static PrintStream out` |  |
| 33 | `final HouseholdFlowCheck.Town town` |  |
| 34 | `final HouseholdBalance balance` |  |
| 35 | `final Household holder` |  |
| 36 | `final TaxPolicy policy` |  |
| 37 | `final Equity equity` |  |
| 38 | `final Exchange exchange` |  |
| 39 | `final Bank bank` |  |
| 40 | `final ExchangeCheck.Firms firms` |  |
| 41 | `final int company` |  |
| 42 | `double received` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 275 | **type** `public final class CapitalGainsCheck` | Household average-cost stocks and investment taxes, through the real fills, census and city (HT1). |
| 16 | 4 | `static void check(String label, boolean ok)` |  |
| 21 | 4 | `static void close(String label, double got, double want)` |  |
| 26 | 5 | `static void quietly(Runnable action)` |  |
| 32 | 37 | **type** `static final class Market` |  |
| 44 | 15 | `Market()` _(in CapitalGainsCheck.Market)_ |  |
| 60 | 8 | `void trade(boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 70 | 24 | `static void costAndLoss()` |  |
| 95 | 10 | `static void lossesFirst()` |  |
| 106 | 44 | `static void foundersSplitsAndDividends()` |  |
| 151 | 14 | `static void waterfall()` |  |
| 166 | 25 | `static void stockFlows()` |  |
| 192 | 24 | `static void liveCity()` |  |
| 218 | 55 | `static boolean saveChecks()` | Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. |
| 274 | 11 | `public static void main(String[] args) throws Exception` |  |


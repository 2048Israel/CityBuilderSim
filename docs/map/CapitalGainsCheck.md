# CapitalGainsCheck.java - 450 lines · 17 methods · 1 constants · harnesses

`ham/citybuildersim/CapitalGainsCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Household average-cost stocks and investment taxes, through the real fills, census and city (HT1).
> WHY: the fund already tracked average cost, but a household sale previously lost its acquisition cost.

**Uses:** [FamilyStructure](FamilyStructure.md) (17), [Exchange](Exchange.md) (15), [HouseholdFlowCheck](HouseholdFlowCheck.md) (14), [Household](Household.md) (14), [PayTier](PayTier.md) (14), [ExchangeCheck](ExchangeCheck.md) (13), [HouseholdBalance](HouseholdBalance.md) (10), [OrderBook](OrderBook.md) (10), [MoneyAudit](MoneyAudit.md) (8), [Equity](Equity.md) (8), [Game](Game.md) (6), [TaxPolicy](TaxPolicy.md) (5), [FundLedgerCheck](FundLedgerCheck.md) (4), [YearBook](YearBook.md) (4), [GameFiles](GameFiles.md) (3), [GameVersion](GameVersion.md) (3), [WageBand](WageBand.md) (2), [LongPlaytest](LongPlaytest.md) (2), [Bank](Bank.md) (1), [DebtManager](DebtManager.md) (1), [Sectors](Sectors.md) (1), [AdultStage](AdultStage.md) (1)

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
| 308 | `final HouseholdFlowCheck.Town town` |  |
| 309 | `final HouseholdBalance balance` |  |
| 310 | `final Household deceased` |  |
| 311 | `final Household heirs` |  |
| 312 | `final TaxPolicy policy` |  |
| 313 | `final Equity equity` |  |
| 314 | `double received` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 440 | **type** `public final class CapitalGainsCheck` | Household average-cost stocks and investment taxes, through the real fills, census and city (HT1). |
| 16 | 4 | `static void check(String label, boolean ok)` |  |
| 21 | 4 | `static void close(String label, double got, double want)` |  |
| 26 | 5 | `static void quietly(Runnable action)` |  |
| 32 | 38 | **type** `static final class Market` |  |
| 44 | 16 | `Market()` _(in CapitalGainsCheck.Market)_ |  |
| 61 | 8 | `void trade(boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 71 | 24 | `static void costAndLoss()` |  |
| 96 | 10 | `static void lossesFirst()` |  |
| 107 | 44 | `static void foundersSplitsAndDividends()` |  |
| 152 | 14 | `static void waterfall()` |  |
| 167 | 25 | `static void stockFlows()` |  |
| 193 | 27 | `static void liveCity()` |  |
| 222 | 83 | `static boolean saveChecks()` | Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. |
| 307 | 20 | **type** `static final class Estate` | A real census estate transfers one of ten senior households to twenty established heirs. |
| 315 | 10 | `Estate(double cash, double debt)` _(in CapitalGainsCheck.Estate)_ |  |
| 325 | 1 | `void run()` _(in CapitalGainsCheck.Estate)_ |  |
| 328 | 52 | `static void securedEstates()` |  |
| 381 | 55 | `static void securedDepartures()` |  |
| 437 | 13 | `public static void main(String[] args) throws Exception` |  |


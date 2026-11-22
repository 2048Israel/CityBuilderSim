# CapitalGainsCheck.java - 1,025 lines · 33 methods · 2 constants · harnesses

`ham/citybuildersim/CapitalGainsCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Household average-cost stocks and investment taxes, through the real fills, census and city (HT1).
> WHY: the fund already tracked average cost, but a household sale previously lost its acquisition cost.

**Uses:** [Game](Game.md) (26), [Household](Household.md) (21), [FamilyStructure](FamilyStructure.md) (19), [BondMarket](BondMarket.md) (17), [HouseholdBalance](HouseholdBalance.md) (16), [PayTier](PayTier.md) (16), [Exchange](Exchange.md) (15), [Sectors](Sectors.md) (15), [HouseholdFlowCheck](HouseholdFlowCheck.md) (14), [Equity](Equity.md) (13), [ExchangeCheck](ExchangeCheck.md) (13), [CorporateBond](CorporateBond.md) (13), [OrderBook](OrderBook.md) (12), [GameFiles](GameFiles.md) (11), [EstateCustody](EstateCustody.md) (11), [MoneyAudit](MoneyAudit.md) (10), [Debt](Debt.md) (6), [TaxPolicy](TaxPolicy.md) (5), [FundLedgerCheck](FundLedgerCheck.md) (5), [LongPlaytest](LongPlaytest.md) (4), [YearBook](YearBook.md) (4), [GameVersion](GameVersion.md) (3), [FundLedger](FundLedger.md) (3), [WageBand](WageBand.md) (2), [LongTermBond](LongTermBond.md) (2), [Bank](Bank.md) (1), [DebtManager](DebtManager.md) (1), [AdultStage](AdultStage.md) (1), [Auction](Auction.md) (1), [WorkingHousehold](WorkingHousehold.md) (1)... and 4 more

**Used by (2):** [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 13 | `CapitalGainsCheck.custodyJson` | `new com.google.gson.Gson()` |  |
| 15 | `CapitalGainsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 12 | `static int fails` |  |
| 14 | `static PrintStream out` |  |
| 34 | `final HouseholdFlowCheck.Town town` |  |
| 35 | `final HouseholdBalance balance` |  |
| 36 | `final Household holder` |  |
| 37 | `final TaxPolicy policy` |  |
| 38 | `final Equity equity` |  |
| 39 | `final Exchange exchange` |  |
| 40 | `final Bank bank` |  |
| 41 | `final ExchangeCheck.Firms firms` |  |
| 42 | `final int company` |  |
| 43 | `double received` |  |
| 383 | `final HouseholdFlowCheck.Town town` |  |
| 384 | `final HouseholdBalance balance` |  |
| 385 | `final Household deceased` |  |
| 386 | `final Household heirs` |  |
| 387 | `final TaxPolicy policy` |  |
| 388 | `final Equity equity` |  |
| 389 | `double received` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 1015 | **type** `public final class CapitalGainsCheck` | Household average-cost stocks and investment taxes, through the real fills, census and city (HT1). |
| 17 | 4 | `static void check(String label, boolean ok)` |  |
| 22 | 4 | `static void close(String label, double got, double want)` |  |
| 27 | 5 | `static void quietly(Runnable action)` |  |
| 33 | 42 | **type** `static final class Market` |  |
| 45 | 16 | `Market()` _(in CapitalGainsCheck.Market)_ |  |
| 62 | 3 | `void trade(boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 66 | 8 | `void trade(int company, boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 76 | 24 | `static void costAndLoss()` |  |
| 101 | 10 | `static void lossesFirst()` |  |
| 112 | 69 | `static void cancellationLosses()` |  |
| 182 | 44 | `static void foundersSplitsAndDividends()` |  |
| 227 | 14 | `static void waterfall()` |  |
| 242 | 25 | `static void stockFlows()` |  |
| 268 | 27 | `static void liveCity()` |  |
| 297 | 83 | `static boolean saveChecks()` | Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. |
| 382 | 20 | **type** `static final class Estate` | A real census estate transfers one of ten senior households to twenty established heirs. |
| 390 | 10 | `Estate(double cash, double debt)` _(in CapitalGainsCheck.Estate)_ |  |
| 400 | 1 | `void run()` _(in CapitalGainsCheck.Estate)_ |  |
| 403 | 52 | `static void securedEstates()` |  |
| 456 | 79 | `static void securedDepartures()` |  |
| 537 | 1 | `static Game custodyEstate(GameFiles files)` | All property enters the real Game receiver through census death, never a fabricated sale. |
| 538 | 79 | `static Game custodyEstate(GameFiles files, boolean lossCase)` |  |
| 618 | 4 | `static void invokeHouseholdSettle(Game g) throws Exception` |  |
| 622 | 1 | `static double poolTotal(Game g)` |  |
| 624 | 128 | `static void custodyContinuation(Game g, String label) throws Exception` |  |
| 754 | 3 | `static int custodySlot(String name) throws ReflectiveOperationException` | Read the owner's native slot names without copying its private index layout. |
| 758 | 3 | `static void payRecordedCoupons(BondMarket market) throws Exception` |  |
| 762 | 10 | `static double[] bondCashEdges(BondMarket b)` | The bond edges declared by MoneyAudit, including both legs of household/world trades. |
| 772 | 15 | `static void chargeFixtureCoupons(Game g)` |  |
| 787 | 6 | `static void bookFixtureBankCoupon(Game g)` |  |
| 793 | 20 | `static void bondCounterparts(Game g, String label)` |  |
| 814 | 108 | `static void custodyCouponPhases() throws Exception` | Nonempty phase snapshots: no end-holder reconstruction, no charge/payment replay. |
| 923 | 47 | `static void custodySaves() throws Exception` |  |
| 971 | 36 | `static void extinguishedBondPositions() throws Exception` |  |
| 1008 | 17 | `public static void main(String[] args) throws Exception` |  |


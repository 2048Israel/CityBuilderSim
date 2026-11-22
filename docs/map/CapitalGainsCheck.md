# CapitalGainsCheck.java - 912 lines · 30 methods · 2 constants · harnesses

`ham/citybuildersim/CapitalGainsCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Household average-cost stocks and investment taxes, through the real fills, census and city (HT1).
> WHY: the fund already tracked average cost, but a household sale previously lost its acquisition cost.

**Uses:** [Game](Game.md) (25), [Household](Household.md) (19), [FamilyStructure](FamilyStructure.md) (18), [PayTier](PayTier.md) (15), [Exchange](Exchange.md) (15), [HouseholdFlowCheck](HouseholdFlowCheck.md) (14), [Sectors](Sectors.md) (14), [HouseholdBalance](HouseholdBalance.md) (13), [ExchangeCheck](ExchangeCheck.md) (13), [Equity](Equity.md) (12), [CorporateBond](CorporateBond.md) (12), [BondMarket](BondMarket.md) (12), [EstateCustody](EstateCustody.md) (11), [MoneyAudit](MoneyAudit.md) (10), [OrderBook](OrderBook.md) (10), [GameFiles](GameFiles.md) (10), [Debt](Debt.md) (6), [TaxPolicy](TaxPolicy.md) (5), [FundLedgerCheck](FundLedgerCheck.md) (4), [LongPlaytest](LongPlaytest.md) (4), [YearBook](YearBook.md) (4), [GameVersion](GameVersion.md) (3), [FundLedger](FundLedger.md) (3), [WageBand](WageBand.md) (2), [LongTermBond](LongTermBond.md) (2), [Bank](Bank.md) (1), [DebtManager](DebtManager.md) (1), [AdultStage](AdultStage.md) (1), [Auction](Auction.md) (1), [WorkingHousehold](WorkingHousehold.md) (1)... and 3 more

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
| 309 | `final HouseholdFlowCheck.Town town` |  |
| 310 | `final HouseholdBalance balance` |  |
| 311 | `final Household deceased` |  |
| 312 | `final Household heirs` |  |
| 313 | `final TaxPolicy policy` |  |
| 314 | `final Equity equity` |  |
| 315 | `double received` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 902 | **type** `public final class CapitalGainsCheck` | Household average-cost stocks and investment taxes, through the real fills, census and city (HT1). |
| 17 | 4 | `static void check(String label, boolean ok)` |  |
| 22 | 4 | `static void close(String label, double got, double want)` |  |
| 27 | 5 | `static void quietly(Runnable action)` |  |
| 33 | 38 | **type** `static final class Market` |  |
| 45 | 16 | `Market()` _(in CapitalGainsCheck.Market)_ |  |
| 62 | 8 | `void trade(boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 72 | 24 | `static void costAndLoss()` |  |
| 97 | 10 | `static void lossesFirst()` |  |
| 108 | 44 | `static void foundersSplitsAndDividends()` |  |
| 153 | 14 | `static void waterfall()` |  |
| 168 | 25 | `static void stockFlows()` |  |
| 194 | 27 | `static void liveCity()` |  |
| 223 | 83 | `static boolean saveChecks()` | Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. |
| 308 | 20 | **type** `static final class Estate` | A real census estate transfers one of ten senior households to twenty established heirs. |
| 316 | 10 | `Estate(double cash, double debt)` _(in CapitalGainsCheck.Estate)_ |  |
| 326 | 1 | `void run()` _(in CapitalGainsCheck.Estate)_ |  |
| 329 | 52 | `static void securedEstates()` |  |
| 382 | 79 | `static void securedDepartures()` |  |
| 463 | 1 | `static Game custodyEstate(GameFiles files)` | All property enters the real Game receiver through census death, never a fabricated sale. |
| 464 | 79 | `static Game custodyEstate(GameFiles files, boolean lossCase)` |  |
| 544 | 4 | `static void invokeHouseholdSettle(Game g) throws Exception` |  |
| 548 | 1 | `static double poolTotal(Game g)` |  |
| 550 | 128 | `static void custodyContinuation(Game g, String label) throws Exception` |  |
| 680 | 3 | `static int custodySlot(String name) throws ReflectiveOperationException` | Read the owner's native slot names without copying its private index layout. |
| 684 | 3 | `static void payRecordedCoupons(BondMarket market) throws Exception` |  |
| 688 | 10 | `static double[] bondCashEdges(BondMarket b)` | The bond edges declared by MoneyAudit, including both legs of household/world trades. |
| 698 | 15 | `static void chargeFixtureCoupons(Game g)` |  |
| 713 | 6 | `static void bookFixtureBankCoupon(Game g)` |  |
| 719 | 20 | `static void bondCounterparts(Game g, String label)` |  |
| 740 | 108 | `static void custodyCouponPhases() throws Exception` | Nonempty phase snapshots: no end-holder reconstruction, no charge/payment replay. |
| 849 | 47 | `static void custodySaves() throws Exception` |  |
| 897 | 15 | `public static void main(String[] args) throws Exception` |  |


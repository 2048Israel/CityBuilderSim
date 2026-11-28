# CapitalGainsCheck.java - 1,032 lines · 34 methods · 2 constants · harnesses

`ham/citybuildersim/CapitalGainsCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Household average-cost stocks and investment taxes, through the real fills, census and city (HT1).
> WHY: the fund already tracked average cost, but a household sale previously lost its acquisition cost.

**Uses:** [Game](Game.md) (26), [Household](Household.md) (21), [FamilyStructure](FamilyStructure.md) (19), [BondMarket](BondMarket.md) (17), [HouseholdBalance](HouseholdBalance.md) (16), [PayTier](PayTier.md) (16), [Exchange](Exchange.md) (15), [Sectors](Sectors.md) (15), [HouseholdFlowCheck](HouseholdFlowCheck.md) (14), [Equity](Equity.md) (13), [ExchangeCheck](ExchangeCheck.md) (13), [CorporateBond](CorporateBond.md) (13), [OrderBook](OrderBook.md) (12), [MoneyAudit](MoneyAudit.md) (11), [GameFiles](GameFiles.md) (11), [EstateCustody](EstateCustody.md) (11), [Debt](Debt.md) (6), [TaxPolicy](TaxPolicy.md) (5), [FundLedgerCheck](FundLedgerCheck.md) (5), [LongPlaytest](LongPlaytest.md) (4), [YearBook](YearBook.md) (4), [GameVersion](GameVersion.md) (3), [FundLedger](FundLedger.md) (3), [WageBand](WageBand.md) (2), [LongTermBond](LongTermBond.md) (2), [Bank](Bank.md) (1), [DebtManager](DebtManager.md) (1), [AdultStage](AdultStage.md) (1), [Auction](Auction.md) (1), [WorkingHousehold](WorkingHousehold.md) (1)... and 4 more

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
| 39 | `final HouseholdFlowCheck.Town town` |  |
| 40 | `final HouseholdBalance balance` |  |
| 41 | `final Household holder` |  |
| 42 | `final TaxPolicy policy` |  |
| 43 | `final Equity equity` |  |
| 44 | `final Exchange exchange` |  |
| 45 | `final Bank bank` |  |
| 46 | `final ExchangeCheck.Firms firms` |  |
| 47 | `final int company` |  |
| 48 | `double received` |  |
| 388 | `final HouseholdFlowCheck.Town town` |  |
| 389 | `final HouseholdBalance balance` |  |
| 390 | `final Household deceased` |  |
| 391 | `final Household heirs` |  |
| 392 | `final TaxPolicy policy` |  |
| 393 | `final Equity equity` |  |
| 394 | `double received` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 1022 | **type** `public final class CapitalGainsCheck` | Household average-cost stocks and investment taxes, through the real fills, census and city (HT1). |
| 17 | 4 | `static void check(String label, boolean ok)` |  |
| 22 | 4 | `static void close(String label, double got, double want)` |  |
| 28 | 3 | `static void close(String label, double got, double want, double tolerance)` | RES3: ...for a difference of two large totals, whose own doubles lie farther apart than close()'s floor: the caller names the tolerance, scaled by the totals subtracted. |
| 32 | 5 | `static void quietly(Runnable action)` |  |
| 38 | 42 | **type** `static final class Market` |  |
| 50 | 16 | `Market()` _(in CapitalGainsCheck.Market)_ |  |
| 67 | 3 | `void trade(boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 71 | 8 | `void trade(int company, boolean buy, double price, double shares)` _(in CapitalGainsCheck.Market)_ |  |
| 81 | 24 | `static void costAndLoss()` |  |
| 106 | 10 | `static void lossesFirst()` |  |
| 117 | 69 | `static void cancellationLosses()` |  |
| 187 | 44 | `static void foundersSplitsAndDividends()` |  |
| 232 | 14 | `static void waterfall()` |  |
| 247 | 25 | `static void stockFlows()` |  |
| 273 | 27 | `static void liveCity()` |  |
| 302 | 83 | `static boolean saveChecks()` | Nonzero stocks, inclusion and struck flows through both actual Game load routes, in scratch slot 10 only. |
| 387 | 20 | **type** `static final class Estate` | A real census estate transfers one of ten senior households to twenty established heirs. |
| 395 | 10 | `Estate(double cash, double debt)` _(in CapitalGainsCheck.Estate)_ |  |
| 405 | 1 | `void run()` _(in CapitalGainsCheck.Estate)_ |  |
| 408 | 52 | `static void securedEstates()` |  |
| 461 | 79 | `static void securedDepartures()` |  |
| 542 | 1 | `static Game custodyEstate(GameFiles files)` | All property enters the real Game receiver through census death, never a fabricated sale. |
| 543 | 79 | `static Game custodyEstate(GameFiles files, boolean lossCase)` |  |
| 623 | 4 | `static void invokeHouseholdSettle(Game g) throws Exception` |  |
| 627 | 1 | `static double poolTotal(Game g)` |  |
| 629 | 130 | `static void custodyContinuation(Game g, String label) throws Exception` |  |
| 761 | 3 | `static int custodySlot(String name) throws ReflectiveOperationException` | Read the owner's native slot names without copying its private index layout. |
| 765 | 3 | `static void payRecordedCoupons(BondMarket market) throws Exception` |  |
| 769 | 10 | `static double[] bondCashEdges(BondMarket b)` | The bond edges declared by MoneyAudit, including both legs of household/world trades. |
| 779 | 15 | `static void chargeFixtureCoupons(Game g)` |  |
| 794 | 6 | `static void bookFixtureBankCoupon(Game g)` |  |
| 800 | 20 | `static void bondCounterparts(Game g, String label)` |  |
| 821 | 108 | `static void custodyCouponPhases() throws Exception` | Nonempty phase snapshots: no end-holder reconstruction, no charge/payment replay. |
| 930 | 47 | `static void custodySaves() throws Exception` |  |
| 978 | 36 | `static void extinguishedBondPositions() throws Exception` |  |
| 1015 | 17 | `public static void main(String[] args) throws Exception` |  |


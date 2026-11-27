# EstateCustody.java - 155 lines · 38 methods · 2 constants · model

`ham/citybuildersim/EstateCustody.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The treasury's passive owner of assets for which an estate has no heir (HT1).
> WHY: the old no-heir path sent the property abroad; it was neither owned by
> the treasury nor distinguished from the fund's trading and rescue books.

**Uses:** [FundLedger](FundLedger.md) (9), [Equity](Equity.md) (8), [DebtManager](DebtManager.md) (3), [CorporateBond](CorporateBond.md) (3), [BondMarket](BondMarket.md) (2), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (7):** [BondMarket](BondMarket.md), [CapitalGainsCheck](CapitalGainsCheck.md), [DataSave](DataSave.md), [Equity](Equity.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [NationalAccounts](NationalAccounts.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `EstateCustody.FLOW_SLOTS` | `7` | Fixed receipt/disclosure slots: household cash, issuer cash, foreign coupon, local book received, asset loss, own-debt gain, face consolidated. |
| 17 | `EstateCustody.CASH` | `0, ISSUER = 1, FOREIGN = 2, IN_KIND = 3, LOSS = 4, DEBT_GAIN = 5, PAPER_FACE = 6` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `private final double[] shares` |  |
| 19 | `private final Map<Integer, Double> bonds` |  |
| 20 | `private FundLedger ledger` |  |
| 21 | `private double usd, cars, lossCarried, pendingPaper, pendingCoupons` |  |
| 22 | `private final double[] flows` |  |
| 23 | `private boolean window` |  |
| 24 | `private Equity equity` |  |
| 25 | `private DebtManager debt` |  |
| 26 | `private BondMarket market` |  |
| 27 | `private DoubleConsumer cashSink` |  |
| 28 | `private IntSupplier month` |  |
| 128 | `double[] shares, flows, pending` |  |
| 129 | `Map<Integer, Double> bonds` |  |
| 130 | `FundLedger ledger` |  |
| 131 | `double usd, cars, lossCarried, pendingPaper, pendingCoupons` |  |
| 132 | `boolean window` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 142 | **type** `public final class EstateCustody` | The treasury's passive owner of assets for which an estate has no heir (HT1). |
| 30 | 4 | `public void attach(Equity register, DebtManager paper, BondMarket bondsMarket, DoubleConsumer sink, IntSupplier clock)` |  |
| 34 | 1 | `private void flow(int slot, double amount)` |  |
| 35 | 4 | `private void cash(int slot, double amount)` |  |
| 39 | 4 | `public void beginMonth()` |  |
| 43 | 1 | `public void closeMonth()` |  |
| 44 | 1 | `public double shares(int company)` |  |
| 45 | 1 | `public double bondFace(int id)` |  |
| 46 | 1 | `public Map<Integer, Double> bondFaces()` |  |
| 47 | 1 | `public FundLedger getLedger()` |  |
| 48 | 1 | `public double usd()` |  |
| 49 | 1 | `public double cars()` |  |
| 50 | 1 | `public double lossCarried()` |  |
| 51 | 1 | `public double pendingPaper()` |  |
| 52 | 1 | `public double pendingCoupons()` |  |
| 53 | 1 | `public double cashReceived()` |  |
| 54 | 1 | `public double cashFromHouseholds()` |  |
| 55 | 1 | `public double foreignCoupon()` |  |
| 56 | 1 | `public double noncashReceived()` |  |
| 57 | 1 | `public double assetLoss()` |  |
| 58 | 1 | `public double debtGain()` |  |
| 59 | 1 | `public double paperConsolidated()` |  |
| 60 | 1 | `public double paperFinding()` |  |
| 63 | 14 | `public void receive(double savings, double[] units, double[] basis, Map<Integer, Double> face, double dollars, double vehicles,...` | Installs actual property before the tax book consolidates the self-claim. |
| 77 | 4 | `void receiveBond(CorporateBond b, double face, double bookValue)` |  |
| 81 | 1 | `void recordCoupon(double amount)` |  |
| 82 | 4 | `void payRecordedCoupon(double amount)` |  |
| 86 | 3 | `void receiveDividend(int company, double amount)` |  |
| 89 | 5 | `void redeemBond(CorporateBond b, double face)` |  |
| 94 | 7 | `void writeDownBond(CorporateBond b, double before, double after)` |  |
| 101 | 5 | `void extinguishShares(int company)` |  |
| 106 | 1 | `void split(int company, double factor)` |  |
| 107 | 4 | `public void consolidatePaper()` |  |
| 111 | 5 | `public void payForeignCoupon(double worldRate, double localPerUsd)` |  |
| 116 | 5 | `public void reset()` |  |
| 121 | 5 | `public void redenominate(double scale)` |  |
| 127 | 14 | **type** `public static final class State` | Fixed owner snapshot; live bond IDs are the existing securities, never estate IDs. |
| 133 | 7 | `public State copy()` _(in EstateCustody.State)_ |  |
| 141 | 5 | `public State toState()` |  |
| 146 | 9 | `public void restore(State saved)` |  |


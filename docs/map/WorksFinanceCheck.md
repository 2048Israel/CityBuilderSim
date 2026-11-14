# WorksFinanceCheck.java - 215 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/WorksFinanceCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Essential funding reservations, actual parcel cash and quoted bond terms (HW1).
> WHY. A plan could count available cash twice, finance fractional parcels,
> charge a reopened bond past maturity, or reuse an unrelated borrowing rate.
> Each fixture causes that side of the rule and checks the real advice/pass.

**Uses:** [BuildAdvice](BuildAdvice.md) (25), [AutoBuilder](AutoBuilder.md) (21), [Game](Game.md) (13), [WorksValue](WorksValue.md) (6), [EssentialWorksCheck](EssentialWorksCheck.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [DebtQuote](DebtQuote.md) (3), [LandManager](LandManager.md) (3), [Debt](Debt.md) (1), [CareType](CareType.md) (1), [AdviceValueOracle](AdviceValueOracle.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Auction](Auction.md) (1), [AuctionCheck](AuctionCheck.md) (1), [TempDirs](TempDirs.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 18 | `WorksFinanceCheck.out` | `System.out` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 199 | **type** `public final class WorksFinanceCheck` | Essential funding reservations, actual parcel cash and quoted bond terms (HW1). |
| 21 | 4 | `static void check(String words, boolean condition)` |  |
| 26 | 3 | `static boolean close(double a, double b)` |  |
| 30 | 8 | `static Game flat(Path root, String name)` |  |
| 39 | 14 | `static double financing(Game g, double amount, double horizon)` |  |
| 54 | 27 | `static void parcelCash(Path root, boolean vault)` |  |
| 82 | 24 | `static void remainingCoupons(Path root)` |  |
| 107 | 33 | `static void laterQuotes(Path root)` |  |
| 141 | 13 | `static AutoBuilder.OpenAsk ask(BuildAdvice.Suggestion plan, int month, double raised)` |  |
| 155 | 48 | `static void reservationRelease(Path root)` |  |
| 204 | 11 | `public static void main(String[] args) throws Exception` |  |


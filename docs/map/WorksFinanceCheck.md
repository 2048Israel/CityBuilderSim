# WorksFinanceCheck.java - 792 lines · 22 methods · 1 constants · harnesses

`ham/citybuildersim/WorksFinanceCheck.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> Essential funding reservations, actual parcel cash and quoted bond terms (HW1).
> WHY. A plan could count available cash twice, finance fractional parcels,
> charge a reopened bond past maturity, or reuse an unrelated borrowing rate.
> Each fixture causes that side of the rule and checks the real advice/pass.

**Uses:** [BuildAdvice](BuildAdvice.md) (120), [AutoBuilder](AutoBuilder.md) (51), [EssentialWorksCheck](EssentialWorksCheck.md) (37), [Game](Game.md) (32), [BuildingsTemplate](BuildingsTemplate.md) (30), [LandManager](LandManager.md) (13), [BuildingManager](BuildingManager.md) (11), [WorksValue](WorksValue.md) (10), [InfrastructureManager](InfrastructureManager.md) (10), [BuildingsStacks](BuildingsStacks.md) (7), [ConstructionControl](ConstructionControl.md) (6), [Traffic](Traffic.md) (6), [RoadCheck](RoadCheck.md) (5), [DebtQuote](DebtQuote.md) (4), [CareType](CareType.md) (4), [AdviceValueOracle](AdviceValueOracle.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (4), [Auction](Auction.md) (4), [BuildAdviceCheck](BuildAdviceCheck.md) (4), [BuildingType](BuildingType.md) (4), [RunWorksCheck](RunWorksCheck.md) (3), [Sector](Sector.md) (3), [AuctionCheck](AuctionCheck.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Debt](Debt.md) (1), [BuildCard](BuildCard.md) (1), [AutoBuildCheck](AutoBuildCheck.md) (1), [Statement](Statement.md) (1), [TempDirs](TempDirs.md) (1)

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
| 17 | 776 | **type** `public final class WorksFinanceCheck` | Essential funding reservations, actual parcel cash and quoted bond terms (HW1). |
| 21 | 4 | `static void check(String words, boolean condition)` |  |
| 26 | 3 | `static boolean close(double a, double b)` |  |
| 30 | 8 | `static Game flat(Path root, String name)` |  |
| 39 | 14 | `static double financing(Game g, double amount, double horizon)` |  |
| 54 | 27 | `static void parcelCash(Path root, boolean vault)` |  |
| 82 | 24 | `static void remainingCoupons(Path root)` |  |
| 107 | 33 | `static void laterQuotes(Path root)` |  |
| 141 | 13 | `static AutoBuilder.OpenAsk ask(BuildAdvice.Suggestion plan, int month, double raised)` |  |
| 155 | 3 | `static void reservationRelease(Path root)` |  |
| 159 | 65 | `static void reservationRelease(Path root, boolean fullyRaised)` |  |
| 225 | 33 | `static void groundUse(String name)` |  |
| 259 | 30 | `static void independentPending(Path root)` |  |
| 290 | 4 | `static double addedPoints(Game.BuildQuote quote, BuildingsTemplate t, long count)` |  |
| 295 | 32 | `static void prospectiveQueue(Path root)` |  |
| 328 | 97 | `static void crewPromise(Path root, boolean partial) throws Exception` |  |
| 426 | 14 | `static Game reserveTown(Path root, String name)` |  |
| 441 | 64 | `static void pavingHold(Path root)` |  |
| 506 | 63 | `static void highwayPromise(Path root)` |  |
| 570 | 87 | `static void promotedDepot(Path root)` |  |
| 658 | 32 | `static void commonCalendar(Path root)` |  |
| 691 | 77 | `static void growingCalendarChoice(Path root)` |  |
| 769 | 23 | `public static void main(String[] args) throws Exception` |  |


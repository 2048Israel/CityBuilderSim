# WorksFinanceCheck.java - 965 lines · 25 methods · 1 constants · harnesses

`ham/citybuildersim/WorksFinanceCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Essential funding reservations, actual parcel cash and quoted bond terms (HW1).
> WHY. A plan could count available cash twice, finance fractional parcels,
> charge a reopened bond past maturity, or reuse an unrelated borrowing rate.
> Each fixture causes that side of the rule and checks the real advice/pass.

**Uses:** [BuildAdvice](BuildAdvice.md) (139), [AutoBuilder](AutoBuilder.md) (52), [EssentialWorksCheck](EssentialWorksCheck.md) (38), [Game](Game.md) (36), [BuildingsTemplate](BuildingsTemplate.md) (36), [LandManager](LandManager.md) (27), [WorksValue](WorksValue.md) (13), [BuildingManager](BuildingManager.md) (11), [InfrastructureManager](InfrastructureManager.md) (10), [BuildingsStacks](BuildingsStacks.md) (7), [AdviceValueOracle](AdviceValueOracle.md) (6), [LandParcel](LandParcel.md) (6), [ConstructionControl](ConstructionControl.md) (6), [Traffic](Traffic.md) (6), [CareType](CareType.md) (5), [CityLand](CityLand.md) (5), [Auction](Auction.md) (5), [RoadCheck](RoadCheck.md) (5), [DebtQuote](DebtQuote.md) (4), [UtilitiesHandler](UtilitiesHandler.md) (4), [BuildAdviceCheck](BuildAdviceCheck.md) (4), [BuildingType](BuildingType.md) (4), [RunWorksCheck](RunWorksCheck.md) (3), [Sector](Sector.md) (3), [Sectors](Sectors.md) (2), [AuctionCheck](AuctionCheck.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [Debt](Debt.md) (1), [GameFiles](GameFiles.md) (1), [Zoning](Zoning.md) (1)... and 5 more

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
| 17 | 949 | **type** `public final class WorksFinanceCheck` | Essential funding reservations, actual parcel cash and quoted bond terms (HW1). |
| 21 | 4 | `static void check(String words, boolean condition)` |  |
| 26 | 3 | `static boolean close(double a, double b)` |  |
| 30 | 8 | `static Game flat(Path root, String name)` |  |
| 39 | 14 | `static double financing(Game g, double amount, double horizon)` |  |
| 54 | 27 | `static void parcelCash(Path root, boolean vault)` |  |
| 82 | 24 | `static void remainingCoupons(Path root)` |  |
| 107 | 33 | `static void laterQuotes(Path root)` |  |
| 142 | 3 | `static void carriedGround(Path root, boolean zoned) throws Exception` | Hand-priced parcels: each has only one and a half orders of reusable ground. |
| 146 | 72 | `static void carriedGround(Path root, boolean zoned, boolean unlinkedForest) throws Exception` |  |
| 220 | 75 | `static void roundedCash(Path root, boolean borrowFirst)` | The physical calendar supplies counts; this ledger independently carries each quote's cash. |
| 296 | 13 | `static AutoBuilder.OpenAsk ask(BuildAdvice.Suggestion plan, int month, double raised)` |  |
| 310 | 3 | `static void reservationRelease(Path root)` |  |
| 314 | 65 | `static void reservationRelease(Path root, boolean fullyRaised)` |  |
| 380 | 33 | `static void groundUse(String name)` |  |
| 414 | 30 | `static void independentPending(Path root)` |  |
| 445 | 4 | `static double addedPoints(Game.BuildQuote quote, BuildingsTemplate t, long count)` |  |
| 450 | 32 | `static void prospectiveQueue(Path root)` |  |
| 483 | 97 | `static void crewPromise(Path root, boolean partial) throws Exception` |  |
| 581 | 14 | `static Game reserveTown(Path root, String name)` |  |
| 596 | 64 | `static void pavingHold(Path root)` |  |
| 661 | 63 | `static void highwayPromise(Path root)` |  |
| 725 | 87 | `static void promotedDepot(Path root)` |  |
| 813 | 32 | `static void commonCalendar(Path root)` |  |
| 846 | 77 | `static void growingCalendarChoice(Path root)` |  |
| 924 | 41 | `public static void main(String[] args) throws Exception` |  |


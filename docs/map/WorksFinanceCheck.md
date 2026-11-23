# WorksFinanceCheck.java - 1,066 lines · 28 methods · 6 constants · harnesses

`ham/citybuildersim/WorksFinanceCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Essential funding reservations, actual parcel cash and quoted bond terms (HW1).
> WHY. A plan could count available cash twice, finance fractional parcels,
> charge a reopened bond past maturity, or reuse an unrelated borrowing rate.
> Each fixture causes that side of the rule and checks the real advice/pass.
> Since FS15 every city is a founding, founded(): its works stood by buildStack,
> its people placed at the cohorts' equilibrium, two months struck for books and
> loads, then its people, trend and year of GDP set again by hand. Until then the
> sections read towns played 24 and 36 months, whose builders, growth and GDP moved
> with every change to the month.

**Uses:** [BuildAdvice](BuildAdvice.md) (139), [AutoBuilder](AutoBuilder.md) (54), [Game](Game.md) (43), [EssentialWorksCheck](EssentialWorksCheck.md) (41), [BuildingsTemplate](BuildingsTemplate.md) (36), [LandManager](LandManager.md) (27), [WorksValue](WorksValue.md) (13), [BuildingManager](BuildingManager.md) (12), [InfrastructureManager](InfrastructureManager.md) (10), [BuildingsStacks](BuildingsStacks.md) (7), [AdviceValueOracle](AdviceValueOracle.md) (6), [LandParcel](LandParcel.md) (6), [ConstructionControl](ConstructionControl.md) (6), [Traffic](Traffic.md) (6), [CareType](CareType.md) (5), [CityLand](CityLand.md) (5), [Auction](Auction.md) (5), [RoadCheck](RoadCheck.md) (5), [Sector](Sector.md) (4), [DebtQuote](DebtQuote.md) (4), [BuildingType](BuildingType.md) (4), [AgeBand](AgeBand.md) (3), [UtilitiesHandler](UtilitiesHandler.md) (3), [RunWorksCheck](RunWorksCheck.md) (3), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [Sectors](Sectors.md) (2), [AuctionCheck](AuctionCheck.md) (2), [BuildAdviceCheck](BuildAdviceCheck.md) (2)... and 10 more

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 23 | `WorksFinanceCheck.out` | `System.out` |  |
| 36 | `WorksFinanceCheck.TOWN_WORKS` | `{ { "Industrial Bakery", "4" }, { "Coal Power Plant", "1" }, { "Water Treatme...` | ChildcareCheck.town()'s works, the town once (k 1), which flat() and laterQuotes() stand on a founding (FS15). |
| 45 | `WorksFinanceCheck.SERVED_WORKS` | `{ { "Industrial Bakery", "2" }, { "Coal Power Plant", "1" }, { "Water Treatme...` | BuildAdviceCheck.servedCity()'s works, which reserveTown() and crewPromise() stand on a founding (FS15). |
| 55 | `WorksFinanceCheck.STRUCK_MONTHS` | `2` | The months founded() strikes on the state it set: the founding's first, which barely trades, and the one after. |
| 58 | `WorksFinanceCheck.GROWN_FROM` | `.75` | growingCalendarChoice()'s year: its people grew from this share of today's (FS15: .9 on the served city played two years, whose builders took longer: a founding's Paved Road opens in 7.5 months, so the city grows fast... |
| 61 | `WorksFinanceCheck.PEOPLE_PER_PLACE` | `.75` | The share of its homes' places founded()'s people fill. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 1045 | **type** `public final class WorksFinanceCheck` | Essential funding reservations, actual parcel cash and quoted bond terms (HW1). |
| 26 | 4 | `static void check(String words, boolean condition)` |  |
| 31 | 3 | `static boolean close(double a, double b)` |  |
| 72 | 31 | `static Game founded(Path root, String name, String[][] works)` | FS15: the city a section's premise is set on, caused. |
| 105 | 7 | `static void place(Game g, long people)` | `people` placed in the city: a settled pyramid (PopulationCohorts.equilibriumShare()), the population and its workforce. |
| 114 | 8 | `static Game town(Path root, String name)` | EssentialWorksCheck.town()'s terms on a caused founding: two years of GDP borrowed, the builder may not borrow more, and builds from cash anyway. |
| 123 | 3 | `static Game flat(Path root, String name)` |  |
| 127 | 14 | `static double financing(Game g, double amount, double horizon)` |  |
| 142 | 27 | `static void parcelCash(Path root, boolean vault)` |  |
| 170 | 24 | `static void remainingCoupons(Path root)` |  |
| 195 | 33 | `static void laterQuotes(Path root)` |  |
| 230 | 3 | `static void carriedGround(Path root, boolean zoned) throws Exception` | Hand-priced parcels: each has only one and a half orders of reusable ground. |
| 234 | 72 | `static void carriedGround(Path root, boolean zoned, boolean unlinkedForest) throws Exception` |  |
| 308 | 75 | `static void roundedCash(Path root, boolean borrowFirst)` | The physical calendar supplies counts; this ledger independently carries each quote's cash. |
| 384 | 13 | `static AutoBuilder.OpenAsk ask(BuildAdvice.Suggestion plan, int month, double raised)` |  |
| 398 | 3 | `static void reservationRelease(Path root)` |  |
| 402 | 65 | `static void reservationRelease(Path root, boolean fullyRaised)` |  |
| 468 | 33 | `static void groundUse(String name)` |  |
| 502 | 30 | `static void independentPending(Path root)` |  |
| 533 | 4 | `static double addedPoints(Game.BuildQuote quote, BuildingsTemplate t, long count)` |  |
| 538 | 32 | `static void prospectiveQueue(Path root)` |  |
| 571 | 97 | `static void crewPromise(Path root, boolean partial) throws Exception` |  |
| 669 | 14 | `static Game reserveTown(Path root, String name)` |  |
| 684 | 64 | `static void pavingHold(Path root)` |  |
| 749 | 67 | `static void highwayPromise(Path root)` |  |
| 817 | 94 | `static void promotedDepot(Path root)` |  |
| 912 | 31 | `static void commonCalendar(Path root)` |  |
| 944 | 80 | `static void growingCalendarChoice(Path root)` |  |
| 1025 | 41 | `public static void main(String[] args) throws Exception` |  |


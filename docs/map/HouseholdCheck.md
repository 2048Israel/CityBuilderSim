# HouseholdCheck.java - 1,805 lines · 13 methods · 10 constants · harnesses

`ham/citybuildersim/HouseholdCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Verifies the residents' books and the demolition log.
> 
> The household statement is the last missing side of this economy's ledger, so
> what matters most here is that it is the OTHER SIDE of figures that already
> exist rather than a second, differently-computed version of them. If the
> people can be shown paying a different rent from the one landlords are shown
> receiving, the statement is worse than useless.
> 
> Three sections CAUSE their conditions (FS16) where they once read them off a
> played town: the flatshare section on a one-tier town's family model and
> hand-struck books (flatshareTown(), flatshareBooks()), the real deposit
> rate on a founding handed the rate savers are paid (handSavingAReturn()),
> and the commute on a bus town whose homes are handed cars (Household.cars).

**Uses:** [HouseholdBalance](HouseholdBalance.md) (91), [PayTier](PayTier.md) (86), [FamilyStructure](FamilyStructure.md) (63), [HouseholdAccounts](HouseholdAccounts.md) (42), [Household](Household.md) (40), [FamilyModel](FamilyModel.md) (18), [SocialSecurity](SocialSecurity.md) (15), [DemolitionLog](DemolitionLog.md) (9), [TaxPolicy](TaxPolicy.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (7), [PriceLevel](PriceLevel.md) (7), [RealEstate](RealEstate.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [Equity](Equity.md) (5), [Game](Game.md) (4), [WageBand](WageBand.md) (3), [BuildingManager](BuildingManager.md) (3), [StudentHousehold](StudentHousehold.md) (3), [RetiredHousehold](RetiredHousehold.md) (3), [BuildingType](BuildingType.md) (3), [MonetaryCheck](MonetaryCheck.md) (3), [NationalAccounts](NationalAccounts.md) (2), [Bank](Bank.md) (2), [Sectors](Sectors.md) (2), [Statement](Statement.md) (2), [HouseholdMemoryCheck](HouseholdMemoryCheck.md) (1), [JobType](JobType.md) (1), [WorkingHousehold](WorkingHousehold.md) (1), [TempDirs](TempDirs.md) (1), [GameFiles](GameFiles.md) (1)... and 1 more

## Sections

| line | section |
|---:|---|
| 122 | · 1. the statement |
| 144 | · 2. spending more than they earn |
| 166 | · 3. accumulating |
| 184 | · 4. per head |
| 208 | · 5. it is the other side of consumption |
| 226 | · 6. the demolition log |
| 254 | · 7. it fades out |
| 286 | · 8. the same books, per tier |
| 418 | · 9. rent is per home, not per head |
| 496 | · 10. pensions |
| 671 | · THE BUDGET CONSTRAINT |
| 825 | · SIXTY-EIGHT CELLS, AND THE MONEY FOLLOWS THE PEOPLE |
| 1172 | · POSITIVE FRACTIONAL HOUSEHOLDS OWN THEIR POSITIONS |
| 1290 | · AND WHEN THEY CANNOT AFFORD A HOME, THEY SHARE |
| 1399 | · A HOME IS A SIZE, AND A HOUSEHOLD HAS TO FIT |
| 1516 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 71 | `HouseholdCheck.FLATSHARE_ADULTS` | `600` | Adults in the flatshare fixture's town, all in the unskilled tier: enough that its singles divide into fives. |
| 74 | `HouseholdCheck.FLATSHARE_RENT` | `400` | The rent a door pays in the flatshare fixture's books, in the game's thousands. |
| 77 | `HouseholdCheck.FLATSHARE_BASKET` | `300` | ...and the basket a head buys: living alone costs FLATSHARE_RENT + FLATSHARE_BASKET. |
| 80 | `HouseholdCheck.FLATSHARE_TAX` | `.60` | The income tax of the flatshare fixture, 60% as the town this replaced had it. |
| 83 | `HouseholdCheck.FLATSHARE_TAKE_HOME` | `500` | What one earner takes home in the flatshare fixture: a basket paid and not a home, so a single adult is priced out of living alone and five of them are not. |
| 1538 | `HouseholdCheck.HAND_REAL_RETURN` | `.03` | The real return the rate fixture hands savers: three points above nought, so the factor is one less SAVING_RESPONSE of it, well inside the floor and the ceiling. |
| 1541 | `HouseholdCheck.RATE_MONTHS` | `3` | Months the rate fixture's city is stepped with savers handed their return before the month it is read at: the audit's months. |
| 1736 | `HouseholdCheck.HAND_CARS_PER_HOME` | `1` | Cars the commute fixture hands every home: one, the ceiling of ownership a month's buyers meet (Motoring.getOwnershipCeiling() starts at 1). |
| 1739 | `HouseholdCheck.COMMUTE_MONTHS_BEFORE_CARS` | `2` | Months the commute town is played before its homes are handed cars: enough for the households to exist and the buses to be riding (fares read from the second month). |
| 1742 | `HouseholdCheck.COMMUTE_MONTHS_AFTER_CARS` | `2` | ...and the months after, before it is read: the first strikes the drivers' fuel and the second reaches the books, which carry the month before's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 20 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 1788 | **type** `public class HouseholdCheck` | Verifies the residents' books and the demolition log. |
| 22 | 6 | `static void check(String label, double actual, double expected)` |  |
| 29 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 35 | 5 | `static double across(HouseholdBalance b, java.util.function.ToDoubleFunction<Household> perHousehold)` | One stock across every cell: per household times households. |
| 49 | 5 | `static double shop(HouseholdBalance b, double price, double delivered)` | The shops' month for a fixture (0.7.43): every basket the households ask for at this price, or this share of them, handed over at it (HouseholdBalance.allocateGroceries(), the call Retail makes) - and what they paid, ... |
| 56 | 3 | `static double openingSavings(Household c)` | Undo the settled cash flows to read the position just after the census. |
| 61 | 3 | `static double openingDebt(Household c)` | Undo lending, fees, repayment and the charge-off to read the census's debt. |
| 66 | 3 | `static double censusWriteOff(HouseholdBalance b)` | Census/estate losses apart from the card's independently tested late pipeline. |
| 86 | 7 | `static FamilyModel flatshareTown()` | A town whose adults all work unskilled jobs: its households as the family model builds them, before any is priced out. |
| 95 | 24 | `static HouseholdAccounts flatshareBooks(FamilyModel town, double takeHomeEach)` | That town's books struck by hand: the rent a door and the basket a head set, and a wage that takes an earner home takeHomeEach after FLATSHARE_TAX. |
| 120 | 1395 | `public static void main(String[] args)` |  |

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 1516-1805)

| line | len | member | says |
|---:|---:|---|---|
| 1544 | 3 | `static void handSavingAReturn(Game city)` | The bank is handed what it pays savers: the inflation they expect and HAND_REAL_RETURN over it. |
| 1548 | 186 | `static void whatItSpendsAnswersTheRealRate()` |  |
| 1758 | 47 | `static void theCommuteByRow()` | THE COMMUTE, BY ROW (0.7.49). |


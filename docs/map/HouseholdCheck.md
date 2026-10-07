# HouseholdCheck.java - 1,696 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/HouseholdCheck.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> Verifies the residents' books and the demolition log.
> 
> The household statement is the last missing side of this economy's ledger, so
> what matters most here is that it is the OTHER SIDE of figures that already
> exist rather than a second, differently-computed version of them. If the
> people can be shown paying a different rent from the one landlords are shown
> receiving, the statement is worse than useless.

**Uses:** [HouseholdBalance](HouseholdBalance.md) (90), [PayTier](PayTier.md) (73), [FamilyStructure](FamilyStructure.md) (53), [HouseholdAccounts](HouseholdAccounts.md) (38), [Household](Household.md) (35), [SocialSecurity](SocialSecurity.md) (15), [FamilyModel](FamilyModel.md) (12), [DemolitionLog](DemolitionLog.md) (9), [TaxPolicy](TaxPolicy.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (8), [RealEstate](RealEstate.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [Equity](Equity.md) (5), [Game](Game.md) (5), [WageBand](WageBand.md) (3), [BuildingManager](BuildingManager.md) (3), [StudentHousehold](StudentHousehold.md) (3), [RetiredHousehold](RetiredHousehold.md) (3), [BuildingType](BuildingType.md) (3), [MonetaryCheck](MonetaryCheck.md) (3), [NationalAccounts](NationalAccounts.md) (2), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (2), [Statement](Statement.md) (2), [JobType](JobType.md) (1), [WorkingHousehold](WorkingHousehold.md) (1), [Sectors](Sectors.md) (1)

## Sections

| line | section |
|---:|---|
| 51 | · 1. the statement |
| 73 | · 2. spending more than they earn |
| 95 | · 3. accumulating |
| 113 | · 4. per head |
| 137 | · 5. it is the other side of consumption |
| 155 | · 6. the demolition log |
| 183 | · 7. it fades out |
| 215 | · 8. the same books, per tier |
| 347 | · 9. rent is per home, not per head |
| 425 | · 10. pensions |
| 599 | · THE BUDGET CONSTRAINT |
| 746 | · SIXTY-EIGHT CELLS, AND THE MONEY FOLLOWS THE PEOPLE |
| 1081 | · A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) |
| 1195 | · AND WHEN THEY CANNOT AFFORD A HOME, THEY SHARE |
| 1295 | · THIS CITY IS NO LONGER POOR, AND THAT IS NOT A FAILURE (2026-09-09). |
| 1335 | · A HOME IS A SIZE, AND A HOUSEHOLD HAS TO FIT |
| 1452 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1685 | **type** `public class HouseholdCheck` | Verifies the residents' books and the demolition log. |
| 16 | 6 | `static void check(String label, double actual, double expected)` |  |
| 23 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 29 | 5 | `static double across(HouseholdBalance b, java.util.function.ToDoubleFunction<Household> perHousehold)` | One stock across every cell: per household times households. |
| 43 | 5 | `static double shop(HouseholdBalance b, double price, double delivered)` | The shops' month for a fixture (0.7.43): every basket the households ask for at this price, or this share of them, handed over at it (HouseholdBalance.allocateGroceries(), the call Retail makes) - and what they paid, ... |
| 49 | 1402 | `public static void main(String[] args)` |  |

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 1452-1696)

| line | len | member | says |
|---:|---:|---|---|
| 1473 | 170 | `static void whatItSpendsAnswersTheRealRate()` |  |
| 1652 | 44 | `static void theCommuteByRow()` | THE COMMUTE, BY ROW (0.7.49). |


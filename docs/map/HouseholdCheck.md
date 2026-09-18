# HouseholdCheck.java - 1,299 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/HouseholdCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Verifies the residents' books and the demolition log.
> 
> The household statement is the last missing side of this economy's ledger, so
> what matters most here is that it is the OTHER SIDE of figures that already
> exist rather than a second, differently-computed version of them. If the
> people can be shown paying a different rent from the one landlords are shown
> receiving, the statement is worse than useless.

**Uses:** [PayTier](PayTier.md) (60), [HouseholdBalance](HouseholdBalance.md) (49), [FamilyStructure](FamilyStructure.md) (40), [HouseholdAccounts](HouseholdAccounts.md) (36), [Household](Household.md) (24), [SocialSecurity](SocialSecurity.md) (15), [FamilyModel](FamilyModel.md) (12), [DemolitionLog](DemolitionLog.md) (9), [TaxPolicy](TaxPolicy.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (8), [RealEstate](RealEstate.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [Equity](Equity.md) (5), [WageBand](WageBand.md) (3), [StudentHousehold](StudentHousehold.md) (3), [RetiredHousehold](RetiredHousehold.md) (3), [BuildingType](BuildingType.md) (3), [NationalAccounts](NationalAccounts.md) (2), [BuildingManager](BuildingManager.md) (2), [Game](Game.md) (2), [Statement](Statement.md) (2), [JobType](JobType.md) (1), [WorkingHousehold](WorkingHousehold.md) (1), [Sectors](Sectors.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 30 | · 1. the statement |
| 52 | · 2. spending more than they earn |
| 74 | · 3. accumulating |
| 92 | · 4. per head |
| 116 | · 5. it is the other side of consumption |
| 134 | · 6. the demolition log |
| 162 | · 7. it fades out |
| 194 | · 8. the same books, per tier |
| 326 | · 9. rent is per home, not per head |
| 404 | · 10. pensions |
| 578 | · THE BUDGET CONSTRAINT |
| 717 | · SIXTY-EIGHT CELLS, AND THE MONEY FOLLOWS THE PEOPLE |
| 1050 | · AND WHEN THEY CANNOT AFFORD A HOME, THEY SHARE |
| 1146 | · THIS CITY IS NO LONGER POOR, AND THAT IS NOT A FAILURE (2026-09-09). |
| 1186 | · A HOME IS A SIZE, AND A HOUSEHOLD HAS TO FIT |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1288 | **type** `public class HouseholdCheck` | Verifies the residents' books and the demolition log. |
| 16 | 6 | `static void check(String label, double actual, double expected)` |  |
| 23 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 28 | 1271 | `public static void main(String[] args)` |  |


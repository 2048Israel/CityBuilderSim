# FinancialOwnerFinding.java - 106 lines · 7 methods · 0 constants · model

`ham/citybuildersim/FinancialOwnerFinding.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Caused absent-owner and positive fractional-owner laws, registered through DenominationCheck.
> WHY: the optional-action threshold classified real fractional owners as absent;
> the native reform scan must retain their claims and forbid raw positions at zero count.

**Uses:** [DenominationCheck](DenominationCheck.md) (11), [HouseholdBalance](HouseholdBalance.md) (7), [Equity](Equity.md) (7), [Household](Household.md) (6), [FamilyStructure](FamilyStructure.md) (5), [PayTier](PayTier.md) (4), [Denomination](Denomination.md) (1)

**Used by (2):** [DenominationCheck](DenominationCheck.md), [HousingMarketCheck](HousingMarketCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 95 | **type** `final class FinancialOwnerFinding` | Caused absent-owner and positive fractional-owner laws, registered through DenominationCheck. |
| 13 | 1 | **type** `private record Position(double value, boolean localMoney)` |  |
| 14 | 1 | `private FinancialOwnerFinding()` |  |
| 16 | 42 | `static void run()` |  |
| 60 | 9 | `private static void seed(Household c)` | Cause every native Stock family, including each security's basis and tax claims. |
| 71 | 17 | `private static Map<String, Position> positions(Household c)` | Match HouseholdBalance.stocks(int[]); also guard the derived bonds rollup. |
| 88 | 1 | `private static void money(Map<String, Position> p, String name, double value)` |  |
| 90 | 8 | `private static Map<String, Position> totals(HouseholdBalance b)` |  |
| 98 | 8 | `private static void compare(String label, Map<String, Position> actual, Map<String, Position> original, double moneyScale)` |  |


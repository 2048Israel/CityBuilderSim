# WorkingHousehold.java - 39 lines · 6 methods · 0 constants · model

`ham/citybuildersim/WorkingHousehold.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> A household with an earner in it, at one pay tier.
> 
> Its income is the tier's wage bill, split across the tier's cells by how
> many earners each shape fields - a couple takes home twice what a single
> adult does, five adults sharing five times. Its money follows its earners
> when the household changes shape, for the same reason.
> 
> There is no SharedHousehold. Five adults sharing differ from a couple in
> nothing this ledger does - they pay one rent, they draw on one credit line
> per household, they discharge together - and a subclass that overrode
> nothing would be a costume. The flatshare's difference is upstream, in how
> FamilyModel forms it and what the rent per door does to five wages at once.

**Uses:** [PayTier](PayTier.md) (3), [Household](Household.md) (1), [FamilyStructure](FamilyStructure.md) (1)

**Used by (2):** [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `private final PayTier tier` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 23 | **type** `public class WorkingHousehold extends Household` | A household with an earner in it, at one pay tier. |
| 21 | 7 | `public WorkingHousehold(FamilyStructure shape, PayTier tier)` |  |
| 29 | 1 | `public PayTier tier()` |  |
| 30 | 1 | `public int row()` |  |
| 31 | 1 | `public boolean isRetired()` |  |
| 32 | 1 | `public int grownUps()` |  |
| 38 | 1 | `protected double studentRepayment()` | A graduate's student loan, repaid out of the family's wages over nine and a half years. |


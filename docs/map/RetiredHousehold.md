# RetiredHousehold.java - 51 lines · 6 methods · 0 constants · model

`ham/citybuildersim/RetiredHousehold.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> A household with nobody of working age in it: a senior or an elder, alone or
> as a couple.
> 
> No pay tier, because a tier is a wage and nobody here draws one. Its income
> is the pension bill, split across the retired cells by how many pensioners
> each shape holds - a senior couple draws two - and that is also the weight
> its money follows: a worker retiring into SENIOR_ALONE carries their wallet
> across the row boundary, and one of a senior couple dying leaves half the
> couple's position to the survivor and takes the other half out of the city.
> 
> A PENSIONER IS ANYONE PAST THE RETIREMENT AGE, not anyone in the senior band.
> This counted members of SENIOR only, and when the band split on 2026-09-15
> the over-85s inherited a household that reported NO pensioners in it: zero
> weight in HouseholdBalance's row split, so zero pension, so an elder paid
> rent out of savings until there were none. Measured on DenominationCheck's
> founding: the landlord's revenue fell, it shed plant under the rule that a
> firm which cannot pay must, and the city went from 7,955 homes and 18,400
> people to 2,881 homes and 4,200 over fifteen years - while the same city on
> the shipped build never lost a single home. Asking the BAND whether it is
> retirement age, rather than naming one, is what makes a third band safe.
> 
> FamilyModel keeps the retired at tier index 0 by convention; this class is
> where that convention stops - a retired cell has no tier at all, and sums
> into RETIRED_ROW.

**Uses:** [FamilyStructure](FamilyStructure.md) (2), [AgeBand](AgeBand.md) (2), [Household](Household.md) (1), [PayTier](PayTier.md) (1)

**Used by (3):** [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [PeopleScreen](PeopleScreen.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 23 | **type** `public class RetiredHousehold extends Household` | A household with nobody of working age in it: a senior or an elder, alone or as a couple. |
| 31 | 6 | `public RetiredHousehold(FamilyStructure shape)` |  |
| 38 | 1 | `public PayTier tier()` |  |
| 39 | 1 | `public int row()` |  |
| 40 | 1 | `public boolean isRetired()` |  |
| 41 | 1 | `public int grownUps()` |  |
| 44 | 7 | `public static int pensionersIn(FamilyStructure shape)` | Everyone in the shape who is past the retirement age, whichever band they are in. |


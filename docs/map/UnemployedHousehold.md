# UnemployedHousehold.java - 83 lines · 14 methods · 0 constants · model

`ham/citybuildersim/UnemployedHousehold.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Adults who are out of work, as one ledger per situation.
> 
> Jerus, 2026-09-11: "we are to add a new household structure called
> unemployed, these are different, these will just sum up by age the
> unemployed... and they will have their own cashflow and stuff."
> 
> Until this class every adult was put in a family at the tier mix of the
> filled jobs, so the out of work shared a tier's wages - an unskilled single
> adult took home $1,262 in a month of 40% unemployment. The families are now
> built from the adults who work (FamilyModel.rebuild), and whoever is left
> is here: one adult per household, no tier, in one of three situations.
> 
>   ON_EI     lost a job, or came for one and did not get it, in the last
>             twelve months; draws Employment Insurance (see Unemployment)
>   OFF_EI    past the twelfth month, or never insured; lives on savings and
>             the credit line, and is evicted when neither covers the rent
>   UNHOUSED  evicted: no rent, no income, going without, sick faster
> 
> THE MONEY FOLLOWS THEM. A worker who loses a job carries their savings and
> debt from the family cells into this row; a hire carries them back; a
> claimant whose twelfth month ends moves theirs from ON_EI to OFF_EI. Weighed
> by grown-ups, like every other move - see HouseholdBalance.followThePeople().
> 
> WHERE THEY LIVE is FamilyModel's: a studio of their own, five to a home
> with their own kind when priced out or short of doors, and no door when
> both valves fail. The rent share that decides is handed in by Game.

**Uses:** [PayTier](PayTier.md) (2), [Household](Household.md) (1)

**Used by (7):** [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 35 | `UnemployedHousehold.Status.ON_EI` |  |
| 36 | `UnemployedHousehold.Status.OFF_EI` |  |
| 37 | `UnemployedHousehold.Status.UNHOUSED` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `private final String label` |  |
| 44 | `private final Status status` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 31 | 53 | **type** `public class UnemployedHousehold extends Household` | Adults who are out of work, as one ledger per situation. |
| 34 | 9 | **type** `public enum Status` | Where one of them stands. |
| 40 | 1 | `Status(String label)` _(in UnemployedHousehold.Status)_ |  |
| 41 | 1 | `public String label()` _(in UnemployedHousehold.Status)_ |  |
| 46 | 4 | `public UnemployedHousehold(Status status)` |  |
| 51 | 1 | `public Status status()` |  |
| 53 | 1 | `public PayTier tier()` |  |
| 54 | 1 | `public int row()` |  |
| 55 | 1 | `public boolean isRetired()` |  |
| 56 | 1 | `public int grownUps()` |  |
| 57 | 1 | `public int size()` |  |
| 60 | 1 | `public double earningWeight()` | The EI bill is split among those still drawing it. |
| 63 | 1 | `public boolean canBeEvicted()` | Only once EI has ended - Jerus: "EI ends, savings gone". |
| 77 | 1 | `public int stockGroup()` | THE OUT OF WORK CARRY THE UNSKILLED TIER'S MONEY, NOT THE CITY'S. |
| 79 | 1 | `public String label()` |  |
| 82 | 1 | `public String key()` | "UNEMPLOYED:ON_EI". |


# RetiredHousehold.java - 78 lines · 11 methods · 0 constants · model

`ham/citybuildersim/RetiredHousehold.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> A household with nobody of working age in it: a senior or an elder, alone or
> as a couple, at the pay tier its people retired from.
> 
> No pay tier of its own, because a tier is a wage and nobody here draws one:
> tier() is null, row() is RETIRED_ROW, and every sum and screen by row reads
> the retired as one row. Its income is the pension bill, split across the
> retired cells by how many pensioners each shape holds - a senior couple draws
> two - and that is also the weight its money follows.
> 
> WHY A FORMER TIER (HS1, 2026-11-18; the households study, 3.2). Jerus: "the
> elderly are routinely broke." The four retired cells had no tier, so a worker
> retiring left their savings in their pay row for whoever joined it, and the
> retired row lived on the pension and on whatever the census's pool happened
> to hand it: at month 4,000 of the playtest city a senior living alone held 9%
> of a working household's net worth, and an Elite couple retired as poor as an
> unskilled widow. Now each retired shape is six cells, one for each tier its
> people worked at (24 in all), the month's flows carry a retiree's money into
> the cell of the tier they left (HouseholdBalance, MONEY FOLLOWS PEOPLE), and
> the cells of one former tier are a stock group of their own (stockGroup()),
> so an Elite couple's money stays Elite through the census's reshuffles.
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

**Uses:** [PayTier](PayTier.md) (5), [FamilyStructure](FamilyStructure.md) (2), [AgeBand](AgeBand.md) (2), [Household](Household.md) (1)

**Used by (3):** [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [PeopleScreen](PeopleScreen.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `private final PayTier former` | The tier this cell's people worked at before they retired (HS1). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 43 | **type** `public class RetiredHousehold extends Household` | A household with nobody of working age in it: a senior or an elder, alone or as a couple, at the pay tier its people retired from. |
| 41 | 8 | `public RetiredHousehold(FamilyStructure shape, PayTier former)` |  |
| 50 | 1 | `public PayTier tier()` |  |
| 51 | 1 | `public int row()` |  |
| 52 | 1 | `public boolean isRetired()` |  |
| 53 | 1 | `public int grownUps()` |  |
| 56 | 1 | `public PayTier formerTier()` | The tier this cell's people worked at before they retired. |
| 59 | 1 | `public int stockGroup()` | The cells of one former tier, together: retirees and the census's reshuffles keep the tier (HS1). |
| 62 | 1 | `public String key()` | "SENIOR_ALONE:SKILLED": the shape and the tier its people retired from. |
| 65 | 1 | `public String label()` | "Senior living alone, formerly Skilled". |
| 68 | 1 | `public static int retiredGroup(PayTier former)` | The stock group of the retired from this tier: after every row's, one for each tier (Household.STOCK_GROUPS). |
| 71 | 7 | `public static int pensionersIn(FamilyStructure shape)` | Everyone in the shape who is past the retirement age, whichever band they are in. |


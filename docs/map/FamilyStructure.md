# FamilyStructure.java - 119 lines · 7 methods · 0 constants · model

`ham/citybuildersim/FamilyStructure.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The shapes a household comes in.
> 
> Each one declares how many of each age band it holds, and that declaration is
> the whole definition - the model assembles households by drawing from the
> cohorts until it runs out, so a shape is just a shopping list of people.
> 
> SENIORS KEEP TO THEMSELVES, which is a modelling choice rather than a claim
> about how people live. A senior household has no earner in it, so mixing
> seniors into working households would put a pay tier on someone who has no
> job - and the tier axis is the thing that makes the matrix mean anything.
> Multi-generational households are a real omission and are noted as such.
> 
> COUPLES SHARE ONE PAY TIER, per Jerus: the model has one tier per household,
> so a household of two earners in different tiers cannot be represented. Real
> enough at the coarse end - people do partner within their own income band far
> more than at random - and the alternative is a tier-pair matrix six times
> larger for a distinction nothing currently reads.

**Uses:** [AgeBand](AgeBand.md) (9)

**Used by (27):** [BankCheck](BankCheck.md), [CarCheck](CarCheck.md), [Consumption](Consumption.md), [ConsumptionCheck](ConsumptionCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FamilyModel](FamilyModel.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [LongPlaytest](LongPlaytest.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 24 | · households with nobody of working age |

## Enum constants

| line | constant | says |
|---:|---|---|
| 26 | `FamilyStructure.SENIOR_ALONE` |  |
| 27 | `FamilyStructure.SENIOR_COUPLE` |  |
| 42 | `FamilyStructure.ELDER_ALONE` | THE OVER-85s KEEP THEIR OWN SHAPES, added 2026-09-15 with the band. |
| 43 | `FamilyStructure.ELDER_COUPLE` |  |
| 47 | `FamilyStructure.SINGLE_ADULT` |  |
| 48 | `FamilyStructure.COUPLE` |  |
| 50 | `FamilyStructure.SINGLE_PARENT` |  |
| 52 | `FamilyStructure.COUPLE_BABY` |  |
| 53 | `FamilyStructure.COUPLE_CHILD` |  |
| 54 | `FamilyStructure.COUPLE_TEEN` |  |
| 56 | `FamilyStructure.COUPLE_BABY_CHILD` |  |
| 57 | `FamilyStructure.COUPLE_CHILD_TEEN` |  |
| 58 | `FamilyStructure.COUPLE_TWO_CHILDREN` |  |
| 60 | `FamilyStructure.LARGE_FAMILY` |  |
| 76 | `FamilyStructure.SHARED_ADULTS` | Five single adults in one home, formed ONLY when homes run short. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `private final String label` |  |
| 79 | `private final int[] members` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 22 | 98 | **type** `public enum FamilyStructure` | The shapes a household comes in. |

### households with nobody of working age (lines 24-119)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 10 | `FamilyStructure(String label, int babies, int children, int teens, int adults, int seniors, int elders)` |  |
| 92 | 1 | `public String getLabel()` |  |
| 94 | 3 | `public int membersOf(AgeBand band)` |  |
| 99 | 5 | `public int size()` | Everyone in the household, of any age. |
| 106 | 3 | `public int earners()` | How many earners it can field. |
| 111 | 3 | `public int dependants()` | Dependants per household - the reason the tier matters. |
| 116 | 3 | `public boolean isRetired()` | True for households with no working-age member, which carry no pay tier. |


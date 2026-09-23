# PayTier.java - 125 lines · 5 methods · 0 constants · model

`ham/citybuildersim/PayTier.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The six pay levels a household can be in.
> 
> WHY SIX, AND WHY THIS AXIS AT ALL
> 
> Families are grouped by what they earn, so there has to be a finite set of
> "what they earn". There were TEN distinct wage figures across the eleven job
> types - not eleven, because UNIV_FINANCE and UNIV_HIGHTECH_ENG were both 6.5 -
> and ten tiers times a dozen family shapes is a matrix nobody can read.
> 
> Grouped by role rather than by arithmetic convenience: the three college jobs
> pay the same as each other, the two professional doctorates pay the same, and
> the applied-science group sits between them. Measured against a real city of
> 8,792 the collapse moves the whole wage bill by **-1.08%**, which is the
> price of the tidier axis and cheap at that.
> 
> WHAT THE SAME MEASUREMENT SHOWED, WHICH MATTERS MORE
> 
> That city employed 3,371 NO_DIPLOMA, 1,274 DIPLOMA, 114 COLLEGE_ENGINEERING,
> 2 COLLEGE_BUSINESS, 2 UNIV_SCIENCE - and ZERO doctors, lawyers, finance staff,
> high-tech engineers or policy staff. Six of the eleven job types are not used
> by any building in the game.
> 
> So in play this axis currently has about three live tiers, and the family
> matrix will be mostly empty until something employs the top half of the
> ladder. That is a fact about the BUILDINGS, not about this enum, and it is
> recorded here because it is the first thing that will look broken on the
> demographics screen and the first thing somebody will try to "fix" in the
> wrong place.

**Uses:** [JobType](JobType.md) (2)

**Used by (41):** [AgricultureCheck](AgricultureCheck.md), [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FamilyModel](FamilyModel.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [LabourCheck](LabourCheck.md), [LabourMarket](LabourMarket.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Migration](Migration.md), [OrphanHousehold](OrphanHousehold.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PopulationManager](PopulationManager.md), [PrisonerHousehold](PrisonerHousehold.md), [RealEstate](RealEstate.md), [RestaurantsCheck](RestaurantsCheck.md), [RetiredHousehold](RetiredHousehold.md), [SocialSecurity](SocialSecurity.md), [StudentHousehold](StudentHousehold.md), [TaxPolicy](TaxPolicy.md), [TradeCostCheck](TradeCostCheck.md), [UnemployedHousehold](UnemployedHousehold.md), [Unemployment](Unemployment.md), [WorkingHousehold](WorkingHousehold.md)

## Sections

| line | section |
|---:|---|
| 33 | THE LADDER, RE-ANCHORED ON PUBLISHED MEDIANS - 2026-09-09 |

## Enum constants

| line | constant | says |
|---:|---|---|
| 68 | `PayTier.UNSKILLED` | Labouring. |
| 71 | `PayTier.SKILLED` | Trades and clerical. |
| 75 | `PayTier.COLLEGE` | The three college paths - health, business, engineering. |
| 78 | `PayTier.PROFESSIONAL` | Applied science and public administration. |
| 81 | `PayTier.SENIOR_PROFESSIONAL` | Finance and high technology. |
| 84 | `PayTier.ELITE` | Medicine and law. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 86 | `private final String label` |  |
| 87 | `private final double monthlyWage` |  |

## Methods, in file order, under their sections

### THE LADDER, RE-ANCHORED ON PUBLISHED MEDIANS - 2026-09-09 (lines 33-125)

| line | len | member | says |
|---:|---:|---|---|
| 65 | 61 | **type** `public enum PayTier` |  |
| 89 | 4 | `PayTier(String label, double monthlyWage)` |  |
| 94 | 1 | `public String getLabel()` |  |
| 95 | 1 | `public double getMonthlyWage()` |  |
| 106 | 14 | `public static PayTier of(JobType job)` | Which tier a job belongs to. |
| 122 | 3 | `public static double wageOf(JobType job)` | The wage every job of this type is paid. |


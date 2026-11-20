# HouseholdStageCheck.java - 563 lines · 11 methods · 1 constants · harnesses

`ham/citybuildersim/HouseholdStageCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> The two adult stages: the young, 18-34, and the established, 35-69, each
> with a pyramid compartment, households and books of its own (HS2).
> 
> WHY, AND WHAT ONE BUCKET GOT WRONG. The pyramid held the adults as one band
> of fifty-two years drained at 1/624 a month, so an eighteen-year-old was as
> likely to retire this month as a sixty-nine-year-old, a city of young
> migrants retired its first pensioners in its first month, and a couple of
> twenty-five and a couple with a teen drew on one pool of adults and one
> pool of money. Now the young move up at 1/204 and the established retire at
> 1/420, both at the adult mortality (docs/studies/spec-households.md 3.4 and
> its 5 for HS2). Every fixture below CAUSES its flow, and every figure is
> asserted against the dials (AdultStage, FamilyModel's YOUNG_SHARE_OF_
> shares), never a number; put one adult bucket back and section 1 fails.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. RESIDENCE OF 204 AND 420 MONTHS. A pyramid of young adults alone,
>      nobody dying or born, spends the young's span among them on average,
>      and one of established adults the established's; a city of young
>      adults retires nobody in its first month; the teens who turn 18 are
>      young, the young who turn 35 established, the retirements the
>      established's alone; both stages die at the adult mortality; the
>      adults are the two stages' sum through the month, the arrivals, the
>      leavers and the save; and a settled city's shares chain the two.
> 
>   2. AGEING CARRIES THE MONEY. A young household turning 35 moves whole to
>      the established cell of its shape and tier carrying every stock it
>      holds at its cell's average, with a competing claimant of its own
>      stage that a pool reading the net change would have handed it; the
>      young retire none and the established at their own share; every
>      stock is conserved. In a real city's months the young move up at the
>      pyramid's share and carry savings, and the households' identity
>      (HL1, MoneyAudit.households()) closes every month.
> 
>   3. NO TEEN SHAPE AMONG THE YOUNG. The families' young form only the
>      seven shapes AdultStage.forms() names, house no teen, and take the
>      dials' shares of the babies and the children; the books keep no young
>      cell for any other shape.
> 
>   4. THE POOL'S GROUP IS (TIER, STAGE). The census pool hands a young
>      cell's released money to the young of its tier first, the
>      established's to the established, and either to the city only when
>      its own stage has no claimant.
> 
>   5. 108 WORKING CELLS: 11 shapes established and 7 young at each of the
>      six tiers, each young cell its own key and every tier's young their
>      own stock group.

**Uses:** [AgeBand](AgeBand.md) (37), [FamilyStructure](FamilyStructure.md) (32), [PayTier](PayTier.md) (23), [Household](Household.md) (21), [PopulationCohorts](PopulationCohorts.md) (15), [HouseholdBalance](HouseholdBalance.md) (13), [AdultStage](AdultStage.md) (12), [FamilyModel](FamilyModel.md) (12), [HouseholdFlowCheck](HouseholdFlowCheck.md) (11), [HouseholdMemoryCheck](HouseholdMemoryCheck.md) (8), [WorkingHousehold](WorkingHousehold.md) (4), [MoneyAudit](MoneyAudit.md) (2), [Game](Game.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 151 | · 1. residence of 204 and 420 months |
| 285 | · 2. ageing carries the money |
| 391 | · 3. no teen shape among the young |
| 479 | · 4. the pool's group is (tier, stage) |
| 517 | · 5. 108 working cells |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 67 | `HouseholdStageCheck.TOLERANCE` | `1e-9` | How close two figures must be, relative to the larger of the want and one: every figure here is a product of a few dials and counts. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 63 | `static PrintStream out` |  |
| 64 | `static PrintStream quiet` |  |
| 113 | `final HouseholdBalance bal` |  |
| 114 | `private final Map<Household, Double> census` |  |
| 115 | `private final Map<FamilyStructure, Double> retired` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 60 | 504 | **type** `public class HouseholdStageCheck` | The two adult stages: the young, 18-34, and the established, 35-69, each with a pyramid compartment, households and books of its own (HS2). |
| 69 | 5 | `static void check(String label, double got, double want)` |  |
| 75 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 80 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 85 | 5 | `static void quietly(Runnable r)` |  |
| 92 | 3 | `static double[] nobodyDies()` | Every band's mortality times nought: nobody dies, so a stage's only outflow is its own. |
| 97 | 3 | `static boolean isZero(double x)` | True for a figure nought to the tolerance. |
| 102 | 3 | `static double total(Household c, ToDoubleFunction<Household> stock)` | One stock of a cell, every household's. |
| 112 | 30 | **type** `static final class Books` | A fixture: a balance, and the census the month ends on, cell by cell. |
| 117 | 6 | `Household cell(FamilyStructure shape, PayTier tier, AdultStage stage, double households, double expected)` _(in HouseholdStageCheck.Books)_ |  |
| 124 | 4 | `Books expectRetired(FamilyStructure shape, double total)` _(in HouseholdStageCheck.Books)_ |  |
| 130 | 11 | `void run()` _(in HouseholdStageCheck.Books)_ | The month's census alone, by stage, as the balance reads it. |
| 143 | 420 | `public static void main(String[] args) throws Exception` |  |


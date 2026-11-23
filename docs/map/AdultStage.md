# AdultStage.java - 88 lines · 10 methods · 0 constants · model

`ham/citybuildersim/AdultStage.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> The two stages of an adult life inside AgeBand.ADULT: the young, 18-34, and
> the established, 35-69.
> 
> WHY (HS2, 2026-11-19; docs/studies/spec-households.md 3.4). The pyramid held
> the adults as one band of fifty-two years drained at 1/624 a month, so an
> eighteen-year-old was as likely to retire this month as a sixty-nine-year-old,
> and every working household was the same age: a couple with a teen and a
> couple of twenty-five drew on one pool of adults and one pool of money.
> Jerus: a "large family would be older adults". Now the adults are two
> compartments in a row: the young move up at 1/204 a month and the
> established retire at 1/420, both at the adult mortality, so the adult
> years are an Erlang spread rather than an exponential one, and a city of
> young migrants retires fewer people in its first decades. The split at 35
> is where Canadian home-ownership jumps (44% to 63%) and median net worth
> more than doubles (the study's [S1]); retirement stays at 70 (its call 5).
> 
> NOT A BAND. AgeBand.ADULT stays the band every reader asks for, and the
> pyramid answers it with the two stages' sum (PopulationCohorts.get), so the
> workforce, the arrivals' mix and every screen read what they read before.
> Only the households' books and the families' builder ask for a stage.

**Uses:** [FamilyStructure](FamilyStructure.md) (1)

**Used by (18):** [CapitalGainsCheck](CapitalGainsCheck.md), [CardCheck](CardCheck.md), [DebtStatistics](DebtStatistics.md), [DebtStatisticsCheck](DebtStatisticsCheck.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HouseholdAccounts](HouseholdAccounts.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdFlowCheck](HouseholdFlowCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [KeptBench](KeptBench.md), [MoneyWordsCheck](MoneyWordsCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCohorts](PopulationCohorts.md), [WorkingHousehold](WorkingHousehold.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 27 | `AdultStage.YOUNG` |  |
| 28 | `AdultStage.ESTABLISHED` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `private final String label` |  |
| 31 | `private final int fromAge` |  |
| 32 | `private final int toAge` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 64 | **type** `public enum AdultStage` | The two stages of an adult life inside AgeBand.ADULT: the young, 18-34, and the established, 35-69. |
| 34 | 5 | `AdultStage(String label, int fromAge, int toAge)` |  |
| 40 | 1 | `public String getLabel()` |  |
| 41 | 1 | `public int getFromAge()` |  |
| 42 | 1 | `public int getToAge()` |  |
| 44 | 1 | `public String getAges()` | The inclusive ages the household money page names (HS3). |
| 47 | 3 | `public int spanMonths()` | How many months an adult spends in the stage, on average: 204 young, 420 established. |
| 57 | 3 | `public double monthlyOutflowRate()` | The fraction that moves on each month: the young to the established, the established to the seniors. |
| 62 | 4 | `public AdultStage next()` | The stage after this one, or null for the established, who retire into AgeBand.SENIOR. |
| 74 | 9 | `public boolean forms(FamilyStructure shape)` | Whether households of this stage take the shape. |
| 85 | 3 | `public String keySuffix()` | The stage a household in this stage's place in the save is written as: "" for the established, whose keys are the ones from before the stages. |


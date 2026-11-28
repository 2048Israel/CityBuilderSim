# HouseholdMemoryCheck.java - 416 lines · 11 methods · 1 constants · harnesses

`ham/citybuildersim/HouseholdMemoryCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The households remember: the builder keeps what still fits.
> 
> Jerus, 2026-09-11: a record of the households of each type, "so when the
> model rebuilds it has a reference to try and keep but still allow change".
> Keep what still fits, 1% a month re-forming on its own, every cell, in the
> save and on the graphs. Every claim sets its own cause. See
> claude/the-households-remember.md.

**Uses:** [FamilyStructure](FamilyStructure.md) (43), [FamilyModel](FamilyModel.md) (42), [AdultStage](AdultStage.md) (27), [AgeBand](AgeBand.md) (24), [PayTier](PayTier.md) (16), [PopulationCohorts](PopulationCohorts.md) (12), [Game](Game.md) (4), [GameFiles](GameFiles.md) (2), [HistorySave](HistorySave.md) (2), [TempDirs](TempDirs.md) (1), [BuildingManager](BuildingManager.md) (1), [LandManager](LandManager.md) (1)

**Used by (5):** [AshbourneCheck](AshbourneCheck.md), [CardCheck](CardCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md)

## Sections

| line | section |
|---:|---|
| 52 | · the two adult stages (HS2) |
| 126 | · 1. a bare model does not remember |
| 140 | · 2. the same city a month later |
| 155 | · 3. kept, and the rest built from the people left over |
| 243 | · 4. a child grows up |
| 282 | · 5. the tiers follow the jobs |
| 310 | · 6. a save |
| 353 | · 7. a city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `HouseholdMemoryCheck.WORKING` | `java.util.Arrays.stream(FamilyStructure.values()).filter(s -> ! s.isRetired()...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 405 | **type** `public class HouseholdMemoryCheck` | The households remember: the builder keeps what still fits. |
| 16 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 22 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 27 | 5 | `static void quietly(Runnable work)` |  |
| 34 | 7 | `static PopulationCohorts pyramid(double babies, double children, double teens, double adults, double seniors)` | A pyramid from the five bands this fixture cares about. |
| 46 | 5 | `static double total(FamilyModel f)` |  |

### the two adult stages (HS2) (lines 52-416)

| line | len | member | says |
|---:|---:|---|---|
| 59 | 5 | `static double totalOf(FamilyModel f, FamilyStructure s, AdultStage g)` | A shape's households of one stage, every tier. |
| 66 | 6 | `static double shareOf(AgeBand b, AdultStage g, PopulationCohorts c)` | Of a band in the families, the share a stage holds: the children by the dials, the adults by the pyramid. |
| 74 | 13 | `static PopulationCohorts staged(double babies, double children, double teens, double adults, double young)` | A pyramid with its adults' young named: the cohorts' own save, the two stages after its seven scalars. |
| 98 | 12 | `static FamilyModel builtFor(AdultStage g, double[] pool, double[] jobs)` | The bare builder's answer for one stage's own pool (babies, children, teens, adults): a pyramid whose adults are all that stage's; the other stage has no adults and forms nothing. |
| 112 | 5 | `static double youngLeftOf(FamilyModel builtYoung, AgeBand band, double pool)` | Of builtFor(YOUNG, pool)'s children of a band, what the young's builder left: its orphans less the established's share, who had no adults to go to. |
| 118 | 298 | `public static void main(String[] args) throws Exception` |  |


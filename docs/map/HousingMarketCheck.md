# HousingMarketCheck.java - 412 lines · 18 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [HousingChoice](HousingChoice.md) (33), [Game](Game.md) (22), [FamilyStructure](FamilyStructure.md) (20), [HousingMarket](HousingMarket.md) (19), [PayTier](PayTier.md) (18), [HousingForms](HousingForms.md) (14), [LongPlaytest](LongPlaytest.md) (12), [AgeBand](AgeBand.md) (11), [FamilyModel](FamilyModel.md) (10), [GameFiles](GameFiles.md) (10), [RealEstate](RealEstate.md) (8), [WorkingHousehold](WorkingHousehold.md) (8), [HousingInventory](HousingInventory.md) (5), [AdultStage](AdultStage.md) (4), [PopulationCohorts](PopulationCohorts.md) (4), [JobType](JobType.md) (4), [HousingRecipe](HousingRecipe.md) (3), [PriceLevel](PriceLevel.md) (2), [MoneyAudit](MoneyAudit.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [StudentHousehold](StudentHousehold.md) (2), [Household](Household.md) (1), [WageBand](WageBand.md) (1), [Founding](Founding.md) (1), [ConstructionControl](ConstructionControl.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 16 | `HousingMarketCheck.JSON` | `new Gson()` |  |
| 18 | `HousingMarketCheck.SECTOR_ENDOWMENT` | `2_000_000 * PriceLevel.FACTOR` | Two billion present-day dollars of fixture equity, before the first audit opens. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static PrintStream out` |  |
| 15 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 13 | 400 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 65 | 48 | `static void classPrices()` |  |
| 113 | 11 | `static void foundingBudgets()` |  |
| 124 | 13 | `static void fractionalPressure()` |  |
| 137 | 36 | `static void sharingOrder()` |  |
| 173 | 26 | `static void doubledDoors()` |  |
| 199 | 13 | `static void studentFacility()` |  |
| 212 | 43 | `static void fundedHost()` |  |
| 255 | 22 | `static void aggregatePackingRemainder()` |  |
| 277 | 27 | `static void familyHostReservation()` |  |
| 304 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 336 | 60 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 396 | 7 | `public static void main(String[] args) throws Exception` |  |
| 403 | 9 | `static void smallProfile(StringBuilder report)` |  |


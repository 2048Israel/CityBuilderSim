# HousingMarketCheck.java - 522 lines · 23 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [Game](Game.md) (36), [HousingChoice](HousingChoice.md) (34), [FamilyStructure](FamilyStructure.md) (22), [PayTier](PayTier.md) (21), [HousingMarket](HousingMarket.md) (19), [FamilyModel](FamilyModel.md) (16), [HousingForms](HousingForms.md) (14), [GameFiles](GameFiles.md) (14), [LongPlaytest](LongPlaytest.md) (13), [AgeBand](AgeBand.md) (13), [WorkingHousehold](WorkingHousehold.md) (9), [RealEstate](RealEstate.md) (8), [PopulationCohorts](PopulationCohorts.md) (6), [HousingInventory](HousingInventory.md) (5), [AdultStage](AdultStage.md) (4), [JobType](JobType.md) (4), [HousingRecipe](HousingRecipe.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [PriceLevel](PriceLevel.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Household](Household.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [StudentHousehold](StudentHousehold.md) (2), [Sectors](Sectors.md) (2), [Bank](Bank.md) (1), [CardCheck](CardCheck.md) (1), [WageBand](WageBand.md) (1), [Founding](Founding.md) (1), [ConstructionControl](ConstructionControl.md) (1)

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
| 13 | 510 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 65 | 48 | `static void classPrices()` |  |
| 113 | 11 | `static void foundingBudgets()` |  |
| 124 | 13 | `static void fractionalPressure()` |  |
| 137 | 44 | `static void sharingOrder()` |  |
| 181 | 26 | `static void doubledDoors()` |  |
| 207 | 13 | `static void studentFacility()` |  |
| 220 | 43 | `static void fundedHost()` |  |
| 263 | 5 | `static double[] cardPositions(HouseholdBalance balances)` |  |
| 268 | 15 | `static void retiredRoom()` |  |
| 283 | 4 | `static void holdHousingFixture(Game g)` |  |
| 287 | 59 | `static void loadedDoorCensus() throws Exception` |  |
| 346 | 19 | `static void cardHousingBudget()` |  |
| 365 | 22 | `static void aggregatePackingRemainder()` |  |
| 387 | 27 | `static void familyHostReservation()` |  |
| 414 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 446 | 60 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 506 | 7 | `public static void main(String[] args) throws Exception` |  |
| 513 | 9 | `static void smallProfile(StringBuilder report)` |  |


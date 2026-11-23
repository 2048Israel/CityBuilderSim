# HousingMarketCheck.java - 547 lines · 24 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [Game](Game.md) (38), [HousingChoice](HousingChoice.md) (35), [PayTier](PayTier.md) (23), [FamilyStructure](FamilyStructure.md) (23), [HousingMarket](HousingMarket.md) (19), [HousingForms](HousingForms.md) (16), [FamilyModel](FamilyModel.md) (16), [GameFiles](GameFiles.md) (15), [AgeBand](AgeBand.md) (15), [LongPlaytest](LongPlaytest.md) (13), [WorkingHousehold](WorkingHousehold.md) (11), [RealEstate](RealEstate.md) (9), [PopulationCohorts](PopulationCohorts.md) (7), [HousingInventory](HousingInventory.md) (5), [AdultStage](AdultStage.md) (5), [JobType](JobType.md) (4), [HousingRecipe](HousingRecipe.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [PriceLevel](PriceLevel.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Household](Household.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [StudentHousehold](StudentHousehold.md) (2), [Sectors](Sectors.md) (2), [Bank](Bank.md) (1), [CardCheck](CardCheck.md) (1), [WageBand](WageBand.md) (1), [Founding](Founding.md) (1), [ConstructionControl](ConstructionControl.md) (1)

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
| 13 | 535 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 65 | 48 | `static void classPrices()` |  |
| 113 | 11 | `static void foundingBudgets()` |  |
| 124 | 13 | `static void fractionalPressure()` |  |
| 137 | 44 | `static void sharingOrder()` |  |
| 181 | 28 | `static void doubledDoors()` |  |
| 209 | 23 | `static void doubledDemand()` |  |
| 232 | 13 | `static void studentFacility()` |  |
| 245 | 43 | `static void fundedHost()` |  |
| 288 | 5 | `static double[] cardPositions(HouseholdBalance balances)` |  |
| 293 | 15 | `static void retiredRoom()` |  |
| 308 | 4 | `static void holdHousingFixture(Game g)` |  |
| 312 | 59 | `static void loadedDoorCensus() throws Exception` |  |
| 371 | 19 | `static void cardHousingBudget()` |  |
| 390 | 22 | `static void aggregatePackingRemainder()` |  |
| 412 | 27 | `static void familyHostReservation()` |  |
| 439 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 471 | 60 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 531 | 7 | `public static void main(String[] args) throws Exception` |  |
| 538 | 9 | `static void smallProfile(StringBuilder report)` |  |


# HousingMarketCheck.java - 627 lines · 28 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [HousingChoice](HousingChoice.md) (48), [Game](Game.md) (42), [HousingMarket](HousingMarket.md) (25), [PayTier](PayTier.md) (23), [FamilyStructure](FamilyStructure.md) (23), [HousingForms](HousingForms.md) (19), [GameFiles](GameFiles.md) (17), [FamilyModel](FamilyModel.md) (16), [AgeBand](AgeBand.md) (15), [LongPlaytest](LongPlaytest.md) (13), [RealEstate](RealEstate.md) (11), [WorkingHousehold](WorkingHousehold.md) (11), [HousingInventory](HousingInventory.md) (8), [PopulationCohorts](PopulationCohorts.md) (7), [AdultStage](AdultStage.md) (5), [JobType](JobType.md) (4), [MoneyAudit](MoneyAudit.md) (3), [HousingRecipe](HousingRecipe.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [PriceLevel](PriceLevel.md) (2), [Household](Household.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [StudentHousehold](StudentHousehold.md) (2), [Sectors](Sectors.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [Bank](Bank.md) (1), [CardCheck](CardCheck.md) (1), [WageBand](WageBand.md) (1), [Founding](Founding.md) (1), [ConstructionControl](ConstructionControl.md) (1)

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
| 13 | 615 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 66 | 11 | `static HousingMarket.State mixedSharingPrices(double singleBudget)` | Five old shared households at twenty, joined by five groups of five singles, preserve their actual resources. |
| 77 | 13 | `static void mergedBudgetPressure()` |  |
| 90 | 30 | `static void plannerInputs()` |  |
| 120 | 48 | `static void classPrices()` |  |
| 168 | 11 | `static void foundingBudgets()` |  |
| 179 | 13 | `static void fractionalPressure()` |  |
| 192 | 44 | `static void sharingOrder()` |  |
| 236 | 28 | `static void doubledDoors()` |  |
| 264 | 23 | `static void doubledDemand()` |  |
| 287 | 13 | `static void studentFacility()` |  |
| 300 | 25 | `static void scarceFreeHost()` |  |
| 325 | 43 | `static void fundedHost()` |  |
| 368 | 5 | `static double[] cardPositions(HouseholdBalance balances)` |  |
| 373 | 15 | `static void retiredRoom()` |  |
| 388 | 4 | `static void holdHousingFixture(Game g)` |  |
| 392 | 59 | `static void loadedDoorCensus() throws Exception` |  |
| 451 | 19 | `static void cardHousingBudget()` |  |
| 470 | 22 | `static void aggregatePackingRemainder()` |  |
| 492 | 27 | `static void familyHostReservation()` |  |
| 519 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 551 | 60 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 611 | 7 | `public static void main(String[] args) throws Exception` |  |
| 618 | 9 | `static void smallProfile(StringBuilder report)` |  |


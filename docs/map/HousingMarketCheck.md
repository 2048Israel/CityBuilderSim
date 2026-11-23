# HousingMarketCheck.java - 701 lines · 30 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [HousingChoice](HousingChoice.md) (62), [Game](Game.md) (46), [HousingMarket](HousingMarket.md) (29), [PayTier](PayTier.md) (25), [FamilyStructure](FamilyStructure.md) (24), [HousingForms](HousingForms.md) (19), [GameFiles](GameFiles.md) (19), [AgeBand](AgeBand.md) (17), [FamilyModel](FamilyModel.md) (16), [HousingInventory](HousingInventory.md) (15), [LongPlaytest](LongPlaytest.md) (13), [WorkingHousehold](WorkingHousehold.md) (12), [RealEstate](RealEstate.md) (11), [PopulationCohorts](PopulationCohorts.md) (8), [HouseholdBalance](HouseholdBalance.md) (6), [AdultStage](AdultStage.md) (5), [JobType](JobType.md) (4), [MoneyAudit](MoneyAudit.md) (3), [HousingRecipe](HousingRecipe.md) (3), [PriceLevel](PriceLevel.md) (2), [Household](Household.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (2), [StudentHousehold](StudentHousehold.md) (2), [Sectors](Sectors.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [Bank](Bank.md) (1), [CardCheck](CardCheck.md) (1), [WageBand](WageBand.md) (1), [Founding](Founding.md) (1), [ConstructionControl](ConstructionControl.md) (1)

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
| 13 | 689 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 66 | 11 | `static HousingMarket.State mixedSharingPrices(double singleBudget)` | Five old shared households at twenty, joined by five groups of five singles, preserve their actual resources. |
| 77 | 13 | `static void mergedBudgetPressure()` |  |
| 90 | 44 | `static void rentPriceIndex()` |  |
| 134 | 30 | `static void plannerInputs()` |  |
| 164 | 48 | `static void classPrices()` |  |
| 212 | 11 | `static void foundingBudgets()` |  |
| 223 | 13 | `static void fractionalPressure()` |  |
| 236 | 44 | `static void sharingOrder()` |  |
| 280 | 28 | `static void doubledDoors()` |  |
| 308 | 23 | `static void doubledDemand()` |  |
| 331 | 13 | `static void studentFacility()` |  |
| 344 | 25 | `static void scarceFreeHost()` |  |
| 369 | 43 | `static void fundedHost()` |  |
| 412 | 5 | `static double[] cardPositions(HouseholdBalance balances)` |  |
| 417 | 15 | `static void retiredRoom()` |  |
| 432 | 4 | `static void holdHousingFixture(Game g)` |  |
| 436 | 59 | `static void loadedDoorCensus() throws Exception` |  |
| 495 | 30 | `static void savingHousingBudget()` |  |
| 525 | 19 | `static void cardHousingBudget()` |  |
| 544 | 22 | `static void aggregatePackingRemainder()` |  |
| 566 | 27 | `static void familyHostReservation()` |  |
| 593 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 625 | 60 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 685 | 7 | `public static void main(String[] args) throws Exception` |  |
| 692 | 9 | `static void smallProfile(StringBuilder report)` |  |


# HousingMarketCheck.java - 1,691 lines · 54 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [HousingChoice](HousingChoice.md) (189), [Game](Game.md) (92), [PayTier](PayTier.md) (41), [FamilyStructure](FamilyStructure.md) (41), [HousingMarket](HousingMarket.md) (38), [HousingInventory](HousingInventory.md) (36), [GameFiles](GameFiles.md) (34), [FamilyModel](FamilyModel.md) (30), [Household](Household.md) (30), [AgeBand](AgeBand.md) (24), [HouseholdBalance](HouseholdBalance.md) (23), [HousingForms](HousingForms.md) (22), [Sectors](Sectors.md) (20), [WorkingHousehold](WorkingHousehold.md) (17), [RealEstate](RealEstate.md) (14), [LongPlaytest](LongPlaytest.md) (13), [PopulationCohorts](PopulationCohorts.md) (11), [MoneyAudit](MoneyAudit.md) (10), [HouseholdAccounts](HouseholdAccounts.md) (10), [Founding](Founding.md) (9), [AdultStage](AdultStage.md) (8), [Mortgage](Mortgage.md) (7), [UnemployedHousehold](UnemployedHousehold.md) (7), [JobType](JobType.md) (6), [TaxPolicy](TaxPolicy.md) (5), [Equity](Equity.md) (4), [HousingRecipe](HousingRecipe.md) (3), [StudentHousehold](StudentHousehold.md) (3), [PriceLevel](PriceLevel.md) (2), [BusinessInvestment](BusinessInvestment.md) (2)... and 8 more

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
| 13 | 1679 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 66 | 11 | `static HousingMarket.State mixedSharingPrices(double singleBudget)` | Five old shared households at twenty, joined by five groups of five singles, preserve their actual resources. |
| 77 | 13 | `static void mergedBudgetPressure()` |  |
| 90 | 44 | `static void rentPriceIndex()` |  |
| 134 | 37 | `static void plannerInputs()` |  |
| 171 | 90 | `static void netOperatingChoice() throws Exception` |  |
| 261 | 48 | `static void classPrices()` |  |
| 309 | 34 | `static void unbuiltGrossQuotes()` |  |
| 343 | 81 | `static void fractionalSettlement()` |  |
| 424 | 57 | `static void fractionalClaims()` |  |
| 481 | 11 | `static void foundingBudgets()` |  |
| 492 | 14 | `static void fractionalPressure()` |  |
| 506 | 44 | `static void sharingOrder()` |  |
| 550 | 28 | `static void doubledDoors()` |  |
| 578 | 23 | `static void doubledDemand()` |  |
| 601 | 13 | `static void studentFacility()` |  |
| 614 | 25 | `static void scarceFreeHost()` |  |
| 639 | 43 | `static void fundedHost()` |  |
| 682 | 5 | `static double[] cardPositions(HouseholdBalance balances)` |  |
| 687 | 15 | `static void retiredRoom()` |  |
| 702 | 4 | `static void holdHousingFixture(Game g)` |  |
| 706 | 61 | `static void loadedDoorCensus() throws Exception` |  |
| 768 | 175 | `static void causedStrandedDebtContinuation() throws Exception` | A paid Bank mortgage, vacant-title closure, then the real capacity/share writer and native twins. |
| 944 | 30 | `static void savingHousingBudget()` |  |
| 974 | 19 | `static void cardHousingBudget()` |  |
| 993 | 22 | `static void aggregatePackingRemainder()` |  |
| 1015 | 27 | `static void familyHostReservation()` |  |
| 1042 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 1074 | 74 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 1148 | 26 | `static void retainedFundedGuest()` |  |
| 1174 | 54 | `static void stagedHousingStocks()` |  |
| 1230 | 8 | `static HouseholdAccounts paidHousingRow(int row, double people, double income, double fees)` | An actually paid non-wage row and its billed source population, before the next census. |
| 1238 | 35 | `static void stagedHousingFlows()` |  |
| 1273 | 34 | `static void carriedStudentService()` |  |
| 1308 | 30 | `static void rentCommitmentBudgets()` |  |
| 1340 | 42 | `static void independentDoorPressure()` |  |
| 1383 | 39 | `static void committedDoorBoundaries()` |  |
| 1423 | 1 | `static HousingInventory.Key lateHome()` |  |
| 1424 | 1 | `static HousingChoice.Demand lateDemand(String key, double count, double budget)` |  |
| 1425 | 20 | `static HousingMarket lateFixture(String label, int stock, List<HousingChoice.Demand> current, List<HousingChoice.Demand> previo...` |  |
| 1445 | 43 | `static void latePaidFootprints()` |  |
| 1490 | 1 | **type** `private record FundingFixture(Game game, HousingMarket market, BuildingsTemplate house, BuildingsTemplate s...` |  |
| 1491 | 3 | `static HousingChoice.Demand fundingDemand(String key, double count, boolean retired, double budget)` |  |
| 1494 | 24 | `static FundingFixture fundingFixture(String label, List<HousingChoice.Demand> demand, boolean pair, boolean cheap)` |  |
| 1518 | 24 | `static void fundedPressureAndForecast()` |  |
| 1543 | 29 | `static void coalescedSponsored()` |  |
| 1572 | 43 | `static void sharingForecastSources()` |  |
| 1615 | 22 | `static void crossClassPaidExcess()` |  |
| 1637 | 35 | `static void savedContractModes() throws Exception` |  |
| 1672 | 10 | `public static void main(String[] args) throws Exception` |  |
| 1682 | 9 | `static void smallProfile(StringBuilder report)` |  |


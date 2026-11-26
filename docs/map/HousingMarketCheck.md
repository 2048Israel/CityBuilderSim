# HousingMarketCheck.java - 2,044 lines · 68 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [HousingChoice](HousingChoice.md) (213), [Game](Game.md) (112), [FamilyStructure](FamilyStructure.md) (47), [HousingMarket](HousingMarket.md) (45), [PayTier](PayTier.md) (44), [HousingInventory](HousingInventory.md) (38), [HouseholdBalance](HouseholdBalance.md) (36), [GameFiles](GameFiles.md) (35), [Household](Household.md) (33), [FamilyModel](FamilyModel.md) (31), [HousingForms](HousingForms.md) (24), [AgeBand](AgeBand.md) (24), [Sectors](Sectors.md) (20), [WorkingHousehold](WorkingHousehold.md) (18), [RealEstate](RealEstate.md) (15), [LongPlaytest](LongPlaytest.md) (15), [PopulationCohorts](PopulationCohorts.md) (11), [MoneyAudit](MoneyAudit.md) (10), [HouseholdAccounts](HouseholdAccounts.md) (10), [Founding](Founding.md) (9), [AdultStage](AdultStage.md) (8), [Mortgage](Mortgage.md) (7), [UnemployedHousehold](UnemployedHousehold.md) (7), [TaxPolicy](TaxPolicy.md) (6), [JobType](JobType.md) (6), [Equity](Equity.md) (4), [HousingRecipe](HousingRecipe.md) (3), [StudentHousehold](StudentHousehold.md) (3), [PriceLevel](PriceLevel.md) (2), [BusinessInvestment](BusinessInvestment.md) (2)... and 8 more

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
| 13 | 2032 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
| 19 | 1 | `static void check(String label, boolean ok)` |  |
| 20 | 1 | `static void near(String label, double a, double b)` |  |
| 21 | 5 | `static void audits(Game g)` |  |
| 26 | 27 | `static void classes(Game g)` |  |
| 53 | 12 | `static HousingMarket.State priceInputs(double studioCarry)` |  |
| 66 | 11 | `static HousingMarket.State mixedSharingPrices(double singleBudget)` | Five old shared households at twenty, joined by five groups of five singles, preserve their actual resources. |
| 77 | 13 | `static void mergedBudgetPressure()` |  |
| 90 | 44 | `static void rentPriceIndex()` |  |
| 134 | 37 | `static void plannerInputs()` |  |
| 171 | 109 | `static void netOperatingChoice() throws Exception` |  |
| 280 | 48 | `static void classPrices()` |  |
| 328 | 34 | `static void unbuiltGrossQuotes()` |  |
| 362 | 81 | `static void fractionalSettlement()` |  |
| 443 | 57 | `static void fractionalClaims()` |  |
| 502 | 28 | `static void fractionalDividends()` | REV-RES3z: a cell under EMPTY_CELL keeps its shares, so its dividend and lending fee are paid to it at home, where Equity books as abroad what creditDividend() does not pay and Exchange pays the cells only what credit... |
| 530 | 11 | `static void foundingBudgets()` |  |
| 541 | 14 | `static void fractionalPressure()` |  |
| 555 | 44 | `static void sharingOrder()` |  |
| 599 | 32 | `static void doubledDoors()` |  |
| 631 | 23 | `static void doubledDemand()` |  |
| 654 | 13 | `static void studentFacility()` |  |
| 667 | 35 | `static void scarceFreeHost()` |  |
| 702 | 45 | `static void fundedHost()` |  |
| 747 | 5 | `static double[] cardPositions(HouseholdBalance balances)` |  |
| 752 | 15 | `static void retiredRoom()` |  |
| 767 | 4 | `static void holdHousingFixture(Game g)` |  |
| 771 | 66 | `static void loadedDoorCensus() throws Exception` |  |
| 838 | 175 | `static void causedStrandedDebtContinuation() throws Exception` | A paid Bank mortgage, vacant-title closure, then the real capacity/share writer and native twins. |
| 1014 | 30 | `static void savingHousingBudget()` |  |
| 1044 | 19 | `static void cardHousingBudget()` |  |
| 1063 | 33 | `static void aggregatePackingRemainder()` |  |
| 1096 | 36 | `static void familyHostReservation()` |  |
| 1133 | 9 | `static boolean withinLine(HousingMarket market, HousingChoice.Result result)` | Every class's occupied doors hold at most their census line of people (HousingChoice.line, RES3c). |
| 1142 | 32 | `static void writeReport(StringBuilder report, Game g, String label, Map<Integer, Long> starts, Map<Integer, Long> completions, ...` |  |
| 1174 | 74 | `static void city(StringBuilder report, boolean historical) throws Exception` |  |
| 1248 | 34 | `static void retainedFundedGuest()` |  |
| 1283 | 9 | `static Game oneHouseTown(String scratch)` | A founded town a year on for its household cells, its homes taken down and one House left: the only door standing. |
| 1292 | 1 | `static double held(Household c)` |  |
| 1300 | 47 | `static void boardOwedAtTheClearing()` | REV-RES3b P1-1 and P1-4: a boarder owes its host board, half the door's ask, billed to no landlord, and the clearing moves no money (the next month's settlement bills it with the rents, boardWithTheRents()); and the c... |
| 1348 | 4 | `static Game twin(Game g)` | A loaded twin of a game saved to slot 10 of its scratch folder. |
| 1353 | 4 | `static void play(Game g, int months)` | The game's own months. |
| 1357 | 4 | `static void saveTen(Game g)` |  |
| 1361 | 1 | `static HousingMarket marketOf(Game g)` |  |
| 1363 | 5 | `static boolean audited(Game g)` | The month's two audits closed, the pools' and the households' own, as HousingDoorCheck reads them. |
| 1369 | 4 | `static double gap(Map<String, Double> a, Map<String, Double> b)` | The largest gap between two maps of money, key by key (a key one lacks reads zero). |
| 1386 | 70 | `static void boardWithTheRents()` | REV-RES3b on 0925f4661: the board goes the rents' own way, through the game's own months. |
| 1462 | 32 | `static void boardThroughAReform()` | REV-RES3b on 19fcb37f0, P1 2, played through (REV-RES3b on 0925f4661: its fixture settled the original city, not the loaded one). |
| 1500 | 21 | `static void trimHeldToStock()` | REV-RES3b P1-3: the trim to the standing stock (HousingMarket.holdToStock(), overcrowding()'s) cannot throw. |
| 1521 | 54 | `static void stagedHousingStocks()` |  |
| 1577 | 8 | `static HouseholdAccounts paidHousingRow(int row, double people, double income, double fees)` | An actually paid non-wage row and its billed source population, before the next census. |
| 1585 | 35 | `static void stagedHousingFlows()` |  |
| 1620 | 34 | `static void carriedStudentService()` |  |
| 1655 | 30 | `static void rentCommitmentBudgets()` |  |
| 1687 | 48 | `static void independentDoorPressure()` |  |
| 1736 | 39 | `static void committedDoorBoundaries()` |  |
| 1776 | 1 | `static HousingInventory.Key lateHome()` |  |
| 1777 | 1 | `static HousingChoice.Demand lateDemand(String key, double count, double budget)` |  |
| 1778 | 20 | `static HousingMarket lateFixture(String label, int stock, List<HousingChoice.Demand> current, List<HousingChoice.Demand> previo...` |  |
| 1798 | 43 | `static void latePaidFootprints()` |  |
| 1843 | 1 | **type** `private record FundingFixture(Game game, HousingMarket market, BuildingsTemplate house, BuildingsTemplate s...` |  |
| 1844 | 3 | `static HousingChoice.Demand fundingDemand(String key, double count, boolean retired, double budget)` |  |
| 1847 | 24 | `static FundingFixture fundingFixture(String label, List<HousingChoice.Demand> demand, boolean pair, boolean cheap)` |  |
| 1871 | 24 | `static void fundedPressureAndForecast()` |  |
| 1896 | 29 | `static void coalescedSponsored()` |  |
| 1925 | 43 | `static void sharingForecastSources()` |  |
| 1968 | 22 | `static void crossClassPaidExcess()` |  |
| 1990 | 35 | `static void savedContractModes() throws Exception` |  |
| 2025 | 10 | `public static void main(String[] args) throws Exception` |  |
| 2035 | 9 | `static void smallProfile(StringBuilder report)` |  |


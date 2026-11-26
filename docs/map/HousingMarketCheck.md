# HousingMarketCheck.java - 2,006 lines · 64 methods · 2 constants · harnesses

`ham/citybuildersim/HousingMarketCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> Integrated quotes, funded sites, rental receipts, budgets and reload evidence.
> WHY (RES3): a synthetic catalogue quote does not prove an actual funded project
> or a month whose occupied rents and household cash reconcile.

**Uses:** [HousingChoice](HousingChoice.md) (217), [Game](Game.md) (101), [FamilyStructure](FamilyStructure.md) (47), [HousingMarket](HousingMarket.md) (44), [PayTier](PayTier.md) (44), [Household](Household.md) (41), [HousingInventory](HousingInventory.md) (38), [GameFiles](GameFiles.md) (35), [FamilyModel](FamilyModel.md) (31), [HouseholdBalance](HouseholdBalance.md) (26), [HousingForms](HousingForms.md) (24), [AgeBand](AgeBand.md) (24), [Sectors](Sectors.md) (20), [WorkingHousehold](WorkingHousehold.md) (18), [RealEstate](RealEstate.md) (15), [LongPlaytest](LongPlaytest.md) (14), [PopulationCohorts](PopulationCohorts.md) (11), [MoneyAudit](MoneyAudit.md) (10), [HouseholdAccounts](HouseholdAccounts.md) (10), [Founding](Founding.md) (9), [AdultStage](AdultStage.md) (8), [Mortgage](Mortgage.md) (7), [UnemployedHousehold](UnemployedHousehold.md) (7), [TaxPolicy](TaxPolicy.md) (6), [JobType](JobType.md) (6), [Equity](Equity.md) (4), [HousingRecipe](HousingRecipe.md) (3), [StudentHousehold](StudentHousehold.md) (3), [PriceLevel](PriceLevel.md) (2), [BusinessInvestment](BusinessInvestment.md) (2)... and 8 more

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
| 13 | 1994 | **type** `public final class HousingMarketCheck` | Integrated quotes, funded sites, rental receipts, budgets and reload evidence. |
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
| 1293 | 5 | `static List<HousingChoice.Demand> boarded(Household host, String boarder)` | A host family of two and its funded guest sitting in the one House since last month, and a boarder of one beside them. |
| 1299 | 8 | `static double boardOn(Game g, Household host, String boarder)` | The clearing that boards it, the host sitting from the month before; what the boarder's cell owes its host. |
| 1307 | 1 | `static double held(Household c)` |  |
| 1316 | 55 | `static void boardBookedBothSides()` | REV-RES3b P1-1 and P1-4: a boarder pays its host board out of its own cell's savings into the host cell's, every cent, half the door's ask, billed to no landlord; and the census's own line on the door says whether it ... |
| 1380 | 38 | `static void boardOnTheLeasesCensus()` | REV-RES3b on 19fcb37f0, P1 1: the board is settled on the census the clearing struck its leases on, the next month's, once the stock has followed the people (HousingMarket.settleBoard(), HouseholdBalance.advanceMonth(... |
| 1423 | 33 | `static void boardThroughAReform()` | REV-RES3b on 19fcb37f0, P1 2: a currency reform scales the board as it scales the savings it moves between and the lease bills beside it (HousingMarket.redenominate()), the board owed for the next settlement and the m... |
| 1462 | 21 | `static void trimHeldToStock()` | REV-RES3b P1-3: the trim to the standing stock (HousingMarket.holdToStock(), overcrowding()'s) cannot throw. |
| 1483 | 54 | `static void stagedHousingStocks()` |  |
| 1539 | 8 | `static HouseholdAccounts paidHousingRow(int row, double people, double income, double fees)` | An actually paid non-wage row and its billed source population, before the next census. |
| 1547 | 35 | `static void stagedHousingFlows()` |  |
| 1582 | 34 | `static void carriedStudentService()` |  |
| 1617 | 30 | `static void rentCommitmentBudgets()` |  |
| 1649 | 48 | `static void independentDoorPressure()` |  |
| 1698 | 39 | `static void committedDoorBoundaries()` |  |
| 1738 | 1 | `static HousingInventory.Key lateHome()` |  |
| 1739 | 1 | `static HousingChoice.Demand lateDemand(String key, double count, double budget)` |  |
| 1740 | 20 | `static HousingMarket lateFixture(String label, int stock, List<HousingChoice.Demand> current, List<HousingChoice.Demand> previo...` |  |
| 1760 | 43 | `static void latePaidFootprints()` |  |
| 1805 | 1 | **type** `private record FundingFixture(Game game, HousingMarket market, BuildingsTemplate house, BuildingsTemplate s...` |  |
| 1806 | 3 | `static HousingChoice.Demand fundingDemand(String key, double count, boolean retired, double budget)` |  |
| 1809 | 24 | `static FundingFixture fundingFixture(String label, List<HousingChoice.Demand> demand, boolean pair, boolean cheap)` |  |
| 1833 | 24 | `static void fundedPressureAndForecast()` |  |
| 1858 | 29 | `static void coalescedSponsored()` |  |
| 1887 | 43 | `static void sharingForecastSources()` |  |
| 1930 | 22 | `static void crossClassPaidExcess()` |  |
| 1952 | 35 | `static void savedContractModes() throws Exception` |  |
| 1987 | 10 | `public static void main(String[] args) throws Exception` |  |
| 1997 | 9 | `static void smallProfile(StringBuilder report)` |  |


# HouseholdAccounts.java - 976 lines · 76 methods · 9 constants · model

`ham/citybuildersim/HouseholdAccounts.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The city's residents, treated as one household.
> 
> Every other participant in this economy had books before this: shops, mills,
> landlords, builders, the utilities, the city itself. The people did not, even
> though they are the largest single flow in the game - they earn every wage
> paid and they are the customer at the other end of every rent cheque and
> every till.
> 
> WHERE THE NUMBERS COME FROM
> 
> Nothing here is estimated or re-derived. Wages are the figure PopulationManager
> already computes for filled jobs; the wage tax is what the city already
> collects; rent and shopping are the two halves of consumption that
> NationalAccounts already measures, because the money households spend IS
> consumption. Building this account is a matter of putting existing figures on
> the other side of the ledger from the businesses that receive them.
> 
> WHAT IT IS FOR
> 
> It answers a question nothing else in the game could: can the people afford
> to live here? Retail spending is currently driven by how many residents there
> are rather than by what they earn - there is no budget constraint anywhere in
> the model - so households CAN be made to spend more than they take home, and
> that shortfall would be money appearing from nowhere. Now it is a visible
> negative line rather than an invisible one.
> 
> AND NOW, SEVEN SETS OF BOOKS
> 
> The note that used to sit here said splitting residents into income groups was
> "the obvious next step" and "a long way off". It is here: the city total, plus
> one statement per pay tier and one for the retired, who have no tier because
> they have no earner.
> 
> NOTHING IN THE SPLIT IS ESTIMATED, which is the only reason it is worth having.
> Wages come from the job mix by tier. The tax is the SAME banded calculation the
> city collects, split rather than re-derived. Rent is charged per resident at a
> uniform price, and shopping is driven by headcount, so allocating both by the
> people living in each tier's households is exact arithmetic rather than an
> apportionment.
> 
> That last point is also the finding. Income across the tiers varies about
> tenfold; rent and shopping per head do not vary at all, because nothing in the
> model lets what a household earns affect what it spends. So the poorest tier
> runs a deficit and the richest banks almost everything, and both are artefacts
> of a missing budget constraint rather than results. The screen says so.

**Uses:** [Household](Household.md) (10), [PayTier](PayTier.md) (7), [FamilyModel](FamilyModel.md) (6), [FamilyStructure](FamilyStructure.md) (5), [Statement](Statement.md) (3), [AgeBand](AgeBand.md) (2), [SocialSecurity](SocialSecurity.md) (1)

**Used by (8):** [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 52 | · this month |
| 106 | THE FARE (2026-09-16) |
| 250 | · the statement |
| 322 | · per head |
| 351 | THE SAME STATEMENT, PER PAY TIER |
| 582 | ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER |
| 754 | THE MONTH'S STATEMENT, CARRIED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 367 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 370 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 371 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 372 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 374 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 375 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 378 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 801 | `HouseholdAccounts.STATE_SCALARS` | `16, STATE_ROWS = 15` | Scalars and row arrays in the statement's state since 2026-09-16. |
| 804 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `private double wages` |  |
| 54 | `private double wageTax` |  |
| 55 | `private double rent` |  |
| 56 | `private double shopping` |  |
| 67 | `private double contributions` | Pension contributions off the workers, and pensions in to the retired. |
| 68 | `private double pensions` |  |
| 76 | `private double eiPremiums` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11): the EI premium off every wage, the EI benefit to the out of work, and the grant to the students. |
| 77 | `private double eiBenefits` |  |
| 78 | `private double studentGrants` |  |
| 102 | `private double healthcare` | What the people paid the healthcare service this month. |
| 103 | `private double tuition` |  |
| 104 | `private double interest` |  |
| 136 | `private double fares` |  |
| 145 | `private int population` |  |
| 146 | `private int workforce` |  |
| 147 | `private int jobsFilled` |  |
| 158 | `private double cumulativeSaving` | Everything households have not spent, accumulated. |
| 380 | `private final double[] rowWages` |  |
| 381 | `private final double[] rowTax` |  |
| 382 | `private final double[] rowRent` |  |
| 383 | `private final double[] rowShopping` |  |
| 384 | `private final double[] rowPeople` |  |
| 385 | `private final double[] rowHouseholds` |  |
| 386 | `private final double[] rowContributions` |  |
| 387 | `private final double[] rowPensions` |  |
| 388 | `private final double[] rowHealthcare` |  |
| 389 | `private final double[] rowTuition` |  |
| 390 | `private final double[] rowFares` |  |
| 391 | `private final double[] rowInterest` |  |
| 392 | `private final double[] rowEiPremiums` |  |
| 398 | `private final double[] rowDoors` | Doors each row's households pay for: one a household, a fifth sharing, none with no home. |
| 400 | `private final double[] rowBenefits` | EI to the out of work, grants to the students. |
| 665 | `private double pensionPerSenior` | What one pensioner receives, as the policy currently sets it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 927 | **type** `public class HouseholdAccounts` | The city's residents, treated as one household. |

### this month (lines 52-105)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 5 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants)` | The month's EI and grants, from the same figures the treasury books - set before update() or refresh(), which leave them as they are. |
| 90 | 1 | `public double getEiPremiums()` |  |
| 91 | 1 | `public double getEiBenefits()` |  |
| 92 | 1 | `public double getStudentGrants()` |  |

### THE FARE (2026-09-16) (lines 106-249)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 3 | `public void setTransitFares(double paid)` | What the city took in fares this month, told to the households who paid it. |
| 143 | 1 | `public double getFares()` |  |
| 161 | 4 | `public void update(double wages, double wageTax, double rent, double shopping, int population, int workforce, int jobsFilled)` | Feed it the month. |
| 166 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, int popul...` |  |
| 173 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` |  |
| 185 | 9 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` | same figure Education collects, not a second copy of it |
| 207 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, int popu...` | The same figures, WITHOUT adding a month to the running total. |
| 214 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 221 | 8 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 230 | 19 | `private void assign(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |

### the statement (lines 250-321)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 1 | `public double getWages()` |  |
| 253 | 1 | `public double getWageTax()` |  |
| 254 | 1 | `public double getRent()` |  |
| 255 | 1 | `public double getShopping()` |  |
| 256 | 1 | `public double getContributions()` |  |
| 257 | 1 | `public double getPensions()` |  |
| 258 | 1 | `public double getHealthcare()` |  |
| 259 | 1 | `public double getTuition()` |  |
| 260 | 1 | `public double getInterest()` |  |
| 269 | 3 | `public double getDisposableIncome()` | What the people actually have to spend after the city has taken its share. |
| 282 | 3 | `public double getSpending()` | Everything that leaves a household in a month. |
| 287 | 3 | `public double getNetSaving()` | Income less tax less everything paid out. |
| 291 | 3 | `public double getCumulativeSaving()` |  |
| 296 | 3 | `public void setCumulativeSaving(double value)` | For the load path. |
| 307 | 4 | `public double getSavingRate()` | Saving as a share of take-home pay. |
| 313 | 4 | `public double getRentBurden()` | Rent as a share of take-home. |
| 318 | 3 | `public double getEffectiveTaxRate()` |  |

### per head (lines 322-350)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 3 | `public double getIncomePerResident()` |  |
| 328 | 3 | `public double getSpendingPerResident()` |  |
| 333 | 3 | `public double getAverageWage()` | What a filled job pays on average. |
| 338 | 3 | `public double getDependencyRatio()` | How many people each working resident is carrying, themselves included. |
| 342 | 1 | `public int getPopulation()` |  |
| 343 | 1 | `public int getWorkforce()` |  |
| 344 | 1 | `public int getJobsFilled()` |  |
| 347 | 3 | `public boolean isLivingBeyondIncome()` | True when the people are being made to spend more than they earn. |

### THE SAME STATEMENT, PER PAY TIER (lines 351-581)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 4 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow)` | Splits the month across the tiers. |
| 438 | 6 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | HouseholdBalance - who could AFFORD to shop, not who was hungry. |
| 449 | 132 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | null for one door a household, as before |

### ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER (lines 582-753)

| line | len | member | says |
|---:|---:|---|---|
| 599 | 3 | **type** `public record Statement(double households, double people, double income, double tax, double rent, double fe...` | One household's month. |
| 604 | 6 | `public double rentPerHousehold()` | Rent one let home pays, whoever lives in it. |
| 611 | 1 | `public double getRowDoors(int row)` |  |
| 633 | 23 | `public double[] livingAlonePressure(FamilyModel families)` | How badly a single adult in each tier cannot afford to live alone, 0-1. |
| 658 | 5 | `public double feesPerHead()` | Healthcare and tuition, per person. |
| 666 | 1 | `public void setPensionPerSenior(double value)` |  |
| 667 | 1 | `public double getPensionPerSenior()` |  |
| 670 | 5 | `public double shoppingPerHead()` | The weekly shop, per person, which is how retail demand is counted. |
| 690 | 14 | `public double[] seekerPressure(double[] households)` | How badly one of each group living outside the families cannot afford a door of their own, 0-1 - livingAlonePressure()'s arithmetic on what they live on: EI for the out of work (nothing past the twelfth month), the gr... |
| 705 | 39 | `public Statement statementFor(FamilyModel families, FamilyStructure shape, PayTier tier)` |  |
| 745 | 1 | `public double getRowWages(int row)` |  |
| 746 | 1 | `public double getRowTax(int row)` |  |
| 747 | 1 | `public double getRowRent(int row)` |  |
| 748 | 1 | `public double getRowShopping(int row)` |  |
| 749 | 1 | `public double getRowFares(int row)` |  |
| 750 | 1 | `public double getRowPeople(int row)` |  |
| 751 | 1 | `public double getRowHouseholds(int row)` |  |
| 752 | 1 | `public int getRowCount()` |  |

### THE MONTH'S STATEMENT, CARRIED (lines 754-976)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 21 | `public double[] getStatementState()` |  |
| 812 | 63 | `public boolean restoreStatement(double[] in)` | Puts a saved statement back, exactly as it was written. |
| 877 | 7 | `private static double[] padOlder(double[] rows)` | A row array from an older caller, padded to today's rows; anything else as it came. |
| 885 | 1 | `public double getRowContributions(int row)` |  |
| 886 | 1 | `public double getRowPensions(int row)` |  |
| 887 | 1 | `public double getRowHealthcare(int row)` |  |
| 888 | 1 | `public double getRowTuition(int row)` |  |
| 889 | 1 | `public double getRowInterest(int row)` |  |
| 890 | 1 | `public double getRowEiPremiums(int row)` |  |
| 892 | 1 | `public double getRowBenefits(int row)` | EI for the out of work, the grant for the students; zero for every other row. |
| 894 | 4 | `public double getRowDisposable(int row)` |  |
| 899 | 4 | `public double getRowSpending(int row)` |  |
| 904 | 3 | `public double getRowSaving(int row)` |  |
| 909 | 4 | `public double getRowSavingRate(int row)` | Saving as a share of take-home. |
| 914 | 8 | `public String getRowLabel(int row)` |  |
| 923 | 34 | `public void reset()` |  |
| 959 | 16 | `public void redenominate(double scale)` | The households' income statement, in the new unit. |


# HouseholdAccounts.java - 1,123 lines · 84 methods · 10 constants · model

`ham/citybuildersim/HouseholdAccounts.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Used by (9):** [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 52 | · this month |
| 124 | THE FARE (2026-09-16) |
| 268 | · the statement |
| 341 | · per head |
| 370 | THE SAME STATEMENT, PER PAY TIER |
| 423 | WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19) |
| 688 | ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER |
| 860 | THE MONTH'S STATEMENT, CARRIED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 386 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 389 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 390 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 391 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 393 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 394 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 397 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 915 | `HouseholdAccounts.STATE_SCALARS` | `17, STATE_ROWS = 18` | Scalars and row arrays in the statement's state since 2026-09-19. |
| 918 | `HouseholdAccounts.SCALARS_BEFORE_HEALTH` | `16, ROWS_BEFORE_HEALTH = 15` | ...and the shape before the health premium was a line on it (2026-09-19). |
| 921 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

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
| 86 | `private double healthPremiums` | THE HEALTH PREMIUM (2026-09-19): a share of every wage into the treasury, employee side, in the EI premium's shape. |
| 120 | `private double healthcare` | What the people paid the healthcare service this month. |
| 121 | `private double tuition` |  |
| 122 | `private double interest` |  |
| 154 | `private double fares` |  |
| 163 | `private int population` |  |
| 164 | `private int workforce` |  |
| 165 | `private int jobsFilled` |  |
| 176 | `private double cumulativeSaving` | Everything households have not spent, accumulated. |
| 399 | `private final double[] rowWages` |  |
| 400 | `private final double[] rowTax` |  |
| 401 | `private final double[] rowRent` |  |
| 402 | `private final double[] rowShopping` |  |
| 403 | `private final double[] rowPeople` |  |
| 404 | `private final double[] rowHouseholds` |  |
| 405 | `private final double[] rowContributions` |  |
| 406 | `private final double[] rowPensions` |  |
| 407 | `private final double[] rowHealthcare` |  |
| 408 | `private final double[] rowTuition` |  |
| 409 | `private final double[] rowFares` |  |
| 410 | `private final double[] rowInterest` |  |
| 411 | `private final double[] rowEiPremiums` |  |
| 417 | `private final double[] rowDoors` | Doors each row's households pay for: one a household, a fifth sharing, none with no home. |
| 419 | `private final double[] rowBenefits` | EI to the out of work, grants to the students. |
| 421 | `private final double[] rowHealthPremiums` | The health premium, a slice off the same payslip the EI premium comes off (2026-09-19). |
| 445 | `private double[] carePaid` | Of each row's people, the share who paid for care last month; null is everybody. |
| 448 | `private double careBilled, careFull` | The treatment fees the households were billed - Healthcare.getTreatmentFees() - and the same at full service. |
| 451 | `private final double[] rowCareBilled` | ...and each by row: the billed over the heads who paid, the full over every head. |
| 452 | `private final double[] rowCareFull` |  |
| 771 | `private double pensionPerSenior` | What one pensioner receives, as the policy currently sets it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1074 | **type** `public class HouseholdAccounts` | The city's residents, treated as one household. |

### this month (lines 52-123)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 3 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants)` | The month's EI and grants, from the same figures the treasury books - set before update() or refresh(), which leave them as they are. |
| 97 | 7 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and the health premium beside them, from the figure the treasury books (2026-09-19). |
| 105 | 1 | `public double getEiPremiums()` |  |
| 106 | 1 | `public double getEiBenefits()` |  |
| 107 | 1 | `public double getStudentGrants()` |  |
| 110 | 1 | `public double getHealthPremiums()` | What the people paid the treasury as health premium this month, off their wages. |

### THE FARE (2026-09-16) (lines 124-267)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 3 | `public void setTransitFares(double paid)` | What the city took in fares this month, told to the households who paid it. |
| 161 | 1 | `public double getFares()` |  |
| 179 | 4 | `public void update(double wages, double wageTax, double rent, double shopping, int population, int workforce, int jobsFilled)` | Feed it the month. |
| 184 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, int popul...` |  |
| 191 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` |  |
| 203 | 9 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` | same figure Education collects, not a second copy of it |
| 225 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, int popu...` | The same figures, WITHOUT adding a month to the running total. |
| 232 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 239 | 8 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 248 | 19 | `private void assign(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |

### the statement (lines 268-340)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 1 | `public double getWages()` |  |
| 271 | 1 | `public double getWageTax()` |  |
| 272 | 1 | `public double getRent()` |  |
| 273 | 1 | `public double getShopping()` |  |
| 274 | 1 | `public double getContributions()` |  |
| 275 | 1 | `public double getPensions()` |  |
| 276 | 1 | `public double getHealthcare()` |  |
| 277 | 1 | `public double getTuition()` |  |
| 278 | 1 | `public double getInterest()` |  |
| 287 | 4 | `public double getDisposableIncome()` | What the people actually have to spend after the city has taken its share. |
| 301 | 3 | `public double getSpending()` | Everything that leaves a household in a month. |
| 306 | 3 | `public double getNetSaving()` | Income less tax less everything paid out. |
| 310 | 3 | `public double getCumulativeSaving()` |  |
| 315 | 3 | `public void setCumulativeSaving(double value)` | For the load path. |
| 326 | 4 | `public double getSavingRate()` | Saving as a share of take-home pay. |
| 332 | 4 | `public double getRentBurden()` | Rent as a share of take-home. |
| 337 | 3 | `public double getEffectiveTaxRate()` |  |

### per head (lines 341-369)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 3 | `public double getIncomePerResident()` |  |
| 347 | 3 | `public double getSpendingPerResident()` |  |
| 352 | 3 | `public double getAverageWage()` | What a filled job pays on average. |
| 357 | 3 | `public double getDependencyRatio()` | How many people each working resident is carrying, themselves included. |
| 361 | 1 | `public int getPopulation()` |  |
| 362 | 1 | `public int getWorkforce()` |  |
| 363 | 1 | `public int getJobsFilled()` |  |
| 366 | 3 | `public boolean isLivingBeyondIncome()` | True when the people are being made to spend more than they earn. |

### THE SAME STATEMENT, PER PAY TIER (lines 370-422)

### WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19) (lines 423-687)

| line | len | member | says |
|---:|---:|---|---|
| 459 | 1 | `public void setCarePaid(double[] shareByRow)` | Tells the split who paid for care: the share of each row's people the clinic's fee did not turn away, from HouseholdBalance.carePaidShare(). |
| 466 | 4 | `public void setCareBills(double treatmentBilled, double treatmentAtFullService)` | Tells the split the month's treatment bill, from Healthcare: what was charged (getTreatmentFees) and what would have been at full service (fullTreatmentFees). |
| 471 | 4 | `private double carePaidOf(int row)` |  |
| 477 | 1 | `public double getRowCareBilled(int row)` | The treatment fees this row's people were billed, over the heads who paid. |
| 480 | 1 | `public double getRowCareFull(int row)` | The treatment fees this row would have been billed had every one of its people paid. |
| 506 | 4 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow)` | Splits the month across the tiers. |
| 518 | 6 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | HouseholdBalance - who could AFFORD to shop, not who was hungry. |
| 529 | 158 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | null for one door a household, as before |

### ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER (lines 688-859)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 3 | **type** `public record Statement(double households, double people, double income, double tax, double rent, double fe...` | One household's month. |
| 710 | 6 | `public double rentPerHousehold()` | Rent one let home pays, whoever lives in it. |
| 717 | 1 | `public double getRowDoors(int row)` |  |
| 739 | 23 | `public double[] livingAlonePressure(FamilyModel families)` | How badly a single adult in each tier cannot afford to live alone, 0-1. |
| 764 | 5 | `public double feesPerHead()` | Healthcare and tuition, per person. |
| 772 | 1 | `public void setPensionPerSenior(double value)` |  |
| 773 | 1 | `public double getPensionPerSenior()` |  |
| 776 | 5 | `public double shoppingPerHead()` | The weekly shop, per person, which is how retail demand is counted. |
| 790 | 14 | `public double[] seekerPressure(double[] households)` | How badly one of each group living outside the families cannot afford a door of their own, 0-1 - livingAlonePressure()'s arithmetic on what they live on: EI for the out of work (nothing past the twelfth month), the gr... |
| 811 | 39 | `public Statement statementFor(FamilyModel families, FamilyStructure shape, PayTier tier)` | What one household of this shape and tier earns, pays and keeps. |
| 851 | 1 | `public double getRowWages(int row)` |  |
| 852 | 1 | `public double getRowTax(int row)` |  |
| 853 | 1 | `public double getRowRent(int row)` |  |
| 854 | 1 | `public double getRowShopping(int row)` |  |
| 855 | 1 | `public double getRowFares(int row)` |  |
| 856 | 1 | `public double getRowPeople(int row)` |  |
| 857 | 1 | `public double getRowHouseholds(int row)` |  |
| 858 | 1 | `public int getRowCount()` |  |

### THE MONTH'S STATEMENT, CARRIED (lines 860-1123)

| line | len | member | says |
|---:|---:|---|---|
| 885 | 28 | `public double[] getStatementState()` |  |
| 929 | 82 | `public boolean restoreStatement(double[] in)` | Puts a saved statement back, exactly as it was written. |
| 1013 | 7 | `private static double[] padOlder(double[] rows)` | A row array from an older caller, padded to today's rows; anything else as it came. |
| 1021 | 1 | `public double getRowContributions(int row)` |  |
| 1022 | 1 | `public double getRowPensions(int row)` |  |
| 1023 | 1 | `public double getRowHealthcare(int row)` |  |
| 1024 | 1 | `public double getRowTuition(int row)` |  |
| 1025 | 1 | `public double getRowInterest(int row)` |  |
| 1026 | 1 | `public double getRowEiPremiums(int row)` |  |
| 1028 | 1 | `public double getRowHealthPremiums(int row)` | The health premium this row's wages carried (2026-09-19). |
| 1030 | 1 | `public double getRowBenefits(int row)` | EI for the out of work, the grant for the students; zero for every other row. |
| 1032 | 4 | `public double getRowDisposable(int row)` |  |
| 1037 | 4 | `public double getRowSpending(int row)` |  |
| 1042 | 3 | `public double getRowSaving(int row)` |  |
| 1047 | 4 | `public double getRowSavingRate(int row)` | Saving as a share of take-home. |
| 1052 | 8 | `public String getRowLabel(int row)` |  |
| 1061 | 41 | `public void reset()` |  |
| 1104 | 18 | `public void redenominate(double scale)` | The households' income statement, in the new unit. |


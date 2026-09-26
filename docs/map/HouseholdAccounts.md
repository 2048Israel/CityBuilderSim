# HouseholdAccounts.java - 1,178 lines · 87 methods · 11 constants · model

`ham/citybuildersim/HouseholdAccounts.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Household](Household.md) (11), [PayTier](PayTier.md) (7), [FamilyModel](FamilyModel.md) (6), [FamilyStructure](FamilyStructure.md) (5), [Statement](Statement.md) (3), [AgeBand](AgeBand.md) (2), [SocialSecurity](SocialSecurity.md) (1)

**Used by (10):** [BankCheck](BankCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 52 | · this month |
| 124 | THE FARE (2026-09-16) |
| 163 | · THE BANK'S ACCOUNT FEE (0.7.7) |
| 294 | · the statement |
| 367 | · per head |
| 396 | THE SAME STATEMENT, PER PAY TIER |
| 449 | WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19) |
| 714 | ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER |
| 888 | THE MONTH'S STATEMENT, CARRIED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 412 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 415 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 416 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 417 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 419 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 420 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 423 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 946 | `HouseholdAccounts.STATE_SCALARS` | `18, STATE_ROWS = 19` | Scalars and row arrays in the statement's state since 0.7.7. |
| 949 | `HouseholdAccounts.SCALARS_BEFORE_ACCOUNT_FEES` | `17, ROWS_BEFORE_ACCOUNT_FEES = 18` | ...and the shape before the bank's account fee was a line on it (0.7.7). |
| 952 | `HouseholdAccounts.SCALARS_BEFORE_HEALTH` | `16, ROWS_BEFORE_HEALTH = 15` | ...and the shape before the health premium was a line on it (2026-09-19). |
| 955 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

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
| 171 | `private double accountFees` |  |
| 172 | `private final double[] rowAccountFees` |  |
| 189 | `private int population` |  |
| 190 | `private int workforce` |  |
| 191 | `private int jobsFilled` |  |
| 202 | `private double cumulativeSaving` | Everything households have not spent, accumulated. |
| 425 | `private final double[] rowWages` |  |
| 426 | `private final double[] rowTax` |  |
| 427 | `private final double[] rowRent` |  |
| 428 | `private final double[] rowShopping` |  |
| 429 | `private final double[] rowPeople` |  |
| 430 | `private final double[] rowHouseholds` |  |
| 431 | `private final double[] rowContributions` |  |
| 432 | `private final double[] rowPensions` |  |
| 433 | `private final double[] rowHealthcare` |  |
| 434 | `private final double[] rowTuition` |  |
| 435 | `private final double[] rowFares` |  |
| 436 | `private final double[] rowInterest` |  |
| 437 | `private final double[] rowEiPremiums` |  |
| 443 | `private final double[] rowDoors` | Doors each row's households pay for: one a household, a fifth sharing, none with no home. |
| 445 | `private final double[] rowBenefits` | EI to the out of work, grants to the students. |
| 447 | `private final double[] rowHealthPremiums` | The health premium, a slice off the same payslip the EI premium comes off (2026-09-19). |
| 471 | `private double[] carePaid` | Of each row's people, the share who paid for care last month; null is everybody. |
| 474 | `private double careBilled, careFull` | The treatment fees the households were billed - Healthcare.getTreatmentFees() - and the same at full service. |
| 477 | `private final double[] rowCareBilled` | ...and each by row: the billed over the heads who paid, the full over every head. |
| 478 | `private final double[] rowCareFull` |  |
| 797 | `private double pensionPerSenior` | What one pensioner receives, as the policy currently sets it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1129 | **type** `public class HouseholdAccounts` | The city's residents, treated as one household. |

### this month (lines 52-123)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 3 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants)` | The month's EI and grants, from the same figures the treasury books - set before update() or refresh(), which leave them as they are. |
| 97 | 7 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and the health premium beside them, from the figure the treasury books (2026-09-19). |
| 105 | 1 | `public double getEiPremiums()` |  |
| 106 | 1 | `public double getEiBenefits()` |  |
| 107 | 1 | `public double getStudentGrants()` |  |
| 110 | 1 | `public double getHealthPremiums()` | What the people paid the treasury as health premium this month, off their wages. |

### THE FARE (2026-09-16) (lines 124-162)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 3 | `public void setTransitFares(double paid)` | What the city took in fares this month, told to the households who paid it. |
| 161 | 1 | `public double getFares()` |  |

### THE BANK'S ACCOUNT FEE (0.7.7) (lines 163-293)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 9 | `public void setAccountFees(double[] byRow)` | What each row's households pay the bank in account fees this month. |
| 186 | 1 | `public double getAccountFees()` | What the people paid the bank in account fees this month. |
| 187 | 1 | `public double getRowAccountFees(int row)` |  |
| 205 | 4 | `public void update(double wages, double wageTax, double rent, double shopping, int population, int workforce, int jobsFilled)` | Feed it the month. |
| 210 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, int popul...` |  |
| 217 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` |  |
| 229 | 9 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` | same figure Education collects, not a second copy of it |
| 251 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, int popu...` | The same figures, WITHOUT adding a month to the running total. |
| 258 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 265 | 8 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 274 | 19 | `private void assign(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |

### the statement (lines 294-366)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 1 | `public double getWages()` |  |
| 297 | 1 | `public double getWageTax()` |  |
| 298 | 1 | `public double getRent()` |  |
| 299 | 1 | `public double getShopping()` |  |
| 300 | 1 | `public double getContributions()` |  |
| 301 | 1 | `public double getPensions()` |  |
| 302 | 1 | `public double getHealthcare()` |  |
| 303 | 1 | `public double getTuition()` |  |
| 304 | 1 | `public double getInterest()` |  |
| 313 | 4 | `public double getDisposableIncome()` | What the people actually have to spend after the city has taken its share. |
| 327 | 3 | `public double getSpending()` | Everything that leaves a household in a month. |
| 332 | 3 | `public double getNetSaving()` | Income less tax less everything paid out. |
| 336 | 3 | `public double getCumulativeSaving()` |  |
| 341 | 3 | `public void setCumulativeSaving(double value)` | For the load path. |
| 352 | 4 | `public double getSavingRate()` | Saving as a share of take-home pay. |
| 358 | 4 | `public double getRentBurden()` | Rent as a share of take-home. |
| 363 | 3 | `public double getEffectiveTaxRate()` |  |

### per head (lines 367-395)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 3 | `public double getIncomePerResident()` |  |
| 373 | 3 | `public double getSpendingPerResident()` |  |
| 378 | 3 | `public double getAverageWage()` | What a filled job pays on average. |
| 383 | 3 | `public double getDependencyRatio()` | How many people each working resident is carrying, themselves included. |
| 387 | 1 | `public int getPopulation()` |  |
| 388 | 1 | `public int getWorkforce()` |  |
| 389 | 1 | `public int getJobsFilled()` |  |
| 392 | 3 | `public boolean isLivingBeyondIncome()` | True when the people are being made to spend more than they earn. |

### THE SAME STATEMENT, PER PAY TIER (lines 396-448)

### WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19) (lines 449-713)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 1 | `public void setCarePaid(double[] shareByRow)` | Tells the split who paid for care: the share of each row's people the clinic's fee did not turn away, from HouseholdBalance.carePaidShare(). |
| 492 | 4 | `public void setCareBills(double treatmentBilled, double treatmentAtFullService)` | Tells the split the month's treatment bill, from Healthcare: what was charged (getTreatmentFees) and what would have been at full service (fullTreatmentFees). |
| 497 | 4 | `private double carePaidOf(int row)` |  |
| 503 | 1 | `public double getRowCareBilled(int row)` | The treatment fees this row's people were billed, over the heads who paid. |
| 506 | 1 | `public double getRowCareFull(int row)` | The treatment fees this row would have been billed had every one of its people paid. |
| 532 | 4 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow)` | Splits the month across the tiers. |
| 544 | 6 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | HouseholdBalance - who could AFFORD to shop, not who was hungry. |
| 555 | 158 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | null for one door a household, as before |

### ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER (lines 714-887)

| line | len | member | says |
|---:|---:|---|---|
| 731 | 3 | **type** `public record Statement(double households, double people, double income, double tax, double rent, double fe...` | One household's month. |
| 736 | 6 | `public double rentPerHousehold()` | Rent one let home pays, whoever lives in it. |
| 743 | 1 | `public double getRowDoors(int row)` |  |
| 765 | 23 | `public double[] livingAlonePressure(FamilyModel families)` | How badly a single adult in each tier cannot afford to live alone, 0-1. |
| 790 | 5 | `public double feesPerHead()` | Healthcare and tuition, per person. |
| 798 | 1 | `public void setPensionPerSenior(double value)` |  |
| 799 | 1 | `public double getPensionPerSenior()` |  |
| 802 | 5 | `public double shoppingPerHead()` | The weekly shop, per person, which is how retail demand is counted. |
| 816 | 14 | `public double[] seekerPressure(double[] households)` | How badly one of each group living outside the families cannot afford a door of their own, 0-1 - livingAlonePressure()'s arithmetic on what they live on: EI for the out of work (nothing past the twelfth month), the gr... |
| 837 | 41 | `public Statement statementFor(FamilyModel families, FamilyStructure shape, PayTier tier)` | What one household of this shape and tier earns, pays and keeps. |
| 879 | 1 | `public double getRowWages(int row)` |  |
| 880 | 1 | `public double getRowTax(int row)` |  |
| 881 | 1 | `public double getRowRent(int row)` |  |
| 882 | 1 | `public double getRowShopping(int row)` |  |
| 883 | 1 | `public double getRowFares(int row)` |  |
| 884 | 1 | `public double getRowPeople(int row)` |  |
| 885 | 1 | `public double getRowHouseholds(int row)` |  |
| 886 | 1 | `public int getRowCount()` |  |

### THE MONTH'S STATEMENT, CARRIED (lines 888-1178)

| line | len | member | says |
|---:|---:|---|---|
| 913 | 31 | `public double[] getStatementState()` |  |
| 963 | 99 | `public boolean restoreStatement(double[] in)` | Puts a saved statement back, exactly as it was written. |
| 1064 | 7 | `private static double[] padOlder(double[] rows)` | A row array from an older caller, padded to today's rows; anything else as it came. |
| 1072 | 1 | `public double getRowContributions(int row)` |  |
| 1073 | 1 | `public double getRowPensions(int row)` |  |
| 1074 | 1 | `public double getRowHealthcare(int row)` |  |
| 1075 | 1 | `public double getRowTuition(int row)` |  |
| 1076 | 1 | `public double getRowInterest(int row)` |  |
| 1077 | 1 | `public double getRowEiPremiums(int row)` |  |
| 1079 | 1 | `public double getRowHealthPremiums(int row)` | The health premium this row's wages carried (2026-09-19). |
| 1081 | 1 | `public double getRowBenefits(int row)` | EI for the out of work, the grant for the students; zero for every other row. |
| 1083 | 4 | `public double getRowDisposable(int row)` |  |
| 1088 | 4 | `public double getRowSpending(int row)` |  |
| 1093 | 3 | `public double getRowSaving(int row)` |  |
| 1098 | 4 | `public double getRowSavingRate(int row)` | Saving as a share of take-home. |
| 1103 | 8 | `public String getRowLabel(int row)` |  |
| 1112 | 43 | `public void reset()` |  |
| 1157 | 20 | `public void redenominate(double scale)` | The households' income statement, in the new unit. |


# HouseholdAccounts.java - 1,451 lines · 109 methods · 13 constants · model

`ham/citybuildersim/HouseholdAccounts.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

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

**Uses:** [Household](Household.md) (21), [PayTier](PayTier.md) (10), [FamilyModel](FamilyModel.md) (7), [FamilyStructure](FamilyStructure.md) (5), [Statement](Statement.md) (3), [AgeBand](AgeBand.md) (2), [SocialSecurity](SocialSecurity.md) (1)

**Used by (20):** [BankCheck](BankCheck.md), [FloorView](FloorView.md), [Forms1900Check](Forms1900Check.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [KeptBench](KeptBench.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 52 | · this month |
| 156 | THE FARE (2026-09-16) |
| 196 | · THE COMMUTE, BY ROW (0.7.49) |
| 248 | · THE HOMES' LAMP OIL AND COAL (R0c, 2026-11-05) |
| 282 | · THE BANK'S ACCOUNT FEE (0.7.7) |
| 413 | · the statement |
| 488 | · per head |
| 517 | THE SAME STATEMENT, PER PAY TIER |
| 572 | WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19) |
| 859 | ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER |
| 1093 | THE MONTH'S STATEMENT, CARRIED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 533 | `HouseholdAccounts.RETIRED` | `PayTier.values().length` | Index of the retired row, which sits after the six tiers. |
| 536 | `HouseholdAccounts.UNEMPLOYED` | `Household.UNEMPLOYED_ROW` | The out of work, the students and the orphans, after the retired - Household's rows. |
| 537 | `HouseholdAccounts.STUDENTS` | `Household.STUDENT_ROW` |  |
| 538 | `HouseholdAccounts.ORPHANS` | `Household.ORPHAN_ROW` |  |
| 540 | `HouseholdAccounts.PRISONERS` | `Household.PRISON_ROW` | ...and the prisoners, since 2026-09-11 (night). |
| 542 | `HouseholdAccounts.OWNERS` | `Household.OWNER_ROW` | ...and the owner families, one two-adult household a company that has listed (BO1). |
| 543 | `HouseholdAccounts.ROWS` | `Household.ROWS` |  |
| 546 | `HouseholdAccounts.ROWS_BEFORE_OUTSIDE` | `RETIRED + 1` | The rows a save from before 2026-09-11 carries: the tiers and the retired. |
| 1154 | `HouseholdAccounts.STATE_SCALARS` | `19, STATE_ROWS = 20` | Scalars and row arrays in the statement's state since 0.7.49. |
| 1157 | `HouseholdAccounts.SCALARS_BEFORE_FUEL` | `18, ROWS_BEFORE_FUEL = 19` | ...and the shape before the households' fuel was a line on it (0.7.7 to 0.7.48). |
| 1160 | `HouseholdAccounts.SCALARS_BEFORE_ACCOUNT_FEES` | `17, ROWS_BEFORE_ACCOUNT_FEES = 18` | ...and the shape before the bank's account fee was a line on it (0.7.7). |
| 1163 | `HouseholdAccounts.SCALARS_BEFORE_HEALTH` | `16, ROWS_BEFORE_HEALTH = 15` | ...and the shape before the health premium was a line on it (2026-09-19). |
| 1166 | `HouseholdAccounts.SCALARS_BEFORE_FARES` | `15, ROWS_BEFORE_FARES = 14` | ...and the shape before the transit fare was a line on it (2026-09-16). |

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
| 125 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries at the sale these books settle - the vouchers, up to the baskets got (HouseholdBalance.allocateGroceries()) - in total and by row, each... |
| 126 | `private final double[] rowFoodAssistance` |  |
| 152 | `private double healthcare` | What the people paid the healthcare service this month. |
| 153 | `private double tuition` |  |
| 154 | `private double interest` |  |
| 187 | `private double fares` |  |
| 208 | `private double fuel` |  |
| 209 | `private final double[] riderWeight` |  |
| 210 | `private final double[] driverWeight` |  |
| 211 | `private final double[] rowFuel` |  |
| 214 | `private double fuelImports` | ...and the part of it the world was paid (0.7.62): the rest was the refiners', a sale on their statement. |
| 258 | `private double homeFuel` |  |
| 259 | `private final double[] rowHomeFuel` |  |
| 262 | `private double homeFuelImports` | ...and the part of it Retail paid the world (Retail's own import, on its own books; the money audit's household import lines read none of it). |
| 290 | `private double accountFees` |  |
| 291 | `private final double[] rowAccountFees` |  |
| 308 | `private long population` |  |
| 309 | `private long workforce` |  |
| 310 | `private long jobsFilled` |  |
| 321 | `private double cumulativeSaving` | Everything households have not spent, accumulated. |
| 548 | `private final double[] rowWages` |  |
| 549 | `private final double[] rowTax` |  |
| 550 | `private final double[] rowRent` |  |
| 551 | `private final double[] rowShopping` |  |
| 552 | `private final double[] rowPeople` |  |
| 553 | `private final double[] rowHouseholds` |  |
| 554 | `private final double[] rowContributions` |  |
| 555 | `private final double[] rowPensions` |  |
| 556 | `private final double[] rowHealthcare` |  |
| 557 | `private final double[] rowTuition` |  |
| 558 | `private final double[] rowFares` |  |
| 559 | `private final double[] rowInterest` |  |
| 560 | `private final double[] rowEiPremiums` |  |
| 566 | `private final double[] rowDoors` | Doors each row's households pay for: one a household, a fifth sharing, none with no home. |
| 568 | `private final double[] rowBenefits` | EI to the out of work, grants to the students. |
| 570 | `private final double[] rowHealthPremiums` | The health premium, a slice off the same payslip the EI premium comes off (2026-09-19). |
| 594 | `private double[] carePaid` | Of each row's people, the share who paid for care last month; null is everybody. |
| 597 | `private double careBilled, careFull` | The treatment fees the households were billed - Healthcare.getTreatmentFees() - and the same at full service. |
| 600 | `private final double[] rowCareBilled` | ...and each by row: the billed over the heads who paid, the full over every head. |
| 601 | `private final double[] rowCareFull` |  |
| 1003 | `private double pensionPerSenior` | What one pensioner receives, as the policy currently sets it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1402 | **type** `public class HouseholdAccounts` | The city's residents, treated as one household. |

### this month (lines 52-155)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 3 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants)` | The month's EI and grants, from the same figures the treasury books - set before update() or refresh(), which leave them as they are. |
| 97 | 7 | `public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and the health premium beside them, from the figure the treasury books (2026-09-19). |
| 105 | 1 | `public double getEiPremiums()` |  |
| 106 | 1 | `public double getEiBenefits()` |  |
| 107 | 1 | `public double getStudentGrants()` |  |
| 110 | 1 | `public double getHealthPremiums()` | What the people paid the treasury as health premium this month, off their wages. |
| 128 | 9 | `public void setFoodAssistance(double total, double[] byRow)` |  |
| 139 | 1 | `public double getFoodAssistance()` | What the treasury paid toward the households' groceries at the sale these books settle. |
| 142 | 1 | `public double getRowFoodAssistance(int row)` | ...and toward one row's. |

### THE FARE (2026-09-16) (lines 156-195)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 3 | `public void setTransitFares(double paid)` | What the city took in fares this month, told to the households who paid it. |
| 194 | 1 | `public double getFares()` |  |

### THE COMMUTE, BY ROW (0.7.49) (lines 196-247)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 3 | `public void setCommute(double fuelBill, double[] riders, double[] drivers)` | The month's fuel bill, every litre of it imported, and each row's riders and drivers (any scale; only their shares are read). |
| 222 | 10 | `public void setCommute(double fuelBill, double fuelImported, double[] riders, double[] drivers)` | ...with the part of the bill the world was paid (0.7.62; Motoring.getFuelImports()), never more than the bill. |
| 234 | 1 | `public double getFuel()` | What the households paid for fuel this month: the refiners' shelf at its price and the world's at the import price (0.7.62); the drivers' petrol is this line and not in the shopping since PD1. |
| 237 | 1 | `public double getFuelImports()` | ...and what of it was paid to the world (0.7.62): all of it with no refinery. |
| 240 | 1 | `public double getRowFuel(int row)` | ...and what this row paid of it. |
| 243 | 1 | `public double rowFaresPerHead(int row)` | The fares one person of the row paid this month: the row's fares over its people (the People page's cell panel). |
| 246 | 1 | `public double rowFuelPerHead(int row)` | ...and the fuel. |

### THE HOMES' LAMP OIL AND COAL (R0c, 2026-11-05) (lines 248-281)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 4 | `public void setHomeFuel(double bill, double imported)` | The month's home fuel bill and the part of it the world was paid, before update() or refresh(); a bill that is not above 0 (or not a number) reads 0. |
| 271 | 1 | `public double getHomeFuel()` | What the households paid for lamp oil and coal this month. |
| 274 | 1 | `public double getHomeFuelImports()` | ...and what of it Retail paid the world. |
| 277 | 1 | `public double getRowHomeFuel(int row)` | ...and what this row paid of it. |
| 280 | 1 | `public double rowHomeFuelPerDoor(int row)` | ...and what one door of the row paid of it (statementFor()). |

### THE BANK'S ACCOUNT FEE (0.7.7) (lines 282-412)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 9 | `public void setAccountFees(double[] byRow)` | What each row's households pay the bank in account fees this month. |
| 305 | 1 | `public double getAccountFees()` | What the people paid the bank in account fees this month. |
| 306 | 1 | `public double getRowAccountFees(int row)` |  |
| 324 | 4 | `public void update(double wages, double wageTax, double rent, double shopping, long population, long workforce, long jobsFilled)` | Feed it the month. |
| 329 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, long popu...` |  |
| 336 | 6 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` |  |
| 348 | 9 | `public void update(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double he...` | same figure Education collects, not a second copy of it |
| 370 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, long pop...` | The same figures, WITHOUT adding a month to the running total. |
| 377 | 6 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 384 | 8 | `public void refresh(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |
| 393 | 19 | `private void assign(double wages, double wageTax, double rent, double shopping, double contributions, double pensions, double h...` |  |

### the statement (lines 413-487)

| line | len | member | says |
|---:|---:|---|---|
| 415 | 1 | `public double getWages()` |  |
| 416 | 1 | `public double getWageTax()` |  |
| 417 | 1 | `public double getRent()` |  |
| 418 | 1 | `public double getShopping()` |  |
| 419 | 1 | `public double getContributions()` |  |
| 420 | 1 | `public double getPensions()` |  |
| 421 | 1 | `public double getHealthcare()` |  |
| 422 | 1 | `public double getTuition()` |  |
| 423 | 1 | `public double getInterest()` |  |
| 432 | 4 | `public double getDisposableIncome()` | What the people actually have to spend after the city has taken its share. |
| 446 | 5 | `public double getSpending()` | Everything that leaves a household in a month. |
| 453 | 3 | `public double getNetSaving()` | Income less tax less everything paid out, and the food assistance in (0.7.43). |
| 457 | 3 | `public double getCumulativeSaving()` |  |
| 462 | 3 | `public void setCumulativeSaving(double value)` | For the load path. |
| 473 | 4 | `public double getSavingRate()` | Saving as a share of take-home pay. |
| 479 | 4 | `public double getRentBurden()` | Rent as a share of take-home. |
| 484 | 3 | `public double getEffectiveTaxRate()` |  |

### per head (lines 488-516)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 3 | `public double getIncomePerResident()` |  |
| 494 | 3 | `public double getSpendingPerResident()` |  |
| 499 | 3 | `public double getAverageWage()` | What a filled job pays on average. |
| 504 | 3 | `public double getDependencyRatio()` | How many people each working resident is carrying, themselves included. |
| 508 | 1 | `public long getPopulation()` |  |
| 509 | 1 | `public long getWorkforce()` |  |
| 510 | 1 | `public long getJobsFilled()` |  |
| 513 | 3 | `public boolean isLivingBeyondIncome()` | True when the people are being made to spend more than they earn. |

### THE SAME STATEMENT, PER PAY TIER (lines 517-571)

### WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19) (lines 572-858)

| line | len | member | says |
|---:|---:|---|---|
| 608 | 1 | `public void setCarePaid(double[] shareByRow)` | Tells the split who paid for care: the share of each row's people the clinic's fee did not turn away, from HouseholdBalance.carePaidShare(). |
| 615 | 4 | `public void setCareBills(double treatmentBilled, double treatmentAtFullService)` | Tells the split the month's treatment bill, from Healthcare: what was charged (getTreatmentFees) and what would have been at full service (fullTreatmentFees). |
| 620 | 4 | `private double carePaidOf(int row)` |  |
| 626 | 1 | `public double getRowCareBilled(int row)` | The treatment fees this row's people were billed, over the heads who paid. |
| 629 | 1 | `public double getRowCareFull(int row)` | The treatment fees this row would have been billed had every one of its people paid. |
| 655 | 4 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow)` | Splits the month across the tiers. |
| 667 | 6 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | HouseholdBalance - who could AFFORD to shop, not who was hungry. |
| 678 | 180 | `public void updateByTier(double[] wagesPerTier, double[] taxPerTier, double[] peoplePerRow, double[] housePerRow, double[] spen...` | null for one door a household, as before |

### ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER (lines 859-1092)

| line | len | member | says |
|---:|---:|---|---|
| 876 | 3 | **type** `public record Statement(double households, double people, double income, double tax, double rent, double fe...` | One household's month. |
| 881 | 6 | `public double rentPerHousehold()` | Rent one let home pays, whoever lives in it. |
| 888 | 1 | `public double getRowDoors(int row)` |  |
| 891 | 3 | `public double costOfLivingAlone()` | What living alone costs a person a month: a home's rent, a basket and the fees (livingAlonePressure()'s figure, and the one the floor's job cover, PolicyPreview.floorJobCover(), is set against). |
| 915 | 23 | `public double[] livingAlonePressure(FamilyModel families)` | How badly a single adult in each tier cannot afford to live alone, 0-1. |
| 940 | 5 | `public double feesPerHead()` | Healthcare, tuition and the fares, per person over the whole city: the living-alone pressure's figure. |
| 959 | 5 | `public double careAndSchoolPerHead()` | The clinic's and the schools' fees, per person: feesPerHead() without the fares. |
| 966 | 5 | `public double faresPerHead()` | The fares, per person over the whole city: the rest of feesPerHead(). |
| 973 | 4 | `public double interestPerHousehold(PayTier tier)` | What one household of the tier pays in interest on what it owes: its row's interest per household. |
| 979 | 4 | `public double accountFeePerHousehold(PayTier tier)` | ...and in the bank's account fee: its row's fees per household. |
| 992 | 9 | `public double wagePerEarner(FamilyModel families, PayTier tier)` | What one earner of the tier is paid this month: the tier's wage bill over its earners, every family shape's earners counted (0.7.27, out of statementFor(), which calls it). |
| 1004 | 1 | `public void setPensionPerSenior(double value)` |  |
| 1005 | 1 | `public double getPensionPerSenior()` |  |
| 1008 | 5 | `public double shoppingPerHead()` | The weekly shop, per person, which is how retail demand is counted. |
| 1022 | 14 | `public double[] seekerPressure(double[] households)` | How badly one of each group living outside the families cannot afford a door of their own, 0-1 - livingAlonePressure()'s arithmetic on what they live on: EI for the out of work (nothing past the twelfth month), the gr... |
| 1043 | 40 | `public Statement statementFor(FamilyModel families, FamilyStructure shape, PayTier tier)` | What one household of this shape and tier earns, pays and keeps. |
| 1084 | 1 | `public double getRowWages(int row)` |  |
| 1085 | 1 | `public double getRowTax(int row)` |  |
| 1086 | 1 | `public double getRowRent(int row)` |  |
| 1087 | 1 | `public double getRowShopping(int row)` |  |
| 1088 | 1 | `public double getRowFares(int row)` |  |
| 1089 | 1 | `public double getRowPeople(int row)` |  |
| 1090 | 1 | `public double getRowHouseholds(int row)` |  |
| 1091 | 1 | `public int getRowCount()` |  |

### THE MONTH'S STATEMENT, CARRIED (lines 1093-1451)

| line | len | member | says |
|---:|---:|---|---|
| 1119 | 33 | `public double[] getStatementState()` |  |
| 1173 | 7 | `private static boolean statementShape(int length, int rows)` | True when a statement this long is one of the shapes from the fare's onward with this many rows in each row array: today's rows, or a save's from before the owner families had one (BO1). |
| 1187 | 130 | `public boolean restoreStatement(double[] in)` | Puts a saved statement back, exactly as it was written. |
| 1319 | 8 | `private static double[] padOlder(double[] rows)` | A row array from an older caller, padded to today's rows; anything else as it came. |
| 1328 | 1 | `public double getRowContributions(int row)` |  |
| 1329 | 1 | `public double getRowPensions(int row)` |  |
| 1330 | 1 | `public double getRowHealthcare(int row)` |  |
| 1331 | 1 | `public double getRowTuition(int row)` |  |
| 1332 | 1 | `public double getRowInterest(int row)` |  |
| 1333 | 1 | `public double getRowEiPremiums(int row)` |  |
| 1335 | 1 | `public double getRowHealthPremiums(int row)` | The health premium this row's wages carried (2026-09-19). |
| 1337 | 1 | `public double getRowBenefits(int row)` | EI for the out of work, the grant for the students; zero for every other row. |
| 1339 | 4 | `public double getRowDisposable(int row)` |  |
| 1344 | 4 | `public double getRowSpending(int row)` |  |
| 1349 | 3 | `public double getRowSaving(int row)` |  |
| 1354 | 4 | `public double getRowSavingRate(int row)` | Saving as a share of take-home. |
| 1359 | 9 | `public String getRowLabel(int row)` |  |
| 1369 | 53 | `public void reset()` |  |
| 1424 | 26 | `public void redenominate(double scale)` | The households' income statement, in the new unit. |


# LongPlaytest.java - 5,720 lines · 85 methods · 68 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> A city played for four thousand months, the way a person plays.
> 
> WHY NOT JUST simulateMonths(4000)
> 
> A single long skip is one input. It exercises the month loop and almost
> nothing else: the player never intervenes, so the city settles into whatever
> equilibrium it finds in the first fifty months and then repeats it. Every bug
> that lives in the interaction between DECIDING and SIMULATING - the ones that
> need something to be built, taxed, borrowed against, saved or reloaded partway
> through - is invisible to it.
> 
> So this alternates. Short hands-on stretches where an advisor looks at the
> city and does something about it, long skips where it just runs, policy
> changes, borrowing, land purchases, demolitions, and save/reload round trips
> partway through. Roughly the shape of a real session, repeated a hundred and
> forty times.
> 
> WHAT IT IS LOOKING FOR
> 
> Not "did it finish". A simulation that runs forever while quietly printing
> nonsense is worse than one that throws. Every month is audited against a list
> of things that must be true of any city in any state (below), and every
> reload is checked against the game it came from to the cent. Findings are
> deduplicated and reported with the month they first appeared, because a
> problem that starts at month 900 and a problem that starts at month 3 are
> different problems.

**Uses:** [Game](Game.md) (89), [BusinessDebtManager](BusinessDebtManager.md) (38), [Good](Good.md) (35), [Equity](Equity.md) (25), [Exchange](Exchange.md) (24), [Sectors](Sectors.md) (22), [Sector](Sector.md) (21), [Bank](Bank.md) (20), [BuildingsTemplate](BuildingsTemplate.md) (16), [WageBand](WageBand.md) (14), [Crime](Crime.md) (13), [AgeBand](AgeBand.md) (12), [CareType](CareType.md) (12), [Founding](Founding.md) (12), [DebtManager](DebtManager.md) (11), [JobType](JobType.md) (11), [InfrastructureManager](InfrastructureManager.md) (9), [LandParcel](LandParcel.md) (9), [FundLedger](FundLedger.md) (9), [EconomyManager](EconomyManager.md) (8), [HouseholdBalance](HouseholdBalance.md) (8), [BuildingType](BuildingType.md) (8), [TaxPolicy](TaxPolicy.md) (8), [FamilyModel](FamilyModel.md) (7), [ForeignAccounts](ForeignAccounts.md) (7), [TreasuryFund](TreasuryFund.md) (7), [InterimLoan](InterimLoan.md) (6), [PopulationManager](PopulationManager.md) (6), [BuildingManager](BuildingManager.md) (6), [LandManager](LandManager.md) (6)... and 52 more

**Used by (19):** [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [GroceryCheck](GroceryCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [MapCheck](MapCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 47 | FINDINGS |
| 66 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 108 | THE AUDIT |
| 179 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 1128 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 1693 | THE ADVISOR |
| 1711 | WHO IS PLAYING (2026-09-17) |
| 1753 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 1782 | THE FOUNDING, AS A CHOICE (0.7.10) |
| 1810 | THE TRACE, BESIDE THE REPORT (0.7.10) |
| 2215 | THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) |
| 2253 | WAGES AGAINST THE INDEX (2026-09-21) |
| 2277 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 2296 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 2313 | THE CEILING, SET (0.7.2) |
| 2328 | THE TARGET, SET (0.7.4) |
| 2496 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 2551 | · AND EVERYTHING THAT IS A PURCHASE. |
| 2891 | · AND THE BEST OF THEM WINS. |
| 3451 | RUNNING MONTHS |
| 3642 | SAVE / RELOAD, MID-RUN |
| 3939 | (untitled) |
| 3947 | · the city's fund and the bank's rescue (0.7.14) |
| 4035 | · the fund's cost basis (0.7.39) |
| 4083 | · an Insane founding (0.7.14) |
| 4172 | · · founding: a few months at a time, by hand |
| 4264 | · · then the real rhythm |
| 4411 | · · the report |
| 4505 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `LongPlaytest.TARGET_MONTHS` | `4000` |  |
| 55 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 161 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 163 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 202 | `LongPlaytest.FAR_FROM_PARITY` | `2.0` | How far from parity, as a multiple of it, a weak currency has to be to count as far from it. |
| 218 | `LongPlaytest.dialPath` | `new java.util.ArrayList<>()` | Every month's policy rate, for the dial's min, median and max over the run. |
| 220 | `LongPlaytest.inflationPath` | `new java.util.ArrayList<>()` | Every month's inflation reading, once the index has a year to read, for its median. |
| 229 | `LongPlaytest.spendPath` | `new java.util.ArrayList<>()` | Every month's spend factor (0.7.3): the share of their spending above subsistence the households planned at, on the month's real deposit rate - HouseholdBalance.getSpendFactor() after the month. |
| 247 | `LongPlaytest.bankNii` | `new double [ 12 ], bankFees = new double [ 12 ], bankOther = new double [ 12 ...` | THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for the checkpoint line that prints its price build-up beside its margin, its cost ratio, its fee share and its return on equity - and the run's deposi... |
| 264 | `LongPlaytest.bankYears` | `new java.util.ArrayList<>()` | THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own target averaged over each year, its return on the equity it opened the year with, its provisions over the loans it made (the businesses' and the ... |
| 287 | `LongPlaytest.watchSpell` | `new java.util.HashMap<>()` |  |
| 300 | `LongPlaytest.refusedOnPrice` | `new java.util.TreeMap<>()` |  |
| 310 | `LongPlaytest.houseReasons` | `new java.util.TreeMap<>()` |  |
| 354 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 509 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 521 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 525 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 560 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 630 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 641 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 642 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 643 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 644 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 645 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 646 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 647 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 650 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 653 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 654 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 655 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 656 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 659 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 660 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 661 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 662 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 725 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 726 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 727 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 728 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 730 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 734 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they left unpaid and wha... |
| 736 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 737 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1027 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1741 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 1748 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1774 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1802 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").t...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 1869 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 1872 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 2204 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 2212 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE...` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2239 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").tri...` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 2242 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT...` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 2245 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 2275 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2294 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2310 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2325 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2341 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2416 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 2984 | `LongPlaytest.OIL_FUEL_IMPORTS_SHARE` | `.01` | The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. |
| 3016 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 3023 | `LongPlaytest.GROUND_AHEAD_CASH_SHARE` | `.10` | The share of the treasury's cash one look spends keeping ground ahead (0.7.58, J1d): a tenth, the share the war chest tops the reserves up from and the every-13th-stop purchase is held under. |
| 3406 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 3438 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 3984 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 3992 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static PrintStream out` | Where the report goes. |
| 58 | `int count` |  |
| 59 | `int firstMonth` |  |
| 60 | `int lastMonth` |  |
| 61 | `String worst` |  |
| 62 | `double worstSize` |  |
| 63 | `int worstMonth` |  |
| 116 | `static double worstCrowding` | How far past the buildings' comfortable capacity the city was pulled. |
| 117 | `static int worstCrowdingMonth` |  |
| 136 | `static double worstUnemployment` | Unemployment, watched rather than asserted. |
| 137 | `static double lastUnemployment` |  |
| 139 | `static double totalEvicted` | The people outside the families, over the run (2026-09-11). |
| 147 | `static double usedOffered` | The second-hand car market over the run (2026-09-17). |
| 148 | `static int worstUnhousedMonth` |  |
| 150 | `static int monthsAnyTierDeclining` |  |
| 151 | `static int monthsPeopleLeft` |  |
| 156 | `static int outbreaks` | Sickness. |
| 157 | `static int monthsInOutbreak` |  |
| 158 | `static boolean wasInOutbreak` |  |
| 159 | `static double worstSickRate` |  |
| 164 | `static double orphanDeathsRun` |  |
| 165 | `static double allDeaths` |  |
| 166 | `static int worstLongSickMonth` |  |
| 173 | `static double personMonths` | The price at the clinic door (2026-09-19): person-months lived, so the deaths can be read per thousand a year; the people the fee turned away, summed for the mean; and the fees the households skipped, summed over the ... |
| 175 | `static double studentInterestSum` | The price of a place (2026-09-21): the interest the graduates paid and the grants paid, summed over the run. |
| 177 | `static double crimeVsSum` | Crime (2026-09-11): the rate against Canada's summed for the mean, the worst, and what it did over the run. |
| 186 | `static double peakSeats` |  |
| 187 | `static int peakSeatsMonth` |  |
| 188 | `static int monthsWithSeats` |  |
| 189 | `static int lastSeatMonth` |  |
| 190 | `static double serviceExportsRun` |  |
| 191 | `static double caughtRun` |  |
| 198 | `static double peakRate` | The currency over the run (2026-09-21): its dearest dollar and when, and how many months it spent far from parity. |
| 199 | `static int peakRateMonth` |  |
| 216 | `static int monthsAtGuard` | THE CURRENCY'S GUARD AND THE DIAL'S PATH, over the run (0.7.2). |
| 231 | `static double spendLow` | ...its lowest and highest, and the first month each was struck in. |
| 232 | `static int spendLowMonth` |  |
| 234 | `static int depositCappedMonths` | Months the bank's interest margin could not pay the deposit rate it chose, and paid what the margin had (Bank.isDepositPayoutHeld(), 0.7.7), and the first and last of them. |
| 250 | `static int bankMonths` |  |
| 251 | `static double shareSum` |  |
| 252 | `static int shareMonths` |  |
| 265 | `static double yrRatio, yrTarget, yrNet, yrProv, yrBook, yrDiv, yrBuyback, yrIssued, yrOpenEquity, yrOpenPop` |  |
| 266 | `static int yrMonths, yrFailuresOpen, yrFrozen, yrBranchMonths` |  |
| 267 | `static int rationedMonths, keepGoingMonths, payingMonths, returningMonths, rebuildingMonths` |  |
| 268 | `static double runDividends, runBuybacks, runIssued, runProvisions` |  |
| 270 | `static double runLoanMonths, runWrittenOff` | Every month's loans (the businesses' and the families') summed, and every write-off: the run's provisions over its average book, through the cycle rather than over the growth years alone. |
| 283 | `static int backstops, sliceMonths, newsMonths, pastWatchMonths, pastTriggerMonths, longestWatchSpell, longe...` | HOW THE DEFAULTS ARRIVE (0.7.8, a sector defaults a slice at a time): the backstop's whole-sector write-downs; the months any slice was written off, and the months one was news (BusinessDebtManager .defaultsAreNews())... |
| 285 | `static String longestWatchSector` |  |
| 286 | `static double sliceWrittenOff, worstWriteOffShare, worstWriteOff, lentPastWatch, runAllowanceUsed` |  |
| 288 | `static Notice lastDefaultNotice` |  |
| 298 | `static int loansPastWatch, loansPastOne, loansPastT, projectsPastOne` | ROUND 2 (0.7.8): the price off the curve and the plant sold as materials. |
| 299 | `static double lentPastOne, lentPastT, rateSumPastOne, rateMaxPastOne, rateSumPastT, rateMaxPastT` |  |
| 308 | `static int mortgagesWritten` | THE LANDLORDS' MORTGAGES (0.7.11): what they wrote, what the insurance took and paid, and every month's reason the landlords built or did not, counted by its words (houseReason()) - the batch's ensembles were read off... |
| 309 | `static double mortgagesLent, premiumsRun, claimsRun` |  |
| 343 | `static int salvageBuildingsDistress, salvageBuildingsSpare` |  |
| 344 | `static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed` |  |
| 353 | `static int leverageMonths, levBankMonths, branchesOpened, branchesClosedSeen, lastBranches` | THE LEVERAGE RATIO AND THE BRANCHES (0.7.11, round 2): how often the leverage requirement was the one that bound, the ratio at its lowest, and the branches opened - by where the bank's capital stood the month each one... |
| 355 | `static double lowestLeverage` |  |
| 356 | `static int lowestLeverageMonth` |  |
| 357 | `static int mortgagesRefusedForCapital` |  |
| 413 | `static double bondRunLoansDefaulted, bondRunLoansLost, bondRunBondsDefaulted, bondRunBondsLost` | THE BONDS (0.7.12): what defaulted in each class and what it lost - the recoveries the sources are read against - the months a sector owed bonds, and the peak share of the businesses' debt in bonds. |
| 414 | `static double bondPeakShare` |  |
| 415 | `static int bondPeakShareMonth, bondMonthsWithBonds` |  |
| 508 | `static int deskOverEquityMaxMonth` | ROUND 4 (0.7.8): the desk's inventory at the close against the bank's equity, month by month while a bank stands out of resolution - one in it holds its old inventory against the few dollars it has earned back, which ... |
| 510 | `static double deskOverEquityMax` |  |
| 522 | `static int shareListedMonths, shareTradedMonths, deskInventoryMonths, refusedAtLineMonths, projectsRefusedA...` |  |
| 523 | `static double deskPnlRun, deskInventorySum, ordinaryDividendsRun` |  |
| 524 | `static double refusedAtLineRun, notRenewedAtLineRun` |  |
| 631 | `static double companyDepositInterestRun, companiesSoldShortRun, buybackRestingRun` |  |
| 632 | `static int overdraftMonths, overdraftNoLenderMonths, overdraftProjectMonths` |  |
| 633 | `static double overdraftSum, overdraftNoLenderSum, overdraftMax, overdraftProjectSum` |  |
| 634 | `static int cannotPayMonths, cannotPayBankUnderLine` |  |
| 635 | `static double cannotPayForgivenRun, cannotPayDebtDefaultedRun, cannotPayForgivenBanned` |  |
| 637 | `static int ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineRationedMonths` | ---- round 5: the lines that stay open, the growth doors the capital rule still shuts, and interim financing ---- |
| 638 | `static double lineLentRationedRun, lineLentPastOldRuleRun` |  |
| 639 | `static int interimWritten, interimRepaid, interimDefaultedAgain, interimRefusedPast, interimRefusedShut, ba...` |  |
| 640 | `static double interimWrittenSum, interimRepaidSum, interimWrittenOffSum, backstopInBanSum` |  |
| 648 | `static int longestCannotPaySpell, longestCannotPayStart, longestCannotPayBanned` |  |
| 649 | `static String longestCannotPaySector` |  |
| 657 | `static int defaultsAfterCut, defaultsOnStock, defaultsOnBondCost` |  |
| 658 | `static double defaultsAfterCutSum, defaultsOnStockSum, defaultsOnBondCostSum` |  |
| 735 | `static int afterProjectCeiling` |  |
| 738 | `static double shellUnpaid, shellInterim` |  |
| 739 | `static int shellMonths, shellLongest, shellLongestFrom` |  |
| 740 | `static String shellLongestWho` |  |
| 1036 | `static int worstCrimeMonth` |  |
| 1037 | `static double lastSickRate` |  |
| 1038 | `static double workLostToIllness` |  |
| 1039 | `static int monthsObserved` |  |
| 1040 | `static double totalDepartures` |  |
| 1041 | `static double totalArrivals` |  |
| 1044 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 1751 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 1870 | `static java.io.PrintWriter traceMonths, traceBorrow, traceHouse, tracePop, traceBank, traceBonds, traceBuil...` |  |
| 2351 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 2352 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 2360 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 2361 | `static double landLocalRun` |  |
| 2363 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 2364 | `static double vaultLow` |  |
| 2367 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 2980 | `static int ironFieldsBought, ironFieldsOnBond, firstIronNeedMonth, firstIronMonth, ironCouldNotPay, ironNon...` | Over the run (0.7.64): iron offers bought, how many on the funding page's bond, the first look that needed iron, the first purchase's month and words, and the looks that needed iron and could not pay for the cheapest ... |
| 2981 | `static String firstIron` |  |
| 2987 | `static int oilBought` | Offers with oil the test player bought over the run (0.7.62). |
| 2995 | `static double fuelBillRun, fuelAbroadRun` | Over the run (0.7.62): the drivers' and the railway's fuel, what of it was bought abroad, the first well and refinery, and the most refineries standing. |
| 2996 | `static int firstWellMonth, firstRefineryMonth, mostRefineries` |  |
| 3026 | `static int groundAheadBought, groundAheadShort` | Over the run: offers bought to keep ground ahead, their dry square feet and US dollars (thousands), and the looks the cash share stopped short. |
| 3027 | `static double groundAheadSqFt, groundAheadUsd` |  |
| 3122 | `static int freshBought, coastsBought, waterLandOffered` | Over the run: offers bought for their lakes and river past the fresh water limit, offers bought for a coast, and the moves that offered either (0.7.59). |
| 3132 | `static int mapMonths, mapMismatches, mapReloads, mapReloadsSame` | THE CITY MAP, WATCHED (0.7.60, batch J3): the run asks for its city's map at the founding, so every month keeps it up (Game.reconcileMap()) and every save writes its sidecar - nothing in the model reads it, so the tra... |
| 3461 | `static int refusedSkips` |  |
| 3476 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 3477 | `static int strainMonths, monthsWithoutCapacity` |  |
| 3478 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 3486 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 3487 | `static double peakAdvances, peakArrears` |  |
| 3496 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 3497 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 3506 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 3507 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 3941 | `static double lifetimeWriteOffs` |  |
| 3943 | `static double lowestPaidIn` | The bank's paid-in capital at its lowest, the month, and the most its two parts were ever off its equity (0.7.13, round 2). |
| 3944 | `static int lowestPaidInMonth` |  |
| 3945 | `static double lifetimeHouseholdWriteOffs` |  |
| 3950 | `static int buttonPresses` | Presses of the Bank tab's button that resolved the bank (-Dplaytest.rescue=BUTTON). |
| 3952 | `static int offersFunded` | Offers accepted after borrowing for them on the funding page's bond, and what that bond raised. |
| 3953 | `static double offerFundingRaised` |  |
| 3979 | `int month` |  |
| 3980 | `double at` |  |
| 3981 | `int under50` |  |
| 3982 | `double lowest` |  |
| 3985 | `static int resolutionsSeen` |  |
| 3987 | `static double fundPeak, fundCashShareSum` | The fund's value, its cash and the most of any company its market book held, over the run. |
| 3988 | `static int fundMonths` |  |
| 3990 | `static double warrantSharesSeen, warrantValueTaken` | The warrants' shares taken at expiry, at the price the month they were taken (share counts move with the exchange's splits, so the count alone does not add up across a run). |
| 3991 | `static int warrantExercises` |  |
| 4046 | `static double[] ledgerBefore` | THE FUND'S LEDGER, MONTH BY MONTH (FundLedger; the project's spec-fund-0739.md, 3.7, rule 3 - count a mechanic in a real run): the identity FundLedgerCheck holds on a fixture - realized + the change in unrealized = th... |
| 4047 | `static double ledgerWorst` |  |
| 4048 | `static int ledgerRuleRows, ledgerHandRows, ledgerMatured, ledgerWrittenDown, ledgerRescues` |  |
| 4086 | `static DebtQuote dayZeroBond, dayZeroNote` | What an Insane city was quoted on day 0 for its village, and what it borrowed in its first year. |
| 4087 | `static double villageInvoice, firstYearBorrowed` |  |
| 4088 | `static int insaneBorrowings` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 5681 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

### FINDINGS (lines 47-65)

| line | len | member | says |
|---:|---:|---|---|
| 57 | 8 | **type** `static final class Finding` |  |

### `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). (lines 66-107)

| line | len | member | says |
|---:|---:|---|---|
| 88 | 3 | `static void flag(int month, String what, String detail)` |  |
| 92 | 15 | `static void flag(int month, String what, String detail, double size)` |  |

### THE AUDIT (lines 108-178)

### A mechanic that is not counted over a real run is a mechanic nobody (lines 179-1692)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 21 | `static String houseReason(String line)` | The landlords' month in a word, off the advisor's line: what built it, or what stopped it. |
| 335 | 8 | `static void countMortgages(Game g)` |  |
| 359 | 47 | `static void countLeverage(Game g)` |  |
| 417 | 53 | `static void countBonds(Game g)` |  |
| 471 | 27 | `static void countPriceAndPlant(Game g)` |  |
| 527 | 1 | `static double pct(double part, double whole)` |  |
| 529 | 30 | `static void countShares(Game g)` |  |
| 562 | 10 | `static void countCapitalLimits(Game g)` |  |
| 574 | 5 | `static double maxOf(java.util.List<Double> v)` | The largest of a list, 0 when it is empty. |
| 581 | 6 | `static double quantile(java.util.List<Double> v, double p)` | The p-th quantile of a list, 0 when it is empty. |
| 588 | 40 | `static void countDefaults(Game g)` |  |
| 664 | 59 | `static void countRound6(Game g)` |  |
| 742 | 42 | `static void countRound7(Game g)` |  |
| 785 | 101 | `static void countRound4(Game g)` |  |
| 888 | 5 | `static double forgivenTotal(EconomyManager em)` | Every overdraft forgiven over the run: the backstop's, the one path left that forgives (round 4's cash-flow test forgave too; since round 5 it lends the rest as interim financing). |
| 895 | 6 | `static double shareOver(java.util.List<Double> v, double line)` | The share of a list's entries over a line, percent. |
| 902 | 112 | `static void bankYear(Game g)` |  |
| 1016 | 9 | `static String yearSpread(int col, double scale)` | The median, least and most of one column over the growth years, as "m% (lo-hi%)". |
| 1030 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |
| 1046 | 631 | `static void audit(Game g)` |  |
| 1678 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 1685 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 1693-1710)

| line | len | member | says |
|---:|---:|---|---|
| 1709 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 1711-1752)

| line | len | member | says |
|---:|---:|---|---|
| 1745 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 1753-1781)

| line | len | member | says |
|---:|---:|---|---|
| 1778 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the currency may move. |

### THE FOUNDING, AS A CHOICE (0.7.10) (lines 1782-1809)

| line | len | member | says |
|---:|---:|---|---|
| 1806 | 3 | `static Founding founding()` | The city the run founds: Danzik, on the flag's preset, in the default world. |

### THE TRACE, BESIDE THE REPORT (0.7.10) (lines 1810-2214)

| line | len | member | says |
|---:|---:|---|---|
| 1879 | 15 | `static void notePaper(Game g, String purpose)` | Writes every piece of paper not yet written, with what it paid for - called the moment the advisor borrows, so the purpose is its own, and at the end of every month for whatever the game issued by itself. |
| 1895 | 56 | `static void traceOpen()` |  |
| 1952 | 51 | `static void traceMonth(Game g)` |  |
| 2013 | 43 | `static void traceLabour(Game g)` | The labour market's month, one row of <prefix>-labour.csv (0.7.18). |
| 2064 | 54 | `static void traceBuild(Game g)` | The builders, the doors and the wage bill, one row of <prefix>-build.csv (0.7.17). |
| 2120 | 7 | `static double cityOrderTax(Game g)` | The builders' tax in the city's orders still on site - paid with the order, not yet billed (revised 0.7.19). |
| 2129 | 5 | `static double sectorsPreTax(Game g)` | Every sector's month before profit tax, summed - what Luxury's share of the city's profit is read against (0.7.19). |
| 2141 | 17 | `static void tracePop(Game g)` | The people's month, one row of <prefix>-pop.csv (0.7.11, round 2): who lives here by age, what migration aimed at and did and why, and the two housing markets - the trace that asks why a city with the same jobs holds ... |
| 2160 | 24 | `static void traceHouse(Game g)` | The landlords' month, one row of <prefix>-house.csv (0.7.11). |
| 2185 | 11 | `static synchronized void traceClose()` |  |

### THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) (lines 2215-2252)

| line | len | member | says |
|---:|---:|---|---|
| 2247 | 5 | `static double fundDial(String s)` |  |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 2253-2276)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 2277-2295)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 2296-2312)

### THE CEILING, SET (0.7.2) (lines 2313-2327)

### THE TARGET, SET (0.7.4) (lines 2328-3450)

| line | len | member | says |
|---:|---:|---|---|
| 2370 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 2377 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 2405 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 2418 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 2424 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 2484 | 418 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 2933 | 26 | `static void ironWhenNeeded(Game g)` | IRON, A WHOLE FIELD AT A TIME (0.7.64, batch L). |
| 2967 | 11 | `static String buyWhole(Game g, LandParcel p, String purpose)` | Buys an offer as the land office does (0.7.64): out of the cash when it covers it - "cash" - or, converting and short, the funding page's BUILD_BOND_YEARS bond for the gap when canService() carries it, then the offer ... |
| 2990 | 3 | `static double fuelBoughtAbroad(Game g)` | The fuel the world sold the city last month (0.7.62): its drivers' (Game.getHouseholdFuelImports()) and its railway's (Rail.getFuelImported()). |
| 2998 | 8 | `static void countFuel(Game g)` |  |
| 3036 | 3 | `static double groundAheadUtilisation(Game g)` | The most of its dry ground a look leaves built on (0.7.58, J1d): the ground in use grown BuildAdvice.HORIZON months by the businesses' growthFactor(), with BuildAdvice.SLACK past it - the build advice's own sizing (0.... |
| 3047 | 21 | `static void keepGroundAhead(Game g)` | GROUND KEPT AHEAD (see advise()): buys the best value for room while more of the dry ground is built on than groundAheadUtilisation(), the offers together costing no more than GROUND_AHEAD_CASH_SHARE of the cash at th... |
| 3084 | 36 | `static void addWater(java.util.List<Move> moves, Game g, double lost)` | WATER, AND WHERE IT COMES FROM (0.7.59, batch J2; spec-land 3, J2's playtest rule). |
| 3142 | 67 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 3219 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 3228 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 3262 | 16 | `static int linesThatPay(Game g, BuildingsTemplate t, int most)` | The most of these transit lines, up to most, that pay their way this month (0.7.49, D3): the output the road gives back with them standing (the month's GDP times the throughput they add, on the network as it would be ... |
| 3280 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 3291 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 3296 | 4 | `static int qty(Game g, String name)` |  |
| 3302 | 3 | `static boolean villageBuild(Game g, String name, int quantity)` | The founding village's builds: build(), and on an Insane founding the funding page's bond for a stage the treasury is short of (0.7.14). |
| 3310 | 72 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 3409 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 3411 | 18 | `static boolean canService(Game g, double extra)` |  |
| 3440 | 3 | `static void refusal(String why)` |  |
| 3444 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |

### RUNNING MONTHS (lines 3451-3641)

| line | len | member | says |
|---:|---:|---|---|
| 3509 | 17 | `static void countTheHolders(Game g)` |  |
| 3527 | 10 | `static void countTheCentralBank(Game g)` |  |
| 3538 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 3552 | 89 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 3642-3938)

| line | len | member | says |
|---:|---:|---|---|
| 3651 | 281 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 3933 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 3939-3946)

### the city's fund and the bank's rescue (0.7.14) (lines 3947-4034)

| line | len | member | says |
|---:|---:|---|---|
| 3961 | 10 | `static void acceptTheOffer(Game g)` | ACCEPTS THE BANK'S OFFER, the way the page lets a player: a treasury short of it takes the funding page's first offer - the build screen's twenty-year bond, sized to the gap - and then pays. |
| 3978 | 6 | **type** `static final class Stake` | THE CITY'S STAKE IN ITS BANK AFTER EACH RESCUE: at the resolution, then five, ten and twenty-five years on, and the first months it is under half and under a tenth - until the next resolution takes it back to everything. |
| 3994 | 40 | `static void watchTheStake(Game g)` |  |

### the fund's cost basis (0.7.39) (lines 4035-4082)

| line | len | member | says |
|---:|---:|---|---|
| 4050 | 7 | `static double[] ledgerSnap(Game g)` |  |
| 4058 | 21 | `static void watchTheLedger(Game g)` |  |
| 4081 | 1 | `static String pct(double v)` | A stake, in words: "84.4%", or "-" before it was read. |

### an Insane founding (0.7.14) (lines 4083-5720)

| line | len | member | says |
|---:|---:|---|---|
| 4097 | 10 | `static void borrowForTheVillage(Game g)` | THE PLAYER OF AN INSANE CITY BORROWS FIRST: the build screen's twenty-year bond, sized to the village's invoice at a new city's prices (Founding.whatItBuys()), before it places a house - the treasury holds nothing and... |
| 4115 | 13 | `static boolean buildOnTheFundingPage(Game g, String name, int quantity)` | ...AND BUILDS THE REST OF IT THE SAME WAY: a stage the treasury is short of is borrowed for on the funding page's bond, sized to the gap (Game.buildFundingGap()), and placed - the page a player is shown, where the adv... |
| 4129 | 1422 | `public static void main(String[] args) throws Exception` |  |
| 5559 | 10 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 5578 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 5604 | 33 | `static String bankEra(Game g)` | The bank as a business at a checkpoint, on one line (0.7.7): its price build-up at the dial - funds-transfer price, running costs, expected loss and capital charge, adding to prime - then what it paid savers and what ... |
| 5639 | 7 | `static double depositBeta()` | The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. |
| 5647 | 57 | `static String era(Game g, String label)` |  |
| 5705 | 7 | `static String money(double v)` |  |
| 5713 | 7 | `static void cleanUp(Path root)` |  |


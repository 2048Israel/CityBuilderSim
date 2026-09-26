# LongPlaytest.java - 4,744 lines · 64 methods · 61 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (52), [BusinessDebtManager](BusinessDebtManager.md) (38), [Good](Good.md) (35), [Exchange](Exchange.md) (24), [Bank](Bank.md) (21), [Sectors](Sectors.md) (21), [Sector](Sector.md) (19), [Equity](Equity.md) (16), [Crime](Crime.md) (13), [AgeBand](AgeBand.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (12), [CareType](CareType.md) (12), [DebtManager](DebtManager.md) (11), [EconomyManager](EconomyManager.md) (8), [HouseholdBalance](HouseholdBalance.md) (8), [TaxPolicy](TaxPolicy.md) (8), [InfrastructureManager](InfrastructureManager.md) (7), [ForeignAccounts](ForeignAccounts.md) (7), [InterimLoan](InterimLoan.md) (6), [FamilyModel](FamilyModel.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [CentralBank](CentralBank.md) (5), [Household](Household.md) (5), [Health](Health.md) (5), [Founding](Founding.md) (5), [PopulationManager](PopulationManager.md) (4), [BuildingManager](BuildingManager.md) (4), [CapitalFlows](CapitalFlows.md) (4), [JobType](JobType.md) (4), [OrderBook](OrderBook.md) (4)... and 40 more

**Used by (9):** [BondCheck](BondCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [LabourCheck](LabourCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 47 | FINDINGS |
| 66 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 108 | THE AUDIT |
| 179 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 1110 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 1670 | THE ADVISOR |
| 1688 | WHO IS PLAYING (2026-09-17) |
| 1730 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 1759 | THE FOUNDING, AS A CHOICE (0.7.10) |
| 1780 | THE TRACE, BESIDE THE REPORT (0.7.10) |
| 1988 | WAGES AGAINST THE INDEX (2026-09-21) |
| 2007 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 2026 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 2043 | THE CEILING, SET (0.7.2) |
| 2058 | THE TARGET, SET (0.7.4) |
| 2226 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 2281 | · AND EVERYTHING THAT IS A PURCHASE. |
| 2570 | · AND THE BEST OF THEM WINS. |
| 2846 | RUNNING MONTHS |
| 3030 | SAVE / RELOAD, MID-RUN |
| 3289 | (untitled) |
| 3336 | · · founding: a few months at a time, by hand |
| 3426 | · · then the real rhythm |
| 3573 | · · the report |
| 3667 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |

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
| 350 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 493 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 505 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 509 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 544 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 614 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 625 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 626 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 627 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 628 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 629 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 630 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 631 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 634 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 637 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 638 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 639 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 640 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 643 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 644 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 645 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 646 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 709 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 710 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 711 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 712 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 714 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 718 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they left unpaid and wha... |
| 720 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 721 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1009 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1718 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 1725 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1751 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1772 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").t...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 1817 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 1820 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 1977 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 1985 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE...` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2005 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2024 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2040 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2055 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2071 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2146 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 2591 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 2801 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 2833 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |

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
| 339 | `static int salvageBuildingsDistress, salvageBuildingsSpare` |  |
| 340 | `static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed` |  |
| 349 | `static int leverageMonths, levBankMonths, branchesOpened, branchesClosedSeen, lastBranches` | THE LEVERAGE RATIO AND THE BRANCHES (0.7.11, round 2): how often the leverage requirement was the one that bound, the ratio at its lowest, and the branches opened - by where the bank's capital stood the month each one... |
| 351 | `static double lowestLeverage` |  |
| 352 | `static int lowestLeverageMonth` |  |
| 353 | `static int mortgagesRefusedForCapital` |  |
| 397 | `static double bondRunLoansDefaulted, bondRunLoansLost, bondRunBondsDefaulted, bondRunBondsLost` | THE BONDS (0.7.12): what defaulted in each class and what it lost - the recoveries the sources are read against - the months a sector owed bonds, and the peak share of the businesses' debt in bonds. |
| 398 | `static double bondPeakShare` |  |
| 399 | `static int bondPeakShareMonth, bondMonthsWithBonds` |  |
| 492 | `static int deskOverEquityMaxMonth` | ROUND 4 (0.7.8): the desk's inventory at the close against the bank's equity, month by month while a bank stands out of resolution - one in it holds its old inventory against the few dollars it has earned back, which ... |
| 494 | `static double deskOverEquityMax` |  |
| 506 | `static int shareListedMonths, shareTradedMonths, deskInventoryMonths, refusedAtLineMonths, projectsRefusedA...` |  |
| 507 | `static double deskPnlRun, deskInventorySum, ordinaryDividendsRun` |  |
| 508 | `static double refusedAtLineRun, notRenewedAtLineRun` |  |
| 615 | `static double companyDepositInterestRun, companiesSoldShortRun, buybackRestingRun` |  |
| 616 | `static int overdraftMonths, overdraftNoLenderMonths, overdraftProjectMonths` |  |
| 617 | `static double overdraftSum, overdraftNoLenderSum, overdraftMax, overdraftProjectSum` |  |
| 618 | `static int cannotPayMonths, cannotPayBankUnderLine` |  |
| 619 | `static double cannotPayForgivenRun, cannotPayDebtDefaultedRun, cannotPayForgivenBanned` |  |
| 621 | `static int ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineRationedMonths` | ---- round 5: the lines that stay open, the growth doors the capital rule still shuts, and interim financing ---- |
| 622 | `static double lineLentRationedRun, lineLentPastOldRuleRun` |  |
| 623 | `static int interimWritten, interimRepaid, interimDefaultedAgain, interimRefusedPast, interimRefusedShut, ba...` |  |
| 624 | `static double interimWrittenSum, interimRepaidSum, interimWrittenOffSum, backstopInBanSum` |  |
| 632 | `static int longestCannotPaySpell, longestCannotPayStart, longestCannotPayBanned` |  |
| 633 | `static String longestCannotPaySector` |  |
| 641 | `static int defaultsAfterCut, defaultsOnStock, defaultsOnBondCost` |  |
| 642 | `static double defaultsAfterCutSum, defaultsOnStockSum, defaultsOnBondCostSum` |  |
| 719 | `static int afterProjectCeiling` |  |
| 722 | `static double shellUnpaid, shellInterim` |  |
| 723 | `static int shellMonths, shellLongest, shellLongestFrom` |  |
| 724 | `static String shellLongestWho` |  |
| 1018 | `static int worstCrimeMonth` |  |
| 1019 | `static double lastSickRate` |  |
| 1020 | `static double workLostToIllness` |  |
| 1021 | `static int monthsObserved` |  |
| 1022 | `static double totalDepartures` |  |
| 1023 | `static double totalArrivals` |  |
| 1026 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 1728 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 1818 | `static java.io.PrintWriter traceMonths, traceBorrow, traceHouse, tracePop, traceBank, traceBonds` |  |
| 2081 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 2082 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 2090 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 2091 | `static double landLocalRun` |  |
| 2093 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 2094 | `static double vaultLow` |  |
| 2097 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 2856 | `static int refusedSkips` |  |
| 2871 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 2872 | `static int strainMonths, monthsWithoutCapacity` |  |
| 2873 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 2881 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 2882 | `static double peakAdvances, peakArrears` |  |
| 2891 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 2892 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 2901 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 2902 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 3291 | `static double lifetimeWriteOffs` |  |
| 3292 | `static double lifetimeBailouts` |  |
| 3293 | `static int failuresSeen` |  |
| 3295 | `static double lowestPaidIn` | The bank's paid-in capital at its lowest, the month, and the most its two parts were ever off its equity (0.7.13, round 2). |
| 3296 | `static int lowestPaidInMonth` |  |
| 3297 | `static double lifetimeHouseholdWriteOffs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 4705 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

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

### A mechanic that is not counted over a real run is a mechanic nobody (lines 179-1669)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 17 | `static String houseReason(String line)` | The landlords' month in a word, off the advisor's line: what built it, or what stopped it. |
| 331 | 8 | `static void countMortgages(Game g)` |  |
| 355 | 35 | `static void countLeverage(Game g)` |  |
| 401 | 53 | `static void countBonds(Game g)` |  |
| 455 | 27 | `static void countPriceAndPlant(Game g)` |  |
| 511 | 1 | `static double pct(double part, double whole)` |  |
| 513 | 30 | `static void countShares(Game g)` |  |
| 546 | 10 | `static void countCapitalLimits(Game g)` |  |
| 558 | 5 | `static double maxOf(java.util.List<Double> v)` | The largest of a list, 0 when it is empty. |
| 565 | 6 | `static double quantile(java.util.List<Double> v, double p)` | The p-th quantile of a list, 0 when it is empty. |
| 572 | 40 | `static void countDefaults(Game g)` |  |
| 648 | 59 | `static void countRound6(Game g)` |  |
| 726 | 42 | `static void countRound7(Game g)` |  |
| 769 | 101 | `static void countRound4(Game g)` |  |
| 872 | 5 | `static double forgivenTotal(EconomyManager em)` | Every overdraft forgiven over the run: the backstop's, the one path left that forgives (round 4's cash-flow test forgave too; since round 5 it lends the rest as interim financing). |
| 879 | 6 | `static double shareOver(java.util.List<Double> v, double line)` | The share of a list's entries over a line, percent. |
| 886 | 110 | `static void bankYear(Game g)` |  |
| 998 | 9 | `static String yearSpread(int col, double scale)` | The median, least and most of one column over the growth years, as "m% (lo-hi%)". |
| 1012 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |
| 1028 | 626 | `static void audit(Game g)` |  |
| 1655 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 1662 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 1670-1687)

| line | len | member | says |
|---:|---:|---|---|
| 1686 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 1688-1729)

| line | len | member | says |
|---:|---:|---|---|
| 1722 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 1730-1758)

| line | len | member | says |
|---:|---:|---|---|
| 1755 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the currency may move. |

### THE FOUNDING, AS A CHOICE (0.7.10) (lines 1759-1779)

| line | len | member | says |
|---:|---:|---|---|
| 1776 | 3 | `static Founding founding()` | The city the run founds: Danzik, on the flag's preset, in the default world. |

### THE TRACE, BESIDE THE REPORT (0.7.10) (lines 1780-1987)

| line | len | member | says |
|---:|---:|---|---|
| 1827 | 15 | `static void notePaper(Game g, String purpose)` | Writes every piece of paper not yet written, with what it paid for - called the moment the advisor borrows, so the purpose is its own, and at the end of every month for whatever the game issued by itself. |
| 1843 | 38 | `static void traceOpen()` |  |
| 1882 | 27 | `static void traceMonth(Game g)` |  |
| 1916 | 17 | `static void tracePop(Game g)` | The people's month, one row of <prefix>-pop.csv (0.7.11, round 2): who lives here by age, what migration aimed at and did and why, and the two housing markets - the trace that asks why a city with the same jobs holds ... |
| 1935 | 24 | `static void traceHouse(Game g)` | The landlords' month, one row of <prefix>-house.csv (0.7.11). |
| 1960 | 9 | `static synchronized void traceClose()` |  |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 1988-2006)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 2007-2025)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 2026-2042)

### THE CEILING, SET (0.7.2) (lines 2043-2057)

### THE TARGET, SET (0.7.4) (lines 2058-2845)

| line | len | member | says |
|---:|---:|---|---|
| 2100 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 2107 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 2135 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 2148 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 2154 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 2214 | 367 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 2601 | 53 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 2664 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 2673 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 2697 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 2708 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 2713 | 4 | `static int qty(Game g, String name)` |  |
| 2722 | 55 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 2804 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 2806 | 18 | `static boolean canService(Game g, double extra)` |  |
| 2835 | 3 | `static void refusal(String why)` |  |
| 2839 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |

### RUNNING MONTHS (lines 2846-3029)

| line | len | member | says |
|---:|---:|---|---|
| 2904 | 17 | `static void countTheHolders(Game g)` |  |
| 2922 | 10 | `static void countTheCentralBank(Game g)` |  |
| 2933 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 2947 | 82 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 3030-3288)

| line | len | member | says |
|---:|---:|---|---|
| 3039 | 243 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 3283 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 3289-4744)

| line | len | member | says |
|---:|---:|---|---|
| 3299 | 1276 | `public static void main(String[] args) throws Exception` |  |
| 4583 | 10 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 4602 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 4628 | 33 | `static String bankEra(Game g)` | The bank as a business at a checkpoint, on one line (0.7.7): its price build-up at the dial - funds-transfer price, running costs, expected loss and capital charge, adding to prime - then what it paid savers and what ... |
| 4663 | 7 | `static double depositBeta()` | The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. |
| 4671 | 57 | `static String era(Game g, String label)` |  |
| 4729 | 7 | `static String money(double v)` |  |
| 4737 | 7 | `static void cleanUp(Path root)` |  |


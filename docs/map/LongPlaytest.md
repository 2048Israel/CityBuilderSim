# LongPlaytest.java - 3,751 lines · 45 methods · 29 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (43), [Good](Good.md) (30), [Sector](Sector.md) (15), [Bank](Bank.md) (14), [BusinessDebtManager](BusinessDebtManager.md) (13), [Sectors](Sectors.md) (13), [Crime](Crime.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (12), [CareType](CareType.md) (12), [Equity](Equity.md) (10), [DebtManager](DebtManager.md) (10), [Exchange](Exchange.md) (9), [HouseholdBalance](HouseholdBalance.md) (8), [TaxPolicy](TaxPolicy.md) (8), [AgeBand](AgeBand.md) (7), [InfrastructureManager](InfrastructureManager.md) (7), [FamilyModel](FamilyModel.md) (6), [ForeignAccounts](ForeignAccounts.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [EconomyManager](EconomyManager.md) (5), [CentralBank](CentralBank.md) (5), [Household](Household.md) (5), [Health](Health.md) (5), [PopulationManager](PopulationManager.md) (4), [BuildingManager](BuildingManager.md) (4), [CapitalFlows](CapitalFlows.md) (4), [JobType](JobType.md) (4), [WageBand](WageBand.md) (4), [Unemployment](Unemployment.md) (3), [Retail](Retail.md) (3)... and 28 more

**Used by (6):** [DenominationCheck](DenominationCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [LabourCheck](LabourCheck.md), [MonetaryCheck](MonetaryCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 47 | FINDINGS |
| 66 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 108 | THE AUDIT |
| 179 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 627 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 1186 | THE ADVISOR |
| 1204 | WHO IS PLAYING (2026-09-17) |
| 1246 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 1284 | WAGES AGAINST THE INDEX (2026-09-21) |
| 1303 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 1322 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 1339 | THE CEILING, SET (0.7.2) |
| 1354 | THE TARGET, SET (0.7.4) |
| 1522 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 1577 | · AND EVERYTHING THAT IS A PURCHASE. |
| 1866 | · AND THE BEST OF THEM WINS. |
| 2141 | RUNNING MONTHS |
| 2312 | SAVE / RELOAD, MID-RUN |
| 2550 | (untitled) |
| 2582 | · · founding: a few months at a time, by hand |
| 2672 | · · then the real rhythm |
| 2818 | · · the report |
| 2912 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |

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
| 342 | `LongPlaytest.ownRefusedRun` | `new double [ Exchange.Seller.values().length ]` |  |
| 343 | `LongPlaytest.deskRefusedRun` | `new double [ Exchange.Seller.values().length ]` |  |
| 344 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 526 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1234 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 1241 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1267 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1282 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 1301 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 1320 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 1336 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 1351 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 1367 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 1442 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 1887 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 2096 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 2128 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |

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
| 301 | `static int salvageBuildingsDistress, salvageBuildingsSpare` |  |
| 302 | `static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed` |  |
| 341 | `static int ownBoundMonths, deskBoundMonths, deskOverEquityMaxMonth` | ROUND 4 (0.7.8): the bank's own shares and its desk held to its capital. |
| 345 | `static double deskOverEquityMax` |  |
| 535 | `static int worstCrimeMonth` |  |
| 536 | `static double lastSickRate` |  |
| 537 | `static double workLostToIllness` |  |
| 538 | `static int monthsObserved` |  |
| 539 | `static double totalDepartures` |  |
| 540 | `static double totalArrivals` |  |
| 543 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 1244 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 1377 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 1378 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 1386 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 1387 | `static double landLocalRun` |  |
| 1389 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 1390 | `static double vaultLow` |  |
| 1393 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 2151 | `static int refusedSkips` |  |
| 2166 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 2167 | `static int strainMonths, monthsWithoutCapacity` |  |
| 2168 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 2176 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 2177 | `static double peakAdvances, peakArrears` |  |
| 2186 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 2187 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 2196 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 2197 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 2552 | `static double lifetimeWriteOffs` |  |
| 2553 | `static double lifetimeBailouts` |  |
| 2554 | `static int failuresSeen` |  |
| 2555 | `static double lifetimeHouseholdWriteOffs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 3712 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

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

### A mechanic that is not counted over a real run is a mechanic nobody (lines 179-1185)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 27 | `static void countPriceAndPlant(Game g)` |  |
| 347 | 16 | `static void countCapitalLimits(Game g)` |  |
| 365 | 6 | `static double quantile(java.util.List<Double> v, double p)` | The p-th quantile of a list, 0 when it is empty. |
| 372 | 36 | `static void countDefaults(Game g)` |  |
| 409 | 104 | `static void bankYear(Game g)` |  |
| 515 | 9 | `static String yearSpread(int col, double scale)` | The median, least and most of one column over the growth years, as "m% (lo-hi%)". |
| 529 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |
| 545 | 625 | `static void audit(Game g)` |  |
| 1171 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 1178 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 1186-1203)

| line | len | member | says |
|---:|---:|---|---|
| 1202 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 1204-1245)

| line | len | member | says |
|---:|---:|---|---|
| 1238 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 1246-1283)

| line | len | member | says |
|---:|---:|---|---|
| 1271 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the currency may move. |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 1284-1302)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 1303-1321)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 1322-1338)

### THE CEILING, SET (0.7.2) (lines 1339-1353)

### THE TARGET, SET (0.7.4) (lines 1354-2140)

| line | len | member | says |
|---:|---:|---|---|
| 1396 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 1403 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 1431 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 1444 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 1450 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 1510 | 367 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 1897 | 53 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 1960 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 1969 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 1993 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 2004 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 2009 | 4 | `static int qty(Game g, String name)` |  |
| 2018 | 54 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 2099 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 2101 | 18 | `static boolean canService(Game g, double extra)` |  |
| 2130 | 3 | `static void refusal(String why)` |  |
| 2134 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |

### RUNNING MONTHS (lines 2141-2311)

| line | len | member | says |
|---:|---:|---|---|
| 2199 | 17 | `static void countTheHolders(Game g)` |  |
| 2217 | 10 | `static void countTheCentralBank(Game g)` |  |
| 2228 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 2242 | 69 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 2312-2549)

| line | len | member | says |
|---:|---:|---|---|
| 2321 | 222 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 2544 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 2550-3751)

| line | len | member | says |
|---:|---:|---|---|
| 2557 | 1025 | `public static void main(String[] args) throws Exception` |  |
| 3590 | 10 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 3609 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 3635 | 33 | `static String bankEra(Game g)` | The bank as a business at a checkpoint, on one line (0.7.7): its price build-up at the dial - funds-transfer price, running costs, expected loss and capital charge, adding to prime - then what it paid savers and what ... |
| 3670 | 7 | `static double depositBeta()` | The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. |
| 3678 | 57 | `static String era(Game g, String label)` |  |
| 3736 | 7 | `static String money(double v)` |  |
| 3744 | 7 | `static void cleanUp(Path root)` |  |


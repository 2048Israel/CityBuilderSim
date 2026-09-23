# LongPlaytest.java - 3,309 lines · 37 methods · 22 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (37), [Good](Good.md) (30), [Sector](Sector.md) (15), [Sectors](Sectors.md) (13), [Crime](Crime.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (12), [CareType](CareType.md) (12), [Equity](Equity.md) (10), [DebtManager](DebtManager.md) (10), [HouseholdBalance](HouseholdBalance.md) (8), [TaxPolicy](TaxPolicy.md) (8), [AgeBand](AgeBand.md) (7), [InfrastructureManager](InfrastructureManager.md) (7), [BusinessDebtManager](BusinessDebtManager.md) (7), [FamilyModel](FamilyModel.md) (6), [ForeignAccounts](ForeignAccounts.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [EconomyManager](EconomyManager.md) (5), [CentralBank](CentralBank.md) (5), [Household](Household.md) (5), [Health](Health.md) (5), [PopulationManager](PopulationManager.md) (4), [BuildingManager](BuildingManager.md) (4), [Bank](Bank.md) (4), [CapitalFlows](CapitalFlows.md) (4), [JobType](JobType.md) (4), [WageBand](WageBand.md) (4), [Unemployment](Unemployment.md) (3), [Retail](Retail.md) (3), [BusinessServices](BusinessServices.md) (3)... and 27 more

**Used by (6):** [DenominationCheck](DenominationCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [LabourCheck](LabourCheck.md), [MonetaryCheck](MonetaryCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 47 | FINDINGS |
| 66 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 108 | THE AUDIT |
| 179 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 338 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 873 | THE ADVISOR |
| 891 | WHO IS PLAYING (2026-09-17) |
| 933 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 971 | WAGES AGAINST THE INDEX (2026-09-21) |
| 990 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 1009 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 1026 | THE CEILING, SET (0.7.2) |
| 1041 | THE TARGET, SET (0.7.4) |
| 1209 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 1264 | · AND EVERYTHING THAT IS A PURCHASE. |
| 1553 | · AND THE BEST OF THEM WINS. |
| 1828 | RUNNING MONTHS |
| 1999 | SAVE / RELOAD, MID-RUN |
| 2226 | (untitled) |
| 2258 | · · founding: a few months at a time, by hand |
| 2348 | · · then the real rhythm |
| 2493 | · · the report |
| 2587 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |

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
| 237 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 921 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 928 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 954 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 969 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 988 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 1007 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 1023 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 1038 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 1054 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 1129 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 1574 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 1783 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 1815 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |

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
| 234 | `static int depositCappedMonths` | Months the bank's quoted deposit rate was its lending rate instead of its payout (Bank.isDepositRateCapped(), 0.7.3), and the first and last of them. |
| 246 | `static int worstCrimeMonth` |  |
| 247 | `static double lastSickRate` |  |
| 248 | `static double workLostToIllness` |  |
| 249 | `static int monthsObserved` |  |
| 250 | `static double totalDepartures` |  |
| 251 | `static double totalArrivals` |  |
| 254 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 931 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 1064 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 1065 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 1073 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 1074 | `static double landLocalRun` |  |
| 1076 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 1077 | `static double vaultLow` |  |
| 1080 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 1838 | `static int refusedSkips` |  |
| 1853 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 1854 | `static int strainMonths, monthsWithoutCapacity` |  |
| 1855 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 1863 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 1864 | `static double peakAdvances, peakArrears` |  |
| 1873 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 1874 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 1883 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 1884 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 2228 | `static double lifetimeWriteOffs` |  |
| 2229 | `static double lifetimeBailouts` |  |
| 2230 | `static int failuresSeen` |  |
| 2231 | `static double lifetimeHouseholdWriteOffs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 3270 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

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

### A mechanic that is not counted over a real run is a mechanic nobody (lines 179-872)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |
| 256 | 601 | `static void audit(Game g)` |  |
| 858 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 865 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 873-890)

| line | len | member | says |
|---:|---:|---|---|
| 889 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 891-932)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 933-970)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the currency may move. |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 971-989)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 990-1008)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 1009-1025)

### THE CEILING, SET (0.7.2) (lines 1026-1040)

### THE TARGET, SET (0.7.4) (lines 1041-1827)

| line | len | member | says |
|---:|---:|---|---|
| 1083 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 1090 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 1118 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 1131 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 1137 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 1197 | 367 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 1584 | 53 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 1647 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 1656 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 1680 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 1691 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 1696 | 4 | `static int qty(Game g, String name)` |  |
| 1705 | 54 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 1786 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 1788 | 18 | `static boolean canService(Game g, double extra)` |  |
| 1817 | 3 | `static void refusal(String why)` |  |
| 1821 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |

### RUNNING MONTHS (lines 1828-1998)

| line | len | member | says |
|---:|---:|---|---|
| 1886 | 17 | `static void countTheHolders(Game g)` |  |
| 1904 | 10 | `static void countTheCentralBank(Game g)` |  |
| 1915 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 1929 | 69 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 1999-2225)

| line | len | member | says |
|---:|---:|---|---|
| 2008 | 211 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 2220 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 2226-3309)

| line | len | member | says |
|---:|---:|---|---|
| 2233 | 958 | `public static void main(String[] args) throws Exception` |  |
| 3199 | 10 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 3218 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 3236 | 57 | `static String era(Game g, String label)` |  |
| 3294 | 7 | `static String money(double v)` |  |
| 3302 | 7 | `static void cleanUp(Path root)` |  |


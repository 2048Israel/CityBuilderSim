# LongPlaytest.java - 2,707 lines · 30 methods · 10 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (30), [Game](Game.md) (29), [Sector](Sector.md) (15), [Sectors](Sectors.md) (13), [Crime](Crime.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (12), [CareType](CareType.md) (12), [Equity](Equity.md) (10), [TaxPolicy](TaxPolicy.md) (8), [AgeBand](AgeBand.md) (7), [InfrastructureManager](InfrastructureManager.md) (7), [BusinessDebtManager](BusinessDebtManager.md) (7), [HouseholdBalance](HouseholdBalance.md) (7), [FamilyModel](FamilyModel.md) (6), [DebtManager](DebtManager.md) (6), [UnemployedHousehold](UnemployedHousehold.md) (6), [EconomyManager](EconomyManager.md) (5), [Household](Household.md) (5), [Health](Health.md) (5), [PopulationManager](PopulationManager.md) (4), [BuildingManager](BuildingManager.md) (4), [Bank](Bank.md) (4), [CapitalFlows](CapitalFlows.md) (4), [JobType](JobType.md) (4), [WageBand](WageBand.md) (4), [Unemployment](Unemployment.md) (3), [Retail](Retail.md) (3), [BusinessServices](BusinessServices.md) (3), [GoodsMarket](GoodsMarket.md) (3), [LandParcel](LandParcel.md) (3)... and 23 more

**Used by (4):** [DenominationCheck](DenominationCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [ShadowBasket](ShadowBasket.md)

## Sections

| line | section |
|---:|---|
| 47 | FINDINGS |
| 66 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 108 | THE AUDIT |
| 179 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 236 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 741 | THE ADVISOR |
| 759 | WHO IS PLAYING (2026-09-17) |
| 910 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 961 | · AND EVERYTHING THAT IS A PURCHASE. |
| 1250 | · AND THE BEST OF THEM WINS. |
| 1525 | RUNNING MONTHS |
| 1593 | SAVE / RELOAD, MID-RUN |
| 1799 | (untitled) |
| 1828 | · · founding: a few months at a time, by hand |
| 1918 | · · then the real rhythm |
| 2044 | · · the report |
| 2138 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `LongPlaytest.TARGET_MONTHS` | `4000` |  |
| 55 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 161 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 163 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 789 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 796 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 830 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 1271 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 1480 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 1512 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |

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
| 192 | `static int worstCrimeMonth` |  |
| 193 | `static double lastSickRate` |  |
| 194 | `static double workLostToIllness` |  |
| 195 | `static int monthsObserved` |  |
| 196 | `static double totalDepartures` |  |
| 197 | `static double totalArrivals` |  |
| 799 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 1535 | `static int refusedSkips` |  |
| 1801 | `static double lifetimeWriteOffs` |  |
| 1802 | `static double lifetimeBailouts` |  |
| 1803 | `static int failuresSeen` |  |
| 1804 | `static double lifetimeHouseholdWriteOffs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 2668 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

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

### A mechanic that is not counted over a real run is a mechanic nobody (lines 179-740)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 526 | `static void audit(Game g)` |  |
| 726 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 733 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 741-758)

| line | len | member | says |
|---:|---:|---|---|
| 757 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 759-1524)

| line | len | member | says |
|---:|---:|---|---|
| 793 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |
| 819 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 832 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 838 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 898 | 363 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 1281 | 53 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 1344 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 1353 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 1377 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 1388 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 1393 | 4 | `static int qty(Game g, String name)` |  |
| 1402 | 54 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 1483 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 1485 | 18 | `static boolean canService(Game g, double extra)` |  |
| 1514 | 3 | `static void refusal(String why)` |  |
| 1518 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |

### RUNNING MONTHS (lines 1525-1592)

| line | len | member | says |
|---:|---:|---|---|
| 1537 | 55 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 1593-1798)

| line | len | member | says |
|---:|---:|---|---|
| 1602 | 190 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 1793 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 1799-2707)

| line | len | member | says |
|---:|---:|---|---|
| 1806 | 783 | `public static void main(String[] args) throws Exception` |  |
| 2597 | 10 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 2616 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 2634 | 57 | `static String era(Game g, String label)` |  |
| 2692 | 7 | `static String money(double v)` |  |
| 2700 | 7 | `static void cleanUp(Path root)` |  |


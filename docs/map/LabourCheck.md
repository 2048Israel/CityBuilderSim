# LabourCheck.java - 1,833 lines · 19 methods · 14 constants · harnesses

`ham/citybuildersim/LabourCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Verifies the labour market: who can hold a job, and what it costs. Not part
> of the game.
> 
> WHY THIS EXISTS
> 
> Two separate things were wrong and both were silent.
> 
> PopulationManager.getJobVacancy() walked the job array from the top index
> down, handing an undifferentiated pool of adults to the most-skilled posts
> first. A measured city of 9,016 people staffed 220 doctor posts at 100% with
> no school, college or university anywhere in the game, while a third of its
> workers sat idle. The eleven job types were real on the demand side and
> imaginary on the supply side, and nothing on any screen said so.
> 
> And a wage was one of six constants. A city that could not staff a hospital
> paid its doctors exactly what a city with a queue of them paid, so the one
> price that should have been screaming was the one number that never moved.
> 
> Both failures produce a city that looks completely normal, which is why every
> assertion here is about a QUANTITY rather than about a screen.
> 
> AND THE FLOOR IS A REAL MINIMUM (WG1, 2026-11-10). What this proves about it:
> the ladder is anchored on the going rate and pays what PayTier paid; a new
> city's floor starts at DEFAULT_FLOOR_SHARE of the going rate, with no job paid
> it; the dial lifts only the wages under it and leaves the ladder's bases
> alone; the floor's tie (prices, the average wage, nothing) holds the floor
> where each says, to the dial's bounds, in both forms of the cost of living's
> walk; the going rate is redenominated with the floor; and the tie is saved,
> an older array reading prices at the default share. And (WS1, 2026-11-10) the average the tie last read is saved and scaled by a reform, so a
> dial move after a load or in a reform month reads its share against the right average, and the tie's share stops short of the ratchet: the range
> is the world file's ratios, never 1, and a floor tied at its top settles under the mean of the wages it does not hold.
> 
> AND THE CONDITIONS ARE CAUSED, NOT PLAYED (FS16, 2026-11-20). The staffing law (who a city can staff), the rent that is not a wage
> formula, the arrivals' adult share, the doctor shortage and who moves in at and over the going rate read a condition a played town
> happened to be in, so they read it by hand: a population manager fed a workforce, skilled heads, posts and licences and a labour market
> priced on it (handFed, pricedOn), one month of arrivals drawn into it (arrivalsOnce), a town one month old beside a control for the rent,
> and a built town one month in for the arrivals' month. Each asserts its premise first, in a line that starts "fixture:".
> The sections that keep a played town for their breadth (the tie's average through a load and a reform, the wage chase against the index,
> the payroll by job type) cause the rest the same way: the tie's floor is struck on an average fed by hand (TIE_AVERAGE_OVER_GOING_RATE),
> the wage chase's basket is stepped to its base and its crawl is the lever whose inflation is asserted as a fixture, and the payroll's
> town has its heads put by hand (PAYROLL_GRADUATE_FILL) on the posts it lived to and its builders struck once with nothing on site.

**Uses:** [LabourMarket](LabourMarket.md) (157), [JobType](JobType.md) (104), [WageBand](WageBand.md) (75), [Game](Game.md) (24), [Migration](Migration.md) (13), [PopulationManager](PopulationManager.md) (11), [GameFiles](GameFiles.md) (8), [PriceLevel](PriceLevel.md) (6), [Sector](Sector.md) (6), [BuildingType](BuildingType.md) (6), [PayTier](PayTier.md) (5), [WageLadder](WageLadder.md) (4), [RealEstate](RealEstate.md) (3), [BuildingManager](BuildingManager.md) (3), [Construction](Construction.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [DecisionLog](DecisionLog.md) (2), [AgeBand](AgeBand.md) (2), [WorldMeasures](WorldMeasures.md) (2), [FamilyModel](FamilyModel.md) (2), [TempDirs](TempDirs.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Sectors](Sectors.md) (1), [PriceIndex](PriceIndex.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (4):** [EducationCheck](EducationCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [SchoolingLadderCheck](SchoolingLadderCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md)

## Sections

| line | section |
|---:|---|
| 87 | · 1. THE GOING RATE ANCHORS THE LADDER, AND THE FLOOR STARTS UNDER IT |
| 147 | · 2. THE DIAL MOVES ONLY THE WAGES UNDER IT |
| 225 | · 2b. THE FLOOR'S TIE |
| 359 | · 3. SCARCITY RAISES IT, SURPLUS LOWERS IT |
| 433 | · 4. IT IS LAGGED, WHICH IS THE POINT |
| 477 | · 5. NOBODY IS A DOCTOR WHO IS NOT A DOCTOR |
| 566 | · 6. AND IT SURVIVES A SAVE |
| 655 | · · RENT NO LONGER FOLLOWS THE UNSKILLED WAGE (2026-09-07). |
| 756 | · · ARRIVING CHILDREN ARE NOT GRADUATES (2026-09-06). |
| 783 | · · CAUSED, NOT HUNTED FOR (FS16). |
| 850 | · · A DOCTOR SHORTAGE IS PRICED ON DOCTORS (2026-09-06). |
| 909 | · · WHO MOVES IN, AND WHY (2026-09-07). |
| 954 | · · SOME ARRIVE WITHOUT A DIPLOMA, FOR THE WAGE (0.7.18). |
| 1055 | · 7. THE TIE'S AVERAGE SURVIVES A LOAD AND A REFORM (WS1) |
| 1150 | · 8. THE TIE'S SHARE STOPS SHORT OF THE RATCHET (WS1) |
| 1249 | · WAGES AGAINST THE INDEX (2026-09-21). |
| 1505 | 13. PAYROLL BY JOB TYPE (0.7.17) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `LabourCheck.OUT` | `System.out` |  |
| 57 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 1281 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 1284 | `LabourCheck.BASING_GRACE_MONTHS` | `120` | The most months the wage-chase town is stepped on past its settling for the basket to be based, before that is a fixture failure. |
| 1287 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |
| 1526 | `LabourCheck.PAYROLL_GRADUATE_FILL` | `.5` | The share of its posts each graduate band holds in heads in the payroll town (FS16), so a band is part filled and the job types fill unevenly. |
| 1529 | `LabourCheck.PAYROLL_TOWN_MONTHS` | `120` | The months the payroll town plays before its fills are put by hand (FS16): ten years, so the identity reads the sectors and posts a lived city has, not a founding's (a KEEP: the played town is its breadth). |
| 1532 | `LabourCheck.LAYOFF_TOWN_PEOPLE` | `1_000` | The people put into the builders' town of the payroll section (FS16), so the sector's households have workers to be paid. |
| 1761 | `LabourCheck.RENT_TOWN_PEOPLE` | `4_000` | People put into the town "rent is a market" is read on, so its labourers outnumber its posts and the floor, not scarcity, is what holds their wage (FS16). |
| 1783 | `LabourCheck.ARRIVALS_OVER_DEPARTURES` | `10` | How many times the month's skilled arrivals must outnumber its departures for it to be one the adult share of the ARRIVALS can be read off (FS16). |
| 1786 | `LabourCheck.PRICED_MONTHS` | `60` | Months a hand-fed market is priced against its posts: ADJUST_RATE a month closes all but under a thousandth of the gap to its target in this many. |
| 1798 | `LabourCheck.ROOM_TOWN` | `4000` | The town a hand-drawn month of arrivals is made into: this many people, with room, jobs and homes for more (ArrivalMixCheck.draw()'s size). |
| 1816 | `LabourCheck.TIE_AVERAGE_OVER_GOING_RATE` | `1.3` | The average wage the tie's section feeds a market by hand, over its going rate: with its shares .5 and .6 the floor lands inside the dial's bounds. |
| 1818 | `LabourCheck.TIE_NEXT_AVERAGE_OVER_LAST` | `1.1` | The next month's average in the tie's section, over the last one fed: a step off it, so a floor struck on the old average is told from one on the new. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 1291 | `final java.util.List<Double> index` |  |
| 1292 | `double worstTarget, worstStep` |  |
| 1293 | `int outsideWindow, windowMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 1781 | **type** `public class LabourCheck` | Verifies the labour market: who can hold a job, and what it costs. |
| 61 | 4 | `static void quietly(Runnable work)` |  |
| 66 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 71 | 6 | `static void close(String label, double a, double b, double tol)` |  |
| 78 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 85 | 1163 | `public static void main(String[] args) throws Exception` |  |

### WAGES AGAINST THE INDEX (2026-09-21). (lines 1249-1504)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 40 | **type** `static final class LagWatch` | The recurrence, checked month by month on one city: what it was handed and what it did. |
| 1296 | 33 | `void month(Game g, Runnable play)` _(in LabourCheck.LagWatch)_ | One month, played; the index and the expectation the month was handed are the ones it read at its top. |
| 1332 | 4 | `static void crawl(Game g)` | The currency one CRAWL weaker, and held there for the month. |
| 1337 | 167 | `static void wagesAgainstTheIndex()` |  |

### 13. PAYROLL BY JOB TYPE (0.7.17) (lines 1505-1833)

| line | len | member | says |
|---:|---:|---|---|
| 1534 | 177 | `static void payrollByJobType()` |  |
| 1713 | 12 | `static double everyOtherEmployer(Game g)` | Every payroll that is not a sector's: the bank, the utilities, and the four services the city staffs. |
| 1726 | 5 | `static double sum(double[] a)` |  |
| 1732 | 3 | `static double pct(double[] mix, WageBand band, double total)` |  |
| 1736 | 8 | `static double staffed(long[] jobs, long[] vacancy, WageBand band)` |  |
| 1750 | 9 | `static PopulationManager handFed(long workforce, double[] skilledHeads, long[] posts, double[] licensed)` | A population manager fed by hand (FS16): this workforce, these skilled heads by band (the unskilled are what is left), these posts by job type and these licence holders by job type. |
| 1764 | 17 | `static Game rentTownBuilt()` | The town "rent is a market" is read on (FS16): flats, a hospital, shops, power, water and roads and RENT_TOWN_PEOPLE in them, one month old, so the landlords have struck a rent and priced a home. |
| 1789 | 7 | `static LabourMarket pricedOn(PopulationManager pm)` | A labour market priced on a hand-fed population manager the way Game.repriceLabour() prices a city's: staffable posts, supply, posts by job and licence holders. |
| 1804 | 10 | `static Migration arrivalsOnce(LabourMarket market, PopulationManager pm)` | One month's draw by a fresh Migration into a town of ROOM_TOWN people with room, jobs and homes for more, priced by this market and placed against these people (so each band's chance of work is the people's own, not o... |
| 1821 | 4 | `static double lastAverageOf(LabourMarket m)` | The average wage a market's tie last read: the last value of its state array (WS1; there is no getter for it). |
| 1826 | 7 | `static void cleanUp(Path root)` |  |


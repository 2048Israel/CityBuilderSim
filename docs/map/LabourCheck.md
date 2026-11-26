# LabourCheck.java - 2,149 lines · 26 methods · 22 constants · harnesses

`ham/citybuildersim/LabourCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
> 
> AND THE FIGURES A PASS HOLDS ARE THE BARE ONES (AP5, 2026-11-26). While a WorksValue scope is open the vacancies, the posts and a sector's buildings are
> held, not recomputed: on a town with people, posts in several sectors, the builders' crews laid off and a gated post, each held figure is the bare one
> to the bit, read afresh once what it was read from has moved (a build, an order, a scrapping, a building's posts, the people by every path) and not
> before, and a closed scope holds nothing (section 14).

**Uses:** [LabourMarket](LabourMarket.md) (157), [JobType](JobType.md) (121), [WageBand](WageBand.md) (77), [Game](Game.md) (28), [WorksValue](WorksValue.md) (26), [PopulationManager](PopulationManager.md) (14), [Migration](Migration.md) (13), [GameFiles](GameFiles.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (7), [PriceLevel](PriceLevel.md) (7), [Sector](Sector.md) (6), [BuildingType](BuildingType.md) (6), [PayTier](PayTier.md) (5), [BuildingManager](BuildingManager.md) (5), [WageLadder](WageLadder.md) (4), [RealEstate](RealEstate.md) (3), [Construction](Construction.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [DecisionLog](DecisionLog.md) (2), [AgeBand](AgeBand.md) (2), [WorldMeasures](WorldMeasures.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [FamilyModel](FamilyModel.md) (2), [TempDirs](TempDirs.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Sectors](Sectors.md) (1), [PriceIndex](PriceIndex.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (4):** [EducationCheck](EducationCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [SchoolingLadderCheck](SchoolingLadderCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md)

## Sections

| line | section |
|---:|---|
| 92 | · 1. THE GOING RATE ANCHORS THE LADDER, AND THE FLOOR STARTS UNDER IT |
| 152 | · 2. THE DIAL MOVES ONLY THE WAGES UNDER IT |
| 230 | · 2b. THE FLOOR'S TIE |
| 364 | · 3. SCARCITY RAISES IT, SURPLUS LOWERS IT |
| 438 | · 4. IT IS LAGGED, WHICH IS THE POINT |
| 482 | · 5. NOBODY IS A DOCTOR WHO IS NOT A DOCTOR |
| 571 | · 6. AND IT SURVIVES A SAVE |
| 660 | · · RENT NO LONGER FOLLOWS THE UNSKILLED WAGE (2026-09-07). |
| 761 | · · ARRIVING CHILDREN ARE NOT GRADUATES (2026-09-06). |
| 788 | · · CAUSED, NOT HUNTED FOR (FS16). |
| 855 | · · A DOCTOR SHORTAGE IS PRICED ON DOCTORS (2026-09-06). |
| 914 | · · WHO MOVES IN, AND WHY (2026-09-07). |
| 959 | · · SOME ARRIVE WITHOUT A DIPLOMA, FOR THE WAGE (0.7.18). |
| 1060 | · 7. THE TIE'S AVERAGE SURVIVES A LOAD AND A REFORM (WS1) |
| 1155 | · 8. THE TIE'S SHARE STOPS SHORT OF THE RATCHET (WS1) |
| 1255 | · WAGES AGAINST THE INDEX (2026-09-21). |
| 1511 | 13. PAYROLL BY JOB TYPE (0.7.17) |
| 1718 | 14. THE LABOUR FIGURES HELD FOR A PASS ARE THE BARE ONES (AP5, 2026-11-26) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `LabourCheck.OUT` | `System.out` |  |
| 62 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 1287 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 1290 | `LabourCheck.BASING_GRACE_MONTHS` | `120` | The most months the wage-chase town is stepped on past its settling for the basket to be based, before that is a fixture failure. |
| 1293 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |
| 1532 | `LabourCheck.PAYROLL_GRADUATE_FILL` | `.5` | The share of its posts each graduate band holds in heads in the payroll town (FS16), so a band is part filled and the job types fill unevenly. |
| 1535 | `LabourCheck.PAYROLL_TOWN_MONTHS` | `120` | The months the payroll town plays before its fills are put by hand (FS16): ten years, so the identity reads the sectors and posts a lived city has, not a founding's (a KEEP: the played town is its breadth). |
| 1538 | `LabourCheck.LAYOFF_TOWN_PEOPLE` | `1_000` | The people put into the builders' town of the payroll section (FS16), so the sector's households have workers to be paid. |
| 1738 | `LabourCheck.HELD_FILL` | `.5` | The share of its posts each graduate band holds in heads, and the unskilled posts in workers, in the held-figures town (AP5): every band has posts open for a move to fill. |
| 1741 | `LabourCheck.HELD_TOWN_PEOPLE` | `1_000` | The people put into the held-figures town (AP5), so it has a workforce for the month's labour market to be struck on. |
| 1744 | `LabourCheck.HELD_INSTANT` | `5` | The buildings put up at once, in the held-figures section (AP5). |
| 1747 | `LabourCheck.HELD_ON_SITE` | `3` | The buildings ordered on site, in the held-figures section (AP5). |
| 1750 | `LabourCheck.HELD_SCRAPPED` | `2` | The buildings scrapped, in the held-figures section (AP5). |
| 1753 | `LabourCheck.HELD_IMPRISONED` | `.1` | The share of the unskilled workers put in prison, in the held-figures section (AP5). |
| 1756 | `LabourCheck.HELD_HEADS_ADDED` | `.25` | The share of a band's posts written onto its heads through the array getSkilledHeads() hands out, in the held-figures section (AP5). |
| 1759 | `LabourCheck.HELD_LICENSED` | `.25` | The share of the university heads who take a doctor's licence, in the held-figures section (AP5). |
| 2077 | `LabourCheck.RENT_TOWN_PEOPLE` | `4_000` | People put into the town "rent is a market" is read on, so its labourers outnumber its posts and the floor, not scarcity, is what holds their wage (FS16). |
| 2099 | `LabourCheck.ARRIVALS_OVER_DEPARTURES` | `10` | How many times the month's skilled arrivals must outnumber its departures for it to be one the adult share of the ARRIVALS can be read off (FS16). |
| 2102 | `LabourCheck.PRICED_MONTHS` | `60` | Months a hand-fed market is priced against its posts: ADJUST_RATE a month closes all but under a thousandth of the gap to its target in this many. |
| 2114 | `LabourCheck.ROOM_TOWN` | `4000` | The town a hand-drawn month of arrivals is made into: this many people, with room, jobs and homes for more (ArrivalMixCheck.draw()'s size). |
| 2132 | `LabourCheck.TIE_AVERAGE_OVER_GOING_RATE` | `1.3` | The average wage the tie's section feeds a market by hand, over its going rate: with its shares .5 and .6 the floor lands inside the dial's bounds. |
| 2134 | `LabourCheck.TIE_NEXT_AVERAGE_OVER_LAST` | `1.1` | The next month's average in the tie's section, over the last one fed: a step off it, so a floor struck on the old average is told from one on the new. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 1297 | `final java.util.List<Double> index` |  |
| 1298 | `double worstTarget, worstStep` |  |
| 1299 | `int outsideWindow, windowMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 2092 | **type** `public class LabourCheck` | Verifies the labour market: who can hold a job, and what it costs. |
| 66 | 4 | `static void quietly(Runnable work)` |  |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 6 | `static void close(String label, double a, double b, double tol)` |  |
| 83 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 90 | 1164 | `public static void main(String[] args) throws Exception` |  |

### WAGES AGAINST THE INDEX (2026-09-21). (lines 1255-1510)

| line | len | member | says |
|---:|---:|---|---|
| 1296 | 40 | **type** `static final class LagWatch` | The recurrence, checked month by month on one city: what it was handed and what it did. |
| 1302 | 33 | `void month(Game g, Runnable play)` _(in LabourCheck.LagWatch)_ | One month, played; the index and the expectation the month was handed are the ones it read at its top. |
| 1338 | 4 | `static void crawl(Game g)` | The currency one CRAWL weaker, and held there for the month. |
| 1343 | 167 | `static void wagesAgainstTheIndex()` |  |

### 13. PAYROLL BY JOB TYPE (0.7.17) (lines 1511-1717)

| line | len | member | says |
|---:|---:|---|---|
| 1540 | 177 | `static void payrollByJobType()` |  |

### 14. THE LABOUR FIGURES HELD FOR A PASS ARE THE BARE ONES (AP5, 2026-11-26) (lines 1718-2149)

| line | len | member | says |
|---:|---:|---|---|
| 1762 | 5 | `static double allPosts(BuildingsTemplate b)` | Every post of a template, over the job types: the getter a sector's buildings are read through in the held-figures section (AP5). |
| 1769 | 3 | `static boolean sameBits(double a, double b)` | A held figure is the bare one to the bit, never within a tolerance (AP5). |
| 1774 | 5 | `static boolean sameBits(double[] a, double[] b)` | ...each of two arrays. |
| 1781 | 15 | `static Game heldTown()` | The town the held-figures section is read on (AP5): flats, shops, a hospital and the builders' depots, stood up free and a month old, so the depots lay crews off. |
| 1801 | 10 | `static long[] heldAfter(String move, WorksValue.Scope s, PopulationManager pm, long[] stood)` | The vacancies held for a scope after the people were moved (AP5): the figure is the bare fill, it was read afresh once, a second read with nothing moved is not another fill, and the move did change the fill (else a fi... |
| 1816 | 13 | `static void postsMovedBy(String change, BuildingManager bm, WorksValue.Scope s, BuildingsTemplate of, long by, Runnable move)` | One change to a stack's buildings inside the scope (AP5): the city's posts at the next reading have moved by this many of the template's posts of each job type, are the bare reading, and were built afresh once. |
| 1830 | 197 | `static void labourHeldForAPass()` |  |
| 2029 | 12 | `static double everyOtherEmployer(Game g)` | Every payroll that is not a sector's: the bank, the utilities, and the four services the city staffs. |
| 2042 | 5 | `static double sum(double[] a)` |  |
| 2048 | 3 | `static double pct(double[] mix, WageBand band, double total)` |  |
| 2052 | 8 | `static double staffed(long[] jobs, long[] vacancy, WageBand band)` |  |
| 2066 | 9 | `static PopulationManager handFed(long workforce, double[] skilledHeads, long[] posts, double[] licensed)` | A population manager fed by hand (FS16): this workforce, these skilled heads by band (the unskilled are what is left), these posts by job type and these licence holders by job type. |
| 2080 | 17 | `static Game rentTownBuilt()` | The town "rent is a market" is read on (FS16): flats, a hospital, shops, power, water and roads and RENT_TOWN_PEOPLE in them, one month old, so the landlords have struck a rent and priced a home. |
| 2105 | 7 | `static LabourMarket pricedOn(PopulationManager pm)` | A labour market priced on a hand-fed population manager the way Game.repriceLabour() prices a city's: staffable posts, supply, posts by job and licence holders. |
| 2120 | 10 | `static Migration arrivalsOnce(LabourMarket market, PopulationManager pm)` | One month's draw by a fresh Migration into a town of ROOM_TOWN people with room, jobs and homes for more, priced by this market and placed against these people (so each band's chance of work is the people's own, not o... |
| 2137 | 4 | `static double lastAverageOf(LabourMarket m)` | The average wage a market's tie last read: the last value of its state array (WS1; there is no getter for it). |
| 2142 | 7 | `static void cleanUp(Path root)` |  |


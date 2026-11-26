# LabourCheck.java - 2,550 lines · 31 methods · 35 constants · harnesses

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

**Uses:** [LabourMarket](LabourMarket.md) (157), [JobType](JobType.md) (142), [WageBand](WageBand.md) (79), [WorksValue](WorksValue.md) (45), [Game](Game.md) (29), [BuildingsTemplate](BuildingsTemplate.md) (21), [PopulationManager](PopulationManager.md) (18), [Migration](Migration.md) (13), [BuildingManager](BuildingManager.md) (12), [Recipe](Recipe.md) (10), [GameFiles](GameFiles.md) (9), [WageLadder](WageLadder.md) (8), [BuildingsStacks](BuildingsStacks.md) (8), [PriceLevel](PriceLevel.md) (7), [Sector](Sector.md) (6), [BuildingType](BuildingType.md) (6), [PayTier](PayTier.md) (5), [RealEstate](RealEstate.md) (3), [Construction](Construction.md) (3), [PopulationCohorts](PopulationCohorts.md) (3), [DecisionLog](DecisionLog.md) (2), [AgeBand](AgeBand.md) (2), [WorldMeasures](WorldMeasures.md) (2), [FamilyModel](FamilyModel.md) (2), [TempDirs](TempDirs.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Sectors](Sectors.md) (1), [PriceIndex](PriceIndex.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [EconomyManager](EconomyManager.md) (1)... and 1 more

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
| 1256 | · WAGES AGAINST THE INDEX (2026-09-21). |
| 1512 | 13. PAYROLL BY JOB TYPE (0.7.17) |
| 1719 | 14. THE LABOUR FIGURES HELD FOR A PASS ARE THE BARE ONES (AP5, 2026-11-26) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `LabourCheck.OUT` | `System.out` |  |
| 62 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 1288 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 1291 | `LabourCheck.BASING_GRACE_MONTHS` | `120` | The most months the wage-chase town is stepped on past its settling for the basket to be based, before that is a fixture failure. |
| 1294 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |
| 1533 | `LabourCheck.PAYROLL_GRADUATE_FILL` | `.5` | The share of its posts each graduate band holds in heads in the payroll town (FS16), so a band is part filled and the job types fill unevenly. |
| 1536 | `LabourCheck.PAYROLL_TOWN_MONTHS` | `120` | The months the payroll town plays before its fills are put by hand (FS16): ten years, so the identity reads the sectors and posts a lived city has, not a founding's (a KEEP: the played town is its breadth). |
| 1539 | `LabourCheck.LAYOFF_TOWN_PEOPLE` | `1_000` | The people put into the builders' town of the payroll section (FS16), so the sector's households have workers to be paid. |
| 1744 | `LabourCheck.HELD_FILL` | `.5` | The share of its posts each graduate band holds in heads, and the unskilled posts in workers, in the held-figures town (AP5): every band has posts open for a move to fill. |
| 1747 | `LabourCheck.HELD_TOWN_PEOPLE` | `1_000` | The people put into the held-figures town (AP5), so it has a workforce for the month's labour market to be struck on. |
| 1750 | `LabourCheck.HELD_INSTANT` | `5` | The buildings put up at once, in the held-figures section (AP5). |
| 1753 | `LabourCheck.HELD_ON_SITE` | `3` | The buildings ordered on site, in the held-figures section (AP5). |
| 1756 | `LabourCheck.HELD_SCRAPPED` | `2` | The buildings scrapped, in the held-figures section (AP5). |
| 1759 | `LabourCheck.HELD_IMPRISONED` | `.1` | The share of the unskilled workers put in prison, in the held-figures section (AP5). |
| 1762 | `LabourCheck.HELD_HEADS_ADDED` | `.25` | The share of a band's posts written onto its heads through the array getSkilledHeads() hands out, in the held-figures section (AP5). |
| 1765 | `LabourCheck.HELD_LICENSED` | `.25` | The share of the university heads who take a doctor's licence, in the held-figures section (AP5). |
| 1768 | `LabourCheck.HELD_CREWS_OUTPUT` | `1e15` | The construction points put into the shops on site at once, in the held-figures section (AP5): more than any order owes, so every one finishes. |
| 1771 | `LabourCheck.HELD_RECIPE_HANDS` | `4` | The unskilled posts a shop's second recipe adds, in the held-figures section (AP5): a fourth of them is one post, the blend's step. |
| 1774 | `LabourCheck.HELD_BLEND_ON` | `1` | The shops on the second recipe in the held-figures section's blend (AP5): one in four, so the blend's posts are one more than the first recipe's. |
| 1777 | `LabourCheck.HELD_BLEND_OFF` | `3` | ...and on the first. |
| 1780 | `LabourCheck.HELD_ORDER_BIG` | `0x1p53` | The house's weight in the held-figures section's ordered sum (AP5): 2^53, so the house's buildings are a number whose last place is wider than one. |
| 1783 | `LabourCheck.HELD_ORDER_PART` | `.375` | Each apartment stack's part of the house's last place in that sum (AP5): under a half, so either alone is lost after the house, both together over it. |
| 1786 | `LabourCheck.HELD_WORKERS_ADDED` | `.25` | The share of the unskilled workers added to the workforce, in the held-figures section (AP5). |
| 1789 | `LabourCheck.HELD_OWNERS` | `.1` | The share of the workforce whose families own their businesses, in the held-figures section (AP5). |
| 1792 | `LabourCheck.HELD_STUDYING` | `.25` | The share of the widest graduate band's heads at their studies, in the held-figures section (AP5). |
| 1795 | `LabourCheck.HELD_JOINED_POSTS` | `100` | The graduate posts in the held-figures section's shared market (AP5), where the wage ladder splits the workers between two bands. |
| 1798 | `LabourCheck.HELD_JOINED_HEADS_PER_POST` | `3` | ...the graduate heads to each of them, so the band has workers to spare for the unskilled posts... |
| 1801 | `LabourCheck.HELD_JOINED_UNSKILLED_POSTS` | `4_000` | ...the unskilled posts, far more than the workers for them, so the bottom band pays more than the graduate one and the two are one market... |
| 1804 | `LabourCheck.HELD_JOINED_UNSKILLED` | `10` | ...and the unskilled workers. |
| 2478 | `LabourCheck.RENT_TOWN_PEOPLE` | `4_000` | People put into the town "rent is a market" is read on, so its labourers outnumber its posts and the floor, not scarcity, is what holds their wage (FS16). |
| 2500 | `LabourCheck.ARRIVALS_OVER_DEPARTURES` | `10` | How many times the month's skilled arrivals must outnumber its departures for it to be one the adult share of the ARRIVALS can be read off (FS16). |
| 2503 | `LabourCheck.PRICED_MONTHS` | `60` | Months a hand-fed market is priced against its posts: ADJUST_RATE a month closes all but under a thousandth of the gap to its target in this many. |
| 2515 | `LabourCheck.ROOM_TOWN` | `4000` | The town a hand-drawn month of arrivals is made into: this many people, with room, jobs and homes for more (ArrivalMixCheck.draw()'s size). |
| 2533 | `LabourCheck.TIE_AVERAGE_OVER_GOING_RATE` | `1.3` | The average wage the tie's section feeds a market by hand, over its going rate: with its shares .5 and .6 the floor lands inside the dial's bounds. |
| 2535 | `LabourCheck.TIE_NEXT_AVERAGE_OVER_LAST` | `1.1` | The next month's average in the tie's section, over the last one fed: a step off it, so a floor struck on the old average is told from one on the new. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 1298 | `final java.util.List<Double> index` |  |
| 1299 | `double worstTarget, worstStep` |  |
| 1300 | `int outsideWindow, windowMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 2493 | **type** `public class LabourCheck` | Verifies the labour market: who can hold a job, and what it costs. |
| 66 | 4 | `static void quietly(Runnable work)` |  |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 6 | `static void close(String label, double a, double b, double tol)` |  |
| 83 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 90 | 1165 | `public static void main(String[] args) throws Exception` |  |

### WAGES AGAINST THE INDEX (2026-09-21). (lines 1256-1511)

| line | len | member | says |
|---:|---:|---|---|
| 1297 | 40 | **type** `static final class LagWatch` | The recurrence, checked month by month on one city: what it was handed and what it did. |
| 1303 | 33 | `void month(Game g, Runnable play)` _(in LabourCheck.LagWatch)_ | One month, played; the index and the expectation the month was handed are the ones it read at its top. |
| 1339 | 4 | `static void crawl(Game g)` | The currency one CRAWL weaker, and held there for the month. |
| 1344 | 167 | `static void wagesAgainstTheIndex()` |  |

### 13. PAYROLL BY JOB TYPE (0.7.17) (lines 1512-1718)

| line | len | member | says |
|---:|---:|---|---|
| 1541 | 177 | `static void payrollByJobType()` |  |

### 14. THE LABOUR FIGURES HELD FOR A PASS ARE THE BARE ONES (AP5, 2026-11-26) (lines 1719-2550)

| line | len | member | says |
|---:|---:|---|---|
| 1807 | 5 | `static double allPosts(BuildingsTemplate b)` | Every post of a template, over the job types: the getter a sector's buildings are read through in the held-figures section (AP5). |
| 1814 | 3 | `static boolean sameBits(double a, double b)` | A held figure is the bare one to the bit, never within a tolerance (AP5). |
| 1819 | 5 | `static boolean sameBits(double[] a, double[] b)` | ...each of two arrays. |
| 1826 | 15 | `static Game heldTown()` | The town the held-figures section is read on (AP5): flats, shops, a hospital and the builders' depots, stood up free and a month old, so the depots lay crews off. |
| 1846 | 10 | `static long[] heldAfter(String move, WorksValue.Scope s, PopulationManager pm, long[] stood)` | The vacancies held for a scope after the people were moved (AP5): the figure is the bare fill, it was read afresh once, a second read with nothing moved is not another fill, and the move did change the fill (else a fi... |
| 1861 | 13 | `static void postsMovedBy(String change, BuildingManager bm, WorksValue.Scope s, BuildingsTemplate of, long by, Runnable move)` | One change to a stack's buildings inside the scope (AP5): the city's posts at the next reading have moved by this many of the template's posts of each job type, are the bare reading, and were built afresh once. |
| 1879 | 17 | `static long[] postsReading(BuildingManager bm, java.util.Collection<String> sectors, java.util.function.ToDoubleFunction<Buildi...` | Every posts figure a pass reads, as raw bits (AP5): each job type's posts, at every post and withheld, each sector's posts withheld, and each sector's buildings by their posts, their capacity and a weight. |
| 1901 | 13 | `static void heldThen(String change, BuildingManager bm, WorksValue.Scope s, java.util.Collection<String> sectors, java.util.fun...` | One change to the stacks on posts held (AP5): the posts read and held first, a second read building nothing; then the change, after which every reading is the bare one, built afresh once; and the change moved a readin... |
| 1916 | 13 | `static long[] sectorReadings(BuildingManager bm, java.util.Collection<String> sectors, java.util.function.ToDoubleFunction<Buil...` | The month's grouped readings, including an order-sensitive sum, as raw bits (AP6). |
| 1931 | 14 | `static void sectorMoved(String change, BuildingManager bm, java.util.Collection<String> sectors, java.util.function.ToDoubleFun...` | Warm first, change through the real writer, then compare the next held reading with the original walk (AP6). |
| 1946 | 156 | `static void sectorWalksAndPostsPast()` |  |
| 2103 | 325 | `static void labourHeldForAPass()` |  |
| 2430 | 12 | `static double everyOtherEmployer(Game g)` | Every payroll that is not a sector's: the bank, the utilities, and the four services the city staffs. |
| 2443 | 5 | `static double sum(double[] a)` |  |
| 2449 | 3 | `static double pct(double[] mix, WageBand band, double total)` |  |
| 2453 | 8 | `static double staffed(long[] jobs, long[] vacancy, WageBand band)` |  |
| 2467 | 9 | `static PopulationManager handFed(long workforce, double[] skilledHeads, long[] posts, double[] licensed)` | A population manager fed by hand (FS16): this workforce, these skilled heads by band (the unskilled are what is left), these posts by job type and these licence holders by job type. |
| 2481 | 17 | `static Game rentTownBuilt()` | The town "rent is a market" is read on (FS16): flats, a hospital, shops, power, water and roads and RENT_TOWN_PEOPLE in them, one month old, so the landlords have struck a rent and priced a home. |
| 2506 | 7 | `static LabourMarket pricedOn(PopulationManager pm)` | A labour market priced on a hand-fed population manager the way Game.repriceLabour() prices a city's: staffable posts, supply, posts by job and licence holders. |
| 2521 | 10 | `static Migration arrivalsOnce(LabourMarket market, PopulationManager pm)` | One month's draw by a fresh Migration into a town of ROOM_TOWN people with room, jobs and homes for more, priced by this market and placed against these people (so each band's chance of work is the people's own, not o... |
| 2538 | 4 | `static double lastAverageOf(LabourMarket m)` | The average wage a market's tie last read: the last value of its state array (WS1; there is no getter for it). |
| 2543 | 7 | `static void cleanUp(Path root)` |  |


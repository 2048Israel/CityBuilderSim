# LabourCheck.java - 1,502 lines · 14 methods · 4 constants · harnesses

`ham/citybuildersim/LabourCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
> an older array reading prices at the default share.

**Uses:** [LabourMarket](LabourMarket.md) (116), [JobType](JobType.md) (62), [WageBand](WageBand.md) (59), [Game](Game.md) (24), [GameFiles](GameFiles.md) (10), [PriceLevel](PriceLevel.md) (8), [Migration](Migration.md) (6), [Sector](Sector.md) (6), [BuildingType](BuildingType.md) (6), [PayTier](PayTier.md) (4), [WageLadder](WageLadder.md) (4), [PopulationManager](PopulationManager.md) (3), [BuildingManager](BuildingManager.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [DecisionLog](DecisionLog.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [RealEstate](RealEstate.md) (2), [Construction](Construction.md) (2), [TempDirs](TempDirs.md) (1), [AgeBand](AgeBand.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Sectors](Sectors.md) (1), [PriceIndex](PriceIndex.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (1):** [LandlordPortCheck](LandlordPortCheck.md)

## Sections

| line | section |
|---:|---|
| 75 | · 1. THE GOING RATE ANCHORS THE LADDER, AND THE FLOOR STARTS UNDER IT |
| 135 | · 2. THE DIAL MOVES ONLY THE WAGES UNDER IT |
| 213 | · 2b. THE FLOOR'S TIE |
| 347 | · 3. SCARCITY RAISES IT, SURPLUS LOWERS IT |
| 421 | · 4. IT IS LAGGED, WHICH IS THE POINT |
| 465 | · 5. NOBODY IS A DOCTOR WHO IS NOT A DOCTOR |
| 542 | · 6. AND IT SURVIVES A SAVE |
| 631 | · · RENT NO LONGER FOLLOWS THE UNSKILLED WAGE (2026-09-07). |
| 712 | · · ARRIVING CHILDREN ARE NOT GRADUATES (2026-09-06). |
| 739 | · · HUNTED FOR, NOT ASSUMED. |
| 814 | · · A DOCTOR SHORTAGE IS PRICED ON DOCTORS (2026-09-06). |
| 881 | · · WHO MOVES IN, AND WHY (2026-09-07). |
| 924 | · · SOME ARRIVE WITHOUT A DIPLOMA, FOR THE WAGE (0.7.18). |
| 1027 | · WAGES AGAINST THE INDEX (2026-09-21). |
| 1276 | 13. PAYROLL BY JOB TYPE (0.7.17) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `LabourCheck.OUT` | `System.out` |  |
| 45 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 1059 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 1062 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |
| 1066 | `final java.util.List<Double> index` |  |
| 1067 | `double worstTarget, worstStep` |  |
| 1068 | `int outsideWindow, windowMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 1462 | **type** `public class LabourCheck` | Verifies the labour market: who can hold a job, and what it costs. |
| 49 | 4 | `static void quietly(Runnable work)` |  |
| 54 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 6 | `static void close(String label, double a, double b, double tol)` |  |
| 66 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 73 | 953 | `public static void main(String[] args) throws Exception` |  |

### WAGES AGAINST THE INDEX (2026-09-21). (lines 1027-1275)

| line | len | member | says |
|---:|---:|---|---|
| 1065 | 40 | **type** `static final class LagWatch` | The recurrence, checked month by month on one city: what it was handed and what it did. |
| 1071 | 33 | `void month(Game g, Runnable play)` _(in LabourCheck.LagWatch)_ | One month, played; the index and the expectation the month was handed are the ones it read at its top. |
| 1107 | 4 | `static void crawl(Game g)` | The currency one CRAWL weaker, and held there for the month. |
| 1112 | 163 | `static void wagesAgainstTheIndex()` |  |

### 13. PAYROLL BY JOB TYPE (0.7.17) (lines 1276-1502)

| line | len | member | says |
|---:|---:|---|---|
| 1296 | 165 | `static void payrollByJobType()` |  |
| 1463 | 12 | `static double everyOtherEmployer(Game g)` | Every payroll that is not a sector's: the bank, the utilities, and the four services the city staffs. |
| 1476 | 5 | `static double sum(double[] a)` |  |
| 1482 | 3 | `static double pct(double[] mix, WageBand band, double total)` |  |
| 1486 | 8 | `static double staffed(long[] jobs, long[] vacancy, WageBand band)` |  |
| 1495 | 7 | `static void cleanUp(Path root)` |  |


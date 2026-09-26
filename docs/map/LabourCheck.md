# LabourCheck.java - 857 lines · 11 methods · 4 constants · harnesses

`ham/citybuildersim/LabourCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [JobType](JobType.md) (39), [WageBand](WageBand.md) (32), [LabourMarket](LabourMarket.md) (27), [Game](Game.md) (17), [LongPlaytest](LongPlaytest.md) (8), [GameFiles](GameFiles.md) (7), [PayTier](PayTier.md) (6), [Migration](Migration.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [RealEstate](RealEstate.md) (2), [PopulationManager](PopulationManager.md) (1), [AgeBand](AgeBand.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [PriceIndex](PriceIndex.md) (1)

## Sections

| line | section |
|---:|---|
| 64 | · 1. THE LADDER IS NEUTRAL AT ITS DEFAULT |
| 83 | · 2. THE DIAL MOVES EVERYTHING |
| 104 | · 3. SCARCITY RAISES IT, SURPLUS LOWERS IT |
| 147 | · 4. IT IS LAGGED, WHICH IS THE POINT |
| 191 | · 5. NOBODY IS A DOCTOR WHO IS NOT A DOCTOR |
| 267 | · 6. AND IT SURVIVES A SAVE |
| 329 | · · RENT NO LONGER FOLLOWS THE UNSKILLED WAGE (2026-09-07). |
| 401 | · · ARRIVING CHILDREN ARE NOT GRADUATES (2026-09-06). |
| 428 | · · HUNTED FOR, NOT ASSUMED. |
| 503 | · · A DOCTOR SHORTAGE IS PRICED ON DOCTORS (2026-09-06). |
| 570 | · · WHO MOVES IN, AND WHY (2026-09-07). |
| 662 | · WAGES AGAINST THE INDEX (2026-09-21). |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `LabourCheck.OUT` | `System.out` |  |
| 34 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 691 | `LabourCheck.CRAWL` | `.005` | How fast the fixture's currency is walked weaker: half a percent a month. |
| 694 | `LabourCheck.INFLATION_MONTHS` | `240` | Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `static int fails` |  |
| 698 | `final java.util.List<Double> index` |  |
| 699 | `double worstTarget, worstStep` |  |
| 700 | `int outsideWindow, windowMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 828 | **type** `public class LabourCheck` | Verifies the labour market: who can hold a job, and what it costs. |
| 38 | 4 | `static void quietly(Runnable work)` |  |
| 43 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 48 | 6 | `static void close(String label, double a, double b, double tol)` |  |
| 55 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 62 | 599 | `public static void main(String[] args) throws Exception` |  |

### WAGES AGAINST THE INDEX (2026-09-21). (lines 662-857)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 30 | **type** `static final class LagWatch` | The recurrence, checked month by month on one city: what it was handed and what it did. |
| 703 | 23 | `void month(Game g, Runnable play)` _(in LabourCheck.LagWatch)_ | One month, played; the index the month was handed is the one it read at its top. |
| 729 | 4 | `static void crawl(Game g)` | The currency one CRAWL weaker, and held there for the month. |
| 734 | 102 | `static void wagesAgainstTheIndex()` |  |
| 837 | 3 | `static double pct(double[] mix, WageBand band, double total)` |  |
| 841 | 8 | `static double staffed(int[] jobs, int[] vacancy, WageBand band)` |  |
| 850 | 7 | `static void cleanUp(Path root)` |  |


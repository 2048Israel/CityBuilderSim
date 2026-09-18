# LabourCheck.java - 680 lines · 8 methods · 2 constants · harnesses

`ham/citybuildersim/LabourCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [JobType](JobType.md) (39), [WageBand](WageBand.md) (32), [LabourMarket](LabourMarket.md) (20), [Game](Game.md) (11), [PayTier](PayTier.md) (6), [GameFiles](GameFiles.md) (5), [Migration](Migration.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [RealEstate](RealEstate.md) (2), [PopulationManager](PopulationManager.md) (1), [AgeBand](AgeBand.md) (1)

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

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `LabourCheck.OUT` | `System.out` |  |
| 34 | `LabourCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 651 | **type** `public class LabourCheck` | Verifies the labour market: who can hold a job, and what it costs. |
| 38 | 4 | `static void quietly(Runnable work)` |  |
| 43 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 48 | 6 | `static void close(String label, double a, double b, double tol)` |  |
| 55 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 62 | 597 | `public static void main(String[] args) throws Exception` |  |
| 660 | 3 | `static double pct(double[] mix, WageBand band, double total)` |  |
| 664 | 8 | `static double staffed(int[] jobs, int[] vacancy, WageBand band)` |  |
| 673 | 7 | `static void cleanUp(Path root)` |  |


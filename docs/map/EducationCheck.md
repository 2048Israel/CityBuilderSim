# EducationCheck.java - 685 lines · 10 methods · 2 constants · harnesses

`ham/citybuildersim/EducationCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Verifies the schools: who gets taught, who is allowed to practise, and what
> it costs. Not part of the game.
> 
> WHY THIS EXISTS
> 
> Education is the longest feedback loop in the game - a medical school built
> today is doctors in the 2040s - and a loop that long is one nobody can debug
> by playing. Every failure mode here looks like patience:
> 
> 1. A SCHOOL THAT TEACHES NOBODY looks exactly like a school whose graduates
>    have not arrived yet. The first version of this feature had a medical
>    school that enrolled zero students for three hundred and sixty months
>    while costing $78M, because the return on the course was computed against
>    the doctor's own wage and therefore came out at exactly 1.00.
> 
> 2. A SCHOOL THAT TEACHES TOO FAST empties the band it draws from. The first
>    version took ninety per cent of every diploma-holder in the city the month
>    the college opened; the diploma band went from 1,390 people to 28 and
>    never recovered, because a STOCK was being consumed at the rate a FLOW
>    should be.
> 
> 3. A LICENCE THAT LEAKS staffs posts out of nothing, which is the
>    220-doctors bug in a new hat.

**Uses:** [WageBand](WageBand.md) (26), [Game](Game.md) (22), [EducationType](EducationType.md) (15), [JobType](JobType.md) (11), [Migration](Migration.md) (7), [GameFiles](GameFiles.md) (6), [Education](Education.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [PopulationManager](PopulationManager.md) (2), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 113 | · 1. THE PROFESSION IS GATED |
| 166 | · 2. AND WITH ONE, IT CAN |
| 185 | · 3. NO SCHOOL TEACHES NOBODY |
| 204 | · 4. AND DOES NOT EMPTY THE BAND IT DRAWS FROM |
| 224 | · 5. A DEGREE IS A MOVE, NOT AN APPEARANCE |
| 252 | · 6. LICENCES CANNOT OUTNUMBER GRADUATES |
| 273 | · 7. THE PIPELINE IS ITS NARROWEST STAGE |
| 310 | · 8. THE SUBSIDY IS A REAL DIAL |
| 345 | · 9. IT SURVIVES A SAVE |
| 412 | · 9b. THE WAIT IS REAL |
| 454 | · 10. AND THE TREASURY PAYS FOR IT |
| 507 | · 11. THE UNSKILLED BAND IS A REPORT CARD |
| 582 | · 12. THE QUEUE FOR A JOB INCLUDES THE OVERQUALIFIED |
| 611 | · · THIS PREMISE USED TO READ `open[dip] > ownHeads[dip]` - "on its own |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `EducationCheck.OUT` | `System.out` |  |
| 37 | `EducationCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 33 | 653 | **type** `public class EducationCheck` | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. |
| 41 | 4 | `static void quietly(Runnable work)` |  |
| 46 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 51 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 58 | 1 | `static void build(Game g, String name, int n)` |  |
| 61 | 41 | `static Game city(GameFiles files)` | A funded city with room, so the thing under test is never money or land. |
| 103 | 7 | `static void schools(Game g)` |  |
| 111 | 550 | `public static void main(String[] args) throws Exception` |  |
| 666 | 3 | `static double heads(Game g, WageBand band)` | A band's headcount, which - unlike its share - no amount of immigration into the OTHER bands can move. |
| 671 | 6 | `static double share(Game g, WageBand band)` | A band's share of the workforce. |
| 678 | 7 | `static void cleanUp(Path root)` |  |


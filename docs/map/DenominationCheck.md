# DenominationCheck.java - 575 lines · 16 methods · 0 constants · harnesses

`ham/citybuildersim/DenominationCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> A currency reform is a change of units, and this is how we know.
> 
> WHAT IS ACTUALLY BEING ASKED. Jerus wanted a button that divides the money by
> ten or a hundred so that three centuries of inflation do not end with bread
> priced at 300M and the arithmetic losing digits at the bottom. That is a
> currency reform, and the entire risk in one is that it is not ONLY a change
> of units: miss one balance and the button quietly creates or destroys money;
> miss one compile-time constant and a House is a hundred times dearer the next
> morning.
> 
> So the test is not a list of fields. It is TWO CITIES:
> 
>   A runs normally.
>   B is the same city, reformed, and then run for exactly as long.
> 
> If the reform is only a change of units then B is A with a different label on
> the axis: every real quantity identical to the person, every money quantity
> identical after dividing by the factor, and the two trajectories still on top
> of each other years later. Any field left unscaled changes a relative price,
> and a changed relative price changes what gets built and who moves in - so
> the populations part company and the harness says so.
> 
> That is a much stronger assertion than "the balances add up", and it is the
> only one that can be trusted, because nobody can enumerate this codebase's
> money by reading it.

**Uses:** [Game](Game.md) (20), [LongPlaytest](LongPlaytest.md) (15), [Sector](Sector.md) (8), [Denomination](Denomination.md) (6), [GameFiles](GameFiles.md) (3), [Equity](Equity.md) (2), [Good](Good.md) (2), [Sectors](Sectors.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [Household](Household.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 122 | · (untitled) |
| 148 | · (untitled) |
| 156 | · 1. THE UNIT |
| 179 | · 2. IT IS THE SAME CITY |
| 217 | · ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT |
| 343 | · 3. AND IT STAYS THE SAME CITY |
| 448 | · 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING |
| 468 | · 4. AND NO MONEY WAS MADE OR LOST |
| 483 | · 5. AND IT SURVIVES A SAVE |
| 516 | · helpers |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `static int fails` |  |
| 38 | `static PrintStream out` |  |
| 39 | `static PrintStream quiet` |  |
| 519 | `static int emptyHolding` | Cells under half a household holding something, counted over every scan. |
| 521 | `static int emptyHeldAfterReform` | ...and the same, as it stood right after the reform. |
| 522 | `static int looks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 541 | **type** `public class DenominationCheck` | A currency reform is a change of units, and this is how we know. |
| 41 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 53 | 23 | `static void sameCity(Game plain, Game lopped, double factor, double band)` | Everything real to the person, everything nominal to the factor. |
| 78 | 11 | `static void relative(String label, double actual, double expected, double band)` | Equal to within a RELATIVE band, so one helper works at any scale. |
| 91 | 4 | `static double gap(double actual, double expected)` | How far apart two figures are, as a percentage of the expected one. |
| 96 | 9 | `static void within(String label, double actual, double expected, double band)` |  |
| 106 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 116 | 5 | `static void quietly(Runnable work)` |  |

### (untitled) (lines 122-147)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 22 | `static Game city(String name)` | A city with a bit of everything in it, so the reform has work to do. |

### (untitled) (lines 148-515)

| line | len | member | says |
|---:|---:|---|---|
| 150 | 365 | `public static void main(String[] args) throws Exception` |  |

### helpers (lines 516-575)

| line | len | member | says |
|---:|---:|---|---|
| 525 | 8 | `static void stepBoth(Game a, Game b, int months)` | Both cities a month at a time, each scanned after every month. |
| 535 | 11 | `static void scanEmpty(Game g)` | One look at a city: whether any cell is under half a household, and any of them holding something. |
| 548 | 5 | `static boolean force(Game g, double factor)` | Reforms regardless of the price-level gate, which is tested separately. |
| 555 | 7 | `static boolean same(Game a, Game b)` | The real city: who lives there and how much of everything there is. |
| 563 | 3 | `static double shelf(Game g)` |  |
| 567 | 3 | `static double rent(Game g)` |  |
| 571 | 4 | `static double cost(Game g, String name)` |  |


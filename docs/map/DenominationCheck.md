# DenominationCheck.java - 659 lines · 17 methods · 0 constants · harnesses

`ham/citybuildersim/DenominationCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

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
> 
> The twins have no transit, so a second pair with a bus network is reformed
> by a thousand in section 6 (B6, 0.7.47): the month's transit lines, the
> riders at the same real fare, and the working rows' fixed bills.

**Uses:** [Game](Game.md) (23), [LongPlaytest](LongPlaytest.md) (17), [Sector](Sector.md) (8), [Denomination](Denomination.md) (6), [Household](Household.md) (4), [GameFiles](GameFiles.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Currency](Currency.md) (2), [Equity](Equity.md) (2), [Good](Good.md) (2), [Founding](Founding.md) (1), [Mining](Mining.md) (1), [HeavyIndustry](HeavyIndustry.md) (1), [Sectors](Sectors.md) (1), [TaxPolicy](TaxPolicy.md) (1), [WorkingHousehold](WorkingHousehold.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

## Sections

| line | section |
|---:|---|
| 129 | · (untitled) |
| 169 | · (untitled) |
| 177 | · 1. THE UNIT |
| 203 | · 2. IT IS THE SAME CITY |
| 241 | · ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT |
| 367 | · 3. AND IT STAYS THE SAME CITY |
| 472 | · 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING |
| 492 | · 4. AND NO MONEY WAS MADE OR LOST |
| 507 | · 5. AND IT SURVIVES A SAVE |
| 536 | · 6. AND THE TRAMS (B6, 0.7.47) |
| 600 | · helpers |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |
| 45 | `static PrintStream out` |  |
| 46 | `static PrintStream quiet` |  |
| 603 | `static int emptyHolding` | Cells under half a household holding something, counted over every scan. |
| 605 | `static int emptyHeldAfterReform` | ...and the same, as it stood right after the reform. |
| 606 | `static int looks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 618 | **type** `public class DenominationCheck` | A currency reform is a change of units, and this is how we know. |
| 48 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 60 | 23 | `static void sameCity(Game plain, Game lopped, double factor, double band)` | Everything real to the person, everything nominal to the factor. |
| 85 | 11 | `static void relative(String label, double actual, double expected, double band)` | Equal to within a RELATIVE band, so one helper works at any scale. |
| 98 | 4 | `static double gap(double actual, double expected)` | How far apart two figures are, as a percentage of the expected one. |
| 103 | 9 | `static void within(String label, double actual, double expected, double band)` |  |
| 113 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 123 | 5 | `static void quietly(Runnable work)` |  |

### (untitled) (lines 129-168)

| line | len | member | says |
|---:|---:|---|---|
| 132 | 1 | `static Game city(String name)` | A city with a bit of everything in it, so the reform has work to do. |
| 135 | 33 | `static Game city(String name, boolean buses)` | ...with a bus network on its roads too (B6, 0.7.47): a fare, riders and the month's transit lines for a reform to carry. |

### (untitled) (lines 169-599)

| line | len | member | says |
|---:|---:|---|---|
| 171 | 428 | `public static void main(String[] args) throws Exception` |  |

### helpers (lines 600-659)

| line | len | member | says |
|---:|---:|---|---|
| 609 | 8 | `static void stepBoth(Game a, Game b, int months)` | Both cities a month at a time, each scanned after every month. |
| 619 | 11 | `static void scanEmpty(Game g)` | One look at a city: whether any cell is under half a household, and any of them holding something. |
| 632 | 5 | `static boolean force(Game g, double factor)` | Reforms regardless of the price-level gate, which is tested separately. |
| 639 | 7 | `static boolean same(Game a, Game b)` | The real city: who lives there and how much of everything there is. |
| 647 | 3 | `static double shelf(Game g)` |  |
| 651 | 3 | `static double rent(Game g)` |  |
| 655 | 4 | `static double cost(Game g, String name)` |  |


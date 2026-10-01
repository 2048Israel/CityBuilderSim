# ConstructionControlCheck.java - 826 lines · 21 methods · 1 constants · harnesses

`ham/citybuildersim/ConstructionControlCheck.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The player's hand on the construction queue (0.7.22): each of
> ConstructionControl's five rules held to its own arithmetic, in a played
> city.
> 
> WHY. Jerus asked to reprioritise, cancel and demolish, and chose how
> (2026-09-30): priority "Both", cancel "Keep the half-built shell",
> demolish "City's, plus buy-outs". Every rule has a source and a figure,
> and every one moves money between the treasury, the builders' book, the
> owners' tills and the households; a rule that is right on the screen
> and wrong in the books is the bug this codebase keeps finding. None of it
> runs in the default playtest - none of it may, unless the player uses it -
> so this is the only place it is played.
> 
> What this has to prove:
>   1. THE OVERTIME TABLE: the Business Roundtable's 50-hour productivity,
>      averaged week by week over each month, written out here from the
>      report's figures; about 1.14, 1.02 and 0.94 times a month's work;
>      time and a half making the wage bill 1.375.
>   2. NO ORDER, THE RULE'S OWN SPLIT, TO THE BIT: a city whose player has
>      set no order is not engaged, its plan is the rule's shares bit for
>      bit, and a twin whose order was set and taken back plays the same
>      month to the bit.
>   3. PRIORITY: the city's share, as the rule gave it, handed out top
>      down in the player's order; every other site's share to the bit, and
>      the landlords' site in the twin without an order built the same.
>   4. RUSH: the month's work is the share x 50/40 x the month's factor,
>      months one, two and three, the third less than a normal month; the
>      premium is 0.375 of the crews' wage bill at the builders' own rate a
>      point, paid with their tax in it and paid out as wages the
>      households receive; the count resets after a month off; the inbox
>      says so before the third month.
>   5. CANCEL: the refund is the city's contract left after the month's
>      work - the work not done, the allowance not drawn and the tax on
>      both - out of the builders' book; the shell keeps its buildings,
>      progress, material owed and ground, and gets no crews; a restart is
>      today's quote for the remainder.
>   6. DEMOLITION: 5% of the template's points, at the builders' rate for
>      its work, taxed; the buildings close as the month starts, not
>      before; the material is sold to the builders by the 0.7.8 rule and
>      the ground returns when done.
>   7. BUY-OUT: market value - the building at its owner's value, the
>      ground at the land market's - plus the business loss; the owner's
>      cash rises by exactly that; its debts stay its own; nobody living
>      in a home bought is deleted.
>   8. MONEY IS CONSERVED through every one: the money audit every month,
>      and every between-the-presses hand moves money between pools only.
>   9. A SAVE AND A LOAD round-trip all of it, and a format-27 save loads
>      with none of it.
>  10. ONE WAIT, AND AN ORDER THAT LASTS AS LONG AS ITS SITES (after the
>      docs pass): with the player's hand off, every screen's wait and the
>      quote are the rule's to the bit; with an order set, the right panel's,
>      a card's and the Needs-you line's wait is the page's - the order's
>      own arithmetic - and a city order's quote is where it would land, a
>      new site at the bottom; a city site placed under an order joins it at
>      the bottom, and an order with none of its sites left is cleared; and
>      "A demolition is done" reads each site's own sale.
> 
> Every fixture causes its condition.

**Uses:** [ConstructionControl](ConstructionControl.md) (57), [Game](Game.md) (26), [BuildingManager](BuildingManager.md) (15), [BuildingsStacks](BuildingsStacks.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (9), [JobType](JobType.md) (6), [GameFiles](GameFiles.md) (4), [Sector](Sector.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Good](Good.md) (3), [GameVersion](GameVersion.md) (3), [Sectors](Sectors.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Construction](Construction.md) (2), [Notice](Notice.md) (2), [Founding](Founding.md) (1), [PopulationManager](PopulationManager.md) (1), [TreasuryJournal](TreasuryJournal.md) (1)

## Sections

| line | section |
|---:|---|
| 195 | 1. THE OVERTIME TABLE |
| 229 | 2. NO ORDER |
| 270 | 3. PRIORITY |
| 318 | 4. RUSH |
| 392 | 5. CANCEL |
| 468 | 6. DEMOLITION |
| 563 | 7. BUY-OUT |
| 617 | 8. CONSERVED |
| 626 | 9. SAVE AND LOAD |
| 701 | 10. ONE WAIT, AND THE ORDER'S LIFE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 111 | `ConstructionControlCheck.RE` | `Sectors.REAL_ESTATE` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |
| 121 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 122 | `static int monthsAudited` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 752 | **type** `public class ConstructionControlCheck` | The player's hand on the construction queue (0.7.22): each of ConstructionControl's five rules held to its own arithmetic, in a played city. |
| 81 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 86 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 98 | 5 | `static void quietly(Runnable r)` |  |
| 104 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 114 | 5 | `static double pooled(Game g)` | The money in the pools the audit reads, all of them. |
| 125 | 7 | `static void month(Game g)` | One month, audited. |
| 134 | 5 | `static double[] handMoves(Game g, Runnable hand)` | A between-the-presses hand, and the pools before and after it. |
| 146 | 27 | `static Game city(Path root, String name)` | A city with work on site: works standing, a population, depots, every sector held so no planner orders or scraps anything in the measurement, and on site a University, two Middle Schools and three Gravel Roads of the ... |
| 174 | 20 | `public static void main(String[] args) throws Exception` |  |

### 1. THE OVERTIME TABLE (lines 195-228)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 31 | `static void overtimeTable()` |  |

### 2. NO ORDER (lines 229-269)

| line | len | member | says |
|---:|---:|---|---|
| 231 | 38 | `static void noOrder(Path root)` |  |

### 3. PRIORITY (lines 270-317)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 45 | `static void priority(Path root)` |  |

### 4. RUSH (lines 318-391)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 71 | `static void rush(Path root)` |  |

### 5. CANCEL (lines 392-467)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 73 | `static void cancel(Path root)` |  |

### 6. DEMOLITION (lines 468-562)

| line | len | member | says |
|---:|---:|---|---|
| 470 | 92 | `static void demolition(Path root)` |  |

### 7. BUY-OUT (lines 563-616)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 51 | `static void buyOut(Path root)` |  |

### 8. CONSERVED (lines 617-625)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 6 | `static void conserved()` |  |

### 9. SAVE AND LOAD (lines 626-700)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 72 | `static void saveAndLoad(Path root) throws Exception` |  |

### 10. ONE WAIT, AND THE ORDER'S LIFE (lines 701-826)

| line | len | member | says |
|---:|---:|---|---|
| 703 | 119 | `static void oneWaitAndTheOrder(Path root)` |  |
| 823 | 3 | `static String fmt(double thousands)` |  |


# QuayCraneCheck.java - 408 lines · 17 methods · 1 constants · harnesses

`ham/citybuildersim/QuayCraneCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The quay crane (HB2, 2026-11-09; QuayCrane, BoatSchedule.atQuay()): a boat at its terminal's quay is worked by a light line from over
> the ship to the apron and a box that slides along it, as Jerus picked look 3a from the renders. Pure geometry, so every fixture is a
> work, a boat's point and a share.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE SHARE: BoatSchedule.atQuay() is 0 or more exactly when position() has the boat docked, over a month's calls and times about
>      each call's arrival and departure; it is 0 at the arrival, 1 at the end of BERTH_SECONDS, -1 before and after.
> 
>   2. WHERE THE LINE LIES, on each of the four sides: one crane at a small berth; its apron end is inside the box, in its sea-most row;
>      its ship end is on the water beyond the wall, past the boat; the line crosses the wall square to the shore; the hook is on it.
> 
>   3. THE MOVES: over a boat's time at the quay the first crane's hook leaves the ship end carrying and puts the load down at the apron
>      end MOVES times, and comes back bare each time; a berth of TWO_CRANES_FROM plots has a second crane that carries from the apron
>      end to the ship end; one plot shorter has one.
> 
>   4. THE CARGO: dry bulk is a grab of ore, containers and general cargo a box, a tanker two loading arms that do not move; a pier of
>      its own (a quay that is not HARBOUR_QUAY_PLOTS), a share outside 0..1 and a null cargo give no crane.
> 
>   5. THE TERMINAL: worksByBerth() finds each berthed work by its berth's point, and no point that is not one.
> 
>   6. THE COST: a frame's boats drawn() and the cranes of every docked one over a bay of 64 berths, within a frame's boat budget.
> 
> Timing harness (Timing.HARNESSES): section 6 judges a frame's cranes through Timing.
> 
> Every fixture CAUSES its condition: the works are laid on a shore, the routes and the month's calls come from the trade at sea.

**Uses:** [QuayCrane](QuayCrane.md) (72), [Ports](Ports.md) (31), [CityShore](CityShore.md) (29), [BoatSchedule](BoatSchedule.md) (25), [PortCheck](PortCheck.md) (15), [MapCheck](MapCheck.md) (7), [Timing](Timing.md) (2)

## Sections

| line | section |
|---:|---|
| 64 | · the fixtures |
| 109 | · 1 |
| 151 | · 2 |
| 203 | · 3 |
| 267 | · 4 |
| 321 | · 5 |
| 351 | · 6 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `QuayCraneCheck.STEPS` | `2000` | How many shares a boat's time at the quay is sampled at in the sections that follow the hook: the hook moves 0.001 of a move a step. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `static int fails` |  |
| 40 | `static int checks` |  |
| 41 | `static PrintStream out` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 372 | **type** `public class QuayCraneCheck` | The quay crane (HB2, 2026-11-09; QuayCrane, BoatSchedule.atQuay()): a boat at its terminal's quay is worked by a light line from over the ship to the apron and a box that slides along it, as Jerus picked look 3a from ... |
| 46 | 5 | `static void check(String label, boolean ok)` |  |
| 53 | 5 | `static void timed(String label, boolean ok, String detail)` | A timing assertion's line, its verdict Timing's: soft under -Dcbs.timing=soft, hard at the gate. |
| 59 | 4 | `static void section(String what)` |  |

### the fixtures (lines 64-108)

| line | len | member | says |
|---:|---:|---|---|
| 67 | 4 | `static CityShore.Work box(int side, int along)` | A work whose sea is on `side`, `along` plots long and the Bulk Berth's PortCheck.BULK_DEEP deep, with the harbour's quay, at (3000, 3000). |
| 73 | 8 | `static double seaward(CityShore.Work w, double x, double y)` | How far a world point is from the work's land edge, out toward its sea, in plots: worked out here side by side, not as the model does. |
| 83 | 3 | `static double alongShore(CityShore.Work w, double x, double y)` | A world point's place along the shore: its x for a sea on the north or south, its y for one on the east or west. |
| 88 | 3 | `static double depth(CityShore.Work w)` | How deep the work's box is, land edge to sea edge, in plots. |
| 93 | 10 | `static boolean inSeaRow(CityShore.Work w, double x, double y)` | Whether a world point lies in the plot row of the box nearest the sea. |
| 105 | 3 | `static double share(int k, int steps)` | The shares 0, 1/steps ... |

### 1 (lines 109-150)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 38 | `static void theShare()` | Section 1: atQuay() is position()'s docked(), as a share. |

### 2 (lines 151-202)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 48 | `static void whereTheLineLies()` | Section 2: the line, for a small berth on each side. |

### 3 (lines 203-266)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 1 | **type** `record Moves(int pickups, int drops, boolean atEnds, boolean steady)` | What one crane does through a boat's time at the quay, sampled STEPS times. |
| 209 | 28 | `static Moves moves(CityShore.Work w, Ports.Cargo cargo, int crane, double from, double to)` | The loads a crane picks up and puts down in a time at the quay, and whether it does so at the ends `from` and `to` (plots from the land) and goes between them steadily. |
| 239 | 27 | `static void theMoves()` | Section 3: the moves of one and of two cranes. |

### 4 (lines 267-320)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 50 | `static void theCargo()` | Section 4: what hangs on the hook, and the works that have no crane. |

### 5 (lines 321-350)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 26 | `static void theTerminal()` | Section 5: a berth's point finds its work. |

### 6 (lines 351-408)

| line | len | member | says |
|---:|---:|---|---|
| 354 | 36 | `static void theCost()` | Section 6: a frame's boats and cranes over a bay of 64 berths, within a frame's boat budget. |
| 391 | 17 | `public static void main(String[] args) throws Exception` |  |


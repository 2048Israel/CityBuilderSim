# DeathCareFirstCheck.java - 535 lines · 18 methods · 5 constants · harnesses

`ham/citybuildersim/DeathCareFirstCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> Death care first while the dead wait (0.7.129, batch T1-B): the rule that
> serves the city's own cemeteries and crematoria first out of its share of
> the crews while anybody lies unburied, held on a city that is caught in
> the trap it breaks.
> 
> WHY. Batch T1 traced Jerus's construction-bound city (the project's
> why-the-builders-stall.md): its cemeteries full and no crematorium, the
> dead at Healthcare's cap made Health's capped share of the city sick, the
> sick builders' month went almost all on repairs, and the cemetery that
> would have buried the dead got its crew-rule share of what was left - a
> thousand months. Nothing in the model broke the circle. Jerus chose the
> rule (2026-10-10). The city in the trace is an old save the game now
> refuses, so this fixture is the evidence: it causes the trap - no plot,
> no crematorium, the dead at the cap, repairs taking most of the builders'
> month - and plays it with the rule and without it.
> 
> What this has to prove:
>   1. THE RULE'S ARITHMETIC: of the city's share as the crews' rule gave
>      it, the death care takes what it owes, top down, or what is left if
>      less; the city's other sites share the rest by the crews' rule
>      (BuildingManager.waterFill()); every other owner's slot to the bit,
>      and the city's total the rule's; with the player's order, the death
>      care heads it and the rest follow in the player's order.
>   2. THE TRAP, CAUSED: no plot and no crematorium, the unburied at
>      Healthcare.MAX_BACKLOG_MONTHS of deaths, Health's unburied term at
>      MAX_UNBURIED_SICKNESS, and repairs taking all but a year's share of
>      a cemetery's points of the builders' sick month; a Memorial Cemetery
>      on site among the city's other sites, and the landlords' Houses.
>   3. BEFORE AND WITH THE RULE, the same month in twin cities: without it
>      the cemetery's share is its crew-rule share, to the bit; with it,
>      what it owes or the city's pool; the landlords' site the same to the
>      bit in both; the order, the plan's line and the one wait say so.
>   4. THE LOOP BREAKS: with the rule the cemetery opens within the months
>      its points take at the trap's city share (and one more), the
>      unburied fall to none and the sick rate to its served level - every
>      part of it but the dead's - and the sites get more; the twin without
>      it is in the trap still, the cemetery a long way off.
>   5. THE PLAYER'S ORDER: set with the death care at its bottom, the death
>      care still heads it while the dead wait, and the rest keep the
>      player's order.
>   6. ONCE NOBODY LIES UNBURIED: a death-care site on site is served as
>      before - no death care first, the order the player's (or the
>      stacks'), the plan the rule's to the bit, and no plan applied.
>   7. MONEY IS CONSERVED: the money audit every month played here.
> 
> Every fixture causes its condition. How often the rule fires in a real
> run is LongPlaytest's line "the dead waited". Since 0.7.130 a depot of
> the builders' served first takes the death care beside it, out of the
> whole month (BuildingManager, THE BUILDERS' OWN DEPOTS FIRST); that is
> BuildersFirstCheck's 7, and no depot goes first here.

**Uses:** [BuildingManager](BuildingManager.md) (21), [Game](Game.md) (19), [ConstructionControl](ConstructionControl.md) (10), [Healthcare](Healthcare.md) (6), [Health](Health.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [Founding](Founding.md) (3), [GameFiles](GameFiles.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [Sectors](Sectors.md) (1), [MoneyAudit](MoneyAudit.md) (1), [TempDirs](TempDirs.md) (1), [Sector](Sector.md) (1)

**Used by (2):** [BuildersFirstCheck](BuildersFirstCheck.md), [ConstructionTrapCheck](ConstructionTrapCheck.md)

## Sections

| line | section |
|---:|---|
| 148 | 1. THE RULE'S ARITHMETIC |
| 196 | 2. THE TRAP, CAUSED |
| 324 | 3. BEFORE AND WITH |
| 385 | 4. THE LOOP BREAKS |
| 453 | 5. THE PLAYER'S ORDER |
| 493 | 6. ONCE NOBODY LIES UNBURIED |
| 525 | 7. CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `DeathCareFirstCheck.RE` | `Sectors.REAL_ESTATE` | Real Estate's key: the landlords, whose Houses the fixture's town is given. |
| 101 | `DeathCareFirstCheck.CEMETERY` | `"Memorial Cemetery"` | The death care the fixture puts on site: the smaller cemetery, which serves the dead by its care type. |
| 104 | `DeathCareFirstCheck.ROAD` | `"Paved Road"` | The road whose repairs fill the builders' month: it has its own repairs share (0.7.103), and carries traffic, nothing else. |
| 112 | `DeathCareFirstCheck.TRAP_MONTHS` | `12` | The months the cemetery's points take at the trap's city share, in the fixture: the trace's measure A opened Jerus's in about twelve (why-the-builders-stall.md), so the repairs leave the builders' sick month a year's ... |
| 115 | `DeathCareFirstCheck.CAP_MONTHS_MORE` | `36` | The most months the trap's city plays past its three years for the backlog to reach its cap (R0b). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 64 | `static int fails` |  |
| 65 | `static PrintStream out` |  |
| 66 | `static PrintStream quiet` |  |
| 118 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 119 | `static int monthsAudited` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 474 | **type** `public class DeathCareFirstCheck` | Death care first while the dead wait (0.7.129, batch T1-B): the rule that serves the city's own cemeteries and crematoria first out of its share of the crews while anybody lies unburied, held on a city that is caught ... |
| 68 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 73 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 79 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 85 | 5 | `static void quietly(Runnable r)` |  |
| 91 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 122 | 7 | `static void month(Game g)` | One month, audited. |
| 130 | 17 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE'S ARITHMETIC (lines 148-195)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 3 | `static double crew(double points, int buildings)` | A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. |
| 155 | 40 | `static void arithmetic()` |  |

### 2. THE TRAP, CAUSED (lines 196-323)

| line | len | member | says |
|---:|---:|---|---|
| 210 | 31 | `static Game city(Path root, String name)` | A city caught in the trap: works, homes, a clinic for everybody, one depot, every sector held so no planner builds or scraps anything but by this fixture's hand - the founding churchyard full and no crematorium, so th... |
| 249 | 10 | `static void toTheCap(Game without, Game with)` | ...and on until the backlog is at its cap (R0b), both twins month for month: on 1900's causes of death the month's dead rise through the three years (2.7 a month at the end, a cap of 64, against a backlog of 56), so t... |
| 261 | 16 | `static void sites(Game g)` | ...and the roads and the sites: the half of the fixture that follows the trap. |
| 278 | 45 | `static Game[] trap(Path root)` |  |

### 3. BEFORE AND WITH (lines 324-384)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 58 | `static void beforeAndWith(Game without, Game with)` |  |

### 4. THE LOOP BREAKS (lines 385-452)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 65 | `static void loopBreaks(Game without, Game with)` |  |

### 5. THE PLAYER'S ORDER (lines 453-492)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 37 | `static void playersOrder(Path root)` |  |

### 6. ONCE NOBODY LIES UNBURIED (lines 493-524)

| line | len | member | says |
|---:|---:|---|---|
| 495 | 29 | `static void onceBuried(Game g)` |  |

### 7. CONSERVED (lines 525-535)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 8 | `static void conserved()` |  |


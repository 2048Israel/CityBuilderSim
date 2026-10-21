# DeathCareFirstCheck.java - 509 lines · 17 methods · 4 constants · harnesses

`ham/citybuildersim/DeathCareFirstCheck.java` - generated 2026-10-20 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildingManager](BuildingManager.md) (21), [Game](Game.md) (17), [ConstructionControl](ConstructionControl.md) (10), [Health](Health.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [Healthcare](Healthcare.md) (4), [Founding](Founding.md) (3), [GameFiles](GameFiles.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [Sectors](Sectors.md) (1), [MoneyAudit](MoneyAudit.md) (1), [Sector](Sector.md) (1)

**Used by (1):** [BuildersFirstCheck](BuildersFirstCheck.md)

## Sections

| line | section |
|---:|---|
| 146 | 1. THE RULE'S ARITHMETIC |
| 194 | 2. THE TRAP, CAUSED |
| 298 | 3. BEFORE AND WITH |
| 359 | 4. THE LOOP BREAKS |
| 427 | 5. THE PLAYER'S ORDER |
| 467 | 6. ONCE NOBODY LIES UNBURIED |
| 499 | 7. CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `DeathCareFirstCheck.RE` | `Sectors.REAL_ESTATE` | Real Estate's key: the landlords, whose Houses the fixture's town is given. |
| 102 | `DeathCareFirstCheck.CEMETERY` | `"Memorial Cemetery"` | The death care the fixture puts on site: the smaller cemetery, which serves the dead by its care type. |
| 105 | `DeathCareFirstCheck.ROAD` | `"Paved Road"` | The road whose repairs fill the builders' month: it has its own repairs share (0.7.103), and carries traffic, nothing else. |
| 113 | `DeathCareFirstCheck.TRAP_MONTHS` | `12` | The months the cemetery's points take at the trap's city share, in the fixture: the trace's measure A opened Jerus's in about twelve (why-the-builders-stall.md), so the repairs leave the builders' sick month a year's ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 65 | `static int fails` |  |
| 66 | `static PrintStream out` |  |
| 67 | `static PrintStream quiet` |  |
| 116 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 117 | `static int monthsAudited` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 447 | **type** `public class DeathCareFirstCheck` | Death care first while the dead wait (0.7.129, batch T1-B): the rule that serves the city's own cemeteries and crematoria first out of its share of the crews while anybody lies unburied, held on a city that is caught ... |
| 69 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 74 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 80 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 86 | 5 | `static void quietly(Runnable r)` |  |
| 92 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 120 | 7 | `static void month(Game g)` | One month, audited. |
| 128 | 17 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE'S ARITHMETIC (lines 146-193)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 3 | `static double crew(double points, int buildings)` | A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. |
| 153 | 40 | `static void arithmetic()` |  |

### 2. THE TRAP, CAUSED (lines 194-297)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 26 | `static Game city(Path root, String name)` | A city caught in the trap: works, homes, a clinic for everybody, one depot, every sector held so no planner builds or scraps anything but by this fixture's hand - the founding churchyard full and no crematorium, so th... |
| 236 | 16 | `static void sites(Game g)` | ...and the roads and the sites: the half of the fixture that follows the trap. |
| 253 | 44 | `static Game[] trap(Path root)` |  |

### 3. BEFORE AND WITH (lines 298-358)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 58 | `static void beforeAndWith(Game without, Game with)` |  |

### 4. THE LOOP BREAKS (lines 359-426)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 65 | `static void loopBreaks(Game without, Game with)` |  |

### 5. THE PLAYER'S ORDER (lines 427-466)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 37 | `static void playersOrder(Path root)` |  |

### 6. ONCE NOBODY LIES UNBURIED (lines 467-498)

| line | len | member | says |
|---:|---:|---|---|
| 469 | 29 | `static void onceBuried(Game g)` |  |

### 7. CONSERVED (lines 499-509)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 8 | `static void conserved()` |  |


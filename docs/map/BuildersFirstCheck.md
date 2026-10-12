# BuildersFirstCheck.java - 578 lines · 17 methods · 4 constants · harnesses

`ham/citybuildersim/BuildersFirstCheck.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

> The builders' own depots first (0.7.130, batch T1-C): the rule that serves
> a depot on site the crews' rule would starve - keep there past the months
> the builders expand at - off the top of their month, before the rule shares
> the rest, while the city could staff it; and, while the dead wait, the
> city's death care beside it at the same priority - held on a city whose
> builders are stalled the way the trace found Jerus's.
> 
> WHY. Batch T1 traced Jerus's construction-bound city (the project's
> why-the-builders-stall.md, section 3): repairs took 0.81 of the builders'
> nameplate, about 420 points a month reached the sites, and the builders'
> one depot order (BusinessInvestment.MAX_CONCURRENT_ORDERS) waited in the
> queue at its crew's weight - 0.4 points a month - so their plan said
> "already building" in 119 of 120 months and they could not grow out of it.
> Jerus chose option C, the depots first, as its own batch (2026-10-10), and
> put the death care beside them at one priority (2026-10-11). The city in
> the trace is an old save the game now refuses, so this fixture is the
> evidence: it causes the stall - repairs taking most of the builders' sick
> month, a queue of years, the builders' own depot ordered by their own plan
> and on site at its crew's weight - and plays it with the rule and without
> it (BuildingManager.setBuildersFirstForTest(), the twin's switch).
> DeathCareFirstCheck's trap is the city (its city() and sites()).
> 
> What this has to prove:
>   1. THE RULE'S ARITHMETIC: the first group takes what it owes, or the
>      whole month if less, shared among its sites by the crews' rule
>      (BuildingManager.waterFill()); every other site shares what is left by
>      the same rule; the month is conserved; with a depot and a cemetery in
>      the group neither takes the other's turn.
>   2. THE STALL, CAUSED: repairs taking most of the builders' sick month,
>      the queue past BACKLOG_MONTHS_BEFORE_EXPANDING months of their site
>      output, every post offered, and the builders' one depot ordered by
>      their own plan and on site at its crew's weight.
>   3. BEFORE AND WITH THE RULE, the same month in twin cities: without it
>      the depot's share is its crew-rule share, to the bit; with it, what it
>      owes or the whole month; every other site the rule's share of what is
>      left; the line and the one wait say so.
>   4. THE BUILDERS GROW: over a fixed run the city with the rule opens its
>      first depot within the months its points take at the trap's month
>      (and one more), orders the next the month after, and from then its
>      one order never holds the plan past the months it expands at (and
>      one more) - a depot the rule would open in time waits by the rule -
>      and it ends with more nameplate; the twin without it holds its
>      nameplate and its plan says "already building" every month.
>   5. NEVER LAID OFF THE MONTH IT OPENS (the 0.7.17 failure: depots bought
>      for rushes, then laid off): every month a depot opens the builders
>      offer every post and their loss clock is under the retirement rule's,
>      and no depot is retired over the run.
>   6. ONLY A DEPOT THE CITY COULD STAFF: in a new town whose founding
>      put depots on site among a hundred people, the builders' own test
>      (Sector.staffing()) says the town could not staff them, and they are
>      crewed by the rule, to the bit; the twin that builds them first all
>      the same opens one in its second month, its builders' fill falls
>      under MIN_STAFFABLE_TO_ORDER, the month under the repairs, and no
>      home is built, where the town with the test builds its homes.
>   7. ONE PRIORITY WITH THE DEATH CARE: while the dead wait, a depot of the
>      builders' and the city's cemetery on site together share what the
>      first group takes by the crews' rule - neither ahead, neither starved;
>      the city's order keeps the cemetery at its top; with the player's
>      order set, the city's other sites take the city's share of what is
>      left in that order.
> ... (4 more lines in the source)

**Uses:** [BuildingManager](BuildingManager.md) (30), [Game](Game.md) (16), [ConstructionControl](ConstructionControl.md) (9), [DeathCareFirstCheck](DeathCareFirstCheck.md) (8), [BuildingsStacks](BuildingsStacks.md) (6), [BusinessInvestment](BusinessInvestment.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [MortgageCheck](MortgageCheck.md) (4), [Construction](Construction.md) (2), [Sector](Sector.md) (2), [Sectors](Sectors.md) (1), [MoneyAudit](MoneyAudit.md) (1), [Good](Good.md) (1)

## Sections

| line | section |
|---:|---|
| 156 | 1. THE RULE'S ARITHMETIC |
| 214 | 2. THE STALL, CAUSED |
| 277 | 3. BEFORE AND WITH |
| 339 | 4. THE BUILDERS GROW |
| 408 | 5. NEVER LAID OFF |
| 423 | 6. ONLY WHAT IT COULD STAFF |
| 484 | 7. ONE PRIORITY |
| 568 | 8. CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `BuildersFirstCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key: whose depots the rule serves first, and whose plan the fixture asks. |
| 116 | `BuildersFirstCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the one template that adds to their capacity. |
| 119 | `BuildersFirstCheck.RUN_MONTHS` | `24` | The months the fixture's run is played: two years, a span the trace's city held through ten times over. |
| 426 | `BuildersFirstCheck.NEW_TOWN_MONTHS` | `6` | The months the new town is played: long enough for its first depot to open, crewed first, and its homes to come or not. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |
| 80 | `static PrintStream out` |  |
| 81 | `static PrintStream quiet` |  |
| 122 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 123 | `static int monthsAudited` |  |
| 342 | `static int openings` | What the run saw in the city with the rule, the months a depot opened: read by section 5. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 502 | **type** `public class BuildersFirstCheck` | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the months the builders expand at - off the top of their month, before the rule... |
| 83 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 88 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 94 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 100 | 5 | `static void quietly(Runnable r)` |  |
| 106 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 126 | 7 | `static void month(Game g)` | One month, audited. |
| 134 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE'S ARITHMETIC (lines 156-213)

| line | len | member | says |
|---:|---:|---|---|
| 159 | 3 | `static double crew(double points, int buildings)` | A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. |
| 163 | 50 | `static void arithmetic()` |  |

### 2. THE STALL, CAUSED (lines 214-276)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 9 | `static Game stalled(Path root, String name, boolean rule)` | DeathCareFirstCheck's trap - repairs leaving the builders' sick month a year's share of a cemetery's points, a University, a Penitentiary, schools and the landlords' Houses on site - with the death-care rule told nobo... |
| 235 | 41 | `static Game[] stall(Path root)` |  |

### 3. BEFORE AND WITH (lines 277-338)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 59 | `static void beforeAndWith(Game without, Game with)` |  |

### 4. THE BUILDERS GROW (lines 339-407)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 63 | `static void grow(Game without, Game with)` |  |

### 5. NEVER LAID OFF (lines 408-422)

| line | len | member | says |
|---:|---:|---|---|
| 410 | 12 | `static void neverLaidOff()` |  |

### 6. ONLY WHAT IT COULD STAFF (lines 423-483)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 55 | `static void onlyWhatItCouldStaff(Path root)` |  |

### 7. ONE PRIORITY (lines 484-567)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 81 | `static void onePriority(Path root)` |  |

### 8. CONSERVED (lines 568-578)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 8 | `static void conserved()` |  |


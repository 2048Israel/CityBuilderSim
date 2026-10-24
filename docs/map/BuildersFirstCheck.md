# BuildersFirstCheck.java - 584 lines · 17 methods · 5 constants · harnesses

`ham/citybuildersim/BuildersFirstCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildingManager](BuildingManager.md) (30), [Game](Game.md) (17), [ConstructionControl](ConstructionControl.md) (9), [DeathCareFirstCheck](DeathCareFirstCheck.md) (8), [BuildingsStacks](BuildingsStacks.md) (6), [BusinessInvestment](BusinessInvestment.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [MortgageCheck](MortgageCheck.md) (4), [Construction](Construction.md) (2), [Good](Good.md) (2), [Sector](Sector.md) (2), [Sectors](Sectors.md) (1), [MoneyAudit](MoneyAudit.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 158 | 1. THE RULE'S ARITHMETIC |
| 216 | 2. THE STALL, CAUSED |
| 279 | 3. BEFORE AND WITH |
| 341 | 4. THE BUILDERS GROW |
| 414 | 5. NEVER LAID OFF |
| 429 | 6. ONLY WHAT IT COULD STAFF |
| 490 | 7. ONE PRIORITY |
| 574 | 8. CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `BuildersFirstCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key: whose depots the rule serves first, and whose plan the fixture asks. |
| 115 | `BuildersFirstCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the template that adds most to their capacity. |
| 118 | `BuildersFirstCheck.YARD` | `Game.FOUNDING_YARD` | ...and the Builders' Yard the city founds with (PT1-M4), the other that adds to it: 160 points. |
| 121 | `BuildersFirstCheck.RUN_MONTHS` | `24` | The months the fixture's run is played: two years, a span the trace's city held through ten times over. |
| 432 | `BuildersFirstCheck.NEW_TOWN_MONTHS` | `6` | The months the new town is played: long enough for its first depot to open, crewed first, and its homes to come or not. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `static int fails` |  |
| 79 | `static PrintStream out` |  |
| 80 | `static PrintStream quiet` |  |
| 124 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 125 | `static int monthsAudited` |  |
| 344 | `static int openings` | What the run saw in the city with the rule, the months a depot opened: read by section 5. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 76 | 509 | **type** `public class BuildersFirstCheck` | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the months the builders expand at - off the top of their month, before the rule... |
| 82 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 87 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 93 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 99 | 5 | `static void quietly(Runnable r)` |  |
| 105 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 128 | 7 | `static void month(Game g)` | One month, audited. |
| 136 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE'S ARITHMETIC (lines 158-215)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 3 | `static double crew(double points, int buildings)` | A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. |
| 165 | 50 | `static void arithmetic()` |  |

### 2. THE STALL, CAUSED (lines 216-278)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 9 | `static Game stalled(Path root, String name, boolean rule)` | DeathCareFirstCheck's trap - repairs leaving the builders' sick month a year's share of a cemetery's points, a University, a Penitentiary, schools and the landlords' Houses on site - with the death-care rule told nobo... |
| 237 | 41 | `static Game[] stall(Path root)` |  |

### 3. BEFORE AND WITH (lines 279-340)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 59 | `static void beforeAndWith(Game without, Game with)` |  |

### 4. THE BUILDERS GROW (lines 341-413)

| line | len | member | says |
|---:|---:|---|---|
| 346 | 67 | `static void grow(Game without, Game with)` |  |

### 5. NEVER LAID OFF (lines 414-428)

| line | len | member | says |
|---:|---:|---|---|
| 416 | 12 | `static void neverLaidOff()` |  |

### 6. ONLY WHAT IT COULD STAFF (lines 429-489)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 55 | `static void onlyWhatItCouldStaff(Path root)` |  |

### 7. ONE PRIORITY (lines 490-573)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 81 | `static void onePriority(Path root)` |  |

### 8. CONSERVED (lines 574-584)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 8 | `static void conserved()` |  |


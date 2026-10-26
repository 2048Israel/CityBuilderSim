# BuildersFirstCheck.java - 648 lines · 17 methods · 7 constants · harnesses

`ham/citybuildersim/BuildersFirstCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

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
>      put depots on site beside three standing and the Builders' Yard, among
>      a few hundred people, the builders' own test (Sector.staffing())
>      says the town could not staff them, and they are crewed by the rule,
>      to the bit; the twin told it could staff them serves them first all
>      the same and opens one while the town with the test has not, where
>      the town with the test has work for its sites every month and builds
>      its homes. The twin's fill under MIN_STAFFABLE_TO_ORDER, its month
>      under the repairs and no home built were the works department's free
>      points multiplied by the fill, and are retired with them (PT1-M4).
>   7. ONE PRIORITY WITH THE DEATH CARE: while the dead wait, a depot of the
>      builders' and the city's cemetery on site together share what the
>      first group takes by the crews' rule - neither ahead, neither starved;
> ... (7 more lines in the source)

**Uses:** [BuildingManager](BuildingManager.md) (30), [Game](Game.md) (17), [ConstructionControl](ConstructionControl.md) (9), [DeathCareFirstCheck](DeathCareFirstCheck.md) (8), [BuildingsStacks](BuildingsStacks.md) (6), [BusinessInvestment](BusinessInvestment.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [MortgageCheck](MortgageCheck.md) (4), [Construction](Construction.md) (2), [Good](Good.md) (2), [Sector](Sector.md) (2), [Sectors](Sectors.md) (1), [MoneyAudit](MoneyAudit.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 161 | 1. THE RULE'S ARITHMETIC |
| 219 | 2. THE STALL, CAUSED |
| 282 | 3. BEFORE AND WITH |
| 344 | 4. THE BUILDERS GROW |
| 417 | 5. NEVER LAID OFF |
| 432 | 6. ONLY WHAT IT COULD STAFF |
| 554 | 7. ONE PRIORITY |
| 638 | 8. CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 115 | `BuildersFirstCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key: whose depots the rule serves first, and whose plan the fixture asks. |
| 118 | `BuildersFirstCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the template that adds most to their capacity. |
| 121 | `BuildersFirstCheck.YARD` | `Game.FOUNDING_YARD` | ...and the Builders' Yard the city founds with (PT1-M4), the other that adds to it: 160 points. |
| 124 | `BuildersFirstCheck.RUN_MONTHS` | `24` | The months the fixture's run is played: two years, a span the trace's city held through ten times over. |
| 439 | `BuildersFirstCheck.NEW_TOWN_MONTHS` | `12` | The most months the new town is played once it has its people, and its first depot opened early or not: the window ends the month the twin without the test opens one, or here (PT1-M4: it opens in the fifth, the gated ... |
| 448 | `BuildersFirstCheck.DEPOTS_STANDING` | `3` | The depots that stand beside the founding Builders' Yard (PT1-M4): the works department that built for a town nobody lived in is retired, and the yard alone, at the town's per-type fill (about a fifth), cannot out-wor... |
| 456 | `BuildersFirstCheck.FOUNDING_MONTHS` | `3` | The months the town is played before the twins are compared (PT1-M4): the town is founded with nobody in it, so the builders' fill after month 1 reads 0, and in months 2 and 3 the first homes fill (184, then 255 peopl... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `static int fails` |  |
| 82 | `static PrintStream out` |  |
| 83 | `static PrintStream quiet` |  |
| 127 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 128 | `static int monthsAudited` |  |
| 347 | `static int openings` | What the run saw in the city with the rule, the months a depot opened: read by section 5. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 570 | **type** `public class BuildersFirstCheck` | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the months the builders expand at - off the top of their month, before the rule... |
| 85 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 90 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 96 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 102 | 5 | `static void quietly(Runnable r)` |  |
| 108 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 131 | 7 | `static void month(Game g)` | One month, audited. |
| 139 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE'S ARITHMETIC (lines 161-218)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 3 | `static double crew(double points, int buildings)` | A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. |
| 168 | 50 | `static void arithmetic()` |  |

### 2. THE STALL, CAUSED (lines 219-281)

| line | len | member | says |
|---:|---:|---|---|
| 230 | 9 | `static Game stalled(Path root, String name, boolean rule)` | DeathCareFirstCheck's trap - repairs leaving the builders' sick month a year's share of a cemetery's points, a University, a Penitentiary, schools and the landlords' Houses on site - with the death-care rule told nobo... |
| 240 | 41 | `static Game[] stall(Path root)` |  |

### 3. BEFORE AND WITH (lines 282-343)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 59 | `static void beforeAndWith(Game without, Game with)` |  |

### 4. THE BUILDERS GROW (lines 344-416)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 67 | `static void grow(Game without, Game with)` |  |

### 5. NEVER LAID OFF (lines 417-431)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 12 | `static void neverLaidOff()` |  |

### 6. ONLY WHAT IT COULD STAFF (lines 432-553)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 95 | `static void onlyWhatItCouldStaff(Path root)` |  |

### 7. ONE PRIORITY (lines 554-637)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 81 | `static void onePriority(Path root)` |  |

### 8. CONSERVED (lines 638-648)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 8 | `static void conserved()` |  |


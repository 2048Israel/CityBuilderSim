# BuildersFirstCheck.java - 683 lines · 17 methods · 8 constants · harnesses

`ham/citybuildersim/BuildersFirstCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

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
> one depot order (one at a time; BusinessInvestment.MAX_CONCURRENT_ORDERS
> until BA1, sectors.Construction.ONE_DEPOT_ON_SITE since) waited in the
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
>      nameplate and its plan says its depot is on site every month
>      ("already building" until BA1; Construction.ONE_DEPOT_ON_SITE).
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
> ... (9 more lines in the source)

**Uses:** [BuildingManager](BuildingManager.md) (30), [Game](Game.md) (21), [ConstructionControl](ConstructionControl.md) (10), [DeathCareFirstCheck](DeathCareFirstCheck.md) (8), [BuildingsStacks](BuildingsStacks.md) (8), [Construction](Construction.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [Sectors](Sectors.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [MortgageCheck](MortgageCheck.md) (4), [Good](Good.md) (2), [Sector](Sector.md) (2), [MoneyAudit](MoneyAudit.md) (1), [TempDirs](TempDirs.md) (1)

**Used by (1):** [ConstructionTrapCheck](ConstructionTrapCheck.md)

## Sections

| line | section |
|---:|---|
| 164 | 1. THE RULE'S ARITHMETIC |
| 222 | 2. THE STALL, CAUSED |
| 285 | 3. BEFORE AND WITH |
| 347 | 4. THE BUILDERS GROW |
| 420 | 5. NEVER LAID OFF |
| 435 | 6. ONLY WHAT IT COULD STAFF |
| 589 | 7. ONE PRIORITY |
| 673 | 8. CONSERVED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 118 | `BuildersFirstCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key: whose depots the rule serves first, and whose plan the fixture asks. |
| 121 | `BuildersFirstCheck.DEPOT` | `"Construction Depot"` | The builders' depot: the template that adds most to their capacity. |
| 124 | `BuildersFirstCheck.YARD` | `Game.FOUNDING_YARD` | ...and the Builders' Yard the city founds with (PT1-M4), the other that adds to it: 160 points. |
| 127 | `BuildersFirstCheck.RUN_MONTHS` | `24` | The months the fixture's run is played: two years, a span the trace's city held through ten times over. |
| 442 | `BuildersFirstCheck.NEW_TOWN_MONTHS` | `12` | The most months the new town is played once it has its people, and its first depot opened early or not: the window ends the month the twin without the test opens one, or here (PT1-M4: it opens in the fifth, the gated ... |
| 451 | `BuildersFirstCheck.DEPOTS_STANDING` | `3` | The depots that stand beside the founding Builders' Yard (PT1-M4): the works department that built for a town nobody lived in is retired, and the yard alone, at the town's per-type fill (about a fifth), cannot out-wor... |
| 459 | `BuildersFirstCheck.FOUNDING_MONTHS` | `3` | The months the town is played before the twins are compared (PT1-M4): the town is founded with nobody in it, so the builders' fill after month 1 reads 0, and in months 2 and 3 the first homes fill (184, then 255 peopl... |
| 462 | `BuildersFirstCheck.LANDLORDS_HOUSES` | `10` | The landlords' Houses the fixture puts on site when the founding months are over (FC1): a few months of the town's sites' work, so the twins' comparison has homes to build. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `static int fails` |  |
| 85 | `static PrintStream out` |  |
| 86 | `static PrintStream quiet` |  |
| 130 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 131 | `static int monthsAudited` |  |
| 350 | `static int openings` | What the run saw in the city with the rule, the months a depot opened: read by section 5. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 602 | **type** `public class BuildersFirstCheck` | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the months the builders expand at - off the top of their month, before the rule... |
| 88 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 93 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 99 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 105 | 5 | `static void quietly(Runnable r)` |  |
| 111 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 134 | 7 | `static void month(Game g)` | One month, audited. |
| 142 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE'S ARITHMETIC (lines 164-221)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 3 | `static double crew(double points, int buildings)` | A site's crew by the rule (BuildingManager.weightOf()): its buildings times one's points to CREW_SCALE_EXPONENT. |
| 171 | 50 | `static void arithmetic()` |  |

### 2. THE STALL, CAUSED (lines 222-284)

| line | len | member | says |
|---:|---:|---|---|
| 233 | 9 | `static Game stalled(Path root, String name, boolean rule)` | DeathCareFirstCheck's trap - repairs leaving the builders' sick month a year's share of a cemetery's points, a University, a Penitentiary, schools and the landlords' Houses on site - with the death-care rule told nobo... |
| 243 | 41 | `static Game[] stall(Path root)` |  |

### 3. BEFORE AND WITH (lines 285-346)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 59 | `static void beforeAndWith(Game without, Game with)` |  |

### 4. THE BUILDERS GROW (lines 347-419)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 67 | `static void grow(Game without, Game with)` |  |

### 5. NEVER LAID OFF (lines 420-434)

| line | len | member | says |
|---:|---:|---|---|
| 422 | 12 | `static void neverLaidOff()` |  |

### 6. ONLY WHAT IT COULD STAFF (lines 435-588)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 124 | `static void onlyWhatItCouldStaff(Path root)` |  |

### 7. ONE PRIORITY (lines 589-672)

| line | len | member | says |
|---:|---:|---|---|
| 591 | 81 | `static void onePriority(Path root)` |  |

### 8. CONSERVED (lines 673-683)

| line | len | member | says |
|---:|---:|---|---|
| 675 | 8 | `static void conserved()` |  |


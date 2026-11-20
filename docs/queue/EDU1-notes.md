# EDU1 - the poverty trap's gap in EducationCheck

Builder: Claude (Opus), headless, 2026-11-20. Branch `batch/EDU1`, cut from origin/main 8ada323e3 (0.9.164), main
merged at 5c8e6c937 (FB14, 0.9.165) and 4d3e18673 (SB1). Harness and records only: no model code, no tag.

## What was found

`EducationCheck` section 15, "the price of a place" (`EducationCheck.java:1187`), builds three cities with
14 / 14 / 10 basic schools and one university, at tuition x1, x3 and x0, and runs 60 months. Its line "a poor city
at x3 enrols fewer than at x1: the trap is back" (`enrolled[1] < enrolled[0] * .9`, now `:1277`) was red on main, a
known red since R6b's pick (2026-11-17; `docs/notes/known-reds.txt`, `docs/notes/gate.txt` 84-86).

**When it went red.** It is a nightly harness the gate runs only when a pick names it (HA1), so its record lives in
the kept gate runs' `checks/times.tsv` (`C:\Users\Jerus\AppData\Local\Temp\cbs-gate-*`). Its last green runs
there: R2e's gate (32869da28, 0.9.123), R1d's (1e88c5466) and **SQ1's (d08c8116c, 0.9.129), exit 0**. Its first
red: R6b's pick on aeac6e5a9 (CU2, 0.9.130). Between SQ1 and CU2 lies one merge, DQ1 (a9f48dafa, no tag). Each probe
was `git archive` of its commit into its own folder (`%TEMP%\edu1-probe-<commit>`), compiled, and `EducationCheck`
run alone, one JVM at a time:

| commit | university enrolled x1 / x3 / x0 | willing x1 / x3 | verdict |
|---|---|---|---|
| a9f48dafa (DQ1) | 1,673 / 1,438 / 1,652 | .72 / .34 | green (x3 = .86 of x1) |
| aeac6e5a9 (CU2) | 1,652 / 1,632 / 1,680 | .72 / .35 | red (.99) |
| 8ada323e3 (0.9.164) | 1,686 / 1,576 / 1,643 | .71 / .38 | red (.93) |
| 5c8e6c937 (0.9.165) | 1,686 / 1,576 / 1,643 | .71 / .38 | red (.93) |

**CU2 closed the gap.** A month-by-month probe of section 15's cities (`%TEMP%\edu1-scripts\EduProbe.java`; the
seats from `BuildingManager.getStaffedEducationPlaces`, the pool from `Education.eligibleFor`) at DQ1 and at CU2:

- The intake is `min(free seats, eligible x willing x ENROLMENT_RATE)` (`Education.java:646`). At both commits the
  one university at x1 and at x0 is **full** by five years: the stock sits at its staffed seats, about 1,650-1,700
  of the 2,000 built (on 0.9.165 x1's peaks at 1.02 of them at month 45, x0's at 1.01 at month 30).
- At x3 the willing share falls exactly as before (affordability .38 at DQ1, .39 at CU2; willing .34 and .35), and
  the month's would-be intake is still about half x1's (at month 60, 61.2 against 111.3 a month on CU2).
- What moved is the city. On the chained world prices it grows faster: at month 60, 23,011 people at x3 (DQ1:
  19,779) and 21,033 at x1 (19,119), the diploma band with it (10,482 eligible at x3 against 8,857). The x3 stock,
  48 months of that half-rate intake, reached 1,632 by month 60, which is the seats x1 is capped at. Two full
  schools compare equal whatever the price.

## The judgment ★

**The premise moved for a true reason. The model did not move wrongly.** The fee still reaches affordability
(`Education.java:745`), affordability still reaches the willing share, and the willing share still reaches the
intake. Nothing between the cost and enrolment was cut. The fixture's sentence "one university in the same city is
willingness-bound" held only while the city stayed small. CU2, a shipped and ensemble-scored change of the world's
prices whose notes record other fixture cities growing differently (the village to 2,501, the played city 256
plots), made it a sixth larger at five years. Its seats bound at x1 and x0 already; now they bind at x3 too.
Mending the model would mean making the city poorer or the trap steeper, and neither is wrong today.

## What was built

`EducationCheck` section 15 only (`EducationCheck.java:1216-1255`):

- **Three universities, not one** (`:1248`), as the rest of the harness has, still with no college, so the pool is
  the diploma band. Three never fill: on 0.9.165 the x1 school stands at most at .72 of its staffed seats, x3's at
  .32 and x0's at .89.
- **Stepped by the month** (`simulateMonths(1)` sixty times, the same figures as one 60-month skip to the person:
  1,686 / 1,576 / 1,643 either way at one university) so the harness can record each city's fullest month.
- **A new fixture line** (`:1275`): "fixture: at x1 and x3 no university filled in five years, so the willing, not
  the seats, set every intake" (`fullest[0] < 1 && fullest[1] < 1`). It is read through `fullness()` (`:1505`):
  the student body over `getStaffedEducationPlaces` at the city's fill rate, the places the month hands
  `Education`. If the seats ever bind again, this line says so instead of the trap line failing for no stated
  reason.
- The trap line is unchanged, label and `.9` alike. The banner comment now says why three (`:1226`), with the
  figures above. The printed line adds the fullest shares.
- `docs/notes/known-reds.txt`: EducationCheck's row retired, as FB1 retired FoodProcessingCheck's.

On 0.9.165 the section prints: 3,307 at x1, 1,543 at x3, 3,867 at x0 (willing .74 / .32 / .90), fullest .72 /
.32 / .89. The trap line has x3 at .47 of x1. Every other line of the section holds: x0 more than x3, the fees and
subsidy identity at x1 and x3, more collected per student at x3.

## Both sides of the line

Each in its own scratch copy of c59751c7e (`%TEMP%\edu1-red-<label>`), with one `sed` on one file:

- **The old reading, one university** (`"University", 1`): the fixture line goes red (fullest 1.02 / .99 / 1.01)
  and the trap line goes red (1,686 / 1,576). The cause is named.
- **The trap removed from the model** (`affordability()` reads `feeAtOne` in place of `feeFor`): the trap line goes
  red (3,307 at x1, 3,312 at x3, willing .74 / .74 / .74), and so do the price line, "at x0 everybody can afford",
  "everybody the cap lets go, goes" and "more than at x3". The seats line stays green (.72 / .73 / .72). The trap
  line is what catches a trap that is gone.
- **Before CU2.** The rewritten harness on DQ1's merge (a9f48dafa) keeps section 15 green: 2,802 / 1,427 / 3,391,
  x3 at .51 of x1, fullest .65 / .30 / .81. The new premise holds on both sides of CU2. That tree's 17 other reds
  are sections written since against later main (the grant and loan levers open from the founding, OP1); none is
  section 15's.

## The trace

Unchanged: no game class, resource or `pom.xml` changed, only a harness and records. No ensemble is owed.

## Checks

- `./batch-check.sh EducationCheck` at c59751c7e: exit 0, "All checks passed".
- `sh tools/pick.sh origin/main HEAD`: DebtExchangeCheck, EducationCheck, HistoryCheck, ScaleCheck, TimingCheck,
  YearBookCheck (6 of 229, not ALL); review "not needed - no money, saves or threading in the diff".
- `./batch-check.sh --fast`: exit 0. Each picked harness alone (`./batch-check.sh <name>`, one at a time) at
  8234e66a7: exit 0, all six. After merging main (SB1: BuildingStackCheck and records only), `--fast` exit 0 and
  the maps regenerated; the merge touches nothing these harnesses read.
- No dry run: the pick is not ALL and the branch does not edit the publisher (docs/contract.md 5.5).

## Calls

1. ★ **The premise moved; the harness is rewritten, not the model** (above). Recommended, taken.
2. ★ **Three universities, not a later or earlier month.** Reading the one-university stock at month 36 or 48
   catches x3 before it fills (on CU2, x3 is .61 of x1 at month 36 and .89 at month 48), but it is a race the next
   change of growth moves. Room the willing cannot fill, asserted as a fixture, holds by construction. Two
   universities still bind at x1 and x0 (.99 and .995 of the seats). Three is the rest of the harness's own count.
3. **The threshold stays `.9`.** The ratio is .47 on 0.9.165 and .51 on DQ1's tree, so there is room on both
   sides, and the line reads as it did.
4. **The record.** `known-reds.txt`'s row is retired on the branch. `gate.txt`'s three EducationCheck lines (84-86)
   are the publisher's. A harness-only gate leaves the file alone, and a full gate drops a harness's lines only
   when it runs it (`tools/gate-record.awk`, HA1). They stay until a full gate's pick names EducationCheck. Until
   then a return of exactly that line would read "known". For the orchestrator, not for this branch.

## Left for Jerus

Nothing.

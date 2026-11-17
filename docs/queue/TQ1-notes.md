# TQ1 - the staffing gate's tie rule (2026-11-16)

The brief is TQ1's row in `QUEUE.md` and [GW1's notes](GW1-notes.md) section 5 ("What the first ensemble's costs came
from"). Cut from main d08c8116 (0.9.129). A model batch, built by an Opus session (Codex out of plan to 2026-11-17);
the harness by a Sonnet slice, reviewed and committed here.

## 1. Measured first, before the rule moved

**How.** The thirteen planners were first routed through one place, `Sector.Staffing.held()`, keeping main's strict
`>` exactly (commit 91aa94ef). Its default playtest (2,400 months, exit 0, "All checks passed.") wrote a trace
identical under `cmp` to main's reference (`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-08c8727d/`) in all eight
files, so the measurement is main's. A temporary probe (never committed; kept beside the run in `target/tq1/`) marked
every refusal `held()` met at the same share as the one it kept, and counted, in `Game.noteStaffingHold()`, each hold
whose kept refusal had a tie at its share: a hold the tie decided.

**How often the tie decides a hold**, by planner (the owner's key; Agriculture plans through
`BusinessInvestment.planMaker()`):

| planner | holds noted | decided by a tie | of those: asked, crews', unpaid | months, first to last |
|---|---|---|---|---|
| Construction | 305 | 278 | 264, 9, 5 | 2 to 1,760 |
| Restaurants | 467 | 226 | 203, 6, 17 | 4 to 1,510 |
| Manufacturing | 522 | 103 | 33, 2, 68 | 2 to 814 |
| Retail | 537 | 19 | 7, 2, 10 | 4 to 814 |
| Luxury Retail | 26 | 6 | 2, 0, 4 | 4 to 7 |
| Agriculture | 414 | 3 | 0, 0, 3 | 805 to 874 |
| Food Processing | 561 | 3 | 0, 1, 2 | 156 to 874 |
| eight more (Automotive, Cement & Concrete, Copper Refining, Freight, Heavy Industry, Industry, Materials, Wood Products) | 670 | 0 | | |
| **all** | **3,502** | **638 (18%)** | 509, 20, 109 | |

Most fall in the founding: of the 638, 435 in months 0-239 (Restaurants 185, Construction 122, Manufacturing 102,
Retail 18, Luxury Retail 6, Food Processing 2); Construction ties again through months 720-1,760 (154).

**Which template the tie gave the hold to**, and its posts against the others tied with it:

| planner: the first (kept), tied with | holds | posts: first; smallest, largest, mean of the tied |
|---|---|---|
| Construction: Builders' Yard, Construction Depot | 278 | 20; 20, 50, 35 |
| Restaurants: Diner, Restaurant | 226 | 4; 4, 12, 8 |
| Manufacturing: Fabrication Shop, Machine Works, Fabrication Works, Wire & Cable Works | 88 | 135; 135, 400, 246 |
| Manufacturing: Fabrication Shop, Machine Works, Fabrication Works | 13 | 135; 135, 276, 195 |
| Manufacturing: Fabrication Shop, Machine Works | 2 | 135; 135, 173, 154 |
| Retail: Convenience Store, Small Grocery Store | 19 | 5; 5, 27, 16 |
| Luxury Retail: Boutique, Department Store | 6 | 7; 7, 91, 49 |
| Agriculture: Mixed Farm, Grain Farm | 3 | 3; 3, 4, 3.5 |
| Food Processing: Meat Works with Bottling Plant, Snack & Oils Plant or both | 3 | 5; 4 to 6 |

In 636 of the 638 the first template listed was the smallest of the tie (the two others are Food Processing's). So
the rule that decided main's ties was in effect "the smallest building": WantedPosts priced a Builders' Yard's 20
posts where the yard and the depot asked alike, a Diner's 4, a Fabrication Shop's 135 against a mean of 246.

**What the first template's win did on GW1's draw** (GW1's notes 5, the gate's review). GW1's first ensemble
(99429bf8, the snapped share) against the unsnapped run (c3ca14fb), where float noise broke the same ties as it fell:
H2 (ZERO_HOT m342-402 minus its twin) went vacuous; the founding's longest spell of yoy < -10% went to 1 [0 to 19]
(0.9.83: 0 [0 to 0]); A2's longest spell of yoy > 10% from m240 to 15 [0 to 30]; the bank's months under
`CAPITAL_RATIO` to m600 to 17 (unsnapped 4). The unsnapped run had none of them (25 of 26, A2 11 [2 to 17]). GW1's
fixed draw (49c2d522) took the first-draw costs away, but kept the snap and so the first template's win: its
ensemble (`docs/ensemble/GW1.md`) has the founding's yoy < -10% at 0 [0 to 5] and A2 at 10 [7 to 24]. The
measurement above says what that win was: the smallest building of each tie bid for the founding's wage.

## 2. The thirteen planners

Each kept `if (staffingHold == null || staffing.share > staffingHold.share)` with its own name variable; each now
calls `Staffing.held(staffingHold, staffing)` and words its hold with `staffingHold.why()`:

| file:line | planner |
|---|---|
| `BusinessInvestment.java:923` | `planMaker()` (the makers without a planner of their own: Agriculture and the rest) |
| `sectors/Automotive.java:211` | `plan()` |
| `sectors/BusinessServices.java:179` | `plan()` |
| `sectors/Construction.java:795` | `plan()` |
| `sectors/ExtractionSector.java:318` | `plan()` (the mines, quarries and wells) |
| `sectors/FoodProcessing.java:244` | `plan()` |
| `sectors/Forestry.java:415` | `plan()` |
| `sectors/Freight.java:1777` | `plan()` |
| `sectors/LuxuryRetail.java:432` | `plan()` |
| `sectors/Manufacturing.java:239` | `plan()` |
| `sectors/Oil.java:598` | `planOnLand()` |
| `sectors/Restaurants.java:400` | `plan()` |
| `sectors/Retail.java:731` | `plan()` |

## 3. The rule ★: a tie is shared

**Refusals at the same share are tied, and the tie is shared.** The hold keeps every refusal at its share
(`Staffing.alike()`); `Game.noteStaffingHold()` notes each tied building as it would note it alone (a crews' hold, an
unpaid hold or an ask), in the order of their template ids, and each ask is its part of its posts, one of as many as
tied (`WantedPosts.Ask.part()`), its pay factor its own. The words of the hold name the first building the planner
met (the share in them is every tied building's); nothing priced depends on which that is. A larger share still wins
outright, as on main.

**Why this way.** The measurement made the call: main's tie rule was "the first listed", and the first listed was the
smallest in 636 of 638, so "the fewest posts" would have named main's rule and left GW1's costs where they were. The
costs were absent where float noise broke the tie as it fell (c3ca14fb); sharing is the deterministic form of that,
every month, and makes the order a sector lists its buildings in decide nothing (the id order makes it so to the bit).

**The other ways**, not taken:
- *The fewest posts* (the nearest to opening in hands): main's outcome in 636 of 638 ties, the costs kept.
- *The most posts*: the largest bid, every month; a Construction Depot's 50 where the yard and the depot asked alike,
  a Wire & Cable Works' 400 in Manufacturing's four-way tie, past what the noise ever priced on average.
- *The planner's own score* (what it would have built had it passed): thirteen scores, not one place, and a refused
  template has no score in most planners (they score after the gate).
- *The lowest id*: a rule by name only, as positional as the first listed.
- *Each tied ask held to its own room* (below): a settle() change, LD1's rule for every month's asks.

**What sharing does in `settle()`.** A month's asks are summed per band and held to the room of the best payer among
them (LD1: "the most the best-paying building that wants them could still pay"), so a tie's two halves are priced as
two sectors' asks always were. StaffingTieCheck's founding fixture shows the size: Retail's Convenience Store (5 posts)
and Small Grocery Store (27) tied at 0; alone, the store's ask settles to 2 unskilled and 3 diploma posts, the
grocery's to 1.31 unskilled (its pay factor's room); tied, the halves (8.5 unskilled and 6.5 diploma asked)
settle to 6 and 5, held by the best payer's room or the band's posts (`MOST_OF_POSTS`), no longer the grocery's. Under the noise's pick the month would have priced one or the other. This is settle()'s rule, not the
tie's, and TQ1 leaves it as it is.

**No save field and no format bump.** The tie lives within a month's planning: `alike()` rides the decision to
`noteStaffingHold()` in the same month, and `WantedPosts.getTiedHolds()` is a month's count, as its crews' and unpaid
counts are, read by StaffingTieCheck in the month. The latent posts saved are the same field as before; a save from
main loads and reads as it did.

## 4. What was built, and where

- `Sector.Staffing.held(kept, refused)` (`Sector.java`, banner "A TIE AT THE GATE IS SHARED"), the one place the
  thirteen planners keep their hold; `alike()`, the refusals tied; `why()`, the hold's words for its own template; a
  private copy constructor carrying the tied list (`tiedWith`).
- The thirteen planners (section 2): each `if (... > ...)` block is the one call, and its name variable is gone.
- `Game.noteStaffingHold(held)` notes each tied building in id order; the old body is
  `noteStaffingHold(held, ways)`, whose ask is `a.part(ways)`.
- `WantedPosts`: `Ask.part(int)`, `heldInATie()`, `getTiedHolds()` (reset in `beginMonth()`); `getHolds()` counts a
  tied hold once for each of its buildings (its javadoc says so).
- `StaffingTieCheck` (new, in `AllChecks` after StaffingDrawCheck), written by a Sonnet slice to this session's brief
  and reviewed here (one printed line's wording mended). Four sections. Section 1: a fresh city played until a planner
  hands in a hold for two of its sector's templates the gate refused at the same share to the bit. The founding's is
  Manufacturing's Fabrication Shop (135 posts) and Machine Works (173) at 0, month 1. The first at a share the draw
  filled part of is Retail's Convenience Store (5) and Small Grocery Store (27) at 0.464864594, month 65. Asking the
  planners moves nothing in the city: its fingerprint is the same after. Section 2: `held(a, b)` and `held(b, a)` carry
  both, a larger share met before, between or after is kept alone, and a smaller one leaves the two. Section 3:
  `noteStaffingHold()` in either order gives the same asks, counts and latent posts to the bit. One tied hold counts
  both buildings, each building's outcome is its outcome alone, each ask is its ask alone halved, and the posts asked
  are the mean of the two. Section 4: a city played 120 months has 466 holds noted and 37 tied, in 34 months. In 31 of
  those months a sector asked one question a month names two of its buildings (Construction 29, Manufacturing 2).
  55 assertions.
- REV-TQ1-1 (Opus, the orchestrator's stranger's review, on da61276a): SHIP, with two harness points, both mended
  (harness only). (1) The grown pair was a Convenience Store with a Filling Station, which no planner forms:
  `Retail.plan()` skips a template with no coverage, and the stations are `planStations()`'s. `tieIn()` now takes its
  pair from the hold a planner hands in (`Sector.plan()`, then `alike()`) and reads the two afresh at the gate. So a
  pair must be one planner's, and the search runs ten years (`MONTHS_TO_A_TIE` 60 to 120; the first such tie comes at
  month 65). (2) "One sector asks for two buildings" could pass with no tie, through Retail's shop and station holds in
  one month. It now counts only a month with a tie, in a sector asked one question a month: every sector but Retail,
  which is asked for its shops, its forecourts and the bank branch.

## 5. What the checks showed

- **StaffingTieCheck by hand on main's rule.** `held()`'s tie line was put back to main's for one run (`return kept;`,
  the strict `>` of 91aa94ef, whose trace is main's to the byte). On the first harness: exit 1, 22 of 53 FAILED, every
  one a claim of the rule; the log is `target/tq1/StaffingTieCheck-main-rule.txt`. On the harness after the review's
  mends: exit 1, 6 FAILED, 2 OK (`target/tq1/StaffingTieCheck-main-rule-2.txt`). On main's rule a planner never hands
  in a tie, so neither of section 1's fixtures is found within the ten years, and sections 2 and 3 have no tie to hold
  (their one line each fails). Section 4 fails "the planners hand in holds the gate tied" and "in some month a tie's two
  buildings both ask". Put back, 55 of 55.
- **The default playtest** on the rule (dbc00115): 2,400 months, exit 0, "All checks passed.".
- **Main merged in** (6169c305). Main moved on to aeac6e5a (0.9.130: DQ1, then CU2) while TQ1 waited for its pick.
  Its one conflict, `AllChecks.HARNESSES`' last line, now keeps both: StaffingTieCheck after StaffingDrawCheck, and
  DQ1's YoungBankCheck at the end.
- **The full pick** (the orchestrator's go, after CU2's dry run), on dc8f4da6 before the merge, ran all 212 harnesses.
  It exited 4, with NEW reds in two: BankCheck ("it pays its savers something", 1 FAILED) and TreasuryCheck (5 FAILED,
  starting with "fixture: the window is open as it falls due"). On the merged tree both of those lines pass. BankCheck
  is red there in another place, section 19 (`theBranchesByTheirCustomers`): 3 FAILED, starting with "the fixture placed
  five branches". BankCheck's pre-merge line was not rerun alone; the dry run is on the merged tree, where it passes.
- **BankCheck section 19, mended in the harness.** The fixture places four branches beside the charter's. Its town
  was given 30,000,000 sq ft at founding, and its builders spend what is left as the path goes. On TQ1's path the town
  has 43,076 sq ft free at that point, against main's 624,076, so the four were refused `NO_LAND`: one branch stood,
  none closed, and the month paid nothing. They are now given their ground (`4 * branch.getLandSqFt()`) before
  `buildStack`, as the town was given its own. The premise is unchanged: five standing on fees that cover two, and
  the three past the fees closed the month after. Unmended, on main's rule (`held()`'s tie line put back by hand), it
  passes: 2 standing a month later, 3 closed, "All checks passed." (`target/tq1/BankCheck-main-rule.txt`). Mended, on
  the merged tree with TQ1's rule: five placed, 2 standing, 3 closed, "All checks passed.".
- **`./batch-check.sh BankCheck TreasuryCheck` on the merged tree with the mend** (4e4a2d7e) ran four harnesses in 678s
  and exited 3, known red only. BankCheck and TreasuryCheck pass. The one red is YoungBankCheck ("the bank is never in
  resolution"), main's recorded OE1 entry in `known-reds.txt`.
- **TreasuryCheck alone on dc8f4da6**, a scratch build of that commit (`target/tq1-pre/`), is still red: 5 FAILED,
  exit 1. So it was not load: it is that tree's path. The fixture's dollar note falls due at month 65, and on dc8f4da6
  the window is shut then ("1,129.25k falls due, 16.26k of it abroad; window shut: 65% of exports already goes to
  foreign creditors"). On the merged tree the same month reads "1,008.85k falls due, 25.28k of it abroad; window open".
  TQ1's base, main d08c8116, passes TreasuryCheck (it is in neither `gate.txt` nor `known-reds.txt` there), and dc8f4da6
  is d08c8116 plus TQ1. So TQ1's path shut the fixture's window, and what the merge brought, DQ1's young-town borrowing
  and CU2's chained world prices, opened it again. The fixture does not cause the open window it asserts; the path
  does. That is a fragility of the fixture's own, which TQ1 does not mend. It is queued as **TW1**, "the fixture
  causes its own open window", in the ship file (the orchestrator's call, 2026-11-17).

## 6. The trace

`-Dplaytest.trace` on the default playtest against main's reference (`C:/Users/Jerus/AppData/Local/Temp/
cbs-trace-main-08c8727d/`, still main's: SQ1's was byte-identical to it). The refactor alone (91aa94ef) is identical
in all eight files. The rule moves all eight: `labour` from month 4 (the founding's first ties fall in months 2-4), `bank`, `bonds`,
`build` and `month` from month 27, `house` from 28, `pop` from 43, `borrow` from its second paper (main's month 99). One city's path, so the ensemble judges; the
size, main then TQ1:

| month | people | out of work | jobs | inflation, a year | unskilled premium | diploma premium |
|---|---|---|---|---|---|---|
| 60 | 1,645; 1,612 | 0; 0 | 963; 897 | 22.1%; 23.3% | 1.196; 1.194 | 0.924; 0.919 |
| 120 | 2,432; 1,332 | 0; 20.6% | 1,554; 582 | 6.3%; -1.6% | 0.942; 0.807 | 0.721; 0.696 |
| 240 | 3,115; 2,319 | 10.3%; 0 | 1,446; 1,259 | -3.2%; 2.9% | 0.938; 1.016 | 0.711; 0.765 |
| 600 | 3,522; 6,593 | 10.1%; 0 | 1,599; 3,214 | 6.3%; 8.4% | 0.928; 0.980 | 0.690; 0.740 |
| 1,200 | 33,240; 33,822 | 7.9%; 0 | 14,026; 16,354 | 9.9%; 9.0% | 0.883; 0.962 | 0.677; 0.720 |
| 1,800 | 286,027; 186,301 | 0.4%; 0 | 123,523; 85,481 | -3.0%; -1.8% | 0.974; 1.032 | 0.709; 0.793 |
| 2,399 | 1,375,880; 1,222,970 | 10.6%; 0.7% | 543,126; 539,218 | -8.4%; -6.6% | 0.917; 1.019 | 0.720; 0.752 |

The wage's premiums stand higher from month 240 on, as the larger bids would have them. This city's founding stalled
where main's did not: out of land at month 62 ("Holding: no land"), then "housing ahead of jobs" with no order and the
builders idle from month 70 to 170 (jobs 572-582, 15-38% out of work), while main's grew through the same months.
A path that parted at month 4; the ensemble's founding rows say whether it is the rule's.

**On the merged tree** (4e4a2d7e: main aeac6e5a merged in, with the BankCheck mend), against main's new reference
(`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-aeac6e5a/`). The default playtest ran 2,400 months and printed
"All checks passed.", then **exited 1 on a finding**, "households with no home", 54 times in months 692-745 (worst,
month 741: 115 households nowhere, 3,162 homes of 6 sizes). Section 9 has its probe. All eight files move: `labour`
from month 4, `bank`, `build`, `month` and `house` from 36, `bonds` from 37, `pop` from 53, `borrow` from its first
paper (month 59). The size, main then TQ1:

| month | people | out of work | jobs | inflation, a year | unskilled premium | diploma premium |
|---|---|---|---|---|---|---|
| 60 | 977; 977 | 0; 0 | 596; 596 | 15.4%; 15.5% | 1.072; 1.079 | 0.825; 0.825 |
| 120 | 1,474; 1,471 | 0.1%; 0 | 767; 767 | 10.6%; 10.6% | 0.964; 0.963 | 0.665; 0.665 |
| 240 | 1,835; 1,833 | 9.4%; 9.4% | 819; 819 | -4.3%; -4.2% | 0.938; 0.939 | 0.713; 0.713 |
| 600 | 14,670; 14,441 | 0; 0 | 7,312; 7,244 | 10.8%; 15.1% | 1.028; 1.037 | 0.790; 0.791 |
| 1,200 | 23,870; 33,302 | 10.9%; 9.4% | 9,864; 13,852 | 8.6%; 7.4% | 0.850; 0.850 | 0.719; 0.700 |
| 1,800 | 259,319; 40,436 | 0; 9.1% | 114,380; 16,856 | -2.0%; -3.8% | 0.993; 0.868 | 0.764; 0.700 |
| 2,399 | 1,473,260; 630,320 | 6.9%; 5.9% | 622,460; 258,580 | -6.2%; -7.8% | 0.948; 0.971 | 0.720; 0.723 |

Both towns stall after month 900. Main's falls to 21,664 at 1,000, stands near 24,000 to 1,300 and takes off at about
1,400. TQ1's stands near 33,000 ("housing ahead of jobs (14,222 now; 0 coming)" from 1,300) until about 1,750, so it
ends at less than half main's size. The ensemble was run again on the merged model (section 7).

## 7. The ensemble

**On the merged model** (the orchestrator's call, 2026-11-17: once on the final model, against the newest versioned
baseline). `BASELINE=docs/ensemble/0.9.130.md tools/ensemble/ensemble.sh TQ1` ran on d0c9d575 (0.9.130 with TQ1): 80
of 80 runs, 44.8 min, exit 4. It is committed as [docs/ensemble/TQ1.md](../ensemble/TQ1.md) in place of the first
run's, whose folder is now `target/ensemble/TQ1-premerge/`. Criteria 20 of 25 against main's 21 of 26 (H2 is vacuous
here, so not counted); hard 7 of 10 against 9 of 10.

**M1 passes** in all five policies. **F1**: AUTO2, ZERO_SLACK and HIGH have 1/16 runs with "households with no home"
against main's 0/16, so they FAIL. SHOCK has 1/16 against main's 1/16 and passes by count, but it is not main's city:
main's is seed 13, months 738-776 (CU2's notes). The four are one city, seed 0, the finding once at month 182, before
any policy parts from its AUTO2 twin. Seed 3, the first run's F1, is clean on the merged model.

**The hard count is information** (the orchestrator, 2026-11-17, its call): 7 of 10 against the baseline's 9 of 10.
Four hard verdicts changed against `docs/ensemble/0.9.130.md`, all F1. AUTO2, ZERO_SLACK and HIGH went PASS to FAIL
(1/16 against 0/16), and SHOCK went FAIL to PASS (1/16 against 1/16: main's seed 13, ours seed 0). Net, two fewer pass.
F1 passes by name: the four flags are one city of FB11's class, crossing the watch's line for one month, and main's
rule from month 2 gives the same city (below). M1 passes in all five.

**Seed 0, replayed** with section 8's probe rebuilt on the merged snapshot (`target/tq1-homes/run0.sh`, output in
`s0/`; the control's CSV equals the ensemble's AUTO2-0 to the byte for 230 rows). Real Estate is held "no land" from
month 100 or earlier (5,570 m² needed and 684 free from 140), with homes at 215 and the treasury's cash just below
zero (-2 to -5). Arrivals stop as the room runs out (none from 179). At 180 a Small Childcare Centre order buys
0.0198 km² first (North 4), and the landlords build 3 Low-Rise Apartments at 181. A second order at 182 buys 0.0216
km² more, and at 183 the town takes off (9 Houses, 37 arrivals). The watch fires once, at 182. Main's rule switched on:

| main's rule from | the path moves from | households with no home |
|---|---|---|
| 2 | 36 | none |
| 30 | 36 | none |
| 60 | 65 | once, 182 |
| 90, 100, 140, 170 | 186 | once, 182 |

From month 2 (main's model in all but the founding's first month) it is the same city: the same 215 homes, the same
hold for land, the same childcare order buying ground at 180, the same take-off at 183, its people within two (1,366
against 1,367 at 182). Only the watch's threshold, more than one household unplaced, is crossed on TQ1's path at 182
and not on main's. So it is FB11's class, as seed 3's and the default playtest's are: landlords held for land while
nothing buys ground, the escape a works order that buys more ground than it uses.

**The rest, information.** H2 is vacuous (-0.88%; main's -0.52%, FAIL). K2 passes (2.86%, the twin -0.20%; main's
3.72%, FAIL). A1 3.68% a year against 3.86%; A2 24 [17 to 40] against 24 [16 to 41]; A3 0 [0 to 4] in both. T1 16/16
seeds reach 40,000 people, at month 1,071 against 824. Final people, median: AUTO2 1,836,044 against 1,570,712;
ZERO_SLACK 1,115,392 against 1,167,092; ZERO_HOT 6,955 against 7,036; SHOCK 38,110 against 37,853; HIGH 26,363 against
29,262. K1 17.33% against 16.61%; R1 -4.08% against -3.81%.

**Before the merge** (the first run, its verdicts as they stood). `tools/ensemble/ensemble.sh TQ1` on 88d9f81b (the
model of dbc00115 and the harness), on the orchestrator's go after CU2's: 80 of 80 runs, 99.0 min, exit 4, scored
against `docs/ensemble/0.9.128.md` (main's newest then), committed then as `docs/ensemble/TQ1.md` (da61276a). Criteria
18 of 25 against main's 22; hard 6 of 10 against 10 of 10.

**M1 passes** in all five policies (no failed or unread money audit). **F1 fails in four**: AUTO2, ZERO_SLACK, SHOCK
and HIGH each have 1/16 runs with "households with no home", against main's 0/16. The four are one city: seed 3, the
finding first at month 428, before any policy parts from its AUTO2 twin (the twins are identical to their switch,
64/64). These four F1s are the only changed verdicts.

**The finding, replayed.** Seed 3's AUTO2 run replayed alone to month 440 on the ensemble's own class snapshot, with
the housing read beside it (`-Densemble.extra`; its columns 1-62 equal the ensemble's run to month 430):

| month | people | homes | households | doubled up | unplaced | arrivals | doors left (room) | on site |
|---|---|---|---|---|---|---|---|---|
| 420 | 3,977 | 613 | 1,145.95 | 571.26 | 0 | 0 | 11.1 | 0 |
| 425 | 3,994 | 613 | 1,152.04 | 576.51 | 0 | 0 | 3.7 | 0 |
| 427 | 4,001 | 613 | 1,154.69 | 578.34 | 0.53 | 0 | 0 | 0 |
| 428 | 4,004 | 613 | 1,155.95 | 578.98 | 1.03 | 0 | 0 | 0 |
| 440 | 4,045 | 613 | 1,169.53 | 585.76 | 6.60 | 0 | 0 | 0 |

A town of 4,000 at full employment, every home doubled (doubled up is the half-the-households-plus-one ceiling,
`FamilyModel`), migration bringing nobody (it is damped as the valves run out, as designed), and the landlords
building nothing: the town's own growth forms about 1.2 households a month past the ceiling, and LongPlaytest's
watch (more than one household unplaced) fires at 428. Section 8's same-state probe reads it. The landlords are held
for land from 324, and no gate is involved. Main's model stalls alike from TQ1's state at 360 on. The finding passes
by name with FB11 (ER1-F's case), not HN1, whose placement lag this first reading guessed.

**The rest, information.** H2 is vacuous as on main's baseline (-0.55% against 0.54%, both VACUOUS: the ZERO_HOT city
has no burst to stop). A1 3.11% a year against 3.04%; A2 22 [14 to 36] against 22 [11 to 37]; A3 0 [0 to 20] against
0 [0 to 12]; the young city's inflation (m240-600) 4.89% against 4.79%; the founding's spells (m120-240) 11 [0 to 24]
and 0 [0 to 0] against 10 [0 to 24] and 0 [0 to 0]. T1 16/16 seeds to 40,000 people (month 1,044) against 15/16 (1,102).
Final people, median: AUTO2 1,627,676 against 1,578,924; ZERO_SLACK 1,207,162 against 984,883; ZERO_HOT 5,220 against
4,933; SHOCK 32,754 against 31,300; HIGH 26,026 against 26,102. K1 16.97% against 15.92%; R1-R3 pass, a little
smaller (R1 -3.47% against -4.22%). The first ensemble's costs GW1 saw (the founding's deflation, A2's longer spells)
are not here; nor is a gain on H2.

## 8. The same-state probe of seed 3's finding

The orchestrator's instruction (2026-11-16): find the month seed 3's homes stopped being ordered, play on from the same
state on main's model and on TQ1's, and read which gate refuses. The probe (scratch, `target/tq1-homes/`, not committed)
is the ensemble's own driver and class snapshot with two changes: `Sector.Staffing.held()` keeps the first of a tie
(main's rule, which section 4 showed is byte-identical to main) from a month named on the command line, and every month
from 290 logs what the landlords and the builders said, the holds, the latent posts, the premiums, the land, the
treasury and the works' orders. No save is written and loaded: the switch happens inside the one run, so the state at
the switch is the ensemble's to the bit (the run with no switch equals the ensemble's AUTO2-3 CSV, all 480 rows), and the
player's script after it is the ensemble's too.

Homes were last ordered at month 320 (63 doors finished at 324); from 324 nothing is on site.

| main's rule from | parts from TQ1's run | homes on site again | "households with no home" |
|---|---|---|---|
| (TQ1 throughout) | - | 455 | 428 |
| 300 | 325 | 381 | none |
| 316 | 325 | 381 | none |
| 340 | 341 | 381 | none |
| 360 | 361 | 455 | 428 |
| 380 | 381 | 455 | 428 |
| 400 | 464 | 455 | 428 |

**No staffing gate refuses the homes.** In every run, Real Estate says "Holding: no land - needs 5,570 m², 2,520 m²
free" every month from 324 to the escape. It never asks the gate (`RealEstate.ordersOnlyWhatItCanStaff()` is false).
Construction says "0.0 months of work queued": it holds for want of work, not for staff. The landlords' plan wants
homes the whole time: the head shortfall is 2,137, against 3,080 places for 2,140 posts. The treasury is overdrawn
($30k to $76k) from 330, and its debt stays flat at $13.1M (the works' ratio is 1.2 to 1.6 of a year's output, under
the 2.40 limit). Nothing buys ground for a sector that is held for land. The player's "room to grow" is a move the
treasury's cash pays, and the works buy ground only for an order of their own.

**What lets homes through is a works order that buys ground.** On main's path from 300, 316 or 340, the works order a
Small Childcare Centre at month 380 ("115% served, 136% once what is on site opens") on a $185k bond. They buy 0.0288
km² (West 19) for it first, leaving 17,251 m² free, and the landlords build 2 Low-Rise Apartments at 381. The town then
takes off: 38 to 58 arrivals a month and 1,076 homes by 415. On TQ1's path, and on main's from 360, 380 or 400, that
order does not come. Ground arrives only with the player's "bought room to grow" at 454, homes at 455, and the
watch has fired at 428 by then.

**What the tie changed is the path.** From 325 TQ1's run has one tie a month, so its holds are 7 (4 asked) against
main's 6 (3 asked). The second building's ask moves the latent posts a few places (m340: [75.5, 0, 5.5, 0] against
[73, 0, 0, 0]) and the premiums in the third decimal. That is the rule's designed effect, the asks priced on the
mean. Whether the childcare order lands at 380 rides on that path. Main's own model, switched on at 360 or later,
misses it as TQ1's does. Switched on at 340 or earlier, it catches it.

So the finding is main's: a town at full employment whose landlords are held for land for 130 months, while nothing
buys them any and the treasury cannot. TQ1's path reaches it, and so does main's model from TQ1's state at 360, 380
and 400. The escape is a works order that happens to buy more ground than it uses, not a mechanism. The tie rule is
not changed for it.

**The disposition** (the orchestrator, 2026-11-16, its call): no model change in TQ1. F1 (seed 3; AUTO2, ZERO_SLACK,
SHOCK and HIGH) passes by name with **FB11** ("held towns: why no works pays, and the advisor's land funding"), not
HN1, and no new batch. It is ER1-F's case, folded into FB11: fund LongPlaytest's `keepGroundAhead`/`advise`
growth-land purchase through the land office's bounded funding flow, with RE3-J beside it. This seed-3 stall (Real
Estate held for land from 324 to 455, the treasury overdrawn, main's model stalling alike from 360) is a measure FB11
can use: the ensemble driver with `-Densemble.months=460` on seed 3 AUTO2, its CSV's homes and treasury columns, and
each month's reasons, read through `Game.getLastInvestment()` and `AutoBuilder.latest()`. The first paragraph says
how the probe was built; its scratch files (`target/tq1-homes/`) go with this worktree.

## 9. The default playtest after the merge: households with no home, months 692-745

**What holds it.** On TQ1's merged path Real Estate is held "no land - needs 5,570 m²; 1,190 m² free" from month 640
(257 m² free from 702), with homes flat at 3,162 from 644 to 741 while households grow from 5,600 to 6,300. The
treasury is overdrawn from 655 to 693 and near empty until about 730. The playtest's "ground kept ahead" (0.7.58)
reports "the cash share stopped a look short 12 time(s)" over the run. Cash builds from 80k at 735 to 197k at 741,
when the hold turns to the bank's capital and then lifts. At 741 the landlords build 10 Low-Rise Apartments (trimmed
from 23). Main's run builds through the same months (3,465 homes at 690, 4,538 at 720, 4,979 at 741).

**The same-state probe**, as section 8's for seed 3: a scratch copy of `LongPlaytest` (`target/tq1-ltp/`, probe only)
sets section 8's `Sector.Staffing.mainRule` before the month `-Dtq1.mainFrom` names, and each run stops at month 780
(`-Dplaytest.months=780`). The control (no switch) matches the full run's trace through 780 and gives the same
finding. Every short run also fires "industry stands farther out than the homes" at 780, its last month, as FB5's does
at 2,400.

| main's rule from | the path moves from | homes at 650 / 700 / 741 / 780 | households with no home |
|---|---|---|---|
| never (TQ1) | - | 3,162 / 3,162 / 3,162 / 4,296 | 54 times, 692-745, worst 115.24 at 741 |
| 640 | 640 | 3,162 / 3,162 / 3,162 / 4,296 | 54 times, 692-745, worst 115.42 at 741 |
| 600 | 600 | 3,162 / 3,162 / 3,162 / 4,296 | 54 times, 692-745, worst 115.28 at 741 |
| 560 | 590 | 3,162 / 3,162 / 3,162 / 4,296 | 54 times, 692-745, worst 115.28 at 741 |
| 480 | 508 | 3,728 / 4,484 / 6,142 / 6,588 | none |
| 400 | 426 | 3,603 / 3,666 / 3,920 / 5,495 | none |
| 300 | 370 | 2,972 / 4,171 / 4,424 / 4,739 | none |

So main's own model, switched on from TQ1's state at 560 or later, stalls the same months to the household. From 480
or earlier, its path finds ground. This is seed 3's pattern (section 8: main's model from 360 or later stalls, from
340 or earlier escapes), and the same class: landlords held for land while nothing pays for ground, and an overdrawn
treasury. Whether the town stalls rides on the path, which the tie rule moves by design.

**On the gate** the full suite's LongPlaytest exits 1 with no FAIL line. `known-reds.txt`'s "LongPlaytest | (no FAIL
line) | FB5" entry (CL6's industry ring) matches any such exit, so the judge passes it under FB5's name.

**The disposition** (the orchestrator, 2026-11-17, its call): the advisor's ground-ahead buy, stopped by the cash share
while Real Estate is held for land, is ER1-F's cause, folded into FB11 ("the advisor's land funding"). It is main's
latent fault, so it passes by name with FB11. **It is named here alone, not in the ship file.** `tools/known-reds.sh`
keys a row by harness and part, and a ship-file row replaces the registry's entry of the same key. Tried with
"LongPlaytest | (no FAIL line) | FB11 | 2026-11-17 | ..." in a copy of this ship file: the merged list kept the FB11
row and dropped FB5's, so it would not stand beside it. When FB5 turns its own red green, this red would be unnamed
again. One clause of the changelog paragraph names it.

## 10. The dry run

**The first** (the orchestrator's go, 2026-11-17): `sh tools/gate.sh TQ1 --dry-run` on e09907c4 (main f38eafbc, FB1,
merged in at 5d64b938, its three generated indexes taken from main and regenerated), run `/tmp/cbs-gate-TQ1-Z9cmgz6L`.
The suite ran in 1,505 s, exit 4; the gate exited 1 on one unnamed red, MapCheck's "fixture: an offer of the city's
own level holds two or more blocks of free ground, each wholly its own" (`MapCheck.java:11256`). Its other red line
there, "at every 30th month the map draws exactly what the city has", is main's (FB5). Known red only besides:
InvestCheck, PlanCheck, DeathRecordCheck, PrizeCheck, YoungBankCheck, and LongPlaytest, whose finding is section 9's
"households with no home" (54 times, months 692-745), passed by FB5's "(no FAIL line)" entry and named FB11 here.

**The MapCheck fixture reads the path.** It takes the first standing offer `ownBlocks()` finds, and on TQ1's merged
path none stands at the city's level when the section runs. Scratch overlays (`target/tq1-map/`, probe only, run
alone): on main's rule (`Sector.Staffing.mainRule` from the start) MapCheck unmended passes the fixture, with a
different played city at level 4 and 22,288 plots. On TQ1's rule, with the section given FR8's own fallback (the
section after it, in the same file: where no such offer stands, the city buys the largest standing offer first,
through the office), it buys 2,048 plots, rises to level 5, and the whole section passes. The sections after it
then read the city the gate's run read (40,080 plots at level 5, South 15 bought). That run's other four reds are main's
FB5 line and three wall-clock timings, which the suite runs soft. The harness is unchanged on this branch until the
orchestrator's call.

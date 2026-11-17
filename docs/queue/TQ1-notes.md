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
  and reviewed here (one printed line's wording mended). Four sections: 1 a fresh city played until the gate refuses
  two of a sector's templates at the same share to the bit (Retail's Convenience Store and Small Grocery Store at 0,
  month 1; the Convenience Store and the Filling Station at 0.333292257, month 10, a share the draw filled part of);
  2 `held(a, b)` and `held(b, a)` carry both, a larger share met before, between or after is kept alone, a smaller
  one leaves the two; 3 `noteStaffingHold()` of either order gives the same asks, counts and latent posts to the bit,
  one tied hold counting both buildings, each building's outcome its outcome alone, each ask its ask alone halved and
  the posts asked the mean of the two; 4 a city played 120 months: 466 holds noted, 37 tied in 34 months, 33 months in
  which one sector asked for two of its buildings (Construction 29, Manufacturing 2, Retail 2). 53 assertions, 12 s.

## 5. What the checks showed

- **StaffingTieCheck by hand on main's rule.** With `held()`'s tie line put back to main's (`return kept;`, the strict
  `>` of 91aa94ef, whose trace is main's to the byte) for one run: exit 1, 22 FAILED of 53, every one a claim of the
  rule (section 1's fixture lines pass; the run's log is `target/tq1/StaffingTieCheck-main-rule.txt` in this
  worktree). Section 2: `held(a, b)` and `held(b, a)` carry both (founding and grown, 4), a smaller share leaves the
  two (2). Section 3, founding and grown: the same asks whichever is met first, the same latent posts to the bit, one
  tied hold counting both, each building's outcome its own, each ask halved, the posts the mean (14 of 16: the
  founding's counts agree on main's rule, each order's one building asking, and the grown city's one ask is a building
  that asked alone). Section 4: "the planners hand in holds the gate tied" and "in some
  month one sector's asks name two of its buildings". Put back, 53 of 53.
- **The default playtest** on the rule (dbc00115): 2,400 months, exit 0, "All checks passed.".

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

## 7. The ensemble

`tools/ensemble/ensemble.sh TQ1` on 88d9f81b (the model of dbc00115 and the harness), on the orchestrator's go after
CU2's: 80 of 80 runs, 99.0 min, exit 4, scored against `docs/ensemble/0.9.128.md` (main's newest), committed as
[docs/ensemble/TQ1.md](../ensemble/TQ1.md). Criteria 18 of 25 against main's 22; hard 6 of 10 against 10 of 10.

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
watch (more than one household unplaced) fires at 428. Nothing in it is the tie: it is HN1's class ("a played seed-12
city leaves households without matching homes for four months on main too; retain the hard housing watch and mend the
model", `docs/queue/HN1.md`, from R0h's same-state probe), main's housing code reached on TQ1's path.

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
not changed for it. The orchestrator's disposition is recorded below.

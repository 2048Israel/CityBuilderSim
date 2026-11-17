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

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

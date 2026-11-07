# FX9 - notes: ground bought inside an open cell gets the cell's own streets ({next}, 2026-11-07)

*{next}, 2026-11-07. [The brief](FX9.md); Jerus, 2026-10-23, on FX6's ★1: "Extend the streets". Built on AN2's tip 5456fabf (RL1); main merged in before the final checks. The map's alone: nothing in the model reads it, and the default playtest's trace is byte-identical to the base's in all eight CSVs, so no ensemble (EN1's rule). Save format and the map's sidecar unchanged. Two Sonnet slices (section 6).*

## 1. What was built

- **The cell's own streets on the ground bought** (`DistrictPlan`, the ground bought, `Builder.cellsOnGrown()`). When a growth of the plan's ground (FX6's `growGround()`) adds plots inside or along a cell already open, that cell's ring and streets are laid again from what its opening recorded: kind, layout (CL2's three), direction, square, boulevard, an estate's lines, a crescent's arc.
  - **What it may lay on.** Only the plots just bought: `lay()`'s gate leaves the old ground as it stands, so a merge, a pruned piece or an avenue's floor there is untouched. Ground the city does not own is a cut, as at opening.
  - **Joins.** Stray pieces are taken up (`prune()`). The new streets' own dead ends are joined along the cut (`shore()`, its other dead ends untouched). No curve is recorded: a band would be kept free over old plots that may hold buildings.
  - **Avenues.** The avenues and their + floor follow as at opening: `avOnGrown()` takes the cells.
  - **Parted pieces.** Any new street not in the district's main network is taken up (`pruneApart()`), so a piece the ground parts, or one touching only the frame's edge, is not grown onto the ground that parted it.
  - **Timing.** A plot just bought holds no building, so nothing that stands moves. The new ground fills from the next month's arrivals, as before.
- `openCell()` factored into `layRing()` and `layLayout()`. With the new streets off, PlanCheck's log is the base's, line for line, so no plan changed by it.
- `avFloor()` no longer repeats a kept junction when a box is floored again (an `avKeptHas` guard; a yes/no, so nothing else changes).
- A harness switch, `DistrictPlan.streetsOnBought` (true in play; false is FX6's way). `--pick` flags its `volatile`: it is read-only in play, as `keepGround` is. The plan reports `boughtStreets` and `boughtCells`.

## 2. Measured

| | FX6's way (main) | FX9 |
|---|---|---|
| MapCheck 2c, the first block at S3 (the district full): its dry plots in cells open before the purchase built on | 233 of 667 | 533 of 667 |
| ...in the cells that opened after it | 1,832 of 2,295 | 1,728 of 2,295 |
| ...the district's houses its plan places, S3 / S4 | 3,188 of 3,356 / 3,488 of 3,656 | 3,356 of 3,356 / 3,656 of 3,656 |
| PlanCheck 12, half a cell bought alone, three months of houses: boxes on the half / without a place | 234 / 3,580 | 382 / 3,433 |
| ...the street plots on the new half the cell lays when owned whole, laid | 0 inside the rings | 166 of 166 |
| the playtest's city at m4000 (kept, read in a copy): its districts' own plans place | 11,422 of 26,317 | 11,655 |
| ...MF1's rim, the buildings packed at its edge | 7,763 | 7,301 (462 fewer) |

- **The district holds its houses at home.** 2c's full district places every house it is given, where FX6's way handed 168 to the next district.
- **The new ground is built on as a new cell is.** 80% of its plots against 75% in the cells opened after the purchase (FX6's way: 35%; FX6's own notes measured 56 of 667 on its fixture of the day).
- **MF1's overflow, "For Claude".** His city (slot 3, save format 43) does not load on main, so the rim was counted on the playtest's city: 4,204 street plots laid on bought ground, in 219 cell layings.

## 3. The moves on the map: DM2's class, moved about

- **No purchase moves a box.** That is FX6's and FX7's watch, and it holds.
- **The cities differ.** The bought ground is now built on, so from the first purchase into an open cell the two histories differ: other cells open at other times, on other ground.
- **Where DM2's events fall changes.** DM2's class is FW11's way to a piece the buildings wall in, kept clear and then let go when a departure cures the wall, the boxes behind it moving. Its events fall elsewhere.
- **The mechanism** (`FX9/FX9Probe5.java`, PlanCheck 6's city). FX6's way left bought ground empty, and the plan's end join found its free way over it; built on, the join must go over buildings. PlanCheck 6 now reports one district (0,0) planned again with a 27-plot way, from m61. It joins a cell's piece to the network; that piece is none of FX9's own streets, which the debug pass found in the main network after every growth.
- **Counted month by month** (`FX9/FX9Moves.java`): every district's own plan, every month of the default playtest, the boxes that moved by the layout watch's rule (moves off a run included, which the watch counts apart):

| default playtest | months | boxes moved | month pairs | pairs only this way |
|---|---|---|---|---|
| main (streets off) | 2,400 | 138 | 26 | 4 (21 boxes) |
| FX9 | 2,400 | 147 | 27 | 5 (38 boxes) |
| main | 4,000 | 5,295 | 102 | 9 (55 boxes) |
| FX9 | 4,000 | 5,339 | 99 | 6 (39 boxes) |

- **The layout watch.** Over 4,000 months the two are a wash. The watch's four 13-month windows see none on main at either length. On FX9 at 2,400 months they see one: m1209, district (-1,-1), 20 of 174 boxes. A departure of 39 buildings cured the wall that m1190's arrivals made (7 boxes then), and the way was let go (`FX9/FX9Probe.java`: only the log's new record moves them, and only with the streets on; `FX9Probe3`, `FX9Probe4`: the corner piece and its way).
- **What it makes red.** That is LongPlaytest's one red at 2,400 months (★1); at 4,000 it is green. Main's own events of the class fall outside the windows (m2179, 18 boxes).
- **Not mended here.** The mend is DM2's dated ways. `pruneApart()` keeps FX9 from growing a parted piece, the one structure of its own that could be walled; it did not move m1209, whose corner piece was the district's main network when its ground was bought (m407).

## 4. The checks

- **PlanCheck 12, (a) to (e)** (new), on the half-cell fixture:
  - the cell's own streets are laid on its new half, and none inside its rings with the switch off;
  - they are the streets the cell lays owned whole (166 of 166, the cells opened alike);
  - more is built on the half bought alone with them than without;
  - every box that stood still stands;
  - the network is joined with every check holding;
  - the plan is a pure function.

  Run with the switch off, (a) and (c) went red.
- **MapCheck 2c** (three new): the first block cuts cells open before it and cells that opened after; its plots in the open cells are built on at least 3/4 as much as in the others with the streets, and not without them; the full district places more of its houses with them. The second block lies wholly in cells opened after it (0 plots in open cells), so it has no share check, as printed.
- **What moved in PlanCheck's other sections** (the played city buys inside open cells):
  - section 6: 1,792 of 2,512 placed against 1,632; 2,028 street plots against 1,103, of them 924 track against 182; 3 beyond reach against 6;
  - the FW11 line above;
  - 13 (f)'s latticeOnly count, 179 against 188;
  - the curves, 33 against 21;
  - section 17's MapCheck 1 city lines.

  Every check of them holds.
- **`--pick`** (14 harnesses). MapCheck's reds are 2d's two, main's recorded ones; LongPlaytest red as above, its cause DM2's class, not this batch's to mend (Jerus, 2026-11-04).
- **Timing.** MapCheck's timing lines are left to the gate, beside main: FX9 does not touch a month's reconcile, and its plans' time over the whole playtest was 68 s against 68 s.

## 5. For his eye

In a city that buys land beside a built district:
- the bought ground inside a cell takes that cell's own streets, its comb, squares or crescent run on;
- the streets are drawn as track until the city's roads are paved;
- nothing standing moves;
- the new ground fills from the next month.

## 6. The slices

Two Sonnet 5.5 slices at xhigh, each diff reviewed as a stranger's:
- **A:** DistrictPlan's `cellsOnGrown`, the factoring and PlanCheck 12;
- **B:** MapCheck 2c.

Claude did the rest:
- the design;
- the review's mends: an unowned plot as a cut, so the joins along it happen; PlanCheck 12 (c)'s half-alone fixture, the first margin 137 against 136; `pruneApart()`'s main network, in place of the slice's walk that took the frame's edge for a join;
- the probes in `docs/queue/FX9/`: the month-by-month counts, the swap at m1209, the corner's pieces and way, PlanCheck 6's wall, the rim on the kept m4000 city;
- the playtests at 2,400 and 4,000 months on both builds.

## ★ calls

- **★1: LongPlaytest's red at the gate, DM2's class.** At 2,400 months: "a building on the map moved" at m1209, 20 boxes, a wall FW11 kept clear and a departure cured. *Recommended:* pass it by name as DM2's (queued), as Jerus's 2026-11-04 rule has it: FX9 adds no move of its own, and over 4,000 months it moves boxes in fewer month pairs than main. The other way: hold FX9 until DM2 ships.

# MB1 - big-city map bands ({next}, 2026-11-09)

Session window: W=150000 (orchestrator's env; compacts near 118k).

## Decisions

- **The rank, not the area** (2026-11-09). A district's band comes from a rank kept per district:
  its ground's first purchase (the smallest holding id over its square; CityLand's purchases are
  numbered in the order bought, and saved), the founding ground first, ties by DISTRICT_ORDER. The
  chain walks the districts in (band, DISTRICT_ORDER). A new district's first purchase is the newest,
  so it sorts last and joins the last band: no cut before it moves. Why not a cut by area: a fixed
  square would cut a city of 128 districts or fewer - Jerus's 93 - into several bands, seams where
  today it is one band; the rank leaves any city of one band exactly as it is drawn now. The rank is
  worked out from the purchases at every sort, never saved (as the harbour is).
- The placement's order (`districts`, `index`, DISTRICT_ORDER, the cursors) is left alone; the chain
  gets its own order beside it.

## The design (decided 2026-11-09, before the slice)

- **The chain's own order.** `sortDistricts()` keeps `districts` and `index`
  in `DISTRICT_ORDER` (the placement reads them) and builds beside them a list
  `chainOrder`: the districts sorted by (rank, `DISTRICT_ORDER`), cut into bands of
  `CHAIN_BAND`, each band re-sorted by `DISTRICT_ORDER`; `d.chainIndex` is a
  district's place in it. Every line that walked a band as
  `[b*CHAIN_BAND, ...)` of `districts` walks it of `chainOrder`. A city of 128
  districts or fewer has one band, so `chainOrder` equals `districts` and its
  drawing is today's to the box.
- **The rank** is the smallest holding owning a plot of the district's square
  (`LandGrid.firstHolding`, a quadtree walk like `holdingsRec`), folded with
  the smallest holding of a site tallied in it (`SiteIndex.tally`), so a
  district of water that joins for a site ranks by its site; neither gives
  `Integer.MAX_VALUE`. Purchases only append and fill never re-owns a plot, so
  a district's rank never changes once it is on the map, and a district bought
  ahead takes the newest rank: it joins the last band, and no band before it
  changes a member. Worked out at every sort, never saved.
- **The limit:** a district whose square was owned before only where there is
  no room (sea, a slope; not masked) was not on the map, and joins, when a
  later purchase gives it room, at the older rank, shifting the later bands'
  cuts by one, as every purchase did before. Ranking by the plots with room
  would need a look at each of the square's 65,536 plots; left.
- **Cached, so a sort costs no walk.** `d.groundFirst` keeps the ground's
  rank once found (a 10B city has ~29k districts, a quadtree walk each at every
  sort would weigh on MapCheck 5's budgets); it never falls on the same land
  (purchases only append; `rebind` copies the land field for field), so it is
  never reset. `d.siteFirst` is kept by `tallySites` beside the count, reset by
  its full rebuild. `d.chainRank` is the rank the chain was cut by; a tally
  that moves a rank without adding a district calls `rebuildChain()` (an O(n)
  look). `rebuildChain()` ends in `bandsStale()`: `checkBand` returns early at
  the same plan epoch, so a band whose members changed is checked again.
- **`chainTo` takes a chain index**, its callers `d.chainIndex`; `index`
  stays wherever it is the placement's order (the cursors, `DISTRICT_ORDER`).
- **Test hooks:** `CityMap.chainBand` (the band's size, `CHAIN_BAND` in the
  game) and `CityMap.chainByOrder` (the old rule), set only by MapCheck 2i.
- **The seam goes to the first in the chain** (2026-11-09, in the review of
  slice a). `planInputHere` gives a seam to the first of the districts sharing
  it; on main "first" was `DISTRICT_ORDER`, which was the chain's order. A
  district bought ahead is `DISTRICT_ORDER`-earlier than its neighbours, so a
  neighbour in another band would lose its seams and its boxes would move. The
  test is `chainIndex` now: still one surfacer per seam, the same as before
  within a band and in a city of one band, and a new district, last in the
  chain, takes no seam from a neighbour. `earlierOf` keeps `DISTRICT_ORDER`:
  it is asked only within a band, where the two orders agree.
- `rankOf` needed no cast: `baseDX`/`baseDY` are `long`.
- **Two slices:** a, the model (CityMap, LandGrid, two MapCheck lines that
  read a band by index); b, MapCheck 2i, after 2g's fixture: small bands, a
  district bought ahead, the old rule moving other bands' boxes, the new
  moving none outside the joining band.

## What the checks found

- **MapCheck 2i** (`bandsHoldOnPurchase`, after 2g; slice b) builds 2g's `Rim` with
  `CityMap.chainBand` 2 (four districts make two bands, the bought fifth a third) and buys the
  dry block nearest the site in a district the map lacks, once under each rule. The old rule
  (`chainByOrder`, bands cut of the map's order): 2 of the 4 districts changed band and 2,885
  of 3,478 boxes in the other bands moved, the fault MF1-B named. The new rule: the bought
  district sorts at chain place 4, in band 2 (the last); no district changed band; 0 of 4,938
  boxes in the other bands moved. A city of one band keeps the map's order (`chainOrder`
  equals `districts`, every `chainIndex == index`). MapCheck's only FAILs are main's 4 yard
  lines (gate.txt).
- **The ensemble is skipped.** The change is the map's plan alone: nothing in the model reads
  the map (`CityMap`, CLAUDE.md), so the month's trace cannot move.
- **MapCheck 5's month at 5B and 10B against main, on the same load: no budget moved.** Run side by
  side (both JVMs at once) the first bounded month after the spurt read 5B 7.43 and 10B 5.80 ms on
  MB1 against main's 2.76 and 2.86, and section 18 (the FX side, 19 ms) failed on both: the load,
  not the change. Run alone, alternated (`MapCheck.cost` on main's classes and the branch's, five
  rounds each, the branch first in three), every run passed section 5 (fails 0), in ms of the wall
  clock, the mean of five:

  | | main 5B | MB1 5B | main 10B | MB1 10B |
  |---|---|---|---|---|
  | the bounded months' worst | 2.74 (2.61-2.86) | 2.89 (2.73-3.31) | 2.95 (2.47-3.97) | 3.22 (2.65-3.99) |
  | the year's mean month | 1.49 | 1.56 | 1.77 | 1.81 |
  | the spurt (+0.05%, no budget) | 9.85 | 11.11 | 25.74 | 26.45 |

  The ranges overlap; the budget is 5 ms. A month with no purchase never reaches the new code
  (`rebuildChain` runs only from `sortDistricts` and from a tally that moves a rank, both on a
  purchase or a rebuild); the spurt's ~0.7 ms more at 10B, inside its spread, would be a
  purchase's sort of ~29k districts.

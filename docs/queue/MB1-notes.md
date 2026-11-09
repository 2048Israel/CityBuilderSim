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
- **Two slices:** a, the model (CityMap, LandGrid, two MapCheck lines that
  read a band by index); b, MapCheck 2i, after 2g's fixture: small bands, a
  district bought ahead, the old rule moving other bands' boxes, the new
  moving none outside the joining band.

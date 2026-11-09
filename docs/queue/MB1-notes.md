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
  `chain`: the districts sorted by (rank, `DISTRICT_ORDER`), cut into bands of
  `CHAIN_BAND`, each band re-sorted by `DISTRICT_ORDER`; `d.chainIndex` is a
  district's place in it. Every line that walked a band as
  `[b*CHAIN_BAND, ...)` of `districts` walks it of `chain`. A city of 128
  districts or fewer has one band, so `chain` equals `districts` and its
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

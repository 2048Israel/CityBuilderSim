# HB1 - a city's ports bundled into one harbour ({next}, 2026-11-08)

The brief is `docs/queue/HB1.md`. Lane A of the night line of 2026-11-08, after MT1.

## 1. What picture 5 is (the brief's first step)

His Bergen autosave (`cbs-fixtures/bergen-pictures-2026-10-31/save`, 0.8.90, save
format 49) no longer loads on main (`OLDEST_LOADABLE_FORMAT` 51, BK2), so it was
read with the 0.8.90 tag's own classes, built apart in the scratchpad, through
`MapCheck.loadCopy()` (a temporary copy, never a slot). At month 2,413 the shore
holds **32 works, every one laid by MF1's harbour rule** (each quay
`HARBOUR_QUAY_PLOTS`, 2): 16 General Cargo Terminals, 12 Bulk Berths, 3 Bulk
Terminals and a Container Terminal, in **two harbours on one landmass**, 27 and 5.

So they are not many small terminals the model should not have: they are the
model's, gathered by MF1, and drawn apart. The first harbour's 27 works spread over
about 124 x 163 plots (3.7 x 4.9 km) round its first work, facing west (11), north
(7) and east (9), on both sides of the inlets. The cause is MF1's search order
(`CityMap.harbourSpot()`): the harbour's tiles nearest its *first* work's shore plot
first, and in the first tile with room its *first spot in plot order*. A tile (32
plots) holds one to three works inside its interior, so the harbour fills its 3 km
disc tile by tile, wherever a tile has shore, instead of growing along one quay.

## 2. The design

- **The bundle (CityMap).** A harbour's next work takes, of every spot its reach
  holds, the one whose shore plot is nearest the shore plot of one of the
  harbour's works (best first: the tiles in order of their least distance to the
  harbour's works, every spot of a tile weighed, stopping once no tile left can
  hold a nearer one). The quay grows from its ends along the shore. The reach
  (`HARBOUR_REACH`), the second harbour (`HARBOURS_MOST`, `HARBOUR_APART`), the
  first work's shore (`openHarbour()`) and one harbour set per landmass are MF1's,
  unchanged: so terminals on one island lean toward one harbour, and a second
  island or a full first harbour still has its own. `CityMap.bundleHarbour` is the
  test switch (false: MF1's order). Works laid before are never moved (H4): his
  Bergen keeps its 32 where they stand.
- **The buildings moved.** A shore work is fixed ground in its district's plan and
  dated in the district's journal by the month's witness, as a yard is (FX7), so
  the boxes it lands on move off it and no others, and the layout watch counts
  them "moved off a run", not "a building on the map moved". Nothing in the watch
  changes; MapCheck 9c holds it for a harbour work.
- **Ships alongside and the joined quay (drawing, from the detail level).**
  `MapFrame.DETAIL_FROM`, where the scale bar first reads 500 m (HB2's brief): from
  there in, a boat at its berth lies along its quay's face, its side to the wall,
  and a harbour's quays are drawn as one wall along the boxes' sea sides, joined
  across the gap between neighbours facing the same water. Below it, as now.
- **The sprite** is a call for Jerus with renders (the brief's item 3), not decided
  here: the batch draws the boxes as now.

## 3. The calls

(filled in as the batch goes)

## 4. The checks

(filled in as the batch goes)

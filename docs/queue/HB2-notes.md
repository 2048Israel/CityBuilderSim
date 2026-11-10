# HB2 - the harbour's detail: its stock, its rail and a crane ({next}, 2026-11-09)

WORKING NOTES (the build is in progress; rewritten when it is done). Brief and
Jerus's picks: [HB2.md](HB2.md). Reference look: `docs/queue/HB2/RenderHB2.java`
(1a, 2a, 3a).

## The design, as decided before the slices

- **The yard is a picture per shore work, not tile paint.** A new toolkit-free
  class `HarbourYard` paints a work's box as ARGB at 8 px a plot (HB1's look
  (b), with 1a's stock at a level in steps, 2a's quay tracks and wagons as rail
  bits), seeded by the work as the render seeds it. `MapView` keeps one image a
  (work, level, rail) and draws it over the box from `MapFrame.DETAIL_FROM` in.
  Why not the tile: the tile draws from 3.2 px a plot and Jerus's rule is detail
  from 3.6 (the 500 m bar); a level step would dirty tiles every month; the
  render was drawn at 8 px a plot and the L0 tile is 4 below zoom 6.
- **The level** is `Ports.busy(kind, berths)`: the kind's sea tonnes both ways
  the month billed over what its standing terminals handle a month, 0 to 1.
  Nothing new is saved. Tank farms have no level.
- **The rail is a real way of the railway, from behind.** `CityRuns` lays one
  QUAY SPUR a quay line (HB1's joined wall) from the main line to a plot two
  behind the land edge of one of the line's boxes, at the box's first or last
  column: `wayClear` ground as a siding's, within `QUAY_REACH`, after the
  model's sidings, yards and branches, on no model track (FX5's precedent: a
  station's line is grown whatever the model's track). From behind, not from the
  flank: `CityMap.shoreFits` refuses a box on a run's plot, so a spur along the
  shore would take the next terminal's place in the bundle. Its arm has a
  corridor of its own the net never lists (`QUAY`), so `sidings()`,
  `sidingsLaid()`, the loading shed and the sidecar's format are untouched;
  `trackSurplus` leaves its plots out. The ends are handed by `CityMap.layRuns`
  each month and hashed into the rail's stamp only when there are some.
- **What the screen draws of the rail** comes from a pure `QuayRail`: which
  works are on a railed line and with which bits (track open to a neighbour low
  or high, the lead up the first or last column, a train), and the verge cells
  between boxes and behind the entry box, each a plot of track.
- **A train stands** on a railed terminal's quay track while its kind was busy
  this month (level above 0).
- **The crane** is a pure `QuayCrane`: for a boat at its quay, at share p of its
  time there, the line from ship to the apron and the box on it (dry bulk a grab
  of ore, a tanker two still arms), two moves a call. `MapView` strokes it from
  `DETAIL_FROM` in.

## Slices

A `HarbourYard`, `Ports.busy`, PortCheck 11 - B the quay spur (`CityShore`
quay lines, `QuayRail`, `CityRuns`, `CityMap`, `QuayRailCheck`) - C `QuayCrane`,
PortCheck 12, and the screen (`MapView`).

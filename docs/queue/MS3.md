# MS3 - the slanted ground: the near map smeared into diagonal streaks away from the city

## Jerus, 2026-10-26 (the requirement)

After MS2 (0.8.55), asked whether the map was done:

> ya, far side of the map has another issue well mid far, basiclaly it ends
> up wierdly slanted

He sent two screenshots, both at the 2 km scale, in a new city at Happy
Valley - Goose Bay (month 3):

- `MS3-slanted.webp`: the hover reads "Lake or river · 42 km north of Happy
  Valley - Goose Bay · Canadian Shield". Every patch of cover is drawn as a
  thin diagonal streak, lower left to upper right. The lake's shores are
  sawtoothed along the same diagonal, and the small lakes are slivers on it.
- `MS3-normal.webp`: ground drawn as it should be at that scale. The patches
  are round, and the lakes and rivers have their real shapes.

Then, on where it goes: TM3, MS3, then MP1 ("yes the rec option").

## What MS3 does

1. **Find where the slant starts and why, before any fix.** Use the camera
   state and distance from the city where the streaks appear, the tier, the
   stride, and which path draws it: the plane's own tiles, MS2's plane nodes
   through the globe's camera (`GlobeFrame`, `MapTiles.nodeTerrain()`), or
   the far rule.
   Suspects:
   - the turn from the plane's up to north over the 50 km past the reach
     (`MS2-notes.md` §1);
   - the plane's equal-area square stretching far from its centre;
   - a node's pixel lookup (`u`, `v`, `perSpan`) mixing its axes or reading
     the wrong level.
   42 km is well inside the 300 km reach, so the plane's own drawing is a
   suspect too.
2. **Fix it at its cause.** The ground at every distance looks as it does in
   `MS3-normal.webp`: round patches and lakes in their real shapes, with no
   visible seam where one way of drawing hands over to another.
3. **A harness section whose fixture causes it.** Measure the streak as a
   number at the place and scale where it showed, for example the patches'
   elongation along one axis against the cells' own, or a lake's outline
   against the data's. Assert it within a dial before and after the
   hand-overs.
4. **The eye check:** the screen-check session, or Jerus, at Goose Bay at
   2 km, near and mid-far, before and after.

## Scope

- The map's drawing: `ui/MapView`, `GlobeFrame`, `MapTiles`, `TilePainter`,
  `TileRaster`, `CityMap`. The model is untouched, so the trace is
  byte-identical and there is no ensemble.
- After TM3. Before MP1, so that MP1 measures the final drawing. Both touch
  the same files, so they run one at a time.

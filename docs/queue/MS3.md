# MS3 - the slanted ground: the near map smeared into diagonal streaks away from the city

## Jerus, 2026-10-26 (the requirement)

After MS2 (0.8.55), asked whether the map was done:

> ya, far side of the map has another issue well mid far, basiclaly it ends
> up wierdly slanted

He sent two screenshots, both at the 2 km scale, in a new city (month 3).
He then added: "sorry in that run location was signapore, so it was outside
the range". The city is at Singapore, and the slanted view is at Goose Bay,
Labrador:

- `MS3-slanted.webp`: the hover reads "Lake or river · 42 km north of Happy
  Valley - Goose Bay · Canadian Shield". Every patch of cover is drawn as a
  thin diagonal streak, lower left to upper right. The lake's shores are
  sawtoothed along the same diagonal, and the small lakes are slivers on it.
- `MS3-normal.webp`: ground drawn as it should be at that scale. The patches
  are round, and the lakes and rivers have their real shapes.

Then, on where it goes: TM3, MS3, then MP1 ("yes the rec option").

## The likely cause (to be proved first, not assumed)

The plane is the Lambert azimuthal equal-area projection about the city
(`GlobeFrame`: `k = R sqrt(2 / (1 + up))`). Goose Bay lies about 13,800 km
from Singapore, 124 degrees of arc. On the plane that is 2R sin(62 deg),
about 11,250 km from the centre: inside the square's half side of 11,305 km
(`World.SIDE`, 22,610 km). So MS2 draws it from the plane's own ground. At
124 degrees the projection keeps area but not shape. Ground is stretched
about 2.13 times across the line to the city and squashed to about 0.47
along it, a 4.5 to 1 distortion. The plane's 30 m plots there each stand for
a long, thin strip of the Earth. Drawn back through the globe's camera, the
cover's patches become streaks across the line to the city. That matches
the screenshot.

## What MS3 does

1. **Find where the slant starts and why, before any fix.** Confirm or refute
   the cause above. Measure the patches' elongation against the
   projection's shape distortion along a line from the city out to the
   square's edge, in a Singapore city. Use the camera
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
2. **Fix it at its cause.** If the distortion is the cause, the likely way
   is this: past a dial of shape distortion (say 1.5 to 1), draw the near
   view from the Earth file's cells directly through the globe's camera, not
   from the plane. The same way then covers MS2's leftover, the ground off
   the square that still uses the old far rule, so the whole globe away from
   the city is drawn by one rule. Design it in the notes with a
   recommendation. The ground at every distance looks as it does in
   `MS3-normal.webp`: round patches and lakes in their real shapes, with no
   visible seam where one way of drawing hands over to another.
3. **A harness section whose fixture causes it.** Measure the streak as a
   number at the place and scale where it showed, for example the patches'
   elongation along one axis against the cells' own, or a lake's outline
   against the data's. Assert it within a dial before and after the
   hand-overs.
4. **The eye check:** the screen-check session, or Jerus, in a Singapore
   city at 2 km: Goose Bay, and points between the reach and the square's
   edge, before and after.

## Scope

- The map's drawing: `ui/MapView`, `GlobeFrame`, `MapTiles`, `TilePainter`,
  `TileRaster`, `CityMap`. The model is untouched, so the trace is
  byte-identical and there is no ensemble.
- After TM3. Before MP1, so that MP1 measures the final drawing. Both touch
  the same files, so they run one at a time.

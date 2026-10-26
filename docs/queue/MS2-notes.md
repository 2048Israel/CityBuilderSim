# MS2 - the near view drawn the same past the reach ({next}, 2026-10-26)

The brief: [MS2.md](MS2.md), Jerus's words on 0.8.52 and his two screenshots beside it (`MS2-near.webp` inside the
reach, `MS2-far.webp` a few km past it, both at the 2 km scale north of Baddeck).

## 1. The design

### The cause

Closer than the handoff (0.48 km a pixel) the view is the city's plane within the reach (300 km, or 50 km round the
city's ground and offers) and, since MS1, the GROUND tier past it - drawn by the far rule through the globe's camera
alone (`MapView.farRuleOnly()`). The two rules differ in exactly what the screenshots show:

- the plane's ground comes from its own camera on the world's plots (`EarthTerrain.Plane`): each class ranked through
  value noise at the plot's place, so forest, grass and slope come in smooth patches, and a small lake is a disc frayed
  by its own noise;
- the far rule through the globe (`EarthTerrain.Globe`) has no plot under a pixel: its noise coordinates are nought and
  each pixel draws its class by a dither, so the same shares come out as speckle, and a small lake is a perfect disc.

The legend follows the tier, so past the reach it is the globe's short one; and the hover reads the globe's codes.

### What the plane reaches

The plane is the world's square, 753,664 plots a side (22,600 km) round the city on Lambert's equal-area projection.
It holds the planet out to about 13,900 km from the city along its axes and to the antipode along its diagonals; past
that the square is off the planet. Its plots are square near the city and stretched far from it (2.5% at 2,000 km,
13% at 4,500 km - Europe from Cape Breton), the projection's own anisotropy.

### The ways weighed

1. **The plane's own far nodes at the near scales, through the camera (recommended).** The GROUND tier already draws the
   plane's far nodes pixel by pixel through the globe's camera near the city (`GlobeFrame.ground()`, `globeNodes()`),
   at the stride the scale asks (`MapFrame.nodePlotsAt()`), down to the handoff's 16 plots a pixel. Closer than the
   handoff, past the reach, draw the same far nodes at the near strides (8, 4, 2, 1 plots a pixel - the plane's own
   camera gives every stride, `MapTiles.nodeTerrain()`), wherever the square holds the ground, and the far rule only
   off the square. In play: the ground past 300 km is the plane's ground - the same patches, the same lakes, the same
   coast and rivers - laid through the globe's camera, which turns from the plane's up to north over the 50 km past the
   reach as it does today; the legend is the plane's at every near scale; the hover says what the plane says.
2. **The plane itself past the reach, under a canvas transform.** Draw the plane's whole map (tiles, overlays) with the
   affine that takes the plane to the globe's view at the view's centre. Reuses everything, but it is the projection
   to first order only (its error grows with distance and with the view's width), turns the labels with it, and the
   plane's tiles in view must be found on a turned footprint.
3. **The far rule refined.** Give the globe's camera plot-like noise in a frame of its own and frayed lakes. It would
   be smooth everywhere, but a different pattern from the plane's: a seam at the reach, which is what the brief rules
   out.

Way 1 is taken (BP1: the recommendation, named here; Jerus can overturn it). It changes no rule of the model's: the
plane's camera already draws every stride, and only the screen's choice of which camera draws past the reach moves.

### Its limits, said plainly

- Off the world's square - less than the fifth of the planet farthest from the city - the near view keeps the far rule.
- Far from the city the plane's plots are stretched as the projection stretches them, so its patches and lakes are too
  (13% at 4,500 km); near the reach, where Jerus looked, they are square to a twentieth of a percent.

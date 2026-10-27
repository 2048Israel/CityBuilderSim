# MS2 - the near view drawn the same past the reach (0.8.55, 2026-10-26)

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

## 2. As built

One slice, written by a Sonnet 5.5 agent at xhigh and reviewed by the Opus batch session.

- **GlobeFrame**: `planeDrawnPastReach(kmPx)` - closer than the handoff the plane's nodes draw wherever the world's
  square holds the ground; `drawsHold()`, `planeHolds()` and `ground()` follow it (and `ground()` now checks the
  square), so the hover and a click take the plane's answer there; `legendTier()`; `planeDrawsKm()` unchanged at the
  handoff and wider.
- **MapView**: `farRuleOnly()` is gone. `globeNodes()` gives the plane's far nodes at the scale's stride (8, 4, 2 or 1
  plots a pixel) over the whole view the square holds, and the far rule's pieces only off it. The legend is the
  plane's at every near scale. The block lines are drawn through the camera (a line split wherever the camera bends
  it by more than a quarter pixel). WORKER drops a node asked for a view that has moved on and asks the rest from the
  view's middle out (`askNodes()`, a volatile view published from the FX thread).
- **MapTiles**: the plane's tiles tint the deposits' fields (`TileRaster.siteTint`) and the far nodes did not, so
  3,404 of 65,536 plots differed at a plot a pixel. `nodePixels(..., sites, ...)` tints them at one plot a pixel and
  MapView asks for it; the shorter forms keep `false`, so the minimap and MapCheck 18 are untouched. With it, 0 of
  65,536 plots differ between the plane's painted tiles and a node at a plot a pixel.

## 3. Checks and the trace

- **GlobeCheck 2c**, on the fixture's windows and on earth-2.bin north of Baddeck (views of 1,345 x 806 straddling the
  reach): at a plot a pixel - Jerus's 2 km scale and the closest - the classes drawn through the camera equal the
  plane's plots exactly on both sides of the reach; at 2, 4 and 8 plots a pixel 98.6 to 99.8% (the forest folded into
  grass). Speckle - pixels unlike both their neighbours across - is 0.04% drawn this way and 0.04% on the plane,
  against 2.7 to 15.2% for the far rule (9.4% at Baddeck). Twelve small lakes the far rule drew as perfect discs
  match the plane's frayed outline exactly (the disc matched it at 0.74 to 0.85). At the square's edge the far rule
  keeps only the pixels off it.
- **MapCheck**: the near-scale hover, legend and square-edge assertions, the colour check, a node's cost; two MS1
  assertions about the hover 2,000 km out now run at the handoff, since closer in the plane's words are the right ones.
- **Timing**: a frame over held nodes takes 27 to 35 ms (FRAME_MS 100); a node with its relief and colours 2 to 12 ms
  (SCREEN_MS 80); a pan reads 1 to 1.4 ms a node at Jerus's scale. MapCheck's timing run alone and hard on this PC
  this afternoon showed 11 red lines on the branch and 10 on main 0.8.53 in the same hour, all but one the same lines
  (the sidecar's write and read, the L0 screen, the month's change, the stamp, THE SCREEN); the eleventh, the month's
  change at 5 billion against 5 ms, is in code MS2 does not touch. The PC was slow that hour; the gate compares with
  main's own run.
- **The default playtest's trace** is byte-identical to main's (0.8.53) but for its run-time lines. **No ensemble.**
- **`./batch-check.sh --pick`**, earth-2.bin named: exit 3, main's recorded MapCheck red the only red.

## 4. Left for Jerus (MS2-C, the eye check on the PC)

Pan from the city out past 300 km at the 2 km scale, and on to a place a few thousand km away: the ground should look
the same all the way, lakes in their shapes, the full legend. Open: the expanded map makes an image per far node, and a
view turned far from the city asks for up to 2.5 times the nodes it shows (about 1.1 at the reach).

# MS3 - the slanted ground ({next}, 2026-10-27)

The brief: [MS3.md](MS3.md), Jerus's words on 0.8.55 and his two screenshots beside it (`MS3-slanted.webp`, Goose Bay
from a Singapore city at the 2 km scale; `MS3-normal.webp`, ground as it should look).

## 1. The cause, measured

**The cause is the plane's shape distortion (suspect 2), proved before any fix; the turn and the node's pixel lookup are
cleared.** GlobeCheck 2d reads a ground's streak as a number: the share of pixel pairs 4 px apart whose classes differ,
along four directions, fitted as an ellipse - E, how many times longer the patches are one way than the other, and the
direction they run (`streak()`). The measure is calibrated on fields of the rule's own noise of known stretch at the
same area, as an equal-area projection stretches round patches (`response()`): it reads no stretch as 1.03, and a
stretch low as it grows (2 to 1 as 1.72, 4 to 1 as 2.73: the offset saturates across the thin patches), so the cause
is read against `response(sigma)`, not sigma. Each view is 512 px at Jerus's scale, the city placed from the reach out
to 12,000 km from the spot (on the fixture's windows, due south of them; on earth-2.bin, Goose Bay - 42 km north of
Happy Valley, where his hover was - with the city on the great circle to Singapore and at Singapore itself). E / E_ref
is E over the same spot's with the city just past the reach, where the plane is true to a thousandth.

| city from Goose Bay | sigma = k/h | response | the plane's noise | the drawn ground | the streak's bearing | the city's |
|---:|---:|---:|---:|---:|---:|---:|
| 300 km (the reference) | 1.001 | 1.000 | 1.000 | 1.000 | - | 19.1 |
| 4,000 km | 1.105 | 1.045 | 1.058 | 1.035 | 3.1 | 19.1 |
| 6,000 km | 1.259 | 1.161 | 1.183 | 1.218 | 13.4 | 19.1 |
| 8,000 km | 1.527 | 1.365 | 1.344 | 1.343 | 14.8 | 19.1 |
| 10,000 km | 1.998 | 1.681 | 1.661 | 1.678 | 15.9 | 19.1 |
| 12,000 km | 2.889 | 2.199 | 2.071 | 1.976 | 17.7 | 19.1 |
| Singapore, 13,730 km (his city) | 4.460 | 2.826 | 2.460 | 2.391 | 16.6 | 19.1 |

- **The plane's noise under each pixel is stretched as Lambert's projection stretches it**: its E over its E at the
  reach is within 0.13 of `response(sigma)` at every placement (Goose Bay from Singapore the worst, 2.46 against
  2.83; the three windows within 0.075).
- **The drawn ground streaks with it, along the line to the city**: from sigma 1.5 the streak runs within 9.4
  degrees of the bearing to the city at all 13 such placements, and its E / E_ref rises at every step out. It
  stretches less than the noise, because the Earth's cells are drawn at each plot's true point and only the noise
  that frays them is the plane's: the lakes alone read 3.10 at Goose Bay from Singapore, the forest and grass less,
  all the classes 2.39. The brief had the streaks across the line to the city; they run along it, as the arithmetic
  says (a round patch on the plane is 1/h long along the line and h across on the planet). Jerus's screenshot agrees:
  north up, the streaks run lower left to upper right, NNE, and Singapore lies at 19 degrees from Goose Bay.
- **Not the pixel lookup**: the camera's drawing equals the plane's own plots under each pixel, 1.0000000 at every
  placement, no pixel without a node - the streak is in the plane's ground itself, not in how ground() reads it.
- **Not the turn**: the roll is 0, north up, at every placement from 2,000 km out (1.3 degrees at the reach), and a
  turn cannot stretch a pattern.
- On the fixture without the file the windows show it too (12,000 km: 1.83, 1.59 and 2.02 to 1 for Rotterdam, Paris
  and Perth), so the cloud's run causes it.

## 2. The design

### What has to change

Closer than the handoff the GROUND tier past the reach lays the city's plane's far nodes through the globe's camera
(MS2). The plane is Lambert's azimuthal equal-area projection about the city. At an angle c from the city it squashes
the ground along the line to the city by h = cos(c/2) and stretches it across by k = 1/cos(c/2), a shape distortion of
k/h = 2/(1 + cos c): 1.0005 at the reach, 1.03 at 2,000 km, 1.14 at 4,500 km, 1.5 at 7,840 km, 2 at 10,000 km, 4.5 at
Goose Bay from Singapore. The cover's patches, frayed coasts and lakes are value noise in the plane's plots
(`EarthTerrain.noiseAt()`), round on the plane, so on the planet they are ellipses k/h to 1, long along the line to the
city. The plane's nodes are sampled a plot a pixel, so the plots themselves (64 m along the line by 14 m across at
Goose Bay) saw the edges on the same diagonal. Nothing in the plane can be mended there: the model reads the plane's
plots, and no flat map of the sphere keeps both area and shape. The near view far from the city has to be drawn from
something else, and handed over to it with no seam.

### The ways weighed

1. **Charts, cached like the plane's nodes, with a noise on the sphere (recommended).** Past a dial of shape
   distortion the near view is drawn from charts: north-up orthographic pieces of the planet about fixed anchors on a
   10-degree lattice, the view's chart the anchor nearest its centre (within 1,300 km of every pixel at the plane's
   widest scale, so true in shape to 2%). A chart is cut into nodes as the plane is (128 px, at the scale's stride) and
   each node is the Earth's cells read at each pixel's own point - the far rule through the globe's camera, which
   already reads the cells at any point - at the scale's stride, the cover ranked through a noise on the sphere: 3D
   value noise at the point's unit vector, scaled to the plane noise's spread so the ranking table and the frays hold.
   The noise is the planet's, not the chart's, so a view that changes chart draws the same ground. Over a band just
   past the hand-over the sphere's noise is blended in from the plane's at each pixel's own plot, so the patches carry
   across the hand-over and morph over the band, with no line anywhere. The charts also draw the ground off the
   world's square (MS2's leftover, the far rule's dither), so at the plane's scales the whole globe past the hand-over
   is one rule. Nodes are kept and reused as the plane's are, so a pan costs what it costs on the plane.
2. **The globe's camera per view (the brief's likely way).** Past the dial, the far rule through the camera at the
   scale's stride, a piece of the screen at a time, as the ground off the square is drawn today. The fewest lines,
   but a piece belongs to one view: every pan is drawn again from nothing (about 120 pieces of the one worker a
   screen), and until it is, the carried picture and the planet's coarse texels - the "weird resolution while panning"
   MS2 was asked to end. MP1, after this batch, is making the map fast; this would hand it a slower map over two
   thirds of the planet.
3. **The plane mended in place.** Noise taken through a local correction of the plane's coordinates, or sub-plot
   strides where the plane squashes the ground. A coordinate correction cannot be integrated across the plane (its
   error grows with the distance from the city), sub-plot strides cost as much as the squash (4.5 times at Goose Bay,
   unbounded toward the antipode), and neither reaches the ground off the square.

Way 1 is taken (BP1: the recommendation, named here; Jerus can overturn it). It changes no rule of the model's: the
plane's tiles, its far nodes and the far rule are byte for byte as they were; only which drawing the near view takes
past the hand-over moves.

### The calls, each taken the recommended way

- ★ **Where the plane hands over: at 1.25 to 1** (`GlobeFrame.SHAPE_HANDOVER`; 53.1 degrees, 5,908 km). The brief
  suggested 1.5. A quarter is under what the eye reads in irregular patches - their own lengths vary more than that
  in `MS3-normal.webp` - and the plane's own ground, which the hover reads plot for plot, still reaches every
  neighbouring continent. At 1.5 (7,840 km) the patches would be half again as long one way as the other.
- ★ **The blend band: 100 km past it** (`SHAPE_BAND_KM`). The noise's widest octave is 480 m (the relief's 1.9 km):
  over 100 km the plane's pattern becomes the sphere's with no place to see it change, and the band is narrow enough
  that the double noise costs nothing anyone will meet.
- ★ **The anchors every 10 degrees** (`CHART_DEG`). The view takes the anchor nearest its centre; a pan across into
  another anchor's cell draws the view's chart nodes again (as a fresh view does) - about once in 1,000 km of
  panning, at the 2 km scale twenty screens.
- ★ **Past the hand-over the deposits' sites are not tinted.** The plane tints them at one plot a pixel; they are the
  plane's fields, distorted that far out, and none is ground the city can buy. A site the hand-over's circle crosses
  is cut there.
- ★ **The hover past the hand-over says what the chart draws**, as it says what the far rule drew off the square: the
  class and relief under the pointer, "Not the city's", the nearest place and the geology.

### Its limits, said plainly

- At the handoff and wider nothing moves: the plane's nodes out to `planeDrawsKm()` and the far rule's dither beyond,
  which has no streak (its noise is at its mean at those strides).
- Within the hand-over the plane's own patches keep up to a quarter of stretch, as the projection gives them.
- A river or a small lake the hand-over's circle crosses is drawn on the plane's side in the plane's plots and on the
  chart's in the planet's metric, so its edge steps where the circle crosses it, by up to a quarter of its half-width
  (k/h - 1 at the hand-over): 60 m on a river 500 m wide, nothing the eye finds at the 2 km scale; but about 2 to 5 km
  on an estuary-wide run of the data, as Hamilton Inlet's (40 km wide, north of Goose Bay). A probe on slice 2 found it
  where a seam view of GlobeCheck crossed that run: both rules paint the run, and agree to 0.994 where the plane is true;
  the step is the plane's distortion of its edge, not a disagreement of the rules.
- ★ **The step at an estuary-wide run is left** (the recommendation). The circle is 37,000 km round and runs that wide
  are a handful on the planet, so few cities' circles cross one; morphing the water's edges over the band as the noise
  is morphed would mean stroking every river in two metrics. A later batch can do it if Jerus sees one.

## 3. As built

Three slices, each written by a Sonnet 5.5 agent at xhigh and reviewed by the Opus batch session.

- **Slice 1, the measure (GlobeCheck 2d)**: `streak()`, `response()`, the placements along a line from the city out;
  section 1's table. Its first run had three asserts set before anything was measured; two were wrong on the
  arithmetic (the calibration stretched one axis where an equal-area projection stretches one and squashes the other,
  and the drawn ground was held to sigma though only its noise stretches), and were restated on what the measure
  reads, the dials set from the calibration and said so.
- **Slice 2, the model half.** `EarthTerrain`: the sphere's noise (`sphereNoise()`, 3D value noise at the plane
  noise's octaves, each octave's spread raised by `SPHERE_GAIN` = sqrt(35/26) to the 2D's, so the ranking table and the
  frays hold), `blendNoise()`, and the chart camera (`chart()`): the globe's orthographic camera about an anchor at the
  scale's stride, the forest ranked, every noise read the sphere's at the pixel's planet-fixed point, blended in from
  the plane's at the pixel's own plot within the band. Every other camera and every model path reads `noiseAt()` as
  before, byte for byte (the relief's stride is now divided as a double, which no octave of a power of two can tell
  apart). `GlobeFrame`: the dials, `distortion()`, `handoverUp()`, `handoverKm()`, `Chart` and `chartFor()` (the
  nearest anchor of the lattice), `chartHolds()`, `chartAt()`, `drawsHold()` and `planeHolds()` taking the
  hand-over, and `ground()` laying the chart's nodes past it through a second set of rows in its composed matrix.
  `MapTiles`: `chartTerrain()` and `chartPixels()` (a chart node's colours, as unowned ground with nothing on it).
- **Slice 3, the screen.** `MapView` keeps chart nodes as it keeps the plane's far nodes: the range a view wants
  sampled over the pixels the chart draws (`chartNodes()`), each node asked of the one worker once, from the middle
  out, dropped unread when the view has moved on (`chartsWanted`, as `nodesWanted`), made and coloured on the worker
  (nothing in a chart node reads the city), kept in a cache bounded as the far nodes are and cleared with the globe;
  `drawGlobePart()` hands them to `ground()`; `askPieces()` asks no far-rule piece where a chart draws; the hover's
  `drawnAt()` reads the chart node's class and relief, and `LandMap.globeHoverWords()` says the globe's words for it
  unchanged. `MapCheck` 13: the square's edge check restated (past the hand-over the globe's words on both sides) and
  one added at the hand-over's circle (the plane's words within it, the globe's past it). The review changed two
  things: the chart's key held its id in 12 bits, so the charts of rows 70 to 90 degrees south and north shared keys
  (now 20 bits); and a node's arrival found its box on the screen by sampling the whole view, about 7,500 points on
  the FX thread a node (now its corners, as the plane's nodes do).

## 4. Checks and the trace

- **GlobeCheck 2d, the fix**, on earth-2.bin and the fixture's windows: past the hand-over and its band the drawn
  ground's E / E_ref is within 0.056 of 1 at all 13 placements (Goose Bay from Singapore 0.98, where the plane's own
  plots read 2.39); within it the camera still draws the plane's own plots, 1.0000000. No seam: across the hand-over's
  circle the class changes over those of a twin drawn by one camera read 1.04 to 1.11 (SEAM_TOL 0.15), across the
  band's far edge 1.000 to 1.002, and a seam made on purpose (the sphere's noise switched on with no band) 2.5 to 3.5;
  the class shares either side within 0.009. The sphere's noise keeps the plane's spread (its standard deviation
  within 0.006 of it, its ranks' shares within 0.008). At the square's edge, past the hand-over, the charts draw both
  sides, no far-rule pixel. A chart node takes 10 ms (SCREEN_MS 80), a frame over held chart nodes 2.3 ms (FRAME_MS
  100), soft beside the other runs. The cause's asserts (section 1) read the plane's own plots and noise, as before.
- **MapCheck 13**: at the hand-over's circle, 166 points inside it said as the plane's plot and 162 past it as the
  globe's, none wrong; at the square's edge 336 of 336 the globe's words.
- **The default playtest's trace** is byte-identical to main's (52dd1fde, 0.8.57) in all eight CSVs and in its report
  but for its two run-time figures (50 s and 52 s). **No ensemble.**
- **`./batch-check.sh --pick`**, earth-2.bin named: exit 3 - 16 harnesses, main's recorded MapCheck red the only red;
  GlobeCheck and EarthTerrainCheck clean. One timing line SOFT, not counted: MapCheck's "a month's FX-side map work at
  5B", 58.8 ms against 58, wall clock beside the other runs, in the month's map work near the city, which MS3 does
  not touch; the gate times it alone. `--pick` asked for a threading review of MapView (a volatile, Platform.runLater):
  they are `chartsWanted` and the FX thread's store, `nodesWanted`'s pattern line for line. MapView is read by no
  harness: the eye check (5) is its check.

## 5. Left for Jerus (MS3-C, the eye check)

In a Singapore city (his own, or a new one), the expanded map at the 2 km scale:

- **Goose Bay**, 42 km north of Happy Valley-Goose Bay, where his screenshot was: round patches and lakes in their
  shapes, as `MS3-normal.webp` shows ground near a city, not streaks.
- **Lake Baikal**, which the hand-over's circle (5,908 km from Singapore) crosses at about 54 degrees north, a little
  north of Olkhon island: pan along the lake from Irkutsk (5,670 km) to Severobaykalsk (6,060 km). The plane draws its south end and the charts its north; there should be no
  line where one hands over to the other, only the same kind of ground.
- **Tokyo** (5,300 km, the plane's own ground, a little stretched as it always was) and **Quito** (near the
  antipode, off the world's square, where the far rule's speckle was drawn until now): ground like the rest.
- **A pan** at the 2 km scale past the hand-over: as smooth as near the city, the ground filling in node by node as it
  does on the plane, the hover saying "Not the city's" with the class under the pointer.

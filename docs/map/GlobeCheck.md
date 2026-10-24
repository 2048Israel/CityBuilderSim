# GlobeCheck.java - 802 lines · 26 methods · 17 constants · harnesses

`ham/citybuildersim/GlobeCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, GlobeFrame's view and its handoff to the city's plane, the far rule's three cameras, and the planet's picture with a frame drawn from it - on screen since 0.7.135 (batch E9), which MapCheck 13 holds.
> 
> WHY. The globe is drawn on a canvas, which cannot be drawn headless in
> the cloud loop, so its look is checked by eye on the PC; the half of it
> that is arithmetic is in the model, as MapFrame's is for the plane, and
> this holds what the eye cannot: that a zoom keeps the point under the
> pointer, that the swap from the globe to the plane cannot be seen where
> the study said, that the plane's far nodes are what they were before the
> camera came between them and the far rule, and that the planet's picture
> is the data's land.
> 
> What this has to prove (spec-globe 4's E8 row):
>   1. THE ORTHOGRAPHIC ROUND TRIP: a point on the disc goes to the planet
>      and back, and one within LIMB_DEG of the centre to the disc and back,
>      within ROUND_TRIP_M; the far side is refused; the centre is the
>      view's middle and north is the roll clockwise from up; north up it is
>      Snyder's textbook projection, the study's prototype's.
>   2. THE VIEW: a zoom keeps the point under the pointer within POINTER_PX
>      - wide, through the GROUND tier and on the plane - and across the
>      handoff within the parting; a drag keeps the point grabbed; Fit is the
>      whole planet facing the city, north up; the tiers change at
>      PICTURE_FROM_KM and HANDOFF_KM; the roll is nought wide, the plane's up
>      at the handoff and eased by notches between, and nought beyond the
>      reach (since 0.7.135 eased back over GlobeFrame.ROLL_EASE_KM past it,
>      so held at twice REACH_KM); the plane's up in closed form is EarthProj.forward()'s own;
>      away from the city the wheel stops at the handoff, and at the plane's
>      scales a pan stops at the reach, which a city's own ground extends.
>   3. THE HANDOFF: the plane and the globe facing the city part by under
>      half a pixel on the expanded map at HANDOFF_PLOTS, and by more at twice
>      it (the dial is the widest power of two that passes); at REACH_KM with
>      the roll eased by under REACH_PARTING_PX, north up by far more; the
>      study's tables printed beside its own measure.
>   4. THE PLANE'S CAMERA IS TODAY'S FAR NODES: every stride's node over the
>      fixture's four windows and the world's, and on the full file eight
>      spots' (when it is named), the CRC32 of 0.7.131's.
>   5. THE GLOBE'S CAMERA: at the handoff facing the city it draws the
>      plane's node but for the forest's dither, its forest within
>      SHARE_POINTS of the plane's and its rivers where the plane's are; its
>      dither cells at least a pixel tall and under two at every scale of the
>      GROUND tier, so a turn of TURN_PX flips no more than that share of the
>      dry pixels' forest.
>   6. THE PLANET'S PICTURE, the grid's camera: the same made twice and on
>      two threads as on one; its land per 1-degree cell the far layers'
>      within SHARE_POINTS in the mean over the cells with a coast and each
>      within its own rounding (the half-contour's most), the planet's within
>      EarthTerrainCheck.LAND_WITHIN; every texel's sea the far layers'
>      half-contour, its ice and desert their bits, its forest the data's
>      within SHARE_POINTS; ice, desert, forest and rivers drawn; its cost and
>      memory printed - on the fixture's bands, and the planet on the full
>      file, FIRST_ARCMIN then ARCMIN.
>   7. A FRAME of the expanded map from the picture in no more than FRAME_MS
>      of the main thread's CPU (Jerus's A28; MapCheck's clock), the wall
>      clock beside it, and on two threads.
> 
> The fixture (EarthCheck's) causes the camera's conditions: its windows
> hold coasts, rivers and small lakes and Fiji's the date line; its bands
> cross three continents' coasts. The full file's sections run where it is
> named (-Dcbs.earth) and say so in one line where it is not.

**Uses:** [GlobeFrame](GlobeFrame.md) (77), [World](World.md) (45), [EarthData](EarthData.md) (22), [GlobePicture](GlobePicture.md) (20), [EarthProj](EarthProj.md) (15), [EarthTerrainCheck](EarthTerrainCheck.md) (13), [MapTiles](MapTiles.md) (12), [EarthTerrain](EarthTerrain.md) (9), [TileRaster](TileRaster.md) (7), [EarthCheck](EarthCheck.md) (5), [MapCheck](MapCheck.md) (4), [MapFrame](MapFrame.md) (3), [Timing](Timing.md) (2)

## Sections

| line | section |
|---:|---|
| 163 | 1. THE ORTHOGRAPHIC PROJECTION |
| 215 | 2. THE VIEW |
| 386 | 3. THE HANDOFF |
| 503 | 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES |
| 554 | 5. THE GLOBE'S CAMERAS |
| 623 | 6. THE PLANET'S PICTURE |
| 771 | 7. A FRAME |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `GlobeCheck.POINTER_PX` | `1e-3` | A zoom keeps the point under the pointer under it to within this, in pixels: a thousandth (★E8) - the solver's rounding, nothing a player sees. |
| 80 | `GlobeCheck.ROUND_TRIP_M` | `1e-6` | The orthographic round trip holds a point to within this, in metres: a micrometre (★E8) - at the limb the inverse loses digits, so the planet's points are held within LIMB_DEG of the centre. |
| 83 | `GlobeCheck.LIMB_DEG` | `89` | ...a point on the planet within this many degrees of the view's centre: 89, the limb's last degree left out, where the disc squeezes 111 km of ground into 17 m. |
| 86 | `GlobeCheck.ROUNDS` | `100_000` | Random views and points the projection is tried on: 100,000. |
| 89 | `GlobeCheck.REACH_PARTING_PX` | `1.25` | At REACH_KM with the roll eased, the plane and the globe part by under this, in pixels: 1.25 (★E8) - the study's 1.16 px, measured with the globe scaled to the plane's mean scale there (globe-prototype.py), 1.21 as th... |
| 92 | `GlobeCheck.BEARINGS` | `{ 0, 45, 90, 135, 180 }` | The bearings a view away from the city is looked at, degrees: the study's five. |
| 95 | `GlobeCheck.TABLE_KM` | `{ 100, 300, 500, 1000 }` | The distances the study's table measured the swap at, km (spec-globe 3.3). |
| 98 | `GlobeCheck.CITIES` | `{ { 44.65, - 63.57 }, { 14.60, 120.98 } }` | The study's cities, its boards' (spec-globe 1): Halifax and Manila. |
| 101 | `GlobeCheck.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding globe's canvas, pixels: 560 x 270 (FoundingScreen's GLOBE_W and GLOBE_H; spec-globe 3.2's second column). |
| 104 | `GlobeCheck.TURN_PX` | `0.1` | A turn of the globe of this many pixels flips the forest on no more than this share of the dry pixels: a tenth (★E8) - the dither's cells are at least a pixel each way, so a point moving a tenth of a pixel crosses a c... |
| 107 | `GlobeCheck.FRAME_MS` | `100` | A frame of the expanded map from the planet's picture in no more than this of the main thread's CPU, ms: 100 (spec-globe 3.6 measured 96 to 102 ms on one cloud thread, 50 on two, and called that live). |
| 110 | `GlobeCheck.FRAME_ROUNDS` | `10` | Frames timed, the least taken, after as many to warm. |
| 113 | `GlobeCheck.FIXTURE_PRINTS` | `Map.of("Rotterdam", 0x5de8b72aL, "Perth", 0x2bd933ffL, "Paris", 0x7016955fL, ...` | The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera (probe E8Fingerprint on its build, 19f1cad). |
| 116 | `GlobeCheck.FULL_SPOTS` | `{ { 51.90625, 4.48125 }, { 44.65, - 63.57 }, { 14.60, 120.98 }, { 72, - 40 },...` | The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. |
| 119 | `GlobeCheck.FULL_PRINTS` | `{ 0x53186882L, 0xdcf6b91eL, 0x4b92b699L, 0x34df4490L, 0x663a5196L, 0x7e38f4f5...` | ...and their nodes' CRC32, as 0.7.131 drew them. |
| 123 | `GlobeCheck.out` | `System.out` | Where the harness prints: the console. |
| 131 | `GlobeCheck.R` | `EarthProj.RADIUS_M` | The planet's radius in metres (EarthProj.RADIUS_M), which the sections measure arcs and the disc by. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 121 | `static int fails` |  |
| 672 | `int mixed, past` |  |
| 673 | `double mean, worst, pictureLand, dataLand` |  |
| 674 | `double[] diffs` |  |
| 725 | `long texels, seaWrong, iceWrong, desertWrong, dry, forest` |  |
| 726 | `double forestData` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 729 | **type** `public class GlobeCheck` | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, GlobeFrame's view and its handoff to the city's plane, the far rule's t... |
| 125 | 4 | `static void check(String label, boolean ok)` |  |
| 133 | 29 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ORTHOGRAPHIC PROJECTION (lines 163-214)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 47 | `static void ortho()` |  |

### 2. THE VIEW (lines 215-385)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 8 | `static double[] pointer(Random r, GlobeFrame f)` |  |
| 229 | 4 | `static double offPx(GlobeFrame f, double[] ll, double sx, double sy)` | How far, in the view's pixels, (lat, lon) lies from screen point (sx, sy): null where it is not seen. |
| 235 | 6 | `static boolean northUpCan(double lat, double sx, double sy, double w, double h, double kmPx)` | Whether north up can put (lat) at screen point (sx, sy) of a wide view at kmPx: the spherical triangle of the pole, the centre and the point allows it - \|sin lat\| no more than the root of cos^2 c + sin^2 c cos^2 a, c ... |
| 242 | 129 | `static void view()` |  |
| 373 | 3 | `static double[] atReach(double[] city, double bearing)` | The point at the reach's edge from a city along a bearing: REACH_KM out, a hair inside it as a pan's stop leaves the view. |
| 378 | 7 | `static double numericRoll(EarthProj p, double lat, double lon)` | The plane's up at (lat, lon) against north, degrees clockwise, by a small step north through EarthProj.forward(). |

### 3. THE HANDOFF (lines 386-502)

| line | len | member | says |
|---:|---:|---|---|
| 391 | 14 | `static double parting(GlobeFrame f, int step)` | The most the view and the plane it hands to part, in pixels, every `step` pixels across it and its edges. |
| 407 | 26 | `static double studyParting(GlobeFrame f, double lat, double lon, double kmPx)` | The study's measure (globe-prototype.py's reach_px): the globe north up turned by the plane's up and scaled to the plane's mean scale against the plane round the view centre, px. |
| 434 | 53 | `static void handoff()` |  |
| 489 | 13 | `static double northUpParting(GlobeFrame f, double lat, double lon)` | The view at (lat, lon) north up against the plane, px. |

### 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES (lines 503-553)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 17 | `static long fingerprint(World.Key key, int from, boolean world)` | Every stride from `from` to the widest the expanded map draws, a far node aligned on the spot's and one centred on it; then, `world`, the whole world's nodes at the widest - their CRC32. |
| 526 | 27 | `static void planeCamera(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 5. THE GLOBE'S CAMERAS (lines 554-622)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 1 | `static int folded(byte b)` | A class with the forest folded into the dry ground it stands on, for comparing two dithers. |
| 561 | 61 | `static void cameras(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data)` |  |

### 6. THE PLANET'S PICTURE (lines 623-770)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 36 | `static GlobePicture picture(EarthData fixture, EarthData full)` |  |
| 665 | 4 | `static boolean freshNear(GlobePicture p, double lat, double lon)` | Whether the picture draws fresh water within a degree of (lat, lon). |
| 671 | 11 | **type** `static final class Land` | A picture's land per 1-degree cell against the far layers': cells with a coast, their mean and worst in points, how many past their own rounding, and the planet's land by area. |
| 676 | 5 | `public String toString()` _(in GlobeCheck.Land)_ |  |
| 683 | 39 | `static Land land(EarthData d, GlobePicture p)` |  |
| 724 | 13 | **type** `static final class Rule` | The far rule at each texel, read back from the data: sea, ice and desert by the rule, the forest's share. |
| 728 | 1 | `boolean ok()` _(in GlobeCheck.Rule)_ |  |
| 730 | 1 | `double forestPoints()` _(in GlobeCheck.Rule)_ |  |
| 732 | 4 | `public String toString()` _(in GlobeCheck.Rule)_ |  |
| 738 | 32 | `static Rule rule(EarthData d, GlobePicture p)` |  |

### 7. A FRAME (lines 771-802)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 27 | `static void frame(GlobePicture pic)` |  |


# GlobeCheck.java - 1,336 lines · 35 methods · 24 constants · harnesses

`ham/citybuildersim/GlobeCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

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
>      the reach stops nothing (MS1, Jerus 2026-10-26, superseding spec-globe
>      3.4 Q3's "away from the city the wheel stops at the handoff, and on
>      the plane a pan stops at the reach"): the wheel carries the view on
>      to the closest scale anywhere and a pan carries its centre past the
>      reach, which a city's own ground extends and which decides only the
>      picture closer than the handoff - section 2b.
>  2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1): far from the city, another
>      continent and across the antimeridian from it, the wheel reaches
>      leastKmPx() in the GROUND tier with the point under the pointer
>      staying under it; drags outward at a near scale carry the centre past
>      the reach - PLANE inside, GROUND past it, the point grabbed staying
>      under the pointer across the crossing; the plane's ground and the
>      GROUND tier's far rule agree over a patch at the reach's edge to a
>      measured share (the thousandth of section 5 at the handoff); a view
>      straddling longitude 180 is continuous, over Fiji's coast on the
>      fixture and Chukotka's on the full file.
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
> ... (25 more lines in the source)

**Uses:** [GlobeFrame](GlobeFrame.md) (146), [World](World.md) (89), [GlobePicture](GlobePicture.md) (52), [EarthData](EarthData.md) (33), [EarthProj](EarthProj.md) (18), [EarthTerrainCheck](EarthTerrainCheck.md) (17), [MapTiles](MapTiles.md) (16), [EarthTerrain](EarthTerrain.md) (13), [MapFrame](MapFrame.md) (11), [TileRaster](TileRaster.md) (8), [EarthCheck](EarthCheck.md) (5), [MapCheck](MapCheck.md) (4), [Timing](Timing.md) (2)

**Used by (1):** [EarthTerrainCheck](EarthTerrainCheck.md)

## Sections

| line | section |
|---:|---|
| 201 | 1. THE ORTHOGRAPHIC PROJECTION |
| 253 | 2. THE VIEW |
| 425 | 2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1) |
| 781 | 3. THE HANDOFF |
| 898 | 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES |
| 949 | 5. THE GLOBE'S CAMERAS |
| 1018 | 6. THE PLANET'S PICTURE |
| 1166 | 7. A FRAME |
| 1199 | 8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `GlobeCheck.POINTER_PX` | `1e-3` | A zoom keeps the point under the pointer under it to within this, in pixels: a thousandth (★E8) - the solver's rounding, nothing a player sees. |
| 107 | `GlobeCheck.ROUND_TRIP_M` | `1e-6` | The orthographic round trip holds a point to within this, in metres: a micrometre (★E8) - at the limb the inverse loses digits, so the planet's points are held within LIMB_DEG of the centre. |
| 110 | `GlobeCheck.LIMB_DEG` | `89` | ...a point on the planet within this many degrees of the view's centre: 89, the limb's last degree left out, where the disc squeezes 111 km of ground into 17 m. |
| 113 | `GlobeCheck.ROUNDS` | `100_000` | Random views and points the projection is tried on: 100,000. |
| 116 | `GlobeCheck.REACH_PARTING_PX` | `1.25` | At REACH_KM with the roll eased, the plane and the globe part by under this, in pixels: 1.25 (★E8) - the study's 1.16 px, measured with the globe scaled to the plane's mean scale there (globe-prototype.py), 1.21 as th... |
| 119 | `GlobeCheck.BEARINGS` | `{ 0, 45, 90, 135, 180 }` | The bearings a view away from the city is looked at, degrees: the study's five. |
| 122 | `GlobeCheck.TABLE_KM` | `{ 100, 300, 500, 1000 }` | The distances the study's table measured the swap at, km (spec-globe 3.3). |
| 125 | `GlobeCheck.CITIES` | `{ { 44.65, - 63.57 }, { 14.60, 120.98 } }` | The study's cities, its boards' (spec-globe 1): Halifax and Manila. |
| 128 | `GlobeCheck.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding globe's canvas, pixels: 560 x 270 (FoundingScreen's GLOBE_W and GLOBE_H; spec-globe 3.2's second column). |
| 131 | `GlobeCheck.TURN_PX` | `0.1` | A turn of the globe of this many pixels flips the forest on no more than this share of the dry pixels: a tenth (★E8) - the dither's cells are at least a pixel each way, so a point moving a tenth of a pixel crosses a c... |
| 134 | `GlobeCheck.FRAME_MS` | `100` | A frame of the expanded map from the planet's picture in no more than this of the main thread's CPU, ms: 100 (spec-globe 3.6 measured 96 to 102 ms on one cloud thread, 50 on two, and called that live). |
| 137 | `GlobeCheck.FRAME_ROUNDS` | `10` | Frames timed, the least taken, after as many to warm. |
| 145 | `GlobeCheck.FIXTURE_PRINTS` | `Map.of("Rotterdam", 0xed2ff708L, "Perth", 0x0b1d7f68L, "Paris", 0xc3979824L, ...` | The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera (probe E8Fingerprint on its build, 19f1cad), but for what the earth-2 fixtur... |
| 148 | `GlobeCheck.FULL_SPOTS` | `{ { 51.90625, 4.48125 }, { 44.65, - 63.57 }, { 14.60, 120.98 }, { 72, - 40 },...` | The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. |
| 155 | `GlobeCheck.FULL_PRINTS` | `{ 0xf291ea65L, 0x208e9728L, 0xd85a163cL, 0x734fdb04L, 0xf5f461adL, 0xbdc3b5b7...` | ...and their nodes' CRC32, as 0.7.131 drew them but for the shallow sea (ET1, 2026-10-24: World.SHALLOW is now a class the far nodes hold, so the figures moved). |
| 159 | `GlobeCheck.out` | `System.out` | Where the harness prints: the console. |
| 167 | `GlobeCheck.R` | `EarthProj.RADIUS_M` | The planet's radius in metres (EarthProj.RADIUS_M), which the sections measure arcs and the disc by. |
| 430 | `GlobeCheck.NEAR_KM` | `GlobeFrame.HANDOFF_KM / 4` | A near scale, km a pixel: a quarter of the handoff's, four plots a pixel (MS1's views closer than the handoff, on the plane and past its reach). |
| 433 | `GlobeCheck.STEP_PX` | `150` | The pixels a drag outward takes at a time in section 2b: 150 - at NEAR_KM, 18 km, so the reach is crossed in about seventeen. |
| 436 | `GlobeCheck.STEPS_MOST` | `100` | The most drags outward section 2b takes: 100, far past twice the reach at NEAR_KM. |
| 439 | `GlobeCheck.FAR_SPOTS` | `{ { 14.60, 120.98 }, { - 31.95, 115.86 }, { - 17.0, 179.9 }, { - 17.0, - 179....` | The spots section 2b zooms in on far from either city, {lat, lon}: Manila, Perth, and a spot each side of the antimeridian (the date line) at 17 degrees south and Manila's latitude. |
| 448 | `GlobeCheck.CROSSING_HANDOFF_SHARE` | `0.01` | The share of a patch's pixels whose class the plane and the GROUND tier's far rule may draw differently at the handoff, over a patch at the reach's edge: 1 percent (measured 0.15 to 0.65 percent over six patches - the... |
| 454 | `GlobeCheck.CROSSING_NEAR_SHARE` | `0.06` | ...and at NEAR_KM, where the plane draws a far node of four plots a pixel and the camera the far rule at its least stride, DITHER_STRIDE (16 plots), so a coast or a river parts by up to a pixel and four: 6 percent (me... |
| 1204 | `GlobeCheck.RELIEF_SIGMAS` | `4, RELIEF_ROUNDING = 0.03` | The picture's relief shares are the data's within this many binomial spreads of the dry texels drawn (a texel is a hash's draw against the 4' block's share, as a far node's pixel is) and a rounding of the slope layer'... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 157 | `static int fails` |  |
| 1067 | `int mixed, past` |  |
| 1068 | `double mean, worst, pictureLand, dataLand` |  |
| 1069 | `double[] diffs` |  |
| 1120 | `long texels, seaWrong, iceWrong, desertWrong, dry, forest` |  |
| 1121 | `double forestData` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 101 | 1236 | **type** `public class GlobeCheck` | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, GlobeFrame's view and its handoff to the city's plane, the far rule's t... |
| 161 | 4 | `static void check(String label, boolean ok)` |  |
| 169 | 31 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ORTHOGRAPHIC PROJECTION (lines 201-252)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 47 | `static void ortho()` |  |

### 2. THE VIEW (lines 253-424)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 8 | `static double[] pointer(Random r, GlobeFrame f)` |  |
| 267 | 4 | `static double offPx(GlobeFrame f, double[] ll, double sx, double sy)` | How far, in the view's pixels, (lat, lon) lies from screen point (sx, sy): null where it is not seen. |
| 273 | 6 | `static boolean northUpCan(double lat, double sx, double sy, double w, double h, double kmPx)` | Whether north up can put (lat) at screen point (sx, sy) of a wide view at kmPx: the spherical triangle of the pole, the centre and the point allows it - \|sin lat\| no more than the root of cos^2 c + sin^2 c cos^2 a, c ... |
| 280 | 130 | `static void view()` |  |
| 412 | 3 | `static double[] atReach(double[] city, double bearing)` | The point at the reach's edge from a city along a bearing: REACH_KM out, a hair inside it as a pan's stop leaves the view. |
| 417 | 7 | `static double numericRoll(EarthProj p, double lat, double lon)` | The plane's up at (lat, lon) against north, degrees clockwise, by a small step north through EarthProj.forward(). |

### 2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1) (lines 425-780)

| line | len | member | says |
|---:|---:|---|---|
| 457 | 6 | `static byte planeClass(World.Key key, long plots, double x, double y, Map<Long, byte[]> nodes)` | The class the city's plane draws at plot (x, y), at `plots` plots a pixel: the far node holding it (MapTiles.nodeTerrain()), its pixel under the plot. |
| 468 | 17 | `static double crossingShare(World.Key key, double cityLat, double cityLon, double lat, double lon, double kmPx, int n)` | The share of a n x n view's pixels whose class the city's plane draws differently from the GROUND tier's far rule through the globe's camera, the forest folded into the dry ground it stands on (its dither is a hash): ... |
| 487 | 5 | `static double mixed(byte[] piece, int w, int col, int h)` | How the share of land and of water among a column's pixels mixes: the lesser of the two, 0 for a column all one or the other. |
| 493 | 287 | `static void nearAnywhere(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 3. THE HANDOFF (lines 781-897)

| line | len | member | says |
|---:|---:|---|---|
| 786 | 14 | `static double parting(GlobeFrame f, int step)` | The most the view and the plane it hands to part, in pixels, every `step` pixels across it and its edges. |
| 802 | 26 | `static double studyParting(GlobeFrame f, double lat, double lon, double kmPx)` | The study's measure (globe-prototype.py's reach_px): the globe north up turned by the plane's up and scaled to the plane's mean scale against the plane round the view centre, px. |
| 829 | 53 | `static void handoff()` |  |
| 884 | 13 | `static double northUpParting(GlobeFrame f, double lat, double lon)` | The view at (lat, lon) north up against the plane, px. |

### 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES (lines 898-948)

| line | len | member | says |
|---:|---:|---|---|
| 903 | 17 | `static long fingerprint(World.Key key, int from, boolean world)` | Every stride from `from` to the widest the expanded map draws, a far node aligned on the spot's and one centred on it; then, `world`, the whole world's nodes at the widest - their CRC32. |
| 921 | 27 | `static void planeCamera(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 5. THE GLOBE'S CAMERAS (lines 949-1017)

| line | len | member | says |
|---:|---:|---|---|
| 954 | 1 | `static int folded(byte b)` | A class with the forest folded into the dry ground it stands on, for comparing two dithers. |
| 956 | 61 | `static void cameras(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data)` |  |

### 6. THE PLANET'S PICTURE (lines 1018-1165)

| line | len | member | says |
|---:|---:|---|---|
| 1022 | 36 | `static GlobePicture picture(EarthData fixture, EarthData full)` |  |
| 1060 | 4 | `static boolean freshNear(GlobePicture p, double lat, double lon)` | Whether the picture draws fresh water within a degree of (lat, lon). |
| 1066 | 11 | **type** `static final class Land` | A picture's land per 1-degree cell against the far layers': cells with a coast, their mean and worst in points, how many past their own rounding, and the planet's land by area. |
| 1071 | 5 | `public String toString()` _(in GlobeCheck.Land)_ |  |
| 1078 | 39 | `static Land land(EarthData d, GlobePicture p)` |  |
| 1119 | 13 | **type** `static final class Rule` | The far rule at each texel, read back from the data: sea, ice and desert by the rule, the forest's share. |
| 1123 | 1 | `boolean ok()` _(in GlobeCheck.Rule)_ |  |
| 1125 | 1 | `double forestPoints()` _(in GlobeCheck.Rule)_ |  |
| 1127 | 4 | `public String toString()` _(in GlobeCheck.Rule)_ |  |
| 1133 | 32 | `static Rule rule(EarthData d, GlobePicture p)` |  |

### 7. A FRAME (lines 1166-1198)

| line | len | member | says |
|---:|---:|---|---|
| 1170 | 28 | `static void frame(GlobePicture pic)` |  |

### 8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3) (lines 1199-1336)

| line | len | member | says |
|---:|---:|---|---|
| 1207 | 10 | `static long[] dryTexelsByRelief(GlobePicture p, int row0, int col0, int rows, int cols)` | The dry texels (not ice, not water) in a box of the picture, `rows` x `cols` of them from (row0 from the north, col0 from 180 W), counted by relief. |
| 1219 | 17 | `static double[] dataReliefShares(EarthData d, double lat0, double lat1, double lon0, double lon1)` | The data's relief shares over a box of 30" cells (the slope layer's bytes unpacked: flat is under foothill's share, foothill what is not steep, and so on): the mean over every cell. |
| 1238 | 9 | `static boolean sharesMatch(long[] counts, double[] data)` | Whether the texels' shares (counts) are the data's within RELIEF_SIGMAS spreads and the rounding, each relief. |
| 1248 | 4 | `static String sharesWords(long[] counts)` |  |
| 1253 | 83 | `static void reliefInPicture(EarthData fixture, String id, EarthData full, GlobePicture fullPicture)` |  |


# GlobeCheck.java - 1,693 lines · 47 methods · 39 constants · harnesses

`ham/citybuildersim/GlobeCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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
>  2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2, Jerus
>      2026-10-26: "look at the wierd resolution by just panning a few more
>      kms"): closer than the handoff the plane's far nodes are drawn through
>      the camera wherever the world's square holds the ground
>      (GlobeFrame.planeDrawnPastReach(), drawsHold(), planeHolds()) - the
>      pixels the square holds and no others, at its edge - and the legend is
>      the plane's; over views straddling the reach on the fixture's three
>      windows and, named, Cape Breton north of Baddeck, at his scale, 2, 4 and
>      8 plots a pixel and the closest, the classes the nodes give are the
>      plane's own plots' (exactly at one plot a pixel, within NEAR_PAST_FOLDED
>      and NEAR_PAST_CLASSES at 2 to 8) on both sides of the reach, no more
>      dithered than the plane's own (the far rule's speckle printed beside
>      them), and a small lake is the plane's frayed lake, not the far rule's
>      disc; a frame of the camera over the nodes held within FRAME_MS.
>   3. THE HANDOFF: the plane and the globe facing the city part by under
>      half a pixel on the expanded map at HANDOFF_PLOTS, and by more at twice
>      it (the dial is the widest power of two that passes); at REACH_KM with
>      the roll eased by under REACH_PARTING_PX, north up by far more; the
>      study's tables printed beside its own measure.
> ... (39 more lines in the source)

**Uses:** [GlobeFrame](GlobeFrame.md) (189), [World](World.md) (114), [GlobePicture](GlobePicture.md) (52), [EarthData](EarthData.md) (40), [EarthTerrainCheck](EarthTerrainCheck.md) (23), [MapTiles](MapTiles.md) (22), [MapFrame](MapFrame.md) (22), [EarthProj](EarthProj.md) (20), [EarthTerrain](EarthTerrain.md) (14), [MapCheck](MapCheck.md) (8), [TileRaster](TileRaster.md) (8), [EarthCheck](EarthCheck.md) (5), [Timing](Timing.md) (3)

**Used by (1):** [EarthTerrainCheck](EarthTerrainCheck.md)

## Sections

| line | section |
|---:|---|
| 216 | 1. THE ORTHOGRAPHIC PROJECTION |
| 268 | 2. THE VIEW |
| 440 | 2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1) |
| 796 | 2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2) |
| 1138 | 3. THE HANDOFF |
| 1255 | 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES |
| 1306 | 5. THE GLOBE'S CAMERAS |
| 1375 | 6. THE PLANET'S PICTURE |
| 1523 | 7. A FRAME |
| 1556 | 8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 118 | `GlobeCheck.POINTER_PX` | `1e-3` | A zoom keeps the point under the pointer under it to within this, in pixels: a thousandth (★E8) - the solver's rounding, nothing a player sees. |
| 121 | `GlobeCheck.ROUND_TRIP_M` | `1e-6` | The orthographic round trip holds a point to within this, in metres: a micrometre (★E8) - at the limb the inverse loses digits, so the planet's points are held within LIMB_DEG of the centre. |
| 124 | `GlobeCheck.LIMB_DEG` | `89` | ...a point on the planet within this many degrees of the view's centre: 89, the limb's last degree left out, where the disc squeezes 111 km of ground into 17 m. |
| 127 | `GlobeCheck.ROUNDS` | `100_000` | Random views and points the projection is tried on: 100,000. |
| 130 | `GlobeCheck.REACH_PARTING_PX` | `1.25` | At REACH_KM with the roll eased, the plane and the globe part by under this, in pixels: 1.25 (★E8) - the study's 1.16 px, measured with the globe scaled to the plane's mean scale there (globe-prototype.py), 1.21 as th... |
| 133 | `GlobeCheck.BEARINGS` | `{ 0, 45, 90, 135, 180 }` | The bearings a view away from the city is looked at, degrees: the study's five. |
| 136 | `GlobeCheck.TABLE_KM` | `{ 100, 300, 500, 1000 }` | The distances the study's table measured the swap at, km (spec-globe 3.3). |
| 139 | `GlobeCheck.CITIES` | `{ { 44.65, - 63.57 }, { 14.60, 120.98 } }` | The study's cities, its boards' (spec-globe 1): Halifax and Manila. |
| 142 | `GlobeCheck.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding globe's canvas, pixels: 560 x 270 (FoundingScreen's GLOBE_W and GLOBE_H; spec-globe 3.2's second column). |
| 145 | `GlobeCheck.TURN_PX` | `0.1` | A turn of the globe of this many pixels flips the forest on no more than this share of the dry pixels: a tenth (★E8) - the dither's cells are at least a pixel each way, so a point moving a tenth of a pixel crosses a c... |
| 148 | `GlobeCheck.FRAME_MS` | `100` | A frame of the expanded map from the planet's picture in no more than this of the main thread's CPU, ms: 100 (spec-globe 3.6 measured 96 to 102 ms on one cloud thread, 50 on two, and called that live). |
| 151 | `GlobeCheck.FRAME_ROUNDS` | `10` | Frames timed, the least taken, after as many to warm. |
| 159 | `GlobeCheck.FIXTURE_PRINTS` | `Map.of("Rotterdam", 0xed2ff708L, "Perth", 0x0b1d7f68L, "Paris", 0xc3979824L, ...` | The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera (probe E8Fingerprint on its build, 19f1cad), but for what the earth-2 fixtur... |
| 162 | `GlobeCheck.FULL_SPOTS` | `{ { 51.90625, 4.48125 }, { 44.65, - 63.57 }, { 14.60, 120.98 }, { 72, - 40 },...` | The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. |
| 169 | `GlobeCheck.FULL_PRINTS` | `{ 0xf291ea65L, 0x208e9728L, 0xd85a163cL, 0x734fdb04L, 0xf5f461adL, 0xbdc3b5b7...` | ...and their nodes' CRC32, as 0.7.131 drew them but for the shallow sea (ET1, 2026-10-24: World.SHALLOW is now a class the far nodes hold, so the figures moved). |
| 173 | `GlobeCheck.out` | `System.out` | Where the harness prints: the console. |
| 181 | `GlobeCheck.R` | `EarthProj.RADIUS_M` | The planet's radius in metres (EarthProj.RADIUS_M), which the sections measure arcs and the disc by. |
| 445 | `GlobeCheck.NEAR_KM` | `GlobeFrame.HANDOFF_KM / 4` | A near scale, km a pixel: a quarter of the handoff's, four plots a pixel (MS1's views closer than the handoff, on the plane and past its reach). |
| 448 | `GlobeCheck.STEP_PX` | `150` | The pixels a drag outward takes at a time in section 2b: 150 - at NEAR_KM, 18 km, so the reach is crossed in about seventeen. |
| 451 | `GlobeCheck.STEPS_MOST` | `100` | The most drags outward section 2b takes: 100, far past twice the reach at NEAR_KM. |
| 454 | `GlobeCheck.FAR_SPOTS` | `{ { 14.60, 120.98 }, { - 31.95, 115.86 }, { - 17.0, 179.9 }, { - 17.0, - 179....` | The spots section 2b zooms in on far from either city, {lat, lon}: Manila, Perth, and a spot each side of the antimeridian (the date line) at 17 degrees south and Manila's latitude. |
| 463 | `GlobeCheck.CROSSING_HANDOFF_SHARE` | `0.01` | The share of a patch's pixels whose class the plane and the GROUND tier's far rule may draw differently at the handoff, over a patch at the reach's edge: 1 percent (measured 0.15 to 0.65 percent over six patches - the... |
| 469 | `GlobeCheck.CROSSING_NEAR_SHARE` | `0.06` | ...and at NEAR_KM, where the plane draws a far node of four plots a pixel and the camera the far rule at its least stride, DITHER_STRIDE (16 plots), so a coast or a river parts by up to a pixel and four: 6 percent (me... |
| 801 | `GlobeCheck.JERUS_KM` | `2.0 / 120` | Jerus's near scale, km a pixel: 2 km in the scale bar's 120 px (ui/MapView.SCALE_BAR_PX), 1.8 px a plot - the view of his two screenshots. |
| 808 | `GlobeCheck.NEAR_PAST_SAME` | `1 - 1e-5` | At one plot a pixel (his scale and the closest) the classes the plane's nodes give through the camera are the plane's own, plot for plot: the share of a view's pixels, either side of the reach, whose class is the clas... |
| 816 | `GlobeCheck.NEAR_PAST_FOLDED` | `0.98` | At 2, 4 and 8 plots a pixel - where the plane draws a far node a pixel of which is one sample of its 2, 4 or 8 plots, and the camera reads that node - the share of pixels whose class is the class of the plot under the... |
| 819 | `GlobeCheck.NEAR_PAST_CLASSES` | `0.90` | ...and with the forest and the grass told apart: 90 percent (measured 91.6 to 98.3% on the fixture, 96.7 to 97.6% on the full file). |
| 822 | `GlobeCheck.NEAR_PAST_SPECKLE` | `0.001` | The share of pixels whose class differs from both horizontal neighbours the reprojected nodes may draw over the plane's own: a tenth of a percent - the camera reads the same node pixels the plane does, turned by a few... |
| 825 | `GlobeCheck.FAR_SPECKLE_AT_LEAST` | `0.05` | The far rule's speckle on a forested window the fixture must cause, for the comparison to mean anything: 5 percent of the pixels (measured 15.2% over Rotterdam and 11.9% over Paris at Jerus's scale; the plane's 0.04%). |
| 828 | `GlobeCheck.LAKE_IS_A_DISC` | `0.02` | A small lake the far rule draws as a disc is one whose pixels part from the equal-area disc about their centroid by no more than this share of their number: 2 percent (the far rule's discs measured 0.000 at 1.6 to 7.4... |
| 831 | `GlobeCheck.LAKE_IS_FRAYED` | `0.10` | ...and the plane's frayed one parts by at least this: 10 percent (its fray is BLOB_FRAY of its own noise: measured 0.10 to 0.23 over the lakes picked). |
| 834 | `GlobeCheck.LAKE_SAME_IOU` | `1 - 1e-3` | The reprojected lake is the plane's: their pixels' intersection over their union at one plot a pixel, within a thousandth of 1 (the share of section 2c's first figure). |
| 837 | `GlobeCheck.LAKE_DISC_IOU` | `0.9` | ...and the far rule's disc is not the plane's lake: at most this (measured 0.55 to 0.85 over the lakes picked). |
| 840 | `GlobeCheck.LAKE_LEAST_PLOTS` | `12` | Small lakes at least this many plots across the radius are tried (smaller are a few pixels at his scale), the least of them picked in each source: 12 plots (0.4 km2), four of them. |
| 841 | `GlobeCheck.LAKES_PICKED` | `4` |  |
| 844 | `GlobeCheck.LAKE_VIEW_PX` | `256` | A lake view's side, pixels: 256, the lake's radius a quarter of it. |
| 847 | `GlobeCheck.NEAR_SCALES` | `{ JERUS_KM, NEAR_KM / 2, NEAR_KM, NEAR_KM * 2, GlobeFrame.leastKmPx() }` | The scales the views straddling the reach are drawn at, km a pixel: Jerus's (1.8 px a plot), then 2, 4 and 8 plots a pixel - NEAR_KM among them - and the closest, 16 px a plot. |
| 857 | `GlobeCheck.NO_NODE` | `- 1` | What the camera draws of the plane's nodes (GlobeFrame.ground()) over a w x h view, a class a pixel (MapTiles.nodeTerrain()), NO_NODE where it drew none, OFF where it drew the far rule's place (beyond()) - and how man... |
| 1561 | `GlobeCheck.RELIEF_SIGMAS` | `4, RELIEF_ROUNDING = 0.03` | The picture's relief shares are the data's within this many binomial spreads of the dry texels drawn (a texel is a hash's draw against the 4' block's share, as a far node's pixel is) and a rounding of the slope layer'... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 171 | `static int fails` |  |
| 983 | `double same` | The least share of pixels at one plot a pixel whose class is the plot's; at 2 to 8 plots a pixel the least folded, and the least told apart; the largest the camera's speckle goes over the plane's; the far rule's most ... |
| 984 | `final List<String[]> lines` |  |
| 1424 | `int mixed, past` |  |
| 1425 | `double mean, worst, pictureLand, dataLand` |  |
| 1426 | `double[] diffs` |  |
| 1477 | `long texels, seaWrong, iceWrong, desertWrong, dry, forest` |  |
| 1478 | `double forestData` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 115 | 1579 | **type** `public class GlobeCheck` | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, GlobeFrame's view and its handoff to the city's plane, the far rule's t... |
| 175 | 4 | `static void check(String label, boolean ok)` |  |
| 183 | 32 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ORTHOGRAPHIC PROJECTION (lines 216-267)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 47 | `static void ortho()` |  |

### 2. THE VIEW (lines 268-439)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 8 | `static double[] pointer(Random r, GlobeFrame f)` |  |
| 282 | 4 | `static double offPx(GlobeFrame f, double[] ll, double sx, double sy)` | How far, in the view's pixels, (lat, lon) lies from screen point (sx, sy): null where it is not seen. |
| 288 | 6 | `static boolean northUpCan(double lat, double sx, double sy, double w, double h, double kmPx)` | Whether north up can put (lat) at screen point (sx, sy) of a wide view at kmPx: the spherical triangle of the pole, the centre and the point allows it - \|sin lat\| no more than the root of cos^2 c + sin^2 c cos^2 a, c ... |
| 295 | 130 | `static void view()` |  |
| 427 | 3 | `static double[] atReach(double[] city, double bearing)` | The point at the reach's edge from a city along a bearing: REACH_KM out, a hair inside it as a pan's stop leaves the view. |
| 432 | 7 | `static double numericRoll(EarthProj p, double lat, double lon)` | The plane's up at (lat, lon) against north, degrees clockwise, by a small step north through EarthProj.forward(). |

### 2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1) (lines 440-795)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 6 | `static byte planeClass(World.Key key, long plots, double x, double y, Map<Long, byte[]> nodes)` | The class the city's plane draws at plot (x, y), at `plots` plots a pixel: the far node holding it (MapTiles.nodeTerrain()), its pixel under the plot. |
| 483 | 17 | `static double crossingShare(World.Key key, double cityLat, double cityLon, double lat, double lon, double kmPx, int n)` | The share of a n x n view's pixels whose class the city's plane draws differently from the GROUND tier's far rule through the globe's camera, the forest folded into the dry ground it stands on (its dither is a hash): ... |
| 502 | 5 | `static double mixed(byte[] piece, int w, int col, int h)` | How the share of land and of water among a column's pixels mixes: the lesser of the two, 0 for a column all one or the other. |
| 508 | 287 | `static void nearAnywhere(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2) (lines 796-1137)

| line | len | member | says |
|---:|---:|---|---|
| 850 | 5 | `static int[] widen(byte[] classes)` | A class as an int in the nodes ground() reads: the node's classes widened, so the camera's pixels are classes. |
| 858 | 8 | `static int[] cameraClasses(World.Key key, GlobeFrame f, int w, int h, int[] missing)` |  |
| 868 | 8 | `static int[] plotClasses(World world, GlobeFrame f, int w, int h)` | The class of the plot under each pixel, as the plane's own tiles hold it (World.terrainAt(), stride one) - -2 off the planet. |
| 878 | 11 | `static int[] planeDraws(World.Key key, GlobeFrame f, int w, int h)` | What the plane draws over the same view at the same scale, its own frame unturned: each pixel the class of its far node's pixel at `plots` plots (planeClass()). |
| 891 | 3 | `static int[] farRuleDraws(World.Key key, GlobeFrame f, int w, int h)` | The far rule's classes through the camera over the same view (GlobeFrame.groundPiece(): what the GROUND tier drew past the reach before MS2). |
| 896 | 12 | `static double speckle(int[] a, int w, int h)` | The share of drawn pixels (a class, not NO_NODE or off the planet) whose class differs from both horizontal neighbours: the speckle of a dithered ground. |
| 910 | 10 | `static double agree(int[] a, int[] b, boolean fold, GlobeFrame f, int w, Boolean inside)` | The share of pixels drawn in both (a class each) whose classes agree, the forest and the desert folded into the grass if `fold`; `inside` restricts them to those within the reach (true), past it (false), or all (null). |
| 922 | 12 | `static double[] discParting(int[] a, int w, int h)` | A lake's pixels - fresh water in `a` - their count, and how far they part from the equal-area disc about their centroid, as a share of their count: 0 for a disc, 2 at the most. |
| 936 | 9 | `static double freshIoU(int[] a, int[] b)` | Two views' fresh water, intersection over union. |
| 947 | 1 | **type** `record LakePick(String where, double lat, double lon, double plots, double[] plane, double[] camera, double...` | One small lake picked: its place, its radius in plots, and what each rule drew of it. |
| 955 | 24 | `static List<LakePick> lakes(String where, EarthData data, World.Key key, double cityLat, double cityLon, double lat, double lon...` | The small lakes of the 1-degree tiles round (lat, lon) within `withinKm` of it, at least LAKE_LEAST_PLOTS across: each viewed through the camera at the scale that makes its radius a quarter of LAKE_VIEW_PX, the plane'... |
| 981 | 28 | **type** `static final class Near` | What the views straddling the reach drew, the worst of each figure (the lines print them). |
| 987 | 21 | `void view(String name, World.Key key, double[] city, double[] at, double km, int n, int m)` _(in GlobeCheck.Near)_ | The view centred at `at` at `km`, the city at `city` on the world `key`: its classes through the camera against the plane's own plots, the plane's own picture and the far rule's. |
| 1010 | 127 | `static void nearPastReach(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data, EarthData full)` |  |

### 3. THE HANDOFF (lines 1138-1254)

| line | len | member | says |
|---:|---:|---|---|
| 1143 | 14 | `static double parting(GlobeFrame f, int step)` | The most the view and the plane it hands to part, in pixels, every `step` pixels across it and its edges. |
| 1159 | 26 | `static double studyParting(GlobeFrame f, double lat, double lon, double kmPx)` | The study's measure (globe-prototype.py's reach_px): the globe north up turned by the plane's up and scaled to the plane's mean scale against the plane round the view centre, px. |
| 1186 | 53 | `static void handoff()` |  |
| 1241 | 13 | `static double northUpParting(GlobeFrame f, double lat, double lon)` | The view at (lat, lon) north up against the plane, px. |

### 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES (lines 1255-1305)

| line | len | member | says |
|---:|---:|---|---|
| 1260 | 17 | `static long fingerprint(World.Key key, int from, boolean world)` | Every stride from `from` to the widest the expanded map draws, a far node aligned on the spot's and one centred on it; then, `world`, the whole world's nodes at the widest - their CRC32. |
| 1278 | 27 | `static void planeCamera(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 5. THE GLOBE'S CAMERAS (lines 1306-1374)

| line | len | member | says |
|---:|---:|---|---|
| 1311 | 1 | `static int folded(byte b)` | A class with the forest folded into the dry ground it stands on, for comparing two dithers. |
| 1313 | 61 | `static void cameras(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data)` |  |

### 6. THE PLANET'S PICTURE (lines 1375-1522)

| line | len | member | says |
|---:|---:|---|---|
| 1379 | 36 | `static GlobePicture picture(EarthData fixture, EarthData full)` |  |
| 1417 | 4 | `static boolean freshNear(GlobePicture p, double lat, double lon)` | Whether the picture draws fresh water within a degree of (lat, lon). |
| 1423 | 11 | **type** `static final class Land` | A picture's land per 1-degree cell against the far layers': cells with a coast, their mean and worst in points, how many past their own rounding, and the planet's land by area. |
| 1428 | 5 | `public String toString()` _(in GlobeCheck.Land)_ |  |
| 1435 | 39 | `static Land land(EarthData d, GlobePicture p)` |  |
| 1476 | 13 | **type** `static final class Rule` | The far rule at each texel, read back from the data: sea, ice and desert by the rule, the forest's share. |
| 1480 | 1 | `boolean ok()` _(in GlobeCheck.Rule)_ |  |
| 1482 | 1 | `double forestPoints()` _(in GlobeCheck.Rule)_ |  |
| 1484 | 4 | `public String toString()` _(in GlobeCheck.Rule)_ |  |
| 1490 | 32 | `static Rule rule(EarthData d, GlobePicture p)` |  |

### 7. A FRAME (lines 1523-1555)

| line | len | member | says |
|---:|---:|---|---|
| 1527 | 28 | `static void frame(GlobePicture pic)` |  |

### 8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3) (lines 1556-1693)

| line | len | member | says |
|---:|---:|---|---|
| 1564 | 10 | `static long[] dryTexelsByRelief(GlobePicture p, int row0, int col0, int rows, int cols)` | The dry texels (not ice, not water) in a box of the picture, `rows` x `cols` of them from (row0 from the north, col0 from 180 W), counted by relief. |
| 1576 | 17 | `static double[] dataReliefShares(EarthData d, double lat0, double lat1, double lon0, double lon1)` | The data's relief shares over a box of 30" cells (the slope layer's bytes unpacked: flat is under foothill's share, foothill what is not steep, and so on): the mean over every cell. |
| 1595 | 9 | `static boolean sharesMatch(long[] counts, double[] data)` | Whether the texels' shares (counts) are the data's within RELIEF_SIGMAS spreads and the rounding, each relief. |
| 1605 | 4 | `static String sharesWords(long[] counts)` |  |
| 1610 | 83 | `static void reliefInPicture(EarthData fixture, String id, EarthData full, GlobePicture fullPicture)` |  |

